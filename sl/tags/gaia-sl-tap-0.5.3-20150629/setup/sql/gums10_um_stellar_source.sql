
-- GUMS10 Table

CREATE TABLE gums10_um_stellar_source
(
  -- UMRoot
  source_id bigint NOT NULL,
  source_extended_id character varying(23),

  -- UMAstroRoot
  alpha double precision,
  delta double precision,
  distance double precision,
  mu_alpha double precision,
  mu_delta double precision,
  radial_velocity double precision,
  pos spoint, -- PgSphere 'spoint'
  q3c_ipix bigint, -- Q3C 'ipix'
  ipix_nest_1024 bigint, -- Healpix pixel id (NSIDE 1024) 

  -- UMPhotoRoot
  mag_g double precision,
  mag_g_bp double precision,
  mag_g_rp double precision,
  mag_g_rvs double precision,
  av double precision,

  -- Rest of UMStellarSource
  ag double precision,
  rv double precision,
  nc integer,
  nt integer,
  host integer,
  mean_absolute_v double precision,
  color_vminus_i double precision,
  orbit_period double precision,
  periastron_date double precision,
  semimajor_axis double precision,
  eccentricity double precision,
  periastron_argument double precision,
  inclination double precision,
  longitude_ascending_node double precision,
  phase double precision,
  flag_interacting integer,
  population integer,
  age double precision,
  fe_h double precision,
  alpha_fe double precision,
  mbol double precision,
  mass double precision,
  radius double precision,
  teff double precision,
  logg double precision,
  spectral_type character varying(50),
  vsini double precision,
  r_env_r_star double precision,
  bond_albedo double precision,
  geom_albedo double precision,
  variability_type character varying(50),
  variability_amplitude double precision,
  variability_period double precision,
  variability_phase double precision,
  has_photocenter_motion boolean,

  CONSTRAINT gums10_um_stellar_source_pkey PRIMARY KEY (source_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE gums10_um_stellar_source OWNER TO geaops;

-- DROP TABLE gums10_um_stellar_source;

-- INDEXES
CREATE INDEX i_gums10_um_stellar_source_source_id
	ON gums10_um_stellar_source(source_id);


-- Subscription of gums10_um_stellar_source into tables
INSERT INTO tables(schema_name, table_name, utype, description) VALUES ('tap_schema', 'gums10_um_stellar_source', null, 'Table for PLATOGOG Sources');

-- Columns belonging to table gums10_um_stellar_source

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'source_id', null, null, null, 'Long identifier', 'long', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'alpha', null, 'pos.eq.ra;meta.main', 'deg', 'Right ascention of the baricenter at J2010 reference epoch in the ICRS frame', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'delta', null, 'pos.eq.dec;meta.main', 'deg', 'Declination of the baricenter at J2010 reference epoch in the ICRS frame', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'distance', null, null, 'pc', 'Distance from the baricenter of the Solar System to the baricenter of the source at J2010 reference epoch', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'mu_alpha', null, null, 'mas/year', 'Proper  motion along right ascention at J2010 reference epoch', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'mu_delta', null, null, 'mas/year', 'Proper motion along declination at J2010 reference epoch', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'radial_velocity', null, null, 'km/s', 'Radial Velocity at J2010 reference epoch', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'mag_g', null, null, 'mag', 'Apparent magnitude at J2010 reference epoch', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'mag_g_bp', null, null, 'mag', 'Apparent magnitude at J2010 reference epoch', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'mag_g_rp', null, null, 'mag', 'Apparent magnitude at J2010 reference epoch', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'mag_g_rvs', null, null, 'mag', 'Apparent magnitude at J2010 reference epoch', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'av', null, null, 'mag', 'Interstellar absorption in the V-band', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'ag', null, null, 'mag', 'Interstellar absortion in the G band', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'rv', null, null, 'mag', 'Extinction parameter', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'source_extended_id', null, null, null, 'Extended source identifier', 'char', 23, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'nc', null, null, null, 'Number of components', 'integer', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'nt', null, null, null, 'Total number of object', 'integer', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'host', null, null, null, '1=Milky way', 'integer', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'mean_absolute_v', null, null, 'mag', 'Mean absolute V magnitude', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'color_vminus_i', null, null, 'mag', 'Intrinsec V-I color', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'orbit_period', null, null, 'day', 'Period of the orbit', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'periastron_date', null, null, 'day', '0:P at J2010 reference epoch', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'semimajor_axis', null, null, 'AU', 'Para hiper bolic orbits', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'eccentricity', null, null, null, 'Eccentricity', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'periastron_argument', null, null, 'deg', 'Periastron argument', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'inclination', null, null, 'deg', 'Inclination', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'longitude_ascending_node', null, null, 'deg', 'Longitude of ascending node', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'phase', null, null, null, 'Exoplanets only', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'flag_interacting', null, null, null, 'Only for star system', 'integer', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'population', null, null, null, 'Population', 'integer', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'age', null, null, 'Gyear', 'Age', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'fe_h', null, null, 'dex', 'Metallicity', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'alpha_fe', null, null, 'dex', 'Alpha elements', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'mbol', null, null, 'mag', 'Absolute bolometric magnitude at J2010 reference epoch', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'mass', null, null, 'Solar Mass', 'Mass', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'radius', null, null, 'Solar Radius', 'Radius', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'teff', null, null, 'K', 'Effective temperature', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'logg', null, null, 'dex', 'Gravity', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'spectral_type', null, null, null, 'For stars only: 1=O,2=B,3=A,4=F,5=G,6=K,7=M,8=AGB,0=WD (e.g. 5.7 = G7), to be extended to brown dwarfs', 'char', 50, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'vsini', null, null, 'km/s', 'v sin i', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'r_env_r_star', null, null, null, 'Envelop characterisic for Be stars', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'bond_albedo', null, null, null, 'Exoplanets only', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'geom_albedo', null, null, null, 'Exoplanets only', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'variability_type', null, null, null, 'deltascuti ACV cepheid RRab ...', 'char', 50, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'variability_amplitude', null, null, 'mag', 'The amplitude', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'variability_period', null, null, 'day', 'The period', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'variability_phase', null, null, null, 'aphase', 'double', null, 0, 0, 0);

INSERT INTO columns (table_name, column_name, utype, ucd, unit, description, datatype, size, principal, indexed, std)
VALUES ('gums10_um_stellar_source', 'has_photocenter_motion', null, null, null, 'Boolean describing if the photocenter has or not motion', 'boolean', null, 0, 0, 0);

