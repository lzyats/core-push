package com.platform.modules.push.dto;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONObject;
import com.platform.modules.push.enums.PushSettingEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 设置消息
 */
@Data
@NoArgsConstructor
@Accessors(chain = true) // 链式调用
public class PushSetting {

    /**
     * 类型
     */
    private String type;
    /**
     * 数据
     */
    private JSONObject data;

    public PushSetting(PushSettingEnum setting, Number primary, String label, String value) {
        this.type = setting.getCode();
        this.data = new JSONObject()
                .set("primary", NumberUtil.toStr(primary))
                .set("label", label)
                .set("value", value)
        ;
    }

    public PushSetting(PushSettingEnum setting, String label, String value) {
        this.type = setting.getCode();
        this.data = new JSONObject()
                .set("primary", "")
                .set("label", label)
                .set("value", value)
        ;
    }

}
