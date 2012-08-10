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
package net.minecraft.src.mumblelink;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author zsawyer
 */
public class BufferedChatNotifier extends ChatNotifier implements Runnable {

    List<String> messages;
    Thread keepTrying;

    public BufferedChatNotifier() {
        super();
        messages = Collections.synchronizedList(new ArrayList<String>());
        keepTrying = new Thread(this);
    }

    @Override
    public void print(String message) {
        messages.add(message);
        startTryingToSend();
    }

    @Override
    public void run() {
        waitUntilSendingPossible();
        sendAllMessages();
    }

    private void startTryingToSend() {
        if (canSendMessage()) {
            sendAllMessages();
        } else {
            try {
                keepTrying.start();
            } catch (IllegalThreadStateException alreadyStartedException) {
                // safely ignore this because we are working on it
            }
        }
    }

    private void sendAllMessages() {
        Iterator<String> messageCrawler = messages.iterator();
        while (messageCrawler.hasNext()) {
            String message = messageCrawler.next();
            send(message);
            messageCrawler.remove();
        }
    }

    private void waitUntilSendingPossible() {
        while (!canSendMessage()) {
            // generous non-time specific wait
            Thread.yield();
        }
    }
}
