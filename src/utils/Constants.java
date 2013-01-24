/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.HashMap;

/**
 *
 * @author shuai
 */
public class Constants {

    private int siteNum;
    private HashMap<String, Integer> privileges;
    private HashMap<String, Integer> snrs;
    private int loginPort;
    private int messagePort;
    private String bizBoardIP;
    private int bizBoardPort;
    private String remoteIP;
    private int remotePort;
    private int localPort;

    public Constants() {
    }

    public int getSiteNum() {
        return siteNum;
    }

    public void setSiteNum(int siteNum) {
        this.siteNum = siteNum;
    }

    public HashMap<String, Integer> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(HashMap<String, Integer> privileges) {
        this.privileges = privileges;
    }

    public int getLoginPort() {
        return loginPort;
    }

    public void setLoginPort(int loginPort) {
        this.loginPort = loginPort;
    }

    public int getMessagePort() {
        return messagePort;
    }

    public int getLocalPort() {
        return localPort;
    }

    public void setLocalPort(int port) {
        this.localPort = port;
    }

    public void setMessagePort(int messagePort) {
        this.messagePort = messagePort;
    }

    public String getBizBoardIP() {
        return bizBoardIP;
    }

    public void setBizBoardIP(String bizBoardIP) {
        this.bizBoardIP = bizBoardIP;
    }

    public int getBizBoardPort() {
        return bizBoardPort;
    }

    public void setBizBoardPort(int bizBoardPort) {
        this.bizBoardPort = bizBoardPort;
    }

    public String getRemoteIP() {
        return remoteIP;
    }

    public void setRemoteIP(String remoteIP) {
        this.remoteIP = remoteIP;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public HashMap<String, Integer> getSnrs() {
        return snrs;
    }

    public void setSnrs(HashMap<String, Integer> snrs) {
        this.snrs = snrs;
    }
    
}
