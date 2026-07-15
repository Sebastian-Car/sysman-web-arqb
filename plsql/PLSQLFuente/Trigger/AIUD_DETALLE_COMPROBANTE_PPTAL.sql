create or replace TRIGGER "AIUD_DETALLE_COMPROBANTE_PPTAL" 
  /*
    NAME              : AIUD_DETALLE_COMPROBANTE_PPTAL
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : 
    DATE MIGRADOR     : 
    TIME              : 
    DESCRIPTION       : Controla el proceso de realizar un detalle presupuestal.
    MODIFIER          : JAVIER ANDRES RODRIGUEZ RIOS 
    MODIFICATIONS     : Se ajusta al estándar.
    DATE MODIFIED     : 27/01/2017
    TIME              : 03:25 PM
    MODIFIER          : JUAN CARLOS RODRÍGUEZ AMÉZQUITA
    DATE MODIFIED     : 12/07/2017
    TIME              : 10:34 AM
    MODIFICATIONS     : 
    MODIFIER          : 
    DATE MODIFIED     : 08/09/2017
    TIME              : 01:00 PM
    MODIFICATIONS     : 
  */

  
FOR INSERT OR
UPDATE OR
DELETE OF COMPANIA ,
  ANO ,
  MES,
  COMPROBANTE,
  TIPO_CPTE ,
  TIPO_CPTE_AFECT ,
  CMPTE_AFECTADO ,
  CONSECUTIVOPPTO ,
  ANO_AFECT ,
  CUENTA ,
  VALOR_DEBITO ,
  VALOR_CREDITO ,
  DEBITO_AFECTADO ,
  CREDITO_AFECTADO ,
  MODIFICACION_DEBITO ,
  MODIFICACION_CREDITO ,
  NATURALEZA ,
  CONTRACTUAL ,
  PAPELES ,
  TERCERO ,
  CENTRO_COSTO ,
  SUCURSAL ,
  AUXILIAR ,
  REFERENCIA ,
  FUENTE_RECURSO 
ON DETALLE_COMPROBANTE_PPTAL 

COMPOUND TRIGGER 
  MI_RTA                  PCK_SUBTIPOS.TI_STRSQL;
  MI_NETO                 PCK_SUBTIPOS.TI_DOBLE;
  MI_EXISTEDETALLE        NUMBER :=0;
  MI_DIFERENCIA                 PCK_SUBTIPOS.TI_DOBLE;
  MI_STRCLASEMOV                VARCHAR(3,CHAR);
  MI_CONDICION                  PCK_SUBTIPOS.TI_CONDICION;
  MI_TABLA                      VARCHAR2(30 CHAR);
  MI_REEMPLAZOS                 PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;

  TYPE REGISTRO IS RECORD
  (
    MI_COMPANIA          PCK_SUBTIPOS.TI_COMPANIA,
    MI_NATURALEZA        PCK_SUBTIPOS.TI_NATURALEZA,
    MI_FECHA             DATE,
    MI_CONTRACTUAL       PCK_SUBTIPOS.TI_LOGICO,
    MI_PAPELES           PCK_SUBTIPOS.TI_LOGICO,
    MI_ANO               PCK_SUBTIPOS.TI_ANIO,
    MI_MES               PCK_SUBTIPOS.TI_MES,
    MI_TIPO_CPTE         PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL,
    MI_CUENTA            PCK_SUBTIPOS.TI_CODIGOPPTAL,
    MI_CENTRO_COSTO      PCK_SUBTIPOS.TI_CENTRO_COSTO,
    MI_TERCERO           PCK_SUBTIPOS.TI_TERCERO,
    MI_SUCURSAL          PCK_SUBTIPOS.TI_SUCURSAL,
    MI_AUXILIAR          PCK_SUBTIPOS.TI_AUXILIAR,
    MI_REFERENCIA        PCK_SUBTIPOS.TI_REFERENCIA,
    MI_FUENTE_RECURSO    PCK_SUBTIPOS.TI_FUENTE_RECURSOS,
    MI_VALOR_DEBITO      PCK_SUBTIPOS.TI_DOBLE,
    MI_VALOR_CREDITO     PCK_SUBTIPOS.TI_DOBLE,
    MI_DEBITO_AFECTADO   PCK_SUBTIPOS.TI_DOBLE,
    MI_CREDITO_AFECTADO  PCK_SUBTIPOS.TI_DOBLE,
    MI_CONSECUTIVO       PCK_SUBTIPOS.TI_DOBLE,
    MI_COMPROBANTE       PCK_SUBTIPOS.TI_NUMEROCOMPROBANTEPPTAL,
    MI_TIPO_CPTE_AFECT   PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL,
    MI_CMPTE_AFECTADO    PCK_SUBTIPOS.TI_NUMEROCOMPROBANTEPPTAL,
    MI_CONSECUTIVOPPTO   PCK_SUBTIPOS.TI_DOBLE,
    MI_ANO_AFECT         PCK_SUBTIPOS.TI_ANIO,
    MI_MODIFICACION_DEBITO  PCK_SUBTIPOS.TI_DOBLE,
    MI_MODIFICACION_CREDITO PCK_SUBTIPOS.TI_DOBLE,
    MI_DELETEHERRADA        PCK_SUBTIPOS.TI_LOGICO ); 
  TYPE REGISTROS IS TABLE OF REGISTRO INDEX BY BINARY_INTEGER;
  TABLAOLD REGISTROS;
  TABLANEW REGISTROS;
  MI_POS NUMBER(10) DEFAULT 0;
  MI_CAMBIA_VALOR PCK_SUBTIPOS.TI_ENTERO DEFAULT 0; 
  MI_SALDO PCK_SUBTIPOS.TI_DOBLE;
  MI_CLASE        TIPO_COMPROBPP.CLASE%TYPE; 
  MI_RUBRO_ANT        PCK_PRESUPUESTO1.TYP_RUBRO_AUX;
  MI_RUBRO_NUE        PCK_PRESUPUESTO1.TYP_RUBRO_AUX;
  MI_NIT              COMPANIA.NITCOMPANIA%TYPE;
  --MROSERO CC1469
  MI_PARMAM1    	  VARCHAR2(2);
