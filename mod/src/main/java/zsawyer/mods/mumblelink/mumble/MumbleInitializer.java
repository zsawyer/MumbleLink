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
package zsawyer.mods.mumblelink.mumble;

import net.minecraft.client.Minecraft;
import zsawyer.mods.mumblelink.error.NativeInitErrorHandler;
import zsawyer.mods.mumblelink.error.NativeInitErrorHandler.NativeInitError;
import zsawyer.mods.mumblelink.mumble.jna.LinkAPIHelper;
import zsawyer.mumble.jna.LinkAPILibrary;

import java.util.function.Consumer;

/**
 * @author zsawyer
 */
public class MumbleInitializer implements Runnable {

    public static final int ONE_SECOND = 1000;
    private LinkAPIHelper link;
    private NativeInitErrorHandler errorHandler;
    private Consumer<Minecraft> gameSetter;
    private NativeInitError initilizationReturnCode = NativeInitError.NOT_YET_INITIALIZED;

    public static final String PLUGIN_NAME = "Minecraft";
    public static final String PLUGIN_DESCRIPTION = "Minecraft (1.16.5)";
    public static final int PLUGIN_UI_VERSION = 2;

    public MumbleInitializer(LinkAPILibrary link, NativeInitErrorHandler errorHandler, Consumer<Minecraft> gameSetter) {
        super();
        this.link = new LinkAPIHelper(link);
        this.errorHandler = errorHandler;
        this.gameSetter = gameSetter;
    }

    @Override
    public void run() {
        while (!isMumbleInitialized()) {
            if (Thread.interrupted()) {
                return;
            }

            synchronized (gameSetter) {
                try {
                    gameSetter.accept(Minecraft.getInstance());
                } catch (Exception e) {
                    // nothing to do here... we'll just wait a bit and retry when we can  actually get the instance properly
                    try {
                        Thread.sleep(ONE_SECOND);
                        break;
                    } catch (InterruptedException ie) {
                        return;
                    }
                }
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
        return initilizationReturnCode == NativeInitError.NO_ERROR;
    }
}
