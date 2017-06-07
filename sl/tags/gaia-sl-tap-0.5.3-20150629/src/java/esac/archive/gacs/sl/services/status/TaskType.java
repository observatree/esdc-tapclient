/*******************************************************************************
 * Copyright (C) 2017 European Space Agency
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package esac.archive.gacs.sl.services.status;

/**
 * Task types.<br/>
 * These task types specifies the current task step.
 * @author juan.carlos.segovia@sciops.esa.int
 *
 */
public enum TaskType {

	/**
	 * Starts user table upload procedure
	 */
	UPLOAD,
	/**
	 * Parse user table (upload related)
	 */
	PARSE,
	/**
	 * User table ingestion (upload related)
	 */
	INGESTION,
	/**
	 * User table edit
	 */
	TABLE_EDIT,

	
	/**
	 * Cross match creation. Includes:
	 * <ul>
	 * <li>Selection of largest table</li>
	 * <li>Creation of cross match table</li>
	 * <li>Computation of matching keys</li>
	 * <li>Computation of estimator indexes</li>
	 * </ul>
	 */
	XMATCH_CREATE,
	/**
	 * Cross match table analysis.
	 */
	XMATCH_ANALYZE,
	/**
	 * Tap update related to cross match table creation.
	 */
	XMATCH_TAP_UPDATE
	

}
