-- Function: tap_schema.get_largest_table(character varying, character varying, character varying, character varying)

-- DROP FUNCTION tap_schema.get_largest_table(character varying, character varying, character varying, character varying);

CREATE OR REPLACE FUNCTION tap_schema.get_largest_table(table_schema_a character varying, table_name_a character varying, table_schema_b character varying, table_nameb character varying)
  RETURNS integer AS
$BODY$

DECLARE size_a integer;
DECLARE size_b integer;
DECLARE largest_ind integer;

BEGIN

SELECT size FROM  tap_schema.all_tables WHERE schema_name ILIKE $1  AND table_name ILIKE $2 INTO size_a;
SELECT size FROM  tap_schema.all_tables WHERE schema_name ILIKE $3  AND table_name ILIKE $4 INTO size_b;

SELECT (
CASE WHEN 
	size_a >= size_b
THEN 1 ELSE 0
END
) INTO largest_ind ;

RETURN largest_ind;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION tap_schema.get_largest_table(character varying, character varying, character varying, character varying)
  OWNER TO postgres;
