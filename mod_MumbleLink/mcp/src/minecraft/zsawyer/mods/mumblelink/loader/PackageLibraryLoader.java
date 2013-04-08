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

public class PackageLibraryLoader extends BruteForceLibraryLoader {

	private LinkAPILibrary libraryInstance;
	private File libraryFile;
	private static final ModErrorHandler errorHandler = ErrorHandlerImpl
			.getInstance();

	public PackageLibraryLoader(String libraryName) throws IOException {
		super(libraryName);
	}

	@Override
	public boolean attemptLoadLibrary(String lib) {

		try {
			writeEmbeddedResourceToLocalFile(lib);
		} catch (Exception e) {
			return false;
		}

		NativeLibrary loadedLibrary = NativeLibrary
				.getInstance(libraryFile.getAbsolutePath());		

		if (loadedLibrary != null) {
			libraryInstance = (LinkAPILibrary) Native.loadLibrary(
					libraryFile.getAbsolutePath(), LinkAPILibrary.class);
			if (libraryInstance != null) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 
	 * Writes an embedded resource to a local file
	 * 
	 * @param resourceName
	 *            the name of the resource to be copied
	 * @throws IOException
	 * @throws Throwable
	 */
	private void writeEmbeddedResourceToLocalFile(final String resourceName)
			throws IOException {
		final URL resourceUrl = Resources.getResource(resourceName);

		String[] split = resourceName.split("\\.");
		String extension = split[split.length - 1];
		libraryFile = new File(getLibraryName() + "." + extension);

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(this.libraryFile);
			Resources.copy(resourceUrl, fos);
			fos.flush();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
	}

	public synchronized LinkAPILibrary getLibraryInstance() {
		return libraryInstance;
	}

}
