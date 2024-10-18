package com.lzy.k8s.saas.infra.constants;

/**
 * 日期格式枚举
 * @author yueqi
 * @date 2021/09/24
 */
public enum TimePatternEnum {

    /**
     * 默认格式
     */
    DEFAULT_MODEL("yyyy-MM-dd HH:mm:ss"),

    DAY_MODEL("yyyyMMdd"),

    DAY_MODEL_HYPHEN("yyyy-MM-dd"),

    MINUTE_MODEL("yyyy-MM-dd HH:mm"),

    ZERO_END_MODEL("yyyy-MM-dd HH:mm:ss.0"),

    SIMPLE_MINUTE_MODEL("MM-dd HH:mm"),

    HHMM("HH:mm");

    private String pattern;

    public String getPattern() {
        return pattern;
    }

    TimePatternEnum(String pattern) {
        this.pattern = pattern;
    }
}
