create or replace PACKAGE BODY "PCK_CHIPFUT1" AS

--1
FUNCTION FC_GENERARPLANOSGRINGRESOS(
      
      /*
      NAME              : FC_GENERARPLANOSGRINGRESOS 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JONATHAN LEONARDO MALAVER JIMÉNEZ
      DATE MIGRADOR     : 19/07/2018
      TIME              : 03:00 PM
      SOURCE MODULE     : SysmanChip2018.04.04
      DESCRIPTION       : Archivo plano de Ingresos Regalías fut. 
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     :                                 
      PARAMETERS        :   UN_COMPANIA 	      => Compania que ingreso a la aplicación
                            UN_ANO              => Año que selecciono en la aplicación
                            UN_CODIGOENTIDAD    => Codigo de la entidad que se selecciona en la aplicación,
                            UN_MES              => Mes seleccionado en la aplicación,
                            UN_TRIMESTRE        => Trimestre seleccionado en la aplicación,
                            UN_MILES            => Check para saber si se genera el informe con miles o no,



    @NAME:   generarArchivoPlanoIngresosRegaliasFut
    @METHOD: GET   
    */
      UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_ANO              IN PCK_SUBTIPOS.TI_ANIO,
      UN_CODIGOENTIDAD    IN VARCHAR2,
      UN_MES              IN PCK_SUBTIPOS.TI_ENTERO,
      UN_TRIMESTRE        IN PCK_SUBTIPOS.TI_ENTERO,
      UN_MILES            IN PCK_SUBTIPOS.TI_LOGICO,
      UN_EXCEL            IN PCK_SUBTIPOS.TI_LOGICO,
      UN_OPCION           IN PCK_SUBTIPOS.TI_ENTERO   
)
RETURN CLOB
AS
      MI_RTA            CLOB;
      MI_MESFINAL       PCK_SUBTIPOS.TI_ENTERO;
      MI_MESINICIAL     PCK_SUBTIPOS.TI_ENTERO ;
      MI_TIPOENTIDAD    COMPANIA.TIPOENTIDAD%TYPE;
      MI_PARDIGITO      PCK_SUBTIPOS.TI_ENTERO;
      MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
      MI_PARDIGITO := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(
                                    UN_COMPANIA  => UN_COMPANIA	  ,
                                    UN_NOMBRE    => 'DIGITO REDONDEO DE INFORMES FUT' ,
                                    UN_MODULO 	 => PCK_DATOS.FC_MODULOENTESDECONTROL ,
                                    UN_FECHA_PAR => SYSDATE        
                                ),2));

BEGIN
      BEGIN
              SELECT TIPOENTIDAD
                  INTO MI_TIPOENTIDAD
              FROM COMPANIA
              WHERE CODIGO = UN_COMPANIA;
        EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_REEMPLAZOS(0).CLAVE:='UN_COMPANIA';
                    MI_REEMPLAZOS(0).VALOR:=UN_COMPANIA;
        RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL;
      END;
EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ENTESDECONTROL THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
      UN_EXC_COD      =>SQLCODE,
      UN_ERROR_COD    =>PCK_ERRORES.ERR_ENTES_FALTTIPOENTIDAD,
      UN_REEMPLAZOS   =>MI_REEMPLAZOS);
END;

IF UN_MES = 0 THEN

      IF UN_TRIMESTRE = 1 THEN
          MI_MESFINAL:= 3;
      END IF;
      IF UN_TRIMESTRE = 2 THEN 
         MI_MESFINAL:= 6;
      END IF;
      IF UN_TRIMESTRE = 3 THEN 
          MI_MESFINAL:= 9;
      END IF;
      IF UN_TRIMESTRE = 4 THEN 
          MI_MESFINAL:= 12;
      END IF;

ELSE 
      IF UN_MES = 1 THEN
        MI_MESFINAL := UN_TRIMESTRE;
      ELSE 
        MI_MESFINAL   := UN_TRIMESTRE;
      END IF;
END IF;
IF UN_OPCION = 0 THEN

IF UN_EXCEL = 0 THEN  

      MI_RTA:='S'||CHR(9)||
              UN_CODIGOENTIDAD||CHR(9)||
              '1'||CASE UN_TRIMESTRE WHEN 1 THEN '0103'
                                     WHEN 2 THEN '0406'
                                     WHEN 3 THEN '0709'
                                     WHEN 4 THEN '1012'
              END||CHR(9)||
              UN_ANO||CHR(9)||
              'EJECUCION_DE_INGRESOS_SGR'||CHR(9)||
              TO_CHAR(SYSDATE, 'DD-MM-YYYY')
              ||CHR(13)||CHR(10);
ELSE
           MI_RTA:='D'                          ||CHR(9)||
                   'CODIGOFUT_REGALIAS'         ||CHR(9)||
                   'PRESUPUESTO_TOTAL_SGR'      ||CHR(9)||
                   'RECAUDO_RECURSOS_APROBADOS' 
                   ||CHR(13)||CHR(10);                   
END IF; 

