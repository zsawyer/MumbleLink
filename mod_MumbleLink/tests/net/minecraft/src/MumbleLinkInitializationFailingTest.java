/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.minecraft.src;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author zsawyer
 */
public class MumbleLinkInitializationFailingTest extends MumbleLinkTest {

    private final String expectedSecondaryErrorMessage = "Library files not found! Searched in:";
    

    @BeforeClass
    public static void setUpClass() throws Exception {
        unloadModClass();
    }

    @Override
    public void setUp() {        
        // override to avoid default setup with working mod instance
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @After
    public void tearDown() {  
        unloadModClass();
    }


    @Test
    public void testLoadingFailsWhenLibrariesNotFound() {
        setUpModInstance(DIRECTORY_WITHOUT_NATIVES);

        try {
            tryModClassLoading();
            fail("This test should use a directory that does NOT have the libraries included!");
        } catch (InstantiationError err) {
            Throwable wrapperException = err.getCause().getCause();
            Throwable initiateException = wrapperException.getCause();


            assertEquals(RuntimeException.class, wrapperException.getClass());
            assertEquals(UnsatisfiedLinkError.class, initiateException.getClass());
            assertMessageContains(expectedSecondaryErrorMessage, initiateException);
        }

    }

    private void assertMessageContains(String expectedPartialMessage, Throwable throwableWithActualMessage) {
        boolean comparisonResult = throwableWithActualMessage.getMessage().contains(expectedPartialMessage);
        assertEquals(true, comparisonResult);
    }

    @Test
    public void testLoadingFailsWhenUsingObfuscatedLibraries() {
        setUpModInstance(DIRECTORY_WITH_OBFUSCATED_NATIVES);

        try {
            tryModClassLoading();
            fail("This test should use a directory that does NOT have working libraries included!");
        } catch (InstantiationError err) {
            Throwable wrapperException = err.getCause().getCause();
            Throwable initiateException = wrapperException.getCause();


            assertEquals(RuntimeException.class, wrapperException.getClass());
            assertEquals(UnsatisfiedLinkError.class, initiateException.getClass());
            assertMessageContains(expectedSecondaryErrorMessage, initiateException);
        }

    }


}
