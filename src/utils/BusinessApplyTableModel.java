/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.table.AbstractTableModel;
import satellite.ContentPanel;

/**
 *
 * @author shuai
 */
public class BusinessApplyTableModel extends AbstractTableModel {

    ContentPanel contentPanel;
    private ArrayList<Object[]> data;
    private static int sequenceNumber = 0;
    private String[] columnNames = {"序号", "主叫站号", "被叫站号", "申请业务状态", "所需信道资源", "主叫优先级", "批准", "驳回"};
    
    private void loadTestData() {
        data.add(new Object[]{"1", "1", "2", "s", "d", "f", "批准", "驳回", "1"});
        data.add(new Object[]{"2", "1", "2", "s", "d", "f", "批准", "驳回", "2"});
        data.add(new Object[]{"3", "1", "2", "s", "d", "f", "批准", "驳回", "3"});
        data.add(new Object[]{"4", "1", "2", "s", "d", "f", "批准", "驳回", "4"});
    }
    
    public BusinessApplyTableModel() {
        data = new ArrayList<Object[]>();
//        loadTestData();
    }

    public BusinessApplyTableModel(ContentPanel cntPanel) {
        contentPanel = cntPanel;
        data = new ArrayList<Object[]>();
//        loadTestData();
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

//    @Override
//    public void setValueAt(Object value, int rowIndex, int columnIndex){
//        data.get(rowIndex)[columnIndex] = value;
//    }
    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }
    
    @Override
    public Class getColumnClass(int coulmnIndex) {
        return getValueAt(0, coulmnIndex).getClass();
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex < columnNames.length - 2) {
            return false;
        } else {
            return true;
        }
    }
    
    public Object[] getRecord(int rowIndex) {
        return data.get(rowIndex);
    }
    
    public int addRecord(Object[] obs) {
        ArrayList<Object> temp = new ArrayList<Object>();
        temp.add(++sequenceNumber);
        for (int i = 0; i < obs.length - 2; ++i) {
//            System.out.println(obs[i].toString());
            temp.add(obs[i]);
        }
        temp.add(obs[obs.length - 2].toString() + "kbps");//格式化带宽
        temp.add(contentPanel.getConstants().getPrivileges().get(obs[0].toString()));//privilege
        temp.add("批准");
        temp.add("驳回");
        temp.add(obs[obs.length - 1]);//站点请求序号
        data.add(temp.toArray());
        this.fireTableDataChanged();
        return data.size() - 1;
    }
    
    public void removeRecord(String id) {
//        for (int i = 0; i < data.size(); i++) {
//            if (data.get(i)[2].equals(id)) {
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
    
    public void removeRecord(int rowIndex) {
        data.remove(rowIndex);
        this.fireTableDataChanged();
    }
}
