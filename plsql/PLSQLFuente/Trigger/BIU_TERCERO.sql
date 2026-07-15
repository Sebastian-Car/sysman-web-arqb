CREATE OR REPLACE TRIGGER "BIU_TERCERO"  
  /*
      NAME              : BI_TERCERO
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JAVIER VILLATE
      DATE MIGRADOR     : 28/04/2017
      TIME              : 8:15 AM
      SOURCE MODULE     : 
      MODIFIER          : JOSE PASCUAL GOMEZ
      DATE MODIFIED     : 31/08/2017
      TIME              : 09:35 AM
      DESCRIPTION       : JOSE PASCUAL: Se ajusta para que cumpla con estandar y 
                                        ademas el mensaje muestre el tercero afectado y la 
                                        acción sobre el mismo
                          
  */
BEFORE INSERT OR UPDATE OF NIT, SUCURSAL ON TERCERO 
FOR EACH ROW
BEGIN
  DECLARE
    MI_MSGERRORES             PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_ACCION                 VARCHAR2(200);
  BEGIN  
    IF :NEW.NIT=PCK_DATOS.FC_CONS_TERCERO AND :NEW.SUCURSAL<>PCK_DATOS.FC_CONS_SUCURSAL 
    OR :NEW.NIT<>PCK_DATOS.FC_CONS_TERCERO AND :NEW.SUCURSAL=PCK_DATOS.FC_CONS_SUCURSAL THEN
      IF UPDATING THEN
        MI_ACCION:='Actualizar';
      ELSE
        MI_ACCION:='Insertar';
      END IF;
      RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
    END IF;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
    MI_MSGERRORES(1).CLAVE := 'NIT';
    MI_MSGERRORES(1).VALOR := :NEW.NIT;
    MI_MSGERRORES(2).CLAVE := 'SUCURSAL';
    MI_MSGERRORES(2).VALOR := :NEW.SUCURSAL;
    MI_MSGERRORES(3).CLAVE := 'ACCION';
    MI_MSGERRORES(3).VALOR := MI_ACCION;
    PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_TABLAERROR => 'TERCERO'
                   ,UN_ERROR_COD  => PCK_ERRORES.ERROR_GRAL_INSERTARTERCERO
                   ,UN_REEMPLAZOS =>MI_MSGERRORES
                   );
  END;  
END;
