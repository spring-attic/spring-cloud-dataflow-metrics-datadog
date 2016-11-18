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


import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationPid;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DatadogApplicationTests {

    private String pid = new ApplicationPid().toString();

    @Autowired
    private DatadogMetricWriter datadogMetricWriter;

    @Value("${spring.cloud.client.hostname:hostname}")
    private String hostname;


    @Test
    public void defaultValues() {
        assertThat(datadogMetricWriter).isNotNull();
        assertThat(datadogMetricWriter.getPrefix()).isEqualTo("group.application." + pid);
        assertThat(datadogMetricWriter.getHostname()).isEqualTo(hostname);
    }


}
