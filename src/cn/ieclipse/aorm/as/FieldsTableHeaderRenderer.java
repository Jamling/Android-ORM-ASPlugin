package cn.ieclipse.aorm.as;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Jamling on 2017/7/21.
 */
public class FieldsTableHeaderRenderer extends JCheckBox implements TableCellRenderer {
    FieldsTableModel model;

    public FieldsTableHeaderRenderer(JTable table) {
        setSelected(true);
        setHorizontalAlignment(CENTER);
        setOpaque(false);
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTableHeader header = (JTableHeader) e.getSource();
                JTable table = header.getTable();
                FieldsTableModel model = (FieldsTableModel) table.getModel();
                TableColumnModel columnModel = table.getColumnModel();
                int vci = columnModel.getColumnIndexAtX(e.getX());
                int mci = table.convertColumnIndexToModel(vci);
                if (mci == 0) {
                    setChecked(!isSelected());
                    if (isSelected()) {
                        model.selectAll();
                    } else {
                        model.selectNone();
                    }
                }
            }
        });
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSel, boolean hasFocus, int row, int column) {
        FieldsTableModel model = (FieldsTableModel) table.getModel();
        this.model = model;
        if (column > 0) {
            JLabel label = new JLabel(); //(JLabel) table.getTableHeader().getDefaultRenderer().getTableCellRendererComponent(table, null, isSel, hasFocus, row, column);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setText(model.getColumnName(column));
            return label;
        }

        setText(model.getColumnName(column));
        return this;
    }

    public void setChecked(boolean checked) {
        if (this.isSelected() != checked) {
            this.setSelected(checked);
            repaint();
        }
    }
}
