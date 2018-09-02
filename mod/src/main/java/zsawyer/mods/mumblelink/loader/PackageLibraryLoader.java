/*
 mod_MumbleLink - Positional Audio Communication for Minecraft with Mumble
 Copyright 2011-2013 zsawyer (http://sourceforge.net/users/zsawyer)

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

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Platform;
import zsawyer.mods.mumblelink.MumbleLinkImpl;
import zsawyer.mumble.jna.LinkAPILibrary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zsawyer
 */
public class PackageLibraryLoader implements LibraryLoader {

    public final static Map<Integer, String> PLATFORM_NAMES_MAPPING = initPlatformNamesMapping();
    public final static Map<Integer, String> PLATFORM_FILE_NAME_MAPPINGS = initPlatformFileNameMappings();
    public static final String filenameConcatinator = "-";
    public static final String RESOURCE_PATH_SEPERATOR = "/";
    protected String libraryName = null;

    private static Map<Integer, String> initPlatformFileNameMappings() {
        final HashMap<Integer, String> mappings = new HashMap<Integer, String>();
        mappings.put(Platform.LINUX, "lib%1s.so");
        mappings.put(Platform.WINDOWS, "%1s.dll");
        mappings.put(Platform.MAC, "lib%1s.dylib");
        return Collections.unmodifiableMap(mappings);
    }

    private static Map<Integer, String> initPlatformNamesMapping() {
        final HashMap<Integer, String> mappings = new HashMap<Integer, String>();
        mappings.put(Platform.LINUX, "linux");
        mappings.put(Platform.WINDOWS, "win32");
        mappings.put(Platform.MAC, "darwin");
        return Collections.unmodifiableMap(mappings);
    }

    @Override
    public LinkAPILibrary loadLibrary(String libraryName)
            throws UnsatisfiedLinkError {
        this.libraryName = libraryName;
        File unpackedLibDir = unpackLibrary();
        NativeLibrary loadedLibrary;
        if (unpackedLibDir != null && unpackedLibDir.exists()) {
            //MumbleLinkImpl.LOG.info("Loading library '" + this.libraryName + "' from: " + unpackedLibDir.getAbsolutePath());
            NativeLibrary.addSearchPath(this.libraryName, unpackedLibDir.getAbsolutePath());
        }
        loadedLibrary = NativeLibrary.getInstance(this.libraryName);

        if (loadedLibrary != null) {
            LinkAPILibrary libraryInstance = (LinkAPILibrary) Native
                    .loadLibrary(this.libraryName, LinkAPILibrary.class);
            if (libraryInstance != null) {
                return libraryInstance;
            }
        }

        throw new UnsatisfiedLinkError(
                "Required library could not be loaded, available libraries are incompatible!");

    }

    private File unpackLibrary() {
        final String folderName = buildFolderName();
        final String fileName = buildFileName();
        final String resourcePath = buildResourcePath(folderName, fileName);

        try {
            final File libFile = extractToTmpDir(resourcePath, fileName);
            return libFile;
        } catch (IOException e) {
            //MumbleLinkImpl.LOG.warn("Problem extracting resource.", e);
            return null;
        }
    }

    private String buildResourcePath(String folderName, String fileName) {
        StringBuilder sb = new StringBuilder();
        sb.append(RESOURCE_PATH_SEPERATOR);
        sb.append(folderName);
        sb.append(RESOURCE_PATH_SEPERATOR);
        sb.append(fileName);
        return sb.toString();
    }

    private String buildFileName() {
        final String fileNameFormat = PLATFORM_FILE_NAME_MAPPINGS.get(Platform.getOSType());
        return String.format(fileNameFormat, this.libraryName);
    }

    private String buildFolderName() {
        final StringBuilder stringBuilder = new StringBuilder();

        final String osType = PLATFORM_NAMES_MAPPING.get(Platform.getOSType());
        stringBuilder.append(osType);

        // there should be a universal dylib for MAC so we can skip the architecture part
        if (Platform.getOSType() != Platform.MAC) {
            String arch = "x86";
            if (Platform.is64Bit()) {
                arch = "x86_64";
            }

            stringBuilder.append(filenameConcatinator);
            stringBuilder.append(arch);
        }
        return stringBuilder.toString();
    }

    private File extractToTmpDir(String resourcePath, String asfileName) throws IOException {
        File tempFile = File.createTempFile("MumbleLink", "native");
        final String tempFolderPath = tempFile.getAbsolutePath();
        if (!tempFile.delete()) {
            throw new IOException("could not delete temporary file '" + tempFolderPath + "'");
        }

        final File tempFolder = new File(tempFolderPath);
        if (!tempFolder.mkdirs()) {
            throw new IOException("could not create directory '" + tempFolderPath + "'");
        }

        tempFile = new File(tempFolderPath + File.separatorChar + asfileName);

        // extract library from jar
        InputStream link = (getClass().getResourceAsStream(resourcePath));
        if (link == null) {
            throw new FileNotFoundException("invalid resource: " + resourcePath);
        }
        org.apache.commons.io.FileUtils.copyInputStreamToFile(link, tempFile.getAbsoluteFile());
        tempFile.deleteOnExit();
        tempFolder.deleteOnExit();
        return tempFolder;
    }
}
