-- create table section -----------------------------------------------------------
-- schema 		: ida			- Id-Authentication 
-- table 		: static_pin_h	- History table of Static pin to authonticate user
-- table alias  : spinh

-- schemas section -----------------------------------------------------------------

-- create schema if ida schema for Id-Authentication schema not exists
create schema if not exists ida
;

-- table section --------------------------------------------------------------------
create table ida.static_pin_h (
	
	uin  	character varying(28) not null,  	-- UIN of indivisuals and referenced from idrepo database
	pin 	character varying(64) not null,		-- Static PIN of an Indivisuals, Created by user using resident portal
	
	is_active 	boolean not null,
	cr_by 		character varying(256) not null,
	cr_dtimes 	timestamp not null,
	upd_by  	character varying (256),
	upd_dtimes timestamp,
	is_deleted 	boolean,
	del_dtimes timestamp,
	
	eff_dtimes timestamp not null				-- for history record maintenance including the latest record in base table.
	
)
;

-- keys section --------------------------------------------------------------------------
alter table ida.static_pin_h add constraint pk_spinh_id primary key (uin, eff_dtimes)
 ;

-- indexes section ------------------------------------------------------------------------
-- create index idx_spinh_<colX> on ida.static_pin_h(ColX)
-- ;

-- comments section ------------------------------------------------------------------------ 
comment on table ida.static_pin_h is 'To store generated list of static pin for Authentication'
;
comment on column ida.static_pin_h.pin is 'Static pin generated by the system for authentication'
;
