/*******************************************************************************
 * Copyright (C) 2017 rgutierrez
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

public abstract class AbstractStatusData implements StatusData{
	private String statusData;
	private TaskType statusType;
	
	public AbstractStatusData(TaskType statusType){
		this(statusType, null);
	}
	
	public AbstractStatusData(TaskType statusType, String initialInfo){
		this.statusType = statusType;
		this.statusData = initialInfo;
	}

	@Override
	public void setData(String data) {
		this.statusData = data;
	}

	@Override
	public String getData() {
		return this.statusData;
	}
	
	@Override
	public TaskType getType(){
		return this.statusType;
	}
	
	@Override
	public String toString(){
		return "Status type: " + getType().name() + ", data: " + getData();
	}

}
