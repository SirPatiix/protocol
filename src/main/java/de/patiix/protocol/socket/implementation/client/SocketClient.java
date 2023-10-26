package de.patiix.protocol.socket.implementation.client;

import de.patiix.protocol.ProtocolUtil;
import de.patiix.protocol.packet.Packet;
import de.patiix.protocol.socket.Socket;
import de.patiix.protocol.socket.SocketConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
public class SocketClient extends Socket {

    private final String hostname;
    private final Integer port;

    private Channel channel;

    private final ExecutorService executor;

    private final EventLoopGroup eventLoopGroup;
    private final Bootstrap bootstrap;

    @SuppressWarnings("all")
    private final Object key;

    public SocketClient(SocketConfig config, Object key) throws ClassCastException {
        if (!(config.getKey().isAssignableFrom(key.getClass()))) {
            throw new ClassCastException("unable to initialize key");
        }

        this.key = key;

        this.hostname = config.getHostname();
        this.port = config.getPort();

        this.eventLoopGroup = ProtocolUtil.getEventLoopGroup();

        this.bootstrap = new Bootstrap();
        this.bootstrap.group(this.eventLoopGroup);
        this.bootstrap.channel(ProtocolUtil.getSocketChannel());
        this.bootstrap.handler(new SocketClientInitializer(this));

        this.executor = Executors.newSingleThreadExecutor();

        this.connect();
    }

    @Override
    public void connect() {
        ChannelFuture channelFuture = bootstrap.connect(this.hostname, this.port);
        channelFuture.addListener((ChannelFutureListener) future -> {
            if (!(future.isSuccess())) {
                eventLoopGroup.schedule(this::connect, 2, TimeUnit.SECONDS);
            } else {
                this.executor.execute(() -> {
                    try {
                        this.channel = future.channel();

                        future.channel().closeFuture().addListener((ChannelFutureListener) closeFuture -> this.connect());
                        future.channel().closeFuture().sync();
                    } catch (InterruptedException exception) {
                        System.err.println(exception.getMessage());
                    }
                });
            }
        });
    }

    @SuppressWarnings("unused")
    public <T extends Packet> void sendPacket(T packet) {
        this.channel.writeAndFlush(packet);
    }

}
