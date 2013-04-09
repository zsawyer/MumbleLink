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
package zsawyer.mods.mumblelink;

import net.minecraft.client.Minecraft;
import net.minecraft.src.BaseMod;
import net.minecraft.src.ModLoader;
import zsawyer.mods.mumblelink.error.ErrorHandlerImpl;

/**
 * mod to link with mumble for positional audio
 * 
 * @see http://mumble.sourceforge.net/
 * 
 *      when developing for it I suggest using "mumblePAHelper" to see
 *      coordinates
 * 
 *      for Minecraft v1.5.1 updated 2012-04-05
 * 
 * @author zsawyer, 2011-03-20
 */
public class mod_MumbleLink extends BaseMod {

	public static mod_MumbleLink instance;

	private ErrorHandlerImpl errorHandler;
	private MumbleLink actualMod;

	public mod_MumbleLink() {
		super();

		if (instance != null) {
			throw new RuntimeException(mod_MumbleLink.class.getSimpleName()
					+ " was already loaded.");
		}
		instance = this;

		errorHandler = ErrorHandlerImpl.getInstance();

		actualMod = new MumbleLink();
	}

	@Override
	public void load() {
		boolean isForge = true;
		try {
			ModLoader.class.getDeclaredField(MumbleLinkConstants.FML_MARKER);
		} catch (NoSuchFieldException e) {
			isForge = false;
		}
		
		if (!isForge) {
			registerWithModLoader();
			actualMod.load();
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
}
