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
package fr.aldostra.link.mod.mumble;

import fr.aldostra.link.mod.error.NativeInitErrorHandler;
import fr.aldostra.link.mod.mumble.jna.LinkAPIHelper;
import fr.aldostra.link.mumble.jna.LinkAPILibrary;

/**
 * @author zsawyer
 */
public class MumbleInitializer implements Runnable {

    public static final int ONE_SECOND = 1000;
    private LinkAPIHelper link;
    private NativeInitErrorHandler errorHandler;
    private NativeInitErrorHandler.NativeInitError initilizationReturnCode = NativeInitErrorHandler.NativeInitError.NOT_YET_INITIALIZED;

    public static final String PLUGIN_NAME = "Minecraft";
    public static final String PLUGIN_DESCRIPTION = "Link plugin for Minecraft with ModLoader";
    public static final int PLUGIN_UI_VERSION = 2;

    public MumbleInitializer(LinkAPILibrary link, NativeInitErrorHandler errorHandler) {
        super();
        this.link = new LinkAPIHelper(link);
        this.errorHandler = errorHandler;
    }

    @Override
    public void run() {
        while (!isMumbleInitialized()) {
            if (Thread.interrupted()) {
                return;
            }
            synchronized (link) {
                initilizationReturnCode = link.initialize(PLUGIN_NAME, PLUGIN_DESCRIPTION, PLUGIN_UI_VERSION);

                errorHandler.handleError(initilizationReturnCode);

                try {
                    Thread.sleep(ONE_SECOND);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

    public boolean isMumbleInitialized() {
        return initilizationReturnCode == NativeInitErrorHandler.NativeInitError.NO_ERROR;
    }
}
