SELECT  V.COMPANIA,
    V.ANO,
    V.CODIGO,
    V.ID,
    V.NOMBRE,
    V.DEPENDENCIAASOCIADA AS CODDEP,
    DEPENDENCIA.NOMBRE                      AS NOMDEPENDENCIA,
    SUM(APROPIADO) APROPIADO,
    SUM(ADICION) ADICION,
    SUM(REDUCCION) REDUCCION,
    SUM(CREDITO) CREDITO,
    SUM(CONTRACREDITO) CONTRACREDITO,
    SUM(TOTALAPROPIADO) TOTALAPROPIADO,
    SUM(DISPONIBILIDAD) DISPONIBILIDAD,
    SUM(SALDODISPONIBLE) SALDODISPONIBLE,
    SUM(REGISTROOBLIGACION) REGISTROOBLIGACION,
    --SUM(APLAZAMIENTOS) APLAZAMIENTOS,
    --SUM(DESAPLAZAMIENTOS) DESAPLAZAMIENTOS,
    SUM(APRREGISTRADAS) APRREGISTRADAS,
    SUM(DISPONIBILIDADESABIERTAS) DISPONIBILIDADESABIERTAS,
    SUM(EJEPTO) AS EJEPTO,
    SUM(PORPAGAR) PORPAGAR,
        
        CASE
        WHEN s$tipoCuenta$s  = 1 THEN
            V.CODIGO
        ELSE
            SUBSTR(NVL(NIVEL1, ''), 1, 3)
            || '-'
            || SUBSTR(NVL(NIVEL2, ''), 1, 3)
            || '-'
            || SUBSTR(NVL(NIVEL3, ''), 1, 3)
            || '-'
            || SUBSTR(NVL(NIVEL4, ''), 1, 3)
            || '-'
            || SUBSTR(NVL(NIVEL5, ''), 1, 3)
            || '-'
            || SUBSTR(NVL(NIVEL6, ''), 1, 3)
    END RUBRO,
    CASE
        WHEN s$tipoCuenta$s  = 1 THEN
            'Codigo cuenta'
        ELSE
            'Fid-Une-Prog-Spr-Proy-Dis-Rec'
    END TITULOCUENTA,
    CASE
        WHEN SUM(TOTALAPROPIADO) <> 0
        THEN ROUND( SUM(APRREGISTRADAS)/SUM(TOTALAPROPIADO)*100,2)
        ELSE 0
    END AS PORCENTEJECUCION
