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
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author shuai
 */
public class LoginServer implements Runnable {

    private CommunicationServer communicationServer;
    private int serverPort;
    private DatagramSocket serverSocket = null;
    private DatagramSocket clientSocket = null;
    private DatagramPacket recvPacket = null;
    private DatagramPacket sendPacket = null;
    private byte[] recvBuffer = null;
    private byte[] sendBuffer = null;
    private int bufferSize = 1024;

    public LoginServer(CommunicationServer communicationServer, int port) {
        this.communicationServer = communicationServer;
        this.serverPort = port;
        try {
            serverSocket = new DatagramSocket(port);
            clientSocket = new DatagramSocket();
        } catch (SocketException ex) {
            Logger.getLogger(LoginServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        recvBuffer = new byte[bufferSize];
        recvPacket = new DatagramPacket(recvBuffer, bufferSize);
        sendBuffer = new byte[bufferSize];
        sendPacket = new DatagramPacket(sendBuffer, bufferSize);

    }

    public void run() {
        while (true) {
            try {
                serverSocket.receive(recvPacket);
                InetAddress addr = recvPacket.getAddress();
                int port = recvPacket.getPort();
                String data = new String(recvBuffer, 0, recvPacket.getLength());
                communicationServer.parseLoginData(data, addr, port);
            } catch (IOException ex) {
                Logger.getLogger(LoginServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void loginConfirm(InetAddress addr, int port) {
        String confirmMessage = "connect:ok";
        sendBuffer = confirmMessage.getBytes();
        try {
            sendPacket.setAddress(addr);
            sendPacket.setPort(port);
            sendPacket.setData(sendBuffer, 0, sendBuffer.length);
            clientSocket.send(sendPacket);
        } catch (IOException ex) {
            Logger.getLogger(LoginServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void keepConfirm(InetAddress addr, int port, String count) {
        int c = Integer.parseInt(count) + 1;
        String confirmMessage = "keep:" + c;
        sendBuffer = confirmMessage.getBytes();
        try {
            sendPacket.setAddress(addr);
            sendPacket.setPort(port);
            sendPacket.setData(sendBuffer, 0, sendBuffer.length);
            clientSocket.send(sendPacket);
        } catch (IOException ex) {
            Logger.getLogger(LoginServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
