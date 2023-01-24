/*
 mod_MumbleLink - Positional Audio Communication for Minecraft with Mumble
 Copyright 2012 zsawyer (http://sourceforge.net/users/zsawyer)

 This file is part of mod_MumbleLink
 (http://sourceforge.net/projects/modmumblelink/).

 mod_MumbleLink is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 mod_MumbleLink is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with mod_MumbleLink.  If not, see <http://www.gnu.org/licenses/>.

 */
package zsawyer.mods.mumblelink.mumble;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import zsawyer.mods.mumblelink.MumbleLinkImpl;
import zsawyer.mods.mumblelink.api.IdentityManipulator;
import zsawyer.mods.mumblelink.error.NativeUpdateErrorHandler;
import zsawyer.mods.mumblelink.error.NativeUpdateErrorHandler.NativeUpdateError;
import zsawyer.mods.mumblelink.mumble.jna.LinkAPIHelper;
import zsawyer.mods.mumblelink.util.json.JSONException;
import zsawyer.mods.mumblelink.util.json.JSONObject;
import zsawyer.mumble.jna.LinkAPILibrary;

/**
 * @author zsawyer
 */
public class UpdateData {
    private static final int HEIGHT_INDEX = 1;


    float[] fAvatarPosition = {0, 0, 0}; // [3]
    float[] fAvatarFront = {0, 0, 0}; // [3]
    float[] fAvatarTop = {0, 0, 0}; // [3]
    String name = ""; // [256]
    String description = ""; // [2048]
    float[] fCameraPosition = {0, 0, 0}; // [3]
    float[] fCameraFront = {0, 0, 0}; // [3]
    float[] fCameraTop = {0, 0, 0}; // [3]
    String identity = ""; // [256]
    String context = ""; // [256]
    LinkAPILibrary mumbleLink;
    NativeUpdateErrorHandler errorHandler;
    private int uiTick = 0;

    public UpdateData(LinkAPILibrary mumbleLink,
                      NativeUpdateErrorHandler errorHandler) {
        this.mumbleLink = mumbleLink;
        this.errorHandler = errorHandler;

        name = MumbleInitializer.PLUGIN_NAME;
        description = MumbleInitializer.PLUGIN_DESCRIPTION;
    }

    public void send() {
        LinkAPILibrary.LinkedMem lm = new LinkAPILibrary.LinkedMem();

        lm.identity = LinkAPIHelper.parseToCharBuffer(
                LinkAPILibrary.MAX_IDENTITY_LENGTH, identity).array();
        lm.context = LinkAPIHelper.parseToByteBuffer(
                LinkAPILibrary.MAX_CONTEXT_LENGTH, context).array();
        lm.context_len = context.length();

        lm.name = LinkAPIHelper.parseToCharBuffer(
                LinkAPILibrary.MAX_NAME_LENGTH, name).array();
        lm.description = LinkAPIHelper.parseToCharBuffer(
                LinkAPILibrary.MAX_DESCRIPTION_LENGTH, description).array();

        lm.uiTick = ++uiTick;
        lm.uiVersion = MumbleInitializer.PLUGIN_UI_VERSION;

        lm.fAvatarPosition = fAvatarPosition;
        lm.fAvatarFront = fAvatarFront;
        lm.fAvatarTop = fAvatarTop;

        lm.fCameraPosition = fCameraPosition;
        lm.fCameraFront = fCameraFront;
        lm.fCameraTop = fCameraTop;

        byte successMessage = mumbleLink.updateData(lm);
        boolean success = (successMessage != 0);

        if (!success) {
            errorHandler
                    .handleError(NativeUpdateError.ERROR_NOT_YET_INITIALIZED);
        }

    }

