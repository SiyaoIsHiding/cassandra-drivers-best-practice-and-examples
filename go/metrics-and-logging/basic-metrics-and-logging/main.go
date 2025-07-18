package main

import (
	"context"
	"github.com/gocql/gocql"
	"log"
	"os"
)

type myQueryObserver struct {
	logger log.Logger
}

func (o *myQueryObserver) ObserveQuery(ctx context.Context, q gocql.ObservedQuery) {
	o.logger.Printf("Query: %s, Latency: %v, Host: %s\n", q.Statement, q.Metrics.TotalLatency, q.Host.HostnameAndPort())
}

func main() {
	cluster := gocql.NewCluster("127.0.0.1:9043", "127.0.0.1:9044", "127.0.0.1:9045")
	cluster.PoolConfig.HostSelectionPolicy = gocql.TokenAwareHostPolicy(gocql.DCAwareRoundRobinPolicy("datacenter1"))
	//cluster.Logger = log.New(os.Stdout, "gocql: ", log.LstdFlags)
	session, err := cluster.CreateSession()
	if err != nil {
		log.Fatalf("Error creating session: %v\n", err)
		return
	}
	defer session.Close()

	queryObserver := &myQueryObserver{logger: *log.New(os.Stdout, "QueryObserver: ", log.LstdFlags)}

	ctx := context.Background()

	//var host_id string
	//var broadcast_address string
	session.Query("SELECT host_id, broadcast_address FROM system.local").WithContext(ctx).Observer(queryObserver).Iter().Scanner()
	//for scanner.Next() {
	//	err := scanner.Scan(&host_id, &broadcast_address)
	//	if err != nil {
	//		log.Fatalf("Error scanning: %v\n", err)
	//		return
	//	}
	//	log.Printf("host_id: %s, broadcast_address: %s\n", host_id, broadcast_address)
	//}
}
