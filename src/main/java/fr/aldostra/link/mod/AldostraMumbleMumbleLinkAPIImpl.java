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

package fr.aldostra.link.mod;

import fr.aldostra.link.mod.api.AldostraMumbleLinkAPI;
import fr.aldostra.link.mod.api.ContextManipulator;
import fr.aldostra.link.mod.api.IdentityManipulator;
import fr.aldostra.link.mod.mumble.ExtendedUpdateData;

public class AldostraMumbleMumbleLinkAPIImpl implements AldostraMumbleLinkAPI {

    private ExtendedUpdateData extendedUpdateData;

    public void setExtendedUpdateData(ExtendedUpdateData extendedUpdateData) {
        this.extendedUpdateData = extendedUpdateData;
    }

    @Override
    public void register(IdentityManipulator manipulator) {
        extendedUpdateData.register(manipulator);
    }

    @Override
    public void unregister(IdentityManipulator manipulator) {
        extendedUpdateData.unregister(manipulator);
    }

    @Override
    public void register(ContextManipulator manipulator) {
        extendedUpdateData.register(manipulator);
    }

    @Override
    public void unregister(ContextManipulator manipulator) {
        extendedUpdateData.unregister(manipulator);
    }
}
