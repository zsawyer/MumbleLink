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

import java.util.logging.Logger;

import zsawyer.mods.mumblelink.error.ErrorHandlerImpl;
import zsawyer.mods.mumblelink.error.ModErrorHandler.ModError;
import zsawyer.mods.mumblelink.loader.PackageLibraryLoader;
import zsawyer.mods.mumblelink.mumble.MumbleInitializer;
import zsawyer.mods.mumblelink.mumble.UpdateData;
import zsawyer.mumble.jna.LinkAPILibrary;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
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
@Mod(modid = MumbleLinkConstants.MOD_ID, name = MumbleLinkConstants.MOD_NAME, version = MumbleLinkConstants.MOD_VERSION)
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
@SideOnly(Side.CLIENT)
public class MumbleLink extends MumbleLinkBase {
	public static Logger LOG;

	// The instance of the mod that Forge uses.
	@Instance(MumbleLinkConstants.MOD_ID)
	public static MumbleLink instance;
	//
	private UpdateTicker updateTicker;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		LOG = event.getModLog();

		if (FMLCommonHandler.instance().getSide().isServer())
			throw new RuntimeException(
					"MumbleLink should not be installed on a server!");
	}

	@SideOnly(Side.CLIENT)
	@EventHandler
	public void load(FMLInitializationEvent event) {
		load();
		initComponents();
		updateTicker.setEnabled(true);
	}

	private void initComponents() {
		updateTicker = new UpdateTicker();
		updateTicker.engage();

	}

	@SideOnly(Side.CLIENT)
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}

}
