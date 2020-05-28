CREATE KEYSPACE IF NOT EXISTS COVID19
  WITH replication = {'class': 'NetworkTopologyStrategy', 'datacenter1': 3 };

CREATE TABLE IF NOT EXISTS COVID19.QUERY1_RESULTS (
  week_year text,
  datestart_week text,
  mean_healed double,
  mean_swabs double,
  PRIMARY KEY (week_year, datestart_week)
) WITH CLUSTERING ORDER BY (datestart_week ASC)
AND comment = 'Q1: per ogni settimana calcolare il numero medio di guariti e tamponi';


CREATE TABLE IF NOT EXISTS COVID19.QUERY2_TOP100_RESULTS (
   trendline_coefficient double,
   state text,
   country text,
   PRIMARY KEY ((state,country),trendline_coefficient)
) WITH CLUSTERING ORDER BY (trendline_coefficient ASC)
AND comment = 'Q2: classifica dei primi 100 stati più colpiti dalla pandemia in base al trend';


CREATE TABLE IF NOT EXISTS COVID19.QUERY2_RESULTS (
   week_year text,
   datestart_week text,
   continent text,
   mean_confirmed_cases double,
   dev_std_confirmed_cases double,
   min_confirmed_cases double,
   max_confirmed_cases double,
   PRIMARY KEY (continent, week_year)
) WITH CLUSTERING ORDER BY (week_year ASC)
AND comment = 'Q2: per ogni continente calcolare (media, std, min, max) dei casi confermati per settimana';


CREATE TABLE IF NOT EXISTS COVID19.QUERY3_KMEANS_NAIVE_RESULTS (
   cluster_id int,
   month_year text,
   trend double,
   state text,
   country text,
   PRIMARY KEY ((month_year, cluster_id, country, state),trend),
) WITH CLUSTERING ORDER BY (trend DESC)
AND comment = 'Q3: determinare stati e nazioni che fanno parte del cluster con kmeans naive';


CREATE TABLE IF NOT EXISTS COVID19.QUERY3_MLLIB_RESULTS (
   cluster_id int,
   month_year text,
   trend double,
   state text,
   country text,
   PRIMARY KEY ((month_year, cluster_id, country, state),trend),
) WITH CLUSTERING ORDER BY (trend DESC)
AND comment = 'Q3: determinare stati e nazioni che fanno parte del cluster con kmeans mllib';



