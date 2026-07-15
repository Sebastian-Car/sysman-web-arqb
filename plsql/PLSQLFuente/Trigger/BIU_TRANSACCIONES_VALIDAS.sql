CREATE OR REPLACE TRIGGER "BIU_TRANSACCIONES_VALIDAS" 
  /*
        NAME              : BIU_TRANSACCIONES_VALIDAS
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : José Pascual Gómez Blanco
        DATE MIGRADOR     : 08/03/2018
        TIME              : 04:07 PM
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Trigger antes de insertar y actualizar para la tabla TRANSACCIONESVALIDADS, 
                            Con este se controla que no incluyan transacciones validas que estan por fuera de la logica dada    
        */
BEFORE INSERT OR UPDATE OF TIPO_ELEMENTO, CLASE_BODEGA_DESTINO, CLASE_BODEGA_ORIGEN ON TRANSACCIONES_VALIDAS
FOR EACH ROW
BEGIN
 PCK_ALMACEN_COM1.PR_VALIDAR_TIPO_CLASE(UN_TIPOMOVIMIENTO       => 'Configuración'
                                       ,UN_TIPOELEMENTO         => :NEW.TIPO_ELEMENTO      
                                       ,UN_CLASE_BODEGA_DESTINO => :NEW.CLASE_BODEGA_DESTINO
                                       ,UN_CLASE_BODEGA_ORIGEN  => :NEW.CLASE_BODEGA_ORIGEN);
END;
