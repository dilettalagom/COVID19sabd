from flask import Flask
from flask import jsonify
import formatterFirstQuery
from cassandra.cluster import Cluster


app = Flask(__name__)



@app.route('/',methods=["GET"])
def get_cassandra_connection():
    #retrieve info
    cluster = Cluster(["cassandra1", "cassandra2","cassandra-seed-node"])
    session = cluster.connect()
    session.set_keyspace("covid19")
    query1 = "SELECT * FROM query1_results"
    rows = session.execute(query1)
    f = formatterFirstQuery.formatterFirstQuery(rows).create_dict_complete(rows)
    return f

    #df = pd.DataFrame(results)

    #keys = [d['text'] for d in data[0]['columns']]
   #pd.DataFrame(data=data[0]['rows'], columns=keys)

    #return df.to_json()



@app.route('/query/query1', methods=["GET"])
def post_query1_results():
    cluster = Cluster(["cassandra1", "cassandra2","cassandra-seed-node"])
    session = cluster.connect()
    session.set_keyspace("covid19")
    query1 = "SELECT * FROM query1_results"
    results = session.execute(query1)
    return results[0].week_year


@app.route('/query/query2/top100', methods=["GET"])
def post_query2_top100_results():
    cluster = Cluster(["cassandra1", "cassandra2","cassandra-seed-node"])
    session = cluster.connect()
    session.set_keyspace("covid19")
    query1 = "SELECT * FROM query1_results"
    results = session.execute(query1)
    return jsonify(results)

@app.route('/query/query2/result', methods=["GET"])
def post_query2_results():
    cluster = Cluster(["cassandra1", "cassandra2","cassandra-seed-node"])
    session = cluster.connect()
    session.set_keyspace("covid19")
    query1 = "SELECT json * FROM query2_results"
    results = session.execute(query1)
    return results

@app.route('/query/query3/naive', methods=["GET"])
def post_query3_naive_results():
    cluster = Cluster(["cassandra1", "cassandra2","cassandra-seed-node"])
    session = cluster.connect()
    session.set_keyspace("covid19")
    query1 = "SELECT * FROM query1_results"
    results = session.execute(query1)
    return toJson(results)


@app.route('/query/query3/mllib', methods=["GET"])
def post_query3_mllib_results():
    cluster = Cluster(["cassandra1", "cassandra2","cassandra-seed-node"])
    session = cluster.connect()
    session.set_keyspace("covid19")
    query1 = "SELECT * FROM query1_results"
    results = session.execute(query1)
    return results[0].week_year


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)