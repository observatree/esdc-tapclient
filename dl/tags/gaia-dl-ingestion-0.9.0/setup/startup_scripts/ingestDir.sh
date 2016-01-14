#$ -S /bin/bash

# Searches for gbin.out and gbint.out.gz files in the specified directory
# and subdirectories and ingest them into the gums10_um_stellar_source table 
#
# Firts parameter: absolute path of the directory to be ingested.

if [ $# != 2 ]
then
  echo "Usage: $0 input_SQL_query /input/path"
  echo "Example: $0 /StagingArea/gridtests/extract/copySqlIgslSource.sh /StagingArea/gridtests/extract/output/IGSL/IgslSource/"
  exit 1
fi

copyGenerator=$1

out=`find $2 -name "*.gbin.out"`
gz=`find $2 -name "*.gbin.out.gz"`

# Ficheros no comprimidos
for f in $out;
do
    echo "Ingesting $f ..."
    copy=`$copyGenerator $f`
    echo $copy | psql -q -p 8300 gacs
    STATUS=$?
    if [[ $STATUS != 0 ]] ; then
        echo "Error in $f"
        exit $STATUS
    fi
done

# Ficheros comprimidos
copy=`$copyGenerator`
for f in $gz;
do
    echo "Ingesting $f ..."
    gunzip -c $f | psql gacs -p 8300 -c "$copy"
    STATUS=$?
    if [[ $STATUS != 0 ]] ; then
        echo "Error in $f"
        exit $STATUS
    fi
done

