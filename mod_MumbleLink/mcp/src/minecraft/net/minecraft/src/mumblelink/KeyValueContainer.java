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

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 *
 * @author zsawyer
 */
public class KeyValueContainer<K extends Enum<K>, V extends Enum<V>> {

    private Map<K, String> items;

    public KeyValueContainer(Class<K> classOfK) {
        items = new EnumMap<K, String>(classOfK);
    }

    public boolean compare(K key, V expectedValue) {
        return compare(key, expectedValue.toString());
    }

    private boolean compare(K key, String expectedValue) {
        if (isDefined(key)) {
            String actualValue = get(key);
            return actualValue.equals(expectedValue);
        }

        return false;
    }

    public void define(K key, V value) {
        define(key, value.toString());
    }

    public void define(K key, String value) {
        items.put(key, value.trim());
    }

    public String get(K key) throws NoSuchElementException {
        if (isDefined(key)) {
            return items.get(key).toString();
        }
        throw new NoSuchElementException("Key '" + key.toString() + "' is not defined!");
    }

    public boolean isDefined(K key) {
        return items.containsKey(key);
    }

    public void defineAll(Map<K, String> newSettings) {
        items.putAll(newSettings);
    }

    public Map<K, String> asMap() {
        return new EnumMap<K, String>(items);
    }

    public Map<String, String> asStringsMap() {
        Map<String, String> outputMap = new HashMap<String, String>();
        for (Map.Entry<K, String> entry : items.entrySet()) {
            outputMap.put(entry.getKey().toString(), entry.getValue());
        }
        return outputMap;
    }
}
