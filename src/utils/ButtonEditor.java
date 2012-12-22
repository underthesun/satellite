/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import communication.CommunicationServer;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellEditor;
import satellite.ContentPanel;

/**
 *
 * @author shuai
 */
public class ButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

    private final static String state_permit = "批准";
    private final static String state_reject = "驳回";
    private final static String state_warn = "警告";
    private final static String state_force = "强制拆链";
    private JButton button;
    private String state = null;
    private Border focusBorder;
    private ContentPanel contentPanel;
    private CommunicationServer communicationServer;

    public ButtonEditor(ContentPanel contentPanel, CommunicationServer communicationServer) {
        button = new JButton();
        button.setFocusPainted(false);
        button.addActionListener(this);
        focusBorder = new LineBorder(Color.blue);
        button.setBorder(focusBorder);
        this.contentPanel = contentPanel;
        this.communicationServer = communicationServer;
    }

    @Override
    public Object getCellEditorValue() {
        return state;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        state = value.toString();
        button.setText(state);
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object[] options = {"确定", "取消"};
        int rowIndex;
        if (state != null) {
            if (state.toString().equals(state_permit)) {
                if (JOptionPane.YES_OPTION == JOptionPane.showOptionDialog(null, "确定允许?", "确认", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1])) {
                    rowIndex = contentPanel.getTableApply().getSelectedRow();
                    if (rowIndex != -1) {
                        communicationServer.sendBusinessMessage(rowIndex, 0);
                        Object[] obs = contentPanel.getApplyModel().getRecord(rowIndex);
                        contentPanel.getApplyModel().removeRecord(rowIndex);
                        contentPanel.getRunningModel().addRecord(obs);
                    }
                }
            } else if (state.toString().equals(state_reject)) {
                if (JOptionPane.YES_OPTION == JOptionPane.showOptionDialog(null, "确定拒绝?", "确认", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1])) {
                    rowIndex = contentPanel.getTableApply().getSelectedRow();
                    if (rowIndex != -1) {
                        communicationServer.sendBusinessMessage(rowIndex, 1);
                        contentPanel.getApplyModel().removeRecord(rowIndex);
                    }
                }
            } else if (state.toString().equals(state_warn)) {
                if (JOptionPane.YES_OPTION == JOptionPane.showOptionDialog(null, "确定警告?", "确认", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1])) {
                    rowIndex = contentPanel.getTableRunning().getSelectedRow();
                    if (rowIndex != -1) {
                        communicationServer.sendBusinessMessage(rowIndex, 2);
                    }
                }
            } else if (state.toString().equals(state_force)) {
                if (JOptionPane.YES_OPTION == JOptionPane.showOptionDialog(null, "确定强制拆链?", "确认", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1])) {
                    rowIndex = contentPanel.getTableRunning().getSelectedRow();
                    if (rowIndex != -1) {
                        communicationServer.sendBusinessMessage(rowIndex, 3);
                        contentPanel.getRunningModel().removeRecord(rowIndex);
                    }
                }
            }
        }
    }
}
