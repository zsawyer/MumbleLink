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
package zsawyer.mods.mumblelink;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import zsawyer.mods.mumblelink.api.MumbleLink;
import zsawyer.mods.mumblelink.util.ConfigHelper;

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
    public static final String DEBUG_TRANSLATION = MumbleLink.MOD_ID + ".configgui.debug";
    public static final String DEBUG_PATH = "debug";
    public static final boolean DEBUG_DEFAULT_VALUE = false;
    public static final String ENABLED_COMMENT = "whether this addon should do stuff";
    public static final String ENABLED_TRANSLATION = MumbleLink.MOD_ID + ".configgui.enabled";
    public static final String ENABLED_PATH = "enabled";
    public static final boolean ENABLED_DEFAULT_VALUE = true;

    public static final String DIMENSIONAL_HEIGHT_COMMENT = "set the maximum expected height of dimensions";
    public static final String DIMENSIONAL_HEIGHT_TRANSLATION = MumbleLink.MOD_ID + ".configgui.dimensionalHeight";
    public static final String DIMENSIONAL_HEIGHT_PATH = "dimensionalHeight";
    public static final int DIMENSIONAL_HEIGHT_DEFAULT_VALUE = 512;


    public final ForgeConfigSpec.BooleanValue debug;
    public final ForgeConfigSpec.BooleanValue enabled;
    public final ForgeConfigSpec.IntValue dimensionalHeight;

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
        dimensionalHeight = ConfigHelper.buildInt(builder, DIMENSIONAL_HEIGHT_PATH, DIMENSIONAL_HEIGHT_COMMENT, DIMENSIONAL_HEIGHT_TRANSLATION, DIMENSIONAL_HEIGHT_DEFAULT_VALUE);

        builder.pop();
    }

    /**
     * frequently used config paths
     *
     * @author zsawyer
     */
    public enum Path {
        enabled, debug, dimensionalHeight;
    }
}
