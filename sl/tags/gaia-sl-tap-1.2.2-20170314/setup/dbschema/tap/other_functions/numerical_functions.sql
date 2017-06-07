-- MOD(DOUBLE, DOUBLE)

CREATE OR REPLACE FUNCTION mod(dividend double precision, divisor double precision) RETURNS double precision AS $$
DECLARE
  result numeric;
BEGIN
  result  := MOD(dividend::numeric, divisor::numeric);
  RETURN result::double precision;
END;
$$ LANGUAGE plpgsql;



-- TAP SCHEMA entries
insert into tap_schema.all_functions (function_name, schema_name, return_type, description, public) values ('stddev','public','DOUBLE','Standard deviation',TRUE);
insert into tap_schema.all_functions_arguments (argument_name,description,arg_type, function_name, schema_name, default_value,max_value,min_value,public) values ('n',null,'DOUBLE','stddev','public',null,null,null,TRUE);

