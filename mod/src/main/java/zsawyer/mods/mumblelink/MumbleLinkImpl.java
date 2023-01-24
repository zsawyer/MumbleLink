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

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zsawyer.mods.mumblelink.api.MumbleLink;
import zsawyer.mods.mumblelink.api.MumbleLinkAPI;
import zsawyer.mods.mumblelink.mumble.ExtendedUpdateData;

/**
 * mod to link with mumble for positional audio
 * <p>
 * this is a forge based implementation (Forge 1.15+)
 *
 * @author zsawyer, 2013-04-09
 */
@Mod(value = MumbleLink.MOD_ID)
@Mod.EventBusSubscriber
@OnlyIn(Dist.CLIENT)
public class MumbleLinkImpl extends MumbleLinkBase implements MumbleLink {
    public static Logger LOG = LogManager.getLogger();

    public static MumbleLinkImpl instance;
    //
    private UpdateTicker updateTicker;
    private MumbleLinkAPIImpl api;

    public MumbleLinkImpl() {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        this.preInit();
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
        if (enabled) {
            load(event);
            LOG.info("loaded and active");
        }
        LOG.trace("setup finished");
    }

    public void preInit() {
        ModLoadingContext context = ModLoadingContext.get();
        context.registerExtensionPoint(IExtensionPoint.DisplayTest.class
                , () -> new IExtensionPoint.DisplayTest(
                        () -> "ANY",
                        (serverVer, isDedicated) -> true));
        IModInfo modInfo = context.getActiveContainer().getModInfo();
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

    public void load(FMLClientSetupEvent event) {
        super.load();
        initComponents();
        activate();
    }

    private void initComponents() {
        ExtendedUpdateData extendedUpdateData = new ExtendedUpdateData(library, errorHandler);
        mumbleData = extendedUpdateData;
        updateTicker = new UpdateTicker();
        api = new MumbleLinkAPIImpl();
        api.setExtendedUpdateData(extendedUpdateData);
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