    public void set(Minecraft game) {
        try {
            // 1 unit = 1 meter

            // initialize multipliers
            // converted to left-handed coordinate system
            //   (Mumble uses a left-handed, Minecraft uses a right-handed)
            float fAvatarPositionX = 1;
            float fAvatarPositionY = 1;
            float fAvatarPositionZ = -1; // switch to left-handedness

            float fCameraPositionX = 1;
            float fCameraPositionY = 1;
            float fCameraPositionZ = -1; // switch to left-handedness

            float fAvatarFrontX = 1;
            float fAvatarFrontY = 1;
            float fAvatarFrontZ = -1; // switch to left-handedness

            float fCameraFrontX = 1;
            float fCameraFrontY = 1;
            float fCameraFrontZ = -1; // switch to left-handedness

            float fAvatarTopX = 1;
            float fAvatarTopY = 1;
            float fAvatarTopZ = -1; // switch to left-handedness

            float fCameraTopX = 1;
            float fCameraTopY = 1;
            float fCameraTopZ = -1; // switch to left-handedness

            Vec3 lookDirection = game.player.getLookAngle();
            Vec3 topDirection = getTopVec(game);

            // Position of the avatar
            fAvatarPosition = new float[]{
                    (float) game.player.getPosition(1f).x() * fAvatarPositionX,
                    (float) game.player.getPosition(1f).y() * fAvatarPositionY,
                    (float) game.player.getPosition(1f).z() * fAvatarPositionZ
            };
            applyDimensionalOffset(game, fAvatarPosition);

            // Unit vector pointing out of the avatar's eyes (here Front looks
            // into scene).
            fAvatarFront = new float[]{
                    (float) lookDirection.x * fAvatarFrontX,
                    (float) lookDirection.y * fAvatarFrontY,
                    (float) lookDirection.z * fAvatarFrontZ
            };

            // Unit vector pointing out of the top of the avatar's head (here
            // Top looks straight up).
            fAvatarTop = new float[]{
                    (float) topDirection.x * fAvatarTopX,
                    (float) topDirection.y * fAvatarTopY,
                    (float) topDirection.z * fAvatarTopZ
            };

            // TODO: use real camera position, s.a.
            // Position of the camera
            fCameraPosition = new float[]{
                    (float) game.player.getPosition(1f).x() * fCameraPositionX,
                    (float) game.player.getPosition(1f).y() * fCameraPositionY,
                    (float) game.player.getPosition(1f).z() * fCameraPositionZ
            };
            applyDimensionalOffset(game, fCameraPosition);

            fCameraFront = new float[]{
                    (float) lookDirection.x * fCameraFrontX,
                    (float) lookDirection.y * fCameraFrontY,
                    (float) lookDirection.z * fCameraFrontZ
            };

            fCameraTop = new float[]{
                    (float) topDirection.x * fCameraTopX,
                    (float) topDirection.y * fCameraTopY,
                    (float) topDirection.z * fCameraTopZ
            };

            // Identifier which uniquely identifies a certain player in a
            // context (e.g. the ingame Name).
            identity = generateIdentity(game,
                    LinkAPILibrary.MAX_IDENTITY_LENGTH);

            // Context should be equal for players which should be able to hear
            // each other positional and differ for those who shouldn't (e.g. it
            // could contain the server+port and team)
            context = generateContext(game, LinkAPILibrary.MAX_CONTEXT_LENGTH);

        } catch (Exception ex) {
            // we'll just ignore errors since they would become too spammy and
            // we will retry anyways
            // ModLoader.getLogger().log(Level.SEVERE, null, ex);
        }
    }

    protected String generateIdentity(Minecraft game, int maxLength) {
        String displayName = game.player.getDisplayName().getString();

        try {
            JSONObject newIdentity = new JSONObject();
            newIdentity.put(IdentityManipulator.IdentityKey.NAME, displayName);
            return newIdentity.toString();
        } catch (JSONException e) {
            MumbleLinkImpl.LOG.fatal("could not generate identity", e);
        }

        return displayName;
    }

    protected String generateContext(Minecraft game, int maxLength) {
        // empty context because the plugin name is already always prepended anyways
        return "";
    }

    private Vec3 getTopVec(Minecraft game) {
        return game.player.getUpVector(1f);
    }


    /**
     * Make people in other dimensions far away so that they're muted.
     * <p>
     * reimplementation of https://github.com/magneticflux-/fabric-mumblelink-mod/blob/12727324ae9ecfc9c6b0ab5b604e824d43cfffa1/src/main/kotlin/com/skaggsm/mumblelinkmod/client/ClientMumbleLinkMod.kt#L136
     *
     * @param game             the source to get live data from
     * @param originalPosition the original position to be offset
     */
    public static void applyDimensionalOffset(Minecraft game, float[] originalPosition) {
        ResourceKey<Level> dimension = game.player.level.dimension();
        if (dimension == null) {
            // silently ignoring because it would become too spammy
            return;
        }

        int configuredOffset = MumbleLinkImpl.dimensionalHeight();
        int hash = LinkAPIHelper.stableHash(dimension.toString());
        float heightOffset = (hash % 2048) * configuredOffset;

        originalPosition[HEIGHT_INDEX] += heightOffset;
    }
}
