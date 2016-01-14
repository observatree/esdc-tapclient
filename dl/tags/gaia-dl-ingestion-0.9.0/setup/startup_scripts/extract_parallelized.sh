#$ -S /bin/bash

#if [ $# != 3 ]
#then
#  echo "Usage: $0 extractStrategy /input/path /output/path"
#  echo "Example: $0 esac.archive.gaia.dl.tools.extractor.ExtractUMStellar /satdev_test/data/GUMS10/MW/N0 /satdev_test/gridtests/extract/output/N0"
#  exit 1
#fi

# GAIA data dir in worker nodes
#export GAIA_DATA_DIR=/StagingArea

# JAVA setup
#export JAVA_HOME=$HOME/software/java1.6
#export PATH=$JAVA_HOME/bin:$PATH

# CLASSPATH setup
# export CLASSPATH=$GAIA_DATA_DIR/gridtests/extract/software
# export LIB_DIR=$GAIA_DATA_DIR/gridtests/extract/software/lib
#for i in `ls $LIB_DIR/*`
#do
#  export CLASSPATH=$CLASSPATH:$i
#done

# Run the job
#~/software/java/jre1.7.0_40/bin/java -Xmx1G -Xms1G -jar /StagingArea/gridtests/extract/software/gbinExtractor.jar  esac.archive.gaia.dl.tools.extractor.ExtractUMQuasar /StagingArea/data/GUMS10/QSO/N0 /StagingArea/gridtests/extract/output/QSO/N0 & > N0.txt

#~/software/java/jre1.7.0_40/bin/java -Xmx1G -Xms1G -jar /StagingArea/gridtests/extract/software/gbinExtractor.jar  esac.archive.gaia.dl.tools.extractor.ExtractUMQuasar /StagingArea/data/GUMS10/QSO/N1 /StagingArea/gridtests/extract/output/QSO/N1 & > N1.txt

#~/software/java/jre1.7.0_40/bin/java -Xmx1G -Xms1G -jar /StagingArea/gridtests/extract/software/gbinExtractor.jar  esac.archive.gaia.dl.tools.extractor.ExtractUMQuasar /StagingArea/data/GUMS10/QSO/N2 /StagingArea/gridtests/extract/output/QSO/N2 & > N2.txt

#~/software/java/jre1.7.0_40/bin/java -Xmx1G -Xms1G -jar /StagingArea/gridtests/extract/software/gbinExtractor.jar  esac.archive.gaia.dl.tools.extractor.ExtractUMQuasar /StagingArea/data/GUMS10/QSO/N3 /StagingArea/gridtests/extract/output/QSO/N3 & > N3.txt


#~/software/java/jre1.7.0_40/bin/java -Xmx1G -Xms1G -jar /StagingArea/gridtests/extract/software/gbinExtractor.jar  esac.archive.gaia.dl.tools.extractor.ExtractUMQuasar /StagingArea/data/GUMS10/QSO/S0 /StagingArea/gridtests/extract/output/QSO/S0 & > S0.txt

#~/software/java/jre1.7.0_40/bin/java -Xmx1G -Xms1G -jar /StagingArea/gridtests/extract/software/gbinExtractor.jar  esac.archive.gaia.dl.tools.extractor.ExtractUMQuasar /StagingArea/data/GUMS10/QSO/S1 /StagingArea/gridtests/extract/output/QSO/S1 & > S1.txt

#~/software/java/jre1.7.0_40/bin/java -Xmx1G -Xms1G -jar /StagingArea/gridtests/extract/software/gbinExtractor.jar  esac.archive.gaia.dl.tools.extractor.ExtractUMQuasar /StagingArea/data/GUMS10/QSO/S2 /StagingArea/gridtests/extract/output/QSO/S2 & > S2.txt

#~/software/java/jre1.7.0_40/bin/java -Xmx1G -Xms1G -jar /StagingArea/gridtests/extract/software/gbinExtractor.jar  esac.archive.gaia.dl.tools.extractor.ExtractUMQuasar /StagingArea/data/GUMS10/QSO/S3 /StagingArea/gridtests/extract/output/QSO/S3 & > S3.txt


