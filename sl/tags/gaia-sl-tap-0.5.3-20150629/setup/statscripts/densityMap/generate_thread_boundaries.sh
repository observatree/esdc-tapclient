# GENERATE CSV FILE

tablename=$1
attname=$2
number_of_threads=$3

query="	with b as(
		with m as (
			SELECT unnest(histogram_bounds::text::numeric[]) as bound FROM pg_stats 
			WHERE tablename='$tablename' and attname='$attname'
		)
		select m.bound,(sum(1) over(order by bound))%(select cast(round(count(*)/$number_of_threads) as integer) from m) as module from m 
	)
	select b.bound from b where b.module=0;
      	"

echo $query
echo "$query" | ssh gacsdb01 "~/software/postgresql-9.3.2/bin/psql -t -p 8300 gacs" > bounds.txt

sed -i "$ d" bounds.txt
