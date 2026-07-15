create or replace PACKAGE BODY PCK_CONTABILIZAR_NOMINA2 AS
     
 --1  
PROCEDURE PR_REVISARCUENTASPLANCONTABLE ( 
/*
NAME              : En Access REVISARCUENTAS_PLANCONTABLEJD04
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : JOSE CACERES ALVAREZ
DATE MIGRADOR     : 26/03/2018
TIME              : 09:27 AM
SOURCE MODULE     : INTERFACES InterfacesPb2017.12.02
MODIFIER          :
DATE MODIFIED     :
TIME              :
DESCRIPTION       : Interface
PARAMETERS        :
MODIFICATIONS     :

@NAME:revisarCuentasPlancontable
@METHOD:POST
*/ 
 UN_COMPANIANOMINA   IN  PCK_SUBTIPOS.TI_COMPANIA
,UN_COMPANIADS       IN  PCK_SUBTIPOS.TI_COMPANIA
,UN_ANNO             IN  PCK_SUBTIPOS.TI_ANIO
,UN_MESS             IN  PCK_SUBTIPOS.TI_MES
,UN_PERIODOO         IN  HISTORICOS.PERIODO%TYPE
,UN_PROCESO          IN  PERSONAL_HISTORICO.ID_DE_PROCESO%TYPE
,UN_USUARIO          IN  PCK_SUBTIPOS.TI_USUARIO
)
AS
    MI_ANIO                           PCK_SUBTIPOS.TI_ANIO;
    MI_MES                            PCK_SUBTIPOS.TI_MES;
    MI_PERIODO                        HISTORICOS.PERIODO%TYPE;
    MI_COMPANIA                       PCK_SUBTIPOS.TI_COMPANIA;

    MI_RS                             SYS_REFCURSOR;
    MI_MSG                            PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_MANEJAOTROSGRUPOSCNT           PCK_SUBTIPOS.TI_PARAMETRO;
    MI_ID_DE_EMPLEADO                 NUMBER(5,0);
    MI_RTA1                           PCK_SUBTIPOS.TI_LOGICO;
    MI_DIFER                          PCK_SUBTIPOS.TI_DOBLE;
    MI_NETOHIS                        PCK_SUBTIPOS.TI_DOBLE;
    MI_TABLA                          PCK_SUBTIPOS.TI_TABLA;
    MI_CONDICIONACME                  PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS                         PCK_SUBTIPOS.TI_CAMPOS;
    MI_FILAS                          PCK_SUBTIPOS.TI_ENTERO;

