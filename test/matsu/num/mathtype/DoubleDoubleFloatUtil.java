/*
 * Copyright © 2024 Matsuura Y.
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package matsu.num.mathtype;

/**
 * {@link DoubleDoubleFloat} に関するユーティリティ(テスト用).
 */
final class DoubleDoubleFloatUtil {

    private DoubleDoubleFloatUtil() {
        throw new AssertionError("インスタンス化不可");
    }

    /**
     * resultがexpectedに近いかを判定する.
     * 
     * @param result result
     * @param expected expected
     * @param relativeError e_r
     * @return {@literal |result - expected| <= |e_r * expected|} ならtrue
     */
    public static boolean isClose(DoubleDoubleFloat result, DoubleDoubleFloat expected, double relativeError) {
        return Math.abs(result.minus(expected).doubleValue()) <= Math.abs(relativeError * expected.doubleValue());
    }
}
