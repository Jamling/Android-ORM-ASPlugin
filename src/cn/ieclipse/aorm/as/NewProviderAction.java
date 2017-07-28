package cn.ieclipse.aorm.as;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.List;

/**
 * Created by Jamling on 2017/7/21.
 */
public class NewProviderAction extends DumbAwareAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        List<VirtualFile> javaFiles = Utils.getSelectJavaFiles(e, true);
        int size = javaFiles.size();
        for (int i = 0; i < javaFiles.size(); i++) {
            VirtualFile vf = javaFiles.get(i);
            if (!Utils.hasOrm(project, vf)) {
                javaFiles.remove(i);
                i--;
            }
        }

        // if (!javaFiles.isEmpty())
        {
            NewProviderDialog dialog = new NewProviderDialog();
            dialog.init(project, e.getData(PlatformDataKeys.VIRTUAL_FILE), javaFiles);
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
            dialog.dispose();
        }
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        getTemplatePresentation().setIcon(AllIcons.Nodes.Class);
    }
}
