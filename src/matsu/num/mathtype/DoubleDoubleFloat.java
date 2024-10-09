/*
 * Copyright (c) 2024 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
/*
 * 2024.10.9
 */
package matsu.num.mathtype;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * double-double 精度の浮動小数点数を表す.
 * 
 * <p>
 * ほとんどの規約は {@code double} に準じており,
 * 正の無限大, 負の無限大, 非数 (NaN) を表現できる. <br>
 * また, 正の0と負の0は区別される. <br>
 * comparabilityは,
 * {@literal 負の無限大 < 負の数 < 負の0 < 正の0 < 正の数 < 正の無限大 < NaN}
 * である. <br>
 * comparability と equality は整合する.
 * </p>
 * 
 * @author Matsuura Y.
 * @version 1.0
 */
public final class DoubleDoubleFloat implements Comparable<DoubleDoubleFloat> {

    /**
     * 文字列表現を得るときに使う特殊なMathContext.
     */
    private static final MathContext MC_TO_STRING =
            new MathContext(32, RoundingMode.HALF_EVEN);

    /**
     * 正の0を表す定数.
     */
    public static final DoubleDoubleFloat POSITIVE_0 =
            new DoubleDoubleFloat(0d, 0d);

    /**
     * 負の0を表す定数.
     */
    public static final DoubleDoubleFloat NEGATIVE_0 =
            new DoubleDoubleFloat(-0d, 0d);

    /**
     * 1を表す定数.
     */
    public static final DoubleDoubleFloat POSITIVE_1 =
            new DoubleDoubleFloat(1d, 0d);

    /**
     * -1を表す定数.
     */
    public static final DoubleDoubleFloat NEGATIVE_1 =
            new DoubleDoubleFloat(-1d, 0d);

    /**
     * 扱える最大値を表す定数.
     */
    public static final DoubleDoubleFloat MAX_VALUE =
            new DoubleDoubleFloat(Double.MAX_VALUE, 0d);

    /**
     * 正の無限大を表す定数.
     */
    public static final DoubleDoubleFloat POSITIVE_INFINITY =
            new DoubleDoubleFloat(Double.POSITIVE_INFINITY, 0d);

    /**
     * 負の無限大を表す定数.
     */
    public static final DoubleDoubleFloat NEGATIVE_INFINITY =
            new DoubleDoubleFloat(Double.NEGATIVE_INFINITY, 0d);

    /**
     * NaNを表す定数.
     */
    public static final DoubleDoubleFloat NaN =
            new DoubleDoubleFloat(Double.NaN, Double.NaN);

    static {
        POSITIVE_0.negated = NEGATIVE_0;
        NEGATIVE_0.negated = POSITIVE_0;
        POSITIVE_1.negated = NEGATIVE_1;
        NEGATIVE_1.negated = POSITIVE_1;
        POSITIVE_INFINITY.negated = NEGATIVE_INFINITY;
        NEGATIVE_INFINITY.negated = POSITIVE_INFINITY;
        NaN.negated = NaN;
    }

    private final double high;
    private final double low;

    /**
     * このインスタンスのハッシュコード.
     * イミュータブルオブジェクトなので計算結果を使いまわせる.
     */
    private final int immutableHashCode;

    // 遅延初期化で使うロック用オブジェクト.
    private final Object lock = new Object();
    //遅延初期化されるフィールド
    private volatile DoubleDoubleFloat negated;
    private volatile DoubleDoubleFloat abs;

    /**
     * 唯一のコンストラクタ.
     * 
     * @param high 上位
     * @param low 下位
     */
    private DoubleDoubleFloat(double high, double low) {
        super();
        this.high = high;
        this.low = low;

        this.immutableHashCode = this.calcHashCode();
    }

    /**
     * 自身の {@code double} による表現を返す.
     * 
     * @return {@code double} 表現
     */
    public double doubleValue() {
        return this.high;
    }

    /**
     * 自身が有限の値かどうかを判定する.
     * 
     * @return 自身が有限ならtrue
     */
    public boolean isFinite() {
        return Double.isFinite(this.high);
    }

    /**
     * 自身が無限大かどうかを判定する.
     * 
     * @return 自身が無限ならtrue
     */
    public boolean isInfinite() {
        return Double.isInfinite(this.high);
    }

    /**
     * 自身がNaNかどうかを判定する.
     * 
     * @return 自身がNaNならtrue
     */
    public boolean isNaN() {
        return Double.isNaN(this.high);
    }

