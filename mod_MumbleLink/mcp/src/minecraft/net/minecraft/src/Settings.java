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

import java.io.*;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zsawyer
 */
public class Settings {

    private static final ErrorHandler errorHandler = ErrorHandler.getInstance();
    private static final String KEY_VALUE_SEPERATOR = ":";
    private Map<Key, String> items;

    public Settings() {
        items = new EnumMap<Key, String>(Key.class);
    }

    public enum Key {

        MUMBLE_CONTEXT("mumbleContext"),
        NOTIFICATION_DELAY_IN_MILLI_SECONDS("notifyDelayMS"),
        LIBRARY_NAME("libraryName"),
        LIBRARY_FILE_PATH("libraryFilePath");
        private final String text;
        private static final Map<String, Key> lookup = new HashMap<String, Key>();

        static {
            for (Key key : EnumSet.allOf(Key.class)) {
                lookup.put(key.toString(), key);
            }
        }

        private Key(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }

        public static Key fromText(String text) {
            return lookup.get(text);
        }
    }

    public enum PresetValue {

        /**
         * There is no differentiation whether or not a player is in the same
         * world/minecraft server; The linking will naively assume that all
         * players are in the same world on the same server.
         *
         * For example:
         *
         * If a player A in the nether is located at 0-0-0 and a player B in the
         * normal world is located at 1-0-0 then A will hear B directionally as
         * if A is just one meter away. Additionally this also applies to
         * different worlds on different servers. It will have an effect of an
         * invisible person speaking. I am assuming that this phenomena is
         * rarely a problem since this would require multiple players to be in
         * the same mumble channel while being distributed over different worlds
         * and servers.
         *
         *
         * !!!Caution!!!
         *
         * Mumble defaults unlinked players to be heard full volume and centered
         * ("force center"). So the more accurate the context is the more people
         * will be heard "force center" since they are "unlinked".
         */
        CONTEXT_ALL_TALK("MinecraftAllTalk"),
        CONTEXT_WORLD("world");
        private static final Map<String, PresetValue> lookup = new HashMap<String, PresetValue>();

        static {
            for (PresetValue value : EnumSet.allOf(PresetValue.class)) {
                lookup.put(value.toString(), value);
            }
        }

        private PresetValue(final String text) {
            this.text = text;
        }
        private final String text;

        @Override
        public String toString() {
            return text;
        }

        public static PresetValue fromText(String text) {
            return lookup.get(text);
        }
    }

    public boolean compare(Key key, PresetValue expectedValue) {
        return compare(key, expectedValue.toString());
    }

    private boolean compare(Key key, String expectedValue) {
        if (isDefined(key)) {
            String actualValue = items.get(key);
            return actualValue.equals(expectedValue);
        }

        return false;
    }

    public boolean isDefined(Key key) {
        return items.containsKey(key);
    }

    public void define(Key key, PresetValue value) {
        define(key, value.toString());
    }

    public void define(Key key, String value) {
        items.put(key, value.trim());
    }

    String get(Key key) throws IndexOutOfBoundsException {
        if (isDefined(key)) {
            return items.get(key).toString();
        }
        throw new IndexOutOfBoundsException("'" + key.toString()
                + "' is not defined in config file");
    }

    public void loadFromFile(File sourceFile) throws IOException {
        BufferedReader reader = createLineReader(sourceFile);
        Map<Key, String> newSettings = parseLines(reader);
        items.putAll(newSettings);
    }

    private BufferedReader createLineReader(File sourceFile) throws FileNotFoundException {
        return new BufferedReader(new FileReader(sourceFile));
    }

    private Map<Key, String> parseLines(BufferedReader reader) throws IOException {

        Map<Key, String> results = new EnumMap<Key, String>(Key.class);

        String currentLineInFile;
        while ((currentLineInFile = reader.readLine()) != null) {
            results.putAll(parseLine(currentLineInFile));
        }

        return results;
    }

    private Map<Key, String> parseLine(String line) {
        Map<Key, String> result = new EnumMap<Key, String>(Key.class);

        String[] setting = line.split(KEY_VALUE_SEPERATOR, 2);

        if (setting.length == 2) {
            define(setting[0].trim(),
                    setting[1].trim());
        }

        return result;
    }

    private void define(String keyName, String valueName) {
        Key key;
        try {
            key = Key.fromText(keyName.trim());
            define(key, valueName);
        } catch (IllegalArgumentException ex) {
            errorHandler.handleError(ErrorHandler.ModError.CONFIG_FILE_SYNTAX, ex);
        }
    }

    public int getInt(Key key) {
        return Integer.parseInt(get(key));
    }
}
