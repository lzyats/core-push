package com.platform.modules.push.service.impl;

import cn.hutool.core.util.StrUtil;
import com.platform.common.enums.YesOrNoEnum;
import com.platform.common.redis.RedisUtils;
import com.platform.modules.push.constant.PushConstant;
import com.platform.modules.push.service.PushProvider;
import com.platform.modules.push.utils.PushUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 推送 服务层
 * </p>
 */
@Slf4j
@Service("pushProvider")
@Configuration
public class PushProviderImpl implements PushProvider {

    @Autowired(required = false)
    private RocketMQTemplate rocketMQTemplate;

    @Autowired(required = false)
    private RedisUtils redisUtils;

    @Value("${rocketmq.enabled:}")
    private String enable;

    @Value("${rocketmq.topic:}")
    private String topic;

    @Override
    public void convertAndSend(String content) {
        if (!YesOrNoEnum.YES.getCode().equalsIgnoreCase(enable)) {
            log.info("mq未配置");
            return;
        }
        try {
            rocketMQTemplate.convertAndSend(topic, content);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Value("${push.enabled:}")
    private String enabled;

    @Value("${push.appId:}")
    private String appId;

    @Value("${push.appKey:}")
    private String appKey;

    @Value("${push.appSecret:}")
    private String appSecret;

    @Value("${push.masterSecret:}")
    private String masterSecret;

    @Override
    public void convertAndSend(Collection receiveList, String message) {
        if (!YesOrNoEnum.YES.getCode().equalsIgnoreCase(enabled)) {
            log.info("个推推送未配置");
            return;
        }
        // 长度判断
        if (StrUtil.length(message) > 3000) {
            log.error("个推消息太长");
            return;
        }
        // 组装token
        String token = this.initPushToken();
        try {
            // 群推
            if (receiveList.size() > 1) {
                PushUtils.pushList(receiveList, message, appId, token);
            }
            // 单推
            else if (!receiveList.contains(10001L)) {
                PushUtils.pushSingle(receiveList, message, appId, token);
            }
        } catch (Exception e) {
            log.error("个推推送出错", e);
        }

    }

    /**
     * 组装token
     */
    private String initPushToken() {
        String token;
        if (redisUtils.hasKey(PushConstant.PUSH_TOKEN)) {
            token = redisUtils.get(PushConstant.PUSH_TOKEN);
        } else {
            token = PushUtils.createToken(appId, appKey, masterSecret);
            redisUtils.set(PushConstant.PUSH_TOKEN, token, 1, TimeUnit.HOURS);
        }
        return token;
    }

}
