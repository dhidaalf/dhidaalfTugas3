package com.example.dhidaalftugas3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import java.util.Stack

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var resultTv: TextView
    private lateinit var solutionTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultTv = findViewById(R.id.result_tv)
        solutionTv = findViewById(R.id.solution_tv)

        val buttonIds = listOf(
            R.id.button_c, R.id.button_open_bracket, R.id.button_close_bracket, R.id.button_divide,
            R.id.button_multiply, R.id.button_plus, R.id.button_minus, R.id.button_equals,
            R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3, R.id.button_4,
            R.id.button_5, R.id.button_6, R.id.button_7, R.id.button_8, R.id.button_9,
            R.id.button_ac, R.id.button_dot
        )

        buttonIds.forEach { id ->
            findViewById<MaterialButton>(id).setOnClickListener(this)
        }
    }

    override fun onClick(view: View) {
        val button = view as MaterialButton
        val buttonText = button.text.toString()
        var dataToCalculate = solutionTv.text.toString()

        when (buttonText) {
            "AC" -> {
                solutionTv.text = ""
                resultTv.text = "0"
            }
            "=" -> {
                val result = evaluateExpression(dataToCalculate)
                resultTv.text = result
            }
            "C" -> {
                if (dataToCalculate.isNotEmpty()) {
                    dataToCalculate = dataToCalculate.dropLast(1)
                    solutionTv.text = dataToCalculate
                }
            }
            else -> {
                dataToCalculate += buttonText
                solutionTv.text = dataToCalculate
            }
        }
    }

    // Evaluasi ekspresi matematika dasar
    private fun evaluateExpression(expression: String): String {
        return try {
            val result = evaluate(expression)
            result.toString()
        } catch (e: Exception) {
            "Error"
        }
    }

    // Evaluator ekspresi tanpa library
    private fun evaluate(expression: String): Double {
        val tokens = expression.toCharArray()
        val values = Stack<Double>()
        val ops = Stack<Char>()

        var i = 0
        while (i < tokens.size) {
            if (tokens[i] == ' ') {
                i++
                continue
            }

            if (tokens[i] in '0'..'9') {
                val sbuf = StringBuilder()
                while (i < tokens.size && (tokens[i] in '0'..'9' || tokens[i] == '.')) {
                    sbuf.append(tokens[i++])
                }
                values.push(sbuf.toString().toDouble())
                i--
            } else if (tokens[i] == '(') {
                ops.push(tokens[i])
            } else if (tokens[i] == ')') {
                while (ops.peek() != '(') {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()))
                }
                ops.pop() // pop '('
            } else if (tokens[i] in listOf('+', '-', '*', '/')) {
                while (!ops.isEmpty() && hasPrecedence(tokens[i], ops.peek())) {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()))
                }
                ops.push(tokens[i])
            }
            i++
        }

        while (!ops.isEmpty()) {
            values.push(applyOp(ops.pop(), values.pop(), values.pop()))
        }

        return values.pop()
    }

    private fun hasPrecedence(op1: Char, op2: Char): Boolean {
        if (op2 == '(' || op2 == ')') return false
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) return false
        return true
    }

    private fun applyOp(op: Char, b: Double, a: Double): Double {
        return when (op) {
            '+' -> a + b
            '-' -> a - b
            '*' -> a * b
            '/' -> if (b != 0.0) a / b else throw UnsupportedOperationException("Cannot divide by zero")
            else -> 0.0
        }
    }
}