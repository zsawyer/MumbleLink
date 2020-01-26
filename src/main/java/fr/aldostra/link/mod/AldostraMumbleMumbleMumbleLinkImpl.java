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

package fr.aldostra.link.mod;

import fr.aldostra.link.mod.api.AldostraMumbleLink;
import fr.aldostra.link.mod.api.AldostraMumbleLinkAPI;
import fr.aldostra.link.mod.mumble.ExtendedUpdateData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * mod to link with mumble for positional audio
 * <p>
 * this is a forge based implementation (Forge 1.13+)
 *
 * @author zsawyer, 2013-04-09
 */
@Mod(value = AldostraMumbleLink.MOD_ID)
@Mod.EventBusSubscriber
@OnlyIn(Dist.CLIENT)
public class AldostraMumbleMumbleMumbleLinkImpl extends AldostraMumbleLinkBase implements AldostraMumbleLink {
    public static Logger LOG = LogManager.getLogger();

    public static AldostraMumbleMumbleMumbleLinkImpl instance;
    //
    private UpdateTicker updateTicker;
    private AldostraMumbleMumbleLinkAPIImpl api;

    public AldostraMumbleMumbleMumbleLinkImpl() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        instance = this;
    }

    private boolean enabled = true;
    private boolean debug = false;

    private String name = "MumbleLink";
    private String version = "unknown";

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void setup(FMLClientSetupEvent event) {
        LOG.debug("setup started");
        preInit();
        if (enabled) {
            load();
            LOG.info("loaded and active");
        }
        LOG.trace("setup finished");
    }

    public void preInit() {
        IModInfo modInfo = ModLoadingContext.get().getActiveContainer().getModInfo();
        name = modInfo.getDisplayName();
        version = modInfo.getVersion().getQualifier();
        loadConfig();
    }

    private void loadConfig() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
        // TODO: make this actually read from config
        debug = false;//Config.CONFIG.debug.get();
        enabled = true;//Config.CONFIG.enabled.get();
    }

    public void load() {
        super.load();
        initComponents();
        activate();
    }

    private void initComponents() {
        ExtendedUpdateData extendedUpdateData = new ExtendedUpdateData(library, errorHandler);
        mumbleData = extendedUpdateData;
        updateTicker = new UpdateTicker();
        api = new AldostraMumbleMumbleLinkAPIImpl();
        api.setExtendedUpdateData(extendedUpdateData);
    }

    @Override
    public AldostraMumbleLinkAPI getApi() {
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
        return instance.debug;
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
