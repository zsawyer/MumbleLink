package net.minecraft.src;

/**
 *
 * @author zsawyer
 */
public class InterrogativeClassLoader extends ClassLoader {

    private static InterrogativeClassLoader instance;

    public static InterrogativeClassLoader getInstance() {
        if(instance == null) {
            instance = new InterrogativeClassLoader();
        }
        return instance;
    }

    
    boolean isClassLoaded(String className) {
        Class<?> found = findLoadedClass(className);
        return found != null;
    }

}
