package com.platform.modules.push.message;

import com.platform.modules.push.dto.PushSetting;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 推送对象
 */
@Data
@Accessors(chain = true) // 链式调用
public class PushMessageSetting extends PushMessageBase {

    public PushMessageSetting(PushSetting setting) {
        this.pushType = "setting";
        this.pushData = setting;
    }

}
