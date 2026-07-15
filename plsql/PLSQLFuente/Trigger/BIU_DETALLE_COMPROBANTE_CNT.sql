create or replace TRIGGER "BIU_DETALLE_COMPROBANTE_CNT"  
/*
      NAME              : BIU_DETALLE_COMPROBANTE_CNT
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : 
      DATE MIGRADOR     : 
      TIME              : 
      SOURCE MODULE     : 
      MODIFIER          : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MODIFIED     : 31/01/2017
      TIME              : 09:20 AM
      DESCRIPTION       : SE AJUSTA AL ESTANDAR        
      MODIFIER          : Carlos Alberto Manrique Palacios
      DATE MODIFIED     : 06/02/2017
      TIME              : 05:11 PM
      DESCRIPTION       : Se modifica este trigger al eliminar las columnas vituales de la base de datos        
*/ 
BEFORE INSERT OR UPDATE OF  COMPANIA
                           , ANO
                           , TIPO_CPTE
                           , COMPROBANTE
                           , CUENTA
                           , AUXILIAR
                           , TERCERO
                           , REFERENCIA
                           , CENTRO_COSTO
                           , FUENTE_RECURSO
                           , SUCURSAL
                           , FECHA
                           , AUXILIARI
                           , TERCEROI
                           , REFERENCIAI
                           , CENTRO_COSTOI
                           , DIA
                           , FUENTE_RECURSOI
                           , SUCURSALI
                           , MES
                           , NATURALEZA
                           , ID
                           , VALOR_DEBITO
                           , DEBITO_AFECTADO
ON DETALLE_COMPROBANTE_CNT 
REFERENCING OLD AS ANT NEW AS NEW 
FOR EACH ROW 
DECLARE  

  MI_PAIS         VARCHAR2(3);
  MI_DEPARTAMENTO VARCHAR2(3);
  MI_CIUDAD       VARCHAR2(3);
  MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR; 
  MI_SALDOAFECTAR PCK_SUBTIPOS.TI_DOBLE;
  MI_CAMBIAR_FECHA VARCHAR2(2);
  MI_FECHA_HEADER DATE;
    --7719472_FACTGENERAL(15/09/2022 mrosero)
  MI_PARMAM1 VARCHAR2(2);
  MI_PARMAM2 VARCHAR2(2);

  --7713468_FACTGENERAL(30/09/2022 mperez)
  MI_CLASETIPOCOMPROBANTE   TIPO_COMPROBANTE.CLASE_CONTABLE%TYPE;
  MI_CLASECUENTA            PLAN_CONTABLE.CLASECUENTA%TYPE;
  
