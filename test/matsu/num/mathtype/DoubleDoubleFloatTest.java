/*
 * Copyright © 2024 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package matsu.num.mathtype;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

/**
 * {@link DoubleDoubleFloat} クラスのテスト.
 */
@RunWith(Enclosed.class)
final class DoubleDoubleFloatTest {

    public static final Class<?> TEST_CLASS = DoubleDoubleFloat.class;

    /**
     * 2^{55} = 3.6 * 1E+17.
     */
    private static final double value_2_55 = 36028797018963968d;

    @RunWith(Theories.class)
    public static class doubleからの生成に関するテスト {

        @DataPoints
        public static double[] values = {
                0d,
                -0d,
                1d,
                -2d,
                Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY,
                Double.NaN,
        };

        @Theory
        public void test_入力と出力のdoubleは等価である(double value) {
            DoubleDoubleFloat dd = DoubleDoubleFloat.valueOf(value);
            assertThat(Double.valueOf(dd.doubleValue()), is(Double.valueOf(value)));
        }
    }

    public static class 比較に関するテスト {

        @Test
        public void test_2meは1peより大きい() {
            assertThat(
                    DoubleDoubleFloat.valueOf(2d, -1E-50).compareTo(DoubleDoubleFloat.valueOf(1d, 1E-50)),
                    is(greaterThan(0)));
            assertThat(
                    DoubleDoubleFloat.valueOf(2d, -0.25).compareTo(DoubleDoubleFloat.valueOf(1d, 0.25)),
                    is(greaterThan(0)));
        }

        @Test
        public void test_1peは1meより大きい() {
            assertThat(
                    DoubleDoubleFloat.valueOf(1d, 1E-50).compareTo(DoubleDoubleFloat.valueOf(1d, -1E-50)),
                    is(greaterThan(0)));
        }

        @Test
        public void test_NaNはNaNと同列であり最大である() {
            assertThat(
                    DoubleDoubleFloat.NaN.compareTo(DoubleDoubleFloat.NaN),
                    is(0));
            assertThat(
                    DoubleDoubleFloat.NaN.compareTo(DoubleDoubleFloat.POSITIVE_INFINITY),
                    is(greaterThan(0)));
        }

        @Test
        public void test_p0はm0より大きい() {
            assertThat(
                    DoubleDoubleFloat.POSITIVE_0.compareTo(DoubleDoubleFloat.NEGATIVE_0),
                    is(greaterThan(0)));
        }
    }

    public static class 等価性に関するテスト {

        @Test
        public void test_同一の値は等価である() {
            double vh = 2d;
            double vl = -1E-20;
            DoubleDoubleFloat dd1 = DoubleDoubleFloat.valueOf(vh, vl);
            DoubleDoubleFloat dd2 = DoubleDoubleFloat.valueOf(vh, vl);

            assertThat(dd1.equals(dd2), is(true));
            assertThat(dd1.hashCode() == dd2.hashCode(), is(true));
        }

        @Test
        public void test_plusInfは等価である() {
            double v = Double.POSITIVE_INFINITY;
            DoubleDoubleFloat dd1 = DoubleDoubleFloat.valueOf(v, 1d);
            DoubleDoubleFloat dd2 = DoubleDoubleFloat.valueOf(v, -1d);

            assertThat(dd1.equals(dd2), is(true));
            assertThat(dd1.hashCode() == dd2.hashCode(), is(true));
        }

        @Test
        public void test_minusInfは等価である() {
            double v = Double.NEGATIVE_INFINITY;
            DoubleDoubleFloat dd1 = DoubleDoubleFloat.valueOf(v, 1d);
            DoubleDoubleFloat dd2 = DoubleDoubleFloat.valueOf(v, -1d);

            assertThat(dd1.equals(dd2), is(true));
            assertThat(dd1.hashCode() == dd2.hashCode(), is(true));
        }

        @Test
        public void test_NaNは等価である() {
            double v = Double.NaN;
            DoubleDoubleFloat dd1 = DoubleDoubleFloat.valueOf(v, 1d);
            DoubleDoubleFloat dd2 = DoubleDoubleFloat.valueOf(v, -1d);

            assertThat(dd1.equals(dd2), is(true));
            assertThat(dd1.hashCode() == dd2.hashCode(), is(true));
        }
    }

    @RunWith(Theories.class)
    public static class 絶対値と逆元の検証 {

        @DataPoints
        public static double[][] values = {
                { 1d, 1E-20 },
                { -2d, 1E-20 },
                { Double.POSITIVE_INFINITY, 0 },
                { Double.NEGATIVE_INFINITY, 0 },
                { Double.NaN, 0 },
        };

