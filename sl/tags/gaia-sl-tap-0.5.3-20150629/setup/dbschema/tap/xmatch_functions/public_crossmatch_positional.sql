-- Function: crossmatch_positional(character varying, character varying, character varying, character varying, double precision, character varying, character varying)

-- DROP FUNCTION crossmatch_positional(character varying, character varying, character varying, character varying, double precision, character varying, character varying);

CREATE OR REPLACE FUNCTION crossmatch_positional(table_schema_a character varying, table_name_a character varying, table_schema_b character varying, table_name_b character varying, radius double precision, output_table_schema character varying, output_table_name character varying)
  RETURNS bigint AS
$BODY$

DECLARE output_size bigint;

BEGIN



EXECUTE 'SELECT tap_schema.crossmatch_positional($1, $2, $3, $4, $5, $6, $7)'
INTO output_size
USING table_schema_a, table_name_a, table_schema_b, table_name_b, radius, output_table_schema, output_table_name;
RETURN output_size;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION crossmatch_positional(character varying, character varying, character varying, character varying, double precision, character varying, character varying)
  OWNER TO postgres;
