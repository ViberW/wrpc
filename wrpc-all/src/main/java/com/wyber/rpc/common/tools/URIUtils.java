package com.wyber.rpc.common.tools;

import org.springframework.util.StringUtils;

import java.net.URI;

public class URIUtils {
    public static String getParam(URI uri, String paramName) {
        if (StringUtils.isEmpty(uri.getQuery())) {
            return null;
        }
        for (String param : uri.getQuery().split("&")) {
            if (param.startsWith(paramName + "=")) {
                return param.replace(paramName + "=", "");
            }
        }
        return null;
    }

    public static String getParam(URI uri, String paramName, String defaultValue) {
        String result = getParam(uri, paramName);
        return null == result ? defaultValue : result;
    }

    public static int getIntParam(URI uri, String paramName, int defaultValue) {
        String result = getParam(uri, paramName);
        return null == result ? defaultValue : Integer.valueOf(result);
    }

    public static String getService(URI uri) {
        return uri.getPath().replace("/", "");
    }
}
