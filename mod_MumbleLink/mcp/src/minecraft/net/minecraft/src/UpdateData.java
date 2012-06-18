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
package net.minecraft.src;

import net.minecraft.client.Minecraft;
import net.minecraft.src.mumblelink.Context;
import net.minecraft.src.mumblelink.MumbleLink;
import net.minecraft.src.mumblelink.NativeUpdateErrorHandler;
import net.minecraft.src.mumblelink.NativeUpdateErrorHandler.NativeUpdateError;
import net.minecraft.src.mumblelink.Settings;
import static net.minecraft.src.mumblelink.Settings.Key.MAX_CONTEXT_LENGTH;
import static net.minecraft.src.mumblelink.Settings.Key.MUMBLE_CONTEXT;
import static net.minecraft.src.mumblelink.Settings.PresetValue.CONTEXT_ALL_TALK;
import static net.minecraft.src.mumblelink.Settings.PresetValue.CONTEXT_WORLD;

/**
 *
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
    MumbleLink mumbleLink;
    NativeUpdateErrorHandler errorHandler;
    Settings settings;

    public UpdateData(MumbleLink mumbleLink, Settings settings, NativeUpdateErrorHandler errorHandler) {
        this.mumbleLink = mumbleLink;
        this.settings = settings;
        this.errorHandler = errorHandler;

        name = "Minecraft";
        description = "Link plugin for Minecraft with ModLoader";
    }

    public void send() {
        NativeUpdateError errorCode = mumbleLink.callUpdateMumble(fAvatarPosition, fAvatarFront, fAvatarTop, name, description, fCameraPosition, fCameraFront, fCameraTop, identity, context);
        errorHandler.handleError(errorCode);
    }

    public void set(Minecraft game) {
        try {
            // 1 unit = 1 meter

            // TODO: use full vectors (all axes)

            // initialize multipliers
            float fAvatarFrontX = 1;
            float fAvatarFrontY = 0; // cancel out if the user is looking up or down
            float fAvatarFrontZ = 1;

            float fCameraFrontX = 1;
            float fCameraFrontY = 0; // cancel out if the user is looking up or down
            float fCameraFrontZ = 1;

            float fAvatarTopX = 0;
            float fAvatarTopY = 1; // Y points up
            float fAvatarTopZ = 0;

            float fCameraTopX = 0;
            float fCameraTopY = 1; // Y points up
            float fCameraTopZ = 0;


            Vec3D lookDirection = game.thePlayer.getLookVec();

            /*
             * TODO: calculate real camera vector from pitch and yaw // camera
             * pitch in degrees (e.g. 0.0f to 360.0f) Float cameraPitch =
             * game.thePlayer.cameraPitch; // camera yaw in degrees (e.g. 0.0f
             * to 360.0f) Float cameraYaw = game.thePlayer.cameraYaw;
             */

            // Position of the avatar
            fAvatarPosition = new float[]{
                Float.parseFloat(Double.toString(game.thePlayer.posX)), // note: losing precision here
                Float.parseFloat(Double.toString(game.thePlayer.posZ)), // note: losing precision here
                Float.parseFloat(Double.toString(game.thePlayer.posY))}; // note: losing precision here

            // Unit vector pointing out of the avatar's eyes (here Front looks into scene).
            fAvatarFront = new float[]{
                Float.parseFloat(Double.toString(lookDirection.xCoord * fAvatarFrontX)), // note: losing precision here
                Float.parseFloat(Double.toString(lookDirection.zCoord * fAvatarFrontZ)), // note: losing precision here
                Float.parseFloat(Double.toString(lookDirection.yCoord * fAvatarFrontY))}; // note: losing precision here

            // Unit vector pointing out of the top of the avatar's head (here Top looks straight up).
            fAvatarTop = new float[]{fAvatarTopX, fAvatarTopZ, fAvatarTopY};


            // TODO: use real camera position, s.a.
            fCameraPosition = new float[]{
                Float.parseFloat(Double.toString(game.thePlayer.posX)), // note: losing precision here
                Float.parseFloat(Double.toString(game.thePlayer.posZ)), // note: losing precision here
                Float.parseFloat(Double.toString(game.thePlayer.posY))}; // note: losing precision here

            // TODO: use real look vector, s.a.
            fCameraFront = new float[]{
                Float.parseFloat(Double.toString(lookDirection.xCoord * fCameraFrontX)), // note: losing precision here
                Float.parseFloat(Double.toString(lookDirection.zCoord * fCameraFrontZ)), // note: losing precision here
                Float.parseFloat(Double.toString(lookDirection.yCoord * fCameraFrontY))}; // note: losing precision here

            fCameraTop = new float[]{fCameraTopX, fCameraTopZ, fCameraTopY};



            // Identifier which uniquely identifies a certain player in a context (e.g. the ingame Name).
            identity = game.thePlayer.username;

            // Context should be equal for players which should be able to hear each other positional and
            //  differ for those who shouldn't (e.g. it could contain the server+port and team)
            //  CAUTION: max len: 256
            context = CONTEXT_ALL_TALK.toString();

            if (settings.isDefined(MUMBLE_CONTEXT)) {
                context = settings.get(MUMBLE_CONTEXT);
            }

            if (settings.compare(MUMBLE_CONTEXT, CONTEXT_WORLD)) {
                // create context string while staying inside bounds and keeping as much information as possible
                context = generateContextJSON(game.theWorld);
            }



            NativeUpdateError errorCode = mumbleLink.callUpdateMumble(fAvatarPosition, fAvatarFront, fAvatarTop, name, description, fCameraPosition, fCameraFront, fCameraTop, identity, context);
            errorHandler.handleError(errorCode);
            //ModLoader.getLogger().log(Level.FINER, "[" + modName + modVersion + "] mumble updated (code: {0})", err);

        } catch (Exception ex) {
            //ModLoader.getLogger().log(Level.SEVERE, null, ex);
        }
    }

    private String generateContextJSON(World world) {
        Context contextObject = new Context();

        contextObject = initContext(contextObject, world);

        int maxStringLength = settings.getInt(MAX_CONTEXT_LENGTH);

        return contextObject.encodeJSON(maxStringLength);
    }

    private Context initContext(Context context, World sourceForValues) {
        // TODO: Seed is not very unique, find a better server identifier
        String worldSeed = Long.toString(sourceForValues.worldInfo.getSeed());
        // TODO: identify Nether and other worlds
        String worldName = sourceForValues.worldInfo.getWorldName();

        context.define(Context.Key.GAME, Context.PresetValue.MINECRAFT);
        context.define(Context.Key.WORLD_SEED, worldSeed);
        context.define(Context.Key.WORLD_NAME, worldName);

        return context;
    }
}
