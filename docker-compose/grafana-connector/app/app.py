from flask import Flask, request, jsonify
from flask import jsonify
from cassandra.cluster import Cluster
from flask import Response
from flask import request
import formatterFirstQueryTS
import formatterThirdQueryMap
import formatterSecondQueryTable
import sys
import json
from flask_cors import CORS

sys.path.append('/usr/local/lib/python2.7/dist-packages')


app = Flask(__name__)
CORS(app)
app.debug = True


@app.route('/', methods=['GET'])
def health_check():
    return 'This datasource is healthy',200



@app.route('/search', methods=['POST'])
def search():
    return Response("""["query1", "query2", "query3"]""", mimetype='application/json');



@app.route('/query', methods=['POST'])
def query():
    cluster = Cluster(["cassandra1", "cassandra2","cassandra-seed-node"])
    session = cluster.connect()
    session.set_keyspace("covid19")
    r = request.json

    #'data': {u'anno': u'2020', u'mese': u'01'}
    requested_query = r["targets"][0]
    if('target' not in requested_query):
        return jsonify([])
    else:
        if (requested_query['target']) == 'query1':
            query1 = "SELECT * FROM query1_results"
            rows = session.execute(query1)
            f = formatterFirstQueryTS.formatterFirstQueryTS(rows).get_target_content(rows)
        elif requested_query['target'] == 'query2':
            query2 = "SELECT * FROM query2_results"
            rows = session.execute(query2)
            f = formatterSecondQueryTable.formatterSecondQueryTable(rows).create_dict_complete(rows)
        elif requested_query['target'] == 'query3':
            data = requested_query["data"]
            app.logger.info(data)
            query3 = "select * from query3_kmeans_naive_results where month_year = '{}-{}' ALLOW FILTERING ".format(data["anno"],data["mese"])
            rows = session.execute(query3)
            f = formatterThirdQueryMap.formatterThirdQueryMap(rows).get_target_content(rows)
    return jsonify(f)


@app.route('/geo', methods=['GET'],)
def geo():
    cluster = Cluster(["cassandra1", "cassandra2","cassandra-seed-node"])
    session = cluster.connect()
    session.set_keyspace("covid19")
    query3 = "SELECT * FROM query3_kmeans_naive_results"
    rows = session.execute(query3)
    f = formatterThirdQueryMap.formatterThirdQueryMap(rows).get_location_content(rows)
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