        @Theory
        public void test_絶対値を検証する(double[] value) {
            double vh = value[0];
            double vl = value[1];
            DoubleDoubleFloat v = DoubleDoubleFloat.valueOf(vh, vl);

            double rh;
            double rl;
            if (Double.isFinite(vh)) {
                if (vh == 0d) {
                    rh = vh;
                    rl = 0d;
                } else if (vh > 0d) {
                    rh = vh;
                    rl = vl;
                } else {
                    rh = -vh;
                    rl = -vl;
                }
            } else {
                rh = Math.abs(vh);
                rl = 0d;
            }

            DoubleDoubleFloat expected = DoubleDoubleFloat.valueOf(rh, rl);
            assertThat(v.abs(), is(expected));
        }

        @Theory
        public void test_加法逆元を検証する(double[] value) {
            double vh = value[0];
            double vl = value[1];
            DoubleDoubleFloat v = DoubleDoubleFloat.valueOf(vh, vl);

            double rh;
            double rl;
            if (Double.isFinite(vh)) {
                if (vh == 0d) {
                    rh = -vh;
                    rl = 0d;
                } else {
                    rh = -vh;
                    rl = -vl;
                }
            } else {
                rh = -vh;
                rl = 0d;
            }

            DoubleDoubleFloat expected = DoubleDoubleFloat.valueOf(rh, rl);
            assertThat(v.negated(), is(expected));
        }

        @Theory
        public void test_乗法逆元を検証する(double[] value) {
            double vh = value[0];
            double vl = value[1];
            DoubleDoubleFloat v = DoubleDoubleFloat.valueOf(vh, vl);
            DoubleDoubleFloat vr = v.reciprocal();
            DoubleDoubleFloat result = v.times(vr);

            DoubleDoubleFloat expected = DoubleDoubleFloat.POSITIVE_1;

            if (result.isFinite()) {
                assertThat(
                        DoubleDoubleFloatUtil.isClose(result, expected, 1E-30),
                        is(true));
            }
        }
    }

    public static class 乗法逆元の検証_特殊値 {

        @Test
        public void test_正の無限大の逆数はp0() {
            assertThat(
                    DoubleDoubleFloat.POSITIVE_INFINITY.reciprocal(),
                    is(DoubleDoubleFloat.POSITIVE_0));
            assertThat(
                    DoubleDoubleFloat.POSITIVE_0.reciprocal(),
                    is(DoubleDoubleFloat.POSITIVE_INFINITY));
        }

        @Test
        public void test_負の無限大の逆数はm0() {
            assertThat(
                    DoubleDoubleFloat.NEGATIVE_INFINITY.reciprocal(),
                    is(DoubleDoubleFloat.NEGATIVE_0));
            assertThat(
                    DoubleDoubleFloat.NEGATIVE_0.reciprocal(),
                    is(DoubleDoubleFloat.NEGATIVE_INFINITY));
        }
    }

    @RunWith(Theories.class)
    public static class 加減算の検証 {

        @DataPoints
        public static double[][][] values = {
                {
                        { 1d * value_2_55, 1d },
                        { 2d * value_2_55, -3d },
                        { 3d * value_2_55, -2d }
                },
                {
                        { Double.MIN_VALUE * value_2_55, Double.MIN_VALUE },
                        { 2d * Double.MIN_VALUE * value_2_55, -3 * Double.MIN_VALUE },
                        { 3d * Double.MIN_VALUE * value_2_55, -2 * Double.MIN_VALUE }
                },
                {
                        { Double.MAX_VALUE, 0 },
                        { Double.MAX_VALUE, 0 },
                        { Double.POSITIVE_INFINITY, 0 }
                },
                {
                        { Double.POSITIVE_INFINITY, 0 },
                        { Double.MAX_VALUE, 0 },
                        { Double.POSITIVE_INFINITY, 0 }
                },
                {
                        { Double.POSITIVE_INFINITY, 0 },
                        { Double.NEGATIVE_INFINITY, 0 },
                        { Double.NaN, 0 }
                },
        };

        @Theory
        public void test_加算結果を比較する(double[][] value) {
            DoubleDoubleFloat dd1 = DoubleDoubleFloat.valueOf(value[0][0], value[0][1]);
            DoubleDoubleFloat dd2 = DoubleDoubleFloat.valueOf(value[1][0], value[1][1]);
            DoubleDoubleFloat expected = DoubleDoubleFloat.valueOf(value[2][0], value[2][1]);
            assertThat(dd1.plus(dd2), is(expected));
        }

