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
package zsawyer.mods.mumblelink.mumble.bridj;

import org.bridj.Pointer;
import org.bridj.ValuedEnum;
import zsawyer.mods.mumblelink.error.NativeErrorHandler;
import zsawyer.mumble.bridj.LinkAPILibrary;

/**
 * @author zsawyer
 */
public class MumbleInitializer implements Runnable {

    private final static Object semaphore = new Object();

    public static final int ONE_SECOND = 1000;
    public static final String PLUGIN_NAME = "Minecraft";
    public static final String PLUGIN_DESCRIPTION = "Link plugin for Minecraft with Forge";
    public static final int PLUGIN_UI_VERSION = 2;
    private NativeErrorHandler errorHandler;
    private ValuedEnum<LinkAPILibrary.LINKAPI_ERROR_CODE> initializationReturnCode = null;

    public MumbleInitializer(NativeErrorHandler errorHandler) {
        super();
        this.errorHandler = errorHandler;
    }

    @Override
    public void run() {
        while (!isMumbleInitialized()) {
            if (Thread.interrupted()) {
                return;
            }
            synchronized (semaphore) {
                try {
                    Pointer<Character> pluginName = Pointer.allocateChars(LinkAPILibrary.LINKAPI_MAX_NAME_LENGTH);
                    pluginName.setWideCString(PLUGIN_NAME);
                    Pointer<Character> pluginDescription = Pointer.allocateChars(LinkAPILibrary.LINKAPI_MAX_DESCRIPTION_LENGTH);
                    pluginName.setWideCString(PLUGIN_DESCRIPTION);

                    initializationReturnCode = LinkAPILibrary.initialize(pluginName, pluginDescription, PLUGIN_UI_VERSION);


                    errorHandler.handleInitError(initializationReturnCode);

                    try {
                        Thread.sleep(ONE_SECOND);
                    } catch (InterruptedException e) {
                        return;
                    }
                } catch (LinkageError e) {
                    errorHandler.handleLinkageError(e);
                }
            }
        }
    }

    public boolean isMumbleInitialized() {
        if (initializationReturnCode == null) {
            return false;
        }
        return initializationReturnCode.value() == LinkAPILibrary.LINKAPI_ERROR_CODE.LINKAPI_ERROR_CODE_NO_ERROR.value();
    }
}
