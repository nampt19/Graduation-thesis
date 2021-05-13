package com.nampt.socialnetworkproject;

import com.nampt.socialnetworkproject.util.CalculateTimeUtil;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class CalculatorTimeTest {

    @Test
    public void testTimeChatViews() {
        CalculateTimeUtil timeUtil = CalculateTimeUtil.getInstance();
        Date timeCurrent = new Date(1620697200000L); // 05/11/2021 8:40:00

        Date tc1 = new Date(1620697201000L);         // 05/11/2021 8:40:01
        Date tc2 = new Date(1620697200000L);         // 05/11/2021 8:40:00
        Date tc3 = new Date(1620697199000L);         // 05/11/2021 8:39:59

        Date tc4 = new Date(1620666000000L);         // 05/11/2021 0:00:00
        Date tc5 = new Date(1620666001000L);         // 05/11/2021 0:00:01
        Date tc6 = new Date(1620665999000L);         // 05/10/2021 23:59:59

        Date tc7 = new Date(1620406800000L);         // 05/08/2021 0:00:00
        Date tc8 = new Date(1620406801000L);         // 05/08/2021 0:00:01
        Date tc9 = new Date(1620406799000L);         // 05/07/2021 23:59:59

        Date tc10 = new Date(1609434000000L);         // 01/01/2021 0:00:00
        Date tc11 = new Date(1609434001000L);         // 01/01/2021 0:00:01
        Date tc12 = new Date(1609433999000L);         // 12/31/2020 23:59:59

        Assert.assertEquals("invalid", timeUtil.calculatorTimeFromChatViews(tc1, timeCurrent));
        Assert.assertEquals("8:40", timeUtil.calculatorTimeFromChatViews(tc2, timeCurrent));
        Assert.assertEquals("8:39", timeUtil.calculatorTimeFromChatViews(tc3, timeCurrent));

        Assert.assertEquals("0:00", timeUtil.calculatorTimeFromChatViews(tc4, timeCurrent));
        Assert.assertEquals("0:00", timeUtil.calculatorTimeFromChatViews(tc5, timeCurrent));
        Assert.assertEquals("T2 lúc 23:59", timeUtil.calculatorTimeFromChatViews(tc6, timeCurrent));

        Assert.assertEquals("T7 lúc 0:00", timeUtil.calculatorTimeFromChatViews(tc7, timeCurrent));
        Assert.assertEquals("T7 lúc 0:00", timeUtil.calculatorTimeFromChatViews(tc8, timeCurrent));
        Assert.assertEquals("7/5 lúc 23:59", timeUtil.calculatorTimeFromChatViews(tc9, timeCurrent));

        Assert.assertEquals("1/1 lúc 0:00", timeUtil.calculatorTimeFromChatViews(tc10, timeCurrent));
        Assert.assertEquals("1/1 lúc 0:00", timeUtil.calculatorTimeFromChatViews(tc11, timeCurrent));
        Assert.assertEquals("31/12/2020 lúc 23:59", timeUtil.calculatorTimeFromChatViews(tc12, timeCurrent));
    }

    @Test
    public void testTimeCommon() {
        CalculateTimeUtil timeUtil = CalculateTimeUtil.getInstance();
        Date timeCurrent = new Date(1620697200000L); // 05/11/2021 8:40:00

        Date tc1 = new Date(1620697201000L);         // 05/11/2021 8:40:01
        Date tc2 = new Date(1620697200000L);         // 05/11/2021 8:40:00
        Date tc3 = new Date(1620697199000L);         // 05/11/2021 8:39:59

        Date tc4 = new Date(1620697140000L);         // 05/11/2021 8:39:00
        Date tc5 = new Date(1620697139000L);         // 05/11/2021 8:38:59
        Date tc6 = new Date(1620697141000L);         // 05/11/2021 8:39:01

        Date tc7 = new Date(1620693600000L);         // 05/11/2021 7:40:00
        Date tc8 = new Date(1620693599000L);         // 05/11/2021 7:39:59
        Date tc9 = new Date(1620693601000L);         // 05/11/2021 7:40:01

        Date tc13 = new Date(1620610800000L);         // 05/10/2021 8:40:00
        Date tc14 = new Date(1620610799000L);         // 05/10/2021 8:39:59
        Date tc15 = new Date(1620610801000L);         // 05/10/2021 8:40:01

        Date tc16 = new Date(1620092400000L);         // 05/04/2021 8:40:00
        Date tc17 = new Date(1620092399000L);         // 05/04/2021 8:39:59
        Date tc18 = new Date(1620092401000L);         // 05/04/2021 8:40:01

        Date tc19 = new Date(1609434000000L);         // 01/01/2021 0:00:00
        Date tc20 = new Date(1609434001000L);         // 01/01/2021 0:00:01
        Date tc21 = new Date(1609433999000L);         // 12/31/2020 23:59:59

//        Assert.assertEquals("invalid", timeUtil.calculatorTimeCommon(tc1, timeCurrent));
//        Assert.assertEquals("vừa xong", timeUtil.calculatorTimeCommon(tc2, timeCurrent));
//        Assert.assertEquals("vừa xong", timeUtil.calculatorTimeCommon(tc3, timeCurrent));
//
//        Assert.assertEquals("1 phút trước", timeUtil.calculatorTimeCommon(tc4, timeCurrent));
//        Assert.assertEquals("1 phút trước", timeUtil.calculatorTimeCommon(tc5, timeCurrent));
//        Assert.assertEquals("vừa xong", timeUtil.calculatorTimeCommon(tc6, timeCurrent));
//
//        Assert.assertEquals("1 giờ trước", timeUtil.calculatorTimeCommon(tc7, timeCurrent));
//        Assert.assertEquals("1 giờ trước", timeUtil.calculatorTimeCommon(tc8, timeCurrent));
//        Assert.assertEquals("59 phút trước", timeUtil.calculatorTimeCommon(tc9, timeCurrent));
//
//        Assert.assertEquals("1 ngày trước", timeUtil.calculatorTimeCommon(tc13, timeCurrent));
//        Assert.assertEquals("1 ngày trước", timeUtil.calculatorTimeCommon(tc14, timeCurrent));
//        Assert.assertEquals("23 giờ trước", timeUtil.calculatorTimeCommon(tc15, timeCurrent));
//
//        Assert.assertEquals("4/5 lúc 8:40", timeUtil.calculatorTimeCommon(tc16, timeCurrent));
//        Assert.assertEquals("4/5 lúc 8:39", timeUtil.calculatorTimeCommon(tc17, timeCurrent));
//        Assert.assertEquals("6 ngày trước", timeUtil.calculatorTimeCommon(tc18, timeCurrent));
//
//        Assert.assertEquals("1/1 lúc 0:00", timeUtil.calculatorTimeCommon(tc19, timeCurrent));
//        Assert.assertEquals("1/1 lúc 0:00", timeUtil.calculatorTimeCommon(tc20, timeCurrent));
//        Assert.assertEquals("31/12/2020 lúc 23:59", timeUtil.calculatorTimeCommon(tc21, timeCurrent));
    }
}
