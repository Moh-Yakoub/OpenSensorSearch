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
package org.n52.sir.ds.pgsql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import org.n52.sir.datastructure.SirService;
import org.n52.sir.ds.IGetAllServicesDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.ows.OwsExceptionReport.ExceptionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte
 * 
 */
public class PGSQLGetAllServicesDAO implements IGetAllServicesDAO {

    /**
     * the logger, used to log exceptions and additionally information
     */
    private static Logger log = LoggerFactory.getLogger(PGSQLGetAllServicesDAO.class);

    /**
     * Connection pool for creating connections to the DB
     */
    private PGConnectionPool cpool;

    /**
     * constructor
     * 
     * @param cpool
     *        the connection pool containing the connections to the DB
     */
    public PGSQLGetAllServicesDAO(PGConnectionPool cpool) {
        this.cpool = cpool;
    }

    @Override
    public Collection<SirService> getServices() throws OwsExceptionReport {
        ArrayList<SirService> result = new ArrayList<SirService>();

        Connection con = null;
        Statement stmt = null;

        StringBuffer query = new StringBuffer();

        // build query
        query.append("SELECT ");
        query.append(PGDAOConstants.serviceUrl);
        query.append(", ");
        query.append(PGDAOConstants.serviceType);
        query.append(" FROM ");
        query.append(PGDAOConstants.service);
        query.append(";");

        // execute query
        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();
            if (log.isDebugEnabled())
                log.debug(">>>Database Query: " + query.toString());
            ResultSet rs = stmt.executeQuery(query.toString());

            // if no phenomenon available give back empty list
            if (rs == null) {
                return result;
            }

            // get result as string
            while (rs.next()) {
                SirService serv = new SirService(rs.getString(PGDAOConstants.serviceUrl),
                                                 rs.getString(PGDAOConstants.serviceType));
                result.add(serv);
            }

        }
        catch (OwsExceptionReport se) {
            log.error("Error while query services for the getAllServices from database!", se);
            throw se;
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("Error while query services for the getAllServices from database!", sqle);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
            throw se;
        }

        finally {
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (SQLException e) {
                    log.error("SQL Error.", e);
                }
            }

            // return connection
            if (con != null)
                this.cpool.returnConnection(con);
        }

        return result;
    }

}
