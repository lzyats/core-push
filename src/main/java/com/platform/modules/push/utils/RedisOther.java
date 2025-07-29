package com.platform.modules.push.utils;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.web.vo.LabelVo;
import com.platform.modules.push.constant.PushConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 *
 * @author WangFan
 * @version 1.1 (GitHub文档: https://github.com/whvcse/RedisUtil )
 * @date 2018-02-24 下午03:09:50
 */
@Component
@ConditionalOnProperty(value = "spring.redis.enabled", havingValue = "Y")
public class RedisOther {

    @Autowired
    @Qualifier("redisOtherTemplate")
    private RedisTemplate<String, Object> redisOtherTemplate;

    /**
     * 存储消息
     */
    public void pushMsg(Long msgId, String groupId, List<Long> receiveList, String message, List<String> dateList) {
        // 聊天消息
        this.set(StrUtil.format(PushConstant.PUSH_MSG, msgId), message, PushConstant.PUSH_TIME, TimeUnit.DAYS);
        // 聊天集合
        List<LabelVo> dataList = new ArrayList();
        // 聊天群组
        dataList.add(new LabelVo(StrUtil.format(PushConstant.PUSH_GROUP, groupId), msgId));
        // 聊天对象
        receiveList.forEach(receiveId -> {
            dateList.forEach(date -> {
                dataList.add(new LabelVo(StrUtil.format(PushConstant.PUSH_USER, date, receiveId), msgId));
            });
        });
        this.rightPush(dataList);
    }

    /**
     * 将值 value 关联到 key ，并将 key 的过期时间设为 timeout
     */
    public void set(String key, String value, long timeout, TimeUnit unit) {
        redisOtherTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 获取指定 key 的值
     */
    public Object get(String key) {
        return redisOtherTemplate.opsForValue().get(key);
    }

    /**
     * 批量获取
     */
    public List<String> multiGet(Collection<String> keys) {
        // 获取
        List<Object> dataList = redisOtherTemplate.opsForValue().multiGet(keys);
        // 集合判空
        if (CollectionUtils.isEmpty(dataList)) {
            return new ArrayList<>();
        }
        // list转Obj
        return dataList.stream().collect(ArrayList::new, (x, y) -> {
            if (y != null) {
                x.add(y.toString());
            }
        }, ArrayList::addAll);
    }

    /**
     * 删除key
     */
    public void delete(String key) {
        // 空key
        if (StringUtils.isEmpty(key)) {
            return;
        }
        // 不包含通配符
        if (!key.contains("*")) {
            redisOtherTemplate.delete(key);
            return;
        }
        // 包含通配符
        Set<String> keys = redisOtherTemplate.keys(key);
        if (!CollectionUtils.isEmpty(keys)) {
            redisOtherTemplate.delete(keys);
        }
    }

    /**
     * 批量删除key
     */
    public void delete(Collection<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        redisOtherTemplate.delete(keys);
    }

    /**
     * 获取列表数据
     */
    public List<String> lRange(String key) {
        // 获取数据
        List<Object> dataList = redisOtherTemplate.opsForList().range(key, 0, -1);
        // 集合判空
        if (CollectionUtils.isEmpty(dataList)) {
            return new ArrayList<>();
        }
        // list转Obj
        return dataList.stream().collect(ArrayList::new, (x, y) -> {
            if (y != null) {
                x.add(y.toString());
            }
        }, ArrayList::addAll);
    }

    /**
     * 删除集合中值等于value得元素
     */
    public void lRemove(String key, List<String> dataList) {
        ThreadUtil.execAsync(() -> {
            dataList.forEach(data -> {
                redisOtherTemplate.opsForList().remove(key, 0, data);
            });
        });
    }

    /**
     * 存储在list尾部
     */
    private void rightPush(List<LabelVo> dataList) {
        ThreadUtil.execAsync(() -> {
            dataList.forEach(data -> {
                redisOtherTemplate.opsForList().rightPush(data.getLabel(), data.getValue());
            });
        });
    }

}