        @Theory
        public void test_減算結果を比較する(double[][] value) {
            DoubleDoubleFloat dd1 = DoubleDoubleFloat.valueOf(value[0][0], value[0][1]);
            DoubleDoubleFloat dd2 = DoubleDoubleFloat.valueOf(-value[1][0], -value[1][1]);
            DoubleDoubleFloat expected = DoubleDoubleFloat.valueOf(value[2][0], value[2][1]);
            assertThat(dd1.minus(dd2), is(expected));
        }
    }

    @RunWith(Theories.class)
    public static class 乗算の検証 {

        @DataPoints
        public static double[][][] values = {
                {
                        { -1d, -1d / value_2_55 },
                        { 2d, -10d / value_2_55 },
                        { -2d, 8d / value_2_55 }
                },
                {
                        { Double.MAX_VALUE * 0.5, 0d },
                        { -2d, 0d },
                        { -Double.MAX_VALUE, 0d }
                },
                {
                        { Double.MAX_VALUE, 0d },
                        { 2d, 0d },
                        { Double.POSITIVE_INFINITY, 0d }
                },
                {
                        { Double.NEGATIVE_INFINITY, 0d },
                        { -2d, 0d },
                        { Double.POSITIVE_INFINITY, 0d }
                },
                {
                        { Double.MIN_NORMAL * 0.125, 0d },
                        { 20d, 0d },
                        { Double.MIN_NORMAL * 2.5d, 0d }
                },
        };

        @Theory
        public void test_乗算結果を比較する(double[][] value) {
            DoubleDoubleFloat dd1 = DoubleDoubleFloat.valueOf(value[0][0], value[0][1]);
            DoubleDoubleFloat dd2 = DoubleDoubleFloat.valueOf(value[1][0], value[1][1]);
            DoubleDoubleFloat result = dd1.times(dd2);

            DoubleDoubleFloat expected = DoubleDoubleFloat.valueOf(value[2][0], value[2][1]);

            if (expected.isFinite()) {
                assertThat(
                        DoubleDoubleFloatUtil.isClose(result, expected, 1E-30),
                        is(true));
            } else {
                assertThat(result, is(expected));
            }
        }
    }

    @RunWith(Theories.class)
    public static class 除算の検証 {

        @DataPoints
        public static double[][][] values = {
                {
                        { -2d, 8d / value_2_55 },
                        { -1d, -1d / value_2_55 },
                        { 2d, -10d / value_2_55 }
                },
                {
                        { -Double.MAX_VALUE, 0d },
                        { Double.MAX_VALUE * 0.5, 0d },
                        { -2d, 0d }
                },
                {
                        { Double.MIN_NORMAL * 2.5d, 0d },
                        { Double.MIN_NORMAL * 0.125, 0d },
                        { 20d, 0d }
                },
                {
                        { Double.MAX_VALUE, 0d },
                        { 0.25, 0d },
                        { Double.POSITIVE_INFINITY, 0d }
                },
                {
                        { 1E-30d, 0d },
                        { Double.MAX_VALUE, 0d },
                        { 0d, 0d }
                },
                {
                        { -1E-30d, 0d },
                        { Double.MAX_VALUE, 0d },
                        { -0d, 0d }
                },
                {
                        { Double.POSITIVE_INFINITY, 0d },
                        { Double.NEGATIVE_INFINITY, 0d },
                        { Double.NaN, 0d }
                },
        };

        @Theory
        public void test_除算結果を比較する(double[][] value) {
            DoubleDoubleFloat dd1 = DoubleDoubleFloat.valueOf(value[0][0], value[0][1]);
            DoubleDoubleFloat dd2 = DoubleDoubleFloat.valueOf(value[1][0], value[1][1]);
            DoubleDoubleFloat result = dd1.dividedBy(dd2);

            DoubleDoubleFloat expected = DoubleDoubleFloat.valueOf(value[2][0], value[2][1]);

            if (expected.isFinite()) {
                assertThat(
                        DoubleDoubleFloatUtil.isClose(result, expected, 1E-30),
                        is(true));
            } else {
                assertThat(result, is(expected));
            }
        }
    }

    public static class toString表示 {

        @Test
        public void test_toString表示() {
            System.out.println(TEST_CLASS.getName());
            System.out.println(DoubleDoubleFloat.valueOf(new BigDecimal("0.2")));
            System.out.println(DoubleDoubleFloat.valueOf(new BigDecimal("1E500")));
            System.out.println(DoubleDoubleFloat.POSITIVE_0);
            System.out.println(DoubleDoubleFloat.NEGATIVE_0);
            System.out.println(DoubleDoubleFloat.MAX_VALUE);
            System.out.println(DoubleDoubleFloat.POSITIVE_INFINITY);
            System.out.println(DoubleDoubleFloat.NEGATIVE_INFINITY);
            System.out.println(DoubleDoubleFloat.NaN);

            System.out.println();
        }

    }
}
