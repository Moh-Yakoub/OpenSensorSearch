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
/**
 * @author Yakoub
 */

package org.n52.sir.IT;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.n52.sir.client.Client;
import org.n52.sir.ows.OwsExceptionReport;
import org.x52North.sir.x032.InsertSensorStatusRequestDocument;
import org.x52North.sir.x032.InsertSensorStatusResponseDocument;

public class InsertSensorStatusIT {

    @Test
    public void insertSensorStatus() throws XmlException, IOException, OwsExceptionReport, HttpException {
        XmlObject res = null;
        /*
         * Create a sensor insert request from sensorFile
         */
        File sensor_status = new File(ClassLoader.getSystemResource("Requests/InsertSensorStatus.xml").getFile());
        InsertSensorStatusRequestDocument req = InsertSensorStatusRequestDocument.Factory.parse(sensor_status);

        res = Client.xSendPostRequest(req);

        InsertSensorStatusResponseDocument res_doc = InsertSensorStatusResponseDocument.Factory.parse(res.getDomNode());

        assertTrue("StatusId not equal",
                   res_doc.getInsertSensorStatusResponse().getSensorIDInSIR().equals(req.getInsertSensorStatusRequest().getStatusDescription().getSensorIDInSIR()));

    }

}
