package com.datastax.examples;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import org.slf4j.Logger;

public class Main {
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try(CqlSession session = CqlSession.builder().build()) {
            for(int i = 0; i < 100000; i++) {
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
        }catch (Exception e){
            LOG.error("Error creating session" + e);
        }
    }
}