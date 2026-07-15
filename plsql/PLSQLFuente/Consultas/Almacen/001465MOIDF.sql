MERGE INTO CONSULTAS FIN USING (SELECT '001465MOIDF' INFORME ,TO_CLOB(q'[SELECT COMPANIA,
       TIPOMOVIMIENTO,
       MOVIMIENTO,
       ELEMENTO,
       SERIE,
       CASE WHEN IND_REG NOT IN (0) THEN '' ELSE '*' END NOREGISTRADO,
       TIPOMOVIMIENTO_NOM NOMBRETIPOMOVIMIENTO,
       TIPOPERSONA_NOM TIPOPESONA, 
       NOMBRELARGO,
       UNIDAD, 
       NOMBRELARGO || ' ' 
        || CASE WHEN TRIM(MARCA) IS NOT NULL 
                THEN ',Marca ' || MARCA 
                ELSE ''
           END  
        || CASE WHEN ESPECIFICACION IS NULL 
                THEN ' ' 
                ELSE ESPECIFICACION
           END   
        || CASE WHEN SERIEDEVOLUTIVO IS NULL OR SERIEDEVOLUTIVO = ' '
                THEN '' 
                ELSE ' ,Serie:' || SERIEDEVOLUTIVO
           END  
        || CASE WHEN SERIE = 0 
                THEN '' 
                ELSE ', Placa:' || SERIE
       END DESCRIPCION1, 
       CASE WHEN TIPOPESONA = 'R' 
            THEN RESPONSABLE_ORIGEN_NOM || ', c.c.' || RESPONSABLE_ORIGEN
            ELSE TERCERO_NOM || ', Tel:' || TERCERO_TEL || ' , Nit: ' || TERCERO_FAX
       END || CASE WHEN TERCERO_DIR IS NULL 
                   THEN '' 
                   ELSE '  Dirección:' || TERCERO_DIR || '' || 
                                          CASE WHEN TERCERO_FAX IS NULL 
                                               THEN ''
                                               ELSE '  Fax:' || TERCERO_FAX
                                          END
       END TTERCERO, 
       CANTIDAD,
       VALORUNITARIO,
       VALORTOTAL_D VALORTOTAL,
       MOVASOCIADO,
       TIPOMOVASOCIADO_NOM NOMDOCASOCIADO, 
       TOTALCONAJUSTE_MOV TOTALCONAJUSTE,
       TIPOMOVIMIENTO || MOVIMIENTO AS GRUPO,
       AJUSTECENTAVOS1_MOV*-1 AS AJUSTECENTAVOS,
       NVL(DESCRIPCION_MOV, ' ') DESCRIPCION,
       TIPOMOVASOCIADO,
       TO_CHAR(FECHAMOVASOCIADO, 'DD/MM/YYYY') FECHAMOVASOCIADO,
       TO_CHAR(FECHA_MOV, 'DD/MM/YYYY') AS FECHA  
FROM V_MOVIMIENTO_INFORME 
 WHERE COMPANIA       = s$compania$s
   AND TIPOMOVIMIENTO = 's$tipoMovimiento$s'
   AND MOVIMIENTO BETWEEN 's$movimientoInicial$s' AND 's$movimientoFinal$s'
 GROUP BY COMPANIA,
          TIPOMOVIMIENTO,
          MOVIMIENTO,
          ELEMENTO,
          SERIE,
          CASE WHEN IND_REG NOT IN (0) THEN '' ELSE '*' END,
          TIPOMOVIMIENTO_NOM,
          TIPOPERSONA_NOM, 
          NOMBRELARGO,
          UNIDAD, 
          NOMBRELARGO || ' ' 
           || CASE WHEN TRIM(MARCA) IS NOT NULL 
                   THEN ',Marca ' || MARCA 
                   ELSE ''
              END  
           || CASE WHEN ESPECIFICACION IS NULL 
                   THEN ' ' 
                   ELSE ESPECIFICACION
              END   
           || CASE WHEN SERIEDEVOLUTIVO IS NULL OR SERIEDEVOLUTIVO = ' '
                   THEN '' 
                   ELSE ' ,Serie:' || SERIEDEVOLUTIVO
              END  
           || CASE WHEN SERIE = 0 
                   THEN '' 
                   ELSE ', Placa:' || SERIE
              END, 
          CASE WHEN TIPOPESONA ]') || TO_CLOB(q'[= 'R' 
               THEN RESPONSABLE_ORIGEN_NOM || ', c.c.' || RESPONSABLE_ORIGEN
               ELSE TERCERO_NOM || ', Tel:' || TERCERO_TEL || ' , Nit: ' || TERCERO_FAX
          END || CASE WHEN TERCERO_DIR IS NULL 
                      THEN '' 
                      ELSE '  Dirección:' || TERCERO_DIR || '' || 
                                             CASE WHEN TERCERO_FAX IS NULL 
                                                  THEN ''
                                                  ELSE '  Fax:' || TERCERO_FAX
                                             END
          END, 
          CANTIDAD,
          VALORUNITARIO,
          VALORTOTAL_D,
          MOVASOCIADO,
          TIPOMOVASOCIADO_NOM, 
          TOTALCONAJUSTE_MOV,
          TIPOMOVIMIENTO || MOVIMIENTO,
          AJUSTECENTAVOS1_MOV*-1,
          NVL(DESCRIPCION_MOV, ' '),  
          TIPOMOVASOCIADO,
          TO_CHAR(FECHAMOVASOCIADO, 'DD/MM/YYYY'),
          TO_CHAR(FECHA_MOV, 'DD/MM/YYYY')
 ORDER BY COMPANIA,
          TIPOMOVIMIENTO,
          MOVIMIENTO,
          ELEMENTO]') CONSULTA, 10 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);