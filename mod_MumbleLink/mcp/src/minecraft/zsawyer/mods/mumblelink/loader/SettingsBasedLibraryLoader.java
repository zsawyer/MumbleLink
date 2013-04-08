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
package zsawyer.mods.mumblelink.loader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import zsawyer.mods.mumblelink.error.ErrorHandlerImpl;
import zsawyer.mods.mumblelink.error.ModErrorHandler;
import zsawyer.mods.mumblelink.error.ModErrorHandler.ModError;
import zsawyer.mods.mumblelink.settings.Settings;
import zsawyer.mods.mumblelink.settings.Settings.Key;
import net.minecraft.client.Minecraft;

/**
 * 
 * @author zsawyer
 */
public class SettingsBasedLibraryLoader extends BruteForceLibraryLoader {

	// / error stack when loading libraries during mod initialization
	private static ArrayList<UnsatisfiedLinkError> errors = new ArrayList<UnsatisfiedLinkError>();
	private static final ModErrorHandler errorHandler = ErrorHandlerImpl
			.getInstance();
	private final Settings settings;

	public SettingsBasedLibraryLoader(Settings settings) {
		super(settings.get(Key.LIBRARY_NAME));
		this.settings = settings;
		updateFromSettings();

	}

	private void updateFromSettings() {
		setLibraryFolder(retrieveLibraryFolder());
	}

	@Override
	public boolean attemptLoadLibrary(String lib) {
		File fileCandidate = new File(lib);
		if (fileCandidate.isFile()) {
			return attemptLoadLibrary(fileCandidate);
		}
		return attemptLoadLibraryByName(lib);
	}

	private boolean attemptLoadLibrary(File file) {
		try {
			System.load(file.getAbsolutePath());
			return true;
		} catch (UnsatisfiedLinkError err) {
			errors.add(err);
		}
		return false;
	}

	private boolean attemptLoadLibraryByName(String libName) {
		try {
			System.loadLibrary(libName);
			return true;
		} catch (UnsatisfiedLinkError err) {
			errors.add(err);
		}
		return false;
	}

	private String retrieveLibraryFolder() {

		String filePath;
		try {
			filePath = getLibraryFolderFromSettings();
		} catch (IOException reason) {
			errorHandler
					.handleError(ModError.CONFIG_FILE_INVALID_VALUE, reason);
			filePath = generateDefaultLibraryFolder();
		} catch (NoSuchFieldError err) {
			filePath = generateDefaultLibraryFolder();
		}

		return filePath;
	}

	private String getLibraryFolderFromSettings() throws IOException {
		if (settings.isDefined(Key.LIBRARY_FOLDER_PATH)) {
			String folderPathCandidate = settings.get(Key.LIBRARY_FOLDER_PATH);

			return validateDirectory(folderPathCandidate);
		}

		throw new NoSuchFieldError("folder not specified in config");
	}

	private String validateDirectory(String filePathCandidate)
			throws IOException {
		File folder = new File(filePathCandidate);

		if (!folder.isDirectory()) {
			throw new IOException("not a directory: '" + filePathCandidate
					+ "'");
		}

		return filePathCandidate;
	}

	private String generateDefaultLibraryFolder() {
		String s = File.separator;
		return Minecraft.getMinecraftDir().getAbsolutePath() + s + "mods" + s
				+ settings.get(Key.MOD_NAME) + s + "natives" + s;
	}

	private UnsatisfiedLinkError createFatalLoadError() {
		UnsatisfiedLinkError err;
		if (noLibraryFilesFound()) {
			err = new UnsatisfiedLinkError(
					"Library files not found! Searched in: \""
							+ getLibraryFolder() + "\"");
		} else {
			err = new UnsatisfiedLinkError(
					"Required library could not be loaded, available libraries are incompatible!");
		}
		return err;
	}

	private boolean noLibraryFilesFound() {
		return errors.isEmpty();
	}

	private void resetErrors() {
		errors.clear();
	}
}
