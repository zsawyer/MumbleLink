package zsawyer.mods.mumblelink.api;

import zsawyer.mods.mumblelink.mumble.ContextManipulator;
import zsawyer.mods.mumblelink.mumble.ExtendedUpdateData;
import zsawyer.mods.mumblelink.mumble.IdentityManipulator;

public class MumbleLinkAPIInstance implements MumbleLinkAPI {

	private ExtendedUpdateData extendedUpdateData;

	public void setExtendedUpdateData(ExtendedUpdateData extendedUpdateData) {
		this.extendedUpdateData = extendedUpdateData;
	}

	@Override
	public void register(IdentityManipulator manipulator) {
		extendedUpdateData.register(manipulator);
	}

	@Override
	public void unregister(IdentityManipulator manipulator) {
		extendedUpdateData.unregister(manipulator);
	}

	@Override
	public void register(ContextManipulator manipulator) {
		extendedUpdateData.register(manipulator);
	}

	@Override
	public void unregister(ContextManipulator manipulator) {
		extendedUpdateData.unregister(manipulator);
	}
}
