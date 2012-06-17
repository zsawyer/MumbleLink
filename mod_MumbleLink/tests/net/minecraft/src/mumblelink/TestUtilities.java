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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.*;
import javax.print.DocFlavor;
import net.minecraft.client.Minecraft;
import net.minecraft.src.mod_MumbleLink;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.ComparisonFailure;
import static org.junit.Assert.*;

/**
 *
 * @author zsawyer
 */
public class TestUtilities {

    public static final String FIXTURES_BASE_DIR = "../../../mod_MumbleLink/tests/fixtures/";
    public static final String DIRECTORY_WITH_WINDOWS_NATIVES = FIXTURES_BASE_DIR + "windowsNatives";
    public static final String DIRECTORY_WITH_LINUX_NATIVES = FIXTURES_BASE_DIR + "linuxNatives";
    public static final String DIRECTORY_WITHOUT_NATIVES = FIXTURES_BASE_DIR + "noNatives";
    public static final String DIRECTORY_WITH_OBFUSCATED_NATIVES = FIXTURES_BASE_DIR + "obfuscatedNatives";
    private static File originalGameExecutionDirectory;
    private static final String FIELD_NAME_OF_EXECUTION_DIRECTORY = "minecraftDir";
    public mod_MumbleLink modInstance;

    private TestUtilities() {
    }

    public static boolean isWindows() {
        String osName = System.getProperty("os.name", "unknown");
        return osName.toLowerCase().indexOf("win") != -1;
    }

    public static void assertStartsWith(String expectedBeginning, String actual) {
        if (!actual.startsWith(expectedBeginning)) {
            throw new ComparisonFailure("String starts do not match", expectedBeginning, actual);
        }
    }

    public static void assertContains(String expectedToken, String actual) {
        if (actual.indexOf(expectedToken) == -1) {
            throw new ComparisonFailure("expected String was not found", expectedToken, actual);
        }
    }

    public static synchronized void setUpModInstance(String executionDirectory) throws Exception {
        setGameExecutionDirectory(executionDirectory);
    }

    public static synchronized void setGameExecutionDirectory(String executionDirectory) throws Exception {
        setGameExecutionDirectory(new File(executionDirectory));
    }

    public static synchronized void setGameExecutionDirectory(File executionDirectory) throws Exception {
        rememberTheVeryFirstExecutionDirectory();

        Field f = hookIntoMinecraftField(FIELD_NAME_OF_EXECUTION_DIRECTORY);
        f.set(null, executionDirectory);
    }

    private static synchronized Field hookIntoMinecraftField(String fieldName) throws Exception {
        /*
         * If this setup broke compare with MCP's Start.main() and possibly
         * update.
         */
        Field f = Minecraft.class.getDeclaredField(fieldName);
        Field.setAccessible(new Field[]{f}, true);
        return f;
    }

    private static void rememberTheVeryFirstExecutionDirectory() throws Exception {
        if (originalGameExecutionDirectory == null) {
            originalGameExecutionDirectory = getGameExecutionDirectory();
        }
    }

    public static synchronized File getGameExecutionDirectory() throws Exception {
        Field f = hookIntoMinecraftField(FIELD_NAME_OF_EXECUTION_DIRECTORY);
        return (File) f.get(null);
    }

    public static void restoreGameExecutionDirectory() throws Exception {
        setGameExecutionDirectory(originalGameExecutionDirectory);
    }

    public static <T> T injectMockObjectIntoField(Mockery mockContext,
            Object injectionTarget, String fieldName, Class<? extends T> fieldType)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        final T mockedObject = mockContext.mock(fieldType);
        injectIntoField(injectionTarget, fieldName, mockedObject);
        return mockedObject;
    }

    public static void injectIntoField(Object objectWithField, String fieldName,
            Object newValue)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field fieldToInjectTo = objectWithField.getClass().getDeclaredField(fieldName);
        Field.setAccessible(new Field[]{fieldToInjectTo}, true);
        fieldToInjectTo.set(objectWithField, newValue);
    }

    public static String[] getLoadedLibraries(final ClassLoader loader) throws Exception {
        Field librariesField = ClassLoader.class.getDeclaredField("loadedLibraryNames");
        librariesField.setAccessible(true);

        final Vector<String> libraries = (Vector<String>) librariesField.get(loader);
        return libraries.toArray(new String[]{});
    }

    public static boolean isLibraryLoaded() throws Exception {
        String[] libraries = getLoadedLibraries(LibraryLoader.class.getClassLoader());
        for (String lib : libraries) {
            if (lib.indexOf("mod_MumbleLink") != -1) {
                return true;
            }
        }
        return false;
    }

    public static Mockery createMockContext() {
        Mockery context = new JUnit4Mockery();
        context.setImposteriser(ClassImposteriser.INSTANCE);
        return context;
    }

    public static boolean isMumbleStarted() throws Exception {
        return isProcess("mumble");
    }

    public static boolean isProcess(String name) throws Exception {
        List<String> processes = getProcessList();
        for(String process : processes) {
            if(process.indexOf(name) != -1) {
                return true;
            }
        }
        
        return false;
    }

    private static List<String> getProcessList() throws Exception {

        InputStream rawProcessList = getProcessListStream();
        BufferedReader readableProcessList =
                new BufferedReader(new InputStreamReader(rawProcessList));

        return parseProcessList(readableProcessList);
    }


    private static InputStream getProcessListStream() throws Exception {
        Process p;
        if (isWindows()) {
            p = Runtime.getRuntime().exec("tasklist.exe");
        } else {
            p = Runtime.getRuntime().exec("ps -e");
        }
        return p.getInputStream();
    }

    private static List<String> parseProcessList(BufferedReader readableProcessList) throws Exception {
        List<String> processes = new ArrayList<String>();

        String line;
        while ((line = readableProcessList.readLine()) != null) {
            processes.add(line);
        }
        readableProcessList.close();

        return processes;
    }

}
