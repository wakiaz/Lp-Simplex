package dz.com.wakiaz.optimisationlineaire


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity



class MainActivity : AppCompatActivity() {


    private lateinit var nextButt: Button
    private lateinit var ediVar: EditText
    private lateinit var ediCont: EditText
    private var i = "0"
    private var j = "0"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nextButt = findViewById(R.id.next_butt)
        nextButt.setOnClickListener {

            ediVar = findViewById(R.id.edi_var)
            ediCont = findViewById(R.id.edi_cont)

            i = ediVar.text!!.toString()
            j = ediCont.text!!.toString()

            if (i == "" || j == "") {

                Toast.makeText(this, "the two value no should not be empty !!", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, TakeVal::class.java)
                intent.putExtra("i", i.toInt())
                intent.putExtra("j", j.toInt())
                startActivity(intent)
            }


        }

    }


}

