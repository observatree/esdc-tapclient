column=$1
table_reduce=$2
output_png_file=$3

# GENERATE CSV FILE
echo "alpha,delta,count" > /tmp/result.txt

echo "select (healpix_ipix2ang_nest(512,$column))[1],(healpix_ipix2ang_nest(512,$column))[2],count from $table_reduce where $column is not null order by count" | ssh gacsdb01 "~/software/postgresql-9.3.2/bin/psql -t -p 8300 gacs" | sed 's/|/,/g' >> /tmp/result.txt


# GENERATE PLOT

#java -jar ~/software/stilts.jar uk.ac.starlink.ttools.plot2.task.Plot2Task type=sky projection=ait geom=Equatorial-Galactic in1=/tmp/result.txt ifmt1=csv lon1=alpha lat1=delta layer1=Mark-aux aux1=count shadelog1=true xpix=1000 crowd=2.5 grid=true ofmt=png-transp out=$output_png_file

java -jar ~/software/stilts.jar uk.ac.starlink.ttools.plot2.task.Plot2Task type=sky projection=ait geom=Equatorial-Galactic in1=/tmp/result.txt ifmt1=csv lon1=alpha lat1=delta layer1=Mark-aux aux1=count shadelog1=true xpix=1024 ypix=567 crowd=2.5 grid=true ofmt=png-transp out=$output_png_file

mogrify -trim +repage $output_png_file
