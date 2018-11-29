package dz.com.wakiaz.optimisationlineaire

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

import java.util.*
import java.util.regex.Pattern


class TakeVal : AppCompatActivity() {

    private lateinit var spinner1: Spinner
    private lateinit var linear: LinearLayout
    private lateinit var linear2: LinearLayout
    private lateinit var post: LinearLayout
    private lateinit var par: LinearLayout.LayoutParams
    private lateinit var par2: LinearLayout.LayoutParams
    private var i = 2
    private var j = 2
    private lateinit var edit: EditText
    //default spinner list value for vector b
    private val ope_val = listOf("<=", ">=", "=")
    private val vars = listOf(">= 0", "<= 0", "\u220A \u211D")

    // default spinner list value for function type
    private val type_fun = listOf("Max z =", "Min z =")

    //linear system element
    private var A = mutableListOf<MutableList<Double>>()
    private var b = mutableListOf<Double>()
    private var c = mutableListOf<Double>()
    private var ztype = "Max z ="
    private val btype = mutableListOf<String>()
    private val varsl = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_val)

        //take parameter from main activity
        i = intent.extras!!["i"]!!.toString().toInt()
        j = intent.extras!!["j"]!!.toString().toInt()

        //initial linear layout and set orientation
        linear = LinearLayout(this)
        linear.orientation = LinearLayout.HORIZONTAL

        linear2 = LinearLayout(this)
        linear2.orientation = LinearLayout.VERTICAL


        //parent layout
        post = findViewById(R.id.ppos)

        //set default layout parameters
        par = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        par.setMargins(65, 10, 25, 30)

        //set objective function layout parameter
        par2 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        par2.setMargins(45, 10, 25, 30)

        for (l in 0 until j)
            btype.add("<=")

        for (l in 0 until i)
            varsl.add(">= 0")

        run(j, i)
        element()

        for (l in 1..j) {
            spinner1 = findViewById(l * 10 + i + 1)
            spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    btype[l - 1] = parent.getItemAtPosition(position) as String
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
        }
        /**
         * take negative variables mark from user
         */
        for (k in 1..i) {
            spinner1 = findViewById((j + 1) * 20 + k)
            spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                    varsl[k - 1] = parent.getItemAtPosition(position) as String
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

        }
        spinner1 = findViewById(0)
        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                ztype = parent.getItemAtPosition(position) as String

            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        val butt = findViewById<Button>(R.id.run)
        butt.setOnClickListener {
            if (element()) {
                //send intent

                val intent = Intent(this, SimplexTab::class.java)

                intent.putExtra("A", A as ArrayList<*>)
                intent.putExtra("b", b as ArrayList<Double>)
                intent.putExtra("c", c as ArrayList<Double>)
                intent.putExtra("btype", btype as ArrayList<String>)
                intent.putExtra("ftype", ztype)
                intent.putExtra("vars", varsl as ArrayList<Int>)

                startActivity(intent)
                // finish()

            } else
                Toast.makeText(this, "one or more field are empty or not correct number!!", Toast.LENGTH_SHORT).show()


        }

    }

    //design layout
    private fun run(i: Int, j: Int) {
        var idv: Int
        var idvs: Int

        val texttile = TextView(this)
        texttile.text = " Ecriver votre PL"
        texttile.textSize = 16f
        texttile.typeface = Typeface.DEFAULT_BOLD
        post.addView(texttile)

        //make objective function
        for (k in 0..j) {

            if (k == 0) {
                val spinner = Spinner(this)
                spinner.id = 0
                //spinner.id = View.generateViewId()

                spinner.onItemSelectedListener
                val arrayAdapter = ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, type_fun)
                spinner.adapter = arrayAdapter
                linear.addView(spinner)
            } else {
                val editText = EditText(this)
                editText.inputType = 3
                editText.id = k
                //editText.id = View.generateViewId()
                linear.addView(editText)

                val textView = TextView(this)
                if (k == j)
                    textView.text = "X$k"
                else
                    textView.text = "X$k+"
                linear.addView(textView)


            }


        }
        post.addView(linear, par2)
        //post.addView(linear2)

        //make contraints layouts
        for (k in 1..i) {
            idv = 10 * k
            linear = LinearLayout(this)
            for (l in 1..j) {

                val textView = TextView(this)
                val editText = EditText(this)

                editText.inputType = 3
                editText.id = idv + l
                //  editText.id = View.generateViewId()
                linear.addView(editText)

                if (l == j)
                    textView.text = "X$l"
                else
                    textView.text = "X$l+"

                linear.addView(textView)


                if (l == j) {
                    val spinner = Spinner(this)
                    spinner.id = idv + l + 1
                    //spinner.id = View.generateViewId()
                    spinner.onItemSelectedListener
                    val arrayAdapter =
                        ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, ope_val)
                    spinner.adapter = arrayAdapter

                    linear.addView(spinner)
                    val editText2 = EditText(this)
                    editText2.inputType = 3

                    editText2.id = idv + l + 2
                    //editText.id = View.generateViewId()
                    linear.addView(editText2)
                }
            }

            post.addView(linear, par)

        }
        val textvars = TextView(this)
        textvars.text = "preisez le signe des variables"
        textvars.typeface = Typeface.DEFAULT_BOLD
        textvars.textSize = 16f
        post.addView(textvars)

        val linear3 = LinearLayout(this)
        linear3.orientation = LinearLayout.HORIZONTAL
        for (l in 1..j) {
            linear = LinearLayout(this)
            //linear2 = LinearLayout(this)
            //linear2.orientation = LinearLayout.VERTICAL
            //create variables bound layout
            idvs = (i + 1) * 20 + l

            val textvs = TextView(this)
            textvs.text = " X$l"

            val spinnervs = Spinner(this)
            spinnervs.id = idvs
            spinnervs.onItemSelectedListener
            val arrayAdapter = ArrayAdapter<String>(
                this,
                R.layout.support_simple_spinner_dropdown_item, vars
            )
            spinnervs.adapter = arrayAdapter

            linear.addView(textvs)
            linear.addView(spinnervs)
            linear2.addView(linear)
            if (l.rem(2) == 0) {
                //linear3 = LinearLayout(this)

                linear3.addView(linear2)
                linear2 = LinearLayout(this)
                linear2.orientation = LinearLayout.VERTICAL

            }
            if (j.rem(2) == 1 && l == j)
                linear3.addView(linear2)
            //number of variable great then 2 !!

        }
        post.addView(linear3)


    }

    //take system elements
    private fun element(): Boolean {
        //initial A, B and C
        A = mutableListOf()
        b = mutableListOf()
        c = mutableListOf()

        //define correct input
        val validnumber = Pattern.compile("^\\u002D\\d+|^\\u002D\\d+\\u002E\\d+|\\d+\\u002E\\d+|\\d+")
        val validnumber1 = Pattern.compile("^\\u0030\\d+")

        //take matrix A elements
        for (k in 1..j) {
            val temp = mutableListOf<Double>()

            for (l in 1..i) {
                edit = findViewById<EditText>(k * 10 + l)

                if (!validnumber.matcher(edit.text).matches() || validnumber1.matcher(edit.text).matches())
                    return false

                temp.add(edit.text.toString().toDouble())
            }
            A.add(temp)
        }

        //take vector b element
        for (l in 1..j) {

            edit = findViewById(l * 10 + i + 2)
            if (!validnumber.matcher(edit.text).matches() || validnumber1.matcher(edit.text).matches())
                return false
            b.add(edit.text.toString().toDouble())

        }

        //take vetor c element
        for (l in 1..i) {
            edit = findViewById<EditText>(l)
            if (!validnumber.matcher(edit.text).matches() || validnumber1.matcher(edit.text).matches())
                return false
            c.add(edit.text.toString().toDouble())

        }
        return true
    }

}
