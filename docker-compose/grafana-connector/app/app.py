from flask import Flask
import cassandra
from cassandra.cluster import Cluster

app = Flask(__name__)


@app.route('/')
def hello_world():
    cluster = Cluster(["cassandra1", "cassandra2","cassandra-seed-node"])
    session = cluster.connect()
    session.set_keyspace('covid19')
    results = session.execute("SELECT * FROM query1_results")
    return results[0].week_year





if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)