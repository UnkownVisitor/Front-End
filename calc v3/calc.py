import re
import math

numbers = re.compile(r'[\d.]')
left_brackets = {'(', 'sin(', 'cos(', 'tan(', 'sqrt(', 'log(', 'exp('}
operator_precedence = {'+': 1, '-': 1, '*': 2, '/': 2, '%': 2, '^': 3}


def factorial(n: int):
    if n <= 1:
        return 1
    return n * factorial(n - 1)


def calc(num_stack: list, op_stack: list):
    if len(num_stack) < 2 or len(op_stack) == 0:
        return False

    num2 = num_stack.pop()
    num1 = num_stack.pop()
    op = op_stack.pop()

    if op == '+':
        num_stack.append(num1 + num2)
        return True
    if op == '-':
        num_stack.append(num1 - num2)
        return True
    if op == '*':
        num_stack.append(num1 * num2)
        return True
    if op == '/':
        num_stack.append(num1 / num2)
        return True
    if op == '^':
        num_stack.append(num1 ** num2)
        return True
    if op == '%':
        if num1 == int(num1) and num2 == int(num2):
            num_stack.append(num1 % num2)
            return True
        else:
            return False

    return False


def solve(formula: str):
    error_flag = False

    formula = re.sub(r'^-', '0-', formula)
    formula = re.sub(r'\(-', '(0-', formula)

    num_stack = []
    op_stack = []

    i = -1

    while i + 1 < len(formula):
        i += 1

        if error_flag:
            return 'error'

        char = formula[i]

        if char == '(':
            op_stack.append('(')
            continue

        if char == ')':
            flag = True
            while len(op_stack) and flag:
                peek = op_stack[-1]
                if peek in left_brackets:
                    flag = False
                    op_stack.pop()
                    if peek == '(':
                        continue
                    if peek == 'sin(':
                        num_stack.append(math.sin(num_stack.pop()))
                        continue
                    if peek == 'cos(':
                        num_stack.append(math.cos(num_stack.pop()))
                        continue
                    if peek == 'tan(':
                        num_stack.append(math.tan(num_stack.pop()))
                        continue
                    if peek == 'sqrt(':
                        num_stack.append(math.sqrt(num_stack.pop()))
                        continue
                    if peek == 'log(':
                        num_stack.append(math.log(num_stack.pop()))
                        continue
                    if peek == 'exp(':
                        num_stack.append(math.exp(num_stack.pop()))
                        continue
                else:
                    if not calc(num_stack, op_stack):
                        error_flag = True
                        break
            if flag:
                error_flag = True
            continue

        if char == 's':
            if formula[i:i + 4] == 'sin(':
                op_stack.append('sin(')
                i += 3
                continue
            if formula[i:i + 5] == 'sqrt(':
                op_stack.append('sqrt(')
                i += 4
                continue

            error_flag = True
            continue

        if char == 'c':
            if formula[i:i + 4] == 'cos(':
                op_stack.append('cos(')
                i += 3
                continue

            error_flag = True
            continue

        if char == 't':
            if formula[i:i + 4] == 'tan(':
                op_stack.append('tan(')
                i += 3
                continue

            error_flag = True
            continue

        if char == 'l':
            if formula[i:i + 4] == 'log(':
                op_stack.append('log(')
                i += 3
                continue

            error_flag = True
            continue

        if char == 'e':
            if formula[i:i + 4] == 'exp(':
                op_stack.append('exp(')
                i += 3
                continue

            error_flag = True
            continue

        if char == 'p':
            if formula[i:i + 2] == 'pi':
                num_stack.append(math.pi)
                i += 1
                continue

            error_flag = True
            continue

        if char == '!':
            if len(num_stack):
                num = num_stack.pop()
                if num == int(num):
                    num_stack.append(factorial(num))
                    continue

            error_flag = True
            continue

        if numbers.match(char) is not None:
            temp = char
            while i + 1 < len(formula) and numbers.match(formula[i + 1]) is not None:
                i += 1
                temp += formula[i]

            try:
                num_stack.append(float(temp))
                continue
            except ValueError:
                error_flag = True
                continue

        if char in operator_precedence:
            while len(op_stack):
                peek = op_stack[-1]
                if peek in left_brackets or operator_precedence[peek] < operator_precedence[char]:
                    break
                if not calc(num_stack, op_stack):
                    error_flag = True
                    break
            op_stack.append(char)

    while len(op_stack):
        if not calc(num_stack, op_stack):
            return 'error'

    if len(num_stack) == 1:
        return str(num_stack.pop())

    return 'error'
