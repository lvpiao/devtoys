package com.antgroup.devtoys.util;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.weisj.jsvg.C;
import com.intellij.openapi.diagnostic.Logger;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class DrmUtil {
    private static final Logger LOG = Logger.getInstance(DrmUtil.class);


    private static String urlPath(String serverMethod) {
        return Constant.SERVER_ADDRESS + "drm/" + serverMethod;
    }


    public static String getDrmValue(String serverMethod, String appName, String resourceId, String attribute, String zoneName, String version) {
        Map<String, Object> params = new HashMap<>();
        params.put("appName", appName);
        params.put("resourceId", resourceId);
        params.put("attribute", attribute);
        params.put("zoneName", version);
        String url = urlPath(serverMethod);
        String res = HttpUtil.get(url, params);
        System.out.println("DRM query, url: " + url + " res:" + res);
        return res;
    }

    public static boolean isOn(String appName, String resourceId, String attribute, String zoneName, String version) {
        try {
            String res = getDrmValue("queryDrmValueBySingleZone", appName, resourceId, attribute, zoneName, version);
            return StringUtils.equals(res, "MYBKC1CN,on-ff,true");
        } catch (Exception e) {
            LOG.info("DRM query error", e);
            System.out.println("DRM query error");
            return false;
        }
    }
}
