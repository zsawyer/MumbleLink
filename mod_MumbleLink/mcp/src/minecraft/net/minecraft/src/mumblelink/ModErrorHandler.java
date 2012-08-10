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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zsawyer
 */
public interface ModErrorHandler {

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

   

    void handleError(ModError err, Throwable stack);

    void throwError(ModError modError, Throwable err);
}
