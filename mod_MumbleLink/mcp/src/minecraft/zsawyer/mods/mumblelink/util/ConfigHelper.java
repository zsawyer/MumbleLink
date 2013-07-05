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
