package cn.ieclipse.aorm.as;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

/**
 * Created by Jamling on 2017/7/18.
 */
public class FieldEntity {
    private PsiField psiField;
    private String fieldName;
    private String dbName;
    private String prefix;
    private String finalDbName;

    private String defaultValue;
    private String dbType;
    private String oldDbType;
    private Boolean notNull = false;
    private Boolean id = false;

    private Boolean selected = true;
    private PsiAnnotation psiAnnotation;
    private PsiElement next = null;

    public FieldEntity(PsiField psiField) {
        this.psiField = psiField;

        fieldName = psiField.getName();
        dbName = fieldName;
        dbType = Utils.getPreferredType(psiField);// TODO
        PsiElement[] children = psiField.getModifierList().getChildren();
        if (children != null) {
            for (PsiElement ch : children) {
                if (ch instanceof PsiAnnotation) {
                    PsiAnnotation temp = (PsiAnnotation) ch;
                    if (Utils.isColumn(temp)) {
                        psiAnnotation = temp;
                        dbName = Utils.getDeclaredStringAttributeValue(temp, "name", fieldName);
                        dbType = Utils.getDeclaredStringAttributeValue(temp, "type", dbType);
                        notNull = Utils.getDeclaredBooleanAttributeValue(temp, "notNull");
                        id = Utils.getDeclaredBooleanAttributeValue(temp, "id");
                        next = temp.getNextSibling();
                        break;
                    }
                }
            }
        }
    }

    public String getDbName() {
        return prefix == null ? dbName : prefix + dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public Boolean isId() {
        return id;
    }

    public void setId(Boolean id) {
        this.id = id;
    }

    public Boolean isNotNull() {
        return notNull;
    }

    public void setNotNull(Boolean notNull) {
        this.notNull = notNull;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setAutoType(boolean auto) {
        if (auto) {
            oldDbType = getDbType();
            setDbType(null);
        } else {
            setDbType(oldDbType);
        }
    }

    @Override
    public String toString() {
        return String.format("%s@Column(name=%s,type=%s,id=%s,notnull=%s", fieldName, dbName, dbType, id, notNull);
    }

    public String getFieldName() {
        return fieldName;
    }


    public void addAnnotation(Project project) {
        if (psiAnnotation != null) {
            psiAnnotation.delete();
        }
        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        psiAnnotation = factory.createAnnotationFromText(getAnnotationText(), psiField);
        if (next != null) {
            psiField.getModifierList().addBefore(psiAnnotation, next);
        } else {
            psiField.getModifierList().addBefore(psiAnnotation, psiField.getModifierList().getFirstChild());
        }
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getAnnotationText() {
        StringBuilder sb = new StringBuilder();
        sb.append("name=\"");
        sb.append(getDbName());
        sb.append("\"");
        if (getDbType() != null && !getDbType().isEmpty()) {
            sb.append(", type=\"");
            sb.append(getDbType());
            sb.append("\"");
        }
        if (isNotNull()) {
            sb.append(", notNull=");
            sb.append(isNotNull());
            sb.append("");
        }
        if (isId()) {
            sb.append(", id=");
            sb.append(isId());
            sb.append("");
        }
        String a = String.format("@%s(%s)", AormConstants.columnName, sb.toString());
        return a;
    }
}
