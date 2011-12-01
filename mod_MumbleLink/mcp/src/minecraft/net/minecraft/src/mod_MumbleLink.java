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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import net.minecraft.client.Minecraft;

/**
 * mod to link with mumble for positional audio 
 *  @see http://mumble.sourceforge.net/
 *
 * when developing for it I suggest using "mumblePAHelper" to see coordinates 
 *
 * for Minecraft Beta 1.5_01
 *  updated 2011-05-04
 *
 * @author zsawyer, 2011-03-20
 */
@SuppressWarnings("StaticNonFinalUsedInInitialization")
public class mod_MumbleLink extends BaseMod {

    /// display name of this mod
    private static final String modName = "MumbleLink";
    /// current version number of this mod
    private static final String modVersion = "2.4";
    /// name of the library
    private static final String libName = "mod_MumbleLink";
    /// whether or not the required native library was already loaded
    private static boolean libLoaded = false;
    /// error stack when loading libraries during mod initialization
    private static ArrayList<UnsatisfiedLinkError> errors = new ArrayList<UnsatisfiedLinkError>();

    /// whether or not the shared memory for mumble was already initialized
    private boolean mumbleInited = false;

    /// name of the config file
    private final String configFileName = "mod_MumbleLink.conf";
    /// config parameters for this mod
    private Map<String, String> config;
    
    // flag if the user has been notified about mumble being linked
    private boolean notfied = false;
    
    public mod_MumbleLink() {
        //ModLoader.getLogger().fine("[" + modName + modVersion "] Initializing...");

        config = new Hashtable<String, String>();
        config.put("mumbleContext", "AllTalk");


        // load and set configuration from file
        readConfig();

        // attempt mumble initialization
        tryInitMumble();

        //ModLoader.getLogger().fine("[" + modName + modVersion "] Hooking to game tick...");

        // hook to game to know when to update
        ModLoader.SetInGameHook(this, true, false);

        //ModLoader.getLogger().fine("[" + modName + modVersion "] Finished hooking to game tick!");
                       
    }

    @Override
    public boolean OnTickInGame(float tick, Minecraft game) {
        super.OnTickInGame(tick, game);
        //ModLoader.getLogger().fine("[" + modName + modVersion "] caught game tick");

        // if initiation was not successful
        if (!mumbleInited) {
            // if retry of mumble init did not work
            if (!tryInitMumble()) {
                // skip
                return true;
            }
            	
        }        
        
        // inform mumble of the current location
        updateMumble(game);
        
        // if link was established and the client was not yet told
        if(!this.notfied && mumbleInited) {
        	// make sure we are in a world
        	if(game != null && game.ingameGUI != null) {        		        	
        		// display a message
        		game.ingameGUI.addChatMessage("Mumble linked.");
        		
        		// remember not to nag again
        		this.notfied = true;
        	}
        }

        return true;
    }

