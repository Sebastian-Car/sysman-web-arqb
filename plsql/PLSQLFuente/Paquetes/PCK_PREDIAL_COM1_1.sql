create or replace PACKAGE BODY "PCK_PREDIAL_COM1" AS
/**@package:  Predial**/
--1
PROCEDURE PR_ANULAR_ABONO_SALDO
  /*
  NAME              : FC_ANULAR_ABONO_SALDO En Access
  AUTHORS           : SYSMAN  SAS 
  AUTHOR MIGRACION  : NICOLÁS GÓMEZ BARBOSA  
  DATE MIGRADOR     : 04/08/2016
  TIME              : 12:30 PM
  MODIFIER          : ELKIN GEOVANNY AMAYA SILVA
  DATE MODIFIED     : 19/07/2017
  MODIFICATIONS     : Se agregaron los campos de auditoria en las operaciones 
                      de actualizacion.Se cambió la función a procedimiento
                      Se cambió el estándar de codificación 
                      y se agregó manejo de excepciones.
  SOURCE MODULE     : PREDIAL
  DESCRIPTION       : Anula abono o Saldo Crédito
  @NAME:  anularAbonoSaldoCredito
  @METHOD:  POST 
  */
  (
  -- Parametro que recibe el nombre de la compania
  UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe el numero de documento a usar en la funcion
  UN_DOCNUM                   IN PCK_SUBTIPOS.TI_DOCNUM,
  -- Parametro que recibe el codigo del predio a usar en la funcion
  UN_PRECOD                   IN PCK_SUBTIPOS.TI_CODPREDIO,
  -- Parametro que recibe el usuario
  UN_USER                     IN PCK_SUBTIPOS.TI_USUARIO,
  -- Parametro que recibe el ano a usar en la funcion
  UN_PREANO                   IN PCK_SUBTIPOS.TI_ANIO,
  -- Parametro que recibe un valor booleano para verificar si se realizo el pago
  UN_PAGADO                   IN PCK_SUBTIPOS.TI_LOGICO

  )
  AS
    MI_ERROR_FUN              PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 1; 
    -- Variable que almacena la cadena de campos a modificar en la funcion.
    MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
    -- Variable que almacena la cadena de valores que se le van a asignar a los 
    -- campos en la funcion.
    MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
    -- Variable que almacenara la cadena que contiene la condicion para hacer 
    -- las modificaciones.
    MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
    -- Variable que almacenara el numero de registro que contiene una consulta.
    MI_I                      PCK_SUBTIPOS.TI_ENTERO_LARGO;   

  BEGIN

   -- BEGIN usado para el control de errores al realizar las actualizaciones
  BEGIN

  IF UN_PAGADO <> 0 THEN

    SELECT COUNT(*) INTO MI_I
      FROM IP_RECIBOS_DE_PAGO
     WHERE COMPANIA = UN_COMPANIA
       AND PRECOD   = UN_PRECOD
       AND PREANO   = UN_PREANO
       AND PAGO NOT IN 0
       AND ANULADO IN 0;

    IF MI_I <> 0 THEN

      SELECT COUNT(*) INTO MI_I
        FROM IP_FACTURADOS 
       WHERE COMPANIA = UN_COMPANIA
         AND CODIGO   = UN_PRECOD
         AND PREANO   = UN_PREANO;

      IF MI_I <> 0 THEN 
		<<ANULAFACTURADO>>
        FOR MI_RS IN (SELECT PREANO,
                             PAGADO,
                             OBSERVACIONES,
                             TOTAL 
                        FROM IP_FACTURADOS
                       WHERE COMPANIA = UN_COMPANIA
                         AND CODIGO   = UN_PRECOD
                         AND PREANO   = UN_PREANO)
        LOOP
        -- Se hacen modificaciones a algunos de los campos de las tablas IP_PAGOSDOBLES, IP_FACTURADOS, dependiendo
        -- del valor de los parametros que ingresan a la funcion  

          IF MI_RS.PAGADO IN (0) THEN 
           BEGIN
            BEGIN
              MI_CAMPOS:='ANULADO = -1
                         , FECHAANULADO=SYSDATE
                         , ANULADOPOR='''||UN_USER||'''
                         , DATE_MODIFIED = SYSDATE
                         , MODIFIED_BY   = '''||UN_USER||'''';

              MI_CONDICION := 'COMPANIA='''||UN_COMPANIA||''' 
                              AND DOCNUM='''||UN_DOCNUM||'''
                              AND PRECOD='''||UN_PRECOD||''' 
                              AND PREANO='||UN_PREANO;
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_PAGOSDOBLES', 
                                                     UN_ACCION   =>'M',
                                                     UN_CAMPOS   =>MI_CAMPOS,
                                                     UN_CONDICION=>MI_CONDICION
                                                     );   

                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                                     
           END;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                             PCK_ERR_MSG.RAISE_WITH_MSG(
                             UN_EXC_COD =>SQLCODE,
                             UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_ANULARABONOSALDO );
          END; 
          ELSE

            IF MI_RS.OBSERVACIONES = 'Vigencia puesta en pago - Total Cobro en Cero' THEN

             BEGIN
              BEGIN
                  MI_CAMPOS:='PAGADO = 0
                              , OBSERVACIONES= '' '' 
                              , DATE_MODIFIED = SYSDATE
                              , MODIFIED_BY   = '''||UN_USER||'''';

                  MI_CONDICION := 'COMPANIA='''||UN_COMPANIA||''' 
                               AND CODIGO='''||UN_PRECOD||''' 
                               AND PREANO='||UN_PREANO;
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOS', 
                                                         UN_ACCION   =>'M', 
                                                         UN_CAMPOS   =>MI_CAMPOS,
                                                         UN_CONDICION=>MI_CONDICION
                                                         );   

                               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                                     
             END;

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                               PCK_ERR_MSG.RAISE_WITH_MSG(
                               UN_EXC_COD =>SQLCODE,
                               UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_ANULARABONOSALDO );
            END; 

            BEGIN
             BEGIN
              MI_CAMPOS:='ANULADO = -1
                        , FECHAANULADO=SYSDATE
                        , ANULADOPOR='''||UN_USER||'''
                        , DATE_MODIFIED = SYSDATE
                        , MODIFIED_BY   = '''||UN_USER||'''';

              MI_CONDICION := 'COMPANIA='''||UN_COMPANIA||'''
                          AND DOCNUM='''||UN_DOCNUM||''' 
                          AND PRECOD='''||UN_PRECOD||''' 
                          AND PREANO='||UN_PREANO;
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_PAGOSDOBLES', 
                                                     UN_ACCION   =>'M', 
                                                     UN_CAMPOS   =>MI_CAMPOS,
                                                     UN_CONDICION=>MI_CONDICION
                                                     ); 

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                                     
           END;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                             PCK_ERR_MSG.RAISE_WITH_MSG(
                             UN_EXC_COD =>SQLCODE,
                             UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_ANULARABONOSALDO );
          END;                                                      
            ELSE
              IF MI_RS.TOTAL = 0 THEN
               BEGIN
                BEGIN
                  MI_CAMPOS:='PAGADO = 0
                              , OBSERVACIONES= '' '' 
                              , DATE_MODIFIED = SYSDATE
                              , MODIFIED_BY   = '''||UN_USER||'''';

                  MI_CONDICION := 'COMPANIA='''||UN_COMPANIA||'''
                               AND CODIGO='''||UN_PRECOD||''' 
                               AND PREANO='||UN_PREANO;
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOS', 
                                                         UN_ACCION   =>'M', 
                                                         UN_CAMPOS   =>MI_CAMPOS, 
                                                         UN_CONDICION=>MI_CONDICION
                                                         );  

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                                     
                END;

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                                 PCK_ERR_MSG.RAISE_WITH_MSG(
                                 UN_EXC_COD =>SQLCODE,
                                 UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_ANULARABONOSALDO );
             END;                                                          

                BEGIN
                 BEGIN
                  MI_CAMPOS:='ANULADO = -1
                            , FECHAANULADO=SYSDATE
                            , ANULADOPOR='''||UN_USER||'''
                            , DATE_MODIFIED = SYSDATE
                            , MODIFIED_BY   = '''||UN_USER||'''';

                  MI_CONDICION := 'COMPANIA='''||UN_COMPANIA||''' 
                               AND DOCNUM='''||UN_DOCNUM||''' 
                               AND PRECOD='''||UN_PRECOD||''' 
                               AND PREANO='||UN_PREANO;
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_PAGOSDOBLES',
                                                         UN_ACCION   =>'M',
                                                         UN_CAMPOS   =>MI_CAMPOS,
                                                         UN_CONDICION=>MI_CONDICION
                                                         );      

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                                     
                 END;

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                                   PCK_ERR_MSG.RAISE_WITH_MSG(
                                   UN_EXC_COD =>SQLCODE,
                                   UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_ANULARABONOSALDO );
                END;                                                        

              ELSE
               BEGIN
                BEGIN

                    MI_CAMPOS:='OBS_ANULACION = ''El proceso se cancelo debido a que el facturado no cumple con las caracteristica para reversar el pago''
                              , DATE_MODIFIED = SYSDATE
                              , MODIFIED_BY   = '''||UN_USER||'''';

                    MI_CONDICION := 'COMPANIA='''||UN_COMPANIA||'''
                                AND DOCNUM='''||UN_DOCNUM||''' 
                                AND PRECOD='''||UN_PRECOD||''' 
                                AND PREANO='||UN_PREANO;


                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_PAGOSDOBLES',
                                                           UN_ACCION   =>'M', 
                                                           UN_CAMPOS   =>MI_CAMPOS, 
                                                           UN_CONDICION=>MI_CONDICION
                                                           );

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                                     


                   IF PCK_DATOS.GL_RTA = 1 THEN
                    BEGIN
                     BEGIN 
                        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;  

                     END;

                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                                         PCK_ERR_MSG.RAISE_WITH_MSG(
                                         UN_EXC_COD =>SQLCODE,
                                         UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_FACTNOCUMPLE );
                    END;    

                   END IF;
               END;

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                                 PCK_ERR_MSG.RAISE_WITH_MSG(
                                 UN_EXC_COD =>SQLCODE,
                                 UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_ANULARABONOSALDO );
              END; 


              END IF; 
            END IF;
          END IF;
        END LOOP ANULAFACTURADO;
      ELSE

       BEGIN
        BEGIN
            MI_CAMPOS:='OBS_ANULACION = ''No se encontró el registro de facturado, se cancela el proceso''
                      , DATE_MODIFIED = SYSDATE
                      , MODIFIED_BY   = '''||UN_USER||'''';

            MI_CONDICION := 'COMPANIA='''||UN_COMPANIA||''' 
                         AND DOCNUM='''||UN_DOCNUM||''' 
                         AND PRECOD='''||UN_PRECOD||''' 
                         AND PREANO='||UN_PREANO;

            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_PAGOSDOBLES',
                                                   UN_ACCION   =>'M',
                                                   UN_CAMPOS   =>MI_CAMPOS,
                                                   UN_CONDICION=>MI_CONDICION
                                                   );    


             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;     

             IF PCK_DATOS.GL_RTA = 1 THEN
                BEGIN
                  BEGIN 
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;  

                  END;

                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                                         PCK_ERR_MSG.RAISE_WITH_MSG(
                                         UN_EXC_COD =>SQLCODE,
                                         UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_FACTNOENCONTRADO );
                    END;    

             END IF;         

         END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                           PCK_ERR_MSG.RAISE_WITH_MSG(
                           UN_EXC_COD =>SQLCODE,
                           UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_ANULARABONOSALDO );
      END; 


      END IF;
    END IF;

  ELSE
   BEGIN
    BEGIN
        MI_CAMPOS:='ANULADO = -1
                , FECHAANULADO=SYSDATE
                , ANULADOPOR='''||UN_USER||'''
                , DATE_MODIFIED = SYSDATE
                , MODIFIED_BY   = '''||UN_USER||'''';

        MI_CONDICION := 'COMPANIA='''||UN_COMPANIA||'''
                     AND DOCNUM='''||UN_DOCNUM||''' 
                     AND PRECOD='''||UN_PRECOD||''' 
                     AND PREANO='||UN_PREANO;
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_PAGOSDOBLES',
                                               UN_ACCION   =>'M',
                                               UN_CAMPOS   =>MI_CAMPOS,
                                               UN_CONDICION=>MI_CONDICION
                                               );   


    END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                       UN_EXC_COD =>SQLCODE,
                       UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_ANULARABONOSALDO );
   END;                                                
  END IF;

  BEGIN
   BEGIN 
      MI_CAMPOS:='PAGO_ANO = '||PCK_PREDIAL_COM1.FC_PAGOANO_VALIDO(UN_COMPANIA,UN_PRECOD)||'
                  , DATE_MODIFIED = SYSDATE
                  , MODIFIED_BY   = '''||UN_USER||'''';

      MI_CONDICION := 'COMPANIA='''||UN_COMPANIA||''' AND CODIGO='''||UN_PRECOD||'''';
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_USUARIOS_PREDIAL',
                                             UN_ACCION   =>'M',
                                             UN_CAMPOS   =>MI_CAMPOS, 
                                             UN_CONDICION=>MI_CONDICION
                                             ); 

   END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                     PCK_ERR_MSG.RAISE_WITH_MSG(
                     UN_EXC_COD =>SQLCODE,
                     UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_ANULARABONOSALDO );
  END; 

  BEGIN
   BEGIN

      MI_CAMPOS:='OBS_ANULACION = ''Anulación ejecutada exitosamente''
                , DATE_MODIFIED = SYSDATE
                , MODIFIED_BY   = '''||UN_USER||'''';

      MI_CONDICION := 'COMPANIA='''||UN_COMPANIA||''' 
                    AND DOCNUM='''||UN_DOCNUM||''' 
                    AND PRECOD='''||UN_PRECOD||''' 
                    AND PREANO='||UN_PREANO;
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_PAGOSDOBLES',
                                             UN_ACCION   =>'M',
                                             UN_CAMPOS   =>MI_CAMPOS, 
                                             UN_CONDICION=>MI_CONDICION
                                             ); 



    END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                           PCK_ERR_MSG.RAISE_WITH_MSG(
                           UN_EXC_COD =>SQLCODE,
                           UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_ANULARABONOSALDO );
  END;                                              


   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END ;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_ANULARABONOSALDO
  );

END PR_ANULAR_ABONO_SALDO;


--2

FUNCTION FC_PAGOANO_VALIDO
  /*
  NAME              : FC_PAGOANO_VALIDO En Access --> PagoAnoValido
  AUTHORS           : SYSMAN  SAS 
  AUTHOR MIGRACION  : NICOLÁS GÓMEZ BARBOSA
  DATE MIGRADOR     : 04/08/2016
  TIME              : 05:30 PM
  SOURCE MODULE     : PREDIAL
  DESCRIPTION       : Busca el año válido
  @NAME:  consultarVigenciaValidaParaPago
  @METHOD:  GET   
  */
  (
  -- Parametro que recibe el numero de la compania  
  UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe el numero del predio 
  UN_PREDIO                   IN PCK_SUBTIPOS.TI_CODPREDIO  

  )RETURN NUMBER
  AS
    -- Variable que almacenara el valor del ano menor
    MI_MENOR                  PCK_SUBTIPOS.TI_ANIO;
    -- Variable que almacenara el valor del indicador de pago
    MI_PAGO                   PCK_SUBTIPOS.TI_LOGICO;
    -- Variable que almacenara el valor del ano mayor
    MI_MAYOR                  PCK_SUBTIPOS.TI_ANIO;
    -- Variable que almacenara el numero de registros que retorna una consulta
    MI_I                      PCK_SUBTIPOS.TI_ENTERO_LARGO;

    MI_ERROR_FUN              PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 1;   
  BEGIN
  --BEGIN usado para el control de errores al ejecutar la funcion 
  BEGIN 
  SELECT COUNT(*) INTO MI_I
    FROM IP_FACTURADOS
   WHERE COMPANIA = UN_COMPANIA
     AND CODIGO   = UN_PREDIO
    --AND NUMERO_ORDEN = UN_NUMERO_ORDEN
    AND PAGADO IN 0
    AND NOCOBRADO IN 0;

  IF MI_I <> 0 THEN  

    SELECT MIN(PREANO) MENOR, 
           MIN(PAGADO) PAGO INTO MI_MENOR,
           MI_PAGO
      FROM IP_FACTURADOS
     WHERE COMPANIA = UN_COMPANIA
       AND CODIGO   = UN_PREDIO
      --AND NUMERO_ORDEN = UN_NUMERO_ORDEN
      AND PAGADO IN 0
      AND NOCOBRADO IN 0;

    IF MI_MENOR = EXTRACT (YEAR FROM SYSDATE) THEN
      RETURN MI_MENOR-1;
    ELSE
      IF MI_PAGO=0 THEN 
        RETURN MI_MENOR-1;
      ELSE 
        RETURN MI_MENOR;
      END IF;
    END IF;    
  ELSE 

    SELECT COUNT(*) INTO MI_I
      FROM IP_FACTURADOS
     WHERE COMPANIA = UN_COMPANIA
       AND CODIGO   = UN_PREDIO
      --AND NUMERO_ORDEN = UN_NUMERO_ORDEN
      AND PAGADO NOT IN 0;

    IF MI_I <> 0 THEN  
      SELECT MAX(PREANO) AS MAYOR INTO MI_MAYOR
        FROM IP_FACTURADOS
       WHERE COMPANIA = UN_COMPANIA
         AND CODIGO   = UN_PREDIO
         --AND NUMERO_ORDEN = UN_NUMERO_ORDEN
         AND PAGADO NOT IN 0;

      RETURN MI_MAYOR;
    ELSE
      RETURN 0;
    END IF;

  END IF;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_PAGOANOVALIDO,
                 UN_TABLAERROR =>'IP_FACTURADOS'
  );      
  END;    

END FC_PAGOANO_VALIDO;

--3. Mantenimiento última vigencia paga - predios nuevos
PROCEDURE PR_MTO_ULTIMAVIGENCIAPAGA 
  /*
  NAME              : PR_MTO_ULTIMAVIGENCIAPAGA
  AUTHORS           : SYSMAN  SAS 
  AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ
  DATE MIGRADOR     : 11/08/2016
  TIME              : 10:00 AM
  MODIFIER          : JUAN CARLOS RODRÍGUEZ AMÉZQUITA
  DATE MODIFIED     : 01/03/2017
  TIME MODIFIED     : 09:34 PM
  SOURCE MODULE     : PredialP2017.01.06VB
  DESCRIPTION       : Al correr esta rutina de mantenimiento se toma el valor de 
                      la vigencia para asignar al rango de predios indicados en el
                      formulario y cuyo valor de ultimo año de pago corresponde a 
                      un valor nulo, cero o a la vigencia actual sin registro de 
                      haber sido calculado previamente
  MODIFICATIONS     : Conversión a procedimiento, reestructuración del código y 
                      adición de manejo de errores según el estándar de desarrollo.
  PARAMETROS DE ENTRADA: 
    UN_COMPANIA:      Código de la compañía.
    UN_CODIGOINICIAL: Código de predio inicial.
    UN_CODIGOFINAL:   Código de predio final.
    UN_VIGENCIA:      Vigencia que será tomada como último año de pago.
    UN_USUARIO:       Usuario que registra el facturado y actualiza el año de pago.
    UN_FECHA:         Fecha en la que se ejecuta el mantenimiento.

  @NAME: actualizarUltimaVigenciaPaga
  @METHOD: POST                 
  */
(
    UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CODIGOINICIAL           IN PCK_SUBTIPOS.TI_CODPREDIO,
    UN_CODIGOFINAL             IN PCK_SUBTIPOS.TI_CODPREDIO,
    UN_VIGENCIA                IN PCK_SUBTIPOS.TI_ANIO,
    UN_USUARIO                 IN PCK_SUBTIPOS.TI_USUARIO

) 
  AS
  -- Cursor predios año cero, nulo o vigencia actual.
  CURSOR CURSOR_PREDIOS
  IS
    SELECT IP_USUARIOS_PREDIAL.CODIGO,
           IP_USUARIOS_PREDIAL.TRPCOD,
           IP_USUARIOS_PREDIAL.TRPRAN
      FROM IP_USUARIOS_PREDIAL
      LEFT JOIN IP_FACTURADOS 
      ON  IP_USUARIOS_PREDIAL.COMPANIA = IP_FACTURADOS.COMPANIA
      AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN = IP_FACTURADOS.NUMERO_ORDEN
      AND IP_USUARIOS_PREDIAL.CODIGO = IP_FACTURADOS.CODIGO
    WHERE IP_USUARIOS_PREDIAL.COMPANIA = UN_COMPANIA
      AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN = '001'
      AND IP_USUARIOS_PREDIAL.CODIGO BETWEEN UN_CODIGOINICIAL AND UN_CODIGOFINAL
      AND (IP_USUARIOS_PREDIAL.PAGO_ANO IN(0, EXTRACT (YEAR FROM SYSDATE))
           OR IP_USUARIOS_PREDIAL.PAGO_ANO IS NULL)
      AND IP_USUARIOS_PREDIAL.INDBORRADO IN (0)
      AND IP_USUARIOS_PREDIAL.CODIGO_NO_ACTIVO IN (0)
      AND IP_FACTURADOS.CODIGO IS NULL;
  -- Variables Tarifa
  MI_CODIGO_TARIFA         IP_TARIFAS.TRPCOD%TYPE;
  MI_RANGO_TARIFA          IP_TARIFAS.TRPRAN%TYPE;
  -- Variables ACME
  MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES               PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;
  MI_RTA_ACME              PCK_SUBTIPOS.TI_RTA_ACME;
  -- Reemplazos en manejo de errores
  MI_REEMPLAZOS            PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
 <<actualizar_datos_pago>>
  FOR RS 
  IN  CURSOR_PREDIOS
  LOOP
    -- Verifica si existe la tarifa actualmente asignada al predio para la vigencia que se va a crear.
    IF RS.TRPCOD IS NOT NULL AND RS.TRPRAN IS NOT NULL THEN
      MI_CODIGO_TARIFA := RS.TRPCOD;
      MI_RANGO_TARIFA := RS.TRPRAN;
    ELSE
      BEGIN
        BEGIN
          -- Extrae una tarifa según la vigencia
          SELECT TRPCOD, TRPRAN
          INTO MI_CODIGO_TARIFA, MI_RANGO_TARIFA
          FROM IP_TARIFAS
          WHERE COMPANIA = UN_COMPANIA
            AND TRPANO   = UN_VIGENCIA;
        EXCEPTION
          WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;        
        END;
      EXCEPTION
      WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD => SQLCODE,
              UN_ERROR_COD => PCK_ERRORES.ERR_PREDIAL_ULTVIGPAGA_TARIFA
            );
      END;
    END IF;
    --
    BEGIN
      BEGIN
        -- Crea el registro del facturado
        MI_CAMPOS := 'COMPANIA, CODIGO, NUMERO_ORDEN, PREANO, AVALUO, TRPCOD, 
          TRPRAN, PAGADO, DATE_CREATED, CREATED_BY';
        MI_VALORES := ''''||UN_COMPANIA||''','''||RS.CODIGO||''',''001'','
          ||UN_VIGENCIA||', 0,'''||MI_CODIGO_TARIFA||''','''||MI_RANGO_TARIFA
          ||''',-1,SYSDATE,'''||UN_USUARIO||'''';
        MI_RTA_ACME := PCK_DATOS.FC_ACME(
          UN_TABLA  => 'IP_FACTURADOS',
          UN_ACCION => 'I',
          UN_CAMPOS => MI_CAMPOS,
          UN_VALORES => MI_VALORES);
      EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;
    EXCEPTION
    WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          MI_REEMPLAZOS(1).CLAVE := 'PREDIO';
          MI_REEMPLAZOS(1).VALOR := RS.CODIGO;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD => SQLCODE,
            UN_ERROR_COD => PCK_ERRORES.ERR_PREDIAL_ULTVIGPAGA_FACTDO
          );
    END;
    -- Actualiza el último año de pago en Usuarios Predial con la vigencia ingresada por parametro
    BEGIN
      BEGIN
        MI_CAMPOS := 'PAGO_ANO = ' || UN_VIGENCIA || ', 
         DATE_MODIFIED = SYSDATE, 
         MODIFIED_BY = ''' || UN_USUARIO || '''' ;
        MI_CONDICION := 'CODIGO = ''' || RS.CODIGO || '''';
        MI_RTA_ACME := PCK_DATOS.FC_ACME(
          UN_TABLA  => 'IP_USUARIOS_PREDIAL',
          UN_ACCION => 'M',
          UN_CAMPOS => MI_CAMPOS,
          UN_CONDICION => MI_CONDICION);
      EXCEPTION 
        WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END; 
  EXCEPTION
    WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          MI_REEMPLAZOS(1).CLAVE := 'PREDIO';
          MI_REEMPLAZOS(1).VALOR := RS.CODIGO;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD => SQLCODE,
            UN_ERROR_COD => PCK_ERRORES.ERR_PREDIAL_ULTVIGPAGA_ANOPAGO
          );
  END;

  PCK_PREDIAL.PR_AUDITORIA( UN_COMPANIA => UN_COMPANIA,
                            UN_CODMOD => RS.CODIGO,
                            UN_OPEMOD => UN_USUARIO,
                            UN_CCOMOD => '110',
                            UN_VANMOD => '-',
                            UN_VNUMOD => '-',
                            UN_DESCRIPCION  => 'Mantenimiento de Año Pago - Predios Nuevos.',
                            UN_NUMERO_ORDEN => '001');
  END LOOP actualizar_datos_pago;



END PR_MTO_ULTIMAVIGENCIAPAGA;

--4
PROCEDURE PR_VERIFICARDISTCUOTAS
  /*
  NAME              : PR_VERIFICARDISTCUOTAS En Access --> VERIFICARDISTCUOTAS
  AUTHORS           : SYSMAN  SAS 
  AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ
  DATE MIGRADOR     : 11/08/2016
  TIME              : 04:30 PM
  SOURCE MODULE     : PREDIAL
  DESCRIPTION       : Verifica distribución de cuotas del acuerdo y de recibos de pago generados para las cuotas.
  @NAME:  distribuirCuotasAcuerdosyRecibosDePago
  @METHOD:  PUT                       
  */
  (
  -- Parametro que recibe el numero de la compania
  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe el codigo inicial del predio
  UN_CODIGOINICIAL           IN PCK_SUBTIPOS.TI_CODPREDIO,
  -- Parametro que recibe el codigo final del predio
  UN_CODIGOFINAL             IN PCK_SUBTIPOS.TI_CODPREDIO,

  UN_USUARIO                 IN PCK_SUBTIPOS.TI_USUARIO)

  AS
    -- Variable que almacenara el nombre de la tabla que se va a actualizar
    MI_TABLAUPDATE           PCK_SUBTIPOS.TI_TABLA;
    -- Variable que almacenara la cadena con los campos que se van a actualizar
    MI_CAMPOSUPDATE          PCK_SUBTIPOS.TI_CAMPOS;
    -- Variable que almacenara la cadena, con la condicion para realizar la actualizacion
    MI_CONDICIONUPDATE       PCK_SUBTIPOS.TI_CONDICION;

    MI_ERROR_FUN 	           PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 1;
  BEGIN
  --BEGIN usado para el control de errores
  BEGIN
    --' Verifica distribución de cuotas del acuerdo
    <<CUOTAACUERDO>>
    FOR RS IN
    (SELECT A.PREDIO,
            A.CODIGOACUERDO,
            F.CUOTA,
            (F.TOTAL - (F.C1+F.C2+F.C3+F.C4+F.C5+F.C6+F.C7+F.C8+F.C9+F.C10+F.C11+F.C12+F.C13+F.C14+F.C15+F.C16+F.C17+F.C18+F.C19+F.C20+F.INTERES_ACUERDO+F.INTERES_RECARGO)) AS DIFERENCIA
       FROM IP_FACTURADOSACUERDOS F,
            IP_ACUERDOS A
      WHERE F.COMPANIA = A.COMPANIA
        AND F.CODIGOACUERDO = A.CODIGOACUERDO
        AND F.COMPANIA=UN_COMPANIA
        AND F.PREDIO BETWEEN UN_CODIGOINICIAL AND UN_CODIGOFINAL
        AND ((F.C1+ F.C2 + F.C3 + F.C4 + F.C5 + F.C6 + F.C7 + F.C8 + F.C9 + F.C10 + F.C11 + F.C12 + F.C13 + F.C14 + F.C15 + F.C16 + F.C17 + F.C18 + F.C19 + F.C20 + F.INTERES_ACUERDO + F.INTERES_RECARGO) <> F.TOTAL)
        AND F.PAGADO                                                                                                                                                                                        IN (0)
        AND A.CANCELADO                                                                                                                                                                                     IN (0)
        AND A.ANULADO                                                                                                                                                                                       IN (0)
    )
    LOOP
      MI_TABLAUPDATE    := 'IP_FACTURADOSACUERDOS';
      MI_CAMPOSUPDATE   := ' INTERES_ACUERDO = INTERES_ACUERDO + '||RS.DIFERENCIA||' , MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
      MI_CONDICIONUPDATE:= ' CODIGOACUERDO = '''||RS.CODIGOACUERDO||''' AND    PREDIO = '''||RS.PREDIO||''' AND    CUOTA = '||RS.CUOTA;
      PCK_DATOS.GL_RTA  :=PCK_DATOS.FC_ACME(UN_TABLA    =>MI_TABLAUPDATE, 
                                            UN_ACCION   =>'M',
                                            UN_CAMPOS   =>MI_CAMPOSUPDATE,
                                            UN_CONDICION=>MI_CONDICIONUPDATE);
    END LOOP CUOTAACUERDO;
    --'Verifica distribución de recibos de pago generados para las cuotas
    <<RECIBOS>>
    FOR RS IN
    (SELECT DOCNUM,
            PRECOD,
            ESACUERDO,
            PREVAL,
            PREVAL-(C1+C2+C3+C4+C5+C6+C7+C8+C9+C10+C11+C12+C13+C14+C15+C16+C17+C18+C19+C20+INTERES_ACUERDO+INTERES_RECARGO) AS DIF
       FROM IP_RECIBOS_DE_PAGO
      WHERE EXTRACT(YEAR FROM PREFEC) = EXTRACT(YEAR FROM SYSDATE)
        AND ESACUERDO NOT IN (0)
        AND (PREVAL-(C1 + C2 + C3 + C4 + C5 + C6 + C7 + C8 + C9 + C10 + C11 + C12 + C13 + C14 + C15 + C16 + C17 + C18 + C19 + C20 + INTERES_ACUERDO + INTERES_RECARGO)) NOT IN (0)
        AND ANULADO IN (0)
    )
    LOOP
      MI_TABLAUPDATE    := 'IP_RECIBOS_DE_PAGO';
      MI_CAMPOSUPDATE   := ' INTERES_ACUERDO = INTERES_ACUERDO + '||RS.DIF||', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
      MI_CONDICIONUPDATE:= ' DOCNUM = '''||RS.DOCNUM||''' AND    PRECOD = '''||RS.PRECOD||'''';
      PCK_DATOS.GL_RTA  :=PCK_DATOS.FC_ACME(UN_TABLA    =>MI_TABLAUPDATE, 
                                            UN_ACCION   =>'M',
                                            UN_CAMPOS   =>MI_CAMPOSUPDATE,
                                            UN_CONDICION=>MI_CONDICIONUPDATE
                                            );
    END LOOP RECIBOS;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
           RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
             END ;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_VERIFDISCUOTAS
  );    

  END PR_VERIFICARDISTCUOTAS;

--5
PROCEDURE PR_ACTUALIZARACUERDOS
  /*
  NAME              : PR_ACTUALIZARACUERDOS En Access --> ACTUALIZARACUERDOS
  AUTHORS           : SYSMAN  SAS 
  AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ
  DATE MIGRADOR     : 11/08/2016
  TIME              : 04:40 PM
  SOURCE MODULE     : PREDIAL
  DESCRIPTION       : Verifica distribución de cuotas del acuerdo y de recibos de pago generados para las cuotas.
  @NAME:  actualizarAcuerdos
  @METHOD:  PUT
  */
  (
  -- Parametro que recibe el numero de la compania
  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe el valor del codigo inicial del predio
  UN_CODIGOINICIAL           IN PCK_SUBTIPOS.TI_CODPREDIO,
  -- Parametro que recibe el valor del codigo final del predio
  UN_CODIGOFINAL             IN PCK_SUBTIPOS.TI_CODPREDIO,

  UN_USUARIO                 IN PCK_SUBTIPOS.TI_USUARIO)    
  AS
    MI_CUOTASPORPAGAR        PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_CUOTASCANCELADAS      PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_VIGENCIA              PCK_SUBTIPOS.TI_ANIO;
    MI_TABLAUPDATE           PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOSUPDATE          PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICIONUPDATE       PCK_SUBTIPOS.TI_CONDICION;
    MI_MERGEUSING            PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE           PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE           PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_ERROR_FUN             PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 1;
  BEGIN
    -- BEGIN usado para control de errores en el merge
    BEGIN
    MI_MERGEUSING := ' SELECT IP_FACTURADOSACUERDOS.COMPANIA, '||CHR(10)||CHR(13)|| 
                             'IP_FACTURADOSACUERDOS.CODIGOACUERDO,   '||CHR(10)||CHR(13)|| 
                             'IP_FACTURADOSACUERDOS.CUOTA, '||CHR(10)||CHR(13)|| 
                             'IP_FACTURADOSACUERDOS.PREDIO, '||CHR(10)||CHR(13)|| 
                             'IP_FACTURADOSACUERDOS.NUMERO_ORDEN, '||CHR(10)||CHR(13)|| 
                             'IP_RECIBOS_DE_PAGO.DOCNUM, '||CHR(10)||CHR(13)|| 
                             'IP_RECIBOS_DE_PAGO.PAG_BANPAG, '||CHR(10)||CHR(13)|| 
                             ''''||UN_USUARIO||''' MODIFIED_BY, '||CHR(10)||CHR(13)|| 
                             'SYSDATE DATE_MODIFIED, '||CHR(10)||CHR(13)|| 
                             'IP_RECIBOS_DE_PAGO.PREFECPAG '||CHR(10)||CHR(13)|| 
                             'FROM IP_RECIBOS_DE_PAGO '||CHR(10)||CHR(13)|| 
                             'INNER JOIN IP_FACTURADOSACUERDOS '||CHR(10)||CHR(13)|| 
                             'ON IP_RECIBOS_DE_PAGO.COMPANIA        = IP_FACTURADOSACUERDOS.COMPANIA '||CHR(10)||CHR(13)|| 
                             'AND IP_RECIBOS_DE_PAGO.PRECOD         = IP_FACTURADOSACUERDOS.PREDIO '||CHR(10)||CHR(13)|| 
                             'AND IP_RECIBOS_DE_PAGO.NCUOTA_ACUERDO = IP_FACTURADOSACUERDOS.CUOTA '||CHR(10)||CHR(13)|| 
                             'AND IP_RECIBOS_DE_PAGO.ACUERDO        = IP_FACTURADOSACUERDOS.CODIGOACUERDO '||CHR(10)||CHR(13)||
                             'AND IP_RECIBOS_DE_PAGO.NUMERO_ORDEN   = IP_FACTURADOSACUERDOS.NUMERO_ORDEN '||CHR(10)||CHR(13)|| 
                             'WHERE IP_RECIBOS_DE_PAGO.COMPANIA     = '''||UN_COMPANIA||''' '||CHR(10)||CHR(13)|| 
                             'AND IP_FACTURADOSACUERDOS.PAGADO     IN (0) '||CHR(10)||CHR(13)|| 
                             'AND IP_RECIBOS_DE_PAGO.PAGO NOT      IN (0) '||CHR(10)||CHR(13)|| 
                             'AND IP_RECIBOS_DE_PAGO.ESACUERDO NOT IN (0) ';
    MI_MERGEENLACE     := 'TABLA.COMPANIA = VISTA.COMPANIA   '||CHR(10)||CHR(13)||
                          'AND TABLA.CODIGOACUERDO =VISTA.CODIGOACUERDO   '||CHR(10)||CHR(13)||  
                          'AND TABLA.CUOTA=VISTA.CUOTA   '||CHR(10)||CHR(13)||
                          'AND TABLA.PREDIO=VISTA.PREDIO   '||CHR(10)||CHR(13)||
                          'AND TABLA.NUMERO_ORDEN=VISTA.NUMERO_ORDEN';
    MI_MERGEEXISTE     := 'UPDATE SET PAGADO  = -1, '||CHR(10)||CHR(13)||
                          'DOCNUM             = VISTA.DOCNUM, '||CHR(10)||CHR(13)||
                          'PAG_BAN            = VISTA.PAG_BANPAG, '||CHR(10)||CHR(13)||
                          'FECHAPAGO          = VISTA.PREFECPAG,'||CHR(10)||CHR(13)||
                          'MODIFIED_BY        = VISTA.MODIFIED_BY,'||CHR(10)||CHR(13)||
                          'DATE_MODIFIED      = VISTA.DATE_MODIFIED';
    PCK_DATOS.GL_RTA   := PCK_DATOS.FC_ACME(UN_TABLA      =>'IP_FACTURADOSACUERDOS',
                                            UN_ACCION     =>'MM',
                                            UN_MERGEUSING =>MI_MERGEUSING,
                                            UN_MERGEENLACE=>MI_MERGEENLACE,
                                            UN_MERGEEXISTE=>MI_MERGEEXISTE
                                            );

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
           RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
             END ;

    -- BEGIN usado para control de errores al realizar actualizaciones
    BEGIN
    MI_TABLAUPDATE     := 'IP_FACTURADOS';
    MI_CAMPOSUPDATE    := ' INDPAGO_ACPAG = 0 , MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
    MI_CONDICIONUPDATE := ' INDPAGO_ACPAG NOT IN  (0) AND PAGADO IN (0)  AND   CODIGO BETWEEN '''|| UN_CODIGOINICIAL ||''' AND '''||UN_CODIGOFINAL||'''';
    PCK_DATOS.GL_RTA   :=PCK_DATOS.FC_ACME(UN_TABLA    =>MI_TABLAUPDATE, 
                                           UN_ACCION   =>'M',
                                           UN_CAMPOS   =>MI_CAMPOSUPDATE,
                                           UN_CONDICION=>MI_CONDICIONUPDATE
                                           );

    <<ACTUALIZAACUERDO>>
    FOR RS IN
   (SELECT CODIGOACUERDO,
           PREDIO,
           NUMERO_ORDEN,
           CANCELADO,
           ANULADO,
           PREANOI,
           PREANO
      FROM IP_ACUERDOS
     WHERE PREANOI IS NOT NULL
       AND PREANO    IS NOT NULL
       AND PREDIO BETWEEN UN_CODIGOINICIAL AND UN_CODIGOFINAL
   )
   LOOP
     SELECT COUNT (PAGADO) AS PORPAGAR
            INTO MI_CUOTASPORPAGAR
       FROM IP_FACTURADOSACUERDOS
      WHERE CODIGOACUERDO = RS.CODIGOACUERDO
        AND PREDIO          = RS.PREDIO
        AND TOTAL NOT      IN (0);

     SELECT COUNT (PAGADO) CUOTASCANCELADAS
            INTO MI_CUOTASCANCELADAS
       FROM IP_FACTURADOSACUERDOS
      WHERE CODIGOACUERDO  = RS.CODIGOACUERDO
        AND PREDIO           = RS.PREDIO
        AND PAGADO NOT      IN (0);

     IF MI_CUOTASPORPAGAR =MI_CUOTASCANCELADAS THEN
        MI_TABLAUPDATE    := 'IP_ACUERDOS';
        MI_CAMPOSUPDATE   := ' CANCELADO = -1 , MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
        MI_CONDICIONUPDATE:= ' CODIGOACUERDO = '''||RS.CODIGOACUERDO ||''' AND PREDIO = '''||RS.PREDIO||'''';
        PCK_DATOS.GL_RTA  :=PCK_DATOS.FC_ACME(UN_TABLA    =>MI_TABLAUPDATE, 
                                              UN_ACCION   =>'M',
                                              UN_CAMPOS   =>MI_CAMPOSUPDATE,
                                              UN_CONDICION=>MI_CONDICIONUPDATE
                                              );
        MI_TABLAUPDATE    := 'IP_FACTURADOS';
        MI_CAMPOSUPDATE   := ' INDPAGO_ACPAG = -1, PAGADO = -1 , MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
        MI_CONDICIONUPDATE:= ' CODIGO = '''|| RS.PREDIO ||''' AND PREANO BETWEEN '''||RS.PREANOI||''' AND '''||RS.PREANO||'''';
        PCK_DATOS.GL_RTA  :=PCK_DATOS.FC_ACME(UN_TABLA    =>MI_TABLAUPDATE,
                                              UN_ACCION   =>'M',
                                              UN_CAMPOS   =>MI_CAMPOSUPDATE,
                                              UN_CONDICION=>MI_CONDICIONUPDATE);
        MI_TABLAUPDATE    := 'IP_USUARIOS_PREDIAL';
        MI_CAMPOSUPDATE   := ' PAGO_ACUERDO = 0 , MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
        MI_CONDICIONUPDATE:= ' CODIGO = '''|| RS.PREDIO ||'''';
        PCK_DATOS.GL_RTA  :=PCK_DATOS.FC_ACME(UN_TABLA    =>MI_TABLAUPDATE,
                                              UN_ACCION   =>'M',
                                              UN_CAMPOS   =>MI_CAMPOSUPDATE,
                                              UN_CONDICION=>MI_CONDICIONUPDATE
                                              );
     ELSE
       IF RS.ANULADO NOT IN(0) THEN
         MI_TABLAUPDATE    := 'IP_USUARIOS_PREDIAL';
         MI_CAMPOSUPDATE   := ' PAGO_ACUERDO = 0 , MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
         MI_CONDICIONUPDATE:= ' CODIGO = '''||RS.PREDIO||'''';
         PCK_DATOS.GL_RTA  :=PCK_DATOS.FC_ACME(UN_TABLA    =>MI_TABLAUPDATE,
                                               UN_ACCION   =>'M',
                                               UN_CAMPOS   =>MI_CAMPOSUPDATE,
                                               UN_CONDICION=>MI_CONDICIONUPDATE
                                               );
       ELSE
         MI_TABLAUPDATE    := 'IP_ACUERDOS';
         MI_CAMPOSUPDATE   := ' CANCELADO = 0 , MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
         MI_CONDICIONUPDATE:= ' CODIGOACUERDO = '''||RS.CODIGOACUERDO ||''' AND PREDIO = '''||RS.PREDIO||'''';
         PCK_DATOS.GL_RTA  :=PCK_DATOS.FC_ACME(UN_TABLA    =>MI_TABLAUPDATE, 
                                               UN_ACCION   =>'M',
                                               UN_CAMPOS   =>MI_CAMPOSUPDATE,
                                               UN_CONDICION=>MI_CONDICIONUPDATE
                                               );
         MI_TABLAUPDATE    := 'IP_FACTURADOS';
         MI_CAMPOSUPDATE   := ' INDPAGO_ACPAG = -1, PAGADO = 0 , MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
         MI_CONDICIONUPDATE:= ' CODIGO = '''|| RS.PREDIO ||''' AND PREANO BETWEEN '''||RS.PREANOI||''' AND '''||RS.PREANO||'''';
         PCK_DATOS.GL_RTA  :=PCK_DATOS.FC_ACME(UN_TABLA    =>MI_TABLAUPDATE, 
                                               UN_ACCION   =>'M',
                                               UN_CAMPOS   =>MI_CAMPOSUPDATE,
                                               UN_CONDICION=>MI_CONDICIONUPDATE
                                               );
         MI_TABLAUPDATE    := 'IP_USUARIOS_PREDIAL';
         MI_CAMPOSUPDATE   := ' PAGO_ACUERDO = -1 , MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
         MI_CONDICIONUPDATE:= ' CODIGO = '''|| RS.PREDIO ||'''';
         PCK_DATOS.GL_RTA  :=PCK_DATOS.FC_ACME(UN_TABLA    =>MI_TABLAUPDATE,
                                               UN_ACCION   =>'M',
                                               UN_CAMPOS   =>MI_CAMPOSUPDATE,
                                               UN_CONDICION=>MI_CONDICIONUPDATE
                                               );
       END IF;
     END IF;
     IF RS.ANULADO IN (0) THEN
       MI_VIGENCIA       := PCK_PREDIAL_COM1.FC_PAGOANO_VALIDO(UN_COMPANIA,RS.PREDIO);
       MI_TABLAUPDATE    := 'IP_USUARIOS_PREDIAL';
       MI_CAMPOSUPDATE   := ' PAGO_ANO = '||MI_VIGENCIA ||', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE';
       MI_CONDICIONUPDATE:= ' CODIGO = '''|| RS.PREDIO ||'''';
       PCK_DATOS.GL_RTA  :=PCK_DATOS.FC_ACME(UN_TABLA    =>MI_TABLAUPDATE,
                                             UN_ACCION   =>'M',
                                             UN_CAMPOS   =>MI_CAMPOSUPDATE,
                                             UN_CONDICION=>MI_CONDICIONUPDATE
                                             );
     END IF;
   END LOOP ACTUALIZAACUERDO;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
           RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
             END ;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_ACTUALIZAACUERDOS
  );

END PR_ACTUALIZARACUERDOS;

--6

FUNCTION FC_MANTENIMIENTOACUERDOS
  /*
  NAME              : FC_MANTENIMIENTOACUERDOS En Access --> ACTUALIZARACUERDOS
  AUTHORS           : SYSMAN  SAS 
  AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ
  DATE MIGRADOR     : 11/08/2016
  TIME              : 05:20 PM
  SOURCE MODULE     : PREDIAL
  DESCRIPTION       : Verifica el número de cuotas canceladas contra las cuotas del acuerdo 
                      y a partir de los resultados actualizar la información necesaria en la base  de datos.
  @NAME:  verificarCuotasCanceladas
  @METHOD:  GET                    
  */
  (
  -- Parametro que recibe el numero de la compania con la que se esta trabajando
  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe el valor del indicador de la fecha de pago.
  UN_INDFECHAPAGO            IN PCK_SUBTIPOS.TI_LOGICO,
  -- Parametro que recibe el valor del codigo inicial del predio
  UN_CODIGOINICIAL           IN PCK_SUBTIPOS.TI_CODPREDIO,
  -- Parametro que recibe el valor del codigo final del predio
  UN_CODIGOFINAL             IN PCK_SUBTIPOS.TI_CODPREDIO,

  UN_USUARIO                 IN PCK_SUBTIPOS.TI_USUARIO)

  RETURN VARCHAR2
  AS
    -- Variable que almacenara la cadena con la consulta a usar para la actualizacion
    MI_MERGEUSING            PCK_SUBTIPOS.TI_MERGEUSING;
    -- Variable que almacenra la cadena con los campos que relacionan  la tabla y la vista
    -- al momento de realizar la actualizacion
    MI_MERGEENLACE           PCK_SUBTIPOS.TI_MERGEENLACE;
    -- Variable que almacenara la cadena con los campos y el valor al cual se van a actualizar
    MI_MERGEEXISTE           PCK_SUBTIPOS.TI_MERGEEXISTE;

    MI_ERROR_FUN             PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 1;
  BEGIN
   IF UN_INDFECHAPAGO NOT IN (0) THEN 
   -- BEGIN usado para el control de errores al ejecutar el ACME
   BEGIN
    MI_MERGEUSING:='SELECT IP_FACTURADOSACUERDOS.COMPANIA, '||CHR(10)||CHR(13)||
                          'IP_FACTURADOSACUERDOS.CODIGOACUERDO, '||CHR(10)||CHR(13)||
                          'IP_FACTURADOSACUERDOS.CUOTA, '||CHR(10)||CHR(13)||
                          'IP_FACTURADOSACUERDOS.PREDIO, '||CHR(10)||CHR(13)||
                          'IP_FACTURADOSACUERDOS.NUMERO_ORDEN, '||CHR(10)||CHR(13)||
                          'IP_RECIBOS_DE_PAGO.PREFECPAG, '||CHR(10)||CHR(13)||
                          ''''||UN_USUARIO||''' MODIFIED_BY,'||CHR(10)||CHR(13)||
                          ' SYSDATE DATE_MODIFIED '||CHR(10)||CHR(13)||
                          'FROM   IP_RECIBOS_DE_PAGO '||CHR(10)||CHR(13)||
                          'INNER JOIN IP_FACTURADOSACUERDOS '||CHR(10)||CHR(13)||
                          'ON  IP_RECIBOS_DE_PAGO.COMPANIA = IP_FACTURADOSACUERDOS.COMPANIA '||CHR(10)||CHR(13)||
                          'AND IP_RECIBOS_DE_PAGO.PRECOD = IP_FACTURADOSACUERDOS.PREDIO '||CHR(10)||CHR(13)||
                          'AND IP_RECIBOS_DE_PAGO.DOCNUM = IP_FACTURADOSACUERDOS.DOCNUM '||CHR(10)||CHR(13)||
                          'WHERE IP_RECIBOS_DE_PAGO.COMPANIA = '||UN_COMPANIA||' '||CHR(10)||CHR(13)||
                          '  AND CASE WHEN IP_RECIBOS_DE_PAGO.PREFECPAG<>IP_FACTURADOSACUERDOS.FECHAPAGO '||CHR(10)||CHR(13)||
                          '           THEN -1 '||CHR(10)||CHR(13)||
                          '           ELSE 0 '||CHR(10)||CHR(13)||
                          '           END  NOT IN (0) '||CHR(10)||CHR(13)||
                          '  AND IP_FACTURADOSACUERDOS.PAGADO NOT IN (0) '||CHR(10)||CHR(13)||
                          '  AND IP_RECIBOS_DE_PAGO.ANULADO IN (0) ';
    MI_MERGEENLACE:='TABLA.COMPANIA =VISTA.COMPANIA '||CHR(10)||CHR(13)||
                    'AND TABLA.CODIGOACUERDO=VISTA.CODIGOACUERDO '||CHR(10)||CHR(13)||
                    'AND TABLA.CUOTA=VISTA.CUOTA '||CHR(10)||CHR(13)||
                    'AND TABLA.PREDIO=VISTA.PREDIO '||CHR(10)||CHR(13)||
                    'AND TABLA.NUMERO_ORDEN=VISTA.NUMERO_ORDEN';
    MI_MERGEEXISTE:=' UPDATE SET FECHAPAGO    =VISTA.PREFECPAG,
                                  MODIFIED_BY   = VISTA.MODIFIED_BY,
                                  DATE_MODIFIED = VISTA.DATE_MODIFIED';
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      =>'IP_FACTURADOSACUERDOS',
                                          UN_ACCION     =>'MM',
                                          UN_MERGEUSING =>MI_MERGEUSING,
                                          UN_MERGEENLACE=>MI_MERGEENLACE,
                                          UN_MERGEEXISTE=>MI_MERGEEXISTE
                                          );

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
             RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
               END ;

    END IF;
    PR_VERIFICARDISTCUOTAS(UN_COMPANIA     =>UN_COMPANIA,
                           UN_CODIGOINICIAL=>UN_CODIGOINICIAL,
                           UN_CODIGOFINAL  =>UN_CODIGOFINAL,
                           UN_USUARIO      => UN_USUARIO);

    PR_ACTUALIZARACUERDOS(UN_COMPANIA      =>UN_COMPANIA,
                          UN_CODIGOINICIAL =>UN_CODIGOINICIAL,
                          UN_CODIGOFINAL   =>UN_CODIGOFINAL,
                          UN_USUARIO       => UN_USUARIO);

    RETURN 'Proceso finalizado';

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                   PCK_ERR_MSG.RAISE_WITH_MSG(
                   UN_EXC_COD =>SQLCODE,
                   UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_MNTOACUERDOS
  );

