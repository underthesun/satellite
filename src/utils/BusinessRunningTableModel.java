/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author shuai
 */
public class BusinessRunningTableModel extends AbstractTableModel {

    private ArrayList<Object[]> data;
    private String[] columnNames = {"序号", "主叫站号", "被叫站号", "申请业务状态", "主叫优先级", "所需信道资源", "业务效率", "警告", "强制拆链"};

    private void loadTestData() {
        data.add(new Object[]{"1", "1", "2", "s", "d", "f", "5", "警告", "强制拆链", "001"});
        data.add(new Object[]{"2", "1", "2", "s", "d", "f", "5", "警告", "强制拆链", "2"});
        data.add(new Object[]{"3", "1", "2", "s", "d", "f", "5", "警告", "强制拆链", "3"});
        data.add(new Object[]{"4", "1", "2", "s", "d", "f", "5", "警告", "强制拆链", "4"});
    }

    public BusinessRunningTableModel() {
        data = new ArrayList<Object[]>();
//        loadTestData();
//        System.out.println(getValueAt(0, columnNames.length));
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
        ArrayList<Object> temp = new ArrayList<Object>();
        for (int i = 0; i < obs.length - 3; ++i) {
            temp.add(obs[i]);
        }
        Random rdm = new Random();
        temp.add(50 + rdm.nextInt(50) + "%");
        temp.add("警告");
        temp.add("强制拆链");
        temp.add(obs[obs.length - 1]);
        data.add(temp.toArray());
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

    public void removeRecord(int rowIndex) {
        data.remove(rowIndex);
        this.fireTableDataChanged();
    }
}
