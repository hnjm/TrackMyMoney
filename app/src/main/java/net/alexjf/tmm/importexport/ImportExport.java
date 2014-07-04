/*******************************************************************************
 * Copyright (c) 2013 - Alexandre Jorge Fonseca (alexjf.net)
 * License: GPL v3 (http://www.gnu.org/licenses/gpl-3.0.txt)
 ******************************************************************************/
package net.alexjf.tmm.importexport;

import java.util.Date;

import net.alexjf.tmm.exceptions.ExportException;
import net.alexjf.tmm.exceptions.ImportException;

public abstract class ImportExport {
    public abstract void importData(String location, boolean replace)
        throws ImportException;

    public void exportData(String location) throws ExportException {
        exportData(location, null, null);
    }

    public abstract void exportData(String location, Date startDate, Date endDate)
        throws ExportException;
}