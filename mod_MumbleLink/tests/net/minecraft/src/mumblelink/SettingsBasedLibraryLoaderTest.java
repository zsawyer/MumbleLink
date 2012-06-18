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

import net.minecraft.src.mod_MumbleLink;
import static net.minecraft.src.mumblelink.TestUtilities.*;
import static org.junit.Assert.fail;
import org.junit.*;

/**
 *
 * @author zsawyer
 */
public class SettingsBasedLibraryLoaderTest {

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
    public void tearDown() throws Exception {
        restoreGameExecutionDirectory();
    }


    /*
     * We can only test for success as the loading of the library will be
     * permanent through out this test suite run.
     */
    @Test
    public void testSuccessLoadLibraryOnWindows() throws Exception {
        if (isWindows()) {
            setUpModInstance(DIRECTORY_WITH_WINDOWS_NATIVES);
            mod_MumbleLink modInstance = new mod_MumbleLink();
            SettingsBasedLibraryLoader instance = new SettingsBasedLibraryLoader(settings);
            try {
                instance.loadLibrary();
            } catch (UnsatisfiedLinkError err) {
                fail("Libraries were not loaded. " + err.getMessage());
            }
        }
    }
}