FOR RS IN (
          WITH CONSULTA_BASE AS
  (
    SELECT 
            V_RESUMENPPTO_BASE.COMPANIA,
            V_RESUMENPPTO_BASE.ANO,
            V_PP.CODIGOFUT_REGALIAS,
            V_RESUMENPPTO_BASE.APROPIADO,
            V_RESUMENPPTO_BASE.TRASLADO,
            V_RESUMENPPTO_BASE.ADICION,
            V_RESUMENPPTO_BASE.REDUCCION,
            V_RESUMENPPTO_BASE.TOTALINGRESOS,
            PLAN_PPTAL_CONFIG.DISPONIBILIDAD_INICIAL_FUT
  FROM V_PLAN_PRESUPUESTAL V_PP
  INNER JOIN PLAN_PPTAL_CONFIG
  ON PLAN_PPTAL_CONFIG.COMPANIA       =V_PP.COMPANIA
  AND PLAN_PPTAL_CONFIG.ANO           =V_PP.ANO
  AND PLAN_PPTAL_CONFIG.CODIGO        =V_PP.CODIGO
  AND PLAN_PPTAL_CONFIG.CENTRO_COSTO  =V_PP.CENTRO_COSTO
  AND PLAN_PPTAL_CONFIG.AUXILIAR      =V_PP.AUXILIAR
  AND PLAN_PPTAL_CONFIG.FUENTE_RECURSO=V_PP.FUENTE_RECURSO
  INNER JOIN V_RESUMENPPTO_BASE
  ON V_PP.COMPANIA                  = V_RESUMENPPTO_BASE.COMPANIA
  AND V_PP.ANO                      = V_RESUMENPPTO_BASE.ANO
  AND V_PP.ID                       = V_RESUMENPPTO_BASE.ID
  WHERE V_RESUMENPPTO_BASE.COMPANIA = UN_COMPANIA
  AND V_RESUMENPPTO_BASE.MES BETWEEN 0 AND UN_MES
  AND V_RESUMENPPTO_BASE.ANO        = UN_ANO
  AND V_PP.CODIGOFUT_REGALIAS      IS NOT NULL
  AND V_RESUMENPPTO_BASE.NATURALEZA = 'C'
  ),
CONSULTA_DOS AS( 
 SELECT 
        CONSULTA_BASE.CODIGOFUT_REGALIAS,
        CASE WHEN CONSULTA_BASE.DISPONIBILIDAD_INICIAL_FUT = -1 
          THEN SUM(
                    CASE WHEN UN_MILES = 0
                      THEN (CONSULTA_BASE.APROPIADO + CONSULTA_BASE.ADICION + CONSULTA_BASE.REDUCCION + CONSULTA_BASE.TRASLADO)
                      ELSE PCK_SYSMAN_UTL.FC_ROUND((CONSULTA_BASE.APROPIADO + CONSULTA_BASE.ADICION + CONSULTA_BASE.REDUCCION + CONSULTA_BASE.TRASLADO)/1000,MI_PARDIGITO)
                    END
                  )
          ELSE 0
        END  DISPONIBILIDAD_INICIAL,
        CASE WHEN (CONSULTA_BASE.DISPONIBILIDAD_INICIAL_FUT <> -1 OR CONSULTA_BASE.DISPONIBILIDAD_INICIAL_FUT IS NULL)
          THEN SUM(
                    CASE WHEN UN_MILES = 0
                      THEN (CONSULTA_BASE.APROPIADO + CONSULTA_BASE.ADICION + CONSULTA_BASE.REDUCCION + CONSULTA_BASE.TRASLADO)
                      ELSE PCK_SYSMAN_UTL.FC_ROUND((CONSULTA_BASE.APROPIADO + CONSULTA_BASE.ADICION + CONSULTA_BASE.REDUCCION + CONSULTA_BASE.TRASLADO)/1000,MI_PARDIGITO)
                    END
                  )
          ELSE 0 
        END INGRESOS_INCORPORADOS,
        SUM(
              CASE WHEN UN_MILES = 0
                THEN CONSULTA_BASE.TOTALINGRESOS
                ELSE PCK_SYSMAN_UTL.FC_ROUND((CONSULTA_BASE.TOTALINGRESOS)/1000,MI_PARDIGITO)
              END
            ) AS RECAUDO_RECURSOS_APROBADOS,                     
        CONSULTA_BASE.DISPONIBILIDAD_INICIAL_FUT
  FROM CONSULTA_BASE 
  GROUP BY CONSULTA_BASE.CODIGOFUT_REGALIAS,CONSULTA_BASE.DISPONIBILIDAD_INICIAL_FUT
  ORDER BY CONSULTA_BASE.CODIGOFUT_REGALIAS
)
SELECT 
  CONSULTA_DOS.CODIGOFUT_REGALIAS,
  CONSULTA_DOS.RECAUDO_RECURSOS_APROBADOS,
  (CONSULTA_DOS.DISPONIBILIDAD_INICIAL + CONSULTA_DOS.INGRESOS_INCORPORADOS) AS PRESUPUESTO_TOTAL_SGR
FROM CONSULTA_DOS
)
LOOP
      MI_RTA := MI_RTA
           ||'D'                            ||CHR(9)
           ||RS.CODIGOFUT_REGALIAS          ||CHR(9)
           ||RS.PRESUPUESTO_TOTAL_SGR       ||CHR(9)
           ||RS.RECAUDO_RECURSOS_APROBADOS              
           ||CHR(13)||CHR(10);
END LOOP;

RETURN MI_RTA;

ELSE 

IF UN_EXCEL = 0 THEN  

      MI_RTA:='S'||CHR(9)||
              UN_CODIGOENTIDAD||CHR(9)||
              '1'||CASE UN_TRIMESTRE WHEN 1 THEN '0103'
                                     WHEN 2 THEN '0406'
                                     WHEN 3 THEN '0709'
                                     WHEN 4 THEN '1012'
              END||CHR(9)||
              UN_ANO||CHR(9)||
              'EJECUCION_DE_GASTOS_SGR'||CHR(9)||
              TO_CHAR(SYSDATE, 'DD-MM-YYYY')
              ||CHR(13)||CHR(10);
ELSE
           MI_RTA:='D'                          ||CHR(9)||
                   'CONCEPTO'                   ||CHR(9)||
                   'CODIGO_BPIN_SGR'            ||CHR(9)||
                   'FUENTES_FINANCIACION_SGR'   ||CHR(9)||
                   'SECTOR'                     ||CHR(9)||
                   'NOMBRE_DEL_PROYECTO'        ||CHR(9)||
                   'APROPIACIONES'              ||CHR(9)||
                   'COMPROMISOS_SGR'            ||CHR(9)||
                   'Obligaciones'               ||CHR(9)||
                   'Pagos'   
                   ||CHR(13)||CHR(10);                   
END IF; 

