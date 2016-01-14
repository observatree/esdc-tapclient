psql -p 8300 gacs -c "CREATE INDEX ON hipparcos2 USING btree (ra);" &
psql -p 8300 gacs -c "CREATE INDEX ON hipparcos2 USING btree (de);" &
psql -p 8300 gacs -c "CREATE INDEX ON hipparcos2 USING btree (public.q3c_ang2ipix(ra, de));" &