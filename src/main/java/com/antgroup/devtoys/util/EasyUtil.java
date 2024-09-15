package com.antgroup.devtoys.util;

import com.intellij.openapi.application.ApplicationManager;

public class EasyUtil {


    /**
     * 获取服务
     */
    public static <T> T getService(Class<T> clazz) {
        return ApplicationManager.getApplication().getService(clazz);
    }
}
