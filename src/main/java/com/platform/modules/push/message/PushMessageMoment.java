package com.platform.modules.push.message;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.platform.modules.push.dto.PushComments;
import com.platform.modules.push.dto.PushFrom;
import com.platform.modules.push.dto.PushMedias;
import com.platform.modules.push.dto.PushMoment;
import com.platform.modules.push.enums.PushMomentEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 推送对象
 */
@Data
@Accessors(chain = true) // 链式调用
public class PushMessageMoment extends PushMessageBase {

    public PushMessageMoment(PushFrom pushFrom , PushMoment setting, PushMomentEnum moment) {
        PusMoData data = new PusMoData(setting)
                .setMsgId(setting.getMsgId())
                .setSyncId(setting.getSyncId())
                .setType(moment.getCode())
                .setSource(new PushMoSource(pushFrom))
                .setCreateTime(NumberUtil.toStr(DateUtil.current()));
        this.pushType = "moment";
        this.pushData = data;
    }


    /**
     * 消息数据
     */
    @Data
    @NoArgsConstructor
    class PusMoData {

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
        /** 动态ID */
        private Long momentId;

        /** 用户ID，改为可选类型 */
        private Long userId;

        /** 用户头像，改为可选类型 */
        private String portrait;

        /** 用户昵称，改为可选类型 */
        private String nickname;

        /** 动态正文，改为可选类型 */
        private String content;

        /** 位置信息，字符类型，可为空 */
        private String location;

        /** 图片列表，改为可选类型 */
        private List<PushMedias> images;

        /** 评论内容，改为可选类型 */
        private List<PushComments> comments;

        /** 点赞列表，改为可选类型 */
        private List<String> likes;

        /**
         * 类型
         */
        private String type;

        /**
         * 消息来源
         */
        private PushMessageMoment.PushMoSource source;

        /**
         * 创建时间
         */
        private String createTime;

        public PusMoData(PushMoment pushSync) {
            this.momentId = pushSync.getMomentId();
            this.userId = pushSync.getUserId();
            this.content = pushSync.getContent();
            this.location = pushSync.getLocation();
            this.likes = pushSync.getLikes();
            this.type = pushSync.getType();
            this.images = pushSync.getImages();
            this.comments = pushSync.getComments();
            this.portrait = pushSync.getPortrait();
            this.nickname = pushSync.getNickname();
        }
    }

    /**
     * 消息来源
     */
    @Data
    @NoArgsConstructor
    class PushMoSource {

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


        public PushMoSource(PushFrom pushFrom) {
            String sign = pushFrom.getSign();
            if (StringUtils.isEmpty(sign)) {
                sign = IdWorker.getIdStr();
            }
            this.sign = sign;
            this.userId = NumberUtil.toStr(pushFrom.getUserId());
            this.portrait = pushFrom.getPortrait();
            this.nickname = pushFrom.getNickname();
        }
    }

}
