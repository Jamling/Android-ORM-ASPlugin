package cn.ieclipse.aorm.as;

import com.intellij.ui.BooleanTableCellRenderer;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Jamling on 2017/7/21.
 */
public class FieldsPropertyCellRenderer extends BooleanTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSel, boolean hasFocus, int row, int column) {
        JCheckBox checkBox = (JCheckBox) super.getTableCellRendererComponent(table, value, isSel, hasFocus, row, column);
        FieldsTableModel model = (FieldsTableModel) table.getModel();
        String name = model.getRowObject(row).getFieldName();
        checkBox.setText(name);
        checkBox.setHorizontalAlignment(JCheckBox.LEADING);
        return checkBox;
    }
}
