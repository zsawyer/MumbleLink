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
import net.minecraft.util.math.Vec3d;
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

    private int dimensionHeightOffset = 0;
    private int dimensionOffsetUpdateCounter = 0;

    // First prime number higher than (2^16 + 512) / 13
    final int DIMENSION_OFFSET_SEED = 5081;
    final int DIMENSION_OFFSET_UPDATE_RATE = 101;

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

            Vec3d lookDirection = game.player.getLookVec();
            Vec3d topDirection = getTopVec(game);

			/*
             * TODO: calculate real camera vector from pitch and yaw // camera
			 * pitch in degrees (e.g. 0.0f to 360.0f) Float cameraPitch =
			 * game.player.cameraPitch; // camera yaw in degrees (e.g. 0.0f
			 * to 360.0f) Float cameraYaw = game.player.cameraYaw;
			 */

            // Position of the avatar
            fAvatarPosition = new float[]{
                    Float.parseFloat(Double.toString(game.player.getPosition().getX())),
                    Float.parseFloat(Double.toString(game.player.getPosition().getZ())),
                    Float.parseFloat(Double.toString(game.player.getPosition().getY()))};

            // Unit vector pointing out of the avatar's eyes (here Front looks
            // into scene).
            fAvatarFront = new float[]{
                    Float.parseFloat(Double.toString(lookDirection.x
                            * fAvatarFrontX)),
                    Float.parseFloat(Double.toString(lookDirection.z
                            * fAvatarFrontZ)),
                    Float.parseFloat(Double.toString(lookDirection.y
                            * fAvatarFrontY))};

            // Unit vector pointing out of the top of the avatar's head (here
            // Top looks straight up).
            fAvatarTop = new float[]{
                    Float.parseFloat(Double.toString(topDirection.x
                            * fAvatarTopX)),
                    Float.parseFloat(Double.toString(topDirection.z
                            * fAvatarTopZ)),
                    Float.parseFloat(Double.toString(topDirection.y
                            * fAvatarTopY))};

            // TODO: use real camera position, s.a.
            fCameraPosition = new float[]{
                    Float.parseFloat(Double.toString(game.player.getPosition().getX())),
                    Float.parseFloat(Double.toString(game.player.getPosition().getZ())),
                    Float.parseFloat(Double.toString(game.player.getPosition().getY()))};

            fCameraFront = new float[]{
                    Float.parseFloat(Double.toString(lookDirection.x
                            * fCameraFrontX)),
                    Float.parseFloat(Double.toString(lookDirection.z
                            * fCameraFrontZ)),
                    Float.parseFloat(Double.toString(lookDirection.y
                            * fCameraFrontY))};

            fCameraTop = new float[]{
                    Float.parseFloat(Double.toString(topDirection.x
                            * fCameraTopX)),
                    Float.parseFloat(Double.toString(topDirection.z
                            * fCameraTopZ)),
                    Float.parseFloat(Double.toString(topDirection.y
                            * fCameraTopY))};

            handleDimensionHeightOffset(game);

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
        try {
            JSONObject newContext = new JSONObject();
            newContext.put(ContextManipulator.ContextKey.DOMAIN,
                    MumbleLinkConstants.MUMBLE_CONTEXT_DOMAIN_ALL_TALK);
            return newContext.toString();
        } catch (JSONException e) {
            MumbleLinkImpl.LOG.fatal("could not generate context", e);
        }

        return MumbleLinkConstants.MUMBLE_CONTEXT_DOMAIN_ALL_TALK;
    }

    private Vec3d getTopVec(Minecraft game) {
        float f1 = MathHelper.cos(-game.player.rotationYaw * 0.017453292F
                - (float) Math.PI);
        float f2 = MathHelper.sin(-game.player.rotationYaw * 0.017453292F
                - (float) Math.PI);
        float f3 = -MathHelper
                .cos((-game.player.rotationPitch + 90) * 0.017453292F);
        float f4 = MathHelper
                .sin((-game.player.rotationPitch + 90) * 0.017453292F);

        return new Vec3d((double) (f2 * f3), (double) f4, (double) (f1 * f3));
    }

    private void handleDimensionHeightOffset(Minecraft game) {
        // Don't recalculate this on every Mumble update.
        // Dimension changes don't happen very often so a second or 2 of delay should not matter.
        if (dimensionOffsetUpdateCounter <= 0) {
            calculateDimensionHeightOffset(game);
            dimensionOffsetUpdateCounter = DIMENSION_OFFSET_UPDATE_RATE;
        }

        dimensionOffsetUpdateCounter--;

        // Offset the height-coordinate based on the dimension
        fAvatarPosition[2] += dimensionHeightOffset;
        fCameraPosition[2] += dimensionHeightOffset;
    }

    private void calculateDimensionHeightOffset(Minecraft game) {
        /*
         * Since the Link API coordinates are stored in floats, and those only have 23 bits of precision, at coordinates
         * around 2^16 meters, the positional audio precision is already reduced to centimeters and becomes increasingly
         * less accurate after that. At 2^23 a float can not contain more precise location data than meters. This is why
         * a maximum y-offset around 2^16 (65536) was chosen.
         * Now there is no way to fit a 256 y-range for each possible dimension id in the range of -65536 to 65536, so
         * instead the three vanilla dimensions, Nether, Overworld and End get their own reserved "Mumble-space" of 256
         * meters high and the rest of the dimensions are distributed over the space in between with the realistic chance
         * of overlapping positional audio spaces, but it is what it is...
         *
         * The distribution is as follows:
         *                                                                     null worlds
         *                                                                     |---------|
         *                                                                    2^16    2^16+256
         *      |-----------------|----------------------|------------|------------------------|------------|
         * -(2^16)-512       -(2^16)-256                 0           256                    2^16+512     2^16+768
         *           Nether         Negative ID Mod dims    Overworld    Positive ID Mod dims       End
         */

        int maxHeightOffset = 65536;
        int heightOfADimension = 256;

        if (game.world == null) {
            // If we don't know what to do, just use a non-vanilla dimension offset so the "Mumble space" won't overlap
            // with the Overworld or End.
            dimensionHeightOffset = maxHeightOffset;
            return;
        }

        int dimID = game.world.dimension.getType().getId();

        switch (dimID) {
            case 0:
                // Overworld
                dimensionHeightOffset = 0;
                return;
            case -1:
                // Nether
                dimensionHeightOffset = -1 * maxHeightOffset - (heightOfADimension * 2);
                return;
            case 1:
                // End
                dimensionHeightOffset = maxHeightOffset + (heightOfADimension * 2);
                return;
            default:
                // In all other cases, use the distribution function.
                break;
        }

        // Min_INT * Min_ INT < Max_LONG
        // -(2^31) * -(2^31) = 2^62 < 2^63-1
        long rawHeightOffset = Math.abs((long) DIMENSION_OFFSET_SEED * (long) dimID);

        // Transform the offset to be distributed between 256 and 2^16 + 256
        dimensionHeightOffset = (int) ((rawHeightOffset % maxHeightOffset) + heightOfADimension);

        // Transform the offset to be distributed between -(2^16) - 256 and -256
        if (dimID < 0) {
            dimensionHeightOffset *= -1;
        }
    }
}
