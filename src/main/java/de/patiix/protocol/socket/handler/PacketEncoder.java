package de.patiix.protocol.socket.handler;

import de.patiix.protocol.packet.Packet;
import de.patiix.protocol.packet.PacketBuffer;
import de.patiix.protocol.socket.Socket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

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
public class PacketEncoder extends MessageToByteEncoder<Packet> {

    private final Socket socket;

    public PacketEncoder(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void encode(ChannelHandlerContext context, Packet packet, ByteBuf buf) throws Exception {
        int id = this.socket.getPacket(packet.getClass());
        if (id < 0) {
            return;
        }

        buf.writeInt(id);

        PacketBuffer buffer = new PacketBuffer();
        packet.write(buffer);

        buf.writeBytes(buffer);
    }

}