    /**
     * 自身と与えれらたインスタンスが等価であるかを判定する. <br>
     * このクラスのインスタンスに関する等価性のルールは, クラス説明文のとおりである.
     * 
     * @return 等価の場合はtrue
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof DoubleDoubleFloat target)) {
            return false;
        }

        return this.compareTo(target) == 0;
    }

    /**
     * このインスタンスのハッシュコードを返す.
     * 
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        return this.immutableHashCode;
    }

    /**
     * このインスタンスのハッシュコードを計算する.
     */
    private int calcHashCode() {
        int result = Double.hashCode(this.high);
        result = 31 * result + Double.hashCode(this.low);
        return result;
    }

    /**
     * 自身と引数とを比較する. <br>
     * このクラスのインスタンスに関する順序ルールは, クラス説明文のとおりである.
     * 
     * @param other 比較相手
     * @return {@code this > other} なら正,
     *             {@code this = other} なら0,
     *             {@code this < other} なら負
     * @throws NullPointerException 引数がnullの場合
     */
    @Override
    public int compareTo(DoubleDoubleFloat other) {
        int result = Double.compare(this.high, other.high);
        return result != 0
                ? result
                : Double.compare(this.low, other.low);
    }

    /**
     * このインスタンスの文字列表現を返す.
     * 
     * <p>
     * この文字列表現は,
     * double-double 精度に相当する32桁程度の10進表示である. <br>
     * 文字列表現は参考情報であり,
     * 自身とこの文字列のequalityが一致することは保証されない. <br>
     * また, この文字列をもとに {@code new BigDecimal(String)} 経由で
     * {@link #valueOf(BigDecimal)} によるインスタンスの生成を行った場合,
     * 自身と等価であることは保証されない.
     * </p>
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return this.isFinite() && this.high != 0d
                ? new BigDecimal(this.high, MathContext.DECIMAL128)
                        .add(
                                new BigDecimal(this.low, MathContext.DECIMAL128),
                                MC_TO_STRING)
                        .toString()
                : Double.toString(this.high);
    }

    /**
     * 自身の絶対値を返す.
     * 
     * @return 絶対値
     */
    public DoubleDoubleFloat abs() {

        DoubleDoubleFloat out = this.abs;
        if (Objects.nonNull(out)) {
            return out;
        }

        //単一チェックイディオム
        //nageted()は無駄なインスタンスを生成しないので,
        //複数回の呼び出しは問題ない
        out = this.compareTo(POSITIVE_0) >= 0
                ? this
                : this.negated();
        this.abs = out;
        return out;
    }

    /**
     * 自身の加法逆元 (-1倍) を返す.
     * 
     * @return 加法逆元
     */
    public DoubleDoubleFloat negated() {
        DoubleDoubleFloat out = this.negated;
        if (Objects.nonNull(out)) {
            return out;
        }

        //二重チェックイディオム
        synchronized (this.lock) {
            out = this.negated;
            if (Objects.nonNull(out)) {
                return out;
            }

            out = canonicalized(-this.high, -this.low);
            this.negated = out;

            /*
             * 特殊値の定数は必ずnegatedが登録されているので, 書き換えられない.
             * canonicalizedで新しいインスタンスを生成した場合は外部に参照が漏れていないので,
             * このチェックで問題ない.
             */
            if (Objects.isNull(out.negated)) {
                out.negated = this;
            }

            return out;
        }
    }

    /**
     * 自身の乗法逆元 (逆数) を返す.
     * 
     * @return 乗法逆元
     */
    public DoubleDoubleFloat reciprocal() {
        return POSITIVE_1.dividedBy(this);
    }

    /**
     * 和を返す.
     * 
     * @param augend augend
     * @return 和
     * @throws NullPointerException 引数がnullの場合
     */
    public DoubleDoubleFloat plus(DoubleDoubleFloat augend) {
        double[] arr = new double[2];
        two_sum_dd(this.high, this.low, augend.high, augend.low, arr);

        return canonicalized(arr[0], arr[1]);
    }

    /**
     * 和を返す.
     * 
     * @param augend augend
     * @return 和
     */
    public DoubleDoubleFloat plus(double augend) {
        double[] arr = new double[2];
        two_sum_dd(this.high, this.low, augend, 0d, arr);

        return canonicalized(arr[0], arr[1]);
    }

    /**
     * 差を返す.
     * 
     * @param subtrahend subtrahend
     * @return 差
     * @throws NullPointerException 引数がnullの場合
     */
    public DoubleDoubleFloat minus(DoubleDoubleFloat subtrahend) {
        double[] arr = new double[2];
        two_sum_dd(this.high, this.low, -subtrahend.high, -subtrahend.low, arr);

        return canonicalized(arr[0], arr[1]);
    }

    /**
     * 差を返す.
     * 
     * @param subtrahend subtrahend
     * @return 差
     */
    public DoubleDoubleFloat minus(double subtrahend) {
        double[] arr = new double[2];
        two_sum_dd(this.high, this.low, -subtrahend, 0d, arr);

        return canonicalized(arr[0], arr[1]);

    }

