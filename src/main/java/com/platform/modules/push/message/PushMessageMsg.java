package com.platform.modules.push.message;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.platform.modules.push.dto.PushFrom;
import com.platform.modules.push.dto.PushGroup;
import com.platform.modules.push.enums.PushMsgTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.util.StringUtils;

/**
 * 普通消息
 */
@Data
@NoArgsConstructor
@Accessors(chain = true) // 链式调用
public class PushMessageMsg extends PushMessageBase {

    public PushMessageMsg(PushFrom pushFrom, PushGroup pushGroup, PushMsgTypeEnum msgType, String content) {
        Long msgId = pushFrom.getMsgId();
        Long syncId = pushFrom.getSyncId();
        if (msgId == null) {
            msgId = IdWorker.getId();
        }
        if (syncId == null) {
            syncId = IdWorker.getId();
        }
        JSONObject jsonObject;
        if (PushMsgTypeEnum.TIPS.equals(msgType)) {
            jsonObject = new JSONObject().set("data", content);
        } else {
            jsonObject = JSONUtil.parseObj(content);
        }
        PushMsgData data = new PushMsgData(pushFrom, pushGroup)
                .setMsgId(NumberUtil.toStr(msgId))
                .setSyncId(NumberUtil.toStr(syncId))
                .setSource(new PushMsgSource(pushFrom))
                .setMsgType(msgType.getCode())
                .setContent(jsonObject)
                .setCreateTime(NumberUtil.toStr(DateUtil.current()));
        this.pushData = data;
    }

    /**
     * 消息数据
     */
    @Data
    @NoArgsConstructor
    class PushMsgData {

        /**
         * 聊天对象（群组/个人/机器人）
         */
        private String chatId;

        /**
         * 聊天对象类型
         */
        private String chatTalk;

        /**
         * 头像
         */
        private String portrait;

        /**
         * 昵称
         */
        private String nickname;

        /**
         * 消息Id
         */
        private String msgId;

        /**
         * 同步Id
         */
        private String syncId;

        /**
         * 消息类型
         */
        private String msgType;

        /**
         * 消息内容
         */
        private JSONObject content;

        /**
         * 消息来源
         */
        private PushMsgSource source;

        /**
         * 创建时间
         */
        private String createTime;

        public PushMsgData(PushFrom pushFrom, PushGroup pushGroup) {
            this.chatId = NumberUtil.toStr(pushFrom.getUserId());
            this.portrait = pushFrom.getPortrait();
            this.nickname = pushFrom.getNickname();
            this.chatTalk = pushFrom.getChatTalk();
            if (pushGroup != null) {
                this.chatId = NumberUtil.toStr(pushGroup.getGroupId());
                this.portrait = pushGroup.getPortrait();
                this.nickname = pushGroup.getGroupName();
                this.chatTalk = pushGroup.getChatTalk();
            }
        }
    }

    /**
     * 消息来源
     */
    @Data
    @NoArgsConstructor
    class PushMsgSource {

        /**
         * 用户id
         */
        private String userId;

        /**
         * 用户头像
         */
        private String portrait;

        /**
         * 用户昵称
         */
        private String nickname;

        /**
         * 消息签名
         */
        private String sign;

        /**
         * 头衔
         */
        private String title;

        public PushMsgSource(PushFrom pushFrom) {
            String sign = pushFrom.getSign();
            if (StringUtils.isEmpty(sign)) {
                sign = IdWorker.getIdStr();
            }
            this.userId = NumberUtil.toStr(pushFrom.getUserId());
            this.portrait = pushFrom.getPortrait();
            this.nickname = pushFrom.getNickname();
            this.title = pushFrom.getTitle();
            this.sign = sign;
        }
    }

}
