SELECT sp_LogProcesos.usuario AS nombrelargo,
    sp_LogProcesos.fechaproceso AS fechaProceso, 
    sp_LogProcesos.horaproceso AS horaProceso, 
    sp_LogProcesos.descripcion AS descripcion, 
    sp_LogProcesos.parametros AS parametros, 
    sp_LogProcesos.resultados AS resultados
FROM sp_LogProcesos LEFT JOIN sp_TipoProceso 
    ON sp_LogProcesos.Compania = sp_TipoProceso.Compania 
    AND sp_LogProcesos.Proceso = sp_TipoProceso.Codigo
WHERE sp_LogProcesos.Compania=s$compania$s
    AND sp_LogProcesos.fechaproceso Between s$fechaInicial$s  And s$fechaFinal$s
    AND sp_LogProcesos.Proceso='s$tipoProceso$s'
