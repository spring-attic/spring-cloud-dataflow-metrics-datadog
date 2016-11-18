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

import java.util.ArrayList;
import java.util.List;

import org.coursera.metrics.datadog.DefaultMetricNameFormatter;
import org.coursera.metrics.datadog.MetricNameFormatter;
import org.coursera.metrics.datadog.model.DatadogCounter;
import org.coursera.metrics.datadog.model.DatadogGauge;
import org.coursera.metrics.datadog.transport.HttpTransport;
import org.coursera.metrics.datadog.transport.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.writer.Delta;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.cloud.dataflow.metrics.MetricsPrefixResolver;

public class DatadogMetricWriter implements MetricWriter {

    private static Logger log = LoggerFactory.getLogger(DatadogMetricWriter.class);

    private HttpTransport transport;

    private MetricNameFormatter metricNameFormatter = new DefaultMetricNameFormatter();

    private List<String> tags = new ArrayList<String>();

    private String prefix;

    @Value("${spring.cloud.client.hostname:hostname}")
    private String hostname;

    public DatadogMetricWriter(HttpTransport transport,
                               MetricsPrefixResolver metricsPrefixResolver) {
        this.transport = transport;
        this.prefix = metricsPrefixResolver.getResolvedPrefix();
    }

    public String getHostname() {
        return hostname;
    }

    @Override
    public void increment(Delta<?> delta) {
        try {
            Transport.Request request = transport.prepare();
            request.addCounter(new DatadogCounter(getDatadogPrefix(delta), delta.getValue().longValue(),
                    getDatadogTimestamp(delta), hostname, tags));
        } catch (Throwable e) {
            log.error("Error reporting metrics to Datadog", e);
        }
    }

    @Override
    public void reset(String metricName) {
        // Not implemented
    }

    @Override
    public void set(Metric<?> metric) {
        try {
            Transport.Request request = transport.prepare();
            if (log.isDebugEnabled()) {
                log.info("Sending to Datadog " + "Metric [name=" + prefix + "." + metric.getName() + ", value=" +
                        metric.getValue() + ", timestamp=" + metric.getTimestamp() + "]");
            }
            request.addGauge(new DatadogGauge(getDatadogPrefix(metric),
                    metric.getValue(), getDatadogTimestamp(metric), hostname, tags));
            request.send();

        } catch (Throwable e) {
            log.error("Error reporting metrics to Datadog", e);
        }
    }

    private long getDatadogTimestamp(Metric<?> metric) {
        return metric.getTimestamp().getTime() / 1000;
    }

    private String getDatadogPrefix(Metric<?> metric) {
        return metricNameFormatter.format(prefix + "." + metric.getName());
    }

    public String getPrefix() {
        return prefix;
    }



}
