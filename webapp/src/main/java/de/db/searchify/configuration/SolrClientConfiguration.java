/*
 * Copyright (c) 2017 Redlink GmbH.
 */
package de.db.searchify.configuration;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 */
@Configuration
@EnableConfigurationProperties(SolrClientProperties.class)
public class SolrClientConfiguration {

    private final SolrClientProperties properties;

    @Autowired
    public SolrClientConfiguration(SolrClientProperties properties) {
        this.properties = properties;
    }

    @Bean
    public SolrClient solrClient() {
        if (StringUtils.isNotBlank(properties.getZkConnection())) {
            return cloudSolrClient();
        } else if (StringUtils.isNotBlank(properties.getBaseUrl())) {
            return httpSolrClient();
        } else {
            throw new NoSuchBeanDefinitionException(SolrClient.class);
        }
    }

    private HttpSolrClient httpSolrClient() {
        Preconditions.checkArgument(StringUtils.isNotBlank(properties.getBaseUrl()));
        return new HttpSolrClient.Builder()
                .withBaseSolrUrl(properties.getBaseUrl())
                .build();
    }

    private CloudSolrClient cloudSolrClient() {
        Preconditions.checkArgument(StringUtils.isNotBlank(properties.getZkConnection()));
        return new CloudSolrClient.Builder()
                .withZkHost(properties.getZkConnection())
                .build();
    }
}
