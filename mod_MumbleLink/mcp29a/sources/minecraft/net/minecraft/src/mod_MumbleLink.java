/*
mod_Mumble - Positional Audio Communication for Minecraft with Mumble
Copyright 2011 zsawyer (http://sourceforge.net/users/zsawyer)

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

import java.io.File;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Vec3D;

/**
 * mod to link with mumble for positional audio 
 *  @see http://mumble.sourceforge.net/
 *
 * when developing for it I suggest using "mumblePAHelper" to see coordinates 
 *
 * for Minecraft Beta 1.3
 *
 * @author zsawyer, 2010-03-20
 */
public class mod_MumbleLink extends BaseMod {

    /// name of the library
    static final String libName = "mod_MumbleLink";
    private static boolean libLoaded = false;
    static ArrayList<UnsatisfiedLinkError> errors = new ArrayList<UnsatisfiedLinkError>();
    static UnsatisfiedLinkError error = new UnsatisfiedLinkError();

    @Override
    public String Version() {
        return "1.1";
    }

    public mod_MumbleLink() {
        //ModLoader.getLogger().fine("[mod_MumbleLink] Initializing...");

        // initialize the mumble link, create linked memory
        initMumble();

        //ModLoader.getLogger().fine("[mod_MumbleLink] Hooking to game tick...");

        // hook to game to know when to update
        ModLoader.SetInGameHook(this, true, false);

        //ModLoader.getLogger().fine("[mod_MumbleLink] Finished hooking to game tick!");
    }

    @Override
    public void OnTickInGame(Minecraft game) {
        super.OnTickInGame(game);

        //ModLoader.getLogger().fine("[mod_MumbleLink] caught game tick");

        // inform mumble of the current location
        updateMumble(game);

    }

