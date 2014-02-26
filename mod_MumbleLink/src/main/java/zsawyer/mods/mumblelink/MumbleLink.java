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

import java.util.logging.Level;
import java.util.logging.Logger;

import zsawyer.mods.Activateable;
import zsawyer.mods.Debuggable;
import zsawyer.mods.mumblelink.api.MumbleLinkAPI;
import zsawyer.mods.mumblelink.api.MumbleLinkAPIInstance;
import zsawyer.mods.mumblelink.mumble.ExtendedUpdateData;
import zsawyer.mods.mumblelink.util.ConfigHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * mod to link with mumble for positional audio
 * 
 * this is a forge based implementation
 * 
 * @author zsawyer, 2013-04-09
 */
@Mod(modid = MumbleLinkConstants.MOD_ID, name = MumbleLinkConstants.MOD_NAME,
		version = MumbleLinkConstants.MOD_VERSION)
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
@SideOnly(Side.CLIENT)
public class MumbleLink extends MumbleLinkBase implements Activateable,
		Debuggable {
	public static Logger LOG;

	// The instance of the mod that Forge uses.
	@Instance(MumbleLinkConstants.MOD_ID)
	public static MumbleLink instance;
	//
	private UpdateTicker updateTicker;
	private MumbleLinkAPIInstance api;

	private boolean enabled = true;
	private boolean debug = false;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		LOG = event.getModLog();

		if (FMLCommonHandler.instance().getSide().isServer())
			throw new RuntimeException(MumbleLinkConstants.MOD_NAME
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
		load();
		initComponents();
		activate();
	}

	private void initComponents() {
		ExtendedUpdateData extendedUpdateData = new ExtendedUpdateData(library,
				errorHandler);
		mumbleData = extendedUpdateData;
		updateTicker = new UpdateTicker();
		api = new MumbleLinkAPIInstance();
		api.setExtendedUpdateData(extendedUpdateData);
	}

	public MumbleLinkAPI getApi() {
		return api;
	}

	@Override
	public void activate() {
		updateTicker.activate();
	}

	@Override
	public void deactivate() {
		updateTicker.deactivate();
	}

	@Override
	public boolean debugging() {
		return debug;
	}

	public static boolean debug() {
		return instance.debug;
	}

}
