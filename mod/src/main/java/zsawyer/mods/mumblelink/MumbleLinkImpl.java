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

import org.apache.logging.log4j.Logger;
import org.dimdev.rift.listener.MinecraftStartListener;
import zsawyer.mods.mumblelink.api.MumbleLink;
import zsawyer.mods.mumblelink.api.MumbleLinkAPI;
import zsawyer.mods.mumblelink.mumble.ExtendedUpdateData;

/**
 * mod to link with mumble for positional audio
 * <p>
 * this is a forge based implementation
 *
 * @author zsawyer, 2013-04-09
 */
// TODO: use "canBeDeactivated = true" to allow mod deactivation via FML
public class MumbleLinkImpl extends MumbleLinkBase implements
        MumbleLink {
    //public static Logger LOG;

    // The instance of the mod that Forge uses.
    public static MumbleLinkImpl instance;
    //
    private UpdateTicker updateTicker;
    private MumbleLinkAPIImpl api;

    private boolean enabled = true;
    private boolean debug = false;

    private String name = "MumbleLink";
    private String version = "unknown";

    //@Override
    public void onMinecraftStart() {
        /*LOG = FMLPreInitializationEvent.getModLog();
        name = event.getModMetadata().name;
        version = event.getModMetadata().version;

        if (FMLCommonHandler.instance().getSide().isServer())
            throw new RuntimeException(name
                    + " should not be installed on a server!");

        loadConfig(event);
        */
        load();
        initComponents();
        activate();
    }

    /*
    private void loadConfig(FMLPreInitializationEvent event) {
        ConfigHelper configHelper = new ConfigHelper(event);

        debug = configHelper.loadDebug(debug);
        enabled = configHelper.loadEnabled(enabled);
    }
    */

    private void initComponents() {
        ExtendedUpdateData extendedUpdateData = new ExtendedUpdateData(library,
                errorHandler);
        mumbleData = extendedUpdateData;
        updateTicker = new UpdateTicker();
        api = new MumbleLinkAPIImpl();
        api.setExtendedUpdateData(extendedUpdateData);
    }

    @Override
    public MumbleLinkImpl getInstance() {
        if (instance == null) {
            instance = new MumbleLinkImpl();
        }
        return instance;
    }

    @Override
    public MumbleLinkAPI getApi() {
        return api;
    }

    @Override
    public void activate() {
        updateTicker.activate();
    }

    @Override
    public void deactivate() {
        updateTicker.deactivate();
    }

    @Override
    public boolean debugging() {
        return debug;
    }

    public static boolean debug() {
        return instance != null ? instance.debug: false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVersion() {
        return version;
    }
}
