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

package zsawyer.mods.mumblelink.util;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.rmi.activation.Activatable;
import java.util.Arrays;

/**
 * Helper utility class which provides access to somewhat common functions
 * required with {@link Configuration}s.
 *
 * @author zsawyer
 */
public class ConfigHelper {

    /**
     * the target configuration to work with
     */
    private Configuration config;

    /**
     * initialize this helper with a target configuration
     *
     * @param config target configuration
     */
    public ConfigHelper(Configuration config) {
        this.config = config;
    }

    /**
     * initialize this helper but get the configuration from the event
     *
     * @param event event from which to get the configuration
     */
    public ConfigHelper(FMLPreInitializationEvent event) {
        this.config = new Configuration(event.getSuggestedConfigurationFile());
    }

    /**
     * getter for the target configuration
     *
     * @return the configuration which this helper is set up for
     */
    public Configuration getConfig() {
        return config;
    }

    /**
     * save outstanding changes defensively only if changes occurred
     */
    public void commit() {
        if (config.hasChanged()) {
            config.save();
        }
    }

    /**
     * Short-hand function to get (and save) a boolean, providing a comment and
     * default value. The comment will be supplemented with possible values.
     *
     * @param key          an identifier for this key-value pair
     * @param comment      a hint which will be written into the config file
     * @param defaultValue a default value to use if it wasn't set yet
     * @return the value read from config
     */
    public boolean loadBoolean(String key, String comment, boolean defaultValue) {
        boolean value = config.get(
                Configuration.CATEGORY_GENERAL,
                key,
                defaultValue,
                ConfigHelper.configComment(comment,
                        new Boolean[]{true, false}))
                .getBoolean(defaultValue);
        commit();
        return value;
    }

    /**
     * shorthand for getting the "enabled" boolean usually used inconjunction
     * with {@link Activatable}
     *
     * @param defaultValue a default value to use if it wasn't set yet
     * @return the value read from config
     */
    public boolean loadEnabled(boolean defaultValue) {
        return loadBoolean(Config.Key.enabled.toString(),
                "whether this addon should do stuff", defaultValue);
    }

    /**
     * shorthand for getting the "debug" boolean
     *
     * @param defaultValue a default value to use if it wasn't set yet
     * @return the value read from config
     */
    public boolean loadDebug(boolean defaultValue) {
        return loadBoolean(Config.Key.debug.toString(),
                "whether to turn on debuggin (extended logging)", defaultValue);
    }

    /**
     * utility constants class of frequently used config strings
     *
     * @author zsawyer
     */
    public static class Config {
        /**
         * frequently used config key strings
         *
         * @author zsawyer
         */
        public enum Key {
            enabled, debug;
        }

    }

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

}
