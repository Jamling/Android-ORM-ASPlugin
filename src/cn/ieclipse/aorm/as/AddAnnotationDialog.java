package cn.ieclipse.aorm.as;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.table.JBTable;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class AddAnnotationDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox comboClass;
    private JBTextField tfTable;
    private JBScrollPane scrollPane;
    private com.intellij.ui.components.JBCheckBox chkHideType;
    private JBTextField tfPrefix;
    private JTextPane textPreview;

    private Project project;
    private List<VirtualFile> files;
    private ClassEntity classEntity;

    public AddAnnotationDialog(Project project, List<VirtualFile> files) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        this.project = project;
        this.files = files;
        init();
    }

    private void onOK() {
// add your code here
        addAnnotation();
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    private JBTable table;
    private List<ClassEntity> classList;

    private void init() {
        classList = new ArrayList<ClassEntity>(files.size());
        for (VirtualFile vf : files) {
            classList.add(Utils.getFrom(project, vf));
        }
        this.classEntity = classList.get(0);
        comboClass.setEditable(false);
        comboClass.setModel(new ListComboBoxModel(classList));
        comboClass.setSelectedIndex(0);
        comboClass.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                ClassEntity entity = (ClassEntity) value;
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Icon icon = Utils.getAnnotationIcon(entity);
                label.setIcon(icon);
                label.setText(entity.getClassName());
                return label;
            }
        });
        comboClass.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    classEntity = (ClassEntity) e.getItem();
                    FieldsTableModel model = new FieldsTableModel(classEntity);
                    model.setTable(table);
                    table.setModel(model);
                    table.updateUI();
                    tfTable.setText(classEntity.getTable());
                }
            }
        });

        if (this.classEntity != null) {
            tfTable.setText(classEntity.getTable());
            FieldsTableModel model = new FieldsTableModel(classEntity);
            table = new JBTable(model);
            model.setTableEditor(table);
            scrollPane.setViewportView(table);

            table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    updatePreview();
                }
            });

            tfTable.getDocument().addDocumentListener(new DocumentAdapter() {
                @Override
                protected void textChanged(DocumentEvent documentEvent) {
                    classEntity.setTable(tfTable.getText());
                }
            });
            tfPrefix.getDocument().addDocumentListener(new DocumentAdapter() {
                @Override
                protected void textChanged(DocumentEvent documentEvent) {
                    setPrefix(tfPrefix.getText());
                }
            });
        }

        chkHideType.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                setAutoType(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
    }

    private boolean autoType = false;

    private void setAutoType(boolean auto) {
        if (this.autoType != auto) {
            for (ClassEntity entity : classList) {
                entity.setAutoType(auto);
            }
            this.autoType = auto;
            FieldsTableModel model = (FieldsTableModel) table.getModel();
            model.setAutoType(auto);
            table.updateUI();
            updatePreview();
        }
    }

    private void setPrefix(String prefix) {
        for (ClassEntity entity : classList) {
            entity.setPrefix(prefix);
        }
        table.updateUI();
        updatePreview();
    }

    private void updatePreview() {
        int row = table.getSelectedRow();
        if (row < 0) {
            textPreview.setText("Please select field to preview");
            return;
        }
        FieldsTableModel model = (FieldsTableModel) table.getModel();
        FieldEntity fieldEntity = model.getRowObject(row);
        if (fieldEntity.getSelected()) {
            textPreview.setText(fieldEntity.getAnnotationText());
        } else {
            textPreview.setText("");
        }
    }

    private void addAnnotation() {
        WriteCommandAction.runWriteCommandAction(project, new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < classList.size(); i++) {
                    ClassEntity entity = classList.get(i);
                    VirtualFile vf = files.get(i);
                    entity.addAnnotation(project);
                    FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, vf), true);
                }
            }
        });
    }

    public static void main(String[] args) {
        AddAnnotationDialog dialog = new AddAnnotationDialog(null, null);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
