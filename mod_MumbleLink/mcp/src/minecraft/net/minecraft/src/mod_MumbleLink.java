/*
 mod_MumbleLink - Positional Audio Communication for Minecraft with Mumble
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
import java.io.IOException;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import static net.minecraft.src.ErrorHandler.ModError.*;
import static net.minecraft.src.Settings.Key.*;
import static net.minecraft.src.Settings.PresetValue.*;

/**
 * mod to link with mumble for positional audio
 *
 * @see http://mumble.sourceforge.net/
 *
 * when developing for it I suggest using "mumblePAHelper" to see coordinates
 *
 * for Minecraft v1.2.5 (snapshot) updated 2012-06-08
 *
 * @author zsawyer, 2011-03-20
 */
@SuppressWarnings("StaticNonFinalUsedInInitialization")
public class mod_MumbleLink extends BaseMod {

    public static final String modVersion = "2.4.4";
    public static final String modName = "MumbleLink";
    //
    //
    private final String settingsFileName = "mod_MumbleLink.conf";
    private Settings settings;
    //
    //
    private boolean libLoaded = false;
    private boolean mumbleInited = false;
    private boolean userWasNotfiedAboutLinkSuccess = false;
    //
    /// error stack when loading libraries during mod initialization
    private static ArrayList<UnsatisfiedLinkError> errors = new ArrayList<UnsatisfiedLinkError>();
    // start for the delay timer
    private long start = -1;
    private final static boolean IS_A_FILE_PATH = true;
    private final static boolean IS_NOT_A_FILE_PATH = false;
    private static final ErrorHandler errorHandler = ErrorHandler.getInstance();

    public mod_MumbleLink() {
        settings = new Settings();
        initDefaultSettings();
    }

    private void initDefaultSettings() {
        settings.define(MUMBLE_CONTEXT, CONTEXT_ALL_TALK);
        settings.define(NOTIFICATION_DELAY_IN_MILLI_SECONDS, "10000");
        settings.define(LIBRARY_NAME, "mod_MumbleLink");

    }

    @Override
    public boolean onTickInGame(float tick, Minecraft game) {
        super.onTickInGame(tick, game);
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




        // if mumble is linked
        if (mumbleInited) {
            // make sure we got a gui
            if (game != null && game.ingameGUI != null) {
                // if the client was not yet notified
                if (!this.userWasNotfiedAboutLinkSuccess) {
                    //ModLoader.getLogger().log(Level.FINER, "[" + modName + modVersion + "] ticked {0})", game.theWorld.getWorldTime());
                    long now = System.currentTimeMillis();
                    // if start was not yet set
                    if (start == -1) {
                        // define start to now
                        start = now;
                    }

                    // if delay time passed
                    if (start + settings.getInt(NOTIFICATION_DELAY_IN_MILLI_SECONDS) < now) {
                        // remember not to nag again
                        this.userWasNotfiedAboutLinkSuccess = true;

                        // display a message
                        game.ingameGUI.addChatMessage("Mumble linked.");
                    }
                }
            } else {
                this.userWasNotfiedAboutLinkSuccess = false;
            }
        }

        return true;
    }

