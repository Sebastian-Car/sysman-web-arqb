create or replace PACKAGE BODY PCK_SERVICIOS_PUBLICOS_COM4 AS
  
  -- 1
  FUNCTION FC_AUTORIZACION_FACTURAPAGADA
    /*
      NAME              : FC_AUTORIZACION_FACTURAPAGADA --> EN ACCESS AUTORIZACION_COPIAFACTURAPAGADA
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : VICTOR JULIO MOLANO BOLIVAR
      DATE MIGRADOR     : 06/10/2016
      TIME              : 03:09 PM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MODIFIED     : 06/01/2017
      TIME              : 05:20 PM
      DESCRIPTION       : FUNCION QUE RETORNA -1(VERDADERO) O 0(FALSO) DEPENDIENDO DEL NIT
      PARAMETERS        : UN_COMPANIA => COMPANIA EN LA QUE SE ESTA TRABAJANDO
                          UN_NIT      => NÚMERO DE NIT QUE SE DESEA VALIDAR Y AUTORIZAR.
      MODIFICATIONS     : CORRECCION SEGUN ESTANDAR DE PROGRAMACION Y REFERENCIACION DE FUNCIONES.
      @NAME:  autorizarFacturaPagada
      @METHOD:  GET
    */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_NIT      IN VARCHAR2 
  )
    RETURN NUMBER
  AS 
    MI_AUTORIZACION  PCK_SUBTIPOS.TI_ENTERO;
    MI_NIT           VARCHAR2(30 CHAR); 
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
      WHEN MI_NIT        = '800021261' THEN --PRUEBA
        MI_AUTORIZACION := -1;
      ELSE
        MI_AUTORIZACION := 0;
    END CASE;
    RETURN MI_AUTORIZACION;
  END FC_AUTORIZACION_FACTURAPAGADA;

  -- 2
  FUNCTION FC_ESTA_BLOQUEADO
    /*
      NAME              : FC_ESTA_BLOQUEADO --> EN ACCESS EstaBloqueado
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : VICTOR JULIO MOLANO BOLIVAR
      DATE MIGRADOR     : 06/10/2016
      TIME              : 05:30 PM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MODIFIED     : 06/01/2017
      TIME              : 05:45 PM
      DESCRIPTION       : FUNCION QUE RETORNA -1 O 0 DEPENDIENDO DE SI UN CICLO ESTA O NO BLOQUEADO.
      PARAMETERS        : UN_COMPANIA => COMPANIA EN LA QUE SE ESTA TRABAJANDO
                          UN_CICLO    => CICLO EN QUE SE ESTA TRABAJANDO
      MODIFICATIONS     : CORRECCION SEGUN ESTANDAR DE PROGRAMACION Y OPTIMIZACION DE MANEJO DE ERRORES.
      @NAME:  estarBloqueado
      @METHOD:  GET
    */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_CICLO    IN PCK_SUBTIPOS.TI_CICLO
  )
    RETURN NUMBER
  AS 
	  MI_BLOQUEADO        PCK_SUBTIPOS.TI_ENTERO;
	  MI_INDBLOQUEOMANUAL PCK_SUBTIPOS.TI_ENTERO;
	  MI_INDPREPARADO     PCK_SUBTIPOS.TI_ENTERO;
	BEGIN
	  BEGIN
	  	SELECT  INDPREPARADO
             ,INDBLOQUEOMANUAL
      INTO    MI_INDPREPARADO
             ,MI_INDBLOQUEOMANUAL
      FROM    SP_CICLO
	  	WHERE   COMPANIA = UN_COMPANIA
        AND   NUMERO   = UN_CICLO;
      EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_INDPREPARADO := 0;
        MI_INDBLOQUEOMANUAL := 0;
    END;
    IF MI_INDPREPARADO = -1 AND MI_INDBLOQUEOMANUAL = -1 THEN
		  MI_BLOQUEADO := -1;
		ELSE
		  MI_BLOQUEADO := 0;
		END IF;
	  RETURN MI_BLOQUEADO;
  END FC_ESTA_BLOQUEADO;

  -- 3
  FUNCTION FC_AUTORIZACION_CONVENIOS
    /*
      NAME              : FC_AUTORIZACION_CONVENIOS --> EN ACCESS AUTORIZACION_CONVENIO
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : VICTOR JULIO MOLANO BOLIVAR
      DATE MIGRADOR     : 14/10/2016
      TIME              : 12:40 PM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MODIFIED     : 10/01/2017
      TIME              : 08:10 AM
      DESCRIPTION       : FUNCION QUE RETORNA -1(VERDADERO) O 0(FALSO) DEPENDIENDO DEL NIT
      PARAMETERS        : UN_COMPANIA => COMPANIA EN LA QUE SE ESTA TRABAJANDO
                          UN_NIT      => NÚMERO DE NIT QUE SE DESEA VALIDAR Y AUTORIZAR.
      MODIFICATIONS     : CORRECCION SEGUN ESTANDAR DE PROGRAMACION Y REFERENCIACION DE FUNCIONES.
      @NAME:  autorizarConvenios
      @METHOD:  GET
    */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_NIT      IN VARCHAR2 
  )
    RETURN NUMBER
  AS 
    MI_AUTORIZACION PCK_SUBTIPOS.TI_ENTERO;
    MI_NIT          VARCHAR2(30 CHAR);
  BEGIN
    IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                             UN_NOMBRE    => 'MANEJA CONVENIO DE FACTURACION', 
                             UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                             UN_FECHA_PAR => SYSDATE) <> 'SI' THEN 
      MI_AUTORIZACION := 0;
      RETURN MI_AUTORIZACION;
    END IF;
    MI_NIT := PCK_SERVICIOS_PUBLICOS_COM1.FC_NITVALIDAR(UN_COMPANIA => UN_COMPANIA,
                                                        UN_NIT => UN_NIT);
    CASE
      WHEN MI_NIT = '844000755' THEN --YOPAL
        MI_AUTORIZACION := -1;
      WHEN MI_NIT = '832000776' THEN --FUNZA
        MI_AUTORIZACION := -1;
      WHEN MI_NIT = '832001512' THEN --MADRID
        MI_AUTORIZACION := -1;
      WHEN MI_NIT = '899999714' THEN --CHIA
        MI_AUTORIZACION := -1;
      WHEN MI_NIT = '890680053' THEN --FUSAGASUGA
        MI_AUTORIZACION := -1;
      WHEN MI_NIT = '800021261' THEN --PRUEBA
        MI_AUTORIZACION := -1;
      ELSE
        MI_AUTORIZACION := 0;
    END CASE;
    RETURN MI_AUTORIZACION;
  END FC_AUTORIZACION_CONVENIOS;

  -- 4 
  FUNCTION FC_PERMISO_ACCION
    /*
      NAME              : FC_PERMISO_ACCION --> EN ACCESS PermisoAccion, PermisoTipoRequerimientoPQR
                                                PermisoDesActivaDesc, PermisoManuales
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : VICTOR JULIO MOLANO BOLIVAR
      DATE MIGRADOR     : 20/10/2016
      TIME              : 10:00 AM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MODIFIED     : 10/01/2017
      TIME              : 08:30 AM
      DESCRIPTION       : FUNCION QUE RETORNA -1(VERDADERO) O 0(FALSO) DEPENDIENDO DEL NIT
      PARAMETERS        : UN_COMPANIA  => COMPANIA EN LA QUE SE ESTA TRABAJANDO
                          UN_ACCION    => NOMBRE DEL PARAMETRO QUE CONTIENE LOS USUARIOS AUTORIZADOS SEPARADOS POR COMA.
                          UN_USUARIO   => USUARIO ACTUAL.
                          UN_PARAMETRO => PARAMETRO QUE ACTIVA O NO EL PROCESO A AUTORIZAR. ESTE PARAMETRO SE EVALUA
                                          PRIMERO, Y SI ESTA EN NO, SE RETORNA 0 PUES INDICA QUE NO MANEJA ESE PROCESO.
      MODIFICATIONS     : CORRECCION SEGUN ESTANDAR DE PROGRAMACION Y REFERENCIACION DE FUNCIONES.
      @NAME:  permitirAccion
      @METHOD:  GET
    */
  (
    UN_COMPANIA  IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_ACCION    IN VARCHAR2 -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
   ,UN_USUARIO   IN PCK_SUBTIPOS.TI_USUARIO
   ,UN_PARAMETRO IN PCK_SUBTIPOS.TI_PARAMETRO DEFAULT NULL
  )
    RETURN NUMBER
  AS 
    MI_PERMISO    PCK_SUBTIPOS.TI_ENTERO;
    MI_VALOR      VARCHAR2(30000 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
  BEGIN
    MI_PERMISO := 0;
    IF UN_PARAMETRO IS NOT NULL THEN
      MI_VALOR := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                        UN_NOMBRE    => UN_PARAMETRO, 
                                        UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                        UN_FECHA_PAR => SYSDATE); 
      IF MI_VALOR IS NULL OR MI_VALOR <> 'SI' THEN
        RETURN MI_PERMISO;
      END IF;
    END IF;
    MI_VALOR := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                      UN_NOMBRE    => UN_ACCION, 
                                      UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                                      UN_FECHA_PAR => SYSDATE); 
    IF MI_VALOR IS NOT NULL THEN 
      MI_VALOR := REPLACE(MI_VALOR, ' ','');
      IF SUBSTR(MI_VALOR,1,1)<>',' THEN
        MI_VALOR := ',' || MI_VALOR;         
      END IF;
      IF SUBSTR(MI_VALOR,LENGTH(MI_VALOR)-1,1) <> ',' THEN
        MI_VALOR := MI_VALOR || ',';
      END IF;
      IF INSTR(MI_VALOR, '' || UN_USUARIO || ',') <> 0 THEN
        MI_PERMISO := -1;
      END IF;
    END IF;
    RETURN MI_PERMISO;
  END FC_PERMISO_ACCION;

  -- 5
  FUNCTION FC_OBTENER_COMENTARIOS_PERIODO
    /*
      NAME              : FC_OBTENER_COMENTARIOS_PERIODO (En getComentariosPeriodo)
      AUTHORS           : SYSMAN  SAS 
      AUTHOR MIGRACION  : VICTOR JULIO MOLANO BOLIVAR
      DATE MIGRADOR     : 10/11/2016
      TIME              : 03:10 PM
      SOURCE MODULE     : SysmanSp2016.05.04
      MODIFIER          : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MODIFIED     : 10/01/2017
      TIME              : 09:05 AM
      DESCRIPTION       : RETORNA LOS COMENTARIOS DE UN DETERMINADO USUARIO EN EL PERIODO SOLICITADO.
      MODIFICATIONS     : EN CUANTO A LA VERSION DE ACCESS, SE AGREGA EL PARAMETRO "UN_COMENTARIOS" PARA HACER LA FUNCION
                          INDEPENDIENTE DE LOS FORMULARIOS.
      PARAMETERS        : UN_COMPANIA    => COMPANIA ACTUAL DEL USUARIO.
                          UN_COMENTARIOS => REPRESENTA EL STRING CON TODOS LOS COMENTARIOS HISTORICOS DEL SUSCRIPTOR.
                          UN_ANO         => ANIO DEL CUAL SE REQUIEREN LOS COMENTARIOS.
                          UN_PERIODO     => PERIODO DEL CUAL SE REQUIEREN LOS COMENTARIOS.
      MODIFICATIONS     : CORRECCION SEGUN ESTANDAR DE PROGRAMACION Y REFERENCIACION DE FUNCIONES.
      @NAME:  obtenerComentariosPeriodo
      @METHOD:  GET
    */
  (
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_COMENTARIOS IN SP_USUARIO.COMENTARIOS%TYPE
   ,UN_ANO         IN PCK_SUBTIPOS.TI_ANIO
   ,UN_PERIODO     IN PCK_SUBTIPOS.TI_PERIODO
  )
    RETURN VARCHAR2
  AS
    MI_POS_INICIAL PCK_SUBTIPOS.TI_ENTERO;
    MI_POS_FINAL   PCK_SUBTIPOS.TI_ENTERO;
    MI_RTA         VARCHAR2(5000 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERÍSTICAS EN PCK_SUBTIPOS
  BEGIN
    MI_POS_INICIAL := INSTR(UN_COMENTARIOS, UN_ANO || UN_PERIODO);
    IF MI_POS_INICIAL = 0 THEN  
      MI_RTA := '';
    ELSE
      MI_POS_FINAL := INSTR(UN_COMENTARIOS, PCK_SERVICIOS_PUBLICOS.FC_ANO_PERIODO_SIGUIENTE(UN_COMPANIA => UN_COMPANIA, 
                                                                                            UN_ANO      => UN_ANO, 
                                                                                            UN_PERIODO  => UN_PERIODO));
      IF MI_POS_FINAL = 0 THEN
        MI_POS_FINAL := LENGTH(UN_COMENTARIOS) - MI_POS_INICIAL;
      ELSE
        MI_POS_FINAL := MI_POS_FINAL - MI_POS_INICIAL - 8;
      END IF;
      MI_RTA := SUBSTR(UN_COMENTARIOS, MI_POS_INICIAL+8, MI_POS_FINAL);
    END IF;
    RETURN MI_RTA;
  END FC_OBTENER_COMENTARIOS_PERIODO;

  -- 6
  FUNCTION FC_ARMACONSULTAPLANOSEXP
    /*
      NAME              : FC_ARMACONSULTAPLANOSEXP --> EN ACCESS ARMACONSULTAPLANOSEXP
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : YEISSON ALEJANDRO ROJAS RUIZ
      DATE MIGRADOR     : 28/12/2016
      TIME              : 04:40 PM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MODIFIED     : 10/01/2017
      TIME              : 09:30 AM
      DESCRIPTION       : FUNCION QUE RETORNA UNA CADENA CON UN QUERY QUE SERVIRA PARA EXPORTAR UN ARCHIVO PLANO.
      PARAMETERS        : UN_COMPANIA      => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_CICLO         => CICLO POR EL CUAL SE VA A FILTRAR LA INFORMACION.
                          UN_CODIGOINICIAL => CODIGO INICIAL PARA LA CONSULTA DE LOS REGISTROS.
                          UN_CODIGOFINAL   => CODIGO FINAL PARA LA CONSULTA DE LOS REGISTROS.
      MODIFICATIONS     : CORRECCION SEGUN ESTANDAR DE PROGRAMACION Y OPTIMIZACION DE MANEJO DE ERRORES.
      @NAME:  armarConsultaPlanoExp
      @METHOD:  GET
    */
  (
    UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_CICLO         IN PCK_SUBTIPOS.TI_CICLO
   ,UN_CODIGOINICIAL IN PCK_SUBTIPOS.TI_CODIGORUTA
   ,UN_CODIGOFINAL   IN PCK_SUBTIPOS.TI_CODIGORUTA
  )
    RETURN VARCHAR2
  AS
    MI_STRSQL           PCK_SUBTIPOS.TI_STRSQL;
    MI_TERCE            PCK_SUBTIPOS.TI_TERCERO;
    MI_TERCERO          PCK_SUBTIPOS.TI_LOGICO;
    MI_CONVE            PCK_SUBTIPOS.TI_LOGICO; 
    MI_PERIODO          PCK_SUBTIPOS.TI_PERIODO;
    MI_ANIO             PCK_SUBTIPOS.TI_ANIO;
    MI_UNCODTER         PCK_SUBTIPOS.TI_LOGICO;
    MI_STRWHERE         PCK_SUBTIPOS.TI_STRSQL;
    MI_CONVENIOSPER     PCK_SUBTIPOS.TI_STRSQL;
    MI_TERCERIZAPER     PCK_SUBTIPOS.TI_STRSQL;
    MI_SQLFINAL         PCK_SUBTIPOS.TI_STRSQL;
    MI_TABLAUPDATE      PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOSUPDATE     PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICIONUPDATE  PCK_SUBTIPOS.TI_CONDICION;
  BEGIN
    MI_TERCE := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                          UN_NOMBRE    => 'MANEJA PROCESO TERCERIZADO',
                                          UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                          UN_FECHA_PAR => SYSDATE), 'NO');
    IF MI_TERCE = 'SI' THEN
      MI_TERCERO := 1;
    ELSE
      MI_TERCERO := 0;
    END IF;
    MI_CONVE := PCK_SERVICIOS_PUBLICOS_COM4.FC_AUTORIZACION_CONVENIOS(UN_COMPANIA => UN_COMPANIA,
                                                                      UN_NIT      => MI_TERCERO);
    IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                 UN_NOMBRE    => 'PROCESO TERCERIZADO CON UN SOLO CODIGO DE BARRAS',
                                 UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                 UN_FECHA_PAR => SYSDATE), 'NO') = 'SI' AND MI_TERCERO = 1 THEN 
      MI_UNCODTER := 1;
    ELSE 
      MI_UNCODTER := 0;
    END IF;
    MI_SQLFINAL := '';
    BEGIN 
      SELECT  ANO 
             ,PERIODO
      INTO    MI_ANIO 
             ,MI_PERIODO
      FROM    SP_CICLO
      WHERE   COMPANIA = UN_COMPANIA
        AND   NUMERO   = UN_CICLO;
      EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_ANIO := NULL;
        MI_PERIODO := NULL;
    END;
    IF MI_ANIO IS NOT NULL THEN
      MI_ANIO := NVL(MI_ANIO,0);
      MI_PERIODO := NVL(MI_PERIODO,0);
    END IF;
    -- CADENA QUE SE VA A ANADIR COMO EL WHERE DE LAS CONSULTAS QUE PERTENECEN A LA CLAUSULA WITH DEL QUERY FINAL
    MI_STRWHERE := '    COMPANIA   = '''|| UN_COMPANIA ||'''
                    AND CICLO      = '  || UN_CICLO ||'  
                    AND CODIGORUTA BETWEEN  '''|| UN_CODIGOINICIAL ||''' AND '''|| UN_CODIGOFINAL ||'''
                    AND ANO        = '  || MI_ANIO ||'  
                    AND PERIODO    = '''|| MI_PERIODO ||'''';
    IF MI_CONVE <> 0 THEN
      -- ACTUALIZACION EN LA TABLA SP_HISTORIA_CONVENIOS
      BEGIN
        BEGIN
          MI_TABLAUPDATE := 'SP_HISTORIA_CONVENIOS';
          MI_CAMPOSUPDATE := 'NOCOBRAR = 0';
          PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLAUPDATE,
                                               UN_ACCION    => 'M',
                                               UN_CAMPOS    => MI_CAMPOSUPDATE,
                                               UN_CONDICION => MI_STRWHERE);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD   => SQLCODE,
            UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_ACTUA_HISCONVENIOS
          );
      END;
      MI_STRWHERE := 'WHERE' || MI_STRWHERE;
      -- CADENA QUE SE AÃ‘ADE EN LA CLÃ?USULA WITH DE LA CONSULTA FINAL      
      MI_CONVENIOSPER := 'CONVENIOSPER AS (
                            SELECT   COMPANIA 
                                    ,CICLO 
                                    ,CODIGORUTA 
                                    ,ANO 
                                    ,PERIODO 
                                     SUM(TOTAL) TOTAL 
                            FROM     SP_HISTORIA_CONVENIOS 
                            '|| MI_STRWHERE ||'
                              AND    NOCOBRAR = 0
                            GROUP BY COMPANIA
                                    ,CICLO 
                                    ,CODIGORUTA 
                                    ,ANO 
                                    ,PERIODO
                          )';
    END IF;
    IF MI_UNCODTER <> 0 THEN
      MI_STRWHERE := 'WHERE' || MI_STRWHERE;
      -- CADENA QUE SE AÃ‘ADE EN LA CLÃ?USULA WITH DE LA CONSULTA FINAL
      MI_TERCERIZAPER := 'TERCERIZAPER AS (
                            SELECT   COMPANIA 
                                    ,CICLO
                                    ,CODIGORUTA
                                    ,ANO
                                    ,PERIODO
                                    ,SUM(VALORASEO) TOTAL
                            FROM     SP_HISTORIA_EXTERNA 
                            '||MI_STRWHERE||'
                            GROUP BY COMPANIA
                                    ,CICLO 
                                    ,CODIGORUTA
                                    ,ANO
                                    ,PERIODO
                          )';
    END IF;
    -- CADENA QUE SE AÃ‘ADE COMO EL WHERE DE LA CONSULTA FINAL
    MI_STRWHERE := 'WHERE SP_USUARIO.COMPANIA   IN ('''|| UN_COMPANIA ||''')
                      AND SP_USUARIO.CICLO      IN ('  || UN_CICLO ||')
                      AND SP_USUARIO.CODIGORUTA BETWEEN '''|| UN_CODIGOINICIAL ||''' AND '''|| UN_CODIGOFINAL ||'''';
    --CONDICIONES QUE ARMAN LA CADENA DE LA CONSULTA FINAL QUE SE VA A RETORNAR      
    IF MI_UNCODTER <> 0 AND MI_CONVE <> 0 THEN
        MI_STRSQL := 'WITH 
                     '|| MI_CONVENIOSPER ||',
                     '|| MI_TERCERIZAPER ||' 
                      SELECT   SP_USUARIO.CODIGORUTA 
                              ,SP_USUARIO.CICLO 
                              ,SP_USUARIO.CODIGOINTERNO 
                              ,SP_USUARIO.FACTURA 
                              ,SP_USUARIO.TOTFACTURAPERACTUAL
                              ,NVL(CONVENIOSPER.TOTAL,0) CONVENIOS
                              ,NVL(TERCERIZAPER.TOTAL,0) TERCERIZA
                              ,SP_USUARIO.ANO 
                              ,SP_USUARIO.PERIODO 
                              ,SP_USUARIO.NIT 
                              ,SP_USUARIO.FECHALIMITE
                      FROM     SP_USUARIO 
                        LEFT JOIN CONVENIOSPER 
                          ON      SP_USUARIO.COMPANIA   = CONVENIOSPER.COMPANIA
                         AND      SP_USUARIO.CICLO      = CONVENIOSPER.CICLO
                         AND      SP_USUARIO.CODIGORUTA = CONVENIOSPER.CODIGORUTA
                        LEFT JOIN TERCERIZAPER
                          ON      SP_USUARIO.COMPANIA   = TERCERIZAPER.COMPANIA
                         AND      SP_USUARIO.CICLO      = TERCERIZAPER.CICLO
                         AND      SP_USUARIO.CODIGORUTA = TERCERIZAPER.CODIGORUTA 
                      '||MI_STRWHERE||' 
                        AND    SP_USUARIO.TOTFACTURAPERACTUAL 
                             + NVL(CONVENIOSPER.TOTAL,0)
                             + NVL(TERCERIZAPER.TOTAL ,0) > 0
                      ORDER BY SP_USUARIO.CICLO 
                              ,SP_USUARIO.CODIGORUTA';
    ELSIF MI_UNCODTER <> 0 THEN
      MI_STRSQL := 'WITH 
                   '|| MI_TERCERIZAPER ||'
                    SELECT   SP_USUARIO.CODIGORUTA 
                            ,SP_USUARIO.CICLO 
                            ,SP_USUARIO.CODIGOINTERNO 
                            ,SP_USUARIO.FACTURA 
                            ,SP_USUARIO.TOTFACTURAPERACTUAL
                            ,0 CONVENIOS 
                            ,NVL(TERCERIZAPER.TOTAL,0) TERCERIZA 
                            ,SP_USUARIO.ANO 
                            ,SP_USUARIO.PERIODO 
                            ,SP_USUARIO.NIT
                            ,SP_USUARIO.FECHALIMITE
                    FROM     SP_USUARIO 
                      LEFT JOIN TERCERIZAPER 
                        ON      SP_USUARIO.COMPANIA   = TERCERIZAPER.COMPANIA
                       AND      SP_USUARIO.CICLO      = TERCERIZAPER.CICLO
                       AND      SP_USUARIO.CODIGORUTA = TERCERIZAPER.CODIGORUTA 
                    '||MI_STRWHERE||' 
                      AND    SP_USUARIO.TOTFACTURAPERACTUAL 
                           + NVL(TERCERIZAPER.TOTAL ,0) > 0 
                    ORDER BY SP_USUARIO.CICLO 
                            ,SP_USUARIO.CODIGORUTA';
    ELSIF MI_TERCERO <> 0 AND MI_CONVE <> 0 THEN
      MI_STRSQL := 'WITH 
                   '|| MI_CONVENIOSPER ||'
                    SELECT   SP_USUARIO.CODIGORUTA 
                            ,SP_USUARIO.CICLO 
                            ,SP_USUARIO.CODIGOINTERNO 
                            ,SP_USUARIO.FACTURA 
                            ,SP_USUARIO.TOTFACTURAPERACTUAL 
                            ,NVL(CONVENIOSPER.TOTAL,0) CONVENIOS 
                            ,0 TERCERIZA 
                            ,SP_USUARIO.ANO 
                            ,SP_USUARIO.PERIODO 
                            ,SP_USUARIO.NIT 
                            ,SP_USUARIO.FECHALIMITE
                    FROM     SP_USUARIO
                      LEFT JOIN CONVENIOSPER 
                        ON      SP_USUARIO.COMPANIA   = CONVENIOSPER.COMPANIA
                       AND      SP_USUARIO.CICLO      = CONVENIOSPER.CICLO
                       AND      SP_USUARIO.CODIGORUTA = CONVENIOSPER.CODIGORUTA  
                    '||MI_STRWHERE||'
                      AND    SP_USUARIO.TOTFACTURAPERACTUAL 
                           + NVL(CONVENIOSPER.TOTAL,0)             > 0
                      AND    NVL(SP_USUARIO.EMPRESAASEOEXT, '' '') = '' ''
                    ORDER BY SP_USUARIO.CICLO
                            ,SP_USUARIO.CODIGORUTA';
    ELSIF MI_TERCERO <> 0 THEN
      MI_STRSQL := 'SELECT   SP_USUARIO.CODIGORUTA 
                            ,SP_USUARIO.CICLO 
                            ,SP_USUARIO.CODIGOINTERNO
                            ,SP_USUARIO.FACTURA
                            ,SP_USUARIO.TOTFACTURAPERACTUAL 
                            ,0 CONVENIOS 
                            ,0 TERCERIZA
                            ,SP_USUARIO.ANO 
                            ,SP_USUARIO.PERIODO 
                            ,SP_USUARIO.NIT 
                            ,SP_USUARIO.FECHALIMITE
                    FROM     SP_USUARIO
                    '||MI_STRWHERE||'
                      AND    SP_USUARIO.TOTFACTURAPERACTUAL        > 0
                      AND    NVL(SP_USUARIO.EMPRESAASEOEXT, '' '') = '' '' 
                    ORDER BY SP_USUARIO.CICLO 
                            ,SP_USUARIO.CODIGORUTA';
    ELSIF MI_CONVE <> 0 THEN
      MI_STRSQL := 'WITH 
                   '|| MI_CONVENIOSPER ||'
                    SELECT   SP_USUARIO.CODIGORUTA 
                            ,SP_USUARIO.CICLO 
                            ,SP_USUARIO.CODIGOINTERNO 
                            ,SP_USUARIO.FACTURA 
                            ,SP_USUARIO.TOTFACTURAPERACTUAL 
                            ,NVL(CONVENIOSPER.TOTAL,0) CONVENIOS 
                            ,0 TERCERIZA 
                            ,SP_USUARIO.ANO 
                            ,SP_USUARIO.PERIODO 
                            ,SP_USUARIO.NIT 
                            ,SP_USUARIO.FECHALIMITE
                    FROM     SP_USUARIO 
                      LEFT JOIN CONVENIOSPER 
                        ON      SP_USUARIO.COMPANIA   = CONVENIOSPER.COMPANIA 
                       AND      SP_USUARIO.CICLO      = CONVENIOSPER.CICLO 
                       AND      SP_USUARIO.CODIGORUTA = CONVENIOSPER.CODIGORUTA  
                    '||MI_STRWHERE||' 
                      AND    SP_USUARIO.TOTFACTURAPERACTUAL
                           + NVL(CONVENIOSPER.TOTAL, 0) > 0
                    ORDER BY SP_USUARIO.CICLO 
                            ,SP_USUARIO.CODIGORUTA';
    ELSE
      MI_STRSQL := 'SELECT   SP_USUARIO.CODIGORUTA 
                            ,SP_USUARIO.CICLO 
                            ,SP_USUARIO.CODIGOINTERNO 
                            ,SP_USUARIO.FACTURA 
                            ,SP_USUARIO.TOTFACTURAPERACTUAL 
                            ,0 CONVENIOS 
                            ,0 TERCERIZA
                            ,SP_USUARIO.ANO 
                            ,SP_USUARIO.PERIODO 
                            ,SP_USUARIO.NIT 
                            ,SP_USUARIO.FECHALIMITE
                    FROM     SP_USUARIO
                    '||MI_STRWHERE||'
                      AND    SP_USUARIO.TOTFACTURAPERACTUAL > 0
                    ORDER BY SP_USUARIO.CICLO 
                            ,SP_USUARIO.CODIGORUTA';
    END IF;
    -- ASIGNACION DEL QUERY FINAL EN LA VARIABLE QUE SE VA A RETORNAR DE LA FUNCION
    MI_SQLFINAL := MI_STRSQL;
    RETURN MI_SQLFINAL;
  END FC_ARMACONSULTAPLANOSEXP;

  -- 7
  FUNCTION FC_PLANOS_ASOBANCARIA
    /*
      NAME              : FC_PLANOS_ASOBANCARIA --> EN ACCESS ASOBANCARIAEXP
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : YEISSON ALEJANDRO ROJAS RUIZ
      DATE MIGRADOR     : 26/12/2016
      TIME              : 14:09 PM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MODIFIED     : 10/01/2017
      TIME              : 11:00 AM
      DESCRIPTION       : FUNCION QUE RETORNA UNA CADENA CON LA CABECERA, EL CUERPO Y LOS TOTALES DE 
      PARAMETERS        : UN_COMPANIA      => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_CICLO         => CICLO POR EL CUAL SE VA A FILTRAR LA INFORMACION.
                          UN_CODIGOINICIAL => CODIGO INICIAL PARA LA CONSULTA DE LOS REGISTROS.
                          UN_CODIGOFINAL   => CODIGO FINAL PARA LA CONSULTA DE LOS REGISTROS.
                          UN_USUARIO       => USUARIO ACTUAL.
                          UN_CHECKATH      => VALOR DE 1 O 0 QUE IDENTIFICA EL VALOR DEL CHECK DE LA INTERFAZ GRAFICA.
      MODIFICATIONS     : CORRECCION SEGUN ESTANDAR DE PROGRAMACION Y OPTIMIZACION DE MANEJO DE ERRORES.
      @NAME:  enviarPlanosAsobancaria
      @METHOD:  GET
    */
  (
    UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_CICLO                IN PCK_SUBTIPOS.TI_CICLO
   ,UN_CODIGOINICIAL        IN PCK_SUBTIPOS.TI_CODIGORUTA
   ,UN_CODIGOFINAL          IN PCK_SUBTIPOS.TI_CODIGORUTA
   ,UN_USUARIO              IN PCK_SUBTIPOS.TI_USUARIO
   ,UN_CHECKATH             IN PCK_SUBTIPOS.TI_LOGICO
  )
    RETURN CLOB
  AS
    MI_STRSQL               PCK_SUBTIPOS.TI_STRSQL;
    MI_CODIGOINICIAL        PCK_SUBTIPOS.TI_CODIGORUTA;
    MI_CODIGOFINAL          PCK_SUBTIPOS.TI_CODIGORUTA; 
    MI_CONSECUTIVO          PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_TOTALES              PCK_SUBTIPOS.TI_DOBLE;
    MI_VALOR                PCK_SUBTIPOS.TI_DOBLE;
    MI_VALORCONVENIOS       PCK_SUBTIPOS.TI_DOBLE;
    MI_VALORTERCERIZA       PCK_SUBTIPOS.TI_DOBLE;
    MI_STRDETALLEPLANO      CLOB; 
    MI_FECHALIMITE1         SP_PARAMETROFACTURACION.FECHALIMITE1%TYPE;
    MI_FECHALIMITE2         SP_PARAMETROFACTURACION.FECHALIMITE2%TYPE; 
    MI_FECHAEXPEDICION      SP_PARAMETROFACTURACION.FECHAEXPEDICION%TYPE;
    MI_RSUSUARIO            SYS_REFCURSOR;
    MI_CONSECUTIVOINTERNO   NUMBER(38,0); -- NUMBER DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_SALIDA               CLOB; 
    MI_TABLAINSERT          PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOSINSERT         PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORESINSERT        PCK_SUBTIPOS.TI_VALORES;
    MI_CODIGORUTA           PCK_SUBTIPOS.TI_CODIGORUTA;
    MI_CICLO                PCK_SUBTIPOS.TI_CICLO;
    MI_CODIGOINTERNO        SP_USUARIO.CODIGOINTERNO%TYPE;
    MI_FACTURA              SP_USUARIO.FACTURA%TYPE;
    MI_TOTFACTURAPERACTUAL  SP_USUARIO.TOTFACTURAPERACTUAL%TYPE; 
    MI_CONVENIOS            NUMBER (10,0); -- NUMBER DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_TERCERIZA            NUMBER (10,0); -- NUMBER DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_ANIO                 PCK_SUBTIPOS.TI_ANIO;
    MI_PERIODO              PCK_SUBTIPOS.TI_PERIODO;
    MI_NIT                  SP_USUARIO.NIT%TYPE;
    MI_FECHALIMITE          SP_USUARIO.FECHALIMITE%TYPE;
    MI_CONTEO               NUMBER (10,0); -- NUMBER DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_J                    NUMBER (10,0); -- NUMBER DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_STRTOTALES           CLOB; 
  BEGIN
    MI_CODIGOINICIAL := UN_CODIGOINICIAL;
    MI_CODIGOFINAL := UN_CODIGOFINAL;
    BEGIN
      BEGIN
        MI_STRSQL := PCK_SERVICIOS_PUBLICOS_COM4.FC_ARMACONSULTAPLANOSEXP(UN_COMPANIA      => UN_COMPANIA, 
                                                                          UN_CICLO         => UN_CICLO, 
                                                                          UN_CODIGOINICIAL => MI_CODIGOINICIAL, 
                                                                          UN_CODIGOFINAL   => MI_CODIGOFINAL);
        EXECUTE IMMEDIATE 'SELECT COUNT(1) CONTEO 
                           FROM ('||MI_STRSQL||')' 
                           INTO MI_CONTEO;
        EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_CONTEO := 0;
      END;  

      BEGIN
        BEGIN
          SELECT   SP_PARAMETROFACTURACION.CICLO
                  ,SP_PARAMETROFACTURACION.FECHALIMITE1
                  ,SP_PARAMETROFACTURACION.FECHALIMITE2 
                  ,SP_PARAMETROFACTURACION.FECHAEXPEDICION
          INTO     MI_CICLO 
                  ,MI_FECHALIMITE1 
                  ,MI_FECHALIMITE2 
                  ,MI_FECHAEXPEDICION
          FROM     SP_PARAMETROFACTURACION
          WHERE    SP_PARAMETROFACTURACION.COMPANIA = UN_COMPANIA
            AND    SP_PARAMETROFACTURACION.CICLO    = UN_CICLO
            AND    ROWNUM                           < 2
          ORDER BY PCK_SERVICIOS_PUBLICOS.FC_NOMBREPERIODO(UN_COMPANIA   => UN_COMPANIA,
                                                           UN_ANO        => SP_PARAMETROFACTURACION.ANO,
                                                           UN_PERIODO    => SP_PARAMETROFACTURACION.PERIODO,
                                                           UN_FRECUENCIA => NULL) DESC;
          EXCEPTION WHEN NO_DATA_FOUND THEN 
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN    
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD   => SQLCODE,
            UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_ASOBANCARIA_CICLO
          );  
      END;
      MI_SALIDA := '';
      IF MI_CONTEO <> 0 THEN
        BEGIN  
          SELECT   MAX(TO_NUMBER(CONSECUTIVOINTERNO))
          INTO     MI_CONSECUTIVOINTERNO
          FROM     SP_REGISTROFACTURACIONHEADER
          WHERE    COMPANIA = UN_COMPANIA
          ORDER BY TO_NUMBER(CONSECUTIVOINTERNO) DESC;
          EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_CONSECUTIVOINTERNO := 0;
        END;
        IF MI_CONSECUTIVOINTERNO IS NOT NULL THEN
          MI_CONSECUTIVO := NVL(MI_CONSECUTIVOINTERNO, 0) + 1;
        ELSE
          MI_CONSECUTIVO := 0;
        END IF;
        BEGIN
          BEGIN
            MI_TABLAINSERT := 'SP_REGISTROFACTURACIONHEADER';
            MI_CAMPOSINSERT := 'TIPOREGISTRO
                               ,CODIGOEMPRESA
                               ,FECHAPRIMERVENCIMIENTO
                               ,FECHASEGUNDOVENCIMIENTO
                               ,FECHAFACTURACION
                               ,CICLO
                               ,FILLER
                               ,COMPANIA
                               ,CONSECUTIVOINTERNO
                               ,FECHA
                               ,HORA
                               ,USUARIO';
            MI_VALORESINSERT := '''01'',
                                '  || PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                            UN_NOMBRE    => 'CODIGO DE SERVICIO EAN',
                                                            UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                            UN_FECHA_PAR => SYSDATE)||',
                                '''|| TO_CHAR(MI_FECHALIMITE1,'YYYYMMDD') ||''',
                                '''|| TO_CHAR(MI_FECHALIMITE2,'YYYYMMDD') ||''',
                                '''|| TO_CHAR(MI_FECHAEXPEDICION,'YYYYMMDD')||''',
                                '  || MI_CICLO ||',
                                '''|| LPAD(' ',42,'0') ||''',
                                '''|| UN_COMPANIA ||''',
                                '  || MI_CONSECUTIVO ||',
                                '''|| TO_CHAR(SYSDATE,'DD/MM/YY') ||''',
                                '''|| TO_CHAR(SYSDATE,'HH24:MM') ||''',
                                '''|| UN_USUARIO ||'''';                
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLAINSERT,
                                          UN_ACCION  => 'I',
                                          UN_CAMPOS  => MI_CAMPOSINSERT,
                                          UN_VALORES => MI_VALORESINSERT);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD   => SQLCODE,
              UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_INSER_REGFACHEADER
            );
        END;
        -- CADENA QUE SIRVE COMO ENCABEZADO DEL ARCHIVO PLANO
        MI_SALIDA := '01'
                     ||PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                             UN_NOMBRE    => 'CODIGO DE SERVICIO EAN',
                                             UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                             UN_FECHA_PAR => SYSDATE)
                     ||TO_CHAR(MI_FECHALIMITE1,'YYYYMMDD')
                     ||TO_CHAR(MI_FECHALIMITE2,'YYYYMMDD')
                     ||TO_CHAR(MI_FECHAEXPEDICION,'YYYYMMDD')
                     ||PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => UN_CICLO,
                                                 UN_LONGITUD => 3)
                     ||LPAD(0,42,0)
                     ||CHR(10);  
      END IF;
      MI_TOTALES := 0;
      MI_J := 1;
      <<DETALLESFACTURACION>>
      OPEN MI_RSUSUARIO FOR MI_STRSQL;
        LOOP
          FETCH     MI_RSUSUARIO 
          INTO      MI_CODIGORUTA
                   ,MI_CICLO
                   ,MI_CODIGOINTERNO
                   ,MI_FACTURA
                   ,MI_TOTFACTURAPERACTUAL
                   ,MI_CONVENIOS
                   ,MI_TERCERIZA
                   ,MI_ANIO
                   ,MI_PERIODO
                   ,MI_NIT
                   ,MI_FECHALIMITE;
          EXIT WHEN MI_RSUSUARIO%NOTFOUND; 
          MI_J := MI_J + 1;    
          MI_VALORCONVENIOS := MI_CONVENIOS;
          MI_VALORTERCERIZA := MI_TERCERIZA;
          MI_VALOR := MI_TOTFACTURAPERACTUAL + MI_VALORCONVENIOS + MI_VALORTERCERIZA;
          MI_TABLAINSERT := 'SP_REGISTROFACTURACIONDETALLE';
          MI_CAMPOSINSERT := 'TIPOREGISTRO
                             ,CODIGOUSUARIO
                             ,NUMEROFACTURA
                             ,PERIODOSFACTURADOS
                             ,VALORSERVICIO
                             ,EMPRESAASOCIADA
                             ,VALORSERVICIOADICIONAL
                             ,FILLER
                             ,COMPANIA
                             ,CONSECUTIVOINTERNO
                             ,SECUENCIAINTERNA
                             ,CONCONVENIO
                             ,CONTERCERIZADO';
          BEGIN
            IF UN_CHECKATH <> 0 THEN
              MI_VALORESINSERT := '''02'',
                                  '  || LPAD(TRIM(TO_CHAR(MI_CODIGOINTERNO)),25) ||',
                                  '''|| RPAD(MI_FACTURA,12,' ') ||''',
                                  ''01'',
                                  '  || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_VALOR,
                                                                  UN_LONGITUD => LENGTH('0000000000000'))||',
                                  '''|| LPAD(' ',13) ||''',
                                  '  || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => 0,
                                                                  UN_LONGITUD => LENGTH('0000000000000'))||',
                                  '''|| LPAD(' ',4) ||''',
                                  '''|| UN_COMPANIA ||''',
                                  '  || MI_CONSECUTIVO ||',
                                  '  || MI_J ||',
                                  '  || MI_VALORCONVENIOS ||',
                                  '  || MI_VALORTERCERIZA||'';
              BEGIN              
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLAINSERT,
                                                      UN_ACCION  => 'I',
                                                      UN_CAMPOS  => MI_CAMPOSINSERT,
                                                      UN_VALORES => MI_VALORESINSERT);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
              END;
              -- CADENA CON LA INFORMACION CONCATENADA DE CADA UNO DE LOS DETALLES DEL ARCHIVO PLANO A GENERAR                       
              MI_STRDETALLEPLANO := '02'
                                    || LPAD(TRIM(TO_CHAR(MI_CODIGOINTERNO)),25,'0')
                                    || RPAD('0',12,'0')
                                    || '01'
                                    || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_VALOR,
                                                                 UN_LONGITUD => LENGTH('0000000000000'))
                                    || LPAD('0',13,'0')
                                    || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => 0,
                                                                 UN_LONGITUD => LENGTH('0000000000000'))
                                    || LPAD('0',4,'0')
                                    || CHR(10);
              -- ASIGNACION DE LOS DETALLES EN LA CADENA FINAL QUE SE RETORNA
              MI_SALIDA := MI_SALIDA||MI_STRDETALLEPLANO;
            ELSE
              MI_VALORESINSERT := '''02'',
                                  '  || LPAD(TRIM(TO_CHAR(MI_CODIGOINTERNO)),25) ||',
                                  '''|| RPAD(MI_FACTURA,12,' ') ||''', 
                                  ''01'',
                                  '  || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_VALOR,
                                                                  UN_LONGITUD => LENGTH('0000000000000'))||',
                                  '''|| LPAD(' ',13) ||''',
                                  '  || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => 0,
                                                                  UN_LONGITUD => LENGTH('0000000000000'))||',
                                  '''|| LPAD(' ',4) ||''',
                                  '''|| UN_COMPANIA ||''',
                                  '  || MI_CONSECUTIVO ||',
                                  '  || MI_J ||',
                                  '  || MI_VALORCONVENIOS ||',
                                  '  || MI_VALORTERCERIZA ||'';
              BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLAINSERT,
                                                      UN_ACCION  => 'I',
                                                      UN_CAMPOS  => MI_CAMPOSINSERT,
                                                      UN_VALORES => MI_VALORESINSERT);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
              END;-- CADENA CON LA INFORMACION CONCATENADA DE CADA UNO DE LOS DETALLES DEL ARCHIVO PLANO A GENERAR                    
              MI_STRDETALLEPLANO := '02'
                                        || LPAD(TRIM(TO_CHAR(MI_CODIGOINTERNO)),25,'0')
                                        || RPAD(MI_FACTURA,12,'0')
                                        || '01'
                                        || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_VALOR,
                                                                     UN_LONGITUD => LENGTH('0000000000000'))
                                        || LPAD('0',13,'0')
                                        || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => 0,
                                                                     UN_LONGITUD => LENGTH('0000000000000'))
                                        || LPAD('0',4,'0')
                                        || CHR(10);

                  -- ASIGNACION DE LOS DETALLES EN LA CADENA FINAL QUE SE RETORNA
                  MI_SALIDA := MI_SALIDA||MI_STRDETALLEPLANO;
            END IF;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD   => SQLCODE,
                UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_INSER_REGFACDETALL
              );
          END;
          MI_TOTALES := MI_TOTALES + MI_VALOR;
        END LOOP DETALLESFACTURACION;
      CLOSE MI_RSUSUARIO;
      BEGIN
        BEGIN
          MI_TABLAINSERT := 'SP_REGISTROFACTURACIONTOTALES';
          MI_CAMPOSINSERT := 'TIPOREGISTRO
                             ,TOTALREGISTROS
                             ,VALORTOTAL
                             ,VALORADICIONAL
                             ,FILLER
                             ,COMPANIA
                             ,CONSECUTIVOINTERNO';
          MI_VALORESINSERT := '''03'',
                              '  || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_J,
                                                              UN_LONGITUD => 9) ||',
                              '  || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_TOTALES,
                                                              UN_LONGITUD => 18) ||',
                              '  || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => 0,
                                                              UN_LONGITUD => 18)||',
                              '  || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => 0,
                                                              UN_LONGITUD => 18)
                                 ||PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => 0,
                                                             UN_LONGITUD => 19)||',
                              '''|| UN_COMPANIA ||''',
                              '  || MI_CONSECUTIVO ||'';
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLAINSERT,
                                        UN_ACCION  => 'I',
                                        UN_CAMPOS  => MI_CAMPOSINSERT,
                                        UN_VALORES => MI_VALORESINSERT);
          EXCEPTION WHEN PCK_EXCEPCIONES. EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD   => SQLCODE,
            UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_INSER_REGFACTOTALS
          );
      END;
      -- ASIGNACION DE LOS VALORES TOTALES A LA CADENA FINAL QUE SE EXPORTARA
      MI_STRTOTALES :=  MI_STRDETALLEPLANO
                        || '03'
                        || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_J,
                                                     UN_LONGITUD => 9)
                        || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_TOTALES,
                                                     UN_LONGITUD => 18)
                        || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => 0,
                                                     UN_LONGITUD => 18)
                        || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => 0,
                                                     UN_LONGITUD => 18)
                        || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => 0,
                                                     UN_LONGITUD => 19);
      MI_SALIDA := MI_SALIDA||MI_STRTOTALES;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
    END;
    -- ASIGNACION DEL QUERY FINAL EN LA VARIABLE QUE SE VA A RETORNAR DE LA FUNCION
    RETURN MI_SALIDA;   
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD   => SQLCODE,
        UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_ASOBANCARIA_GENE
      );
  END FC_PLANOS_ASOBANCARIA;

  -- 8
  FUNCTION FC_FIMMCABEZA
    /*
      NAME              : FC_FIMMCABEZA --> EN ACCESS EnviarFIMM
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICENO
      DATE MIGRADOR     : 26/12/2016
      TIME              : 11:30 AM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MODIFIED     : 10/01/2017
      TIME              : 02:05 PM
      DESCRIPTION       : FUNCION QUE RETORNA UNA CADENA CON EL TEXTO CORRESPONDIENTE AL ARCHIVO CABEZA QUE SE QUIERE EXPORTAR.
      PARAMETERS        : UN_COMPANIA         => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_CICLO            => CICLO POR EL CUAL SE VA A FILTRAR LA INFORMACION.
                          UN_STRDIASAFACTURAR => NUMERO DE DIAS A FACTURAR.
                          UN_DIASDESDE        => NUMERO DEL DIA INICIAL.
                          UN_DIASHASTA        => NUMERO DEL DIA FINAL.
                          UN_CODIGOINICIAL    => CODIGO INICIAL PARA LA CONSULTA DE LOS REGISTROS.
                          UN_CODIGOFINAL      => CODIGO FINAL PARA LA CONSULTA DE LOS REGISTROS.
                          UN_FECHAEMISION     => FECHA DE EMISION DE LA FACTURA. 
                          UN_USUARIO          => USUARIO QUE ESTA TRABAJANDO.
      MODIFICATIONS     : CORRECCION SEGUN ESTANDAR DE PROGRAMACION, REFERENCIACION DE FUNCIONES Y OPTIMIZACION 
                          DE MANEJO DE ERRORES.
      @NAME:  enviarFimmCabeza
      @METHOD:  GET
    */
  (
    UN_COMPANIA              IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_CICLO                 IN PCK_SUBTIPOS.TI_CICLO
   ,UN_STRDIASAFACTURAR      IN PCK_SUBTIPOS.TI_ENTERO
   ,UN_DIASDESDE             IN PCK_SUBTIPOS.TI_ENTERO
   ,UN_DIASHASTA             IN PCK_SUBTIPOS.TI_ENTERO
   ,UN_CODIGOINICIAL         IN PCK_SUBTIPOS.TI_CODIGORUTA
   ,UN_CODIGOFINAL           IN PCK_SUBTIPOS.TI_CODIGORUTA
   ,UN_FECHAEMISION          IN DATE 
   ,UN_USUARIO               IN PCK_SUBTIPOS.TI_USUARIO
  )
    RETURN CLOB
  AS
    MI_P1               PCK_SUBTIPOS.TI_ENTERO;
    MI_P2               VARCHAR2(30 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_P3               VARCHAR2(30 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_STRESTADO        VARCHAR2(1 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_ANIO             PCK_SUBTIPOS.TI_ANIO; 
    MI_PERIODO          PCK_SUBTIPOS.TI_PERIODO;
    MI_PARFUNCION       VARCHAR2(800 CHAR);  -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_INTC             PCK_SUBTIPOS.TI_ENTERO;
    MI_DESCON           VARCHAR2(3000 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_CNFIMM           PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_STRTEMP          CLOB; 
    MI_CAD              CLOB;
    MI_CAD1             CLOB;
    MI_CAR              VARCHAR2(1 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
  BEGIN
    MI_PARFUNCION := 'COMPANIA = '''       ||UN_COMPANIA||
                     ''', CICLO = '        ||UN_CICLO||
                     ', CODIGOINICIAL = '''||UN_CODIGOINICIAL||
                     ''', CODIGOFINAL = '''||UN_CODIGOFINAL||
                     ''', FECHAEMISION = ' ||UN_FECHAEMISION||'';
    MI_PARFUNCION := REPLACE(MI_PARFUNCION, '''', '*');
    MI_STRESTADO := 'C';
    MI_P1 := UN_STRDIASAFACTURAR;
    MI_P2 := UN_DIASDESDE;
    MI_P3 := UN_DIASHASTA;
    -- SE OBTIENE EL ANIO, PERIODO Y CICLO DE SP_USUARIO 
    BEGIN
      BEGIN
        SELECT   ANO
                ,PERIODO
        INTO     MI_ANIO
                ,MI_PERIODO
        FROM     SP_USUARIO 
        WHERE    COMPANIA                  = UN_COMPANIA
          AND    CICLO                     = UN_CICLO
          AND    CODIGORUTA                BETWEEN UN_CODIGOINICIAL AND UN_CODIGOFINAL 
          AND    NVL(BANCOPERPROCESO,' ')  = ' ' 
          AND    NVL(PERIODOSNOCOBROFAC,0) = 0 
          AND    ESTADO                    IN ('A')
          AND    ROWNUM                    = 1;
        EXCEPTION WHEN NO_DATA_FOUND THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      -- SE LANZA UNA EXCEPCION EN CASO DE NO OBTENER DATOS, EN LA QUE SE REALIZA LA
      -- INSERCION DEL PROCESO REALIZADO, POR MEDIO DEL PROCESO PR_REGISTRARPROCESO 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_STRESTADO := 'I';
        PCK_SERVICIOS_PUBLICOS_COM4.PR_REGISTRARPROCESO(UN_COMPANIA    => UN_COMPANIA, 
                                                        UN_PROCESO     => 'PFIMME', 
                                                        UN_DESCRIPCION => 'EXPORTACION PLANOS' || SYSDATE, 
                                                        UN_PARAMETROS  => MI_PARFUNCION, 
                                                        UN_RESULTADOS  => PCK_ERRORES.ERR_FACSERP_FIMM_USUARIO, 
                                                        UN_ESTADO      => MI_STRESTADO, 
                                                        UN_USUARIO     => UN_USUARIO, 
                                                        UN_FECHA       => SYSDATE);
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD   => SQLCODE,
          UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_FIMM_USUARIO
        );
    END;
    MI_INTC := 1;
    MI_DESCON := '';
    -- SE REGISTRAN CODIGO Y NOMBRE DE SP_CONCEPTOS
    <<CONCEPTOS>>
    FOR MI_RSCONCEPTOS IN (
      SELECT   CODIGO
              ,NOMBRE    
      FROM     SP_CONCEPTOS 
      WHERE    COMPANIA           = UN_COMPANIA
        AND    (CODIGO            BETWEEN 1 AND 50 
         OR    CODIGO             BETWEEN 246 AND 250) 
        AND    SUBSTR(NOMBRE,1,8) <> 'CONCEPTO' 
      ORDER BY CODIGO) 
    LOOP
      MI_CNFIMM(MI_RSCONCEPTOS.CODIGO).CLAVE := MI_RSCONCEPTOS.NOMBRE;
       MI_CNFIMM(MI_RSCONCEPTOS.CODIGO).VALOR := MI_INTC;
      MI_INTC := MI_INTC + 1;
    END LOOP CONCEPTOS;
    -- SE CONCATENA EL NOMBRE DE SP_CONCEPTOS, TRAIDO DEL CLAVEVALOR MI_CNFIMM
    <<NOMBRECONCEPTOS>>
    FOR MI_INTC IN MI_CNFIMM.FIRST..MI_CNFIMM.LAST LOOP
      IF MI_CNFIMM.EXISTS(MI_INTC) THEN
        MI_DESCON := MI_DESCON || RPAD(NVL(MI_CNFIMM(MI_INTC).CLAVE, ' '),25);
      END IF;
    END LOOP NOMBRECONCEPTOS;
    -- SE ASIGNA CONTENIDO A LA VARIABLE QUE RETORNARA TODO EL TEXTO DEL ARCHIVO
    MI_STRTEMP := ''
                  || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_PERIODO,
                                               UN_LONGITUD => 2) 
                  || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => SUBSTR(TO_CHAR(MI_ANIO),3),
                                               UN_LONGITUD => 2)
                  || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => TO_NUMBER(UN_CICLO), 
                                               UN_LONGITUD => 3);
    MI_STRTEMP := RPAD(MI_STRTEMP,LENGTH(MI_STRTEMP)+6);  
    MI_STRTEMP := MI_STRTEMP || SUBSTR(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                             UN_NOMBRE    => 'FIMM - CODIGO DE GESTION',
                                                             UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                             UN_FECHA_PAR => SYSDATE), 1, 10);
    MI_STRTEMP := RPAD(MI_STRTEMP,LENGTH(MI_STRTEMP)+11);
    MI_STRTEMP := MI_STRTEMP || MI_PERIODO
                             || SUBSTR(TRIM(TO_CHAR(MI_ANIO)), 3)
                             || MI_PERIODO
                             || SUBSTR(TRIM(TO_CHAR(MI_ANIO)), 3);
    MI_STRTEMP := RPAD(MI_STRTEMP,LENGTH(MI_STRTEMP)+20);   
    MI_STRTEMP := MI_STRTEMP || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_P1, 
                                                          UN_LONGITUD => 3)
                             || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_P2, 
                                                          UN_LONGITUD => 3)
                             || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_P3, 
                                                          UN_LONGITUD => 3)
                             || TO_CHAR(SYSDATE, 'DD') 
                             || TO_CHAR(SYSDATE, 'MM') 
                             || TO_CHAR(SYSDATE, 'YY')
                             || SUBSTR(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                             UN_NOMBRE    => 'FIMM - FLAG DE INCIDENCIAS',
                                                             UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                             UN_FECHA_PAR => SYSDATE), 1, 40); 
    MI_STRTEMP := RPAD(MI_STRTEMP,LENGTH(MI_STRTEMP)+59);                                
    MI_CAD := ''
              || RPAD(MI_DESCON, 2475);   
    MI_CAD1 := '';
    -- SE CONTROLA QUE UNICAMENTE VAYAN NUMEROS DE 0 A 9 Y LETRAS DE A a Z 
    -- (TRATAMIENTO A CARACTERES ESPECIALES)
    <<CARACTERESESPECIALES>>
    FOR T IN 1..LENGTH(MI_CAD) LOOP
      MI_CAR := SUBSTR(MI_CAD, T, 1);
      MI_CAR := CASE 
                  WHEN ASCII(MI_CAR) < 32 THEN ' '
                  WHEN ASCII(MI_CAR) = 42 THEN ' '
                  WHEN ASCII(MI_CAR) = 130 THEN 'e'
                  WHEN ASCII(MI_CAR) = 160 THEN 'a'
                  WHEN ASCII(MI_CAR) = 161 THEN 'i'
                  WHEN ASCII(MI_CAR) = 162 THEN 'o'
                  WHEN ASCII(MI_CAR) = 163 THEN 'u'
                  WHEN ASCII(MI_CAR) = 164 THEN 'n'
                  WHEN ASCII(MI_CAR) = 165 THEN 'N'
                  WHEN ASCII(MI_CAR) > 122 THEN ' '
                  WHEN ASCII(MI_CAR) BETWEEN 58 AND 64 THEN ' '
                  WHEN ASCII(MI_CAR) BETWEEN 91 AND 96 THEN ' '
                  ELSE MI_CAR
                END;
      MI_CAD1 := MI_CAD1 || MI_CAR;
    END LOOP CARACTERESESPECIALES;
    MI_STRTEMP := MI_STRTEMP || MI_CAD1
                             || RPAD(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA    => UN_COMPANIA,
                                                             UN_NOMBRE    => 'FIMM - AREA DONDE SE IMPRIME',
                                                             UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                             UN_FECHA_PAR => SYSDATE), 43); 
    MI_STRTEMP := RPAD(MI_STRTEMP,LENGTH(MI_STRTEMP)+108);
    MI_STRTEMP := MI_STRTEMP || 'S'; 
    RETURN MI_STRTEMP;
  END FC_FIMMCABEZA;

  -- 9
  FUNCTION FC_FIMMCUERPO
    /*
      NAME              : FC_FIMMCUERPO --> EN ACCESS EnviarFIMM (Archivo detalles)
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICENO
      DATE MIGRADOR     : 30/12/2016
      TIME              : 10:25 AM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MODIFIED     : 10/01/2017
      TIME              : 02:35 PM
      DESCRIPTION       : FUNCION QUE RETORNA UNA CADENA CON EL TEXTO CORRESPONDIENTE AL ARCHIVO CUERPO, 
                          QUE CONTIENE LOS DETALLES DE LOS USUARIOS CON FACTURAS SIN CANCELAR, QUE SE QUIERE EXPORTAR.
      PARAMETERS        : UN_COMPANIA => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_CICLO => CICLO POR EL CUAL SE VA A FILTRAR LA INFORMACION.
                          UN_DIASPRIMERVENC => NUMERO DE DIAS QUE FALTAN PARA EL VENCIMIENTO.
                          UN_CODIGOINICIAL => CODIGO INICIAL PARA LA CONSULTA DE LOS REGISTROS.
                          UN_CODIGOFINAL => CODIGO FINAL PARA LA CONSULTA DE LOS REGISTROS.
                          UN_FECHAEMISION => FECHA DE EMISION DE LA FACTURA. 
                          UN_FECHAVENCIMIENTO => FECHA DE VENCIMIENTO DE LA FACTURA. 
                          UN_INDEMISIONFIJA => VALOR QUE TOMA EL INDICADOR DE SI LA FECHA DE EMISION ES FIJA.
                          UN_INDVENCIMIENTOFIJO => VALOR QUE TOMA EL INDICADOR DE SI LA FECHA DE VENCIMIENTO ES FIJA.
                          UN_USUARIO => USUARIO QUE ESTA TRABAJANDO.
      MODIFICATIONS     : CORRECCION SEGUN ESTANDAR DE PROGRAMACION, REFERENCIACION DE FUNCIONES Y OPTIMIZACION 
                          DE MANEJO DE ERRORES.
      MODIFIER          : SERGIO ESTEBAN PIÑA VARGAS
      DATE_MODIFIED     : 26/05/2017
      DESCRIPTION       : AGREGO ACTUALIZACION E INSERCION DE CAMPOS DE AUDITORIA CREATED_BY, MODIFIED_BY, DATE_CREATED, DATE_MODIFIED

      @NAME:  enviarFimmCuerpo
      @METHOD:  GET
    */
  (
    UN_COMPANIA              IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_CICLO                 IN PCK_SUBTIPOS.TI_CICLO
   ,UN_DIASPRIMERVENC        IN PCK_SUBTIPOS.TI_ENTERO
   ,UN_CODIGOINICIAL         IN PCK_SUBTIPOS.TI_CODIGORUTA
   ,UN_CODIGOFINAL           IN PCK_SUBTIPOS.TI_CODIGORUTA
   ,UN_FECHAEMISION          IN DATE 
   ,UN_FECHAVENCIMIENTO      IN DATE 
   ,UN_INDEMISIONFIJA        IN PCK_SUBTIPOS.TI_LOGICO
   ,UN_INDVENCIMIENTOFIJO    IN PCK_SUBTIPOS.TI_LOGICO
   ,UN_USUARIO               IN PCK_SUBTIPOS.TI_USUARIO
  )
    RETURN CLOB
  AS
    MI_RANGO                  PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_SC_CON                 NUMBER(30); -- NUMBER DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_P1                     DATE;
    MI_P2                     DATE; 
    MI_P3                     VARCHAR2(30 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_P4                     PCK_SUBTIPOS.TI_ENTERO;
    MI_H_BM                   VARCHAR2(100 CHAR);  -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_XANO                   PCK_SUBTIPOS.TI_ANIO;        
    MI_XMES                   PCK_SUBTIPOS.TI_PERIODO;
    MI_DBLNUMFACTURA          PCK_SUBTIPOS.TI_DOBLE;
    MI_STRPLANO               VARCHAR2(200 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_STRESTADO              VARCHAR2(1 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_BLSELLOFIMM            PCK_SUBTIPOS.TI_LOGICO;
    MI_RENGLONMENSAJE         PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CONSECUTIVO            PCK_SUBTIPOS.TI_ENTERO;
    MI_TABLAINSERT            PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOSINSERT           PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORESINSERT          PCK_SUBTIPOS.TI_VALORES;
    MI_INDSELLOFIMM           PCK_SUBTIPOS.TI_ENTERO;
    MI_ANIO                   PCK_SUBTIPOS.TI_ANIO; 
    MI_PERIODO                PCK_SUBTIPOS.TI_PERIODO;
    MI_PER_HIS                VARCHAR2(800 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_PER_HIS1               PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_PARFUNCION             VARCHAR2(800 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_INTC                   PCK_SUBTIPOS.TI_ENTERO;
    MI_CNFIMM                 PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CAD                    CLOB; 
    MI_CAD1                   CLOB; 
    MI_CAR                    VARCHAR2(10 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_FIN                    VARCHAR2(1000 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_S_F1                   PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_NC                     PCK_SUBTIPOS.TI_DOBLE;
    MI_SALDOS                 VARCHAR2(1000 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_CONS                   VARCHAR2(1000 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_RECS                   VARCHAR2(1000 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_FACS                   VARCHAR2(1000 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_SUMA                   PCK_SUBTIPOS.TI_DOBLE;
    MI_STRTAR                 CLOB; 
    MI_SUBFIJO                PCK_SUBTIPOS.TI_ENTERO;
    MI_SUBBASICO              PCK_SUBTIPOS.TI_ENTERO;
    MI_SUBALCFIJO             PCK_SUBTIPOS.TI_ENTERO;
    MI_SUBALCBASICO           PCK_SUBTIPOS.TI_ENTERO;
    MI_SOBREFIJO              PCK_SUBTIPOS.TI_ENTERO;
    MI_SOBREBASICO            PCK_SUBTIPOS.TI_ENTERO;
    MI_SOBREALCBASICO         PCK_SUBTIPOS.TI_ENTERO;
    MI_SUBCOMPLEMENTARIO      PCK_SUBTIPOS.TI_ENTERO;
    MI_SUBALCCOMPLEMENTARIO   PCK_SUBTIPOS.TI_ENTERO;
    MI_SOBRECOMPLEMENTARIO    PCK_SUBTIPOS.TI_ENTERO;
    MI_SUBALCSUNTUARIO        PCK_SUBTIPOS.TI_ENTERO;
    MI_SUBSUNTUARIO           PCK_SUBTIPOS.TI_ENTERO;
    MI_SOBRESUNTUARIO         PCK_SUBTIPOS.TI_ENTERO;
    MI_SOBREALCFIJO           PCK_SUBTIPOS.TI_ENTERO;
    MI_SOBREALCCOMPLEMENTARIO PCK_SUBTIPOS.TI_ENTERO;
    MI_SOBREALCSUNTUARIO      PCK_SUBTIPOS.TI_ENTERO;
    MI_CONT                   PCK_SUBTIPOS.TI_ENTERO;
    MI_TABLAUPDATE            PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOSUPDATE           PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICIONUPDATE        PCK_SUBTIPOS.TI_CONDICION;
    MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR; 
  BEGIN
    MI_PARFUNCION := 'COMPANIA = '''||UN_COMPANIA||
                     ''', CICLO = '||UN_CICLO||
                     ', CODIGOINICIAL = '''||UN_CODIGOINICIAL||
                     ''', CODIGOFINAL = '''||UN_CODIGOFINAL||
                     ''', FECHAEMISION = '||UN_FECHAEMISION||'';
    MI_PARFUNCION := REPLACE(MI_PARFUNCION, '''', '*');
    --NUMERO DE CONCEPTOS
    MI_SC_CON := 35; 
    MI_STRESTADO := 'C';
    <<LIMITERANGO>>
    FOR MI_RSRANGOS IN (
      SELECT   CODIGO
              ,LIMITESUPERIOR
      FROM     SP_RANGO 
      WHERE    COMPANIA = UN_COMPANIA
        AND    ROWNUM   < 3)
    LOOP
      MI_RANGO(TO_NUMBER(MI_RSRANGOS.CODIGO)).CLAVE := 'LIMITESUPERIOR'||MI_RSRANGOS.CODIGO;
      MI_RANGO(TO_NUMBER(MI_RSRANGOS.CODIGO)).VALOR := MI_RSRANGOS.LIMITESUPERIOR; 
    END LOOP LIMITERANGO;
    BEGIN
      SELECT   CONSECUTIVO
      INTO     MI_CONSECUTIVO
      FROM     SP_FACTURAS 
      WHERE    CICLO    = UN_CICLO 
        AND    COMPANIA = UN_COMPANIA 
      ORDER BY CONSECUTIVO DESC;
      -- SE LANZA UNA EXCEPCION EN CASO DE NO OBTENER DATOS
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_DBLNUMFACTURA := 1.0;
        BEGIN
          BEGIN
            --insertar facturas
            MI_TABLAINSERT:='SP_FACTURAS';
            MI_CAMPOSINSERT:='COMPANIA
                             ,CICLO
                             ,CONSECUTIVO
                             ,CREATED_BY
                             ,DATE_CREATED';
            MI_VALORESINSERT:='''' ||UN_COMPANIA||''','||UN_CICLO||','||1||','||UN_USUARIO||','||UN_FECHAEMISION||' ';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLAINSERT, 
                                                  UN_ACCION  => 'I',
                                                  UN_CAMPOS  => MI_CAMPOSINSERT,    
                                                  UN_VALORES => MI_VALORESINSERT);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD   => SQLCODE,
            UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_REGISTRAR_FACTURA,
            UN_TABLAERROR => 'SP_FACTURAS'
          );
      END;
    END;
    IF MI_CONSECUTIVO IS NOT NULL THEN
      MI_DBLNUMFACTURA := MI_CONSECUTIVO + 1;
      MI_BLSELLOFIMM := 0; 
    END IF;
    BEGIN
      SELECT   INDSELLOFIMM
      INTO     MI_INDSELLOFIMM
      FROM     SP_CICLO
      WHERE    COMPANIA = UN_COMPANIA 
        AND    NUMERO   = UN_CICLO;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_INDSELLOFIMM := NULL;
    END;
    MI_BLSELLOFIMM := NVL(MI_INDSELLOFIMM,0);
    MI_STRPLANO := ' ';
    MI_H_BM := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                     UN_NOMBRE    => 'FRECUENCIA PERIODOS DE FACTURACION',
                                     UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                     UN_FECHA_PAR => SYSDATE);
    MI_SC_CON := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                       UN_NOMBRE    => 'NUMERO MAXIMO CONCEPTOS',
                                       UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                       UN_FECHA_PAR => SYSDATE);
    <<MENSAJERENGLON>>
    FOR J IN 1..5 LOOP
      MI_RENGLONMENSAJE(J).CLAVE := 'RENGLON'||J;
      MI_RENGLONMENSAJE(J).VALOR := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                             UN_NOMBRE    => 'FIMM - RENGLON '|| J ||' MENSAJE USUARIO',
                                                             UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                             UN_FECHA_PAR => SYSDATE);
      IF SUBSTR(MI_RENGLONMENSAJE(J).VALOR,1,1) = '-' THEN
        MI_RENGLONMENSAJE(J).VALOR := ' ';
      END IF;
    END LOOP MENSAJERENGLON;
    IF MI_H_BM = ' ' Then
      MI_H_BM := 'M';
    END IF;
    -- SE OBTIENE EL ANIO Y PERIODO DE SP_USUARIO 
    BEGIN
      SELECT   PERIODO
              ,ANO
      INTO     MI_PERIODO
              ,MI_ANIO
      FROM     SP_USUARIO 
      WHERE    COMPANIA                  = UN_COMPANIA 
        AND    CICLO                     = UN_CICLO 
        AND    CODIGORUTA                BETWEEN UN_CODIGOINICIAL AND UN_CODIGOFINAL 
        AND    ESTADO                    IN ('A')
        AND    NVL(BANCOPERPROCESO,' ')  = ' ' 
        AND    NVL(PERIODOSNOCOBROFAC,0) = 0 
        AND    ROWNUM                    < 2
      ORDER BY COMPANIA,CICLO,CODIGORUTA;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_PERIODO := NULL;
        MI_ANIO := NULL;
    END;
    MI_XANO := MI_ANIO;
    MI_XMES := MI_PERIODO;
    MI_PER_HIS := '';
    <<HISTORIALPERDESC>>
    FOR I IN REVERSE 1..6 LOOP
      MI_XMES := MI_XMES - 1;
      IF MI_XMES <= 0 THEN
        MI_XANO := MI_XANO - 1;
        MI_XMES := PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => MI_H_BM = 'M',
                                         UN_SI        => 12,
                                         UN_NO        => PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => MI_H_BM = 'T',
                                                                               UN_SI        => 4,
                                                                               UN_NO        => 6));
      END IF;
      MI_PER_HIS1(I).CLAVE := 'MESANIO'|| I;
      MI_PER_HIS1(I).VALOR := RPAD(PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_XMES, 
                                                                UN_LONGITUD => 2)
                                      ||SUBSTR(PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_XANO,
                                                                         UN_LONGITUD => 4), 3, 2), 5);
    END LOOP HISTORIALPERDESC;
    <<HISTORIALPERASC>>
    FOR I IN 1..6 LOOP
      MI_PER_HIS := MI_PER_HIS || MI_PER_HIS1(I).VALOR;
    END LOOP HISTORIALPERASC;
    MI_P1 := UN_FECHAEMISION;
    MI_P2 := UN_FECHAVENCIMIENTO;
    MI_P3 := 'S';
    MI_P4 := UN_DIASPRIMERVENC;
    IF UN_INDVENCIMIENTOFIJO = 0 THEN
      MI_P2 := PCK_SYSMAN_UTL.FC_SUMARDIAS_FECHA(UN_FECHA => MI_P1,
                                                 UN_DIAS  => MI_P4);
    END IF;
    -- SE OBTIENE EL ANIO, PERIODO Y CICLO DE SP_USUARIO 
    BEGIN
      BEGIN
        SELECT   ANO
                ,PERIODO
        INTO     MI_ANIO
                ,MI_PERIODO
        FROM     SP_USUARIO 
        WHERE    COMPANIA                  = UN_COMPANIA
          AND    CODIGORUTA                BETWEEN UN_CODIGOINICIAL AND UN_CODIGOFINAL 
          AND    NVL(BANCOPERPROCESO,' ')  = ' ' 
          AND    NVL(PERIODOSNOCOBROFAC,0) = 0 
          AND    SP_USUARIO.ESTADO         IN ('A')
          AND    ROWNUM                    < 2;
        EXCEPTION WHEN NO_DATA_FOUND THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      -- SE LANZA UNA EXCEPCION EN CASO DE NO OBTENER DATOS, EN LA QUE SE REALIZA LA
      -- INSERCION DEL PROCESO REALIZADO, POR MEDIO DEL PROCESO PR_REGISTRARPROCESO 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_STRPLANO := 'REVISE EL CICLO DE LOS ARCHIVOS';
        MI_STRESTADO := 'I';
        PCK_SERVICIOS_PUBLICOS_COM4.PR_REGISTRARPROCESO(UN_COMPANIA    => UN_COMPANIA, 
                                                        UN_PROCESO     => 'PFIMME', 
                                                        UN_DESCRIPCION => 'EXPORTACION PLANOS' || SYSDATE, 
                                                        UN_PARAMETROS  => MI_PARFUNCION, 
                                                        UN_RESULTADOS  => PCK_ERRORES.ERR_FACSERP_FIMM_USUARIO, 
                                                        UN_ESTADO      => MI_STRESTADO, 
                                                        UN_USUARIO     => UN_USUARIO, 
                                                        UN_FECHA       => SYSDATE);
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD   => SQLCODE,
          UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_FIMM_USUARIO
        );
    END;
    MI_INTC := 1;
    <<CONCEPTOS>>
    FOR MI_RSCONCEPTOS IN (
      SELECT   CODIGO
              ,NOMBRE    
      FROM     SP_CONCEPTOS 
      WHERE    COMPANIA           = UN_COMPANIA
        AND    (CODIGO            BETWEEN 1 AND 50 
         OR    CODIGO             BETWEEN 246 AND 250) 
        AND    SUBSTR(NOMBRE,1,8) <> 'CONCEPTO' 
      ORDER BY CODIGO) 
    LOOP
      MI_CNFIMM(MI_RSCONCEPTOS.CODIGO).CLAVE := MI_RSCONCEPTOS.NOMBRE||MI_INTC;
      MI_CNFIMM(MI_RSCONCEPTOS.CODIGO).VALOR := MI_INTC;
      MI_INTC := MI_INTC + 1;
    END LOOP CONCEPTOS;
    <<TARIFASUSUA>>
    FOR MI_RS IN (
      SELECT   SP_USUARIO.PRIMERAPELLIDO||' '||
               SP_USUARIO.SEGUNDOAPELLIDO||' '||
               SP_USUARIO.NOMBRES NOMBRE
              ,SP_USUARIO.COMPANIA
              ,SP_USUARIO.CICLO
              ,SP_USUARIO.CODIGORUTA
              ,SP_USUARIO.ANO
              ,SP_USUARIO.PERIODO
              ,SP_USUARIO.CODIGOINTERNO
              ,SP_USUARIO.DIRTECNICA
              ,SP_USUARIO.DIRGUIA
              ,SP_USUARIO.TELEFONO
              ,SP_USUARIO.NIT
              ,SP_USUARIO.USO
              ,SP_USUARIO.ESTRATO
              ,SP_USUARIO.ACUEDUCTO
              ,SP_USUARIO.ASEO
              ,SP_USUARIO.ALCANTARILLADO
              ,SP_USUARIO.PORCENTAJEAPLICAR
              ,SP_USUARIO.PESOASEO
              ,SP_USUARIO.FRECUENCIAASEOSEMANA
              ,SP_USUARIO.MEDIDOR
              ,SP_USUARIO.NUMERODIGITOS
              ,SP_USUARIO.LECTURA1
              ,SP_USUARIO.LECTURA2
              ,SP_USUARIO.LECTURA
              ,SP_USUARIO.FECHALECANTERIOR
              ,SP_USUARIO.PERIODOSATRASO
              ,SP_USUARIO.BANCOPERPROCESO
              ,SP_USUARIO.CONSUMO1
              ,SP_USUARIO.CONSUMO2
              ,SP_USUARIO.CONSUMO3
              ,SP_USUARIO.CONSUMO4
              ,SP_USUARIO.CONSUMO5
              ,SP_USUARIO.CONSUMO6
              ,SP_USUARIO.CONSUMOPROM
              ,SP_USUARIO.PERIODOSNOCOBROFAC
              ,SP_USUARIO.FACTURA
              ,SP_USUARIO.ACUMULADO
              ,SP_USUARIO.SELLOFIMM
              ,SP_USUARIO.FECHALIMITE
              ,SP_TARIFAS.CONSUMOBASICO CONSUMOBASICO
              ,SP_TARIFAS.TARIFABASICO TARIFABASICO
              ,SP_TARIFAS.TARIFACOMPLEMENTARIO TARIFACOMPLEMENTARIO
              ,SP_TARIFAS.CONSUMOCOMPLEMENTARIO CONSUMOCOMPLEMENTARIO
              ,SP_TARIFAS.CONSUMOSUNTUARIO CONSUMOSUNTUARIO
              ,SP_TARIFAS.TARIFASUNTUARIO TARIFASUNTUARIO
              ,SP_TARIFAS.ALCSUNTUARIO ALCSUNTUARIO
              ,SP_TARIFAS.ALCCOMPLEMENTARIO ALCCOMPLEMENTARIO
              ,SP_TARIFAS.ALCBASICO ALCBASICO
      FROM     SP_USUARIO 
        LEFT JOIN SP_TARIFAS
          ON   SP_USUARIO.ESTRATO   = SP_TARIFAS.ESTRATO
         AND   SP_USUARIO.USO       = SP_TARIFAS.USO
         AND   SP_USUARIO.PERIODO   = SP_TARIFAS.PERIODO
         AND   SP_USUARIO.ANO       = SP_TARIFAS.ANO
         AND   SP_USUARIO.COMPANIA  = SP_TARIFAS.COMPANIA
      WHERE    SP_USUARIO.COMPANIA                   = UN_COMPANIA
        AND    SP_USUARIO.CICLO                      = UN_CICLO
        AND    SP_USUARIO.CODIGORUTA                 BETWEEN UN_CODIGOINICIAL AND UN_CODIGOFINAL
        AND    SP_USUARIO.ESTADO                     IN ('A')
        AND    NVL(SP_USUARIO.BANCOPERPROCESO,' ')   = ' '
        AND    NVL(SP_USUARIO.PERIODOSNOCOBROFAC,0)  = 0
      ORDER BY SP_USUARIO.COMPANIA
              ,SP_USUARIO.CICLO
              ,SP_USUARIO.CODIGORUTA) 
    LOOP
      MI_CAD := RPAD(MI_RS.CODIGORUTA,14) || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => NVL(MI_RS.CODIGOINTERNO, ' '), 
                                                                       UN_LONGITUD => 8);
      IF MI_BLSELLOFIMM <> 0 THEN
        MI_CAD := MI_CAD || RPAD(NVL(MI_RS.SELLOFIMM, ''), 10); 
      ELSE
      -- NUMERO MEDIDOR
        MI_CAD := MI_CAD || RPAD(NVL(MI_RS.MEDIDOR, ''), 10);
      END IF;
      MI_CAD := MI_CAD || RPAD(MI_RS.NOMBRE, 32)
                       || RPAD(NVL(MI_RS.DIRGUIA, ''), 18)
                       || RPAD(NVL(MI_RS.DIRTECNICA,''), 30)
                       || RPAD(NVL(MI_RS.TELEFONO, 0), 7)
                       || RPAD(PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => NVL(MI_RS.NIT, '0') = '0',
                                                     UN_SI        => ' ',
                                                     UN_NO        => MI_RS.NIT), 12)
                       || RPAD(TO_NUMBER(NVL(MI_RS.USO, '1')), 1)
                       || RPAD(TO_NUMBER(NVL(MI_RS.ESTRATO, '1')), 1)
                       || RPAD(NVL(MI_RS.FRECUENCIAASEOSEMANA, 0), 1)
                       || RPAD(TRIM(TO_CHAR(NVL(MI_RS.PESOASEO, 0) * 1000, '0000000.00')), 10, '0')
                       || PCK_SERVICIOS_PUBLICOS_COM4.FC_NOMBREPER(UN_COMPANIA   => UN_COMPANIA,
                                                                   UN_ANO        => MI_RS.ANO,
                                                                   UN_PERIODO    => MI_RS.PERIODO,
                                                                   UN_FRECUENCIA => PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                                                                          UN_NOMBRE    => 'FRECUENCIA PERIODOS DE FACTURACION',
                                                                                                          UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                                                                          UN_FECHA_PAR => SYSDATE))
                       || 'N'
                       || 'S'
                       || RPAD(MI_P3, 1);
      -- MENSAJE AL LECTOR
      IF MI_RS.CONSUMO1 > 60 THEN
        MI_CAD := MI_CAD || RPAD('VERIFIQUE CONSUMO', 16);
      ELSIF MI_RS.LECTURA1 = MI_RS.LECTURA2 AND MI_RS.LECTURA2 <> 0 THEN
        MI_CAD := MI_CAD || RPAD('REV MED DETENIDO', 16);
      ELSIF MI_RS.LECTURA1 < MI_RS.LECTURA2 THEN
        MI_CAD := MI_CAD || RPAD('REV MED INVERTIDO', 16);
      ELSE
        MI_CAD := MI_CAD || RPAD(' ', 16);
      END IF;
      IF MI_RS.PERIODOSATRASO >= 2 THEN
        MI_CAD := MI_CAD || RPAD(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                       UN_NOMBRE    => 'FIMM MENSAJE SUSPENSION',
                                                       UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                       UN_FECHA_PAR => SYSDATE), 75);
      ELSE
        MI_CAD := MI_CAD || RPAD(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                       UN_NOMBRE    => 'FIMM - OBSERVACIONES',
                                                       UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                       UN_FECHA_PAR => SYSDATE), 75);
      END IF;
      MI_CAD := MI_CAD || MI_RENGLONMENSAJE(1).VALOR 
                       || MI_RENGLONMENSAJE(2).VALOR 
                       || MI_RENGLONMENSAJE(3).VALOR
                       || MI_RENGLONMENSAJE(4).VALOR
                       || MI_RENGLONMENSAJE(5).VALOR;
      MI_FIN := RPAD(' ',4);
      MI_SALDOS := ' ';
      MI_CONT := 1;
      <<CUOTAS>>
      FOR MI_RSFINAN IN (
        SELECT   NUMEROCUOTAS
                ,SALDOFINANCIABLE
                ,NROCUOTA
        FROM     SP_FINANCIABLES 
        WHERE    COMPANIA   = UN_COMPANIA 
          AND    CICLO      = UN_CICLO 
          AND    CODIGORUTA = MI_RS.CODIGORUTA 
          AND    ANO        = MI_RS.ANO
          AND    PERIODO    = MI_RS.PERIODO 
        ORDER BY NUMEROCUOTAS) 
      LOOP
        MI_SALDOS := MI_SALDOS || LPAD(NVL(SUBSTR(NVL(MI_RSFINAN.SALDOFINANCIABLE, 0), MI_CONT * 9 - 8,9),0), 5, '0');
        MI_CONT := MI_CONT + 1;
        MI_NC := MI_RSFINAN.NUMEROCUOTAS;
        -- SE DEJA LA ULTIMA CUOTA DEL ULTIMO CONCEPTO
        -- YA QUE FIMM AGUANTA 4 DIGITOS. EJ: CUOTA 02 DE 04
        MI_FIN := MI_FIN || TO_CHAR(MI_RSFINAN.NROCUOTA,'00') || TO_CHAR(MI_NC,'00');
      END LOOP CUOTAS;
      <<ESPSALDOS>>
      FOR I IN 1..11 LOOP
        MI_SALDOS := MI_SALDOS||'     ';
      END LOOP ESPSALDOS;
      MI_CAD := MI_CAD || MI_FIN 
                       || RPAD(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                     UN_NOMBRE    => 'FIMM - LUGARES DE PAGO',
                                                     UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                     UN_FECHA_PAR => SYSDATE), 20);
      MI_CAD := RPAD(MI_CAD,LENGTH(MI_CAD)+6);        
      MI_RS.FACTURA := MI_DBLNUMFACTURA;
      MI_RS.FECHALIMITE := UN_FECHAVENCIMIENTO;
      MI_DBLNUMFACTURA := MI_DBLNUMFACTURA + 1;
      MI_CAD := MI_CAD || LPAD(MI_RS.FACTURA, 16, '0')
                       || RPAD(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                     UN_NOMBRE    => 'FIMM - CODIGO IAC',
                                                     UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                     UN_FECHA_PAR => SYSDATE), 18)
                       || RPAD(PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => MI_RS.ACUEDUCTO <> 0,
                                                     UN_SI        => 'PPP' ,
                                                     UN_NO        =>'   '), 3)
                       || RPAD(PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => MI_RS.ALCANTARILLADO <> 0,
                                                     UN_SI        => 'PPP' ,
                                                     UN_NO        =>'   '), 3)
                       || RPAD(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                     UN_NOMBRE    => 'FIMM - TIPO DE CALCULO',
                                                     UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                     UN_FECHA_PAR => SYSDATE), 18)
                       || RPAD(MI_SALDOS, 50);
      IF MI_H_BM = 'M' THEN 
        MI_CAD := MI_CAD || '1';
      ELSIF MI_H_BM = 'T' THEN
        MI_CAD := MI_CAD || '3';
      ELSIF MI_H_BM = 'S' THEN
        MI_CAD := MI_CAD || '6';
      ELSE
        MI_CAD := MI_CAD || '2';
      END IF;
      IF MI_RS.NUMERODIGITOS < 4 THEN
        MI_CAD := MI_CAD || '4';
      ELSE
        MI_CAD := MI_CAD || RPAD(MI_RS.NUMERODIGITOS, 1);
      END IF;
      MI_CAD := MI_CAD || LPAD(NVL(MI_RS.LECTURA1, 0), 7, ' ');
      IF NVL(MI_RS.FECHALECANTERIOR, SYSDATE) = SYSDATE THEN
        MI_CAD := MI_CAD || TO_CHAR(NVL(MI_RS.FECHALECANTERIOR, ADD_MONTHS(-1 * (PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => MI_H_BM = 'M',
                                                                                                       UN_SI        => 1,
                                                                                                       UN_NO        =>2)), SYSDATE)), 'ddmmyy');
      ELSE
        MI_CAD := MI_CAD || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => TO_CHAR(MI_RS.FECHALECANTERIOR,'DD'), 
                                                      UN_LONGITUD => 2) 
                         || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => TO_CHAR(MI_RS.FECHALECANTERIOR,'MM'), 
                                                      UN_LONGITUD => 2) 
                         || SUBSTR(PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => TO_CHAR(MI_RS.FECHALECANTERIOR,'YYYY'), 
                                                             UN_LONGITUD => 4), 3, 2);
      END IF;
        MI_CAD := MI_CAD || LPAD(NVL(MI_RS.CONSUMO6, 0), 7, '0')
                         || LPAD(NVL(MI_RS.CONSUMO5, 0), 7, '0')
                         || LPAD(NVL(MI_RS.CONSUMO4, 0), 7, '0')
                         || LPAD(NVL(MI_RS.CONSUMO3, 0), 7, '0')
                         || LPAD(NVL(MI_RS.CONSUMO2, 0), 7, '0')
                         || LPAD(NVL(MI_RS.CONSUMO1, 0), 7, '0')
                         || LPAD(MI_PER_HIS, 30);
      IF NVL(MI_RS.ACUMULADO, 0) <> 0 THEN
        MI_CAD := MI_CAD || '-' 
                         || LPAD(NVL(MI_RS.ACUMULADO, 0), 6, '0');
      ELSE
        MI_CAD := MI_CAD || LPAD(0, 7, '0');
      END IF;
      MI_CAD := MI_CAD || TRIM(TO_CHAR(PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => MI_RS.PORCENTAJEAPLICAR = 100,
                                                             UN_SI        => 1,
                                                             UN_NO        => MI_RS.PORCENTAJEAPLICAR/100), '00000.000'))
                       || LPAD(NVL(MI_RS.CONSUMOPROM, 0) * 0.5, 7, '0')
                       || LPAD(NVL(MI_RS.CONSUMOPROM, 0) * 1.5, 7, '0')
                       || LPAD(MI_RS.CONSUMOPROM, 7, '0')
                       || LPAD(PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => NVL(MI_RS.LECTURA, 0) = 0,
                                                     UN_SI        => 0,
                                                     UN_NO        => MI_RS.LECTURA), 7)
                       || RPAD(PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => UN_INDEMISIONFIJA <> 0,
                                                     UN_SI        => 2,
                                                     UN_NO        => 1), 1)
                       || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => TO_CHAR(MI_P1,'DD'), 
                                                    UN_LONGITUD => 2) 
                       || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => TO_CHAR(MI_P1,'MM'), 
                                                    UN_LONGITUD => 2) 
                       || SUBSTR(PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => TO_CHAR(MI_P1,'YYYY'), 
                                                           UN_LONGITUD => 4), 3, 2)
                       || RPAD(PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => UN_INDVENCIMIENTOFIJO <> 0,
                                                     UN_SI        => 2,
                                                     UN_NO        => 1), 1)
                       || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => TO_CHAR(MI_P2,'DD'), 
                                                    UN_LONGITUD => 2) 
                       || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => TO_CHAR(MI_P2,'MM'), 
                                                    UN_LONGITUD => 2) 
                       || SUBSTR(PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => TO_CHAR(MI_P2,'YYYY'), 
                                                           UN_LONGITUD => 4), 3, 2)
                       || LPAD(MI_P4, 3)
                       || LPAD('0', 9, '0')
                       || LPAD(MI_RS.PERIODOSATRASO, 1, '0');
      IF MI_RS.ACUEDUCTO <> 0 THEN
        MI_CAD := MI_CAD || '02';
      ELSE
        MI_CAD := MI_CAD || '  ';
      END IF;
      IF MI_RS.ALCANTARILLADO <> 0 THEN
        MI_CAD := MI_CAD ||  PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_CNFIMM(33).VALOR, 
                                                       UN_LONGITUD => 2);
      ELSE
        MI_CAD := MI_CAD || '  ';
      END IF;
      MI_CAD := MI_CAD || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_CNFIMM(40).VALOR, 
                                                    UN_LONGITUD => 2)
                       || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_CNFIMM(41).VALOR, 
                                                    UN_LONGITUD => 2)
                       || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_CNFIMM(42).VALOR, 
                                                    UN_LONGITUD => 2)
                       || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_CNFIMM(43).VALOR, 
                                                    UN_LONGITUD => 2)
                       || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_CNFIMM(44).VALOR, 
                                                    UN_LONGITUD => 2)
                       || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_CNFIMM(45).VALOR, 
                                                    UN_LONGITUD => 2)
                       || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_CNFIMM(46).VALOR, 
                                                    UN_LONGITUD => 2)
                       || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_CNFIMM(47).VALOR, 
                                                    UN_LONGITUD => 2)
                       || '    '; 
      IF MI_RS.ACUEDUCTO <> 0 THEN
        MI_CAD := MI_CAD || '01';
      ELSE
        MI_CAD := MI_CAD || '  ';
      END IF;
      IF MI_RS.ALCANTARILLADO <> 0 THEN
        MI_CAD := MI_CAD || '04';
      ELSE
        MI_CAD := MI_CAD || '  ';
      END IF;
      BEGIN
        SELECT   SUBFIJO
                ,SUBBASICO
                ,SUBALCFIJO
                ,SUBALCBASICO
                ,SOBREFIJO
                ,SOBREBASICO
                ,SOBREALCFIJO
                ,SOBREALCBASICO
                ,SUBCOMPLEMENTARIO
                ,SUBSUNTUARIO
                ,SUBALCCOMPLEMENTARIO
                ,SUBALCSUNTUARIO
                ,SOBRECOMPLEMENTARIO
                ,SOBRESUNTUARIO
                ,SOBREALCCOMPLEMENTARIO
                ,SOBREALCSUNTUARIO
        INTO     MI_SUBFIJO
                ,MI_SUBBASICO
                ,MI_SUBALCFIJO
                ,MI_SUBALCBASICO
                ,MI_SOBREFIJO
                ,MI_SOBREBASICO
                ,MI_SOBREALCFIJO
                ,MI_SOBREALCBASICO
                ,MI_SUBCOMPLEMENTARIO
                ,MI_SUBSUNTUARIO
                ,MI_SUBALCCOMPLEMENTARIO
                ,MI_SUBALCSUNTUARIO
                ,MI_SOBRECOMPLEMENTARIO
                ,MI_SOBRESUNTUARIO
                ,MI_SOBREALCCOMPLEMENTARIO
                ,MI_SOBREALCSUNTUARIO
        FROM     SP_TARIFAS 
        WHERE    COMPANIA = UN_COMPANIA
          AND    ANO      = MI_RS.ANO
          AND    PERIODO  = MI_RS.PERIODO
          AND    USO      = MI_RS.USO
          AND    ESTRATO  = MI_RS.ESTRATO;
        EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_CAD := MI_CAD || '                ';
          MI_STRTAR := LPAD(' ', 128);
      END;
      IF MI_SUBFIJO <> 0 THEN
        MI_CAD := MI_CAD || '07';
      ELSE
        MI_CAD := MI_CAD || '  ';
      END IF;
      IF MI_SUBBASICO <> 0 THEN
        MI_CAD := MI_CAD || '08';
      ELSE
        MI_CAD := MI_CAD || '  ';
      END IF;
      IF MI_SUBALCFIJO <> 0 THEN
        MI_CAD := MI_CAD || '11';
      ELSE
        MI_CAD := MI_CAD || '  ';
      END IF;
      IF MI_SUBALCBASICO <> 0 THEN
        MI_CAD := MI_CAD || '12';
      ELSE
        MI_CAD := MI_CAD || '  ';
      END IF;
      IF MI_SOBREFIJO <> 0 THEN
        MI_CAD := MI_CAD || '15';
      ELSE
        MI_CAD := MI_CAD || '  ';
      END IF;
      IF MI_SOBREBASICO <> 0 THEN
        MI_CAD := MI_CAD || '16';
      ELSE
        MI_CAD := MI_CAD || '  ';
      END IF;
      IF MI_SOBREALCFIJO <> 0 THEN
        MI_CAD := MI_CAD || '19';
      ELSE
        MI_CAD := MI_CAD || '  ';
      END IF;
      IF MI_SOBREALCBASICO <> 0 THEN
        MI_CAD := MI_CAD || '20';
      ELSE
        MI_CAD := MI_CAD || '  ';
      END IF;
      MI_CAD := MI_CAD || '    ' 
                       || RPAD('0', 24, '0')
                       || RPAD('1111111111', 12) 
                       || '++NNNNNNNNNN' 
                       || RPAD('1100000000', 12)
                       || RPAD(' ', 92);
      IF MI_RS.ACUEDUCTO <> 0 THEN
        MI_CAD := MI_CAD || PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => MI_RS.CONSUMOBASICO <= 0,
                                                  UN_SI        => '     ',
                                                  UN_NO        => PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_RANGO(1).VALOR, 
                                                                                            UN_LONGITUD => 5))
                         || PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => MI_RS.CONSUMOCOMPLEMENTARIO <= 0,
                                                  UN_SI        => '     ',
                                                  UN_NO        => PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_RANGO(2).VALOR,
                                                                                            UN_LONGITUD => 5))
                         || PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => MI_RS.CONSUMOSUNTUARIO <= 0,
                                                  UN_SI        => '     ',
                                                  UN_NO        => '99999');
      ELSE
        MI_CAD := RPAD(MI_CAD,LENGTH(MI_CAD)+15);
      END IF;
      IF MI_RS.ALCANTARILLADO <> 0 THEN
        MI_CAD := MI_CAD || PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => MI_RS.CONSUMOBASICO <= 0,
                                                  UN_SI        => '     ',
                                                  UN_NO        => PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_RANGO(1).VALOR,
                                                                                            UN_LONGITUD => 5))
                         || PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => MI_RS.CONSUMOCOMPLEMENTARIO <= 0,
                                                  UN_SI        => '     ',
                                                  UN_NO        => PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_RANGO(2).VALOR,
                                                                                            UN_LONGITUD => 5))
                         || PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => MI_RS.CONSUMOSUNTUARIO <= 0,
                                                  UN_SI        => '     ',
                                                  UN_NO        => '99999');
      ELSE
        MI_CAD := RPAD(MI_CAD,LENGTH(MI_CAD)+15);
      END IF;
      MI_CAD := MI_CAD || '99999'
                       || TRIM(TO_CHAR(MI_RANGO(1).VALOR, '00000')
                       || TO_CHAR(MI_RANGO(2).VALOR, '00000'))
                       || '9999999999'
                       || TRIM(TO_CHAR(MI_RANGO(1).VALOR, '00000')
                       || TO_CHAR(MI_RANGO(2).VALOR, '00000'))
                       || '9999999999'
                       || TRIM(TO_CHAR(MI_RANGO(1).VALOR, '00000')
                       || TO_CHAR(MI_RANGO(2).VALOR, '00000'))
                       || '9999999999'
                       || TRIM(TO_CHAR(MI_RANGO(1).VALOR, '00000')
                       || TO_CHAR(MI_RANGO(2).VALOR, '00000'))
                       || '99999'
                       || LPAD(' ', 10, ' '); 
      IF MI_RS.ACUEDUCTO <> 0 THEN
        MI_CAD := MI_CAD || LPAD(PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_RS.TARIFABASICO * 10000, 
                                                           UN_LONGITUD => 8),8,' ')
                         || LPAD(PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_RS.TARIFACOMPLEMENTARIO * 10000,
                                                           UN_LONGITUD => 8), 8, ' ')
                         || LPAD(PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_RS.TARIFASUNTUARIO * 10000, 
                                                           UN_LONGITUD => 8), 8,' ');
      ELSE
        MI_CAD := RPAD(MI_CAD, LENGTH(MI_CAD)+24);
      END IF;
      IF MI_RS.ALCANTARILLADO <> 0 THEN
        MI_CAD := MI_CAD || LPAD(PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_RS.ALCBASICO * 10000, 
                                                           UN_LONGITUD => 8), 8,' ')
                         || LPAD(PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_RS.ALCCOMPLEMENTARIO * 10000, 
                                                           UN_LONGITUD => 8), 8,' ')
                         || LPAD(PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_RS.ALCSUNTUARIO * 10000, 
                                                           UN_LONGITUD => 8), 8,' ');
      ELSE
        MI_CAD := RPAD(MI_CAD, LENGTH(MI_CAD)+24);
      END IF;
      IF NVL(MI_SUBFIJO, 0) <> 0 THEN
        MI_STRTAR := LPAD(NVL(MI_SUBFIJO, 0) * 10000, 8, '0');
      ELSE
        MI_STRTAR := LPAD(' ', 8);
      END IF;
      IF NVL(MI_SUBBASICO, 0) <> 0 THEN
        MI_STRTAR := MI_STRTAR || LPAD(NVL(MI_SUBBASICO, 0) * 10000, 8, '0');
      ELSE
        MI_STRTAR := MI_STRTAR || RPAD(' ', 8);
      END IF;
      IF NVL(MI_SUBCOMPLEMENTARIO, 0) <> 0 THEN
        MI_STRTAR := MI_STRTAR || LPAD(NVL(MI_SUBCOMPLEMENTARIO, 0) * 10000, 8, '0');
      ELSE
        MI_STRTAR := MI_STRTAR || RPAD(' ', 8);
      END IF;
      IF NVL(MI_SUBSUNTUARIO, 0) <> 0 THEN
        MI_STRTAR := MI_STRTAR || LPAD(NVL(MI_SUBSUNTUARIO, 0) * 10000, 8, '0');
      ELSE
        MI_STRTAR := MI_STRTAR || RPAD(' ', 8);
      END IF;
      IF NVL(MI_SUBALCFIJO, 0) <> 0 THEN
        MI_STRTAR := MI_STRTAR || LPAD(NVL(MI_SUBALCFIJO, 0) * 10000, 8, '0');
      ELSE
        MI_STRTAR := MI_STRTAR || RPAD(' ', 8);
      END IF;
      IF NVL(MI_SUBALCBASICO, 0) <> 0 THEN
        MI_STRTAR := MI_STRTAR || LPAD(NVL(MI_SUBALCBASICO, 0) * 10000, 8, '0');
      ELSE
        MI_STRTAR := MI_STRTAR || RPAD(' ', 8);
      END IF;
      IF NVL(MI_SUBALCCOMPLEMENTARIO, 0) <> 0 THEN
        MI_STRTAR := MI_STRTAR || LPAD(NVL(MI_SUBALCCOMPLEMENTARIO, 0) * 10000, 8, '0');
      ELSE
        MI_STRTAR := MI_STRTAR || RPAD(' ', 8);
      END IF;
      IF NVL(MI_SUBALCSUNTUARIO, 0) <> 0 THEN
        MI_STRTAR := MI_STRTAR || LPAD(NVL(MI_SUBALCSUNTUARIO, 0) * 10000, 8, '0');
      ELSE
        MI_STRTAR := MI_STRTAR || RPAD(' ', 8);
      END IF;
      IF NVL(MI_SOBREFIJO, 0) <> 0 THEN
        MI_STRTAR := MI_STRTAR || LPAD(NVL(MI_SOBREFIJO, 0) * 10000, 8, '0');
      ELSE
        MI_STRTAR := MI_STRTAR || RPAD(' ', 8);
      END IF;
      IF NVL(MI_SOBREBASICO, 0) <> 0 THEN
        MI_STRTAR := MI_STRTAR || LPAD(NVL(MI_SOBREBASICO, 0) * 10000, 8, '0');
      ELSE
        MI_STRTAR := MI_STRTAR || RPAD(' ', 8);
      END IF;
      IF NVL(MI_SOBRECOMPLEMENTARIO, 0) <> 0 THEN
        MI_STRTAR := MI_STRTAR || LPAD(NVL(MI_SOBRECOMPLEMENTARIO, 0) * 10000, 8, '0');
      ELSE
        MI_STRTAR := MI_STRTAR || RPAD(' ', 8);
      END IF;
      IF NVL(MI_SOBRESUNTUARIO, 0) <> 0 THEN
        MI_STRTAR := MI_STRTAR || LPAD(NVL(MI_SOBRESUNTUARIO, 0) * 10000, 8, '0');
      ELSE
        MI_STRTAR := MI_STRTAR || RPAD(' ', 8);
      END IF;
      IF NVL(MI_SOBREALCFIJO, 0) <> 0 THEN
        MI_STRTAR := MI_STRTAR || LPAD(NVL(MI_SOBREALCFIJO, 0) * 10000, 8, '0');
      ELSE
        MI_STRTAR := MI_STRTAR || RPAD(' ', 8);
      END IF;
      IF NVL(MI_SOBREALCBASICO, 0) <> 0 THEN
        MI_STRTAR := MI_STRTAR || LPAD(NVL(MI_SOBREALCBASICO, 0) * 10000, 8, '0');
      ELSE
        MI_STRTAR := MI_STRTAR || RPAD(' ', 8);
      END IF;
      IF NVL(MI_SOBREALCCOMPLEMENTARIO, 0) <> 0 THEN
        MI_STRTAR := MI_STRTAR || LPAD(NVL(MI_SOBREALCCOMPLEMENTARIO, 0) * 10000, 8, '0');
      ELSE
        MI_STRTAR := MI_STRTAR || RPAD(' ', 8);
      END IF;
      IF NVL(MI_SOBREALCSUNTUARIO, 0) <> 0 THEN
        MI_STRTAR := MI_STRTAR || LPAD(NVL(MI_SOBREALCSUNTUARIO, 0) * 10000, 8, '0');
      ELSE
        MI_STRTAR := MI_STRTAR || RPAD(' ', 8);
      END IF;
      MI_CAD := MI_CAD || MI_STRTAR
                       || RPAD(' ', 16);
      MI_CONS := ' ';
      MI_RECS := ' ';                            
      MI_FACS := ' ';                            
      MI_SUMA := 0;
      <<CEROS>>
      FOR I IN 1..MI_SC_CON LOOP
        MI_S_F1(I).CLAVE := '0'||I;
        MI_S_F1(I).VALOR := 0;
      END LOOP CEROS;
      <<CONCEPTOFACTURADO>>
      FOR MI_RSFACTURADOS IN (
        SELECT   CONCEPTO
                ,VALOR_FACTURADO
        FROM     SP_FACTURADO
        WHERE    COMPANIA   = UN_COMPANIA
          AND    CICLO      = UN_CICLO 
          AND    CODIGORUTA = MI_RS.CODIGORUTA
          AND    PERIODO    = MI_RS.PERIODO
          AND    ANO        = MI_RS.ANO) 
      LOOP
        IF MI_RSFACTURADOS.CONCEPTO BETWEEN 1 AND 50 AND MI_RSFACTURADOS.CONCEPTO BETWEEN 246 AND 250 THEN 
          MI_S_F1(TO_NUMBER(MI_CNFIMM(MI_RSFACTURADOS.CONCEPTO).VALOR)).CLAVE := 'VALORFACTURADO'|| MI_RSFACTURADOS.CONCEPTO;
          MI_S_F1(TO_NUMBER(MI_CNFIMM(MI_RSFACTURADOS.CONCEPTO).VALOR)).VALOR := MI_RSFACTURADOS.VALOR_FACTURADO;
        END IF;
      END LOOP CONCEPTOFACTURADO;
      <<VALORESFACS>>
      FOR I IN 1..MI_SC_CON LOOP
        IF MI_S_F1(I).VALOR <> 0 THEN
          IF I BETWEEN 1 AND 50 AND I BETWEEN 246 AND 250 THEN
            IF MI_CNFIMM(I).VALOR <> 2  AND MI_CNFIMM(I).VALOR <> 5 AND I <> MI_CNFIMM(33).VALOR THEN
              MI_CONS := MI_CONS || TO_CHAR(I, '00');
              MI_FACS := MI_FACS || PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => MI_S_F1(I).VALOR >= 0,
                                                           UN_SI       => TO_CHAR(MI_S_F1(I).VALOR, '000000000'),
                                                           UN_NO       => TO_CHAR(MI_S_F1(I).VALOR, '00000000'));
              MI_SUMA := MI_SUMA + MI_S_F1(I).VALOR;
              IF I <> 16 AND I <> 23 THEN
                MI_RECS := MI_RECS || '1';
              ELSE
                MI_RECS := MI_RECS || '0';
              END IF;
            END IF;
          END IF;
        END IF;
      END LOOP VALORESFACS;
      MI_CAD := MI_CAD || RPAD(MI_CONS, 40)
                       || RPAD(MI_RECS, 20)
                       || RPAD(MI_FACS, 180)
                       || LPAD(MI_RS.PERIODOSATRASO, 2, '0')
                       || '    ' 
                       || PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => MI_SUMA >= 0,
                                                UN_SI        => TO_CHAR(MI_SUMA, '000000000'),
                                                UN_NO        => TO_CHAR(MI_SUMA, '00000000'));
      <<CARACTERESESP>>
      FOR T IN 1..LENGTH(MI_CAD) LOOP
        MI_CAR := SUBSTR(MI_CAD, T, 1);
        MI_CAR := CASE 
                    WHEN ASCII(MI_CAR) < 32 THEN ' '
                    WHEN ASCII(MI_CAR) = 42 THEN ' '
                    WHEN ASCII(MI_CAR) = 130 THEN 'e'
                    WHEN ASCII(MI_CAR) = 160 THEN 'a'
                    WHEN ASCII(MI_CAR) = 161 THEN 'i'
                    WHEN ASCII(MI_CAR) = 162 THEN 'o'
                    WHEN ASCII(MI_CAR) = 163 THEN 'u'
                    WHEN ASCII(MI_CAR) = 164 THEN 'n'
                    WHEN ASCII(MI_CAR) = 165 THEN 'N'
                    WHEN ASCII(MI_CAR) > 122 THEN ' '
                    WHEN ASCII(MI_CAR) BETWEEN 58 AND 64 THEN ' '
                    WHEN ASCII(MI_CAR) BETWEEN 91 AND 96 THEN ' '
                    ELSE MI_CAR
                  END;
        MI_CAD1 := MI_CAD1 || MI_CAR ;
      END LOOP CARACTERESESP;
      BEGIN
        BEGIN
          MI_TABLAUPDATE    :='SP_USUARIO';
          MI_CAMPOSUPDATE   :='FIMM = ''P'', MODIFIED_BY='''|| UN_USUARIO ||''', DATE_MODIFIED=SYSDATE';
          MI_CONDICIONUPDATE:=' COMPANIA    = '''||UN_COMPANIA||
                              ''' AND CICLO ='||UN_CICLO|| 
                              ' AND CODIGORUTA ='''||MI_RS.CODIGORUTA||'''';
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLAUPDATE,
                                                UN_ACCION    => 'M',      
                                                UN_CAMPOS    => MI_CAMPOSUPDATE,      
                                                UN_CONDICION => MI_CONDICIONUPDATE);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'CAMPOS';
          MI_MSGERROR(1).VALOR := 'FIMM';
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD   => SQLCODE,
            UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_ACTUALIZARUSUARIO,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END;
      MI_CAD1 := MI_CAD1 || CHR(10);
    END LOOP TARIFASUSUA;
    RETURN MI_CAD1;
    BEGIN
      BEGIN
        MI_TABLAUPDATE    := 'SP_FACTURAS';
        MI_CAMPOSUPDATE   := 'CONSECUTIVO = '||MI_DBLNUMFACTURA ||', MODIFIED_BY='''||UN_USUARIO||''', DATE_MODIFIED=SYSDATE';
        MI_CONDICIONUPDATE:= 'COMPANIA    = '''||UN_COMPANIA||
                             ''' AND CICLO ='  ||UN_CICLO;
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLAUPDATE,
                                              UN_ACCION    => 'M',      
                                              UN_CAMPOS    => MI_CAMPOSUPDATE,      
                                              UN_CONDICION => MI_CONDICIONUPDATE);
        PCK_SERVICIOS_PUBLICOS_COM4.PR_REGISTRARPROCESO(UN_COMPANIA    => UN_COMPANIA, 
                                                        UN_PROCESO     => 'PFIMME', 
                                                        UN_DESCRIPCION => 'EXPORTACION PLANOS' || SYSDATE, 
                                                        UN_PARAMETROS  => MI_PARFUNCION, 
                                                        UN_RESULTADOS  => MI_STRPLANO, 
                                                        UN_ESTADO      => MI_STRESTADO, 
                                                        UN_USUARIO     => UN_USUARIO, 
                                                        UN_FECHA       => SYSDATE);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CAMPOS';
        MI_MSGERROR(1).VALOR := 'CONSECUTIVO';
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD   => SQLCODE,
          UN_ERROR_COD => PCK_ERRORES.ERR_FACSERP_ACTUALIZARFACTURA,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
  END FC_FIMMCUERPO;

  -- 10
  FUNCTION FC_NOMBREPER
    /*
      NAME              : FC_NOMBREPER En Access --> NombrePer
      AUTHORS           : SYSMAN  SAS 
      AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICENO
      DATE MIGRADOR     : 03/01/2017
      TIME              : 12:50 PM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MODIFIED     : 10/01/2017
      TIME              : 03:10 PM
      DESCRIPTION       : RETORNA LAS TRES PRIMERAS LETRAS DEL MES, CONCATENADO CON EL ANIO
      PARAMETERS        : UN_COMPANIA   => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_ANO        => ANIO DEL QUE SE QUIERE OBTENER EL PERIODO.
                          UN_PERIODO    => NUMERO DEL MES DEL QUE SE QUIERE OBTENER EL NOMBRE.
                          UN_FRECUENCIA => FRECUENCIA DEL PERIODO.
      MODIFICATIONS     : CORRECCION SEGUN ESTANDAR DE PROGRAMACION Y REFERENCIACION DE FUNCIONES.
      @NAME:  obtenerNombrePeriodo
      @METHOD:  GET
    */
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_ANO          IN PCK_SUBTIPOS.TI_ANIO
   ,UN_PERIODO      IN PCK_SUBTIPOS.TI_PERIODO
   ,UN_FRECUENCIA   VARCHAR2 -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
  )
    RETURN VARCHAR2
  AS 
    MI_PERIODO_ANT   PCK_SUBTIPOS.TI_PERIODO;
    MI_FRECUENCIA    VARCHAR2(3 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_NOMBREPERIODO VARCHAR2(300 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_MES           PCK_SUBTIPOS.TI_MES;
    MI_MESUNO        PCK_SUBTIPOS.TI_MES;
  BEGIN 
    IF UN_FRECUENCIA IS NULL OR UN_FRECUENCIA = '' THEN 
      MI_FRECUENCIA := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                             UN_NOMBRE    => 'FRECUENCIA PERIODOS DE FACTURACION',
                                             UN_MODULO    => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                             UN_FECHA_PAR => SYSDATE);
    ELSE
      MI_FRECUENCIA := UN_FRECUENCIA;
    END IF;
    CASE 
      WHEN MI_FRECUENCIA = 'M' THEN 
        MI_NOMBREPERIODO := SUBSTR(PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => UN_PERIODO),1,3) 
                         || '/' 
                         || UN_ANO;
      WHEN MI_FRECUENCIA = 'B' THEN 
        MI_MES := UN_PERIODO * 2 - 1;
        MI_MESUNO := UN_PERIODO * 2;
        MI_NOMBREPERIODO := SUBSTR(PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => MI_MES),1,3) 
                         || '-' 
                         || PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => MI_MESUNO) 
                         || '/' 
                         || UN_ANO;
      WHEN MI_FRECUENCIA = 'C' THEN 
        MI_PERIODO_ANT := CASE 
                            WHEN UN_PERIODO * 2 - 2 = 0 
                            THEN 12 
                            ELSE UN_PERIODO * 2 - 2 
                          END;
        MI_MES := UN_PERIODO * 2 - 1;
        MI_NOMBREPERIODO := SUBSTR(PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => MI_PERIODO_ANT),1,3) 
                         || '-' 
                         || PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => MI_MES) 
                         || '/' 
                         || UN_ANO;
      WHEN MI_FRECUENCIA = 'T' THEN 
        MI_MES := UN_PERIODO * 3 - 2;
        MI_MESUNO := UN_PERIODO *  3;
        MI_NOMBREPERIODO := SUBSTR(PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => MI_MES),1,3) 
                         || '-' 
                         || PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => MI_MESUNO) 
                         || '/' 
                         || UN_ANO;
    END CASE;
    RETURN MI_NOMBREPERIODO;
  END FC_NOMBREPER;

  -- 11
  FUNCTION FC_FIMMSOLOLECTURACABEZA
    /*
      NAME              : FC_FIMMSOLOLECTURACABEZA --> EN ACCESS EnviarFIMMSoloLectura
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICENO
      DATE MIGRADOR     : 06/01/2017
      TIME              : 08:35 AM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MODIFIED     : 10/01/2017
      TIME              : 03:30 PM
      DESCRIPTION       : FUNCION QUE RETORNA UNA CADENA CON EL TEXTO CORRESPONDIENTE AL ARCHIVO PLANO QUE SE QUIERE EXPORTAR.
      PARAMETERS        : UN_COMPANIA         => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_CICLO            => CICLO POR EL CUAL SE VA A FILTRAR LA INFORMACION.
                          UN_STRDIASAFACTURAR => NUMERO DE DIAS A FACTURAR.
                          UN_DIASDESDE        => NUMERO DEL DIA INICIAL.
                          UN_DIASHASTA        => NUMERO DEL DIA FINAL.
                          UN_CODIGOINICIAL    => CODIGO INICIAL PARA LA CONSULTA DE LOS REGISTROS.
                          UN_CODIGOFINAL      => CODIGO FINAL PARA LA CONSULTA DE LOS REGISTROS.
      MODIFICATIONS     : CORRECCION SEGUN ESTANDAR DE PROGRAMACION, REFERENCIACION DE FUNCIONES Y OPTIMIZACION 
                          DE MANEJO DE ERRORES.
      @NAME:  enviarFimmSoloLecturaCabeza
      @METHOD:  GET
    */
  (
    UN_COMPANIA              IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_CICLO                 IN PCK_SUBTIPOS.TI_CICLO
   ,UN_STRDIASAFACTURAR      IN PCK_SUBTIPOS.TI_ENTERO
   ,UN_DIASDESDE             IN PCK_SUBTIPOS.TI_ENTERO
   ,UN_DIASHASTA             IN PCK_SUBTIPOS.TI_ENTERO
   ,UN_CODIGOINICIAL         IN PCK_SUBTIPOS.TI_CODIGORUTA
   ,UN_CODIGOFINAL           IN PCK_SUBTIPOS.TI_CODIGORUTA
  )
    RETURN CLOB
  AS 
    MI_P1       PCK_SUBTIPOS.TI_ENTERO;
    MI_P2       VARCHAR2(30 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_P3       VARCHAR2(30 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_STRTEMP  CLOB; 
    MI_ANIO     PCK_SUBTIPOS.TI_ANIO; 
    MI_PERIODO  PCK_SUBTIPOS.TI_PERIODO;
  BEGIN
    MI_P1 := 30;
    MI_P2 := 33;
    MI_P3  := 40;
    BEGIN
      WITH TARSINMEDICION AS (
        SELECT   SP_FACTURADO.COMPANIA
                ,SP_FACTURADO.CICLO
                ,SP_FACTURADO.CODIGORUTA 
                ,SP_FACTURADO.ANO
                ,SP_FACTURADO.PERIODO
                ,SP_FACTURADO.CONCEPTO 
                ,SP_FACTURADO.VALOR_FACTURADO
        FROM     SP_FACTURADO
        WHERE    SP_FACTURADO.CONCEPTO        = 19 
          AND    SP_FACTURADO.VALOR_FACTURADO <> 0)
      SELECT   SP_USUARIO.ANO
              ,SP_USUARIO.PERIODO
      INTO     MI_ANIO,
               MI_PERIODO
      FROM     SP_USUARIO 
        LEFT JOIN TARSINMEDICION 
          ON      SP_USUARIO.PERIODO    = TARSINMEDICION.PERIODO 
         AND      SP_USUARIO.ANO        = TARSINMEDICION.ANO 
         AND      SP_USUARIO.CODIGORUTA = TARSINMEDICION.CODIGORUTA
         AND      SP_USUARIO.CICLO      = TARSINMEDICION.CICLO
         AND      SP_USUARIO.COMPANIA   = TARSINMEDICION.COMPANIA
      WHERE    SP_USUARIO.COMPANIA     = UN_COMPANIA 
        AND    SP_USUARIO.CICLO        = UN_CICLO 
        AND    SP_USUARIO.CODIGORUTA   BETWEEN UN_CODIGOINICIAL AND UN_CODIGOFINAL 
        AND    TARSINMEDICION.CONCEPTO IS NULL 
        AND    (SP_USUARIO.LECTURA 
             + SP_USUARIO.LECTURA1 
             + SP_USUARIO.LECTURA2 
             + SP_USUARIO.LECTURA3 
             + SP_USUARIO.LECTURA4 
             + SP_USUARIO.LECTURA5 
             + SP_USUARIO.LECTURA6)    > 0 
        AND    SP_USUARIO.ACUEDUCTO    <> 0
        AND    ROWNUM                  = 1
      ORDER BY SP_USUARIO.COMPANIA
              ,SP_USUARIO.CICLO
              ,SP_USUARIO.CODIGORUTA;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_ANIO := 0;
        MI_PERIODO := 0;
    END;
    MI_P1 := UN_STRDIASAFACTURAR;
    MI_P2 := UN_DIASDESDE;
    MI_P3 := UN_DIASHASTA;
    MI_STRTEMP := '' 
                  || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => UN_CICLO, 
                                               UN_LONGITUD => 2) 
                  || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_PERIODO, 
                                               UN_LONGITUD => 2)
                  || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => SUBSTR(LPAD(MI_ANIO,5,' '), 3),
                                               UN_LONGITUD => 2)
                  || '1      '
                  || RPAD(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA,
                                                UN_NOMBRE => 'FIMM - CODIGO DE GESTION',
                                                UN_MODULO => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                UN_FECHA_PAR => SYSDATE), 10)
                  || RPAD(' ',11)
                  || MI_PERIODO
                  || SUBSTR(MI_ANIO ,3)
                  || MI_PERIODO 
                  || SUBSTR(MI_ANIO ,3)
                  || RPAD(' ',20)
                  || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_P1, 
                                               UN_LONGITUD => 3)       
                  || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_P2, 
                                               UN_LONGITUD => 3)       
                  || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_P3, 
                                               UN_LONGITUD => 3) 
                  || RPAD(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA,
                                                UN_NOMBRE => 'FIMM - FLAG_INCI',
                                                UN_MODULO => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                UN_FECHA_PAR => SYSDATE), 99) 
                  || RPAD(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA,
                                                UN_NOMBRE => 'FIMM - EXITRUTINE',
                                                UN_MODULO => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                UN_FECHA_PAR => SYSDATE), 99) 
                  || RPAD(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA,
                                                UN_NOMBRE => 'FIMM - CAMPOS 270 A 309',
                                                UN_MODULO => PCK_DATOS.MODULOSERVICIOSPUBLICOS,
                                                UN_FECHA_PAR => SYSDATE), 40)    
                  || RPAD(' ',7)
                  || 'N';
    RETURN MI_STRTEMP;
  END FC_FIMMSOLOLECTURACABEZA;

  -- 12
  FUNCTION FC_FIMMSOLOLECTURACUERPO
    /*
      NAME              : FC_FIMMSOLOLECTURACUERPO --> EN ACCESS EnviarFIMMSoloLectura
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICENO
      DATE MIGRADOR     : 06/01/2017
      TIME              : 10:37 AM
      SOURCE MODULE     : SERVICIOS PUBLICOS
      MODIFIER          : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MODIFIED     : 10/01/2017
      TIME              : 03:55 PM
      DESCRIPTION       : FUNCION QUE RETORNA UNA CADENA CON EL TEXTO CORRESPONDIENTE AL ARCHIVO PLANO QUE SE QUIERE EXPORTAR.
      PARAMETERS        : UN_COMPANIA      => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_CICLO         => CICLO POR EL CUAL SE VA A FILTRAR LA INFORMACION.
                          UN_CODIGOINICIAL => CODIGO INICIAL PARA LA CONSULTA DE LOS REGISTROS.
                          UN_CODIGOFINAL   => CODIGO FINAL PARA LA CONSULTA DE LOS REGISTROS.
                          UN_USUARIO       => USUARIO QUE ESTA TRABAJANDO.
      MODIFICATIONS     : CORRECCION SEGUN ESTANDAR DE PROGRAMACION.
      @NAME:  enviarFimmSoloLecturaCuerpo
      @METHOD:  GET
    */
  (
    UN_COMPANIA              IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_CICLO                 IN PCK_SUBTIPOS.TI_CICLO
   ,UN_CODIGOINICIAL         IN PCK_SUBTIPOS.TI_CODIGORUTA
   ,UN_CODIGOFINAL           IN PCK_SUBTIPOS.TI_CODIGORUTA
  )
    RETURN CLOB
  AS 
    MI_CAD        CLOB; 
  BEGIN
    <<FACTURAUSUARIO>>
    FOR MI_RSUSUARIO IN (
      WITH TARSINMEDICION AS (
        SELECT   SP_FACTURADO.COMPANIA
                ,SP_FACTURADO.CICLO 
                ,SP_FACTURADO.CODIGORUTA 
                ,SP_FACTURADO.ANO
                ,SP_FACTURADO.PERIODO 
                ,SP_FACTURADO.CONCEPTO
                ,SP_FACTURADO.VALOR_FACTURADO
        FROM     SP_FACTURADO
        WHERE    SP_FACTURADO.CONCEPTO        = 19 
          AND    SP_FACTURADO.VALOR_FACTURADO <> 0)
      SELECT   SP_USUARIO.CODIGORUTA
              ,SP_USUARIO.CODIGOINTERNO
              ,SP_USUARIO.MEDIDOR
              ,SP_USUARIO.PRIMERAPELLIDO
              ,SP_USUARIO.SEGUNDOAPELLIDO
              ,SP_USUARIO.NOMBRES
              ,SP_USUARIO.DIRGUIA
              ,SP_USUARIO.DIRTECNICA
              ,SP_USUARIO.USO
              ,SP_USUARIO.NUMERODIGITOS
              ,SP_USUARIO.LECTURA
              ,SP_USUARIO.FECHALECANTERIOR
              ,SP_USUARIO.CONSUMOPROM
      FROM     SP_USUARIO 
        LEFT JOIN TARSINMEDICION 
          ON      SP_USUARIO.PERIODO    = TARSINMEDICION.PERIODO 
         AND      SP_USUARIO.ANO        = TARSINMEDICION.ANO 
         AND      SP_USUARIO.CODIGORUTA = TARSINMEDICION.CODIGORUTA
         AND      SP_USUARIO.CICLO      = TARSINMEDICION.CICLO
         AND      SP_USUARIO.COMPANIA   = TARSINMEDICION.COMPANIA
      WHERE    SP_USUARIO.COMPANIA     = UN_COMPANIA 
        AND    SP_USUARIO.CICLO        = UN_CICLO 
        AND    SP_USUARIO.CODIGORUTA   BETWEEN UN_CODIGOINICIAL AND UN_CODIGOFINAL 
        AND    TARSINMEDICION.CONCEPTO IS NULL 
        AND    (SP_USUARIO.LECTURA 
             + SP_USUARIO.LECTURA1 
             + SP_USUARIO.LECTURA2 
             + SP_USUARIO.LECTURA3 
             + SP_USUARIO.LECTURA4 
             + SP_USUARIO.LECTURA5 
             + SP_USUARIO.LECTURA6)    > 0 
        AND    SP_USUARIO.ACUEDUCTO    <> 0
        AND    ROWNUM                  = 1
      ORDER BY SP_USUARIO.COMPANIA
              ,SP_USUARIO.CICLO
              ,SP_USUARIO.CODIGORUTA)
    LOOP
      MI_CAD := '' 
                || RPAD(MI_RSUSUARIO.CODIGORUTA, 16, ' ')
                || LPAD(NVL(MI_RSUSUARIO.CODIGOINTERNO, ' '), 8) 
                || RPAD(NVL(MI_RSUSUARIO.MEDIDOR, 0), 12)
                || RPAD(MI_RSUSUARIO.PRIMERAPELLIDO 
                || ' ' 
                || NVL(MI_RSUSUARIO.SEGUNDOAPELLIDO, ' ') 
                || ' ' 
                || NVL(MI_RSUSUARIO.NOMBRES, ' '), 70)
                || RPAD(NVL(MI_RSUSUARIO.DIRGUIA, ' '), 40) 
                || RPAD(NVL(MI_RSUSUARIO.DIRTECNICA, ' '), 40)
                || RPAD(NVL(MI_RSUSUARIO.USO, ' '), 1)
                || RPAD(' ',36)
                || TRIM(NVL(MI_RSUSUARIO.NUMERODIGITOS, 0))
                || LPAD(TRIM(NVL(MI_RSUSUARIO.LECTURA, 0)), 7, '0')
                || TRIM(TO_CHAR(NVL(MI_RSUSUARIO.FECHALECANTERIOR, SYSDATE), 'DDMMYY')) 
                || '0001.0000'
                || TRIM(TO_CHAR(TO_NUMBER(MI_RSUSUARIO.CONSUMOPROM * 0.5), '0000000')) 
                || TRIM(TO_CHAR(TO_NUMBER(MI_RSUSUARIO.CONSUMOPROM * 1.5), '0000000'))
                || TRIM(TO_CHAR(MI_RSUSUARIO.CONSUMOPROM, '0000000'))
                || '  ';
    END LOOP FACTURAUSUARIO;
    RETURN MI_CAD;
  END FC_FIMMSOLOLECTURACUERPO;

  -- 13
  PROCEDURE PR_REGISTRARPROCESO
    /*
      NAME              : PR_REGISTRARPROCESO -- EN ACCESS REGISTARPROCESO
      AUTHORS           : SYSMAN LTDA
      AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICENO
      DATE MIGRADOR     : 27/12/2016
      TIME              : 04:35 PM
      MODIFIER          : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MODIFIED     : 10/01/2017
      TIME              : 4:15
      DESCRIPTION       : PROCEDIMIENTO QUE PERMITE INSERTAR EN LA TABLA SP_LOGPROCESOS LOS PROCESOS REALIZADOS
                          EN EL MODULO DE SERVICIOS PUBLICOS.
      PARAMETERS        : UN_COMPANIA     => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_PROCESO      => NOMBRE DEL PROCESO REALIZADO.
                          UN_DESCRIPCION  => BREVE DESCRIPCION DE LO QUE HACE EL PROCESO.
                          UN_PARAMETROS   => PARAMETROS NECESARIOS PARA REALIZAR EL PROCESO.
                          UN_RESULTADOS   => DESCRIPCION DE LOS RESULTADOS OBTENIDOS LUEGO DE LA EJECUCION DEL PROCESO.
                          UN_ESTADO       => ESTADO EN EL QUE SE ENCUENTRA EL PROCESO
                          UN_USUARIO      => USUARIO QUE REALIZA LA MODIFICACION.
                          UN_FECHA        => FECHA CON HORA, EN LA QUE SE EJECUTA EL PROCESO
      MODIFICATIONS     : CORRECCION SEGUN ESTANDAR DE PROGRAMACION, REFERENCIACION DE FUNCIONES Y OPTIMIZACION 
                          DE MANEJO DE ERRORES.
      MODIFIER          : SERGIO ESTEBAN PIÑA VARGAS
      DATE MODIFIED     : 26/05/2017
      DESCRIPTION       : AGREGO CAMPOS DE AUDITORIA CREATED_BY, DATE_CREATED
      @NAME:  registrarProceso
      @METHOD:  POST
     */
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_PROCESO      IN VARCHAR2 -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
   ,UN_DESCRIPCION  IN VARCHAR2 -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
   ,UN_PARAMETROS   IN VARCHAR2 -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
   ,UN_RESULTADOS   IN VARCHAR2 -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
   ,UN_ESTADO       IN VARCHAR2 -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
   ,UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO  
   ,UN_FECHA        IN DATE 
  )
  AS
    MI_TABLAINSERT      PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOSINSERT     PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORESINSERT    PCK_SUBTIPOS.TI_VALORES;
    MI_PARAMETROS       VARCHAR2(1000 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
  BEGIN
    BEGIN
      MI_PARAMETROS := REPLACE(UN_PARAMETROS, '''', '*');
      -- SE REALIZA LA INSERCION EN LA TABLA SP_LOGPROCESOS
      MI_TABLAINSERT:='SP_LOGPROCESOS';
      MI_CAMPOSINSERT:='COMPANIA
                       ,PROCESO
                       ,FECHAPROCESO
                       ,DATE_CREATED
                       ,HORAPROCESO
                       ,USUARIO
                       ,CREATED_BY
                       ,PARAMETROS
                       ,DESCRIPCION
                       ,RESULTADOS
                       ,ESTADO';
      MI_VALORESINSERT:=''''   ||UN_COMPANIA||
                        ''','''||UN_PROCESO||
                        ''',TO_DATE('''||TO_CHAR(UN_FECHA,'DD/MM/YYYY')||''',''DD/MM/YYYY''),
                        TO_DATE('''||TO_CHAR(UN_FECHA,'DD/MM/YYYY')||''',''DD/MM/YYYY''),
                        TO_DATE('''||TO_CHAR(UN_FECHA,'HH24:MM:SS')||''',''HH24:MM:SS''),
                        '''    ||UN_USUARIO||''',
                        '''    ||UN_USUARIO||''',
                        '''||UN_PARAMETROS||
                        ''','''||UN_DESCRIPCION||
                        ''','''||UN_RESULTADOS||
                        ''','''||UN_ESTADO||'''';
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLAINSERT, 
                                            UN_ACCION  => 'I',
                                            UN_CAMPOS  => MI_CAMPOSINSERT,    
                                            UN_VALORES => MI_VALORESINSERT);
      -- SE LANZA UNA EXCEPCION EN CASO DE QUE NO SE REALICE LA INSERCION
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
    END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD    => SQLCODE,
        UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_REGISTRAR_PROCESO,
        UN_TABLAERROR => 'SP_LOGPROCESOS'
      );
  END PR_REGISTRARPROCESO;

END PCK_SERVICIOS_PUBLICOS_COM4;