/*
 mod_MumbleLink - Positional Audio Communication for Minecraft with Mumble
 Adapted by Aldostra Team (http://www.aldostra.fr/)
 Copyright 2011-2013 zsawyer (http://sourceforge.net/users/zsawyer)

 This file is part of mod_MumbleLink
 (http://sourceforge.net/projects/modmumblelink/)
 Adapted on this fork : https://github.com/alexandrelefourner/MumbleLink.

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

package fr.aldostra.link.mod.api;

import net.minecraft.client.Minecraft;

/**
 * A class implementing this interface can register at the {@link AldostraMumbleLinkAPI} to be
 * consulted when generating the context to send to Mumble. There is no
 * exclusive privilege, expect other manipulators to run after and before your
 * implementation. Use JSON formatting for compatibility.
 *
 * @author zsawyer
 */
public interface ContextManipulator {
    /**
     * <p>
     * This method is expected to return a string for use as the Mumble
     * positional audio (PA) context. The result is required to adhere to the
     * maxLength requirement which is dictated by the Link Plugin's internal
     * data structure and specifies the number of characters that can be sent.
     * </p><p>
     * The possibly previously manipulated context is handed over and expected
     * to be respected by each implementation. Respecting herein means that the
     * previous context should be - if possible - appended to, or at least be
     * reparsed (e.g. if length is likely to be over stepped).
     * </p><p>
     * For compatibility with other addons the suggested format is JSON.
     * </p><br>
     * <p>
     * Excerpt from Mumble Wiki (http://mumble.sourceforge.net/Link 2013-07-08):
     * </p><p>
     * The context string is used to determine which users on a Mumble server
     * should hear each other positionally. If context between two mumble user
     * does not match the positional audio data is stripped server-side and
     * voice will be received as non-positional.
     * </p><p>
     * Accordingly the context should only match for players on the same server
     * in the same game on the same map. Whether to include things like team in
     * this string depends on the game itself. When in doubt err on the side of
     * including less. This gives more flexibility later on.
     * </p>
     *
     * @param context   the context with all previous manipulations applied expect it
     *                  to be a string representation of a JSON-Object which you can
     *                  append key-value pairs to
     * @param game      the Minecraft instance for convenience
     * @param maxLength the maximum number of characters the returned string may have,
     *                  else it will not fit into the Link-Plugin's data structure
     * @return (preferably) a string representation of a JSON-Object (keep in
     * mind that not using JSON will probably make your addon
     * incompatible with other manipulator instances)
     */
    public String manipulateContext(String context, Minecraft game,
                                    int maxLength);

    /**
     * class holding a set of common keys to expect to be present within the context
     */
    public static class ContextKey {
        public static final String DOMAIN = "domain";

        private ContextKey() {
        }
    }
}
