package com.datastax.examples;

import com.codahale.metrics.MetricRegistry;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Optional;

public class Main {
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        DefaultExports.initialize();
        Optional<HTTPServer> prometheusServer = Optional.empty();
        try {
            prometheusServer = Optional.of(new HTTPServer(9095));
        } catch (IOException e) {
            System.out.println("Exception when creating HTTP server for Prometheus: " + e.getMessage());
        }

        try (CqlSession session = CqlSession.builder().build()) {
            MetricRegistry registry = session.getMetrics()
                    .orElseThrow(() -> new IllegalStateException("Metrics are not enabled"))
                    .getRegistry();
            CollectorRegistry.defaultRegistry.register(new DropwizardExports(registry));
            for (int i = 0; i < 100000; i++) {
                session.executeAsync(SimpleStatement.newInstance("SELECT release_version FROM system.local").setIdempotent(true))
                        .whenComplete((rs, error) -> {
                            if (error != null) {
                                LOG.error("Error executing query on release_version", error);
                            } else {
                                LOG.info("Successfully fetching release_version: " + rs.one().getString("release_version"));
                            }
                        });
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            LOG.error("Error creating session" + e);
        }
        prometheusServer.ifPresent(HTTPServer::stop);
    }
}