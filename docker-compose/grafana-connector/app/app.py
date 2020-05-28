from flask import Flask
import cassandra
from cassandra.cluster import Cluster

app = Flask(__name__)
query1 = "SELECT * FROM query1_results"

def get_cassandra_connection(string_query):
    cluster = Cluster(["cassandra1", "cassandra2","cassandra-seed-node"])
    session = cluster.connect()
    session.set_keyspace('covid19')
    results = session.execute(string_query)
    return results


@app.route('/query1', methods=["POST"])
def post_query1_results():
    # get_query1_values
    results_q1 = get_cassandra_connection(query1)
    print(results_q1)

    # convert to json

    #send back to grafana




if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)