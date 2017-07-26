package cn.ieclipse.aorm.as;

import com.intellij.designer.model.EmptyXmlTag;
import com.intellij.ide.util.TreeJavaClassChooserDialog;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageDialogBuilder;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlTag;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.table.JBTable;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomManager;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.model.impl.DomModelFactoryHelper;
import com.intellij.xml.util.XmlTagUtil;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class NewProviderDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField tfPkg;
    private JTextField tfName;
    private JCheckBox chkSuffix;
    private JTextField tfSuper;
    private JButton btnSuper;
    private JTextField tfAuth;
    private JTextField tfDb;
    private JScrollPane scrollPane;
    private JButton btnAdd;

    // data
    private Project project;
    private VirtualFile pkgDir;
    private List<VirtualFile> files;
    private JBTable table;
    private List<ClassEntity> classList;
    private ClassEntity classEntity;

    public NewProviderDialog() {
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
    }

    private void onOK() {
// add your code here
        generate();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public void init(final Project project, VirtualFile file, List<VirtualFile> files) {
        this.project = project;
        this.files = files;
        PsiJavaDirectoryImpl subDir = Utils.getJavaDir(project, file);
        tfPkg.setText(Utils.getJavaPkgName(subDir));

        if (file.isDirectory()) {
            pkgDir = file;
        } else {
            pkgDir = file.getParent();
        }
        tfSuper.setText(AormConstants.providerSuperQName);
        btnAdd.setIcon(Utils.getAddEditIcon(true));
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                com.intellij.ide.util.ClassFilter classFilter = new com.intellij.ide.util.ClassFilter() {

                    @Override
                    public boolean isAccepted(PsiClass psiClass) {
                        return Utils.findOrm(psiClass) != null;
                    }
                };
                TreeJavaClassChooserDialog dialog = TreeJavaClassChooserDialog.withInnerClasses("Choose ORM bean", project, GlobalSearchScope.projectScope(project), classFilter, null);
                dialog.setModal(true);
                dialog.show();
                {
                    PsiClass psiClass = dialog.getSelected();
                    if (psiClass != null) {
                        for (ClassEntity entity : classList) {
                            if (entity.getPsiClass().getQualifiedName().equals(psiClass.getQualifiedName())) {
                                com.intellij.openapi.ui.Messages.showInfoMessage("The class already exists", "");
                                return;
                            }
                        }
                        classList.add(new ClassEntity(psiClass));
                        table.updateUI();
                    }
                }
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                tfName.requestFocus();
                tfName.grabFocus();
            }
        });
        tfAuth.setText(AUTH);
        KeyListener inputVerifier = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                boolean empty = tfDb.getText().isEmpty() || tfName.getText().isEmpty();
                buttonOK.setEnabled(!empty);
                tfAuth.setText(AUTH + tfName.getText());
            }
        };
        buttonOK.setEnabled(false);
        tfDb.addKeyListener(inputVerifier);
        tfName.addKeyListener(inputVerifier);
        init();
    }

    private String AUTH = "${applicationId}.";

    private void init() {
        classList = new ArrayList<ClassEntity>(files.size());
        for (VirtualFile vf : files) {
            classList.add(Utils.getFrom(project, vf));
        }
        if (!classList.isEmpty()) {
            this.classEntity = classList.get(0);
        }
        ClassTableModel model = new ClassTableModel(classList);
        table = new JBTable(model);
        model.setTableEditor(table);
        scrollPane.setViewportView(table);
    }

    private void generate() {
        if (tfDb.getText().isEmpty()) {
            com.intellij.openapi.ui.Messages.showErrorDialog("Db name is empty!", "");
            return;
        }
        if (tfName.getText().isEmpty()) {
            com.intellij.openapi.ui.Messages.showErrorDialog("Class name is empty!", "");
            return;
        }
        PsiClass generate = null;
        WriteCommandAction.runWriteCommandAction(project, new Runnable() {
            @Override
            public void run() {
                PsiClass clazz = Utils.createProvider(project, pkgDir, tfPkg.getText(), tfName.getText(), tfSuper.getText(), tfDb.getText(), classList);

                FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, clazz.getContainingFile().getVirtualFile()), true);


//                XmlTag p = new EmptyXmlTag();
//                p.setName("provider");
//                p.setAttribute("android:name", clazz.getQualifiedName());
//                p.setAttribute("android:authorities", tfAuth.getText());
//                JTextField tf = new JTextField(p.toString());

                Messages.showInfoMessage("The ContentProvider create successfully!\nDon't forget to add provider to your AndroidManifest.xml", "Success");
            }
        });
        dispose();
    }

    public static void main(String[] args) {
        NewProviderDialog dialog = new NewProviderDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
