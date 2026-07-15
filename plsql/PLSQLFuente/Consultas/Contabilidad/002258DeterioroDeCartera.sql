SELECT DISTINCT 
                D.ANO,
                D.MES,
                D.TERCERO,
                TRIM(T.NOMBRE1) || ' ' || TRIM(T.NOMBRE2) || ' ' || TRIM(T.APELLIDO1) || ' ' || TRIM(T.APELLIDO2)  AS NOMBRETERCERO,
                D.TIPO_FACTURA AS TIPO,
                D.NUMERO_FACTURA AS NUMERO,
                D.DESCRIPCION,
                TO_CHAR(D.FECHA_FACTURA,'DD-Mon-YYYY') AS FECHA,
                D.CUENTA,
                D.EDAD,
                D.TASA,
                D.VALOR_FACTURA AS VALOR,
                D.DETERIORO_ACUMULADO,
                D.VALOR AS DETERIORO_MENSUAL,
                VALOR_FACTURA-DETERIORO_ACUMULADO VALOR_NETO                 
 FROM DETERIORO_CARTERA D
 INNER JOIN TERCERO T
 ON T.COMPANIA=D.COMPANIA
 AND T.NIT= D.TERCERO
 WHERE D.COMPANIA = s$compania$s
       AND D.ANO = s$ano$s
       AND D.MES = s$mes$s