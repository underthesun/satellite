/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.swing.table.AbstractTableModel;
import satellite.ContentPanel;

/**
 *
 * @author shuai
 */
public class BusinessRunningTableModel extends AbstractTableModel {

    ContentPanel contentPanel;
    private ArrayList<Object[]> data;
    private String[] columnNames = {"序号", "主叫站号", "被叫站号", "信道1信噪比", "信道2信噪比", "申请业务状态", "所需信道资源", "主叫优先级", "业务效率", "警告", "强制拆链"};

    private void loadTestData() {
        data.add(new Object[]{"1", "1", "2", "s", "d", "f", "100%", "警告", "强制拆链", "001"});
        data.add(new Object[]{"2", "1", "2", "s", "d", "f", "100%", "警告", "强制拆链", "2"});
        data.add(new Object[]{"3", "1", "2", "s", "d", "f", "100%", "警告", "强制拆链", "3"});
        data.add(new Object[]{"4", "1", "2", "s", "d", "f", "100%", "警告", "强制拆链", "4"});
    }

    public BusinessRunningTableModel() {
        data = new ArrayList<Object[]>();
//        loadTestData();
//        System.out.println(getValueAt(0, columnNames.length));
    }

    public BusinessRunningTableModel(ContentPanel cntPanel) {
        contentPanel = cntPanel;
        data = new ArrayList<Object[]>();
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data.get(rowIndex)[columnIndex];
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public Class getColumnClass(int coulmnIndex) {
        return getValueAt(0, coulmnIndex).getClass();
    }

//    @Override
//    public void setValueAt(Object value, int rowIndex, int columnIndex) {
//        data.get(rowIndex)[columnIndex] = value;
//        fireTableDataChanged();
//    }
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex < columnNames.length - 2) {
            return false;
        } else {
            return true;
        }
    }

    public void addRecord(Object[] obs) {
        Object serial = obs[0];
        Object idCalling = obs[1];
        Object idCalled = obs[2];
        Object bizStatus = obs[3];
        Object resources = obs[4];
        Object privilege = obs[5];  
        Object serialCalling = obs[6];
        String snr1 = contentPanel.getConstants().getSnrs().get(idCalling.toString());
        String snr2 = contentPanel.getConstants().getSnrs().get(idCalled.toString());
//        ArrayList<Object> temp = new ArrayList<Object>();
//        temp.add(serial);
//        temp.add(idCalling);
//        temp.add(idCalled);
//        temp.add(snr1);
//        temp.add(snr2);
//        temp.add(serial);
//        
//        
        Random rdm = new Random();
//        temp.add(50 + rdm.nextInt(50) + "%");//业务效率
//        temp.add("警告");
//        temp.add("强制拆链");
//        temp.add(obs[obs.length - 1]);//站点请求序号
//        data.add(temp.toArray());
        Object efficiency = 50 + rdm.nextInt(50) + "%";
        Object[] temp = {serial, idCalling, idCalled, snr1, snr2,bizStatus,resources,privilege,efficiency,"警告", "强制拆链"};
        data.add(temp);
        this.fireTableDataChanged();
    }

    public void removeRecord(String id) {
//        for (int i=0; i<data.size(); i++){
//            if(data.get(i)[2].equals(id)){
//                data.remove(i);
//            }
//        }
        Iterator<Object[]> it = data.iterator();
        while (it.hasNext()) {
            Object[] obs = it.next();
            if (obs[1].equals(id)) {
                it.remove();
            }
        }
        fireTableDataChanged();
    }

    public void removeRecord(String idCalling, String idCalled) {
        Iterator<Object[]> it = data.iterator();
        while (it.hasNext()) {
            Object[] obs = it.next();
            if ((obs[1].equals(idCalling) && obs[2].equals(idCalled)) || (obs[2].equals(idCalling) && obs[1].equals(idCalled))) {
                it.remove();
//                System.out.println("remove one ");
            }
        }
        fireTableDataChanged();
    }

    public void removeRecord(int rowIndex) {
        data.remove(rowIndex);
        this.fireTableDataChanged();
    }

    public Object getRelativeSite(String idCalling) {
        for (Object[] obs : data) {
            if (idCalling.equals(obs[1])) {
                return obs[2];
            }
        }
        for (Object[] obs : data) {
            if (idCalling.equals(obs[2])) {
                return obs[1];
            }
        }
        return null;
    }

    public void updateSnr(String id, String snr) {
        for(Object[] obs:data){
            if(obs[1].equals(id)){
                obs[3] = snr;
            }
            if(obs[2].equals(id)){
                obs[4] = snr;
            }
        }
        this.fireTableDataChanged();
    }
}
