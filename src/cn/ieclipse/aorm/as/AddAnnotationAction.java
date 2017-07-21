package cn.ieclipse.aorm.as;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.List;

/**
 * Created by Jamling on 2017/5/16.
 */
public class AddAnnotationAction extends DumbAwareAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        List<VirtualFile> javaFiles = Utils.getSelectJavaFiles(e, true);

        if (!javaFiles.isEmpty()) {
            AddAnnotationDialog dialog = new AddAnnotationDialog(project, javaFiles);
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
            dialog.dispose();
        }
    }

    @Override
    public void update(AnActionEvent event) {
        super.update(event);
        setEnabledInModalContext(true);
        getTemplatePresentation().setEnabled(true);
        getTemplatePresentation().setVisible(true);
    }
}
