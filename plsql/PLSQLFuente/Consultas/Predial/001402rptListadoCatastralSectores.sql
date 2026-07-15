SELECT SUBSTR(CODIGO,1,2) UR, 
       SUBSTR(CODIGO,3,2) SC, 
       SUBSTR(CODIGO,5,4) MN, 
       SUM(CASE WHEN NUMERO_ORDEN = '001'
                THEN 1
                ELSE 0
           END)  S1,
       COUNT(CODIGO)  S2, 
       SUM(CASE WHEN NUMERO_ORDEN = '001' 
                THEN AREA_M2 + AREA_HA * 10000
                ELSE 0
           END)  S3, 
       SUM(CASE WHEN NUMERO_ORDEN = '001' 
                THEN AREA_CONSTRUIDA
                ELSE 0
           END)  S4, 
       SUM(CASE WHEN NUMERO_ORDEN = '001' 
                THEN 
                  CASE WHEN AREA_CONSTRUIDA <= 0
                       THEN 1
                       ELSE 0
                  END
                ELSE 0
           END)  S5, 
       SUM(CASE WHEN NUMERO_ORDEN = '001' 
                THEN 
                  CASE WHEN AREA_CONSTRUIDA > 0
                       THEN 1
                       ELSE 0
                  END
                ELSE 0
           END)  S6,
       SUM(CASE WHEN NUMERO_ORDEN = '001' 
                THEN 
                  CASE WHEN AREA_CONSTRUIDA > 0
                       THEN AVALUO_ANO
                       ELSE 0
                  END
                ELSE 0
           END)  S7, 
       SUM(CASE WHEN NUMERO_ORDEN = '001' 
                THEN 
                  CASE WHEN AREA_CONSTRUIDA <= 0
                       THEN AVALUO_ANO
                       ELSE 0
                  END
                ELSE 0
           END)  S8
  FROM IP_USUARIOS_PREDIAL
 WHERE COMPANIA = s$compania$s
   AND SUBSTR(CODIGO,1,8)  BETWEEN 's$sectorInicial$s' AND 's$sectorFinal$s'
GROUP BY SUBSTR(CODIGO,1,2), 
         SUBSTR(CODIGO,3,2), 
         SUBSTR(CODIGO,5,4)  
 ORDER BY SUBSTR(CODIGO,1,2) DESC, 
         SUBSTR(CODIGO,3,2) DESC, 
         SUBSTR(CODIGO,5,4)  
DESC
 
