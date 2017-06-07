-- Function: tap_schema.move_table(text, text, text)

-- DROP FUNCTION tap_schema.move_table(text, text, text);

CREATE OR REPLACE FUNCTION tap_schema.move_table(
    orig_schema text,
    orig_table text,
    to_schema text)
  RETURNS void AS
$BODY$

BEGIN
EXECUTE 'ALTER TABLE '||$1||'.'||$2||' SET SCHEMA '||$3;

-- Update all_keys
UPDATE tap_schema.all_keys SET from_schema=$3 WHERE from_schema=$1 AND from_table=$2;
UPDATE tap_schema.all_keys SET target_schema=$3 WHERE target_schema=$1 AND target_table=$2;

-- Create new entries for the table
INSERT INTO tap_schema.all_tables (table_name, schema_name, table_type, description, utype, size, flags) 
	SELECT b.table_name, $3, b.table_type, b.description, b.utype, b.size, b.flags
	FROM tap_schema.all_tables b WHERE b.schema_name=$1 AND b.table_name=$2;

INSERT INTO tap_schema.all_columns (column_name, description, ucd, utype, datatype, unit, table_name, schema_name, size, principal, std, indexed, flags) 
	SELECT column_name, description, ucd, utype, datatype, unit, table_name, $3, size, principal, std, indexed, flags
	FROM tap_schema.all_columns b WHERE b.schema_name=$1 AND b.table_name=$2;

-- Remove old entries
DELETE FROM tap_schema.all_columns WHERE schema_name=$1 AND table_name=$2;
DELETE FROM tap_schema.all_tables WHERE schema_name=$1 AND table_name=$2;

END;
		
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION tap_schema.move_table(text, text, text)
  OWNER TO postgres;