    /**
     * try to initialize mumble does some error checking
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

            /*
             *
             * DEBUG: for debugging you can copy the
             * "mumbleMultipliers.settings" into the .minecraft folder for a
             * sample file see SVN trunk: SOURCES\trunk\templates\confs
             *
             * I suggest to uncomment the "ModLoader.getLogger" lines to get
             * debug logging into "ModLoader.txt"
             *
             * /
             *
             * try { //ModLoader.getLogger().fine("[" + modName + modVersion "]
             * loading modifiers");
             *
             * // get the settings file for multipliers File settingsFile = new
             * File(Minecraft.getAppDir("minecraft"),
             * "mumbleMultipliers.settings");
             *
             * // if the settings file exsists if (settingsFile.exists()) {
             * //ModLoader.getLogger().fine("[" + modName + modVersion "] file
             * exists");
             *
             * // read file contents BufferedReader bufferedreader = new
             * BufferedReader(new FileReader(settingsFile)); String s; // for
             * every line while ((s = bufferedreader.readLine()) != null) {
             * //ModLoader.getLogger().fine("[" + modName + modVersion "]
             * reading line: " + s); // extract value String as[] =
             * s.split(":");
             *
             * if (as[0].equals("fAvatarFrontX")) { fAvatarFrontX =
             * Float.parseFloat(as[1]); } if (as[0].equals("fAvatarFrontY")) {
             * fAvatarFrontY = Float.parseFloat(as[1]); } if
             * (as[0].equals("fAvatarFrontZ")) { fAvatarFrontZ =
             * Float.parseFloat(as[1]); }
             *
             * if (as[0].equals("fCameraFrontX")) { fCameraFrontX =
             * Float.parseFloat(as[1]); } if (as[0].equals("fCameraFrontY")) {
             * fCameraFrontY = Float.parseFloat(as[1]); } if
             * (as[0].equals("fCameraFrontZ")) { fCameraFrontZ =
             * Float.parseFloat(as[1]); }
             *
             * if (as[0].equals("fAvatarTopX")) { fAvatarTopX =
             * Float.parseFloat(as[1]); } if (as[0].equals("fAvatarTopY")) {
             * fAvatarTopY = Float.parseFloat(as[1]); } if
             * (as[0].equals("fAvatarTopZ")) { fAvatarTopZ =
             * Float.parseFloat(as[1]); }
             *
             * if (as[0].equals("fCameraTopX")) { fCameraTopX =
             * Float.parseFloat(as[1]); } if (as[0].equals("fCameraTopY")) {
             * fCameraTopY = Float.parseFloat(as[1]); } if
             * (as[0].equals("fCameraTopZ")) { fCameraTopZ =
             * Float.parseFloat(as[1]); } } }
             *
             * } catch (Exception ex) { // do nothing }
             */


            //ModLoader.getLogger().fine("[" + modName + modVersion "] preparing mumble update");


            /// view vector
            Vec3D camera = game.thePlayer.getLookVec();

            /*
             * TODO: calculate real camera vector from pitch and yaw // camera
             * pitch in degrees (e.g. 0.0f to 360.0f) Float cameraPitch =
             * game.thePlayer.cameraPitch; // camera yaw in degrees (e.g. 0.0f
             * to 360.0f) Float cameraYaw = game.thePlayer.cameraYaw;
             */

            // Position of the avatar
            float[] fAvatarPosition = {
                Float.parseFloat(Double.toString(game.thePlayer.posX)), // note: losing precision here
                Float.parseFloat(Double.toString(game.thePlayer.posZ)), // note: losing precision here
                Float.parseFloat(Double.toString(game.thePlayer.posY))}; // note: losing precision here

            // Unit vector pointing out of the avatar's eyes (here Front looks into scene).
            float[] fAvatarFront = {
                Float.parseFloat(Double.toString(camera.xCoord * fAvatarFrontX)), // note: losing precision here
                Float.parseFloat(Double.toString(camera.zCoord * fAvatarFrontZ)), // note: losing precision here
                Float.parseFloat(Double.toString(camera.yCoord * fAvatarFrontY))}; // note: losing precision here

            // Unit vector pointing out of the top of the avatar's head (here Top looks straight up).
            float[] fAvatarTop = {fAvatarTopX, fAvatarTopZ, fAvatarTopY};


            // TODO: use real camera position, s.a.
            float[] fCameraPosition = {
                Float.parseFloat(Double.toString(game.thePlayer.posX)), // note: losing precision here
                Float.parseFloat(Double.toString(game.thePlayer.posZ)), // note: losing precision here
                Float.parseFloat(Double.toString(game.thePlayer.posY))}; // note: losing precision here

            // TODO: use real look vector, s.a.
            float[] fCameraFront = {
                Float.parseFloat(Double.toString(camera.xCoord * fCameraFrontX)), // note: losing precision here
                Float.parseFloat(Double.toString(camera.zCoord * fCameraFrontZ)), // note: losing precision here
                Float.parseFloat(Double.toString(camera.yCoord * fCameraFrontY))}; // note: losing precision here

            float[] fCameraTop = {fCameraTopX, fCameraTopZ, fCameraTopY};



