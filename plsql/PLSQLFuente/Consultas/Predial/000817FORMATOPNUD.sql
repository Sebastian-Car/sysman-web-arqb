SELECT 
    T.TRPRAN,
    T.TRPRAN1,
    T.TRPRAN2,
    UP.COMPANIA,
    UP.CODIGO,
    UP.NUMERO_ORDEN,
    UP.NIT,
    UP.DIRECCION,
    UP.AREA_HA,
    UP.AREA_M2,
    UP.AREA_CONSTRUIDA,
    UP.PAGO_ANO,
    UP.PAG_VAL,
    NVL(TO_CHAR(UP.PAG_FEC, 'DD/MM/YYYY'),'No registrada') PAG_FEC,
   NVL(UP.NUM_COM, ' ') NUM_COM,
    UP.DIRECCION_CORRESPONDENCIA,
    UP.COD_POSTAL,
    UP.RECIBO_ACTUAL,
    UP.NOMBRE,
    RP.PREVAL,
    TO_CHAR(RP.PREFECLIM, 'DD/MM/YYYY') PREFECLIM,
    '  ' INFO,
    RP.DOCNUM,
    SUBSTR(CHR(205)||CHR(102)||CHR(40)||'415'||CHR(41)
    	   ||s$strCodigoEAN$s||CHR(40)||'8020'||CHR(41)
    	   ||CODIGO||SUBSTR(LPAD(CASE WHEN INSTR(RP.DOCNUM,'EX',1) NOT IN (0)
    	   	                          THEN REPLACE(RP.DOCNUM,'EX','00') 
                                      ELSE RP.DOCNUM  
    	   	                     END,9,'0')       
    	                   ,1,9)  
    	   ||CHR(40)||'3900'||CHR(41)
    	   ||LPAD(RP.PREVAL,14,0)
    	   ||CHR(40)||'96'||CHR(41)
    	   ||TO_CHAR(RP.PREFECLIM,'YYYYMMDD'),3) PIEBARRA,
    TO_CHAR(PCK_CODIGODEBARRAS.FC_IMPRIMIRCODIGODEBARRAS(
    	                       CHR(205)||CHR(102)||'415'
    	                       ||s$strCodigoEAN$s||'8020'  
    	                       ||CODIGO  
    	                       ||SUBSTR(LPAD(CASE WHEN INSTR(RP.DOCNUM,'EX',1) NOT IN (0)
    	   	                                      THEN REPLACE(RP.DOCNUM,'EX','00') 
                                                  ELSE RP.DOCNUM  
    	   	                                 END
    	   	                                ,9,'0')       
    	                               ,1,9)  
    	                       ||CHR(102)||'3900'||LPAD(RP.PREVAL,14,0)
    	                       ||CHR(102)||'96'
    	                       ||TO_CHAR(RP.PREFECLIM,'YYYYMMDD'))) CODIGODEBARRAS_BIN
FROM IP_USUARIOS_PREDIAL UP INNER JOIN IP_TARIFAS T ON 
    UP.COMPANIA = T.COMPANIA
    AND UP.TRPCOD  = T.TRPCOD
    AND UP.TRPRAN  = T.TRPRAN
    AND UP.TRPANO  = T.TRPANO
INNER JOIN IP_RECIBOS_DE_PAGO RP ON 
    UP.COMPANIA      = RP.COMPANIA
    AND UP.NUMERO_ORDEN = RP.NUMERO_ORDEN
    AND UP.CODIGO       = RP.PRECOD
WHERE UP.COMPANIA   = s$compania$s
  s$condicion$s
