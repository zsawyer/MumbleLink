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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import zsawyer.mods.mumblelink.MumbleLinkConstants;
import zsawyer.mods.mumblelink.MumbleLinkImpl;
import zsawyer.mods.mumblelink.api.ContextManipulator;
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
            float fAvatarFrontX = 1;
            float fAvatarFrontY = 1;
            float fAvatarFrontZ = 1;

            float fCameraFrontX = 1;
            float fCameraFrontY = 1;
            float fCameraFrontZ = 1;

            float fAvatarTopX = 1;
            float fAvatarTopY = 1; // Y points up
            float fAvatarTopZ = 1;

            float fCameraTopX = 1;
            float fCameraTopY = 1; // Y points up
            float fCameraTopZ = 1;

            Vector3d lookDirection = game.player.getLookAngle();
            Vector3d topDirection = getTopVec(game);

            // Position of the avatar
            fAvatarPosition = new float[]{
                    (float) game.player.getPosition(1f).x(),
                    (float) game.player.getPosition(1f).z(),
                    (float) game.player.getPosition(1f).y()
            };

            // Unit vector pointing out of the avatar's eyes (here Front looks
            // into scene).
            fAvatarFront = new float[]{
                    (float) lookDirection.x * fAvatarFrontX,
                    (float) lookDirection.z * fAvatarFrontZ,
                    (float) lookDirection.y * fAvatarFrontY
            };

            // Unit vector pointing out of the top of the avatar's head (here
            // Top looks straight up).
            fAvatarTop = new float[]{
                    (float) topDirection.x * fAvatarTopX,
                    (float) topDirection.z * fAvatarTopZ,
                    (float) topDirection.y * fAvatarTopY
            };

            // TODO: use real camera position, s.a.
            fCameraPosition = new float[]{
                    (float) game.player.getPosition(1f).x(),
                    (float) game.player.getPosition(1f).z(),
                    (float) game.player.getPosition(1f).y()
            };

            fCameraFront = new float[]{
                    (float) lookDirection.x * fCameraFrontX,
                    (float) lookDirection.z * fCameraFrontZ,
                    (float) lookDirection.y * fCameraFrontY
            };

            fCameraTop = new float[]{
                    (float) topDirection.x * fCameraTopX,
                    (float) topDirection.z * fCameraTopZ,
                    (float) topDirection.y * fCameraTopY
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

    private Vector3d getTopVec(Minecraft game) {
        return game.player.getUpVector(1f);
    }
}
