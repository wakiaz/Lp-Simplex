package dz.com.wakiaz.optimisationlineaire

class Linear {

    private val matr = Oper()


    /***
     * simplex algorithm for resolve linear program
     */

    /**
     * function simplex 1 it's phase 1 for two phase methode
     * @param1 matrix A
     * @type list of double list
     * @param2 vector b
     * @type list of Double
     * @param3 pos the list of line numbers with >=
     * @return list of element for pass to phase2
     *
     */
    fun simplex1(a: MutableList<List<Double>>, b: List<Double>, v: Int, pos: List<Int>):
            Pair<Pair<MutableList<List<List<Any>>>, Boolean>, List<Any>> {

        /**
         * tables simplex variables
         * c_b : basic variables  coefficients in z function
         * c_h : non basic variables coefficients in z function
         * j_b : ensemble of index for basic element
         * j_h : ensemble of index for basic element
         * a_b : basic matrix
         * a_h : non basic matrix
         * x_b : vector for solutions
         * b : vector b define Ax<=b
         * z : max value of objective function
         * ele : for the indexes who will be delete in phase 2
         */

        var theta: MutableList<Double>
        var e_j: MutableList<Double>
        val x_b = mutableListOf<Double>()
        val c_b = mutableListOf<Double>()
        val c_h = matr.zeros(a[0].size)[0]
        val j_b = mutableListOf<Int>()
        val j_h = mutableListOf<Int>()
        var a_b: MutableList<MutableList<Double>> = mutableListOf(mutableListOf())
        var a_h = a as MutableList<MutableList<Double>>
        var b = b
        var z = 0.0
        val ele = mutableListOf<Int>()
        val result = mutableListOf<List<List<Any>>>()
        var tab_main = mutableListOf<List<Any>>()
        var success = true
        var i_0: Int
        var j_0: Int

        /**
         * initial x_b by zero for index non basic
         * and j_h by 0 to number of variable (a[0].size)
         */
        for (i in 0 until a[0].size) {
            x_b.add(i, 0.0)
            j_h.add(i, i)
        }

        //initial c_b, j_b, ele
        var l = 0
        val k = mutableListOf<String>()
        for (i in 0 until b.size) {
            x_b.add(a[0].size + i, b[i])

            if (i in pos) {
                c_b.add(i, -1.0)
                j_b.add(i, a[0].size + b.size - pos.size + i)
                ele.add(i, a[0].size + b.size - pos.size + i)
                k.add("X${a[0].size + b.size - pos.size + i + 1}")
            }
            else {
                c_b.add(i, 0.0)
                j_b.add(i, a[0].size + l)
                k.add("X${a[0].size + l + 1}")
                l++
            }

            //calculate z for step 1 every time = 0
            z += c_b[i] * b[i]
        }
        val jb_in = j_b.toList()
        /**
         * calculate e_j vector for optimum criteria e_j <=0 for each j
         * we use the formula ej = c_h - c_b * a_h
         * if you use the formula ej = c_b * a_h - c_h
         * your e_j will be the mines of the mine
         * and optimum criteria will be e_j >=0
         */
        e_j = matr.mine_vec(c_h, matr.multv_a(c_b, a_h))
        a_b = matr.eyes(b.size)

        theta = mutableListOf()

        //j_0 is the index of enter variable
        j_0 = e_j.indexOf(e_j.max()!!)

        for (i in 0 until a.size) {
            if (a_h[i][j_0] <= 0)
                theta.add(i, Double.POSITIVE_INFINITY)
            else
                theta.add(i, b[i].div(a_h[i][j_0]))

        }

        if (theta.all { it == Double.POSITIVE_INFINITY }) {
            success = false

        }

        //result.add(listOf(a_b, a_h, b, c_b, c_h, x_b, z, j_b, j_h, ele))

        for (i in 0 until b.size) {
            tab_main.add(listOf(listOf(k[i], c_b[i], b[i]), a_b[i], a_h[i], listOf(theta[i])).flatten())
        }
        result.add(
            listOf(
                listOf(c_b, c_h).flatten(),
                listOf(j_b, j_h).flatten(),
                tab_main,
                listOf(listOf(z), matr.zeros(b.size)[0], e_j).flatten(),
                listOf(x_b.subList(0, v), z, "Simplex Phase I")
            )
        )

        while (e_j.max()!! > 0.0 && success) {


            //i_0 is the index of variable outlet
            i_0 = theta.indexOf(theta.min())

            // a_b = matr.eyes(b.size)
            for (i in 0 until a_b.size)
                a_b[i][i_0] = a_h[i][j_0]

            val ab_inv = matr.inv(a_b)



            a_h = matr.mult(ab_inv, a_h) as MutableList<MutableList<Double>>

            for (i in 0 until a_h.size)
                a_h[i][j_0] = ab_inv[i][i_0]

            b = matr.multv(ab_inv, b)
            val swap1 = c_b[i_0]
            c_b[i_0] = c_h[j_0]
            c_h[j_0] = swap1


            k[k.indexOf("X${j_b[i_0] + 1}")] = "X${j_h[j_0] + 1}"
            z = 0.0

            for (i in 0 until j_b.size) {
                if (i == i_0) {
                    val swap = j_b[i]
                    j_b[i] = j_h[j_0]
                    j_h[j_0] = swap
                }
                z += c_b[i] * b[i]
            }


            for (i in 0 until x_b.size) {
                if (i in j_b) {
                    x_b[i] = b[j_b.indexOf(i)]

                }
                if (i in j_h)
                    x_b[i] = 0.0
            }
            a_b = matr.eyes(b.size)
            e_j = matr.mine_vec(c_h, matr.multv_a(c_b, a_h))

            theta = mutableListOf()

            //j_0 is the index of enter variable
            j_0 = e_j.indexOf(e_j.max()!!)

            for (i in 0 until a.size) {
                if (a_h[i][j_0] <= 0)
                    theta.add(i, Double.POSITIVE_INFINITY)
                else
                    theta.add(i, b[i].div(a_h[i][j_0]))

            }

            if (theta.all { it == Double.POSITIVE_INFINITY }) {
                success = false

            }


            tab_main = mutableListOf<List<Any>>()
            for (i in 0 until b.size) {
                tab_main.add(listOf(listOf(k[i], c_b[i], b[i]), a_b[i], a_h[i], listOf(theta[i])).flatten())
            }
            result.add(
                listOf(
                    listOf(c_b, c_h).flatten(),
                    listOf(j_b, j_h).flatten(),
                    tab_main,
                    listOf(listOf(z), matr.zeros(b.size)[0], e_j).flatten(),
                    listOf(x_b.subList(0, v), z, "Simplex Phase I")
                )
            )


            //result.add(listOf(a_b, a_h, b, c_b, c_h, x_b, z, j_b, j_h, ele))

        }
        if (e_j.max()!! <= 0)
            success = true

        for (i in jb_in.subList(0, pos.size)) {
            if (i in j_b)
                success = false
        }


        return Pair(Pair(result, success), listOf(a_b, a_h, b, c_b, c_h, x_b, z, j_b, j_h, ele))
    }

