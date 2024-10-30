from cassandra.cluster import Cluster
from cassandra import metadata, cluster

import logging

from cassandra.policies import DCAwareRoundRobinPolicy, TokenAwarePolicy


def config_logger():
    logging.basicConfig(level=logging.DEBUG, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
    # setting default log level for the whole driver
    logging.getLogger('cassandra').setLevel(logging.INFO)
    # example to debug topology events
    logging.getLogger(metadata.__name__).setLevel(logging.DEBUG)
    logging.getLogger(cluster.__name__).setLevel(logging.DEBUG)


if __name__ == '__main__':
    config_logger()
    cluster = Cluster(['127.0.0.1'], port=9043,
                      load_balancing_policy=TokenAwarePolicy(DCAwareRoundRobinPolicy(local_dc='datecenter1')))
    session = cluster.connect()
    rows = session.execute('SELECT * FROM system.local')
