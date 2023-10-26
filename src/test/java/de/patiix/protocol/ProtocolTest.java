package de.patiix.protocol;

import de.patiix.protocol.socket.SocketConfig;
import de.patiix.protocol.socket.SocketInitPacket;
import de.patiix.protocol.socket.implementation.client.SocketClient;
import de.patiix.protocol.socket.implementation.server.SocketServer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

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
public class ProtocolTest {

    private final CountDownLatch latch = new CountDownLatch(1);
    private final SocketConfig config = new SocketConfig() {
        @Override
        public String getHostname() {
            return "127.0.0.1";
        }

        @Override
        public Integer getPort() {
            return 1357;
        }

        @Override
        public Class<?> getKey() {
            return UUID.class;
        }
    };

    private final String debug = "Hello World!";

    @Before
    public void setup() throws InterruptedException {
        SocketServer socketServer = Protocol.createServer(this.config);
        socketServer.registerPacket(0, ProtocolTestPacket.class);
        socketServer.registerListener(SocketInitPacket.class, (packet, channel) -> {
            channel.writeAndFlush(new ProtocolTestPacket(debug));
        });

        Thread.sleep(2000);
    }

    @Test
    public void test() throws InterruptedException {
        SocketClient socketClient = Protocol.createClient(this.config, UUID.randomUUID());
        socketClient.registerPacket(0, ProtocolTestPacket.class);
        socketClient.registerListener(ProtocolTestPacket.class, (packet, channel) -> {
            this.latch.countDown();
            Assert.assertEquals(packet.getMessage(), this.debug);
        });

        this.latch.await();
    }

}
