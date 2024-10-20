package com.lzy.k8s.saas.infra.utils;


import com.google.common.collect.Maps;
import com.lzy.k8s.saas.infra.constants.TimePatternEnum;
import com.lzy.k8s.saas.infra.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import com.lzy.k8s.saas.client.result.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yueqi
 * @date 2021/09/21
 */
public class TimeUtils {

    private static final Logger log = LoggerFactory.getLogger(TimeUtils.class.getName());

    /**
     * 判断是否是同一个日期
     */
    public static Boolean identifyIsSameDay(Date datetime1, Date datetime2) {
        if (Objects.isNull(datetime1) || Objects.isNull(datetime2)) {
            return false;
        }
        LocalDate date1 = TimeUtils.convert2Time(datetime1).toLocalDate();
        LocalDate date2 = TimeUtils.convert2Time(datetime2).toLocalDate();

        return date1.isEqual(date2);
    }


    /**
     * 获取周日期
     */
    public static Integer getDayOfWeek() {
        return LocalDateTime.now().getDayOfWeek().getValue() - 1;
    }

    /**
     * 获取小时
     */
    public static Integer getHours(Date dateTime) {
        if (dateTime == null) {
            return null;
        }
        return convert2Time(dateTime).getHour();
    }

    /**
     * 格式化MAP
     */
    private static final Map<TimePatternEnum, DateTimeFormatter> FORMATTER_MAP = Maps.newHashMap();

    static {
        for (TimePatternEnum timePatternEnum : TimePatternEnum.values()) {
            FORMATTER_MAP.put(timePatternEnum, DateTimeFormatter.ofPattern(timePatternEnum.getPattern()));
        }
    }

    /**
     * 将localDateTime的HHMMSS按照localTime赋值
     *
     * @param localDateTime
     * @param localTime
     * @return
     */
    public static LocalDateTime convert2DateTime(LocalDateTime localDateTime, LocalTime localTime) {
        if (localDateTime == null || localTime == null) {
            return null;
        }
        return localDateTime.withHour(localTime.getHour())
                .withMinute(localTime.getMinute())
                .withSecond(localTime.getSecond())
                .withNano(localTime.getNano());
    }


    /**
     * 基于时间字符串和格式化类型
     * 获取LocalTime类型
     *
     * @param timeStr
     * @return
     */
    public static LocalTime getLocalTime(String timeStr, TimePatternEnum patternEnum) {
        if (StringUtils.isBlank(timeStr)) {
            return null;
        }
        if (patternEnum == null) {
            patternEnum = TimePatternEnum.HHMM;
        }
        DateTimeFormatter formatter = FORMATTER_MAP.get(patternEnum);
        LocalTime localTime = LocalTime.parse(timeStr, formatter);
        return localTime;
    }

    /**
     * 基于时间字符串和格式化类型
     * 获取LocalTime类型
     *
     * @param timeStr
     * @return
     */
    public static LocalDate getLocalDate(String timeStr, TimePatternEnum patternEnum) {
        if (StringUtils.isBlank(timeStr)) {
            return null;
        }
        if (patternEnum == null) {
            patternEnum = TimePatternEnum.DAY_MODEL;
        }
        try {
            DateTimeFormatter formatter = FORMATTER_MAP.get(patternEnum);
            LocalDate localDate = LocalDate.parse(timeStr, formatter);
            return localDate;
        } catch (Throwable e) {
            log.error("getLocalDate error. input: {}, format {}", timeStr, patternEnum);
            return null;
        }
    }

    /**
     * 将时间str转换成date
     *
     * @param timeStr
     * @return
     */
    public static Date getDate(String timeStr) {
        if (StringUtils.isBlank(timeStr)) {
            return null;
        }
        DateTimeFormatter formatter = FORMATTER_MAP.get(TimePatternEnum.DEFAULT_MODEL);
        LocalDateTime localDateTime = LocalDateTime.parse(timeStr, formatter);
        Date date = convert2Date(localDateTime);
        return date;
    }

    /**
     * 将时间字符串转换成date
     *
     * @param timeStr
     * @param patternEnum
     * @return
     */
    public static Date getDate(String timeStr, TimePatternEnum patternEnum) {
        if (StringUtils.isBlank(timeStr)) {
            return null;
        }
        if (patternEnum == null) {
            patternEnum = TimePatternEnum.DEFAULT_MODEL;
        }
        DateTimeFormatter formatter = FORMATTER_MAP.get(patternEnum);
        LocalDateTime localDateTime = LocalDateTime.parse(timeStr, formatter);
        Date date = convert2Date(localDateTime);
        return date;
    }


