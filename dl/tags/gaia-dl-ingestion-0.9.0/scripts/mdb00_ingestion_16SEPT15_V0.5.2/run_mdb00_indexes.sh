psql -p 8300 gacs -c "CREATE INDEX ON mdb00 USING btree (alpha_error);" &
psql -p 8300 gacs -c "CREATE INDEX ON mdb00 USING btree (alpha) ;" &
psql -p 8300 gacs -c "CREATE INDEX ON mdb00 USING btree (astrometric_decomposed_n);" &
psql -p 8300 gacs -c "CREATE INDEX ON mdb00 USING btree (delta_error);" &
psql -p 8300 gacs -c "CREATE INDEX ON mdb00 USING btree (delta);" &
psql -p 8300 gacs -c "CREATE INDEX ON mdb00 USING btree (matched_observations);" &
psql -p 8300 gacs -c "CREATE INDEX ON mdb00 USING btree (mu_alpha_star_error);" &
psql -p 8300 gacs -c "CREATE INDEX ON mdb00 USING btree (mu_alpha_star);" &
psql -p 8300 gacs -c "CREATE INDEX ON mdb00 USING btree (mu_delta_error);" &
psql -p 8300 gacs -c "CREATE INDEX ON mdb00 USING btree (mu_delta);" &
psql -p 8300 gacs -c "CREATE INDEX ON mdb00 USING btree (observed);" &
psql -p 8300 gacs -c "CREATE INDEX ON mdb00 USING btree (public.q3c_ang2ipix(alpha, delta));" &
psql -p 8300 gacs -c "CREATE INDEX ON mdb00 USING btree (source_id);" &