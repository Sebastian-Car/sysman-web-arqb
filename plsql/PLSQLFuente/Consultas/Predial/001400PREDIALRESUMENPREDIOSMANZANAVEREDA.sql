SELECT SUBSTR(IP_USUARIOS_PREDIAL.CODIGO,1,2) AS UR, 
       SUBSTR(IP_USUARIOS_PREDIAL.CODIGO,3,2) AS SC, 
       SUBSTR(IP_USUARIOS_PREDIAL.CODIGO,5,4) AS MN, 
       SUM(CASE WHEN NUMERO_ORDEN = '001'
                THEN 1
                ELSE 0
           END) AS S1, 
       COUNT(IP_USUARIOS_PREDIAL.CODIGO) AS S2, 
       SUM(CASE WHEN NUMERO_ORDEN = '001' 
                THEN AREA_M2 + AREA_HA * 10000
                ELSE 0
           END) AS S3, 
       SUM(CASE WHEN NUMERO_ORDEN = '001' 
                THEN AREA_CONSTRUIDA
                ELSE 0
           END) AS S4, 
       SUM(CASE WHEN NUMERO_ORDEN = '001' 
                THEN 
                  CASE WHEN AREA_CONSTRUIDA <= 0
                       THEN 1
                       ELSE 0
                  END
                ELSE 0
           END) AS S5, 
       SUM(CASE WHEN NUMERO_ORDEN = '001' 
                THEN 
                  CASE WHEN AREA_CONSTRUIDA > 0
                       THEN 1
                       ELSE 0
                  END
                ELSE 0
           END) AS S6,
       SUM(CASE WHEN NUMERO_ORDEN = '001' 
                THEN 
                  CASE WHEN AREA_CONSTRUIDA > 0
                       THEN AVALUO_ANO
                       ELSE 0
                  END
                ELSE 0
           END) AS S7, 
       SUM(CASE WHEN NUMERO_ORDEN = '001' 
                THEN 
                  CASE WHEN AREA_CONSTRUIDA <= 0
                       THEN AVALUO_ANO
                       ELSE 0
                  END
                ELSE 0
           END) AS S8, 
       M_INMOBILIARIA,
       COMPANIA.NOMBRE NOMBRECOMPANIA
  FROM IP_USUARIOS_PREDIAL 
        INNER JOIN COMPANIA
              ON IP_USUARIOS_PREDIAL.COMPANIA = COMPANIA.CODIGO 
WHERE IP_USUARIOS_PREDIAL.COMPANIA =  s$compania$s  
 GROUP BY SUBSTR(IP_USUARIOS_PREDIAL.CODIGO,1,2), 
         SUBSTR(IP_USUARIOS_PREDIAL.CODIGO,3,2), 
         SUBSTR(IP_USUARIOS_PREDIAL.CODIGO,5,4),
         M_INMOBILIARIA,
         COMPANIA.NOMBRE    
   
 ORDER BY SUBSTR(IP_USUARIOS_PREDIAL.CODIGO,5,4),
          SUBSTR(IP_USUARIOS_PREDIAL.CODIGO,1,2),
          SUBSTR(IP_USUARIOS_PREDIAL.CODIGO,3,2)    

