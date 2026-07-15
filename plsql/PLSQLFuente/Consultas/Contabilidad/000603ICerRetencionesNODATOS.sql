SELECT  COMPANIA, 
        NIT TERCERO, 
        NOMBRE TERCERO_NOMBRE, 
        CASE WHEN CODIGOPOSTAL IS NULL OR CODIGOPOSTAL=''
             THEN 's$codigoPostal$s'
             ELSE TO_CHAR(CODIGOPOSTAL)   
             END CODPOSTAL, 
        0 TOTALRETENIDO
FROM  TERCERO
WHERE COMPANIA    =s$compania$s 
  AND s$filtroNitONombre$s BETWEEN 's$tercero1$s' 
                               AND 's$tercero2$s'
