package com.platform.modules.push.utils;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 推送服务
 */
@Slf4j
public class PushUtils {

    /**
     * 鉴权URL
     */
    private static final String AUTH_URL = "https://restapi.getui.com/v2/$appId/auth";
    /**
     * 单推推送
     */
    private static final String SINGLE_URL = "https://restapi.getui.com/v2/$appId/push/single/alias";
    /**
     * 群推推送
     */
    private static final String LIST_URL = "https://restapi.getui.com/v2/$appId/push/list/alias";
    /**
     * 创建消息
     */
    private static final String MESSAGE_URL = "https://restapi.getui.com/v2/$appId/push/list/message";
    /**
     * 常量
     */
    private static final String APP_ID = "$appId";
    /**
     * 超时时间
     */
    private static final Integer TIMEOUT = 5000;
    /**
     * TTL 3 * 86400000 (普通账号3天，VIP7天)
     */
    private static final Long TTL = 259200000L;

    /**
     * 获取鉴权token，获得返回值之后，应存入缓存
     */
    public static String createToken(String appId, String appKey, String masterSecret) {
        String url = formatUrl(appId, AUTH_URL);
        String timestamp = String.valueOf(DateUtil.current());
        String sign = SecureUtil.sha256(appKey + timestamp + masterSecret);
        Dict dict = Dict.create()
                .set("sign", sign)
                .set("timestamp", timestamp)
                .set("appkey", appKey);
        String jsonStr = HttpUtil.post(url, JSONUtil.toJsonStr(dict), TIMEOUT);
        log.info(jsonStr);
        JSONObject data = JSONUtil.parseObj(jsonStr).getJSONObject("data");
        return data.getStr("token");
    }

    /**
     * 单推消息
     */
    public static void pushSingle(Collection alias, String transmission, String appId, String token) {
        // 组装消息
        Dict body = Dict.create()
                .set("request_id", IdWorker.getId())
                .set("settings", Dict.create().set("ttl", TTL))
                .set("audience", Dict.create().set("alias", alias))
                .set("push_message", Dict.create().set("transmission", transmission));
        // 发送消息
        String jsonStr = HttpUtil.createPost(formatUrl(appId, SINGLE_URL))
                .header("token", token)
                .body(JSONUtil.toJsonStr(body))
                .timeout(TIMEOUT)
                .execute()
                .body();
        log.info(jsonStr);
    }

    /**
     * 群推消息
     */
    public static void pushList(Collection alias, String transmission, String appId, String token) {
        // 组装消息
        Dict message = Dict.create()
                .set("settings", Dict.create().set("ttl", TTL))
                .set("push_message", Dict.create().set("transmission", transmission));
        // 创建消息
        String json = HttpUtil.createPost(formatUrl(appId, MESSAGE_URL))
                .header("token", token)
                .body(JSONUtil.toJsonStr(message))
                .timeout(TIMEOUT)
                .execute()
                .body();
        log.info(json);
        // 解析
        String taskId = JSONUtil.parseObj(json).getJSONObject("data").getStr("taskid");
        // 循环
        batch(alias).forEach(data -> {
            // 组装消息
            Dict body = Dict.create()
                    .set("taskid", taskId)
                    .set("audience", Dict.create().set("alias", data));
            // 发送消息
            String jsonStr = HttpUtil.createPost(formatUrl(appId, LIST_URL))
                    .header("token", token)
                    .body(JSONUtil.toJsonStr(body))
                    .timeout(TIMEOUT)
                    .execute()
                    .body();
            log.info(jsonStr);
        });
    }

    /**
     * 循环格式
     */
    private static List<Collection> batch(Collection dataList) {
        List<Collection> resultList = new ArrayList();
        Collection batchList = new ArrayList();
        dataList.forEach((data) -> {
            batchList.add(data);
            if (200 == batchList.size()) {
                resultList.add(ListUtil.toList(batchList));
                batchList.clear();
            }
        });
        if (!CollectionUtils.isEmpty(batchList)) {
            resultList.add(ListUtil.toList(batchList));
            batchList.clear();
        }
        return resultList;
    }

    /**
     * 格式化url
     */
    private static String formatUrl(String appId, String url) {
        return url.replace(APP_ID, appId);
    }

}
