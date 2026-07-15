create or replace TRIGGER "BIU_DETALLE_COMP_CNT_CARTERA" 
/*
      NAME              : BIU_DETALLE_COMP_CNT_CARTERA
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 07/10/2019
      TIME              : 12:30 PM
      SOURCE MODULE     : 
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : VERIFICA QUE SE PUEDAN REALIZAR MOVIMIENTOS A LAS CUENTAS CONTABLES

  */
BEFORE INSERT OR UPDATE ON DETALLE_COMPROBANTE_CNT
FOR EACH ROW

DECLARE

  MI_RSCUENTAS            SYS_REFCURSOR;
  MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_INDCAUSACION         CARTERA_CUENTA.IND_CAUSACION%TYPE;
  MI_PARAMETRO            VARCHAR2(255 CHAR);

BEGIN
    MI_PARAMETRO := NVL(PCK_SYSMAN_UTL.FC_PAR(:NEW.COMPANIA, 'CONTROLA CARTERA CUENTA', PCK_DATOS.MODULOCONTABILIDAD , SYSDATE),'NO');
    IF MI_PARAMETRO = 'SI' THEN
    FOR MI_RSCUENTAS IN ( SELECT TIPO_COMPROBANTE
                         FROM CARTERA_CUENTA
                         WHERE COMPANIA =:NEW.COMPANIA
                           AND ANO      =:NEW.ANO
                           AND CUENTA   =:NEW.CUENTA)LOOP

       BEGIN 

         SELECT IND_CAUSACION
         INTO MI_INDCAUSACION
         FROM CARTERA_CUENTA
         WHERE COMPANIA         =:NEW.COMPANIA
           AND ANO              =:NEW.ANO
           AND CUENTA           =:NEW.CUENTA
           AND TIPO_COMPROBANTE =:NEW.TIPO_CPTE ;

       EXCEPTION WHEN NO_DATA_FOUND THEN
         BEGIN
          RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;           
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 

            MI_MSGERROR(1).CLAVE := 'TIPO';
            MI_MSGERROR(1).VALOR := :NEW.TIPO_CPTE;
            MI_MSGERROR(2).CLAVE := 'CUENTA';
            MI_MSGERROR(2).VALOR := :NEW.CUENTA;
                  
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                         UN_EXC_COD => SQLCODE,
                         UN_TABLAERROR=>'DETALLE_COMPROBANTE_CNT',
                         UN_ERROR_COD => PCK_ERRORES.ERR_CNT_CUENTACARTERA,
                         UN_REEMPLAZOS => MI_MSGERROR
                       );
                END;           

        END;        
        
        
        IF MI_INDCAUSACION IN (0) THEN
            IF :NEW.ANO_AFECT IS NULL AND :NEW.TIPO_CPTE_AFECT IS NULL AND :NEW.CMPTE_AFECTADO IS NULL AND :NEW.CONSECUTIVOAFECTADO IS NULL THEN
                BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;   
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
        
                        MI_MSGERROR(1).CLAVE := 'CUENTA';
                        MI_MSGERROR(1).VALOR := :NEW.CUENTA;        
        
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                                 UN_EXC_COD => SQLCODE,
                                 UN_TABLAERROR=>'DETALLE_COMPROBANTE_CNT',
                                 UN_ERROR_COD => PCK_ERRORES.ERR_CNT_CUENTACARTERAAFECT,
                                 UN_REEMPLAZOS => MI_MSGERROR
                               );
                END;
             END IF;  
            
        END IF;
        

   END LOOP; 
 END IF;
END;