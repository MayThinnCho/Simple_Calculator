package com.example.simplecalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isGone
import com.example.simplecalculator.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder
import javax.xml.xpath.XPathExpression

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var canAddOperation = false
    private var canAddDecimal = true
    private lateinit var  expression : Expression
    private var lastNumeric = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root)
    }

    fun onDigitClick(view:View){
        val display = binding.numberDisplay.text.toString();
        val digitText = (view as Button).text.toString()

        if (digitText != ".") {
            handleNonDecimalDigit(digitText)
        } else {
            handleDecimalDigit(display)
        }
    }

    private fun handleNonDecimalDigit(digitText: String) {
        binding.numberDisplay.append(digitText)
        canAddOperation = true
        lastNumeric = true
        onCalculate()
        binding.result.visibility = View.VISIBLE
    }

    private fun handleDecimalDigit(display: String) {
        if (canAddDecimal && display.isNotEmpty() && display.last() !in listOf('+', '-', '*', '%', '/', '.')) {
            binding.numberDisplay.append(".")
            canAddOperation = false
            canAddDecimal = false
            binding.result.visibility = View.VISIBLE
        }
    }

    fun onOperatorClick(view:View){
        var operator = (view as Button).text
        if(canAddOperation && lastNumeric) {
            binding.numberDisplay.append(view.text)
        }
        lastNumeric = false
        canAddOperation= false
        canAddDecimal = true
    }

    fun onResultClick(view:View){
        val result = binding.result.text.toString();
        if(result.isNotEmpty()){
            binding.numberDisplay.text = binding.result.text.toString().removePrefix("=")
            binding.result.visibility = View.GONE
        }
    }

    private fun onCalculate(){
        var textExpression = binding.numberDisplay.text.toString();
        if (textExpression.isNotEmpty() && textExpression.last() in listOf('+', '-', '*', '%','/')) {
            binding.numberDisplay.text = textExpression.dropLast(1)
            textExpression = textExpression.dropLast(1)
        }

        try {
            expression = ExpressionBuilder(textExpression).build()
            val result = expression.evaluate()
            val resultText = if (result % 1 == 0.0) {
                "= ${result.toInt()}"
            } else {
                "= ${result.toString()}"
            }
            binding.result.text = resultText
        }catch (e: ArithmeticException) {
            Snackbar.make(binding.root, "Cannot divided by 0 ", Snackbar.LENGTH_SHORT).show()
            resetDisplay()
        }

    }

    fun onBackClick(view:View){
        var display = binding.numberDisplay.text.toString();
        display = display.dropLast(1).trim()

        if (display.isNotEmpty()) {
            binding.numberDisplay.text = display
            if (display.last() !in listOf('+', '-', '*', '%', '/')) {
                canAddOperation = true
            }
            onCalculate()
            binding.result.visibility = View.VISIBLE
        }else {
            Log.d("empty", "is empty: ")
            resetDisplay()
        }
    }

    private fun calculate() {
        val textExpression = binding.numberDisplay.text.toString();
        val operators = arrayOf("+", "-", "*", "%","/")
        val numberArray = textExpression.split(Regex("(?<=[${operators.joinToString("") { Regex.escape(it.toString()) }}])|(?=[${operators.joinToString("") { Regex.escape(it.toString()) }}])"))

        var index =1;
        var result = numberArray[0].toDouble();

        while(index < numberArray.size){
            val operator = numberArray[index]
            val nextNumber = numberArray[index + 1].toDouble()
            when (operator) {
                "+" -> result += nextNumber
                "-" -> result -= nextNumber
                "*" -> result *= nextNumber
                "%" -> result %= nextNumber
                "/" -> result /= nextNumber
            }
            index += 2
        }
        binding.result.text  = "$result"
    }

    private fun resetDisplay(){
        canAddOperation = false
        lastNumeric =false
        canAddDecimal = true
        binding.numberDisplay.text = ""
        binding.result.text=""
        binding.result.visibility = View.GONE
    }

    fun onAllClearClick(view:View){
        resetDisplay()
    }
}

