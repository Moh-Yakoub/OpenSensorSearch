<%--

    ﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<?xml version="1.0" encoding="utf-8"?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">

<%@page import="org.n52.sir.client.Client"%>

<jsp:useBean id="insertSensorStatus"
	class="org.n52.sir.client.InsertSensorStatusBean" scope="page" />
<jsp:setProperty property="*" name="insertSensorStatus" />

<%
    if (request.getParameter("build") != null) {
        insertSensorStatus.buildRequest();
    }

    if (request.getParameter("sendRequest") != null) {
        insertSensorStatus.setResponseString(insertSensorStatus.sendRequest(insertSensorStatus.getRequestString()));
    }
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Insert Sensor Status Request</title>

<jsp:include page="htmlHead.jsp"></jsp:include>

</head>
<body onload="load()">

	<div id="content"><jsp:include page="header.jsp" />
		<jsp:include page="../menu.jsp" />

		<div id="pageContent">

			<h2>Insert Sensor Status Request:</h2>

			<form action="insertSensorStatus.jsp" method="post">

				<ul class="inputTablesList">
					<li>
						<p>Status Description</p>
						<table>
							<tr>
								<td class="inputTitle">SensorID In SIR</td>
								<td><input type="text" class="inputField"
									name="sensorIdValue" size="100"
									value="<%=insertSensorStatus.getSensorIdValue()%>" /></td>
							</tr>
							<tr>
								<td class="inputTitle">Property Name:</td>
								<td><input type="text" class="inputField"
									name="propertyName" size="100"
									value="<%=insertSensorStatus.getPropertyName()%>" /></td>
							</tr>
							<tr>
								<td class="inputTitle">Property Value:</td>
								<td><input type="text" class="inputField"
									name="propertyValue" size="100"
									value="<%=insertSensorStatus.getPropertyValue()%>" /></td>
							</tr>
							<tr>
								<td class="inputTitle">UOM (optional):</td>
								<td><input type="text" class="inputField" name="uom"
									size="100" value="<%=insertSensorStatus.getUom()%>" /></td>
							</tr>
							<tr>
								<td class="inputTitle">TimeStamp (optional):</td>
								<td><input type="text" class="inputField" name="timestamp"
									size="100" value="<%=insertSensorStatus.getTimestamp()%>" /></td>
							</tr>
						</table>
						<p>You can insert multiple StatusDesription elements manually
							in below before sending the request.</p>
					</li>
				</ul>

				<p>
					<input type="submit" name="build" value="Build request" />
				</p>
			</form>
			<form action="insertSensorStatus.jsp" method="post">
				<p class="textareaBorder">
					<textarea id="requestStringArea" class="mediumTextarea"
						name="requestString" rows="10" cols="10"><%=insertSensorStatus.getRequestString()%></textarea>
				</p>
				<p>
					<input type="submit" name="sendRequest" value="Send request" />
				</p>
			</form>
			<p class="textareaBorder">
				<textarea id="responseStringArea" class="mediumTextarea" rows="10"
					cols="10"><%=insertSensorStatus.getResponseString()%></textarea>
			</p>

		</div>
	</div>
</body>
</html>