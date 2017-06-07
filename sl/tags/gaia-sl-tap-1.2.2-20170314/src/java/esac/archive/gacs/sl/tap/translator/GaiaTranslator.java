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
package esac.archive.gacs.sl.tap.translator;

import esac.archive.gacs.sl.tap.adql.functions.GaiaHealpixIndex;
import esavo.adql.query.operand.function.ADQLFunction;
import esavo.adql.query.operand.function.MathFunction;
import esavo.adql.query.operand.function.SQLFunction;
import esavo.adql.query.operand.function.UserDefinedFunction;
import esavo.adql.query.operand.function.geometry.GeometryFunction;
import esavo.adql.translator.Q3cPgSphereTranslator;
import esavo.adql.translator.TranslationException;

public class GaiaTranslator extends Q3cPgSphereTranslator {
		
		public GaiaTranslator() {
			super();
		}
		
		
		@Override
		public String translate(UserDefinedFunction fct) throws TranslationException{
			if (fct instanceof GaiaHealpixIndex){
				return translate((GaiaHealpixIndex)fct);
			}else{
				return super.translate(fct);
			}
		}
		
		private String translate(GaiaHealpixIndex fct) throws TranslationException {
			
			
			int norder;
			try {
				norder = new Integer(fct.getParameter(0).toADQL()).intValue();
			} catch(Exception e) {
				throw new TranslationException("Healpix norder should be an integer value");
			}

			if(!fct.getParameter(1).isNumeric()){
				throw new TranslationException("Not valid Gaia source_id.");
			}
			
			if(norder>12){
				throw new TranslationException("Gaia source_id maximum Healpix norder is 12");
			}
			int shiftbits = 35+2*(12-norder);
			
			StringBuffer str = new StringBuffer();
			str.append("(").append(translate(fct.getParameter(1))).append(">>" + shiftbits+")");
			return str.toString();
		}

}

