/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.dataflow.actuate.metrics.datadog;

import org.coursera.metrics.datadog.transport.HttpTransport;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.dataflow.metrics.MetricsPrefixResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(HttpTransport.class)
@ConditionalOnProperty("spring.cloud.dataflow.datadog.api-key")
@EnableConfigurationProperties(DatadogProperties.class)
public class DatadogAutoConfiguration {

    private final DatadogProperties properties;

    public DatadogAutoConfiguration(DatadogProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean(HttpTransport.class)
    public HttpTransport httpTransport() {
        return new HttpTransport.Builder().withApiKey(properties.getApiKey()).build();
    }

    @Bean
    public DatadogMetricWriter datadogMetricWriter(HttpTransport httpTransport,
                                                   MetricsPrefixResolver metricsPrefixResolver) {
        return new DatadogMetricWriter(httpTransport, metricsPrefixResolver);
    }

}
