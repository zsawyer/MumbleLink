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

package zsawyer.mods.mumblelink.addons.pa.es;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zsawyer.mods.mumblelink.api.Activateable;
import zsawyer.mods.mumblelink.api.IdentityManipulator;
import zsawyer.mods.mumblelink.api.MumbleLink;
import zsawyer.mods.mumblelink.util.InstanceHelper;
import zsawyer.mods.mumblelink.util.json.JSONArray;
import zsawyer.mods.mumblelink.util.json.JSONException;
import zsawyer.mods.mumblelink.util.json.JSONObject;

import javax.annotation.Nonnull;
import javax.management.InstanceNotFoundException;

/**
 * An addon to the MumbleLink mod (forge version) which injects extended
 * positional audio support (i.e. identity) based on vanilla Minecraft.
 *
 * @author zsawyer, 2013-07-05
 * @version 1.0.1
 */
@Mod(ExtendedPASupport.MOD_ID)
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
@OnlyIn(Dist.CLIENT)
public class ExtendedPASupport implements Activateable, IdentityManipulator {
    public static Logger LOG = LogManager.getLogger();

    public static final @Nonnull
    String MOD_ID = "extendedpasupport";
    public final static @Nonnull
    String MOD_NAME = "ExtendedPASupport for MumbleLink";
    public final static @Nonnull
    String VERSION = "1.1.0";
    public final static @Nonnull
    String MOD_DEPENDENCIES = "required-after:" + MumbleLink.MOD_ID;

    // whether this mod is active
    private boolean enabled = true;
    // whether debugging mode is on
    private boolean debug = false;

    private String name = "ExtendedPASupport for MumbleLink";
    private String version = "unknown";
    private MumbleLink mumbleLinkInstance;

    public ExtendedPASupport() {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        this.preInit();
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void setup(InterModEnqueueEvent event) {
        LOG.debug("setup started");
        try {
            if (enabled) {
                load();
                LOG.info("loaded and active");
            }
        } catch (Throwable t) {
            String s = "Error in mod during setup " + getName();
            ModContainer modContainer = ModList.get().getModContainerById(MOD_ID).orElseThrow(() -> new RuntimeException(s, t));
            throw new ModLoadingException(modContainer.getModInfo(), modContainer.getCurrentState(), s, t);
        }
        LOG.debug("setup finished");
    }

    public void preInit() {
        ModLoadingContext context = ModLoadingContext.get();
        context.registerExtensionPoint(IExtensionPoint.DisplayTest.class
                , () -> new IExtensionPoint.DisplayTest(
                        () -> "ANY",
                        (serverVer, isDedicated) -> true));
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

    public void load() throws InstanceNotFoundException {
        mumbleLinkInstance = InstanceHelper.getMumbleLink();

        if (enabled && mumbleLinkInstance != null) {
            activate();
        }
    }

    @Override
    public void activate() {
        mumbleLinkInstance.getApi().register(this);
    }

    @Override
    public void deactivate() {
        mumbleLinkInstance.getApi().unregister(this);
    }

    @Override
    public String manipulateIdentity(String identity, Minecraft game,
                                     int maxLength) {

        try {
            // presume the previous identity is a JSON-Object
            JSONObject newIdentity = new JSONObject(identity);
            // inject our information
            appendToIdentity(newIdentity, game);
            // print the (intermediate) result for debugging
            printDebug(newIdentity, "identity");

            return newIdentity.toString();
        } catch (JSONException e) {
            // no JSON... this is not going to work...
            LOG.fatal("could not generate identity", e);
            return identity;
        }
    }

    /**
     * append our information to the given identity
     *
     * @param identity the identity to be supplement
     * @param game     the game instance from which to retrieve the information
     * @throws JSONException see {@link JSONObject#put(String, Object)}
     */
    private void appendToIdentity(JSONObject identity, Minecraft game)
            throws JSONException {
        // build spawn location coordinates (sadly this is the only somewhat
        // identifiable information the client has about the world (and server)
        // it connects to.
        // TODO: test if we can use game.world.getSeed()
        JSONArray spawnCoordinates = new JSONArray();
        spawnCoordinates.put(game.level.getSharedSpawnPos().getX());
        spawnCoordinates.put(game.level.getSharedSpawnPos().getX());
        spawnCoordinates.put(game.level.getSharedSpawnPos().getX());
        // append coordinates
        identity.put(IdentityKey.WORLD_SPAWN, spawnCoordinates);

        // append the dimension
        identity.put(IdentityKey.DIMENSION, game.player.level.dimension());
    }

    /**
     * short-hand method to print a JSON object in the log
     *
     * @param objectToPrint the object to print to the log
     * @param nameOfObject  the name to show (which identifies the object in the context
     *                      of the log)
     */
    private void printDebug(JSONObject objectToPrint, String nameOfObject) {
        if (debug) {
            ExtendedPASupport.LOG.info(nameOfObject + ": "
                    + objectToPrint.toString(), "");
        }
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
