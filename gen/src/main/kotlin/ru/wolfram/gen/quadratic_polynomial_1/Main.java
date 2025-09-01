package ru.wolfram.gen.quadratic_polynomial_1;

import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) {
        try (final Scanner scanner = new Scanner(System.in)) {
            final Long p = scanner.nextLong();
            final Long q = scanner.nextLong();
            final Long n = scanner.nextLong();
            final BiFunction<Long, Long, Long> plus = Long::sum;
            final BiFunction<Long, Long, Long> mul = (x, y) -> x * y;
            final Function<Long, Long> mod = (x) -> x % 998244353;
            final Function<Long, Long> toLong = Function.identity();
            System.out.println(solve(
                    p,
                    q,
                    n,
                    -1L,
                    0L,
                    1L,
                    2L,
                    plus,
                    mul,
                    mod,
                    toLong
            ));
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T solve(
            T p,
            T q,
            T n,
            T minusNeutral,
            T zero,
            T neutral,
            T two,
            BiFunction<T, T, T> plus,
            BiFunction<T, T, T> mul,
            Function<T, T> mod,
            Function<T, Long> toLong
    ) {
        if (n.equals(zero)) {
            return two;
        } else if (n.equals(neutral)) {
            return mul.apply(minusNeutral, p);
        } else {
            p = mod.apply(p);
            q = mod.apply(q);
            T[][] pow = (T[][]) new Object[2][2];
            pow[0][0] = mod.apply(mul.apply(minusNeutral, p));
            pow[0][1] = neutral;
            pow[1][0] = mod.apply(mul.apply(minusNeutral, q));
            pow[1][1] = zero;
            pow = pow(pow, zero, neutral, plus, mul, mod, toLong.apply(n) - 2);
            T[][] start = (T[][]) new Object[2][2];
            start[0][0] = mod.apply(plus.apply(mod.apply(mul.apply(p, p)), mod.apply(mul.apply(minusNeutral, mod.apply(mul.apply(two, q))))));
            start[0][1] = mod.apply(mul.apply(minusNeutral, p));
            start[1][0] = mod.apply(mul.apply(minusNeutral, p));
            start[1][1] = two;
            final T[][] result = (T[][]) new Object[2][2];
            fillZeroes(result, zero);
            multiply(start, pow, result, plus, mul, mod);
            return result[0][0];
        }
    }

    private static <T> void multiply(
            T[][] mat1,
            T[][] mat2,
            T[][] dst,
            BiFunction<T, T, T> plus,
            BiFunction<T, T, T> mul,
            Function<T, T> mod
    ) {
        for (int i = 0; i < mat1.length; i++) {
            for (int j = 0; j < mat1[0].length; j++) {
                for (int k = 0; k < mat2[0].length; k++) {
                    dst[i][k] = mod.apply(
                            plus.apply(
                                    mod.apply(dst[i][k]),
                                    mod.apply(
                                            mul.apply(
                                                    mod.apply(mat1[i][j]),
                                                    mod.apply(mat2[j][k])))));
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T[][] pow(
            T[][] mat,
            T zero,
            T neutral,
            BiFunction<T, T, T> plus,
            BiFunction<T, T, T> mul,
            Function<T, T> mod,
            long n
    ) {
        assert (n >= 0);
        final int size = mat.length;
        T[][] result = (T[][]) new Object[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    result[i][j] = neutral;
                } else {
                    result[i][j] = zero;
                }
            }
        }
        final T[][] a = (T[][]) new Object[mat.length][];
        final T[][] clone = (T[][]) new Object[result.length][];
        clone2D(mat, a);
        while (n > 0) {
            if (n % 2 == 1) {
                clone2D(result, clone);
                fillZeroes(result, zero);
                multiply(a, clone, result, plus, mul, mod);
            }
            clone2D(a, clone);
            fillZeroes(a, zero);
            multiply(clone, clone, a, plus, mul, mod);
            n = n / 2;
        }
        return result;
    }

    private static <T> void clone2D(T[][] src, T[][] dst) {
        for (int i = 0; i < src.length; i++) {
            dst[i] = src[i].clone();
        }
    }

    private static <T> void fillZeroes(T[][] dst, T zero) {
        for (int i = 0; i < dst.length; i++) {
            for (int j = 0; j < dst[0].length; j++) {
                dst[i][j] = zero;
            }
        }
    }

}
