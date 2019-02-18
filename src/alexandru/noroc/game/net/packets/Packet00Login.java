package alexandru.noroc.game.net.packets;

import alexandru.noroc.game.net.GameClient;
import alexandru.noroc.game.net.GameServer;

public class Packet00Login extends Packet
{
    private String username;

    public Packet00Login(byte[] data)
    { // this is when we retrieve data
        super(00);
        this.username = readData(data);
    }

    public Packet00Login(String username)
    { //this is when we are sending from the client
        super(00);
        this.username = username;
    }

    @Override
    public void writeData(GameClient client) {
        client.sendData(getData());
    }

    @Override
    public void writeData(GameServer server) {
        server.sendDataToAllClients(getData());
    }


    @Override
    public byte[] getData() {
        return ("00" + this.username).getBytes();
    }

    public String getUsername()
    {
        return username;
    }
}
