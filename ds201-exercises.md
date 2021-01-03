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

### Exercise 3 – Partitions
- Create table videos_by_tag to store video data categorised by tags
	```
	cqlsh:killrvideo> CREATE TABLE videos_by_tag (video_id timeuuid, added_date timestamp, title text, tag text, primary key ((tag), video_id));
	cqlsh:killrvideo> DESC TABLE videos_by_tag ;

	CREATE TABLE killrvideo.videos_by_tag (
		tag text,
		video_id timeuuid,
		added_date timestamp,
		title text,
		PRIMARY KEY (tag, video_id)
	) WITH CLUSTERING ORDER BY (video_id ASC)
		AND bloom_filter_fp_chance = 0.01
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
	```

- Load the data for the video table from a CSV file
	```
	cqlsh:killrvideo> COPY videos_by_tag (tag, video_id, added_date, title) 
				  ... FROM '/home/iamsmkr/Downloads/datastax/ds201-6.0-labwork/labwork/data-files/videos-by-tag.csv'
				  ... WITH HEADER=true;
	Using 3 child processes

	Starting copy of killrvideo.videos_by_tag with columns [tag, video_id, added_date, title].
	Processed: 5 rows; Rate:       7 rows/s; Avg. rate:      11 rows/s
	5 rows imported from 1 files in 0.443 seconds (0 skipped).
	cqlsh:killrvideo> SELECT * FROM videos_by_tag ;

	 tag       | video_id                             | added_date                      | title
	-----------+--------------------------------------+---------------------------------+------------------------------
	  datastax | 4845ed97-14bd-11e5-8a40-8338255b7e33 | 2013-10-16 00:00:00.000000+0000 |              DataStax Studio
	  datastax | 5645f8bd-14bd-11e5-af1a-8638355b8e3a | 2013-04-16 00:00:00.000000+0000 | What is DataStax Enterprise?
	 cassandra | 1645ea59-14bd-11e5-a993-8138354b7e31 | 2014-01-29 00:00:00.000000+0000 |            Cassandra History
	 cassandra | 245e8024-14bd-11e5-9743-8238356b7e32 | 2012-04-03 00:00:00.000000+0000 |             Cassandra & SSDs
	 cassandra | 3452f7de-14bd-11e5-855e-8738355b7e3a | 2013-03-17 00:00:00.000000+0000 |              Cassandra Intro

	(5 rows)
	cqlsh:killrvideo> SELECT * FROM videos_by_tag WHERE tag='cassandra';

	 tag       | video_id                             | added_date                      | title
	-----------+--------------------------------------+---------------------------------+-------------------
	 cassandra | 1645ea59-14bd-11e5-a993-8138354b7e31 | 2014-01-29 00:00:00.000000+0000 | Cassandra History
	 cassandra | 245e8024-14bd-11e5-9743-8238356b7e32 | 2012-04-03 00:00:00.000000+0000 |  Cassandra & SSDs
	 cassandra | 3452f7de-14bd-11e5-855e-8738355b7e3a | 2013-03-17 00:00:00.000000+0000 |   Cassandra Intro

	(3 rows)
	cqlsh:killrvideo> select * from videos_by_tag where title='Cassandra Intro';
	InvalidRequest: Error from server: code=2200 [Invalid query] message="Cannot execute this query as it might involve data filtering and thus may have unpredictable performance. If you want to execute this query despite the performance unpredictability, use ALLOW FILTERING"
	```

### Exercise 4 - Clustering Columns
- Create table videos_by_tags to incorporate desc order by added_date
	```
	cqlsh:killrvideo> CREATE TABLE videos_by_tag ( tag text, video_id uuid, added_date timestamp, title text, PRIMARY KEY ((tag), added_date, video_id)) WITH CLUSTERING ORDER BY (added_date DESC);
	cqlsh:killrvideo> DESC videos_by_tag;

	CREATE TABLE killrvideo.videos_by_tag (
		tag text,
		added_date timestamp,
		video_id uuid,
		title text,
		PRIMARY KEY (tag, added_date, video_id)
	) WITH CLUSTERING ORDER BY (added_date DESC, video_id ASC)
		AND bloom_filter_fp_chance = 0.01
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
	```

