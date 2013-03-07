/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package communication;

import java.awt.Color;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
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
    private HashMap<String, String> snrs;
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
//删除离线站点

    public void removeSite(Site site) {
        for (int i = 0; i < sites.size(); i++) {
            if (sites.get(i).getId().equals(site.getId())) {
                sites.remove(i);
                break;
            }
        }
    }
//通过颜色设置标志站点运行情况

    public void setSiteColor(Site s, Color c) {
        String id = s.getId();
        contentPanel.getSiteLabels().get(Integer.parseInt(id) - 1).setBackground(c);
    }
//增加站点检查索引

    public void addSiteCheckBox(Site s) {
        String id = s.getId();
        JCheckBox jcb = new JCheckBox("站点" + id);
        contentPanel.getSiteCheckBoxs().add(jcb);
        contentPanel.getSiteConnectedPanel().add(jcb);
        contentPanel.getSiteConnectedPanel().validate();
    }
//删除站点查询索引

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
//通过索引号查找站点

    public Site findSite(int rowIndex) {
        return sites.get(rowIndex);
    }
//通过字符串查找站点

    public Site findSite(String id) {
        for (Site site : sites) {
            if (site.getId().equals(id)) {
                return site;
            }
        }
        return null;
    }
//获取站点索引号

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
//获取站点id对应的jcheckbox对象

    public JCheckBox findSiteCheckBox(String id) {
        for (JCheckBox cb : contentPanel.getSiteCheckBoxs()) {
            if (cb.getText().equals("站点" + id)) {
                return cb;
            }
        }
        return null;
    }
