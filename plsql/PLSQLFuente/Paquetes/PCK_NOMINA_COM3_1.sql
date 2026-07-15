create or replace PACKAGE BODY "PCK_NOMINA_COM3" AS

--01
FUNCTION FC_CALCULAR_PORC_RETENCION
/*
NAME              : FC_CALCULAR_PORC_RETENCION
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
DATE MIGRADOR     : 09/07/2015
TIME              : 4:00 PM
SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio
DESCRIPTION       : Primera parte del calculo del porcentaje fijo de retencion en la fuente.
MODIFIER          : MIGUEL ANGEL ZANGUÑA HURTADO
DATE MODIFIED     : 11/01/2018
TIME              : 11:40 AM
MODIFICATIONS     : Se adicionan cambios de última versión de utilitario NOMINAH2017.12.03 MPV UNIFICADAS 137 21122017

@NAME:  calcularPorcentajeRetencion
@METHOD:  GET

*/
(
UN_FECHA_COMBO1         IN DATE,
UN_FECHA_COMBO2         IN DATE,
UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,
UN_ANO                  IN PCK_SUBTIPOS.TI_ANIO,
UN_PROCESO              IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
UN_CN309                IN PCK_SUBTIPOS.TI_LOGICO := 0,
UN_ANIOHASTADEDSALUD    IN PCK_SUBTIPOS.TI_LOGICO := 0,  --(MZANGUNA:14/01/2019)-Se agrega para tomar el año de la fecha final.
UN_USUARIO              IN PCK_SUBTIPOS.TI_USUARIO := ' '
 )
RETURN TYPEMATRIZ
AS
    MI_FECHA_TEMP                         DATE;
    MI_FECHA_COMBO1                       DATE;
    MI_FECHA_COMBO2                       DATE;
    MI_FECHA_1                            DATE;
    MI_FECHA_2                            DATE;
    MI_FECHA_3                            DATE;
    MI_FECHA_4                            DATE;
    MI_FECHAVARIABLE                      DATE;
    MI_VALOR_PUNTA                        PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_VALORMES                           PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_DDEVENGOS                          HISTORICOS.VALOR%TYPE;
    MI_DDESCUENTOS                        PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_DDEVENGOS2                         HISTORICOS.VALOR%TYPE;
    MI_DTDEVENGOS                         HISTORICOS.VALOR%TYPE;
    MI_DDESCUENTOS2                       PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_VDEDUCIBLE                         PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_VDEDUCIBLE1                        PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_VDEDUCIBLE2                        PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_VDEDUCIBLESALUD                    PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_DEDUCIBLEACUMULADO                 PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_DESCUENTOSALUD                     PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_TOTALMES                           PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_DIASLNR                            PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_DIAST                              PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_DEDUCIBLE30TOPE                    PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_DEDUCIBLE25P                       PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_MAXDEDUCIBLESALUD                  PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_VALORUVTMAXIMOSRENTAEXCENTA        PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_PORCDEDUCIBLEMAXSALUDEDU           PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_UVTANTERIOR                        PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_UVTACTUAL                          PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_UVTTOPEMAXIMOSALUDEDUCACION        PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_UVTTOPEMAXIMOVIVIENDA              PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_PORCENTAJERENTAEXCENTA             PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_VALLIMINGGRAVRETENCION             PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_FIP                                DATE;
    MI_FFP                                DATE;
    MI_D                                  VARCHAR2(10 CHAR);
    MI_I                                  PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_K                                  PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_L                                  PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_M                                  PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_N                                  PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_X                                  VARCHAR2(10 CHAR);
    MI_CCM                                VARCHAR2(30 CHAR);
    MI_IMESES                             PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_RESULTADO                          PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_RESULTADO_NUM_DCTO                 PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_RESULTADODEV                       PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
    MI_RESULTADO_NUM_DCTODEV              PERSONAL.NUMERO_DCTO%TYPE;
    MI_RESULTADODESC                      PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_RESULTADODESCSALUD                 PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_RESULTADODEV2                      PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
    MI_RESDOCDESCSALUD                    PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_DDESCUENTOSRENTA                   PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_OTROSPAGOSLABORALES                PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_SUMATOTALRENTAEXENTA               PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_UVTMAXIMOVIVIENDA                  PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;--UVTTOPEMAXIMOVIVIENDA = Nz(par("VALOR UVT MAXIMOS DEDUCIBLES VIVIENDA"), 100)
    MI_DEDUCIBLE2                         PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_PERSONALDEPENDIENTES               NUMBER DEFAULT 0;
    MI_PROMEDIO                           PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_RENTAEXCENTA                       PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_EXCENTO25                          PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_VPORCENTAJE                        PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_VVALOR                             PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_TOPE                               PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_SUELDOMENSUAL                      PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_CANTIDADUVT                        PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_PRO                                PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_PROC                               VARCHAR2(255 CHAR);
    MI_UPDATE                             PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_L_CONTADOR                         PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_AUXILIAR                           HISTORICOS.VALOR%TYPE;
    MI_MATRIZ                             TYPEMATRIZ;
    COMPANIA                              PERSONAL.COMPANIA%TYPE;
    NOMBRECOMPLETO                        PERSONAL.NOMBRECOMPLETO%TYPE;
    ID_EMPLEADO                           PERSONAL.ID_DE_EMPLEADO%TYPE;
    DOCUMENTO                             PERSONAL.NUMERO_DCTO%TYPE;
    INGRESO                               PERSONAL.FECHA_DE_INGRESO%TYPE;
    INGRESO_DIS                           PERSONAL.INGRESO_DISTRITO%TYPE;
    ESTADO_ACTUAL                         PERSONAL.ESTADO_ACTUAL%TYPE;
    PROCESORETENCION                      PERSONAL.PROCESORETENCION%TYPE;
    SALARIO                               PERSONAL.SALARIO_BASE_IBC%TYPE;
    RS                                    SYS_REFCURSOR;
    MI_CUENTAFILAS                        PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_QUERY                              VARCHAR2(32000 CHAR);
    MI_SELECT                             VARCHAR2(32000 CHAR);
    MI_WHERE                              VARCHAR2(32000 CHAR);
    MI_CONTADOR                           VARCHAR2(50 CHAR);
    MI_PARAPLICARENTA                     PARAMETRO.VALOR%TYPE;
    MI_PARLIMMAXRENTAANUAL                PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PARVALUVTMAXMEDICINAPREPA          PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_DESCPENSIONVOLAFCTOTAL             PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_DESCPENSIONVOLAFCNMESES            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PARPORCMAXIPERSONALACARGO          PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PARVALUVTMACPERSONALACARGO         PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PARPORCENLIBRERETENCION            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;

    MI_MSG                                PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_TABLA                              PCK_SUBTIPOS.TI_TABLA;
    MI_MERGEUSING                         PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE                        PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE                        PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_MERGENOEXIS                        PCK_SUBTIPOS.TI_MERGENOEXISTE;
    MI_VALORNOVEDAD                       PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;

    MI_FILAS                              PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_VALOR                              PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CN379                              PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_INDRENTAEXCENTACN                  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;

BEGIN
    EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_DATE_FORMAT=''DD/MM/YYYY''';
    MI_VALORUVTMAXIMOSRENTAEXCENTA := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA ,UN_NOMBRE => 'VALOR UVT MAXIMOS RENTA EXCENTA' ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA ,UN_FECHA_PAR => SYSDATE),240));
    MI_PORCDEDUCIBLEMAXSALUDEDU  := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA ,UN_NOMBRE => 'PORCENTAJE DEDUCIBLE MAXIMO SALUD/EDUCACION' ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA ,UN_FECHA_PAR =>SYSDATE),0));
    MI_UVTANTERIOR :=  TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA ,UN_NOMBRE => 'VALOR UVT AÑO ANTERIOR' ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA ,UN_FECHA_PAR => SYSDATE), 22054));
    MI_UVTACTUAL := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA ,UN_NOMBRE => 'VALOR UVT ACTUAL' ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA ,UN_FECHA_PAR => SYSDATE), 23763));
    MI_UVTTOPEMAXIMOSALUDEDUCACION := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA ,UN_NOMBRE => 'VALOR UVT MAXIMOS DEDUCIBLES SALUD/EDUCACION' ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA ,UN_FECHA_PAR => SYSDATE), 4600));
    MI_UVTTOPEMAXIMOVIVIENDA := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA ,UN_NOMBRE => 'VALOR UVT MAXIMOS DEDUCIBLES VIVIENDA' ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA ,UN_FECHA_PAR => SYSDATE), 100));

    MI_PORCENTAJERENTAEXCENTA := 1 - TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA ,UN_NOMBRE => 'VALOR PORCENTAJE APLICAR PROCESO RETEFUENTE' ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA ,UN_FECHA_PAR => SYSDATE)  ,0));
    MI_VALLIMINGGRAVRETENCION := TO_NUMBER( NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA ,UN_NOMBRE => 'VALOR LIMITE INGRESOS GRAVADOS RETENCION' ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA ,UN_FECHA_PAR => SYSDATE) / 100, 0.3));
    MI_PARAPLICARENTA := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA ,UN_NOMBRE => 'APLICA RENTA INDEMNIZACIONES' ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA ,UN_FECHA_PAR => SYSDATE), 'NO');
    MI_PARLIMMAXRENTAANUAL :=  TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA ,UN_NOMBRE => 'LIMITE MAXIMO RENTA EXCENTA ANUAL' ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA ,UN_FECHA_PAR => SYSDATE),3800));

    MI_PARVALUVTMAXMEDICINAPREPA :=  TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA ,UN_NOMBRE => 'VALOR UVT MAXIMOS DEDUCIBLES MEDICINA PREPAGADAS' ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA ,UN_FECHA_PAR => SYSDATE),16));
    MI_PARPORCMAXIPERSONALACARGO := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA ,UN_NOMBRE => 'PORCENTAJE MAXIMO PERSONAL A CARGO' ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA ,UN_FECHA_PAR => SYSDATE),0));

    MI_PARVALUVTMACPERSONALACARGO :=  TO_NUMBER(NVL( PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA ,UN_NOMBRE => 'VALOR UVT MAXIMOS PERSONAL A CARGO' ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA ,UN_FECHA_PAR => SYSDATE)  ,16));
    MI_PARPORCENLIBRERETENCION := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA ,UN_NOMBRE => 'PORCENTAJE LIBRE DE RETENCION' ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA ,UN_FECHA_PAR => SYSDATE), 25));

    MI_MATRIZ := TYPEMATRIZ();
    MI_K :=  4;
    MI_L :=  6;
    MI_FECHA_COMBO1 := TO_DATE(UN_FECHA_COMBO1,'DD/MM/YYYY');
    MI_FECHA_COMBO2 := TO_DATE(UN_FECHA_COMBO2,'DD/MM/YYYY');

    IF MI_FECHA_COMBO1 > MI_FECHA_COMBO2 THEN
        MI_FECHA_TEMP := MI_FECHA_COMBO1;
        MI_FECHA_COMBO1 := MI_FECHA_COMBO2;
        MI_FECHA_COMBO2 := MI_FECHA_TEMP;
    END IF;

    IF TO_CHAR(MI_FECHA_COMBO1, 'YYYY') >= 2012 THEN
        MI_FECHA_1 := TO_DATE(MI_FECHA_COMBO1,'DD/MM/YYYY');
        MI_FECHA_2 := TO_DATE('31/12/'||TO_CHAR(MI_FECHA_COMBO1, 'YYYY'),'DD/MM/YYYY');
        MI_FECHA_3 := TO_DATE('01/01/'||TO_CHAR(MI_FECHA_COMBO2, 'YYYY'),'DD/MM/YYYY');
        MI_FECHA_4 := MI_FECHA_COMBO2;
    ELSE
        MI_FECHA_1 := MI_FECHA_COMBO1;
        MI_FECHA_2 := MI_FECHA_COMBO2;
        MI_FECHA_3 := NULL;
        MI_FECHA_4 := NULL;
    END IF;
    --MOD POR JM 13/01/2025 buscar primera fecha de ingreso cuando tiene varios IDS 636
    MI_SELECT := 'SELECT  COMPANIA,
                          NOMBRECOMPLETO NOMBRECOMPLETO,
                          ID_DE_EMPLEADO ID_DE_EMPLEADO,
                          NUMERO_DCTO DOCUMENTO,
                          TO_DATE(TO_CHAR(FIRST_VALUE(PERSONAL.FECHA_DE_INGRESO) OVER ( PARTITION BY PERSONAL.NUMERO_DCTO ORDER BY PERSONAL.FECHA_DE_INGRESO ASC ),''DD/MM/YYYY''),''DD/MM/YYYY'') INGRESO,
                          INGRESO_DISTRITO INGRESO_DIS,
                          ESTADO_ACTUAL ESTADO_ACTUAL,
                          PROCESORETENCION PROCESORETENCION,
                          SALARIO_BASE_IBC SUELDO
                  FROM   PERSONAL ';

        --(MZANGUNA:11/01/2019)-Se adiciona condición para que no incluya empleados que no esten dentro del rango de la generación del informe
        MI_WHERE:=' WHERE PERSONAL.COMPANIA       = '''||UN_COMPANIA||'''
                      AND PERSONAL.ID_DE_EMPLEADO   NOT IN(0)
                      AND PERSONAL.ESTADO_ACTUAL   NOT IN(2)
                      AND TRUNC(PERSONAL.FECHA_DE_INGRESO) <= TO_DATE(''' || TO_CHAR(MI_FECHA_COMBO2, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'') ';


    --20180615_207@MZANGUNA - NJIMENEZ Si es segundo semestre solo debe mostrar los tipo retención porcentaje fijo
    IF PCK_SYSMAN_UTL.FC_MES(MI_FECHA_COMBO1) = 6 THEN
        MI_WHERE := MI_WHERE || ' AND PROCESORETENCION = 2 ';
    END IF;
    MI_WHERE := MI_WHERE || 'ORDER  BY PERSONAL.NOMBRECOMPLETO';

    MI_QUERY   := MI_SELECT||''||MI_WHERE;
    MI_L_CONTADOR:=1;
    MI_M := 8;

    --(MZANGUNA:15/01/2019)-Se deja para validar si los conceptos 370 al 378 no tienen el indicador IND_RENTA_EXENTA tome el concepto 379
    BEGIN
        SELECT SUM(ABS(IND_RENTA_EXENTA))
        INTO   MI_INDRENTAEXCENTACN
        FROM   CONCEPTOS
        WHERE  COMPANIA = UN_COMPANIA
          AND  ID_DE_CONCEPTO BETWEEN 370 AND 378;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_INDRENTAEXCENTACN := 0;
    END;
    --
    OPEN RS FOR MI_QUERY;
    LOOP
        FETCH RS
        INTO COMPANIA,
             NOMBRECOMPLETO,
             ID_EMPLEADO,
             DOCUMENTO,
             INGRESO,
             INGRESO_DIS,
             ESTADO_ACTUAL,
             PROCESORETENCION,
             SALARIO;
        EXIT WHEN RS%NOTFOUND;
        MI_CCM := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA ,UN_NOMBRE => 'CONCEPTOS NO CONSTITUTIVOS DE RENTA' ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA ,UN_FECHA_PAR => SYSDATE);
        MI_DIASLNR := 0;
        MI_DIAST := 0;
        MI_FIP := '';
        MI_DDEVENGOS := 0;
        MI_DDEVENGOS2 := 0;
        MI_DDESCUENTOS := 0;
        MI_DDESCUENTOS2:= 0;

        IF ESTADO_ACTUAL = 3 THEN
            MI_L_CONTADOR:=MI_L_CONTADOR+1;
            CONTINUE;
        END IF;

        IF INGRESO > MI_FECHA_1 THEN
            MI_X := 'F';
        ELSE
            MI_X := '';
        END IF;

        IF INGRESO > MI_FECHA_1 THEN
            IF INGRESO < MI_FECHA_2 THEN
                --Revisar cálculo de personal que ingreso dentro del periodo liquidado, parte --PARTE--, empleado: --CODIGO--   --NOMEMPLEADO--, Fecha de ingreso: --FECHAINGRESO--.
                /*MI_MSG(1).CLAVE := 'PARTE';
                MI_MSG(1).VALOR := '1';
                MI_MSG(2).CLAVE := 'CODIGO';
                MI_MSG(2).VALOR := ID_EMPLEADO;
                MI_MSG(3).CLAVE := 'FECHAINGRESO';
                MI_MSG(3).VALOR := INGRESO;

                PCK_NOMINA_COM7.PR_ALERTA
                (UN_COMPANIA     => UN_COMPANIA
                ,UN_MENSAJE_COD  => PCK_ERRORES.ERRRR_ALERCLCRTNCCALCPERSL
                ,UN_REEMPLAZOS   => MI_MSG
                ,UN_PROCESO      => UN_PROCESO
                ,UN_ANO          => (TO_NUMBER(TO_CHAR(MI_FECHA_COMBO1, 'YYYY')))
                ,UN_MES          => (TO_NUMBER(TO_CHAR(MI_FECHA_COMBO1, 'MM')))
                ,UN_PERIODO      => 1
                ,UN_USER         => UN_USUARIO);*/

                MI_FIP := INGRESO;
                MI_D := TO_CHAR(LAST_DAY(INGRESO),'DD');
                MI_FFP :=TO_CHAR(MI_D)||'/'||PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO => TO_CHAR(INGRESO,'MM') ,UN_LONGITUD => 2)||'/'||TO_CHAR(INGRESO,'YYYY');
                MI_FIP := CASE WHEN  MI_FIP >= MI_FECHA_COMBO2 THEN MI_FECHA_COMBO2  ELSE MI_FIP END;
                MI_FFP := CASE WHEN  MI_FFP >= MI_FECHA_COMBO2 THEN MI_FECHA_COMBO2  ELSE MI_FFP END;

                MI_VALORMES := 0;
                MI_VALOR_PUNTA:= 0;

                BEGIN
                    SELECT DISTINCT SUM(HISTORICOS.VALOR) TOTAL,
                           PERSONAL.NUMERO_DCTO DOCUMENTO
                    INTO MI_RESULTADODEV, MI_RESULTADO_NUM_DCTODEV
                    FROM PERSONAL INNER JOIN HISTORICOS
                             ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
                            AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                        INNER JOIN CONCEPTOS
                             ON CONCEPTOS.COMPANIA        = HISTORICOS.COMPANIA
                            AND CONCEPTOS.ID_DE_CONCEPTO = HISTORICOS.ID_DE_CONCEPTO
                        INNER JOIN PERIODOS
                             ON PERIODOS.COMPANIA       = HISTORICOS.COMPANIA
                            AND PERIODOS.ID_DE_PROCESO = HISTORICOS.ID_DE_PROCESO
                            AND PERIODOS.ANO           = HISTORICOS.ANO
                            AND PERIODOS.MES           = HISTORICOS.MES
                            AND PERIODOS.PERIODO       = HISTORICOS.PERIODO
                    WHERE HISTORICOS.COMPANIA  = UN_COMPANIA
                      AND PERIODOS.FECHAFINAL BETWEEN MI_FIP AND MI_FECHA_2
                      AND PERIODOS.ACUMULADO <> 0
                      AND CONCEPTOS.DEDRETENCION <> 0
                      AND PERSONAL.NUMERO_DCTO    = DOCUMENTO
                    GROUP BY PERSONAL.NUMERO_DCTO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_RESULTADODEV := 0;
                    MI_RESULTADO_NUM_DCTODEV := ' ';
                END;

                IF MI_RESULTADODEV <> 0 THEN
                    MI_DDEVENGOS := MI_RESULTADODEV + MI_VALORMES - MI_VALOR_PUNTA;
                ELSE
                    MI_DDEVENGOS:= 0 + MI_VALORMES - MI_VALOR_PUNTA;
                END IF;

                --'ACUMULAR DESCUENTOS PARTE1
                BEGIN
                    SELECT SUM(HISTORICOS.VALOR) TOTAL
                    INTO MI_DDESCUENTOS
                    FROM PERSONAL INNER JOIN HISTORICOS
                             ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
                            AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                        INNER JOIN CONCEPTOS
                             ON CONCEPTOS.COMPANIA        = HISTORICOS.COMPANIA
                            AND CONCEPTOS.ID_DE_CONCEPTO = HISTORICOS.ID_DE_CONCEPTO
                        INNER JOIN PERIODOS
                             ON PERIODOS.COMPANIA       = HISTORICOS.COMPANIA
                            AND PERIODOS.ID_DE_PROCESO = HISTORICOS.ID_DE_PROCESO
                            AND PERIODOS.ANO           = HISTORICOS.ANO
                            AND PERIODOS.MES           = HISTORICOS.MES
                            AND PERIODOS.PERIODO       = HISTORICOS.PERIODO
                    WHERE HISTORICOS.COMPANIA  = UN_COMPANIA
                      AND PERIODOS.FECHAFINAL BETWEEN MI_FECHA_1 AND MI_FECHA_2
                      AND PERIODOS.ACUMULADO <> 0
                      AND CONCEPTOS.DEDRETENCION <> 0
                      AND PERSONAL.NUMERO_DCTO    = DOCUMENTO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_DDESCUENTOS:= 0;
                END;

                IF MI_DDEVENGOS = 0 AND MI_DDESCUENTOS > 0 THEN
                    BEGIN
                        SELECT SUM(HISTORICOS.VALOR) TOTAL
                        INTO   MI_DDEVENGOS
                        FROM PERSONAL INNER JOIN HISTORICOS
                                 ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
                                AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                            INNER JOIN CONCEPTOS
                                 ON CONCEPTOS.COMPANIA        = HISTORICOS.COMPANIA
                                AND CONCEPTOS.ID_DE_CONCEPTO = HISTORICOS.ID_DE_CONCEPTO
                            INNER JOIN PERIODOS
                                 ON PERIODOS.COMPANIA       = HISTORICOS.COMPANIA
                                AND PERIODOS.ID_DE_PROCESO = HISTORICOS.ID_DE_PROCESO
                                AND PERIODOS.ANO           = HISTORICOS.ANO
                                AND PERIODOS.MES           = HISTORICOS.MES
                                AND PERIODOS.PERIODO       = HISTORICOS.PERIODO
                        WHERE HISTORICOS.COMPANIA  = UN_COMPANIA
                          AND PERIODOS.FECHAFINAL BETWEEN MI_FECHA_1 AND MI_FECHA_2
                          AND PERIODOS.ACUMULADO     <> 0
                          AND CONCEPTOS.DEVRETENCION <> 0
                          AND CONCEPTOS.IND_RENTA_EXENTA = 0        --(MZANGUNA:18/01/2019)-Se excluyen los devengos de la renta excenta.
                          AND PERSONAL.NUMERO_DCTO    = DOCUMENTO;
                          --AND PERSONAL.ID_DE_EMPLEADO = ID_EMPLEADO; --JM CC 3548
                    EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_DDEVENGOS:= 0;
                    END;
                END IF;
                -- PARTE 2
                BEGIN
                    SELECT SUM(HISTORICOS.VALOR) TOTAL
                    INTO   MI_DDEVENGOS2
                    FROM PERSONAL INNER JOIN HISTORICOS
                             ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
                            AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                        INNER JOIN CONCEPTOS
                             ON CONCEPTOS.COMPANIA        = HISTORICOS.COMPANIA
                            AND CONCEPTOS.ID_DE_CONCEPTO = HISTORICOS.ID_DE_CONCEPTO
                        INNER JOIN PERIODOS
                             ON PERIODOS.COMPANIA       = HISTORICOS.COMPANIA
                            AND PERIODOS.ID_DE_PROCESO = HISTORICOS.ID_DE_PROCESO
                            AND PERIODOS.ANO           = HISTORICOS.ANO
                            AND PERIODOS.MES           = HISTORICOS.MES
                            AND PERIODOS.PERIODO       = HISTORICOS.PERIODO
                    WHERE HISTORICOS.COMPANIA  = UN_COMPANIA
                      AND PERIODOS.FECHAFINAL BETWEEN MI_FECHA_3 AND MI_FECHA_4
                      AND PERIODOS.ACUMULADO     <> 0
                      AND CONCEPTOS.DEVRETENCION <> 0
                      AND CONCEPTOS.IND_RENTA_EXENTA = 0        --(MZANGUNA:18/01/2019)-Se excluyen los devengos de la renta excenta.
                      AND PERSONAL.NUMERO_DCTO    = DOCUMENTO
                    GROUP BY PERSONAL.NUMERO_DCTO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_DDEVENGOS2:= 0;
                END;

                --'ACUMULAR DESCUENTOS PARTE2
                BEGIN
                    SELECT SUM(HISTORICOS.VALOR) TOTAL
                    INTO   MI_DDESCUENTOS2
                    FROM PERSONAL INNER JOIN HISTORICOS
                             ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
                            AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                        INNER JOIN CONCEPTOS
                             ON CONCEPTOS.COMPANIA        = HISTORICOS.COMPANIA
                            AND CONCEPTOS.ID_DE_CONCEPTO = HISTORICOS.ID_DE_CONCEPTO
                        INNER JOIN PERIODOS
                             ON PERIODOS.COMPANIA       = HISTORICOS.COMPANIA
                            AND PERIODOS.ID_DE_PROCESO = HISTORICOS.ID_DE_PROCESO
                            AND PERIODOS.ANO           = HISTORICOS.ANO
                            AND PERIODOS.MES           = HISTORICOS.MES
                            AND PERIODOS.PERIODO       = HISTORICOS.PERIODO
                    WHERE HISTORICOS.COMPANIA  = UN_COMPANIA
                      AND PERIODOS.FECHAFINAL BETWEEN MI_FECHA_3 AND MI_FECHA_4
                      AND PERIODOS.ACUMULADO     <> 0
                      AND CONCEPTOS.DEDRETENCION <> 0
                      AND PERSONAL.NUMERO_DCTO    = DOCUMENTO
                    GROUP BY PERSONAL.NUMERO_DCTO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_DDESCUENTOS2:= 0;
                END;
            ELSE
                --SEGUNDA PARTE SOLAMENTE
                --Revisar cálculo de personal que ingreso dentro del periodo liquidado, parte --PARTE--, empleado: --CODIGO--   --NOMEMPLEADO--, Fecha de ingreso: --FECHAINGRESO--.
                /*MI_MSG(1).CLAVE := 'PARTE';
                MI_MSG(1).VALOR := '2';
                MI_MSG(2).CLAVE := 'CODIGO';
                MI_MSG(2).VALOR := ID_EMPLEADO;
                MI_MSG(3).CLAVE := 'FECHAINGRESO';
                MI_MSG(3).VALOR := INGRESO;

                PCK_NOMINA_COM7.PR_ALERTA
                (UN_COMPANIA     => UN_COMPANIA
                ,UN_MENSAJE_COD  => PCK_ERRORES.ERRRR_ALERCLCRTNCCALCPERSL
                ,UN_REEMPLAZOS   => MI_MSG
                ,UN_PROCESO      => UN_PROCESO
                ,UN_ANO          => (TO_NUMBER(TO_CHAR(MI_FECHA_COMBO1, 'YYYY')))
                ,UN_MES          => (TO_NUMBER(TO_CHAR(MI_FECHA_COMBO1, 'MM')))
                ,UN_PERIODO      => 1
                ,UN_USER         => UN_USUARIO);*/

                MI_FIP := INGRESO;
                MI_D := TO_CHAR(LAST_DAY(INGRESO),'DD');
                MI_FFP:= TO_DATE(TO_CHAR(MI_D)||'/'||PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO => TO_CHAR(INGRESO,'MM') ,UN_LONGITUD => 2)||'/'||TO_CHAR(INGRESO,'YYYY'),'DD/MM/YYYY');
                MI_FIP:= CASE WHEN MI_FIP >= MI_FECHA_COMBO2 THEN MI_FECHA_COMBO2 ELSE MI_FIP END;
                MI_FFP:= CASE WHEN MI_FFP >= MI_FECHA_COMBO2 THEN MI_FECHA_COMBO2 ELSE MI_FFP END;

                BEGIN
                    SELECT SUM(HISTORICOS.VALOR) TOTAL
                    INTO   MI_VALOR_PUNTA
                    FROM PERSONAL INNER JOIN HISTORICOS
                             ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
                            AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                        INNER JOIN CONCEPTOS
                             ON CONCEPTOS.COMPANIA        = HISTORICOS.COMPANIA
                            AND CONCEPTOS.ID_DE_CONCEPTO = HISTORICOS.ID_DE_CONCEPTO
                    INNER JOIN PERIODOS
                             ON PERIODOS.COMPANIA       = HISTORICOS.COMPANIA
                            AND PERIODOS.ID_DE_PROCESO = HISTORICOS.ID_DE_PROCESO
                            AND PERIODOS.ANO           = HISTORICOS.ANO
                            AND PERIODOS.MES           = HISTORICOS.MES
                            AND PERIODOS.PERIODO       = HISTORICOS.PERIODO
                    WHERE PERSONAL.COMPANIA  = UN_COMPANIA
                      AND PERIODOS.FECHAFINAL BETWEEN MI_FIP AND MI_FFP
                      AND PERIODOS.ACUMULADO     <> 0
                      AND CONCEPTOS.DEVRETENCION <> 0
                      AND CONCEPTOS.IND_RENTA_EXENTA = 0        --(MZANGUNA:18/01/2019)-Se excluyen los devengos de la renta excenta.
                      AND PERSONAL.NUMERO_DCTO = DOCUMENTO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_VALOR_PUNTA:= 0;
                END;

                IF MI_VALOR_PUNTA > 0 THEN
                    MI_VALORMES := MI_VALOR_PUNTA ;--NVL(PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => (MI_VALOR_PUNTA/(30 - TO_NUMBER(TO_CHAR (INGRESO,'DD')) +1 )*30)
                    --,UN_PRECISION => 0),0);
                ELSE
                    MI_VALORMES:=0;
                END IF;

                BEGIN
                    SELECT SUM(HISTORICOS.VALOR) TOTAL
                    INTO   MI_RESULTADODEV2
                    FROM   PERSONAL INNER JOIN HISTORICOS
                             ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
                            AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                        INNER JOIN CONCEPTOS
                             ON CONCEPTOS.COMPANIA        = HISTORICOS.COMPANIA
                            AND CONCEPTOS.ID_DE_CONCEPTO = HISTORICOS.ID_DE_CONCEPTO
                    INNER JOIN PERIODOS
                             ON PERIODOS.COMPANIA       = HISTORICOS.COMPANIA
                            AND PERIODOS.ID_DE_PROCESO = HISTORICOS.ID_DE_PROCESO
                            AND PERIODOS.ANO           = HISTORICOS.ANO
                            AND PERIODOS.MES           = HISTORICOS.MES
                            AND PERIODOS.PERIODO       = HISTORICOS.PERIODO
                    WHERE HISTORICOS.COMPANIA  = UN_COMPANIA
                      AND PERIODOS.FECHAFINAL BETWEEN MI_FECHA_3 AND MI_FECHA_4
                      AND PERIODOS.ACUMULADO     <> 0
                      AND CONCEPTOS.DEVRETENCION <> 0
                      AND CONCEPTOS.IND_RENTA_EXENTA = 0        --(MZANGUNA:18/01/2019)-Se excluyen los devengos de la renta excenta.
                      AND PERSONAL.NUMERO_DCTO = DOCUMENTO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_RESULTADODEV2 := 0;
                END;

                IF MI_RESULTADODEV2 <> 0 THEN
                    MI_DDEVENGOS2  := MI_RESULTADODEV2 + MI_VALORMES - MI_VALOR_PUNTA;
                ELSE
                    MI_DDEVENGOS2:= 0 + MI_VALORMES - MI_VALOR_PUNTA;
                END IF;

                --'ACUMULAR DESCUENTOS JULIO 11/2002
                BEGIN
                    SELECT SUM(HISTORICOS.VALOR) TOTAL
                    INTO   MI_DDESCUENTOS2
                    FROM   PERSONAL INNER JOIN HISTORICOS
                             ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
                            AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                        INNER JOIN CONCEPTOS
                             ON CONCEPTOS.COMPANIA        = HISTORICOS.COMPANIA
                            AND CONCEPTOS.ID_DE_CONCEPTO = HISTORICOS.ID_DE_CONCEPTO
                    INNER JOIN PERIODOS
                             ON PERIODOS.COMPANIA       = HISTORICOS.COMPANIA
                            AND PERIODOS.ID_DE_PROCESO = HISTORICOS.ID_DE_PROCESO
                            AND PERIODOS.ANO           = HISTORICOS.ANO
                            AND PERIODOS.MES           = HISTORICOS.MES
                            AND PERIODOS.PERIODO       = HISTORICOS.PERIODO
                    WHERE HISTORICOS.COMPANIA  = UN_COMPANIA
                      AND PERIODOS.FECHAFINAL BETWEEN MI_FECHA_3 AND MI_FECHA_4
                      AND PERIODOS.ACUMULADO     <> 0
                      AND CONCEPTOS.DEDRETENCION <> 0
                      AND PERSONAL.NUMERO_DCTO    = DOCUMENTO
                    GROUP BY PERSONAL.NUMERO_DCTO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_DDESCUENTOS2:= 0;
                END;

                IF MI_DDEVENGOS2 = 0 AND MI_DDESCUENTOS2 > 0 THEN
                    BEGIN
                        SELECT SUM(HISTORICOS.VALOR) TOTAL
                        INTO   MI_DDEVENGOS2
                        FROM   PERSONAL INNER JOIN HISTORICOS
                                 ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
                                AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                            INNER JOIN CONCEPTOS
                                 ON CONCEPTOS.COMPANIA        = HISTORICOS.COMPANIA
                                AND CONCEPTOS.ID_DE_CONCEPTO = HISTORICOS.ID_DE_CONCEPTO
                        INNER JOIN PERIODOS
                                 ON PERIODOS.COMPANIA       = HISTORICOS.COMPANIA
                                AND PERIODOS.ID_DE_PROCESO = HISTORICOS.ID_DE_PROCESO
                                AND PERIODOS.ANO           = HISTORICOS.ANO
                                AND PERIODOS.MES           = HISTORICOS.MES
                                AND PERIODOS.PERIODO       = HISTORICOS.PERIODO
                        WHERE HISTORICOS.COMPANIA  = UN_COMPANIA
                          AND PERIODOS.FECHAFINAL BETWEEN MI_FECHA_1 AND MI_FECHA_2
                          AND PERIODOS.ACUMULADO     <> 0
                          AND CONCEPTOS.DEVRETENCION <> 0
                          AND CONCEPTOS.IND_RENTA_EXENTA = 0        --(MZANGUNA:18/01/2019)-Se excluyen los devengos de la renta excenta.
                          AND PERSONAL.NUMERO_DCTO    = DOCUMENTO
                        GROUP BY PERSONAL.NUMERO_DCTO;
                    EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_DDEVENGOS2:= 0;
                    END;
                END IF;
            END IF;
        ELSE
            --PARTE 1
            BEGIN
                SELECT SUM(HISTORICOS.VALOR) TOTAL
                INTO   MI_DDEVENGOS
                FROM   PERSONAL INNER JOIN HISTORICOS
                         ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
                        AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                INNER JOIN CONCEPTOS
                         ON CONCEPTOS.COMPANIA        = HISTORICOS.COMPANIA
                        AND CONCEPTOS.ID_DE_CONCEPTO = HISTORICOS.ID_DE_CONCEPTO
                INNER JOIN PERIODOS
                         ON PERIODOS.COMPANIA       = HISTORICOS.COMPANIA
                        AND PERIODOS.ID_DE_PROCESO = HISTORICOS.ID_DE_PROCESO
                        AND PERIODOS.ANO           = HISTORICOS.ANO
                        AND PERIODOS.MES           = HISTORICOS.MES
                        AND PERIODOS.PERIODO       = HISTORICOS.PERIODO
                WHERE HISTORICOS.COMPANIA  = UN_COMPANIA
                  AND PERIODOS.FECHAFINAL BETWEEN MI_FECHA_1 AND MI_FECHA_2
                  AND PERIODOS.ACUMULADO     <> 0
                  AND CONCEPTOS.DEVRETENCION <> 0
                  AND CONCEPTOS.IND_RENTA_EXENTA = 0        --(MZANGUNA:18/01/2019)-Se excluyen los devengos de la renta excenta.
                  AND PERSONAL.NUMERO_DCTO    = DOCUMENTO
                  --AND PERSONAL.ID_DE_EMPLEADO = ID_EMPLEADO; --JM CC 3548
                GROUP BY PERSONAL.NUMERO_DCTO;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_DDEVENGOS:= 0;
            END;

            BEGIN
                SELECT SUM(HISTORICOS.VALOR) TOTAL
                INTO   MI_DDESCUENTOS
                FROM   PERSONAL INNER JOIN HISTORICOS
                         ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
                        AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                    INNER JOIN CONCEPTOS
                         ON CONCEPTOS.COMPANIA        = HISTORICOS.COMPANIA
                        AND CONCEPTOS.ID_DE_CONCEPTO = HISTORICOS.ID_DE_CONCEPTO
                    INNER JOIN PERIODOS
                         ON PERIODOS.COMPANIA       = HISTORICOS.COMPANIA
                        AND PERIODOS.ID_DE_PROCESO = HISTORICOS.ID_DE_PROCESO
                        AND PERIODOS.ANO           = HISTORICOS.ANO
                        AND PERIODOS.MES           = HISTORICOS.MES
                        AND PERIODOS.PERIODO       = HISTORICOS.PERIODO
                WHERE HISTORICOS.COMPANIA  = UN_COMPANIA
                  AND PERIODOS.FECHAFINAL BETWEEN MI_FECHA_1 AND MI_FECHA_2
                  AND PERIODOS.ACUMULADO     <> 0
                  AND CONCEPTOS.DEDRETENCION <> 0
                  AND PERSONAL.NUMERO_DCTO    = DOCUMENTO
                  --AND PERSONAL.ID_DE_EMPLEADO = ID_EMPLEADO; --JM CC 3548
                GROUP BY PERSONAL.NUMERO_DCTO;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_DDESCUENTOS:= 0;
            END;

            BEGIN
                SELECT SUM(HISTORICOS.VALOR) TOTAL
                INTO   MI_DDEVENGOS2
                FROM   PERSONAL INNER JOIN HISTORICOS
                         ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
                        AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                    INNER JOIN CONCEPTOS
                         ON CONCEPTOS.COMPANIA        = HISTORICOS.COMPANIA
                        AND CONCEPTOS.ID_DE_CONCEPTO = HISTORICOS.ID_DE_CONCEPTO
                    INNER JOIN PERIODOS
                         ON PERIODOS.COMPANIA       = HISTORICOS.COMPANIA
                        AND PERIODOS.ID_DE_PROCESO = HISTORICOS.ID_DE_PROCESO
                        AND PERIODOS.ANO           = HISTORICOS.ANO
                        AND PERIODOS.MES           = HISTORICOS.MES
                        AND PERIODOS.PERIODO       = HISTORICOS.PERIODO
                WHERE HISTORICOS.COMPANIA  = UN_COMPANIA
                  AND PERIODOS.FECHAFINAL BETWEEN MI_FECHA_3 AND MI_FECHA_4
                  AND PERIODOS.ACUMULADO     <> 0
                  AND CONCEPTOS.DEVRETENCION <> 0
                  AND CONCEPTOS.IND_RENTA_EXENTA = 0        --(MZANGUNA:18/01/2019)-Se excluyen los devengos de la renta excenta.
                  AND PERSONAL.NUMERO_DCTO    = DOCUMENTO
                GROUP BY PERSONAL.NUMERO_DCTO;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_DDEVENGOS2:= 0;
            END;

            --' Localizar el total descuentos por empleado
            BEGIN
                SELECT SUM(HISTORICOS.VALOR) TOTAL
                INTO   MI_DDESCUENTOS2
                FROM   PERSONAL INNER JOIN HISTORICOS
                         ON HISTORICOS.COMPANIA       = PERSONAL.COMPANIA
                        AND HISTORICOS.ID_DE_EMPLEADO=PERSONAL.ID_DE_EMPLEADO
                    INNER JOIN CONCEPTOS
                         ON CONCEPTOS.COMPANIA       = HISTORICOS.COMPANIA
                        AND CONCEPTOS.ID_DE_CONCEPTO=HISTORICOS.ID_DE_CONCEPTO
                    INNER JOIN PERIODOS
                         ON PERIODOS.COMPANIA      =HISTORICOS.COMPANIA
                        AND PERIODOS.ID_DE_PROCESO=HISTORICOS.ID_DE_PROCESO
                        AND PERIODOS.ANO          = HISTORICOS.ANO
                        AND PERIODOS.MES          = HISTORICOS.MES
                        AND PERIODOS.PERIODO      = HISTORICOS.PERIODO
                WHERE HISTORICOS.COMPANIA = UN_COMPANIA
                  AND PERIODOS.FECHAFINAL BETWEEN MI_FECHA_3 AND MI_FECHA_4
                  AND PERIODOS.ACUMULADO     <> 0
                  AND CONCEPTOS.DEDRETENCION <> 0
                  AND PERSONAL.NUMERO_DCTO    = DOCUMENTO
                GROUP BY PERSONAL.NUMERO_DCTO;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_DDESCUENTOS2:= 0;
            END;
        END IF;

        MI_DIASLNR := 0;    --(MZANGUNA:11/01/2019)-Se sube esta parte del código para calcular los meses trabajados.---
       --MI_DIAST := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN => INGRESO ,UN_FECHAFIN => MI_FECHA_COMBO2); --COMENTADO POR JM 13/01/2025 636
       --JM 13/01/2025 DIAS TRABAJADOS VARIOS IDS 636
       BEGIN 
        SELECT 
        SUM(PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN => GREATEST(PERSONAL.FECHA_DE_INGRESO, TO_DATE(MI_FECHA_COMBO1)), 
            UN_FECHAFIN =>  CASE WHEN PERSONAL.ESTADO_ACTUAL = 1
                THEN 
                    TO_DATE(MI_FECHA_COMBO2)
                ELSE 
                    PERSONAL.FECHATERCONTRATO
                END )) AS DIAST
                INTO MI_DIAST
                FROM   PERSONAL  WHERE PERSONAL.COMPANIA       = UN_COMPANIA
                      AND PERSONAL.ID_DE_EMPLEADO  NOT IN(0)
                      AND PERSONAL.ESTADO_ACTUAL   NOT IN(2)
                      AND NUMERO_DCTO = DOCUMENTO
                      AND TRUNC(PERSONAL.FECHA_DE_INGRESO) <= TO_DATE(MI_FECHA_COMBO2,'DD/MM/YYYY') 
                      AND (TRUNC(PERSONAL.FECHATERCONTRATO) >= TO_DATE(MI_FECHA_COMBO1,'DD/MM/YYYY') 
                      OR (PERSONAL.ESTADO_ACTUAL = 1 AND PERSONAL.FECHATERCONTRATO IS NULL))
                      ORDER  BY PERSONAL.NOMBRECOMPLETO;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_DIAST := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN => INGRESO ,UN_FECHAFIN => MI_FECHA_COMBO2);
            END;
         --JM FIN 13/01/2025 DIAS TRABAJADOS VARIOS IDS 636

        IF MI_DIAST > 360 THEN
            MI_DIAST := 360;
            --Columna 44
            MI_MATRIZ.EXTEND();
            MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,43,TO_CHAR(MI_FECHA_COMBO1,'DD/MM/YYYY'));
            --Columna 45
            MI_MATRIZ.EXTEND();
            MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,44,TO_CHAR(MI_FECHA_COMBO2,'DD/MM/YYYY'));
        ELSE
            MI_DIAST:= MI_DIAST;
            --Columna 44
            MI_MATRIZ.EXTEND();
            MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,43,TO_CHAR(INGRESO,'DD/MM/YYYY'));
            --Columna 45
            MI_MATRIZ.EXTEND();
            MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,44,TO_CHAR(MI_FECHA_COMBO2,'DD/MM/YYYY'));
        END IF;

        MI_DIASLNR:=0;
        BEGIN
            SELECT SUM(HISTORICOS.VALOR) TOTAL
            INTO   MI_DIASLNR
            FROM   PERSONAL INNER JOIN HISTORICOS
                     ON HISTORICOS.COMPANIA       = PERSONAL.COMPANIA
                    AND HISTORICOS.ID_DE_EMPLEADO=PERSONAL.ID_DE_EMPLEADO
                INNER JOIN CONCEPTOS
                     ON CONCEPTOS.COMPANIA       = HISTORICOS.COMPANIA
                    AND CONCEPTOS.ID_DE_CONCEPTO=HISTORICOS.ID_DE_CONCEPTO
                INNER JOIN PERIODOS
                     ON PERIODOS.COMPANIA      =HISTORICOS.COMPANIA
                    AND PERIODOS.ID_DE_PROCESO=HISTORICOS.ID_DE_PROCESO
                    AND PERIODOS.ANO          = HISTORICOS.ANO
                    AND PERIODOS.MES          = HISTORICOS.MES
                    AND PERIODOS.PERIODO      = HISTORICOS.PERIODO
            WHERE HISTORICOS.COMPANIA = UN_COMPANIA
              AND PERIODOS.FECHAFINAL BETWEEN MI_FECHA_COMBO1 AND MI_FECHA_COMBO2
              AND PERIODOS.ACUMULADO        <> 0
              AND PERSONAL.NUMERO_DCTO       = DOCUMENTO
              AND HISTORICOS.ID_DE_CONCEPTO IN(356,357,359,339); --MOD JM 13/01/2025 se agregan dias en comision 636
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_DIASLNR:= 0;
        END;
        --FIN (MZANGUNA:11/01/2019)-Se sube esta parte del código para calcular los meses trabajados.

        MI_IMESES := TRUNC(MONTHS_BETWEEN(MI_FECHA_COMBO2,INGRESO)) + 1;
        IF MI_IMESES < 13 THEN
            --MI_IMESES := (PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(UN_FECHA1 => INGRESO ,UN_FECHA2 => MI_FECHA_COMBO2) + 1);
            --(MZANGUNA:11/01/2019)-Solicitud NJIMENEZ, Para empleados que llevan menos de un año trabajando se debe dejar el valor exacto en meses
            MI_IMESES := PCK_SYSMAN_UTL.FC_ROUND(NVL((MI_DIAST - NVL(MI_DIASLNR,0)), 0) * 12/ 360, 2);

        ELSE
            MI_IMESES := MI_IMESES + CASE WHEN MI_VALORMES > 0 AND PCK_SYSMAN_UTL.FC_DIA(INGRESO) > 1 THEN 1 ELSE 0 END ;
        END IF;


        MI_IMESES := CASE WHEN MI_IMESES >= 12 THEN 13 ELSE MI_IMESES END;

        IF MI_IMESES <= 0 THEN
            MI_IMESES  :=1;
        END IF;

        MI_VDEDUCIBLE1 := 0;
        MI_VDEDUCIBLE2 := 0;
        MI_DEDUCIBLE25P := 0;

        MI_FECHAVARIABLE :=  MI_FECHA_1;
        MI_DEDUCIBLEACUMULADO := 0;
        MI_DEDUCIBLE30TOPE := 0;

        --MZ AQUI QUEDE, Comienzo promedios

        MI_M := MI_M + 1;  --Fila
        MI_N := 0;         --Columna


        --Columna 1
        MI_MATRIZ.EXTEND();
        MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,MI_N,TO_CHAR(NVL(DOCUMENTO,0)));

        --Columna 2
        MI_N := MI_N + 1;
        MI_MATRIZ.EXTEND();
        MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,MI_N,TO_CHAR(NVL(NOMBRECOMPLETO,0)));

        --Columna 3
        MI_N := MI_N + 1;
        MI_AUXILIAR := (MI_DDEVENGOS + MI_DDEVENGOS2);
        MI_MATRIZ.EXTEND();
        MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,MI_N,NVL(MI_AUXILIAR,0));

        MI_N := MI_N+4;

        --Columna 12
        MI_MATRIZ.EXTEND();
        MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,11,NVL((MI_DIAST - NVL(MI_DIASLNR,0)), 0));  --20180607_0900:@eamaya Adición de NVL a MI_DIASLNR porque la diferencia de nulls es 0

        --Columna 13
        MI_MATRIZ.EXTEND();
        MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,12,NVL(MI_DIASLNR, 0));

        --Columna 6
        MI_MATRIZ.EXTEND();
        MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,5,0);

        BEGIN
            --(MZANGUNA:15/01/2019)-Se quita concepto 379 y se agrega validación para conceptos con renta excenta del 370 al 379
            SELECT SUM(HISTORICOS.VALOR) TOTALRENTA, SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 379 THEN HISTORICOS.VALOR ELSE 0 END) TOTALINCP
            INTO   MI_DDESCUENTOSRENTA, MI_CN379
            FROM   PERSONAL INNER JOIN HISTORICOS
                     ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
                    AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                INNER JOIN CONCEPTOS
                     ON CONCEPTOS.COMPANIA        = HISTORICOS.COMPANIA
                    AND CONCEPTOS.ID_DE_CONCEPTO = HISTORICOS.ID_DE_CONCEPTO
                INNER JOIN PERIODOS
                     ON PERIODOS.COMPANIA       = HISTORICOS.COMPANIA
                    AND PERIODOS.ID_DE_PROCESO = HISTORICOS.ID_DE_PROCESO
                    AND PERIODOS.ANO           = HISTORICOS.ANO
                    AND PERIODOS.MES           = HISTORICOS.MES
                    AND PERIODOS.PERIODO       = HISTORICOS.PERIODO
            WHERE HISTORICOS.COMPANIA  = UN_COMPANIA
              AND PERIODOS.FECHAFINAL BETWEEN MI_FECHA_COMBO1 AND MI_FECHA_COMBO2
              AND PERIODOS.ACUMULADO          <> 0
              AND (    CONCEPTOS.IND_RENTA_EXENTA <> 0
                    OR CASE WHEN MI_PARAPLICARENTA = 'SI' THEN RENTA_EXENTA_INDEMNIZACION ELSE 0 END <> 0
                    --OR (CONCEPTOS.ID_DE_CONCEPTO IN (379) AND MI_INDRENTAEXCENTACN = 0) --(MZANGUNA:15/01/2019)-Se deja para validar si los conceptos 370 al 378 no tienen el indicador IND_RENTA_EXENTA tome el concepto 379
                   )
              AND PERSONAL.NUMERO_DCTO        = DOCUMENTO
              --AND (CONCEPTOS.IND_RENTA_EXENTA <> 0 OR CONCEPTOS.ID_DE_CONCEPTO IN(169,177,569,577,379) OR CASE WHEN MI_PARAPLICARENTA = 'SI' THEN RENTA_EXENTA_INDEMNIZACION ELSE 0 END <> 0)
            GROUP BY PERSONAL.NUMERO_DCTO;

        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_DDESCUENTOSRENTA := 0;
            MI_CN379 := 0;
        END;
        --20180615_782@MZANGUNA, Se cambia condición de igual a mayor
        IF MI_DDESCUENTOSRENTA > 0 THEN
            --Columna 21
            MI_MATRIZ.EXTEND();
            MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,20,MI_DDESCUENTOSRENTA);

            --Columna 4 --(MZANGUNA:11/01/2019)-Se cambia para que tome solo los conceptos con indicador OTROSPAGOSLABORALES
            /*MI_MATRIZ.EXTEND();
            MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,3,MI_DDESCUENTOSRENTA);*/

            --Columna 3
            --(MZANGUNA:15/01/2019):Petición de WILLIAM FERNANDO Se sumen las licencias a la columna 3
            --((MZANGUNA:18/01/2019)-Se excluyen los devengos de la renta excenta.)
            MI_VALOR := NVL( (((MI_DDEVENGOS + MI_DDEVENGOS2) ) + MI_CN379) ,0);
            MI_MATRIZ.EXTEND();
            MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,2,MI_VALOR);
        ELSE
            --Columna 21
            MI_MATRIZ.EXTEND();
            MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,20,0);

        END IF;

        --Columna 7
        MI_MATRIZ.EXTEND();
        MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,6,0);
        --Columna 8
        MI_MATRIZ.EXTEND();
        MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,7,0);
        --Columna 8
        MI_MATRIZ.EXTEND();
        MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,8,0);
        --Columna 10
        MI_MATRIZ.EXTEND();
        MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,8,0);
        --Columna 16
        MI_MATRIZ.EXTEND();
        MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,15,0);
        --Columna 17
        MI_MATRIZ.EXTEND();
        MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,16,0);
        --Columna 18
        MI_MATRIZ.EXTEND();
        MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,17,0);
        --Columna 20
        MI_MATRIZ.EXTEND();
        MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,19,0);
        --Columna 15
        MI_MATRIZ.EXTEND();
        MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,14,PCK_NOMINA_COM3.FC_MIDEPENDIENTES(UN_COMPANIA => UN_COMPANIA ,UN_PARAMETRO => NULL ,UN_IDEMPLEADO => ID_EMPLEADO));

        MI_N := MI_N + 1;

        --Columna 29
        MI_MATRIZ.EXTEND();
        MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,28,NVL(MI_IMESES, 0));

        MI_SUMATOTALRENTAEXENTA := 0;
        --Columna 10
        MI_MATRIZ.EXTEND();
        MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,9,NVL(MI_DDESCUENTOS + MI_DDESCUENTOS2, 0));

        BEGIN
            SELECT SUM(HISTORICOS.VALOR) TOTAL , COUNT(HISTORICOS.MES) NMESES
            INTO   MI_DESCPENSIONVOLAFCTOTAL, MI_DESCPENSIONVOLAFCNMESES
            FROM   PERSONAL INNER JOIN HISTORICOS
                     ON HISTORICOS.COMPANIA       = PERSONAL.COMPANIA
                    AND HISTORICOS.ID_DE_EMPLEADO=PERSONAL.ID_DE_EMPLEADO
                INNER JOIN CONCEPTOS
                     ON CONCEPTOS.COMPANIA       = HISTORICOS.COMPANIA
                    AND CONCEPTOS.ID_DE_CONCEPTO=HISTORICOS.ID_DE_CONCEPTO
                INNER JOIN PERIODOS
                     ON PERIODOS.COMPANIA      =HISTORICOS.COMPANIA
                    AND PERIODOS.ID_DE_PROCESO=HISTORICOS.ID_DE_PROCESO
                    AND PERIODOS.ANO          = HISTORICOS.ANO
                    AND PERIODOS.MES          = HISTORICOS.MES
                    AND PERIODOS.PERIODO      = HISTORICOS.PERIODO
            WHERE HISTORICOS.COMPANIA = UN_COMPANIA
              AND PERIODOS.FECHAFINAL BETWEEN MI_FECHA_COMBO1 AND MI_FECHA_COMBO2
              AND PERIODOS.ACUMULADO        <> 0
              AND PERSONAL.NUMERO_DCTO       = DOCUMENTO
              AND HISTORICOS.ID_DE_CONCEPTO IN(124,127);
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_DESCPENSIONVOLAFCTOTAL := 0;
            MI_DESCPENSIONVOLAFCNMESES := 0;
        END;

        IF MI_DESCPENSIONVOLAFCNMESES > 0 THEN
            IF MI_DESCPENSIONVOLAFCTOTAL > PCK_SYSMAN_UTL.FC_ROUND( (MI_PARLIMMAXRENTAANUAL * MI_UVTACTUAL), 0) THEN
                MI_SUMATOTALRENTAEXENTA := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (NVL(MI_PARLIMMAXRENTAANUAL,3800) * MI_UVTACTUAL) /12 ,UN_PRECISION => 0);
                --Columna 20
                MI_MATRIZ.EXTEND();
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,19,MI_SUMATOTALRENTAEXENTA);
            ELSE
                --Columna 20
                MI_MATRIZ.EXTEND();
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,19,NVL(MI_DESCPENSIONVOLAFCTOTAL,0));
                MI_SUMATOTALRENTAEXENTA := NVL(MI_DESCPENSIONVOLAFCTOTAL,0);
            END IF;
        ELSE
            --Columna 20
            MI_MATRIZ.EXTEND();
            MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,19,0);
        END IF;


        --(MZANGUNA:11/01/2019)-Se cambia para que tome solo los conceptos con indicador OTROSPAGOSLABORALES
        BEGIN
            SELECT SUM(HISTORICOS.VALOR) TOTALRENTA
            INTO   MI_OTROSPAGOSLABORALES
            FROM   PERSONAL INNER JOIN HISTORICOS
                     ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
                    AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                INNER JOIN CONCEPTOS
                     ON CONCEPTOS.COMPANIA        = HISTORICOS.COMPANIA
                    AND CONCEPTOS.ID_DE_CONCEPTO = HISTORICOS.ID_DE_CONCEPTO
                INNER JOIN PERIODOS
                     ON PERIODOS.COMPANIA       = HISTORICOS.COMPANIA
                    AND PERIODOS.ID_DE_PROCESO = HISTORICOS.ID_DE_PROCESO
                    AND PERIODOS.ANO           = HISTORICOS.ANO
                    AND PERIODOS.MES           = HISTORICOS.MES
                    AND PERIODOS.PERIODO       = HISTORICOS.PERIODO
            WHERE HISTORICOS.COMPANIA  = UN_COMPANIA
              AND PERIODOS.FECHAFINAL BETWEEN MI_FECHA_COMBO1 AND MI_FECHA_COMBO2
              AND PERIODOS.ACUMULADO         <> 0
              AND ((CONCEPTOS.OTROSPAGOSLABORALES <> 0
              AND CONCEPTOS.IND_RENTA_EXENTA = 0)--(MZANGUNA:18/01/2019)-Se excluyen los devengos de la renta excenta.
              OR (CONCEPTOS.DEVRETENCION <> 0
              AND CONCEPTOS.IND_RENTA_EXENTA <> 0))-- JM 7749473  19/07/2024  --JM CC612 07/01/2024 faltaban parentesis
              AND PERSONAL.NUMERO_DCTO        = DOCUMENTO
            GROUP BY PERSONAL.NUMERO_DCTO;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_OTROSPAGOSLABORALES:=0;
        END;
        --(MZANGUNA:11/01/2019)-Se cambia para que tome solo los conceptos con indicador OTROSPAGOSLABORALES
        IF MI_OTROSPAGOSLABORALES > 0 THEN
            --Columna 4
            MI_MATRIZ.EXTEND();
            MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,3,MI_OTROSPAGOSLABORALES);
        ELSE
            --Columna 4
            MI_MATRIZ.EXTEND();
            MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,3,MI_OTROSPAGOSLABORALES);
        END IF;

        MI_N := MI_N + 2;
        --(MZANGUNA:16/01/2019):La columna otros pagos debe sumar WGONZALEZ
        MI_DTDEVENGOS := (MI_DDEVENGOS+MI_DDEVENGOS2 + MI_OTROSPAGOSLABORALES);
        MI_DTDEVENGOS := PCK_SYSMAN_UTL.FC_ROUND(MI_DTDEVENGOS,0);

        --DEDUCIBLE MEDICINA PREPAGADA
        MI_VDEDUCIBLE := NVL(PCK_NOMINA_COM3.FC_DEDUCIBLEPREPAGADA(UN_COMPANIA => UN_COMPANIA ,UN_IDDEEMPLEADO => ID_EMPLEADO),0);
        IF MI_VDEDUCIBLE > (PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => MI_PARVALUVTMAXMEDICINAPREPA * TO_NUMBER(MI_UVTACTUAL) ,UN_PRECISION => 0)  * CASE WHEN MI_IMESES >= 13 THEN 12 ELSE  MI_IMESES END) THEN
            MI_MAXDEDUCIBLESALUD:= PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => MI_PARVALUVTMAXMEDICINAPREPA * MI_UVTACTUAL ,UN_PRECISION => 0) * 12;
        ELSE
            MI_VDEDUCIBLE := MI_VDEDUCIBLE * CASE WHEN MI_IMESES >= 13 THEN 12 ELSE MI_IMESES END;
            MI_MAXDEDUCIBLESALUD:= MI_VDEDUCIBLE;
        END IF;


        IF MI_VDEDUCIBLE > (PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => MI_PARVALUVTMAXMEDICINAPREPA * MI_UVTACTUAL ,UN_PRECISION => 0) * CASE WHEN MI_IMESES >= 13 THEN 12 ELSE MI_IMESES END ) THEN
            MI_VDEDUCIBLE1:=PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => MI_PARVALUVTMAXMEDICINAPREPA * MI_UVTACTUAL ,UN_PRECISION => 0) * 12;
        ELSE
            MI_VDEDUCIBLE1 := MI_VDEDUCIBLE;
        END IF;

        --PROCEDIMIENTOS VIVIENDA
        MI_VDEDUCIBLE := PCK_NOMINA_COM3.FC_DEDUCIBLEV(UN_COMPANIA => UN_COMPANIA ,UN_IDEMPLEADO => ID_EMPLEADO);
        IF MI_VDEDUCIBLE >0 AND MI_VDEDUCIBLE> ((MI_UVTTOPEMAXIMOVIVIENDA*MI_UVTACTUAL) * CASE WHEN MI_IMESES >= 13 THEN 12 ELSE MI_IMESES END ) THEN
            MI_VDEDUCIBLE2 := (MI_UVTTOPEMAXIMOVIVIENDA * MI_UVTACTUAL) * 12;
        ELSE
            MI_VDEDUCIBLE2 := MI_VDEDUCIBLE * CASE WHEN MI_IMESES >= 13 THEN 12 ELSE MI_IMESES END ;
        END IF;

        IF MI_VDEDUCIBLE2 > 0 AND MI_VDEDUCIBLE2 > ((MI_UVTTOPEMAXIMOVIVIENDA * MI_UVTACTUAL) * CASE WHEN MI_IMESES >= 13 THEN 12 ELSE MI_IMESES END) THEN
            MI_VDEDUCIBLE2:=(MI_UVTTOPEMAXIMOVIVIENDA * MI_UVTACTUAL) * 12;
        END IF;

        MI_VDEDUCIBLE := (MI_VDEDUCIBLE1 + MI_VDEDUCIBLE2);
        MI_N := MI_N + 2;

        --Columna 17
        MI_MATRIZ.EXTEND();
        MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,16,NVL(MI_VDEDUCIBLE1,0)); --'MEDICINA PREPAGADA

        MI_N := MI_N + 1;
        --Columna 16
        MI_MATRIZ.EXTEND();
        MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,15,NVL(MI_VDEDUCIBLE2,0)); --'VIVIENDA

        MI_N := 11;

        --MI_VDEDUCIBLESALUD := PCK_NOMINA.FC_DEDUCIBLESALUD(UN_COMPANIA => UN_COMPANIA ,UN_IDDEEMPLEADO => ID_EMPLEADO ,UN_IDPROCESO => UN_PROCESO);
        MI_DESCUENTOSALUD := 0;
        MI_DESCUENTOSALUD := MI_VDEDUCIBLESALUD;


        IF UN_ANIOHASTADEDSALUD = -2 THEN 
            BEGIN
                SELECT DISTINCT SUM(HISTORICOS.VALOR) TOTAL
                INTO   MI_DESCUENTOSALUD
                FROM   PERSONAL INNER JOIN HISTORICOS
                         ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
                        AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                    INNER JOIN CONCEPTOS
                         ON CONCEPTOS.COMPANIA        = HISTORICOS.COMPANIA
                        AND CONCEPTOS.ID_DE_CONCEPTO = HISTORICOS.ID_DE_CONCEPTO
                    INNER JOIN PERIODOS
                         ON PERIODOS.COMPANIA       = HISTORICOS.COMPANIA
                        AND PERIODOS.ID_DE_PROCESO = HISTORICOS.ID_DE_PROCESO
                        AND PERIODOS.ANO           = HISTORICOS.ANO
                        AND PERIODOS.MES           = HISTORICOS.MES
                        AND PERIODOS.PERIODO       = HISTORICOS.PERIODO
                WHERE HISTORICOS.COMPANIA  = UN_COMPANIA
                  AND PERIODOS.FECHAFINAL BETWEEN MI_FECHA_COMBO1 AND MI_FECHA_COMBO2
                  AND PERIODOS.ACUMULADO <> 0
                  AND PERSONAL.NUMERO_DCTO    = DOCUMENTO
                  AND CONCEPTOS.ID_DE_CONCEPTO = 130;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_DESCUENTOSALUD := 0;
            END;
        ELSE 
             BEGIN
                SELECT DISTINCT SUM(HISTORICOS.VALOR) TOTAL
                INTO   MI_DESCUENTOSALUD
                FROM   PERSONAL INNER JOIN HISTORICOS
                         ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
                        AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                    INNER JOIN CONCEPTOS
                         ON CONCEPTOS.COMPANIA        = HISTORICOS.COMPANIA
                        AND CONCEPTOS.ID_DE_CONCEPTO = HISTORICOS.ID_DE_CONCEPTO
                    INNER JOIN PERIODOS
                         ON PERIODOS.COMPANIA       = HISTORICOS.COMPANIA
                        AND PERIODOS.ID_DE_PROCESO = HISTORICOS.ID_DE_PROCESO
                        AND PERIODOS.ANO           = HISTORICOS.ANO
                        AND PERIODOS.MES           = HISTORICOS.MES
                        AND PERIODOS.PERIODO       = HISTORICOS.PERIODO
                WHERE HISTORICOS.COMPANIA  = UN_COMPANIA
                  AND PERIODOS.FECHAFINAL BETWEEN TO_DATE('01/01/'||TO_CHAR(CASE WHEN UN_ANIOHASTADEDSALUD <> 0 THEN MI_FECHA_COMBO2 ELSE MI_FECHA_COMBO1 END, 'YYYY'),'DD/MM/YYYY') AND TO_DATE('31/12/'||TO_CHAR(CASE WHEN UN_ANIOHASTADEDSALUD <> 0 THEN MI_FECHA_COMBO2 ELSE MI_FECHA_COMBO1 END, 'YYYY'),'DD/MM/YYYY')  --(MZANGUNA:14/01/2019)-Se agrega para tomar el año de la fecha final.
                  AND PERIODOS.ACUMULADO <> 0
                  AND PERSONAL.NUMERO_DCTO    = DOCUMENTO
                  AND CONCEPTOS.ID_DE_CONCEPTO = 130;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_DESCUENTOSALUD := 0;
            END;
        END IF;

        IF MI_DESCUENTOSALUD > 0 THEN
            --Columna 9.
            MI_MATRIZ.EXTEND();
            MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,8,MI_DESCUENTOSALUD);
        END IF;

        MI_PERSONALDEPENDIENTES := 0;

        MI_PERSONALDEPENDIENTES := PCK_NOMINA_COM3.FC_DEDUCUBLEACUMRETEF
            (UN_COMPANIA   => UN_COMPANIA
            ,UN_EMPLEADO    => ID_EMPLEADO
            ,UN_ANO1        => TO_NUMBER(TO_CHAR(MI_FECHA_COMBO1, 'YYYY'))
            ,UN_MES1        => TO_NUMBER(TO_CHAR(MI_FECHA_COMBO1, 'MM'))
            ,UN_ANO2        => TO_NUMBER(TO_CHAR(MI_FECHA_COMBO2, 'YYYY'))
            ,UN_MES2        => TO_NUMBER(TO_CHAR(MI_FECHA_COMBO2, 'MM'))
            ,UN_PAR         => 'P');


        IF PCK_NOMINA_COM3.FC_MIDEPENDIENTES(UN_COMPANIA => UN_COMPANIA ,UN_PARAMETRO => '' ,UN_IDEMPLEADO => ID_EMPLEADO) <>0 THEN
            IF MI_PERSONALDEPENDIENTES = 0 THEN
                IF PCK_SYSMAN_UTL.FC_ROUND( (MI_DTDEVENGOS * MI_PARPORCMAXIPERSONALACARGO) / 100, 0)   > PCK_SYSMAN_UTL.FC_ROUND(MI_PARVALUVTMACPERSONALACARGO * MI_UVTACTUAL, 0) * CASE WHEN MI_IMESES >= 13 THEN 12 ELSE MI_IMESES END THEN
                    MI_PERSONALDEPENDIENTES := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => MI_PARVALUVTMACPERSONALACARGO * MI_UVTACTUAL ,UN_PRECISION => 0) * CASE WHEN MI_IMESES >= 13 THEN 12 ELSE MI_IMESES END ;
                ELSE
                    MI_PERSONALDEPENDIENTES :=  PCK_SYSMAN_UTL.FC_ROUND((MI_DTDEVENGOS * MI_PARPORCMAXIPERSONALACARGO) / 100, 0);
                    --Columna 18
                    MI_MATRIZ.EXTEND();
                    MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,17, NVL(MI_PERSONALDEPENDIENTES, 0));
                END IF;
            ELSE
                IF PCK_SYSMAN_UTL.FC_ROUND((MI_DTDEVENGOS * MI_PARPORCMAXIPERSONALACARGO) / 100, 0) > PCK_SYSMAN_UTL.FC_ROUND(MI_PARVALUVTMACPERSONALACARGO * MI_UVTACTUAL,0) * CASE WHEN MI_IMESES >= 13 THEN 12 ELSE MI_IMESES END THEN
                    MI_PERSONALDEPENDIENTES := PCK_SYSMAN_UTL.FC_ROUND(MI_PARVALUVTMACPERSONALACARGO * MI_UVTACTUAL, 0) * CASE WHEN MI_IMESES >= 13 THEN 12 ELSE MI_IMESES END;
                ELSE
                    MI_PERSONALDEPENDIENTES := PCK_SYSMAN_UTL.FC_ROUND(MI_DTDEVENGOS * MI_PARPORCMAXIPERSONALACARGO / 100, 0);
                END IF;
                --Columna 18
                MI_MATRIZ.EXTEND();
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,17, NVL(MI_PERSONALDEPENDIENTES ,0));
            END IF;
        END IF;

        IF UN_CN309 <> 0 THEN
            MI_PROC := '';
            MI_PROC := PCK_NOMINA_COM1.FC_MIRESOLUCION(UN_COMPANIA => UN_COMPANIA
            ,UN_EMPLEADO    => ID_EMPLEADO
            ,UN_OPCION      => 99 );

            IF ESTADO_ACTUAL = 1 AND MI_PROC = '2' THEN
                BEGIN
                    MI_TABLA := 'NOVEDADES';
                    MI_MERGEUSING  := ' SELECT 1 FROM DUAL ' ;

                    MI_MERGEENLACE := ' COMPANIA  = '''|| UN_COMPANIA ||'''
                                    AND ID_DE_PROCESO = 0
                                    AND ANO  = 0
                                    AND MES = 0
                                    AND PERIODO = 0
                                    AND ID_DE_EMPLEADO = '|| ID_EMPLEADO ||'
                                    AND ID_DE_CONCEPTO = 309 ';

                    MI_VALORNOVEDAD := PCK_SYSMAN_UTL.FC_ROUND(MI_PERSONALDEPENDIENTES / CASE WHEN MI_IMESES >= 13 THEN 12 ELSE MI_IMESES END, 0);

                    MI_MERGEEXISTE := ' UPDATE SET VALOR  = ' || MI_VALORNOVEDAD || '
                                       ,FECHA  = SYSDATE
                                       ,MODIFIED_BY  = '''|| UN_USUARIO ||'''
                                       ,DATE_MODIFIED  = SYSDATE ';

                    MI_MERGENOEXIS := ' INSERT (COMPANIA, ID_DE_EMPLEADO, ID_DE_PROCESO, ANO, MES,PERIODO, FECHA ,ID_DE_CONCEPTO, VALOR, CREATED_BY, DATE_CREATED)
                                        VALUES (''' || UN_COMPANIA || ''',' || ID_EMPLEADO || ',0, 0, 0, 0, SYSDATE, 309, '|| MI_VALORNOVEDAD ||','''|| UN_USUARIO ||''',SYSDATE)';

                    BEGIN
                        MI_FILAS     := PCK_DATOS.FC_ACME
                            (UN_TABLA       => MI_TABLA,
                             UN_ACCION      => 'IM',
                             UN_MERGEUSING  => MI_MERGEUSING,
                             UN_MERGEENLACE => MI_MERGEENLACE,
                             UN_MERGEEXISTE => MI_MERGEEXISTE,
                             UN_MERGENOEXIS => MI_MERGENOEXIS);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                        RAISE PCK_EXCEPCIONES.EXC_NOMINA;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
                    MI_MSG(1).CLAVE := 'EMPLEADO';
                    MI_MSG(1).VALOR := ID_EMPLEADO;
                    PCK_ERR_MSG.RAISE_WITH_MSG
                        (UN_EXC_COD   => SQLCODE,
                         UN_ERROR_COD => PCK_ERRORES.ERR_MGNOVEDADGENCONCEPTO309,
                         UN_REEMPLAZOS => MI_MSG);
                END;
            END IF;
        END IF;

        --Columna 29
        MI_MATRIZ.EXTEND();
        MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,28,MI_IMESES);


        MI_VALORMES := 0;
        MI_L_CONTADOR:=(MI_L_CONTADOR+1);

    END LOOP;
    CLOSE RS;
    RETURN MI_MATRIZ;
END FC_CALCULAR_PORC_RETENCION;




FUNCTION FC_CALCULARPORCRET
/*
    NAME              : FC_CALCULARPORCRET
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 11/07/2015
    TIME              : 4:00 PM
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio
    DESCRIPTION       : Segunda parte del calculo del porcentaje fijo de retencion en la fuente.
    MODIFIER          : MIGUEL ANGEL ZANGUÑA HURTADO
    DATE MODIFIED     : 17/01/2018
    TIME              : 08:41 AM
    MODIFICATIONS     : Se adicionan cambios de última versión de utilitario NOMINAH2017.12.03 MPV UNIFICADAS 137 21122017

    @NAME:  calcularProCret
    @METHOD:  GET
*/
(
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_DOCUMENTO        IN PERSONAL.NUMERO_DCTO%TYPE,
    UN_PROMEDIO         IN PCK_SUBTIPOS.TI_DOBLE,
    UN_FECHA_COMBO1     IN DATE,
    UN_FECHA_COMBO2     IN DATE,
    UN_IMESES           IN PCK_SUBTIPOS.TI_DOBLE,
    UN_PROMEDIOC22      IN PCK_SUBTIPOS.TI_DOBLE,
    UN_ASIGNARPORRETEN  IN PCK_SUBTIPOS.TI_LOGICO,
    UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
)RETURN TYPEMATRIZ
    AS
    MI_PROMEDIO             PCK_SUBTIPOS.TI_DOBLE;
    MI_CANTIDADUVT          PCK_SUBTIPOS.TI_ENTERO;
    MI_VPORCENTAJE          PCK_SUBTIPOS.TI_ENTERO;
    MI_VVALOR               PCK_SUBTIPOS.TI_ENTERO;
    MI_MATRIZ               TYPEMATRIZ;
    MI_DDESCUENTOSRENTA     PCK_SUBTIPOS.TI_ENTERO;
    MI_EXCENTO25            PCK_SUBTIPOS.TI_ENTERO;
    MI_EXENTO25             PCK_SUBTIPOS.TI_DOBLE;
    MI_UVTACTUAL            PCK_SUBTIPOS.TI_ENTERO;
    MI_N                    PCK_SUBTIPOS.TI_ENTERO;
    MI_M                    PCK_SUBTIPOS.TI_ENTERO;
    MI_TOPE                 PCK_SUBTIPOS.TI_ENTERO;
    MI_SUELDOMENSUAL        PCK_SUBTIPOS.TI_ENTERO;
    MI_PROC                 VARCHAR2(100 CHAR);
    MI_UPDATE               PCK_SUBTIPOS.TI_ENTERO;
    MI_ID_EMPLEADO          PERSONAL.ID_DE_EMPLEADO%TYPE;
    MI_INGRESO              PERSONAL.FECHA_DE_INGRESO%TYPE;
    MI_PARPORCENLIBRERETE   PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PARVALORUVTMAXRENTA  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PROCESORETEN         PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_CONDICIONACME           PCK_SUBTIPOS.TI_CONDICION;
    MI_TABLA                   PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS                  PCK_SUBTIPOS.TI_CAMPOS;
    MI_FILAS                   PCK_SUBTIPOS.TI_ENTERO;
    MI_MSGERROR                PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
    MI_MATRIZ:= TYPEMATRIZ();
    MI_UVTACTUAL := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA ,UN_NOMBRE => 'VALOR UVT ACTUAL' ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA ,UN_FECHA_PAR => SYSDATE), 23763));  --MOD JM CC 1971
    BEGIN
        SELECT  SUB.ID_EMPLEADO,
                SUB.FECHA
          INTO  MI_ID_EMPLEADO,
                MI_INGRESO
        FROM(SELECT PERSONAL.ID_DE_EMPLEADO ID_EMPLEADO,
                    MAX(FECHA_DE_INGRESO) KEEP (DENSE_RANK LAST ORDER BY FECHA_DE_INGRESO) FECHA
             FROM   PERSONAL
             WHERE  PERSONAL.NUMERO_DCTO = UN_DOCUMENTO
             AND    PERSONAL.COMPANIA = UN_COMPANIA --MOD JM CC 1971
             GROUP BY PERSONAL.ID_DE_EMPLEADO ORDER BY 2 DESC) SUB
        WHERE ROWNUM <2;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_ID_EMPLEADO:= 0;
    END;

    BEGIN
        SELECT SUM(HISTORICOS.VALOR) TOTALRENTA
          INTO MI_DDESCUENTOSRENTA
        FROM PERSONAL
          INNER JOIN HISTORICOS
                 ON  HISTORICOS.COMPANIA         = PERSONAL.COMPANIA
                 AND HISTORICOS.ID_DE_EMPLEADO   = PERSONAL.ID_DE_EMPLEADO
          INNER JOIN CONCEPTOS
                 ON  CONCEPTOS.COMPANIA          = HISTORICOS.COMPANIA
                 AND CONCEPTOS.ID_DE_CONCEPTO    = HISTORICOS.ID_DE_CONCEPTO
          INNER JOIN PERIODOS
                 ON  PERIODOS.COMPANIA           = HISTORICOS.COMPANIA
                 AND PERIODOS.ID_DE_PROCESO      = HISTORICOS.ID_DE_PROCESO
                 AND PERIODOS.ANO                = HISTORICOS.ANO
                 AND PERIODOS.MES                = HISTORICOS.MES
                 AND PERIODOS.PERIODO            = HISTORICOS.PERIODO
          WHERE HISTORICOS.COMPANIA              = UN_COMPANIA
            AND PERIODOS.FECHAFINAL         BETWEEN UN_FECHA_COMBO1 AND UN_FECHA_COMBO2
            AND PERIODOS.ACUMULADO          <> 0
            AND CONCEPTOS.IND_RENTA_EXENTA  <> 0
            AND PERSONAL.NUMERO_DCTO        = UN_DOCUMENTO
          GROUP BY PERSONAL.NUMERO_DCTO;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_DDESCUENTOSRENTA:= 0;
    END;

    MI_PARPORCENLIBRERETE := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                                      ,UN_NOMBRE    => 'PORCENTAJE LIBRE DE RETENCION'
                                                      ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                                      ,UN_FECHA_PAR => SYSDATE), 25));


    MI_PARVALORUVTMAXRENTA :=  TO_NUMBER( NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                        ,UN_NOMBRE    => 'VALOR UVT MAXIMOS RENTA EXCENTA'
                                        ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                        ,UN_FECHA_PAR => SYSDATE), 0));


    --MZ Primero lo toma de la columna 22
    MI_PROMEDIO := UN_PROMEDIOC22;
    MI_PROMEDIO:= PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => MI_PROMEDIO, UN_PRECISION => 0);

    --'CALCULAR 25%EXCENTO SUMATOTALRENTAEXENTA
    MI_EXCENTO25:= 0;
    MI_EXENTO25:= PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_PROMEDIO * NVL(MI_PARPORCENLIBRERETE, 25) / 100), UN_PRECISION =>  0);

    IF MI_EXENTO25 > ((PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => MI_PARVALORUVTMAXRENTA * MI_UVTACTUAL - 1, UN_PRECISION => -3)) * CASE WHEN UN_IMESES = 13 THEN 12 ELSE UN_IMESES END) THEN
        MI_EXENTO25 := ((PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => MI_PARVALORUVTMAXRENTA * MI_UVTACTUAL - 1,
                        UN_PRECISION => -3)) * CASE WHEN UN_IMESES = 13 THEN 12 ELSE UN_IMESES END);
    ELSE
        MI_EXENTO25 := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => MI_PROMEDIO * NVL(MI_PARPORCENLIBRERETE, 25) / 100, UN_PRECISION =>  0);
    END IF;


    --Toma el valor de la columna 28
    MI_PROMEDIO := UN_PROMEDIO;

    MI_N:= 18;
    MI_PROMEDIO:= PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_PROMEDIO / UN_IMESES), UN_PRECISION =>  0);
    MI_CANTIDADUVT:= PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_PROMEDIO / MI_UVTACTUAL), UN_PRECISION => 3);

    MI_VPORCENTAJE:= 0;

    PCK_PARST.PR_INICIALIZAR_PARSISTEMA (UN_COMPANIA => UN_COMPANIA,
                                         UN_MODULO   => PCK_DATOS.FC_MODULONOMINA,
                                         UN_FECHA_PAR=> SYSDATE);
    --MZ AQUI
    MI_VPORCENTAJE:= PCK_NOMINA.FC_RETEF2007(UN_COMPANIA => UN_COMPANIA
                                            ,UN_ANIO     =>  MI_CANTIDADUVT
                                            ,UN_BASE     =>  PCK_NOMINA.FC_ASIGNARPERIODO(UN_IDENTIFICADOR => 2)
                                            ,UN_OPCION   =>  'P');


    MI_VVALOR:= CASE WHEN PCK_NOMINA.FC_RETEF2007(UN_COMPANIA => UN_COMPANIA
                                                 ,UN_ANIO     => MI_CANTIDADUVT
                                                 ,UN_BASE     =>  PCK_NOMINA.FC_ASIGNARPERIODO(UN_IDENTIFICADOR => 2)
                                                 ,UN_OPCION   => 'P') > 99.99
                    THEN  PCK_NOMINA.FC_RETEF2007(UN_COMPANIA => UN_COMPANIA
                                                 ,UN_ANIO    => MI_CANTIDADUVT - 1
                                                 ,UN_BASE    =>  PCK_NOMINA.FC_ASIGNARPERIODO(UN_IDENTIFICADOR => 2)
                                                 ,UN_OPCION  => 'P')
                    ELSE PCK_NOMINA.FC_RETEF2007(UN_COMPANIA => UN_COMPANIA
                                                 ,UN_ANIO    => MI_CANTIDADUVT
                                                 ,UN_BASE    =>  PCK_NOMINA.FC_ASIGNARPERIODO(UN_IDENTIFICADOR => 2)) END;

    MI_TOPE:= CASE WHEN PCK_NOMINA.FC_RETEF2007(UN_COMPANIA => UN_COMPANIA
                                                            ,UN_ANIO    => MI_CANTIDADUVT
                                                            ,UN_BASE    =>  PCK_NOMINA.FC_ASIGNARPERIODO(UN_IDENTIFICADOR => 2)
                                                            ,UN_OPCION  =>  'V') > 99.99
                    THEN PCK_NOMINA.FC_RETEF2007(UN_COMPANIA => UN_COMPANIA
                                                ,UN_ANIO     => MI_CANTIDADUVT
                                                ,UN_BASE     =>  PCK_NOMINA.FC_ASIGNARPERIODO(UN_IDENTIFICADOR => 2)
                                                 ,UN_OPCION   => 'V')
                    ELSE PCK_NOMINA.FC_RETEF2007(UN_COMPANIA => UN_COMPANIA
                                                ,UN_ANIO => MI_CANTIDADUVT
                                                ,UN_BASE => PCK_NOMINA.FC_ASIGNARPERIODO(UN_IDENTIFICADOR => 2)) END;


    IF MI_VVALOR >= 0 AND MI_CANTIDADUVT > 0 THEN  --MOD JM CC 1971 --MOD JM CC 3344
        MI_VPORCENTAJE:= CASE WHEN  MI_VPORCENTAJE < 0 THEN 0 ELSE PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => ((MI_VVALOR / MI_UVTACTUAL) / MI_CANTIDADUVT * 100), UN_PRECISION => 3) END;
        MI_VPORCENTAJE:= PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => MI_VPORCENTAJE, UN_PRECISION => 2);

        --Columna 40
        MI_MATRIZ.EXTEND();
        MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_M,39,NVL(MI_VPORCENTAJE, 0));

        --MZ La columna 39 se toma desde la columna 38 * 100
        MI_VPORCENTAJE:= PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => MI_VPORCENTAJE
                                                ,UN_PRECISION => 2);
    END IF;
    --(MZANGUNA:11/01/2019)-Se cambia el año de la generación de la categoria
    MI_SUELDOMENSUAL:= TO_NUMBER(FC_MISUELDO(UN_COMPANIA   => UN_COMPANIA
                                            ,UN_IDEMPLEADO => MI_ID_EMPLEADO
                                            ,UN_ANO        => TO_CHAR(UN_FECHA_COMBO2,'YYYY')));

    MI_N:= MI_N + 2;
    MI_PROC:= NVL(PCK_NOMINA_COM1.FC_MIRESOLUCION(UN_COMPANIA  => UN_COMPANIA
                                                  ,UN_EMPLEADO => MI_ID_EMPLEADO
                                                  ,UN_OPCION   => 99), 0);
    IF MI_PROC = '2' THEN
        MI_PROC:= 'Porcentaje Fijo';
    ELSIF MI_PROC = '1' THEN
        MI_PROC:= 'Por Tabla';
    ELSE
        MI_PROC:= 'Sin Definir';
    END IF;

    --1;Porcentaje por Tabla;2;Porcentaje Fijo

    --Columna 39
    MI_MATRIZ.EXTEND();
    MI_MATRIZ(MI_MATRIZ.COUNT):= VC_RECORD(MI_M,40,MI_PROC);
    --Columna 40
    MI_MATRIZ.EXTEND();
    MI_MATRIZ(MI_MATRIZ.COUNT):= VC_RECORD(MI_M,41,MI_SUELDOMENSUAL);
    --Columna 43
    MI_MATRIZ.EXTEND();
    MI_MATRIZ(MI_MATRIZ.COUNT):= VC_RECORD(MI_M,42,MI_INGRESO);

    --Columna 100, Para operaciones no va en el plano
    MI_MATRIZ.EXTEND();
    MI_MATRIZ(MI_MATRIZ.COUNT):= VC_RECORD(100,100, MI_ID_EMPLEADO);

    IF UN_ASIGNARPORRETEN <> 0 THEN
        IF PCK_SYSMAN_UTL.FC_MES(MI_INGRESO) = PCK_SYSMAN_UTL.FC_MES(UN_FECHA_COMBO2) AND PCK_SYSMAN_UTL.FC_ANIO(MI_INGRESO) = PCK_SYSMAN_UTL.FC_ANIO(UN_FECHA_COMBO2) THEN
            IF PCK_SYSMAN_UTL.FC_MES(UN_FECHA_COMBO2) >= 11 OR PCK_SYSMAN_UTL.FC_MES(UN_FECHA_COMBO2) <= 1 THEN
                MI_PROCESORETEN := 1;
            END IF;
        ELSE
            IF PCK_SYSMAN_UTL.FC_MES(UN_FECHA_COMBO2) >= 11 OR PCK_SYSMAN_UTL.FC_MES(UN_FECHA_COMBO2) <= 1 THEN
                MI_PROCESORETEN := 2;
            END IF;
        END IF;

        IF MI_PROCESORETEN <> 0 THEN
            BEGIN
                MI_TABLA     := ' PERSONAL ';
                MI_CAMPOS    := ' PROCESORETENCION   = '|| MI_PROCESORETEN ||'
                                 ,MODIFIED_BY        = '''|| UN_USUARIO ||'''
                                 ,DATE_MODIFIED      = SYSDATE ';

                MI_CONDICIONACME := 'COMPANIA   = '''|| UN_COMPANIA || '''
                             AND ID_DE_EMPLEADO = '|| MI_ID_EMPLEADO ||'   ';
                BEGIN
                    MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                               UN_ACCION    => 'M',
                                               UN_CAMPOS    => MI_CAMPOS,
                                               UN_CONDICION => MI_CONDICIONACME);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_NOMINA;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
                --Error al actualizar el proceso de retención al empleado: --EMPLEADO--.
                MI_MSGERROR(1).CLAVE := 'EMPLEADO';
                MI_MSGERROR(1).VALOR := MI_ID_EMPLEADO;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                           UN_ERROR_COD => PCK_ERRORES.ERR_NOMCALCRETENCIONEMP ,
                          UN_TABLAERROR => MI_TABLA,
                          UN_REEMPLAZOS => MI_MSGERROR);
            END;
        END IF;
    END IF;


    RETURN MI_MATRIZ;
END FC_CALCULARPORCRET;


--02
PROCEDURE PR_INCLUIRCONCEPTO (
/*
    NAME              : PR_INSERTARCONCEPTO  --> EN ACCESS IncluirConcepto  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 15/07/2015
    TIME              : 8:00 AM
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    DESCRIPTION       : INGRESA UN CONCEPTO
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED     : 21/03/2017
    TIME              : 04:10 PM
    MODIFICATIONS     : Se cambió el estándar de codificación y se agregó manejo de excepciones.

    @NAME:  incluirConcepto
    @METHOD:  POST
  */

    UN_NOMBRE        IN VARCHAR2,
    UN_CODIGO        IN PCK_SUBTIPOS.TI_ENTERO,
    UN_TIPOC         IN PCK_SUBTIPOS.TI_ENTERO,
    UN_UNIDAD        IN VARCHAR2,
    UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO
)
AS
    MI_VALORES       VARCHAR2(3200 CHAR);
    MI_CAMPOS        VARCHAR2(3200 CHAR);
    MI_NUM           PCK_SUBTIPOS.TI_ENTERO;
    MI_MSGERROR      PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN
   <<CARGAR_CODIGO_COMPANIA>>
    FOR RS IN (SELECT   CODIGO
               FROM     COMPANIA
               ORDER BY CODIGO
            )
    LOOP
        IF FC_CONCEPTO(UN_COMPANIA=> RS.CODIGO, UN_CODIGO=> UN_CODIGO) IS NULL THEN
            MI_VALORES:= ''''||RS.CODIGO||'''
                          ,'''||UN_NOMBRE||'''
                          ,'||UN_CODIGO||'
                          ,'''||UN_TIPOC||'''
                          ,'''||UN_UNIDAD||'''
                          ,'''||-1||'''';
            BEGIN
                BEGIN
                    MI_CAMPOS:= 'COMPANIA
                                ,NOMBRE_CONCEPTO
                                ,ID_DE_CONCEPTO
                                ,CLASE,UNIDAD,DENOVEDAD';

                    MI_NUM:= PCK_DATOS.FC_ACME(UN_TABLA => 'CONCEPTOS'
                                              ,UN_ACCION =>  'I'
                                              ,UN_CAMPOS =>  MI_CAMPOS
                                              ,UN_VALORES => MI_VALORES);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_NOMINA;

                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
                MI_MSGERROR(1).CLAVE := 'CONCEPTO';
                MI_MSGERROR(1).VALOR := UN_NOMBRE;
                    PCK_ERR_MSG.RAISE_WITH_MSG
                        (UN_EXC_COD    => SQLCODE
                        ,UN_ERROR_COD  => PCK_ERRORES.ER_NOMINA_INSERT_CONCEPTO
                        ,UN_REEMPLAZOS => MI_MSGERROR);

           END;
        END IF;
    END LOOP CARGAR_CODIGO_COMPANIA;
END PR_INCLUIRCONCEPTO;

--04
FUNCTION FC_CONCEPTO
(
  /*
    NAME              : FC_CONCEPTO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 09/07/2015
    TIME              : 4:00 PM
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio
    DESCRIPTION       : Retorna el valor de un concepto
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA 
    DATE MODIFIED     : 21/03/2017
    TIME              : 04:55 PM
    MODIFICATIONS     : Se cambió el estándar de codificación y se agregó manejo de excepciones.       
    @NAME:  nombreConcepto
    @METHOD:  GET                      
  */
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CODIGO       IN PCK_SUBTIPOS.TI_ENTERO
)     
RETURN VARCHAR2 
AS 
  MI_VALOR         VARCHAR2(30000 CHAR);
  MI_MSGERROR      PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
      --BEGIN
          BEGIN
              SELECT  UPPER(CONCEPTOS.NOMBRE_CONCEPTO)
                INTO  MI_VALOR
                FROM  CONCEPTOS
               WHERE  CONCEPTOS.COMPANIA = UN_COMPANIA 
                 AND  ID_DE_CONCEPTO     = UN_CODIGO;
          EXCEPTION WHEN NO_DATA_FOUND THEN
              MI_VALOR:=NULL;                                        
          END;
      /*EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN 
          MI_MSGERROR(1).CLAVE := 'CODIGO';
          MI_MSGERROR(1).VALOR := UN_CODIGO;
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE
                             ,UN_ERROR_COD=>PCK_ERRORES.ER_NOMINA_CONCEPTO_NOTFOUND
                             ,UN_REEMPLAZOS  => MI_MSGERROR);*/
      --END;
RETURN  MI_VALOR;      
END FC_CONCEPTO;

--05
FUNCTION FC_DEDUCIBLE_DEPENDIENTES
  /*
    NAME              : FC_DEDUCIBLE_DEPENDIENTES  --> EN ACCESS DEDUCIBLE_DEPENDIENTES  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 15/07/2015
    TIME              : 8:50 AM
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    DESCRIPTION       : RETORNA EL SUELDO DE UN EMPLEADO
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA 
    DATE MODIFIED     : 21/03/2017
    TIME              : 05:10 PM
    MODIFICATIONS     : Se cambió el estándar de codificación.     

    @NAME:  deducibleDependientes
    @METHOD:  GET  
  */
  (
          UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
          UN_IDEMPLEADO       IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO
  )
   RETURN PCK_SUBTIPOS.TI_DOBLE
       AS
          MI_ASIGNARPERIODO  PCK_SUBTIPOS.TI_ENTERO;
          MI_VALORTOTAL      PCK_SUBTIPOS.TI_ENTERO;

  BEGIN 
      MI_ASIGNARPERIODO:= PCK_NOMINA.FC_ASIGNARPERIODO(UN_IDENTIFICADOR => 1);
     BEGIN
          SELECT NOVEDADES.VALOR  
            INTO MI_VALORTOTAL
            FROM NOVEDADES 
           WHERE NOVEDADES.ID_DE_EMPLEADO  = UN_IDEMPLEADO 
             AND NOVEDADES.COMPANIA        = UN_COMPANIA
             AND (NOVEDADES.ID_DE_PROCESO  = 0 
             OR  NOVEDADES.ID_DE_PROCESO   = MI_ASIGNARPERIODO) 
             AND NOVEDADES.ANO             = 0 
             AND NOVEDADES.MES             = 0
             AND NOVEDADES.PERIODO         = 0
             AND NOVEDADES.ID_DE_CONCEPTO  = 309;

        EXCEPTION WHEN NO_DATA_FOUND THEN
             MI_VALORTOTAL:=0;  
     END;
        RETURN MI_VALORTOTAL;

END FC_DEDUCIBLE_DEPENDIENTES; 

--06
FUNCTION FC_MIDEPENDIENTES
/*
    NAME              : FC_MIDEPENDIENTES  --> EN ACCESS MIDEPENDIENTES  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 15/07/2015
    TIME              : 8:15 AM
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    DESCRIPTION       : RETORNA EL VALOR DE DEDUCIBLE DEPENDIENTES DE UN EMPLEADO
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA 
    DATE MODIFIED     : 21/03/2017
    TIME              : 05:20 PM
    MODIFICATIONS     : Se cambió el estándar de codificación.  

    @NAME:  deduciblePendiente
    @METHOD:  GET    
  */
  (
        UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
        UN_PARAMETRO      IN VARCHAR2,
        UN_IDEMPLEADO     IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO
  )
    RETURN PCK_SUBTIPOS.TI_LOGICO
        AS
        MI_PARAMETRO      VARCHAR2(5 CHAR);
        MI_DEPENDIENTES   PCK_SUBTIPOS.TI_ENTERO;

BEGIN 
        MI_PARAMETRO:=  UN_PARAMETRO;

    IF MI_PARAMETRO IS NULL OR NVL(MI_PARAMETRO,'')='' THEN
        MI_PARAMETRO:='D';
    END IF;

  BEGIN
        SELECT  PERSONAL.DEPENDIENTES384
          INTO  MI_DEPENDIENTES
        FROM    PERSONAL
        WHERE   PERSONAL.ID_DE_EMPLEADO = UN_IDEMPLEADO
            AND PERSONAL.COMPANIA       = UN_COMPANIA;

          EXCEPTION WHEN NO_DATA_FOUND THEN
               MI_DEPENDIENTES:=0;
  END;
        IF MI_PARAMETRO='D' THEN
             MI_DEPENDIENTES:= MI_DEPENDIENTES;
        ELSE
             MI_DEPENDIENTES:= 0;
        END IF;
     RETURN MI_DEPENDIENTES;

END FC_MIDEPENDIENTES;

FUNCTION FC_DEDUCIBLEV
/*
    NAME              : FC_DEDUCIBLEV  --> EN ACCESS DEDUCIBLE_DEPENDIENTES  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 15/07/2015
    TIME              : 11:15 AM
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    DESCRIPTION       : RETORNA EL VALOR DEL DEDUCIBLE DE UN EMPLEADO
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA 
    DATE MODIFIED     : 21/03/2017
    TIME              : 05:25 PM
    MODIFICATIONS     : Se cambió el estándar de codificación.  

    @NAME:  deducibleValor300
    @METHOD:  GET  
  */
  (
        UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
        UN_IDEMPLEADO       IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO
  )     
  RETURN PCK_SUBTIPOS.TI_DOBLE
     AS
        MI_ASIGNARPERIODO   PCK_SUBTIPOS.TI_ENTERO;
        MI_VALORTOTAL       PCK_SUBTIPOS.TI_DOBLE;

BEGIN 
        MI_ASIGNARPERIODO:= PCK_NOMINA.FC_ASIGNARPERIODO(UN_IDENTIFICADOR => 1);
  BEGIN
        SELECT NOVEDADES.VALOR TOTAL
          INTO MI_VALORTOTAL
          FROM NOVEDADES
         WHERE NOVEDADES.ID_DE_EMPLEADO  = UN_IDEMPLEADO 
           AND NOVEDADES.COMPANIA        = UN_COMPANIA
           AND NOVEDADES.ID_DE_PROCESO  IN (00,MI_ASIGNARPERIODO) 
           AND NOVEDADES.ANO             = 0 
           AND NOVEDADES.MES             = 0
           AND NOVEDADES.PERIODO         = 0
           AND NOVEDADES.ID_DE_CONCEPTO  = 300;

         EXCEPTION WHEN NO_DATA_FOUND THEN
              MI_VALORTOTAL:= 0;  
  END;

   IF MI_VALORTOTAL IS NULL THEN
     MI_VALORTOTAL:=0;
  END IF;
  RETURN MI_VALORTOTAL;

END FC_DEDUCIBLEV; 

FUNCTION FC_MISUELDO
  /*
    NAME              : FC_MISUELDO  --> EN ACCESS MISUELDO  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 13/07/2015
    TIME              : 12:00 M
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    DESCRIPTION       : RETORNA EL SUELDO DE UN EMPLEADO
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA 
    DATE MODIFIED     : 21/03/2017
    TIME              : 05:30 PM
    MODIFICATIONS     : Se cambió el estándar de codificación.  

    @NAME:  miSueldo
    @METHOD:  GET  
  */
  (
          UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
          UN_IDEMPLEADO         IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
          UN_ANO                IN PCK_SUBTIPOS.TI_ANIO
  )       
  RETURN PCK_SUBTIPOS.TI_DOBLE
      AS
          MI_SUELDO             PCK_SUBTIPOS.TI_DOBLE;

  BEGIN 
    BEGIN
           SELECT CATEGORIA.SALARIO_BASE
             INTO MI_SUELDO
             FROM PERSONAL
               INNER JOIN  CATEGORIA 
                       ON  PERSONAL.COMPANIA = CATEGORIA.COMPANIA
                       AND PERSONAL.ID_DE_CATEGORIA = CATEGORIA.ID_DE_CATEGORIA
                       AND PERSONAL.ESCALAFON       = CATEGORIA.ESCALAFON
            WHERE CATEGORIA.ANO                     = UN_ANO
              AND PERSONAL.ID_DE_EMPLEADO           = UN_IDEMPLEADO
              AND PERSONAL.COMPANIA                 = UN_COMPANIA;

       EXCEPTION WHEN NO_DATA_FOUND THEN
            RETURN 0;  
    END;

  RETURN MI_SUELDO;

END FC_MISUELDO;     

--11
PROCEDURE PR_ACTUALIZARNOVEDADES304
/*
    NAME              : PR_ACTUALIZARNOVEDADES304  --> EN ACCESS ACTUALIZARNOVEDADES304  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 13/07/2015
    TIME              : 12:00 M
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    DESCRIPTION       : ACTUALIZA LAS NOVEDADES DEL CONCEPTO 304
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA 
    DATE MODIFIED     : 21/03/2017
    TIME              : 05:35 PM
    MODIFICATIONS     : Se cambió el estándar de codificación.  

    @NAME:  actualizarNovedad304
    @METHOD:  GET  
  */
(
  UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO         IN PCK_SUBTIPOS.TI_ANIO,
  UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO
)
AS
  MI_PROMEDIO     PCK_SUBTIPOS.TI_ENTERO;
  MI_MESES        PCK_SUBTIPOS.TI_ENTERO;
  MI_FECHAC       DATE;
  BEGIN
  <<ACUMULADOS_POR_ANIO>>
        FOR RS IN( SELECT V_ACUMULADOS.COMPANIA,
                          V_ACUMULADOS.ANO, 
                          TO_NUMBER(V_ACUMULADOS.ID_DE_EMPLEADO) ID_DE_EMPLEADO,
                          SUM(V_ACUMULADOS.VALOR) SUMA,
                          V_ACUMULADOS.ID_DE_CONCEPTO
                    FROM  V_ACUMULADOS
                    WHERE V_ACUMULADOS.COMPANIA       = UN_COMPANIA
                      AND V_ACUMULADOS.ANO            = UN_ANIO 
                      AND V_ACUMULADOS.ID_DE_CONCEPTO = 130 
                    GROUP BY V_ACUMULADOS.ID_DE_EMPLEADO, 
                             V_ACUMULADOS.COMPANIA,
                             V_ACUMULADOS.ANO,
                             V_ACUMULADOS.ID_DE_CONCEPTO)
  LOOP      
         MI_MESES:= 0; 
         MI_PROMEDIO:= 0;
         MI_MESES:= FC_NUMERO_MESES_LABORADOS(UN_COMPANIA    => UN_COMPANIA 
                                             ,UN_ANIO        => UN_ANIO
                                             ,UN_ID_EMPLEADO =>  RS.ID_DE_EMPLEADO
                                             ,UN_CONCEPTO    =>  130);
         IF MI_MESES <> 0 THEN
            MI_PROMEDIO:= PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => (RS.SUMA / MI_MESES)
                                                 ,UN_PRECISION => 0);
         END IF;
      IF MI_PROMEDIO > 0 AND MI_MESES > 0 THEN
             PCK_NOMINA.PR_INCLUIRNOVEDAD (UN_COMPANIA    => UN_COMPANIA
                                           ,UN_PROCESO    => 0
                                           ,UN_ANIO       => 0
                                           ,UN_MES        => 0
                                           ,UN_PERIODO    => 0
                                           ,UN_IDEMPLEADO => RS.ID_DE_EMPLEADO
                                           ,UN_IDCONCEPTO => 304
                                           ,UN_VALOR      => MI_PROMEDIO
                                           ,UN_USER       => UN_USUARIO); 
      END IF;
  END LOOP ACUMULADOS_POR_ANIO;
END PR_ACTUALIZARNOVEDADES304;

FUNCTION FC_NUMERO_MESES_LABORADOS
/*
    NAME              : FC_NUMERO_MESES_LABORADOS  --> EN ACCESS NUMERO_MESES_LABORADOS  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 10/08/2015
    TIME              : 12:00 M
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    DESCRIPTION       : RETORNA EL NUMERO DE MESES LABORADOS DE UN EMPLEADO
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA 
    DATE MODIFIED     : 21/03/2017
    TIME              : 05:42 PM
    MODIFICATIONS     : Se cambió el estándar de codificación.  

    @NAME:  mesesLaborados
    @METHOD:  GET 
  */
    (
  UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO          IN PCK_SUBTIPOS.TI_ANIO, 
  UN_ID_EMPLEADO   IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO, 
  UN_CONCEPTO      IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO
    )
       RETURN PCK_SUBTIPOS.TI_ENTERO
   AS
      MI_NUMERO_MESES_LABORADOS          PCK_SUBTIPOS.TI_ENTERO;
  BEGIN
   MI_NUMERO_MESES_LABORADOS := 0;
     BEGIN
            SELECT COUNT(*)
              INTO MI_NUMERO_MESES_LABORADOS
              FROM (SELECT DISTINCT HISTORICOS.MES
                      FROM HISTORICOS
                     WHERE HISTORICOS.COMPANIA       = UN_COMPANIA
                       AND HISTORICOS.ID_DE_EMPLEADO = UN_ID_EMPLEADO 
                       AND HISTORICOS.ANO            = UN_ANIO 
                       AND HISTORICOS.ID_DE_CONCEPTO = UN_CONCEPTO);
      EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_NUMERO_MESES_LABORADOS:=0;
     END;
  RETURN MI_NUMERO_MESES_LABORADOS;

END FC_NUMERO_MESES_LABORADOS;

PROCEDURE PR_CALCULARDIFRETROACTIVOS
/*
    NAME              : PR_CALCULARDIFRETROACTIVOS  --> EN ACCESS CALCULARCLICK  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 10/08/2015
    TIME              : 12:00 M
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    DESCRIPTION       : RETORNA EL CALCULO DE LA DIFERENCIA DE RETROACTIVOS
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA 
    DATE MODIFIED     : 21/03/2017
    TIME              : 05:50 PM
    MODIFICATIONS     : Se cambió el estándar de codificación y manejó de excepciones.  

    @NAME:  calcularDiferenciaRetroactivo
    @METHOD:  GET 
  */
(
  UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_IDEMPLEADO   IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO, 
  UN_PROCESO      IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
  UN_ANIO         IN PCK_SUBTIPOS.TI_ANIO,
  UN_MES          IN PCK_SUBTIPOS.TI_MES,
  UN_PERIODO      IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
  UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO
  )
 AS
--'RUTINA QUE IDENTIFICA EL NUMERO DE MESES QUE SE LE PAGO EN DETERMINADO AÑO'

              MI_SELECT                   VARCHAR2(3200 CHAR);
              MI_QUERY                    VARCHAR2(3200 CHAR);
              MI_QUERY2                   VARCHAR2(3200 CHAR);
              MI_WHERE                    VARCHAR2(3200 CHAR);
              MI_RS                       SYS_REFCURSOR;
              MI_RS2                      SYS_REFCURSOR;
              MI_ELIMINAR                 PCK_SUBTIPOS.TI_ENTERO;
              MI_CODIGO                   HISTORICOS.ID_DE_EMPLEADO%TYPE; 
              MI_TOTAL                    PCK_SUBTIPOS.TI_ENTERO; 
              MI_NOM_EMPLEADO             PERSONAL.NOMBRECOMPLETO%TYPE;
              MI_ID_DE_TIPO               PERSONAL.ID_DE_TIPO%TYPE;
              MI_ID_DE_TIPO2              PERSONAL.ID_DE_TIPO%TYPE; 
              MI_ID_CENTRO_DE_COSTO       PERSONAL.ID_CENTRO_DE_COSTO%TYPE; 
              MI_ESTADO_ACTUAL            PERSONAL.ESTADO_ACTUAL%TYPE; 
              MI_CODIGO2                  HISTORICOS.ID_DE_EMPLEADO%TYPE;
              MI_NOM_EMPLEADO2            PERSONAL.NOMBRECOMPLETO%TYPE;
              MI_ESTADO_ACTUAL2           PERSONAL.ESTADO_ACTUAL%TYPE;          
              MI_CN101                    PCK_SUBTIPOS.TI_DOBLE; 
              MI_CN102                    PCK_SUBTIPOS.TI_DOBLE; 
              MI_CN103                    PCK_SUBTIPOS.TI_DOBLE; 
              MI_CN104                    PCK_SUBTIPOS.TI_DOBLE; 
              MI_CN105                    PCK_SUBTIPOS.TI_DOBLE; 
              MI_CN107                    PCK_SUBTIPOS.TI_DOBLE; 
              MI_CN108                    PCK_SUBTIPOS.TI_DOBLE;
              MI_CN130                    PCK_SUBTIPOS.TI_DOBLE; 
              MI_CN131                    PCK_SUBTIPOS.TI_DOBLE;
              MI_CN132                    PCK_SUBTIPOS.TI_DOBLE; 
              MI_CN113                    PCK_SUBTIPOS.TI_DOBLE;
              MI_CN115                    PCK_SUBTIPOS.TI_DOBLE;
              MI_CN116                    PCK_SUBTIPOS.TI_DOBLE;
              MI_CN117                    PCK_SUBTIPOS.TI_DOBLE;
              MI_CN118                    PCK_SUBTIPOS.TI_DOBLE;
              MI_CN119                    PCK_SUBTIPOS.TI_DOBLE;
              MI_CN120                    PCK_SUBTIPOS.TI_DOBLE;
              MI_CN112                    PCK_SUBTIPOS.TI_DOBLE;
              MI_CN212                    PCK_SUBTIPOS.TI_DOBLE;
              MI_CN111                    PCK_SUBTIPOS.TI_DOBLE;
              MI_FECHAC                   DATE;
              MI_CONDICION                VARCHAR2(500 CHAR);
              MI_CAMPOS                   VARCHAR2(500 CHAR);
              DESCTRANSPORTEBASEPARAF     VARCHAR2(500 CHAR);
              MI_IBLR                     PCK_SUBTIPOS.TI_DOBLE;
              MI_SALUD                    PCK_SUBTIPOS.TI_DOBLE;
              MI_PENSION                  PCK_SUBTIPOS.TI_DOBLE;
              MI_RIESGOS                  PCK_SUBTIPOS.TI_DOBLE;
              MI_NUMERODEC                PCK_SUBTIPOS.TI_DOBLE;
              MI_MSGERROR                 PCK_SUBTIPOS.TI_CLAVEVALOR;   
  BEGIN
    MI_SELECT:='SELECT  HISTORICOS.ID_DE_EMPLEADO CODIGO, 
                        SUM(HISTORICOS.VALOR) TOTAL, 
                        MAX(PERSONAL.NOMBRECOMPLETO) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) NOM_EMPLEADO,
                        MAX(PERSONAL.ID_DE_TIPO) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) ID_DE_TIPO, 
                        PERSONAL.ID_CENTRO_DE_COSTO ID_CENTRO_DE_COSTO, 
                        PERSONAL.ESTADO_ACTUAL 
                FROM    HISTORICOS 
                    LEFT JOIN CONCEPTOS ON 
                           HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO 
                       AND HISTORICOS.COMPANIA = CONCEPTOS.COMPANIA 
                    LEFT JOIN PERIODOS ON 
                           HISTORICOS.PERIODO = PERIODOS.PERIODO 
                       AND HISTORICOS.MES = PERIODOS.MES 
                       AND HISTORICOS.ANO = PERIODOS.ANO 
                       AND HISTORICOS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
                       AND HISTORICOS.COMPANIA = PERIODOS.COMPANIA 
                    LEFT JOIN PERSONAL ON 
                            HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO 
                        AND HISTORICOS.COMPANIA = PERSONAL.COMPANIA'; 
    IF UN_IDEMPLEADO <> 0 THEN
     MI_WHERE:='   WHERE HISTORICOS.COMPANIA          = '||UN_COMPANIA||'
                     AND HISTORICOS.ANO               = '||UN_ANIO||'
                     AND HISTORICOS.MES               = '||UN_MES||'
                     AND HISTORICOS.ID_DE_PROCESO     = '||UN_PROCESO||'
                     AND PERIODOS.ACUMULADO           <> 0 
                     AND HISTORICOS.PERIODO           <= 10
                     AND CONCEPTOS.FACTOR_PARAFISCAL  <> 0
                     AND HISTORICOS.ID_DE_EMPLEADO    = '||UN_IDEMPLEADO||'
                GROUP BY HISTORICOS.ID_DE_EMPLEADO, 
                         PERSONAL.ID_CENTRO_DE_COSTO, 
                         PERSONAL.ESTADO_ACTUAL
                ORDER BY 3';
    ELSE 
     MI_WHERE:='WHERE HISTORICOS.COMPANIA          = '||UN_COMPANIA||'
                  AND HISTORICOS.ANO               = '||UN_ANIO||' 
                  AND HISTORICOS.MES               = '||UN_MES||'
                  AND HISTORICOS.ID_DE_PROCESO     = '||UN_PROCESO||'  
                  AND PERIODOS.ACUMULADO           <> 0 
                  AND HISTORICOS.PERIODO           <= 07 
                  AND CONCEPTOS.FACTOR_PARAFISCAL  <> 0
                  AND HISTORICOS.ID_DE_EMPLEADO    = '||UN_IDEMPLEADO||'
             GROUP BY HISTORICOS.ID_DE_EMPLEADO, 
                      PERSONAL.ID_CENTRO_DE_COSTO, 
                      PERSONAL.ESTADO_ACTUAL
             ORDER BY 3';
    END IF;
          MI_QUERY:= MI_SELECT||' '||MI_WHERE;
          PCK_NOMINA_COM7.PR_CARGAR_PARENTRADA(UN_COMPANIA => UN_COMPANIA);
          MI_FECHAC:= PCK_NOMINA.FC_FECHAINIFINPERIODO(UN_COMPANIA  =>  UN_COMPANIA
                                                      ,UN_PROCESO   =>  UN_PROCESO
                                                      ,UN_ANIO      =>  UN_ANIO
                                                      ,UN_MES       =>  UN_MES
                                                      ,UN_PERIODO   =>  UN_PERIODO
                                                      ,UN_FECHAINICIO =>  2
                                                      ,UN_TOTAL     =>  MI_TOTAL);
 <<ELIMINAR_PARAFISCALES>>
  OPEN MI_RS FOR MI_QUERY;
    LOOP
  FETCH MI_RS INTO MI_CODIGO, MI_TOTAL, MI_NOM_EMPLEADO, MI_ID_DE_TIPO, MI_ID_CENTRO_DE_COSTO,MI_ESTADO_ACTUAL;
    EXIT WHEN MI_RS%NOTFOUND;
    --'PRIMERO ELIMINO LOS REGISTROS DE PARAFISCALES DEL RETROACTIVO
    MI_CONDICION:=   'HISTORICOS.COMPANIA       = '||UN_COMPANIA||' 
                  AND HISTORICOS.ID_DE_PROCESO  = '||UN_PROCESO||' 
                  AND HISTORICOS.ANO            = '||UN_ANIO||' 
                  AND HISTORICOS.MES            = '||UN_MES||' 
                  AND HISTORICOS.PERIODO        = '||UN_PERIODO||' 
                  AND HISTORICOS.ID_DE_EMPLEADO = '||MI_CODIGO||' 
                  AND HISTORICOS.ID_DE_CONCEPTO BETWEEN 101 AND 108';

    MI_CAMPOS:= 'COMPANIA
                ,ID_DE_PROCESO
                ,ANO
                ,MES
                ,PERIODO
                ,ID_DE_EMPLEADO
                ,ID_DE_CONCEPTO
                ,FECHA,VALOR';

   BEGIN
    BEGIN
      MI_ELIMINAR:= PCK_DATOS.FC_ACME(UN_TABLA      => 'HISTORICOS'
                                      ,UN_ACCION    =>  'E'
                                      ,UN_CAMPOS    =>  MI_CAMPOS
                                      ,UN_CONDICION =>  MI_CONDICION);  

         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_NOMINA;                                        

    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN 
                   MI_MSGERROR(1).CLAVE := 'EMPLEADO';
                   MI_MSGERROR(1).VALOR := MI_CODIGO;
                     PCK_ERR_MSG.RAISE_WITH_MSG(
                           UN_EXC_COD =>SQLCODE
                           ,UN_ERROR_COD=>PCK_ERRORES.ER_NOMINA_DELETE_HISTORICOS
                           ,UN_REEMPLAZOS  => MI_MSGERROR
                          );


  END;                     
  --'2O, SUMA LOS CONCEPTOS 108, PARA GUARDAR LA DIFERENCIA
   BEGIN
    SELECT  HISTORICOS.ID_DE_EMPLEADO CODIGO, 
            MAX(PERSONAL.NOMBRECOMPLETO) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) NOM_EMPLEADO,
            PERSONAL.ESTADO_ACTUAL, 
            SUM(CASE WHEN 
                          HISTORICOS.ID_DE_CONCEPTO = 101 
                     THEN HISTORICOS.VALOR 
                     ELSE 0 
                END) CN101, 
            SUM(CASE WHEN 
                         HISTORICOS.ID_DE_CONCEPTO = 102 
                    THEN HISTORICOS.VALOR 
                    ELSE 0 
                END) CN102, 
            SUM(CASE WHEN 
                         HISTORICOS.ID_DE_CONCEPTO = 103 
                    THEN HISTORICOS.VALOR 
                    ELSE 0 
                END) CN103, 
            SUM(CASE WHEN 
                         HISTORICOS.ID_DE_CONCEPTO = 104 
                    THEN HISTORICOS.VALOR 
                    ELSE 0 
                END) CN104, 
            SUM(CASE WHEN 
                         HISTORICOS.ID_DE_CONCEPTO = 105 
                    THEN HISTORICOS.VALOR 
                    ELSE 0 
                END) CN105, 
            SUM(CASE WHEN 
                         HISTORICOS.ID_DE_CONCEPTO = 107 
                    THEN HISTORICOS.VALOR 
                    ELSE 0 
                END) CN107, 
            SUM(CASE WHEN 
                         HISTORICOS.ID_DE_CONCEPTO = 108 
                    THEN HISTORICOS.VALOR 
                    ELSE 0 
                END) CN108
              INTO   MI_CODIGO2,
                     MI_NOM_EMPLEADO2,
                     MI_ESTADO_ACTUAL2, 
                     MI_CN101, 
                     MI_CN102, 
                     MI_CN103, 
                     MI_CN104, 
                     MI_CN105, 
                     MI_CN107, 
                     MI_CN108
    FROM     HISTORICOS 
      LEFT JOIN  CONCEPTOS 
             ON  HISTORICOS.COMPANIA             = CONCEPTOS.COMPANIA 
             AND HISTORICOS.ID_DE_CONCEPTO       = CONCEPTOS.ID_DE_CONCEPTO
      LEFT JOIN  PERIODOS 
              ON HISTORICOS.COMPANIA = PERIODOS.COMPANIA 
             AND HISTORICOS.ID_DE_PROCESO        = PERIODOS.ID_DE_PROCESO 
             AND HISTORICOS.ANO                  = PERIODOS.ANO 
             AND HISTORICOS.MES                  = PERIODOS.MES
             AND HISTORICOS.PERIODO              = PERIODOS.PERIODO
      LEFT JOIN  PERSONAL 
              ON HISTORICOS.COMPANIA = PERSONAL.COMPANIA
             AND HISTORICOS.ID_DE_EMPLEADO        = PERSONAL.ID_DE_EMPLEADO
    WHERE       HISTORICOS.COMPANIA               = UN_COMPANIA
            AND HISTORICOS.ID_DE_PROCESO          = UN_PROCESO
            AND HISTORICOS.ANO                    = UN_ANIO 
            AND HISTORICOS.MES                    = UN_MES
            AND HISTORICOS.PERIODO                <= 10
            AND HISTORICOS.ID_DE_CONCEPTO         BETWEEN 101 AND 108
            AND PERIODOS.ACUMULADO                <> 0 
            AND HISTORICOS.ID_DE_EMPLEADO         = MI_CODIGO
    GROUP BY     HISTORICOS.ID_DE_EMPLEADO, 
                 PERSONAL.ESTADO_ACTUAL 
    ORDER BY     NOM_EMPLEADO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_CODIGO2:= 0;
                    MI_NOM_EMPLEADO2:= 0;
                    MI_ESTADO_ACTUAL2:= 0; 
                    MI_CN101:= 0;
                    MI_CN102:= 0;
                    MI_CN103:= 0; 
                    MI_CN104:= 0;
                    MI_CN105:= 0;
                    MI_CN107:= 0;
                    MI_CN108:= 0;
   END;

   PCK_NOMINA.CN.DELETE;

   PCK_NOMINA_COM7.PR_CARGAR_PARENTRADA(UN_COMPANIA => UN_COMPANIA);

   PCK_NOMINA.CN(108):= PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => MI_TOTAL
                                                ,UN_PRECISION => -3);

   MI_NUMERODEC :=PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                        ,UN_NOMBRE    => 'NUMERO DECIMALES REDONDEO PARAFISCALES'
                                        ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                        ,UN_FECHA_PAR => SYSDATE);

  IF  MI_NUMERODEC <> 0 THEN
       PCK_NOMINA.CN(101):= PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => (PCK_NOMINA.FC_CN(108) - PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => DESCTRANSPORTEBASEPARAF = 'SI'
                                                                                                                   ,UN_SI =>  (PCK_NOMINA.CN(80) + PCK_NOMINA.CN(525) + PCK_NOMINA.CN(533))
                                                                                                                   ,UN_NO => 0)) * PCK_NOMINA.CPARENTRADA(1).PORC_CAJA / 100
                                                    ,UN_PRECISION => TO_NUMBER(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                                                                                     ,UN_NOMBRE    => 'NUMERO DECIMALES REDONDEO PARAFISCALES'
                                                                                                     ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                                                                                     ,UN_FECHA_PAR => SYSDATE))) - MI_CN101; --' APORTES CAJA DE COMPENSACION


       PCK_NOMINA.CN(102):= PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => (PCK_NOMINA.FC_CN(108) - PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => DESCTRANSPORTEBASEPARAF = 'SI'
                                                                                                                   ,UN_SI        => (PCK_NOMINA.CN(80) + PCK_NOMINA.CN(525) + PCK_NOMINA.CN(533))
                                                                                                                   ,UN_NO        => 0)) * PCK_NOMINA.CPARENTRADA(1).PORC_ICBF / 100
                                                    ,UN_PRECISION => TO_NUMBER(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                                                                                     ,UN_NOMBRE    =>  'NUMERO DECIMALES REDONDEO PARAFISCALES'
                                                                                                     ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                                                                                     ,UN_FECHA_PAR => SYSDATE))) - MI_CN102; -- ' APORTES PARA I.C.B.F.

       PCK_NOMINA.CN(103):= PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => (PCK_NOMINA.FC_CN(108) - PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => DESCTRANSPORTEBASEPARAF = 'SI'
                                                                                                                   ,UN_SI        => (PCK_NOMINA.CN(80) + PCK_NOMINA.CN(525) + PCK_NOMINA.CN(533))
                                                                                                                   ,UN_NO        =>  0)) * PCK_NOMINA.CPARENTRADA(1).PORC_TOTAL_EPS / 100
                                                    ,UN_PRECISION => TO_NUMBER(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                                                                                     ,UN_NOMBRE    => 'NUMERO DECIMALES REDONDEO PARAFISCALES'
                                                                                                     ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                                                                                     ,UN_FECHA_PAR => SYSDATE))) - MI_CN103; -- ' APORTES PARA SENA

       PCK_NOMINA.CN(104):= PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => (PCK_NOMINA.FC_CN(108) - PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => DESCTRANSPORTEBASEPARAF = 'SI'
                                                                                                                   ,UN_SI        => (PCK_NOMINA.CN(80) + PCK_NOMINA.CN(525) + PCK_NOMINA.CN(533))
                                                                                                                   ,UN_NO        => 0)) * PCK_NOMINA.CPARENTRADA(1).PORC_ESAP / 100
                                                    ,UN_PRECISION => TO_NUMBER(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                                                                                     ,UN_NOMBRE    => 'NUMERO DECIMALES REDONDEO PARAFISCALES'
                                                                                                     ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                                                                                     ,UN_FECHA_PAR => SYSDATE))) - MI_CN104; -- ' E.S.A.P.

       PCK_NOMINA.CN(105):= PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => (PCK_NOMINA.FC_CN(108) - PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => DESCTRANSPORTEBASEPARAF = 'SI'
                                                                                                                  ,UN_SI        => (PCK_NOMINA.CN(80) + PCK_NOMINA.CN(525) + PCK_NOMINA.CN(533))
                                                                                                                  ,UN_NO        => 0)) * PCK_NOMINA.CPARENTRADA(1).PORC_INST / 100
                                                   ,UN_PRECISION => TO_NUMBER(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                                                                                    ,UN_NOMBRE    => 'NUMERO DECIMALES REDONDEO PARAFISCALES'
                                                                                                    ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                                                                                    ,UN_FECHA_PAR => SYSDATE))) - MI_CN105; -- ' INSTITUTOS TECNICOS
  ELSE
       PCK_NOMINA.CN(101):= PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => (PCK_NOMINA.FC_CN(108) -PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => DESCTRANSPORTEBASEPARAF = 'SI'
                                                                                                                 ,UN_SI => (PCK_NOMINA.CN(80) + PCK_NOMINA.CN(525) + PCK_NOMINA.CN(533))
                                                                                                                 ,UN_NO => 0)) * PCK_NOMINA.CPARENTRADA(1).PORC_CAJA / 100
                                                   ,UN_PRECISION =>  0) - MI_CN101;

       PCK_NOMINA.CN(102):= PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => (PCK_NOMINA.FC_CN(108) -PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => DESCTRANSPORTEBASEPARAF = 'SI'
                                                                                                                  ,UN_SI        => (PCK_NOMINA.CN(80) + PCK_NOMINA.CN(525) + PCK_NOMINA.CN(533))
                                                                                                                  ,UN_NO        => 0)) * PCK_NOMINA.CPARENTRADA(1).PORC_ICBF / 100
                                                   ,UN_PRECISION => 0) - MI_CN102; --' APORTES PARA I.C.B.F.

       PCK_NOMINA.CN(103):= PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => (PCK_NOMINA.FC_CN(108) -PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => DESCTRANSPORTEBASEPARAF = 'SI'
                                                                                                                  ,UN_SI        => (PCK_NOMINA.CN(80) + PCK_NOMINA.CN(525) + PCK_NOMINA.CN(533))
                                                                                                                  ,UN_NO        => 0)) * PCK_NOMINA.CPARENTRADA(1).PORC_TOTAL_EPS / 100
                                                   ,UN_PRECISION => 0) - MI_CN103; --' APORTES PARA SENA

       PCK_NOMINA.CN(104):= PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR       => (PCK_NOMINA.FC_CN(108) -PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => DESCTRANSPORTEBASEPARAF = 'SI'
                                                                                                                   ,UN_SI => (PCK_NOMINA.CN(80) + PCK_NOMINA.CN(525) + PCK_NOMINA.CN(533))
                                                                                                                   ,UN_NO => 0)) * PCK_NOMINA.CPARENTRADA(1).PORC_ESAP / 100
                                                   ,UN_PRECISION =>  0) - MI_CN104; --' E.S.A.P.

       PCK_NOMINA.CN(105):= PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (PCK_NOMINA.FC_CN(108) -PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => DESCTRANSPORTEBASEPARAF = 'SI'
                                                                                                             ,UN_SI => (PCK_NOMINA.CN(80) + PCK_NOMINA.CN(525) + PCK_NOMINA.CN(533))
                                                                                                             ,UN_NO => 0)) * PCK_NOMINA.CPARENTRADA(1).PORC_INST / 100
                                                   ,UN_PRECISION => 0) - MI_CN105; --' INSTITUTOS TECNICOS
  END IF;
  PCK_NOMINA.CN(108):= PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => MI_TOTAL
                                              ,UN_PRECISION => -3);

  PCK_NOMINA.CN(108):= PCK_NOMINA.FC_CN(108)-MI_CN108;

  IF (PCK_NOMINA.CPARENTRADA(1).NIT = '891800475-0' AND MI_ID_CENTRO_DE_COSTO = 12) THEN   --'PARA ALC CHIQUINQ NO SE GIRA POR EMPLEADOS SENA
       PCK_NOMINA.CN(108) := 0; 
       PCK_NOMINA.CN(107) := 0; 
       PCK_NOMINA.CN(101) := 0; 
       PCK_NOMINA.CN(102) := 0; 
       PCK_NOMINA.CN(103) := 0; 
       PCK_NOMINA.CN(104) := 0; 
       PCK_NOMINA.CN(105) := 0;
  END IF;
  IF MI_ID_DE_TIPO = 5 OR MI_ID_DE_TIPO = 4 AND PCK_NOMINA.CPARENTRADA(1).NIT <> '891800475-0' AND PCK_NOMINA.CPARENTRADA(1).NIT <> '890701933-4' THEN-- '<> CHIQ. SEP17/203 'CHIQ
       PCK_NOMINA.CN(108) := 0; 
       PCK_NOMINA.CN(107) := 0; 
       PCK_NOMINA.CN(101) := 0; 
       PCK_NOMINA.CN(102) := 0; 
       PCK_NOMINA.CN(103) := 0; 
       PCK_NOMINA.CN(104) := 0; 
       PCK_NOMINA.CN(105) := 0;
  END IF;
  IF MI_ID_DE_TIPO = 99 OR MI_ESTADO_ACTUAL = 2 THEN-- '<> CHIQ. SEP17/203 'CHIQ
       PCK_NOMINA.CN(108) := 0;
       PCK_NOMINA.CN(107) := 0; 
       PCK_NOMINA.CN(101) := 0; 
       PCK_NOMINA.CN(102) := 0; 
       PCK_NOMINA.CN(103) := 0; 
       PCK_NOMINA.CN(104) := 0; 
       PCK_NOMINA.CN(105) := 0;
  END IF;

  IF PCK_NOMINA.CPARENTRADA(1).EXONERADO_1607 = 'S' AND (PCK_NOMINA.FC_CN(108) - MI_CN108) < 10 * PCK_NOMINA.CPARENTRADA(1).SALARIOMINIMO THEN --' 13062013 ESP CAJICA  20062014 CHIA RETROACTIVO
       PCK_NOMINA.CN(102) := 0;
       PCK_NOMINA.CN(103) := 0;
       PCK_NOMINA.CN(104) := 0;
       PCK_NOMINA.CN(105) := 0;
  END IF;
  <<INCLUIR_HISTORICO>>
  FOR I IN 101 .. 108 
  LOOP
       PCK_NOMINA.PR_INCLUIRHISTORICOF(UN_COMPANIA   => UN_COMPANIA
                                      ,UN_PROCESO    => UN_PROCESO
                                      ,UN_ANIO       => UN_ANIO
                                      ,UN_MES        => UN_MES
                                      ,UN_PERIODO    => UN_PERIODO
                                      ,UN_IDEMPLEADO => MI_CODIGO
                                      ,UN_IDCONCEPTO => PCK_SYSMAN_UTL.FC_STRZERO(I, 3)
                                      ,UN_VALOR      => PCK_NOMINA.FC_CN(I)
                                      ,UN_FECHAC     => MI_FECHAC
                                      ,UN_OBS        => ''''
                                      ,UN_USUARIO    => UN_USUARIO);
  END LOOP INCLUIR_HISTORICO;
  --'REVISO SEGURIDAD SOCIAL SALUD Y PENSION
  -- '2O, SUMA LOS CONCEPTOS 108, PARA GUARDAR LA DIFERENCIA
    BEGIN
      SELECT  HISTORICOS.ID_DE_EMPLEADO CODIGO, 
              MAX(PERSONAL.NOMBRECOMPLETO) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) NOM_EMPLEADO, 
              PERSONAL.ESTADO_ACTUAL, 
              SUM((CASE WHEN HISTORICOS.ID_DE_CONCEPTO=130 
                        THEN HISTORICOS.VALOR 
                        ELSE 0
                   END)) CN130, 
              SUM((CASE WHEN HISTORICOS.ID_DE_CONCEPTO=131 
                        THEN HISTORICOS.VALOR 
                        ELSE 0
                   END)) CN131, 
              SUM((CASE WHEN HISTORICOS.ID_DE_CONCEPTO=132 
                        THEN HISTORICOS.VALOR 
                        ELSE 0
                   END)) CN132, 
              SUM((CASE WHEN HISTORICOS.ID_DE_CONCEPTO=113 
                        THEN HISTORICOS.VALOR 
                        ELSE 0
                   END)) CN113, 
              SUM((CASE WHEN HISTORICOS.ID_DE_CONCEPTO=115 
                        THEN HISTORICOS.VALOR 
                        ELSE 0
                   END)) CN115,
              SUM((CASE WHEN HISTORICOS.ID_DE_CONCEPTO=116 
                        THEN HISTORICOS.VALOR 
                        ELSE 0
                   END)) CN116, 
              SUM((CASE WHEN HISTORICOS.ID_DE_CONCEPTO=117 
                        THEN HISTORICOS.VALOR 
                        ELSE 0
                   END)) CN117, 
              SUM((CASE WHEN HISTORICOS.ID_DE_CONCEPTO=118 
                        THEN HISTORICOS.VALOR 
                        ELSE 0
                   END)) CN118, 
              SUM((CASE WHEN HISTORICOS.ID_DE_CONCEPTO=120 
                        THEN HISTORICOS.VALOR 
                        ELSE 0
                   END)) CN120, 
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO=112 
                        THEN HISTORICOS.VALOR 
                        ELSE 0
                   END) CN112, 
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO=212 
                        THEN HISTORICOS.VALOR 
                        ELSE 0
                   END) CN212, 
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO=111 
                        THEN HISTORICOS.VALOR 
                        ELSE 0
                   END) CN111, 
              PERSONAL.ID_DE_TIPO ID_DE_TIPO
      INTO    MI_CODIGO2,
              MI_NOM_EMPLEADO2,
              MI_ESTADO_ACTUAL2, 
              MI_CN130, 
              MI_CN131, 
              MI_CN132, 
              MI_CN113, 
              MI_CN115,
              MI_CN116,
              MI_CN117,
              MI_CN118, 
              MI_CN120,
              MI_CN112,
              MI_CN212,
              MI_CN111,
              MI_ID_DE_TIPO2
      FROM    HISTORICOS 
          LEFT JOIN CONCEPTOS 
                ON  HISTORICOS.COMPANIA       = CONCEPTOS.COMPANIA 
                AND HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO
          LEFT JOIN PERIODOS 
                ON  HISTORICOS.COMPANIA       = PERIODOS.COMPANIA 
                AND HISTORICOS.ID_DE_PROCESO  = PERIODOS.ID_DE_PROCESO 
                AND HISTORICOS.ANO            = PERIODOS.ANO 
                AND HISTORICOS.MES            = PERIODOS.MES 
                AND HISTORICOS.PERIODO        = PERIODOS.PERIODO
          LEFT JOIN PERSONAL 
                ON  HISTORICOS.COMPANIA       = PERSONAL.COMPANIA 
                AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO 
      WHERE    HISTORICOS.COMPANIA           = UN_COMPANIA
            AND HISTORICOS.ANO                = UN_ANIO
            AND HISTORICOS.MES                = UN_MES
            AND HISTORICOS.ID_DE_PROCESO      = UN_PROCESO
            AND HISTORICOS.PERIODO            < 07
            AND HISTORICOS.ID_DE_CONCEPTO     BETWEEN 111 AND 212 
            AND PERIODOS.ACUMULADO            <> 0 
            AND HISTORICOS.ID_DE_EMPLEADO     = MI_CODIGO
      GROUP BY  HISTORICOS.ID_DE_EMPLEADO, 
                PERSONAL.ESTADO_ACTUAL , 
                PERSONAL.ID_DE_TIPO
      ORDER BY  2;
      EXCEPTION WHEN NO_DATA_FOUND THEN
                      MI_CODIGO2:= 0;
                      MI_NOM_EMPLEADO2:= 0;
                      MI_ESTADO_ACTUAL2:= 0;
                      MI_CN130:= 0; 
                      MI_CN131:= 0;
                      MI_CN132:= 0;
                      MI_CN113:= 0;
                      MI_CN115:= 0;
                      MI_CN116:= 0;
                      MI_CN117:= 0;
                      MI_CN118:= 0;
                      MI_CN119:= 0;
                      MI_CN120:= 0;
                      MI_CN112:= 0;
                      MI_CN212:= 0;
                      MI_CN111:= 0;
                      MI_ID_DE_TIPO2:= 0;
    END;
  PCK_NOMINA.CN(116):= 0;
  -- 'SALUD
  IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                          ,UN_NOMBRE    => 'CALCULAR SALUD CON DECRETO 1122'
                          ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                          ,UN_FECHA_PAR => SYSDATE) = 'SI' THEN

       MI_IBLR := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_CN112
                                         ,UN_PRECISION =>  -3);

       MI_IBLR := ( CASE WHEN MI_IBLR > (PCK_NOMINA.CPARENTRADA(1).SALARIOS_MAXIMOS_EPS * PCK_NOMINA.CPARENTRADA(1).SALARIOMINIMO) 
                         THEN PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => PCK_NOMINA.CPARENTRADA(1).SALARIOS_MAXIMOS_EPS * PCK_NOMINA.CPARENTRADA(1).SALARIOMINIMO
                                                     ,UN_PRECISION =>  -3)
                         ELSE MI_IBLR 
                    END);

       MI_SALUD := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => MI_IBLR * PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                                                                      ,UN_NOMBRE    => 'PORCENTAJE SALUD DECRETO 1122'
                                                                                      ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                                                                      ,UN_FECHA_PAR => SYSDATE) / 100
                                          ,UN_PRECISION => -2);

       PCK_NOMINA.CN(116) := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_SALUD - (MI_CN113 + MI_CN116)
                                                    ,UN_PRECISION =>  0);-- 'APORTES SALUD PATRONO

     IF PCK_NOMINA.CPARENTRADA(1).EXONERADO_1607 = 'S' AND PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_TOTAL
                                                                                  ,UN_PRECISION => -3) < 10 * PCK_NOMINA.CPARENTRADA(1).SALARIOMINIMO THEN --'20062014
         PCK_NOMINA.CN(116) := 0;
         PCK_NOMINA.CN(130) := 0;
         PCK_NOMINA.CN(130) := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_CN112
                                                                              ,UN_PRECISION => -3) * PCK_NOMINA.CPARENTRADA(1).PORC_EMPLEADO_EPS / 100, -2) - MI_CN130;
         PCK_NOMINA.CN(113) := PCK_NOMINA.FC_CN(130);
        IF PCK_NOMINA.FC_CN(130) <> 0  THEN
           PCK_NOMINA.PR_INCLUIRHISTORICOF(UN_COMPANIA   => UN_COMPANIA
                                          ,UN_PROCESO    => UN_PROCESO
                                          ,UN_ANIO       => UN_ANIO
                                          ,UN_MES        => UN_MES
                                          ,UN_PERIODO    => UN_PERIODO
                                          ,UN_IDEMPLEADO => MI_CODIGO
                                          ,UN_IDCONCEPTO => 130
                                          ,UN_VALOR      => PCK_NOMINA.FC_CN(130)
                                          ,UN_FECHAC     => MI_FECHAC
                                          ,UN_OBS        => ''''
                                          ,UN_ACCION     => '+'
                                          ,UN_USUARIO    => UN_USUARIO); --'AL ENVIAR LA FECHA SUMA EL VALOR AL HISTORICO

           PCK_NOMINA.PR_INCLUIRHISTORICOF(UN_COMPANIA   => UN_COMPANIA
                                          ,UN_PROCESO    => UN_PROCESO
                                          ,UN_ANIO       => UN_ANIO
                                          ,UN_MES        => UN_MES
                                          ,UN_PERIODO    => UN_PERIODO
                                          ,UN_IDEMPLEADO => MI_CODIGO
                                          ,UN_IDCONCEPTO => 113
                                          ,UN_VALOR      => PCK_NOMINA.FC_CN(113)
                                          ,UN_FECHAC     => MI_FECHAC
                                          ,UN_OBS        => ''''
                                          ,UN_ACCION     => '+'
                                          ,UN_USUARIO    => UN_USUARIO); --'AL ENVIAR LA FECHA SUMA EL VALOR AL HISTORICO
        END IF;
        GOTO PENSIONN;
      END IF;
      IF PCK_NOMINA.FC_CN(116) > 0 THEN
           PCK_NOMINA.PR_INCLUIRHISTORICOF(UN_COMPANIA   => UN_COMPANIA
                                          ,UN_PROCESO    => UN_PROCESO
                                          ,UN_ANIO       => UN_ANIO
                                          ,UN_MES        => UN_MES
                                          ,UN_PERIODO    => UN_PERIODO
                                          ,UN_IDEMPLEADO => MI_CODIGO
                                          ,UN_IDCONCEPTO => 116
                                          ,UN_VALOR      => PCK_NOMINA.FC_CN(116)
                                          ,UN_FECHAC     => MI_FECHAC
                                          ,UN_OBS        => ''''
                                          ,UN_ACCION     => '+'
                                          ,UN_USUARIO    => UN_USUARIO); --'AL ENVIAR LA FECHA SUMA EL VALOR AL HISTORICO

      ELSIF PCK_NOMINA.FC_CN(116) < 0 THEN
           PCK_NOMINA.PR_INCLUIRHISTORICOF(UN_COMPANIA   => UN_COMPANIA
                                          ,UN_PROCESO    => UN_PROCESO
                                          ,UN_ANIO       => UN_ANIO
                                          ,UN_MES        => UN_MES
                                          ,UN_PERIODO    => UN_PERIODO
                                          ,UN_IDEMPLEADO => MI_CODIGO
                                          ,UN_IDCONCEPTO => 116
                                          ,UN_VALOR      => PCK_NOMINA.FC_CN(116)
                                          ,UN_FECHAC     => MI_FECHAC
                                          ,UN_OBS        => ''''
                                          ,UN_ACCION     => '+'
                                          ,UN_USUARIO    => UN_USUARIO); --'AL ENVIAR LA FECHA SUMA EL VALOR AL HISTORICO
      END IF;
     END IF;
--'PENSION
<<PENSIONN>>
    PCK_NOMINA.CN(117):= 0;
    MI_IBLR:= PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_CN112
                                     ,UN_PRECISION =>  -3);

    MI_IBLR:= PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => MI_IBLR > (PCK_NOMINA.CPARENTRADA(1).SALARIOS_MAXIMOS_AFP * PCK_NOMINA.CPARENTRADA(1).SALARIOMINIMO)
                                   ,UN_SI => PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_NOMINA.CPARENTRADA(1).SALARIOS_MAXIMOS_AFP * PCK_NOMINA.CPARENTRADA(1).SALARIOMINIMO
                                                                    ,UN_PRECISION => -3)
                                   ,UN_NO => MI_IBLR);
    IF MI_ID_DE_TIPO2 = 95 THEN
       MI_PENSION := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_IBLR * PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                                                                            ,UN_NOMBRE    => 'PORCENTAJE APORTES PENSION BOMBEROS'
                                                                                            ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                                                                            ,UN_FECHA_PAR => SYSDATE) / 100
                                            ,UN_PRECISION => -2);
    ELSE
       MI_PENSION := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_IBLR * PCK_NOMINA.CPARENTRADA(1).PORC_TOTAL_AFP / 100
                                            ,UN_PRECISION =>  -2);
    END IF;
    PCK_NOMINA.CN(117) := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_PENSION - (MI_CN117 + MI_CN118)
                                                 ,UN_PRECISION =>  0); --'APORTES SALUD PATRONO
    IF PCK_NOMINA.FC_CN(117) > 0 THEN
      PCK_NOMINA.PR_INCLUIRHISTORICOF(UN_COMPANIA   => UN_COMPANIA
                                     ,UN_PROCESO    => UN_PROCESO
                                     ,UN_ANIO       => UN_ANIO
                                     ,UN_MES        => UN_MES
                                     ,UN_PERIODO    => UN_PERIODO
                                     ,UN_IDEMPLEADO => MI_CODIGO
                                     ,UN_IDCONCEPTO => 117
                                     ,UN_VALOR      => PCK_NOMINA.FC_CN(117)
                                     ,UN_FECHAC     => MI_FECHAC
                                     ,UN_OBS        => ''''
                                     ,UN_ACCION     => '+'
                                     ,UN_USUARIO    => UN_USUARIO); --'AL ENVIAR LA FECHA SUMA EL VALOR AL HISTORICO
    ELSIF PCK_NOMINA.FC_CN(117) < 0 THEN
      PCK_NOMINA.PR_INCLUIRHISTORICOF(UN_COMPANIA   => UN_COMPANIA
                                     ,UN_PROCESO    => UN_PROCESO
                                     ,UN_ANIO       => UN_ANIO
                                     ,UN_MES        => UN_MES
                                     ,UN_PERIODO    => UN_PERIODO
                                     ,UN_IDEMPLEADO => MI_CODIGO
                                     ,UN_IDCONCEPTO => 117
                                     ,UN_VALOR      => PCK_NOMINA.FC_CN(117)
                                     ,UN_FECHAC     => MI_FECHAC
                                     ,UN_OBS        => ''''
                                     ,UN_ACCION     => '+'
                                     ,UN_USUARIO    => UN_USUARIO); --'AL ENVIAR LA FECHA SUMA EL VALOR AL HISTORICO
    END IF;
--'FSP
    PCK_NOMINA.CN(132) := 0;
    PCK_NOMINA.CN(115) := 0;
    IF MI_IBLR > (4 * PCK_NOMINA.CPARENTRADA(1).SALARIOMINIMO) THEN
    --'02072013 IF RS2!CN132 <> RS2!CN115 OR RS2!CN132 = 0 THEN
         PCK_NOMINA.CN(132) := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_CN112
                                                                                              ,UN_PRECISION =>  -3) * PCK_NOMINA.CPARENTRADA(1).PORC_FSP_AFP / 100 / 2
                                                      ,UN_PRECISION => -2) * 2 - MI_CN132;

         PCK_NOMINA.PR_INCLUIRHISTORICOF(UN_COMPANIA   => UN_COMPANIA
                                        ,UN_PROCESO    =>  UN_PROCESO
                                        ,UN_ANIO       =>  UN_ANIO
                                        ,UN_MES        =>  UN_MES
                                        ,UN_PERIODO    =>  UN_PERIODO
                                        ,UN_IDEMPLEADO =>  MI_CODIGO
                                        ,UN_IDCONCEPTO =>  132
                                        ,UN_VALOR      =>  PCK_NOMINA.FC_CN(132)
                                        ,UN_FECHAC     =>  MI_FECHAC
                                        ,UN_OBS        =>  ''''
                                        ,UN_ACCION     =>  '+'
                                        ,UN_USUARIO    => UN_USUARIO); --'AL ENVIAR LA FECHA SUMA EL VALOR AL HISTORICO

         PCK_NOMINA.CN(115) := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_CN112
                                                                                          ,UN_PRECISION =>  -3) * PCK_NOMINA.CPARENTRADA(1).PORC_FSP_AFP / 100 / 2
                                                      ,UN_PRECISION => -2) * 2 - MI_CN115;

         PCK_NOMINA.PR_INCLUIRHISTORICOF(UN_COMPANIA   => UN_COMPANIA
                                        ,UN_PROCESO    => UN_PROCESO
                                        ,UN_ANIO       => UN_ANIO
                                        ,UN_MES        => UN_MES
                                        ,UN_PERIODO    => UN_PERIODO
                                        ,UN_IDEMPLEADO => MI_CODIGO
                                        ,UN_IDCONCEPTO => 115
                                        ,UN_VALOR      => PCK_NOMINA.FC_CN(115)
                                        ,UN_FECHAC     => MI_FECHAC
                                        ,UN_OBS        => ''''
                                        ,UN_ACCION     => '+'
                                        ,UN_USUARIO    => UN_USUARIO);--'AL ENVIAR LA FECHA SUMA EL VALOR AL HISTORICO
       --'140832013 CVALLE
       IF MI_IBLR > (16 * PCK_NOMINA.CPARENTRADA(1).SALARIOMINIMO) THEN
            IF MI_IBLR > (16 * PCK_NOMINA.FC_CN(201)) AND MI_IBLR <= (17 * PCK_NOMINA.FC_CN(201)) THEN
                  PCK_NOMINA.CN(120):= PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_IBLR * 1.2 / 100
                                                              ,UN_PRECISION => -2) - MI_CN115 - MI_CN120 - PCK_NOMINA.CN(115);

            ELSIF MI_IBLR > (17 * PCK_NOMINA.FC_CN(201)) AND MI_IBLR <= (18 * PCK_NOMINA.FC_CN(201)) THEN
                  PCK_NOMINA.CN(120) := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_IBLR * 1.4 / 100
                                                               ,UN_PRECISION =>  -2) - MI_CN115 - MI_CN120 - PCK_NOMINA.CN(115);

            ELSIF MI_IBLR > (18 * PCK_NOMINA.FC_CN(201)) AND MI_IBLR <= (19 * PCK_NOMINA.FC_CN(201)) THEN
                  PCK_NOMINA.CN(120) := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_IBLR * 1.6 / 100
                                                               ,UN_PRECISION =>  -2) - MI_CN115 - MI_CN120 - PCK_NOMINA.CN(115);

            ELSIF MI_IBLR > (19 * PCK_NOMINA.FC_CN(201)) AND MI_IBLR <= (20 * PCK_NOMINA.FC_CN(201)) THEN
                  PCK_NOMINA.CN(120) := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_IBLR * 1.8 / 100
                                                               ,UN_PRECISION => -2) - MI_CN115 - MI_CN120 - PCK_NOMINA.CN(115);

            ELSIF MI_IBLR > (20 * PCK_NOMINA.FC_CN(201)) THEN
                  PCK_NOMINA.CN(120) := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR    => MI_IBLR * 2 / 100
                                                               ,UN_PRECISION =>  -2) - MI_CN115 - MI_CN120 - PCK_NOMINA.CN(115);
            END IF;
            PCK_NOMINA.PR_INCLUIRNOVEDAD (UN_COMPANIA   => UN_COMPANIA
                                         ,UN_PROCESO    =>  UN_PROCESO
                                         ,UN_ANIO       =>  UN_ANIO
                                         ,UN_MES        =>  UN_MES
                                         ,UN_PERIODO    =>  UN_PERIODO
                                         ,UN_IDEMPLEADO =>  MI_CODIGO
                                         ,UN_IDCONCEPTO =>  120
                                         ,UN_VALOR      =>  PCK_NOMINA.FC_CN(120)
                                         ,UN_ACCION     =>  MI_FECHAC); --'AL ENVIAR LA FECHA SUMA EL VALOR AL HISTORICO

            PCK_NOMINA.PR_INCLUIRHISTORICOF (UN_COMPANIA   => UN_COMPANIA
                                            ,UN_PROCESO    => UN_PROCESO
                                            ,UN_ANIO       => UN_ANIO
                                            ,UN_MES        => UN_MES
                                            ,UN_PERIODO    => UN_PERIODO
                                            ,UN_IDEMPLEADO => MI_CODIGO
                                            ,UN_IDCONCEPTO => 120
                                            ,UN_VALOR      => PCK_NOMINA.FC_CN(120)
                                            ,UN_FECHAC     => MI_FECHAC
                                            ,UN_OBS        => ''''
                                            ,UN_ACCION     => '+'
                                            ,UN_USUARIO    => UN_USUARIO); --'AL ENVIAR LA FECHA SUMA EL VALOR AL HISTORICO
      END IF;
    --'END IF
    END IF;
--'ARP
    PCK_NOMINA.CN(111) := 0;
    MI_IBLR := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_CN212
                                      ,UN_PRECISION => -3);

    MI_IBLR := PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => MI_IBLR > (PCK_NOMINA.CPARENTRADA(1).SALARIOS_MAXIMOS_ARP * PCK_NOMINA.CPARENTRADA(1).SALARIOMINIMO)
                                    ,UN_SI        =>  PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => PCK_NOMINA.CPARENTRADA(1).SALARIOS_MAXIMOS_ARP * PCK_NOMINA.CPARENTRADA(1).SALARIOMINIMO
                                                                             ,UN_PRECISION => -3)
                                    ,UN_NO        =>  MI_IBLR);

    MI_RIESGOS := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_IBLR * PCK_NOMINA_SEGSOCI.FC_PORCENRIESGO(UN_COMPANIA   => UN_COMPANIA
                                                                                              ,UN_IDEMPLEADO => MI_CODIGO) / 100
                                         ,UN_PRECISION => -2);

    IF PCK_NOMINA_SEGSOCI.FC_PORCENRIESGO(UN_COMPANIA => UN_COMPANIA
                                 ,UN_IDEMPLEADO =>  MI_CODIGO) > 7 THEN --' 20062014

       MI_RIESGOS := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_IBLR * PCK_NOMINA_SEGSOCI.FC_PORCENRIESGO(UN_COMPANIA   => UN_COMPANIA
                                                                                                 ,UN_IDEMPLEADO => MI_CODIGO) / 1000 / 100
                                            ,UN_PRECISION => -2);
    END IF;
    PCK_NOMINA.CN(111) := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_RIESGOS - (MI_CN111)
                                                 ,UN_PRECISION => 0);-- 'APORTES SALUD PATRONO

    IF PCK_NOMINA.FC_CN(111) > 0 THEN
          PCK_NOMINA.PR_INCLUIRHISTORICOF (UN_COMPANIA   => UN_COMPANIA
                                          ,UN_PROCESO    => UN_PROCESO
                                          ,UN_ANIO       =>  UN_ANIO
                                          ,UN_MES        =>  UN_MES
                                          ,UN_PERIODO    =>  UN_PERIODO
                                          ,UN_IDEMPLEADO =>  MI_CODIGO
                                          ,UN_IDCONCEPTO =>  111
                                          ,UN_VALOR      => PCK_NOMINA.FC_CN(111)
                                          ,UN_FECHAC     =>  MI_FECHAC
                                          ,UN_OBS        => ''''
                                          ,UN_ACCION     => '+'
                                          ,UN_USUARIO    => UN_USUARIO);-- 'AL ENVIAR LA FECHA SUMA EL VALOR AL HISTORICO
    ELSIF PCK_NOMINA.FC_CN(111) < 0 THEN
          PCK_NOMINA.PR_INCLUIRHISTORICOF (UN_COMPANIA   => UN_COMPANIA
                                          ,UN_PROCESO    => UN_PROCESO
                                          ,UN_ANIO       => UN_ANIO
                                          ,UN_MES        => UN_MES
                                          ,UN_PERIODO    => UN_PERIODO
                                          ,UN_IDEMPLEADO => MI_CODIGO
                                          ,UN_IDCONCEPTO => 111
                                          ,UN_VALOR      => PCK_NOMINA.FC_CN(111)
                                          ,UN_FECHAC     => MI_FECHAC
                                          ,UN_OBS        => ''''
                                          ,UN_ACCION     => '+'
                                          ,UN_USUARIO    => UN_USUARIO);-- 'AL ENVIAR LA FECHA SUMA EL VALOR AL HISTORICO
    END IF;

  END LOOP ELIMINAR_PARAFISCALES;

END PR_CALCULARDIFRETROACTIVOS;

PROCEDURE PR_RETEFTERETROACTIVOS
/*
    NAME              : PR_RETEFTERETROACTIVOS  --> EN ACCESS RETEFTE_Click  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 21/08/2015
    TIME              : 12:00 M
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    DESCRIPTION       : CALCULA LA RETENCION EN LA FUENTE DE LOS RETROACTIVOS
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA 
    DATE MODIFIED     : 22/03/2017
    TIME              : 02:40 PM
    MODIFICATIONS     : Se cambió el estándar de codificación y manejó de excepciones.

    @NAME:  reteFteRetroActivos
    @METHOD:  GET 

  */
  (
  UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_IDEMPLEADO   IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO, 
  UN_PROCESO      IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
  UN_ANIO         IN PCK_SUBTIPOS.TI_ANIO,
  UN_MES          IN PCK_SUBTIPOS.TI_MES,
  UN_PERIODO      IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
  UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO
  )
    AS
          MI_ELIMINAR           PCK_SUBTIPOS.TI_ENTERO;
          MI_FECHAC             DATE;
          MI_CONDICION          VARCHAR(3200 CHAR);
          MI_CAMPOS             VARCHAR(3200 CHAR);
          MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;    
  BEGIN
    IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                            ,UN_NOMBRE    => 'LIQUIDAR RETE FUENTE EN PERIODOS 05 RETROACTIVO'
                            ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                            ,UN_FECHA_PAR => SYSDATE) = 'SI' THEN

       MI_CONDICION:= ' (HISTORICOS.ID_DE_PROCESO='||UN_PROCESO||
                    ' AND (HISTORICOS.ANO='||UN_ANIO||') 
                      AND (HISTORICOS.MES='||UN_MES||') 
                      AND (HISTORICOS.PERIODO='||UN_PERIODO||') 
                      AND (HISTORICOS.ID_DE_CONCEPTO (303,125)';

      MI_CAMPOS:='ID_DE_PROCESO
                  ,ANO
                  ,MES
                  ,PERIODO
                  ,ID_DE_EMPLEADO
                  ,ID_DE_CONCEPTO
                  ,FECHA
                  ,VALOROBSERVACIONES
                  ,CREATED_BY
                  ,DATE_CREATED
                  ,MODIFIED_BY
                  ,DATE_MODIFIED';

   BEGIN
     BEGIN
      MI_ELIMINAR:= PCK_DATOS.FC_ACME(UN_TABLA     => 'HISTORICOS'
                                     ,UN_ACCION    =>  'E'
                                     ,UN_CAMPOS    =>  MI_CAMPOS
                                     ,UN_CONDICION =>  MI_CONDICION);

         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_NOMINA;                                        

     END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN 
                   MI_MSGERROR(1).CLAVE := 'PERIODO';
                   MI_MSGERROR(1).VALOR := UN_PERIODO;
                     PCK_ERR_MSG.RAISE_WITH_MSG(
                           UN_EXC_COD =>SQLCODE
                           ,UN_ERROR_COD=>PCK_ERRORES.ER_NOMINA_DELETE_PHISTORICOS
                           ,UN_REEMPLAZOS  => MI_MSGERROR
                          );


   END;

      MI_FECHAC:= PCK_NOMINA.FC_FECHAINIFINPERIODO(UN_COMPANIA  => UN_COMPANIA
                                                  ,UN_PROCESO   => UN_PROCESO
                                                  ,UN_ANIO      => UN_ANIO
                                                  ,UN_MES       => UN_MES
                                                  ,UN_PERIODO   => UN_PERIODO
                                                  ,UN_FECHAINICIO =>  2);

     <<INCLUIR_HISTORICO>>                                             
      FOR RS IN (SELECT   HISTORICOS.ID_DE_PROCESO, 
                          HISTORICOS.ANO, 
                          HISTORICOS.MES, 
                          HISTORICOS.PERIODO, 
                          HISTORICOS.ID_DE_EMPLEADO, 
                          SUM((CASE WHEN CONCEPTOS.CLASE=3 
                                    THEN HISTORICOS.VALOR 
                                    ELSE 0 
                               END)) DEVENGOS, 
                          SUM((CASE WHEN CONCEPTOS.CLASE=5 
                                    THEN HISTORICOS.VALOR 
                                    ELSE 0 
                               END)) DESCUENTOS, 
                          (SUM((CASE WHEN CONCEPTOS.CLASE=3 
                                      THEN HISTORICOS.VALOR 
                                      ELSE 0 END))-SUM((CASE WHEN CONCEPTOS.CLASE=5 
                                      THEN HISTORICOS.VALOR 
                                      ELSE 0 
                                END))) NETO, 
                          PCK_SYSMAN_UTL.FC_ROUND((SUM((CASE WHEN CONCEPTOS.CLASE = 3 
                                      THEN HISTORICOS.VALOR 
                                      ELSE 0 END))-SUM((CASE WHEN CONCEPTOS.CLASE = 5 
                                      THEN HISTORICOS.VALOR 
                                      ELSE 0 END))),0)-PCK_SYSMAN_UTL.FC_ROUND(SUM(CASE WHEN CONCEPTOS.CLASE=7 
                                      THEN HISTORICOS.VALOR 
                                      ELSE 0 END),0) DIFER, 
                                    PERSONAL.PROCESORETENCION, 
                                    CN303.VALOR303
                 FROM    ((HISTORICOS 
                        LEFT JOIN CONCEPTOS 
                               ON HISTORICOS.ID_DE_CONCEPTO                  = CONCEPTOS.ID_DE_CONCEPTO) 
                        LEFT JOIN PERSONAL 
                               ON HISTORICOS.ID_DE_EMPLEADO                  = PERSONAL.ID_DE_EMPLEADO) 
                        INNER JOIN (SELECT  NOVEDADES.COMPANIA, 
                                            NOVEDADES.ID_DE_PROCESO, 
                                            NOVEDADES.ANO, 
                                            NOVEDADES.MES, 
                                            NOVEDADES.PERIODO, 
                                            NOVEDADES.ID_DE_EMPLEADO, 
                                            NOVEDADES.ID_DE_CONCEPTO, 
                                            NOVEDADES.VALOR VALOR303
                                    FROM    NOVEDADES
                                    WHERE  NOVEDADES.COMPANIA = UN_COMPANIA  
                                      AND  NOVEDADES.ANO                    = 0
                                      AND  NOVEDADES.MES                    = 0
                                      AND  NOVEDADES.PERIODO                = 0
                                      AND  NOVEDADES.ID_DE_CONCEPTO         = 303
                                      AND  NOVEDADES.VALOR<>0)CN303 
                                        ON  HISTORICOS.ID_DE_EMPLEADO        = CN303.ID_DE_EMPLEADO 
                  WHERE   ((CONCEPTOS.CLASE = 3) 
                          AND ((CONCEPTOS.FACTOR_RETEFUENTE) = -1) 
                          AND (PERSONAL.ESTADO_ACTUAL = 1)) 
                          OR ((CONCEPTOS.CLASE = 5) 
                          AND (PERSONAL.ESTADO_ACTUAL = 1))
                          AND  (((HISTORICOS.ID_DE_PROCESO=UN_PROCESO) 
                                AND (HISTORICOS.ANO=UN_ANIO) 
                                AND (HISTORICOS.MES=UN_MES) 
                                AND (HISTORICOS.PERIODO=UN_PERIODO))) 
                  GROUP BY  HISTORICOS.ID_DE_PROCESO, 
                            HISTORICOS.ANO, 
                            HISTORICOS.MES, 
                            HISTORICOS.PERIODO, 
                            HISTORICOS.ID_DE_EMPLEADO, 
                            PERSONAL.PROCESORETENCION, 
                            CN303.VALOR303)
      LOOP
         PCK_NOMINA.CN(125) := 0;
         PCK_NOMINA.CN(125) := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => ((RS.DEVENGOS - RS.DESCUENTOS) * PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                                                                                                             ,UN_NOMBRE    => 'VALOR PORCENTAJE APLICAR PROCESO RETEFUENTE'
                                                                                                                             ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                                                                                                             ,UN_FECHA_PAR => SYSDATE)) * RS.VALOR303 / 100
                                                      ,UN_PRECISION => -3);
         IF PCK_NOMINA.CN(125) > 0 THEN
            PCK_NOMINA.PR_INCLUIRHISTORICOF (UN_COMPANIA   => UN_COMPANIA
                                            ,UN_PROCESO    => UN_PROCESO
                                            ,UN_ANIO       =>  UN_ANIO
                                            ,UN_MES        => UN_MES
                                            ,UN_PERIODO    =>  UN_PERIODO
                                            ,UN_IDEMPLEADO =>  RS.ID_DE_EMPLEADO
                                            ,UN_IDCONCEPTO =>  125
                                            ,UN_VALOR      =>  PCK_NOMINA.CN(125)
                                            ,UN_FECHAC     =>  MI_FECHAC
                                            ,UN_OBS        =>  ' '
                                            ,UN_USUARIO    => UN_USUARIO);

            PCK_NOMINA.PR_INCLUIRHISTORICOF (UN_COMPANIA   => UN_COMPANIA
                                            ,UN_PROCESO    => UN_PROCESO
                                            ,UN_ANIO       =>  UN_ANIO
                                            ,UN_MES        => UN_MES
                                            ,UN_PERIODO    =>  UN_PERIODO
                                            ,UN_IDEMPLEADO =>  RS.ID_DE_EMPLEADO
                                            ,UN_IDCONCEPTO =>  303
                                            ,UN_VALOR      =>  RS.VALOR303
                                            ,UN_FECHAC     => MI_FECHAC
                                            ,UN_OBS        =>  ' '
                                            ,UN_USUARIO    => UN_USUARIO);
         END IF;

      END LOOP INCLUIR_HISTORICO;
     END IF;       

END PR_RETEFTERETROACTIVOS;

PROCEDURE PR_NETOSRETROACTIVO
/*
    NAME              : PR_NETOSRETROACTIVO  --> EN ACCESS NETOS_Click  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 22/08/2015
    TIME              : 12:00 M
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    DESCRIPTION       : RETORNA EL CALCULO DE LA DIFERENCIA DE RETROACTIVOS
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA 
    DATE MODIFIED     : 22/03/2017
    TIME              : 03:00 PM
    MODIFICATIONS     : Se cambió el estándar de codificación y manejó de excepciones.

    @NAME:  netosRetroActivo
    @METHOD:  GET 
  */
(
  UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_PROCESO    IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
  UN_MES        IN PCK_SUBTIPOS.TI_MES,
  UN_ANIO       IN PCK_SUBTIPOS.TI_ANIO,
  UN_PERIODO    IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
  UN_USUARIO    IN PCK_SUBTIPOS.TI_USUARIO
)
      AS
--'CALCULAR NETOS
          MI_FECHAC     DATE;
          MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION;
          MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;
          MI_ELIMINAR   PCK_SUBTIPOS.TI_ENTERO;
          MI_MSGERROR   PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
      MI_FECHAC := PCK_NOMINA.FC_FECHAINIFINPERIODO(UN_COMPANIA  => UN_COMPANIA
                                                   ,UN_PROCESO   => UN_PROCESO
                                                   ,UN_ANIO      => UN_ANIO
                                                   ,UN_MES       => UN_MES
                                                   ,UN_PERIODO   => UN_PERIODO
                                                   ,UN_FECHAINICIO => 2);

      MI_CAMPOS := 'ID_DE_PROCESO
                  , ANO
                  , MES
                  , PERIODO
                  , ID_DE_EMPLEADO
                  , ID_DE_CONCEPTO
                  , FECHA
                  , VALOR
                  , OBSERVACIONES
                  , CREATED_BY
                  , DATE_CREATED
                  , MODIFIED_BY
                  , DATE_MODIFIED';

      MI_CONDICION := '((HISTORICOS.ID_DE_PROCESO='||UN_PROCESO||') 
                    AND (HISTORICOS.ANO='||UN_ANIO||') 
                    AND (HISTORICOS.MES='||UN_MES||') 
                    AND (HISTORICOS.PERIODO='||UN_PERIODO||') 
                    AND (HISTORICOS.COMPANIA='||UN_COMPANIA||')) 
                    AND (HISTORICOS.ID_DE_CONCEPTO IN (097,140,144))';

    BEGIN
     BEGIN
      MI_ELIMINAR:= PCK_DATOS.FC_ACME(UN_TABLA     => 'HISTORICOS'
                                     ,UN_ACCION    =>  'E'
                                     ,UN_CAMPOS    => MI_CAMPOS
                                     ,UN_CONDICION => MI_CONDICION);

           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_NOMINA;                                        

     END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN 
                   MI_MSGERROR(1).CLAVE := 'PERIODO';
                   MI_MSGERROR(1).VALOR := UN_PERIODO;
                     PCK_ERR_MSG.RAISE_WITH_MSG(
                           UN_EXC_COD =>SQLCODE
                           ,UN_ERROR_COD=>PCK_ERRORES.ER_NOMINA_DELETE_PHISTORICOS
                           ,UN_REEMPLAZOS  => MI_MSGERROR
                          );


    END;

    <<INCLUIR_HISTORICO>>
      FOR RS IN( SELECT   V_ACUMULADOS.COMPANIA, 
                          V_ACUMULADOS.ID_DE_PROCESO, 
                          V_ACUMULADOS.ANO, 
                          V_ACUMULADOS.MES, 
                          V_ACUMULADOS.PERIODO, 
                          V_ACUMULADOS.ID_DE_EMPLEADO, 
                          SUM(V_ACUMULADOS.DEVENGOS)   DEVENGOS, 
                          SUM(V_ACUMULADOS.DESCUENTOS) DESCUENTOS, 
                          SUM(V_ACUMULADOS.NETOAPAGAR) NETO, 
                          SUM(CASE WHEN V_ACUMULADOS.CLASE = 7 
                                   THEN V_ACUMULADOS.VALOR 
                                   ELSE 0 
                              END) AS NETOHIS
                 FROM  V_ACUMULADOS 
                 WHERE V_ACUMULADOS.COMPANIA      = UN_COMPANIA
                   AND V_ACUMULADOS.ID_DE_PROCESO = UN_PROCESO
                   AND V_ACUMULADOS.ANO           = UN_ANIO
                   AND V_ACUMULADOS.MES           = UN_MES
                   AND V_ACUMULADOS.PERIODO       = UN_PERIODO
                   AND V_ACUMULADOS.CLASE         IN (3,5,7)
                 GROUP BY  V_ACUMULADOS.COMPANIA, 
                           V_ACUMULADOS.ID_DE_PROCESO, 
                           V_ACUMULADOS.ANO, 
                           V_ACUMULADOS.MES,
                           V_ACUMULADOS.PERIODO, 
                           V_ACUMULADOS.ID_DE_EMPLEADO)
          LOOP
              PCK_NOMINA.PR_INCLUIRHISTORICOF(UN_COMPANIA   => UN_COMPANIA
                                             ,UN_PROCESO    => UN_PROCESO
                                             ,UN_ANIO       =>  UN_ANIO
                                             ,UN_MES        =>  UN_MES
                                             ,UN_PERIODO    =>  UN_PERIODO
                                             ,UN_IDEMPLEADO => RS.ID_DE_EMPLEADO
                                             ,UN_IDCONCEPTO =>  097
                                             ,UN_VALOR      => RS.DEVENGOS
                                             ,UN_FECHAC     => MI_FECHAC
                                             ,UN_OBS        => ' '
                                             ,UN_USUARIO    => UN_USUARIO);

              PCK_NOMINA.PR_INCLUIRHISTORICOF(UN_COMPANIA   => UN_COMPANIA
                                             ,UN_PROCESO    => UN_PROCESO
                                             ,UN_ANIO       => UN_ANIO
                                             ,UN_MES        => UN_MES
                                             ,UN_PERIODO    => UN_PERIODO
                                             ,UN_IDEMPLEADO => RS.ID_DE_EMPLEADO
                                             ,UN_IDCONCEPTO => 140
                                             ,UN_VALOR      => RS.DESCUENTOS
                                             ,UN_FECHAC     => MI_FECHAC
                                             ,UN_OBS        => ' '
                                             ,UN_USUARIO    => UN_USUARIO);

              PCK_NOMINA.PR_INCLUIRHISTORICOF(UN_COMPANIA   => UN_COMPANIA
                                             ,UN_PROCESO    => UN_PROCESO
                                             ,UN_ANIO       =>  UN_ANIO
                                             ,UN_MES        =>  UN_MES
                                             ,UN_PERIODO    =>  UN_PERIODO
                                             ,UN_IDEMPLEADO =>  RS.ID_DE_EMPLEADO
                                             ,UN_IDCONCEPTO => 144
                                             ,UN_VALOR      => RS.NETO
                                             ,UN_FECHAC     => MI_FECHAC
                                             ,UN_OBS        => ' '
                                             ,UN_USUARIO    => UN_USUARIO);

      END LOOP INCLUIR_HISTORICO ;

END PR_NETOSRETROACTIVO;

PROCEDURE PR_CALCRETENCION
/*
    NAME              : PR_CALCRETENCION  
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 01/09/2015
    TIME              : 12:00 M
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    DESCRIPTION       : Actualiza el porcentaje fijo de retencion en la fuente
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA 
    DATE MODIFIED     : 22/03/2017
    TIME              : 03:13 PM
    MODIFICATIONS     : Se cambió el estándar de codificación y manejó de excepciones.

    @NAME:  calcRetencion
    @METHOD:  PUT 
  */
(
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_FECHA_COMBO2   IN DATE,
  UN_INGRESO        IN DATE,
  UN_VPORCENTAJE    IN PCK_SUBTIPOS.TI_DOBLE,
  UN_DOCUMENTO      IN PERSONAL.NUMERO_DCTO%TYPE,
  UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
)
AS
  MI_VPORCENTAJE     PCK_SUBTIPOS.TI_DOBLE;
  MI_UPDATE          PCK_SUBTIPOS.TI_ENTERO;
  MI_ID_EMPLEADO     PCK_SUBTIPOS.TI_DOBLE;
  MI_CAMPOS          PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION       PCK_SUBTIPOS.TI_CONDICION;
  MI_MSGERROR        PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
  MI_VPORCENTAJE:=UN_VPORCENTAJE;
  BEGIN
        SELECT  SUB.ID_EMPLEADO
          INTO  MI_ID_EMPLEADO
          FROM  (SELECT PERSONAL.ID_DE_EMPLEADO ID_EMPLEADO
                   FROM PERSONAL
                  WHERE PERSONAL.NUMERO_DCTO = UN_DOCUMENTO
                    AND PERSONAL.FECHA_DE_INGRESO = UN_INGRESO
                    AND PERSONAL.ESTADO_ACTUAL IN (1,2) 
               GROUP BY PERSONAL.ID_DE_EMPLEADO ORDER BY 1 DESC) SUB
        WHERE ROWNUM <2;

    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_ID_EMPLEADO:= 0;

  END;
  IF UN_INGRESO IS NULL THEN 
   BEGIN
    SELECT PERSONAL.ID_DE_EMPLEADO ID_EMPLEADO
      INTO MI_ID_EMPLEADO
      FROM PERSONAL
     WHERE PERSONAL.NUMERO_DCTO = UN_DOCUMENTO
       AND PERSONAL.ESTADO_ACTUAL IN (1,2) 
     GROUP BY PERSONAL.ID_DE_EMPLEADO ORDER BY 1 DESC;

     EXCEPTION WHEN NO_DATA_FOUND THEN
       MI_ID_EMPLEADO:= 0;

  END;
  END IF;

  IF (TO_CHAR(UN_INGRESO,'MM') = TO_CHAR(UN_FECHA_COMBO2,'MM')) AND (TO_CHAR(UN_INGRESO,'YYYY') = TO_CHAR(UN_FECHA_COMBO2,'YYYY')) THEN

      IF PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => TO_CHAR(UN_FECHA_COMBO2,'MM')
                                  ,UN_LONGITUD => 2) >= 11 OR PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO    => TO_CHAR(UN_FECHA_COMBO2,'MM')
                                                                                        ,UN_LONGITUD => 2) <= 1 THEN

        MI_CAMPOS := 'PROCESORETENCION = 1,
                      DATE_MODIFIED = SYSDATE,
                      MODIFIED_BY = '''||UN_USUARIO||'''';

        MI_CONDICION := 'COMPANIA      ='||UN_COMPANIA||' 
                    AND ID_DE_EMPLEADO ='||MI_ID_EMPLEADO||'';

       BEGIN
         BEGIN
            MI_UPDATE:=PCK_DATOS.FC_ACME(UN_TABLA     => 'PERSONAL'
                                        ,UN_ACCION    => 'M'
                                        ,UN_CAMPOS    => MI_CAMPOS
                                        ,UN_CONDICION => MI_CONDICION);

               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                    RAISE PCK_EXCEPCIONES.EXC_NOMINA;                                        

         END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN 
                       MI_MSGERROR(1).CLAVE := 'EMPLEADO';
                       MI_MSGERROR(1).VALOR := MI_ID_EMPLEADO;
                         PCK_ERR_MSG.RAISE_WITH_MSG(
                               UN_EXC_COD =>SQLCODE
                               ,UN_ERROR_COD=>PCK_ERRORES.ER_NOMINA_UPDATE_PERSONAL
                               ,UN_REEMPLAZOS  => MI_MSGERROR
                              );

       END;  
      END IF;

      MI_VPORCENTAJE:=0;
      PCK_NOMINA.PR_INCLUIRNOVEDAD(UN_COMPANIA   => UN_COMPANIA
                                  ,UN_PROCESO    => 0
                                  ,UN_ANIO       => 0
                                  ,UN_MES        => 0
                                  ,UN_PERIODO    => 0
                                  ,UN_IDEMPLEADO => MI_ID_EMPLEADO
                                  ,UN_IDCONCEPTO => 303
                                  ,UN_VALOR      => MI_VPORCENTAJE);
  ELSE
      PCK_NOMINA.PR_INCLUIRNOVEDAD(UN_COMPANIA   => UN_COMPANIA
                                  ,UN_PROCESO    => 0
                                  ,UN_ANIO       => 0
                                  ,UN_MES        => 0
                                  ,UN_PERIODO    => 0
                                  ,UN_IDEMPLEADO => MI_ID_EMPLEADO
                                  ,UN_IDCONCEPTO => 303
                                  ,UN_VALOR      => MI_VPORCENTAJE);

      IF PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO => TO_CHAR(UN_FECHA_COMBO2,'MM')
                                  ,UN_LONGITUD => 2) = '11' OR PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => TO_CHAR(UN_FECHA_COMBO2,'MM')
                                                                                        ,UN_LONGITUD => 2) <= '01' THEN
          MI_CAMPOS := 'PROCESORETENCION = 2,
                        DATE_MODIFIED = SYSDATE,
                        MODIFIED_BY = '''||UN_USUARIO||'''';    

          MI_CONDICION := 'COMPANIA      ='||UN_COMPANIA||'
                      AND ID_DE_EMPLEADO ='||MI_ID_EMPLEADO||'';

        BEGIN
         BEGIN
          MI_UPDATE:=PCK_DATOS.FC_ACME(UN_TABLA     => 'PERSONAL'
                                      ,UN_ACCION    => 'M'
                                      ,UN_CAMPOS    => MI_CAMPOS
                                      ,UN_CONDICION => MI_CONDICION);


              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                    RAISE PCK_EXCEPCIONES.EXC_NOMINA;                                        

         END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN 
                       MI_MSGERROR(1).CLAVE := 'EMPLEADO';
                       MI_MSGERROR(1).VALOR := MI_ID_EMPLEADO;
                         PCK_ERR_MSG.RAISE_WITH_MSG(
                               UN_EXC_COD =>SQLCODE
                               ,UN_ERROR_COD=>PCK_ERRORES.ER_NOMINA_UPDATE_PERSONAL
                               ,UN_REEMPLAZOS  => MI_MSGERROR
                              );
        END; 
      END IF;
  END IF;

-----------         
END PR_CALCRETENCION;

FUNCTION FC_DEDUCIBLEPREPAGADA

/*
    NAME              : FC_DEDUCIBLEPREPAGADA  
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 18/09/2015
    TIME              : 12:00 M
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    DESCRIPTION       : Trae desde la novedades el valor del concepto 301 para siempre a todos los empleados
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA 
    DATE MODIFIED     : 22/03/2017
    TIME              : 03:32 PM
    MODIFICATIONS     : Se cambió el estándar de codificación.


    @NAME:  deduciblePrepagada
    @METHOD:  GET 
  */
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_IDDEEMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO
  )
RETURN NUMBER
  AS
    MI_VALOR        PCK_SUBTIPOS.TI_DOBLE:=0;
BEGIN 
  BEGIN
    SELECT SUM(VALOR)
    INTO MI_VALOR
    FROM   NOVEDADES 
    WHERE NOVEDADES.COMPANIA        = UN_COMPANIA
      AND NOVEDADES.ID_DE_PROCESO   = 0
      AND NOVEDADES.ANO             = 0
      AND NOVEDADES.MES             = 0
      AND NOVEDADES.PERIODO         = 0
      AND NOVEDADES.ID_DE_CONCEPTO  IN (301)
      AND NOVEDADES.ID_DE_EMPLEADO  = UN_IDDEEMPLEADO;

     EXCEPTION WHEN NO_DATA_FOUND THEN
      RETURN 0;  
  END;
  IF MI_VALOR IS NULL THEN
     MI_VALOR:=0;
  END IF;
  RETURN MI_VALOR;

END FC_DEDUCIBLEPREPAGADA;

PROCEDURE PR_CARGARPARCALCRET

/*
    NAME              : PR_CARGARPARCALCRET  
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 14/01/2016
    TIME              : 12:00 M
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    DESCRIPTION       : carga los parametros para generar el calculo de porcentaje fijo de retencion
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA 
    DATE MODIFIED     : 22/03/2017
    TIME              : 03:40 PM
    MODIFICATIONS     : Se cambió el estándar de codificación.


    @NAME:  cargarParCalCret
    @METHOD:  GET 
  */
(
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO
)
  AS
    MI_UPDATE       PCK_SUBTIPOS.TI_ENTERO;
    MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
BEGIN 
    PCK_SYSMAN_UTL.PR_INSERTARPARAMETRO(UN_NOMBRE => 'VALOR UVT ACTUAL'
                                       ,UN_VALOR  => '23763'
                                       ,UN_FECHA  => TO_DATE('01/01/2000','DD/MM/YYYY')
                                       ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA
                                       ,UN_USUARIO=>UN_USUARIO);

    PCK_SYSMAN_UTL.PR_INSERTARPARAMETRO(UN_NOMBRE => 'VALOR UVT AÑO ANTERIOR'
                                       ,UN_VALOR  => '22054'
                                       ,UN_FECHA  => TO_DATE('01/01/2000','DD/MM/YYYY')
                                       ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA
                                       ,UN_USUARIO=>UN_USUARIO);

    PCK_SYSMAN_UTL.PR_INSERTARPARAMETRO(UN_NOMBRE => 'VALOR UVT MAXIMOS DEDUCIBLES SALUD/EDUCACION'
                                       ,UN_VALOR  => '4600'
                                       ,UN_FECHA  => TO_DATE('01/01/2000','DD/MM/YYYY')
                                       ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA
                                       ,UN_USUARIO=>UN_USUARIO);

    PCK_SYSMAN_UTL.PR_INSERTARPARAMETRO(UN_NOMBRE => 'VALOR UVT MAXIMOS DEDUCIBLES VIVIENDA'
                                       ,UN_VALOR  => '100'
                                       ,UN_FECHA  => TO_DATE('01/01/2000','DD/MM/YYYY')
                                       ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA
                                       ,UN_USUARIO=>UN_USUARIO);

    PCK_SYSMAN_UTL.PR_INSERTARPARAMETRO(UN_NOMBRE => 'VALOR UVT MAXIMOS RENTA EXCENTA'
                                       ,UN_VALOR  => '240'
                                       ,UN_FECHA  => TO_DATE('01/01/2000','DD/MM/YYYY')
                                       ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA
                                       ,UN_USUARIO=>UN_USUARIO);

    PCK_SYSMAN_UTL.PR_INSERTARPARAMETRO(UN_NOMBRE => 'VALOR LIMITE INGRESOS GRAVADOS RETENCION'
                                       ,UN_VALOR  => '30'
                                       ,UN_FECHA  => TO_DATE('01/01/2000','DD/MM/YYYY')
                                       ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA
                                       ,UN_USUARIO=>UN_USUARIO);

    PR_INCLUIRCONCEPTO(UN_NOMBRE => 'DESCUENTO RETEFUENTE SALUD EMPLEADO'
                      ,UN_CODIGO => 304
                      ,UN_TIPOC  => 1
                      ,UN_UNIDAD => 'Porcentaje'
                      ,UN_USUARIO=>UN_USUARIO);

    MI_CAMPOS := 'DEDRETENCION=0,
                  DATE_MODIFIED = SYSDATE,
                  MODIFIED_BY = '''||UN_USUARIO||'''';

    MI_CONDICION := 'COMPANIA            = '''||UN_COMPANIA||'''
                      AND ID_DE_CONCEPTO = 130';
    BEGIN
      BEGIN
        MI_UPDATE:=PCK_DATOS.FC_ACME(UN_TABLA     => 'CONCEPTOS'
                                    ,UN_ACCION    => 'M'
                                    ,UN_CAMPOS    => MI_CAMPOS
                                    ,UN_CONDICION => MI_CONDICION );
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_NOMINA;                                                  
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN 
           PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE
                 ,UN_ERROR_COD=>PCK_ERRORES.ER_NOMINA_UPDATE_CONCEPTO130
                );
    END;                                                                       
END PR_CARGARPARCALCRET;

FUNCTION FC_CALCULARRTFPARUNO
/*
    NAME              : FC_CALCULARRTFPARUNO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
    DATE MIGRADOR     : 24/10/2015
    TIME              : 12:30 PM
    SOURCE MODULE     :
    DESCRIPTION       : Primera parte transformacion de la funcion del calculo del porcentaje fijo de retencion en la fuente.
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    PARAMETERS        : UN_COMPANIA       => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                        UN_ANO            => ANIO PARA EL CUAL SE VA A REALIZAR EL CALCULO DEL PORCENTAJE FIJO DE RETENCION
                        UN_PROCESO        => PROCESO PARA EL CUAL SE VA A REALIZAR EL CALCULO DEL PORCENTAJE FIJO DE RETENCION
                        UN_FECHA_INICIAL  => FECHA INICIAL PARA EL CUAL SE VA A REALIZAR EL CALCULO DEL PORCENTAJE FIJO DE RETENCION
                        UN_FECHA_FINAL    => FECHA FINAL PARA EL CUAL SE VA A REALIZAR EL CALCULO DEL PORCENTAJE FIJO DE RETENCION

    @NAME:  calcularRtfParUno
    @METHOD:  GET
*/
(
  UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANO                IN PCK_SUBTIPOS.TI_ANIO,
  UN_PROCESO            IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
  UN_FECHA_INICIAL      IN VARCHAR2,
  UN_FECHA_FINAL        IN VARCHAR2,
  UN_CN309              IN PCK_SUBTIPOS.TI_LOGICO,
  UN_ANIOHASTADEDSALUD  IN PCK_SUBTIPOS.TI_LOGICO := 0,  --(MZANGUNA:14/01/2019)-Se agrega para tomar el año de la fecha final.
  UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN CLOB
AS
MI_RETORNO    CLOB;
MI_MATRIZ     TYPEMATRIZ;
BEGIN

  MI_MATRIZ :=  PCK_NOMINA_COM3.FC_CALCULAR_PORC_RETENCION(UN_FECHA_COMBO1 =>  UN_FECHA_INICIAL,
                                                        UN_FECHA_COMBO2         => UN_FECHA_FINAL,
                                                        UN_COMPANIA             => UN_COMPANIA,
                                                        UN_ANO                  => UN_ANO,
                                                        UN_PROCESO              => UN_PROCESO,
                                                        UN_CN309                => UN_CN309 ,
                                                        UN_ANIOHASTADEDSALUD    => UN_ANIOHASTADEDSALUD,
                                                        UN_USUARIO              => UN_USUARIO);

      FOR RS IN (SELECT  DISTINCT
                         CALCRETENCION.FILA,
                         CALCRETENCION.COLUMNA,
                         MAX(CALCRETENCION.VALOR) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) VALOR
                  FROM TABLE(MI_MATRIZ) CALCRETENCION
                  GROUP BY CALCRETENCION.FILA, CALCRETENCION.COLUMNA
                  ORDER BY 1,2)
      LOOP
        MI_RETORNO := MI_RETORNO  ||
                      TO_CLOB(RS.FILA     ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                              RS.COLUMNA  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                              RS.VALOR    ||  PCK_DATOS.GL_SEPARADOR_REG);
      END LOOP;

RETURN MI_RETORNO;
END FC_CALCULARRTFPARUNO;


FUNCTION FC_CALCULARRTFPARDOS
/*
    NAME              : FC_CALCULARRTFPARDOS
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
    DATE MIGRADOR     : 24/10/2015
    TIME              : 12:30 PM
    SOURCE MODULE     :
    DESCRIPTION       : Segunda parte transformacion de la funcion del calculo del porcentaje fijo de retencion en la fuente.
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    PARAMETERS        : UN_COMPANIA      => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                        UN_DOCUMENTO     => DOCUMENTO PARA EL CUAL SE VA A REALIZAR EL CALCULO DEL PORCENTAJE FIJO DE RETENCION
                        UN_PROMEDIO      => PROMEDIO PARA EL CUAL SE VA A REALIZAR EL CALCULO DEL PORCENTAJE FIJO DE RETENCION
                        UN_FECHA_COMBO1  => FECHA INICIAL PARA EL CUAL SE VA A REALIZAR EL CALCULO DEL PORCENTAJE FIJO DE RETENCION
                        UN_FECHA_COMBO2  => FECHA FINAL PARA EL CUAL SE VA A REALIZAR EL CALCULO DEL PORCENTAJE FIJO DE RETENCION
                        UN_IMESES        => MES PARA EL CUAL SE VA A REALIZAR EL CALCULO DEL PORCENTAJE FIJO DE RETENCION

    @NAME:  calcularRtfParDos
    @METHOD:  GET
*/
(
  UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_DOCUMENTO    IN PERSONAL.NUMERO_DCTO%TYPE,
  UN_PROMEDIO     IN PCK_SUBTIPOS.TI_DOBLE,
  UN_FECHA_COMBO1 IN DATE,
  UN_FECHA_COMBO2 IN DATE,
  UN_IMESES       IN PCK_SUBTIPOS.TI_DOBLE,
  UN_PROMEDIOC22  IN PCK_SUBTIPOS.TI_DOBLE,
  UN_ASIGNARPORRETEN  IN PCK_SUBTIPOS.TI_LOGICO,
  UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN CLOB
AS
MI_RETORNO    CLOB;
MI_MATRIZ     TYPEMATRIZ;
BEGIN
    --(MZANGUNA:11/01/2019)-Se agrega replace para quitar caracteres en el número de documento
    MI_MATRIZ := PCK_NOMINA_COM3.FC_CALCULARPORCRET(UN_COMPANIA      => UN_COMPANIA,
                                                UN_DOCUMENTO       => REPLACE(REPLACE(UN_DOCUMENTO,',',''),'.',''),
                                                UN_PROMEDIO        => UN_PROMEDIO,
                                                UN_FECHA_COMBO1    => UN_FECHA_COMBO1,
                                                UN_FECHA_COMBO2    => UN_FECHA_COMBO2,
                                                UN_IMESES          => UN_IMESES,
                                                UN_PROMEDIOC22     => UN_PROMEDIOC22,
                                                UN_ASIGNARPORRETEN => UN_ASIGNARPORRETEN,
                                                UN_USUARIO         => UN_USUARIO);

    FOR RS IN (SELECT  FILA, COLUMNA, VALOR
               FROM TABLE(MI_MATRIZ))
    LOOP
        MI_RETORNO:= MI_RETORNO ||
                     TO_CLOB(RS.FILA     ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                             RS.COLUMNA  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                             RS.VALOR    ||  PCK_DATOS.GL_SEPARADOR_REG);
    END LOOP;

    RETURN MI_RETORNO;
END FC_CALCULARRTFPARDOS;


FUNCTION FC_CALCULARRTFPARTRES
/*
    NAME              : FC_CALCULARRTFPARTRES
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
    DATE MIGRADOR     : 23/01/2018
    TIME              : 09:22 AM
    SOURCE MODULE     :
    DESCRIPTION       : Tercera parte transformacion de la funcion del calculo del porcentaje fijo de retencion en la fuente.
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    PARAMETERS        : UN_COMPANIA      => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                        UN_DOCUMENTO     => DOCUMENTO PARA EL CUAL SE VA A REALIZAR EL CALCULO DEL PORCENTAJE FIJO DE RETENCION
                        UN_PROMEDIO      => PROMEDIO PARA EL CUAL SE VA A REALIZAR EL CALCULO DEL PORCENTAJE FIJO DE RETENCION
                        UN_FECHA_COMBO1  => FECHA INICIAL PARA EL CUAL SE VA A REALIZAR EL CALCULO DEL PORCENTAJE FIJO DE RETENCION
                        UN_FECHA_COMBO2  => FECHA FINAL PARA EL CUAL SE VA A REALIZAR EL CALCULO DEL PORCENTAJE FIJO DE RETENCION
                        UN_IMESES        => MES PARA EL CUAL SE VA A REALIZAR EL CALCULO DEL PORCENTAJE FIJO DE RETENCION

    @NAME:  calcularRtfParTres
    @METHOD:  GET
*/
(
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO             IN PCK_SUBTIPOS.TI_ANIO,
    UN_FECHA_INICIAL   IN VARCHAR2,
    UN_FECHA_FINAL     IN VARCHAR2,
    UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN CLOB
AS
    MI_RETORNO    CLOB;
    MI_MATRIZ     TYPEMATRIZ;
BEGIN
    MI_MATRIZ :=  PCK_NOMINA_COM3.FC_CALCULARPROMEDIOSRETENCION
        (UN_COMPANIA     =>  UN_COMPANIA,
        UN_FECHA_COMBO1 =>  UN_FECHA_INICIAL,
        UN_FECHA_COMBO2 =>  UN_FECHA_FINAL,
        UN_USUARIO      =>  UN_USUARIO);

    FOR RS IN (SELECT  DISTINCT
                        CALCRETENCION.FILA,
                        CALCRETENCION.COLUMNA,
                        MAX(CALCRETENCION.VALOR) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) VALOR
                FROM TABLE(MI_MATRIZ) CALCRETENCION
                GROUP BY CALCRETENCION.FILA, CALCRETENCION.COLUMNA
                ORDER BY 1,2)
    LOOP
        MI_RETORNO := MI_RETORNO  ||
                    TO_CLOB(RS.FILA     ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                            RS.COLUMNA  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                            RS.VALOR    ||  PCK_DATOS.GL_SEPARADOR_REG);
    END LOOP;
  RETURN MI_RETORNO;
END FC_CALCULARRTFPARTRES;


FUNCTION FC_DEDUCUBLEACUMRETEF
/*
    NAME              : FC_DEDUCUBLEACUMRETEF
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
    DATE MIGRADOR     : 16/01/2018
    TIME              : 08:11 AM
    SOURCE MODULE     : NOMINAP2017.12.03 UNIFICADAS MPV- En access DEDUCIBLE_ACUMULADO_RETEFUENTE
    DESCRIPTION       : Función en access DEDUCIBLE_ACUMULADO_RETEFUENTE.
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :

    @NAME:  calcularPorcentajeRetencion
    @METHOD:  GET

  */

  (
     UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_EMPLEADO    IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO
    ,UN_ANO1        IN PCK_SUBTIPOS.TI_ANIO
    ,UN_MES1        IN PCK_SUBTIPOS.TI_MES
    ,UN_ANO2        IN PCK_SUBTIPOS.TI_ANIO
    ,UN_MES2        IN PCK_SUBTIPOS.TI_MES
    ,UN_PAR         IN PCK_SUBTIPOS.TI_TEXTO1
  ) RETURN NUMBER
AS
    MI_RTA              NUMBER DEFAULT 0;
    MI_VIVIENDA         NUMBER DEFAULT 0;
    MI_MEDICINA         NUMBER DEFAULT 0;
    MI_PERSONASCARGO    NUMBER DEFAULT 0;
    MI_PROMEMEDIOSALUD  NUMBER DEFAULT 0;
    MI_PARNOMINAMENSUAL PARAMETRO.VALOR%TYPE;

BEGIN
    MI_PARNOMINAMENSUAL := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA
                                ,UN_NOMBRE => 'NOMINA MENSUAL'
                                ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA
                                ,UN_FECHA_PAR => SYSDATE), 'NO');
    BEGIN
        SELECT NVL(SUM(DED_2_1_VIIVENDA),0) VIVIENDA,
               NVL(SUM(DED_2_2_MEDICINA),0) MEDICINA,
               NVL(SUM(DED_2_3_PERSONASACARGO),0) PERSONASACARGO,
               NVL(SUM(DED_2_4_PROMEDIOSALUD),0) PROMEDIOSALUD
        INTO   MI_VIVIENDA, MI_MEDICINA, MI_PERSONASCARGO, MI_PROMEMEDIOSALUD
        FROM   RETEFUENTE_CALCULOS
        WHERE  COMPANIA  = UN_COMPANIA
          AND  ID_DE_EMPLEADO = UN_EMPLEADO
          AND  (ANO = UN_ANO1 AND MES >= UN_MES1 OR ANO = UN_ANO2 AND MES <= UN_MES2)
          AND  PERIODO = CASE WHEN MI_PARNOMINAMENSUAL = 'SI' THEN 3 ELSE 2 END;

    EXCEPTION WHEN NO_DATA_FOUND THEN
        RETURN 0;
    END;

    IF UN_PAR = 'V' THEN
        MI_RTA := MI_VIVIENDA;
    ELSIF UN_PAR = 'M' THEN
        MI_RTA := MI_MEDICINA;
    ELSIF UN_PAR = 'P' THEN
        MI_RTA := MI_PERSONASCARGO;
    ELSIF UN_PAR = 'S' THEN
        MI_RTA := MI_PROMEMEDIOSALUD;
    END IF;

    RETURN MI_RTA;

END FC_DEDUCUBLEACUMRETEF;

FUNCTION FC_CALCULARPROMEDIOSRETENCION
/*
    NAME              : FC_CALCULARPROMEDIOSRETENCION
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
    DATE MIGRADOR     : 22/01/2018
    TIME              : 8:25 AM
    SOURCE MODULE     : NominaH2017.12.03 MPV UNIFICADAS 137 21122017
    DESCRIPTION       : Tercera parte del calculo del porcentaje fijo de retencion en la fuente.
    MODIFIER          : MIGUEL ANGEL ZANGUÑA HURTADO
    DATE MODIFIED     : 11/01/2018
    TIME              : 11:40 AM
    MODIFICATIONS     : Se adicionan cambios de última versión de utilitario NOMINAH2017.12.03 MPV UNIFICADAS 137 21122017

    @NAME:  FC_CALCULARPROMEDIOSRETENCION
    @METHOD:  GET

  */
(   UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHA_COMBO1  IN DATE,
    UN_FECHA_COMBO2  IN DATE,
    UN_USUARIO       IN PCK_SUBTIPOS.TI_USUARIO := ' '
) RETURN TYPEMATRIZ
AS
    NOMBRECOMPLETO                        PERSONAL.NOMBRECOMPLETO%TYPE;
    DOCUMENTO                             PERSONAL.NUMERO_DCTO%TYPE;
    MI_FECHA_COMBO1                       DATE;
    MI_FECHA_COMBO2                       DATE;
    MI_FECHA_1                            DATE;
    MI_FECHA_2                            DATE;
    MI_FECHA_3                            DATE;
    MI_FECHA_4                            DATE;
    MI_FECHA_TEMP                         DATE;
    MI_MATRIZ                             TYPEMATRIZ;
    MI_NOMBREEMPRESA                      VARCHAR2(100 CHAR);
    MI_SELECT                             VARCHAR2(1000 CHAR);
    MI_WHERE                              VARCHAR2(1000 CHAR);
    MI_QUERY                              VARCHAR2(2000 CHAR);
    MI_K                                  PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_L                                  PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    RS                                    SYS_REFCURSOR;
    MI_FECHAVARIABLE                      DATE;
    MI_FECHAVARIABLE2                     DATE;
    MI_RCDEVENGOSNTOTAL                   PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_RCDEVENGOSNCUENTA                  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;

    MI_RCDESCUENTOSNTOTAL                 PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_RCDESCUENTOSNCUENTA                PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;

    MI_DEDUCIBLE25DSTO                    PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_DEDUCIBLE30TOPE                    PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_TOTALMES                           PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_VALLIMINGGRAVRETENCION             PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PORCENTAJERENTAEXCENTA             PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_VALORUVTMAXIMOSRENTAEXCENTA        PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_UVTACTUAL                          PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_UVTANTERIOR                        PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_DEDUCIBLEACUMULADO                 PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CONTADOR                           PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTAFOR                          PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_TEXT                                VARCHAR2(2000 CHAR);
BEGIN
    EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_DATE_FORMAT=''DD/MM/YYYY''';

    MI_VALLIMINGGRAVRETENCION := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA ,UN_NOMBRE => 'VALOR LIMITE INGRESOS GRAVADOS RETENCION' ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA ,UN_FECHA_PAR => SYSDATE), 0.3));
    MI_VALLIMINGGRAVRETENCION := CASE WHEN MI_VALLIMINGGRAVRETENCION = 0.3 THEN MI_VALLIMINGGRAVRETENCION ELSE MI_VALLIMINGGRAVRETENCION / 100 END;
    MI_VALORUVTMAXIMOSRENTAEXCENTA := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA ,UN_NOMBRE => 'VALOR UVT MAXIMOS RENTA EXCENTA' ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA ,UN_FECHA_PAR => SYSDATE),240));
    MI_PORCENTAJERENTAEXCENTA := 1 - TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA ,UN_NOMBRE => 'VALOR PORCENTAJE APLICAR PROCESO RETEFUENTE' ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA ,UN_FECHA_PAR => SYSDATE) ,0));
    MI_UVTANTERIOR :=  TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA ,UN_NOMBRE => 'VALOR UVT AÑO ANTERIOR' ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA ,UN_FECHA_PAR => SYSDATE), 22054));
    MI_UVTACTUAL := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA ,UN_NOMBRE => 'VALOR UVT ACTUAL' ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA ,UN_FECHA_PAR => SYSDATE), 23763));

    MI_MATRIZ := TYPEMATRIZ();
    MI_FECHA_COMBO1 := TO_DATE(UN_FECHA_COMBO1,'DD/MM/YYYY');
    MI_FECHA_COMBO2 := TO_DATE(UN_FECHA_COMBO2,'DD/MM/YYYY');

    IF MI_FECHA_COMBO1 > MI_FECHA_COMBO2 THEN
        MI_FECHA_TEMP := MI_FECHA_COMBO1;
        MI_FECHA_COMBO1 := MI_FECHA_COMBO2;
        MI_FECHA_COMBO2 := MI_FECHA_TEMP;
    END IF;

    IF TO_CHAR(MI_FECHA_COMBO1, 'YYYY') >= 2012 THEN
        MI_FECHA_1 := TO_DATE(MI_FECHA_COMBO1,'DD/MM/YYYY');
        MI_FECHA_2 := TO_DATE('31/12/'||TO_CHAR(MI_FECHA_COMBO1, 'YYYY'),'DD/MM/YYYY');
        MI_FECHA_3 := TO_DATE('01/01/'||TO_CHAR(MI_FECHA_COMBO2, 'YYYY'),'DD/MM/YYYY');
        MI_FECHA_4 := MI_FECHA_COMBO2;
    ELSE
        MI_FECHA_1 := MI_FECHA_COMBO1;
        MI_FECHA_2 := MI_FECHA_COMBO2;
        MI_FECHA_3 := NULL;
        MI_FECHA_4 := NULL;
    END IF;

    BEGIN
        SELECT RAZONSOCIAL
        INTO   MI_NOMBREEMPRESA
        FROM   PARAMETROS_DE_ENTRADA
        WHERE  COMPANIA = UN_COMPANIA
          AND  ROWNUM = 1;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_NOMBREEMPRESA := 'Sysman Software';
    END;

    MI_SELECT := 'SELECT  NOMBRECOMPLETO NOMBRECOMPLETO,
                          NUMERO_DCTO DOCUMENTO
                  FROM    PERSONAL ';
    IF UN_COMPANIA = '002' THEN
        MI_WHERE := 'WHERE  PERSONAL.ID_DE_EMPLEADO <> 0
                       AND  PERSONAL.ESTADO_ACTUAL  = 2
                       AND  PERSONAL.COMPANIA       = '''||UN_COMPANIA||'''  ';
    ELSE
        MI_WHERE:='WHERE  PERSONAL.ID_DE_EMPLEADO <> 0
                     AND TRUNC(FECHA_DE_INGRESO)   <= TRUNC(TO_DATE('''||UN_FECHA_COMBO2||''',''DD/MM/YYYY HH24:MI:SS''))
                     AND ((ESTADO_ACTUAL    <>3 AND FECHA_DE_RETIRO    IS NULL) OR (ESTADO_ACTUAL <> 2 
                     AND TRUNC(FECHA_DE_RETIRO)    >=TRUNC(TO_DATE('''||UN_FECHA_COMBO1||''',''DD/MM/YYYY HH24:MI:SS''))  ) )
                     AND  PERSONAL.COMPANIA       = '''||UN_COMPANIA||'''  ';
    END IF;

    --20180615_3565@MZANGUNA - NJIMENEZ Si es segundo semestre solo debe mostrar los tipo retención porcentaje fijo
    IF PCK_SYSMAN_UTL.FC_MES(MI_FECHA_COMBO1) = 6 THEN
        MI_WHERE := MI_WHERE || ' AND PROCESORETENCION = 2 ';
    END IF;
    MI_WHERE := MI_WHERE || 'ORDER  BY PERSONAL.NOMBRECOMPLETO';

    MI_QUERY   := MI_SELECT||''||MI_WHERE;
    MI_K := 3;

    --Encabezado
    MI_MATRIZ.EXTEND();
    MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(0,0,TO_CHAR(SYSDATE, 'DD/MM/YYYY'));
    MI_MATRIZ.EXTEND();
    MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(1,0,MI_NOMBREEMPRESA);

    OPEN RS FOR MI_QUERY;
    LOOP
        FETCH RS
        INTO NOMBRECOMPLETO,
             DOCUMENTO;
        EXIT WHEN RS%NOTFOUND;

        --MI_CUENTAFOR := RS.COUNT;
        MI_FECHAVARIABLE := MI_FECHA_1;
        MI_DEDUCIBLEACUMULADO := 0;
        MI_L := 0;

        MI_K:= MI_K + 1;
        MI_MATRIZ.EXTEND();
        MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,0,NOMBRECOMPLETO);

        MI_K:= MI_K + 1;
        --Columna 1
        MI_TEXT := 'FECHA';
        MI_MATRIZ.EXTEND();
        MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,0, MI_TEXT);
        --Columna 2
        MI_MATRIZ.EXTEND();
        MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,1,'TOTAL INGRESOS');
        --Columna 3
        MI_MATRIZ.EXTEND();
        MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,2,'Pensión + AFC mensual completa INCRGO');
        --Columna 4
        MI_MATRIZ.EXTEND();
        MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,3,'Limitación 30% del Ing. Gravado acumulado');
        --Columna 5
        MI_MATRIZ.EXTEND();
        MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,4,'BASE');
        --Columna 6
        MI_MATRIZ.EXTEND();
        MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,5,'25% EXCENTO');
        --Columna 7
        MI_MATRIZ.EXTEND();
        MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,6,'LIMITE MENSUAL');

        IF PCK_SYSMAN_UTL.FC_MES(MI_FECHA_1) = 6 OR PCK_SYSMAN_UTL.FC_MES(MI_FECHA_1) = 7 THEN
            FOR i IN 7.. 13 LOOP
                MI_K := MI_K + 1;
                MI_L := 0;

                MI_FECHAVARIABLE2 := LAST_DAY(MI_FECHAVARIABLE);

                BEGIN
                    SELECT DISTINCT SUM(HISTORICOS.VALOR) TOTAL,COUNT(HISTORICOS.VALOR) CUENTA
                    INTO   MI_RCDEVENGOSNTOTAL, MI_RCDEVENGOSNCUENTA
                    FROM   PERSONAL INNER JOIN HISTORICOS
                                ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
                                AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                            INNER JOIN CONCEPTOS
                                ON CONCEPTOS.COMPANIA        = HISTORICOS.COMPANIA
                                AND CONCEPTOS.ID_DE_CONCEPTO = HISTORICOS.ID_DE_CONCEPTO
                            INNER JOIN PERIODOS
                                ON PERIODOS.COMPANIA       = HISTORICOS.COMPANIA
                                AND PERIODOS.ID_DE_PROCESO = HISTORICOS.ID_DE_PROCESO
                                AND PERIODOS.ANO           = HISTORICOS.ANO
                                AND PERIODOS.MES           = HISTORICOS.MES
                                AND PERIODOS.PERIODO       = HISTORICOS.PERIODO
                    WHERE HISTORICOS.COMPANIA  = UN_COMPANIA       
                      --(APINEDA:23/01/2019)-Se cambia filtro de fecha final del periodo por año y mes del periodo, debido a que por causa de una fecha mal configurada se estaban obteniendo datos inconsistentes TAR 1000089564
                      AND PERIODOS.ANO || PCK_SYSMAN_UTL.FC_STRZERO(PERIODOS.MES, 2) 
                      BETWEEN (PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_ANIO(MI_FECHAVARIABLE), 4) || PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_MES(MI_FECHAVARIABLE), 2)) 
                        AND (PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_ANIO(MI_FECHAVARIABLE2), 4) || PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_MES(MI_FECHAVARIABLE2), 2))
                      AND PERIODOS.ACUMULADO <> 0
                      AND CONCEPTOS.DEVRETENCION <> 0
                      AND PERSONAL.NUMERO_DCTO    = DOCUMENTO
                    GROUP BY PERSONAL.NUMERO_DCTO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_RCDEVENGOSNTOTAL := 0;
                    MI_RCDEVENGOSNCUENTA := 0;
                END;

                BEGIN
                    SELECT DISTINCT SUM(HISTORICOS.VALOR) TOTAL, COUNT(HISTORICOS.VALOR) CUENTA
                    INTO   MI_RCDESCUENTOSNTOTAL, MI_RCDESCUENTOSNCUENTA
                    FROM   PERSONAL INNER JOIN HISTORICOS
                                ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
                                AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                            INNER JOIN CONCEPTOS
                                ON CONCEPTOS.COMPANIA        = HISTORICOS.COMPANIA
                                AND CONCEPTOS.ID_DE_CONCEPTO = HISTORICOS.ID_DE_CONCEPTO
                            INNER JOIN PERIODOS
                                ON PERIODOS.COMPANIA       = HISTORICOS.COMPANIA
                                AND PERIODOS.ID_DE_PROCESO = HISTORICOS.ID_DE_PROCESO
                                AND PERIODOS.ANO           = HISTORICOS.ANO
                                AND PERIODOS.MES           = HISTORICOS.MES
                                AND PERIODOS.PERIODO       = HISTORICOS.PERIODO
                    WHERE HISTORICOS.COMPANIA  = UN_COMPANIA                   
                      --(APINEDA:23/01/2019)-Se cambia filtro de fecha final del periodo por año y mes del periodo, debido a que por causa de una fecha mal configurada se estaban obteniendo datos inconsistentes TAR 1000089564
                      AND PERIODOS.ANO || PCK_SYSMAN_UTL.FC_STRZERO(PERIODOS.MES, 2) 
                      BETWEEN (PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_ANIO(MI_FECHAVARIABLE), 4) || PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_MES(MI_FECHAVARIABLE), 2)) 
                        AND (PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_ANIO(MI_FECHAVARIABLE2), 4) || PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_MES(MI_FECHAVARIABLE2), 2))                      
                      AND PERIODOS.ACUMULADO <> 0
                      AND CONCEPTOS.DEDRETENCION <> 0
                      AND PERSONAL.NUMERO_DCTO    = DOCUMENTO
                    GROUP BY PERSONAL.NUMERO_DCTO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_RCDESCUENTOSNTOTAL := 0;
                    MI_RCDESCUENTOSNCUENTA := 0;
                END;

                MI_DEDUCIBLE25DSTO := 0;
                MI_DEDUCIBLE30TOPE := 0;
                MI_TOTALMES := 0;

                IF MI_RCDEVENGOSNCUENTA > 0 AND MI_RCDESCUENTOSNCUENTA > 0 THEN
                    IF MI_RCDESCUENTOSNTOTAL > (NVL(CASE WHEN MI_RCDEVENGOSNCUENTA > 0 THEN MI_RCDEVENGOSNTOTAL ELSE 0 END, 0) * MI_VALLIMINGGRAVRETENCION) THEN
                        MI_DEDUCIBLE30TOPE := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_RCDEVENGOSNCUENTA > 0 THEN MI_RCDEVENGOSNTOTAL ELSE 0 END * MI_VALLIMINGGRAVRETENCION, 0);
                        MI_TOTALMES := (CASE WHEN MI_RCDEVENGOSNCUENTA > 0 THEN MI_RCDEVENGOSNTOTAL ELSE 0 END - MI_DEDUCIBLE30TOPE);
                    ELSE
                        MI_TOTALMES := (CASE WHEN MI_RCDEVENGOSNCUENTA > 0 THEN MI_RCDEVENGOSNTOTAL ELSE 0 END - MI_RCDESCUENTOSNTOTAL);
                    END IF;
                ELSE
                    MI_TOTALMES := CASE WHEN MI_RCDEVENGOSNCUENTA > 0 THEN MI_RCDEVENGOSNTOTAL ELSE 0 END;
                END IF;

                --Columna 1
                MI_MATRIZ.EXTEND();
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,MI_L,TO_CHAR(MI_FECHAVARIABLE2, 'DD/MM/YYYY'));

                MI_L := MI_L + 1;

                IF (MI_TOTALMES * MI_PORCENTAJERENTAEXCENTA) > PCK_SYSMAN_UTL.FC_ROUND((MI_VALORUVTMAXIMOSRENTAEXCENTA * MI_UVTANTERIOR), -3) THEN --LIMITE MAXIMO DEL 25% Y TOPE DE 240 UVT AÑO ANTERIOR
                    MI_DEDUCIBLE25DSTO := PCK_SYSMAN_UTL.FC_ROUND((MI_VALORUVTMAXIMOSRENTAEXCENTA * MI_UVTANTERIOR), -3);
                ELSE
                    MI_DEDUCIBLE25DSTO := PCK_SYSMAN_UTL.FC_ROUND(MI_TOTALMES * MI_PORCENTAJERENTAEXCENTA, 0);
                END IF;


                MI_MATRIZ.EXTEND();
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,MI_L,CASE WHEN MI_RCDEVENGOSNCUENTA > 0 THEN MI_RCDEVENGOSNTOTAL ELSE 0 END);

                MI_L := MI_L + 1;
                IF MI_RCDESCUENTOSNCUENTA > 0 THEN
                    MI_MATRIZ.EXTEND();
                    MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,MI_L,NVL(MI_RCDESCUENTOSNTOTAL, 0)); --JM 08/092024 7750652 
                    --MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,MI_L,MI_RCDESCUENTOSNCUENTA); --comentado por JM el 08/09/2024 
                ELSE
                    MI_MATRIZ.EXTEND();
                    MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,MI_L,0);
                END IF;

                MI_L := MI_L + 1;
                IF MI_RCDESCUENTOSNCUENTA > 0 THEN
                    MI_MATRIZ.EXTEND();
                    MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,MI_L,NVL(CASE WHEN MI_DEDUCIBLE30TOPE > 0 THEN MI_DEDUCIBLE30TOPE ELSE MI_RCDESCUENTOSNTOTAL END , 0));
                ELSE
                    MI_MATRIZ.EXTEND();
                    MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,MI_L,NVL(CASE WHEN MI_DEDUCIBLE30TOPE > 0 THEN MI_DEDUCIBLE30TOPE ELSE 0 END, 0));
                END IF;

                MI_L := MI_L + 1;
                IF MI_RCDESCUENTOSNCUENTA > 0 THEN
                    MI_MATRIZ.EXTEND();
                    MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,MI_L, (CASE WHEN MI_RCDEVENGOSNCUENTA > 0 THEN MI_RCDEVENGOSNTOTAL ELSE 0 END - CASE WHEN MI_DEDUCIBLE30TOPE > 0 THEN MI_DEDUCIBLE30TOPE ELSE MI_RCDESCUENTOSNTOTAL END));
                ELSE
                    MI_MATRIZ.EXTEND();
                    MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,MI_L, (CASE WHEN MI_RCDEVENGOSNCUENTA > 0 THEN MI_RCDEVENGOSNTOTAL ELSE 0 END - CASE WHEN MI_DEDUCIBLE30TOPE > 0 THEN MI_DEDUCIBLE30TOPE ELSE 0 END));
                END IF;

                MI_L := MI_L + 1;
                MI_MATRIZ.EXTEND();
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,MI_L, PCK_SYSMAN_UTL.FC_ROUND(MI_TOTALMES * MI_PORCENTAJERENTAEXCENTA, 0) );

                MI_L := MI_L + 1;
                MI_MATRIZ.EXTEND();
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,MI_L, MI_DEDUCIBLE25DSTO );

                MI_DEDUCIBLEACUMULADO := MI_DEDUCIBLEACUMULADO + MI_DEDUCIBLE25DSTO;
                MI_FECHAVARIABLE := MI_FECHAVARIABLE2 + 1;
            END LOOP;

            MI_FECHAVARIABLE := MI_FECHAVARIABLE2 +1;
            MI_L := 0;

            FOR i IN 1.. 5 LOOP
            	MI_K := MI_K + 1;
            	MI_L := 0;
            	MI_FECHAVARIABLE2 := LAST_DAY(MI_FECHAVARIABLE);

                BEGIN
                    SELECT DISTINCT SUM(HISTORICOS.VALOR) TOTAL,COUNT(HISTORICOS.VALOR) CUENTA
                    INTO   MI_RCDEVENGOSNTOTAL, MI_RCDEVENGOSNCUENTA
                    FROM   PERSONAL INNER JOIN HISTORICOS
                                ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
                                AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                            INNER JOIN CONCEPTOS
                                ON CONCEPTOS.COMPANIA        = HISTORICOS.COMPANIA
                                AND CONCEPTOS.ID_DE_CONCEPTO = HISTORICOS.ID_DE_CONCEPTO
                            INNER JOIN PERIODOS
                                ON PERIODOS.COMPANIA       = HISTORICOS.COMPANIA
                                AND PERIODOS.ID_DE_PROCESO = HISTORICOS.ID_DE_PROCESO
                                AND PERIODOS.ANO           = HISTORICOS.ANO
                                AND PERIODOS.MES           = HISTORICOS.MES
                                AND PERIODOS.PERIODO       = HISTORICOS.PERIODO
                    WHERE HISTORICOS.COMPANIA  = UN_COMPANIA              
                      --(APINEDA:23/01/2019)-Se cambia filtro de fecha final del periodo por año y mes del periodo, debido a que por causa de una fecha mal configurada se estaban obteniendo datos inconsistentes TAR 1000089564
                      AND PERIODOS.ANO || PCK_SYSMAN_UTL.FC_STRZERO(PERIODOS.MES, 2) 
                      BETWEEN (PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_ANIO(MI_FECHAVARIABLE), 4) || PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_MES(MI_FECHAVARIABLE), 2)) 
                        AND (PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_ANIO(MI_FECHAVARIABLE2), 4) || PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_MES(MI_FECHAVARIABLE2), 2))
                      AND PERIODOS.ACUMULADO <> 0
                      AND CONCEPTOS.DEVRETENCION <> 0
                      AND PERSONAL.NUMERO_DCTO    = DOCUMENTO
                    GROUP BY PERSONAL.NUMERO_DCTO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_RCDEVENGOSNTOTAL := 0;
                    MI_RCDEVENGOSNCUENTA := 0;
                END;

                BEGIN
                    SELECT DISTINCT SUM(HISTORICOS.VALOR) TOTAL, COUNT(HISTORICOS.VALOR) CUENTA
                    INTO   MI_RCDESCUENTOSNTOTAL, MI_RCDESCUENTOSNCUENTA
                    FROM   PERSONAL INNER JOIN HISTORICOS
                                ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
                                AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                            INNER JOIN CONCEPTOS
                                ON CONCEPTOS.COMPANIA        = HISTORICOS.COMPANIA
                                AND CONCEPTOS.ID_DE_CONCEPTO = HISTORICOS.ID_DE_CONCEPTO
                            INNER JOIN PERIODOS
                                ON PERIODOS.COMPANIA       = HISTORICOS.COMPANIA
                                AND PERIODOS.ID_DE_PROCESO = HISTORICOS.ID_DE_PROCESO
                                AND PERIODOS.ANO           = HISTORICOS.ANO
                                AND PERIODOS.MES           = HISTORICOS.MES
                                AND PERIODOS.PERIODO       = HISTORICOS.PERIODO
                    WHERE HISTORICOS.COMPANIA  = UN_COMPANIA  
                      --(APINEDA:23/01/2019)-Se cambia filtro de fecha final del periodo por año y mes del periodo, debido a que por causa de una fecha mal configurada se estaban obteniendo datos inconsistentes TAR 1000089564
                      AND PERIODOS.ANO || PCK_SYSMAN_UTL.FC_STRZERO(PERIODOS.MES, 2) 
                      BETWEEN (PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_ANIO(MI_FECHAVARIABLE), 4) || PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_MES(MI_FECHAVARIABLE), 2)) 
                        AND (PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_ANIO(MI_FECHAVARIABLE2), 4) || PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_MES(MI_FECHAVARIABLE2), 2))                      
                      AND PERIODOS.ACUMULADO <> 0
                      AND CONCEPTOS.DEDRETENCION <> 0
                      AND PERSONAL.NUMERO_DCTO    = DOCUMENTO
                    GROUP BY PERSONAL.NUMERO_DCTO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_RCDESCUENTOSNTOTAL := 0;
                    MI_RCDESCUENTOSNCUENTA := 0;
                END;

            	MI_DEDUCIBLE25DSTO := 0;
            	MI_TOTALMES := 0;
            	MI_DEDUCIBLE30TOPE := 0;

            	IF MI_RCDESCUENTOSNCUENTA > 0 THEN
            		IF NVL(MI_RCDESCUENTOSNTOTAL, 0) > NVL(CASE WHEN MI_RCDEVENGOSNCUENTA > 0 THEN MI_RCDEVENGOSNTOTAL ELSE 0 END, 0) * MI_VALLIMINGGRAVRETENCION THEN
            			MI_DEDUCIBLE30TOPE := PCK_SYSMAN_UTL.FC_ROUND(NVL(CASE WHEN MI_RCDEVENGOSNCUENTA > 0 THEN MI_RCDEVENGOSNTOTAL ELSE 0 END, 0) * MI_VALLIMINGGRAVRETENCION, 0);
            			MI_TOTALMES := NVL(CASE WHEN MI_RCDEVENGOSNCUENTA > 0 THEN MI_RCDEVENGOSNTOTAL ELSE 0 END, 0) - NVL(MI_DEDUCIBLE30TOPE, 0);
            		ELSE
            			MI_TOTALMES := NVL(CASE WHEN MI_RCDEVENGOSNCUENTA > 0 THEN MI_RCDEVENGOSNTOTAL ELSE 0 END, 0) - NVL(MI_RCDESCUENTOSNTOTAL, 0);
            		END IF;
            	ELSE
            		MI_TOTALMES := NVL(CASE WHEN MI_RCDEVENGOSNCUENTA > 0 THEN MI_RCDEVENGOSNTOTAL ELSE 0 END, 0);
            	END IF;

                MI_MATRIZ.EXTEND();
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,MI_L,TO_CHAR(MI_FECHAVARIABLE2, 'DD/MM/YYYY'));

                MI_L := MI_L + 1;
            	IF (MI_TOTALMES * MI_PORCENTAJERENTAEXCENTA) > PCK_SYSMAN_UTL.FC_ROUND((MI_VALORUVTMAXIMOSRENTAEXCENTA * MI_UVTACTUAL), -3) THEN
            		MI_DEDUCIBLE25DSTO := PCK_SYSMAN_UTL.FC_ROUND((MI_VALORUVTMAXIMOSRENTAEXCENTA * MI_UVTACTUAL), -3);
            	ELSE
            		MI_DEDUCIBLE25DSTO := PCK_SYSMAN_UTL.FC_ROUND(MI_TOTALMES * MI_PORCENTAJERENTAEXCENTA, 0);
            	END IF;

                MI_MATRIZ.EXTEND();
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,MI_L,NVL(CASE WHEN MI_RCDEVENGOSNCUENTA > 0 THEN MI_RCDEVENGOSNTOTAL ELSE 0 END, 0));

                MI_L := MI_L + 1;
            	IF MI_RCDESCUENTOSNCUENTA > 0 THEN
                    MI_MATRIZ.EXTEND();
                    MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,MI_L,NVL(MI_RCDESCUENTOSNTOTAL, 0));
            	ELSE
                    MI_MATRIZ.EXTEND();
                    MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,MI_L,0);
            	END IF;

            	MI_L := MI_L + 1;
            	IF MI_RCDESCUENTOSNCUENTA > 0 THEN
                    MI_MATRIZ.EXTEND();
                    MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,MI_L,NVL(CASE WHEN MI_DEDUCIBLE30TOPE > 0 THEN MI_DEDUCIBLE30TOPE ELSE MI_RCDESCUENTOSNTOTAL END, 0));
            	ELSE
                    MI_MATRIZ.EXTEND();
                    MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,MI_L, NVL(CASE WHEN MI_DEDUCIBLE30TOPE > 0 THEN MI_DEDUCIBLE30TOPE ELSE 0 END, 0));
            	END IF;

            	MI_L := MI_L + 1;
            	IF MI_RCDESCUENTOSNCUENTA > 0 THEN
                    MI_MATRIZ.EXTEND();
                    MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,MI_L, (NVL(CASE WHEN MI_RCDEVENGOSNCUENTA > 0 THEN MI_RCDEVENGOSNTOTAL ELSE 0 END, 0) - NVL(CASE WHEN MI_DEDUCIBLE30TOPE > 0 THEN MI_DEDUCIBLE30TOPE ELSE MI_RCDESCUENTOSNTOTAL END, 0)));
            	ELSE
                    MI_MATRIZ.EXTEND();
                    MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,MI_L, (NVL(CASE WHEN MI_RCDEVENGOSNCUENTA > 0 THEN MI_RCDEVENGOSNTOTAL ELSE 0 END, 0) - NVL(CASE WHEN MI_DEDUCIBLE30TOPE > 0 THEN MI_DEDUCIBLE30TOPE ELSE 0 END, 0)));
            	END IF;

            	MI_L := MI_L + 1;
                MI_MATRIZ.EXTEND();
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,MI_L,NVL(PCK_SYSMAN_UTL.FC_ROUND(MI_TOTALMES * MI_PORCENTAJERENTAEXCENTA, 0), 0));

            	MI_L := MI_L + 1;
                MI_MATRIZ.EXTEND();
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,MI_L,NVL(MI_DEDUCIBLE25DSTO, 0));


            	MI_DEDUCIBLEACUMULADO := MI_DEDUCIBLEACUMULADO + MI_DEDUCIBLE25DSTO;
            	MI_FECHAVARIABLE := MI_FECHAVARIABLE2 + 1;

            END LOOP;
        ELSE
            --MZ
            --FOR i IN 12..12 LOOP
            MI_K := MI_K + 1;
            MI_L := 0;
            MI_FECHAVARIABLE2 := LAST_DAY(MI_FECHAVARIABLE);

            BEGIN
                SELECT DISTINCT SUM(HISTORICOS.VALOR) TOTAL,COUNT(HISTORICOS.VALOR) CUENTA
                INTO   MI_RCDEVENGOSNTOTAL, MI_RCDEVENGOSNCUENTA
                FROM   PERSONAL INNER JOIN HISTORICOS
                            ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
                            AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                        INNER JOIN CONCEPTOS
                            ON CONCEPTOS.COMPANIA        = HISTORICOS.COMPANIA
                            AND CONCEPTOS.ID_DE_CONCEPTO = HISTORICOS.ID_DE_CONCEPTO
                        INNER JOIN PERIODOS
                            ON PERIODOS.COMPANIA       = HISTORICOS.COMPANIA
                            AND PERIODOS.ID_DE_PROCESO = HISTORICOS.ID_DE_PROCESO
                            AND PERIODOS.ANO           = HISTORICOS.ANO
                            AND PERIODOS.MES           = HISTORICOS.MES
                            AND PERIODOS.PERIODO       = HISTORICOS.PERIODO
                WHERE HISTORICOS.COMPANIA  = UN_COMPANIA     
                  --(APINEDA:23/01/2019)-Se cambia filtro de fecha final del periodo por año y mes del periodo, debido a que por causa de una fecha mal configurada se estaban obteniendo datos inconsistentes TAR 1000089564
                  AND PERIODOS.ANO || PCK_SYSMAN_UTL.FC_STRZERO(PERIODOS.MES, 2) 
                  BETWEEN (PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_ANIO(MI_FECHAVARIABLE), 4) || PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_MES(MI_FECHAVARIABLE), 2)) 
                    AND (PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_ANIO(MI_FECHAVARIABLE2), 4) || PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_MES(MI_FECHAVARIABLE2), 2))                                  
                  AND PERIODOS.ACUMULADO <> 0
                  AND CONCEPTOS.DEVRETENCION <> 0
                  AND PERSONAL.NUMERO_DCTO    = DOCUMENTO
                GROUP BY PERSONAL.NUMERO_DCTO;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_RCDEVENGOSNTOTAL := 0;
                MI_RCDEVENGOSNCUENTA := 0;
            END;

            BEGIN
                SELECT DISTINCT SUM(HISTORICOS.VALOR) TOTAL, COUNT(HISTORICOS.VALOR) CUENTA
                INTO   MI_RCDESCUENTOSNTOTAL, MI_RCDESCUENTOSNCUENTA
                FROM   PERSONAL INNER JOIN HISTORICOS
                            ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
                            AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                        INNER JOIN CONCEPTOS
                            ON CONCEPTOS.COMPANIA        = HISTORICOS.COMPANIA
                            AND CONCEPTOS.ID_DE_CONCEPTO = HISTORICOS.ID_DE_CONCEPTO
                        INNER JOIN PERIODOS
                            ON PERIODOS.COMPANIA       = HISTORICOS.COMPANIA
                            AND PERIODOS.ID_DE_PROCESO = HISTORICOS.ID_DE_PROCESO
                            AND PERIODOS.ANO           = HISTORICOS.ANO
                            AND PERIODOS.MES           = HISTORICOS.MES
                            AND PERIODOS.PERIODO       = HISTORICOS.PERIODO
                WHERE HISTORICOS.COMPANIA  = UN_COMPANIA  
                  --(APINEDA:23/01/2019)-Se cambia filtro de fecha final del periodo por año y mes del periodo, debido a que por causa de una fecha mal configurada se estaban obteniendo datos inconsistentes TAR 1000089564
                  AND PERIODOS.ANO || PCK_SYSMAN_UTL.FC_STRZERO(PERIODOS.MES, 2) 
                  BETWEEN (PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_ANIO(MI_FECHAVARIABLE), 4) || PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_MES(MI_FECHAVARIABLE), 2)) 
                    AND (PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_ANIO(MI_FECHAVARIABLE2), 4) || PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_MES(MI_FECHAVARIABLE2), 2))
                  AND PERIODOS.ACUMULADO <> 0
                  AND CONCEPTOS.DEDRETENCION <> 0
                  AND PERSONAL.NUMERO_DCTO    = DOCUMENTO
                GROUP BY PERSONAL.NUMERO_DCTO;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_RCDESCUENTOSNTOTAL := 0;
                MI_RCDESCUENTOSNCUENTA := 0;
            END;

            MI_DEDUCIBLE25DSTO := 0;
            MI_TOTALMES := 0;
            MI_DEDUCIBLE30TOPE := 0;

            IF MI_RCDESCUENTOSNCUENTA > 0 THEN
            		IF NVL(MI_RCDESCUENTOSNTOTAL, 0) > (NVL(CASE WHEN MI_RCDEVENGOSNTOTAL > 0 THEN MI_RCDEVENGOSNTOTAL ELSE 0 END, 0) * MI_VALLIMINGGRAVRETENCION) THEN
                  MI_DEDUCIBLE30TOPE := PCK_SYSMAN_UTL.FC_ROUND(NVL(CASE WHEN MI_RCDEVENGOSNTOTAL > 0 THEN MI_RCDEVENGOSNTOTAL ELSE 0 END, 0) * MI_VALLIMINGGRAVRETENCION, 0);
                  MI_TOTALMES := NVL(CASE WHEN MI_RCDEVENGOSNTOTAL > 0 THEN MI_RCDEVENGOSNTOTAL ELSE 0 END, 0) - NVL(MI_DEDUCIBLE30TOPE, 0);
                ELSE
                  MI_TOTALMES := NVL(CASE WHEN MI_RCDEVENGOSNTOTAL > 0 THEN MI_RCDEVENGOSNTOTAL ELSE 0 END, 0) - NVL(MI_RCDESCUENTOSNTOTAL, 0);
                END IF;
            ELSE
          		MI_TOTALMES := NVL(CASE WHEN MI_RCDEVENGOSNTOTAL > 0 THEN MI_RCDEVENGOSNTOTAL ELSE 0 END, 0);
            END IF;

            MI_MATRIZ.EXTEND();
            MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,MI_L,TO_CHAR(MI_FECHAVARIABLE2, 'DD/MM/YYYY'));

            MI_L := MI_L + 1;
            IF (MI_TOTALMES * MI_PORCENTAJERENTAEXCENTA) > PCK_SYSMAN_UTL.FC_ROUND((MI_VALORUVTMAXIMOSRENTAEXCENTA * MI_UVTANTERIOR), -3) THEN
                MI_DEDUCIBLE25DSTO := PCK_SYSMAN_UTL.FC_ROUND((MI_VALORUVTMAXIMOSRENTAEXCENTA * MI_UVTANTERIOR), -3);
            ELSE
                MI_DEDUCIBLE25DSTO := PCK_SYSMAN_UTL.FC_ROUND(MI_TOTALMES * MI_PORCENTAJERENTAEXCENTA, 0);
            END IF;

            MI_MATRIZ.EXTEND();
            MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,MI_L,NVL(CASE WHEN MI_RCDEVENGOSNTOTAL > 0 THEN MI_RCDEVENGOSNTOTAL ELSE 0 END, 0));

            MI_L := MI_L + 1;
            IF MI_RCDESCUENTOSNCUENTA > 0 THEN
                  MI_MATRIZ.EXTEND();
                  MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,MI_L,NVL(MI_RCDESCUENTOSNTOTAL, 0));
            ELSE
                  MI_MATRIZ.EXTEND();
                  MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,MI_L,0);
            END IF;

            MI_L := MI_L + 1;
            IF MI_RCDESCUENTOSNCUENTA > 0 THEN
                  MI_MATRIZ.EXTEND();
                  MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,MI_L,NVL(CASE WHEN MI_DEDUCIBLE30TOPE > 0 THEN MI_DEDUCIBLE30TOPE ELSE MI_RCDESCUENTOSNTOTAL END, 0));
            ELSE
                  MI_MATRIZ.EXTEND();
                  MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,MI_L,NVL(CASE WHEN MI_DEDUCIBLE30TOPE > 0 THEN MI_DEDUCIBLE30TOPE ELSE 0 END, 0));
            END IF;

            --MZ
            MI_L := MI_L + 1;

            IF MI_RCDESCUENTOSNCUENTA > 0 THEN
                  MI_MATRIZ.EXTEND();
                  MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,MI_L, NVL(CASE WHEN MI_RCDEVENGOSNTOTAL > 0 THEN MI_RCDEVENGOSNTOTAL ELSE 0 END, 0) - NVL(CASE WHEN MI_DEDUCIBLE30TOPE > 0 THEN MI_DEDUCIBLE30TOPE ELSE MI_RCDESCUENTOSNTOTAL END, 0));
            ELSE
                  MI_MATRIZ.EXTEND();
                  MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K,MI_L, NVL(CASE WHEN MI_RCDEVENGOSNTOTAL > 0 THEN MI_RCDEVENGOSNTOTAL ELSE 0 END, 0) - NVL(CASE WHEN MI_DEDUCIBLE30TOPE > 0 THEN MI_DEDUCIBLE30TOPE ELSE 0 END, 0));
            END IF;

            MI_L := MI_L + 1;
            MI_MATRIZ.EXTEND();
            MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K, MI_L, NVL(PCK_SYSMAN_UTL.FC_ROUND(MI_TOTALMES * MI_PORCENTAJERENTAEXCENTA, 0), 0));

            MI_L := MI_L + 1;
            MI_MATRIZ.EXTEND();
            MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K, MI_L, NVL(MI_DEDUCIBLE25DSTO, 0));

            MI_DEDUCIBLEACUMULADO := MI_DEDUCIBLEACUMULADO + MI_DEDUCIBLE25DSTO;
            MI_FECHAVARIABLE := MI_FECHAVARIABLE2 + 1;

            --NEXT I

            MI_FECHAVARIABLE := MI_FECHAVARIABLE2 + 1;
            MI_L := 0;

            FOR i IN 1.. 11 LOOP
                MI_K := MI_K + 1;
                MI_L := 0;
                MI_FECHAVARIABLE2 := LAST_DAY(MI_FECHAVARIABLE);

                BEGIN
                    SELECT DISTINCT SUM(HISTORICOS.VALOR) TOTAL,COUNT(HISTORICOS.VALOR) CUENTA
                    INTO   MI_RCDEVENGOSNTOTAL, MI_RCDEVENGOSNCUENTA
                    FROM   PERSONAL INNER JOIN HISTORICOS
                                ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
                                AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                            INNER JOIN CONCEPTOS
                                ON CONCEPTOS.COMPANIA        = HISTORICOS.COMPANIA
                                AND CONCEPTOS.ID_DE_CONCEPTO = HISTORICOS.ID_DE_CONCEPTO
                            INNER JOIN PERIODOS
                                ON PERIODOS.COMPANIA       = HISTORICOS.COMPANIA
                                AND PERIODOS.ID_DE_PROCESO = HISTORICOS.ID_DE_PROCESO
                                AND PERIODOS.ANO           = HISTORICOS.ANO
                                AND PERIODOS.MES           = HISTORICOS.MES
                                AND PERIODOS.PERIODO       = HISTORICOS.PERIODO
                    WHERE HISTORICOS.COMPANIA  = UN_COMPANIA     
                      --(APINEDA:23/01/2019)-Se cambia filtro de fecha final del periodo por año y mes del periodo, debido a que por causa de una fecha mal configurada se estaban obteniendo datos inconsistentes TAR 1000089564
                      AND PERIODOS.ANO || PCK_SYSMAN_UTL.FC_STRZERO(PERIODOS.MES, 2) 
                      BETWEEN (PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_ANIO(MI_FECHAVARIABLE), 4) || PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_MES(MI_FECHAVARIABLE), 2)) 
                        AND (PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_ANIO(MI_FECHAVARIABLE2), 4) || PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_MES(MI_FECHAVARIABLE2), 2))                      
                      AND PERIODOS.ACUMULADO <> 0
                      AND CONCEPTOS.DEVRETENCION <> 0
                      AND PERSONAL.NUMERO_DCTO   = DOCUMENTO
                    GROUP BY PERSONAL.NUMERO_DCTO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_RCDEVENGOSNTOTAL := 0;
                    MI_RCDEVENGOSNCUENTA := 0;
                END;

            	--REALIZAR LA ACUMULACION SOBRE LOS CONCEPTOS DE DESCUENTO
                BEGIN
                    SELECT DISTINCT SUM(HISTORICOS.VALOR) TOTAL, COUNT(HISTORICOS.VALOR) CUENTA
                    INTO   MI_RCDESCUENTOSNTOTAL, MI_RCDESCUENTOSNCUENTA
                    FROM   PERSONAL INNER JOIN HISTORICOS
                                ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
                                AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                            INNER JOIN CONCEPTOS
                                ON CONCEPTOS.COMPANIA        = HISTORICOS.COMPANIA
                                AND CONCEPTOS.ID_DE_CONCEPTO = HISTORICOS.ID_DE_CONCEPTO
                            INNER JOIN PERIODOS
                                ON PERIODOS.COMPANIA       = HISTORICOS.COMPANIA
                                AND PERIODOS.ID_DE_PROCESO = HISTORICOS.ID_DE_PROCESO
                                AND PERIODOS.ANO           = HISTORICOS.ANO
                                AND PERIODOS.MES           = HISTORICOS.MES
                                AND PERIODOS.PERIODO       = HISTORICOS.PERIODO
                    WHERE HISTORICOS.COMPANIA  = UN_COMPANIA      
                      --(APINEDA:23/01/2019)-Se cambia filtro de fecha final del periodo por año y mes del periodo, debido a que por causa de una fecha mal configurada se estaban obteniendo datos inconsistentes TAR 1000089564
                      AND PERIODOS.ANO || PCK_SYSMAN_UTL.FC_STRZERO(PERIODOS.MES, 2) 
                      BETWEEN (PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_ANIO(MI_FECHAVARIABLE), 4) || PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_MES(MI_FECHAVARIABLE), 2)) 
                        AND (PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_ANIO(MI_FECHAVARIABLE2), 4) || PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_MES(MI_FECHAVARIABLE2), 2))                                          
                      AND PERIODOS.ACUMULADO <> 0
                      AND CONCEPTOS.DEDRETENCION <> 0
                      AND PERSONAL.NUMERO_DCTO    = DOCUMENTO
                    GROUP BY PERSONAL.NUMERO_DCTO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_RCDESCUENTOSNTOTAL := 0;
                    MI_RCDESCUENTOSNCUENTA := 0;
                END;

                MI_DEDUCIBLE25DSTO := 0;
                MI_TOTALMES := 0;
                MI_DEDUCIBLE30TOPE := 0;

                IF MI_RCDESCUENTOSNCUENTA > 0 THEN
                  IF NVL(MI_RCDESCUENTOSNTOTAL, 0) > NVL(MI_RCDEVENGOSNTOTAL, 0) * MI_VALLIMINGGRAVRETENCION THEN
                    MI_DEDUCIBLE30TOPE := PCK_SYSMAN_UTL.FC_ROUND(NVL(MI_RCDEVENGOSNTOTAL, 0) * MI_VALLIMINGGRAVRETENCION, 0);
                    MI_TOTALMES := NVL(MI_RCDEVENGOSNTOTAL, 0) - NVL(MI_DEDUCIBLE30TOPE, 0);
                  ELSE
                    MI_TOTALMES := NVL(CASE WHEN MI_RCDEVENGOSNCUENTA > 0 THEN MI_RCDEVENGOSNTOTAL ELSE 0 END, 0) - NVL(MI_RCDESCUENTOSNTOTAL, 0);
                  END IF;
                ELSE
                  MI_TOTALMES := NVL(CASE WHEN MI_RCDEVENGOSNCUENTA > 0 THEN MI_RCDEVENGOSNTOTAL ELSE 0 END, 0);
                END IF;

                MI_MATRIZ.EXTEND();
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K, MI_L, TO_CHAR(MI_FECHAVARIABLE2, 'DD/MM/YYYY'));

                MI_L := MI_L + 1;
                IF (MI_TOTALMES * MI_PORCENTAJERENTAEXCENTA) > PCK_SYSMAN_UTL.FC_ROUND((MI_VALORUVTMAXIMOSRENTAEXCENTA * MI_UVTACTUAL), -3) THEN
                  MI_DEDUCIBLE25DSTO := PCK_SYSMAN_UTL.FC_ROUND((MI_VALORUVTMAXIMOSRENTAEXCENTA * MI_UVTACTUAL), -3);
                ELSE
                  MI_DEDUCIBLE25DSTO := PCK_SYSMAN_UTL.FC_ROUND(MI_TOTALMES * MI_PORCENTAJERENTAEXCENTA, 0);
                END IF;

                MI_MATRIZ.EXTEND();
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K, MI_L, NVL(CASE WHEN MI_RCDEVENGOSNCUENTA > 0 THEN MI_RCDEVENGOSNTOTAL ELSE 0 END, 0));

                MI_L := MI_L + 1;
                IF MI_RCDESCUENTOSNCUENTA > 0 THEN
                      MI_MATRIZ.EXTEND();
                      MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K, MI_L,  NVL(MI_RCDESCUENTOSNTOTAL, 0));
                ELSE
                      MI_MATRIZ.EXTEND();
                      MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K, MI_L, 0);
                END IF;

                MI_L := MI_L + 1;
                IF MI_RCDESCUENTOSNCUENTA > 0 THEN
                      MI_MATRIZ.EXTEND();
                      MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K, MI_L, NVL(CASE WHEN MI_DEDUCIBLE30TOPE > 0 THEN MI_DEDUCIBLE30TOPE ELSE MI_RCDESCUENTOSNTOTAL END, 0));
                ELSE
                      MI_MATRIZ.EXTEND();
                      MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K, MI_L, NVL(CASE WHEN MI_DEDUCIBLE30TOPE > 0 THEN MI_DEDUCIBLE30TOPE ELSE 0 END, 0));
                END IF;

                MI_L := MI_L + 1;
                IF MI_RCDESCUENTOSNCUENTA > 0 THEN
                      MI_MATRIZ.EXTEND();
                      MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K, MI_L, NVL(CASE WHEN MI_RCDEVENGOSNCUENTA > 0 THEN MI_RCDEVENGOSNTOTAL ELSE 0 END, 0) - NVL(CASE WHEN MI_DEDUCIBLE30TOPE > 0 THEN MI_DEDUCIBLE30TOPE ELSE MI_RCDESCUENTOSNTOTAL END, 0));
                ELSE
                      MI_MATRIZ.EXTEND();
                      MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K, MI_L, NVL(CASE WHEN MI_RCDEVENGOSNCUENTA > 0 THEN MI_RCDEVENGOSNTOTAL ELSE 0 END, 0) - NVL(CASE WHEN MI_DEDUCIBLE30TOPE > 0 THEN MI_DEDUCIBLE30TOPE ELSE 0 END, 0));
                END IF;

                MI_L := MI_L + 1;
                MI_MATRIZ.EXTEND();
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K, MI_L, NVL(PCK_SYSMAN_UTL.FC_ROUND(MI_TOTALMES * MI_PORCENTAJERENTAEXCENTA, 0), 0));

                MI_L := MI_L + 1;
                MI_MATRIZ.EXTEND();
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K, MI_L, NVL(MI_DEDUCIBLE25DSTO, 0));

                MI_DEDUCIBLEACUMULADO := MI_DEDUCIBLEACUMULADO + MI_DEDUCIBLE25DSTO;
                MI_FECHAVARIABLE := MI_FECHAVARIABLE2 + 1;
            END LOOP;
        END IF;

        MI_K := MI_K + 2;
        MI_MATRIZ.EXTEND();
        MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_K, MI_L, NVL(MI_DEDUCIBLEACUMULADO, 0));

        MI_K := MI_K + 2;
        MI_CONTADOR := MI_CONTADOR + 1;
    END LOOP;
    CLOSE RS;
    RETURN MI_MATRIZ;

END FC_CALCULARPROMEDIOSRETENCION;

FUNCTION FC_PLANOINCAPACIDADESSIIF(
/*
    NAME              : FC_PLANOINCAPACIDADESSIIF
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
    DATE MIGRADOR     : 19/10/2018
    TIME              : 02:22 PM
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    PARAMETERS        : UN_COMPANIA => Compañia actual de la entidad.
                        UN_PROCESO  => Proceso de nómina.
                        UN_ANO      => Año de nómina.
                        UN_MES      => Mes de nómina.
                        UN_PERIODO  => Periodo de nómina.
                        UN_ARCHIVO     => Hoja a generar.
                        UN_SALIDA   => Salida de plano, Excel o Archivo Plano

    MODIFICATIONS     :

    @NAME:generarPlanoIncapacidadSiif
    @METHOD:  GET
*/
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO		IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_ANO		    IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES		    IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO		IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_ARCHIVO      IN PCK_SUBTIPOS.TI_ENTERO DEFAULT 1,
    UN_SALIDA       IN VARCHAR2
)
    RETURN CLOB
AS
    MI_RS               SYS_REFCURSOR;
    MI_RSDETALLE        SYS_REFCURSOR;
    MI_MATRIZ           TYPEMATRIZ;
    MI_FILA             PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_COLUMNA          PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_CUENTA           PCK_SUBTIPOS.TI_ENTERO DEFAULT 1;
    MI_PARIDAUXILIARANE PCK_SUBTIPOS.TI_PARAMETRO;
    MI_TIPOAUXANT       PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_TIPOAUX          PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_AUXCNT           PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_RETORNO          CLOB;
    MI_REEMPLAZOS       PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_STRETAPA                        VARCHAR2(250 CHAR);
BEGIN

    MI_PARIDAUXILIARANE := NVL(PCK_SYSMAN_UTL.FC_PAR
                    (UN_COMPANIA    => UN_COMPANIA
                    ,UN_NOMBRE      => 'PCI DE CONEXION SIIF'
                    ,UN_MODULO      => PCK_DATOS.MODULONOMINA
                    ,UN_FECHA_PAR   => SYSDATE ), ' ');

    MI_MATRIZ := TYPEMATRIZ();
    <<ARCHIVO1>>
    FOR MI_RS IN
    (
        SELECT
            CASE HIS.ID_DE_CONCEPTO
            WHEN 378 THEN 1
            WHEN 379 THEN 3
            ELSE 2
            END TIPO

            ,SUM(
            CASE WHEN HIS.ID_DE_CONCEPTO IN (378)
            THEN HIS.VALOR
            ELSE 0
            END) VALOR1DEBITO

            ,SUM(
            CASE WHEN HIS.ID_DE_CONCEPTO IN (370,371,372,373,374,375,376,377)
            THEN HIS.VALOR
            ELSE 0
            END) VALOR2DEBITO

            ,SUM(
            CASE WHEN HIS.ID_DE_CONCEPTO IN (379)
            THEN HIS.VALOR
            ELSE 0
            END) VALOR3CREDITO

            ,CASE WHEN HIS.ID_DE_CONCEPTO <> 379 THEN CN.CTA_DBT_ADMINISTRACION ELSE CN.CTA_CRD_ADMINISTRACION END CUENTAHOJA1
        FROM HISTORICOS HIS
        INNER JOIN CONCEPTOS CN
           ON HIS.COMPANIA = CN.COMPANIA
          AND HIS.ID_DE_CONCEPTO = CN.ID_DE_CONCEPTO
        INNER JOIN PERSONAL_HISTORICO PH
           ON HIS.COMPANIA        = PH.COMPANIA
          AND HIS.ID_DE_PROCESO = PH.ID_DE_PROCESO
          AND HIS.ANO = PH.ANO
          AND HIS.MES	= PH.MES
          AND HIS.PERIODO = PH.PERIODO
          AND HIS.ID_DE_EMPLEADO = PH.ID_DE_EMPLEADO
        WHERE HIS.COMPANIA = UN_COMPANIA
          AND HIS.ID_DE_PROCESO = UN_PROCESO
          AND HIS.ANO = UN_ANO
          AND HIS.MES = UN_MES
          AND HIS.PERIODO = UN_PERIODO
          AND HIS.ID_DE_CONCEPTO BETWEEN 370 AND 379
        GROUP BY CASE HIS.ID_DE_CONCEPTO WHEN 378 THEN 1 WHEN 379 THEN 3 ELSE 2 END, CASE WHEN HIS.ID_DE_CONCEPTO <> 379 THEN CN.CTA_DBT_ADMINISTRACION ELSE CN.CTA_CRD_ADMINISTRACION END
        ORDER BY CASE HIS.ID_DE_CONCEPTO WHEN 378 THEN 1 WHEN 379 THEN 3 ELSE 2 END
    )
    LOOP
        IF UN_ARCHIVO = 1 THEN
            --HOJA1
            MI_FILA := MI_CUENTA;

            MI_STRETAPA := 'H1-Consecutivo A';

            --Consecutivo
            MI_COLUMNA := 0;
            MI_MATRIZ.EXTEND();
            MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, 1);

            MI_STRETAPA := 'H1-Consecutivo B';
            --Consecutivo
            MI_COLUMNA := 1;
            MI_MATRIZ.EXTEND();
            MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, MI_RS.TIPO);

            MI_STRETAPA := 'H1-Código Contable';
            --Código Contable
            MI_COLUMNA := 2;
            MI_MATRIZ.EXTEND();
            MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, NVL(MI_RS.CUENTAHOJA1,' '));

            MI_STRETAPA := 'H1-Valor Debito';
            --Valor Debito
            MI_COLUMNA := 3;
            MI_MATRIZ.EXTEND();
            MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, CASE MI_RS.TIPO WHEN 1 THEN MI_RS.VALOR1DEBITO WHEN 2 THEN MI_RS.VALOR2DEBITO ELSE 0 END);

            MI_STRETAPA := 'H1-Valor Credito';
            --Valor Credito
            MI_COLUMNA := 4;
            MI_MATRIZ.EXTEND();
            MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, CASE WHEN MI_RS.TIPO = 3 THEN MI_RS.VALOR3CREDITO ELSE 0 END);

            MI_CUENTA := MI_CUENTA + 1;
            --Fin Pimera hoja
        END IF;

        IF UN_ARCHIVO = 2 THEN
            <<ARCHIVO2>>
            MI_FILA := CASE WHEN MI_FILA <> 0 THEN MI_FILA ELSE 0 END;
            FOR MI_RSDETALLE IN
            (
                SELECT CASE TO_NUMBER(MI_RS.TIPO) WHEN 1 THEN CN.CTA_CRE_PPTAL WHEN 2 THEN 1||'-'|| FONDO.NIT ELSE TO_NUMBER(TD.DOC_SIIF)||'-'|| PH.NUMERO_DCTO END ID_AUXILIAR
                  ,SUM(HIS.VALOR) VALORAUXILIAR
                FROM HISTORICOS HIS
                    INNER JOIN CONCEPTOS CN
                       ON HIS.COMPANIA = CN.COMPANIA
                      AND HIS.ID_DE_CONCEPTO = CN.ID_DE_CONCEPTO
                    INNER JOIN PERSONAL_HISTORICO PH
                       ON HIS.COMPANIA        = PH.COMPANIA
                      AND HIS.ID_DE_PROCESO = PH.ID_DE_PROCESO
                      AND HIS.ANO = PH.ANO
                      AND HIS.MES	= PH.MES
                      AND HIS.PERIODO = PH.PERIODO
                      AND HIS.ID_DE_EMPLEADO = PH.ID_DE_EMPLEADO
                    INNER JOIN V_FONDO_DE_SALUD FONDO
                       ON PH.COMPANIA           = FONDO.COMPANIA
                      AND PH.FONDO_SALUD        = FONDO.FONDO_SALUD
                    INNER JOIN TIPOS_DOCUMENTOS TD
                       ON PH.COMPANIA = TD.COMPANIA
                      AND PH.DCTO_IDENTIDAD = TD.DCTO_IDENTIDAD
                WHERE HIS.COMPANIA = UN_COMPANIA
                  AND HIS.ID_DE_PROCESO = UN_PROCESO
                  AND HIS.ANO = UN_ANO
                  AND HIS.MES = UN_MES
                  AND HIS.PERIODO = UN_PERIODO
                  AND (  (1 = CASE WHEN MI_RS.TIPO = 1 THEN 1 ELSE 0 END
                         AND HIS.ID_DE_CONCEPTO IN (378)  )
                      OR (1 = CASE WHEN MI_RS.TIPO = 2 THEN 1 ELSE 0 END
                        AND HIS.ID_DE_CONCEPTO IN (370,371,372,373,374,375,376,377) )
                      OR (1 = CASE WHEN MI_RS.TIPO = 3 THEN 1 ELSE 0 END
                        AND HIS.ID_DE_CONCEPTO IN (379) )
                      )
                GROUP BY CASE TO_NUMBER(MI_RS.TIPO) WHEN 1 THEN CN.CTA_CRE_PPTAL WHEN 2 THEN 1||'-'|| FONDO.NIT ELSE TO_NUMBER(TD.DOC_SIIF)||'-'|| PH.NUMERO_DCTO END
            )
            LOOP
                MI_STRETAPA := 'H2';
                MI_FILA := MI_FILA + 1;

                IF MI_TIPOAUXANT <> MI_RS.TIPO THEN --El primer registro con el valor de parámetro de la ane
                    --Consecutivo
                    MI_COLUMNA := 0;
                    MI_MATRIZ.EXTEND();
                    MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, MI_RS.TIPO);

                    MI_STRETAPA := 'H2.1-Tipo de axiliar';
                    --Tipo de axiliar
                    MI_COLUMNA := 1;
                    MI_TIPOAUX := 1;
                    MI_MATRIZ.EXTEND();
                    MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, MI_TIPOAUX);

                    MI_STRETAPA := 'H2.1-Auxliar contable';
                    --Auxliar contable
                    MI_COLUMNA := 2;
                    MI_AUXCNT := 1;
                    MI_MATRIZ.EXTEND();
                    MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, MI_AUXCNT);

                    MI_STRETAPA := 'H2.1-Identificación Auxiliar';
                    --Identificación Auxiliar
                    MI_COLUMNA := 3;
                    MI_MATRIZ.EXTEND();
                    MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, MI_PARIDAUXILIARANE);

                    MI_STRETAPA := 'H2.1-Valor Auxiliar';
                    --Valor Auxiliar
                    MI_COLUMNA := 4;
                    MI_MATRIZ.EXTEND();
                    MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, (MI_RS.VALOR1DEBITO + MI_RS.VALOR2DEBITO + MI_RS.VALOR3CREDITO) );

                    MI_FILA := MI_FILA + 1;
                END IF;

                MI_TIPOAUXANT := MI_RS.TIPO;

                MI_STRETAPA := 'H2.2-Consecutivo';
                --Consecutivo
                MI_COLUMNA := 0;
                MI_MATRIZ.EXTEND();
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, MI_RS.TIPO);

                MI_STRETAPA := 'H2.2-Tipo de axiliar';
                --Tipo de axiliar
                MI_COLUMNA := 1;
                MI_TIPOAUX := CASE WHEN MI_RS.TIPO = 1 THEN 2 ELSE 1 END;
                MI_MATRIZ.EXTEND();
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, MI_TIPOAUX);

                MI_STRETAPA := 'H2.2-Auxliar contable';
                --Auxliar contable
                MI_COLUMNA := 2;
                MI_AUXCNT := CASE WHEN MI_RS.TIPO = 1 THEN 7 ELSE 3 END;
                MI_MATRIZ.EXTEND();
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, MI_AUXCNT);

                MI_STRETAPA := 'H2.2-Identificación Auxiliar';
                --Identificación Auxiliar
                MI_COLUMNA := 3;
                MI_MATRIZ.EXTEND();
                --(MZANGUNA:31/10/2018)-Se quitan espacios en cuentas.
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, CASE WHEN MI_RSDETALLE.ID_AUXILIAR IS NULL THEN ' ' ELSE REPLACE(MI_RSDETALLE.ID_AUXILIAR, ' ','') END);

                MI_STRETAPA := 'H2.2-Valor Auxiliar';
                --Valor Auxiliar
                MI_COLUMNA := 4;
                MI_MATRIZ.EXTEND();
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, MI_RSDETALLE.VALORAUXILIAR);

            END LOOP ARCHIVO2;

        END IF; --Hoja2
    END LOOP ARCHIVO1;


    FOR MI_RS IN
    (
        SELECT DISTINCT
               INCAPACIDADSIIF.FILA,
               INCAPACIDADSIIF.COLUMNA,
               INCAPACIDADSIIF.VALOR VALOR
        FROM TABLE(MI_MATRIZ) INCAPACIDADSIIF
        ORDER BY INCAPACIDADSIIF.FILA, INCAPACIDADSIIF.COLUMNA
    )
    LOOP
        MI_STRETAPA := 'H-Armando archivo';
        IF UN_SALIDA = 'EXCEL' THEN
            MI_RETORNO := MI_RETORNO  ||
                         TO_CLOB(MI_RS.FILA     ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                 MI_RS.COLUMNA  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                 MI_RS.VALOR    ||  PCK_DATOS.GL_SEPARADOR_REG);
        ELSIF UN_SALIDA = 'PLANO' THEN
            IF MI_FILA <> MI_RS.FILA AND MI_RS.FILA NOT IN (1) THEN --Enter en el plano
                MI_RETORNO := TO_CLOB(MI_RETORNO || CHR(13) || CHR(10));
            ELSIF MI_RETORNO IS NOT NULL THEN
                MI_RETORNO := MI_RETORNO || '|';
            END IF;
            MI_RETORNO := MI_RETORNO || TO_CLOB(MI_RS.VALOR);

            MI_FILA := MI_RS.FILA;
        END IF;
    END LOOP;

    RETURN MI_RETORNO;
EXCEPTION WHEN OTHERS THEN
    --Ocurrió un problema con error --CODERROR--, En el registro --REGISTRO--, Etapa --ETAPA--.
    MI_REEMPLAZOS(1).CLAVE := 'CODERROR';
    MI_REEMPLAZOS(1).VALOR := SQLCODE;
    MI_REEMPLAZOS(2).CLAVE := 'REGISTRO';
    MI_REEMPLAZOS(2).VALOR := MI_FILA;
    MI_REEMPLAZOS(3).CLAVE := 'ETAPA';
    MI_REEMPLAZOS(3).VALOR := MI_STRETAPA;

    PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => -20000,
                                UN_ERROR_COD  => PCK_ERRORES.ERR_PLANONOMINASIIF,
                                UN_REEMPLAZOS => MI_REEMPLAZOS);

END FC_PLANOINCAPACIDADESSIIF;

FUNCTION FC_PLANORETEFUENTESIIF(
/*
    NAME              : FC_PLANORETEFUENTESIIF
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
    DATE MIGRADOR     : 22/10/2018
    TIME              : 05:00 PM
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    PARAMETERS        : UN_COMPANIA => Compañia actual de la entidad.
                        UN_PROCESO  => Proceso de nómina.
                        UN_ANO      => Año de nómina.
                        UN_MES      => Mes de nómina.
                        UN_PERIODO  => Periodo de nómina.
                        UN_ARCHIVO     => Hoja a generar.
                        UN_SALIDA   => Salida de plano, Excel o Archivo Plano

    MODIFICATIONS     :

    @NAME:generarPlanoRetefuenteSiif
    @METHOD:  GET
*/
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO		IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_ANO		    IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES		    IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO		IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_ARCHIVO      IN PCK_SUBTIPOS.TI_ENTERO DEFAULT 1,
    UN_SALIDA       IN VARCHAR2
)
    RETURN CLOB
AS
    MI_RETORNO          CLOB;
    MI_REEMPLAZOS       PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_RS               SYS_REFCURSOR;
    MI_RSDETALLE        SYS_REFCURSOR;
    MI_PARIDAUXILIARANE PCK_SUBTIPOS.TI_PARAMETRO;
    MI_PARRUBRORENTAS   PCK_SUBTIPOS.TI_PARAMETRO;
    MI_VALOR125         PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CNTCRDADMIN      TIPO_RETENCIONES.CUENTACONTABLE%TYPE;
    MI_CNTSIIF          TIPO_RETENCIONES.CUENTACONTABLE%TYPE;
    MI_TERCEROCN        CONCEPTOS.TERCERO%TYPE;
    MI_MATRIZ           TYPEMATRIZ;
    MI_FILA             PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_COLUMNA          PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_STRETAPA         VARCHAR2(250 CHAR);
BEGIN
    MI_STRETAPA := '1';
    MI_PARRUBRORENTAS := NVL(PCK_SYSMAN_UTL.FC_PAR
                    (UN_COMPANIA    => UN_COMPANIA
                    ,UN_NOMBRE      => 'RUBRO SIIF RENTAS DE TRABAJO PAGADAS'
                    ,UN_MODULO      => PCK_DATOS.MODULONOMINA
                    ,UN_FECHA_PAR   => SYSDATE ), ' ');

    MI_PARIDAUXILIARANE := NVL(PCK_SYSMAN_UTL.FC_PAR
                    (UN_COMPANIA    => UN_COMPANIA
                    ,UN_NOMBRE      => 'PCI DE CONEXION SIIF'
                    ,UN_MODULO      => PCK_DATOS.MODULONOMINA
                    ,UN_FECHA_PAR   => SYSDATE ), ' ');

    MI_MATRIZ := TYPEMATRIZ();

    MI_STRETAPA := '2';
    BEGIN
        SELECT SUM(HIS.VALOR) VALOR, MAX(TR.CUENTACONTABLE) CUENTACONTABLE ,MAX(CUENTACONTABLESIIF) CUENTASIIF, CN.TERCERO
        INTO   MI_VALOR125, MI_CNTCRDADMIN, MI_CNTSIIF, MI_TERCEROCN
        FROM HISTORICOS HIS
        INNER JOIN PERSONAL_HISTORICO PH
           ON HIS.COMPANIA        = PH.COMPANIA
          AND HIS.ID_DE_PROCESO = PH.ID_DE_PROCESO
          AND HIS.ANO = PH.ANO
          AND HIS.MES	= PH.MES
          AND HIS.PERIODO = PH.PERIODO
          AND HIS.ID_DE_EMPLEADO = PH.ID_DE_EMPLEADO
        INNER JOIN CONCEPTOS CN
          ON HIS.COMPANIA = CN.COMPANIA
         AND HIS.ID_DE_CONCEPTO = CN.ID_DE_CONCEPTO
        INNER JOIN TIPO_RETENCIONES TR
           ON PH.COMPANIA  = TR.COMPANIA
          AND PH.TIPORET   = TR.TIPORET
        WHERE HIS.COMPANIA = UN_COMPANIA
          AND HIS.ID_DE_PROCESO = UN_PROCESO
          AND HIS.ANO = UN_ANO
          AND HIS.MES = UN_MES
          AND HIS.PERIODO = UN_PERIODO
          AND HIS.ID_DE_CONCEPTO = 125
        GROUP BY CN.TERCERO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_VALOR125 := 0;
        MI_CNTCRDADMIN := '';
        MI_CNTSIIF := '';
        MI_TERCEROCN := '';
    END;

    IF MI_VALOR125 <> 0 THEN
        IF UN_ARCHIVO = 1 THEN
            FOR I IN 1..2 LOOP
                MI_STRETAPA := '3';
                MI_FILA := i;
                --Consecutivo Columna A
                MI_MATRIZ.EXTEND();
                MI_COLUMNA := 0;
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, 1);

                --Consecutivo Columna B
                MI_MATRIZ.EXTEND();
                MI_COLUMNA := 1;
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, i);

                --Código Contable
                MI_MATRIZ.EXTEND();
                MI_COLUMNA := 2;
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, CASE i WHEN 1 THEN NVL(MI_CNTCRDADMIN, ' ') ELSE NVL(MI_CNTSIIF, ' ') END);

                --Valor Debito
                MI_MATRIZ.EXTEND();
                MI_COLUMNA := 3;
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, CASE WHEN i = 1 THEN MI_VALOR125 ELSE 0 END);

                --Valor Credito
                MI_MATRIZ.EXTEND();
                MI_COLUMNA := 4;
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, CASE WHEN i = 2 THEN MI_VALOR125 ELSE 0 END);

            END LOOP;
        ELSIF UN_ARCHIVO = 2 THEN
            <<DETALLE1>>
            FOR i IN 1..2 LOOP
                MI_STRETAPA := '4';
                MI_FILA := i;
                --Consecutivo (Archivo 2)
                MI_MATRIZ.EXTEND();
                MI_COLUMNA := 0;
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, 1);

                --Tipo de Auxiliar
                MI_MATRIZ.EXTEND();
                MI_COLUMNA := 1;
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, 1);

                --Auxiliar Contable
                MI_MATRIZ.EXTEND();
                MI_COLUMNA := 2;
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, i);

                --Identificacion Auxiliar
                MI_MATRIZ.EXTEND();
                MI_COLUMNA := 3;
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, CASE WHEN i = 1 THEN MI_PARIDAUXILIARANE ELSE MI_PARRUBRORENTAS END);

                --Valor Auxiliar
                MI_MATRIZ.EXTEND();
                MI_COLUMNA := 4;
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, MI_VALOR125);

            END LOOP DETALLE1;

            <<ARCHIVO2>>
            FOR MI_RSDETALLE IN
            (
                SELECT TO_NUMBER(TD.DOC_SIIF)||'-'|| PH.NUMERO_DCTO NUMERO_DCTO, HIS.VALOR
                FROM HISTORICOS HIS
                INNER JOIN PERSONAL_HISTORICO PH
                   ON HIS.COMPANIA        = PH.COMPANIA
                  AND HIS.ID_DE_PROCESO = PH.ID_DE_PROCESO
                  AND HIS.ANO = PH.ANO
                  AND HIS.MES	= PH.MES
                  AND HIS.PERIODO = PH.PERIODO
                  AND HIS.ID_DE_EMPLEADO = PH.ID_DE_EMPLEADO
                INNER JOIN TIPOS_DOCUMENTOS TD
                   ON PH.COMPANIA = TD.COMPANIA
                  AND PH.DCTO_IDENTIDAD = TD.DCTO_IDENTIDAD
                WHERE HIS.COMPANIA = UN_COMPANIA
                  AND HIS.ID_DE_PROCESO = UN_PROCESO
                  AND HIS.ANO = UN_ANO
                  AND HIS.MES = UN_MES
                  AND HIS.PERIODO = UN_PERIODO
                  AND HIS.ID_DE_CONCEPTO = 125
            )
            LOOP
                MI_STRETAPA := '5';
                MI_FILA := MI_FILA + 1;

                --Consecutivo (Archivo 2)
                MI_MATRIZ.EXTEND();
                MI_COLUMNA := 0;
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, 1);

                --Tipo de Auxiliar
                MI_MATRIZ.EXTEND();
                MI_COLUMNA := 1;
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, 1);

                --Auxiliar Contable
                MI_MATRIZ.EXTEND();
                MI_COLUMNA := 2;
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, 3);

                --Identificacion Auxiliar
                MI_MATRIZ.EXTEND();
                MI_COLUMNA := 3;
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, NVL(MI_RSDETALLE.NUMERO_DCTO, ' '));

                --Valor Auxiliar
                MI_MATRIZ.EXTEND();
                MI_COLUMNA := 4;
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, MI_RSDETALLE.VALOR);

            END LOOP ARCHIVO2;

            <<DETALLE2>>
            FOR i IN 1..3 LOOP
                MI_STRETAPA := '6';
                MI_FILA := MI_FILA + 1;
                --Consecutivo (Archivo 2)
                MI_MATRIZ.EXTEND();
                MI_COLUMNA := 0;
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, 2);

                --Tipo de Auxiliar
                MI_MATRIZ.EXTEND();
                MI_COLUMNA := 1;
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, 1);

                --Auxiliar Contable
                MI_MATRIZ.EXTEND();
                MI_COLUMNA := 2;
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, i);

                --Identificacion Auxiliar
                MI_MATRIZ.EXTEND();
                MI_COLUMNA := 3;
                --(MZANGUNA:31/10/2018)-Se adiciona tipo 1 en la cuenta del tercero.
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, CASE i WHEN 1 THEN NVL(MI_PARIDAUXILIARANE, ' ') WHEN 2 THEN NVL(MI_PARRUBRORENTAS, ' ') ELSE NVL('1-' || MI_TERCEROCN, ' ') END);

                --Valor Auxiliar
                MI_MATRIZ.EXTEND();
                MI_COLUMNA := 4;
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, MI_VALOR125);
            END LOOP DETALLE2;

        END IF;

        FOR MI_RS IN
        (
            SELECT DISTINCT
                   RETENCION.FILA,
                   RETENCION.COLUMNA,
                   RETENCION.VALOR VALOR
            FROM TABLE(MI_MATRIZ) RETENCION
            ORDER BY RETENCION.FILA, RETENCION.COLUMNA
        )
        LOOP
            MI_STRETAPA := '7';
            MI_STRETAPA := 'H-Armando archivo';
            IF UN_SALIDA = 'EXCEL' THEN
                MI_RETORNO := MI_RETORNO  ||
                             TO_CLOB(MI_RS.FILA     ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                     MI_RS.COLUMNA  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                     MI_RS.VALOR    ||  PCK_DATOS.GL_SEPARADOR_REG);
            ELSIF UN_SALIDA = 'PLANO' THEN
                IF MI_FILA <> MI_RS.FILA AND MI_RS.FILA NOT IN (1) THEN --Enter en el plano
                    MI_RETORNO := TO_CLOB(MI_RETORNO || CHR(13) || CHR(10));
                ELSIF MI_RETORNO IS NOT NULL THEN
                    MI_RETORNO := MI_RETORNO || '|';
                END IF;
                MI_RETORNO := MI_RETORNO || TO_CLOB(MI_RS.VALOR);

                MI_FILA := MI_RS.FILA;
            END IF;
        END LOOP;

    END IF; --No existen datos;
    RETURN MI_RETORNO;
EXCEPTION WHEN OTHERS THEN
    --Ocurrió un problema con error --CODERROR--, En el registro --REGISTRO--, Etapa --ETAPA--.
    MI_REEMPLAZOS(1).CLAVE := 'CODERROR';
    MI_REEMPLAZOS(1).VALOR := SQLCODE;
    MI_REEMPLAZOS(2).CLAVE := 'REGISTRO';
    MI_REEMPLAZOS(2).VALOR := MI_FILA;
    MI_REEMPLAZOS(3).CLAVE := 'ETAPA';
    MI_REEMPLAZOS(3).VALOR := MI_STRETAPA;

    PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => -20000,
                                UN_ERROR_COD  => PCK_ERRORES.ERR_PLANONOMINASIIF,
                                UN_REEMPLAZOS => MI_REEMPLAZOS);
END FC_PLANORETEFUENTESIIF;

FUNCTION FC_PLANOBENEFICIOSSIIF(
/*
    NAME              : FC_PLANOBENEFICIOSSIIF
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
    DATE MIGRADOR     : 23/10/2018
    TIME              : 04:43 PM
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    PARAMETERS        : UN_COMPANIA => Compañia actual de la entidad.
                        UN_PROCESO  => Proceso de nómina.
                        UN_ANO      => Año de nómina.
                        UN_MES      => Mes de nómina.
                        UN_PERIODO  => Periodo de nómina.
                        UN_ARCHIVO     => Hoja a generar.
                        UN_SALIDA   => Salida de plano, Excel o Archivo Plano

    MODIFICATIONS     :

    @NAME:generarPlanoBeneficiosSiif
    @METHOD:  GET
*/
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO		IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_ANO		    IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES		    IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO		IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_ARCHIVO      IN PCK_SUBTIPOS.TI_ENTERO DEFAULT 1,
    UN_SALIDA       IN VARCHAR2
)
    RETURN CLOB
AS
    MI_RETORNO          CLOB;
    MI_MATRIZ           TYPEMATRIZ;
    MI_RS               SYS_REFCURSOR;
    MI_FILA             PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_COLUMNA          PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_PARIDAUXILIARANE PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CONSECUTIVO      PCK_SUBTIPOS.TI_ENTERO DEFAULT 1;
    MI_STRETAPA         VARCHAR2(250 CHAR);
    MI_REEMPLAZOS       PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
    MI_MATRIZ := TYPEMATRIZ();

    MI_FILA := 1;
    MI_CONSECUTIVO := 1;
    MI_STRETAPA := '1';
    MI_PARIDAUXILIARANE := NVL(PCK_SYSMAN_UTL.FC_PAR
                    (UN_COMPANIA    => UN_COMPANIA
                    ,UN_NOMBRE      => 'PCI DE CONEXION SIIF'
                    ,UN_MODULO      => PCK_DATOS.MODULONOMINA
                    ,UN_FECHA_PAR   => SYSDATE ), ' ');

    <<ARCHIVO1>>
    FOR MI_RS IN
    (
        WITH CNBENEFICIOS AS
        (
            SELECT SUM(HIS.VALOR) VALOR, HIS.ID_DE_CONCEPTO CONCEPTO, CN.CTA_DBT_ADMINISTRACION CNTDEBITO, CN.CTA_CRD_ADMINISTRACION CNTCREDITO,
                   CN.CTA_CRE_PPTAL
            FROM HISTORICOS HIS
            INNER JOIN CONCEPTOS CN
              ON HIS.COMPANIA = CN.COMPANIA
             AND HIS.ID_DE_CONCEPTO = CN.ID_DE_CONCEPTO
            WHERE HIS.COMPANIA = UN_COMPANIA
              AND HIS.ID_DE_PROCESO = UN_PROCESO
              AND HIS.ANO = UN_ANO
              AND HIS.MES = UN_MES
              AND HIS.PERIODO = UN_PERIODO
              AND HIS.ID_DE_CONCEPTO IN (497,494,492,498,493,490,491)
            GROUP BY HIS.ID_DE_CONCEPTO, CN.CTA_DBT_ADMINISTRACION, CN.CTA_CRD_ADMINISTRACION, CN.CTA_CRE_PPTAL
            ORDER BY HIS.ID_DE_CONCEPTO
        )
        SELECT 1 TIPO, VALOR VALORDEBITO, 0 VALORCREDITO, CONCEPTO, CNTDEBITO CUENTA, CTA_CRE_PPTAL IDAUX
        FROM CNBENEFICIOS
        UNION ALL
        SELECT 2 TIPO,     0 VALORDEBITO, VALOR VALORCREDITO, CONCEPTO, CNTCREDITO CUENTA , ' ' IDAUX
        FROM CNBENEFICIOS
    )
    LOOP
        MI_STRETAPA := '2';
        IF UN_ARCHIVO = 1 THEN
            --Consecutivo (Columna A-Hoja Encabezado)
            MI_STRETAPA := '3';
            MI_MATRIZ.EXTEND();
            MI_COLUMNA := 0;
            MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, 1);

            --Consecutivo (Columna B-Hoja Archivo 1)
            MI_MATRIZ.EXTEND();
            MI_COLUMNA := 1;
            MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, MI_FILA);

            MI_STRETAPA := '4';
            --Código Contable
            MI_MATRIZ.EXTEND();
            MI_COLUMNA := 2;
            MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, NVL(MI_RS.CUENTA, ' '));

            --Valor Debe
            MI_MATRIZ.EXTEND();
            MI_COLUMNA := 3;
            MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, MI_RS.VALORDEBITO);

            --Valor Haber
            MI_STRETAPA := '5';
            MI_MATRIZ.EXTEND();
            MI_COLUMNA := 4;
            MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, MI_RS.VALORCREDITO);

            MI_FILA :=  MI_FILA + 1;
        ELSIF UN_ARCHIVO = 2 THEN
            IF MI_RS.TIPO = 1 THEN
                <<DETALLE1>>
                FOR i IN 1..2 LOOP
                    --Consecutivo (Columna A-Hoja Encabezado)
                    MI_STRETAPA := '6';
                    MI_MATRIZ.EXTEND();
                    MI_COLUMNA := 0;
                    MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, MI_CONSECUTIVO);

                    --Tipo de Auxiliar
                    MI_MATRIZ.EXTEND();
                    MI_COLUMNA := 1;
                    MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, i);

                    MI_STRETAPA := '7';
                    --Auxiliar Contable
                    MI_MATRIZ.EXTEND();
                    MI_COLUMNA := 2;
                    MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, CASE WHEN i = 1 THEN 1 ELSE 7 END);

                    --Identificacion Auxiliar
                    MI_MATRIZ.EXTEND();
                    MI_COLUMNA := 3;
                    --(MZANGUNA:31/10/2018)-Se quitan espacios en cuentas.
                    MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, CASE WHEN i = 1 THEN NVL(MI_PARIDAUXILIARANE, ' ') ELSE CASE WHEN MI_RS.IDAUX IS NULL THEN ' ' ELSE REPLACE(MI_RS.IDAUX, ' ', '') END END);

                    MI_STRETAPA := '8';
                    --Valor Auxiliar
                    MI_MATRIZ.EXTEND();
                    MI_COLUMNA := 4;
                    MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, MI_RS.VALORDEBITO);

                    MI_FILA := MI_FILA + 1;
                END LOOP DETALLE1;
            ELSE
                MI_STRETAPA := '9';
                --Consecutivo (Columna A-Hoja Encabezado)
                MI_MATRIZ.EXTEND();
                MI_COLUMNA := 0;
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, MI_CONSECUTIVO);

                --Tipo de Auxiliar
                MI_MATRIZ.EXTEND();
                MI_COLUMNA := 1;
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, 1);

                --Auxiliar Contable
                MI_MATRIZ.EXTEND();
                MI_COLUMNA := 2;
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, 1);

                MI_STRETAPA := '10';
                --Identificacion Auxiliar
                MI_MATRIZ.EXTEND();
                MI_COLUMNA := 3;
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, NVL(MI_PARIDAUXILIARANE, ' '));

                --Valor Auxiliar
                MI_MATRIZ.EXTEND();
                MI_COLUMNA := 4;
                MI_MATRIZ(MI_MATRIZ.COUNT) := VC_RECORD(MI_FILA, MI_COLUMNA, MI_RS.VALORCREDITO);
                MI_FILA := MI_FILA + 1;
            END IF;
            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;

        END IF;

    END LOOP ARCHIVO1;

    FOR MI_RS IN
    (
        SELECT DISTINCT
               BENEFICIOS.FILA,
               BENEFICIOS.COLUMNA,
               BENEFICIOS.VALOR VALOR
        FROM TABLE(MI_MATRIZ) BENEFICIOS
        ORDER BY BENEFICIOS.FILA, BENEFICIOS.COLUMNA
    )
    LOOP
        MI_STRETAPA := '11';
        MI_STRETAPA := 'H-Armando archivo';
        IF UN_SALIDA = 'EXCEL' THEN
            MI_RETORNO := MI_RETORNO  ||
                         TO_CLOB(MI_RS.FILA     ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                 MI_RS.COLUMNA  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                 MI_RS.VALOR    ||  PCK_DATOS.GL_SEPARADOR_REG);
        ELSIF UN_SALIDA = 'PLANO' THEN
            IF MI_FILA <> MI_RS.FILA AND MI_RS.FILA NOT IN (1) THEN --Entre en el plano
                MI_RETORNO := TO_CLOB(MI_RETORNO || CHR(13) || CHR(10));
            ELSIF MI_RETORNO IS NOT NULL THEN
                MI_RETORNO := MI_RETORNO || '|';
            END IF;
            MI_RETORNO := MI_RETORNO || TO_CLOB(MI_RS.VALOR);

            MI_FILA := MI_RS.FILA;
        END IF;
    END LOOP;
    RETURN MI_RETORNO;

EXCEPTION WHEN OTHERS THEN
    --Ocurrió un problema con error --CODERROR--, En el registro --REGISTRO--, Etapa --ETAPA--.
    MI_REEMPLAZOS(1).CLAVE := 'CODERROR';
    MI_REEMPLAZOS(1).VALOR := SQLCODE;
    MI_REEMPLAZOS(2).CLAVE := 'REGISTRO';
    MI_REEMPLAZOS(2).VALOR := MI_FILA;
    MI_REEMPLAZOS(3).CLAVE := 'ETAPA';
    MI_REEMPLAZOS(3).VALOR := MI_STRETAPA;

    PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => -20000,
                                UN_ERROR_COD  => PCK_ERRORES.ERR_PLANONOMINASIIF,
                                UN_REEMPLAZOS => MI_REEMPLAZOS);
END FC_PLANOBENEFICIOSSIIF;

PROCEDURE PR_LIQPRIPRISEMSENA
/*
  NAME              : PPR_LIQPRIPRISEM
  AUTHORS           : SYSMAN  SAS
  AUTHOR            : EDWIN FERNANDO CABRERA MARTINEZ
  DATE              : 10/05/2023
  TIME              : 10:00 AM
  SOURCE MODULE     : -
  MODIFIER          : -
  DATE MODIFIED     : -
  TIME              : -
  DESCRIPTION       :PROCEDIMIENTO LIQUIDACION DE VACACIONES EN RETIRO PARA NOMINAS PRIVADAS
                        TICKET: 7720876
  --NAME:  PR_LIQPRIPRISEM
*/
AS
    MI_DOCEAVASMINIMASPS         PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_PSPAGADA                     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PRIMAJUNIO                   PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_INDRETIR                     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_MSG                          PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_VALOR                        PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_VALOR_F                      VARCHAR2(100 CHAR);
BEGIN
    MI_DOCEAVASMINIMASPS := TO_NUMBER(PCK_PARST.FC_PAR('DOCEAVAS MINIMAS PRIMA SERVICIOS', '1'));
    PCK_NOMINA.GL_FACTORPS := 0;
    PCK_NOMINA.GL_FACTORPS1 := 0;
    PCK_NOMINA.GL_DNT := 0;
  
      --(APINEDA:22/03/2019)-Se agregan validaciones faltantes para la fecha de inicio de la prima semestral debido a que no esta calculando el concepto 160 para liquidaciones finales. TAR 1000090732
    IF PCK_NOMINA.GL_SMES <= 7 THEN
        --(CFBARRERA_CC:3135) Se hace ajuste para tomar la fecha inicio del semestre sea del 01 de enero
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('01/01/' || (PCK_NOMINA.GL_SANO), 'DD/MM/YYYY') ELSE TO_DATE('01/01/' || (PCK_NOMINA.GL_SANO), 'DD/MM/YYYY') END;
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
        --(APINEDA:01/12/2018)-TAR1000087154 Se toma la fecha de ingreso como fecha de inicio cuando un funcionario se retira y en julio del año anterior no habia cumplido 180 dias trabajados
        IF PCK_NOMINA.FC_CN(404) <> 0 AND PCK_NOMINA.GL_PERIODOACTUAL = 7 THEN
            IF (PCK_NOMINA.GL_FECHAIPS > PCK_NOMINA.GL_FECHAI) AND (PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAIPS) < 180) THEN
                PCK_NOMINA.GL_FECHAIPS := PCK_NOMINA.GL_FECHAI;
            END IF;
        END IF;
    END IF;
    IF PCK_NOMINA.GL_FECHAINI < TO_DATE('01/07/2025', 'DD/MM/YYYY') AND PCK_NOMINA.GL_SANO = '2025' THEN--(CFBARRERA_CC:3135) Se agrega validacion solo para el año 2025
       PCK_NOMINA.GL_FECHAINI := TO_DATE('01/07/2025', 'DD/MM/YYYY');
    END IF;
    IF PCK_NOMINA.GL_FECHAIPS < TO_DATE('01/07/2025', 'DD/MM/YYYY') AND PCK_NOMINA.GL_SANO = '2025' THEN--(CFBARRERA_CC:3135) Se agrega validacion solo para el año 2025
        PCK_NOMINA.GL_FECHAIPS := TO_DATE('01/07/2025', 'DD/MM/YYYY');
    END IF;
      --(APINEDA:22/03/2019)-Se agregan validaciones faltantes para la fecha de inicio de la prima semestral debido a que no esta calculando el concepto 160 para liquidaciones finales. TAR 1000090732
    IF PCK_NOMINA.FC_CN(404) <> 0 AND PCK_NOMINA.GL_PERIODOACTUAL = 7 AND PCK_NOMINA.GL_SMES = 7 AND PCK_NOMINA.GL_SANO = '2025' THEN--(CFBARRERA_CC:3135) Se agrega validacion solo para el año 2025
      --LVEGA 12/08/2025
        PCK_NOMINA.GL_FECHAIPS :=  CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > TO_DATE('25/06/2025', 'DD/MM/YYYY') THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO ELSE  TO_DATE('25/06/2025', 'DD/MM/YYYY') END;
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
    END IF;
    IF PCK_NOMINA.GL_SMES > 7 THEN
     PCK_NOMINA.GL_FECHAIPS := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');--(CFBARRERA_CC:3135_INI) Se agrega validacion si el mes es mayor a julio tome el 01/07
      -- Si la fecha de ingreso es mayor a la fecha inicial del segundo semestre, usa la fecha de ingreso
       PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS
            THEN
                PCK_NOMINA.GL_FECHAI
            ELSE
                PCK_NOMINA.GL_FECHAIPS
        END;
       PCK_NOMINA.GL_FECHAIPS1 := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');--(CFBARRERA_CC:3135_FIN)     
        --LVEGA 12/08/2025
         IF (PCK_NOMINA.FC_CN(404) <> 0 AND PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) = 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '05') AND PCK_NOMINA.GL_SANO = '2025'  THEN--(CFBARRERA_CC:3135) Se agrega validacion solo para el año 2025
            PCK_NOMINA.GL_FECHAIPS :=  CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > TO_DATE('25/06/2025', 'DD/MM/YYYY') THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO ELSE  TO_DATE('25/06/2025', 'DD/MM/YYYY') END;
            PCK_NOMINA.GL_FECHAIPS1 := CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > TO_DATE('25/06/2025', 'DD/MM/YYYY') THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO ELSE  TO_DATE('25/06/2025', 'DD/MM/YYYY') END;
        END IF;
    END IF;
    --(APINEDA:26/07/2019)-Se modifica CASE para asignar valor a la fecha final de la prima semestral debido a que estaba presentando inconsistencias.
    IF PCK_NOMINA.GL_SMES <= 7 THEN--(CFBARRERA_CC:3135_INI:) Se agrega validacion menor o igual al mes de julio
    -- Condición específica para período 7 con  fecha retiro y fecha de terminación
        IF PCK_NOMINA.GL_SPER = 7 
           AND PCK_NOMINA.FC_CN(404) <> 0 
           AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL 
        THEN 
            PCK_NOMINA.GL_FECHAFPS := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO;
        
        -- Si no cumple la condición anterior, cuando es periodo 4 y el mes junio o julio
        ELSE
         -- ORGINIAL PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN CASE WHEN PCK_NOMINA.GL_FECHAFIN1 > TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.GL_FECHAFIN1 ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END; 
            PCK_NOMINA.GL_FECHAFPS := CASE
                WHEN PCK_NOMINA.FC_CN(404) <> 0 
                     OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL
                THEN
                    CASE
                        WHEN PCK_NOMINA.GL_FECHAFIN1 > TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY')
                        THEN PCK_NOMINA.GL_FECHAFIN1
                        ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY')
                    END
                ELSE 
                    TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY')
            END;
        END IF;--(CFBARRERA_CC:3135_FIN)
    ELSE
    --LVEGA 12/08/2025
       PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN NVL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO,PCK_NOMINA.GL_FECHAFIN1) ELSE TO_DATE('30/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END;--(CFBARRERA_CC:3135) Se agrega ajuste para segundo semetre else 30/12
        IF (PCK_NOMINA.FC_CN(402) <> 0 OR PCK_NOMINA.GL_SMES > 7)  AND PCK_NOMINA.GL_SANO = '2025' THEN --JM CC 3101 - (CFBARRERA_CC:3135) Se agrega validacion solo para el año 2025
            PCK_NOMINA.GL_FECHAIPS :=  CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > TO_DATE('25/06/2025', 'DD/MM/YYYY') THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO ELSE  TO_DATE('25/06/2025', 'DD/MM/YYYY') END;
            PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAIPS > TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.GL_FECHAIPS ELSE TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END;
            PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN NVL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO,PCK_NOMINA.GL_FECHAFIN1) ELSE TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END;
        END IF;
    END IF;
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPS) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, 6, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    IF PCK_NOMINA.GL_SPRC = 99 THEN
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPS) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    END IF;
    PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
    IF (PCK_NOMINA.GL_SMES = 7 AND PCK_NOMINA.GL_SPER = 4) AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO >= PCK_NOMINA.GL_FECHAINI AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO <= PCK_NOMINA.GL_FECHAFIN) THEN
        PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0);
    ELSE
        PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0);
    END IF;
    IF PCK_NOMINA.FC_CN(946) = 0 AND PCK_NOMINA.FC_CN(514) <> 0 THEN
        PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(514) / 12, 0);
    END IF;
    PCK_NOMINA.GL_FACTORPS := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(946), 0);
    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT ;
    IF (PCK_NOMINA.GL_DCC = 179 AND PCK_NOMINA.GL_SMES <= 7) OR PCK_NOMINA.GL_FECHAIPS = PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
        PCK_NOMINA.GL_DOCEAVAS := 6;
        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
    ELSE
        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
    END IF;
    FOR i IN 7..12 LOOP
        PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_SANO - 1), i, 1, (PCK_NOMINA.GL_SANO - 1), i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
            IF NOT (PCK_NOMINA.GL_SMES > 7 AND PCK_NOMINA.FC_CN(404) = 1) THEN  --(MZANGUNA:25/10/2018)-Respete el proporcional de la prima cuando se retira.
                PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
            END IF;
            PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
        END IF;
    END LOOP;
    IF PCK_NOMINA.GL_SMES <= 7 THEN
        FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
            PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                IF NOT (PCK_NOMINA.GL_SMES > 7 AND PCK_NOMINA.FC_CN(404) = 1) THEN  --(MZANGUNA:25/10/2018)-Respete el proporcional de la prima cuando se retira.
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                END IF;
                PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
            END IF;
        END LOOP;
    END IF;
    IF PCK_NOMINA.FC_CN(952) > 0 THEN
        PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
    END IF;
    IF (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 179 AND PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS THEN
        PCK_NOMINA.GL_DCC := 0;
        PCK_NOMINA.GL_DOCEAVAS := 0;
    END IF;
    IF (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 179 AND PCK_NOMINA.GL_FECHAIPS > PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
        PCK_NOMINA.GL_DCC := 0;
        PCK_NOMINA.GL_DOCEAVAS := 0;
    END IF;
    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
    IF PCK_NOMINA.FC_CN(952) > 0 THEN
        PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
        PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.GL_DCC / 30);
    END IF;
    IF ( PCK_NOMINA.FC_CN(404) != 0 ) THEN
        MI_PSPAGADA := 0;
        IF PCK_NOMINA.GL_SMES = 6 OR PCK_NOMINA.GL_SMES = 7 THEN
            PCK_NOMINA.GL_ACS := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA, 160, PCK_NOMINA.GL_SANO, 6, 1, PCK_NOMINA.GL_SANO, 7, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            MI_PSPAGADA := PCK_NOMINA.FC_CNP(160);
        END IF;
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
        PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(953);
        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
        IF (PCK_NOMINA.GL_DCC = 179 AND PCK_NOMINA.GL_SMES <= 6) OR PCK_NOMINA.GL_FECHAIPS = PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
            PCK_NOMINA.GL_DOCEAVAS := 6;
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT ;
        ELSE
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
        END IF;
        IF PCK_NOMINA.GL_SPRC <> 99 THEN
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF ((PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0) THEN
                    IF PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAFPS) <> i AND PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAFPS) < PCK_SYSMAN_UTL.FC_DIA(LAST_DAY(PCK_NOMINA.GL_FECHAFPS)) THEN
                        PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    END IF;
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            FOR i IN 6..12 LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_SANO - 1), i, 1, (PCK_NOMINA.GL_SANO - 1), i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    IF NOT (PCK_NOMINA.GL_SMES > 7 AND PCK_NOMINA.FC_CN(404) = 1) THEN  --(MZANGUNA:25/10/2018)-Respete el proporcional de la prima cuando se retira.
                        PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    END IF;
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
        END IF;
        PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(953);
        IF PCK_NOMINA.GL_DOCEAVAS = 0 OR PCK_NOMINA.GL_DOCEAVAS > 12 THEN
            PCK_NOMINA.GL_DCC := 0;
            PCK_NOMINA.GL_DOCEAVAS := 0;
        END IF;
        IF PCK_NOMINA.FC_CN(952) > 0 THEN
            PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
            PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953) / 30);
        END IF;
        MI_VALOR:= PCK_NOMINA.GL_DOCEAVAS;
        IF (PCK_NOMINA.GL_DOCEAVAS = 0 OR PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS) OR (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 179 AND PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS AND PCK_NOMINA.FC_CN(404) = 0 THEN
            IF PCK_NOMINA.GL_DOCEAVAS = 0 AND PCK_NOMINA.FC_CN(404) = 0 THEN
                PCK_NOMINA.GL_DCC := 0;
                PCK_NOMINA.GL_DOCEAVAS := 0;
                -- 'REVISAR PCK_NOMINA.GL_DOCEAVAS CAUSADAS, YA QUE NO CUMPLIO M??S DE 6 MESES RADICADO 201520160102642 01/06/15  A: ' and PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 and ' ' and PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 and ' ' and PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES
                --Revisar Doceavas Causadas, ya que no cumplio Más de 6 meses Radicado --RADICADO-- --> 01/06/15  a: --NOMBRES--.
                MI_MSG(1).CLAVE := 'NOMBRES';
                MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
                MI_MSG(2).CLAVE := 'RADICADO';
                MI_MSG(2).VALOR := '201520160102642';
                PCK_NOMINA_COM7.PR_ALERTA
                    (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                    ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_DOCEAVASCAUSADAS
                    ,UN_REEMPLAZOS   => MI_MSG
                    ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                    ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                    ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                    ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                    ,UN_USER         => PCK_CONEXION.FC_GETUSER
                    );
            END IF;
        END IF;
        -- LIQUIDACION NOMINA PRIVADA
        --(CFBARRERA_CC:3135_INI)Solo reste el CN 160 si es del mismo mes y año
        PCK_NOMINA.GL_ACS := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA, 160, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        MI_PSPAGADA := PCK_NOMINA.FC_CNP(160);
         PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND( CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXT , 0) / 360 * PCK_NOMINA.GL_DCC , 0) ELSE PCK_NOMINA.FC_CN(160) END - MI_PSPAGADA;--(CFBARRERA_CC:3135_FIN)
        --(APINEDA:22/03/2019)-Se agrega validación para que no se calcule prima semestral cuando el funcionario no ha cumplido 6 meses TAR 1000090732
        --(MZANGUNA:19/12/2019)-Se quita validación dado que ahora si se debe calcular con los días que el empleado lleve
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
            MI_VALOR_F := PCK_NOMINA.GL_FECHAIPS;
            MI_VALOR_F := PCK_NOMINA.GL_FECHAFPS;
            MI_VALOR := PCK_NOMINA.FC_CN(953);
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
                MI_VALOR := PCK_NOMINA.GL_DCC;
                MI_VALOR_F := PCK_NOMINA.GL_FECHAFPS;
                MI_VALOR_F := PCK_NOMINA.GL_FECHAIPS;
                MI_VALOR := PCK_NOMINA.FC_CN(953);
            --(MZANGUNA:19/12/2019)-Se quita condición dado que tocancipa empieza a pagar la prima por días.
                MI_VALOR := PCK_NOMINA.FC_CN(160);
                 MI_VALOR := PCK_NOMINA.GL_DCC;
                 MI_VALOR := PCK_NOMINA.GL_FACTORPS;
                 MI_VALOR := PCK_NOMINA.FC_CN(67);
                --(CFBARRERA_CC:3135_INI)Solo reste el CN 160 si es del mismo mes y año
                 PCK_NOMINA.GL_ACS := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA, 160, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                 MI_PSPAGADA := PCK_NOMINA.FC_CNP(160);
                 PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 180 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67), 0) ELSE PCK_NOMINA.FC_CN(160) END -  MI_PSPAGADA;
                --(CFBARRERA_CC:3135_FIN)                
        ELSE
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67), 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        END IF;
        IF PCK_NOMINA.FC_CN(160) < 0 THEN
            PCK_NOMINA.CN(160) := 0;
        END IF;
        IF UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'TD' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_SANO, i, '01', PCK_NOMINA.GL_SANO, i, '99', PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DCC := PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
                PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 180 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
            ELSE
                PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
            END IF;
        END IF;
        --(APINEDA:26/07/2019)-Se modifica condición debido a que esta descontando la prima semestral a persona que se retira en Julio
        IF MI_PSPAGADA > 0 AND PCK_NOMINA.FC_CN(160) > 0 AND PCK_NOMINA.GL_PROCESOACTUAL <> 99 AND PCK_NOMINA.GL_PERIODOACTUAL <> 7 THEN
            PCK_NOMINA.CN(160) := 0;
            --La prima Semestral ya debió ser pagada, según datos históricos, revise pagos. se eliminara éste concepto en el pago de liquidación a: --NOMEMPLEADO--, Cédula No.--CEDULA--, Tipo: --TIPO--.
            MI_MSG(1).CLAVE := 'NOMEMPLEADO';
            MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
            MI_MSG(2).CLAVE := 'CEDULA';
            MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
            MI_MSG(3).CLAVE := 'TIPO';
            MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO;
            PCK_NOMINA_COM7.PR_ALERTA
                (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_PRIMASEMESTRALPAGA
                ,UN_REEMPLAZOS   => MI_MSG
                ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                ,UN_USER         => PCK_CONEXION.FC_GETUSER
                );
        END IF;
    ELSE
        PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_DOCEAVAS >= MI_DOCEAVASMINIMASPS THEN PCK_NOMINA.GL_DOCEAVAS ELSE 0 END;
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
           --(CFBARRERA_CC:3135_INI)Solo reste el CN 160 si es del mismo mes y año
           PCK_NOMINA.GL_ACS := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA, 160, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
           MI_PSPAGADA := PCK_NOMINA.FC_CNP(160);
           PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 180 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67), 0) ELSE PCK_NOMINA.FC_CN(160) END - MI_PSPAGADA;
        ELSE
            PCK_NOMINA.GL_ACS := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA, 160, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            MI_PSPAGADA := PCK_NOMINA.FC_CNP(160);
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67), 0) ELSE PCK_NOMINA.FC_CN(160) END - MI_PSPAGADA;
        END IF;--(CFBARRERA_CC:3135_FIN)
    END IF;
    MI_VALOR := PCK_NOMINA.GL_DCC;
    MI_VALOR := PCK_NOMINA.GL_AUXT;
    MI_PRIMAJUNIO := PCK_NOMINA.FC_CN(160);
    MI_INDRETIR := PCK_NOMINA.FC_CN(404);
    PCK_NOMINA.CN(945) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
    IF PCK_NOMINA.GL_SPER = 4 THEN
        FOR i IN 2..699 LOOP
            IF (i <> 125) AND (i <> 303) AND (i <> 301) AND (i <> 300) AND (i < 599) OR (i >= 600 AND i <= 698) AND (PCK_NOMINA.FC_CN(i) > 0 AND PCK_NOMINA.FC_CN(i) < 1) THEN
                PCK_NOMINA.CN(i) := 0;
            END IF;
        END LOOP;
    END IF;
    PCK_NOMINA.CN(404) := MI_INDRETIR;
    PCK_NOMINA.CN(67) := PCK_NOMINA.GL_DOCEAVAS;
    PCK_NOMINA.CN(160) := MI_PRIMAJUNIO;
    PCK_NOMINA.CN(947) := 0; --PCK_NOMINA.GL_VPT   ;
    PCK_NOMINA.CN(948) := PCK_NOMINA.GL_AUXT ;
    PCK_NOMINA.CN(949) := 0; --PCK_NOMINA.GL_AUXA ;
    PCK_NOMINA.CN(950) := 0; --PCK_NOMINA.GL_GRPNGV  ;
    PCK_NOMINA.CN(951) := 0; --PCK_NOMINA.FC_CN(67) ;
    PCK_NOMINA.CN(952) := PCK_NOMINA.GL_DCC ;
    IF PCK_NOMINA.GL_SPRC = 99 THEN
        PCK_NOMINA.CN(991) := PCK_NOMINA.FC_CNPR(994) ;
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_SANO, 6, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.CN(992) := PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503);
        PCK_NOMINA.CN(993) := PCK_NOMINA.FC_CN(160) ;
        PCK_NOMINA.CN(994) := PCK_NOMINA.FC_CN(992) - PCK_NOMINA.FC_CN(991) + PCK_NOMINA.FC_CN(993) ;
    END IF;
    PCK_NOMINA.CN(985) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORPS), 0) ;
    PCK_NOMINA.GL_PS_BASE := PCK_NOMINA.GL_FACTORPS;
    PCK_NOMINA.GL_PS_DIAS := PCK_NOMINA.GL_DCC;
END PR_LIQPRIPRISEMSENA;

PROCEDURE PR_CALCPRIMSEMEST_ITTACAC(
/*
NAME              : PR_CALCPRIMSEMEST_ITTACAC
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : JEIMMY CAROLINA ROJAS GUERRERO
DATE MIGRADOR     : 27/08/2025
DESCRIPTION       : PROCEDIMIENTO CALCULO DE LA PRIMA DE JUNIO
*/
    UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA)
AS
    MI_DOCEAVASMINIMASPS PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_PSPAGADA          PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_MSG               PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_INDRETIR          PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
BEGIN
    MI_DOCEAVASMINIMASPS := TO_NUMBER(PCK_PARST.FC_PAR('DOCEAVAS MINIMAS PRIMA SERVICIOS', '1'));
    PCK_NOMINA.GL_DOCEAVAS := 0;
    PCK_NOMINA.GL_FACTORPS := 0;
    PCK_NOMINA.GL_FACTORPS1 := 0;
    PCK_NOMINA.GL_DNT := 0;
    
    IF PCK_NOMINA.GL_SMES <= 7 THEN
        PCK_NOMINA.GL_FECHAIPS := (CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') ELSE TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') END);
        PCK_NOMINA.GL_FECHAIPS := (CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END);
        PCK_NOMINA.GL_FECHAIPS := (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESODISTRITOREAL < PCK_NOMINA.GL_FECHAI THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESODISTRITOREAL ELSE PCK_NOMINA.GL_FECHAIPS END);
        IF PCK_NOMINA.GL_FECHAIPS = TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESODISTRITOREAL, 'DD/MM/YYYY'), 'DD/MM/YYYY') THEN
            --La prima Semestral se calcula con la fecha de ingreso real o continuidad: --FECHADISTR-- a: --NOMEMPLEADO--, CÃ©dula No. --CEDULA--, Tipo: --TIPO--
            MI_MSG(1).CLAVE := 'FECHADISTR';
            MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESODISTRITOREAL;
            MI_MSG(2).CLAVE := 'NOMEMPLEADO';
            MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
            MI_MSG(3).CLAVE := 'CEDULA';
            MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
            MI_MSG(4).CLAVE := 'TIPO';
            MI_MSG(4).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO;
            PCK_NOMINA_COM7.PR_ALERTA(UN_COMPANIA => PCK_NOMINA.GL_COMPANIA,UN_MENSAJE_COD => PCK_ERRORES.ALER_PRIMASEMESFECHACONTIN,UN_REEMPLAZOS => MI_MSG,UN_PROCESO => PCK_NOMINA.GL_PROCESOACTUAL,UN_ANO => PCK_NOMINA.GL_ANOACTUAL,UN_MES => PCK_NOMINA.GL_MESACTUAL,UN_PERIODO => PCK_NOMINA.GL_PERIODOACTUAL,UN_USER => PCK_CONEXION.FC_GETUSER);
            IF PCK_NOMINA.GL_FECHAIPS < TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') THEN
                PCK_NOMINA.GL_FECHAIPS := (CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') ELSE TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') END);
            END IF;
        END IF;
        IF PCK_NOMINA.GL_SPER = 7 AND PCK_NOMINA.FC_CN(404) <> 0 AND PCK_NOMINA.GL_SMES >= 7 THEN
            PCK_NOMINA.GL_FECHAIPS := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        END IF;
    END IF;
    IF PCK_NOMINA.GL_SMES > 7 THEN
        PCK_NOMINA.GL_FECHAIPS := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        PCK_NOMINA.GL_FECHAIPS := (CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END);
        PCK_NOMINA.GL_FECHAIPS := (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESODISTRITOREAL < PCK_NOMINA.GL_FECHAI THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESODISTRITOREAL ELSE PCK_NOMINA.GL_FECHAIPS END);
        
        IF PCK_NOMINA.GL_FECHAIPS = TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESODISTRITOREAL, 'DD/MM/YYYY'), 'DD/MM/YYYY') THEN
            --La prima Semestral se calcula con la fecha de ingreso real o continuidad: --FECHADISTR-- a: --NOMEMPLEADO--, CÃ©dula No. --CEDULA--, Tipo: --TIPO--
            MI_MSG(1).CLAVE := 'FECHADISTR';
            MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESODISTRITOREAL;
            MI_MSG(2).CLAVE := 'NOMEMPLEADO';
            MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
            MI_MSG(3).CLAVE := 'CEDULA';
            MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
            MI_MSG(4).CLAVE := 'TIPO';
            MI_MSG(4).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO;
            PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA => PCK_NOMINA.GL_COMPANIA,UN_MENSAJE_COD => PCK_ERRORES.ALER_PRIMASEMESFECHACONTIN,UN_REEMPLAZOS => MI_MSG,UN_PROCESO => PCK_NOMINA.GL_PROCESOACTUAL,UN_ANO => PCK_NOMINA.GL_ANOACTUAL,UN_MES => PCK_NOMINA.GL_MESACTUAL,UN_PERIODO => PCK_NOMINA.GL_PERIODOACTUAL,UN_USER => PCK_CONEXION.FC_GETUSER);
        END IF;
        PCK_NOMINA.GL_FECHAIPS1 := TO_DATE('01/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        PCK_NOMINA.GL_FECHAIPS1 := (CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS1 THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS1 END);
    END IF;
    IF PCK_NOMINA.GL_SMES = 7 AND PCK_NOMINA.GL_SPER <> 7 THEN
        PCK_NOMINA.GL_FECHAFPS := (CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN (CASE WHEN PCK_NOMINA.GL_FECHAFIN1 > TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') ELSE PCK_NOMINA.GL_FECHAFIN1 END) ELSE
        TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END);
    ELSIF PCK_NOMINA.GL_SMES  = 7 AND PCK_NOMINA.GL_SPER = 7 THEN
        PCK_NOMINA.GL_FECHAFPS := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO;
    ELSE
        PCK_NOMINA.GL_FECHAFPS := (CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN PCK_NOMINA.GL_FECHAFIN1 ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END);
    END IF;
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS),(CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPS) < 16 THEN 1 ELSE 2 END), PCK_NOMINA.GL_SANO, 6, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    IF PCK_NOMINA.GL_SPRC = 99 THEN
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS), (CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPS) < 16 THEN 1 ELSE 2 END), PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    END IF;
    PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
    PCK_NOMINA.CN(946) := 0;
    IF PCK_PARST.FC_PAR('SUMAR BASP A PRIMA SERVICIOS DECRETO 2351', ' ') = 'SI' THEN
        IF (PCK_NOMINA.GL_SMES = 7 AND PCK_NOMINA.GL_SPER = 4) AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO >= PCK_NOMINA.GL_FECHAINI AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO <= PCK_NOMINA.GL_FECHAFIN) THEN
            PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0);
        ELSE
            PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + (CASE WHEN PCK_PARST.FC_PAR('USAR ULTIMA BASP PAGADA EN LIQUIDACION FINAL', 'SI') = 'SI' THEN PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514) ELSE 0 END)) / 12, 0);
        END IF;
        IF PCK_NOMINA.FC_CN(946) = 0 AND PCK_NOMINA.FC_CN(514) <> 0 THEN
            PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(514) / 12, 0);
        END IF;
    END IF;
    PCK_NOMINA.GL_FACTORPS := (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + PCK_NOMINA.FC_CN(946);
    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
    IF (PCK_NOMINA.GL_DCC = 0 OR PCK_NOMINA.GL_DCC = 1) AND (PCK_NOMINA.GL_FECHAIPS <> PCK_NOMINA.GL_FECHAFPS) AND PCK_NOMINA.GL_DNT = 0 THEN
        PCK_NOMINA.GL_DCC := TO_NUMBER(PCK_NOMINA.GL_FECHAIPS - PCK_NOMINA.GL_FECHAFPS) + 1 - PCK_NOMINA.GL_DNT;
    END IF;
    IF PCK_PARST.FC_PAR('ELIMINAR TIEMPO MINIMO EN PRIMA DE SERVICIOS', ' ') = 'SI' THEN
        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.FC_CN(953);
        MI_DOCEAVASMINIMASPS := NVL(TO_NUMBER(PCK_PARST.FC_PAR('DOCEAVAS MINIMAS PRIMA SERVICIOS', '1')),1);
        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS - PCK_NOMINA.FC_CN(953));
    ELSE
        IF PCK_NOMINA.GL_SMES = 7 AND PCK_NOMINA.GL_SPER = 7 THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) + 1 - PCK_NOMINA.GL_DNT;
        END IF;
        IF PCK_NOMINA.GL_DCC = 1 AND (PCK_NOMINA.GL_FECHAIPS <> PCK_NOMINA.GL_FECHAFPS) AND PCK_NOMINA.GL_DNT = 0 THEN
            PCK_NOMINA.GL_DCC := TO_NUMBER(PCK_NOMINA.GL_FECHAIPS - PCK_NOMINA.GL_FECHAFPS) + 1 - PCK_NOMINA.GL_DNT;
        END IF;
        IF (PCK_NOMINA.GL_DCC = 179 AND PCK_NOMINA.GL_SMES <= 7) OR PCK_NOMINA.GL_FECHAIPS = PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
            PCK_NOMINA.GL_DOCEAVAS := 6;
            --DOCEAVASMINIMASPRIMASERVICIOS := 6;
            PCK_PARST.LSTPAR('DOCEAVAS MINIMAS PRIMA SERVICIOS') := '6';
            PCK_NOMINA.GL_DCC := (PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT) /30;
            IF (PCK_NOMINA.GL_DCC = 0 OR PCK_NOMINA.GL_DCC = 1) AND (PCK_NOMINA.GL_FECHAIPS <> PCK_NOMINA.GL_FECHAFPS) AND PCK_NOMINA.GL_DNT = 0 THEN
                PCK_NOMINA.GL_DCC := TO_NUMBER(PCK_NOMINA.GL_FECHAIPS - PCK_NOMINA.GL_FECHAFPS) + 1 - PCK_NOMINA.GL_DNT;
            END IF;
        ELSE
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS - PCK_NOMINA.GL_DNT);
        END IF;
        IF PCK_NOMINA.GL_SMES = 7 AND PCK_NOMINA.GL_SPER = 7 THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) + 1 - PCK_NOMINA.GL_DNT;
            IF (PCK_NOMINA.GL_DCC = 0 OR PCK_NOMINA.GL_DCC = 1) AND (PCK_NOMINA.GL_FECHAIPS <> PCK_NOMINA.GL_FECHAFPS) AND PCK_NOMINA.GL_DNT = 0 THEN
                PCK_NOMINA.GL_DCC := TO_NUMBER(PCK_NOMINA.GL_FECHAIPS - PCK_NOMINA.GL_FECHAFPS) + 1 - PCK_NOMINA.GL_DNT;
            END IF;
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS    - PCK_NOMINA.GL_DNT) + 1;
        END IF;
    END IF;
    FOR i IN 7..12 LOOP
        PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO- 1), i, 1, (PCK_NOMINA.GL_SANO - 1), i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN 
            PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
            PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
        END IF;
    END LOOP;
    IF PCK_NOMINA.GL_SMES <= 7 THEN
        FOR i IN 1..PCK_NOMINA.GL_SMES
        LOOP
            PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF (PCK_NOMINA.FC_CNA(356)                                                                                                                              + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
            END IF;
        END LOOP;
    END IF;
    IF PCK_PARST.FC_PAR('ELIMINAR TIEMPO MINIMO EN PRIMA DE SERVICIOS', ' ') = 'SI' THEN
        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.FC_CN(953);
        IF (PCK_NOMINA.GL_DCC = 0 OR PCK_NOMINA.GL_DCC = 1) AND (PCK_NOMINA.GL_FECHAIPS <> PCK_NOMINA.GL_FECHAFPS) AND PCK_NOMINA.GL_DNT = 0 THEN
            PCK_NOMINA.GL_DCC := TO_NUMBER(PCK_NOMINA.GL_FECHAIPS - PCK_NOMINA.GL_FECHAFPS) + 1 - PCK_NOMINA.GL_DNT;
        END IF;
        MI_DOCEAVASMINIMASPS := NVL(TO_NUMBER(PCK_PARST.FC_PAR('DOCEAVAS MINIMAS PRIMA SERVICIOS', '1')),1);
        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS - PCK_NOMINA.FC_CN(953));
    ELSE        
        IF PCK_NOMINA.FC_CN(952) > 0 THEN
            PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
        END IF;
        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
        IF PCK_NOMINA.GL_DCC < 179 AND PCK_NOMINA.GL_FECHAIPS > PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) AND PCK_NOMINA.GL_FECHAIPS < PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
            PCK_NOMINA.GL_DCC := 0;
            PCK_NOMINA.GL_DOCEAVAS := 0;
        END IF;
        IF (PCK_NOMINA.GL_DCC = 0 OR PCK_NOMINA.GL_DCC = 1) AND (PCK_NOMINA.GL_FECHAIPS <> PCK_NOMINA.GL_FECHAFPS) AND PCK_NOMINA.GL_DNT = 0 THEN
            PCK_NOMINA.GL_DCC := TO_NUMBER(PCK_NOMINA.GL_FECHAIPS - PCK_NOMINA.GL_FECHAFPS) + 1 - PCK_NOMINA.GL_DNT;
        END IF;        
        IF (PCK_NOMINA.GL_DCC = 179 AND PCK_NOMINA.GL_SMES <= 7) OR PCK_NOMINA.GL_FECHAIPS = PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
            PCK_NOMINA.GL_DOCEAVAS := 6;
            PCK_PARST.LSTPAR('DOCEAVAS MINIMAS PRIMA SERVICIOS') := '6';
            PCK_NOMINA.GL_DCC := (PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT) /30;
        ELSE
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS - PCK_NOMINA.GL_DNT);
        END IF;
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > (PCK_NOMINA.GL_FECHAFPS - 180) AND PCK_NOMINA.GL_FECHAIPS > PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
            PCK_NOMINA.GL_DOCEAVAS := 0;
            --Revisar Doceavas Causadas, ya que no cumpliÃ³ mÃ¡s de 6 meses --FECHADIST-- a: --EMPLEADO--
            MI_MSG(1).CLAVE := 'EMPLEADO';
            MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
            MI_MSG(2).CLAVE := 'FECHADIST';
            MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESODISTRITOREAL;
            PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA => PCK_NOMINA.GL_COMPANIA ,UN_MENSAJE_COD => PCK_ERRORES.ALER_DOCEAVASCAUSADASSINRAD ,UN_REEMPLAZOS => MI_MSG ,UN_PROCESO => PCK_NOMINA.GL_PROCESOACTUAL ,UN_ANO => PCK_NOMINA.GL_ANOACTUAL ,UN_MES => PCK_NOMINA.GL_MESACTUAL ,UN_PERIODO => PCK_NOMINA.GL_PERIODOACTUAL ,UN_USER => PCK_CONEXION.FC_GETUSER );
        END IF;
        IF PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS THEN
            PCK_NOMINA.GL_DOCEAVAS := 0;
            PCK_NOMINA.GL_DCC := 0;
        END IF;
        IF (PCK_NOMINA.GL_DOCEAVAS = 0 OR PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS) OR (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 179 AND PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS THEN
            PCK_NOMINA.GL_DCC := 0;
            PCK_NOMINA.GL_DOCEAVAS := 0;
            --Revisar Doceavas Causadas, ya que no cumplio MÃ¡s de 6 meses Radicado --RADICADO--  --> 01/06/15  a: --NOMBRES--.
            MI_MSG(1).CLAVE := 'NOMBRES';
            MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
            MI_MSG(2).CLAVE := 'RADICADO';
            MI_MSG(2).VALOR := '201520160102642';
            PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA => PCK_NOMINA.GL_COMPANIA ,UN_MENSAJE_COD => PCK_ERRORES.ALER_DOCEAVASCAUSADAS ,UN_REEMPLAZOS => MI_MSG ,UN_PROCESO => PCK_NOMINA.GL_PROCESOACTUAL ,UN_ANO => PCK_NOMINA.GL_ANOACTUAL ,UN_MES => PCK_NOMINA.GL_MESACTUAL ,UN_PERIODO => PCK_NOMINA.GL_PERIODOACTUAL ,UN_USER => PCK_CONEXION.FC_GETUSER);
        END IF;
        IF PCK_NOMINA.FC_CN(952)  > 0 THEN
            PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
            PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.GL_DCC / 30);
        END IF;
    END IF;
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ESTADO_ACTUAL = 6 THEN
        PCK_NOMINA.GL_DCC := TO_NUMBER(PCK_NOMINA.GL_DCC - (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_INICIO_COMISION > PCK_NOMINA.GL_FECHAIPS THEN TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_INICIO_COMISION, 'DD/MM/YYYY'), 'DD/MM/YYYY') ELSE PCK_NOMINA.GL_FECHAIPS END)
        - (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL_COMISION < PCK_NOMINA.GL_FECHAFPS THEN TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL_COMISION, 'DD/MM/YYYY'), 'DD/MM/YYYY') ELSE PCK_NOMINA.GL_FECHAFPS END)) + 1;
    END IF;
    IF PCK_NOMINA.GL_FECHAIPS  >= TO_DATE('16/06/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAIPS <= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') AND PCK_NOMINA.FC_CN(404) = 0 THEN
        IF PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS THEN
            PCK_NOMINA.GL_DOCEAVAS := 0;
            PCK_NOMINA.GL_DCC := 0;
        END IF;
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ESTADO_ACTUAL = 6 THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
                PCK_NOMINA.GL_DCC := PCK_NOMINA.GL_DCC - TO_NUMBER((CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_INICIO_COMISION > PCK_NOMINA.GL_FECHAIPS THEN TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_INICIO_COMISION, 'DD/MM/YYYY'), 'DD/MM/YYYY') ELSE PCK_NOMINA.GL_FECHAIPS END)
                - (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL_COMISION < PCK_NOMINA.GL_FECHAFPS THEN TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL_COMISION, 'DD/MM/YYYY'), 'DD/MM/YYYY') ELSE PCK_NOMINA.GL_FECHAFPS END)) + 1;
            ELSE
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
            END IF;
            PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);  
        ELSE
            PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
        END IF;   
    ELSIF PCK_NOMINA.GL_FECHAIPS >= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') AND PCK_NOMINA.FC_CN(404) = 0 AND PCK_NOMINA.GL_DOCEAVAS >= 1 THEN
        IF PCK_PARST.FC_PAR('ELIMINAR TIEMPO MINIMO EN PRIMA DE SERVICIOS', ' ') = 'SI' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.FC_CN(953);
            MI_DOCEAVASMINIMASPS := NVL(TO_NUMBER(PCK_PARST.FC_PAR('DOCEAVAS MINIMAS PRIMA SERVICIOS', '1')),1);
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS - PCK_NOMINA.FC_CN(953));
        ELSE
            IF PCK_NOMINA.GL_DCC        < 179 AND PCK_NOMINA.GL_FECHAIPS > PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) AND PCK_NOMINA.GL_FECHAIPS < PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
                PCK_NOMINA.GL_DCC := 0;
                PCK_NOMINA.GL_DOCEAVAS := 0;
            END IF;
            PCK_NOMINA.GL_DOCEAVAS := (CASE WHEN PCK_NOMINA.GL_DOCEAVAS >= MI_DOCEAVASMINIMASPS THEN PCK_NOMINA.GL_DOCEAVAS ELSE 0 END);
        END IF;
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ESTADO_ACTUAL = 6 THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
                PCK_NOMINA.GL_DCC := PCK_NOMINA.GL_DCC 
                                    - TO_NUMBER((CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_INICIO_COMISION > PCK_NOMINA.GL_FECHAIPS THEN TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_INICIO_COMISION, 'DD/MM/YYYY'), 'DD/MM/YYYY') ELSE PCK_NOMINA.GL_FECHAIPS END) 
                                    - (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL_COMISION < PCK_NOMINA.GL_FECHAFPS THEN TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL_COMISION, 'DD/MM/YYYY'), 'DD/MM/YYYY') ELSE PCK_NOMINA.GL_FECHAFPS END)) + 1;
            ELSE
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
            END IF;
            PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
            IF (PCK_NOMINA.GL_FECHAIPS > PCK_NOMINA.GL_FECHAFPS) AND PCK_NOMINA.GL_SMES = 7 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO >= TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                PCK_NOMINA.CN(160) := 0;
            END IF;
        ELSE
            PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
        END IF;
    ELSIF PCK_NOMINA.FC_CN(404) = 1 THEN
        MI_PSPAGADA := 0;
        IF PCK_NOMINA.GL_SMES = 6 OR PCK_NOMINA.GL_SMES = 7 THEN                
            PCK_NOMINA.GL_ACS := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(UN_COMPANIA, 160, PCK_NOMINA.GL_SANO, 6, 1, PCK_NOMINA.GL_SANO, 7, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            MI_PSPAGADA := PCK_NOMINA.FC_CNP(160);
        END IF;  
        PCK_NOMINA.GL_FECHAIPS := (CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END);
        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
        IF (PCK_NOMINA.GL_DCC = 0 OR PCK_NOMINA.GL_DCC = 1) AND (PCK_NOMINA.GL_FECHAIPS <> PCK_NOMINA.GL_FECHAFPS) AND PCK_NOMINA.GL_DNT = 0 THEN
            PCK_NOMINA.GL_DCC := TO_NUMBER(PCK_NOMINA.GL_FECHAIPS - PCK_NOMINA.GL_FECHAFPS) + 1 - PCK_NOMINA.GL_DNT;
        END IF;    
        IF PCK_NOMINA.GL_SMES = 7 AND PCK_NOMINA.GL_SPER = 7 THEN
            PCK_NOMINA.GL_DCC := TO_NUMBER(PCK_NOMINA.GL_FECHAIPS - PCK_NOMINA.GL_FECHAFPS) + 1 - PCK_NOMINA.GL_DNT;
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS - PCK_NOMINA.GL_DNT) + 1;
        END IF;
        IF (PCK_NOMINA.GL_DCC = 0 OR PCK_NOMINA.GL_DCC = 1) AND (PCK_NOMINA.GL_FECHAIPS <> PCK_NOMINA.GL_FECHAFPS) AND PCK_NOMINA.GL_DNT = 0 THEN
            PCK_NOMINA.GL_DCC := TO_NUMBER(PCK_NOMINA.GL_FECHAIPS - PCK_NOMINA.GL_FECHAFPS) + 1 - PCK_NOMINA.GL_DNT;
        END IF; 
        IF PCK_NOMINA.GL_SPRC <> 99 THEN
            FOR i IN 1..PCK_NOMINA.GL_SMES
            LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF ((PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0) THEN
                    IF PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAFPS)                                                                             <> i AND PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAFPS) < PCK_SYSMAN_UTL.FC_DIA(LAST_DAY(PCK_NOMINA.GL_FECHAFPS)) THEN
                        PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    END IF;
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            FOR i IN 6..12
            LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO- 1), i, 1, (PCK_NOMINA.GL_SANO - 1), i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
        END IF;
        IF PCK_PARST.FC_PAR('ELIMINAR TIEMPO MINIMO EN PRIMA DE SERVICIOS', ' ') = 'SI' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.FC_CN(953);
            MI_DOCEAVASMINIMASPS := NVL(TO_NUMBER(PCK_PARST.FC_PAR('DOCEAVAS MINIMAS PRIMA SERVICIOS', '1')),1);
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS - PCK_NOMINA.FC_CN(953));
        ELSE
            IF PCK_NOMINA.GL_DOCEAVAS = 0 OR PCK_NOMINA.GL_DOCEAVAS > 12 THEN
                PCK_NOMINA.GL_DCC := 0;
                PCK_NOMINA.GL_DOCEAVAS := 0;
            END IF;
            IF PCK_NOMINA.FC_CN(952)  > 0 THEN
                PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
                PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.GL_DCC / 30);
            END IF;
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > (PCK_NOMINA.GL_FECHAFPS - 180) AND MI_DOCEAVASMINIMASPS > 1 THEN
                PCK_NOMINA.GL_DOCEAVAS := 0;
                --Revisar Doceavas Causadas, ya que no cumpliÃ³ mÃ¡s de 6 meses --FECHADIST-- a: --EMPLEADO--
                MI_MSG(1).CLAVE := 'EMPLEADO';
                MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
                MI_MSG(2).CLAVE := 'FECHADIST';
                MI_MSG(2).VALOR := ' ';
                PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA => PCK_NOMINA.GL_COMPANIA ,UN_MENSAJE_COD => PCK_ERRORES.ALER_DOCEAVASCAUSADASSINRAD ,UN_REEMPLAZOS => MI_MSG ,UN_PROCESO => PCK_NOMINA.GL_PROCESOACTUAL ,UN_ANO => PCK_NOMINA.GL_ANOACTUAL ,UN_MES => PCK_NOMINA.GL_MESACTUAL ,UN_PERIODO => PCK_NOMINA.GL_PERIODOACTUAL ,UN_USER => PCK_CONEXION.FC_GETUSER );
            END IF;
            IF (PCK_NOMINA.GL_DOCEAVAS = 0 OR PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS) OR (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 179 AND PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS  AND PCK_NOMINA.GL_SPRC <> 99 THEN
                PCK_NOMINA.GL_DCC := 0;
                PCK_NOMINA.GL_DOCEAVAS := 0;
                --Revisar Doceavas Causadas, ya que no cumplio MÃ¡s de 6 meses Radicado --RADICADO--  --> 01/06/15  a: --NOMBRES--.
                MI_MSG(1).CLAVE := 'NOMBRES';
                MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
                MI_MSG(2).CLAVE := 'RADICADO';
                MI_MSG(2).VALOR := '201520160102642';
                PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA => PCK_NOMINA.GL_COMPANIA ,UN_MENSAJE_COD => PCK_ERRORES.ALER_DOCEAVASCAUSADAS ,UN_REEMPLAZOS => MI_MSG ,UN_PROCESO => PCK_NOMINA.GL_PROCESOACTUAL ,UN_ANO => PCK_NOMINA.GL_ANOACTUAL ,UN_MES => PCK_NOMINA.GL_MESACTUAL ,UN_PERIODO => PCK_NOMINA.GL_PERIODOACTUAL ,UN_USER => PCK_CONEXION.FC_GETUSER);
            END IF;
            IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESODISTRITOREAL, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT < 179 THEN
                PCK_NOMINA.GL_DCC := 0;
                PCK_NOMINA.GL_DOCEAVAS := 0;
            END IF;
        END IF;
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ESTADO_ACTUAL = 6 THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
                PCK_NOMINA.GL_DCC := PCK_NOMINA.GL_DCC - TO_NUMBER((CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_INICIO_COMISION > PCK_NOMINA.GL_FECHAIPS THEN TO_DATE(TO_NUMBER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_INICIO_COMISION, 'DD/MM/YYYY'), 'DD/MM/YYYY') ELSE PCK_NOMINA.GL_FECHAIPS END) 
                                     - (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL_COMISION < PCK_NOMINA.GL_FECHAFPS THEN TO_DATE(TO_NUMBER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL_COMISION, 'DD/MM/YYYY'), 'DD/MM/YYYY') ELSE PCK_NOMINA.GL_FECHAFPS END)) + 1;
            ELSE
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
            END IF;
            IF (PCK_NOMINA.GL_DCC = 0 OR PCK_NOMINA.GL_DCC = 1) AND (PCK_NOMINA.GL_FECHAIPS <> PCK_NOMINA.GL_FECHAFPS) AND PCK_NOMINA.GL_DNT = 0 THEN
                PCK_NOMINA.GL_DCC := TO_NUMBER(PCK_NOMINA.GL_FECHAIPS - PCK_NOMINA.GL_FECHAFPS) + 1 - PCK_NOMINA.GL_DNT;
            END IF;
            IF (PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.FC_CN(953)) >= 180 THEN
                PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
            END IF;
        ELSE
            PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
        END IF;
        IF UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'TD' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
            FOR i IN 1..PCK_NOMINA.GL_SMES
            LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DCC := PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CNA(359);
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ESTADO_ACTUAL = 6 THEN
                    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
                    PCK_NOMINA.GL_DCC := PCK_NOMINA.GL_DCC - TO_NUMBER((CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_INICIO_COMISION > PCK_NOMINA.GL_FECHAIPS THEN TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_INICIO_COMISION, 'DD/MM/YYYY'), 'DD/MM/YYYY') ELSE PCK_NOMINA.GL_FECHAIPS END) 
                                         - (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL_COMISION < PCK_NOMINA.GL_FECHAFPS THEN TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL_COMISION, 'DD/MM/YYYY'), 'DD/MM/YYYY') ELSE PCK_NOMINA.GL_FECHAFPS END)) + 1;
                ELSE
                    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
                END IF;
                PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
            ELSE
                PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
            END IF;
        END IF;
        IF MI_PSPAGADA > 0 AND PCK_NOMINA.FC_CN(160) > 0 AND PCK_NOMINA.GL_DCC > 30 AND PCK_NOMINA.GL_SPRC <> 99 THEN
            PCK_NOMINA.CN(160) := 0;
            --La prima Semestral ya debiÃ³ ser pagada, segÃºn datos histÃ³ricos, revise pagos. se eliminara Ã©ste concepto en el pago de liquidaciÃ³n a: --NOMEMPLEADO--, CÃ©dula No.--CEDULA--, Tipo: --TIPO--.
            MI_MSG(1).CLAVE := 'NOMEMPLEADO';
            MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
            MI_MSG(2).CLAVE := 'CEDULA';
            MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
            MI_MSG(3).CLAVE := 'TIPO';
            MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO;
            PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA => PCK_NOMINA.GL_COMPANIA ,UN_MENSAJE_COD => PCK_ERRORES.ALER_PRIMASEMESTRALPAGA ,UN_REEMPLAZOS => MI_MSG ,UN_PROCESO => PCK_NOMINA.GL_PROCESOACTUAL ,UN_ANO => PCK_NOMINA.GL_ANOACTUAL ,UN_MES => PCK_NOMINA.GL_MESACTUAL ,UN_PERIODO => PCK_NOMINA.GL_PERIODOACTUAL ,UN_USER => PCK_CONEXION.FC_GETUSER);
        END IF;
    ELSE
        IF PCK_PARST.FC_PAR('ELIMINAR TIEMPO MINIMO EN PRIMA DE SERVICIOS', ' ') = 'SI' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.FC_CN(953);
            MI_DOCEAVASMINIMASPS := NVL(TO_NUMBER(PCK_PARST.FC_PAR('DOCEAVAS MINIMAS PRIMA SERVICIOS', '1')),1);
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS - PCK_NOMINA.FC_CN(953));
        ELSE
            PCK_NOMINA.GL_DOCEAVAS := (CASE WHEN PCK_NOMINA.GL_DOCEAVAS >= MI_DOCEAVASMINIMASPS THEN PCK_NOMINA.GL_DOCEAVAS ELSE 0 END);
        END IF;
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > (PCK_NOMINA.GL_FECHAFPS - 180) AND PCK_NOMINA.GL_FECHAIPS > PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) AND PCK_NOMINA.GL_FECHAIPS < PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
                PCK_NOMINA.GL_DOCEAVAS := 0;
                PCK_NOMINA.GL_DCC := 0;
            END IF;
            PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
        ELSE
            PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
        END IF;
    END IF;
    PCK_NOMINA.GL_PRIMAJUN := PCK_NOMINA.FC_CN(160);
    MI_INDRETIR := PCK_NOMINA.FC_CN(404);
    PCK_NOMINA.CN(945) := (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END);
    IF PCK_NOMINA.GL_SPER = 4 THEN
        FOR i IN 2..699 LOOP
            IF (i <> 125) AND (i <> 303) AND (i <> 301) AND (i <> 404) AND (i <> 300) AND (i < 599) OR (i >= 600 AND i <= 698) AND (PCK_NOMINA.FC_CN(i) > 0 AND PCK_NOMINA.FC_CN(i) < 1) THEN
                IF PCK_NOMINA.FC_CN(i) <> 0.01 THEN
                    PCK_NOMINA.CN(i) := 0;
                END IF;
            END IF;
        END LOOP;
    END IF;
    PCK_NOMINA.CN(404) := MI_INDRETIR;
    PCK_NOMINA.CN(67) := PCK_NOMINA.GL_DOCEAVAS;
    PCK_NOMINA.CN(160) := PCK_NOMINA.GL_PRIMAJUN;
    PCK_NOMINA.CN(947) := 0;
    PCK_NOMINA.CN(948) := PCK_NOMINA.GL_AUXT;
    PCK_NOMINA.CN(949) := PCK_NOMINA.GL_AUXA;
    PCK_NOMINA.CN(950) := 0 ;
    PCK_NOMINA.CN(951) := PCK_NOMINA.FC_CN(67);
    PCK_NOMINA.CN(952) := PCK_NOMINA.GL_DCC;
    IF PCK_NOMINA.GL_SPRC = 99 THEN
        PCK_NOMINA.CN(991) := PCK_NOMINA.FC_CNPR(994) ;
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 6, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.CN(992) := PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503);
        PCK_NOMINA.CN(993) := PCK_NOMINA.FC_CN(160) ;
        PCK_NOMINA.CN(994) := PCK_NOMINA.FC_CN(992) - PCK_NOMINA.FC_CN(991) + PCK_NOMINA.FC_CN(993) ;
    END IF;
    PCK_NOMINA.CN(985) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORPS), 0);
    PCK_NOMINA.GL_PS_BASE := PCK_NOMINA.GL_FACTORPS;
    PCK_NOMINA.GL_PS_DIAS := PCK_NOMINA.GL_DCC;
END PR_CALCPRIMSEMEST_ITTACAC;

PROCEDURE PR_CALCRPRIMNAVIDAD_ITTACAC(
    /*
    NAME              : PR_CALCRPRIMNAVIDAD_ITTACAC
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JEIMMY CAROLINA ROJAS GUERRERO
    DATE MIGRADOR     : 27/08/2025
    DESCRIPTION       : PROCEDIMIENTO CALCULO DE LA PRIMA DE NAVIDAD
*/
    UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA
)
AS
    MI_FECHAFPN         DATE;
    MI_TRANSPORTELEGAL  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_RETEFUENTE       PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_FACTORSUELDO     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
BEGIN
    PCK_NOMINA.GL_PVAC := 0;
    IF NOT(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '10' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '11') THEN
        PCK_NOMINA.GL_FACTORPN := 0;
        PCK_NOMINA.GL_DNT := 0;
        
        PCK_NOMINA.GL_FECHAIPN := TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        PCK_NOMINA.GL_FECHAIPN := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPN THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPN END;
        PCK_NOMINA.GL_FECHAIPN1 := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        PCK_NOMINA.GL_FECHAIPN1 := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPN1 THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPN1 END;
        
        IF ((PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO = '373' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO = '372') AND PCK_NOMINA.GL_SANO = 2005) OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO < PCK_NOMINA.GL_FECHAIPN THEN
            PCK_NOMINA.GL_FECHAIPN := TO_DATE('01/' || PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN) || '/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            PCK_NOMINA.GL_FECHAI := PCK_NOMINA.GL_FECHAIPN;
        END IF;
        IF PCK_NOMINA.GL_SMES = 12 AND (PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3) THEN
            PCK_NOMINA.GL_FECHAIPN := TO_DATE('01/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            PCK_NOMINA.GL_FECHAIPN1 := TO_DATE('01/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        END IF;
        IF PCK_NOMINA.FC_CN(404) = 0 AND PCK_NOMINA.GL_FECHAI <= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
            MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_PVAC := 0;
            PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0);
            PCK_NOMINA.CN(942) := 0;
            PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0);
            PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
            PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0) ;
            PCK_NOMINA.GL_FACTORPN := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.FC_CN(942) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(931) + (PCK_NOMINA.GL_PVAC / 12) + PCK_NOMINA.FC_CN(939);            
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938) + (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            IF PCK_NOMINA.GL_FECHAR IS NOT NULL THEN
                IF PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
                END IF;
            END IF;
            PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);
            IF PCK_NOMINA.FC_CN(937) > 0 THEN
                PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937)/30);
            END IF;
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN 
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC, 0);
                END IF;
                IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(404) <> 0 THEN
                    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 12, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                    PCK_NOMINA.CN(504) := CASE WHEN PCK_NOMINA.FC_CN(504) = 0 THEN PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158) ELSE PCK_NOMINA.FC_CN(504) END;
                    PCK_NOMINA.CN(158) := 0;
                END IF;
            ELSE
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                END IF;
            END IF;
        ELSIF PCK_NOMINA.FC_CN(404) <> 0 THEN
            MI_FECHAFPN := PCK_NOMINA.GL_FECHAR;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.CN(942) := 0;
            IF PCK_NOMINA.FC_CN(164) > 1 THEN
                PCK_NOMINA.GL_PVAC := ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0) / PCK_NOMINA.FC_CNA(164);
            ELSE
                IF ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0) > 0 THEN
                    PCK_NOMINA.GL_PVAC := ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0);
                ELSE
                    PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CN(155);
                END IF;
                IF PCK_NOMINA.FC_CN(155) > 0 AND PCK_NOMINA.GL_SPRC <> 99 AND PCK_NOMINA.CPARENTRADA(1).NIT <> '820000042-4' THEN
                    PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CN(155);
                ELSE 
                    PCK_NOMINA.GL_PVAC := ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0);
                END IF;
            END IF;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO - 1), PCK_NOMINA.GL_SMES,  1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF PCK_NOMINA.FC_CN(160) > 0 AND PCK_NOMINA.GL_SPRC <> 99 THEN
                PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503))/ 12, 0);
            ELSIF (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) > 0 THEN
                PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12, 0);
            END IF;
            PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
            IF PCK_NOMINA.FC_CN(150) > 0  AND PCK_NOMINA.GL_SPRC <> 99 THEN
                PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(150)) / 12, 0);
            ELSIF PCK_NOMINA.FC_CNA(150) > 0 THEN 
                PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514)) / 12, 0);
            END IF;        
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938) + (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            PCK_NOMINA.GL_FACTORPN := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.FC_CN(942) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(931) + (PCK_NOMINA.GL_PVAC / 12) + PCK_NOMINA.FC_CN(939);            
            IF PCK_NOMINA.FC_CN(937) > 0 THEN
                PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937) / 30);
            END IF;
            PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC, 0);
                END IF;
                IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(404) <> 0 THEN
                    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 12, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                    PCK_NOMINA.CN(504) := CASE WHEN PCK_NOMINA.FC_CN(504) = 0 THEN PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158) ELSE PCK_NOMINA.FC_CN(504) END;
                    PCK_NOMINA.CN(158) := 0;
                END IF;
            ELSE
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                END IF;
                IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.GL_SPER = 7 THEN
                    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 12, 4, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                    PCK_NOMINA.CN(158) := PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158);
                END IF;
            END IF;
        ELSIF PCK_NOMINA.GL_FECHAI > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAI <= TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
            MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            IF NVL(PCK_NOMINA.GL_FECHAR, '') <> '' THEN
                IF PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
                END IF;
            END IF;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, 11, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF PCK_NOMINA.FC_CNA(164) > 1 THEN
                PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0) / PCK_NOMINA.FC_CNA(164);
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0);
            END IF;
            PCK_NOMINA.CN(942) := 0;
            PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0) / 12 ;
            PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
            PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0) / 12 ;
            PCK_NOMINA.GL_FACTORPN := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(931) + (PCK_NOMINA.GL_PVAC / 12) + PCK_NOMINA.FC_CN(939);
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
            FOR i IN 1..PCK_NOMINA.GL_SMES 
            LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938) + (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            IF PCK_NOMINA.FC_CN(937) > 0 THEN
                PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937) / 30);
            END IF;
            PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC, 0);
                END IF;
                IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(404) <> 0 THEN
                    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 12, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                    PCK_NOMINA.CN(504) := CASE WHEN PCK_NOMINA.FC_CN(504) = 0 THEN PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158) ELSE PCK_NOMINA.FC_CN(504) END;
                    PCK_NOMINA.CN(158) := 0;
                END IF;
            ELSE
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                END IF;
            END IF;
        ELSIF PCK_NOMINA.GL_FECHAI > TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
            MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF PCK_NOMINA.FC_CNA(155) = 0 THEN
                PCK_NOMINA.GL_PVAC := 0;
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155);
            END IF;
            IF PCK_NOMINA.CPARENTRADA(1).NIT = '899999419-1' AND PCK_NOMINA.FC_CN(404) <> 0 AND PCK_NOMINA.GL_SPRC = 1 THEN
                PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0);
            END IF;
            PCK_NOMINA.CN(942) := 0 ;
            PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0) / 12 ;
            PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
            PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0) / 12 ;
            PCK_NOMINA.GL_FACTORPN := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(931) + (PCK_NOMINA.GL_PVAC / 12) + PCK_NOMINA.FC_CN(939);
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
            FOR i IN 1..PCK_NOMINA.GL_SMES 
            LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938) + (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            IF PCK_NOMINA.FC_CN(937) > 0 THEN
                PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937) / 30);
            END IF;
            PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC, 0);
                END IF;
                IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(404) <> 0 THEN
                    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 12, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                    PCK_NOMINA.CN(504) := CASE WHEN PCK_NOMINA.FC_CN(504) = 0 THEN PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158) ELSE PCK_NOMINA.FC_CN(504) END;
                    PCK_NOMINA.CN(158) := 0;
                END IF;
            ELSE
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                END IF;
            END IF;
        END IF;
    END IF;
    PCK_NOMINA.GL_PRIMADIC := PCK_NOMINA.FC_CN(158);
    MI_TRANSPORTELEGAL := PCK_NOMINA.FC_CN(254);
    MI_RETEFUENTE := PCK_NOMINA.FC_CN(125);
    IF PCK_NOMINA.GL_SPER = 4 THEN
        FOR i IN 2..599 LOOP
            IF (i <> 125) AND (i < 599) AND (i <> 303) AND (i <> 301) AND (i <> 10) AND (i <> 300) OR (i >= 600 AND i <= 698) AND (PCK_NOMINA.FC_CN(i) > 0 AND PCK_NOMINA.FC_CN(i) < 1) THEN
                PCK_NOMINA.CN(i) := 0;
            END IF;
        END LOOP;
    END IF;
    PCK_NOMINA.CN(125) := MI_RETEFUENTE;
    PCK_NOMINA.CN(254) := MI_TRANSPORTELEGAL;
    PCK_NOMINA.CN(158) := PCK_NOMINA.GL_PRIMADIC;
    PCK_NOMINA.CN(67) := PCK_NOMINA.GL_DOCEAVAS;
    PCK_NOMINA.CN(930) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
    PCK_NOMINA.CN(933) := PCK_NOMINA.GL_AUXT ;
    PCK_NOMINA.CN(934) := PCK_NOMINA.GL_AUXA;
    PCK_NOMINA.CN(936) := PCK_NOMINA.FC_CN(67) ;
    PCK_NOMINA.CN(937) := PCK_NOMINA.GL_DCC;
    PCK_NOMINA.CN(935) := PCK_NOMINA.GL_GRPNGV;
    PCK_NOMINA.CN(940) := PCK_NOMINA.GL_VPT;
END PR_CALCRPRIMNAVIDAD_ITTACAC;

PROCEDURE PR_CALCPRIMAVAC_ITTACACIAS
/*
NAME              : PR_CALCPRIMAVAC_ITTACACIAS
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : JEIMMY CAROLINA ROJAS GUERRERO
DATE MIGRADOR     : 28/08/2025
DESCRIPTION       : PROCEDIMIENTO CALCULO DE LA PRIMA DE VACACIONES
*/
AS
    MI_BONPAGADA     PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_FECHA         DATE;
    MI_VALOR         NUMBER DEFAULT 0;
BEGIN
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '10' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '11' THEN
        PCK_NOMINA.CN(174) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + (PCK_NOMINA.FC_CN(160) / 12), 0) / 2;
    ELSE
        PCK_NOMINA.GL_DIASVAC := 0;
        PCK_NOMINA.GL_DIASPENDIENTES := 0;
        PCK_NOMINA.GL_PENDIENTES := 0;
        PCK_NOMINA.GL_LICENCIAS := 0;
        PCK_NOMINA.GL_PRIMAPROPORCIONAL := 0;
        PCK_NOMINA.GL_DINEROPROPORCIONAL := 0;
        PCK_NOMINA.GL_FECHAUV := CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL END;
        IF PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.GL_SPER = 8 THEN
            PCK_NOMINA.CN(984) := 0;
            IF PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) <> 0 THEN
                PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + (CASE WHEN PCK_PARST.FC_PAR('USAR ULTIMA PRIMA SEMESTRAL PAGADA EN LIQUIDACION FINAL', 'SI') = 'SI' THEN PCK_NOMINA.FC_CN(160) ELSE 0 END)) / 12, 0);
                IF PCK_NOMINA.FC_CN(160) > PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) AND PCK_NOMINA.FC_CN(404) <> 0 AND (PCK_NOMINA.GL_SMES = 7 OR PCK_NOMINA.GL_SMES = 6) AND PCK_NOMINA.GL_SANO >= 2022 THEN
                    PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160) / 12), 0);
                END IF;
                IF (PCK_NOMINA.GL_SMES = 7 OR PCK_NOMINA.GL_SMES = 6) AND (PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) <> 0 AND PCK_NOMINA.FC_CN(160) <> 0) AND PCK_NOMINA.FC_CN(404) <> 0 THEN
                    PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA, 160, PCK_NOMINA.GL_SANO, 6, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                    IF PCK_NOMINA.FC_CN(160) = 0 AND PCK_NOMINA.FC_CN(160) > 0 THEN
                        PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160)) / 12, 0);
                    ELSIF PCK_NOMINA.FC_CNP(160) > 0 AND PCK_NOMINA.FC_CN(160) > 0 THEN
                        PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNP(160) + PCK_NOMINA.FC_CN(160)) / 12, 0);
                    END IF;
                    IF PCK_NOMINA.FC_CN(981) = 0 THEN
                        PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + (CASE WHEN PCK_PARST.FC_PAR('USAR ULTIMA PRIMA SEMESTRAL PAGADA EN LIQUIDACION FINAL', 'SI') = 'SI' THEN PCK_NOMINA.FC_CN(160) ELSE 0 END)) / 12, 0);
                    END IF;
                END IF;
            ELSE
                PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160)) / 12, 0);
            END IF;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAUV), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAUV), 1, (PCK_NOMINA.GL_SANO), PCK_NOMINA.GL_SMES, 99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_LICENCIAS := NVL(((PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(339) + PCK_NOMINA.FC_CNA(339)) 
                                       + (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.FC_CESANTIA(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 'L') ELSE 0 END) 
                                       + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION),0);
            PCK_NOMINA.GL_DIASPENDIENTES := PCK_NOMINA.FC_CNA(91);
            PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL((CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL 
                                 + (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL = PCK_NOMINA.GL_FECHAI THEN 0 ELSE 1 END) END), PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
            PCK_NOMINA.GL_DIASPROP := PCK_NOMINA.GL_DTV - PCK_NOMINA.GL_LICENCIAS;
            PCK_NOMINA.GL_PERIODOS := TRUNC(PCK_NOMINA.GL_DTV / 360);
            IF (PCK_NOMINA.GL_DTV - (360 * PCK_NOMINA.GL_PERIODOS)) >= 315 THEN
                PCK_NOMINA.GL_PERIODOS := PCK_NOMINA.GL_PERIODOS + 1;
            END IF;
            PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 * PCK_NOMINA.GL_PERIODOS, 2);
            PCK_NOMINA.CN(93) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(68) * CASE WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN 1 ELSE PCK_NOMINA.GL_PERIODOS END, 2);
            IF PCK_NOMINA.GL_DIASVAC = 0 THEN
                PCK_NOMINA.GL_PERIODOS := CASE WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN 1 ELSE PCK_NOMINA.GL_PERIODOS END;
                PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 * CASE WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN 1 ELSE PCK_NOMINA.GL_PERIODOS END / 360 * PCK_NOMINA.GL_DTV, 0);
            END IF;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_ANOA), PCK_NOMINA.GL_MESA, PCK_NOMINA.GL_PERA, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            MI_BONPAGADA := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            MI_BONPAGADA := CASE WHEN MI_BONPAGADA > 0 THEN PCK_SYSMAN_UTL.FC_ROUND(MI_BONPAGADA/ 12, 0) ELSE PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0) END;    
            IF PCK_NOMINA.GL_DTV = 0 THEN
                PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA, 0);
            ELSE
                PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA, 0);
            END IF;
            PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL((CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL 
                                 + (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL = PCK_NOMINA.GL_FECHAI THEN 0 ELSE 1 END) END), PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
            PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 * CASE WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN 1 ELSE PCK_NOMINA.GL_PERIODOS END / 360 * PCK_NOMINA.GL_DTV, 0);
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                PCK_NOMINA.CN(155) := CASE WHEN PCK_NOMINA.FC_CN(155) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CNAN(10) <> 0 THEN PCK_NOMINA.FC_CNAN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA) * 15 / 30 / 360 * PCK_NOMINA.GL_DTV, 0) ELSE PCK_NOMINA.FC_CN(155) END;
            ELSE
                PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CNAN(10) <> 0 THEN PCK_NOMINA.FC_CNAN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA, 0);
                PCK_NOMINA.CN(155) := CASE WHEN PCK_NOMINA.FC_CN(155) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CNAN(10) <> 0 THEN PCK_NOMINA.FC_CNAN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA, 0) / 30 * 15 / 360 * PCK_NOMINA.GL_DTV, 0) ELSE PCK_NOMINA.FC_CN(155) END;
            END IF;
            IF PCK_NOMINA.FC_CN(175) = 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '01' THEN
                PCK_NOMINA.GL_DTV := PCK_NOMINA.GL_DTV / 30 * 15 / 360;
                IF PCK_NOMINA.GL_SPRC = 99 THEN
                    PCK_NOMINA.GL_RTA := 7;
                END IF;
                PCK_NOMINA.GL_FECHAFF := '';
                PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, TO_DATE(TO_CHAR(CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END, 'DD/MM/YYYY'), 'DD/MM/YYYY') - 1, NVL(PCK_NOMINA.GL_DIASVAC, 1)); 
                IF PCK_NOMINA.GL_SPER = 8 AND PCK_NOMINA.GL_SMES = 12 THEN
                    PCK_NOMINA.GL_PERIODOS := 1;
                    PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1 END, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
                    PCK_NOMINA.CN(93) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(68) * PCK_NOMINA.GL_PERIODOS, 2);
                    PCK_NOMINA.GL_DIASVAC := CASE WHEN PCK_NOMINA.GL_DIASVAC = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(15 * PCK_NOMINA.GL_PERIODOS, 2) ELSE PCK_NOMINA.GL_DIASVAC END;
                    PCK_NOMINA.GL_DIASVAC := TRUNC(PCK_NOMINA.GL_DIASVAC / 360 * PCK_NOMINA.GL_DTV);
                    PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_FECHAFIN + 1, PCK_NOMINA.GL_DIASVAC);
                END IF;
                IF PCK_NOMINA.FC_CN(96) = 0 THEN
                    IF PCK_NOMINA.GL_RTA = 6 THEN
                        PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END, PCK_NOMINA.GL_DIASVAC);
                        PCK_NOMINA.CN(96) := TO_NUMBER(TO_DATE(TO_CHAR(CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END, 'DD/MM/YYYY'), 'DD/MM/YYYY') - PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END, PCK_NOMINA.GL_DIASVAC, 0)) + 1;
                        PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END, PCK_NOMINA.GL_DIASVAC));
                    ELSE
                        PCK_NOMINA.GL_FECHAFF1 := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END, PCK_NOMINA.GL_DIASVAC);
                        PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END, PCK_NOMINA.GL_DIASVAC);
                        IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL = PCK_NOMINA.GL_FECHAI THEN 0 ELSE 1 END) END, PCK_NOMINA.GL_FECHAFIN1) > 315 THEN
                            PCK_NOMINA.CN(96) := TO_NUMBER(TO_DATE(TO_CHAR(CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END, 'DD/MM/YYYY'), 'DD/MM/YYYY') - PCK_NOMINA.GL_FECHAFF1) + 1;
                            PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL((CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END), PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, (CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END), PCK_NOMINA.GL_DIASVAC));
                        END IF;
                        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN
                            PCK_NOMINA.CN(96) := (PCK_NOMINA.GL_FECHAFF - (PCK_NOMINA.GL_FECHAFIN + 1));
                            PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                            IF PCK_NOMINA.FC_CN(96) = 0 AND PCK_NOMINA.GL_DIASVAC = 1 THEN
                                PCK_NOMINA.CN(96) := 1;
                            END IF;
                            IF PCK_NOMINA.FC_CN(96) < 0 AND PCK_NOMINA.GL_DIASVAC <> 0 THEN
                                PCK_NOMINA.CN(96) := 0;
                            END IF;
                        END IF;
                    END IF;
                END IF;
                IF PCK_NOMINA.FC_CN(96) = 0 THEN
                    PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END, PCK_NOMINA.GL_DIASVAC);
                    PCK_NOMINA.CN(96) := TO_NUMBER(PCK_NOMINA.GL_FECHAFF - TO_DATE(TO_CHAR(CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END, 'DD/MM/YYYY'), 'DD/MM/YYYY')) + 1;
                    PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END, PCK_NOMINA.GL_DIASVAC));
                    IF TRUNC(PCK_NOMINA.GL_DIASVAC) = 1 THEN
                        PCK_NOMINA.CN(96) := TRUNC(PCK_NOMINA.GL_DIASVAC);
                    END IF;
                    PCK_NOMINA.CN(96) := PCK_NOMINA.FC_CN(96) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_DIASVAC - TRUNC(PCK_NOMINA.GL_DIASVAC), 2);
                END IF;
                PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 * PCK_NOMINA.GL_PERIODOS, 2);
                PCK_NOMINA.CN(164) := PCK_NOMINA.GL_PERIODOS;
                PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL((CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL 
                                     + (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL = PCK_NOMINA.GL_FECHAI THEN 0 ELSE 1 END) END), PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
                PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(96) / 30, 0);
                IF PCK_NOMINA.GL_SPRC = '99' THEN
                    PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175)    ;
                ELSE
                    PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175) + PCK_NOMINA.GL_PENDIENTES;
                END IF;
                IF PCK_NOMINA.GL_SPER = 8 AND PCK_NOMINA.GL_SMES = 12 THEN
                    PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543)) / 12, 0);
                    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                        PCK_NOMINA.CN(155) := PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CNA(10) <> 0 THEN PCK_NOMINA.FC_CNA(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543)) / 12 + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(538)) / 12) * PCK_NOMINA.FC_CN(93) / 30 / 360 * PCK_NOMINA.GL_DTV, 0);
                    ELSE
                        PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CNA(10) <> 0 THEN PCK_NOMINA.FC_CNA(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543)) / 12 + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(543)) / 12, 0);
                        PCK_NOMINA.CN(155) := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CNA(10) <> 0 THEN PCK_NOMINA.FC_CNA(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543)) / 12 + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(543)) / 12, 0) * PCK_NOMINA.FC_CN(93) / 30 / 360 * PCK_NOMINA.GL_DTV, 0);
                    END IF;
                    PCK_NOMINA.CN(964) := PCK_NOMINA.GL_DIASPENDIENTES;
                    PCK_NOMINA.GL_DIASPROPORCIONAL := PCK_NOMINA.GL_DTV;
                END IF;
                PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) < 0 THEN 0 ELSE PCK_NOMINA.FC_CN(175) END;
            ELSIF PCK_NOMINA.FC_CN(175) = 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                PCK_NOMINA.GL_DTV := PCK_NOMINA.GL_DTV / 30 * 15 / 360;
                IF PCK_NOMINA.GL_SPRC = 99 THEN
                    PCK_NOMINA.GL_RTA := 7;
                END IF;
                PCK_NOMINA.GL_FECHAFF := '';
                PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL((CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL 
                                     + (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL = PCK_NOMINA.GL_FECHAI THEN 0 ELSE 1 END) END), PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
                PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 * PCK_NOMINA.GL_DTV / 360, 0);
                PCK_NOMINA.CN(164) := CASE WHEN PCK_NOMINA.GL_DIASVAC > 0 THEN 1 ELSE PCK_NOMINA.GL_DIASVAC END;
                PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                IF PCK_NOMINA.FC_CN(96) = 0 THEN
                    IF PCK_NOMINA.GL_RTA = 6 THEN
                        PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                        PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                    ELSE
                        PCK_NOMINA.GL_FECHAFF1 := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                        PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                        PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                    END IF;
                END IF;
                IF PCK_NOMINA.FC_CN(96) = 0 THEN
                    PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                    PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                END IF;
                PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(96) / 30, 0);
                IF PCK_NOMINA.GL_SPRC = '99' THEN
                    PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175)    ;
                ELSE
                    PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175) + PCK_NOMINA.GL_PENDIENTES;
                END IF;
                PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) < 0 THEN 0 ELSE PCK_NOMINA.FC_CN(175) END;
            END IF;
            IF (PCK_NOMINA.GL_SPER = 5 OR PCK_NOMINA.GL_SPER = 7) OR PCK_NOMINA.FC_CN(404) <> 0 THEN
                PCK_NOMINA.CN(68) := 0;
                PCK_NOMINA.CN(93) := CASE WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN 1 ELSE PCK_NOMINA.GL_PERIODOS END;
                IF PCK_NOMINA.FC_CN(155) > 1 OR PCK_NOMINA.FC_CN(155) = 0 THEN
                    IF PCK_NOMINA.GL_DIASPROP > 315 THEN
                        PCK_NOMINA.GL_DIASPROP := PCK_NOMINA.GL_DIASPROP - 360;
                        PCK_NOMINA.GL_DIASPROP := CASE WHEN PCK_NOMINA.GL_DIASPROP < 0 THEN 0 ELSE PCK_NOMINA.GL_DIASPROP END;
                    END IF;
                    IF PCK_NOMINA.GL_DIASPROP >= 0 AND ((PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06') AND PCK_NOMINA.FC_CN(155) = 0) THEN
                        PCK_NOMINA.CN(68) := 15;
                        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                            PCK_NOMINA.CN(155) := PCK_NOMINA.FC_CN(155) + PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_GRPNGV + MI_BONPAGADA + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(543) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160)) / 12) * PCK_NOMINA.FC_CN(68) / 30 / 360 * PCK_NOMINA.GL_DIASPROP, 0);
                        END IF;
                    END IF;
                END IF;
                IF UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'TD' OR UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'PV' OR UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'LN' OR UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'SN' THEN
                    PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL((CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL 
                                         + (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL = PCK_NOMINA.GL_FECHAI THEN 0 ELSE 1 END) END), PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
                    PCK_NOMINA.GL_DIASVAC := TRUNC(15 * PCK_NOMINA.GL_DTV / 360) + PCK_NOMINA.GL_DIASPENDIENTES;
                    PCK_NOMINA.CN(164) := CASE WHEN PCK_NOMINA.GL_DIASVAC > 0 THEN 1 ELSE PCK_NOMINA.GL_DIASVAC END;
                    PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, CASE WHEN PCK_NOMINA.GL_DIASVAC <= 0 THEN 1 ELSE PCK_NOMINA.GL_DIASVAC END);
                    PCK_NOMINA.CN(96) := 0;
                    IF PCK_NOMINA.FC_CN(96) = 0 THEN
                        IF PCK_NOMINA.GL_RTA = 6 THEN
                            PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                            PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                        ELSE
                            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN
                                PCK_NOMINA.CN(96) := (PCK_NOMINA.GL_FECHAFF - (PCK_NOMINA.GL_FECHAFIN + 1));
                            END IF;
                            PCK_NOMINA.GL_FECHAFF1 := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                            PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                            PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL AND PCK_NOMINA.FC_CN(96) = 0 THEN
                                PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_FECHAFIN + 1, PCK_NOMINA.GL_DIASVAC);
                                PCK_NOMINA.CN(96) := (PCK_NOMINA.GL_FECHAFF - (PCK_NOMINA.GL_FECHAFIN + 1));
                                PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                            END IF;
                        END IF;
                    END IF;
                    PCK_NOMINA.CN(96) := CASE WHEN PCK_NOMINA.FC_CN(96) = 1 OR PCK_NOMINA.FC_CN(96) < 1 THEN 0 ELSE PCK_NOMINA.FC_CN(96) END;
                    PCK_NOMINA.GL_DIASVAC := CASE WHEN PCK_NOMINA.GL_DIASVAC = 0 THEN 1 ELSE PCK_NOMINA.GL_DIASVAC END;
                    IF PCK_NOMINA.FC_CN(96) < 0 THEN
                        PCK_NOMINA.CN(96) := 0;
                    END IF;
                    IF PCK_NOMINA.FC_CN(96) = 0 THEN
                        PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                        PCK_NOMINA.CN(96) := TO_NUMBER(PCK_NOMINA.GL_FECHAFF - TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY')) + 1;
                        PCK_NOMINA.CN(96) := CASE WHEN PCK_NOMINA.FC_CN(96) = 1 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO <> PCK_NOMINA.GL_FECHAFF AND PCK_NOMINA.GL_DIASVAC < 1 THEN 0 ELSE PCK_NOMINA.FC_CN(96) END;
                        IF PCK_NOMINA.FC_CN(96) < 0 THEN
                            PCK_NOMINA.CN(96) := 0;
                        END IF;
                    END IF;
                    IF PCK_NOMINA.GL_DTV < 24 AND PCK_NOMINA.FC_CN(96) <= 1 THEN
                        PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_SYSMAN_UTL.FC_ROUND(((15 * PCK_NOMINA.GL_DTV / 360)), 3), 0);
                        PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_ROUND(((15 * PCK_NOMINA.GL_DTV / 360)), 3);
                    ELSE
                        PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(96) / 30, 0) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_SYSMAN_UTL.FC_ROUND(((15 * PCK_NOMINA.GL_DTV / 360)) - PCK_NOMINA.GL_DIASVAC + 0.005, 2), 0);
                        PCK_NOMINA.CN(96)  := PCK_NOMINA.FC_CN(96) + PCK_SYSMAN_UTL.FC_ROUND((15 * PCK_NOMINA.GL_DTV / 360) - PCK_NOMINA.GL_DIASVAC + 0.005, 2);
                    END IF;
                    IF PCK_NOMINA.GL_SPRC = '99' AND (PCK_NOMINA.FC_CN(175) = 0 OR PCK_NOMINA.FC_CN(175) IS NULL) AND PCK_NOMINA.FC_CN(155) > 0 THEN
                        PCK_NOMINA.CN(96) := CASE WHEN PCK_NOMINA.FC_CN(96) IS NULL THEN 21 ELSE PCK_NOMINA.FC_CN(96) END;
                        PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(96) / 30 / 360 * PCK_NOMINA.GL_DTV, 0);
                    END IF;
                END IF;
            END IF;
            PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) < 0 THEN 0 ELSE PCK_NOMINA.FC_CN(175) END;
            PCK_NOMINA.CN(982) := MI_BONPAGADA;
            PCK_NOMINA.CN(987) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORESPV), 0);
        ELSE
            PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0);
            PCK_NOMINA.CN(984) := 0;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAUV), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAUV), 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_DIASPENDIENTES := PCK_NOMINA.FC_CNA(91);
            PCK_NOMINA.CN(93) := PCK_NOMINA.FC_CN(68) * PCK_NOMINA.FC_CN(164) ;
            MI_BONPAGADA := 0;
            IF PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 12 THEN
                PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF PCK_NOMINA.FC_CNA(150) > 0 THEN
                    MI_BONPAGADA := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_SANO - 1), PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, (PCK_NOMINA.GL_SMES - 1), 99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                ELSE
                    MI_BONPAGADA := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                END IF;
            ELSE
                MI_BONPAGADA := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_SANO - 1), PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            END IF;
            IF MI_BONPAGADA = 0 AND PCK_NOMINA.FC_CN(150) > 0 AND PCK_NOMINA.GL_SPER <= 3 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INICIO_DISFRUTE > PCK_NOMINA.GL_FECHAFIN THEN
                MI_BONPAGADA := PCK_NOMINA.FC_CN(150);
            END IF;
            
            --JM CC 4072 (para cuando cumple el mismo mes de pago de vacaciones)
            IF MI_BONPAGADA > 0 AND PCK_NOMINA.FC_CN(150) > 0 AND  MI_BONPAGADA <> PCK_NOMINA.FC_CN(150) AND PCK_NOMINA.GL_SPER <= 3 THEN
                MI_BONPAGADA := PCK_NOMINA.FC_CN(150);
            END IF;
             --JM CC 4072 FIN

            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + (PCK_NOMINA.GL_VPA / 12) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.FC_CN(981) + (MI_BONPAGADA / 12), 0), 0);
            ELSE
                PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + (PCK_NOMINA.GL_VPA / 12) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.FC_CN(981) + (MI_BONPAGADA / 12), 0), 0)   ;
                PCK_NOMINA.GL_FACTORESPV1 := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + (PCK_NOMINA.GL_VPA / 12) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.FC_CN(981) + (MI_BONPAGADA) / 12, 0), 0);
            END IF;
            PCK_NOMINA.CN(982) := PCK_SYSMAN_UTL.FC_ROUND((MI_BONPAGADA) / 12, 0);
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '02' THEN
                IF PCK_NOMINA.FC_CN(403) <> 0 THEN
                    PCK_NOMINA.CN(174) := CASE WHEN PCK_NOMINA.FC_CN(174) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(94), 0) ELSE PCK_NOMINA.FC_CN(174) END;
                END IF;
                IF PCK_NOMINA.FC_CN(419) <> 0 THEN
                    PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(96), 0) ELSE PCK_NOMINA.FC_CN(175) END;
                END IF;
                PCK_NOMINA.CN(155) := CASE WHEN PCK_NOMINA.FC_CN(155) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(68) * PCK_NOMINA.FC_CN(164), 0) ELSE PCK_NOMINA.FC_CN(155) END;
            ELSE
                IF PCK_NOMINA.FC_CN(403) <> 0 THEN
                    PCK_NOMINA.CN(174) := CASE WHEN PCK_NOMINA.FC_CN(174) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(94), 0) ELSE PCK_NOMINA.FC_CN(174) END;
                END IF;
                IF PCK_NOMINA.FC_CN(419) <> 0 THEN
                    PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(96), 0) ELSE PCK_NOMINA.FC_CN(175) END;
                END IF;
            END IF;
        END IF;
    END IF;
    PCK_NOMINA.CN(987) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORESPV), 0);
    PCK_NOMINA.CN(960) := PCK_NOMINA.GL_FACTORESPV ;
    IF PCK_NOMINA.GL_SPRC = '99' THEN
        PCK_NOMINA.CN(961) := PCK_NOMINA.FC_CN(93)     ;
        PCK_NOMINA.CN(962) := PCK_NOMINA.GL_PERIODOS    ;
    ELSE
        PCK_NOMINA.CN(961) := PCK_NOMINA.FC_CN(94)     ;
        PCK_NOMINA.CN(962) := PCK_NOMINA.FC_CN(164)    ;
    END IF;
    PCK_NOMINA.CN(963) := PCK_NOMINA.GL_LICENCIAS  ;
    PCK_NOMINA.CN(964) := PCK_NOMINA.GL_DIASPENDIENTES;
    PCK_NOMINA.CN(965) := PCK_NOMINA.GL_PRIMAPROPORCIONAL;
    PCK_NOMINA.CN(966) := PCK_NOMINA.GL_DINEROPROPORCIONAL;
    PCK_NOMINA.CN(967) := PCK_NOMINA.GL_PENDIENTES;
    PCK_NOMINA.CN(968) := PCK_NOMINA.GL_DIASPROPORCIONAL;
    PCK_NOMINA.CN(975) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
    PCK_NOMINA.CN(976) := 0;
    PCK_NOMINA.CN(977) := PCK_NOMINA.GL_GRPNGV;
    PCK_NOMINA.CN(978) := PCK_NOMINA.GL_AUXT;
    PCK_NOMINA.CN(979) := PCK_NOMINA.GL_AUXA;
    PCK_NOMINA.CN(980) := PCK_NOMINA.GL_VPT;
    PCK_NOMINA.CN(982) := PCK_SYSMAN_UTL.FC_ROUND((MI_BONPAGADA) / 12, 0);
    PCK_NOMINA.CN(983) := 0;
    PCK_NOMINA.CN(984) := PCK_NOMINA.FC_VALORULTIMOQUINQUENIO(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    PCK_NOMINA.CN(987) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORESPV), 0);
    IF PCK_NOMINA.GL_SMES = '12' AND (PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3) AND PCK_NOMINA.FC_CN(155) > 0 AND UPPER(PCK_PARST.FC_PAR('RELIQUIDAR PRIMA DE NAVIDAD EN DICIEMBRE CON VACACIONES', 'NO')) = 'SI' THEN
        PCK_NOMINA.CN(402) := 1;
    END IF;
    PCK_NOMINA.GL_PV_BASE := PCK_NOMINA.GL_FACTORESPV;
    PCK_NOMINA.GL_PV_DIAS := PCK_NOMINA.GL_DIASPROP;
    PCK_NOMINA.GL_VAC_DIAS := PCK_NOMINA.GL_DTV;
END PR_CALCPRIMAVAC_ITTACACIAS;

FUNCTION BUSCAR_PAGOS_DSTOS_OTRO_ID(
    /*
  NAME              : BUSCAR_PAGOS_DSTOS_OTRO_ID
  AUTHOR            : LINA PAOLA VEGA AVELLA
  DATE              : 26/09/2025
  TIME              : 09:35 AM
  SOURCE MODULE     : -
  MODIFIER          : -
  DATE MODIFIED     : -
  TIME              : -
  DESCRIPTION       :FUNCION PARA DEVOLVER LA SUMATORIA DE LOS DEVENGOS O DESCUENTOS DE UN EMPELADO CON DOS ID
  CC: 2484
*/
        UN_COMPANIA       IN VARCHAR2,
        UN_EMPLEADO       IN VARCHAR2,
        UN_CEDULA         IN VARCHAR2,
        UN_ANO            IN VARCHAR2,
        UN_MES            IN VARCHAR2,
        UN_PARAMETRO      IN VARCHAR2
    ) RETURN NUMBER AS
        V_VALORSUMA   NUMBER := 0;
        V_ID_EMPLEADO VARCHAR2(50);
        V_VALORSUMA125 NUMBER := 0;
        V_VALORSUMA110 NUMBER := 0;
        V_RETORNO      NUMBER := 0;

    BEGIN
        IF UN_PARAMETRO = 'P' THEN
            FOR MI_RS IN
            (
                SELECT 
                    HISTORICOS.ANO,
                    HISTORICOS.MES,
                    MAX(PERSONAL.ID_DE_EMPLEADO) AS ID_DE_EMPLEADO,
                    SUM(
                        CASE 
                            WHEN CONCEPTOS.CLASE = 3 
                            THEN HISTORICOS.VALOR 
                            ELSE HISTORICOS.VALOR * -1 
                        END
                    ) AS VALOR_SUMA
                FROM 
                    HISTORICOS
                    INNER JOIN PERIODOS 
                        ON (HISTORICOS.PERIODO = PERIODOS.PERIODO 
                            AND HISTORICOS.MES = PERIODOS.MES 
                            AND HISTORICOS.ANO = PERIODOS.ANO 
                            AND HISTORICOS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO 
                            AND HISTORICOS.COMPANIA = PERIODOS.COMPANIA)
                    INNER JOIN PERSONAL 
                        ON (HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO 
                            AND HISTORICOS.COMPANIA = PERSONAL.COMPANIA)
                    INNER JOIN CONCEPTOS 
                        ON (HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO 
                            AND HISTORICOS.COMPANIA = CONCEPTOS.COMPANIA)
                WHERE 
                    HISTORICOS.COMPANIA = UN_COMPANIA
                    AND HISTORICOS.ID_DE_EMPLEADO <> UN_EMPLEADO
                    AND PERSONAL.NUMERO_DCTO = UN_CEDULA
                    AND (CONCEPTOS.CLASE = 3 OR CONCEPTOS.CLASE = 5)
                    AND CONCEPTOS.FACTOR_RETEFUENTE = -1  
                    AND PERIODOS.ACUMULADO = -1
                GROUP BY 
                    HISTORICOS.ANO, 
                    HISTORICOS.MES
                HAVING 
                    HISTORICOS.ANO = UN_ANO 
                    AND HISTORICOS.MES = UN_MES
            )
            LOOP
                V_RETORNO := MI_RS.VALOR_SUMA;
                V_ID_EMPLEADO := MI_RS.ID_DE_EMPLEADO;
                V_VALORSUMA := MI_RS.VALOR_SUMA;
            
                PCK_NOMINA.GL_ID_DE_EMPLEADO_OTROID := V_ID_EMPLEADO;
                EXIT;
            END LOOP;
    
     ELSIF UN_PARAMETRO = 'D' THEN
        FOR MI_RS IN
            (
                SELECT 
                    HISTORICOS.ANO,
                    HISTORICOS.MES,
                    MAX(PERSONAL.ID_DE_EMPLEADO) AS ID_DE_EMPLEADO,
                    SUM(HISTORICOS.VALOR) AS VALOR_SUMA
                FROM 
                    HISTORICOS
                    INNER JOIN PERIODOS 
                        ON (HISTORICOS.COMPANIA = PERIODOS.COMPANIA 
                            AND HISTORICOS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO 
                            AND HISTORICOS.ANO = PERIODOS.ANO 
                            AND HISTORICOS.MES = PERIODOS.MES 
                            AND HISTORICOS.PERIODO = PERIODOS.PERIODO)
                    INNER JOIN PERSONAL 
                        ON (HISTORICOS.COMPANIA = PERSONAL.COMPANIA 
                            AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO)
                    INNER JOIN CONCEPTOS 
                        ON (HISTORICOS.COMPANIA = CONCEPTOS.COMPANIA 
                            AND HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO)
                WHERE 
                    HISTORICOS.COMPANIA = UN_COMPANIA
                    AND HISTORICOS.ID_DE_EMPLEADO <> UN_EMPLEADO
                    AND PERSONAL.NUMERO_DCTO = UN_CEDULA
                    AND CONCEPTOS.CLASE = 5
                    AND HISTORICOS.ID_DE_CONCEPTO IN ('130', '131', '132', '120')
                    AND PERIODOS.ACUMULADO = -1
                GROUP BY 
                    HISTORICOS.ANO, 
                    HISTORICOS.MES
                HAVING 
                    HISTORICOS.ANO = UN_ANO 
                    AND HISTORICOS.MES = UN_MES
            )
            LOOP
                 V_RETORNO := MI_RS.VALOR_SUMA;
                 V_ID_EMPLEADO := MI_RS.ID_DE_EMPLEADO;
                 V_VALORSUMA := MI_RS.VALOR_SUMA;
                
                 PCK_NOMINA.GL_ID_DE_EMPLEADO_OTROID := V_ID_EMPLEADO;
                 EXIT;
            END LOOP;
    
        ELSIF UN_PARAMETRO = 'R' THEN
       FOR MI_RS IN
        (
            SELECT 
                MAX(PERSONAL.ID_DE_EMPLEADO) AS ID_DE_EMPLEADO,
                SUM(
                    CASE 
                        WHEN HISTORICOS.ID_DE_CONCEPTO = '125' 
                        THEN HISTORICOS.VALOR 
                        ELSE 0 
                    END
                ) AS VALOR_SUMA_125,
                SUM(
                    CASE 
                        WHEN HISTORICOS.ID_DE_CONCEPTO = '110' 
                        THEN HISTORICOS.VALOR 
                        ELSE 0 
                    END
                ) AS VALOR_SUMA_110
            FROM 
                HISTORICOS
                INNER JOIN PERIODOS 
                    ON (HISTORICOS.PERIODO = PERIODOS.PERIODO 
                        AND HISTORICOS.MES = PERIODOS.MES 
                        AND HISTORICOS.ANO = PERIODOS.ANO 
                        AND HISTORICOS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO 
                        AND HISTORICOS.COMPANIA = PERIODOS.COMPANIA)
                INNER JOIN PERSONAL 
                    ON (HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO 
                        AND HISTORICOS.COMPANIA = PERSONAL.COMPANIA)
                INNER JOIN CONCEPTOS 
                    ON (HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO 
                        AND HISTORICOS.COMPANIA = CONCEPTOS.COMPANIA)
            WHERE 
                HISTORICOS.COMPANIA = UN_COMPANIA
                AND HISTORICOS.ID_DE_EMPLEADO <> UN_EMPLEADO
                AND PERSONAL.NUMERO_DCTO = UN_CEDULA
                AND HISTORICOS.ID_DE_CONCEPTO IN ('110', '125')
                AND PERIODOS.ACUMULADO = -1
            GROUP BY 
                HISTORICOS.ANO, 
                HISTORICOS.MES
            HAVING 
                HISTORICOS.ANO = UN_ANO 
                AND HISTORICOS.MES = UN_MES
        )
        LOOP
            V_RETORNO := MI_RS.VALOR_SUMA_125;
            V_ID_EMPLEADO := MI_RS.ID_DE_EMPLEADO;
            V_VALORSUMA125 := MI_RS.VALOR_SUMA_125;
            V_VALORSUMA110 := MI_RS.VALOR_SUMA_110;
            
            PCK_NOMINA.GL_ID_DE_EMPLEADO_OTROID := V_ID_EMPLEADO;
            PCK_NOMINA.GL_RETEFUENTE_OTROID := V_VALORSUMA125;
            PCK_NOMINA.GL_CN110_BASE_OTROID := V_VALORSUMA110;
            
            EXIT;
        END LOOP;
    END IF;
        RETURN V_RETORNO;

END BUSCAR_PAGOS_DSTOS_OTRO_ID;

PROCEDURE PR_CALCPRIMASEMEST_ESPFUNZA(
/*
NAME              : PR_CALCPRIMASEMEST_ESPFUNZA -> EN ACCESS calcularprimasemestralESPFUNZA
AUTHORS           : MARIA ALEJANDRA PEREZ SALAZAR
DATE MIGRADOR     : 03/09/2025
DESCRIPTION       : PROCEDIMIENTO CALCULO DE LA PRIMA DE JUNIO
@NAME:  CALCULARPRIMASEMESTRALESPFUNZA
*/
    UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA)
AS
    MI_DOCEAVASMINIMASPS PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_PSPAGADA          PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_MSG               PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_INDRETIR          PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_MESANTERIOR       PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
BEGIN
    MI_DOCEAVASMINIMASPS := TO_NUMBER(PCK_PARST.FC_PAR('DOCEAVAS MINIMAS PRIMA SERVICIOS', '1'));
    PCK_NOMINA.GL_DOCEAVAS := 0;
    PCK_NOMINA.GL_FACTORPS := 0;
    PCK_NOMINA.GL_FACTORPS1 := 0;
    PCK_NOMINA.GL_DNT := 0;
    
    IF PCK_NOMINA.GL_SMES <= 7 THEN
        PCK_NOMINA.GL_FECHAIPS := (CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('16/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') ELSE TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') END);
        PCK_NOMINA.GL_FECHAIPS := (CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END);
    END IF;
    IF PCK_NOMINA.GL_SMES > 7 THEN
        PCK_NOMINA.GL_FECHAIPS := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        PCK_NOMINA.GL_FECHAIPS := (CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END);
        PCK_NOMINA.GL_FECHAIPS1 := TO_DATE('01/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');        
    END IF;
    IF (PCK_NOMINA.GL_SMES = 7 AND PCK_NOMINA.GL_SPER = 4) AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO >= PCK_NOMINA.GL_FECHAINI AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO <= PCK_NOMINA.GL_FECHAFIN) THEN
        PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0);
    ELSE
        PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0);
    END IF;
    IF PCK_NOMINA.FC_CN(946) = 0 AND PCK_NOMINA.FC_CN(514) <> 0 THEN
        PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(514) / 12, 0);
    END IF;
    IF PCK_NOMINA.GL_SMES = 7 THEN
        PCK_NOMINA.GL_FECHAFPS := (CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN (CASE WHEN PCK_NOMINA.GL_FECHAFIN1 > TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') ELSE PCK_NOMINA.GL_FECHAFIN1 END) ELSE
        TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END);
    ELSE
        PCK_NOMINA.GL_FECHAFPS := (CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN PCK_NOMINA.GL_FECHAFIN1 ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END);
    END IF;
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS),(CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPS) < 16 THEN 1 ELSE 2 END), PCK_NOMINA.GL_SANO, 6, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
    PCK_NOMINA.GL_PVAC := 0;
    --GROJAS CC_3933: Se retira validacion 404 para relizarla por separado
    IF PCK_NOMINA.GL_SMES >= 7 THEN
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO), 6,  1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.CN(947) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_SUMACONA(45, 60) + PCK_NOMINA.FC_SUMACON(45, 60) + PCK_NOMINA.FC_CN(511) + PCK_NOMINA.FC_CN(523) + PCK_NOMINA.FC_CN(528) + PCK_NOMINA.FC_CNA(511) + PCK_NOMINA.FC_CNA(523) + PCK_NOMINA.FC_CNA(528)) / 6, 0);
    ELSE
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO - 1), 12,  1, PCK_NOMINA.GL_SANO, 5, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.CN(947) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_SUMACONA(45, 60) + PCK_NOMINA.FC_SUMACON(45, 60) + PCK_NOMINA.FC_CN(511) + PCK_NOMINA.FC_CN(523) + PCK_NOMINA.FC_CN(528) + PCK_NOMINA.FC_CNA(511) + PCK_NOMINA.FC_CNA(523) + PCK_NOMINA.FC_CNA(528)) / 6, 0);
    END IF;
    --GROJAS: Se agrega la validación para cuando tiene 404
    IF PCK_NOMINA.FC_CN(404) <> 0  THEN
        IF PCK_NOMINA.GL_SMES <= 6 THEN
        SELECT PCK_SYSMAN_UTL.FC_MES(ADD_MONTHS(TO_DATE('01-'||PCK_NOMINA.GL_SMES||'-'||PCK_NOMINA.GL_SANO,'DD-MM-YYYY'), -5)) INTO MI_MESANTERIOR FROM DUAL;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO - 1 ), MI_MESANTERIOR ,  1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        ELSE
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO), 6,  1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        END IF;
        PCK_NOMINA.CN(947) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_SUMACONA(45, 60) + PCK_NOMINA.FC_SUMACON(45, 60) + PCK_NOMINA.FC_CN(511) + PCK_NOMINA.FC_CN(523) + PCK_NOMINA.FC_CN(528) + PCK_NOMINA.FC_CNA(511) + PCK_NOMINA.FC_CNA(523) + PCK_NOMINA.FC_CNA(528)) / 6, 0);
    END IF;
    
    PCK_NOMINA.CN(980) := 0;
    PCK_NOMINA.GL_FACTORPS := PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + PCK_NOMINA.FC_CN(947) + PCK_NOMINA.FC_CN(946), 0);
    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
    PCK_NOMINA.CN(953) := PCK_NOMINA.GL_DNT;
    
    IF PCK_PARST.FC_PAR('ELIMINAR TIEMPO MINIMO EN PRIMA DE SERVICIOS', ' ') = 'SI' THEN
        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.FC_CN(953);
        MI_DOCEAVASMINIMASPS := NVL(TO_NUMBER(PCK_PARST.FC_PAR('DOCEAVAS MINIMAS PRIMA SERVICIOS', '1')),1);
        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS - PCK_NOMINA.FC_CN(953));
    ELSE        
        IF (PCK_NOMINA.GL_DCC = 179) THEN
            PCK_NOMINA.GL_DOCEAVAS := 6;
            PCK_NOMINA.GL_DCC := 180;            
        ELSE
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
        END IF;
        IF PCK_NOMINA.FC_CN(952) > 0 THEN
            PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
        END IF;
        IF PCK_NOMINA.GL_SPRC = 99 THEN
            IF PCK_NOMINA.FC_CN(952)  > 0 THEN
                PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
                PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.GL_DCC / 30);
            END IF;
        END IF;
    END IF;

    IF PCK_NOMINA.GL_FECHAIPS  >= TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAIPS <= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') AND PCK_NOMINA.GL_SPER <> 7 THEN
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
            PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);  
        ELSE
            PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 6 * PCK_NOMINA.GL_DOCEAVAS / 30 * 15 + 0.5, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);    
        END IF;
    ELSIF PCK_NOMINA.GL_FECHAIPS  >= TO_DATE('01/01/' || (PCK_NOMINA.GL_SANO), 'DD/MM/YYYY') AND PCK_NOMINA.FC_CN(404) = 0 THEN
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
            PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);  
            IF (PCK_NOMINA.GL_FECHAIPS > PCK_NOMINA.GL_FECHAFPS) AND PCK_NOMINA.GL_SMES = 7 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO >= TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                PCK_NOMINA.CN(160) := 0;
            END IF;
        ELSE
            PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 6 * PCK_NOMINA.GL_DOCEAVAS / 30 * 15 + 0.5, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
        END IF;
    ELSIF PCK_NOMINA.FC_CN(404) = 1 THEN  
        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAR);
        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
            PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);  
        ELSE 
            PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 2 / 6 * PCK_NOMINA.GL_DOCEAVAS + 0.5, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
        END IF;   
    END IF;    
    PCK_NOMINA.GL_PRIMAJUN := PCK_NOMINA.FC_CN(160);
    MI_INDRETIR := PCK_NOMINA.FC_CN(404);
    IF PCK_NOMINA.GL_SPER = 4 THEN
        FOR i IN 2..699 LOOP
            IF (i <> 125) AND (i <> 303) AND (i <> 301) AND (i <> 300) AND (i < 599) OR (i >= 600 AND i <= 698) AND (PCK_NOMINA.FC_CN(i) > 0 AND PCK_NOMINA.FC_CN(i) < 1) THEN
                PCK_NOMINA.CN(i) := 0;
            END IF;
        END LOOP;
    END IF;
    PCK_NOMINA.CN(404) := MI_INDRETIR;
    PCK_NOMINA.CN(67) := PCK_NOMINA.GL_DOCEAVAS;
    PCK_NOMINA.CN(160) := PCK_NOMINA.GL_PRIMAJUN;
    PCK_NOMINA.CN(945) := (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END);
    PCK_NOMINA.CN(948) := PCK_NOMINA.GL_AUXT;
    PCK_NOMINA.CN(949) := PCK_NOMINA.GL_AUXA;
    PCK_NOMINA.CN(950) := PCK_NOMINA.GL_PVAC ;
    PCK_NOMINA.CN(951) := PCK_NOMINA.FC_CN(67);
    PCK_NOMINA.CN(952) := PCK_NOMINA.GL_DCC;
    PCK_NOMINA.CN(953) := PCK_NOMINA.GL_DNT;   
    PCK_NOMINA.CN(985) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORPS), 0);
    PCK_NOMINA.GL_PS_BASE := PCK_NOMINA.GL_FACTORPS;
    PCK_NOMINA.GL_PS_DIAS := PCK_NOMINA.GL_DCC;
END PR_CALCPRIMASEMEST_ESPFUNZA;

PROCEDURE PR_CALCRPRIMNAVIDAD_ESPFUNZA(
    /*
    NAME              : PR_CALCRPRIMNAVIDAD_ESPFUNZA -> EN ACCESS 
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MIGRADOR     : 03/09/2025
    DESCRIPTION       : PROCEDIMIENTO CALCULO DE LA PRIMA DE NAVIDAD
    @NAME:  CALCULARPRIMADENAVIDADESPFUNZA
*/
    UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA
)
AS
    MI_FECHAFPN         DATE;
    MI_TRANSPORTELEGAL  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_RETEFUENTE       PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_FACTORSUELDO     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_EXTRASJUNNOV     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_BONPAGADA        PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_EXTRAS           PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_MESANTERIOR      PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
BEGIN
    PCK_NOMINA.GL_PVAC := 0;
    MI_EXTRASJUNNOV := 0;
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 6, 1, PCK_NOMINA.GL_SANO, 11, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    IF PCK_NOMINA.FC_CNA(70) <> 0 THEN
        MI_EXTRASJUNNOV := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(70) + PCK_NOMINA.FC_CNA(507) + PCK_NOMINA.FC_CNA(508) + PCK_NOMINA.FC_CNA(511) +
          PCK_NOMINA.FC_CNA(518) + PCK_NOMINA.FC_CNA(519) + PCK_NOMINA.FC_CNA(520) + PCK_NOMINA.FC_CNA(521) + PCK_NOMINA.FC_CNA(522) + PCK_NOMINA.FC_CNA(523) + 
            PCK_NOMINA.FC_CNA(528) + PCK_NOMINA.FC_CN(70)) / 6, 0);                  
    END IF;
    IF NOT(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '10' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '11') THEN
        PCK_NOMINA.GL_FACTORPN := 0;
        PCK_NOMINA.GL_DNT := 0;
        
        PCK_NOMINA.GL_FECHAIPN := TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        PCK_NOMINA.GL_FECHAIPN := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPN THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPN END;
        PCK_NOMINA.GL_FECHAIPN1 := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        PCK_NOMINA.GL_FECHAIPN1 := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPN1 THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPN1 END;
        
        IF PCK_NOMINA.GL_SMES = 12 AND (PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3) THEN
            PCK_NOMINA.GL_FECHAIPN := TO_DATE('01/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            PCK_NOMINA.GL_FECHAIPN1 := TO_DATE('01/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        END IF;
        IF PCK_NOMINA.FC_CN(404) = 0 AND PCK_NOMINA.GL_FECHAI <= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
            MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_PVAC := 0;
            PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0);
            PCK_NOMINA.CN(942) := MI_EXTRASJUNNOV;
            PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0);
            PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
            PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0) ;
            PCK_NOMINA.GL_FACTORPN := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.FC_CN(942) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(931) + (PCK_NOMINA.GL_PVAC / 12) + PCK_NOMINA.FC_CN(939);            
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938) + (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            IF PCK_NOMINA.GL_FECHAR IS NOT NULL THEN
                IF PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
                END IF;
            END IF;
            PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);
            IF PCK_NOMINA.FC_CN(937) > 0 THEN
                PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937)/30);
            END IF;
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN 
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC, 0);
                END IF;
                IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(404) <> 0 THEN
                    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 12, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                    PCK_NOMINA.CN(504) := CASE WHEN PCK_NOMINA.FC_CN(504) = 0 THEN PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158) ELSE PCK_NOMINA.FC_CN(504) END;
                    PCK_NOMINA.CN(158) := 0;
                END IF;
            ELSE
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                END IF;
            END IF;
        ELSIF PCK_NOMINA.FC_CN(404) <> 0 THEN
            MI_FECHAFPN := PCK_NOMINA.GL_FECHAR;
            IF PCK_NOMINA.GL_SMES <= 6 THEN
                SELECT PCK_SYSMAN_UTL.FC_MES(ADD_MONTHS(TO_DATE('01-'||PCK_NOMINA.GL_SMES||'-'||PCK_NOMINA.GL_SANO,'DD-MM-YYYY'), -5)) INTO MI_MESANTERIOR FROM DUAL;
                    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO - 1 ), MI_MESANTERIOR ,  1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                ELSE
                    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO), 6,  1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                END IF;
            MI_EXTRAS := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_SUMACONA(45, 60) + PCK_NOMINA.FC_SUMACON(45, 60) + PCK_NOMINA.FC_CN(511) + PCK_NOMINA.FC_CN(523) + PCK_NOMINA.FC_CN(528) + PCK_NOMINA.FC_CNA(511) + PCK_NOMINA.FC_CNA(523) + PCK_NOMINA.FC_CNA(528)) / 6, 0);
            MI_BONPAGADA := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.CN(942) := MI_EXTRAS;
            IF PCK_NOMINA.FC_CNA(164) > 1 THEN
                PCK_NOMINA.GL_PVAC := ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0) / PCK_NOMINA.FC_CNA(164);
            ELSE
                IF ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0) > 0 THEN
                    PCK_NOMINA.GL_PVAC := ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0);
                ELSE
                    PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CN(155);
                END IF;
            END IF;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO - 1), PCK_NOMINA.GL_SMES,  1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0);
            IF PCK_NOMINA.FC_CN(931) = 0 AND PCK_NOMINA.FC_CN(160) > 0  THEN
                PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(160) / 12, 0);
            END IF;
            PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
            IF PCK_NOMINA.FC_CN(150) > 0  AND PCK_NOMINA.GL_SPRC <> 99 THEN
                PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(150)) / 12, 0);
            ELSIF PCK_NOMINA.FC_CNA(150) > 0 THEN 
                PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514)) / 12, 0);
            END IF;        
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938) + (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            PCK_NOMINA.GL_FACTORPN := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.FC_CN(942) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(931) + (PCK_NOMINA.GL_PVAC / 12) + (MI_BONPAGADA / 12);            
            IF PCK_NOMINA.FC_CN(937) > 0 THEN
                PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937) / 30);
            END IF;
            PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC, 0);
                END IF;
                IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(404) <> 0 THEN
                    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 12, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                    PCK_NOMINA.CN(504) := CASE WHEN PCK_NOMINA.FC_CN(504) = 0 THEN PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158) ELSE PCK_NOMINA.FC_CN(504) END;
                    PCK_NOMINA.CN(158) := 0;
                END IF;
            ELSE
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                END IF;
                IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.GL_SPER = 7 THEN
                    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 12, 4, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                    PCK_NOMINA.CN(158) := PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158);
                END IF;
            END IF;
        ELSIF PCK_NOMINA.GL_FECHAI > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAI <= TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
            MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            IF NVL(PCK_NOMINA.GL_FECHAR, '') <> '' THEN
                IF PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
                END IF;
            END IF;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, 11, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF PCK_NOMINA.FC_CNA(164) > 1 THEN
                PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0) / PCK_NOMINA.FC_CNA(164);
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0);
            END IF;
            PCK_NOMINA.CN(942) := MI_EXTRASJUNNOV;
            PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0) / 12 ;
            PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
            PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0) / 12 ;
            PCK_NOMINA.GL_FACTORPN := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(931) + (PCK_NOMINA.GL_PVAC / 12) + PCK_NOMINA.FC_CN(939);
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
            FOR i IN 1..PCK_NOMINA.GL_SMES 
            LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938) + (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            IF PCK_NOMINA.FC_CN(937) > 0 THEN
                PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937) / 30);
            END IF;
            PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC, 0);
                END IF;
                IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(404) <> 0 THEN
                    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 12, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                    PCK_NOMINA.CN(504) := CASE WHEN PCK_NOMINA.FC_CN(504) = 0 THEN PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158) ELSE PCK_NOMINA.FC_CN(504) END;
                    PCK_NOMINA.CN(158) := 0;
                END IF;
            ELSE
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                END IF;
            END IF;
        ELSIF PCK_NOMINA.GL_FECHAI > TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
            MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF PCK_NOMINA.FC_CNA(155) = 0 THEN
                PCK_NOMINA.GL_PVAC := 0;
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155);
            END IF;
            IF PCK_NOMINA.CPARENTRADA(1).NIT = '899999419-1' AND PCK_NOMINA.FC_CN(404) <> 0 AND PCK_NOMINA.GL_SPRC = 1 THEN
                PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0);
            END IF;
            PCK_NOMINA.CN(942) := MI_EXTRASJUNNOV ;
            PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0) / 12 ;
            PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
            PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0) / 12 ;
            PCK_NOMINA.GL_FACTORPN := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(931) + (PCK_NOMINA.GL_PVAC / 12) + PCK_NOMINA.FC_CN(939);
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
            FOR i IN 1..PCK_NOMINA.GL_SMES 
            LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938) + (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            IF PCK_NOMINA.FC_CN(937) > 0 THEN
                PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937) / 30);
            END IF;
            PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC, 0);
                END IF;
                IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(404) <> 0 THEN
                    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 12, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                    PCK_NOMINA.CN(504) := CASE WHEN PCK_NOMINA.FC_CN(504) = 0 THEN PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158) ELSE PCK_NOMINA.FC_CN(504) END;
                    PCK_NOMINA.CN(158) := 0;
                END IF;
            ELSE
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                END IF;
            END IF;
        END IF;
    END IF;
    PCK_NOMINA.GL_PRIMADIC := PCK_NOMINA.FC_CN(158);
    MI_TRANSPORTELEGAL := PCK_NOMINA.FC_CN(254);
    MI_RETEFUENTE := PCK_NOMINA.FC_CN(125);
    IF PCK_NOMINA.GL_SPER = 4 THEN
        FOR i IN 2..599 LOOP
            IF (i <> 125) AND (i < 599) AND (i <> 303) AND (i <> 301) AND (i <> 10) AND (i <> 300) OR (i >= 600 AND i <= 698) AND (PCK_NOMINA.FC_CN(i) > 0 AND PCK_NOMINA.FC_CN(i) < 1) THEN
                PCK_NOMINA.CN(i) := 0;
            END IF;
        END LOOP;
    END IF;
    PCK_NOMINA.CN(80)  := 0;
    PCK_NOMINA.CN(125) := MI_RETEFUENTE;
    PCK_NOMINA.CN(254) := MI_TRANSPORTELEGAL;
    PCK_NOMINA.CN(158) := PCK_NOMINA.GL_PRIMADIC;
    PCK_NOMINA.CN(67)  := PCK_NOMINA.GL_DOCEAVAS;
    PCK_NOMINA.CN(930) := CASE WHEN PCK_NOMINA.FC_CN(11) >= 30 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
    PCK_NOMINA.CN(933) := PCK_NOMINA.GL_AUXT ;
    PCK_NOMINA.CN(934) := PCK_NOMINA.GL_AUXA;
    PCK_NOMINA.CN(936) := PCK_NOMINA.FC_CN(67) ;
    PCK_NOMINA.CN(937) := PCK_NOMINA.GL_DCC;
    PCK_NOMINA.CN(935) := PCK_NOMINA.GL_GRPNGV;
    PCK_NOMINA.CN(940) := PCK_NOMINA.GL_VPT;
    PCK_NOMINA.CN(159) := 0;
    PCK_NOMINA.CN(988) := PCK_NOMINA.GL_FACTORPN;
END PR_CALCRPRIMNAVIDAD_ESPFUNZA;

PROCEDURE PR_CALCPRIMAVACACIO_ESPFUNZA
/*
NAME              : PR_CALCPRIMAVACACIO_ESPFUNZA -> EN ACCESS 
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
DATE MIGRADOR     : 03/09/2025
DESCRIPTION       : PROCEDIMIENTO CALCULO DE LA PRIMA DE VACACIONES
@NAME:  CALCULARPRIMADEVACACIONESESPUNZA
*/
AS
    MI_BONPAGADA     PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_FECHA         DATE;
    MI_VALOR         NUMBER DEFAULT 0;
BEGIN
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '10' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '11' THEN
        PCK_NOMINA.CN(174) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + (PCK_NOMINA.FC_CN(160) / 12), 0) / 2;
    ELSE
        PCK_NOMINA.GL_DIASVAC := 0;
        PCK_NOMINA.GL_DIASPENDIENTES := 0;
        PCK_NOMINA.GL_PENDIENTES := 0;
        PCK_NOMINA.GL_LICENCIAS := 0;
        PCK_NOMINA.GL_PRIMAPROPORCIONAL := 0;
        PCK_NOMINA.GL_DINEROPROPORCIONAL := 0;
        PCK_NOMINA.GL_FECHAUV := CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL END;
        IF PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.GL_SPER = 8 THEN
            PCK_NOMINA.CN(984) := 0;
            IF PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) <> 0 THEN
                --GROJAS: Se retira el parametro ultima prima pagada Y se calcula la bonificación pagada
                PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0);
                MI_BONPAGADA := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF PCK_NOMINA.FC_CN(160) > PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) AND PCK_NOMINA.FC_CN(404) <> 0 AND (PCK_NOMINA.GL_SMES = 7 OR PCK_NOMINA.GL_SMES = 6) AND PCK_NOMINA.GL_SANO >= 2022 THEN
                    PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160) / 12), 0);
                END IF;
                IF (PCK_NOMINA.GL_SMES = 7 OR PCK_NOMINA.GL_SMES = 6) AND (PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) <> 0 AND PCK_NOMINA.FC_CN(160) <> 0) AND PCK_NOMINA.FC_CN(404) <> 0 THEN
                    PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA, 160, PCK_NOMINA.GL_SANO, 6, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                    IF PCK_NOMINA.FC_CN(160) = 0 AND PCK_NOMINA.FC_CN(160) > 0 THEN
                        PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160)) / 12, 0);
                    ELSIF PCK_NOMINA.FC_CNP(160) > 0 AND PCK_NOMINA.FC_CN(160) > 0 THEN
                        PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNP(160) + PCK_NOMINA.FC_CN(160)) / 12, 0);
                    END IF;
                    IF PCK_NOMINA.FC_CN(981) = 0 THEN
                        PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + (CASE WHEN PCK_PARST.FC_PAR('USAR ULTIMA PRIMA SEMESTRAL PAGADA EN LIQUIDACION FINAL', 'SI') = 'SI' THEN PCK_NOMINA.FC_CN(160) ELSE 0 END)) / 12, 0);
                    END IF;
                END IF;
            ELSE
                PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160)) / 12, 0);
            END IF;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAUV), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAUV), 1, (PCK_NOMINA.GL_SANO), PCK_NOMINA.GL_SMES, 99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_LICENCIAS := NVL(((PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(339) + PCK_NOMINA.FC_CNA(339)) 
                                       + (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.FC_CESANTIA(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 'L') ELSE 0 END) 
                                       + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION),0);
            PCK_NOMINA.GL_DIASPENDIENTES := PCK_NOMINA.FC_CNA(91);
            PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL((CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI 
                                    ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1 END), PCK_NOMINA.GL_FECHAFIN1);
            PCK_NOMINA.GL_DIASPROP := PCK_NOMINA.GL_DTV - PCK_NOMINA.GL_LICENCIAS;
            PCK_NOMINA.GL_PERIODOS := TRUNC(PCK_NOMINA.GL_DTV / 360);
            IF (PCK_NOMINA.GL_DTV - (360 * PCK_NOMINA.GL_PERIODOS)) >= 315 THEN
                PCK_NOMINA.GL_PERIODOS := PCK_NOMINA.GL_PERIODOS + 1;
            END IF;
            PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 * PCK_NOMINA.GL_PERIODOS, 2);
            PCK_NOMINA.CN(93) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(68) * CASE WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN 1 ELSE PCK_NOMINA.GL_PERIODOS END, 2);
            IF PCK_NOMINA.GL_DIASVAC = 0 THEN
                PCK_NOMINA.GL_PERIODOS := CASE WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN 1 ELSE PCK_NOMINA.GL_PERIODOS END;
                PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 * CASE WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN 1 ELSE PCK_NOMINA.GL_PERIODOS END / 360 * PCK_NOMINA.GL_DTV, 0);
            END IF;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_ANOA), PCK_NOMINA.GL_MESA, PCK_NOMINA.GL_PERA, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF PCK_NOMINA.GL_DTV = 0 THEN
                PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(981) + (MI_BONPAGADA / 12), 0);
            ELSE
                PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(981) + (MI_BONPAGADA / 12), 0);
            END IF;
            PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL((CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI 
                                    ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1 END), PCK_NOMINA.GL_FECHAFIN1);
            PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 * CASE WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN 1 ELSE PCK_NOMINA.GL_PERIODOS END / 360 * PCK_NOMINA.GL_DTV, 0);
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                PCK_NOMINA.CN(155) := CASE WHEN PCK_NOMINA.FC_CN(155) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CNAN(10) <> 0 THEN PCK_NOMINA.FC_CNAN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.FC_CN(981) + (MI_BONPAGADA/12)) * 15 / 30 / 360 * PCK_NOMINA.GL_DTV, 0) ELSE PCK_NOMINA.FC_CN(155) END;
            ELSE
                PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CNAN(10) <> 0 THEN PCK_NOMINA.FC_CNAN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA, 0);
                PCK_NOMINA.CN(155) := CASE WHEN PCK_NOMINA.FC_CN(155) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CNAN(10) <> 0 THEN PCK_NOMINA.FC_CNAN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA, 0) / 30 * 15 / 360 * PCK_NOMINA.GL_DTV, 0) ELSE PCK_NOMINA.FC_CN(155) END;
            END IF;
            IF PCK_NOMINA.FC_CN(175) = 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '01' THEN
                PCK_NOMINA.GL_DTV := PCK_NOMINA.GL_DTV / 30 * 15 / 360;
                IF PCK_NOMINA.GL_SPRC = 99 THEN
                    PCK_NOMINA.GL_RTA := 7;
                END IF;
                PCK_NOMINA.GL_FECHAFF := '';
                PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, TO_DATE(TO_CHAR(CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END, 'DD/MM/YYYY'), 'DD/MM/YYYY') - 1, NVL(PCK_NOMINA.GL_DIASVAC, 1)); 
                IF PCK_NOMINA.GL_SPER = 8 AND PCK_NOMINA.GL_SMES = 12 THEN
                    PCK_NOMINA.GL_PERIODOS := 1;
                    PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1 END, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
                    PCK_NOMINA.CN(93) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(68) * PCK_NOMINA.GL_PERIODOS, 2);
                    PCK_NOMINA.GL_DIASVAC := CASE WHEN PCK_NOMINA.GL_DIASVAC = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(15 * PCK_NOMINA.GL_PERIODOS, 2) ELSE PCK_NOMINA.GL_DIASVAC END;
                    PCK_NOMINA.GL_DIASVAC := TRUNC(PCK_NOMINA.GL_DIASVAC / 360 * PCK_NOMINA.GL_DTV);
                    PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_FECHAFIN + 1, PCK_NOMINA.GL_DIASVAC);
                END IF;
                IF PCK_NOMINA.FC_CN(96) = 0 THEN
                    IF PCK_NOMINA.GL_RTA = 6 THEN
                        PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END, PCK_NOMINA.GL_DIASVAC);
                        PCK_NOMINA.CN(96) := TO_NUMBER(TO_DATE(TO_CHAR(CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END, 'DD/MM/YYYY'), 'DD/MM/YYYY') - PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END, PCK_NOMINA.GL_DIASVAC, 0)) + 1;
                        PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END, PCK_NOMINA.GL_DIASVAC));
                    ELSE
                        PCK_NOMINA.GL_FECHAFF1 := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END, PCK_NOMINA.GL_DIASVAC);
                        PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END, PCK_NOMINA.GL_DIASVAC);
                        IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL = PCK_NOMINA.GL_FECHAI THEN 0 ELSE 1 END) END, PCK_NOMINA.GL_FECHAFIN1) > 315 THEN
                            PCK_NOMINA.CN(96) := TO_NUMBER(TO_DATE(TO_CHAR(CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END, 'DD/MM/YYYY'), 'DD/MM/YYYY') - PCK_NOMINA.GL_FECHAFF1) + 1;
                            PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL((CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END), PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, (CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END), PCK_NOMINA.GL_DIASVAC));
                        END IF;
                        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN
                            PCK_NOMINA.CN(96) := (PCK_NOMINA.GL_FECHAFF - (PCK_NOMINA.GL_FECHAFIN + 1));
                            PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                            IF PCK_NOMINA.FC_CN(96) = 0 AND PCK_NOMINA.GL_DIASVAC = 1 THEN
                                PCK_NOMINA.CN(96) := 1;
                            END IF;
                            IF PCK_NOMINA.FC_CN(96) < 0 AND PCK_NOMINA.GL_DIASVAC <> 0 THEN
                                PCK_NOMINA.CN(96) := 0;
                            END IF;
                        END IF;
                    END IF;
                END IF;
                IF PCK_NOMINA.FC_CN(96) = 0 THEN
                    PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END, PCK_NOMINA.GL_DIASVAC);
                    PCK_NOMINA.CN(96) := PCK_NOMINA.GL_FECHAFF - (CASE WHEN PCK_NOMINA.GL_SPRC = 99 THEN PCK_NOMINA.GL_FECHAFIN1 ELSE TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'),'DD/MM/YYYY') END);
                    IF TRUNC(PCK_NOMINA.GL_DIASVAC) = 1 THEN
                        PCK_NOMINA.CN(96) := TRUNC(PCK_NOMINA.GL_DIASVAC);
                    END IF;
                    PCK_NOMINA.CN(96) := PCK_NOMINA.FC_CN(96) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_DIASVAC - TRUNC(PCK_NOMINA.GL_DIASVAC), 2);
                END IF;
                PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 * PCK_NOMINA.GL_PERIODOS, 2);
                PCK_NOMINA.CN(164) := PCK_NOMINA.GL_PERIODOS;
                PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL((CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI 
                                    ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1 END), PCK_NOMINA.GL_FECHAFIN1);
                PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(96) / 30, 0);
                IF PCK_NOMINA.GL_SPRC = '99' THEN
                    PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175)    ;
                ELSE
                    PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175) + PCK_NOMINA.GL_PENDIENTES;
                END IF;
                IF PCK_NOMINA.GL_SPER = 8 AND PCK_NOMINA.GL_SMES = 12 THEN
                    PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543)) / 12, 0);
                    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                        PCK_NOMINA.CN(155) := PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CNA(10) <> 0 THEN PCK_NOMINA.FC_CNA(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543)) / 12 + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(538)) / 12) * PCK_NOMINA.FC_CN(93) / 30 / 360 * PCK_NOMINA.GL_DTV, 0);
                    ELSE
                        PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CNA(10) <> 0 THEN PCK_NOMINA.FC_CNA(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543)) / 12 + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(543)) / 12, 0);
                        PCK_NOMINA.CN(155) := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CNA(10) <> 0 THEN PCK_NOMINA.FC_CNA(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543)) / 12 + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(543)) / 12, 0) * PCK_NOMINA.FC_CN(93) / 30 / 360 * PCK_NOMINA.GL_DTV, 0);
                    END IF;
                    PCK_NOMINA.CN(964) := PCK_NOMINA.GL_DIASPENDIENTES;
                    PCK_NOMINA.GL_DIASPROPORCIONAL := PCK_NOMINA.GL_DTV;
                END IF;
                PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) < 0 THEN 0 ELSE PCK_NOMINA.FC_CN(175) END;
            ELSIF PCK_NOMINA.FC_CN(175) = 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                PCK_NOMINA.GL_DTV := PCK_NOMINA.GL_DTV / 30 * 15 / 360;
                IF PCK_NOMINA.GL_SPRC = 99 THEN
                    PCK_NOMINA.GL_RTA := 7;
                END IF;
                PCK_NOMINA.GL_FECHAFF := '';
                PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL((CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI 
                                    ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1 END), PCK_NOMINA.GL_FECHAFIN1);
                PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 * PCK_NOMINA.GL_DTV / 360, 0);
                PCK_NOMINA.CN(164) := CASE WHEN PCK_NOMINA.GL_DIASVAC > 0 THEN 1 ELSE PCK_NOMINA.GL_DIASVAC END;
                PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                IF PCK_NOMINA.FC_CN(96) = 0 THEN
                    IF PCK_NOMINA.GL_RTA = 6 THEN
                        PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                        PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                    ELSE
                        PCK_NOMINA.GL_FECHAFF1 := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                        PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                        PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                    END IF;
                END IF;
                IF PCK_NOMINA.FC_CN(96) = 0 THEN
                    PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                    PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                END IF;
                IF PCK_NOMINA.FC_CN(404) <> 0 THEN
                    PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * (15 * PCK_NOMINA.GL_DTV / 360) / 30, 0);
                ELSE
                    PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(96) / 30, 0);
                END IF;
                IF PCK_NOMINA.GL_SPRC = '99' THEN
                    PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175)    ;
                ELSE
                    PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175) + PCK_NOMINA.GL_PENDIENTES;
                END IF;
                PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) < 0 THEN 0 ELSE PCK_NOMINA.FC_CN(175) END;
            END IF;
            IF (PCK_NOMINA.GL_SPER = 5 OR PCK_NOMINA.GL_SPER = 7) OR PCK_NOMINA.FC_CN(404) <> 0 THEN
                PCK_NOMINA.CN(68) := 0;
                PCK_NOMINA.CN(93) := CASE WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN 1 ELSE PCK_NOMINA.GL_PERIODOS END;
                IF PCK_NOMINA.FC_CN(155) > 1 OR PCK_NOMINA.FC_CN(155) = 0 THEN
                    IF PCK_NOMINA.GL_DIASPROP > 315 THEN
                        PCK_NOMINA.GL_DIASPROP := PCK_NOMINA.GL_DIASPROP - 360;
                        PCK_NOMINA.GL_DIASPROP := CASE WHEN PCK_NOMINA.GL_DIASPROP < 0 THEN 0 ELSE PCK_NOMINA.GL_DIASPROP END;
                    END IF;
                    IF PCK_NOMINA.GL_DIASPROP >= 0 AND ((PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06') AND PCK_NOMINA.FC_CN(155) = 0) THEN
                        PCK_NOMINA.CN(68) := 15;
                        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                            PCK_NOMINA.CN(155) := PCK_NOMINA.FC_CN(155) + PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_GRPNGV + MI_BONPAGADA + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(543) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160)) / 12) * PCK_NOMINA.FC_CN(68) / 30 / 360 * PCK_NOMINA.GL_DIASPROP, 0);
                        END IF;
                    END IF;
                END IF;
                IF UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'TD' OR UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'PV' OR UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'LN' OR UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'SN' THEN
                    PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL((CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI 
                                    ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1 END), PCK_NOMINA.GL_FECHAFIN1);
                    PCK_NOMINA.GL_DIASVAC := TRUNC(15 * PCK_NOMINA.GL_DTV / 360) + PCK_NOMINA.GL_DIASPENDIENTES;
                    PCK_NOMINA.CN(164) := CASE WHEN PCK_NOMINA.GL_DIASVAC > 0 THEN 1 ELSE PCK_NOMINA.GL_DIASVAC END;
                    PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, CASE WHEN PCK_NOMINA.GL_DIASVAC <= 0 THEN 1 ELSE PCK_NOMINA.GL_DIASVAC END);
                    PCK_NOMINA.CN(96) := 0;
                    IF PCK_NOMINA.FC_CN(96) = 0 THEN
                        IF PCK_NOMINA.GL_RTA = 6 THEN
                            PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                            PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                        ELSE
                            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN
                                PCK_NOMINA.CN(96) := (PCK_NOMINA.GL_FECHAFF - (PCK_NOMINA.GL_FECHAFIN + 1));
                            END IF;
                            PCK_NOMINA.GL_FECHAFF1 := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                            PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                            PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL AND PCK_NOMINA.FC_CN(96) = 0 THEN
                                PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_FECHAFIN + 1, PCK_NOMINA.GL_DIASVAC);
                                PCK_NOMINA.CN(96) := (PCK_NOMINA.GL_FECHAFF - (PCK_NOMINA.GL_FECHAFIN + 1));
                                PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                            END IF;
                        END IF;
                    END IF;
                    PCK_NOMINA.CN(96) := CASE WHEN PCK_NOMINA.FC_CN(96) = 1 OR PCK_NOMINA.FC_CN(96) < 1 THEN 0 ELSE PCK_NOMINA.FC_CN(96) END;
                    PCK_NOMINA.GL_DIASVAC := CASE WHEN PCK_NOMINA.GL_DIASVAC = 0 THEN 1 ELSE PCK_NOMINA.GL_DIASVAC END;
                    IF PCK_NOMINA.FC_CN(96) < 0 THEN
                        PCK_NOMINA.CN(96) := 0;
                    END IF;
                    IF PCK_NOMINA.FC_CN(96) = 0 THEN
                        PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                        PCK_NOMINA.CN(96) := TO_NUMBER(PCK_NOMINA.GL_FECHAFF - TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY')) + 1;
                        PCK_NOMINA.CN(96) := CASE WHEN PCK_NOMINA.FC_CN(96) = 1 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO <> PCK_NOMINA.GL_FECHAFF AND PCK_NOMINA.GL_DIASVAC < 1 THEN 0 ELSE PCK_NOMINA.FC_CN(96) END;
                        IF PCK_NOMINA.FC_CN(96) < 0 THEN
                            PCK_NOMINA.CN(96) := 0;
                        END IF;
                    END IF;
                    IF PCK_NOMINA.GL_DTV < 24 AND PCK_NOMINA.FC_CN(96) <= 1 THEN
                        PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_SYSMAN_UTL.FC_ROUND(((15 * PCK_NOMINA.GL_DTV / 360)), 3), 0);
                        PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_ROUND(((15 * PCK_NOMINA.GL_DTV / 360)), 3);
                    ELSE
                        PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(96) / 30, 0) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_SYSMAN_UTL.FC_ROUND(((15 * PCK_NOMINA.GL_DTV / 360)) - PCK_NOMINA.GL_DIASVAC + 0.005, 2), 0);
                        PCK_NOMINA.CN(96)  := PCK_NOMINA.FC_CN(96) + PCK_SYSMAN_UTL.FC_ROUND((15 * PCK_NOMINA.GL_DTV / 360) - PCK_NOMINA.GL_DIASVAC + 0.005, 2);
                    END IF;
                    IF PCK_NOMINA.GL_SPRC = '99' AND (PCK_NOMINA.FC_CN(175) = 0 OR PCK_NOMINA.FC_CN(175) IS NULL) AND PCK_NOMINA.FC_CN(155) > 0 THEN
                        PCK_NOMINA.CN(96) := CASE WHEN PCK_NOMINA.FC_CN(96) IS NULL THEN 21 ELSE PCK_NOMINA.FC_CN(96) END;
                        PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(96) / 30 / 360 * PCK_NOMINA.GL_DTV, 0);
                    END IF;
                END IF;
            END IF;
            PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) < 0 THEN 0 ELSE PCK_NOMINA.FC_CN(175) END;
            PCK_NOMINA.CN(982) := MI_BONPAGADA;
            PCK_NOMINA.CN(987) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORESPV), 0);
        ELSE
            PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0);
            PCK_NOMINA.CN(984) := 0;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAUV), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAUV), 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_DIASPENDIENTES := PCK_NOMINA.FC_CNA(91);
            PCK_NOMINA.CN(93) := PCK_NOMINA.FC_CN(68) * PCK_NOMINA.FC_CN(164) ;
            MI_BONPAGADA := 0;
            IF PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 12 THEN
                PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF PCK_NOMINA.FC_CNA(150) > 0 THEN
                    MI_BONPAGADA := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_SANO - 1), PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, (PCK_NOMINA.GL_SMES - 1), 99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                ELSE
                    MI_BONPAGADA := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                END IF;
            ELSE
                MI_BONPAGADA := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_SANO - 1), PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            END IF;
            IF MI_BONPAGADA = 0 AND PCK_NOMINA.FC_CN(150) > 0 AND PCK_NOMINA.GL_SPER <= 3 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INICIO_DISFRUTE > PCK_NOMINA.GL_FECHAFIN THEN
                MI_BONPAGADA := PCK_NOMINA.FC_CN(150);
            END IF;
            IF PCK_NOMINA.FC_CN(150) <> 0 THEN 
                MI_BONPAGADA := GREATEST(NVL(MI_BONPAGADA,0),PCK_NOMINA.FC_CN(150));
            END IF;
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + (PCK_NOMINA.GL_VPA / 12) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.FC_CN(981) + (MI_BONPAGADA / 12), 0), 0);
            ELSE
                PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + (PCK_NOMINA.GL_VPA / 12) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.FC_CN(981) + (MI_BONPAGADA / 12), 0), 0)   ;
                PCK_NOMINA.GL_FACTORESPV1 := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + (PCK_NOMINA.GL_VPA / 12) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.FC_CN(981) + (MI_BONPAGADA) / 12, 0), 0);
            END IF;
            PCK_NOMINA.CN(982) := PCK_SYSMAN_UTL.FC_ROUND((MI_BONPAGADA) / 12, 0);
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '02' THEN
                IF PCK_NOMINA.FC_CN(403) <> 0 THEN
                    PCK_NOMINA.CN(174) := CASE WHEN PCK_NOMINA.FC_CN(174) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(94), 0) ELSE PCK_NOMINA.FC_CN(174) END;
                END IF;
                IF PCK_NOMINA.FC_CN(419) <> 0 THEN
                    PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(96), 0) ELSE PCK_NOMINA.FC_CN(175) END;
                END IF;
                PCK_NOMINA.CN(155) := CASE WHEN PCK_NOMINA.FC_CN(155) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(68) * PCK_NOMINA.FC_CN(164), 0) ELSE PCK_NOMINA.FC_CN(155) END;
            ELSE
                IF PCK_NOMINA.FC_CN(403) <> 0 THEN
                    PCK_NOMINA.CN(174) := CASE WHEN PCK_NOMINA.FC_CN(174) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(94), 0) ELSE PCK_NOMINA.FC_CN(174) END;
                END IF;
                IF PCK_NOMINA.FC_CN(419) <> 0 THEN
                    PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(96), 0) ELSE PCK_NOMINA.FC_CN(175) END;
                END IF;
            END IF;
        END IF;
    END IF;
    PCK_NOMINA.CN(987) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORESPV), 0);
    PCK_NOMINA.CN(960) := PCK_NOMINA.GL_FACTORESPV ;
    IF PCK_NOMINA.GL_SPRC = '99' THEN
        PCK_NOMINA.CN(961) := PCK_NOMINA.FC_CN(93)     ;
        PCK_NOMINA.CN(962) := PCK_NOMINA.GL_PERIODOS    ;
    ELSE
        PCK_NOMINA.CN(961) := PCK_NOMINA.FC_CN(94)     ;
        PCK_NOMINA.CN(962) := PCK_NOMINA.FC_CN(164)    ;
    END IF;
    PCK_NOMINA.CN(963) := PCK_NOMINA.GL_LICENCIAS  ;
    PCK_NOMINA.CN(964) := PCK_NOMINA.GL_DIASPENDIENTES;
    PCK_NOMINA.CN(965) := PCK_NOMINA.GL_PRIMAPROPORCIONAL;
    PCK_NOMINA.CN(966) := PCK_NOMINA.GL_DINEROPROPORCIONAL;
    PCK_NOMINA.CN(967) := PCK_NOMINA.GL_PENDIENTES;
    PCK_NOMINA.CN(968) := PCK_NOMINA.GL_DIASPROPORCIONAL;
    PCK_NOMINA.CN(975) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
    PCK_NOMINA.CN(976) := 0;
    PCK_NOMINA.CN(977) := PCK_NOMINA.GL_GRPNGV;
    PCK_NOMINA.CN(978) := PCK_NOMINA.GL_AUXT;
    PCK_NOMINA.CN(979) := PCK_NOMINA.GL_AUXA;
    PCK_NOMINA.CN(980) := PCK_NOMINA.GL_VPT;
    PCK_NOMINA.CN(982) := PCK_SYSMAN_UTL.FC_ROUND((MI_BONPAGADA) / 12, 0);
    PCK_NOMINA.CN(983) := 0;
    PCK_NOMINA.CN(984) := 0;
    PCK_NOMINA.CN(987) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORESPV), 0);
    PCK_NOMINA.CN(93)  := PCK_NOMINA.GL_DTV;
    IF (PCK_NOMINA.GL_SMES = 6) THEN
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INICIO_DISFRUTE >= TO_DATE('16/06/' || (PCK_NOMINA.GL_SANO), 'DD/MM/YYYY')  THEN
            PCK_NOMINA.CN(160) := 0;
        END IF;
    END IF;
    PCK_NOMINA.GL_PV_BASE := PCK_NOMINA.GL_FACTORESPV;
    PCK_NOMINA.GL_PV_DIAS := PCK_NOMINA.GL_DIASPROP;
    PCK_NOMINA.GL_VAC_DIAS := PCK_NOMINA.GL_DTV;
END PR_CALCPRIMAVACACIO_ESPFUNZA;

PROCEDURE PR_CALRETROACTIVOMANUAL(
 /*
    NAME              : PR_CALRETROACTIVOMANUAL
    AUTHORS           : LVEGA
    DATE              : 09/10/2025
*/
     UN_CONCEPTO IN NUMBER
 )
 AS 
      MI_VALOR     NUMBER;
 BEGIN
  IF (PCK_NOMINA_PROC01.FC_ACUMNOVEDADES_PROC(
     PCK_NOMINA.GL_SANO,
     PCK_NOMINA.GL_SMES,
     PCK_NOMINA.GL_SPER,
     PCK_NOMINA.GL_SANO,
     PCK_NOMINA.GL_SMES,
     PCK_NOMINA.GL_SPER,
     PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO,
     1,
     UN_CONCEPTO)) <> 0 AND PCK_NOMINA.GL_PROCESOREAL = 10 THEN
        IF PCK_NOMINA.FC_CNAN(UN_CONCEPTO) <> 0 THEN
            PCK_NOMINA.CN(UN_CONCEPTO) := PCK_NOMINA.FC_CNAN(UN_CONCEPTO) +  PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNAN(UN_CONCEPTO)) * (PCK_NOMINA.CCATEGORIA(1).VLR_INCREMENTO / 100), 0);
            MI_VALOR := PCK_NOMINA.FC_CN(UN_CONCEPTO);
        END IF;
    END IF; 
 END PR_CALRETROACTIVOMANUAL;  

 FUNCTION FC_PERIODOACTIVADONOMINAH
/*
    NAME              : PERIODOACTIVADONOMINAH
    AUTHORS           : SYSMAN  SAS, JM CC2893
    DESCRIPTION       : VERIFICA SI UN PERIODO (EN NOMINAS HIBRIDAS) YA SE ENCUENTRA CONFIGURADO PARA NOMINAS HIBRIDAS, RETORNANDO VERDADERO O FALSO NUMERICO./Se ajusta tipo de retorno
    --NAME : validarPeriodoActivoNominaH
  */
  (
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO    IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_ANIO       IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES        IN PCK_SUBTIPOS.TI_MES,
    UN_FECHA      IN DATE
  )
RETURN PCK_SUBTIPOS.TI_LOGICO
  AS
    MI_ESTADO      PCK_SUBTIPOS.TI_LOGICO;
BEGIN
  BEGIN
    SELECT NVL(ESTADO,0) ESTADO
    INTO  MI_ESTADO
    FROM  PERIODOS
    WHERE  COMPANIA       = UN_COMPANIA
      AND  ID_DE_PROCESO  = UN_PROCESO
      AND  ANO            = UN_ANIO
      AND  MES            = UN_MES
      AND  PERIODO        IN (1,2,3)
      AND  TO_DATE(UN_FECHA) BETWEEN FECHAINICIO AND FECHAFINAL 
      AND UPPER(NOM_PERIODO) IN ('MENSUAL', 'PRIMERA QUINCENA', 'SEGUNDA QUINCENA');
    EXCEPTION WHEN NO_DATA_FOUND THEN
       MI_ESTADO := 0;
       RETURN MI_ESTADO;
  END ;
  RETURN MI_ESTADO;
END FC_PERIODOACTIVADONOMINAH;
 
PROCEDURE PR_CALCPRIMAVACUES(
/*
    NAME              : PR_CALCPRIMAVACUES
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JEIMMY CAROLINA ROJAS GUERRERO
    DATE MIGRADOR     : 22/10/2025
*/
    UN_COMPANIA    IN  PCK_SUBTIPOS.TI_COMPANIA
)
AS
    MI_BONPAGADA         PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_PVCOMPLETA        PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
    MI_DIAS_PVACRETIRO   PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
BEGIN
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '10' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '11' THEN
        PCK_NOMINA.CN(174) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1)+PCK_NOMINA.GL_AUXT+PCK_NOMINA.GL_AUXA+(PCK_NOMINA.FC_CN(160)/12),0)/2;
    ELSE
        IF PCK_NOMINA.FC_CNAN(96) <> 0 THEN
            PCK_NOMINA.CN(96) :=  PCK_NOMINA.CNAN(96); 
        END IF;
        PCK_NOMINA.GL_DIASVAC := 0;
        PCK_NOMINA.GL_DIASPENDIENTES := 0;
        PCK_NOMINA.GL_PENDIENTES := 0;
        PCK_NOMINA.GL_LICENCIAS := 0;
        PCK_NOMINA.GL_PRIMAPROPORCIONAL := 0;
        PCK_NOMINA.GL_DINEROPROPORCIONAL := 0;
        PCK_NOMINA.GL_FECHAUV := CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL+1 END;        
        IF PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.GL_SPER = 8 THEN
            PCK_NOMINA.CN(175) := 0;
            PCK_NOMINA.CN(984) := 0;
            PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO))/12,0);
            IF PCK_NOMINA.GL_SMES < 6 AND PCK_NOMINA.FC_CN(160) > 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO <> 353 THEN
                PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160))/12,0);
            END IF;
            MI_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO))/12,0);
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAUV),PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAUV),1,PCK_NOMINA.GL_SANO,PCK_NOMINA.GL_SMES,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_LICENCIAS := NVL(((PCK_NOMINA.FC_CNA(356)+PCK_NOMINA.FC_CNA(357)+PCK_NOMINA.FC_CNA(359)+PCK_NOMINA.FC_CN(356)+PCK_NOMINA.FC_CN(357)+PCK_NOMINA.FC_CN(359)+PCK_NOMINA.FC_CN(339)+PCK_NOMINA.FC_CNA(339))+
                                       (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL 
                                            THEN PCK_NOMINA.FC_CESANTIA(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO,'L')
                                            ELSE 0 END) +PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION),0);
            PCK_NOMINA.GL_DIASPENDIENTES := PCK_NOMINA.FC_CNA(91);
            PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL((CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL 
                                                                        THEN PCK_NOMINA.GL_FECHAI
                                                                        ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL+1 END),PCK_NOMINA.GL_FECHAFIN1);
            PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL((CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL = PCK_NOMINA.GL_FECHAI 
                                                                        THEN PCK_NOMINA.GL_FECHAI
                                                                        ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL+1 END),PCK_NOMINA.GL_FECHAFIN1);
            PCK_NOMINA.GL_DIASPROP := PCK_NOMINA.GL_DTV;
            PCK_NOMINA.GL_PERIODOS := TRUNC(PCK_NOMINA.GL_DTV/360);
            IF (PCK_NOMINA.GL_DTV-(360*PCK_NOMINA.GL_PERIODOS)) >= 330 THEN
                PCK_NOMINA.GL_PERIODOS := PCK_NOMINA.GL_PERIODOS+1;
            END IF;
            IF PCK_NOMINA.GL_PERIODOS = 0 THEN
                PCK_NOMINA.GL_PERIODOS := 1;
            END IF;
            PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15*PCK_NOMINA.GL_PERIODOS,2);
            PCK_NOMINA.CN(93) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(68)*PCK_NOMINA.GL_PERIODOS,2);
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_ANOA,PCK_NOMINA.GL_MESA,PCK_NOMINA.GL_PERA,PCK_NOMINA.GL_SANO,PCK_NOMINA.GL_SMES,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF PCK_NOMINA.GL_DTV = 0 THEN
                PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1)+PCK_NOMINA.GL_GRPNGV+PCK_NOMINA.GL_AUXT+PCK_NOMINA.GL_AUXA+PCK_NOMINA.GL_VPT+PCK_NOMINA.GL_VPA+PCK_NOMINA.FC_CN(981)+MI_BONPAGADA,0);
            ELSE
                PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1)+PCK_NOMINA.GL_GRPNGV+PCK_NOMINA.GL_AUXT+PCK_NOMINA.GL_AUXA+PCK_NOMINA.GL_VPT+PCK_NOMINA.GL_VPA+PCK_NOMINA.FC_CN(981)+MI_BONPAGADA,0);
            END IF;
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                PCK_NOMINA.CN(155) := CASE WHEN PCK_NOMINA.FC_CN(155) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1)+PCK_NOMINA.GL_AUXT+PCK_NOMINA.GL_AUXA+PCK_NOMINA.GL_VPA+PCK_NOMINA.GL_VPT+PCK_NOMINA.GL_GRPNGV+PCK_NOMINA.FC_CN(981)+MI_BONPAGADA)*PCK_NOMINA.FC_CN(93)/30/360*PCK_NOMINA.GL_DTV,0) ELSE PCK_NOMINA.FC_CN(155) END;
            ELSE
                PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1)+PCK_NOMINA.GL_AUXT+PCK_NOMINA.GL_GRPNGV+PCK_NOMINA.GL_AUXA+PCK_NOMINA.GL_VPA+PCK_NOMINA.GL_VPT+PCK_NOMINA.FC_CN(981)+MI_BONPAGADA,0);
                PCK_NOMINA.CN(155) := CASE WHEN PCK_NOMINA.FC_CN(155) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1)+PCK_NOMINA.GL_GRPNGV+PCK_NOMINA.GL_AUXT+PCK_NOMINA.GL_AUXA+PCK_NOMINA.GL_VPA+PCK_NOMINA.GL_VPT+PCK_NOMINA.FC_CN(981)+MI_BONPAGADA,0)*PCK_NOMINA.FC_CN(93)/30/360*PCK_NOMINA.GL_DTV,0) ELSE PCK_NOMINA.FC_CN(155) END;
            END IF;
            IF PCK_NOMINA.FC_CN(175) = 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '01' THEN
                PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL+1 END,PCK_NOMINA.GL_FECHAFIN1);
                PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL = PCK_NOMINA.GL_FECHAI THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL+1 END,PCK_NOMINA.GL_FECHAFIN1);
                PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15*PCK_NOMINA.GL_DTV/360,0);         
                PCK_NOMINA.GL_DTV := PCK_NOMINA.GL_DTV/30*15/360;
                PCK_NOMINA.GL_PERIODOS := CASE WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN 1 ELSE PCK_NOMINA.GL_PERIODOS END;
                IF PCK_NOMINA.GL_SPRC = 99 THEN
                    PCK_NOMINA.GL_RTA := 7;
                END IF;
                PCK_NOMINA.GL_FECHAFF := '';
                PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO,'DD/MM/YYYY'),'DD/MM/YYYY'),PCK_NOMINA.GL_DIASVAC);
                IF (PCK_NOMINA.GL_SPER = 8 AND PCK_NOMINA.GL_SMES = 12) OR PCK_NOMINA.GL_SPRC = 99 THEN
                    PCK_NOMINA.GL_PERIODOS := 1;
                    PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL+1 END,PCK_NOMINA.GL_FECHAFIN1);
                    PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL = PCK_NOMINA.GL_FECHAI THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL+1 END,PCK_NOMINA.GL_FECHAFIN1);
                    PCK_NOMINA.CN(93) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(68)*PCK_NOMINA.GL_PERIODOS,2);
                    PCK_NOMINA.GL_DIASVAC := CASE WHEN PCK_NOMINA.GL_DIASVAC = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(15*PCK_NOMINA.GL_PERIODOS,2) ELSE PCK_NOMINA.GL_DIASVAC END;
                    PCK_NOMINA.GL_DIASVAC := TRUNC(PCK_NOMINA.GL_DIASVAC/360*PCK_NOMINA.GL_DTV);
                    PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_FECHAFIN+1,PCK_NOMINA.GL_DIASVAC);
                END IF;
                IF PCK_NOMINA.FC_CN(96) = 0 THEN
                    IF PCK_NOMINA.GL_RTA = 6 THEN
                        PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO,PCK_NOMINA.GL_DIASVAC);
                        PCK_NOMINA.CN(96) := TO_NUMBER(TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO,'DD/MM/YYYY'),'DD/MM/YYYY')-PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO,PCK_NOMINA.GL_DIASVAC))+1;
                    ELSE
                        PCK_NOMINA.GL_FECHAFF1 := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END,PCK_NOMINA.GL_DIASVAC);
                        PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END,PCK_NOMINA.GL_DIASVAC);
                        IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL+1 END,PCK_NOMINA.GL_FECHAFIN1) > 315 THEN
                            PCK_NOMINA.CN(96) := TO_NUMBER(TO_DATE(TO_CHAR(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END,'DD/MM/YYYY'),'DD/MM/YYYY')-PCK_NOMINA.GL_FECHAFF1)+1;
                        END IF;
                        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN
                            PCK_NOMINA.CN(96) := ((PCK_NOMINA.GL_FECHAFIN+1)-PCK_NOMINA.GL_FECHAFF);
                        END IF;
                    END IF;
                END IF;
                IF PCK_NOMINA.FC_CN(96) = 0 THEN
                    PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END,PCK_NOMINA.GL_DIASVAC);
                    PCK_NOMINA.CN(96) := TO_NUMBER(TO_DATE(TO_CHAR(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN PCK_NOMINA.GL_FECHAFIN1 ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO END,'DD/MM/YYYY'),'DD/MM/YYYY')-PCK_NOMINA.GL_FECHAFF);
                END IF;                                                         
                PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15*PCK_NOMINA.GL_PERIODOS,2);
                PCK_NOMINA.CN(164) := PCK_NOMINA.GL_PERIODOS;
                PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV*PCK_NOMINA.FC_CN(96)/30,0);
                IF PCK_NOMINA.GL_SPRC = 99 THEN
                    PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175);
                ELSE
                    PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175)+PCK_NOMINA.GL_PENDIENTES;
                END IF;                                                        
                IF PCK_NOMINA.GL_SPER = 8 AND PCK_NOMINA.GL_SMES = 12 THEN
                    PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1)+PCK_NOMINA.GL_GRPNGV+PCK_NOMINA.GL_AUXT+PCK_NOMINA.GL_AUXA+PCK_NOMINA.GL_VPT+(PCK_NOMINA.FC_CNA(160)+PCK_NOMINA.FC_CNA(503)+PCK_NOMINA.FC_CNA(543))/12,0);
                    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                        PCK_NOMINA.CN(155) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1)+PCK_NOMINA.GL_AUXT+PCK_NOMINA.GL_AUXA+PCK_NOMINA.GL_VPA+PCK_NOMINA.GL_VPT+PCK_NOMINA.GL_GRPNGV+(PCK_NOMINA.FC_CNA(160)+PCK_NOMINA.FC_CNA(503)+PCK_NOMINA.FC_CNA(543))/12+(PCK_NOMINA.FC_CNA(150)+PCK_NOMINA.FC_CNA(514)+PCK_NOMINA.FC_CNA(538))/12)*PCK_NOMINA.FC_CN(93)/30/360*PCK_NOMINA.GL_DTV,0);
                    ELSE
                        PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1)+PCK_NOMINA.GL_AUXT+PCK_NOMINA.GL_GRPNGV+PCK_NOMINA.GL_AUXA+PCK_NOMINA.GL_VPT+(PCK_NOMINA.FC_CNA(160)+PCK_NOMINA.FC_CNA(503)+PCK_NOMINA.FC_CNA(543))/12+(PCK_NOMINA.FC_CNA(150)+PCK_NOMINA.FC_CNA(514)+PCK_NOMINA.FC_CNA(543))/12,0);
                        PCK_NOMINA.CN(155) := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1)+PCK_NOMINA.GL_GRPNGV+PCK_NOMINA.GL_VPA+PCK_NOMINA.GL_AUXT+PCK_NOMINA.GL_AUXA+PCK_NOMINA.GL_VPT+(PCK_NOMINA.FC_CNA(160)+PCK_NOMINA.FC_CNA(503)+PCK_NOMINA.FC_CNA(543))/12+(PCK_NOMINA.FC_CNA(150)+PCK_NOMINA.FC_CNA(514)+PCK_NOMINA.FC_CNA(543))/12,0)*PCK_NOMINA.FC_CN(93)/30/360*PCK_NOMINA.GL_DTV,0);
                    END IF;
                    PCK_NOMINA.CN(964) := PCK_NOMINA.GL_DIASPENDIENTES;
                    PCK_NOMINA.GL_DIASPROPORCIONAL := PCK_NOMINA.GL_DTV;
                END IF;                                                        
            ELSIF PCK_NOMINA.FC_CN(175) = 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                PCK_NOMINA.GL_DTV := PCK_NOMINA.GL_DTV/30*15/360;
                IF PCK_NOMINA.GL_SPRC = 99 THEN
                    PCK_NOMINA.GL_RTA := 7;
                END IF;
                PCK_NOMINA.GL_FECHAFF := '';
                PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL+1 END,PCK_NOMINA.GL_FECHAFIN1);
                PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL = PCK_NOMINA.GL_FECHAI THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL+1 END,PCK_NOMINA.GL_FECHAFIN1);
                PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15*PCK_NOMINA.GL_DTV/360,0);
                PCK_NOMINA.CN(164) := CASE WHEN PCK_NOMINA.GL_DIASVAC > 0 THEN 1 ELSE PCK_NOMINA.GL_DIASVAC END;
                PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO,PCK_NOMINA.GL_DIASVAC);
                IF PCK_NOMINA.FC_CN(96) = 0 THEN
                    IF PCK_NOMINA.GL_RTA = 6 THEN
                        PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO,PCK_NOMINA.GL_DIASVAC);
                        PCK_NOMINA.CN(96) := TO_NUMBER(TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO,'DD/MM/YYYY'),'DD/MM/YYYY')-PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO,PCK_NOMINA.GL_DIASVAC))+1;
                    ELSE
                        PCK_NOMINA.GL_FECHAFF1 := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO,PCK_NOMINA.GL_DIASVAC);
                        PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO,PCK_NOMINA.GL_DIASVAC);
                        PCK_NOMINA.CN(96) := TO_NUMBER(TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO,'DD/MM/YYYY'),'DD/MM/YYYY')-PCK_NOMINA.GL_FECHAFF1)+1;
                    END IF;
                END IF;
                IF PCK_NOMINA.FC_CN(96) = 0 THEN
                    PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO,PCK_NOMINA.GL_DIASVAC);
                    PCK_NOMINA.CN(96) := TO_NUMBER(TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO,'DD/MM/YYYY'),'DD/MM/YYYY')-PCK_NOMINA.GL_FECHAFF)+1;
                END IF;
                PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV*PCK_NOMINA.FC_CN(96)/30,0);
                IF PCK_NOMINA.GL_SPRC = 99 THEN
                    PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175);
                ELSE
                    PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175)+PCK_NOMINA.GL_PENDIENTES;
                END IF;
            END IF;
            IF (PCK_NOMINA.GL_SPER = 5 OR PCK_NOMINA.GL_SPER = 7) OR PCK_NOMINA.FC_CN(404) <> 0 THEN
                PCK_NOMINA.CN(68) := 0;
                PCK_NOMINA.CN(93) := CASE WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN 1 ELSE PCK_NOMINA.GL_PERIODOS END;
                IF PCK_NOMINA.FC_CN(155) > 1 OR PCK_NOMINA.FC_CN(155) = 0 THEN
                    IF PCK_NOMINA.GL_DIASPROP > 315 THEN
                        PCK_NOMINA.GL_DIASPROP := PCK_NOMINA.GL_DIASPROP-360;
                        PCK_NOMINA.GL_DIASPROP := CASE WHEN PCK_NOMINA.GL_DIASPROP < 0 THEN 0 ELSE PCK_NOMINA.GL_DIASPROP END;
                    END IF;
                    IF PCK_NOMINA.GL_DIASPROP >= 0 AND ((PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06') AND PCK_NOMINA.FC_CN(155) = 0) THEN
                        PCK_NOMINA.CN(68) := 15;
                        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                            PCK_NOMINA.CN(155) := PCK_NOMINA.FC_CN(155)+PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END +PCK_NOMINA.GL_VPA+PCK_NOMINA.GL_AUXT+PCK_NOMINA.GL_AUXA+PCK_NOMINA.GL_GRPNGV+MI_BONPAGADA+(PCK_NOMINA.FC_CNA(160)+PCK_NOMINA.FC_CNA(543)+PCK_NOMINA.FC_CNA(503)+PCK_NOMINA.FC_CN(160))/12)*PCK_NOMINA.FC_CN(68)/30/360*PCK_NOMINA.GL_DIASPROP,0);
                        END IF;
                    END IF;
                END IF;                                                            
                IF UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'TD' OR UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'PV' OR UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'LN' THEN     
                    PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL+1 END,PCK_NOMINA.GL_FECHAFIN1);
                    PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL = PCK_NOMINA.GL_FECHAI THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL+1 END,PCK_NOMINA.GL_FECHAFIN1);    
                    PCK_NOMINA.GL_DTV := CASE WHEN PCK_NOMINA.GL_DTV > 330 THEN PCK_NOMINA.GL_DTV ELSE 0 END;
                    PCK_NOMINA.GL_DTV := CASE WHEN PCK_NOMINA.GL_DTV > 315 AND PCK_NOMINA.GL_DTV < 360 THEN 360 ELSE PCK_NOMINA.GL_DTV END;
                    PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15*PCK_NOMINA.GL_DTV/360,0)+PCK_NOMINA.GL_DIASPENDIENTES;
                    IF PCK_NOMINA.GL_DIASVAC <= 0 THEN 
                        PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL+1 END,PCK_NOMINA.GL_FECHAFIN1);
                        PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL = PCK_NOMINA.GL_FECHAI THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL+1 END,PCK_NOMINA.GL_FECHAFIN1);    
                        PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15*PCK_NOMINA.GL_DTV/360,0);
                    END IF;
                    PCK_NOMINA.CN(164) := CASE WHEN PCK_NOMINA.GL_DIASVAC > 0 THEN 1 ELSE PCK_NOMINA.GL_DIASVAC END;
                    IF PCK_NOMINA.FC_CN(164) <= 0 THEN
                        PCK_NOMINA.CN(164) := CASE WHEN PCK_NOMINA.GL_DIASVAC <= 0 THEN 1 ELSE PCK_NOMINA.GL_DIASVAC END;
                    END IF;
                    PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO,PCK_NOMINA.GL_DIASVAC);
                    PCK_NOMINA.CN(96) := 0;
                    IF PCK_NOMINA.FC_CN(96) = 0 THEN
                        IF PCK_NOMINA.GL_RTA = 6 THEN
                            PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO,PCK_NOMINA.GL_DIASVAC);
                            PCK_NOMINA.CN(96) := TO_NUMBER(TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO,'DD/MM/YYYY'),'DD/MM/YYYY')-PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO,PCK_NOMINA.GL_DIASVAC))+1;
                        ELSE
                            PCK_NOMINA.GL_FECHAFF1 := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO,PCK_NOMINA.GL_DIASVAC);
                            PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO,PCK_NOMINA.GL_DIASVAC);
                            PCK_NOMINA.CN(96) := TO_NUMBER(TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO,'DD/MM/YYYY'),'DD/MM/YYYY')-PCK_NOMINA.GL_FECHAFF1)+1;
                        END IF;
                        IF PCK_NOMINA.FC_CN(96) < 1 THEN                                                
                            PCK_NOMINA.CN(96) := TO_NUMBER(TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO,'DD/MM/YYYY'),'DD/MM/YYYY')-PCK_NOMINA.GL_FECHAFF1)+1;
                        END IF;
                    END IF;
                    PCK_NOMINA.CN(96) := CASE WHEN PCK_NOMINA.FC_CN(96) = 1 OR PCK_NOMINA.FC_CN(96) < 0 THEN 0 ELSE PCK_NOMINA.FC_CN(96) END;
                    IF PCK_NOMINA.FC_CN(96) = 0 THEN
                        PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO,PCK_NOMINA.GL_DIASVAC);
                        PCK_NOMINA.CN(96) := TO_NUMBER(TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO,'DD/MM/YYYY'),'DD/MM/YYYY')-PCK_NOMINA.GL_FECHAFF)+1;
                        PCK_NOMINA.CN(96) := CASE WHEN PCK_NOMINA.FC_CN(96) = 1 OR PCK_NOMINA.FC_CN(96) < 0 THEN 0 ELSE PCK_NOMINA.FC_CN(96) END;
                    END IF;
                    IF PCK_NOMINA.FC_CN(96) < 0 THEN
                        PCK_NOMINA.CN(96) := 0;
                    END IF;
                    PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV*PCK_NOMINA.FC_CN(96)/30,0);
                END IF;
            END IF; 
            PCK_NOMINA.CN(982) := MI_BONPAGADA;
            IF PCK_NOMINA.GL_SPRC = 99 THEN
                IF PCK_PARST.FC_PAR('ENTIDAD PUBLICA O PRIVADA','NO') = 'PRIVADA' THEN
                    PCK_NOMINA.GL_PV_DIAS := 0;
                    PCK_NOMINA.GL_PV_BASE := 0;
                    PCK_NOMINA.GL_PV_TOTAL := 0;
                    PCK_NOMINA.GL_PV_MESANT := 0;
                    PCK_NOMINA.GL_PV_PAGOSMES := 0;
                    PCK_NOMINA.GL_PV_PRV := 0;
                    PCK_NOMINA.CN(491) := 0;
                ELSE
                    PCK_NOMINA.GL_DIASVAC := 0;
                    PCK_NOMINA.GL_DIASVAC := PCK_NOMINA.GL_DTV;
                    MI_PVCOMPLETA := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV/30*15,0);    
                    PCK_NOMINA.CN(494) := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV,0)/30*15,0)/360*PCK_NOMINA.GL_DIASVAC,0);
                    PCK_NOMINA.GL_PV_DIAS := PCK_NOMINA.GL_DTV;
                    PCK_NOMINA.GL_PV_BASE := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV,0);
                    PCK_NOMINA.GL_PV_TOTAL := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV,0)/30*15,0)/360*PCK_NOMINA.GL_DIASVAC,0);
                    PCK_NOMINA.GL_PV_MESANT := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV/30*15/360*CASE WHEN (PCK_NOMINA.GL_DTV-30) <= 0 THEN 0 ELSE (PCK_NOMINA.GL_DTV-30) END,0);
                    PCK_NOMINA.GL_PV_PAGOSMES := PCK_NOMINA.CNA(155);
                    PCK_NOMINA.GL_PV_PRV := PCK_NOMINA.FC_CN(494)-PCK_NOMINA.GL_PV_MESANT;
                    PCK_NOMINA.CN(494) := PCK_NOMINA.FC_CN(494)-PCK_NOMINA.GL_PV_MESANT;
                END IF;
                PCK_NOMINA.GL_DIASVAC := 0;        
                PCK_NOMINA.GL_DIASVAC := PCK_NOMINA.GL_DTV;
                IF PCK_PARENTR.PARAMETRO31 = '800021261-8' THEN
                    MI_DIAS_PVACRETIRO := 15;
                ELSE
                    MI_DIAS_PVACRETIRO := MI_DIAS_PVACRETIRO;
                END IF;
                MI_PVCOMPLETA := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV,0)/30*MI_DIAS_PVACRETIRO,0),0);    
                PCK_NOMINA.CN(497) := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV,0)/30*MI_DIAS_PVACRETIRO,0)/360*PCK_NOMINA.GL_DIASVAC,0);
                PCK_NOMINA.GL_VAC_DIAS := 0;
                PCK_NOMINA.GL_VAC_BASE := 0;
                PCK_NOMINA.GL_VAC_TOTAL := 0;
                PCK_NOMINA.GL_VAC_MESANT := 0;
                PCK_NOMINA.GL_VAC_PAGOSMES := 0;
                PCK_NOMINA.GL_VAC_PRV := 0;
                PCK_NOMINA.CN(497) := 0;
                PCK_NOMINA.CN(497) := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV,0)/30*MI_DIAS_PVACRETIRO,0)/360*PCK_NOMINA.GL_DIASVAC,0);
                PCK_NOMINA.GL_VACD_DIAS := PCK_NOMINA.GL_DIASVAC;        
                PCK_NOMINA.GL_VACD_BASE := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV,0);                            
                PCK_NOMINA.GL_VACD_TOTAL := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV,0)/30*MI_DIAS_PVACRETIRO,0)/360*PCK_NOMINA.GL_DIASVAC,0);
                PCK_NOMINA.GL_VACD_MESANT := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV,0)/30*MI_DIAS_PVACRETIRO,0)/360*CASE WHEN (PCK_NOMINA.GL_DIASVAC-30) <= 0 THEN 0 ELSE (PCK_NOMINA.GL_DIASVAC-30) END,0);        
                PCK_NOMINA.GL_VACD_PAGOSMES := PCK_NOMINA.CNA(175)+PCK_NOMINA.CNA(174);
                PCK_NOMINA.GL_VACD_PRV := PCK_NOMINA.FC_CN(497)-PCK_NOMINA.GL_VACD_MESANT;
                PCK_NOMINA.CN(497) := PCK_NOMINA.FC_CN(497)-PCK_NOMINA.GL_VACD_MESANT;    
            END IF;
        ELSE
            PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)/12,0)+PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_CALCULO.FC_VLRULTIMPRIMAEXTRASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)/12,0);
            PCK_NOMINA.CN(984) := PCK_NOMINA.FC_VALORULTIMOQUINQUENIO(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAUV),PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAUV),1,PCK_NOMINA.GL_SANO,PCK_NOMINA.GL_SMES,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_DIASPENDIENTES := PCK_NOMINA.FC_CNA(91);
            PCK_NOMINA.CN(93) := PCK_NOMINA.FC_CN(68)*PCK_NOMINA.FC_CN(164);
            MI_BONPAGADA := 0;
            IF PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 12 THEN
                PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO,1,1,PCK_NOMINA.GL_SANO,PCK_NOMINA.GL_SMES,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF PCK_NOMINA.FC_CNA(150) > 0 THEN
                    MI_BONPAGADA := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO-1,PCK_NOMINA.GL_SMES,1,PCK_NOMINA.GL_SANO,PCK_NOMINA.GL_SMES-1,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                ELSE
                    MI_BONPAGADA := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                END IF;
            ELSE
                MI_BONPAGADA := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO-1,PCK_NOMINA.GL_SMES,1,PCK_NOMINA.GL_SANO,PCK_NOMINA.GL_SMES-1,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            END IF;
            IF PCK_NOMINA.FC_CN(150) > 0 THEN
                MI_BONPAGADA := PCK_NOMINA.FC_CN(150)-PCK_NOMINA.FC_CN(514);
            END IF;    
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END +PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_AUXT+PCK_NOMINA.GL_AUXA+PCK_NOMINA.GL_VPT+PCK_NOMINA.GL_VPA+PCK_NOMINA.GL_GRPNGV+PCK_NOMINA.FC_CN(981)+(MI_BONPAGADA/12),0),0);
            ELSE 
                PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END +PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_AUXT+PCK_NOMINA.GL_AUXA+PCK_NOMINA.GL_VPT+PCK_NOMINA.GL_VPA+PCK_NOMINA.GL_GRPNGV+PCK_NOMINA.FC_CN(981)+MI_BONPAGADA/12,0),0);    
                PCK_NOMINA.GL_FACTORESPV1 := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END +PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_AUXT+PCK_NOMINA.GL_AUXA+PCK_NOMINA.GL_VPT+PCK_NOMINA.GL_VPA+PCK_NOMINA.GL_GRPNGV+PCK_NOMINA.FC_CN(981)+(MI_BONPAGADA)/12,0),0);    
            END IF;
            PCK_NOMINA.CN(982) := PCK_SYSMAN_UTL.FC_ROUND(MI_BONPAGADA/12,0);
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '02' THEN
                IF PCK_NOMINA.FC_CN(403) <> 0 THEN
                    PCK_NOMINA.CN(174) := CASE WHEN PCK_NOMINA.FC_CN(174) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV/30*PCK_NOMINA.FC_CN(94),0) ELSE PCK_NOMINA.FC_CN(174) END;
                END IF;
                IF PCK_NOMINA.FC_CN(419) <> 0 THEN
                    PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV/30*PCK_NOMINA.FC_CN(96),0) ELSE PCK_NOMINA.FC_CN(175) END;
                END IF;
                PCK_NOMINA.CN(155) := CASE WHEN PCK_NOMINA.FC_CN(155) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV/30*PCK_NOMINA.FC_CN(68)*PCK_NOMINA.FC_CN(164),0) ELSE PCK_NOMINA.FC_CN(155) END;
            ELSE
                IF PCK_NOMINA.FC_CN(403) <> 0 THEN
                    PCK_NOMINA.CN(174) := CASE WHEN PCK_NOMINA.FC_CN(174) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV/30*PCK_NOMINA.FC_CN(94),0) ELSE PCK_NOMINA.FC_CN(174) END;
                END IF;
                IF PCK_NOMINA.FC_CN(419) <> 0 THEN
                    PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV/30*PCK_NOMINA.FC_CN(96),0) ELSE PCK_NOMINA.FC_CN(175) END;
                END IF;    
            END IF;
        END IF;
    END IF;                     
    PCK_NOMINA.CN(960) := PCK_NOMINA.GL_FACTORESPV;              
    IF PCK_NOMINA.GL_SPRC = 99 THEN
        PCK_NOMINA.CN(961) := PCK_NOMINA.FC_CN(93);
        PCK_NOMINA.CN(962) := PCK_NOMINA.GL_PERIODOS;
    ELSE 
        PCK_NOMINA.CN(961) := PCK_NOMINA.FC_CN(94);
        PCK_NOMINA.CN(962) := PCK_NOMINA.FC_CN(164);
    END IF;
    PCK_NOMINA.CN(963) := PCK_NOMINA.GL_LICENCIAS;
    PCK_NOMINA.CN(964) := PCK_NOMINA.GL_DIASPENDIENTES;
    PCK_NOMINA.CN(965) := PCK_NOMINA.GL_PRIMAPROPORCIONAL;
    PCK_NOMINA.CN(966) := PCK_NOMINA.GL_DINEROPROPORCIONAL;
    PCK_NOMINA.CN(967) := PCK_NOMINA.GL_PENDIENTES;
    PCK_NOMINA.CN(968) := PCK_NOMINA.GL_DIASPROPORCIONAL;
    PCK_NOMINA.CN(975) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
    PCK_NOMINA.CN(976) := 0;
    PCK_NOMINA.CN(977) := PCK_NOMINA.GL_GRPNGV;
    PCK_NOMINA.CN(978) := PCK_NOMINA.GL_AUXT;
    PCK_NOMINA.CN(979) := PCK_NOMINA.GL_AUXA;
    PCK_NOMINA.CN(980) := PCK_NOMINA.GL_VPT;
    PCK_NOMINA.CN(982) := PCK_SYSMAN_UTL.FC_ROUND(MI_BONPAGADA/12,0);
    PCK_NOMINA.CN(983) := 0;
    PCK_NOMINA.CN(984) := PCK_NOMINA.FC_VALORULTIMOQUINQUENIO(UN_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    PCK_NOMINA.CN(985) := 0; PCK_NOMINA.CN(986) := 0; PCK_NOMINA.CN(987) := 0;
    PCK_NOMINA.GL_FV_SBM := PCK_NOMINA.FC_CN(975);
    PCK_NOMINA.GL_FV_ISPA := 0;
    PCK_NOMINA.GL_FV_GR := PCK_NOMINA.GL_GRPNGV;
    PCK_NOMINA.GL_FV_PT := PCK_NOMINA.GL_VPT;
    PCK_NOMINA.GL_FV_PA := PCK_NOMINA.GL_VPA;
    PCK_NOMINA.GL_FV_AT := PCK_NOMINA.GL_AUXT;
    PCK_NOMINA.GL_FV_SA := PCK_NOMINA.GL_AUXA;
    PCK_NOMINA.GL_FV_BASP := PCK_SYSMAN_UTL.FC_ROUND(MI_BONPAGADA/12,0);
    PCK_NOMINA.GL_FV_PS := PCK_NOMINA.FC_CN(981);
    PCK_NOMINA.GL_FV_PV := 0; PCK_NOMINA.GL_FV_PN := 0; PCK_NOMINA.GL_FV_HE := 0; PCK_NOMINA.GL_FV_QUI := 0;
    PCK_NOMINA.GL_FV_BASE := PCK_NOMINA.GL_FACTORESPV;
    PCK_NOMINA.GL_FV_DIAS := PCK_NOMINA.FC_CN(35);
    PCK_NOMINA.GL_FV_DIASD := PCK_NOMINA.FC_CN(96);
    PCK_NOMINA.GL_FV_VVAC := PCK_NOMINA.FC_CN(174);
    PCK_NOMINA.GL_FV_VVACD := PCK_NOMINA.FC_CN(175);
    PCK_NOMINA.GL_FV_VPV := PCK_NOMINA.FC_CN(155);
    PCK_NOMINA.GL_FV_VBER := PCK_NOMINA.FC_CN(151);
    PCK_NOMINA.GL_FV_VPA := 0;
    PCK_NOMINA.GL_PV_BASE := PCK_NOMINA.GL_FACTORESPV;
    PCK_NOMINA.GL_PV_DIAS := PCK_NOMINA.GL_DIASPROP;
    PCK_NOMINA.GL_VAC_DIAS := PCK_NOMINA.GL_DTV;
END PR_CALCPRIMAVACUES;

PROCEDURE PR_CALCPRIMANAVIDADUES(
/*
    NAME              : PR_CALCPRIMANAVIDADUES
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JEIMMY CAROLINA ROJAS GUERRERO
    DATE MIGRADOR     : 22/10/2025
*/
    UN_COMPANIA    IN  PCK_SUBTIPOS.TI_COMPANIA
)
AS
    MI_FECHAFPN    DATE;
    MI_PERI		   PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_N2		   NUMBER := 0;
    MI_N1		   NUMBER := 0;
	MI_TRANSPORTELEGAL  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_RETEFUENTE  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_DIASPRIMADIC	NUMBER := 0;
BEGIN
    IF PCK_NOMINA.FC_CNA(11) >= 30 AND PCK_NOMINA.FC_CN(10) = 0 AND PCK_NOMINA.FC_CN(11) = 0 THEN
        PCK_NOMINA.PR_INCLUIRNOVEDAD(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SPRC,PCK_NOMINA.GL_SANO,PCK_NOMINA.GL_SMES,PCK_NOMINA.GL_SPER,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO,10,PCK_NOMINA.FC_CNA(10),NULL,PCK_CONEXION.FC_GETUSER);
        PCK_NOMINA.PR_INCLUIRNOVEDAD(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SPRC,PCK_NOMINA.GL_SANO,PCK_NOMINA.GL_SMES,PCK_NOMINA.GL_SPER,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO,11,PCK_NOMINA.FC_CNA(11),NULL,PCK_CONEXION.FC_GETUSER);
        PCK_NOMINA.CN(10) := PCK_NOMINA.FC_CNA(10);
    END IF;
    PCK_NOMINA.GL_PVAC := 0;
    IF NOT(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '10' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '11') THEN
        PCK_NOMINA.GL_FACTORPN := 0;
        PCK_NOMINA.GL_DNT := 0;
        PCK_NOMINA.GL_FECHAIPN := TO_DATE('01/01/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY');
        PCK_NOMINA.GL_FECHAIPN := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPN THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPN END;
        PCK_NOMINA.GL_FECHAIPN1 := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY');
        PCK_NOMINA.GL_FECHAIPN1 := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPN1 THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPN1 END;
        IF PCK_NOMINA.GL_SMES = 12 AND (PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3) THEN
            PCK_NOMINA.GL_FECHAIPN := TO_DATE('01/12/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY');
            PCK_NOMINA.GL_FECHAIPN1 := TO_DATE('01/12/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY');
        END IF;
        IF PCK_NOMINA.FC_CN(404) = 0 AND PCK_NOMINA.GL_FECHAI <= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') THEN
            MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY');
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO,PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN),1,PCK_NOMINA.GL_SANO,12,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_PVAC := 0;
            IF PCK_NOMINA_CALCULO2.FC_VLRULTPERDPRIMAVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) > 1 THEN
                MI_PERI := 0;
				MI_PERI := PCK_NOMINA_CALCULO2.FC_VLRULTPERDPRIMAVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
				PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0);
			ELSE
				PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0);
			END IF;
            PCK_NOMINA.CN(942) := 0;
            PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)/12,0) +PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(159)/12,0);
            IF PCK_PARENTR.PARAMETRO31 = '800016757-9' THEN
                PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO,PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN),CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN 1 ELSE 2 END,PCK_NOMINA.GL_SANO,12,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(160)/12+PCK_NOMINA.FC_CN(160)/12,0);
            END IF;
            PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC/12,0);    
            PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)/12+PCK_NOMINA.FC_CN(160)/12,0);
            PCK_NOMINA.GL_FACTORPN := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END +PCK_NOMINA.FC_CN(942)+PCK_NOMINA.GL_GRPNGV+PCK_NOMINA.GL_AUXT+PCK_NOMINA.GL_AUXA+PCK_NOMINA.GL_VPT+PCK_NOMINA.GL_VPA+PCK_NOMINA.FC_CN(931)+(PCK_NOMINA.GL_PVAC/12)+PCK_NOMINA.FC_CN(939);
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN,MI_FECHAFPN);    
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN,MI_FECHAFPN);  
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO,i,1,PCK_NOMINA.GL_SANO,i,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(359)+PCK_NOMINA.FC_CNA(356)+PCK_NOMINA.FC_CNA(357)+PCK_NOMINA.FC_CNA(339)+PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS-1;
                    PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938)+(PCK_NOMINA.FC_CNA(359)+PCK_NOMINA.FC_CNA(356)+PCK_NOMINA.FC_CNA(357)+PCK_NOMINA.FC_CNA(339)+PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            IF PCK_NOMINA.GL_FECHAR IS NOT NULL THEN
                IF PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') THEN
                    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN,PCK_NOMINA.GL_FECHAR)-PCK_NOMINA.GL_DNT;
                END IF;
            END IF;
            IF PCK_NOMINA.FC_CN(937) > 0 THEN
                PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937)/30);
            END IF;
            PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS',' ') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN,MI_FECHAFPN)-PCK_NOMINA.GL_DNT;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN/360*PCK_NOMINA.GL_DCC,0);
                END IF;
                IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(404) <> 0 THEN
                    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO,12,1,PCK_NOMINA.GL_SANO,12,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);   
                    PCK_NOMINA.CN(504) := CASE WHEN PCK_NOMINA.FC_CN(504) = 0 THEN PCK_NOMINA.FC_CN(158)-PCK_NOMINA.FC_CNA(158) ELSE PCK_NOMINA.FC_CN(504) END;
                    PCK_NOMINA.CN(158) := 0;
                END IF;
            ELSE 
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN/12*PCK_NOMINA.GL_DOCEAVAS,0);
                END IF;
            END IF;
        ELSIF PCK_NOMINA.FC_CN(404) <> 0 THEN
            MI_FECHAFPN := PCK_NOMINA.GL_FECHAR;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO,PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN),CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN 1 ELSE 2 END,PCK_NOMINA.GL_SANO,PCK_NOMINA.GL_SMES,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.CN(942) := 0;
            IF PCK_NOMINA.FC_CNA(164) > 1 THEN
                PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0)/PCK_NOMINA.FC_CNA(164);
            ELSE
                IF PCK_NOMINA.FC_CN(155) > 0 THEN
                    PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CN(155);
                ELSE
                    PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0);
                END IF;
            END IF;
            IF PCK_NOMINA.FC_CNA(160) > 0 THEN
                PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(160)/12,0)+PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(159)/12+PCK_NOMINA.FC_CN(160)/12,0);
            ELSIF PCK_NOMINA.FC_CN(160) > 0 THEN
                PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(160)/12,0)+PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(159)/12,0);
            END IF;
            IF PCK_PARENTR.PARAMETRO31 = '800016757-9' THEN   
                PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO,PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN),CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN 1 ELSE 2 END,PCK_NOMINA.GL_SANO,12,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(160)/12+PCK_NOMINA.FC_CN(160)/12,0);
            END IF;
            PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC/12,0);
            IF PCK_NOMINA.FC_CN(150) > 0 THEN
                PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(150)/12,0);
            ELSIF PCK_NOMINA.FC_CNA(150) > 0 THEN
                PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150)+PCK_NOMINA.FC_CNA(514))/12,0);
            END IF;
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN,MI_FECHAFPN)-PCK_NOMINA.GL_DNT;
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN,MI_FECHAFPN);
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO,i,1,PCK_NOMINA.GL_SANO,i,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(359)+PCK_NOMINA.FC_CNA(356)+PCK_NOMINA.FC_CNA(357)+PCK_NOMINA.FC_CNA(339)+PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS-1;
                    PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938)+(PCK_NOMINA.FC_CNA(359)+PCK_NOMINA.FC_CNA(356)+PCK_NOMINA.FC_CNA(357)+PCK_NOMINA.FC_CNA(339)+PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            PCK_NOMINA.GL_FACTORPN := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1)+PCK_NOMINA.FC_CN(942)+PCK_NOMINA.GL_GRPNGV+PCK_NOMINA.GL_AUXT+PCK_NOMINA.GL_VPA+PCK_NOMINA.GL_AUXA+PCK_NOMINA.GL_VPT+PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(931),0)+PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC/12,0)+PCK_NOMINA.FC_CN(939),0);    
            IF PCK_NOMINA.FC_CN(937) > 0 THEN
                PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937)/30);
            END IF;
            PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS',' ') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN,MI_FECHAFPN)-PCK_NOMINA.GL_DNT;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN/360*PCK_NOMINA.GL_DCC,0);
                END IF;
                IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(404) <> 0 THEN
                    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO,12,1,PCK_NOMINA.GL_SANO,12,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);   
                    PCK_NOMINA.CN(504) := CASE WHEN PCK_NOMINA.FC_CN(504) = 0 THEN PCK_NOMINA.FC_CN(158)-PCK_NOMINA.FC_CNA(158) ELSE PCK_NOMINA.FC_CN(504) END;
                    PCK_NOMINA.CN(158) := 0;
                END IF;
            ELSE 
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN/12*PCK_NOMINA.GL_DOCEAVAS,0);
                END IF;
            END IF;
        ELSIF PCK_NOMINA.GL_FECHAI > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAI <= TO_DATE('30/06/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') THEN
            MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY');
            IF PCK_NOMINA.GL_FECHAR IS NOT NULL THEN
                IF PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') THEN
                    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN,PCK_NOMINA.GL_FECHAR)-PCK_NOMINA.GL_DNT;
                END IF;
            END IF;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO,PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN),CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN 1 ELSE 2 END,PCK_NOMINA.GL_SANO,11,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF PCK_NOMINA.FC_CNA(164) > 1 THEN
                PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0)/PCK_NOMINA.FC_CNA(164); 
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0);
            END IF;
            PCK_NOMINA.CN(942) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(70)/12,0);
            PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0)/12+PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(159)/12,0)+PCK_NOMINA.FC_CN(160)/12;
            PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC/12,0);
            PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)/12,0);
            IF PCK_PARENTR.PARAMETRO31 = '800016757-9' THEN
                PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO,PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN),CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN 1 ELSE 2 END,PCK_NOMINA.GL_SANO,12,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(160)/12+PCK_NOMINA.FC_CN(160)/12,0);
            END IF;
            PCK_NOMINA.GL_FACTORPN := PCK_NOMINA.FC_CN(1)+PCK_NOMINA.FC_CN(942)+PCK_NOMINA.GL_GRPNGV+PCK_NOMINA.GL_VPA+PCK_NOMINA.GL_AUXT+PCK_NOMINA.GL_AUXA+PCK_NOMINA.GL_VPT+PCK_NOMINA.FC_CN(931)+(PCK_NOMINA.GL_PVAC/12)+PCK_NOMINA.FC_CN(939);
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN,MI_FECHAFPN)-PCK_NOMINA.GL_DNT;    
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN,MI_FECHAFPN);  
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO,i,1,PCK_NOMINA.GL_SANO,i,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(359)+PCK_NOMINA.FC_CNA(356)+PCK_NOMINA.FC_CNA(357)+PCK_NOMINA.FC_CNA(339)+PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS-1;
                    PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938)+(PCK_NOMINA.FC_CNA(359)+PCK_NOMINA.FC_CNA(356)+PCK_NOMINA.FC_CNA(357)+PCK_NOMINA.FC_CNA(339)+PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            IF PCK_NOMINA.FC_CN(937) > 0 THEN
                PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937)/30);
            END IF;
            PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS',' ') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN,MI_FECHAFPN)-PCK_NOMINA.GL_DNT;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN/360*PCK_NOMINA.GL_DCC,0);
                END IF;
                IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(404) <> 0 THEN
                    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO,12,1,PCK_NOMINA.GL_SANO,12,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);   
                    PCK_NOMINA.CN(504) := CASE WHEN PCK_NOMINA.FC_CN(504) = 0 THEN PCK_NOMINA.FC_CN(158)-PCK_NOMINA.FC_CNA(158) ELSE PCK_NOMINA.FC_CN(504) END;
                    PCK_NOMINA.CN(158) := 0;
                END IF;
            ELSE 
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN/12*PCK_NOMINA.GL_DOCEAVAS,0);
                END IF;
            END IF;
        ELSIF PCK_NOMINA.GL_FECHAI > TO_DATE('30/06/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') THEN
            MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY');    
            MI_N2 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN,MI_FECHAFPN)-PCK_NOMINA.GL_DNT;
            MI_N1 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN,TO_DATE('30/11/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY'))-PCK_NOMINA.GL_DNT1;
            IF PCK_NOMINA.GL_FECHAR IS NOT NULL THEN
                IF PCK_NOMINA.GL_FECHAR < TO_DATE('30/11/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') THEN
                    MI_N2 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN,PCK_NOMINA.GL_FECHAR)-PCK_NOMINA.GL_DNT;
                    MI_N1 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN,PCK_NOMINA.GL_FECHAR)-PCK_NOMINA.GL_DNT1;
                END IF;
                IF PCK_NOMINA.GL_FECHAR > TO_DATE('30/11/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') THEN
                    MI_N2 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN,PCK_NOMINA.GL_FECHAR)-PCK_NOMINA.GL_DNT;   
                END IF;
            END IF;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO,PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN),CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN 1 ELSE 2 END,PCK_NOMINA.GL_SANO,12,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF PCK_NOMINA.FC_CNA(155) = 0 THEN
                PCK_NOMINA.GL_PVAC := 0;
            ELSE
                PCK_NOMINA.GL_PVAC := 0;
            END IF;
            PCK_NOMINA.CN(942) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(70)/12,0);
            PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)/12,0)+PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(159)/12,0)+PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(160)/12,0);
            IF PCK_PARENTR.PARAMETRO31 = '800016757-9' THEN
                PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO,PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN),CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN 1 ELSE 2 END,PCK_NOMINA.GL_SANO,12,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(160)/12+PCK_NOMINA.FC_CN(160)/12,0);
            END IF;
            PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC/12,0);    
            PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)/12,0);
            PCK_NOMINA.GL_FACTORPN := PCK_NOMINA.FC_CN(1)+PCK_NOMINA.FC_CN(942)+PCK_NOMINA.GL_GRPNGV+PCK_NOMINA.GL_AUXT+PCK_NOMINA.GL_VPA+PCK_NOMINA.GL_AUXA+PCK_NOMINA.GL_VPT+PCK_NOMINA.FC_CN(931)+(PCK_NOMINA.GL_PVAC/12)+PCK_NOMINA.FC_CN(939);
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN,MI_FECHAFPN);  
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO,i,1,PCK_NOMINA.GL_SANO,i,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(359)+PCK_NOMINA.FC_CNA(356)+PCK_NOMINA.FC_CNA(357)+PCK_NOMINA.FC_CNA(339)+PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS-1;
                    PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938)+(PCK_NOMINA.FC_CNA(359)+PCK_NOMINA.FC_CNA(356)+PCK_NOMINA.FC_CNA(357)+PCK_NOMINA.FC_CNA(339)+PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            IF PCK_NOMINA.FC_CN(937) > 0 THEN
                PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937)/30);
            END IF;
            PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS',' ') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN,MI_FECHAFPN)-PCK_NOMINA.GL_DNT;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN/360*PCK_NOMINA.GL_DCC,0);
                END IF;
                IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(404) <> 0 THEN
                    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO,12,1,PCK_NOMINA.GL_SANO,12,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);   
                    PCK_NOMINA.CN(504) := CASE WHEN PCK_NOMINA.FC_CN(504) = 0 THEN PCK_NOMINA.FC_CN(158)-PCK_NOMINA.FC_CNA(158) ELSE PCK_NOMINA.FC_CN(504) END;
                    PCK_NOMINA.CN(158) := 0;
                END IF;
            ELSE 
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN/12*PCK_NOMINA.GL_DOCEAVAS,0);
                END IF;
            END IF;
        END IF;
    END IF;
    PCK_NOMINA.GL_PRIMADIC := PCK_NOMINA.FC_CN(158);
    MI_DIASPRIMADIC := PCK_NOMINA.GL_DOCEAVAS;
    MI_TRANSPORTELEGAL := PCK_NOMINA.FC_CN(254);
    MI_RETEFUENTE := PCK_NOMINA.FC_CN(125);
    IF PCK_NOMINA.GL_SPER = 4 THEN
        FOR i IN 2..599 LOOP
            IF ((i <> 125) AND (i <> 303) AND (i <> 301) AND (i <> 401) AND (i <> 402) AND (i <> 159) AND (i <> 163) AND (i <> 300) AND (i < 599)) OR (PCK_NOMINA.FC_CN(i) > 0 AND PCK_NOMINA.FC_CN(i) < 1) THEN
                PCK_NOMINA.CN(i) := 0;
            END IF;
        END LOOP;
    END IF; 
    PCK_NOMINA.CN(125) := MI_RETEFUENTE;
    PCK_NOMINA.CN(254) := MI_TRANSPORTELEGAL;
    PCK_NOMINA.CN(158) := PCK_NOMINA.GL_PRIMADIC;
    PCK_NOMINA.CN(67) := PCK_NOMINA.GL_DOCEAVAS;
    PCK_NOMINA.CN(930) := PCK_NOMINA.FC_CN(1);
    PCK_NOMINA.CN(933) := PCK_NOMINA.GL_AUXT;
    PCK_NOMINA.CN(934) := PCK_NOMINA.GL_AUXA;
    PCK_NOMINA.CN(936) := PCK_NOMINA.FC_CN(67) ;
    PCK_NOMINA.CN(937) := PCK_NOMINA.GL_DCC;
    PCK_NOMINA.CN(935) := PCK_NOMINA.GL_GRPNGV+PCK_NOMINA.GL_VPA;
    PCK_NOMINA.CN(940) := PCK_NOMINA.GL_VPT;
    PCK_NOMINA.GL_PN_DIAS := PCK_NOMINA.GL_DCC;
    PCK_NOMINA.GL_PN_BASE := PCK_NOMINA.GL_FACTORPN;
END PR_CALCPRIMANAVIDADUES;

PROCEDURE PR_BASPFECHAEMPLEADO(
/*
    NAME              : PR_BASPFECHAEMPLEADO
    AUTHORS           : SYSMAN  SAS, JM 
*/
    UN_COMPANIA    IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO         IN PCK_SUBTIPOS.TI_ANIO,
    UN_SALARIO     IN PCK_SUBTIPOS.TI_DOBLE,
    UN_FECHA       IN DATE
)
AS
    MI_PORCENTAJES  PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_PORCENTAJEI  PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_TOPESALARIAL PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN

    BEGIN
        SELECT LIMITE_SUPERIOR, LIMITE_INFERIOR, TOPE_SALARIAL
        INTO   MI_PORCENTAJES,MI_PORCENTAJEI, MI_TOPESALARIAL
        FROM   CONFIGURACION_PORC_BASP
        WHERE  COMPANIA =UN_COMPANIA
        AND    ANO = UN_ANO
        AND  PCK_NOMINA.GL_FECHAFIN1 BETWEEN INICIO_VIGENCIA AND FINAL_VIGENCIA;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            RAISE PCK_EXCEPCIONES.EXC_NOMINA;
            MI_MSGERROR(1).CLAVE := 'VIGENCIA';
            MI_MSGERROR(1).VALOR := UN_ANO;
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE, UN_ERROR_COD=>PCK_ERRORES.ERR_CONFIG_PORC_BASPE, UN_REEMPLAZOS => MI_MSGERROR);
        END ;
         
        IF UN_SALARIO  >  MI_TOPESALARIAL THEN 
            PCK_NOMINA.GL_PORC_BASP := MI_PORCENTAJEI;
        ELSE 
            PCK_NOMINA.GL_PORC_BASP := MI_PORCENTAJES;
        END IF;

END PR_BASPFECHAEMPLEADO;

PROCEDURE PR_VERIFICAR_FECHA_BASP
/*
    NAME              : PR_VERIFICAR_FECHA_BASP
    AUTHORS           : SYSMAN  SAS, JM CC 3741
*/
AS

BEGIN
    IF NVL(PCK_SYSMAN_UTL.FC_PAR(PCK_NOMINA.GL_COMPANIA,'CALCULAR FECHA BASP EN BASE A FECHA DE INGRESO',6,SYSDATE),'NO') = 'SI' AND  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NULL THEN 
            PCK_NOMINA.GL_SANO5 := PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO);
            PCK_NOMINA.GL_SMES5 := PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO);
            PCK_NOMINA.GL_FECB := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO;
            PCK_NOMINA.GL_FECB := TO_DATE(PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECB) ||'/' ||PCK_NOMINA.GL_SMES5 ||'/' ||(PCK_NOMINA.GL_SANO5+1),'DD/MM/YYYY');
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_SANO5, PCK_NOMINA.GL_SMES5, 1 , PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.GL_SMES5 = PCK_NOMINA.GL_SMES OR (PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(339)) >= 30, (PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(339)), 0);
            PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO := TO_DATE(PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO) ||'/' ||PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECB) ||'/' ||PCK_NOMINA.GL_SANO,'DD/MM/YYYY') + NVL(PCK_NOMINA.GL_LICENCIAS,0) ;
            
            IF PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO) = 31 THEN 
                PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO + 1;
            END IF;
            PCK_NOMINA.GL_FECB := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO;
        END IF;

END PR_VERIFICAR_FECHA_BASP;

PROCEDURE PR_INCLUIRNOVEDADSAUE
/*
    NAME              : PR_INCLUIRNOVEDADSAUE
    AUTHORS           : SYSMAN  SAS, JM CC 3810
    insertar novedades siempre a un empleado 
  */
  (
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_IDEMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_IDCONCEPTO IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO,
    UN_VALOR      IN PCK_SUBTIPOS.TI_DOBLE,
    UN_OBSERV     IN VARCHAR2, --(4000)
    UN_USER       IN PCK_SUBTIPOS.TI_USUARIO         DEFAULT PCK_CONEXION.FC_GETUSER()
  )
  AS
    MI_VLR          NUMBER:=0;
    MI_CAMPOS       VARCHAR2(32000);
    MI_VALORES      VARCHAR2(32000);
    MI_CONDICION    VARCHAR2(32000);
    MI_TABLA        VARCHAR2(32000);
    MI_USING        VARCHAR2(32000);
    MI_MERGEENLACE  VARCHAR2(32000);
    MI_MERGEEXISTE  VARCHAR2(32000);
    MI_MERGENOEXIS  VARCHAR2(32000);
    MI_ERROR_FUN    NUMBER:=GL_ERROR_NUM + 22;
    MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
    IF UN_IDEMPLEADO IS NULL OR UN_IDEMPLEADO = '' THEN RETURN; END IF;
    IF UN_IDCONCEPTO IS NULL OR UN_IDCONCEPTO = '' THEN RETURN; END IF;

        BEGIN
            MI_TABLA := 'NOVEDADES';
            MI_USING := ' SELECT ''' || UN_COMPANIA  || ''' COMPANIA,     ' ||
                                      0   || ' ID_DE_PROCESO,  ' ||
                                      0      || ' ANO,            ' ||
                                      0       || ' MES,            ' ||
                                      0   || ' PERIODO,        ' ||
                                      UN_IDEMPLEADO|| ' ID_DE_EMPLEADO, ' ||
                                      UN_IDCONCEPTO|| ' ID_DE_CONCEPTO
                        FROM COMPANIA     ' || '
                        WHERE CODIGO = ''' || UN_COMPANIA  || '''';
            MI_MERGEENLACE := '  TABLA.COMPANIA       = VISTA.COMPANIA
                           AND TABLA.ID_DE_PROCESO  = VISTA.ID_DE_PROCESO
                           AND TABLA.ANO            = VISTA.ANO
                           AND TABLA.MES            = VISTA.MES
                           AND TABLA.PERIODO        = VISTA.PERIODO
                           AND TABLA.ID_DE_EMPLEADO = VISTA.ID_DE_EMPLEADO
                           AND TABLA.ID_DE_CONCEPTO = VISTA.ID_DE_CONCEPTO';

            MI_MERGEEXISTE := ' UPDATE SET TABLA.VALOR = ' || UN_VALOR||
                                        ', TABLA.FECHA = ' || PCK_SYSMAN_UTL.FC_SDATE(SYSDATE);
                                        
            MI_MERGEEXISTE := MI_MERGEEXISTE || ', TABLA.DATE_MODIFIED = ' || PCK_SYSMAN_UTL.FC_SDATE(SYSDATE) ||
                                              ', TABLA.MODIFIED_BY = ''' || UN_USER  || '''';
                                              
            MI_MERGEEXISTE := MI_MERGEEXISTE || ', TABLA.OBSERVACIONES = ''' || UN_OBSERV  || '''';
                                              
            MI_MERGENOEXIS := ' INSERT(COMPANIA, ID_DE_PROCESO, ANO, MES, PERIODO, ID_DE_EMPLEADO, ID_DE_CONCEPTO, VALOR, FECHA, CREATED_BY, DATE_CREATED, OBSERVACIONES) ' || '
                              VALUES(''' || UN_COMPANIA || ''', ' || 0 || ',' || 0 || ',' || 0 || ', '|| 0 || ', ' || UN_IDEMPLEADO || ',
                              ' || UN_IDCONCEPTO || ',' || UN_VALOR || ',' || PCK_SYSMAN_UTL.FC_SDATE(SYSDATE) || ', ''' || UN_USER || ''', ' || PCK_SYSMAN_UTL.FC_SDATE(SYSDATE) || ', ''' || UN_OBSERV || ''')
                              ';
            BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA,
                                                      UN_ACCION      => 'IM',
                                                      UN_MERGEUSING  => MI_USING,
                                                      UN_MERGEENLACE => MI_MERGEENLACE,
                                                      UN_MERGEEXISTE => MI_MERGEEXISTE,
                                                      UN_MERGENOEXIS => MI_MERGENOEXIS);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                RAISE PCK_EXCEPCIONES.EXC_NOMINA;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
            MI_MSGERROR(1).CLAVE := 'PERSONAL';
            MI_MSGERROR(1).VALOR :=  UN_IDEMPLEADO;
            MI_MSGERROR(2).CLAVE := 'CONCEPTO';
            MI_MSGERROR(2).VALOR :=  UN_IDCONCEPTO;
            MI_MSGERROR(3).CLAVE := 'VALOR';
            MI_MSGERROR(3).VALOR :=  UN_VALOR;
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                      UN_ERROR_COD => PCK_ERRORES.ERRRR_INCLUIRNOVEDAD ,
                                      UN_REEMPLAZOS => MI_MSGERROR);
        END;

END PR_INCLUIRNOVEDADSAUE;
END PCK_NOMINA_COM3;