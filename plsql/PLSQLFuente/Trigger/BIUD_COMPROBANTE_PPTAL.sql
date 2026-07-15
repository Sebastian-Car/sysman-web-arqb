CREATE OR REPLACE TRIGGER "BIUD_COMPROBANTE_PPTAL" 
BEFORE DELETE OR INSERT OR UPDATE ON COMPROBANTE_PPTAL
FOR EACH ROW
DECLARE
  MI_RTA          VARCHAR2(2 CHAR);
  MI_RTA_OLD VARCHAR2(2 CHAR);
  MI_RTA_NEW VARCHAR2(2 CHAR);
BEGIN
  MI_RTA_OLD:='A';
  MI_RTA_NEW:='A';

IF PCK_GENERALES.FC_CONS_CAMBIONIT() IN (0) THEN --VALIDACION PARA CUANDO SE ESTA CAMBIANDO EL NIT AL TERCERO 

  --VALIDAR QUE NO ESTE CERRADO EL DÍA DEL COMPROBANTE

  IF DELETING THEN
    MI_RTA := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADODIA(UN_COMPANIA   => :OLD.COMPANIA,
                                                    UN_ANO        => TO_NUMBER(TO_CHAR(:OLD.FECHA,'YYYY')),
                                                    UN_MES        => TO_NUMBER(TO_CHAR(:OLD.FECHA,'MM')),
                                                    UN_DIA        => TO_NUMBER(TO_CHAR(:OLD.FECHA,'DD')),
                                                    UN_MODULO     => 3,
                                                    UN_PROCESO    => 1);
  ELSIF  INSERTING  THEN
     MI_RTA := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADODIA(UN_COMPANIA   => :NEW.COMPANIA,
                                                    UN_ANO        => TO_NUMBER(TO_CHAR(:NEW.FECHA,'YYYY')),
                                                    UN_MES        => TO_NUMBER(TO_CHAR(:NEW.FECHA,'MM')),
                                                    UN_DIA        => TO_NUMBER(TO_CHAR(:NEW.FECHA,'DD')),
                                                    UN_MODULO     => 3,
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
                 UN_TABLAERROR=>'COMPROBANTE_PPTAL',
                 UN_ERROR_COD => PCK_ERRORES.ERROR_GRAL_DIACERRADO,
                 UN_REEMPLAZOS => MI_MSGERROR
               );
      END;  
    END IF;  
    
   IF DELETING THEN
      PCK_PRESUPUESTO3.PR_MODIFICARNOVEDADPROYECTO(
                                              UN_COMPANIA           => :OLD.COMPANIA,
                                              UN_ANO                => :OLD.ANO,
                                              UN_TIPO               => :OLD.TIPO,
                                              UN_COMPROBANTE        => :OLD.NUMERO,
                                              UN_CLASEORDEN         => :OLD.TIPOCONTRATO,
                                              UN_ORDENDECOMPRA      => :OLD.NUMEROCONTRATO);
    END IF;
 
  IF UPDATING THEN
      
      IF(:NEW.FECHA<>:OLD.FECHA) THEN 
      
        MI_RTA_OLD    := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADODIA(
          UN_COMPANIA => :OLD.COMPANIA
        , UN_ANO => TO_NUMBER(TO_CHAR(:OLD.FECHA,'YYYY'))
        , UN_MES => TO_NUMBER(TO_CHAR(:OLD.FECHA,'MM'))
        , UN_DIA => TO_NUMBER(TO_CHAR(:OLD.FECHA,'DD'))
        , UN_MODULO => 3
        , UN_PROCESO => 1);
      END IF;
  
    MI_RTA_NEW := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADODIA(UN_COMPANIA   => :NEW.COMPANIA,
                                                    UN_ANO        => TO_NUMBER(TO_CHAR(:NEW.FECHA,'YYYY')),
                                                    UN_MES        => TO_NUMBER(TO_CHAR(:NEW.FECHA,'MM')),
                                                    UN_DIA        => TO_NUMBER(TO_CHAR(:NEW.FECHA,'DD')),
                                                    UN_MODULO     => 3,
                                                    UN_PROCESO    => 1); 
    IF MI_RTA_NEW <> 'A' OR MI_RTA_OLD <> 'A' THEN
      IF (  :NEW.COMPANIA	 <> :OLD.COMPANIA
					OR :NEW.ANO	 <> :OLD.ANO
          OR :NEW.TIPO	 <> :OLD.TIPO
					OR :NEW.NUMERO	 <> :OLD.NUMERO
					OR :NEW.TERCERO	 <> :OLD.TERCERO
					OR :NEW.SUCURSAL	 <> :OLD.SUCURSAL
					OR :NEW.CENTRO_COSTO	 <> :OLD.CENTRO_COSTO
					OR :NEW.AUXILIAR	 <> :OLD.AUXILIAR
					OR :NEW.REFERENCIA	 <> :OLD.REFERENCIA
					OR :NEW.FUENTE_RECURSO	 <> :OLD.FUENTE_RECURSO
					OR :NEW.DESCRIPCION	 <> :OLD.DESCRIPCION
					OR :NEW.TEXTO	 <> :OLD.TEXTO
					OR :NEW.NRO_DOCUMENTO	 <> :OLD.NRO_DOCUMENTO
					OR :NEW.VLR_DOCUMENTO	 <> :OLD.VLR_DOCUMENTO
          OR :NEW.DEBITO	 <> :OLD.DEBITO
					OR :NEW.CREDITO	 <> :OLD.CREDITO
					OR :NEW.DEBITO_AFECTADOCNT	 <> :OLD.DEBITO_AFECTADOCNT
					OR :NEW.CREDITO_AFECTADOCNT	 <> :OLD.CREDITO_AFECTADOCNT
					OR :NEW.ABONADO	 <> :OLD.ABONADO
					OR :NEW.ENTREGADO	 <> :OLD.ENTREGADO
					OR :NEW.PAGADOBANCO	 <> :OLD.PAGADOBANCO
					OR :NEW.IMPRESO	 <> :OLD.IMPRESO
					OR :NEW.ANULADO	 <> :OLD.ANULADO
					OR :NEW.CONTRACTUAL	 <> :OLD.CONTRACTUAL
					OR :NEW.DESTINO	 <> :OLD.DESTINO
					OR :NEW.REGISTROAUTOMATICO	 <> :OLD.REGISTROAUTOMATICO
					OR :NEW.CARGO	 <> :OLD.CARGO
					OR :NEW.DEPENDENCIA	 <> :OLD.DEPENDENCIA
					OR :NEW.CODSOLICITANTE	 <> :OLD.CODSOLICITANTE
					OR :NEW.SUCSOLICITANTE	 <> :OLD.SUCSOLICITANTE
					OR :NEW.PAPELES	 <> :OLD.PAPELES
					OR :NEW.TIPOCONTRATO	 <> :OLD.TIPOCONTRATO
					OR :NEW.NUMEROCONTRATO	 <> :OLD.NUMEROCONTRATO
					OR :NEW.SITUACIONFONDOS	 <> :OLD.SITUACIONFONDOS
					OR :NEW.TIPO_DOCUMENTO	 <> :OLD.TIPO_DOCUMENTO
					OR :NEW.ANO_GENERA	 <> :OLD.ANO_GENERA
					OR :NEW.TIPO_GENERA	 <> :OLD.TIPO_GENERA
					OR :NEW.NRO_GENERA	 <> :OLD.NRO_GENERA
					OR :NEW.TIPO_VF	 <> :OLD.TIPO_VF
					OR :NEW.TIPO_AUTORIZACION_VF	 <> :OLD.TIPO_AUTORIZACION_VF
					OR :NEW.NRO_AUTORIZACION_VF	 <> :OLD.NRO_AUTORIZACION_VF
					OR :NEW.COD_PROYECTO_PPTAL	 <> :OLD.COD_PROYECTO_PPTAL
					OR :NEW.LUGAR	 <> :OLD.LUGAR
					OR :NEW.FUENTE_FINANCIACION	 <> :OLD.FUENTE_FINANCIACION
					OR :NEW.ASIGNACION	 <> :OLD.ASIGNACION
					OR :NEW.DANE	 <> :OLD.DANE
					OR :NEW.CREATED_BY	 <> :OLD.CREATED_BY
					OR :NEW.MEN_VIATICOS	 <> :OLD.MEN_VIATICOS
					OR :NEW.INF_MEN	 <> :OLD.INF_MEN
					/*OR :NEW.FECHA_VCN_DOC	 <> :OLD.FECHA_VCN_DOC*/
					OR :NEW.FECHA_APROB_VF	 <> :OLD.FECHA_APROB_VF
					OR :NEW.FECHA_VENCIMIENTO	 <> :OLD.FECHA_VENCIMIENTO
					OR :NEW.DATE_CREATED	 <> :OLD.DATE_CREATED
					OR :NEW.FECHA_AUTO_VF	 <> :OLD.FECHA_AUTO_VF
          OR :NEW.MES	 <> :OLD.MES)THEN    
    /*
       Los campos excluido por efecto de las afectaciones y conciliaciones son
          NEW.DEBITO_AFECTADO	
          NEW.CREDITO_AFECTADO	
          NEW.DATE_MODIFIED	
          NEW.MODIFIED_BY	
          NEW.FECHA
      */          
      /*
      YB y RM 05/06/2018
      Se quitó validacion de NEW.MODIFICACION_DEBITO y NEW.MODIFICACION_CREDITO
      debido a que estos valores pueden o no pueden ser diferentes, y se se hace esa validacion 
      esto no permite la actualizacion de los campos de las afectaciones a los comprobantes presupuestales
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
                 UN_TABLAERROR=>'COMPROBANTE_PPTAL',
                 UN_ERROR_COD => PCK_ERRORES.ERROR_GRAL_DIACERRADO,
                 UN_REEMPLAZOS => MI_MSGERROR
               );
        END;  
      END IF;
    END IF; 
    PCK_PRESUPUESTO3.PR_VALIDATERCEROCOMPPTAL(UN_COMPANIA       => :NEW.COMPANIA,
                                              UN_TIPOMOVIMIENTO => :NEW.TIPO,
                                              UN_TERCERO        => :NEW.TERCERO);
    
  END IF;
END IF;  
END;