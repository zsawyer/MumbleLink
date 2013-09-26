/*
 mod_MumbleLink - Positional Audio Communication for Minecraft with Mumble
 Copyright 2011 zsawyer (http://sourceforge.net/users/zsawyer)

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
package net.minecraft.src;

import net.minecraft.client.Minecraft;
import zsawyer.mods.mumblelink.MumbleLink;
import zsawyer.mods.mumblelink.MumbleLinkBase;
import zsawyer.mods.mumblelink.MumbleLinkConstants;
import zsawyer.mods.mumblelink.error.ErrorHandlerImpl;

/**
 * mod to link with mumble for positional audio
 * 
 * this is an implementation intended for Risugami's ModLoader but it also
 * handles if Forge is used
 * 
 * the main reason this class is still present is to support RML and FML
 * side-by-side
 * 
 * @see http://mumble.sourceforge.net/
 * 
 *      when developing for it I suggest using "mumblePAHelper" to see
 *      coordinates
 * 
 * @author zsawyer, 2011-03-20
 */
public class mod_MumbleLink extends BaseMod {

	public static mod_MumbleLink instance;

	private MumbleLinkBase actualMod;

	private Boolean isForge = null;

	public mod_MumbleLink() {
		super();

		if (instance != null) {
			throw new RuntimeException(mod_MumbleLink.class.getSimpleName()
					+ " was already loaded.");
		}

		instance = this;
	}

	@Override
	public void load() {
		/*
		 * only do something if FML is not used, else the actual FML
		 * implementation will take over so this one will just be loaded but do
		 * nothing
		 */
		if (!isForge()) {
			actualMod = new MumbleLinkBase();
			registerWithModLoader();
			ClassLoader backupLoader = Thread.currentThread()
					.getContextClassLoader();
			Thread.currentThread().setContextClassLoader(
					Minecraft.class.getClassLoader());
			actualMod.load();
			Thread.currentThread().setContextClassLoader(backupLoader);
		} else {
			actualMod = new MumbleLink();
		}
	}

	private void registerWithModLoader() {
		ModLoader.setInGameHook(this, true, false);
	}

	@Override
	public boolean onTickInGame(float tick, Minecraft game) {
		super.onTickInGame(tick, game);

		actualMod.tryUpdateMumble(game);

		return true;
	}

	@Override
	public String getVersion() {
		return MumbleLinkConstants.MOD_VERSION;
	}

	private boolean isForge() {
		if (isForge == null) {
			isForge = true;
			try {
				ModLoader.class
						.getDeclaredField(MumbleLinkConstants.FML_MARKER);
			} catch (NoSuchFieldException e) {
				isForge = false;
			}
		}
		return isForge;
	}
}