FOR RS IN (
         WITH CONSULTA_BASE AS (
SELECT V_RESUMENPPTO_BASE.COMPANIA,
  V_RESUMENPPTO_BASE.ANO,
  V_PP.CODIGOFUT_REGALIAS                                                                                     AS CODIGOREGALIAS,
  V_PP.SECTOR_REGALIAS                                                                                        AS SECTORREGALIAS,
  V_PP.FUENTE_FUTREGALIAS                                                                                     AS FUENTEREGALIAS,  
  V_RESUMENPPTO_BASE.APROPIADO,  
  V_RESUMENPPTO_BASE.TRASLADO,  
  V_RESUMENPPTO_BASE.ADICION,
  V_RESUMENPPTO_BASE.REDUCCION,  
  (V_RESUMENPPTO_BASE.EJECUCIONPPT - V_RESUMENPPTO_BASE.REINTEGRO)                                                     AS PAGOSF,  
  (V_RESUMENPPTO_BASE.REG_CONTRACT         + V_RESUMENPPTO_BASE.REG_NO_CONTRACT)               AS REGISTROSF,  
  (V_RESUMENPPTO_BASE.REO + V_RESUMENPPTO_BASE.MODIFREO)                                AS REOF,
  V_PP.NOMBRE AS NOMBREPROYECTO,
  PLAN_PPTAL_CONFIG.CODIGO_BPIN_SGR,
  V_RESUMENPPTO_BASE.TIPOVIGENCIA
FROM V_PLAN_PRESUPUESTAL V_PP
  INNER JOIN PLAN_PPTAL_CONFIG
  ON PLAN_PPTAL_CONFIG.COMPANIA       =V_PP.COMPANIA
  AND PLAN_PPTAL_CONFIG.ANO           =V_PP.ANO
  AND PLAN_PPTAL_CONFIG.CODIGO        =V_PP.CODIGO
  AND PLAN_PPTAL_CONFIG.CENTRO_COSTO  =V_PP.CENTRO_COSTO
  AND PLAN_PPTAL_CONFIG.AUXILIAR      =V_PP.AUXILIAR
  AND PLAN_PPTAL_CONFIG.FUENTE_RECURSO=V_PP.FUENTE_RECURSO
  INNER JOIN V_RESUMENPPTO_BASE
  ON V_PP.COMPANIA                  = V_RESUMENPPTO_BASE.COMPANIA
  AND V_PP.ANO                      = V_RESUMENPPTO_BASE.ANO
  AND V_PP.ID                       = V_RESUMENPPTO_BASE.ID
WHERE V_RESUMENPPTO_BASE.COMPANIA         = UN_COMPANIA 
AND V_RESUMENPPTO_BASE.MES BETWEEN 0 AND UN_MES
AND V_RESUMENPPTO_BASE.ANO                 = UN_ANO
AND V_PP.CODIGOFUT_REGALIAS IS NOT NULL
AND V_PP.SECTOR_REGALIAS    IS NOT NULL
AND V_PP.FUENTE_FUTREGALIAS IS NOT NULL
AND V_PP.NATURALEZA          ='D')
SELECT CONSULTA_BASE.CODIGOREGALIAS AS CONCEPTO,
NVL(CONSULTA_BASE.CODIGO_BPIN_SGR,0) AS CODIGO_BPIN_SGR,
CONSULTA_BASE.FUENTEREGALIAS AS FUENTES_FINANCIACION_SGR,
CONSULTA_BASE.SECTORREGALIAS AS SECTOR,
CONSULTA_BASE.NOMBREPROYECTO AS NOMBRE_DEL_PROYECTO,
CASE WHEN UN_MILES = 0
  THEN CONSULTA_BASE.APROPIADO + CONSULTA_BASE.ADICION + CONSULTA_BASE.REDUCCION + CONSULTA_BASE.TRASLADO
  ELSE PCK_SYSMAN_UTL.FC_ROUND((CONSULTA_BASE.APROPIADO + CONSULTA_BASE.ADICION + CONSULTA_BASE.REDUCCION + CONSULTA_BASE.TRASLADO)/1000, MI_PARDIGITO)
END APROPIACIONES, 
CASE WHEN UN_MILES = 0
  THEN CONSULTA_BASE.REGISTROSF
  ELSE PCK_SYSMAN_UTL.FC_ROUND(CONSULTA_BASE.REGISTROSF/1000, MI_PARDIGITO) 
END COMPROMISOS_SGR, 
CASE WHEN UN_MILES = 0
  THEN CONSULTA_BASE.REOF
  ELSE PCK_SYSMAN_UTL.FC_ROUND(CONSULTA_BASE.REOF/1000,MI_PARDIGITO) 
END OBLIGACIONES, 
CASE WHEN UN_MILES = 0
  THEN CONSULTA_BASE.PAGOSF
  ELSE PCK_SYSMAN_UTL.FC_ROUND(CONSULTA_BASE.PAGOSF/1000, MI_PARDIGITO)
END PAGOS
FROM CONSULTA_BASE
WHERE (
        (
          CASE WHEN UN_MILES = 0
            THEN CONSULTA_BASE.APROPIADO
            ELSE PCK_SYSMAN_UTL.FC_ROUND(CONSULTA_BASE.APROPIADO/1000,MI_PARDIGITO)
          END
          +
          CASE WHEN UN_MILES = 0
            THEN CONSULTA_BASE.APROPIADO + CONSULTA_BASE.ADICION + CONSULTA_BASE.REDUCCION + CONSULTA_BASE.TRASLADO
            ELSE PCK_SYSMAN_UTL.FC_ROUND((CONSULTA_BASE.APROPIADO + CONSULTA_BASE.ADICION + CONSULTA_BASE.REDUCCION + CONSULTA_BASE.TRASLADO)/1000,MI_PARDIGITO)
          END 
          +
          CASE WHEN UN_MILES = 0
            THEN CONSULTA_BASE.REGISTROSF
            ELSE PCK_SYSMAN_UTL.FC_ROUND(CONSULTA_BASE.REGISTROSF/1000,MI_PARDIGITO)
          END 
          +
          CASE WHEN UN_MILES = 0
            THEN CONSULTA_BASE.REOF
            ELSE PCK_SYSMAN_UTL.FC_ROUND(CONSULTA_BASE.REOF/1000,MI_PARDIGITO)
          END
          +
          CASE WHEN UN_MILES = 0
            THEN CONSULTA_BASE.PAGOSF
            ELSE PCK_SYSMAN_UTL.FC_ROUND(CONSULTA_BASE.PAGOSF/1000,MI_PARDIGITO)
          END
         ) NOT IN (0)
      )
AND CONSULTA_BASE.TIPOVIGENCIA NOT IN ('RC','RA')
)
LOOP
      MI_RTA := MI_RTA
           ||'D'                          ||CHR(9)
           ||RS.CONCEPTO                  ||CHR(9)
           ||RS.CODIGO_BPIN_SGR           ||CHR(9)
           ||RS.FUENTES_FINANCIACION_SGR  ||CHR(9)
           ||RS.SECTOR                    ||CHR(9)
           ||RS.NOMBRE_DEL_PROYECTO       ||CHR(9)
           ||RS.APROPIACIONES             ||CHR(9)
           ||RS.COMPROMISOS_SGR           ||CHR(9)
           ||RS.OBLIGACIONES              ||CHR(9)
           ||RS.PAGOS         
           ||CHR(13)||CHR(10);
END LOOP;

RETURN MI_RTA;

END IF;



END FC_GENERARPLANOSGRINGRESOS;

--2
 FUNCTION FC_GENERARPLANORESERVASPPTALES 
  /*
    NAME              : FC_GENERARPLANORESERVASPPTALES En Access --> generarplanoInversiones_FUT
    AUTHORS           : STEFANINI SYSMAN  
    AUTHOR MIGRATION  : AURA LILIANA MONROY GARCIA
    DATE MIGRADOR     : 24/07/2018
    TIME              : 04:17 PM
    MODIFIER          : 
    SOURCE MODULE     : ENTES DE CONTROL (99)
    DESCRIPTION       : FUNCION QUE RETORNA UNA CADENA CON LA INFORMACION DE RESERVAS PPTALES.
    PARAMETERS        : UN_COMPANIA         => Compañía de ingreso a la aplicación
                        UN_CODENTIDAD       => Código Chip de la entidad
                        UN_TRIMESTRE        => Trimestre en el que se desea generar el informe
                        UN_ANO              => Año para el que se desea generar el informe
                        UN_TIPOACTOADMTIVO  => Tipo de documento administrativo definido para el informe 
                        UN_NROACTOADMTIVO   => Número de acto administrativo configurado
                        UN_FECHADOCUMENTO   => Fecha en la que se genera el documento administrativo
                        UN_PESOS            => Indicador de Miles de Pesos, usado para aplicar el redondeo en los valores 
                        UN_SEPARADAS        => 
                        UN_EXCEL            => Indica si el formato con el que se va a generar el reporte es en formato Excel
    MODIFICATIONS     : 

    @NAME  : generarPlanoReservasPptales   
    @METHOD: GET
  */
  (
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CODENTIDAD       IN VARCHAR2,
    UN_TRIMESTRE        IN PCK_SUBTIPOS.TI_ENTERO,
    UN_ANO              IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPOACTOADMTIVO  IN PCK_SUBTIPOS.TI_ENTERO,
    UN_NROACTOADMTIVO   IN PCK_SUBTIPOS.TI_ENTERO,
    UN_FECHADOCUMENTO   IN DATE,
    UN_PESOS            IN PCK_SUBTIPOS.TI_LOGICO,
    UN_SEPARADAS        IN PCK_SUBTIPOS.TI_LOGICO,
    UN_EXCEL            IN PCK_SUBTIPOS.TI_LOGICO
  )
  RETURN CLOB 
  AS 
    MI_RTA              CLOB;
    MI_CIFRA_CONTROL    PCK_SUBTIPOS.TI_PARAMETRO;
    MI_ACTOADMTIVOVAL   PCK_SUBTIPOS.TI_PARAMETRO;
    MI_REDONDEO         PCK_SUBTIPOS.TI_ENTERO;
    MI_VALOR_RESERVA    PCK_SUBTIPOS.TI_DOBLE;
    MI_OBLIGACIONES     PCK_SUBTIPOS.TI_DOBLE;
    MI_PAGOS            PCK_SUBTIPOS.TI_DOBLE;
    MI_MESFINAL         PCK_SUBTIPOS.TI_ENTERO;

