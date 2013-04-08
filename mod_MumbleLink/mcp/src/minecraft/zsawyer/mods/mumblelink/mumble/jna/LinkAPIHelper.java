package zsawyer.mods.mumblelink.mumble.jna;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import zsawyer.mods.mumblelink.error.NativeInitErrorHandler.NativeInitError;
import zsawyer.mumble.jna.LinkAPILibrary;

public final class LinkAPIHelper {
	private LinkAPILibrary libraryInstance;

	public LinkAPIHelper(LinkAPILibrary libraryInstance) {
		this.libraryInstance = libraryInstance;
	}

	
	
	public static CharBuffer parseToCharBuffer(int capacity, String value) {
		CharBuffer buffer = CharBuffer.allocate(capacity);
		buffer.rewind();
		buffer.put(value.toCharArray());
		buffer.rewind();
		return buffer;
	}

	public static ByteBuffer parseToByteBuffer(int capacity, String value) {
		ByteBuffer buffer = ByteBuffer.allocate(capacity);
		buffer.rewind();
		buffer.put(value.getBytes());
		buffer.rewind();
		return buffer;
	}

	public synchronized LinkAPILibrary getLibraryInstance() {
		return libraryInstance;
	}

	public NativeInitError initialize(String pluginName,
			String pluginDescription, int pluginUiVersion) {
				
		int errorCode = libraryInstance.initialize(
				parseToCharBuffer(LinkAPILibrary.MAX_NAME_LENGTH, pluginName), 
				parseToCharBuffer(LinkAPILibrary.MAX_DESCRIPTION_LENGTH, pluginDescription),
				pluginUiVersion);
		return NativeInitError.fromCode(errorCode);
	}	
}
