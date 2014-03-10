/*
 mod_MumbleLink - Positional Audio Communication for Minecraft with Mumble
 Copyright 2011-2013 zsawyer (http://sourceforge.net/users/zsawyer)

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

package zsawyer.mods.mumblelink.loader;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import zsawyer.mumble.jna.LinkAPILibrary;

/**
 * @author zsawyer
 */
public class PackageLibraryLoader implements LibraryLoader {

    @Override
    public LinkAPILibrary loadLibrary(String libraryName)
            throws UnsatisfiedLinkError {

        NativeLibrary loadedLibrary = NativeLibrary.getInstance(libraryName);

        if (loadedLibrary != null) {
            LinkAPILibrary libraryInstance = (LinkAPILibrary) Native
                    .loadLibrary(libraryName, LinkAPILibrary.class);
            if (libraryInstance != null) {
                return libraryInstance;
            }
        }

        throw new UnsatisfiedLinkError(
                "Required library could not be loaded, available libraries are incompatible!");

    }

}
