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
import net.minecraft.client.Minecraft;
import static net.minecraft.src.mumblelink.ModErrorHandler.ModError.CONFIG_FILE_READ;
import static net.minecraft.src.mumblelink.ModErrorHandler.ModError.LIBRARY_LOAD_FAILED;
import net.minecraft.src.mumblelink.NativeInitErrorHandler.NativeInitError;
import net.minecraft.src.mumblelink.NativeUpdateErrorHandler.NativeUpdateError;
import static net.minecraft.src.mumblelink.Settings.Key.*;
import static net.minecraft.src.mumblelink.Settings.PresetValue.CONTEXT_ALL_TALK;
import net.minecraft.src.mumblelink.*;

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
public class mod_MumbleLink extends BaseMod implements MumbleLink {

    public static final String modVersion = "2.4.4";
    public static final String modName = "MumbleLink";
    //
    //
    private final String settingsFileName = "mod_MumbleLink.conf";
    //
    private ErrorHandlerImpl errorHandler;
    //
    private Settings settings;
    private MumbleInitializer mumbleInititer;
    private Thread mumbleInititerThread;
    private UpdateData mumbleData;

    public mod_MumbleLink() {
        super();

        errorHandler = ErrorHandlerImpl.getInstance();

        settings = new Settings();
        initDefaultSettings();

        mumbleData = new UpdateData(this, settings, errorHandler);

        mumbleInititer = new MumbleInitializer(this, errorHandler);
        this.mumbleInititerThread = new Thread(mumbleInititer);
    }

    private void initDefaultSettings() {
        settings.define(MOD_NAME, modName);
        settings.define(MOD_VERSION, modVersion);

        settings.define(LIBRARY_NAME, "mod_MumbleLink");

        settings.define(MUMBLE_CONTEXT, CONTEXT_ALL_TALK);
        settings.define(MAX_CONTEXT_LENGTH, "256");
        settings.define(NOTIFICATION_DELAY_IN_MILLI_SECONDS, "10000");
    }

    @Override
    public void load() {
        loadSettings();
        loadLibrary();

        registerWithModLoader();


        mumbleInititerThread.start();
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

    private void registerWithModLoader() {
        ModLoader.setInGameHook(this, true, false);
    }

    private void loadLibrary() {
        SettingsBasedLibraryLoader loader = new SettingsBasedLibraryLoader(settings);

        try {
            loader.loadLibrary();
        } catch (UnsatisfiedLinkError err) {
            errorHandler.throwError(LIBRARY_LOAD_FAILED, err);
        }
    }

    @Override
    public boolean onTickInGame(float tick, Minecraft game) {
        super.onTickInGame(tick, game);

        if (mumbleInititer.isMumbleInitialized()) {
            mumbleData.set(game);
            mumbleData.send();
        }

        // no idea what this value does
        return true;
    }

    @Override
    public NativeInitError callInitMumble() {
        int responseCode = initMumble();

        return NativeInitError.fromCode(responseCode);
    }

    /**
     * method from dll (prepare mumble)
     *
     * initializes the shared memory
     *
     * this basically corresponds to the suggested function as posted in the
     * mumble wiki (http://mumble.sourceforge.net/Link)
     *
     * @return error code
     *
     * 0: no error 1: win32 specific: OpenFileMappingW failed to return a handle
     * 2: win32 specific: MapViewOfFile failed to return a structure 3: unix
     * specific: shm_open returned a negative integer * 4: unix specific: mmap
     * failed to return a structure
     */
    private native int initMumble(); // DON'T TOUCH unless you want to recompile the libraries

    @Override
    public NativeUpdateError callUpdateMumble(float[] fAvatarPosition,
            float[] fAvatarFront, float[] fAvatarTop, String name,
            String description, float[] fCameraPosition, float[] fCameraFront,
            float[] fCameraTop, String identity, String context) {
        int responseCode = updateLinkedMumble(fAvatarPosition, fAvatarFront, fAvatarTop, modName, modVersion, fCameraPosition, fCameraFront, fCameraTop, modVersion, modName);
        return NativeUpdateError.fromCode(responseCode);
    }

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
    private native int updateLinkedMumble( // DON'T TOUCH unless you want to recompile the libraries
            float[] fAvatarPosition, // [3]
            float[] fAvatarFront, // [3]
            float[] fAvatarTop, // [3]
            String name, // [256]
            String description, // [2048]
            float[] fCameraPosition, // [3]
            float[] fCameraFront, // [3]
            float[] fCameraTop, // [3]
            String identity, // [256]
            String context);

    @Override
    public String getVersion() {
        return modVersion;
    }
}
