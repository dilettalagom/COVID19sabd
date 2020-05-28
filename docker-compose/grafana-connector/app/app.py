from flask import Flask, request, jsonify
from flask import Response
app = Flask(__name__)

app.debug = True


@app.route('/', methods=['GET'])
def health_check():
    return 'This datasource is healthy.',200

@app.route('/search', methods=['POST'])
def search():
    return Response("""["query1"]""", mimetype='application/json');

@app.route('/query', methods=['POST'])
def query():
    a =  """[
      {
        "columns":[
          {"text":"Time","type":"time"},
          {"text":"Country","type":"string"},
          {"text":"Number","type":"number"}
        ],
        "rows":[
          [1234567,"SE",123],
          [1234567,"DE",231],
          [1234567,"US",321]
        ],
        "type":"table"
      }
    ]"""
    return Response(a, mimetype='application/json')

@app.route('/annotations', methods=["POST"])
def annotations():
    x = """[
        {
            "text": "text shown in body",
            "title": "Annotation Title",
            "isRegion": true,
            "time": "timestamp",
            "timeEnd": "timestamp",
            "tags": ["tag1"]
          }
        ]"""
    return Response(x, mimetype='application/json')





