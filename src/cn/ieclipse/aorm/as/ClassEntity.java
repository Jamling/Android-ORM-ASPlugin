package cn.ieclipse.aorm.as;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jamling on 2017/7/18.
 */
public class ClassEntity {

    private String table;
    private PsiClass psiClass;
    private PsiAnnotation tablePsiAnnotation;
    private PsiElement next;

    private List<FieldEntity> fieldList = new ArrayList<FieldEntity>();

    public ClassEntity(PsiClass psiClass) {
        this.psiClass = psiClass;
        table = psiClass.getName().toLowerCase();
        PsiModifierList psiModifierList = psiClass.getModifierList();
        PsiAnnotation[] annotations = psiModifierList.getAnnotations();
        if (annotations != null) {
            for (PsiAnnotation temp : annotations) {
                if (Utils.isTable(temp)) {
                    table = Utils.getDeclaredStringAttributeValue(temp, "name", null);
                    tablePsiAnnotation = temp;
                    next = temp.getNextSibling();
                    break;
                }
            }
        }

        PsiField[] fields = psiClass.getFields();
        if (fields != null) {
            for (PsiField f : fields) {
                if (accept(f)) {
                    fieldList.add(new FieldEntity(f));
                }
            }
        }
    }

    private boolean accept(PsiField field) {
        PsiModifierList modifierList = field.getModifierList();
        String[] illegals = {"static", "final", "transient"};
        for (String attr : illegals) {
            if (modifierList.hasModifierProperty(attr)) {
                return false;
            }
        }
        return true;
    }

    public List<FieldEntity> getFieldList() {
        if (fieldList == null) {
            fieldList = new ArrayList<FieldEntity>(0);
        }
        return fieldList;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getClassName() {
        return psiClass.getQualifiedName();
    }

    public PsiClass getPsiClass() {
        return psiClass;
    }

    public void addAnnotation(Project project) {
        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        if (tablePsiAnnotation == null) {
            String a = String.format("@%s(name=\"%s\")", AormConstants.tableName, table);
            tablePsiAnnotation = factory.createAnnotationFromText(a, psiClass);
            if (next != null) {
                psiClass.getModifierList().addBefore(tablePsiAnnotation, next);
            } else {
                psiClass.getModifierList().addBefore(tablePsiAnnotation, psiClass.getModifierList().getFirstChild());
            }
        } else {
            PsiAnnotationMemberValue v = factory.createExpressionFromText(String.format("\"%s\"", table), tablePsiAnnotation);
            tablePsiAnnotation.setDeclaredAttributeValue("name", v);
        }

        for (FieldEntity entity : getFieldList()) {
            entity.addAnnotation(project);
        }
        PsiJavaFile javaFile = (PsiJavaFile) psiClass.getContainingFile();
        Utils.saveDocument(javaFile);
        PsiClass clazz = JavaPsiFacade.getInstance(project).findClass(AormConstants.tableQName, GlobalSearchScope.allScope(project));
        if (clazz != null) {
            PsiImportStatement importStatement = factory.createImportStatement(clazz);
            javaFile.getImportList().add(importStatement);
            clazz = JavaPsiFacade.getInstance(project).findClass(AormConstants.columnQName, GlobalSearchScope.allScope(project));
            importStatement = factory.createImportStatement(clazz);
            javaFile.getImportList().add(importStatement);
        } else {

        }
        // Utils.saveDocument(javaFile);
        CodeStyleManager.getInstance(project).reformat(psiClass);
    }

    public void setAutoType(boolean auto) {
        for (FieldEntity entity : getFieldList()) {
            entity.setAutoType(auto);
        }
    }

    public void setPrefix(String prefix) {
        for (FieldEntity entity : getFieldList()) {
            entity.setPrefix(prefix);
        }
    }
}
