WITH RS AS (
    SELECT 
       CASE WHEN AREA_HA + AREA_M2/10000  BETWEEN 0 AND 1.0
          THEN 1
          WHEN AREA_HA + AREA_M2/10000 BETWEEN 1.0 AND 3.0
          THEN 2
          WHEN AREA_HA + AREA_M2/10000 BETWEEN 3.0 AND 5.0
          THEN 3
          WHEN AREA_HA + AREA_M2/10000 BETWEEN 5.0 AND 10.0
          THEN 4
          WHEN AREA_HA + AREA_M2/10000 BETWEEN 10.0 AND 15.0
          THEN 5
          WHEN AREA_HA + AREA_M2/10000 BETWEEN 15.0 AND 20.0
          THEN 6
          WHEN AREA_HA + AREA_M2/10000 BETWEEN 20.0 AND 50.0
          THEN 7
          WHEN AREA_HA + AREA_M2/10000 BETWEEN 50.0 AND 100.0
          THEN 8
          WHEN AREA_HA + AREA_M2/10000 BETWEEN 100.0 AND 200.0
          THEN 9
          WHEN AREA_HA + AREA_M2/10000 BETWEEN 200.0 AND 500.0
          THEN 10
          WHEN AREA_HA + AREA_M2/10000 BETWEEN 500.0 AND 1000.0
          THEN 11
          WHEN AREA_HA + AREA_M2/10000 BETWEEN 1000.0 AND 2000.0
          THEN 12
          WHEN AREA_HA + AREA_M2/10000 BETWEEN 2000.0 AND 99999999.0
          THEN 13
       END RANGO,
        AREA_HA+ AREA_M2/10000  AREAHEC,
        AREA_M2,
        AREA_CONSTRUIDA AREACONS, 
        AVALUO_ANO AVALUO,
        CODIGO,
        NUMERO_ORDEN,
        COMPANIA
    FROM IP_USUARIOS_PREDIAL 
    WHERE COMPANIA = s$compania$s 
    AND SUBSTR(CODIGO,1,2) IN '00'), 
RSCUENTA AS (
    SELECT RS.RANGO RANGO,
          COUNT (RS.RANGO) PREDIOS
    FROM RS
    GROUP BY RS.RANGO),
URB AS (
    SELECT 
       CASE WHEN Area_Ha *10000 + area_m2  BETWEEN 0 AND 50
          THEN 1
          WHEN Area_Ha *10000 + area_m2 BETWEEN 50 AND 100
          THEN 2
          WHEN Area_Ha *10000 + area_m2 BETWEEN 100 AND 200
          THEN 3
          WHEN Area_Ha *10000 + area_m2 BETWEEN 200 AND 300
          THEN 4
          WHEN Area_Ha *10000 + area_m2 BETWEEN 300 AND 400
          THEN 5
          WHEN Area_Ha *10000 + area_m2 BETWEEN 400 AND 500
          THEN 6
          WHEN Area_Ha *10000 + area_m2 BETWEEN 500 AND 1000
          THEN 7
          WHEN Area_Ha *10000 + area_m2 BETWEEN 1000 AND 2000
          THEN 8
          WHEN Area_Ha *10000 + area_m2 BETWEEN 2000 AND 3000
          THEN 9
          WHEN Area_Ha *10000 + area_m2 BETWEEN 3000 AND 4000
          THEN 10
          WHEN Area_Ha *10000 + area_m2 BETWEEN 4000 AND 5000
          THEN 11
          WHEN Area_Ha *10000 + area_m2 BETWEEN 5000 AND 10000
          THEN 12
          WHEN Area_Ha *10000 + area_m2 BETWEEN 10000 AND 99999999
          THEN 13
       END RANGO,
        AREA_HA+ AREA_M2/10000  AREAHEC,
        AREA_M2,
        AREA_CONSTRUIDA AREACONS, 
        AVALUO_ANO AVALUO,
        CODIGO,
        NUMERO_ORDEN,
        COMPANIA
    FROM IP_USUARIOS_PREDIAL 
    WHERE COMPANIA = s$compania$s
    AND SUBSTR(CODIGO,1,2) IN '01'), 
URBCUENTA AS(
    SELECT URB.RANGO RANGO,
          COUNT (URB.RANGO) PREDIOS
    FROM URB
    GROUP BY 
      URB.RANGO)
SELECT  RS.RANGO RSRANGO, 
        COUNT (RS.CODIGO) PROPIETARIOS, 
        RSCUENTA.PREDIOS, 
        SUM(RS.AREACONS) AREACONSTRUIDA,
        SUM(RS.AREAHEC) AREAHECTAREA,
        SUM(RS.AVALUO) AVALUO,
        DECODE(RS.RANGO
              ,1,'0 - 1 Has'
              ,2,'1 - 3 Has'
              ,3,'3 - 5 Has'
              ,4,'5 - 10 Has'
              ,5,'10 - 15 Has'
              ,6,'15 - 20 Has'
              ,7,'20 - 50 Has'
              ,8,'50 - 100 Has'
              ,9,'100 - 200 Has'
              ,10,'200 - 500 Has'
              ,11,'500 - 1000 Has'
              ,12,'1000 - 2000 Has'
              ,13,'2000 - 99999999 Has') RANGO,
        C.NOMBRE COMPANIA,
        1 ORDEN
FROM RS 
      INNER JOIN RSCUENTA 
            ON RS.RANGO = RSCUENTA.RANGO
      INNER JOIN COMPANIA C
            ON C.CODIGO = RS.COMPANIA
WHERE RS.NUMERO_ORDEN = '001'
GROUP BY RS.RANGO,
         RSCUENTA.PREDIOS,
         C.NOMBRE
UNION ALL
SELECT  URB.RANGO URBRANGO, 
        COUNT (URB.CODIGO) PROPIETARIOS, 
        URBCUENTA.PREDIOS,
        SUM(URB.AREACONS) AREACONSTRUIDA,
        SUM(URB.AREAHEC) AREAHECTAREA,
        SUM(URB.AVALUO) AVALUO,
        DECODE(URB.RANGO
              ,1,'0 - 50 M2'
              ,2,'50 - 100 M2'
              ,3,'100 - 200 M2'
              ,4,'200 - 300 M2'
              ,5,'300 - 400 M2'
              ,6,'400 - 500 M2'
              ,7,'500 - 1000 M2'
              ,8,'1000 - 2000 M2'
              ,9,'2000 - 3000 M2'
              ,10,'3000 - 4000 M2'
              ,11,'4000 - 5000 M2'
              ,12,'5000 - 10000 M2'
              ,13,'10000 - 99999999 M2') RANGO,
        C.NOMBRE COMPANIA,
        2 ORDEN 
FROM URB 
      INNER JOIN URBCUENTA 
            ON URB.RANGO = URBCUENTA.RANGO
      INNER JOIN COMPANIA C
            ON C.CODIGO = URB.COMPANIA
WHERE URB.NUMERO_ORDEN = '001'
GROUP BY  URB.RANGO,
          URBCUENTA.PREDIOS,
          C.NOMBRE
ORDER BY ORDEN,RSRANGO
