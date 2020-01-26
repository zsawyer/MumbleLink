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

import javax.annotation.Nonnull;

/**
 * Interface for the MumbleLink mod.
 * This interface provides implementation independent access to the MumbleLink mod.
 * <p>
 * Use {@link fr.aldostra.link.mod.util.InstanceHelper#getMumbleLink()} to retrieve the mod instance.
 */
public interface AldostraMumbleLink extends Activateable, Debuggable {
    public final static @Nonnull String MOD_ID = "aldostramumblelink";
    public final static @Nonnull String MOD_NAME = "AldostraMumbleLink for Forge";
    public final static @Nonnull String VERSION = "1.15.2-1.0.0";
    public final static @Nonnull String MOD_DEPENDENCIES = "";

    /**
     * the API instance which is used by this mod instance
     * registering at this api effectively registers your manipulators with the core MumbleLink mod
     *
     * @return mod's API instance
     */
    public AldostraMumbleLinkAPI getApi();

    /**
     * display name of the mod
     *
     * @return mod's name
     */
    public String getName();

    /**
     * version of the mod
     *
     * @return mod's version
     */
    public String getVersion();
}
