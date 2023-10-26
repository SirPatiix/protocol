package de.patiix.protocol;

import de.patiix.protocol.socket.SocketConfig;
import de.patiix.protocol.socket.SocketInitPacket;
import de.patiix.protocol.socket.implementation.client.SocketClient;
import de.patiix.protocol.socket.implementation.server.SocketServer;

/*
 * MIT License
 *
 * Copyright (c) 2023 Patrick H.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class Protocol {

    public static SocketClient createClient(SocketConfig config, Object key) {
        SocketClient socketClient = new SocketClient(config, key);
        socketClient.registerPacket(Integer.MAX_VALUE, new SocketInitPacket());

        return socketClient;
    }

    public static SocketServer createServer(SocketConfig config) {
        SocketServer socketServer = new SocketServer(config);
        socketServer.registerPacket(Integer.MAX_VALUE, new SocketInitPacket());
        socketServer.registerListener(SocketInitPacket.class, (packet, channel) -> {
            if (!(config.getKey().isAssignableFrom(packet.getKey().getClass()))) {
                throw new ClassCastException("unable to read key");
            }

            socketServer.getChannels().put(packet.getKey(), channel);
        });

        return socketServer;
    }

}
