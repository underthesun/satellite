/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author shuai
 */
public class ButtonRenderer extends JButton implements TableCellRenderer {

    private final static String state_permitted = "批准";
    private final static String state_rejected = "驳回";
    private final static String state_warn = "警告";
    private final static String state_force = "强制拆链";
    private Border focusBorder;
    private Border originalBorder;

    public ButtonRenderer() {
        originalBorder = getBorder();
        focusBorder = new LineBorder(Color.blue);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(UIManager.getColor("Button.background"));
        }

        if (hasFocus) {
            setBorder(focusBorder);
        } else {
            setBorder(originalBorder);
        }
        String state = value.toString();
        setBackground(state);
        setText(state);
        return this;
    }

    private void setBackground(String state) {
        if (state.equals(state_force) || state.equals(state_rejected)) {
            setBackground(Color.red);
        }
        if (state.equals(state_permitted)) {
            setBackground(Color.green);
        }
        if(state.equals(state_warn)){
            setBackground(Color.yellow);
        }
    }
}
