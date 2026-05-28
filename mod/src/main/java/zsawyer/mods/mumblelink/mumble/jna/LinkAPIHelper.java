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

package zsawyer.mods.mumblelink.mumble.jna;

import zsawyer.mods.mumblelink.error.NativeInitErrorHandler.NativeInitError;
import zsawyer.mumble.jna.LinkAPILibrary;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.PrimitiveIterator;

/**
 * @author zsawyer
 */
public final class LinkAPIHelper {
    private LinkAPILibrary libraryInstance;

    public LinkAPIHelper(LinkAPILibrary libraryInstance) {
        this.libraryInstance = libraryInstance;
    }

    public static CharBuffer parseToCharBuffer(int capacity, String value) {
        CharBuffer buffer = CharBuffer.allocate(capacity);
        buffer.rewind();
        buffer.put(value.toCharArray());
        buffer.rewind();
        return buffer;
    }

    public static ByteBuffer parseToByteBuffer(int capacity, String value) {
        ByteBuffer buffer = ByteBuffer.allocate(capacity);
        buffer.rewind();
        buffer.put(value.getBytes());
        buffer.rewind();
        return buffer;
    }

    /**
     * A stable hash function designed for world IDs.
     * Different clients should be able to run this on the same world ID and get the same result.
     * <p>
     * Based on the `djb2` hash function: [Hash Functions](http://www.cse.yorku.ca/~oz/hash.html)
     * <p>
     * reimplementation of https://github.com/magneticflux-/fabric-mumblelink-mod/blob/3866317c64f9b7f5b9b4f17e88cd51c0a717b993/src/main/kotlin/com/skaggsm/mumblelinkmod/client/Utils.kt#L21
     *
     * @param hashingTarget the strings whose character sequence should be hashed
     * @return the stable hash built for the given input
     */
    public static int stableHash(String hashingTarget) {
        int hash = 5381;
        PrimitiveIterator.OfInt characterIterator = hashingTarget.chars().iterator();

        while (characterIterator.hasNext()) {
            hash += (hash << 5) + characterIterator.nextInt();
        }

        return hash;
    }

    public synchronized LinkAPILibrary getLibraryInstance() {
        return libraryInstance;
    }

    public NativeInitError initialize(String pluginName,
                                      String pluginDescription, int pluginUiVersion) {

        int errorCode = libraryInstance.initialize(
                parseToCharBuffer(LinkAPILibrary.MAX_NAME_LENGTH, pluginName),
                parseToCharBuffer(LinkAPILibrary.MAX_DESCRIPTION_LENGTH,
                        pluginDescription), pluginUiVersion);
        return NativeInitError.fromCode(errorCode);
    }
}