- Load data
	```
	cqlsh:killrvideo> COPY videos_by_tag(tag, video_id, added_date, title) FROM '/home/codingkapoor/Downloads/datastax/ds201-6.0-labwork/labwork/data-files/videos-by-tag.csv' WITH HEADER = true ;
	Using 3 child processes

	Starting copy of killrvideo.videos_by_tag with columns [tag, video_id, added_date, title].
	Processed: 5 rows; Rate:       8 rows/s; Avg. rate:      12 rows/s
	5 rows imported from 1 files in 0.414 seconds (0 skipped).
	cqlsh:killrvideo> SELECT * FROM videos_by_tag;

	 tag       | added_date                      | video_id                             | title
	-----------+---------------------------------+--------------------------------------+------------------------------
	  datastax | 2013-10-16 00:00:00.000000+0000 | 4845ed97-14bd-11e5-8a40-8338255b7e33 |              DataStax Studio
	  datastax | 2013-04-16 00:00:00.000000+0000 | 5645f8bd-14bd-11e5-af1a-8638355b8e3a | What is DataStax Enterprise?
	 cassandra | 2014-01-29 00:00:00.000000+0000 | 1645ea59-14bd-11e5-a993-8138354b7e31 |            Cassandra History
	 cassandra | 2013-03-17 00:00:00.000000+0000 | 3452f7de-14bd-11e5-855e-8738355b7e3a |              Cassandra Intro
	 cassandra | 2012-04-03 00:00:00.000000+0000 | 245e8024-14bd-11e5-9743-8238356b7e32 |             Cassandra & SSDs

	(5 rows)
	```

- Query records ordered by date
	```
	cqlsh:killrvideo> select * from videos_by_tag where tag='cassandra' ORDER BY added_date;

	 tag       | added_date                      | title             | video_id
	-----------+---------------------------------+-------------------+--------------------------------------
	 cassandra | 2012-04-03 00:00:00.000000+0000 |  Cassandra & SSDs | 245e8024-14bd-11e5-9743-8238356b7e32
	 cassandra | 2013-03-17 00:00:00.000000+0000 |   Cassandra Intro | 3452f7de-14bd-11e5-855e-8738355b7e3a
	 cassandra | 2014-01-29 00:00:00.000000+0000 | Cassandra History | 1645ea59-14bd-11e5-a993-8138354b7e31

	(3 rows)
	```

- Query records orered by date that are older than 2013
	```
	cqlsh:killrvideo> select * from videos_by_tag where tag='cassandra' and added_date >= '2013-01-01' ORDER BY added_date;

	 tag       | added_date                      | title             | video_id
	-----------+---------------------------------+-------------------+--------------------------------------
	 cassandra | 2013-03-17 00:00:00.000000+0000 |   Cassandra Intro | 3452f7de-14bd-11e5-855e-8738355b7e3a
	 cassandra | 2014-01-29 00:00:00.000000+0000 | Cassandra History | 1645ea59-14bd-11e5-a993-8138354b7e31

	(2 rows)
	```

### Exercise 5 - Drivers
https://github.com/iamsmkr/datastax-cassandra/tree/main/exercises

### Exercise 6 - Node
- Execute nodetool with the `help` command to list all possible commands
	```
	$ nodetool help
	usage: nodetool [(-u <username> | --username <username>)]
			[(-pw <password> | --password <password>)] [(-h <host> | --host <host>)]
			[(-p <port> | --port <port>)]
			[(-pwf <passwordFilePath> | --password-file <passwordFilePath>)] <command>
			[<args>]

	The most commonly used nodetool commands are:
		assassinate                  Forcefully remove a dead node without re-replicating any data.  Use as a last resort if you cannot removenode
		bootstrap                    Monitor/manage node's bootstrap process
		cleanup                      Triggers the immediate cleanup of keys no longer belonging to a node. By default, clean all keyspaces
		clearsnapshot                Remove the snapshot with the given name from the given keyspaces. If no snapshotName is specified we will remove all snapshots
		compact                      Force a (major) compaction on one or more tables or user-defined compaction on given SSTables
	...
	```

- The `status` command shows information about the entire cluster, particularly the state of each node, and information about each of those nodes: IP address, data load, number of tokens,  total percentage of data saved on each node, host ID, and datacenter and rack.
	```
	$ nodetool status
	Datacenter: datacenter1
	=======================
	Status=Up/Down
	|/ State=Normal/Leaving/Joining/Moving
	--  Address    Load       Tokens       Owns    Host ID                               Rack
	UN  127.0.0.1  784.5 KiB  256          ?       d5a70cb7-1fcf-49f2-9b04-20aa95924976  rack1
	```

