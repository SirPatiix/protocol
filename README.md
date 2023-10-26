# PROTOCOL
##### create simple packet-based client-server communication with netty

### Example Config:
```java
public class ExampleConfig extends SocketConfig {

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
        return String.class;
    }

}
```

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
SocketServer socketServer = Protocol.createServer(new ExampleConfig());
socketServer.registerPacket(0, ExamplePacket.class);
socketServer.registerListener(ExamplePacket.class, (packet, channel) -> {
    System.out.println(packet.getId().toString());
});
```

### Example Client:
```java
SocketClient socketClient = Protocol.createClient(new ExampleConfig(), "client#1");
socketClient.registerPacket(0, ExamplePacket.class);
socketClient.sendPacket(new ExamplePacket(UUID.randomUUID()));
```