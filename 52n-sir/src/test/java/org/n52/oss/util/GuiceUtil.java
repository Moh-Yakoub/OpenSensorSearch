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

package org.n52.oss.util;

import java.io.IOException;
import java.util.Properties;

import org.n52.sir.SirConfigurator;
import org.n52.sir.IT.GetCapabilitiesIT;
import org.n52.sir.client.Client;
import org.n52.sir.ds.IDAOFactory;
import org.n52.sir.ds.pgsql.DAOFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

public class GuiceUtil {

    private static Logger log = LoggerFactory.getLogger(GuiceUtil.class);

    public static Client configureSirClient() {
        Injector i = Guice.createInjector(new AbstractModule() {

            @Override
            protected void configure() {
                bindConstant().annotatedWith(Names.named("oss.sir.serviceurl")).to("http://localhost:8080/OpenSensorSearch/sir");
                bind(Client.class);
                log.info("Configured client for tests.");
            }
        });

        return i.getInstance(Client.class);
    }

    /**
     * binds all the properties files as a side effect
     * 
     * @return
     */
    public static SirConfigurator configureSirConfigurator() {
        Injector i = Guice.createInjector(new AbstractModule() {

            @Override
            protected void configure() {
                try {
                    Properties sirProps = new Properties();
                    Properties dbProps = new Properties();
                    sirProps.load(GuiceUtil.class.getResourceAsStream("/prop/sir.properties"));
                    dbProps.load(GuiceUtil.class.getResourceAsStream("/prop/db.properties"));
                    Names.bindProperties(binder(), sirProps);
                    Names.bindProperties(binder(), dbProps);

                    bindConstant().annotatedWith(Names.named("context.basepath")).to("TEST_BASEPATH");
                    bind(IDAOFactory.class).to(DAOFactory.class);
                }
                catch (IOException e) {
                    log.error("Could not bind properties.", e);
                }

                bind(SirConfigurator.class);
                log.info("Configured SirConfigurator for tests.");
            }
        });

        SirConfigurator sc = i.getInstance(SirConfigurator.class);
        log.info("SirConfigurator: {}", sc);
        return sc;
    }

    public static Injector configurePropertiesFiles() {
        Injector i = Guice.createInjector(new AbstractModule() {

            @Override
            protected void configure() {
                try {
                    Properties sirProps = new Properties();
                    Properties dbProps = new Properties();
                    sirProps.load(GetCapabilitiesIT.class.getResourceAsStream("/prop/sir.properties"));
                    dbProps.load(GetCapabilitiesIT.class.getResourceAsStream("/prop/db.properties"));
                    Names.bindProperties(binder(), sirProps);
                    Names.bindProperties(binder(), dbProps);

                    log.info("Bound properties for tests: \n\t{}\n\t{}", sirProps.toString(), dbProps.toString());
                }
                catch (IOException e) {
                    log.error("Could not bind properties.", e);
                }
            }
        });
        
        return i;
    }

}