    /**
     * Big M  simplex method
     *@param1 matrix A
     * @type list of double list
     * @param2 vector b
     * @type list of Double
     * @param3 vector c the list of variables coefficients in objective function
     **/
    fun big_M(
        a: MutableList<List<Double>>,
        b: List<Double>,
        c: List<Double>,
        ele: List<Int>,
        v: Int
    ): Pair<MutableList<List<List<Any>>>, Boolean> {

        /**
         * tables simplex variables
         * c_b : basic variables  coefficients in z function
         * c_h : non basic variables coefficients in z function
         * j_b : ensemble of index for basic element
         * j_h : ensemble of index for basic element
         * a_b : basic matrix
         * a_h : non basic matrix
         * x_b : vector for solutions
         * b : vector b define Ax<=b
         * z : max value of objective function
         * ele : for the indexes who will be delete in phase 2
         */
        var theta: MutableList<Double>
        var e_j: MutableList<Double>
        val x_b = mutableListOf<Double>()
        val c_b = mutableListOf<Double>()
        val c_h = c as MutableList
        val j_b = mutableListOf<Int>()
        val j_h = mutableListOf<Int>()
        val result = mutableListOf<List<List<Any>>>()
        var a_h = a as MutableList<MutableList<Double>>
        var b = b
        var tab_main = mutableListOf<List<Any>>()
        var z = 0.0
        var i_0: Int
        var j_0: Int
        var success = true


        for (i in 0 until a[0].size) {
            x_b.add(i, 0.0)
            j_h.add(i, i)
        }

        //we take big M = -1000000000000000000000000000000000000000000.0
        /**
         * @TODO create sym function for M
         */
        var l = 0
        val k = mutableListOf<String>()
        for (i in 0 until b.size) {
            x_b.add(a[0].size + i, b[i])
            //c_b.add(i, Double.NEGATIVE_INFINITY)
            if (i in ele) {

                c_b.add(i, -1000000000000000000000000000000000000000000.0)
                j_b.add(i, a[0].size + b.size - ele.size + i)
                k.add("X${a[0].size + b.size - ele.size + i + 1}")
            } else {
                c_b.add(i, 0.0)
                j_b.add(i, a[0].size + l)
                k.add("X${a[0].size + l + 1}")
                l++
            }


            //j_b.add(i, a[0].size + i)
            z += c_b[i] * b[i]
        }

        val j_bin = j_b.toList()

//        val k = mutableListOf<String>()
        for (i in 0 until x_b.size) {
            if (i in j_b) {
                x_b[i] = b[j_b.indexOf(i)]
                //k.add(j_b.indexOf(i), "X${i + 1}")
            } else
                x_b[i] = 0.0
        }
        e_j = matr.mine_vec(c_h, matr.multv_a(c_b, a_h))
        var a_b = matr.eyes(b.size)

        theta = mutableListOf()

        //j_0 is the index of enter variable
        j_0 = e_j.indexOf(e_j.max()!!)

        for (i in 0 until a.size) {
            if (a_h[i][j_0] <= 0)
                theta.add(i, Double.POSITIVE_INFINITY)
            else
                theta.add(i, b[i].div(a_h[i][j_0]))

        }

        if (theta.all { it == Double.POSITIVE_INFINITY }) {
            success = false

        }


        // result.add(listOf("Big M",z, x_b, b, a_h, a_b, j_b, c_b, c_h, j_h))
        for (i in 0 until b.size) {
            tab_main.add(listOf(listOf(k[i], c_b[i], b[i]), a_b[i], a_h[i], listOf(theta[i])).flatten())
        }
        result.add(
            listOf(
                listOf(c_b, c_h).flatten(),
                listOf(j_b, j_h).flatten(),
                tab_main,
                listOf(listOf(z), matr.zeros(b.size)[0], e_j).flatten(),
                listOf(x_b.subList(0, v), z, "Big M method")
            )
        )

        while (e_j.max()!! > 0.0 && success) {


            i_0 = theta.indexOf(theta.min())


            for (i in 0 until a_b.size)
                a_b[i][i_0] = a_h[i][j_0]

            val ab_inv = matr.inv(a_b)

            a_h = matr.mult(ab_inv, a_h) as MutableList<MutableList<Double>>

            for (i in 0 until a_h.size)
                a_h[i][j_0] = ab_inv[i][i_0]

            b = matr.multv(ab_inv, b)

            c_b[i_0] = c_h[j_0]
            c_h[j_0] = -1000000000000000000000000000000000000000000.0

            k[k.indexOf("X${j_b[i_0] + 1}")] = "X${j_h[j_0] + 1}"

            z = 0.0
            for (i in 0 until j_b.size) {
                if (i == i_0) {
                    val swap = j_b[i]
                    j_b[i] = j_h[j_0]
                    j_h[j_0] = swap
                }
                z += c_b[i] * b[i]
            }


            for (i in 0 until x_b.size) {
                if (i in j_b) {
                    x_b[i] = b[j_b.indexOf(i)]

                }
                if (i in j_h)
                    x_b[i] = 0.0
            }
            a_b = matr.eyes(b.size)
            e_j = matr.mine_vec(c_h, matr.multv_a(c_b, a_h))

            theta = mutableListOf()

            j_0 = e_j.indexOf(e_j.max()!!)

            for (i in 0 until a.size) {
                if (a_h[i][j_0] <= 0)
                    theta.add(i, Double.POSITIVE_INFINITY)
                else
                    theta.add(i, b[i].div(a_h[i][j_0]))
            }

            if (theta.all { it == Double.POSITIVE_INFINITY }) {

                success = false
            }

            //result.add(listOf(z, x_b, b, a_h, a_b, j_b, c_b, c_h, j_h))

            tab_main = mutableListOf<List<Any>>()
            for (i in 0 until b.size) {
                tab_main.add(listOf(listOf(k[i], c_b[i], b[i]), a_b[i], a_h[i], listOf(theta[i])).flatten())
            }
            result.add(
                listOf(
                    listOf(c_b, c_h).flatten(),
                    listOf(j_b, j_h).flatten(),
                    tab_main,
                    listOf(listOf(z), matr.zeros(b.size)[0], e_j).flatten(),
                    listOf(x_b.subList(0, v), z, "Big M method")
                )
            )

        }
        if (e_j.max()!! <= 0)
            success = true
        for (i in j_bin.subList(0, ele.size)) {
            if (i in j_b)
                success = false
        }


        //return listOf(z, x_b, b, a_h, a_b, j_b, c_b, c_h, j_h)
        return Pair(result, success)
    }

