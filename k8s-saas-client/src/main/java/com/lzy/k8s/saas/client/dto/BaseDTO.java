package com.lzy.k8s.saas.client.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * @Author: liangzhiyu
 * @Date: 2023/9/8
 */
public class BaseDTO implements Serializable {

    private static final long serialVersionUID = 2765975368164562464L;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
