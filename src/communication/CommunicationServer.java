/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package communication;

import java.awt.Color;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import satellite.ContentPanel;
import utils.Constants;
import utils.Site;
import utils.SiteUpdate;

;

/**
 *
 * @author shuai
 */
public class CommunicationServer {

    private ContentPanel contentPanel;
//    private int portLogin;
//    private int portMessage;
    private ArrayList<Site> sites;
//    private LoginServer loginServer;
//    private MessageServerBack messageServer;
    private SiteUpdate siteUpdate;
    private int localPort;
    private int remotePort;
    private String remoteIP;
    private int bizBoardPort;
    private String bizBoardIP;
    private HashMap<String, Integer> snrs;
    private DatagramSocket socket = null;
    private Constants constants;
    private MessageServer messageServer;

    public CommunicationServer(ContentPanel contentPanel) {
        this.contentPanel = contentPanel;
        loadConstants();
        this.sites = new ArrayList<Site>();
//        portLogin = contentPanel.getConstants().getLoginPort();
//        portMessage = contentPanel.getConstants().getMessagePort();
//        this.loginServer = new LoginServer(this, portLogin);
//        this.messageServer = new MessageServerBack(this, portMessage);

//        new Thread(loginServer).start();
//        new Thread(messageServer).start();

        this.siteUpdate = new SiteUpdate(this);
        new Timer().schedule(siteUpdate, 0, 1000);
        try {
            socket = new DatagramSocket(localPort);
        } catch (SocketException ex) {
            Logger.getLogger(CommunicationServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.messageServer = new MessageServer(socket, this);
        new Thread(messageServer).start();

    }

    private void loadConstants() {
        constants = contentPanel.getConstants();
        localPort = constants.getLocalPort();
        remotePort = constants.getRemotePort();
        remoteIP = constants.getRemoteIP();
        bizBoardIP = constants.getBizBoardIP();
        bizBoardPort = constants.getBizBoardPort();
        snrs = constants.getSnrs();
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

    public ArrayList<Site> getSites() {
        return sites;
    }

    public void removeSite(Site site) {
        for (int i = 0; i < sites.size(); i++) {
            if (sites.get(i).getId().equals(site.getId())) {
                sites.remove(i);
                break;
            }
        }
    }

    public void setSiteColor(Site s, Color c) {
        String id = s.getId();
        contentPanel.getSiteLabels().get(Integer.parseInt(id) - 1).setBackground(c);
    }

    public void addSiteCheckBox(Site s) {
        String id = s.getId();
        JCheckBox jcb = new JCheckBox("站点" + id);
        contentPanel.getSiteCheckBoxs().add(jcb);
        contentPanel.getSiteConnectedPanel().add(jcb);
        contentPanel.getSiteConnectedPanel().validate();
    }

    public void removeSiteCheckBox(Site s) {
        String id = s.getId();
        ArrayList<JCheckBox> checkBoxes = contentPanel.getSiteCheckBoxs();
        int siteIndex = findSiteCheckBox(s);

        if (siteIndex != -1) {
            contentPanel.getSiteConnectedPanel().remove(checkBoxes.get(siteIndex));
            contentPanel.repaint();
            contentPanel.getSiteConnectedPanel().revalidate();
            checkBoxes.remove(siteIndex);
        }
    }

    public Site findSite(int rowIndex) {
        return sites.get(rowIndex);
    }

    public Site findSite(String id) {
        for (Site site : sites) {
            if (site.getId().equals(id)) {
                return site;
            }
        }
        return null;
    }

    public int findSiteCheckBox(Site s) {
        String id = s.getId();
//        System.out.println("id: " + id);
        ArrayList<JCheckBox> checkBoxes = contentPanel.getSiteCheckBoxs();
        for (int i = 0; i < checkBoxes.size(); ++i) {
            if (checkBoxes.get(i).getText().equals("站点" + id)) {
                return i;
            }
        }
        return -1;
    }

    public JCheckBox findSiteCheckBox(String id) {
        for (JCheckBox cb : contentPanel.getSiteCheckBoxs()) {
            if (cb.getText().equals("站点" + id)) {
                return cb;
            }
        }
        return null;
    }

    public void parseMessage(String data, InetAddress addr, int port) {
        int indexOfColon = data.indexOf(":");
        String command = data.substring(0, indexOfColon);
        if (command.equals("connect") || command.equals("keep")) {
            parseLoginData(data, addr, port);
        } else if (command.equals("message") || command.equals("request") || command.equals("snr")) {
            parseMessageData(data, addr, port);
        }
    }

    public void parseLoginData(String data, InetAddress addr, int port) {
        int indexOfColon = data.indexOf(":");
        String command = data.substring(0, indexOfColon);

        if (command.equals("connect")) {
            String id = data.substring(indexOfColon + 1);
            Site site = findSite(id);
            if (site == null) {
                Site s = new Site(id, addr, port);
                s.setSnr(0);//设置信噪比
                sites.add(s);
                setSiteColor(s, Color.green);
                addSiteCheckBox(s);
            } else {
                site.setLastTime(new Date().getTime());
            }
//            System.out.println("addr:"+addr + " prot: " + port);
            loginConfirm(addr, port);
        } else if (command.equals("keep")) {
            String str = data.substring(indexOfColon + 1);
            String[] ss = str.split(":");
            String id = ss[0];
            String count = ss[1];
            Site site = findSite(id);
            if (site != null) {
                site.setLastTime(new Date().getTime());
            } else {
                Site s = new Site(id, addr, port);
                sites.add(s);
                s.setSnr(0);//设置信噪比
                setSiteColor(s, Color.green);
                addSiteCheckBox(s);
            }
            keepConfirm(addr, port, count);
        }
    }

    public void parseMessageData(String data, InetAddress addr, int port) {
        int indexOfColon = data.indexOf(":");
        String command = data.substring(0, indexOfColon);
        String str = data.substring(indexOfColon + 1);

        indexOfColon = str.indexOf(":");
        String id = str.substring(0, indexOfColon);

        if (command.equals("message")) {
            String message = str.substring(indexOfColon + 1);
            contentPanel.getMessageRecordTextArea().append("[站点" + id + "]: " + message + "\n");
        } else if (command.equals("request")) {
//            System.out.println("request:"+data);
            String[] items = str.split(":");
            contentPanel.getApplyModel().addRecord(items);
        } else if (command.equals("snr")) {
            //信噪比
            String s = str.substring(indexOfColon + 1);
            String[] ss = s.split(":");
            if (ss[0].equals("1")) {//查询
                String idToQuery = ss[1];
                Site site = findSite(id);
                Integer snr = snrs.get(idToQuery);                
//                System.out.println(data);
                echoSNR(site.getAddr(), site.getPort(), id, idToQuery, snr);
            } else if (ss[0].equals("0")) {//上报
                String snr = ss[1];
                snrs.put(id, new Integer(snr));
            }
        }
    }

    public void echoSNR(InetAddress addr, int port, String id, String idToQuery, Integer snr) {
        String snrEcho = "snr:" + id + ":" + idToQuery + ":" + snr.toString();
        messageServer.sendMessage(addr, port, snrEcho);
    }

    public void loginConfirm(InetAddress addr, int port) {
        String confirmMessage = "connect:ok";
        messageServer.sendMessage(addr, port, confirmMessage);
//        sendBuffer = confirmMessage.getBytes();
//        try {
//            sendPacket.setAddress(addr);
//            sendPacket.setPort(port);
//            sendPacket.setData(sendBuffer, 0, sendBuffer.length);
//            clientSocket.send(sendPacket);
//        } catch (IOException ex) {
//            Logger.getLogger(LoginServer.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    public void keepConfirm(InetAddress addr, int port, String count) {
        int c = Integer.parseInt(count) + 1;
        String confirmMessage = "keep:" + c;
        messageServer.sendMessage(addr, port, confirmMessage);
//        sendBuffer = confirmMessage.getBytes();
//        try {
//            sendPacket.setAddress(addr);
//            sendPacket.setPort(port);
//            sendPacket.setData(sendBuffer, 0, sendBuffer.length);
//            clientSocket.send(sendPacket);
//        } catch (IOException ex) {
//            Logger.getLogger(LoginServer.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    public void sendMessage(String message) {
        Pattern p = Pattern.compile("[^0-9]");
        Matcher m;
        Site site;

        for (JCheckBox cb : contentPanel.getSiteCheckBoxs()) {
            if (cb.isSelected()) {
                String str = cb.getText();
                m = p.matcher(str);
                String id = m.replaceAll("");
                site = findSite(id);
//                System.out.println(site.toString());
                String data = "message:" + message;
                messageServer.sendMessage(site.getAddr(), site.getPort(), data);
            }
        }
    }

    public void sendBusinessMessage(int rowIndex, int type, String conf) {
        String id = null;
        String requestId = null;
        String bm = "";
        if (type == 0 || type == 1) {
            id = contentPanel.getApplyModel().getValueAt(rowIndex, 1).toString();
            requestId = contentPanel.getApplyModel().getValueAt(rowIndex, 8).toString();
            if (type == 0) {
                bm = "request:" + id + ":" + type + ":" + requestId + ":" + conf;
            } else {
                bm = "request:" + id + ":" + type + ":" + requestId;
            }
        }
        if (type == 2 || type == 3) {
            id = contentPanel.getRunningModel().getValueAt(rowIndex, 1).toString();
            requestId = contentPanel.getRunningModel().getValueAt(rowIndex, 9).toString();
            bm = "request:" + id + ":" + type + ":" + requestId;
        }
        Site site = findSite(id);
        if (site != null) {
            messageServer.sendMessage(site.getAddr(), site.getPort(), bm);
        }
    }

    public void siteOffLine(Site site) {
        String id = site.getId();
        contentPanel.getApplyModel().removeRecord(id);
        contentPanel.getRunningModel().removeRecord(id);
        setSiteColor(site, Color.red);
        removeSiteCheckBox(site);
        removeSite(site);
    }
}
