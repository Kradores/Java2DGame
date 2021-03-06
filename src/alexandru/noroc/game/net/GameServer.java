package alexandru.noroc.game.net;

import alexandru.noroc.game.Game;
import alexandru.noroc.game.entities.PlayerMP;
import alexandru.noroc.game.net.packets.Packet;
import alexandru.noroc.game.net.packets.Packet.PacketTypes;
import alexandru.noroc.game.net.packets.Packet00Login;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class GameServer extends Thread
{
    private DatagramSocket socket;
    private Game game;
    private List<PlayerMP> connectedPlayers = new ArrayList<>();

    public GameServer(Game game)
    {
        this.game = game;
        try {
            this.socket = new DatagramSocket(13331);
        } catch (SocketException e) {
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
        Packet packet = null;
        switch(type)
        {
            default:
            case INVALID:
                break;
            case LOGIN:
                packet = new Packet00Login(data);
                System.out.println("["+address.getHostAddress()+":"+port+"] "+
                        ((Packet00Login)packet).getUsername()+" has connected...");
                PlayerMP player = new PlayerMP(game.level, 100, 100, ((Packet00Login)packet).getUsername(), address, port);

                this.addConnection(player, (Packet00Login)packet);
                break;
            case DISCONNECT:
                break;
        }
    }

    public void addConnection(PlayerMP player, Packet00Login packet)
    {
        boolean alreadyConnected = false;
        for(PlayerMP p : this.connectedPlayers)
        {
            if(player.getUsername().equalsIgnoreCase(p.getUsername()))
            {
                if(p.ipAddress == null)
                {
                    p.ipAddress = player.ipAddress;
                }

                if(p.port == -1)
                {
                    p.port = player.port;
                }
                alreadyConnected = true;
            } else
            {
                sendData(packet.getData(), p.ipAddress, p.port);
                packet = new Packet00Login(p.getUsername());
                sendData(packet.getData(), player.ipAddress, player.port);
            }

            if(!alreadyConnected)
            {
                this.connectedPlayers.add(player);
            }
        }
    }

    public void sendData(byte[] data, InetAddress ipAddress, int port)
    {
        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
        try {
            this.socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendDataToAllClients(byte[] data)
    {
        for(PlayerMP p : connectedPlayers)
        {
            sendData(data, p.ipAddress, p.port);
        }
    }
}
