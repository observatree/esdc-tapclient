#$ -S /bin/bash

if [ $# != 3 ]
then
  echo "Usage: $0 extractStrategy /input/path /output/path"
  echo "Example: $0 esac.archive.gaia.dl.tools.extractor.ExtractUMStellar /satdev_test/data/GUMS10/MW/N0 /satdev_test/gridtests/extract/output/N0"
  exit 1
fi

# GAIA data dir in worker nodes
export GAIA_DATA_DIR=/StagingArea

# JAVA setup
export JAVA_HOME=$HOME/software/java1.6
export PATH=$JAVA_HOME/bin:$PATH

# CLASSPATH setup
# export CLASSPATH=$GAIA_DATA_DIR/gridtests/extract/software
# export LIB_DIR=$GAIA_DATA_DIR/gridtests/extract/software/lib
#for i in `ls $LIB_DIR/*`
#do
#  export CLASSPATH=$CLASSPATH:$i
#done

# Run the job
~/software/java/jre1.7.0_40/bin/java -Xmx1G -Xms1G -jar $GAIA_DATA_DIR/gridtests/extract/software_new/gaia-dl-tools-0.1.jar $1 $2 $3

