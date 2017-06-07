-- Function: tap_schema.db_tapupload_table_usage(text)

-- DROP FUNCTION tap_schema.db_tapupload_table_usage(text);

CREATE OR REPLACE FUNCTION tap_schema.db_tapupload_table_usage(text)
  RETURNS bigint AS
$BODY$
SELECT COALESCE( (SELECT sum(pg_total_relation_size(quote_ident(schemaname) || '.' || quote_ident(tablename)))::bigint 
			FROM pg_tables WHERE schemaname = 'tap_upload' AND tablename = $1)
		,0)
			
$BODY$
  LANGUAGE sql VOLATILE
  COST 100;
ALTER FUNCTION tap_schema.db_tapupload_table_usage(text)
  OWNER TO postgres;
