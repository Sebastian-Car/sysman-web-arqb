WITH QRY_DATOSPERACTUAL AS  ( 
                            SELECT  SP_USUARIO.CICLO, 
                                    SP_USOS.NOMBRE AS USO, 
                                    COUNT(SP_USUARIO.CODIGORUTA) AS USUARIOS_ACUEDUCTO, 
                                    SP_USUARIO.ANO, 
                                    SP_USUARIO.PERIODO, 
                                    SUM(SP_USUARIO.CARGOFIJO) AS SUMADECARGOFIJO, 
                                    SUM(SP_USUARIO.CONSUMO)   AS SUMADECONSUMO, 
                                    SP_USUARIO.ACUEDUCTO, 
                                    SP_USUARIO.ESTADO 
                            FROM SP_USUARIO INNER JOIN SP_USOS ON SP_USOS.CODIGO = SP_USUARIO.USO AND SP_USUARIO.COMPANIA = SP_USOS.COMPANIA 
                            WHERE SP_USUARIO.COMPANIA   = s$compania$s 
                            AND   SP_USUARIO.CARGOFIJO  = 0 
                            GROUP BY  SP_USUARIO.CICLO, 
                                      SP_USOS.NOMBRE, 
                                      SP_USUARIO.ANO, 
                                      SP_USUARIO.PERIODO, 
                                      SP_USUARIO.ACUEDUCTO, 
                                      SP_USUARIO.ESTADO 
                            HAVING SP_USUARIO.ANO     IN (SELECT MAX(ANO) AS ANOACTUAL FROM SP_USUARIO) 
                            AND SP_USUARIO.PERIODO    IN (SELECT MAX(PERIODO) AS PERIODOACTUAL FROM SP_USUARIO) 
                            AND SP_USUARIO.ACUEDUCTO  = -1 
                            AND SP_USUARIO.ESTADO     = 'A') 
SELECT  QRY_DATOSPERACTUAL.CICLO, 
        QRY_DATOSPERACTUAL.USO, 
        QRY_DATOSPERACTUAL.USUARIOS_ACUEDUCTO, 
        QRY_DATOSPERACTUAL.ANO, 
        QRY_DATOSPERACTUAL.PERIODO, 
        QRY_DATOSPERACTUAL.SUMADECARGOFIJO, 
        QRY_DATOSPERACTUAL.SUMADECONSUMO, 
        QRY_DATOSPERACTUAL.ACUEDUCTO, 
        QRY_DATOSPERACTUAL.ESTADO, 
        QRY_SINCARGOFIJO_ALC.USUARIOS_ALCANTARILLADO 
FROM QRY_DATOSPERACTUAL 
INNER JOIN  (SELECT SP_USUARIO.CICLO, 
                    SP_USOS.NOMBRE AS USO, 
                    COUNT(SP_USUARIO.CODIGORUTA) AS USUARIOS_ALCANTARILLADO, 
                    SP_USUARIO.ANO, 
                    SP_USUARIO.PERIODO, 
                    SUM(SP_USUARIO.CARGOFIJO) AS SUMADECARGOFIJO, 
                    SUM(SP_USUARIO.CONSUMO)   AS SUMADECONSUMO, 
                    SP_USUARIO.ALCANTARILLADO, 
                    SP_USUARIO.ESTADO 
              FROM SP_USUARIO INNER JOIN SP_USOS ON SP_USOS.CODIGO = SP_USUARIO.USO AND SP_USUARIO.COMPANIA = SP_USOS.COMPANIA 
              WHERE SP_USUARIO.COMPANIA     = s$compania$s 
              AND   SP_USUARIO.CARGOFIJOAL  = 0 
              GROUP BY  SP_USUARIO.CICLO, 
                        SP_USOS.NOMBRE, 
                        SP_USUARIO.ANO, 
                        SP_USUARIO.PERIODO, 
                        SP_USUARIO.ALCANTARILLADO, 
                        SP_USUARIO.ESTADO 
              HAVING  SP_USUARIO.ANO            IN (SELECT MAX(ANO) AS ANOACTUAL FROM SP_USUARIO) 
              AND     SP_USUARIO.PERIODO        IN (SELECT MAX(PERIODO) AS PERIODOACTUAL FROM SP_USUARIO) 
              AND     SP_USUARIO.ALCANTARILLADO = -1 
              AND     SP_USUARIO.ESTADO         = 'A' 
            ) QRY_SINCARGOFIJO_ALC 
ON  QRY_DATOSPERACTUAL.PERIODO = QRY_SINCARGOFIJO_ALC.PERIODO 
AND QRY_DATOSPERACTUAL.ANO     = QRY_SINCARGOFIJO_ALC.ANO 
AND QRY_DATOSPERACTUAL.CICLO   = QRY_SINCARGOFIJO_ALC.CICLO
