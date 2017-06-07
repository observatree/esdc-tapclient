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
package esac.archive.gacs.sl.services.nameresolution.actions.handlers.tgas;

public class TgasTarget {
	
	private String principalName;
	private String tychoName="";
	private String hipName="";
	private Double ra;
	private Double de;
	
	public String getPrincipalName() {
		return principalName;
	}

	public void setPrincipalName(String principalName) {
		this.principalName = principalName;
	}

	public String getTychoName() {
		return tychoName;
	}

	public void setTychoName(String tychoName) {
		this.tychoName = tychoName;
	}

	public String getHipName() {
		return hipName;
	}

	public void setHipName(String hipName) {
		this.hipName = hipName;
	}

	public Double getRa() {
		return ra;
	}

	public void setRa(Double ra) {
		this.ra = ra;
	}

	public Double getDe() {
		return de;
	}

	public void setDe(Double de) {
		this.de = de;
	}
}
