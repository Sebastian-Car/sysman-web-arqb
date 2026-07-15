CREATE OR REPLACE TRIGGER "BU_TIPOMOVIMIENTO"  
/*
      NAME              : BU_DEPENDENCIA
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : 
      DATE MIGRADOR     : 
      TIME              : 
      SOURCE MODULE     : 
      MODIFIER          : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MODIFIED     : 31/01/2017
      TIME              : 11:55 AM
      DESCRIPTION       : SE AJUSTA AL ESTANDAR                          
*/
BEFORE UPDATE OF   CLASE
                 , TIPOELEMENTO
                 , CONCEPTO
                 , INVENTARIOINICIAL
                 , GENERAPLACA
                 , CLASE_BODEGA_ORIGEN
                 , CLASE_BODEGA_DESTINO 
              ON TIPOMOVIMIENTO
FOR EACH ROW 
DECLARE  
  MI_I          NUMBER;
BEGIN
  
  BEGIN
    SELECT COUNT(COMPANIA) 
      INTO MI_I
      FROM MOVIMIENTO   
     WHERE COMPANIA       = :NEW.COMPANIA
       AND TIPOMOVIMIENTO = :NEW.CODIGO;
  EXCEPTION WHEN NO_DATA_FOUND THEN 
    MI_I :=0;
  END;
  BEGIN 
    IF MI_I>0 
       AND (    :NEW.CLASE               <> :OLD.CLASE
            OR :NEW.TIPOELEMENTO         <> :OLD.TIPOELEMENTO
            OR :NEW.CONCEPTO             <> :OLD.CONCEPTO
            OR :NEW.INVENTARIOINICIAL    <> :OLD.INVENTARIOINICIAL
            OR :NEW.GENERAPLACA          <> :OLD.GENERAPLACA
            OR :NEW.CLASE_BODEGA_ORIGEN  <> :OLD.CLASE_BODEGA_ORIGEN
            OR :NEW.CLASE_BODEGA_DESTINO <> :OLD.CLASE_BODEGA_DESTINO) THEN
       RAISE PCK_EXCEPCIONES.EXC_ALMACEN; 
      --RAISE_APPLICATION_ERROR(-20000, 'No es posible realizar esta modificación debido a que ya existen movimientos con este tipo de comprobante.'  );
    END IF;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE
               ,UN_TABLAERROR => 'TIPOMOVIMIENTO'
               ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_MOV_TIPOMOVIMI
         ); 
  END;
END;
