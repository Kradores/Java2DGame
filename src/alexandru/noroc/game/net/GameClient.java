package alexandru.noroc.game.net;

import alexandru.noroc.game.Game;
import alexandru.noroc.game.entities.PlayerMP;
import alexandru.noroc.game.net.packets.Packet;
import alexandru.noroc.game.net.packets.Packet.PacketTypes;
import alexandru.noroc.game.net.packets.Packet00Login;

import java.io.IOException;
import java.net.*;

public class GameClient extends Thread
{
    private InetAddress ipAddress;
    private DatagramSocket socket;
    private Game game;

    public GameClient(Game game, String ipAddress)
    {
        this.game = game;
        try {
            this.socket = new DatagramSocket();
            this.ipAddress = InetAddress.getByName(ipAddress);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void run()
    {
        while (true)
        {
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
        }
    }

    private void parsePacket(byte[] data, InetAddress address, int port)
    {
        String message = new String(data).trim();
        PacketTypes type = Packet.lookupPacket(message.substring(0, 2));
        System.out.println(message);
        Packet packet;
        switch(type)
        {
            default:
                System.out.println("DEFAULT");
            case INVALID:
                System.out.println("INVALID");
                break;
            case LOGIN:
                packet = new Packet00Login(data);
                System.out.println("["+address.getHostAddress()+":"+port+"] "+
                        ((Packet00Login)packet).getUsername()+" has joined the game ...");
                PlayerMP player = new PlayerMP(game.level, 100, 100, ((Packet00Login)packet).getUsername(), address, port);
                game.level.addEntity(player);
                break;
            case DISCONNECT:
                System.out.println("DISCONNECT");
                break;
        }
    }

    public void sendData(byte[] data)
    {
        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, 13331);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