#./extract.sh esac.archive.gaia.dl.tools.extractor.ExtractIgslSource /idtdata/OPSRepo/DPCT/IGSL/OPS-IGSL-15_01/mdb/cu3/auxdata/igsl/IgslSource/000/001 /StagingArea/gridtests/extract/output/IGSL/IgslSource/001 & > N1.txt
#./extract.sh esac.archive.gaia.dl.tools.extractor.ExtractIgslSource /idtdata/OPSRepo/DPCT/IGSL/OPS-IGSL-15_01/mdb/cu3/auxdata/igsl/IgslSource/000/002 /StagingArea/gridtests/extract/output/IGSL/IgslSource/002 & > N2.txt
#./extract.sh esac.archive.gaia.dl.tools.extractor.ExtractIgslSource /idtdata/OPSRepo/DPCT/IGSL/OPS-IGSL-15_01/mdb/cu3/auxdata/igsl/IgslSource/000/003 /StagingArea/gridtests/extract/output/IGSL/IgslSource/003 & > N3.txt
#./extract.sh esac.archive.gaia.dl.tools.extractor.ExtractIgslSource /idtdata/OPSRepo/DPCT/IGSL/OPS-IGSL-15_01/mdb/cu3/auxdata/igsl/IgslSource/000/004 /StagingArea/gridtests/extract/output/IGSL/IgslSource/004 & > N4.txt
#./extract.sh esac.archive.gaia.dl.tools.extractor.ExtractIgslSource /idtdata/OPSRepo/DPCT/IGSL/OPS-IGSL-15_01/mdb/cu3/auxdata/igsl/IgslSource/000/005 /StagingArea/gridtests/extract/output/IGSL/IgslSource/005 & > N5.txt
#./extract.sh esac.archive.gaia.dl.tools.extractor.ExtractIgslSource /idtdata/OPSRepo/DPCT/IGSL/OPS-IGSL-15_01/mdb/cu3/auxdata/igsl/IgslSource/000/006 /StagingArea/gridtests/extract/output/IGSL/IgslSource/006 & > N6.txt
#./extract.sh esac.archive.gaia.dl.tools.extractor.ExtractIgslSource /idtdata/OPSRepo/DPCT/IGSL/OPS-IGSL-15_01/mdb/cu3/auxdata/igsl/IgslSource/000/007 /StagingArea/gridtests/extract/output/IGSL/IgslSource/007 & > N7.txt
#./extract.sh esac.archive.gaia.dl.tools.extractor.ExtractIgslSource /idtdata/OPSRepo/DPCT/IGSL/OPS-IGSL-15_01/mdb/cu3/auxdata/igsl/IgslSource/000/008 /StagingArea/gridtests/extract/output/IGSL/IgslSource/008 & > N8.txt


./extract.sh esac.archive.gaia.dl.tools.extractor.ExtractSourceCatalogIDs /idtdata/OPSRepo/DPCT/IGSL/OPS-IGSL-15_01/mdb/cu3/auxdata/igsl/SourceCatalogIDs/000/000 /StagingArea/gridtests/extract/output/IGSL/SourceCatalogIDs/000 & > N0.txt
./extract.sh esac.archive.gaia.dl.tools.extractor.ExtractSourceCatalogIDs /idtdata/OPSRepo/DPCT/IGSL/OPS-IGSL-15_01/mdb/cu3/auxdata/igsl/SourceCatalogIDs/000/001 /StagingArea/gridtests/extract/output/IGSL/SourceCatalogIDs/001 & > N1.txt
./extract.sh esac.archive.gaia.dl.tools.extractor.ExtractSourceCatalogIDs /idtdata/OPSRepo/DPCT/IGSL/OPS-IGSL-15_01/mdb/cu3/auxdata/igsl/SourceCatalogIDs/000/002 /StagingArea/gridtests/extract/output/IGSL/SourceCatalogIDs/002 & > N2.txt