            // Identifier which uniquely identifies a certain player in a context (e.g. the ingame Name).
            String identity = game.thePlayer.username;

            // Context should be equal for players which should be able to hear each other positional and
            //  differ for those who shouldn't (e.g. it could contain the server+port and team)
            //  CAUTION: max len: 256
            String context = CONTEXT_ALL_TALK.toString();

            if (settings.isDefined(MUMBLE_CONTEXT)) {
                context = settings.get(MUMBLE_CONTEXT);
            }

            if (settings.compare(MUMBLE_CONTEXT, CONTEXT_WORLD)) {
                // create context string while staying inside bounds and keeping as much information as possible
                context = generateContextJSON(game.theWorld);
            }

            String name = "Minecraft";

            String description = "Link plugin for Minecraft with ModLoader";



            /*
             * ModLoader.getLogger().fine("[" + modName + modVersion "] updating
             * mumble: \ncontext: " + context + "\ndescription: " + description
             * + "\nidentity: " + identity + "\nname: " + name +
             * "\nfAvatarFront: " + fAvatarFront[0] + ", " + fAvatarFront[1] +
             * ", " + fAvatarFront[2] + "\fAvatarPosition: " +
             * fAvatarPosition[0] + ", " + fAvatarPosition[1] + ", " +
             * fAvatarPosition[2] + "\fAvatarTop: " + fAvatarTop[0] + ", " +
             * fAvatarTop[1] + ", " + fAvatarTop[2] + "\fCameraFront: " +
             * fCameraFront[0] + ", " + fCameraFront[1] + ", " + fCameraFront[2]
             * + "\fCameraPosition: " + fCameraPosition[0] + ", " +
             * fCameraPosition[1] + ", " + fCameraPosition[2] + "\fCameraTop: "
             * + fCameraTop[0] + ", " + fCameraTop[1] + ", " + fCameraTop[2]);
             */



            int err = updateLinkedMumble(fAvatarPosition, fAvatarFront, fAvatarTop, name, description, fCameraPosition, fCameraFront, fCameraTop, identity, context);