    /**
     * try to initialize mumble
     *  does some error checking
     *
     * @return true if initialized
     */
    private boolean tryInitMumble() {
        // initialize the mumble link, create linked memory
        int err = initMumble();

        // if there was an error initializing mumble
        if (err != 0) {
            // remember to try again
            mumbleInited = false;

            //ModLoader.getLogger().log(Level.WARNING, "[" + modName + modVersion "] could not link with Mumble (not started?) (code: {0})", err);
        } else {
            // mark as initialized
            mumbleInited = true;
            return true;
        }

        return false;
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
                for a sample file see SVN trunk: SOURCES\trunk\templates\confs

            I suggest to uncomment the "ModLoader.getLogger" lines to get
            debug logging into "ModLoader.txt"

             * /

            try {
            //ModLoader.getLogger().fine("[" + modName + modVersion "] loading modifiers");

            // get the settings file for multipliers
            File settingsFile = new File(Minecraft.getAppDir("minecraft"), "mumbleMultipliers.settings");

            // if the settings file exsists
            if (settingsFile.exists()) {
            //ModLoader.getLogger().fine("[" + modName + modVersion "] file exists");

            // read file contents
            BufferedReader bufferedreader = new BufferedReader(new FileReader(settingsFile));
            String s;
            // for every line
            while ((s = bufferedreader.readLine()) != null) {
            //ModLoader.getLogger().fine("[" + modName + modVersion "] reading line: " + s);
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


            //ModLoader.getLogger().fine("[" + modName + modVersion "] preparing mumble update");


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
            String identity = game.thePlayer.username;

            // Context should be equal for players which should be able to hear each other positional and
            //  differ for those who shouldn't (e.g. it could contain the server+port and team)
            //  CAUTION: max len: 256
            String context = "MinecraftAllTalk";

            // if config entry is set and is not set to AllTalk
            if( config.containsKey("mumbleContext") ? config.get("mumbleContext").equals("world") : false ) {
                // create context string while staying inside bounds and keeping as much information as possible
                context = generateContextJSON(game.theWorld);
            }

            String name = "Minecraft";

            String description = "Link plugin for Minecraft with ModLoader";



            /*ModLoader.getLogger().fine("[" + modName + modVersion "] updating mumble: \ncontext: " + context
            + "\ndescription: " + description
            + "\nidentity: " + identity
            + "\nname: " + name
            + "\nfAvatarFront: " + fAvatarFront[0] + ", " + fAvatarFront[1] + ", " + fAvatarFront[2]
            + "\fAvatarPosition: " + fAvatarPosition[0] + ", " + fAvatarPosition[1] + ", " + fAvatarPosition[2]
            + "\fAvatarTop: " + fAvatarTop[0] + ", " + fAvatarTop[1] + ", " + fAvatarTop[2]
            + "\fCameraFront: " + fCameraFront[0] + ", " + fCameraFront[1] + ", " + fCameraFront[2]
            + "\fCameraPosition: " + fCameraPosition[0] + ", " + fCameraPosition[1] + ", " + fCameraPosition[2]
            + "\fCameraTop: " + fCameraTop[0] + ", " + fCameraTop[1] + ", " + fCameraTop[2]);*/



            int err = updateLinkedMumble(fAvatarPosition, fAvatarFront, fAvatarTop, name, description, fCameraPosition, fCameraFront, fCameraTop, identity, context);

            //ModLoader.getLogger().log(Level.FINER, "[" + modName + modVersion + "] mumble updated (code: {0})", err);

        } catch (Exception ex) {
            //ModLoader.getLogger().log(Level.SEVERE, null, ex);
        }
    }

    /**
     * create a JSON String representation of the context using world unique information
     *  keeps the output string within a certain length (256)
     *
     * @param world instance in which the game takes place
     * @return JSON string unique to a world
     */
    private String generateContextJSON(World world) {
        int contextSize = 256; // from linkedMem.h: unsigned char context[256];

        // TODO: Seed is not very unique, find a better server identifyer 
        // strings needed for context
        String startStr = "{";
        String gameStr = "\"game\":\"Minecraft\", ";
        // NOTE: worldName for multiplayer servers is by default "MPServer" seed is probably unique enough
        //String worldNameInit = "\"WorldName\":\"";
        String worldSeedInit = "\"WorldSeed\":\"";
        String concatinator = "\", ";
        String endStr = "\"}";


        // 1 for 1 dynamic context only (world seed)
        // 2 for 2 dynamic contexts (world name, world seed)
        int numContents = 1;
        /// name of the world
        String worldName = world.worldInfo.getWorldName();
        /// seed of the world
        String worldSeed = Long.toString(world.worldInfo.getRandomSeed());


        // string if world is not set
        String context_empty = startStr
                + gameStr
                //      + worldNameInit + concatinator
                + worldSeedInit
                + endStr;

        // calcualte the rest that we can use for dynamic content
        int remainderFraction = (contextSize - context_empty.getBytes().length) / numContents; // 256 is set by linkedMem (as defined in Link plugin from mumble)

        // get the actual length if the string is smaller then the allocated space
        int newWorldNameLen = Math.min(worldName.getBytes().length, remainderFraction);
        // get the actual length if the string is smaller then the allocated space
        int newWorldSeedLen = Math.min(worldSeed.getBytes().length, remainderFraction);

        String context = startStr
                + gameStr
                //+ worldNameInit + worldName.substring(0, newWorldNameLen) + concatinator
                + worldSeedInit + worldSeed.substring(0, newWorldSeedLen)
                + endStr;

        return context;
    }

    /* ********* NATIVE FUNCTIONS FROM DLL ********* */
    /**
     * method from dll (heartbeat to mumble)
     *
     * updates the shared memory with the newest data
     *
     *  this basically corresponds to the suggested function as posted in the
     *  mumble wiki (http://mumble.sourceforge.net/Link)
     *
     *
     * @param fAvatarPosition Position of the avatar
     * @param fAvatarFront Unit vector pointing out of the avatar's
     *                     eyes (pitch, yaw)
     * @param fAvatarTop Unit vector pointing out of the top of the avatar's
     *                   head (pitch, roll)
     * @param name this tool's name
     * @param description what this tool is/does
     * @param fCameraPosition Position of the camera 
     * @param fCameraFront Unit vector pointing which direction the camera is
     *                     facing (pitch, yaw)
     * @param fCameraTop Unit vector pointing out of the top of the
     *                   camera (pitch, roll)
     * @param identity Identifier which uniquely identifies a certain player in
     *                 a context (e.g. the ingame Name)
     * @param context Context should be equal for players which should be able
     *                to hear each other positional and differ for those who
     *                shouldn't (e.g. it could contain the server+port and team)
     * @return error code 
     *         0: no error
     *         1: shared memory was not initialized (was initMumble() called?)
     */
    private native int updateLinkedMumble(
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
     *
     * initializes the shared memory
     *
     *  this basically corresponds to the suggested function as posted in the
     *  mumble wiki (http://mumble.sourceforge.net/Link)
     * 
     * @return error code 
     *         0: no error
     *         1: win32 specific: OpenFileMappingW failed to return a handle
     *         2: win32 specific: MapViewOfFile failed to return a structure
     *         3: unix specific: shm_open returned a negative integer
     * *       4: unix specific: mmap failed to return a structure
     */
    private native int initMumble();

    
    /**
     * statically load the native libraries
     */
    static {

        // assemble the current minecraft path
        String s = File.separator;
        String dllFolder = Minecraft.getAppDir("minecraft").getAbsolutePath() + s + "mods" + s + modName + s + "natives" + s;


        // loading the library by trying different versions and file locations

        // try 32 bit library
        attemptLoadLibrary(
                libName); // from path
        // windows - 32 bit library file
        attemptLoadLibrary(
                dllFolder + libName + ".dll", true); // from file
        // linux - 32 bit library file
        attemptLoadLibrary(
                dllFolder + "lib" + libName + ".so", true); // from file
        // mac - 32 bit library file
        attemptLoadLibrary(
                dllFolder + "lib" + libName + ".dylib", true); // from file

        // try 64 bit library
        attemptLoadLibrary(
                libName + "_x64"); // from path
        // windows - 64 bit library file
        attemptLoadLibrary(
                dllFolder + libName + "_x64.dll", true); // from file
        // linux - 64 bit library file
        attemptLoadLibrary(
                dllFolder + "lib" + libName + "_x64.so", true); // from file
        // mac - 64 bit library file
        attemptLoadLibrary(
                dllFolder + "lib" + libName + "_x64.dylib", true); // from file

        // if no library could be loaded
        if (!libLoaded) {
            UnsatisfiedLinkError err;
            // if no errors were registered

            if (errors.isEmpty()) {
                // throw missing libraries error
                err = new UnsatisfiedLinkError("Library files not found! Searched in: \"" + dllFolder + "\"");

            } else {
                // throw incompatibility error
                err = new UnsatisfiedLinkError("Required library could not be loaded, available libraries are incompatible!");

            }

            // give output to the log
            ModLoader.getLogger().log(Level.SEVERE, "[" + modName + modVersion + "][ERROR] {0}", err.getMessage());

            // halt Minecraft
            ModLoader.ThrowException("Couldn't load library for mod " + modName + modVersion, err);

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
                if (err.getMessage().startsWith("no ")
                        || err.getMessage().startsWith("Can't load library")) {

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

    /**
     * fetches the settings from config file
     */
    @SuppressWarnings("NestedAssignment")
    private void readConfig() {

        // get the config file
        File settingsFile = new File(Minecraft.getAppDir("minecraft"), configFileName);

        // if the settings file exsists
        if (settingsFile.exists()) {
            //ModLoader.getLogger().fine("[" + modName + modVersion "] config file exists");

            // read file contents
            BufferedReader bufferedreader;
            try {
                bufferedreader = new BufferedReader(new FileReader(settingsFile));
            } catch (FileNotFoundException ex) {
                //abort
                return;
            }


            try {
                String s;
                // for every line
                while ((s = bufferedreader.readLine()) != null) {
                    //ModLoader.getLogger().fine("[" + modName + modVersion "] reading line: " + s);

                    // extract key-value-pair
                    String[] as = s.split(":", 2);

                    // if its a pair
                    if (as.length == 2) {
                        // add/replace to/in config
                        config.put(as[0].trim(), as[1].trim());
                    }

                }

            } catch (IOException ex) {
                // abort
                return;
            }
        }
    }

	@Override
	public String getVersion() {
		return modVersion;
	}

	@Override
	public void load() {
		// nothing to do here, we do not alter Minecraft's behavior
		
	}
}
