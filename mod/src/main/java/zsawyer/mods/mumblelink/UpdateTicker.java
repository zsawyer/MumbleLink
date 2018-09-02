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

import net.minecraft.client.Minecraft;
import org.dimdev.rift.listener.client.ClientTickable;
import zsawyer.mods.mumblelink.api.Activateable;

/**
 * @author zsawyer
 */
public class UpdateTicker implements Activateable, ClientTickable {

    private boolean enabled = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void activate() {
        enabled = true;
    }

    @Override
    public void deactivate() {
        enabled = false;
    }

    @Override
    public void clientTick() {
        if (enabled) {
            MumbleLinkImpl.instance.tryUpdateMumble(Minecraft.getMinecraft());
        }
    }
}