END FC_MANTENIMIENTOACUERDOS;

--7

PROCEDURE PR_REGISTRAR_RECAUDO_ABONO
  /*
  NAME              : PR_REGISTRAR_RECAUDO_ABONO En Access --> RegistrarRecaudoAbono
  AUTHORS           : SYSMAN  SAS 
  AUTHOR MIGRACION  : NICOLÁS GÓMEZ BARBOSA
  DATE MIGRADOR     : 11/08/2016
  TIME              : 08:30 AM
  SOURCE MODULE     : PREDIAL
  DESCRIPTION       : Registrar recaudo de abono
  MODIFIER          : Leydi Milena Cortés Forero
  DATE MODIFIED     : 22/08/2017
  TIME              : 12:30 PM
  MODIFICATIONS     : Se incluyen los campos de auditoría para las actualizaciones de tablas.
  @NAME:  registrarRecaudoAbonos
  @METHOD:  PUT                      
  */
  (
  -- Parametro que recibe el numero de la compania, con la que se esta trabajando
  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe el numero del recibo 
  UN_RECIBO                  IN PCK_SUBTIPOS.TI_DOCNUM,
  -- Parametro que recibe el numero del predio
  UN_PREDIO                  IN PCK_SUBTIPOS.TI_CODPREDIO,
  -- Parametro que recibe el numero del banco
  UN_BANCO                   IN IP_BANCOS.CODIGOBANCO%TYPE,
  -- Parametro que recibe el valor de la fecha de recaudo
  UN_FECHA_RECAUDO           IN VARCHAR2,
  -- Parametro que recibe el numero de orden 
  UN_NUMERO_ORDEN            IN PCK_SUBTIPOS.TI_NUMORDEN,
  -- Parametro que recibe el numero del modulo
  UN_MODULO                  IN PCK_SUBTIPOS.TI_MODULO,
  -- Parametro que recibe el usuario
  UN_USER                    IN PCK_SUBTIPOS.TI_USUARIO,
  -- Parametro que recibe el valor del recibo
  UN_VALOR_RECIBO            IN PCK_SUBTIPOS.TI_DOBLE  
  )
  AS
    -- Variable que almacenara la cadena con los campos a modificar en la funcion
    MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
    -- Variable que almacenara la cadena con la condicion a usar al realizar modificaciones
    MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;
    -- Variable que almacenara el numero de registros que retorna una consulta
    MI_I                     PCK_SUBTIPOS.TI_ENTERO_LARGO;
    -- Variable que almacenara el valor que retorna el parametro MANEJA EXCEDENTES DE VIG. ANTERIORES
    MI_MANEXCVIGANT          PCK_SUBTIPOS.TI_PARAMETRO; 
    -- Variable que almacenara el ano de pago final 
    MI_PAGO_ANOFIN           PCK_SUBTIPOS.TI_ANIO:=0;
    -- Variable que almacenara el valor de la tabla en la que se realizan las actualizaciones
    MI_TABLA                 PCK_SUBTIPOS.TI_TABLA;
    -- Variable que almacenara el numero de registros que se modifican, insertan o eliminan
    MI_RTA                   PCK_SUBTIPOS.TI_ENTERO_LARGO;
    -- Variable que almacenara los reemplazos que se mostraran en el mensaje 
    MI_REEMPLAZOS            PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN

  MI_MANEXCVIGANT:=NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA=>UN_COMPANIA,
                                             UN_NOMBRE=>'MANEJA EXCEDENTES DE VIG. ANTERIORES',
                                             UN_MODULO=>UN_MODULO,
                                             UN_FECHA_PAR=>SYSDATE
                                             ),'NO');

  -- BEGIN usado para el control de errores al realizar las actualizaciones
  BEGIN
  <<REVISAFACTURADOS>>
  FOR MI_RSFAC IN ( SELECT PREANO,
                           TOTALABONADO,
                           C1,
                           C2,
                           C3,
                           C4,
                           ALDIAINTERES,
                           ALDIAINTERESCAR
                      FROM IP_FACTURADOS 
                     WHERE COMPANIA     = UN_COMPANIA
                       AND CODIGO       = UN_PREDIO
                       AND NUMERO_ORDEN = UN_NUMERO_ORDEN
                       AND PAGADO IN (0)
                     ORDER BY PREANO)

  LOOP

	<<REALIZAABONOOPAGO>>
    FOR MI_RSFACAB IN ( SELECT PREANO,
                               TOTAL,
                               C2,
                               C4,
                               NVL(FECHAFACTURADO,'') FECHAFACTURADO
                          FROM IP_FACTURADOSABONOS 
                         WHERE COMPANIA = UN_COMPANIA
                           AND CODIGO   = UN_PREDIO
                           AND NUMERO_ORDEN = UN_NUMERO_ORDEN
                           AND DOCNUM   = UN_RECIBO
                           AND PREANO   = MI_RSFAC.PREANO
                           AND PAGADO IN (0) )
    LOOP
      SELECT COUNT(*) INTO MI_I
        FROM IP_FACTURADOS
       INNER JOIN IP_FACTURADOSABONOS
               ON IP_FACTURADOS.COMPANIA = IP_FACTURADOSABONOS.COMPANIA
              AND IP_FACTURADOS.CODIGO = IP_FACTURADOSABONOS.CODIGO
              AND IP_FACTURADOS.NUMERO_ORDEN = IP_FACTURADOSABONOS.NUMERO_ORDEN
              AND IP_FACTURADOS.PREANO = IP_FACTURADOSABONOS.PREANO
       WHERE IP_FACTURADOSABONOS.COMPANIA = UN_COMPANIA
         AND IP_FACTURADOSABONOS.CODIGO = UN_PREDIO
         AND IP_FACTURADOSABONOS.NUMERO_ORDEN = UN_NUMERO_ORDEN
         AND IP_FACTURADOSABONOS.DOCNUM = UN_RECIBO
         AND IP_FACTURADOSABONOS.PREANO = MI_RSFAC.PREANO
         AND IP_FACTURADOS.C1 = IP_FACTURADOSABONOS.C1 
         AND IP_FACTURADOS.C2 = IP_FACTURADOSABONOS.C2
         AND IP_FACTURADOS.C3 = IP_FACTURADOSABONOS.C3 
         AND IP_FACTURADOS.C4 = IP_FACTURADOSABONOS.C4 
         AND IP_FACTURADOS.C5 = IP_FACTURADOSABONOS.C5 
         AND IP_FACTURADOS.C6 = IP_FACTURADOSABONOS.C6 
         AND IP_FACTURADOS.C7 = IP_FACTURADOSABONOS.C7 
         AND IP_FACTURADOS.C8 = IP_FACTURADOSABONOS.C8 
         AND IP_FACTURADOS.C9 = IP_FACTURADOSABONOS.C9 
         AND IP_FACTURADOS.C10 = IP_FACTURADOSABONOS.C10 
         AND IP_FACTURADOS.C11 = IP_FACTURADOSABONOS.C11 
         AND IP_FACTURADOS.C12 = IP_FACTURADOSABONOS.C12
         AND IP_FACTURADOS.C13 = IP_FACTURADOSABONOS.C13 
         AND IP_FACTURADOS.C14 = IP_FACTURADOSABONOS.C14 
         AND IP_FACTURADOS.C15 = IP_FACTURADOSABONOS.C15 
         AND IP_FACTURADOS.C16 = IP_FACTURADOSABONOS.C16
         AND IP_FACTURADOS.C17 = IP_FACTURADOSABONOS.C17 
         AND IP_FACTURADOS.C18 = IP_FACTURADOSABONOS.C18 
         AND IP_FACTURADOS.C19 = IP_FACTURADOSABONOS.C19 
         AND IP_FACTURADOS.C20 = IP_FACTURADOSABONOS.C20;

        IF MI_I = 1 THEN
            BEGIN
                BEGIN 
                    MI_TABLA := 'IP_FACTURADOS';
                    MI_CAMPOS:='PAGADO = -1, PAG_BAN='''||UN_BANCO||
                               ''', DOCNUM='''|| UN_RECIBO        ||
                               ''', PREVAL='  || MI_RSFACAB.TOTAL ||
                               ',  FECHAPAGO=TO_DATE('''  || UN_FECHA_RECAUDO||''',''DD/MM/YYYY''), ' || 
                               '   MODIFIED_BY   = '''    || UN_USER         || 
                               ''',DATE_MODIFIED = SYSDATE';
                    MI_CONDICION := '       COMPANIA='''    ||  UN_COMPANIA     || 
                                    ''' AND CODIGO  ='''    ||  UN_PREDIO       ||
                                    ''' AND NUMERO_ORDEN='''||  UN_NUMERO_ORDEN ||
                                    ''' AND PREANO='        ||  MI_RSFAC.PREANO;
                    MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA,
                                                 UN_ACCION   => 'M',
                                                 UN_CAMPOS   => MI_CAMPOS,
                                                 UN_CONDICION=> MI_CONDICION);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                          ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INDICFACTURADOS
                                          ,UN_TABLAERROR => MI_TABLA);     
            END;
            MI_TABLA := 'IP_PAGOSDOBLES';
            IF MI_MANEXCVIGANT='SI' THEN
                BEGIN
                    BEGIN
                        MI_CAMPOS := 'PAGO = -1, 
                                         MODIFIED_BY   = ''' || UN_USER || 
                                     ''',DATE_MODIFIED = SYSDATE';
                        MI_CONDICION := '       COMPANIA =    ''' ||  UN_COMPANIA    || 
                                        ''' AND PRECOD  =     ''' ||  UN_PREDIO      ||
                                        ''' AND NUMERO_ORDEN =''' ||  UN_NUMERO_ORDEN||
                                        ''' AND PREANO      <='   ||  MI_RSFAC.PREANO||
                                        ' AND PAGO IN (0)';
                        MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA,
                                                     UN_ACCION   => 'M',
                                                     UN_CAMPOS   => MI_CAMPOS,
                                                     UN_CONDICION=> MI_CONDICION);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTUPAGOSDOBLES
                                           ,UN_TABLAERROR => MI_TABLA); 
                END;
           ELSE
               BEGIN
                   BEGIN
                       MI_CAMPOS := '    PAGO = -1, MODIFIED_BY   = ''' || UN_USER || 
                                  ''', DATE_MODIFIED = SYSDATE';
                       MI_CONDICION :=  '     COMPANIA  =     '''    || UN_COMPANIA || 
                                        ''' AND PRECOD  =     '''    || UN_PREDIO   ||
                                        ''' AND NUMERO_ORDEN= '''    || UN_NUMERO_ORDEN||
                                        ''' AND PREANO=       '      || MI_RSFAC.PREANO||
                                        '   AND PAGO IN (0)';
                       PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA, 
                                                              UN_ACCION   => 'M', 
                                                              UN_CAMPOS   => MI_CAMPOS, 
                                                              UN_CONDICION=> MI_CONDICION ); 
                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                   END;
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                   PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                             ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTUPAGOSDOBLES
                                             ,UN_TABLAERROR => MI_TABLA);
               END;
           END IF;
           BEGIN
               BEGIN 
                   MI_TABLA := 'IP_FACTURADOSABONOS';
                   MI_CAMPOS:='PAGADO = -1,  PAG_BAN='''|| UN_BANCO ||
                              ''', FECHAPAGO=TO_DATE('''||UN_FECHA_RECAUDO||''',''DD/MM/YYYY''), ' ||
                              '    MODIFIED_BY   = ''' || UN_USER || 
                              ''', DATE_MODIFIED = SYSDATE';
                   MI_CONDICION :='       COMPANIA     ='''||  UN_COMPANIA    ||''' AND CODIGO='''||UN_PREDIO||
                                  ''' AND NUMERO_ORDEN ='''||  UN_NUMERO_ORDEN||''' AND DOCNUM='''||UN_RECIBO||
                                  ''' AND PREANO       ='  ||  MI_RSFACAB.PREANO;
                   MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOSABONOS', 
                                                      UN_ACCION   =>'M',
                                                      UN_CAMPOS   =>MI_CAMPOS,
                                                      UN_CONDICION=>MI_CONDICION);   
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
               END;
           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
               PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                         ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INDICFACTURADOS
                                         ,UN_TABLAERROR => MI_TABLA);
           END; 
           BEGIN
               BEGIN
                     MI_TABLA := 'IP_RECIBOS_DE_PAGO';
                     MI_CAMPOS:='PAGO = -1, PAG_BANPAG=''' || UN_BANCO        || 
                                ''', PREFECPAG=TO_DATE(''' || UN_FECHA_RECAUDO||''',''DD/MM/YYYY''), 
                                PAQUETEPAG=''01'', FECHA_REGRECAUDO=SYSDATE, USUARIO_REGRECAUDO='''||UN_USER||''', 
                                ' ||' MODIFIED_BY   = ''' || UN_USER || ''', DATE_MODIFIED = SYSDATE';
                     MI_CONDICION := '     COMPANIA='''||UN_COMPANIA||
                                     ''' AND DOCNUM='''||UN_RECIBO||'''';
                     MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA,
                                                  UN_ACCION   => 'M', 
                                                  UN_CAMPOS   => MI_CAMPOS, 
                                                  UN_CONDICION=> MI_CONDICION);   

                     MI_PAGO_ANOFIN:=MI_RSFACAB.PREANO;
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
               END;
           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
               PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                          UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTURECIBOSPAGO,
                                          UN_TABLAERROR => MI_TABLA);
           END;
        ELSE
           BEGIN
               BEGIN
                   MI_TABLA := 'IP_FACTURADOS';
                   MI_CAMPOS := 'TOTALABONADO2 = TOTALABONADO1, 
                                 VALORULTIMOABONO2 = VALORULTIMOABONO1, 
                                 FECHAULTIMOABONO2 = FECHAULTIMOABONO1, 
                                 DOCNUMABONO2 = DOCNUMABONO1, 
                                 MODIFIED_BY  = ''' || UN_USER || ''', 
                                 DATE_MODIFIED = SYSDATE';
                   MI_CONDICION := ' COMPANIA = '''    || UN_COMPANIA    || ''' 
                                   AND CODIGO = '''    || UN_PREDIO      || ''' 
                              AND NUMERO_ORDEN= '''    || UN_NUMERO_ORDEN||''' 
                              AND PAGADO IN (0)';
                   MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA,
                                                UN_ACCION   => 'M',
                                                UN_CAMPOS   => MI_CAMPOS, 
                                                UN_CONDICION=> MI_CONDICION);   
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
               END;
           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
               PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                          UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INDICFACTURADOS,
                                          UN_TABLAERROR => MI_TABLA);
           END;
           BEGIN
               BEGIN
                   MI_TABLA := 'IP_FACTURADOS';
                   MI_CAMPOS:='TOTALABONADO1 = TOTALABONADO, 
                               VALORULTIMOABONO1 = VALORULTIMOABONO, 
                               FECHAULTIMOABONO1 = FECHAULTIMOABONO, 
                               DOCNUMABONO1 = DOCNUMABONO, 
                               MODIFIED_BY   = ''' || UN_USER || ''', 
                               DATE_MODIFIED = SYSDATE';
                   MI_CONDICION := '   COMPANIA = ''' || UN_COMPANIA || ''' 
                                AND CODIGO      = ''' ||UN_PREDIO    || ''' 
                                AND NUMERO_ORDEN= '''||UN_NUMERO_ORDEN||''' 
                                AND PAGADO IN (0)';
                   MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA, 
                                                  UN_ACCION   => 'M', 
                                                  UN_CAMPOS   => MI_CAMPOS, 
                                                  UN_CONDICION=> MI_CONDICION);   
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
               END;
           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
               PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                          UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INDICFACTURADOS,
                                          UN_TABLAERROR => MI_TABLA);
           END;
           BEGIN
               BEGIN
                   MI_TABLA := 'IP_FACTURADOS';
                   MI_CAMPOS:=' TOTALABONADO      = '||(NVL(MI_RSFAC.TOTALABONADO,0)+NVL(MI_RSFACAB.TOTAL,0))||
                              ', VALORULTIMOABONO = '||NVL(MI_RSFACAB.TOTAL,0)||
                              ', FECHAULTIMOABONO = TO_DATE('''||UN_FECHA_RECAUDO||''',''DD/MM/YYYY''), 
                                DOCNUMABONO1      = DOCNUMABONO, ' || 
                              ' MODIFIED_BY       = ''' || UN_USER        || ''', 
                                DATE_MODIFIED = SYSDATE';
                   MI_CONDICION := 'COMPANIA        = '''||UN_COMPANIA    ||''' 
                                    AND CODIGO      = '''||UN_PREDIO      ||''' 
                                    AND NUMERO_ORDEN= '''||UN_NUMERO_ORDEN||''' 
                                    AND PAGADO IN (0)';
                   PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA, 
                                                          UN_ACCION   => 'M',
                                                          UN_CAMPOS   => MI_CAMPOS, 
                                                          UN_CONDICION=> MI_CONDICION
                                                          ); 
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
               END;
           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
               PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                          UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INDICFACTURADOS,
                                          UN_TABLAERROR => MI_TABLA);
           END; 
           BEGIN
               BEGIN
                   MI_TABLA := 'IP_FACTURADOSABONOS';
                   MI_CAMPOS:='PAGADO = -1, PAG_BAN=''' || UN_BANCO || ''', 
                               FECHAPAGO=TO_DATE('''||UN_FECHA_RECAUDO||''',''DD/MM/YYYY''), ' ||
                              'MODIFIED_BY   = ''' || UN_USER || ''', 
                               DATE_MODIFIED = SYSDATE';
                   MI_CONDICION := '     COMPANIA =     ''' || UN_COMPANIA    || 
                                   ''' AND CODIGO =     ''' || UN_PREDIO      ||
                                   ''' AND NUMERO_ORDEN=''' || UN_NUMERO_ORDEN||
                                   ''' AND DOCNUM     = ''' || UN_RECIBO      ||
                                   ''' AND PREANO     =    '|| MI_RSFACAB.PREANO;
                   MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA,
                                                UN_ACCION   =>'M',
                                                UN_CAMPOS   =>MI_CAMPOS,
                                                UN_CONDICION=>MI_CONDICION);   
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
               END;
           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
               PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                          UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INDICFACTURADOS,
                                          UN_TABLAERROR => MI_TABLA);
           END;
           BEGIN 
               BEGIN
                   MI_TABLA :='IP_RECIBOS_DE_PAGO';
                   MI_CAMPOS:='PAGO = -1, PAG_BANPAG = ''' || UN_BANCO||''', 
                               PREFECPAG=TO_DATE('''||UN_FECHA_RECAUDO||''',''DD/MM/YYYY''), 
                               PAQUETEPAG=''01'', FECHA_REGRECAUDO=SYSDATE, 
                               USUARIO_REGRECAUDO = ''' || UN_USER || ''', ' ||
                              'MODIFIED_BY   = ''' || UN_USER || ''', 
                               DATE_MODIFIED = SYSDATE';
                   MI_CONDICION := '    COMPANIA='''||UN_COMPANIA||''' 
                                    AND DOCNUM='''||UN_RECIBO||'''';
                   MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA, 
                                                UN_ACCION   => 'M', 
                                                UN_CAMPOS   => MI_CAMPOS, 
                                                UN_CONDICION=> MI_CONDICION
                                                          );   
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
               END;
           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
               PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                          UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTURECIBOSPAGO,
                                          UN_TABLAERROR => MI_TABLA);
           END; 
           BEGIN 
               BEGIN
                   MI_TABLA := 'IP_USUARIOS_PREDIAL';
                   MI_CAMPOS:='PAGO_ANO_ABONO2 = PAGO_ANO_ABONO1, 
                               VALORULTIMOABONO2 = VALORULTIMOABONO1, 
                               FECHAULTIMOABONO2 = FECHAULTIMOABONO1,
                               FACTURAULTIMOABONO2 = FACTURAULTIMOABONO1, 
                               BANCOULTIMOABONO2 = BANCOULTIMOABONO1, ' ||
                               ' MODIFIED_BY      = ''' || UN_USER || 
                               ''', DATE_MODIFIED = SYSDATE';
                   MI_CONDICION := 'COMPANIA='''||UN_COMPANIA||''' AND CODIGO='''||UN_PREDIO||''' AND NUMERO_ORDEN='''||UN_NUMERO_ORDEN||'''';
                   MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA,
                                                UN_ACCION   => 'M',
                                                UN_CAMPOS   => MI_CAMPOS,
                                                UN_CONDICION=> MI_CONDICION);   
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
               END;
               BEGIN
                   MI_CAMPOS:=  'PAGO_ANO_ABONO1 = PAGO_ANO_ABONO, 
                                 VALORULTIMOABONO1 = VALORULTIMOABONO, 
                                 FECHAULTIMOABONO1 = FECHAULTIMOABONO, 
                                 FACTURAULTIMOABONO1 = FACTURAULTIMOABONO, 
                                 BANCOULTIMOABONO1 = BANCOULTIMOABONO, ' ||
                                'MODIFIED_BY   = ''' || UN_USER || ''', 
                                 DATE_MODIFIED = SYSDATE';
                   MI_CONDICION := '  COMPANIA       = ''' || UN_COMPANIA || ''' 
                                    AND CODIGO       = ''' || UN_PREDIO||''' 
                                    AND NUMERO_ORDEN = ''' || UN_NUMERO_ORDEN||'''';
                   MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA,
                                                UN_ACCION   => 'M',
                                                UN_CAMPOS   => MI_CAMPOS, 
                                                UN_CONDICION=> MI_CONDICION);   
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
               END;
               BEGIN 
                   MI_CAMPOS:='    PAGO_ANO_ABONO = '||MI_RSFACAB.PREANO||', 
                                 VALORULTIMOABONO = '||NVL(MI_RSFACAB.TOTAL,0)||
                               ',FECHAULTIMOABONO = TO_DATE('''||UN_FECHA_RECAUDO||''',''DD/MM/YYYY''), 
                               FACTURAULTIMOABONO = '''||UN_RECIBO||
                             ''',BANCOULTIMOABONO = '''||UN_BANCO||''', ' || 
                                  ' MODIFIED_BY   = ''' || UN_USER || ''', 
                                    DATE_MODIFIED = SYSDATE';
                   MI_CONDICION := 'COMPANIA      = '''||UN_COMPANIA    ||''' 
                                  AND CODIGO      = '''||UN_PREDIO      ||''' 
                                  AND NUMERO_ORDEN= '''||UN_NUMERO_ORDEN||'''';
                   MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_USUARIOS_PREDIAL', 
                                                  UN_ACCION   =>'M', 
                                                  UN_CAMPOS   =>MI_CAMPOS, 
                                                  UN_CONDICION=>MI_CONDICION
                                                  );   
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
               END;
           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
               MI_REEMPLAZOS(0).CLAVE:= 'FACTURA';
               MI_REEMPLAZOS(0).VALOR:= UN_RECIBO;
               MI_REEMPLAZOS(1).CLAVE:= 'PREDIO';
               MI_REEMPLAZOS(1).VALOR:= UN_PREDIO;
               PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                         ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTFACTURADEPRE
                                         ,UN_TABLAERROR => MI_TABLA
                                         ,UN_REEMPLAZOS => MI_REEMPLAZOS);  
           END;    
           BEGIN 
               MI_TABLA := 'IP_FACTURADOS';
               BEGIN
                   MI_CAMPOS:='ALDIAINTERES2 = ALDIAINTERES1, 
                               ALDIAINTERES1 = ALDIAINTERES, 
                                ALDIAINTERES =TO_DATE('''||MI_RSFACAB.FECHAFACTURADO||''',''DD/MM/YYYY''), ' ||
                              ' MODIFIED_BY  = ''' || UN_USER || ''', 
                               DATE_MODIFIED = SYSDATE';
                   MI_CONDICION := '  COMPANIA ='''     || UN_COMPANIA || ''' 
                                    AND CODIGO ='''     || UN_PREDIO   || ''' 
                                    AND NUMERO_ORDEN='''||UN_NUMERO_ORDEN||''' 
                                    AND PAGADO IN (0)';
                   MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>MI_TABLA,
                                                UN_ACCION   =>'M', 
                                                UN_CAMPOS   =>MI_CAMPOS,
                                                UN_CONDICION=>MI_CONDICION);
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
               END;  
               BEGIN
               MI_CAMPOS := 'ALDIAINTERES2CAR = ALDIAINTERES1CAR, 
                             ALDIAINTERES1CAR = ALDIAINTERESCAR,   
                             ALDIAINTERESCAR=TO_DATE('''||MI_RSFACAB.FECHAFACTURADO||''',''DD/MM/YYYY''), ' ||
                            'MODIFIED_BY   = ''' || UN_USER || ''', 
                            DATE_MODIFIED = SYSDATE';
               MI_CONDICION :=  '   COMPANIA    = ''' || UN_COMPANIA    || ''' 
                                AND CODIGO      = ''' || UN_PREDIO      || ''' 
                                AND NUMERO_ORDEN= ''' || UN_NUMERO_ORDEN||''' 
                                AND PAGADO IN (0)';
               MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA,
                                            UN_ACCION   => 'M',
                                            UN_CAMPOS   => MI_CAMPOS, 
                                            UN_CONDICION=> MI_CONDICION); 
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
               END; 
           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INDICFACTURADOS
                                    ,UN_TABLAERROR => MI_TABLA);     
      END;    
        END IF;
    END LOOP REALIZAABONOOPAGO;

  END LOOP REVISAFACTURADOS;

  BEGIN
      MI_TABLA := 'IP_USUARIOS_PREDIAL';
      BEGIN
          MI_CAMPOS:='NUM_COM2 = NUM_COM1, PAGO_ANO2 = PAGO_ANO1, 
                      PAG_VAL2 = PAG_VAL1, PAG_FEC2 = PAG_FEC1, 
                      PAG_BAN2 = PAG_BAN1, ' ||
                     'MODIFIED_BY   = ''' || UN_USER || ''', 
                      DATE_MODIFIED = SYSDATE';
          MI_CONDICION := ' COMPANIA       = '''||UN_COMPANIA||''' 
                          AND CODIGO       = '''||UN_PREDIO||''' 
                          AND NUMERO_ORDEN = '''||UN_NUMERO_ORDEN||'''';
          MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA, 
                                       UN_ACCION   => 'M', 
                                       UN_CAMPOS   => MI_CAMPOS,
                                       UN_CONDICION=> MI_CONDICION);
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;
      BEGIN
          MI_CAMPOS:='NUM_COM1 = NUM_COM, PAGO_ANO1 = PAGO_ANO, 
                      PAG_VAL1 = PAG_VAL, PAG_FEC1 = PAG_FEC, 
                      PAG_BAN1 = PAG_BAN, ' ||
                     'MODIFIED_BY   = ''' || UN_USER || ''', 
                      DATE_MODIFIED = SYSDATE';
          MI_CONDICION :=  'COMPANIA       = ''' || UN_COMPANIA || ''' 
                           AND CODIGO      = ''' || UN_PREDIO   || ''' 
                           AND NUMERO_ORDEN= '''||UN_NUMERO_ORDEN||'''';
          MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>MI_TABLA, 
                                       UN_ACCION   =>'M',
                                       UN_CAMPOS   =>MI_CAMPOS, 
                                       UN_CONDICION=>MI_CONDICION);   
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;

      IF MI_PAGO_ANOFIN<>0 THEN
          BEGIN
              MI_CAMPOS:='NUM_COM = '''||UN_RECIBO||''', 
                          PAGO_ANO='||MI_PAGO_ANOFIN||' , 
                          PAG_VAL = '||UN_VALOR_RECIBO||', 
                          PAG_FEC = TO_DATE('''||UN_FECHA_RECAUDO||''',''DD/MM/YYYY''), 
                          PAG_BAN = '''||UN_BANCO||''', ' ||
                         'MODIFIED_BY   = ''' || UN_USER || ''', 
                          DATE_MODIFIED = SYSDATE';
              MI_CONDICION := '  COMPANIA = '''||UN_COMPANIA||''' 
                               AND CODIGO = '''||UN_PREDIO||''' 
                               AND NUMERO_ORDEN='''||UN_NUMERO_ORDEN||'''';
              MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_USUARIOS_PREDIAL',
                                           UN_ACCION   =>'M', 
                                           UN_CAMPOS   =>MI_CAMPOS, 
                                           UN_CONDICION=>MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
      END IF;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
               MI_REEMPLAZOS(0).CLAVE:= 'FACTURA';
               MI_REEMPLAZOS(0).VALOR:= UN_RECIBO;
               MI_REEMPLAZOS(1).CLAVE:= 'PREDIO';
               MI_REEMPLAZOS(1).VALOR:= UN_PREDIO;
               PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                         ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTFACTURADEPRE
                                         ,UN_TABLAERROR => MI_TABLA
                                         ,UN_REEMPLAZOS => MI_REEMPLAZOS);  
  END;  
END; 
END PR_REGISTRAR_RECAUDO_ABONO;

--8

PROCEDURE PR_REGISTRAR_RECAUDO_CUOTA
  /*
  NAME              : PR_REGISTRAR_RECAUDO_CUOTA En Access --> RegistrarRecaudoCuota
  AUTHORS           : SYSMAN  SAS 
  AUTHOR MIGRACION  : NICOLÁS GÓMEZ BARBOSA
  DATE MIGRADOR     : 11/08/2016
  TIME              : 12:00 PM
  SOURCE MODULE     : PREDIAL
  DESCRIPTION       : Registrar recaudo cuota
  MODIFIER          : Leydi Milena Cortés Forero
  DATE MODIFIED     : 22/08/2017
  TIME              : 12:40 PM
  MODIFICATIONS     : Se incluyen los campos de auditoría para las actualizaciones de tablas.
  @NAME:  registrarRecaudoCuotas
  @METHOD:  PUT                    
  */
  (
  -- Parametro que recibe el numero de la compania en la que se registra un recaudo
  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe el numero de recibo  
  UN_RECIBO                  IN PCK_SUBTIPOS.TI_DOCNUM,
  -- Parametro que recibe el numero del predio para registrar el recaudo
  UN_PREDIO                  IN PCK_SUBTIPOS.TI_CODPREDIO,
  -- Parametro que recibe el numero del banco en el que se registra el recaudo
  UN_BANCO                   IN IP_BANCOS.CODIGOBANCO%TYPE,
  -- Parametro que recibe la fecha de recaudo 
  UN_FECHA_RECAUDO           IN VARCHAR2,
  -- Parametro que recibe el numero de orden 
  UN_NUMERO_ORDEN            IN PCK_SUBTIPOS.TI_NUMORDEN,
  -- Parametro que recibe el numero del modulo
  UN_MODULO                  IN PCK_SUBTIPOS.TI_MODULO,
  -- Parametro que recibe el nombre del usuario
  UN_USER                    IN PCK_SUBTIPOS.TI_USUARIO,
  -- Parametro que recibe el valor del recibo
  UN_VALOR_RECIBO            IN PCK_SUBTIPOS.TI_DOBLE,
  -- Parametro que recibe el numero de cuotas al registrar un recaudo
  UN_NRO_CUOTA               IN PCK_SUBTIPOS.TI_DOBLE,
  -- Parametro que recibe la vigencia en la cual se registra un recaudo
  UN_VIGENCIA                IN PCK_SUBTIPOS.TI_ANIO  
  )
  AS
    -- Variable que almacenara la cadena con los campos que se van a modificar en las diferentes tablas.
    MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
    -- Variable que almacenara la cadena con la condicion con la que se van a realizar las actualizaciones
    MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;
    -- Variable que almacenra la cadena que retorna el parametro MANEJA EXCEDENTES DE VIG. ANTERIORES
    MI_MANEXCVIGANT          PCK_SUBTIPOS.TI_PARAMETRO;
    -- Variable que almacenara el valor del indicador de pago
    MI_PAGADO                PCK_SUBTIPOS.TI_LOGICO;
    -- Variable que almacenara las cuotas canceladas cuando se va a registrar el recaudo
    MI_CUOTASCANCELADAS      PCK_SUBTIPOS.TI_ENTERO_LARGO;
    -- Variable que almacenara el numero de cuotas cuando se va a registrar el recaudo
    MI_CANTIDADCUOTAS        PCK_SUBTIPOS.TI_ENTERO_LARGO;
    -- Variable que almacenara el numero de registros actualizados
    MI_RTA                   PCK_SUBTIPOS.TI_ENTERO_LARGO;
    -- Variable que almacena el nombre de la tabla a actualizar
    MI_TABLA                 PCK_SUBTIPOS.TI_TABLA;
    -- Variable que almacena los reemplazos en el mensaje de error
    MI_REEMPLAZOS            PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN
      MI_MANEXCVIGANT:=NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA =>UN_COMPANIA,
                                             UN_NOMBRE   =>'MANEJA EXCEDENTES DE VIG. ANTERIORES',
                                             UN_MODULO   =>UN_MODULO,
                                             UN_FECHA_PAR=>SYSDATE
                                             ),'NO');
      BEGIN
          SELECT PAGADO INTO MI_PAGADO
            FROM IP_FACTURADOSCUOTAS 
           WHERE COMPANIA = UN_COMPANIA
             AND CODIGO   = UN_PREDIO
             AND NUMERO_ORDEN = UN_NUMERO_ORDEN
             AND DOCNUM   = UN_RECIBO
             AND CUOTA    = UN_NRO_CUOTA
             AND PREANO   = UN_VIGENCIA;

      EXCEPTION WHEN NO_DATA_FOUND THEN
          RETURN;
      END;  

  IF MI_PAGADO IN (0) THEN
    -- BEGIN usado para el control de errores al realizar actualizaciones
         BEGIN
            MI_TABLA := 'IP_RECIBOS_DE_PAGO';
            BEGIN
                MI_CAMPOS :=  'PAGO = -1, PAG_BANPAG = ''' || UN_BANCO || ''',
                              PREFECPAG=TO_DATE('''||UN_FECHA_RECAUDO||''',''DD/MM/YYYY''), 
                              PAQUETEPAG=''01'', FECHA_REGRECAUDO=SYSDATE, 
                              USUARIO_REGRECAUDO = ''' || UN_USER || ''', ' || 
                              'MODIFIED_BY       = ''' || UN_USER || ''', ' || 
                              'DATE_MODIFIED  = SYSDATE';
                MI_CONDICION :=  'COMPANIA   = ''' || UN_COMPANIA || ''' 
                                  AND DOCNUM = ''' || UN_RECIBO   || '''';
                MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_RECIBOS_DE_PAGO',
                                                       UN_ACCION   =>'M', 
                                                       UN_CAMPOS   =>MI_CAMPOS, 
                                                       UN_CONDICION=>MI_CONDICION); 
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_ACTUALIZAR;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                      ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTURECIBOSPAGO
                                      ,UN_TABLAERROR => MI_TABLA);
        END;
        BEGIN
            BEGIN
                MI_TABLA := 'IP_FACTURADOSCUOTAS';
                MI_CAMPOS:= 'PAGADO = -1, PAG_BAN='''||UN_BANCO||''', 
                             FECHAPAGO=TO_DATE('''||UN_FECHA_RECAUDO||''',''DD/MM/YYYY''), ' || 
                            'MODIFIED_BY    = ''' || UN_USER || ''', ' || 
                            'DATE_MODIFIED  = SYSDATE';
                MI_CONDICION := ' COMPANIA       = ''' || UN_COMPANIA    || ''' 
                                AND CODIGO       = ''' || UN_PREDIO      || ''' 
                                AND NUMERO_ORDEN = ''' || UN_NUMERO_ORDEN|| ''' 
                                AND DOCNUM       = ''' || UN_RECIBO      ||''' 
                                AND CUOTA        =   ' || UN_NRO_CUOTA;
                MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA,
                                             UN_ACCION   => 'M',
                                             UN_CAMPOS   => MI_CAMPOS,
                                             UN_CONDICION=> MI_CONDICION);  
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_ACTUALIZAR;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                      ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTCUOTAFACTURA
                                      ,UN_TABLAERROR => MI_TABLA);
        END;
        BEGIN
            BEGIN
                MI_CAMPOS:= 'PAGO_CUOTAS = -1, MODIFIED_BY    = ''' || UN_USER || ''', ' || 
                            'DATE_MODIFIED  = SYSDATE';
                MI_CONDICION := 'COMPANIA        = ''' || UN_COMPANIA    ||''' 
                                 AND CODIGO      = ''' || UN_PREDIO      ||''' 
                                 AND NUMERO_ORDEN= ''' || UN_NUMERO_ORDEN||'''';
                MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_USUARIOS_PREDIAL',
                                                       UN_ACCION   =>'M', 
                                                       UN_CAMPOS   =>MI_CAMPOS,
                                                       UN_CONDICION=>MI_CONDICION);  
             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_ACTUALIZAR;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
               MI_REEMPLAZOS(0).CLAVE:= 'FACTURA';
               MI_REEMPLAZOS(0).VALOR:= UN_RECIBO;
               MI_REEMPLAZOS(1).CLAVE:= 'PREDIO';
               MI_REEMPLAZOS(1).VALOR:= UN_PREDIO;
               PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                         ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTFACTURADEPRE
                                         ,UN_TABLAERROR => MI_TABLA
                                         ,UN_REEMPLAZOS => MI_REEMPLAZOS);  
        END;  
        BEGIN
            BEGIN
                MI_TABLA := 'IP_FACTURADOS';
                MI_CAMPOS:='INDPAGO_CUOTAS = -1, 
                            MODIFIED_BY    = ''' || UN_USER || ''', ' || '
                            DATE_MODIFIED  = SYSDATE';
                MI_CONDICION := 'COMPANIA        = ''' || UN_COMPANIA     ||''' AND CODIGO ='''||UN_PREDIO||''' 
                                 AND NUMERO_ORDEN= ''' || UN_NUMERO_ORDEN ||''' AND PREANO = ' ||UN_VIGENCIA;
                MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA=>MI_TABLA,
                                             UN_ACCION=>'M', 
                                             UN_CAMPOS=>MI_CAMPOS,
                                             UN_CONDICION=>MI_CONDICION);  
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END ; 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INDICFACTURADOS
                                    ,UN_TABLAERROR => MI_TABLA);     
        END;

    SELECT COUNT(PAGADO) INTO MI_CUOTASCANCELADAS
      FROM IP_FACTURADOSCUOTAS 
     WHERE COMPANIA = UN_COMPANIA
       AND CODIGO   = UN_PREDIO
       AND NUMERO_ORDEN = UN_NUMERO_ORDEN
       AND PREANO   = UN_VIGENCIA
       AND PAGADO NOT IN (0)
     GROUP BY CODIGO;

    SELECT MAX(CUOTA) INTO MI_CANTIDADCUOTAS
      FROM IP_FACTURADOSCUOTAS 
     WHERE COMPANIA = UN_COMPANIA
       AND CODIGO   = UN_PREDIO
       AND NUMERO_ORDEN = UN_NUMERO_ORDEN
       AND PREANO   = UN_VIGENCIA
     GROUP BY CODIGO;

    --BEGIN usado para el control de errores al realizar las actualizaciones
    BEGIN   
    IF MI_CUOTASCANCELADAS=MI_CANTIDADCUOTAS THEN
        BEGIN
            BEGIN
                MI_CAMPOS:='PAGADO = -1';
                MI_CONDICION := 'COMPANIA='''||UN_COMPANIA||''' AND CODIGO='''||UN_PREDIO||
                      ''' AND NUMERO_ORDEN='''||UN_NUMERO_ORDEN||''' AND PREANO = '||UN_VIGENCIA || 
                      ', MODIFIED_BY    = ''' || UN_USER || ''', ' || 'DATE_MODIFIED  = SYSDATE';
                MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOS', 
                                             UN_ACCION   =>'M',
                                             UN_CAMPOS   =>MI_CAMPOS, 
                                             UN_CONDICION=>MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END ; 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INDICFACTURADOS
                                    ,UN_TABLAERROR => MI_TABLA);     
        END;
              IF MI_MANEXCVIGANT='SI' THEN 
                  BEGIN
                      BEGIN
                          MI_CAMPOS:='PAGO = -1';
                          MI_CONDICION := '  COMPANIA         = ''' || UN_COMPANIA    ||
                                          ''' AND PRECOD='''||UN_PREDIO||
                                          ''' AND NUMERO_ORDEN= ''' || UN_NUMERO_ORDEN||
                                          ''' AND PREANO<='||UN_VIGENCIA||' 
                                              AND PAGO IN (0), ' ||
                                             'MODIFIED_BY    = ''' || UN_USER || ''', ' || 
                                             'DATE_MODIFIED  = SYSDATE';
                          MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_PAGOSDOBLES',
                                                       UN_ACCION   =>'M', 
                                                       UN_CAMPOS   => MI_CAMPOS, 
                                                       UN_CONDICION=>MI_CONDICION
                                                       );   
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                      END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                      PCK_ERR_MSG.RAISE_WITH_MSG(  UN_EXC_COD    => SQLCODE
                                                  ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTUPAGOSDOBLES
                                                  ,UN_TABLAERROR => MI_TABLA);  
                  END;

              ELSE
                BEGIN
                    BEGIN
                        MI_CAMPOS:='PAGO = -1, MODIFIED_BY    = ''' || UN_USER || ''', ' || 
                                   'DATE_MODIFIED  = SYSDATE';
                        MI_CONDICION := 'COMPANIA ='''||UN_COMPANIA||''' 
                                        AND PRECOD='''||UN_PREDIO||''' 
                                        AND NUMERO_ORDEN='''||UN_NUMERO_ORDEN||''' 
                                        AND PREANO='||UN_VIGENCIA||' 
                                        AND PAGO IN (0)';
                        MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_PAGOSDOBLES', 
                                                     UN_ACCION   =>'M', 
                                                     UN_CAMPOS   =>MI_CAMPOS,
                                                     UN_CONDICION=>MI_CONDICION); 
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                      END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                      PCK_ERR_MSG.RAISE_WITH_MSG(  UN_EXC_COD    => SQLCODE
                                                  ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTUPAGOSDOBLES
                                                  ,UN_TABLAERROR => MI_TABLA);  
                  END;                                
              END IF;

      BEGIN
          MI_TABLA := 'IP_USUARIOS_PREDIAL';
          BEGIN
              MI_CAMPOS:='PAGO_CUOTAS = 0, PAGO_ANO='||UN_VIGENCIA||', 
                          PAG_FEC=TO_DATE('''||UN_FECHA_RECAUDO||''',''DD/MM/YYYY''), 
                          PAG_VAL='||UN_VALOR_RECIBO||', NUM_COM='''||UN_RECIBO||''', 
                          PAG_BAN='''||UN_BANCO||''', ' || 
                          'MODIFIED_BY    = ''' || UN_USER || ''', ' || 
                          'DATE_MODIFIED  = SYSDATE';
              MI_CONDICION := 'COMPANIA='''||UN_COMPANIA||''' 
                               AND CODIGO='''||UN_PREDIO||''' 
                               AND NUMERO_ORDEN='''||UN_NUMERO_ORDEN||'''';
              MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_USUARIOS_PREDIAL', 
                                                     UN_ACCION   =>'M',
                                                     UN_CAMPOS   =>MI_CAMPOS,
                                                     UN_CONDICION=>MI_CONDICION
                                                     ); 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
          BEGIN
              MI_CAMPOS:='PAGO_ANO_CUOTA2 = PAGO_ANO_CUOTA1, 
                          FACTURA_CUOTA2 = FACTURA_CUOTA1, 
                          TOTAL_CUOTA2 = TOTAL_CUOTA1, 
                          PAGBAN_CUOTA2 = PAGBAN_CUOTA1, 
                          PAGFEC_CUOTA2 = PAGFEC_CUOTA1, 
                          PAGO_NCUOTA2 = PAGO_NCUOTA1, ' ||
                          'MODIFIED_BY    = ''' || UN_USER || ''', ' || 
                          'DATE_MODIFIED  = SYSDATE';
              MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||''' 
                              AND CODIGO= '''||UN_PREDIO||''' 
                              AND NUMERO_ORDEN='''||UN_NUMERO_ORDEN||'''';
              MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_USUARIOS_PREDIAL', 
                                                     UN_ACCION   =>'M', 
                                                     UN_CAMPOS   =>MI_CAMPOS,
                                                     UN_CONDICION=>MI_CONDICION
                                                     ); 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;

          BEGIN
              MI_CAMPOS:='PAGO_ANO_CUOTA1 = PAGO_ANO_CUOTA, 
                          FACTURA_CUOTA1  = FACTURA_CUOTA, 
                          TOTAL_CUOTA1 = TOTAL_CUOTA, 
                          PAGBAN_CUOTA1 = PAGBAN_CUOTA, 
                          PAGFEC_CUOTA1 = PAGFEC_CUOTA, 
                          PAGO_NCUOTA1 = PAGO_NCUOTA, ' ||
                          'MODIFIED_BY    = ''' || UN_USER || ''', ' || 
                          'DATE_MODIFIED  = SYSDATE';
              MI_CONDICION := 'COMPANIA       ='''||UN_COMPANIA||''' 
                              AND CODIGO      ='''||UN_PREDIO||''' 
                              AND NUMERO_ORDEN='''||UN_NUMERO_ORDEN||'''';
              MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_USUARIOS_PREDIAL',
                                                     UN_ACCION   =>'M', 
                                                     UN_CAMPOS   =>MI_CAMPOS,
                                                     UN_CONDICION=>MI_CONDICION ); 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
          BEGIN
              MI_CAMPOS:='PAGO_ANO_CUOTA = 0, FACTURA_CUOTA = 0, TOTAL_CUOTA = 0, 
                          PAGBAN_CUOTA = 0, PAGFEC_CUOTA = NULL, PAGO_NCUOTA = 0, ' ||
                          'MODIFIED_BY    = ''' || UN_USER || ''', ' || 'DATE_MODIFIED  = SYSDATE';
              MI_CONDICION := 'COMPANIA       ='''||UN_COMPANIA||''' 
                              AND CODIGO      ='''||UN_PREDIO||''' 
                              AND NUMERO_ORDEN='''||UN_NUMERO_ORDEN||'''';
              MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_USUARIOS_PREDIAL', 
                                                     UN_ACCION   =>'M', 
                                                     UN_CAMPOS   =>MI_CAMPOS, 
                                                     UN_CONDICION=>MI_CONDICION
                                                     ); 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;                                           
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
          MI_REEMPLAZOS(0).CLAVE:= 'FACTURA';
          MI_REEMPLAZOS(0).VALOR:= UN_RECIBO;
          MI_REEMPLAZOS(1).CLAVE:= 'PREDIO';
          MI_REEMPLAZOS(1).VALOR:= UN_PREDIO;
          PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTFACTURADEPRE
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_REEMPLAZOS => MI_REEMPLAZOS);  
      END;                                           
    ELSE 
      BEGIN
          BEGIN
              MI_CAMPOS:='PAGO_ANO_CUOTA2 = PAGO_ANO_CUOTA1, FACTURA_CUOTA2 = FACTURA_CUOTA1, 
                          TOTAL_CUOTA2 = TOTAL_CUOTA1, PAGBAN_CUOTA2 = PAGBAN_CUOTA1, 
                          PAGFEC_CUOTA2 = PAGFEC_CUOTA1, PAGO_NCUOTA2 = PAGO_NCUOTA1, ' ||
                          'MODIFIED_BY    = ''' || UN_USER || ''', ' || 'DATE_MODIFIED  = SYSDATE';
              MI_CONDICION := 'COMPANIA  ='''||UN_COMPANIA||''' 
                               AND CODIGO='''||UN_PREDIO||''' 
                               AND NUMERO_ORDEN='''||UN_NUMERO_ORDEN||'''';
              MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_USUARIOS_PREDIAL',
                                                     UN_ACCION   =>'M',
                                                     UN_CAMPOS   =>MI_CAMPOS,
                                                     UN_CONDICION=>MI_CONDICION
                                                     ); 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
          BEGIN
              MI_CAMPOS:='PAGO_ANO_CUOTA1 = PAGO_ANO_CUOTA, 
                          FACTURA_CUOTA1 = FACTURA_CUOTA, 
                          TOTAL_CUOTA1 = TOTAL_CUOTA, 
                          PAGBAN_CUOTA1 = PAGBAN_CUOTA, 
                          PAGFEC_CUOTA1 = PAGFEC_CUOTA, 
                          PAGO_NCUOTA1 = PAGO_NCUOTA, ' ||
                          'MODIFIED_BY    = ''' || UN_USER || ''', ' || 
                          'DATE_MODIFIED  = SYSDATE';
              MI_CONDICION := 'COMPANIA='''||UN_COMPANIA||''' 
                               AND CODIGO='''||UN_PREDIO||''' 
                               AND NUMERO_ORDEN='''||UN_NUMERO_ORDEN||'''';
              MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_USUARIOS_PREDIAL',
                                                     UN_ACCION   =>'M',
                                                     UN_CAMPOS   =>MI_CAMPOS,
                                                     UN_CONDICION=>MI_CONDICION
                                                     ); 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
          BEGIN
              MI_CAMPOS:='PAGO_ANO_CUOTA = '||UN_VIGENCIA||', 
                          FACTURA_CUOTA='''||UN_RECIBO||''', 
                          TOTAL_CUOTA='||UN_VALOR_RECIBO||', 
                          PAGBAN_CUOTA='''||UN_BANCO||''', 
                          PAGFEC_CUOTA=TO_DATE('''||UN_FECHA_RECAUDO||''',''DD/MM/YYYY''), 
                          PAGO_NCUOTA='||UN_NRO_CUOTA || 'MODIFIED_BY    = ''' || UN_USER || ''', ' || 'DATE_MODIFIED  = SYSDATE';
              MI_CONDICION := 'COMPANIA='''||UN_COMPANIA||''' 
                              AND CODIGO='''||UN_PREDIO||''' 
                              AND NUMERO_ORDEN='''||UN_NUMERO_ORDEN||'''';
              MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_USUARIOS_PREDIAL',
                                                     UN_ACCION   =>'M', 
                                                     UN_CAMPOS   =>MI_CAMPOS,
                                                     UN_CONDICION=>MI_CONDICION); 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END; 
          BEGIN
              MI_CAMPOS:='PAG_VAL='||UN_VALOR_RECIBO||', 
                          PAG_FEC=TO_DATE('''||UN_FECHA_RECAUDO||''',''DD/MM/YYYY''), 
                          NUM_COM='''||UN_RECIBO||''', PAG_BAN='''||UN_BANCO||''', ' || 
                          'MODIFIED_BY    = ''' || UN_USER || ''', ' || 
                          'DATE_MODIFIED  = SYSDATE';
              MI_CONDICION := 'COMPANIA='''||UN_COMPANIA||''' 
                               AND CODIGO='''||UN_PREDIO||''' 
                               AND NUMERO_ORDEN='''||UN_NUMERO_ORDEN||'''';
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_USUARIOS_PREDIAL',
                                                     UN_ACCION   =>'M', 
                                                     UN_CAMPOS   =>MI_CAMPOS, 
                                                     UN_CONDICION=>MI_CONDICION
                                                     ); 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END; 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
          MI_REEMPLAZOS(0).CLAVE:= 'FACTURA';
          MI_REEMPLAZOS(0).VALOR:= UN_RECIBO;
          MI_REEMPLAZOS(1).CLAVE:= 'PREDIO';
          MI_REEMPLAZOS(1).VALOR:= UN_PREDIO;
          PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTFACTURADEPRE
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_REEMPLAZOS => MI_REEMPLAZOS);  
      END;
    END IF;
      BEGIN
          BEGIN
              MI_CAMPOS:='TOTALCUOTA2 = TOTALCUOTA1, PAGNCUOTA2 = PAGNCUOTA1, 
                          PAGFECCUOTA2 = PAGFECCUOTA1, DOCNUMCUOTA2 = DOCNUMCUOTA1, ' ||
                         'MODIFIED_BY    = ''' || UN_USER || ''', ' || 'DATE_MODIFIED  = SYSDATE';
              MI_CONDICION := 'COMPANIA       ='''||UN_COMPANIA    ||''' AND CODIGO='''||UN_PREDIO||''' 
                              AND NUMERO_ORDEN='''||UN_NUMERO_ORDEN||''' AND PREANO = '||UN_VIGENCIA;
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOS', 
                                                     UN_ACCION   =>'M',
                                                     UN_CAMPOS   =>MI_CAMPOS,
                                                     UN_CONDICION=>MI_CONDICION
                                                     );
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
          BEGIN
              MI_CAMPOS:='TOTALCUOTA1 = TOTALCUOTA, PAGNCUOTA1 = PAGNCUOTA, 
                          PAGFECCUOTA = PAGFECCUOTA, DOCNUMCUOTA1 = DOCNUMCUOTA, ' ||
                         'MODIFIED_BY    = ''' || UN_USER || ''', ' || 'DATE_MODIFIED  = SYSDATE';
              MI_CONDICION := 'COMPANIA='''||UN_COMPANIA||''' 
                              AND CODIGO='''||UN_PREDIO||''' 
                              AND NUMERO_ORDEN='''||UN_NUMERO_ORDEN||''' 
                              AND PREANO = '||UN_VIGENCIA;
              MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOS',
                                             UN_ACCION   =>'M', 
                                             UN_CAMPOS   =>MI_CAMPOS, 
                                             UN_CONDICION=>MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
          BEGIN
              MI_CAMPOS:='TOTALCUOTA = '||UN_VALOR_RECIBO||', PAGNCUOTA = '||UN_NRO_CUOTA||', 
                          PAGFECCUOTA = TO_DATE('''||UN_FECHA_RECAUDO||''',''DD/MM/YYYY''), 
                          DOCNUMCUOTA='''||UN_RECIBO||''', ' || 'MODIFIED_BY    = ''' || UN_USER || ''', ' || 
                          'DATE_MODIFIED  = SYSDATE';
              MI_CONDICION := 'COMPANIA       ='''||UN_COMPANIA    ||''' AND CODIGO='''||UN_PREDIO||''' 
                              AND NUMERO_ORDEN='''||UN_NUMERO_ORDEN||''' AND PREANO = '||UN_VIGENCIA;
              MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOS',
                                           UN_ACCION   =>'M',
                                           UN_CAMPOS   =>MI_CAMPOS,
                                           UN_CONDICION=>MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
          BEGIN
              MI_CAMPOS:='PAG_BAN = '''||UN_BANCO||''', PREVAL = '||UN_VALOR_RECIBO||', DOCNUM='''||UN_RECIBO||
                         ''' , FECHAPAGO = TO_DATE('''||UN_FECHA_RECAUDO||''',''DD/MM/YYYY''), 
                         OBSERVACIONES=''CANC. '||MI_CUOTASCANCELADAS||'/'||MI_CANTIDADCUOTAS||''', ' || 
                         'MODIFIED_BY    = ''' || UN_USER || ''', ' || 'DATE_MODIFIED  = SYSDATE';
              MI_CONDICION := 'COMPANIA        ='''||UN_COMPANIA    ||''' AND CODIGO='''||UN_PREDIO||''' 
                               AND NUMERO_ORDEN='''||UN_NUMERO_ORDEN||''' AND PREANO = '||UN_VIGENCIA;
              MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOS', 
                                                     UN_ACCION   =>'M',
                                                     UN_CAMPOS   =>MI_CAMPOS, 
                                                     UN_CONDICION=>MI_CONDICION
                                                     );
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                   PCK_ERR_MSG.RAISE_WITH_MSG(
                   UN_EXC_COD =>SQLCODE,
                   UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_INDICFACTURADOS, 
                   UN_TABLAERROR =>'IP_FACTURADOS');

  END;
  END;
  END IF;
END PR_REGISTRAR_RECAUDO_CUOTA;

--9

PROCEDURE PR_REGISTRAR_RECAUDO_ACUERDO
  /*
  NAME              : PR_REGISTRAR_RECAUDO_ACUERDO En Access --> RegistrarRecaudoAcuerdo
  AUTHORS           : SYSMAN  SAS 
  AUTHOR MIGRACION  : NICOLÁS GÓMEZ BARBOSA
  DATE MIGRADOR     : 11/08/2016
  TIME              : 15:30 PM
  SOURCE MODULE     : PREDIAL
  DESCRIPTION       : Registrar recaudo acuerdo
  @NAME:  registrarRecaudoAcuerdos
  @METHOD:  PUT                      
  */
  (
  -- Parametro que recibe el numero de la compania
  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe el numero del recibo al registrar el recaudo acuerdo
  UN_RECIBO                  IN PCK_SUBTIPOS.TI_DOCNUM,
  -- Parametro que recibe el numero del predio 
  UN_PREDIO                  IN PCK_SUBTIPOS.TI_CODPREDIO,
  -- Parametro que recibe le numero del banco en el que se registra el recaudo
  UN_BANCO                   IN IP_BANCOS.CODIGOBANCO%TYPE,
  -- Parametro que recibe la fecha en la que se registra el recaudo
  UN_FECHA_RECAUDO           IN VARCHAR2,
  -- Parametro que recibe el numero de orden 
  UN_NUMERO_ORDEN            IN PCK_SUBTIPOS.TI_NUMORDEN,
  -- Parametro que recibe el numero del modulo
  UN_MODULO                  IN PCK_SUBTIPOS.TI_MODULO,
  -- Parametro que recibe el nombre del usuario que registra el recaudo
  UN_USER                    IN PCK_SUBTIPOS.TI_USUARIO,
  -- Parametro que recibe el valor por el cual se hace el registro del pago
  UN_VALOR_RECIBO            IN PCK_SUBTIPOS.TI_DOBLE

  )
  AS
    -- Variable que almacenara la cadena con los campos que se van a modificar en la funcion
    MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
    -- Variable que almacenara la cadena con la condicion que se usa al momento de hacer la actualizacion
    MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;
    -- Variable que almacenara el valor que retorna un parametro
    MI_MANEXCVIGANT          PCK_SUBTIPOS.TI_PARAMETRO;
    -- Variable que almacenara el numero de cuotas
    MI_RSACUERDOS_NCUOTAS    PCK_SUBTIPOS.TI_DOBLE;
    -- Variable que almacenara el ano en el que se registra el recaudo
    MI_RSACUERDOS_PREANO     PCK_SUBTIPOS.TI_ANIO;
    -- Varibale que almacenara el ano inicial en el que se hace el recaudo
    MI_RSACUERDOS_PREANOI    PCK_SUBTIPOS.TI_ANIO;
    -- Variable que almacenara el numero de cuotas canceladas al momento de realizar un recaudo
    MI_CANCELADAS            PCK_SUBTIPOS.TI_ENTERO_LARGO;
    -- Variable que almacenara la tabla que se actualiza en ese procedimiento
    MI_TABLA                 PCK_SUBTIPOS.TI_TABLA;
    -- Variable que almacenara los reemplazos para mostrar en el mensajes de error
    MI_REEMPLAZOS            PCK_SUBTIPOS.TI_CLAVEVALOR; 
    -- Variable que almacenara la cantidad de registros que se actualicen
    MI_RTA                   PCK_SUBTIPOS.TI_ENTERO_LARGO;

  BEGIN

  MI_MANEXCVIGANT:=NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA =>UN_COMPANIA,
                                             UN_NOMBRE   =>'MANEJA EXCEDENTES DE VIG. ANTERIORES',
                                             UN_MODULO   =>UN_MODULO,
                                             UN_FECHA_PAR=>SYSDATE
                                             ),'NO');

  -- BEGIN usado para control de errores al actualizar la tabla IP_RECIBOS_DE_PAGO
  BEGIN
      BEGIN
          BEGIN
              MI_TABLA := 'IP_RECIBOS_DE_PAGO';
              MI_CAMPOS:='PAGO = -1, PAG_BANPAG='''||UN_BANCO||''', 
                          PREFECPAG=TO_DATE('''||UN_FECHA_RECAUDO||''',''DD/MM/YYYY''), 
                          PAQUETEPAG=''01'', FECHA_REGRECAUDO=SYSDATE,
                          DATE_MODIFIED = SYSDATE,
                          MODIFIED_BY = ''' || UN_USER || ''',
                          USUARIO_REGRECAUDO='''||UN_USER||'''';
              MI_CONDICION := ' COMPANIA = ''' || UN_COMPANIA || ''' 
                              AND DOCNUM = ''' || UN_RECIBO   || '''';
              MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA,
                                           UN_ACCION   => 'M',
                                           UN_CAMPOS   => MI_CAMPOS,
                                           UN_CONDICION=> MI_CONDICION ); 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTURECIBOSPAGO
                                    ,UN_TABLAERROR => MI_TABLA);
      END;

  <<REVISARECAUDO>>
  FOR MI_RSFACTURADOSACUERDOS IN (SELECT CODIGOACUERDO,
                                         PREDIO, 
                                         PREANOI,
                                         PREANO, 
                                         CUOTA 
                                    FROM IP_FACTURADOSACUERDOS 
                                   WHERE COMPANIA = UN_COMPANIA
                                     AND NUMERO_ORDEN = UN_NUMERO_ORDEN
                                     AND PREDIO   = UN_PREDIO
                                     AND DOCNUM   = UN_RECIBO)
  LOOP

    -- BEGIN usado para control de errores al revisar el recaudo 

        BEGIN
            BEGIN
                MI_TABLA := 'IP_FACTURADOSACUERDOS';
                MI_CAMPOS:= 'PAGADO = -1, PAG_BAN='''||UN_BANCO||''', 
                             DATE_MODIFIED = SYSDATE,
                             MODIFIED_BY = ''' || UN_USER || ''',
                             FECHAPAGO=TO_DATE( '''||UN_FECHA_RECAUDO||''',''DD/MM/YYYY'')';
                MI_CONDICION := 'COMPANIA        ='''||UN_COMPANIA||''' 
                                 AND PREDIO      ='''||UN_PREDIO||''' 
                                 AND NUMERO_ORDEN='''||UN_NUMERO_ORDEN||''' 
                                 AND DOCNUM      ='''||UN_RECIBO||'''';
                MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA, 
                                             UN_ACCION   => 'M', 
                                             UN_CAMPOS   => MI_CAMPOS, 
                                             UN_CONDICION=> MI_CONDICION );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
            BEGIN
                MI_TABLA := 'IP_FACTURADOSACUERDOS';
                MI_CAMPOS:= 'PAGADO = -1, DATE_MODIFIED = SYSDATE, MODIFIED_BY = ''' || UN_USER || '''';
                MI_CONDICION := 'COMPANIA         ='''|| UN_COMPANIA                           ||''' 
                                 AND NVL(TOTAL,0)=0 
                                 AND NUMERO_ORDEN ='''|| UN_NUMERO_ORDEN                       ||''' 
                                 AND CODIGOACUERDO='''|| MI_RSFACTURADOSACUERDOS.CODIGOACUERDO ||''' 
                                 AND CUOTA        = ' || MI_RSFACTURADOSACUERDOS.CUOTA;
                MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>MI_TABLA, 
                                             UN_ACCION   =>'M', 
                                             UN_CAMPOS   =>MI_CAMPOS,
                                             UN_CONDICION=>MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;                                                       

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACUERDORECAUDO
                                    ,UN_TABLAERROR => MI_TABLA);
        END;
    IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA =>UN_COMPANIA, 
                                 UN_NOMBRE   =>'TRABAJA CUOTAS ACUERDO SEGUN VIGENCIAS ADEUDADAS',
                                 UN_MODULO   =>UN_MODULO,
                                 UN_FECHA_PAR=>SYSDATE
                                 ),'NO')='SI' THEN 

      BEGIN
          BEGIN
              MI_TABLA := 'IP_FACTURADOS';
              MI_CAMPOS:='PAGADO = -1, INDPAGO_ACPAG = -1, 
                          DOCNUM='''||UN_RECIBO||''', 
                          PAG_BAN='''||UN_BANCO||''', 
                          PREVAL='||UN_RECIBO||',
                          DATE_MODIFIED = SYSDATE,
                          MODIFIED_BY = ''' || UN_USER || ''',
                          FECHAPAGO=TO_DATE('''||UN_FECHA_RECAUDO||''',''DD/MM/YYYY'')';
              MI_CONDICION := 'COMPANIA       = ''' || UN_COMPANIA ||''' 
                              AND CODIGO      = ''' || UN_PREDIO   ||''' 
                              AND NUMERO_ORDEN= ''' || UN_NUMERO_ORDEN||''' 
                              AND PREANO BETWEEN '  || MI_RSFACTURADOSACUERDOS.PREANOI||' 
                              AND '||MI_RSFACTURADOSACUERDOS.PREANO;
              MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA,
                                           UN_ACCION   => 'M',
                                           UN_CAMPOS   => MI_CAMPOS, 
                                           UN_CONDICION=> MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INDICFACTURADOS
                                    ,UN_TABLAERROR => MI_TABLA);
        END;
    END IF;

    SELECT NCUOTAS,
           PREANO,
           PREANOI 
           INTO MI_RSACUERDOS_NCUOTAS,
           MI_RSACUERDOS_PREANO,
           MI_RSACUERDOS_PREANOI
      FROM IP_ACUERDOS 
     WHERE COMPANIA      = UN_COMPANIA
       AND CODIGOACUERDO = MI_RSFACTURADOSACUERDOS.CODIGOACUERDO
       AND NUMERO_ORDEN  = UN_NUMERO_ORDEN
       AND PREDIO        = UN_PREDIO;

    SELECT NVL(MAX(CUOTA),-1) INTO MI_CANCELADAS 
      FROM IP_FACTURADOSACUERDOS 
    WHERE COMPANIA       = UN_COMPANIA
       AND NUMERO_ORDEN  = UN_NUMERO_ORDEN
       AND CODIGOACUERDO = MI_RSFACTURADOSACUERDOS.CODIGOACUERDO
       AND PREDIO        = UN_PREDIO
       AND PAGADO NOT IN (0);

    IF MI_CANCELADAS <> -1 AND MI_CANCELADAS = MI_RSACUERDOS_NCUOTAS THEN
      -- BEGIN usado para control de errores al realizar las actualizaciones
      BEGIN

          BEGIN
              MI_TABLA := 'IP_ACUERDOS';
              MI_CAMPOS     := 'CANCELADO = -1, DATE_MODIFIED = SYSDATE, MODIFIED_BY = ''' || UN_USER || '''';
              MI_CONDICION  := 'COMPANIA         ='''||UN_COMPANIA||''' 
                                AND PREDIO       ='''||UN_PREDIO||''' 
                                AND NUMERO_ORDEN ='''||UN_NUMERO_ORDEN||''' 
                                AND CODIGOACUERDO='''||MI_RSFACTURADOSACUERDOS.CODIGOACUERDO||'''';
              MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA, 
                                           UN_ACCION   => 'M', 
                                           UN_CAMPOS   => MI_CAMPOS, 
                                           UN_CONDICION=> MI_CONDICION);  
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACUERDORECAUDO
                                    ,UN_TABLAERROR => MI_TABLA);
      END;                              
      BEGIN
          BEGIN
              MI_TABLA := 'IP_USUARIOS_PREDIAL';
              MI_CAMPOS:='PAGO_ANO2 = NVL(PAGO_ANO1,0), 
                          PAG_VAL2 = NVL(PAG_VAL1,0), 
                          PAG_FEC2 = PAG_FEC1, NUM_COM2 = NUM_COM1, 
                          PAGO_ANO1 = NVL(PAGO_ANO,0), 
                          PAG_VAL1 = NVL(PAG_VAL,0), 
                          PAG_FEC1 = PAG_FEC, NUM_COM1 = NUM_COM,
                          PAGO_ANO = '||MI_RSACUERDOS_PREANO||', 
                          PAG_VAL = '||UN_VALOR_RECIBO||', 
                          PAG_FEC=TO_DATE('''||UN_FECHA_RECAUDO||''',''DD/MM/YYYY''), 
                          NUM_COM='''||UN_RECIBO||''',  PAGO_ACUERDO = 0, 
                          DATE_MODIFIED = SYSDATE, MODIFIED_BY = ''' || UN_USER || ''',
                          PAG_BAN='''||UN_BANCO||'''';
              MI_CONDICION := 'COMPANIA         ='''||UN_COMPANIA     ||''' 
                               AND CODIGO       ='''||UN_PREDIO       ||''' 
                               AND NUMERO_ORDEN ='''||UN_NUMERO_ORDEN ||'''';
              MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA, 
                                           UN_ACCION   => 'M', 
                                           UN_CAMPOS   => MI_CAMPOS,
                                           UN_CONDICION=> MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
               MI_REEMPLAZOS(0).CLAVE:= 'FACTURA';
               MI_REEMPLAZOS(0).VALOR:= UN_RECIBO;
               MI_REEMPLAZOS(1).CLAVE:= 'PREDIO';
               MI_REEMPLAZOS(1).VALOR:= UN_PREDIO;
               PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                         ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTFACTURADEPRE
                                         ,UN_TABLAERROR => MI_TABLA
                                         ,UN_REEMPLAZOS => MI_REEMPLAZOS);
      END;
      BEGIN
          BEGIN
              MI_TABLA := 'IP_FACTURADOS';
              MI_CAMPOS:='PAGADO = -1, DATE_MODIFIED = SYSDATE, MODIFIED_BY = ''' || UN_USER || '''';
              MI_CONDICION := 'COMPANIA         ='''||UN_COMPANIA||''' 
                              AND CODIGO        ='''||UN_PREDIO||''' 
                              AND NUMERO_ORDEN  ='''||UN_NUMERO_ORDEN||''' 
                              AND PREANO BETWEEN   '||MI_RSACUERDOS_PREANOI||' AND '||MI_RSACUERDOS_PREANO;
              MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA,
                                           UN_ACCION   => 'M', 
                                           UN_CAMPOS   => MI_CAMPOS, 
                                           UN_CONDICION=> MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INDICFACTURADOS
                                    ,UN_TABLAERROR => MI_TABLA);
      END;
      IF MI_MANEXCVIGANT='SI' THEN 
        BEGIN
            BEGIN
                MI_TABLA := 'IP_PAGOSDOBLES';
                MI_CAMPOS:='PAGO = -1, DATE_MODIFIED = SYSDATE, MODIFIED_BY = ''' || UN_USER || '''';
                MI_CONDICION := 'COMPANIA       ='''||UN_COMPANIA         ||''' 
                                AND PRECOD      ='''||UN_PREDIO           ||''' 
                                AND NUMERO_ORDEN='''||UN_NUMERO_ORDEN     ||''' 
                                AND PREANO     <=  '||MI_RSACUERDOS_PREANO||' 
                                AND PAGO IN (0)';
                MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA,
                                             UN_ACCION   => 'M', 
                                             UN_CAMPOS   => MI_CAMPOS,
                                             UN_CONDICION=> MI_CONDICION);   
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                      ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTUPAGOSDOBLES
                                      ,UN_TABLAERROR => MI_TABLA);  
        END;                                           
      ELSE
        BEGIN
            BEGIN
                MI_TABLA := 'IP_PAGOSDOBLES';
                MI_CAMPOS:='PAGO = -1, DATE_MODIFIED = SYSDATE, MODIFIED_BY = ''' || UN_USER || '''';
                MI_CONDICION := 'COMPANIA         ='''||UN_COMPANIA     ||''' 
                                 AND PRECOD       ='''||UN_PREDIO       ||''' 
                                 AND NUMERO_ORDEN ='''||UN_NUMERO_ORDEN ||''' 
                                 AND PREANO BETWEEN '||MI_RSACUERDOS_PREANOI||' AND '||MI_RSACUERDOS_PREANO||' 
                                 AND PAGO IN (0)';
                MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA, 
                                             UN_ACCION   => 'M', 
                                             UN_CAMPOS   => MI_CAMPOS,
                                             UN_CONDICION=> MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                      ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTUPAGOSDOBLES
                                      ,UN_TABLAERROR => MI_TABLA);  
        END;                       
      END IF;


    ELSIF MI_CANCELADAS <> -1 AND MI_CANCELADAS <> MI_RSACUERDOS_NCUOTAS THEN

    -- BEGIN usado para control de errores al realizar la actualizacion en la tabla IP_USUARIOS_PREDIAL
    BEGIN
        BEGIN
            MI_TABLA := 'IP_USUARIOS_PREDIAL';
            MI_CAMPOS    := 'PAGO_ANO2 = NVL(PAGO_ANO1,0), 
                             PAG_VAL2 = NVL(PAG_VAL1,0), PAG_FEC2 = PAG_FEC1, 
                             NUM_COM2 = NUM_COM1, PAGO_ANO1 = NVL(PAGO_ANO,0), 
                             PAG_VAL1 = NVL(PAG_VAL,0), PAG_FEC1 = PAG_FEC, 
                             NUM_COM1 = NUM_COM,
                             PAG_VAL = '||UN_VALOR_RECIBO||', 
                             PAG_FEC=TO_DATE('''|| UN_FECHA_RECAUDO||''',''DD/MM/YYYY''), 
                             NUM_COM=''' || UN_RECIBO || ''',  PAGO_ACUERDO = -1, 
                             DATE_MODIFIED = SYSDATE, MODIFIED_BY = ''' || UN_USER || ''',
                             PAG_BAN=''' || UN_BANCO  || '''';
            MI_CONDICION := 'COMPANIA        ='''||UN_COMPANIA    ||''' 
                             AND CODIGO      ='''||UN_PREDIO      ||''' 
                             AND NUMERO_ORDEN='''||UN_NUMERO_ORDEN||'''';
            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA,
                                         UN_ACCION   => 'M',
                                         UN_CAMPOS   => MI_CAMPOS,
                                         UN_CONDICION=> MI_CONDICION);  
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
         MI_REEMPLAZOS(0).CLAVE:= 'FACTURA';
         MI_REEMPLAZOS(0).VALOR:= UN_RECIBO;
         MI_REEMPLAZOS(1).CLAVE:= 'PREDIO';
         MI_REEMPLAZOS(1).VALOR:= UN_PREDIO;
         PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTFACTURADEPRE
                                   ,UN_TABLAERROR => MI_TABLA
                                   ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;
    END IF;

  END LOOP REVISARECAUDO;
END; 
END PR_REGISTRAR_RECAUDO_ACUERDO;

--10

PROCEDURE PR_REGISTRAR_REC_ABONO_ACUERDO
  /*
  NAME              : PR_REGISTRAR_REC_ABONO_ACUERDO En Access --> RegistrarReciboAbonoAAcuerdo
  AUTHORS           : SYSMAN  SAS 
  AUTHOR MIGRACION  : NICOLÁS GÓMEZ BARBOSA
  DATE MIGRADOR     : 12/08/2016
  TIME              : 08:00 AM
  SOURCE MODULE     : PREDIAL
  DESCRIPTION       : Registrar recibo abono acuerdo
  MODIFIER          : Leydi Milena Cortés Forero
  DATE MODIFIED     : 22/08/2017
  TIME              : 12:49 PM
  MODIFICATIONS     : Se incluyen los campos de auditoría para las actualizaciones de tablas.
  @NAME:  registrarReciboAbonoAAcuerdo
  @METHOD:  POST  
  */
  (
  -- Parametro que recibe el numero de la compania
  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe el numero del recibo 
  UN_RECIBO                  IN PCK_SUBTIPOS.TI_DOCNUM,
  -- Parametro que recibe el numero del predio que realiza el abono
  UN_PREDIO                  IN PCK_SUBTIPOS.TI_CODPREDIO,
  -- Parametro que recibe el numero del banco en el que se realiza el abono
  UN_BANCO                   IN IP_BANCOS.CODIGOBANCO%TYPE,
  -- Parametro que recibe la fecha en la que se realiza el abono
  UN_FECHA_RECAUDO           IN VARCHAR2,
  -- Parametro que recibe el numero de orden 
  UN_NUMERO_ORDEN            IN PCK_SUBTIPOS.TI_NUMORDEN,
  -- Parametro que recibe el numero del modulo
  UN_MODULO                  IN PCK_SUBTIPOS.TI_MODULO,
  -- Parametro que recibe el nombre del usuario que realiza el abono
  UN_USER                    IN PCK_SUBTIPOS.TI_USUARIO,
  -- Parametro que recibe el valor total del abono
  UN_TOTAL_RECIBO            IN PCK_SUBTIPOS.TI_DOBLE,

  UN_PAQUETE                 IN IP_RECIBOS_DE_PAGO.PAQUETEPAG%TYPE
  )
  AS
    -- Variable que almacenara la cadena con los campos a insertar o actualizar en la funcion
    MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
    -- Variable que almacenara la cadena de valores a ser insertados o actualizados en la funcion.
    MI_VALORES               PCK_SUBTIPOS.TI_VALORES;
    -- Variable que almacenara la cadena con la condicion para realizar la actualizacion
    MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;
    -- variable que almacenara el tipo de abono del acuerdo 
    MI_TIPOABONOAACUERDO     VARCHAR2(2 CHAR);
    -- Variable que almacenara el numero del consecutivo con el cual se va insertar el registro
    MI_CONSECUTIVO           PCK_SUBTIPOS.TI_DOBLE;
    -- Variable que almacenara la tabla que se actualiza en ese procedimiento
    MI_TABLA                 PCK_SUBTIPOS.TI_TABLA;
    -- Variable que almacenara los reemplazos para mostrar en el mensajes de error
    MI_REEMPLAZOS            PCK_SUBTIPOS.TI_CLAVEVALOR; 
    -- Variable que almacenara la cantidad de registros que se actualicen
    MI_RTA                   PCK_SUBTIPOS.TI_ENTERO_LARGO;

  BEGIN

  <<REVISAFACTACUERDOS>>
  FOR MI_RSFACACUERDOS IN ( SELECT CODIGOACUERDO, 
                                   CUOTA 
                              FROM IP_FACTURADOSACUERDOS 
                             WHERE COMPANIA = UN_COMPANIA
                               AND NUMERO_ORDEN = UN_NUMERO_ORDEN
                               AND PREDIO   = UN_PREDIO
                               AND DOCNUM   = UN_RECIBO )
  LOOP

   BEGIN

    SELECT TIPOABONOAACUERDO INTO MI_TIPOABONOAACUERDO
      FROM IP_RECIBOS_DE_PAGO
     WHERE COMPANIA = UN_COMPANIA
       AND DOCNUM   = UN_RECIBO;

    EXCEPTION WHEN NO_DATA_FOUND THEN
              MI_TIPOABONOAACUERDO:=NULL;
    END;

    IF MI_TIPOABONOAACUERDO IS NOT NULL THEN

      MI_CONSECUTIVO:=PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA   =>'IP_FACTURADOSACUERDOS_CPY',
                                                       UN_CRITERIO=>'COMPANIA         ='''||UN_COMPANIA                   ||''' 
                                                                     AND NUMERO_ORDEN ='''||UN_NUMERO_ORDEN               ||''' 
                                                                     AND CODIGOACUERDO='''||MI_RSFACACUERDOS.CODIGOACUERDO||''' 
                                                                     AND PREDIO='''||UN_PREDIO||'''',
                                                       UN_CAMPO   =>'CONSECUTIVO');
      <<REGPAGOACUERDO>>
	  FOR MI_RSFACAC IN ( SELECT DOCNUM, CUOTA, PAGADO, 
                               TOTAL, CAPITAL, INTERESES, 
                               INTERES_ACUERDO, INTERES_RECARGO,
                               FECHAFACTURADO, PAG_BAN, 
                               FECHAPAGO, PREANOI, PREANO,
                               C1, C2, C3, C4, C5, C6, C7, C8,
                               C9, C10, C11, C12, C13, C14, C15, 
                               C16, C17, C18, C19, C20
                          FROM IP_FACTURADOSACUERDOS 
                         WHERE COMPANIA = UN_COMPANIA
                           AND NUMERO_ORDEN = UN_NUMERO_ORDEN
                           AND PREDIO   = UN_PREDIO
                           AND DOCNUM   = UN_RECIBO
                         ORDER BY CUOTA)
      LOOP

      -- BEGIN usado para el control de errores al hacer la insercion en la tabla IP_FACTURADOSACUERDOS_CPY
      BEGIN
          BEGIN
              MI_TABLA    := 'IP_FACTURADOSACUERDOS_CPY';
              MI_CAMPOS   :='COMPANIA, NUMERO_ORDEN, CODIGOACUERDO, PREDIO, CONSECUTIVO, 
                             CUOTA, DOCNUM, PAGADO, C1, C2, C3, C4, C5, C6, C7, C8, C9, 
                             C10, C11, C12, C13, C14, C15, C16, C17, C18, C19, C20,
                             TOTAL, CAPITAL, INTERESES, INTERES_ACUERDO, INTERES_RECARGO, 
                             FECHAFACTURADO, PAG_BAN, FECHAPAGO, PREANOI, PREANO, 
                             DOCNUMGENERADOR, FECHAGENERACION, 
                             CREATED_BY, DATE_CREATED';        
              MI_VALORES  := ''''||UN_COMPANIA||''','''||UN_NUMERO_ORDEN||''',
                             ''' ||MI_RSFACACUERDOS.CODIGOACUERDO||''','''||UN_PREDIO||''',
                             '||MI_CONSECUTIVO||','||MI_RSFACAC.CUOTA||','''||MI_RSFACAC.DOCNUM||''',
                             '||MI_RSFACAC.PAGADO||','||MI_RSFACAC.C1||','||MI_RSFACAC.C2||',
                             '||MI_RSFACAC.C3||','||MI_RSFACAC.C4||','||MI_RSFACAC.C5||',
                             '||MI_RSFACAC.C6||','||MI_RSFACAC.C7||','||MI_RSFACAC.C8||',
                             '||MI_RSFACAC.C9||','||MI_RSFACAC.C10||','||MI_RSFACAC.C11||',
                             '||MI_RSFACAC.C12||','||MI_RSFACAC.C13||','||MI_RSFACAC.C14||',
                             '||MI_RSFACAC.C15||','||MI_RSFACAC.C16||','||MI_RSFACAC.C17||',
                             '||MI_RSFACAC.C18||','||MI_RSFACAC.C19||','||MI_RSFACAC.C20||'
                            ,'||MI_RSFACAC.TOTAL||','||MI_RSFACAC.CAPITAL||','||MI_RSFACAC.INTERESES||',
                            '||MI_RSFACAC.INTERES_ACUERDO||','||MI_RSFACAC.INTERES_RECARGO||',
                            '||CASE WHEN MI_RSFACAC.FECHAFACTURADO IS NULL 
                               THEN 'NULL' ELSE PCK_SYSMAN_UTL.FC_SDATE(MI_RSFACAC.FECHAFACTURADO) END||',
                               '''||NVL(MI_RSFACAC.PAG_BAN,'')||''','||
                               CASE WHEN MI_RSFACAC.FECHAPAGO IS NULL THEN 'NULL' ELSE PCK_SYSMAN_UTL.FC_SDATE(MI_RSFACAC.FECHAPAGO) END||',
                            '||MI_RSFACAC.PREANOI||','||MI_RSFACAC.PREANO||','''||UN_RECIBO||''', 
                            TO_DATE('''||UN_FECHA_RECAUDO||''',''DD/MM/YYYY''), ''' || 
                            UN_USER || ''', SYSDATE' ;
              MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA  => MI_TABLA,
                                                     UN_ACCION => 'I',
                                                     UN_CAMPOS => MI_CAMPOS,
                                                     UN_VALORES=> MI_VALORES);
              MI_CONSECUTIVO:=MI_CONSECUTIVO+1;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END ;     
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                      ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INSERCACUERDCPY
                                      ,UN_TABLAERROR => MI_TABLA);
      END;
      END LOOP REGPAGOACUERDO;

      -- BEGIN usado para el control de errores al realizar la actualizacion en la tabla IP_RECIBOS_DE_PAGO
      BEGIN
          BEGIN
              MI_TABLA := 'IP_RECIBOS_DE_PAGO';
              MI_CAMPOS:='PAGO = -1, PAG_BANPAG='''||UN_BANCO||''', 
                          PREFECPAG=TO_DATE('''||UN_FECHA_RECAUDO||''',''DD/MM/YYYY''), 
                          PAQUETEPAG='''||UN_PAQUETE||''', FECHA_REGRECAUDO=SYSDATE, 
                          USUARIO_REGRECAUDO='''||UN_USER||''', ' ||
                          'MODIFIED_BY    = ''' || UN_USER || ''', ' || 'DATE_MODIFIED  = SYSDATE';
              MI_CONDICION := 'COMPANIA='''||UN_COMPANIA||''' AND DOCNUM='''||UN_RECIBO||'''';
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA,
                                                     UN_ACCION   => 'M',
                                                     UN_CAMPOS   => MI_CAMPOS,
                                                     UN_CONDICION=> MI_CONDICION); 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
      --CalcularNuevoAcuerdo ACUERDO, CodPredio, CUOTA, TotalRecibo, rsRecibos!tipoabonoaacuerdo (Ya no aplica)
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTURECIBOSPAGO
                                    ,UN_TABLAERROR => MI_TABLA);
      END;
    ELSE 
      EXIT;
    END IF;

    -- BEGIN usado para actualizar la tabla IP_FACTURADOSACUERDOS
    BEGIN
        BEGIN
            MI_TABLA := 'IP_FACTURADOSACUERDOS';
            MI_CAMPOS:= 'PAGADO = -1, PAG_BAN='''||UN_BANCO||''', 
                         FECHAPAGO=TO_DATE('''||UN_FECHA_RECAUDO||''',''DD/MM/YYYY''), ' ||
                        'MODIFIED_BY    = ''' || UN_USER || ''', ' || 'DATE_MODIFIED  = SYSDATE';
            MI_CONDICION := 'COMPANIA         ='''||UN_COMPANIA                   ||''' 
                             AND PREDIO       ='''||UN_PREDIO                     ||''' 
                             AND NUMERO_ORDEN ='''||UN_NUMERO_ORDEN               ||''' 
                             AND CODIGOACUERDO='''||MI_RSFACACUERDOS.CODIGOACUERDO||''' 
                             AND CUOTA        ='  ||MI_RSFACACUERDOS.CUOTA;
            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>MI_TABLA,
                                         UN_ACCION   =>'M', 
                                         UN_CAMPOS   =>MI_CAMPOS,
                                         UN_CONDICION=>MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;                                                       
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACUERDORECAUDO
                                  ,UN_TABLAERROR => MI_TABLA);
    END;

  END LOOP REVISAFACTACUERDOS;
END PR_REGISTRAR_REC_ABONO_ACUERDO;

--11

PROCEDURE PR_REGISTRAR_RECAUDO_UNICO_ANO
  /*
  NAME              : PR_REGISTRAR_RECAUDO_UNICO_ANO En Access --> RegistrarRecaudoUnicoAno
  AUTHORS           : SYSMAN  SAS 
  AUTHOR MIGRACION  : NICOLÁS GÓMEZ BARBOSA
  DATE MIGRADOR     : 12/08/2016
  TIME              : 10:00 AM
  SOURCE MODULE     : PREDIAL
  DESCRIPTION       : Registrar recaudo único año
  MODIFIER          : Leydi Milena Cortés Forero
  DATE MODIFIED     : 22/08/2017
  TIME              : 10:30 AM
  MODIFICATIONS     : Se incluyen los campos de auditoría en cada operación y si no existen vigencias anteriores a la vigencia evaluada
                      sin pago, se actualiza la tabla IP_USUARIOS_PREDIAL.
  @NAME:  registrarRecaudoUnicoVigencia
  @METHOD:  PUT    
  */
  (
  -- Parametro que recibe el numero de la compania
  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe el numero del recibo del recaudo
  UN_RECIBO                  IN PCK_SUBTIPOS.TI_DOCNUM,
  -- Parametro que recibe el numero del predio
  UN_PREDIO                  IN PCK_SUBTIPOS.TI_CODPREDIO,
  -- Parametro que recibe el numero del banco en el que se realiza el recaudo
  UN_BANCO                   IN IP_BANCOS.CODIGOBANCO%TYPE,
  -- Parametro que recibe la fecha en la que se realiza el recaudo
  UN_FECHA_RECAUDO           IN VARCHAR2,
  -- Parametro que recibe el numero de la orden 
  UN_NUMERO_ORDEN            IN PCK_SUBTIPOS.TI_NUMORDEN,
  -- Parametro que recibe el numero del modulo
  UN_MODULO                  IN PCK_SUBTIPOS.TI_MODULO,
  -- Parametro que recibe el valor del recibo a cancelar 
  UN_VALOR_RECIBO            IN PCK_SUBTIPOS.TI_DOBLE,
  -- Parametro que recibe la vigencia del recaudo
  UN_VIGENCIA                IN PCK_SUBTIPOS.TI_ANIO,
  -- Código del usuario que realiza el registro.
  UN_USUARIO                 IN PCK_SUBTIPOS.TI_USUARIO

  )
  AS
    -- Variable que almacena la cadena de campos que se van a actualizar
    MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
    -- Variable que almacena la cadena con la condicion de la actualizacion a realizar
    MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;
    -- Variable que almacena el valor del retorno del parametro MANEJA EXCEDENTES DE VIG. ANTERIORES
    MI_MANEXCVIGANT          PCK_SUBTIPOS.TI_PARAMETRO;
    -- Variable que almacena el número de años anteriores a la vigencia actual que no están pagos.
    MI_NUMVIGSINPAGO         PCK_SUBTIPOS.TI_ENTERO;
    -- Variable que almacenara el numero de registros actualizados
    MI_RTA                   PCK_SUBTIPOS.TI_ENTERO_LARGO;
    -- Variable que almacena el nombre de la tabla a actualizar
    MI_TABLA                 PCK_SUBTIPOS.TI_TABLA;
    -- Variable que almacena los reemplazos en el mensaje de error
    MI_REEMPLAZOS            PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN

    MI_MANEXCVIGANT:=NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA =>UN_COMPANIA,
                                               UN_NOMBRE   =>'MANEJA EXCEDENTES DE VIG. ANTERIORES',
                                               UN_MODULO   =>UN_MODULO,
                                               UN_FECHA_PAR=>SYSDATE
                                               ),'NO');
    -- BEGIN usado para el control de errores
      BEGIN 
          BEGIN    
              MI_TABLA := 'IP_FACTURADOS';
              MI_CAMPOS:='PAGADO   = -1, DOCNUM='''||UN_RECIBO||''', 
                          PAG_BAN  ='''||UN_BANCO       ||''', 
                          PREVAL   ='  ||UN_VALOR_RECIBO||', 
                          FECHAPAGO=TO_DATE('''||UN_FECHA_RECAUDO||''',''DD/MM/YYYY''), ' ||
                         'MODIFIED_BY   = ''' || UN_USUARIO || ''', 
                          DATE_MODIFIED = SYSDATE';
              MI_CONDICION := 'COMPANIA='''||UN_COMPANIA||
                              ''' AND CODIGO='''||UN_PREDIO||
                              ''' AND NUMERO_ORDEN='''||UN_NUMERO_ORDEN||
                              ''' AND PREANO = '||UN_VIGENCIA;
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOS', 
                                                     UN_ACCION   =>'M',
                                                     UN_CAMPOS   =>MI_CAMPOS, 
                                                     UN_CONDICION=>MI_CONDICION
                                                     );
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END ; 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INDICFACTURADOS
                                    ,UN_TABLAERROR => MI_TABLA);     
      END;
      IF MI_MANEXCVIGANT='SI' THEN 
          BEGIN
              BEGIN
                  MI_TABLA := 'IP_PAGOSDOBLES';
                  MI_CAMPOS:='PAGO = -1, MODIFIED_BY   = ''' || UN_USUARIO || ''', ' || 
                             'DATE_MODIFIED = SYSDATE';
                  MI_CONDICION := 'COMPANIA='''||UN_COMPANIA||
                                  ''' AND PRECOD='''||UN_PREDIO||
                                  ''' AND NUMERO_ORDEN='''||UN_NUMERO_ORDEN||
                                  ''' AND PREANO<='||UN_VIGENCIA||
                                  ' AND PAGO IN (0)';
                  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>MI_TABLA, 
                                               UN_ACCION   =>'M',
                                               UN_CAMPOS   =>MI_CAMPOS, 
                                               UN_CONDICION=>MI_CONDICION);   
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
              PCK_ERR_MSG.RAISE_WITH_MSG(  UN_EXC_COD    => SQLCODE
                                          ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTUPAGOSDOBLES
                                          ,UN_TABLAERROR => MI_TABLA);  
          END;
      ELSE
          BEGIN
              BEGIN
                  MI_TABLA := 'IP_PAGOSDOBLES';
                  MI_CAMPOS:='PAGO = -1, MODIFIED_BY   = ''' || UN_USUARIO || ''', ' || 
                             'DATE_MODIFIED = SYSDATE';
                  MI_CONDICION := 'COMPANIA='''||UN_COMPANIA||
                                ''' AND PRECOD='''||UN_PREDIO||
                                ''' AND NUMERO_ORDEN='''||UN_NUMERO_ORDEN||
                                ''' AND PREANO='||UN_VIGENCIA||
                                ' AND PAGO IN (0)';
                  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA, 
                                               UN_ACCION   => 'M', 
                                               UN_CAMPOS   => MI_CAMPOS,
                                               UN_CONDICION=> MI_CONDICION ); 
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
              PCK_ERR_MSG.RAISE_WITH_MSG(  UN_EXC_COD    => SQLCODE
                                          ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTUPAGOSDOBLES
                                          ,UN_TABLAERROR => MI_TABLA);  
          END;
      END IF;

      SELECT COUNT(CODIGO) VIGSINPAGO
        INTO MI_NUMVIGSINPAGO
        FROM IP_FACTURADOS
       WHERE COMPANIA = UN_COMPANIA
         AND CODIGO = UN_PREDIO
         AND PAGADO IN (0)
         AND PREANO <= UN_VIGENCIA;

      IF MI_NUMVIGSINPAGO IN (0)
      THEN
          BEGIN
              BEGIN
                  MI_TABLA := 'IP_USUARIOS_PREDIAL';
                  MI_CAMPOS    := 'PAGO_ANO       = ' || UN_VIGENCIA || ', ' ||
                                  'PAG_FEC        = TO_DATE(''' || UN_FECHA_RECAUDO || ''', ''DD/MM/YYYY HH24:mi:ss''), ' || 
                                  'PAG_BAN        = ''' || UN_BANCO || ''', ' ||  
                                  'PAG_VAL        = ' || UN_VALOR_RECIBO || ', ' ||
                                  'NUM_COM        = ''' || UN_RECIBO || ''', ' || 
                                  'MODIFIED_BY    = ''' || UN_USUARIO || ''', ' || 
                                  'DATE_MODIFIED  = SYSDATE';
                  MI_CONDICION := ' COMPANIA = ''' || UN_COMPANIA || 
                              ''' AND CODIGO = ''' || UN_PREDIO || 
                              ''' AND NUMERO_ORDEN = ''' || UN_NUMERO_ORDEN || '''';
                  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'IP_USUARIOS_PREDIAL', 
                                                         UN_ACCION   => 'M', 
                                                         UN_CAMPOS   => MI_CAMPOS,
                                                         UN_CONDICION=> MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END;                                           
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
              MI_REEMPLAZOS(0).CLAVE:= 'FACTURA';
              MI_REEMPLAZOS(0).VALOR:= UN_RECIBO;
              MI_REEMPLAZOS(1).CLAVE:= 'PREDIO';
              MI_REEMPLAZOS(1).VALOR:= UN_PREDIO;
              PCK_ERR_MSG.RAISE_WITH_MSG(    
                                UN_EXC_COD    => SQLCODE
                               ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTFACTURADEPRE
                               ,UN_TABLAERROR => MI_TABLA
                               ,UN_REEMPLAZOS => MI_REEMPLAZOS); 
          END;
      END IF;

END PR_REGISTRAR_RECAUDO_UNICO_ANO;

--12
PROCEDURE PR_REGISTRAR_RECAUDO_VIGENCIAS
  /*
  NAME              : PR_REGISTRAR_RECAUDO_VIGENCIAS En Access --> RegistrarRecaudoVigencias
  AUTHORS           : SYSMAN  SAS 
  AUTHOR MIGRACION  : NICOLÁS GÓMEZ BARBOSA
  DATE MIGRADOR     : 12/08/2016
  TIME              : 11:00 AM
  SOURCE MODULE     : PREDIAL
  DESCRIPTION       : Registrar recaudo vigencias
  @NAME:  registrarRecaudoEnVigencia
  MODIFIER          : Leydi Milena Cortés Forero
  DATE MODIFIED     : 22/08/2017
  TIME              : 12:55 PM
  MODIFICATIONS     : Se incluyen los campos de auditoría para las actualizaciones de tablas.
  @METHOD:  PUT
  */
  (
  -- Parametro que recibe el numero de la compania
  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe el numero del recibo del recaudo
  UN_RECIBO                  IN PCK_SUBTIPOS.TI_DOCNUM,
  -- Parametro que recibe le numero del predio que va a realizar el recaudo
  UN_PREDIO                  IN PCK_SUBTIPOS.TI_CODPREDIO,
  -- Parametro que recibe el numero del banco en donde se hace el recaudo
  UN_BANCO                   IN IP_BANCOS.CODIGOBANCO%TYPE,
  -- Parametro que recibe la fecha en la que se realiza el recaudo
  UN_FECHA_RECAUDO           IN VARCHAR2,
  -- Parametro que recibe el numero de orden 
  UN_NUMERO_ORDEN            IN PCK_SUBTIPOS.TI_NUMORDEN,
  -- Parametro que recibe el valor a cancelar en el recaudo
  UN_VALOR_RECIBO            IN PCK_SUBTIPOS.TI_DOBLE,
  -- Parametro que recibe la vigencia del recaudo
  UN_VIGENCIA_FINAL          IN PCK_SUBTIPOS.TI_ANIO,
  -- Código del usuario que realiza el registro.
  UN_USUARIO                 IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    -- Variable que almacenara la cadena de campos a actualizar
    MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
    -- Variable que almacenara la cadena con la condicion usada en la actualizacion
    MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;

    MI_ERROR_FUN             PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 1;   
  BEGIN
  -- BEGIN usado para control de errores
  BEGIN
  MI_CAMPOS:='PAGADO = -1, DOCNUM='''||UN_RECIBO||''', PAG_BAN='''||UN_BANCO||''', PREVAL='||UN_VALOR_RECIBO||
             ', FECHAPAGO=TO_DATE('''||UN_FECHA_RECAUDO||''',''DD/MM/YYYY''), ' ||
             'MODIFIED_BY    = ''' || UN_USUARIO || ''', ' || 'DATE_MODIFIED  = SYSDATE';
  MI_CONDICION := 'COMPANIA='''||UN_COMPANIA||''' AND CODIGO='''||UN_PREDIO||''' AND NUMERO_ORDEN='''||UN_NUMERO_ORDEN||''' AND PREANO <= '||UN_VIGENCIA_FINAL||' AND PAGADO IN (0)';
  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOS', 
                                         UN_ACCION   =>'M', 
                                         UN_CAMPOS   =>MI_CAMPOS, 
                                         UN_CONDICION=>MI_CONDICION
                                        );

  MI_CAMPOS:='PAGO = -1, MODIFIED_BY    = ''' || UN_USUARIO || ''', ' || 'DATE_MODIFIED  = SYSDATE';
  MI_CONDICION := 'COMPANIA='''||UN_COMPANIA||''' AND PRECOD='''||UN_PREDIO||''' AND NUMERO_ORDEN='''||UN_NUMERO_ORDEN||''' AND PREANO<='||UN_VIGENCIA_FINAL||' AND PAGO IN (0)';
  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_PAGOSDOBLES',
                                         UN_ACCION   =>'M', 
                                         UN_CAMPOS   =>MI_CAMPOS,
                                         UN_CONDICION=>MI_CONDICION
                                         );   

  MI_CAMPOS:='PAGO_ANO2 = NVL(PAGO_ANO1,0), PAG_VAL2 = NVL(PAG_VAL1,0), PAG_FEC2 = PAG_FEC1, NUM_COM2 = NUM_COM1, 
              PAGO_ANO1 = NVL(PAGO_ANO,0), PAG_VAL1 = NVL(PAG_VAL,0), PAG_FEC1 = PAG_FEC, NUM_COM1 = NUM_COM 
              ,PAG_VAL = '||UN_VALOR_RECIBO||', PAG_FEC=TO_DATE('''||UN_FECHA_RECAUDO||''',''DD/MM/YYYY''), NUM_COM='''||UN_RECIBO||
              ''',  PAGO_ACUERDO = -1, PAG_BAN='''||UN_BANCO||''', PAGO_ANO='||UN_VIGENCIA_FINAL || 
              ', MODIFIED_BY    = ''' || UN_USUARIO || ''', ' || 'DATE_MODIFIED  = SYSDATE';
  MI_CONDICION := 'COMPANIA='''||UN_COMPANIA||''' AND CODIGO='''||UN_PREDIO||''' AND NUMERO_ORDEN='''||UN_NUMERO_ORDEN||'''';
  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_USUARIOS_PREDIAL', 
                                         UN_ACCION   =>'M', 
                                         UN_CAMPOS   =>MI_CAMPOS, 
                                         UN_CONDICION=>MI_CONDICION
                                         );  

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
           RAISE PCK_EXCEPCIONES.EXC_PREDIAL; 
             END ; 

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_RECAUDOVIGENCIAS
  ); 

END PR_REGISTRAR_RECAUDO_VIGENCIAS;

--13

PROCEDURE PR_REGISTRAR_RECAUDO_UNICOANO
  /*
  NAME              : PR_REGISTRAR_RECAUDO_UNICOANO 
  AUTHORS           : SYSMAN  SAS 
  AUTHOR MIGRACION  : NICOLÁS GÓMEZ BARBOSA
  DATE MIGRADOR     : 30/08/2016
  TIME              : 10:00 AM
  SOURCE MODULE     : PREDIAL
  DESCRIPTION       : Registrar recaudo único año
  @NAME:  registrarRecaudoUnicoVigencia
  @METHOD:  PUT  
  */
  (
  -- Parametro que recibe el numero de la compania
  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe el numero del recibo
  UN_RECIBO                  IN PCK_SUBTIPOS.TI_DOCNUM,
  -- Parametro que recibe el numero del predio  
  UN_PREDIO                  IN PCK_SUBTIPOS.TI_CODPREDIO,
  -- Parametro que recibe el numero del banco 
  UN_BANCO                   IN IP_BANCOS.CODIGOBANCO%TYPE,
  -- Parametro que recibe la fecha en la que se realiza el recaudo
  UN_FECHA_RECAUDO           IN VARCHAR2,
  -- Parametro que recibe el numero de orden 
  UN_NUMERO_ORDEN            IN PCK_SUBTIPOS.TI_NUMORDEN,
  -- Parametro que recibe el numero del modulo 
  UN_MODULO                  IN PCK_SUBTIPOS.TI_MODULO,
  -- Parametro que recibe el valor del recaudo
  UN_VALOR_RECIBO            IN PCK_SUBTIPOS.TI_DOBLE,
  -- Parametro que recibe la vigencia del recaudo
  UN_VIGENCIA                IN PCK_SUBTIPOS.TI_ANIO,

  UN_PAQUETE                 IN IP_RECIBOS_DE_PAGO.PAQUETEPAG%TYPE,
  -- Parametro que recibe el nombre del usuario 
  UN_USER                    IN PCK_SUBTIPOS.TI_USUARIO

  )
  AS
    -- Variable que almacena la cadena con los campos que se van a actualizar
    MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
    -- Variable que almacena la cadena con la condicion usada al hacer las actualizaciones
    MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;
    -- Variable que almacenara el valor que retorna el parametro MANEJA EXCEDENTES DE VIG. ANTERIORES
    MI_MANEXCVIGANT          PCK_SUBTIPOS.TI_PARAMETRO;
    -- Variable que almacena el número de años anteriores a la vigencia actual que no están pagos.
    MI_NUMVIGSINPAGO         PCK_SUBTIPOS.TI_ENTERO;
     -- Variable que almacenara el numero de registros actualizados
    MI_RTA                   PCK_SUBTIPOS.TI_ENTERO_LARGO;
    -- Variable que almacena el nombre de la tabla a actualizar
    MI_TABLA                 PCK_SUBTIPOS.TI_TABLA;
    -- Variable que almacena los reemplazos en el mensaje de error
    MI_REEMPLAZOS            PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN

  MI_MANEXCVIGANT:=NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA =>UN_COMPANIA, 
                                             UN_NOMBRE   =>'MANEJA EXCEDENTES DE VIG. ANTERIORES',
                                             UN_MODULO   =>UN_MODULO,
                                             UN_FECHA_PAR=>SYSDATE
                                             ),'NO');
  -- BEGIN usado para el control de errores
    BEGIN 
        BEGIN  
            MI_TABLA := 'IP_FACTURADOS';
            MI_CAMPOS:='PAGADO = -1, DOCNUM='''||UN_RECIBO||''', 
                        PAG_BAN='''||UN_BANCO||''', PREVAL='||UN_VALOR_RECIBO||',
                        DATE_MODIFIED = SYSDATE,
                        MODIFIED_BY   = ''' || UN_USER || ''', 
                        FECHAPAGO=TO_DATE('''||UN_FECHA_RECAUDO||''',''DD/MM/YYYY'')';
            MI_CONDICION := 'COMPANIA       ='''||UN_COMPANIA     ||''' 
                            AND CODIGO      ='''||UN_PREDIO       ||''' 
                            AND NUMERO_ORDEN='''||UN_NUMERO_ORDEN ||''' 
                            AND PREANO      = ' ||UN_VIGENCIA;
            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>MI_TABLA, 
                                                   UN_ACCION   =>'M',
                                                   UN_CAMPOS   =>MI_CAMPOS,
                                                   UN_CONDICION=>MI_CONDICION
                                                   );
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END ; 
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INDICFACTURADOS
                                  ,UN_TABLAERROR => MI_TABLA);     
    END;
  IF MI_MANEXCVIGANT='SI' THEN 
      BEGIN
          BEGIN
              MI_TABLA := 'IP_PAGOSDOBLES';
              MI_CAMPOS:='PAGO = -1, MODIFIED_BY   = ''' || UN_USER || ''', ' || 
                         'DATE_MODIFIED = SYSDATE';
              MI_CONDICION := 'COMPANIA       ='''||UN_COMPANIA    ||''' 
                              AND PRECOD      ='''||UN_PREDIO      ||''' 
                              AND NUMERO_ORDEN='''||UN_NUMERO_ORDEN||''' 
                              AND PREANO     <='  ||UN_VIGENCIA||' 
                              AND PAGO IN (0)';
              MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_PAGOSDOBLES', 
                                                     UN_ACCION   =>'M',
                                                     UN_CAMPOS   =>MI_CAMPOS,
                                                     UN_CONDICION=>MI_CONDICION
                                                     );  
     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
              PCK_ERR_MSG.RAISE_WITH_MSG(  UN_EXC_COD    => SQLCODE
                                          ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTUPAGOSDOBLES
                                          ,UN_TABLAERROR => MI_TABLA);  
          END;                                      
  ELSE
      BEGIN
          BEGIN
              MI_TABLA := 'IP_PAGOSDOBLES';
              MI_CAMPOS:='PAGO = -1, MODIFIED_BY   = ''' || UN_USER || ''', ' || 
                         'DATE_MODIFIED = SYSDATE';
              MI_CONDICION := 'COMPANIA         ='''||UN_COMPANIA     ||''' 
                               AND PRECOD       ='''||UN_PREDIO       ||''' 
                               AND NUMERO_ORDEN ='''||UN_NUMERO_ORDEN ||''' 
                               AND PREANO       ='  ||UN_VIGENCIA     ||' 
                               AND PAGO IN (0)';
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA, 
                                                     UN_ACCION   => 'M',
                                                     UN_CAMPOS   => MI_CAMPOS,
                                                     UN_CONDICION=> MI_CONDICION
                                                     ); 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(  UN_EXC_COD    => SQLCODE
                                      ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTUPAGOSDOBLES
                                      ,UN_TABLAERROR => MI_TABLA);  
      END;                                     
  END IF;

  BEGIN
      BEGIN
          MI_CAMPOS:='PAGO = -1, PAG_BANPAG='''||UN_BANCO||''', 
                      PREFECPAG=TO_DATE('''||UN_FECHA_RECAUDO||''',''DD/MM/YYYY''), 
                      PAQUETEPAG='''||UN_PAQUETE||''', FECHA_REGRECAUDO=SYSDATE, 
                      DATE_MODIFIED = SYSDATE, MODIFIED_BY   = ''' || UN_USER || ''',
                      USUARIO_REGRECAUDO='''||UN_USER||'''';
          MI_CONDICION := 'COMPANIA='''||UN_COMPANIA||''' AND DOCNUM='''||UN_RECIBO||'''';
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_RECIBOS_DE_PAGO', 
                                                 UN_ACCION   =>'M', 
                                                 UN_CAMPOS   =>MI_CAMPOS, 
                                                 UN_CONDICION=>MI_CONDICION
                                                 ); 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(  UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTUPAGOSDOBLES
                                  ,UN_TABLAERROR => MI_TABLA);  
  END;
  SELECT COUNT(CODIGO) VIGSINPAGO
        INTO MI_NUMVIGSINPAGO
        FROM IP_FACTURADOS
       WHERE COMPANIA = UN_COMPANIA
         AND CODIGO = UN_PREDIO
         AND PAGADO IN (0)
         AND PREANO <= UN_VIGENCIA;

      IF MI_NUMVIGSINPAGO IN (0)
      THEN
          BEGIN
              BEGIN
                  MI_TABLA := 'IP_USUARIOS_PREDIAL';
                  MI_CAMPOS    := 'PAGO_ANO       = ' || UN_VIGENCIA || ', ' ||
                                  'PAG_FEC        = TO_DATE(''' || UN_FECHA_RECAUDO || ''', ''DD/MM/YYYY HH24:mi:ss''), ' || 
                                  'PAG_BAN        = ''' || UN_BANCO || ''', ' ||  
                                  'PAG_VAL        = ' || UN_VALOR_RECIBO || ', ' ||
                                  'NUM_COM        = ''' || UN_RECIBO || ''', ' || 
                                  'MODIFIED_BY    = ''' || UN_USER || ''', ' || 
                                  'DATE_MODIFIED  = SYSDATE';
                  MI_CONDICION := ' COMPANIA = ''' || UN_COMPANIA || 
                              ''' AND CODIGO = ''' || UN_PREDIO || 
                              ''' AND NUMERO_ORDEN = ''' || UN_NUMERO_ORDEN || '''';
                  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'IP_USUARIOS_PREDIAL', 
                                                         UN_ACCION   => 'M', 
                                                         UN_CAMPOS   => MI_CAMPOS,
                                                         UN_CONDICION=> MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END;                                           
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
              MI_REEMPLAZOS(0).CLAVE:= 'FACTURA';
              MI_REEMPLAZOS(0).VALOR:= UN_RECIBO;
              MI_REEMPLAZOS(1).CLAVE:= 'PREDIO';
              MI_REEMPLAZOS(1).VALOR:= UN_PREDIO;
              PCK_ERR_MSG.RAISE_WITH_MSG(    
                                UN_EXC_COD    => SQLCODE
                               ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTFACTURADEPRE
                               ,UN_TABLAERROR => MI_TABLA
                               ,UN_REEMPLAZOS => MI_REEMPLAZOS); 
          END;
      END IF;

END PR_REGISTRAR_RECAUDO_UNICOANO;

--14

PROCEDURE PR_REGISTRAR_RECAUDO
  /*
  NAME              : PR_REGISTRAR_RECAUDO
  AUTHORS           : SYSMAN  SAS 
  AUTHOR MIGRACION  : NICOLÁS GÓMEZ BARBOSA
  DATE MIGRADOR     : 30/08/2016
  TIME              : 11:00 AM
  SOURCE MODULE     : PREDIAL
  DESCRIPTION       : Registrar recaudo vigencias
  @NAME:  registrarRecaudo
  @METHOD:  PUT    
  */
  (
  -- Parametro que recibe el nombre de la compania
  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe el numero de la compania
  UN_RECIBO                  IN PCK_SUBTIPOS.TI_DOCNUM,
  -- Parametro que recibe el numero de recibo del recaudo
  UN_PREDIO                  IN PCK_SUBTIPOS.TI_CODPREDIO,
  -- Parametro que recibe el numero del predio 
  UN_BANCO                   IN IP_BANCOS.CODIGOBANCO%TYPE,
  -- Parametro que recibe el numero del banco en donde se realiza el recaudo
  UN_FECHA_RECAUDO           IN VARCHAR2,
  -- Parametro que recibe la fecha del recaudo
  UN_NUMERO_ORDEN            IN PCK_SUBTIPOS.TI_NUMORDEN,
  -- Parametro que recibe el numero de orden 
  UN_VALOR_RECIBO            IN PCK_SUBTIPOS.TI_DOBLE,
  -- Parametro que recibe el valor a cancelar en el recaudo
  UN_VIGENCIA_FINAL          IN PCK_SUBTIPOS.TI_ANIO,
  -- Parametro que recibe la vigencia del recaudo
  UN_PAQUETE                 IN IP_RECIBOS_DE_PAGO.PAQUETEPAG%TYPE,
  -- Parametro que recibe el nombre del usuario 
  UN_USER                    IN PCK_SUBTIPOS.TI_USUARIO

  )
  AS
    -- Variable que almacenara los campos a ser actualizados en a funcion 
    MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
    -- Variable que almacenara la condicion usada al realizar las actualizaciones
    MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;
    -- Variable que almacenara la cantidad de registros que se actualicen
    MI_RTA                   PCK_SUBTIPOS.TI_ENTERO_LARGO;
    -- Variable que almacenara la tabla que se actualiza en ese procedimiento
    MI_TABLA                 PCK_SUBTIPOS.TI_TABLA;
    -- Variable que almacenara los reemplazos para mostrar en el mensajes de error
    MI_REEMPLAZOS            PCK_SUBTIPOS.TI_CLAVEVALOR; 
  BEGIN
  -- BEGIN usado para control de errores
      BEGIN
          BEGIN
              MI_TABLA := 'IP_FACTURADOS';
              MI_CAMPOS:='PAGADO  =   -1,         DOCNUM = ''' || UN_RECIBO       ||''',
                          PAG_BAN='''||UN_BANCO||''', PREVAL=' || UN_VALOR_RECIBO ||',
                          FECHAPAGO =      TO_DATE('''|| UN_FECHA_RECAUDO||''',''DD/MM/YYYY''), ' || 
                          '   MODIFIED_BY   = '''    || UN_USER         || 
                          ''',DATE_MODIFIED = SYSDATE';

              MI_CONDICION := '          COMPANIA ='''|| UN_COMPANIA     ||
                              '''      AND CODIGO ='''|| UN_PREDIO       ||
                              '''AND NUMERO_ORDEN ='''|| UN_NUMERO_ORDEN ||
                              '''      AND PREANO<='  ||UN_VIGENCIA_FINAL||
                              ' AND PAGADO IN (0)';
              MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA,
                                           UN_ACCION   => 'M',
                                           UN_CAMPOS   => MI_CAMPOS,
                                           UN_CONDICION=> MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INDICFACTURADOS
                                    ,UN_TABLAERROR => MI_TABLA);     
      END;
      BEGIN
          BEGIN
              MI_TABLA := 'IP_PAGOSDOBLES';
              MI_CAMPOS := 'PAGO = -1,  DATE_MODIFIED = SYSDATE,  MODIFIED_BY = ''' || UN_USER || '''';
              MI_CONDICION := '     COMPANIA =     ''' || UN_COMPANIA       ||
                              ''' AND PRECOD =     ''' || UN_PREDIO         ||
                              ''' AND NUMERO_ORDEN=''' ||UN_NUMERO_ORDEN    ||
                              ''' AND PREANO <=      ' || UN_VIGENCIA_FINAL ||
                              '   AND PAGO IN (0)';
              MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => MI_TABLA, 
                                          UN_ACCION   => 'M',
                                          UN_CAMPOS   => MI_CAMPOS,
                                          UN_CONDICION=> MI_CONDICION);   
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(  UN_EXC_COD    => SQLCODE
                                      ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTUPAGOSDOBLES
                                      ,UN_TABLAERROR => MI_TABLA);  
      END;
      BEGIN
          BEGIN
              MI_TABLA := 'IP_USUARIOS_PREDIAL';
              MI_CAMPOS:='PAGO_ANO2 = NVL(PAGO_ANO1,0), PAG_VAL2 = NVL(PAG_VAL1,0), 
                          PAG_FEC2 = PAG_FEC1, NUM_COM2 = NUM_COM1, 
                          PAGO_ANO1 = NVL(PAGO_ANO,0), PAG_VAL1 = NVL(PAG_VAL,0), 
                          PAG_FEC1 = PAG_FEC, NUM_COM1 = NUM_COM,  
                          PAG_VAL = '||UN_VALOR_RECIBO||', DATE_MODIFIED = SYSDATE, 
                          MODIFIED_BY   = '''    || UN_USER         || 
                          ''', PAG_FEC=TO_DATE('''|| UN_FECHA_RECAUDO||''',''DD/MM/YYYY''), 
                          NUM_COM=        '''|| UN_RECIBO       ||''',  PAGO_ACUERDO = -1, 
                          PAG_BAN=        '''||UN_BANCO         ||''', 
                          PAGO_ANO=         '||UN_VIGENCIA_FINAL;
              MI_CONDICION := '     COMPANIA =      ''' || UN_COMPANIA   || 
                              ''' AND CODIGO =      ''' || UN_PREDIO     ||
                              ''' AND NUMERO_ORDEN= ''' ||UN_NUMERO_ORDEN||'''';
              MI_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    => MI_TABLA,
                                            UN_ACCION   =>'M', 
                                            UN_CAMPOS   =>MI_CAMPOS,
                                            UN_CONDICION=>MI_CONDICION);  
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
               MI_REEMPLAZOS(0).CLAVE:= 'FACTURA';
               MI_REEMPLAZOS(0).VALOR:= UN_RECIBO;
               MI_REEMPLAZOS(1).CLAVE:= 'PREDIO';
               MI_REEMPLAZOS(1).VALOR:= UN_PREDIO;
               PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                         ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTFACTURADEPRE
                                         ,UN_TABLAERROR => MI_TABLA
                                         ,UN_REEMPLAZOS => MI_REEMPLAZOS);
      END;
      BEGIN
          BEGIN
              MI_TABLA := 'IP_RECIBOS_DE_PAGO';
              MI_CAMPOS:= 'PAGO = -1, PAG_BANPAG = ''' || UN_BANCO         || ''', 
                          PREFECPAG = TO_DATE(''' || UN_FECHA_RECAUDO || ''',''DD/MM/YYYY''), 
                          PAQUETEPAG='''||UN_PAQUETE||''', FECHA_REGRECAUDO=SYSDATE, 
                          DATE_MODIFIED = SYSDATE, MODIFIED_BY   = '''|| UN_USER || ''', 
                          USUARIO_REGRECAUDO='''||UN_USER||'''';
              MI_CONDICION := 'COMPANIA='''||UN_COMPANIA||''' AND DOCNUM='''||UN_RECIBO||'''';
              MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA  => MI_TABLA,
                                         UN_ACCION   => 'M',
                                         UN_CAMPOS   => MI_CAMPOS, 
                                         UN_CONDICION=> MI_CONDICION); 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTURECIBOSPAGO
                           ,UN_TABLAERROR => MI_TABLA);                                                                    
     END;                                    
END PR_REGISTRAR_RECAUDO;

--15

PROCEDURE PR_ACTIVAR_PREDIAL_COBRO
  /*
  NAME              : PR_ACTIVAR_PREDIAL_COBRO Nuevo
  AUTHORS           : SYSMAN  SAS 
  AUTHOR CREACIÓN   : SERGIO ESTEBAN PIÑA
  DATE CREACIÓN     : 10/01/2017
  TIME              : 10:15 AM
  SOURCE MODULE     : PREDIAL
  DESCRIPTION       : Activa el predial con proceso de cobro
  @NAME:  activarPredialEnCobro
  @METHOD:  GET 
  */
  (
  -- Parametro que recibe el nombre de la compania
  UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe el numero de documento a usar en la funcion
  UN_PRECOD                   IN PCK_SUBTIPOS.TI_CODPREDIO,
  -- Parametro que recibe el usuario
  UN_USER                     IN PCK_SUBTIPOS.TI_USUARIO,
  -- Parametro que recibe el número del proceso
  UN_NUMPROCESO               IN VARCHAR2,
  -- Parametro que recibe el número de la orden
  UN_NUMORDEN                 IN VARCHAR2
  )
  AS
    MI_ERROR_FUN              PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 1; 
    -- Variable que almacena la cadena de campos a modificar en la funcion.
    MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
    -- Variable que almacena la cadena de valores que se le van a asignar a los 
    -- campos en la funcion.
    MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
    -- Variable que almacenara la cadena que contiene la condicion para hacer 
    -- las modificaciones.
    MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
    -- Variable que almacenara el numero de registro que contiene una consulta.
    MI_I                      PCK_SUBTIPOS.TI_ENTERO_LARGO;   
    -- Variable que lleva el valor de las observaciones
    MI_OBS                    VARCHAR2(200 CHAR);
    MI_MSGERRORES             PCK_SUBTIPOS.TI_CLAVEVALOR;
    --Variable para cargar los valores para reemplazar en los mensajes de error
  BEGIN

   -- BEGIN usado para el control de errores al realizar las actualizaciones
  BEGIN                   

    BEGIN
        MI_OBS := 'Inicio Proceso de cobro en ' ||  TO_CHAR(SYSDATE, 'DD/MM/YYYY') ; 
        MI_CAMPOS := ' PROCESO_DE_COBRO               = -1
                     , NUMERO_PROCESO                 = '''||UN_NUMPROCESO||'''
                     , OBSERVACION_COBRO_COACTIVO     = '''|| MI_OBS ||''' 
                     , DATE_MODIFIED                  = SYSDATE
                     , MODIFIED_BY                    = '''||UN_USER||''' ' ;

        MI_CONDICION := 'COMPANIA         = '''||UN_COMPANIA||''' 
                    AND CODIGO            = '''||UN_PRECOD||'''
                    AND NUMERO_ORDEN      = '''||UN_NUMORDEN||''' ';

        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_USUARIOS_PREDIAL', 
                                               UN_ACCION   =>'M',
                                               UN_CAMPOS   =>MI_CAMPOS,
                                               UN_CONDICION=>MI_CONDICION
                                               );   

       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END ;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                     MI_MSGERRORES(1).CLAVE := 'CODIGO';
                     MI_MSGERRORES(1).VALOR := UN_PRECOD;
                     PCK_ERR_MSG.RAISE_WITH_MSG(
                     UN_EXC_COD     =>SQLCODE,
                     UN_ERROR_COD   =>PCK_ERRORES.ERRR_PREDIAL_ACTPROCESOCOBRO,
                     UN_REEMPLAZOS  =>MI_MSGERRORES

          );
    END;

    BEGIN
    -- BEGIN usado para el control de errores al realizar las inserciones
      BEGIN
          MI_CAMPOS := ' COMPANIA                
                       , MODCCO                 
                       , MODCOD                  
                       , MODOPE                
                       , MODVAN               
                       , MODVNU                  
                       , DESCRIPCION             
                       , MODFEC                 
                       , MODHOR           
                       , DATE_CREATED
                       , CREATED_BY ' ;


         MI_VALORES := ' '''|| UN_COMPANIA ||'''
                       , ''114''
                       , '''|| UN_PRECOD ||'''
                       , '''|| UN_USER ||'''
                       , 0
                       , -1
                       , ''Activa Indicador de Proceso de Cobro''
                       , TO_DATE('''||TO_CHAR(SYSDATE, 'DD/MM/YYYY')||''', ''DD/MM/YYYY'')
                       , TO_DATE('''||TO_CHAR(SYSDATE, 'DD/MM/YYYY HH24:MI:SS')||''', ''DD/MM/YYYY HH24:MI:SS'')
                       , SYSDATE
                       , '''||UN_USER||''' ' ;


          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_AUDITORIA', 
                                                 UN_ACCION   =>'I',
                                                 UN_CAMPOS   =>MI_CAMPOS,
                                                 UN_VALORES  =>MI_VALORES
                                                 );   

         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                    END ;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                       MI_MSGERRORES(1).CLAVE := 'CODIGO';
                       MI_MSGERRORES(1).VALOR := UN_PRECOD;
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                       UN_EXC_COD =>SQLCODE,
                       UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_AUDPROCESOCOBRO,
                       UN_REEMPLAZOS  =>MI_MSGERRORES

            );
    END;  

END PR_ACTIVAR_PREDIAL_COBRO;

FUNCTION FC_ACT_FACTURADOS_PRESCRIPCION
  /*
    NAME              : ACTFACTURADOSPRESCRIPCION
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : Juan Camilo
    DATE MIGRADOR     : 10/02/2017
    TIME              : 14:21 PM
    SOURCE MODULE     : Predial
    MODIFIER          : JUAN CAMILO \ JAVIER RODRIGUEZ
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : PERMITE ACTUALIZAR LA PRESCRIPCION DEL PREDIAL
    MODIFICATIONS     : SE AJUSTA AL ESTANDAR Y SU INDENTACION, SE QUITA CODIGO QUE NO APLICA.
    PARAMETROS DE ENTRADA: 
      UN_COMPANIA         --Codigo de la compañia
      UN_CODIGO           --Codigo del predio
      UN_NUMPREDIAL       --numero de factura de predial
      UN_PRESCRIPCION     --Prescripcion
      UN_RESOLUCION       --numero de resolucion
      UN_DESDE            --año calculado desde
      UN_HASTA            --año calculado hasta   
      UN_OBSERVACION      --observaciones de la resolucion,elaborado por,fecha de prescripcion y firmada,
      UN_USUARIO          --Usuario
    @NAME: actFacturadosPrescripcion
    @METHOD: post       
  */
(
      UN_COMPANIA      IN  PCK_SUBTIPOS.TI_COMPANIA, 
      UN_CODIGO        IN  PCK_SUBTIPOS.TI_CODPREDIO,
      UN_NUMPREDIAL    IN  PCK_SUBTIPOS.TI_DOCNUM,
      UN_PRESCRIPCION  IN  PCK_SUBTIPOS.TI_ENTERO,
      UN_RESOLUCION    IN  PCK_SUBTIPOS.TI_PARAMETRO,
      UN_DESDE         IN  PCK_SUBTIPOS.TI_ENTERO,
      UN_HASTA         IN  PCK_SUBTIPOS.TI_ENTERO,
      UN_OBSERVACION   IN  PCK_SUBTIPOS.TI_PARAMETRO,
      UN_USUARIO       IN  PCK_SUBTIPOS.TI_USUARIO 
)
RETURN NUMBER AS 
	MI_CAMPOS      PCK_SUBTIPOS.TI_CAMPOS;
  MI_CAMPO       PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION   PCK_SUBTIPOS.TI_CONDICION;
  MI_RPTA        PCK_SUBTIPOS.TI_LOGICO;
  MI_OBSERVACION PCK_SUBTIPOS.TI_CAMPOS;
  MI_ANOPAGO     PCK_SUBTIPOS.TI_ANIO;
  MI_CURSOCICLO  SYS_REFCURSOR;
  MI_REEMPLAZOS  PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_PARAMETROS  PCK_SUBTIPOS.TI_PARAMETRO;

BEGIN
    IF UN_COMPANIA     IS NULL OR
       UN_CODIGO       IS NULL OR 
       UN_NUMPREDIAL   IS NULL OR 
       UN_PRESCRIPCION IS NULL OR
       UN_RESOLUCION   IS NULL OR
       UN_DESDE        IS NULL OR
       UN_HASTA        IS NULL OR
       UN_OBSERVACION  IS NULL OR
       UN_USUARIO      IS NULL THEN    
        MI_PARAMETROS := ' COMPAÑIA: '      ||UN_COMPANIA||
                         ' CODIGO: '        ||UN_CODIGO||
                         ' NUMERO PREDIAL: '||UN_NUMPREDIAL||
                         ' PRESCRIPCION: '  ||UN_PRESCRIPCION||
                         ' RESOLUCION: '    ||UN_RESOLUCION||
                         ' FECHA DESDE: '   ||UN_DESDE||
                         ' FECHA HASTA: '   ||UN_HASTA||
                         ' OBSERVACION '    ||UN_OBSERVACION||
                         ' USUARIO: '       ||UN_USUARIO;
        BEGIN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
            MI_REEMPLAZOS (0).CLAVE := 'NOPARAMETRO';
            MI_REEMPLAZOS (0).VALOR :=  MI_PARAMETROS;        
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD     => SQLCODE,                                      
                        UN_ERROR_COD   => PCK_ERRORES.ERRR_PREDIAL_PARAMNODATOS,                                   
                        UN_REEMPLAZOS  => MI_REEMPLAZOS
                        );      
        END;
    END IF;
    BEGIN
        MI_CAMPOS:= 'NOCOBRADO         = 0 ,
                     PAGADO            = 0 , 
                     FECHAPRESCRIPCION = NULL ,
                     OBSERVACIONES     = ''RES. DE ANULACION DE PRESCRIPCION: '||UN_RESOLUCION||''',
                     DATE_MODIFIED     = SYSDATE,
                     MODIFIED_BY       = '''||UN_USUARIO||''' ';                 

        MI_CONDICION := '    IP_FACTURADOS.COMPANIA  = '''||UN_COMPANIA||''' 
                         AND IP_FACTURADOS.CODIGO    = '''||UN_CODIGO||'''
                         AND IP_FACTURADOS.PREANO    BETWEEN '||UN_DESDE||'  AND '||UN_HASTA||'
                         AND IP_FACTURADOS.NOCOBRADO NOT IN(0) 
                         AND IP_FACTURADOS.PAGADO    NOT IN(0)';
        MI_PARAMETROS:=' COMPAÑIA: '          ||UN_COMPANIA||
                       ' CODIGO DE PREDIAL: ' ||UN_CODIGO||
                       ' FECHA DESDE: '       ||UN_DESDE||
                       ' FEHCA HASTA:'        ||UN_HASTA;
        BEGIN
            MI_RPTA:=PCK_DATOS.FC_ACME(
                               UN_TABLA => 'IP_FACTURADOS',
                               UN_ACCION    => 'M',
                               UN_CAMPOS    => MI_CAMPOS,
                               UN_CONDICION => MI_CONDICION);
         EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
             RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                         
         END;    
     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
         MI_REEMPLAZOS (0).CLAVE := 'NOPARAMETROS';
         MI_REEMPLAZOS (0).VALOR :=  MI_PARAMETROS;
         MI_REEMPLAZOS (1).CLAVE := 'TABLA';
         MI_REEMPLAZOS (1).VALOR := 'IP_FACTURADOS';
         PCK_ERR_MSG.RAISE_WITH_MSG(
                     UN_EXC_COD    => SQLCODE, 
                     UN_TABLAERROR => 'IP_FACTURADOS',
                     UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTIPFACT_CODEHA,                                   
                     UN_REEMPLAZOS => MI_REEMPLAZOS);                                           
     END;
     IF MI_RPTA > 0 THEN
         MI_ANOPAGO:=PCK_PREDIAL.FC_PAGOANOVALIDO( 
                                 UN_COMPANIA => UN_COMPANIA , 
                                 UN_PREDIO   => UN_CODIGO);            
         BEGIN 
             SELECT IP_USUARIOS_PREDIAL.OBSERVACIO 
               INTO MI_CAMPO
               FROM IP_USUARIOS_PREDIAL
              WHERE IP_USUARIOS_PREDIAL.COMPANIA     = UN_COMPANIA
                AND IP_USUARIOS_PREDIAL.CODIGO       = UN_CODIGO
                AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN = UN_NUMPREDIAL;
        EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_CAMPO:=NULL;
        END;

        IF MI_CAMPO IS NOT NULL THEN    
            BEGIN       
                MI_OBSERVACION :=NVL(MI_CAMPO,' ')
                               ||' RES. DE ANULACION DE PRESCRIPCION Nº '
                               ||UN_RESOLUCION
                               ||' DEL '||UN_PRESCRIPCION;

                MI_CAMPOS     :='OBSERVACIO    = '''||MI_OBSERVACION||''', 
                                 PAGO_ANO      = '||MI_ANOPAGO||',
                                 DATE_MODIFIED = SYSDATE,
                                 MODIFIED_BY   = '''||UN_USUARIO||''' ';

                MI_CONDICION  :='     COMPANIA = '''||UN_COMPANIA||'''
                                  AND CODIGO   = '''||UN_CODIGO||'''';

                MI_PARAMETROS:=' COMPAÑIA: '          ||UN_COMPANIA||
                               ' CODIGO DE PREDIAL: ' ||UN_CODIGO;

                BEGIN          
                    MI_RPTA:=PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                               UN_ACCION    => 'M',
                                               UN_CAMPOS    => MI_CAMPOS,
                                               UN_CONDICION => MI_CONDICION
                                               );  
                EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE  PCK_EXCEPCIONES.EXC_PREDIAL;                                         
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                MI_REEMPLAZOS (1).CLAVE := 'NOPARAMETROS';
                MI_REEMPLAZOS (1).VALOR :=  MI_PARAMETROS;
                MI_REEMPLAZOS (2).CLAVE := 'TABLA';
                MI_REEMPLAZOS (2).VALOR := 'IP_USUARIOS_PREDIAL';
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE, 
                                           UN_TABLAERROR  => 'IP_USUARIOS_PREDIAL',
                                           UN_ERROR_COD   => PCK_ERRORES.ERRR_PREDIAL_ACTIPFACT_CODEHA,                                   
                                           UN_REEMPLAZOS  => MI_REEMPLAZOS
                                            );            
            END;
        ELSE
            BEGIN
                MI_CAMPOS      :='PAGO_ANO      =  '||MI_ANOPAGO||',
                                  DATE_MODIFIED = SYSDATE,
                                  MODIFIED_BY   = '''||UN_USUARIO||''' ';

                MI_CONDICION   :='    COMPANIA = '''||UN_COMPANIA||'''
                                  AND CODIGO   = '''||UN_CODIGO||'''';

                MI_PARAMETROS :=' COMPAÑIA: '      ||UN_COMPANIA||
                                ' CODIGO: '        ||UN_CODIGO||                                 
                                ' AÑO DE PAGO: '   ||MI_ANOPAGO;

                BEGIN            
                    MI_RPTA:=PCK_DATOS.FC_ACME(
                                       UN_TABLA      => 'IP_USUARIOS_PREDIAL',
                                       UN_ACCION     => 'M',
                                       UN_CAMPOS     => MI_CAMPOS,
                                       UN_CONDICION  => MI_CONDICION
                                       ); 
                 EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                     RAISE  PCK_EXCEPCIONES.EXC_PREDIAL; 
                 END;
             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                 MI_REEMPLAZOS (1).CLAVE := 'NOPARAMETROS';
                 MI_REEMPLAZOS (1).VALOR :=  MI_PARAMETROS;
                 MI_REEMPLAZOS (2).CLAVE := 'TABLA';
                 MI_REEMPLAZOS (2).VALOR := 'IP_USUARIOS_PREDIAL';
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                             UN_EXC_COD     => SQLCODE, 
                             UN_TABLAERROR  => 'IP_USUARIOS_PREDIAL',
                             UN_ERROR_COD   => PCK_ERRORES.ERRR_PREDIAL_ACTIPFACT_CODEHA,                                   
                             UN_REEMPLAZOS  => MI_REEMPLAZOS
                            ); 
             END;
        END IF;
        MI_OBSERVACION:=MI_OBSERVACION||UN_OBSERVACION;
        <<BUCLEANULAR>>
        FOR MI_CURSOCICLO IN(
            SELECT RMOCOD      
              FROM IP_MODICOD 
             WHERE RMOVAR='ANULARPRESCRIPCION')
        LOOP
            IF MI_CURSOCICLO.RMOCOD IS NOT NULL THEN
                PCK_PREDIAL.PR_AUDITORIA(
                            UN_COMPANIA    => UN_COMPANIA,
                            UN_CODMOD      => UN_CODIGO,
                            UN_OPEMOD      => UN_USUARIO,
                            UN_CCOMOD      => MI_CURSOCICLO.RMOCOD ,      
                            UN_VANMOD      => '-',
                            UN_VNUMOD      => '-',
                            UN_DESCRIPCION => UN_OBSERVACION);
            END IF;
        END LOOP BUCLEANULAR;        
    END IF;
    RETURN MI_RPTA; 
END FC_ACT_FACTURADOS_PRESCRIPCION;

FUNCTION FC_ACT_MODIFIESTRASOCIO
 /*
    NAME                  : FC_ACT_MODIFIESTRASOCIO
    AUTHORS               : SYSMAN  SAS 
    AUTHOR MIGRACION      : JUAN CAMILO RODRIGUEZ DIAZ
    DATE MIGRADOR         : 22/02/2017
    TIME                  : 10:12 PM
    SOURCE MODULE         : PREDIAL
    MODIFIER              : 
    DATE MODIFIED         : 
    TIME                  : 
    DESCRIPTION           : 
    PARAMETROS DE ENTRADA :
      UN_CODIGO           --Codigo del predio
      UN_ESTRATO          --Estrato del predio
      UN_NUEVOESTRATO     --Nuevo Estrato para el predio
      UN_FORMATO          --Certificado
      UN_NUEVOFORMATO     --Certificado
      UN_TABLA            --Tabla para la actualizacion
      UN_TABLAI            --Tabla para la insercion
      UN_COMPANIA         --Compañia
      UN_USUARIO          --Usuario
      UN_DESCRIPCION      --Descripcion
    @NAME:  consultarEncabezadoDeColumna
    @METHOD:  post     
  */
(
  UN_CODIGO         IN PCK_SUBTIPOS.TI_CODPREDIO,
  UN_ESTRATO        IN PCK_SUBTIPOS.TI_PARAMETRO,
  UN_NUEVOESTRATO   IN PCK_SUBTIPOS.TI_PARAMETRO,
  UN_FORMATO        IN PCK_SUBTIPOS.TI_PARAMETRO,
  UN_NUEVOFORMATO   IN PCK_SUBTIPOS.TI_PARAMETRO,
  UN_TABLA          IN PCK_SUBTIPOS.TI_PARAMETRO,
  UN_TABLAI         IN PCK_SUBTIPOS.TI_PARAMETRO,
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO,
  UN_DESCRIPCION    IN PCK_SUBTIPOS.TI_PARAMETRO
)
RETURN PCK_SUBTIPOS.TI_ENTERO AS
  MI_RPTA           PCK_SUBTIPOS.TI_ENTERO;
  MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION; 
  MI_VALORESINSERT  PCK_SUBTIPOS.TI_PARAMETRO;
  MI_MODCCO         PCK_SUBTIPOS.TI_PARAMETRO;
  MI_REEMPLAZOS     PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_PARAMETROS     PCK_SUBTIPOS.TI_PARAMETRO;
BEGIN
    BEGIN
        IF UN_CODIGO       IS NULL OR 
           UN_ESTRATO      IS NULL OR  
           UN_NUEVOESTRATO IS NULL OR 
           UN_FORMATO      IS NULL OR 
           UN_NUEVOFORMATO IS NULL OR 
           UN_TABLA        IS NULL OR 
           UN_COMPANIA     IS NULL OR 
           UN_USUARIO      IS NULL OR 
           UN_DESCRIPCION  IS NULL THEN           
           MI_RPTA:=0;
           MI_PARAMETROS:=UN_CODIGO       ||','||
                          UN_ESTRATO      ||','||
                          UN_NUEVOESTRATO ||','||
                          UN_FORMATO      ||','||
                          UN_NUEVOFORMATO ||','||
                          UN_TABLA        ||','||
                          UN_COMPANIA     ||','|| 
                          UN_USUARIO      ||','||
                          UN_DESCRIPCION ;             
           RAISE  PCK_EXCEPCIONES.EXC_GENERAL;       
        END IF;
         EXCEPTION
             WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN 

                  MI_REEMPLAZOS (1).CLAVE := 'CAMPO';
                  MI_REEMPLAZOS (1).VALOR :=  MI_PARAMETROS;                
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,                                
                                             UN_ERROR_COD   => PCK_ERRORES.ERR_PREDIAL_NULL_MODIFIESTRAS,                                   
                                             UN_REEMPLAZOS  => MI_REEMPLAZOS
                                             );      
    END;
  BEGIN
      MI_CAMPOS:='ESTRATO_SOCIOECONOMICO = '''||UN_NUEVOESTRATO ||''','||
                'FORMATO_ESTRATO = '''||UN_NUEVOFORMATO||''','||
                'DATE_MODIFIED=SYSDATE,'||
                'MODIFIED_BY='''||UN_USUARIO||'''';
      MI_CONDICION:=' CODIGO = '''||UN_CODIGO ||'''';
    BEGIN
         MI_RPTA:= PCK_DATOS.FC_ACME(UN_TABLA     => UN_TABLA,
                                     UN_ACCION    =>'M',
                                     UN_CAMPOS    => MI_CAMPOS,
                                     UN_CONDICION => MI_CONDICION);           
        EXCEPTION
             WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE  PCK_EXCEPCIONES.EXC_PREDIAL;
    END;
         EXCEPTION
         WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 

                  MI_REEMPLAZOS (1).CLAVE := 'CAMPO';
                  MI_REEMPLAZOS (1).VALOR :=  MI_CAMPOS;
                  MI_REEMPLAZOS (2).CLAVE := 'TABLA';
                  MI_REEMPLAZOS (2).VALOR :=  UN_TABLA;

                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE, 
                                             UN_TABLAERROR  => UN_TABLA,
                                             UN_ERROR_COD   => PCK_ERRORES.ERR_PREDIAL_ACT_MODIFIESTRAS,                                   
                                             UN_REEMPLAZOS  => MI_REEMPLAZOS
                                            );    
  END;
      MI_CAMPOS:='COMPANIA,'||
                  'MODCCO,'||
                  'MODCOD,'||
                  'MODOPE,'||
                  'MODFEC,'||
                  'MODHOR,'||
                  'MODVAN,'||
                  'MODVNU,'||
                  'DESCRIPCION,'||
                  'CREATED_BY,'||
                  'DATE_CREATED';
      MI_MODCCO:='550';
      MI_VALORESINSERT:=''''||UN_COMPANIA||''''||','||
                        ''''||MI_MODCCO||''''||','||
                        ''''||UN_CODIGO||''''||','||
                        ''''||UN_USUARIO||''''||','||
                        'SYSDATE,SYSDATE,'||
                        ''''||UN_ESTRATO||''''||','||   
                        ''''||UN_NUEVOESTRATO||''''||','||
                        ''''||UN_DESCRIPCION||''','||
                        ''''||UN_USUARIO||''''||','||
                        'SYSDATE';

      MI_RPTA:=FC_INSERT_MODIFIESTRASOCIO(UN_TABLAI,MI_CAMPOS,MI_VALORESINSERT);
      MI_MODCCO:='551';
      MI_VALORESINSERT:=''''||UN_COMPANIA||''''||','||
                        ''''||MI_MODCCO||''''||','||
                        ''''||UN_CODIGO||''''||','||
                        ''''||UN_USUARIO||''''||','||
                        'SYSDATE,SYSDATE,'||
                        ''''||UN_FORMATO||''''||','||   
                        ''''||UN_NUEVOFORMATO||''''||','||
                        ''''||UN_DESCRIPCION||''','||
                        ''''||UN_USUARIO||''''||','||
                        'SYSDATE';

     MI_RPTA:=FC_INSERT_MODIFIESTRASOCIO(UN_TABLAI,MI_CAMPOS,MI_VALORESINSERT);


  RETURN MI_RPTA;
    EXCEPTION
       WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD   => SQLCODE,               
                                        UN_ERROR_COD => PCK_ERRORES.ERRR_OTHER_MODIFIESTRASOCIO
                                        );    

