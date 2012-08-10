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

import java.util.Observable;
import net.minecraft.src.mumblelink.NativeInitErrorHandler.NativeInitError;
import static net.minecraft.src.mumblelink.NativeInitErrorHandler.NativeInitError.NOT_YET_INITIALIZED;
import static net.minecraft.src.mumblelink.NativeInitErrorHandler.NativeInitError.NO_ERROR;

/**
 *
 * @author zsawyer
 */
public class MumbleInitializer implements Runnable {

    private MumbleLink link;
    private NativeInitErrorHandler errorHandler;
    private NativeInitError initilizationReturnCode = NOT_YET_INITIALIZED;

    public MumbleInitializer(MumbleLink link, NativeInitErrorHandler errorHandler) {
        super();
        this.link = link;
        this.errorHandler = errorHandler;
    }

    @Override
    public void run() {
        while (!isMumbleInitialized()) {
            if (Thread.interrupted()) {
                return;
            }
            synchronized (link) {
                initilizationReturnCode = link.callInitMumble();

                errorHandler.handleError(initilizationReturnCode);
            }
        }
    }

    public boolean isMumbleInitialized() {
        return initilizationReturnCode == NO_ERROR;
    }
}
