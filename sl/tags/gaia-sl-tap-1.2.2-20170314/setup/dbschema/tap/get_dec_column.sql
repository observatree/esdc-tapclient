-- Function: tap_schema.get_dec_column(character varying, character varying)

-- DROP FUNCTION tap_schema.get_dec_column(character varying, character varying);

CREATE OR REPLACE FUNCTION tap_schema.get_dec_column(
    table_schema_a character varying,
    table_name_a character varying)
  RETURNS character varying AS
$BODY$

BEGIN

RETURN (

SELECT '"'||column_name||'"' FROM tap_schema.all_columns WHERE 
    table_name ILIKE $2
    AND schema_name ILIKE $1
    AND (
        (flags & 2) > 0
    )
)
;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION tap_schema.get_dec_column(character varying, character varying)
  OWNER TO postgres;
