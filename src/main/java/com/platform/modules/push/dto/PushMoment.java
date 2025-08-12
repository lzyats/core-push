package com.platform.modules.push.dto;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONObject;
import com.platform.modules.push.enums.PushMomentEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * 设置消息
 */
@Data
@NoArgsConstructor
@Accessors(chain = true) // 链式调用
public class PushMoment {

    /**
     * 消息Id
     */
    private String msgId;
    /**
     * 同步Id
     */
    private String syncId;
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

    /** 发布时间，改为可选类型 */
    private Date createTime;

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
    /**
     * 数据
    private JSONObject data;

    // 朋友圈信息
    public PushMoment(PushMomentEnum type,Long momentId, Long userId, String portrait, String nickname, String content,String location,Date createTime,List<PushMedias> value) {
        this.momentId = momentId;
        this.userId = userId;
        this.portrait = portrait;
        this.nickname = nickname;
        this.images = value;
        this.type = type.getCode();
    }
    // 点赞信息
    public PushMoment(PushMomentEnum type,Long momentId, Long userId, String portrait, String nickname, List<String> value) {
        this.momentId = momentId;
        this.userId = userId;
        this.portrait = portrait;
        this.nickname = nickname;
        this.type = type.getCode();
        this.likes = value;
    }
    // 评论信息
    public PushMoment(PushMomentEnum type,Long momentId, Long userId, String portrait, String nickname, List<PushComments> data) {
        this.momentId = momentId;
        this.userId = userId;
        this.portrait = portrait;
        this.nickname = nickname;
        this.type = type.getCode();
        this.comments=data;
    }
    // 媒体信息
    public PushMoment(PushMomentEnum type,Long momentId, Long userId, String portrait, String nickname, List<PushMedias> value) {
        this.momentId = momentId;
        this.userId = userId;
        this.portrait = portrait;
        this.nickname = nickname;
        this.type = type.getCode();
        this.images = value;
    }
    */
}