BEGIN

  IF UN_EXCEL = 0 THEN                                                      
      MI_RTA := 'S'                              
                || CHR(9) || UN_CODENTIDAD
                || CHR(9) || '1' || CASE UN_TRIMESTRE
                                      WHEN 1 THEN '0103'
                                      WHEN 2 THEN '0406'
                                      WHEN 3 THEN '0709'
                                      WHEN 4 THEN '1012'
                                    END                       
                || CHR(9) || UN_ANO
                || CHR(9) || 'REPORTE_RESERVAS_PRESUPUESTALES'
                || CHR(9) || TO_CHAR(SYSDATE, 'DD-MM-YYYY') || CHR(13) || CHR(10);   
  ELSE
     MI_RTA:='D'                          ||CHR(9)||
             'CONCEPTO'                   ||CHR(9)||
             'FUENTE'                     ||CHR(9)||
             'TIPO_ACTO_ADMINISTRATIVO'   ||CHR(9)||
             'NUMERO_ACTO_ADMINISTRATIVO' ||CHR(9)||
             'FECHA_ACTO_ADMINISTRATIVO'  ||CHR(9)||
             'VALOR_RESERVAS_CONSTITUIDAS'||CHR(9)||
             'OBLIGACIONES_POR_RESERVAS'  ||CHR(9)||
             'PAGOS'                      ||CHR(13)||CHR(10);                   
  END IF; 

  MI_CIFRA_CONTROL :=  PCK_SYSMAN_UTL.FC_PAR( UN_COMPANIA  => UN_COMPANIA 
                                             ,UN_NOMBRE    => 'FUENTE FUT PARA CIFRA DE CONTROL VAL'
                                             ,UN_MODULO    => PCK_DATOS.FC_MODULOENTESDECONTROL()
                                             ,UN_FECHA_PAR => SYSDATE
                                             ,UN_IND_MAYUS => 0) ;

  MI_REDONDEO := PCK_SYSMAN_UTL.FC_PAR( UN_COMPANIA  => UN_COMPANIA 
                                             ,UN_NOMBRE    => 'DIGITO REDONDEO DE INFORMES FUT'
                                             ,UN_MODULO    => PCK_DATOS.FC_MODULOENTESDECONTROL()
                                             ,UN_FECHA_PAR => SYSDATE
                                             ,UN_IND_MAYUS => 0) ;             

  MI_ACTOADMTIVOVAL := PCK_SYSMAN_UTL.FC_PAR( UN_COMPANIA  => UN_COMPANIA 
                                             ,UN_NOMBRE    => 'ACTO ADMINISTRATIVO FUT PARA CIFRA DE CONTROL VAL'
                                             ,UN_MODULO    => PCK_DATOS.FC_MODULOENTESDECONTROL()
                                             ,UN_FECHA_PAR => SYSDATE
                                             ,UN_IND_MAYUS => 0) ;    

  MI_MESFINAL := CASE UN_TRIMESTRE
                    WHEN 1 THEN 3
                    WHEN 2 THEN 6
                    WHEN 3 THEN 9
                    WHEN 4 THEN 12
                  END;                                              

  -- Se arma el encabezado
 WITH TMP_RESUMENFINAL AS(
      SELECT 
        V_RESUMENPPTO_BASE.COMPANIA, 
        V_RESUMENPPTO_BASE.ANO, 
        V_RESUMENPPTO_BASE.MES,
        V_PP.CODIGOFUT_H AS FUT,
        V_RESUMENPPTO_BASE.NATURALEZA, 
        V_PP.FUENTE_FUT, 
        V_RESUMENPPTO_BASE.APROPIADO AS APROPIADOPR, 
        V_RESUMENPPTO_BASE.ADICION, 
        V_RESUMENPPTO_BASE.OBLIGACIONESACUM AS REOF,
        V_RESUMENPPTO_BASE.TIPOVIGENCIA,
        V_RESUMENPPTO_BASE.EJECUCIONPPT - V_RESUMENPPTO_BASE.REINTEGRO AS PAGOSF,
        V_RESUMENPPTO_BASE.TRASLADO AS DEFINITIVA, 
        V_RESUMENPPTO_BASE.APLAZAMIENTO,
        V_PP.FUENTEFUT_RESERVA         
      FROM  V_PLAN_PRESUPUESTAL V_PP
                INNER JOIN V_RESUMENPPTO_BASE
           ON V_PP.COMPANIA = V_RESUMENPPTO_BASE.COMPANIA
          AND V_PP.ANO      = V_RESUMENPPTO_BASE.ANO
          AND V_PP.ID   = V_RESUMENPPTO_BASE.ID
      WHERE V_RESUMENPPTO_BASE.COMPANIA     = UN_COMPANIA
         AND V_RESUMENPPTO_BASE.ANO               = UN_ANO
         AND V_RESUMENPPTO_BASE.MES               <= MI_MESFINAL
        AND V_RESUMENPPTO_BASE.TIPOVIGENCIA IN ('RA')
        AND V_PP.NATURALEZA = 'D'
        AND V_PP.FUENTE_FUT IS NOT NULL
  ), RESERVASFUT AS ( 
    SELECT 
      SUM(CASE WHEN UN_PESOS  = 0
               THEN APROPIADOPR + ADICION
               ELSE ROUND((APROPIADOPR + ADICION)/1000, MI_REDONDEO )
          END) AS VALOR_RESERVA_CONSTITUIDA,  
      SUM(CASE WHEN UN_PESOS = 0
               THEN TMP_RESUMENFINAL.REOF
               ELSE ROUND(TMP_RESUMENFINAL.REOF /1000, MI_REDONDEO )
          END) AS OBLIGACIONES,                    
      SUM(CASE WHEN UN_PESOS = 0
               THEN TMP_RESUMENFINAL.PAGOSF
               ELSE ROUND((TMP_RESUMENFINAL.PAGOSF)/1000, MI_REDONDEO )
          END) AS PAGOS   
    FROM TMP_RESUMENFINAL
      INNER JOIN CODIGOSFUT
         ON TMP_RESUMENFINAL.COMPANIA   = CODIGOSFUT.COMPANIA
        AND TMP_RESUMENFINAL.ANO        = CODIGOSFUT.ANO
        AND TMP_RESUMENFINAL.FUT        = CODIGOSFUT.CODIGOFUT
    WHERE TMP_RESUMENFINAL.COMPANIA          = UN_COMPANIA
      AND TMP_RESUMENFINAL.ANO               = UN_ANO
      AND TMP_RESUMENFINAL.MES               <= MI_MESFINAL
      AND TMP_RESUMENFINAL.FUENTE_FUT IS NOT NULL 
    ORDER BY CODIGOSFUT.CODIGOFUT
  )
  SELECT 
    SUM(VALOR_RESERVA_CONSTITUIDA) AS VALOR_RESERVA,
    SUM(OBLIGACIONES)              AS OBLIGACIONES,
    SUM(PAGOS)                     AS PAGOS 
  INTO 
    MI_VALOR_RESERVA,
    MI_OBLIGACIONES,
    MI_PAGOS	
   FROM RESERVASFUT;  

  -- Se agrega el encabezado del plano
    MI_RTA := MI_RTA  || 'D'                              
            || CHR(9) || 'VAL'
            || CHR(9) || MI_CIFRA_CONTROL                    
            || CHR(9) || MI_ACTOADMTIVOVAL
            || CHR(9) || UN_NROACTOADMTIVO
            || CHR(9) || TO_CHAR(UN_FECHADOCUMENTO, 'DD-MM-YYYY')
            || CHR(9) || MI_VALOR_RESERVA            
            || CHR(9) || MI_OBLIGACIONES  
            || CHR(9) || MI_PAGOS || CHR(13) || CHR(10);

    MI_RTA := MI_RTA  || 'D'                              
            || CHR(9) || CASE WHEN UN_SEPARADAS NOT IN (0) THEN 'FRSEP' ELSE 'FRINC' END
            || CHR(9) || MI_CIFRA_CONTROL                    
            || CHR(9) || MI_ACTOADMTIVOVAL
            || CHR(9) || UN_NROACTOADMTIVO
            || CHR(9) || TO_CHAR(UN_FECHADOCUMENTO, 'DD-MM-YYYY')
            || CHR(9) || 0            
            || CHR(9) || 0  
            || CHR(9) || 0 || CHR(13) || CHR(10);
                    

  -- Se arman los detalles del Plano
  <<DETALLES>>
 FOR RS IN (
       
    SELECT 
      CODIGOFUT AS CODIGO,
      NOMBRE,
      TMP_RESUMENFINAL.FUENTE_FUT AS FUENTE,
      SUM(CASE WHEN UN_PESOS  = 0
               THEN APROPIADOPR + ADICION
               ELSE ROUND((APROPIADOPR + ADICION)/1000, MI_REDONDEO )
          END) AS VALOR_RESERVA_CONSTITUIDA,  
      SUM(CASE WHEN UN_PESOS = 0
               THEN TMP_RESUMENFINAL.REOF
               ELSE ROUND(TMP_RESUMENFINAL.REOF /1000, MI_REDONDEO )
          END) AS OBLIGACIONES,                    
      SUM(CASE WHEN UN_PESOS = 0
               THEN TMP_RESUMENFINAL.PAGOSF
               ELSE ROUND((TMP_RESUMENFINAL.PAGOSF)/1000, MI_REDONDEO )
          END) AS PAGOS   
    FROM (
       SELECT 
        V_RESUMENPPTO_BASE.COMPANIA, 
        V_RESUMENPPTO_BASE.ANO, 
        V_RESUMENPPTO_BASE.MES,
        V_PP.CODIGOFUT_H AS FUT,
        V_RESUMENPPTO_BASE.NATURALEZA, 
        V_PP.FUENTE_FUT, 
        CODIGOSFUT.CODIGOFUT,
        CODIGOSFUT.NOMBRE,
        V_RESUMENPPTO_BASE.APROPIADO AS APROPIADOPR, 
        V_RESUMENPPTO_BASE.ADICION, 
        V_RESUMENPPTO_BASE.OBLIGACIONESACUM AS REOF,
        V_RESUMENPPTO_BASE.TIPOVIGENCIA,
        V_RESUMENPPTO_BASE.EJECUCIONPPT - V_RESUMENPPTO_BASE.REINTEGRO AS PAGOSF,
        V_RESUMENPPTO_BASE.TRASLADO AS DEFINITIVA, 
        V_RESUMENPPTO_BASE.APLAZAMIENTO,
        V_PP.FUENTEFUT_RESERVA         
      FROM  V_PLAN_PRESUPUESTAL V_PP        
        INNER JOIN V_RESUMENPPTO_BASE
           ON V_PP.COMPANIA = V_RESUMENPPTO_BASE.COMPANIA
          AND V_PP.ANO      = V_RESUMENPPTO_BASE.ANO
          AND V_PP.ID   = V_RESUMENPPTO_BASE.ID
        INNER JOIN CODIGOSFUT
         ON V_PP.COMPANIA   = CODIGOSFUT.COMPANIA
        AND V_PP.ANO        = CODIGOSFUT.ANO
        AND V_PP.CODIGOFUT_H        = CODIGOSFUT.CODIGOFUT  
      WHERE V_RESUMENPPTO_BASE.COMPANIA     = UN_COMPANIA
           AND V_RESUMENPPTO_BASE.ANO               = UN_ANO
           AND V_RESUMENPPTO_BASE.MES              <= MI_MESFINAL
           AND V_RESUMENPPTO_BASE.TIPOVIGENCIA IN ('RA')
           AND V_PP.NATURALEZA = 'D'
           AND V_PP.FUENTE_FUT IS NOT NULL
    ) TMP_RESUMENFINAL
    GROUP BY CODIGOFUT,
      NOMBRE,
      TMP_RESUMENFINAL.FUENTE_FUT 
    ORDER BY CODIGOFUT
  )
  LOOP

    MI_RTA := MI_RTA  || 'D'                              
            || CHR(9) || RS.CODIGO
            || CHR(9) || RS.FUENTE                    
            || CHR(9) || UN_TIPOACTOADMTIVO
            || CHR(9) || UN_NROACTOADMTIVO
            || CHR(9) || TO_CHAR(UN_FECHADOCUMENTO, 'DD-MM-YYYY')
            || CHR(9) || RS.VALOR_RESERVA_CONSTITUIDA            
            || CHR(9) || RS.OBLIGACIONES  
            || CHR(9) || RS.PAGOS || CHR(13) || CHR(10);      
        
  END LOOP DETALLES;


  RETURN MI_RTA;
  END FC_GENERARPLANORESERVASPPTALES;

 --3 