    /**
     * 将时间字符串转换成LocalTime
     *
     * @param timeStr
     * @return
     */
    public static LocalTime getLocalTime(String timeStr) {
        return getLocalTime(timeStr, TimePatternEnum.HHMM);
    }

    /**
     * 获取LocalDateTime
     *
     * @param timeStr
     * @param patternEnum
     * @return
     */
    public static LocalDateTime getLocalDateTime(String timeStr, TimePatternEnum patternEnum) {
        if (StringUtils.isBlank(timeStr)) {
            return null;
        }
        if (patternEnum == null) {
            patternEnum = TimePatternEnum.DEFAULT_MODEL;
        }
        try {
            DateTimeFormatter formatter = FORMATTER_MAP.get(patternEnum);
            LocalDateTime localDateTime = LocalDateTime.parse(timeStr, formatter);
            return localDateTime;
        } catch (Throwable e) {
            log.error("getLocalDateTime error. input: {}, format {}", timeStr, patternEnum);
            return null;
        }

    }

    /**
     * 获取格式化时间
     *
     * @param date
     * @param patternEnum
     * @return
     */
    public static String getFormatTime(Date date, TimePatternEnum patternEnum) {
        if (date == null) {
            return null;
        }
        if (patternEnum == null) {
            patternEnum = TimePatternEnum.DEFAULT_MODEL;
        }
        LocalDateTime localDateTime = convert2Time(date);
        DateTimeFormatter formatter = FORMATTER_MAP.get(patternEnum);
        String formatTime = formatter.format(localDateTime);
        return formatTime;
    }