FROM V_PLAN_PRESUPUESTAL V INNER JOIN (
    SELECT P.COMPANIA,
       P.ANO,
       S.ID,
       SUM(CASE WHEN S.MES < 1 
                THEN CASE WHEN P.NATURALEZA='D' 
                          THEN   APROPIACION_DEBITO  - APROPIACION_CREDITO - TRASLADO_CREDITO + TRASLADO_DEBITO 
                          ELSE - APROPIACION_DEBITO  + APROPIACION_CREDITO + TRASLADO_CREDITO - TRASLADO_DEBITO 
                          END + ADICION + REDUCCION
                ELSE 0 END
            ) AS APROPIADO,      
       SUM(S.ADICION) AS ADICION,                     
       SUM(S.REDUCCION*-1) AS REDUCCION,          
       SUM(S.TRASLADO_DEBITO)  CREDITO,         
       SUM(S.TRASLADO_CREDITO )  CONTRACREDITO, 
       SUM(CASE WHEN P.NATURALEZA='D' 
                           THEN   S.APLAZAM_DEBITO 
                           ELSE  S.APLAZAM_CREDITO
                           END
                          )APLAZAMIENTOS,             
       SUM(CASE WHEN P.NATURALEZA='D' 
                THEN   S.APLAZAM_CREDITO
                ELSE  S.APLAZAM_DEBITO
          END) DESAPLAZAMIENTOS,  
        SUM(CASE WHEN P.NATURALEZA='D' 
                THEN   APROPIACION_DEBITO  - APROPIACION_CREDITO - TRASLADO_CREDITO + TRASLADO_DEBITO 
                ELSE - APROPIACION_DEBITO  + APROPIACION_CREDITO + TRASLADO_CREDITO - TRASLADO_DEBITO 
            END + ADICION + REDUCCION) AS TOTALAPROPIADO,  
        SUM(S.DISPONIBILIDAD) DISPONIBILIDAD,  
        SUM(CASE WHEN P.NATURALEZA='D' 
                THEN   APROPIACION_DEBITO  - APROPIACION_CREDITO - TRASLADO_CREDITO + TRASLADO_DEBITO 
                ELSE - APROPIACION_DEBITO  + APROPIACION_CREDITO + TRASLADO_CREDITO - TRASLADO_DEBITO 
            END + ADICION + REDUCCION) - SUM(S.DISPONIBILIDAD )SALDODISPONIBLE,
        SUM(S.REGISTRO_OBLIGACION
               + S.MODIF_REGISTRO_OBLIGACION
          )  REGISTROOBLIGACION,    
            
       SUM(S.REG_CONTRACT 
               + S.REG_NO_CONTRACT
               + S.MODIF_REG_CONT
               + S.MODIF_REG_NOCONT
            )  APRREGISTRADAS,
       SUM(S.DISPONIBILIDAD- (S.REG_CONTRACT 
               + S.REG_NO_CONTRACT
               + S.MODIF_REG_CONT
               + S.MODIF_REG_NOCONT
            )) DISPONIBILIDADESABIERTAS, 
       SUM(CASE WHEN P.NATURALEZA='D' 
                THEN   S.EJE_PPT_DEBITO - S.EJE_PPT_CREDITO
                ELSE - S.EJE_PPT_DEBITO + S.EJE_PPT_CREDITO
                END 
          )  EJEPTO,      
        SUM(S.REGISTRO_OBLIGACION
               + S.MODIF_REGISTRO_OBLIGACION
          ) - SUM(CASE WHEN P.NATURALEZA='D' 
                THEN   S.EJE_PPT_DEBITO - S.EJE_PPT_CREDITO
                ELSE - S.EJE_PPT_DEBITO + S.EJE_PPT_CREDITO
                END )PORPAGAR
                
FROM PLAN_PRESUPUESTAL P INNER JOIN SALDO_AUX_PPTAL S
  ON P.COMPANIA = S.COMPANIA 
 AND P.ANO      = S.ANO
 AND P.CODIGO   = S.CODIGO

WHERE P.COMPANIA     = s$compania$s
  AND P.ANO          = s$anio$s
  AND S.MES BETWEEN 0 AND s$mes$s
  AND P.NATURALEZA   IN ('D')
  AND ID BETWEEN 's$cuentaInicial$s' AND 's$cuentaFinal$s'
  AND P.DEPENDENCIAASOCIADA BETWEEN 's$dependenciaInicial$s' AND 's$dependenciaFinal$s'
 GROUP BY P.COMPANIA,
         P.ANO,
         S.ID
)SALDOS
  ON V.COMPANIA = SALDOS.COMPANIA
  AND V.ANO      = SALDOS.ANO
  AND V.ID       = SUBSTR(SALDOS.ID, 1, LENGTH(V.ID))
INNER JOIN DEPENDENCIA
 ON V.COMPANIA             = DEPENDENCIA.COMPANIA
AND V.DEPENDENCIAASOCIADA = DEPENDENCIA.CODIGO
WHERE LENGTH(V.CODIGO) BETWEEN 0 AND s$nivel$s
  AND V.DEPENDENCIAASOCIADA BETWEEN 's$dependenciaInicial$s' AND 's$dependenciaFinal$s'
GROUP BY   V.COMPANIA,
    V.ANO,
    V.CODIGO,
    V.ID,
    V.NOMBRE,
    CASE WHEN s$tipoCuenta$s  = 1 THEN
            V.CODIGO
        ELSE
            SUBSTR(NVL(NIVEL1, ''), 1, 3)
            || '-'
            || SUBSTR(NVL(NIVEL2, ''), 1, 3)
            || '-'
            || SUBSTR(NVL(NIVEL3, ''), 1, 3)
            || '-'
            || SUBSTR(NVL(NIVEL4, ''), 1, 3)
            || '-'
            || SUBSTR(NVL(NIVEL5, ''), 1, 3)
            || '-'
            || SUBSTR(NVL(NIVEL6, ''), 1, 3)
    END ,
    CASE
        WHEN s$tipoCuenta$s  = 1 THEN
            'Codigo cuenta'
        ELSE
            'Fid-Une-Prog-Spr-Proy-Dis-Rec'
    END,
    V.DEPENDENCIAASOCIADA,
    DEPENDENCIA.NOMBRE
