package cn.ieclipse.aorm.as;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Jamling on 2017/7/24.
 */
public class ClassTableHeaderRenderer extends CheckableTableHeaderRenderer {
    public ClassTableHeaderRenderer(JTable table, boolean checked) {
        super(table, checked);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSel, boolean hasFocus, int row, int column) {
        if (column > 0) {
            JLabel label = new JLabel(); //(JLabel) table.getTableHeader().getDefaultRenderer().getTableCellRendererComponent(table, null, isSel, hasFocus, row, column);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setText(getDisplayText(value));
            return label;
        }

        setText(getDisplayText(value));
        return this;
    }
}
