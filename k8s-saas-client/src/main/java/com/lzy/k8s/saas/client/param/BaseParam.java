package com.lzy.k8s.saas.client.param;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * @Author: liangzhiyu
 * @Date: 2023/9/8
 */
public class BaseParam implements Serializable {
    private static final long serialVersionUID = 1164796983220238151L;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
