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
public interface NativeUpdateErrorHandler {

    public enum NativeUpdateError {

        NOT_YET_INITIALIZED(-1),
        NO_ERROR(0);
        private int code;
        private static final Map<Integer, NativeUpdateError> lookup = new HashMap<Integer, NativeUpdateError>();

        static {
            for (NativeUpdateError error : EnumSet.allOf(NativeUpdateError.class)) {
                lookup.put(error.getCode(), error);
            }
        }

        public static NativeUpdateError fromCode(int code) {
            return lookup.get(code);
        }

        public int getCode() {
            return code;
        }

        private NativeUpdateError(int code) {
            this.code = code;
        }
    }

    void handleError(NativeUpdateError fromCode);
}
