/*
 mod_MumbleLink - Positional Audio Communication for Minecraft with Mumble
 Copyright 2012 zsawyer (http://sourceforge.net/users/zsawyer)

 This file is part of mod_MumbleLink
 (http://sourceforge.net/projects/modmumblelink/)
 Adapted on this fork : https://github.com/alexandrelefourner/MumbleLink.

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
package fr.aldostra.link.mod.error;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zsawyer
 */
public interface NativeInitErrorHandler {

    public enum NativeInitError {

        NOT_YET_INITIALIZED(-1),
        NO_ERROR(0),
        ERROR_WIN_NO_HANDLE(1),
        ERROR_WIN_NO_STRUCTURE(2),
        ERROR_UNIX_NO_HANDLE(3),
        ERROR_UNIX_NO_STRUCTURE(4);


        private int code;
        private static final Map<Integer, NativeInitError> lookup = new HashMap<Integer, NativeInitError>();

        static {
            for (NativeInitError error : EnumSet.allOf(NativeInitError.class)) {
                lookup.put(error.getCode(), error);
            }
        }

        public static NativeInitError fromCode(int code) {
            return lookup.get(code);
        }

        public int getCode() {
            return code;
        }

        private NativeInitError(int code) {
            this.code = code;
        }
    }

    void handleError(NativeInitError fromCode);
}
