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

import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.client.Minecraft;
import zsawyer.mods.Activateable;
import zsawyer.mods.mumblelink.MumbleLink;
import zsawyer.mods.mumblelink.MumbleLinkConstants;
import zsawyer.mods.mumblelink.addons.pa.es.ExtendedPASupportConstants.ContextKey;
import zsawyer.mods.mumblelink.addons.pa.es.ExtendedPASupportConstants.IdentityKey;
import zsawyer.mods.mumblelink.json.JSONArray;
import zsawyer.mods.mumblelink.json.JSONException;
import zsawyer.mods.mumblelink.json.JSONObject;
import zsawyer.mods.mumblelink.mumble.ContextManipulator;
import zsawyer.mods.mumblelink.mumble.IdentityManipulator;
import zsawyer.mods.mumblelink.util.ConfigHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * An addon to the MumbleLink mod (forge version) which injects extended
 * positional audio support (i.e. context and identity) based on vanilla
 * minecraft.
 * 
 * @author zsawyer, 2013-07-05
 */
@Mod(modid = ExtendedPASupportConstants.MOD_ID,
		name = ExtendedPASupportConstants.MOD_NAME,
		version = ExtendedPASupportConstants.MOD_VERSION,
		dependencies = "required-after:" + MumbleLinkConstants.MOD_ID)
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
@SideOnly(Side.CLIENT)
public class ExtendedPASupport implements Activateable, IdentityManipulator,
		ContextManipulator {
	public static Logger LOG;

	// The instance of the mod that Forge uses.
	@Instance(ExtendedPASupportConstants.MOD_ID)
	public static ExtendedPASupport instance;

	private boolean enabled = true;
	private boolean debug = false;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		LOG = event.getModLog();

		if (FMLCommonHandler.instance().getSide().isServer())
			throw new RuntimeException(ExtendedPASupportConstants.MOD_NAME
					+ " should not be installed on a server!");

		loadConfig(event);

		if (!debug) {
			LOG.setLevel(Level.SEVERE);
		}
	}

	private void loadConfig(FMLPreInitializationEvent event) {
		ConfigHelper configHelper = new ConfigHelper(event);

		debug = configHelper.loadDebug(debug);
		enabled = configHelper.loadEnabled(enabled);
	}

	@SideOnly(Side.CLIENT)
	@EventHandler
	public void load(FMLInitializationEvent event) {
		if (enabled) {
			activate();
		}
	}

	@SideOnly(Side.CLIENT)
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}

	@Override
	public void activate() {
		MumbleLink.instance.getApi().register((IdentityManipulator) this);
		MumbleLink.instance.getApi().register((ContextManipulator) this);
	}

	@Override
	public void deactivate() {
		MumbleLink.instance.getApi().unregister((IdentityManipulator) this);
		MumbleLink.instance.getApi().unregister((ContextManipulator) this);
	}

	@Override
	public String manipulateContext(String context, Minecraft game,
			int maxLength) {

		JSONObject newContext = new JSONObject();

		try {
			
			newContext.put(ContextKey.DOMAIN, MumbleLinkConstants.MUMBLE_CONTEXT);
			
		} catch (JSONException e) {
			LOG.log(Level.SEVERE, "could not generate identity", e);
			return context;
		}

		if (debug) {
			ExtendedPASupport.LOG.log(Level.INFO,
					"contexts: " + context.toString());
		}

		return newContext.toString();
	}

	@Override
	public String manipulateIdentity(String identity, Minecraft game,
			int maxLength) {

		JSONObject newIdentity = new JSONObject();

		try {
			newIdentity.put(IdentityKey.NAME, game.thePlayer.getEntityName());

			JSONArray spawnCoordinates = new JSONArray();
			spawnCoordinates.put(game.theWorld.getWorldInfo().getSpawnX());
			spawnCoordinates.put(game.theWorld.getWorldInfo().getSpawnY());
			spawnCoordinates.put(game.theWorld.getWorldInfo().getSpawnZ());
			newIdentity.put(IdentityKey.WORLD_SPAWN, spawnCoordinates);

			newIdentity.put(IdentityKey.DIMENSION, game.thePlayer.dimension);

		} catch (JSONException e) {
			LOG.log(Level.SEVERE, "could not generate identity", e);
			return identity;
		}

		if (debug) {
			ExtendedPASupport.LOG.log(Level.INFO,
					"identity: " + newIdentity.toString());
		}

		return newIdentity.toString();
	}
}
