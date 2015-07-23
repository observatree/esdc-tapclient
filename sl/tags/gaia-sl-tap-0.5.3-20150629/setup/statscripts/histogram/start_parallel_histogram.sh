hist_table=igsl_source
hist_column=mag_g

round_prec=2
threads=9

output_png_file=$1
table_map="test.${hist_table}_histogram_$hist_column"
table_reduce="test.${hist_table}_histogram_${hist_column}_result"



### DROP TABLES
echo "DROP TABLES______________________"

echo "drop table if exists $table_map" | ssh gacsdb01 "~/software/postgresql-9.3.2/bin/psql -p 8300 gacs"
echo "drop table if exists $table_reduce" | ssh gacsdb01 "~/software/postgresql-9.3.2/bin/psql -p 8300 gacs"

### CREATE TABLES
echo "CREATE TABLES____________________"

echo "create table $table_map as select round($hist_column) as $hist_column, count(*) as count from $hist_table where 1=0 group by round($hist_column)" | ssh gacsdb01 "~/software/postgresql-9.3.2/bin/psql -p 8300 gacs"

echo "create table $table_reduce as select round($hist_column) as $hist_column, count(*) as count from $hist_table where 1=0 group by round($hist_column)" | ssh gacsdb01 "~/software/postgresql-9.3.2/bin/psql -p 8300 gacs"


### CREATE BOUNDS FILE

./generate_thread_boundaries.sh $hist_table $hist_column $threads


### MAP PART
echo "MAP______________________________"
i=0
while read line
do
   let i=$i+1

   if [ $i = "1" ]; then
        bounds=" where $hist_column < $line "
   else
        bounds=" where $hist_column >= $prev and $hist_column < $line "
   fi

   query="\timing on
      insert into $table_map (
        select round(cast($hist_column as numeric),$round_prec) as $hist_column, count(*) as count
        from $hist_table
	$bounds
        group by round(cast($hist_column as numeric),$round_prec)
      )"

   prev=$line


   echo "Executing segment #$i: $bounds"
   #echo "$query"
   echo "$query" | ssh gacsdb01 "~/software/postgresql-9.3.2/bin/psql -p 8300 gacs" &

done < bounds.txt

# Execution of last segment
bounds="  where $hist_column >= $prev "

   query="\timing on
      insert into $table_map (
        select round(cast($hist_column as numeric),$round_prec) as $hist_column, count(*) as count
        from $hist_table
	$bounds
        group by round(cast($hist_column as numeric),$round_prec)
      )"

echo "Executing last segment: $bounds"
#echo "$query"
echo "$query" | ssh gacsdb01 "~/software/postgresql-9.3.2/bin/psql -p 8300 gacs" &



wait
#
## REDUCE PART
echo "REDUCE___________________________"
query="insert into $table_reduce ( 
select $hist_column, sum(count) from $table_map 
group by $hist_column
)"

#echo "$query"
echo "$query" | ssh gacsdb01 "~/software/postgresql-9.3.2/bin/psql -p 8300 gacs"


# GENERATE CSV FILE
./generate_histogram_plot.sh $hist_column $table_reduce histogram.png
