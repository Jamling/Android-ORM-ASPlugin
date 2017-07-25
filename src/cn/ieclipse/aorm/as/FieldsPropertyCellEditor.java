package cn.ieclipse.aorm.as;

import com.intellij.ui.BooleanTableCellEditor;
import com.intellij.ui.BooleanTableCellRenderer;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Jamling on 2017/7/21.
 */
public class FieldsPropertyCellEditor extends BooleanTableCellEditor {

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        JCheckBox component = (JCheckBox) super.getTableCellEditorComponent(table, value, isSelected, row, column);
        FieldsTableModel model = (FieldsTableModel) table.getModel();
        component.setText(model.getRowObject(row).getFieldName());
        component.setHorizontalAlignment(JCheckBox.LEADING);
        return component;
    }
}
