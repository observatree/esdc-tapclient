#$ -S /bin/bash

# Generates the SQL query to import the content of a CSV file into the
# gums10_um_stellar_source table.
#
# Firts parameter: absolute path of the CSV input file. If no parameter
#                  is provided, STDIN is assumed.

read -d ''  copy <<"EOF"
COPY g10_smc
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
nc,
nt,
host,
mean_absolute_v,
color_vminus_i,
orbit_period,
periastron_date,
semimajor_axis,
eccentricity,
periastron_argument,
inclination,
longitude_ascending_node,
phase,
flag_interacting,
population,
age,
fe_h,
alpha_fe,
mbol,
mass,
radius,
teff,
logg,
spectral_type,
vsini,
r_env_r_star,
bond_albedo,
geom_albedo,
variability_type,
variability_amplitude,
variability_period,
variability_phase,
has_photocenter_motion
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
