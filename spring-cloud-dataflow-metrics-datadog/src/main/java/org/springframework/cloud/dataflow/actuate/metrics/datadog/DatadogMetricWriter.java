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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.coursera.metrics.datadog.DefaultMetricNameFormatter;
import org.coursera.metrics.datadog.MetricNameFormatter;
import org.coursera.metrics.datadog.model.DatadogGauge;
import org.coursera.metrics.datadog.transport.HttpTransport;
import org.coursera.metrics.datadog.transport.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private String hostname;

    public DatadogMetricWriter(HttpTransport transport,
                               MetricsPrefixResolver metricsPrefixResolver) {
        this.transport = transport;
        this.prefix = metricsPrefixResolver.getResolvedPrefix();
        this.hostname = getLocalHost().getCanonicalHostName();
    }

    @Override
    public void increment(Delta<?> delta) {
        // Not implemented
    }

    @Override
    public void reset(String metricName) {
        // Not implemented
    }

    @Override
    public void set(Metric<?> metric) {
        try {
            final Number value = metric.getValue();
            Transport.Request request = transport.prepare();
            log.info("Sending to DataDog " + prefix + ":" + metric);
            request.addGauge(new DatadogGauge(metricNameFormatter.format(prefix + "." + metric.getName()),
                    metric.getValue(), metric.getTimestamp().getTime() / 1000, hostname, tags));
            request.send();

        } catch (Throwable e) {
            log.error("Error reporting metrics to Datadog", e);
        }
    }

    public String getPrefix() {
        return prefix;
    }

    protected InetAddress getLocalHost() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }
}
