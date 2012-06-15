/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.minecraft.src;

import java.io.File;
import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import net.minecraft.src.mumblelink.NativeInitErrorHandler.NativeInitError;
import net.minecraft.src.mumblelink.NativeUpdateErrorHandler.NativeUpdateError;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;

/**
 *
 * @author zsawyer
 */
@RunWith(JMock.class)
public class mod_MumbleLinkTest {

    public static final float IGNORABLE_TICK = 0.0F;
    public static final String MOD_CLASS_NAME = mod_MumbleLink.class.getName();
    public static Mockery mockContext;
    public static Minecraft mockedGameInstance;

    public mod_MumbleLinkTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        mockContext = createMockContext();
        mockedGameInstance = createGameInstance(mockContext);
    }

    private static Mockery createMockContext() {
        Mockery context = new JUnit4Mockery();
        context.setImposteriser(ClassImposteriser.INSTANCE);
        return context;
    }

    private static Minecraft createGameInstance(Mockery mockContext) {
        return mockContext.mock(Minecraft.class);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    @Test
    public void testLoad() {
        mod_MumbleLink instance = new mod_MumbleLink();
        instance.load();
    }


    @Test
    public void testOnTickInGame() {
        mod_MumbleLink instance = new mod_MumbleLink();
        boolean expResult = true;
        boolean result = instance.onTickInGame(IGNORABLE_TICK, mockedGameInstance);
        assertEquals(expResult, result);
    }

    @Test
    public void testCallInitMumble() {
        System.out.println("callInitMumble");
        mod_MumbleLink instance = new mod_MumbleLink();
        NativeInitError expResult = null;
        NativeInitError result = instance.callInitMumble();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void testCallUpdateMumble() {
        System.out.println("callUpdateMumble");
        float[] fAvatarPosition = null;
        float[] fAvatarFront = null;
        float[] fAvatarTop = null;
        String name = "";
        String description = "";
        float[] fCameraPosition = null;
        float[] fCameraFront = null;
        float[] fCameraTop = null;
        String identity = "";
        String context = "";
        mod_MumbleLink instance = new mod_MumbleLink();
        NativeUpdateError expResult = null;
        NativeUpdateError result = instance.callUpdateMumble(fAvatarPosition, fAvatarFront, fAvatarTop, name, description, fCameraPosition, fCameraFront, fCameraTop, identity, context);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetVersion() {
        System.out.println("getVersion");
        mod_MumbleLink instance = new mod_MumbleLink();
        String expResult = "";
        String result = instance.getVersion();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
