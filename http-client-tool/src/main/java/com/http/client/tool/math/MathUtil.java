package com.http.client.tool.math;

import java.util.Objects;

/**
 * @author linzhou
 * @ClassName MathUtil.java
 * @createTime 2021年11月18日 13:55:00
 * @Description
 */
public class MathUtil {
    public static int add(Integer a, Integer b) {
        if (Objects.isNull(a)) {
            a = 0;
        }
        if (Objects.isNull(b)) {
            b = 0;
        }
        return a + b;
    }

    public static double add(Double a, Double b) {
        if (Objects.isNull(a)) {
            a = 0D;
        }
        if (Objects.isNull(b)) {
            b = 0D;
        }
        return a + b;
    }

    public static float add(Float a, Float b) {
        if (Objects.isNull(a)) {
            a = 0F;
        }
        if (Objects.isNull(b)) {
            b = 0F;
        }
        return a + b;
    }

    public static long add(Long a, Long b) {
        if (Objects.isNull(a)) {
            a = 0L;
        }
        if (Objects.isNull(b)) {
            b = 0L;
        }
        return a + b;
    }
}
