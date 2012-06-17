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
package net.minecraft.src;

import net.minecraft.client.Minecraft;
import net.minecraft.src.mumblelink.NativeInitErrorHandler.NativeInitError;
import net.minecraft.src.mumblelink.*;
import static net.minecraft.src.mumblelink.TestUtilities.*;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.*;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

/**
 *
 * @author zsawyer
 */
@RunWith(JMock.class)
/*
 * these tests just run with whatever library is loaded first so they are not
 * very good tests one would have to find a way to unload the library on each
 * tearDown(). There is also a major issue if these would run in parallel as you
 * cannot garantee that no other library was loaded.
 */
//@Ignore
public class mod_MumbleLinkTest {

    @Rule
    public TestName name = new TestName();
    public static final float IGNORABLE_TICK = 0.0F;
    public static final String MOD_CLASS_NAME = mod_MumbleLink.class.getName();
    public Mockery mockContext;
    public Minecraft mockedGameInstance;
    private mod_MumbleLink modInstance;
    private LibraryLoader mockedLibraryLoader;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        restoreGameExecutionDirectory();
    }

    @Before
    public void setUp() throws Exception {
        mockContext = createMockContext();
        mockedGameInstance = mockContext.mock(Minecraft.class);
        modInstance = new mod_MumbleLink();
        mockedLibraryLoader = injectMockObjectIntoField(mockContext, modInstance,
                "loader", LibraryLoader.class);
    }

    @After
    public void tearDown() throws Exception {
        mockContext = null;
        mockedGameInstance = null;
        modInstance = null;
        mockedLibraryLoader = null;
        System.gc();
    }

    @Test
    public void testLoadSucceeds() throws Exception {
        System.out.println("testLoadSucceeds");

        final ErrorHandlerImpl mockErrorHandler = injectMockObjectIntoField(
                mockContext, modInstance, "errorHandler", ErrorHandlerImpl.class);
        final MumbleInitializer mockIniter = injectMockObjectIntoField(mockContext,
                modInstance, "mumbleInititer", MumbleInitializer.class);
        final Thread mockIniterThread = injectMockObjectIntoField(mockContext,
                modInstance, "mumbleInititerThread", Thread.class);

        Expectations expectations = new Expectations() {

            {
                never(mockErrorHandler).throwError(
                        with(any(ModErrorHandler.ModError.class)),
                        with(any(Throwable.class)));
                oneOf(mockedLibraryLoader).loadLibrary();
                allowing(mockIniter).run();
                oneOf(mockIniterThread).start();
            }
        };

        mockContext.checking(expectations);

        modInstance.load();
    }

    @Test
    public void testOnTickInGame() throws Exception {
        System.out.println("testOnTickInGame");

        final Thread mockIniterThread = injectMockObjectIntoField(mockContext,
                modInstance, "mumbleInititerThread", Thread.class);

        Expectations expectations = new Expectations() {

            {
                oneOf(mockIniterThread).start();
            }
        };

        mockContext.checking(expectations);

        boolean expResult = true;
        boolean result = modInstance.onTickInGame(IGNORABLE_TICK, mockedGameInstance);

        assertEquals(expResult, result);
    }

    @Test
    public void testCallInitMumbleSucceedsWithCall() throws Exception {
        System.out.println("testCallInitMumbleSucceedsWithCall");
        if (isWindows()) {
            setUpModInstance(DIRECTORY_WITH_WINDOWS_NATIVES);

            modInstance = new mod_MumbleLink();

            modInstance.load();

            while (!isLibraryLoaded()) {
                Thread.yield();
            }

            NativeInitError expResult;
            if (isMumbleStarted()) {
                expResult = NativeInitError.NO_ERROR;
            } else {
                expResult = NativeInitError.ERROR_WIN_NO_HANDLE;
            }

            NativeInitError result = modInstance.callInitMumble();

            assertEquals(expResult, result);

            if (!isWindows()) {
                fail("test should only work on windows");
            }
        }
    }

    @Test
    public void testCallUpdateMumbleSucceedsWithCall() throws Exception {
        System.out.println("testCallUpdateMumbleSucceedsWithCall");
        if (isWindows()) {
            setUpModInstance(DIRECTORY_WITH_WINDOWS_NATIVES);

            float[] fAvatarPosition, fAvatarFront, fAvatarTop, fCameraPosition, fCameraFront, fCameraTop;
            fAvatarPosition = fAvatarFront = fAvatarTop = fCameraPosition = fCameraFront = fCameraTop = new float[3];

            String name, description, identity, context;
            name = description = identity = context = "";

            modInstance = new mod_MumbleLink();

            modInstance.load();

            while (!isLibraryLoaded()) {
                Thread.yield();
            }

            NativeUpdateErrorHandler.NativeUpdateError expResult;
            if (isMumbleStarted()) {
                expResult = NativeUpdateErrorHandler.NativeUpdateError.NO_ERROR;
            } else {
                expResult = NativeUpdateErrorHandler.NativeUpdateError.ERROR_NOT_YET_INITIALIZED;
            }

            NativeUpdateErrorHandler.NativeUpdateError result = modInstance.callUpdateMumble(fAvatarPosition, fAvatarFront, fAvatarTop, MOD_CLASS_NAME, MOD_CLASS_NAME, fCameraPosition, fCameraFront, fCameraTop, MOD_CLASS_NAME, MOD_CLASS_NAME);

            assertEquals(expResult, result);

            if (!isWindows()) {
                fail("test should only work on windows");
            }
        }
    }

    @Test
    /*
     * This test cannot work properly as it is dependent on library loading and
     * unloading which is not reliably implemented. Furthermore it can have
     * random behavior depending on which or whether a library was loaded.
     *
     * For now the convention is to only use tests that work expecte valid
     * libraries to be loaded as this applies for more and further tests.
     */
    @Ignore
    public void testCallInitMumbleFailsWithBadLoadedNativeLibrary() throws Exception {
        System.out.println("testCallInitMumbleFailsWithBadLoadedNativeLibrary");
        Expectations expectations = new Expectations() {

            {
                oneOf(mockedLibraryLoader).loadLibrary();
            }
        };

        mockContext.checking(expectations);

        modInstance.load();

        try {
            modInstance.callInitMumble();
            fail("those libraries should cause an error");
        } catch (UnsatisfiedLinkError err) {
            assertContains("initMumble", err.getMessage());
        }
    }

    @Test
    /*
     * This test cannot work properly as it is dependent on library loading and
     * unloading which is not reliably implemented. Furthermore it can have
     * random behavior depending on which or whether a library was loaded.
     *
     * For now the convention is to only use tests that work expecte valid
     * libraries to be loaded as this applies for more and further tests.
     */
    @Ignore
    public void testCallInitMumbleFailsBecauseOfNoLoadedNativeLibrary() throws Exception {
        System.out.println("testCallInitMumbleFailsBecauseOfNoLoadedNativeLibrary");

        // note: load not called here

        try {
            modInstance.callInitMumble();
            fail("those libraries should cause an error");
        } catch (UnsatisfiedLinkError err) {
            assertContains("initMumble", err.getMessage());
        }
    }
}
