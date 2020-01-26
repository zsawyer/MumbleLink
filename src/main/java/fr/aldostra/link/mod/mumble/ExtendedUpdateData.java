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

package fr.aldostra.link.mod.mumble;

import fr.aldostra.link.mod.AldostraMumbleMumbleMumbleLinkImpl;
import fr.aldostra.link.mod.api.ContextManipulator;
import fr.aldostra.link.mod.api.IdentityManipulator;
import fr.aldostra.link.mod.error.NativeUpdateErrorHandler;
import fr.aldostra.link.mumble.jna.LinkAPILibrary;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;

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
            String newContext = contextManipulator.manipulateContext(context,
                    game, maxLength);
            if (verify("context", newContext, maxLength)) {
                context = newContext;
            }
        }

        if (AldostraMumbleMumbleMumbleLinkImpl.debug()) {
            AldostraMumbleMumbleMumbleLinkImpl.LOG.info("context: " + context);
        }

        return context;
    }

    @Override
    protected String generateIdentity(Minecraft game, int maxLength) {
        String identity = super.generateIdentity(game, maxLength);

        for (IdentityManipulator identityManipulator : identityManipulators) {
            String newIdentity = identityManipulator.manipulateIdentity(
                    identity, game, maxLength);
            if (verify("identity", newIdentity, maxLength)) {
                identity = newIdentity;
            }
        }

        if (AldostraMumbleMumbleMumbleLinkImpl.debug()) {
            AldostraMumbleMumbleMumbleLinkImpl.LOG.info("identity: " + identity);
        }

        return identity;
    }

    private boolean verify(String type, String value, int maxLength) {
        if (value.length() > maxLength) {
            AldostraMumbleMumbleMumbleLinkImpl.LOG
                    .fatal(type + " (" + value.length()
                            + ") is too long (max. " + maxLength + "): '"
                            + value + "'");
            return false;
        }
        return true;
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
