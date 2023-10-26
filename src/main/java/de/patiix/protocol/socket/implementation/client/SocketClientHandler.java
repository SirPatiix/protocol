package de.patiix.protocol.socket.implementation.client;

import de.patiix.protocol.packet.Packet;
import de.patiix.protocol.packet.PacketListener;
import de.patiix.protocol.socket.SocketInitPacket;
import de.patiix.protocol.socket.handler.PacketChannelInboundHandler;
import io.netty.channel.ChannelHandlerContext;

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
public class SocketClientHandler extends PacketChannelInboundHandler {

    public SocketClientHandler(SocketClient socket) {
        super(socket);
    }

    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
        ((SocketClient) this.getSocket()).setChannel(context.channel());
        context.channel().writeAndFlush(new SocketInitPacket(((SocketClient) this.getSocket()).getKey()));
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void channelRead0(ChannelHandlerContext context, Packet packet) throws Exception {
        if (this.getSocket().getListeners().containsKey(packet.getClass())) {
            for (PacketListener listener : this.getSocket().getListeners().get(packet.getClass())) {
                listener.handle(packet, context.channel());
            }
        }
    }

}
