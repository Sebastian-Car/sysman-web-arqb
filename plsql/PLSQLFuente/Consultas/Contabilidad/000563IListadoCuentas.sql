SELECT DISTINCT COMPANIA,  
       ANO,  
       CODIGO,  
       NOMBRE,  
       NATURALEZA,  
       DECODE(MOVIMIENTO,  '-1','SI','NO')  MOVIMIENTO,  
       DECODE(MAN_CEN_CTO, '-1','SI','NO')  MAN_CEN_CTO,  
       DECODE(MAN_AUX_TER, '-1','SI','NO')  MAN_AUX_TER,  
       DECODE(MAN_AUX_GEN, '-1','SI','NO')  MAN_AUX_GEN,  
       DECODE(MAN_AUX_REF, '-1','SI','NO')  MAN_AUX_REF,  
       DECODE(MAN_AUX_FUE, '-1','SI','NO')  MAN_AUX_FUE,  
       DINAMICA,  
       CUENTA_PPTAL,  
       PRESUPUESTO_ANUAL,  
       CORRIENTE,  
       FORMATO,  
       CLASECUENTA 
 FROM   PLAN_CONTABLE  
 WHERE COMPANIA =s$compania$s     
      AND ANO =  s$anio$s    
      AND CODIGO BETWEEN 's$codigoInicial$s' AND 's$codigoFinal$s' 
 ORDER BY CODIGO
