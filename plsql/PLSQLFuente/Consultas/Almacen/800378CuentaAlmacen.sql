MERGE INTO CONSULTAS FIN USING ( SELECT '800378CuentaAlmacen' AS INFORME_VAL, TO_CLOB(q'[WITH VISTA_CUENTAALMACEN AS (
  SELECT
    COMPANIA
   ,GRUPO
 s$grupoEtiq$s
   ,TIPO TIPO_COD
   ,DECODE(TIPO,'C' ,'Consumo'
               ,'D' ,'Devolutivos'
               ,'E' ,'Recibidos en Comodato'
               ,'EC','Entregados en Comodato'
               ,'M' ,'Consumo Controlado'
               ,'N' ,'ActivosFijos'
               ,'S' ,'Servicios') TIPO
   ,NOMBREBODEGA DEPEND
   ,BODEGA_DESTINO1
   ,CLASEFINAL
   ,NOMBREGRUPO NOMBRE
   ,SUM(CASE WHEN (FECHA < s$fechaInicial$s)
             THEN NVL(VALOR_ENTRADAS,0) -
                  NVL(VALOR_SALIDAS ,0) +
                  NVL(AJUSTESDEBITO ,0) +
                  NVL(AJUSTESCREDITO,0)
             ELSE 0
        END) ANTERIOR
   ,SUM (CASE WHEN FECHA BETWEEN s$fechaInicial$s AND s$fechaFinal$s
              THEN NVL(VALOR_ENTRADAS,0)
              ELSE 0
         END) ENTRADAS
   ,SUM(CASE WHEN FECHA BETWEEN s$fechaInicial$s AND s$fechaFinal$s
             THEN NVL(VALOR_SALIDAS,0)
             ELSE 0
        END) SALIDAS
   ,SUM(CASE WHEN FECHA BETWEEN s$fechaInicial$s AND s$fechaFinal$s
             THEN NVL(AJUSTESDEBITO,0)
             ELSE 0
        END) DEBITO
   ,SUM(CASE WHEN FECHA BETWEEN s$fechaInicial$s AND s$fechaFinal$s
             THEN NVL(AJUSTESCREDITO,0)
             ELSE 0
        END) CREDITO
   ,SUM(CASE WHEN FECHA BETWEEN s$fechaInicial$s AND s$fechaFinal$s
             THEN (NVL(VALOR_ENTRADAS,0) -
                 NVL(VALOR_SALIDAS ,0) +
                   NVL(AJUSTESDEBITO ,0) +
                   NVL(AJUSTESCREDITO,0))
             ELSE 0
             END) SALDOFINAL
  FROM V_BASE_CUENTAALMACEN
  WHERE COMPANIA = s$compania$s
    AND FECHA BETWEEN s$fechaCorte$s AND s$fechaFinal$s
    AND IND_REG NOT IN (0)
    AND CASE
        WHEN s$sinConsumoControlado$s NOT IN (0)
        THEN (TIPO||BODEGA_DESTINO1)
        ELSE ' '
    END NOT IN ('M30')
  GROUP BY
    COMPANIA
   ,GRUPO
   ,TIPO
   ,NOMBREBODEGA
   ,BODEGA_DESTINO1
   ,CLASEFINAL
   ,NOMBREGRUPO
)
SELECT
  s$grupoEtiq$s
 ,TIPO
 ,DEPEND
 DEPENDENCIA
 ,VISTA_CUENTAALMACEN.NOMBRE
 ,CASE WHEN DEPEND IN('PROVEEDORES','DONACIONES','COMODATO')
       THEN ABS(ANTERIOR)
       ELSE ANTERIOR
  END ANTERIOR
 ,ENTRADAS
 ,SALIDAS
 ,DEBITO
 ,CREDITO
 ,CASE WHEN DEPEND IN('PROVEEDORES')
       THEN ABS(CASE WHEN DEPEND IN('PROVEEDORES','DONACIONES','COMODATO')
                      THEN ABS(ANTERIOR)
                      ELSE ANTERIOR
                 END                 +
                 ENTRADAS      -
                 SALIDAS       +
                 DEBITO +
                 CREDITO)
       ELSE (CASE WHEN DEPEND IN('PROVEEDORES','DONACIONES','COMODATO')
                  THEN ABS(ANTERIOR)
                  ELSE ANTERIOR
             END                 +
             ENTRADAS      -
             SALIDAS       +
             DEBITO +
             CREDITO)

       END SALDOFINAL
FROM VISTA_CUENTAALMACEN    INNER JOIN BODEGA
  ON VISTA_CUENTAALMACEN.COMPANIA        = BODEGA.COMPANIA
 AND VISTA_CUENTAALMA]') || TO_CLOB(q'[CEN.BODEGA_DESTINO1 = BODEGA.CODIGO
WHERE BODEGA.MOSTRAR NOT IN (0)
      AND ((CASE WHEN DEPEND IN('PROVEEDORES','DONACIONES','COMODATO')
             THEN ABS(ANTERIOR)
             ELSE ANTERIOR
         END                 +
         ENTRADAS      -
         SALIDAS       +
         DEBITO +
         CREDITO) NOT IN(0)
        OR(
          (CASE WHEN DEPEND IN('PROVEEDORES','DONACIONES','COMODATO')
                THEN ABS(ANTERIOR)
                ELSE ANTERIOR
           END                 +
           ENTRADAS      -
           SALIDAS       +
           DEBITO +
           CREDITO) IN(0)
           AND (ENTRADAS NOT IN (0)
            OR  SALIDAS  NOT IN (0))
          )
        )
  AND TIPO_COD NOT IN(CASE WHEN DEPEND IN('SERVICIO','INSERVIBLES','RESPONSABILIDAD FISCAL','DONACIONES','COMODATO')
                           THEN 'C'
                           ELSE ' '
                      END)
  ORDER BY s$grupoEtiq$s]') AS CONSULTA_VAL, 10 AS APLICACION_VAL, TO_CLOB(q'[]') AS CONSULTA_OPCIONAL_VAL, (SELECT MIN(ROWID) FROM CONSULTAS WHERE INFORME = '800378CuentaAlmacen') AS RID_DESTINO  FROM DUAL ) INI ON (FIN.ROWID = INI.RID_DESTINO)  WHEN MATCHED THEN   UPDATE SET FIN.CONSULTA = INI.CONSULTA_VAL, FIN.APLICACION = INI.APLICACION_VAL, FIN.CONSULTA_OPCIONAL = INI.CONSULTA_OPCIONAL_VAL, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN   INSERT (INFORME, CONSULTA, APLICACION, CONSULTA_OPCIONAL, DATE_CREATED)   VALUES (INI.INFORME_VAL, INI.CONSULTA_VAL, INI.APLICACION_VAL, INI.CONSULTA_OPCIONAL_VAL, SYSDATE);