FUNCTION FC_VERIFICARCONFIGURACION( 
/*
    NAME              : FC_VERIFICARCONFIGURACION En Access --> VERIFICARCONFIGURACION
    AUTHORS           : STEFANINI SYSMAN  
    AUTHOR MIGRATION  : LAURA MELIZA BOTIA PEREZ
    DATE MIGRADOR     : 25/07/2018
    TIME              : 12:11 PM
    MODIFIER          : 
    SOURCE MODULE     : ENTES DE CONTROL (99)
    DESCRIPTION       : 
    PARAMETERS        : UN_COMPANIA         => Compañía de ingreso a la aplicación
                        UN_ANO              => Año que selecciono en el combo de la aplicación
    MODIFICATIONS     : 

    @NAME  : verificarConfiguracion   
    @METHOD: GET
  */      
      UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_ANO                IN  PCK_SUBTIPOS.TI_ANIO 

)     
RETURN CLOB
AS
MI_RTA                  CLOB;

BEGIN
    FOR MI_RS IN (
                  SELECT 
                      V_PLAN_PRESUPUESTAL.ID,
                      PLAN_PPTAL_CONFIG.CODIGOFUT_H,
                      V_PLAN_PRESUPUESTAL.FUT_TRANSFERENCIA
                    FROM PLAN_PPTAL_CONFIG
                      INNER JOIN V_PLAN_PRESUPUESTAL
                      ON  PLAN_PPTAL_CONFIG.COMPANIA    = V_PLAN_PRESUPUESTAL.COMPANIA
                      AND PLAN_PPTAL_CONFIG.ANO         = V_PLAN_PRESUPUESTAL.ANO
                      AND PLAN_PPTAL_CONFIG.CODIGOFUT_H = V_PLAN_PRESUPUESTAL.CODIGOFUT_H 
                      WHERE PLAN_PPTAL_CONFIG.COMPANIA  = UN_COMPANIA
                        AND PLAN_PPTAL_CONFIG.ANO       =UN_ANO
                )

    LOOP    
          MI_RTA := 'Las siguientes inconsistencias se presentan en la Configuración de las cuentas presupuestales.'||CHR(13)||CHR(10);

          MI_RTA := MI_RTA || 'CUENTA'                          ||CHR(9)|| 
                              'CODIGOFUT_H'                     ||CHR(9)||
                              'INDICADOR_TRANSFERENCIA '   ||CHR(13)||CHR(10); 			


          MI_RTA := MI_RTA || 'La cuenta: '             ||CHR(9)|| 
                              MI_RS.ID                  ||CHR(9)|| 
                              MI_RS.CODIGOFUT_H         ||CHR(9)||
                              MI_RS.FUT_TRANSFERENCIA   ||CHR(13)||CHR(10); 

    END LOOP;
		RETURN MI_RTA;



