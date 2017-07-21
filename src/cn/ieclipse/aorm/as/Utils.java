package cn.ieclipse.aorm.as;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jamling on 2017/7/18.
 */
public abstract class Utils {

    public static boolean hasOrm(Project project, VirtualFile vf) {
        PsiFile psiFile = PsiManager.getInstance(project).findFile(vf);
        if (psiFile instanceof PsiJavaFile || vf.getFileType().equals(StdFileTypes.JAVA)) {
            PsiJavaFile jvf = (PsiJavaFile) psiFile;
            PsiClass psiClass = jvf.getClasses()[0];
            return Utils.findOrm(psiClass) != null;
        }
        return false;
    }

    public static PsiAnnotation findOrm(PsiClass psiClass) {
        PsiModifierList psiModifierList = psiClass.getModifierList();
        PsiAnnotation[] annotations = psiModifierList.getAnnotations();
        if (annotations != null) {
            for (PsiAnnotation temp : annotations) {
                if (Utils.isTable(temp)) {
                    return temp;
                }
            }
        }
        return null;
    }

    public static boolean isTable(PsiAnnotation annotation) {
        String qname = annotation.getQualifiedName();
        return AormConstants.tableName.equals(qname) || AormConstants.tableQName.equals(qname);
    }

    public static boolean isColumn(PsiAnnotation annotation) {
        String qname = annotation.getQualifiedName();
        return AormConstants.columnName.equals(qname) || AormConstants.columnQName.equals(qname);
    }

    public static String getDeclaredStringAttributeValue(PsiAnnotation annotation, String attr, String defaultValue) {
        String value = AnnotationUtil.getDeclaredStringAttributeValue(annotation, attr);
        return value == null ? defaultValue : value;
    }

    public static Boolean getDeclaredBooleanAttributeValue(PsiAnnotation annotation, String attr) {
        // return AnnotationUtil.getBooleanAttributeValue(annotation, attr);
        PsiAnnotationMemberValue attrValue = annotation.findAttributeValue(attr);
        Object constValue = JavaPsiFacade.getInstance(annotation.getProject()).getConstantEvaluationHelper().computeConstantExpression(attrValue);
        if (constValue instanceof Boolean) {
            return (Boolean) constValue;
        } else if (constValue == null) {
            return false;
        } else if (constValue instanceof String) {
            try {
                return Boolean.valueOf((String) constValue);
            } catch (Exception e) {

            }
        }
        return false;
    }

    public static String getPreferredType(PsiField field) {
        String type = field.getType().getCanonicalText();
        if (type != null) {
            if (type.startsWith("java.lang.")) {
                type = type.substring(10);
            }
            String map = AormConstants.getTypeMap().get(type);
            return map == null ? type : map;
        }
        return "";
    }

    public static List<VirtualFile> getSelectJavaFiles(AnActionEvent e, boolean recursive) {
        List<VirtualFile> list = new ArrayList<VirtualFile>();
        VirtualFile[] vfs = e.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);
        if (vfs != null) {
            for (VirtualFile vf : vfs) {
                list.addAll(Utils.getJavaFileTree(e.getProject(), vf, recursive));
            }
        }
        return list;
    }

    public static List<VirtualFile> getJavaFileTree(Project project, VirtualFile file, boolean recursive) {
        List<VirtualFile> list = new ArrayList<VirtualFile>();
        if (!file.isDirectory()) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
            if (psiFile instanceof PsiJavaFile || file.getFileType().equals(StdFileTypes.JAVA)) {
                list.add(file);
            }
        } else if (recursive) {
            VirtualFile[] vfs = file.getChildren();
            if (vfs != null) {
                for (VirtualFile vf : vfs) {
                    list.addAll(Utils.getJavaFileTree(project, vf, recursive));
                }
            }
        }
        return list;
    }

    public static ClassEntity getFrom(Project project, VirtualFile vf) {
        PsiFile psiFile = PsiManager.getInstance(project).findFile(vf);
        PsiJavaFile javaFile = (PsiJavaFile) psiFile;
        return new ClassEntity(javaFile.getClasses()[0]);
    }

    public static Icon getAnnotationIcon(ClassEntity entity) {
        return getAddEditIcon(Utils.findOrm(entity.getPsiClass()) == null);
    }

    public static Icon getAddEditIcon(boolean add) {
        try {
            return add ? AllIcons.ToolbarDecorator.Add : AllIcons.ToolbarDecorator.Edit;
        } catch (Exception e) {

        }
        return null;
    }

    public static void saveDocument(PsiFile psiFile) {
        PsiDocumentManager manager = PsiDocumentManager.getInstance(psiFile.getProject());
        Document document = manager.getDocument(psiFile);
        manager.doPostponedOperationsAndUnblockDocument(document);
    }

    public static void codeFormat(Project project, PsiElement element){
        CodeStyleManager.getInstance(project).reformat(element);
    }
}
