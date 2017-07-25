package cn.ieclipse.aorm.as;

import com.intellij.ui.BooleanTableCellEditor;
import com.intellij.ui.BooleanTableCellRenderer;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jamling on 2017/7/21.
 */
public class ClassTableModel extends AbstractTableModel implements ICheckableTableModel {
    private List<ClassEntity> classEntityList;
    private List<String> columnNames = Arrays.asList("", "Table", "Class");
    //private List<Class<?>> columnClasses = Arrays.asList(String.class, String.class, String.class, Boolean.class, Boolean.class);
    private JTable table;
    public static final int COL_CHECK = 0;
    public static final int COL_TABLE = 1;
    public static final int COL_CLASS = 2;

    public ClassTableModel(List<ClassEntity> classEntityList) {
        this.classEntityList = classEntityList;
    }

    @Override
    public int getRowCount() {
        return classEntityList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }


    @Override
    public String getColumnName(int column) {
        return columnNames.get(column);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == COL_CHECK) {
            return Boolean.class;
        } else if (columnIndex == 3 || columnIndex == 4) {
            return Boolean.class;
        }
        return super.getColumnClass(columnIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ClassEntity fieldEntity = classEntityList.get(rowIndex);
        if (columnIndex == COL_CHECK) {
            return fieldEntity.getSelected();
        } else if (columnIndex == COL_TABLE) {
            return fieldEntity.getTable();
        } else if (columnIndex == COL_CLASS) {
            return fieldEntity.getPsiClass().getQualifiedName();
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        ClassEntity fieldEntity = classEntityList.get(rowIndex);
        if (columnIndex == COL_CHECK) {
            fieldEntity.setSelected((Boolean) aValue);
            int count = getSelectCount();
            CheckableTableHeaderRenderer headerRenderer = (CheckableTableHeaderRenderer) table.getTableHeader().getDefaultRenderer();
            if (count < getRowCount()) {
                headerRenderer.setChecked(false);
            } else {
                headerRenderer.setChecked(true);
            }
        }
    }

    public ClassEntity getRowObject(int row) {
        return classEntityList.get(row);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return true;
        }
        return super.isCellEditable(rowIndex, columnIndex);
    }

    public void setTableEditor(JTable table) {
        this.table = table;
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(COL_CHECK).setCellRenderer(new BooleanTableCellRenderer());
        columnModel.getColumn(COL_CHECK).setCellEditor(new BooleanTableCellEditor());
        columnModel.getColumn(COL_CHECK).setPreferredWidth(50);
        columnModel.getColumn(COL_TABLE).setPreferredWidth(100);

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new ClassTableHeaderRenderer(table, true));
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    public int getSelectCount() {
        int count = 0;
        for (ClassEntity entity : classEntityList) {
            if (entity.getSelected()) {
                count++;
            }
        }
        return count;
    }

    public void selectAll() {
        for (ClassEntity entity : classEntityList) {
            entity.setSelected(true);
        }
        table.updateUI();
    }

    public void selectNone() {
        for (ClassEntity entity : classEntityList) {
            entity.setSelected(false);
        }
        table.updateUI();
    }

    @Override
    public void selectInverse() {

    }
}
