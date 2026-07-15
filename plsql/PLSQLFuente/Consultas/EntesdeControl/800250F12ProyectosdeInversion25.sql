SELECT PROYECTOS.NOMBREPROYECTO                                                       "Nombre Del Proyecto",
       PROYECTOS.CODIGOBPIM                                                           "Numero De Inscripcion",
       PROYECTOS.FECHAREGISTRO                                                        "Fecha De Aprobacion", 
      CASE WHEN FUENTE_RECURSOS.TIPO_FUENTE=1
          THEN DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO
          ELSE 0 END                                                                  "Orden De Recursos - Valor Recursos Propios",
      CASE WHEN FUENTE_RECURSOS.TIPO_FUENTE=2
          THEN DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO
          ELSE 0 END                                                                  "Orden De Recursos - Valor Recursos Externos",      
      CASE WHEN FUENTE_RECURSOS.TIPO_FUENTE=3
          THEN DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO
          ELSE 0 END                                                                  "Orden De Recursos - Valor Recursos Convenios",    
      CASE WHEN FUENTE_RECURSOS.TIPO_FUENTE=4
          THEN DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO 
          ELSE 0 END                                                                  "Orden De Recursos - Valor Otros",
       PROYECTOS.VALOREJECUTADO                                                       "Total Inversión" ,
       (VIGENCIAFIN)-(VIGENCIAINICIO)|| ''||'AÑOS'                                    "Plazo", 
       SECTORDNP.NOMBRE                                                               "Sector Inversión",
       NVL(PROYECTOS.PORCEJECUCION,0) || '%'                                          "Porcentaje De Ejecución"
  FROM PROYECTOS LEFT JOIN BP_PROYECTOSRUBROS 
                 ON PROYECTOS.COMPANIA = BP_PROYECTOSRUBROS.COMPANIA
                 AND PROYECTOS.CODIGO = BP_PROYECTOSRUBROS.PROYECTO
          LEFT JOIN FUENTE_RECURSOS 
                 ON BP_PROYECTOSRUBROS.COMPANIA = FUENTE_RECURSOS.COMPANIA
                 AND BP_PROYECTOSRUBROS.VIGENCIA = FUENTE_RECURSOS.ANO 
                 AND BP_PROYECTOSRUBROS.FUENTERECURSOSRUBRO = FUENTE_RECURSOS.CODIGO
           LEFT JOIN DETALLE_COMPROBANTE_PPTAL 
                 ON BP_PROYECTOSRUBROS.COMPANIA = DETALLE_COMPROBANTE_PPTAL.COMPANIA
                 AND BP_PROYECTOSRUBROS.VIGENCIA = DETALLE_COMPROBANTE_PPTAL.ANO
                 AND BP_PROYECTOSRUBROS.RUBROPPTALES = DETALLE_COMPROBANTE_PPTAL.CUENTA
                  AND BP_PROYECTOSRUBROS.FUENTERECURSOSRUBRO = DETALLE_COMPROBANTE_PPTAL.FUENTE_RECURSO
           LEFT JOIN SECTORDNP 
                ON PROYECTOS.COMPANIA = SECTORDNP.COMPANIA 
                AND PROYECTOS.SECTORDNP = SECTORDNP.CODIGODNP
                 WHERE COMPANIA = s$compania$s
                        AND (PROYECTOS.VIGENCIAINICIO<=s$vigenciafins$  AND  PROYECTOS.VIGENCIAFIN>=:s$vigenciainis$)
                        
