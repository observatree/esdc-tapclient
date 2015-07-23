-- Function: tap_schema.crossmatch_positional(character varying, character varying, character varying, character varying, double precision, character varying, character varying)

-- DROP FUNCTION tap_schema.crossmatch_positional(character varying, character varying, character varying, character varying, double precision, character varying, character varying);

CREATE OR REPLACE FUNCTION tap_schema.crossmatch_positional(table_schema_a character varying, table_name_a character varying, table_schema_b character varying, table_name_b character varying, radius double precision, output_table_schema character varying, output_table_name character varying)
  RETURNS bigint AS
$BODY$


DECLARE large_table_schema character varying;
DECLARE large_table_name character varying;
DECLARE large_table_oid character varying;
DECLARE small_table_schema character varying;
DECLARE small_table_name character varying;
DECLARE small_table_oid character varying;

DECLARE output_size bigint;

BEGIN

output_table_schema := lower(output_table_schema);
output_table_name   := lower(output_table_name);
-- Identifying the largest table

IF tap_schema.get_largest_table(table_schema_a, table_name_a, table_schema_b, table_name_b) = 0 THEN
    SELECT $1 INTO large_table_schema;
    SELECT $2 INTO large_table_name;
    SELECT $3 INTO small_table_schema;
    SELECT $4 INTO small_table_name;
ELSE
    SELECT $3 INTO large_table_schema;
    SELECT $4 INTO large_table_name;
    SELECT $1 INTO small_table_schema;
    SELECT $2 INTO small_table_name;
END IF;

SELECT tap_schema.get_pk_column(large_table_schema, large_table_name) INTO large_table_oid;
SELECT tap_schema.get_pk_column(small_table_schema, small_table_name) INTO small_table_oid;  


-- Creating matching table

EXECUTE 'CREATE TABLE '||output_table_schema||'.'||output_table_name||' AS (
		SELECT b.'||large_table_oid||' as '||large_table_name || '_' || large_table_oid ||', 
			a.'||small_table_oid||' as '||small_table_name || '_' || small_table_oid ||', 
			q3c_dist(b.'||tap_schema.get_ra_column(large_table_schema,large_table_name)||', b.'
					||tap_schema.get_dec_column(large_table_schema,large_table_name)||', a.'
					||tap_schema.get_ra_column(small_table_schema,small_table_name)||', a.'
					||tap_schema.get_dec_column(small_table_schema,small_table_name)||') as dist 
		FROM '||small_table_schema||'.'||small_table_name||' AS a, 
			'||large_table_schema||'.'||large_table_name||' AS b
		WHERE q3c_join(b.'||tap_schema.get_ra_column(large_table_schema,large_table_name)||', b.'
					||tap_schema.get_dec_column(large_table_schema,large_table_name)||', a.'
					||tap_schema.get_ra_column(small_table_schema,small_table_name)||', a.'
					||tap_schema.get_dec_column(small_table_schema,small_table_name)||', ((CAST('||radius||' AS double precision))/3600))
	);

-- Create Primary Key

ALTER TABLE '||output_table_schema||'.'||output_table_name||'
	ADD CONSTRAINT '||output_table_name||'_pk
	PRIMARY KEY ('||small_table_name || '_' || small_table_oid ||', '||large_table_name || '_' || large_table_oid ||');

-- Create Foreign Keys
ALTER TABLE '||output_table_schema||'.'||output_table_name||'
    ADD CONSTRAINT '||output_table_name||'_'||small_table_name||'_fk
    FOREIGN KEY ('||small_table_name || '_' || small_table_oid || ') 
    REFERENCES '||small_table_schema||'.'||small_table_name||'('||small_table_oid || ');
ALTER TABLE '||output_table_schema||'.'||output_table_name||'
    ADD CONSTRAINT '||output_table_name||'_'||large_table_name||'_fk
    FOREIGN KEY ('||large_table_name|| '_' || large_table_oid || ') 
    REFERENCES '||large_table_schema||'.'||large_table_name||'('||large_table_oid || ');

-- Create estimator index
CREATE INDEX ON '||output_table_schema||'.'||output_table_name||' (dist);

-- Insert table in TAP_SCHEMA
INSERT INTO tap_schema.all_tables VALUES ('||quote_literal(output_table_name)||', '||quote_literal(output_table_schema)||', ''table'', 
	''xmatch between '||large_table_name||' and '||small_table_name||' with '||radius||' radius'', 
	'''', ( SELECT count(*) FROM '||output_table_schema||'.'||output_table_name||'), 2 );

SELECT tap_schema.extract_table_metadata_to_tap_schema('||quote_literal(output_table_schema)||','||quote_literal(output_table_name)||')';

--RETURN QUERY EXECUTE 'SELECT '||lower(quote_literal(output_table_schema))||','||lower(quote_literal(output_table_name))||', 
--		    tap_schema.db_table_usage('||lower(quote_literal(output_table_schema))||','||lower(quote_literal(output_table_name))||')';


--RETURN QUERY EXECUTE 'SELECT tap_schema.db_table_usage($1,$2)'
--		    USING output_table_schema, output_table_name;

EXECUTE 'SELECT tap_schema.db_table_usage($1,$2)'
INTO output_size
USING output_table_schema, output_table_name;

return output_size;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION tap_schema.crossmatch_positional(character varying, character varying, character varying, character varying, double precision, character varying, character varying)
  OWNER TO postgres;
