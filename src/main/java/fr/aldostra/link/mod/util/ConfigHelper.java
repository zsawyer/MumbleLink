/*
 mod_MumbleLink - Positional Audio Communication for Minecraft with Mumble
 Adapted by Aldostra Team (http://www.aldostra.fr/)
 Copyright 2011-2013 zsawyer (http://sourceforge.net/users/zsawyer)

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

package fr.aldostra.link.mod.util;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;

/**
 * Helper utility class which provides access to somewhat common functions
 * required with configs.
 *
 * @author zsawyer
 */
public class ConfigHelper {

    /**
     * short-hand function to supplement the given comment with the values
     * supplied
     *
     * @param comment         the comment to be supplemented
     * @param availableValues the values to be appended to the comment
     * @return the compound string result
     */
    public static String configComment(String comment, Object[] availableValues) {
        return comment + System.getProperty("line.separator") + "available values: "
                + Arrays.toString(availableValues);
    }

    /**
     * Short-hand function to get a {@link ForgeConfigSpec.BooleanValue}, providing a comment, translation and
     * default value. The comment will be supplemented with possible values.
     *
     * @param builder      a builder instance to work with
     * @param path         an identifier for this key-value pair
     * @param comment      a hint which will be written into the config file
     * @param translation  the key for the translation string
     * @param defaultValue a default value to use if it wasn't set yet
     * @return the value read from config
     */
    public static ForgeConfigSpec.BooleanValue buildBoolean(ForgeConfigSpec.Builder builder, String path, String comment, String translation, boolean defaultValue) {
        return builder
                .comment(ConfigHelper.configComment(comment,
                        new Boolean[]{true, false}))
                .translation(translation)
                .define(path, defaultValue);
    }
}