    /**
     * do mumble preparations and call the JNI mumble link function
     *
     * @param game current minecraft instance
     */
    private void updateMumble(Minecraft game) {


        try {
            // 1 unit = 1 meter

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

            /*

            DEBUG: for debugging you can copy the "mumbleMultipliers.settings"
            into the .minecraft folder

            I suggest to uncomment the "ModLoader.getLogger" lines to get
            debug logging into "ModLoader.txt"

             * /

            try {
            //ModLoader.getLogger().fine("[mod_MumbleLink] loading modifiers");

            // get the settings file for multipliers
            File settingsFile = new File(Minecraft.getAppDir("minecraft"), "mumbleMultipliers.settings");

            // if the settings file exsists
            if (settingsFile.exists()) {
            //ModLoader.getLogger().fine("[mod_MumbleLink] file exists");

            // read file contents
            BufferedReader bufferedreader = new BufferedReader(new FileReader(settingsFile));
            String s;
            // for every line
            while ((s = bufferedreader.readLine()) != null) {
            //ModLoader.getLogger().fine("[mod_MumbleLink] reading line: " + s);
            // extract value
            String as[] = s.split(":");

            if (as[0].equals("fAvatarFrontX")) {
            fAvatarFrontX = Float.parseFloat(as[1]);
            }
            if (as[0].equals("fAvatarFrontY")) {
            fAvatarFrontY = Float.parseFloat(as[1]);
            }
            if (as[0].equals("fAvatarFrontZ")) {
            fAvatarFrontZ = Float.parseFloat(as[1]);
            }

            if (as[0].equals("fCameraFrontX")) {
            fCameraFrontX = Float.parseFloat(as[1]);
            }
            if (as[0].equals("fCameraFrontY")) {
            fCameraFrontY = Float.parseFloat(as[1]);
            }
            if (as[0].equals("fCameraFrontZ")) {
            fCameraFrontZ = Float.parseFloat(as[1]);
            }

            if (as[0].equals("fAvatarTopX")) {
            fAvatarTopX = Float.parseFloat(as[1]);
            }
            if (as[0].equals("fAvatarTopY")) {
            fAvatarTopY = Float.parseFloat(as[1]);
            }
            if (as[0].equals("fAvatarTopZ")) {
            fAvatarTopZ = Float.parseFloat(as[1]);
            }

            if (as[0].equals("fCameraTopX")) {
            fCameraTopX = Float.parseFloat(as[1]);
            }
            if (as[0].equals("fCameraTopY")) {
            fCameraTopY = Float.parseFloat(as[1]);
            }
            if (as[0].equals("fCameraTopZ")) {
            fCameraTopZ = Float.parseFloat(as[1]);
            }
            }
            }

            } catch (Exception ex) {
            // do nothing
            }
             */


            //ModLoader.getLogger().fine("[mod_MumbleLink] preparing mumble update");


            /// view vector
            Vec3D camera = game.thePlayer.getLookVec();

            // Position of the avatar
            float[] fAvatarPosition = {
                Float.parseFloat(Double.toString(game.thePlayer.posX)), // TOFIX: losing precision here
                Float.parseFloat(Double.toString(game.thePlayer.posZ)), // TOFIX: losing precision here
                Float.parseFloat(Double.toString(game.thePlayer.posY))}; // TOFIX: losing precision here


            // Unit vector pointing out of the avatars eyes (here Front looks into scene).
            float[] fAvatarFront = {
                Float.parseFloat(Double.toString(camera.xCoord * fAvatarFrontX)), // TOFIX: losing precision here
                Float.parseFloat(Double.toString(camera.zCoord * fAvatarFrontZ)), // TOFIX: losing precision here
                Float.parseFloat(Double.toString(camera.yCoord * fAvatarFrontY))}; // TOFIX: losing precision here

            // Unit vector pointing out of the top of the avatars head (here Top looks straight up).
            float[] fAvatarTop = {fAvatarTopX, fAvatarTopZ, fAvatarTopY};

            float[] fCameraPosition = {
                Float.parseFloat(Double.toString(game.thePlayer.posX)), // TOFIX: losing precision here
                Float.parseFloat(Double.toString(game.thePlayer.posZ)), // TOFIX: losing precision here
                Float.parseFloat(Double.toString(game.thePlayer.posY))}; // TOFIX: losing precision here

            float[] fCameraFront = {
                Float.parseFloat(Double.toString(camera.xCoord * fCameraFrontX)), // TOFIX: losing precision here
                Float.parseFloat(Double.toString(camera.zCoord * fCameraFrontZ)), // TOFIX: losing precision here
                Float.parseFloat(Double.toString(camera.yCoord * fCameraFrontY))}; // TOFIX: losing precision here

            float[] fCameraTop = {fCameraTopX, fCameraTopZ, fCameraTopY};



            // Identifier which uniquely identifies a certain player in a context (e.g. the ingame Name).
            //char[] identity = game.thePlayer.username.toCharArray();
            String identity = game.thePlayer.username;

            // Context should be equal for players which should be able to hear each other positional and
            // differ for those who shouldn't (e.g. it could contain the server+port and team)
            //char[] context = "MinecraftAllTalk".toCharArray();
            //int context_len = context.length;
            String context = "MinecraftAllTalk";

            String name = "Minecraft";

            String description = "Link plugin for Minecraft Beta 1.3 with ModLoaderV5";



            /*ModLoader.getLogger().fine("[mod_MumbleLink] updating mumble: \ncontext: " + context
            + "\ndescription: " + description
            + "\nidentity: " + identity
            + "\nname: " + name
            + "\nfAvatarFront: " + fAvatarFront[0] + ", " + fAvatarFront[1] + ", " + fAvatarFront[2]
            + "\fAvatarPosition: " + fAvatarPosition[0] + ", " + fAvatarPosition[1] + ", " + fAvatarPosition[2]
            + "\fAvatarTop: " + fAvatarTop[0] + ", " + fAvatarTop[1] + ", " + fAvatarTop[2]
            + "\fCameraFront: " + fCameraFront[0] + ", " + fCameraFront[1] + ", " + fCameraFront[2]
            + "\fCameraPosition: " + fCameraPosition[0] + ", " + fCameraPosition[1] + ", " + fCameraPosition[2]
            + "\fCameraTop: " + fCameraTop[0] + ", " + fCameraTop[1] + ", " + fCameraTop[2]);*/



            updateLinkedMumble(fAvatarPosition, fAvatarFront, fAvatarTop, name, description, fCameraPosition, fCameraFront, fCameraTop, identity, context);

            //ModLoader.getLogger().fine("[mod_MumbleLink] mumble updated");

        } catch (Exception ex) {
            //ModLoader.getLogger().log(Level.SEVERE, null, ex);
        }
    }