- The `info` command displays information about the connected node, which includes token information, host ID, protocol status, data load, node uptime, heap memory usage and capacity, datacenter and rack information, number of errors reported, cache usage, and percentage of SSTables that have been incrementally repaired.
	```
	$ nodetool info
	ID                     : d5a70cb7-1fcf-49f2-9b04-20aa95924976
	Gossip active          : true
	Thrift active          : false
	Native Transport active: true
	Load                   : 784.5 KiB
	Generation No          : 1609413858
	Uptime (seconds)       : 263319
	Heap Memory (MB)       : 861.35 / 3948.00
	Off Heap Memory (MB)   : 0.00
	Data Center            : datacenter1
	Rack                   : rack1
	Exceptions             : 0
	Key Cache              : entries 45, size 3.84 KiB, capacity 100 MiB, 554 hits, 643 requests, 0.862 recent hit rate, 14400 save period in seconds
	Row Cache              : entries 0, size 0 bytes, capacity 0 bytes, 0 hits, 0 requests, NaN recent hit rate, 0 save period in seconds
	Counter Cache          : entries 0, size 0 bytes, capacity 50 MiB, 0 hits, 0 requests, NaN recent hit rate, 7200 save period in seconds
	Chunk Cache            : entries 20, size 1.25 MiB, capacity 480 MiB, 1489 misses, 3645 requests, 0.591 recent hit rate, NaN microseconds miss latency
	Percent Repaired       : 0.0%
	Token                  : (invoke with -T/--tokens to see all 256 tokens)
	```

- The `describecluster` shows the settings that are common across all of the nodes in the cluster and the current schema version used by each node.
	```
	$ nodetool describecluster
	Cluster Information:
		Name: Test Cluster
		Snitch: org.apache.cassandra.locator.SimpleSnitch
		DynamicEndPointSnitch: enabled
		Partitioner: org.apache.cassandra.dht.Murmur3Partitioner
		Schema versions:
			25413619-18f1-30be-b35a-5234fc842b6f: [127.0.0.1]
	```

- The command `setlogginglevel` dynamically changes the logging level used by Apache Cassandra™ without the need for a restart. You can also look at the `/var/log/cassandra/system.log` afterwards to observe the changes.
	```
	$ nodetool setlogginglevel org.apache.cassandra TRACE

	$ nodetool getlogginglevels
	Logger Name                                        Log Level
	ROOT                                                    INFO
	com.thinkaurelius.thrift                               ERROR
	org.apache.cassandra                                   TRACE
	```

- The resultant value from the `settraceprobability` command represents a decimal describing the percentage of queries being saved, starting from 0 (0%) to 1 (100%). Saved traces can then be viewed in the `system_traces` keyspace.
	```
	$ nodetool settraceprobability 0.1

	$ nodetool gettraceprobability
	Current trace probability: 0.1
	```

- The `drain` command stops writes from occurring on the node and flushes all data to disk. Typically, this command may be run before stopping an Apache Cassandra™ node.
	```
	$ nodetool drain
	```

- The `stopdaemon` command stops a node's execution. Wait for it to complete. 
	```
	$ nodetool stopdaemon

	# Restart your node by running:
	/opt/cassandra/current/bin cassandra
	```

- We can stress the node using a simple tool called Apache Cassandra(TM) Stress, like so:
	```
	$ /opt/cassandra/current/tools/bin/cassandra-stress write n=50000 no-warmup -rate threads=1
	```

	Initially, we will see a long list of setting for the stress run. As Apache Cassandra™ stress executes, it logs several statistics to the terminal. Each line displays the statistics for the operations that occurred each second and shows number of partitions written, operations per second, latency information, and more.
	 Now with the `flush` command commits all written (memtable, discussed later) data to disk. Unlike drain, flush allows further writes to occur.
	```
	$ /opt/cassandra/current/bin/nodetool flush
	```
	
	Next we can examine the data cassandra-stress wrote to our node.
	```
	cqsh> SELECT * FROM keyspace1.standard1 LIMIT 5;
	```
