package cn.ieclipse.aorm.as;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import org.jetbrains.annotations.NotNull;

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

    private Boolean selected = true;
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

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    private void editTableAnnotation(Project project, PsiAnnotation psiAnnotation, boolean delete) {
        if (delete) {
            if (psiAnnotation != null) {
                psiAnnotation.delete();
            }
            return;
        }
        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        if (psiAnnotation == null) {
            String a = String.format("@%s(name=\"%s\")", AormConstants.tableName, table);
            psiAnnotation = factory.createAnnotationFromText(a, psiClass);
            next = null; // TODO insert into old position
            if (next != null) {
                psiClass.getModifierList().addBefore(psiAnnotation, next);
            } else {
                psiClass.getModifierList().addBefore(psiAnnotation, psiClass.getModifierList().getFirstChild());
            }
        } else {
            PsiAnnotationMemberValue v = factory.createExpressionFromText(String.format("\"%s\"", table), psiAnnotation);
            psiAnnotation.setDeclaredAttributeValue("name", v);
        }
    }

    public void addAnnotation(Project project) {
        for (FieldEntity entity : getFieldList()) {
            entity.addAnnotation(project);
        }

        editTableAnnotation(project, tablePsiAnnotation, getSelectedEntities().size() == 0);

        PsiJavaFile javaFile = (PsiJavaFile) psiClass.getContainingFile();
        Utils.saveDocument(javaFile);

        Utils.addImport(project, javaFile, null, AormConstants.tableQName, AormConstants.columnQName);
        Utils.optimizeImport(project, psiClass);

        CodeStyleManager.getInstance(project).reformat(psiClass);
        Utils.saveDocument(psiClass.getContainingFile());
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

    public List<FieldEntity> getSelectedEntities() {
        List<FieldEntity> list = new ArrayList<FieldEntity>();
        for (FieldEntity entity : getFieldList()) {
            if (entity.getSelected()) {
                list.add(entity);
            }
        }
        return list;
    }
}
