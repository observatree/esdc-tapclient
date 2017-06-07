-- Function: tap_schema.get_bounds(text, text, text, integer)

-- DROP FUNCTION tap_schema.get_bounds(text, text, text, integer);

CREATE OR REPLACE FUNCTION tap_schema.get_bounds(
    schema_name text,
    table_name text,
    column_name text,
    threadn integer)
  RETURNS numeric[] AS
$BODY$
DECLARE
  orig_bounds numeric[];
  orig_size int;
  reduced_bounds numeric[];
  i int;
BEGIN
  SELECT  (SELECT histogram_bounds::text::numeric[] FROM pg_stats 
			WHERE schemaname=schema_name AND tablename=table_name AND attname=column_name) INTO orig_bounds;
  SELECT orig_bounds[2:array_length(orig_bounds, 1)-1] INTO orig_bounds;
  SELECT array_length(orig_bounds, 1) INTO orig_size;
  FOR i IN 1..(threadn-1)
  LOOP
    RAISE NOTICE 'index %', round(i*(orig_size/threadn));
    reduced_bounds[i] := orig_bounds[round(i::double precision*(orig_size::double precision/threadn::double precision))];
  END LOOP;
  RETURN reduced_bounds;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION tap_schema.get_bounds(text, text, text, integer)
  OWNER TO postgres;
