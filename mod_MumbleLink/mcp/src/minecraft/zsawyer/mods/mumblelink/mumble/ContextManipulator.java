package zsawyer.mods.mumblelink.mumble;

import net.minecraft.client.Minecraft;

public interface ContextManipulator {
	public String manipulateContext(String context, Minecraft game, int maxLength);
}
