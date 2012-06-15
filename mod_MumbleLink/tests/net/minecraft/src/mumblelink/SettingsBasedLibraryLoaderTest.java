/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.minecraft.src.mumblelink;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author zsawyer
 */
public class SettingsBasedLibraryLoaderTest {

    public static final String FIXTURES_BASE_DIR = "../../../mod_MumbleLink/tests/fixtures/";
    public static final String DIRECTORY_WITH_WINDOWS_NATIVES = FIXTURES_BASE_DIR + "windowsNatives";
    public static final String DIRECTORY_WITH_LINUX_NATIVES = FIXTURES_BASE_DIR + "linuxNatives";
    public static final String DIRECTORY_WITHOUT_NATIVES = FIXTURES_BASE_DIR + "noNatives";
    public static final String DIRECTORY_WITH_OBFUSCATED_NATIVES = FIXTURES_BASE_DIR + "obfuscatedNatives";
    private Settings settings;

    public SettingsBasedLibraryLoaderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        settings = new Settings();
        settings.define(Settings.Key.MOD_NAME, "MumbleLink");
        settings.define(Settings.Key.LIBRARY_NAME, "mod_MumbleLink");
    }

    @After
    public void tearDown() {
    }

    protected void setUpModInstance(String executionDirectory) {
        try {
            setGameExecutionDirectory(executionDirectory);
        } catch (Throwable error) {
            fail("This setup broke. Compare with MCP's Start.main() and possibly update.");
        }
    }

    private void setGameExecutionDirectory(String executionDirectory) throws Exception {
        Field f = Minecraft.class.getDeclaredField("minecraftDir");
        Field.setAccessible(new Field[]{f}, true);
        f.set(null, new File(executionDirectory));
    }

    @Test
    public void testFailLoadLibraryOnNoFiles() throws Exception {
        setUpModInstance(DIRECTORY_WITHOUT_NATIVES);
        SettingsBasedLibraryLoader instance = new SettingsBasedLibraryLoader(settings);
        try {
            instance.loadLibrary();
            fail("Libraries were unexpectedly loaded.");
        } catch (UnsatisfiedLinkError err) {
            assertStartsWith("Library files not found!", err.getMessage());
        }
    }

    @Test
    public void testFailLoadLibraryOnWrongDirectory() throws Exception {
        settings.define(Settings.Key.MOD_NAME, "SomeNameThatDoesntMapToAFolder");
        setUpModInstance(DIRECTORY_WITHOUT_NATIVES);
        SettingsBasedLibraryLoader instance = new SettingsBasedLibraryLoader(settings);
        try {
            instance.loadLibrary();
            fail("Libraries were unexpectedly loaded.");
        } catch (UnsatisfiedLinkError err) {
            assertStartsWith("Library files not found!", err.getMessage());
        }
    }

    @Test
    public void testFailLoadLibraryOnWindows() throws Exception {
        if (isWindows()) {
            setUpModInstance(DIRECTORY_WITH_LINUX_NATIVES);
            SettingsBasedLibraryLoader instance = new SettingsBasedLibraryLoader(settings);
            try {
                instance.loadLibrary();
                fail("Libraries were unexpectedly loaded.");
            } catch (UnsatisfiedLinkError err) {
                assertStartsWith("Required library could not be loaded", err.getMessage());
            }
        }
    }

    @Test
    public void testFailLoadLibraryOnWindowsWithBadNativeLibrary() throws Exception {
        if (isWindows()) {
            setUpModInstance(DIRECTORY_WITH_OBFUSCATED_NATIVES);
            SettingsBasedLibraryLoader instance = new SettingsBasedLibraryLoader(settings);
            try {
                instance.loadLibrary();
                fail("Libraries were unexpectedly loaded.");
            } catch (UnsatisfiedLinkError err) {
                assertStartsWith("Required library could not be loaded", err.getMessage());
            }
        }
    }

    @Test
    public void testSuccessLoadLibraryOnWindows() throws Exception {
        if (isWindows()) {
            setUpModInstance(DIRECTORY_WITH_WINDOWS_NATIVES);
            SettingsBasedLibraryLoader instance = new SettingsBasedLibraryLoader(settings);
            try {
                instance.loadLibrary();
            } catch (UnsatisfiedLinkError err) {
                fail("Libraries were not loaded. " + err.getMessage());
            }
        }
    }

    @Test
    public void testGetErrors() {
        System.out.println("getErrors");
        ArrayList expResult = null;
        ArrayList result = SettingsBasedLibraryLoader.getErrors();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    private void assertStartsWith(String expectedBeginning, String actual) {
        if (!actual.startsWith(expectedBeginning)) {
            throw new ComparisonFailure("String starts do not match", expectedBeginning, actual);
        }

    }

    private boolean isWindows() {
        String osName = System.getProperty("os.name", "unknown");
        return osName.indexOf("win") != -1;
    }
}