//解析消息

    public void parseMessage(String data, InetAddress addr, int port) {
        int indexOfColon = data.indexOf(":");
        String command = data.substring(0, indexOfColon);
        if (command.equals("connect") || command.equals("keep")) {
            parseLoginData(data, addr, port);//连接保持请求
        } else if (command.equals("message") || command.equals("request") || command.equals("snr")) {
            parseMessageData(data, addr, port);//业务处理请求
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
                s.setIsAvailable(true);
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
                s.setIsAvailable(true);
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
            String idCalling = items[0];
            String type = items[1];
            if (type.equals("0")) {//将业务连接申请情况发送到被呼叫端
                String serial = items[10];
                String idCalled = items[2];
                Site siteCalling = findSite(idCalling);
                Site siteCalled = findSite(idCalled);
                if (null != findSite(idCalled) && siteCalling.getIsAvailable() && siteCalled.getIsAvailable()) {
                    siteCalling.setIsAvailable(false);
                    siteCalled.setIsAvailable(false);
                    String newdata = "request:" + items[0] + ":5";
                    for (int i = 2; i < items.length; i++) {
                        newdata += ":" + items[i];
                    }
//                    System.out.println("pc:" + s.getId() + s.getAddr() + ":" + ":" + s.getPort());
                    messageServer.sendMessage(siteCalled.getAddr(), siteCalled.getPort(), newdata);
                    contentPanel.getApplyModel().addRecord(items);
                } else {
                    sendRefuseMessage(id, 4, serial);
                }
            } else if (type.equals("1")) {//request cancel
//                contentPanel.bizCancelling(idCalling);
                bizCancelling(idCalling);
            }
        } else if (command.equals("snr")) {
            //信噪比
            String[] ss = str.split(":");
            if (ss[1].equals("1")) {//查询
                String idQueryed = ss[2];
                Site site = findSite(id);
                String snr = snrs.get(idQueryed);
                if (!snr.equals("")) {
                    System.out.println("snr: " + snr);
                    echoSNR(site.getAddr(), site.getPort(), id, idQueryed, snr);
                }
            } else if (ss[1].equals("0")) {//上报
                String snr = ss[2];
                snrs.put(id, snr);
                contentPanel.updateSnr(id, snr);
            }
        }
    }

    public void echoSNR(InetAddress addr, int port, String id, String idQueryed, String snr) {
        String snrEcho = "snr:" + id + ":" + idQueryed + ":" + snr;
        messageServer.sendMessage(addr, port, snrEcho);
    }

    public void loginConfirm(InetAddress addr, int port) {
        String confirmMessage = "connect:ok";
        messageServer.sendMessage(addr, port, confirmMessage);
    }

    public void keepConfirm(InetAddress addr, int port, String count) {
        int c = Integer.parseInt(count) + 1;
        String confirmMessage = "keep:" + c;
        messageServer.sendMessage(addr, port, confirmMessage);
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
//                System.out.println(siteCalling.toString());
                String data = "message:" + message;
                messageServer.sendMessage(site.getAddr(), site.getPort(), data);
            }
        }
    }

    public void sendRefuseMessage(String id, int type, String serial) {
        String rm = "";
        if (type == 4) {
            rm += "request" + ":" + id + ":" + type + ":" + serial;
        }
        Site site = findSite(id);
        if (site != null) {
            messageServer.sendMessage(site.getAddr(), site.getPort(), rm);
        }
    }

    public void sendCancelPermission(String idCalling, String idCalled) {
        String cp = "request:" + idCalling + ":6";
        Site site = findSite(idCalling);
        if (site != null) {
            messageServer.sendMessage(site.getAddr(), site.getPort(), cp);
        }
        cp = "request:" + idCalled + ":6";
        site = findSite(idCalled);
        if (site != null) {
            messageServer.sendMessage(site.getAddr(), site.getPort(), cp);
        }
    }

    public void sendBusinessMessage(int rowIndex, int type, String conf) {
        String id = null;
        String idCalled = null;
//        String requestId = null;

        String bm = "";
        String bm2 = "";
        if (type == 0 || type == 1) {
            id = contentPanel.getApplyModel().getValueAt(rowIndex, 1).toString();
            idCalled = contentPanel.getApplyModel().getValueAt(rowIndex, 2).toString();
            //requestId = contentPanel.getApplyModel().getValueAt(rowIndex, 8).toString();
            if (type == 0) {
                bm = "request:" + id + ":" + type + ":" + conf;
                bm2 = "request:" + idCalled + ":" + type + ":" + conf;;
            } else {
                bm = "request:" + ":" + id + ":" + type;
                bm2 = "request:" + ":" + idCalled + ":" + type;;
            }
        }
        if (type == 2 || type == 3) {
            id = contentPanel.getRunningModel().getValueAt(rowIndex, 1).toString();
            idCalled = contentPanel.getRunningModel().getValueAt(rowIndex, 2).toString();
//            requestId = contentPanel.getRunningModel().getValueAt(rowIndex, 8).toString();
            bm = "request:" + id + ":" + type;
            bm2 = "request:" + idCalled + ":" + type;
        }
        Site site = findSite(id);
        if (site != null) {
            messageServer.sendMessage(site.getAddr(), site.getPort(), bm);
            if (type == 1 || type == 3) {
                site.setIsAvailable(true);
            }
        }
        site = findSite(idCalled);
        if (site != null) {
            messageServer.sendMessage(site.getAddr(), site.getPort(), bm2);
            if (type == 1 || type == 3) {
                site.setIsAvailable(true);
            }
        }
    }

    public void siteOffLine(Site site) {
        site.setIsAvailable(false);
        String id = site.getId();
        Object idMatched = contentPanel.getRunningModel().getRelativeSite(id);
        if (idMatched != null) {
            Site siteMatched = findSite(idMatched.toString());
            siteMatched.setIsAvailable(true);
            String req = "request:" + idMatched.toString() + ":6";
            messageServer.sendMessage(siteMatched.getAddr(), siteMatched.getPort(), req);
        }
        contentPanel.getApplyModel().removeRecord(id);
        contentPanel.getRunningModel().removeRecord(id);
        setSiteColor(site, Color.red);
        removeSiteCheckBox(site);
        removeSite(site);
    }

    public void bizCancelling(String idCalling) {
        final String id = idCalling;
        Site siteCalling = findSite(id);
        if (siteCalling != null) {
            siteCalling.setIsAvailable(true);
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(null, "站点" + id + "申请取消业务", "提醒", JOptionPane.OK_CANCEL_OPTION)) {
                    Object idCalled = contentPanel.getRunningModel().getRelativeSite(id);
                    if (idCalled != null) {
                        Site siteCalled = findSite(idCalled.toString());
                        if (siteCalled != null) {
                            siteCalled.setIsAvailable(true);
                        }
                        sendCancelPermission(id, idCalled.toString());
                        contentPanel.getRunningModel().removeRecord(id, idCalled.toString());
                    }
                    idCalled = contentPanel.getApplyModel().getRelativeSite(id);
                    if (idCalled != null) {
                        Site siteCalled = findSite(idCalled.toString());
                        if (siteCalled != null) {
                            siteCalled.setIsAvailable(true);
                        }
                        sendCancelPermission(id, idCalled.toString());
                        contentPanel.getApplyModel().removeRecord(id, idCalled.toString());
                    }
                }
            }
        });
    }
}
