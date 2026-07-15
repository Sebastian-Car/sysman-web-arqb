SELECT PERSONAL.ID_DE_EMPLEADO,
                          PERSONAL.APELLIDO1
                          || ' ' ||
                          PERSONAL.APELLIDO2
                          || ' ' || 
                          PERSONAL.NOMBRES AS NOMCOMPLETO,
                          PERSONAL.NUMERO_DCTO,
                          PERSONAL.FECHANCTO,
                          TO_CHAR(PERSONAL.FECHA_DE_INGRESO,'DD/MM/YYYY') FECHA_DE_INGRESO,
                          TO_CHAR(PERSONAL.FECHA_DE_RETIRO,'DD/MM/YYYY') FECHA_DE_RETIRO,
                          PERSONAL.ESTADO_ACTUAL,
                          CARGOS.NOMBRE_DEL_CARGO,
                          CATEGORIA.SALARIO_BASE,
                          DEPENDENCIA.CODIGO DEPENDENCIA,
                          DEPENDENCIA.NOMBRE,
                          CENTRO_COSTO.CODIGO ID_CENTRO_DE_COSTO,
                          CENTRO_COSTO.NOMBRE NOMBRE_CENTRO_DE_COSTO,
                         TO_NUMBER( PCK_SYSMAN_UTL.FC_EDAD (UN_FECHAINI => PERSONAL.FECHANCTO 
                                                 ,UN_FECHAFIN => s$fecha$s 
                                                 ,UN_FORMATO  => 4))  EDAD,
                          PCK_SYSMAN_UTL.FC_EDAD (UN_FECHAINI => PERSONAL.FECHA_DE_INGRESO
                                                , UN_FECHAFIN => CASE
                                                                   WHEN FECHA_DE_RETIRO IS NOT NULL
                                                                   THEN FECHA_DE_RETIRO + 1
                                                                   ELSE s$fecha$s
                                                                END
                                                , UN_FORMATO => 1
                                                , UN_EDADPERSONA => 0) TSERV
                  FROM PERSONAL
                  INNER JOIN CENTRO_COSTO
                    ON PERSONAL.COMPANIA            = CENTRO_COSTO.COMPANIA
                    AND PERSONAL.ANO                = CENTRO_COSTO.ANO
                    AND PERSONAL.ID_CENTRO_DE_COSTO = CENTRO_COSTO.CODIGO
                  INNER JOIN CARGOS
                    ON PERSONAL.COMPANIA     = CARGOS.COMPANIA
                    AND PERSONAL.ID_DE_CARGO = CARGOS.ID_DE_CARGO
                  INNER JOIN DEPENDENCIA
                    ON PERSONAL.COMPANIA     = DEPENDENCIA.COMPANIA
                    AND PERSONAL.DEPENDENCIA = DEPENDENCIA.CODIGO
                  INNER JOIN CATEGORIA
                    ON PERSONAL.COMPANIA                                = CATEGORIA.COMPANIA
                    AND PERSONAL.ESCALAFON                              = CATEGORIA.ESCALAFON
                    AND PERSONAL.ID_DE_CATEGORIA                        = CATEGORIA.ID_DE_CATEGORIA
                    AND PERSONAL.ANO                                    = CATEGORIA.ANO
                  WHERE PERSONAL.COMPANIA         = 's$compania$s' 
                    AND (PERSONAL.FECHA_DE_RETIRO <= s$fecha$s 
                         OR PERSONAL.FECHA_DE_RETIRO IS NULL ) 
                    AND PERSONAL.ESTADO_ACTUAL  NOT IN (3)  
                  s$condicion$s
