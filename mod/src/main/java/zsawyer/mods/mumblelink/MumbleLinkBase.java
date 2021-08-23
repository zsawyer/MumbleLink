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
import zsawyer.mods.mumblelink.error.ErrorHandlerImpl;
import zsawyer.mods.mumblelink.error.ModErrorHandler.ModError;
import zsawyer.mods.mumblelink.loader.PackageLibraryLoader;
import zsawyer.mods.mumblelink.mumble.MumbleInitializer;
import zsawyer.mods.mumblelink.mumble.UpdateData;
import zsawyer.mumble.jna.LinkAPILibrary;

import java.util.function.Consumer;

/**
 * <p>
 * mod to link with mumble for positional audio
 * </p><p>
 * http://mumble.sourceforge.net/
 * </p><p>
 * when developing for it I suggest using "mumblePAHelper" to see
 * coordinates
 * </p>
 * @author zsawyer, 2013-04-09
 */
public class MumbleLinkBase implements Consumer<Minecraft> {
    protected static Minecraft game;

    protected MumbleInitializer mumbleInitializer;
    protected Thread mumbleInitializerThread;
    protected UpdateData mumbleData;
    protected LinkAPILibrary library;
    protected ErrorHandlerImpl errorHandler;


    public MumbleLinkBase() {
        super();
    }

    public void load() {
        initComponents();
    }

    private void initComponents() {
        errorHandler = ErrorHandlerImpl.getInstance();

        try {
            library = new PackageLibraryLoader()
                    .loadLibrary(MumbleLinkConstants.LIBRARY_NAME);
        } catch (Exception e) {
            errorHandler.throwError(ModError.LIBRARY_LOAD_FAILED, e);
        }

        mumbleData = new UpdateData(library, errorHandler);

        mumbleInitializer = new MumbleInitializer(library, errorHandler, this);
        mumbleInitializerThread = new Thread(mumbleInitializer);
    }

    public void tryUpdateMumble() {
        if (null != game && mumbleInitializer.isMumbleInitialized()) {
            if (game.player != null && game.level != null) {
                mumbleData.set(game);
                mumbleData.send();
            }
        } else {
            try {
                mumbleInitializerThread.start();
            } catch (IllegalThreadStateException ex) {
                // thread was already started so we do nothing
            }
        }
    }

    public static Minecraft getGame() {
        return MumbleLinkBase.game;
    }

    /**
     * Performs this operation on the given argument.
     *
     * @param minecraft the input argument
     */
    @Override
    public void accept(Minecraft minecraft) {
        game = minecraft;
        ErrorHandlerImpl.getInstance().init(game);
    }
}
