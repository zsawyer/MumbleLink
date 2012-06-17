///*
// mod_MumbleLink - Positional Audio Communication for Minecraft with Mumble
// Copyright 2012 zsawyer (http://sourceforge.net/users/zsawyer)
//
// This file is part of mod_MumbleLink
// (http://sourceforge.net/projects/modmumblelink/).
//
// mod_MumbleLink is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// mod_MumbleLink is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with mod_MumbleLink.  If not, see <http://www.gnu.org/licenses/>.
//
// */
//package net.minecraft.src.mumblelink;
//
//import java.io.File;
//import java.lang.reflect.Field;
//import net.minecraft.client.Minecraft;
//import net.minecraft.src.mod_MumbleLink;
//import static org.junit.Assert.fail;
//import org.junit.*;
//import static net.minecraft.src.mumblelink.TestUtilities.*;
//
///**
// *
// * @author zsawyer
// */
//public class SettingsBasedLibraryLoaderTest {
//
//    private Settings settings;
//
//    public SettingsBasedLibraryLoaderTest() {
//    }
//
//    @BeforeClass
//    public static void setUpClass() throws Exception {
//    }
//
//    @AfterClass
//    public static void tearDownClass() throws Exception {
//    }
//
//    @Before
//    public void setUp() {
//        settings = new Settings();
//        settings.define(Settings.Key.MOD_NAME, "MumbleLink");
//        settings.define(Settings.Key.LIBRARY_NAME, "mod_MumbleLink");
//
//        modInstance = new mod_MumbleLink()
//    }
//
//    @After
//    public void tearDown() throws Exception{
//        restoreGameExecutionDirectory();
//    }
//
//    @Test
//    public void testFailLoadLibraryOnNoFiles() throws Exception {
//        synchronized (modLoaderFiddlingSemaphore) {
//            setUpModInstance(DIRECTORY_WITHOUT_NATIVES);
//            SettingsBasedLibraryLoader instance = new SettingsBasedLibraryLoader(settings);
//            try {
//                instance.loadLibrary();
//                fail("Libraries were unexpectedly loaded.");
//            } catch (UnsatisfiedLinkError err) {
//                assertStartsWith("Library files not found!", err.getMessage());
//            }
//        }
//    }
//
//    @Test
//    public void testFailLoadLibraryOnWrongDirectory() throws Exception {
//        settings.define(Settings.Key.MOD_NAME, "SomeNameThatDoesntMapToAFolder");
//        synchronized (modLoaderFiddlingSemaphore) {
//            setUpModInstance(DIRECTORY_WITHOUT_NATIVES);
//            SettingsBasedLibraryLoader instance = new SettingsBasedLibraryLoader(settings);
//            try {
//                instance.loadLibrary();
//                fail("Libraries were unexpectedly loaded.");
//            } catch (UnsatisfiedLinkError err) {
//                assertStartsWith("Library files not found!", err.getMessage());
//            }
//        }
//    }
//
//    @Test
//    public void testFailLoadLibraryOnWindows() throws Exception {
//        if (isWindows()) {
//            synchronized (modLoaderFiddlingSemaphore) {
//                setUpModInstance(DIRECTORY_WITH_LINUX_NATIVES);
//                SettingsBasedLibraryLoader instance = new SettingsBasedLibraryLoader(settings);
//                try {
//                    instance.loadLibrary();
//                    fail("Libraries were unexpectedly loaded.");
//                } catch (UnsatisfiedLinkError err) {
//                    assertStartsWith("Required library could not be loaded", err.getMessage());
//                }
//            }
//        }
//    }
//
//    @Test
//    public void testSuccessLoadLibraryOnWindows() throws Exception {
//        if (isWindows()) {
//            synchronized (modLoaderFiddlingSemaphore) {
//                setUpModInstance(DIRECTORY_WITH_WINDOWS_NATIVES);
//                SettingsBasedLibraryLoader instance = new SettingsBasedLibraryLoader(settings);
//                try {
//                    instance.loadLibrary();
//                } catch (UnsatisfiedLinkError err) {
//                    fail("Libraries were not loaded. " + err.getMessage());
//                }
//            }
//        }
//    }
//}
