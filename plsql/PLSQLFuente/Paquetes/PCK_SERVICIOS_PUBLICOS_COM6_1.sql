create or replace PACKAGE BODY PCK_SERVICIOS_PUBLICOS_COM6 AS

  -- 1
  FUNCTION FC_GEN_ACTA
    /*
      NAME              : FC_GEN_ACTA
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JUAN SEBASTIAN FORERO NOGUERA
      DATE MIGRADOR     : 07/02/2017
      TIME              : 4:40 PM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : METODO QUE GENERA LAS ACTAS DE CAMBIO DE MEDIDOR, PARTIENDO DE UN NUMERO DE ACTA INICIAL
                          ESTE METODO RETORNA EL NUMERO DE ACTAS QUE GENERO.
      PARAMETERS        : UN_COMPANIA => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_CODIGOINICIAL    => CODIGO DE RUTA INICIAL.
                          UN_CODIGOFINAL      => CODIGO DE RUTA FINAL.
                          UN_CICLO            => CICLO AL QUE PERTENECE EL RANGO DE CODIGOS.
                          UN_PROBLEMAINICIAL  => PROBLEMA INICIAL PARA GENERAR EL ACTA.
                          UN_PROBLEMAFINAL    => PROBLEMA FINAL PARA GENERAR EL ACTA.
                          UN_ACTA             => ACTA DESDE LA CUAL SE VA A INICIAR                            
      MODIFICATIONS     : 
      @NAME:  generarActa
      @METHOD: GET
    */
  (
    UN_COMPANIA        IN    PCK_SUBTIPOS.TI_COMPANIA
   ,UN_CODIGOINICIAL   IN    PCK_SUBTIPOS.TI_CODIGORUTA
   ,UN_CODIGOFINAL     IN    PCK_SUBTIPOS.TI_CODIGORUTA
   ,UN_CICLO           IN    PCK_SUBTIPOS.TI_CICLO
   ,UN_PROBLEMAINICIAL IN    VARCHAR2
   ,UN_PROBLEMAFINAL   IN    VARCHAR2 
   ,UN_ACTA            IN    PCK_SUBTIPOS.TI_ENTERO 
  )
    RETURN NUMBER
  AS
    MI_ACTA      PCK_SUBTIPOS.TI_ENTERO; -- Acta desde la cual se va a empezar a generar las actas
    MI_CAMPOS    PCK_SUBTIPOS.TI_CAMPOS; -- Campos que se van a insertar en la tabla ps_actacambiomedidor
    MI_VALORES   PCK_SUBTIPOS.TI_VALORES; -- Valores que se van a ingresar en los campos
  BEGIN 
    MI_ACTA := UN_ACTA;
    <<MI_UPRO>>
    FOR MI_UPRO IN(SELECT DISTINCT SP_USUARIO_PROBLEMA.ANO 
                         ,SP_USUARIO_PROBLEMA.CICLO 
                         ,SP_USUARIO_PROBLEMA.CLASE
                         ,SP_USUARIO_PROBLEMA.CODIGORUTA
                         ,SP_USUARIO_PROBLEMA.LECTURA
                         ,SP_USUARIO_PROBLEMA.PERIODO
                         ,SP_USUARIO_PROBLEMA.PROBLEMA
                   FROM   SP_CICLO 
                     LEFT JOIN SP_USUARIO_PROBLEMA 
                       ON      SP_CICLO.PERIODO  = SP_USUARIO_PROBLEMA.PERIODO
                      AND      SP_CICLO.ANO      = SP_USUARIO_PROBLEMA.ANO 
                      AND      SP_CICLO.COMPANIA = SP_USUARIO_PROBLEMA.COMPANIA
                  WHERE   SP_USUARIO_PROBLEMA.COMPANIA   IS NOT NULL 
                    AND   SP_CICLO.COMPANIA              = UN_COMPANIA 
                    AND   SP_USUARIO_PROBLEMA.CICLO      = UN_CICLO 
                    AND   SP_USUARIO_PROBLEMA.CODIGORUTA BETWEEN UN_CODIGOINICIAL AND UN_CODIGOFINAL
                    AND   SP_USUARIO_PROBLEMA.PROBLEMA   BETWEEN UN_PROBLEMAINICIAL AND UN_PROBLEMAFINAL) 
    LOOP
      MI_CAMPOS := 'COMPANIA,
                    CICLO,
                    CODIGORUTA,
                    IDACTA,
                    FECHAACTA';
      MI_VALORES := ''''||UN_COMPANIA||''',
                       '||UN_CICLO||',
                     '''||MI_UPRO.CODIGORUTA||''', 
                     '''||MI_ACTA||''',SYSDATE';
      BEGIN 
        -- EL NUMERO DE ACTA VA A INCREMENTADO A MEDAD QUE SE LE INSERTAN 
        -- REGISTROS , POR ESO SE LE SUMA EL RETORNO DE LA FUNCION ACME, 
        -- QUE PARA ESTE CASO SIEMPRE A SER 1
        MI_ACTA := MI_ACTA + PCK_DATOS.FC_ACME(UN_TABLA   => 'SP_ACTACAMBIOMEDIDOR',
                                               UN_ACCION  => 'I', 
                                               UN_CAMPOS  => MI_CAMPOS, 
                                               UN_VALORES => MI_VALORES); 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;                    
    END LOOP MI_UPRO;
    -- AL RETORNO SE LE RESTA UNO, YA QUE EN REALIDAD SE INSERTARON ESTA CANTIDAD DE DATOS 
    RETURN MI_ACTA - 1; 
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERR_FACSERP_GENACTA
      );
  END FC_GEN_ACTA;

  -- 2
  FUNCTION FC_AUTORIZACION_DESCTOTFACTURA 
    /*
      NAME              : FC_AUTORIZACION_DESCTOTFACTURA --> EN ACCESS AUTORIZACION_DESCTOTFACTURA
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MIGRADOR     : 03/02/2017
      TIME              : 11:25 AM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : FUNCION QUE VERIFICA SI HAY DESCUENTO CONTROLADO POR TOTAL DE FACTURA 
                          UNICAMENTE PARA NOBSA.
      PARAMETERS        : UN_COMPANIA => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_NIT      => NUMERO DE NIT QUE SE VA A EVALUAR.
      MODIFICATIONS     : 
      @NAME:  autorizarDescuentoTotal
      @METHOD:  GET
    */ 
  (
    UN_COMPANIA       IN  PCK_SUBTIPOS.TI_COMPANIA
   ,UN_NIT            IN  VARCHAR2 -- VARCHAR2 debido a que no existe un tipo de dato con las mismas características en el paquete PCK_SUBTIPOS.
  )
    RETURN NUMBER
  AS
    MI_RESULTADO      PCK_SUBTIPOS.TI_LOGICO;
  BEGIN
    MI_RESULTADO := 0;
    IF PCK_SERVICIOS_PUBLICOS_COM1.FC_NITVALIDAR(UN_COMPANIA => UN_COMPANIA,
                                                 UN_NIT      => UN_NIT) = '900331439' THEN
      MI_RESULTADO := PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                                                  UN_NOMBRE    => 'DESCUENTO CONTROLADO TOTAL DE LA FACTURA',
                                                                                  UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                                                  UN_FECHA_PAR => SYSDATE) = 'SI',
                                            UN_SI        => 1,
                                            UN_NO        => 0);
    END IF;
    RETURN MI_RESULTADO;
  END FC_AUTORIZACION_DESCTOTFACTURA;

  -- 3
  FUNCTION FC_AUTORIZALIMPIADESHCIERRE
    /*
      NAME              : FC_AUTORIZALIMPIADESHCIERRE --> EN ACCESS AUTORIZACION_LIMPIADESHCIERRE
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MIGRADOR     : 03/02/2017
      TIME              : 03:51 PM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : FUNCION QUE VERIFICA SI SE REALIZA LIMPIEZA DE DESHABILITADOS EN CIERRE, PARA CHIA.
      PARAMETERS        : UN_COMPANIA => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_NIT      => NUMERO DE NIT QUE SE VA A EVALUAR.
      MODIFICATIONS     : 
      @NAME:  autorizarLimpiaDeshabilitados
      @METHOD:  GET
    */
  (
    UN_COMPANIA       IN  PCK_SUBTIPOS.TI_COMPANIA
   ,UN_NIT            IN  VARCHAR2 -- VARCHAR2 debido a que no existe un tipo de dato con las mismas características en el paquete PCK_SUBTIPOS.
  )
    RETURN NUMBER
  AS
    MI_RESULTADO      PCK_SUBTIPOS.TI_LOGICO;
  BEGIN
    MI_RESULTADO := 0;
    IF PCK_SERVICIOS_PUBLICOS_COM1.FC_NITVALIDAR(UN_COMPANIA => UN_COMPANIA,
                                                 UN_NIT      => UN_NIT) = '899999714' THEN
      MI_RESULTADO := PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                                                      UN_NOMBRE    => 'LIMPIAR DESHABITADOS EN CIERRE',
                                                                                      UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                                                      UN_FECHA_PAR => SYSDATE),' ') = 'SI',
                                            UN_SI        => 1,
                                            UN_NO        => 0);
    END IF;
    RETURN MI_RESULTADO;
  END FC_AUTORIZALIMPIADESHCIERRE;

  -- 4
  FUNCTION FC_PREPARARSIGPERIODO
   /*
      NAME              : FC_PREPARARSIGPERIODO --> EN ACCESS PrepararSigPeriodo
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MIGRADOR     : 03/02/2017
      TIME              : 03:51 PM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : FUNCION PREPARA EL SIGUIENTE PERIODO, ES DECIR HACE EL CIERRE 
                          DEL PERIODO DE FACTURACIÓN.
      PARAMETERS        : UN_COMPANIA => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_CICLO    => CICLO EN EL QUE SE REALIZARÁ EL CIERRE DE PERIODO.
                          UN_USUARIO  => USUARIO QUE REALIZA EL CIERRE.
                          UN_NIT      => NUMERO DE NIT QUE SE VA A EVALUAR PARA EL CIERRE
                                         DEL PERIODO.
      MODIFICATIONS     : 
      @NAME:  prepararSiguientePeriodo
      @METHOD:  GET
    */ 
  (
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_CICLO            IN PCK_SUBTIPOS.TI_CICLO
   ,UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
   ,UN_NIT              IN VARCHAR2 -- VARCHAR2 debido a que no existe un tipo de dato con las mismas características en el paquete PCK_SUBTIPOS.
   ,UN_ANIO             IN  PCK_SUBTIPOS.TI_ANIO
   ,UN_PERIODO          IN  PCK_SUBTIPOS.TI_PERIODO

  )
    RETURN CLOB
  AS
    MI_NUMCONCEPTOS           PCK_SUBTIPOS.TI_ENTERO;
    MI_ANIOSIG                PCK_SUBTIPOS.TI_ANIO;
    MI_PERIODOSIG             PCK_SUBTIPOS.TI_PERIODO;
    MI_FRECUENCIAFAC          VARCHAR2(3 CHAR);
    MI_FINSITU                VARCHAR2(50 CHAR);
    MI_DESCUENTAACUM          VARCHAR2(50 CHAR);
    MI_PERIODODESCUENTAACUM   PCK_SUBTIPOS.TI_DOBLE;
    MI_RECARGO                VARCHAR2(50 CHAR);
    MI_TIPOFACT               VARCHAR2(50 CHAR);
    MI_ETAPA                  VARCHAR2(50 CHAR);
    MI_PESOASEO               PCK_SUBTIPOS.TI_DOBLE;
    MI_CIEPERDIS              VARCHAR2(50 CHAR);
    MI_ANIO                   PCK_SUBTIPOS.TI_ANIO;
    MI_PERIODO                PCK_SUBTIPOS.TI_PERIODO;
    MI_ACTPESOASEO            PCK_SUBTIPOS.TI_LOGICO;
    MI_PESOASEO1              PCK_SUBTIPOS.TI_DOBLE;
    MI_ACTIVADESHABI          PCK_SUBTIPOS.TI_LOGICO;
    MI_ACTIVAHABI             PCK_SUBTIPOS.TI_LOGICO;
    MI_CONMIXTO               VARCHAR2(50 CHAR);
    MI_CONSUMOMANUAL          VARCHAR2(50 CHAR);
    MI_GUARDAHIS              VARCHAR2(50 CHAR);
    MI_NUEVACRITICA           PCK_SUBTIPOS.TI_LOGICO;
    MI_RECONOCEABONOS         VARCHAR2(50 CHAR);
    MI_PLAZOSUSPENSION        VARCHAR2(50 CHAR);
    MI_NUMPERIODOS            PCK_SUBTIPOS.TI_ENTERO;
    MI_AUMENPERSUSP           VARCHAR2(50 CHAR);
    MI_MANEJAMICROSITIO       VARCHAR2(50 CHAR);
    MI_DESCN                  PCK_SUBTIPOS.TI_LOGICO;
    MI_AFECTADOS              PCK_SUBTIPOS.TI_DOBLE;
    MI_MANEJA720              PCK_SUBTIPOS.TI_LOGICO;
    MI_LIMPIAINDDESH          PCK_SUBTIPOS.TI_LOGICO;
    MI_PARACTIVASUSP          PCK_SUBTIPOS.TI_LOGICO;
    MI_STRRESUMEN             CLOB;
    MI_CONT                   PCK_SUBTIPOS.TI_ENTERO; 
    MI_CANT_FINANCIABLES      PCK_SUBTIPOS.TI_ENTERO;  
    MI_MERGEUSING             PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE            PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE            PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_MERGENOEXIS            PCK_SUBTIPOS.TI_MERGENOEXISTE;
    MI_TABLA                  PCK_SUBTIPOS.TI_CAMPOS;
    MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
    MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
    MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR;  
    MI_CAMP                   VARCHAR2(1 CHAR);
    MI_HORAINICIO             DATE;
    MI_ANO_IND_PREPARADO       PCK_SUBTIPOS.TI_ANIO;
    MI_PERIODO_IND_PREPARADO   PCK_SUBTIPOS.TI_PERIODO;
    MI_RS_SALDO_CREDITO         SYS_REFCURSOR;


  BEGIN
    MI_AFECTADOS := 0;
    MI_ETAPA := '';
    MI_STRRESUMEN := '';
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '1. Cargando parametros';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    MI_MANEJAMICROSITIO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                 UN_NOMBRE    => 'MANEJA MICROMEDICION EN SITIO',
                                                 UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                 UN_FECHA_PAR => SYSDATE);
    MI_DESCUENTAACUM := UPPER(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                    UN_NOMBRE    => 'DESCONTAR ACUMULADO SIN LECTURA',
                                                    UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                    UN_FECHA_PAR => SYSDATE));                                             
    MI_PERIODODESCUENTAACUM := TO_NUMBER(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                               UN_NOMBRE    => 'PERIODOS DESCONTAR ACUMULADO SIN LECTURA',
                                                               UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                               UN_FECHA_PAR => SYSDATE));  
    MI_CONMIXTO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                         UN_NOMBRE    => 'MANEJA CONSUMOS MIXTOS',
                                         UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                         UN_FECHA_PAR => SYSDATE);  
    MI_CONSUMOMANUAL := UPPER(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                    UN_NOMBRE    => 'MANEJA CONSUMO MANUAL',
                                                    UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                    UN_FECHA_PAR => SYSDATE));    
    MI_GUARDAHIS := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                              UN_NOMBRE    => 'GUARDA HISTORICOS DE FACTURA',
                                              UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                              UN_FECHA_PAR => SYSDATE),'NO');    
    MI_NUEVACRITICA := PCK_SERVICIOS_PUBLICOS_COM2.FC_AUTORIZACION_DESVIACION (UN_COMPANIA => UN_COMPANIA,
                                                                               UN_NIT      => UN_NIT);
    MI_PLAZOSUSPENSION := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                UN_NOMBRE    => 'MANEJA AMPLIA PLAZO DE SUSPENSION',
                                                UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                UN_FECHA_PAR => SYSDATE);  
    MI_RECONOCEABONOS := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                   UN_NOMBRE    => 'RECONOCER ABONOS SOBRE ACUERDOS DE PAGO',
                                                   UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                   UN_FECHA_PAR => SYSDATE),'NO');    
    MI_NUMPERIODOS := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                UN_NOMBRE    => 'PERIODOS DE ATRASO PARA RECONOCER ABONOS',
                                                UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                UN_FECHA_PAR => SYSDATE),0); 
    MI_AUMENPERSUSP := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                 UN_NOMBRE    => 'INCREMENTAR PERIODO  ATRASO SUSPENDIDOS',
                                                 UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                 UN_FECHA_PAR => SYSDATE),'NO');  
    MI_NUMCONCEPTOS := TO_NUMBER(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                       UN_NOMBRE    => 'NUMERO MAXIMO CONCEPTOS',
                                                       UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                       UN_FECHA_PAR => SYSDATE));  
    MI_FINSITU := UPPER(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                              UN_NOMBRE    => 'FACTURACION EN SITIO',
                                              UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                              UN_FECHA_PAR => SYSDATE));
    MI_FRECUENCIAFAC := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                              UN_NOMBRE    => 'FRECUENCIA PERIODOS DE FACTURACION',
                                              UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                              UN_FECHA_PAR => SYSDATE);
    MI_RECARGO := UPPER(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                              UN_NOMBRE    => 'RECARGO SEGUNDA FECHA EN PERIODO SIGUIENTE',
                                              UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                              UN_FECHA_PAR => SYSDATE));
    MI_TIPOFACT := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                         UN_NOMBRE    => 'FACTURACION DE RIEGO',
                                         UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                         UN_FECHA_PAR => SYSDATE);
    MI_FRECUENCIAFAC := PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => MI_FRECUENCIAFAC = ' ',
                                              UN_SI        => 'M',
                                              UN_NO        => MI_FRECUENCIAFAC);
    MI_CIEPERDIS := UPPER(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                UN_NOMBRE    => 'CIERRE DE PERIODO DISCRIMINADO',
                                                UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                UN_FECHA_PAR => SYSDATE));
    MI_DESCN := PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                                            UN_NOMBRE    => 'CALCULAR DESCUENTO CON PORCENTAJE POR CONCEPTO',
                                                                            UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                                            UN_FECHA_PAR => SYSDATE) = 'SI',
                                      UN_SI        => -1,
                                      UN_NO        => 0);
    MI_ACTPESOASEO := PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                                                  UN_NOMBRE    => 'ACTUALIZAR PESOASEO AL PREPARAR PERIODO',
                                                                                  UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                                                  UN_FECHA_PAR => SYSDATE) = 'SI',
                                            UN_SI        => -1,
                                            UN_NO        => 0);
    MI_PESOASEO1 := TO_NUMBER(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                    UN_NOMBRE    => 'PESO ASEO PARA CALCULO DE ESTADISTICAS',
                                                    UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                    UN_FECHA_PAR => SYSDATE));  
    MI_MANEJA720 := PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                                                UN_NOMBRE    => 'APLICA RESOLUCION CRA 720',
                                                                                UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                                                UN_FECHA_PAR => SYSDATE) = 'SI',
                                          UN_SI        => -1,
                                          UN_NO        => 0);

    MI_LIMPIAINDDESH := PCK_SERVICIOS_PUBLICOS_COM6.FC_AUTORIZALIMPIADESHCIERRE(UN_COMPANIA => UN_COMPANIA,
                                                                                UN_NIT      => UN_NIT);
    MI_PARACTIVASUSP := PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                                                    UN_NOMBRE    => 'ACTIVA USUARIOS SUSPENDIDOS QUE PAGARON',
                                                                                    UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                                                    UN_FECHA_PAR => SYSDATE) = 'SI',
                                          UN_SI        => -1,
                                          UN_NO        => 0);
    MI_STRRESUMEN := 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '2. Cargando información básica';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    EXECUTE IMMEDIATE 'SELECT COUNT(1)
                       FROM   (SELECT DISTINCT ANO
                                     ,PERIODO
                               FROM   SP_USUARIO
                               WHERE  COMPANIA = '''|| UN_COMPANIA ||'''
                                 AND  CICLO = '|| UN_CICLO ||'
                                 AND  INDPREPARADO IN (0))'
    INTO MI_CONT;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_CONT, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '2.1 Nulos de deshabitados';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, 
                             UN_NOMBRE    => 'ACTIVA DESHABITADOS DESDE PROBLEMAS AFORO',
                             UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                             UN_FECHA_PAR => SYSDATE) = 'SI' THEN
      MI_ACTIVADESHABI := 1;
      BEGIN
        BEGIN
          MI_TABLA     :='SP_PROBLEMA';
          MI_CAMPOS    :='ACTIVA_DESHABITADO = 0,
                          MODIFIED_BY    = '''||UN_USUARIO||''',
                          DATE_MODIFIED  = SYSDATE  ';

          MI_CONDICION :='   COMPANIA            = '''||UN_COMPANIA||''' 
                         AND ACTIVA_DESHABITADO IS NULL';
          MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                            UN_ACCION    => 'M',      
                                            UN_CAMPOS    => MI_CAMPOS,      
                                            UN_CONDICION => MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CAMPOS';
          MI_MSGERROR(1).VALOR := 'ACTIVA_DESHABITADO';
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD   => SQLCODE,
            UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_ACTUALIZARPROBLEMA ,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END;
    ELSE
      MI_ACTIVADESHABI := 0;
    END IF;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '2.2 Nulos de habitados';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                             UN_NOMBRE    => 'ACTIVA HABITADOS DESDE PROBLEMAS AFORO',
                             UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                             UN_FECHA_PAR => SYSDATE) = 'SI' THEN
      MI_ACTIVAHABI := 1;
      BEGIN
        BEGIN
          MI_TABLA    :='SP_PROBLEMA';
          MI_CAMPOS   :='ACTIVA_HABITADO = 0,
                         MODIFIED_BY    = '''||UN_USUARIO||''',
                         DATE_MODIFIED  = SYSDATE  ';

          MI_CONDICION:='   COMPANIA    = '''||UN_COMPANIA||
                              ''' AND ACTIVA_HABITADO IS NULL';
          MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                            UN_ACCION    => 'M',      
                                            UN_CAMPOS    => MI_CAMPOS,      
                                            UN_CONDICION => MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CAMPOS';
          MI_MSGERROR(1).VALOR := 'COMPANIA= '''||UN_COMPANIA||
                                  ''' y ACTIVA_HABITADO= NULL';
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD   => SQLCODE,
            UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_ACTUALIZARPROBLEMA ,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END;
    ELSE
      MI_ACTIVAHABI := 0;
    END IF;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '2.3 Nulos e indicadores de desviado segun critica';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    IF MI_NUEVACRITICA <> 0 THEN
      BEGIN
        BEGIN
          MI_TABLA    :='SP_USUARIO';
          MI_CAMPOS   :='DESVIOSIGNIFICATIVO = 0,
                         MODIFIED_BY    = '''||UN_USUARIO||''',
                         DATE_MODIFIED  = SYSDATE  ';

          MI_CONDICION:=' COMPANIA    = '''|| UN_COMPANIA ||
                              ''' AND CICLO = '|| UN_CICLO ||
                              ' AND  DESVIOSIGNIFICATIVO IS NULL';
          MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                UN_ACCION    => 'M',      
                                                UN_CAMPOS    => MI_CAMPOS,      
                                                UN_CONDICION => MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CAMPOS';
          MI_MSGERROR(1).VALOR := 'COMPANIA= '''|| UN_COMPANIA ||
                                  ''', CICLO= '|| UN_CICLO ||
                                  ' y DESVIOSIGNIFICATIVO= NULL';
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD   => SQLCODE,
            UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_ACTUALIZARUSUARIO,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END;
      BEGIN
        BEGIN
          MI_TABLA    :='SP_USUARIO';
          MI_CAMPOS   :='DESVIACIONAFORO = 0,
                         MODIFIED_BY    = '''||UN_USUARIO||''',
                         DATE_MODIFIED  = SYSDATE  ';

          MI_CONDICION:=' COMPANIA    = '''|| UN_COMPANIA ||
                              ''' AND CICLO = '|| UN_CICLO ||
                              ' AND  DESVIACIONAFORO IS NULL';
          MI_AFECTADOS := MI_AFECTADOS + PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                           UN_ACCION    => 'M',      
                                                           UN_CAMPOS    => MI_CAMPOS,      
                                                           UN_CONDICION => MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CAMPOS';
          MI_MSGERROR(1).VALOR := 'COMPANIA= '''|| UN_COMPANIA ||
                                  ''', CICLO= '|| UN_CICLO ||
                                  ' y DESVIACIONAFORO= NULL';
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD   => SQLCODE,
            UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_ACTUALIZARUSUARIO,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END;
    END IF;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '2.4 Datos del ciclo y periodo';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    BEGIN
      BEGIN
        MI_TABLA    :='SP_CICLO';
        MI_CAMPOS   :='INDPREPARADO             = -1
                            ,INDBLOQUEOMANUAL   = -1
                            ,PREFACTURANDO      = -1
                            ,FECHA_CIERREPREFAC = NULL
                            ,MODIFIED_BY        = '''||UN_USUARIO||'''
                            ,DATE_MODIFIED      = SYSDATE  ';

        MI_CONDICION:=' COMPANIA    = '''|| UN_COMPANIA ||
                            ''' AND NUMERO = '|| UN_CICLO;
        MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                          UN_ACCION    => 'M',      
                                          UN_CAMPOS    => MI_CAMPOS,      
                                          UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CAMPOS';
        MI_MSGERROR(1).VALOR := 'COMPANIA= '''|| UN_COMPANIA ||
                                ''' y NUMERO= '|| UN_CICLO;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD   => SQLCODE,
          UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_ACTUALIZARCICLO,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
    -- SE TOMAN LOS DATOS DE SP_USUARIO DADO EL CASO QUE EL CICLO NO ESTE EN EL MISMO PERIODO QUE EL USUARIO
    BEGIN 
      BEGIN 

             SELECT DISTINCT ANO,
                    PERIODO
              INTO  MI_ANO_IND_PREPARADO,
                    MI_PERIODO_IND_PREPARADO
              FROM  SP_USUARIO
             WHERE  COMPANIA = UN_COMPANIA 
               AND  CICLO    = UN_CICLO 
               AND  ANO      = UN_ANIO
               AND PERIODO   = UN_PERIODO
               AND  INDPREPARADO IN (0);


            EXCEPTION WHEN NO_DATA_FOUND THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;

        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                   UN_ERROR_COD => PCK_ERRORES.ERR_USER_IND_PREPARA,
                                   UN_REEMPLAZOS => MI_MSGERROR);
      END;

       BEGIN
        BEGIN
          SELECT DISTINCT SP_CICLO.ANO
                ,SP_CICLO.PERIODO
          INTO   MI_ANIOSIG
                ,MI_PERIODOSIG
          FROM   SP_CICLO 
            INNER JOIN SP_USUARIO 
               ON      SP_CICLO.COMPANIA = SP_USUARIO.COMPANIA 
              AND      SP_CICLO.ANO      = SP_USUARIO.ANO 
              AND      SP_CICLO.PERIODO  = SP_USUARIO.PERIODO
          WHERE  SP_CICLO.COMPANIA = UN_COMPANIA 
            AND  SP_CICLO.NUMERO   = UN_CICLO 
            AND  SP_CICLO.PERIODO  = MI_PERIODO_IND_PREPARADO;
          EXCEPTION WHEN NO_DATA_FOUND THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD   => SQLCODE,
            UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_ALERTACICLO,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END;
      MI_ANIO := MI_ANIOSIG;
      MI_PERIODO := MI_PERIODOSIG;


    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '2.5 Informe de pesoAseo';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                             UN_NOMBRE    => 'ACTUALIZAR PESOASEO AL PREPARAR PERIODO',
                             UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                             UN_FECHA_PAR => SYSDATE) = 'SI' THEN
      MI_PESOASEO := TO_NUMBER(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                           UN_NOMBRE    => 'PESO ASEO PARA CALCULO DE ESTADISTICAS',
                           UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                           UN_FECHA_PAR => SYSDATE));
      BEGIN
        BEGIN
          MI_TABLA := 'SP_TMP_USUARIOS_PESOASEO';
          MI_CAMPOS := 'COMPANIA
                       ,CICLO
                       ,CODIGORUTA
                       ,ANO
                       ,PERIODO
                       ,PESOASEO
                       ,USO
                       ,ESTRATO
                       ,DATE_CREATED
                       ,CREATED_BY';
          MI_VALORES := 'SELECT COMPANIA
                               ,CICLO
                               ,CODIGORUTA
                               ,ANO
                               ,PERIODO
                               ,PESOASEO
                               ,USO
                               ,ESTRATO
                               ,SYSDATE
                               ,'''||UN_USUARIO||'''
                         FROM   SP_USUARIO
                         WHERE  COMPANIA = '''|| UN_COMPANIA || '''
                           AND  CICLO    = '|| UN_CICLO ||'
                           AND  PESOASEO NOT IN ('|| MI_PESOASEO ||',0) 
                           AND  ASEO NOT IN (0)';

          MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA, 
                                            UN_ACCION  => 'IS', 
                                            UN_CAMPOS  => MI_CAMPOS, 
                                            UN_VALORES => MI_VALORES);    
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN               
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_INSERTSPPESOASEO
          ); 
      END;
    END IF;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '2.6 nuevo periodo';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    BEGIN
      SELECT 'X'
      INTO   MI_CAMP
      FROM   SP_PERIODO
      WHERE  COMPANIA = UN_COMPANIA
        AND  ANO      = MI_ANIOSIG
        AND  MES      = MI_PERIODOSIG;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        BEGIN
          BEGIN
            MI_TABLA := 'SP_PERIODO';
            MI_CAMPOS := 'COMPANIA
                         ,ANO
                         ,MES
                         ,ESTADO
                         ,DATE_CREATED
                         ,CREATED_BY';
            MI_VALORES := '''' || UN_COMPANIA  ||'''
                          ,'|| MI_ANIOSIG||'
                          ,'''|| MI_PERIODOSIG||'''
                          ,-1
                          ,SYSDATE
                          ,'''||UN_USUARIO||'''';

            MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA, 
                                              UN_ACCION  => 'I', 
                                              UN_CAMPOS  => MI_CAMPOS, 
                                              UN_VALORES => MI_VALORES);    
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN               
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_INSERTSPPERIODO
            ); 
        END;
    END;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '2.7 Historicos de pago';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    BEGIN
      BEGIN
        MI_TABLA := 'SP_USUARIO_HISTORICOS';
        MI_CAMPOS := 'COMPANIA
                     ,CICLO
                     ,CODIGORUTA
                     ,DATE_CREATED
                     ,CREATED_BY';

        MI_VALORES := 'SELECT SP_USUARIO.COMPANIA
                             ,SP_USUARIO.CICLO
                             ,SP_USUARIO.CODIGORUTA
                             ,SYSDATE
                             ,'''||UN_USUARIO||'''
                       FROM   SP_USUARIO
                         LEFT JOIN SP_USUARIO_HISTORICOS
                           ON      SP_USUARIO.COMPANIA   = SP_USUARIO_HISTORICOS.COMPANIA
                          AND      SP_USUARIO.CICLO      = SP_USUARIO_HISTORICOS.CICLO
                          AND      SP_USUARIO.CODIGORUTA = SP_USUARIO_HISTORICOS.CODIGORUTA
                       WHERE  SP_USUARIO.COMPANIA            = '''|| UN_COMPANIA ||'''
                         AND  SP_USUARIO.CICLO               = '  || UN_CICLO    ||'
                         AND  SP_USUARIO_HISTORICOS.COMPANIA IS NULL';
        MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA, 
                                          UN_ACCION  => 'IS', 
                                          UN_CAMPOS  => MI_CAMPOS, 
                                          UN_VALORES => MI_VALORES);    
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN               
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_INSERTSPUSUHISTORI
        ); 
    END;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '2.8 Actualiza Nulos de periodos de entrada';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    BEGIN
      BEGIN
        MI_TABLA    :='SP_USUARIO';
        MI_CAMPOS   :='ANOENTRADANUEVOUSUARIO = 0
                      ,MODIFIED_BY    = '''||UN_USUARIO||'''
                      ,DATE_MODIFIED  = SYSDATE';

        MI_CONDICION:=' ANOENTRADANUEVOUSUARIO IS NULL';
        MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                          UN_ACCION    => 'M',      
                                          UN_CAMPOS    => MI_CAMPOS,      
                                          UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CAMPOS';
        MI_MSGERROR(1).VALOR := 'ANOENTRADANUEVOUSUARIO= NULL';
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD   => SQLCODE,
          UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_ACTUALIZARUSUARIO,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
    BEGIN
      BEGIN
        MI_TABLA    :='SP_USUARIO';
        MI_CAMPOS   :='PERENTRADANUEVOUSUARIO = '' ''
                       ,MODIFIED_BY    = '''||UN_USUARIO||'''
                       ,DATE_MODIFIED  = SYSDATE ';

        MI_CONDICION := ' PERENTRADANUEVOUSUARIO IS NULL';
        MI_AFECTADOS := MI_AFECTADOS + PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                         UN_ACCION    => 'M',      
                                                         UN_CAMPOS    => MI_CAMPOS,      
                                                         UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CAMPOS';
        MI_MSGERROR(1).VALOR := 'PERENTRADANUEVOUSUARIO= NULL';
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD   => SQLCODE,
          UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_ACTUALIZARUSUARIO,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '3 Incremento de periodo';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    BEGIN
      BEGIN
       SELECT DISTINCT 'X'
       INTO   MI_CAMP
       FROM   SP_USUARIO
         INNER JOIN SP_USUARIO_HISTORICOS
            ON      SP_USUARIO.COMPANIA   = SP_USUARIO_HISTORICOS.COMPANIA
           AND      SP_USUARIO.CICLO      = SP_USUARIO_HISTORICOS.CICLO
           AND      SP_USUARIO.CODIGORUTA = SP_USUARIO_HISTORICOS.CODIGORUTA
       WHERE  SP_USUARIO.COMPANIA     = UN_COMPANIA
         AND  SP_USUARIO.CICLO        = UN_CICLO
         AND  SP_USUARIO.INDPREPARADO = 0;
       EXCEPTION WHEN NO_DATA_FOUND THEN
         RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD   => SQLCODE,
          UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_PREPARARPERIODO
        );
    END;

    MI_PERIODOSIG := PCK_SERVICIOS_PUBLICOS.FC_ANO_PERIODO_SIGUIENTE(UN_COMPANIA     => UN_COMPANIA,
                                                                     UN_ANO          => UN_ANIO,
                                                                     UN_PERIODO      => UN_PERIODO,
                                                                     UN_TIPO_RETORNO => '1',
                                                                     UN_FRECUENCIA   => MI_FRECUENCIAFAC);

    MI_ANIOSIG := PCK_SERVICIOS_PUBLICOS.FC_ANO_PERIODO_SIGUIENTE(UN_COMPANIA   => UN_COMPANIA,
                                                                  UN_ANO        => UN_ANIO,
                                                                  UN_PERIODO    => UN_PERIODO,
                                                                  UN_TIPO_RETORNO => '0',
                                                                  UN_FRECUENCIA => MI_FRECUENCIAFAC);
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(0, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '3.1 Validando Tarifas';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    BEGIN
      BEGIN
        SELECT DISTINCT 'X'
        INTO   MI_CAMP
        FROM  SP_TARIFAS 
      WHERE SP_TARIFAS.COMPANIA = UN_COMPANIA 
        AND SP_TARIFAS.ANO      = MI_ANIOSIG
        AND SP_TARIFAS.PERIODO  = MI_PERIODOSIG;

      EXCEPTION WHEN NO_DATA_FOUND THEN
       RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD   => SQLCODE,
          UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_INCREMENTARTARIFAS
        );
    END;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(0, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    -- PROCESO 0 -> GUARDA HISTORICOS DE FACTURA (PARA IMPRESION DE FACTURAS DE PERIODOS ANTERIORES)
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '4. Historicos de factura';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    IF MI_GUARDAHIS = 'SI' THEN
      PCK_SERVICIOS_PUBLICOS_COM6.PR_GUARDARHISTORICOSFACTURA(UN_COMPANIA => UN_COMPANIA,
                                                              UN_CICLO    => UN_CICLO,
                                                              UN_ANIO     => MI_ANIO,
                                                              UN_PERIODO  => MI_PERIODO,
                                                              UN_USUARIO  => UN_USUARIO);
    END IF;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(0, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -- PROCESO 1 -> CORRER LA INFORMACION DE PERIODOS PARA USUARIOS QUE PAGARON EN EL PERIODO
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '5 Historicos de pago';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    BEGIN
      BEGIN
        MI_TABLA := 'SP_USUARIO_HISTORICOS';
        MI_MERGEUSING  := 'SELECT SP_USUARIO_HISTORICOS.COMPANIA
                                 ,SP_USUARIO_HISTORICOS.CICLO
                                 ,SP_USUARIO_HISTORICOS.CODIGORUTA
                                 ,SP_USUARIO.FECHAPAGOPERPROCESO
                                 ,SP_USUARIO.TOTFACTURAPERACTUAL
                                 ,SP_USUARIO.BANCOPERPROCESO
                                 ,SP_USUARIO.CONSUMO
                                 ,SP_USUARIO.ANO
                                 ,SP_USUARIO.PERIODO
                           FROM   SP_USUARIO_HISTORICOS  
                             INNER JOIN SP_USUARIO 
                                ON      SP_USUARIO_HISTORICOS.COMPANIA   = SP_USUARIO.COMPANIA 
                               AND      SP_USUARIO_HISTORICOS.CICLO      = SP_USUARIO.CICLO
                               AND      SP_USUARIO_HISTORICOS.CODIGORUTA = SP_USUARIO.CODIGORUTA
                           WHERE  SP_USUARIO.COMPANIA = '''|| UN_COMPANIA ||'''
                             AND  SP_USUARIO.CICLO    =   '|| UN_CICLO ||'
                             AND  SP_USUARIO.ANO      =   '|| MI_ANIO ||'
                             AND  SP_USUARIO.PERIODO  = '''|| MI_PERIODO ||'''
                             AND  SP_USUARIO.FECHAPAGOPERPROCESO IS NOT NULL';
        MI_MERGEENLACE := '    TABLA.COMPANIA   = VISTA.COMPANIA 
                           AND TABLA.CICLO      = VISTA.CICLO
                           AND TABLA.CODIGORUTA = VISTA.CODIGORUTA';
        MI_MERGEEXISTE := 'UPDATE 
                             SET TABLA.FECHAPAGO6 = TABLA.FECHAPAGO5
                                ,TABLA.FECHAPAGO5 = TABLA.FECHAPAGO4
                                ,TABLA.FECHAPAGO4 = TABLA.FECHAPAGO3
                                ,TABLA.FECHAPAGO3 = TABLA.FECHAPAGO2
                                ,TABLA.FECHAPAGO2 = TABLA.FECHAPAGO1
                                ,TABLA.FECHAPAGO1 = VISTA.FECHAPAGOPERPROCESO
                                ,TABLA.VALORPAGO6 = TABLA.VALORPAGO5
                                ,TABLA.VALORPAGO5 = TABLA.VALORPAGO4
                                ,TABLA.VALORPAGO4 = TABLA.VALORPAGO3
                                ,TABLA.VALORPAGO3 = TABLA.VALORPAGO2
                                ,TABLA.VALORPAGO2 = TABLA.VALORPAGO1
                                ,TABLA.VALORPAGO1 = VISTA.TOTFACTURAPERACTUAL
                                ,TABLA.BANCOPAGO6 = TABLA.BANCOPAGO5
                                ,TABLA.BANCOPAGO5 = TABLA.BANCOPAGO4
                                ,TABLA.BANCOPAGO4 = TABLA.BANCOPAGO3
                                ,TABLA.BANCOPAGO3 = TABLA.BANCOPAGO2
                                ,TABLA.BANCOPAGO2 = TABLA.BANCOPAGO1
                                ,TABLA.BANCOPAGO1 = VISTA.BANCOPERPROCESO
                                ,TABLA.CONSUMO6   = TABLA.CONSUMO5
                                ,TABLA.CONSUMO5   = TABLA.CONSUMO4
                                ,TABLA.CONSUMO4   = TABLA.CONSUMO3
                                ,TABLA.CONSUMO3   = TABLA.CONSUMO2
                                ,TABLA.CONSUMO2   = TABLA.CONSUMO1
                                ,TABLA.CONSUMO1   = NVL(VISTA.CONSUMO, 0)
                                ,TABLA.ANO6       = TABLA.ANO5
                                ,TABLA.ANO5       = TABLA.ANO4
                                ,TABLA.ANO4       = TABLA.ANO3
                                ,TABLA.ANO3       = TABLA.ANO2
                                ,TABLA.ANO2       = TABLA.ANO1
                                ,TABLA.ANO1       = VISTA.ANO
                                ,TABLA.PERIODO6   = TABLA.PERIODO5
                                ,TABLA.PERIODO5   = TABLA.PERIODO4
                                ,TABLA.PERIODO4   = TABLA.PERIODO3
                                ,TABLA.PERIODO3   = TABLA.PERIODO2
                                ,TABLA.PERIODO2   = TABLA.PERIODO1
                                ,TABLA.PERIODO1   = VISTA.PERIODO
                                ,TABLA.MODIFIED_BY    = '''||UN_USUARIO||'''
                                ,TABLA.DATE_MODIFIED  = SYSDATE';

        MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA, 
                                          UN_ACCION      => 'MM', 
                                          UN_MERGEUSING  => MI_MERGEUSING, 
                                          UN_MERGEENLACE => MI_MERGEENLACE,
                                          UN_MERGEEXISTE => MI_MERGEEXISTE);  

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_MERGEUSUHISTORICOS
        );
    END; 
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -- PROCESO 2 --> ACTUALIZAR DATOS DE MEDIDOR
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '6. Datos de Medidores';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    BEGIN
      BEGIN
        MI_TABLA := 'SP_MEDIDOR';
        MI_MERGEUSING  := 'SELECT SP_MEDIDOR.COMPANIA
                                 ,SP_MEDIDOR.CONSECUTIVO
                                 ,SP_USUARIO.LECTURA
                                 ,SP_USUARIO.LECTURAAFORO
                           FROM   SP_MEDIDOR  
                             INNER JOIN SP_USUARIO 
                                ON      SP_MEDIDOR.COMPANIA    = SP_USUARIO.COMPANIA 
                               AND      SP_MEDIDOR.CONSECUTIVO = SP_USUARIO.MEDIDOR
                           WHERE  SP_MEDIDOR.COMPANIA   = '''||UN_COMPANIA||'''
                             AND  SP_MEDIDOR.CICLO      = '  ||UN_CICLO||'
                             AND  SP_MEDIDOR.ANO        = '  ||MI_ANIOSIG||'
                             AND  SP_MEDIDOR.PERIODO    = '''||MI_PERIODOSIG||'''
                             AND  SP_MEDIDOR.ELEMCAMBIO IN(''M'',''MEDIDORES'')';
        MI_MERGEENLACE := '    TABLA.COMPANIA    = VISTA.COMPANIA 
                           AND TABLA.CONSECUTIVO = VISTA.CONSECUTIVO';
        MI_MERGEEXISTE := 'UPDATE 
                             SET TABLA.LECTMEDANT     = VISTA.LECTURA
                                ,TABLA.LECTACTUAL     = VISTA.LECTURAAFORO
                                ,TABLA.MODIFIED_BY    = '''||UN_USUARIO||'''
                                ,TABLA.DATE_MODIFIED  = SYSDATE';

        MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA, 
                                          UN_ACCION      => 'MM', 
                                          UN_MERGEUSING  => MI_MERGEUSING, 
                                          UN_MERGEENLACE => MI_MERGEENLACE,
                                          UN_MERGEEXISTE => MI_MERGEEXISTE);    
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CAMPOS';
        MI_MSGERROR(1).VALOR := 'LECTMEDANT, LECTACTUAL';
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_MERGEMEDIDORES,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
    BEGIN
      BEGIN
        MI_TABLA := 'SP_USUARIO';
        MI_MERGEUSING  := 'SELECT SP_USUARIO.COMPANIA
                                 ,SP_USUARIO.CICLO
                                 ,SP_USUARIO.CODIGORUTA
                                 ,SP_MEDIDOR.CONSECUTIVO
                                 ,SP_MEDIDOR.DIGITOS
                                 ,SP_MEDIDOR.FECHAINST  
                                 ,SP_MEDIDOR.ESTADO
                                 ,SP_MEDIDOR.LECTURAFINALVIEJO
                                 ,SP_MEDIDOR.ANO
                                 ,SP_MEDIDOR.PERIODO
                           FROM   SP_USUARIO  
                             INNER JOIN SP_MEDIDOR
                                ON      SP_USUARIO.COMPANIA   = SP_MEDIDOR.COMPANIA 
                               AND      SP_USUARIO.CICLO      = SP_MEDIDOR.CICLO
                               AND      SP_USUARIO.CODIGORUTA = SP_MEDIDOR.CODIGORUTA
                           WHERE  SP_MEDIDOR.COMPANIA   = '''||UN_COMPANIA||'''
                             AND  SP_MEDIDOR.CICLO      = '  ||UN_CICLO||'
                             AND  SP_MEDIDOR.ANO        = '  ||MI_ANIOSIG||'
                             AND  SP_MEDIDOR.PERIODO    = '''||MI_PERIODOSIG||'''
                             AND  SP_MEDIDOR.ELEMCAMBIO IN(''M'',''MEDIDORES'')';
        MI_MERGEENLACE := '    TABLA.COMPANIA   = VISTA.COMPANIA 
                           AND TABLA.CICLO      = VISTA.CICLO
                           AND TABLA.CODIGORUTA = VISTA.CODIGORUTA';
        MI_MERGEEXISTE := 'UPDATE 
                             SET TABLA.MEDIDOR          = VISTA.CONSECUTIVO
                                ,TABLA.NUMERODIGITOS    = CASE 
                                                            WHEN NVL(VISTA.DIGITOS,0) = 0
                                                            THEN 4
                                                            ELSE VISTA.DIGITOS
                                                          END
                                ,TABLA.FECHAINSTALACION = VISTA.FECHAINST
                                ,TABLA.ESTADOMEDIDOR    = NVL(VISTA.ESTADO, ''B'')
                                ,TABLA.INDMEDCAMBIADO   = -1
                                ,TABLA.MODIFIED_BY      = '''||UN_USUARIO||'''
                                ,TABLA.DATE_MODIFIED    = SYSDATE';

        IF MI_CONMIXTO = 'SI' THEN
          MI_MERGEEXISTE := MI_MERGEEXISTE || 
                            ',TABLA.CONSUMOMEDIDORANT = CASE 
                                                         WHEN (NVL(VISTA.LECTURAFINALVIEJO,0) - NVL(TABLA.LECTURA,0)) > 0
                                                         THEN (NVL(VISTA.LECTURAFINALVIEJO,0) - NVL(TABLA.LECTURA,0))
                                                         ELSE TABLA.CONSUMOMEDIDORANT
                                                       END
                            ,TABLA.ANOCAMBIOMEDIDOR = CASE 
                                                        WHEN(NVL(VISTA.LECTURAFINALVIEJO,0) - NVL(TABLA.LECTURA,0)) > 0
                                                        THEN VISTA.ANO
                                                        ELSE TABLA.ANOCAMBIOMEDIDOR
                                                        END
                            ,TABLA.PERIODOCAMBIOMEDIDOR = CASE 
                                                            WHEN (NVL(VISTA.LECTURAFINALVIEJO,0) - NVL(TABLA.LECTURA,0)) > 0
                                                            THEN VISTA.PERIODO
                                                            ELSE TABLA.PERIODOCAMBIOMEDIDOR
                                                          END';
        ELSE
          MI_MERGEEXISTE := MI_MERGEEXISTE || 
                            ',TABLA.CONSUMOMEDIDORANT = CASE
                                                         WHEN (NVL(VISTA.LECTURAFINALVIEJO,0) - NVL(TABLA.LECTURA,0)) <= 0
                                                         THEN 0
                                                         ELSE TABLA.CONSUMOMEDIDORANT
                                                       END';
        END IF;
        MI_AFECTADOS := MI_AFECTADOS + PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA, 
                                                         UN_ACCION      => 'MM', 
                                                         UN_MERGEUSING  => MI_MERGEUSING, 
                                                         UN_MERGEENLACE => MI_MERGEENLACE,
                                                         UN_MERGEEXISTE => MI_MERGEEXISTE);    
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_MERGEUSUARIO
        );
    END;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '6.1 Cambios de medidor';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    BEGIN
      BEGIN
        MI_TABLA := 'SP_USUARIO';
        MI_MERGEUSING  := 'SELECT SP_USUARIO.COMPANIA
                                 ,SP_USUARIO.CICLO
                                 ,SP_USUARIO.CODIGORUTA
                           FROM   SP_USUARIO  
                             INNER JOIN SP_MEDIDOR
                                ON      SP_USUARIO.COMPANIA   = SP_MEDIDOR.COMPANIA 
                               AND      SP_USUARIO.CICLO      = SP_MEDIDOR.CICLO
                               AND      SP_USUARIO.CODIGORUTA = SP_MEDIDOR.CODIGORUTA
                           WHERE  SP_MEDIDOR.COMPANIA   = '''||UN_COMPANIA||'''
                             AND  SP_MEDIDOR.CICLO      = '  ||UN_CICLO||'
                             AND  SP_MEDIDOR.ANO        = '  ||MI_ANIOSIG||'
                             AND  SP_MEDIDOR.PERIODO    = '''||MI_PERIODOSIG||'''
                             AND  SP_MEDIDOR.ELEMCAMBIO IN(''M'',''MEDIDORES'')';
        MI_MERGEENLACE := '    TABLA.COMPANIA   = VISTA.COMPANIA 
                           AND TABLA.CICLO      = VISTA.CICLO
                           AND TABLA.CODIGORUTA = VISTA.CODIGORUTA';
        MI_MERGEEXISTE := 'UPDATE 
                             SET TABLA.INDMEDCAMBIADO    = 0
                                ,TABLA.CONSUMOMEDIDORANT = 0
                                ,TABLA.MODIFIED_BY    = '''||UN_USUARIO||'''
                                ,TABLA.DATE_MODIFIED  = SYSDATE';

        MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA, 
                                          UN_ACCION      => 'MM', 
                                          UN_MERGEUSING  => MI_MERGEUSING, 
                                          UN_MERGEENLACE => MI_MERGEENLACE,
                                          UN_MERGEEXISTE => MI_MERGEEXISTE);    
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CAMPOS';
        MI_MSGERROR(1).VALOR := 'INDMEDCAMBIADO y CONSUMOMEDIDORANT';
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTUALIZARUSUARIO,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -- PROCESO 3 -> ACTUALIZACION DE LECTURAS
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '7. Lecturas';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    IF MI_FINSITU = 'SI' THEN
      IF MI_MANEJAMICROSITIO = 'SI' THEN
        BEGIN
          BEGIN
            MI_TABLA := 'SP_USUARIO';
            MI_MERGEUSING  := 'SELECT SP_USUARIO.COMPANIA
                                     ,SP_USUARIO.CICLO
                                     ,SP_USUARIO.CODIGORUTA
                                     ,SP_MICROMEDICION_CAMBIO.LECTURAINICIAL
                               FROM   SP_USUARIO  
                                 INNER JOIN SP_MICROMEDICION_CAMBIO
                                    ON      SP_USUARIO.COMPANIA   = SP_MICROMEDICION_CAMBIO.COMPANIA 
                                   AND      SP_USUARIO.CICLO      = SP_MICROMEDICION_CAMBIO.CICLO
                                   AND      SP_USUARIO.CODIGORUTA = SP_MICROMEDICION_CAMBIO.CODIGORUTA
                               WHERE  SP_MICROMEDICION_CAMBIO.COMPANIA   = '''||UN_COMPANIA||'''
                                 AND  SP_MICROMEDICION_CAMBIO.CICLO      = '  ||UN_CICLO||'
                                 AND  SP_MICROMEDICION_CAMBIO.ANO        = '  ||MI_ANIOSIG||'
                                 AND  SP_MICROMEDICION_CAMBIO.PERIODO    = '''||MI_PERIODOSIG||'''
                                 AND  SP_MICROMEDICION_CAMBIO.TIPODECAMBIO IN(''PP'',''TP'')
                                 AND  SP_MICROMEDICION_CAMBIO.CONTROLLECTURA <> 0';
            MI_MERGEENLACE := '    TABLA.COMPANIA   = VISTA.COMPANIA 
                               AND TABLA.CICLO      = VISTA.CICLO
                               AND TABLA.CODIGORUTA = VISTA.CODIGORUTA';
            MI_MERGEEXISTE := 'UPDATE 
                                 SET TABLA.LECTURA1 = VISTA.LECTURAINICIAL,
                                     ,TABLA.MODIFIED_BY    = '''||UN_USUARIO||'''
                                     ,TABLA.DATE_MODIFIED  = SYSDATE';

            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA, 
                                                  UN_ACCION      => 'MM', 
                                                  UN_MERGEUSING  => MI_MERGEUSING, 
                                                  UN_MERGEENLACE => MI_MERGEENLACE,
                                                  UN_MERGEEXISTE => MI_MERGEEXISTE);    
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
            MI_MSGERROR(1).CLAVE := 'CAMPOS';
            MI_MSGERROR(1).VALOR := 'LECTURA1';
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTUALIZARUSUARIO,
              UN_REEMPLAZOS => MI_MSGERROR
            );
        END; 
      END IF;
      BEGIN
        BEGIN
          MI_TABLA     := 'SP_USUARIO';
          MI_CAMPOS    := 'SP_USUARIO.FECHALECANTERIOR = CASE
                                                           WHEN SP_USUARIO.LECTURA <> 0
                                                           THEN SP_USUARIO.FECHALECACTUAL
                                                           ELSE SP_USUARIO.FECHALECANTERIOR
                                                         END
                          ,SP_USUARIO.LECTURA6    = SP_USUARIO.LECTURA5
                          ,SP_USUARIO.LECTURA5    = SP_USUARIO.LECTURA4
                          ,SP_USUARIO.LECTURA4    = SP_USUARIO.LECTURA3
                          ,SP_USUARIO.LECTURA3    = SP_USUARIO.LECTURA2
                          ,SP_USUARIO.LECTURA2    = SP_USUARIO.LECTURA1
                          ,SP_USUARIO.TIPOLECTURA = NULL
                          ,SP_USUARIO.FIMM        = NULL
                          ,MODIFIED_BY    = '''||UN_USUARIO||'''
                          ,DATE_MODIFIED  = SYSDATE';

          MI_CONDICION := '    SP_USUARIO.COMPANIA = '''|| UN_COMPANIA ||'''
                           AND SP_USUARIO.CICLO    = '  || UN_CICLO;
          MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                        UN_ACCION    => 'M',
                                        UN_CAMPOS    => MI_CAMPOS,
                                        UN_CONDICION => MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CAMPOS';
          MI_MSGERROR(1).VALOR := 'COMPANIA= '''|| UN_COMPANIA ||
                                 ''' y CICLO= '|| UN_CICLO;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTUALIZARUSUARIO,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END; 
      MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
      MI_AFECTADOS := 0;
      -----------------------------------------------------------------------------------------------------
      MI_ETAPA := '7.1 Sin lectura';
      MI_HORAINICIO := SYSDATE;
      -----------------------------------------------------------------------------------------------------
      BEGIN
        BEGIN
          MI_TABLA     := 'SP_USUARIO';
          MI_CAMPOS    := ' SP_USUARIO.LECTURA1 = SP_USUARIO.LECTURA
                            ,MODIFIED_BY    = '''||UN_USUARIO||'''
                            ,DATE_MODIFIED  = SYSDATE';
          MI_CONDICION := '    SP_USUARIO.COMPANIA = '''|| UN_COMPANIA ||'''
                           AND SP_USUARIO.CICLO    = '  || UN_CICLO || '
                           AND SP_USUARIO.LECTURA  NOT IN (0)';
          MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                        UN_ACCION    => 'M',
                                        UN_CAMPOS    => MI_CAMPOS,
                                        UN_CONDICION => MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CAMPOS';
          MI_MSGERROR(1).VALOR := 'COMPANIA= '''|| UN_COMPANIA ||
                                 ''', CICLO = '|| UN_CICLO ||
                                 ' y LECTURA diferente de 0';
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTUALIZARUSUARIO,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END;
      MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    ELSE
      MI_AFECTADOS := 0;
      -----------------------------------------------------------------------------------------------------
      MI_ETAPA := '7 Lecturas';
      MI_HORAINICIO := SYSDATE;
      -----------------------------------------------------------------------------------------------------
      BEGIN
        BEGIN
          MI_TABLA     := 'SP_USUARIO';
          MI_CAMPOS    := 'SP_USUARIO.FECHALECANTERIOR = CASE
                                                           WHEN SP_USUARIO.LECTURAAFORO <> 0
                                                           THEN SP_USUARIO.FECHALECACTUAL
                                                           ELSE SP_USUARIO.FECHALECANTERIOR
                                                         END
                          ,SP_USUARIO.LECTURA6         = SP_USUARIO.LECTURA5
                          ,SP_USUARIO.LECTURA5         = SP_USUARIO.LECTURA4
                          ,SP_USUARIO.LECTURA4         = SP_USUARIO.LECTURA3
                          ,SP_USUARIO.LECTURA3         = SP_USUARIO.LECTURA2
                          ,SP_USUARIO.LECTURA2         = SP_USUARIO.LECTURA1
                          ,SP_USUARIO.TIPOLECTURA      = NULL
                          ,SP_USUARIO.FIMM             = NULL
                          ,MODIFIED_BY    = '''||UN_USUARIO||'''
                          ,DATE_MODIFIED  = SYSDATE';

          IF MI_LIMPIAINDDESH <> 0 THEN
            MI_CAMPOS := MI_CAMPOS ||
                         ',SP_USUARIO.INDDESHABITADO = 0';
          END IF;
          MI_CONDICION := '    SP_USUARIO.COMPANIA = '''|| UN_COMPANIA ||'''
                           AND SP_USUARIO.CICLO    = '  || UN_CICLO;
          MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                        UN_ACCION    => 'M',
                                        UN_CAMPOS    => MI_CAMPOS,
                                        UN_CONDICION => MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CAMPOS';
          MI_MSGERROR(1).VALOR := 'COMPANIA= '''|| UN_COMPANIA ||
                                 ''' y CICLO= '|| UN_CICLO;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTUALIZARUSUARIO,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END;
      MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
      MI_AFECTADOS := 0;
      -----------------------------------------------------------------------------------------------------
      MI_ETAPA := '7.1 Sin lectura';
      MI_HORAINICIO := SYSDATE;
      -----------------------------------------------------------------------------------------------------
      BEGIN
        BEGIN
          MI_TABLA     := 'SP_USUARIO';
          MI_CAMPOS    := 'SP_USUARIO.LECTURA1 = SP_USUARIO.LECTURA
                          ,MODIFIED_BY         = '''||UN_USUARIO||'''
                          ,DATE_MODIFIED       = SYSDATE';

          MI_CONDICION := '    SP_USUARIO.COMPANIA = '''|| UN_COMPANIA ||'''
                           AND SP_USUARIO.CICLO    = '  || UN_CICLO || '
                           AND SP_USUARIO.LECTURA  NOT IN (0)';

          MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                        UN_ACCION    => 'M',
                                        UN_CAMPOS    => MI_CAMPOS,
                                        UN_CONDICION => MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CAMPOS';
          MI_MSGERROR(1).VALOR := 'COMPANIA= '''|| UN_COMPANIA ||
                                 ''', CICLO= '|| UN_CICLO ||
                                 ' y LECTURA deferente de 0';
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTUALIZARUSUARIO,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END;
      MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    END IF;
    -- PROCESO 4 -> ACTUALIZACION DE CONSUMOS
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '8. Consumos y PesoAseo';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    BEGIN
      BEGIN
        MI_TABLA     := 'SP_USUARIO';
        MI_CAMPOS    := 'SP_USUARIO.FECHALECACTUAL = SP_USUARIO.FECHALECTURAAFORO 
                        ,SP_USUARIO.CONSUMO6       = SP_USUARIO.CONSUMO5
                        ,SP_USUARIO.CONSUMO5       = SP_USUARIO.CONSUMO4
                        ,SP_USUARIO.CONSUMO4       = SP_USUARIO.CONSUMO3
                        ,SP_USUARIO.CONSUMO3       = SP_USUARIO.CONSUMO2
                        ,SP_USUARIO.CONSUMO2       = SP_USUARIO.CONSUMO1
                        ,SP_USUARIO.CONSUMO1       = SP_USUARIO.CONSUMO
                        ,MODIFIED_BY    = '''||UN_USUARIO||'''
                        ,DATE_MODIFIED  = SYSDATE';

        IF MI_ACTPESOASEO <> 0 THEN
          MI_CAMPOS := MI_CAMPOS ||
                       ',SP_USUARIO.PESOASEO = CASE
                                                 WHEN SP_USUARIO.ASEO <> 0 AND SP_USUARIO.PESOASEO <> '||MI_PESOASEO1||'
                                                 THEN '||MI_PESOASEO1||'
                                                 ELSE SP_USUARIO.PESOASEO
                                               END';
        END IF;
        MI_CAMPOS := MI_CAMPOS ||
                       ',SP_USUARIO.LECTURACRITICA    = 0
                        ,SP_USUARIO.FECHALECTURAAFORO = NULL';
        IF MI_TIPOFACT = 'SI' THEN 
          MI_CAMPOS := MI_CAMPOS ||
                       ',SP_USUARIO.CONSUMOAFORO = 0';  
        ELSE
          CASE 
            WHEN MI_FRECUENCIAFAC = 'M'
            THEN MI_CAMPOS := MI_CAMPOS ||
                              ',SP_USUARIO.VALCONSUMOPROM = (SP_USUARIO.VALCONSUMO6 
                                                           + SP_USUARIO.VALCONSUMO5 
                                                           + SP_USUARIO.VALCONSUMO4 
                                                           + SP_USUARIO.VALCONSUMO3 
                                                           + SP_USUARIO.VALCONSUMO2 
                                                           + SP_USUARIO.VALCONSUMO1) / 6';
            WHEN MI_FRECUENCIAFAC = 'B' OR MI_FRECUENCIAFAC = 'C'
            THEN MI_CAMPOS := MI_CAMPOS ||
                              ',SP_USUARIO.VALCONSUMOPROM = (SP_USUARIO.VALCONSUMO3 
                                                           + SP_USUARIO.VALCONSUMO2 
                                                           + SP_USUARIO.VALCONSUMO1) / 3';
            WHEN MI_FRECUENCIAFAC = 'T'
            THEN MI_CAMPOS := MI_CAMPOS ||
                              ',SP_USUARIO.VALCONSUMOPROM = (SP_USUARIO.VALCONSUMO2 
                                                           + SP_USUARIO.VALCONSUMO1) / 2';
          END CASE;
        END IF;
        MI_CAMPOS := MI_CAMPOS ||
                     ',SP_USUARIO.CONSUMO                = 0
                      ,SP_USUARIO.VALCONSUMO6            = SP_USUARIO.VALCONSUMO5
                      ,SP_USUARIO.VALCONSUMO5            = SP_USUARIO.VALCONSUMO4
                      ,SP_USUARIO.VALCONSUMO4            = SP_USUARIO.VALCONSUMO3
                      ,SP_USUARIO.VALCONSUMO3            = SP_USUARIO.VALCONSUMO2
                      ,SP_USUARIO.VALCONSUMO2            = SP_USUARIO.VALCONSUMO1
                      ,SP_USUARIO.VALCONSUMO1            = SP_USUARIO.VALCONSUMO
                      ,SP_USUARIO.VALCONSUMO             = 0
                      ,SP_USUARIO.NOFECHAPAGOPERANTERIOR = SP_USUARIO.NOFECHAPAGOPERPROCESO
                      ,SP_USUARIO.NOFECHAPAGOPERPROCESO  = NULL';

        MI_CONDICION := '    SP_USUARIO.COMPANIA     = '''|| UN_COMPANIA ||'''
                         AND SP_USUARIO.CICLO        = '  || UN_CICLO ||'
                         AND SP_USUARIO.ESTADO       NOT IN (''R'')
                         AND SP_USUARIO.INDPREPARADO = 0';

        MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CAMPOS';
          MI_MSGERROR(1).VALOR := 'COMPANIA= '''|| UN_COMPANIA ||
                                 ''', CICLO= '|| UN_CICLO ||
                                 ', ESTADO diferente de R y INDPREPARADO= 0';
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTUALIZARUSUARIO,
            UN_REEMPLAZOS => MI_MSGERROR
          );
    END;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    -- PROCESO 5 -> PERIODOS DE ATRASO
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '9. Periodos de atraso';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    BEGIN
      BEGIN
        MI_TABLA     := 'SP_USUARIO';
        MI_CAMPOS    := 'SP_USUARIO.PERIODOSATRASO = 0
                        ,MODIFIED_BY    = '''||UN_USUARIO||'''
                        ,DATE_MODIFIED  = SYSDATE';

        MI_CONDICION := '    SP_USUARIO.COMPANIA = '''|| UN_COMPANIA ||'''
                         AND SP_USUARIO.CICLO    = '  || UN_CICLO || '
                         AND TRIM(NVL(SP_USUARIO.BANCOPERPROCESO,'' '')) <> '' ''
                         AND SP_USUARIO.CODIGORUTA NOT IN (SELECT SP_FINANCIABLES.CODIGORUTA
                                                           FROM   SP_FINANCIABLES
                                                           WHERE  SP_FINANCIABLES.COMPANIA = '''||UN_COMPANIA||'''
                                                             AND  SP_FINANCIABLES.CICLO    = '||UN_CICLO||'
                                                             AND  SP_FINANCIABLES.CONCEPTO = 12
                                                             AND  SP_FINANCIABLES.ANO      = '||MI_ANIO||'
                                                             AND  SP_FINANCIABLES.PERIODO  = '''||MI_PERIODO||''')';
        MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CAMPOS';
        MI_MSGERROR(1).VALOR := 'PERIODOSATRASO';
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTUALIZARUSUARIO,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '9.1 reconoce abonos';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    IF MI_RECONOCEABONOS = 'SI' THEN
      BEGIN
        BEGIN
          MI_TABLA := 'SP_USUARIO';
          MI_MERGEUSING  := 'SELECT SP_USUARIO.COMPANIA
                                   ,SP_USUARIO.CICLO
                                   ,SP_USUARIO.CODIGORUTA
                                   ,SP_FINANCIABLES.PERIODOSATRASOINI
                                   ,SP_FINANCIABLES.MONTOFINANCIAR
                                   ,SP_FACTURADO.VALOR_FACTURADO
                                   ,SP_FACTURADO.DEUDA
                                   ,SP_FINANCIABLES.SALDOFINANCIABLE
                                   ,SP_FINANCIABLES.VALORCUOTA
                             FROM   SP_USUARIO
                               INNER JOIN SP_FINANCIABLES
                                  ON      SP_USUARIO.COMPANIA   = SP_FINANCIABLES.COMPANIA 
                                 AND      SP_USUARIO.CICLO      = SP_FINANCIABLES.CICLO
                                 AND      SP_USUARIO.CODIGORUTA = SP_FINANCIABLES.CODIGORUTA
                               INNER JOIN SP_FACTURADO
                                  ON      SP_FINANCIABLES.COMPANIA   = SP_FACTURADO.COMPANIA 
                                 AND      SP_FINANCIABLES.CICLO      = SP_FACTURADO.CICLO
                                 AND      SP_FINANCIABLES.CODIGORUTA = SP_FACTURADO.CODIGORUTA
                                 AND      SP_FINANCIABLES.CONCEPTO   = SP_FACTURADO.CONCEPTO
                                 AND      SP_FINANCIABLES.PERIODO    = SP_FACTURADO.PERIODO 
                                 AND      SP_FINANCIABLES.ANO        = SP_FACTURADO.ANO 
                             WHERE  SP_FINANCIABLES.COMPANIA     = '''|| UN_COMPANIA ||'''
                               AND  SP_FINANCIABLES.CICLO        =   '|| UN_CICLO ||'
                               AND  SP_FINANCIABLES.ANO          =   '|| MI_ANIO ||'
                               AND  SP_FINANCIABLES.PERIODO      = '''|| MI_PERIODO ||'''
                               AND  SP_FINANCIABLES.NUMEROCUOTAS >= SP_FINANCIABLES.NROCUOTA
                               AND  SP_FINANCIABLES.CONCEPTO     = 12
                               AND  (NVL(SP_USUARIO.PERIODOSATRASO,0)+1) = '||MI_NUMPERIODOS||'
                               AND  (SP_USUARIO.ESTADO                    IN(''A'',''C'',''S'') '|| 
                               PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => MI_AUMENPERSUSP = 'SI',
                                                     UN_SI        => ' OR  (SP_USUARIO.ESTADO = ''S'' 
                                                                      AND  SP_USUARIO.TOTFACTURAPERACTUAL > 0))',
                                                     UN_NO        => ')');
          MI_MERGEENLACE := '    TABLA.COMPANIA   = VISTA.COMPANIA 
                             AND TABLA.CICLO      = VISTA.CICLO
                             AND TABLA.CODIGORUTA = VISTA.CODIGORUTA';
          MI_MERGEEXISTE := 'UPDATE 
                               SET TABLA.PERIODOSATRASO = (NVL(VISTA.PERIODOSATRASOINI,0) + '|| MI_NUMPERIODOS ||') 
                                                         - TRUNC((NVL(VISTA.MONTOFINANCIAR,0) - (NVL(VISTA.VALOR_FACTURADO,0) + NVL(VISTA.DEUDA,0)) 
                                                         - VISTA.SALDOFINANCIABLE + VISTA.VALORCUOTA) / NVL(VISTA.VALORCUOTA, 0))
                                  ,TABLA.MODIFIED_BY    = '''||UN_USUARIO||'''
                                  ,TABLA.DATE_MODIFIED  = SYSDATE';

          MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA, 
                                            UN_ACCION      => 'MM', 
                                            UN_MERGEUSING  => MI_MERGEUSING, 
                                            UN_MERGEENLACE => MI_MERGEENLACE,
                                            UN_MERGEEXISTE => MI_MERGEEXISTE);    
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD   => SQLCODE,
            UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_MERGEUSUARIO
          );
      END; 
    END IF;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '9.2 usuarios no pagos';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    -- CUANDO EL USUARIO NO PAGA, LOS PERIODOS DE ATRASO SE INCREMENTAN EN 1
    BEGIN
      BEGIN
        MI_TABLA     := 'SP_USUARIO';
        MI_CAMPOS    := 'SP_USUARIO.PERIODOSATRASO = SP_USUARIO.PERIODOSATRASO + 1
                        ,MODIFIED_BY               = '''||UN_USUARIO||'''
                        ,DATE_MODIFIED             = SYSDATE';
        MI_CONDICION := '    SP_USUARIO.COMPANIA                         = '''|| UN_COMPANIA ||'''
                         AND SP_USUARIO.CICLO                            = '  || UN_CICLO || '
                         AND SP_USUARIO.ANO                              = '||MI_ANIO||'
                         AND SP_USUARIO.PERIODO                          = '''||MI_PERIODO||'''
                         AND TRIM(NVL(SP_USUARIO.BANCOPERPROCESO,'' '')) = '' ''
                         AND SP_USUARIO.TOTFACTURAPERACTUAL              NOT IN (0) 
                         AND (SP_USUARIO.ESTADO                          IN(''A'',''C'',''S'')'||
                         PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => MI_AUMENPERSUSP = 'SI',
                                               UN_SI        => ' OR (SP_USUARIO.ESTADO = ''S'' 
                                                                AND  SP_USUARIO.TOTFACTURAPERACTUAL > 0))',
                                               UN_NO        => ')');
        MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CAMPOS';
        MI_MSGERROR(1).VALOR := 'PERIODOSATRASO';
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTUALIZARUSUARIO,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -- PROCESO 7 -> PERIODO DE NO COBRO FINANCIABLES
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '11. Periodos de no cobro financiables';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    BEGIN
      BEGIN
      MI_TABLA     := 'SP_FINANCIABLES';
      MI_MERGEUSING:='SELECT SP_FINANCIABLES.COMPANIA,
                             SP_FINANCIABLES.CICLO,
                             SP_FINANCIABLES.CODIGORUTA,
                             SP_FINANCIABLES.CONCEPTO,
                             SP_FINANCIABLES.SALDOFINANCIABLE ,
                             CASE
                               WHEN SP_FINANCIABLES.SALDOFINANCIABLE <= SP_FINANCIABLES.VALORCUOTA
                               THEN 0
                               ELSE SP_FINANCIABLES.SALDOFINANCIABLE - SP_FINANCIABLES.VALORCUOTA
                             END SAL ,
                             SP_FINANCIABLES.NROCUOTA+1 NROCUOTA,
                             '||  MI_ANIOSIG||' ANO,
                             '''||MI_PERIODOSIG||''' PERIODO
                        FROM SP_FACTURADO
                       INNER JOIN SP_FINANCIABLES
                          ON SP_FACTURADO.CONCEPTO   = SP_FINANCIABLES.CONCEPTO
                         AND SP_FACTURADO.PERIODO    = SP_FINANCIABLES.PERIODO
                         AND SP_FACTURADO.ANO        = SP_FINANCIABLES.ANO
                         AND SP_FACTURADO.CODIGORUTA = SP_FINANCIABLES.CODIGORUTA
                         AND SP_FACTURADO.CICLO      = SP_FINANCIABLES.CICLO
                         AND SP_FACTURADO.COMPANIA   = SP_FINANCIABLES.COMPANIA
                       INNER JOIN SP_USUARIO
                          ON SP_FINANCIABLES.COMPANIA      = SP_USUARIO.COMPANIA
                         AND SP_FINANCIABLES.CICLO         = SP_USUARIO.CICLO
                             AND SP_FINANCIABLES.CODIGORUTA    = SP_USUARIO.CODIGORUTA
                       WHERE SP_FINANCIABLES.COMPANIA                 = '''||UN_COMPANIA||'''
                         AND SP_FINANCIABLES.CICLO                      = '  ||UN_CICLO||'
                         AND SP_FINANCIABLES.ANO                        = '  ||MI_ANIO||'
                         AND SP_FINANCIABLES.PERIODO                    = '''||MI_PERIODO||'''
                         AND SP_USUARIO.PERIODOSNOCOBROFIN              > 0
                         AND (ROUND(SP_FACTURADO.VALOR_FACTURADO,0) + 2 > ROUND(SP_FINANCIABLES.VALORCUOTA,0)
                         AND ROUND(SP_FACTURADO.VALOR_FACTURADO,0)  - 2 < ROUND(SP_FINANCIABLES.VALORCUOTA,0))';

           MI_MERGEENLACE :=' TABLA.COMPANIA   = VISTA.COMPANIA 
                       AND TABLA.CICLO      = VISTA.CICLO
                       AND TABLA.CODIGORUTA = VISTA.CODIGORUTA
                       AND TABLA.CONCEPTO   = VISTA.CONCEPTO ';  

          MI_MERGEEXISTE:=' UPDATE SET  TABLA.SALDOFINANCIABLE    = VISTA.SAL,
                                        TABLA.NROCUOTA            = VISTA.NROCUOTA,
                                        TABLA.ANO                 = VISTA.ANO,
                                        TABLA.PERIODO             = VISTA.PERIODO,
                                        TABLA.MODIFIED_BY    = '''||UN_USUARIO||''',
                                        TABLA.DATE_MODIFIED  = SYSDATE';

        MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA, 
                                          UN_ACCION      => 'MM', 
                                          UN_MERGEUSING  => MI_MERGEUSING,
                                          UN_MERGEENLACE => MI_MERGEENLACE,
                                          UN_MERGEEXISTE => MI_MERGEEXISTE);  

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CAMPOS';
        MI_MSGERROR(1).VALOR := 'SALDOFINANCIABLE,NROCUOTA,ANO y PERIODO';
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD   => SQLCODE,
          UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_MERGEFINANCIABLES,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END; 
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '11.1 financiables';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    BEGIN
      BEGIN

          MI_MERGEUSING:='SELECT SP_FINANCIABLES.COMPANIA,
                                 SP_FINANCIABLES.CICLO,
                                 SP_FINANCIABLES.CODIGORUTA,
                                 SP_FINANCIABLES.CONCEPTO,
                                 SP_FINANCIABLES.SALDOFINANCIABLE ,
                                 CASE
                                   WHEN SP_FINANCIABLES.SALDOFINANCIABLE <= SP_FINANCIABLES.VALORCUOTA
                                   THEN 0
                                   ELSE SP_FINANCIABLES.SALDOFINANCIABLE - SP_FINANCIABLES.VALORCUOTA
                                 END SAL ,
                                 SP_FINANCIABLES.NROCUOTA+1 NROCUOTA,
                                 '||  MI_ANIOSIG||' ANO,
                                 '''||MI_PERIODOSIG||''' PERIODO
                            FROM SP_FINANCIABLES
                            INNER JOIN SP_USUARIO
                               ON SP_FINANCIABLES.COMPANIA    = SP_USUARIO.COMPANIA
                              AND SP_FINANCIABLES.CICLO      = SP_USUARIO.CICLO
                              AND SP_FINANCIABLES.CODIGORUTA = SP_USUARIO.CODIGORUTA
                            INNER JOIN SP_D_DEUDAFACTURADAFINANCIADA
                               ON SP_FINANCIABLES.COMPANIA    = SP_D_DEUDAFACTURADAFINANCIADA.COMPANIA
                              AND SP_FINANCIABLES.CICLO      = SP_D_DEUDAFACTURADAFINANCIADA.CICLO
                              AND SP_FINANCIABLES.CODIGORUTA = SP_D_DEUDAFACTURADAFINANCIADA.CODIGORUTA
                              AND SP_FINANCIABLES.ANO        = SP_D_DEUDAFACTURADAFINANCIADA.ANO
                              AND SP_FINANCIABLES.PERIODO    = SP_D_DEUDAFACTURADAFINANCIADA.PERIODO
                              AND SP_FINANCIABLES.CONCEPTO   = SP_D_DEUDAFACTURADAFINANCIADA.CONCEPTO
                             LEFT JOIN SP_FACTURADO
                               ON SP_D_DEUDAFACTURADAFINANCIADA.COMPANIA         = SP_FACTURADO.COMPANIA
                              AND SP_D_DEUDAFACTURADAFINANCIADA.CICLO           = SP_FACTURADO.CICLO
                              AND SP_D_DEUDAFACTURADAFINANCIADA.CODIGORUTA      = SP_FACTURADO.CODIGORUTA
                              AND SP_D_DEUDAFACTURADAFINANCIADA.ANO             = SP_FACTURADO.ANO
                              AND SP_D_DEUDAFACTURADAFINANCIADA.PERIODO         = SP_FACTURADO.PERIODO
                              AND SP_D_DEUDAFACTURADAFINANCIADA.CONCEPTO        = SP_FACTURADO.CONCEPTO
                            WHERE SP_FINANCIABLES.COMPANIA      = '''||UN_COMPANIA||'''
                              AND SP_FINANCIABLES.CICLO         = '  ||UN_CICLO||'
                              AND SP_FINANCIABLES.ANO           = '  ||MI_ANIO||'
                              AND SP_FINANCIABLES.PERIODO       = '''||MI_PERIODO||'''
                              AND SP_USUARIO.PERIODOSNOCOBROFIN > 0
                              AND SP_FACTURADO.VALOR_FACTURADO = SP_USUARIO.TOTFACTURAPERACTUAL
                              AND ROUND(SP_D_DEUDAFACTURADAFINANCIADA.VALOR_FACTURADO) +2 > ROUND (SP_FINANCIABLES.VALORCUOTA)
                              AND ROUND(SP_D_DEUDAFACTURADAFINANCIADA.VALOR_FACTURADO) -2 < ROUND (SP_FINANCIABLES.VALORCUOTA)';

               MI_MERGEENLACE :=' TABLA.COMPANIA   = VISTA.COMPANIA 
                           AND TABLA.CICLO      = VISTA.CICLO
                           AND TABLA.CODIGORUTA = VISTA.CODIGORUTA
                           AND TABLA.CONCEPTO   = VISTA.CONCEPTO ';  

              MI_MERGEEXISTE:=' UPDATE SET  TABLA.SALDOFINANCIABLE     = VISTA.SAL,
                                            TABLA.NROCUOTA             = VISTA.NROCUOTA,
                                            TABLA.MODIFIED_BY          = '''||UN_USUARIO||''',
                                            TABLA.DATE_MODIFIED        = SYSDATE';

              MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA, 
                                                UN_ACCION      => 'MM', 
                                                UN_MERGEUSING  => MI_MERGEUSING,
                                                UN_MERGEENLACE => MI_MERGEENLACE,
                                                UN_MERGEEXISTE => MI_MERGEEXISTE);   

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CAMPOS';
        MI_MSGERROR(1).VALOR := 'SALDOFINANCIABLE,NROCUOTA,ANO y PERIODO';
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD   => SQLCODE,
          UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_MERGEFINANCIABLES,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '11.2 financiables';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    BEGIN
      BEGIN

      MI_TABLA     := 'SP_FINANCIABLES';
      MI_MERGEUSING:='SELECT SP_FINANCIABLES.COMPANIA,
                             SP_FINANCIABLES.CICLO,
                             SP_FINANCIABLES.CODIGORUTA,
                             '||  MI_ANIOSIG||' ANO,
                             '''||MI_PERIODOSIG||''' PERIODO,
                             SP_FINANCIABLES.CONCEPTO
                        FROM SP_FINANCIABLES
                       INNER JOIN SP_USUARIO
                          ON SP_FINANCIABLES.COMPANIA      = SP_USUARIO.COMPANIA
                         AND SP_FINANCIABLES.CICLO         = SP_USUARIO.CICLO
                         AND SP_FINANCIABLES.CODIGORUTA    = SP_USUARIO.CODIGORUTA
                       WHERE SP_FINANCIABLES.COMPANIA      = '''||UN_COMPANIA||'''
                         AND SP_FINANCIABLES.CICLO         = '  ||UN_CICLO||'
                         AND SP_FINANCIABLES.ANO           = '  ||MI_ANIO||'
                         AND SP_FINANCIABLES.PERIODO       = '''||MI_PERIODO||'''
                         AND SP_USUARIO.PERIODOSNOCOBROFIN > 0';

           MI_MERGEENLACE :=' TABLA.COMPANIA   = VISTA.COMPANIA 
                       AND TABLA.CICLO      = VISTA.CICLO
                       AND TABLA.CODIGORUTA = VISTA.CODIGORUTA
                       AND TABLA.CONCEPTO   = VISTA.CONCEPTO ';  

          MI_MERGEEXISTE:=' UPDATE SET  TABLA.ANO           = VISTA.ANO,
                                        TABLA.PERIODO      = VISTA.PERIODO,
                                        TABLA.MODIFIED_BY   = '''||UN_USUARIO||''',
                                        TABLA.DATE_MODIFIED = SYSDATE';


        MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA, 
                                          UN_ACCION      => 'MM', 
                                          UN_MERGEUSING  => MI_MERGEUSING,
                                          UN_MERGEENLACE => MI_MERGEENLACE,
                                          UN_MERGEEXISTE => MI_MERGEEXISTE); 


        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CAMPOS';
        MI_MSGERROR(1).VALOR := 'ANO y PERIODO';
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD   => SQLCODE,
          UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_MERGEFINANCIABLES,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -- PROCESO 8 -> ACTUALIZACION FACTURADO
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '12. Actualiza Facturado';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    BEGIN
      BEGIN

        MI_TABLA      := 'SP_FACTURADO';               
        MI_MERGEUSING := 'SELECT SP_FACTURADO.COMPANIA,
                                 SP_FACTURADO.CICLO,
                                 SP_FACTURADO.CODIGORUTA,
                                 '||  MI_ANIOSIG||'  ANO,
                                 '''||MI_PERIODOSIG||''' PERIODO ,
                                 SP_FACTURADO.CONCEPTO,
                                 0 VALOR_FACTURADO ,
                                 0 VALOR_FACTURADOIN ,
                                 0 DEUDAIN ,
                                 0 VALORFINANT ,
                                 0 VALORFINACT ,
                                 0 VALORABONOANT ,
                                 0 VALORABONOACT ,
                                 0 VALORABONODEUDA ,
                                 0 CREDITOABONADO ,
                                 0 SALDOCREDITO ,
                                 CASE WHEN TRIM(NVL(SP_USUARIO.BANCOPERPROCESO,'' '')) = '' '' AND SP_USUARIO.TOTFACTURAPERACTUAL = 0
                                            AND SP_USUARIO.CREDITOABONADO              > 0
                                 THEN 
                                     0
                                 ELSE
                                     (CASE WHEN TRIM(NVL(SP_USUARIO.BANCOPERPROCESO,'' '')) = '' ''
                                      THEN
                                         NVL(SP_FACTURADO.DEUDA,0) - NVL(SP_FACTURADO.VALORABONOANT,0) + NVL(SP_FACTURADO.VALORFINANT,0)
                                                                   + NVL(SP_FACTURADO.VALORFINACT,0) - NVL(SP_FACTURADO.CREDITOABONADO,0) + ';

        IF MI_RECARGO = 'SI' THEN
            MI_MERGEUSING := MI_MERGEUSING || 
                      '(CASE
                         WHEN SP_FACTURADO.CONCEPTO NOT IN('||MI_NUMCONCEPTOS||',49,12,17)';
        ELSE
        MI_MERGEUSING := MI_MERGEUSING || 
                      '(CASE
                         WHEN SP_FACTURADO.CONCEPTO NOT IN('||MI_NUMCONCEPTOS||','||(MI_NUMCONCEPTOS -3)||',49,12,17)';
        END IF;
        MI_MERGEUSING := MI_MERGEUSING || 
                    '  THEN SP_FACTURADO.VALOR_FACTURADO - NVL(SP_FACTURADO.VALORABONOACT,0)
                           ELSE 0
                         END)
                       ELSE 0
                     END)
                   END DEUDA';

        IF MI_CIEPERDIS = 'SI' THEN 
           MI_MERGEUSING := MI_MERGEUSING || 
                    ' ,CASE WHEN SP_FACTURADO.CONCEPTO NOT IN(12,17) 
                             AND (SP_FACTURADO.CONCEPTO BETWEEN 1 AND 48 
                              OR  SP_FACTURADO.CONCEPTO IN(201,202,203,204,205,206,207,246,247,248,249))                             
                           THEN SP_FACTURADO.VALOR_FACTURADO 
                              + SP_FACTURADO.VALORFINACT 
                              - SP_FACTURADO.VALORABONOACT 
                              - SP_FACTURADO.CREDITOABONADO
                           ELSE SP_FACTURADO.VALOR_FACTURADOANT
                      END VALOR_FACTURADOANT                     
                     ,CASE WHEN SP_FACTURADO.CONCEPTO NOT IN(12,17) 
                             AND (SP_FACTURADO.CONCEPTO BETWEEN 1 AND 48 
                              OR SP_FACTURADO.CONCEPTO IN  (201,202,203,204,205,206,207,246,247,248,249))
                           THEN SP_FACTURADO.DEUDA 
                              + SP_FACTURADO.VALORFINANT 
                              - SP_FACTURADO.VALORABONOANT
                           ELSE SP_FACTURADO.DEUDAANT
                      END DEUDAANT';

        ELSE
           MI_MERGEUSING := MI_MERGEUSING || 
                    ',SP_FACTURADO.VALOR_FACTURADOANT
                     ,SP_FACTURADO.DEUDAANT';
        END IF;
        MI_MERGEUSING := MI_MERGEUSING ||
                    ' FROM SP_FACTURADO
                        INNER JOIN SP_USUARIO 
                           ON      SP_FACTURADO.COMPANIA   = SP_USUARIO.COMPANIA
                          AND      SP_FACTURADO.CICLO      = SP_USUARIO.CICLO
                          AND      SP_FACTURADO.CODIGORUTA = SP_USUARIO.CODIGORUTA
                      WHERE  SP_FACTURADO.COMPANIA = '''||UN_COMPANIA||'''
                        AND  SP_FACTURADO.CICLO    = '  ||UN_CICLO||'
                        AND  SP_FACTURADO.ANO      = '  ||MI_ANIO||'
                        AND  SP_FACTURADO.PERIODO  = '''||MI_PERIODO||'''';



        MI_MERGEENLACE :=' TABLA.COMPANIA   = VISTA.COMPANIA 
                       AND TABLA.CICLO      = VISTA.CICLO
                       AND TABLA.CODIGORUTA = VISTA.CODIGORUTA
                       AND TABLA.ANO        = VISTA.ANO
                       AND TABLA.PERIODO    = VISTA.PERIODO
                       AND TABLA.CONCEPTO   = VISTA.CONCEPTO ';


      MI_MERGEEXISTE:=' UPDATE SET  TABLA.VALOR_FACTURADO    = VISTA.VALOR_FACTURADO,
                                    TABLA.VALOR_FACTURADOIN  = VISTA.VALOR_FACTURADOIN,
                                    TABLA.DEUDAIN            = VISTA.DEUDAIN,
                                    TABLA.VALORFINANT        = VISTA.VALORFINANT,
                                    TABLA.VALORFINACT        = VISTA.VALORFINACT,
                                    TABLA.VALORABONOANT      = VISTA.VALORABONOANT,
                                    TABLA.VALORABONOACT      = VISTA.VALORABONOACT,
                                    TABLA.VALORABONODEUDA    = VISTA.VALORABONODEUDA,
                                    TABLA.CREDITOABONADO     = VISTA.CREDITOABONADO,
                                    TABLA.SALDOCREDITO       = VISTA.SALDOCREDITO,
                                    TABLA.DEUDA              = VISTA.DEUDA,
                                    TABLA.VALOR_FACTURADOANT = VISTA.VALOR_FACTURADOANT,
                                    TABLA.DEUDAANT           = VISTA.DEUDAANT,
                                    MODIFIED_BY    = '''||UN_USUARIO||''',
                                    DATE_MODIFIED  = SYSDATE';

        MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA, 
                                          UN_ACCION      => 'MM', 
                                          UN_MERGEUSING  => MI_MERGEUSING,
                                          UN_MERGEENLACE => MI_MERGEENLACE,
                                          UN_MERGEEXISTE => MI_MERGEEXISTE);    

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD   => SQLCODE,
          UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_MERGEFACTURADO
        );
    END;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -- PROCESO 9 -> ACTUALIZACION FINANCIABLES
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '13. Actualiza Financiables';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    BEGIN 

        SELECT COUNT (*) CANTIDAD
         INTO MI_CANT_FINANCIABLES
         FROM SP_FINANCIABLES
        INNER JOIN SP_USUARIO
           ON SP_FINANCIABLES.COMPANIA          = SP_USUARIO.COMPANIA
          AND SP_FINANCIABLES.CICLO            = SP_USUARIO.CICLO
          AND SP_FINANCIABLES.CODIGORUTA       = SP_USUARIO.CODIGORUTA
        WHERE SP_FINANCIABLES.COMPANIA       = UN_COMPANIA
          AND SP_FINANCIABLES.CICLO            = UN_CICLO
          AND SP_FINANCIABLES.ANO              = MI_ANIOSIG
          AND SP_FINANCIABLES.PERIODO          = MI_PERIODOSIG
          AND SP_USUARIO.PERIODOSNOCOBROFIN   <= 0
          AND NVL(SP_FINANCIABLES.BLOQUEADO,0) = 0;

          IF MI_CANT_FINANCIABLES >0 THEN 
               RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;

          END IF;
       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CAMPOS';
        MI_MSGERROR(1).VALOR := 'SALDOFINANCIABLE y NROCUOTA';
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD   => SQLCODE,
          UN_ERROR_COD => PCK_ERRORES.ERR_FINANCIABLES,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;    

    -- A LOS USUARIOS QUE NO ESTAN BLOQUEADOS, SE LES ACTUALIZA EL SALDO, NUMERO DE CUOTA Y SE PASAN AL SIGUIENTE PERIODO
    BEGIN
      BEGIN
            MI_CAMPOS    := 'NROCUOTA         = NROCUOTA + 1,
                             ANO              = '||  MI_ANIOSIG||',
                             PERIODO          = '''||MI_PERIODOSIG||''',
                             SALDOFINANCIABLE = FSALDOFINANCIABLE';

            MI_TABLA := '( SELECT 
                                  FINANCIABLES.ANO,
                                  FINANCIABLES.PERIODO,
                                  FINANCIABLES.NROCUOTA,
                                  CASE WHEN FINANCIABLES.SALDOFINANCIABLE <= FINANCIABLES.VALORCUOTA 
                                  THEN 0
                                  ELSE FINANCIABLES.SALDOFINANCIABLE - FINANCIABLES.VALORCUOTA 
                                  END FSALDOFINANCIABLE,
                                  SALDOFINANCIABLE
                            FROM SP_FINANCIABLES FINANCIABLES
                           INNER JOIN SP_USUARIO USUARIO
                              ON FINANCIABLES.COMPANIA   = USUARIO.COMPANIA
                             AND FINANCIABLES.CICLO      = USUARIO.CICLO
                             AND FINANCIABLES.CODIGORUTA = USUARIO.CODIGORUTA
                           WHERE FINANCIABLES.COMPANIA          = '''||UN_COMPANIA||'''
                             AND FINANCIABLES.CICLO             ='  ||UN_CICLO||'
                             AND FINANCIABLES.ANO               = '  ||MI_ANIO||'
                             AND FINANCIABLES.PERIODO           = '''||MI_PERIODO||'''
                             AND USUARIO.PERIODOSNOCOBROFIN    <= 0
                             AND NVL(FINANCIABLES.BLOQUEADO,0)  = 0
            )';

           MI_AFECTADOS := PCK_DATOS.FC_ACME ( UN_TABLA     => MI_TABLA,
                                               UN_ACCION    => 'M',
                                               UN_CAMPOS    => MI_CAMPOS);


        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'PERIODO';
        MI_MSGERROR(1).VALOR := MI_PERIODOSIG;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD   => SQLCODE,
          UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_MERGEFINANCIABLES,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '13.1 Actualiza Financiables';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    -- SI EL USUARIO ESTA BLOQUEADO, SE PASA AL SIGUIENTE PERIODO CUANDO SEA MENOR O IGUAL A LOS DATOS DE BLOQUEADOHASTA
     BEGIN 

        SELECT COUNT (*) CANTIDAD
         INTO MI_CANT_FINANCIABLES
         FROM SP_FINANCIABLES
        INNER JOIN SP_USUARIO
           ON SP_FINANCIABLES.COMPANIA         = SP_USUARIO.COMPANIA
          AND SP_FINANCIABLES.CICLO            = SP_USUARIO.CICLO
          AND SP_FINANCIABLES.CODIGORUTA       = SP_USUARIO.CODIGORUTA
        WHERE SP_FINANCIABLES.COMPANIA               = UN_COMPANIA
          AND SP_FINANCIABLES.CICLO                  = UN_CICLO
          AND SP_FINANCIABLES.ANO                    = UN_ANIO
          AND SP_FINANCIABLES.PERIODO                = UN_PERIODO
          AND SP_USUARIO.PERIODOSNOCOBROFIN          <= 0
          AND NVL(SP_FINANCIABLES.BLOQUEADO,0)       = 0
          AND NVL(SP_FINANCIABLES.BLOQUEADO,0)       <> 0
          AND SP_FINANCIABLES.BLOQUEADOHASTAPERIODO  >= MI_PERIODOSIG
          AND SP_FINANCIABLES.BLOQUEADOHASTAANO      = MI_ANIOSIG;

          IF MI_CANT_FINANCIABLES >0  THEN 
               RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;

          END IF;
       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'PERIODO';
        MI_MSGERROR(1).VALOR := MI_PERIODOSIG;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD   => SQLCODE,
          UN_ERROR_COD => PCK_ERRORES.ERR_FINANCIABLES,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;    



    BEGIN
      BEGIN

            MI_CAMPOS    := 'ANO              = '||  MI_ANIOSIG||',
                             PERIODO          = '''||MI_PERIODOSIG||'''';

            MI_TABLA := '( SELECT 
                                  FINANCIABLES.ANO,
                                  FINANCIABLES.PERIODO
                            FROM SP_FINANCIABLES FINANCIABLES
                           INNER JOIN SP_USUARIO USUARIO
                              ON FINANCIABLES.COMPANIA   = USUARIO.COMPANIA
                             AND FINANCIABLES.CICLO      = USUARIO.CICLO
                             AND FINANCIABLES.CODIGORUTA = USUARIO.CODIGORUTA
                           WHERE FINANCIABLES.COMPANIA                 = '''||UN_COMPANIA||'''
                             AND FINANCIABLES.CICLO                    = '  ||UN_CICLO||'
                             AND FINANCIABLES.ANO                      = '  ||MI_ANIO||'
                             AND FINANCIABLES.PERIODO                  = '''||MI_PERIODO||'''
                             AND USUARIO.PERIODOSNOCOBROFIN            <= 0
                             AND NVL(FINANCIABLES.BLOQUEADO,0)         <> 0
                             AND FINANCIABLES.BLOQUEADOHASTAPERIODO >= '''||MI_PERIODOSIG||'''
                            AND FINANCIABLES.BLOQUEADOHASTAANO      = '  ||MI_ANIOSIG||'
                         )';

           MI_AFECTADOS := PCK_DATOS.FC_ACME ( UN_TABLA     => MI_TABLA,
                                               UN_ACCION    => 'M',
                                               UN_CAMPOS    => MI_CAMPOS);


        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CAMPOS';
        MI_MSGERROR(1).VALOR := 'SALDOFINANCIABLE y NROCUOTA';
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD   => SQLCODE,
          UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_MERGEFINANCIABLES,
          UN_REEMPLAZOS => MI_MSGERROR
        );
        END;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '13.2 Actualiza Financiables';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    -- SI EL USUARIO ESTA BLOQUEADO, SE PASA AL SIGUIENTE PERIODO Y SE DESBLOQUEA CUANDO SEA MAYOR O IGUAL A LOS DATOS DE BLOQUEADOHASTA


      BEGIN
      BEGIN

            MI_CAMPOS    := 'ANO           = '||  MI_ANIOSIG||',
                             PERIODO       = '''||MI_PERIODOSIG||''',
                             BLOQUEADO     = 0,
                             MODIFIED_BY   = '''||UN_USUARIO||''',
                             DATE_MODIFIED = SYSDATE';

            MI_TABLA := '( SELECT SP_FINANCIABLES.COMPANIA,
                                  SP_FINANCIABLES.CICLO,
                                  SP_FINANCIABLES.CODIGORUTA,
                                  SP_FINANCIABLES.ANO,
                                  SP_FINANCIABLES.PERIODO,
                                  SP_FINANCIABLES.CONCEPTO,
                                  SP_FINANCIABLES.BLOQUEADO,
                                  SP_FINANCIABLES.MODIFIED_BY,
                                  SP_FINANCIABLES.DATE_MODIFIED
                             FROM SP_FINANCIABLES
                            INNER JOIN SP_USUARIO
                               ON SP_FINANCIABLES.COMPANIA               = SP_USUARIO.COMPANIA
                              AND SP_FINANCIABLES.CICLO                  = SP_USUARIO.CICLO
                              AND SP_FINANCIABLES.CODIGORUTA             = SP_USUARIO.CODIGORUTA
                            WHERE SP_FINANCIABLES.COMPANIA               = '''||UN_COMPANIA||'''
                              AND SP_FINANCIABLES.CICLO                  = '  ||UN_CICLO||'
                              AND SP_FINANCIABLES.ANO                    = '  ||MI_ANIO||'
                              AND SP_FINANCIABLES.PERIODO                = '''||MI_PERIODO||'''
                              AND SP_USUARIO.PERIODOSNOCOBROFIN          <= 0
                              AND NVL(SP_FINANCIABLES.BLOQUEADO,0)       <> 0
                              AND ((SP_FINANCIABLES.BLOQUEADOHASTAPERIODO  < '''||MI_PERIODOSIG||'''
                              AND SP_FINANCIABLES.BLOQUEADOHASTAANO       = '  ||MI_ANIOSIG||')
                               OR (SP_FINANCIABLES.BLOQUEADOHASTAPERIODO  = ''01''
                               AND SP_FINANCIABLES.BLOQUEADOHASTAANO      < ' ||MI_ANIOSIG||'))
                         )';

           MI_AFECTADOS := PCK_DATOS.FC_ACME ( UN_TABLA     => MI_TABLA,
                                               UN_ACCION    => 'M',
                                               UN_CAMPOS    => MI_CAMPOS);


        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CAMPOS';
        MI_MSGERROR(1).VALOR := 'SALDOFINANCIABLE y NROCUOTA';
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD   => SQLCODE,
          UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_MERGEFINANCIABLES,
          UN_REEMPLAZOS => MI_MSGERROR
        );
        END;


    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '13.3 Actualiza Financiables';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    -- UNA VEZ QUE SE HAN HECHRO LAS VALIDACIONES DE LOS PERIODOS DE 'NO' COBRO PARA ACTUALIZAR LOS FINANCIABLES, SE DISMINUYEN DICHROS PERIODOS
    BEGIN
      BEGIN
        MI_TABLA     := 'SP_USUARIO';
        MI_CAMPOS    := 'SP_USUARIO.PERIODOSNOCOBROFIN = SP_USUARIO.PERIODOSNOCOBROFIN - 1
                        ,MODIFIED_BY                   = '''||UN_USUARIO||'''
                        ,DATE_MODIFIED                 = SYSDATE';

        MI_CONDICION := '    SP_USUARIO.COMPANIA                         = '''|| UN_COMPANIA ||'''
                         AND SP_USUARIO.CICLO                            = '  || UN_CICLO || '
                         AND SP_USUARIO.PERIODOSNOCOBROFIN               > 0';
        MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CAMPOS';
        MI_MSGERROR(1).VALOR := 'COMPANIA= '''|| UN_COMPANIA ||
                               ''', CICLO= '|| UN_CICLO || 
                               ' y PERIODOSNOCOBROFIN mayor que 0';
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTUALIZARUSUARIO,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -- PROCESO 10 -> FINANCIABLES VENCIDOS CON DEUDA
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '14. Financiables de deuda';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    -- UNA VEZ QUE SE HAN HECHRO LAS VALIDACIONES DE LOS PERIODOS DE 'NO' COBRO PARA ACTUALIZAR LOS FINANCIABLES, SE DISMINUYEN DICHROS PERIODOS
    IF MI_RECONOCEABONOS = 'SI' THEN
      BEGIN
        BEGIN
          MI_TABLA := 'SP_FINANCIABLES';
          MI_MERGEUSING  := 'SELECT SP_FINANCIABLES.COMPANIA
                                   ,SP_FINANCIABLES.CICLO
                                   ,SP_FINANCIABLES.CODIGORUTA
                                   ,SP_FINANCIABLES.CONCEPTO
                                   ,SP_FINANCIABLES.PERIODO
                                   ,SP_FINANCIABLES.ANO
                             FROM   SP_FINANCIABLES 
                               INNER JOIN SP_USUARIO
                                  ON      SP_FINANCIABLES.COMPANIA   = SP_USUARIO.COMPANIA 
                                 AND      SP_FINANCIABLES.CICLO      = SP_USUARIO.CICLO
                                 AND      SP_FINANCIABLES.CODIGORUTA = SP_USUARIO.CODIGORUTA
                             WHERE  SP_FINANCIABLES.COMPANIA     = '''|| UN_COMPANIA ||'''
                               AND  SP_FINANCIABLES.CICLO        =   '|| UN_CICLO ||'
                               AND  SP_FINANCIABLES.ANO          =   '|| MI_ANIOSIG ||'
                               AND  SP_FINANCIABLES.PERIODO      = '''|| MI_PERIODOSIG ||'''
                               AND  SP_FINANCIABLES.CONCEPTO     = 12
                               AND  SP_USUARIO.PERIODOSATRASO +1 ='||MI_NUMPERIODOS||' 
                               AND  SP_USUARIO.ESTADO            IN (''A'',''C'')';
          MI_MERGEENLACE := '    TABLA.COMPANIA   = VISTA.COMPANIA 
                             AND TABLA.CICLO      = VISTA.CICLO
                             AND TABLA.CODIGORUTA = VISTA.CODIGORUTA
                             AND TABLA.CONCEPTO   = VISTA.CONCEPTO
                             AND TABLA.PERIODO    = VISTA.PERIODO 
                             AND TABLA.ANO        = VISTA.ANO ';
          MI_MERGEEXISTE := 'UPDATE 
                               SET TABLA.VALORCUOTA = TABLA.SALDOFINANCIABLE
                               ,MODIFIED_BY    = '''||UN_USUARIO||'''
                               ,DATE_MODIFIED  = SYSDATE';

          MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA, 
                                            UN_ACCION      => 'MM', 
                                            UN_MERGEUSING  => MI_MERGEUSING, 
                                            UN_MERGEENLACE => MI_MERGEENLACE,
                                            UN_MERGEEXISTE => MI_MERGEEXISTE);    
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CAMPOS';
          MI_MSGERROR(1).VALOR := 'VALORCUOTA';
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD   => SQLCODE,
            UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_MERGEFINANCIABLES,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END; 
    END IF;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -- PROCESO 11 -> PERIODOS DE NO COBRO FACTURADOS
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '15. Periodos de no cobro facturados';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    BEGIN
      BEGIN
        MI_TABLA     := 'SP_USUARIO';
        MI_CAMPOS    := 'SP_USUARIO.PERIODOSNOCOBROFAC = CASE 
                                                           WHEN SP_USUARIO.PERIODOSNOCOBROFAC > 0
                                                           THEN SP_USUARIO.PERIODOSNOCOBROFAC - 1
                                                           ELSE SP_USUARIO.PERIODOSNOCOBROFAC
                                                         END
                        ,SP_USUARIO.NOTACREDITO        = CASE
                                                           WHEN NVL(SP_USUARIO.NOTACREDITO,0) > 0
                                                           THEN NVL(SP_USUARIO.NOTACREDITO,0) - NVL(SP_USUARIO.CREDITOABONADO,0)
                                                           ELSE SP_USUARIO.NOTACREDITO
                                                         END
                        ,MODIFIED_BY    = '''||UN_USUARIO||'''
                        ,DATE_MODIFIED  = SYSDATE';

        MI_CONDICION := '    SP_USUARIO.COMPANIA                         = '''|| UN_COMPANIA ||'''
                         AND SP_USUARIO.CICLO                            = '  || UN_CICLO;
        MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CAMPOS';
        MI_MSGERROR(1).VALOR := 'COMPANIA= '''|| UN_COMPANIA ||
                               ''' y SP_USUARIO.CICLO= '|| UN_CICLO;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTUALIZARUSUARIO,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -- PROCESO 12 -> SALDOS CREDITOS
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '16. Saldos Crédito';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    BEGIN
     BEGIN

       MI_TABLA := 'SP_TBLHIST_SALDO_CREDITO';
       MI_CAMPOS := 'COMPANIA
                    ,CICLO
                    ,CODIGORUTA
                    ,ANO
                    ,PERIODO
                    ,VALOR_SALDO
                    ,FECHA
                    ,CONSECUTIVO
                    ,USUARIOREG
                    ,VALORDES
                    ,VALORINICIAL
                    ,BANCO
                    ,CODINTERNO
                    ,HORAREG
                    ,FECHAREG
                    ,PERIODOCREADO
                    ,ANOCREADO
                    ,CODSALDOCREDITO
                    ,TIPOSALDOCREDITO
                    ,DATE_CREATED
                    ,CREATED_BY';


       MI_VALORES := 'SELECT SP_TBLHIST_SALDO_CREDITO.COMPANIA
                            ,SP_TBLHIST_SALDO_CREDITO.CICLO
                            ,SP_TBLHIST_SALDO_CREDITO.CODIGORUTA
                            ,'  ||MI_ANIOSIG||'
                            ,'''||MI_PERIODOSIG||'''
                            ,ROUND(NVL(SP_TBLHIST_SALDO_CREDITO.VALOR_SALDO,0) - NVL(SP_TBLHIST_SALDO_CREDITO.VALORDES,0))
                            ,SP_TBLHIST_SALDO_CREDITO.FECHA
                            ,'||PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    =>'SP_TBLHIST_SALDO_CREDITO',
                                                                 UN_CRITERIO =>'COMPANIA ='''||UN_COMPANIA||'''',
                                                                 UN_CAMPO    => 'CONSECUTIVO')|| '+ROWNUM
                            ,'''||UN_USUARIO||''' CIERRE
                            ,0,0
                            ,SP_TBLHIST_SALDO_CREDITO.BANCO
                            ,SP_TBLHIST_SALDO_CREDITO.CODINTERNO
                            ,SP_TBLHIST_SALDO_CREDITO.HORAREG
                            ,SP_TBLHIST_SALDO_CREDITO.FECHAREG
                            ,SP_TBLHIST_SALDO_CREDITO.PERIODOCREADO
                            ,SP_TBLHIST_SALDO_CREDITO.ANOCREADO
                            ,SP_TBLHIST_SALDO_CREDITO.CODSALDOCREDITO
                            ,SP_TBLHIST_SALDO_CREDITO.TIPOSALDOCREDITO
                            ,SYSDATE
                            ,'''||UN_USUARIO||'''
                      FROM   SP_TBLHIST_SALDO_CREDITO 
                        INNER JOIN SP_USUARIO 
                           ON      SP_TBLHIST_SALDO_CREDITO.COMPANIA   = SP_USUARIO.COMPANIA
                          AND      SP_TBLHIST_SALDO_CREDITO.CICLO      = SP_USUARIO.CICLO
                          AND      SP_TBLHIST_SALDO_CREDITO.CODIGORUTA = SP_USUARIO.CODIGORUTA
                          AND      SP_TBLHIST_SALDO_CREDITO.ANO        = SP_USUARIO.ANO
                          AND      SP_TBLHIST_SALDO_CREDITO.PERIODO    = SP_USUARIO.PERIODO
                      WHERE  SP_TBLHIST_SALDO_CREDITO.COMPANIA = '''||UN_COMPANIA||'''
                        AND  SP_TBLHIST_SALDO_CREDITO.CICLO    = '  ||UN_CICLO||'
                        AND  SP_TBLHIST_SALDO_CREDITO.ANO      = '  ||MI_ANIO||'
                        AND  SP_TBLHIST_SALDO_CREDITO.PERIODO  = '''||MI_PERIODO||'''
                        AND  SP_USUARIO.NOTACREDITO            > 0
                        AND  (NVL(SP_TBLHIST_SALDO_CREDITO.VALOR_SALDO,0) 
                            - NVL(SP_TBLHIST_SALDO_CREDITO.VALORDES,0)) > 0';
       PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA, 
                                             UN_ACCION  => 'IS', 
                                             UN_CAMPOS  => MI_CAMPOS, 
                                             UN_VALORES => MI_VALORES);    
       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
         RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
     END;
     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN               
       PCK_ERR_MSG.RAISE_WITH_MSG(
         UN_EXC_COD   => SQLCODE,
         UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_TBLHISTSALDOCREDI
       ); 
    END;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    -- ACTUALIZAR USUARIOS SUPENDIDOS QUE TUVIERON PAGO
    -- PROCESO 13 -> DATOS DE USUARIO
    IF MI_PARACTIVASUSP <> 0 THEN
      MI_AFECTADOS := 0;
      -----------------------------------------------------------------------------------------------------
      MI_ETAPA := '17.0 Datos de usuario Suspendidos';
      MI_HORAINICIO := SYSDATE;
      -----------------------------------------------------------------------------------------------------
      BEGIN
        BEGIN
          MI_TABLA     := 'SP_USUARIO';
          MI_CAMPOS    := 'SP_USUARIO.ESTADO = ''A''
                           ,MODIFIED_BY    = '''||UN_USUARIO||'''
                           ,DATE_MODIFIED  = SYSDATE  ';

          MI_CONDICION := '    SP_USUARIO.COMPANIA        = '''|| UN_COMPANIA ||'''
                           AND SP_USUARIO.CICLO           = '  || UN_CICLO ||'
                           AND SP_USUARIO.INDPREPARADO    = 0 
                           AND SP_USUARIO.ESTADO          = ''S'' 
                           AND SP_USUARIO.BANCOPERPROCESO <> NULL';
          MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA,
                                        UN_ACCION    => 'M',
                                        UN_CAMPOS    => MI_CAMPOS,
                                        UN_CONDICION => MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CAMPOS';
          MI_MSGERROR(1).VALOR := 'ESTADO';
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTUALIZARUSUARIO,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END;
      MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    END IF;
    MI_AFECTADOS := 0;
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '17. Datos de usuario';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------

    BEGIN
      BEGIN
        MI_TABLA    := 'SP_USUARIO';
        MI_MERGEUSING := 'SELECT SP_USUARIO.COMPANIA,
                                 SP_USUARIO.CICLO,
                                 SP_USUARIO.CODIGORUTA,
                                 0 INDFINANINICIAL,
                                 0 FACTURA,
                                 0 INDCALCULADO,
                                 -1 INDPREPARADO,
                                 TOTFACTURAPER2,
                                 TOTFACTURAPERANTERIOR,
                                 TOTFACTURAPERACTUAL,
                                  0 TOTFACTURAPERACTUALNUE,
                                 BANCOPERPROCESO,
                                 FECHAPAGOPERPROCESO,
                                 PAQUETEPAGOPERPROCESO,
                                 RECAUDADOPROCESO,
                                 NULL BANCOPERPROCESONULL,
                                 NULL FECHAPAGOPERPROCESONULL,
                                 0 PAQUETEPAGOPERPROCESONUEV,
                                 0 RECAUDADOPROCESONUEV,
                                 NULL FECHALIMITE,
                                '''||MI_PERIODOSIG||''' PERIODO,
                                '||MI_ANIOSIG||' ANO,
                                0 INDFACTURADO,
                                0 INDRECAUDADO,
                                 CASE WHEN SP_USUARIO.INDBANCOS = ''C''
                                  THEN
                                    ''B''
                                  ELSE
                                      SP_USUARIO.INDBANCOS
                                  END INDBANCOS,
                                  CASE WHEN SP_USUARIO.TIPOCALCULO   = ''P'' AND SP_USUARIO.INDPREPARADO = 0
                                  THEN 
                                      SP_USUARIO.PERIODOSINLECTURA + 1
                                  ELSE 0
                                  END PERIODOSINLECTURA,';

        IF SUBSTR(MI_DESCUENTAACUM,1,1) = 'S' THEN
          MI_MERGEUSING := MI_MERGEUSING ||
                       'CASE WHEN SP_USUARIO.TIPOCALCULO        = ''P'' AND SP_USUARIO.PERIODOSINLECTURA <= '||MI_PERIODODESCUENTAACUM||'
                       THEN
                            SP_USUARIO.ACUMULADO + SP_USUARIO.CONSUMO
                       ELSE 
                            SP_USUARIO.ACUMULADO
                       END ACUMULADO , ';
        ELSE
          MI_MERGEUSING := MI_MERGEUSING ||
                      'CASE WHEN SP_USUARIO.TIPOCALCULO <> ''P''
                       THEN
                          0
                      ELSE 
                          SP_USUARIO.ACUMULADO
                     END ACUMULADO , ';
        END IF;
        MI_MERGEUSING := MI_MERGEUSING ||
                      '  0 LECTURAAFORO ,
                       LECTURAAFORO LECTURA,
                       0 LECTURAINICIAL';
        CASE 
          WHEN MI_FRECUENCIAFAC = 'M' THEN
            MI_MERGEUSING := MI_MERGEUSING ||
                          ', TO_NUMBER((NVL(SP_USUARIO.CONSUMO6, 0) 
                                                             + NVL(SP_USUARIO.CONSUMO5, 0) 
                                                             + NVL(SP_USUARIO.CONSUMO4, 0) 
                                                             + NVL(SP_USUARIO.CONSUMO3, 0) 
                                                             + NVL(SP_USUARIO.CONSUMO2, 0) 
                                                             + NVL(SP_USUARIO.CONSUMO1, 0)) / 6) CONSUMOPROM';
          WHEN MI_FRECUENCIAFAC = 'B' OR MI_FRECUENCIAFAC = 'C' THEN 
            MI_MERGEUSING := MI_MERGEUSING ||
                        ',TO_NUMBER((NVL(SP_USUARIO.CONSUMO3, 0) 
                                                             + NVL(SP_USUARIO.CONSUMO2, 0) 
                                                             + NVL(SP_USUARIO.CONSUMO1, 0)) / 3) CONSUMOPROM';
          WHEN MI_FRECUENCIAFAC = 'T' THEN
            MI_MERGEUSING := MI_MERGEUSING ||
                         ' , TO_NUMBER((NVL(SP_USUARIO.CONSUMO2, 0) 
                                                             + NVL(SP_USUARIO.CONSUMO1, 0)) / 2) CONSUMOPROM';
        END CASE;
        MI_MERGEUSING :=MI_MERGEUSING || '  FROM SP_USUARIO  
                                   INNER JOIN SP_TARIFAS
                                      ON SP_USUARIO.COMPANIA      = SP_TARIFAS.COMPANIA
                                     AND '||MI_ANIOSIG||'         = SP_TARIFAS.ANO
                                     AND '''||MI_PERIODOSIG||'''  = SP_TARIFAS.PERIODO
                                     AND SP_USUARIO.USO           = SP_TARIFAS.USO
                                     AND SP_USUARIO.ESTRATO       = SP_TARIFAS.ESTRATO
                             WHERE SP_USUARIO.COMPANIA            = '''|| UN_COMPANIA ||'''
                               AND SP_USUARIO.CICLO               = '  || UN_CICLO ||'
                               AND (SP_USUARIO.ANOENTRADANUEVOUSUARIO || SP_USUARIO.PERENTRADANUEVOUSUARIO) NOT IN ('''||MI_ANIO||MI_PERIODO||''')
                               AND  SP_USUARIO.INDPREPARADO   = 0';

        MI_MERGEENLACE :=' TABLA.COMPANIA    = VISTA.COMPANIA
                       AND TABLA.CICLO       = VISTA.CICLO
                       AND TABLA.CODIGORUTA  = VISTA.CODIGORUTA';

        MI_MERGEEXISTE :='UPDATE   SET  TABLA.INDFINANINICIAL      = VISTA.INDFINANINICIAL ,
                                        TABLA.FACTURA                = VISTA.FACTURA ,
                                        TABLA.INDCALCULADO           = VISTA.INDCALCULADO ,
                                        TABLA.INDPREPARADO           = VISTA.INDPREPARADO ,
                                        TABLA.TOTFACTURAPER3         = VISTA.TOTFACTURAPER2 ,
                                        TABLA.TOTFACTURAPER2         = VISTA.TOTFACTURAPERANTERIOR ,
                                        TABLA.TOTFACTURAPERANTERIOR  = VISTA.TOTFACTURAPERACTUAL ,
                                        TABLA.TOTFACTURAPERACTUAL    = VISTA.TOTFACTURAPERACTUALNUE ,
                                        TABLA.BANCOPERANTERIOR       = VISTA.BANCOPERPROCESO ,
                                        TABLA.FECHAPAGOPERANTERIOR   = VISTA.FECHAPAGOPERPROCESO ,
                                        TABLA.PAQUETEPAGOPERANTERIOR = VISTA.PAQUETEPAGOPERPROCESO ,
                                        TABLA.RECAUDADOANTERIOR      = VISTA.RECAUDADOPROCESO ,
                                        TABLA.BANCOPERPROCESO        = VISTA.BANCOPERPROCESONULL ,
                                        TABLA.FECHAPAGOPERPROCESO    = VISTA.FECHAPAGOPERPROCESONULL ,
                                        TABLA.PAQUETEPAGOPERPROCESO  = VISTA.PAQUETEPAGOPERPROCESONUEV ,
                                        TABLA.RECAUDADOPROCESO       = VISTA.RECAUDADOPROCESONUEV ,
                                        TABLA.FECHALIMITE            = VISTA.FECHALIMITE ,
                                        TABLA.PERIODO                = VISTA.PERIODO ,
                                        TABLA.ANO                    = VISTA.ANO ,
                                        TABLA.INDFACTURADO           = VISTA.INDFACTURADO ,
                                        TABLA.INDRECAUDADO           = VISTA.INDRECAUDADO ,
                                        TABLA.INDBANCOS              = VISTA.INDBANCOS ,
                                        TABLA.PERIODOSINLECTURA      = VISTA.PERIODOSINLECTURA ,
                                        TABLA.ACUMULADO              = VISTA.ACUMULADO ,
                                        TABLA.LECTURAAFORO           = VISTA.LECTURAAFORO ,
                                        TABLA.LECTURA                = VISTA.LECTURA ,
                                        TABLA.LECTURAINICIAL         = VISTA.LECTURAINICIAL ,
                                        TABLA.CONSUMOPROM            = VISTA.CONSUMOPROM,
                                        TABLA.MODIFIED_BY            = '''||UN_USUARIO||''',
                                        TABLA.DATE_MODIFIED          = SYSDATE  ';               

        MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA, 
                                          UN_ACCION      => 'MM', 
                                          UN_MERGEUSING  => MI_MERGEUSING,
                                          UN_MERGEENLACE => MI_MERGEENLACE,
                                          UN_MERGEEXISTE => MI_MERGEEXISTE);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CAMPOS';
        MI_MSGERROR(1).VALOR := 'ESTADO';
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTUALIZARUSUARIO,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -- PROCESO 14 -> INDICADOR DESHABILITADO DESDE PROBLEMAS DE AFORO
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '18. Usuarios habitados y deshabitados';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    IF MI_ACTIVADESHABI <> 0 OR MI_ACTIVAHABI <> 0 THEN
      BEGIN
        BEGIN
          MI_TABLA       := 'SP_USUARIO';
          MI_MERGEUSING  := 'SELECT DISTINCT SP_USUARIO.COMPANIA
                                   ,SP_USUARIO.CICLO
                                   ,SP_USUARIO.CODIGORUTA
                                   ,SP_PROBLEMA.ACTIVA_DESHABITADO
                                   ,SP_PROBLEMA.ACTIVA_HABITADO
                             FROM   SP_USUARIO
                               INNER JOIN SP_USUARIO_PROBLEMA
                                  ON      SP_USUARIO.COMPANIA   = SP_USUARIO_PROBLEMA.COMPANIA
                                 AND      SP_USUARIO.CICLO      = SP_USUARIO_PROBLEMA.CICLO
                                 AND      SP_USUARIO.CODIGORUTA = SP_USUARIO_PROBLEMA.CODIGORUTA
                               INNER JOIN SP_PROBLEMA
                                  ON      SP_USUARIO_PROBLEMA.COMPANIA = SP_PROBLEMA.COMPANIA
                                 AND      SP_USUARIO_PROBLEMA.PROBLEMA = SP_PROBLEMA.CODIGO
                                 AND      SP_USUARIO_PROBLEMA.CLASE    = SP_PROBLEMA.CLASEPROBLEMA
                             WHERE  SP_USUARIO_PROBLEMA.COMPANIA    = '''||UN_COMPANIA||'''
                               AND  SP_USUARIO_PROBLEMA.CICLO       =   '||UN_CICLO||'
                               AND  SP_USUARIO_PROBLEMA.ANO         = '||MI_ANIO||'
                               AND  SP_USUARIO_PROBLEMA.PERIODO     = '''||MI_PERIODO||'''
                               AND  (SP_PROBLEMA.ACTIVA_DESHABITADO NOT IN (0) 
                                OR  SP_PROBLEMA.ACTIVA_HABITADO NOT IN (0))';
          MI_MERGEENLACE := '    TABLA.COMPANIA   = VISTA.COMPANIA 
                             AND TABLA.CICLO      = VISTA.CICLO
                             AND TABLA.CODIGORUTA = VISTA.CODIGORUTA';
          MI_MERGEEXISTE := 'UPDATE 
                               SET TABLA.INDDESHABITADO = CASE
                                                            WHEN VISTA.ACTIVA_DESHABITADO <> 0
                                                            THEN -1
                                                            ELSE (CASE
                                                                    WHEN VISTA.ACTIVA_HABITADO <> 0
                                                                    THEN 0
                                                                    ELSE TABLA.INDDESHABITADO
                                                                  END)
                                                          END
                                    ,MODIFIED_BY    = '''||UN_USUARIO||'''
                                    ,DATE_MODIFIED  = SYSDATE  ';

          MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA, 
                                            UN_ACCION      => 'MM', 
                                            UN_MERGEUSING  => MI_MERGEUSING, 
                                            UN_MERGEENLACE => MI_MERGEENLACE,
                                            UN_MERGEEXISTE => MI_MERGEEXISTE);    
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CAMPOS';
          MI_MSGERROR(1).VALOR := 'INDDESHABITADO';
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD   => SQLCODE,
            UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_ACTUALIZARUSUARIO,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END; 
    END IF;  
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -- PROCESO 15 -> INICIALIZA SUBSIDIOS, FECHAS, CONSUMOS Y TOTALES
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '19. Limpia Subsidios';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    BEGIN
      BEGIN
        MI_TABLA     := 'SP_USUARIO';
        MI_CAMPOS    := 'SP_USUARIO.CARGOFIJO              = 0
                        ,SP_USUARIO.SUBFIJO                = 0
                        ,SP_USUARIO.SOBREFIJO              = 0
                        ,SP_USUARIO.CONSUMOBASICO          = 0
                        ,SP_USUARIO.CONSUMOCOMPLEMENTARIO  = 0
                        ,SP_USUARIO.CONSUMOSUNTUARIO       = 0
                        ,SP_USUARIO.VALORBASICO            = 0
                        ,SP_USUARIO.VALORCOMPLEMENTARIO    = 0
                        ,SP_USUARIO.VALORSUNTUARIO         = 0
                        ,SP_USUARIO.SUBCONSUMOAC           = 0
                        ,SP_USUARIO.SOBRECONSUMOAC         = 0
                        ,SP_USUARIO.ACSINMEDICION          = 0
                        ,SP_USUARIO.SUBSINMEDICION         = 0
                        ,SP_USUARIO.SOBRESINMEDICION       = 0
                        ,SP_USUARIO.CARGOFIJOAL            = 0
                        ,SP_USUARIO.SUBFIJOALC             = 0
                        ,SP_USUARIO.SOBREFIJOALC           = 0
                        ,SP_USUARIO.VALORALCBASICO         = 0
                        ,SP_USUARIO.VALORALCCOMPLEMENTARIO = 0
                        ,SP_USUARIO.VALORALCSUNTUARIO      = 0
                        ,SP_USUARIO.SUBCONSUMOAL           = 0
                        ,SP_USUARIO.SOBRECONSUMOAL         = 0
                        ,SP_USUARIO.ALSINMEDICION          = 0
                        ,SP_USUARIO.SUBALCSINMEDICION      = 0
                        ,SP_USUARIO.SOBREALCSINMEDICION    = 0
                        ,SP_USUARIO.VCONSUMOALC            = 0
                        ,SP_USUARIO.TOTALASEO              = 0
                        ,SP_USUARIO.TOTALALCANTARILLADO    = 0
                        ,SP_USUARIO.TOTALACUEDUCTO         = 0
                        ,SP_USUARIO.TOTALALUMBRADO         = 0
                        ,SP_USUARIO.TOTFACTURAASEO         = 0
                        ,SP_USUARIO.VASEOUNICO             = 0
                        ,SP_USUARIO.VASEODOMICILIARIO      = 0
                        ,SP_USUARIO.VASEOBARRIDO           = 0
                        ,SP_USUARIO.VASEOCONSUMO           = 0
                        ,SP_USUARIO.SUBASEO                = 0
                        ,SP_USUARIO.SUBACUEDUCTO           = 0
                        ,SP_USUARIO.SUBALCANTARILLADO      = 0
                        ,SP_USUARIO.SOBREASEO              = 0
                        ,SP_USUARIO.SOBREACUEDUCTO         = 0
                        ,SP_USUARIO.SOBREALCANTARILLADO    = 0
                        ,SP_USUARIO.SUBSIDIO               = 0
                        ,SP_USUARIO.SOBREPRECIO            = 0
                        ,SP_USUARIO.FECHALECANTERIOR       = CASE
                                                               WHEN SP_USUARIO.ESTADO = ''S'' 
                                                                 OR SP_USUARIO.ESTADO = ''C''
                                                               THEN NULL
                                                               ELSE SP_USUARIO.FECHALECANTERIOR
                                                             END
                        ,SP_USUARIO.FECHALECACTUAL         = CASE
                                                               WHEN SP_USUARIO.ESTADO = ''S'' 
                                                                 OR SP_USUARIO.ESTADO = ''C''
                                                               THEN NULL
                                                               ELSE SP_USUARIO.FECHALECACTUAL
                                                             END
                       ,MODIFIED_BY    = '''||UN_USUARIO||'''
                       ,DATE_MODIFIED  = SYSDATE  ';

        IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                 UN_NOMBRE    => 'ACTIVAR USUARIOS PAGADOS EN EL CIERRE',
                                 UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                 UN_FECHA_PAR => SYSDATE) = 'SI' THEN
          MI_CAMPOS := MI_CAMPOS ||
                       ',SP_USUARIO.ESTADO = CASE
                                               WHEN NVL(SP_USUARIO.BANCOPERPROCESO, '' '') <> '' '' 
                                                 AND (SP_USUARIO.ESTADO = ''S'' OR SP_USUARIO.ESTADO = ''C'')
                                               THEN ''A''
                                               ELSE SP_USUARIO.ESTADO
                                             END';
        END IF;
        MI_CONDICION := '    SP_USUARIO.COMPANIA        = '''|| UN_COMPANIA ||'''
                         AND SP_USUARIO.CICLO           = '  || UN_CICLO ||'
                         AND SP_USUARIO.INDPREPARADO    = 0';
        MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_MERGEUSUARIO
        );
    END;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -- PROCESO 16 -> USUARIOS NUEVOS
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '20. Usuarios nuevos';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    BEGIN
      BEGIN
        MI_TABLA     := 'SP_USUARIO';
        MI_CAMPOS    := 'SP_USUARIO.INDFINANINICIAL        = 0
                        ,SP_USUARIO.FACTURA                = 0
                        ,SP_USUARIO.INDCALCULADO           = 0
                        ,SP_USUARIO.INDPREPARADO           = -1
                        ,SP_USUARIO.INDMEDCAMBIADO         = 0
                        ,SP_USUARIO.LECTURA6               = 0
                        ,SP_USUARIO.LECTURA5               = 0
                        ,SP_USUARIO.LECTURA4               = 0
                        ,SP_USUARIO.LECTURA3               = 0
                        ,SP_USUARIO.LECTURA2               = 0
                        ,SP_USUARIO.LECTURA1               = SP_USUARIO.LECTURA
                        ,SP_USUARIO.LECTURA                = SP_USUARIO.LECTURAAFORO
                        ,SP_USUARIO.LECTURAAFORO           = 0
                        ,SP_USUARIO.FECHALECACTUAL         = SP_USUARIO.FECHALECTURAAFORO
                        ,SP_USUARIO.CONSUMO6               = 0
                        ,SP_USUARIO.CONSUMO5               = 0
                        ,SP_USUARIO.CONSUMO4               = 0
                        ,SP_USUARIO.CONSUMO3               = 0
                        ,SP_USUARIO.CONSUMO2               = 0
                        ,SP_USUARIO.CONSUMO1               = 0
                        ,SP_USUARIO.CONSUMO                = 0
                        ,SP_USUARIO.LecturaCritica         = 0
                        ,SP_USUARIO.FECHALECTURAAFORO      = NULL
                        ,SP_USUARIO.VALCONSUMO6            = 0
                        ,SP_USUARIO.VALCONSUMO5            = 0
                        ,SP_USUARIO.VALCONSUMO4            = 0
                        ,SP_USUARIO.VALCONSUMO3            = 0
                        ,SP_USUARIO.VALCONSUMO2            = 0
                        ,SP_USUARIO.VALCONSUMO1            = 0
                        ,SP_USUARIO.VALCONSUMO             = 0
                        ,SP_USUARIO.VALCONSUMOPROM         = 0
                        ,SP_USUARIO.NOFECHAPAGOPERANTERIOR = SP_USUARIO.NOFECHAPAGOPERPROCESO
                        ,SP_USUARIO.NOFECHAPAGOPERPROCESO  = NULL
                        ,SP_USUARIO.PERIODOSATRASO         = CASE
                                                               WHEN NVL(SP_USUARIO.BANCOPERPROCESO,'' '') = '' ''
                                                               THEN 1
                                                               ELSE 0
                                                             END
                        ,SP_USUARIO.PERIODOSNOCOBROFAC     = CASE
                                                               WHEN SP_USUARIO.PERIODOSNOCOBROFAC > 0
                                                               THEN SP_USUARIO.PERIODOSNOCOBROFAC - 1
                                                               ELSE 0
                                                             END
                        ,SP_USUARIO.TOTFACTURAPER3         = 0
                        ,SP_USUARIO.TOTFACTURAPER2         = 0
                        ,SP_USUARIO.TOTFACTURAPERANTERIOR  = SP_USUARIO.TOTFACTURAPERACTUAL
                        ,SP_USUARIO.TOTFACTURAPERACTUAL    = 0
                        ,SP_USUARIO.BANCOPERANTERIOR       = SP_USUARIO.BANCOPERPROCESO
                        ,SP_USUARIO.FECHAPAGOPERANTERIOR   = SP_USUARIO.FECHAPAGOPERPROCESO
                        ,SP_USUARIO.PAQUETEPAGOPERANTERIOR = SP_USUARIO.PAQUETEPAGOPERPROCESO
                        ,SP_USUARIO.RECAUDADOANTERIOR      = SP_USUARIO.RECAUDADOPROCESO
                        ,SP_USUARIO.BANCOPERPROCESO        = NULL
                        ,SP_USUARIO.FECHAPAGOPERPROCESO    = NULL
                        ,SP_USUARIO.PAQUETEPAGOPERPROCESO  = 0
                        ,SP_USUARIO.RECAUDADOPROCESO       = 0
                        ,SP_USUARIO.FECHALIMITE            = NULL
                        ,SP_USUARIO.PERIODO                = '''||MI_PERIODOSIG||'''
                        ,SP_USUARIO.ANO                    = '||MI_ANIOSIG||'
                        ,SP_USUARIO.INDFACTURADO           = 0
                        ,SP_USUARIO.INDRECAUDADO           = 0
                        ,MODIFIED_BY    = '''||UN_USUARIO||'''
                        ,DATE_MODIFIED  = SYSDATE  ';

        MI_CONDICION := '    SP_USUARIO.COMPANIA                = '''|| UN_COMPANIA ||'''
                         AND SP_USUARIO.CICLO                   = '  || UN_CICLO ||'
                         AND SP_USUARIO.ANOENTRADANUEVOUSUARIO  = '  || MI_ANIO ||'
                         AND SP_USUARIO.PERENTRADANUEVOUSUARIO  = '''|| MI_PERIODO ||'''';
        MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_MERGEUSUARIO
        );
    END;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -- PROCESO 17 -> USUARIO CON DATOS DE ASEO E HISTORICOS
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '21. Datos adicionales de usuario';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    BEGIN
      BEGIN
        MI_TABLA     := 'SP_USUARIO';
        MI_CAMPOS    := 'SP_USUARIO.VALASEO6               = SP_USUARIO.VALASEO5
                        ,SP_USUARIO.VALASEO5               = SP_USUARIO.VALASEO4
                        ,SP_USUARIO.VALASEO4               = SP_USUARIO.VALASEO3
                        ,SP_USUARIO.VALASEO3               = SP_USUARIO.VALASEO2
                        ,SP_USUARIO.VALASEO2               = SP_USUARIO.VALASEO1
                        ,SP_USUARIO.VALASEO1               = SP_USUARIO.TOTALASEO
                        ,SP_USUARIO.PESO6                  = SP_USUARIO.PESO5
                        ,SP_USUARIO.PESO5                  = SP_USUARIO.PESO4
                        ,SP_USUARIO.PESO4                  = SP_USUARIO.PESO3
                        ,SP_USUARIO.PESO3                  = SP_USUARIO.PESO2
                        ,SP_USUARIO.PESO2                  = SP_USUARIO.PESO1
                        ,SP_USUARIO.PESO1                  = SP_USUARIO.PESOASEO
                        ,SP_USUARIO.FACTURAAC3             = SP_USUARIO.FACTURAAC2
                        ,SP_USUARIO.FACTURAAC2             = SP_USUARIO.FACTURAAC1
                        ,SP_USUARIO.FACTURAAC1             = SP_USUARIO.TOTALACUEDUCTO
                        ,SP_USUARIO.FACTURAALC3            = SP_USUARIO.FACTURAALC2
                        ,SP_USUARIO.FACTURAALC2            = SP_USUARIO.FACTURAALC1
                        ,SP_USUARIO.FACTURAALC1            = SP_USUARIO.TOTALALCANTARILLADO
                        ,SP_USUARIO.FACTURAASE3            = SP_USUARIO.FACTURAASE2
                        ,SP_USUARIO.FACTURAASE2            = SP_USUARIO.FACTURAASE1
                        ,SP_USUARIO.FACTURAASE1            = SP_USUARIO.TOTALASEO
                        ,SP_USUARIO.VDESHABITADO           = 0 
                        ,SP_USUARIO.VTRAMOEXCEDENTE        = 0
                        ,SP_USUARIO.SUBBARR_LIM            = 0
                        ,SP_USUARIO.SUBDISP_FINAL          = 0
                        ,SP_USUARIO.SUBMANEJO_REC          = 0
                        ,SP_USUARIO.SUBRECOLECCION         = 0
                        ,SP_USUARIO.SUBTRAMO               = 0
                        ,SP_USUARIO.SUBDESHABITADO         = 0
                        ,SP_USUARIO.SOBBARR_LIM            = 0
                        ,SP_USUARIO.SOBDISP_FINAL          = 0
                        ,SP_USUARIO.SOBMANEJO_REC          = 0
                        ,SP_USUARIO.SOBRECOLECCION         = 0
                        ,SP_USUARIO.SOBTRAMO               = 0 
                        ,SP_USUARIO.SOBDESHABITADO         = 0
                        ,SP_USUARIO.ACUMULADOPER           = 0 
                        ,SP_USUARIO.CALCULODESH            = 0
                        ,SP_USUARIO.DESCONTADO             = 0 
                        ,SP_USUARIO.SUB_TASAAMBIENTALAC    = 0
                        ,SP_USUARIO.SOBRE_TASAAMBIENTALAC  = 0
                        ,SP_USUARIO.SUB_TASAAMBIENTALALC   = 0
                        ,SP_USUARIO.SOBRE_TASAAMBIENTALALC = 0
                        ,MODIFIED_BY                       = '''||UN_USUARIO||'''
                        ,DATE_MODIFIED                     = SYSDATE  ';

        IF MI_CONSUMOMANUAL = 'SI' THEN
          MI_CAMPOS := MI_CAMPOS ||
                       ',SP_USUARIO.AFOROCONSMANUAL        = 0
                        ,SP_USUARIO.CONSUMOACUMANUAL       = 0
                        ,SP_USUARIO.CONSUMOALCMANUAL       = 0
                        ,SP_USUARIO.CONSUMOMANUAL          = CASE
                                                               WHEN SP_USUARIO.AFOROCONSMANUAL <> 0
                                                               THEN SP_USUARIO.AFOROCONSMANUAL
                                                               ELSE 0
                                                             END
                        ,SP_USUARIO.CONSUMOACU             = CASE
                                                               WHEN SP_USUARIO.AFOROCONSMANUAL <> 0 
                                                               THEN SP_USUARIO.CONSUMOACUMANUAL
                                                               ELSE 0
                                                             END
                        ,SP_USUARIO.CONSUMOALC             = CASE
                                                               WHEN SP_USUARIO.AFOROCONSMANUAL <> 0
                                                               THEN SP_USUARIO.CONSUMOALCMANUAL
                                                               ELSE 0
                                                             END';
        END IF;
        IF MI_NUEVACRITICA <> 0 THEN
          -- SE MANTIENE EL INDICADOR EN EL AFORADO PUES EL CIERRE DE LAS DESVIACIONES SE REALIZA DENTRO DEL PROCESO DE ADMINISTRACIÓN DE DESVIACIONES
          MI_CAMPOS := MI_CAMPOS ||
                      ',SP_USUARIO.DESVIOSIGNIFICATIVO   = SP_USUARIO.DESVIACIONAFORO
                       ,SP_USUARIO.METROSDESVIACION      = SP_USUARIO.METROSDESVIACIONAFORO
                       ,SP_USUARIO.METROSDESVIACIONAFORO = 0';
        END IF;
        IF MI_DESCN <> 0 THEN
          MI_CAMPOS := MI_CAMPOS ||
                      ',SP_USUARIO.INCENTIVO = SP_USUARIO.AFOROINCENTIVO';
        END IF;
        MI_CAMPOS := MI_CAMPOS ||
                     ',SP_USUARIO.SUBCCS_720        = 0
                      ,SP_USUARIO.SOBCCS_720        = 0
                      ,SP_USUARIO.VALASEOCBLS_720   = 0
                      ,SP_USUARIO.SUBCBLS_720       = 0
                      ,SP_USUARIO.SOBCBLS_720       = 0
                      ,SP_USUARIO.VALASEOCLUS_720   = 0
                      ,SP_USUARIO.SUBCLUS_720       = 0
                      ,SP_USUARIO.SOBCLUS_720       = 0
                      ,SP_USUARIO.VALASEOCRT_720    = 0
                      ,SP_USUARIO.SUBCRT_720        = 0
                      ,SP_USUARIO.SOBCRT_720        = 0
                      ,SP_USUARIO.VALASEOCDF_720    = 0
                      ,SP_USUARIO.SUBCDF_720        = 0
                      ,SP_USUARIO.SOBCDF_720        = 0 
                      ,SP_USUARIO.VALASEOCTL_720    = 0  
                      ,SP_USUARIO.SUBCTL_720        = 0
                      ,SP_USUARIO.SOBCTL_720        = 0
                      ,SP_USUARIO.VALASEOVBA_720    = 0
                      ,SP_USUARIO.SUBVBA_720        = 0 
                      ,SP_USUARIO.SOBVBA_720        = 0';
        IF MI_MANEJA720 <> 0 THEN
          MI_CAMPOS := MI_CAMPOS ||
                       ',SP_USUARIO.TAFNA_720         = SP_USUARIO.TAFNA_AFORO_720
                        ,SP_USUARIO.TAFNA_AFORO_720   = 0
                        ,SP_USUARIO.TAFA_720          = SP_USUARIO.TAFA_AFORO_720
                        ,SP_USUARIO.TAFA_AFORO_720    = 0';
        ELSE
          MI_CAMPOS := MI_CAMPOS ||
                       ',SP_USUARIO.TAFNA_720         = 0
                        ,SP_USUARIO.TAFNA_AFORO_720   = 0
                        ,SP_USUARIO.TAFA_720          = 0
                        ,SP_USUARIO.TAFA_AFORO_720    = 0';
        END IF;
        MI_CONDICION := '    SP_USUARIO.COMPANIA                = '''|| UN_COMPANIA ||'''
                         AND SP_USUARIO.CICLO                   = '  || UN_CICLO ||'';
        MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_MERGEUSUARIO
        );
    END;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -- FIN AJUSTE DEL CIERRE.
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '22. Nueva critica - auditoria de desviaciones';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    -- SE HACE LA AUDITORÍA DESPUÉS DE CERRAR EL USUARIO
    <<AUDITORIAUSUARIO>>
    FOR MI_RSCOMP IN (SELECT SP_USUARIO.ANO
                            ,SP_USUARIO.PERIODO
                            ,SP_USUARIO.CODIGOINTERNO
                            ,SP_USUARIO.DESVIOSIGNIFICATIVO
                            ,SP_USUARIO.DESVIACIONAFORO
                      FROM   SP_USUARIO 
                      WHERE  SP_USUARIO.COMPANIA = UN_COMPANIA
                        AND  SP_USUARIO.CICLO    = UN_CICLO 
                        AND  SP_USUARIO.ANO      = MI_ANIO 
                        AND  SP_USUARIO.PERIODO  = MI_PERIODO)
    LOOP
      MI_AFECTADOS := MI_AFECTADOS + 1;
      IF MI_NUEVACRITICA <> 0 THEN
        IF MI_RSCOMP.DESVIOSIGNIFICATIVO < MI_RSCOMP.DESVIACIONAFORO THEN
          PCK_SERVICIOS_PUBLICOS_COM6.PR_AUDITORIADESVIO(UN_COMPANIA    => UN_COMPANIA,
                                                         UN_USUARIO     => UN_USUARIO,
                                                         UN_PROCESO     => 'Activó',
                                                         UN_ANIO        => MI_RSCOMP.ANO,
                                                         UN_PERIODO     => MI_RSCOMP.PERIODO,
                                                         UN_CICLO       => UN_CICLO,
                                                         UN_CODINTERNO  => MI_RSCOMP.CODIGOINTERNO,
                                                         UN_DESCRIPCION => 'Activó Desvío Significativo en el cierre');
        ELSIF MI_RSCOMP.DESVIOSIGNIFICATIVO > MI_RSCOMP.DESVIACIONAFORO THEN
          PCK_SERVICIOS_PUBLICOS_COM6.PR_AUDITORIADESVIO(UN_COMPANIA    => UN_COMPANIA,
                                                         UN_USUARIO     => UN_USUARIO,
                                                         UN_PROCESO     => 'Desactivó',
                                                         UN_ANIO        => MI_RSCOMP.ANO,
                                                         UN_PERIODO     => MI_RSCOMP.PERIODO,
                                                         UN_CICLO       => UN_CICLO,
                                                         UN_CODINTERNO  => MI_RSCOMP.CODIGOINTERNO,
                                                         UN_DESCRIPCION => 'Desactivó Desvío Significativo en el cierre');
        END IF;
      END IF;
    END LOOP AUDITORIAUSUARIO;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '23';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    IF MI_PLAZOSUSPENSION = 'SI' THEN
      BEGIN
        BEGIN
          MI_TABLA     := 'SP_USUARIO';
          MI_CAMPOS    := 'SP_USUARIO.FECHAACTASUSPEN = NULL
                          ,MODIFIED_BY    = '''||UN_USUARIO||'''
                          ,DATE_MODIFIED  = SYSDATE  ';

          MI_CONDICION := '    SP_USUARIO.COMPANIA        = '''|| UN_COMPANIA ||'''
                           AND SP_USUARIO.CICLO           = '  || UN_CICLO ||'
                           AND SP_USUARIO.FECHAACTASUSPEN IS NOT NULL';
          MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA,
                                        UN_ACCION    => 'M',
                                        UN_CAMPOS    => MI_CAMPOS,
                                        UN_CONDICION => MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CAMPOS';
          MI_MSGERROR(1).VALOR := 'FECHAACTASUSPEN';
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD   => SQLCODE,
            UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_ACTUALIZARUSUARIO,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END;
    END IF;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '24';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                 UN_NOMBRE    => 'MANEJO DISPOSICION FINAL DETALLADOS',
                                 UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                 UN_FECHA_PAR => SYSDATE),'NO') = 'SI' THEN
      PCK_SERVICIOS_PUBLICOS_COM6.PR_SUMPESOASEO(UN_COMPANIA => UN_COMPANIA,  
                                                 UN_CICLO    => UN_CICLO,
                                                 UN_ANIO     => MI_ANIOSIG,
                                                 UN_PERIODO  => MI_PERIODOSIG);
    END IF;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '25';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    BEGIN
      BEGIN
        MI_TABLA     := 'SP_USUARIO';
        MI_MERGEUSING := 'SELECT SP_USUARIO.COMPANIA,
                                 SP_USUARIO.CICLO,
                                 SP_USUARIO.CODIGORUTA,
                                 '||MI_ANIOSIG||' ANO,
                                  '''||MI_PERIODOSIG||'''  PERIODO
                            FROM SP_USUARIO
                           INNER JOIN SP_TARIFAS 
                              ON SP_USUARIO.COMPANIA = SP_TARIFAS.COMPANIA
                             AND '||MI_ANIOSIG||'     = SP_TARIFAS.ANO
                             AND '''||MI_PERIODOSIG||'''   = SP_TARIFAS.PERIODO
                             AND SP_USUARIO.USO      = SP_TARIFAS.USO
                             AND SP_USUARIO.ESTRATO  = SP_TARIFAS.ESTRATO
                           WHERE SP_USUARIO.COMPANIA =  '''|| UN_COMPANIA ||'''
                             AND SP_USUARIO.CICLO   = '  || UN_CICLO ||'   
                             AND SP_USUARIO.PERIODO = '''||MI_PERIODO||'''
                             AND SP_USUARIO.ANO     = '||MI_ANIO;

        MI_MERGEENLACE:='  TABLA.COMPANIA    = VISTA.COMPANIA
                       AND TABLA.CICLO       = VISTA.CICLO
                       AND TABLA.CODIGORUTA  = VISTA.CODIGORUTA';

       MI_MERGEEXISTE:='UPDATE SET TABLA.ANO           = VISTA.ANO
                                  ,TABLA.PERIODO       = VISTA.PERIODO
                                  ,TABLA.MODIFIED_BY   = '''||UN_USUARIO||'''
                                  ,TABLA.DATE_MODIFIED = SYSDATE  ';          

        MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA, 
                                          UN_ACCION      => 'MM', 
                                          UN_MERGEUSING  => MI_MERGEUSING,
                                          UN_MERGEENLACE => MI_MERGEENLACE,
                                          UN_MERGEEXISTE => MI_MERGEEXISTE);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CAMPOS';
        MI_MSGERROR(1).VALOR := 'ANO y PERIODO';
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD   => SQLCODE,
          UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_ACTUALIZARUSUARIO,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '26';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    BEGIN
      BEGIN
        MI_TABLA     := 'SP_FINANCIABLES';
        MI_CONDICION := '    COMPANIA         = '''|| UN_COMPANIA ||''' 
                         AND CICLO            =   '|| UN_CICLO    ||' 
                         AND SALDOFINANCIABLE <= 0';
        MI_AFECTADOS := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                               UN_ACCION    => 'E',
                                               UN_CONDICION => MI_CONDICION); 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_BORRARFINANCIABLES
        );
    END;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '27';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    BEGIN
      BEGIN
        MI_TABLA     := 'SP_CICLO';
        MI_CAMPOS    := 'SP_CICLO.INDPREPARADO = -1
                        ,SP_CICLO.ANO          = '  ||MI_ANIOSIG||'
                        ,SP_CICLO.PERIODO      = '''||MI_PERIODOSIG||'''
                        ,MODIFIED_BY    = '''||UN_USUARIO||'''
                        ,DATE_MODIFIED  = SYSDATE  ';

        MI_CONDICION := '    SP_CICLO.COMPANIA = '''|| UN_COMPANIA ||'''
                         AND SP_CICLO.NUMERO   = '  || UN_CICLO ||'';
        MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA,
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CAMPOS';
        MI_MSGERROR(1).VALOR := 'INDPREPARADO, ANO y PERIODO';
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD   => SQLCODE,
          UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_ACTUALIZARCICLO,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '28';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------

      SELECT 'X' 
      INTO   MI_CAMP
      FROM   SP_FACTURAS
      WHERE  COMPANIA = UN_COMPANIA
        AND  CICLO    = UN_CICLO;

     IF MI_CAMP IS  NULL THEN 
           BEGIN
              BEGIN
                MI_TABLA := 'SP_FACTURAS';
                MI_CAMPOS := 'COMPANIA
                             ,CICLO
                             ,CONSECUTIVO
                             ,DATE_CREATED
                             ,CREATED_BY';

                MI_VALORES := ''''||UN_COMPANIA||'''
                                ,'||UN_CICLO||'
                                ,0
                                ,SYSDATE
                                ,'''||UN_USUARIO||'''';

                MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA, 
                                                  UN_ACCION  => 'I', 
                                                  UN_CAMPOS  => MI_CAMPOS, 
                                                  UN_VALORES => MI_VALORES);    
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN               
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE,
                  UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_INSERTARFACTURAS
                ); 
          END;

     ELSE 
          BEGIN
            BEGIN
              MI_TABLA     := 'SP_FACTURAS';

              MI_CAMPOS    := 'SP_FACTURAS.CONSECUTIVO = 0
                              ,MODIFIED_BY    = '''||UN_USUARIO||'''
                              ,DATE_MODIFIED  = SYSDATE ';

              MI_CONDICION := ' SP_FACTURAS.COMPANIA    = '''||UN_COMPANIA||'''
                               AND SP_FACTURAS.CICLO       = '  ||UN_CICLO;               

              MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA,
                                            UN_ACCION    => 'M',
                                            UN_CAMPOS    => MI_CAMPOS,
                                            UN_CONDICION => MI_CONDICION);

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              MI_MSGERROR(1).CLAVE := 'CAMPOS';
              MI_MSGERROR(1).VALOR := 'COMPANIA, CICLO y CONSECUTIVO';
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                         UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_ACTUALIZARFACTURAS ,
                                         UN_REEMPLAZOS => MI_MSGERROR);
          END;
     END IF;




    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    MI_AFECTADOS := 0;
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '29 Consumo';
    MI_HORAINICIO := SYSDATE;
    -----------------------------------------------------------------------------------------------------
    <<CONSUMOPROMEDIO>>
    FOR MI_RSCONSUMOPROMUE IN (SELECT   SP_USUARIO.COMPANIA
                                       ,SP_USUARIO.CICLO
                                       ,SP_USUARIO.PERIODO
                                       ,SP_USUARIO.USO
                                       ,SP_USUARIO.ESTRATO
                                       ,SUM(SP_USUARIO.CONSUMOPROM) SUMADECONSUMOPROM
                                       ,SP_USUARIO.ANO
                                       ,COUNT(SP_USUARIO.CODIGORUTA) CUENTADECODIGORUTA
                               FROM     SP_USUARIO 
                               WHERE    SP_USUARIO.ESTADO NOT IN ('C','R') 
                                 AND    SP_USUARIO.COMPANIA = UN_COMPANIA
                                 AND    SP_USUARIO.CICLO    = UN_CICLO
                                 AND    SP_USUARIO.PERIODO = MI_PERIODOSIG 
                                 AND    SP_USUARIO.ANO = MI_ANIOSIG
                               GROUP BY SP_USUARIO.COMPANIA
                                       ,SP_USUARIO.CICLO
                                       ,SP_USUARIO.PERIODO
                                       ,SP_USUARIO.USO
                                       ,SP_USUARIO.ESTRATO
                                       ,SP_USUARIO.ANO)
    LOOP
      BEGIN
        BEGIN
          MI_TABLA     := 'SP_TARIFAS';
          MI_CAMPOS    := 'SP_TARIFAS.CONSUMOPROM_USOS_ESTRATOS = '||ROUND(MI_RSCONSUMOPROMUE.SUMADECONSUMOPROM 
                                                                         / MI_RSCONSUMOPROMUE.CUENTADECODIGORUTA, 0)||'
                          ,MODIFIED_BY                          = '''||UN_USUARIO||'''
                          ,DATE_MODIFIED                        = SYSDATE ';

          MI_CONDICION := '    SP_TARIFAS.COMPANIA = '''|| UN_COMPANIA ||'''
                           AND SP_TARIFAS.ANO      = '  || MI_ANIOSIG ||'
                           AND SP_TARIFAS.PERIODO  = '''|| MI_PERIODOSIG ||''' 
                           AND SP_TARIFAS.USO      = '''|| MI_RSCONSUMOPROMUE.USO||'''
                           AND SP_TARIFAS.ESTRATO  = '''|| MI_RSCONSUMOPROMUE.ESTRATO||'''';
          MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA,
                                        UN_ACCION    => 'M',
                                        UN_CAMPOS    => MI_CAMPOS);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CAMPOS';
          MI_MSGERROR(1).VALOR := 'CONSUMOPROM_USOS_ESTRATOS';
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD   => SQLCODE,
            UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_ACTUALIZARTARIFAS,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END;
    END LOOP CONSUMOPROMEDIO;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(MI_AFECTADOS, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    -----------------------------------------------------------------------------------------------------
    MI_ETAPA := '30 Fin';
    MI_HORAINICIO := SYSDATE;
    MI_STRRESUMEN := MI_STRRESUMEN || 'Etapa: ' || RPAD(MI_ETAPA, 40, ' ')|| ' = ' || 
                     LPAD(0, 7, ' ') || 
                     ' registros afectados en ' || ((SYSDATE - MI_HORAINICIO)*24)*60 || ' minutos.' || 
                     CHR(10) || CHR(13);
    -----------------------------------------------------------------------------------------------------
    BEGIN
        BEGIN
          MI_TABLA    :='SP_CICLO';
          MI_CAMPOS   :='INDPREPARADO      = -1
                        ,INDCALCULADO      = 0
                        ,FECHA_PREPARACION = SYSDATE
                        ,HORA_PREPARACION  = SYSDATE
                        ,MODIFIED_BY    = '''||UN_USUARIO||'''
                        ,DATE_MODIFIED  = SYSDATE ';

          MI_CONDICION:='       COMPANIA = '''||UN_COMPANIA||
                        ''' AND NUMERO   = '||UN_CICLO;
          MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                            UN_ACCION    => 'M',      
                                            UN_CAMPOS    => MI_CAMPOS,      
                                            UN_CONDICION => MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CAMPOS';
          MI_MSGERROR(1).VALOR := 'COMPANIA= '''||UN_COMPANIA||
                                  ''' y NUMERO= '||UN_CICLO;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD   => SQLCODE,
            UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_ACTUALIZARCICLO ,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END;
      DBMS_OUTPUT.PUT_LINE(MI_STRRESUMEN);
    RETURN MI_STRRESUMEN;
  END FC_PREPARARSIGPERIODO;

  -- 5
  FUNCTION FC_LLAMARPREPARARPER
    /*
      NAME              : FC_LLAMARPREPARARPER 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MIGRADOR     : 20/02/2017
      TIME              : 05:25 PM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : FUNCION QUE PREPARA EL CICLO, CALCULA ESTADISTICAS
                          PARA EL CIERRE DE PERIODO Y REALIZA EL LLAMADO A LA FUNCION
                          QUE PREPARA EL SIGUIENTE PERIODO DE FACTURACION.
      PARAMETERS        : UN_COMPANIA       => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_CICLO          => CICLO SELECCIONADO PARA EL CIERRE.
                          UN_ANIO           => AÑO EN EL QUE SE VA A REALIZAR CIERRE DE FACTURACION.
                          UN_PERIODO        => PERIODO EN EL QUE SE VA A REALIZAR CIERRE DE FACTURACION.
                          UN_SOBREPRODUCT   => SI O NO DEPENDIENDO SI EL USUARIO ESCOGIO SOBREESCRIBIR LA
                                               INFORMACION DE PRODUCTIVIDAD DURANTE EL CIERRE.
                          UN_SOBREDESCUENTO => SI O NO DEPENDIENDO SI EL USUARIO ESCOGIO SOBREESCRIBIR LA
                                               INFORMACION DE DESCUENTOS DURANTE EL CIERRE.
                          UN_NIT            => NIT DE LA COMPANIA.
                          UN_USUARIO        => USUARIO QUE ESTA TRABAJANDO. 
      MODIFICATIONS     : 
      @NAME:  llamarPrepararSigPeriodo
      @METHOD:  GET
    */ 
  (
    UN_COMPANIA       IN  PCK_SUBTIPOS.TI_COMPANIA
   ,UN_CICLO          IN  PCK_SUBTIPOS.TI_CICLO 
   ,UN_ANIO           IN  PCK_SUBTIPOS.TI_ANIO
   ,UN_PERIODO        IN  PCK_SUBTIPOS.TI_PERIODO
   ,UN_SOBREPRODUCT   IN  VARCHAR2
   ,UN_SOBREDESCUENTO IN  VARCHAR2
   ,UN_NIT            IN  VARCHAR2
   ,UN_USUARIO        IN  PCK_SUBTIPOS.TI_USUARIO
  )
    RETURN CLOB
  AS
    MI_TABLA                PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
    MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_FRECUENCIA           VARCHAR2(3 CHAR);
    MI_FECHAPREPA           DATE;
    MI_RETORNO              CLOB;
  BEGIN   
    MI_FRECUENCIA := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                           UN_NOMBRE    => 'FRECUENCIA PERIODOS DE FACTURACION',
                                           UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                           UN_FECHA_PAR => SYSDATE);
    BEGIN
      BEGIN
        SELECT FECHA_PREPARACION
        INTO   MI_FECHAPREPA
        FROM   SP_CICLO
        WHERE  COMPANIA = UN_COMPANIA
          AND  NUMERO   = UN_CICLO;
        EXCEPTION WHEN NO_DATA_FOUND THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ESTADOCICLO
        );
    END; 
    BEGIN
      IF (SYSDATE - MI_FECHAPREPA) < 15 THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END IF; 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_DIASCIERRE
        );
    END;

    PCK_SERVICIOS_PUBLICOS_COM6.PR_REVISAFACTURADOSABONOS(UN_COMPANIA => UN_COMPANIA,
                                                          UN_CICLO    => UN_CICLO,
                                                          UN_ANIO     => UN_ANIO,
                                                          UN_PERIODO  => UN_PERIODO,
                                                          UN_USUARIO  => UN_USUARIO);

    PCK_SERVICIOS_PUBLICOS_COM6.PR_FACTURADOSNULOS(UN_COMPANIA => UN_COMPANIA,
                                                   UN_CICLO    => UN_CICLO,
                                                   UN_ANIO     => UN_ANIO,
                                                   UN_PERIODO  => UN_PERIODO,
                                                   UN_USUARIO  => UN_USUARIO);

    PCK_SERVICIOS_PUBLICOS_COM6.PR_ESTADISTICACONSUMO(UN_COMPANIA => UN_COMPANIA,
                                                      UN_CICLO    => UN_CICLO,
                                                      UN_USUARIO  => UN_USUARIO);

    PCK_SERVICIOS_PUBLICOS_COM6.PR_ESTADISTICAFACTURACION(UN_COMPANIA => UN_COMPANIA,
                                                          UN_CICLO    => UN_CICLO,
                                                          UN_USUARIO  => UN_USUARIO);

    PCK_SERVICIOS_PUBLICOS_COM6.PR_ESTRECAUDOPERATRASO(UN_COMPANIA => UN_COMPANIA,
                                                       UN_CICLO    => UN_CICLO,
                                                       UN_USUARIO  => UN_USUARIO);
    BEGIN
      BEGIN
        MI_TABLA :=  'SP_USUARIO_HISTORICOS'; 
        MI_CAMPOS := 'COMPANIA,
                      CICLO,
                      CODIGORUTA,
                      DATE_CREATED,
                      CREATED_BY';

        MI_VALORES := 'SELECT SP_USUARIO.COMPANIA
                              ,SP_USUARIO.CICLO
                              ,SP_USUARIO.CODIGORUTA
                              ,SYSDATE
                              , '''||UN_USUARIO||'''
                         FROM SP_USUARIO 
                         LEFT JOIN SP_USUARIO_HISTORICOS 
                           ON SP_USUARIO.COMPANIA   = SP_USUARIO_HISTORICOS.COMPANIA
                          AND SP_USUARIO.CICLO      = SP_USUARIO_HISTORICOS.CICLO
                          AND SP_USUARIO.CODIGORUTA = SP_USUARIO_HISTORICOS.CODIGORUTA
                        WHERE SP_USUARIO_HISTORICOS.COMPANIA IS NULL';

        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                              UN_ACCION  => 'IS',
                                              UN_CAMPOS  => MI_CAMPOS,
                                              UN_VALORES => MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_INSERTSPUSUHISTORI
        );
    END;
    BEGIN
      BEGIN
        MI_TABLA  := 'SP_CICLO';
        MI_CAMPOS := ' INDESTADISTICAS = -1,
                       MODIFIED_BY    = '''||UN_USUARIO||''',
                       DATE_MODIFIED  = SYSDATE ';

        MI_CONDICION := '  COMPANIA = '''||UN_COMPANIA||'''
                         AND NUMERO = '  ||UN_CICLO;
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CAMPOS';
        MI_MSGERROR(1).VALOR := 'COMPANIA= '''||UN_COMPANIA||
                                ''' y NUMERO= '||UN_CICLO;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTUALIZARCICLO,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
    PCK_SERVICIOS_PUBLICOS_COM6.PR_GUARDAHISTORIA(UN_COMPANIA => UN_COMPANIA,
                                                  UN_CICLO    => UN_CICLO,
                                                  UN_ANIO     => UN_ANIO,
                                                  UN_PERIODO  => UN_PERIODO,
                                                  UN_USUARIO  => UN_USUARIO);

    IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                             UN_NOMBRE    => 'DESCONTAR PRODUCTIVIDAD',
                             UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                             UN_FECHA_PAR => SYSDATE) = 'SI' THEN
      PCK_SERVICIOS_PUBLICOS_COM6.PR_CERRARPRODUCTIVIDAD(UN_COMPANIA      => UN_COMPANIA,
                                                         UN_CICLO         => UN_CICLO,
                                                         UN_ANIO          => UN_ANIO,
                                                         UN_PERIODO       => UN_PERIODO,
                                                         UN_SOBREESCRIBIR => UN_SOBREPRODUCT,
                                                         UN_USUARIO       => UN_USUARIO);
    END IF;
    IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                             UN_NOMBRE    => 'MANEJA CONTROL DE DESCUENTOS POR CONCEPTOS',
                             UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                             UN_FECHA_PAR => SYSDATE) = 'SI' THEN
      PCK_SERVICIOS_PUBLICOS_COM6.PR_CERRARDESCUENTOS(UN_COMPANIA      => UN_COMPANIA,
                                                      UN_CICLO         => UN_CICLO,
                                                      UN_ANIO          => UN_ANIO,
                                                      UN_PERIODO       => UN_PERIODO,
                                                      UN_NIT           => UN_NIT,
                                                      UN_SOBREESCRIBIR => UN_SOBREDESCUENTO,
                                                      UN_USUARIO       => UN_USUARIO);
    END IF;
    IF PCK_SERVICIOS_PUBLICOS.FC_AUTORIZACION_MICROMEDICION(UN_COMPANIA => UN_COMPANIA,
                                                            UN_NIT      => UN_NIT) <> 0 THEN
      IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                               UN_NOMBRE    => 'MANEJA MICROMEDICION EN SITIO',
                               UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                               UN_FECHA_PAR => SYSDATE) <> 'SI' THEN
        PCK_SERVICIOS_PUBLICOS_COM6.PR_CIERRECONSUMOSMICRO(UN_COMPANIA => UN_COMPANIA,
                                                           UN_CICLO    => UN_CICLO,
                                                           UN_USUARIO  => UN_USUARIO);
      ELSE
        PCK_DATOS.GL_RTA := PCK_SERVICIOS_PUBLICOS_COM5.FC_MICROMEDICIONSITIO(UN_COMPANIA   => UN_COMPANIA,
                                                                              UN_CICLO      => UN_CICLO,
                                                                              UN_CODIGORUTA => '',
                                                                              UN_ANIO       => UN_ANIO,
                                                                              UN_PERIODO    => UN_PERIODO,
                                                                              UN_MOMENTO    => 'P',
                                                                              UN_USUARIO    => UN_USUARIO);
      END IF;
    END IF;  
    IF PCK_SERVICIOS_PUBLICOS_COM1.FC_AUTORIZACION_PERSUASIVO(UN_COMPANIA => UN_COMPANIA,
                                                              UN_NIT      => UN_NIT) <> 0 THEN
      PCK_SERVICIOS_PUBLICOS_COM6.PR_CIERREPERSUASIVO(UN_COMPANIA => UN_COMPANIA,
                                                      UN_CICLO    => UN_CICLO,
                                                      UN_USUARIO  => UN_USUARIO);
    END IF;
    MI_RETORNO := PCK_SERVICIOS_PUBLICOS_COM6.FC_PREPARARSIGPERIODO(UN_COMPANIA => UN_COMPANIA,
                                                                    UN_CICLO    => UN_CICLO,
                                                                    UN_USUARIO  => UN_USUARIO,
                                                                    UN_NIT      => UN_NIT,
                                                                    UN_ANIO     => UN_ANIO,
                                                                    UN_PERIODO  => UN_PERIODO);

    RETURN MI_RETORNO;
  END FC_LLAMARPREPARARPER;

  -- 6
  PROCEDURE PR_GUARDAHISTORIA 
    /*
      NAME              : PR_GUARDAHISTORIA --> EN ACCESS F_Guarda_historia
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MIGRADOR     : 30/01/2017
      TIME              : 10:55 AM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PROCEDIMIENTO QUE REGISTRA EN SP_HISTORIA LOS CONCEPTOS DE UN PERIODO ANTERIOR PARA UN 
                          CODIGORUTA, AÑO Y PERIODO ESPECIFICO, PARA PREPARAR EL SIGUIENTE PERIODO. RETORNA 
                          VERDADERO EN CASO DE QUE SE REALICE EL PROCESO DE INSERCION DE POR LO MENOS UN 
                          REGISTRO, DE LOS CONTRARIO RETORNA FALSO.
      PARAMETERS        : UN_COMPANIA => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_CICLO    => CICLO POR EL CUAL SE VA A FILTRAR LA INFORMACION.
                          UN_ANIO     => ANIO DEL QUE SE QUIERE PREPARAR EL SIGUIENTE PERIODO.
                          UN_PERIODO  => PERIODO ANTERIOR, DEL QUE SE REGISTRARA LA INFORMACION EN SP_HISTORIA.
      MODIFICATIONS     : 
      @NAME:  guardarHistoria
      @METHOD:  POST
      */
  (
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_CICLO      IN PCK_SUBTIPOS.TI_CICLO
   ,UN_ANIO       IN PCK_SUBTIPOS.TI_ANIO
   ,UN_PERIODO    IN PCK_SUBTIPOS.TI_PERIODO
   ,UN_USUARIO    IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
   MI_STRSQL              PCK_SUBTIPOS.TI_STRSQL;
   MI_MANUALES            VARCHAR(100 CHAR);
   MI_TERCERIZA           VARCHAR(100 CHAR);
   MI_CONVENIOS           VARCHAR(100 CHAR);
   MI_TABLA               PCK_SUBTIPOS.TI_TABLA;
   MI_CAMPOS              PCK_SUBTIPOS.TI_CAMPOS;
   MI_VALORES             PCK_SUBTIPOS.TI_VALORES;
   MI_CONDICION           PCK_SUBTIPOS.TI_CONDICION;
   MI_CONTEO              PCK_SUBTIPOS.TI_ENTERO;
   MI_CONT                PCK_SUBTIPOS.TI_ENTERO;
   MI_CONCEPTOS           VARCHAR(32000 CHAR);
   MI_CADENA              VARCHAR(32000 CHAR);
   MI_PIVOT               PCK_SUBTIPOS.TI_STRSQL;
   MI_MSGERROR            PCK_SUBTIPOS.TI_CLAVEVALOR;  
   MI_FILASAFECT          PCK_SUBTIPOS.TI_ENTERO_LARGO;
   MI_MERGEUSING          PCK_SUBTIPOS.TI_MERGEUSING;
   MI_MERGEENLACE         PCK_SUBTIPOS.TI_MERGEENLACE;
   MI_MERGEEXISTE         PCK_SUBTIPOS.TI_MERGEEXISTE;
  BEGIN
    MI_FILASAFECT := 0;
    BEGIN
      SELECT COUNT(1)
        INTO MI_CONT
        FROM SP_HISTORIA HISTORIA
       WHERE HISTORIA.COMPANIA = UN_COMPANIA
         AND HISTORIA.CICLO    = UN_CICLO   
         AND HISTORIA.ANO      = UN_ANIO     
         AND HISTORIA.PERIODO  = UN_PERIODO;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_CONT := 0;
    END;
    IF MI_CONT <> 0 THEN
      BEGIN
        BEGIN
          MI_TABLA         := 'SP_HISTORIA';
          MI_CONDICION     := '     COMPANIA = '''|| UN_COMPANIA ||''' 
                                AND CICLO    =   '|| UN_CICLO    ||' 
                                AND ANO      =   '|| UN_ANIO     ||'
                                AND PERIODO  = '''|| UN_PERIODO  ||'''';
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                                 UN_ACCION    => 'E',
                                                 UN_CONDICION => MI_CONDICION); 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_BORRARHISTORIA
          );
      END;
    END IF;
    MI_STRSQL := 'SELECT DISTINCT SP_CONCEPTOS.CODIGO 
                    FROM SP_CONCEPTOS
                   WHERE SP_CONCEPTOS.COMPANIA = '''||UN_COMPANIA||'''
                     AND SP_CONCEPTOS.CODIGO BETWEEN 1 AND 50
                      OR SP_CONCEPTOS.CODIGO BETWEEN 246 AND 250';
    BEGIN
      EXECUTE IMMEDIATE 'SELECT COUNT(1) CONTEO 
                         FROM ('||MI_STRSQL||')' 
                         INTO MI_CONTEO;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_CONTEO := 0;
    END;  
    <<CONCEPTOS>>
    FOR MI_RS IN (SELECT DISTINCT SP_CONCEPTOS.CODIGO
                    FROM SP_CONCEPTOS
                   WHERE SP_CONCEPTOS.COMPANIA = UN_COMPANIA
                     AND (SP_CONCEPTOS.CODIGO BETWEEN 1 AND 50
                      OR SP_CONCEPTOS.CODIGO BETWEEN 246 AND 250)) 
    LOOP 
      IF MI_CONTEO > 1 THEN
        MI_CONCEPTOS := MI_CONCEPTOS || MI_RS.CODIGO || ' AS C'||MI_RS.CODIGO||', ';
         MI_CADENA := MI_CADENA || 'C' || MI_RS.CODIGO || '||'',''||' ;

      ELSE
        MI_CONCEPTOS := MI_CONCEPTOS || MI_RS.CODIGO || ' AS C'||MI_RS.CODIGO;
        MI_CADENA := MI_CADENA || 'C' || MI_RS.CODIGO;
      END IF;  
      MI_CONTEO := MI_CONTEO - 1;
    END LOOP CONCEPTOS;
    MI_TABLA := 'SP_HISTORIA'; 

    MI_CAMPOS := ' COMPANIA
                  ,CICLO
                  ,CODIGORUTA
                  ,ANO
                  ,PERIODO
                  ,CODIGOINTERNO
                  ,USO
                  ,ESTRATO
                  ,LECTURA
                  ,BANCOPERPROCESO
                  ,FECHAPAGOPERPROCESO
                  ,PAQUETEPAGOPERPROCESO
                  ,TOTFACTURAPERACTUAL
                  ,CONSUMO
                  ,LECTURA1
                  ,CONSUMOPROM
                  ,LECTURAAFORO
                  ,AFORADOR
                  ,C1,C2,C3,C4,C5,C6,C7,C8,C9,C10
                  ,C11,C12,C13,C14,C15,C16,C17,C18
                  ,C19,C20,C21,C22,C23,C24,C25,C26
                  ,C27,C28,C29,C30,C31,C32,C33,C34
                  ,C35,C36,C37,C38,C39,C40,C41,C42
                  ,C43,C44,C45,C46,C47,C48,C49,C50
                  ,C246,C247,C248,C249,C250
                  ,DATE_CREATED,CREATED_BY';

    <<USUARIOFACTURADO>>
    FOR MI_HISTORIA IN (SELECT   U.COMPANIA
                                ,U.CICLO
                                ,U.CODIGORUTA
                                ,U.ANO
                                ,U.PERIODO
                                ,U.CODIGOINTERNO
                                ,U.USO
                                ,U.ESTRATO
                                ,U.LECTURA
                                ,U.BANCOPERPROCESO
                                ,U.FECHAPAGOPERPROCESO
                                ,U.PAQUETEPAGOPERPROCESO
                                ,U.TOTFACTURAPERACTUAL
                                ,U.CONSUMO
                                ,U.LECTURA1
                                ,U.CONSUMOPROM
                                ,U.LECTURAAFORO
                                ,U.AFORADOR
                          FROM SP_USUARIO U
                          LEFT JOIN SP_FACTURADO F
                            ON U.CODIGORUTA = F.CODIGORUTA
                           AND U.CICLO      = F.CICLO
                           AND U.COMPANIA   = F.COMPANIA
                         WHERE U.COMPANIA = UN_COMPANIA
                           AND U.CICLO    = UN_CICLO
                           AND U.ANO      = UN_ANIO
                           AND U.PERIODO  = UN_PERIODO
                           AND (CONCEPTO  BETWEEN 1 AND 50
                            OR CONCEPTO   BETWEEN 246 AND 250 )
                         GROUP BY 
                               U.COMPANIA
                               ,U.CICLO
                               ,U.CODIGORUTA
                               ,U.ANO
                               ,U.PERIODO
                               ,U.CODIGOINTERNO
                               ,U.USO
                               ,U.ESTRATO
                               ,U.LECTURA
                               ,U.BANCOPERPROCESO
                               ,U.FECHAPAGOPERPROCESO
                               ,U.PAQUETEPAGOPERPROCESO
                               ,U.TOTFACTURAPERACTUAL
                               ,U.CONSUMO
                               ,U.LECTURA1
                               ,U.CONSUMOPROM
                               ,U.LECTURAAFORO
                               ,U.AFORADOR)
    LOOP   
      BEGIN
        EXECUTE IMMEDIATE 'SELECT LISTAGG('||MI_CADENA||') WITHIN GROUP(ORDER BY C1)
                           FROM (SELECT   DISTINCT F.CONCEPTO
                                         ,NVL(SUM(F.VALOR_FACTURADO),0) VALOR_FACTURADO
                                 FROM     SP_USUARIO U
                                   LEFT JOIN SP_FACTURADO F
                                     ON      U.CODIGORUTA = F.CODIGORUTA
                                    AND      U.CICLO      = F.CICLO
                                    AND      U.COMPANIA   = F.COMPANIA
                                 WHERE    U.COMPANIA   = '''||UN_COMPANIA||'''
                                   AND    U.CICLO      =   '||UN_CICLO||' 
                                   AND    U.ANO        =   '||UN_ANIO||'
                                   AND    U.PERIODO    = '''||UN_PERIODO||'''
                                   AND    U.CODIGORUTA = '''||MI_HISTORIA.CODIGORUTA||'''
                                   AND    (CONCEPTO    BETWEEN 1 AND 50
                                    OR    CONCEPTO     BETWEEN 246 AND 250)
                                 GROUP BY F.CONCEPTO
                                 UNION(
                                   SELECT   SP_CONCEPTOS.CODIGO
                                           ,0 VALOR_FACTURADO
                                   FROM     SP_CONCEPTOS
                                   WHERE    SP_CONCEPTOS.COMPANIA = '''|| UN_COMPANIA ||'''
                                     AND    SP_CONCEPTOS.CODIGO   BETWEEN 1 AND 50
                                      OR    SP_CONCEPTOS.CODIGO   BETWEEN 246 AND 250
                                   MINUS
                                     SELECT  SP_FACTURADO.CONCEPTO
                                            ,0 VALOR_FACTURADO
                                     FROM    SP_FACTURADO 
                                     WHERE   SP_FACTURADO.COMPANIA   ='''|| UN_COMPANIA ||'''
                                       AND   SP_FACTURADO.CICLO      =  '|| UN_CICLO ||'
                                       AND   SP_FACTURADO.ANO        =  '||UN_ANIO||'
                                       AND   SP_FACTURADO.PERIODO    ='''||UN_PERIODO||'''
                                       AND   SP_FACTURADO.CODIGORUTA ='''||MI_HISTORIA.CODIGORUTA||'''
                                       AND   (SP_FACTURADO.CONCEPTO BETWEEN 1 AND 50
                                        OR   SP_FACTURADO.CONCEPTO BETWEEN 246 AND 250)) 
                           )PIVOT( 
                             SUM (VALOR_FACTURADO)  
                             FOR CONCEPTO IN ('||MI_CONCEPTOS||')
                           )ORDER BY 1'
                           INTO MI_PIVOT;
        EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_MSGERROR(1).CLAVE := 'CODIGO';
          MI_MSGERROR(1).VALOR := MI_HISTORIA.CODIGORUTA;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_CONCEPTOSCODRUT,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END;
      MI_VALORES := ''''   ||MI_HISTORIA.COMPANIA||
                    ''','  ||MI_HISTORIA.CICLO||
                    ','''  ||MI_HISTORIA.CODIGORUTA||
                    ''','  ||MI_HISTORIA.ANO|| 
                    ','''  ||MI_HISTORIA.PERIODO||
                    ''','''||MI_HISTORIA.CODIGOINTERNO||
                    ''','''||MI_HISTORIA.USO||
                    ''','''||MI_HISTORIA.ESTRATO||
                    ''','  ||MI_HISTORIA.LECTURA||
                    ','''  ||MI_HISTORIA.BANCOPERPROCESO||
                    ''',TO_DATE('''||TO_CHAR(MI_HISTORIA.FECHAPAGOPERPROCESO,'DD/MM/YYYY')||''',''DD/MM/YYYY'')
                    ,'''   ||MI_HISTORIA.PAQUETEPAGOPERPROCESO||
                    ''','  ||MI_HISTORIA.TOTFACTURAPERACTUAL||
                    ','    ||MI_HISTORIA.CONSUMO||
                    ','    ||MI_HISTORIA.LECTURA1||
                    ','    ||MI_HISTORIA.CONSUMOPROM||
                    ','    ||MI_HISTORIA.LECTURAAFORO||
                    ','''  ||MI_HISTORIA.AFORADOR||
                    ''','  ||MI_PIVOT||',SYSDATE,'''||UN_USUARIO||'''';
      BEGIN
        BEGIN
          MI_FILASAFECT := MI_FILASAFECT + PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA, 
                                                             UN_ACCION  => 'I', 
                                                             UN_CAMPOS  => MI_CAMPOS, 
                                                             UN_VALORES => MI_VALORES);  
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CODIGO';
          MI_MSGERROR(1).VALOR := MI_HISTORIA.CODIGORUTA;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_INSERTHISTORIA,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END;
    END LOOP USUARIOFACTURADO;
    IF MI_FILASAFECT <> 0 THEN
      BEGIN
        BEGIN
          MI_TABLA := 'SP_HISTORIA';
          MI_MERGEUSING  := 'SELECT SP_HISTORIA.COMPANIA
                                   ,SP_HISTORIA.CICLO
                                   ,SP_HISTORIA.CODIGORUTA
                                   ,SP_HISTORIA.ANO
                                   ,SP_HISTORIA.PERIODO
                                   ,SP_USUARIO.DESVIOSIGNIFICATIVO
                             FROM   SP_HISTORIA  
                               INNER JOIN SP_USUARIO 
                                  ON      SP_HISTORIA.COMPANIA   =  SP_USUARIO.COMPANIA 
                                 AND      SP_HISTORIA.CICLO      = SP_USUARIO.CICLO
                                 AND      SP_HISTORIA.CODIGORUTA = SP_USUARIO.CODIGORUTA
                                 AND      SP_HISTORIA.ANO        = SP_USUARIO.ANO
                                 AND      SP_HISTORIA.PERIODO    = SP_USUARIO.PERIODO
                             WHERE  SP_HISTORIA.COMPANIA = '''|| UN_COMPANIA ||'''
                               AND  SP_HISTORIA.CICLO    =   '|| UN_CICLO ||'
                               AND  SP_HISTORIA.ANO      =   '|| UN_ANIO ||'
                               AND  SP_HISTORIA.PERIODO  = '''|| UN_PERIODO ||'''';
          MI_MERGEENLACE := '    TABLA.COMPANIA   = VISTA.COMPANIA 
                             AND TABLA.CICLO      = VISTA.CICLO
                             AND TABLA.CODIGORUTA = VISTA.CODIGORUTA
                             AND TABLA.ANO        = VISTA.ANO
                             AND TABLA.PERIODO    = VISTA.PERIODO';
          MI_MERGEEXISTE := 'UPDATE 
                               SET TABLA.DESVIACION     = NVL(VISTA.DESVIOSIGNIFICATIVO,0),
                                   TABLA.MODIFIED_BY    = '''||UN_USUARIO||''',
                                   TABLA.DATE_MODIFIED  = SYSDATE ';

          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA, 
                                                UN_ACCION      => 'MM', 
                                                UN_MERGEUSING  => MI_MERGEUSING, 
                                                UN_MERGEENLACE => MI_MERGEENLACE,
                                                UN_MERGEEXISTE => MI_MERGEEXISTE);    
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_MERGEHISTORIA
          );
      END; 
      MI_MANUALES := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                           UN_NOMBRE    => 'MANEJA CONSUMO MANUAL', 
                                           UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                           UN_FECHA_PAR => SYSDATE);
      MI_TERCERIZA := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                            UN_NOMBRE    => 'MANEJA PROCESO TERCERIZADO', 
                                            UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                            UN_FECHA_PAR => SYSDATE);
      MI_CONVENIOS := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                            UN_NOMBRE    => 'MANEJA CONVENIO DE FACTURACION', 
                                            UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                            UN_FECHA_PAR => SYSDATE);
      IF MI_MANUALES = 'SI' THEN
        BEGIN
          BEGIN
            MI_TABLA := 'SP_HISTORIA';
            MI_MERGEUSING  := 'SELECT SP_HISTORIA.COMPANIA
                                     ,SP_HISTORIA.CICLO
                                     ,SP_HISTORIA.CODIGORUTA
                                     ,SP_HISTORIA.ANO
                                     ,SP_HISTORIA.PERIODO
                                     ,SP_USUARIO.CONSUMOALC
                                     ,SP_USUARIO.CONSUMOMANUAL
                               FROM   SP_HISTORIA  
                                 INNER JOIN SP_USUARIO 
                                    ON      SP_HISTORIA.COMPANIA   = SP_USUARIO.COMPANIA 
                                   AND      SP_HISTORIA.CICLO      = SP_USUARIO.CICLO
                                   AND      SP_HISTORIA.CODIGORUTA = SP_USUARIO.CODIGORUTA
                                   AND      SP_HISTORIA.ANO        = SP_USUARIO.ANO
                                   AND      SP_HISTORIA.PERIODO    = SP_USUARIO.PERIODO
                               WHERE  SP_HISTORIA.COMPANIA     = '''|| UN_COMPANIA ||'''
                                 AND  SP_HISTORIA.CICLO        =   '|| UN_CICLO ||'
                                 AND  SP_HISTORIA.ANO          =   '|| UN_ANIO ||'
                                 AND  SP_HISTORIA.PERIODO      = '''|| UN_PERIODO ||'''
                                 AND  SP_USUARIO.CONSUMOMANUAL = -1';
            MI_MERGEENLACE := '    TABLA.COMPANIA   = VISTA.COMPANIA 
                               AND TABLA.CICLO      = VISTA.CICLO
                               AND TABLA.CODIGORUTA = VISTA.CODIGORUTA
                               AND TABLA.ANO        = VISTA.ANO
                               AND TABLA.PERIODO    = VISTA.PERIODO';
            MI_MERGEEXISTE := 'UPDATE 
                                 SET TABLA.CONSUMOALC    = VISTA.CONSUMOALC
                                    ,TABLA.CONSUMOMANUAL = VISTA.CONSUMOMANUAL
                                    ,TABLA.MODIFIED_BY         = '''||UN_USUARIO||'''
                                    ,TABLA.DATE_MODIFIED       = SYSDATE ';

            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA, 
                                                  UN_ACCION      => 'MM', 
                                                  UN_MERGEUSING  => MI_MERGEUSING, 
                                                  UN_MERGEENLACE => MI_MERGEENLACE,
                                                  UN_MERGEEXISTE => MI_MERGEEXISTE);   
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
            MI_MSGERROR(1).CLAVE := 'CAMPOS';
            MI_MSGERROR(1).VALOR := 'CONSUMOALC y CONSUMOMANUAL';
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_MERGEHISTCONSUMO,
              UN_REEMPLAZOS => MI_MSGERROR
            );
        END;
      END IF;  
      IF MI_TERCERIZA = 'SI' THEN
        BEGIN
          BEGIN
            MI_TABLA := 'SP_HISTORIA';
            MI_MERGEUSING  := 'SELECT SP_HISTORIA.COMPANIA
                                     ,SP_HISTORIA.CICLO
                                     ,SP_HISTORIA.CODIGORUTA
                                     ,SP_HISTORIA.ANO
                                     ,SP_HISTORIA.PERIODO
                                     ,SP_HISTORIA_EXTERNA.VALORASEO
                                FROM SP_HISTORIA  
                               INNER JOIN SP_HISTORIA_EXTERNA 
                                  ON SP_HISTORIA.COMPANIA     = SP_HISTORIA_EXTERNA.COMPANIA 
                                 AND SP_HISTORIA.CICLO        = SP_HISTORIA_EXTERNA.CICLO
                                 AND SP_HISTORIA.CODIGORUTA   = SP_HISTORIA_EXTERNA.CODIGORUTA
                                 AND SP_HISTORIA.ANO          = SP_HISTORIA_EXTERNA.ANO
                                 AND SP_HISTORIA.PERIODO      = SP_HISTORIA_EXTERNA.PERIODO
                               WHERE SP_HISTORIA.COMPANIA     = '''|| UN_COMPANIA ||'''
                                 AND SP_HISTORIA.CICLO        =   '|| UN_CICLO ||'
                                 AND SP_HISTORIA.ANO          =   '|| UN_ANIO ||'
                                 AND SP_HISTORIA.PERIODO      = '''|| UN_PERIODO ||'''';
            MI_MERGEENLACE := '    TABLA.COMPANIA   = VISTA.COMPANIA 
                               AND TABLA.CICLO      = VISTA.CICLO
                               AND TABLA.CODIGORUTA = VISTA.CODIGORUTA
                               AND TABLA.ANO        = VISTA.ANO
                               AND TABLA.PERIODO    = VISTA.PERIODO';
            MI_MERGEEXISTE := 'UPDATE 
                                 SET TABLA.TERCERIZADO    = VISTA.VALORASEO,
                                     TABLA.MODIFIED_BY    = '''||UN_USUARIO||''',
                                     TABLA.DATE_MODIFIED  = SYSDATE ';

            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA, 
                                                  UN_ACCION      => 'MM', 
                                                  UN_MERGEUSING  => MI_MERGEUSING, 
                                                  UN_MERGEENLACE => MI_MERGEENLACE,
                                                  UN_MERGEEXISTE => MI_MERGEEXISTE);  
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_MERGEHISTTERCE 
            );
        END; 
      END IF;
      IF MI_CONVENIOS = 'SI' THEN
        BEGIN
          BEGIN
            MI_TABLA := 'SP_HISTORIA';
            MI_MERGEUSING  := 'SELECT SP_HISTORIA.COMPANIA
                                     ,SP_HISTORIA.CICLO
                                     ,SP_HISTORIA.CODIGORUTA
                                     ,SP_HISTORIA.ANO
                                     ,SP_HISTORIA.PERIODO
                                     ,SP_HISTORIA_CONVENIOS.TOTAL
                                FROM SP_HISTORIA  
                               INNER JOIN SP_HISTORIA_CONVENIOS
                                  ON SP_HISTORIA.COMPANIA   = SP_HISTORIA_CONVENIOS.COMPANIA 
                                 AND SP_HISTORIA.CICLO      = SP_HISTORIA_CONVENIOS.CICLO
                                 AND SP_HISTORIA.CODIGORUTA = SP_HISTORIA_CONVENIOS.CODIGORUTA
                                 AND SP_HISTORIA.ANO        = SP_HISTORIA_CONVENIOS.ANO
                                 AND SP_HISTORIA.PERIODO    = SP_HISTORIA_CONVENIOS.PERIODO
                               WHERE SP_HISTORIA.COMPANIA     = '''|| UN_COMPANIA ||'''
                                 AND SP_HISTORIA.CICLO        =   '|| UN_CICLO ||'
                                 AND SP_HISTORIA.ANO          =   '|| UN_ANIO ||'
                                 AND SP_HISTORIA.PERIODO      = '''|| UN_PERIODO ||'''';
            MI_MERGEENLACE := '    TABLA.COMPANIA   = VISTA.COMPANIA 
                               AND TABLA.CICLO      = VISTA.CICLO
                               AND TABLA.CODIGORUTA = VISTA.CODIGORUTA
                               AND TABLA.ANO        = VISTA.ANO
                               AND TABLA.PERIODO    = VISTA.PERIODO';
            MI_MERGEEXISTE := 'UPDATE 
                                 SET TABLA.CONVENIOS      = VISTA.TOTAL,
                                     TABLA.MODIFIED_BY    = '''||UN_USUARIO||''',
                                     TABLA.DATE_MODIFIED  = SYSDATE ';

            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA, 
                                                  UN_ACCION      => 'MM', 
                                                  UN_MERGEUSING  => MI_MERGEUSING, 
                                                  UN_MERGEENLACE => MI_MERGEENLACE,
                                                  UN_MERGEEXISTE => MI_MERGEEXISTE);  
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_MERGEHISTCONVENIOS  
            );
        END; 
      END IF;
    END IF;  
  END PR_GUARDAHISTORIA;

  -- 7
  PROCEDURE PR_CIERRECONSUMOSMICRO
    /*
      NAME              : PR_CIERRECONSUMOSMICRO --> EN ACCESS CierreConsumosMicro
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MIGRADOR     : 03/02/2017
      TIME              : 11:30 AM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PROCEDIMIENTO QUE ACTUALIZA LOS CONSUMOS MICRO DEL USUARIO 
                          Y LAS LECTURAS DEL MICROMEDIDOR PARA QUE EN LOS SIGUIENTES 
                          PERIODOS EMPIECEN EN LA LECTURA INICIAL.
      PARAMETERS        : UN_COMPANIA => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_CICLO    => CICLO DEL QUE SE VA A REALIZAR EL CIERRE.
      MODIFICATIONS     : 
      @NAME:  cerrarConsumosMicro
      @METHOD:  PUT
    */ 
  (
    UN_COMPANIA         IN  PCK_SUBTIPOS.TI_COMPANIA
   ,UN_CICLO            IN  PCK_SUBTIPOS.TI_CICLO
   ,UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;  
    MI_TABLA            PCK_SUBTIPOS.TI_TABLA; 
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS; 
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
  BEGIN
    BEGIN
      BEGIN
        MI_TABLA     := 'SP_USUARIO';
        MI_CAMPOS    := 'PORMICRO = 0';
        MI_CONDICION := '    COMPANIA = '''||UN_COMPANIA||'''
                         AND CICLO    ='||UN_CICLO||' 
                         AND PORMICRO <> 0';
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          MI_MSGERROR(1).CLAVE := 'CAMPOS';
          MI_MSGERROR(1).VALOR := 'PORMICRO';
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      BEGIN
        MI_TABLA     := 'SP_USUARIO';
        MI_CAMPOS    := 'CONSAFOROMICRO = 0,
                         MODIFIED_BY    = '''||UN_USUARIO||''',
                         DATE_MODIFIED  = SYSDATE  ';

        MI_CONDICION := '    COMPANIA       = '''||UN_COMPANIA||'''
                         AND CICLO          ='||UN_CICLO||' 
                         AND CONSAFOROMICRO IS NULL';
       PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                             UN_ACCION    => 'M',
                                             UN_CAMPOS    => MI_CAMPOS,
                                             UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          MI_MSGERROR(1).CLAVE := 'CAMPOS';
          MI_MSGERROR(1).VALOR := 'CONSAFOROMICRO';
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      -- EN EL WHERE SE ENVIA EL AFOROCONSMANUAL CON EL FIN DE DAR PRIORIDAD AL CONSUMO MANUAL
      BEGIN
        MI_TABLA     := 'SP_USUARIO';
        MI_CAMPOS    := 'CONSUMOACUMANUAL = CONSAFOROMICRO
                        ,CONSUMOALCMANUAL = CONSAFOROMICRO
                        ,AFOROCONSMANUAL  = -1
                        ,PORMICRO         = -1
                        ,CONSAFOROMICRO   = 0
                        ,MODIFIED_BY      = '''||UN_USUARIO||'''
                        ,DATE_MODIFIED    = SYSDATE';

        MI_CONDICION := '    COMPANIA               = '''||UN_COMPANIA||'''
                         AND CICLO                  =   '||UN_CICLO||' 
                         AND CONSAFOROMICRO         > 0
                         AND NVL(AFOROCONSMANUAL,0) = 0';
       PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                             UN_ACCION    => 'M',
                                             UN_CAMPOS    => MI_CAMPOS,
                                             UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          MI_MSGERROR(1).CLAVE := 'CAMPOS';
          MI_MSGERROR(1).VALOR := 'CONSUMOACUMANUAL, CONSUMOALCMANUAL, AFOROCONSMANUAL, PORMICRO, CONSAFOROMICRO';
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD   => SQLCODE,
          UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_ACTUALIZARUSUARIO,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
    -- ACTUALIZA LA TABLA DE MICROMEDIDOR PARA QUE EL CONTROL DE LA LECTURA PARA SIGUIENTES 
    -- PERIODOS LO TOME DESDE LA LECTURA INICIAL 
    BEGIN
      BEGIN
        MI_TABLA     := 'SP_MICROMEDICION_CAMBIO';
        MI_CAMPOS    := 'CONTROLLECTURA = 0,
                         MODIFIED_BY    = '''||UN_USUARIO||''',
                         DATE_MODIFIED  = SYSDATE  ';
        MI_CONDICION := '    COMPANIA       = '''||UN_COMPANIA||'''
                         AND CICLO          =   '||UN_CICLO||' 
                         AND CONTROLLECTURA = -1';
       PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                             UN_ACCION    => 'M',
                                             UN_CAMPOS    => MI_CAMPOS,
                                             UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CAMPOS';
        MI_MSGERROR(1).VALOR := 'CONTROLLECTURA';
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD   => SQLCODE,
          UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_ACTUALIZARMEDICION,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
    -- SE ACTUALIZA EL CONSAFOROMICRO A 0 POR SI SE QUEDA UNO CALCULADO PERO NO ENVIADO A 
    -- CONSUMO MANUAL YA QUE SE LE DA PRIORIDAD A ESTE ÚLTIMO
    BEGIN
      BEGIN
        MI_TABLA     := 'SP_USUARIO';
        MI_CAMPOS    := 'CONSAFOROMICRO = 0,
                         MODIFIED_BY    = '''||UN_USUARIO||''',
                         DATE_MODIFIED  = SYSDATE  ';

        MI_CONDICION := '    COMPANIA       = '''||UN_COMPANIA||'''
                         AND CICLO          =   '||UN_CICLO||' 
                         AND CONSAFOROMICRO <> 0';

       PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                             UN_ACCION    => 'M',
                                             UN_CAMPOS    => MI_CAMPOS,
                                             UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CAMPOS';
        MI_MSGERROR(1).VALOR := 'CONSAFOROMICRO';
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD   => SQLCODE,
          UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_ACTUALIZARUSUARIO,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
  END PR_CIERRECONSUMOSMICRO;

  -- 8
  PROCEDURE PR_CIERREPERSUASIVO
    /*
      NAME              : PR_CIERREPERSUASIVO --> EN ACCESS CierrePersuasivo
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MIGRADOR     : 03/02/2017
      TIME              : 02:31 PM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PROCEDIMIENTO QUE ACTUALIZA EL TIPO DE COBRO EN LOS USUARIOS
                          Y ESTADO, AÑO DE CIERRE, PERIODO DE CIERRE Y FECHACIERRE EN 
                          COBROSPERSUASIVOS.
      PARAMETERS        : UN_COMPANIA => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_CICLO    => CICLO DEL QUE SE VA A REALIZAR EL CIERRE.
      MODIFICATIONS     : 
      @NAME:  realizarCierrePersuasivo
      @METHOD:  PUT
    */
  (
    UN_COMPANIA       IN  PCK_SUBTIPOS.TI_COMPANIA
   ,UN_CICLO          IN  PCK_SUBTIPOS.TI_CICLO 
   ,UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO

  )
  AS
    MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;  
    MI_MERGEUSING     PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE    PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE    PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_TABLA          PCK_SUBTIPOS.TI_TABLA;
  BEGIN
    <<ESTADOSCOBRO>>
    FOR MI_RS IN (SELECT CODIGO 
                  FROM   SP_ESTADOSCOBRO 
                  WHERE  COMPANIA    = UN_COMPANIA
                  AND    PERTENECE_A = 'N')
    LOOP
      IF MI_RS.CODIGO <> ' ' THEN
        BEGIN
          BEGIN
            MI_TABLA := 'SP_USUARIO';
            MI_MERGEUSING  := 'SELECT SP_USUARIO.COMPANIA
                                     ,SP_USUARIO.CICLO
                                     ,SP_USUARIO.CODIGORUTA
                               FROM   SP_USUARIO  
                                 INNER JOIN SP_COBROSPERSUASIVOS 
                                    ON      SP_USUARIO.COMPANIA   = SP_COBROSPERSUASIVOS.COMPANIA 
                                   AND      SP_USUARIO.CICLO      = SP_COBROSPERSUASIVOS.CICLO
                                   AND      SP_USUARIO.CODIGORUTA = SP_COBROSPERSUASIVOS.CODIGORUTA
                               WHERE  SP_USUARIO.COMPANIA            = '''|| UN_COMPANIA ||'''
                                 AND  SP_USUARIO.CICLO               =   '|| UN_CICLO ||'
                                 AND  SP_USUARIO.FECHAPAGOPERPROCESO IS NOT NULL
                                 AND  (SP_USUARIO.TIPOCOBRO          <> '''||MI_RS.CODIGO||'''
                                 AND  SP_COBROSPERSUASIVOS.ESTADO    <> ''C'')';
            MI_MERGEENLACE := '    TABLA.COMPANIA   = VISTA.COMPANIA 
                               AND TABLA.CICLO      = VISTA.CICLO
                               AND TABLA.CODIGORUTA = VISTA.CODIGORUTA';
            MI_MERGEEXISTE := 'UPDATE 
                                 SET TABLA.TIPOCOBRO      = '''||MI_RS.CODIGO||''',
                                     TABLA.MODIFIED_BY    = '''||UN_USUARIO||''',
                                     TABLA.DATE_MODIFIED  = SYSDATE ';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA, 
                                                  UN_ACCION      => 'MM', 
                                                  UN_MERGEUSING  => MI_MERGEUSING, 
                                                  UN_MERGEENLACE => MI_MERGEENLACE,
                                                  UN_MERGEEXISTE => MI_MERGEEXISTE);   
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
            MI_MSGERROR(1).CLAVE := 'CAMPOS';
            MI_MSGERROR(1).VALOR := 'TIPOCOBRO';
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTUALIZARUSUARIO,
              UN_REEMPLAZOS => MI_MSGERROR
            );
        END;
        BEGIN
          BEGIN
            MI_TABLA := 'SP_COBROSPERSUASIVOS';
            MI_MERGEUSING  := 'SELECT SP_COBROSPERSUASIVOS.COMPANIA
                                     ,SP_COBROSPERSUASIVOS.IDCOBRO
                                     ,SP_USUARIO.ANO
                                     ,SP_USUARIO.PERIODO
                                     ,SP_USUARIO.CICLO
                                     ,SP_USUARIO.CODIGORUTA
                               FROM   SP_COBROSPERSUASIVOS  
                                 INNER JOIN SP_USUARIO 
                                    ON      SP_COBROSPERSUASIVOS.COMPANIA   = SP_USUARIO.COMPANIA 
                                   AND      SP_COBROSPERSUASIVOS.CICLO      = SP_USUARIO.CICLO
                                   AND      SP_COBROSPERSUASIVOS.CODIGORUTA = SP_USUARIO.CODIGORUTA
                               WHERE  SP_USUARIO.COMPANIA            = '''|| UN_COMPANIA ||'''
                                 AND  SP_USUARIO.CICLO               =   '|| UN_CICLO ||'
                                 AND  SP_USUARIO.FECHAPAGOPERPROCESO IS NOT NULL
                                 AND  (SP_USUARIO.TIPOCOBRO          <> '''||MI_RS.CODIGO||'''
                                 AND  SP_COBROSPERSUASIVOS.ESTADO    <> ''C'')';
            MI_MERGEENLACE := '    TABLA.COMPANIA   = VISTA.COMPANIA 
                               AND TABLA.CICLO      = VISTA.CICLO
                               AND TABLA.CODIGORUTA = VISTA.CODIGORUTA';
            MI_MERGEEXISTE := 'UPDATE 
                                 SET TABLA.ESTADO    = ''C''
                                    ,TABLA.ANOCIERRE    = VISTA.ANO
                                    ,TABLA.PERIODOCIERRE    = VISTA.PERIODO
                                    ,TABLA.FECHACIERRE    = SYSDATE';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA, 
                                                  UN_ACCION      => 'MM', 
                                                  UN_MERGEUSING  => MI_MERGEUSING, 
                                                  UN_MERGEENLACE => MI_MERGEENLACE,
                                                  UN_MERGEEXISTE => MI_MERGEEXISTE);   
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
            MI_MSGERROR(1).CLAVE := 'CAMPOS';
            MI_MSGERROR(1).VALOR := 'ESTADO,ANOCIERRE,PERIODOCIERRE,FECHACIERRE';
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTUALIZARCOBROS,
              UN_REEMPLAZOS => MI_MSGERROR
            ); 
        END;
      END IF;
    END LOOP ESTADOSCOBRO;
  END PR_CIERREPERSUASIVO;

  -- 9
  PROCEDURE PR_GUARDARHISTORICOSFACTURA
    /*
      NAME              : PR_GUARDARHISTORICOSFACTURA --> EN ACCESS guardarHistoricosFactura
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MIGRADOR     : 07/02/2017
      TIME              : 08:05 AM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PROCEDIMIENTO QUE REALIZA INSERCIONES EN SP_HISTORICOFACTURA, PARA UNA COMPANIA,
                          UN CICLO, UN AÑO Y UN PERIODO ESPECIFICO.
      PARAMETERS        : UN_COMPANIA => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_CICLO    => CICLO POR EL CUAL SE VA A FILTRAR LA INFORMACION.
                          UN_ANIO     => ANIO DEL QUE SE QUIERE ALMACENAR EL HISTORICO.
                          UN_PERIODO  => PERIODO DEL QUE SE QUIERE ALMACENAR EL HISTORICO.
      MODIFICATIONS     : 
      @NAME:  guardarHistoricosFactura
      @METHOD:  POST
    */
  (
    UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_CICLO         IN PCK_SUBTIPOS.TI_CICLO
   ,UN_ANIO          IN PCK_SUBTIPOS.TI_ANIO
   ,UN_PERIODO       IN PCK_SUBTIPOS.TI_PERIODO
   ,UN_USUARIO       IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_RES720             PCK_SUBTIPOS.TI_LOGICO;
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR; 
    MI_CONT               PCK_SUBTIPOS.TI_ENTERO;
    MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
  BEGIN
    IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                             UN_NOMBRE    => 'APLICA RESOLUCION CRA 720',
                             UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                             UN_FECHA_PAR => SYSDATE) = 'SI' 
       AND UN_ANIO || ',' || UN_PERIODO >= PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                                            UN_NOMBRE    => 'PERIODO INICIO RES 720 ASEO',
                                                                            UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                                            UN_FECHA_PAR => SYSDATE)
       AND UN_CICLO <> NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                 UN_NOMBRE    => 'RES 720 CICLO EXCLUIDO',
                                                 UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                 UN_FECHA_PAR => SYSDATE),0) THEN
      MI_RES720 := -1;
    ELSE
      MI_RES720 := 0;
    END IF;
    BEGIN
      EXECUTE IMMEDIATE 'SELECT COUNT (1)
                         FROM   SP_PARAMETROFACTURACION
                         WHERE  COMPANIA = '''||UN_COMPANIA||'''
                           AND  CICLO    = '  ||UN_CICLO||'
                           AND  ANO      = '  ||UN_ANIO||'
                           AND  PERIODO  = '''||UN_PERIODO||''''
      INTO MI_CONT;
      IF MI_CONT = 0 THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END IF;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CICLO';
        MI_MSGERROR(1).VALOR := UN_CICLO;
        MI_MSGERROR(2).CLAVE := 'ANIO';
        MI_MSGERROR(2).VALOR := UN_ANIO;
        MI_MSGERROR(3).CLAVE := 'PERIODO';
        MI_MSGERROR(3).VALOR := UN_PERIODO;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD   => SQLCODE,
          UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_PARAMETROSFACTURAS,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;  
    <<PARFACTURACION>>
    FOR MI_RSPARAMETRO IN (SELECT FECHALIMITE1
                                 ,FECHALIMITE2
                                 ,PAGO1
                                 ,PAGO2
                                 ,OBSERVACION1
                                 ,FECHAEXPEDICION
                                 ,FECHASUSPENSION
                           FROM   SP_PARAMETROFACTURACION
                           WHERE  COMPANIA = UN_COMPANIA
                             AND  CICLO    = UN_CICLO
                             AND  ANO      = UN_ANIO
                             AND  PERIODO  = UN_PERIODO)
    LOOP
      BEGIN
        BEGIN
          MI_TABLA := 'SP_HISTORICOFACTURA';
          MI_CAMPOS := 'COMPANIA,CICLO,CODIGORUTA,ANO,PERIODO,ESTADO,NIT,NOMBRES
                       ,CODIGOINTERNO,DIRTECNICA,DIRCORRESPONDENCIA,USO,ESTRATO
                       ,ACUEDUCTO,ALCANTARILLADO,ASEO,LECTURA1,LECTURA2,LECTURA3
                       ,LECTURA4,LECTURA5,LECTURA6,LECTURA,LECTURAAFORO,CONSUMO1
                       ,CONSUMO2,CONSUMO3,CONSUMO4,CONSUMO5,CONSUMO6,CONSUMO
                       ,CONSUMOPROM,DEUDA,LECTURAINICIAL,TIPOLECTURA,FACTURA
                       ,TOTALASEO,FECHALIMITE,FECHALIMITE1,FECHALIMITE2,PAGO1
                       ,PAGO2,TOTFACTURAPERANTERIOR,MEDIDOR,ABONOACT,TARIFABASICO
                       ,TARSOBREBAS,TARSUBBAS,TARIFAM3BASICOAC,RAN1,RAN2,RAN3
                       ,TOTFACTURAPERACTUAL,EMPRESAASEOEXT,NOMBREUSO,CODIGOCATASTRAL
                       ,PERIODOSATRASO,FRE_RECO_SYS,FREC_BARRI_SYS,PESOASEO_SYS
                       ,CONSUMOBASICO,CONSUMOCOMPLEMENTARIO,CONSUMOSUNTUARIO
                       ,PINTA,VALASEO1_SYS,VALASEO2_SYS,VALASEO3_SYS
                       ,VALASEO4_SYS,VALASEO5_SYS,VALASEO6_SYS,PROBLEMA
                       ,CARGOFIJO,SUBFIJO,SOBREFIJO,SUBBASICO,SOBREBASICO
                       ,TARIFACOMPLEMENTARIO,SUBCOMPLEMENTARIO,SOBRECOMPLEMENTARIO
                       ,TARIFASUNTUARIO,SUBSUNTUARIO,SOBRESUNTUARIO
                       ,TARIFAM3SUNTUARIOAC,SUBALCCOMPLEMENTARIO,SUBALCBASICO
                       ,SUBALCSUNTUARIO,SOBREALCSUNTUARIO,SOBREALCCOMPLEMENTARIO
                       ,ALCBASICO,SOBREALCBASICO,ALCCOMPLEMENTARIO,TOTALACUEDUCTO
                       ,TOTALALCANTARILLADO,SUBACUEDUCTO,SUBALCANTARILLADO
                       ,VASEOUNICO,VASEODOMICILIARIO,VASEOBARRIDO,VASEOCONSUMO
                       ,VDESHABITADO,VTRAMOEXCEDENTE,TOTALDEUDA,RECAUDADOANTERIOR
                       ,RECARGOASEO,RECARGOALC,RECARGOACUEDUCTO,CODIGOANTERIOR
                       ,CODIGOPRIMERO,FECHASPERIODO,PRIMERAPELLIDO,SEGUNDOAPELLIDO
                       ,CONSUMOBASICOALC,CONSUMOSUNTUARIOALC,CONSUMOCOMPLEMENTARIOALC
                       ,SUBBARR_LIM,SOBBARR_LIM,SUBDISP_FINAL,SOBDISP_FINAL
                       ,SUBMANEJO_REC,SUBRECOLECCION,SOBRECOLECCION,SUBDESHABITADO
                       ,SOBDESHABITADO,SUBASEO,SOBREASEO,SOBMANEJO_REC,OBSERVACION1
                       ,FECHAEXPEDICION,FECHASUSPENSION,CODIGO,SOBREACUEDUCTO
                       ,SOBREALCANTARILLADO,NOTADEBITO,VALORTASAAMBIENTALAC
                       ,VALORTASAAMBIENTALALC,ALCSUNTUARIO,FIJOALCANTARILLADO
                       ,SUBALCFIJO,SOBREALCFIJO,INDDESHABITADO,ASEODESHABITADOS
                       ,ASEOUNICO,SOBREASEOUNICO,ASEODOMICILIARIO
                       ,SOBREASEODOMICILIARIO,SUBASEODOMICILIARIO,ASEOBARRIDO
                       ,SUBASEOBARRIDO,SOBREASEOBARRIDO,ASEOCONSUMO,SUBASEOCONSUMO
                       ,SOBREASEOCONSUMO,PORC_SUBSIDIO,PORC_CONTRIBUCION
                       ,NOUNDMULTIUSUARIO,SUBASEOUNICO,CONPROM,CONREAL,MATRICULA
                       ,FECHAEXPFACTURA,FECHALECANTERIOR,FECHALECACTUAL
                       ,TARIFAM3COMPLEMENTARIOAC,TARIFAM3BASICOAL
                       ,TARIFAM3COMPLEMENTARIOAL,TARIFAM3SUNTUARIOAL
                       ,PESOASEO,FRECUENCIARECOLECCION,FACTURAALC1,FACTURAALC2
                       ,FACTURAALC3,FACTURAASE1,FACTURAASE2,FACTURAASE3
                       ,FACTURAAC1,FACTURAAC2,FACTURAAC3,SUBCLUS_720,SOBCLUS_720
                       ,VALASEOCTL_720,SUBCTL_720,SOBCTL_720,VALASEOVBA_720
                       ,SUBVBA_720,SOBVBA_720,TAFNA_720,TAFA_720,FECHALECTURAAFORO
                       ,AFORADOR,DATE_CREATED,CREATED_BY ';

          MI_VALORES := 'SELECT SP_USUARIO.COMPANIA,SP_USUARIO.CICLO
                               ,SP_USUARIO.CODIGORUTA,SP_USUARIO.ANO
                               ,SP_USUARIO.PERIODO,SP_USUARIO.ESTADO
                               ,SP_USUARIO.NIT,SP_USUARIO.NOMBRES
                               ,SP_USUARIO.CODIGOINTERNO,SP_USUARIO.DIRTECNICA
                               ,SP_USUARIO.DIRCORRESPONDENCIA,SP_USUARIO.USO
                               ,SP_USUARIO.ESTRATO,SP_USUARIO.ACUEDUCTO
                               ,SP_USUARIO.ALCANTARILLADO,SP_USUARIO.ASEO
                               ,SP_USUARIO.LECTURA1,SP_USUARIO.LECTURA2
                               ,SP_USUARIO.LECTURA3,SP_USUARIO.LECTURA4
                               ,SP_USUARIO.LECTURA5,SP_USUARIO.LECTURA6
                               ,SP_USUARIO.LECTURA,SP_USUARIO.LECTURAAFORO
                               ,SP_USUARIO.CONSUMO1,SP_USUARIO.CONSUMO2
                               ,SP_USUARIO.CONSUMO3,SP_USUARIO.CONSUMO4
                               ,SP_USUARIO.CONSUMO5,SP_USUARIO.CONSUMO6
                               ,SP_USUARIO.CONSUMO,SP_USUARIO.CONSUMOPROM
                               ,SP_USUARIO.DEUDA,SP_USUARIO.LECTURAINICIAL
                               ,SP_USUARIO.TIPOLECTURA,SP_USUARIO.FACTURA
                               ,SP_USUARIO.TOTALASEO,TO_CHAR(SP_USUARIO.FECHALIMITE, ''DD/MM/YYYY'')
                               ,'''||TO_CHAR(MI_RSPARAMETRO.FECHALIMITE1, 'DD/MM/YYYY')||'''
                               ,'''||TO_CHAR(MI_RSPARAMETRO.FECHALIMITE2, 'DD/MM/YYYY')||'''
                               ,'''||MI_RSPARAMETRO.PAGO1||'''
                               ,'''||MI_RSPARAMETRO.PAGO2||'''
                               ,SP_USUARIO.TOTFACTURAPERANTERIOR,SP_USUARIO.MEDIDOR
                               ,SP_USUARIO.ABONOACT,SP_TARIFAS.TARIFABASICO
                               ,SP_TARIFAS.SOBREBASICO TARSOBREBAS,SP_TARIFAS.SUBBASICO TARSUBBAS
                               ,SP_USUARIO.TARIFAM3BASICOAC,SP_TARIFAS.CONSUMOBASICO RAN1
                               ,SP_TARIFAS.CONSUMOCOMPLEMENTARIO RAN2,SP_TARIFAS.CONSUMOSUNTUARIO RAN3
                               ,SP_USUARIO.TOTFACTURAPERACTUAL,SP_USUARIO.EMPRESAASEOEXT
                               ,SP_USOS.NOMBRE NOMBREUSO,SP_USUARIO.CODIGOCATASTRAL
                               ,SP_USUARIO.PERIODOSATRASO,SP_USUARIO.FREC_RECO FRE_RECO_SYS
                               ,SP_USUARIO.FREC_BARRI FREC_BARRI_SYS,SP_USUARIO.PESOASEO PESOASEO_SYS
                               ,SP_USUARIO.CONSUMOBASICO,SP_USUARIO.CONSUMOCOMPLEMENTARIO
                               ,SP_USUARIO.CONSUMOSUNTUARIO,0 PINTA
                               ,SP_USUARIO.VALASEO1 VALASEO1_SYS,SP_USUARIO.VALASEO2 VALASEO2_SYS
                               ,SP_USUARIO.VALASEO3 VALASEO3_SYS,SP_USUARIO.VALASEO4 VALASEO4_SYS
                               ,SP_USUARIO.VALASEO5 VALASEO5_SYS,SP_USUARIO.VALASEO6 VALASEO6_SYS
                               ,'' '' AS PROBLEMA,SP_TARIFAS.CARGOFIJO,SP_TARIFAS.SUBFIJO
                               ,SP_TARIFAS.SOBREFIJO,SP_TARIFAS.SUBBASICO,SP_TARIFAS.SOBREBASICO
                               ,SP_TARIFAS.TARIFACOMPLEMENTARIO,SP_TARIFAS.SUBCOMPLEMENTARIO
                               ,SP_TARIFAS.SOBRECOMPLEMENTARIO,SP_TARIFAS.TARIFASUNTUARIO
                               ,SP_TARIFAS.SUBSUNTUARIO,SP_TARIFAS.SOBRESUNTUARIO
                               ,SP_USUARIO.TARIFAM3SUNTUARIOAC,SP_TARIFAS.SUBALCCOMPLEMENTARIO
                               ,SP_TARIFAS.SUBALCBASICO,SP_TARIFAS.SUBALCSUNTUARIO
                               ,SP_TARIFAS.SOBREALCSUNTUARIO,SP_TARIFAS.SOBREALCCOMPLEMENTARIO
                               ,SP_TARIFAS.ALCBASICO,SP_TARIFAS.SOBREALCBASICO
                               ,SP_TARIFAS.ALCCOMPLEMENTARIO,SP_USUARIO.TOTALACUEDUCTO
                               ,SP_USUARIO.TOTALALCANTARILLADO,SP_USUARIO.SUBACUEDUCTO
                               ,SP_USUARIO.SUBALCANTARILLADO
                               ,'||CASE 
                                     WHEN MI_RES720 = -1
                                     THEN 'SP_USUARIO.VALASEOCBLS_720,'
                                     ELSE 'SP_USUARIO.VASEOUNICO,'
                                   END 
                                 ||CASE 
                                     WHEN MI_RES720 = -1
                                     THEN 'SP_USUARIO.VALASEOCDF_720,'
                                     ELSE 'SP_USUARIO.VASEODOMICILIARIO,'
                                   END
                                 ||CASE 
                                     WHEN MI_RES720 = -1
                                     THEN 'SP_USUARIO.VALASEOCCS_720,'
                                     ELSE 'SP_USUARIO.VASEOBARRIDO,' 
                                   END
                                 ||CASE 
                                     WHEN MI_RES720 = -1 
                                     THEN 'SP_USUARIO.VALASEOCRT_720,' 
                                     ELSE 'SP_USUARIO.VASEOCONSUMO,' 
                                   END
                                 ||'SP_USUARIO.VDESHABITADO,'
                                 ||CASE 
                                     WHEN MI_RES720 = -1
                                     THEN 'SP_USUARIO.VALASEOCLUS_720,' 
                                     ELSE 'SP_USUARIO.VTRAMOEXCEDENTE,' 
                                   END
                                 ||'SP_USUARIO.TOTALDEUDA,SP_USUARIO.RECAUDADOANTERIOR
                               ,SP_USUARIO.RECARGOASEO,SP_USUARIO.RECARGOALC,SP_USUARIO.RECARGOACUEDUCTO
                               ,SP_USUARIO.CODIGOANTERIOR
                               ,SP_USUARIO.CODIGOINTERNO|| ''-'' ||SP_USUARIO.CODIGOANTERIOR CODIGOPRIMERO
                               ,TO_CHAR(SP_USUARIO.FECHALECANTERIOR,''DD/MM/YYYY'')|| ''-'' ||TO_CHAR(SP_USUARIO.FECHALECACTUAL,''DD/MM/YYYY'') FECHASPERIODO
                               ,SP_USUARIO.PRIMERAPELLIDO,SP_USUARIO.SEGUNDOAPELLIDO
                               ,SP_USUARIO.CONSUMOBASICOALC,SP_USUARIO.CONSUMOSUNTUARIOALC
                               ,SP_USUARIO.CONSUMOCOMPLEMENTARIOALC,SP_USUARIO.SUBBARR_LIM
                               ,SP_USUARIO.SOBBARR_LIM,SP_USUARIO.SUBDISP_FINAL
                               ,SP_USUARIO.SOBDISP_FINAL,SP_USUARIO.SUBMANEJO_REC
                               ,SP_USUARIO.SUBRECOLECCION,SP_USUARIO.SOBRECOLECCION
                               ,SP_USUARIO.SUBDESHABITADO,SP_USUARIO.SOBDESHABITADO
                               ,SP_USUARIO.SUBASEO,SP_USUARIO.SOBREASEO
                               ,SP_USUARIO.SOBMANEJO_REC,'''||MI_RSPARAMETRO.OBSERVACION1||''' OBSERVACION1
                               ,'''||TO_CHAR(NVL(MI_RSPARAMETRO.FECHAEXPEDICION, SYSDATE), 'DD/MM/YYYY') ||''' FECHAEXPEDICION
                               ,'''||TO_CHAR(NVL(MI_RSPARAMETRO.FECHASUSPENSION, SYSDATE), 'DD/MM/YYYY') ||''' FECHASUSPENSION
                               ,SP_USUARIO.COMPANIA|| ''-'' ||SP_USUARIO.CICLO|| ''-'' ||SP_USUARIO.CODIGORUTA CODIGO
                               ,SP_USUARIO.SOBREACUEDUCTO,SP_USUARIO.SOBREALCANTARILLADO
                               ,SP_USUARIO.NOTADEBITO,SP_TARIFAS.VALORTASAAMBIENTALAC
                               ,SP_TARIFAS.VALORTASAAMBIENTALALC,SP_USUARIO.ALCSUNTUARIO
                               ,SP_TARIFAS.FIJOALCANTARILLADO,SP_TARIFAS.SUBALCFIJO
                               ,SP_TARIFAS.SOBREALCFIJO,SP_USUARIO.INDDESHABITADO
                               ,SP_TARIFAS.ASEODESHABITADOS,SP_TARIFAS.ASEOUNICO
                               ,'||CASE 
                                     WHEN MI_RES720 = -1
                                     THEN 'SP_USUARIO.SOBCBLS_720,'
                                     ELSE 'SP_TARIFAS.SOBREASEOUNICO,' 
                                   END
                                 ||'SP_TARIFAS.ASEODOMICILIARIO
                               ,'||CASE 
                                     WHEN MI_RES720 = -1
                                     THEN 'SP_USUARIO.SOBCDF_720,'
                                     ELSE 'SP_TARIFAS.SOBREASEODOMICILIARIO,' 
                                   END
                                 ||CASE 
                                     WHEN MI_RES720 = -1
                                     THEN 'SP_USUARIO.SUBCDF_720,'
                                     ELSE 'SP_TARIFAS.SUBASEODOMICILIARIO,'
                                   END
                                 ||'SP_TARIFAS.ASEOBARRIDO
                               ,'||CASE 
                                     WHEN MI_RES720 = -1
                                     THEN 'SP_USUARIO.SUBCCS_720,'
                                     ELSE 'SP_TARIFAS.SUBASEOBARRIDO,' 
                                   END
                                 ||CASE 
                                     WHEN MI_RES720 = -1
                                     THEN 'SP_USUARIO.SOBCCS_720,'
                                     ELSE 'SP_TARIFAS.SOBREASEOBARRIDO,' 
                                   END
                                 ||'SP_TARIFAS.ASEOCONSUMO
                               ,'||CASE 
                                     WHEN MI_RES720 = -1
                                     THEN 'SP_USUARIO.SUBCRT_720,'
                                     ELSE 'SP_TARIFAS.SUBASEOCONSUMO,' 
                                   END
                                 ||CASE 
                                     WHEN MI_RES720 = -1
                                     THEN 'SP_USUARIO.SOBCRT_720,'
                                     ELSE 'SP_TARIFAS.SOBREASEOCONSUMO,' 
                                   END
                                 ||'SP_TARIFAS.PORC_SUBSIDIO,SP_TARIFAS.PORC_CONTRIBUCION
                               ,SP_USUARIO.NOUNDMULTIUSUARIO
                               ,'||CASE 
                                     WHEN MI_RES720 = -1
                                     THEN 'SP_USUARIO.SUBCBLS_720,'
                                     ELSE 'SP_TARIFAS.SUBASEOUNICO,' 
                                   END
                                 ||'CASE 
                                      WHEN TIPOCALCULO = ''P''
                                      THEN ''X''
                                      ELSE '' ''
                                    END CONPROM
                               ,CASE
                                  WHEN TIPOCALCULO = ''C''
                                  THEN ''X''
                                  ELSE '' ''
                                END CONREAL
                               ,SP_USUARIO.MATRICULA,SP_USUARIO.FECHAEXPFACTURA
                               ,SP_USUARIO.FECHALECANTERIOR, SP_USUARIO.FECHALECACTUAL
                               ,SP_USUARIO.TARIFAM3COMPLEMENTARIOAC,SP_USUARIO.TARIFAM3BASICOAL
                               ,SP_USUARIO.TARIFAM3COMPLEMENTARIOAL,SP_USUARIO.TARIFAM3SUNTUARIOAL
                               ,SP_USUARIO.PESOASEO,SP_USUARIO.FRECUENCIARECOLECCION
                               ,SP_USUARIO.FACTURAALC1,SP_USUARIO.FACTURAALC2
                               ,SP_USUARIO.FACTURAALC3,SP_USUARIO.FACTURAASE1
                               ,SP_USUARIO.FACTURAASE2,SP_USUARIO.FACTURAASE3
                               ,SP_USUARIO.FACTURAAC1,SP_USUARIO.FACTURAAC2
                               ,SP_USUARIO.FACTURAAC3,SP_USUARIO.SUBCLUS_720
                               ,SP_USUARIO.SOBCLUS_720,SP_USUARIO.VALASEOCTL_720
                               ,SP_USUARIO.SUBCTL_720,SP_USUARIO.SOBCTL_720
                               ,SP_USUARIO.VALASEOVBA_720,SP_USUARIO.SUBVBA_720
                               ,SP_USUARIO.SOBVBA_720,SP_USUARIO.TAFNA_720
                               ,SP_USUARIO.TAFA_720,SP_USUARIO.FECHALECTURAAFORO
                               ,SP_USUARIO.AFORADOR
                               ,SYSDATE
                               ,'''||UN_USUARIO||'''
                         FROM   SP_USUARIO 
                           INNER JOIN SP_TARIFAS 
                              ON      SP_USUARIO.COMPANIA = SP_TARIFAS.COMPANIA
                             AND      SP_USUARIO.ESTRATO  = SP_TARIFAS.ESTRATO 
                             AND      SP_USUARIO.USO      = SP_TARIFAS.USO 
                             AND      SP_USUARIO.ANO      = SP_TARIFAS.ANO 
                             AND      SP_USUARIO.PERIODO  = SP_TARIFAS.PERIODO
                           INNER JOIN SP_USOS 
                              ON      SP_USUARIO.COMPANIA = SP_USOS.COMPANIA
                             AND      SP_USUARIO.USO      = SP_USOS.CODIGO
                           INNER JOIN SP_ESTRATOS 
                              ON      SP_USUARIO.COMPANIA = SP_ESTRATOS.COMPANIA
                             AND      SP_USUARIO.ESTRATO  = SP_ESTRATOS.CODIGO
                             AND      SP_USUARIO.USO      = SP_ESTRATOS.USO 
                           INNER JOIN COMPANIA 
                              ON      SP_USUARIO.COMPANIA = COMPANIA.CODIGO
                           LEFT JOIN SP_HISTORICOFACTURA 
                             ON      SP_USUARIO.COMPANIA   = SP_HISTORICOFACTURA.COMPANIA
                            AND      SP_USUARIO.CICLO      = SP_HISTORICOFACTURA.CICLO
                            AND      SP_USUARIO.CODIGORUTA = SP_HISTORICOFACTURA.CODIGORUTA
                            AND      SP_USUARIO.ANO        = SP_HISTORICOFACTURA.ANO
                            AND      SP_USUARIO.PERIODO    = SP_HISTORICOFACTURA.PERIODO
                         WHERE  SP_USUARIO.COMPANIA          = '''||UN_COMPANIA||''' 
                           AND  SP_USUARIO.CICLO             = '  ||UN_CICLO||'
                           AND  SP_HISTORICOFACTURA.COMPANIA IS NULL';
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA, 
                                                UN_ACCION  => 'IS', 
                                                UN_CAMPOS  => MI_CAMPOS, 
                                                UN_VALORES => MI_VALORES);    
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN               
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD   => SQLCODE,
            UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_HISTORICOFACTURAS
          ); 
      END;
    END LOOP PARFACTURACION;
  END PR_GUARDARHISTORICOSFACTURA;

  -- 10
  PROCEDURE PR_AUDITORIADESVIO
    /*
      NAME              : PR_AUDITORIADESVIO --> EN ACCESS AuditoriaDesvio
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MIGRADOR     : 09/02/2017
      TIME              : 02:30 PM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PROCEDIMIENTO QUE REALIZA UN SEGUIMIENTO A LA ACTIVACION
                          DE LA VERIFICACION DEL DESVIO DE LAS FACTURAS, HACIENDO 
                          UNA INSERCION EN SP_AUDIDESVIO.
      PARAMETERS        : UN_COMPANIA    => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_USUARIO     => USUARIO ACTIVO.
                          UN_PROCESO     => PROCESO QUE SE QUIERE REGISTRAR EN LA AUDITORIA.
                          UN_ANIO        => ANIO EN EL QUE SE QUIERE REALIZAR EL REGISTRO
                                            DE LA AUDITORIA.
                          UN_PERIODO     => PERIODO EN EL QUE SE QUIERE REALIZAR EL REGISTRO
                                            DE LA AUDITORIA.
                          UN_CICLO       => CICLO EN EL QUE SE REALIZA LA AUDITORIA.
                          UN_CODINTERNO  => CODIGO INTERNO DEL USUARIO.
                          UN_DESCRIPCION => DESCRIPCION DEL PROCESO QUE SE QUIERE
                                            REGISTRAR.
      MODIFICATIONS     : 
      @NAME:  auditarDesvio
      @METHOD:  POST
    */ 
    (
    UN_COMPANIA       IN  PCK_SUBTIPOS.TI_COMPANIA
   ,UN_USUARIO        IN  PCK_SUBTIPOS.TI_USUARIO
   ,UN_PROCESO        IN  VARCHAR2 -- VARCHAR2 debido a que no existe un tipo de dato con las mismas características en el paquete PCK_SUBTIPOS.
   ,UN_ANIO           IN  PCK_SUBTIPOS.TI_ANIO
   ,UN_PERIODO        IN  PCK_SUBTIPOS.TI_PERIODO
   ,UN_CICLO          IN  PCK_SUBTIPOS.TI_CICLO
   ,UN_CODINTERNO     IN  SP_USUARIO.CODIGOINTERNO%TYPE
   ,UN_DESCRIPCION    IN  VARCHAR2 -- VARCHAR2 debido a que no existe un tipo de dato con las mismas características en el paquete PCK_SUBTIPOS.
  )
  AS
    MI_TABLA          PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
  BEGIN
    BEGIN
      MI_TABLA := 'SP_AUDIDESVIO';
      MI_CAMPOS := 'COMPANIA
                   ,USUARIO
                   ,PROCESO
                   ,FECHA
                   ,HORA
                   ,ANO
                   ,PERIODO
                   ,CICLO
                   ,CODINTERNO
                   ,RESUMEN';
      MI_VALORES := ''''||UN_COMPANIA||'''
                    ,'''||UN_USUARIO||'''
                    ,'''||UN_PROCESO||'''
                    ,SYSDATE
                    ,SYSDATE
                    ,'||UN_ANIO||'
                    ,'''||UN_PERIODO||'''
                    ,'||UN_CICLO||'
                    ,'''||UN_CODINTERNO||'''
                    ,'''||UN_DESCRIPCION||'''';
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA, 
                                            UN_ACCION  => 'I', 
                                            UN_CAMPOS  => MI_CAMPOS, 
                                            UN_VALORES => MI_VALORES);    
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
    END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN               
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD    => SQLCODE,
        UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_INSERTARAUDIDESVIO
      ); 
  END PR_AUDITORIADESVIO;

  -- 11
  PROCEDURE PR_SUMPESOASEO
    /*
      NAME              : PR_SUMPESOASEO --> EN ACCESS SumPesoAseo
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MIGRADOR     : 06/02/2017
      TIME              : 08:25 PM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PROCEDIMIENTO QUE ACTUALIZA EL PESOASEO EN SP_USUARIO, PARA UN AÑO Y PERIODO ESPECIFICO.
      PARAMETERS        : UN_COMPANIA    => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_CICLO       => CICLO PARA EL QUE SE VA A REALIZAR LA ACTUALIZACION.
                          UN_ANIO        => AÑO PARA EL QUE SE VA A REALIZAR LA ACTUALIZACION.
                          UN_PERIODO     => PERIODO PARA EL QUE SE VA A REALIZAR LA ACTUALIZACION.
      MODIFICATIONS     : 
      @NAME:  sumarPesoAseo
      @METHOD:  PUT
    */ 
  (
    UN_COMPANIA     IN    PCK_SUBTIPOS.TI_COMPANIA
   ,UN_CICLO        IN    PCK_SUBTIPOS.TI_CICLO
   ,UN_ANIO         IN    PCK_SUBTIPOS.TI_ANIO
   ,UN_PERIODO      IN    PCK_SUBTIPOS.TI_PERIODO
  )
  AS
    MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
    MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    <<DETALLESDISPOSICION>>
    FOR MI_RS IN (SELECT   SP_DETALLEDISPOSICION.CODIGORUTA
                          ,SUM(SP_DETALLEDISPOSICION.PESOASEO) SUMPESO
                  FROM     SP_DETALLEDISPOSICION
                  WHERE    COMPANIA = UN_COMPANIA 
                    AND    CICLO    = UN_CICLO
                    AND    ANO      = UN_ANIO
                    AND    PERIODO  = UN_PERIODO
                  GROUP BY SP_DETALLEDISPOSICION.CODIGORUTA)
    LOOP
      BEGIN
        BEGIN
          MI_TABLA     := 'SP_USUARIO';
          MI_CAMPOS    := 'SP_USUARIO.PESOASEO     = '||MI_RS.SUMPESO||'';
          MI_CONDICION := '    SP_USUARIO.COMPANIA   = '''|| UN_COMPANIA ||'''
                           AND SP_USUARIO.CICLO      = '  || UN_CICLO ||'
                           AND SP_USUARIO.CODIGORUTA = '''|| MI_RS.CODIGORUTA ||'''';
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA,
                                        UN_ACCION    => 'M',
                                        UN_CAMPOS    => MI_CAMPOS,
                                        UN_CONDICION => MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CAMPOS';
          MI_MSGERROR(1).VALOR := 'COMPANIA= '''|| UN_COMPANIA ||
                                  ''', CICLO= '|| UN_CICLO ||
                                  ' y CODIGORUTA= '''|| MI_RS.CODIGORUTA ||'''';
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD   => SQLCODE,
            UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_ACTUALIZARUSUARIO,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END;
    END LOOP DETALLESDISPOSICION;
  END PR_SUMPESOASEO;

  -- 12
  PROCEDURE PR_REVISAFACTURADOSABONOS
    /*
      NAME              : PR_REVISAFACTURADOSABONOS --> EN ACCESS RevisaFacturadosAbonos
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MIGRADOR     : 10/02/2017
      TIME              : 12:30 PM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PROCEDIMIENTO QUE ACTUALIZA EL VALOR DEL ABONO ACTUAL EN SP_FACTURADO.
      PARAMETERS        : UN_COMPANIA    => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_CICLO       => CICLO PARA EL QUE SE VA A REALIZAR LA ACTUALIZACION.
                          UN_ANIO        => AÑO PARA EL QUE SE VA A REALIZAR LA ACTUALIZACION.
                          UN_PERIODO     => PERIODO PARA EL QUE SE VA A REALIZAR LA ACTUALIZACION.
      MODIFICATIONS     : 
      @NAME:  revisarFacturadosAbonos
      @METHOD:  PUT
    */ 
  (
    UN_COMPANIA         IN  PCK_SUBTIPOS.TI_COMPANIA
   ,UN_CICLO            IN  PCK_SUBTIPOS.TI_CICLO
   ,UN_ANIO             IN  PCK_SUBTIPOS.TI_ANIO
   ,UN_PERIODO          IN  PCK_SUBTIPOS.TI_PERIODO
   ,UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO

  )
  AS
    MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
    MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    <<FACTURAABONOS>>
    FOR MI_RS IN (WITH ABONOSPERIODO AS (
                    SELECT SP_FACTURADO.COMPANIA
                          ,SP_FACTURADO.CICLO
                          ,SP_FACTURADO.CODIGORUTA
                          ,SP_FACTURADO.ANO
                          ,SP_FACTURADO.PERIODO
                          ,SP_FACTURADO.CONCEPTO
                          ,SP_FACTURADO.VALOR_FACTURADO
                    FROM   SP_CICLO 
                      INNER JOIN SP_FACTURADO 
                         ON      SP_CICLO.PERIODO  = SP_FACTURADO.PERIODO 
                        AND      SP_CICLO.ANO      = SP_FACTURADO.ANO
                        AND      SP_CICLO.NUMERO   = SP_FACTURADO.CICLO
                        AND      SP_CICLO.COMPANIA = SP_FACTURADO.COMPANIA
                    WHERE  SP_FACTURADO.CONCEPTO           = 49 
                      AND  SP_FACTURADO.VALOR_FACTURADO <> 0
                  )
                  SELECT   SP_FACTURADO.COMPANIA
                          ,SP_FACTURADO.CICLO
                          ,SP_FACTURADO.CODIGORUTA
                          ,SP_FACTURADO.ANO
                          ,SP_FACTURADO.PERIODO
                          ,ABONOSPERIODO.VALOR_FACTURADO
                  FROM     ABONOSPERIODO 
                    INNER JOIN SP_FACTURADO 
                       ON      ABONOSPERIODO.PERIODO    = SP_FACTURADO.PERIODO
                      AND      ABONOSPERIODO.ANO        = SP_FACTURADO.ANO 
                      AND      ABONOSPERIODO.CODIGORUTA = SP_FACTURADO.CODIGORUTA
                      AND      ABONOSPERIODO.CICLO      = SP_FACTURADO.CICLO 
                      AND      ABONOSPERIODO.COMPANIA   = SP_FACTURADO.COMPANIA
                  WHERE   (SP_FACTURADO.CONCEPTO BETWEEN 1 AND 48 
                     OR    SP_FACTURADO.CONCEPTO BETWEEN 201 AND 220 
                     OR    SP_FACTURADO.CONCEPTO BETWEEN 246 AND 249) 
                    AND    SP_FACTURADO.CONCEPTO NOT IN (12,17) 
                    AND    SP_FACTURADO.COMPANIA = UN_COMPANIA
                    AND    SP_FACTURADO.CICLO    = UN_CICLO
                    AND    SP_FACTURADO.ANO      = UN_ANIO
                    AND    SP_FACTURADO.PERIODO  = UN_PERIODO
                  GROUP BY SP_FACTURADO.COMPANIA
                          ,SP_FACTURADO.CICLO
                          ,SP_FACTURADO.CODIGORUTA
                          ,SP_FACTURADO.ANO
                          ,SP_FACTURADO.PERIODO
                          ,ABONOSPERIODO.VALOR_FACTURADO
                  HAVING SUM(SP_FACTURADO.VALORABONOACT + SP_FACTURADO.VALORABONOANT) <> -ABONOSPERIODO.VALOR_FACTURADO)
    LOOP
      <<ABONOS>>
      FOR MI_RS1 IN (SELECT   SP_D_ABONOS.COMPANIA
                             ,SP_D_ABONOS.CICLO
                             ,SP_D_ABONOS.CODIGORUTA
                             ,SP_D_ABONOS.ANO
                             ,SP_D_ABONOS.PERIODO
                             ,SP_D_ABONOS.CONCEPTO
                             ,SUM(SP_D_ABONOS.VALORACT) SUMADEVALORACT
                             ,SUM(SP_D_ABONOS.VALORANT) SUMADEVALORANT
                     FROM     SP_D_ABONOS
                     WHERE    SP_D_ABONOS.COMPANIA   = UN_COMPANIA 
                       AND    SP_D_ABONOS.CICLO      = UN_CICLO 
                       AND    SP_D_ABONOS.CODIGORUTA = MI_RS.CODIGORUTA 
                       AND    SP_D_ABONOS.ANO        = MI_RS.ANO
                       AND    SP_D_ABONOS.PERIODO    = MI_RS.PERIODO
                       AND   (SP_D_ABONOS.CONCEPTO   BETWEEN 1 AND 49 
                        OR    SP_D_ABONOS.CONCEPTO   BETWEEN 201 AND 220 
                        OR    SP_D_ABONOS.CONCEPTO   BETWEEN 246 AND 249)
                     GROUP BY SP_D_ABONOS.COMPANIA
                             ,SP_D_ABONOS.CICLO
                             ,SP_D_ABONOS.CODIGORUTA
                             ,SP_D_ABONOS.ANO
                             ,SP_D_ABONOS.PERIODO
                             ,SP_D_ABONOS.CONCEPTO)
      LOOP
        BEGIN
          BEGIN
            MI_TABLA    :='SP_FACTURADO';
            MI_CAMPOS   :='VALORABONOACT  = '||MI_RS1.SUMADEVALORACT||',
                           VALORABONOANT  = '||MI_RS1.SUMADEVALORANT||',
                           MODIFIED_BY    = '''||UN_USUARIO||''',
                           DATE_MODIFIED  = SYSDATE';

            MI_CONDICION:='   COMPANIA = '''||UN_COMPANIA||''' 
                          AND CICLO      = '||UN_CICLO||'
                          AND CODIGORUTA = '''||MI_RS.CODIGORUTA||'''
                          AND ANO        = '||MI_RS.ANO||'
                          AND PERIODO    = '''||MI_RS.PERIODO||'''
                          AND CONCEPTO   = '||MI_RS1.CONCEPTO;
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                  UN_ACCION    => 'M',      
                                                  UN_CAMPOS    => MI_CAMPOS,      
                                                  UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
            MI_MSGERROR(1).CLAVE := 'CAMPOS';
            MI_MSGERROR(1).VALOR := 'COMPANIA= '''||UN_COMPANIA||
                                    ''';CICLO= '||UN_CICLO||
                                    ',CODIGORUTA= '''||MI_RS.CODIGORUTA||
                                    ''',ANO= '||MI_RS.ANO||
                                    ',PERIODO= '''||MI_RS.PERIODO||
                                    ''' y CONCEPTO= '||MI_RS1.CONCEPTO;
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD   => SQLCODE,
              UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_ACTUALIZAFACTURADO,
              UN_REEMPLAZOS => MI_MSGERROR
            );
        END;
      END LOOP ABONOS;
    END LOOP FACTURAABONOS;
  END PR_REVISAFACTURADOSABONOS;

  -- 13
  PROCEDURE PR_FACTURADOSNULOS 
    /*
        NAME              : PR_FACTURADOSNULOS --> EN ACCESS FacturadosNulos
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICEÑO
        DATE MIGRADOR     : 10/02/2017
        TIME              : 02:20 PM
        SOURCE MODULE     : SERVICIOS PUBLICOS
        MODIFIER          : 
        DATE MODIFIED     : 
        TIME              : 
        DESCRIPTION       : PROCEDIMIENTO EN EL QUE SE REALIZA INSERCION EN SP_FACTURADO, EN CASO
                            DE QUE EXISTAN FACTURAS CON COMPANIA NULA.
        PARAMETERS        : UN_COMPANIA    => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                            UN_CICLO       => CICLO PARA EL QUE SE VA A REALIZAR LA INSERCION.
                            UN_ANIO        => AÑO PARA EL QUE SE VA A REALIZAR LA INSERCION.
                            UN_PERIODO     => PERIODO PARA EL QUE SE VA A REALIZAR LA INSERCION.
        MODIFICATIONS     : 
        @NAME:  verificarFacturadosNulos
        @METHOD:  POST
    */ 
  (
    UN_COMPANIA       IN  PCK_SUBTIPOS.TI_COMPANIA
   ,UN_CICLO          IN  PCK_SUBTIPOS.TI_CICLO
   ,UN_ANIO           IN  PCK_SUBTIPOS.TI_ANIO
   ,UN_PERIODO        IN  PCK_SUBTIPOS.TI_PERIODO
   ,UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
  ) 
  AS
    MI_CAMP           VARCHAR2(1 CHAR);
    MI_TABLA          PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
    MI_STRSQL         PCK_SUBTIPOS.TI_STRSQL;  
  BEGIN
    BEGIN
      MI_STRSQL := '     FROM   SP_USUARIO 
                           LEFT JOIN SP_FACTURADO 
                             ON      SP_USUARIO.COMPANIA   = SP_FACTURADO.COMPANIA
                            AND      SP_USUARIO.CICLO      = SP_FACTURADO.CICLO
                            AND      SP_USUARIO.CODIGORUTA = SP_FACTURADO.CODIGORUTA
                            AND      SP_USUARIO.ANO        = SP_FACTURADO.ANO
                            AND      SP_USUARIO.PERIODO    = SP_FACTURADO.PERIODO
                         WHERE  SP_USUARIO.COMPANIA   = '''||UN_COMPANIA||'''
                           AND  SP_USUARIO.CICLO      = '  ||UN_CICLO||'
                           AND  SP_USUARIO.ANO        = '  ||UN_ANIO||'
                           AND  SP_USUARIO.PERIODO    = '''||UN_PERIODO||'''
                           AND  SP_USUARIO.ESTADO     NOT IN (''R'',''S'')
                           AND  SP_FACTURADO.COMPANIA IS NULL';
      EXECUTE IMMEDIATE 'SELECT DISTINCT ''X'''||MI_STRSQL
      INTO MI_CAMP;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_CAMP := ' ';
    END;
    IF MI_CAMP <> ' ' THEN
      BEGIN
        BEGIN
          MI_TABLA := 'SP_FACTURADO';
          MI_CAMPOS := 'COMPANIA
                       ,CICLO
                       ,CODIGORUTA
                       ,ANO
                       ,PERIODO
                       ,CREDITOABONADO
                       ,CONCEPTO
                       ,VALOR_FACTURADO
                       ,DEUDA
                       ,DATE_CREATED
                       ,CREATED_BY';
          MI_VALORES := 'SELECT SP_USUARIO.COMPANIA
                               ,SP_USUARIO.CICLO
                               ,SP_USUARIO.CODIGORUTA
                               ,SP_USUARIO.ANO
                               ,SP_USUARIO.PERIODO
                               ,0
                               ,1
                               ,0
                               ,0
                               ,SYSDATE
                               , '''||UN_USUARIO||''''
                         ||MI_STRSQL;
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA  => MI_TABLA,
                                                UN_ACCION => 'IS',
                                                UN_CAMPOS => MI_CAMPOS,
                                                UN_VALORES => MI_VALORES);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD   => SQLCODE,
            UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_INSERTARFACTURADO
          );
      END;
    END IF;
  END PR_FACTURADOSNULOS;

  -- 14
  PROCEDURE PR_ESTADISTICASTARIFARIAS
   /*
      NAME              : PR_ESTADISTICASTARIFARIAS --> EN ACCESS EstadisticasTarifarias
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MIGRADOR     : 13/02/2017
      TIME              : 04:56 PM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PROCEDIMIENTO QUE LLENA ESTADISTICAS DE CONSUMO CON BASE EN LA ESTADISTICA
                          PREVIAMENTE GENERADA.
      PARAMETERS        : UN_COMPANIA    => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_CICLO       => CICLO PARA EL QUE SE VA A REALIZAR LAS ACTUALIZACIONES.
                          UN_ANIO        => AÑO PARA EL QUE SE VA A REALIZAR LAS ACTUALIZACIONES.
                          UN_PERIODO     => PERIODO PARA EL QUE SE VA A REALIZAR LAS ACTUALIZACIONES.
      MODIFICATIONS     : 
      @NAME:  registrarEstadisticasTrifarias
      @METHOD:  PUT
    */ 
  (
    UN_COMPANIA             IN  PCK_SUBTIPOS.TI_COMPANIA
   ,UN_CICLO                IN  PCK_SUBTIPOS.TI_CICLO
   ,UN_ANIO                 IN  PCK_SUBTIPOS.TI_ANIO
   ,UN_PERIODO              IN  PCK_SUBTIPOS.TI_PERIODO
   ,UN_USUARIO              IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_LIMITEINFERIOR         PCK_SUBTIPOS.TI_ENTERO;
    MI_TMPVAL                 VARCHAR2(100 CHAR);
    MI_TABLA                  PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
    MI_MERGEUSING             PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE            PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE            PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN 
    BEGIN
      BEGIN
        MI_TABLA := 'SP_ESTADISTICAS_CONSUMO';
        MI_CAMPOS := ' SUMAVALORPROM = 0
                      ,SUMACONSUMOPROM = 0
                      ,SUMAUSUARIOS = 0
                      ,SUMAVALOR = 0
                      ,SUMACONSUMO = 0
                      ,SUMAVALORALC = 0
                      ,SUMACONSUMOALC = 0
                      ,MODIFIED_BY    = '''||UN_USUARIO||'''
                      ,DATE_MODIFIED  = SYSDATE  ';
        MI_CONDICION := '     COMPANIA = '''||UN_COMPANIA||'''
                          AND CICLO    = '  ||UN_CICLO||'
                          AND ANO      = '  ||UN_ANIO||'
                          AND PERIODO  = '''||UN_PERIODO||''''; 
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CONDICION';
        MI_MSGERROR(1).VALOR := 'COMPANIA= '''||UN_COMPANIA||
                                ''',CICLO= '||UN_CICLO||
                                ',ANO= '||UN_ANIO||
                                ' y PERIODO= '''||UN_PERIODO||'''';
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTUALIESTACONSUMO,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
    <<TARIFASANTERIORES>>
    FOR MI_RSTARIFASANT IN (SELECT SP_ESTADISTICAS_CONSUMO.SUMAUSUARIOSPROBLEMA
                                  ,SP_ESTADISTICAS_CONSUMO.CANTUSUARIOSPROBLEMA
                                  ,SP_ESTADISTICAS_CONSUMO.SUMAUSUARIOS
                                  ,SP_ESTADISTICAS_CONSUMO.CANTUSUARIOSACUEDUCTO
                                  ,SP_ESTADISTICAS_CONSUMO.SUMAUSUARIOSALC
                                  ,SP_ESTADISTICAS_CONSUMO.SERVICIO
                                  ,SP_ESTADISTICAS_CONSUMO.CANTUSUARIOSALCANTARILLADO
                                  ,SP_ESTADISTICAS_CONSUMO.SUMACONSUMOPROM
                                  ,SP_ESTADISTICAS_CONSUMO.CONSUMOPROMEDIO
                                  ,SP_ESTADISTICAS_CONSUMO.CANTUSUARIOSPORPROMEDIO
                                  ,SP_ESTADISTICAS_CONSUMO.SUMACONSUMO
                                  ,SP_ESTADISTICAS_CONSUMO.CONSUMO
                                  ,SP_ESTADISTICAS_CONSUMO.USO
                                  ,SP_ESTADISTICAS_CONSUMO.ESTRATO
                                  ,SP_ESTADISTICAS_CONSUMO.RANGO
                                  ,SP_ESTADISTICAS_CONSUMO.SUMACONSUMOALC
                                  ,SP_RANGO.LIMITEINFERIOR
                                  ,SP_RANGO.LIMITESUPERIOR
                                  ,SP_TARIFAS.CONSUMOSUNTUARIO
                                  ,SP_TARIFAS.TARIFABASICO
                                  ,SP_TARIFAS.TARIFACOMPLEMENTARIO
                                  ,SP_TARIFAS.TARIFASUNTUARIO
                                  ,SP_TARIFAS.ALCBASICO
                                  ,SP_TARIFAS.ALCCOMPLEMENTARIO
                                  ,SP_TARIFAS.ALCSUNTUARIO
                                  ,SP_TARIFAS.CARGOFIJO
                            FROM   SP_ESTADISTICAS_CONSUMO 
                              LEFT JOIN SP_TARIFAS 
                                ON      SP_ESTADISTICAS_CONSUMO.ESTRATO  = SP_TARIFAS.ESTRATO
                               AND      SP_ESTADISTICAS_CONSUMO.USO      = SP_TARIFAS.USO
                               AND      SP_ESTADISTICAS_CONSUMO.PERIODO  = SP_TARIFAS.PERIODO
                               AND      SP_ESTADISTICAS_CONSUMO.ANO      = SP_TARIFAS.ANO
                               AND      SP_ESTADISTICAS_CONSUMO.COMPANIA = SP_TARIFAS.COMPANIA
                              LEFT JOIN SP_RANGO 
                                ON      SP_ESTADISTICAS_CONSUMO.RANGO    = SP_RANGO.CODIGO
                               AND      SP_ESTADISTICAS_CONSUMO.COMPANIA = SP_RANGO.COMPANIA
                            WHERE  SP_ESTADISTICAS_CONSUMO.COMPANIA = UN_COMPANIA
                              AND  SP_ESTADISTICAS_CONSUMO.CICLO    = UN_CICLO
                              AND  SP_ESTADISTICAS_CONSUMO.ANO      = UN_ANIO
                              AND  SP_ESTADISTICAS_CONSUMO.PERIODO  = UN_PERIODO)
    LOOP
      MI_LIMITEINFERIOR := MI_RSTARIFASANT.LIMITEINFERIOR - 1; 
      IF MI_LIMITEINFERIOR < 0 THEN
        MI_LIMITEINFERIOR := 0;
      END IF;
      MI_RSTARIFASANT.SUMAUSUARIOSPROBLEMA := MI_RSTARIFASANT.CANTUSUARIOSPROBLEMA;
      MI_RSTARIFASANT.SUMAUSUARIOS := MI_RSTARIFASANT.CANTUSUARIOSACUEDUCTO 
                                    - MI_RSTARIFASANT.CANTUSUARIOSPROBLEMA;
      MI_RSTARIFASANT.SUMACONSUMOPROM := MI_RSTARIFASANT.CONSUMOPROMEDIO 
                                      - (MI_RSTARIFASANT.CANTUSUARIOSPORPROMEDIO * MI_LIMITEINFERIOR);
      MI_RSTARIFASANT.SUMACONSUMO := MI_RSTARIFASANT.CONSUMO 
                                  - (MI_RSTARIFASANT.CANTUSUARIOSACUEDUCTO * MI_LIMITEINFERIOR) 
                                   - MI_RSTARIFASANT.SUMACONSUMOPROM;
      IF MI_RSTARIFASANT.SERVICIO = '02' THEN
        MI_RSTARIFASANT.SUMAUSUARIOSALC := MI_RSTARIFASANT.CANTUSUARIOSALCANTARILLADO 
                                         - MI_RSTARIFASANT.CANTUSUARIOSPROBLEMA;
        MI_RSTARIFASANT.SUMACONSUMOALC := MI_RSTARIFASANT.CONSUMO 
                                       - (MI_RSTARIFASANT.CANTUSUARIOSALCANTARILLADO * MI_LIMITEINFERIOR) 
                                        - MI_RSTARIFASANT.SUMACONSUMOPROM;
      END IF;
      IF MI_RSTARIFASANT.RANGO = '01' THEN
        MI_TMPVAL := 'BASICO';
      ELSIF MI_RSTARIFASANT.RANGO = '02' THEN
        MI_TMPVAL := 'COMPLEMENTARIO';
      ELSE
        MI_TMPVAL := 'SUNTUARIO';
      END IF;
      <<TARIFASRECIENTES>>
      FOR MI_RSTARIFASREC IN (SELECT SP_ESTADISTICAS_CONSUMO.CANTUSUARIOSACUEDUCTO
                                    ,SP_ESTADISTICAS_CONSUMO.CANTUSUARIOSALCANTARILLADO
                                    ,SP_ESTADISTICAS_CONSUMO.CANTUSUARIOSPORPROMEDIO
                                    ,SP_ESTADISTICAS_CONSUMO.CANTUSUARIOSPROBLEMA
                                    ,SP_ESTADISTICAS_CONSUMO.RANGO ESTRANGO
                                    ,SP_RANGO.LIMITEINFERIOR
                                    ,SP_RANGO.LIMITESUPERIOR
                              FROM   SP_ESTADISTICAS_CONSUMO 
                                LEFT JOIN SP_RANGO 
                                  ON      SP_ESTADISTICAS_CONSUMO.RANGO    = SP_RANGO.CODIGO
                                 AND      SP_ESTADISTICAS_CONSUMO.COMPANIA = SP_RANGO.COMPANIA
                              WHERE  SP_ESTADISTICAS_CONSUMO.COMPANIA = UN_COMPANIA
                                AND  SP_ESTADISTICAS_CONSUMO.ANO      = UN_ANIO
                                AND  SP_ESTADISTICAS_CONSUMO.PERIODO  = UN_PERIODO
                                AND  SP_ESTADISTICAS_CONSUMO.CICLO    = UN_CICLO
                                AND  SP_ESTADISTICAS_CONSUMO.SERVICIO = MI_RSTARIFASANT.SERVICIO
                                AND  SP_ESTADISTICAS_CONSUMO.USO      = MI_RSTARIFASANT.USO
                                AND  SP_ESTADISTICAS_CONSUMO.ESTRATO  = MI_RSTARIFASANT.ESTRATO
                                AND  SP_ESTADISTICAS_CONSUMO.RANGO    > MI_RSTARIFASANT.RANGO)
      LOOP
        BEGIN
          BEGIN
            MI_TABLA := 'SP_ESTADISTICAS_CONSUMO';
            MI_MERGEUSING  := 'SELECT SP_ESTADISTICAS_CONSUMO.COMPANIA
                                     ,SP_ESTADISTICAS_CONSUMO.CICLO
                                     ,SP_ESTADISTICAS_CONSUMO.ANO
                                     ,SP_ESTADISTICAS_CONSUMO.PERIODO
                                     ,SP_ESTADISTICAS_CONSUMO.USO
                                     ,SP_ESTADISTICAS_CONSUMO.SERVICIO
                                     ,SP_ESTADISTICAS_CONSUMO.ESTRATO
                                     ,SP_ESTADISTICAS_CONSUMO.RANGO
                               FROM   SP_ESTADISTICAS_CONSUMO
                                 LEFT JOIN SP_TARIFAS 
                                   ON      SP_ESTADISTICAS_CONSUMO.COMPANIA = SP_TARIFAS.COMPANIA
                                  AND      SP_ESTADISTICAS_CONSUMO.ANO      = SP_TARIFAS.ANO
                                  AND      SP_ESTADISTICAS_CONSUMO.PERIODO  = SP_TARIFAS.PERIODO
                                  AND      SP_ESTADISTICAS_CONSUMO.USO      = SP_TARIFAS.USO
                                  AND      SP_ESTADISTICAS_CONSUMO.ESTRATO  = SP_TARIFAS.ESTRATO
                                 LEFT JOIN SP_RANGO 
                                   ON      SP_ESTADISTICAS_CONSUMO.COMPANIA = SP_RANGO.COMPANIA
                                  AND      SP_ESTADISTICAS_CONSUMO.RANGO    = SP_RANGO.CODIGO
                               WHERE  SP_ESTADISTICAS_CONSUMO.COMPANIA = '''||UN_COMPANIA||'''
                                 AND  SP_ESTADISTICAS_CONSUMO.CICLO    = '  ||UN_CICLO||'
                                 AND  SP_ESTADISTICAS_CONSUMO.ANO      = '  ||UN_ANIO||'
                                 AND  SP_ESTADISTICAS_CONSUMO.PERIODO  = '''||UN_PERIODO||'''';
            MI_MERGEENLACE := '     TABLA.COMPANIA = VISTA.COMPANIA
                                AND TABLA.CICLO    = VISTA.CICLO
                                AND TABLA.ANO      = VISTA.ANO
                                AND TABLA.PERIODO  = VISTA.PERIODO
                                AND TABLA.USO      = VISTA.USO
                                AND TABLA.SERVICIO = VISTA.SERVICIO
                                AND TABLA.ESTRATO  = VISTA.ESTRATO
                                AND TABLA.RANGO    = VISTA.RANGO';
            MI_MERGEEXISTE := 'UPDATE 
                                 SET TABLA.SUMAUSUARIOS         = '||NVL(MI_RSTARIFASANT.SUMAUSUARIOS, 0)||' + 
                                                                  '||MI_RSTARIFASREC.CANTUSUARIOSACUEDUCTO||'
                                    ,TABLA.SUMAUSUARIOSALC      = '||NVL(MI_RSTARIFASANT.SUMAUSUARIOSALC, 0)||' + 
                                                                  '||MI_RSTARIFASREC.CANTUSUARIOSALCANTARILLADO||'
                                    ,TABLA.SUMAUSUARIOSPROBLEMA = '||NVL(MI_RSTARIFASANT.SUMAUSUARIOSPROBLEMA, 0)||' + (
                                                                  '||MI_RSTARIFASREC.CANTUSUARIOSPORPROMEDIO||' + 
                                                                  '||MI_RSTARIFASREC.CANTUSUARIOSPROBLEMA||')
                                    ,TABLA.SUMACONSUMO          = '||NVL(MI_RSTARIFASANT.SUMACONSUMO, 0)||' + (
                                                                  '||MI_RSTARIFASREC.CANTUSUARIOSACUEDUCTO||' - 
                                                                  '||MI_RSTARIFASREC.CANTUSUARIOSPORPROMEDIO||') * (
                                                                  '||MI_RSTARIFASANT.LIMITESUPERIOR||' - 
                                                                  '||MI_LIMITEINFERIOR||')
                                    ,TABLA.SUMACONSUMOALC       = '||NVL(MI_RSTARIFASANT.SUMACONSUMOALC, 0)||' + 
                                                                  '||MI_RSTARIFASREC.CANTUSUARIOSALCANTARILLADO||' * (
                                                                  '||MI_RSTARIFASANT.LIMITESUPERIOR||' - 
                                                                  '||MI_LIMITEINFERIOR||')
                                    ,TABLA.SUMACONSUMOPROM      = '||NVL(MI_RSTARIFASANT.SUMACONSUMOPROM, 0)||' + (
                                                                  '||MI_RSTARIFASREC.CANTUSUARIOSPORPROMEDIO||' + 
                                                                  '||MI_RSTARIFASREC.CANTUSUARIOSPROBLEMA||') * (
                                                                  '||MI_RSTARIFASANT.LIMITESUPERIOR||' - 
                                                                  '||MI_LIMITEINFERIOR||')
                                    ,TABLA.MODIFIED_BY          = '''||UN_USUARIO||'''
                                    ,TABLA.DATE_MODIFIED        = SYSDATE   ';

            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA, 
                                                  UN_ACCION      => 'MM', 
                                                  UN_MERGEUSING  => MI_MERGEUSING, 
                                                  UN_MERGEENLACE => MI_MERGEENLACE,
                                                  UN_MERGEEXISTE => MI_MERGEEXISTE);    

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
            MI_MSGERROR(1).CLAVE :='CONDICION';
            MI_MSGERROR(1).VALOR :='COMPANIA= '''||UN_COMPANIA||
                                   ''', CICLO= '||UN_CICLO||
                                   ', ANO= '||UN_ANIO||
                                   ' y PERIODO= '''||UN_PERIODO||'''';
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTUALIESTACONSUMO,
              UN_REEMPLAZOS => MI_MSGERROR
            );
        END; 
      END LOOP TARIFASRECIENTES;
      BEGIN
        BEGIN
          MI_TABLA := 'SP_ESTADISTICAS_CONSUMO';
          MI_MERGEUSING := 'SELECT SP_ESTADISTICAS_CONSUMO.COMPANIA
                                     ,SP_ESTADISTICAS_CONSUMO.CICLO
                                     ,SP_ESTADISTICAS_CONSUMO.ANO
                                     ,SP_ESTADISTICAS_CONSUMO.PERIODO
                                     ,SP_ESTADISTICAS_CONSUMO.USO
                                     ,SP_ESTADISTICAS_CONSUMO.SERVICIO
                                     ,SP_ESTADISTICAS_CONSUMO.ESTRATO
                                     ,SP_ESTADISTICAS_CONSUMO.RANGO
                               FROM   SP_ESTADISTICAS_CONSUMO
                                 LEFT JOIN SP_TARIFAS 
                                   ON      SP_ESTADISTICAS_CONSUMO.COMPANIA = SP_TARIFAS.COMPANIA
                                  AND      SP_ESTADISTICAS_CONSUMO.ANO      = SP_TARIFAS.ANO
                                  AND      SP_ESTADISTICAS_CONSUMO.PERIODO  = SP_TARIFAS.PERIODO
                                  AND      SP_ESTADISTICAS_CONSUMO.USO      = SP_TARIFAS.USO
                                  AND      SP_ESTADISTICAS_CONSUMO.ESTRATO  = SP_TARIFAS.ESTRATO
                                 LEFT JOIN SP_RANGO 
                                   ON      SP_ESTADISTICAS_CONSUMO.COMPANIA = SP_RANGO.COMPANIA
                                  AND      SP_ESTADISTICAS_CONSUMO.RANGO    = SP_RANGO.CODIGO
                               WHERE  SP_ESTADISTICAS_CONSUMO.COMPANIA = '''||UN_COMPANIA||'''
                                 AND  SP_ESTADISTICAS_CONSUMO.CICLO    = '  ||UN_CICLO||'
                                 AND  SP_ESTADISTICAS_CONSUMO.ANO      = '  ||UN_ANIO||'
                                 AND  SP_ESTADISTICAS_CONSUMO.PERIODO  = '''||UN_PERIODO||'''';
          MI_MERGEENLACE := '     TABLA.COMPANIA = VISTA.COMPANIA
                              AND TABLA.CICLO    = VISTA.CICLO
                              AND TABLA.ANO      = VISTA.ANO
                              AND TABLA.PERIODO  = VISTA.PERIODO
                              AND TABLA.USO      = VISTA.USO
                              AND TABLA.SERVICIO = VISTA.SERVICIO
                              AND TABLA.ESTRATO  = VISTA.ESTRATO
                              AND TABLA.RANGO    = VISTA.RANGO';
          MI_MERGEEXISTE := 'UPDATE
                               SET TABLA.SUMAVALOR = TO_NUMBER('||NVL(MI_RSTARIFASANT.SUMACONSUMO, 0)||' * ';
          CASE MI_TMPVAL
            WHEN 'BASICO' THEN
              MI_MERGEEXISTE := MI_MERGEEXISTE ||
                                NVL(MI_RSTARIFASANT.TARIFABASICO, 0)||')';
            WHEN 'COMPLEMENTARIO' THEN
              MI_MERGEEXISTE := MI_MERGEEXISTE ||
                                NVL(MI_RSTARIFASANT.TARIFACOMPLEMENTARIO, 0)||')';
            WHEN 'SUNTUARIO' THEN
              MI_MERGEEXISTE := MI_MERGEEXISTE ||
                                NVL(MI_RSTARIFASANT.TARIFASUNTUARIO, 0)||')';
          END CASE;  
          MI_MERGEEXISTE := MI_MERGEEXISTE ||
                            ',TABLA.SUMAVALORALC = TO_NUMBER('||NVL(MI_RSTARIFASANT.SUMACONSUMOALC, 0)||' *';
          CASE MI_TMPVAL
            WHEN 'BASICO' THEN
              MI_MERGEEXISTE := MI_MERGEEXISTE ||
                                NVL(MI_RSTARIFASANT.ALCBASICO, 0)||')';
            WHEN 'COMPLEMENTARIO' THEN
              MI_MERGEEXISTE := MI_MERGEEXISTE ||
                                NVL(MI_RSTARIFASANT.ALCCOMPLEMENTARIO, 0)||')';
            WHEN 'SUNTUARIO' THEN
              MI_MERGEEXISTE := MI_MERGEEXISTE ||
                                NVL(MI_RSTARIFASANT.ALCSUNTUARIO, 0)||')';
          END CASE;  
          MI_MERGEEXISTE := MI_MERGEEXISTE ||
                            ',TABLA.SUMAVALORPROM = TO_NUMBER('||NVL(MI_RSTARIFASANT.CARGOFIJO, 0)||' * 
                                                              '||NVL(MI_RSTARIFASANT.SUMACONSUMOPROM, 0)||')
                             ,TABLA.MODIFIED_BY         = '''||UN_USUARIO||'''
                             ,TABLA.DATE_MODIFIED       = SYSDATE  ';

          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA, 
                                                UN_ACCION      => 'MM', 
                                                UN_MERGEUSING  => MI_MERGEUSING, 
                                                UN_MERGEENLACE => MI_MERGEENLACE,
                                                UN_MERGEEXISTE => MI_MERGEEXISTE);    

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE :='CONDICION';
          MI_MSGERROR(1).VALOR :='COMPANIA= '''||UN_COMPANIA||
                                 ''', CICLO= '||UN_CICLO||
                                 ', ANO= '||UN_ANIO||
                                 ' y PERIODO= '''||UN_PERIODO||'''';
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTUALIESTACONSUMO,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END; 
    END LOOP TARIFASANTERIORES;
  END PR_ESTADISTICASTARIFARIAS;

  -- 15
  PROCEDURE PR_CUADREESTRATOS
   /*
      NAME              : PR_CUADREESTRATOS --> EN ACCESS cuadreEstratos
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MIGRADOR     : 14/02/2017
      TIME              : 11:18 AM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PROCEDIMIENTO QUE ASEGURA QUE TODOS LOS USOS Y ESTRATOS EXISTENTES
                          EN LA TABLA SP_ESTADISTICAS_FACTURACION SE CREEEN TAMBIEN EN
                          SP_ESTADISTICAS_CONSUMO.
      PARAMETERS        : UN_COMPANIA    => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_CICLO       => CICLO PARA EL QUE SE VA A REALIZAR LAS INSERCIONES.
                          UN_ANIO        => AÑO PARA EL QUE SE VA A REALIZAR LAS INSERCIONES.
                          UN_PERIODO     => PERIODO PARA EL QUE SE VA A REALIZAR LAS INSERCIONES.
      MODIFICATIONS     : 
      @NAME:  cuadrarEstratos
      @METHOD:  POST
    */ 
  (
    UN_COMPANIA             IN  PCK_SUBTIPOS.TI_COMPANIA
   ,UN_CICLO                IN  PCK_SUBTIPOS.TI_CICLO
   ,UN_ANIO                 IN  PCK_SUBTIPOS.TI_ANIO
   ,UN_PERIODO              IN  PCK_SUBTIPOS.TI_PERIODO
   ,UN_USUARIO              IN PCK_SUBTIPOS.TI_USUARIO

  )
  AS
    MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
    MI_CAMP               VARCHAR2(1 CHAR);
  BEGIN 
    <<SERVESTADISTICAS>>
    FOR MI_I IN 1..3 LOOP
      BEGIN
        BEGIN
          MI_TABLA := 'SP_ESTADISTICAS_CONSUMO';
          MI_CAMPOS := 'COMPANIA,
                        ANO,
                        PERIODO,
                        CICLO,
                        SERVICIO,
                        USO,
                        ESTRATO,
                        RANGO,
                        DATE_CREATED,
                        CREATED_BY';

          MI_VALORES := 'SELECT SP_ESTADISTICAS_FACTURACION.COMPANIA
                                ,SP_ESTADISTICAS_FACTURACION.ANO
                                ,SP_ESTADISTICAS_FACTURACION.PERIODO
                                ,SP_ESTADISTICAS_FACTURACION.CICLO
                                ,''0'||MI_I||''' SERVICIO
                                ,SP_ESTADISTICAS_FACTURACION.USO
                                ,SP_ESTADISTICAS_FACTURACION.ESTRATO
                                ,''01'' RANGO
                                ,SYSDATE
                                ,'''||UN_USUARIO||'''
                           FROM SP_ESTADISTICAS_FACTURACION 
                           LEFT JOIN SP_ESTADISTICAS_CONSUMO 
                             ON SP_ESTADISTICAS_FACTURACION.COMPANIA = SP_ESTADISTICAS_CONSUMO.COMPANIA
                            AND SP_ESTADISTICAS_FACTURACION.ANO      = SP_ESTADISTICAS_CONSUMO.ANO
                            AND SP_ESTADISTICAS_FACTURACION.PERIODO  = SP_ESTADISTICAS_CONSUMO.PERIODO
                            AND SP_ESTADISTICAS_FACTURACION.CICLO    = SP_ESTADISTICAS_CONSUMO.CICLO
                            AND SP_ESTADISTICAS_FACTURACION.USO      = SP_ESTADISTICAS_CONSUMO.USO
                            AND SP_ESTADISTICAS_FACTURACION.ESTRATO  = SP_ESTADISTICAS_CONSUMO.ESTRATO
                          WHERE SP_ESTADISTICAS_FACTURACION.COMPANIA = '''||UN_COMPANIA||'''
                            AND SP_ESTADISTICAS_FACTURACION.ANO      = '  ||UN_ANIO||'
                            AND SP_ESTADISTICAS_FACTURACION.PERIODO  = '''||UN_PERIODO||'''
                            AND SP_ESTADISTICAS_FACTURACION.CICLO    = '  ||UN_CICLO||'
                            AND SP_ESTADISTICAS_CONSUMO.COMPANIA     IS NULL';

          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                UN_ACCION  => 'IS',
                                                UN_CAMPOS  => MI_CAMPOS,
                                                UN_VALORES => MI_VALORES);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_INSERESTACONSUMO
          );
      END;
    END LOOP SERVESTADISTICAS;
    <<ESTAFACTURACION>>
    FOR MI_RS IN (SELECT SP_ESTADISTICAS_FACTURACION.COMPANIA
                         ,SP_ESTADISTICAS_FACTURACION.ANO
                         ,SP_ESTADISTICAS_FACTURACION.PERIODO
                         ,SP_ESTADISTICAS_FACTURACION.CICLO
                         ,SP_ESTADISTICAS_FACTURACION.USO
                         ,SP_ESTADISTICAS_FACTURACION.ESTRATO
                    FROM SP_ESTADISTICAS_FACTURACION
                   WHERE SP_ESTADISTICAS_FACTURACION.COMPANIA = UN_COMPANIA
                     AND SP_ESTADISTICAS_FACTURACION.ANO      = UN_ANIO
                     AND SP_ESTADISTICAS_FACTURACION.PERIODO  = UN_PERIODO
                     AND SP_ESTADISTICAS_FACTURACION.CICLO    = UN_CICLO
                   GROUP BY 
                         SP_ESTADISTICAS_FACTURACION.COMPANIA,
                         SP_ESTADISTICAS_FACTURACION.ANO,
                         SP_ESTADISTICAS_FACTURACION.PERIODO,
                         SP_ESTADISTICAS_FACTURACION.CICLO,
                         SP_ESTADISTICAS_FACTURACION.SERVICIO,
                         SP_ESTADISTICAS_FACTURACION.USO,
                         SP_ESTADISTICAS_FACTURACION.ESTRATO)
    LOOP
      <<SERVICIOSESTA>>
      FOR MI_J IN 1..3 LOOP
        BEGIN
          SELECT DISTINCT 'X'
            INTO MI_CAMP
            FROM SP_ESTADISTICAS_CONSUMO
           WHERE SP_ESTADISTICAS_CONSUMO.COMPANIA = UN_COMPANIA
             AND SP_ESTADISTICAS_CONSUMO.ANO      = UN_ANIO
             AND SP_ESTADISTICAS_CONSUMO.PERIODO  = UN_PERIODO
             AND SP_ESTADISTICAS_CONSUMO.CICLO    = UN_CICLO
             AND SP_ESTADISTICAS_CONSUMO.SERVICIO = '''0'||MI_J||''''
             AND SP_ESTADISTICAS_CONSUMO.USO      = MI_RS.USO
             AND SP_ESTADISTICAS_CONSUMO.ESTRATO  = MI_RS.ESTRATO 
             AND SP_ESTADISTICAS_CONSUMO.RANGO    = '01';
          EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_CAMP := ' ';
        END;
        IF MI_CAMP = ' ' THEN
          BEGIN
              BEGIN
                MI_TABLA := 'SP_ESTADISTICAS_CONSUMO';
                MI_CAMPOS := 'COMPANIA,
                              ANO,
                              PERIODO, 
                              CICLO, 
                              SERVICIO,
                              USO, 
                              ESTRATO,
                              RANGO,
                              DATE_CREATED,
                              CREATED_BY';

                MI_VALORES := ''''||UN_COMPANIA||''','||UN_ANIO||
                              ','''||UN_PERIODO||''','||UN_CICLO||
                              ',''0'||MI_J||''','''||MI_RS.USO||
                              ''','''||MI_RS.ESTRATO||''',''01'',
                              SYSDATE,'''||UN_USUARIO||'''';

                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                      UN_ACCION  => 'I',
                                                      UN_CAMPOS  => MI_CAMPOS,
                                                      UN_VALORES => MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE,
                UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_INSERESTACONSUMO
              );
          END;
        END IF;
      END LOOP SERVICIOSESTA;
    END LOOP ESTAFACTURACION;
  END PR_CUADREESTRATOS;

  -- 16
  PROCEDURE PR_DISCRIMINARES688
    /*
      NAME              : PR_DISCRIMINARES688 --> EN ACCESS DiscriminaRES688
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MIGRADOR     : 15/02/2017
      TIME              : 11:43 AM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PROCEDIMIENTO QUE DISTRIBUYE LOS CONCEPTOS DE ACUEDCUTO (ACU)
                          Y ALCANTARILLADO (ALC) EN 3 NUEVOS CONCEPTOS CMI, CMO Y CTM.
      PARAMETERS        : UN_COMPANIA => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_CICLO    => CICLO SELECCIONADO
                          UN_ANIO     => ANIO ACTUAL EN EL QUE SE REALIZARA LA DISTRIBUCION
                                         DE LOS CONCEPTOS.
                          UN_PERIODO  => PERIODO ACTUAL EN EL QUE SE REALIZARA LA DISTRIBUCION
                                         DE LOS CONCEPTOS.
      MODIFICATIONS     : 
      @NAME:  discriminarRes688
      @METHOD:  POST
    */ 
  ( 
    UN_COMPANIA       IN  PCK_SUBTIPOS.TI_COMPANIA
   ,UN_CICLO          IN  PCK_SUBTIPOS.TI_CICLO
   ,UN_PERIODO        IN  PCK_SUBTIPOS.TI_PERIODO
   ,UN_ANIO           IN  PCK_SUBTIPOS.TI_ANIO
   ,UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_PORCMO             PCK_SUBTIPOS.TI_DOBLE;
    MI_PORCMI             PCK_SUBTIPOS.TI_DOBLE;
    MI_PORCMT             PCK_SUBTIPOS.TI_DOBLE;
    MI_SUBSOBRE           PCK_SUBTIPOS.TI_DOBLE;
    MI_TPLENA             PCK_SUBTIPOS.TI_DOBLE;
    MI_SERVICIO           VARCHAR2(50 CHAR);
    MI_RSCMO              PCK_SUBTIPOS.TI_DOBLE;
    MI_RSCMI              PCK_SUBTIPOS.TI_DOBLE;
    MI_RSCMT              PCK_SUBTIPOS.TI_DOBLE;
    MI_LIMITE             PCK_SUBTIPOS.TI_ENTERO;
    MI_PORCENTAJE         PCK_SUBTIPOS.TI_DOBLE;
    MI_DIFERENCIA         PCK_SUBTIPOS.TI_DOBLE;
    MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
    MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_RSCAMPOS           PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_INICIO             PCK_SUBTIPOS.TI_ENTERO;
  BEGIN
    BEGIN
      BEGIN
        MI_TABLA := 'SP_ESTADISTICAS_FACTURACION';
        MI_CONDICION := '    CONCEPTO BETWEEN 221 AND 226
                         AND COMPANIA = '''||UN_COMPANIA||'''
                         AND ANO      = '  ||UN_ANIO||'
                         AND PERIODO  = '''||UN_PERIODO||'''
                         AND CICLO    = '  ||UN_CICLO;
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA,
                                              UN_ACCION => 'E',
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CONDICION';
        MI_MSGERROR(1).VALOR := 'CONCEPTO está entre 221 y 226, COMPANIA= '''
                                 ||UN_COMPANIA||''', ANO= '||UN_ANIO||
                                 ', PERIODO= '''||UN_PERIODO||
                                 ''' y CICLO= '||UN_CICLO;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_BORESTAFACTURACION,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
    <<ESTADISTICASFAC>>
    FOR MI_RS IN (SELECT  EF.COMPANIA
                         ,EF.ANO
                         ,EF.PERIODO
                         ,EF.CICLO
                         ,EF.SERVICIO
                         ,EF.USO
                         ,EF.ESTRATO
                         ,CASE WHEN EF.CONCEPTO IN (2,5,19)
                               THEN 'ACU'
                               ELSE 'ALC'
                          END SERVICIOS
                         ,SUM(NVL(EF.DEUDAANT,0)) DEUDAANT
                         ,SUM(NVL(EF.VALOR_FACTURADOANT,0)) VALOR_FACTURADOANT
                         ,SUM(NVL(EF.DEUDAIN,0)) DEUDAIN
                         ,SUM(NVL(EF.VALOR_FACTURADOIN,0)) VALOR_FACTURADOIN
                         ,SUM(NVL(EF.DEUDA,0)) DEUDA
                         ,SUM(NVL(EF.VALOR_FACTURADO,0)) VALOR_FACTURADO
                         ,SUM(NVL(EF.RECAUDADODEUDA,0)) RECAUDADODEUDA
                         ,SUM(NVL(EF.RECAUDADOPERIODO,0)) RECAUDADOPERIODO
                         ,SUM(NVL(EF.DOBLEPAGO,0)) DOBLEPAGO
                         ,SUM(NVL(EF.SALDOCREDITO,0)) SALDOCREDITO
                         ,SUM(NVL(EF.ABONO,0)) ABONO
                         ,SUM(NVL(EF.MODIFDEUDA,0)) MODIFDEUDA
                         ,SUM(NVL(EF.VALORFINACT,0)) VALORFINACT
                         ,SUM(NVL(EF.VALORFINANT,0)) VALORFINANT
                         ,SUM(NVL(EF.VALORABONOACT,0)) VALORABONOACT
                         ,SUM(NVL(EF.VALORABONOANT,0)) VALORABONOANT
                         ,SUM(NVL(EF.VALORABONODEUDA,0)) VALORABONODEUDA
                         ,SUM(NVL(EF.RECAUDOFINACT,0)) RECAUDOFINACT
                         ,SUM(NVL(EF.RECAUDOFINANT,0)) RECAUDOFINANT
                         ,SUM(NVL(EF.SALDOFINOTROS,0)) SALDOFINOTROS
                         ,SUM(NVL(EF.SALDOFINDEUDA,0)) SALDOFINDEUDA
                         ,SUM(NVL(EF.SALDOFINPERIODO,0)) SALDOFINPERIODO
                         ,SUM(NVL(EF.VALORFINDEUDA,0)) VALORFINDEUDA
                         ,SUM(NVL(EF.VALORFINPERIODO,0)) VALORFINPERIODO
                         ,SUM(NVL(EF.SALDOXCOBRAR,0)) SALDOXCOBRAR
                         ,SUM(EF.TOT_FINANCIABLE) TOT_FINANCIABLE
                         ,SUM(EF.VALCONSUMO) VALCONSUMO
                         ,SUM(EF.VALCONSUMOPROM) VALCONSUMOPROM
                         ,SUM(EF.DEUDAANT_ENFINAN) DEUDAANT_ENFINAN
                         ,NVL(SP_TARIFAS.CMO_ACU_712,0) CMO_ACU 
                         ,NVL(SP_TARIFAS.CMI_ACU_712,0) CMI_ACU
                         ,NVL(SP_TARIFAS.CMT_ACU_712,0) CMT_ACU
                         ,NVL(SP_TARIFAS.PORC_SUBACU_712,0) PORC_SUBACU
                         ,NVL(SP_TARIFAS.PORC_SOBREACU_712,0) PORC_SOBREACU
                         ,NVL(SP_TARIFAS.CMO_ALC_712,0) CMO_ALC
                         ,NVL(SP_TARIFAS.CMI_ALC_712,0) CMI_ALC
                         ,NVL(SP_TARIFAS.CMT_ALC_712,0) CMT_ALC
                         ,NVL(SP_TARIFAS.PORC_SUBALC_712,0) PORC_SUBALC
                         ,NVL(SP_TARIFAS.PORC_SOBREALC_712,0) PORC_SOBREALC
                    FROM     SP_ESTADISTICAS_FACTURACION EF 
                   INNER JOIN SP_TARIFAS 
                      ON EF.COMPANIA = SP_TARIFAS.COMPANIA
                     AND EF.ANO      = SP_TARIFAS.ANO
                     AND EF.PERIODO  = SP_TARIFAS.PERIODO
                     AND EF.USO      = SP_TARIFAS.USO
                     AND EF.ESTRATO  = SP_TARIFAS.ESTRATO
                   WHERE EF.COMPANIA = UN_COMPANIA
                     AND EF.ANO      = UN_ANIO
                     AND EF.PERIODO  = UN_PERIODO
                     AND EF.CICLO    = UN_CICLO
                     AND EF.CONCEPTO IN (2,5,19,24,33)
                   GROUP BY 
                         EF.COMPANIA
                         ,EF.ANO
                         ,EF.PERIODO
                         ,EF.CICLO
                         ,EF.SERVICIO
                         ,EF.USO
                         ,EF.ESTRATO
                         ,CASE WHEN EF.CONCEPTO IN (2,5,19)
                               THEN 'ACU'
                               ELSE 'ALC'
                          END
                         ,SP_TARIFAS.CMO_ACU_712
                         ,SP_TARIFAS.CMI_ACU_712
                         ,SP_TARIFAS.CMT_ACU_712
                         ,SP_TARIFAS.PORC_SUBACU_712
                         ,SP_TARIFAS.PORC_SOBREACU_712
                         ,SP_TARIFAS.CMO_ALC_712
                         ,SP_TARIFAS.CMI_ALC_712
                         ,SP_TARIFAS.CMT_ALC_712
                         ,SP_TARIFAS.PORC_SUBALC_712
                         ,SP_TARIFAS.PORC_SOBREALC_712)
    LOOP
      MI_PORCMI := 0;
      MI_PORCMT := 0;
      MI_SUBSOBRE := 0;
      MI_TPLENA := 0;
      MI_SERVICIO := NVL(MI_RS.SERVICIOS,' ');
      IF MI_SERVICIO = 'ACU' THEN
        MI_RSCMO := NVL(MI_RS.CMO_ACU, 0);
        MI_RSCMI := NVL(MI_RS.CMI_ACU, 0);
        MI_RSCMT := NVL(MI_RS.CMT_ACU, 0);
        MI_SUBSOBRE := PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => NVL(MI_RS.PORC_SUBACU, 0) <> 0,
                                             UN_SI        => MI_RS.PORC_SUBACU,
                                             UN_NO        => NVL(MI_RS.PORC_SOBREACU, 0));
      ELSIF MI_SERVICIO = 'ALC' THEN
        MI_RSCMO := NVL(MI_RS.CMO_ALC, 0);
        MI_RSCMI := NVL(MI_RS.CMI_ALC, 0);
        MI_RSCMT := NVL(MI_RS.CMT_ALC, 0);
        MI_SUBSOBRE := PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => NVL(MI_RS.PORC_SUBALC, 0) <> 0,
                                             UN_SI        => MI_RS.PORC_SUBALC,
                                             UN_NO        => NVL(MI_RS.PORC_SOBREALC, 0));
      END IF;
      IF NVL(MI_RS.PORC_SUBACU, 0) <> 0 THEN --SUBSIDIO
        MI_PORCMO := MI_RSCMO - (MI_RSCMO * MI_SUBSOBRE);
        MI_PORCMI := MI_RSCMI - (MI_RSCMI * MI_SUBSOBRE);
        MI_PORCMT := MI_RSCMT - (MI_RSCMT * MI_SUBSOBRE);
      ELSE --SOBREPRECIO
        MI_PORCMO := MI_RSCMO + (MI_RSCMO * MI_SUBSOBRE);
        MI_PORCMI := MI_RSCMI + (MI_RSCMI * MI_SUBSOBRE);
        MI_PORCMT := MI_RSCMT + (MI_RSCMT * MI_SUBSOBRE);
      END IF;
      MI_TPLENA := MI_PORCMO + MI_PORCMI + MI_PORCMT;
      IF (MI_PORCMO + MI_PORCMI + MI_PORCMT) > 0 THEN
        MI_PORCMO := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => (MI_PORCMO * 100) / MI_TPLENA,
                                             UN_PRECISION => 2);
        MI_PORCMI := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => (MI_PORCMI * 100) / MI_TPLENA,
                                             UN_PRECISION => 2);
        MI_PORCMT := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => (MI_PORCMT * 100) / MI_TPLENA,
                                             UN_PRECISION => 2);
      ELSE
        IF MI_PORCMO = 0 THEN 
          MI_PORCMO := 33; 
        END IF;
        IF MI_PORCMI = 0 THEN 
          MI_PORCMI := 33;
        END IF;
        IF MI_PORCMT = 0 THEN 
          MI_PORCMT := 34;
        END IF;
      END IF;
      IF MI_SERVICIO = 'ACU' THEN
        MI_INICIO := 221;
        MI_LIMITE := 223;
      ELSIF MI_SERVICIO = 'ALC' THEN
        MI_INICIO := 224;
        MI_LIMITE := 226;
      END IF;
      <<PORCENTAJES>>
      FOR MI_I IN MI_INICIO..MI_LIMITE LOOP
        IF MI_I = 221 OR MI_I = 224 THEN
          MI_PORCENTAJE := MI_PORCMO;
        ELSIF MI_I = 222 OR MI_I = 225 THEN
          MI_PORCENTAJE := MI_PORCMI;
        ELSE
          MI_PORCENTAJE := MI_PORCMT;
        END IF;
        BEGIN
          BEGIN
            MI_TABLA := 'SP_ESTADISTICAS_FACTURACION';
            MI_CAMPOS := 'COMPANIA,ANO,PERIODO,CICLO
                         ,SERVICIO,USO,ESTRATO,CONCEPTO
                         ,DEUDAANT,VALOR_FACTURADOANT
                         ,DEUDAIN,VALOR_FACTURADOIN
                         ,DEUDA,VALOR_FACTURADO
                         ,RECAUDADODEUDA,RECAUDADOPERIODO
                         ,DOBLEPAGO,SALDOCREDITO,ABONO
                         ,MODIFDEUDA,VALORFINACT
                         ,VALORFINANT,VALORABONOACT
                         ,VALORABONOANT,VALORABONODEUDA
                         ,RECAUDOFINACT,RECAUDOFINANT
                         ,SALDOFINOTROS,SALDOFINDEUDA
                         ,SALDOFINPERIODO,VALORFINDEUDA
                         ,VALORFINPERIODO,SALDOXCOBRAR
                         ,TOT_FINANCIABLE,VALCONSUMO
                         ,VALCONSUMOPROM,DEUDAANT_ENFINAN
                         ,DATE_CREATED,CREATED_BY';

            MI_VALORES := ''''||UN_COMPANIA||'''
                          ,'||UN_ANIO||'
                          ,'''||UN_PERIODO||'''
                          ,'||UN_CICLO||'
                          ,'''||MI_RS.SERVICIO||'''
                          ,'''||MI_RS.USO||'''
                          ,'''||MI_RS.ESTRATO||'''
                          ,'||MI_I||'
                          ,'||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_RS.DEUDAANT * MI_PORCENTAJE)/100)||'
                          ,'||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_RS.VALOR_FACTURADOANT * MI_PORCENTAJE)/100)||'
                          ,'||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_RS.DEUDAIN * MI_PORCENTAJE)/100)||'
                          ,'||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_RS.VALOR_FACTURADOIN * MI_PORCENTAJE)/100)||'
                          ,'||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_RS.DEUDA * MI_PORCENTAJE)/100)||'
                          ,'||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_RS.VALOR_FACTURADO * MI_PORCENTAJE)/100)||'
                          ,'||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_RS.RECAUDADODEUDA * MI_PORCENTAJE)/100)||'
                          ,'||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_RS.RECAUDADOPERIODO * MI_PORCENTAJE)/100)||'
                          ,'||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_RS.DOBLEPAGO * MI_PORCENTAJE)/100)||'
                          ,'||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_RS.SALDOCREDITO * MI_PORCENTAJE)/100)||'
                          ,'||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_RS.ABONO * MI_PORCENTAJE)/100)||'
                          ,'||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_RS.MODIFDEUDA * MI_PORCENTAJE)/100)||'
                          ,'||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_RS.VALORFINACT * MI_PORCENTAJE)/100)||'
                          ,'||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_RS.VALORFINANT * MI_PORCENTAJE)/100)||'
                          ,'||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_RS.VALORABONOACT * MI_PORCENTAJE)/100)||'
                          ,'||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_RS.VALORABONOANT * MI_PORCENTAJE)/100)||'
                          ,'||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_RS.VALORABONODEUDA * MI_PORCENTAJE)/100)||'
                          ,'||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_RS.RECAUDOFINACT * MI_PORCENTAJE)/100)||'
                          ,'||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_RS.RECAUDOFINANT * MI_PORCENTAJE)/100)||'
                          ,'||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_RS.SALDOFINOTROS * MI_PORCENTAJE)/100)||'
                          ,'||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_RS.SALDOFINDEUDA * MI_PORCENTAJE)/100)||'
                          ,'||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_RS.SALDOFINPERIODO * MI_PORCENTAJE)/100)||'
                          ,'||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_RS.VALORFINDEUDA * MI_PORCENTAJE)/100)||'
                          ,'||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_RS.VALORFINPERIODO * MI_PORCENTAJE)/100)||'
                          ,'||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_RS.SALDOXCOBRAR * MI_PORCENTAJE)/100)||'
                          ,'||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_RS.TOT_FINANCIABLE * MI_PORCENTAJE)/100)||'
                          ,'||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_RS.VALCONSUMO * MI_PORCENTAJE)/100)||'
                          ,'||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_RS.VALCONSUMOPROM * MI_PORCENTAJE)/100)||'
                          ,'||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (NVL(MI_RS.DEUDAANT_ENFINAN, 0) * MI_PORCENTAJE)/100)||'
                          ,SYSDATE,'''||UN_USUARIO||'''';

            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA,
                                                  UN_ACCION => 'I',
                                                  UN_CAMPOS => MI_CAMPOS,
                                                  UN_VALORES => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_INSERESTAFACTURA
            );
        END;
        IF MI_I = MI_LIMITE THEN
          MI_RSCAMPOS(1).CLAVE := 'DEUDAANT';
          MI_RSCAMPOS(1).VALOR := MI_RS.DEUDAANT;
          MI_RSCAMPOS(2).CLAVE := 'VALOR_FACTURADOANT';
          MI_RSCAMPOS(2).VALOR := MI_RS.VALOR_FACTURADOANT;
          MI_RSCAMPOS(3).CLAVE := 'DEUDAIN';
          MI_RSCAMPOS(3).VALOR := MI_RS.DEUDAIN;
          MI_RSCAMPOS(4).CLAVE := 'VALOR_FACTURADOIN';
          MI_RSCAMPOS(4).VALOR := MI_RS.VALOR_FACTURADOIN;
          MI_RSCAMPOS(5).CLAVE := 'DEUDA';
          MI_RSCAMPOS(5).VALOR := MI_RS.DEUDA;
          MI_RSCAMPOS(6).CLAVE := 'VALOR_FACTURADO';
          MI_RSCAMPOS(6).VALOR := MI_RS.VALOR_FACTURADO;
          MI_RSCAMPOS(7).CLAVE := 'RECAUDADODEUDA';
          MI_RSCAMPOS(7).VALOR := MI_RS.RECAUDADODEUDA;
          MI_RSCAMPOS(8).CLAVE := 'RECAUDADOPERIODO';
          MI_RSCAMPOS(8).VALOR := MI_RS.RECAUDADOPERIODO;
          MI_RSCAMPOS(9).CLAVE := 'DOBLEPAGO';
          MI_RSCAMPOS(9).VALOR := MI_RS.DOBLEPAGO;
          MI_RSCAMPOS(10).CLAVE := 'SALDOCREDITO';
          MI_RSCAMPOS(10).VALOR := MI_RS.SALDOCREDITO;
          MI_RSCAMPOS(11).CLAVE := 'ABONO';
          MI_RSCAMPOS(11).VALOR := MI_RS.ABONO;
          MI_RSCAMPOS(12).CLAVE := 'MODIFDEUDA';
          MI_RSCAMPOS(12).VALOR := MI_RS.MODIFDEUDA;
          MI_RSCAMPOS(13).CLAVE := 'VALORFINACT';
          MI_RSCAMPOS(13).VALOR := MI_RS.VALORFINACT;
          MI_RSCAMPOS(14).CLAVE := 'VALORFINANT';
          MI_RSCAMPOS(14).VALOR := MI_RS.VALORFINANT;
          MI_RSCAMPOS(15).CLAVE := 'VALORABONOACT';
          MI_RSCAMPOS(15).VALOR := MI_RS.VALORABONOACT;
          MI_RSCAMPOS(16).CLAVE := 'VALORABONOANT';
          MI_RSCAMPOS(16).VALOR := MI_RS.VALORABONOANT;
          MI_RSCAMPOS(17).CLAVE := 'VALORABONODEUDA';
          MI_RSCAMPOS(17).VALOR := MI_RS.VALORABONODEUDA;
          MI_RSCAMPOS(18).CLAVE := 'RECAUDOFINACT';
          MI_RSCAMPOS(18).VALOR := MI_RS.RECAUDOFINACT;
          MI_RSCAMPOS(19).CLAVE := 'RECAUDOFINANT';
          MI_RSCAMPOS(19).VALOR := MI_RS.RECAUDOFINANT;
          MI_RSCAMPOS(20).CLAVE := 'SALDOFINOTROS';
          MI_RSCAMPOS(20).VALOR := MI_RS.SALDOFINOTROS;
          MI_RSCAMPOS(21).CLAVE := 'SALDOFINDEUDA';
          MI_RSCAMPOS(21).VALOR := MI_RS.SALDOFINDEUDA;
          MI_RSCAMPOS(22).CLAVE := 'SALDOFINPERIODO';
          MI_RSCAMPOS(22).VALOR := MI_RS.SALDOFINPERIODO;
          MI_RSCAMPOS(23).CLAVE := 'VALORFINDEUDA';
          MI_RSCAMPOS(23).VALOR := MI_RS.VALORFINDEUDA;
          MI_RSCAMPOS(24).CLAVE := 'VALORFINPERIODO';
          MI_RSCAMPOS(24).VALOR := MI_RS.VALORFINPERIODO;
          MI_RSCAMPOS(25).CLAVE := 'SALDOXCOBRAR';
          MI_RSCAMPOS(25).VALOR := MI_RS.SALDOXCOBRAR;
          MI_RSCAMPOS(26).CLAVE := 'TOT_FINANCIABLE';
          MI_RSCAMPOS(26).VALOR := MI_RS.TOT_FINANCIABLE;
          MI_RSCAMPOS(27).CLAVE := 'VALCONSUMO';
          MI_RSCAMPOS(27).VALOR := MI_RS.VALCONSUMO;
          MI_RSCAMPOS(28).CLAVE := 'VALCONSUMOPROM';
          MI_RSCAMPOS(28).VALOR := MI_RS.VALCONSUMOPROM;
          MI_RSCAMPOS(29).CLAVE := 'DEUDAANT_ENFINAN';
          MI_RSCAMPOS(29).VALOR := MI_RS.DEUDAANT_ENFINAN;
          <<ESTADISTICASCAMPOS>>
          FOR MI_F IN 1..29 LOOP
            MI_DIFERENCIA := NVL(MI_RSCAMPOS(MI_F).VALOR, 0)
                          - (PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (NVL(MI_RSCAMPOS(MI_F).VALOR, 0)*MI_PORCMO)/100) 
                          +  PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (NVL(MI_RSCAMPOS(MI_F).VALOR, 0)*MI_PORCMI)/100)
                          +  PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (NVL(MI_RSCAMPOS(MI_F).VALOR, 0)*MI_PORCMT)/100));
            IF MI_DIFERENCIA <> 0 THEN
              BEGIN
                BEGIN
                  MI_TABLA := 'SP_ESTADISTICAS_FACTURACION';
                  MI_CAMPOS := MI_RSCAMPOS(MI_F).CLAVE||' = '||MI_RSCAMPOS(MI_F).CLAVE||' + '||MI_DIFERENCIA||',
                               MODIFIED_BY    = '''||UN_USUARIO||''',
                               DATE_MODIFIED  = SYSDATE  ';
                  MI_CONDICION := '    COMPANIA = '''||UN_COMPANIA||'''
                                   AND ANO      = '  ||UN_ANIO||'
                                   AND PERIODO  = '''||UN_PERIODO||'''
                                   AND CICLO    = '  ||UN_CICLO||'
                                   AND USO      = '''||MI_RS.USO||'''
                                   AND ESTRATO  = '''||MI_RS.ESTRATO||'''
                                   AND CONCEPTO = '  ||CASE WHEN MI_SERVICIO = 'ACU'
                                                            THEN '223'
                                                            ELSE '226'
                                                       END;
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                        UN_ACCION    => 'M',
                                                        UN_CAMPOS    => MI_CAMPOS,
                                                        UN_CONDICION => MI_CONDICION);
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                  MI_MSGERROR(1).CLAVE := 'CONDICION';
                  MI_MSGERROR(1).VALOR := 'COMPANIA= '''||UN_COMPANIA||
                                          ''', ANO= '||UN_ANIO||
                                          ', PERIODO= '''||UN_PERIODO||
                                          ''', CICLO= '||UN_CICLO||
                                          ', USO= '''||MI_RS.USO||
                                          ''' y ESTRATO= '''||MI_RS.ESTRATO||'''';
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTESTAFACTURACION,
                    UN_REEMPLAZOS => MI_MSGERROR
                  );
              END; 
            END IF;
          END LOOP ESTADISTICASCAMPOS;
        END IF;
      END LOOP PORCENTAJES;
    END LOOP ESTADISTICASFAC;
  END PR_DISCRIMINARES688;

  -- 17
  PROCEDURE PR_ESTADISTICAFACTURACION
    /*
      NAME              : PR_ESTADISTICAFACTURACION --> EN ACCESS EstadisticaFacturacion
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MIGRADOR     : 14/02/2017
      TIME              : 02:55 PM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PROCEDIMIENTO QUE ACTUALIZA ESTADISTICAS DE FACTURACION.
      PARAMETERS        : UN_COMPANIA    => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_CICLO       => CICLO SELECCIONADO.
      MODIFICATIONS     : 
      @NAME:  actualizarEstadisticaFacturacion
      @METHOD:  POST
    */ 
   (
    UN_COMPANIA         IN  PCK_SUBTIPOS.TI_COMPANIA
   ,UN_CICLO            IN  PCK_SUBTIPOS.TI_CICLO
   ,UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO

  )
  AS
    MI_ANIOANTERIOR         PCK_SUBTIPOS.TI_ANIO;
    MI_PERIODOANTERIOR      PCK_SUBTIPOS.TI_PERIODO;
    MI_ANIO                 PCK_SUBTIPOS.TI_ANIO;
    MI_PERIODO              PCK_SUBTIPOS.TI_PERIODO;
    MI_CALSUS               VARCHAR2(500 CHAR);
    MI_EFECTOCERO           VARCHAR2(5 CHAR);
    MI_AFECTARDEUDAANT      VARCHAR2(100 CHAR);
    MI_TABLA                PCK_SUBTIPOS.TI_TABLA;
    MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
    MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
    MI_MERGEUSING           PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEEXISTE          PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_MERGEENLACE          PCK_SUBTIPOS.TI_MERGEENLACE;
    UN_MERGENOEXIS          PCK_SUBTIPOS.TI_MERGENOEXISTE;
    MI_CAMP                 VARCHAR2(1 CHAR);
    MI_STRSQL               PCK_SUBTIPOS.TI_STRSQL;
    MI_RSANT2               SYS_REFCURSOR;
    MI_RSANT                SYS_REFCURSOR;
    MI_USO                  SP_USUARIO.USO%TYPE;
    MI_ESTRATO              SP_USUARIO.ESTRATO%TYPE;
    MI_CONCEPTO             SP_FINANCIABLES.CONCEPTO%TYPE;
    MI_SALDOFINOTROS        PCK_SUBTIPOS.TI_DOBLE;
    MI_MONTOFINANCIAR       PCK_SUBTIPOS.TI_DOBLE;
    MI_RESPUESTA            NUMBER;
    MI_SERVICIO_CODIGO      SP_SERVICIO.CODIGO%TYPE;

  BEGIN
    IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                             UN_NOMBRE    => 'DEUDA SIN MODIFICACIONES, ESTADISTICA FACTURACION',
                             UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                             UN_FECHA_PAR => SYSDATE) = 'SI' THEN 
      MI_AFECTARDEUDAANT := ' - VISTA.VALORABONOACT - VISTA.VALORABONOANT';
    END IF;
    MI_EFECTOCERO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                           UN_NOMBRE    => 'ESTADISTICA CON EFECTO CERO DE FINANCIACIONES',
                                           UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                           UN_FECHA_PAR => SYSDATE);
    BEGIN  
      BEGIN
        SELECT ANO
              ,PERIODO
        INTO   MI_ANIO
              ,MI_PERIODO
        FROM   SP_USUARIO
        WHERE  COMPANIA = UN_COMPANIA
          AND  CICLO    = UN_CICLO
          AND  ROWNUM = 1;
        EXCEPTION WHEN NO_DATA_FOUND THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CICLO';
        MI_MSGERROR(1).VALOR := UN_CICLO;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_NOUSUARIOSCICLO,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
    -- SE BORRAN LOS DATOS DE ESTADISTICAS ANTERIORES
    BEGIN
      BEGIN
        MI_TABLA := 'SP_ESTADISTICAS_FACTURACION';
        MI_CONDICION := '    COMPANIA = '''||UN_COMPANIA||'''
                         AND CICLO    = '  ||UN_CICLO||'
                         AND ANO      = '  ||MI_ANIO||'
                         AND PERIODO  = '''||MI_PERIODO||'''';
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'E',
                                              UN_CONDICION => MI_CONDICION); 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CONDICION';
        MI_MSGERROR(1).VALOR := 'COMPANIA= '''||UN_COMPANIA||
                                ''', CICLO= '||UN_CICLO||
                                ', ANO= '||MI_ANIO||
                                ' y PERIODO= '''||MI_PERIODO||'''';
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_BORESTAFACTURACION,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
    IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                 UN_NOMBRE    => 'PERMITE CALCULO SUSPENDIDOS',
                                 UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                 UN_FECHA_PAR => SYSDATE), 'NO') = 'SI' THEN
      MI_CALSUS := ' AND SP_USUARIO.ESTADO NOT IN (''R'')';
    ELSE
      MI_CALSUS := ' AND (SP_USUARIO.ESTADO NOT IN (''R'',''S'') 
                      OR (SP_USUARIO.ESTADO IN (''S'') 
                     AND SP_USUARIO.FECHACAMBIOEST >= SP_CICLO.FECHA_PREPARACION))';
    END IF;
    <<SERVICIOS>>
   SELECT CODIGO 
     INTO MI_SERVICIO_CODIGO
     FROM SP_SERVICIO 
    WHERE COMPANIA = UN_COMPANIA 
    AND ROWNUM = 1
    ORDER BY COMPANIA,
          CODIGO;

      IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                               UN_NOMBRE    => 'MANEJA ESTADISTICA ESPECIAL',
                               UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                               UN_FECHA_PAR => SYSDATE) = 'SI' THEN
        BEGIN
          BEGIN
            MI_TABLA := 'SP_ESTADISTICAS_FACTURACION';
            MI_CAMPOS := 'COMPANIA,ANO,PERIODO,CICLO,SERVICIO
                         ,USO,ESTRATO,CONCEPTO,DEUDAANT
                         ,VALOR_FACTURADOANT,DEUDAIN,VALOR_FACTURADOIN
                         ,DEUDA,VALOR_FACTURADO,RECAUDADODEUDA
                         ,RECAUDADOPERIODO,DOBLEPAGO,SALDOCREDITO
                         ,ABONO,MODIFDEUDA,VALORFINACT,VALORFINANT
                         ,VALORABONOACT,SALDOFINDEUDA,SALDOFINPERIODO
                         ,VALORFINPERIODO,VALORFINDEUDA,VALORABONOANT
                         ,RECAUDOFINACT,RECAUDOFINANT
                         ,DATE_CREATED,CREATED_BY';

            MI_VALORES := 'SELECT SP_USUARIO.COMPANIA
                                  ,SP_USUARIO.ANO
                                  ,SP_USUARIO.PERIODO
                                  ,SP_USUARIO.CICLO
                                  ,'''||MI_SERVICIO_CODIGO||'''
                                  ,SP_USUARIO.USO
                                  ,SP_USUARIO.ESTRATO
                                  ,SP_FACTURADO.CONCEPTO
                                  ,0,SUM(SP_FACTURADO.VALOR_FACTURADOANT)
                                  ,SUM(SP_FACTURADO.DEUDAIN)
                                  ,SUM(SP_FACTURADO.VALOR_FACTURADOIN)
                                  ,SUM(SP_FACTURADO.DEUDA)
                                  ,SUM(SP_FACTURADO.VALOR_FACTURADO)
                                  ,SUM(CASE WHEN NVL(SP_USUARIO.BANCOPERPROCESO,'' '') <> '' ''
                                            THEN SP_FACTURADO.DEUDA - NVL(SP_FACTURADO.VALORABONOANT,0)
                                            ELSE 0
                                       END)
                                  ,SUM(CASE WHEN NVL(SP_USUARIO.BANCOPERPROCESO,'' '') <> '' ''
                                            THEN SP_FACTURADO.VALOR_FACTURADO 
                                               - NVL(SP_FACTURADO.VALORABONOACT,0)
                                               - SP_FACTURADO.CREDITOABONADO
                                            ELSE 0
                                       END)
                                  ,SUM(SP_FACTURADO.DOBLEPAGO)
                                  ,SUM(SP_FACTURADO.CREDITOABONADO)
                                  ,SUM(SP_FACTURADO.ABONO)
                                  ,SUM(CASE WHEN NVL(SP_USUARIO.BANCOPERANTERIOR,'' '') <> '' ''
                                            THEN 0
                                            ELSE SP_FACTURADO.DEUDA
                                               - SP_FACTURADO.DEUDAANT
                                               - SP_FACTURADO.VALOR_FACTURADOANT
                                       END)
                                  ,SUM(SP_FACTURADO.VALORFINACT)
                                  ,SUM(SP_FACTURADO.VALORFINANT)
                                  ,SUM(SP_FACTURADO.VALORABONOACT)
                                  ,SUM(NVL(SP_D_DEUDAFACTURADAFINANCIADA.SALDOFINANT,0))
                                  ,SUM(NVL(SP_D_DEUDAFACTURADAFINANCIADA.SALDOFINACT,0))
                                  ,SUM(NVL(SP_D_DEUDAFACTURADAFINANCIADA.VALORFINPERIODO,0))
                                  ,SUM(NVL(SP_D_DEUDAFACTURADAFINANCIADA.VALORFINDEUDA,0))
                                  ,SUM(SP_FACTURADO.VALORABONOANT)
                                  ,SUM(CASE WHEN NVL(SP_USUARIO.BANCOPERPROCESO,'' '') <> '' ''
                                            THEN SP_FACTURADO.VALORFINACT
                                            ELSE 0
                                       END)
                                  ,SUM(CASE WHEN NVL(SP_USUARIO.BANCOPERPROCESO,'' '') <> '' ''
                                            THEN SP_FACTURADO.VALORFINANT
                                            ELSE 0
                                       END)
                                  SYSDATE,'''||UN_USUARIO||'''     
                             FROM SP_USUARIO 
                            INNER JOIN SP_FACTURADO 
                               ON SP_USUARIO.PERIODO    = SP_FACTURADO.PERIODO
                              AND SP_USUARIO.ANO        = SP_FACTURADO.ANO
                              AND SP_USUARIO.CODIGORUTA = SP_FACTURADO.CODIGORUTA
                              AND SP_USUARIO.CODIGORUTA = SP_FACTURADO.CODIGORUTA
                              AND SP_USUARIO.CICLO      = SP_FACTURADO.CICLO
                              AND SP_USUARIO.COMPANIA   = SP_FACTURADO.COMPANIA
                             LEFT JOIN SP_D_DEUDAFACTURADAFINANCIADA 
                               ON SP_FACTURADO.CONCEPTO   = SP_D_DEUDAFACTURADAFINANCIADA.CONCEPTO
                              AND SP_FACTURADO.PERIODO    = SP_D_DEUDAFACTURADAFINANCIADA.PERIODO
                              AND SP_FACTURADO.ANO        = SP_D_DEUDAFACTURADAFINANCIADA.ANO
                              AND SP_FACTURADO.CODIGORUTA = SP_D_DEUDAFACTURADAFINANCIADA.CODIGORUTA
                              AND SP_FACTURADO.CICLO      = SP_D_DEUDAFACTURADAFINANCIADA.CICLO
                              AND SP_FACTURADO.COMPANIA   = SP_D_DEUDAFACTURADAFINANCIADA.COMPANIA
                            INNER JOIN SP_CICLO 
                               ON SP_USUARIO.COMPANIA = SP_CICLO.COMPANIA
                              AND SP_USUARIO.CICLO    = SP_CICLO.NUMERO
                            WHERE SP_USUARIO.COMPANIA = '''||UN_COMPANIA||'''
                              AND SP_USUARIO.CICLO    = '  ||UN_CICLO||'
                              AND SP_USUARIO.ANO      = '  ||MI_ANIO||'
                              AND SP_USUARIO.PERIODO  = '''||MI_PERIODO||''''||
                             MI_CALSUS||'
                            GROUP BY 
                                  SP_USUARIO.COMPANIA
                                  ,SP_USUARIO.ANO
                                  ,SP_USUARIO.PERIODO
                                  ,SP_USUARIO.CICLO
                                  ,'''||MI_SERVICIO_CODIGO||'''
                                  ,SP_USUARIO.USO
                                  ,SP_USUARIO.ESTRATO
                                  ,SP_FACTURADO.CONCEPTO,
                                  SYSDATE,'''||UN_USUARIO||'''';

            MI_RESPUESTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                  UN_ACCION  => 'IS',
                                                  UN_CAMPOS  => MI_CAMPOS,
                                                  UN_VALORES => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_INSERESTAFACTURA
            );
        END;
      ELSE
        BEGIN
          BEGIN
            MI_TABLA := 'SP_ESTADISTICAS_FACTURACION';
            MI_CAMPOS := 'COMPANIA,ANO,PERIODO,CICLO,SERVICIO,USO
                         ,ESTRATO,CONCEPTO,DEUDAANT
                         ,VALOR_FACTURADOANT,DEUDAIN
                         ,VALOR_FACTURADOIN,DEUDA,VALOR_FACTURADO
                         ,RECAUDADODEUDA,RECAUDADOPERIODO,DOBLEPAGO
                         ,SALDOCREDITO,ABONO,MODIFDEUDA,VALORFINACT
                         ,VALORFINANT,VALORABONOACT,SALDOFINDEUDA
                         ,SALDOFINPERIODO,VALORABONOANT,RECAUDOFINACT
                         ,RECAUDOFINANT,VALCONSUMO,VALCONSUMOPROM
                         ,CONSUMO_BASICO,CONSUMO_COMPL,CONSUMO_SUNT
                         ,DATE_CREATED,CREATED_BY';

            MI_VALORES := 'SELECT SP_FACTURADO.COMPANIA
                                  ,SP_FACTURADO.ANO
                                  ,SP_FACTURADO.PERIODO
                                  ,SP_FACTURADO.CICLO
                                  ,''01'',SP_USUARIO.USO
                                  ,SP_USUARIO.ESTRATO
                                  ,SP_FACTURADO.CONCEPTO
                                  ,0,SUM(SP_FACTURADO.VALOR_FACTURADOANT)
                                  ,SUM(SP_FACTURADO.DEUDAIN)
                                  ,SUM(SP_FACTURADO.VALOR_FACTURADOIN)
                                  ,SUM(SP_FACTURADO.DEUDA)
                                  ,SUM(SP_FACTURADO.VALOR_FACTURADO)
                                  ,SUM(CASE WHEN NVL(SP_USUARIO.BANCOPERPROCESO,'' '') <> '' ''
                                            THEN SP_FACTURADO.DEUDA
                                            ELSE 0
                                       END)
                                  ,SUM(CASE WHEN NVL(SP_USUARIO.BANCOPERPROCESO,'' '') <> '' ''
                                            THEN SP_FACTURADO.VALOR_FACTURADO
                                            ELSE 0
                                       END)
                                  ,SUM(SP_FACTURADO.DOBLEPAGO)
                                  ,SUM(SP_FACTURADO.CREDITOABONADO)
                                  ,SUM(SP_FACTURADO.ABONO)
                                  ,SUM(CASE WHEN NVL(SP_USUARIO.BANCOPERANTERIOR,'' '') <> '' ''
                                            THEN 0
                                            ELSE SP_FACTURADO.DEUDA
                                               - SP_FACTURADO.DEUDAANT
                                               - SP_FACTURADO.VALOR_FACTURADOANT
                                       END)
                                  ,SUM(SP_FACTURADO.VALORFINACT)
                                  ,SUM(SP_FACTURADO.VALORFINANT)
                                  ,SUM(SP_FACTURADO.VALORABONOACT)
                                  ,SUM(NVL(SP_D_DEUDAFACTURADAFINANCIADA.SALDOFINANT,0))
                                  ,SUM(NVL(SP_D_DEUDAFACTURADAFINANCIADA.SALDOFINACT,0))
                                  ,SUM(SP_FACTURADO.VALORABONOANT)
                                  ,SUM(CASE WHEN NVL(SP_USUARIO.BANCOPERPROCESO,'' '') <> '' ''
                                            THEN SP_FACTURADO.VALORFINACT
                                            ELSE 0
                                       END)
                                  ,SUM(CASE WHEN NVL(SP_USUARIO.BANCOPERPROCESO,'' '') <> '' ''
                                            THEN SP_FACTURADO.VALORFINANT
                                            ELSE 0
                                       END)
                                  ,PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (SUM(CASE WHEN SP_FACTURADO.CONCEPTO = 2
                                                                                 THEN SP_USUARIO.VALCONSUMO
                                                                                 ELSE (CASE WHEN SP_FACTURADO.CONCEPTO = 5
                                                                                            THEN SP_USUARIO.VALCONSUMOPROM
                                                                                            ELSE (CASE WHEN SP_FACTURADO.CONCEPTO = 33
                                                                                                       THEN SP_USUARIO.VALORALCBASICO
                                                                                                          + SP_USUARIO.VALORALCCOMPLEMENTARIO
                                                                                                          + SP_USUARIO.VALORALCSUNTUARIO
                                                                                                       ELSE 0
                                                                                                  END)
                                                                                       END)
                                                                            END)))
                                  ,PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (SUM(CASE WHEN SP_FACTURADO.CONCEPTO IN (2,5,33)
                                                                                 THEN SP_USUARIO.VALCONSUMOPROM
                                                                                  ELSE 0
                                                                            END)))
                                  ,PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (SUM(CASE WHEN SP_FACTURADO.CONCEPTO IN (2,5)
                                                                                 THEN SP_USUARIO.VALORBASICO
                                                                                 ELSE (CASE WHEN SP_FACTURADO.CONCEPTO = 33
                                                                                            THEN SP_USUARIO.VALORALCBASICO
                                                                                            ELSE 0
                                                                                       END)
                                                                            END)))
                                  ,PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (SUM(CASE WHEN SP_FACTURADO.CONCEPTO IN (2,5)
                                                                                 THEN SP_USUARIO.VALORCOMPLEMENTARIO
                                                                                 ELSE (CASE WHEN SP_FACTURADO.CONCEPTO = 33
                                                                                            THEN SP_USUARIO.VALORALCCOMPLEMENTARIO
                                                                                            ELSE 0
                                                                                       END)
                                                                            END)))
                                  ,PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (SUM(CASE WHEN SP_FACTURADO.CONCEPTO IN (2,5)
                                                                                 THEN SP_USUARIO.VALORSUNTUARIO
                                                                                 ELSE (CASE WHEN SP_FACTURADO.CONCEPTO = 33
                                                                                            THEN SP_USUARIO.VALORALCSUNTUARIO
                                                                                            ELSE 0
                                                                                        END)
                                                                          END)))
                                   ,SYSDATE,'''||UN_USUARIO||'''                                      
                              FROM SP_USUARIO 
                             INNER JOIN SP_FACTURADO 
                                ON SP_USUARIO.PERIODO    = SP_FACTURADO.PERIODO
                               AND SP_USUARIO.ANO        = SP_FACTURADO.ANO
                               AND SP_USUARIO.CODIGORUTA = SP_FACTURADO.CODIGORUTA
                               AND SP_USUARIO.CICLO      = SP_FACTURADO.CICLO
                               AND SP_USUARIO.COMPANIA   = SP_FACTURADO.COMPANIA
                              LEFT JOIN SP_D_DEUDAFACTURADAFINANCIADA 
                                ON SP_FACTURADO.CONCEPTO   = SP_D_DEUDAFACTURADAFINANCIADA.CONCEPTO
                               AND SP_FACTURADO.PERIODO    = SP_D_DEUDAFACTURADAFINANCIADA.PERIODO
                               AND SP_FACTURADO.ANO        = SP_D_DEUDAFACTURADAFINANCIADA.ANO
                               AND SP_FACTURADO.CODIGORUTA = SP_D_DEUDAFACTURADAFINANCIADA.CODIGORUTA
                               AND SP_FACTURADO.CICLO      = SP_D_DEUDAFACTURADAFINANCIADA.CICLO
                               AND SP_FACTURADO.COMPANIA   = SP_D_DEUDAFACTURADAFINANCIADA.COMPANIA
                             INNER JOIN SP_CICLO 
                                ON SP_USUARIO.COMPANIA = SP_CICLO.COMPANIA
                               AND SP_USUARIO.CICLO    = SP_CICLO.NUMERO
                             WHERE SP_FACTURADO.COMPANIA = '''||UN_COMPANIA||'''
                               AND SP_FACTURADO.CICLO    = '  ||UN_CICLO||'
                               AND SP_FACTURADO.ANO      = '  ||MI_ANIO||'
                               AND SP_FACTURADO.PERIODO  = '''||MI_PERIODO||''''||
                              MI_CALSUS||'
                            GROUP BY
                                  SP_FACTURADO.COMPANIA
                                  ,SP_FACTURADO.ANO
                                  ,SP_FACTURADO.PERIODO
                                  ,SP_FACTURADO.CICLO
                                  ,'''||MI_SERVICIO_CODIGO||'''
                                  ,SP_USUARIO.USO
                                  ,SP_USUARIO.ESTRATO
                                  ,SP_FACTURADO.CONCEPTO
                                  ,SYSDATE,'''||UN_USUARIO||'''';

            MI_RESPUESTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                  UN_ACCION  => 'IS',
                                                  UN_CAMPOS  => MI_CAMPOS,
                                                  UN_VALORES => MI_VALORES);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_INSERESTAFACTURA
            );
        END;
      END IF;
      MI_ANIOANTERIOR := PCK_SERVICIOS_PUBLICOS.FC_ANO_PERIODO_ANTERIOR(UN_COMPANIA     => UN_COMPANIA, 
                                                                        UN_ANO          => MI_ANIO,
                                                                        UN_PERIODO      => MI_PERIODO,
                                                                        UN_TIPO_RETORNO => '0');
      MI_PERIODOANTERIOR := PCK_SERVICIOS_PUBLICOS.FC_ANO_PERIODO_ANTERIOR(UN_COMPANIA     => UN_COMPANIA, 
                                                                           UN_ANO          => MI_ANIO,
                                                                           UN_PERIODO      => MI_PERIODO,
                                                                           UN_TIPO_RETORNO => '1');
      BEGIN
        BEGIN

          MI_TABLA := 'SP_ESTADISTICAS_FACTURACION';

          MI_MERGEUSING :='SELECT COMPANIA,
                                  '||MI_ANIO||' ANO,
                                  '''||MI_PERIODO||''' PERIODO ,
                                  CICLO,
                                  SERVICIO,
                                  USO,
                                  ESTRATO,
                                  CONCEPTO,
                                  0 DEUDAANT,
                                  0 VALOR_FACTURADO,
                                  0 RECAUDADODEUDA,
                                  0 RECAUDADOPERIODO,
                                  0 DOBLEPAGO,
                                  0 SALDOCREDITO,
                                  0 ABONO,
                                  0 VALOR_FACTURADOANT,
                                  0 DEUDAIN,
                                  0 VALOR_FACTURADOIN,
                                  0 DEUDA,
                                  SYSDATE DATE_CREATED,
                                  '''||UN_USUARIO||''' CREATED_BY
                            FROM SP_ESTADISTICAS_FACTURACION E0
                           WHERE E0.COMPANIA = '''||UN_COMPANIA||'''
                             AND E0.CICLO    = '  ||UN_CICLO||'
                             AND E0.ANO      = '  ||MI_ANIOANTERIOR||'
                             AND E0.PERIODO  = '''||MI_PERIODOANTERIOR||'''';


          MI_MERGEENLACE := '  TABLA.COMPANIA = VISTA.COMPANIA
                           AND TABLA.ANO      = VISTA.ANO
                           AND TABLA.PERIODO  = VISTA.PERIODO
                           AND TABLA.CICLO    = VISTA.CICLO
                           AND TABLA.SERVICIO = VISTA.SERVICIO
                           AND TABLA.USO      = VISTA.USO
                           AND TABLA.ESTRATO  = VISTA.ESTRATO
                           AND TABLA.CONCEPTO = VISTA.CONCEPTO';

          UN_MERGENOEXIS:= 'INSERT (COMPANIA,ANO,PERIODO,CICLO,SERVICIO,USO
                       ,ESTRATO,CONCEPTO,DEUDAANT,VALOR_FACTURADOANT
                       ,DEUDAIN,VALOR_FACTURADOIN,DEUDA,VALOR_FACTURADO
                       ,RECAUDADODEUDA,RECAUDADOPERIODO,DOBLEPAGO
                       ,SALDOCREDITO,ABONO,DATE_CREATED,CREATED_BY) 

   VALUES ( VISTA.COMPANIA,VISTA.ANO,VISTA.PERIODO,VISTA.CICLO,VISTA.SERVICIO,VISTA.USO,VISTA.ESTRATO,VISTA.CONCEPTO,
            VISTA.DEUDAANT,VISTA.VALOR_FACTURADOANT,VISTA.DEUDAIN,VISTA.VALOR_FACTURADOIN,VISTA.DEUDA,VISTA.VALOR_FACTURADO,
            VISTA.RECAUDADODEUDA,VISTA.RECAUDADOPERIODO,VISTA.DOBLEPAGO,VISTA.SALDOCREDITO,VISTA.ABONO,VISTA.DATE_CREATED,VISTA.CREATED_BY)';

          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA,
                                                UN_ACCION      => 'IN',
                                                UN_MERGEUSING  => MI_MERGEUSING,
                                                UN_MERGEENLACE => MI_MERGEENLACE,
                                                UN_MERGENOEXIS => UN_MERGENOEXIS);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_INSERESTAFACTURA
          );
      END;
      BEGIN
        BEGIN
          MI_TABLA := 'SP_ESTADISTICAS_FACTURACION';
          MI_MERGEUSING := 'SELECT E0.COMPANIA
                                  ,E0.ANO
                                  ,E0.PERIODO
                                  ,E0.CICLO
                                  ,E0.SERVICIO
                                  ,E0.USO
                                  ,E0.ESTRATO
                                  ,E0.CONCEPTO
                                  ,E1.DEUDA
                                  ,E1.VALOR_FACTURADO
                                  ,E1.VALORFINANT
                                  ,E1.VALORFINACT
                                  ,E1.RECAUDADODEUDA
                                  ,E1.RECAUDADOPERIODO
                                  ,E1.RECAUDOFINACT
                                  ,E1.RECAUDOFINANT
                                  ,E1.SALDOCREDITO
                                  ,E1.VALORABONOANT
                                  ,E1.VALORABONOACT
                                  ,E1.SALDOFINDEUDA
                                  ,E1.SALDOFINPERIODO
                                  ,E1.SALDOFINOTROS
                             FROM SP_ESTADISTICAS_FACTURACION E0
                             LEFT JOIN SP_ESTADISTICAS_FACTURACION E1
                               ON E0.COMPANIA = E1.COMPANIA
                              AND E0.CICLO    = E1.CICLO
                              AND E0.SERVICIO = E1.SERVICIO
                              AND E0.USO      = E1.USO
                              AND E0.ESTRATO  = E1.ESTRATO
                              AND E0.CONCEPTO = E1.CONCEPTO
                            WHERE E1.ANO      = '  ||MI_ANIOANTERIOR||'
                              AND E1.PERIODO  = '''||MI_PERIODOANTERIOR||'''
                              AND E0.ANO      = '  ||MI_ANIO||'
                              AND E0.PERIODO  = '''||MI_PERIODO||'''
                              AND E0.COMPANIA = '''||UN_COMPANIA||'''
                              AND E0.CICLO    = '  ||UN_CICLO;
          IF MI_EFECTOCERO = 'SI' THEN
            IF MI_AFECTARDEUDAANT <> ' ' THEN
              MI_MERGEEXISTE := 'UPDATE
                                   SET TABLA.DEUDAANT = CASE WHEN TABLA.CONCEPTO = 49
                                                             THEN 0
                                                             ELSE  (VISTA.DEUDA + VISTA.VALOR_FACTURADO
                                                                 +  VISTA.VALORFINANT + VISTA.VALORFINACT)
                                                                 - (VISTA.RECAUDADODEUDA + VISTA.RECAUDADOPERIODO 
                                                                 +  VISTA.RECAUDOFINACT + VISTA.RECAUDOFINANT) '||MI_AFECTARDEUDAANT||'
                                                        END,
                                       TABLA.MODIFIED_BY    = '''||UN_USUARIO||''',
                                       TABLA.DATE_MODIFIED  = SYSDATE  ';
            ELSE
              MI_MERGEEXISTE := 'UPDATE
                                   SET TABLA.DEUDAANT =  (VISTA.DEUDA + VISTA.VALOR_FACTURADO
                                                       +  VISTA.VALORFINANT + VISTA.VALORFINACT)
                                                       - (VISTA.RECAUDADODEUDA + VISTA.RECAUDADOPERIODO 
                                                       +  VISTA.RECAUDOFINACT + VISTA.RECAUDOFINANT)
                                       TABLA.MODIFIED_BY    = '''||UN_USUARIO||''',
                                       TABLA.DATE_MODIFIED  = SYSDATE  ';
            END IF;
          ELSE
            MI_MERGEEXISTE := 'UPDATE
                                 SET TABLA.DEUDAANT = (VISTA.DEUDA + VISTA.VALOR_FACTURADO)  
                                                    - (VISTA.RECAUDADODEUDA + VISTA.RECAUDADOPERIODO)'||MI_AFECTARDEUDAANT||',
                                     TABLA.MODIFIED_BY    = '''||UN_USUARIO||''',
                                     TABLA.DATE_MODIFIED  = SYSDATE';
          END IF;
          MI_MERGEEXISTE := MI_MERGEEXISTE||
                            ' ,TABLA.SALDOXCOBRAR =  ((VISTA.DEUDA + (VISTA.VALOR_FACTURADO - VISTA.SALDOCREDITO) 
                                                    +  VISTA.VALORFINANT + VISTA.VALORFINACT)
                                                    - (VISTA.RECAUDADODEUDA + VISTA.VALORABONOANT
                                                    +  VISTA.RECAUDOFINANT + VISTA.RECAUDADOPERIODO
                                                    +  VISTA.VALORABONOACT + VISTA.RECAUDOFINACT))
                                                    +  VISTA.SALDOFINDEUDA + VISTA.SALDOFINPERIODO 
                                                    +  VISTA.SALDOFINOTROS';
          MI_MERGEENLACE := '    TABLA.COMPANIA = VISTA.COMPANIA
                             AND TABLA.ANO      = VISTA.ANO
                             AND TABLA.PERIODO  = VISTA.PERIODO
                             AND TABLA.CICLO    = VISTA.CICLO
                             AND TABLA.SERVICIO = VISTA.SERVICIO
                             AND TABLA.USO      = VISTA.USO
                             AND TABLA.ESTRATO  = VISTA.ESTRATO
                             AND TABLA.CONCEPTO = VISTA.CONCEPTO';
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA,
                                                UN_ACCION      => 'MM',
                                                UN_MERGEUSING  => MI_MERGEUSING,
                                                UN_MERGEENLACE => MI_MERGEENLACE,
                                                UN_MERGEEXISTE => MI_MERGEEXISTE);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CONDICION';
          MI_MSGERROR(1).VALOR := 'ANO= '  ||MI_ANIO||
                                  ', PERIODO= '''||MI_PERIODO||
                                  ''', COMPANIA= '''||UN_COMPANIA||
                                  ''' y E0.CICLO= '||UN_CICLO;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTESTAFACTURACION,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END;
      MI_STRSQL := 'SELECT SP_USUARIO.USO
                           ,SP_USUARIO.ESTRATO
                           ,SP_FINANCIABLES.CONCEPTO
                           ,SUM(PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => SALDOFINANCIABLE) - VALOR_FACTURADO) SALDOFINOTROS
                      FROM SP_FINANCIABLES 
                     INNER JOIN SP_USUARIO 
                        ON SP_FINANCIABLES.PERIODO    = SP_USUARIO.PERIODO
                       AND SP_FINANCIABLES.ANO        = SP_USUARIO.ANO
                       AND SP_FINANCIABLES.CICLO      = SP_USUARIO.CICLO
                       AND SP_FINANCIABLES.CODIGORUTA = SP_USUARIO.CODIGORUTA
                       AND SP_FINANCIABLES.COMPANIA   = SP_USUARIO.COMPANIA
                     INNER JOIN SP_CICLO 
                        ON SP_USUARIO.COMPANIA = SP_CICLO.COMPANIA
                       AND SP_USUARIO.CICLO    = SP_CICLO.NUMERO
                     INNER JOIN SP_FACTURADO 
                        ON SP_FINANCIABLES.COMPANIA   = SP_FACTURADO.COMPANIA
                       AND SP_FINANCIABLES.CICLO      = SP_FACTURADO.CICLO
                       AND SP_FINANCIABLES.CODIGORUTA = SP_FACTURADO.CODIGORUTA
                       AND SP_FINANCIABLES.ANO        = SP_FACTURADO.ANO
                       AND SP_FINANCIABLES.PERIODO    = SP_FACTURADO.PERIODO
                       AND SP_FINANCIABLES.CONCEPTO   = SP_FACTURADO.CONCEPTO
                     WHERE SP_USUARIO.COMPANIA = '''||UN_COMPANIA||'''
                       AND SP_USUARIO.CICLO    = '||UN_CICLO||'
                       AND SP_USUARIO.ANO      = '||MI_ANIO||'
                       AND SP_USUARIO.PERIODO  = '''||MI_PERIODO||''''
                         ||MI_CALSUS||'
                      GROUP BY 
                            SP_USUARIO.COMPANIA
                            ,SP_USUARIO.ANO
                            ,SP_USUARIO.PERIODO
                            ,SP_USUARIO.CICLO
                            ,SP_USUARIO.USO
                            ,SP_USUARIO.ESTRATO
                            ,SP_FINANCIABLES.CONCEPTO
                      HAVING SUM(PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => SALDOFINANCIABLE) - VALOR_FACTURADO) > 0';
      <<FINANCIABLESANT>>
      OPEN MI_RSANT FOR MI_STRSQL;
      LOOP
      FETCH MI_RSANT
      INTO MI_USO,MI_ESTRATO,MI_CONCEPTO,MI_SALDOFINOTROS;
      EXIT WHEN MI_RSANT%NOTFOUND;
        IF MI_CONCEPTO <> 12 THEN
          BEGIN
            BEGIN
              MI_TABLA := 'SP_ESTADISTICAS_FACTURACION';
              MI_CAMPOS := ' SP_ESTADISTICAS_FACTURACION.SALDOFINOTROS = '||MI_SALDOFINOTROS||',
                             MODIFIED_BY    = '''||UN_USUARIO||''',
                             DATE_MODIFIED  = SYSDATE   ';

              MI_CONDICION := '    SP_ESTADISTICAS_FACTURACION.COMPANIA = '''||UN_COMPANIA||'''
                               AND SP_ESTADISTICAS_FACTURACION.ANO      = '  ||MI_ANIO||'
                               AND SP_ESTADISTICAS_FACTURACION.PERIODO  = '''||MI_PERIODO||'''
                               AND SP_ESTADISTICAS_FACTURACION.CICLO    = '  ||UN_CICLO||'
                               AND SP_ESTADISTICAS_FACTURACION.USO      = '''||MI_USO||'''
                               AND SP_ESTADISTICAS_FACTURACION.ESTRATO  = '''||MI_ESTRATO||'''
                               AND SP_ESTADISTICAS_FACTURACION.CONCEPTO = '  ||MI_CONCEPTO;
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                    UN_ACCION    =>'M',
                                                    UN_CAMPOS    => MI_CAMPOS,
                                                    UN_CONDICION => MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              MI_MSGERROR(1).CLAVE := 'CONDICION';
              MI_MSGERROR(1).VALOR := 'COMPANIA= '''||UN_COMPANIA||''', ANO= '||MI_ANIO||
                                      ', PERIODO= '''||MI_PERIODO||''', CICLO= '||UN_CICLO||
                                      ', USO= '''||MI_USO||''', ESTRATO= '''||MI_ESTRATO||
                                      ''' y CONCEPTO= '||MI_CONCEPTO;
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE,
                UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTESTAFACTURACION,
                UN_REEMPLAZOS => MI_MSGERROR
              );
          END;
        END IF;
        END LOOP USUARIOSFINAN;  
      CLOSE MI_RSANT;

      MI_STRSQL := 'SELECT SP_USUARIO.USO
                           ,SP_USUARIO.ESTRATO
                           ,SP_FINANCIABLES.CONCEPTO
                           ,SUM(SP_FINANCIABLES.MONTOFINANCIAR) MONTOFINANCIAR
                      FROM SP_FINANCIABLES 
                     INNER JOIN SP_USUARIO 
                        ON SP_FINANCIABLES.PERIODOINICIAL = SP_USUARIO.PERIODO
                       AND SP_FINANCIABLES.ANOINICIAL     = SP_USUARIO.ANO
                       AND SP_FINANCIABLES.CICLO          = SP_USUARIO.CICLO
                       AND SP_FINANCIABLES.CODIGORUTA     = SP_USUARIO.CODIGORUTA
                       AND SP_FINANCIABLES.COMPANIA       = SP_USUARIO.COMPANIA
                     INNER JOIN SP_CICLO 
                        ON SP_USUARIO.COMPANIA = SP_CICLO.COMPANIA
                       AND SP_USUARIO.CICLO    = SP_CICLO.NUMERO
                     WHERE SP_USUARIO.COMPANIA = '''||UN_COMPANIA||'''
                       AND SP_USUARIO.CICLO    = '||UN_CICLO||'
                       AND SP_USUARIO.ANO      = '||MI_ANIO||'
                       AND SP_USUARIO.PERIODO  = '''||MI_PERIODO||''''
                         ||MI_CALSUS||'
                     GROUP BY
                           SP_USUARIO.COMPANIA
                           ,SP_USUARIO.ANO
                           ,SP_USUARIO.PERIODO
                           ,SP_USUARIO.CICLO
                           ,SP_USUARIO.USO
                           ,SP_USUARIO.ESTRATO
                           ,SP_FINANCIABLES.CONCEPTO';
      <<USUARIOSFINAN>>
      OPEN MI_RSANT2 FOR MI_STRSQL;
      LOOP
      FETCH MI_RSANT2
      INTO MI_USO, MI_ESTRATO, MI_CONCEPTO, MI_MONTOFINANCIAR;
      EXIT WHEN MI_RSANT2%NOTFOUND;
        BEGIN
          SELECT DISTINCT 'X' 
            INTO MI_CAMP
            FROM SP_ESTADISTICAS_FACTURACION  
           WHERE SP_ESTADISTICAS_FACTURACION.COMPANIA = UN_COMPANIA
             AND SP_ESTADISTICAS_FACTURACION.ANO      = MI_ANIO
             AND SP_ESTADISTICAS_FACTURACION.PERIODO  = MI_PERIODO
             AND SP_ESTADISTICAS_FACTURACION.CICLO    = UN_CICLO
             AND SP_ESTADISTICAS_FACTURACION.USO      = MI_USO
             AND SP_ESTADISTICAS_FACTURACION.ESTRATO  = MI_ESTRATO
             AND SP_ESTADISTICAS_FACTURACION.CONCEPTO = MI_CONCEPTO;
          EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_CAMP := ' ';
        END;
        IF MI_CAMP = ' ' THEN
          BEGIN
            BEGIN

              MI_TABLA := 'SP_ESTADISTICAS_FACTURACION';
              MI_CAMPOS := 'COMPANIA,ANO,PERIODO,CICLO,SERVICIO,USO,ESTRATO
                           ,CONCEPTO,DEUDAANT,VALOR_FACTURADOANT,DEUDAIN
                           ,VALOR_FACTURADOIN,DEUDA,VALOR_FACTURADO
                           ,RECAUDADODEUDA,RECAUDADOPERIODO,DOBLEPAGO
                           ,SALDOCREDITO,ABONO,MODIFDEUDA,VALORFINACT
                           ,VALORFINANT,VALORABONOACT,VALORABONOANT
                           ,VALORABONODEUDA,RECAUDOFINACT,RECAUDOFINANT
                           ,SALDOFINOTROS,SALDOFINDEUDA,SALDOFINPERIODO
                           ,SALDOXCOBRAR,VALORFINPERIODO,VALORFINDEUDA
                           ,MEDIDORESMALOS,MEDIDORESBUENOS,TOT_FINANCIABLE
                           ,DATE_CREATED,CREATED_BY';

              MI_VALORES := ''''   ||UN_COMPANIA||
                            ''','  ||MI_ANIO||
                            ','''  ||MI_PERIODO||
                            ''','  ||UN_CICLO||
                            ','''  ||MI_SERVICIO_CODIGO||
                            ''','''  ||MI_USO||
                            ''','''||MI_ESTRATO||
                            ''','  ||MI_CONCEPTO||
                            ',0,0,0,0,0,0,0,0,0,0,0,0,
                            0,0,0,0,0,0,0,0,0,0,0,0,0
                            ,0,0,' ||NVL(MI_MONTOFINANCIAR,0)||',
                            SYSDATE,'''||UN_USUARIO||'''';

              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                    UN_ACCION  => 'I',
                                                    UN_CAMPOS  => MI_CAMPOS,
                                                    UN_VALORES => MI_VALORES);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE,
                UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_INSERESTAFACTURA
              );
          END;
        ELSE
          BEGIN
            BEGIN
              MI_TABLA := 'SP_ESTADISTICAS_FACTURACION';
              MI_CAMPOS := ' SP_ESTADISTICAS_FACTURACION.TOT_FINANCIABLE = '||NVL(MI_MONTOFINANCIAR, 0)||',
                             SP_ESTADISTICAS_FACTURACION.MODIFIED_BY    = '''||UN_USUARIO||''',
                             SP_ESTADISTICAS_FACTURACION.DATE_MODIFIED  = SYSDATE';
              MI_CONDICION := '    COMPANIA = '''||UN_COMPANIA||'''
                               AND ANO      = '  ||MI_ANIO||'
                               AND PERIODO  = '''||MI_PERIODO||'''
                               AND CICLO    = '  ||UN_CICLO||'
                               AND USO      = '''||MI_USO||'''
                               AND ESTRATO  = '''||MI_ESTRATO||'''
                               AND CONCEPTO = '  ||MI_CONCEPTO;
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                    UN_ACCION    => 'M',
                                                    UN_CAMPOS    => MI_CAMPOS,
                                                    UN_CONDICION => MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              MI_MSGERROR(1).CLAVE := 'CONDICION';
              MI_MSGERROR(1).VALOR := 'COMPANIA= '''||UN_COMPANIA||''', ANO= '||MI_ANIO||
                                      ', PERIODO= '''||MI_PERIODO||''', CICLO= '||UN_CICLO||
                                      ', USO= '''||MI_USO||''', ESTRATO= '''||MI_ESTRATO||
                                      ''' y CONCEPTO= '||MI_CONCEPTO;
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE,
                UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTESTAFACTURACION,
                UN_REEMPLAZOS => MI_MSGERROR
              );
          END;
        END IF;

          END LOOP SERVICIOS;
      CLOSE MI_RSANT2;

    BEGIN
      BEGIN
        MI_TABLA := 'SP_ESTADISTICAS_FACTURACION';
        MI_CAMPOS := ' SP_ESTADISTICAS_FACTURACION.TOT_FINANCIABLE = 0,
                       SP_ESTADISTICAS_FACTURACION.MODIFIED_BY    = '''||UN_USUARIO||''',
                       SP_ESTADISTICAS_FACTURACION.DATE_MODIFIED  = SYSDATE   ';

        MI_CONDICION := ' TOT_FINANCIABLE IS NULL';
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CONDICION';
        MI_MSGERROR(1).VALOR := 'TOT_FINANCIABLE= NULL';
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTESTAFACTURACION,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
    <<PAGOS>>
    FOR MI_RSANT IN (SELECT SP_D_PAGOSDOBLES.CONCEPTO
                            ,RES_PAGO.USO
                            ,RES_PAGO.ESTRATO
                            ,SUM(SP_D_PAGOSDOBLES.VALOR) SUMADEVALOR
                       FROM (SELECT SP_PAGO.COMPANIA
                                    ,SP_PAGO.FECHA
                                    ,SP_PAGO.BANCO
                                    ,SP_PAGO.CODIGORUTA
                                    ,SP_PAGO.NUMEROPAQUETE
                                    ,SP_USUARIO.USO
                                    ,SP_USUARIO.ESTRATO
                               FROM     SP_PAGO 
                              INNER JOIN SP_USUARIO 
                                 ON SP_PAGO.COMPANIA   = SP_USUARIO.COMPANIA
                                AND SP_PAGO.CICLO      = SP_USUARIO.CICLO
                                AND SP_PAGO.CODIGORUTA = SP_USUARIO.CODIGORUTA
                                AND SP_PAGO.ANO        = SP_USUARIO.ANO
                                AND SP_PAGO.PERIODO    = SP_USUARIO.PERIODO
                              WHERE SP_PAGO.COMPANIA  = UN_COMPANIA
                                AND SP_PAGO.CICLO     = UN_CICLO
                                AND SP_PAGO.ANO       = MI_ANIO
                                AND SP_PAGO.PERIODO   = MI_PERIODO
                                AND SP_PAGO.OPERACION = 'D'
                              GROUP BY
                                    SP_PAGO.COMPANIA
                                    ,SP_PAGO.FECHA
                                    ,SP_PAGO.BANCO
                                    ,SP_PAGO.CODIGORUTA
                                    ,SP_PAGO.NUMEROPAQUETE
                                    ,SP_USUARIO.USO
                                    ,SP_USUARIO.ESTRATO       ) RES_PAGO 

                       INNER JOIN SP_D_PAGOSDOBLES 
                          ON RES_PAGO.COMPANIA      = SP_D_PAGOSDOBLES.COMPANIA
                         AND RES_PAGO.FECHA         = SP_D_PAGOSDOBLES.FECHA
                         AND RES_PAGO.BANCO         = SP_D_PAGOSDOBLES.BANCO
                         AND RES_PAGO.NUMEROPAQUETE = SP_D_PAGOSDOBLES.NUMEROPAQUETE
                         AND RES_PAGO.CODIGORUTA    = SP_D_PAGOSDOBLES.CODIGORUTA
                       GROUP BY
                             SP_D_PAGOSDOBLES.CONCEPTO
                             ,RES_PAGO.USO
                             ,RES_PAGO.ESTRATO)
    LOOP
      BEGIN
        BEGIN
          MI_TABLA := 'SP_ESTADISTICAS_FACTURACION';
          MI_CAMPOS := ' DOBLEPAGO      = '||MI_RSANT.SUMADEVALOR||',
                         MODIFIED_BY    = '''||UN_USUARIO||''',
                         DATE_MODIFIED  = SYSDATE   ';

          MI_CONDICION := '    COMPANIA = '''||UN_COMPANIA||'''
                           AND ANO      = '  ||MI_ANIO||'
                           AND PERIODO  = '''||MI_PERIODO||'''
                           AND CICLO    = '  ||UN_CICLO||'
                           AND USO      = '''||MI_RSANT.USO||'''
                           AND ESTRATO  = '''||MI_RSANT.ESTRATO||'''
                           AND CONCEPTO = '  ||MI_RSANT.CONCEPTO;

          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                UN_ACCION    => 'M',
                                                UN_CAMPOS    => MI_CAMPOS,
                                                UN_CONDICION => MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CONDICION';
          MI_MSGERROR(1).VALOR := 'COMPANIA= '''||UN_COMPANIA||
                                  ''', ANO= '||MI_ANIO||
                                  ', PERIODO= '''||MI_PERIODO||
                                  ''', CICLO= '||UN_CICLO||
                                  ', USO= '''||MI_RSANT.USO||
                                  ''', ESTRATO= '''||MI_RSANT.ESTRATO||
                                  ''' y CONCEPTO= '||MI_RSANT.CONCEPTO;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTESTAFACTURACION,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END; 
    END LOOP PAGOS;
    IF PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                                   UN_NOMBRE    => 'INTERFAZ RESOLUCION 688',
                                                                   UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                                   UN_FECHA_PAR => SYSDATE) = 'SI',
                                      UN_SI        => -1,
                                      UN_NO        => 0) <> 0 THEN
      PCK_SERVICIOS_PUBLICOS_COM6.PR_DISCRIMINARES688(UN_COMPANIA => UN_COMPANIA,
                                                      UN_CICLO    => UN_CICLO,
                                                      UN_PERIODO  => MI_PERIODO,
                                                      UN_ANIO     => MI_ANIO,
                                                      UN_USUARIO  => UN_USUARIO);
    END IF;
  END PR_ESTADISTICAFACTURACION;

  -- 18
  PROCEDURE PR_ESTRECAUDOPERATRASO
    /*
      NAME              : PR_ESTRECAUDOPERATRASO --> EN ACCESS EstRecaudoPerAtraso
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MIGRADOR     : 15/02/2017
      TIME              : 05:15 PM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PROCEDIMIENTO QUE ACTUALIZA LAS ESTADISTICAS DE RECAUDO.
      PARAMETERS        : UN_COMPANIA => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_CICLO    => CICLO SELECCIONADO.
      MODIFICATIONS     : 
      @NAME:  actualizarEstadisticasRecaudo
      @METHOD:  POST
    */ 
  (
    UN_COMPANIA               IN  PCK_SUBTIPOS.TI_COMPANIA
   ,UN_CICLO                  IN  PCK_SUBTIPOS.TI_CICLO 
   ,UN_USUARIO                IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_ANIO                   PCK_SUBTIPOS.TI_ANIO;
    MI_PERIODO                PCK_SUBTIPOS.TI_PERIODO;
    MI_CALSUS                 VARCHAR2(500 CHAR);
    MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_TABLA                  PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
  BEGIN   
    BEGIN
      BEGIN
        SELECT ANO
              ,PERIODO
        INTO   MI_ANIO
              ,MI_PERIODO
        FROM   SP_USUARIO 
        WHERE  COMPANIA = UN_COMPANIA
          AND  CICLO    = UN_CICLO
          AND  ROWNUM   = 1;
        EXCEPTION WHEN NO_DATA_FOUND THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CICLO';
        MI_MSGERROR(1).VALOR := UN_CICLO;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_NOUSUARIOSCICLO,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
    BEGIN
      BEGIN
        MI_TABLA := 'SP_RECAUDOPERATRASO';
        MI_CONDICION := '    COMPANIA = '''||UN_COMPANIA||'''
                         AND ANO      = '  ||MI_ANIO||'
                         AND PERIODO  = '''||MI_PERIODO||'''';
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'E',
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CONDICION';
        MI_MSGERROR(1).VALOR := 'COMPANIA= '''||UN_COMPANIA||
                                ''', ANO= '||MI_ANIO||
                                ' y PERIODO= '''||MI_PERIODO||'''';
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_BORRECAUPERATRASO,
          UN_REEMPLAZOS => MI_MSGERROR
        ); 
    END;
    IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                 UN_NOMBRE    => 'PERMITE CALCULO SUSPENDIDOS',
                                 UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                 UN_FECHA_PAR => SYSDATE),'NO') = 'SI' THEN
      MI_CALSUS := ' AND SP_USUARIO.ESTADO NOT IN (''R'')';
    ELSE
      MI_CALSUS := ' AND (SP_USUARIO.ESTADO         NOT IN (''R'',''S'') 
                      OR (SP_USUARIO.ESTADO         IN (''S'') 
                     AND  SP_USUARIO.FECHACAMBIOEST >= SP_CICLO.FECHA_PREPARACION))';
    END IF;
    BEGIN
      BEGIN
        MI_TABLA := 'SP_RECAUDOPERATRASO';
        MI_CAMPOS := 'COMPANIA,CICLO,ANO,PERIODO,CODIGORUTA
                     ,PERATRASO,USO,ESTRATO,CONCEPTO
                     ,VALOR_FACTURADO,DEUDA,VALORRECAUDADO
                     ,DATE_CREATED,CREATED_BY';

        MI_VALORES := 'SELECT SP_FACTURADO.COMPANIA
                              ,SP_FACTURADO.CICLO
                              ,SP_FACTURADO.ANO
                              ,SP_FACTURADO.PERIODO
                              ,SP_USUARIO.CODIGORUTA
                              ,SP_USUARIO.PERIODOSATRASO
                              ,SP_USUARIO.USO
                              ,SP_USUARIO.ESTRATO
                              ,SP_FACTURADO.CONCEPTO
                              ,SUM(SP_FACTURADO.VALOR_FACTURADO) SUMADEVALOR_FACTURADO
                              ,SUM(SP_FACTURADO.DEUDA) SUMADEDEUDA
                              ,SUM(CASE WHEN NVL(SP_USUARIO.BANCOPERPROCESO,'' '') <> '' ''
                                        THEN SP_FACTURADO.DEUDA
                                           + SP_FACTURADO.VALOR_FACTURADO
                                        ELSE 0
                                   END) EXPR3
                              ,SYSDATE, '''||UN_USUARIO||'''  
                         FROM SP_FACTURADO 
                        INNER JOIN SP_USUARIO 
                           ON SP_FACTURADO.CODIGORUTA = SP_USUARIO.CODIGORUTA
                          AND SP_FACTURADO.CICLO      = SP_USUARIO.CICLO
                          AND SP_FACTURADO.COMPANIA   = SP_USUARIO.COMPANIA
                        INNER JOIN SP_CICLO 
                           ON SP_FACTURADO.COMPANIA   = SP_CICLO.COMPANIA
                          AND SP_FACTURADO.CICLO      = SP_CICLO.NUMERO
                        WHERE SP_FACTURADO.COMPANIA = '''||UN_COMPANIA||'''
                          AND SP_FACTURADO.CICLO    = '  ||UN_CICLO||'
                          AND SP_FACTURADO.ANO      = '  ||MI_ANIO||'
                          AND SP_FACTURADO.PERIODO  = '''||MI_PERIODO||''''||
                         MI_CALSUS||'
                        GROUP BY
                              SP_FACTURADO.COMPANIA
                              ,SP_FACTURADO.CICLO
                              ,SP_FACTURADO.ANO
                              ,SP_FACTURADO.PERIODO
                              ,SP_USUARIO.CODIGORUTA
                              ,SP_USUARIO.PERIODOSATRASO
                              ,SP_USUARIO.USO
                              ,SP_USUARIO.ESTRATO
                              ,SP_FACTURADO.CONCEPTO
                              ,SYSDATE, '''||UN_USUARIO||'''';

        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                              UN_ACCION  => 'IS',
                                              UN_CAMPOS  => MI_CAMPOS,
                                              UN_VALORES => MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_INSRECAUPERATRASO
        ); 
    END;
  END PR_ESTRECAUDOPERATRASO;

  -- 19
  PROCEDURE PR_INSERTARESTADISTICA
    /*
      NAME              : PR_INSERTARESTADISTICA --> EN ACCESS InsertarEstadistica
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MIGRADOR     : 13/02/2017
      TIME              : 02:00 PM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PROCEDIMIENTO QEU REGISTRA ESTADISTICAS DE CONSUMO, TENIENDO EN CUENTA
                          TANTO EL CONSUMO REAL, COMO EL MANUAL.
      PARAMETERS        : UN_COMPANIA           => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_CICLO              => CICLO PARA EL QUE SE VA A REALIZAR LA INSERCION.
                          UN_SERVICIO           => CODIGO DE SP_SERVICIO.      
                          UN_RANGO              => CODIGO DE SP_RANGO.        
                          UN_LIMITEINFERIOR     => LIMITE INFERIOR DE SP_RANGO.  
                          UN_LIMITESUPERIOR     => LIMITE SUPERIOR DE SP_RANGO.  
                          UN_CONDICION          => CONDICION QUE SE VA A AGREGAR A LAS CONSULTAS 
                          UN_CONDICION1         => CONDICION QUE SE VA A AGREGAR A LAS CONSULTAS   
                          UN_CALSUSPENDIDOS     => VALOR (SI O NO) DEPENDIENDO SI SE PERMITE O NO
                                                   EL CALCULO DE SUSPENDIDOS. 
                          UN_CONMANUALES        => VERDADERO (-1) O FALSO (0) DEPENDIENDO SI SE
                                                   MANEJA O NO CONSUMO MANUAL.
                          UN_SITIO              => SITIO DE FACTURACION  
                          UN_PESOASEOESTAD      => PESO DEL ASEO PARA CALCULO DE ESTADISTICAS.  
                          UN_ASEORES720         => VERDADERO (-1) O FALSO (0) DEPENDIENDO DE SI
                                                   MANEJA RES720.
                          UN_PAREXCLUIRPNOCOBRO => VERDADERO (-1) O FALSO (0) DEPENDIENDO SI SE
                                                   EXCLUYE EL NO COBRO.
      MODIFICATIONS     : 
      @NAME:  InsertarEstadistica
      @METHOD:  POST
    */ 
  (
    UN_COMPANIA           IN  PCK_SUBTIPOS.TI_COMPANIA
   ,UN_CICLO              IN  PCK_SUBTIPOS.TI_CICLO
   ,UN_SERVICIO           IN  SP_SERVICIO.CODIGO%TYPE
   ,UN_RANGO              IN  SP_RANGO.CODIGO%TYPE
   ,UN_LIMITEINFERIOR     IN  PCK_SUBTIPOS.TI_DOBLE
   ,UN_LIMITESUPERIOR     IN  PCK_SUBTIPOS.TI_DOBLE
   ,UN_CONDICION          IN  VARCHAR2
   ,UN_CONDICION1         IN  VARCHAR2
   ,UN_CALSUSPENDIDOS     IN  VARCHAR2
   ,UN_CONMANUALES        IN  PCK_SUBTIPOS.TI_LOGICO
   ,UN_SITIO              IN  VARCHAR2
   ,UN_PESOASEOESTAD      IN  PCK_SUBTIPOS.TI_DOBLE
   ,UN_ASEORES720         IN  PCK_SUBTIPOS.TI_LOGICO
   ,UN_PAREXCLUIRPNOCOBRO IN  PCK_SUBTIPOS.TI_LOGICO
   ,UN_USUARIO           IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_COND                   VARCHAR2(500 CHAR);
    MI_COND2                  VARCHAR2(500 CHAR);
    MI_CALSUS                 VARCHAR2(500 CHAR);
    MI_QRYTRAMOEXCEDENTE      PCK_SUBTIPOS.TI_STRSQL;
    MI_ESTFACTURADO           PCK_SUBTIPOS.TI_STRSQL;
    MI_TABLA                  PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
  BEGIN
    IF UN_CALSUSPENDIDOS = 'SI' THEN
      MI_CALSUS := ' AND SP_USUARIO.ESTADO NOT IN (''R'')';
    ELSE
      MI_CALSUS := ' AND (SP_USUARIO.ESTADO NOT IN (''R'',''S'') 
                      OR (SP_USUARIO.ESTADO IN (''S'') 
                     AND  SP_USUARIO.FECHACAMBIOEST >= SP_CICLO.FECHA_PREPARACION))';
    END IF;
    MI_QRYTRAMOEXCEDENTE := 'SELECT SP_CONCEPTOS.COMPANIA
                                   ,SP_FACTURADO.CICLO
                                   ,SP_FACTURADO.CODIGORUTA
                                   ,SP_FACTURADO.ANO
                                   ,SP_FACTURADO.PERIODO
                                   ,SP_FACTURADO.CONCEPTO
                                   ,SP_FACTURADO.VALOR_FACTURADO
                                   ,SP_CONCEPTOS.NOMBRE
                             FROM   SP_CONCEPTOS
                               INNER JOIN SP_FACTURADO
                                  ON      SP_CONCEPTOS.CODIGO   = SP_FACTURADO.CONCEPTO
                                 AND      SP_CONCEPTOS.COMPANIA = SP_FACTURADO.COMPANIA
                             WHERE  SP_CONCEPTOS.COMPANIA           = '''||UN_COMPANIA||'''
                               AND  SP_FACTURADO.VALOR_FACTURADO <> 0
                               AND  SP_CONCEPTOS.NOMBRE             LIKE ''%EXCEDENTE%''';
    BEGIN
      BEGIN
        MI_TABLA := 'SP_ESTADISTICAS_CONSUMO';
        MI_CAMPOS := 'COMPANIA,ANO,PERIODO,CICLO,SERVICIO,USO
                     ,ESTRATO,RANGO,CANTUSUARIOSACUEDUCTO
                     ,CANTUSUARIOSPORPROMEDIO,CANTUSUARIOSPROBLEMA
                     ,CANTUSUARIOACUDESH,CARGOFIJOACUEDUCTO
                     ,VCONSUMOACUEDUCTO,FACTURADOPROMEDIO
                     ,MEDIDORESBUENOS,MEDIDORESMALOS
                     ,CANTUSUARIOSALCANTARILLADO,CANTUSUARIOALCDESH
                     ,CARGOFIJOALCANTARILLADO,VCONSUMOALCANTARILLADO
                     ,CANTUSUARIOSASEO,CANTUSUARIOSASEOBARRIDO
                     ,CANTUSUARIOASEODESH,FACTURADOASEO,VASEOUNICO
                     ,VASEODOMICILIARIO,VASEOBARRIDO,VASEOCONSUMO
                     ,PESOASEO,VTRAMOEXCEDENTE,CANTUSUARIOSAFORO
                     ,CANTUSUARIOSALUMBRADO,VALUMBRADO,CONSUMO
                     ,CONSUMOPROMEDIO,SUBSIDIO,SOBREPRECIO
                     ,CANT_USU_FACT_CERO,CANTACTIVOS,CANTRETIRADOS
                     ,CANTSUSPENDIDOS,CONSUMO_BASICO,CONSUMO_COMPL
                     ,CONSUMO_SUNT,CANT_USU_SIN_FACT,CONSUMO_ACUEDUCTO
                     ,VALOR_BASICO,VALOR_COMPLEMENTARIO,VALOR_SUNTUARIO';
        MI_VALORES := 'SELECT   SP_USUARIO.COMPANIA
                               ,SP_USUARIO.ANO
                               ,SP_USUARIO.PERIODO
                               ,SP_USUARIO.CICLO
                               ,'''||UN_SERVICIO||'''
                               ,SP_USUARIO.USO
                               ,SP_USUARIO.ESTRATO
                               ,'''||UN_RANGO||'''';
        IF UN_SERVICIO = '01' THEN
          MI_COND := ' CASE WHEN SP_MEDIDOR.CODIGO <> '' '' 
                              AND SP_MEDIDOR.ELEMCAMBIO = ''MEDIDOR''
                            THEN -1
                            ELSE 0
                        END';
          MI_COND2 := ' CASE WHEN (SP_USUARIO.INDDESHABITADO <> 0 
                               AND SP_USUARIO.CHAPETAS <> 0) 
                                OR (SP_USUARIO.INDDESHABITADO = 0 
                               AND SP_USUARIO.CHAPETAS <> 0) 
                                OR (SP_USUARIO.INDDESHABITADO <> 0 
                               AND SP_USUARIO.CHAPETAS = 0)
                             THEN -1
                             ELSE 0
                        END';
          MI_VALORES := MI_VALORES ||
                        ',SUM(CASE WHEN SP_USUARIO.ACUEDUCTO <> 0
                                   THEN 1
                                   ELSE 0
                              END)
                         ,SUM(CASE WHEN SP_USUARIO.ACUEDUCTO <> 0 
                                     AND TIPOCALCULO = ''P''
                                   THEN 1
                                   ELSE 0
                              END) 
                         ,SUM(CASE WHEN SP_USUARIO.PROBLEMALECTURA <> 0
                                   THEN 1
                                   ELSE 0
                              END)
                         ,SUM(CASE WHEN SP_USUARIO.ACUEDUCTO <> 0 
                                     AND INDDESHABITADO = -1 
                                     AND (LECTURA - LECTURA1 = 0)
                                   THEN 1
                                   ELSE 0
                              END)
                         ,SUM(CASE WHEN SP_USUARIO.ACUEDUCTO = -1
                                   THEN SP_USUARIO.CARGOFIJO
                                   ELSE 0
                              END)
                         ,SUM(VALCONSUMO)
                         ,SUM(CASE WHEN TIPOCALCULO IN(''D'',''P'')
                                   THEN VALCONSUMOPROM
                                   ELSE 0
                              END)
                         ,SUM(CASE WHEN (LECTURA1 = LECTURA 
                                     AND '||MI_COND2||' <> 0
                                     AND LECTURA <> 0) 
                                      OR (LECTURA1 > LECTURA 
                                     AND '||MI_COND||' <> 0) 
                                      OR (LECTURA > LECTURA1) 
                                      OR (LECTURA = 0 
                                     AND LECTURA1 = 0 
                                     AND MEDIDOR <> NULL 
                                     AND MEDIDOR <> ''0'' 
                                     AND MEDIDOR <> '''')
                                   THEN 1
                                   ELSE 0
                              END)
                         ,SUM(CASE WHEN (LECTURA1 = LECTURA 
                                     AND '||MI_COND2||' = 0 
                                     AND LECTURA <> 0) 
                                      OR (LECTURA1 > LECTURA 
                                     AND '||MI_COND||' = 0)
                                   THEN 1
                                   ELSE 0
                              END)';
        ELSE
          MI_VALORES := MI_VALORES || ',0,0,0,0,0,0,0,0,0';
        END IF;
        IF UN_SERVICIO = '02' THEN
          MI_VALORES := MI_VALORES ||
                        ',SUM(CASE WHEN ALCANTARILLADO <> 0
                                   THEN 1
                                   ELSE 0
                              END)
                         ,SUM(CASE WHEN ALCANTARILLADO <> 0 
                                     AND INDDESHABITADO = -1 
                                     AND (LECTURA - LECTURA1) = 0
                                   THEN 1
                                   ELSE 0
                              END)';
          IF UN_SITIO = 'SI' THEN
            MI_VALORES := MI_VALORES ||
                          ',SUM(CARGOFIJOAL)
                           ,SUM(VALORALCBASICO + VALORALCCOMPLEMENTARIO + VALORALCSUNTUARIO)';
          ELSE
            MI_VALORES := MI_VALORES ||
                          ',SUM(CARGOFIJOAL)
                           ,SUM(VCONSUMOALC)';
          END IF;
        ELSE
           MI_VALORES := MI_VALORES ||
                        ',0,0,0,0';
        END IF;
        IF UN_SERVICIO = '03' THEN
          MI_VALORES := MI_VALORES ||
                        ',SUM(CASE WHEN ASEO <> 0
                                   THEN 1
                                   ELSE 0
                              END)
                         ,SUM(CASE WHEN SP_USUARIO.ASEOBARRIDO <> 0
                                   THEN 1
                                   ELSE 0
                              END)';
          IF UN_SITIO = 'SI' THEN
            MI_VALORES := MI_VALORES ||
                          ',SUM(CASE WHEN ASEO <> 0 
                                       AND INDDESHABITADO = -1
                                     THEN 1
                                     ELSE 0
                            END)';
          ELSE
            MI_VALORES := MI_VALORES ||
                          ',SUM(CASE WHEN EST_FACTURADO.COMPANIA IS NULL
                                     THEN 0
                                     ELSE 1
                                END)';
          END IF;
          IF UN_ASEORES720 <> 0 THEN
            MI_VALORES := MI_VALORES ||
                          ',SUM(VALASEOCCS_720 + VALASEOCBLS_720 
                              + VALASEOCLUS_720 + VALASEOCRT_720 
                              + VALASEOCDF_720 + VALASEOCTL_720 
                              + VALASEOVBA_720)';
          ELSE
            MI_VALORES := MI_VALORES ||
                          ',SUM(VASEOUNICO + VASEODOMICILIARIO 
                              + VASEOBARRIDO + VASEOCONSUMO 
                              + NVL(QRY_TRAMO_EXCEDENTE.VALOR_FACTURADO,0))';
          END IF;
          MI_VALORES := MI_VALORES ||
                        ',SUM(VASEOUNICO)
                         ,SUM(VASEODOMICILIARIO)
                         ,SUM(VASEOBARRIDO)
                         ,SUM(VASEOCONSUMO)
                         ,SUM(CASE WHEN PESOASEO <> '||UN_PESOASEOESTAD||'
                                   THEN PESOASEO
                                   ELSE 0
                              END)
                         ,SUM(NVL(QRY_TRAMO_EXCEDENTE.VALOR_FACTURADO,0))
                         ,SUM(CASE WHEN SP_USUARIO.ASEO = -1 
                                     AND SP_USUARIO.PESOASEO > 0 
                                     AND SP_USUARIO.PESOASEO <> '||UN_PESOASEOESTAD||'
                                   THEN 1
                                   ELSE 0
                              END)';
        ELSE
          MI_VALORES := MI_VALORES ||
                        ',0,0,0,0,0,0,0,0,0,0,0';
        END IF;
        IF UN_SERVICIO = '04' THEN
          MI_VALORES := MI_VALORES ||
                        ',SUM(CASE WHEN ALUMBRADO <> 0
                                   THEN 1
                                   ELSE 0
                              END)
                         ,SUM(FIJOALUMBRADO)';
        ELSE
          MI_VALORES := MI_VALORES ||
                        ',0,0';
        END IF;
        IF UN_SERVICIO <= '02' THEN
          MI_VALORES := MI_VALORES ||
                        ',SUM(CONSUMO)
                         ,SUM(CASE WHEN TIPOCALCULO IN (''D'',''P'')
                                   THEN CONSUMOPROM
                                   ELSE 0
                              END)';
        ELSE
          MI_VALORES := MI_VALORES ||
                        ',0,0';
        END IF;
        IF UN_SERVICIO = '01' THEN
          MI_VALORES := MI_VALORES ||
                        ',SUM(SUBACUEDUCTO)
                         ,SUM(SOBREACUEDUCTO)';
        ELSIF UN_SERVICIO = '02' THEN
          MI_VALORES := MI_VALORES ||
                        ',SUM(SUBALCANTARILLADO)
                         ,SUM(SOBREALCANTARILLADO)';
        ELSIF UN_SERVICIO = '03' THEN
          MI_VALORES := MI_VALORES ||
                        ',SUM(SUBASEO)
                         ,SUM(SOBREASEO)';
        ELSE
          MI_VALORES := MI_VALORES || ',0,0';
        END IF;
        IF UN_SERVICIO = '03' THEN
          MI_VALORES := MI_VALORES ||
                        ',SUM(CASE WHEN SP_USUARIO.ASEO = -1 
                                     AND SP_USUARIO.TOTALASEO = 0
                                   THEN 1
                                   ELSE 0
                              END)';
        ELSIF UN_SERVICIO = '02' THEN
          MI_VALORES := MI_VALORES ||
                        ',SUM(CASE WHEN SP_USUARIO.ALCANTARILLADO = -1 
                                     AND SP_USUARIO.TOTALALCANTARILLADO = 0
                                   THEN 1
                                   ELSE 0
                              END)';
        ELSE
          MI_VALORES := MI_VALORES ||
                        ',SUM(CASE WHEN SP_USUARIO.ACUEDUCTO = -1 
                                     AND SP_USUARIO.TOTALACUEDUCTO = 0
                                   THEN 1
                                   ELSE 0
                              END)';
        END IF;
        IF UN_SERVICIO = '01' THEN
          MI_VALORES := MI_VALORES ||
                        ',SUM(CASE WHEN SP_USUARIO.ESTADO = ''A'' 
                                     AND SP_USUARIO.ACUEDUCTO = -1
                                   THEN 1
                                   ELSE 0
                              END)
                         ,SUM(CASE WHEN SP_USUARIO.ESTADO = ''C'' 
                                     AND SP_USUARIO.ACUEDUCTO = -1
                                   THEN 1
                                   ELSE 0
                              END)
                         ,SUM(CASE WHEN SP_USUARIO.ESTADO = ''S'' 
                                     AND SP_USUARIO.ACUEDUCTO = -1
                                   THEN 1
                                   ELSE 0
                              END)
                         ,SUM(CASE WHEN SP_USUARIO.ACUEDUCTO = -1
                                   THEN SP_USUARIO.CONSUMOBASICO
                                   ELSE 0
                              END)
                         ,SUM(CASE WHEN SP_USUARIO.ACUEDUCTO = -1
                                   THEN SP_USUARIO.CONSUMOCOMPLEMENTARIO
                                   ELSE 0
                              END)
                         ,SUM(CASE WHEN SP_USUARIO.ACUEDUCTO = -1
                                   THEN SP_USUARIO.CONSUMOSUNTUARIO
                                   ELSE 0
                              END)
                         ,SUM(CASE WHEN SP_USUARIO.ACUEDUCTO = -1 
                                     AND SP_USUARIO.CARGOFIJO = 0
                                   THEN 1
                                   ELSE 0
                              END)
                         ,SUM(CASE WHEN SP_USUARIO.ACUEDUCTO = -1
                                   THEN SP_USUARIO.CONSUMO
                                   ELSE 0
                              END)
                         ,SUM(CASE WHEN SP_USUARIO.ACUEDUCTO = -1
                                   THEN SP_USUARIO.VALORBASICO
                                   ELSE 0
                              END)
                         ,SUM(CASE WHEN SP_USUARIO.ACUEDUCTO = -1
                                   THEN SP_USUARIO.VALORCOMPLEMENTARIO
                                   ELSE 0
                              END)
                         ,SUM(CASE WHEN SP_USUARIO.ACUEDUCTO = -1
                                   THEN SP_USUARIO.VALORSUNTUARIO
                                   ELSE 0
                              END)';
        ELSIF UN_SERVICIO = '02' THEN
          MI_VALORES := MI_VALORES ||
                        ',SUM(CASE WHEN SP_USUARIO.ESTADO = ''A'' 
                                     AND SP_USUARIO.ALCANTARILLADO = -1
                                   THEN 1
                                   ELSE 0
                              END)
                         ,SUM(CASE WHEN SP_USUARIO.ESTADO = ''C'' 
                                     AND SP_USUARIO.ALCANTARILLADO = -1
                                   THEN 1
                                   ELSE 0
                              END)
                         ,SUM(CASE WHEN SP_USUARIO.ESTADO = ''S'' 
                                     AND SP_USUARIO.ALCANTARILLADO = -1
                                   THEN 1
                                   ELSE 0
                              END)
                         ,SUM(CASE WHEN SP_USUARIO.ALCANTARILLADO = -1
                                   THEN SP_USUARIO.CONSUMOBASICO
                                   ELSE 0
                              END)
                         ,SUM(CASE WHEN SP_USUARIO.ALCANTARILLADO = -1
                                   THEN SP_USUARIO.CONSUMOCOMPLEMENTARIO
                                   ELSE 0
                              END)
                         ,SUM(CASE WHEN SP_USUARIO.ALCANTARILLADO = -1
                                   THEN SP_USUARIO.CONSUMOSUNTUARIO
                                   ELSE 0
                              END)
                         ,SUM(CASE WHEN SP_USUARIO.ALCANTARILLADO = -1 
                                     AND SP_USUARIO.CARGOFIJO = 0
                                   THEN 1
                                   ELSE 0
                              END),0
                         ,SUM(CASE WHEN SP_USUARIO.ALCANTARILLADO = -1
                                   THEN SP_USUARIO.VALORALCBASICO
                                   ELSE 0
                              END)
                         ,SUM(CASE WHEN SP_USUARIO.ALCANTARILLADO = -1
                                   THEN SP_USUARIO.VALORALCCOMPLEMENTARIO
                                   ELSE 0
                              END)
                         ,SUM(CASE WHEN SP_USUARIO.ALCANTARILLADO = -1
                                   THEN SP_USUARIO.VALORALCSUNTUARIO
                                   ELSE 0
                              END)';
        ELSE
          MI_VALORES := MI_VALORES ||
                        ',SUM(CASE WHEN SP_USUARIO.ESTADO = ''A'' 
                                     AND SP_USUARIO.ASEO = -1
                                   THEN 1
                                   ELSE 0
                              END)
                         ,SUM(CASE WHEN SP_USUARIO.ESTADO = ''C'' 
                                     AND SP_USUARIO.ASEO = -1
                                   THEN 1
                                   ELSE 0
                              END)
                         ,SUM(CASE WHEN SP_USUARIO.ESTADO = ''S'' 
                                     AND SP_USUARIO.ASEO = -1
                                   THEN 1
                                   ELSE 0
                              END)
                         ,0,0,0,0,0,0,0,0';
        END IF;
        -- SE GUARDA TANTO CONSUMO REAL COMO MANUAL DE LOS USUARIOS
        IF UN_CONMANUALES <> 0 AND  UN_SERVICIO <= '02' THEN
          MI_CAMPOS := MI_CAMPOS ||
                       ',CONSUMO_REAL_ACU,CONSUMO_MANUAL_ACU
                        ,CONSUMO_REAL_ALC,CONSUMO_MANUAL_ALC';
          MI_VALORES := MI_VALORES ||
                        ',SUM(CASE WHEN CONSUMOMANUAL <> 0
                                   THEN CONSUMO
                                   ELSE 0
                              END)
                         ,SUM(CASE WHEN CONSUMOMANUAL <> 0
                                   THEN CONSUMOACU
                                   ELSE 0
                              END)
                         ,SUM(CASE WHEN CONSUMOMANUAL <> 0
                                   THEN CONSUMO
                                   ELSE 0
                              END)
                         ,SUM(CASE WHEN CONSUMOMANUAL <> 0
                                   THEN CONSUMOALC
                                   ELSE 0
                              END)';
        END IF;
        MI_CAMPOS := MI_CAMPOS ||
                     ',TAFNA_720,TAFA_720,VASEOCCS_720,VASEOCBLS_720
                      ,VASEOCLUS_720,VASEOCRT_720,VASEOCDF_720
                      ,VASEOCTL_720,VASEOVBA_720
                      ,DATE_CREATED,CREATED_BY'; 

        IF UN_ASEORES720 <> 0 AND UN_SERVICIO = '03' THEN
          MI_VALORES := MI_VALORES ||
                        ',SUM(TAFNA_720)
                         ,SUM(TAFNA_AFORO_720)
                         ,SUM(VALASEOCCS_720)
                         ,SUM(VALASEOCBLS_720)
                         ,SUM(VALASEOCLUS_720)
                         ,SUM(VALASEOCRT_720)
                         ,SUM(VALASEOCDF_720)
                         ,SUM(VALASEOCTL_720)
                         ,SUM(VALASEOVBA_720)';
        ELSE
          MI_VALORES := MI_VALORES ||
                        ',0,0,0,0,0,0,0,0,0';
        END IF;
        IF UN_SITIO <> 'SI' AND UN_SERVICIO = '03' THEN
          MI_ESTFACTURADO := 'SELECT U.COMPANIA
                                     ,U.CICLO
                                     ,U.CODIGORUTA
                                     ,U.ANO
                                     ,U.PERIODO
                                FROM SP_USUARIO U 
                               INNER JOIN SP_FACTURADO F 
                                  ON U.COMPANIA   = F.COMPANIA
                                 AND U.CICLO      = F.CICLO
                                 AND U.CODIGORUTA = F.CODIGORUTA
                                 AND U.ANO        = F.ANO
                                 AND U.PERIODO    = F.PERIODO
                               WHERE U.COMPANIA               = '''||UN_COMPANIA||'''
                                 AND U.CICLO                  = '  ||UN_CICLO||'
                                 AND U.ASEO                   = -1 
                                 AND U.INDDESHABITADO         = -1
                                 AND (U.LECTURA - U.LECTURA1) = 0
                                 AND U.ANO||U.PERIODO         <> U.ANOENTRADANUEVOUSUARIO||U.PERENTRADANUEVOUSUARIO
                               GROUP BY 
                                     U.COMPANIA
                                     ,U.CICLO
                                     ,U.CODIGORUTA
                                     ,U.ANO
                                     ,U.PERIODO';
          MI_VALORES := MI_VALORES ||
                        ',SYSDATE, '''||UN_USUARIO||'''
                        FROM SP_CICLO 
                            INNER JOIN (SP_USUARIO 
                             LEFT JOIN SP_TARIFAS 
                               ON SP_USUARIO.PERIODO     = SP_TARIFAS.PERIODO
                              AND SP_USUARIO.ANO         = SP_TARIFAS.ANO
                              AND SP_USUARIO.USO         = SP_TARIFAS.USO
                              AND SP_USUARIO.ESTRATOASEO = SP_TARIFAS.ESTRATO
                              AND SP_USUARIO.COMPANIA    = SP_TARIFAS.COMPANIA) 
                               ON SP_CICLO.COMPANIA = SP_USUARIO.COMPANIA
                              AND SP_CICLO.NUMERO   = SP_USUARIO.CICLO
                              AND SP_CICLO.ANO      = SP_USUARIO.ANO
                              AND SP_CICLO.PERIODO  = SP_USUARIO.PERIODO
                             LEFT JOIN ('||MI_QRYTRAMOEXCEDENTE||') QRY_TRAMO_EXCEDENTE 
                               ON SP_USUARIO.PERIODO    = QRY_TRAMO_EXCEDENTE.PERIODO
                              AND SP_USUARIO.ANO        = QRY_TRAMO_EXCEDENTE.ANO 
                              AND SP_USUARIO.CODIGORUTA = QRY_TRAMO_EXCEDENTE.CODIGORUTA
                              AND SP_USUARIO.CICLO      = QRY_TRAMO_EXCEDENTE.CICLO
                              AND SP_USUARIO.COMPANIA   = QRY_TRAMO_EXCEDENTE.COMPANIA
                             LEFT JOIN SP_MEDIDOR 
                               ON SP_USUARIO.CODIGORUTA = SP_MEDIDOR.CODIGORUTA
                              AND SP_USUARIO.PERIODO    = SP_MEDIDOR.PERIODO
                              AND SP_USUARIO.ANO        = SP_MEDIDOR.ANO
                              AND SP_USUARIO.CICLO      = SP_MEDIDOR.CICLO
                              AND SP_USUARIO.MEDIDOR    = SP_MEDIDOR.CONSECUTIVO
                              AND SP_USUARIO.COMPANIA   = SP_MEDIDOR.COMPANIA
                             LEFT JOIN ('||MI_ESTFACTURADO||') EST_FACTURADO 
                               ON SP_USUARIO.COMPANIA   = EST_FACTURADO.COMPANIA
                              AND SP_USUARIO.CODIGORUTA = EST_FACTURADO.CODIGORUTA
                              AND SP_USUARIO.CICLO      = EST_FACTURADO.CICLO
                              AND SP_USUARIO.ANO        = EST_FACTURADO.ANO
                              AND SP_USUARIO.PERIODO    = EST_FACTURADO.PERIODO
                            WHERE SP_USUARIO.COMPANIA = '''||UN_COMPANIA||'''
                              AND SP_USUARIO.CICLO    = '  ||UN_CICLO||'
                              AND (CONSUMO             BETWEEN '||UN_LIMITEINFERIOR||' AND '||UN_LIMITESUPERIOR||'
                               OR CONSUMOPROM         BETWEEN '||UN_LIMITEINFERIOR||' AND '||UN_LIMITESUPERIOR||')'||
                            MI_CALSUS||'
                            AND '||UN_CONDICION||
                            PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => NVL(UN_CONDICION1, ' ') = ' ',
                                                  UN_SI        => ' ',
                                                  UN_NO        => 'AND '||UN_CONDICION1)||
                            PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => UN_PAREXCLUIRPNOCOBRO <> 0,
                                                  UN_SI        => 'AND SP_USUARIO.PERIODOSNOCOBROFAC = 0',
                                                  UN_NO        => ' ')||'
                          GROUP BY SP_USUARIO.COMPANIA
                                  ,SP_USUARIO.ANO
                                  ,SP_USUARIO.PERIODO
                                  ,SP_USUARIO.CICLO
                                  ,'||UN_SERVICIO||'
                                  ,SP_USUARIO.USO
                                  ,SP_USUARIO.ESTRATO
                                  ,'||UN_RANGO;
        ELSE
          MI_VALORES :='('|| MI_VALORES ||
                  ',SYSDATE, '''||UN_USUARIO||'''
                        FROM SP_CICLO 
                       INNER JOIN (SP_USUARIO 
                        LEFT JOIN SP_TARIFAS 
                          ON SP_USUARIO.PERIODO     = SP_TARIFAS.PERIODO
                         AND SP_USUARIO.ANO         = SP_TARIFAS.ANO
                         AND SP_USUARIO.USO         = SP_TARIFAS.USO
                         AND SP_USUARIO.ESTRATOASEO = SP_TARIFAS.ESTRATO
                         AND SP_USUARIO.COMPANIA    = SP_TARIFAS.COMPANIA)
                          ON SP_CICLO.COMPANIA = SP_USUARIO.COMPANIA
                         AND SP_CICLO.NUMERO   = SP_USUARIO.CICLO
                         AND SP_CICLO.ANO      = SP_USUARIO.ANO
                         AND SP_CICLO.PERIODO  = SP_USUARIO.PERIODO
                        LEFT JOIN ('||MI_QRYTRAMOEXCEDENTE||') QRY_TRAMO_EXCEDENTE 
                          ON SP_USUARIO.PERIODO    = QRY_TRAMO_EXCEDENTE.PERIODO
                         AND SP_USUARIO.ANO        = QRY_TRAMO_EXCEDENTE.ANO
                         AND SP_USUARIO.CODIGORUTA = QRY_TRAMO_EXCEDENTE.CODIGORUTA
                         AND SP_USUARIO.CICLO      = QRY_TRAMO_EXCEDENTE.CICLO
                         AND SP_USUARIO.COMPANIA   = QRY_TRAMO_EXCEDENTE.COMPANIA
                        LEFT JOIN SP_MEDIDOR 
                          ON SP_USUARIO.CODIGORUTA = SP_MEDIDOR.CODIGORUTA
                         AND SP_USUARIO.PERIODO    = SP_MEDIDOR.PERIODO
                         AND SP_USUARIO.ANO        = SP_MEDIDOR.ANO
                         AND SP_USUARIO.CICLO      = SP_MEDIDOR.CICLO
                         AND SP_USUARIO.COMPANIA   = SP_MEDIDOR.COMPANIA
                         AND SP_USUARIO.MEDIDOR    = SP_MEDIDOR.CONSECUTIVO
                       WHERE SP_USUARIO.COMPANIA = '''||UN_COMPANIA||'''
                         AND SP_USUARIO.CICLO    = '  ||UN_CICLO||'
                         AND (CONSUMO             BETWEEN '||UN_LIMITEINFERIOR||' AND '||UN_LIMITESUPERIOR||'
                          OR CONSUMOPROM         BETWEEN '||UN_LIMITEINFERIOR||' AND '||UN_LIMITESUPERIOR||')'||
                            MI_CALSUS||'
                            AND '||UN_CONDICION||
                            PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => NVL(UN_CONDICION1, ' ') = ' ',
                                                  UN_SI        => ' ',
                                                  UN_NO        => 'AND '||UN_CONDICION1)||
                            PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => UN_PAREXCLUIRPNOCOBRO <> 0,
                                                  UN_SI        => 'AND SP_USUARIO.PERIODOSNOCOBROFAC = 0',
                                                  UN_NO        => ' ')||'
                          GROUP BY SP_USUARIO.COMPANIA
                                  ,SP_USUARIO.ANO
                                  ,SP_USUARIO.PERIODO
                                  ,SP_USUARIO.CICLO
                                  ,'||UN_SERVICIO||'
                                  ,SP_USUARIO.USO
                                  ,SP_USUARIO.ESTRATO
                                  ,'||UN_RANGO||')';
        END IF;
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                              UN_ACCION  => 'IS',
                                              UN_CAMPOS  => MI_CAMPOS,
                                              UN_VALORES => MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_INSERESTACONSUMO
        );
    END;
  END PR_INSERTARESTADISTICA;

  -- 20
  PROCEDURE PR_ESTADISTICACONSUMO
    /*
      NAME              : PR_ESTADISTICACONSUMO --> EN ACCESS EstadisticaConsumo
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MIGRADOR     : 10/02/2017
      TIME              : 04:15 PM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PROCEDIMIENTO QUE PERMITE ACTUALIZAR LAS ESTADISTICAS DE CONSUMO,
                          PARA PODER REALIZAR EL CIERRE DE PERIODO.
      PARAMETERS        : UN_COMPANIA => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_CICLO    => CICLO QUE FUE SELECCIONADO PARA EL CIERRE.
      MODIFICATIONS     : 
      @NAME:  actualizarEstadisticasConsumo
      @METHOD:  POST
    */ 
  (
    UN_COMPANIA         IN  PCK_SUBTIPOS.TI_COMPANIA
   ,UN_CICLO            IN  PCK_SUBTIPOS.TI_CICLO 
   ,UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_SERVICIO1              VARCHAR2(500 CHAR);
    MI_CONDICION1             VARCHAR2(500 CHAR);
    MI_RANGO1                 VARCHAR2(50 CHAR);
    MI_PESOASEO               PCK_SUBTIPOS.TI_DOBLE;
    MI_PESOASEOESTAD          PCK_SUBTIPOS.TI_DOBLE;
    MI_PESOASEOGRANP          VARCHAR2(10 CHAR);
    MI_COND                   VARCHAR2(500 CHAR); 
    MI_PARPESO                PCK_SUBTIPOS.TI_DOBLE;
    MI_STRSUM                 VARCHAR2(500 CHAR);
    MI_CALSUS                 VARCHAR2(500 CHAR);
    MI_CALSUSPENDIDOS         VARCHAR2(50 CHAR);
    MI_CONMANUALES            PCK_SUBTIPOS.TI_LOGICO;
    MI_SITIO                  VARCHAR2(50 CHAR);
    MI_PERUNICO               VARCHAR2(50 CHAR);
    MI_TAFNA720               VARCHAR2(10 CHAR);
    MI_APLICARES720           PCK_SUBTIPOS.TI_LOGICO;
    MI_EXCLUIRNOCOBRO         PCK_SUBTIPOS.TI_LOGICO;
    MI_ANIOESTAD              PCK_SUBTIPOS.TI_ANIO;
    MI_PERIODOESTAD           PCK_SUBTIPOS.TI_PERIODO; 
    MI_REGISTRO               VARCHAR2(2 CHAR);
    MI_QRYPESOASEOEST         PCK_SUBTIPOS.TI_STRSQL;  
    MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_TABLA                  PCK_SUBTIPOS.TI_TABLA;
    MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION; 
    MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
    MI_QRYPESOASEOEST720      PCK_SUBTIPOS.TI_STRSQL;  
  BEGIN

    MI_EXCLUIRNOCOBRO := PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                                                     UN_NOMBRE    => 'EXCLUIR USUARIOS PERIODOS NO COBRO FACTURA',
                                                                                     UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                                                     UN_FECHA_PAR => SYSDATE) =  'SI',
                                               UN_SI        => -1,
                                               UN_NO        => 0);
    MI_PESOASEO := TO_NUMBER(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                   UN_NOMBRE    => 'PESO ASEO PEQUEÑOS PRODUCTORES',
                                                   UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                   UN_FECHA_PAR => SYSDATE));
    MI_TAFNA720 := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                         UN_NOMBRE    => 'RES 720 TAFNA PEQUEÑO PRODUCTOR',
                                         UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                         UN_FECHA_PAR => SYSDATE);
    MI_PESOASEOGRANP := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                              UN_NOMBRE    => 'PESO ASEO GRAN PRODUCTOR',
                                              UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                              UN_FECHA_PAR => SYSDATE);
    MI_PESOASEOESTAD := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                              UN_NOMBRE    => 'PESO ASEO PARA CALCULO DE ESTADISTICAS',
                                              UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                              UN_FECHA_PAR => SYSDATE);
    MI_CALSUSPENDIDOS := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                   UN_NOMBRE    => 'PERMITE CALCULO SUSPENDIDOS',
                                                   UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                   UN_FECHA_PAR => SYSDATE), 'NO');
    MI_CONMANUALES := PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                                                  UN_NOMBRE    => 'MANEJA CONSUMO MANUAL',
                                                                                  UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                                                  UN_FECHA_PAR => SYSDATE) =  'SI',
                                            UN_SI        => -1,
                                            UN_NO        => 0);
    MI_SITIO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                      UN_NOMBRE    => 'FACTURACION EN SITIO',
                                      UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                      UN_FECHA_PAR => SYSDATE);
    IF MI_CALSUSPENDIDOS = 'SI' THEN
      MI_CALSUS := ' AND SP_USUARIO.ESTADO NOT IN (''R'')';
    ELSE
      MI_CALSUS := ' AND (SP_USUARIO.ESTADO NOT IN (''R'',''S'')
                      OR (SP_USUARIO.ESTADO IN (''S'') 
                     AND  SP_USUARIO.FECHACAMBIOEST >= SP_CICLO.FECHA_PREPARACION))';
    END IF;
    BEGIN
      BEGIN
        SELECT ANO
              ,PERIODO
        INTO   MI_ANIOESTAD
              ,MI_PERIODOESTAD
        FROM   SP_USUARIO
        WHERE  COMPANIA = UN_COMPANIA
          AND  CICLO    = UN_CICLO
          AND  ROWNUM   = 1;
        EXCEPTION WHEN NO_DATA_FOUND THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CICLO';
        MI_MSGERROR(1).VALOR := UN_CICLO;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_NOUSUARIOSCICLO, 
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
    IF MI_ANIOESTAD||','||MI_PERIODOESTAD >= PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                                   UN_NOMBRE    => 'PERIODO INICIO RES 720 ASEO',
                                                                   UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                                   UN_FECHA_PAR => SYSDATE)
      AND NVL(UN_CICLO, 0) <> NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                        UN_NOMBRE    => 'RES 720 CICLO EXCLUIDO',
                                                        UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                        UN_FECHA_PAR => SYSDATE), 0) THEN
      MI_APLICARES720 := PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                                                     UN_NOMBRE    => 'APLICA RESOLUCION CRA 720',
                                                                                     UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                                                     UN_FECHA_PAR => SYSDATE) =  'SI',
                                               UN_SI        => -1,
                                               UN_NO        => 0);
    ELSE
      MI_APLICARES720 := 0;
    END IF;
    BEGIN
      SELECT USOSUPERSERVICIOS 
      INTO   MI_REGISTRO
      FROM   SP_TARIFAS
      WHERE  SP_TARIFAS.ANO      = MI_ANIOESTAD
        AND  SP_TARIFAS.PERIODO  = MI_PERIODOESTAD
        AND  SP_TARIFAS.COMPANIA = UN_COMPANIA
        AND  SP_TARIFAS.USOSUPERSERVICIOS IS  NULL;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_REGISTRO := ' ';
    END;
    BEGIN
      IF MI_REGISTRO <> ' ' THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END IF;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'PERIODO';
        MI_MSGERROR(1).VALOR := MI_PERIODOESTAD;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_NOCALCULOESTADISTI, 
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
    BEGIN
      BEGIN
        MI_TABLA     := 'SP_QRY_PESOASEOESTTOT';
        MI_CONDICION := '    SP_QRY_PESOASEOESTTOT.COMPANIA = '''||UN_COMPANIA||'''
                         AND SP_QRY_PESOASEOESTTOT.ANO      = '  ||MI_ANIOESTAD||'
                         AND SP_QRY_PESOASEOESTTOT.PERIODO  = '''||MI_PERIODOESTAD||'''
                         AND SP_QRY_PESOASEOESTTOT.CICLO    = '  ||UN_CICLO;
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                               UN_ACCION    => 'E',
                                               UN_CONDICION => MI_CONDICION); 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CONDICION';
        MI_MSGERROR(1).CLAVE := 'COMPANIA= '''||UN_COMPANIA||
                                ''', ANO= '||MI_ANIOESTAD||
                                ', PERIODO= '''||MI_PERIODOESTAD||
                                ''' y CICLO= '||UN_CICLO;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_BORRPESOASEOESTTOT,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
    MI_STRSUM := '       (VALOR_FACTURADO + SP_FACTURADO.DEUDA 
                        + VALORFINACT + VALORFINANT 
                        - VALORABONOANT - VALORABONOACT 
                        - SP_FACTURADO.CREDITOABONADO)
                    ELSE 0
                  END';
    MI_PARPESO := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                      UN_NOMBRE    => 'PESO ASEO PARA CALCULO DE ESTADISTICAS',
                                                      UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                      UN_FECHA_PAR => SYSDATE),0));
    MI_QRYPESOASEOEST := 'SELECT   SP_USUARIO.COMPANIA
                                  ,SP_USUARIO.CICLO
                                  ,SP_USUARIO.CODIGORUTA
                                  ,SP_USUARIO.ANO
                                  ,SP_USUARIO.PERIODO
                                  ,SP_USUARIO.USO
                                  ,SP_USUARIO.ESTRATO
                                  ,CASE WHEN SP_USUARIO.USO IN (''1'',''2'') 
                                          AND SP_USUARIO.FRECUENCIAASEOSEMANA <> 1
                                        THEN 0
                                        ELSE (CASE WHEN SP_USUARIO.PESOASEO = '||MI_PARPESO||'
                                                   THEN 0
                                                   ELSE SP_USUARIO.PESOASEO
                                              END)
                                   END PESOASEO
                                  ,SUM(CASE WHEN CONCEPTO = 21
                                            THEN '||MI_STRSUM||') CONCEPTO21
                                  ,SUM(CASE WHEN CONCEPTO = 20
                                            THEN '||MI_STRSUM||') CONCEPTO20
                                  ,SUM(CASE WHEN CONCEPTO = 22
                                            THEN '||MI_STRSUM||') CONCEPTO22
                                  ,SUM(CASE WHEN CONCEPTO = 3 
                                            THEN '||MI_STRSUM||') CONCEPTO3 
                                  ,SUM(CASE WHEN CONCEPTO = 37
                                            THEN '||MI_STRSUM||') CONCEPTO37
                                  ,SUM(CASE WHEN CONCEPTO = 38
                                            THEN '||MI_STRSUM||') CONCEPTO38
                                  ,SUM(CASE WHEN CONCEPTO = 48
                                            THEN '||MI_STRSUM||') CONCEPTO48
                                  ,SUM(CASE WHEN INSTR(SP_CONCEPTOS.NOMBRE,UPPER(''EXCEDENTE''),1) > 0
                                            THEN '||MI_STRSUM||') CONCEPTO46
                                  ,SP_USUARIO.TOTALASEO
                          FROM     SP_FACTURADO 
                            INNER JOIN SP_CONCEPTOS 
                               ON      SP_FACTURADO.COMPANIA = SP_CONCEPTOS.COMPANIA
                              AND      SP_FACTURADO.CONCEPTO = SP_CONCEPTOS.CODIGO
                            INNER JOIN SP_USUARIO 
                               ON      SP_FACTURADO.COMPANIA   = SP_USUARIO.COMPANIA 
                              AND      SP_FACTURADO.CICLO      = SP_USUARIO.CICLO
                              AND      SP_FACTURADO.CODIGORUTA = SP_USUARIO.CODIGORUTA
                              AND      SP_FACTURADO.ANO        = SP_USUARIO.ANO
                              AND      SP_FACTURADO.PERIODO    = SP_USUARIO.PERIODO
                          WHERE    SP_FACTURADO.COMPANIA = '''||UN_COMPANIA||'''
                            AND    SP_FACTURADO.CICLO    = '  ||UN_CICLO||'
                            AND    SP_FACTURADO.ANO      = '  ||MI_ANIOESTAD||'
                            AND    SP_FACTURADO.PERIODO  = '''||MI_PERIODOESTAD||'''
                            AND    SP_FACTURADO.CONCEPTO IN (3,20,21,22,23,48,46,37,38)
                          GROUP BY SP_USUARIO.COMPANIA
                                  ,SP_USUARIO.CICLO
                                  ,SP_USUARIO.CODIGORUTA
                                  ,SP_USUARIO.ANO
                                  ,SP_USUARIO.PERIODO
                                  ,SP_USUARIO.USO
                                  ,SP_USUARIO.ESTRATO
                                  ,CASE WHEN SP_USUARIO.USO IN (''1'',''2'') 
                                          AND SP_USUARIO.FRECUENCIAASEOSEMANA <> 1
                                        THEN 0
                                        ELSE (CASE WHEN SP_USUARIO.PESOASEO = '||MI_PARPESO||'
                                                   THEN 0
                                                   ELSE SP_USUARIO.PESOASEO
                                              END)
                                   END
                                  ,SP_USUARIO.TOTALASEO';
   MI_QRYPESOASEOEST720 := 'SELECT   SP_USUARIO.COMPANIA
                                    ,SP_USUARIO.CICLO
                                    ,SP_USUARIO.CODIGORUTA
                                    ,SP_USUARIO.ANO
                                    ,SP_USUARIO.PERIODO
                                    ,SP_USUARIO.USO
                                    ,SP_USUARIO.ESTRATO
                                    ,CASE WHEN SP_USUARIO.USO IN (''1'',''2'') 
                                            AND SP_USUARIO.FRECUENCIAASEOSEMANA <> 1
                                          THEN 0
                                          ELSE (CASE WHEN SP_USUARIO.PESOASEO = '||MI_PARPESO||'
                                                     THEN 0
                                                     ELSE SP_USUARIO.PESOASEO
                                                END)
                                     END PESOASEO
                                    ,SP_USUARIO.TAFNA_720
                                    ,SUM(CASE WHEN CONCEPTO = 201
                                            THEN '||MI_STRSUM||') CONCEPTO201
                                    ,SUM(CASE WHEN CONCEPTO = 202
                                            THEN '||MI_STRSUM||') CONCEPTO202
                                    ,SUM(CASE WHEN CONCEPTO = 203
                                            THEN '||MI_STRSUM||') CONCEPTO203
                                    ,SUM(CASE WHEN CONCEPTO = 204
                                            THEN '||MI_STRSUM||') CONCEPTO204
                                    ,SUM(CASE WHEN CONCEPTO = 205
                                            THEN '||MI_STRSUM||') CONCEPTO205
                                    ,SUM(CASE WHEN CONCEPTO = 206
                                            THEN '||MI_STRSUM||') CONCEPTO206
                                    ,SUM(CASE WHEN CONCEPTO = 207
                                            THEN '||MI_STRSUM||') CONCEPTO207
                                    ,SP_USUARIO.TOTALASEO
                            FROM     SP_USUARIO 
                              INNER JOIN SP_FACTURADO 
                                 ON      SP_USUARIO.COMPANIA   = SP_FACTURADO.COMPANIA
                                AND      SP_USUARIO.CICLO      = SP_FACTURADO.CICLO
                                AND      SP_USUARIO.CODIGORUTA = SP_FACTURADO.CODIGORUTA
                                AND      SP_USUARIO.ANO        = SP_FACTURADO.ANO
                                AND      SP_USUARIO.PERIODO    = SP_FACTURADO.PERIODO
                            WHERE    SP_FACTURADO.COMPANIA = '''||UN_COMPANIA||'''
                              AND    SP_FACTURADO.CICLO    = '  ||UN_CICLO||'
                              AND    SP_FACTURADO.ANO      = '  ||MI_ANIOESTAD||'
                              AND    SP_FACTURADO.PERIODO  = '''||MI_PERIODOESTAD||'''
                              AND    SP_FACTURADO.CONCEPTO IN (201,202,203,204,205,206,207)
                              AND    SP_USUARIO.TAFNA_720  <> 0
                            GROUP BY SP_USUARIO.COMPANIA
                                    ,SP_USUARIO.CICLO
                                    ,SP_USUARIO.CODIGORUTA
                                    ,SP_USUARIO.ANO
                                    ,SP_USUARIO.PERIODO
                                    ,SP_USUARIO.USO
                                    ,SP_USUARIO.ESTRATO
                                    ,CASE WHEN SP_USUARIO.USO IN (''1'',''2'') 
                                            AND SP_USUARIO.FRECUENCIAASEOSEMANA <> 1
                                          THEN 0
                                          ELSE (CASE WHEN SP_USUARIO.PESOASEO = '||MI_PARPESO||'
                                                     THEN 0
                                                     ELSE SP_USUARIO.PESOASEO
                                                END)
                                     END
                                    ,SP_USUARIO.TOTALASEO
                                    ,SP_USUARIO.TAFNA_720';
    MI_PERUNICO := MI_ANIOESTAD || MI_PERIODOESTAD;
    IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                             UN_NOMBRE    => 'MANEJA MULTIUSUARIOS',
                             UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                             UN_FECHA_PAR => SYSDATE) = 'SI' THEN
      IF MI_APLICARES720 <> 0 THEN
        MI_COND := 'CASE WHEN NVL(SP_MULTIUSUARIOS.TAFNA_720,QRY_PESOASEOEST720.TAFNA_720) <= '||MI_TAFNA720||'
                         THEN ''PEQUEÑO PRODUCTOR''
                         ELSE ''GRAN PRODUCTOR''
                    END';
      ELSIF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                  UN_NOMBRE    => 'MANEJA RANGOS PRODUCTOR ASEO',
                                  UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                  UN_FECHA_PAR => SYSDATE) = 'SI' THEN
        MI_COND := 'CASE WHEN NVL(SP_MULTIUSUARIOS.PESOASEO, SP_USUARIO.PESOASEO) <'||MI_PESOASEO||'
                         THEN ''PEQUEÑO PRODUCTOR''
                         ELSE (CASE WHEN NVL(SP_MULTIUSUARIOS.PESOASEO,SP_USUARIO.PESOASEO) >= '||MI_PESOASEO||'
                                      AND NVL(SP_MULTIUSUARIOS.PESOASEO,SP_USUARIO.PESOASEO) < '||MI_PESOASEOGRANP||'
                                    THEN ''GRAN PRODUCTOR MENOS''
                                    ELSE ''GRAN PRODUCTOR MAS''
                               END)
                    END';
      ELSE
        MI_COND := 'CASE WHEN NVL(SP_MULTIUSUARIOS.PESOASEO,SP_USUARIO.PESOASEO) <= '||MI_PESOASEO||'
                         THEN ''PEQUEÑO PRODUCTOR''
                         ELSE ''GRAN PRODUCTOR''
                    END';
      END IF;
      IF MI_APLICARES720 <> 0 THEN
        BEGIN
          BEGIN
            MI_TABLA := 'SP_QRY_PESOASEOESTTOT';
            MI_CAMPOS := 'COMPANIA,NUMUSUARIOS,PERIODOUNICO,TIPOPRODUCTOR
                         ,SUMADEPESOASEO,ANO,PERIODO,USO,ESTRATO
                         ,SUMADECONCEPTO21,SUMADECONCEPTO20
                         ,SUMADECONCEPTO22,SUMADECONCEPTO3
                         ,SUMADECONCEPTO48,SUMADECONCEPTO46
                         ,SUMADECONCEPTO37,SUMADECONCEPTO38
                         ,SUMADECONCEPTO201,SUMADECONCEPTO202
                         ,SUMADECONCEPTO203,SUMADECONCEPTO204
                         ,SUMADECONCEPTO205,SUMADECONCEPTO206
                         ,SUMADECONCEPTO207,SUMADETAFNA
                         ,SUMADESUMADETOTFACTURAASEO,NOMBREUSO,CICLO                         
                        ,DATE_CREATED,CREATED_BY';
            MI_VALORES := 'SELECT   SP_USUARIO.COMPANIA
                                   ,SUM(NVL(CANTIDAD,1))
                                   ,'''||MI_PERUNICO||'''
                                   ,'||MI_COND||'
                                   ,SUM(QRY_PESOASEOEST720.PESOASEO)
                                   ,SP_USUARIO.ANO
                                   ,SP_USUARIO.PERIODO
                                   ,SP_USUARIO.USO
                                   ,SP_USUARIO.ESTRATO
                                   ,0,0,0,0,0,0,0,0
                                   ,SUM(QRY_PESOASEOEST720.CONCEPTO201)
                                   ,SUM(QRY_PESOASEOEST720.CONCEPTO202)
                                   ,SUM(QRY_PESOASEOEST720.CONCEPTO203)
                                   ,SUM(QRY_PESOASEOEST720.CONCEPTO204)
                                   ,SUM(QRY_PESOASEOEST720.CONCEPTO205)
                                   ,SUM(QRY_PESOASEOEST720.CONCEPTO206)
                                   ,SUM(QRY_PESOASEOEST720.CONCEPTO207)
                                   ,SUM(QRY_PESOASEOEST720.TAFNA_720)
                                   ,SUM(QRY_PESOASEOEST720.TOTALASEO)
                                   ,SP_USOS.NOMBRE
                                   ,SP_USUARIO.CICLO
                                   ,SYSDATE
                                   ,'''||UN_USUARIO||'''
                              FROM SP_USUARIO 
                             INNER JOIN ('||MI_QRYPESOASEOEST720||') QRY_PESOASEOEST720 
                                ON SP_USUARIO.ESTRATO    = QRY_PESOASEOEST720.ESTRATO
                               AND SP_USUARIO.USO        = QRY_PESOASEOEST720.USO
                               AND SP_USUARIO.PERIODO    = QRY_PESOASEOEST720.PERIODO
                               AND SP_USUARIO.ANO        = QRY_PESOASEOEST720.ANO
                               AND SP_USUARIO.CODIGORUTA = QRY_PESOASEOEST720.CODIGORUTA
                               AND SP_USUARIO.COMPANIA   = QRY_PESOASEOEST720.COMPANIA
                               AND SP_USUARIO.CICLO      = QRY_PESOASEOEST720.CICLO
                             INNER JOIN SP_USOS 
                                ON SP_USUARIO.USO      = SP_USOS.CODIGO
                               AND SP_USUARIO.COMPANIA = SP_USOS.COMPANIA
                             INNER JOIN SP_CICLO 
                                ON SP_USUARIO.COMPANIA = SP_CICLO.COMPANIA
                               AND SP_USUARIO.CICLO    = SP_CICLO.NUMERO
                              LEFT JOIN SP_MULTIUSUARIOS 
                                ON SP_USUARIO.USO        = SP_MULTIUSUARIOS.USO
                               AND SP_USUARIO.ESTRATO    = SP_MULTIUSUARIOS.ESTRATO
                               AND SP_USUARIO.CODIGORUTA = SP_MULTIUSUARIOS.CODIGORUTA
                               AND SP_USUARIO.CICLO      = SP_MULTIUSUARIOS.CICLO
                               AND SP_USUARIO.COMPANIA   = SP_MULTIUSUARIOS.COMPANIA
                             WHERE SP_USUARIO.COMPANIA = '''||UN_COMPANIA||'''
                               AND SP_USUARIO.ANO      = '  ||MI_ANIOESTAD||'
                               AND SP_USUARIO.PERIODO  = '''||MI_PERIODOESTAD||'''
                               AND SP_USUARIO.CICLO    = '  ||UN_CICLO||
                             MI_CALSUS||'
                             AND    (CONCEPTO201 + CONCEPTO202 
                                   + CONCEPTO203 + CONCEPTO204
                                   + CONCEPTO205 + CONCEPTO206 
                                   + CONCEPTO207) <> 0
                             AND NVL(SP_MULTIUSUARIOS.TAFNA_720,QRY_PESOASEOEST720.TAFNA_720) <> 0
                           GROUP BY SP_USUARIO.COMPANIA
                                   ,SP_USUARIO.CICLO
                                   ,SP_USUARIO.ANO
                                   ,SP_USUARIO.PERIODO
                                   ,'||MI_COND||'
                                   ,SP_USUARIO.USO
                                   ,SP_USUARIO.ESTRATO
                                   ,SP_USOS.NOMBRE';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                  UN_ACCION  => 'IS',
                                                  UN_CAMPOS  => MI_CAMPOS,
                                                  UN_VALORES => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_INSPESOASEOESTTOT 
            );
        END;
      ELSE
        BEGIN
          BEGIN
            MI_TABLA := 'SP_QRY_PESOASEOESTTOT';
            MI_CAMPOS := 'COMPANIA,NUMUSUARIOS,PERIODOUNICO
                         ,TIPOPRODUCTOR,SUMADEPESOASEO,ANO
                         ,PERIODO,USO,ESTRATO,SUMADECONCEPTO21
                         ,SUMADECONCEPTO20,SUMADECONCEPTO22
                         ,SUMADECONCEPTO3,SUMADECONCEPTO48
                         ,SUMADECONCEPTO46,SUMADECONCEPTO37
                         ,SUMADECONCEPTO38,SUMADESUMADETOTFACTURAASEO
                         ,NOMBREUSO,CICLO,DATE_CREATED,CREATED_BY ';

            MI_VALORES := 'SELECT SP_USUARIO.COMPANIA
                                  ,SUM(NVL(CANTIDAD,1))
                                  ,'''||MI_PERUNICO||'''
                                  ,'||MI_COND||'
                                  ,SUM(QRY_PESOASEOEST.PESOASEO)
                                  ,SP_USUARIO.ANO
                                  SP_USUARIO.PERIODO
                                  ,SP_USUARIO.USO
                                  ,SP_USUARIO.ESTRATO
                                  ,SUM(QRY_PESOASEOEST.CONCEPTO21)
                                  ,SUM(QRY_PESOASEOEST.CONCEPTO20)
                                  ,SUM(QRY_PESOASEOEST.CONCEPTO22)
                                  ,SUM(QRY_PESOASEOEST.CONCEPTO3)
                                  ,SUM(QRY_PESOASEOEST.CONCEPTO48)
                                  ,SUM(QRY_PESOASEOEST.CONCEPTO46)
                                  ,SUM(QRY_PESOASEOEST.CONCEPTO37)
                                  ,SUM(QRY_PESOASEOEST.CONCEPTO38)
                                  ,SUM(QRY_PESOASEOEST.TOTALASEO)
                                  ,SP_USOS.NOMBRE
                                  ,SP_USUARIO.CICLO
                                  ,SYSDATE
                                  ,'''||UN_USUARIO||'''
                             FROM SP_USUARIO 
                            INNER JOIN ('||MI_QRYPESOASEOEST||') QRY_PESOASEOEST 
                               ON SP_USUARIO.PESOASEO   = QRY_PESOASEOEST.PESOASEO
                              AND SP_USUARIO.ESTRATO    = QRY_PESOASEOEST.ESTRATO
                              AND SP_USUARIO.USO        = QRY_PESOASEOEST.USO
                              AND SP_USUARIO.PERIODO    = QRY_PESOASEOEST.PERIODO
                              AND SP_USUARIO.ANO        = QRY_PESOASEOEST.ANO
                              AND SP_USUARIO.CODIGORUTA = QRY_PESOASEOEST.CODIGORUTA
                              AND SP_USUARIO.CICLO      = QRY_PESOASEOEST.CICLO
                              AND SP_USUARIO.COMPANIA   = QRY_PESOASEOEST.COMPANIA
                            INNER JOIN SP_USOS 
                               ON SP_USUARIO.USO      = SP_USOS.CODIGO
                              AND P_USUARIO.COMPANIA = SP_USOS.COMPANIA
                            INNER JOIN SP_CICLO 
                               ON SP_USUARIO.COMPANIA = SP_CICLO.COMPANIA
                              AND SP_USUARIO.CICLO    = SP_CICLO.NUMERO
                             LEFT JOIN SP_MULTIUSUARIOS 
                               ON SP_USUARIO.USO        = SP_MULTIUSUARIOS.USO
                              AND SP_USUARIO.ESTRATO    = SP_MULTIUSUARIOS.ESTRATO
                              AND SP_USUARIO.CODIGORUTA = SP_MULTIUSUARIOS.CODIGORUTA
                              AND SP_USUARIO.CICLO      = SP_MULTIUSUARIOS.CICLO
                              AND SP_USUARIO.COMPANIA   = SP_MULTIUSUARIOS.COMPANIA
                            WHERE SP_USUARIO.COMPANIA = '''||UN_COMPANIA||'''
                              AND SP_USUARIO.ANO      = '  ||MI_ANIOESTAD||'
                              AND SP_USUARIO.PERIODO  = '''||MI_PERIODOESTAD||'''
                              AND SP_USUARIO.CICLO    = '  ||UN_CICLO||
                              MI_CALSUS||'
                              AND    (CONCEPTO20 + CONCEPTO21
                                   + CONCEPTO22 + CONCEPTO3 
                                   + CONCEPTO48) <> 0
                              AND    NVL(SP_MULTIUSUARIOS.PESOASEO,SP_USUARIO.PESOASEO) <> 0
                            GROUP BY SP_USUARIO.COMPANIA
                                   ,SP_USUARIO.CICLO
                                   ,SP_USUARIO.ANO
                                   ,SP_USUARIO.PERIODO
                                   ,'||MI_COND||'
                                   ,SP_USUARIO.USO
                                   ,SP_USUARIO.ESTRATO
                                   ,SP_USOS.NOMBRE';

            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                  UN_ACCION  => 'IS',
                                                  UN_CAMPOS  => MI_CAMPOS,
                                                  UN_VALORES => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_INSPESOASEOESTTOT
            );
        END;
      END IF;
    ELSE
      IF MI_APLICARES720 <> 0 THEN
        MI_COND := 'CASE WHEN QRY_PESOASEOEST720.TAFNA_720 <= '||MI_TAFNA720||'
                         THEN ''PEQUEÑO PRODUCTOR''
                         ELSE ''GRAN PRODUCTOR''
                    END';
      ELSIF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                  UN_NOMBRE    => 'MANEJA RANGOS PRODUCTOR ASEO',
                                  UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                  UN_FECHA_PAR => SYSDATE) = 'SI' THEN
        MI_COND := 'CASE WHEN SP_USUARIO.PESOASEO < '||MI_PESOASEO||'
                         THEN ''PEQUEÑO PRODUCTOR''
                         ELSE (CASE WHEN SP_USUARIO.PESOASEO >= '||MI_PESOASEO||'
                                      AND SP_USUARIO.PESOASEO < '||MI_PESOASEOGRANP||'
                                    THEN ''GRAN PRODUCTOR MENOS''
                                    ELSE ''GRAN PRODUCTOR MAS''
                               END)
                    END';
      ELSE
        MI_COND := 'CASE WHEN SP_USUARIO.PESOASEO <= '||MI_PESOASEO||'
                         THEN ''PEQUEÑO PRODUCTOR''
                         ELSE ''GRAN PRODUCTOR''
                    END';
      END IF;
      IF MI_APLICARES720 <> 0 THEN
        BEGIN
          BEGIN
            MI_TABLA := 'SP_QRY_PESOASEOESTTOT';
            MI_CAMPOS := 'COMPANIA,NUMUSUARIOS,PERIODOUNICO,TIPOPRODUCTOR
                         ,SUMADEPESOASEO,ANO,PERIODO,USO,ESTRATO
                         ,SUMADECONCEPTO21,SUMADECONCEPTO20
                         ,SUMADECONCEPTO22,SUMADECONCEPTO3
                         ,SUMADECONCEPTO48,SUMADECONCEPTO46
                         ,SUMADECONCEPTO37,SUMADECONCEPTO38
                         ,SUMADECONCEPTO201,SUMADECONCEPTO202
                         ,SUMADECONCEPTO203,SUMADECONCEPTO204
                         ,SUMADECONCEPTO205,SUMADECONCEPTO206
                         ,SUMADECONCEPTO207,SUMADETAFNA
                         ,SUMADESUMADETOTFACTURAASEO,NOMBREUSO,CICLO
                         ,DATE_CREATED,CREATED_BY';
            MI_VALORES := 'SELECT   SP_USUARIO.COMPANIA
                                   ,COUNT(SP_USUARIO.PESOASEO)
                                   ,'''||MI_PERUNICO||'''
                                   ,'||MI_COND||'
                                   ,SUM(QRY_PESOASEOEST720.PESOASEO)
                                   ,SP_USUARIO.ANO
                                   ,SP_USUARIO.PERIODO
                                   ,SP_USUARIO.USO
                                   ,SP_USUARIO.ESTRATO
                                   ,0,0,0,0,0,0,0,0
                                   ,SUM(QRY_PESOASEOEST720.CONCEPTO201)
                                   ,SUM(QRY_PESOASEOEST720.CONCEPTO202)
                                   ,SUM(QRY_PESOASEOEST720.CONCEPTO203)
                                   ,SUM(QRY_PESOASEOEST720.CONCEPTO204)
                                   ,SUM(QRY_PESOASEOEST720.CONCEPTO205)
                                   ,SUM(QRY_PESOASEOEST720.CONCEPTO206)
                                   ,SUM(QRY_PESOASEOEST720.CONCEPTO207)
                                   ,SUM(QRY_PESOASEOEST720.TAFNA_720)
                                   ,SUM(QRY_PESOASEOEST720.TOTALASEO)
                                   ,SP_USOS.NOMBRE,SP_USUARIO.CICLO
                                   ,SYSDATE,'''||UN_USUARIO||'''
                              FROM SP_USUARIO 
                             INNER JOIN ('||MI_QRYPESOASEOEST720||') QRY_PESOASEOEST720 
                                ON SP_USUARIO.COMPANIA   = QRY_PESOASEOEST720.COMPANIA
                               AND SP_USUARIO.CICLO      = QRY_PESOASEOEST720.CICLO
                               AND SP_USUARIO.CODIGORUTA = QRY_PESOASEOEST720.CODIGORUTA
                               AND SP_USUARIO.ANO        = QRY_PESOASEOEST720.ANO
                               AND SP_USUARIO.PERIODO    = QRY_PESOASEOEST720.PERIODO
                               AND SP_USUARIO.USO        = QRY_PESOASEOEST720.USO
                               AND SP_USUARIO.ESTRATO    = QRY_PESOASEOEST720.ESTRATO
                             INNER JOIN SP_USOS 
                                ON SP_USUARIO.COMPANIA = SP_USOS.COMPANIA
                               AND SP_USUARIO.USO      = SP_USOS.CODIGO
                             INNER JOIN SP_CICLO 
                                ON SP_USUARIO.COMPANIA = SP_CICLO.COMPANIA
                               AND SP_USUARIO.CICLO    = SP_CICLO.NUMERO
                             WHERE SP_USUARIO.COMPANIA = '''||UN_COMPANIA||'''
                               AND SP_USUARIO.CICLO    = '  ||UN_CICLO||'
                               AND SP_USUARIO.ANO      = '  ||MI_ANIOESTAD||'
                               AND SP_USUARIO.PERIODO  = '''||MI_PERIODOESTAD||''''||
                             MI_CALSUS||'
                               AND (CONCEPTO201 + CONCEPTO202 
                                   + CONCEPTO203 + CONCEPTO204
                                   + CONCEPTO205 + CONCEPTO206
                                   + CONCEPTO207) <> 0
                           GROUP BY SP_USUARIO.COMPANIA
                                   ,SP_USUARIO.ANO
                                   ,SP_USUARIO.PERIODO
                                   ,'||MI_COND||'
                                   ,SP_USUARIO.USO
                                   ,SP_USUARIO.ESTRATO
                                   ,SP_USOS.NOMBRE
                                   ,SP_USUARIO.CICLO';

            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                  UN_ACCION  => 'IS',
                                                  UN_CAMPOS  => MI_CAMPOS,
                                                  UN_VALORES => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_INSPESOASEOESTTOT 
            );
        END;
      ELSE
        BEGIN
          BEGIN
            MI_TABLA := 'SP_QRY_PESOASEOESTTOT';
            MI_CAMPOS := 'COMPANIA,NUMUSUARIOS,PERIODOUNICO
                         ,TIPOPRODUCTOR,SUMADEPESOASEO,ANO
                         ,PERIODO,USO,ESTRATO,SUMADECONCEPTO21
                         ,SUMADECONCEPTO20,SUMADECONCEPTO22
                         ,SUMADECONCEPTO3,SUMADECONCEPTO48
                         ,SUMADECONCEPTO46,SUMADECONCEPTO37
                         ,SUMADECONCEPTO38,SUMADESUMADETOTFACTURAASEO
                         ,NOMBREUSO,CICLO ,DATE_CREATED,CREATED_BY';
            MI_VALORES := 'SELECT SP_USUARIO.COMPANIA
                                  ,COUNT(SP_USUARIO.PESOASEO)
                                  ,'''||MI_PERUNICO||'''
                                  ,'||MI_COND||'
                                  ,SUM(QRY_PESOASEOEST.PESOASEO)
                                  ,SP_USUARIO.ANO
                                  ,SP_USUARIO.PERIODO
                                  ,SP_USUARIO.USO
                                  ,SP_USUARIO.ESTRATO
                                  ,SUM(QRY_PESOASEOEST.CONCEPTO21)
                                  ,SUM(QRY_PESOASEOEST.CONCEPTO20)
                                  ,SUM(QRY_PESOASEOEST.CONCEPTO22)
                                  ,SUM(QRY_PESOASEOEST.CONCEPTO3)
                                  ,SUM(QRY_PESOASEOEST.CONCEPTO48)
                                  ,SUM(QRY_PESOASEOEST.CONCEPTO46)
                                  ,SUM(QRY_PESOASEOEST.CONCEPTO37)
                                  ,SUM(QRY_PESOASEOEST.CONCEPTO38)
                                  ,SUM(QRY_PESOASEOEST.TOTALASEO)
                                  ,SP_USOS.NOMBRE,SP_USUARIO.CICLO
                                  ,SYSDATE,'''||UN_USUARIO||'''
                             FROM SP_USUARIO 
                            INNER JOIN ('||MI_QRYPESOASEOEST||') QRY_PESOASEOEST 
                               ON SP_USUARIO.COMPANIA   = QRY_PESOASEOEST.COMPANIA
                              AND SP_USUARIO.CICLO      = QRY_PESOASEOEST.CICLO
                              AND SP_USUARIO.CODIGORUTA = QRY_PESOASEOEST.CODIGORUTA
                              AND SP_USUARIO.ANO        = QRY_PESOASEOEST.ANO
                              AND SP_USUARIO.PERIODO    = QRY_PESOASEOEST.PERIODO
                              AND SP_USUARIO.USO        = QRY_PESOASEOEST.USO
                              AND SP_USUARIO.ESTRATO    = QRY_PESOASEOEST.ESTRATO
                              AND SP_USUARIO.PESOASEO   = QRY_PESOASEOEST.PESOASEO
                            INNER JOIN SP_USOS 
                               ON SP_USUARIO.COMPANIA = SP_USOS.COMPANIA
                              AND SP_USUARIO.USO      = SP_USOS.CODIGO
                            INNER JOIN SP_CICLO 
                               ON SP_USUARIO.COMPANIA = SP_CICLO.COMPANIA
                              AND SP_USUARIO.CICLO    = SP_CICLO.NUMERO
                            WHERE SP_USUARIO.COMPANIA = '''||UN_COMPANIA||'''
                              AND SP_USUARIO.CICLO    = '  ||UN_CICLO||'
                              AND SP_USUARIO.ANO      = '  ||MI_ANIOESTAD||'
                              AND SP_USUARIO.PERIODO  = '''||MI_PERIODOESTAD||''''||
                              MI_CALSUS||'
                              AND    (CONCEPTO20 + CONCEPTO21 
                                    + CONCEPTO22 + CONCEPTO3 
                                    + CONCEPTO48) <> 0
                           GROUP BY SP_USUARIO.COMPANIA
                                   ,SP_USUARIO.ANO
                                   ,SP_USUARIO.PERIODO
                                   ,'||MI_COND||'
                                   ,SP_USUARIO.USO
                                   ,SP_USUARIO.ESTRATO
                                   ,SP_USOS.NOMBRE
                                   ,SP_USUARIO.CICLO';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                  UN_ACCION  => 'IS',
                                                  UN_CAMPOS  => MI_CAMPOS,
                                                  UN_VALORES => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_INSPESOASEOESTTOT
            );
        END;
      END IF;
    END IF;
    BEGIN
      BEGIN
        MI_TABLA := 'SP_ESTADISTICAS_CONSUMO';
        MI_CONDICION := '       COMPANIA = '''||UN_COMPANIA||
                        ''' AND CICLO    = '  ||UN_CICLO||
                          ' AND ANO      = '  ||MI_ANIOESTAD||
                          ' AND PERIODO  = '''||MI_PERIODOESTAD||'''';
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'E',
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CONDICION';
        MI_MSGERROR(1).VALOR := 'COMPANIA= '''||UN_COMPANIA||
                                ''',CICLO= '  ||UN_CICLO||
                                ',ANO= '  ||MI_ANIOESTAD||
                                ' y PERIODO= '''||MI_PERIODOESTAD||'''';
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_BORRESTACONSUMO,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
    <<SERVICIOS>>
    FOR MI_RSSERVICIO IN (SELECT   CODIGO
                          FROM     SP_SERVICIO
                          WHERE    COMPANIA = UN_COMPANIA
                          ORDER BY CODIGO)
    LOOP
      CASE 
        WHEN MI_RSSERVICIO.CODIGO = '01'
        THEN MI_SERVICIO1 := '(ACUEDUCTO <> 0 OR SP_USUARIO.CARGOFIJO <> 0)';
        WHEN MI_RSSERVICIO.CODIGO = '02'
        THEN MI_SERVICIO1 := '(ALCANTARILLADO <> 0 OR SP_USUARIO.CARGOFIJOAL <> 0)';
        WHEN MI_RSSERVICIO.CODIGO = '03'
        THEN 
          IF MI_APLICARES720 <> 0 THEN
            MI_SERVICIO1 := '(ASEO <> 0 OR (VALASEOCCS_720 + VALASEOCBLS_720 
                                          + VALASEOCLUS_720 + VALASEOCRT_720 
                                          + VALASEOCDF_720 + VALASEOCTL_720 
                                          + VALASEOVBA_720) <> 0)';
          ELSE
            MI_SERVICIO1 := '(ASEO <> 0 OR (SP_USUARIO.VASEOUNICO + SP_USUARIO.VASEODOMICILIARIO 
                                          + SP_USUARIO.VASEOBARRIDO + SP_USUARIO.VASEOCONSUMO) <> 0)';
          END IF;
        WHEN MI_RSSERVICIO.CODIGO = '04'
        THEN MI_SERVICIO1 := '(ALUMBRADO <> 0 OR SP_USUARIO.FIJOALUMBRADO <> 0)';
        WHEN MI_RSSERVICIO.CODIGO > '04'
        THEN MI_SERVICIO1 := ' ';
      END CASE;
      IF MI_SERVICIO1 <> ' ' THEN
        IF MI_RSSERVICIO.CODIGO <= '02' THEN 
          <<LIMITESRANGO>>
          FOR MI_RSRANGO IN (SELECT CODIGO
                                   ,LIMITEINFERIOR
                                   ,LIMITESUPERIOR
                             FROM   SP_RANGO
                             WHERE  COMPANIA = UN_COMPANIA
                             ORDER BY CODIGO)
          LOOP
            MI_CONDICION1 := '     CONSUMO >= '||MI_RSRANGO.LIMITEINFERIOR||
                             ' AND CONSUMO <= '||MI_RSRANGO.LIMITESUPERIOR;

            PCK_SERVICIOS_PUBLICOS_COM6.PR_INSERTARESTADISTICA(UN_COMPANIA           => UN_COMPANIA, 
                                                               UN_CICLO              => UN_CICLO,
                                                               UN_SERVICIO           => MI_RSSERVICIO.CODIGO,
                                                               UN_RANGO              => MI_RSRANGO.CODIGO,
                                                               UN_LIMITEINFERIOR     =>MI_RSRANGO.LIMITEINFERIOR,
                                                               UN_LIMITESUPERIOR     =>MI_RSRANGO.LIMITESUPERIOR,
                                                               UN_CONDICION          => MI_SERVICIO1,
                                                               UN_CONDICION1         => MI_CONDICION1,
                                                               UN_CALSUSPENDIDOS     => MI_CALSUSPENDIDOS,
                                                               UN_CONMANUALES        => MI_CONMANUALES,
                                                               UN_SITIO              => MI_SITIO,
                                                               UN_PESOASEOESTAD      => MI_PESOASEOESTAD,
                                                               UN_ASEORES720         => MI_APLICARES720,
                                                               UN_PAREXCLUIRPNOCOBRO => MI_EXCLUIRNOCOBRO,
                                                               UN_USUARIO            => UN_USUARIO);
          END LOOP LIMITESRANGO;
        ELSIF MI_RSSERVICIO.CODIGO = '03' THEN
          MI_RANGO1 := '01';
          MI_CONDICION1 := 'USOSUPERSERVICIOS = ''1''';
          PCK_SERVICIOS_PUBLICOS_COM6.PR_INSERTARESTADISTICA(UN_COMPANIA           => UN_COMPANIA, 
                                                             UN_CICLO              => UN_CICLO,
                                                             UN_SERVICIO           => MI_RSSERVICIO.CODIGO,
                                                             UN_RANGO              => MI_RANGO1,
                                                             UN_LIMITEINFERIOR     => 0,
                                                             UN_LIMITESUPERIOR     => 99999999999,
                                                             UN_CONDICION          => MI_SERVICIO1,
                                                             UN_CONDICION1         => MI_CONDICION1,
                                                             UN_CALSUSPENDIDOS     => MI_CALSUSPENDIDOS,
                                                             UN_CONMANUALES        => MI_CONMANUALES,
                                                             UN_SITIO              => MI_SITIO,
                                                             UN_PESOASEOESTAD      => MI_PESOASEOESTAD,
                                                             UN_ASEORES720         => MI_APLICARES720,
                                                             UN_PAREXCLUIRPNOCOBRO => MI_EXCLUIRNOCOBRO,
                                                             UN_USUARIO            => UN_USUARIO);
          IF MI_APLICARES720 <> 0 THEN
            MI_RANGO1 := '02';
            MI_CONDICION1 := ' USOSUPERSERVICIOS <> ''1'' AND NVL(SP_USUARIO.TAFNA_720,0) < 1.5';
            PCK_SERVICIOS_PUBLICOS_COM6.PR_INSERTARESTADISTICA(UN_COMPANIA           => UN_COMPANIA, 
                                                               UN_CICLO              => UN_CICLO,
                                                               UN_SERVICIO           => MI_RSSERVICIO.CODIGO,
                                                               UN_RANGO              => MI_RANGO1,
                                                               UN_LIMITEINFERIOR     => 0,
                                                               UN_LIMITESUPERIOR     => 99999999999,
                                                               UN_CONDICION          => MI_SERVICIO1,
                                                               UN_CONDICION1         => MI_CONDICION1,
                                                               UN_CALSUSPENDIDOS     => MI_CALSUSPENDIDOS,
                                                               UN_CONMANUALES        => MI_CONMANUALES,
                                                               UN_SITIO              => MI_SITIO,
                                                               UN_PESOASEOESTAD      => MI_PESOASEOESTAD,
                                                               UN_ASEORES720         => MI_APLICARES720,
                                                               UN_PAREXCLUIRPNOCOBRO => MI_EXCLUIRNOCOBRO,
                                                               UN_USUARIO            => UN_USUARIO);
            MI_RANGO1 := '03';
            MI_CONDICION1 := ' USOSUPERSERVICIOS <> ''1'' AND NVL(SP_USUARIO.TAFNA_720,0) >= 1.5';
            PCK_SERVICIOS_PUBLICOS_COM6.PR_INSERTARESTADISTICA(UN_COMPANIA           => UN_COMPANIA, 
                                                               UN_CICLO              => UN_CICLO,
                                                               UN_SERVICIO           => MI_RSSERVICIO.CODIGO,
                                                               UN_RANGO              => MI_RANGO1,
                                                               UN_LIMITEINFERIOR     => 0,
                                                               UN_LIMITESUPERIOR     => 99999999999,
                                                               UN_CONDICION          => MI_SERVICIO1,
                                                               UN_CONDICION1         => MI_CONDICION1,
                                                               UN_CALSUSPENDIDOS     => MI_CALSUSPENDIDOS,
                                                               UN_CONMANUALES        => MI_CONMANUALES,
                                                               UN_SITIO              => MI_SITIO,
                                                               UN_PESOASEOESTAD      => MI_PESOASEOESTAD,
                                                               UN_ASEORES720         => MI_APLICARES720,
                                                               UN_PAREXCLUIRPNOCOBRO => MI_EXCLUIRNOCOBRO,
                                                               UN_USUARIO            => UN_USUARIO);
          ELSE
            MI_RANGO1 := '02';
            MI_CONDICION1 := ' USOSUPERSERVICIOS <> ''1'' AND NVL(PESOASEO,0) < 1';
            PCK_SERVICIOS_PUBLICOS_COM6.PR_INSERTARESTADISTICA(UN_COMPANIA           => UN_COMPANIA, 
                                                               UN_CICLO              => UN_CICLO,
                                                               UN_SERVICIO           => MI_RSSERVICIO.CODIGO,
                                                               UN_RANGO              => MI_RANGO1,
                                                               UN_LIMITEINFERIOR     => 0,
                                                               UN_LIMITESUPERIOR     => 99999999999,
                                                               UN_CONDICION          => MI_SERVICIO1,
                                                               UN_CONDICION1         => MI_CONDICION1,
                                                               UN_CALSUSPENDIDOS     => MI_CALSUSPENDIDOS,
                                                               UN_CONMANUALES        => MI_CONMANUALES,
                                                               UN_SITIO              => MI_SITIO,
                                                               UN_PESOASEOESTAD      => MI_PESOASEOESTAD,
                                                               UN_ASEORES720         => MI_APLICARES720,
                                                               UN_PAREXCLUIRPNOCOBRO => MI_EXCLUIRNOCOBRO,
                                                               UN_USUARIO            => UN_USUARIO);
            MI_RANGO1 := '03';
            MI_CONDICION1 := ' USOSUPERSERVICIOS <> ''1'' AND NVL(PESOASEO,0) >= 1';
            PCK_SERVICIOS_PUBLICOS_COM6.PR_INSERTARESTADISTICA(UN_COMPANIA           => UN_COMPANIA, 
                                                               UN_CICLO              => UN_CICLO,
                                                               UN_SERVICIO           => MI_RSSERVICIO.CODIGO,
                                                               UN_RANGO              => MI_RANGO1,
                                                               UN_LIMITEINFERIOR     => 0,
                                                               UN_LIMITESUPERIOR     => 99999999999,
                                                               UN_CONDICION          => MI_SERVICIO1,
                                                               UN_CONDICION1         => MI_CONDICION1,
                                                               UN_CALSUSPENDIDOS     => MI_CALSUSPENDIDOS,
                                                               UN_CONMANUALES        => MI_CONMANUALES,
                                                               UN_SITIO              => MI_SITIO,
                                                               UN_PESOASEOESTAD      => MI_PESOASEOESTAD,
                                                               UN_ASEORES720         => MI_APLICARES720,
                                                               UN_PAREXCLUIRPNOCOBRO => MI_EXCLUIRNOCOBRO,
                                                               UN_USUARIO            => UN_USUARIO);
          END IF;
        ELSE
          BEGIN
            SELECT CODIGO
            INTO   MI_RANGO1
            FROM   SP_RANGO
            WHERE  COMPANIA = UN_COMPANIA
              AND  ROWNUM = 1;
            EXCEPTION WHEN NO_DATA_FOUND THEN
              MI_RANGO1 := '1';
          END;
          PCK_SERVICIOS_PUBLICOS_COM6.PR_INSERTARESTADISTICA(UN_COMPANIA           => UN_COMPANIA, 
                                                             UN_CICLO              => UN_CICLO,
                                                             UN_SERVICIO           => MI_RSSERVICIO.CODIGO,
                                                             UN_RANGO              => MI_RANGO1,
                                                             UN_LIMITEINFERIOR     => 0,
                                                             UN_LIMITESUPERIOR     => 99999999999,
                                                             UN_CONDICION          => MI_SERVICIO1,
                                                             UN_CONDICION1         => ' ',
                                                             UN_CALSUSPENDIDOS     => MI_CALSUSPENDIDOS,
                                                             UN_CONMANUALES        => MI_CONMANUALES,
                                                             UN_SITIO              => MI_SITIO,
                                                             UN_PESOASEOESTAD      => MI_PESOASEOESTAD,
                                                             UN_ASEORES720         => MI_APLICARES720,
                                                             UN_PAREXCLUIRPNOCOBRO => MI_EXCLUIRNOCOBRO,
                                                             UN_USUARIO            => UN_USUARIO);
        END IF; 
      END IF;
    END LOOP SERVICIOS;
    -- SE AJUSTA PARA EL ALCANTARILLADO
    PCK_SERVICIOS_PUBLICOS_COM6.PR_ESTADISTICASTARIFARIAS(UN_COMPANIA => UN_COMPANIA,
                                                          UN_CICLO    => UN_CICLO,
                                                          UN_ANIO     => MI_ANIOESTAD,
                                                          UN_PERIODO  => MI_PERIODOESTAD,
                                                          UN_USUARIO  => UN_USUARIO);
    -- SE ASEGURA QUE TODOS LOS USOS Y ESTRATOS QUE ESTAN EN LA TABLA SP_ESTADISTICAS_FACTURACION 
    -- ESTEN EN LA TABLA SP_ESTADISTICAS_CONSUMO
    IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                             UN_NOMBRE    => 'INFORMES ESPECIALES ESTADISTICAS',
                             UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                             UN_FECHA_PAR => SYSDATE) = 'SI' THEN
      PCK_SERVICIOS_PUBLICOS_COM6.PR_CUADREESTRATOS(UN_COMPANIA => UN_COMPANIA,
                                                    UN_CICLO    => UN_CICLO,
                                                    UN_ANIO     => MI_ANIOESTAD,
                                                    UN_PERIODO  => MI_PERIODOESTAD,
                                                    UN_USUARIO  => UN_USUARIO);
    END IF;
    -- SE ACTUALIZA EL CONSUMO DEFINITIVO SEGUN LOS CONSUMOS MANUALES
    IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                             UN_NOMBRE    => 'MANEJA CONSUMO MANUAL',
                             UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                             UN_FECHA_PAR => SYSDATE) = 'SI' THEN
      BEGIN
        BEGIN
          MI_TABLA := 'SP_ESTADISTICAS_CONSUMO';
          MI_CAMPOS := ' CONSUMO        = CONSUMO - (CASE WHEN SERVICIO = ''01''
                                                     THEN CONSUMO_REAL_ACU - CONSUMO_MANUAL_ACU
                                                     ELSE (CASE WHEN SERVICIO = ''02''
                                                           THEN CONSUMO_REAL_ALC - CONSUMO_MANUAL_ALC
                                                           ELSE 0
                                                          END)
                                                      END),
                         MODIFIED_BY    = '''||UN_USUARIO||''',
                         DATE_MODIFIED  = SYSDATE  ';
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA  => MI_TABLA,
                                                UN_ACCION => 'M',
                                                UN_CAMPOS => MI_CAMPOS);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CONDICION';
          MI_MSGERROR(1).VALOR := 'El campo CONSUMO';
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACTUALIESTACONSUMO,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END;
    END IF;
  END PR_ESTADISTICACONSUMO;

  -- 21
  PROCEDURE PR_CERRARDESCUENTOS
    /*
      NAME              : PR_CERRARDESCUENTOS --> EN ACCESS CerrarDescuentos
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MIGRADOR     : 03/02/2017
      TIME              : 12:05 AM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PROCEDIMIENTO QUE PERMITE REALIZAR EL CIERRE PARA DESCUENTOS 
                          CONTROLADOS POR CONCEPTO.
      PARAMETERS        : UN_COMPANIA      => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_CICLO         => CICLO EN EL QUE SE QUIERE REALIZAR EL CIERRE.
                          UN_ANIO          => AÑO EN EL QUE VA A REALIZAR EL CIERRE.
                          UN_PERIODO       => PERIODO DEL AÑO EN EL QUE SE VA A REALIZAR EL CIERRE.  
                          UN_NIT           => NIT QUE SE VA A EVALUAR.
                          UN_SOBREESCRIBIR => VARIABLE QUE DEFINE SI EL USUARIO DECIDIO SOBREESCRIBIR LA INFORMACION
                                              EN CASO DE QUE YA EXISTA.
      MODIFICATIONS     : 
      @NAME:  cerrarDescuentos
      @METHOD:  POST
    */ 
  (
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_CICLO           IN PCK_SUBTIPOS.TI_CICLO
   ,UN_ANIO            IN PCK_SUBTIPOS.TI_ANIO
   ,UN_PERIODO         IN PCK_SUBTIPOS.TI_PERIODO
   ,UN_NIT             IN VARCHAR2 -- VARCHAR2 debido a que no existe un tipo de dato con las mismas características en el paquete PCK_SUBTIPOS.
   ,UN_SOBREESCRIBIR   IN VARCHAR2 
   ,UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_ANIOSIG            PCK_SUBTIPOS.TI_ANIO;
    MI_PERIODOSIG         PCK_SUBTIPOS.TI_PERIODO;
  BEGIN
    MI_ANIOSIG := PCK_SERVICIOS_PUBLICOS.FC_ANO_PERIODO_SIGUIENTE(UN_COMPANIA     => UN_COMPANIA,
                                                                  UN_ANO          => UN_ANIO,
                                                                  UN_PERIODO      => UN_PERIODO,
                                                                  UN_TIPO_RETORNO => '0');
    MI_PERIODOSIG := PCK_SERVICIOS_PUBLICOS.FC_ANO_PERIODO_SIGUIENTE(UN_COMPANIA  => UN_COMPANIA,
                                                                  UN_ANO          => UN_ANIO,
                                                                  UN_PERIODO      => UN_PERIODO,
                                                                  UN_TIPO_RETORNO => '1');
    IF UN_SOBREESCRIBIR = 'SI' THEN
      BEGIN
        BEGIN
          MI_TABLA := 'SP_USUARIO_DESCU';
          MI_CONDICION := '    COMPANIA             = '''||UN_COMPANIA||'''
                           AND CICLO                = '  ||UN_CICLO||'
                           AND ANOFAC               = '  ||MI_ANIOSIG||'
                           AND PERIODOFAC           = '''||MI_PERIODOSIG||'''
                           AND NVL(PORPORCENTAJE,0) = 0';
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                UN_ACCION    => 'E',
                                                UN_CONDICION => MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CONDICION';
          MI_MSGERROR(1).VALOR := 'COMPANIA= '''||UN_COMPANIA||
                                  ''',CICLO= '||UN_CICLO||
                                  ', ANOFAC= '||MI_ANIOSIG||
                                  ', PERIODOFAC= '''||MI_PERIODOSIG||
                                  ''' y PORPORCENTAJE= null';
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_BORRUSUARIODESCU,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END;
    END IF;
    IF PCK_SERVICIOS_PUBLICOS_COM6.FC_AUTORIZACION_DESCTOTFACTURA(UN_COMPANIA => UN_COMPANIA,
                                                                  UN_NIT      => UN_NIT) = 0 THEN
      BEGIN
        BEGIN
          MI_TABLA := 'SP_USUARIO_DESCU'; 
          MI_CAMPOS := ' COMPANIA
                        ,CICLO
                        ,CODIGORUTA
                        ,ANO_INI
                        ,PERIODO_INI
                        ,ANOFAC
                        ,PERIODOFAC
                        ,CONCEPTO
                        ,VALOR_INI
                        ,VALOR_FAC
                        ,DATE_CREATED
                        ,CREATED_BY';
          MI_VALORES := 'SELECT SP_USUARIO_DESCU.COMPANIA
                               ,SP_USUARIO_DESCU.CICLO
                               ,SP_USUARIO_DESCU.CODIGORUTA
                               ,SP_USUARIO_DESCU.ANO_INI
                               ,SP_USUARIO_DESCU.PERIODO_INI
                               ,'||MI_ANIOSIG||'
                               ,'''||MI_PERIODOSIG||'''
                               ,SP_USUARIO_DESCU.CONCEPTO
                               ,SP_USUARIO_DESCU.VALOR_INI - SP_USUARIO_DESCU.VALOR_FAC
                               ,0
                               ,SYSDATE
                               ,'''||UN_USUARIO||'''
                         FROM   SP_USUARIO_DESCU
                         WHERE  COMPANIA                                                = '''||UN_COMPANIA||'''
                           AND  CICLO                                                   =   '||UN_CICLO||'
                           AND  ANOFAC                                                  =   '||UN_ANIO||'
                           AND  PERIODOFAC                                              = '''||UN_PERIODO||'''
                           AND  SP_USUARIO_DESCU.VALOR_INI - SP_USUARIO_DESCU.VALOR_FAC > 0
                           AND  NVL(SP_USUARIO_DESCU.PORPORCENTAJE,0)                   = 0';
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA, 
                                                UN_ACCION  => 'IS', 
                                                UN_CAMPOS  => MI_CAMPOS, 
                                                UN_VALORES => MI_VALORES);  
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_USUARIODESCU
          );
      END;
    END IF;
  END PR_CERRARDESCUENTOS;

  -- 22
  PROCEDURE PR_CERRARPRODUCTIVIDAD
    /*
      NAME              : PR_CERRARPRODUCTIVIDAD --> EN ACCESS CerrarProductividad
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MIGRADOR     : 23/02/2017
      TIME              : 03:55 PM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PROCEDIMIENTO QUE PERMITE REALIZAR EL CIERRE DEPRODUCTIVIDAD, 
                          ACTUALIZANDO CAMPOS DE ASEO
      PARAMETERS        : UN_COMPANIA      => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_CICLO         => CICLO EN EL QUE SE QUIERE REALIZAR EL CIERRE.
                          UN_ANIO          => AÑO EN EL QUE VA A REALIZAR EL CIERRE.
                          UN_PERIODO       => PERIODO DEL AÑO EN EL QUE SE VA A REALIZAR EL CIERRE.  
                          UN_SOBREESCRIBIR => VARIABLE QUE DEFINE SI EL USUARIO DECIDIO SOBREESCRIBIR LA INFORMACION
                                              EN CASO DE QUE YA EXISTA.
      MODIFICATIONS     : 
      @NAME:  cerrarProductividad
      @METHOD:  POST
    */ 
  (
    UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_CICLO         IN PCK_SUBTIPOS.TI_CICLO
   ,UN_ANIO          IN PCK_SUBTIPOS.TI_ANIO
   ,UN_PERIODO       IN PCK_SUBTIPOS.TI_PERIODO
   ,UN_SOBREESCRIBIR IN VARCHAR2
   ,UN_USUARIO       IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_ANIOSIG                      PCK_SUBTIPOS.TI_ANIO;
    MI_PERIODOSIG                   PCK_SUBTIPOS.TI_PERIODO;
    MI_TABLA                        PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS                       PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                      PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION                    PCK_SUBTIPOS.TI_CONDICION;
    MI_MSGERROR                     PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_REGISTRO                     VARCHAR2(1 CHAR);
  BEGIN
    MI_ANIOSIG := PCK_SERVICIOS_PUBLICOS.FC_ANO_PERIODO_SIGUIENTE(UN_COMPANIA     => UN_COMPANIA,
                                                                  UN_ANO          => UN_ANIO,
                                                                  UN_PERIODO      => UN_PERIODO,
                                                                  UN_TIPO_RETORNO => '0');

    MI_PERIODOSIG := PCK_SERVICIOS_PUBLICOS.FC_ANO_PERIODO_SIGUIENTE(UN_COMPANIA  => UN_COMPANIA,
                                                                  UN_ANO          => UN_ANIO,
                                                                  UN_PERIODO      => UN_PERIODO,
                                                                  UN_TIPO_RETORNO => '1');
    IF UN_SOBREESCRIBIR = 'SI' THEN
      BEGIN
        BEGIN
          MI_TABLA := 'SP_USUARIO_PRODUC';
          MI_CONDICION := '     COMPANIA   = '''||UN_COMPANIA||'''
                           AND  CICLO      = '  ||UN_CICLO||'
                           AND  ANOFAC     = '  ||MI_ANIOSIG||'  
                           AND  PERIODOFAC = '''||MI_PERIODOSIG||'''';
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                UN_ACCION    => 'E',
                                                UN_CONDICION => MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CONDICION';
          MI_MSGERROR(1).VALOR := 'COMPANIA= '''||UN_COMPANIA||
                                  ''',CICLO= '||UN_CICLO||
                                  ', ANOFAC= '||MI_ANIOSIG||
                                  ', PERIODOFAC= '''||MI_PERIODOSIG||'''';
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_BORRUSUARIOPRODUC,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END;
    END IF;
    <<TARIFAS>>
    FOR MI_RS IN (SELECT USO
                        ,ESTRATO
                  FROM   SP_TARIFAS
                  WHERE  COMPANIA = UN_COMPANIA
                    AND  ANO      = UN_ANIO
                    AND  PERIODO  = UN_PERIODO)
    LOOP
      BEGIN
        BEGIN
          MI_TABLA := 'SP_USUARIO_PRODUC';
          MI_CAMPOS := ' COMPANIA,CICLO,CODIGORUTA,ANO_INI
                        ,PERIODO_INI,ANOFAC,PERIODOFAC,USO
                        ,ESTRATO,ASEOUNICO_INI,ASEODOMICILIARIO_INI
                        ,ASEOBARRIDO_INI,ASEOCONSUMO_INI,ASEOUNICO
                        ,ASEODOMICILIARIO,ASEOBARRIDO,ASEOCONSUMO
                        ,ASEOUNICOXREC,ASEODOMICILIARIOXREC
                        ,ASEOBARRIDOXREC,ASEOCONSUMOXREC
                        ,DATE_CREATED,CREATED_BY';

          MI_VALORES := 'SELECT   SP_USUARIO_PRODUC.COMPANIA
                                 ,SP_USUARIO_PRODUC.CICLO
                                 ,SP_USUARIO_PRODUC.CODIGORUTA
                                 ,SP_USUARIO_PRODUC.ANO_INI
                                  ,SP_USUARIO_PRODUC.PERIODO_INI
                                 ,'||MI_ANIOSIG||' ANOFAC
                                 ,'''||MI_PERIODOSIG||''' PERIODOFAC
                                 ,SP_USUARIO_PRODUC.USO
                                 ,SP_USUARIO_PRODUC.ESTRATO
                                 ,CASE WHEN (SP_USUARIO_PRODUC.ASEOUNICO_INI        - SP_USUARIO_PRODUC.ASEOUNICO) < 0
                                       THEN 0
                                       ELSE (SP_USUARIO_PRODUC.ASEOUNICO_INI        - SP_USUARIO_PRODUC.ASEOUNICO)
                                  END ASEOUNICO_INI
                                 ,CASE WHEN (SP_USUARIO_PRODUC.ASEODOMICILIARIO_INI - SP_USUARIO_PRODUC.ASEODOMICILIARIO) < 0
                                       THEN 0
                                       ELSE (SP_USUARIO_PRODUC.ASEODOMICILIARIO_INI - SP_USUARIO_PRODUC.ASEODOMICILIARIO)
                                  END ASEODOMICILIARIO_INI
                                 ,CASE WHEN (SP_USUARIO_PRODUC.ASEOBARRIDO_INI      - SP_USUARIO_PRODUC.ASEOBARRIDO) < 0
                                       THEN 0
                                       ELSE (SP_USUARIO_PRODUC.ASEOBARRIDO_INI      - SP_USUARIO_PRODUC.ASEOBARRIDO)
                                  END ASEOBARRIDO_INI
                                 ,CASE WHEN (SP_USUARIO_PRODUC.ASEOCONSUMO_INI      - SP_USUARIO_PRODUC.ASEOCONSUMO) < 0
                                       THEN 0
                                       ELSE (SP_USUARIO_PRODUC.ASEOCONSUMO_INI      - SP_USUARIO_PRODUC.ASEOCONSUMO)
                                  END ASEOCONSUMO_INI
                                 ,0 ASEOUNICO
                                 ,0 ASEODOMICILIARIO
                                 ,0 ASEOBARRIDO
                                 ,0 ASEOCONSUMO
                                 ,CASE WHEN (SP_USUARIO_PRODUC.ASEOUNICOXREC 
                                           - SUM(SP_RECAUDO_PRODUCTIVIDAD.ASEOUNICO_REC)) < 0
                                       THEN 0
                                       ELSE (SP_USUARIO_PRODUC.ASEOUNICOXREC 
                                           - SUM(SP_RECAUDO_PRODUCTIVIDAD.ASEOUNICO_REC))
                                  END ASEOUNICOXREC
                                 ,CASE WHEN (SP_USUARIO_PRODUC.ASEODOMICILIARIOXREC 
                                           - SUM(SP_RECAUDO_PRODUCTIVIDAD.ASEODOMICILIARIO_REC)) < 0
                                       THEN 0
                                       ELSE (SP_USUARIO_PRODUC.ASEODOMICILIARIOXREC 
                                           - SUM(SP_RECAUDO_PRODUCTIVIDAD.ASEODOMICILIARIO_REC))
                                  END ASEODOMICILIARIOXREC
                                 ,CASE WHEN (SP_USUARIO_PRODUC.ASEOBARRIDOXREC 
                                           - SUM(SP_RECAUDO_PRODUCTIVIDAD.ASEOBARRIDO_REC)) < 0
                                       THEN 0
                                       ELSE (SP_USUARIO_PRODUC.ASEOBARRIDOXREC 
                                           - SUM(SP_RECAUDO_PRODUCTIVIDAD.ASEOBARRIDO_REC)) 
                                  END ASEOBARRIDOXREC
                                 ,CASE WHEN (SP_USUARIO_PRODUC.ASEOCONSUMOXREC 
                                           - SUM(SP_RECAUDO_PRODUCTIVIDAD.ASEOCONSUMO_REC)) < 0
                                       THEN 0
                                       ELSE (SP_USUARIO_PRODUC.ASEOCONSUMOXREC 
                                           - SUM(SP_RECAUDO_PRODUCTIVIDAD.ASEOCONSUMO_REC)) 
                                  END ASEOCONSUMOXREC,
                                  SYSDATE,
                                  '''||UN_USUARIO||'''
                         FROM     SP_USUARIO_PRODUC
                           INNER JOIN SP_RECAUDO_PRODUCTIVIDAD 
                             ON      SP_USUARIO_PRODUC.COMPANIA   = SP_RECAUDO_PRODUCTIVIDAD.COMPANIA
                            AND      SP_USUARIO_PRODUC.CICLO      = SP_RECAUDO_PRODUCTIVIDAD.CICLO
                            AND      SP_USUARIO_PRODUC.CODIGORUTA = SP_RECAUDO_PRODUCTIVIDAD.CODIGORUTA
                            AND      SP_USUARIO_PRODUC.ANOFAC     = SP_RECAUDO_PRODUCTIVIDAD.ANO
                            AND      SP_USUARIO_PRODUC.PERIODOFAC = SP_RECAUDO_PRODUCTIVIDAD.PERIODO
                         WHERE    SP_USUARIO_PRODUC.COMPANIA   = '''||UN_COMPANIA||'''
                           AND    SP_USUARIO_PRODUC.CICLO      = '  ||UN_CICLO||'
                           AND    SP_USUARIO_PRODUC.ANOFAC     = '  ||UN_ANIO||'
                           AND    SP_USUARIO_PRODUC.PERIODOFAC = '''||UN_PERIODO||'''
                           AND    SP_USUARIO_PRODUC.USO        = '''||MI_RS.USO||'''
                           AND    SP_USUARIO_PRODUC.ESTRATO     = '''||MI_RS.ESTRATO||'''
                         GROUP BY SP_USUARIO_PRODUC.COMPANIA
                                 ,SP_USUARIO_PRODUC.CICLO
                                 ,SP_USUARIO_PRODUC.CODIGORUTA
                                 ,SP_USUARIO_PRODUC.ANO_INI
                                 ,SP_USUARIO_PRODUC.PERIODO_INI
                                 ,'||MI_ANIOSIG||' 
                                 ,'''||MI_PERIODOSIG||''' 
                                 ,SP_USUARIO_PRODUC.USO
                                 ,SP_USUARIO_PRODUC.ESTRATO
                                 ,SP_USUARIO_PRODUC.ASEOUNICO_INI       
                                 ,SP_USUARIO_PRODUC.ASEOUNICO
                                 ,SP_USUARIO_PRODUC.ASEODOMICILIARIO_INI
                                 ,SP_USUARIO_PRODUC.ASEODOMICILIARIO
                                 ,SP_USUARIO_PRODUC.ASEOBARRIDO_INI     
                                 ,SP_USUARIO_PRODUC.ASEOBARRIDO 
                                 ,SP_USUARIO_PRODUC.ASEOCONSUMO_INI     
                                 ,SP_USUARIO_PRODUC.ASEOCONSUMO 
                                 ,0,0,0,0 
                                 ,SP_USUARIO_PRODUC.ASEOUNICOXREC 
                                 ,SP_USUARIO_PRODUC.ASEODOMICILIARIOXREC 
                                 ,SP_USUARIO_PRODUC.ASEOBARRIDOXREC 
                                 ,SP_USUARIO_PRODUC.ASEOCONSUMOXREC
                                 ,SYSDATE
                                 ,'''||UN_USUARIO||'''';
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                UN_ACCION  => 'IS',
                                                UN_CAMPOS  => MI_CAMPOS,
                                                UN_VALORES => MI_VALORES);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_USUARIOPRODUC
          );
      END;
    END LOOP TARIFAS;
  END PR_CERRARPRODUCTIVIDAD;

  -- 23
PROCEDURE PR_ELIMINARABONO(
     /*
     NAME              : PR_ELIMINARABONO --> Se pasa del formulario frmAbonosSub.
     AUTHORS           : SYSMAN  SAS
     AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
     DATE MIGRADOR     : 09/05/2017
     TIME              : 08:15 AM
     SOURCE MODULE     : SERVICIOS PUBLICOS
     MODIFIER          :
     DATE MODIFIED     :
     TIME              :
     DESCRIPTION       : Procedimiento Para eliminar los detalles de los abonos, Se ejecuta al eliminar un registro en la tabla
                         SP_ABONOS, y despues de validarse las condiciones en el trigger BIUD_SP_ABONOS

     PARAMETERS        : UN_COMPANIA       => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                         UN_CODIGORUTA     => Codigo de ruta del usuario que se elimina el abono
                         UN_CICLO          => Ciclo del usuario
                         UN_ANO            => Año del cual se va a eliminar el abono.
                         UN_PERIODO        => Periodo del cual se va a eliminar el abono.
                         UN_FECHAABONO     => Fecha en la que se creo el abono.
                         UN_BANCOABONO     => Banco en el que se pago el abono
                         UN_CONSECUTIVOABONO => Consecutivo unico del abono
                         UN_PAGOCONVENIO    => Valor por pago de convenios
                         UN_PAGOTERCERIZADO => Valor por pago de Aseo tercerizado

     MODIFICATIONS     :

     @NAME:    EliminarAbono
     @METHOD:  POST
   */

   UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA
  ,UN_CODIGORUTA       IN SP_USUARIO.CODIGORUTA%TYPE
  ,UN_CICLO            IN PCK_SUBTIPOS.TI_CICLO
  ,UN_ANO              IN PCK_SUBTIPOS.TI_ANIO
  ,UN_PERIODO          IN PCK_SUBTIPOS.TI_PERIODO
  ,UN_FECHAABONO       IN SP_ABONOS.FECHA%TYPE
  ,UN_BANCOABONO       IN SP_ABONOS.BANCO%TYPE
  ,UN_CONSECUTIVOABONO IN SP_ABONOS.CONSECUTIVO%TYPE
  ,UN_PAGOCONVENIO     IN SP_ABONOS.PAGOCONVENIOS%TYPE     DEFAULT 0
  ,UN_PAGOTERCERIZADO  IN SP_ABONOS.PAGOTERCERIZADO%TYPE   DEFAULT 0
  ,UN_USUARIOELIMABONO IN VARCHAR2
) AS
MI_PARAMETRO       PCK_SUBTIPOS.TI_PARAMETRO;
MI_RTA             PCK_SUBTIPOS.TI_ENTERO   DEFAULT 0;
MI_RTACALCULO      VARCHAR2(300 CHAR);

MI_TABLA           PCK_SUBTIPOS.TI_STRSQL;
MI_MERGEUSING      PCK_SUBTIPOS.TI_MERGEUSING;
MI_MERGEENLACE     PCK_SUBTIPOS.TI_MERGEENLACE;
MI_MERGEEXISTE     PCK_SUBTIPOS.TI_MERGEEXISTE;
MI_MSGERROR        PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_CONDICION       PCK_SUBTIPOS.TI_CONDICION;

BEGIN
   MI_PARAMETRO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                             UN_NOMBRE    =>  'ABONOS PRIORIDAD TERCERIZADO Y CONVENIOS',
                                             UN_MODULO    =>  PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                             UN_FECHA_PAR =>  SYSDATE),'NO');
   IF MI_PARAMETRO = 'SI' THEN
       IF UN_PAGOCONVENIO > 0 THEN
           MI_RTA := PCK_SERVICIOS_PUBLICOS_COM2.FC_ACTUALIZARPAGOCONVENIOS
                       (UN_COMPANIA    => UN_COMPANIA
                       ,UN_CICLO       => UN_CICLO
                       ,UN_CODIGORUTA  => UN_CODIGORUTA
                       ,UN_ANO         => UN_ANO
                       ,UN_PERIODO     => UN_PERIODO
                       ,UN_FECHA       => UN_FECHAABONO
                       ,UN_BANCO       => UN_BANCOABONO
                       ,UN_PAQUETE     => '888'
                       ,UN_REVERSA     => -1
                       ,UN_PAGODOBLE   => 0
                       ,UN_CONVENIO    => MI_PARAMETRO );
       END IF;

       IF UN_PAGOTERCERIZADO > 0 THEN
           MI_RTA := PCK_SERVICIOS_PUBLICOS_COM2.FC_ACTUALIZAPAGOTERCERIZADOS
                       (UN_COMPANIA      => UN_COMPANIA
                       ,UN_CICLO         => UN_CICLO
                       ,UN_CODIGORUTA    => UN_CODIGORUTA
                       ,UN_ANO           => UN_ANO
                       ,UN_PERIODO       => UN_PERIODO
                       ,UN_FECHA         => UN_FECHAABONO
                       ,UN_BANCO         => UN_BANCOABONO
                       ,UN_PAQUETE       => '888'
                       ,UN_REVERSA       => -1
                       ,UN_PAGODOBLE     => 0
                       ,UN_TERCER        => MI_PARAMETRO);
       END IF;
   END IF;

   --Reversa los abonos de la tabla facturado
   BEGIN
       MI_TABLA := 'SP_FACTURADO';
       MI_MERGEUSING := ' SELECT SP_D_ABONOS.COMPANIA,
                                 SP_D_ABONOS.CICLO,
                                 SP_D_ABONOS.CODIGORUTA,
                                 SP_D_ABONOS.ANO,
                                 SP_D_ABONOS.PERIODO,
                                 SP_D_ABONOS.CONCEPTO,
                                 SP_FACTURADO.VALORABONOACT - SP_D_ABONOS.VALORACT VALORABONOACT,
                                 SP_FACTURADO.VALORABONOANT - SP_D_ABONOS.VALORANT VALORABONOANT
                         FROM    SP_FACTURADO  INNER JOIN SP_D_ABONOS
                                 ON SP_FACTURADO.COMPANIA   = SP_D_ABONOS.COMPANIA
                                 AND SP_FACTURADO.CICLO      = SP_D_ABONOS.CICLO
                                 AND SP_FACTURADO.CODIGORUTA = SP_D_ABONOS.CODIGORUTA
                                 AND SP_FACTURADO.ANO        = SP_D_ABONOS.ANO
                                 AND SP_FACTURADO.PERIODO    = SP_D_ABONOS.PERIODO
                                 AND SP_FACTURADO.CONCEPTO   = SP_D_ABONOS.CONCEPTO
                         WHERE   SP_FACTURADO.COMPANIA   = '''|| UN_COMPANIA ||'''
                           AND   SP_FACTURADO.CICLO      = '|| UN_CICLO ||'
                           AND   SP_FACTURADO.CODIGORUTA = '''|| UN_CODIGORUTA ||'''
                           AND   SP_FACTURADO.ANO        = '|| UN_ANO ||'
                           AND   SP_FACTURADO.PERIODO    = '''|| UN_PERIODO ||'''
                           AND   SP_D_ABONOS.CONSECUTIVO = '|| UN_CONSECUTIVOABONO ||'  ';

       MI_MERGEENLACE := ' VISTA.COMPANIA       = TABLA.COMPANIA
                           AND VISTA.CICLO      = TABLA.CICLO
                           AND VISTA.CODIGORUTA = TABLA.CODIGORUTA
                           AND VISTA.ANO        = TABLA.ANO
                           AND VISTA.PERIODO    = TABLA.PERIODO
                           AND VISTA.CONCEPTO   = TABLA.CONCEPTO  ';

       MI_MERGEEXISTE := 'UPDATE SET TABLA.VALORABONOACT    = VISTA.VALORABONOACT,
                                     TABLA.VALORABONOANT    = VISTA.VALORABONOANT';

       BEGIN
           MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA,
                                       UN_ACCION      => 'MM',
                                       UN_MERGEUSING  => MI_MERGEUSING,
                                       UN_MERGEENLACE => MI_MERGEENLACE,
                                       UN_MERGEEXISTE => MI_MERGEEXISTE);
           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
               RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
       END;
   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
       MI_MSGERROR(1).CLAVE := 'USUARIO';
       MI_MSGERROR(1).VALOR := UN_CODIGORUTA;
       PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                  UN_ERROR_COD  => PCK_ERRORES.ERR_BORRARABONOFACTURADO,
                                  UN_REEMPLAZOS => MI_MSGERROR);
   END;

   BEGIN
       MI_TABLA := 'SP_D_ABONOS';
       MI_CONDICION := ' COMPANIA        = '''|| UN_COMPANIA ||'''
                         AND CICLO       = '|| UN_CICLO ||'
                         AND CODIGORUTA  = '''|| UN_CODIGORUTA ||'''
                         AND ANO         = '|| UN_ANO ||'
                         AND PERIODO     = '''|| UN_PERIODO ||'''
                         AND CONSECUTIVO = '|| UN_CONSECUTIVOABONO ||' ';
       BEGIN
           MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => MI_TABLA
                                      ,UN_ACCION     => 'E'
                                      ,UN_CONDICION  => MI_CONDICION);
       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
       END;
   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
       MI_MSGERROR(1).CLAVE := 'USUARIO';
       MI_MSGERROR(1).VALOR := UN_CODIGORUTA ;
       PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                  UN_ERROR_COD => PCK_ERRORES.ERR_BORRARABONODETALLE,
                                  UN_TABLAERROR => MI_TABLA,
                                  UN_REEMPLAZOS => MI_MSGERROR);
   END;

   --Actualiza los abonos en la D_recaudo.
   PCK_SERVICIOS_PUBLICOS_COM2.PR_PASAABONOSRECAUDOS
       (UN_COMPANIA    => UN_COMPANIA
       ,UN_STRFECHA    => UN_FECHAABONO
       ,UN_STRBANCO    => UN_BANCOABONO
       ,UN_USUARIO     => UN_USUARIOELIMABONO);

   --Abonos Productividad
   PCK_SERVICIOS_PUBLICOS_COM1.PR_ELIMINARECPROD
       (UN_COMPANIA    => UN_COMPANIA
       ,UN_CICLO       => UN_CICLO
       ,UN_USUARIO     => UN_CODIGORUTA
       ,UN_FECHA       => UN_FECHAABONO
       ,UN_BANCO       => UN_BANCOABONO
       ,UN_PAQUETE     => '888'
       ,UN_OPERACION   => 'ABONO' );

   --Realiza cálculo para actualizar los facturados
   MI_RTACALCULO := PCK_SERVICIOS_PUBLICOS_COM7.FC_CALCULOFACTURACION
                       (UN_COMPANIA          => UN_COMPANIA
                       ,UN_INTCICLO          => UN_CICLO
                       ,UN_STRCODIGOINICIAL  => UN_CODIGORUTA
                       ,UN_STRCODIGOFINAL    => UN_CODIGORUTA
                       ,UN_ENSERIE           => -1
                       ,UN_FINAL             => 0);



END PR_ELIMINARABONO;


END PCK_SERVICIOS_PUBLICOS_COM6;