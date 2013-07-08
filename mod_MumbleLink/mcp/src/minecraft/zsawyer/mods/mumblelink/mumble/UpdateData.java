/*
 mod_MumbleLink - Positional Audio Communication for Minecraft with Mumble
 Copyright 2012 zsawyer (http://sourceforge.net/users/zsawyer)

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
package zsawyer.mods.mumblelink.mumble;

import java.util.logging.Level;

import net.minecraft.client.Minecraft;
import net.minecraft.src.ModLoader;
import net.minecraft.util.Vec3;
import zsawyer.mods.mumblelink.MumbleLinkBase;
import zsawyer.mods.mumblelink.MumbleLinkConstants;
import zsawyer.mods.mumblelink.addons.pa.es.ExtendedPASupportConstants.ContextKey;
import zsawyer.mods.mumblelink.addons.pa.es.ExtendedPASupportConstants.IdentityKey;
import zsawyer.mods.mumblelink.error.NativeUpdateErrorHandler;
import zsawyer.mods.mumblelink.error.NativeUpdateErrorHandler.NativeUpdateError;
import zsawyer.mods.mumblelink.json.JSONException;
import zsawyer.mods.mumblelink.json.JSONObject;
import zsawyer.mods.mumblelink.mumble.jna.LinkAPIHelper;
import zsawyer.mumble.jna.LinkAPILibrary;

/**
 * 
 * @author zsawyer
 */
public class UpdateData {

	float[] fAvatarPosition = { 0, 0, 0 }; // [3]
	float[] fAvatarFront = { 0, 0, 0 }; // [3]
	float[] fAvatarTop = { 0, 0, 0 }; // [3]
	String name = ""; // [256]
	String description = ""; // [2048]
	float[] fCameraPosition = { 0, 0, 0 }; // [3]
	float[] fCameraFront = { 0, 0, 0 }; // [3]
	float[] fCameraTop = { 0, 0, 0 }; // [3]
	String identity = ""; // [256]
	String context = ""; // [256]
	LinkAPILibrary mumbleLink;
	NativeUpdateErrorHandler errorHandler;
	private int uiTick = 0;

	public UpdateData(LinkAPILibrary mumbleLink,
			NativeUpdateErrorHandler errorHandler) {
		this.mumbleLink = mumbleLink;
		this.errorHandler = errorHandler;

		name = MumbleInitializer.PLUGIN_NAME;
		description = MumbleInitializer.PLUGIN_DESCRIPTION;
	}

	public void send() {
		LinkAPILibrary.LinkedMem lm = new LinkAPILibrary.LinkedMem();

		lm.identity = LinkAPIHelper.parseToCharBuffer(
				LinkAPILibrary.MAX_IDENTITY_LENGTH, identity).array();
		lm.context = LinkAPIHelper.parseToByteBuffer(
				LinkAPILibrary.MAX_CONTEXT_LENGTH, context).array();
		lm.context_len = context.length();

		lm.name = LinkAPIHelper.parseToCharBuffer(
				LinkAPILibrary.MAX_NAME_LENGTH, name).array();
		lm.description = LinkAPIHelper.parseToCharBuffer(
				LinkAPILibrary.MAX_DESCRIPTION_LENGTH, description).array();

		lm.uiTick = ++uiTick;
		lm.uiVersion = MumbleInitializer.PLUGIN_UI_VERSION;

		lm.fAvatarPosition = fAvatarPosition;
		lm.fAvatarFront = fAvatarFront;
		lm.fAvatarTop = fAvatarTop;

		lm.fCameraPosition = fCameraPosition;
		lm.fCameraFront = fCameraFront;
		lm.fCameraTop = fCameraTop;

		byte successMessage = mumbleLink.updateData(lm);
		boolean success = (successMessage != 0);

		if (!success) {
			errorHandler
					.handleError(NativeUpdateError.ERROR_NOT_YET_INITIALIZED);
		}

	}