    /********** NATIVE FUNCTIONS FROM DLL **********/
    /**
     * method from dll (heartbeat to mumble)
     */
    private native void updateLinkedMumble(
            float[] fAvatarPosition, // [3]
            float[] fAvatarFront, // [3]
            float[] fAvatarTop, // [3]            
            //char[] name, // [256]
            String name, // [256]
            String description,
            float[] fCameraPosition, // [3]
            float[] fCameraFront, // [3]
            float[] fCameraTop, // [3]
            //char[] identity); // [256]
            String identity, // [256]
            String context);

    /**
     * method from dll (prepare mumble)
     */
    private native void initMumble();

    /**
     * load dll
     */
    static {
        // assemble the current minecraft path
        String s = File.separator;
        String dllFolder = Minecraft.getAppDir("minecraft").getAbsolutePath() + s + "bin" + s + "natives" + s + "mumbleLink" + s;


        // loading the library by trying different versions and file locations

        // try 32 bit library
        attemptLoadLibrary(libName); // from path
        attemptLoadLibrary(dllFolder + libName + ".dll", true); // from file
        attemptLoadLibrary(dllFolder + "lib" + libName + ".so", true); // from file

        // try 64 bit library
        attemptLoadLibrary(libName + "_x64"); // from path
        attemptLoadLibrary(dllFolder + libName + "_x64.dll", true); // from file
        attemptLoadLibrary(dllFolder + "lib" + libName + "_x64.so", true); // from file

        // if no library could be loaded
        if (!libLoaded) {
            UnsatisfiedLinkError err;
            // if no error were registered
            if (errors.size() == 0) {
                // throw missing libraries error
                 err = new UnsatisfiedLinkError("Library files not found!");

                // give output to the log
                ModLoader.getLogger().severe("[mod_MumbleLink][ERROR]" + err);                
            } else {
                // throw incompatibility error
                err = new UnsatisfiedLinkError("Required library could not be loaded, available libraries are incompatible!");
                
                // give output to the log
                ModLoader.getLogger().severe("[mod_MumbleLink][ERROR]" + err);
            }

            ModLoader.ThrowException("Couldn't load library for mod_MumbleLink", err);
        }

    }

    /**
     * load the specified library from path
     *
     * @param lib library name 
     * @throws UnsatisfiedLinkError loading of a found library failed
     */
    private static void attemptLoadLibrary(String lib) {
        attemptLoadLibrary(lib, false);
    }

    /**
     * load library from either path or a file
     *
     * @param lib name of the library or path of the file
     * @param file if true lib is expected to specify a file
     * @throws UnsatisfiedLinkError loading of a found library failed
     */
    private static void attemptLoadLibrary(String lib, boolean file) {
        // if the library was already loaded skip
        if (!libLoaded) {

            // try loading lib
            try {
                // if supplied lib is a file path
                if (file) {
                    // attempt to load library file
                    System.load(lib);
                } else {
                    // attemt to load the library from jpath
                    System.loadLibrary(lib);
                }

            } catch (UnsatisfiedLinkError err) {
                //ModLoader.getLogger().fine("[DEBUG] " + err);

                // check if the library was not found
                if (err.getMessage().startsWith("no ") ||
                        err.getMessage().startsWith("Can't load library")) {
                    
                    // library was not loaded because it was not found
                    return;
                    
                } else {
                    // loading failed, throw error
                    errors.add(err);
                    return;
                }
            }

            // mark success
            libLoaded = true;

            //ModLoader.getLogger().fine("[DEBUG] loaded: " + lib);
        }
    }
}
