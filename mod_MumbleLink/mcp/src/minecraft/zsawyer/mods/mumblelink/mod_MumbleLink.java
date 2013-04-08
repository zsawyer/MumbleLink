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
package zsawyer.mods.mumblelink;

import java.io.File;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.src.BaseMod;
import net.minecraft.src.ModLoader;
import zsawyer.mods.mumblelink.error.ErrorHandlerImpl;
import zsawyer.mods.mumblelink.error.ModErrorHandler.ModError;
import zsawyer.mods.mumblelink.error.NativeInitErrorHandler.NativeInitError;
import zsawyer.mods.mumblelink.error.NativeUpdateErrorHandler.NativeUpdateError;
import zsawyer.mods.mumblelink.loader.LibraryLoader;
import zsawyer.mods.mumblelink.loader.PackageLibraryLoader;
import zsawyer.mods.mumblelink.mumble.MumbleInitializer;
import zsawyer.mods.mumblelink.mumble.UpdateData;
import zsawyer.mods.mumblelink.mumble.jna.LinkAPIHelper;
import zsawyer.mods.mumblelink.settings.Settings;
import zsawyer.mods.mumblelink.settings.Settings.Key;
import zsawyer.mods.mumblelink.settings.Settings.PresetValue;
import zsawyer.mumble.jna.LinkAPILibrary;

/**
 * mod to link with mumble for positional audio
 *
 * @see http://mumble.sourceforge.net/
 *
 * when developing for it I suggest using "mumblePAHelper" to see coordinates
 *
 * for Minecraft v1.5.1 updated 2012-04-05
 *
 * @author zsawyer, 2011-03-20
 */
public class mod_MumbleLink extends BaseMod {

    public static final String modVersion = "3.0.0";
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
    private LinkAPILibrary library;
    private UpdateTicker updateTicker;

    public mod_MumbleLink() {
        super();

        errorHandler = ErrorHandlerImpl.getInstance();

        settings = new Settings();
        initDefaultSettings();

        try {
			library = new PackageLibraryLoader().loadLibrary(settings.get(Key.LIBRARY_NAME));
		} catch (Exception e) {
			errorHandler.throwError(ModError.LIBRARY_LOAD_FAILED, e);
		}
        
        mumbleData = new UpdateData(library, settings, errorHandler);

        mumbleInititer = new MumbleInitializer(library, errorHandler);
        mumbleInititerThread = new Thread(mumbleInititer);
                
    	updateTicker = new UpdateTicker(this);

    }

    private void initDefaultSettings() {
        settings.define(Key.MOD_NAME, modName);
        settings.define(Key.MOD_VERSION, modVersion);

        settings.define(Key.LIBRARY_NAME, "LinkAPI");

        settings.define(Key.MUMBLE_CONTEXT, PresetValue.CONTEXT_ALL_TALK);
        settings.define(Key.MAX_CONTEXT_LENGTH, "256");
    }

    @Override
    public void load() {
        loadSettings();

        registerWithModLoader();

        mumbleInititerThread.start();
    }

    private void loadSettings() {
        File settingsFile = getSettingsFile();

        if (settingsFile.exists()) {
            try {
                settings.loadFromFile(settingsFile);
            } catch (IOException fileError) {
                errorHandler.handleError(ModError.CONFIG_FILE_READ, fileError);
            }
        }
    }

    private File getSettingsFile() {
        return new File(Minecraft.getMinecraftDir(), settingsFileName);
    }

    private void registerWithModLoader() {
        ModLoader.setInGameHook(this, true, false);
    }

    @Override
    public boolean onTickInGame(float tick, Minecraft game) {
        super.onTickInGame(tick, game);

        tryUpdateMumble(game);

        // no idea what this value does
        return true;
    } 

	public void tryUpdateMumble(Minecraft game) {
		if (mumbleInititer.isMumbleInitialized()) {
            mumbleData.set(game);
            mumbleData.send();
        } else {
            try {
                mumbleInititerThread.start();
            } catch (IllegalThreadStateException ex) {
                // thread was already started so we do nothing
            }
        }
	}

    @Override
    public String getVersion() {
        return modVersion;
    }
}
