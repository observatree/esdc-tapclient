hist_column=$1
table_reduce=$2
output_png_file=$3

# GENERATE CSV FILE
echo "$hist_column,count" > /tmp/result.txt

echo "select * from $table_reduce where $hist_column is not null order by $hist_column" | ssh gacsdb01 "~/software/postgresql-9.3.2/bin/psql -t -p 8300 gacs" | sed 's/|/,/g' >> /tmp/result.txt


# GENERATE PLOT

java -jar ~/software/stilts.jar plothist in=/tmp/result.txt ifmt=csv xdata=$hist_column weight=count binwidth=0.2 xpix=1024 ypix=768 out=$output_png_file
