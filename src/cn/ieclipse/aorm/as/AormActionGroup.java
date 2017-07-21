package cn.ieclipse.aorm.as;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * Created by Jamling on 2017/7/20.
 */
public class AormActionGroup extends DefaultActionGroup {
    @Override
    public void update(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        VirtualFile vf = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        e.getPresentation().setVisible(true);
        AddAnnotationAction action = findAction(e);
        if (action != null && vf != null) {
            action.getTemplatePresentation().setVisible(true);
            action.getTemplatePresentation().setEnabled(true);
            boolean has = Utils.hasOrm(project, vf);
            String text = has ? "Edit annotation" : "Add annotation";
            action.getTemplatePresentation().setText(text);
            action.getTemplatePresentation().setIcon(Utils.getAddEditIcon(!has));
        }
    }

    private AddAnnotationAction findAction(AnActionEvent e) {
        AnAction[] actions = getChildren(e);
        if (actions != null) {
            for (AnAction a : actions) {
                if (a instanceof AddAnnotationAction) {
                    return (AddAnnotationAction) a;
                }
            }
        }
        return null;
    }
}
