#table=igsl_source
#column="source_id>>41"
table=twomass_psc_healpix
column="hpix>>6"
colalias=healpix

stats_tablename=$table
stats_attname=hpix
threads=9

output_png_file=$1
table_map="test.${table}_density_$colalias"
table_reduce="test.${table}_density_${colalias}_result"



### DROP TABLES
echo "DROP TABLES______________________"

echo "drop table if exists $table_map" | ssh gacsdb01 "~/software/postgresql-9.3.2/bin/psql -p 8300 gacs"
echo "drop table if exists $table_reduce" | ssh gacsdb01 "~/software/postgresql-9.3.2/bin/psql -p 8300 gacs"

### CREATE TABLES
echo "CREATE TABLES____________________"

echo "create table $table_map as select $column as ipix, count(*) as count from $table where 1=0 group by $column" | ssh gacsdb01 "~/software/postgresql-9.3.2/bin/psql -p 8300 gacs"

echo "create table $table_reduce as select $column as ipix, count(*) as count from $table where 1=0 group by $column" | ssh gacsdb01 "~/software/postgresql-9.3.2/bin/psql -p 8300 gacs"


### CREATE BOUNDS FILE

./generate_thread_boundaries.sh $stats_tablename $stats_attname $threads


### MAP PART
echo "MAP______________________________"
i=0
while read line
do
   let i=$i+1

   if [ $i = "1" ]; then
        bounds=" where $stats_attname < $line "
   else
        bounds=" where $stats_attname >= $prev and $stats_attname < $line "
   fi

   query="\timing on
      insert into $table_map (
        select $column as ipix, count(*) as count
        from $table
	$bounds
        group by $column
      )"

   prev=$line


   echo "Executing segment #$i: $bounds"
   echo "$query"
   echo "$query" | ssh gacsdb01 "~/software/postgresql-9.3.2/bin/psql -p 8300 gacs" &

done < bounds.txt

# Execution of last segment
bounds="  where $stats_attname >= $prev "

   query="\timing on
      insert into $table_map (
        select $column as ipix, count(*) as count
        from $table
	$bounds
        group by $column      )"

echo "Executing last segment: $bounds"
echo "$query"
echo "$query" | ssh gacsdb01 "~/software/postgresql-9.3.2/bin/psql -p 8300 gacs" &



wait
#
## REDUCE PART
echo "REDUCE___________________________"
query="insert into $table_reduce ( 
select ipix, sum(count) from $table_map 
group by ipix
)"

#echo "$query"
echo "$query" | ssh gacsdb01 "~/software/postgresql-9.3.2/bin/psql -p 8300 gacs"


# GENERATE CSV FILE
echo "./generate_density_plot.sh ipix $table_reduce public.${table}_density.png"
./generate_density_plot.sh ipix $table_reduce public.${table}_density.png
