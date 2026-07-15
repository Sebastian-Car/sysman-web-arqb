CREATE OR REPLACE TRIGGER "BIUD_COMPROBANTE_CNT" 
BEFORE DELETE OR INSERT OR UPDATE ON COMPROBANTE_CNT
FOR EACH ROW
DECLARE
  MI_RTA_OLD          VARCHAR2(2 CHAR);
  MI_RTA_NEW          VARCHAR2(2 CHAR);
  MI_RTA              VARCHAR2(2 CHAR);
  MI_TABLA                 VARCHAR2(200);
  MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;
BEGIN

IF PCK_GENERALES.FC_CONS_CAMBIONIT()=0 THEN --VALIDACION PARA CUANDO SE ESTA CAMBIANDO EL NIT AL TERCERO
  
  --VALIDAR QUE NO ESTE CERRADO EL DÍA DEL COMPROBANTE
  MI_RTA_OLD:='A';
  MI_RTA_NEW:='A';
  MI_RTA    :='A';
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
                 UN_EXC_COD => SQLCODE,
                 UN_TABLAERROR=>'DETALLE_COMPROBANTE_CNT',
                 UN_ERROR_COD => PCK_ERRORES.ERROR_GRAL_DIACERRADO,
                 UN_REEMPLAZOS => MI_MSGERROR
               );
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

        IF PCK_CONTABILIDAD.GL_ACT_FECHA_VENC <> 0 THEN --JM CC 2574
            MI_RTA_OLD := 'A'; 
            MI_RTA_NEW := 'A';
        END IF;

  END IF;
  IF MI_RTA_OLD<>'A' OR MI_RTA_NEW<>'A' THEN
		IF (:NEW.COMPANIA                           <>:OLD.COMPANIA
     OR :NEW.ANO                                <>:OLD.ANO
     OR :NEW.TIPO                               <>:OLD.TIPO
     OR :NEW.NUMERO                             <>:OLD.NUMERO
     OR :NEW.TERCERO                            <>:OLD.TERCERO
     OR :NEW.SUCURSAL                           <>:OLD.SUCURSAL
     OR :NEW.CENTRO_COSTO                       <>:OLD.CENTRO_COSTO
     OR :NEW.AUXILIAR                           <>:OLD.AUXILIAR
     OR :NEW.REFERENCIA                         <>:OLD.REFERENCIA
     OR :NEW.FUENTE_RECURSO                     <>:OLD.FUENTE_RECURSO
     OR :NEW.VLR_BASE                           <>:OLD.VLR_BASE
     OR :NEW.TEXTO                              <>:OLD.TEXTO
     OR :NEW.NRO_DOCUMENTO                      <>:OLD.NRO_DOCUMENTO
     OR :NEW.VLR_DOCUMENTO                      <>:OLD.VLR_DOCUMENTO
     OR :NEW.DEBITO                             <>:OLD.DEBITO
     OR :NEW.CREDITO                            <>:OLD.CREDITO
     OR :NEW.CUENTA                             <>:OLD.CUENTA
     OR :NEW.ENTREGADO                          <>:OLD.ENTREGADO
     OR :NEW.PAGADOBANCO                        <>:OLD.PAGADOBANCO
     OR :NEW.VLR_BASEIVA                        <>:OLD.VLR_BASEIVA
     OR :NEW.VLRAGIRAR                          <>:OLD.VLRAGIRAR
     OR :NEW.REGISTRO                           <>:OLD.REGISTRO
     OR :NEW.TIPODEPRESUPUESTO                  <>:OLD.TIPODEPRESUPUESTO
     OR :NEW.TIPOCONTRATO                       <>:OLD.TIPOCONTRATO
     OR :NEW.NUMEROCONTRATO                     <>:OLD.NUMEROCONTRATO
     OR :NEW.TASADECAMBIO                       <>:OLD.TASADECAMBIO
     OR :NEW.BANCO                              <>:OLD.BANCO
     OR :NEW.CUENTABANCO                        <>:OLD.CUENTABANCO
     OR :NEW.PORCIVA                            <>:OLD.PORCIVA
     OR :NEW.ORDENADOR                          <>:OLD.ORDENADOR
     OR :NEW.ORDENADORSUCURSAL                  <>:OLD.ORDENADORSUCURSAL
     OR :NEW.PAGOENPLANO                        <>:OLD.PAGOENPLANO
     OR :NEW.ENVIADO                            <>:OLD.ENVIADO
     OR :NEW.PAGOAPODERADO                      <>:OLD.PAGOAPODERADO
     OR :NEW.TIPOPAGO                           <>:OLD.TIPOPAGO
     OR :NEW.TERCERO_NSESION                    <>:OLD.TERCERO_NSESION
     OR :NEW.SUCURSAL_NSESION                   <>:OLD.SUCURSAL_NSESION
     OR :NEW.PAGO_EFECTIVO                      <>:OLD.PAGO_EFECTIVO
     OR :NEW.PROGRAMADO                         <>:OLD.PROGRAMADO
     OR :NEW.PAGADOGN                           <>:OLD.PAGADOGN
     OR :NEW.CEDULAINHUMACION                   <>:OLD.CEDULAINHUMACION
     OR :NEW.CONSECUTIVOWF                      <>:OLD.CONSECUTIVOWF
     OR :NEW.ABONOCXP                           <>:OLD.ABONOCXP
     OR :NEW.GIROELECTRONICO                    <>:OLD.GIROELECTRONICO
     OR :NEW.TIPOPAGO_SIA                       <>:OLD.TIPOPAGO_SIA
     OR :NEW.LEY1450                            <>:OLD.LEY1450
     OR :NEW.COD_PROYECTO                       <>:OLD.COD_PROYECTO
     OR :NEW.CONSFACTURA                        <>:OLD.CONSFACTURA
     OR :NEW.CONTRIBUYENTE                      <>:OLD.CONTRIBUYENTE
     OR :NEW.MODIFICADOR_CH                     <>:OLD.MODIFICADOR_CH
     OR :NEW.DEPENDENCIACNT                     <>:OLD.DEPENDENCIACNT
     OR :NEW.CONCEPTO_SF                        <>:OLD.CONCEPTO_SF
     OR :NEW.IND_ANTICIPO                       <>:OLD.IND_ANTICIPO
     OR :NEW.NONIIF                             <>:OLD.NONIIF
     OR :NEW.VALORPRESENTE                      <>:OLD.VALORPRESENTE
     OR :NEW.REC_DETERIORO                      <>:OLD.REC_DETERIORO
     OR :NEW.CODIGO_NIIF                        <>:OLD.CODIGO_NIIF
     OR :NEW.FECHARECEPCION                     <>:OLD.FECHARECEPCION
     OR :NEW.FECHA_CALC_DET                     <>:OLD.FECHA_CALC_DET
     OR :NEW.FECHA_MODIFICADOR_CH               <>:OLD.FECHA_MODIFICADOR_CH
     OR :NEW.FECHA_ABONO                        <>:OLD.FECHA_ABONO
     OR :NEW.FECHAINHUMACION                    <>:OLD.FECHAINHUMACION
     OR :NEW.FECHAPAGADOGN                      <>:OLD.FECHAPAGADOGN
     OR :NEW.FECHAPROGPAGO                      <>:OLD.FECHAPROGPAGO
     OR :NEW.HORA                               <>:OLD.HORA
     OR :NEW.FECHA_VCN_DOC                      <>:OLD.FECHA_VCN_DOC
     OR :NEW.FECHA                              <>:OLD.FECHA
     OR :NEW.NOMBRE_PROYECTO                    <>:OLD.NOMBRE_PROYECTO
    ) THEN
    /*
       Los campos excluido por efecto de las afectaciones y conciliaciones son
       :NEW.DEBITOSAFECTADOS                  
       :NEW.CREDITOSAFECTADOS             
       :NEW.DEBITOSAFECTADOS_CXP          
       :NEW.CREDITOSAFECTADOS_CXP          
       :NEW.CODI_SIA   
       :NEW.IMPRESO
       
       Se excluyen por efectos de las anulaciones
         :NEW.ANULADO            
         :NEW.DESCRIPCION  
         
      Se excluye para ejecutar proceso de mantenimiento de revisar cuentas por pagar
       :NEW.ABONADO
       :NEW.FECHA_ABONO
       :NEW.USUARIO_ABONO
      */
    DECLARE
        MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR; 
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;           
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
        MI_MSGERROR(1).CLAVE := 'FECHA';
        MI_MSGERROR(1).VALOR := TO_CHAR(:NEW.FECHA,'DD/MM/YYYY');
        PCK_ERR_MSG.RAISE_WITH_MSG(
           UN_EXC_COD => SQLCODE,
           UN_TABLAERROR=>'DETALLE_COMPROBANTE_CNT',
           UN_ERROR_COD => PCK_ERRORES.ERROR_GRAL_DIACERRADO,
           UN_REEMPLAZOS => MI_MSGERROR
           );
      END; 
    END IF;
  END IF;
  
  IF DELETING THEN
    PCK_CONTABILIDAD6.PR_ELIMINAR_CNTBANCOS(
                                            UN_COMPANIA           => :OLD.COMPANIA,
                                            UN_ANO                => :OLD.ANO,
                                            UN_TIPO_CPTE          => :OLD.TIPO,
                                            UN_COMPROBANTE        => :OLD.NUMERO);

    -- CC:3275 -  NCARDENAS - FECHA:5/01/2026                                         
        DECLARE
      VALOR NUMBER;
      NUMERO_COMP NUMBER;
      MI_TIPO COMPROBANTE_CNT.TIPO%TYPE;
      CONSECUTIVO NUMBER;
    BEGIN    
        
        BEGIN
        SELECT CMTR.CONSECUTIVO_RIPS
            INTO CONSECUTIVO
        FROM 
            CM_CAUSACION_AUTOMATICA CMCA
        JOIN 
            CM_ARCHIVO_TRANSACCIONES CMTR
        ON CMCA.COMPANIA = CMTR.COMPANIA_COMPROBANTE
        AND CMCA.TIPO_COMPROBANTE = CMTR.TIPO_COMPROBANTE 
        JOIN 
            COMPROBANTE_CNT HCNT
        ON  CMTR.COMPANIA_COMPROBANTE = HCNT.COMPANIA 
        AND CMTR.TIPO_COMPROBANTE = HCNT.NUMERO
        AND CMTR.NUMERO_COMPROBANTE = HCNT.TIPO
        AND TO_CHAR(CMTR.CAUSADO_FECHA,'YYYY') = HCNT.ANO
        WHERE 
            CMTR.COMPANIA_COMPROBANTE = :OLD.COMPANIA  
            AND CMCA.TIPO_COMPROBANTE= :OLD.TIPO
            AND CMTR.NUMERO_COMPROBANTE =  :OLD.NUMERO
            GROUP BY CMTR.CONSECUTIVO_RIPS;

        EXCEPTION WHEN OTHERS THEN
            CONSECUTIVO := NULL;
        END;

      
        IF CONSECUTIVO IS NOT NULL THEN

            BEGIN
                MI_TABLA     := 'CM_ARCHIVO_TRANSACCIONES';
                MI_CAMPOS    := 'NUMERO_COMPROBANTE = 0,
                                  CAUSADO_FECHA = NULL, 
                                  CAUSADO = 0, 
                                  CAUSADO_POR = NULL';
                MI_CONDICION := 'COMPANIA          = ''' || :OLD.COMPANIA || '''
                                 AND TIPO_COMPROBANTE = ''' || MI_TIPO ||'''
                                 AND NUMERO_COMPROBANTE = ' || :OLD.NUMERO ||'';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA
                                                      ,UN_ACCION    => 'M'
                                                      ,UN_CAMPOS    => MI_CAMPOS
                                                      ,UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;
       
            BEGIN
                MI_TABLA     := 'CM_IMPORTARRIPS';
                MI_CAMPOS    := 'ESTADO = ''C'' ';
                MI_CONDICION := 'COMPANIA          = ''' || :OLD.COMPANIA || '''
                            AND CONSECUTIVO = ' || CONSECUTIVO ||' ';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA
                                                      ,UN_ACCION    => 'M'
                                                      ,UN_CAMPOS    => MI_CAMPOS
                                                      ,UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
             RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;
        END IF;
     END;
  -- FIN CC:3275   
  END IF; 
  
  END IF;
END;