/*
 mod_MumbleLink - Positional Audio Communication for Minecraft with Mumble
 Copyright 2012 zsawyer (http://sourceforge.net/users/zsawyer)

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
package fr.aldostra.link.mod.error;

import fr.aldostra.link.mod.AldostraMumbleMumbleMumbleLinkImpl;
import fr.aldostra.link.mod.notification.BufferedChatNotifier;
import fr.aldostra.link.mod.notification.UserNotifier;
import fr.aldostra.link.mod.util.SingletonFactory;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Level;

/**
 * @author zsawyer
 */
public class ErrorHandlerImpl implements ModErrorHandler, NativeInitErrorHandler, NativeUpdateErrorHandler {

    private UserNotifier chat;

    public ErrorHandlerImpl() {
        chat = new BufferedChatNotifier(Minecraft.getInstance());
    }

    @Override
    public void throwError(ModError modError, Throwable cause) {
        log(Level.FATAL, cause.getMessage(), cause);
        haltMinecraftUsingAnException(modError.toString(), cause);
    }

    private void haltMinecraftUsingAnException(String message, Throwable err) {
        throw new GenericError("Error in mod "
                + AldostraMumbleMumbleMumbleLinkImpl.instance.getName() + AldostraMumbleMumbleMumbleLinkImpl.instance.getVersion()
                + ": " + message,
                err);
    }

    @Override
    public void handleError(ModError err, Throwable stack) {
        chatMessage("[MumbleLink] Error: " + err.toString());
        log(Level.WARN, err.toString(), stack);
    }

    private void log(Level severity, String message, Throwable stack) {
        AldostraMumbleMumbleMumbleLinkImpl.LOG.log(severity,
                "[" + AldostraMumbleMumbleMumbleLinkImpl.instance.getName() + AldostraMumbleMumbleMumbleLinkImpl.instance.getVersion() + "]"
                        + "[" + severity.toString() + "] "
                        + message,
                stack);
    }

    private void chatMessage(String message) {
        chat.print(message);
    }

    @Override
    public void handleError(NativeUpdateError fromCode) {
        if (fromCode != NativeUpdateError.NO_ERROR) {
            log(Level.WARN, "Update failed! Error: " + fromCode.getCode() + " (" + fromCode.toString() + ")", null);
        }
    }

    @Override
    public void handleError(NativeInitError fromCode) {
        if (fromCode == NativeInitError.NO_ERROR) {
            chat.print(UserNotifier.LINK_SUCCESS_MESSAGE);
        }
    }

    public static ErrorHandlerImpl getInstance() {
        try {
            return SingletonFactory.getInstance(ErrorHandlerImpl.class);
        } catch (Exception ex) {
            // nothing we can do
            throw new RuntimeException(ex);
        }
    }

}
