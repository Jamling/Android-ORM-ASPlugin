package cn.ieclipse.aorm.as;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jamling on 2017/7/18.
 */
public class FieldsTableModel extends AbstractTableModel {
    private ClassEntity classEntity;
    private List<String> columnNames = Arrays.asList("Property", "Filed", "Type", "Not Null", "ID");
    //private List<Class<?>> columnClasses = Arrays.asList(String.class, String.class, String.class, Boolean.class, Boolean.class);
    private JTable table;
    public static final int COL_PROPERTY = 0;
    public static final int COL_FIELD = 1;
    public static final int COL_TYPE = 2;
    public static final int COL_NOTNULL = 3;
    public static final int COL_ID = 4;

    public FieldsTableModel(ClassEntity entity) {
        this.classEntity = entity;
    }

    @Override
    public int getRowCount() {
        return classEntity.getFieldList().size();
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
        if (columnIndex == COL_PROPERTY) {
            return Boolean.class;
        } else if (columnIndex == 3 || columnIndex == 4) {
            return Boolean.class;
        }
        return super.getColumnClass(columnIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        FieldEntity fieldEntity = classEntity.getFieldList().get(rowIndex);
        if (columnIndex == COL_PROPERTY) {
            return fieldEntity.getSelected();
        } else if (columnIndex == COL_FIELD) {
            return fieldEntity.getDbName();
        } else if (columnIndex == COL_TYPE) {
            return fieldEntity.getDbType();
        } else if (columnIndex == COL_NOTNULL) {
            return fieldEntity.isNotNull();
        } else if (columnIndex == COL_ID) {
            return fieldEntity.isId();
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        FieldEntity fieldEntity = classEntity.getFieldList().get(rowIndex);
        if (columnIndex == COL_PROPERTY) {
            fieldEntity.setSelected((Boolean) aValue);
            int count = getSelectCount();
            FieldsTableHeaderRenderer headerRenderer = (FieldsTableHeaderRenderer) table.getTableHeader().getDefaultRenderer();
            if (count < getRowCount()) {
                headerRenderer.setChecked(false);
            } else {
                headerRenderer.setChecked(true);
            }
        } else if (columnIndex == COL_FIELD) {
            fieldEntity.setDbName((String) aValue);
        } else if (columnIndex == COL_TYPE) {
            fieldEntity.setDbType((String) aValue);
        } else if (columnIndex == COL_NOTNULL) {
            fieldEntity.setNotNull((Boolean) aValue);
        } else if (columnIndex == COL_ID) {
            fieldEntity.setId((Boolean) aValue);
        }
    }

    public FieldEntity getRowObject(int row) {
        return classEntity.getFieldList().get(row);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (autoType && columnIndex == COL_TYPE) {
            return false;
        }
        if (columnIndex >= 0) {
            return true;
        }
        return super.isCellEditable(rowIndex, columnIndex);
    }

    public void setTableEditor(JTable table) {
        this.table = table;
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(COL_PROPERTY).setCellRenderer(new FieldsPropertyCellRenderer());
        columnModel.getColumn(COL_PROPERTY).setCellEditor(new FieldsPropertyCellEditor());
        columnModel.getColumn(COL_TYPE).setCellEditor(new FieldsTypeCellEditor());
        columnModel.getColumn(COL_PROPERTY).setPreferredWidth(150);
        columnModel.getColumn(COL_FIELD).setPreferredWidth(150);
        columnModel.getColumn(COL_NOTNULL).setPreferredWidth(75);
        columnModel.getColumn(COL_NOTNULL).setPreferredWidth(50);
        columnModel.getColumn(COL_ID).setPreferredWidth(50);

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new FieldsTableHeaderRenderer(table));
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    public int getSelectCount() {
        int count = 0;
        for (FieldEntity entity : classEntity.getFieldList()) {
            if (entity.getSelected()) {
                count++;
            }
        }
        return count;
    }

    public void selectAll() {
        for (FieldEntity entity : classEntity.getFieldList()) {
            entity.setSelected(true);
        }
        table.updateUI();
    }

    public void selectNone() {
        for (FieldEntity entity : classEntity.getFieldList()) {
            entity.setSelected(false);
        }
        table.updateUI();
    }

    private boolean autoType = false;

    public void setAutoType(boolean auto) {
        this.autoType = auto;
    }
}
