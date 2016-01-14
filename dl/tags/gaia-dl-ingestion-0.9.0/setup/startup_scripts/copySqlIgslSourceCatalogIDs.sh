#$ -S /bin/bash

# Generates the SQL query to import the content of a CSV file into the
# gums10_um_stellar_source table.
#
# Firts parameter: absolute path of the CSV input file. If no parameter
#                  is provided, STDIN is assumed.

read -d ''  copy <<"EOF"
COPY igsl_source_catalog_ids
(
id_epc_bytes,
id_gsc23,
id_hip,
id_lqrf,
id_ogle,
id_ppmxl,
id_sdss,
id_tmass,
id_tycho,
id_ucac,
solution_id,
source_id
)
FROM STDIN 
DELIMITER '|'
NULL '\N';
EOF

# Si se ha especificado fichero de entrada se sustituye STDIN por dicho fichero.
if [ -z "$1" ]
then
    echo $copy
else
   echo $copy | sed -e "s|STDIN|'$1'|g"
fi