            //ModLoader.getLogger().log(Level.FINER, "[" + modName + modVersion + "] mumble updated (code: {0})", err);

        } catch (Exception ex) {
            //ModLoader.getLogger().log(Level.SEVERE, null, ex);
        }
    }

    /**
     * create a JSON String representation of the context using world unique
     * information keeps the output string within a certain length (256)
     *
     * @param world instance in which the game takes place
     * @return JSON string unique to a world
     */
    private String generateContextJSON(World world) {
        int contextSize = 256; // from linkedMem.h: unsigned char context[256];

        // TODO: Seed is not very unique, find a better server identifier
        // TODO: identify Nether and other worlds
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
        String worldSeed = Long.toString(world.worldInfo.getSeed());


        // string if world is not set
        String context_empty = startStr
                + gameStr
                //      + worldNameInit + concatinator
                + worldSeedInit
                + endStr;

        // calculate the rest that we can use for dynamic content
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


    /*
     * ********* NATIVE FUNCTIONS FROM DLL *********
     */
    /**
     * method from dll (heartbeat to mumble)
     *
     * updates the shared memory with the newest data
     *
     * this basically corresponds to the suggested function as posted in the
     * mumble wiki (http://mumble.sourceforge.net/Link)
     *
     *
     * @param fAvatarPosition Position of the avatar
     * @param fAvatarFront Unit vector pointing out of the avatar's eyes (pitch,
     * yaw)
     * @param fAvatarTop Unit vector pointing out of the top of the avatar's
     * head (pitch, roll)
     * @param name this tool's name
     * @param description what this tool is/does
     * @param fCameraPosition Position of the camera
     * @param fCameraFront Unit vector pointing which direction the camera is
     * facing (pitch, yaw)
     * @param fCameraTop Unit vector pointing out of the top of the camera
     * (pitch, roll)
     * @param identity Identifier which uniquely identifies a certain player in
     * a context (e.g. the ingame Name)
     * @param context Context should be equal for players which should be able
     * to hear each other positional and differ for those who shouldn't (e.g. it
     * could contain the server+port and team)
     * @return error code 0: no error 1: shared memory was not initialized (was
     * initMumble() called?)
     */
    private native int updateLinkedMumble(
            float[] fAvatarPosition, // [3]
            float[] fAvatarFront, // [3]
            float[] fAvatarTop, // [3]
            String name, // [256]
            String description,
            float[] fCameraPosition, // [3]
            float[] fCameraFront, // [3]
            float[] fCameraTop, // [3]
            String identity, // [256]
            String context);

    /**
     * method from dll (prepare mumble)
     *
     * initializes the shared memory
     *
     * this basically corresponds to the suggested function as posted in the
     * mumble wiki (http://mumble.sourceforge.net/Link)
     *
     * @return error code 0: no error 1: win32 specific: OpenFileMappingW failed
     * to return a handle 2: win32 specific: MapViewOfFile failed to return a
     * structure 3: unix specific: shm_open returned a negative integer * 4:
     * unix specific: mmap failed to return a structure
     */
    private native int initMumble();

    /**
     * load the specified library from path
     *
     * @param lib library name
     */
    private void attemptLoadLibrary(String lib) {
        attemptLoadLibrary(lib, IS_NOT_A_FILE_PATH);
    }

    /**
     * load library from either path or a file
     *
     * @param lib name of the library or path of the file
     * @param file if true lib is expected to specify a file
     */
    private void attemptLoadLibrary(String lib, boolean file) {
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

    @Override
    public void load() {

        loadSettings();
        loadLibrary();
        registerWithModLoader();
        initializeMumble();
    }

    private void loadSettings() {

        File settingsFile = getSettingsFile();

        if (settingsFile.exists()) {
            try {
                settings.loadFromFile(settingsFile);
            } catch (IOException fileError) {
                errorHandler.handleError(CONFIG_FILE_READ, fileError);
            }

        }
    }

    private File getSettingsFile() {
        return new File(Minecraft.getMinecraftDir(), settingsFileName);
    }

    private void loadLibrary() {
        String libraryFolder = getLibraryFolder();

        String libName = settings.get(LIBRARY_NAME);
        // loading the library by trying different versions and file locations

        // try 32 bit library
        attemptLoadLibrary(
                libName); // from path
        // windows - 32 bit library file
        attemptLoadLibrary(
                libraryFolder + libName + ".dll", IS_A_FILE_PATH); // from file
        // linux - 32 bit library file
        attemptLoadLibrary(
                libraryFolder + "lib" + libName + ".so", IS_A_FILE_PATH); // from file
        // mac - 32 bit library file
        attemptLoadLibrary(
                libraryFolder + "lib" + libName + ".dylib", IS_A_FILE_PATH); // from file

        // try 64 bit library
        attemptLoadLibrary(
                libName + "_x64"); // from path
        // windows - 64 bit library file
        attemptLoadLibrary(
                libraryFolder + libName + "_x64.dll", IS_A_FILE_PATH); // from file
        // linux - 64 bit library file
        attemptLoadLibrary(
                libraryFolder + "lib" + libName + "_x64.so", IS_A_FILE_PATH); // from file
        // mac - 64 bit library file
        attemptLoadLibrary(
                libraryFolder + "lib" + libName + "_x64.dylib", IS_A_FILE_PATH); // from file

        // if no library could be loaded
        if (!libLoaded) {
            UnsatisfiedLinkError err;
            // if no errors were registered

            if (errors.isEmpty()) {
                // throw missing libraries error
                err = new UnsatisfiedLinkError("Library files not found! Searched in: \"" + libraryFolder + "\"");

            } else {
                // throw incompatibility error
                err = new UnsatisfiedLinkError("Required library could not be loaded, available libraries are incompatible!");

            }

            errorHandler.throwError(LIBRARY_LOAD_FAILED, err);
        }
    }

    private String getLibraryFolder() {
        String s = File.separator;
        return Minecraft.getMinecraftDir().getAbsolutePath()
                + s + "mods"
                + s + modName
                + s + "natives"
                + s;
    }

    private void registerWithModLoader() {
        ModLoader.setInGameHook(this, true, false);
    }

    private void initializeMumble() {
        // attempt mumble initialization
        tryInitMumble();
    }

    @Override
    public String getVersion() {
        return modVersion;
    }
}
