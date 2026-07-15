create or replace PACKAGE BODY "PCK_CONTABILIZAR_NOMINA3" AS

FUNCTION FC_CONTABILIZARNOMINAHBUCARAMA(
/*
    NAME              : FC_CONTABILIZARNOMINAH
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JAVIER VILLATE
    DATE MIGRADOR     : 26/03/2018
    TIME              : 09:41 PM
    SOURCE MODULE     : INTERFACESPB2018.01.02, EN ACCESS InterfaceNominaHCia
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    PARAMETERS        :
    MODIFICATIONS     :

    @NAME:CONTABILIZARNOMINAHBUCARAMA
    @METHOD:  POST
*/
     UN_COMPANIANOMINA      IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_PROCESO             IN PCK_SUBTIPOS.TI_ID_DE_PROCESO
    ,UN_ANO	                IN PCK_SUBTIPOS.TI_ANIO
    ,UN_MES	                IN PCK_SUBTIPOS.TI_MES
    ,UN_PERIODO	            IN PCK_SUBTIPOS.TI_PERIODO_NOMI
    ,UN_USUARIO             IN PCK_SUBTIPOS.TI_USUARIO

)RETURN CLOB

AS
    MI_RTAPLANO                 CLOB;
    MI_STRETAPA                 VARCHAR2(1000 CHAR);
    MI_CONSECUTIVO              PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_ANO2                     PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_CONCEPTO                 PCK_SUBTIPOS.TI_ENTERO;
    MI_TIANUMDOC                PCK_SUBTIPOS.TI_DESCRIPCION;
    MI_NUMEROCOMP               PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT DEFAULT 0;
    MI_NUMEROCOMPAPORTES        PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT DEFAULT 0;
    MI_NUMEROCOMPCORTOPLAZO     PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT DEFAULT 0;
    MI_PARAMETRO                PCK_SUBTIPOS.TI_PARAMETRO;
    MI_MSGERROR                 PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_PERFECHAINICIO           DATE;
    MI_PERFECHAFINAL            DATE;
    MI_FECHAINTER               DATE;
    MI_AGREGAR                  BOOLEAN DEFAULT FALSE;
    MI_CUENTADEBITO             PCK_SUBTIPOS.TI_CODIGOCONTA;
    MI_CUENTACREDITO            PCK_SUBTIPOS.TI_CODIGOCONTA;
    MI_SUCURSAL                 PCK_SUBTIPOS.TI_SUCURSAL;
    MI_TERCERO                  PCK_SUBTIPOS.TI_TERCERO;
    MI_TERCEROASIG              PCK_SUBTIPOS.TI_TERCERO;
    MI_CENTROCOSTO              PCK_SUBTIPOS.TI_CENTRO_COSTO;
    MI_NOMBRE                   COMPROBANTE_CNT.DESCRIPCION%TYPE;
    MI_STRTEXTO                 PCK_SUBTIPOS.TI_DESCRIPCION;
    MI_CLASE                    TERCERO.CLASE%TYPE;
    MI_VALOR                    PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_NATURALEZA               PCK_SUBTIPOS.TI_NATURALEZACONTA;
    MI_CAMPOS                   PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                  PCK_SUBTIPOS.TI_VALORES;
    MI_VALORESSQL               PCK_SUBTIPOS.TI_VALORES;
    MI_TABLA                    PCK_SUBTIPOS.TI_TABLA;
    MI_FILAS                    PCK_SUBTIPOS.TI_ENTERO;
    MI_REEMPLAZOS               PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_NOMBRECOMPLETO           PERSONAL.NOMBRECOMPLETO%TYPE;
    MI_NOMCOMPANIANOMINA        COMPANIA.NOMBRE%TYPE;
    MI_NOMPROCESO               PROCESOS_DE_NOMINA.NOMBRE_PROCESO%TYPE;
    MI_FONDOPUBLICO             PCK_SUBTIPOS.TI_LOGICO DEFAULT 0;
    MI_PARCCOSTOUNICO           PCK_SUBTIPOS.TI_PARAMETRO;   
    MI_TIPOCOMPROBANTE          PCK_SUBTIPOS.TI_PARAMETRO;
    MI_TIPO                     VARCHAR2(5);
    MI_RUTASALIDA               VARCHAR2(3200):='SCRIPTEMPORALINTERFAZ'||'.sql';
    MI_RUTASALIDA2              VARCHAR2(3200):='SCRIPTEMPORALINTERFAZASIENTO'||'.sql';
   -- MI_S_ARCHIVO                UTL_FILE.FILE_TYPE;
  --  MI_S_ARCHIVO2               UTL_FILE.FILE_TYPE;
    MI_RETORNO                  CLOB:='';
    MI_SCRIP                    CLOB;
    MI_UNIDADNEGOCIO            PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CONDICION                PCK_SUBTIPOS.TI_CONDICION;
    MI_RTAACME                  PCK_SUBTIPOS.TI_RTA_ACME;
    MI_INCONSISTENCIADATO       CLOB;
BEGIN
    BEGIN       
        BEGIN
          SELECT LAST_DAY('01/' || UN_MES || '/' || UN_ANO||'') 
          INTO MI_FECHAINTER
          FROM DUAL;
        EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_FECHAINTER := '30/' || UN_MES|| UN_ANO;
    END;

        MI_PARAMETRO := 'TIPO COMPROBANTE INTERFAZ NOMINA';
        MI_TIPOCOMPROBANTE :=  NVL(PCK_SYSMAN_UTL.FC_PAR
            (UN_COMPANIA  => UN_COMPANIANOMINA
            ,UN_NOMBRE    => MI_PARAMETRO
            ,UN_MODULO    => PCK_DATOS.MODULONOMINA
            ,UN_FECHA_PAR => SYSDATE ), 'NO');

        MI_PARAMETRO := 'UNIDAD DE NEGOCIO';
        MI_UNIDADNEGOCIO :=  NVL(PCK_SYSMAN_UTL.FC_PAR
            (UN_COMPANIA  => UN_COMPANIANOMINA
            ,UN_NOMBRE    => MI_PARAMETRO
            ,UN_MODULO    => PCK_DATOS.MODULONOMINA
            ,UN_FECHA_PAR => SYSDATE ), 'NO');

    EXCEPTION WHEN OTHERS THEN
        MI_MSGERROR(1).CLAVE := 'PARAMETRO';
        MI_MSGERROR(1).VALOR := MI_PARAMETRO;
        PCK_ERR_MSG.RAISE_WITH_MSG(
               UN_EXC_COD =>-20000,
               UN_ERROR_COD=>PCK_ERRORES.ERR_INICIARPARAMETROSOI,
               UN_TABLAERROR =>'PR_INICIARPARAMETROSPLANOSOI',
               UN_REEMPLAZOS => MI_MSGERROR
             );
    END;

  --  MI_S_ARCHIVO             := UTL_FILE.FOPEN ('INTERFACE', MI_RUTASALIDA, 'w');
  --  MI_S_ARCHIVO2            := UTL_FILE.FOPEN ('INTERFACE', MI_RUTASALIDA2, 'w');

    MI_STRETAPA := '01';
    MI_CONSECUTIVO := 1;

    MI_STRETAPA := '02';
    BEGIN
        SELECT COMPANIA.NOMBRE, PN.NOMBRE_PROCESO, COMPANIA.NITCOMPANIA
        INTO   MI_NOMCOMPANIANOMINA, MI_NOMPROCESO, MI_TERCEROASIG
        FROM   COMPANIA INNER JOIN PROCESOS_DE_NOMINA PN ON COMPANIA.CODIGO = PN.COMPANIA
        WHERE  COMPANIA.CODIGO = UN_COMPANIANOMINA
        AND  ID_DE_PROCESO = UN_PROCESO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_NOMCOMPANIANOMINA := '';
        MI_NOMPROCESO := '';
    END;
    MI_ANO2 := SUBSTR(UN_ANO, 3, 2);

   --15/08/2019 eamaya Se eliminan registros de la tabla para cada mes y año para que no se repitan cada que se llame el proceso

            MI_CONDICION :='  EXTRACT(YEAR FROM TIACOMFEC) = '||UN_ANO||'
                          AND EXTRACT(MONTH FROM TIACOMFEC)  = '||UN_MES;

                BEGIN
                   BEGIN 
                       PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'TEMPORALINTERFAZASIENTO'
                                                            ,UN_ACCION  => 'E'
                                                            ,UN_CONDICION => MI_CONDICION);                                                           
    
                         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                                    RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;                                 
                   
                   END;                   
                   
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                            MI_REEMPLAZOS(1).CLAVE := 'ETAPA';
                            MI_REEMPLAZOS(1).VALOR := MI_STRETAPA;
                            MI_REEMPLAZOS(2).CLAVE := 'CODERROR';
                            MI_REEMPLAZOS(2).VALOR := SQLCODE;
                            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                                        UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTANOMINA,
                                                        UN_REEMPLAZOS => MI_REEMPLAZOS);
                
                END;

            MI_CONDICION :='  EXTRACT(YEAR FROM TICREFEC) = '||UN_ANO||'
                          AND EXTRACT(MONTH FROM TICREFEC)  = '||UN_MES;

                BEGIN

                    BEGIN
                        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'TEMPORALINTERFAZ'
                                                            ,UN_ACCION  => 'E'
                                                            ,UN_CONDICION => MI_CONDICION);                                                           
    
                         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                                    RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;                                 

                    END;        
                 
                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                        MI_REEMPLAZOS(1).CLAVE := 'ETAPA';
                        MI_REEMPLAZOS(1).VALOR := MI_STRETAPA;
                        MI_REEMPLAZOS(2).CLAVE := 'CODERROR';
                        MI_REEMPLAZOS(2).VALOR := SQLCODE;
                        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                                    UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTANOMINA,
                                                    UN_REEMPLAZOS => MI_REEMPLAZOS);
                END;
                 

    BEGIN
        SELECT NVL(MAX(TISEC),0)
        INTO  MI_NUMEROCOMP
        FROM  TEMPORALINTERFAZ
        WHERE EXTRACT(MONTH FROM TICREFEC)=UN_MES;

        IF MI_NUMEROCOMP  = 0 THEN
            MI_NUMEROCOMP := TO_NUMBER(UN_ANO || PCK_SYSMAN_UTL.FC_STRZERO(UN_MES, 2) || '0001');
        ELSE
            MI_NUMEROCOMP := MI_NUMEROCOMP  + 1;
        END IF;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_NUMEROCOMP := TO_NUMBER(UN_ANO || PCK_SYSMAN_UTL.FC_STRZERO(UN_MES, 2) || '0001');
    END;


    MI_STRETAPA := '03';
    BEGIN
        SELECT FECHAFINAL, FECHAINICIO
        INTO   MI_PERFECHAFINAL, MI_PERFECHAINICIO
        FROM   PERIODOS
        WHERE  COMPANIA =  UN_COMPANIANOMINA
          AND  ID_DE_PROCESO = UN_PROCESO
          AND  ANO =UN_ANO
          AND  MES = UN_MES
          AND  PERIODO = UN_PERIODO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        RETURN 'No existe el periodo de nomina';
    END;    

