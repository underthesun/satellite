/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package communication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
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
//        System.out.println("send: " + addr + " port: " +port);
        try {
            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            DataOutputStream dataStream = new DataOutputStream(ostream);
            dataStream.writeUTF(message);
            dataStream.close();
            byte[] data = ostream.toByteArray();
            sendPacket = new DatagramPacket(data, data.length, addr, port);
            serverSocket.send(sendPacket);
        } catch (IOException ex) {
            Logger.getLogger(MessageServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        while (true) {
            try {
                serverSocket.receive(recvPacket);
//                System.out.println("Received: "+data);
                InetAddress addr = recvPacket.getAddress();
                int port = recvPacket.getPort();
                DataInputStream istream = new DataInputStream(new ByteArrayInputStream(recvPacket.getData()));
                String data = istream.readUTF();
//                System.out.println("recved: "+addr +"/"+port);
                comm.parseMessage(data, addr, port);
            } catch (IOException ex) {
                Logger.getLogger(MessageServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
