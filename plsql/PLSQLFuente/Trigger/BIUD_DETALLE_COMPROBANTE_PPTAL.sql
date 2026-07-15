CREATE OR REPLACE TRIGGER "BIUD_DETALLE_COMPROBANTE_PPTAL" 
  /*
    NAME              : BIUD_DETALLE_COMPROBANTE_PPTAL
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : 
    DATE MIGRADOR     : 
    TIME              : 
    MODIFIER          : JUAN CARLOS RODRÍGUEZ AMÉZQUITA
    DATE MODIFIED     : 26/07/2017
    TIME              : 04:09 PM
    DESCRIPTION       : Acciones ejecutadas antes de insertar, actualizar o 
                        eliminar en la tabla DETALLE_COMPROBANTE_PPTAL.
    MODIFICATIONS     : Adición de validaciones para que no permita eliminar el registro 
                        si tiene valores, afectaciones o modificaciones en debito/crédito.
                        Eliminación de PAC dependiendo del tipo de comprobante.
  */ 
  BEFORE
  INSERT OR
  UPDATE OR
  DELETE ON DETALLE_COMPROBANTE_PPTAL FOR EACH ROW 
  DECLARE 
    MI_RTA VARCHAR2(2 CHAR);
    MI_RTA_OLD VARCHAR2(2 CHAR);
    MI_RTA_NEW VARCHAR2(2 CHAR);
  BEGIN
  MI_RTA_OLD:='A';
  MI_RTA_NEW:='A';
    -- Validaciones antes de permitir eliminar el detalle.
  /*  IF DELETING THEN
      DECLARE
        MI_REEMPLAZOS                            PCK_SUBTIPOS.TI_CLAVEVALOR;
      BEGIN
        -- Verificar si tiene valores en Debito/Crédito.
        IF :OLD.VALOR_DEBITO <> 0 THEN
          MI_REEMPLAZOS(1).CLAVE := 'TEXTO';
          MI_REEMPLAZOS(1).VALOR := 'Debito';
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END IF;
        IF :OLD.VALOR_CREDITO <> 0 THEN
          MI_REEMPLAZOS(1).CLAVE := 'TEXTO';
          MI_REEMPLAZOS(1).VALOR := 'Crédito';
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END IF;
        -- Verificar valores en Debito/Crédito Afectado.
        IF :OLD.DEBITO_AFECTADO <> 0 THEN
          MI_REEMPLAZOS(1).CLAVE := 'TEXTO';
          MI_REEMPLAZOS(1).VALOR := 'Debito Afectado';
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END IF;
        IF :OLD.CREDITO_AFECTADO <> 0 THEN
          MI_REEMPLAZOS(1).CLAVE := 'TEXTO';
          MI_REEMPLAZOS(1).VALOR := 'Crédito Afectado';
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END IF;
        -- Verificar valores en Modificación Debito/Crédito.
        IF :OLD.MODIFICACION_DEBITO <> 0 THEN
          MI_REEMPLAZOS(1).CLAVE := 'TEXTO';
          MI_REEMPLAZOS(1).VALOR := 'Modificación Debito';
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END IF;
        IF :OLD.MODIFICACION_CREDITO <> 0 THEN
          MI_REEMPLAZOS(1).CLAVE := 'TEXTO';
          MI_REEMPLAZOS(1).VALOR := 'Modificación Crédito';
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END IF;
      EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE
          , UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ELIMINAR_DETALLE
          , UN_REEMPLAZOS => MI_REEMPLAZOS);
       END;
      
    END IF;*/
    --VALIDAR QUE NO ESTE CERRADO EL DÍA DEL COMPROBANTE
    IF DELETING  THEN
      MI_RTA    := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADODIA(
        UN_COMPANIA => :OLD.COMPANIA
      , UN_ANO => TO_NUMBER(TO_CHAR(:OLD.FECHA,'YYYY'))
      , UN_MES => TO_NUMBER(TO_CHAR(:OLD.FECHA,'MM'))
      , UN_DIA => TO_NUMBER(TO_CHAR(:OLD.FECHA,'DD'))
      , UN_MODULO => 3
      , UN_PROCESO => 1);
      IF MI_RTA <> 'A' THEN
        DECLARE
          MI_MSGERROR PCK_SUBTIPOS.TI_CLAVEVALOR;
        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
          MI_MSGERROR(1).CLAVE := 'FECHA';
          MI_MSGERROR(1).VALOR := TO_CHAR(:NEW.FECHA,'DD/MM/YYYY');
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD => SQLCODE
          , UN_TABLAERROR=>'DETALLE_COMPROBANTE_PPTAL'
          , UN_ERROR_COD => PCK_ERRORES.ERROR_GRAL_DIACERRADO
          , UN_REEMPLAZOS => MI_MSGERROR );
        END;
      END IF;
    END IF;
    -- Validaciones antes de permitir insertar el detalle.
    IF INSERTING THEN
      MI_RTA    := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADODIA(
        UN_COMPANIA => :NEW.COMPANIA
      , UN_ANO => TO_NUMBER(TO_CHAR(:NEW.FECHA,'YYYY'))
      , UN_MES => TO_NUMBER(TO_CHAR(:NEW.FECHA,'MM'))
      , UN_DIA => TO_NUMBER(TO_CHAR(:NEW.FECHA,'DD'))
      , UN_MODULO => 3, UN_PROCESO => 1);
      IF MI_RTA <> 'A' THEN
        DECLARE
          MI_MSGERROR PCK_SUBTIPOS.TI_CLAVEVALOR;
        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
          MI_MSGERROR(1).CLAVE := 'FECHA';
          MI_MSGERROR(1).VALOR := TO_CHAR(:NEW.FECHA,'DD/MM/YYYY');
          PCK_ERR_MSG.RAISE_WITH_MSG( 
            UN_EXC_COD => SQLCODE
          , UN_TABLAERROR=>'DETALLE_COMPROBANTE_PPTAL'
          , UN_ERROR_COD => PCK_ERRORES.ERROR_GRAL_DIACERRADO
          , UN_REEMPLAZOS => MI_MSGERROR );
        END;
      END IF;
    END IF;
	
	
	IF PCK_GENERALES.FC_CONS_CAMBIONIT() IN (0) THEN --VALIDACION PARA CUANDO SE ESTA CAMBIANDO EL NIT AL TERCERO
		
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
		  
		   MI_RTA_NEW    := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADODIA(
			UN_COMPANIA => :NEW.COMPANIA
		  , UN_ANO => TO_NUMBER(TO_CHAR(:NEW.FECHA,'YYYY'))
		  , UN_MES => TO_NUMBER(TO_CHAR(:NEW.FECHA,'MM'))
		  , UN_DIA => TO_NUMBER(TO_CHAR(:NEW.FECHA,'DD'))
		  , UN_MODULO => 3
		  , UN_PROCESO => 1); 
		
		  IF (MI_RTA_NEW <> 'A' OR MI_RTA_OLD<>'A')  THEN
			IF(  :NEW.COMPANIA	 <> :OLD.COMPANIA
				OR :NEW.ANO	 <> :OLD.ANO
				OR :NEW.TIPO_CPTE	 <> :OLD.TIPO_CPTE
				OR :NEW.COMPROBANTE	 <> :OLD.COMPROBANTE
				OR :NEW.CONSECUTIVO	 <> :OLD.CONSECUTIVO
				OR :NEW.CUENTA	 <> :OLD.CUENTA
				OR :NEW.DESCRIPCION	 <> :OLD.DESCRIPCION
				OR :NEW.VALOR_DEBITO	 <> :OLD.VALOR_DEBITO
				OR :NEW.VALOR_CREDITO	 <> :OLD.VALOR_CREDITO
				OR :NEW.DEBITO_AFECTADOCNT	 <> :OLD.DEBITO_AFECTADOCNT
				OR :NEW.CREDITO_AFECTADOCNT	 <> :OLD.CREDITO_AFECTADOCNT
				OR :NEW.TIPO_DOCUMENTO	 <> :OLD.TIPO_DOCUMENTO
				OR :NEW.NRO_DOCUMENTO	 <> :OLD.NRO_DOCUMENTO
				OR :NEW.CENTRO_COSTO	 <> :OLD.CENTRO_COSTO
				OR :NEW.TERCERO	 <> :OLD.TERCERO
				OR :NEW.SUCURSAL	 <> :OLD.SUCURSAL
				OR :NEW.AUXILIAR	 <> :OLD.AUXILIAR
				OR :NEW.REFERENCIA	 <> :OLD.REFERENCIA
				OR :NEW.FUENTE_RECURSO	 <> :OLD.FUENTE_RECURSO
				OR :NEW.ANO_AFECT	 <> :OLD.ANO_AFECT
				OR :NEW.TIPO_CPTE_AFECT	 <> :OLD.TIPO_CPTE_AFECT
				OR :NEW.CMPTE_AFECTADO	 <> :OLD.CMPTE_AFECTADO
				OR :NEW.CONSECUTIVOPPTO	 <> :OLD.CONSECUTIVOPPTO
				OR :NEW.NATURALEZA	 <> :OLD.NATURALEZA
				OR :NEW.PROGRAMARPAC	 <> :OLD.PROGRAMARPAC
				OR :NEW.SALDO	 <> :OLD.SALDO
				OR :NEW.VALORCOMPROMISO	 <> :OLD.VALORCOMPROMISO
				OR :NEW.DISPONIBILIDADNETA	 <> :OLD.DISPONIBILIDADNETA
				OR :NEW.DISACUMULADAS	 <> :OLD.DISACUMULADAS
				OR :NEW.RESACUMULADAS	 <> :OLD.RESACUMULADAS
				OR :NEW.APRDEFINITIVA	 <> :OLD.APRDEFINITIVA
				OR :NEW.TIPOCONTRATO	 <> :OLD.TIPOCONTRATO
				OR :NEW.NUMEROCONTRATO	 <> :OLD.NUMEROCONTRATO
				OR :NEW.CONTRACTUAL	 <> :OLD.CONTRACTUAL
				OR :NEW.PAPELES	 <> :OLD.PAPELES
				OR :NEW.PAC_DISPONIBLE	 <> :OLD.PAC_DISPONIBLE
				OR :NEW.RECONOCIMIENTO	 <> :OLD.RECONOCIMIENTO
				OR :NEW.CONSITUACIONFONDOS	 <> :OLD.CONSITUACIONFONDOS
				OR :NEW.ANO_GENERAMOP	 <> :OLD.ANO_GENERAMOP
				OR :NEW.TIPO_GENERAMOP	 <> :OLD.TIPO_GENERAMOP
				OR :NEW.NRO_GENERAMOP	 <> :OLD.NRO_GENERAMOP
				OR :NEW.PORCENTAJEDISTRIBUIDO	 <> :OLD.PORCENTAJEDISTRIBUIDO
				OR :NEW.CREATED_BY	 <> :OLD.CREATED_BY
				OR :NEW.DATE_CREATED	 <> :OLD.DATE_CREATED
				OR :NEW.HORA	 <> :OLD.HORA
				OR :NEW.AUXILIARI	 <> :OLD.AUXILIARI
				OR :NEW.TERCEROI	 <> :OLD.TERCEROI
				OR :NEW.REFERENCIAI	 <> :OLD.REFERENCIAI
				OR :NEW.CENTRO_COSTOI	 <> :OLD.CENTRO_COSTOI
				OR :NEW.DIA	 <> :OLD.DIA
				OR :NEW.FUENTE_RECURSOI	 <> :OLD.FUENTE_RECURSOI
				OR :NEW.SUCURSALI	 <> :OLD.SUCURSALI
				OR :NEW.ID	 <> :OLD.ID
				OR :NEW.MES	 <> :OLD.MES		) THEN
		  /*
		   Los campos excluido por efecto de las afectaciones y conciliaciones son
			:NEW.DEBITO_AFECTADO	
			:NEW.CREDITO_AFECTADO
			:NEW.MODIFIED_BY	 
			:NEW.DATE_MODIFIED
			:NEW.FECHA
		  */
		  /*
		  YB y RM 05/06/2018
		  Se quitó validacion de NEW.MODIFICACION_DEBITO y NEW.MODIFICACION_CREDITO
		  debido a que estos valores pueden o no pueden ser diferentes, y se se hace esa validacion 
		  esto no permite la actualizacion de los campos de las afectaciones a los comprobantes presupuestales
		  */  
			  DECLARE
				MI_MSGERROR PCK_SUBTIPOS.TI_CLAVEVALOR;
			  BEGIN
				RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
			  EXCEPTION
			  WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
				MI_MSGERROR(1).CLAVE := 'FECHA';
				MI_MSGERROR(1).VALOR := TO_CHAR(:NEW.FECHA,'DD/MM/YYYY');
				PCK_ERR_MSG.RAISE_WITH_MSG( 
				  UN_EXC_COD => SQLCODE
				, UN_TABLAERROR=>'DETALLE_COMPROBANTE_PPTAL'
				, UN_ERROR_COD => PCK_ERRORES.ERROR_GRAL_DIACERRADO
				, UN_REEMPLAZOS => MI_MSGERROR );
			  END;
			END IF;
		  END IF;
		END IF;
	END IF; 
    
    
    /* Acciones que deben ser ejecutadas antes de eliminar un registro del detalle, 
    debido a las dependencia con la tabla DETALLE_COMPROBANTE_PPTAL */
    IF DELETING THEN
      -- Eliminación de PAC dependiendo del tipo de comprobante
      DECLARE
          MI_CONDICION                  PCK_SUBTIPOS.TI_CONDICION;
          MI_RTA                        PCK_SUBTIPOS.TI_RTA_ACME;
          MI_TABLA                      VARCHAR2(30 CHAR);
          MI_REEMPLAZOS                 PCK_SUBTIPOS.TI_CLAVEVALOR;
      BEGIN
        MI_CONDICION := 'COMPANIA        = ''' || :OLD.COMPANIA    || ''' 
                         AND ANO         = '   || :OLD.ANO         || ' 
                         AND TIPO_CPTE   = ''' || :OLD.TIPO_CPTE   || ''' 
                         AND COMPROBANTE = '   || :OLD.COMPROBANTE || '
                         AND CONSECUTIVO = '   || :OLD.CONSECUTIVO;
        -- Limpieza del PAC Programado
        MI_TABLA := 'PACPROGRAMADO';
        MI_RTA := PCK_DATOS.FC_ACME(
                    UN_TABLA     => MI_TABLA
                  , UN_ACCION    => 'E'
                  , UN_CONDICION => MI_CONDICION);
        -- Limpieza del PAC Comprometido
        MI_TABLA := 'PACCOMPROMETIDO';
        MI_RTA := PCK_DATOS.FC_ACME(
                    UN_TABLA     => MI_TABLA
                  , UN_ACCION    => 'E'
                  , UN_CONDICION => MI_CONDICION);
        -- Limpieza del PAC Ejecutado
        MI_TABLA := 'PACEJECUTADO';
        MI_RTA := PCK_DATOS.FC_ACME(
                    UN_TABLA     => MI_TABLA
                  , UN_ACCION    => 'E'
                  , UN_CONDICION => MI_CONDICION);
        -- Limpieza del PAC Tesorería
        MI_CONDICION := 'COMPANIA        = ''' || :OLD.COMPANIA    || ''' 
                         AND ANO         = '   || :OLD.ANO         || ' 
                         AND TIPO        = ''' || :OLD.TIPO_CPTE   || ''' 
                         AND NUMERO      = '   || :OLD.COMPROBANTE || '
                         AND CONSECUTIVO = '   || :OLD.CONSECUTIVO;
        MI_TABLA := 'PACTESORERIA';
        MI_RTA := PCK_DATOS.FC_ACME(
                    UN_TABLA     => MI_TABLA
                  , UN_ACCION    => 'E'
                  , UN_CONDICION => MI_CONDICION);
      EXCEPTION
          WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            MI_REEMPLAZOS(1).CLAVE := 'PAC';
            MI_REEMPLAZOS(1).VALOR := MI_TABLA;
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE
            , UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_LIMPIAR_PAC
            , UN_REEMPLAZOS => MI_REEMPLAZOS
            );
      END;
    END IF;
  END;