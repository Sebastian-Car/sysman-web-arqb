SELECT COMPANIA,
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
            ELSE TERCERO_NOM || ', Tel:' || TERCERO_TEL || ' , Nit: ' || TERCERO
       END || CASE WHEN TERCERO_DIR IS NULL 
                   THEN '' 
                   ELSE '  Dirección:' || TERCERO_DIR || '' || 
                                          CASE WHEN TERCERO_FAX IS NULL 
                                               THEN ''
                                               ELSE '  Fax:' || TERCERO_FAX
                                          END
       END TTERCERO,

       CASE WHEN TIPOPESONA = 'R' 
            THEN RESPONSABLE_ORIGEN_NOM
            ELSE TERCERO_NOM
       END NOMRESP,
       
       CASE WHEN TIPOPESONA = 'R' 
            THEN 'c.c. ' || RESPONSABLE_ORIGEN
            ELSE 'NIT. ' || TERCERO
       END IDRESP,
       
       CASE WHEN TIPOPESONA = 'R' 
            THEN RESPONSABLE_ORIGEN_CARGO
            ELSE ''
       END CARGORESP,
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
       RESPONSABLE_ORIGEN_NOM,
       RESPONSABLE_ORIGEN,
       RESPONSABLE_ORIGEN_CARGO
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
          CASE WHEN TIPOPESONA = 'R' 
               THEN RESPONSABLE_ORIGEN_NOM || ', c.c.' || RESPONSABLE_ORIGEN
               ELSE TERCERO_NOM || ', Tel:' || TERCERO_TEL || ' , Nit: ' || TERCERO
          END || CASE WHEN TERCERO_DIR IS NULL 
                      THEN '' 
                      ELSE '  Dirección:' || TERCERO_DIR || '' || 
                                             CASE WHEN TERCERO_FAX IS NULL 
                                                  THEN ''
                                                  ELSE '  Fax:' || TERCERO_FAX
                                             END
          END, 
   	CASE WHEN TIPOPESONA = 'R' 
            THEN RESPONSABLE_ORIGEN_NOM
            ELSE TERCERO_NOM
       END,
       
       CASE WHEN TIPOPESONA = 'R' 
            THEN 'c.c. ' || RESPONSABLE_ORIGEN
            ELSE 'NIT. ' || TERCERO
       END,
       
       CASE WHEN TIPOPESONA = 'R' 
            THEN RESPONSABLE_ORIGEN_CARGO
            ELSE ''
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
	  RESPONSABLE_ORIGEN_NOM,
          RESPONSABLE_ORIGEN,
       	  RESPONSABLE_ORIGEN_CARGO

 ORDER BY COMPANIA,
          TIPOMOVIMIENTO,
          MOVIMIENTO,
          ELEMENTO