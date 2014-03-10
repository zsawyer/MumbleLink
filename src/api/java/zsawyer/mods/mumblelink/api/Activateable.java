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

package zsawyer.mods.mumblelink.api;

/**
 * A class implementing this interface can be told to suspend its activities.
 * The class will not unload nor will it guarantee to stop monitoring if it is
 * subscribed anywhere.
 *
 * @author zsawyer
 */
public interface Activateable {

    /**
     * enable all major activities both initially and subsequently to
     * intermissions
     */
    public abstract void activate();

    /**
     * suspend all major activities
     */
    public abstract void deactivate();
}
