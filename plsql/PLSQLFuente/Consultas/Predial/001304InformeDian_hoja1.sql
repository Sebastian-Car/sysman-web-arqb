SELECT '1' "Tipo de registro",
      's$anio$s' "Año gravable",
      '81' "Codigo de entidad informante",
      CASE WHEN INSTR(COMPANIA.NITCOMPANIA, '-') > 0 
        THEN SUBSTR(COMPANIA.NITCOMPANIA, 1, INSTR(COMPANIA.NITCOMPANIA, '-') - 1)
        ELSE COMPANIA.NITCOMPANIA
      END NIT,
      COMPANIA.NOMBRE "Razón social",
      COMPANIA.DIRECCION "Dirección",
      COMPANIA.CIUDAD "Codigo del municipio",
      COMPANIA.DIRECCIONEMAIL "Correo electrónico"
FROM COMPANIA
WHERE COMPANIA.CODIGO = 's$compania$s'
