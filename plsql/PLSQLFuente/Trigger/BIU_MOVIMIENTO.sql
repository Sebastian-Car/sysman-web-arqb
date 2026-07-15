CREATE OR REPLACE TRIGGER "BIU_MOVIMIENTO" 
  /*
        NAME              : BIU_MOVIMIENTO 
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : Carlos Alberto Manrique Palacios
        DATE MIGRADOR     : 06/02/2017
        TIME              : 04:07 PM
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Trigger antes de insertar y actualizar para la tabla MOVIMIENTO, 
                            genera el campos relacionados con la fecha del registro insertado o modificado.
                            Se crea este trigger al eliminar las columnas vituales de la base de datos
        */
BEFORE INSERT OR UPDATE OF COMPANIA, TIPOMOVIMIENTO, CLASE_BODEGA_DESTINO,CLASE_BODEGA_ORIGEN, FECHA,ANO ON MOVIMIENTO
FOR EACH ROW
BEGIN

 IF PCK_GENERALES.FC_CONS_CAMBIONIT() IN (0) THEN --VALIDACION PARA CUANDO SE ESTA CAMBIANDO EL NIT AL TERCERO
   PCK_ALMACEN_COM1.PR_VALIDAR_TIPO_CLASE_MOV(UN_COMPANIA             => :NEW.COMPANIA
                                             ,UN_TIPOMOVIMIENTO       => :NEW.TIPOMOVIMIENTO
                                             ,UN_CLASE_BODEGA_DESTINO => :NEW.CLASE_BODEGA_DESTINO
                                             ,UN_CLASE_BODEGA_ORIGEN  => :NEW.CLASE_BODEGA_ORIGEN);
   :NEW.ANO:= TO_NUMBER(TO_CHAR(:NEW.FECHA,'YYYY'));
 END IF;  
   
END;