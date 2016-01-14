psql -p 8300 gacs -c "CREATE TABLE gog_cataloguesource
(
  solution_id bigint,
  source_id bigint NOT NULL,
  ref_epoch double precision,
  alpha double precision,
  alpha_error double precision,
  delta double precision,
  delta_error double precision,
  mu_alpha_star double precision,
  mu_alpha_star_error double precision,
  mu_delta double precision,
  mu_delta_error double precision,
  radial_velocity double precision,
  radial_velocity_error double precision,
  lin_decomp_normals character varying,
  g_mean_const_flag integer,
  g_mean_const_level double precision,
  g_mean_mag_error double precision,
  g_mean_mean_mag double precision,
  g_mean_n_obs integer
)" > creation_1.txt 

psql -p 8300 gacs -c "CREATE TABLE gog_cataloguesource_new
(
  solution_id bigint,
  source_id bigint NOT NULL,
  ref_epoch double precision,
  alpha double precision,
  alpha_error double precision,
  delta double precision,
  delta_error double precision,
  mu_alpha_star double precision,
  mu_alpha_star_error double precision,
  mu_delta double precision,
  mu_delta_error double precision,
  radial_velocity double precision,
  radial_velocity_error double precision,
  lin_decomp_normals character varying,
  g_mean_const_flag integer,
  g_mean_const_level double precision,
  g_mean_mag_error double precision,
  g_mean_mean_mag double precision,
  g_mean_n_obs integer,
  rv_constancy_probability double precision,
  random_index bigserial
)" > creation_2.txt 
