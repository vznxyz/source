package com.minexd.source.chat.spam.algorithm

import java.util.*
import kotlin.math.roundToInt

object JaroWrinkler {

    /**
     * Find the Jaro Winkler Distance which indicates the similarity score
     * between two CharSequences.
     *
     * <pre>
     * distance.apply(null, null)          = IllegalArgumentException
     * distance.apply("","")               = 0.0
     * distance.apply("","a")              = 0.0
     * distance.apply("aaapppp", "")       = 0.0
     * distance.apply("frog", "fog")       = 0.93
     * distance.apply("fly", "ant")        = 0.0
     * distance.apply("elephant", "hippo") = 0.44
     * distance.apply("hippo", "elephant") = 0.44
     * distance.apply("hippo", "zzzzzzzz") = 0.0
     * distance.apply("hello", "hallo")    = 0.88
     * distance.apply("ABC Corporation", "ABC Corp") = 0.93
     * distance.apply("D N H Enterprises Inc", "D &amp; H Enterprises, Inc.") = 0.95
     * distance.apply("My Gym Children's Fitness Center", "My Gym. Childrens Fitness") = 0.92
     * distance.apply("PENNSYLVANIA", "PENNCISYLVNIA")    = 0.88
    </pre> *
     *
     * @param left the first String, must not be null
     * @param right the second String, must not be null
     * @return result distance
     * @throws IllegalArgumentException if either String input `null`
     */
    fun apply(left: CharSequence, right: CharSequence): Double {
        val defaultScalingFactor = 0.1
        val percentageRoundValue = 100.0

        val mtp = matches(left, right)
        val m = mtp[0].toDouble()

        if (m == 0.0) {
            return 0.0
        }

        val j = (m / left.length + m / right.length + (m - mtp[1]) / m) / 3

        val jw = if (j < 0.7) {
            j
        } else {
            j + defaultScalingFactor.coerceAtMost(1.0 / mtp[3]) * mtp[2] * (1.0 - j)
        }

        return (jw * percentageRoundValue).roundToInt() / percentageRoundValue
    }

    /**
     * This method returns the Jaro-Winkler string matches, transpositions, prefix, max array.
     *
     * @param first the first string to be matched
     * @param second the second string to be matched
     * @return mtp array containing: matches, transpositions, prefix, and max length
     */
    @JvmStatic
    fun matches(first: CharSequence, second: CharSequence): IntArray {
        val max: CharSequence
        val min: CharSequence

        if (first.length > second.length) {
            max = first
            min = second
        } else {
            max = second
            min = first
        }

        val range = (max.length / 2 - 1).coerceAtLeast(0)

        val matchIndexes = IntArray(min.length)
        Arrays.fill(matchIndexes, -1)

        val matchFlags = BooleanArray(max.length)
        var matches = 0
        for (mi in min.indices) {
            val c1 = min[mi]
            var xi = (mi - range).coerceAtLeast(0)
            val xn = (mi + range + 1).coerceAtMost(max.length)

            while (xi < xn) {
                if (!matchFlags[xi] && c1 == max[xi]) {
                    matchIndexes[mi] = xi
                    matchFlags[xi] = true
                    matches++
                    break
                }
                xi++
            }
        }

        val ms1 = CharArray(matches)
        val ms2 = CharArray(matches)

        run {
            var i = 0
            var si = 0
            while (i < min.length) {
                if (matchIndexes[i] != -1) {
                    ms1[si] = min[i]
                    si++
                }
                i++
            }
        }

        run {
            var i = 0
            var si = 0
            while (i < max.length) {
                if (matchFlags[i]) {
                    ms2[si] = max[i]
                    si++
                }
                i++
            }
        }

        var transpositions = 0
        for (mi in ms1.indices) {
            if (ms1[mi] != ms2[mi]) {
                transpositions++
            }
        }

        var prefix = 0
        for (mi in min.indices) {
            if (first.elementAt(mi) == second.elementAt(mi)) {
                prefix++
            } else {
                break
            }
        }

        return intArrayOf(matches, transpositions / 2, prefix, max.length)
    }

}