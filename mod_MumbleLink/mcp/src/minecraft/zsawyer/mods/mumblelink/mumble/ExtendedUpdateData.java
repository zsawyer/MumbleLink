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

package zsawyer.mods.mumblelink.mumble;

import java.util.ArrayList;
import java.util.logging.Level;

import net.minecraft.client.Minecraft;
import zsawyer.mods.mumblelink.MumbleLink;
import zsawyer.mods.mumblelink.error.NativeUpdateErrorHandler;
import zsawyer.mumble.jna.LinkAPILibrary;

public class ExtendedUpdateData extends UpdateData {

	protected ArrayList<IdentityManipulator> identityManipulators;

	protected ArrayList<ContextManipulator> contextManipulators;

	public ExtendedUpdateData(LinkAPILibrary mumbleLink,
			NativeUpdateErrorHandler errorHandler) {
		super(mumbleLink, errorHandler);
		identityManipulators = new ArrayList<IdentityManipulator>();
		contextManipulators = new ArrayList<ContextManipulator>();
	}

	@Override
	protected String generateContext(Minecraft game, int maxLength) {
		String context = super.generateContext(game, maxLength);

		for (ContextManipulator contextManipulator : contextManipulators) {
			context = contextManipulator.manipulateContext(context, game,
					maxLength);
		}

		if(MumbleLink.debug()){
			MumbleLink.LOG.log(Level.INFO, "context: " + context);
		}
		
		return context;		
	}

	@Override
	protected String generateIdentity(Minecraft game, int maxLength) {
		String identity = super.generateIdentity(game, maxLength);

		for (IdentityManipulator identityManipulator : identityManipulators) {
			identity = identityManipulator.manipulateIdentity(identity, game,
					maxLength);
		}

		if(MumbleLink.debug()){
			MumbleLink.LOG.log(Level.INFO, "identity: " + identity);
		}		
		
		return identity;
	}

	public void register(IdentityManipulator manipulator) {
		synchronized (identityManipulators) {
			identityManipulators.add(manipulator);
		}
	}

	public void unregister(IdentityManipulator manipulator) {
		synchronized (identityManipulators) {
			identityManipulators.remove(manipulator);
		}
	}

	public void register(ContextManipulator manipulator) {
		synchronized (contextManipulators) {
			contextManipulators.add(manipulator);
		}
	}

	public void unregister(ContextManipulator manipulator) {
		synchronized (contextManipulators) {
			contextManipulators.remove(manipulator);
		}
	}

}
