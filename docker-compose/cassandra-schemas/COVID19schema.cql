CREATE KEYSPACE IF NOT EXISTS COVID19
  WITH replication = {'class': 'NetworkTopologyStrategy', 'datacenter1': 1 };

CREATE TABLE IF NOT EXISTS COVID19.QUERY1_RESULTS (
  week_year text,
  datestart_week text,
  mean_healed double,
  mean_swabs double,
  PRIMARY KEY (datestart_week, week_year)
) WITH CLUSTERING ORDER BY (week_year ASC)
AND comment = 'Q1: per ogni settimana calcolare il numero medio di guariti e tamponi';


CREATE TABLE IF NOT EXISTS COVID19.QUERY2_TOP100_RESULTS (
   trendline_coefficient double,
   state text,
   country text,
   PRIMARY KEY ((state, country), trendline_coefficient)
) WITH CLUSTERING ORDER BY (trendline_coefficient DESC)
AND comment = 'Q2: classifica dei primi 100 stati più colpiti dalla pandemia in base al trend';


CREATE TABLE IF NOT EXISTS COVID19.QUERY2_RESULTS (
   week_year text,
   continent text,
   mean_confirmed_cases double,
   dev_std_confirmed_cases double,
   min_confirmed_cases double,
   max_confirmed_cases double,
   PRIMARY KEY (continent, week_year)
) WITH CLUSTERING ORDER BY (week_year ASC)
AND comment = 'Q2: per ogni continente calcolare (media, std, min, max) dei casi confermati per settimana';


CREATE TABLE IF NOT EXISTS COVID19.QUERY3_TOP50_RESULTS (
   month_year text,
   trendline_coefficient double,
   state text,
   country text,
   PRIMARY KEY (month_year, trendline_coefficient)
) WITH CLUSTERING ORDER BY (trendline_coefficient DESC)
AND comment = 'Q3: classifica dei 50 stati più colpiti dalla pandemia in base al trend mensile';


CREATE TABLE IF NOT EXISTS COVID19.QUERY3_KMEANS_NAIVE_RESULTS (
   cluster_id int,
   trends_countries map<double,text>,
   PRIMARY KEY (cluster_id),
) WITH comment = 'Q3: determinare stati e nazioni che fanno parte del cluster con kmeans naive';


CREATE TABLE IF NOT EXISTS COVID19.QUERY3_KMEANS_MLLIB_RESULTS (
   cluster_id int,
   trends_countries map<double,text>,
   PRIMARY KEY (cluster_id)
) WITH comment = 'Q3: determinare stati e nazioni che fanno parte del cluster con kmeans mllib';



/*

TODO: JUST A TEST
//show: describe keyspaces
CREATE KEYSPACE IF NOT EXISTS covid19
  WITH replication = {'class': 'NetworkTopologyStrategy', 'pickle-east': 1 };

//describe tables;


CREATE TABLE IF NOT EXISTS pickle.employees (
  employee_id uuid,
  PRIMARY KEY ((employee_id))
) WITH comment = 'Table with all employee IDs.';

CREATE TABLE IF NOT EXISTS pickle.timesheets (
  employee_id uuid,
  pickle_tree_id uuid,
  timestamp timeuuid,
  pickle_count int,
  pickle_avg_size float,
  watered boolean,
  PRIMARY KEY ((employee_id), pickle_tree_id, timestamp)
) WITH CLUSTERING ORDER BY (pickle_tree_id ASC, timestamp DESC)
  AND comment = 'The most recent employee visits for each pickle tree.';

CREATE TABLE IF NOT EXISTS pickle.trees (
  pickle_tree_id uuid,
  timestamp timeuuid,
  employee_id uuid,
  pickle_count int,
  pickle_avg_size float,
  watered boolean,
  PRIMARY KEY ((pickle_tree_id), timestamp)
) WITH CLUSTERING ORDER BY (timestamp DESC)
  AND comment = 'The most recent history for each pickle tree';

CREATE TABLE IF NOT EXISTS pickle.production (
  pickle_count int,
  pickle_tree_id uuid,
  timestamp timeuuid,
  PRIMARY KEY ((pickle_count), pickle_tree_id, timestamp)
) WITH CLUSTERING ORDER BY (pickle_tree_id ASC, timestamp ASC)
  AND comment = 'How often a pickle tree produces the same number of pickles.';


CREATE KEYSPACE IF NOT EXISTS reaper_db
  WITH replication = {'class': 'NetworkTopologyStrategy', 'pickle-east': 1 };

  */
