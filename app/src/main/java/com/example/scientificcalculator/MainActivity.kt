package com.example.scientificcalculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.*

class MainActivity : AppCompatActivity() {

    private lateinit var display: TextView
    private var firstValue: Double = Double.NaN
    private var currentOperator: String? = null
    private var isNewOp = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        display = findViewById(R.id.display)

        val buttons = mapOf(
            R.id.button24 to "0", R.id.button20 to "1", R.id.button22 to "2",
            R.id.button21 to "3", R.id.button17 to "4", R.id.button19 to "5",
            R.id.button18 to "6", R.id.button14 to "7", R.id.button15 to "8",
            R.id.button16 to "9", R.id.button23 to "."
        )

        for ((id, value) in buttons) {
            findViewById<Button>(id).setOnClickListener { appendDigit(value) }
        }

        findViewById<Button>(R.id.button).setOnClickListener { clear() }
        findViewById<Button>(R.id.button10).setOnClickListener { setOperator("+") }
        findViewById<Button>(R.id.button12).setOnClickListener { setOperator("-") }
        findViewById<Button>(R.id.button11).setOnClickListener { setOperator("x") }
        findViewById<Button>(R.id.button9).setOnClickListener { setOperator("/") }

        findViewById<Button>(R.id.button13).setOnClickListener { calculate() }

        findViewById<Button>(R.id.button4).setOnClickListener { applyUnaryOp { sin(Math.toRadians(it)) } }
        findViewById<Button>(R.id.button3).setOnClickListener { applyUnaryOp { cos(Math.toRadians(it)) } }
        findViewById<Button>(R.id.button2).setOnClickListener { applyUnaryOp { tan(Math.toRadians(it)) } }
        findViewById<Button>(R.id.button5).setOnClickListener { applyUnaryOp { log10(it) } }
        findViewById<Button>(R.id.button8).setOnClickListener { applyUnaryOp { sqrt(it) } }
    }

    private fun appendDigit(digit: String) {
        if (isNewOp) {
            display.text = if (digit == ".") "0." else digit
            isNewOp = false
        } else {
            val currentText = display.text.toString()
            if (digit == "." && currentText.contains(".")) return
            display.text = currentText + digit
        }
    }

    private fun clear() {
        display.text = "0"
        firstValue = Double.NaN
        currentOperator = null
        isNewOp = true
    }

    private fun setOperator(op: String) {
        val currentText = display.text.toString()
        if (currentText.isNotEmpty()) {
            firstValue = currentText.toDoubleOrNull() ?: Double.NaN
            currentOperator = op
            isNewOp = true
        }
    }

    private fun calculate() {
        val secondValue = display.text.toString().toDoubleOrNull() ?: return
        if (!firstValue.isNaN() && currentOperator != null) {
            val result = when (currentOperator) {
                "+" -> firstValue + secondValue
                "-" -> firstValue - secondValue
                "x" -> firstValue * secondValue
                "/" -> if (secondValue != 0.0) firstValue / secondValue else Double.NaN
                else -> secondValue
            }
            display.text = formatResult(result)
            firstValue = Double.NaN
            currentOperator = null
            isNewOp = true
        }
    }

    private fun applyUnaryOp(op: (Double) -> Double) {
        val value = display.text.toString().toDoubleOrNull() ?: return
        val result = op(value)
        display.text = formatResult(result)
        isNewOp = true
    }

    private fun formatResult(result: Double): String {
        return if (result.isNaN()) {
            "Error"
        } else if (result == result.toLong().toDouble()) {
            result.toLong().toString()
        } else {
            result.toString()
        }
    }
}