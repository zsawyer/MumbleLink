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
package net.minecraft.src.MumbleLink;

/**
 *
 *
 * @author zsawyer
 */
public abstract class Singleton {

    private static Class<?> getClass(StackTraceElement stackTraceElement) {
        try {
            String className = stackTraceElement.getClassName();
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            throw new Error(ex);
        }
    }

    protected static class SingletonHolder {

        public static final Singleton instance;

        static {
            try {
                instance = (Singleton) getClassStatic().newInstance();
            } catch (Throwable t) {
                throw new Error(t);
            }
        }
    }

    public static <T extends Singleton> T getInstance() {
        return (T) SingletonHolder.instance;
    }

    /*
     * private static Singleton getInstance() { return SingletonHolder.instance;
    }
     */
    private static Class<?> getClassStatic() {
        try {
            throw new Exception();
        } catch (Exception e) {
            StackTraceElement[] sTrace = e.getStackTrace();
            // sTrace[0] will be always there
            return getClass(sTrace[0]);
        }
    }
}
