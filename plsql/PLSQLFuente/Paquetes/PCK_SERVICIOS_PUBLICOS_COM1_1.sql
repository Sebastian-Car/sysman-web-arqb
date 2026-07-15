create or replace PACKAGE BODY PCK_SERVICIOS_PUBLICOS_COM1
AS
  --1
  FUNCTION FC_MEDIDORDUDOSO
  (
    /*
      NAME              : FC_MEDIDORDUDOSO --> EN ACCESS medidordudoso
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MIGRADOR     : 24/08/2016
      TIME              : 03:30 PM
      SOURCE MODULE     : SysmanSp2016.05.04
      MODIFIER          : YEISSON ALEJANDRO ROJAS RUIZ
      DATE MODIFIED     : 04/01/2017
      TIME              : 09:00 AM
      MODIFICATIONS     : CORRECCIÓN SEGÚN ESTÁNDAR DE PROGRAMACIÓN Y OPTIMIZACIÓN DEL MANEJO DE ERRORES.
      DESCRIPTION       : FUNCION QUE PERMITIA LLENAR LA TABLA TMP_DUDOSO, AHORA REALIZA LA VERIFICACIÓN MEDIANTE UNA CONSULTA.
      PARAMETERS        : UN_COMPANIA => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                          UN_CICLO    => CICLO POR EL CUAL SE VA A FILTRAR LA INFORMACIÓN.

      @NAME:    obtenerMedidoresDudosos 
      @METHOD:  GET
    */
    UN_COMPANIA IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_CICLO    IN  PCK_SUBTIPOS.TI_CICLO 
  )
  RETURN NUMBER AS
    MI_DUDOSOS    PCK_SUBTIPOS.TI_ENTERO;
  BEGIN
    BEGIN
      SELECT  COUNT(1)
      INTO    MI_DUDOSOS
      FROM    SP_USUARIO
        INNER JOIN SP_MEDIDOR
          ON  SP_USUARIO.COMPANIA = SP_MEDIDOR.COMPANIA
          AND SP_USUARIO.MEDIDOR  = SP_MEDIDOR.CONSECUTIVO
      WHERE   SP_USUARIO.COMPANIA                      = UN_COMPANIA
        AND   SP_USUARIO.CICLO                         = UN_CICLO
        AND   (REGEXP_COUNT(SP_MEDIDOR.CODIGO,'[0-9]') < 2
        OR    REGEXP_COUNT(SP_MEDIDOR.CODIGO,'[^0-9]') > 4);

      EXCEPTION WHEN NO_DATA_FOUND THEN 
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
    END;

    IF MI_DUDOSOS > 0 THEN
      RETURN 1;
    ELSE
      RETURN 0;
    END IF;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN    
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERR_FACSERP_MEDIDOR_DUDOSO
      );
  END FC_MEDIDORDUDOSO;

  --2
  FUNCTION FC_ACTUALIZAMEDIDORSINESTFEC
  (
    /*
      NAME              : FC_ACTUALIZAMEDIDORSINESTFEC --> EN ACCESS Aceptar_Click opcion 2 form actualizarmedidor
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MIGRADOR     : 24/08/2016
      TIME              : 03:45 PM
      SOURCE MODULE     : SysmanSp2016.05.04
      MODIFIER          : YEISSON ALEJANDRO ROJAS RUIZ
      DATE MODIFIED     : 04/01/2017
      TIME              : 11:00 AM
      MODIFICATIONS     : CORRECCIÓN SEGÚN ESTÁNDAR DE PROGRAMACIÓN Y OPTIMIZACIÓN DEL MANEJO DE ERRORES.
      DESCRIPTION       : Función que realiza el proceso de actualizar medidores sin estado o sin fecha.
      PARAMETERS        : UN_COMPANIA => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                          UN_CICLO    => CICLO POR EL CUAL SE VA A FILTRAR LA INFORMACIÓN.

      @NAME:    actualizarMedidores 
      @METHOD:  GET
    */      
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CICLO        IN PCK_SUBTIPOS.TI_CICLO,
    UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO
  )
  RETURN PCK_SUBTIPOS.TI_LOGICO 
  AS
    -- 'actualiza los que estan en la tabla
    MI_MEDIDOR          SP_MEDIDOR.CODIGO%TYPE;
    MI_TABLAUPDATE      PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOSUPDATE     PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICIONUPDATE  PCK_SUBTIPOS.TI_CONDICION;
    MI_RTA              PCK_SUBTIPOS.TI_ENTERO_LARGO;
