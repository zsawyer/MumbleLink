package zsawyer.mods.mumblelink.api;

import zsawyer.mods.mumblelink.mumble.ContextManipulator;
import zsawyer.mods.mumblelink.mumble.IdentityManipulator;

public interface MumbleLinkAPI {

	public void register(IdentityManipulator manipulator);

	public void unregister(IdentityManipulator manipulator);

	public void register(ContextManipulator manipulator);

	public void unregister(ContextManipulator manipulator);
}