BEFORE EACH ROW IS
  BEGIN
      EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS = ''.,''';
  IF PCK_GENERALES.FC_CONS_CAMBIONIT() IN (0) THEN --VALIDACION PARA CUANDO SE ESTA CAMBIANDO EL NIT AL TERCERO
  --MROSERO CC1469     
   MI_PARMAM1 := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => :NEW.COMPANIA,
                                    UN_NOMBRE=>'MANEJA DETALLE POR TERCERO EN LA IMPUTACION PRESUPUESTAL',
                                    UN_MODULO=>3,UN_FECHA_PAR=>SYSDATE,UN_IND_MAYUS=>-1);
    IF INSERTING  THEN
  --MROSERO CC1469 
    IF MI_PARMAM1 = 'NO' THEN
      IF :NEW.TIPO_CPTE_AFECT    IS NOT NULL
       AND :NEW.CMPTE_AFECTADO   IS NOT NULL
       AND :NEW.CONSECUTIVOPPTO  IS NOT NULL
       AND :NEW.ANO_AFECT        IS NOT NULL THEN
        MI_CLASE := PCK_PRESUPUESTO2.FC_CLASEPPTAL(UN_COMPANIA  => :NEW.COMPANIA
                                                  ,UN_TIPO_CPTE => :NEW.TIPO_CPTE_AFECT); 
        SELECT CASE WHEN MI_CLASE <> 'DIS' THEN TERCERO ELSE :NEW.TERCERO END
                          , CASE WHEN MI_CLASE <> 'DIS' THEN SUCURSAL ELSE :NEW.SUCURSAL END 
        INTO :NEW.TERCERO, 
             :NEW.SUCURSAL 
        FROM COMPROBANTE_PPTAL
        WHERE COMPANIA = :NEW.COMPANIA
          AND ANO      = :NEW.ANO_AFECT
          AND TIPO     = :NEW.TIPO_CPTE_AFECT
          AND NUMERO   = :NEW.CMPTE_AFECTADO;  
         END IF;
      END IF;
    END IF;
   END IF; 
  END BEFORE EACH ROW;
AFTER EACH ROW IS
  BEGIN
    IF PCK_GENERALES.FC_CONS_CAMBIONIT() IN (0) THEN --VALIDACION PARA CUANDO SE ESTA CAMBIANDO EL NIT AL TERCERO   
    MI_SALDO :=0;
    MI_POS := MI_POS+1;    
    TABLAOLD(MI_POS).MI_DELETEHERRADA :=-1;
    --(APINEDA:17/12/2021) Se elimina llamado a la función PCK_PRESUPUESTO1.FC_VERIFICAR_INDICADORES_PPTO dado que asigna el valor 0 a MI_DELETEHERRADA que impide que ingrese a las validaciones de eliminar

    IF UPDATING OR DELETING THEN
      TABLAOLD(MI_POS).MI_COMPANIA             :=:OLD.COMPANIA;
      TABLAOLD(MI_POS).MI_NATURALEZA           :=:OLD.NATURALEZA;
      TABLAOLD(MI_POS).MI_FECHA                :=:OLD.FECHA;
      TABLAOLD(MI_POS).MI_CONTRACTUAL          :=:OLD.CONTRACTUAL;
      TABLAOLD(MI_POS).MI_PAPELES              :=:OLD.PAPELES;
      TABLAOLD(MI_POS).MI_ANO                  :=:OLD.ANO;
      TABLAOLD(MI_POS).MI_MES                  :=:OLD.MES;
      TABLAOLD(MI_POS).MI_TIPO_CPTE            :=:OLD.TIPO_CPTE; 
      TABLAOLD(MI_POS).MI_CUENTA               :=:OLD.CUENTA;
      TABLAOLD(MI_POS).MI_CENTRO_COSTO         :=:OLD.CENTRO_COSTO;
      TABLAOLD(MI_POS).MI_TERCERO              :=:OLD.TERCERO;
      TABLAOLD(MI_POS).MI_SUCURSAL             :=:OLD.SUCURSAL;
      TABLAOLD(MI_POS).MI_AUXILIAR             :=:OLD.AUXILIAR;
      TABLAOLD(MI_POS).MI_REFERENCIA           :=:OLD.REFERENCIA;
      TABLAOLD(MI_POS).MI_FUENTE_RECURSO       :=:OLD.FUENTE_RECURSO;
      TABLAOLD(MI_POS).MI_VALOR_DEBITO         :=:OLD.VALOR_DEBITO;
      TABLAOLD(MI_POS).MI_VALOR_CREDITO        :=:OLD.VALOR_CREDITO;
      TABLAOLD(MI_POS).MI_DEBITO_AFECTADO      :=:OLD.DEBITO_AFECTADO;
      TABLAOLD(MI_POS).MI_CREDITO_AFECTADO     :=:OLD.CREDITO_AFECTADO;
      TABLAOLD(MI_POS).MI_CONSECUTIVO          :=:OLD.CONSECUTIVO;
      TABLAOLD(MI_POS).MI_COMPROBANTE          :=:OLD.COMPROBANTE; 
      TABLAOLD(MI_POS).MI_TIPO_CPTE_AFECT      :=:OLD.TIPO_CPTE_AFECT;
      TABLAOLD(MI_POS).MI_CMPTE_AFECTADO       :=:OLD.CMPTE_AFECTADO;
      TABLAOLD(MI_POS).MI_CONSECUTIVOPPTO      :=:OLD.CONSECUTIVOPPTO;
      TABLAOLD(MI_POS).MI_ANO_AFECT            :=:OLD.ANO_AFECT;
      TABLAOLD(MI_POS).MI_MODIFICACION_DEBITO  :=:OLD.MODIFICACION_DEBITO;
      TABLAOLD(MI_POS).MI_MODIFICACION_CREDITO :=:OLD.MODIFICACION_CREDITO;
    END IF;
    IF INSERTING OR UPDATING THEN
      TABLANEW(MI_POS).MI_COMPANIA             :=:NEW.COMPANIA;
      TABLANEW(MI_POS).MI_NATURALEZA           :=:NEW.NATURALEZA;
      TABLANEW(MI_POS).MI_FECHA                :=:NEW.FECHA;
      TABLANEW(MI_POS).MI_CONTRACTUAL          :=:NEW.CONTRACTUAL;
      TABLANEW(MI_POS).MI_PAPELES              :=:NEW.PAPELES;
      TABLANEW(MI_POS).MI_ANO                  :=:NEW.ANO;
      TABLANEW(MI_POS).MI_MES                  :=:NEW.MES;
      TABLANEW(MI_POS).MI_TIPO_CPTE            :=:NEW.TIPO_CPTE; 
      TABLANEW(MI_POS).MI_CUENTA               :=:NEW.CUENTA;
      TABLANEW(MI_POS).MI_CENTRO_COSTO         :=:NEW.CENTRO_COSTO;
      TABLANEW(MI_POS).MI_TERCERO              :=:NEW.TERCERO;
      TABLANEW(MI_POS).MI_SUCURSAL             :=:NEW.SUCURSAL;
      TABLANEW(MI_POS).MI_AUXILIAR             :=:NEW.AUXILIAR;
      TABLANEW(MI_POS).MI_REFERENCIA           :=:NEW.REFERENCIA;
      TABLANEW(MI_POS).MI_FUENTE_RECURSO       :=:NEW.FUENTE_RECURSO;
      TABLANEW(MI_POS).MI_VALOR_DEBITO         :=:NEW.VALOR_DEBITO;
      TABLANEW(MI_POS).MI_VALOR_CREDITO        :=:NEW.VALOR_CREDITO;
      TABLANEW(MI_POS).MI_DEBITO_AFECTADO      :=:NEW.DEBITO_AFECTADO;
      TABLANEW(MI_POS).MI_CREDITO_AFECTADO     :=:NEW.CREDITO_AFECTADO;
      TABLANEW(MI_POS).MI_CONSECUTIVO          :=:NEW.CONSECUTIVO;
      TABLANEW(MI_POS).MI_COMPROBANTE          :=:NEW.COMPROBANTE; 
      TABLANEW(MI_POS).MI_TIPO_CPTE_AFECT      :=:NEW.TIPO_CPTE_AFECT;
      TABLANEW(MI_POS).MI_CMPTE_AFECTADO       :=:NEW.CMPTE_AFECTADO;
      TABLANEW(MI_POS).MI_CONSECUTIVOPPTO      :=:NEW.CONSECUTIVOPPTO;
      TABLANEW(MI_POS).MI_ANO_AFECT            :=:NEW.ANO_AFECT;
      TABLANEW(MI_POS).MI_MODIFICACION_DEBITO  :=:NEW.MODIFICACION_DEBITO;
      TABLANEW(MI_POS).MI_MODIFICACION_CREDITO :=:NEW.MODIFICACION_CREDITO;
    END IF;
    --VALIDA SALDOS DE LA CUENTA
    
    IF UPDATING THEN
      PCK_PRESUPUESTO2.PR_SALDODISPONIBLE(
                                          UN_TIPO_CPTE     => :NEW.TIPO_CPTE,  
                                          UN_COMPANIA      => :NEW.COMPANIA,
                                          UN_ANIO          => :NEW.ANO,
                                          UN_CODIGO        => :NEW.CUENTA,
                                          UN_TERCERO       => :NEW.TERCERO,
                                          UN_SUCURSAL      => :NEW.SUCURSAL,
                                          UN_AUXILIAR      => :NEW.AUXILIAR,
                                          UN_CENTRO        => :NEW.CENTRO_COSTO,
                                          UN_REFERENCIA    => :NEW.REFERENCIA,
                                          UN_FUENTERECURSO => :NEW.FUENTE_RECURSO,
                                          UN_DEBITO        => :NEW.VALOR_DEBITO,
                                          UN_CREDITO       => :NEW.VALOR_CREDITO,                                          
                                          UN_TIPO_CPTE_ANT     => :OLD.TIPO_CPTE,  
                                          UN_COMPANIA_ANT      => :OLD.COMPANIA,
                                          UN_ANIO_ANT          => :OLD.ANO,
                                          UN_CODIGO_ANT        => :OLD.CUENTA,
                                          UN_TERCERO_ANT       => :OLD.TERCERO,
                                          UN_SUCURSAL_ANT      => :OLD.SUCURSAL,
                                          UN_AUXILIAR_ANT      => :OLD.AUXILIAR,
                                          UN_CENTRO_ANT        => :OLD.CENTRO_COSTO,
                                          UN_REFERENCIA_ANT    => :OLD.REFERENCIA,
                                          UN_FUENTERECURSO_ANT => :OLD.FUENTE_RECURSO,
                                          UN_DEBITO_ANT        => :OLD.VALOR_DEBITO,
                                          UN_CREDITO_ANT       => :OLD.VALOR_CREDITO
                                        ); 
/*
                                        IF :NEW.NRO_DOCUMENTO IS NOT NULL THEN--(INI_CFBARRERA:CC_2605_Cuando se disminuye el saldo del rubro de la disponibilidad desmarque el check Afectado de la solicitud) 
                                            IF NVL(:NEW.VALOR_DEBITO, 0) < NVL(:OLD.VALOR_DEBITO, 0) THEN
                                              UPDATE SOLICITUDDISPONIBILIDAD 
                                              SET AFECTADO = 0
                                              WHERE NUMERO = :NEW.NRO_DOCUMENTO;
                                            END IF;
                                          END IF;     */                                       
    END IF;
    IF INSERTING THEN
      PCK_PRESUPUESTO2.PR_SALDODISPONIBLE(
                                          UN_TIPO_CPTE     => :NEW.TIPO_CPTE,  
                                          UN_COMPANIA      => :NEW.COMPANIA,
                                          UN_ANIO          => :NEW.ANO,
                                          UN_CODIGO        => :NEW.CUENTA,
                                          UN_TERCERO       => :NEW.TERCERO,
                                          UN_SUCURSAL      => :NEW.SUCURSAL,
                                          UN_AUXILIAR      => :NEW.AUXILIAR,
                                          UN_CENTRO        => :NEW.CENTRO_COSTO,
                                          UN_REFERENCIA    => :NEW.REFERENCIA,
                                          UN_FUENTERECURSO => :NEW.FUENTE_RECURSO,
                                          UN_DEBITO        => :NEW.VALOR_DEBITO,
                                          UN_CREDITO       => :NEW.VALOR_CREDITO,                                          
                                          UN_TIPO_CPTE_ANT     => :NEW.TIPO_CPTE,  
                                          UN_COMPANIA_ANT      => :NEW.COMPANIA,
                                          UN_ANIO_ANT          => :NEW.ANO,
                                          UN_CODIGO_ANT        => :NEW.CUENTA,
                                          UN_TERCERO_ANT       => :NEW.TERCERO,
                                          UN_SUCURSAL_ANT      => :NEW.SUCURSAL,
                                          UN_AUXILIAR_ANT      => :NEW.AUXILIAR,
                                          UN_CENTRO_ANT        => :NEW.CENTRO_COSTO,
                                          UN_REFERENCIA_ANT    => :NEW.REFERENCIA,
                                          UN_FUENTERECURSO_ANT => :NEW.FUENTE_RECURSO,
                                          UN_DEBITO_ANT        => 0,
                                          UN_CREDITO_ANT       => 0
                                        );  
    END IF;
    IF DELETING THEN
      PCK_PRESUPUESTO2.PR_SALDODISPONIBLE(
                                          UN_TIPO_CPTE     => :OLD.TIPO_CPTE,  
                                          UN_COMPANIA      => :OLD.COMPANIA,
                                          UN_ANIO          => :OLD.ANO,
                                          UN_CODIGO        => :OLD.CUENTA,
                                          UN_TERCERO       => :OLD.TERCERO,
                                          UN_SUCURSAL      => :OLD.SUCURSAL,
                                          UN_AUXILIAR      => :OLD.AUXILIAR,
                                          UN_CENTRO        => :OLD.CENTRO_COSTO,
                                          UN_REFERENCIA    => :OLD.REFERENCIA,
                                          UN_FUENTERECURSO => :OLD.FUENTE_RECURSO,
                                          UN_DEBITO        => 0,
                                          UN_CREDITO       => 0,                                          
                                          UN_TIPO_CPTE_ANT     => :OLD.TIPO_CPTE,  
                                          UN_COMPANIA_ANT      => :OLD.COMPANIA,
                                          UN_ANIO_ANT          => :OLD.ANO,
                                          UN_CODIGO_ANT        => :OLD.CUENTA,
                                          UN_TERCERO_ANT       => :OLD.TERCERO,
                                          UN_SUCURSAL_ANT      => :OLD.SUCURSAL,
                                          UN_AUXILIAR_ANT      => :OLD.AUXILIAR,
                                          UN_CENTRO_ANT        => :OLD.CENTRO_COSTO,
                                          UN_REFERENCIA_ANT    => :OLD.REFERENCIA,
                                          UN_FUENTERECURSO_ANT => :OLD.FUENTE_RECURSO,
                                          UN_DEBITO_ANT        => :OLD.VALOR_DEBITO,
                                          UN_CREDITO_ANT       => :OLD.VALOR_CREDITO
                                        ); 
/*
                                         IF :OLD.NRO_DOCUMENTO IS NOT NULL THEN--(INI_CFBARRERA:CC_2605_Cuando se elimine el rubro de la disponibilidad desmarque el check Afectado de la solicitud)   
                                            UPDATE SOLICITUDDISPONIBILIDAD 
                                            SET AFECTADO = 0
                                            WHERE NUMERO = :OLD.NRO_DOCUMENTO;
                                          END IF;   */

    END IF;
  END IF;
  END AFTER EACH ROW;
AFTER STATEMENT IS
  BEGIN
  IF PCK_GENERALES.FC_CONS_CAMBIONIT() IN (0) THEN --VALIDACION PARA CUANDO SE ESTA CAMBIANDO EL NIT AL TERCERO 
    FOR INTI IN 1..MI_POS LOOP         
      IF  UPDATING OR INSERTING THEN
        IF TABLANEW(INTI).MI_DEBITO_AFECTADO     <> 0 OR TABLANEW(INTI).MI_CREDITO_AFECTADO     <> 0  OR
           TABLANEW(INTI).MI_MODIFICACION_DEBITO <> 0 OR TABLANEW(INTI).MI_MODIFICACION_CREDITO <> 0 THEN
          IF TABLANEW(INTI).MI_NATURALEZA='D' THEN
            MI_NETO:=  (TABLANEW(INTI).MI_VALOR_DEBITO        - TABLANEW(INTI).MI_VALOR_CREDITO)
                      -(TABLANEW(INTI).MI_DEBITO_AFECTADO     - TABLANEW(INTI).MI_CREDITO_AFECTADO)
                      +(TABLANEW(INTI).MI_MODIFICACION_DEBITO - TABLANEW(INTI).MI_MODIFICACION_CREDITO);
          ELSE
            MI_NETO:=  (TABLANEW(INTI).MI_VALOR_CREDITO        - TABLANEW(INTI).MI_VALOR_DEBITO)
                      -(TABLANEW(INTI).MI_CREDITO_AFECTADO     - TABLANEW(INTI).MI_DEBITO_AFECTADO)
                      +(TABLANEW(INTI).MI_MODIFICACION_CREDITO - TABLANEW(INTI).MI_MODIFICACION_DEBITO);
          END IF;
          DECLARE
            MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR; 
          BEGIN
            IF MI_NETO<0 THEN
              RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;             
            END IF;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
            MI_MSGERROR(1).CLAVE := 'AFECTADO';
            MI_MSGERROR(1).VALOR := ABS(MI_NETO);
            MI_MSGERROR(2).CLAVE := 'CUENTA';
            MI_MSGERROR(2).VALOR := TABLANEW(INTI).MI_CUENTA;
            MI_MSGERROR(3).CLAVE := 'TIPO';
            MI_MSGERROR(3).VALOR := TABLANEW(INTI).MI_TIPO_CPTE;
            MI_MSGERROR(4).CLAVE := 'NUMERO';
            MI_MSGERROR(4).VALOR := TABLANEW(INTI).MI_COMPROBANTE;
            MI_MSGERROR(5).CLAVE := 'CONSECUTIVO';
            MI_MSGERROR(5).VALOR := TABLANEW(INTI).MI_CONSECUTIVO;
            
            PCK_ERR_MSG.RAISE_WITH_MSG(
                   UN_EXC_COD => SQLCODE,
                   UN_TABLAERROR=>'DETALLE_COMPROBANTE_PPTAL',
                   UN_ERROR_COD => PCK_ERRORES.ERR_PRESUPUESTAL_AFECTACIONES,
                   UN_REEMPLAZOS => MI_MSGERROR
                 );
          END;
        END IF;
      END IF;   
      --IF UPDATING OR DELETING THEN
      IF DELETING AND TABLAOLD(INTI).MI_DELETEHERRADA <>0 THEN
        MI_NETO:= (TABLAOLD(INTI).MI_DEBITO_AFECTADO + TABLAOLD(INTI).MI_CREDITO_AFECTADO);
        DECLARE
          MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
        BEGIN
          IF MI_NETO>0 THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;                  
          END IF;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
          MI_MSGERROR(1).CLAVE := 'AFECTADO';
          MI_MSGERROR(1).VALOR := ABS(MI_NETO);
          MI_MSGERROR(2).CLAVE := 'CUENTA';
          MI_MSGERROR(2).VALOR := TABLANEW(INTI).MI_CUENTA;
          MI_MSGERROR(3).CLAVE := 'TIPO';
          MI_MSGERROR(3).VALOR := TABLANEW(INTI).MI_TIPO_CPTE;
          MI_MSGERROR(4).CLAVE := 'NUMERO';
          MI_MSGERROR(4).VALOR := TABLANEW(INTI).MI_COMPROBANTE;
          MI_MSGERROR(5).CLAVE := 'CONSECUTIVO';
          MI_MSGERROR(5).VALOR := TABLANEW(INTI).MI_CONSECUTIVO;
          PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD => SQLCODE,
                 UN_TABLAERROR=>'DETALLE_COMPROBANTE_PPTAL',
                 UN_ERROR_COD => PCK_ERRORES.ERR_PRESUPUESTAL_AFECTACIONESD,
                 UN_REEMPLAZOS => MI_MSGERROR
             );
        END;
      END IF; 
IF PCK_GENERALES.FC_ACT_AXUILIARES_MANT() IN (0) THEN   
      IF UPDATING THEN
        IF       TABLAOLD(INTI).MI_COMPANIA        <>TABLAOLD(INTI).MI_COMPANIA 
             OR  TABLAOLD(INTI).MI_ANO             <>TABLAOLD(INTI).MI_ANO 
             OR  TABLAOLD(INTI).MI_TIPO_CPTE       <>TABLAOLD(INTI).MI_TIPO_CPTE
             OR  TABLAOLD(INTI).MI_COMPROBANTE     <>TABLAOLD(INTI).MI_COMPROBANTE
             OR  TABLAOLD(INTI).MI_CUENTA          <>TABLAOLD(INTI).MI_CUENTA
             OR  TABLAOLD(INTI).MI_CONSECUTIVO     <>TABLAOLD(INTI).MI_CONSECUTIVO 
        THEN
          BEGIN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;             
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD    => SQLCODE,
                 UN_TABLAERROR => 'DETALLE_COMPROBANTE_PPTAL',
                 UN_ERROR_COD  => PCK_ERRORES.ERR_DETALLE_CAMBIO
               );
          END;           
        END IF;
        
        MI_NETO:= NVL(TABLAOLD(INTI).MI_VALOR_DEBITO,0) + NVL(TABLAOLD(INTI).MI_VALOR_CREDITO,0);
        IF       TABLANEW(INTI).MI_TIPO_CPTE_AFECT  IS NOT NULL
             AND TABLANEW(INTI).MI_CMPTE_AFECTADO   IS NOT NULL
             AND TABLANEW(INTI).MI_CONSECUTIVOPPTO  IS NOT NULL
             AND TABLANEW(INTI).MI_ANO_AFECT        IS NOT NULL THEN
            BEGIN
                SELECT  NITCOMPANIA
                INTO    MI_NIT
                FROM    COMPANIA
                WHERE   CODIGO = TABLANEW(INTI).MI_COMPANIA;
            END;
            IF MI_NIT = '800.244.322-6' OR MI_NIT ='800244322'  THEN
              PCK_PRESUPUESTO_COM4.PR_ACTUALIZAR_SALDOPPTAL_MOV(UN_COMPANIA    => TABLANEW(INTI).MI_COMPANIA 
                                          ,UN_ANIO        => TABLANEW(INTI).MI_ANO
                                          ,UN_TIPO        => TABLANEW(INTI).MI_TIPO_CPTE
                                          ,UN_COMPROBANTE => TABLANEW(INTI).MI_COMPROBANTE
                                          ,UN_CONSECUTIVO => TABLANEW(INTI).MI_CONSECUTIVO
                                          ,UN_FECHA       => TABLANEW(INTI).MI_FECHA
                                          );
            END IF;
            MI_RUBRO_NUE := PCK_PRESUPUESTO1.FC_VERIFICAR_INDICADORES_PPTO(UN_COMPANIA      => TABLANEW(INTI).MI_COMPANIA,
                                                      UN_ANIO          => TABLANEW(INTI).MI_ANO_AFECT,
                                                      UN_MES           => 1,
                                                      UN_CODIGO        => TABLANEW(INTI).MI_CUENTA,
                                                      UN_CENTRO        => TABLANEW(INTI).MI_CENTRO_COSTO ,
                                                      UN_TERCERO       => TABLANEW(INTI).MI_TERCERO,
                                                      UN_SUCURSAL      => TABLANEW(INTI).MI_SUCURSAL,
                                                      UN_AUXILIAR      => TABLANEW(INTI).MI_AUXILIAR ,
                                                      UN_REFERENCIA    => TABLANEW(INTI).MI_REFERENCIA,
                                                      UN_FUENTERECURSO => TABLANEW(INTI).MI_FUENTE_RECURSO); 
            MI_RUBRO_ANT := PCK_PRESUPUESTO1.FC_VERIFICAR_INDICADORES_PPTO(UN_COMPANIA      => TABLAOLD(INTI).MI_COMPANIA,
                                                      UN_ANIO          => TABLAOLD(INTI).MI_ANO,
                                                      UN_MES           => 1,
                                                      UN_CODIGO        => TABLAOLD(INTI).MI_CUENTA,
                                                      UN_CENTRO        => TABLAOLD(INTI).MI_CENTRO_COSTO ,
                                                      UN_TERCERO       => TABLAOLD(INTI).MI_TERCERO,
                                                      UN_SUCURSAL      => TABLAOLD(INTI).MI_SUCURSAL,
                                                      UN_AUXILIAR      => TABLAOLD(INTI).MI_AUXILIAR ,
                                                      UN_REFERENCIA    => TABLAOLD(INTI).MI_REFERENCIA,
                                                      UN_FUENTERECURSO => TABLAOLD(INTI).MI_FUENTE_RECURSO); 
               
          IF       (TABLANEW(INTI).MI_TIPO_CPTE_AFECT<>TABLAOLD(INTI).MI_TIPO_CPTE_AFECT AND MI_NETO<>0)
               OR  (TABLANEW(INTI).MI_CMPTE_AFECTADO <>TABLAOLD(INTI).MI_CMPTE_AFECTADO  AND MI_NETO<>0)
               OR  (TABLANEW(INTI).MI_CONSECUTIVOPPTO<>TABLAOLD(INTI).MI_CONSECUTIVOPPTO AND MI_NETO<>0) 
               OR  (TABLANEW(INTI).MI_ANO_AFECT      <>TABLANEW(INTI).MI_ANO             AND MI_NETO<>0)
               OR  (MI_RUBRO_NUE.MI_CENTROCOSTO      <>MI_RUBRO_ANT.MI_CENTROCOSTO       AND MI_NETO<>0)
               OR  (MI_RUBRO_NUE.MI_TERCERO          <>MI_RUBRO_ANT.MI_TERCERO           AND MI_NETO<>0)
               OR  (MI_RUBRO_NUE.MI_SUCURSAL         <>MI_RUBRO_ANT.MI_SUCURSAL          AND MI_NETO<>0)
               OR  (MI_RUBRO_NUE.MI_AUXILIAR         <>MI_RUBRO_ANT.MI_AUXILIAR          AND MI_NETO<>0)
               OR  (MI_RUBRO_NUE.MI_REFERENCIA       <>MI_RUBRO_ANT.MI_REFERENCIA        AND MI_NETO<>0)
               OR  (MI_RUBRO_NUE.MI_FUENTERECURSO    <>MI_RUBRO_ANT.MI_FUENTERECURSO     AND MI_NETO<>0)
          THEN
        
          BEGIN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;             
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD => SQLCODE,
                 UN_TABLAERROR=>'DETALLE_COMPROBANTE_PPTAL',
                 UN_ERROR_COD => PCK_ERRORES.ERR_DETALLE_CAMBIO_AUX
               );
          END;       
        END IF;
      END IF;       
    END IF;
  END IF;
  END LOOP;
 
  FOR INTI IN 1..MI_POS LOOP
    IF INSERTING  THEN 
       PCK_PRESUPUESTO1.PR_ACTPPTO(UN_TIPO_CPTE    => TABLANEW(INTI).MI_TIPO_CPTE,
                                   UN_COMPANIA     => TABLANEW(INTI).MI_COMPANIA,
                                   UN_FECHA        => TABLANEW(INTI).MI_FECHA, 
                                   UN_CODIGO       => TABLANEW(INTI).MI_CUENTA, 
                                   UN_CENTRO       => TABLANEW(INTI).MI_CENTRO_COSTO, 
                                   UN_TERCERO      => TABLANEW(INTI).MI_TERCERO, 
                                   UN_SUCURSAL     => TABLANEW(INTI).MI_SUCURSAL, 
                                   UN_AUXILIAR     => TABLANEW(INTI).MI_AUXILIAR, 
                                   UN_REFERENCIA   => TABLANEW(INTI).MI_REFERENCIA, 
                                   UN_FUENTE       => TABLANEW(INTI).MI_FUENTE_RECURSO, 
                                   UN_NATURALEZA   => TABLANEW(INTI).MI_NATURALEZA, 
                                   UN_DEBITO_ANT   => 0, 
                                   UN_CREDITO_ANT  => 0, 
                                   UN_DEBITO       => TABLANEW(INTI).MI_VALOR_DEBITO, 
                                   UN_CREDITO      => TABLANEW(INTI).MI_VALOR_CREDITO,
                                   UN_DIFERENCIA   => 0,
                                   UN_DIFERENCIAANT=> 0, 
                                   UN_TIPO         => CASE WHEN TABLANEW(INTI).MI_CONTRACTUAL <> 0 THEN 'C' ELSE 'N' END, 
                                   UN_TIPOINGRESO  => CASE WHEN TABLANEW(INTI).MI_PAPELES     <> 0 THEN 'P' ELSE 'E' END);                           
    END IF;         
    IF  UPDATING THEN

      --Se llama el procedimiento para actualizar los valores del pac despues de que se actualiza el registro ticket 7724476
      --Se restructura el proceso  ya que se estaba presentado caida del sistema  y es por que  la tabla esta mutando Ticket#7728163 — Caida del sistema sysman web autor:cp
      MI_CLASE := PCK_PRESUPUESTO2.FC_CLASEPPTAL(UN_COMPANIA  => TABLANEW(INTI).MI_COMPANIA
                                                ,UN_TIPO_CPTE => TABLANEW(INTI).MI_TIPO_CPTE);

      PCK_PRESUPUESTO1.PR_ACTUALIZAR_PAC(  UN_COMPANIA        => TABLANEW(INTI).MI_COMPANIA
                                         , UN_ANO           => TABLANEW(INTI).MI_ANO
                                         , UN_TIPO_CPTE     => TABLANEW(INTI).MI_TIPO_CPTE
                                         , UN_COMPROBANTE   => TABLANEW(INTI).MI_COMPROBANTE
                                         , UN_CONSECUTIVO   => TABLANEW(INTI).MI_CONSECUTIVO
                                         , UN_VALORDEBITO   => TABLANEW(INTI).MI_VALOR_DEBITO
                                         , UN_VALORCREDITO  => TABLANEW(INTI).MI_VALOR_CREDITO
                                         , UN_MES           => TABLANEW(INTI).MI_MES
                                         , UN_CODIGO        => TABLANEW(INTI).MI_CUENTA
                                         , UN_AUXILIAR      => TABLANEW(INTI).MI_AUXILIAR
                                         , UN_CENTRO        => TABLANEW(INTI).MI_CENTRO_COSTO
                                         , UN_REFERENCIA    => TABLANEW(INTI).MI_REFERENCIA
                                         , UN_FUENTERECURSO => TABLANEW(INTI).MI_FUENTE_RECURSO
                                         , UN_CLASE         => MI_CLASE
                                         , UN_TERCERO       => TABLANEW(INTI).MI_TERCERO
                                         , UN_SUCURSAL      => TABLANEW(INTI).MI_SUCURSAL
                                         , UN_CONTRACTUAL   => TABLANEW(INTI).MI_CONTRACTUAL
                                          ); 

       PCK_PRESUPUESTO1.PR_ACTPPTO(UN_TIPO_CPTE    => TABLAOLD(INTI).MI_TIPO_CPTE,
                                   UN_COMPANIA     => TABLAOLD(INTI).MI_COMPANIA,
                                   UN_FECHA        => TABLAOLD(INTI).MI_FECHA, 
                                   UN_CODIGO       => TABLAOLD(INTI).MI_CUENTA, 
                                   UN_CENTRO       => TABLAOLD(INTI).MI_CENTRO_COSTO, 
                                   UN_TERCERO      => TABLAOLD(INTI).MI_TERCERO, 
                                   UN_SUCURSAL     => TABLAOLD(INTI).MI_SUCURSAL, 
                                   UN_AUXILIAR     => TABLAOLD(INTI).MI_AUXILIAR, 
                                   UN_REFERENCIA   => TABLAOLD(INTI).MI_REFERENCIA, 
                                   UN_FUENTE       => TABLAOLD(INTI).MI_FUENTE_RECURSO, 
                                   UN_NATURALEZA   => TABLAOLD(INTI).MI_NATURALEZA, 
                                   UN_DEBITO_ANT   => TABLAOLD(INTI).MI_VALOR_DEBITO, 
                                   UN_CREDITO_ANT  => TABLAOLD(INTI).MI_VALOR_CREDITO, 
                                   UN_DEBITO       => TABLANEW(INTI).MI_VALOR_DEBITO, 
                                   UN_CREDITO      => TABLANEW(INTI).MI_VALOR_CREDITO,
                                   UN_DIFERENCIA   => 0,
                                   UN_DIFERENCIAANT=> 0,
                                   UN_TIPO         => CASE WHEN TABLANEW(INTI).MI_CONTRACTUAL <> 0 THEN 'C' ELSE 'N' END, 
                                   UN_TIPOINGRESO  => CASE WHEN TABLANEW(INTI).MI_PAPELES     <> 0 THEN 'P' ELSE 'E' END);
    END IF;
    IF DELETING  AND TABLAOLD(INTI).MI_DELETEHERRADA <>0  THEN
       PCK_PRESUPUESTO1.PR_ACTPPTO(UN_TIPO_CPTE    => TABLAOLD(INTI).MI_TIPO_CPTE,
                                   UN_COMPANIA     => TABLAOLD(INTI).MI_COMPANIA,
                                   UN_FECHA        => TABLAOLD(INTI).MI_FECHA, 
                                   UN_CODIGO       => TABLAOLD(INTI).MI_CUENTA, 
                                   UN_CENTRO       => TABLAOLD(INTI).MI_CENTRO_COSTO, 
                                   UN_TERCERO      => TABLAOLD(INTI).MI_TERCERO, 
                                   UN_SUCURSAL     => TABLAOLD(INTI).MI_SUCURSAL, 
                                   UN_AUXILIAR     => TABLAOLD(INTI).MI_AUXILIAR, 
                                   UN_REFERENCIA   => TABLAOLD(INTI).MI_REFERENCIA, 
                                   UN_FUENTE       => TABLAOLD(INTI).MI_FUENTE_RECURSO, 
                                   UN_NATURALEZA   => TABLAOLD(INTI).MI_NATURALEZA, 
                                   UN_DEBITO_ANT   => TABLAOLD(INTI).MI_VALOR_DEBITO, 
                                   UN_CREDITO_ANT  => TABLAOLD(INTI).MI_VALOR_CREDITO, 
                                   UN_DEBITO       => 0, 
                                   UN_CREDITO      => 0,
                                   UN_DIFERENCIA   => 0,
                                   UN_DIFERENCIAANT=> 0,
                                   UN_TIPO         => CASE WHEN TABLAOLD(INTI).MI_CONTRACTUAL <> 0 THEN 'C' ELSE 'N' END, 
                                   UN_TIPOINGRESO  => CASE WHEN TABLAOLD(INTI).MI_PAPELES     <> 0 THEN 'P' ELSE 'E' END);
    END IF;
  END LOOP; 
  
  
  FOR INTI IN 1..MI_POS LOOP
    IF  UPDATING THEN
      IF TABLANEW(INTI).MI_TIPO_CPTE_AFECT IS NOT NULL AND TABLANEW(INTI).MI_TIPO_CPTE_AFECT IS NOT NULL THEN
        PCK_PRESUPUESTO2.PR_AFECTAROTROCOMPROBANTE(UN_COMPANIA      => TABLANEW(INTI).MI_COMPANIA,
                                                   UN_MODULO        => 3, 
                                                   UN_ANO           => TABLANEW(INTI).MI_ANO_AFECT, 
                                                   UN_ANO0          => TABLANEW(INTI).MI_ANO, 
                                                   UN_TIPO0         => TABLANEW(INTI).MI_TIPO_CPTE, 
                                                   UN_TIPO          => TABLANEW(INTI).MI_TIPO_CPTE_AFECT, 
                                                   UN_NUMERO        => TABLANEW(INTI).MI_CMPTE_AFECTADO, 
                                                   UN_CUENTA        => TABLANEW(INTI).MI_CUENTA, 
                                                   UN_CREDITOA      => TABLAOLD(INTI).MI_VALOR_DEBITO ,  
                                                   UN_CONTRACREDITOA=> TABLAOLD(INTI).MI_VALOR_CREDITO ,   
                                                   UN_CREDITO       => TABLANEW(INTI).MI_VALOR_DEBITO ,
                                                   UN_CONTRACREDITO => TABLANEW(INTI).MI_VALOR_CREDITO ,  
                                                   UN_CONSECUTIVO   => TABLANEW(INTI).MI_CONSECUTIVO, 
                                                   UN_CONSECUTIVOPPTO=> TABLANEW(INTI).MI_CONSECUTIVOPPTO, 
                                                   UN_CON           => '0', 
                                                   UN_NUMERO0       => TABLANEW(INTI).MI_COMPROBANTE); 
      END IF;
      IF (TABLANEW(INTI).MI_VALOR_DEBITO - TABLAOLD(INTI).MI_VALOR_DEBITO<>0) OR 
         (TABLANEW(INTI).MI_VALOR_CREDITO - TABLAOLD(INTI).MI_VALOR_CREDITO <>0) THEN
         MI_CAMBIA_VALOR:= -1;
         PCK_PRESUPUESTO1.PR_ACTUALIZAVALORDOCUMENTO(UN_COMPANIA      => TABLANEW(INTI).MI_COMPANIA, 
                                                  UN_ANO           => TABLANEW(INTI).MI_ANO, 
                                                  UN_TIPOMOVIMIENTO=> TABLANEW(INTI).MI_TIPO_CPTE, 
                                                  UN_MOVIMIENTO    => TABLANEW(INTI).MI_COMPROBANTE,
                                                  UN_ACTUALIZA_VALOR => MI_CAMBIA_VALOR ); 
      END IF;
      PCK_PRESUPUESTO1.PR_ACT_VLR_ACTUAL (UN_COMPANIA => TABLANEW(INTI).MI_COMPANIA
                                        ,UN_ANO => TABLANEW(INTI).MI_ANO
                                        ,UN_TIPO_CPTE => TABLANEW(INTI).MI_TIPO_CPTE
                                        ,UN_COMPROBANTE => TABLANEW(INTI).MI_COMPROBANTE
                                        ,UN_CONSECUTIVO => TABLANEW(INTI).MI_CONSECUTIVO);
    ELSIF INSERTING THEN
      IF TABLANEW(INTI).MI_TIPO_CPTE_AFECT IS NOT NULL AND TABLANEW(INTI).MI_TIPO_CPTE_AFECT IS NOT NULL THEN
        PCK_PRESUPUESTO2.PR_AFECTAROTROCOMPROBANTE(UN_COMPANIA      => TABLANEW(INTI).MI_COMPANIA,
                                                   UN_MODULO        => 3, 
                                                   UN_ANO0          => TABLANEW(INTI).MI_ANO, 
                                                   UN_ANO           => TABLANEW(INTI).MI_ANO_AFECT, 
                                                   UN_TIPO0         => TABLANEW(INTI).MI_TIPO_CPTE, 
                                                   UN_TIPO          => TABLANEW(INTI).MI_TIPO_CPTE_AFECT, 
                                                   UN_NUMERO        => TABLANEW(INTI).MI_CMPTE_AFECTADO, 
                                                   UN_CUENTA        => TABLANEW(INTI).MI_CUENTA, 
                                                   UN_CREDITOA      => 0, 
                                                   UN_CONTRACREDITOA=> 0, 
                                                   UN_CREDITO       => TABLANEW(INTI).MI_VALOR_DEBITO, 
                                                   UN_CONTRACREDITO => TABLANEW(INTI).MI_VALOR_CREDITO, 
                                                   UN_CONSECUTIVO   => TABLANEW(INTI).MI_CONSECUTIVO, 
                                                   UN_CONSECUTIVOPPTO=> TABLANEW(INTI).MI_CONSECUTIVOPPTO, 
                                                   UN_CON           => '0', 
                                                   UN_NUMERO0       => TABLANEW(INTI).MI_COMPROBANTE);     
      END IF;
      PCK_PRESUPUESTO1.PR_ACTUALIZAVALORDOCUMENTO(UN_COMPANIA      => TABLANEW(INTI).MI_COMPANIA, 
                                                  UN_ANO           => TABLANEW(INTI).MI_ANO, 
                                                  UN_TIPOMOVIMIENTO=> TABLANEW(INTI).MI_TIPO_CPTE, 
                                                  UN_MOVIMIENTO    => TABLANEW(INTI).MI_COMPROBANTE,
                                                  UN_ACTUALIZA_VALOR => -1 ); 
      PCK_PRESUPUESTO1.PR_ACT_VLR_ACTUAL (UN_COMPANIA => TABLANEW(INTI).MI_COMPANIA
                                        ,UN_ANO => TABLANEW(INTI).MI_ANO
                                        ,UN_TIPO_CPTE => TABLANEW(INTI).MI_TIPO_CPTE
                                        ,UN_COMPROBANTE => TABLANEW(INTI).MI_COMPROBANTE
                                        ,UN_CONSECUTIVO => TABLANEW(INTI).MI_CONSECUTIVO);
    ELSIF DELETING  THEN
      IF    TABLAOLD(INTI).MI_TIPO_CPTE_AFECT IS NOT NULL 
        AND TABLAOLD(INTI).MI_TIPO_CPTE_AFECT IS NOT NULL 
        AND TABLAOLD(INTI).MI_DELETEHERRADA <> 0  THEN
        PCK_PRESUPUESTO2.PR_AFECTAROTROCOMPROBANTE(UN_COMPANIA      => TABLAOLD(INTI).MI_COMPANIA,
                                                   UN_MODULO        => 3, 
                                                   UN_ANO           => TABLAOLD(INTI).MI_ANO_AFECT, 
                                                   UN_ANO0          => TABLAOLD(INTI).MI_ANO, 
                                                   UN_TIPO0         => TABLAOLD(INTI).MI_TIPO_CPTE, 
                                                   UN_TIPO          => TABLAOLD(INTI).MI_TIPO_CPTE_AFECT, 
                                                   UN_NUMERO        => TABLAOLD(INTI).MI_CMPTE_AFECTADO, 
                                                   UN_CUENTA        => TABLAOLD(INTI).MI_CUENTA, 
                                                   UN_CREDITOA      => TABLAOLD(INTI).MI_VALOR_DEBITO,
                                                   UN_CONTRACREDITOA=> TABLAOLD(INTI).MI_VALOR_CREDITO,
                                                   UN_CREDITO       => 0, 
                                                   UN_CONTRACREDITO => 0, 
                                                   UN_CONSECUTIVO   => TABLAOLD(INTI).MI_CONSECUTIVO, 
                                                   UN_CONSECUTIVOPPTO=> TABLAOLD(INTI).MI_CONSECUTIVOPPTO, 
                                                   UN_CON           => '0', 
                                                   UN_NUMERO0       => TABLAOLD(INTI).MI_COMPROBANTE);  
      END IF;
      PCK_PRESUPUESTO1.PR_ACTUALIZAVALORDOCUMENTO(UN_COMPANIA      => TABLAOLD(INTI).MI_COMPANIA, 
                                                  UN_ANO           => TABLAOLD(INTI).MI_ANO, 
                                                  UN_TIPOMOVIMIENTO=> TABLAOLD(INTI).MI_TIPO_CPTE, 
                                                  UN_MOVIMIENTO    => TABLAOLD(INTI).MI_COMPROBANTE,
                                                  UN_ACTUALIZA_VALOR => -1); 
      IF TABLAOLD(INTI).MI_TIPO_CPTE_AFECT IS NOT NULL THEN
        -- Para limpiar los datos de la afectación de la tabla COMPROBANTE_PPTALAFECTADOS
        DECLARE
            MI_CONDICION                  PCK_SUBTIPOS.TI_CONDICION;
            MI_RTA                        PCK_SUBTIPOS.TI_RTA_ACME;
        BEGIN
            MI_CONDICION := 'COMPANIA            = ''' || TABLAOLD(INTI).MI_COMPANIA        || ''' 
                           AND ANO               = '   || TABLAOLD(INTI).MI_ANO             || ' 
                           AND TIPO_CPTE         = ''' || TABLAOLD(INTI).MI_TIPO_CPTE       || ''' 
                           AND COMPROBANTE       = '   || TABLAOLD(INTI).MI_COMPROBANTE     || '
                           AND ANO_AFECT         = '   || TABLAOLD(INTI).MI_ANO_AFECT       || '
                           AND TIPO_CPTE_AFECT   = ''' || TABLAOLD(INTI).MI_TIPO_CPTE_AFECT || '''
                           AND COMPROBANTE_AFECT = '   || TABLAOLD(INTI).MI_CMPTE_AFECTADO;
            MI_RTA := PCK_DATOS.FC_ACME(
                      UN_TABLA     => 'COMPROBANTE_PPTALAFECTADOS'
                    , UN_ACCION    => 'E'
                    , UN_CONDICION => MI_CONDICION);
        EXCEPTION
            WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE
                , UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_LIMPIAR_CPTEAFECTADOS);
        END;
      END IF;
    END IF; 
  END LOOP; 
 END IF; 
END AFTER STATEMENT;

END;