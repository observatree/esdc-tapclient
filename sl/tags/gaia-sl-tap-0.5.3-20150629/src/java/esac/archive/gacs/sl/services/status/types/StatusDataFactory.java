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
package esac.archive.gacs.sl.services.status.types;

import esac.archive.gacs.sl.services.status.StatusData;
import esac.archive.gacs.sl.services.status.TaskType;

public class StatusDataFactory {
	
	public static StatusData createStatusData(TaskType type, String info){
		switch(type){
		case INGESTION:
			return new StatusIngestion(info);
		case PARSE:
			return new StatusParse(info);
		case UPLOAD:
			return new StatusUpload(info);
		case TABLE_EDIT:
			return new StatusTableEdit(info);
		case XMATCH_ANALYZE:
			return new XMatchAnalysis(info);
		case XMATCH_CREATE:
			return new XMatchCreation(info);
		case XMATCH_TAP_UPDATE:
			return new XMatchTapUpdate(info);
		default:
			throw new IllegalArgumentException("Cannot find suitable class for type: " + type.name());
		}
	}

}