	public void set(Minecraft game) {
		try {
			// 1 unit = 1 meter

			// TODO: use full vectors (all axes)

			// initialize multipliers
			float fAvatarFrontX = 1;
			float fAvatarFrontY = 0; // cancel out if the user is looking up or
										// down
			float fAvatarFrontZ = 1;

			float fCameraFrontX = 1;
			float fCameraFrontY = 0; // cancel out if the user is looking up or
										// down
			float fCameraFrontZ = 1;

			float fAvatarTopX = 0;
			float fAvatarTopY = 1; // Y points up
			float fAvatarTopZ = 0;

			float fCameraTopX = 0;
			float fCameraTopY = 1; // Y points up
			float fCameraTopZ = 0;

			Vec3 lookDirection = game.thePlayer.getLookVec();

			/*
			 * TODO: calculate real camera vector from pitch and yaw // camera
			 * pitch in degrees (e.g. 0.0f to 360.0f) Float cameraPitch =
			 * game.thePlayer.cameraPitch; // camera yaw in degrees (e.g. 0.0f
			 * to 360.0f) Float cameraYaw = game.thePlayer.cameraYaw;
			 */

			// Position of the avatar
			fAvatarPosition = new float[] {
					Float.parseFloat(Double.toString(game.thePlayer.posX)), // note:
																			// losing
																			// precision
																			// here
					Float.parseFloat(Double.toString(game.thePlayer.posZ)), // note:
																			// losing
																			// precision
																			// here
					Float.parseFloat(Double.toString(game.thePlayer.posY)) }; // note:
																				// losing
																				// precision
																				// here

			// Unit vector pointing out of the avatar's eyes (here Front looks
			// into scene).
			fAvatarFront = new float[] {
					Float.parseFloat(Double.toString(lookDirection.xCoord
							* fAvatarFrontX)), // note: losing precision here
					Float.parseFloat(Double.toString(lookDirection.zCoord
							* fAvatarFrontZ)), // note: losing precision here
					Float.parseFloat(Double.toString(lookDirection.yCoord
							* fAvatarFrontY)) }; // note: losing precision here

			// Unit vector pointing out of the top of the avatar's head (here
			// Top looks straight up).
			fAvatarTop = new float[] { fAvatarTopX, fAvatarTopZ, fAvatarTopY };

			// TODO: use real camera position, s.a.
			fCameraPosition = new float[] {
					Float.parseFloat(Double.toString(game.thePlayer.posX)), // note:
																			// losing
																			// precision
																			// here
					Float.parseFloat(Double.toString(game.thePlayer.posZ)), // note:
																			// losing
																			// precision
																			// here
					Float.parseFloat(Double.toString(game.thePlayer.posY)) }; // note:
																				// losing
																				// precision
																				// here

			// TODO: use real look vector, s.a.
			fCameraFront = new float[] {
					Float.parseFloat(Double.toString(lookDirection.xCoord
							* fCameraFrontX)), // note: losing precision here
					Float.parseFloat(Double.toString(lookDirection.zCoord
							* fCameraFrontZ)), // note: losing precision here
					Float.parseFloat(Double.toString(lookDirection.yCoord
							* fCameraFrontY)) }; // note: losing precision here

			fCameraTop = new float[] { fCameraTopX, fCameraTopZ, fCameraTopY };

			// Identifier which uniquely identifies a certain player in a
			// context (e.g. the ingame Name).
			identity = generateIdentity(game,
					LinkAPILibrary.MAX_IDENTITY_LENGTH);

			// Context should be equal for players which should be able to hear
			// each other positional and
			// differ for those who shouldn't (e.g. it could contain the
			// server+port and team)
			// CAUTION: max len: 256
			context = generateContext(game, LinkAPILibrary.MAX_CONTEXT_LENGTH);

		} catch (Exception ex) {
			// we'll just ignore errors since they would become too spammy and
			// we will retry anyways
			// ModLoader.getLogger().log(Level.SEVERE, null, ex);
		}
	}

	protected String generateIdentity(Minecraft game, int maxLength) {
		try {
			JSONObject newIdentity = new JSONObject();
			newIdentity.put(IdentityKey.NAME, game.thePlayer.getEntityName());
			return newIdentity.toString();
		} catch (JSONException e) {
			ModLoader.getLogger().log(Level.SEVERE,
					"could not generate identity", e);
		}

		return game.thePlayer.getEntityName();
	}

	protected String generateContext(Minecraft game, int maxLength) {
		try {
			JSONObject newContext = new JSONObject();
			newContext.put(ContextKey.DOMAIN,
					MumbleLinkConstants.MUMBLE_CONTEXT_DOMAIN_ALL_TALK);
			return newContext.toString();
		} catch (JSONException e) {
			ModLoader.getLogger().log(Level.SEVERE,
					"could not generate context", e);
		}

		return MumbleLinkConstants.MUMBLE_CONTEXT_DOMAIN_ALL_TALK;
	}
}
