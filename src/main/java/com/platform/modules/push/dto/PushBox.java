package com.platform.modules.push.dto;

import cn.hutool.core.util.NumberUtil;
import com.platform.modules.push.enums.PushBoxEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 盒子消息
 */
@Data
@Accessors(chain = true) // 链式调用
public class PushBox {

    /**
     * 数据
     */
    private String data;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 备注
     */
    private String remark;
    /**
     * 类型
     */
    private String type;
    /**
     * 参数
     */
    private String value;

    public PushBox(String data, String title, String content, String remark) {
        this.data = data;
        this.title = title;
        this.content = content;
        this.remark = remark;
        this.type = PushBoxEnum.SYSTEM.getCode();
        this.value = "";
    }

    public PushBox(String data, String title, String content, String remark, PushBoxEnum pushType, String value) {
        this.data = data;
        this.title = title;
        this.content = content;
        this.remark = remark;
        this.type = pushType.getCode();
        this.value = value;
    }

    public PushBox(String data, String title, String content, String remark, PushBoxEnum pushType, Number value) {
        this.data = data;
        this.title = title;
        this.content = content;
        this.remark = remark;
        this.type = pushType.getCode();
        this.value = NumberUtil.toStr(value);
    }

}
