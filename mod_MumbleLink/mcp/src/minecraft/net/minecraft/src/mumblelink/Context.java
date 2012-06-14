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

import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author zsawyer
 */
public class Context extends KeyValueContainer<Context.Key, Context.PresetValue> {

    private static final ModErrorHandler errorHandler = ErrorHandlerImpl.getInstance();

    public Context() {
        super(Context.Key.class);
    }

    public enum Key {

        GAME("game"),
        WORLD_SEED("WorldSeed"),
        WORLD_NAME("WorldName");
        private final String text;

        private Key(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public enum PresetValue {

        MINECRAFT("Minecraft");
        private final String text;

        private PresetValue(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public String encodeJSON(int maxContextSizeInBytes) {
        JSONObject contextJSONified = asJSON();

        while (isTooLong(contextJSONified.toString(), maxContextSizeInBytes)) {
            contextJSONified = condenseJSON(contextJSONified);
        }

        return contextJSONified.toString();

    }

    private JSONObject asJSON() {
        Map<String, String> contextStringsMap = asStringsMap();

        JSONObject contextJSONified = new JSONObject(contextStringsMap);

        return contextJSONified;
    }

    private boolean isTooLong(String jsonString, int maxContextSizeInBytes) {
        // TODO: test threshold
        return jsonString.getBytes().length >= maxContextSizeInBytes;
    }

    private JSONObject condenseJSON(JSONObject contextJSONified) {
        try {
            contextJSONified = removeOneCharacterOffEachValue(contextJSONified);
        } catch (JSONException cause) {
            JSONException ex = new JSONException("Context generation failed. Use AllTalk in the config file instead. And contact the developer");
            ex.initCause(cause);

            errorHandler.throwError(ModErrorHandler.ModError.CONFIG_FILE_READ, ex);
        }

        return contextJSONified;
    }

    private JSONObject removeOneCharacterOffEachValue(JSONObject contextJSONified) throws JSONException {
        Iterator<String> keyIterator = contextJSONified.keys();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            Object value;
            try {
                String valueString = contextJSONified.getString(key);
                value = removeLastCharacter(valueString);
            } catch (JSONException ex) {
                // we'll ignore this for now, since it shouldn't happen with our
                //  flat structure
                // hint: this will be a pain in the butt when you later want to
                // support a cascaded object structure
                value = contextJSONified.get(key);
            }

            contextJSONified.put(key, value);
        }

        return contextJSONified;
    }

    private Object removeLastCharacter(String valueString) {
        int firstIndex = 0;
        int secondToLastIndex = valueString.length() - 2;
        return valueString.substring(firstIndex, secondToLastIndex);
    }
}
