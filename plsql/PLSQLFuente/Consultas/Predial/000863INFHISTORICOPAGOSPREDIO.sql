SELECT 
    PRECOD, 
    DOCNUM, 
    PAQUETEPAG, 
    AVALUO, 
    PREVAL, 
    PAG_BANPAG, 
    TO_CHAR(PREFECPAG, 'DD/MM/YYYY') PREFECPAG, 
    TIPO, 
    FINALPREANO 
FROM 
    (
        SELECT 
            IP_RECIBOS_DE_PAGO.PRECOD, 
            IP_RECIBOS_DE_PAGO.DOCNUM, 
            IP_RECIBOS_DE_PAGO.PAGO, 
            IP_RECIBOS_DE_PAGO.PAQUETEPAG, 
            IP_RECIBOS_DE_PAGO.ANULADO, 
            IP_RECIBOS_DE_PAGO.PREANO, 
            IP_RECIBOS_DE_PAGO.PREANOI, 
            IP_RECIBOS_DE_PAGO.AVALUO, 
            IP_RECIBOS_DE_PAGO.PREVAL, 
            IP_RECIBOS_DE_PAGO.PAG_BANPAG, 
            IP_RECIBOS_DE_PAGO.PREFECPAG, 
            'CUOTA'  TIPO, 
             CASE WHEN
                IP_RECIBOS_DE_PAGO.PREANOI <> IP_RECIBOS_DE_PAGO.PREANO AND IP_RECIBOS_DE_PAGO.PREANOI <>  0            THEN  
                 TO_CHAR(IP_RECIBOS_DE_PAGO.PREANOI) || ' - ' ||        TO_CHAR(IP_RECIBOS_DE_PAGO.PREANO)   
              ELSE 
            TO_CHAR(IP_RECIBOS_DE_PAGO.PREANO) 
               END FINALPREANO  
        FROM 
            IP_RECIBOS_DE_PAGO 
        WHERE 
            IP_RECIBOS_DE_PAGO.COMPANIA =  s$compania$s
                AND 
            IP_RECIBOS_DE_PAGO.PRECOD = 's$codigoConsulta$s' 
                AND 
            NOT IP_RECIBOS_DE_PAGO.PAGO IN(0) 
                AND 
            IP_RECIBOS_DE_PAGO.ANULADO IN(0) 
                AND 
            NOT IP_RECIBOS_DE_PAGO.ESCUOTA IN(0) 
        UNION 
            SELECT 
            IP_RECIBOS_DE_PAGO.PRECOD, 
            IP_RECIBOS_DE_PAGO.DOCNUM, 
            IP_RECIBOS_DE_PAGO.PAGO, 
            IP_RECIBOS_DE_PAGO.PAQUETEPAG, 
            IP_RECIBOS_DE_PAGO.ANULADO, 
            IP_RECIBOS_DE_PAGO.PREANO, 
            IP_RECIBOS_DE_PAGO.PREANOI, 
            IP_RECIBOS_DE_PAGO.AVALUO, 
            IP_RECIBOS_DE_PAGO.PREVAL, 
            IP_RECIBOS_DE_PAGO.PAG_BANPAG, 
            IP_RECIBOS_DE_PAGO.PREFECPAG, 
            'ABONO'  TIPO, 
                         CASE WHEN
                IP_RECIBOS_DE_PAGO.PREANOI <> IP_RECIBOS_DE_PAGO.PREANO AND IP_RECIBOS_DE_PAGO.PREANOI <>  0            THEN  
                 TO_CHAR(IP_RECIBOS_DE_PAGO.PREANOI) || ' - ' ||        TO_CHAR(IP_RECIBOS_DE_PAGO.PREANO)   
              ELSE 
            TO_CHAR(IP_RECIBOS_DE_PAGO.PREANO) 
               END FINALPREANO  
        FROM 
            IP_RECIBOS_DE_PAGO 
        WHERE 
            IP_RECIBOS_DE_PAGO.COMPANIA =  s$compania$s
                AND 
            IP_RECIBOS_DE_PAGO.PRECOD = 's$codigoConsulta$s' 
                AND 
            NOT IP_RECIBOS_DE_PAGO.PAGO IN(0) 
                AND 
            IP_RECIBOS_DE_PAGO.ANULADO IN(0) 
                AND 
            NOT IP_RECIBOS_DE_PAGO.ESABONO IN(0) 
        UNION 
            SELECT 
            IP_RECIBOS_DE_PAGO.PRECOD, 
            IP_RECIBOS_DE_PAGO.DOCNUM, 
            IP_RECIBOS_DE_PAGO.PAGO, 
            IP_RECIBOS_DE_PAGO.PAQUETEPAG, 
            IP_RECIBOS_DE_PAGO.ANULADO, 
            IP_RECIBOS_DE_PAGO.PREANO, 
            IP_RECIBOS_DE_PAGO.PREANOI, 
            IP_RECIBOS_DE_PAGO.AVALUO, 
            IP_RECIBOS_DE_PAGO.PREVAL, 
            IP_RECIBOS_DE_PAGO.PAG_BANPAG, 
            IP_RECIBOS_DE_PAGO.PREFECPAG, 
            'ACUERDO'  TIPO, 
                         CASE WHEN
                IP_RECIBOS_DE_PAGO.PREANOI <> IP_RECIBOS_DE_PAGO.PREANO AND IP_RECIBOS_DE_PAGO.PREANOI <>  0            THEN  
                 TO_CHAR(IP_RECIBOS_DE_PAGO.PREANOI) || ' - ' ||        TO_CHAR(IP_RECIBOS_DE_PAGO.PREANO)   
              ELSE 
            TO_CHAR(IP_RECIBOS_DE_PAGO.PREANO) 
               END FINALPREANO  
        FROM 
            IP_RECIBOS_DE_PAGO 
        WHERE 
            IP_RECIBOS_DE_PAGO.COMPANIA =  s$compania$s
                AND 
            IP_RECIBOS_DE_PAGO.PRECOD = 's$codigoConsulta$s' 
                AND 
            NOT IP_RECIBOS_DE_PAGO.PAGO IN(0) 
                AND 
            IP_RECIBOS_DE_PAGO.ANULADO IN(0) 
                AND 
            NOT IP_RECIBOS_DE_PAGO.ESACUERDO IN(0) 
        UNION 
            SELECT 
            IP_RECIBOS_DE_PAGO.PRECOD, 
            IP_RECIBOS_DE_PAGO.DOCNUM, 
            IP_RECIBOS_DE_PAGO.PAGO, 
            IP_RECIBOS_DE_PAGO.PAQUETEPAG, 
            IP_RECIBOS_DE_PAGO.ANULADO, 
            IP_RECIBOS_DE_PAGO.PREANO, 
            IP_RECIBOS_DE_PAGO.PREANOI, 
            
            IP_RECIBOS_DE_PAGO.AVALUO, 
            IP_RECIBOS_DE_PAGO.PREVAL, 
            IP_RECIBOS_DE_PAGO.PAG_BANPAG, 
            IP_RECIBOS_DE_PAGO.PREFECPAG, 
            'VIGENCIA'  TIPO, 
                         CASE WHEN
                IP_RECIBOS_DE_PAGO.PREANOI <> IP_RECIBOS_DE_PAGO.PREANO AND IP_RECIBOS_DE_PAGO.PREANOI <>  0            THEN  
                 TO_CHAR(IP_RECIBOS_DE_PAGO.PREANOI) || ' - ' ||        TO_CHAR(IP_RECIBOS_DE_PAGO.PREANO)   
              ELSE 
            TO_CHAR(IP_RECIBOS_DE_PAGO.PREANO) 
               END FINALPREANO  
        FROM 
            IP_RECIBOS_DE_PAGO 
        WHERE 
            IP_RECIBOS_DE_PAGO.COMPANIA =  s$compania$s
                AND 
            IP_RECIBOS_DE_PAGO.PRECOD = 's$codigoConsulta$s' 
                AND 
            NOT IP_RECIBOS_DE_PAGO.PAGO IN(0) 
                AND 
            IP_RECIBOS_DE_PAGO.ANULADO IN(0) 
                AND 
            IP_RECIBOS_DE_PAGO.ESACUERDO IN(0) 
                AND 
            IP_RECIBOS_DE_PAGO.ESCUOTA IN(0) 
                AND 
            IP_RECIBOS_DE_PAGO.ESABONO IN(0)
    )  PAGOS 
ORDER BY 
    FINALPREANO DESC
