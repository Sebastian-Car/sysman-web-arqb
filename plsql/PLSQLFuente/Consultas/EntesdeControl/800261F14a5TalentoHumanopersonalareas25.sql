SELECT CASE WHEN PERSONAL.AREAMISOADM='MI'
                THEN 'MISIONAL'
                ELSE
          CASE WHEN PERSONAL.AREAMISOADM='AD' 
             THEN 'ADMINISTRATIVA'
            ELSE 'ND'
            END
            END                                              ÁREA,
            COUNT (CASE WHEN PERSONAL.AREAMISOADM='MI'
               THEN PERSONAL.AREAMISOADM      
               ELSE 
                  CASE WHEN PERSONAL.AREAMISOADM='AD'
                       THEN PERSONAL.AREAMISOADM      
                     ELSE 'ND'
                     END
                     END)                                    CANTIDAD
 FROM PERSONAL 
WHERE AREAMISOADM IN('MI','AD')
AND ID_DE_EMPLEADO NOT IN(0)
AND PERSONAL.ID_DE_TIPO NOT IN('99')
AND (PERSONAL.ESTADO_ACTUAL=1 OR (ESTADO_ACTUAL=3 AND TO_NUMBER(TO_CHAR(FECHA_DE_RETIRO,'YYYY'))=s$ano$s AND TO_NUMBER(TO_CHAR(FECHA_DE_RETIRO,'MM')) BETWEEN  s$mesInicial$s AND s$mesFinal$s))
GROUP BY PERSONAL.AREAMISOADM
