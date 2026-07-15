CREATE OR REPLACE TRIGGER BIU_DETALLE_COMPROBANTE_CNT_CU
/*
      NAME              : BIU_DETALLE_COMPROBANTE_CNT_CU
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  :  JOSÉ PASCUAL GÓMEZ BLANCO    
      DATE MIGRADOR     : 15/04/2019
      TIME              : 12:00 PM
      SOURCE MODULE     : 
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : SE CONTROLA QUE LAS CUENTAS QUE SE DEBEN CONTROLAR POR NÚMERO DE DOCUMENTO SE PUEDAN CONTROLAR
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : 
*/ 
BEFORE INSERT OR UPDATE OF  COMPANIA
                           , ANO
                           , TIPO_CPTE
                           , COMPROBANTE
                           , CUENTA                           
                           , NRO_DOCUMENTO
                           , ANO_AFECT
                           , TIPO_CPTE_AFECT
                           , CMPTE_AFECTADO
                           , CONSECUTIVOAFECTADO
ON DETALLE_COMPROBANTE_CNT 
REFERENCING OLD AS ANT NEW AS NUE 
FOR EACH ROW 
DECLARE  

MI_PARAMETRO            VARCHAR2(255 CHAR);

BEGIN

    MI_PARAMETRO := NVL(PCK_SYSMAN_UTL.FC_PAR(:NUE.COMPANIA, 'CONTROLA CARTERA CUENTA', PCK_DATOS.MODULOCONTABILIDAD , SYSDATE),'NO');
    IF MI_PARAMETRO = 'SI' THEN
    PCK_CONTABILIDAD5.PR_CONTROLACUENTA(UN_COMPANIA            =>:NUE.COMPANIA,
                      UN_ANIO                =>:NUE.ANO,
                      UN_CUENTA              =>:NUE.CUENTA,
                      UN_TIPO                =>:NUE.TIPO_CPTE,
                      UN_NRO_DOCUMENTO       =>:NUE.NRO_DOCUMENTO,
                      UN_ANO_AFECT           =>:NUE.ANO_AFECT,
                      UN_TIPO_CPTE_AFECT     =>:NUE.TIPO_CPTE_AFECT,
                      UN_CMPTE_AFECTADO      =>:NUE.CMPTE_AFECTADO,
                      UN_CONSECUTIVOAFECTADO =>:NUE.CONSECUTIVOAFECTADO);
    END IF;
END;
