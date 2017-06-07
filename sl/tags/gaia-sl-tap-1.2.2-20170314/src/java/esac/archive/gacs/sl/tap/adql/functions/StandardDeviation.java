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
package esac.archive.gacs.sl.tap.adql.functions;

import esavo.adql.query.ADQLObject;
import esavo.adql.query.operand.ADQLOperand;
import esavo.adql.query.operand.function.UserDefinedFunction;

public class StandardDeviation extends UserDefinedFunction {

    private String name;
    private ADQLOperand[] params;

    public StandardDeviation(String name, ADQLOperand[] params) {
		
		if(params.length!= 1) 
			throw new UnsupportedOperationException("Incorrect number of parameters for this function");

		this.params = params;
		this.name = name;
    }

    @Override
    public boolean isNumeric() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isString() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ADQLObject getCopy() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getNbParameters() {
        // TODO Auto-generated method stub
        return params.length;
    }

    @Override
    public ADQLOperand[] getParameters() {
        return params;
    }

    @Override
    public ADQLOperand getParameter(int index) throws ArrayIndexOutOfBoundsException {
        return params[index];
    }

    @Override
    public ADQLOperand setParameter(int index, ADQLOperand replacer)
            throws ArrayIndexOutOfBoundsException, NullPointerException, Exception {
        return params[index] = replacer;
    }

}
