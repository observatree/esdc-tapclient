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
