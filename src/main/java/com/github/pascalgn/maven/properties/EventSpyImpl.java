/*
 * Copyright 2018 Pascal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pascalgn.maven.properties;

import java.util.Map;

import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.eventspy.EventSpy;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

/**
 * Main entry point. Reads properties and exposes them as user properties.
 * Existing user properties will not be overwritten.
 */
@Component(role = EventSpy.class)
public class EventSpyImpl extends AbstractEventSpy {
    @Requirement
    private Logger logger;

    @Override
    public void init(Context context) throws Exception {
        Map<String, Object> data = context.getData();
        Object userPropertiesObj = data.get("userProperties");
        if (userPropertiesObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> userProperties = (Map<String, Object>) userPropertiesObj;
            addProperties(userProperties);
        }
    }

    private void addProperties(Map<String, Object> userProperties) {
        Map<String, String> gitProperties = new GitProperties(logger).getProperties();
        for (Map.Entry<String, String> entry : gitProperties.entrySet()) {
            userProperties.putIfAbsent(entry.getKey(), entry.getValue());
        }
    }
}
