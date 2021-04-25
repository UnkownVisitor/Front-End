const numbers = /[0-9.]/;
const operators = /[+\-*/%^]/;
const operator_precedence = new Map([["^", 3], ["*", 2], ["/", 2], ["%", 2], ["+", 1], ["-", 1]]);
const left_brackets = new Set(["(", "sin(", "cos(", "tan(", "sqrt(", "log(", "exp("]);
let result = document.getElementById("result");
let error = false;

function input(expression) {
    if (!error)
        result.value += expression;
}

function clr() {
    result.value = "";
    error = false;
}

function del() {
    result.value = result.value.substr(0, result.value.length - 1);
}

function factorial(n) {
    if (n <= 1) return 1;
    return n * factorial(n - 1);
}

function solve() {
    let str = result.value;
    str = str.replace(/^-/, "0-");
    str = str.replace(/\(-/g, "(0-");

    let nums = [];
    let ops = [];

    function calc() {
        if (nums.length < 2 || ops.length === 0) {
            result.value = "ERROR!";
            error = true;
            return false;
        }
        let num2 = nums.pop(), num1 = nums.pop();
        let op = ops.pop();
        if (op === "+") {
            nums.push(num1 + num2);
        } else if (op === "-") {
            nums.push(num1 - num2);
        } else if (op === '*') {
            nums.push(num1 * num2);
        } else if (op === '/') {
            nums.push(num1 / num2);
        } else if (op === '%') {
            if (num1 % 1 === 0 && num2 % 1 === 0) {
                nums.push(num1 % num2);
            } else {
                result.value = "ERROR!";
                error = true;
                return false;
            }
        } else if (op === '^') {
            nums.push(Math.pow(num1, num2));
        }
        return true;
    }

    for (let i = 0; i < str.length; i++) {
        console.log(str.slice(i));
        let char = str.charAt(i);

        if (char === "!") {
            if (nums.length) {
                let num = nums.pop();
                if (num % 1 === 0) {
                    nums.push(factorial(num));
                } else {
                    result.value = "ERROR!";
                    error = true;
                    return;
                }
            }
        } else if (char === ')') {
            let flag = false;
            while (ops.length) {
                let peek = ops[ops.length - 1];
                if (left_brackets.has(peek)) {
                    flag = true;
                    let left_bracket = ops.pop();
                    if (left_bracket === "(") {
                        break;
                    }
                    if (left_bracket === "sin(") {
                        let num = nums.pop();
                        nums.push(Math.sin(num));
                        break;
                    }
                    if (left_bracket === "cos(") {
                        let num = nums.pop();
                        nums.push(Math.cos(num));
                        break;
                    }
                    if (left_bracket === "tan(") {
                        let num = nums.pop();
                        nums.push(Math.tan(num));
                        break;
                    }
                    if (left_bracket === "sqrt(") {
                        let num = nums.pop();
                        nums.push(Math.sqrt(num));
                        break;
                    }
                    if (left_bracket === "log(") {
                        let num = nums.pop();
                        nums.push(Math.log(num));
                        break;
                    }
                    if (left_bracket === "exp(") {
                        let num = nums.pop();
                        nums.push(Math.exp(num));
                        break;
                    }
                } else {
                    if (calc() === false)
                        return;
                }
            }
            if (!flag) {
                result.value = "ERROR!";
                error = true;
                return;
            }
        } else if (char === "(") {
            ops.push("(");
        } else if (char === "s") {
            if (str.substr(i, 4) === "sin(") {
                ops.push('sin(');
                i += 3;
            } else if (str.substr(i, 5) === "sqrt(") {
                ops.push('sqrt(');
                i += 4;
            } else {
                result.value = "ERROR!";
                error = true;
                return;
            }
        } else if (char === "c") {
            if (str.substr(i, 4) === "cos(") {
                ops.push("cos(");
                i += 3;
            } else {
                result.value = "ERROR!";
                error = true;
                return;
            }
        } else if (char === "t") {
            if (str.substr(i, 4) === "tan(") {
                ops.push("tan(");
                i += 3;
            } else {
                result.value = "ERROR!";
                error = true;
                return;
            }
        } else if (char === "l") {
            if (str.substr(i, 4) === "log(") {
                ops.push("log(");
                i += 3;
            } else {
                result.value = "ERROR!";
                error = true;
                return;
            }
        } else if (char === "e") {
            if (str.substr(i, 4) === "exp(") {
                ops.push("exp(");
                i += 3;
            } else {
                result.value = "ERROR!";
                error = true;
                return;
            }
        } else if (numbers.test(char)) {
            let temp = char;
            while (numbers.test(str.charAt(++i))) {
                temp += str.charAt(i);
            }
            i--;
            if (isNaN(temp)) {
                result.value = "ERROR!";
                error = true;
                return;
            } else {
                nums.push(Number(temp));
            }
        } else if (operators.test(char)) {
            while (ops.length) {
                let peek = ops[ops.length - 1];
                if (left_brackets.has(peek) || operator_precedence.get(peek) < operator_precedence.get(char)) {
                    break;
                } else {
                    if (calc() === false)
                        return;
                }
            }
            ops.push(char);
        }

        console.log(nums);
        console.log(ops);
    }

    while (ops.length) {
        if (calc() === false)
            return;
        console.log(nums);
        console.log(ops);
    }

    if (nums.length === 1) {
        result.value = nums.pop();
    } else {
        result.value = "ERROR!";
        error = true;
        return;
    }

}