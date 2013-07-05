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

import java.util.Arrays;

import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ConfigHelper {

	private Configuration config;

	public ConfigHelper(Configuration config) {
		this.config = config;
	}

	public ConfigHelper(FMLPreInitializationEvent event) {
		this.config = new Configuration(event.getSuggestedConfigurationFile());
	}

	public Configuration getConfig() {
		return config;
	}

	public static String configComment(String comment, Object[] availableValues) {
		return comment + System.lineSeparator() + "available values: "
				+ Arrays.toString(availableValues);
	}

	public void commit() {
		if (config.hasChanged()) {
			config.save();
		}
	}

	public boolean loadBoolean(String key, String comment, boolean defaultValue) {
		boolean value = config.get(
				Configuration.CATEGORY_GENERAL,
				key,
				defaultValue,
				ConfigHelper.configComment(comment,
						new Boolean[] { true, false }))
				.getBoolean(defaultValue);
		commit();
		return value;
	}

	public boolean loadEnabled(boolean defaultValue) {
		return loadBoolean(Config.Key.enabled.toString(),
				"whether this addon should do stuff", defaultValue);
	}

	public boolean loadDebug(boolean defaultValue) {
		return loadBoolean(Config.Key.debug.toString(),
				"whether to turn on debuggin (extended logging)", defaultValue);
	}

	public static class Config {
		public enum Key {
			enabled, debug;
		}

	}

}