/*CONSULTA GENERAL*/
    <<TPERSONAL>>
    FOR MI_RS IN
    (SELECT HIS.COMPANIA, HIS.ID_DE_PROCESO, HIS.ANO, CN.ID_DE_CONCEPTO CONCEPTO, HIS.MES, HIS.PERIODO, HIS.ID_DE_EMPLEADO, HIS.ID_DE_CONCEPTO, HIS.VALOR,CN.CLASE,
    	PH.NOMBRECOMPLETO, PH.APELLIDO1,PH.APELLIDO2 ,PH.NOMBRES, PH.FONDO_SALUD,PH.ID_DEL_FONDO,PH.FONDO_RIESGOS,PH.FONDO_CESANTIAS,
    	PH.FONDO_PENSION_VOL, PH.CAJA_COMPENSACION, PH.MEDICINA_PREPAGADA, PH.FONDO_SINDICATO, PH.NUMERO_DCTO, PH.SUCURSAL SUCURSALPERSONAL , PH.GRUPOCONTABLE, TERCERO.TIPOID TIPO_DOCUMENTO,
    	CASE WHEN CN.PORCDC IN(0) THEN  '99999999999999999999' ELSE PH.ID_CENTRO_DE_COSTO END ID_CENTRO_DE_COSTO_ACT,
        CN.NITDE, CN.TERCERO, CN.SUCURSAL, CN.NOMBRE_CONCEPTO, CASE WHEN CN.PORCDC IN(0) THEN  'VARIOS' || '-' || CN.NOMBRE_CONCEPTO  ELSE CC.NOMBRE ||'-' || CN.NOMBRE_CONCEPTO END CONCEPTOCCOSTO, CN.TIPODEFONDO,
        CN.CTA_DBT_VENTAS, CN.CTA_CRD_VENTAS, CN.CTA_DBT_PRODUCCION, CN.CTA_CRD_PRODUCCION, CN.CTA_DBT_ADMINISTRACION,
        CN.CTA_CRD_ADMINISTRACION, CN.CTA_CRD_OTRO, CN.CTA_DBT_OTRO, CN.CTA_CRD_SUPERNUM, CN.CTA_DBT_SUPERNUM,
        CN.BOLCUENTA_ADM_PRICIPAL, CN.BOLCUENTA_VNT_PRICIPAL, CN.BOLCUENTA_PRD_PRICIPAL, CN.BOLCUENTA_OTRO_PRICIPAL,
        CN.BOLCUENTA_SUP_PRICIPAL
    	,V_FONDO_DE_SALUD.NIT EPSNIT, V_FONDO_DE_SALUD.NOMBRE_FONDO_SALUD EPSNOMBRE, V_FONDO_DE_SALUD.SUCURSAL EPSSUCURSAL,V_FONDO_DE_SALUD.PUBLICO  EPSPUBLICO
    	,V_FONDO_DE_PENSIONES.NIT AFPNIT , V_FONDO_DE_PENSIONES.NOMBRE_DEL_FONDO AFPNOMBRE, V_FONDO_DE_PENSIONES.SUCURSAL AFPSUCURSAL, V_FONDO_DE_PENSIONES.PUBLICO AFPPUBLICO
    	,V_FONDO_DE_RIESGOS.NIT ARLNIT  , V_FONDO_DE_RIESGOS.NOMBRE_FONDO_RIESGOS ARLNOMBRE, V_FONDO_DE_RIESGOS.SUCURSAL ARLSUCURSAL, V_FONDO_DE_RIESGOS.PUBLICO ARLPUBLICO
    	,V_FONDO_DE_CESANTIAS.NIT CESNIT  , V_FONDO_DE_CESANTIAS.NOMBRE_FONDO_CESANTIAS CESNOMBRE, V_FONDO_DE_CESANTIAS.SUCURSAL CESSUCURSAL, V_FONDO_DE_CESANTIAS.PUBLICO CESPUBLICO
    	,V_FONDO_DE_PENSION_VOL.NIT APVNIT  , V_FONDO_DE_PENSION_VOL.NOMBRE_DEL_FONDO APVNOMBRE, V_FONDO_DE_PENSION_VOL.SUCURSAL APVSUCURSAL,V_FONDO_DE_PENSION_VOL.PUBLICO APVPUBLICO
        ,V_FONDO_DE_PENSION_AFC.NIT AFCNIT  , V_FONDO_DE_PENSION_AFC.NOMBRE_FONDO_AFC AFCNOMBRE, V_FONDO_DE_PENSION_AFC.SUCURSAL AFCSUCURSAL, V_FONDO_DE_PENSION_AFC.PUBLICO AFCPUBLICO
    	,V_CAJA_COMPENSACION.NIT CCFNIT  , V_CAJA_COMPENSACION.NOMBRE_CAJA CCFNOMBRE, V_CAJA_COMPENSACION.SUCURSAL CCFSUCURSAL, V_CAJA_COMPENSACION.PUBLICO CCFPUBLICO
    	,V_MEDICINA_PREPAGADA.NIT FMPNIT  , V_MEDICINA_PREPAGADA.NOMBRE_MEDICINA FMPNOMBRE, V_MEDICINA_PREPAGADA.SUCURSAL FMPSUCURSAL, V_MEDICINA_PREPAGADA.PUBLICO FMPPUBLICO
    	,V_FONDO_DE_SINDICATOS.NIT SINNIT  , V_FONDO_DE_SINDICATOS.NOMBRE_FONDO_SINDICATO SINNOMBRE, V_FONDO_DE_SINDICATOS.SUCURSAL SINSUCURSAL,PH.FUENTE_DE_RECURSO FUENTE_DE_RECURSO,PH.REFERENCIA REFERENCIA

     FROM HISTORICOS HIS
    	INNER JOIN CONCEPTOS CN
    		 ON HIS.COMPANIA=CN.COMPANIA
    		AND HIS.ID_DE_CONCEPTO=CN.ID_DE_CONCEPTO
        INNER JOIN PERSONAL_HISTORICO PH
             ON HIS.COMPANIA = PH.COMPANIA
            AND HIS.ID_DE_PROCESO = PH.ID_DE_PROCESO
            AND HIS.ANO = PH.ANO
            AND HIS.MES = PH.MES
            AND HIS.PERIODO = PH.PERIODO
            AND HIS.ID_DE_EMPLEADO = PH.ID_DE_EMPLEADO

      LEFT JOIN CENTRO_COSTO CC
    		 ON CC.COMPANIA      = PH.COMPANIA
    		AND CC.ANO           = PH.ANO
        AND CC.CODIGO        = PH.ID_CENTRO_DE_COSTO

      LEFT JOIN TIPOS_DOCUMENTOS TD
    		 ON TD.COMPANIA       = PH.COMPANIA
    		AND TD.DCTO_IDENTIDAD = PH.DCTO_IDENTIDAD

    	LEFT JOIN V_FONDO_DE_SALUD
    		 ON PH.COMPANIA      = V_FONDO_DE_SALUD.COMPANIA
    		AND PH.FONDO_SALUD   = V_FONDO_DE_SALUD.FONDO_SALUD

    	LEFT JOIN V_FONDO_DE_PENSIONES
    		 ON PH.COMPANIA      = V_FONDO_DE_PENSIONES.COMPANIA
    		AND PH.ID_DEL_FONDO  = V_FONDO_DE_PENSIONES.ID_DEL_FONDO
            
            LEFT JOIN TERCERO
    ON TERCERO.COMPANIA = CN.COMPANIA 
    AND TERCERO.NIT = CN.TERCERO
    AND TERCERO.SUCURSAL = CN.SUCURSAL

    	LEFT JOIN V_FONDO_DE_RIESGOS
    		 ON PH.COMPANIA      = V_FONDO_DE_RIESGOS.COMPANIA
    		AND PH.FONDO_RIESGOS = V_FONDO_DE_RIESGOS.FONDO_RIESGOS

    	LEFT JOIN V_FONDO_DE_CESANTIAS
    		 ON PH.COMPANIA = V_FONDO_DE_CESANTIAS.COMPANIA
    		AND PH.FONDO_CESANTIAS = V_FONDO_DE_CESANTIAS.FONDO_CESANTIAS

    	LEFT JOIN V_FONDO_DE_PENSION_VOL
    		 ON PH.COMPANIA = V_FONDO_DE_PENSION_VOL.COMPANIA
    		AND PH.FONDO_PENSION_VOL = V_FONDO_DE_PENSION_VOL.ID_DEL_FONDO

        LEFT JOIN V_FONDO_DE_PENSION_AFC        --AQUI
             ON PH.COMPANIA = V_FONDO_DE_PENSION_AFC.COMPANIA
            AND PH.FONDO_AFC = V_FONDO_DE_PENSION_AFC.FONDO_AFC

    	LEFT JOIN V_CAJA_COMPENSACION
    		 ON PH.COMPANIA = V_CAJA_COMPENSACION.COMPANIA
    		AND PH.CAJA_COMPENSACION = V_CAJA_COMPENSACION.CAJA_COMPENSACION

    	LEFT JOIN V_MEDICINA_PREPAGADA
    		 ON PH.COMPANIA = V_MEDICINA_PREPAGADA.COMPANIA
    		AND PH.MEDICINA_PREPAGADA = V_MEDICINA_PREPAGADA.MEDICINA_PREPAGADA

    	LEFT JOIN V_FONDO_DE_SINDICATOS
    		 ON PH.COMPANIA = V_FONDO_DE_SINDICATOS.COMPANIA
    		AND PH.FONDO_SINDICATO = V_FONDO_DE_SINDICATOS.FONDO_SINDICATO
     WHERE HIS.COMPANIA = UN_COMPANIANOMINA
       AND HIS.ID_DE_PROCESO = UN_PROCESO
       AND HIS.ANO = UN_ANO
       AND HIS.MES = UN_MES
       AND HIS.PERIODO = UN_PERIODO
       AND HIS.VALOR<>0
       AND CN.CLASE IN (3,5,7,8)
       AND PH.FECHA_DE_INGRESO <= TO_DATE(TO_CHAR(MI_PERFECHAFINAL, 'DD/MM/YYYY') ,'DD/MM/YYYY')

    ORDER BY HIS.ID_DE_PROCESO, HIS.ANO, HIS.MES, HIS.PERIODO, CN.CLASE, HIS.ID_DE_EMPLEADO, HIS.ID_DE_CONCEPTO
    )
    LOOP

        MI_STRETAPA := '10';
        MI_NOMBRECOMPLETO := MI_RS.NOMBRECOMPLETO;
        MI_STRETAPA := '11';
        
        IF MI_RS.TIPO_DOCUMENTO = 'N' THEN
        MI_TIPO := 'NIT';
        ELSIF MI_RS.TIPO_DOCUMENTO = 'C' THEN
        MI_TIPO := 'CC';
        ELSE MI_TIPO := MI_RS.TIPO_DOCUMENTO;
        END IF;

        --(MZANGUNA:26/09/2018)--Se agrega condicion de empleados y otros
        
        IF NVL(MI_RS.NITDE, ' ') = 'E' THEN 
            MI_TERCERO := MI_RS.NUMERO_DCTO; --VIENE DE PERSONAL HISTORICO ES LA CEDULA DE LA PERSONA 
            MI_SUCURSAL := NVL(MI_RS.SUCURSALPERSONAL, '001');
            MI_NOMBRE := MI_RS.NOMBRE_CONCEPTO;
        ELSIF MI_RS.TERCERO IS NOT NULL AND NVL(MI_RS.NITDE, ' ') = 'O' THEN -- VIENE DE LA TABLA CONCEPTO
            MI_TERCERO := MI_RS.TERCERO;
            MI_SUCURSAL := NVL(MI_RS.SUCURSAL, '001');
            MI_NOMBRE := MI_RS.NOMBRE_CONCEPTO;
            MI_CLASE := 'T';
        ELSE
            IF NVL(MI_RS.TIPODEFONDO, 'NIN') <> 'NIN' THEN
                IF MI_RS.TIPODEFONDO = 'EPS' THEN
                    MI_STRETAPA := '13';
                    MI_TERCERO := MI_RS.EPSNIT;
                    MI_SUCURSAL := MI_RS.EPSSUCURSAL;
                    MI_NOMBRE := MI_RS.EPSNOMBRE ;
                    MI_FONDOPUBLICO := MI_RS.EPSPUBLICO;

                ELSIF MI_RS.TIPODEFONDO = 'AFP' THEN
                    MI_STRETAPA := '14';
                    MI_TERCERO := MI_RS.AFPNIT;
                    MI_SUCURSAL := MI_RS.AFPSUCURSAL;
                    MI_NOMBRE := MI_RS.AFPNOMBRE;
                    MI_FONDOPUBLICO := MI_RS.AFPPUBLICO;

                ELSIF MI_RS.TIPODEFONDO = 'ARL' THEN
                    MI_STRETAPA := '16';
                    MI_TERCERO := MI_RS.ARLNIT;
                    MI_SUCURSAL := MI_RS.ARLSUCURSAL;
                    MI_NOMBRE := MI_RS.ARLNOMBRE;
                    MI_FONDOPUBLICO := MI_RS.ARLPUBLICO;

                ELSIF MI_RS.TIPODEFONDO = 'CES' THEN
                    MI_STRETAPA := '17';
                    MI_TERCERO := MI_RS.CESNIT;
                    MI_SUCURSAL :=  MI_RS.CESSUCURSAL;
                    MI_NOMBRE := MI_RS.CESNOMBRE;

                ELSIF MI_RS.TIPODEFONDO = 'APV' THEN
                    MI_STRETAPA := '20';
                    MI_TERCERO := MI_RS.APVNIT;
                    MI_SUCURSAL := MI_RS.APVSUCURSAL;
                    MI_NOMBRE := MI_RS.APVNOMBRE;

                ELSIF MI_RS.TIPODEFONDO = 'AFC' THEN
                    MI_STRETAPA := '20';
                    MI_TERCERO := MI_RS.AFCNIT;
                    MI_SUCURSAL := MI_RS.AFCSUCURSAL;
                    MI_NOMBRE := MI_RS.AFCNOMBRE;

                ELSIF MI_RS.TIPODEFONDO = 'CCF' THEN
                    MI_STRETAPA := '22';
                    MI_TERCERO := MI_RS.CCFNIT;
                    MI_SUCURSAL := MI_RS.CCFSUCURSAL;
                    MI_NOMBRE := MI_RS.CCFNOMBRE;


                ELSIF MI_RS.TIPODEFONDO = 'FMP' THEN
                    MI_STRETAPA := '24';
                    MI_TERCERO := MI_RS.FMPNIT;
                    MI_SUCURSAL := MI_RS.FMPSUCURSAL;
                    MI_NOMBRE := MI_RS.FMPNOMBRE;


                ELSIF MI_RS.TIPODEFONDO = 'SIN' THEN
                    MI_STRETAPA := '26';
                    MI_TERCERO := MI_RS.SINNIT;
                    MI_SUCURSAL := MI_RS.SINSUCURSAL;
                    MI_NOMBRE := MI_RS.SINSUCURSAL;
                END IF;
                MI_CLASE := 'E';
            ELSE
                MI_STRETAPA := '28';
                MI_TERCERO := MI_RS.NUMERO_DCTO;
                MI_SUCURSAL := MI_RS.SUCURSALPERSONAL;
                MI_NOMBRE := MI_RS.NOMBRECOMPLETO;
                MI_CLASE := 'E';
            END IF;
        END IF;

        MI_STRETAPA := '29';
        MI_TERCERO := REPLACE(MI_TERCERO, '-', '');
        MI_TERCERO := REPLACE(MI_TERCERO, '.', '');
        MI_TERCERO := REPLACE(MI_TERCERO, ' ', '');
        MI_TERCERO := NVL(MI_TERCERO, '999999999999999999');
        MI_SUCURSAL := CASE WHEN MI_TERCERO='999999999999999999' THEN '999' ELSE MI_SUCURSAL END;

    --      GRUPO CONTABLE
            MI_CUENTADEBITO := MI_RS.CTA_DBT_ADMINISTRACION;
           	MI_CUENTACREDITO := MI_RS.CTA_CRD_ADMINISTRACION;

        MI_VALOR := TRUNC(NVL(MI_RS.VALOR, 0) * 100 + 0.001) / 100;

        MI_STRETAPA := '33';

        MI_CENTROCOSTO := MI_RS.ID_CENTRO_DE_COSTO_ACT;
        
        MI_CONCEPTO := MI_RS.CONCEPTO;
        
        MI_TIANUMDOC := MI_NOMCOMPANIANOMINA || ', ' || UN_ANO || ' - ' || UN_MES || ' - ' || UN_PERIODO;

        MI_STRTEXTO := 'INT.NOMINA DE: ' ||  MI_NOMCOMPANIANOMINA  || ', PROC: ' ||  MI_NOMPROCESO  || ', ANIO: ' ||  UN_ANO  || ', MES: ' || UN_MES || ', PERIODO: ' ||  UN_PERIODO ;

        BEGIN
            MI_TABLA := 'TEMP_PLANA_AJUSTES';
            MI_CAMPOS := ' COMPANIA      ,ANO            ,TIPO_CPTE
                          ,COMPROBANTE   ,CONSECUTIVO    ,CUENTA
                          ,FECHA
                          ,NATURALEZA     ,VALOR_DEBITO  ,VALOR_CREDITO
                          ,EJECUCION_DEBITO, EJECUCION_CREDITO, CENTRO_COSTO,TERCERO
                          ,SUCURSAL ,AUXILIAR ,DESCRIPCION , CONCEPTO, FUENTE_RECURSOS, REFERENCIA, CLASE, ID_DE_CONCEPTO
                          ,CONCEPTOCCOSTO, TIPO_DOCUMENTO, UNIDADNEGOCIO, COMPDOC';

            IF NVL(TRIM(MI_CUENTADEBITO), ' ') <> ' ' AND MI_VALOR <> 0 THEN
                MI_STRETAPA := '34';

                MI_NATURALEZA := PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CUENTADEBITO);
                MI_VALORES := '  '''|| UN_COMPANIANOMINA ||'''        ,'|| UN_ANO ||'         , '''|| MI_TIPOCOMPROBANTE ||'''
                                ,'|| MI_NUMEROCOMP ||'              ,'|| MI_CONSECUTIVO ||' , '''|| MI_CUENTADEBITO ||'''
                                ,TO_DATE(''' || TO_CHAR(MI_FECHAINTER, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                , '''|| MI_NATURALEZA ||'''      ,'|| MI_VALOR ||'          ,0
                                ,'|| MI_VALOR ||'               ,0                      , '''|| MI_CENTROCOSTO ||'''
                                , '''|| MI_TERCERO ||'''         , '''|| MI_SUCURSAL ||''', '''|| PCK_DATOS.CONS_AUXILIAR ||'''
                                , '''||MI_STRTEXTO ||''', '||MI_CONCEPTO ||' , '''|| CASE WHEN  MI_RS.FUENTE_DE_RECURSO IS NOT NULL THEN MI_RS.FUENTE_DE_RECURSO ELSE '99999999999999999999' END ||''', '''|| CASE WHEN MI_RS.REFERENCIA IS NOT NULL THEN MI_RS.REFERENCIA ELSE '99999999999999999999' END  ||''','|| MI_RS.CLASE ||','|| MI_RS.ID_DE_CONCEPTO || '
                                ,'''|| MI_RS.CONCEPTOCCOSTO  ||''','''|| MI_TIPO  ||''','''|| MI_UNIDADNEGOCIO  ||''', '''|| MI_TIANUMDOC ||''' ';

                BEGIN
                    BEGIN
                    
                        MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                      UN_ACCION  => 'I',
                                                      UN_CAMPOS  => MI_CAMPOS,
                                                      UN_VALORES => MI_VALORES);
    
    
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                    END;
                
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                        MI_REEMPLAZOS(1).CLAVE := 'ETAPA';
                        MI_REEMPLAZOS(1).VALOR := MI_STRETAPA;
                        MI_REEMPLAZOS(2).CLAVE := 'CODERROR';
                        MI_REEMPLAZOS(2).VALOR := SQLCODE;
                        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                                    UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTANOMINA,
                                                    UN_REEMPLAZOS => MI_REEMPLAZOS);
                END;
                
                MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
                MI_AGREGAR := TRUE;
            END IF;


            IF NVL(TRIM(MI_CUENTACREDITO), ' ') <> ' ' AND MI_VALOR <> 0 THEN
                MI_STRETAPA := '35';

                MI_NATURALEZA := PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CUENTACREDITO);
                MI_VALORES := '  '''|| UN_COMPANIANOMINA ||'''        ,'|| UN_ANO ||'         , '''|| MI_TIPOCOMPROBANTE ||'''
                                ,'|| MI_NUMEROCOMP ||'          ,'|| MI_CONSECUTIVO ||' , '''|| MI_CUENTACREDITO ||'''
                                ,TO_DATE(''' || TO_CHAR(MI_FECHAINTER, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                , '''|| MI_NATURALEZA ||'''      ,0                         ,'|| MI_VALOR ||'
                                ,0                              ,'|| MI_VALOR ||'    , '''|| MI_CENTROCOSTO ||'''
                                , '''|| MI_TERCERO ||'''         , '''|| MI_SUCURSAL ||''', '''|| PCK_DATOS.CONS_AUXILIAR ||'''
                                , '''||MI_STRTEXTO ||''', '||MI_CONCEPTO ||',  '''|| CASE WHEN  MI_RS.FUENTE_DE_RECURSO IS NOT NULL THEN MI_RS.FUENTE_DE_RECURSO ELSE '99999999999999999999' END ||''', '''|| CASE WHEN MI_RS.REFERENCIA IS NOT NULL THEN MI_RS.REFERENCIA ELSE '99999999999999999999' END  ||''', '|| MI_RS.CLASE ||','|| MI_RS.ID_DE_CONCEPTO || '
                                ,'''|| MI_RS.CONCEPTOCCOSTO  ||''','''|| MI_TIPO ||''','''|| MI_UNIDADNEGOCIO ||''', '''|| MI_TIANUMDOC ||'''';

                BEGIN
                    BEGIN
                        MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                      UN_ACCION  => 'I',
                                                      UN_CAMPOS  => MI_CAMPOS,
                                                      UN_VALORES => MI_VALORES);
    
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                    END;
                
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                        MI_REEMPLAZOS(1).CLAVE := 'ETAPA';
                        MI_REEMPLAZOS(1).VALOR := MI_STRETAPA;
                        MI_REEMPLAZOS(2).CLAVE := 'CODERROR';
                        MI_REEMPLAZOS(2).VALOR := SQLCODE;
                        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                                    UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTANOMINA,
                                                    UN_REEMPLAZOS => MI_REEMPLAZOS);
                END;                
                

                MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
                MI_AGREGAR := TRUE;
            END IF;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
            --Error en el proceso de contabilizar Nomina,  Para el tercero: --TERCERO-- , Centro de costo: --COSTO--, Descripción: --DESCRIP-- ,Valor débito: --VALORDEB--, Valor crédito: --VALORCRED--.
            MI_REEMPLAZOS(1).CLAVE := 'TERCERO';
            MI_REEMPLAZOS(1).VALOR := MI_TERCERO;
            MI_REEMPLAZOS(2).CLAVE := 'COSTO';
            MI_REEMPLAZOS(2).VALOR := MI_RS.ID_CENTRO_DE_COSTO_ACT;
            MI_REEMPLAZOS(3).CLAVE := 'DESCRIP';
            MI_REEMPLAZOS(3).VALOR := MI_RS.NOMBRECOMPLETO;
            MI_REEMPLAZOS(4).CLAVE := 'VALORDEB';
            MI_REEMPLAZOS(4).VALOR := MI_VALOR;
            MI_REEMPLAZOS(5).CLAVE := 'VALORCRED';
            MI_REEMPLAZOS(5).VALOR := MI_VALOR;

            PCK_ERR_MSG.RAISE_WITH_MSG
                    ( UN_EXC_COD => SQLCODE
                    , UN_TABLAERROR => 'TERCERO'
                    , UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTANOMINAH
                    , UN_REEMPLAZOS => MI_REEMPLAZOS);
        END;

    END LOOP TPERSONAL;



