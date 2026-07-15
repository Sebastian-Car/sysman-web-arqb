SELECT 
      M.COMPANIA, 
      '' OBSERVACION_GENERAL, 
      '' SI_CUMPLEGENERAL, 
      '' NO_CUMPLEGENERAL, 
      M.CODIGO CODIGO_DET, 
      '' PROYECTO, 
      M.SECTOR, 
      M.SECCION, 
      M.ITEM, 
      '' SI_APLICA, 
      '' NO_APLICA, 
      '' SI_CUMPLE, 
      '' NO_CUMPLE, 
      '' OBSERVACION, 
      '' NOMBREPROYECTO, 
      '' OBJETO
  FROM BP_MODELO_FICHA_TECNICA M
  WHERE M.COMPANIA = s$compania$s 
       AND 
        M.SECTOR   ='s$sector$s'
  ORDER BY M.CODIGO, 
           M.SECTOR
