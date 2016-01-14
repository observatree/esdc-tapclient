#$ -S /bin/bash

# Generates the SQL query to import the content of a CSV file into the
# gums10_um_stellar_source table.
#
# Firts parameter: absolute path of the CSV input file. If no parameter
#                  is provided, STDIN is assumed.

read -d ''  copy <<"EOF"
COPY igsl_source
(
alpha,
alpha_epoch,
alpha_error,
aux_epc,
aux_gsc23,
aux_hip,
aux_lqrf,
aux_ogle,
aux_ppmxl,
aux_sdss,
aux_tmass,
aux_tycho,
aux_ucac,
classification,
delta,
delta_epoch,
delta_error,
ecliptic_lat,
ecliptic_lon,
galactic_lat,
galactic_lon,
mag_b_j,
mag_b_j_error,
mag_g,
mag_g_error,
mag_grvs,
mag_grvs_error,
mag_r_f,
mag_r_f_error,
mu_alpha,
mu_alpha_error,
mu_delta,
mu_delta_error,
solution_id,
source_id,
source_classification,
source_mag_b_j,
source_mag_g,
source_mag_grvs,
source_mag_r_f,
source_mu,
source_position,
toggle_asc
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
