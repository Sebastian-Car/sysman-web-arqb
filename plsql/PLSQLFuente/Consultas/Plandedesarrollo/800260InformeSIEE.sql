SELECT  CODIGO_DEPARTAMENTO "Cod Dpto",
        NOMBRE_DEPARTAMENTO "Departamento",
        CODIGO_MUNICIPIO "Cod Mpio",
        NOMBRE_MUNICIPIO "Municipio",
        RESULTADO "Resultado",
        INDICADOR_RESULTADO "Indicador Resultado",
        LB_RESULTADO "LB Resultado",
        META_RESULTADO "Meta Resultado",
        ' ' "Código Producto",
        PRODUCTO "Producto",
        ' ' "Alerta Descripción",
        INDICADOR_PRODUCTO "Indicador Producto",
        LB_PRODUCTO "LB Producto",
        META_PRODUCTO "Meta Producto",
        ORIENTACION "Orientacion",
        CODIGO_SECTOR "Código Sector",
        SECTOR "Sector",
        ODS ,
        PILAR_MARCO "Pilar Marco Implementación",
        META "Valor Esperado",
        AVANCE "Valor Ejecutado",
        EJECUTADO "% Avance",
        ' '  "BPIN",
        ' ' "Observaciones",
        'Normal' "Priorizada",
        COFDPTO "Prog Cof Dpto" ,
        COFNACION "Prog Cof Nación",  
        CREDITO "Prog Crédito" , 
        OTROS "Prog Otros",  
        RECURSOSPROPIOS "Prog Recursos Propios",  
        SGPALIMESCOLAR "Prog SGP Alim Escolar",  
        SGPAPSB "Prog SGP APSB",
        SGPCULTURA "Prog SGP Cultura",
        SGPDEPORTE "Prog SGP Deporte",
        SGPEDUCACION "Prog SGP Educación",
        SGPLIBREDEST "Prog SGP Libre Dest", 
        SGPLIBREINV "Prog SGP Libre Inv", 
        SGPRIOMAGDALENA "Prog SGP RioMagdalena", 
        SGPRIMERAINFANCIA "Prog SGP Primera Infancia", 
        SGPSALUD "Prog SGP Salud", 
        REGALIAS "Prog Regalías",
        FUNCIONAMIENTO "Prog Funcionamiento",
        
        (COFDPTO +
        COFNACION +
        CREDITO +
        OTROS +
        RECURSOSPROPIOS +
        SGPALIMESCOLAR +
        SGPAPSB +
        SGPCULTURA +
        SGPDEPORTE +
        SGPEDUCACION +
        SGPLIBREDEST +
        SGPLIBREINV +
        SGPRIOMAGDALENA +
        SGPRIMERAINFANCIA +
        SGPSALUD +
        REGALIAS +
        FUNCIONAMIENTO) "Prog Total",
        
        COFDPTO_OBL "Ejec Cof Dpto" ,
        COFNACION_OBL "Ejec Cof Nación",  
        CREDITO_OBL "Ejec Crédito" , 
        OTROSDNP_OBL "Ejec Otros",  
        RECURSOSPROPIOS_OBL "Ejec Recursos Propios",  
        SGPALIMESCOLAR_OBL "Ejec SGP Alim Escolar",  
        SGPAPSB_OBL "Ejec SGP APSB",
        SGPCULTURA_OBL "Ejec SGP Cultura",
        SGPDEPORTE_OBL "Ejec SGP Deporte",
        SGPEDUCACION_OBL "Ejec SGP Educación",
        SGPLIBREDEST_OBL "Ejec SGP Libre Dest", 
        SGPLIBREINV_OBL "Ejec SGP Libre Inv", 
        SGPRIOMAGDALENA_OBL "Ejec SGP RioMagdalena", 
        SGPRIMERAINFANCIA_OBL "Ejec SGP Primera Infancia", 
        SGPSALUD_OBL "Ejec SGP Salud", 
        REGALIAS_OBL "Ejec Regalías",
        FUNCIONAMIENTO_OBL "Ejec Funcionamiento", 
  
        (COFDPTO_OBL +
        COFNACION_OBL +
        CREDITO_OBL +
        OTROSDNP_OBL +
        RECURSOSPROPIOS_OBL +
        SGPALIMESCOLAR_OBL +
        SGPAPSB_OBL +
        SGPCULTURA_OBL +
        SGPDEPORTE_OBL +
        SGPEDUCACION_OBL +
        SGPLIBREDEST_OBL +
        SGPLIBREINV_OBL +
        SGPRIOMAGDALENA_OBL +
        SGPRIMERAINFANCIA_OBL +
        SGPSALUD_OBL +
        REGALIAS_OBL +
        FUNCIONAMIENTO_OBL) "Ejec Total"
FROM V_PLAN_INDICATIVO 
WHERE COMPANIA = s$compania$s
AND VIGENCIA_META = s$ano$s
