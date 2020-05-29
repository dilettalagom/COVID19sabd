from flask import Flask, request, jsonify
from flask import jsonify
from cassandra.cluster import Cluster
from flask import Response
import formatterSecondQueryTable
app = Flask(__name__)

app.debug = True


@app.route('/', methods=['GET'])
def health_check():
    return 'This datasource is healthy.',200

@app.route('/search', methods=['POST'])
def search():
    return Response("""["query1", "query2", "query3_naive", "query3_mllib"]""", mimetype='application/json');

@app.route('/query', methods=['POST'])
def query():
    cluster = Cluster(["cassandra1", "cassandra2","cassandra-seed-node"])
    session = cluster.connect()
    session.set_keyspace("covid19")
    query1 = "SELECT * FROM query2_results"
    rows = session.execute(query1)
    f = formatterSecondQueryTable.formatterSecondQueryTable(rows).create_dict_complete(rows)
    return jsonify(f)

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