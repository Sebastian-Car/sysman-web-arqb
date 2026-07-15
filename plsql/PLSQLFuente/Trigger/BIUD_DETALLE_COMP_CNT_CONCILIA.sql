CREATE OR REPLACE TRIGGER "BIUD_DETALLE_COMP_CNT_CONCILIA"  
BEFORE DELETE OR INSERT OR UPDATE OF COMPANIA, ANO, TIPO_CPTE, COMPROBANTE, CUENTA, PAGADOBANCO, CONCILIADOR, FECHA_CONCILIA
        ON DETALLE_COMPROBANTE_CNT
FOR EACH ROW
DECLARE
  MI_RTA_OLD            VARCHAR2(2 CHAR);
  MI_RTA_NEW            VARCHAR2(2 CHAR);
  MI_RTA_COLD           VARCHAR2(2 CHAR);
  MI_RTA_CNEW           VARCHAR2(2 CHAR);
  MI_RTA                VARCHAR2(2 CHAR);  
BEGIN
  --EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_DATE_FORMAT =''DD/MM/YYYY HH24:MI:SS''';
  --VALIDAR QUE NO ESTE CERRADO EL DÍA DEL COMPROBANTE
  MI_RTA_OLD:='A';
  MI_RTA_NEW:='A';
  MI_RTA    :='A';

IF PCK_GENERALES.FC_CONS_CAMBIONIT() IN (0) THEN --VALIDACION PARA CUANDO SE ESTA CAMBIANDO EL NIT AL TERCERO  
  IF DELETING THEN
    IF   :OLD.FECHA_CONCILIA IS NOT NULL THEN
      MI_RTA := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADODIA(UN_COMPANIA   => :OLD.COMPANIA,
                                                      UN_ANO        => TO_NUMBER(TO_CHAR(:OLD.FECHA_CONCILIA,'YYYY')),
                                                      UN_MES        => TO_NUMBER(TO_CHAR(:OLD.FECHA_CONCILIA,'MM')),
                                                      UN_DIA        => TO_NUMBER(TO_CHAR(:OLD.FECHA_CONCILIA,'DD')),
                                                      UN_MODULO     => 1,
                                                      UN_PROCESO    => 2); 
    END IF;
  END IF;
  IF INSERTING  THEN
    IF   :NEW.FECHA_CONCILIA IS NOT NULL THEN
      MI_RTA := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADODIA(UN_COMPANIA   => :NEW.COMPANIA,
                                                      UN_ANO        => TO_NUMBER(TO_CHAR(:NEW.FECHA_CONCILIA,'YYYY')),
                                                      UN_MES        => TO_NUMBER(TO_CHAR(:NEW.FECHA_CONCILIA,'MM')),
                                                      UN_DIA        => TO_NUMBER(TO_CHAR(:NEW.FECHA_CONCILIA,'DD')),
                                                      UN_MODULO     => 1,
                                                      UN_PROCESO    => 2); 
    END IF;
  END IF;

  IF MI_RTA <> 'A' THEN
    DECLARE
      MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR; 
    BEGIN
      RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;           
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
      MI_MSGERROR(1).CLAVE := 'FECHA';
      MI_MSGERROR(1).VALOR := TO_CHAR(:NEW.FECHA_CONCILIA,'DD/MM/YYYY');
      PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_TABLAERROR =>'DETALLE_COMPROBANTE_CNT',
          UN_ERROR_COD  => PCK_ERRORES.ERROR_GRAL_DIACERRADO,
          UN_REEMPLAZOS => MI_MSGERROR);
    END;  
  END IF;

  IF UPDATING THEN
    IF   :OLD.FECHA_CONCILIA <> :NEW.FECHA_CONCILIA 
      OR :OLD.PAGADOBANCO    <> :NEW.PAGADOBANCO 
      OR :OLD.CONCILIADOR    <> :NEW.CONCILIADOR
      OR :OLD.CUENTA         <> :NEW.CUENTA  THEN
      IF   :OLD.FECHA_CONCILIA IS NOT NULL THEN
        MI_RTA_OLD := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADODIA(UN_COMPANIA   => :OLD.COMPANIA,
                                                        UN_ANO        => TO_NUMBER(TO_CHAR(:OLD.FECHA_CONCILIA,'YYYY')),
                                                        UN_MES        => TO_NUMBER(TO_CHAR(:OLD.FECHA_CONCILIA,'MM')),
                                                        UN_DIA        => TO_NUMBER(TO_CHAR(:OLD.FECHA_CONCILIA,'DD')),
                                                        UN_MODULO     => 1,
                                                        UN_PROCESO    => 2); 
      END IF;
      IF   :NEW.FECHA_CONCILIA IS NOT NULL THEN
        MI_RTA_NEW := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADODIA(UN_COMPANIA   => :NEW.COMPANIA,
                                                        UN_ANO        => TO_NUMBER(TO_CHAR(:NEW.FECHA_CONCILIA,'YYYY')),
                                                        UN_MES        => TO_NUMBER(TO_CHAR(:NEW.FECHA_CONCILIA,'MM')),
                                                        UN_DIA        => TO_NUMBER(TO_CHAR(:NEW.FECHA_CONCILIA,'DD')),
                                                        UN_MODULO     => 1,
                                                        UN_PROCESO    => 2); 
      END IF;
    END IF;
  END IF;


    IF MI_RTA_OLD<>'A' OR MI_RTA_NEW<>'A' THEN

        DECLARE
          MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR; 
        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;           
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
          MI_MSGERROR(1).CLAVE := 'FECHA';
          MI_MSGERROR(1).VALOR := TO_CHAR(:NEW.FECHA_CONCILIA,'DD/MM/YYYY');
          PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_TABLAERROR =>'DETALLE_COMPROBANTE_CNT',
              UN_ERROR_COD  => PCK_ERRORES.ERROR_GRAL_DIACERRADO,
              UN_REEMPLAZOS => MI_MSGERROR
              );
        END; 
    END IF;                                                 
  END IF;

END;