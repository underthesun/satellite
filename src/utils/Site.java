/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;

import java.net.InetAddress;
import java.util.Date;

/**
 *    
 * @author shuai
 */
public class Site {
    private String id;
    private int port;
    private InetAddress addr;
    private int snr;
    private long lastTime;
    private boolean isConnected = false;
    private boolean isBusy = false;
    
    public Site(String id, InetAddress addr, int port){
        this.id = id;
        this.port = port;
        this.addr = addr;
        this.lastTime = new Date().getTime();
    }

    public int getSnr() {
        return snr;
    }

    public void setSnr(int snr) {
        this.snr = snr;
    }
    
    public String getId(){
        return id;
    }
    
    public InetAddress getAddr(){
        return addr;
    }
    
    public int getPort(){
        return port;
    }

    public boolean isIsConnected() {
        return isConnected;
    }

    public void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }
    

    public long getLastTime(){
        return lastTime;
    }
    
    public void setLastTime(long lastTime){
        this.lastTime = lastTime;
    }
    
    public String toString(){
        return "Site:"+id;
    }

    public boolean isIsBusy() {
        return isBusy;
    }

    public void setIsBusy(boolean isBusy) {
        this.isBusy = isBusy;
    }
    
}