    /**
     * 获取格式化信息
     *
     * @param localDateTime
     * @return
     */
    public static String getFormatTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = FORMATTER_MAP.get(TimePatternEnum.DEFAULT_MODEL);
        String formatTime = formatter.format(localDateTime);
        return formatTime;
    }

    public static String getFormatTime(LocalDateTime localDateTime, TimePatternEnum patternEnum) {
        if (localDateTime == null) {
            return null;
        }
        if (patternEnum == null) {
            patternEnum = TimePatternEnum.DEFAULT_MODEL;
        }

        DateTimeFormatter formatter = FORMATTER_MAP.get(patternEnum);
        String formatTime = formatter.format(localDateTime);
        return formatTime;
    }

    /**
     * 将LocalDateTime转换成Date
     *
     * @param localDateTime
     * @return
     */
    public static Date convert2Date(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        return date;
    }

    /**
     * 将date转换成LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime convert2Time(Date date) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDateTime = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        return localDateTime;
    }

    /**
     * 获取日期最大值
     *
     * @param aDate
     * @param bDate
     * @return
     */
    public static Date max(Date aDate, Date bDate) {
        if (aDate == null) {
            return bDate;
        }
        if (bDate == null) {
            return aDate;
        }
        return aDate.after(bDate) ? aDate : bDate;
    }

    /**
     * 获取日期最小值
     *
     * @param aDate
     * @param bDate
     * @return
     */
    public static Date min(Date aDate, Date bDate) {
        if (aDate == null) {
            return bDate;
        }
        if (bDate == null) {
            return aDate;
        }
        return aDate.before(bDate) ? aDate : bDate;
    }

    /**
     * 获取日期最大值
     *
     * @param aTime
     * @param bTime
     * @return
     */
    public static LocalDateTime max(LocalDateTime aTime, LocalDateTime bTime) {
        if (aTime == null) {
            return bTime;
        }
        if (bTime == null) {
            return aTime;
        }
        return aTime.isAfter(bTime) ? aTime : bTime;
    }

    /**
     * 对date类型数据进行小时数加法
     *
     * @param date
     * @param hours
     * @return
     */
    public static Date plusHours(Date date, Long hours) {
        if (date == null || hours == null) {
            return date;
        }
        LocalDateTime localDateTime = convert2Time(date);
        localDateTime = localDateTime.plusHours(hours);
        return convert2Date(localDateTime);
    }


    /**
     * 两个Date之间的Duration
     *
     * @param start
     * @param end
     * @return
     */
    public static Duration getDuration(Date start, Date end) {
        if (start == null || end == null) {
            log.error("getDuration exception, aDate: {}, bDate: {}", start, end);
            throw new SystemException(ErrorCode.INVALID_PARAM);
        }
        return Duration.between(TimeUtils.convert2Time(start), TimeUtils.convert2Time(end));
    }

    /**
     * 当天剩余日期
     *
     * @return
     */
    public static Long getTodayLeftSeconds() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayEndTime = LocalDateTime.of(now.toLocalDate(), LocalTime.of(23, 59, 59));

        return Long.max(Duration.between(now, todayEndTime).getSeconds(), 0);
    }

    /**
     * 某个时间点后的当天剩余时间
     *
     * @return
     */
    public static Long getOneDayLeftSeconds(LocalDateTime localDateTime) {
        LocalDateTime todayEndTime = LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.of(23, 59, 59));

        return Long.max(Duration.between(localDateTime, todayEndTime).getSeconds(), 0);
    }

    /**
     * 将 timestamp 转换成 LocalDateTime
     */
    public static LocalDateTime convertTimestamp2Date(Long timeStamp) {
        if (timeStamp == null) {
            return LocalDateTime.now();
        }
        Instant instant = Instant.ofEpochMilli(timeStamp);
        ZoneId zoneId = ZoneId.systemDefault();
        try {
            return LocalDateTime.ofInstant(instant, zoneId);
        } catch (Throwable e) {
            log.error("Error timestamp {} that cannot convert to local datetime", timeStamp);
            return LocalDateTime.now();
        }
    }

    /**
     * 得到昨天日期
     * yyyyMMdd
     * @return
     */
    public static Integer getYesterday() {
        Date today = new Date();
        Calendar dBefore = Calendar.getInstance();
        dBefore.setTime(today);
        dBefore.add(Calendar.DAY_OF_YEAR,-1);
        SimpleDateFormat sf = new SimpleDateFormat(TimePatternEnum.DAY_MODEL.getPattern());
        String dBeforeStr = sf.format(dBefore.getTime());
        Integer yest = Integer.parseInt(dBeforeStr);
        return yest;
    }

    /**
     * 获取T-n日期
     * @param n
     * @return
     */
    public static Integer getDayBeforeToday(Integer n) {
        Date today = new Date();
        Calendar dBefore = Calendar.getInstance();
        dBefore.setTime(today);
        dBefore.add(Calendar.DAY_OF_YEAR, n);
        SimpleDateFormat sf = new SimpleDateFormat(TimePatternEnum.DAY_MODEL.getPattern());
        String dBeforeStr = sf.format(dBefore.getTime());
        Integer day = Integer.parseInt(dBeforeStr);
        return day;
    }

    /**
     * 两个时间的间隔
     * @return
     */
    public static Long getTimeGap(Date beforeTime, Date afterTime) {
        if(Objects.isNull(afterTime) || Objects.isNull(beforeTime)) {
            return null;
        }
        return afterTime.getTime() - beforeTime.getTime();
    }

    /**
     * 两个时间的间隔
     * @return
     */
    public static Long getTimeGap(Long beforeTime, Long afterTime) {
        if(Objects.isNull(afterTime) || Objects.isNull(beforeTime) || afterTime == 0 || beforeTime == 0) {
            return null;
        }
        return afterTime - beforeTime;
    }

    /**
     * 计算时间列表中的最大时间
     */
    public static Date getMaxDate(List<Date> timestampList) {
        if (CollectionUtils.isEmpty(timestampList)) {
            return null;
        }
        List<Date> noNullTimeList = timestampList.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(noNullTimeList)) {
            return null;
        }
        return Collections.max(noNullTimeList);
    }

    /**
     * 获取两个DATE的小时差
     * @param date1
     * @param date2
     * @return
     */
    public static Integer getDateTimeDiff(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return null;
        }
        Long timeDiff = date2.getTime() - date1.getTime();
        Long timeDiffHour = timeDiff / (1000 * 60 * 60);
        return timeDiffHour.intValue();
    }

    /**
     * 获取两个date的整天差
     * @param date1
     * @param date2
     * @return
     */
    public static Integer getDateTimeDayDiff(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return null;
        }
        Duration duration = getDuration(date1, date2);
        long days = duration.toDays();
        return (int) days;
    }

    /**
     * 获取两个DATE的分钟差
     * @param date1
     * @param date2
     * @return
     */
    public static Double getDateTimeMinuteDiff(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return null;
        }
        Long timeDiff = date2.getTime() - date1.getTime();
        double minute = 1000 * 60d;
        double timeDiffMinute = timeDiff / minute;
        return timeDiffMinute;
    }
}

