from cassandra.cluster import Cluster
from cassandra import metadata, cluster, connection

import logging

from cassandra.policies import DCAwareRoundRobinPolicy, TokenAwarePolicy


def config_logger():
    logging.basicConfig(level=logging.DEBUG, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
    # setting default log level for the whole driver
    logging.getLogger('cassandra').setLevel(logging.INFO)
    # example to debug topology events
    logging.getLogger(metadata.__name__).setLevel(logging.DEBUG)
    logging.getLogger(cluster.__name__).setLevel(logging.DEBUG)
    logging.getLogger(connection.__name__).setLevel(logging.DEBUG)


def request_logger(rf: cluster.ResponseFuture):
    logger = logging.getLogger(__name__)

    def on_success(_, response_future: cluster.ResponseFuture):
        logger.debug("Success: %s; coordinator: %s", response_future.query, response_future.coordinator_host)

    def on_error(_, response_future: cluster.ResponseFuture):
        logger.warning("Error: %s; coordinator: %s", response_future.query, response_future.coordinator_host)

    rf.add_callbacks(on_success, on_error, callback_args=(rf,), errback_args=(rf,))


if __name__ == '__main__':
    config_logger()
    cl = Cluster(['127.0.0.1'], port=9043, protocol_version=5,
                 load_balancing_policy=TokenAwarePolicy(DCAwareRoundRobinPolicy(local_dc='datacenter1')))
    session = cl.connect()
    session.add_request_init_listener(request_logger)
    rows = session.execute('SELECT * FROM system.local')
