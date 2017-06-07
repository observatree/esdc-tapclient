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

import esac.archive.gacs.sl.tap.TapUtils;
import esavo.adql.parser.ADQLParser;
import esavo.adql.parser.ParseException;
import esavo.adql.query.ADQLQuery;
import esavo.adql.search.IReplaceHandler;
import esavo.adql.translator.ADQLTranslator;
import esavo.adql.translator.PgSphereTranslator;
import esavo.adql.translator.TranslationException;

public class ADQLService {

	public static void main(String[] args) {

		// 1. PARSE ADQL
		ADQLQuery query = null;
		try{
			// Build the ADQL parser:
			ADQLParser parser = new ADQLParser();

			// Check DB consistency:
			//parser.setQueryChecker(new DBChecker(TapServiceConfiguration.getDBTables()));
			// Restrict coordinate systems:
			//parser.setCoordinateSystems(TapServiceConfiguration.getInstance().getCoordinateSystems());
			parser.setCoordinateSystems(TapUtils.getCoordinateSystems());

			// Parse:
			query = parser.parseQuery(System.in);
			System.out.println("Correct ADQL !");

		}catch(ParseException pe){
			//System.err.println("ADQL syntax incorrect between (l."+pe.getBeginLine()+",c."+pe.getBeginColumn()+") and (l."+pe.getEndLine()+",c."+pe.getEndColumn()+"): "+pe.getMessage());
			System.exit(2);
		}

		// MANIPULATE ADQL (OPTIONAL)
		IReplaceHandler replacer = new ReplacePointHandler();
		replacer.searchAndReplace(query);
		if (replacer.getNbMatch() > 0)
			System.out.println("INFO: "+replacer.getNbReplacement()+"/"+replacer.getNbMatch()+" replacements done");

		// 2. TRANSLATE ADQL INTO SQL
		try{
			// Build the translator (for PostgreSQL+PgSphere):
			ADQLTranslator translator = new PgSphereTranslator();

			// Translate:
			String sql = translator.translate(query);
			System.out.println("*** SQL ***\n"+sql);

		}catch(TranslationException te){
			System.err.println("Translation into SQL failed: "+te.getMessage());
		}

	}

}