END FC_ACT_MODIFIESTRASOCIO;

FUNCTION FC_INSERT_MODIFIESTRASOCIO
 /*
    NAME                  : FC_INSERT_MODIFIESTRASOCIO
    AUTHORS               : SYSMAN  SAS 
    AUTHOR MIGRACION      : JUAN CAMILO RODRIGUEZ DIAZ
    DATE MIGRADOR         : 22/02/2017
    TIME                  : 10:12 PM
    SOURCE MODULE         : PREDIAL
    MODIFIER              : 
    DATE MODIFIED         : 
    TIME                  : 
    DESCRIPTION           : Insercion de modificacion de estrato socieconomico.
    PARAMETROS DE ENTRADA :
      UN_TABLA            --Tabla de la insercion
      UN_CAMPOS           --Campos de la insercion
      UN_VALORESINSERT    --Valores para la insercion
    @NAME:  insertModifiEstraSocio
    @METHOD:  get     
  */
(
UN_TABLA          IN PCK_SUBTIPOS.TI_PARAMETRO,
UN_CAMPOS         IN PCK_SUBTIPOS.TI_PARAMETRO,
UN_VALORESINSERT  IN PCK_SUBTIPOS.TI_PARAMETRO
)
RETURN NUMBER AS
  MI_RPTA         PCK_SUBTIPOS.TI_LOGICO;
  MI_REEMPLAZOS   PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
  BEGIN
      BEGIN
          MI_RPTA := PCK_DATOS.FC_ACME(UN_TABLA   =>UN_TABLA,
                                       UN_ACCION  =>'I',
                                       UN_CAMPOS  =>UN_CAMPOS,
                                       UN_VALORES =>UN_VALORESINSERT     
                                      );     

          EXCEPTION
               WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_PREDIAL;
      END;
        EXCEPTION
         WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 

                  MI_REEMPLAZOS (1).CLAVE := 'CAMPO';
                  MI_REEMPLAZOS (1).VALOR :=  UN_CAMPOS;
                  MI_REEMPLAZOS (2).CLAVE := 'TABLA';
                  MI_REEMPLAZOS (2).VALOR :=  UN_TABLA;

                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE, 
                                             UN_TABLAERROR  => UN_TABLA,
                                             UN_ERROR_COD   => PCK_ERRORES.ERR_PREDIAL_INSE_MODIFIESTRAS,                                   
                                             UN_REEMPLAZOS  => MI_REEMPLAZOS
                                            );    
  END;
