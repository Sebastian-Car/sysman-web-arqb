SELECT  
    UP.COMPANIA,  
    UP.NIT,  
    UP.CODIGO,  
    UP.NUMERO_ORDEN,  
    UP.NOMBRE,  
    UP.DIRECCION,  
    UP.AREA_M2,  
    UP.AREA_CONSTRUIDA,  
    UP.TRPCOD,  
    UP.AVALUO_ANO,  
    'RECIBO DE PAGO NO. ' || UP.NUM_COM || ' - VALOR  ' || UP.PAG_VAL || ' - FECHA PAGO ' || TO_CHAR(UP.PAG_FEC, 'DD/MM/YYYY')  PAGO,  
    F.PREANO,  
    F.AVALUO,  
    F.C1,  
    F.C2,  
    F.C3,  
    F.C4,  
    F.C13,  
    F.C14,  
    F.C15,  
    F.C16,  
    F.C17,  
    F.C18,  
    F.C19,  
    F.C20,  
    F.TOTAL  
FROM  
    IP_USUARIOS_PREDIAL  UP  
        INNER JOIN IP_FACTURADOS  F  
        ON  
            (UP.NUMERO_ORDEN = F.NUMERO_ORDEN)  
                AND  
            (UP.CODIGO = F.CODIGO)  
                AND  
            (UP.COMPANIA = F.COMPANIA)  
WHERE  
    UP.COMPANIA = s$compania$s  
        AND  
    UP.CODIGO = 's$codigoPredio$s'  
   
        AND  
    UP.NUMERO_ORDEN = 's$numeroOrden$s'  
   
        AND  
    F.PAGADO IN(0)  
        AND  
    F.NOCOBRADO IN(0)
