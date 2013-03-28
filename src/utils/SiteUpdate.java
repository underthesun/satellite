/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import communication.CommunicationServer;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimerTask;

/**
 *
 * @author shuai
 */
public class SiteUpdate extends TimerTask {

    CommunicationServer communicationServer;
    int count = 0;

    public SiteUpdate(CommunicationServer communicationServer) {
        this.communicationServer = communicationServer;
    }

    @Override
    public void run() {
//        System.out.println("checking..." + count++);
        ArrayList<Site> sites = communicationServer.getSites();

        if (!sites.isEmpty()) {
            long t = new Date().getTime();
            for (int i = 0; i < sites.size(); i++) {
                Site site = sites.get(i);
                if ((t - site.getLastTime()) / 1000 > 5) {
                    communicationServer.siteOffLine(site);
                }
            }
        }
    }
}
