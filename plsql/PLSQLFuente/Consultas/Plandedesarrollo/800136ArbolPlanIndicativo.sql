SELECT ID,DESCRIPCION NOMBRES 
FROM   BP_PLAN_INDICATIVO 
WHERE  COMPANIA         = s$compania$s
  AND  VIGENCIA_INICIAL = s$vigenciaInicial$s
  AND  LENGTH(ID)      <> s$accion$s
  AND  (CASE
          WHEN s$esAdministrador$s = 0
          THEN LENGTH(ID)    
          ELSE -1
        END   <> s$metaProducto$s 
        OR 
        DEPENDENCIA = 
        CASE
          WHEN s$esAdministrador$s = 0
          THEN s$dependencia$s
          ELSE DEPENDENCIA
        END )
UNION 
SELECT ID,DESCRIPCION  NOMBRES 
FROM   BP_PLAN_INDICATIVO 
WHERE  COMPANIA         = s$compania$s
  AND  VIGENCIA_INICIAL = s$vigenciaInicial$s
  AND  PREDECESOR       IN (SELECT PREDECESOR 
                              FROM BP_PLAN_INDICATIVO
                             WHERE  COMPANIA         = s$compania$s
                               AND  VIGENCIA_INICIAL =  s$vigenciaInicial$s)
  AND  LENGTH(ID)      <> s$accion$s
  AND  (CASE
          WHEN s$esAdministrador$s = 0
          THEN LENGTH(ID)    
          ELSE -1
        END   <> s$metaProducto$s 
        OR 
        DEPENDENCIA = 
        CASE
          WHEN s$esAdministrador$s = 0
          THEN s$dependencia$s
          ELSE DEPENDENCIA
        END )
