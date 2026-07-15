SELECT     TO_CHAR(ANO) Anio,
           NITCOMPANIA  NIT_Entidad,
           NOMBRECOM    Nombre_Entidad,
           CODIGO       Codigo_Contable,
           NOMBRE       Descripcion,
           SALDOFINAL   Saldo
                
    FROM (   
        SELECT     PLAN_CONTABLE.ANO,PLAN_CONTABLE.CODIGO,                                                                                            
                   PLAN_CONTABLE.NOMBRE,
                   COMPANIA.NITCOMPANIA,
                   COMPANIA.NOMBRE NOMBRECOM,
                   (CASE WHEN 0  = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO0  ELSE 0 END
                           + CASE WHEN 1  = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO1  ELSE 0 END
                           + CASE WHEN 2  = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO2  ELSE 0 END
                           + CASE WHEN 3  = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO3  ELSE 0 END
                           + CASE WHEN 4  = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO4  ELSE 0 END
                           + CASE WHEN 5  = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO5  ELSE 0 END
                           + CASE WHEN 6  = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO6  ELSE 0 END
                           + CASE WHEN 7  = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO7  ELSE 0 END
                           + CASE WHEN 8  = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO8  ELSE 0 END
                           + CASE WHEN 9  = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO9  ELSE 0 END
                           + CASE WHEN 10 = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO10 ELSE 0 END
                           + CASE WHEN 11 = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO11 ELSE 0 END
                           + CASE WHEN 12 = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO12 ELSE 0 END
                           + CASE WHEN 13 = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO13 ELSE 0 END
                           )SALDOFINAL                                                                                
                                                                                                                 
                                                                                     
           FROM  PLAN_CONTABLE
           INNER JOIN COMPANIA 
            ON PLAN_CONTABLE.COMPANIA=COMPANIA.CODIGO
           WHERE PLAN_CONTABLE.COMPANIA =s$compania$s
             AND PLAN_CONTABLE.ANO      =s$ano$s             
             
           )
WHERE     (CODIGO LIKE '4%' OR CODIGO LIKE'5%') 
AND    SALDOFINAL NOT IN (0)
ORDER BY  CODIGO