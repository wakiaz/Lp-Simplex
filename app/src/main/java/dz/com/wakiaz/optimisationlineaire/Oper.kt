/**
 * class for matrix operations
 */
package dz.com.wakiaz.optimisationlineaire

class Oper {

    fun mult(a: List<List<Double>>, b: List<List<Double>>): MutableList<List<Double>> {

        val m: MutableList<List<Double>> = mutableListOf()
        if (check_format(a) and check_format(b) && a[0].size == b.size) {

            var temp: MutableList<Double> = mutableListOf()
            var s = 0.0
            for (i in 0 until a[0].size) {
                for (j in 0 until b[0].size) {
                    for (k in 0 until a[0].size) {
                        s += a[i][k] * b[k][j]
                    }
                    temp.add(j, s)
                    s = 0.0
                }
                m.add(i, temp)
                temp = mutableListOf()
            }
        }
        return m

    }

    fun multv(a: List<List<Double>>, b: List<Double>): MutableList<Double> {

        val m: MutableList<Double> = mutableListOf()
        if (check_format(a) && a[0].size == b.size) {
            var s = 0.0
            for (i in 0 until a[0].size) {
                for (k in 0 until a[0].size) {
                    s += a[i][k] * b[k]

                }
                m.add(i, s)
                s = 0.0
            }
        }

        return m

    }

    /**
     * function for calcul v*A
     */
    fun multv_a(v: List<Double>, a: List<List<Double>>): MutableList<Double> {

        val m: MutableList<Double> = mutableListOf()
        if (check_format(a) && a.size == v.size) {

            var s = 0.0
            for (i in 0 until a[0].size) {
                for (k in 0 until a.size) {
                    //                  if (((a[k][i] == Double.NEGATIVE_INFINITY || a[k][i] == Double.NEGATIVE_INFINITY) && v[k] ==0.0) ||
                    //                        ((v[k] == Double.NEGATIVE_INFINITY || v[k] == Double.NEGATIVE_INFINITY) && a[k][i] ==0.0))
                    //  s +=0
//                    else
                    s += a[k][i] * v[k]

                }
                m.add(i, s)
                s = 0.0
            }
        }

        return m

    }

    fun times_mat_scal(a: MutableList<MutableList<Double>>, n: Double): MutableList<MutableList<Double>> {

        for (i in 0 until a.size) {
            for (j in 0 until a.size)
                a[i][j] *= n
        }
        return a

    }

    fun times_vect_scal(v: MutableList<Double>, n: Double): MutableList<Double> {
        for (i in 0 until v.size)
            v[i] *= n
        return v
    }

    /**
     * mine of two vectors A+B
     */
    fun mine_vec(a: List<Double>, b: List<Double>): MutableList<Double> {

        val m: MutableList<Double> = mutableListOf()

        if (a.size == b.size) {
            for (i in 0 until a.size)
                if (a[i] == b[i])
                    m.add(i, 0.0)
                else
                    m.add(i, a[i] - b[i])

            return m

        }
        return m

    }

    /***
     * check if all matrix lines have the same size
     */
    fun check_format(m: List<List<Double>>): Boolean {
        for (i in 0 until m.size) {
            return m[i].size == m[0].size

        }
        return false
    }


    fun eyes(n: Int): MutableList<MutableList<Double>> {

        var tem = mutableListOf<Double>()
        val m = mutableListOf<MutableList<Double>>()
        if (n > 0) {
            for (i in 0 until n) {
                for (j in 0 until n) {
                    if (i == j)
                        tem.add(j, 1.0)
                    else
                        tem.add(j, 0.0)
                }

                m.add(i, tem)
                tem = mutableListOf()
            }
        }
        return m
    }

    fun zeros(n: Int): MutableList<MutableList<Double>> {

        var tem = mutableListOf<Double>()
        val m = mutableListOf<MutableList<Double>>()
        if (n > 0) {
            for (i in 0 until n) {
                for (j in 0 until n)
                    tem.add(j, 0.0)
                m.add(i, tem)
                tem = mutableListOf()
            }
        }
        return m
    }

    /***
     * function for calculate matrix determinant
     */
    fun det(m: List<List<Double>>): Double {

        if (!check_format(m) || m.size != m[0].size) {

            return 0.0
        } else {
            var s = 0.0
            return if (m.size == 1)
                m[0][0]
            else
                if (m.size == 2)
                    return m[0][0] * m[1][1] - m[1][0] * m[0][1]
                else {
                    for (i in 0 until m.size) {
                        s += Math.pow(-1.0, i + 0.0) * m[0][i] * det(mineur(m, 0, i))
                    }
                    return s
                }


        }
    }

    /***
     * function for calculate the principal mineaur
     */
    fun mineur(a: List<List<Double>>, n: Int = 0, l: Int = 0): MutableList<MutableList<Double>> {

        var tem = mutableListOf<Double>()
        val m = mutableListOf<MutableList<Double>>()

        if (a.size == 2) {
            return when {
                l == n -> if (l == 0)
                    mutableListOf(mutableListOf(a[1][1]))
                else
                    mutableListOf(mutableListOf(a[0][0]))
                n == 0 -> mutableListOf(mutableListOf(a[1][0]))
                else -> mutableListOf(mutableListOf(a[0][1]))
            }
        } else


            if (a.size >= 3) {
                var k = 0
                var s = 0
                for (i in 0 until a.size) {
                    if (i == n) {
                        s = 1
                        continue
                    }
                    for (j in 0 until a.size) {

                        if (j == l)
                            k = 1
                        else {
                            tem.add(j - k, a[i][j])

                        }
                    }

                    m.add(i - s, tem)


                    tem = mutableListOf()
                    k = 0
                }

                return m

            }
        return a as MutableList<MutableList<Double>>
    }

    /***
     * calcul matrix transpose
     */

    fun trans(a: List<List<Double>>): MutableList<MutableList<Double>> {
        var tem = mutableListOf<Double>()
        val m = mutableListOf<MutableList<Double>>()

        for (i in 0 until a.size) {
            for (j in 0 until a.size)
                tem.add(j, a[j][i])
            m.add(i, tem)
            tem = mutableListOf()
        }
        return m
    }

    /***
     * //tem.add(j, detco(mineur(a, i, j), j.toDouble(), i.toDouble(), a.size))
     * inverse matrix
     */
    fun inv(a: List<List<Double>>): MutableList<MutableList<Double>> {
        var tem = mutableListOf<Double>()
        var m = mutableListOf<MutableList<Double>>()
        val d = det(a)
        if (d != 0.0) {
            for (i in 0 until a.size) {
                for (j in 0 until a.size)
                    tem.add(j, Math.pow(-1.0, i + j + 0.0) * det(mineur(a, i, j)))

                m.add(i, tem)
                tem = mutableListOf()

            }
            m = trans(m)

            m = times_mat_scal(m, 1.0 / d)
        }
        return m

    }


    fun phase(m: List<List<Double>>, el: List<Int>): MutableList<List<Double>> {
        val l = m.toMutableList()
        for (i in 0 until m.size)
            l[i] = m[i].filterIndexed { index, it -> index !in el }

        return l
    }

}