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
package esac.archive.gacs.sl.tap.adql;

import esac.archive.gacs.sl.tap.adql.functions.CrossMatchPositional;
import esac.archive.gacs.sl.tap.adql.functions.GaiaHealpixIndex;
import esac.archive.gacs.sl.tap.adql.functions.StandardDeviation;
import esavo.adql.parser.ADQLQueryFactory;
import esavo.adql.query.operand.ADQLOperand;
import esavo.adql.query.operand.function.UserDefinedFunction;
import esavo.tap.TAPService;
import esavo.uws.owner.UwsJobOwner;

public class GaiaADQLQueryFactory extends ADQLQueryFactory{
	
	UwsJobOwner owner = null;
	
	public GaiaADQLQueryFactory(UwsJobOwner owner){
		//Allow user (ESAC) defined functions
		super(true);
		this.owner=owner;
	}
	
	/**
	 * <p>Creates the user defined functions called as the given name and with the given parameters.</p>
	 * @param name			Name of the user defined function to create.
	 * @param params		Parameters of the user defined function to create.
	 * 
	 * @return				The corresponding user defined function.
	 * 
	 * @throws Exception	An {@link UnsupportedOperationException} by default, otherwise any other type of error may be
	 * 						thrown if there is a problem while creating the function.
	 */
	@Override
	public UserDefinedFunction createUserDefinedFunction(String name, ADQLOperand[] params) throws Exception {
		if (allowUnknownFunctions) {
			//if("tap_schema.crossmatch_positional".equalsIgnoreCase(name)){
			String id = name.toLowerCase();
			if(id.matches(".*\\.{0,1}"+TAPService.TAP_ADQL_FUNCTION_XMATCH)){
				return new CrossMatchPositional(name, params, this.owner);
			}else if(id.matches(".*\\.{0,1}"+TAPService.TAP_ADQL_FUNCTION_STDDEV)){
				return new StandardDeviation(name, params);
			}else if(id.matches(".*\\.{0,1}"+GaiaHealpixIndex.TAP_ADQL_FUNCTION_GAIA_HEALPIX_INDEX)){
				return new GaiaHealpixIndex(name, params);
			}
//			else if(id.matches(".*\\.{0,1}"+TAPService.TAP_ADQL_FUNCTION_STDDEV)){
//				return new StandardDeviation(name, params, this.owner);
//			}
		}
		throw new UnsupportedOperationException("No ADQL function called \""+name+"\" !");

	}
	
	
	//TODO TEST HARNESS
	
//	public static boolean test(String id){
//		if(id.matches(".*\\.{0,1}crossmatch_positional")){
//			System.out.println("OK");
//			return true;
//		}else{
//			System.out.println("No OK");
//			return false;
//		}
//	}
//	
//	public static void main(String[] args){
//		test("tap_schema.crossmatch_positional(x,y,z)");
//		test("tap_schemacrossmatch_positional");
//		test("tap_schema.crossmatch_positional");
//		test("crossmatch_positional");
//	}


}
