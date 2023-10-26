# PROTOCOL
##### create simple packet-based client-server communication with netty

### Example Packet:
```java
public class ExamplePacket extends Packet {
    
    private UUID id;
    
    public ExamplePacket() {
    }
    
    public ExamplePacket(UUID id) {
        this.id = id;
    }
    
    @Override
    public void read(PacketBuffer buffer) {
        this.id = buffer.readUUID();
    }
    
    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeUUID(this.id);
    }
    
    public UUID getId() {
        return this.id;
    }
    
}
```

### Example Server:
```java
SocketServer socketServer = Protocol.createServer();
socketServer.registerPacket(0, ExamplePacket.class);
socketServer.registerListener(ExamplePacket.class, (packet, channel) -> {
    System.out.println(packet.getId().toString());
});
```

### Example Client:
```java
SocketClient socketClient = Protocol.createClient("client#1");
socketClient.registerPacket(0, ExamplePacket.class);
socketClient.sendPacket(new ExamplePacket(UUID.randomUUID()));
```