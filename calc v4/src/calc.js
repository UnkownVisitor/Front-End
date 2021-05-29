import $ from 'jquery'

import 'bootstrap/dist/css/bootstrap.min.css'
import './calc.css'

let xmlHttp = new XMLHttpRequest();

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

window.clr =
    function () {
        error_flag = false;
        input_stack = [];
        show()
    }

window.input =
    function (expression) {
        if (error_flag) return;
        input_stack.push(expression);
        show();
    }

window.del =
    function () {
        if (error_flag) return;
        input_stack.pop();
        show();
    }

window.solve =
    function () {
        if (error_flag) return;
        let str = "";

        for (let expr of input_stack) {
            str += expr;
        }

        xmlHttp.open("POST", "http://localhost:8000/", false);
        xmlHttp.send(str)

        let resp = xmlHttp.responseText;

        console.log(resp)

        input_stack = [resp]

        if (resp === "error")
            error_flag = true;
        show();
    }