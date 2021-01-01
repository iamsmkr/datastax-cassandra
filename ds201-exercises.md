## DS201: DataStax Enterprise 6 Foundations of Apache Cassandra™

### Exercise 1 - Install and Start Apache Cassandra™
- To check the status of running Cassandra instance
```sh
$ nodetool status
Datacenter: datacenter1
=======================
Status=Up/Down
|/ State=Normal/Leaving/Joining/Moving
--  Address    Load       Tokens       Owns    Host ID                               Rack
UN  127.0.0.1  795.3 KiB  256          ?       d5a70cb7-1fcf-49f2-9b04-20aa95924976  rack1

Note: Non-system keyspaces don't have the same replication settings, effective ownership information is meaningless
```

### Exercise 2 - CQL
- Create a keyspace for KillrVideo
```sh
cqlsh> CREATE KEYSPACE KillrVideo WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
cqlsh> DESC KEYSPACES ;
```

- Create a table to store video metadata
```sh
cqlsh> USE killrvideo ;
cqlsh:killrvideo> CREATE TABLE videos ( video_id timeuuid, added_date timestamp, title text, primary key (video_id));
cqlsh:killrvideo> DESC COLUMNFAMILIES ;

cqlsh:killrvideo> DESC COLUMNFAMILY videos ;

CREATE TABLE killrvideo.videos (
    video_id timeuuid PRIMARY KEY,
    added_date timestamp,
    title text
) WITH bloom_filter_fp_chance = 0.01
    AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
    AND comment = ''
    AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
    AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
    AND crc_check_chance = 1.0
    AND dclocal_read_repair_chance = 0.1
    AND default_time_to_live = 0
    AND gc_grace_seconds = 864000
    AND max_index_interval = 2048
    AND memtable_flush_period_in_ms = 0
    AND min_index_interval = 128
    AND read_repair_chance = 0.0
    AND speculative_retry = '99PERCENTILE';

cqlsh:killrvideo> INSERT INTO videos (video_id, added_date, title) VALUES(1645ea59-14bd-11e5-a993-8138354b7e31, '2014-01-29', 'Cassandra History');
cqlsh:killrvideo> SELECT * FROM videos;

 video_id                             | added_date                      | title
--------------------------------------+---------------------------------+-------------------
 1645ea59-14bd-11e5-a993-8138354b7e31 | 2014-01-29 00:00:00.000000+0000 | Cassandra History
 
cqlsh:killrvideo> TRUNCATE videos;
cqlsh:killrvideo> SELECT * FROM videos;

 video_id | added_date | title
----------+------------+-------
```

- Load the data for the video table from a CSV file
```
cqlsh:killrvideo> COPY videos(video_id, added_date, title) 
              ... FROM '/home/iamsmkr/Downloads/datastax/ds201-6.0-labwork/labwork/data-files/videos.csv'
              ... WITH HEADER=true;
Using 3 child processes

Starting copy of killrvideo.videos with columns [video_id, added_date, title].
Processed: 5 rows; Rate:       8 rows/s; Avg. rate:      12 rows/s
5 rows imported from 1 files in 0.430 seconds (0 skipped).

cqlsh:killrvideo> SELECT * FROM videos;

 video_id                             | added_date                      | title
--------------------------------------+---------------------------------+------------------------------
 245e8024-14bd-11e5-9743-8238356b7e32 | 2012-04-03 00:00:00.000000+0000 |             Cassandra & SSDs
 3452f7de-14bd-11e5-855e-8738355b7e3a | 2013-03-17 00:00:00.000000+0000 |              Cassandra Intro
 5645f8bd-14bd-11e5-af1a-8638355b8e3a | 2013-04-16 00:00:00.000000+0000 | What is DataStax Enterprise?
 1645ea59-14bd-11e5-a993-8138354b7e31 | 2014-01-29 00:00:00.000000+0000 |            Cassandra History
 4845ed97-14bd-11e5-8a40-8338255b7e33 | 2013-10-16 00:00:00.000000+0000 |              DataStax Studio
 
 cqlsh:killrvideo> SELECT TOKEN(video_id), video_id FROM videos;

 system.token(video_id) | video_id
------------------------+--------------------------------------
   -7805440677194688247 | 245e8024-14bd-11e5-9743-8238356b7e32
   -1963973032031712291 | 3452f7de-14bd-11e5-855e-8738355b7e3a
   -1613479371119279545 | 5645f8bd-14bd-11e5-af1a-8638355b8e3a
    3855558958565172223 | 1645ea59-14bd-11e5-a993-8138354b7e31
    7966101712501124149 | 4845ed97-14bd-11e5-8a40-8338255b7e33
```