    /**
     * function simplex phase two and simplex
     */
    fun simplex2(
        a: MutableList<List<Double>>,
        b: List<Double>,
        c: List<Double>,
        v: Int,
        f: Boolean = false,
        Jb: List<Int> = listOf(0),
        cb: List<Double> = listOf(0.0),
        x: List<Double> = listOf(0.0),
        jh: List<Int> = listOf(0)
    ): Pair<MutableList<List<List<Any>>>, Boolean> {

        var theta: MutableList<Double>
        var e_j: MutableList<Double>
        var x_b = mutableListOf<Double>()
        var c_b = mutableListOf<Double>()
        val c_h = c as MutableList
        var j_b = mutableListOf<Int>()
        var j_h = mutableListOf<Int>()
        val result = mutableListOf<List<List<Any>>>()
        var tab_main = mutableListOf<List<Any>>()
        var sms = "La methode Simplex "
        var i_0: Int
        var j_0: Int
        var success = true
        if (f)
            sms = "Simplex Phase II"


        var a_h = a as MutableList<MutableList<Double>>
        var b = b
        var z = 0.0
        val k = mutableListOf<String>()

        for (i in 0 until a[0].size) {
            x_b.add(i, 0.0)
            j_h.add(i, i)
        }

        for (i in 0 until b.size) {

            x_b.add(a[0].size + i, b[i])
            c_b.add(i, 0.0)
            j_b.add(i, a[0].size + i)
            k.add("X${i + 1}")
        }

        if (f) {
            j_b = Jb.toMutableList()
            c_b = cb.toMutableList()
            j_h = jh.toMutableList()
        }



        for (i in 0 until x_b.size) {
            if (i in j_b) {
                x_b[i] = b[j_b.indexOf(i)]
                k[j_b.indexOf(i)] = "X${i + 1}"
            } else
                x_b[i] = 0.0
        }

        if (f)
            x_b = x.toMutableList()


        var a_b = matr.eyes(b.size)

        e_j = matr.mine_vec(c_h, matr.multv_a(c_b, a_h))

        theta = mutableListOf()

        j_0 = e_j.indexOf(e_j.max()!!)

        for (i in 0 until a.size) {
            if (a[i][j_0] <= 0)
                theta.add(i, Double.POSITIVE_INFINITY)
            else
                theta.add(i, b[i].div(a[i][j_0]))
        }

        if (theta.all { it == Double.POSITIVE_INFINITY }) {

            success = false

        }

        i_0 = theta.indexOf(theta.min())


        for (i in 0 until b.size) {
            tab_main.add(listOf(listOf(k[i], c_b[i], b[i]), a_b[i], a_h[i], listOf(theta[i])).flatten())
            z += c_b[i] * b[i]
        }
        result.add(
            listOf(
                listOf(c_b, c_h).flatten(),
                listOf(j_b, j_h).flatten(),
                tab_main,
                listOf(listOf(z), matr.zeros(b.size)[0], e_j).flatten(),
                listOf(x_b.subList(0, v), z, sms)
            )
        )


        while (e_j.max()!! > 0.0 && success) {


            for (i in 0 until a_b.size)
                a_b[i][i_0] = a_h[i][j_0]

            val ab_inv = matr.inv(a_b)
            a_h = matr.mult(ab_inv, a_h) as MutableList<MutableList<Double>>

            for (i in 0 until a_h.size)
                a_h[i][j_0] = ab_inv[i][i_0]

            b = matr.multv(ab_inv, b)
            val swap1 = c_b[i_0]
            c_b[i_0] = c_h[j_0]
            c_h[j_0] = swap1

            k[k.indexOf("X${j_b[i_0] + 1}")] = "X${j_h[j_0] + 1}"

            z = 0.0
            for (i in 0 until j_b.size) {
                if (i == i_0) {
                    val swap = j_b[i]
                    j_b[i] = j_h[j_0]
                    j_h[j_0] = swap
                }
                z += c_b[i] * b[i]
            }




            for (i in 0 until x_b.size) {
                if (i in j_b) {
                    x_b[i] = b[j_b.indexOf(i)]

                }
                if (i in j_h)
                    x_b[i] = 0.0
            }

            a_b = matr.eyes(b.size)
            e_j = matr.mine_vec(c_h, matr.multv_a(c_b, a_h))

            theta = mutableListOf()

            j_0 = e_j.indexOf(e_j.max()!!)

            for (i in 0 until a.size) {
                if (a[i][j_0] <= 0)
                    theta.add(i, Double.POSITIVE_INFINITY)
                else
                    theta.add(i, b[i].div(a[i][j_0]))
            }

            if (theta.all { it == Double.POSITIVE_INFINITY }) {

                success = false

            }

            i_0 = theta.indexOf(theta.min())


            tab_main = mutableListOf()

            for (i in 0 until b.size) {
                tab_main.add(listOf(listOf(k[i], c_b[i], b[i]), a_b[i], a_h[i], listOf(theta[i])).flatten())
            }
            result.add(
                listOf(
                    listOf(c_b, c_h).flatten(),
                    listOf(j_b, j_h).flatten(),
                    tab_main,
                    listOf(listOf(z), matr.zeros(b.size)[0], e_j).flatten(),
                    listOf(x_b.subList(0, v), z, sms)
                )
            )

        }
        if (e_j.max()!! <= 0)
            success = true



        return Pair(result, success)
    }

    /**
     * TODO
     * branch and bound method
     * Gomory method
     */

}