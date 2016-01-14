#$ -S /bin/bash

# Generates the SQL query to import the content of a CSV file into the
# g10_gal table.
#
# Firts parameter: absolute path of the CSV input file. If no parameter
#                  is provided, STDIN is assumed.

read -d ''  copy <<"EOF"
COPY g10_gal
(
 source_id,
 source_extended_id,
 redshift,
 alpha,
 delta,
 distance,
 mu_alpha,
 mu_delta,
 radial_velocity,
 mag_g,
 mag_g_bp,
 mag_g_rp,
 mag_g_rvs,
 av,
 ag,
 rv,
 hubble_type,
 shape,
 mean_abs_v,
 color_vminusi,
 attached_supernova_id
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
