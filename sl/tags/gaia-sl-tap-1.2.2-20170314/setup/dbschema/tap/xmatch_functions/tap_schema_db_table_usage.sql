-- Function: tap_schema.db_table_usage(text, text)

-- DROP FUNCTION tap_schema.db_table_usage(text, text);

CREATE OR REPLACE FUNCTION tap_schema.db_table_usage(text, text)
  RETURNS bigint AS
$BODY$
SELECT COALESCE( (SELECT sum(pg_total_relation_size(quote_ident(schemaname) || '.' || quote_ident(tablename)))::bigint 
			FROM pg_tables WHERE schemaname = $1 AND tablename = $2)
		,0)
			
$BODY$
  LANGUAGE sql VOLATILE
  COST 100;
ALTER FUNCTION tap_schema.db_table_usage(text, text)
  OWNER TO postgres;

