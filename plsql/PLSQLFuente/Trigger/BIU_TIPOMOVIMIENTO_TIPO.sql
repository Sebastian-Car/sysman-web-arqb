CREATE OR REPLACE TRIGGER "BIU_TIPOMOVIMIENTO_TIPO"  
BEFORE INSERT OR UPDATE OF COMPANIA, CODIGO, CONCEPTO, CLASE, CLASE_BODEGA_DESTINO,CLASE_BODEGA_ORIGEN,TIPOELEMENTO ON TIPOMOVIMIENTO 
FOR EACH ROW 
BEGIN
  PCK_ALMACEN_COM1.PR_VALIDAR_TIPO_CLASE(UN_TIPOMOVIMIENTO       => :NEW.CODIGO,
                                         UN_TIPOELEMENTO         => :NEW.TIPOELEMENTO,
                                         UN_CLASE_BODEGA_DESTINO => :NEW.CLASE_BODEGA_DESTINO,
                                         UN_CLASE_BODEGA_ORIGEN  => :NEW.CLASE_BODEGA_ORIGEN);
  PCK_ALMACEN_COM1.PR_VALIDAR_TRANSACCIONVALIDA(UN_COMPANIA             => :NEW.COMPANIA,
                                                 UN_TIPOMOVIMIENTO       => :NEW.CODIGO,
                                                 UN_CONCEPTO             => :NEW.CONCEPTO,
                                                 UN_CLASE                => :NEW.CLASE,
                                                 UN_TIPOELEMENTO         => :NEW.TIPOELEMENTO,
                                                 UN_CLASE_BODEGA_DESTINO => :NEW.CLASE_BODEGA_DESTINO,
                                                 UN_CLASE_BODEGA_ORIGEN  => :NEW.CLASE_BODEGA_ORIGEN);

END;
