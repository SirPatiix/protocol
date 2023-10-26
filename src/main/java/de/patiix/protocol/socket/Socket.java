package de.patiix.protocol.socket;

import de.patiix.protocol.packet.Packet;
import de.patiix.protocol.packet.PacketHolder;
import de.patiix.protocol.packet.PacketListener;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public abstract class Socket {

    private final Int2ObjectOpenHashMap<PacketHolder> packets;
    private final Map<Class<? extends Packet>, List<PacketListener<? extends Packet>>> listeners;

    public Socket() {
        this.packets = new Int2ObjectOpenHashMap<>();
        this.listeners = new HashMap<>();
    }

    public abstract void connect();

    public void registerPacket(int id, Packet packet) {
        this.registerPacket(id, packet.getClass());
    }

    public void registerPacket(int id, Class<? extends Packet> packet) {
        try {
            this.packets.put(id, new PacketHolder(packet));
        } catch (NoSuchMethodException exception) {
            System.err.println(exception.getMessage());
        }
    }

    public <T extends Packet> void registerListener(Class<T> packet, PacketListener<T> packetListener) {
        if (!(this.listeners.containsKey(packet))) {
            this.listeners.put(packet, new ArrayList<>());
        }

        this.listeners.get(packet).add(packetListener);
    }

    @SuppressWarnings("unchecked")
    public <T extends Packet> T construct(int id) {
        try {
            return (T) this.packets.get(id).getPacketConstructor().newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException exception) {
            System.err.println(exception.getMessage());
        }
        return null;
    }

    public Integer getPacket(Class<? extends Packet> packet) {
        for (ObjectIterator<Int2ObjectMap.Entry<PacketHolder>> iterator = this.packets.int2ObjectEntrySet().fastIterator(); iterator.hasNext();) {
            Int2ObjectMap.Entry<PacketHolder> entry = iterator.next();
            if (entry.getValue().getPacketClass().equals(packet)) {
                return entry.getIntKey();
            }
        }
        return -1;
    }

    public Int2ObjectOpenHashMap<PacketHolder> getPackets() {
        return this.packets;
    }

    public Map<Class<? extends Packet>, List<PacketListener<? extends Packet>>> getListeners() {
        return this.listeners;
    }

}
