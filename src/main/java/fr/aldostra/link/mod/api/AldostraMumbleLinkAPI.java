/*
 mod_MumbleLink - Positional Audio Communication for Minecraft with Mumble
 Adapted by Aldostra Team (http://www.aldostra.fr/)
 Copyright 2011-2013 zsawyer (http://sourceforge.net/users/zsawyer)

 This file is part of mod_MumbleLink
 (http://sourceforge.net/projects/modmumblelink/)
 Adapted on this fork : https://github.com/alexandrelefourner/MumbleLink.

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

package fr.aldostra.link.mod.api;

/**
 * The MumbleLinkAPI is an access point for addons to interface with the
 * forge-based {@link AldostraMumbleLink} mod.<br>
 * <br>
 * Get an instance of the API with: <br>
 * {@link AldostraMumbleLink#getApi()}
 * <p>
 * Use this to (un)subscribe/(un)register your {@link ContextManipulator} and/or {@link IdentityManipulator}.
 *
 * @author zsawyer
 */
public interface AldostraMumbleLinkAPI {

    /**
     * register an identity manipulator for injecting a custom positional audio
     * (PA) identity
     *
     * @param manipulator subscriber to be registered
     */
    public void register(IdentityManipulator manipulator);

    /**
     * unsubscribe the listener
     *
     * @param manipulator previously registered subscriber to unsubscribe
     */
    public void unregister(IdentityManipulator manipulator);

    /**
     * register a context manipulator for injecting a custom positional audio
     * (PA) context
     *
     * @param manipulator subscriber to be registered
     */
    public void register(ContextManipulator manipulator);

    /**
     * unsubscribe the listener
     *
     * @param manipulator previously registered subscriber to unsubscribe
     */
    public void unregister(ContextManipulator manipulator);
}
