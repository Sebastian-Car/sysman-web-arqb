WITH TEMP_HISTORIA AS   
(SELECT HISTORIA.COMPANIA,   
        HISTORIA.CICLO,    
        HISTORIA.CODIGORUTA,    
        HISTORIA.ANO,    
        HISTORIA.PERIODO,   
        HISTORIA.CODIGOINTERNO,   
        CONCEPTO.CODIGO CONCEPTO,   
        CONCEPTO.NOMBRE,   
        NVL(CASE CONCEPTO.CODIGO   
            WHEN 1  THEN C1   
            WHEN 2  THEN C2    
            WHEN 3  THEN C3   
            WHEN 4  THEN C4  
            WHEN 5  THEN C5  
            WHEN 6  THEN C6  
            WHEN 7  THEN C7   
            WHEN 8  THEN C8   
            WHEN 9  THEN C9   
            WHEN 10 THEN C10  
            WHEN 11 THEN C11  
            WHEN 12 THEN C12  
            WHEN 13 THEN C13   
            WHEN 14 THEN C14   
            WHEN 15 THEN C15  
            WHEN 16 THEN C16   
            WHEN 17 THEN C17  
            WHEN 18 THEN C18   
            WHEN 19 THEN C19   
            WHEN 20 THEN C20   
            WHEN 21 THEN C21  
            WHEN 22 THEN C22  
            WHEN 23 THEN C23  
            WHEN 24 THEN C24  
            WHEN 25 THEN C25   
            WHEN 26 THEN C26   
            WHEN 27 THEN C27  
            WHEN 28 THEN C28  
            WHEN 29 THEN C29  
            WHEN 30 THEN C30  
            WHEN 31 THEN C31   
            WHEN 32 THEN C32   
            WHEN 33 THEN C33   
            WHEN 34 THEN C34   
            WHEN 35 THEN C35   
            WHEN 36 THEN C36   
            WHEN 37 THEN C37   
            WHEN 38 THEN C38   
            WHEN 39 THEN C39  
            WHEN 40 THEN C40   
            WHEN 41 THEN C41  
            WHEN 42 THEN C42  
            WHEN 43 THEN C43   
            WHEN 44 THEN C44   
            WHEN 45 THEN C45  
            WHEN 46 THEN C46  
            WHEN 47 THEN C47  
            WHEN 48 THEN C48  
            WHEN 49 THEN C49   
            WHEN 50 THEN C50   
            WHEN 246 THEN C246  
            WHEN 247 THEN C247  
            WHEN 248 THEN C248  
            WHEN 249 THEN C249  
            WHEN 250 THEN C250  
        END, 0) VALOR  ,
        PCK_SERVICIOS_PUBLICOS.FC_NOMBREPERIODO(UN_COMPANIA   => HISTORIA.COMPANIA,
                                                UN_ANO        => HISTORIA.ANO,
                                                UN_PERIODO    => HISTORIA.PERIODO,
                                                UN_FRECUENCIA => NULL) PERIODOA
   FROM SP_HISTORIA HISTORIA   
        INNER JOIN SP_CONCEPTOS CONCEPTO   
                ON HISTORIA.COMPANIA = CONCEPTO.COMPANIA   
  WHERE HISTORIA.COMPANIA   = s$compania$s   
    AND HISTORIA.CICLO      = s$ciclo$s    
    AND HISTORIA.CODIGORUTA = 's$codigoRuta$s'  
    AND (CODIGO BETWEEN 1 AND 50 OR CODIGO BETWEEN 246 AND 250))  
SELECT ID,  
       CONCEPTO,
       SUM(s$total$s) TOTAL, 
       s$columnas$s    
 FROM ( 
 SELECT *  
 FROM ( 
SELECT CONCEPTO ID,    
       NOMBRE CONCEPTO,   
       NVL(VALOR,0) VALOR,  
       PERIODOA 
  FROM TEMP_HISTORIA   
 WHERE VALOR NOT IN 0  
   AND ANO || PERIODO BETWEEN s$periodoInicial$s AND s$periodoFinal$s  
 ORDER BY ANO, PERIODO, CONCEPTO )   
PIVOT(SUM(NVL(VALOR,0)) FOR PERIODOA IN(s$listaPeriodos$s) )   
ORDER BY ID)    
GROUP BY  ID, CONCEPTO, s$groupBy$s    
ORDER BY ID      
