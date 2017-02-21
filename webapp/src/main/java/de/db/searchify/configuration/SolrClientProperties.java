/*
 * Copyright (c) 2017 Redlink GmbH.
 */
package de.db.searchify.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 */
@ConfigurationProperties(prefix = "solr")
public class SolrClientProperties {


    private String zkConnection;
    private String baseUrl;

    public String getZkConnection() {
        return zkConnection;
    }

    public void setZkConnection(String zkConnection) {
        this.zkConnection = zkConnection;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
