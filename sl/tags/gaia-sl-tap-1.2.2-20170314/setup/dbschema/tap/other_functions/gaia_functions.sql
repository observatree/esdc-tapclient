-- TAP SCHEMA entries
insert into tap_schema.all_functions (function_name, schema_name, return_type, description, public) values ('gaia_healpix_index','public','DOUBLE','Returns the healpix index of the given norder extracted from the given gaia Source ID',TRUE);

insert into tap_schema.all_functions_arguments (argument_name,description,arg_type, function_name, schema_name, default_value,max_value,min_value,public) values ('norder',null,'INTEGER','gaia_healpix_index','public',null,'12','0',TRUE);
insert into tap_schema.all_functions_arguments (argument_name,description,arg_type, function_name, schema_name, default_value,max_value,min_value,public) values ('gaia_source_id',null,'BIGINT','gaia_healpix_index','public',null,null,null,TRUE);
