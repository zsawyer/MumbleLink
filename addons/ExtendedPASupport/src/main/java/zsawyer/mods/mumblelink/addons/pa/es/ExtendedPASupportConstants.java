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

public class ExtendedPASupportConstants {
	public static final String MOD_ID = "ExtendedPASupport";
	public static final String MOD_NAME = "ExtendedPASupport for MumbleLink";
	public static final String MOD_VERSION = "0.0.3";

	public static class IdentityKey {
		public static final String NAME = "name";
		public static final String DIMENSION = "dimension";
		public static final String WORLD_SPAWN = "worldSpawn";

		private IdentityKey() {
		}
	}

	public static class ContextKey {
		public static final String DOMAIN = "domain";

		private ContextKey() {
		};
	}

	private ExtendedPASupportConstants() {
	}
}
