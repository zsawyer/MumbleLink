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
package net.minecraft.src.mumblelink;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.src.ModLoader;
import net.minecraft.src.mod_MumbleLink;

/**
 *
 * @author zsawyer
 */
public class ErrorHandlerImpl extends Singleton implements ModErrorHandler, NativeInitErrorHandler, NativeUpdateErrorHandler {

    private UserNotifier chat;
    private static final Logger logger = ModLoader.getLogger();

    public ErrorHandlerImpl() {
        chat = new ChatNotifier();
    }

    @Override
    public void throwError(ModError modError, Throwable err) {
        modloaderLog(Level.SEVERE, err.getMessage(), err);
        haltMinecraftUsingAnException(modError.toString(), err);
    }

    private void haltMinecraftUsingAnException(String message, Throwable err) {
        ModLoader.throwException("Error in mod "
                + mod_MumbleLink.modName + mod_MumbleLink.modVersion
                + ": " + message,
                err);
    }

    @Override
    public void handleError(ModError err, Throwable stack) {
        chatMessage(err.toString());

        modloaderLog(Level.WARNING, err.toString(), stack);
    }

    private void modloaderLog(Level severity, String message, Throwable stack) {
        logger.log(severity,
                "[" + mod_MumbleLink.modName + mod_MumbleLink.modVersion + "]"
                + "[" + severity.getLocalizedName() + "]"
                + message,
                stack);
    }

    private void chatMessage(String message) {
        chat.print(message);
    }

    @Override
    public void handleError(NativeUpdateError fromCode) {
        if(fromCode != NativeUpdateError.NO_ERROR) {
            modloaderLog(Level.WARNING, "Update failed! Error: " + fromCode.getCode() + " (" + fromCode.toString() + ")", null);
        }
    }

    @Override
    public void handleError(NativeInitError fromCode) {
        if(fromCode == NativeInitError.NO_ERROR) {
            chat.print(UserNotifier.LINK_SUCCESS_MESSAGE);
        }
    }
}
