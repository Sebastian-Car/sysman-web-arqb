SELECT *
 
FROM (
      WITH TOTALPORDEPENDENCIA AS (SELECT LPAD(INVENTARIO.CODIGOELEMENTO, 3) AS GRUPO, 
                                          INVENTARIO.NOMBRECORTO, 
                                          DEPENDENCIA.CODIGO, 
                                          DEPENDENCIA.NOMBRE, 
                                          SUM(DEVOLUTIVO.VALOR) AS VALORDEVOLUTIVO
                                    FROM (DEVOLUTIVO 
                                      INNER JOIN INVENTARIO 
                                        ON (DEVOLUTIVO.COMPANIA = INVENTARIO.COMPANIA) 
                                        AND (LPAD(DEVOLUTIVO.ElEMENTO, 3) = INVENTARIO.CODIGOELEMENTO)) 
                                      INNER JOIN DEPENDENCIA ON (DEVOLUTIVO.COMPANIA = DEPENDENCIA.COMPANIA) 
                                        AND (DEVOLUTIVO.DEPENDENCIA = DEPENDENCIA.CODIGO)
                                    WHERE DEVOLUTIVO.COMPANIA = s$compania$s 
                                      AND LPAD(INVENTARIO.CODIGOELEMENTO, 3) BETWEEN LPAD('s$elemDesde$s',s$agrupacion$s) AND LPAD('s$elemHasta$s',s$agrupacion$s)
                                      AND CODIGO BETWEEN NVL('s$codIni$s','000000000000') AND NVL('s$codFin$s','999999999999')
                                      AND (DEVOLUTIVO.PLACAANULADA = 0)
                                      
                                    GROUP BY 
                                      LPAD(INVENTARIO.CODIGOELEMENTO, 3), 
                                      INVENTARIO.NOMBRECORTO, 
                                      DEPENDENCIA.CODIGO, 
                                      DEPENDENCIA.NOMBRE
                                  )
      SELECT GRUPO,  SUBSTR(CODIGO||'_'||NOMBRE,1,28) NOMBRE,NOMBRECORTO ELEMENTO,
      VALORDEVOLUTIVO,
      SUM(VALORDEVOLUTIVO) OVER (Partition BY GRUPO) Total 
      FROM  TOTALPORDEPENDENCIA
      
)
PIVOT (SUM(VALORDEVOLUTIVO) FOR NOMBRE  IN (s$pivot$s))
ORDER BY GRUPO
