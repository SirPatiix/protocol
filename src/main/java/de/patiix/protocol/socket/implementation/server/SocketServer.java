package de.patiix.protocol.socket.implementation.server;

import de.patiix.protocol.ProtocolUtil;
import de.patiix.protocol.socket.Socket;
import de.patiix.protocol.socket.SocketConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

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
public class SocketServer extends Socket {

    private final Integer port;
    private final Map<Object, Channel> channels;

    public SocketServer(SocketConfig config) {
        this.port = config.getPort();
        this.channels = new HashMap<>();

        this.connect();
    }

    @Override
    public void connect() {
        EventLoopGroup eventLoopGroup = ProtocolUtil.getEventLoopGroup();
        EventLoopGroup eventLoopGroupWorker = ProtocolUtil.getEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(eventLoopGroup, eventLoopGroupWorker);
        serverBootstrap.channel(ProtocolUtil.getServerSocketChannel());
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        serverBootstrap.childHandler(new SocketServerInitializer(this));

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                ChannelFuture channelFuture = serverBootstrap.bind(this.port);
                channelFuture.sync();
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException exception) {
                System.err.println(exception.getMessage());
            } finally {
                eventLoopGroup.shutdownGracefully();
                eventLoopGroupWorker.shutdownGracefully();
            }
        });
    }

    @SuppressWarnings("unused")
    public Channel getChannel(Object key) {
        return this.getChannels().get(key);
    }

    @SuppressWarnings("unused")
    public Map<Object, Channel> getChannels() {
        return this.channels;
    }

}
