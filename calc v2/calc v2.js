const numbers = /[0-9.]/;
const operators = /[+\-*/%^]/;
const left_brackets = new Set(["(", "sin(", "cos(", "tan(", "sqrt(", "log(", "exp("]);

const operator_precedence = new Map([["^", 3], ["*", 2], ["/", 2], ["%", 2], ["+", 1], ["-", 1]]);

let error_flag = false;
let input_stack = []
let result = $("#result")

function show() {
    if (error_flag) {
        result.val("ERROR!");
        return;
    }
    let str = "";
    for (let expression of input_stack) {
        str += expression;
    }
    result.val(str);
}

function clr() {
    error_flag = false;
    input_stack = [];
    show()
}

function input(expression) {
    if (error_flag) return;
    input_stack.push(expression);
    show();
}

function del() {
    if (error_flag) return;
    input_stack.pop();
    show();
}

function solve() {
    let str = "";
    for (let expression of input_stack) {
        str += expression;
    }
    str = str.replace(/^-/, "0-");
    str = str.replace(/\(-/g, "(0-");

    let num_stack = [];
    let op_stack = [];

    function factorial(n) {
        if (n <= 1) return 1;
        return n * factorial(n - 1);
    }

    function calc() {
        if (num_stack.length < 2 || op_stack.length === 0) {
            error_flag = true;
            return;
        }

        let num2 = num_stack.pop(), num1 = num_stack.pop();
        let op = op_stack.pop();

        switch (op) {
            case "+":
                num_stack.push(num1 + num2);
                break;
            case "-":
                num_stack.push(num1 - num2);
                break;
            case "*":
                num_stack.push(num1 * num2);
                break;
            case "/":
                num_stack.push(num1 / num2);
                break;
            case "^":
                num_stack.push(Math.pow(num1, num2));
                break;
            case "%":
                if (num1 % 1 === 0 && num2 % 1 === 0)
                    num_stack.push(num1 % num2);
                else
                    error_flag = true;
                break;
        }
    }

    for (let i = 0; i < str.length; i++) {
        console.log(str.slice(i));
        let char = str.charAt(i);

        switch (char) {
            case "(":
                op_stack.push("(");
                break;
            case ")":
                let flag = true;
                while (op_stack.length && flag) {
                    let peek = op_stack[op_stack.length - 1];
                    if (left_brackets.has(peek)) {
                        flag = false;
                        let num;
                        switch (op_stack.pop()) {
                            case "(":
                                break;
                            case "sin(":
                                num = num_stack.pop();
                                num_stack.push(Math.sin(num));
                                break;
                            case "cos(":
                                num = num_stack.pop();
                                num_stack.push(Math.cos(num));
                                break;
                            case "tan(":
                                num = num_stack.pop();
                                num_stack.push(Math.tan(num));
                                break;
                            case "sqrt(":
                                num = num_stack.pop();
                                num_stack.push(Math.sqrt(num));
                                break;
                            case "log(":
                                num = num_stack.pop();
                                num_stack.push(Math.log(num));
                                break;
                            case "exp(":
                                num = num_stack.pop();
                                num_stack.push(Math.exp(num));
                                break;
                        }
                    } else {
                        calc();
                        if (error_flag) break;
                    }
                }
                if (flag) error_flag = true;
                break;
            case "s":
                if (str.substr(i, 4) === "sin(") {
                    op_stack.push('sin(');
                    i += 3;
                }
                if (str.substr(i, 5) === "sqrt(") {
                    op_stack.push('sqrt(');
                    i += 4;
                }
                break;
            case "c":
                if (str.substr(i, 4) === "cos(") {
                    op_stack.push("cos(");
                    i += 3;
                }
                break;
            case "t":
                if (str.substr(i, 4) === "tan(") {
                    op_stack.push("tan(");
                    i += 3;
                }
                break;
            case "l":
                if (str.substr(i, 4) === "log(") {
                    op_stack.push("log(");
                    i += 3;
                }
                break;
            case "e":
                if (str.substr(i, 4) === "exp(") {
                    op_stack.push("exp(");
                    i += 3;
                }
                break;
            case "p":
                if (str.substr(i, 2) === "pi") {
                    num_stack.push(Math.PI);
                    i += 1;
                }
                break;
            case "!":
                if (op_stack.length) {
                    let num = num_stack.pop();
                    if (num % 1 === 0)
                        num_stack.push(factorial(num));
                    else error_flag = true;
                } else error_flag = true;
                break;
            case (char.match(numbers) || {}).input:
                let temp = char;
                while (numbers.test(str.charAt(++i))) {
                    temp += str.charAt(i);
                }
                i--;
                let num = Number(temp);
                if (isNaN(num))
                    error_flag = true;
                else
                    num_stack.push(num);
                break;
            case (char.match(operators) || {}).input:
                while (op_stack.length) {
                    let peek = op_stack[op_stack.length - 1];
                    if (left_brackets.has(peek) || operator_precedence.get(peek) < operator_precedence.get(char))
                        break;
                    else {
                        calc();
                        if (error_flag) break;
                    }
                }
                op_stack.push(char);
        }

        if (error_flag) {
            show();
            return;
        }
        console.log(num_stack);
        console.log(op_stack);
    }

    while (op_stack.length) {
        calc();
        if (error_flag) {
            show();
            return;
        }
        console.log(num_stack);
        console.log(op_stack);
    }

    if (num_stack.length === 1)
        input_stack = [num_stack.pop()];
    else
        error_flag = true;

    show();
}