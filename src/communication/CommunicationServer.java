/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package communication;

import utils.Site;
import java.awt.Color;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import satellite.ContentPanel;
import utils.SiteUpdate;

;

/**
 *
 * @author shuai
 */
public class CommunicationServer {

    private ContentPanel contentPanel;
    private int portLogin = 6666;
    private int portMessage = 6667;
    private ArrayList<Site> sites;
    private LoginServer loginServer;
    private MessageServer messageServer;
    private SiteUpdate siteUpdate;

    public CommunicationServer(ContentPanel contentPanel) {
        this.contentPanel = contentPanel;
        this.sites = new ArrayList<Site>();
        this.loginServer = new LoginServer(this, portLogin);
        this.messageServer = new MessageServer(this, portMessage);
        new Thread(loginServer).start();
        new Thread(messageServer).start();

        siteUpdate = new SiteUpdate(this);
        new Timer().schedule(siteUpdate, 0, 1000);
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
//            Object[] options = {"确定"};
//            JOptionPane jp = new JOptionPane("站点(" + id + ")掉线", JOptionPane.WARNING_MESSAGE, null, options );
//            JDialog jd = jp.createDialog(contentPanel, id);
//            jd.setModal(false);
//            jd.setVisible(true);

//            JOptionPane.showMessageDialog(contentPanel, "站点(" + id + ")掉线");
//            JOptionPane will block the whole process
//            JOptionPane.showMessageDialog(contentPanel, "站点(" + id + ")掉线", "掉线通知", JOptionPane.WARNING_MESSAGE);
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

    public void parseLoginData(String data, InetAddress addr, int port) {
        int indexOfColon = data.indexOf(":");
        String command = data.substring(0, indexOfColon);

        if (command.equals("connect")) {
            String id = data.substring(indexOfColon + 1);
            Site site = findSite(id);
            if (site == null) {
                Site s = new Site(id, addr, port);
                sites.add(s);
                setSiteColor(s, Color.green);
                addSiteCheckBox(s);
            } else {
                site.setLastTime(new Date().getTime());
            }
            loginServer.loginConfirm(addr, port);
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
                setSiteColor(s, Color.green);
                addSiteCheckBox(s);
            }
            loginServer.keepConfirm(addr, port, count);
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
            String[] items = str.split(":");
            contentPanel.getApplyModel().addRecord(items);
        }
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

    public void sendBusinessMessage(int rowIndex, int type) {
        String id = null;
        String requestId = null;
        if (type == 0 || type == 1) {
            id = contentPanel.getApplyModel().getValueAt(rowIndex, 1).toString();
            requestId = contentPanel.getApplyModel().getValueAt(rowIndex, 8).toString();
        }
        if (type == 2 || type == 3) {
            id = contentPanel.getRunningModel().getValueAt(rowIndex, 1).toString();
            requestId = contentPanel.getRunningModel().getValueAt(rowIndex, 9).toString();
        }

        String bm = "request:" + type + ":" + requestId;
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
