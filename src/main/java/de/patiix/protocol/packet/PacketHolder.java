package de.patiix.protocol.packet;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

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
public class PacketHolder {

    private final Class<? extends Packet> packetClass;
    private final Constructor<? extends Packet> packetConstructor;

    @SuppressWarnings("unchecked")
    public PacketHolder(Class<? extends Packet> packetClass) throws NoSuchMethodException {
        List<Constructor<?>> constructors = Arrays.stream(packetClass.getConstructors())
                .filter(constructor -> constructor.getParameterCount() == 0)
                .toList();

        if (constructors.isEmpty()) {
            throw new NoSuchMethodException("missing no-args-constructor");
        }

        this.packetClass = packetClass;
        this.packetConstructor = (Constructor<? extends Packet>) constructors.get(0);
    }

    public Class<? extends Packet> getPacketClass() {
        return this.packetClass;
    }

    public Constructor<? extends Packet> getPacketConstructor() {
        return this.packetConstructor;
    }

}
