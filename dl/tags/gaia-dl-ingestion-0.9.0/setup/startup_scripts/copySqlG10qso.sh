#$ -S /bin/bash

# Generates the SQL query to import the content of a CSV file into the
# gums10_um_stellar_source table.
#
# Firts parameter: absolute path of the CSV input file. If no parameter
#                  is provided, STDIN is assumed.

read -d ''  copy <<"EOF"
COPY g10_quasars
(
source_id,
source_extended_id,
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
slope,
w,
red_shift
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
