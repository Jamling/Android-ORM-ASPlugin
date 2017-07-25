package cn.ieclipse.aorm.as;

import com.intellij.ui.BooleanTableCellRenderer;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Jamling on 2017/7/21.
 */
public class CheckableTableHeaderRenderer extends BooleanTableCellRenderer {
    ICheckableTableModel model;

    public CheckableTableHeaderRenderer(JTable table, boolean checked) {
        setSelected(checked);
        setOpaque(false);
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTableHeader header = (JTableHeader) e.getSource();
                JTable table = header.getTable();
                ICheckableTableModel model = (ICheckableTableModel) table.getModel();
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
        Component component = super.getTableCellRendererComponent(table, value, isSel, hasFocus, row, column);
        setText(getDisplayText(value));
        return component;
    }

    protected String getDisplayText(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public void setChecked(boolean checked) {
        if (this.isSelected() != checked) {
            this.setSelected(checked);
            repaint();
        }
    }
}