--****INSERTA NOMINA POR PAGAR TABLA TEMPORAL INTERFAZ

    FOR MI_RS IN
    (
      SELECT DISTINCT 1 EMPCOD, COMPROBANTE TISEC, DESCRIPCION TIPRODES, 'I' TIESTADO , 'SA' TIPROIND, FECHA TICREFEC, UN_USUARIO TICREUSU,
      TO_DATE('01/01/1753','DD/MM/YYYY HH24:MI:SS') TIMODFEC, '' TIMODUSU,TO_DATE('01/01/1753','DD/MM/YYYY HH24:MI:SS') TIVALFEC, '' TIVALUSU, TO_DATE('01/01/1753','DD/MM/YYYY HH24:MI:SS') TIPROFEC, UN_USUARIO TIPROUSU,
      'L' TIINDLN, ''	TIANUUSU,	'' TIANUFEC,	TO_DATE('01/01/1753','DD/MM/YYYY HH24:MI:SS') TIFECVIG, 	'' TIANUCAU, '' TITIP
      FROM TEMP_PLANA_AJUSTES
      WHERE TIPO_CPTE=MI_TIPOCOMPROBANTE AND COMPROBANTE=MI_NUMEROCOMP AND TO_DATE('01/01/1753','DD/MM/YYYY HH24:MI:SS') IS NOT NULL) LOOP 

    BEGIN
            MI_TABLA := 'TEMPORALINTERFAZ';
            MI_CAMPOS := ' EMPCOD,TISEC,TIPRODES,TIESTADO,TIPROIND,TICREFEC,TICREUSU,
                           TIMODFEC,TIMODUSU,TIVALFEC,TIVALUSU,TIPROFEC,TIPROUSU,
                           TIINDLN,TIANUUSU,TIANUFEC,TIFECVIG,TIANUCAU,TITIP';

                MI_STRETAPA := '34';
                MI_VALORES := ''|| MI_RS.EMPCOD ||', '|| MI_RS.TISEC ||', '''|| MI_RS.TIPRODES ||'''
                                ,'''|| MI_RS.TIESTADO ||''', '''|| MI_RS.TIPROIND ||'''   
                                ,TO_DATE(''' || TO_CHAR(MI_RS.TICREFEC, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')    
                                , '''|| MI_RS.TICREUSU ||'''
                                ,TO_DATE(''' || TO_CHAR(MI_RS.TIMODFEC, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                ,'''|| MI_RS.TIMODUSU ||'''
                               ,TO_DATE(''' || TO_CHAR(MI_RS.TIVALFEC, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                ,'''|| MI_RS.TIVALUSU ||'''  
                                ,TO_DATE(''' || TO_CHAR(MI_RS.TIPROFEC, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'') 
                                ,'''|| MI_RS.TIPROUSU ||'''  , '''|| MI_RS.TIINDLN ||''', '''|| MI_RS.TIANUUSU ||''','''|| MI_RS.TIANUFEC ||'''                                
                                ,TO_DATE(''' || TO_CHAR(MI_RS.TIFECVIG, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                ,'''|| MI_RS.TIANUCAU ||'''  ,'''|| MI_RS.TITIP ||'''';

                MI_VALORESSQL := ''|| MI_RS.EMPCOD ||', '|| MI_RS.TISEC ||', '''|| MI_RS.TIPRODES ||'''
                                   ,'''|| MI_RS.TIESTADO ||''', '''|| MI_RS.TIPROIND ||'''   
                                   ,CONVERT(DATETIME,''' || (MI_RS.TICREFEC) || ''',103)     
                                   , '''|| MI_RS.TICREUSU ||'''
                                   ,CONVERT(DATETIME,''' || (MI_RS.TIMODFEC) || ''',103)
                                   ,'''|| MI_RS.TIMODUSU ||'''
                                  ,CONVERT(DATETIME,''' || (MI_RS.TIVALFEC) || ''',103)
                                   ,'''|| MI_RS.TIVALUSU ||'''  
                                   ,CONVERT(DATETIME,''' || (MI_RS.TIPROFEC) || ''',103)
                                   ,'''|| MI_RS.TIPROUSU ||'''  , '''|| MI_RS.TIINDLN ||''', '''|| MI_RS.TIANUUSU ||''','''|| MI_RS.TIANUFEC ||'''                                
                                   ,CONVERT(DATETIME,''' || (MI_RS.TIFECVIG) || ''',103)
                                   ,'''|| MI_RS.TIANUCAU ||'''  ,'''|| MI_RS.TITIP ||'''';

                BEGIN
                    BEGIN
                        MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                      UN_ACCION  => 'I',
                                                      UN_CAMPOS  => MI_CAMPOS,
                                                      UN_VALORES => MI_VALORES);
    
                         MI_SCRIP:='INSERT INTO ' || MI_TABLA ||'(' || MI_CAMPOS || ')' || ' VALUES ' || '(' || MI_VALORESSQL || ');';
                         --UTL_FILE.PUT_LINE(MI_S_ARCHIVO,CHR(10)||CHR(13) || MI_SCRIP);
                         MI_RETORNO:=MI_RETORNO||CHR(13)||CHR(10)||MI_SCRIP;
    
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                    END;
                
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                        MI_REEMPLAZOS(1).CLAVE := 'ETAPA';
                        MI_REEMPLAZOS(1).VALOR := MI_STRETAPA;
                        MI_REEMPLAZOS(2).CLAVE := 'CODERROR';
                        MI_REEMPLAZOS(2).VALOR := SQLCODE;
                        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                                    UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTANOMINA,
                                                    UN_REEMPLAZOS => MI_REEMPLAZOS);
                END;                
         END;   
    END LOOP;     

--****INSERTA NOMINA POR PAGAR TABLA TEMPORAL INTERFAZ ASIENTO
  MI_CONSECUTIVO := 1;
    FOR MI_RS IN
    (
      SELECT EMPCOD,TISEC,TIAESTADO,TIACOMTIP,TIACOMNUM,TIACOMFEC,TIATERTIPID,
             TIATERNUMID,TIAPUCNROCTA,TIAVALDEB,TIAVALCRE,TIAAGECOD,TIAIMPPOR,TIAIMPBASE,
             TIADESMOV, CONCEPT, TIANUMDOC,TIACIUCOD,TIAINTFINNUMUNI,TIAINTFINNUMREG,TIABANSECINT,
             TIABANTIPCUE,TIABANNUMCUE,TIACODCONT,TIACODCONV,TIACOMSECINT,TIAAGESECINT,
             TIACENSECINT,TIATERSECINT,TIAPUCSECINT,TIABANSUCSECINT,TIACIUSECINT,
             TIAPRECOMFEC,TIAPRECOMSEC
      FROM(
             SELECT 1 EMPCOD, COMPROBANTE TISEC, 'I' TIAESTADO, TIPO_CPTE TIACOMTIP,COMPROBANTE TIACOMNUM,
             TO_DATE(FECHA,'DD/MM/YYYY HH24:MI:SS') TIACOMFEC, TIPO_DOCUMENTO TIATERTIPID ,TERCERO TIATERNUMID,CUENTA TIAPUCNROCTA, SUM(VALOR_DEBITO) TIAVALDEB,
             SUM(VALOR_CREDITO) TIAVALCRE,UNIDADNEGOCIO TIAAGECOD,0 TIAIMPPOR,0 TIAIMPBASE, DESCRIPCION TIADESMOV, CONCEPTO CONCEPT, COMPDOC TIANUMDOC, '68001' TIACIUCOD,
             0 TIAINTFINNUMUNI,0 TIAINTFINNUMREG,'NA' TIABANSECINT,'NA' TIABANTIPCUE,'' TIABANNUMCUE,'' TIACODCONT, 0 TIACODCONV,
             0 TIACOMSECINT,0 TIAAGESECINT,0 TIACENSECINT,0 TIATERSECINT, 0 TIAPUCSECINT, 0 TIABANSUCSECINT,0 TIACIUSECINT,
             TO_DATE('01/01/1753','DD/MM/YYYY HH24:MI:SS') TIAPRECOMFEC,0 TIAPRECOMSEC, CLASE
             FROM TEMP_PLANA_AJUSTES
             WHERE CLASE IN(3,5,7) AND TIPO_CPTE=MI_TIPOCOMPROBANTE AND COMPROBANTE=MI_NUMEROCOMP
             GROUP BY COMPROBANTE,TIPO_CPTE,FECHA,TIPO_DOCUMENTO,TERCERO,CUENTA,UNIDADNEGOCIO,DESCRIPCION,CLASE, CONCEPTO, COMPDOC
      ORDER BY CLASE)) LOOP 

    BEGIN
            MI_TABLA := 'TEMPORALINTERFAZASIENTO';
            MI_CAMPOS := ' EMPCOD,TISEC,TIASEC,TIAESTADO,TIACOMTIP,TIACOMNUM,TIACOMFEC,TIATERTIPID,
                           TIATERNUMID,TIAPUCNROCTA,TIAVALDEB,TIAVALCRE,TIAAGECOD,TIAIMPPOR,TIAIMPBASE,
                           TIADESMOV, CONCEPT, TIANUMDOC,TIACIUCOD,TIAINTFINNUMUNI,TIAINTFINNUMREG,TIABANSECINT,
                           TIABANTIPCUE,TIABANNUMCUE,TIACODCONT,TIACODCONV,TIACOMSECINT,TIAAGESECINT,
                           TIACENSECINT,TIATERSECINT,TIAPUCSECINT,TIABANSUCSECINT,TIACIUSECINT,
                           TIAPRECOMFEC,TIAPRECOMSEC';

                MI_STRETAPA := '34';
                MI_VALORES := '  '|| MI_RS.EMPCOD ||', '|| MI_RS.TISEC ||', '||   MI_CONSECUTIVO ||'
                                ,'''|| MI_RS.TIAESTADO ||''', '''|| MI_RS.TIACOMTIP ||''' ,
                                '|| MI_RS.TIACOMNUM ||'
                                ,TO_DATE(''' || TO_CHAR(MI_RS.TIACOMFEC, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                , '''|| MI_RS.TIATERTIPID ||''', '''|| MI_RS.TIATERNUMID ||'''
                                ,'''|| MI_RS.TIAPUCNROCTA ||''','|| MI_RS.TIAVALDEB ||','|| MI_RS.TIAVALCRE ||'
                                ,'''|| MI_RS.TIAAGECOD ||''','|| MI_RS.TIAIMPPOR ||', '|| MI_RS.TIAIMPBASE ||'
                                ,'''|| MI_RS.TIADESMOV ||''', '|| MI_RS.CONCEPT ||' ,'''||  MI_RS.TIANUMDOC ||''','''|| MI_RS.TIACIUCOD||'''
                                ,'''|| MI_RS.TIAINTFINNUMUNI ||''','''||  MI_RS.TIAINTFINNUMREG||''','''||  MI_RS.TIABANSECINT ||'''
                                , '''||  MI_RS.TIABANTIPCUE ||''','''||  MI_RS.TIABANNUMCUE ||''','''||  MI_RS.TIACODCONT ||'''
                                ,'|| MI_RS.TIACODCONV||','|| MI_RS.TIACOMSECINT||','|| MI_RS.TIAAGESECINT||'
                                ,'|| MI_RS.TIACENSECINT||','|| MI_RS.TIATERSECINT||','|| MI_RS.TIAPUCSECINT||','|| MI_RS.TIABANSUCSECINT||'
                                ,'|| MI_RS.TIACIUSECINT||'
                                ,TO_DATE(''' || TO_CHAR(MI_RS.TIAPRECOMFEC, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                ,'|| MI_RS.TIAPRECOMSEC||'';

                MI_VALORESSQL := '  '|| MI_RS.EMPCOD ||', '|| MI_RS.TISEC ||', '||   MI_CONSECUTIVO ||'
                                   ,'''|| MI_RS.TIAESTADO ||''', '''|| MI_RS.TIACOMTIP ||''' ,
                                   '|| MI_RS.TIACOMNUM ||'
                                   ,CONVERT(DATETIME,''' || (MI_RS.TIACOMFEC) || ''',103)
                                   , '''|| MI_RS.TIATERTIPID ||''', '''|| MI_RS.TIATERNUMID ||'''
                                   ,'''|| MI_RS.TIAPUCNROCTA ||''','|| MI_RS.TIAVALDEB ||','|| MI_RS.TIAVALCRE ||'
                                   ,'''|| MI_RS.TIAAGECOD ||''','|| MI_RS.TIAIMPPOR ||', '|| MI_RS.TIAIMPBASE ||'
                                   ,'''|| MI_RS.TIADESMOV ||''', '|| MI_RS.CONCEPT ||' ,'''||  MI_RS.TIANUMDOC ||''','''|| MI_RS.TIACIUCOD||'''
                                   ,'''|| MI_RS.TIAINTFINNUMUNI ||''','''||  MI_RS.TIAINTFINNUMREG||''','''||  MI_RS.TIABANSECINT ||'''
                                   , '''||  MI_RS.TIABANTIPCUE ||''','''||  MI_RS.TIABANNUMCUE ||''','''||  MI_RS.TIACODCONT ||'''
                                   ,'|| MI_RS.TIACODCONV||','|| MI_RS.TIACOMSECINT||','|| MI_RS.TIAAGESECINT||'
                                   ,'|| MI_RS.TIACENSECINT||','|| MI_RS.TIATERSECINT||','|| MI_RS.TIAPUCSECINT||','|| MI_RS.TIABANSUCSECINT||'
                                   ,'|| MI_RS.TIACIUSECINT||'
                                   ,CONVERT(DATETIME,''' || (MI_RS.TIAPRECOMFEC) || ''',103)
                                   ,'|| MI_RS.TIAPRECOMSEC||'';
                BEGIN
                    BEGIN
                        MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                      UN_ACCION  => 'I',
                                                      UN_CAMPOS  => MI_CAMPOS,
                                                      UN_VALORES => MI_VALORES);
    
                         MI_CONSECUTIVO := MI_CONSECUTIVO + 1;                              
                         MI_SCRIP:='INSERT INTO ' || MI_TABLA ||'(' || MI_CAMPOS || ')' || ' VALUES ' || '(' || MI_VALORESSQL || ');';
                         --UTL_FILE.PUT_LINE(MI_S_ARCHIVO2,CHR(10)||CHR(13) || MI_SCRIP);
                         MI_RETORNO:=MI_RETORNO||CHR(13)||CHR(10)||MI_SCRIP;
    
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
    
                    END;
                
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                        MI_REEMPLAZOS(1).CLAVE := 'ETAPA';
                        MI_REEMPLAZOS(1).VALOR := MI_STRETAPA;
                        MI_REEMPLAZOS(2).CLAVE := 'CODERROR';
                        MI_REEMPLAZOS(2).VALOR := SQLCODE;
                        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                                    UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTANOMINA,
                                                    UN_REEMPLAZOS => MI_REEMPLAZOS);
                END;                
                
         END;   
    END LOOP;

--****INSERTA APORTES PATRONALES TEMPORAL INTERFAZ
    BEGIN
        SELECT NVL(MAX(TISEC),0)
        INTO  MI_NUMEROCOMPAPORTES
        FROM  TEMPORALINTERFAZ
        WHERE EXTRACT(MONTH FROM TICREFEC)=UN_MES;

        IF MI_NUMEROCOMPAPORTES  = 0 THEN
            MI_NUMEROCOMPAPORTES := TO_NUMBER(UN_ANO || PCK_SYSMAN_UTL.FC_STRZERO(UN_MES, 2) || '0001');
        ELSE
            MI_NUMEROCOMPAPORTES := MI_NUMEROCOMPAPORTES  + 1;
        END IF;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_NUMEROCOMPAPORTES := TO_NUMBER(UN_ANO || PCK_SYSMAN_UTL.FC_STRZERO(UN_MES, 2) || '0001');
    END;

    FOR MI_RS IN
    (
      SELECT DISTINCT 1 EMPCOD, MI_NUMEROCOMPAPORTES TISEC, DESCRIPCION TIPRODES, 'I' TIESTADO , 'SA' TIPROIND, FECHA TICREFEC, UN_USUARIO TICREUSU,
      TO_DATE('01/01/1753','DD/MM/YYYY HH24:MI:SS') TIMODFEC, '' TIMODUSU,TO_DATE('01/01/1753','DD/MM/YYYY HH24:MI:SS') TIVALFEC, '' TIVALUSU, TO_DATE('01/01/1753','DD/MM/YYYY HH24:MI:SS') TIPROFEC, UN_USUARIO TIPROUSU,
      'L' TIINDLN, ''	TIANUUSU,	'' TIANUFEC,	TO_DATE('01/01/1753','DD/MM/YYYY HH24:MI:SS') TIFECVIG, 	'' TIANUCAU, '' TITIP
      FROM TEMP_PLANA_AJUSTES
      WHERE TIPO_CPTE=MI_TIPOCOMPROBANTE AND COMPROBANTE=MI_NUMEROCOMP AND TO_DATE('01/01/1753','DD/MM/YYYY HH24:MI:SS') IS NOT NULL) LOOP 

    BEGIN
            MI_TABLA := 'TEMPORALINTERFAZ';
            MI_CAMPOS := ' EMPCOD,TISEC,TIPRODES,TIESTADO,TIPROIND,TICREFEC,TICREUSU,
                           TIMODFEC,TIMODUSU,TIVALFEC,TIVALUSU,TIPROFEC,TIPROUSU,
                           TIINDLN,TIANUUSU,TIANUFEC,TIFECVIG,TIANUCAU,TITIP';

                MI_STRETAPA := '34';
                MI_VALORES := ''|| MI_RS.EMPCOD ||', '|| MI_RS.TISEC ||', '''|| MI_RS.TIPRODES ||'''
                                ,'''|| MI_RS.TIESTADO ||''', '''|| MI_RS.TIPROIND ||'''   
                                ,TO_DATE(''' || TO_CHAR(MI_RS.TICREFEC, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')    
                                , '''|| MI_RS.TICREUSU ||'''
                                ,TO_DATE(''' || TO_CHAR(MI_RS.TIMODFEC, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                ,'''|| MI_RS.TIMODUSU ||'''
                               ,TO_DATE(''' || TO_CHAR(MI_RS.TIVALFEC, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                ,'''|| MI_RS.TIVALUSU ||'''  
                                ,TO_DATE(''' || TO_CHAR(MI_RS.TIPROFEC, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'') 
                                ,'''|| MI_RS.TIPROUSU ||'''  , '''|| MI_RS.TIINDLN ||''', '''|| MI_RS.TIANUUSU ||''','''|| MI_RS.TIANUFEC ||'''                                
                                ,TO_DATE(''' || TO_CHAR(MI_RS.TIFECVIG, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                ,'''|| MI_RS.TIANUCAU ||'''  ,'''|| MI_RS.TITIP ||'''';

                MI_VALORESSQL := ''|| MI_RS.EMPCOD ||', '|| MI_RS.TISEC ||', '''|| MI_RS.TIPRODES ||'''
                                   ,'''|| MI_RS.TIESTADO ||''', '''|| MI_RS.TIPROIND ||'''   
                                   ,CONVERT(DATETIME,''' || (MI_RS.TICREFEC) || ''',103)     
                                   , '''|| MI_RS.TICREUSU ||'''
                                   ,CONVERT(DATETIME,''' || (MI_RS.TIMODFEC) || ''',103)
                                   ,'''|| MI_RS.TIMODUSU ||'''
                                  ,CONVERT(DATETIME,''' || (MI_RS.TIVALFEC) || ''',103)
                                   ,'''|| MI_RS.TIVALUSU ||'''  
                                   ,CONVERT(DATETIME,''' || (MI_RS.TIPROFEC) || ''',103)
                                   ,'''|| MI_RS.TIPROUSU ||'''  , '''|| MI_RS.TIINDLN ||''', '''|| MI_RS.TIANUUSU ||''','''|| MI_RS.TIANUFEC ||'''                                
                                   ,CONVERT(DATETIME,''' || (MI_RS.TIFECVIG) || ''',103)
                                   ,'''|| MI_RS.TIANUCAU ||'''  ,'''|| MI_RS.TITIP ||'''';

                BEGIN
                    BEGIN
                        MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                      UN_ACCION  => 'I',
                                                      UN_CAMPOS  => MI_CAMPOS,
                                                      UN_VALORES => MI_VALORES);
    
                         MI_SCRIP:='INSERT INTO ' || MI_TABLA ||'(' || MI_CAMPOS || ')' || ' VALUES ' || '(' || MI_VALORESSQL || ');';
                         --UTL_FILE.PUT_LINE(MI_S_ARCHIVO,CHR(10)||CHR(13) || MI_SCRIP);
                         MI_RETORNO:=MI_RETORNO||CHR(13)||CHR(10)||MI_SCRIP;
    
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                    END;
                
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                        MI_REEMPLAZOS(1).CLAVE := 'ETAPA';
                        MI_REEMPLAZOS(1).VALOR := MI_STRETAPA;
                        MI_REEMPLAZOS(2).CLAVE := 'CODERROR';
                        MI_REEMPLAZOS(2).VALOR := SQLCODE;
                        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                                    UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTANOMINA,
                                                    UN_REEMPLAZOS => MI_REEMPLAZOS);
                END;                
                
         END;   
    END LOOP;  

 --****INSERTA APORTES PATRONALES TABLA TEMPORAL INTERFAZ ASIENTO
  MI_CONSECUTIVO := 1;
    FOR MI_RS IN
    (
      SELECT EMPCOD,MI_NUMEROCOMPAPORTES TISEC,TIAESTADO,TIACOMTIP,TIACOMNUM,TIACOMFEC,TIATERTIPID,
             TIATERNUMID,TIAPUCNROCTA,TIAVALDEB,TIAVALCRE,TIAAGECOD,TIAIMPPOR,TIAIMPBASE,
             TIADESMOV, CONCEPT,TIANUMDOC,TIACIUCOD,TIAINTFINNUMUNI,TIAINTFINNUMREG,TIABANSECINT,
             TIABANTIPCUE,TIABANNUMCUE,TIACODCONT,TIACODCONV,TIACOMSECINT,TIAAGESECINT,
             TIACENSECINT,TIATERSECINT,TIAPUCSECINT,TIABANSUCSECINT,TIACIUSECINT,
             TIAPRECOMFEC,TIAPRECOMSEC
      FROM(
             SELECT 1 EMPCOD, MI_NUMEROCOMPAPORTES TISEC, 'I' TIAESTADO, TIPO_CPTE TIACOMTIP,MI_NUMEROCOMPAPORTES TIACOMNUM,
             TO_DATE(FECHA,'DD/MM/YYYY HH24:MI:SS') TIACOMFEC, TIPO_DOCUMENTO TIATERTIPID ,TERCERO TIATERNUMID,CUENTA TIAPUCNROCTA, SUM(VALOR_DEBITO) TIAVALDEB,
             SUM(VALOR_CREDITO) TIAVALCRE,UNIDADNEGOCIO TIAAGECOD,0 TIAIMPPOR,0 TIAIMPBASE, DESCRIPCION TIADESMOV, CONCEPTO CONCEPT ,COMPDOC TIANUMDOC, '68001' TIACIUCOD,
             0 TIAINTFINNUMUNI,0 TIAINTFINNUMREG,'NA' TIABANSECINT,'NA' TIABANTIPCUE,'' TIABANNUMCUE,'' TIACODCONT, 0 TIACODCONV,
             0 TIACOMSECINT,0 TIAAGESECINT,0 TIACENSECINT,0 TIATERSECINT, 0 TIAPUCSECINT, 0 TIABANSUCSECINT,0 TIACIUSECINT,
             TO_DATE('01/01/1753','DD/MM/YYYY HH24:MI:SS') TIAPRECOMFEC,0 TIAPRECOMSEC, CLASE
             FROM TEMP_PLANA_AJUSTES
             WHERE CLASE IN(8) AND ID_DE_CONCEPTO NOT BETWEEN 490 AND 499  
             AND TIPO_CPTE=MI_TIPOCOMPROBANTE AND COMPROBANTE=MI_NUMEROCOMP
             GROUP BY MI_NUMEROCOMPAPORTES,TIPO_CPTE,FECHA,TIPO_DOCUMENTO,TERCERO,CUENTA,UNIDADNEGOCIO,DESCRIPCION,CLASE, CONCEPTO, COMPDOC
      ORDER BY CLASE)) LOOP 

    BEGIN
            MI_TABLA := 'TEMPORALINTERFAZASIENTO';
            MI_CAMPOS := ' EMPCOD,TISEC,TIASEC,TIAESTADO,TIACOMTIP,TIACOMNUM,TIACOMFEC,TIATERTIPID,
                           TIATERNUMID,TIAPUCNROCTA,TIAVALDEB,TIAVALCRE,TIAAGECOD,TIAIMPPOR,TIAIMPBASE,
                           TIADESMOV, CONCEPT,TIANUMDOC,TIACIUCOD,TIAINTFINNUMUNI,TIAINTFINNUMREG,TIABANSECINT,
                           TIABANTIPCUE,TIABANNUMCUE,TIACODCONT,TIACODCONV,TIACOMSECINT,TIAAGESECINT,
                           TIACENSECINT,TIATERSECINT,TIAPUCSECINT,TIABANSUCSECINT,TIACIUSECINT,
                           TIAPRECOMFEC,TIAPRECOMSEC';

                MI_STRETAPA := '34';
                MI_VALORES := '  '|| MI_RS.EMPCOD ||', '|| MI_RS.TISEC ||', '||   MI_CONSECUTIVO ||'
                                ,'''|| MI_RS.TIAESTADO ||''', '''|| MI_RS.TIACOMTIP ||''' ,
                                '|| MI_RS.TIACOMNUM ||'
                                ,TO_DATE(''' || TO_CHAR(MI_RS.TIACOMFEC, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                , '''|| MI_RS.TIATERTIPID ||''', '''|| MI_RS.TIATERNUMID ||'''
                                ,'''|| MI_RS.TIAPUCNROCTA ||''','|| MI_RS.TIAVALDEB ||','|| MI_RS.TIAVALCRE ||'
                                ,'''|| MI_RS.TIAAGECOD ||''','|| MI_RS.TIAIMPPOR ||', '|| MI_RS.TIAIMPBASE ||'
                                ,'''|| MI_RS.TIADESMOV ||''', '|| MI_RS.CONCEPT ||','''||  MI_RS.TIANUMDOC ||''','''|| MI_RS.TIACIUCOD||'''
                                ,'''|| MI_RS.TIAINTFINNUMUNI ||''','''||  MI_RS.TIAINTFINNUMREG||''','''||  MI_RS.TIABANSECINT ||'''
                                , '''||  MI_RS.TIABANTIPCUE ||''','''||  MI_RS.TIABANNUMCUE ||''','''||  MI_RS.TIACODCONT ||'''
                                ,'|| MI_RS.TIACODCONV||','|| MI_RS.TIACOMSECINT||','|| MI_RS.TIAAGESECINT||'
                                ,'|| MI_RS.TIACENSECINT||','|| MI_RS.TIATERSECINT||','|| MI_RS.TIAPUCSECINT||','|| MI_RS.TIABANSUCSECINT||'
                                ,'|| MI_RS.TIACIUSECINT||'
                                ,TO_DATE(''' || TO_CHAR(MI_RS.TIAPRECOMFEC, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                ,'|| MI_RS.TIAPRECOMSEC||'';

                MI_VALORESSQL := '  '|| MI_RS.EMPCOD ||', '|| MI_RS.TISEC ||', '||   MI_CONSECUTIVO ||'
                                   ,'''|| MI_RS.TIAESTADO ||''', '''|| MI_RS.TIACOMTIP ||''' ,
                                   '|| MI_RS.TIACOMNUM ||'
                                   ,CONVERT(DATETIME,''' || (MI_RS.TIACOMFEC) || ''',103)
                                   , '''|| MI_RS.TIATERTIPID ||''', '''|| MI_RS.TIATERNUMID ||'''
                                   ,'''|| MI_RS.TIAPUCNROCTA ||''','|| MI_RS.TIAVALDEB ||','|| MI_RS.TIAVALCRE ||'
                                   ,'''|| MI_RS.TIAAGECOD ||''','|| MI_RS.TIAIMPPOR ||', '|| MI_RS.TIAIMPBASE ||'
                                   ,'''|| MI_RS.TIADESMOV ||''', '|| MI_RS.CONCEPT ||' ,'''||  MI_RS.TIANUMDOC ||''','''|| MI_RS.TIACIUCOD||'''
                                   ,'''|| MI_RS.TIAINTFINNUMUNI ||''','''||  MI_RS.TIAINTFINNUMREG||''','''||  MI_RS.TIABANSECINT ||'''
                                   , '''||  MI_RS.TIABANTIPCUE ||''','''||  MI_RS.TIABANNUMCUE ||''','''||  MI_RS.TIACODCONT ||'''
                                   ,'|| MI_RS.TIACODCONV||','|| MI_RS.TIACOMSECINT||','|| MI_RS.TIAAGESECINT||'
                                   ,'|| MI_RS.TIACENSECINT||','|| MI_RS.TIATERSECINT||','|| MI_RS.TIAPUCSECINT||','|| MI_RS.TIABANSUCSECINT||'
                                   ,'|| MI_RS.TIACIUSECINT||'
                                   ,CONVERT(DATETIME,''' || (MI_RS.TIAPRECOMFEC) || ''',103)
                                   ,'|| MI_RS.TIAPRECOMSEC||'';
                BEGIN
                    BEGIN
                        MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                      UN_ACCION  => 'I',
                                                      UN_CAMPOS  => MI_CAMPOS,
                                                      UN_VALORES => MI_VALORES);
    
                         MI_CONSECUTIVO := MI_CONSECUTIVO + 1;                              
                         MI_SCRIP:='INSERT INTO ' || MI_TABLA ||'(' || MI_CAMPOS || ')' || ' VALUES ' || '(' || MI_VALORESSQL || ');';
                         --UTL_FILE.PUT_LINE(MI_S_ARCHIVO2,CHR(10)||CHR(13) || MI_SCRIP);
                         MI_RETORNO:=MI_RETORNO||CHR(13)||CHR(10)||MI_SCRIP;
    
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
    
                    END;
                
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                        MI_REEMPLAZOS(1).CLAVE := 'ETAPA';
                        MI_REEMPLAZOS(1).VALOR := MI_STRETAPA;
                        MI_REEMPLAZOS(2).CLAVE := 'CODERROR';
                        MI_REEMPLAZOS(2).VALOR := SQLCODE;
                        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                                    UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTANOMINA,
                                                    UN_REEMPLAZOS => MI_REEMPLAZOS);
                END;                
         END;   
    END LOOP;

--****INSERTA BENEFICIOS A CORTO PLAZO TABLA TEMPORAL INTERFAZ
    BEGIN
        SELECT NVL(MAX(TISEC),0)
        INTO  MI_NUMEROCOMPCORTOPLAZO
        FROM  TEMPORALINTERFAZ
        WHERE EXTRACT(MONTH FROM TICREFEC)=UN_MES;

        IF MI_NUMEROCOMPCORTOPLAZO  = 0 THEN
            MI_NUMEROCOMPCORTOPLAZO := TO_NUMBER(UN_ANO || PCK_SYSMAN_UTL.FC_STRZERO(UN_MES, 2) || '0001');
        ELSE
            MI_NUMEROCOMPCORTOPLAZO := MI_NUMEROCOMPCORTOPLAZO  + 1;
        END IF;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_NUMEROCOMPCORTOPLAZO := TO_NUMBER(UN_ANO || PCK_SYSMAN_UTL.FC_STRZERO(UN_MES, 2) || '0001');
    END;

    FOR MI_RS IN
    (
      SELECT DISTINCT 1 EMPCOD, MI_NUMEROCOMPCORTOPLAZO TISEC, DESCRIPCION TIPRODES, 'I' TIESTADO , 'SA' TIPROIND, FECHA TICREFEC, UN_USUARIO TICREUSU,
      TO_DATE('01/01/1753','DD/MM/YYYY HH24:MI:SS') TIMODFEC, '' TIMODUSU,TO_DATE('01/01/1753','DD/MM/YYYY HH24:MI:SS') TIVALFEC, '' TIVALUSU, TO_DATE('01/01/1753','DD/MM/YYYY HH24:MI:SS') TIPROFEC, UN_USUARIO TIPROUSU,
      'L' TIINDLN, ''	TIANUUSU,	'' TIANUFEC,	TO_DATE('01/01/1753','DD/MM/YYYY HH24:MI:SS') TIFECVIG, 	'' TIANUCAU, '' TITIP
      FROM TEMP_PLANA_AJUSTES
      WHERE TIPO_CPTE=MI_TIPOCOMPROBANTE AND COMPROBANTE=MI_NUMEROCOMP AND TO_DATE('01/01/1753','DD/MM/YYYY HH24:MI:SS') IS NOT NULL) LOOP 

    BEGIN
            MI_TABLA := 'TEMPORALINTERFAZ';
            MI_CAMPOS := ' EMPCOD,TISEC,TIPRODES,TIESTADO,TIPROIND,TICREFEC,TICREUSU,
                           TIMODFEC,TIMODUSU,TIVALFEC,TIVALUSU,TIPROFEC,TIPROUSU,
                           TIINDLN,TIANUUSU,TIANUFEC,TIFECVIG,TIANUCAU,TITIP';

                MI_STRETAPA := '34';
                MI_VALORES := ''|| MI_RS.EMPCOD ||', '|| MI_RS.TISEC ||', '''|| MI_RS.TIPRODES ||'''
                                ,'''|| MI_RS.TIESTADO ||''', '''|| MI_RS.TIPROIND ||'''   
                                ,TO_DATE(''' || TO_CHAR(MI_RS.TICREFEC, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')    
                                , '''|| MI_RS.TICREUSU ||'''
                                ,TO_DATE(''' || TO_CHAR(MI_RS.TIMODFEC, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                ,'''|| MI_RS.TIMODUSU ||'''
                               ,TO_DATE(''' || TO_CHAR(MI_RS.TIVALFEC, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                ,'''|| MI_RS.TIVALUSU ||'''  
                                ,TO_DATE(''' || TO_CHAR(MI_RS.TIPROFEC, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'') 
                                ,'''|| MI_RS.TIPROUSU ||'''  , '''|| MI_RS.TIINDLN ||''', '''|| MI_RS.TIANUUSU ||''','''|| MI_RS.TIANUFEC ||'''                                
                                ,TO_DATE(''' || TO_CHAR(MI_RS.TIFECVIG, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                ,'''|| MI_RS.TIANUCAU ||'''  ,'''|| MI_RS.TITIP ||'''';

                MI_VALORESSQL := ''|| MI_RS.EMPCOD ||', '|| MI_RS.TISEC ||', '''|| MI_RS.TIPRODES ||'''
                                   ,'''|| MI_RS.TIESTADO ||''', '''|| MI_RS.TIPROIND ||'''   
                                   ,CONVERT(DATETIME,''' || (MI_RS.TICREFEC) || ''',103)     
                                   , '''|| MI_RS.TICREUSU ||'''
                                   ,CONVERT(DATETIME,''' || (MI_RS.TIMODFEC) || ''',103)
                                   ,'''|| MI_RS.TIMODUSU ||'''
                                  ,CONVERT(DATETIME,''' || (MI_RS.TIVALFEC) || ''',103)
                                   ,'''|| MI_RS.TIVALUSU ||'''  
                                   ,CONVERT(DATETIME,''' || (MI_RS.TIPROFEC) || ''',103)
                                   ,'''|| MI_RS.TIPROUSU ||'''  , '''|| MI_RS.TIINDLN ||''', '''|| MI_RS.TIANUUSU ||''','''|| MI_RS.TIANUFEC ||'''                                
                                   ,CONVERT(DATETIME,''' || (MI_RS.TIFECVIG) || ''',103)
                                   ,'''|| MI_RS.TIANUCAU ||'''  ,'''|| MI_RS.TITIP ||'''';

                BEGIN
                    BEGIN
                        MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                      UN_ACCION  => 'I',
                                                      UN_CAMPOS  => MI_CAMPOS,
                                                      UN_VALORES => MI_VALORES);
    
                         MI_SCRIP:='INSERT INTO ' || MI_TABLA ||'(' || MI_CAMPOS || ')' || ' VALUES ' || '(' || MI_VALORESSQL || ');';
                         --UTL_FILE.PUT_LINE(MI_S_ARCHIVO,CHR(10)||CHR(13) || MI_SCRIP);
                         MI_RETORNO:=MI_RETORNO||CHR(13)||CHR(10)||MI_SCRIP;
    
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                    END;
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                        MI_REEMPLAZOS(1).CLAVE := 'ETAPA';
                        MI_REEMPLAZOS(1).VALOR := MI_STRETAPA;
                        MI_REEMPLAZOS(2).CLAVE := 'CODERROR';
                        MI_REEMPLAZOS(2).VALOR := SQLCODE;
                        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                                    UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTANOMINA,
                                                    UN_REEMPLAZOS => MI_REEMPLAZOS);
                END;                
         END;   
    END LOOP;  

 --****INSERTA BENEFICIOS A CORTO PLAZO TABLA TEMPORAL INTERFAZ ASIENTO
  MI_CONSECUTIVO := 1;
    FOR MI_RS IN
    (
      SELECT EMPCOD,MI_NUMEROCOMPCORTOPLAZO TISEC,TIAESTADO,TIACOMTIP,TIACOMNUM,TIACOMFEC,TIATERTIPID,
             TIATERNUMID,TIAPUCNROCTA,TIAVALDEB,TIAVALCRE,TIAAGECOD,TIAIMPPOR,TIAIMPBASE,
             TIADESMOV,CONCEPT,TIANUMDOC,TIACIUCOD,TIAINTFINNUMUNI,TIAINTFINNUMREG,TIABANSECINT,
             TIABANTIPCUE,TIABANNUMCUE,TIACODCONT,TIACODCONV,TIACOMSECINT,TIAAGESECINT,
             TIACENSECINT,TIATERSECINT,TIAPUCSECINT,TIABANSUCSECINT,TIACIUSECINT,
             TIAPRECOMFEC,TIAPRECOMSEC
      FROM(
             SELECT 1 EMPCOD, MI_NUMEROCOMPCORTOPLAZO TISEC, 'I' TIAESTADO, TIPO_CPTE TIACOMTIP,MI_NUMEROCOMPCORTOPLAZO TIACOMNUM,
             TO_DATE(FECHA,'DD/MM/YYYY HH24:MI:SS') TIACOMFEC, TIPO_DOCUMENTO TIATERTIPID ,TERCERO TIATERNUMID,CUENTA TIAPUCNROCTA, SUM(VALOR_DEBITO) TIAVALDEB,
             SUM(VALOR_CREDITO) TIAVALCRE,UNIDADNEGOCIO TIAAGECOD,0 TIAIMPPOR,0 TIAIMPBASE, DESCRIPCION TIADESMOV, CONCEPTO CONCEPT  ,COMPDOC TIANUMDOC, '68001' TIACIUCOD,
             0 TIAINTFINNUMUNI,0 TIAINTFINNUMREG,'NA' TIABANSECINT,'NA' TIABANTIPCUE,'' TIABANNUMCUE,'' TIACODCONT, 0 TIACODCONV,
             0 TIACOMSECINT,0 TIAAGESECINT,0 TIACENSECINT,0 TIATERSECINT, 0 TIAPUCSECINT, 0 TIABANSUCSECINT,0 TIACIUSECINT,
             TO_DATE('01/01/1753','DD/MM/YYYY HH24:MI:SS') TIAPRECOMFEC,0 TIAPRECOMSEC, CLASE
             FROM TEMP_PLANA_AJUSTES
             WHERE CLASE IN(8) AND ID_DE_CONCEPTO BETWEEN 490 AND 499  
             AND TIPO_CPTE=MI_TIPOCOMPROBANTE AND COMPROBANTE=MI_NUMEROCOMP
             GROUP BY MI_NUMEROCOMPCORTOPLAZO,TIPO_CPTE,FECHA,TIPO_DOCUMENTO,TERCERO,CUENTA,UNIDADNEGOCIO,DESCRIPCION,CLASE, CONCEPTO, COMPDOC
      ORDER BY CLASE)) LOOP 

    BEGIN
            MI_TABLA := 'TEMPORALINTERFAZASIENTO';
            MI_CAMPOS := ' EMPCOD,TISEC,TIASEC,TIAESTADO,TIACOMTIP,TIACOMNUM,TIACOMFEC,TIATERTIPID,
                           TIATERNUMID,TIAPUCNROCTA,TIAVALDEB,TIAVALCRE,TIAAGECOD,TIAIMPPOR,TIAIMPBASE,
                           TIADESMOV, CONCEPT,TIANUMDOC,TIACIUCOD,TIAINTFINNUMUNI,TIAINTFINNUMREG,TIABANSECINT,
                           TIABANTIPCUE,TIABANNUMCUE,TIACODCONT,TIACODCONV,TIACOMSECINT,TIAAGESECINT,
                           TIACENSECINT,TIATERSECINT,TIAPUCSECINT,TIABANSUCSECINT,TIACIUSECINT,
                           TIAPRECOMFEC,TIAPRECOMSEC';

                MI_STRETAPA := '34';
                MI_VALORES := '  '|| MI_RS.EMPCOD ||', '|| MI_RS.TISEC ||', '||   MI_CONSECUTIVO ||'
                                ,'''|| MI_RS.TIAESTADO ||''', '''|| MI_RS.TIACOMTIP ||''' ,
                                '|| MI_RS.TIACOMNUM ||'
                                ,TO_DATE(''' || TO_CHAR(MI_RS.TIACOMFEC, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                , '''|| MI_RS.TIATERTIPID ||''', '''|| MI_RS.TIATERNUMID ||'''
                                ,'''|| MI_RS.TIAPUCNROCTA ||''','|| MI_RS.TIAVALDEB ||','|| MI_RS.TIAVALCRE ||'
                                ,'''|| MI_RS.TIAAGECOD ||''','|| MI_RS.TIAIMPPOR ||', '|| MI_RS.TIAIMPBASE ||'
                                ,'''|| MI_RS.TIADESMOV ||''', '|| MI_RS.CONCEPT ||','''||  MI_RS.TIANUMDOC ||''','''|| MI_RS.TIACIUCOD||'''
                                ,'''|| MI_RS.TIAINTFINNUMUNI ||''','''||  MI_RS.TIAINTFINNUMREG||''','''||  MI_RS.TIABANSECINT ||'''
                                , '''||  MI_RS.TIABANTIPCUE ||''','''||  MI_RS.TIABANNUMCUE ||''','''||  MI_RS.TIACODCONT ||'''
                                ,'|| MI_RS.TIACODCONV||','|| MI_RS.TIACOMSECINT||','|| MI_RS.TIAAGESECINT||'
                                ,'|| MI_RS.TIACENSECINT||','|| MI_RS.TIATERSECINT||','|| MI_RS.TIAPUCSECINT||','|| MI_RS.TIABANSUCSECINT||'
                                ,'|| MI_RS.TIACIUSECINT||'
                                ,TO_DATE(''' || TO_CHAR(MI_RS.TIAPRECOMFEC, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                ,'|| MI_RS.TIAPRECOMSEC||'';

                MI_VALORESSQL := '  '|| MI_RS.EMPCOD ||', '|| MI_RS.TISEC ||', '||   MI_CONSECUTIVO ||'
                                   ,'''|| MI_RS.TIAESTADO ||''', '''|| MI_RS.TIACOMTIP ||''' ,
                                   '|| MI_RS.TIACOMNUM ||'
                                   ,CONVERT(DATETIME,''' || (MI_RS.TIACOMFEC) || ''',103)
                                   , '''|| MI_RS.TIATERTIPID ||''', '''|| MI_RS.TIATERNUMID ||'''
                                   ,'''|| MI_RS.TIAPUCNROCTA ||''','|| MI_RS.TIAVALDEB ||','|| MI_RS.TIAVALCRE ||'
                                   ,'''|| MI_RS.TIAAGECOD ||''','|| MI_RS.TIAIMPPOR ||', '|| MI_RS.TIAIMPBASE ||'
                                   ,'''|| MI_RS.TIADESMOV ||''', '|| MI_RS.CONCEPT ||' ,'''||  MI_RS.TIANUMDOC ||''','''|| MI_RS.TIACIUCOD||'''
                                   ,'''|| MI_RS.TIAINTFINNUMUNI ||''','''||  MI_RS.TIAINTFINNUMREG||''','''||  MI_RS.TIABANSECINT ||'''
                                   , '''||  MI_RS.TIABANTIPCUE ||''','''||  MI_RS.TIABANNUMCUE ||''','''||  MI_RS.TIACODCONT ||'''
                                   ,'|| MI_RS.TIACODCONV||','|| MI_RS.TIACOMSECINT||','|| MI_RS.TIAAGESECINT||'
                                   ,'|| MI_RS.TIACENSECINT||','|| MI_RS.TIATERSECINT||','|| MI_RS.TIAPUCSECINT||','|| MI_RS.TIABANSUCSECINT||'
                                   ,'|| MI_RS.TIACIUSECINT||'
                                   ,CONVERT(DATETIME,''' || (MI_RS.TIAPRECOMFEC) || ''',103)
                                   ,'|| MI_RS.TIAPRECOMSEC||'';
                BEGIN
                    BEGIN
                    
                        MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                      UN_ACCION  => 'I',
                                                      UN_CAMPOS  => MI_CAMPOS,
                                                      UN_VALORES => MI_VALORES);
    
                         MI_CONSECUTIVO := MI_CONSECUTIVO + 1;                              
                         MI_SCRIP:='INSERT INTO ' || MI_TABLA ||'(' || MI_CAMPOS || ')' || ' VALUES ' || '(' || MI_VALORESSQL || ');';
                         --UTL_FILE.PUT_LINE(MI_S_ARCHIVO2,CHR(10)||CHR(13) || MI_SCRIP);
                         MI_RETORNO:=MI_RETORNO||CHR(13)||CHR(10)||MI_SCRIP;
    
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_INTERFAZ ;
                        
                    END;

                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                        MI_REEMPLAZOS(1).CLAVE := 'ETAPA';
                        MI_REEMPLAZOS(1).VALOR := MI_STRETAPA;
                        MI_REEMPLAZOS(2).CLAVE := 'CODERROR';
                        MI_REEMPLAZOS(2).VALOR := SQLCODE;
                        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                                    UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTANOMINA,
                                                    UN_REEMPLAZOS => MI_REEMPLAZOS);
                END;
         END;   
    END LOOP;
/*    
    MI_STRETAPA := '36';
    IF MI_AGREGAR = TRUE THEN
        MI_INCONSISTENCIADATO := TO_CLOB(PCK_CONTABILIZAR_NOMINA1.FC_VERIFICADATOSPLANA
            (UN_COMPANIA         => UN_COMPANIANOMINA
            ,UN_ANO              => UN_ANO
            ,UN_TIPOCOMPROBANTE  => MI_TIPOCOMPROBANTE
            ,UN_NUMERO           => MI_NUMEROCOMP));
        IF LENGTH(MI_INCONSISTENCIADATO) > 0 THEN
            MI_RTAPLANO := MI_RTAPLANO ||  TO_CLOB(MI_INCONSISTENCIADATO);
        END IF;
        MI_NUMEROCOMP := MI_NUMEROCOMP + 1;
        MI_CONSECUTIVO := 1;

    ELSE
        MI_RTAPLANO := 'No existe información para realizar el proceso';
    END IF;
*/
   -- RETURN MI_RTAPLANO;
      RETURN MI_RETORNO;


END FC_CONTABILIZARNOMINAHBUCARAMA;

END PCK_CONTABILIZAR_NOMINA3;