package zsawyer.mods.mumblelink.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import zsawyer.mods.mumblelink.mod_MumbleLink;
import zsawyer.mods.mumblelink.error.ErrorHandlerImpl;
import zsawyer.mods.mumblelink.error.ModErrorHandler;
import zsawyer.mods.mumblelink.error.ModErrorHandler.ModError;
import zsawyer.mumble.jna.LinkAPILibrary;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

public class PackageLibraryLoader implements LibraryLoader {

	@Override
	public LinkAPILibrary loadLibrary(String libraryName)
			throws UnsatisfiedLinkError {

		NativeLibrary loadedLibrary = NativeLibrary.getInstance(libraryName);

		if (loadedLibrary != null) {
			LinkAPILibrary libraryInstance = (LinkAPILibrary) Native
					.loadLibrary(libraryName, LinkAPILibrary.class);
			if (libraryInstance != null) {
				return libraryInstance;
			}
		}

		throw new UnsatisfiedLinkError(
				"Required library could not be loaded, available libraries are incompatible!");

	}

}
