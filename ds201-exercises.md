## DS201: DataStax Enterprise 6 Foundations of Apache Cassandra™

### Exercise 1 - Install and Start Apache Cassandra™
- To check the status of running Cassandra instance
```
$ nodetool status
Datacenter: datacenter1
=======================
Status=Up/Down
|/ State=Normal/Leaving/Joining/Moving
--  Address    Load       Tokens       Owns    Host ID                               Rack
UN  127.0.0.1  795.3 KiB  256          ?       d5a70cb7-1fcf-49f2-9b04-20aa95924976  rack1

Note: Non-system keyspaces don't have the same replication settings, effective ownership information is meaningless
```

