UNION
 
SELECT ID,
       MES, 
       TO_NUMBER('') DIA,
       ANO,
       TO_NUMBER('') COMPROBANTE,
       '' CLASE,
       '' SIGNO,
       TO_NUMBER('') VALOR,
       '.' CLASELETRAS,     
       SUM(TOTALREO) CXPCONSTITUIDAS,
       TO_NUMBER('') INICIAL,
       TO_NUMBER('') CANCELACION,
       TO_NUMBER('') EJECUCION, 
       TO_NUMBER('') REVERSION,
       SUM(EJECUCIONPPT - REINTEGRO) TOTALPAGOS,
       '' DESCRIPCION, 
       NOMBRE, 
       TO_DATE('') FECHA,
       NIVEL1,
       NIVEL2,
       NIVEL3,
       NIVEL4,
       NIVEL5,
       NIVEL6,
       NIVEL6 NIVEL7,
       CASE DESTINO 
            WHEN 'F' THEN 'Funcionamiento'
            WHEN 'I' THEN 'Inversión'
            WHEN 'S' THEN 'Servicio de la Deuda'
       END DESTINO,
       '' NOMBRETERCERO,
       TO_NUMBER('') CUENTASXPAGAR,
       INITCAP(CASE WHEN DESTINO = 'I' 
                    THEN PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => s$compania$s,
                                               UN_NOMBRE    => 'NIVEL 1I',
                                               UN_MODULO 	  => PCK_DATOS.FC_MODULOENTESDECONTROL,
                                               UN_FECHA_PAR => SYSDATE)
                    ELSE PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => s$compania$s,
                                               UN_NOMBRE    => 'NIVEL 1F',
                                               UN_MODULO 	  => PCK_DATOS.FC_MODULOENTESDECONTROL,
                                               UN_FECHA_PAR => SYSDATE)
               END) NIVEL1ET,
       INITCAP(CASE WHEN DESTINO = 'I' 
                    THEN PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => s$compania$s,
                                               UN_NOMBRE    => 'NIVEL 2I',
                                               UN_MODULO 	  => PCK_DATOS.FC_MODULOENTESDECONTROL,
                                               UN_FECHA_PAR => SYSDATE)
                    ELSE PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => s$compania$s,
                                               UN_NOMBRE    => 'NIVEL 2F',
                                               UN_MODULO 	  => PCK_DATOS.FC_MODULOENTESDECONTROL,
                                               UN_FECHA_PAR => SYSDATE)
               END) NIVEL2ET,
       INITCAP(CASE WHEN DESTINO = 'I' 
                    THEN PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => s$compania$s,
                                               UN_NOMBRE    => 'NIVEL 3I',
                                               UN_MODULO 	  => PCK_DATOS.FC_MODULOENTESDECONTROL,
                                               UN_FECHA_PAR => SYSDATE)
                    ELSE PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => s$compania$s,
                                               UN_NOMBRE    => 'NIVEL 3F',
                                               UN_MODULO 	  => PCK_DATOS.FC_MODULOENTESDECONTROL,
                                               UN_FECHA_PAR => SYSDATE)
               END) NIVEL3ET,
       INITCAP(CASE WHEN DESTINO = 'I' 
                    THEN PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => s$compania$s,
                                               UN_NOMBRE    => 'NIVEL 4I',
                                               UN_MODULO 	  => PCK_DATOS.FC_MODULOENTESDECONTROL,
                                               UN_FECHA_PAR => SYSDATE)
                    ELSE PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => s$compania$s,
                                               UN_NOMBRE    => 'NIVEL 4F',
                                               UN_MODULO 	  => PCK_DATOS.FC_MODULOENTESDECONTROL,
                                               UN_FECHA_PAR => SYSDATE)
               END) NIVEL4ET,
       INITCAP(CASE WHEN DESTINO = 'I' 
                    THEN PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => s$compania$s,
                                               UN_NOMBRE    => 'NIVEL 5I',
                                               UN_MODULO 	  => PCK_DATOS.FC_MODULOENTESDECONTROL,
                                               UN_FECHA_PAR => SYSDATE)
                    ELSE PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => s$compania$s,
                                               UN_NOMBRE    => 'NIVEL 5F',
                                               UN_MODULO 	  => PCK_DATOS.FC_MODULOENTESDECONTROL,
                                               UN_FECHA_PAR => SYSDATE)
               END) NIVEL5ET,
       INITCAP(CASE WHEN DESTINO = 'I' 
                    THEN PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => s$compania$s,
                                               UN_NOMBRE    => 'NIVEL 6I',
                                               UN_MODULO 	  => PCK_DATOS.FC_MODULOENTESDECONTROL,
                                               UN_FECHA_PAR => SYSDATE)
                    ELSE PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => s$compania$s,
                                               UN_NOMBRE    => 'NIVEL 6F',
                                               UN_MODULO 	  => PCK_DATOS.FC_MODULOENTESDECONTROL,
                                               UN_FECHA_PAR => SYSDATE)
               END) NIVEL6ET
 FROM V_RESUMENPPTO_BASE
 WHERE COMPANIA = s$compania$s
   AND ANO      = s$anio$s
   AND ID BETWEEN 's$cuentaInicial$s' AND 's$cuentaFinal$s'
   AND LENGTH(ID) <= s$nivel$s
   AND MES BETWEEN s$mesInicial$s AND s$mesFinal$s
   AND MOVIMIENTO NOT IN(0)
   AND NATURALEZA ='D'
 GROUP BY COMPANIA, 
		   ANO, 
       MES,
		   ID, 
		   CODIGO,
		   NATURALEZA,
		   CENTRO_COSTO,
		   TERCERO,
		   SUCURSAL,
		   AUXILIAR,
		   REFERENCIA,  
		   FUENTE_RECURSO,
		   MOVIMIENTO,
       NOMBRE, 
       NIVEL1,
       NIVEL2,
       NIVEL3,
       NIVEL4,
       NIVEL5,
       NIVEL6, 
       DESTINO
