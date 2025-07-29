package com.platform.modules.push.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 推送审核类型
 */
@Getter
public enum PushAuditEnum {

    /**
     * 申请_认证
     */
    APPLY_AUTH("applyAuth", 1, "申请实名"),
    /**
     * 申请_提现
     */
    APPLY_CASH("applyCash", 2, "申请提现"),
    /**
     * 申请_解封
     */
    APPLY_BANNED("applyBanned", 3, "申请解封"),
    /**
     * 异常账号
     */
    APPLY_SPECIAL("applySpecial", 4, "异常账号"),
    /**
     * 举报_用户
     */
    INFORM_USER("informUser", 5, "举报用户"),
    /**
     * 举报_群组
     */
    INFORM_GROUP("informGroup", 6, "举报群组"),
    /**
     * 用户_反馈
     */
    USER_FEEDBACK("feedback", 7, "建议反馈"),
    /**
     * 用户_充值
     */
    USER_RECHARGE("recharge", 8, "账户充值"),
    /**
     * 用户_注册
     */
    USER_REGISTER("register", 9, "用户注册"),
    ;

    @JsonValue
    private String code;
    private Integer type;
    private String info;

    PushAuditEnum(String code, Integer type, String info) {
        this.code = code;
        this.type = type;
        this.info = info;
    }

}
