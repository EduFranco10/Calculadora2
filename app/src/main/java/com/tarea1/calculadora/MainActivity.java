package com.tarea1.calculadora;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    private String operaciones = "";
    private TextView operacionesAnteriores;
    private EditText result;
    private Boolean calcular = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        operacionesAnteriores = findViewById(R.id.operationAfter);
        result = findViewById(R.id.result);

        setButtonListeners();
    }

    private void setButtonListeners() {
        int[] numericButtons = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
                R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7,
                R.id.btn8, R.id.btn9, R.id.btnDecimal
        };

        for (int id : numericButtons) {
            findViewById(id).setOnClickListener(this::addExpression);
        }

        int[] operationButtons = {
                R.id.btnPlus, R.id.btnRestar, R.id.btnMulti, R.id.btnResolver,
                R.id.btnAC, R.id.btnC, R.id.btnDividir, R.id.btnResto
        };

        for (int id : operationButtons) {
            findViewById(id).setOnClickListener(this::addOperations);
        }
    }

    private void addExpression(View vista) {
        Button button = (Button) vista;
        String value = button.getText().toString();
        operaciones += value;
        operacionesAnteriores.setText(operaciones);
    }

    private void addOperations(View vistaOperations) {
        Button buttonOperation = (Button) vistaOperations;
        String operation = buttonOperation.getText().toString();

        switch (operation) {
            case "AC":
                operaciones = "";
                operacionesAnteriores.setText("");
                result.setText("");
                break;
            case "C":
                operaciones = !operaciones.isEmpty() ? operaciones.substring(0, operaciones.length() - 1) : operaciones;
                operacionesAnteriores.setText(operaciones);
                break;
            case "=":
                double resultAux = evaluateOperation(operaciones);
                result.setText(String.valueOf(resultAux));
                operaciones = "";
                break;
            default:
                operaciones += operation.equalsIgnoreCase("X") ? "*" : operation;
                operacionesAnteriores.setText(operaciones);
                break;
        }
    }

    private double evaluateOperation(String expression) {
        expression = expression.replaceAll("\\s+", "");

        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                StringBuilder sb = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    sb.append(expression.charAt(i));
                    i++;
                }
                i--;
                numbers.push(Double.parseDouble(sb.toString()));
            } else if (isOperator(c)) {
                while (!operators.isEmpty() && hasPrecedence(c, operators.peek())) {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(c);
            }
        }

        while (!operators.isEmpty()) {
            numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
        }

        return numbers.pop();
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%';
    }

    private boolean hasPrecedence(char op1, char op2) {
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) return false;
        return true;
    }

    private double applyOperation(char operator, double b, double a) {
        switch (operator) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/':
                if (b == 0) throw new ArithmeticException("División por cero");
                return a / b;
            case '%': return a % b;
            default: throw new UnsupportedOperationException("Operador no soportado: " + operator);
        }
    }
}
