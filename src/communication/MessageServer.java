/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author shuai
 */
public class MessageServer implements Runnable {

    DatagramSocket serverSocket = null;
    DatagramSocket clientSocket = null;
    DatagramPacket sendPacket = null;
    DatagramPacket recvPacket = null;
    byte[] sendBuffer = null;
    byte[] recvBuffer = null;
    CommunicationServer comm;
    int bufLength = 1024;

    public MessageServer(DatagramSocket socket, CommunicationServer c) {
        this.serverSocket = socket;
        this.comm = c;

        sendBuffer = new byte[bufLength];
        recvBuffer = new byte[bufLength];
        recvPacket = new DatagramPacket(recvBuffer, bufLength);
        sendPacket = new DatagramPacket(sendBuffer, bufLength);
    }

    public void sendMessage(InetAddress addr, int port, String message) {
        byte[] data = message.getBytes();
        sendPacket = new DatagramPacket(data, data.length, addr, port);
        try {
            serverSocket.send(sendPacket);
        } catch (IOException ex) {
            Logger.getLogger(MessageServerBack.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        while (true) {
            try {
                serverSocket.receive(recvPacket);
                String data = new String(recvBuffer, 0, recvPacket.getLength());
                System.out.println("Received: "+data);
//                comm.parseMessage(data);
                InetAddress addr = recvPacket.getAddress();
                int port = recvPacket.getPort();
                comm.parseMessage(data, addr, port);
            } catch (IOException ex) {
                Logger.getLogger(MessageServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
