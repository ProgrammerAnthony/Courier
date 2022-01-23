package com.courier.core.utils;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.asm.Type;
import org.springframework.cglib.core.ClassInfo;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.StringJoiner;

/**
 * @author Anthony
 * @create 2022/1/16
 * @desc
 */
@Slf4j
public class ReflectUtils {

    private static final HashMap<String, Class<?>> PRIMITIVE_MAP = new HashMap<String, Class<?>>() {
        {
            put("java.lang.Integer", int.class);
            put("java.lang.Double", double.class);
            put("java.lang.Float", float.class);
            put("java.lang.Long", long.class);
            put("java.lang.Short", short.class);
            put("java.lang.Boolean", boolean.class);
            put("java.lang.Char", char.class);
        }
    };
    public static final String METHOD_SEPARATOR = ",";
    public static final String CLAZZ_SEPARATOR = "#";


    public static Class<?>[] buildTypeClassArray(String[] parameterTypes) {
        Class<?>[] parameterTypeClassArray = new Class<?>[parameterTypes.length];
        for (int i = parameterTypes.length - 1; i >= 0; i--) {
            try {
                parameterTypeClassArray[i] = Class.forName(parameterTypes[i]);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return parameterTypeClassArray;
    }


    public static Class<?> getClassByName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("could not find class, loading class: {}", className, e);
            return null;
        }
    }


    public static Class<?> checkClassByName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * build parameter by JSON String parameter
     * @param parameterText JSON String parameter
     * @param parameterTypeClassArray
     * @return
     */
    public static Object[] buildArgs(String parameterText, Class<?>[] parameterTypeClassArray) {
        JSONArray paramJsonArray = JSONUtil.parseArray(parameterText);
        Object[] args = new Object[paramJsonArray.size()];

        for (int i = paramJsonArray.size() - 1; i >= 0; i--) {
            if (paramJsonArray.getStr(i).startsWith("{")) {
                args[i] = JSONUtil.toBean(paramJsonArray.getStr(i), parameterTypeClassArray[i]);
            } else {
                args[i] = paramJsonArray.get(i);
            }
        }
        return args;
    }


    public static String getArgsClassNames(Signature signature) {
        MethodSignature methodSignature = (MethodSignature) signature;
        Class<?>[] parameterTypes = methodSignature.getParameterTypes();
        StringBuilder parameterStrTypes = new StringBuilder();
        for (int i = 0; i < parameterTypes.length; i++) {
            parameterStrTypes.append(parameterTypes[i].getName());
            if (parameterTypes.length != (i + 1)) {
                parameterStrTypes.append(METHOD_SEPARATOR);
            }
        }
        return parameterStrTypes.toString();
    }


    public static String getTargetMethodFullyQualifiedName(JoinPoint point, Class<?>[] argsClazz) {
        StringJoiner methodSignNameJoiner = new StringJoiner("", "", "");
        methodSignNameJoiner
                .add(point.getTarget().getClass().getName())
                .add(CLAZZ_SEPARATOR)
                .add(point.getSignature().getName());
        methodSignNameJoiner.add("(");
        for (int i = 0; i < argsClazz.length; i++) {
            String className = argsClazz[i].getName();
            methodSignNameJoiner.add(className);
            if (argsClazz.length != (i + 1)) {
                methodSignNameJoiner.add(METHOD_SEPARATOR);
            }
        }
        methodSignNameJoiner.add(")");
        return methodSignNameJoiner.toString();
    }


    public static Class<?>[] getArgsClass(Object[] args) {
        Class<?>[] clazz = new Class[args.length];
        for (int k = 0; k < args.length; k++) {
            if (!args[k].getClass().isPrimitive()) {
                String result = args[k].getClass().getName();
                Class<?> typeClazz = PRIMITIVE_MAP.get(result);
                clazz[k] = ObjectUtils.isEmpty(typeClazz) ? args[k].getClass() : typeClazz;
            }
        }
        return clazz;
    }


    public static String getFullyQualifiedClassName(Class<?> clazz) {
        if (ObjectUtils.isEmpty(clazz)) {
            return "";
        }
        return clazz.getName();
    }


    public static boolean isRealizeTargetInterface(Class<?> targetClass, String targetInterfaceClassName) {
        ClassInfo classInfo = org.springframework.cglib.core.ReflectUtils.getClassInfo(targetClass);
        for (Type anInterface : classInfo.getInterfaces()) {
            if (anInterface.getClassName().equals(targetInterfaceClassName)) {
                return true;
            }
        }
        return false;
    }

}
