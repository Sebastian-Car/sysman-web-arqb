CREATE OR REPLACE VIEW V_D_MOV_SALIDAS AS 
  SELECT
                    D_MOVIMIENTO.COMPANIA,
                    D_MOVIMIENTO.ELEMENTO,
                    NVL(D_MOVIMIENTO.LOTE, '-1.-')            LOTE,
                    NVL(D_MOVIMIENTO.FUENTEDERECURSO, '-1.-') FUENTEDERECURSO,
                    SUM(D_MOVIMIENTO.CANTIDAD)                SALIDA
                FROM
                         D_MOVIMIENTO
                    INNER JOIN TIPOMOVIMIENTO ON TIPOMOVIMIENTO.COMPANIA = D_MOVIMIENTO.COMPANIA
                                                 AND TIPOMOVIMIENTO.CODIGO = D_MOVIMIENTO.TIPOMOVIMIENTO
                WHERE
                    TIPOMOVIMIENTO.CLASE IN ( 'S' )
                    AND TIPOMOVIMIENTO.TIPOELEMENTO IN ( 'C' )
                GROUP BY
                    D_MOVIMIENTO.COMPANIA,
                    D_MOVIMIENTO.ELEMENTO,
                    NVL(D_MOVIMIENTO.LOTE, '-1.-'),
                    NVL(D_MOVIMIENTO.FUENTEDERECURSO, '-1.-');
                
                