BEGIN

    BEGIN

        MI_CONDICIONACME := 'COMPANIA = ''' || UN_COMPANIANOMINA || ''' ';
        MI_FILAS := PCK_DATOS.FC_ACME
            (UN_TABLA     => 'ERRORES'
            ,UN_ACCION    => 'E'
            ,UN_CONDICION => MI_CONDICIONACME);
    END;
    IF(UN_COMPANIANOMINA IS NULL OR UN_ANNO IS NULL OR UN_MESS IS NULL  OR UN_PERIODOO IS NULL OR UN_PROCESO IS NULL OR UN_COMPANIANOMINA ='' OR UN_ANNO ='' OR UN_MESS ='' OR UN_PERIODOO ='' OR UN_PROCESO ='' ) THEN
        RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
        --   EXIT PROCEDURE;--
    END IF;

    MI_MANEJAOTROSGRUPOSCNT := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIANOMINA, UN_NOMBRE => 'MANEJA OTROS GRUPOS CONTABLES EN NOMINA' ,UN_MODULO => PCK_DATOS.MODULONOMINA, UN_FECHA_PAR => SYSDATE ), 'NO');

    MI_ANIO     := UN_ANNO;
    MI_MES      := UN_MESS;
    MI_PERIODO  := UN_PERIODOO;
    MI_COMPANIA := UN_PROCESO;


    BEGIN


        IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIANOMINA, 
                                     UN_NOMBRE => 'REVISION DE CONCEPTOS POR CENTRO DE COSTO' ,
                                     UN_MODULO => PCK_DATOS.MODULONOMINA, 
                                     UN_FECHA_PAR => SYSDATE ), 'NO') = 'NO' THEN
            FOR MI_RS IN
               (SELECT HISTORICOS.COMPANIA AS COMPANIA,
                       HISTORICOS.ID_DE_PROCESO,
                       HISTORICOS.ANO,
                       HISTORICOS.MES,
                       HISTORICOS.PERIODO,
                       CONCEPTOS.CLASE AS CLASE,
                       HISTORICOS.ID_DE_CONCEPTO,
                       SUM(HISTORICOS.VALOR) AS SUMADEVALOR,
                       PERSONAL.GRUPOCONTABLE,
                       CONCEPTOS.CTA_DBT_ADMINISTRACION,
                       CONCEPTOS.CTA_CRD_ADMINISTRACION,
                       CONCEPTOS.CTA_DBT_PRODUCCION,
                       CONCEPTOS.CTA_CRD_PRODUCCION,
                       CONCEPTOS.CTA_DBT_VENTAS,
                       CONCEPTOS.CTA_CRD_VENTAS,
                       CONCEPTOS.CTA_DBT_OTRO,
                       CONCEPTOS.CTA_CRD_OTRO
                FROM HISTORICOS
                    LEFT JOIN CONCEPTOS
                    ON HISTORICOS.COMPANIA        = CONCEPTOS.COMPANIA
                    AND HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO
                    LEFT JOIN PERSONAL
                    ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
                    AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                WHERE  HISTORICOS.COMPANIA    = UN_COMPANIANOMINA
                  AND HISTORICOS.ANO         = UN_ANNO
                  AND HISTORICOS.MES         = UN_MESS
                  AND HISTORICOS.PERIODO     = UN_PERIODOO
                  AND CONCEPTOS.CLASE       IN (3,5,7,8)
                GROUP BY HISTORICOS.COMPANIA,
                         HISTORICOS.ID_DE_PROCESO,
                         HISTORICOS.ANO,
                         HISTORICOS.MES,
                         HISTORICOS.PERIODO,
                         HISTORICOS.ID_DE_CONCEPTO,
                         PERSONAL.GRUPOCONTABLE,
                         CONCEPTOS.CLASE,
                         CONCEPTOS.CTA_DBT_ADMINISTRACION,
                         CONCEPTOS.CTA_CRD_ADMINISTRACION,
                         CONCEPTOS.CTA_DBT_PRODUCCION,
                         CONCEPTOS.CTA_CRD_PRODUCCION,
                         CONCEPTOS.CTA_DBT_VENTAS,
                         CONCEPTOS.CTA_CRD_VENTAS,
                         CONCEPTOS.CTA_DBT_OTRO,
                         CONCEPTOS.CTA_CRD_OTRO
                ORDER BY  CONCEPTOS.CLASE, HISTORICOS.ID_DE_CONCEPTO
            )
            LOOP
                --MSG
                MI_MSG(1).CLAVE := 'CONCEPTO';
                MI_MSG(1).VALOR := MI_RS.ID_DE_CONCEPTO;

                /*
                PCK_NOMINA_COM7.PR_ALERTA
                    (UN_COMPANIA     => MI_RS.COMPANIA
                    ,UN_MENSAJE_COD  => PCK_ERRORES.ERRRR_NOEXISTECATEGORIA
                    ,UN_REEMPLAZOS   => MI_MSG
                    ,UN_PROCESO      => MI_RS.ID_DE_PROCESO
                    ,UN_ANO          => MI_RS.ANO
                    ,UN_MES          => MI_RS.MES
                    ,UN_PERIODO      => MI_RS.PERIODO
                    ,UN_USER         => UN_USUARIO );
                */
                IF MI_RS.CLASE = 3 THEN
                    IF MI_RS.GRUPOCONTABLE = 'A' AND MI_RS.CTA_DBT_ADMINISTRACION IS NULL THEN
                        --El concepto --CONCEPTO-- , no tiene definido la cuenta contable debito en administración y tiene valores en los históricos.
                        PCK_NOMINA_COM7.PR_ALERTA
                            (UN_COMPANIA     => UN_COMPANIANOMINA,
                             UN_MENSAJE_COD  => PCK_ERRORES.ALER_SINDEFCTACONTABLEDEBADMN,
                             UN_REEMPLAZOS   => MI_MSG);
                    ELSE
                        IF MI_RS.CTA_DBT_ADMINISTRACION IS NOT NULL AND MI_RS.GRUPOCONTABLE = 'A' THEN
                            PCK_CONTABILIZAR_NOMINA.PR_VERIFICARCUENTACONTABLE(UN_COMPANIA => MI_RS.COMPANIA,
                            UN_ANO         =>UN_ANNO,
                            UN_CUENTA      =>MI_RS.CTA_DBT_ADMINISTRACION);
                        END IF;
                    END IF;

                    IF MI_RS.GRUPOCONTABLE = 'V' AND MI_RS.CTA_DBT_VENTAS IS NULL THEN
                        PCK_NOMINA_COM7.PR_ALERTA(UN_COMPANIA     => UN_COMPANIANOMINA,
                        UN_MENSAJE_COD  => PCK_ERRORES.ALER_SINDEFCTACONTDEBVENT,
                        UN_REEMPLAZOS   => MI_MSG);
                    ELSE
                        IF MI_RS.CTA_DBT_VENTAS IS NOT NULL AND MI_RS.GRUPOCONTABLE = 'V' THEN
                            PCK_CONTABILIZAR_NOMINA.PR_VERIFICARCUENTACONTABLE(UN_COMPANIA    =>MI_RS.COMPANIA,
                            UN_ANO         =>UN_ANNO,
                            UN_CUENTA      =>MI_RS.CTA_DBT_VENTAS);
                        END IF;
                    END IF;

                    IF MI_RS.GRUPOCONTABLE = 'P' AND MI_RS.CTA_DBT_PRODUCCION IS NULL THEN
                        PCK_NOMINA_COM7.PR_ALERTA(UN_COMPANIA     => UN_COMPANIANOMINA,
                            UN_MENSAJE_COD  => PCK_ERRORES.ALER_SINDEFCTACONTDEBPROD,
                            UN_REEMPLAZOS   => MI_MSG);
                    ELSE
                        IF MI_RS.CTA_DBT_PRODUCCION IS NOT NULL AND MI_RS.GRUPOCONTABLE = 'P' THEN
                            PCK_CONTABILIZAR_NOMINA.PR_VERIFICARCUENTACONTABLE(UN_COMPANIA    =>MI_RS.COMPANIA,
                            UN_ANO         =>UN_ANNO,
                            UN_CUENTA      =>MI_RS.CTA_DBT_PRODUCCION);
                        END IF;
                    END IF;

                    IF MI_RS.GRUPOCONTABLE = 'O' AND MI_RS.CTA_DBT_OTRO IS NULL THEN
                        PCK_NOMINA_COM7.PR_ALERTA(UN_COMPANIA     => UN_COMPANIANOMINA,
                        UN_MENSAJE_COD  => PCK_ERRORES.ALER_SINDEFCTACONTDEBOTRO,
                        UN_REEMPLAZOS   => MI_MSG);
                    ELSE
                        IF MI_RS.CTA_DBT_OTRO IS NOT NULL AND MI_RS.GRUPOCONTABLE = 'O' THEN
                            PCK_CONTABILIZAR_NOMINA.PR_VERIFICARCUENTACONTABLE(UN_COMPANIA    =>MI_RS.COMPANIA,
                            UN_ANO         =>UN_ANNO,
                            UN_CUENTA      =>MI_RS.CTA_DBT_OTRO);
                        END IF;
                    END IF;

                    IF MI_RS.CTA_CRD_ADMINISTRACION IS NOT NULL OR  MI_RS.CTA_CRD_PRODUCCION IS NOT NULL OR  MI_RS.CTA_CRD_VENTAS IS NOT NULL OR  MI_RS.CTA_CRD_OTRO IS NOT NULL THEN
                        PCK_NOMINA_COM7.PR_ALERTA(UN_COMPANIA     => UN_COMPANIANOMINA,
                        UN_MENSAJE_COD  => PCK_ERRORES.ALER_PAGDEFCNTCREELMCNTCR,
                        UN_REEMPLAZOS   => MI_MSG);
                    END IF;
                END IF; --- FIN CLASE 3

                ---- lo que varia es el tipo de cuenta de DBT a CRD
                IF MI_RS.CLASE = 5 THEN
                    IF MI_RS.GRUPOCONTABLE = 'A' AND MI_RS.CTA_CRD_ADMINISTRACION IS NULL THEN
                        PCK_NOMINA_COM7.PR_ALERTA(UN_COMPANIA     => UN_COMPANIANOMINA,
                            UN_MENSAJE_COD  => PCK_ERRORES.ALER_SINDEFCTACONTABLEDEBADMN,
                            UN_REEMPLAZOS   => MI_MSG);
                    ELSE
                        IF MI_RS.CTA_CRD_ADMINISTRACION IS NOT NULL AND MI_RS.GRUPOCONTABLE = 'A' THEN
                            PCK_CONTABILIZAR_NOMINA.PR_VERIFICARCUENTACONTABLE(UN_COMPANIA    =>MI_RS.COMPANIA,
                                UN_ANO         =>UN_ANNO,
                                UN_CUENTA      =>MI_RS.CTA_CRD_ADMINISTRACION);
                        END IF;
                    END IF;

                    IF MI_RS.GRUPOCONTABLE = 'V' AND MI_RS.CTA_CRD_VENTAS IS NULL THEN
                        PCK_NOMINA_COM7.PR_ALERTA(UN_COMPANIA     => UN_COMPANIANOMINA,
                            UN_MENSAJE_COD  => PCK_ERRORES.ALER_SINDEFCTACONTDEBVENT,
                            UN_REEMPLAZOS   => MI_MSG);
                    ELSE
                        IF MI_RS.CTA_CRD_VENTAS IS NOT NULL AND MI_RS.GRUPOCONTABLE = 'V' THEN
                            PCK_CONTABILIZAR_NOMINA.PR_VERIFICARCUENTACONTABLE(UN_COMPANIA    =>MI_RS.COMPANIA,
                                UN_ANO         =>UN_ANNO,
                                UN_CUENTA      =>MI_RS.CTA_CRD_VENTAS);
                        END IF;
                    END IF;

                    IF MI_RS.GRUPOCONTABLE = 'P' AND MI_RS.CTA_CRD_PRODUCCION IS NULL THEN
                        PCK_NOMINA_COM7.PR_ALERTA(UN_COMPANIA     => UN_COMPANIANOMINA,
                            UN_MENSAJE_COD  => PCK_ERRORES.ALER_SINDEFCTACONTDEBPROD,
                            UN_REEMPLAZOS   => MI_MSG);
                    ELSE
                        IF MI_RS.CTA_CRD_PRODUCCION IS NOT NULL AND MI_RS.GRUPOCONTABLE = 'P' THEN
                            PCK_CONTABILIZAR_NOMINA.PR_VERIFICARCUENTACONTABLE(UN_COMPANIA    =>MI_RS.COMPANIA,
                                UN_ANO         =>UN_ANNO,
                                UN_CUENTA      =>MI_RS.CTA_CRD_PRODUCCION);
                        END IF;
                    END IF;

                    IF MI_RS.GRUPOCONTABLE = 'O' AND MI_RS.CTA_CRD_OTRO IS NULL THEN
                        PCK_NOMINA_COM7.PR_ALERTA(UN_COMPANIA     => UN_COMPANIANOMINA,
                            UN_MENSAJE_COD  => PCK_ERRORES.ALER_SINDEFCTACONTDEBOTRO,
                            UN_REEMPLAZOS   => MI_MSG);
                    ELSE
                        IF MI_RS.CTA_CRD_OTRO IS NOT NULL AND MI_RS.GRUPOCONTABLE = 'O' THEN
                            PCK_CONTABILIZAR_NOMINA.PR_VERIFICARCUENTACONTABLE(UN_COMPANIA    =>MI_RS.COMPANIA,
                                UN_ANO         =>UN_ANNO,
                                UN_CUENTA      =>MI_RS.CTA_CRD_OTRO);
                        END IF;
                    END IF;

                    IF MI_RS.CTA_DBT_ADMINISTRACION IS NOT NULL OR  MI_RS.CTA_DBT_PRODUCCION IS NOT NULL OR  MI_RS.CTA_DBT_VENTAS IS NOT NULL OR  MI_RS.CTA_DBT_OTRO IS NOT NULL THEN
                        PCK_NOMINA_COM7.PR_ALERTA(UN_COMPANIA     => UN_COMPANIANOMINA,
                            UN_MENSAJE_COD  => PCK_ERRORES.ALER_PAGDEFCNTCREELMCNTCR,
                            UN_REEMPLAZOS   => MI_MSG);
                    END IF;
                END IF; --- FIN CLASE 5

                IF MI_RS.CLASE = 8 THEN
                    IF MI_RS.GRUPOCONTABLE IS NULL THEN
                        PCK_NOMINA_COM7.PR_ALERTA(UN_COMPANIA     => UN_COMPANIANOMINA,
                            UN_MENSAJE_COD  => PCK_ERRORES.ALER_NODEFGRPCONTPERCNTCONT,
                            UN_REEMPLAZOS   => MI_MSG);
                    ELSE
                        IF MI_RS.GRUPOCONTABLE = 'A' AND (MI_RS.CTA_CRD_ADMINISTRACION IS NULL OR MI_RS.CTA_DBT_ADMINISTRACION IS NULL) THEN
                            PCK_NOMINA_COM7.PR_ALERTA(UN_COMPANIA     => UN_COMPANIANOMINA,
                                UN_MENSAJE_COD  => PCK_ERRORES.ALER_SINDEFCTACONTABLEDEBADMN,
                                UN_REEMPLAZOS   => MI_MSG);
                        ELSE
                            IF MI_RS.GRUPOCONTABLE = 'A' THEN
                                PCK_CONTABILIZAR_NOMINA.PR_VERIFICARCUENTACONTABLE(UN_COMPANIA    =>MI_RS.COMPANIA,
                                    UN_ANO         =>UN_ANNO,
                                    UN_CUENTA      =>MI_RS.CTA_CRD_ADMINISTRACION);

                                PCK_CONTABILIZAR_NOMINA.PR_VERIFICARCUENTACONTABLE(UN_COMPANIA    =>MI_RS.COMPANIA,
                                    UN_ANO         =>UN_ANNO,
                                    UN_CUENTA      =>MI_RS.CTA_DBT_ADMINISTRACION);
                            END IF;
                        END IF;

                        IF MI_RS.GRUPOCONTABLE = 'V' AND (MI_RS.CTA_CRD_VENTAS IS NULL OR MI_RS.CTA_DBT_VENTAS IS NULL) THEN
                            PCK_NOMINA_COM7.PR_ALERTA(UN_COMPANIA     => UN_COMPANIANOMINA,
                                UN_MENSAJE_COD  => PCK_ERRORES.ALER_SINDEFCTACONTDEBVENT,
                                UN_REEMPLAZOS   => MI_MSG);
                        ELSE
                            IF MI_RS.GRUPOCONTABLE = 'V' THEN
                                PCK_CONTABILIZAR_NOMINA.PR_VERIFICARCUENTACONTABLE(UN_COMPANIA    =>MI_RS.COMPANIA,
                                    UN_ANO         =>UN_ANNO,
                                    UN_CUENTA      =>MI_RS.CTA_CRD_VENTAS);
                                PCK_CONTABILIZAR_NOMINA.PR_VERIFICARCUENTACONTABLE(UN_COMPANIA    =>MI_RS.COMPANIA,
                                    UN_ANO         =>UN_ANNO,
                                    UN_CUENTA      =>MI_RS.CTA_DBT_VENTAS);
                            END IF;
                        END IF;

                        IF MI_RS.GRUPOCONTABLE = 'P' AND (MI_RS.CTA_CRD_PRODUCCION IS NULL OR MI_RS.CTA_DBT_PRODUCCION IS NULL) THEN
                            PCK_NOMINA_COM7.PR_ALERTA(UN_COMPANIA     => UN_COMPANIANOMINA,
                            UN_MENSAJE_COD  => PCK_ERRORES.ALER_SINDEFCTACONTDEBPROD,
                            UN_REEMPLAZOS   => MI_MSG);
                        ELSE
                            IF MI_RS.GRUPOCONTABLE = 'P' THEN
                                PCK_CONTABILIZAR_NOMINA.PR_VERIFICARCUENTACONTABLE(UN_COMPANIA    =>MI_RS.COMPANIA,
                                    UN_ANO         =>UN_ANNO,
                                    UN_CUENTA      =>MI_RS.CTA_CRD_PRODUCCION);
                                PCK_CONTABILIZAR_NOMINA.PR_VERIFICARCUENTACONTABLE(UN_COMPANIA    =>MI_RS.COMPANIA,
                                    UN_ANO         =>UN_ANNO,
                                    UN_CUENTA      =>MI_RS.CTA_DBT_PRODUCCION);

                            END IF;
                        END IF;

                        IF MI_MANEJAOTROSGRUPOSCNT = 'SI' THEN
                            IF MI_RS.GRUPOCONTABLE = 'O' AND (MI_RS.CTA_CRD_OTRO IS NULL OR MI_RS.CTA_DBT_OTRO IS NULL) THEN
                                PCK_NOMINA_COM7.PR_ALERTA(UN_COMPANIA     => UN_COMPANIANOMINA,
                                    UN_MENSAJE_COD  => PCK_ERRORES.ALER_SINDEFCTACONTDEBOTRO,
                                    UN_REEMPLAZOS   => MI_MSG);
                            ELSE
                                IF MI_RS.GRUPOCONTABLE = 'O' THEN
                                    PCK_CONTABILIZAR_NOMINA.PR_VERIFICARCUENTACONTABLE(UN_COMPANIA    =>MI_RS.COMPANIA,
                                        UN_ANO         =>UN_ANNO,
                                        UN_CUENTA      =>MI_RS.CTA_CRD_OTRO);
                                    PCK_CONTABILIZAR_NOMINA.PR_VERIFICARCUENTACONTABLE(UN_COMPANIA    =>MI_RS.COMPANIA,
                                        UN_ANO         =>UN_ANNO,
                                        UN_CUENTA      =>MI_RS.CTA_DBT_OTRO);
                                END IF;
                            END IF;
                        END IF;
                    END IF;
                END IF;--- FIN CLASE 8
            END LOOP;
            --
        ELSE
            FOR MI_RS IN
                (SELECT HISTORICOS.COMPANIA AS COMPANIA,
                        HISTORICOS.ID_DE_PROCESO,
                        HISTORICOS.ANO,
                        HISTORICOS.MES,
                        HISTORICOS.PERIODO,
                        CONCEPTOS.CLASE AS CLASE,
                        HISTORICOS.ID_DE_CONCEPTO,
                        SUM(HISTORICOS.VALOR) AS SUMADEVALOR,
                        PERSONAL.GRUPOCONTABLE,
                        CONCEPTO_CENTROCOSTO.CTA_CREDITO,
                        CONCEPTO_CENTROCOSTO.CTA_DEBITO
                FROM HISTORICOS
                    LEFT JOIN CONCEPTO_CENTROCOSTO
                    ON HISTORICOS.COMPANIA        = CONCEPTO_CENTROCOSTO.COMPANIA
                    AND HISTORICOS.ID_DE_CONCEPTO = CONCEPTO_CENTROCOSTO.ID_DE_CONCEPTO
                    LEFT JOIN PERSONAL
                    ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
                    AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                    INNER JOIN CONCEPTOS
                    ON HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO
                    AND HISTORICOS.COMPANIA      = CONCEPTOS.COMPANIA
                WHERE HISTORICOS.COMPANIA          = UN_COMPANIANOMINA
                  AND HISTORICOS.ANO               = UN_ANNO
                  AND HISTORICOS.MES               = UN_MESS
                  AND HISTORICOS.PERIODO           = UN_PERIODOO
                  AND CONCEPTOS.CLASE IN (3,5,7,8)
                GROUP BY HISTORICOS.COMPANIA,
                         HISTORICOS.ID_DE_PROCESO,
                         HISTORICOS.ANO,
                         HISTORICOS.MES,
                         HISTORICOS.PERIODO,
                         CONCEPTOS.CLASE,
                         HISTORICOS.ID_DE_CONCEPTO,
                         PERSONAL.GRUPOCONTABLE,
                         CONCEPTO_CENTROCOSTO.CTA_CREDITO,
                         CONCEPTO_CENTROCOSTO.CTA_DEBITO
                ORDER BY CONCEPTOS.CLASE,HISTORICOS.ID_DE_CONCEPTO
            )
            LOOP
                MI_MSG(1).CLAVE := 'CONCEPTO';
                MI_MSG(1).VALOR := MI_RS.ID_DE_CONCEPTO;
                IF MI_RS.CLASE = 3 THEN
                    IF MI_RS.CTA_DEBITO IS NULL THEN
                        PCK_NOMINA_COM7.PR_ALERTA(UN_COMPANIA     => UN_COMPANIANOMINA,
                            UN_MENSAJE_COD  => PCK_ERRORES.ALER_SINDEFCTACONTDEBCNVALHIS,
                            UN_REEMPLAZOS   => MI_MSG);
                    ELSE
                        IF MI_RS.CTA_DEBITO IS NOT NULL THEN
                            PCK_CONTABILIZAR_NOMINA.PR_VERIFICARCUENTACONTABLE(UN_COMPANIA    =>MI_RS.COMPANIA,
                                UN_ANO         =>UN_ANNO,
                                UN_CUENTA      =>MI_RS.CTA_DEBITO);
                        END IF;
                    END IF;
                END IF; -- FIN DEVENGOS 3

                IF MI_RS.CLASE = 5 THEN
                    IF MI_RS.CTA_CREDITO IS NULL THEN
                        PCK_NOMINA_COM7.PR_ALERTA(UN_COMPANIA     => UN_COMPANIANOMINA,
                            UN_MENSAJE_COD  => PCK_ERRORES.ALER_SINDEFCTACONTCRECNVALHIS,
                            UN_REEMPLAZOS   => MI_MSG);
                    ELSE
                        IF MI_RS.CTA_CREDITO IS NOT NULL THEN
                            PCK_CONTABILIZAR_NOMINA.PR_VERIFICARCUENTACONTABLE(UN_COMPANIA    =>MI_RS.COMPANIA,
                                UN_ANO         =>UN_ANNO,
                                UN_CUENTA      =>MI_RS.CTA_CREDITO);
                        END IF;
                    END IF;
                END IF; -- FIN DEVENGOS 5

                IF MI_RS.CLASE = 8 THEN
                    IF MI_RS.CTA_CREDITO IS NULL OR MI_RS.CTA_DEBITO IS NULL THEN
                        PCK_NOMINA_COM7.PR_ALERTA(UN_COMPANIA     => UN_COMPANIANOMINA,
                            UN_MENSAJE_COD  => PCK_ERRORES.ALER_SINDEFCTACONTPERCTACONT,
                            UN_REEMPLAZOS   => MI_MSG);
                    ELSE
                        IF  MI_RS.CTA_CREDITO IS NULL AND  MI_RS.CTA_DEBITO IS NOT NULL THEN
                            PCK_CONTABILIZAR_NOMINA.PR_VERIFICARCUENTACONTABLE(UN_COMPANIA    =>MI_RS.COMPANIA,
                                UN_ANO         =>UN_ANNO,
                                UN_CUENTA      =>MI_RS.CTA_CREDITO);
                            PCK_CONTABILIZAR_NOMINA.PR_VERIFICARCUENTACONTABLE(UN_COMPANIA    =>MI_RS.COMPANIA,
                                UN_ANO         =>UN_ANNO,
                                UN_CUENTA      =>MI_RS.CTA_DEBITO);
                        END IF;
                    END IF;
                END IF;
            END LOOP;
        END IF;
    END;

    BEGIN
        FOR MI_RS IN
            (SELECT PERSONAL.ID_CENTRO_DE_COSTO
             FROM HISTORICOS
                INNER JOIN CONCEPTOS
                ON HISTORICOS.COMPANIA          = CONCEPTOS.COMPANIA
                AND  HISTORICOS.ID_DE_CONCEPTO   = CONCEPTOS.ID_DE_CONCEPTO
                INNER JOIN PERSONAL
                ON HISTORICOS.COMPANIA          = PERSONAL.COMPANIA
                AND HISTORICOS.ID_DE_EMPLEADO    = PERSONAL.ID_DE_EMPLEADO
                LEFT JOIN CENTRO_COSTO
                ON  PERSONAL.COMPANIA           = CENTRO_COSTO.COMPANIA
                AND  PERSONAL.ID_CENTRO_DE_COSTO = CENTRO_COSTO.CODIGO
             WHERE    HISTORICOS.COMPANIA    = UN_COMPANIANOMINA
               AND HISTORICOS.ANO         = UN_ANNO
               AND HISTORICOS.MES         = UN_MESS
               AND HISTORICOS.PERIODO     = UN_PERIODOO
               AND CONCEPTOS.PORCDC   <> 0
               AND CENTRO_COSTO.CODIGO IS NULL
             GROUP BY PERSONAL.ID_CENTRO_DE_COSTO
        )
        LOOP
            MI_MSG.DELETE;
            MI_MSG(2).CLAVE := 'CENTRO_DE_COSTO';
            MI_MSG(2).VALOR := MI_RS.ID_CENTRO_DE_COSTO;

            /*PCK_NOMINA_COM7.PR_ALERTA
            (UN_COMPANIA     => MI_RS.COMPANIA
            ,UN_MENSAJE_COD  => PCK_ERRORES.ERRRR_NOEXISTECATEGORIA
            ,UN_REEMPLAZOS   => MI_MSG
            ,UN_PROCESO      => MI_RS.ID_DE_PROCESO
            ,UN_ANO          => MI_RS.ANO
            ,UN_MES          => MI_RS.MES
            ,UN_PERIODO      => MI_RS.PERIODO
            ,UN_USER         => UN_USUARIO );
            */
            PCK_NOMINA_COM7.PR_ALERTA(UN_COMPANIA    => UN_COMPANIANOMINA,
                UN_MENSAJE_COD  => PCK_ERRORES.ALER_CTROCSTNOMSINCRECONT,
                UN_REEMPLAZOS   => MI_MSG);
        END LOOP;
    END ;

    BEGIN
        MI_TABLA := 'CONCEPTOS';
        MI_CAMPOS := 'NITDE = ''O''
                     ,MODIFIED_BY       = '''|| UN_USUARIO ||'''
                     ,DATE_MODIFIED     = SYSDATE ';

        MI_CONDICIONACME := ' COMPANIA = '''|| UN_COMPANIANOMINA || '''
                            AND (CONCEPTOS.CLASE    =3
                              OR CONCEPTOS.CLASE  =5
                              OR CONCEPTOS.CLASE  =7
                              OR CONCEPTOS.CLASE  =8)
                            AND CONCEPTOS.NITDE IS NULL';
        BEGIN
            MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                            UN_ACCION    => 'M',
                            UN_CAMPOS    => MI_CAMPOS,
                            UN_CONDICION => MI_CONDICIONACME);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
        --Se presentó error al actualizar los conceptos nitDe en el proceso de contabilizar nómina.
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERR_CONTACONCEPTOSNITO,
                                   UN_TABLAERROR => MI_TABLA);
    END;

    BEGIN
        FOR MI_RS IN
            (SELECT CONCEPTOS.NITDE,
                    CONCEPTOS.TIPODEFONDO,
                    CONCEPTOS.TERCERO
             FROM HISTORICOS
                LEFT JOIN CONCEPTOS
                ON HISTORICOS.COMPANIA       = CONCEPTOS.COMPANIA
                AND HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO
             WHERE     HISTORICOS.COMPANIA     = UN_COMPANIANOMINA
               AND HISTORICOS.ANO          = UN_ANNO
               AND HISTORICOS.MES          = UN_MESS
               AND HISTORICOS.PERIODO      = UN_PERIODOO
               AND CONCEPTOS.CLASE     IN (3,5,7,8)
             GROUP BY CONCEPTOS.NITDE, CONCEPTOS.TIPODEFONDO, CONCEPTOS.TERCERO
        )
        LOOP
            IF MI_RS.NITDE = 'F' THEN
                IF MI_RS.TERCERO IS NOT NULL THEN
                    PCK_CONTABILIZAR_NOMINA.PR_EXISTE_TERCERO(UN_COMPANIA    =>UN_COMPANIANOMINA,
                        UN_TERCERO     =>MI_RS.TERCERO);
                    -----FONDO DE PENSIONES
                ELSE
                    IF MI_RS.TIPODEFONDO = 'AFP' THEN
                        FOR MI_RS1 IN
                            (SELECT CONCEPTOS.TIPODEFONDO,
                                    V_FONDO_DE_PENSIONES.NIT
                             FROM HISTORICOS
                                LEFT JOIN PERSONAL
                                ON HISTORICOS.COMPANIA = PERSONAL.COMPANIA
                                AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                                LEFT JOIN CONCEPTOS
                                ON HISTORICOS.COMPANIA = CONCEPTOS.COMPANIA
                                AND HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO
                                LEFT JOIN V_FONDO_DE_PENSIONES
                                ON PERSONAL.COMPANIA = V_FONDO_DE_PENSIONES.COMPANIA
                                AND PERSONAL.ID_DEL_FONDO = V_FONDO_DE_PENSIONES.ID_DEL_FONDO
                             WHERE HISTORICOS.COMPANIA= UN_COMPANIANOMINA
                               AND HISTORICOS.ANO= UN_ANNO
                               AND HISTORICOS.MES=UN_MESS
                               AND HISTORICOS.PERIODO=UN_PERIODOO
                               AND CONCEPTOS.CLASE IN (3,5,7,8)
                               AND CONCEPTOS.NITDE='F'
                               AND CONCEPTOS.TIPODEFONDO='AFP'
                             GROUP BY CONCEPTOS.TIPODEFONDO, V_FONDO_DE_PENSIONES.NIT
                        )
                        LOOP
                            PCK_CONTABILIZAR_NOMINA.PR_EXISTE_TERCERO(UN_COMPANIA    =>UN_COMPANIANOMINA,
                                UN_TERCERO     =>MI_RS1.NIT);
                        END LOOP;
                    ----FONDO SALUD
                    ELSIF MI_RS.TIPODEFONDO = 'EPS' THEN
                        FOR MI_RS1 IN
                            (SELECT CONCEPTOS.TIPODEFONDO,
                                    V_FONDO_DE_SALUD.NIT
                             FROM HISTORICOS
                                LEFT JOIN PERSONAL
                                ON HISTORICOS.COMPANIA = PERSONAL.COMPANIA
                                AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                                LEFT JOIN CONCEPTOS
                                ON HISTORICOS.COMPANIA = CONCEPTOS.COMPANIA
                                AND HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO
                                LEFT JOIN V_FONDO_DE_SALUD
                                ON PERSONAL.COMPANIA = V_FONDO_DE_SALUD.COMPANIA
                                AND PERSONAL.FONDO_SALUD = V_FONDO_DE_SALUD.FONDO_SALUD
                             WHERE HISTORICOS.COMPANIA= UN_COMPANIANOMINA
                               AND HISTORICOS.ANO= UN_ANNO
                               AND HISTORICOS.MES= UN_MESS
                               AND HISTORICOS.PERIODO= UN_PERIODOO
                               AND CONCEPTOS.CLASE IN (3,5,7,8)
                               AND CONCEPTOS.NITDE='F'
                               AND CONCEPTOS.TIPODEFONDO='EPS'
                             GROUP BY CONCEPTOS.TIPODEFONDO, V_FONDO_DE_SALUD.NIT
                        )
                        LOOP
                            PCK_CONTABILIZAR_NOMINA.PR_EXISTE_TERCERO(UN_COMPANIA    =>UN_COMPANIANOMINA,
                                UN_TERCERO     =>MI_RS1.NIT);
                        END LOOP;
                    ---FONDO DE RIESGOS
                    ELSIF MI_RS.TIPODEFONDO = 'ARL' THEN
                        FOR MI_RS1 IN
                            ( SELECT CONCEPTOS.TIPODEFONDO,
                                    V_FONDO_DE_RIESGOS.NIT
                            FROM HISTORICOS
                                LEFT JOIN PERSONAL
                                ON HISTORICOS.COMPANIA = PERSONAL.COMPANIA
                                AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                                LEFT JOIN CONCEPTOS
                                ON HISTORICOS.COMPANIA = CONCEPTOS.COMPANIA
                                AND HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO
                                LEFT JOIN V_FONDO_DE_RIESGOS
                                ON PERSONAL.COMPANIA = V_FONDO_DE_RIESGOS.COMPANIA
                                AND PERSONAL.FONDO_SALUD = V_FONDO_DE_RIESGOS.FONDO_RIESGOS
                            WHERE HISTORICOS.COMPANIA= UN_COMPANIANOMINA
                              AND HISTORICOS.ANO= UN_ANNO
                              AND HISTORICOS.MES= UN_MESS
                              AND HISTORICOS.PERIODO= UN_PERIODOO
                              AND CONCEPTOS.CLASE IN (3,5,7,8)
                              AND CONCEPTOS.NITDE='F'
                              AND CONCEPTOS.TIPODEFONDO='ARL'
                            GROUP BY CONCEPTOS.TIPODEFONDO, V_FONDO_DE_RIESGOS.NIT
                        )
                        LOOP
                            PCK_CONTABILIZAR_NOMINA.PR_EXISTE_TERCERO(UN_COMPANIA    =>UN_COMPANIANOMINA,
                                UN_TERCERO    =>MI_RS1.NIT);
                        END LOOP;
                    ---  CAJAS DE COMPENSACION
                    ELSIF MI_RS.TIPODEFONDO = 'CCF' THEN
                        FOR MI_RS1 IN
                            (SELECT CONCEPTOS.TIPODEFONDO, V_CAJA_COMPENSACION.NIT
                            FROM   HISTORICOS
                                LEFT JOIN PERSONAL
                                ON HISTORICOS.COMPANIA = PERSONAL.COMPANIA
                                AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                                LEFT JOIN CONCEPTOS
                                ON HISTORICOS.COMPANIA = CONCEPTOS.COMPANIA
                                AND HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO
                                LEFT JOIN V_CAJA_COMPENSACION
                                ON PERSONAL.COMPANIA = V_CAJA_COMPENSACION.COMPANIA
                                AND PERSONAL.FONDO_SALUD = V_CAJA_COMPENSACION.CAJA_COMPENSACION
                            WHERE   HISTORICOS.COMPANIA= UN_COMPANIANOMINA
                              AND HISTORICOS.ANO= UN_ANNO
                              AND HISTORICOS.MES= UN_MESS
                              AND HISTORICOS.PERIODO= UN_PERIODOO
                              AND CONCEPTOS.CLASE IN (3,5,7,8)
                              AND CONCEPTOS.NITDE='F'
                              AND CONCEPTOS.TIPODEFONDO='CCF'
                            GROUP BY  CONCEPTOS.TIPODEFONDO, V_CAJA_COMPENSACION.NIT
                            )
                        LOOP
                            PCK_CONTABILIZAR_NOMINA.PR_EXISTE_TERCERO(UN_COMPANIA    =>UN_COMPANIANOMINA,
                                UN_TERCERO    =>MI_RS1.NIT);
                        END LOOP;

                    ---APORTES VOLUNTARIOS EN PENSION
                    ELSIF MI_RS.TIPODEFONDO = 'APV' THEN
                        FOR MI_RS1 IN
                            ( SELECT CONCEPTOS.TIPODEFONDO, V_FONDO_DE_PENSION_VOL.NIT
                            FROM HISTORICOS
                                LEFT JOIN PERSONAL
                                ON HISTORICOS.COMPANIA = PERSONAL.COMPANIA
                                AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                                LEFT JOIN CONCEPTOS
                                ON HISTORICOS.COMPANIA = CONCEPTOS.COMPANIA
                                AND HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO
                                LEFT JOIN V_FONDO_DE_PENSION_VOL
                                ON PERSONAL.COMPANIA = V_FONDO_DE_PENSION_VOL.COMPANIA
                                AND PERSONAL.FONDO_PENSION_VOL = V_FONDO_DE_PENSION_VOL.ID_DEL_FONDO
                            WHERE HISTORICOS.COMPANIA= UN_COMPANIANOMINA
                              AND HISTORICOS.ANO= UN_ANNO
                              AND HISTORICOS.MES= UN_MESS
                              AND HISTORICOS.PERIODO= UN_PERIODOO
                              AND CONCEPTOS.CLASE IN (3,5,7,8)
                              AND CONCEPTOS.NITDE='F'
                              AND CONCEPTOS.TIPODEFONDO='APV'
                            GROUP BY CONCEPTOS.TIPODEFONDO, V_FONDO_DE_PENSION_VOL.NIT
                        )
                        LOOP
                            PCK_CONTABILIZAR_NOMINA.PR_EXISTE_TERCERO(UN_COMPANIA    =>UN_COMPANIANOMINA,
                            UN_TERCERO    =>MI_RS1.NIT);
                        END LOOP ;
                    END IF;
                END IF;
            ELSE
                IF MI_RS.NITDE = 'E' AND MI_RS.TERCERO IS NOT NULL THEN
                    PCK_CONTABILIZAR_NOMINA.PR_EXISTE_TERCERO(UN_COMPANIA    =>UN_COMPANIANOMINA,
                    UN_TERCERO     =>MI_RS.TERCERO);
                ---EMPLEADOS NOMINA
                ELSIF MI_RS.NITDE ='E' AND MI_RS.TERCERO IS NULL THEN
                    FOR MI_RS1 IN
                        (SELECT CONCEPTOS.TIPODEFONDO,
                        PERSONAL.NUMERO_DCTO,
                        CONCEPTOS.NITDE
                        FROM HISTORICOS LEFT JOIN PERSONAL
                            ON  HISTORICOS.COMPANIA = PERSONAL.COMPANIA
                            AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                            LEFT JOIN CONCEPTOS
                            ON  HISTORICOS.COMPANIA = CONCEPTOS.COMPANIA
                            AND HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO
                        WHERE HISTORICOS.COMPANIA= UN_COMPANIANOMINA
                          AND HISTORICOS.ANO= UN_ANNO
                          AND HISTORICOS.MES= UN_MESS
                          AND HISTORICOS.PERIODO= UN_PERIODOO
                          AND CONCEPTOS.CLASE IN (3,5,7,8)
                          AND CONCEPTOS.NITDE='E'
                        GROUP BY CONCEPTOS.TIPODEFONDO,PERSONAL.NUMERO_DCTO,CONCEPTOS.NITDE
                    )
                    LOOP
                        PCK_CONTABILIZAR_NOMINA.PR_EXISTE_TERCERO(UN_COMPANIA    =>UN_COMPANIANOMINA,
                        UN_TERCERO     =>MI_RS1.NUMERO_DCTO);
                        IF MI_RS.NITDE = 'E' AND MI_RS.TERCERO IS NOT NULL THEN
                            PCK_CONTABILIZAR_NOMINA.PR_EXISTE_TERCERO(UN_COMPANIA    =>UN_COMPANIANOMINA,
                            UN_TERCERO     =>MI_RS.TERCERO);
                        END IF;
                    END LOOP;
                END IF ;
            END IF; --IF NITDE
        END LOOP;-- DE LA CONSULTA GENERAL
    END;--FINALIZA BEGIN


    FOR MI_RS IN
    (
        SELECT HISTORICOS.ID_DE_EMPLEADO,
                SUM (CASE WHEN CONCEPTOS.CLASE = 7
                     THEN HISTORICOS.VALOR ELSE 0 END) AS NETOHIS,
                ROUND (SUM(CASE WHEN CONCEPTOS.CLASE = 3
                        THEN HISTORICOS.VALOR ELSE 0 END )
                    - SUM(CASE WHEN CONCEPTOS.CLASE =  5
                        THEN HISTORICOS.VALOR ELSE 0 END),0)
                    - ROUND (SUM(CASE WHEN CONCEPTOS.CLASE =  7
                        THEN HISTORICOS.VALOR ELSE 0 END ),0) AS DIFER
        --INTO MI_ID_DE_EMPLEADO, MI_NETOHIS, MI_DIFER
        FROM HISTORICOS
            LEFT JOIN CONCEPTOS
            ON HISTORICOS.COMPANIA = CONCEPTOS.COMPANIA
            AND HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO
        WHERE HISTORICOS.COMPANIA= UN_COMPANIANOMINA
          AND HISTORICOS.ANO= UN_ANNO
          AND HISTORICOS.MES= UN_MESS
          AND HISTORICOS.PERIODO= UN_PERIODOO
          AND CONCEPTOS.CLASE IN (3,5,7)
        GROUP BY HISTORICOS.ID_DE_EMPLEADO
        HAVING ((ROUND(SUM(CASE WHEN CONCEPTOS.CLASE = 3
                    THEN HISTORICOS.VALOR ELSE 0 END )
                - SUM(CASE WHEN CONCEPTOS.CLASE = 5
                    THEN HISTORICOS.VALOR ELSE 0 END),0)
                - ROUND (SUM(CASE WHEN CONCEPTOS.CLASE = 7
                    THEN HISTORICOS.VALOR ELSE 0 END ),0)) <>0)
    )
    LOOP
            MI_MSG.DELETE;
            MI_MSG(1).CLAVE :='ID_DE_EMPLEADO';
            MI_MSG(1).VALOR := MI_RS.ID_DE_EMPLEADO;
            PCK_NOMINA_COM7.PR_ALERTA
                (UN_COMPANIA     => UN_COMPANIANOMINA
                ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_EXTDIFNETAPAGRESDEVMNDES
                ,UN_REEMPLAZOS   => MI_MSG);
    END LOOP;



END PR_REVISARCUENTASPLANCONTABLE;

 --2
FUNCTION FC_TRAERCTAPPTAL 
/* 
        NAME              : En Access TraerCtaPptal
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : 
        DATE MIGRADOR     : 21/05/2018
        TIME              : 04:37 PM
        SOURCE MODULE     : INTERFACES InterfacesPb2017.12.02
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Retorna la cuenta presupuestal asignada a una cuenta contable 
        PARAMETERS        :
        MODIFICATIONS     : 
*/
    (  
       UN_COMPANIA               IN  PCK_SUBTIPOS.TI_COMPANIA
      ,UN_CUENTACONTABLE         IN  PCK_SUBTIPOS.TI_CODIGOCONTA
      ,UN_ANO                    IN  PCK_SUBTIPOS.TI_ANIO
    )
RETURN VARCHAR2 

AS
       MI_RTA                   PCK_SUBTIPOS.TI_CODIGOCONTA;

BEGIN
                 SELECT CUENTA_PPTAL
                   INTO MI_RTA
                   FROM PLAN_CONTABLE
                  WHERE COMPANIA= UN_COMPANIA 
                       AND ANO= UN_ANO   
                       AND CODIGO= UN_CUENTACONTABLE 
                       AND CLASECUENTA='G'; 
-- 5805900101             
       IF MI_RTA IS NOT NULL THEN 
          MI_RTA:= MI_RTA;
       ELSE
          MI_RTA:= '';
       END IF;  

  RETURN MI_RTA;

 END FC_TRAERCTAPPTAL; 

END PCK_CONTABILIZAR_NOMINA2;