BEGIN
IF PCK_GENERALES.FC_CONS_ELIM_COMPROBANTE()=0 THEN --VALIDACION PARA CUANDO SE ESTÁ ELIMINANDO COMPROBANTE PPTAL TICKET 7734212 (19/07/2023 lvega)
 --INI_7719472_FACTGENERAL(15/09/2022 mrosero)
    MI_PARMAM1 := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => :NEW.COMPANIA,
                                    UN_NOMBRE=>'SF PERMITA CONFIGURAR PASIVOS EN RECAUDO',
                                    UN_MODULO=>1,UN_FECHA_PAR=>SYSDATE,UN_IND_MAYUS=>-1);
                                                
     MI_PARMAM2 := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => :NEW.COMPANIA,
                                    UN_NOMBRE=>'SF CONFIGURAR CLASE CUENTA RECAUDO PASIVOS',
                                    UN_MODULO=>1,UN_FECHA_PAR=>SYSDATE,UN_IND_MAYUS=>-1); 

    --7713468_FACTGENERAL(30/09/2022 mperez)
        /*Se obtiene la clase del tipo de comprobante*/
        SELECT CLASE_CONTABLE
          INTO MI_CLASETIPOCOMPROBANTE
          FROM TIPO_COMPROBANTE 
         WHERE CODIGO = :NEW.TIPO_CPTE 
           AND COMPANIA= :NEW.COMPANIA;
        
        /*Se obtiene la clase de la cuenta*/
        SELECT CLASECUENTA
          INTO MI_CLASECUENTA
          FROM PLAN_CONTABLE
         WHERE COMPANIA  = :NEW.COMPANIA
           AND ANO       = :NEW.ANO
           AND CODIGO    = :NEW.CUENTA;
    END IF;
    IF PCK_GENERALES.FC_CONS_CAMBIONIT() IN (0) THEN --VALIDACION PARA CUANDO SE ESTA CAMBIANDO EL NIT AL TERCERO
        :NEW.NATURALEZA:= PCK_CONTABILIDAD.FC_VALIDARCUENTAUTILIZAR(UN_COMPANIA   =>:NEW.COMPANIA,
                                                                    UN_ANO        =>:NEW.ANO,
                                                                    UN_CUENTA     =>:NEW.CUENTA,
                                                                    UN_VALIDABLOQUEADO =>-1);


        --VALIDA QUE LOS AFECTADOS NO SOBREPASEN LOS DEBITOS Y CREDITOS
        IF :NEW.CREDITO_AFECTADO<>0 OR :NEW.DEBITO_AFECTADO<>0 THEN 
            MI_SALDOAFECTAR :=CASE WHEN :NEW.NATURALEZA = 'D' AND :NEW.VALOR_CREDITO = 0
                                   THEN :NEW.VALOR_DEBITO  - :NEW.VALOR_CREDITO + :NEW.DEBITO_AFECTADO  - :NEW.CREDITO_AFECTADO
                                   WHEN :NEW.NATURALEZA = 'D' AND MI_CLASETIPOCOMPROBANTE = 'V' AND MI_CLASECUENTA = 'C' AND :NEW.VALOR_CREDITO > 0
                                   THEN :NEW.VALOR_CREDITO - :NEW.VALOR_DEBITO  + :NEW.CREDITO_AFECTADO - :NEW.DEBITO_AFECTADO
                                   WHEN :NEW.NATURALEZA = 'C' AND MI_CLASETIPOCOMPROBANTE = 'V' AND (MI_CLASECUENTA = 'N' OR MI_CLASECUENTA = 'C') AND :NEW.VALOR_DEBITO > 0
                                   THEN :NEW.VALOR_DEBITO  - :NEW.VALOR_CREDITO + :NEW.DEBITO_AFECTADO  - :NEW.CREDITO_AFECTADO
                                   WHEN :NEW.NATURALEZA = 'C'  AND MI_PARMAM1 = 'SI' AND MI_PARMAM2 = MI_CLASECUENTA 
                                   THEN :NEW.VALOR_DEBITO  - :NEW.VALOR_CREDITO + :NEW.DEBITO_AFECTADO  - :NEW.CREDITO_AFECTADO
                                   ELSE :NEW.VALOR_CREDITO - :NEW.VALOR_DEBITO  + :NEW.CREDITO_AFECTADO - :NEW.DEBITO_AFECTADO
                              END;
            IF MI_SALDOAFECTAR < 0 THEN
                DECLARE
                    MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR; 
                BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;           
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                  MI_MSGERROR(1).CLAVE := 'TIPO';
                  MI_MSGERROR(1).VALOR := :NEW.TIPO_CPTE;
                  MI_MSGERROR(2).CLAVE := 'COMPROBANTE';
                  MI_MSGERROR(2).VALOR := :NEW.COMPROBANTE;
                  MI_MSGERROR(3).CLAVE := 'CUENTA';
                  MI_MSGERROR(3).VALOR := :NEW.CUENTA;
                  MI_MSGERROR(4).CLAVE := 'CONSECUTIVO';
                  MI_MSGERROR(4).VALOR := :NEW.CONSECUTIVO;
                  MI_MSGERROR(5).CLAVE := 'SALDO';
                  MI_MSGERROR(5).VALOR := TRIM(TO_CHAR(MI_SALDOAFECTAR,'999,999,999,999,999,999.99'));
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                         UN_EXC_COD => SQLCODE,
                         UN_TABLAERROR=>'DETALLE_COMPROBANTE_CNT',
                         UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_AFECTADONEGATIVO,
                         UN_REEMPLAZOS => MI_MSGERROR
                       );
                END;  
            END IF;
        END IF; 
        /*21/11/2017 JP Se comenta la validacion PCK_CONTABILIDAD.PR_VALIDARFECHAHEADERCNT
                    debido a que si cambian la fecha en el header este trigger se dispara y envia 
                    el error.
                    Se decide que siempre tome la fecha desde el comprobante y actualice el detalle
    
        */
        
        /*12/01/22 - (vmolano-dramirez) Se ajusta para que permita por parametro editar la fecha de los detalles si y solo si es dentro del mismo mes*/
        BEGIN
        SELECT  FECHA
          INTO MI_FECHA_HEADER
        FROM  COMPROBANTE_CNT   
        WHERE COMPANIA = :NEW.COMPANIA
          AND ANO      = :NEW.ANO
          AND TIPO     = :NEW.TIPO_CPTE
          AND NUMERO   = :NEW.COMPROBANTE;
          EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_FECHA_HEADER := :NEW.FECHA;       
        END;

        MI_CAMBIAR_FECHA := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => :NEW.COMPANIA,UN_NOMBRE=>'PERMITE CAMBIAR FECHA EN DETALLES COMPROBANTE',UN_MODULO=>1,UN_FECHA_PAR=>SYSDATE,UN_IND_MAYUS=>-1);
        
        IF MI_CAMBIAR_FECHA = 'NO' OR EXTRACT(MONTH FROM :NEW.FECHA) <> EXTRACT(MONTH FROM MI_FECHA_HEADER) THEN
            :NEW.FECHA := MI_FECHA_HEADER;
        END IF;
        
        /*PCK_CONTABILIDAD.PR_VALIDARFECHAHEADERCNT(UN_COMPANIA   =>:NEW.COMPANIA,
                                                UN_ANO        =>:NEW.ANO,
                                                UN_TIPO       =>:NEW.TIPO_CPTE,
                                                UN_NUMERO     =>:NEW.COMPROBANTE,
                                                UN_FECHA      =>:NEW.FECHA);
        */
      END IF;
      IF :NEW.PAIS IS NULL 
        OR :NEW.DEPARTAMENTO IS NULL 
        OR :NEW.CIUDAD IS NULL THEN
          SELECT  PAIS
                  ,DEPARTAMENTO
                  ,CIUDAD
            INTO  MI_PAIS
                  ,MI_DEPARTAMENTO
                  ,MI_CIUDAD
            FROM COMPANIA 
          WHERE CODIGO = :NEW.COMPANIA;
          :NEW.PAIS         := MI_PAIS;
          :NEW.DEPARTAMENTO := MI_DEPARTAMENTO;
          :NEW.CIUDAD       := MI_CIUDAD;
      END IF;    
    IF INSERTING THEN
        :NEW.AUXILIARI       :=PCK_SYSMAN_UTL.FC_GEN_AUXILIARCNT_VIRTUAL(:NEW.COMPANIA,:NEW.ANO,:NEW.CUENTA,:NEW.AUXILIAR,4);
        :NEW.TERCEROI        :=PCK_SYSMAN_UTL.FC_GEN_AUXILIARCNT_VIRTUAL(:NEW.COMPANIA,:NEW.ANO,:NEW.CUENTA,:NEW.TERCERO,2);
        :NEW.REFERENCIAI     :=PCK_SYSMAN_UTL.FC_GEN_AUXILIARCNT_VIRTUAL(:NEW.COMPANIA,:NEW.ANO,:NEW.CUENTA,:NEW.REFERENCIA,5);
        :NEW.CENTRO_COSTOI   :=PCK_SYSMAN_UTL.FC_GEN_AUXILIARCNT_VIRTUAL(:NEW.COMPANIA,:NEW.ANO,:NEW.CUENTA,:NEW.CENTRO_COSTO,1);
        :NEW.FUENTE_RECURSOI :=PCK_SYSMAN_UTL.FC_GEN_AUXILIARCNT_VIRTUAL(:NEW.COMPANIA,:NEW.ANO,:NEW.CUENTA,:NEW.FUENTE_RECURSO,6);
        :NEW.SUCURSALI       :=PCK_SYSMAN_UTL.FC_GEN_AUXILIARCNT_VIRTUAL(:NEW.COMPANIA,:NEW.ANO,:NEW.CUENTA,:NEW.SUCURSAL,3);
    ELSIF UPDATING THEN   
        IF UPDATING('COMPANIA') OR UPDATING('ANO') OR UPDATING('CUENTA') OR  UPDATING('TERCERO') OR UPDATING('SUCURSAL') THEN
          :NEW.TERCEROI  :=PCK_SYSMAN_UTL.FC_GEN_AUXILIARCNT_VIRTUAL(:NEW.COMPANIA,:NEW.ANO,:NEW.CUENTA,:NEW.TERCERO,2);
          :NEW.SUCURSALI :=PCK_SYSMAN_UTL.FC_GEN_AUXILIARCNT_VIRTUAL(:NEW.COMPANIA,:NEW.ANO,:NEW.CUENTA,:NEW.SUCURSAL,3);
        END IF;
        IF UPDATING('COMPANIA') OR UPDATING('ANO') OR UPDATING('CUENTA') OR UPDATING('AUXILIAR') THEN  
          :NEW.AUXILIARI :=PCK_SYSMAN_UTL.FC_GEN_AUXILIARCNT_VIRTUAL(:NEW.COMPANIA,:NEW.ANO,:NEW.CUENTA,:NEW.AUXILIAR,4);
        END IF;
        IF UPDATING('COMPANIA') OR UPDATING('ANO') OR UPDATING('CUENTA') OR  UPDATING('REFERENCIA')THEN
          :NEW.REFERENCIAI :=PCK_SYSMAN_UTL.FC_GEN_AUXILIARCNT_VIRTUAL(:NEW.COMPANIA,:NEW.ANO,:NEW.CUENTA,:NEW.REFERENCIA,5);
        END IF;
        IF UPDATING('COMPANIA') OR UPDATING('ANO') OR UPDATING('CUENTA') OR  UPDATING('CENTRO_COSTO')THEN
          :NEW.CENTRO_COSTOI :=PCK_SYSMAN_UTL.FC_GEN_AUXILIARCNT_VIRTUAL(:NEW.COMPANIA,:NEW.ANO,:NEW.CUENTA,:NEW.CENTRO_COSTO,1);
        END IF;
        IF UPDATING('COMPANIA') OR UPDATING('ANO') OR UPDATING('CUENTA') OR  UPDATING('FUENTE_RECURSO')THEN
          :NEW.FUENTE_RECURSOI :=PCK_SYSMAN_UTL.FC_GEN_AUXILIARCNT_VIRTUAL(:NEW.COMPANIA,:NEW.ANO,:NEW.CUENTA,:NEW.FUENTE_RECURSO,6);
        END IF;
    END IF;
    :NEW.MES:=TO_NUMBER(TO_CHAR(:NEW.FECHA,'MM'));
    :NEW.DIA:=TO_NUMBER(TO_CHAR(:NEW.FECHA,'DD'));
    :NEW.ID:=PCK_SYSMAN_UTL.FC_CODIGO_CNT(:NEW.COMPANIA,:NEW.ANO,:NEW.CUENTA,:NEW.CENTRO_COSTO,:NEW.TERCERO,:NEW.SUCURSAL,:NEW.AUXILIAR,:NEW.REFERENCIA,:NEW.FUENTE_RECURSO);

END;