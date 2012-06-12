/*
 mod_Mumble - Positional Audio Communication for Minecraft with Mumble
 Copyright 2011 zsawyer (http://sourceforge.net/users/zsawyer)

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

import java.io.File;
import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.*;
import org.junit.runner.RunWith;

/**
 *
 * @author zsawyer, 2012-06-11
 */
public class MumbleLinkIntegrationTest extends MumbleLinkTest {

    public MumbleLinkIntegrationTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        MumbleLinkTest.setUpClass();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testOnTickInGame() {

        boolean expResult = true;

//        mockContext.checking(new Expectations() {
//            {
//            }
//        });

        boolean result = testSubjectInstance.onTickInGame(IGNORABLE_TICK, mockedGameInstance);
        assertEquals(expResult, result);


        mockContext.assertIsSatisfied();

    }

    /**
     * Test of getVersion method, of class mod_MumbleLink.
     */
    @Test
    @Ignore
    public void testGetVersion() {
        System.out.println("getVersion");        
        String expResult = "";
        String result = testSubjectInstance.getVersion();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void testLoadingFailsWhenLibrariesNotFound() {
        setUpModInstance(DIRECTORY_WITHOUT_NATIVES);

        try {
            tryModInstantiation();
        } catch (InstantiationError err) {
            Throwable originalException = err.getCause().getCause().getCause();
            assertEquals(UnsatisfiedLinkError.class, originalException);
            boolean comparisonResult = err.getCause().getMessage().contains("Couldn't load library for mod");
            assertEquals(true, comparisonResult);
        }

        fail("This test should use a directory that does NOT have the libraries included!");

    }

}
