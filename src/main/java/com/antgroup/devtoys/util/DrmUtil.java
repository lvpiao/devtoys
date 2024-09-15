package com.antgroup.devtoys.util;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.intellij.openapi.diagnostic.Logger;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class DrmUtil {
    private static final Logger LOG = Logger.getInstance(DrmUtil.class);

    private static final String SERVER = "127.0.0.1:" + Constant.SERVER_PORT + "/";


    private static String urlPath(String serverMethod) {
        return SERVER + serverMethod;
    }

    public static JSONObject getDrmValue(String serverMethod, String appName, String resourceId, String attribute, String zoneName, String version) {
        Map<String, Object> params = new HashMap<>();
        params.put("appName", appName);
        params.put("resourceId", resourceId);
        params.put("attribute", attribute);
        params.put("zoneName", version);
        String res = HttpUtil.post(urlPath(serverMethod), params);
        return JSONUtil.parseObj(res);
    }

    public static boolean isOn(String appName, String resourceId, String attribute, String zoneName, String version) {
        try {
            JSONObject jsonObject = getDrmValue("queryDrmValueBySingleZone", appName, resourceId, attribute, zoneName, version);
            String target = jsonObject.getStr("target").trim();
            return StringUtils.equals(target, "MYBKC1CN,on-ff,true");
        } catch (Exception e) {
            LOG.info("DRM query error", e);
            return false;
        }
    }
}
