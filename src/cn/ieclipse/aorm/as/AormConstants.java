package cn.ieclipse.aorm.as;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jamling on 2017/7/20.
 */
public abstract class AormConstants {

    public static String providerSuperQName = "android.content.ContentProvider";
    public static String sessionQName = "cn.ieclipse.aorm.Session";
    public static String aormQName = "cn.ieclipse.aorm.Aorm";

    public static String annotationPackage = "cn.ieclipse.aorm.annotation";
    public static String tableQName = "cn.ieclipse.aorm.annotation.Table";
    public static String tableName = "Table";

    public static String columnQName = "cn.ieclipse.aorm.annotation.Column";
    public static String columnName = "Column";

    public static Map<String, String> typeMap = new HashMap<String, String>();
    public static List<String> dbTypes = Arrays.asList("", "INTEGER", "TEXT", "STRING", "BLOB");

    public static Map<String, String> getTypeMap() {
        if (typeMap.isEmpty()) {
            typeMap.put("int", "INTEGER");
            typeMap.put("Integer", "INTEGER");
            typeMap.put("short", "INTEGER");
            typeMap.put("Short", "INTEGER");
            typeMap.put("Long", "INTEGER");
            typeMap.put("long", "INTEGER");
            typeMap.put("boolean", "INTEGER");
            typeMap.put("Boolean", "INTEGER");

            typeMap.put("byte[]", "BLOB");
            typeMap.put("String", "TEXT");
        }
        return typeMap;
    }
}