END FC_VERIFICARCONFIGURACION;

--4
PROCEDURE PR_LOADFILE_SEGU_RECIPROCAS
/*
    NAME              : PR_LOADFILE_SEGU_RECIPROCAS
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JEISSON FERNANDO ROJAS COCUNUBO
    DATE MIGRADOR     : 20/11/2018
    TIME              : 03:00 PM  
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : Permite insertar en la tabla SEGUIMIENTO_RECIPROCAS nuevos registros por medio de la lectura de un Excel

      @NAME:    subirSeguimientoReciprocas 
      @METHOD:  POST
  */
(
 UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
 UN_CAMBIOS          IN CLOB,
 UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO,
 UN_CONSECUTIVO      VARCHAR2
)
AS
MI_DATOS_FILA         PCK_SYSMAN_UTL.T_SPLIT;
MI_DATOS_COLUMNAS     PCK_SYSMAN_UTL.T_SPLIT;
MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_CONTADOR           NUMBER (10) := 0;
MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
MI_MES                PCK_SUBTIPOS.TI_MES;
MI_ANIO               PCK_SUBTIPOS.TI_ANIO;
MI_CONSECUTIVO        PCK_SUBTIPOS.TI_ENTERO:=0;
MI_OBSERVACIONES      VARCHAR2(1000 CHAR) :='';
MI_FECHA_OBSERVACIONES DATE ;
MI_ALGO                VARCHAR(32CHAR);
MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
MI_TRIMESTRE           NUMBER;

BEGIN

  MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CAMBIOS,
                                               UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);

  <<CAMBIOS_CODIGO_ELEMENTO>>
  FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST 
  LOOP
    MI_CONTADOR := MI_CONTADOR + 1;
    MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                     UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);



    MI_MES := CASE  UPPER(MI_DATOS_COLUMNAS(2)) 
                WHEN  'PRIMERO' THEN 3
                WHEN  'SEGUNDO' THEN 6
                WHEN  'TERCERO' THEN 9
                WHEN  'CUARTO'  THEN 12
                ELSE 0
              END;

    MI_ANIO := EXTRACT (YEAR FROM SYSDATE);


   MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(
                                     UN_TABLA    => 'SEGUIMIENTO_RECIPROCAS',
                                     UN_CRITERIO => ' COMPANIA = '||UN_COMPANIA||'',
                                     UN_CAMPO    => 'CONSECUTIVO',
                                     UN_INICIAL  => '1');


    MI_TRIMESTRE := CASE  UPPER(MI_DATOS_COLUMNAS(2)) 
                WHEN  'PRIMERO' THEN 1
                WHEN  'SEGUNDO' THEN 2
                WHEN  'TERCERO' THEN 3
                WHEN  'CUARTO'  THEN 4
                ELSE 0
              END;


      MI_CAMPOS:='  COMPANIA
                    ,CODIGO
                    ,TRIMESTRE
                    ,NOMBRE
                    ,CODIGO_ENTIDAD_RECIPROCA
                    ,ENTIDAD_RECIPROCA
                    ,VALOR_CORRIENTE
                    ,VALOR_NO_CORRIENTE
                    ,CONSECUTIVO
                    ,ANO
                    ,MES
                    ,OBSERVACIONES
                    ,FECHA_OBSERVACIONES
                    ,CREATED_BY
                    ,DATE_CREATED';


       MI_VALORES:= ''''||  UN_COMPANIA           ||''',
                    ''' ||  MI_DATOS_COLUMNAS(1)  ||''',
                      ' ||  MI_TRIMESTRE          ||',
                    ''' ||  MI_DATOS_COLUMNAS(3)  ||''',
                    ''' ||  MI_DATOS_COLUMNAS(4)  ||''',
                    ''' ||  MI_DATOS_COLUMNAS(5)  ||''',
                      ' ||  REPLACE(MI_DATOS_COLUMNAS(6),',','.')  ||',
                      ' ||  REPLACE(MI_DATOS_COLUMNAS(7),',','.')  ||',
                    ''' ||  MI_CONSECUTIVO        ||''',
                      ' ||  MI_ANIO               ||',
                      ' ||  MI_MES                ||',
                    ''' ||  MI_OBSERVACIONES      ||''',
                    ''' ||  MI_FECHA_OBSERVACIONES||''',
                    ''' ||  UN_USUARIO        ||''',
                    SYSDATE';
   BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'SEGUIMIENTO_RECIPROCAS'
                                               ,UN_ACCION  => 'I'
                                               ,UN_CAMPOS  => MI_CAMPOS
                                               ,UN_VALORES => MI_VALORES);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
           RAISE PCK_EXCEPCIONES.EXC_ENTESCONTROL;
      END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ENTESCONTROL THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD => PCK_ERRORES.ERR_ALM_INCONSISTENCIASPLACA5
               );
    END;                                


  END LOOP CAMBIOS_CODIGO_ELEMENTO;  
END PR_LOADFILE_SEGU_RECIPROCAS;


FUNCTION FC_VALIDAR_CUENTAS 
/*
    NAME              : FC_VALIDAR_CUENTAS 
    AUTHOR MIGRACION  : BRAYAN SEBASTIAN CARDENAS AGUILAR
    DATE MIGRADOR     : 11/01/2019                                                                                              
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : Comparar si se encuentran configuradas las cuentas del formulario PLANO SALDOS Y MOVIMIENTOS SISTEMA CHIP
    @NAME:    validarCuentas
    @METHOD:  GET
    */
(
    UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_ANO                 IN PCK_SUBTIPOS.TI_ANIO,
    UN_CUENTAEXCEL         IN CLOB
)RETURN CLOB AS 
    MI_MENSAJE                CLOB:='';
    MI_ERRORES                PCK_SUBTIPOS.TI_ENTERO:=0;
    MI_RS                     SYS_REFCURSOR;
    MI_DATOS_FILA             PCK_SYSMAN_UTL.T_SPLIT;
    MI_DATOS_COLUMNAS         PCK_SYSMAN_UTL.T_SPLIT;
    MI_SEPARADOR_COL          VARCHAR2(10);
    MI_SEPARADOR_REG          VARCHAR2(10);
    MI_CUENTA                 PCK_SUBTIPOS.TI_VALORES;
    MI_EXISTE                 PCK_SUBTIPOS.TI_ENTERO_LARGO;
 BEGIN

   MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CUENTAEXCEL,
                                               UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);

        MI_MENSAJE:=MI_MENSAJE||'**** CUENTAS EXISTENTES EN PLANTILLA ****';
            MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);
            MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);


        <<REGISTROS>>
        FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST 
          LOOP                            
            BEGIN                                          
                MI_CUENTA      :=  MI_DATOS_FILA(RS);

                SELECT CODIGO
                  INTO MI_CUENTA
                  FROM PLAN_CONTABLE 
                WHERE COMPANIA = UN_COMPANIA
                  AND ANO      = UN_ANO
                  AND CODIGO   = MI_CUENTA;


               EXCEPTION WHEN NO_DATA_FOUND THEN

                   BEGIN 
                     MI_MENSAJE:=MI_MENSAJE||'El numero de cuenta: '||MI_CUENTA|| ' no se encuentra en base de datos.'||CHR(10)||CHR(13);
                     MI_ERRORES:=MI_ERRORES+1;
                   END; 
            END;

          END LOOP REGISTROS;

           MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);
                    MI_MENSAJE:=MI_MENSAJE||'Proceso terminado ';
                    MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);
                    MI_MENSAJE:=MI_MENSAJE||'******************** FIN DEL INFORME ********************'||CHR(10)||CHR(13);

      IF MI_ERRORES = 0 THEN

      MI_MENSAJE := 'FALSE';

      END IF;



  RETURN MI_MENSAJE;
 END FC_VALIDAR_CUENTAS;

