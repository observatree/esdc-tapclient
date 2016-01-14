psql -p 8300 gacs -c "INSERT INTO gog_cataloguesource_new(solution_id, source_id, ref_epoch, alpha, alpha_error, 
delta, delta_error, mu_alpha_star, mu_alpha_star_error, mu_delta, mu_delta_error,
radial_velocity, radial_velocity_error, lin_decomp_normals, g_mean_const_flag,
g_mean_const_level, g_mean_mag_error, g_mean_mean_mag, g_mean_n_obs, rv_constancy_probability)

SELECT 
solution_id, source_id, ref_epoch, 
degrees(alpha), 3.6*degrees(alpha_error), 
degrees(delta), 3.6*degrees(delta_error), 
mu_alpha_star, mu_alpha_star_error, mu_delta, mu_delta_error,
radial_velocity, radial_velocity_error, lin_decomp_normals, g_mean_const_flag,
g_mean_const_level, g_mean_mag_error, g_mean_mean_mag, g_mean_n_obs, NULL
FROM gog_cataloguesource" > update.txt 
psql -p 8300 gacs -c "CREATE INDEX gog_cataloguesource_new_q3c ON gog_cataloguesource_new USING btree(q3c_ang2ipix(alpha, delta))" > 0.txt &
psql -p 8300 gacs -c "CREATE INDEX ON gog_cataloguesource_new(mu_alpha_star)" > 1.txt &
psql -p 8300 gacs -c "CREATE INDEX ON gog_cataloguesource_new(mu_alpha_star_error)" > 2.txt &
psql -p 8300 gacs -c "CREATE INDEX ON gog_cataloguesource_new(mu_delta)" > 3.txt &
psql -p 8300 gacs -c "CREATE INDEX ON gog_cataloguesource_new(mu_delta_error)" > 4.txt &
psql -p 8300 gacs -c "CREATE INDEX ON gog_cataloguesource_new(radial_velocity)" > 5.txt &
psql -p 8300 gacs -c "CREATE INDEX ON gog_cataloguesource_new(radial_velocity_error)" > 6.txt &
psql -p 8300 gacs -c "CREATE INDEX ON gog_cataloguesource_new(g_mean_const_flag)" > 8.txt &
psql -p 8300 gacs -c "CREATE INDEX ON gog_cataloguesource_new(g_mean_const_level)" > 9.txt &
psql -p 8300 gacs -c "CREATE INDEX ON gog_cataloguesource_new(g_mean_mag_error)" > 10.txt &
psql -p 8300 gacs -c "CREATE INDEX ON gog_cataloguesource_new(g_mean_mean_mag)" > 11.txt &
psql -p 8300 gacs -c "CREATE INDEX ON gog_cataloguesource_new(g_mean_n_obs)" > 12.txt &
psql -p 8300 gacs -c "CREATE INDEX ON gog_cataloguesource_new(solution_id)" > 13.txt &
psql -p 8300 gacs -c "CREATE INDEX ON gog_cataloguesource_new(source_id)" > 14.txt &
psql -p 8300 gacs -c "CREATE INDEX ON gog_cataloguesource_new(ref_epoch)" > 15.txt &


psql -p 8300 gacs -c "DROP gog_cataloguesource"

psql -p 8300 gacs -c "ALTER TABLE gog_cataloguesource_new
  RENAME TO gog_cataloguesource"

psql -p 8300 gacs -c "ANALYZE gog_cataloguesource"

psql -p 8300 gacs -c "copy(
	select attname, null_frac, n_distinct, most_common_vals, most_common_freqs, histogram_bounds, correlation  
	from pg_stats where tablename ilike 'gog_cataloguesource'
) to '/home/postgres/stats_gog_cataloguesource.csv' with csv header"
