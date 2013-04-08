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
public abstract class BruteForceLibraryLoader implements LibraryLoader {

	public static final String[] PREFIXES = { "", "lib" };
	public static final String[] POSTFIXES = { "", "64" };
	public static final String[] EXTENSIONS = { "dll", "so", "dylib" };

	public boolean libLoaded = false;

	private String libraryName;
	private String libraryFolder;
	
	public BruteForceLibraryLoader(String libraryName) {
		this.libraryName = libraryName;
	}

	@Override
	public void loadLibrary() throws UnsatisfiedLinkError {
		libLoaded = loadLibraryByBruteForce();
		if (!libLoaded) {
			throw new UnsatisfiedLinkError(
					"Required library could not be loaded, available libraries are incompatible!");
		}
	}

	/**
	 * The specified library is attempted to be loaded.
	 * 
	 * @param lib
	 *            The library name to load. This can be short form (e.g. "c"),
	 *            an explicit version (e.g. "libc.so.6"), or the full path to
	 *            the library (e.g. "/lib/libc.so.6").
	 * @return true if loading was successful
	 */
	public abstract boolean attemptLoadLibrary(String lib);

	private boolean loadLibraryByBruteForce() {
		if (alreadyLoaded()) {
			return true;
		}

		boolean loaded = false;
		List<String> candidates = getLibraryCandidates();
		Iterator<String> candidateCrawler = candidates.iterator();
		while (!loaded && candidateCrawler.hasNext()) {
			loaded = attemptLoadLibrary(candidateCrawler.next());
		}
		return loaded;
	}

	private List<String> getLibraryCandidates() {
		List<String> candidates = generateFileNames();
		if (libraryFolder != null) {
			for (String candidate : candidates) {
				candidate = libraryFolder + File.separator + candidate;
			}
		}
		return candidates;
	}

	private List<String> generateFileNames() {
		List<String> filenames = new ArrayList<String>();

		for (String prefix : PREFIXES) {
			for (String postfix : POSTFIXES) {
				for (String extension : EXTENSIONS) {
					filenames.add(generateFileName(prefix, libraryName,
							postfix, extension));
				}
			}
		}

		return filenames;
	}

	private String generateFileName(String prefix, String name, String suffix,
			String extension) {
		return prefix + name + suffix + "." + extension;
	}

	public boolean alreadyLoaded() {
		return libLoaded;
	}

	public String getLibraryName() {
		return libraryName;
	}

	public void setLibraryName(String libraryName) {
		this.libraryName = libraryName;
	}

	public String getLibraryFolder() {
		return libraryFolder;
	}

	public void setLibraryFolder(String libraryFolder) {
		this.libraryFolder = libraryFolder;
	}
	
	
}
