package com.platform.modules.push.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 消息类型枚举
 */
@Getter
public enum PushMsgTypeEnum {

    /**
     * 文字/表情
     */
    TEXT("text", "文字"),
    /**
     * 图片/拍照
     */
    IMAGE("image", "图片"),
    /**
     * 提醒
     */
    TIPS("tips", "提醒"),
    /**
     * 声音
     */
    VOICE("voice", "声音"),
    /**
     * 视频/拍摄
     */
    VIDEO("video", "视频"),
    /**
     * 位置
     */
    LOCATION("location", "位置"),
    /**
     * 名片
     */
    CARD("card", "名片"),
    /**
     * 文件
     */
    FILE("file", "文件"),
    /**
     * 语音/视频
     */
    CALL("call", "语音视频"),
    /**
     * 转账
     */
    TRANSFER("transfer", "转账"),
    /**
     * 红包
     */
    PACKET("packet", "红包"),
    /**
     * 专属红包
     */
    GROUP_ASSIGN("group_assign", "专属红包"),
    /**
     * 手气红包
     */
    GROUP_LUCK("group_luck", "手气红包"),
    /**
     * 普通红包
     */
    GROUP_PACKET("group_packet", "普通红包"),
    /**
     * 群组转账
     */
    GROUP_TRANSFER("group_transfer", "群组转账"),
    /**
     * @某人
     */
    AT("at", "@某人"),
    /**
     * 撤回消息
     */
    RECALL("recall", "撤回消息"),
    /**
     * 转发（合并转发）
     */
    FORWARD("forward", "转发"),
//    /**
//     * 阅后即焚
//     */
//    SNAP("snap", "阅后即焚"),
//    /**
//     * 接龙
//     */
//    SOLITAIRE("solitaire", "接龙"),
    /**
     * 回复
     */
    REPLY("reply", "回复"),
    /**
     * 卡片
     */
    BOX("box", "卡片"),
    /**
     * 事件
     */
    EVEN("even", "事件"),
    ;

    @EnumValue
    @JsonValue
    private String code;
    private String info;

    PushMsgTypeEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }

}
