/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */

/**
 * @author Yakoub
 */

package org.n52.sir.ds.solr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.opengis.sensorML.x101.KeywordsDocument.Keywords;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SensorMLDocument.SensorML;

import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.common.SolrDocument;
import org.apache.xmlbeans.XmlException;
import org.junit.Before;
import org.junit.Test;
import org.n52.sir.datastructure.SirSearchCriteria;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.SirSensor;
import org.n52.sir.datastructure.SirSensorDescription;
import org.n52.sir.datastructure.SirSimpleSensorDescription;
import org.n52.sir.datastructure.solr.SirSolrSensorDescription;
import org.n52.sir.ows.OwsExceptionReport;

public class SearchByLocationTest {
	@Before
	public void insertSensor() throws XmlException, IOException,
			OwsExceptionReport {
		/*
		 * Insert testSensor for search
		 */
		File sensor_file = new File(ClassLoader.getSystemResource(
				"Requests/testsensor.xml").getFile());

		SensorMLDocument doc = SensorMLDocument.Factory.parse(sensor_file);
		SirSensor sensor = new SirSensor();
		sensor.setSensorMLDocument(doc);
		/*
		 * Inserts this sensor
		 */
		// probably this will take some configuration - haven't decided yet.
		SOLRInsertSensorInfoDAO dao = new SOLRInsertSensorInfoDAO();
		dao.insertSensor(sensor);
	}

	@Test
	public void searchByLocation() throws OwsExceptionReport, XmlException,
			IOException {
		//
		SOLRSearchSensorDAO searchDAO = new SOLRSearchSensorDAO();
		/*
		 * Prepare the list of keywords
		 */
		String lat = "1.5";
		String lng = "3.49";
		Collection<SirSearchResultElement> results = searchDAO
				.searchSensorByLocation(lat, lng, 10);

		assertNotNull(results);
		assertEquals(results.size(), 1);

		Iterator<SirSearchResultElement> iter = results.iterator();
		SirSearchResultElement result = iter.next();
		// SensorML is stored in the sensor description value
		SirSolrSensorDescription description = (SirSolrSensorDescription) result
				.getSensorDescription();
		assertNotNull(description);
		assertTrue("urn:ogc:object:feature:testsensor".equals(description.getId()));
	}
	/*
	 * Searches for a sensor but not in the range covered , should return 0
	 */
	@Test
	public void searchByLocationNotInRange() throws OwsExceptionReport, XmlException,
			IOException {
		//
		SOLRSearchSensorDAO searchDAO = new SOLRSearchSensorDAO();
		/*
		 * Prepare the list of keywords
		 */
		String lat = "1";
		String lng = "3";
		Collection<SirSearchResultElement> results = searchDAO
				.searchSensorByLocation(lat, lng, 10);

		assertNotNull(results);
		assertEquals(results.size(), 0);

		}
}