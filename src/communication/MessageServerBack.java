/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author shuai
 */
public class MessageServerBack implements Runnable {

    private CommunicationServer communicationServer;
    private int serverPort;
    private DatagramSocket serverSocket = null;
    private DatagramPacket recvPacket = null;
    private byte[] recvBuffer = null;
    private int bufferSize = 1024;

    public MessageServerBack(CommunicationServer communicationServer, int port) {
        this.communicationServer = communicationServer;
        this.serverPort = port;
        try {
            this.serverSocket = new DatagramSocket(serverPort);
        } catch (SocketException ex) {
            Logger.getLogger(MessageServerBack.class.getName()).log(Level.SEVERE, null, ex);
        }
        recvBuffer = new byte[bufferSize];
        recvPacket = new DatagramPacket(recvBuffer, bufferSize);
    }

    public void sendMessage(InetAddress addr, int port, String message) {
        byte[] data = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, addr, port);
        try {
            serverSocket.send(sendPacket);
        } catch (IOException ex) {
            Logger.getLogger(MessageServerBack.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                serverSocket.receive(recvPacket);
                InetAddress addr = recvPacket.getAddress();
                int port = recvPacket.getPort();
                String data = new String(recvBuffer, 0, recvPacket.getLength());
//                System.out.println("received: "+data);
                communicationServer.parseMessageData(data, addr, port);
            } catch (IOException ex) {
                Logger.getLogger(MessageServerBack.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
