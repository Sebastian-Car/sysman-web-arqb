CREATE OR REPLACE TRIGGER "BIUD_DETALLE_COMPROBANTE_CNT"  
BEFORE DELETE OR INSERT OR UPDATE ON DETALLE_COMPROBANTE_CNT
FOR EACH ROW
DECLARE
  MI_RTA_OLD            VARCHAR2(2 CHAR);
  MI_RTA_NEW            VARCHAR2(2 CHAR);
  MI_RTA_COLD           VARCHAR2(2 CHAR);
  MI_RTA_CNEW           VARCHAR2(2 CHAR);
  MI_RTA                VARCHAR2(2 CHAR);
  MI_F                  DATE;
BEGIN
  --EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_DATE_FORMAT =''DD/MM/YYYY HH24:MI:SS''';
  --VALIDAR QUE NO ESTE CERRADO EL DÍA DEL COMPROBANTE
  MI_RTA_OLD:='A';
  MI_RTA_NEW:='A';
  MI_RTA    :='A';
  MI_F:= :NEW.FECHA;
IF PCK_GENERALES.FC_CONS_CAMBIOABONO() IN (0) THEN --VALIDACION PARA CUANDO SE ESTA CAMBIANDO EL ABONO EN CUADRECARTERA
  
IF PCK_GENERALES.FC_CONS_CAMBIONIT() IN (0) THEN --VALIDACION PARA CUANDO SE ESTA CAMBIANDO EL NIT AL TERCERO
  
  IF DELETING THEN
    MI_RTA := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADODIA(UN_COMPANIA   => :OLD.COMPANIA,
                                                    UN_ANO        => TO_NUMBER(TO_CHAR(:OLD.FECHA,'YYYY')),
                                                    UN_MES        => TO_NUMBER(TO_CHAR(:OLD.FECHA,'MM')),
                                                    UN_DIA        => TO_NUMBER(TO_CHAR(:OLD.FECHA,'DD')),
                                                    UN_MODULO     => 1,
                                                    UN_PROCESO    => 1); 
  END IF;
  IF INSERTING  THEN
    MI_RTA := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADODIA(UN_COMPANIA   => :NEW.COMPANIA,
                                                    UN_ANO        => TO_NUMBER(TO_CHAR(:NEW.FECHA,'YYYY')),
                                                    UN_MES        => TO_NUMBER(TO_CHAR(:NEW.FECHA,'MM')),
                                                    UN_DIA        => TO_NUMBER(TO_CHAR(:NEW.FECHA,'DD')),
                                                    UN_MODULO     => 1,
                                                    UN_PROCESO    => 1); 
  END IF;
  
  IF MI_RTA <> 'A' THEN
    DECLARE
      MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR; 
    BEGIN
      RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;           
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
      MI_MSGERROR(1).CLAVE := 'FECHA';
      MI_MSGERROR(1).VALOR := TO_CHAR(:NEW.FECHA,'DD/MM/YYYY');
      PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_TABLAERROR =>'DETALLE_COMPROBANTE_CNT',
          UN_ERROR_COD  => PCK_ERRORES.ERROR_GRAL_DIACERRADO,
          UN_REEMPLAZOS => MI_MSGERROR);
    END;  
  END IF;
   
  IF UPDATING THEN
    MI_RTA_OLD := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADODIA(UN_COMPANIA   => :OLD.COMPANIA,
                                                    UN_ANO        => TO_NUMBER(TO_CHAR(:OLD.FECHA,'YYYY')),
                                                    UN_MES        => TO_NUMBER(TO_CHAR(:OLD.FECHA,'MM')),
                                                    UN_DIA        => TO_NUMBER(TO_CHAR(:OLD.FECHA,'DD')),
                                                    UN_MODULO     => 1,
                                                    UN_PROCESO    => 1); 
    MI_RTA_NEW := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADODIA(UN_COMPANIA   => :NEW.COMPANIA,
                                                    UN_ANO        => TO_NUMBER(TO_CHAR(:NEW.FECHA,'YYYY')),
                                                    UN_MES        => TO_NUMBER(TO_CHAR(:NEW.FECHA,'MM')),
                                                    UN_DIA        => TO_NUMBER(TO_CHAR(:NEW.FECHA,'DD')),
                                                    UN_MODULO     => 1,
                                                    UN_PROCESO    => 1); 
  END IF;
  IF MI_RTA_OLD<>'A' OR MI_RTA_NEW<>'A' THEN
    IF (:NEW.COMPANIA                           <>:OLD.COMPANIA
      OR :NEW.ANO                                <>:OLD.ANO
      OR :NEW.TIPO_CPTE                          <>:OLD.TIPO_CPTE
      OR :NEW.COMPROBANTE                        <>:OLD.COMPROBANTE
      OR :NEW.CONSECUTIVO                        <>:OLD.CONSECUTIVO
      OR :NEW.CUENTA                             <>:OLD.CUENTA
      OR :NEW.NATURALEZA                         <>:OLD.NATURALEZA
      OR :NEW.TIPOPPTAL                          <>:OLD.TIPOPPTAL
      OR :NEW.NUMEROPPTAL                        <>:OLD.NUMEROPPTAL
      OR :NEW.CUENTAPPTAL                        <>:OLD.CUENTAPPTAL
      OR :NEW.CONSECUTIVOPPTO                    <>:OLD.CONSECUTIVOPPTO
      OR :NEW.VALOR_DEBITO                       <>:OLD.VALOR_DEBITO
      OR :NEW.VALOR_CREDITO                      <>:OLD.VALOR_CREDITO
      OR :NEW.EJECUCION_DEBITO                   <>:OLD.EJECUCION_DEBITO
      OR :NEW.EJECUCION_CREDITO                  <>:OLD.EJECUCION_CREDITO
      OR :NEW.BASE_GRAVABLE                      <>:OLD.BASE_GRAVABLE
      OR :NEW.TIPO_DOCUMENTO                     <>:OLD.TIPO_DOCUMENTO
      OR :NEW.NRO_DOCUMENTO                      <>:OLD.NRO_DOCUMENTO
      OR :NEW.CENTRO_COSTO                       <>:OLD.CENTRO_COSTO
      OR :NEW.TERCERO                            <>:OLD.TERCERO
      OR :NEW.SUCURSAL                           <>:OLD.SUCURSAL
      OR :NEW.AUXILIAR                           <>:OLD.AUXILIAR
      OR :NEW.REFERENCIA                         <>:OLD.REFERENCIA
      OR :NEW.FUENTE_RECURSO                     <>:OLD.FUENTE_RECURSO
      OR :NEW.ANO_AFECT                          <>:OLD.ANO_AFECT
      OR :NEW.TIPO_CPTE_AFECT                    <>:OLD.TIPO_CPTE_AFECT
      OR :NEW.CMPTE_AFECTADO                     <>:OLD.CMPTE_AFECTADO
      OR :NEW.CONSECUTIVOAFECTADO                <>:OLD.CONSECUTIVOAFECTADO
      OR :NEW.CHEQUEPARAANULAR                   <>:OLD.CHEQUEPARAANULAR
      OR :NEW.CIERRE                             <>:OLD.CIERRE
      OR :NEW.BASE_IVA                           <>:OLD.BASE_IVA
      OR :NEW.TIPOCONTRATO                       <>:OLD.TIPOCONTRATO
      OR :NEW.NUMEROCONTRATO                     <>:OLD.NUMEROCONTRATO
      OR :NEW.SALDOCUENTA                        <>:OLD.SALDOCUENTA
      OR :NEW.ABONOINICIAL                       <>:OLD.ABONOINICIAL
      OR :NEW.PORCENTAJERETENCION                <>:OLD.PORCENTAJERETENCION
      OR :NEW.VALORTOTAL                         <>:OLD.VALORTOTAL
      OR :NEW.RECONOCIMIENTO                     <>:OLD.RECONOCIMIENTO
      OR :NEW.TEXTOD                             <>:OLD.TEXTOD
      OR :NEW.FORMATO_CONCEPTO_EX                <>:OLD.FORMATO_CONCEPTO_EX
      OR :NEW.REVELACIONES                       <>:OLD.REVELACIONES
      OR :NEW.PORDISTRIBUIDOCNT                  <>:OLD.PORDISTRIBUIDOCNT
      OR :NEW.PAIS                               <>:OLD.PAIS
      OR :NEW.DEPARTAMENTO                       <>:OLD.DEPARTAMENTO
      OR :NEW.CIUDAD                             <>:OLD.CIUDAD
      OR :NEW.D_DEPENDENCIACNT                   <>:OLD.D_DEPENDENCIACNT
      OR :NEW.D_RESPONSABLECNT                   <>:OLD.D_RESPONSABLECNT
      OR :NEW.D_RESPSUCURSALCNT                  <>:OLD.D_RESPSUCURSALCNT
      OR :NEW.CODIGO_PPTAL                       <>:OLD.CODIGO_PPTAL
      OR :NEW.DEBITO_EQUIV                       <>:OLD.DEBITO_EQUIV
      OR :NEW.CREDITO_EQUIV                      <>:OLD.CREDITO_EQUIV
      OR :NEW.SINIDENTIFICAR                     <>:OLD.SINIDENTIFICAR
      OR :NEW.DESEMBOLSO                         <>:OLD.DESEMBOLSO
      OR :NEW.TIPO_GENERADIF                     <>:OLD.TIPO_GENERADIF
      OR :NEW.NRO_GENERADIF                      <>:OLD.NRO_GENERADIF
      OR :NEW.FECHA_CONSIGNACIONPLANO            <>:OLD.FECHA_CONSIGNACIONPLANO
      OR :NEW.HORA                               <>:OLD.HORA
      OR :NEW.FECHA                              <>:OLD.FECHA
      OR :NEW.FECHA_IDENTIFICACION               <>:OLD.FECHA_IDENTIFICACION
      OR :NEW.FECHA_CALC_DET                     <>:OLD.FECHA_CALC_DET
			)
    THEN
       /*
       Los campos excluido por efecto de las afectaciones y conciliaciones son
       :NEW.DEBITO_AFECTADO                   
       :NEW.CREDITO_AFECTADO        
       :NEW.DEBITOSAFECTADOS_CXP      
       :NEW.CREDITOSAFECTADOS_CXP  
       
       Se excluyen por efectos de las anulaciones
        :NEW.DESCRIPCION
       
       Se excluyen los campos de pago retención por que el mantenimiento no afecta losw saldos  
       OR :NEW.PAGO_RETENCION                     <>:OLD.PAGO_RETENCION
       OR :NEW.BANCO_RETENCION                    <>:OLD.BANCO_RETENCION
       OR :NEW.FECHA_PAGO_RETENCION               <>:OLD.FECHA_PAGO_RETENCION
      */
      DECLARE
        MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR; 
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;           
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
        MI_MSGERROR(1).CLAVE := 'FECHA';
        MI_MSGERROR(1).VALOR := TO_CHAR(:NEW.FECHA,'DD/MM/YYYY');
        PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD     => SQLCODE,
            UN_TABLAERROR  =>'DETALLE_COMPROBANTE_CNT',
            UN_ERROR_COD   => PCK_ERRORES.ERROR_GRAL_DIACERRADO,
            UN_REEMPLAZOS  => MI_MSGERROR
            );
      END;  
    END IF;   
                                    
  END IF;
  
  
  --VALIDAR QUE SI SE VA A REALIZAR LA AFECTACIÓN ESTA ESTE COMPLETO EN SU INTEGRIDAD
  IF INSERTING OR UPDATING THEN  	
    DECLARE 
      NUMAFEC NUMBER(1,0);
    BEGIN
      NUMAFEC := 0;
      NUMAFEC := NUMAFEC + CASE WHEN :NEW.ANO_AFECT           IS NOT NULL THEN 1 ELSE 0 END;
      NUMAFEC := NUMAFEC + CASE WHEN :NEW.TIPO_CPTE_AFECT     IS NOT NULL THEN 1 ELSE 0 END;
      NUMAFEC := NUMAFEC + CASE WHEN :NEW.CMPTE_AFECTADO      IS NOT NULL THEN 1 ELSE 0 END;
      NUMAFEC := NUMAFEC + CASE WHEN :NEW.CONSECUTIVOAFECTADO IS NOT NULL THEN 1 ELSE 0 END;
      IF NUMAFEC>0 AND NUMAFEC<4  THEN
         DECLARE
            MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR; 
          BEGIN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;           
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
              MI_MSGERROR(1).CLAVE := 'TIPO';
              MI_MSGERROR(1).VALOR := :NEW.TIPO_CPTE;
              MI_MSGERROR(2).CLAVE := 'COMPROBANTE';
              MI_MSGERROR(2).VALOR := :NEW.COMPROBANTE;
              MI_MSGERROR(3).CLAVE := 'CONSECUTIVO';
              MI_MSGERROR(3).VALOR := :NEW.CONSECUTIVO;
              PCK_ERR_MSG.RAISE_WITH_MSG(
                     UN_EXC_COD => SQLCODE,
                     UN_TABLAERROR=>'DETALLE_COMPROBANTE_CNT',
                     UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_AFECTARHERRADO,
                     UN_REEMPLAZOS => MI_MSGERROR
                   );
          END;  
      END IF;
    END;  
  END IF;  
END IF;  
END IF;
END;