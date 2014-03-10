/*
 mod_MumbleLink - Positional Audio Communication for Minecraft with Mumble
 Copyright 2012 zsawyer (http://sourceforge.net/users/zsawyer)

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
package zsawyer.mods.mumblelink.error;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.src.ModLoader;
import zsawyer.mods.mumblelink.MumbleLinkConstants;
import zsawyer.mods.mumblelink.notification.BufferedChatNotifier;
import zsawyer.mods.mumblelink.notification.UserNotifier;
import zsawyer.mods.mumblelink.util.SingletonFactory;


/**
 *
 * @author zsawyer
 */
public class ErrorHandlerImpl implements ModErrorHandler, NativeInitErrorHandler, NativeUpdateErrorHandler {

    private UserNotifier chat;
    private static final Logger logger = ModLoader.getLogger();

    public ErrorHandlerImpl() {
        chat = new BufferedChatNotifier(ModLoader.getMinecraftInstance());
    }

    @Override
    public void throwError(ModError modError, Throwable cause) {
        modloaderLog(Level.SEVERE, cause.getMessage(), cause);
        haltMinecraftUsingAnException(modError.toString(), cause);
    }

    private void haltMinecraftUsingAnException(String message, Throwable err) {
        ModLoader.throwException("Error in mod "
                + MumbleLinkConstants.MOD_NAME + MumbleLinkConstants.MOD_VERSION
                + ": " + message,
                err);
    }

    @Override
    public void handleError(ModError err, Throwable stack) {
        chatMessage("[MumbleLink] Error: " + err.toString());

        modloaderLog(Level.WARNING, err.toString(), stack);
    }

    private void modloaderLog(Level severity, String message, Throwable stack) {
        logger.log(severity,
                "[" + MumbleLinkConstants.MOD_NAME + MumbleLinkConstants.MOD_VERSION + "]"
                + "[" + severity.getLocalizedName() + "] "
                + message,
                stack);
    }

    private void chatMessage(String message) {
        chat.print(message);
    }

    @Override
    public void handleError(NativeUpdateError fromCode) {
        if (fromCode != NativeUpdateError.NO_ERROR) {
            modloaderLog(Level.WARNING, "Update failed! Error: " + fromCode.getCode() + " (" + fromCode.toString() + ")", null);
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
        }  catch (Exception ex) {
            // nothing we can do
            throw new RuntimeException(ex);
        }
    }

}
