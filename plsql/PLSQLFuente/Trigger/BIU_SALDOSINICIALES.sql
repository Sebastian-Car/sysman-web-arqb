CREATE OR REPLACE TRIGGER "BIU_SALDOSINICIALES" 
    /*
      NAME              : BIU_SALDOSINICIALES
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : Carlos Alberto Manrique Palacios
      DATE MIGRADOR     : 07/02/2017
      TIME              : 08:35 PM
      MODIFIER          : Yeisson Alejandro Rojas Ruiz
      DATE MODIFIED     : 24/04/2017
      TIME              : 08:30 AM
      MODIFICATIONS     : Se agrego verificacion de si la cuenta existe o tiene movimiento al momento
                          de actualizar o de insertar.
      DESCRIPTION       : Trigger antes de insertar y actualizar para la tabla SALDOSINICIALES,
                          genera el ID del registro insertado o modificado. 
                          Se crea este trigger al eliminar las columnas vituales de la base de datos
    */
  BEFORE
  INSERT OR
  UPDATE OR DELETE 
  OF COMPANIA,
            ANO,
            CODIGO,
            CENTRO_COSTO,
            TERCERO,
            SUCURSAL,
            AUXILIAR,
            REFERENCIA,
            FUENTE_RECURSO,
            ID,
            SALDOINICIAL,
            DEBITO,
            CREDITO 
      ON SALDOSINICIALES FOR EACH ROW 
  DECLARE  
    MI_NATURALEZA            VARCHAR2(1 CHAR); 
    MI_RTA          VARCHAR2(2 CHAR);
  BEGIN 
    IF PCK_GENERALES.FC_CONS_CAMBIONIT() IN (0) THEN --VALIDACION PARA CUANDO SE ESTA CAMBIANDO EL NIT AL TERCERO
        IF DELETING OR UPDATING THEN
          MI_RTA := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADOANO(UN_COMPANIA   => :OLD.COMPANIA,
                                                          UN_ANO        => :OLD.ANO,
                                                          UN_MODULO     => 1,
                                                          UN_PROCESO    => 1); 
          IF MI_RTA <> 'A' THEN
            DECLARE
              MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR; 
            BEGIN
              RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;           
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                MI_MSGERROR(1).CLAVE := 'ANO';
                MI_MSGERROR(1).VALOR := :OLD.ANO;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                       UN_EXC_COD => SQLCODE,
                       UN_TABLAERROR=>'SALDOSINICIALES',
                       UN_ERROR_COD => PCK_ERRORES.ERROR_GRAL_ANOCERRADO,
                       UN_REEMPLAZOS => MI_MSGERROR
                     );
            END;  
          END IF;
        END IF;
        
        IF INSERTING OR UPDATING THEN
         MI_RTA := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADOANO(UN_COMPANIA   => :NEW.COMPANIA,
                                                          UN_ANO        => :NEW.ANO,
                                                          UN_MODULO     => 1,
                                                          UN_PROCESO    => 1); 
          IF MI_RTA <> 'A' THEN
            DECLARE
              MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR; 
            BEGIN
              RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;           
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                MI_MSGERROR(1).CLAVE := 'ANO';
                MI_MSGERROR(1).VALOR := :NEW.ANO;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                       UN_EXC_COD => SQLCODE,
                       UN_TABLAERROR=>'SALDOSINICIALES',
                       UN_ERROR_COD => PCK_ERRORES.ERROR_GRAL_ANOCERRADO,
                       UN_REEMPLAZOS => MI_MSGERROR
                     );
            END;  
          END IF;
        END IF;
    END IF;
    IF INSERTING OR UPDATING THEN
      :NEW.ID:= PCK_SYSMAN_UTL.FC_CODIGO_CNT(UN_COMPANIA     => :NEW.COMPANIA,
                                             UN_ANO          => :NEW.ANO,
                                             UN_CUENTA       => :NEW.CODIGO,
                                             UN_CENTRO_COSTO => :NEW.CENTRO_COSTO,
                                             UN_TERCERO      => :NEW.TERCERO,
                                             UN_SUCURSAL     => :NEW.SUCURSAL,
                                             UN_AUXILIAR     => :NEW.AUXILIAR,
                                             UN_REFERENCIA   => :NEW.REFERENCIA,
                                             UN_FUENTE       => :NEW.FUENTE_RECURSO);
    IF PCK_GENERALES.FC_CONS_CAMBIONIT() IN (0) THEN                                         
      MI_NATURALEZA:= PCK_CONTABILIDAD.FC_VALIDARCUENTAUTILIZAR(UN_COMPANIA        =>:NEW.COMPANIA,
                                                                UN_ANO             =>:NEW.ANO,
                                                                UN_CUENTA          =>:NEW.CODIGO,
                                                                UN_VALIDABLOQUEADO =>-1);
      IF (MI_NATURALEZA='D' AND :NEW.SALDOINICIAL>0) OR (MI_NATURALEZA='C' AND :NEW.SALDOINICIAL<0) THEN
        :NEW.DEBITO  := ABS(:NEW.SALDOINICIAL);
        :NEW.CREDITO := 0;
      ELSIF (MI_NATURALEZA='C' AND :NEW.SALDOINICIAL>0) OR (MI_NATURALEZA='D' AND :NEW.SALDOINICIAL<0) THEN
        :NEW.DEBITO  := 0;
        :NEW.CREDITO := ABS(:NEW.SALDOINICIAL);
      ELSE
        :NEW.DEBITO  := 0;
        :NEW.CREDITO := 0;
      END IF;
    END IF;
    END IF;
  END;