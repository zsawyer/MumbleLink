package zsawyer.mods.mumblelink.util;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import zsawyer.mods.mumblelink.api.MumbleLink;

import javax.management.InstanceNotFoundException;

public class InstanceHelper {

    private static MumbleLink mumbleLinkInstance;

    public static MumbleLink getMumbleLink() throws InstanceNotFoundException {
        if (mumbleLinkInstance != null) {
            return mumbleLinkInstance;
        }

        ModContainer modContainer = Loader.instance().getIndexedModList().get(MumbleLink.MOD_ID);

        if (modContainer != null) {
            Object mod = modContainer.getMod();

            if (mod instanceof MumbleLink) {
                mumbleLinkInstance = (MumbleLink) mod;
                return mumbleLinkInstance;
            }
        }

        throw new InstanceNotFoundException(MumbleLink.MOD_ID);
    }

    private InstanceHelper() {
    }
}
