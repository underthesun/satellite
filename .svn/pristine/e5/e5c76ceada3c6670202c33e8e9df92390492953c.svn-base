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
    private long lastTime;
    private boolean isConnected = false;
    
    public Site(String id, InetAddress addr, int port){
        this.id = id;
        this.port = port;
        this.addr = addr;
        this.lastTime = new Date().getTime();
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
    
    public void setState(boolean isConnected){
        this.isConnected = isConnected;
    }
    
    public boolean getState(){
        return isConnected;
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
}