RETURN MI_RPTA;
END FC_INSERT_MODIFIESTRASOCIO; 

FUNCTION FC_VERIFICARPREDIOSFACTURADOS
  (
    /*
      NAME              : FC_VERIFICARPREDIOSFACTURADOS  
      AUTHORS           : STEFANINI SYSMAN
      AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
      DATE MIGRADOR     : 19/07/2017
      TIME              : 12:31 PM
      SOURCE MODULE     : ALMACEN
      DESCRIPTION       : Verificar cuales predios estan facturados y que el año pago este en 0 o en la vigencia actual
      PARAMETROS DE ENTRADA: 
						UN_COMPANIA => Compania por la cual se ingresa en la aplicacion
						UN_CODIGOINICIAL => Codigo inicial seleccionado en el formulario
						UN_CODIGOFINAL => Codigo final seleccionado en el formulario
      MODIFIER			: 
      DATE MODIFIED 	: 
      TIME				      
      MODIFICATIONS	    : 

      @NAME:    verificarPrediosFacturados
      @METHOD:  GET                       
    */
    UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA,  
    UN_CODIGOINICIAL       IN PCK_SUBTIPOS.TI_CODPREDIO,      
    UN_CODIGOFINAL         IN PCK_SUBTIPOS.TI_CODPREDIO     
  )RETURN CLOB
    AS

    MI_MENSAJE                    CLOB:='';
	  MI_ERRORES                    NUMBER:=0;


    BEGIN

      MI_MENSAJE:=MI_MENSAJE||'*** Predios a los que no se le actualizó la última vigencia cancelada *** ' || CHR(10)||CHR(13);
      MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);

      <<CODIGOSFACTURADOS>>     
      FOR MI_RS IN( SELECT 	IP_FACTURADOS.CODIGO ,
							IP_USUARIOS_PREDIAL.NOMBRE,
							IP_USUARIOS_PREDIAL.NUMERO_ORDEN,
							IP_USUARIOS_PREDIAL.PAGO_ANO,
							IP_FACTURADOS.PREANO
                    FROM IP_FACTURADOS
                        INNER JOIN IP_USUARIOS_PREDIAL
                            ON IP_FACTURADOS.COMPANIA = IP_USUARIOS_PREDIAL.COMPANIA
                            AND IP_FACTURADOS.CODIGO = IP_USUARIOS_PREDIAL.CODIGO
                            AND IP_FACTURADOS.NUMERO_ORDEN = IP_USUARIOS_PREDIAL.NUMERO_ORDEN
                    WHERE IP_FACTURADOS.COMPANIA = UN_COMPANIA
                        AND IP_FACTURADOS.CODIGO BETWEEN UN_CODIGOINICIAL AND UN_CODIGOFINAL
                        AND (IP_USUARIOS_PREDIAL.PAGO_ANO IN(0,EXTRACT (YEAR FROM SYSDATE)) OR IP_USUARIOS_PREDIAL.PAGO_ANO <> EXTRACT (YEAR FROM SYSDATE) ) )
      LOOP
        MI_MENSAJE:=MI_MENSAJE ||'Predio: '|| MI_RS.CODIGO || ' ' || 'Nombre: '|| MI_RS.NOMBRE|| ' ' || CHR(10)||CHR(13);
        MI_MENSAJE := MI_MENSAJE || ' Número de Orden: ' ||MI_RS.NUMERO_ORDEN|| ' ' ||'Año Pago: ' || ' '||MI_RS.PAGO_ANO|| ' ' || 'Año Facturado : ' ||MI_RS.PREANO ||CHR(10)||CHR(13);
        MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);
        MI_ERRORES:=MI_ERRORES+1;
      END LOOP CODIGOSFACTURADOS;

      MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);
      MI_MENSAJE:=MI_MENSAJE||'Proceso terminado con '||TO_CHAR(MI_ERRORES)||' inconsistencias.'||CHR(10)||CHR(13);
      MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);
      MI_MENSAJE:=MI_MENSAJE||' *** Fin del Informe *** '||CHR(10)||CHR(13);
    RETURN MI_MENSAJE;

  END FC_VERIFICARPREDIOSFACTURADOS;
END PCK_PREDIAL_COM1;