FUNCTION FC_ENVIARFORMATOESPECIAL 
/*
    NAME              : FC_ENVIARFORMATOESPECIAL
    AUTHOR MIGRACION  : BRAYAN SEBASTIAN CARDENAS AGUILAR
    DATE MIGRADOR     : 11/01/2019                                                                                              
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : Actualizar informacion de saldos PLANO SALDOS Y MOVIMIENTOS SISTEMA CHIP
    @NAME:    enviarFormatoEspecial
    @METHOD:  GET
    */
(
    UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_ANO                 IN PCK_SUBTIPOS.TI_ANIO,
    UN_TRIMESTRE           IN PCK_SUBTIPOS.TI_ENTERO,
    UN_CUENTASEXCEL        IN CLOB
)
RETURN CLOB AS 
 MI_SALDOS_FILA            PCK_SYSMAN_UTL.T_SPLIT;
 MI_CUENTAS_FILA           PCK_SYSMAN_UTL.T_SPLIT;
 MI_SALDOS_COLUMNAS        PCK_SYSMAN_UTL.T_SPLIT;
 MI_CUENTA                 PCK_SUBTIPOS.TI_CODIGOCONTA;
 MI_CAMPO1                 VARCHAR2(1000);
 MI_CAMPO2                 VARCHAR2(1000);
 MIRS_CODIGO               PCK_SUBTIPOS.TI_CODIGOCONTA;
 MIRS_NOMBRE               VARCHAR2(100);
 MIRS_SALDOINICIAL         PCK_SUBTIPOS.TI_DOBLE;
 MIRS_SALDOFINAL           PCK_SUBTIPOS.TI_DOBLE;
 MIRS_DEBITO               PCK_SUBTIPOS.TI_DOBLE;
 MIRS_CREDITO              PCK_SUBTIPOS.TI_DOBLE;
 MIRS_SALDOCORRIENTE           PCK_SUBTIPOS.TI_DOBLE;
 MIRS_SALDONOCORRIENTE       PCK_SUBTIPOS.TI_DOBLE;
 MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
 MI_RTA                    CLOB :=' ';
 MI_CONTADOR               PCK_SUBTIPOS.TI_ENTERO := 0 ;
BEGIN

 MI_CUENTAS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CUENTASEXCEL,
                                               UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);



  <<SALDOS>>
  FOR RS IN MI_CUENTAS_FILA.FIRST..MI_CUENTAS_FILA.LAST 
  LOOP                       

     IF LENGTH(MI_CUENTAS_FILA(RS)) <= 6 THEN
     BEGIN
            SELECT
                  CODIGO,
                  NOMBRE,
                  SUM(CASE UN_TRIMESTRE 
                        WHEN 1 THEN SALDO0
                        WHEN 2 THEN SALDO3
                        WHEN 3 THEN SALDO6
                        WHEN 4 THEN SALDO9
                      END) SALDOINICIAL,
                  SUM(CASE UN_TRIMESTRE
                        WHEN 1 THEN DEBITO1 + DEBITO2 + DEBITO3
                        WHEN 2 THEN DEBITO4 + DEBITO5 + DEBITO6
                        WHEN 3 THEN DEBITO7 + DEBITO8 + DEBITO9
                        WHEN 4 THEN DEBITO10 + DEBITO11 + DEBITO12
                      END) DEBITO,
                  SUM(CASE UN_TRIMESTRE 
                        WHEN 1 THEN CREDITO1 + CREDITO2 + CREDITO3
                        WHEN 2 THEN CREDITO4 + CREDITO5 + CREDITO6
                        WHEN 3 THEN CREDITO7 + CREDITO8 + CREDITO9
                        WHEN 4 THEN CREDITO10 + CREDITO11 + CREDITO12
                      END) CREDITO,
                  SUM(CASE UN_TRIMESTRE  
                        WHEN 1 THEN SALDO3  
                        WHEN 2 THEN SALDO6 
                        WHEN 3 THEN SALDO9 
                        WHEN 4 THEN SALDO12 
                      END ) SALDOFINAL,

                  SUM(CASE WHEN CORRIENTE IN (0) THEN
                           CASE UN_TRIMESTRE  
                            WHEN 1 THEN SALDO3  
                            WHEN 2 THEN SALDO6 
                            WHEN 3 THEN SALDO9 
                            WHEN 4 THEN SALDO12 
                           END 
                        ELSE 0
                      END ) SALDOCORRIENTE,
                  SUM(CASE WHEN CORRIENTE NOT IN (0) THEN
                          CASE UN_TRIMESTRE  
                                WHEN 1 THEN SALDO3  
                                WHEN 2 THEN SALDO6 
                                WHEN 3 THEN SALDO9 
                                WHEN 4 THEN SALDO12 
                          END 
                       ELSE 0
                      END) SALDONOCORRIENTE
                  INTO MIRS_CODIGO,
                     MIRS_NOMBRE,
                     MIRS_SALDOINICIAL,
                     MIRS_DEBITO,
                     MIRS_CREDITO,
                     MIRS_SALDOFINAL,
                     MIRS_SALDOCORRIENTE,
                     MIRS_SALDONOCORRIENTE
                FROM PLAN_CONTABLE
                WHERE COMPANIA = UN_COMPANIA
                AND ANO = UN_ANO
                AND CODIGO = MI_CUENTAS_FILA(RS)
                AND LENGTH(CODIGO) <= 6
                GROUP BY CODIGO,
                  NOMBRE,
                  CORRIENTE
                HAVING   SUM(
                  CASE UN_TRIMESTRE 
                    WHEN 1 THEN SALDO0
                    WHEN 2 THEN SALDO3
                    WHEN 3 THEN SALDO6
                    WHEN 4 THEN SALDO9
                  END) NOT IN (0)
                  OR  SUM(
                  CASE UN_TRIMESTRE 
                    WHEN 1 THEN DEBITO1 + DEBITO2 + DEBITO3
                    WHEN 2 THEN DEBITO4 + DEBITO5 + DEBITO6
                    WHEN 3 THEN DEBITO7 + DEBITO8 + DEBITO9
                    WHEN 4 THEN DEBITO10 + DEBITO11 + DEBITO12
                  END) NOT IN (0)
                  OR  SUM(
                  CASE UN_TRIMESTRE 
                    WHEN 1 THEN CREDITO1 + CREDITO2 + CREDITO3
                    WHEN 2 THEN CREDITO4 + CREDITO5 + CREDITO6
                    WHEN 3 THEN CREDITO7 + CREDITO8 + CREDITO9
                    WHEN 4 THEN CREDITO10 + CREDITO11 + CREDITO12
                  END) NOT IN (0)
                  OR SUM(CASE UN_TRIMESTRE  
                    WHEN 1 THEN SALDO3  
                    WHEN 2 THEN SALDO6 
                    WHEN 3 THEN SALDO9 
                    WHEN 4 THEN SALDO12 
                  END ) NOT IN (0)
                ORDER BY CODIGO;

          EXCEPTION WHEN NO_DATA_FOUND THEN
          MIRS_CODIGO := MI_CUENTAS_FILA(RS);
          MIRS_NOMBRE := ' ';
          MIRS_SALDOINICIAL := 0;
          MIRS_DEBITO       := 0;
          MIRS_CREDITO      := 0;
          MIRS_SALDOFINAL   := 0;
          MIRS_SALDOCORRIENTE := 0;
          MIRS_SALDONOCORRIENTE := 0;


     END;  

     END IF;


      MI_RTA :=   MI_RTA || TO_CLOB( 
                  MIRS_CODIGO ||  PCK_DATOS.GL_SEPARADOR_COL || 
                  MIRS_NOMBRE ||  PCK_DATOS.GL_SEPARADOR_COL || 
                  MIRS_SALDOINICIAL ||  PCK_DATOS.GL_SEPARADOR_COL || 
                  MIRS_DEBITO ||  PCK_DATOS.GL_SEPARADOR_COL || 
                  MIRS_CREDITO ||  PCK_DATOS.GL_SEPARADOR_COL || 
                  MIRS_SALDOFINAL ||  PCK_DATOS.GL_SEPARADOR_COL ||
                  MIRS_SALDOCORRIENTE ||  PCK_DATOS.GL_SEPARADOR_COL ||
                  MIRS_SALDONOCORRIENTE ||  PCK_DATOS.GL_SEPARADOR_COL || 
                  PCK_DATOS.GL_SEPARADOR_REG);

  END LOOP SALDOS;

  RETURN MI_RTA;
