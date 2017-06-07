-- Function: tap_schema.create_neighbourhood_tables(integer, integer)

-- DROP FUNCTION tap_schema.create_neighbourhood_tables(integer, integer);

CREATE OR REPLACE FUNCTION tap_schema.create_neighbourhood_tables(
    bound_min integer,
    bound_max integer)
  RETURNS void AS
$BODY$

DECLARE 
	curs_slave refcursor;
	count INTEGER;
	slave_row RECORD;
	neigh RECORD;
	bestneigh RECORD;

BEGIN

IF bound_min is not null and bound_max is not null THEN
	OPEN curs_slave FOR EXECUTE 'SELECT * FROM user_satgaia.tycho2 WHERE source_id>'||bound_min||' AND source_id<='||bound_max;
ELSIF bound_min is not null THEN
	OPEN curs_slave FOR EXECUTE 'SELECT * FROM user_satgaia.tycho2 WHERE source_id>'||bound_min;
ELSIF bound_max is not null THEN
	OPEN curs_slave FOR EXECUTE 'SELECT * FROM user_satgaia.tycho2 WHERE source_id<='||bound_max;
ELSE
	OPEN curs_slave FOR EXECUTE 'SELECT * FROM user_satgaia.tycho2';
END IF;


LOOP
	FETCH curs_slave INTO slave_row;

	EXIT WHEN NOT FOUND;
	--RAISE NOTICE 'SLAVE';
	count := 0;

	-- Obtain the neighbours
	FOR neigh IN select a.source_id as master_source_id, 
			slave_row.original_source_id as slave_source_id, 
			q3c_dist(a.alpha, a.delta, slave_row.alpha, slave_row.delta) as dist 
			from igsl_source a where q3c_radial_query(a.alpha, a.delta, slave_row.alpha, slave_row.delta, 1.0/3600) 
			ORDER BY dist 
	LOOP		
		--RAISE NOTICE 'MASTER';
		IF count = 0 THEN
			-- Get the best neighbour
			bestneigh := neigh;
		END IF;
		count := count + 1;

		insert into test_neighbourhood values (neigh.master_source_id, neigh.slave_source_id, neigh.dist, null, null, null,count);

	END LOOP;

	-- If there has ben results, insert into best neighbour table.
	IF count > 0 THEN

		insert into test_bestneighbour values (bestneigh.master_source_id, bestneigh.slave_source_id, bestneigh.dist, null, null, null, count, null);

	END IF;

END LOOP;

close curs_slave;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION tap_schema.create_neighbourhood_tables(integer, integer)
  OWNER TO postgres;
