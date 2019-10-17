package ru.ifmo.rain.balahnin

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.asdev.expr.Value
import java.lang.StringBuilder

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var expression: MutableList<String> = ArrayList()
    private var curText: String = ""
    private lateinit var expressionView: TextView
    private lateinit var button0: Button
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button
    private lateinit var button5: Button
    private lateinit var button6: Button
    private lateinit var button7: Button
    private lateinit var button8: Button
    private lateinit var button9: Button
    private lateinit var buttonClear: Button
    private lateinit var buttonDel: Button
    private lateinit var buttonDiv: Button
    private lateinit var buttonMul: Button
    private lateinit var buttonPlus: Button
    private lateinit var buttonMinus: Button
    private lateinit var buttonDot: Button
    private lateinit var buttonCalc: Button
    private lateinit var buttonOpenedBracket: Button
    private lateinit var buttonClosedBracket: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        expressionView = findViewById(R.id.expression_view)
        button1 = findViewById(R.id.button_1)
        button1.setOnClickListener(this)
        button2 = findViewById(R.id.button_2)
        button2.setOnClickListener(this)
        button3 = findViewById(R.id.button_3)
        button3.setOnClickListener(this)
        button4 = findViewById(R.id.button_4)
        button4.setOnClickListener(this)
        button5 = findViewById(R.id.button_5)
        button5.setOnClickListener(this)
        button6 = findViewById(R.id.button_6)
        button6.setOnClickListener(this)
        button7 = findViewById(R.id.button_7)
        button7.setOnClickListener(this)
        button8 = findViewById(R.id.button_8)
        button8.setOnClickListener(this)
        button9 = findViewById(R.id.button_9)
        button9.setOnClickListener(this)
        button0 = findViewById(R.id.button_0)
        button0.setOnClickListener(this)
        buttonClear = findViewById(R.id.button_clear)
        buttonClear.setOnClickListener(this)
        buttonCalc = findViewById(R.id.button_calc)
        buttonCalc.setOnClickListener(this)
        buttonDel = findViewById(R.id.button_del)
        buttonDel.setOnClickListener(this)
        buttonDiv = findViewById(R.id.button_div)
        buttonDiv.setOnClickListener(this)
        buttonMul = findViewById(R.id.button_mul)
        buttonMul.setOnClickListener(this)
        buttonMinus = findViewById(R.id.button_minus)
        buttonMinus.setOnClickListener(this)
        buttonPlus = findViewById(R.id.button_plus)
        buttonPlus.setOnClickListener(this)
        buttonDot = findViewById(R.id.button_dot)
        buttonDot.setOnClickListener(this)
        buttonOpenedBracket = findViewById(R.id.button_open_bracket)
        buttonOpenedBracket.setOnClickListener(this)
        buttonClosedBracket = findViewById(R.id.button_closed_bracket)
        buttonClosedBracket.setOnClickListener(this)


    }

    override fun onClick(v: View?) {
        var isCalc = false
        when (v?.id) {
            R.id.button_0 -> expression.add("0")
            R.id.button_1 -> expression.add("1")
            R.id.button_2 -> expression.add("2")
            R.id.button_3 -> expression.add("3")
            R.id.button_4 -> expression.add("4")
            R.id.button_5 -> expression.add("5")
            R.id.button_6 -> expression.add("6")
            R.id.button_7 -> expression.add("7")
            R.id.button_8 -> expression.add("8")
            R.id.button_9 -> expression.add("9")
            R.id.button_open_bracket -> expression.add(" (")
            R.id.button_closed_bracket -> expression.add(") ")
            R.id.button_div -> expression.add(" ÷ ")
            R.id.button_mul -> expression.add(" * ")
            R.id.button_minus -> expression.add(" - ")
            R.id.button_plus -> expression.add(" + ")
            R.id.button_dot -> expression.add(".")
            R.id.button_calc -> {
                curText = try {
                    val res = Value(expression.joinToString (separator = ""){ token -> token }).resolve().toString()
                    res
                } catch (e: RuntimeException) {
                    getString(R.string.non_correct_expression)
                } finally {
                    expression.clear()
                    isCalc = true
                }
            }
            R.id.button_del -> if (expression.isNotEmpty()) expression.removeAt(expression.size - 1)
            R.id.button_clear -> expression.clear()
        }
        if (!isCalc) curText = expression.joinToString (separator = ""){ token -> token }
        expressionView.text = curText
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putStringArrayList("expression", expression as java.util.ArrayList<String>)
        outState.putString("curText", curText)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        expression = savedInstanceState.getStringArrayList("expression") as MutableList<String>
        curText = savedInstanceState.getString("curText")!!
        expressionView.text = curText
    }

}
