package dz.com.wakiaz.optimisationlineaire


import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity


class SimplexTab : AppCompatActivity() {

    private lateinit var table1: TableLayout
    private lateinit var listRaw: MutableList<TableRow>
    private lateinit var listTab: MutableList<TableLayout>
    private lateinit var phasebutt: Button
    private lateinit var scrollView: ScrollView
    private lateinit var horizontalScrollView: HorizontalScrollView
    private lateinit var lin1: LinearLayout


    private lateinit var parTab: TableLayout.LayoutParams
    private lateinit var row: TableRow
    private lateinit var linear: LinearLayout


    private var A = mutableListOf<List<Double>>()
    private var b = mutableListOf<Double>()
    private var c = mutableListOf<Double>()
    private lateinit var c1: MutableList<Double>
    private var ftype = "Max z ="
    private var twophase = false
    private lateinit var oper: Oper
    private val bt = mutableListOf<Int>()
    private var ele = mutableListOf<Int>()

    private lateinit var zres: MutableList<List<List<Any>>>
    private var n = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.draw_table)

        linear = findViewById(R.id.simplex)
        linear.orientation = LinearLayout.VERTICAL
        linear.setBackgroundColor(Color.parseColor("#03DAC5"))
        lin1 = LinearLayout(this)
        lin1.orientation = LinearLayout.VERTICAL

        table1 = TableLayout(this)

        phasebutt = Button(this)
        horizontalScrollView = HorizontalScrollView(this)
        scrollView = ScrollView(this)
        scrollView.setBackgroundColor(Color.parseColor("#03DAC5"))


        parTab = TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT)

        row = TableRow(this)

        val l = Linear()

        //take data input from user
        A = intent.extras!!["A"] as MutableList<List<Double>>
        b = intent.extras!!["b"] as MutableList<Double>
        c = intent.extras!!["c"] as MutableList<Double>
        val vars = intent.extras!!["vars"] as List<String>

        c1 = c.toMutableList()
        val b1 = b.toList()


        n = c.size
        ftype = intent.extras!!["ftype"].toString()
        val btype = intent.extras!!["btype"] as List<String>

        var A1 = mutableListOf<List<Double>>()
        var success = true

        var c2 = c1.toList()


        twophase = btype.contains(">=")
        if (twophase) {
            A1 = standard(ftype, btype, A, vars, twophase)
            if (ftype == "Min z =") {
                oper = Oper()
                c1 = oper.times_vect_scal(c1, -1.0)

            }
            c2 = c1.toList()

            val res = l.big_M(A1, b, c1, ele, n)
            zres = res.first
            success = res.second
        }
        else {
            A1 = standard(ftype, btype, A, vars)
            if (ftype == "Min z =") {
                oper = Oper()
                c1 = oper.times_vect_scal(c1, -1.0)

            }
            val res = l.simplex2(A1, b, c1, n)
            success = res.second
            zres = res.first
        }

        drawTable(zres, twophase, success)

        /**
         * @TODO create function for this code
         * button for show two phase method
         */
        phasebutt.setOnClickListener {
            val res = l.simplex1(A1, b1, n, ele)
            val f2send = res.second
            val temp = res.first.first
            linear = LinearLayout(this)
            linear.orientation = LinearLayout.VERTICAL
            val l1 = drawTable(temp)
            linear = LinearLayout(this)
            linear.orientation = LinearLayout.VERTICAL

            lin1.addView(l1)
            if (res.first.second) {

                val jb = f2send[7] as List<Int>
                val jh = f2send[8] as List<Int>
                val cb = mutableListOf<Double>()

                val bb = f2send[2] as List<Double>
                val chh = f2send[4] as List<Double>
                val ele1 = f2send[9] as List<Int>
                val ele = mutableListOf<Int>()
                val x = f2send[5] as List<Double>
                for (i in ele1) {
                    ele.add(jh.indexOf(i))
                    ele.add(jb.indexOf(i))
                }

                for (i in jb) {
                    //test c2 par c1
                    if (i < c2.size)
                        cb.add(c2[i])
                    else
                        cb.add(0.0)
                }

                val ch = mutableListOf<Double>()

                for (i in jh) {
                    if (i !in ele1) {
                        if (i >= c2.size)
                            ch.add(0.0)
                        else
                            ch.add(c2[i])
                    }
                }


                val a = oper.phase(f2send[1] as List<List<Double>>, ele)
                val xb = x.filterIndexed { index, it -> index !in ele1 }
                val jhh = jh.filter { it !in ele1 }

                // n = ch.size
                val f2res = l.simplex2(a, bb, ch, n, true, jb, cb, xb, jhh)

                val l2 = drawTable(f2res.first, false, f2res.second)
                lin1.addView(l2)
            } else {
                val err = TextView(this)
                err.setBackgroundColor(Color.parseColor("#D50000"))
                err.setPadding(50, 30, 50, 50)
                err.text = getString(R.string.nosolphase)
                err.textSize = 25f
                err.typeface = Typeface.DEFAULT_BOLD
                lin1.addView(err)
            }

            horizontalScrollView.addView(lin1)
            scrollView.addView(horizontalScrollView)
            setContentView(scrollView)
        }


    }

    //function for draw simplex tables
    private fun drawTable(res: List<List<List<Any>>>, twph: Boolean = false, sucss: Boolean = true): LinearLayout {

        var err = TextView(this)
        table1 = TableLayout(this)

        listRaw = mutableListOf()
        listTab = mutableListOf()

        table1.isStretchAllColumns = true
        table1.isShrinkAllColumns = true

        val parRow = TableRow.LayoutParams()
        parRow.span = 6
        parRow.setMargins(25, 5, 25, 5)

        parTab.setMargins(10, 10, 10, 10)
        if (ftype == "Min z =") {

            val varx = TextView(this)
            varx.text = getString(R.string.MinTitle)
            varx.textSize = 20f
            varx.setTextColor(Color.parseColor("#000000"))
            varx.typeface = Typeface.DEFAULT_BOLD
            varx.setPadding(30, 30, 5, 10)
            varx.gravity = Gravity.CENTER_HORIZONTAL
            linear.addView(varx)
        }
        val varx = TextView(this)

        varx.text = res[res.lastIndex][4][2].toString()
        varx.textSize = 20f
        varx.setTextColor(Color.parseColor("#000000"))
        varx.typeface = Typeface.DEFAULT_BOLD
        varx.setPadding(30, 30, 5, 10)
        varx.gravity = Gravity.CENTER_HORIZONTAL
        linear.addView(varx)

        for ((compt, j) in res.withIndex()) {

            table1 = TableLayout(this)

            val rowTitle = TableRow(this)
            rowTitle.gravity = Gravity.CENTER_HORIZONTAL
            rowTitle.setBackgroundColor(Color.parseColor("#3700b3"))
            val varx = TextView(this)

            varx.text = getString(R.string.iterationnb) + compt
            varx.typeface = Typeface.DEFAULT_BOLD
            varx.setTextColor(Color.parseColor("#FFFFFF"))
            rowTitle.addView(varx, parRow)
            table1.addView(rowTitle, parTab)

            for (i in 0 until 4) {
                when (i) {
                    0 -> {
                        val temp = j[0] as List<Double>
                        val text1 = TextView(this)
                        val text2 = TextView(this)
                        val text3 = TextView(this)
                        text1.gravity = Gravity.CENTER
                        text2.gravity = Gravity.CENTER
                        text3.gravity = Gravity.CENTER
                        text1.text = " "
                        text3.text = "C"
                        text2.text = " "
                        val row = TableRow(this)

                        row.addView(text1)
                        row.addView(text2)
                        row.addView(text3)
                        for (k in temp) {
                            val text = TextView(this)
                            text.text = k.toString()
                            text.gravity = Gravity.CENTER
                            text.setPadding(50, 5, 50, 5)
                            row.addView(text)

                        }
                        table1.addView(row)
                    }
                    1 -> {
                        val temp = j[1] as List<Double>
                        val text1 = TextView(this)
                        val text2 = TextView(this)
                        val text3 = TextView(this)
                        text1.gravity = Gravity.CENTER
                        text2.gravity = Gravity.CENTER
                        text3.gravity = Gravity.CENTER

                        text3.text = getString(R.string.xb)
                        text1.text = getString(R.string.cb)
                        text2.text = getString(R.string.b)
                        val row = TableRow(this)
                        row.addView(text3)
                        row.addView(text1)
                        row.addView(text2)

                        for (k in temp) {
                            val text = TextView(this)
                            text.text = "X${k.toInt() + 1}"
                            text.gravity = Gravity.CENTER
                            text.setPadding(50, 5, 50, 5)
                            row.addView(text)

                        }
                        val text = TextView(this)
                        text.text = "\u0398"
                        text.gravity = Gravity.CENTER
                        text.setPadding(50, 5, 50, 5)
                        row.addView(text)

                        listRaw.add(row)

                    }

                    2 -> {
                        //val temp = j[2] as List<List<Double>>
                        val temp = j[2] as List<List<Any>>
                        for (k in 0 until temp.size) {
                            val row = TableRow(this)

                            for (l in 0 until temp[0].size) {
                                val text = TextView(this)
                                val kk = temp[k][l].toString()
                                text.text = kk.filterIndexed { index, c -> index < kk.indexOf('.') + 3 }
                                text.gravity = Gravity.CENTER
                                text.setPadding(50, 5, 50, 5)
                                row.addView(text)
                            }
                            listRaw.add(row)

                        }

                    }

                    3 -> {

                        val temp = j[3] as List<Double>

                        val text1 = TextView(this)
                        text1.setBackgroundColor(Color.parseColor("#6200EE"))
                        text1.setTextColor(Color.parseColor("#FFFFFF"))

                        val text2 = TextView(this)
                        val text0 = TextView(this)

                        text0.setPadding(50, 5, 50, 5)
                        text1.setPadding(50, 5, 50, 5)
                        text2.setPadding(50, 5, 50, 5)

                        text0.text = " "
                        text1.text = "Z = ${temp[0]}"
                        text2.text = "Ej"

                        val row = TableRow(this)
                        row.addView(text1)
                        row.addView(text0)
                        row.addView(text2)

                        for (k in temp.filterIndexed { index, d -> index != 0 }) {

                            val kk = k.toString()
                            val text = TextView(this)
                            text.text = kk.filterIndexed { index, c -> index < kk.indexOf('.') + 3 }
                            text.gravity = Gravity.CENTER
                            text.setPadding(50, 5, 50, 5)
                            row.addView(text)

                        }
                        listRaw.add(row)

                    }

                }

            }

            for (i in listRaw)
                table1.addView(i)
            listRaw = mutableListOf()

            listTab.add(table1)

            if (!sucss) {
                err = TextView(this)
                err.setBackgroundColor(Color.parseColor("#D50000"))
                err.setPadding(50, 30, 50, 50)
                err.text = getString(R.string.nosol)
                err.textSize = 25f
                err.typeface = Typeface.DEFAULT_BOLD

            }


        }
        val textView = TextView(this)
        val z = res[res.lastIndex][4][1] as Double
        if (ftype == "Min z =")
            textView.text = "X* = " + res[res.lastIndex][4][0] + ",  Z* = " + (z * -1)
        else
            textView.text = "X* = ${res[res.lastIndex][4][0]},  Z* = $z"
        textView.typeface = Typeface.DEFAULT_BOLD
        textView.gravity = Gravity.START
        textView.setPadding(20, 10, 20, 50)

        val textView2 = TextView(this)
        textView2.text = getString(R.string.finresultat)
        textView2.typeface = Typeface.DEFAULT_BOLD
        textView2.setTextColor(Color.parseColor("#FFFFFF"))
        textView2.setBackgroundColor(Color.parseColor("#3700b3"))
        textView2.textSize = 15f
        textView2.gravity = Gravity.START
        textView2.setPadding(20, 20, 20, 20)

        for (i in listTab)
            linear.addView(i)

        if (sucss) {
            linear.addView(textView2)
            linear.addView(textView)
        } else
            linear.addView(err)

        if (twph) {
            phasebutt.text = getString(R.string.twophase)
            phasebutt.setPadding(40, 50, 40, 50)
            phasebutt.gravity = Gravity.CENTER_HORIZONTAL
            phasebutt.width = LinearLayout.LayoutParams.WRAP_CONTENT
            phasebutt.height = 80
            phasebutt.setBackgroundColor(Color.parseColor("#3d00e0"))
            phasebutt.setTextColor(Color.parseColor("#FFFFFF"))
            linear.addView(phasebutt)

        }

        return linear

    }

    //function for take standard format for two phase and big M methods
    private fun standard(
        ftype: String, btype: List<String>, A: List<List<Double>>,
        vars: List<String>, phas2: Boolean = false
    ):
            MutableList<List<Double>> {

//      var At = mutableListOf<List<Double>>()
        var At = A.toMutableList()
        oper = Oper()
        /**
        if (ftype == "Min z =") {
        c = oper.times_vect_scal(c, -1.0)
        }*/
        var l = 0
        for (i in btype) {
            if (i == ">=")
                bt.add(l)
            if (i == ">=" || i == "=")
                ele.add(l)

            l++
        }
        /**
         * @TODO fix c error
         */
        var idx = 0

        if ("\u220A \u211D" in vars || "<= 0" in vars) {

            val A2t = A as MutableList<MutableList<Double>>
            for (i in 0 until A.size) {
                idx = 0

                l = 0
                while (idx < vars.size) {
                    when {
                        vars[idx] == "\u220A \u211D" -> {
                            A2t[i].add(idx + l + 1, A[i][idx + l] * -1)
                            if (c1.size <= A2t[0].size && i == 0)
                                c1.add(idx + l + 1, c[idx] * -1)
                            idx++
                            l++

                        }
                        vars[idx] == "<= 0" -> {
                            A2t[i][idx + l] = A[i][idx + l] * -1
                            if (c1.size <= A2t[0].size && i == 0)
                                c1[idx + l] = c[idx] * -1
                            idx++

                        }
                        else -> idx++
                    }

                }
            }
            n = c1.size
            //test remplace A By A2t
            if (phas2) {
                At = mutableListOf()
                l = 0
                for (i in 0 until A2t.size) {

                    if (i in bt) {
                        val temp = oper.times_vect_scal(oper.eyes(bt.size)[l], -1.0)
                        At.add(listOf(A2t[i], temp).flatten())
                        c1.add(0.0)


                        l++
                    } else {
                        At.add(listOf(A2t[i], oper.zeros(bt.size)[0]).flatten())

                    }
                }
                // n = c1.size
            }
        } else
            if (phas2) {
                l = 0
                At = mutableListOf()
                //test remplace A By A2t
                for (i in 0 until A.size) {

                    if (i in bt) {

                        val temp = oper.times_vect_scal(oper.eyes(bt.size)[l], -1.0)
                        At.add(listOf(A[i], temp).flatten())
                        c1.add(0.0)
                        l++

                    } else {
                        At.add(listOf(A[i], oper.zeros(bt.size)[0]).flatten())

                    }
                }
            }


        return At
    }


}
