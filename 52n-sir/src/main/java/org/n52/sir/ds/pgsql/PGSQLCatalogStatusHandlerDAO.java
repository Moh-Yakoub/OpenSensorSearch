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
import java.sql.SQLException;
import java.sql.Statement;

import org.n52.sir.ds.ICatalogStatusHandlerDAO;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.ows.OwsExceptionReport.ExceptionCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte
 * 
 */
public class PGSQLCatalogStatusHandlerDAO implements ICatalogStatusHandlerDAO {

    private static Logger log = LoggerFactory.getLogger(PGSQLCatalogStatusHandlerDAO.class);

    private PGConnectionPool cpool;

    public PGSQLCatalogStatusHandlerDAO(PGConnectionPool cpool) {
        this.cpool = cpool;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.ds.ICatalogStatusHandlerDAO#setNewStatus(java.lang.String, java.lang.String)
     */
    @Override
    public void setNewStatus(String connectionID, String status) throws OwsExceptionReport {
        Connection con = null;
        Statement stmt = null;

        String setNewStatusQuery = setNewStatusQuery(connectionID, status);

        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();
            if (log.isDebugEnabled())
                PGSQLCatalogStatusHandlerDAO.log.debug(">>>Database Query: " + setNewStatusQuery);
            stmt.execute(setNewStatusQuery);
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.NoApplicableCode, null, "Error while set a new Status for ID "
                    + connectionID + " in database: " + sqle.getMessage());
            PGSQLCatalogStatusHandlerDAO.log.error("Error while set a new Status for ID " + connectionID
                    + " in database: " + sqle.getMessage());
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
            if (con != null) {
                this.cpool.returnConnection(con);
            }
        }
    }

    private String setNewStatusQuery(String connectionID, String status) {
        StringBuilder query = new StringBuilder();

        query.append("UPDATE ");
        query.append(PGDAOConstants.catalog);
        query.append(" SET ");
        query.append(PGDAOConstants.catalogStatus);
        query.append(" = '");
        query.append(status);
        query.append("' WHERE ");
        query.append(PGDAOConstants.catalogIdSir);
        query.append(" = ");
        query.append(connectionID);
        query.append("");

        return query.toString();
    }

}
