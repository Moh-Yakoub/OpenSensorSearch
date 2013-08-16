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
package org.n52.sir.ds.solr.data;

/**
 * @author Yakoub
 */
import static org.junit.Assert.assertNotEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.UnavailableException;

import net.opengis.sensorML.x101.KeywordsDocument.Keywords.KeywordList;
import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.http.HttpException;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.n52.sir.SirConfigurator;
import org.n52.sir.client.Client;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.datastructure.SirTimePeriod;
import org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO;
import org.n52.sir.ds.solr.data.ds.JSONSensor;
import org.n52.sir.ds.solr.data.ds.JSONSensorsCollection;
import org.n52.sir.ows.OwsExceptionReport;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument;
import org.x52North.sir.x032.InsertSensorInfoResponseDocument;

import com.google.gson.Gson;

public class TempelateSensorTest {

	@Before
	public void prepare() throws UnavailableException, OwsExceptionReport {

		if (SirConfigurator.getInstance() == null) {
			InputStream dbStream = ClassLoader
					.getSystemResourceAsStream("prop/db.PROPERTIES");
			InputStream sirStream = ClassLoader
					.getSystemResourceAsStream("prop/sir.PROPERTIES");
			// Read configurator if null
			SirConfigurator.getInstance(sirStream, dbStream, null, null);

		}
	}

	public void parseJsonSensorsAndInsert() throws IOException,
			OwsExceptionReport, XmlException, HttpException {

		File sensor_file = new File(ClassLoader.getSystemResource(
				"data/randomSensors.json").getFile());
		File sensor_temp = new File(ClassLoader.getSystemResource(
				"AirBase-test.xml").getFile());
		SensorMLDocument DOC = SensorMLDocument.Factory.parse(sensor_temp);
		Gson gson = new Gson();
		StringBuilder builder = new StringBuilder();
		String s;
		BufferedReader reader = new BufferedReader(
				(new FileReader(sensor_file)));
		while ((s = reader.readLine()) != null)
			builder.append(s);
		JSONSensorsCollection collection = gson.fromJson(builder.toString(),
				JSONSensorsCollection.class);
		Iterator<JSONSensor> sensors = collection.sensors.iterator();
		while (sensors.hasNext()) {
			SirSensor sensor = new SirSensor();
			JSONSensor jsensor = sensors.next();
			sensor.setKeywords(jsensor.keywords);
			SirTimePeriod period = new SirTimePeriod();
			DateTime begin = DateTime.parse(jsensor.beginPosition);
			DateTime end = DateTime.parse(jsensor.endPosition);
			// fix because we need start < end , and the data is randomly
			// generated
			if (begin.getMillis() > end.getMillis()) {
				DateTime temp = begin;
				end = begin;
				begin = temp;
			}
			period.setStartTime(begin.toDate());
			period.setEndTime(end.toDate());
			sensor.setTimePeriod(period);
			sensor.setIdentificationsList(jsensor.Identifiers);
			List<String> contacts = new ArrayList<String>();
			contacts.add(jsensor.contacts);
			sensor.setContacts(contacts);
			
			/*
			 * Add to SIRPQSQL
			 */
			KeywordList klist = KeywordList.Factory.newInstance();
			klist.setKeywordArray(jsensor.keywords.toArray(new String[]{}));
			DOC.getSensorML().getMemberArray(0).getProcess().getKeywordsArray(0).setKeywordList(klist);
			InsertSensorInfoRequestDocument req = InsertSensorInfoRequestDocument.Factory
					.newInstance();
			req.addNewInsertSensorInfoRequest()
					.addNewInfoToBeInserted()
					.setSensorDescription(
							DOC.getSensorML().getMemberArray(0).getProcess());
			XmlObject res = Client.xSendPostRequest(req);
			InsertSensorInfoResponseDocument resp = InsertSensorInfoResponseDocument.Factory
					.parse(res.getDomNode());
			assertNotEquals("Failed to insert sensor",
					resp.getInsertSensorInfoResponse()
							.getNumberOfInsertedSensors(), 0);
			/*
			 * Insert into apache solr
			 */
			// SOLRInsertSensorInfoDAO dao = new SOLRInsertSensorInfoDAO();
			// dao.insertSensor(sensor);
		}

	}

}
