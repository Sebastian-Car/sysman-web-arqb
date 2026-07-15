WITH FACTORESBONIFICACION AS (SELECT     HISTORICOS.COMPANIA , 
                                         HISTORICOS.ID_DE_EMPLEADO , 
                                         SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO=001
                                                  THEN HISTORICOS.VALOR
                                                  ELSE 0
                                                  END) SUELDOBON, 
                                         SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO=150
                                                  THEN HISTORICOS.VALOR
                                                  ELSE 0
                                                  END) BONIFICACION, 
                                         SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO=130
                                                  THEN HISTORICOS.VALOR
                                                  ELSE 0
                                                  END) PORCEN, 
                                         SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO=882
                                                  THEN HISTORICOS.VALOR
                                                  ELSE 0
                                                  END) GREPBON, 
                                         SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO=860
                                           THEN HISTORICOS.VALOR
                                           ELSE 0
                                           END) DIASLICBON, 
                                         MIN(HISTORICOS.FECHA) KEEP (DENSE_RANK FIRST ORDER BY ROWNUM) FECHA
                              FROM HISTORICOS
                              WHERE HISTORICOS.COMPANIA      = s$compania$s
                                AND HISTORICOS.ID_DE_PROCESO = 1 
                                AND HISTORICOS.ANO           = s$anio$s  
                                AND HISTORICOS.MES           = s$mes$s  
                                AND HISTORICOS.PERIODO       = s$periodo$s 
                                AND HISTORICOS.ID_DE_CONCEPTO IN (001,882,150,130)
                                AND (SELECT SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO=150
                                                THEN HISTORICOS.VALOR
                                                  ELSE 0
                                                  END) FROM HISTORICOS) >0
                              GROUP BY HISTORICOS.COMPANIA, HISTORICOS.ID_DE_EMPLEADO
                              )

SELECT DISTINCT PERSONAL.ID_DE_EMPLEADO, 
                PERSONAL.NOMBRECOMPLETO NOMCOMPLETO, 
                PERSONAL.NUMERO_DCTO, 
                PERSONAL.FECHA_DE_INGRESO, 
                CENTRO_COSTO.NOMBRE NOMBRE_CENTRO_DE_COSTO, 
                DEPENDENCIA.NOMBRE, 
                CARGOS.NOMBRE_DEL_CARGO, 
                PERSONAL.ID_CENTRO_DE_COSTO, 
                FACTORESBONIFICACION.SUELDOBON, 
                FACTORESBONIFICACION.BONIFICACION, 
                FACTORESBONIFICACION.PORCEN, 
                FACTORESBONIFICACION.GREPBON, 
                FACTORESBONIFICACION.DIASLICBON, 
                FACTORESBONIFICACION.FECHA,
                (FACTORESBONIFICACION.SUELDOBON+FACTORESBONIFICACION.GREPBON) TOTAL
FROM FACTORESBONIFICACION 
  LEFT JOIN PERSONAL 
    ON  FACTORESBONIFICACION.COMPANIA       = PERSONAL.COMPANIA 
    AND FACTORESBONIFICACION.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
  LEFT JOIN DEPENDENCIA 
    ON  PERSONAL.COMPANIA    = DEPENDENCIA.COMPANIA 
    AND PERSONAL.DEPENDENCIA = DEPENDENCIA.CODIGO 
  LEFT JOIN CENTRO_COSTO 
    ON  PERSONAL.COMPANIA           = CENTRO_COSTO.COMPANIA 
    AND PERSONAL.ID_CENTRO_DE_COSTO = CENTRO_COSTO.CODIGO  
  LEFT JOIN CARGOS 
    ON  PERSONAL.COMPANIA    = CARGOS.COMPANIA 
    AND PERSONAL.ID_DE_CARGO = CARGOS.ID_DE_CARGO
