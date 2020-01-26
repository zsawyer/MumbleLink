/*
 mod_MumbleLink - Positional Audio Communication for Minecraft with Mumble
 Adapted by Aldostra Team (http://www.aldostra.fr/)
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
package fr.aldostra.link.mod;

import fr.aldostra.link.mod.api.AldostraMumbleLink;
import fr.aldostra.link.mod.util.ConfigHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

/**
 * utility constants class of frequently used config strings
 *
 * @author zsawyer
 */
public class Config {
    public static final ForgeConfigSpec SPEC;
    /**
     * the target Config to work with
     */
    public static final Config CONFIG;

    public static final String DEBUG_COMMENT = "whether this addon should do stuff";
    public static final String DEBUG_TRANSLATION = AldostraMumbleLink.MOD_ID + ".configgui.debug";
    public static final String DEBUG_PATH = "debug";
    public static final boolean DEBUG_DEFAULT_VALUE = false;
    public static final String ENABLED_COMMENT = "whether this addon should do stuff";
    public static final String ENABLED_TRANSLATION = AldostraMumbleLink.MOD_ID + ".configgui.enabled";
    public static final String ENABLED_PATH = "enabled";
    public static final boolean ENABLED_DEFAULT_VALUE = true;

    public final ForgeConfigSpec.BooleanValue debug;
    public final ForgeConfigSpec.BooleanValue enabled;

    static {
        final Pair<Config, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config::new);

        SPEC = specPair.getRight();
        CONFIG = specPair.getLeft();
    }

    Config(ForgeConfigSpec.Builder builder) {
        builder.comment("Client only settings")
                .push("client");

        debug = ConfigHelper.buildBoolean(builder, DEBUG_PATH, DEBUG_COMMENT, DEBUG_TRANSLATION, DEBUG_DEFAULT_VALUE);
        enabled = ConfigHelper.buildBoolean(builder, ENABLED_PATH, ENABLED_COMMENT, ENABLED_TRANSLATION, ENABLED_DEFAULT_VALUE);

        builder.pop();
    }

    /**
     * frequently used config paths
     *
     * @author zsawyer
     */
    public enum Path {
        enabled, debug;
    }
}
