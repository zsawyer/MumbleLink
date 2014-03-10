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

package zsawyer.mods.mumblelink.api;


import net.minecraft.client.Minecraft;

/**
 * A class implementing this interface can register at the MumbleLinkAPI to be
 * consulted when generating the identity to send to Mumble. There is no
 * exclusive privilege, expect other manipulators to run after and before your
 * implementation. Use JSON formatting for compatibility.
 *
 * @author zsawyer
 */
public interface IdentityManipulator {
    /**
     * This method is expected to return a string for use as the Mumble
     * positional audio (PA) identity. The result is required to adhere to the
     * maxLength requirement which is dictated by the Link Plugin's internal
     * data structure and specifies the number of characters that can be sent.
     * <p/>
     * The possibly previously manipulated identity is handed over and expected
     * to be respected by each implementation. Respecting herein means that the
     * previous identity should be - if possible - appended to, or at least be
     * reparsed (e.g. if length is likely to be over stepped).
     * <p/>
     * For compatibility with other addons the suggested format is JSON.
     * <p/>
     * <p/>
     * Excerpt from Mumble Wiki (http://mumble.sourceforge.net/Link 2013-07-08):
     * <p/>
     * Identity should contain a string which uniquely identifies the player in
     * the given context. This is usually satisfied by the in-game player name
     * or the players ID (player-/connection-ID on the server or a global ID).
     * <p/>
     * Additionally the identity can contain any additional information about
     * the player that might be interesting for the mumble server.
     * <p/>
     * For example by including team information in the identity a script on the
     * mumble server can move players into team channels automatically.
     * Additional information like squad number, squad leader status and so on
     * can be used to trigger even more behavior like automatically maintaining
     * a leadership structure inside mumble which is kept in-sync with in-game
     * state. E.g. someone is elected squad leader and now can whisper to all
     * other squad leaders and the team leader. For an example of such a
     * server-side script see the Battlefield 2 MuMo plugin.
     *
     * @param identity  the identity with all previous manipulations applied expect it
     *                  to be a string representation of a JSON-Object which you can
     *                  append key-value pairs to
     * @param game      the Minecraft instance for convenience
     * @param maxLength the maximum number of characters the returned string may have,
     *                  else it will not fit into the Link-Plugin's data structure
     * @return (preferably) a string representation of a JSON-Object (keep in
     * mind that not using JSON will probably make your addon
     * incompatible with other manipulator instances)
     */
    public String manipulateIdentity(String identity, Minecraft game,
                                     int maxLength);

    public static class IdentityKey {
        public static final String NAME = "name";
        public static final String DIMENSION = "dimension";
        public static final String WORLD_SPAWN = "worldSpawn";

        private IdentityKey() {
        }
    }
}