    /**
     * double-doubleの文脈でx+yを計算しresultに格納する. <br>
     * resultはサイズ2以上が必要.
     */
    private static void two_sum_dd(
            double xh, double xl, double yh, double yl, double[] result) {
        two_sum(xh, yh, result);
        result[1] += xl + yl;
    }

    /**
     * x+yを計算し, double-doubleとしてresultに格納する. <br>
     * resultはサイズ2以上が必要.
     */
    private static void two_sum(double x, double y, double[] result) {
        double s = x + y;
        double v = s - x;
        result[0] = s;
        result[1] = (x - (s - v)) + (y - v);
    }

    /**
     * 積を返す.
     * 
     * @param multiplicand multiplicand
     * @return 積
     * @throws NullPointerException 引数がnullの場合
     */
    public DoubleDoubleFloat times(DoubleDoubleFloat multiplicand) {
        double[] arr = new double[2];
        two_prod_dd(this.high, this.low, multiplicand.high, multiplicand.low, arr);
        return canonicalized(arr[0], arr[1]);
    }

    /**
     * 積を返す.
     * 
     * @param multiplicand multiplicand
     * @return 積
     */
    public DoubleDoubleFloat times(double multiplicand) {
        double[] arr = new double[2];
        two_prod_dd(this.high, this.low, multiplicand, 0d, arr);
        return canonicalized(arr[0], arr[1]);
    }

    /**
     * double-doubleの文脈でx*yを計算しresultに格納する. <br>
     * resultはサイズ2以上が必要.
     */
    private static void two_prod_dd(
            double xh, double xl, double yh, double yl, double[] result) {
        two_prod(xh, yh, result);
        result[1] += xl * yh + xh * yl;
    }

    /**
     * x*yを計算し, double-doubleとしてresultに格納する. <br>
     * resultはサイズ2以上が必要.
     */
    private static void two_prod(double x, double y, double[] result) {
        double p = x * y;

        //split x,y: xを上位26bitと下位26bitに分ける
        doubleSplit(x, result);
        double xh = result[0];
        double xl = result[1];
        doubleSplit(y, result);
        double yh = result[0];
        double yl = result[1];

        result[0] = p;
        result[1] = ((xh * yh - p) + xh * yl + xl * yh) + xl * yl;
    }

    /**
     * 与えられた値を上位25bit(本当は26bit, ケチ表現)と下位に分け, resultに格納する. <br>
     * resultはサイズ2以上が必要.
     */
    private static void doubleSplit(double x, double[] result) {

        //0でない非正規数と正規数以外を排除
        if (!Double.isFinite(x) || x == 0d) {
            result[0] = x;
            result[1] = 0d;
            return;
        }

        final double scale = ((double) 0x1000_0000L) * 0x1000_0000L;
        final double unscale = (1d / 0x1000_0000L / 0x1000_0000L);
        boolean scaling = false;
        //非正規数の場合は定数倍して正規化する
        if (Math.abs(x) < Double.MIN_NORMAL) {
            scaling = true;
            x *= scale;
        }

        //xの下位27bitを0埋めしたものをxhとする
        double xh = Double.longBitsToDouble(
                Double.doubleToLongBits(x) & 0xFFFF_FFFF_F800_0000L);
        double xl = x - xh;

        //正規化を行った場合は元に戻す
        if (scaling) {
            xh *= unscale;
            xl *= unscale;
        }

        result[0] = xh;
        result[1] = xl;
    }

    /**
     * 商を返す.
     * 
     * @param divisor divisor
     * @return 商
     * @throws NullPointerException 引数がnullの場合
     */
    public DoubleDoubleFloat dividedBy(DoubleDoubleFloat divisor) {
        double[] arr = new double[2];
        two_divide_dd(this.high, this.low, divisor.high, divisor.low, arr);
        return canonicalized(arr[0], arr[1]);
    }

    /**
     * 商を返す.
     * 
     * @param divisor divisor
     * @return 商
     */
    public DoubleDoubleFloat dividedBy(double divisor) {
        double[] arr = new double[2];
        two_divide_dd(this.high, this.low, divisor, 0d, arr);
        return canonicalized(arr[0], arr[1]);
    }

