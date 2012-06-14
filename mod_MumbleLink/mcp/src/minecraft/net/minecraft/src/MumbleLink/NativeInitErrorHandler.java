/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.minecraft.src.mumblelink;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zsawyer
 */
public interface NativeInitErrorHandler {

    public enum NativeInitError {

        NOT_YET_INITIALIZED(-1),
        NO_ERROR(0);
        private int code;
        private static final Map<Integer, NativeInitError> lookup = new HashMap<Integer, NativeInitError>();

        static {
            for (NativeInitError error : EnumSet.allOf(NativeInitError.class)) {
                lookup.put(error.getCode(), error);
            }
        }

        public static NativeInitError fromCode(int code) {
            return lookup.get(code);
        }

        public int getCode() {
            return code;
        }

        private NativeInitError(int code) {
            this.code = code;
        }
    }

    void handleError(NativeInitError fromCode);
}
