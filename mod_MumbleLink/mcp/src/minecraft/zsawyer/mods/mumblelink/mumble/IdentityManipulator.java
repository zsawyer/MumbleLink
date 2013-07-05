package zsawyer.mods.mumblelink.mumble;

import net.minecraft.client.Minecraft;

public interface IdentityManipulator {
	public String manipulateIdentity(String identity, Minecraft game, int maxLength);
}
