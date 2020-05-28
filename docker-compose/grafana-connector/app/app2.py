from calendar import timegm
from datetime import datetime
import _strptime # https://bugs.python.org/issue7980
from flask import Flask, request, jsonify
app = Flask(__name__)

app.debug = True


def convert_to_time_ms(timestamp):
    return 1000 * timegm(datetime.strptime(timestamp, '%Y-%m-%dT%H:%M:%S.%fZ').timetuple())


@app.route('/')
def health_check():
    return 'This datasource is healthy.'


@app.route('/search', methods=['POST'])
def search():
    return jsonify(['my_series', 'another_series'])


@app.route('/query', methods=['POST'])
def query():
    req = request.get_json()
    data = [
        {
            "target": req['targets'][0]['target'],
            "datapoints": [
                [861, convert_to_time_ms(req['range']['from'])],
                [767, convert_to_time_ms(req['range']['to'])]
            ]
        }
    ]
    return jsonify(data)


@app.route('/annotations', methods=['POST'])
def annotations():
    req = request.get_json()
    data = [
        {
            "annotation": 'This is the annotation',
            "time": (convert_to_time_ms(req['range']['from']) +
                     convert_to_time_ms(req['range']['to'])) / 2,
            "title": 'Deployment notes',
            "tags": ['tag1', 'tag2'],
            "text": 'Hm, something went wrong...'
        }
    ]
    return jsonify(data)


@app.route('/tag-keys', methods=['POST'])
def tag_keys():
    data = [
        {"type": "string", "text": "City"},
        {"type": "string", "text": "Country"}
    ]
    return jsonify(data)


@app.route('/tag-values', methods=['POST'])
def tag_values():
    req = request.get_json()
    if req['key'] == 'City':
        return jsonify([
            {'text': 'Tokyo'},
            {'text': 'São Paulo'},
            {'text': 'Jakarta'}
        ])
    elif req['key'] == 'Country':
    return jsonify([
        {'text': 'China'},
        {'text': 'India'},
        {'text': 'United States'}
    ])


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)











# from flask import Flask
# from flask import jsonify
# import formatterFirstQuery
# from cassandra.cluster import Cluster
#
#
# app = Flask(__name__)
#
#
#
# @app.route('/',methods=["GET"])
# def get_cassandra_connection():
#     #retrieve info
#     cluster = Cluster(["cassandra1", "cassandra2","cassandra-seed-node"])
#     session = cluster.connect()
#     session.set_keyspace("covid19")
#     query1 = "SELECT * FROM query1_results"
#     rows = session.execute(query1)
#     f = formatterFirstQuery.formatterFirstQuery(rows).create_dict_complete(rows)
#     return f
#
#     #df = pd.DataFrame(results)
#
#     #keys = [d['text'] for d in data[0]['columns']]
#    #pd.DataFrame(data=data[0]['rows'], columns=keys)
#
#     #return df.to_json()
#
# @app.route('/', methods=["GET"])
# def alloOk():
#     return 200
#
#
# @app.route('/search', methods=["GET"])
# def search():
#     return {"target": ("week_year","datestart_week","mean_healed","mean_swabs")}
#
#
# @app.route('/search', methods=["GET"])
# def search():
#     return {"target": ("week_year","datestart_week","mean_healed","mean_swabs")}
#
#
# @app.route('/query', methods=["POST"])
# def post_query1_results():
#     cluster = Cluster(["cassandra1", "cassandra2","cassandra-seed-node"])
#     session = cluster.connect()
#     session.set_keyspace("covid19")
#     query1 = "SELECT * FROM query1_results"
#     results = session.execute(query1)
#     return results[0].week_year
#
#
# @app.route('/query/query2/top100', methods=["GET"])
# def post_query2_top100_results():
#     cluster = Cluster(["cassandra1", "cassandra2","cassandra-seed-node"])
#     session = cluster.connect()
#     session.set_keyspace("covid19")
#     query1 = "SELECT * FROM query1_results"
#     results = session.execute(query1)
#     return jsonify(results)
#
# @app.route('/query/query2/result', methods=["GET"])
# def post_query2_results():
#     cluster = Cluster(["cassandra1", "cassandra2","cassandra-seed-node"])
#     session = cluster.connect()
#     session.set_keyspace("covid19")
#     query1 = "SELECT json * FROM query2_results"
#     results = session.execute(query1)
#     return results
#
# @app.route('/query/query3/naive', methods=["GET"])
# def post_query3_naive_results():
#     cluster = Cluster(["cassandra1", "cassandra2","cassandra-seed-node"])
#     session = cluster.connect()
#     session.set_keyspace("covid19")
#     query1 = "SELECT * FROM query1_results"
#     results = session.execute(query1)
#     return toJson(results)
#
#
# @app.route('/query/query3/mllib', methods=["GET"])
# def post_query3_mllib_results():
#     cluster = Cluster(["cassandra1", "cassandra2","cassandra-seed-node"])
#     session = cluster.connect()
#     session.set_keyspace("covid19")
#     query1 = "SELECT * FROM query1_results"
#     results = session.execute(query1)
#     return results[0].week_year
#
#
# if __name__ == '__main__':
#     app.run(debug=True, host='0.0.0.0', port=5000)