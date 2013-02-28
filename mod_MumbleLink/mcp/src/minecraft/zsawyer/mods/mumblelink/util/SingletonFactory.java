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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 *
 * @author zsawyer
 */
public class SingletonFactory {

    private static Map<Class<?>, Object> instances = new ConcurrentHashMap<Class<?>, Object>();

    public static <T> T getInstance(Class<T> classToGetInstanceOf) throws InstantiationException, IllegalAccessException {
        T instance = classToGetInstanceOf.cast(instances.get(classToGetInstanceOf));
        if (instance == null) {
            instance = classToGetInstanceOf.newInstance();
            instances.put(classToGetInstanceOf, instance);
        }

        return instance;
    }

    private SingletonFactory() {
    }
}
