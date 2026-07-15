ALTER TABLE SF_DETALLE_COBRO ADD (CONSECUTIVO NUMBER(3) DEFAULT 0 );
COMMIT;
COMMENT ON COLUMN SF_DETALLE_COBRO.CONSECUTIVO IS 'almacena el consecutivo del registro del concepto, reinicia por objeto cobro';
COMMIT;