END FC_ENVIARFORMATOESPECIAL;

FUNCTION FC_CUENTAS_EXISTENTES 
/*
    NAME              : FC_CUENTAS_EXISTENTES 
    AUTHOR MIGRACION  : BRAYAN SEBASTIAN CARDENAS AGUILAR
    DATE MIGRADOR     : 05/03/2019                                                                                              
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : Comparar si se encuentran configuradas las cuentas del formulario PLANO SALDOS Y MOVIMIENTOS SISTEMA CHIP
    @NAME:    cuentasExistentes
    @METHOD:  GET
    */
(
    UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_ANO                 IN PCK_SUBTIPOS.TI_ANIO,
    UN_CUENTAEXCEL         IN CLOB
)RETURN CLOB AS 
    MI_MENSAJE                CLOB:='';
    MI_ERRORES                PCK_SUBTIPOS.TI_ENTERO:=0;
    MI_RS                     SYS_REFCURSOR;
    MI_DATOS_FILA             PCK_SYSMAN_UTL.T_SPLIT;
    MI_DATOS_COLUMNAS         PCK_SYSMAN_UTL.T_SPLIT;
    MI_SEPARADOR_COL          VARCHAR2(10);
    MI_SEPARADOR_REG          VARCHAR2(10);
    MI_CUENTA                 PCK_SUBTIPOS.TI_VALORES;
    MI_CUENTA_EXCEL           PCK_SUBTIPOS.TI_VALORES;
    MI_NOMBRE                 PCK_SUBTIPOS.TI_VALORES;
    MI_EXISTE                 PCK_SUBTIPOS.TI_ENTERO_LARGO:=0;
    INDIGUALES                PCK_SUBTIPOS.TI_ENTERO_LARGO:=0;
 BEGIN


   MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CUENTAEXCEL,
                                               UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);

        MI_MENSAJE:=MI_MENSAJE||'**** CUENTAS EXISTENTES EN BASE DE DATOS ****';
            MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);
            MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);



        FOR RS  IN (SELECT CODIGO,
                           NOMBRE  
                    FROM PLAN_CONTABLE 
                    WHERE COMPANIA = UN_COMPANIA
                    AND ANO = UN_ANO
                    ORDER BY CODIGO)
          LOOP 


  FOR MI_RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST
          LOOP
           MI_CUENTA      :=  MI_DATOS_FILA(MI_RS);


           BEGIN                                          
                   IF RS.CODIGO = MI_CUENTA THEN
                    INDIGUALES := -1;
                   EXIT;
                    END IF;
            END;

         END LOOP FILA;     
          IF INDIGUALES = 0 THEN
           BEGIN 
                     MI_MENSAJE:=MI_MENSAJE||TO_CLOB('El número de cuenta: '''||RS.CODIGO || ' => ' || RS.NOMBRE  ||''' no se encuentra en la plantilla.') ;
                     MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);
                     MI_ERRORES:=MI_ERRORES+1;
            END; 

          END IF;
          INDIGUALES:=0;
         END LOOP REGISTROS;

           MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);
                    MI_MENSAJE:=MI_MENSAJE||'Proceso terminado ';
                    MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);
                    MI_MENSAJE:=MI_MENSAJE||'******************** FIN DEL INFORME ********************'||CHR(10)||CHR(13);

      IF MI_ERRORES = 0 THEN

      MI_MENSAJE := 'FALSE';

      END IF;



  RETURN MI_MENSAJE;
END FC_CUENTAS_EXISTENTES;

FUNCTION FC_GENERARPROCESOSIA
/*
  NAME              : FC_GENERARPROCESOSIA
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRATION  : CAMILO ANDRES PEREZ DUEÑAS 
  DATE MIGRATION    : 08/06/2022
  TIME              : 04:00 PM
  SOURCE MODULE     : SIA (6)
  DESCRIPTION       : Devuelve un string concatenado  de una consulta enviada por parámetro  la consulta debe tener una sola columna 

 @NAME: FC_GENERARPROCESOSIA
*/
( 
 UN_STRSQL CLOB,
 UN_IND NUMBER DEFAULT 0
)
RETURN CLOB
AS
  MI_RESPUESTA       CLOB := '';
  RESULTSET SYS_REFCURSOR;
  TYPE DEMO_RECTYPE IS RECORD ( CADENA VARCHAR2(2500) );
  RECORDSET DEMO_RECTYPE;
BEGIN
    OPEN RESULTSET FOR UN_STRSQL ;
    LOOP
        FETCH RESULTSET INTO RECORDSET;
        EXIT WHEN RESULTSET%NOTFOUND;

        IF UN_IND = 1 THEN
            -- Reemplazar caracteres especiales y comas
            MI_RESPUESTA := MI_RESPUESTA ||
                       REPLACE(
                           REPLACE(
                               REPLACE(
                                   REPLACE(
                                       REPLACE(
                                           REPLACE(
                                               REPLACE(
                                                   REPLACE(
                                                       REPLACE(
                                                           REPLACE(
                                                               REPLACE(
                                                                   REPLACE(RECORDSET.CADENA, 'á', 'a'),
                                                                   'é', 'e'),
                                                               'í', 'i'),
                                                           'ó', 'o'),
                                                       'ú', 'u'),
                                                   'Á', 'A'),
                                               'É', 'E'),
                                           'Í', 'I'),
                                       'Ó', 'O'),
                                   'Ú', 'U'),
                               'Ñ', 'N'),
                           ',', '') || CHR(13) || CHR(10);
        ELSE
   		     MI_RESPUESTA :=  MI_RESPUESTA  || REPLACE (RECORDSET.CADENA, ',' , '') || CHR(13) || CHR(10);
        END IF;
    END LOOP;
    CLOSE RESULTSET;
    RETURN MI_RESPUESTA;
END FC_GENERARPROCESOSIA;

END PCK_CHIPFUT1;