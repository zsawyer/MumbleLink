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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 *
 * @author zsawyer
 */
public class ErrorHandler extends Singleton {

    public enum ModError {

        CONFIG_FILE_READ("Config not loaded! Check file permissions."),
        CONFIG_FILE_SYNTAX("Unrecognized key or value in config file."),
        CONFIG_FILE_INVALID_VALUE("Value in config file is invalid."),
        LIBRARY_LOAD_FAILED("Couldn't load library.");
        private String message;

        private ModError(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return message;
        }
    }

    public enum NativeError {

        NO_ERROR(0);
        private int code;
        private static final Map<Integer, NativeError> lookup = new HashMap<Integer, NativeError>();

        static {
            for (NativeError error : EnumSet.allOf(NativeError.class)) {
                lookup.put(error.getCode(), error);
            }
        }

        public static NativeError fromCode(int code) {
            return lookup.get(code);
        }

        public int getCode() {
            return code;
        }

        private NativeError(int code) {
            this.code = code;
        }


    }

    public void throwError(ModError modError, Throwable err) {
        modloaderLog(Level.SEVERE, err.getMessage(), err);
        haltMinecraftUsingAnException(modError.message, err);
    }

    private void haltMinecraftUsingAnException(String message, Throwable err) {
        ModLoader.throwException("Error in mod "
                + mod_MumbleLink.modName + mod_MumbleLink.modVersion
                + ": " + message,
                err);
    }

    public void handleError(ModError err, Throwable stack) {
        chatMessage(err.message);

        modloaderLog(Level.WARNING, err.message, stack);
    }

    private void chatMessage(String message) {
        ModLoader.getMinecraftInstance().ingameGUI.addChatMessage("[MumbleLink] Error: " + message);
    }

    private void modloaderLog(Level severity, String message, Throwable stack) {
        ModLoader.getLogger().log(severity,
                "[" + mod_MumbleLink.modName + mod_MumbleLink.modVersion + "]"
                + "[" + severity.getLocalizedName() + "]"
                + message,
                stack);
    }

    void handleError(NativeError fromCode, Object object) {
        // we'll just ignore native errors for now, else it would probably get too spammy
    }
}