    /**
     * double-doubleの文脈でx/yを計算しresultに格納する. <br>
     * resultはサイズ2以上が必要.
     */
    private static void two_divide_dd(
            double xh, double xl, double yh, double yl, double[] result) {

        double zh = xh / yh;
        if (!Double.isFinite(zh) || zh == 0d) {
            result[0] = zh;
            result[1] = 0d;
            return;
        }

        //極端な値の場合は適切にスケールする
        if (Math.abs(xh) > 1E300 || Math.abs(yh) > 1E300) {
            final double scale = 1d / 0x1000_0000L / 0x1000_0000L;
            xh *= scale;
            xl *= scale;
            yh *= scale;
            yl *= scale;
        } else if (Math.abs(xh) < 1E-300 || Math.abs(yh) < 1E-300) {
            final double scale = (double) 0x1000_0000L * 0x1000_0000L;
            xh *= scale;
            xl *= scale;
            yh *= scale;
            yl *= scale;
        }

        //x-y*zhを計算する
        //r = y*zh
        two_prod_dd(zh, 0, yh, yl, result);
        //x-r
        two_sum_dd(xh, xl, -result[0], -result[1], result);

        double zl = (result[0] + result[1]) / yh;

        result[0] = zh;
        result[1] = zl;
    }

    /**
     * デバッグ用, doubleの64bit表現を得る.
     */
    @SuppressWarnings("unused")
    private static String toBinaryString(double d) {
        String raw = Long.toBinaryString(Double.doubleToLongBits(d));
        return IntStream.range(0, 64 - raw.length())
                .mapToObj(i -> "0").collect(Collectors.joining(""))
                + raw;
    }

    /**
     * 与えた high, low を正規化した物に相当するインスタンスを返す. <br>
     * 通常はabs(high) >= abs(low) でなければならない, ただしhigh=0なら問題ない.
     */
    private static DoubleDoubleFloat canonicalized(double high, double low) {
        assert (!Double.isFinite(high) || Double.isFinite(low)) : String.format(
                "highが有限であるのにlowが+-infまたはNaN: high = %s, low = %s", high, low);

        //有限数でない場合は別処理
        if (!Double.isFinite(high)) {
            return notFiniteValue(high);
        }

        assert (high == 0d || Math.abs(high) >= Math.abs(low)) : String.format(
                "high != 0d であり, かつ|high| >= |low|を満たさない: high = %s, low = %s", high, low);

        if (high == 0d) {
            if (low != 0d) {
                return new DoubleDoubleFloat(low, 0d);
            }
            return Double.compare(high, -0d) == 0
                    ? NEGATIVE_0
                    : POSITIVE_0;
        }

        double s = high + low;
        double e = low - (s - high);

        if (!Double.isFinite(s)) {
            return notFiniteValue(s);
        }

        //Double.MAX_VALUEを超える場合は無限大に置き換える
        if (Math.abs(s) == Double.MAX_VALUE) {
            if (s > 0d && e > 0d) {
                return notFiniteValue(Double.POSITIVE_INFINITY);
            }
            if (s < 0d && e < 0d) {
                return notFiniteValue(Double.NEGATIVE_INFINITY);
            }
        }

        assert 0.5 * Math.ulp(s) >= Math.abs(e) : String.format(
                "正規化条件が満たされていない: s = %s, e = %s", s, e);

        return new DoubleDoubleFloat(s, e);
    }

    /**
     * 有限でない値について, 対応するインスタンスを返す.
     */
    private static DoubleDoubleFloat notFiniteValue(double value) {
        assert !Double.isFinite(value) : "有限値である";

        if (value == Double.POSITIVE_INFINITY) {
            return POSITIVE_INFINITY;
        }
        if (value == Double.NEGATIVE_INFINITY) {
            return NEGATIVE_INFINITY;
        }
        return NaN;
    }

    /**
     * 与えられた {@code double} 値に対応する
     * double-double 浮動小数点数のインスタンスを返す.
     * 
     * <p>
     * {@code dd = valueOf(value)} について,
     * {@code dd.doubleValue()} と {@code value}
     * が ({@link Double} の文脈で) 等価であることが保証されている.
     * </p>
     * 
     * @param value 値
     * @return valueと同等のインスタンス
     */
    public static DoubleDoubleFloat valueOf(double value) {
        return canonicalized(value, 0d);
    }

    /**
     * 与えられた {@link BigDecimal} 値に対応する
     * double-double 浮動小数点数のインスタンスを返す.
     * 
     * @param value 値
     * @return valueと同等のインスタンス
     * @throws NullPointerException 引数がnullの場合
     */
    public static DoubleDoubleFloat valueOf(BigDecimal value) {
        double high = value.doubleValue();
        if (!Double.isFinite(high)) {
            return canonicalized(high, 0d);
        }
        double low = value
                .subtract(
                        new BigDecimal(high, MathContext.DECIMAL128),
                        MathContext.DECIMAL128)
                .doubleValue();
        return canonicalized(high, low);
    }

    /**
     * テスト用であり非公開.
     * 与えられた {@code double} 値に対応する
     * double-double 浮動小数点数のインスタンスを返す.
     * 
     * <p>
     * 十分にバリデーションされていない.
     * </p>
     */
    static DoubleDoubleFloat valueOf(double high, double low) {
        return canonicalized(high, low);
    }

}
