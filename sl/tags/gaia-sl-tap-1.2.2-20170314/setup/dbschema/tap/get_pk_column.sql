-- Function: tap_schema.get_pk_column(text, text)

-- DROP FUNCTION tap_schema.get_pk_column(text, text);

CREATE OR REPLACE FUNCTION tap_schema.get_pk_column(
    schemaname text,
    tablename text)
  RETURNS text AS
$BODY$
SELECT column_name FROM tap_schema.all_columns WHERE schema_name ILIKE $1 AND table_name ILIKE $2 AND flags = 16

$BODY$
  LANGUAGE sql VOLATILE
  COST 100;
ALTER FUNCTION tap_schema.get_pk_column(text, text)
  OWNER TO postgres;
