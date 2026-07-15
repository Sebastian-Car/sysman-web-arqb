SELECT 
   IP_PAGOS_WEB.REFERENCIA
  ,CASE WHEN IP_PAGOS_WEB.CODIGO_PREDIO = '999999999999999'
        THEN 'No Registrado'
        ELSE IP_PAGOS_WEB.CODIGO_PREDIO 
   END CODIGO_PREDIO
  ,IP_PAGOS_WEB.NUMERO_ORDEN
  ,IP_PAGOS_WEB.FECHA_EXPEDICION
  ,IP_PAGOS_WEB.VALOR
  ,IP_PAGOS_WEB.FECHA_LIMITE_PAG
  ,IP_PAGOS_WEB.ANO_GENERADO
  ,IP_USUARIOS_PREDIAL.DIRECCION
  ,TO_CHAR(FECHA_LIMITE_PAG,'DD-MON-YYYY') PAGUEANTES
  ,s$nombres$s AS NOMBRES
  ,s$nit$s AS NIT
  ,TO_CHAR(
     PCK_CODIGODEBARRAS.FC_IMPRIMIRCODIGODEBARRAS(
       CHR(205)                            ||    
       CHR(102)                            ||    
       '415'                               ||    
       's$codigoEAN$s'                     ||    
       '8020'                              ||    
       s$codFac$s                          ||    
       SUBSTR(IP_PAGOS_WEB.REFERENCIA,1,9) ||    
       '3900'                              ||    
       LPAD(IP_PAGOS_WEB.VALOR,14,'0')     ||    
       '96'                                ||    
       TO_CHAR(FECHA_LIMITE_PAG,'YYYYMMDD'))
           ) AS CODIGODEBARRAS_BIN
  ,CHR(40) || 
   '415'   || 
   CHR(41)                             || 
   's$codigoEAN$s'                     || 
   CHR(40)                             || 
   '8020'                              || 
   CHR(41)                             ||
   s$codFac$s                          || 
   SUBSTR(IP_PAGOS_WEB.REFERENCIA,1,9) || 
   CHR(40)                             || 
   '3900'                              || 
   CHR(41)                             || 
   LPAD(IP_PAGOS_WEB.VALOR,14,'0')     || 
   CHR(40)                             || 
   '96'                                || 
   CHR(41)                             || 
   TO_CHAR(FECHA_EXPEDICION,'YYYYMMDD') AS BARRAS 
FROM IP_PAGOS_WEB  
  LEFT JOIN IP_USUARIOS_PREDIAL 
     ON IP_PAGOS_WEB.COMPANIA      = IP_USUARIOS_PREDIAL.COMPANIA 
    AND IP_PAGOS_WEB.NUMERO_ORDEN  = IP_USUARIOS_PREDIAL.NUMERO_ORDEN 
    AND IP_PAGOS_WEB.CODIGO_PREDIO = IP_USUARIOS_PREDIAL.CODIGO 
WHERE IP_PAGOS_WEB.COMPANIA  =s$compania$s 
  AND IP_PAGOS_WEB.REFERENCIA=s$recibo$s 
  AND IP_PAGOS_WEB.TIPO      ='PS'
