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
package net.minecraft.src.mumblelink;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import static net.minecraft.src.mumblelink.ModErrorHandler.ModError.CONFIG_FILE_INVALID_VALUE;
import static net.minecraft.src.mumblelink.Settings.Key.*;

/**
 *
 * @author zsawyer
 */
public class SettingsBasedLibraryLoader implements LibraryLoader {

    private boolean libLoaded = false;
    /// error stack when loading libraries during mod initialization
    private static ArrayList<UnsatisfiedLinkError> errors = new ArrayList<UnsatisfiedLinkError>();
    private static final ModErrorHandler errorHandler = ErrorHandlerImpl.getInstance();
    private Settings settings;

    public SettingsBasedLibraryLoader(Settings settings) {
        this.settings = settings;
    }

    @Override
    public void loadLibrary() throws UnsatisfiedLinkError {
        libLoaded = loadLibraryFromSettingsSpecification();
        libLoaded = loadLibraryByBruteForce();
        if (!libLoaded) {
            UnsatisfiedLinkError error = createFatalLoadError();
            resetErrors();
            throw error;
        }
    }

    private boolean loadLibraryFromSettingsSpecification() {
        if (!settings.isDefined(LIBRARY_FILE_PATH)) {
            return false;
        }

        String filePath = settings.get(LIBRARY_FILE_PATH);
        if (!attemptLoadLibrary(filePath)) {
            Exception reason = new IllegalArgumentException("The specified file was not found: '" + filePath + "'");
            errorHandler.handleError(CONFIG_FILE_INVALID_VALUE, reason);
            return false;
        }

        return true;
    }

    private boolean attemptLoadLibrary(String lib) {
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

    private boolean loadLibraryByBruteForce() {
        boolean loaded = false;
        List<File> files = getLibraryCandidates();
        Iterator<File> fileCrawler = files.iterator();
        while (!loaded && fileCrawler.hasNext()) {
            loaded = attemptLoadLibrary(fileCrawler.next());
        }
        return loaded;
    }

    private List<File> getLibraryCandidates() {
        List<File> files = new ArrayList<File>();
        String libraryFolder = getLibraryFolder();
        String[] fileNames = generateFileNames();
        for (String fileName : fileNames) {
            File possibleCandidate = new File(libraryFolder + fileName);
            if (possibleCandidate.exists()) {
                files.add(possibleCandidate);
            }
        }
        return files;
    }

    private String getLibraryFolder() {
        String s = File.separator;
        return Minecraft.getMinecraftDir().getAbsolutePath() + s + "mods" + s + settings.get(MOD_NAME) + s + "natives" + s;
    }

    private String[] generateFileNames() {
        String libName = settings.get(LIBRARY_NAME);
        String[] names = {
            generateFileName("", libName, "", "dll"),
            generateFileName("", libName, "_x64", "dll"),
            generateFileName("lib", libName, "", "so"),
            generateFileName("lib", libName, "_x64", "so"),
            generateFileName("lib", libName, "", "dylib"),
            generateFileName("lib", libName, "_x64", "dylib")
        };
        return names;
    }

    private String generateFileName(String prefix, String name, String suffix, String extension) {
        return prefix + name + suffix + "." + extension;
    }

    private UnsatisfiedLinkError createFatalLoadError() {
        UnsatisfiedLinkError err;
        if (noLibraryFilesFound()) {
            err = new UnsatisfiedLinkError("Library files not found! Searched in: \"" + getLibraryFolder() + "\"");
        } else {
            err = new UnsatisfiedLinkError("Required library could not be loaded, available libraries are incompatible!");
        }
        return err;
    }

    private boolean noLibraryFilesFound() {
        return errors.isEmpty();
    }

    private void resetErrors() {
        errors.clear();
    }

    public static ArrayList<UnsatisfiedLinkError> getErrors() {
        return errors;
    }
}
