package cn.ieclipse.aorm.as;

import com.intellij.util.ui.table.ComboBoxTableCellEditor;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Jamling on 2017/7/20.
 */
public class FieldsTypeCellEditor extends ComboBoxTableCellEditor {
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        final JComboBox comboBox = (JComboBox) this.getComponent();
        comboBox.setEditable(true);
        comboBox.setModel(new ListComboBoxModel(AormConstants.dbTypes));
        // String old = (String) value;
        comboBox.setSelectedItem(value);
        return comboBox;
    }
}
