package cn.ieclipse.aorm.as;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.codeInsight.generation.OverrideImplementUtil;
import com.intellij.icons.AllIcons;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleFileIndex;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.psi.impl.file.PsiJavaDirectoryFactory;
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl;
import com.intellij.psi.infos.CandidateInfo;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;
import java.util.*;

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

    public static PsiDirectory getSubDir(Project project, VirtualFile vf) {
        if (vf.isDirectory()) {
            return PsiManager.getInstance(project).findDirectory(vf);
        } else {
            return getSubDir(project, vf.getParent());
        }
    }

    public static PsiJavaDirectoryImpl getJavaDir(Project project, VirtualFile vf) {
        PsiDirectory dir = Utils.getSubDir(project, vf);
        if (dir instanceof PsiJavaDirectoryImpl) {
            return (PsiJavaDirectoryImpl) dir;
        }
        return null;
    }

    public static String getJavaPkgName(PsiJavaDirectoryImpl dir) {
        PsiDirectoryFactory factory = PsiJavaDirectoryFactory.getInstance(dir.getProject());
        if (factory.isPackage(dir)) {
            String n = factory.getQualifiedName(dir, true);
            return factory.getQualifiedName(dir, false);
        }
        return null;
    }

    public static String getPkgName(Project project, VirtualFile vf) {
        PsiFile psiFile = PsiManager.getInstance(project).findFile(vf);
        if (psiFile instanceof PsiJavaFile) {
            PsiJavaFile javaFile = (PsiJavaFile) psiFile;
            return javaFile.getPackageName();
        } else if (psiFile instanceof PsiJavaDirectoryImpl) {
            PsiJavaDirectoryImpl dir = (PsiJavaDirectoryImpl) psiFile;
            PsiDirectoryFactory factory = PsiJavaDirectoryFactory.getInstance(project);
            if (factory.isPackage(dir)) {
                String n = factory.getQualifiedName(dir, true);
                return factory.getQualifiedName(dir, false);
            }
        }
        return null;
    }


    public static PsiClass createProvider(Project project, VirtualFile vf, String pkgName, String className, String superName, String dbName, List<ClassEntity> entities) {
        PsiDirectory dir = Utils.getSubDir(project, vf);
        PsiClass generateClass = JavaDirectoryService.getInstance().createClass(dir, className);
        PsiJavaFile generateJava = (PsiJavaFile) generateClass.getContainingFile();

        ((PsiJavaFile) generateClass.getContainingFile()).setPackageName(pkgName);

        Utils.saveDocument(generateClass.getContainingFile());
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);

        // GlobalSearchScope scope = GlobalSearchScope.moduleScope(ModuleUtilCore.findModuleForFile(vf, project));

        PsiClass superClass = JavaPsiFacade.getInstance(project).findClass(superName, GlobalSearchScope.allScope(project));
        if (superClass != null) {
            PsiJavaCodeReferenceElement element = JavaPsiFacade.getElementFactory(project).createClassReferenceElement(superClass);
            generateClass.getExtendsList().add(element);

            // add field
            PsiField field = factory.createFieldFromText("private SQLiteOpenHelper mOpenHelper;", generateClass);
            PsiClassType type = factory.createTypeByFQClassName("android.database.sqlite.SQLiteOpenHelper");
            field = factory.createField("mOpenHelper", type);
            field.getModifierList().setModifierProperty(PsiModifier.PRIVATE, true);
            generateClass.add(field);

            // field = factory.createFieldFromText("private static Session session;", generateClass);
            type = factory.createTypeByFQClassName(AormConstants.sessionQName);
            field = factory.createField("session", type);
            field.getModifierList().setModifierProperty(PsiModifier.PRIVATE, true);
            field.getModifierList().setModifierProperty(PsiModifier.STATIC, true);
            generateClass.add(field);

            // create getSession
            type = factory.createTypeByFQClassName(AormConstants.sessionQName);
            PsiMethod getSession = factory.createMethod("getSession", type);
            getSession.getModifierList().setModifierProperty(PsiModifier.PUBLIC, true);
            getSession.getModifierList().setModifierProperty(PsiModifier.STATIC, true);
            getSession.getBody().add(factory.createStatementFromText("return session;", getSession));
            generateClass.add(getSession);

            // generate onCreate
            // read template
            FileTemplate template = FileTemplateManager.getInstance(project).getInternalTemplate("AORMCreate");
            Map map = new HashMap();
            String DB = (dbName == null || dbName.isEmpty()) ? className : dbName;
            if (!DB.endsWith(".db")) {
                DB = DB + ".db";
            }
            map.put("DB", DB);
            StringBuilder sb1 = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            for (ClassEntity entity : entities) {
                if (!entity.getSelected()) {
                    continue;
                }
                sb1.append(String.format("Aorm.createTable(db, %s.class);", entity.getClassName()));
                sb1.append("\n");

                sb2.append(String.format("Aorm.updateTable(db, %s.class);", entity.getClassName()));
                sb2.append("\n");
            }
            if (sb1.length() > 0) {
                sb1.deleteCharAt(sb1.length() - 1);
                sb2.deleteCharAt(sb2.length() - 1);
            }
            map.put("CREATE_TABLES", sb1.toString());
            map.put("UPDATE_TABLES", sb2.toString());
            String text = null;
            try {
                text = template.getText(map);
            } catch (IOException e) {
                e.printStackTrace();
            }
            text = StringUtil.convertLineSeparators(text);

            PsiMethod onCreate = factory.createMethodFromText(text, generateClass);
            generateClass.add(onCreate);

            // complete other method.
            Collection<CandidateInfo> list = OverrideImplementUtil.getMethodsToOverrideImplement(generateClass, true);
            for (CandidateInfo t : list) {
                List<PsiMethod> list1 = OverrideImplementUtil.overrideOrImplementMethod(generateClass, (PsiMethod) t.getElement(), t.getSubstitutor(), false, true);
                if (!list1.isEmpty()) {
                    for (PsiMethod m : list1) {
                        if (m.getName().equals("onCreate")) {
                            continue;
                        }
                        generateClass.add(m);
                    }
                }
            }

            Utils.saveDocument(generateClass.getContainingFile());
            Utils.addJavaImport(project, generateJava, null, "android.database.sqlite.SQLiteDatabase", AormConstants.aormQName);

            Utils.optimizeImport(project, generateClass);
            CodeStyleManager.getInstance(project).reformat(generateClass);
            Utils.saveDocument(generateJava);

        }
        return generateClass;
    }

    public static void addJavaImport(Project project, @NotNull PsiJavaFile psiJavaFile, GlobalSearchScope scope, String... classQNames) {
        if (project == null) {
            project = psiJavaFile.getProject();
        }
        if (scope == null) {
            scope = GlobalSearchScope.allScope(project);
        }
        if (classQNames != null) {
            JavaCodeStyleManager factory = JavaCodeStyleManager.getInstance(project);
            for (String name : classQNames) {
                PsiClass clazz = JavaPsiFacade.getInstance(project).findClass(name, scope);
                if (clazz != null) {
                    factory.addImport(psiJavaFile, clazz);
                }
            }
        }
    }

    public static void addImport(Project project, @NotNull PsiJavaFile psiJavaFile, GlobalSearchScope scope, String... classQNames) {
        if (project == null) {
            project = psiJavaFile.getProject();
        }
        if (scope == null) {
            scope = GlobalSearchScope.allScope(project);
        }
        if (classQNames != null) {
            PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
            for (String name : classQNames) {
                PsiClass clazz = JavaPsiFacade.getInstance(project).findClass(name, scope);
                if (clazz != null) {
                    PsiImportStatement importStatement = factory.createImportStatement(clazz);
                    psiJavaFile.getImportList().add(importStatement);
                }
            }
        }
    }

    public static void optimizeImport(Project project, PsiClass psiClass) {
        JavaCodeStyleManager javaCodeStyleManager = JavaCodeStyleManager.getInstance(project);
        javaCodeStyleManager.optimizeImports(psiClass.getContainingFile());
        javaCodeStyleManager.shortenClassReferences(psiClass);
    }

    public static void fillProvider() {

    }

    public static void addManifest(Project project) {

    }

    public static PsiDirectory getModuleBase(Project project, VirtualFile vf) {
        Module module = ModuleUtilCore.findModuleForFile(vf, project);
        ModuleFileIndex fileIndex = ModuleRootManager.getInstance(module).getFileIndex();
        PsiDirectory baseDir = PsiDirectoryFactory.getInstance(project).createDirectory(project.getBaseDir());
        PsiDirectory moduleDir = baseDir.findSubdirectory(module.getName());
        if (moduleDir == null) {
            return baseDir;
        }
        return moduleDir;
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

    public static void codeFormat(Project project, PsiElement element) {
        CodeStyleManager.getInstance(project).reformat(element);
    }
}
