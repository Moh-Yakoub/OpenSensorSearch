/**
 * ﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
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
package org.n52.sir.json;

import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SerializeService {

    private ObjectMapper mapper;
    private ByteArrayOutputStream baos;

    @Before
    public void setUp() throws Exception {
        this.mapper = MapperFactory.getMapper();
        this.baos = new ByteArrayOutputStream();
    }

    @Test
    public void test() throws Exception {
        Service service = new Service("http://host:port/path", "SOS");

        this.mapper.writeValue(this.baos, service);
        
        System.out.println(new String(this.baos.toByteArray()));
        
        // TODO add assertion to test
    }

}
