import calc

from sanic import Sanic
from sanic.response import text

app = Sanic('calc')


@app.post('/')
async def handler(request):
    formula = bytes.decode(request.body)
    return text(calc.solve(formula))


app.run('0.0.0.0', 8000)