BEGIN  
    <<ACTUALIZAR_SP_MEDIDOR>>
    FOR RS IN
      (SELECT SP_MEDIDOR.COMPANIA,
              SP_USUARIO.CICLO,
              SP_USUARIO.CODIGORUTA,
              SP_USUARIO.FECHAINSTALACION,
              SP_MEDIDOR.CODIGO
      FROM    SP_USUARIO
        INNER JOIN SP_MEDIDOR
          ON  SP_USUARIO.COMPANIA = SP_MEDIDOR.COMPANIA
          AND SP_USUARIO.MEDIDOR  = SP_MEDIDOR.CONSECUTIVO
      WHERE   SP_USUARIO.COMPANIA                      = UN_COMPANIA
        AND   SP_USUARIO.CICLO                         = UN_CICLO
        AND   (REGEXP_COUNT(SP_MEDIDOR.CODIGO,'[0-9]') < 2
        OR    REGEXP_COUNT(SP_MEDIDOR.CODIGO,'[^0-9]') > 4)
      )
    LOOP
      IF INSTR( RS.CODIGO,CHR(39),1) > 0 
         OR INSTR( RS.CODIGO, '|',1) > 0 THEN
          MI_MEDIDOR := REPLACE(REPLACE(RS.CODIGO, CHR(39), ''), '|', ''); 
      ELSE
        MI_MEDIDOR := RS.CODIGO;
      END IF;
     BEGIN      
         BEGIN
             MI_TABLAUPDATE     := 'SP_MEDIDOR';
             MI_CAMPOSUPDATE    := 'CICLO         = '''||RS.CICLO||''', 
                                    CODIGORUTA    = '''||RS.CODIGORUTA||''',
                                    ESTADO        = ''B'',
                                    DATE_MODIFIED = SYSDATE,
                                    MODIFIED_BY   = '''||UN_USUARIO||''',
                                    CODIGO        = REPLACE(REPLACE(CODIGO, CHR(39), ''''), ''|'', '''') '
                                    ||CASE WHEN RS.FECHAINSTALACION IS NULL 
                                           THEN ''
                                           ELSE ', FECHAINST = TO_DATE(''' || TRUNC(RS.FECHAINSTALACION) || ''', ''DD/MM/YYYY'')'
                                      END;

             MI_CONDICIONUPDATE := '   COMPANIA = ''' || UN_COMPANIA || ''' 
                                   AND REPLACE(REPLACE(CODIGO, CHR(39), ''''), ''|'', '''')   = ''' || MI_MEDIDOR || '''';
             MI_RTA   := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLAUPDATE,
                                                     UN_ACCION    => 'M',
                                                     UN_CAMPOS    => MI_CAMPOSUPDATE,
                                                     UN_CONDICION => MI_CONDICIONUPDATE);
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
             RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
         END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN    
          PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD   => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_ACTLZR_MEDIDOR);     
      END;
    END LOOP ACTUALIZAR_SP_MEDIDOR;
    RETURN -1; 
END FC_ACTUALIZAMEDIDORSINESTFEC;

  --3
  FUNCTION FC_NITVALIDAR
  (
    /*
      NAME              : FC_NITVALIDAR --> EN ACCESS NitValidar
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
      DATE MIGRADOR     : 29/08/2016
      TIME              : 09:50 AM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : YEISSON ALEJANDRO ROJAS RUIZ
      DATE MODIFIED     : 04/01/2017
      TIME              : 12:30 PM
      MODIFICATIONS     : CORRECCIÓN SEGÚN ESTÁNDAR DE PROGRAMACIÓN.
      DESCRIPTION       : Función que retorna solo el número de nit.
      PARAMETERS        : UN_COMPANIA => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                          UN_NIT      => NIT QUE SE VA A VALIDAR.

      @NAME:    validarNit 
      @METHOD:  GET
    */
    UN_COMPANIA  IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_NIT       IN  VARCHAR2 -- VARCHAR2 debido a que no existe un tipo de dato con las mismas características en el paquete PCK_SUBTIPOS.
  )
  RETURN VARCHAR2 AS
    MI_NIT        VARCHAR2(30 CHAR); -- VARCHAR2 debido a que no existe un tipo de dato con las mismas características en el paquete PCK_SUBTIPOS.

  BEGIN
    MI_NIT := REPLACE(UN_NIT, '.' , '' );
    MI_NIT := REPLACE(MI_NIT, '-' , '' );
    MI_NIT := REPLACE(MI_NIT, ' ' , '');
    MI_NIT := SUBSTR(MI_NIT,1,9);

    RETURN MI_NIT;
  END FC_NITVALIDAR;

  -- 4
  FUNCTION FC_AUTORIZACION_PERSUASIVO
  (
    /*
      NAME              : FC_AUTORIZACION_PERSUASIVO --> EN ACCESS AUTORIZACION_PERSUASIVO
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
      DATE MIGRADOR     : 29/08/2016
      TIME              : 10:27 AM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : YEISSON ALEJANDRO ROJAS RUIZ
      DATE MODIFIED     : 04/01/2017
      TIME              : 03:00 PM
      MODIFICATIONS     : CORRECCIÓN SEGÚN ESTÁNDAR DE PROGRAMACIÓN.
      DESCRIPTION       : Función que retorna -1(Verdadero) o 0(False)dependediendo del  nit.
      PARAMETERS        : UN_COMPANIA => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                          UN_NIT      => NIT A TENER EN CUENTA PARA EL VALOR DE LA AUTORIZACIÓN.

      @NAME:    obtenerAutorizaciónPersuasivo 
      @METHOD:  GET
    */
    UN_COMPANIA   IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_NIT        IN  VARCHAR2 -- VARCHAR2 debido a que no existe un tipo de dato con las mismas características en el paquete PCK_SUBTIPOS.
  )
  RETURN NUMBER AS
    MI_AUTORIZACION PCK_SUBTIPOS.TI_ENTERO;
    MI_NIT          VARCHAR2(30 CHAR); -- VARCHAR2 debido a que no existe un tipo de dato con las mismas características en el paquete PCK_SUBTIPOS.

  BEGIN
    MI_NIT := PCK_SERVICIOS_PUBLICOS_COM1.FC_NITVALIDAR(UN_COMPANIA => UN_COMPANIA,
                                                        UN_NIT      => UN_NIT);
    CASE
      WHEN MI_NIT        = '844000755' THEN --YOPAL
        MI_AUTORIZACION := -1;
      WHEN MI_NIT        = '832000776' THEN --FUNZA
        MI_AUTORIZACION := -1;
      WHEN MI_NIT        = '832001512' THEN --MADRID
        MI_AUTORIZACION := -1;
      WHEN MI_NIT        = '899999714' THEN --CHIA
        MI_AUTORIZACION := -1;
      WHEN MI_NIT        = '890680053' THEN --FUSAGASUGA
        MI_AUTORIZACION := -1;
      ELSE
        MI_AUTORIZACION := 0;
      END CASE;
    RETURN MI_AUTORIZACION;

  END FC_AUTORIZACION_PERSUASIVO;

  -- 5
PROCEDURE PR_CAMBIORUTA
(
  /*
    NAME              : FC_CAMBIORUTA --> EN ACCESS cambioRuta
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ADRIANA MARITZA CÁCERES BONILLA
    DATE MIGRADOR     : 29/08/2016
    TIME              : 11:39 AM
    SOURCE MODULE     : SERVICIOS PUBLICOS
    MODIFIER          : MIGUEL ANGEL ZANGUÑA HURTADO
    DATE MODIFIED     : 02/10/2017
    TIME              : 10:21 AM
    MODIFICATIONS     : CORRECCIÓN SEGÚN ESTÁNDAR DE PROGRAMACIÓN Y OPTIMIZACIÓN DEL MANEJO DE ERRORES, SE ADICIONA LA TABLA SP_D_RECAUDO_USUARIO.
    DESCRIPTION       : Procedimiento que Actualiza el código de la ruta del ciclo que sea seleccionado.
    PARAMETERS        : UN_NUE_COMPANIA   => CÓDIGO DE LA COMPAÑÍA NUEVA.
                        UN_ANT_COMPANIA   => CÓDIGO DE LA COMPAÑÍA QUE SE VA A ACTUALIZAR.
                        UN_NUE_CICLO      => NÚMERO DEL CICLO NUEVO.
                        UN_ANT_CICLO      => CICLO QUE SE VA A ACTUALIZAR.
                        UN_NUE_CODIGORUTA => NÚMERO DEL CÓDIGO RUTA NUEVO.
                        UN_ANT_CODIGORUTA => CÓDIGO RUTA QUE SE VA A ACTUALIZAR.

    @NAME:    cambiarRuta
    @METHOD:  POST
  */
  UN_NUE_COMPANIA    IN   PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANT_COMPANIA    IN   PCK_SUBTIPOS.TI_COMPANIA,
  UN_NUE_CICLO       IN   PCK_SUBTIPOS.TI_CICLO,
  UN_ANT_CICLO       IN   PCK_SUBTIPOS.TI_CICLO,
  UN_NUE_CODIGORUTA  IN  PCK_SUBTIPOS.TI_CODIGORUTA,
  UN_ANT_CODIGORUTA  IN PCK_SUBTIPOS.TI_CODIGORUTA,
  UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO
)
AS
  MI_PCKDATOS         PCK_SUBTIPOS.TI_ENTERO;
  MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
  MI_EXCLUIDOS        PCK_SUBTIPOS.TI_EXCLUIDOS;
  MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN
  BEGIN
    MI_TABLA     := 'SP_USUARIO';
    MI_EXCLUIDOS := 'COMPANIA,'||
                     'CICLO,'||
                     'CODIGORUTA';
    MI_CAMPOS    := PCK_SYSMAN_UTL.FC_LISTA_CAMPOS(UN_TABLA     => 'SP_USUARIO',
                                                   UN_EXCLUIDOS => MI_EXCLUIDOS);
    MI_VALORES   := 'SELECT ''' || UN_NUE_COMPANIA || ''',
                            ''' || UN_NUE_CICLO || ''',
                            ''' || UN_NUE_CODIGORUTA || ''',
                            '   || MI_CAMPOS || ',
                            SYSDATE,'''||UN_USUARIO||'''
                     FROM   SP_USUARIO
                     WHERE  SP_USUARIO.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                       AND  SP_USUARIO.CICLO      = ''' || UN_ANT_CICLO || '''
                       AND  SP_USUARIO.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || '''';
    MI_CAMPOS   :=MI_CAMPOS||',DATE_CREATED,CREATED_BY';
    MI_CAMPOS   := MI_EXCLUIDOS|| ',' ||MI_CAMPOS ;
    MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                     UN_ACCION  => 'IS',
                                     UN_CAMPOS  => MI_CAMPOS,
                                     UN_VALORES => MI_VALORES);

    MI_TABLA :='SP_USUARIO';
    MI_CAMPOS:='MODIFIED_BY='''||UN_USUARIO||''','||
               'DATE_MODIFIED=SYSDATE,'||
               'CAMBIOCICLORUTA=-1';

    MI_CONDICION:='COMPANIA ='''||UN_NUE_COMPANIA||''' AND CODIGORUTA IN('''|| UN_NUE_CODIGORUTA || ''','||
                  ''''|| UN_ANT_CODIGORUTA || ''')';

    MI_PCKDATOS:=PCK_DATOS.FC_ACME(UN_TABLA     =>MI_TABLA,
                                   UN_ACCION    =>'M',
                                   UN_CAMPOS    =>MI_CAMPOS,
                                   UN_CONDICION =>MI_CONDICION);


    MI_TABLA     := 'SP_ABONOS';
    MI_EXCLUIDOS := 'COMPANIA,'||
                     'CICLO,'||
                     'CODIGORUTA';
    MI_CAMPOS    := PCK_SYSMAN_UTL.FC_LISTA_CAMPOS(UN_TABLA     => 'SP_ABONOS',
                                                   UN_EXCLUIDOS => MI_EXCLUIDOS);
    MI_VALORES   := 'SELECT ''' || UN_NUE_COMPANIA || ''' ,
                            ''' || UN_NUE_CICLO || ''' ,
                            ''' || UN_NUE_CODIGORUTA || ''' ,
                            '   || MI_CAMPOS || ',
                            SYSDATE,'''||UN_USUARIO||'''
                     FROM   SP_ABONOS
                     WHERE  SP_ABONOS.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                       AND  SP_ABONOS.CICLO      = ''' || UN_ANT_CICLO || '''
                       AND  SP_ABONOS.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || '''';
    MI_CAMPOS    :=MI_CAMPOS||',DATE_CREATED,CREATED_BY';
    MI_CAMPOS    :=MI_EXCLUIDOS|| ',' || MI_CAMPOS  ;
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                      UN_ACCION  => 'IS',
                                      UN_CAMPOS  => MI_CAMPOS,
                                      UN_VALORES => MI_VALORES);
    MI_TABLA     := 'SP_D_ABONOS';
    MI_CAMPOS    := 'SP_D_ABONOS.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_D_ABONOS.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_D_ABONOS.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_D_ABONOS.DATE_MODIFIED=SYSDATE,
                     SP_D_ABONOS.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '    SP_D_ABONOS.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                     AND SP_D_ABONOS.CICLO      = ''' || UN_ANT_CICLO || '''
                     AND SP_D_ABONOS.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_R_ABONO';
    MI_CAMPOS    := 'SP_R_ABONO.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_R_ABONO.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_R_ABONO.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_R_ABONO.DATE_MODIFIED=SYSDATE,
                     SP_R_ABONO.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_R_ABONO.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_R_ABONO.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_R_ABONO.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_ACTACAMBIOMEDIDOR';
    MI_CAMPOS    := 'SP_ACTACAMBIOMEDIDOR.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_ACTACAMBIOMEDIDOR.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_ACTACAMBIOMEDIDOR.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_ACTACAMBIOMEDIDOR.DATE_MODIFIED=SYSDATE,
                     SP_ACTACAMBIOMEDIDOR.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_ACTACAMBIOMEDIDOR.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_ACTACAMBIOMEDIDOR.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_ACTACAMBIOMEDIDOR.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_ACTASDESUSPENSION';
    MI_CAMPOS    := 'SP_ACTASDESUSPENSION.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_ACTASDESUSPENSION.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_ACTASDESUSPENSION.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_ACTASDESUSPENSION.DATE_MODIFIED=SYSDATE,
                     SP_ACTASDESUSPENSION.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_ACTASDESUSPENSION.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_ACTASDESUSPENSION.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_ACTASDESUSPENSION.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_ACTAS_RR';
    MI_CAMPOS    := 'SP_ACTAS_RR.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_ACTAS_RR.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_ACTAS_RR.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_ACTAS_RR.DATE_MODIFIED=SYSDATE,
                     SP_ACTAS_RR.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_ACTAS_RR.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_ACTAS_RR.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_ACTAS_RR.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_CERTIFICADOSESTRATIFICACION';
    MI_CAMPOS    := 'SP_CERTIFICADOSESTRATIFICACION.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_CERTIFICADOSESTRATIFICACION.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_CERTIFICADOSESTRATIFICACION.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_CERTIFICADOSESTRATIFICACION.DATE_MODIFIED=SYSDATE,
                     SP_CERTIFICADOSESTRATIFICACION.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_CERTIFICADOSESTRATIFICACION.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_CERTIFICADOSESTRATIFICACION.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_CERTIFICADOSESTRATIFICACION.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_COBROSJURIDICOS';
    MI_CAMPOS    := 'SP_COBROSJURIDICOS.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_COBROSJURIDICOS.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_COBROSJURIDICOS.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_COBROSJURIDICOS.DATE_MODIFIED=SYSDATE,
                     SP_COBROSJURIDICOS.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_COBROSJURIDICOS.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_COBROSJURIDICOS.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_COBROSJURIDICOS.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_COBROSPERSUASIVOS';
    MI_CAMPOS    := 'SP_COBROSPERSUASIVOS.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_COBROSPERSUASIVOS.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_COBROSPERSUASIVOS.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_COBROSPERSUASIVOS.DATE_MODIFIED=SYSDATE,
                     SP_COBROSPERSUASIVOS.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_COBROSPERSUASIVOS.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_COBROSPERSUASIVOS.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_COBROSPERSUASIVOS.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_DESVIACIONES';
    MI_CAMPOS    := 'SP_DESVIACIONES.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_DESVIACIONES.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_DESVIACIONES.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_DESVIACIONES.DATE_MODIFIED=SYSDATE,
                     SP_DESVIACIONES.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_DESVIACIONES.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_DESVIACIONES.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_DESVIACIONES.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_DESVIACIONES_MODIFICACION';
    MI_CAMPOS    := 'SP_DESVIACIONES_MODIFICACION.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_DESVIACIONES_MODIFICACION.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_DESVIACIONES_MODIFICACION.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_DESVIACIONES_MODIFICACION.DATE_MODIFIED=SYSDATE,
                     SP_DESVIACIONES_MODIFICACION.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_DESVIACIONES_MODIFICACION.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_DESVIACIONES_MODIFICACION.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_DESVIACIONES_MODIFICACION.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_DETALLEDISPOSICION';
    MI_CAMPOS    := 'SP_DETALLEDISPOSICION.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_DETALLEDISPOSICION.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_DETALLEDISPOSICION.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_DETALLEDISPOSICION.DATE_MODIFIED=SYSDATE,
                     SP_DETALLEDISPOSICION.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_DETALLEDISPOSICION.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_DETALLEDISPOSICION.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_DETALLEDISPOSICION.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_D_DEUDAFACTURADAFINANCIADA';
    MI_CAMPOS    := 'SP_D_DEUDAFACTURADAFINANCIADA.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_D_DEUDAFACTURADAFINANCIADA.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_D_DEUDAFACTURADAFINANCIADA.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_D_DEUDAFACTURADAFINANCIADA.DATE_MODIFIED=SYSDATE,
                     SP_D_DEUDAFACTURADAFINANCIADA.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_D_DEUDAFACTURADAFINANCIADA.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_D_DEUDAFACTURADAFINANCIADA.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_D_DEUDAFACTURADAFINANCIADA.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_D_PAGOSDOBLES';
    MI_CAMPOS    := 'SP_D_PAGOSDOBLES.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_D_PAGOSDOBLES.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_D_PAGOSDOBLES.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_D_PAGOSDOBLES.DATE_MODIFIED=SYSDATE,
                     SP_D_PAGOSDOBLES.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_D_PAGOSDOBLES.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_D_PAGOSDOBLES.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_D_PAGOSDOBLES.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_ERRORCALCULO';
    MI_CAMPOS    := 'SP_ERRORCALCULO.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_ERRORCALCULO.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_ERRORCALCULO.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_ERRORCALCULO.DATE_MODIFIED=SYSDATE,
                     SP_ERRORCALCULO.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_ERRORCALCULO.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_ERRORCALCULO.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_ERRORCALCULO.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_ESTADISTICAS_CONSUMOACUM';
    MI_CAMPOS    := 'SP_ESTADISTICAS_CONSUMOACUM.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_ESTADISTICAS_CONSUMOACUM.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_ESTADISTICAS_CONSUMOACUM.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_ESTADISTICAS_CONSUMOACUM.DATE_MODIFIED=SYSDATE,
                     SP_ESTADISTICAS_CONSUMOACUM.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_ESTADISTICAS_CONSUMOACUM.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_ESTADISTICAS_CONSUMOACUM.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_ESTADISTICAS_CONSUMOACUM.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_ESTADISTICAS_FACTURACIONESP';
    MI_CAMPOS    := 'SP_ESTADISTICAS_FACTURACIONESP.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_ESTADISTICAS_FACTURACIONESP.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_ESTADISTICAS_FACTURACIONESP.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_ESTADISTICAS_FACTURACIONESP.DATE_MODIFIED=SYSDATE,
                     SP_ESTADISTICAS_FACTURACIONESP.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := 'SP_ESTADISTICAS_FACTURACIONESP.COMPANIA      = ''' || UN_ANT_COMPANIA || '''
                    AND SP_ESTADISTICAS_FACTURACIONESP.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_ESTADISTICAS_FACTURACIONESP.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_FACTURADO';
    MI_CAMPOS    := 'SP_FACTURADO.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_FACTURADO.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_FACTURADO.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_FACTURADO.DATE_MODIFIED=SYSDATE,
                     SP_FACTURADO.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_FACTURADO.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_FACTURADO.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_FACTURADO.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_FACTURADO_EXTERNO';
    MI_CAMPOS    := 'SP_FACTURADO_EXTERNO.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_FACTURADO_EXTERNO.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_FACTURADO_EXTERNO.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_FACTURADO_EXTERNO.DATE_MODIFIED=SYSDATE,
                     SP_FACTURADO_EXTERNO.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_FACTURADO_EXTERNO.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_FACTURADO_EXTERNO.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_FACTURADO_EXTERNO.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_FINANCIABLES';
    MI_CAMPOS    := 'SP_FINANCIABLES.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_FINANCIABLES.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_FINANCIABLES.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_FINANCIABLES.DATE_MODIFIED=SYSDATE,
                     SP_FINANCIABLES.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_FINANCIABLES.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_FINANCIABLES.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_FINANCIABLES.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_FINANCIABLESDEDEUDA';
    MI_CAMPOS    := 'SP_FINANCIABLESDEDEUDA.COMPANIA = ''' || UN_NUE_COMPANIA || ''',
                     SP_FINANCIABLESDEDEUDA.CICLO    = ''' || UN_NUE_CICLO || ''',
                     SP_FINANCIABLESDEDEUDA.USUARIO  = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_FINANCIABLESDEDEUDA.DATE_MODIFIED=SYSDATE,
                     SP_FINANCIABLESDEDEUDA.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_FINANCIABLESDEDEUDA.COMPANIA = ''' || UN_ANT_COMPANIA || '''
                    AND SP_FINANCIABLESDEDEUDA.CICLO    = ''' || UN_ANT_CICLO || '''
                    AND SP_FINANCIABLESDEDEUDA.USUARIO  = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_FRAUDES';
    MI_CAMPOS    := 'SP_FRAUDES.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_FRAUDES.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_FRAUDES.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_FRAUDES.DATE_MODIFIED=SYSDATE,
                     SP_FRAUDES.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_FRAUDES.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_FRAUDES.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_FRAUDES.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_HISTORIA';
    MI_CAMPOS    := 'SP_HISTORIA.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_HISTORIA.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_HISTORIA.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_HISTORIA.DATE_MODIFIED=SYSDATE,
                     SP_HISTORIA.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_HISTORIA.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_HISTORIA.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_HISTORIA.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || '''';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_HISTORIA_CONVENIOS';
    MI_CAMPOS    := 'SP_HISTORIA_CONVENIOS.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_HISTORIA_CONVENIOS.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_HISTORIA_CONVENIOS.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_HISTORIA_CONVENIOS.DATE_MODIFIED=SYSDATE,
                     SP_HISTORIA_CONVENIOS.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_HISTORIA_CONVENIOS.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_HISTORIA_CONVENIOS.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_HISTORIA_CONVENIOS.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_HISTORIA_COPIAS';
    MI_CAMPOS    := 'SP_HISTORIA_COPIAS.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_HISTORIA_COPIAS.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_HISTORIA_COPIAS.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_HISTORIA_COPIAS.DATE_MODIFIED=SYSDATE,
                     SP_HISTORIA_COPIAS.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_HISTORIA_COPIAS.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_HISTORIA_COPIAS.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_HISTORIA_COPIAS.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_HISTORIA_EXTERNA';
    MI_CAMPOS    := 'SP_HISTORIA_EXTERNA.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_HISTORIA_EXTERNA.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_HISTORIA_EXTERNA.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_HISTORIA_EXTERNA.DATE_MODIFIED=SYSDATE,
                     SP_HISTORIA_EXTERNA.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_HISTORIA_EXTERNA.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_HISTORIA_EXTERNA.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_HISTORIA_EXTERNA.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_HISTORIA_EXTERNA_DESACTIVA';
    MI_CAMPOS    := 'SP_HISTORIA_EXTERNA_DESACTIVA.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_HISTORIA_EXTERNA_DESACTIVA.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_HISTORIA_EXTERNA_DESACTIVA.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_HISTORIA_EXTERNA_DESACTIVA.DATE_MODIFIED=SYSDATE,
                     SP_HISTORIA_EXTERNA_DESACTIVA.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_HISTORIA_EXTERNA_DESACTIVA.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_HISTORIA_EXTERNA_DESACTIVA.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_HISTORIA_EXTERNA_DESACTIVA.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_HISTORICOFACTURA';
    MI_CAMPOS    := 'SP_HISTORICOFACTURA.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_HISTORICOFACTURA.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_HISTORICOFACTURA.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_HISTORICOFACTURA.DATE_MODIFIED=SYSDATE,
                     SP_HISTORICOFACTURA.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_HISTORICOFACTURA.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_HISTORICOFACTURA.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_HISTORICOFACTURA.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_INFO_TIPO_OPERACION';
    MI_CAMPOS    := 'SP_INFO_TIPO_OPERACION.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_INFO_TIPO_OPERACION.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_INFO_TIPO_OPERACION.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_INFO_TIPO_OPERACION.DATE_MODIFIED=SYSDATE,
                     SP_INFO_TIPO_OPERACION.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_INFO_TIPO_OPERACION.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_INFO_TIPO_OPERACION.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_INFO_TIPO_OPERACION.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_MEDIDOR';
    MI_CAMPOS    := 'SP_MEDIDOR.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_MEDIDOR.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_MEDIDOR.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_MEDIDOR.DATE_MODIFIED=SYSDATE,
                     SP_MEDIDOR.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_MEDIDOR.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_MEDIDOR.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_MEDIDOR.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_MICROMEDICIONES';
    MI_CAMPOS    := 'SP_MICROMEDICIONES.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_MICROMEDICIONES.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_MICROMEDICIONES.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_MICROMEDICIONES.DATE_MODIFIED=SYSDATE,
                     SP_MICROMEDICIONES.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_MICROMEDICIONES.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_MICROMEDICIONES.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_MICROMEDICIONES.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_MICROMEDICION_CAMBIO';
    MI_CAMPOS    := 'SP_MICROMEDICION_CAMBIO.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_MICROMEDICION_CAMBIO.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_MICROMEDICION_CAMBIO.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_MICROMEDICION_CAMBIO.DATE_MODIFIED=SYSDATE,
                     SP_MICROMEDICION_CAMBIO.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_MICROMEDICION_CAMBIO.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_MICROMEDICION_CAMBIO.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_MICROMEDICION_CAMBIO.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_MODIFICACIONES';
    MI_CAMPOS    := 'SP_MODIFICACIONES.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_MODIFICACIONES.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_MODIFICACIONES.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_MODIFICACIONES.DATE_MODIFIED=SYSDATE,
                     SP_MODIFICACIONES.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_MODIFICACIONES.COMPANIA = ''' || UN_ANT_COMPANIA || '''
                    AND SP_MODIFICACIONES.CICLO = ''' || UN_ANT_CICLO || '''
                    AND SP_MODIFICACIONES.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_MODIFICACIONESDEUDA';
    MI_CAMPOS    := 'SP_MODIFICACIONESDEUDA.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_MODIFICACIONESDEUDA.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_MODIFICACIONESDEUDA.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_MODIFICACIONESDEUDA.DATE_MODIFIED=SYSDATE,
                     SP_MODIFICACIONESDEUDA.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_MODIFICACIONESDEUDA.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_MODIFICACIONESDEUDA.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_MODIFICACIONESDEUDA.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_MULTIUSUARIOS';
    MI_CAMPOS    := 'SP_MULTIUSUARIOS.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_MULTIUSUARIOS.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_MULTIUSUARIOS.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_MULTIUSUARIOS.DATE_MODIFIED=SYSDATE,
                     SP_MULTIUSUARIOS.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_MULTIUSUARIOS.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_MULTIUSUARIOS.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_MULTIUSUARIOS.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_ORDENTRABAJO';
    MI_CAMPOS    := 'SP_ORDENTRABAJO.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_ORDENTRABAJO.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_ORDENTRABAJO.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_ORDENTRABAJO.DATE_MODIFIED=SYSDATE,
                     SP_ORDENTRABAJO.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_ORDENTRABAJO.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_ORDENTRABAJO.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_ORDENTRABAJO.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_PAGO';
    MI_CAMPOS    := 'SP_PAGO.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_PAGO.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_PAGO.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_PAGO.DATE_MODIFIED=SYSDATE,
                     SP_PAGO.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_PAGO.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_PAGO.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_PAGO.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_D_RECAUDO_USUARIO';
    MI_CAMPOS    := 'COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     CICLO      = ''' || UN_NUE_CICLO || ''',
                     CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     DATE_MODIFIED = SYSDATE,
                     MODIFIED_BY='''||UN_USUARIO||'''';

    MI_CONDICION := '   COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                      AND CICLO      = ''' || UN_ANT_CICLO || '''
                      AND CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                        UN_ACCION    => 'M',
                                        UN_CAMPOS    => MI_CAMPOS,
                                        UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_PARAMETROFACTURACION';
    MI_CAMPOS    := 'SP_PARAMETROFACTURACION.COMPANIA    = ''' || UN_NUE_COMPANIA || ''',
                     SP_PARAMETROFACTURACION.CICLO       = ''' || UN_NUE_CICLO || ''',
                     SP_PARAMETROFACTURACION.CODIGOFINAL = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_PARAMETROFACTURACION.DATE_MODIFIED=SYSDATE,
                     SP_PARAMETROFACTURACION.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_PARAMETROFACTURACION.COMPANIA    = ''' || UN_ANT_COMPANIA || '''
                    AND SP_PARAMETROFACTURACION.CICLO       = ''' || UN_ANT_CICLO || '''
                    AND SP_PARAMETROFACTURACION.CODIGOFINAL = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_PARAMETROFACTURACION';
    MI_CAMPOS    := 'SP_PARAMETROFACTURACION.COMPANIA      = ''' || UN_NUE_COMPANIA || ''',
                     SP_PARAMETROFACTURACION.CICLO         = ''' || UN_NUE_CICLO || ''',
                     SP_PARAMETROFACTURACION.CODIGOINICIAL = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_PARAMETROFACTURACION.DATE_MODIFIED=SYSDATE,
                     SP_PARAMETROFACTURACION.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_PARAMETROFACTURACION.COMPANIA      = ''' || UN_ANT_COMPANIA || '''
                    AND SP_PARAMETROFACTURACION.CICLO         = ''' || UN_ANT_CICLO || '''
                    AND SP_PARAMETROFACTURACION.CODIGOINICIAL = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_PLANOSDIGITAL';
    MI_CAMPOS    := 'SP_PLANOSDIGITAL.COMPANIA = ''' || UN_NUE_COMPANIA || ''',
                     SP_PLANOSDIGITAL.CICLO    = ''' || UN_NUE_CICLO || ''',
                     SP_PLANOSDIGITAL.MAECOD   = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_PLANOSDIGITAL.DATE_MODIFIED=SYSDATE,
                     SP_PLANOSDIGITAL.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_PLANOSDIGITAL.COMPANIA = ''' || UN_ANT_COMPANIA || '''
                    AND SP_PLANOSDIGITAL.CICLO    = ''' || UN_ANT_CICLO || '''
                    AND SP_PLANOSDIGITAL.MAECOD   = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_PLANOSDIGITALENVIO';
    MI_CAMPOS    := 'SP_PLANOSDIGITALENVIO.COMPANIA    = ''' || UN_NUE_COMPANIA || ''',
                     SP_PLANOSDIGITALENVIO.CICLO       = ''' || UN_NUE_CICLO || ''',
                     SP_PLANOSDIGITALENVIO.CODIGOFINAL = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_PLANOSDIGITALENVIO.DATE_MODIFIED=SYSDATE,
                     SP_PLANOSDIGITALENVIO.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_PLANOSDIGITALENVIO.COMPANIA    = ''' || UN_ANT_COMPANIA || '''
                    AND SP_PLANOSDIGITALENVIO.CICLO       = ''' || UN_ANT_CICLO || '''
                    AND SP_PLANOSDIGITALENVIO.CODIGOFINAL = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_PLANOSDIGITALENVIO';
    MI_CAMPOS    := 'SP_PLANOSDIGITALENVIO.COMPANIA      = ''' || UN_NUE_COMPANIA || ''',
                     SP_PLANOSDIGITALENVIO.CICLO         = ''' || UN_NUE_CICLO || ''',
                     SP_PLANOSDIGITALENVIO.CODIGOINICIAL = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_PLANOSDIGITALENVIO.DATE_MODIFIED=SYSDATE,
                     SP_PLANOSDIGITALENVIO.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_PLANOSDIGITALENVIO.COMPANIA      = ''' || UN_ANT_COMPANIA || '''
                    AND SP_PLANOSDIGITALENVIO.CICLO         = ''' || UN_ANT_CICLO || '''
                    AND SP_PLANOSDIGITALENVIO.CODIGOINICIAL = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_RECAUDOPERATRASO';
    MI_CAMPOS    := 'SP_RECAUDOPERATRASO.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_RECAUDOPERATRASO.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_RECAUDOPERATRASO.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_RECAUDOPERATRASO.DATE_MODIFIED=SYSDATE,
                     SP_RECAUDOPERATRASO.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_RECAUDOPERATRASO.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_RECAUDOPERATRASO.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_RECAUDOPERATRASO.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_RECAUDO_PRODUCTIVIDAD';
    MI_CAMPOS    := 'SP_RECAUDO_PRODUCTIVIDAD.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_RECAUDO_PRODUCTIVIDAD.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_RECAUDO_PRODUCTIVIDAD.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_RECAUDO_PRODUCTIVIDAD.DATE_MODIFIED=SYSDATE,
                     SP_RECAUDO_PRODUCTIVIDAD.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_RECAUDO_PRODUCTIVIDAD.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_RECAUDO_PRODUCTIVIDAD.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_RECAUDO_PRODUCTIVIDAD.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_TBLHIST_SALDO_CREDITO';
    MI_CAMPOS    := 'SP_TBLHIST_SALDO_CREDITO.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_TBLHIST_SALDO_CREDITO.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_TBLHIST_SALDO_CREDITO.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_TBLHIST_SALDO_CREDITO.DATE_MODIFIED=SYSDATE,
                     SP_TBLHIST_SALDO_CREDITO.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_TBLHIST_SALDO_CREDITO.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_TBLHIST_SALDO_CREDITO.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_TBLHIST_SALDO_CREDITO.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_TBL_HIST_NOVEDADES_USU';
    MI_CAMPOS    := 'SP_TBL_HIST_NOVEDADES_USU.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_TBL_HIST_NOVEDADES_USU.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_TBL_HIST_NOVEDADES_USU.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_TBL_HIST_NOVEDADES_USU.DATE_MODIFIED=SYSDATE,
                     SP_TBL_HIST_NOVEDADES_USU.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_TBL_HIST_NOVEDADES_USU.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_TBL_HIST_NOVEDADES_USU.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_TBL_HIST_NOVEDADES_USU.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_TMP_USUARIOS_PESOASEO';
    MI_CAMPOS    := 'SP_TMP_USUARIOS_PESOASEO.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_TMP_USUARIOS_PESOASEO.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_TMP_USUARIOS_PESOASEO.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_TMP_USUARIOS_PESOASEO.DATE_MODIFIED=SYSDATE,
                     SP_TMP_USUARIOS_PESOASEO.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_TMP_USUARIOS_PESOASEO.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_TMP_USUARIOS_PESOASEO.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_TMP_USUARIOS_PESOASEO.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_UNIDADESRESIDENCIALES';
    MI_EXCLUIDOS := 'COMPANIA,'||
                     'CICLO,'||
                     'CODIGORUTA';
    MI_CAMPOS    := PCK_SYSMAN_UTL.FC_LISTA_CAMPOS(UN_TABLA     => 'SP_UNIDADESRESIDENCIALES',
                                                   UN_EXCLUIDOS => MI_EXCLUIDOS);
    MI_VALORES   := 'SELECT ''' || UN_NUE_COMPANIA || ''',
                            ''' || UN_NUE_CICLO || ''',
                            ''' || UN_NUE_CODIGORUTA || ''',
                            '   || MI_CAMPOS || ',
                            SYSDATE,'''||UN_USUARIO||'''
                     FROM   SP_UNIDADESRESIDENCIALES
                     WHERE  SP_UNIDADESRESIDENCIALES.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                       AND  SP_UNIDADESRESIDENCIALES.CICLO      = ''' || UN_ANT_CICLO || '''
                       AND  SP_UNIDADESRESIDENCIALES.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || '''';

    MI_CAMPOS:=MI_CAMPOS||',DATE_CREATED,CREATED_BY';

    MI_CAMPOS    := MI_EXCLUIDOS|| ',' ||MI_CAMPOS  ;
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                      UN_ACCION  => 'IS',
                                      UN_CAMPOS  => MI_CAMPOS,
                                      UN_VALORES => MI_VALORES);

    MI_TABLA     := 'SP_HISTORIA_EXTERNA';
    MI_CAMPOS    := 'SP_HISTORIA_EXTERNA.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_HISTORIA_EXTERNA.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_HISTORIA_EXTERNA.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_HISTORIA_EXTERNA.DATE_MODIFIED=SYSDATE,
                     SP_HISTORIA_EXTERNA.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_HISTORIA_EXTERNA.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_HISTORIA_EXTERNA.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_HISTORIA_EXTERNA.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_HISTORIA_EXTERNA_DESACTIVA';
    MI_CAMPOS    := 'SP_HISTORIA_EXTERNA_DESACTIVA.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_HISTORIA_EXTERNA_DESACTIVA.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_HISTORIA_EXTERNA_DESACTIVA.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_HISTORIA_EXTERNA_DESACTIVA.DATE_MODIFIED=SYSDATE,
                     SP_HISTORIA_EXTERNA_DESACTIVA.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_HISTORIA_EXTERNA_DESACTIVA.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_HISTORIA_EXTERNA_DESACTIVA.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_HISTORIA_EXTERNA_DESACTIVA.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_USUARIO_ABONO';
    MI_CAMPOS    := 'SP_USUARIO_ABONO.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_USUARIO_ABONO.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_USUARIO_ABONO.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_USUARIO_ABONO.DATE_MODIFIED=SYSDATE,
                     SP_USUARIO_ABONO.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_USUARIO_ABONO.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_USUARIO_ABONO.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_USUARIO_ABONO.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_USUARIO_DESCU';
    MI_CAMPOS    := 'SP_USUARIO_DESCU.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_USUARIO_DESCU.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_USUARIO_DESCU.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_USUARIO_DESCU.DATE_MODIFIED=SYSDATE,
                     SP_USUARIO_DESCU.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_USUARIO_DESCU.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_USUARIO_DESCU.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_USUARIO_DESCU.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_USUARIO_HISTORICOS';
    MI_CAMPOS    := 'SP_USUARIO_HISTORICOS.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_USUARIO_HISTORICOS.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_USUARIO_HISTORICOS.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_USUARIO_HISTORICOS.DATE_MODIFIED=SYSDATE,
                     SP_USUARIO_HISTORICOS.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_USUARIO_HISTORICOS.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_USUARIO_HISTORICOS.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_USUARIO_HISTORICOS.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_USUARIO_PROBLEMA';
    MI_CAMPOS    := 'SP_USUARIO_PROBLEMA.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_USUARIO_PROBLEMA.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_USUARIO_PROBLEMA.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_USUARIO_PROBLEMA.DATE_MODIFIED=SYSDATE,
                     SP_USUARIO_PROBLEMA.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_USUARIO_PROBLEMA.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_USUARIO_PROBLEMA.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_USUARIO_PROBLEMA.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || '''';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_USUARIO_PRODUC';
    MI_CAMPOS    := 'SP_USUARIO_PRODUC.COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                     SP_USUARIO_PRODUC.CICLO      = ''' || UN_NUE_CICLO || ''',
                     SP_USUARIO_PRODUC.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                     SP_USUARIO_PRODUC.DATE_MODIFIED=SYSDATE,
                     SP_USUARIO_PRODUC.MODIFIED_BY='''||UN_USUARIO||'''';
    MI_CONDICION := '   SP_USUARIO_PRODUC.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_USUARIO_PRODUC.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_USUARIO_PRODUC.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);



    MI_TABLA     := 'SP_AUDITORIAMNUMOD';
    MI_CAMPOS    := 'COMPANIA   = ''' || UN_NUE_COMPANIA || ''',
                   CICLO      = ''' || UN_NUE_CICLO || ''',
                   CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                   DATE_MODIFIED=SYSDATE,
                   MODIFIED_BY='''||UN_USUARIO||'''';

    MI_CONDICION := '   COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                  AND CICLO      = ''' || UN_ANT_CICLO || '''
                  AND CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                    UN_ACCION    => 'M',
                                    UN_CAMPOS    => MI_CAMPOS,
                                    UN_CONDICION => MI_CONDICION);


    MI_TABLA     := 'SP_UNIDADESRESIDENCIALES';
    MI_CONDICION := '   SP_UNIDADESRESIDENCIALES.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_UNIDADESRESIDENCIALES.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_UNIDADESRESIDENCIALES.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'E',
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_ABONOS';
    MI_CONDICION := '   SP_ABONOS.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_ABONOS.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_ABONOS.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'E',
                                      UN_CONDICION => MI_CONDICION);

    MI_TABLA     := 'SP_USUARIO';
    MI_CONDICION := '   SP_USUARIO.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                    AND SP_USUARIO.CICLO      = ''' || UN_ANT_CICLO || '''
                    AND SP_USUARIO.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || '''';
    MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'E',
                                      UN_CONDICION => MI_CONDICION);


    MI_TABLA :='SP_USUARIO';
    MI_CAMPOS:='MODIFIED_BY='''||UN_USUARIO||''','||
               'DATE_MODIFIED=SYSDATE,'||
               'CAMBIOCICLORUTA=0';

    MI_CONDICION:='COMPANIA ='''||UN_ANT_COMPANIA||''' AND CODIGORUTA='''|| UN_NUE_CODIGORUTA || '''';

    MI_PCKDATOS:=PCK_DATOS.FC_ACME(UN_TABLA     =>MI_TABLA,
                                   UN_ACCION    =>'M',
                                   UN_CAMPOS    =>MI_CAMPOS,
                                   UN_CONDICION =>MI_CONDICION);


    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
      RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
    WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
      RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
    WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
      RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
  END;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
    MI_MSGERROR(0).CLAVE := 'ETAPA';
    MI_MSGERROR(0).VALOR := MI_TABLA;
    PCK_ERR_MSG.RAISE_WITH_MSG(
      UN_EXC_COD =>SQLCODE,
      UN_REEMPLAZOS => MI_MSGERROR,
      UN_ERROR_COD=>PCK_ERRORES.ERR_FACSERP_CAMB_RUTA_GEN
    );
END PR_CAMBIORUTA;

  -- 6
FUNCTION FC_CAMBIOCICLO
(
  /*
    NAME              : FC_CAMBIOCICLO --> EN ACCESS CAMBIOCICLO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 24/08/2016
    TIME              : 04:45 PM
    SOURCE MODULE     : SysmanSp2016.05.04
    MODIFIER          : YEISSON ALEJANDRO ROJAS RUIZ
    DATE MODIFIED     : 05/01/2017
    TIME              : 11:00 AM
    MODIFICATIONS     : CORRECCIÓN SEGÚN ESTÁNDAR DE PROGRAMACIÓN Y OPTIMIZACIÓN DEL MANEJO DE ERRORES.
    DESCRIPTION       : Función que realiza el proceso de actualizar el ciclo de un usuario.
    PARAMETERS        : UN_NUE_COMPANIA   => CÓDIGO DE LA COMPAÑÍA NUEVA.
                        UN_COMPANIA       => CÓDIGO DE LA COMPAÑÍA QUE SE VA A ACTUALIZAR.
                        UN_NUE_CICLO      => NÚMERO DEL CICLO NUEVO.
                        UN_ANT_CICLO      => CICLO QUE SE VA A ACTUALIZAR.
                        UN_NUE_CODIGORUTA => NÚMERO DEL CÓDIGO RUTA NUEVO.
                        UN_ANT_CODIGORUTA => CÓDIGO RUTA QUE SE VA A ACTUALIZAR.
                        UN_ANO            => AÑO QUE SE VA A INSERTAR.
                        UN_PERIODO        => PERIODO QUE SE VA A INSERTAR.

    @NAME:    cambiarCiclo
    @METHOD:  GET
*/
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_NUE_CICLO      IN PCK_SUBTIPOS.TI_CICLO,
  UN_NUE_CODIGORUTA IN PCK_SUBTIPOS.TI_CODIGORUTA,
  UN_ANO            IN PCK_SUBTIPOS.TI_ANIO,
  UN_PERIODO        IN PCK_SUBTIPOS.TI_PERIODO,
  UN_ANT_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANT_CICLO      IN PCK_SUBTIPOS.TI_CICLO,
  UN_ANT_CODIGORUTA IN PCK_SUBTIPOS.TI_CODIGORUTA,
  UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN VARCHAR2 AS
  MI_TABLA          PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
  MI_EXCLUIDOS      PCK_SUBTIPOS.TI_EXCLUIDOS;
  MI_REEMPLAZOS 		PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN
    MI_REEMPLAZOS(0).CLAVE 	:= 'CODIGO';
          MI_REEMPLAZOS(0).VALOR	:= UN_ANT_CODIGORUTA;
    MI_TABLA         := 'SP_USUARIO';
    MI_EXCLUIDOS     := 'COMPANIA,CICLO,CODIGORUTA,ANO,PERIODO,CREATED_BY ,DATE_CREATED';
    MI_CAMPOS        := PCK_SYSMAN_UTL.FC_LISTA_CAMPOS(UN_TABLA     => 'SP_USUARIO',
                                                       UN_EXCLUIDOS => MI_EXCLUIDOS);
    MI_VALORES       := 'SELECT ''' || UN_COMPANIA || ''',
                                ''' || UN_NUE_CICLO || ''',
                                ''' || UN_NUE_CODIGORUTA || ''',
                                ''' || UN_ANO || ''',
                                ''' || UN_PERIODO || ''',
                                ''' || UN_USUARIO || ''',
                                SYSDATE,
                                '   || MI_CAMPOS || '
                         FROM   SP_USUARIO
                         WHERE  SP_USUARIO.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                           AND  SP_USUARIO.CICLO      = ''' || UN_ANT_CICLO || '''
                           AND  SP_USUARIO.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || '''';
    MI_CAMPOS        := MI_EXCLUIDOS || ',' ||MI_CAMPOS ;

    MI_TABLA :='SP_USUARIO';
    MI_CAMPOS:='MODIFIED_BY='''||UN_USUARIO||''','||
               'DATE_MODIFIED=SYSDATE,'||
               'CAMBIOCICLORUTA=-1';

    MI_CONDICION:='COMPANIA ='''||UN_COMPANIA||''' AND CODIGORUTA IN('''|| UN_NUE_CODIGORUTA || ''','||
                  ''''|| UN_ANT_COMPANIA || ''')';

    PCK_DATOS.GL_RTA :=PCK_DATOS.FC_ACME(UN_TABLA     =>MI_TABLA,
                                   UN_ACCION    =>'M',
                                   UN_CAMPOS    =>MI_CAMPOS,
                                   UN_CONDICION =>MI_CONDICION);


    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                              UN_ACCION  => 'IS',
                                              UN_CAMPOS  => MI_CAMPOS,
                                              UN_VALORES => MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_INSUSUARIOCICLO
                    );
    END;

    MI_TABLA         := 'SP_ABONOS';
    MI_EXCLUIDOS     := 'COMPANIA,CICLO,CODIGORUTA,CREATED_BY ,DATE_CREATED';
    MI_CAMPOS        := PCK_SYSMAN_UTL.FC_LISTA_CAMPOS(UN_TABLA     => 'SP_ABONOS',
                                                       UN_EXCLUIDOS => MI_EXCLUIDOS);
    MI_VALORES       := 'SELECT ''' || UN_COMPANIA || ''' ,
                                ''' || UN_NUE_CICLO || ''' ,
                                ''' || UN_NUE_CODIGORUTA || ''' ,
                                ''' || UN_USUARIO || ''',
                                SYSDATE,
                                '   || MI_CAMPOS || '
                         FROM   SP_ABONOS
                         WHERE  SP_ABONOS.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                           AND  SP_ABONOS.CICLO      = ''' || UN_ANT_CICLO || '''
                           AND  SP_ABONOS.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || '''';
    MI_CAMPOS        := MI_EXCLUIDOS|| ',' ||MI_CAMPOS  ;

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                              UN_ACCION  => 'IS',
                                              UN_CAMPOS  => MI_CAMPOS,
                                              UN_VALORES => MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_INSABONOCICLO
                    );
    END;

    MI_TABLA         := 'SP_D_ABONOS';
    MI_CAMPOS        := 'SP_D_ABONOS.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_D_ABONOS.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_D_ABONOS.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_D_ABONOS.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_D_ABONOS.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_D_ABONOS.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'detalle del abono';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_R_ABONO';
    MI_CAMPOS        := 'SP_R_ABONO.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_R_ABONO.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_R_ABONO.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_R_ABONO.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_R_ABONO.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_R_ABONO.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'abono';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_ACTACAMBIOMEDIDOR';
    MI_CAMPOS        := 'SP_ACTACAMBIOMEDIDOR.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_ACTACAMBIOMEDIDOR.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_ACTACAMBIOMEDIDOR.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_ACTACAMBIOMEDIDOR.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_ACTACAMBIOMEDIDOR.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_ACTACAMBIOMEDIDOR.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'actas de cambio de medidor';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_ACTASDESUSPENSION';
    MI_CAMPOS        := 'SP_ACTASDESUSPENSION.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_ACTASDESUSPENSION.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_ACTASDESUSPENSION.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_ACTASDESUSPENSION.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_ACTASDESUSPENSION.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_ACTASDESUSPENSION.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'actas de suspencion';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_ACTAS_RR';
    MI_CAMPOS        := 'SP_ACTAS_RR.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_ACTAS_RR.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_ACTAS_RR.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_ACTAS_RR.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_ACTAS_RR.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_ACTAS_RR.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'actas';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_CERTIFICADOSESTRATIFICACION';
    MI_CAMPOS        := 'SP_CERTIFICADOSESTRATIFICACION.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_CERTIFICADOSESTRATIFICACION.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_CERTIFICADOSESTRATIFICACION.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_CERTIFICADOSESTRATIFICACION.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_CERTIFICADOSESTRATIFICACION.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_CERTIFICADOSESTRATIFICACION.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'certificados de estratificacion';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_COBROSJURIDICOS';
    MI_CAMPOS        := 'SP_COBROSJURIDICOS.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_COBROSJURIDICOS.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_COBROSJURIDICOS.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_COBROSJURIDICOS.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_COBROSJURIDICOS.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_COBROSJURIDICOS.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'cobros juridicos';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_COBROSPERSUASIVOS';
    MI_CAMPOS        := 'SP_COBROSPERSUASIVOS.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_COBROSPERSUASIVOS.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_COBROSPERSUASIVOS.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_COBROSPERSUASIVOS.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_COBROSPERSUASIVOS.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_COBROSPERSUASIVOS.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'cobros persuasivos';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_DESVIACIONES';
    MI_CAMPOS        := 'SP_DESVIACIONES.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_DESVIACIONES.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_DESVIACIONES.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_DESVIACIONES.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_DESVIACIONES.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_DESVIACIONES.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'desviaciones';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_DESVIACIONES_MODIFICACION';
    MI_CAMPOS        := 'SP_DESVIACIONES_MODIFICACION.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_DESVIACIONES_MODIFICACION.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_DESVIACIONES_MODIFICACION.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_DESVIACIONES_MODIFICACION.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_DESVIACIONES_MODIFICACION.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_DESVIACIONES_MODIFICACION.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'modificaciones de desviacion';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_DETALLEDISPOSICION';
    MI_CAMPOS        := 'SP_DETALLEDISPOSICION.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_DETALLEDISPOSICION.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_DETALLEDISPOSICION.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_DETALLEDISPOSICION.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_DETALLEDISPOSICION.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_DETALLEDISPOSICION.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'detalles de disposicion';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_D_DEUDAFACTURADAFINANCIADA';
    MI_CAMPOS        := 'SP_D_DEUDAFACTURADAFINANCIADA.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_D_DEUDAFACTURADAFINANCIADA.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_D_DEUDAFACTURADAFINANCIADA.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_D_DEUDAFACTURADAFINANCIADA.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_D_DEUDAFACTURADAFINANCIADA.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_D_DEUDAFACTURADAFINANCIADA.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'deudas financiadas';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_D_PAGOSDOBLES';
    MI_CAMPOS        := 'SP_D_PAGOSDOBLES.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_D_PAGOSDOBLES.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_D_PAGOSDOBLES.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_D_PAGOSDOBLES.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_D_PAGOSDOBLES.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_D_PAGOSDOBLES.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'detalles de pagos dobles';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_ERRORCALCULO';
    MI_CAMPOS        := 'SP_ERRORCALCULO.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_ERRORCALCULO.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_ERRORCALCULO.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_ERRORCALCULO.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_ERRORCALCULO.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_ERRORCALCULO.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'errores de calculo';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_ESTADISTICAS_CONSUMOACUM';
    MI_CAMPOS        := 'SP_ESTADISTICAS_CONSUMOACUM.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_ESTADISTICAS_CONSUMOACUM.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_ESTADISTICAS_CONSUMOACUM.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_ESTADISTICAS_CONSUMOACUM.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_ESTADISTICAS_CONSUMOACUM.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_ESTADISTICAS_CONSUMOACUM.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'estadisticas acumuladas de consumo';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_ESTADISTICAS_FACTURACIONESP';
    MI_CAMPOS        := 'SP_ESTADISTICAS_FACTURACIONESP.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_ESTADISTICAS_FACTURACIONESP.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_ESTADISTICAS_FACTURACIONESP.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_ESTADISTICAS_FACTURACIONESP.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_ESTADISTICAS_FACTURACIONESP.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_ESTADISTICAS_FACTURACIONESP.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'estadisticas de facturacion';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_FACTURADO';
    MI_CAMPOS        := 'SP_FACTURADO.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_FACTURADO.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_FACTURADO.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_FACTURADO.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_FACTURADO.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_FACTURADO.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'facturaciones';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_FACTURADO_EXTERNO';
    MI_CAMPOS        := 'SP_FACTURADO_EXTERNO.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_FACTURADO_EXTERNO.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_FACTURADO_EXTERNO.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_FACTURADO_EXTERNO.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_FACTURADO_EXTERNO.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_FACTURADO_EXTERNO.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'facturaciones externas';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_FINANCIABLES';
    MI_CAMPOS        := 'SP_FINANCIABLES.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_FINANCIABLES.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_FINANCIABLES.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_FINANCIABLES.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_FINANCIABLES.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_FINANCIABLES.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'financiables';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_FINANCIABLESDEDEUDA';
    MI_CAMPOS        := 'SP_FINANCIABLESDEDEUDA.COMPANIA = ''' || UN_COMPANIA || ''',
                         SP_FINANCIABLESDEDEUDA.CICLO    = ''' || UN_NUE_CICLO || ''',
                         SP_FINANCIABLESDEDEUDA.USUARIO  = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_FINANCIABLESDEDEUDA.COMPANIA = ''' || UN_ANT_COMPANIA || '''
                        AND SP_FINANCIABLESDEDEUDA.CICLO    = ''' || UN_ANT_CICLO || '''
                        AND SP_FINANCIABLESDEDEUDA.USUARIO  = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'financiables de deuda';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_FRAUDES';
    MI_CAMPOS        := 'SP_FRAUDES.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_FRAUDES.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_FRAUDES.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_FRAUDES.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_FRAUDES.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_FRAUDES.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'fraudes';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_HISTORIA';
    MI_CAMPOS        := 'SP_HISTORIA.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_HISTORIA.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_HISTORIA.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_HISTORIA.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_HISTORIA.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_HISTORIA.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'historias';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_HISTORIA_CONVENIOS';
    MI_CAMPOS        := 'SP_HISTORIA_CONVENIOS.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_HISTORIA_CONVENIOS.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_HISTORIA_CONVENIOS.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_HISTORIA_CONVENIOS.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_HISTORIA_CONVENIOS.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_HISTORIA_CONVENIOS.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'historia de convenios';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_HISTORIA_COPIAS';
    MI_CAMPOS        := 'SP_HISTORIA_COPIAS.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_HISTORIA_COPIAS.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_HISTORIA_COPIAS.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_HISTORIA_COPIAS.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_HISTORIA_COPIAS.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_HISTORIA_COPIAS.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'historia de copias';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_HISTORIA_EXTERNA';
    MI_CAMPOS        := 'SP_HISTORIA_EXTERNA.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_HISTORIA_EXTERNA.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_HISTORIA_EXTERNA.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_HISTORIA_EXTERNA.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_HISTORIA_EXTERNA.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_HISTORIA_EXTERNA.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'historia externa';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_HISTORIA_EXTERNA_DESACTIVA';
    MI_CAMPOS        := 'SP_HISTORIA_EXTERNA_DESACTIVA.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_HISTORIA_EXTERNA_DESACTIVA.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_HISTORIA_EXTERNA_DESACTIVA.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_HISTORIA_EXTERNA_DESACTIVA.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_HISTORIA_EXTERNA_DESACTIVA.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_HISTORIA_EXTERNA_DESACTIVA.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'historia externa inactiva';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_HISTORICOFACTURA';
    MI_CAMPOS        := 'SP_HISTORICOFACTURA.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_HISTORICOFACTURA.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_HISTORICOFACTURA.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_HISTORICOFACTURA.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_HISTORICOFACTURA.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_HISTORICOFACTURA.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'historico de factura';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_INFO_TIPO_OPERACION';
    MI_CAMPOS        := 'SP_INFO_TIPO_OPERACION.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_INFO_TIPO_OPERACION.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_INFO_TIPO_OPERACION.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_INFO_TIPO_OPERACION.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_INFO_TIPO_OPERACION.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_INFO_TIPO_OPERACION.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'tipos de operacion';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_MEDIDOR';
    MI_CAMPOS        := 'SP_MEDIDOR.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_MEDIDOR.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_MEDIDOR.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_MEDIDOR.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_MEDIDOR.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_MEDIDOR.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'medidor';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_MICROMEDICIONES';
    MI_CAMPOS        := 'SP_MICROMEDICIONES.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_MICROMEDICIONES.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_MICROMEDICIONES.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_MICROMEDICIONES.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_MICROMEDICIONES.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_MICROMEDICIONES.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'micromedicion';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_MICROMEDICION_CAMBIO';
    MI_CAMPOS        := 'SP_MICROMEDICION_CAMBIO.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_MICROMEDICION_CAMBIO.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_MICROMEDICION_CAMBIO.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_MICROMEDICION_CAMBIO.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_MICROMEDICION_CAMBIO.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_MICROMEDICION_CAMBIO.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'cambio de micromedicion';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_MODIFICACIONES';
    MI_CAMPOS        := 'SP_MODIFICACIONES.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_MODIFICACIONES.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_MODIFICACIONES.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_MODIFICACIONES.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_MODIFICACIONES.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_MODIFICACIONES.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'modificaciones';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_MODIFICACIONESDEUDA';
    MI_CAMPOS        := 'SP_MODIFICACIONESDEUDA.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_MODIFICACIONESDEUDA.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_MODIFICACIONESDEUDA.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_MODIFICACIONESDEUDA.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_MODIFICACIONESDEUDA.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_MODIFICACIONESDEUDA.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'modificaciones de deuda';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_MULTIUSUARIOS';
    MI_CAMPOS        := 'SP_MULTIUSUARIOS.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_MULTIUSUARIOS.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_MULTIUSUARIOS.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_MULTIUSUARIOS.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_MULTIUSUARIOS.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_MULTIUSUARIOS.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'multiusuarios';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_ORDENTRABAJO';
    MI_CAMPOS        := 'SP_ORDENTRABAJO.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_ORDENTRABAJO.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_ORDENTRABAJO.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_ORDENTRABAJO.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_ORDENTRABAJO.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_ORDENTRABAJO.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'ordenes de trabajo';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_PAGO';
    MI_CAMPOS        := 'SP_PAGO.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_PAGO.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_PAGO.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_PAGO.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_PAGO.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_PAGO.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'pagos';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;


    MI_TABLA         := 'SP_D_RECAUDO_USUARIO';
    MI_CAMPOS        := 'COMPANIA   = ''' || UN_COMPANIA || ''',
                         CICLO      = ''' || UN_NUE_CICLO || ''',
                         CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND CICLO      = ''' || UN_ANT_CICLO || '''
                        AND CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'pagos';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;




    MI_TABLA         := 'SP_PARAMETROFACTURACION';
    MI_CAMPOS        := 'SP_PARAMETROFACTURACION.COMPANIA    = ''' || UN_COMPANIA || ''',
                         SP_PARAMETROFACTURACION.CICLO       = ''' || UN_NUE_CICLO || ''',
                         SP_PARAMETROFACTURACION.CODIGOFINAL = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_PARAMETROFACTURACION.COMPANIA    = ''' || UN_ANT_COMPANIA || '''
                        AND SP_PARAMETROFACTURACION.CICLO       = ''' || UN_ANT_CICLO || '''
                        AND SP_PARAMETROFACTURACION.CODIGOFINAL = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'parametros de facturacion';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_PARAMETROFACTURACION';
    MI_CAMPOS        := 'SP_PARAMETROFACTURACION.COMPANIA      = ''' || UN_COMPANIA || ''',
                         SP_PARAMETROFACTURACION.CICLO         = ''' || UN_NUE_CICLO || ''',
                         SP_PARAMETROFACTURACION.CODIGOINICIAL = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_PARAMETROFACTURACION.COMPANIA      = ''' || UN_ANT_COMPANIA || '''
                        AND SP_PARAMETROFACTURACION.CICLO         = ''' || UN_ANT_CICLO || '''
                        AND SP_PARAMETROFACTURACION.CODIGOINICIAL = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'parametros de facturacion';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_PLANOSDIGITAL';
    MI_CAMPOS        := 'SP_PLANOSDIGITAL.COMPANIA = ''' || UN_COMPANIA || ''',
                         SP_PLANOSDIGITAL.CICLO    = ''' || UN_NUE_CICLO || ''',
                         SP_PLANOSDIGITAL.MAECOD   = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_PLANOSDIGITAL.COMPANIA = ''' || UN_ANT_COMPANIA || '''
                        AND SP_PLANOSDIGITAL.CICLO    = ''' || UN_ANT_CICLO || '''
                        AND SP_PLANOSDIGITAL.MAECOD   = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'planos digitales';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_PLANOSDIGITALENVIO';
    MI_CAMPOS        := 'SP_PLANOSDIGITALENVIO.COMPANIA      = ''' || UN_COMPANIA || ''',
                         SP_PLANOSDIGITALENVIO.CICLO         = ''' || UN_NUE_CICLO || ''',
                         SP_PLANOSDIGITALENVIO.CODIGOINICIAL = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_PLANOSDIGITALENVIO.COMPANIA      = ''' || UN_ANT_COMPANIA || '''
                        AND SP_PLANOSDIGITALENVIO.CICLO         = ''' || UN_ANT_CICLO || '''
                        AND SP_PLANOSDIGITALENVIO.CODIGOINICIAL = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'planes digitales de envio';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_PLANOSDIGITALENVIO';
    MI_CAMPOS        := 'SP_PLANOSDIGITALENVIO.COMPANIA    = ''' || UN_COMPANIA || ''',
                         SP_PLANOSDIGITALENVIO.CICLO       = ''' || UN_NUE_CICLO || ''',
                         SP_PLANOSDIGITALENVIO.CODIGOFINAL = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_PLANOSDIGITALENVIO.COMPANIA    = ''' || UN_ANT_COMPANIA || '''
                        AND SP_PLANOSDIGITALENVIO.CICLO       = ''' || UN_ANT_CICLO || '''
                        AND SP_PLANOSDIGITALENVIO.CODIGOFINAL = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'planes digitales de envio';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_RECAUDOPERATRASO';
    MI_CAMPOS        := 'SP_RECAUDOPERATRASO.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_RECAUDOPERATRASO.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_RECAUDOPERATRASO.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_RECAUDOPERATRASO.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_RECAUDOPERATRASO.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_RECAUDOPERATRASO.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'recaudos de periodos atraso';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_RECAUDO_PRODUCTIVIDAD';
    MI_CAMPOS        := 'SP_RECAUDO_PRODUCTIVIDAD.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_RECAUDO_PRODUCTIVIDAD.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_RECAUDO_PRODUCTIVIDAD.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_RECAUDO_PRODUCTIVIDAD.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_RECAUDO_PRODUCTIVIDAD.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_RECAUDO_PRODUCTIVIDAD.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'recaudos de productividad';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_TBLHIST_SALDO_CREDITO';
    MI_CAMPOS        := 'SP_TBLHIST_SALDO_CREDITO.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_TBLHIST_SALDO_CREDITO.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_TBLHIST_SALDO_CREDITO.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_TBLHIST_SALDO_CREDITO.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_TBLHIST_SALDO_CREDITO.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_TBLHIST_SALDO_CREDITO.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'historia de saldos de credito';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_TBL_HIST_NOVEDADES_USU';
    MI_CAMPOS        := 'SP_TBL_HIST_NOVEDADES_USU.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_TBL_HIST_NOVEDADES_USU.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_TBL_HIST_NOVEDADES_USU.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_TBL_HIST_NOVEDADES_USU.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_TBL_HIST_NOVEDADES_USU.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_TBL_HIST_NOVEDADES_USU.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'historias de novedades de usuario';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;


    MI_TABLA         := 'SP_TMP_USUARIOS_PESOASEO';
    MI_CAMPOS        := 'SP_TMP_USUARIOS_PESOASEO.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_TMP_USUARIOS_PESOASEO.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_TMP_USUARIOS_PESOASEO.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_TMP_USUARIOS_PESOASEO.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_TMP_USUARIOS_PESOASEO.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_TMP_USUARIOS_PESOASEO.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'usuarios pesoaseo';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_UNIDADESRESIDENCIALES';
    MI_EXCLUIDOS     := 'COMPANIA,CICLO,CODIGORUTA,CREATED_BY ,DATE_CREATED';
    MI_CAMPOS        := PCK_SYSMAN_UTL.FC_LISTA_CAMPOS(UN_TABLA     => 'SP_UNIDADESRESIDENCIALES',
                                                       UN_EXCLUIDOS => MI_EXCLUIDOS);
    MI_VALORES       := 'SELECT ''' || UN_COMPANIA || ''' ,
                                ''' || UN_NUE_CICLO || ''' ,
                                ''' || UN_NUE_CODIGORUTA || ''',
                                ''' || UN_USUARIO || ''',
                                SYSDATE,
                                '   || MI_CAMPOS || '
                         FROM   SP_UNIDADESRESIDENCIALES
                         WHERE  SP_UNIDADESRESIDENCIALES.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                           AND  SP_UNIDADESRESIDENCIALES.CICLO      = ''' || UN_ANT_CICLO || '''
                           AND  SP_UNIDADESRESIDENCIALES.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || '''';
    MI_CAMPOS        := MI_EXCLUIDOS || ',' || MI_CAMPOS;

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                              UN_ACCION  => 'IS',
                                              UN_CAMPOS  => MI_CAMPOS,
                                              UN_VALORES => MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_INSUNIRESIDENCIAL
                    );
    END;

    MI_TABLA         := 'SP_HISTORIA_EXTERNA';
    MI_CAMPOS        := 'SP_HISTORIA_EXTERNA.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_HISTORIA_EXTERNA.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_HISTORIA_EXTERNA.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_HISTORIA_EXTERNA.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_HISTORIA_EXTERNA.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_HISTORIA_EXTERNA.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'historia externa';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_HISTORIA_EXTERNA_DESACTIVA';
    MI_CAMPOS        := 'SP_HISTORIA_EXTERNA_DESACTIVA.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_HISTORIA_EXTERNA_DESACTIVA.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_HISTORIA_EXTERNA_DESACTIVA.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_HISTORIA_EXTERNA_DESACTIVA.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_HISTORIA_EXTERNA_DESACTIVA.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_HISTORIA_EXTERNA_DESACTIVA.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'historia externa desactiva';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_USUARIO_ABONO';
    MI_CAMPOS        := 'SP_USUARIO_ABONO.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_USUARIO_ABONO.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_USUARIO_ABONO.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_USUARIO_ABONO.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_USUARIO_ABONO.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_USUARIO_ABONO.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'abonos de usuario';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_USUARIO_DESCU';
    MI_CAMPOS        := 'SP_USUARIO_DESCU.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_USUARIO_DESCU.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_USUARIO_DESCU.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_USUARIO_DESCU.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_USUARIO_DESCU.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_USUARIO_DESCU.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'usuarios descu';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_USUARIO_HISTORICOS';
    MI_CAMPOS        := 'SP_USUARIO_HISTORICOS.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_USUARIO_HISTORICOS.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_USUARIO_HISTORICOS.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_USUARIO_HISTORICOS.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_USUARIO_HISTORICOS.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_USUARIO_HISTORICOS.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'historicos de usuario';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_USUARIO_PROBLEMA';
    MI_CAMPOS        := 'SP_USUARIO_PROBLEMA.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_USUARIO_PROBLEMA.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_USUARIO_PROBLEMA.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_USUARIO_PROBLEMA.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_USUARIO_PROBLEMA.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_USUARIO_PROBLEMA.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'problemas de usuario';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_USUARIO_PRODUC';
    MI_CAMPOS        := 'SP_USUARIO_PRODUC.COMPANIA   = ''' || UN_COMPANIA || ''',
                         SP_USUARIO_PRODUC.CICLO      = ''' || UN_NUE_CICLO || ''',
                         SP_USUARIO_PRODUC.CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   SP_USUARIO_PRODUC.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_USUARIO_PRODUC.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_USUARIO_PRODUC.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'usuarios produc';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;


    MI_TABLA         := 'SP_AUDITORIAMNUMOD';
    MI_CAMPOS        := 'COMPANIA   = ''' || UN_COMPANIA || ''',
                         CICLO      = ''' || UN_NUE_CICLO || ''',
                         CODIGORUTA = ''' || UN_NUE_CODIGORUTA || ''',
                         DATE_MODIFIED = SYSDATE,
                         MODIFIED_BY   = '''||UN_USUARIO||'''';
    MI_CONDICION     := '   COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND CICLO      = ''' || UN_ANT_CICLO || '''
                        AND CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';
    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'Auditoria Usuario';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;



    MI_TABLA         := 'SP_UNIDADESRESIDENCIALES';
    MI_CONDICION     := '   SP_UNIDADESRESIDENCIALES.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_UNIDADESRESIDENCIALES.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_UNIDADESRESIDENCIALES.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'E',
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'unidades adicionales de aseo';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_DELELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;



    MI_TABLA         := 'SP_ABONOS';
    MI_CONDICION     := '   SP_ABONOS.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_ABONOS.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_ABONOS.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'E',
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'abonos';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_DELELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA         := 'SP_USUARIO';
    MI_CONDICION     := '   SP_USUARIO.COMPANIA   = ''' || UN_ANT_COMPANIA || '''
                        AND SP_USUARIO.CICLO      = ''' || UN_ANT_CICLO || '''
                        AND SP_USUARIO.CODIGORUTA = ''' || UN_ANT_CODIGORUTA || ''' ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'E',
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_REEMPLAZOS(1).CLAVE 	:= 'ELEMENTO';
        MI_REEMPLAZOS(1).VALOR	:= 'usuario';
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_TABLAERROR => MI_TABLA,
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_DELELEMENTOCICLO,
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                    );
    END;

    MI_TABLA :='SP_USUARIO';
    MI_CAMPOS:='MODIFIED_BY='''||UN_USUARIO||''','||
               'DATE_MODIFIED=SYSDATE,'||
               'CAMBIOCICLORUTA=0';

    MI_CONDICION:='COMPANIA ='''||UN_ANT_COMPANIA||''' AND CODIGORUTA='''|| UN_NUE_CODIGORUTA || '''';

    PCK_DATOS.GL_RTA :=PCK_DATOS.FC_ACME(UN_TABLA     =>MI_TABLA,
                                   UN_ACCION    =>'M',
                                   UN_CAMPOS    =>MI_CAMPOS,
                                   UN_CONDICION =>MI_CONDICION);


  RETURN '1';
END FC_CAMBIOCICLO;

  --7
  PROCEDURE PR_ACTUALIZARRANGOS
  (
    /*
      NAME              : PR_ACTUALIZARRANGOS --> EN ACCESS ActualizarRangos 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MIGRADOR     : 30/08/2016
      TIME              : 08:35 AM
      SOURCE MODULE     : SysmanSp2016.05.04
      MODIFIER          : YEISSON ALEJANDRO ROJAS RUIZ
      DATE MODIFIED     : 06/01/2017
      TIME              : 09:00 AM
      MODIFICATIONS     : CORRECCIÓN SEGÚN ESTÁNDAR DE PROGRAMACIÓN Y OPTIMIZACIÓN DEL MANEJO DE ERRORES.
      DESCRIPTION       : Función que realiza el proceso de actualizar el código inicial y final de un ciclo.
      PARAMETERS        : UN_COMPANIA   => CÓDIGO DE LA COMPAÑÍA ACTUALMENTE UTILIZADA.                          
                          UN_CICLO      => NÚMERO DEL CICLO ACTUAL.      

      @NAME:    actualizarRangos 
      @METHOD:  PUT
    */  
    UN_COMPANIA        IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_CICLO           IN  PCK_SUBTIPOS.TI_CICLO  
  )
  AS
    MI_PRIMERO          PCK_SUBTIPOS.TI_CODIGORUTA;
    MI_ULTIMO           PCK_SUBTIPOS.TI_CODIGORUTA;
    MI_TABLAUPDATE      PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOSUPDATE     PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICIONUPDATE  PCK_SUBTIPOS.TI_CONDICION;
  BEGIN
    BEGIN
      BEGIN  
        BEGIN
          SELECT  MIN(CODIGORUTA) PRIMERO,
                  MAX(CODIGORUTA) ULTIMO
          INTO    MI_PRIMERO,
                  MI_ULTIMO
          FROM    SP_USUARIO
          WHERE   COMPANIA = UN_COMPANIA
            AND   CICLO    = UN_CICLO;

          EXCEPTION WHEN NO_DATA_FOUND THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;

        END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD =>SQLCODE,
          UN_ERROR_COD=>PCK_ERRORES.ERR_FACSERP_ACTL_RANGO_GEN
        );  
      END;

      MI_TABLAUPDATE := 'SP_CICLO';
      MI_CAMPOSUPDATE := 'CODIGOINICIAL = ''' || MI_PRIMERO || ''', 
                          CODIGOFINAL   = ''' || MI_ULTIMO || '''';
      MI_CONDICIONUPDATE := 'NUMERO = ''' || UN_CICLO||'''';

      PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLAUPDATE,
                                             UN_ACCION    => 'M',
                                             UN_CAMPOS    => MI_CAMPOSUPDATE,
                                             UN_CONDICION => MI_CONDICIONUPDATE);


      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
    END;  

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERR_FACSERP_ACTL_RANGO_GEN
      );  

  END PR_ACTUALIZARRANGOS; 

  --8
  PROCEDURE PR_REVERSARPORPAQUETE
  (
    /*
      NAME              : PR_REVERSARPORPAQUETE --> EN ACCESS ReversaPorPaquete 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MIGRADOR     : 30/08/2016
      TIME              : 12:45 PM
      SOURCE MODULE     : SysmanSp2016.05.04
      MODIFIER          : YEISSON ALEJANDRO ROJAS RUIZ
      DATE MODIFIED     : 06/01/2017
      TIME              : 11:30 AM
      MODIFICATIONS     : CORRECCIÓN SEGÚN ESTÁNDAR DE PROGRAMACIÓN Y OPTIMIZACIÓN DEL MANEJO DE ERRORES.
      DESCRIPTION       : Procedimiento que realiza el proceso de reversar por paquete.
      PARAMETERS        : UN_COMPANIA   => CÓDIGO DE LA COMPAÑÍA ACTUAL.                          
                          UN_FECHA      => FECHA POR LA QUE SE VA A FILTRAR EN LA CONSULTA DEL MERGE.
                          UN_BANCO      => BANCO POR EL QUE SE VA A FILTRAR EN LA CONSULTA DEL MERGE.
                          UN_PAQUETE    => PAQUETE POR EL QUE SE VA A FILTRAR EN LA CONSULTA DEL MERGE.

      @NAME:    reversarPorPaquete
      @METHOD:  PUT
    */ 
    UN_COMPANIA   IN   PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHA      IN   DATE,
    UN_BANCO      IN   PCK_SUBTIPOS.TI_BANCO,
    UN_PAQUETE    IN   SP_HISTORIA_EXTERNA.PAQUETE_PAGO%TYPE 
  )
  AS
    MI_TABLAMERGE     PCK_SUBTIPOS.TI_TABLA;
    MI_MERGEUSING    PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE   PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE   PCK_SUBTIPOS.TI_MERGEEXISTE;
  BEGIN
    BEGIN  
      MI_TABLAMERGE    := 'SP_HISTORIA_EXTERNA';
      MI_MERGEUSING    := 'SELECT  H.COMPANIA, '|| CHR(13) || CHR(10) || 
                          '        H.CICLO, '|| CHR(13) || CHR(10) || 
                          '        H.CODIGORUTA, '|| CHR(13) || CHR(10) || 
                          '        H.ANO, '|| CHR(13) || CHR(10) || 
                          '        H.PERIODO, '|| CHR(13) || CHR(10) || 
                          '        H.ID_EMPRESA '|| CHR(13) || CHR(10) || 
                          'FROM    SP_USUARIO U '|| CHR(13) || CHR(10) || 
                          '  INNER JOIN SP_HISTORIA_EXTERNA H '|| CHR(13) || CHR(10) || 
                          '    ON  U.COMPANIA       = H.COMPANIA '|| CHR(13) || CHR(10) || 
                          '    AND U.CICLO          = H.CICLO '|| CHR(13) || CHR(10) || 
                          '    AND U.CODIGORUTA     = H.CODIGORUTA '|| CHR(13) || CHR(10) || 
                          '    AND U.EMPRESAASEOEXT = H.ID_EMPRESA '|| CHR(13) || CHR(10) || 
                          'WHERE   U.COMPANIA     = '''|| UN_COMPANIA||''''|| CHR(13) || CHR(10) || 
                          '  AND   H.FECHA_PAGO   = TO_DATE(''' || UN_FECHA || ''',''DD/MM/YYYY'')'|| CHR(13) || CHR(10) || 
                          '  AND   H.BANCO_PAGO   = ''' || UN_BANCO || ''''|| CHR(13) || CHR(10) || 
                          '  AND   H.PAQUETE_PAGO = ''' || UN_PAQUETE || '''';
      MI_MERGEENLACE   := '    TABLA.COMPANIA   = VISTA.COMPANIA '|| CHR(13) || CHR(10) || 
                          'AND TABLA.CICLO      = VISTA.CICLO '|| CHR(13) || CHR(10) || 
                          'AND TABLA.CODIGORUTA = VISTA.CODIGORUTA '|| CHR(13) || CHR(10) || 
                          'AND TABLA.ANO        = VISTA.ANO '|| CHR(13) || CHR(10) || 
                          'AND TABLA.PERIODO    = VISTA.PERIODO '|| CHR(13) || CHR(10) || 
                          'AND TABLA.ID_EMPRESA = VISTA.ID_EMPRESA ';
      MI_MERGEEXISTE   := ' UPDATE '|| CHR(13) || CHR(10) || 
                          ' SET TABLA.FECHA_PAGO   = NULL, '|| CHR(13) || CHR(10) || 
                          '     TABLA.BANCO_PAGO   = NULL, '|| CHR(13) || CHR(10) || 
                          '     TABLA.PAQUETE_PAGO = NULL, '|| CHR(13) || CHR(10) || 
                          '     TABLA.REVERSADO    = -1 ';
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => 'SP_HISTORIA_EXTERNA',
                                           UN_ACCION      => 'MM',
                                           UN_MERGEUSING  => MI_MERGEUSING, 
                                           UN_MERGEENLACE => MI_MERGEENLACE,
                                           UN_MERGEEXISTE => MI_MERGEEXISTE);

      MI_MERGEUSING    := 'SELECT  H.COMPANIA, '|| CHR(13) || CHR(10) || 
                          '        H.CICLO, '|| CHR(13) || CHR(10) || 
                          '        H.CODIGORUTA, '|| CHR(13) || CHR(10) || 
                          '        H.ANO, '|| CHR(13) || CHR(10) || 
                          '        H.PERIODO, '|| CHR(13) || CHR(10) || 
                          '        H.ID_EMPRESA '|| CHR(13) || CHR(10) || 
                          'FROM    SP_USUARIO U '|| CHR(13) || CHR(10) || 
                          '  INNER JOIN SP_HISTORIA_EXTERNA H '|| CHR(13) || CHR(10) || 
                          '    ON  U.COMPANIA       = H.COMPANIA '|| CHR(13) || CHR(10) || 
                          '    AND U.CICLO          = H.CICLO '|| CHR(13) || CHR(10) || 
                          '    AND U.CODIGORUTA     = H.CODIGORUTA '|| CHR(13) || CHR(10) || 
                          '    AND U.EMPRESAASEOEXT = H.ID_EMPRESA '|| CHR(13) || CHR(10) || 
                          'WHERE   U.COMPANIA           = ''' || UN_COMPANIA || ''''|| CHR(13) || CHR(10) || 
                          '  AND   H.FECHA_PAGO_DOBLE   = TO_DATE(''' || UN_FECHA || ''',''DD/MM/YYYY'')'|| CHR(13) || CHR(10) ||
                          '  AND   H.BANCO_PAGO_DOBLE   = ''' || UN_BANCO || ''''|| CHR(13) || CHR(10) || 
                          '  AND   H.PAQUETE_PAGO_DOBLE = ''' || UN_PAQUETE || '''';
      MI_MERGEENLACE   := ' TABLA.COMPANIA   = VISTA.COMPANIA '|| CHR(13) || CHR(10) || 
                          'AND TABLA.CICLO      = VISTA.CICLO '|| CHR(13) || CHR(10) || 
                          'AND TABLA.CODIGORUTA = VISTA.CODIGORUTA '|| CHR(13) || CHR(10) || 
                          'AND TABLA.ANO        = VISTA.ANO '|| CHR(13) || CHR(10) || 
                          'AND TABLA.PERIODO    = VISTA.PERIODO '|| CHR(13) || CHR(10) || 
                          'AND TABLA.ID_EMPRESA = VISTA.ID_EMPRESA ';
      MI_MERGEEXISTE   := 'UPDATE '|| CHR(13) || CHR(10) || 
                          '  SET TABLA.FECHA_PAGO_DOBLE   = NULL, '|| CHR(13) || CHR(10) || 
                          '    TABLA.BANCO_PAGO_DOBLE   = NULL, '|| CHR(13) || CHR(10) || 
                          '    TABLA.PAQUETE_PAGO_DOBLE = NULL, '|| CHR(13) || CHR(10) || 
                          '    TABLA.REVERSADO          = -1 ';
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => 'SP_HISTORIA_EXTERNA',
                                           UN_ACCION      => 'MM',
                                           UN_MERGEUSING  => MI_MERGEUSING, 
                                           UN_MERGEENLACE => MI_MERGEENLACE,
                                           UN_MERGEEXISTE => MI_MERGEEXISTE);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
    END;  

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERR_FACSERP_REV_PAQT_GEN
      );       

  END PR_REVERSARPORPAQUETE;  

  --9
  PROCEDURE PR_REVERSARPORPAQUETECONV
  (
    /*
      NAME              : PR_REVERSARPORPAQUETECONV --> EN ACCESS ReversaPorPaquete 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MIGRADOR     : 30/08/2016
      TIME              : 2:00 PM
      SOURCE MODULE     : SysmanSp2016.05.04
      MODIFIER          : YEISSON ALEJANDRO ROJAS RUIZ
      DATE MODIFIED     : 06/01/2017
      TIME              : 04:00 PM
      MODIFICATIONS     : CORRECCIÓN SEGÚN ESTÁNDAR DE PROGRAMACIÓN Y OPTIMIZACIÓN DEL MANEJO DE ERRORES.
      DESCRIPTION       : Procedimiento que realiza el proceso de reversar los pagos de convenios.
      PARAMETERS        : UN_COMPANIA   => CÓDIGO DE LA COMPAÑÍA ACTUAL.                          
                          UN_FECHA      => FECHA POR LA QUE SE VA A FILTRAR EN LA CONDICIÓN DEL UPDATE.
                          UN_BANCO      => BANCO POR EL QUE SE VA A FILTRAR EN LA CONDICION DEL UPDATE.
                          UN_PAQUETE    => PAQUETE POR EL QUE SE VA A FILTRAR EN LA CONDICION DEL UPDATE.

      @NAME:    reversarPorPaqueteConvenio
      @METHOD:  PUT
    */ 
    UN_COMPANIA      IN     PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHA         IN    DATE,
    UN_BANCO         IN    PCK_SUBTIPOS.TI_BANCO,
    UN_PAQUETE       IN     SP_HISTORIA_EXTERNA.PAQUETE_PAGO%TYPE 
  )
  AS
    MI_TABLAUPDATE      PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOSUPDATE     PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICIONUPDATE  PCK_SUBTIPOS.TI_CONDICION;
  BEGIN
    BEGIN  
      MI_TABLAUPDATE     := 'SP_HISTORIA_CONVENIOS';
      MI_CAMPOSUPDATE    := ' FECHA_PAGO   = NULL, '|| CHR(13) || CHR(10) || 
                            ' BANCO_PAGO   = NULL, '|| CHR(13) || CHR(10) || 
                            ' PAQUETE_PAGO = NULL, '|| CHR(13) || CHR(10) || 
                            ' REVERSADO    = -1';
      MI_CONDICIONUPDATE := '     COMPANIA     = ''' || UN_COMPANIA || ''''|| CHR(13) || CHR(10) || 
                            ' AND FECHA_PAGO   = TO_DATE( ''' || UN_FECHA || ''',''DD/MM/YYYY'')'|| CHR(13) || CHR(10) ||
                            ' AND BANCO_PAGO   = ''' || UN_BANCO || ''''|| CHR(13) || CHR(10) || 
                            ' AND PAQUETE_PAGO = ''' || UN_PAQUETE ||'''';
      PCK_DATOS.GL_RTA   := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLAUPDATE,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOSUPDATE,
                                              UN_CONDICION => MI_CONDICIONUPDATE);

      MI_CAMPOSUPDATE    := ' FECHA_PAGO_DOBLE   =NULL, '|| CHR(13) || CHR(10) || 
                            ' BANCO_PAGO_DOBLE   =NULL, '|| CHR(13) || CHR(10) || 
                            ' PAQUETE_PAGO_DOBLE =NULL, '|| CHR(13) || CHR(10) || 
                            ' REVERSADO          =-1';
      MI_CONDICIONUPDATE := '     COMPANIA           = '''|| UN_COMPANIA || ''''|| CHR(13) || CHR(10) || 
                            ' AND FECHA_PAGO_DOBLE   = TO_DATE(''' || UN_FECHA || ''',''DD/MM/YYYY'')'|| CHR(13) || CHR(10) ||
                            ' AND BANCO_PAGO_DOBLE   = ''' || UN_BANCO ||''''|| CHR(13) || CHR(10) || 
                            ' AND PAQUETE_PAGO_DOBLE = ''' || UN_PAQUETE ||'''';
      PCK_DATOS.GL_RTA   := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLAUPDATE,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOSUPDATE,
                                              UN_CONDICION => MI_CONDICIONUPDATE);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
    END;  

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERR_FACSERP_REV_PAQT_CONV
      );      

  END PR_REVERSARPORPAQUETECONV;

  --10 
  PROCEDURE PR_ELIMINARECPRODPAQUETE
  (
    /*
      NAME              : PR_ELIMINARECPRODPAQUETE --> EN ACCESS EliminaRecProdPaquete 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MIGRADOR     : 30/08/2016
      TIME              : 2:20 PM
      SOURCE MODULE     : SysmanSp2016.05.04
      MODIFIER          : YEISSON ALEJANDRO ROJAS RUIZ
      DATE MODIFIED     : 10/01/2017
      TIME              : 09:00 AM
      MODIFICATIONS     : CORRECCIÓN SEGÚN ESTÁNDAR DE PROGRAMACIÓN Y OPTIMIZACIÓN DEL MANEJO DE ERRORES.
      DESCRIPTION       : Procedimiento que realiza el proceso de eliminar los pagos.
      PARAMETERS        : UN_COMPANIA   => CÓDIGO DE LA COMPAÑÍA ACTUAL.                          
                          UN_FECHA      => FECHA POR LA QUE SE VA A FILTRAR EN LA CONDICIÓN DEL DELETE.
                          UN_BANCO      => BANCO POR EL QUE SE VA A FILTRAR EN LA CONDICION DEL DELETE.
                          UN_PAQUETE    => PAQUETE POR EL QUE SE VA A FILTRAR EN LA CONDICION DEL DELETE.

      @NAME:    eliminarRecProdPaquete
      @METHOD:  DELETE
    */
    UN_COMPANIA      IN    PCK_SUBTIPOS.TI_COMPANIA, 
    UN_FECHA         IN    DATE,
    UN_BANCO         IN    PCK_SUBTIPOS.TI_BANCO,
    UN_PAQUETE       IN    SP_HISTORIA_EXTERNA.PAQUETE_PAGO%TYPE
  )
  AS
    MI_TABLADELETE      PCK_SUBTIPOS.TI_TABLA;
    MI_CONDICIONDELETE  PCK_SUBTIPOS.TI_CONDICION;
    MI_PARAMETRO        VARCHAR2(3 CHAR); -- VARCHAR2 debido a que no existe un tipo de dato con las mismas características en el paquete PCK_SUBTIPOS.
  BEGIN
    BEGIN
      MI_PARAMETRO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                            UN_NOMBRE    => 'DESCONTAR PRODUCTIVIDAD',
                                            UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                            UN_FECHA_PAR => SYSDATE);
      IF MI_PARAMETRO = 'SI' THEN
        MI_TABLADELETE     := 'SP_RECAUDO_PRODUCTIVIDAD';
        MI_CONDICIONDELETE := '     COMPANIA = ''' || UN_COMPANIA || ''''|| CHR(13) || CHR(10) ||
                              ' AND FECHA    = TO_DATE(''' || UN_FECHA || ''',''DD/MM/YYYY'')'|| CHR(13) || CHR(10) || 
                              ' AND BANCO    = ''' || UN_BANCO ||''''|| CHR(13) || CHR(10) || 
                              ' AND PAQUETE  = ''' || UN_PAQUETE ||'''';
        PCK_DATOS.GL_RTA   := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLADELETE,
                                                UN_ACCION    => 'E',
                                                UN_CONDICION => MI_CONDICIONDELETE);
      END IF;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
    END;  

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERR_FACSERP_ELIM_PAQT_PROD
      ); 

  END PR_ELIMINARECPRODPAQUETE;

  --11
  FUNCTION FC_BORRAREGPAGOS
  (
    /*
      NAME              : FC_BORRAREGPAGOS --> EN ACCESS Aceptar_Click -- FORM BORRAREGPAGOS 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MIGRADOR     : 30/08/2016
      TIME              : 2:20 PM
      SOURCE MODULE     : SysmanSp2016.05.04
      MODIFIER          : YEISSON ALEJANDRO ROJAS RUIZ
      DATE MODIFIED     : 10/01/2017
      TIME              : 11:00 AM
      MODIFICATIONS     : CORRECCIÓN SEGÚN ESTÁNDAR DE PROGRAMACIÓN Y OPTIMIZACIÓN DEL MANEJO DE ERRORES.
      DESCRIPTION       : Procedimiento que realiza el proceso de eliminar los pagos.
      PARAMETERS        : UN_COMPANIA   => CÓDIGO DE LA COMPAÑÍA ACTUAL.                          
                          UN_FECHA      => FECHA POR LA QUE SE VA A FILTRAR EN LAS CONDICIONES DE UPDATE Y DELETE.
                          UN_BANCO      => BANCO POR EL QUE SE VA A FILTRAR EN LA CONDICION DEL DELETE.
                          UN_PAQUETE    => PAQUETE POR EL QUE SE VA A FILTRAR EN LA CONDICION DEL DELETE.

      @NAME:    borrarRecPagos
      @METHOD:  PUT
    */
    UN_COMPANIA      IN    PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHA         IN    DATE,
    UN_BANCO         IN    PCK_SUBTIPOS.TI_BANCO,
    UN_PAQUETE       IN    SP_HISTORIA_EXTERNA.PAQUETE_PAGO%TYPE
  ) 
  RETURN VARCHAR2 AS 
    MI_TABLADELETE      PCK_SUBTIPOS.TI_TABLA;
    MI_CONDICIONDELETE  PCK_SUBTIPOS.TI_CONDICION;
    MI_TABLAUPDATE      PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOSUPDATE     PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICIONUPDATE  PCK_SUBTIPOS.TI_CONDICION;
    MI_INTRE1           PCK_SUBTIPOS.TI_ENTERO;
    MI_INTRE2           PCK_SUBTIPOS.TI_ENTERO;
    MI_INTRE3           PCK_SUBTIPOS.TI_ENTERO;
    MI_INTRE4           PCK_SUBTIPOS.TI_ENTERO;
    MI_RTA              VARCHAR2(1000 CHAR); -- VARCHAR2 debido a que no existe un tipo de dato con las mismas características en el paquete PCK_SUBTIPOS.
  BEGIN
    BEGIN
      MI_INTRE1 := 0;
      MI_INTRE2 := 0;
      MI_INTRE3 := 0;
      MI_INTRE4 := 0;

      MI_TABLADELETE     := 'SP_PAGO';
      MI_CONDICIONDELETE := '     COMPANIA      = ''' ||  UN_COMPANIA||''''|| 
                            ' AND FECHA         = TO_DATE(''' || UN_FECHA || ''',''DD/MM/YYYY'')'|| CHR(13) || CHR(10) ||
                            ' AND BANCO         = ''' || UN_BANCO || ''''|| CHR(13) || CHR(10) || 
                            ' AND NUMEROPAQUETE = ''' || UN_PAQUETE ||'''';

      MI_INTRE1          := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLADELETE,
                                              UN_ACCION    => 'E',
                                              UN_CONDICION => MI_CONDICIONDELETE); 

      MI_TABLADELETE     := 'SP_D_RECAUDO';
      MI_CONDICIONDELETE := '     COMPANIA      = ''' || UN_COMPANIA || ''''|| CHR(13) || CHR(10) ||
                            ' AND FECHA         = TO_DATE(''' || UN_FECHA || ''',''DD/MM/YYYY'')'|| CHR(13) || CHR(10) ||
                            ' AND BANCO         = ''' || UN_BANCO || ''''|| CHR(13) || CHR(10) ||
                            ' AND NUMEROPAQUETE = ''' || UN_PAQUETE || '''';
      PCK_DATOS.GL_RTA   := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLADELETE,
                                              UN_ACCION    => 'E',
                                              UN_CONDICION => MI_CONDICIONDELETE); 

      MI_TABLADELETE     := 'SP_RECAUDOS';
      MI_CONDICIONDELETE := '     COMPANIA      = ''' || UN_COMPANIA || ''''|| CHR(13) || CHR(10) ||
                            ' AND FECHA         = TO_DATE(''' || UN_FECHA || ''',''DD/MM/YYYY'')'|| CHR(13) || CHR(10) ||
                            ' AND BANCO         = ''' || UN_BANCO || ''''|| CHR(13) || CHR(10) ||
                            ' AND NUMEROPAQUETE = ''' || UN_PAQUETE ||'''';
      MI_INTRE2          := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLADELETE,
                                              UN_ACCION    => 'E',
                                              UN_CONDICION => MI_CONDICIONDELETE);                     

      MI_TABLADELETE := 'SP_D_PAGOSDOBLES';
      MI_CONDICIONDELETE := '     COMPANIA      = ''' || UN_COMPANIA || ''''|| CHR(13) || CHR(10) ||
                            ' AND FECHA         = TO_DATE(''' || UN_FECHA || ''',''DD/MM/YYYY'')'|| CHR(13) || CHR(10) ||
                            ' AND BANCO         = ''' || UN_BANCO || ''''|| CHR(13) || CHR(10) ||
                            ' AND NUMEROPAQUETE = ''' || UN_PAQUETE || '''';
      MI_INTRE4          := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLADELETE,
                                              UN_ACCION    => 'E',
                                              UN_CONDICION => MI_CONDICIONDELETE); 

      MI_TABLAUPDATE     := 'SP_USUARIO';
      MI_CAMPOSUPDATE    := ' BANCOPERPROCESO       = NULL, '|| 
                            ' FECHAPAGOPERPROCESO   = NULL, '||
                            ' PAQUETEPAGOPERPROCESO = NULL, '||
                            ' NOFECHAPAGOPERPROCESO = NULL  ';
      MI_CONDICIONUPDATE := '     COMPANIA              = ''' || UN_COMPANIA || ''''|| CHR(13) || CHR(10) ||
                            ' AND BANCOPERPROCESO       = ''' || UN_BANCO || ''''|| CHR(13) || CHR(10) ||
                            ' AND FECHAPAGOPERPROCESO   = TO_DATE(''' || UN_FECHA || ''',''DD/MM/YYYY'')'|| CHR(13) || CHR(10) ||
                            ' AND PAQUETEPAGOPERPROCESO = ''' || UN_PAQUETE || '''';
      MI_INTRE3          := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLAUPDATE,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOSUPDATE,
                                              UN_CONDICION => MI_CONDICIONUPDATE);

      --'JP 10/10/2012 Actualiza el Pago cuando es tercerizado el aseo
      PCK_SERVICIOS_PUBLICOS_COM1.PR_REVERSARPORPAQUETE(UN_COMPANIA => UN_COMPANIA,
                                                        UN_FECHA    => UN_FECHA,
                                                        UN_BANCO    => UN_BANCO,
                                                        UN_PAQUETE  => UN_PAQUETE);
      --'18/02/2014 JP para reversar los pagos de convenios
      PCK_SERVICIOS_PUBLICOS_COM1.PR_REVERSARPORPAQUETECONV(UN_COMPANIA => UN_COMPANIA,
                                                            UN_FECHA    => UN_FECHA,
                                                            UN_BANCO    => UN_BANCO,
                                                            UN_PAQUETE  => UN_PAQUETE);
      -- '26/08/2015
      PCK_SERVICIOS_PUBLICOS_COM1.PR_ELIMINARECPRODPAQUETE(UN_COMPANIA => UN_COMPANIA,
                                                           UN_FECHA    => UN_FECHA,
                                                           UN_BANCO    => UN_BANCO,
                                                           UN_PAQUETE  => UN_PAQUETE);


      MI_RTA := 'Se eliminaron ' || MI_INTRE1 || ' cupones de pago '||CHR(13)|| 
                'Se eliminaron '|| MI_INTRE2 || ' consignaciones de pago '||CHR(13)|| 
                'Se actualizaron ' || MI_INTRE3 || ' usuarios '||CHR(13)|| 
                ' y se eliminaron ' || MI_INTRE4 || ' pagos dobles ';

      IF MI_INTRE1 <> MI_INTRE3 THEN
        MI_RTA := MI_RTA||CHR(10)||CHR(13)||
                  'Posiblemente se están tratando de eliminar pagos de un ciclo que ya fue cerrado. 
                  Por lo tanto, no coinciden los cupones borrados con los usuarios afectados';
      END IF;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
    END;  

    RETURN MI_RTA;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERR_FACSERP_ELIM_REG_PAGO
      );

  END FC_BORRAREGPAGOS; 

  --12
  FUNCTION FC_HAYPERIODOSCERRADOS
  (
    /*
      NAME              : FC_HAYPERIODOSCERRADOS --> EN ACCESS hayPeriodosCerrados
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MIGRADOR     : 31/08/2016
      TIME              : 09:50 aM
      SOURCE MODULE     : SysmanSp2016.05.04
      MODIFIER          : YEISSON ALEJANDRO ROJAS RUIZ
      DATE MODIFIED     : 10/01/2017
      TIME              : 03:00 PM
      MODIFICATIONS     : CORRECCIÓN SEGÚN ESTÁNDAR DE PROGRAMACIÓN Y OPTIMIZACIÓN DEL MANEJO DE ERRORES.
      DESCRIPTION       : Función que verifica si los usuario de una ruta se encuentran en ciclos cerrados.
      PARAMETERS        : UN_COMPANIA   => CÓDIGO DE LA COMPAÑÍA ACTUAL.                          
                          UN_FECHA      => FECHA POR LA QUE SE VA A FILTRAR LA CONSULTA INICIAL.
                          UN_BANCO      => BANCO POR EL QUE SE VA A FILTRAR EN LA CONSULTA DEL CONTEO DE LOS CODIGORUTA.
                          UN_PAQUETE    => PAQUETE POR EL QUE SE VA A FILTRAR EN LA CONSULTA DEL CONTEO DE LOS CODIGORUTA.

      @NAME:    consultarPeriodosCerrados
      @METHOD:  GET
    */ 
    UN_COMPANIA IN   PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHA    IN   DATE,
    UN_BANCO    IN   PCK_SUBTIPOS.TI_BANCO,
    UN_PAQUETE  IN   SP_HISTORIA_EXTERNA.PAQUETE_PAGO%TYPE
  )
  RETURN NUMBER AS
    MI_TOTAL      PCK_SUBTIPOS.TI_ENTERO;
  BEGIN
    BEGIN 
      SELECT  COUNT(CODIGORUTA) TOTAL
      INTO    MI_TOTAL
      FROM    SP_USUARIO
      WHERE   COMPANIA              =  UN_COMPANIA 
        AND   CICLO IN 
        ( SELECT  NUMERO
          FROM    SP_CICLO
          WHERE   COMPANIA          =  UN_COMPANIA
            AND   FECHA_PREPARACION >= UN_FECHA
        )
        AND   BANCOPERPROCESO       =  UN_BANCO
        AND   FECHAPAGOPERPROCESO   >= UN_FECHA
        AND   PAQUETEPAGOPERPROCESO =  UN_PAQUETE;

      EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_TOTAL:=0;
    END;

    IF MI_TOTAL >0 THEN
      RETURN -1;
    ELSE
      RETURN 0;
    END IF;

  END FC_HAYPERIODOSCERRADOS;

  --13
  FUNCTION FC_DEVOLUCIONDECAJA
  (
    /*
      NAME              : FC_DEVOLUCIONDECAJA --> EN ACCESS DEVOLUCIONDECAJA
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MIGRADOR     : 12/09/2016
      TIME              : 04:00 PM
      SOURCE MODULE     : SysmanSp2016.05.04
      MODIFIER          : YEISSON ALEJANDRO ROJAS RUIZ
      DATE MODIFIED     : 10/01/2017
      TIME              : 04:30 PM
      MODIFICATIONS     : CORRECCIÓN SEGÚN ESTÁNDAR DE PROGRAMACIÓN Y OPTIMIZACIÓN DEL MANEJO DE ERRORES.
      PARAMETERS        : UN_COMPANIA   => CÓDIGO DE LA COMPAÑÍA ACTUAL.                          
                          UN_FECHA      => FECHA POR LA QUE SE VA A FILTRAR LA CONSULTA INICIAL.
                          UN_TIPO       => TIPO DE MOVIMIENTO DE CAJA.
                          UN_CLASE      => CLASE.
                          UN_DBLVALOR   => VALOR PAGO DE LAS ENTRADAS.
                          UN_USUARIO    => USUARIO ACTUAL.
                          UN_TIPOANULAR => TIPO DE MOVIMIENTO DE CAJA (ANULAR).

      @NAME:    realizarDevolucionDeCaja
      @METHOD:  GET
    */ 
    UN_COMPANIA       IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_TIPO           IN    SP_TIPOMOVIMIENTOCAJA.CODIGO%TYPE,
    UN_CLASE          IN    VARCHAR2, -- VARCHAR2 debido a que el parámetro no se usa en la función y por lo tanto, no se conoce con exactitud su tipo de dato.
    UN_FECHA          IN    DATE,
    UN_DBLVALOR       IN    PCK_SUBTIPOS.TI_DOBLE,
    UN_USUARIO        IN    PCK_SUBTIPOS.TI_USUARIO,
    UN_TIPOANULAR     IN    SP_MOVIMIENTOCAJA.TIPO%TYPE 
  )
  RETURN VARCHAR2 AS
    MI_DBLNUMERO         PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_DBLSALDO          PCK_SUBTIPOS.TI_DOBLE;
    MI_DBLSALDOANTERIOR  PCK_SUBTIPOS.TI_DOBLE;
    MI_RSCANO            SP_MOVIMIENTOCAJA.ANO%TYPE;
    MI_RSCPERIODO        SP_MOVIMIENTOCAJA.PERIODO%TYPE;
    MI_RSCPAQUETE        SP_MOVIMIENTOCAJA.PAQUETE%TYPE;
    MI_RSCBANCO          SP_MOVIMIENTOCAJA.BANCO%TYPE;
    MI_RSCTARJETAFECHA   SP_MOVIMIENTOCAJA.FECHA%TYPE;
    MI_RSCCHEQUEFECHA    SP_MOVIMIENTOCAJA.CHEQUEFECHA%TYPE;
    MI_RSCSALIDAS        SP_MOVIMIENTOCAJA.SALIDAS%TYPE;
    MI_RSCENTRADAS       SP_MOVIMIENTOCAJA.ENTRADAS%TYPE;
    MI_RSCEFECTIVO       SP_MOVIMIENTOCAJA.EFECTIVO%TYPE;
    MI_RSCCHEQUE         SP_MOVIMIENTOCAJA.CHEQUE%TYPE;
    MI_RSCCHEQUEBANCO    SP_MOVIMIENTOCAJA.CHEQUEBANCO%TYPE;
    MI_RSCDESCRIPCION    SP_MOVIMIENTOCAJA.DESCRIPCION%TYPE;
    MI_RSCDEVOLUCION     SP_MOVIMIENTOCAJA.DEVOLUCION%TYPE;
    MI_RSCTARJETANOMBRE  SP_MOVIMIENTOCAJA.TARJETANOMBRE%TYPE;
    MI_RSCTARJETA        SP_MOVIMIENTOCAJA.TARJETA%TYPE;
    MI_RSCCREDITO        SP_MOVIMIENTOCAJA.CREDITO%TYPE;
    MI_RSCTARJETANUMERO  SP_MOVIMIENTOCAJA.TARJETANUMERO%TYPE;
    MI_RSCCHEQUENUMERO   SP_MOVIMIENTOCAJA.CHEQUENUMERO%TYPE;
    MI_ULTIMOCONSECUTIVO SP_TIPOMOVIMIENTOCAJA.ULTIMOCONSECUTIVO%TYPE;
    MI_CONSECUTIVO       SP_TIPOMOVIMIENTOCAJA.CONSECUTIVO%TYPE;
    MI_INSERT            PCK_SUBTIPOS.TI_ENTERO;
    MI_TABLAINSERT       PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOSINSERT      PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORESINSERT     PCK_SUBTIPOS.TI_VALORES;
    MI_NUM               PCK_SUBTIPOS.TI_DOBLE;
  BEGIN
    BEGIN
      BEGIN
        SELECT  ANO,
                PERIODO,
                PAQUETE,
                BANCO,
                TARJETAFECHA,
                CHEQUEFECHA,
                SALIDAS,
                ENTRADAS,
                EFECTIVO,
                CHEQUE,
                CHEQUEBANCO,
                DESCRIPCION, 
                DEVOLUCION,
                TARJETANOMBRE,
                TARJETA, 
                CREDITO, 
                TARJETANUMERO, 
                CHEQUENUMERO
        INTO    MI_RSCANO,
                MI_RSCPERIODO,
                MI_RSCPAQUETE,
                MI_RSCBANCO,
                MI_RSCTARJETAFECHA,
                MI_RSCCHEQUEFECHA,
                MI_RSCSALIDAS, 
                MI_RSCENTRADAS,
                MI_RSCEFECTIVO,
                MI_RSCCHEQUE,
                MI_RSCCHEQUEBANCO,
                MI_RSCDESCRIPCION, 
                MI_RSCDEVOLUCION,
                MI_RSCTARJETANOMBRE,
                MI_RSCTARJETA,
                MI_RSCCREDITO,
                MI_RSCTARJETANUMERO,
                MI_RSCCHEQUENUMERO 
        FROM    SP_MOVIMIENTOCAJA  
        WHERE   COMPANIA = UN_COMPANIA 
          AND   TIPO     = UN_TIPOANULAR 
          AND   FECHA    = UN_FECHA 
          AND   ENTRADAS = UN_DBLVALOR 
          AND   USUARIO  = UN_USUARIO
        ORDER BY COMPANIA, 
                TIPO, 
                NUMERO, 
                FECHA DESC;

        EXCEPTION WHEN NO_DATA_FOUND THEN 
          MI_RSCANO:=NULL;
          MI_RSCPERIODO:=NULL;
          MI_RSCPAQUETE:=NULL;
          MI_RSCBANCO:=NULL;
          MI_RSCTARJETAFECHA:=NULL;
          MI_RSCCHEQUEFECHA:=NULL;
          MI_RSCSALIDAS:=NULL;
          MI_RSCENTRADAS:=NULL;
          MI_RSCEFECTIVO:=NULL;
          MI_RSCCHEQUE:=NULL;
          MI_RSCCHEQUEBANCO:=NULL;
          MI_RSCDESCRIPCION:=NULL;
          MI_RSCDEVOLUCION:=NULL;
          MI_RSCTARJETANOMBRE:=NULL;
      END;

      IF MI_RSCANO IS NOT NULL THEN
        BEGIN   
          SELECT  NVL(SALDOCAJA,0) SALDOCAJA 
          INTO    MI_DBLSALDOANTERIOR 
          FROM    SP_MOVIMIENTOCAJA 
          WHERE   COMPANIA = UN_COMPANIA  
            AND   CAJERO   = UN_USUARIO   
          ORDER BY FECHA DESC,   
                  HORA DESC;

          EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_DBLSALDOANTERIOR:=0;
        END;
        MI_DBLSALDO:= MI_DBLSALDOANTERIOR - UN_DBLVALOR;

        --'Aquí averigua el último consecutivo del tipo movimiento caja 
        BEGIN 
          BEGIN 
            SELECT  NVL(ULTIMOCONSECUTIVO,0) ULTIMOCONSECUTIVO,
                    CONSECUTIVO
            INTO    MI_ULTIMOCONSECUTIVO,
                    MI_CONSECUTIVO 
            FROM    SP_TIPOMOVIMIENTOCAJA  
            WHERE   COMPANIA = UN_COMPANIA  
              AND   CODIGO   = UN_TIPO; 

            EXCEPTION WHEN NO_DATA_FOUND THEN 
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;  

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD =>SQLCODE,
              UN_ERROR_COD=>PCK_ERRORES.ERR_FACSERP_DEV_CAJA_MOV
            );      
        END;

        IF NVL(MI_ULTIMOCONSECUTIVO, 0) = 0 THEN
          MI_DBLNUMERO:= NVL(MI_CONSECUTIVO, 1);
        ELSE 
          MI_DBLNUMERO:= MI_ULTIMOCONSECUTIVO + 1;
        END IF;   

        --INSERTARMOVIMIENTOCAJA:
        --'Aquí inserta en la tabla MovimientoCaja el movimiento contrario
        MI_TABLAINSERT   := 'SP_MOVIMIENTOCAJA';
        MI_CAMPOSINSERT  := 'COMPANIA, TIPO, NUMERO, FECHA, HORA, CAJERO, ENTRADAS, SALIDAS, EFECTIVO, CHEQUE, TARJETA, 
                             CREDITO, DEVOLUCION, TARJETANOMBRE, TARJETANUMERO, TARJETAFECHA, CHEQUEFECHA,CHEQUENUMERO, 
                             CHEQUEBANCO, DESCRIPCION, SALDOCAJA, USUARIO, FECHAREAL, HORAREAL, BANCO, PAQUETE, ANO, PERIODO';
        MI_VALORESINSERT := '''' || UN_COMPANIA || ''',
                            '''  || UN_TIPO || ''' ,
                            '    || MI_DBLNUMERO ||',
                            SYSDATE,
                            '    || ' SYSDATE,
                            '''  || UN_USUARIO || ''',
                            '    || MI_RSCSALIDAS || ',
                            '    || MI_RSCENTRADAS || ',
                            '    || MI_RSCEFECTIVO || ',
                            '    || MI_RSCCHEQUE || ',
                            '    || MI_RSCTARJETA || ',
                            '    || MI_RSCCREDITO || ',
                            '    || MI_RSCDEVOLUCION || ',
                            '''  || MI_RSCTARJETANOMBRE || ''',
                            '''  || MI_RSCTARJETANUMERO ||',''';

        IF MI_RSCTARJETAFECHA IS NULL  THEN
          MI_VALORESINSERT := MI_VALORESINSERT||'NULL,';
        ELSE
          MI_VALORESINSERT := MI_VALORESINSERT||'TO_DATE('''||MI_RSCTARJETAFECHA||''',''DD/MM/YYYY''),';
        END IF;

        IF MI_RSCCHEQUEFECHA IS NULL THEN
          MI_VALORESINSERT := MI_VALORESINSERT||'NULL,';
        ELSE
          MI_VALORESINSERT := MI_VALORESINSERT||'TO_DATE(''' || MI_RSCCHEQUEFECHA || ''',''DD/MM/YYYY''),';
        END IF;

        MI_VALORESINSERT := MI_VALORESINSERT||NVL(MI_RSCCHEQUENUMERO,'')||',
                            '   || NVL(MI_RSCCHEQUEBANCO, '') || ',
                            '   || NVL(MI_RSCDESCRIPCION, '') || ',
                            '   || MI_DBLSALDO || ',
                            ''' || UN_USUARIO || ''',
                            SYSDATE,
                            SYSDATE,
                            '   || NVL(MI_RSCBANCO, '') || ',
                            '   || NVL(MI_RSCPAQUETE, '') || ',
                            '   || NVL(MI_RSCANO, 0)||',
                            '   || NVL(MI_RSCPERIODO,'');
        MI_INSERT        := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLAINSERT,
                                              UN_ACCION  => 'I',
                                              UN_CAMPOS  => MI_CAMPOSINSERT,
                                              UN_VALORES => MI_VALORESINSERT);
        --db.Execute UN_sql, dbSeeChanges
        --'Aquí verifica si lo insertó y si no va y busca el último número
        --'de la tabla de MovimientoCaja
        IF MI_INSERT <= 0 THEN
         -- 'Aquí averigua el número a crear, pues el que generó está mal  
          BEGIN 
            SELECT  MAX(NUMERO)+1 NUM  
            INTO    MI_NUM
            FROM    SP_MOVIMIENTOCAJA
            WHERE   COMPANIA = UN_COMPANIA  
              AND   TIPO     = UN_TIPO; 

            EXCEPTION WHEN NO_DATA_FOUND THEN  
            MI_DBLNUMERO:= 1;
          END; 

          --'Aquí intenta insertar nuevamente
          MI_TABLAINSERT   := 'SP_MOVIMIENTOCAJA';
          MI_CAMPOSINSERT  := 'COMPANIA, TIPO, NUMERO, FECHA, HORA, CAJERO, ENTRADAS, SALIDAS, EFECTIVO, CHEQUE, TARJETA, 
                               CREDITO, DEVOLUCION, TARJETANOMBRE, TARJETANUMERO,TARJETAFECHA, CHEQUEFECHA, CHEQUENUMERO, 
                               CHEQUEBANCO, DESCRIPCION, SALDOCAJA, USUARIO, FECHAREAL, HORAREAL, BANCO, PAQUETE, ANO, PERIODO';
          MI_VALORESINSERT := '''' || UN_COMPANIA || ''',
                              '''  || UN_TIPO || ''' ,
                              '    || MI_DBLNUMERO || ',
                              SYSDATE,
                              '    || ' SYSDATE,
                              '''  || UN_USUARIO || ''' ,
                              '    || MI_RSCSALIDAS || ',
                              '    || MI_RSCENTRADAS || ',
                              '    || MI_RSCEFECTIVO || ',
                              '    || MI_RSCCHEQUE || ',
                              '    || MI_RSCTARJETA || ',
                              '    || MI_RSCCREDITO||',
                              '    || MI_RSCDEVOLUCION || ',
                              '''  || MI_RSCTARJETANOMBRE ||''',
                              '''  || MI_RSCTARJETANUMERO ||',''';

          IF MI_RSCTARJETAFECHA IS NULL THEN
            MI_VALORESINSERT := MI_VALORESINSERT||'NULL,';
          ELSE
            MI_VALORESINSERT := MI_VALORESINSERT||'TO_DATE('''||MI_RSCTARJETAFECHA||''',''DD/MM/YYYY''),';
          END IF;

          IF MI_RSCCHEQUEFECHA IS NULL THEN
            MI_VALORESINSERT := MI_VALORESINSERT||'NULL,';
          ELSE
            MI_VALORESINSERT := MI_VALORESINSERT||'TO_DATE('''||MI_RSCCHEQUEFECHA||''',''DD/MM/YYYY''),';
          END IF;

          MI_VALORESINSERT := MI_VALORESINSERT||NVL(MI_RSCCHEQUENUMERO,'')||',
                              '   || NVL(MI_RSCCHEQUEBANCO, '') || ', 
                              '   || NVL(MI_RSCDESCRIPCION, '') || ',
                              '   || MI_DBLSALDO || ',
                              ''' || UN_USUARIO || ''',
                              SYSDATE,
                              SYSDATE,
                              '   || NVL(MI_RSCBANCO, '') || ',
                              '   || NVL(MI_RSCPAQUETE, '') || ',
                              '   || NVL(MI_RSCANO, 0) || ',
                              '   || NVL(MI_RSCPERIODO,'');
          MI_INSERT        := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLAINSERT,
                                                UN_ACCION  => 'I',
                                                UN_CAMPOS  => MI_CAMPOSINSERT,
                                                UN_VALORES => MI_VALORESINSERT);
        END IF;

        IF NVL(MI_ULTIMOCONSECUTIVO, 0) <= MI_DBLNUMERO THEN
          MI_ULTIMOCONSECUTIVO:= MI_DBLNUMERO;  
        END IF;

      END IF; 

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;

    END;

    RETURN 'Proceso completado.';

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERR_FACSERP_DEV_CAJA_GEN
      );

  END FC_DEVOLUCIONDECAJA;

  --14
  FUNCTION FC_BORRARPAGO
  (
    /*
      NAME              : FC_BORRARPAGO --> EN ACCESS Form_delete en formulario BorrarPagos
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MIGRADOR     : 12/09/2016
      TIME              : 04:00 PM
      SOURCE MODULE     : SysmanSp2016.05.04
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       : Función que retorna un mensaje dependiendo de la acción realizada en lo que concierne a los conceptos de pago.
      PARAMETERS        : UN_COMPANIA      => CÓDIGO DE LA COMPAÑÍA ACTUAL.                          
                          UN_FECHA         => FECHA POR LA QUE SE VA A FILTRAR EN LAS CONSULTAS DEL MERGE Y CONDICIÓN DEL DELETE.
                          UN_BANCO         => BANCO POR EL QUE SE VA A FILTRAR EN LA CONDICION DE LOS UPDATE Y DELETE, ADEMÁS DE EN LAS CONSULTAS DEL MERGE.
                          UN_NUMEROPAQUETE => PAQUETE POR EL QUE SE VA A FILTRAR EN LA CONDICION DE LOS UPDATE Y DELETE, ADEMÁS DE EN LAS CONSULTAS DEL MERGE.
                          UN_CICLO         => CÓDIGO DEL CICLO ACTUAL Y POR EL QUE SE VA A FILTRAR EN LA CONDICION DE LOS UPDATE Y DELETE, 
                                              ADEMÁS DE EN LAS CONSULTAS DEL MERGE.                     
                          UN_CODIGORUTA    => CODIGO RUTA POR EL QUE SE VA A FILTRAR EN LA CONDICION DE LOS UPDATE Y DELETE, 
                                              ADEMÁS DE EN LAS CONSULTAS DEL MERGE.
                          UN_OPERACION     => PARÁMETRO QUE DETERMINA LA OPERACIÓN A REALIZAR.
                          UN_CONSECUTIVO   => CONSECUTIVO QUE SE TIENE EN CUENTA EN LA CONDICIÓN DE DELETE.
                          UN_GRUPO         => PARÁMETRO QUE DEFINE SI EL GRUPO ES CAJEROS O NO.
                          UN_VALORPAGO     => PARÁMETRO QUE DEFINE EL VALOR PAGO A ENVIAR A LA FUNCION FC_DEVOLUCIONDECAJA.
                          UN_USUARIO       => USUARIO ACTUAL.

      @NAME:    borrarPagos
      @METHOD:  GET
    */ 
    UN_COMPANIA       IN         PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHA          IN         DATE,
    UN_BANCO          IN         PCK_SUBTIPOS.TI_BANCO,
    UN_NUMEROPAQUETE  IN         SP_HISTORIA_EXTERNA.PAQUETE_PAGO%TYPE,
    UN_CICLO          IN         PCK_SUBTIPOS.TI_CICLO ,
    UN_CODIGORUTA     IN         PCK_SUBTIPOS.TI_CODIGORUTA,
    UN_OPERACION      IN        VARCHAR2, -- VARCHAR2 debido a que no existe un tipo de dato con las mismas características en el paquete PCK_SUBTIPOS.
    UN_CONSECUTIVO    IN        SP_PAGO.CONSECUTIVO%TYPE,
    UN_GRUPO          IN        VARCHAR2, -- VARCHAR2 debido a que no existe un tipo de dato con las mismas características en el paquete PCK_SUBTIPOS.
    UN_VALORPAGO      IN        PCK_SUBTIPOS.TI_DOBLE,
    UN_USUARIO        IN        PCK_SUBTIPOS.TI_USUARIO
  )
  RETURN VARCHAR2 AS
    MI_DBLVLRRECARGO          PCK_SUBTIPOS.TI_DOBLE;
    MI_COMPANIA               PCK_SUBTIPOS.TI_COMPANIA;
    MI_CICLO                  PCK_SUBTIPOS.TI_CICLO;
    MI_CODIGORUTA             PCK_SUBTIPOS.TI_CODIGORUTA;
    MI_BANCOPERPROCESO        SP_USUARIO.BANCOPERPROCESO%TYPE;
    MI_FECHAPAGOPERPROCESO    DATE;
    MI_PAQUETEPAGOPERPROCESO  SP_USUARIO.PAQUETEPAGOPERPROCESO%TYPE;
    MI_EXISTE                 PCK_SUBTIPOS.TI_ENTERO;
    MI_TABLAUPDATE            PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOSUPDATE           PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICIONUPDATE        PCK_SUBTIPOS.TI_CONDICION;
    MI_UPDATE                 PCK_SUBTIPOS.TI_ENTERO;
    MI_NUMCONCEPTOS           PCK_SUBTIPOS.TI_ENTERO;
    MI_RECARGOSTEPERIODO      PCK_SUBTIPOS.TI_ENTERO;
    MI_DBLCREDITO             PCK_SUBTIPOS.TI_DOBLE;
    MI_TABLADELETE            PCK_SUBTIPOS.TI_TABLA;
    MI_CONDICIONDELETE        PCK_SUBTIPOS.TI_CONDICION;
    MI_ANIO                   PCK_SUBTIPOS.TI_ANIO;
    MI_PERIODO                PCK_SUBTIPOS.TI_PERIODO;
    MI_MERGEUSING             PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE            PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE            PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_RTA                    VARCHAR2(500 CHAR); -- VARCHAR2 debido a que no existe un tipo de dato con las mismas características en el paquete PCK_SUBTIPOS.
  BEGIN

    BEGIN
      BEGIN
        SELECT  COMPANIA, 
                CICLO,
                CODIGORUTA,
                BANCOPERPROCESO,
                FECHAPAGOPERPROCESO,
                PAQUETEPAGOPERPROCESO
        INTO    MI_COMPANIA,
                MI_CICLO,
                MI_CODIGORUTA,
                MI_BANCOPERPROCESO,
                MI_FECHAPAGOPERPROCESO,
                MI_PAQUETEPAGOPERPROCESO
        FROM    SP_USUARIO
        WHERE   COMPANIA   = UN_COMPANIA
          AND   CICLO      = UN_CICLO
          AND   CODIGORUTA = UN_CODIGORUTA;

        EXCEPTION  WHEN NO_DATA_FOUND THEN
          MI_COMPANIA             :=NULL;
          MI_CICLO                :=NULL;
          MI_CODIGORUTA           :=NULL;
          MI_BANCOPERPROCESO      :=NULL;
          MI_FECHAPAGOPERPROCESO  :=NULL;
          MI_PAQUETEPAGOPERPROCESO:=NULL;
      END;

      IF MI_COMPANIA IS NULL THEN
        RETURN 'El usuario del pago ya no existe o no está permitido actualizarlo en este momento.';
      END IF;
      IF (MI_COMPANIA <> UN_COMPANIA OR MI_BANCOPERPROCESO <> UN_BANCO OR MI_FECHAPAGOPERPROCESO <> UN_FECHA OR MI_PAQUETEPAGOPERPROCESO <> UN_NUMEROPAQUETE) 
        AND UN_OPERACION <> 'D' THEN
        RETURN 'El registro de pago no corresponde a este periodo, por lo tanto, no se permite borrar.';
      END IF;

      BEGIN
        SELECT  VALOR_FACTURADO
        INTO    MI_DBLVLRRECARGO
        FROM    SP_FACTURADO 
        WHERE   COMPANIA   = UN_COMPANIA 
          AND   CICLO      = UN_CICLO 
          AND   CODIGORUTA = UN_CODIGORUTA 
          AND   CONCEPTO   = 247;

        EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_DBLVLRRECARGO := 0;
      END; 
      -- VERIFICAR QUE EL PAGO A ELIMINAR SI SEA DE ESTE PERIODO ES DECIR QUE
      -- EL PAQUETE,PERIODO, CICLO Y USUARIO COINCIDAN
      -- DE LO CONTRARIO NO PERMITIRLO
      -- STRETAPA = '1'
      BEGIN 
        SELECT  COUNT(*) EXISTE
        INTO    MI_EXISTE
        FROM    SP_USUARIO 
        WHERE   COMPANIA   = UN_COMPANIA 
          AND   CICLO      = UN_CICLO 
          AND   CODIGORUTA = UN_CODIGORUTA;

        EXCEPTION WHEN NO_DATA_FOUND THEN  
          MI_EXISTE := 0;
      END;  

      IF MI_EXISTE = 0 THEN
        RETURN 'El usuario del pago ya no existe o no está permitido actualizarlo en este momento.';
            --RSUSUARIO.CLOSE
            --EXIT SUB
      END IF;

      BEGIN
        SELECT  ANO, 
                PERIODO
        INTO    MI_ANIO,
                MI_PERIODO
        FROM    SP_USUARIO 
        WHERE   COMPANIA   = UN_COMPANIA 
          AND   CICLO      = UN_CICLO 
          AND   CODIGORUTA = UN_CODIGORUTA;

        EXCEPTION WHEN NO_DATA_FOUND THEN 
          MI_ANIO:=NULL;
          MI_PERIODO:=NULL;
      END; 

      IF UN_OPERACION NOT IN ('D') THEN
        MI_TABLAUPDATE     := 'SP_USUARIO';
        MI_CAMPOSUPDATE    := ' FECHAPAGOPERPROCESO   = NULL, '||
                              ' PAQUETEPAGOPERPROCESO = NULL,'||
                              ' NOFECHAPAGOPERPROCESO = NULL,'||
                              ' BANCOPERPROCESO       = NULL,'||
                              ' RECAUDADOPROCESO      = 0,'|| 
                              ' MODIFIED_BY           = '''||UN_USUARIO||''','||
                              ' DATE_MODIFIED         = SYSDATE,'||
                              (CASE WHEN PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                             UN_NOMBRE    => 'RECARGO SEGUNDA FECHA EN PERIODO SIGUIENTE',
                                                             UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                             UN_FECHA_PAR => SYSDATE) = 'NO' 
                                      AND UN_OPERACION = '2'
                                    THEN ',TOTFACTURAPERACTUAL = TOTFACTURAPERACTUAL - ' || (MI_DBLVLRRECARGO)
                                    ELSE ''
                              END);
        MI_CONDICIONUPDATE := '     COMPANIA   = ''' || UN_COMPANIA || ''' '||
                              ' AND CICLO      = '   || UN_CICLO || ' '||
                              ' AND CODIGORUTA = ''' || UN_CODIGORUTA || '''';
        MI_UPDATE          := MI_UPDATE + PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLAUPDATE,
                                                   UN_ACCION    => 'M',
                                                   UN_CAMPOS    => MI_CAMPOSUPDATE,
                                                   UN_CONDICION => MI_CONDICIONUPDATE);

        --'JP 10/10/2012 ACTUALIZA EL PAGO CUANDO ES TERCERIZADO EL ASEO
        IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                 UN_NOMBRE    => 'MANEJA PROCESO TERCERIZADO',
                                 UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                 UN_FECHA_PAR => SYSDATE) = 'SI' THEN 

          MI_MERGEUSING    := ' SELECT  H.COMPANIA,'||CHR(10)||CHR(13)|| 
                              '         H.CICLO,'||CHR(10)||CHR(13)|| 
                              '         H.CODIGORUTA,'||CHR(10)||CHR(13)|| 
                              '         H.ID_EMPRESA,'||CHR(10)||CHR(13)|| 
                              '         H.BANCO_PAGO,'||CHR(10)||CHR(13)|| 
                              '         H.PAQUETE_PAGO,'||CHR(10)||CHR(13)|| 
                              '         H.REVERSADO, '||CHR(10)||CHR(13)|| 
                              '         U.EMPRESAASEOEXT '||CHR(10)||CHR(13)|| 
                              ' FROM SP_HISTORIA_EXTERNA H '||CHR(10)||CHR(13)|| 
                              '   INNER JOIN SP_USUARIO U '||CHR(10)||CHR(13)|| 
                              '     ON  U.COMPANIA       = H.COMPANIA'||CHR(10)||CHR(13)|| 
                              '     AND U.CICLO          = H.CICLO'||CHR(10)||CHR(13)|| 
                              '     AND U.CODIGORUTA     = H.CODIGORUTA'||CHR(10)||CHR(13)|| 
                              '     AND U.EMPRESAASEOEXT = H.ID_EMPRESA '||CHR(10)||CHR(13)|| 
                              ' WHERE   U.COMPANIA     = ''' || UN_COMPANIA || ''''||CHR(10)||CHR(13)|| 
                              '   AND   U.CICLO        = ''' || UN_CICLO || ''''||CHR(10)||CHR(13)|| 
                              '   AND   U.CODIGORUTA   = ''' || UN_CODIGORUTA || ''''||CHR(10)||CHR(13)|| 
                              '   AND   H.ANO          = ''' || MI_ANIO || ''''||CHR(10)||CHR(13)|| 
                              '   AND   H.PERIODO      = ''' || MI_PERIODO || ''' '||CHR(10)||CHR(13)|| 
                              '   AND   H.FECHA_PAGO   = TO_DATE(''' || UN_FECHA || ''',''DD/MM/YYYY'')'||CHR(10)||CHR(13)|| 
                              '   AND   H.BANCO_PAGO   = ''' || UN_BANCO || ''' '||CHR(10)||CHR(13)|| 
                              '   AND   H.PAQUETE_PAGO = ''' || UN_NUMEROPAQUETE || '''';
          MI_MERGEENLACE   := '     TABLA.COMPANIA   = VISTA.COMPANIA '||CHR(10)||CHR(13)|| 
                              ' AND TABLA.CICLO      = VISTA.CICLO '||CHR(10)||CHR(13)|| 
                              ' AND TABLA.CODIGORUTA = VISTA.CODIGORUTA '||CHR(10)||CHR(13)|| 
                              ' AND TABLA.ID_EMPRESA = VISTA.EMPRESAASEOEXT ';
          MI_MERGEEXISTE   := ' UPDATE '||
                              '   SET TABLA.FECHA_PAGO   = NULL, '||CHR(10)||CHR(13)|| 
                              '       TABLA.BANCO_PAGO   = NULL, '||CHR(10)||CHR(13)|| 
                              '       TABLA.PAQUETE_PAGO = NULL, '||CHR(10)||CHR(13)|| 
                              '       TABLA.REVERSADO    = -1';
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => 'SP_HISTORIA_EXTERNA',
                                                UN_ACCION      => 'MM',
                                                UN_MERGEUSING  => MI_MERGEUSING, 
                                                UN_MERGEENLACE => MI_MERGEENLACE,
                                                UN_MERGEEXISTE => MI_MERGEEXISTE);
        END IF;
        --'19/02/2014 JP PARA ACTUALIZAR LOS CONVENIOS
        IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                 UN_NOMBRE    => 'MANEJA CONVENIO DE FACTURACION',
                                 UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                 UN_FECHA_PAR => SYSDATE) = 'SI' THEN 
          MI_TABLAUPDATE     := 'SP_HISTORIA_CONVENIOS';
          MI_CAMPOSUPDATE    := ' UPDATE '||
                                '  SET  FECHA_PAGO   = NULL, '||
                                '       BANCO_PAGO   = NULL, '||
                                '       PAQUETE_PAGO = NULL, '||
                                '       REVERSADO    = -1, ' ||
                                ' MODIFIED_BY           = '''||UN_USUARIO||''','||
                                ' DATE_MODIFIED         = SYSDATE';
          MI_CONDICIONUPDATE := '     COMPANIA     = ''' || UN_COMPANIA || ''' '||
                                ' AND CICLO        = ''' || UN_CICLO || ''' '||
                                ' AND CODIGORUTA   = ''' || UN_CODIGORUTA || ''' '||
                                ' AND ANO          = ''' || MI_ANIO || ''' '||
                                ' AND PERIODO      = ''' || MI_PERIODO || ''' '||
                                ' AND NOCOBRAR     IN (0) '||
                                ' AND FECHA_PAGO   =  TO_DATE(SYSDATE,''DD/MM/YYYY'') '||
                                ' AND BANCO_PAGO   = ''' || UN_BANCO ||' '||
                                ' AND PAQUETE_PAGO = ''' || UN_NUMEROPAQUETE || ''''; 
          MI_UPDATE          := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLAUPDATE,
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOSUPDATE,
                                                  UN_CONDICION => MI_CONDICIONUPDATE);   
        END IF;
        --'26/08/2015 JP PARA ELIMINAR PRODUCTIVIDAD
        -- ELIMINARECPROD GETCOMPANY(), RSUSUARIO!CICLO, RSUSUARIO!CODIGORUTA, ME!FECHA, FORMS!BORRARPAGOS!BANCO, FORMS!BORRARPAGOS!NUMEROPAQUETE, 'PAGO'
        PCK_SERVICIOS_PUBLICOS_COM1.PR_ELIMINARECPROD(UN_COMPANIA  => UN_COMPANIA,
                                                      UN_CICLO     => UN_CICLO,
                                                      UN_USUARIO   => UN_CODIGORUTA,
                                                      UN_FECHA     => UN_FECHA,
                                                      UN_BANCO     => UN_BANCO,
                                                      UN_PAQUETE   => UN_NUMEROPAQUETE,
                                                      UN_OPERACION => 'PAGO');

        IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                 UN_NOMBRE    => 'PERMITE CARGAR Y RECAUDAR NOVEDADES EXTERNAS',
                                 UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                 UN_FECHA_PAR => SYSDATE) = 'SI' THEN

          MI_TABLAUPDATE     := 'SP_FACTURADO_EXTERNO'; 
          MI_CAMPOSUPDATE    := ' VALOR_RECAUDO = 0, '||CHR(10)||CHR(13)|| 
                                ' FECHA_PAGO    = NULL, '||CHR(10)||CHR(13)|| 
                                ' BANCO_PAGO    = NULL, '||CHR(10)||CHR(13)|| 
                                ' PAQUETE_PAGO  = NULL, '||CHR(10)||CHR(13)|| 
                                ' CODIGO_BARRA  = NULL, '||
                                ' MODIFIED_BY   = '''||UN_USUARIO||''','||
                                ' DATE_MODIFIED = SYSDATE';
          MI_CONDICIONUPDATE := '     COMPANIA   = ''' || UN_COMPANIA || ''' '||CHR(10)||CHR(13)|| 
                                ' AND CICLO      = ''' || UN_CICLO || ''' '||CHR(10)||CHR(13)|| 
                                ' AND CODIGORUTA = ''' || UN_CODIGORUTA || ''' '||CHR(10)||CHR(13)|| 
                                ' AND ANULADO    IN (0)';
          MI_UPDATE          := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLAUPDATE,
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOSUPDATE,
                                                  UN_CONDICION => MI_CONDICIONUPDATE);
          IF MI_UPDATE <= 0 THEN
            RETURN  'No se reversó correctamente el concepto externo';
          END IF;
        END IF;
      END IF;
       --' ***** RUTINA ADICIONADA POR HPV EN ABRIL 15 DE 2002 PARA REVERSAR DETALLE_RECAUDOS
       --STRETAPA = '4'
      MI_NUMCONCEPTOS := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                               UN_NOMBRE    => 'NUMERO MAXIMO DE CONCEPTOS',
                                               UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                               UN_FECHA_PAR => SYSDATE);

      IF NVL(MI_NUMCONCEPTOS,0) <= 0 THEN
        MI_NUMCONCEPTOS := 250;
      END IF;
      --STRETAPA = '5'
      IF UN_OPERACION = '1' OR UN_OPERACION = '2' THEN
           --STRETAPA = '6'
        IF UN_OPERACION = '2' AND PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                        UN_NOMBRE    => 'RECARGO SEGUNDA FECHA EN PERIODO SIGUIENTE',
                                                        UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                        UN_FECHA_PAR => SYSDATE) <> 'SI' THEN
          MI_RECARGOSTEPERIODO:= -1;
        ELSE
          MI_RECARGOSTEPERIODO:= 0;
        END IF;

        MI_MERGEUSING    := ' SELECT  SP_FACTURADO.COMPANIA, '||
                            '         SP_FACTURADO.CONCEPTO, '||
                            '         SP_FACTURADO.DEUDA, '||
                            '         SP_FACTURADO.VALOR_FACTURADO, '||
                            '         SP_FACTURADO.VALORFINACT, '||
                            '         SP_FACTURADO.VALORFINANT, '||
                            '         SP_FACTURADO.CREDITOABONADO '||
                            ' FROM    SP_D_RECAUDO '||
                            '  INNER JOIN SP_FACTURADO '||
                            '    ON  SP_D_RECAUDO.COMPANIA = SP_FACTURADO.COMPANIA '||
                            '    AND SP_D_RECAUDO.CONCEPTO = SP_FACTURADO.CONCEPTO '||
                            ' WHERE   SP_D_RECAUDO.COMPANIA      = ''' || UN_COMPANIA ||''''||
                            '   AND   SP_D_RECAUDO.FECHA         = TO_DATE(''' || UN_FECHA || ''', ''DD/MM/YYYY'')'||
                            '   AND   SP_D_RECAUDO.BANCO         = ''' || UN_BANCO || ''''||
                            '   AND   SP_D_RECAUDO.NUMEROPAQUETE = ''' || UN_NUMEROPAQUETE || '''' ||
                            '   AND   SP_D_RECAUDO.CONCEPTO      NOT IN ('||MI_NUMCONCEPTOS || ')'||
                            '   AND   SP_FACTURADO.CICLO         = ''' || UN_CICLO || ''' '||
                            '   AND   SP_FACTURADO.CODIGORUTA    = ''' || UN_CODIGORUTA || ''' '||
                            '   AND   SP_FACTURADO.ANO           = ''' || MI_ANIO || ''' '|| 
                            '   AND   SP_FACTURADO.PERIODO       = ''' || MI_PERIODO || '''';
        MI_MERGEEXISTE   := ' UPDATE '|| 
                            '   SET TABLA.VALORDEUDA       = TABLA.VALORDEUDA - '||
                                                            ' (CASE WHEN TABLA.CONCEPTO='||(MI_NUMCONCEPTOS - 3)||
                                                            '       THEN 0 '||
                                                            '       ELSE NVL(VISTA.DEUDA, 0)'||
                                                            ' END), '||
                            '       TABLA.VALORPAGOPERIODO = TABLA.VALORPAGOPERIODO - '||
                                                            ' (CASE WHEN TABLA.CONCEPTO = '||(MI_NUMCONCEPTOS - 3)|| 
                                                            '       AND ''TRUE'' = '''||(CASE WHEN MI_RECARGOSTEPERIODO NOT IN (0)
                                                                                              THEN 'TRUE'
                                                                                              ELSE 'FALSE'
                                                                                              END)||''' 
                                                                    OR  TABLA.CONCEPTO NOT IN ('||(MI_NUMCONCEPTOS - 3)||')'||
                                                             '      THEN NVL(VISTA.VALOR_FACTURADO,0)'||
                                                             '      ELSE 0 '||
                                                             '      END), '||
                            '       TABLA.VALORFINACT      = NVL(TABLA.VALORFINACT,0) - NVL(VISTA.VALORFINACT,0), '||
                            '       TABLA.VALORFINANT      = NVL(TABLA.VALORFINANT,0) - NVL(VISTA.VALORFINANT,0),'||
                            '       TABLA.CREDITOABONADO   = TABLA.CREDITOABONADO - VISTA.CREDITOABONADO ';
        MI_MERGEENLACE   := '     TABLA.COMPANIA = VISTA.COMPANIA '||
                            ' AND TABLA.CONCEPTO = VISTA.CONCEPTO ';  
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => 'SP_D_RECAUDO',
                                              UN_ACCION      => 'MM',
                                              UN_MERGEUSING  => MI_MERGEUSING, 
                                              UN_MERGEENLACE => MI_MERGEENLACE,
                                              UN_MERGEEXISTE => MI_MERGEEXISTE);

        IF UN_OPERACION = '2' AND PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                        UN_NOMBRE    => 'RECARGO SEGUNDA FECHA EN PERIODO SIGUIENTE',
                                                        UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                        UN_FECHA_PAR => SYSDATE) = 'NO' THEN
          MI_TABLAUPDATE     := 'SP_FACTURADO'; 
          MI_CAMPOSUPDATE    := ' VALOR_FACTURADO = 0,'||
                                ' MODIFIED_BY   = '''||UN_USUARIO||''','||
                                ' DATE_MODIFIED = SYSDATE';
          MI_CONDICIONUPDATE := '     COMPANIA   = ''' || UN_COMPANIA || ''' '||
                                ' AND CICLO      = ''' || UN_CICLO || ''' '||
                                ' AND CODIGORUTA = ''' || UN_CODIGORUTA ||''' '||
                                ' AND ANO        = '   || MI_ANIO ||
                                ' AND PERIODO    = ''' || MI_PERIODO ||''' '||
                                ' AND CONCEPTO   = 247';
          PCK_DATOS.GL_RTA   := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLAUPDATE,
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOSUPDATE,
                                                  UN_CONDICION => MI_CONDICIONUPDATE);
        END IF;   
      END IF;

      IF UN_OPERACION IN ('D') THEN
        MI_DBLCREDITO:= 0;
        --STRETAPA = '10'
        BEGIN 
          SELECT   NVL(SUM(NVL(VALOR_FACTURADO, 0) + NVL(DEUDA, 0)), 0) SALDOCREDITO
          INTO     MI_DBLCREDITO
          FROM     SP_FACTURADO 
          WHERE    COMPANIA   = UN_COMPANIA 
             AND    CICLO      = UN_CICLO 
             AND    CODIGORUTA = UN_CODIGORUTA 
             AND    ANO        = MI_ANIO 
             AND    PERIODO    = MI_PERIODO
             AND    CONCEPTO   < 49 
              OR    CONCEPTO   > 246 
             AND    CONCEPTO   NOT IN (MI_NUMCONCEPTOS);

          EXCEPTION WHEN NO_DATA_FOUND THEN
              MI_DBLCREDITO:= 0;
        END;

        --STRETAPA = '11'
        MI_TABLAUPDATE     := 'SP_FACTURADO'; 
        MI_CAMPOSUPDATE    := ' SALDOCREDITO = SALDOCREDITO 
                                - NVL(DEUDA, 0) 
                                - NVL(VALOR_FACTURADO, 0) ,'||
                              ' MODIFIED_BY   = '''||UN_USUARIO||''','||
                              ' DATE_MODIFIED = SYSDATE';
        MI_CONDICIONUPDATE := '     COMPANIA   = ''' || UN_COMPANIA || ''' '|| 
                              ' AND CICLO      = ' || UN_CICLO || ' '||
                              ' AND CODIGORUTA = ''' || UN_CODIGORUTA || ''' '||
                              ' AND ANO        = ' || MI_ANIO || ' '||
                              ' AND PERIODO    = ' || MI_PERIODO || ' '||
                              ' AND CONCEPTO   NOT IN (' || MI_NUMCONCEPTOS || ')';
        PCK_DATOS.GL_RTA   := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLAUPDATE,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOSUPDATE,
                                              UN_CONDICION => MI_CONDICIONUPDATE);
        --STRETAPA = '12'
        MI_TABLADELETE     := 'SP_D_PAGOSDOBLES'; 
        MI_CONDICIONDELETE := '     COMPANIA      = '''||UN_COMPANIA||''' ' ||
                              ' AND FECHA         = TO_DATE(''' || UN_FECHA || ''',''DD/MM/YYYY'') '||
                              ' AND BANCO         = ''' || UN_BANCO || ''' '|| 
                              ' AND NUMEROPAQUETE = ''' || UN_NUMEROPAQUETE || ''' '||
                              ' AND CODIGORUTA    = ''' || UN_CODIGORUTA || '''';
        PCK_DATOS.GL_RTA   := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLADELETE,
                                                UN_ACCION    => 'E',
                                                UN_CONDICION => MI_CONDICIONDELETE); 
        --STRETAPA = '13'
        --RSUSUARIO.EDIT
        MI_TABLAUPDATE     := 'SP_USUARIO';
        MI_CAMPOSUPDATE    := ' NOTACREDITO = NOTACREDITO 
                                + CREDITOABONADO 
                                - '||MI_DBLCREDITO||','||
                              ' MODIFIED_BY   = '''||UN_USUARIO||''','||
                              ' DATE_MODIFIED = SYSDATE';
        MI_CONDICIONUPDATE := '     COMPANIA   = ''' || UN_COMPANIA || ''' '||
                              ' AND CICLO      = ' || UN_CICLO || ' '||
                              ' AND CODIGORUTA = ' || UN_CODIGORUTA;
        PCK_DATOS.GL_RTA   := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLAUPDATE,
                                                UN_ACCION    => 'M',
                                                UN_CAMPOS    => MI_CAMPOSUPDATE,
                                                UN_CONDICION => MI_CONDICIONUPDATE);
        --RSUSUARIO.UPDATE
        --'JP 10/10/2012 ACTUALIZA EL PAGO CUANDO ES TERCERIZADO EL ASEO
        --ACTPAGOTER NUMCICLO, CODRUTA, RSUSUARIO!ANO, RSUSUARIO!PERIODO, DATE, FORMS!BORRARPAGOS!BANCO, FORMS!BORRARPAGOS!NUMEROPAQUETE, TRUE, TRUE, ME!TERCE, 0
        IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                 UN_NOMBRE    => 'MANEJA PROCESO TERCERIZADO',
                                 UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                 UN_FECHA_PAR => SYSDATE) = 'SI' THEN 
          MI_MERGEUSING   := '  SELECT H.COMPANIA,'||CHR(10)||CHR(13)|| 
                             '         H.CICLO,'||CHR(10)||CHR(13)|| 
                             '         H.CODIGORUTA,'||CHR(10)||CHR(13)|| 
                             '         H.ID_EMPRESA,'||CHR(10)||CHR(13)|| 
                             '         H.BANCO_PAGO,'||CHR(10)||CHR(13)|| 
                             '         H.PAQUETE_PAGO,'||CHR(10)||CHR(13)|| 
                             '         H.REVERSADO, '||CHR(10)||CHR(13)|| 
                             '         U.EMPRESAASEOEXT '||CHR(10)||CHR(13)|| 
                             '  FROM SP_HISTORIA_EXTERNA H '||CHR(10)||CHR(13)|| 
                             '   INNER JOIN SP_USUARIO U ON '||CHR(10)||CHR(13)|| 
                             '    U.COMPANIA       = H.COMPANIA AND '||CHR(10)||CHR(13)|| 
                             '    U.CICLO          = H.CICLO AND '||CHR(10)||CHR(13)|| 
                             '    U.CODIGORUTA     = H.CODIGORUTA AND '||CHR(10)||CHR(13)|| 
                             '    U.EMPRESAASEOEXT = H.ID_EMPRESA '||CHR(10)||CHR(13)|| 
                             ' WHERE U.COMPANIA     ='''||UN_COMPANIA||''''||CHR(10)||CHR(13)|| 
                             '   AND U.CICLO        ='''||UN_CICLO||''''||CHR(10)||CHR(13)|| 
                             '   AND U.CODIGORUTA   ='''||UN_CODIGORUTA||''''||CHR(10)||CHR(13)|| 
                             '   AND H.ANO          ='''||MI_ANIO||''''||CHR(10)||CHR(13)|| 
                             '   AND H.PERIODO      ='''||MI_PERIODO||''' '||CHR(10)||CHR(13)|| 
                             '   AND H.FECHA_PAGO_DOBLE   = TO_DATE('''||UN_FECHA||''',''DD/MM/YYYY'')'||CHR(10)||CHR(13)|| 
                             '   AND H.BANCO_PAGO_DOBLE   = '''||UN_BANCO||''' '||CHR(10)||CHR(13)|| 
                             '   AND H.PAQUETE_PAGO_DOBLE = '''||UN_NUMEROPAQUETE||'''';
         MI_MERGEENLACE   := '   TABLA.COMPANIA       = VISTA.COMPANIA AND '||CHR(10)||CHR(13)|| 
                             '   TABLA.CICLO          = VISTA.CICLO AND '||CHR(10)||CHR(13)|| 
                             '   TABLA.CODIGORUTA     = VISTA.CODIGORUTA AND '||CHR(10)||CHR(13)|| 
                             '   TABLA.ID_EMPRESA     = VISTA.EMPRESAASEOEXT ';
         MI_MERGEEXISTE   := 'UPDATE SET TABLA.FECHA_PAGO_DOBLE   = NULL, '||CHR(10)||CHR(13)|| 
                             '           TABLA.BANCO_PAGO_DOBLE   = NULL, '||CHR(10)||CHR(13)|| 
                             '           TABLA.PAQUETE_PAGO_DOBLE = NULL, '||CHR(10)||CHR(13)|| 
                             '           TABLA.REVERSADO          = -1';
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => 'SP_HISTORIA_EXTERNA',
                                               UN_ACCION      => 'MM',
                                               UN_MERGEUSING  => MI_MERGEUSING, 
                                               UN_MERGEENLACE => MI_MERGEENLACE,
                                               UN_MERGEEXISTE => MI_MERGEEXISTE);
        END IF;

        --'19/02/2014 JP PARA ACTUALIZAR LOS CONVENIOS
        --ACTPAGOCONVE NUMCICLO, CODRUTA, RSUSUARIO!ANO, RSUSUARIO!PERIODO, DATE, FORMS!BORRARPAGOS!BANCO, FORMS!BORRARPAGOS!NUMEROPAQUETE, TRUE, TRUE, ME!CONVE, 0
        IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                 UN_NOMBRE    => 'MANEJA CONVENIO DE FACTURACION',
                                 UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                 UN_FECHA_PAR => SYSDATE) = 'SI' THEN 
          MI_TABLAUPDATE     := 'SP_HISTORIA_CONVENIOS';
          MI_CAMPOSUPDATE    := ' FECHA_PAGO_DOBLE   = NULL, '||
                                ' BANCO_PAGO_DOBLE   = NULL, '||
                                ' PAQUETE_PAGO_DOBLE = NULL, '||
                                ' REVERSADO          = -1,'||
                                ' MODIFIED_BY   = '''||UN_USUARIO||''','||
                                ' DATE_MODIFIED = SYSDATE';
          MI_CONDICIONUPDATE := '     COMPANIA           = ''' || UN_COMPANIA || ''' '||
                                ' AND CICLO              = ''' || UN_CICLO || ''' '||
                                ' AND CODIGORUTA         = ''' || UN_CODIGORUTA || ''' '||
                                ' AND ANO                = ''' || MI_ANIO || ''' '||
                                ' AND PERIODO            = ''' || MI_PERIODO || ''' '||
                                ' AND NOCOBRAR           IN (0) '||
                                ' AND FECHA_PAGO_DOBLE   =  TO_DATE(SYSDATE,''DD/MM/YYYY'') '||
                                ' AND BANCO_PAGO_DOBLE   = ''' || UN_BANCO ||''' '||
                                ' AND PAQUETE_PAGO_DOBLE = ''' || UN_NUMEROPAQUETE || ''''; 
          PCK_DATOS.GL_RTA   := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLAUPDATE,
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOSUPDATE,
                                                  UN_CONDICION => MI_CONDICIONUPDATE);
        END IF;
      END IF;

      -- 'AQUÍ REALIZA EL MOVIMIENTO DE ANULACIÓN DE CAJA
      IF UN_GRUPO = 'CAJEROS' THEN
        MI_RTA := MI_RTA||' '||PCK_SERVICIOS_PUBLICOS_COM1.FC_DEVOLUCIONDECAJA(UN_COMPANIA   => UN_COMPANIA,
                                                                               UN_TIPO       => '02',
                                                                               UN_CLASE      => 'S',
                                                                               UN_FECHA      => UN_FECHA,
                                                                               UN_DBLVALOR   => UN_VALORPAGO,
                                                                               UN_USUARIO    => UN_USUARIO,
                                                                               UN_TIPOANULAR => '01');
      END IF;
      MI_RTA := MI_RTA||' '||'Proceso Terminado.'; 

      MI_TABLADELETE     := 'SP_PAGO'; 
      MI_CONDICIONDELETE := '     COMPANIA      = ''' || UN_COMPANIA || ''' '||
                            ' AND FECHA         = TO_DATE(''' || UN_FECHA || ''',''DD/MM/YYYY'') '||
                            ' AND BANCO         = ''' || UN_BANCO || ''' '||
                            ' AND NUMEROPAQUETE = ''' || UN_NUMEROPAQUETE || ''' '||
                            ' AND CODIGORUTA    = ''' || UN_CODIGORUTA || ''' '||
                            ' AND CONSECUTIVO   = ''' || UN_CONSECUTIVO || ''''; 
     PCK_DATOS.GL_RTA   := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLADELETE,
                                             UN_ACCION    => 'E',
                                             UN_CONDICION => MI_CONDICIONDELETE); 

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;

    END;

    RETURN MI_RTA ;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERR_FACSERP_BORRAR_PAGO
      );

  END FC_BORRARPAGO;


  PROCEDURE PR_ELIMINARECPROD
  (
    /*
      NAME              : PR_ELIMINARECPROD --> EN ACCESS ELIMINARECPROD 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MIGRADOR     : 12/09/2016
      TIME              : 05:00 PM
      SOURCE MODULE     : SysmanSp2016.05.04
      MODIFIER          : YEISSON ALEJANDRO ROJAS RUIZ
      DATE MODIFIED     : 10/01/2017
      TIME              : 04:30 PM
      MODIFICATIONS     : CORRECCIÓN SEGÚN ESTÁNDAR DE PROGRAMACIÓN Y OPTIMIZACIÓN DEL MANEJO DE ERRORES.
      DESCRIPTION       : 
      PARAMETERS        : UN_COMPANIA  => CÓDIGO DE LA COMPAÑÍA ACTUAL.
                          UN_CICLO     => CÓDIGO DEL CICLO ACTUAL Y POR EL QUE SE VA A FILTRAR EN LA CONDICION DEL DELETE.
                          UN_USUARIO   => USUARIO ACTUAL. 
                          UN_FECHA     => FECHA POR LA QUE SE VA A FILTRAR EN LA CONDICION DEL DELETE.
                          UN_BANCO     => BANCO POR EL QUE SE VA A FILTRAR EN LA CONDICION DEL DELETE.
                          UN_PAQUETE   => PAQUETE POR EL QUE SE VA A FILTRAR EN LA CONDICION DE LOS UPDATE Y DELETE, ADEMÁS DE EN LAS CONSULTAS DEL MERGE.
                          UN_OPERACION => PARÁMETRO QUE DETERMINA LA OPERACIÓN A REALIZAR.


      @NAME:    eliminarRecProd
      @METHOD:  DELETE
    */ 
    UN_COMPANIA     IN    PCK_SUBTIPOS.TI_COMPANIA,
    UN_CICLO        IN    PCK_SUBTIPOS.TI_CICLO,
    UN_USUARIO      IN    PCK_SUBTIPOS.TI_USUARIO,
    UN_FECHA        IN    DATE,
    UN_BANCO        IN    PCK_SUBTIPOS.TI_BANCO,
    UN_PAQUETE      IN    SP_RECAUDO_PRODUCTIVIDAD.PAQUETE%TYPE,
    UN_OPERACION    IN    SP_RECAUDO_PRODUCTIVIDAD.OPERACION%TYPE
  )
  AS
    --'StrOperacion Puede tener los siguientes valores
    -- ' PAGO
    -- ' ABONO
    -- ' DOBLE
    MI_TABLADELETE      PCK_SUBTIPOS.TI_TABLA;
    MI_CONDICIONDELETE  PCK_SUBTIPOS.TI_CONDICION;

  BEGIN
    BEGIN
      IF (UPPER(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                      UN_NOMBRE    => 'DESCONTAR PRODUCTIVIDAD',
                                      UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                      UN_FECHA_PAR => SYSDATE))) IN ('SI') THEN
        MI_TABLADELETE     := 'SP_RECAUDO_PRODUCTIVIDAD';
        MI_CONDICIONDELETE := '     COMPANIA   = ''' || UN_COMPANIA || ''' '|| 
                              ' AND CICLO      = ''' || UN_CICLO || ''' '||
                              ' AND CODIGORUTA = ''' || UN_USUARIO || ''' '||
                              ' AND FECHA      = TO_DATE(''' || UN_FECHA || ''',''DD/MM/YYYY HH24:MI:SS'') '||
                              ' AND BANCO      = ''' || UN_BANCO || ''' '||
                              ' AND PAQUETE    = ''' || UN_PAQUETE || ''' '||
                              ' AND OPERACION  = ''' || UN_OPERACION || '''';
        PCK_DATOS.GL_RTA   := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLADELETE,
                                                UN_ACCION    => 'E',
                                                UN_CONDICION => MI_CONDICIONDELETE);

      END IF;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;

    END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERR_FACSERP_REC_PROD
      );

  END PR_ELIMINARECPROD;


END PCK_SERVICIOS_PUBLICOS_COM1;