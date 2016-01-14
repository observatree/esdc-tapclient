psql -p 8300 gacs -c "
CREATE TABLE tmass_new
(
  j_mag double precision,
  j_mag_error double precision,
  h_mag double precision,
  h_mag_error double precision,
  k_mag double precision,
  k_mag_error double precision,
  ext_key bigint,
  source_id bigint,
  original_source_id character varying(4000),
  alpha double precision,
  delta double precision,
  alpha_error double precision,
  delta_error double precision,
  ref_epoch double precision
);

CREATE TABLE tmass_original_valid_new
(
  ph_qual character varying(2),
  id bigint,
  designation character varying(4000),
  ra double precision,
  "dec" double precision,
  err_maj real,
  err_min real,
  err_ang real,
  j_m real,
  j_msigcom real,
  h_m real,
  h_msigcom real,
  k_m real,
  k_msigcom real,
  ext_key bigint,
  j_date real
);

CREATE TABLE tycho2_new
(
  mu_alpha_star double precision,
  mu_delta double precision,
  mu_alpha_star_error double precision,
  mu_delta_error double precision,
  bt_mag double precision,
  bt_mag_error double precision,
  vt_mag double precision,
  vt_mag_error double precision,
  source_id bigint,
  original_source_id character varying(4000),
  alpha double precision,
  delta double precision,
  alpha_error double precision,
  delta_error double precision,
  ref_epoch double precision
);

CREATE TABLE tycho2_original_valid_new
(
  posflg character varying(2),
  id bigint,
  tyc1 integer,
  tyc2 integer,
  tyc3 integer,
  m_r_adeg real,
  m_d_edeg real,
  em_r_a integer,
  em_d_e integer,
  pm_r_a real,
  pm_d_e real,
  epm_r_a real,
  epm_d_e real,
  bt real,
  ebt real,
  vt real,
  evt real,
  radeg real,
  dedeg real,
  e_r_a real,
  e_d_e real,
  ep_r_a real,
  ep_d_e real
);

" > creation_2.txt 
