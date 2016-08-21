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

package zsawyer.mods.mumblelink.addons.pa.es;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;
import zsawyer.mods.mumblelink.api.Activateable;
import zsawyer.mods.mumblelink.api.IdentityManipulator;
import zsawyer.mods.mumblelink.api.MumbleLink;
import zsawyer.mods.mumblelink.util.ConfigHelper;
import zsawyer.mods.mumblelink.util.InstanceHelper;
import zsawyer.mods.mumblelink.util.json.JSONArray;
import zsawyer.mods.mumblelink.util.json.JSONException;
import zsawyer.mods.mumblelink.util.json.JSONObject;

import javax.management.InstanceNotFoundException;

/**
 * An addon to the MumbleLink mod (forge version) which injects extended
 * positional audio support (i.e. identity) based on vanilla Minecraft.
 *
 * @author zsawyer, 2013-07-05
 * @version 1.0.0
 */
// FIX for #10 - setting the dependencies fixes NPE since MC 1.8.9
@Mod(modid = ExtendedPASupport.MOD_ID, dependencies = "required-after:" + MumbleLink.MOD_ID, useMetadata = true)
public class ExtendedPASupport implements Activateable, IdentityManipulator {
    public static Logger LOG;

    public static final String MOD_ID = "ExtendedPASupport";

    // The instance of the mod that Forge uses.
    @Instance(ExtendedPASupport.MOD_ID)
    public static ExtendedPASupport instance;

    // whether this mod is active
    private boolean enabled = true;
    // whether debugging mode is on
    private boolean debug = false;

    private String name = "ExtendedPASupport for MumbleLink";
    private String version = "unknown";
    private MumbleLink mumbleLinkInstance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // initialize logger
        LOG = event.getModLog();

        // save guard because this mod should only run on the client
        if (FMLCommonHandler.instance().getSide().isServer())
            throw new RuntimeException(getName()
                    + " should not be installed on a server!");

        loadConfig(event);
    }

    /**
     * load the configuration from the config file
     *
     * @param event the event from which to get the configuration from
     */
    private void loadConfig(FMLPreInitializationEvent event) {
        ConfigHelper configHelper = new ConfigHelper(event);

        // load the debug variable from config file
        debug = configHelper.loadDebug(debug);
        // load the enabled variable from config file
        enabled = configHelper.loadEnabled(enabled);
    }

    @SideOnly(Side.CLIENT)
    @EventHandler
    public void load(FMLInitializationEvent event) {
        try {
            mumbleLinkInstance = InstanceHelper.getMumbleLink();

            if (enabled) {
                activate();
            }
        } catch (InstanceNotFoundException e) {
            FMLClientHandler.instance().haltGame("Error in mod "
                    + getName() + getVersion()
                    + ": no instance of " + MumbleLink.MOD_ID + " found!", e);
        }
    }

    @Override
    public void activate() {
        mumbleLinkInstance.getApi().register(this);
    }

    @Override
    public void deactivate() {
        mumbleLinkInstance.getApi().unregister(this);
    }

    @Override
    public String manipulateIdentity(String identity, Minecraft game,
                                     int maxLength) {

        try {
            // presume the previous identity is a JSON-Object
            JSONObject newIdentity = new JSONObject(identity);
            // inject our information
            appendToIdentity(newIdentity, game);
            // print the (intermediate) result for debugging
            printDebug(newIdentity, "identity");

            return newIdentity.toString();
        } catch (JSONException e) {
            // no JSON... this is not going to work...
            LOG.fatal("could not generate identity", e);
            return identity;
        }
    }

    /**
     * append our information to the given identity
     *
     * @param identity the identity to be supplement
     * @param game     the game instance from which to retrieve the information
     * @throws JSONException see {@link JSONObject#put(String, Object)}
     */
    private void appendToIdentity(JSONObject identity, Minecraft game)
            throws JSONException {
        // build spawn location coordinates (sadly this is the only somewhat
        // identifiable information the client has about the world (and server)
        // it connects to.
        JSONArray spawnCoordinates = new JSONArray();
        spawnCoordinates.put(game.theWorld.getWorldInfo().getSpawnX());
        spawnCoordinates.put(game.theWorld.getWorldInfo().getSpawnY());
        spawnCoordinates.put(game.theWorld.getWorldInfo().getSpawnZ());
        // append coordinates
        identity.put(IdentityKey.WORLD_SPAWN, spawnCoordinates);

        // append the dimension
        identity.put(IdentityKey.DIMENSION, game.thePlayer.dimension);
    }

    /**
     * short-hand method to print a JSON object in the log
     *
     * @param objectToPrint the object to print to the log
     * @param nameOfObject  the name to show (which identifies the object in the context
     *                      of the log)
     */
    private void printDebug(JSONObject objectToPrint, String nameOfObject) {
        if (debug) {
            ExtendedPASupport.LOG.info(nameOfObject + ": "
                    + objectToPrint.toString(), "");
        }
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
