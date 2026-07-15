create or replace PACKAGE BODY "PCK_PRESUPUESTO1" AS
  
  -- 1
  FUNCTION FC_VERIFICAR_INDICADORES_PPTO
    /*   
      NAME              : VERIFICAR_INDICADORES 
      AUTHORS           : SYSMAN LTDA
      AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO  
      DATE MIGRADOR     : 23/01/2015  
      TIME              : 2:15 PM
      MODIFIER          : SANDRA MILENA DAZA LEGUIZAMON / JESSICA LISSETH RAMIREZ BRICEÑO / LEYDI MILENA CORTÉS FORERO
      DATE MODIFIED     : 27/01/2015 / 10/01/2017 / 30/11/2017
      TIME              : 9:30 AM / 04:25 PM / 4:30 PM
      DESCRIPTION       : REVISA LOS INDICADORES DE MOVIMIENTO Y AUXILIARES PARA DEVOLVER EN UNA TYPE 
                          LOS VALORES CORRECTOS DE LOS MISMOS PARA UN RUBRO DADO
      PARAMETERS        : UN_COMPANIA      => COMPANIA EN LA QUE SE EST�? TRABAJANDO.
                          UN_ANIO          => ANIO DEL QUE SE QUIERE VERIFICAR EL RUBRO.
                          UN_MES           => MES DEL QUE SE QUIERE VERIFICAR EL RUBRO.
                          UN_CODIGO        => CODIGO DEL PLAN PRESUPUESTAL.
                          UN_CENTRO        => CODIGO DEL CENTRO DE COSTO.
                          UN_TERCERO       => NIT DEL TERCERO.
                          UN_SUCURSAL      => SUCURSAL DEL TERCERO.
                          UN_AUXILIAR      => CODIGO DEL AUXILIAR.
                          UN_REFERENCIA    => CODIGO DE LA REFERENCIA.
                          UN_FUENTERECURSO => CODIGO DE LA FUENTE DE RECURSO.
      MODIFICATIONS     : CORRECCION SEGUN ESTANDAR DE PROGRAMACION
                          LCORTES-> SE AGREGA EL CAMPO DESTINO PARA AGREGARLO EN LA TABLA PLAN_PPTAL_CONFIG EN EL PROCEDIMIENTO 
                                    PR_CREAR_SALDOAUXPRESUPUESTAL.
      @NAME:  verificarIndicadoresDeMovimientoPresupuestales
      @METHOD:  GET
    */
  (
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_ANIO             IN PCK_SUBTIPOS.TI_ANIO
   ,UN_MES              IN PCK_SUBTIPOS.TI_MES 
   ,UN_CODIGO           IN PCK_SUBTIPOS.TI_CODIGOPPTAL
   ,UN_CENTRO           IN PCK_SUBTIPOS.TI_CENTRO_COSTO 
   ,UN_TERCERO          IN PCK_SUBTIPOS.TI_TERCERO   
   ,UN_SUCURSAL         IN PCK_SUBTIPOS.TI_SUCURSAL
   ,UN_AUXILIAR         IN PCK_SUBTIPOS.TI_AUXILIAR
   ,UN_REFERENCIA       IN PCK_SUBTIPOS.TI_REFERENCIA
   ,UN_FUENTERECURSO    IN PCK_SUBTIPOS.TI_FUENTE_RECURSOS    
  ) 
    RETURN TYP_RUBRO_AUX  
  AS
    MI_RUBRO        TYP_RUBRO_AUX;
    MI_MAN_CEN_CTO  PCK_SUBTIPOS.TI_LOGICO;
    MI_MAN_AUX_TER  PCK_SUBTIPOS.TI_LOGICO;
    MI_MAN_AUX_GEN  PCK_SUBTIPOS.TI_LOGICO;
    MI_MAN_AUX_FUE  PCK_SUBTIPOS.TI_LOGICO;  
    MI_MAN_AUX_REF  PCK_SUBTIPOS.TI_LOGICO;
    MI_MOVIMIENTO   PCK_SUBTIPOS.TI_LOGICO;
    MI_NATURALEZA   VARCHAR2(1 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS 
    MI_TIPOVIGENCIA PLAN_PRESUPUESTAL.TIPOVIGENCIA%TYPE;
    MI_DESTINO      PLAN_PRESUPUESTAL.DESTINO%TYPE;
    MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;  
  BEGIN 
    BEGIN
      SELECT  MAN_CEN_CTO
             ,MAN_AUX_TER
             ,MAN_AUX_GEN
             ,MAN_AUX_FUE
             ,MAN_AUX_REF
             ,MOVIMIENTO
             ,NATURALEZA
             ,TIPOVIGENCIA
             ,DESTINO
      INTO    MI_MAN_CEN_CTO
             ,MI_MAN_AUX_TER
             ,MI_MAN_AUX_GEN
             ,MI_MAN_AUX_FUE
             ,MI_MAN_AUX_REF
             ,MI_MOVIMIENTO
             ,MI_NATURALEZA
             ,MI_TIPOVIGENCIA
             ,MI_DESTINO
      FROM    PLAN_PRESUPUESTAL 
      WHERE   COMPANIA = UN_COMPANIA 
      AND     ANO      = UN_ANIO 
      AND     CODIGO   = UN_CODIGO;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_MSGERROR(1).CLAVE := 'CODIGO';
        MI_MSGERROR(1).VALOR := UN_CODIGO;
        MI_MSGERROR(2).CLAVE := 'ANO';
        MI_MSGERROR(2).VALOR := UN_ANIO;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => -20001,
          UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_VERIFIND_NE,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;      
    MI_RUBRO.MI_CODIGO := UN_CODIGO;
    IF(MI_MAN_CEN_CTO +  MI_MAN_AUX_TER + MI_MAN_AUX_GEN + MI_MAN_AUX_FUE + MI_MAN_AUX_REF + MI_MOVIMIENTO) = 0 THEN
      BEGIN
        MI_MSGERROR(1).CLAVE := 'CODIGO';
        MI_MSGERROR(1).VALOR := UN_CODIGO;
        MI_MSGERROR(2).CLAVE := 'ANO';
        MI_MSGERROR(2).VALOR := UN_ANIO; 
     RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO  THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(          
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_VERIFIND_IND0,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END;
    END IF;  
    MI_RUBRO.MI_COMPANIA      := UN_COMPANIA;
    MI_RUBRO.MI_ANIO          := UN_ANIO;
    MI_RUBRO.MI_MES           := UN_MES;
    MI_RUBRO.MI_NATURALEZA    := MI_NATURALEZA;
    MI_RUBRO.MI_MOVIMIENTO    := MI_MOVIMIENTO;
    MI_RUBRO.MI_TERCERO       := UN_TERCERO;
    MI_RUBRO.MI_SUCURSAL      := UN_SUCURSAL;
    MI_RUBRO.MI_CENTROCOSTO   := UN_CENTRO;  
    MI_RUBRO.MI_AUXILIAR      := UN_AUXILIAR;
    MI_RUBRO.MI_REFERENCIA    := UN_REFERENCIA;
    MI_RUBRO.MI_FUENTERECURSO := UN_FUENTERECURSO;
    MI_RUBRO.MI_TIPOVIGENCIA  := MI_TIPOVIGENCIA;
    MI_RUBRO.MI_DESTINO       := MI_DESTINO;

    IF MI_MAN_CEN_CTO = 0 OR UN_CENTRO IS NULL THEN 
      MI_RUBRO.MI_CENTROCOSTO := PCK_DATOS.CONS_CENTRO;
    END IF;
    IF MI_MAN_AUX_TER = 0 OR UN_TERCERO IS NULL THEN 
      MI_RUBRO.MI_TERCERO := PCK_DATOS.CONS_TERCERO;
      MI_RUBRO.MI_SUCURSAL := PCK_DATOS.CONS_SUCURSAL;
    END IF;
    IF MI_MAN_AUX_GEN = 0 OR UN_AUXILIAR IS NULL THEN 
      MI_RUBRO.MI_AUXILIAR := PCK_DATOS.CONS_AUXILIAR;       
    END IF;
    IF MI_MAN_AUX_REF = 0 OR UN_REFERENCIA IS NULL THEN 
      MI_RUBRO.MI_REFERENCIA := PCK_DATOS.CONS_REFERENCIA;      
    END IF; 
    IF MI_MAN_AUX_FUE = 0 OR UN_FUENTERECURSO IS NULL THEN 
      MI_RUBRO.MI_FUENTERECURSO := PCK_DATOS.CONS_FUENTE;   
    END IF;
    RETURN MI_RUBRO;  
  END FC_VERIFICAR_INDICADORES_PPTO;

  -- 2
  FUNCTION FC_VERIFICAR_INDICADORES_PPTO
    /*   
      NAME              : VERIFICAR_INDICADORES 
      AUTHORS           : SYSMAN LTDA
      AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLNCO  
      DATE MIGRADOR     : 29/12/2016  
      TIME              : 17:15 PM
      MODIFIER          : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MODIFIED     : 10/01/2017
      TIME              : 05:30 PM
      DESCRIPTION       : REVISA LOS INDICADORES DE MOVIMIENTO Y AUXILIARES PARA DEVOLVER 0 SI NO EXISTE Y -1 SI EXISTE CON INDICADORES
      MODIFICATIONS     : SE TOMA LA FUNCION DE VERIFICAR INDICADORES CONTABLES Y SE AJUSTA A PRESUPUESTO.
                          JESSICA : CORRECCION SEGUN ESTANDAR DE PROGRAMACION.
      PARAMETERS        : UN_COMPANIA      => COMPANIA EN LA QUE SE EST�? TRABAJANDO.
                          UN_ANIO          => ANIO DEL QUE SE QUIERE REVISAR LOS INDICADORES.
                          UN_CODIGO        => CODIGO DEL PLAN PRESUPUESTAL.
      @NAME:  verificarIndicadoresDeMovimientoPresupuestales
      @METHOD:  GET
    */
  (
    UN_COMPANIA  IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_ANIO      IN PCK_SUBTIPOS.TI_ANIO
   ,UN_CODIGO    IN PCK_SUBTIPOS.TI_CODIGOPPTAL    
  ) 
    RETURN NUMBER  
  AS
    MI_RTA  PCK_SUBTIPOS.TI_COMPANIA;
  BEGIN 
    BEGIN
      SELECT  COMPANIA
      INTO    MI_RTA
      FROM    PLAN_PRESUPUESTAL 
      WHERE   COMPANIA     = UN_COMPANIA 
        AND   ANO          = UN_ANIO 
        AND   CODIGO       = UN_CODIGO
        AND   (MAN_CEN_CTO 
             + MAN_AUX_TER 
             + MAN_AUX_GEN 
             + MAN_AUX_FUE 
             + MAN_AUX_REF 
             + MOVIMIENTO) = 0;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        RETURN 0;
    END;      
    RETURN -1;      
  END FC_VERIFICAR_INDICADORES_PPTO;

  -- 3
  PROCEDURE PR_CREAR_SALDOAUXPRESUPUESTAL 
    /*
      NAME              : CREAR_SALDOAUXPRESUPUESTAL
      AUTHORS           : SYSMAN LTDA
      AUTHOR MIGRACION  : ANDREA PINEDA / CARLOS MANRIQUE 
      DATE MIGRADOR     : 30/04/2013 
      TIME              : 3:30 PM     
      MODIFIER          : SANDRA MILENA DAZA LEGUIZAMON / JAVIER RODRIGUEZ / JOSÉ PASCUAL GOMEZ / JESSICA LISSETH RAMIREZ BRICEÑO /JOSÉ PASCUAL GOMEZ
      DATE MODIFIED     : 27/01/2015 /  / 26/12/2016 / 11/01/2017  /  /  25/04/2017 
      TIME              :  / / / 08:10 AM
      DESCRIPTION       : CREA LAS CUENTAS AUXILIARES DE ACUERDO A LOS VALORES INGRESADOS POR PARAMETRO.
      PARAMETERS        : UN_COMPANIA      => COMPANIA EN LA QUE SE EST�? TRABAJANDO.
                          UN_ANIO          => ANIO DEL QUE SE QUIEREN CREAR LAS CUENTAS AUXILIARES.
                          UN_CODIGO        => CODIGO DEL PLAN PRESUPUESTAL.
                          UN_CENTRO        => CODIGO DEL CENTRO DE COSTO.
                          UN_TERCERO       => NIT DEL TERCERO.
                          UN_SUCURSAL      => SUCURSAL DEL TERCERO.
                          UN_AUXILIAR      => CODIGO DEL AUXILIAR.
                          UN_REFERENCIA    => CODIGO DE LA REFERENCIA.
                          UN_FUENTERECURSO => CODIGO DE LA FUENTE DE RECURSO.
                          UN_NATURALEZA    => TIPO DE NATURALEZA DEL PLAN CONTABLE.
                          UN_IND_DEPURADOS => INDICADOR DEL NUMERO DE DEPURADOS.
      MODIFICATIONS     : MODIFICACION DE LA RUTINA DE CREAR UNA CUENTA CONTABLE Y ADAPTARLA PARA CREAR UN  RUBRO PRESUPUESTAL - 
                          16/12/2016 JARR - SE RETIRA LA PARTE DE CREACION DE LOS SALDOS EN LA TABLA SALDO_PLAN_PPTAL PRA SEPARAR ESTE
                          PROCESO SOLO SE CREAN LOS SALDOS AUXILIARES CUANDO LA CUENTA MANEJA ALGUN INDICADOR. 
                          SE INCLUYE LAS EXCEPCIONES DE NEGOCIO BAJO EL ESTANDAR.
                          JESSICA : CORRECCION SEGUN ESTANDAR DE PROGRAMACION Y REFERENCIACION DE FUNCIONES.
                          JP  : Se incluye la tabla header de las auxiliares
      @NAME:  insertarSaldosAuxiliaresPresupuestales
      @METHOD:  POST
    */
  ( 
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA 
   ,UN_ANIO            IN PCK_SUBTIPOS.TI_ANIO   
   ,UN_CODIGO          IN PCK_SUBTIPOS.TI_CODIGOPPTAL
   ,UN_CENTRO          IN PCK_SUBTIPOS.TI_CENTRO_COSTO 
   ,UN_TERCERO         IN PCK_SUBTIPOS.TI_TERCERO    
   ,UN_SUCURSAL        IN PCK_SUBTIPOS.TI_SUCURSAL
   ,UN_AUXILIAR        IN PCK_SUBTIPOS.TI_AUXILIAR
   ,UN_REFERENCIA      IN PCK_SUBTIPOS.TI_REFERENCIA
   ,UN_FUENTERECURSO   IN PCK_SUBTIPOS.TI_FUENTE_RECURSOS
   ,UN_NATURALEZA      IN PCK_SUBTIPOS.TI_NATURALEZACONTA
   ,UN_IND_DEPURADOS   IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0
  )      
  AS
    MI_CAMPOS        PCK_SUBTIPOS.TI_CAMPOS;    
    MI_VALORES       PCK_SUBTIPOS.TI_VALORES;
    MI_CREA_CUENTA   PCK_SUBTIPOS.TI_ENTERO;
    MI_I             PCK_SUBTIPOS.TI_ENTERO :=-1;  
    MI_MSGERROR      PCK_SUBTIPOS.TI_CLAVEVALOR; 
    MI_RUBRO         TYP_RUBRO_AUX;
    MI_MES           PCK_SUBTIPOS.TI_MES :=0;
  BEGIN
    --SE DEBEN CREAR LAS 14 CUENTAS EN  SALDO_AUX_PPTAL
    --IF UN_IND_DEPURADOS=0 THEN
      MI_RUBRO := FC_VERIFICAR_INDICADORES_PPTO(UN_COMPANIA      => UN_COMPANIA,
                                                UN_ANIO          => UN_ANIO,
                                                UN_MES           => MI_MES,
                                                UN_CODIGO        => UN_CODIGO,
                                                UN_CENTRO        => UN_CENTRO,
                                                UN_TERCERO       => UN_TERCERO,
                                                UN_SUCURSAL      => UN_SUCURSAL,
                                                UN_AUXILIAR      => UN_AUXILIAR,
                                                UN_REFERENCIA    => UN_REFERENCIA,
                                                UN_FUENTERECURSO => UN_FUENTERECURSO);   
    /*
    ELSE
      MI_RUBRO.MI_COMPANIA     :=UN_COMPANIA;
      MI_RUBRO.MI_ANIO         :=UN_ANIO;
      MI_RUBRO.MI_MES          :=MI_MES;
      MI_RUBRO.MI_CODIGO       :=UN_CODIGO;
      MI_RUBRO.MI_NATURALEZA   :=UN_NATURALEZA;
      MI_RUBRO.MI_CENTROCOSTO  :=UN_CENTRO;
      MI_RUBRO.MI_TERCERO      :=UN_TERCERO;
      MI_RUBRO.MI_SUCURSAL     :=UN_SUCURSAL;
      MI_RUBRO.MI_AUXILIAR     :=UN_AUXILIAR;
      MI_RUBRO.MI_REFERENCIA   :=UN_REFERENCIA;
      MI_RUBRO.MI_FUENTERECURSO:=UN_FUENTERECURSO;
    END IF;
    */
    --Se crea la cuenta auxiliar en el header si aun no existe
    BEGIN
      SELECT COUNT(1)
      INTO   MI_CREA_CUENTA
      FROM   PLAN_PPTAL_CONFIG 
      WHERE  COMPANIA       = UN_COMPANIA 
        AND  ANO            = UN_ANIO 
        AND  CODIGO         = UN_CODIGO
        AND  CENTRO_COSTO   = MI_RUBRO.MI_CENTROCOSTO
        AND  TERCERO        = MI_RUBRO.MI_TERCERO
        AND  SUCURSAL       = MI_RUBRO.MI_SUCURSAL
        AND  AUXILIAR       = MI_RUBRO.MI_AUXILIAR
        AND  REFERENCIA     = MI_RUBRO.MI_REFERENCIA
        AND  FUENTE_RECURSO = MI_RUBRO.MI_FUENTERECURSO; 
      EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_CREA_CUENTA:=0;
    END;
    IF MI_CREA_CUENTA=0 THEN 
      BEGIN
        BEGIN
            MI_CAMPOS := 'COMPANIA
                         ,ANO
                         ,CODIGO
                         ,CENTRO_COSTO
                         ,TERCERO
                         ,SUCURSAL
                         ,AUXILIAR
                         ,REFERENCIA
                         ,FUENTE_RECURSO
                         ,NATURALEZA
                         ,TIPOVIGENCIA 
                         ,DESTINO';
            MI_VALORES := ''''      || UN_COMPANIA || 
                          ''','     || UN_ANIO || 
                          ','''     || UN_CODIGO || 
                          ''','''   || MI_RUBRO.MI_CENTROCOSTO || 
                          ''','''   || MI_RUBRO.MI_TERCERO || 
                          ''','''   || MI_RUBRO.MI_SUCURSAL || 
                          ''','''   || MI_RUBRO.MI_AUXILIAR || 
                          ''','''   || MI_RUBRO.MI_REFERENCIA || 
                          ''','''   || MI_RUBRO.MI_FUENTERECURSO || 
                          ''','''   || MI_RUBRO.MI_NATURALEZA ||
                          ''','''   || MI_RUBRO.MI_TIPOVIGENCIA ||
                          ''','''   || MI_RUBRO.MI_DESTINO || ''''; 
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'PLAN_PPTAL_CONFIG', 
                                                   UN_ACCION     => 'I', 
                                                   UN_CAMPOS     => MI_CAMPOS, 
                                                   UN_VALORES    => MI_VALORES);            
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;
        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
          MI_MSGERROR(1).CLAVE := 'CENTRO_COSTO';
          MI_MSGERROR(1).VALOR := MI_RUBRO.MI_CENTROCOSTO ;
          MI_MSGERROR(2).CLAVE := 'RUBRO';
          MI_MSGERROR(2).VALOR := UN_CODIGO;
          MI_MSGERROR(3).CLAVE := 'AUXILIAR';
          MI_MSGERROR(3).VALOR := MI_RUBRO.MI_AUXILIAR;
          MI_MSGERROR(4).CLAVE := 'TERCERO';
          MI_MSGERROR(4).VALOR := MI_RUBRO.MI_TERCERO;
          MI_MSGERROR(5).CLAVE := 'REFERENCIA';
          MI_MSGERROR(5).VALOR := MI_RUBRO.MI_REFERENCIA;
          MI_MSGERROR(6).CLAVE := 'FUENTERECURSO';
          MI_MSGERROR(6).VALOR := MI_RUBRO.MI_FUENTERECURSO;
          MI_MSGERROR(7).CLAVE := 'TIPOVIGENCIA';
          MI_MSGERROR(7).VALOR := MI_RUBRO.MI_TIPOVIGENCIA;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_CREANDO_SALDOS_CONF,
            UN_TABLAERROR => 'PLAN_PPTAL_CONFIG',
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END;
    END IF;   

    BEGIN
      SELECT COUNT(1)
      INTO   MI_CREA_CUENTA
      FROM   SALDO_AUX_PPTAL 
      WHERE  COMPANIA       = UN_COMPANIA 
        AND  ANO            = UN_ANIO 
        AND  CODIGO         = UN_CODIGO
        AND  CENTRO_COSTO   = MI_RUBRO.MI_CENTROCOSTO
        AND  TERCERO        = MI_RUBRO.MI_TERCERO
        AND  SUCURSAL       = MI_RUBRO.MI_SUCURSAL
        AND  AUXILIAR       = MI_RUBRO.MI_AUXILIAR
        AND  REFERENCIA     = MI_RUBRO.MI_REFERENCIA
        AND  FUENTE_RECURSO = MI_RUBRO.MI_FUENTERECURSO; 
      EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_CREA_CUENTA:=0;
    END;

    IF MI_CREA_CUENTA=0 THEN 
      BEGIN
        <<INSERTASALDOS>>
        FOR MI_I IN 0 .. 13 LOOP
          MI_CAMPOS := 'COMPANIA
                       ,ANO
                       ,CODIGO
                       ,MES
                       ,CENTRO_COSTO
                       ,TERCERO
                       ,SUCURSAL
                       ,AUXILIAR
                       ,REFERENCIA
                       ,FUENTE_RECURSO
                       ,NATURALEZA';
          MI_VALORES := ''''    || UN_COMPANIA || 
                        ''','   || UN_ANIO || 
                        ','''   || UN_CODIGO || 
                        ''','   || MI_I || 
                        ','''   || MI_RUBRO.MI_CENTROCOSTO || 
                        ''',''' || MI_RUBRO.MI_TERCERO || 
                        ''',''' || MI_RUBRO.MI_SUCURSAL || 
                        ''',''' || MI_RUBRO.MI_AUXILIAR || 
                        ''',''' || MI_RUBRO.MI_REFERENCIA || 
                        ''',''' || MI_RUBRO.MI_FUENTERECURSO || 
                        ''',''' || MI_RUBRO.MI_NATURALEZA || ''''; 
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'SALDO_AUX_PPTAL', 
                                                 UN_ACCION     => 'I', 
                                                 UN_CAMPOS     => MI_CAMPOS, 
                                                 UN_VALORES    => MI_VALORES);            
        END LOOP INSERTASALDOS;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
      END;
    END IF;       
    EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
      MI_MSGERROR(1).CLAVE := 'MES';
      MI_MSGERROR(1).VALOR := MI_I;
      MI_MSGERROR(2).CLAVE := 'RUBRO';
      MI_MSGERROR(2).VALOR := UN_CODIGO;
      MI_MSGERROR(3).CLAVE := 'AUXILIAR';
      MI_MSGERROR(3).VALOR := MI_RUBRO.MI_AUXILIAR;
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD    => SQLCODE,
        UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_CREANDO_SALDOS,
        UN_TABLAERROR => 'SALDO_AUX_PPTAL',
        UN_REEMPLAZOS => MI_MSGERROR
      );
  END PR_CREAR_SALDOAUXPRESUPUESTAL;

  -- 4
  PROCEDURE PR_ACTPPTO 
    /*
      NAME              : ACTPPTO
      AUTHORS           : SYSMAN LTDA
      AUTHOR MIGRACION  : SANDRA MILENA DAZA LEGUIZAMON / JAVIER VILLATE
      DATE MIGRADOR     : 27/01/2015
      TIME              : 09:00 AM
      MODIFIER          : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MODIFIED     : 11/01/2017
      TIME              : 11:35 AM
      DESCRIPTION       : REALIZA ACTUALIZACIONES DE PRESUPUESTO
      PARAMETERS        : UN_TIPO_CPTE     => TIPO DE COMPROBANTE PRESUPUESTAL.
                          UN_COMPANIA      => COMPANIA EN LA QUE SE EST�? TRABAJANDO.
                          UN_FECHA         => FECHA EN EL QUE SE QUIERE ACTUALIZAR LOS VALORES.
                          UN_CODIGO        => CODIGO DEL PLAN PRESUPUESTAL.
                          UN_CENTRO        => CODIGO DEL CENTRO DE COSTO.
                          UN_TERCERO       => NIT DEL TERCERO.
                          UN_SUCURSAL      => SUCURSAL DEL TERCERO.
                          UN_AUXILIAR      => CODIGO DEL AUXILIAR.
                          UN_REFERENCIA    => CODIGO DE LA REFERENCIA.
                          UN_FUENTE        => CODIGO DE LA FUENTE DE RECURSO.
                          UN_NATURALEZA    => TIPO DE NATURALEZA DEL PLAN CONTABLE.
                          UN_DEBITO_ANT    => MONTO CORRESPONDIENTE AL DEBITO ANTERIOR.
                          UN_CREDITO_ANT   => MONTO CORRESPONDIENTE AL CREDITO ANTERIOR.
                          UN_DEBITO        => MONTO NUEVO DE DEBITO.
                          UN_CREDITO       => MONTO NUEVO DE CREDITO.
                          UN_DIFERENCIA    => NUMERO CORRESPONDIENTE A LA NUEVA DIFERENCIA ENTRE DEBITO Y CREDITO.
                          UN_DIFERENCIAANT => NUMERO CORRESPONDIENTE A LA DIFERENCIA ENTRE DEBITO ANTERIOR Y CREDITO ANTERIOR.
                          UN_TIPO          => TIPO DE COMPROBANTE.
                          UN_TIPOINGRESO   => TIPO DE COMPROBANTE DE INGRESO.
      MODIFICATIONS     : CORRECCION SEGUN ESTANDAR DE PROGRAMACION Y REFERENCIACION DE FUNCIONES.
      @NAME:  prepararActualizacionPresupuesto
      @METHOD:  POST       
    */
  (
    UN_TIPO_CPTE      IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE   
   ,UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA 
   ,UN_FECHA          IN DATE
   ,UN_CODIGO         IN PCK_SUBTIPOS.TI_CODIGOPPTAL
   ,UN_CENTRO         IN PCK_SUBTIPOS.TI_CENTRO_COSTO
   ,UN_TERCERO        IN PCK_SUBTIPOS.TI_TERCERO
   ,UN_SUCURSAL       IN PCK_SUBTIPOS.TI_SUCURSAL
   ,UN_AUXILIAR       IN PCK_SUBTIPOS.TI_AUXILIAR
   ,UN_REFERENCIA     IN PCK_SUBTIPOS.TI_REFERENCIA
   ,UN_FUENTE         IN PCK_SUBTIPOS.TI_FUENTE_RECURSOS
   ,UN_NATURALEZA     IN PCK_SUBTIPOS.TI_NATURALEZACONTA
   ,UN_DEBITO_ANT     IN PCK_SUBTIPOS.TI_DOBLE
   ,UN_CREDITO_ANT    IN PCK_SUBTIPOS.TI_DOBLE
   ,UN_DEBITO         IN PCK_SUBTIPOS.TI_DOBLE
   ,UN_CREDITO        IN PCK_SUBTIPOS.TI_DOBLE
   ,UN_DIFERENCIA     IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0
   ,UN_DIFERENCIAANT  IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0
   ,UN_TIPO           IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE DEFAULT 'N'
   ,UN_TIPOINGRESO    IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE DEFAULT 'E'
  )
  AS 
    MI_RUBRO          TYP_RUBRO_AUX; 
    MI_ANIO           PCK_SUBTIPOS.TI_ANIO;
    MI_MES            PCK_SUBTIPOS.TI_MES;
    MI_CLASE          VARCHAR2(32000 CHAR);
    MI_DIFERENCIAANT  PCK_SUBTIPOS.TI_DOBLE;
    MI_DIFERENCIA     PCK_SUBTIPOS.TI_DOBLE;
  BEGIN 
    BEGIN
      SELECT CLASE 
      INTO   MI_CLASE
      FROM   TIPO_COMPROBPP
      WHERE  COMPANIA = UN_COMPANIA
        AND  CODIGO   = UN_TIPO_CPTE;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_CLASE:=UN_TIPO_CPTE;
    END;
    MI_ANIO  := TO_CHAR(UN_FECHA, 'YYYY');
    MI_MES   := TO_CHAR(UN_FECHA, 'MM');
    MI_RUBRO := FC_VERIFICAR_INDICADORES_PPTO(UN_COMPANIA      => UN_COMPANIA,
                                              UN_ANIO          => MI_ANIO,
                                              UN_MES           => MI_MES,
                                              UN_CODIGO        => UN_CODIGO,
                                              UN_CENTRO        => UN_CENTRO,
                                              UN_TERCERO       => UN_TERCERO,
                                              UN_SUCURSAL      => UN_SUCURSAL,
                                              UN_AUXILIAR      => UN_AUXILIAR,
                                              UN_REFERENCIA    => UN_REFERENCIA,
                                              UN_FUENTERECURSO => UN_FUENTE);   
    MI_DIFERENCIAANT := CASE 
                          WHEN UN_NATURALEZA = 'D' 
                          THEN UN_DEBITO_ANT  - UN_CREDITO_ANT 
                          ELSE UN_CREDITO_ANT - UN_DEBITO_ANT 
                        END;
    MI_DIFERENCIA := CASE 
                       WHEN UN_NATURALEZA = 'D' 
                       THEN UN_DEBITO  - UN_CREDITO
                       ELSE UN_CREDITO - UN_DEBITO
                     END;
    PR_ACTPPTO0AUX(UN_CLASE         => MI_CLASE, 
                   UN_COMPANIA      => UN_COMPANIA,
                   UN_ANIO          => MI_ANIO,
                   UN_CODIGO        => UN_CODIGO,
                   UN_TERCERO       => MI_RUBRO.MI_TERCERO,
                   UN_SUCURSAL      => MI_RUBRO.MI_SUCURSAL,
                   UN_AUXILIAR      => MI_RUBRO.MI_AUXILIAR,
                   UN_CENTRO        => MI_RUBRO.MI_CENTROCOSTO,
                   UN_REFERENCIA    => MI_RUBRO.MI_REFERENCIA,
                   UN_FUENTERECURSO => MI_RUBRO.MI_FUENTERECURSO,
                   UN_MES           => MI_MES,
                   UN_DEBITO        => NVL(UN_DEBITO,0),
                   UN_CREDITO       => NVL(UN_CREDITO,0),
                   UN_DEBITO_ANT    => NVL(UN_DEBITO_ANT,0),
                   UN_CREDITO_ANT   => NVL(UN_CREDITO_ANT,0),
                   UN_NATURALEZA    => MI_RUBRO.MI_NATURALEZA,
                   UN_DIFERENCIA    => MI_DIFERENCIA,
                   UN_DIFERENCIAANT => MI_DIFERENCIAANT,
                   UN_TIPO          => UN_TIPO,
                   UN_TIPOINGRESO   => UN_TIPOINGRESO); 
    IF MI_CLASE = 'ING' THEN
      PR_ACTPPTO0AUX(UN_CLASE         => 'OIN', 
                     UN_COMPANIA      => UN_COMPANIA,
                     UN_ANIO          => MI_ANIO,
                     UN_CODIGO        => UN_CODIGO,
                     UN_TERCERO       => MI_RUBRO.MI_TERCERO,
                     UN_SUCURSAL      => MI_RUBRO.MI_SUCURSAL,
                     UN_AUXILIAR      => MI_RUBRO.MI_AUXILIAR,
                     UN_CENTRO        => MI_RUBRO.MI_CENTROCOSTO,
                     UN_REFERENCIA    => MI_RUBRO.MI_REFERENCIA,
                     UN_FUENTERECURSO => MI_RUBRO.MI_FUENTERECURSO,
                     UN_MES           => MI_MES,
                     UN_DEBITO        => NVL(UN_DEBITO,0),
                     UN_CREDITO       => NVL(UN_CREDITO,0),
                     UN_DEBITO_ANT    => NVL(UN_DEBITO_ANT,0),
                     UN_CREDITO_ANT   => NVL(UN_CREDITO_ANT,0),
                     UN_NATURALEZA    => MI_RUBRO.MI_NATURALEZA,
                     UN_DIFERENCIA    => MI_DIFERENCIA,
                     UN_DIFERENCIAANT => MI_DIFERENCIAANT,
                     UN_TIPO          => UN_TIPO,
                     UN_TIPOINGRESO   => UN_TIPOINGRESO); 
    ELSIF  MI_CLASE ='AIN' OR MI_CLASE = 'DIN' THEN
      PR_ACTPPTO0AUX(UN_CLASE         => 'MOI', 
                     UN_COMPANIA      => UN_COMPANIA,
                     UN_ANIO          => MI_ANIO,
                     UN_CODIGO        => UN_CODIGO,
                     UN_TERCERO       => MI_RUBRO.MI_TERCERO,
                     UN_SUCURSAL      => MI_RUBRO.MI_SUCURSAL,
                     UN_AUXILIAR      => MI_RUBRO.MI_AUXILIAR,
                     UN_CENTRO        => MI_RUBRO.MI_CENTROCOSTO,
                     UN_REFERENCIA    => MI_RUBRO.MI_REFERENCIA,
                     UN_FUENTERECURSO => MI_RUBRO.MI_FUENTERECURSO,
                     UN_MES           => MI_MES,
                     UN_DEBITO        => NVL(UN_DEBITO,0),
                     UN_CREDITO       => NVL(UN_CREDITO,0),
                     UN_DEBITO_ANT    => NVL(UN_DEBITO_ANT,0),
                     UN_CREDITO_ANT   => NVL(UN_CREDITO_ANT,0),
                     UN_NATURALEZA    => MI_RUBRO.MI_NATURALEZA,
                     UN_DIFERENCIA    => MI_DIFERENCIA,
                     UN_DIFERENCIAANT => MI_DIFERENCIAANT,
                     UN_TIPO          => UN_TIPO,
                     UN_TIPOINGRESO   => UN_TIPOINGRESO);    
    END IF;
  END PR_ACTPPTO;

  -- 5
  PROCEDURE PR_ACTPPTO0AUX
    /*
      NAME              : PR_ACTPPTO0AUX
      AUTHORS           : SYSMAN LTDA
      AUTHOR MIGRACION  : SANDRA MILENA DAZA LEGUIZAMON
      DATE MIGRADOR     : 19/01/2015
      TIME              : 2:00 PM 
      MODIFIER          : SANDRA MILENA DAZA LEGUIZAMON / JOSE PASCUAL GOMEZ BLANCO / ADRIANA CACERES / JAVIER VILLATE / JOSE PASCUAL GOMEZ BLANCO / JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MODIFIED     : 23/01/2015 / 27/01/2015 / 17/11/2016 / 26/12/2016 /  / 11/01/2017
      TIME              :  / / / / / 9:30 AM
      DESCRIPTION       : ACTUALIZA LOS VALORES DE SALDO_AUX_CONTABLE
      PARAMETERS        : UN_CLASE         => CLASE DEL TIPO DE COMPROBANTE PRESUPUESTAL.
                          UN_COMPANIA      => COMPANIA EN LA QUE SE EST�? TRABAJANDO.
                          UN_ANIO          => ANIO EN EL QUE SE QUIERE ACTUALIZAR LOS VALORES.
                          UN_CODIGO        => CODIGO DEL PLAN PRESUPUESTAL.
                          UN_TERCERO       => NIT DEL TERCERO.
                          UN_SUCURSAL      => SUCURSAL DEL TERCERO.
                          UN_AUXILIAR      => CODIGO DEL AUXILIAR.
                          UN_CENTRO        => CODIGO DEL CENTRO DE COSTO.
                          UN_REFERENCIA    => CODIGO DE LA REFERENCIA.
                          UN_FUENTERECURSO => CODIGO DE LA FUENTE DE RECURSO.
                          UN_MES           => MES EN EL QUE SE QUIERE ACTUALIZAR LOS VALORES.
                          UN_DEBITO        => MONTO NUEVO DE DEBITO.
                          UN_CREDITO       => MONTO NUEVO DE CREDITO.
                          UN_DEBITO_ANT    => MONTO CORRESPONDIENTE AL DEBITO ANTERIOR.
                          UN_CREDITO_ANT   => MONTO CORRESPONDIENTE AL CREDITO ANTERIOR.
                          UN_NATURALEZA    => TIPO DE NATURALEZA DEL PLAN CONTABLE.
                          UN_DIFERENCIA    => NUMERO CORRESPONDIENTE A LA NUEVA DIFERENCIA ENTRE DEBITO Y CREDITO.
                          UN_DIFERENCIAANT => NUMERO CORRESPONDIENTE A LA DIFERENCIA ENTRE DEBITO ANTERIOR Y CREDITO ANTERIOR.
                          UN_TIPO          => TIPO DE COMPROBANTE.
                          UN_TIPOINGRESO   => TIPO DE COMPROBANTE DE INGRESO.
                          UN_IND_DEPURADOS => INDICADOR DEL NUMERO DE DEPURADOS.
      MODIFICATIONS     : INCLUIR MANEJO DE ERRORES / MODIFICACION MANEJO DE EXCEPCIONES  
                          JP. OPTIMZACIÓN DE MANEJO DE EXCEPCIONES Y LLAMADO DE FUNCIONES PR_CREAR_SALDOAUXPRESUPUESTAL Y PR_CREAR_SALDOPLANPPTAL
                          JESSICA : CORRECCION SEGUN ESTANDAR DE PROGRAMACION Y REFERENCIACION DE FUNCIONES.
      @NAME:  actualizarAuxiliaresContables
      @METHOD:  POST
    */
  ( 
    UN_CLASE          IN PCK_SUBTIPOS.TI_CLASECOMPROPPTO 
   ,UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA 
   ,UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO 
   ,UN_CODIGO         IN PCK_SUBTIPOS.TI_CODIGOPPTAL
   ,UN_TERCERO        IN PCK_SUBTIPOS.TI_TERCERO 
   ,UN_SUCURSAL       IN PCK_SUBTIPOS.TI_SUCURSAL
   ,UN_AUXILIAR       IN PCK_SUBTIPOS.TI_AUXILIAR
   ,UN_CENTRO         IN PCK_SUBTIPOS.TI_CENTRO_COSTO 
   ,UN_REFERENCIA     IN PCK_SUBTIPOS.TI_REFERENCIA
   ,UN_FUENTERECURSO  IN PCK_SUBTIPOS.TI_FUENTE_RECURSOS 
   ,UN_MES            IN PCK_SUBTIPOS.TI_MES
   ,UN_DEBITO         IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0
   ,UN_CREDITO        IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0
   ,UN_DEBITO_ANT     IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0 
   ,UN_CREDITO_ANT    IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0
   ,UN_NATURALEZA     IN PCK_SUBTIPOS.TI_NATURALEZACONTA
   ,UN_DIFERENCIA     IN PCK_SUBTIPOS.TI_DOBLE   
   ,UN_DIFERENCIAANT  IN PCK_SUBTIPOS.TI_DOBLE
   ,UN_TIPO           IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE DEFAULT 'N'
   ,UN_TIPOINGRESO    IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE DEFAULT 'E'
   ,UN_IND_DEPURADOS  IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0  
  )
  AS
    MI_COLUMNADEBITO            PCK_SUBTIPOS.TI_STRSQL;
    MI_COLUMNACREDITO           PCK_SUBTIPOS.TI_STRSQL;
    MI_CONDICIONAUX             PCK_SUBTIPOS.TI_CONDICION;
    MI_CONDICIONSALDO           PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS                   PCK_SUBTIPOS.TI_CAMPOS;
    MI_CAMPOSDIS                PCK_SUBTIPOS.TI_CAMPOS;
    MI_TABLA                    PCK_SUBTIPOS.TI_TABLA:='  ';
    MI_RUBRO                    TYP_RUBRO_AUX;
    MI_CREDITO                  PCK_SUBTIPOS.TI_STRSQL;
    MI_DEBITO                   PCK_SUBTIPOS.TI_STRSQL;
    MI_MSGERROR                 PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN   
    --si los indicadores no vienen depurados los depura
    IF UN_IND_DEPURADOS = 0 THEN
      MI_RUBRO := FC_VERIFICAR_INDICADORES_PPTO(UN_COMPANIA      => UN_COMPANIA,
                                                UN_ANIO          => UN_ANIO,
                                                UN_MES           => UN_MES,
                                                UN_CODIGO        => UN_CODIGO,
                                                UN_CENTRO        => UN_CENTRO,
                                                UN_TERCERO       => UN_TERCERO,
                                                UN_SUCURSAL      => UN_SUCURSAL,
                                                UN_AUXILIAR      => UN_AUXILIAR,
                                                UN_REFERENCIA    => UN_REFERENCIA,
                                                UN_FUENTERECURSO => UN_FUENTERECURSO);   
    ELSE
      MI_RUBRO.MI_COMPANIA     :=UN_COMPANIA;
      MI_RUBRO.MI_ANIO         :=UN_ANIO;
      MI_RUBRO.MI_MES          :=UN_MES;
      MI_RUBRO.MI_CODIGO       :=UN_CODIGO;
      MI_RUBRO.MI_NATURALEZA   :=UN_NATURALEZA;
      MI_RUBRO.MI_CENTROCOSTO  :=UN_CENTRO;
      MI_RUBRO.MI_TERCERO      :=UN_TERCERO;
      MI_RUBRO.MI_SUCURSAL     :=UN_SUCURSAL;
      MI_RUBRO.MI_AUXILIAR     :=UN_AUXILIAR;
      MI_RUBRO.MI_REFERENCIA   :=UN_REFERENCIA;
      MI_RUBRO.MI_FUENTERECURSO:=UN_FUENTERECURSO;
    END IF;
   PR_CREAR_SALDOAUXPRESUPUESTAL(UN_COMPANIA        => MI_RUBRO.MI_COMPANIA,  
                                  UN_ANIO            => MI_RUBRO.MI_ANIO,     
                                  UN_CODIGO          => MI_RUBRO.MI_CODIGO,
                                  UN_CENTRO          => MI_RUBRO.MI_CENTROCOSTO, 
                                  UN_TERCERO         => MI_RUBRO.MI_TERCERO,     
                                  UN_SUCURSAL        => MI_RUBRO.MI_SUCURSAL,
                                  UN_AUXILIAR        => MI_RUBRO.MI_AUXILIAR,
                                  UN_REFERENCIA      => MI_RUBRO.MI_REFERENCIA,
                                  UN_FUENTERECURSO   => MI_RUBRO.MI_FUENTERECURSO,
                                  UN_NATURALEZA      => MI_RUBRO.MI_NATURALEZA,
                                  UN_IND_DEPURADOS   => -1);
    PR_CREAR_SALDOPLANPPTAL(UN_COMPANIA => MI_RUBRO.MI_COMPANIA,  
                            UN_ANIO     => MI_RUBRO.MI_ANIO,     
                            UN_CODIGO   => MI_RUBRO.MI_CODIGO);

    --no trae debitos o creditos no se actualizan saldos
    IF (UN_DEBITO<>0 OR UN_CREDITO<>0 OR UN_CREDITO_ANT<>0 OR UN_DEBITO_ANT<>0) THEN        
      BEGIN
        BEGIN   
          SELECT COLUMNADEBITO
                ,COLUMNACREDITO 
          INTO   MI_COLUMNADEBITO
                ,MI_COLUMNACREDITO
          FROM   CLASECNTPRES
          WHERE  CODIGO = UN_CLASE;
          EXCEPTION WHEN NO_DATA_FOUND THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;        
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
          MI_MSGERROR(1).CLAVE := 'CLASE';
          MI_MSGERROR(1).VALOR := UN_CLASE;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_CLASE_SIN_CONFI,
            UN_TABLAERROR => 'CLASECNTPRES',
            UN_REEMPLAZOS => MI_MSGERROR
          );  
      END;
      BEGIN
        BEGIN
          MI_COLUMNADEBITO  := UPPER(MI_COLUMNADEBITO);
          MI_COLUMNACREDITO := UPPER(MI_COLUMNACREDITO);
          MI_DEBITO   := SUBSTR(MI_COLUMNADEBITO , 2, LENGTH(MI_COLUMNADEBITO)  - 2);
          MI_CREDITO  := SUBSTR(MI_COLUMNACREDITO, 2, LENGTH(MI_COLUMNACREDITO) - 2);  
          IF LPAD(MI_DEBITO, 3) = 'IIF' THEN
            IF UN_CLASE = 'OIN' OR UN_CLASE = 'MOI' THEN 
              MI_DEBITO  := REPLACE(MI_DEBITO,  'TIPO', '''' || UN_TIPOINGRESO || '''');
              MI_DEBITO  := REPLACE(MI_DEBITO,  CHR(34), '''''');
              MI_DEBITO  := REPLACE(MI_DEBITO,  'CHR(69)', '''E'''); 
              MI_CREDITO := REPLACE(MI_CREDITO, 'TIPO', '''' || UN_TIPOINGRESO || '''');
              MI_CREDITO := REPLACE(MI_CREDITO, CHR(34), '''''');
              MI_CREDITO := REPLACE(MI_CREDITO, 'CHR(69)', '''E'''); 
            ELSE   
              MI_DEBITO  := REPLACE(MI_DEBITO,  'TIPO', NVL('''' || UN_TIPO || '''', 'C'));   
              MI_DEBITO  := REPLACE(MI_DEBITO,  CHR(34), '''''');  
              MI_DEBITO  := REPLACE(MI_DEBITO,  'CHR(67)', '''C'''); 
              MI_CREDITO := REPLACE(MI_DEBITO,  'TIPO', NVL('''' || UN_TIPO || '''', 'C'));   
              MI_CREDITO := REPLACE(MI_CREDITO, CHR(34), '''''');
              MI_CREDITO := REPLACE(MI_CREDITO, 'CHR(67)', '''C'''); 
            END IF;
            MI_DEBITO  := PCK_SYSMAN_UTL.FC_IIFPPTO(MI_DEBITO);
            MI_CREDITO := PCK_SYSMAN_UTL.FC_IIFPPTO(MI_CREDITO);
            MI_DEBITO  := REPLACE(MI_DEBITO, '''', '');
            MI_CREDITO := REPLACE(MI_CREDITO, '''', '');
          END IF;    
          --ACTUALIZA LOS SALDOS AUXILIARES
          MI_CONDICIONAUX := ' COMPANIA           = ''' || MI_RUBRO.MI_COMPANIA || '''' ||
                             ' AND ANO            = '   || MI_RUBRO.MI_ANIO ||                
                             ' AND MES            = '   || UN_MES || 
                             ' AND CODIGO         = ''' || MI_RUBRO.MI_CODIGO || '''' ||
                             ' AND TERCERO        = ''' || MI_RUBRO.MI_TERCERO || '''' ||
                             ' AND SUCURSAL       = ''' || MI_RUBRO.MI_SUCURSAL || '''' ||
                             ' AND CENTRO_COSTO   = ''' || MI_RUBRO.MI_CENTROCOSTO || '''' ||
                             ' AND AUXILIAR       = ''' || MI_RUBRO.MI_AUXILIAR || '''' ||
                             ' AND REFERENCIA     = ''' || MI_RUBRO.MI_REFERENCIA || '''' ||
                             ' AND FUENTE_RECURSO = ''' || MI_RUBRO.MI_FUENTERECURSO || '''';
          MI_CONDICIONSALDO := ' COMPANIA   = '''        || UN_COMPANIA || '''' ||
                               ' AND ANO    = '          || UN_ANIO ||    
                               ' AND MES    = '          || UN_MES ||    
                               ' AND CODIGO BETWEEN '''  || SUBSTR(UN_CODIGO, 1,1) || ''' AND ''' || SUBSTR(UN_CODIGO, 1,LENGTH(UN_CODIGO)) || '''' ||
                               ' AND CODIGO = SUBSTR(''' || UN_CODIGO || ''',1,LENGTH(CODIGO))';  
          IF MI_DEBITO <>  MI_CREDITO THEN
            MI_CAMPOS := '' ||  MI_DEBITO  || '' || '= (' ||  MI_DEBITO || '+' || UN_DEBITO  || ') - ' || UN_DEBITO_ANT ||
                        ',' ||  MI_CREDITO || '' || '= (' || MI_CREDITO || '+' || UN_CREDITO || ') - ' || UN_CREDITO_ANT ;                
          ELSE
            MI_CAMPOS := '' || MI_DEBITO || '' || '= (' || MI_DEBITO || '-(' || UN_DIFERENCIAANT || ')) + (' || UN_DIFERENCIA || ')' ;
            MI_CAMPOSDIS := REPLACE('' || MI_DEBITO || '',''    || UN_CLASE      ||'','') || '= (' || 
                            REPLACE('' || MI_DEBITO || '',''    || UN_CLASE      ||'','') || '- (' || 
                                   UN_DIFERENCIAANT || ')) + (' || UN_DIFERENCIA || ')' ;
          END IF;
          MI_TABLA := 'SALDO_AUX_PPTAL';
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA, 
                                                 UN_ACCION     => 'M', 
                                                 UN_CAMPOS     => MI_CAMPOS, 
                                                 UN_CONDICION  => MI_CONDICIONAUX);   
          IF MI_DEBITO = MI_CREDITO AND UN_CLASE IN('ADD','ADR','ARO','DMD','DMR','DRO') THEN
         
				PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
													   UN_ACCION    => 'M', 
													   UN_CAMPOS    => MI_CAMPOSDIS, 
													   UN_CONDICION => MI_CONDICIONAUX);                                       
			
          END IF;                                       
          MI_TABLA := 'SALDO_PLAN_PPTAL';
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA, 
                                                 UN_ACCION     => 'M', 
                                                 UN_CAMPOS     => MI_CAMPOS, 
                                                 UN_CONDICION  => MI_CONDICIONSALDO);            
          IF MI_DEBITO = MI_CREDITO AND UN_CLASE IN('ADD','ADR','ARO','DMD','DMR','DRO') THEN
             
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA, 
                                                         UN_ACCION     => 'M', 
                                                         UN_CAMPOS     => MI_CAMPOSDIS, 
                                                         UN_CONDICION  => MI_CONDICIONSALDO);  
            
          END IF;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END; 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
          MI_MSGERROR(1).CLAVE := 'RUBRO';
          MI_MSGERROR(1).VALOR := NVL(MI_RUBRO.MI_CODIGO,'');
          PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE,
                UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ACTPPTO0AUX,
                UN_TABLAERROR => MI_TABLA,
                UN_REEMPLAZOS => MI_MSGERROR
          );
      END;  
    END IF;
  END PR_ACTPPTO0AUX;

  -- 6    
  PROCEDURE PR_ACTPTO0_MOP
    /*
      NAME              : ACTPTO0_MOP
      AUTHORS           : SYSMAN LTDA
      AUTHOR MIGRACION  : SANDRA MILENA DAZA LEGUIZAMON
      DATE MIGRADOR     : 06/07/2016
      TIME              : 11:00 AM
      MODIFIER          : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MODIFIED     : 11/01/2017
      TIME              : 02:20 PM
      DESCRIPTION       : CREA EL COMPROBANTE DE MODIFICACION PRESUPUESTRAL.
      PARAMETERS        : UN_CLASE         => CLASE DEL TIPO DE COMPROBANTE.
                          UN_COMPANIA      => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_MODULO        => MODULO EN EL QUE SE ESTA TRABAJANDO.
                          UN_ANIO          => ANIO EN EL QUE SE QUIERE ACTUALIZAR LOS VALORES.
                          UN_MES           => MES EN EL QUE SE QUIERE ACTUALIZAR LOS VALORES.
                          UN_CODIGO        => CODIGO DEL PLAN.
                          UN_DEBITOANT     => MONTO CORRESPONDIENTE AL DEBITO ANTERIOR.
                          UN_CREDITOANT    => MONTO CORRESPONDIENTE AL CREDITO ANTERIOR.
                          UN_DEBITO        => MONTO NUEVO DE DEBITO.
                          UN_CREDITO       => MONTO NUEVO DE CREDITO.
                          UN_DIFERENCIA    => NUMERO CORRESPONDIENTE A LA NUEVA DIFERENCIA.
                          UN_DIFERENCIAANT => NUMERO CORRESPONDIENTE A LA DIFERENCIA.
                          UN_TIPO          => TIPO DE COMPROBANTE.
                          UN_TIPOINGRESO   => TIPO DE COMPROBANTE DE INGRESO.
                          UN_ANIOCPTE      => ANIO DEL COMPROBANTE.
                          UN_TIPOCPTE      => TIPO DE COMPROBANTE.
                          UN_NROCPTE       => NUMERO DE COMPROBANTE CONTABLE
                          UN_DEBITOCPTE    => MONTO DEBITO DEL COMPROBANTE.
                          UN_CREDITOCPTE   => MONTO CREDITO DEL COMPROBANTE.
                          UN_CTACPTE       => CUENTA DEL COMPROBANTE.
                          UN_CSCCPTE       => NUMERO DEL COMPROBANTE.
      MODIFICATIONS     : SE MODIFICA PARA ELIMINAR EL PROCESO PROPIO CUANDO ES UN MOP, SE CREA OTRO PROCEDIMIENTO 
                          PARA ESE PROCESO  
                          JESSICA : CORRECCION SEGUN ESTANDAR DE PROGRAMACION, REFERENCIACION DE FUNCIONES Y OPTIMIZACION 
                          DE MANEJO DE ERRORES.
      @NAME:  crearComprobanteDeModificacionPresupuestal
      @METHOD:  POST 
    */
  (
    UN_CLASE          IN PCK_SUBTIPOS.TI_CLASECUENTACONTA
   ,UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA 
   ,UN_MODULO         IN PCK_SUBTIPOS.TI_MODULO 
   ,UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO
   ,UN_MES            IN PCK_SUBTIPOS.TI_MES
   ,UN_CODIGO         IN PCK_SUBTIPOS.TI_CODIGOPPTAL  
   ,UN_DEBITOANT      IN PCK_SUBTIPOS.TI_DOBLE
   ,UN_CREDITOANT     IN PCK_SUBTIPOS.TI_DOBLE 
   ,UN_DEBITO         IN PCK_SUBTIPOS.TI_DOBLE 
   ,UN_CREDITO        IN PCK_SUBTIPOS.TI_DOBLE 
   ,UN_DIFERENCIA     IN PCK_SUBTIPOS.TI_DOBLE  
   ,UN_DIFERENCIAANT  IN PCK_SUBTIPOS.TI_DOBLE
   ,UN_TIPO           IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE DEFAULT 'C'
   ,UN_TIPOINGRESO    IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE DEFAULT 'E'
   ,UN_ANIOCPTE       IN PCK_SUBTIPOS.TI_ANIO
   ,UN_TIPOCPTE       IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE DEFAULT 'X'
   ,UN_NROCPTE        IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT DEFAULT 0
   ,UN_DEBITOCPTE     IN PCK_SUBTIPOS.TI_DOBLE
   ,UN_CREDITOCPTE    IN PCK_SUBTIPOS.TI_DOBLE
   ,UN_CTACPTE        IN VARCHAR2
   ,UN_CSCCPTE        IN PCK_SUBTIPOS.TI_DOBLE
  )
  AS
    MI_DEBITO                      VARCHAR2(32000 CHAR);
    MI_CREDITO                     VARCHAR2(32000 CHAR);
    MI_TABLA                       PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS                      PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                     PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION                   PCK_SUBTIPOS.TI_CONDICION;
    MI_EXCLUIDOS                   PCK_SUBTIPOS.TI_EXCLUIDOS;
    MI_X                           VARCHAR2(1 CHAR) := 'N';
    MI_CSC                         PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_CPTE                        PCK_SUBTIPOS.TI_DOBLE; 
    MI_CREDITOS                    PCK_SUBTIPOS.TI_DOBLE; 
    MI_CONTRACREDITOS              PCK_SUBTIPOS.TI_DOBLE;    
    MI_CREDITOAFECTADO             PCK_SUBTIPOS.TI_DOBLE;   
    MI_CONTRACREDITOAFECTADO       PCK_SUBTIPOS.TI_DOBLE; 
    MI_MODIFICACIONCREDITO         PCK_SUBTIPOS.TI_DOBLE; 
    MI_MODIFICACIONCONTRACREDITO   PCK_SUBTIPOS.TI_DOBLE; 
    MI_CREDITOAFECTADOCNT          PCK_SUBTIPOS.TI_DOBLE; 
    MI_CONTRACREDITOAFECTADOCNT    PCK_SUBTIPOS.TI_DOBLE; 
  BEGIN
    -- CUANDO SE ENVIAN LOS DATOS DE UN TIPO Y NÚMERO DE COMPROBANTE SE MODIFICA CON LOS PAR�?METROS 
    -- PARA OMITIR LA VALIDACIÓN QUE SE TENIA
    IF UN_TIPOCPTE <> 'X' AND PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                    UN_NOMBRE    => 'GENERA MOP EN MODIFICACIONES DE APROPIACION', 
                                                    UN_MODULO    => UN_MODULO,
                                                    UN_FECHA_PAR => SYSDATE) = 'SI' THEN
      IF UN_CLASE = 'ADC' THEN
        MI_DEBITO := 'ADICION';
        MI_CREDITO := 'ADICION';
      ELSE
        IF UN_CLASE = 'RED' THEN
          MI_DEBITO := 'REDUCCION';     
          MI_CREDITO := 'REDUCCION';
        ELSE
          IF UN_CLASE = 'TRA' THEN
            MI_DEBITO := 'TRASLADO_DEBITO';
            MI_CREDITO := 'TRASLADO_CREDITO';    
          END IF;
        END IF;
      END IF;
      IF UN_CLASE = 'TRA' THEN
        MI_TABLA := 'SALDO_PLAN_PPTAL';
        MI_CAMPOS := MI_DEBITO  || '=((' || MI_DEBITO  || ' + ' || UN_DEBITO  || ')-' || UN_DEBITOANT  ||'),' ||
                     MI_CREDITO || '=((' || MI_CREDITO || ' + ' || UN_CREDITO || ')-' || UN_CREDITOANT ||')';
        MI_CONDICION := ' COMPANIA = '''        || UN_COMPANIA || '''' ||
                      ' AND ANO  = '          || UN_ANIO ||    
                      ' AND MES  = '          || UN_MES ||    
                      ' AND CODIGO   BETWEEN '''  || SUBSTR(UN_CODIGO, 1,1) || ''' AND ''' || SUBSTR(UN_CODIGO, 1,LENGTH(UN_CODIGO)) || '''' ||
                      ' AND CODIGO   = SUBSTR(''' || UN_CODIGO || ''',1,LENGTH(CODIGO) )';  
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',      
                                              UN_CAMPOS    => MI_CAMPOS,      
                                              UN_CONDICION => MI_CONDICION);
        MI_DEBITO := 'MODIF_PAC_DEBITO';
        MI_CREDITO := 'MODIF_PAC_CREDITO';
        MI_CAMPOS := MI_DEBITO  || '=((' || MI_DEBITO  || ' + ' || UN_DEBITO  || ')-' || UN_DEBITOANT  || '),' ||
                     MI_CREDITO || '=((' || MI_CREDITO || ' + ' || UN_CREDITO || ')-' || UN_CREDITOANT || ')';
        MI_CONDICION := ' COMPANIA = '''            || UN_COMPANIA || '''' ||
                        ' AND ANO  = '              || UN_ANIO ||    
                        ' AND MES  = '              || UN_MES ||    
                        ' AND CODIGO   BETWEEN '''  || SUBSTR(UN_CODIGO, 1,1) || ''' AND ''' || SUBSTR(UN_CODIGO, 1,LENGTH(UN_CODIGO)) || '''' ||
                        ' AND CODIGO   = SUBSTR(''' || UN_CODIGO || ''',1,LENGTH(CODIGO) )';  
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',      
                                              UN_CAMPOS    => MI_CAMPOS,      
                                              UN_CONDICION => MI_CONDICION);
      ELSE
        MI_TABLA := 'SALDO_PLAN_PPTAL';
        MI_CAMPOS := MI_DEBITO || '=((' || MI_DEBITO || '-' || UN_DIFERENCIAANT || ')+' || UN_DIFERENCIA ||')' ;
        MI_CONDICION := ' COMPANIA = '''            || UN_COMPANIA || '''' ||
                        ' AND ANO  = '              || UN_ANIO ||    
                        ' AND MES  = '              || UN_MES ||    
                        ' AND CODIGO   BETWEEN '''  || SUBSTR(UN_CODIGO, 1,1) || ''' AND ''' || SUBSTR(UN_CODIGO, 1,LENGTH(UN_CODIGO)) || '''' ||
                        ' AND CODIGO   = SUBSTR(''' || UN_CODIGO || ''',1,LENGTH(CODIGO) )';  
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',      
                                              UN_CAMPOS    => MI_CAMPOS,      
                                              UN_CONDICION => MI_CONDICION);
        MI_DEBITO := 'MODIF_PAC_DEBITO';
        MI_CREDITO := 'MODIF_PAC_CREDITO';
        MI_CAMPOS := MI_DEBITO  || '=((' || MI_DEBITO  || ' + ' || UN_DEBITO  || ')-' || UN_DEBITOANT  ||'),' ||
                     MI_CREDITO || '=((' || MI_CREDITO || ' + ' || UN_CREDITO || ')-' || UN_CREDITOANT || ')';
        MI_CONDICION := ' COMPANIA = '''            || UN_COMPANIA || '''' ||
                        ' AND ANO  = '              || UN_ANIO ||    
                        ' AND MES  = '              || UN_MES ||    
                        ' AND CODIGO   BETWEEN '''  || SUBSTR(UN_CODIGO, 1,1) || ''' AND ''' || SUBSTR(UN_CODIGO, 1,LENGTH(UN_CODIGO)) || '''' ||
                        ' AND CODIGO   = SUBSTR(''' || UN_CODIGO || ''',1,LENGTH(CODIGO) )';  
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',      
                                              UN_CAMPOS    => MI_CAMPOS,      
                                              UN_CONDICION => MI_CONDICION);
      END IF;
      IF UN_CLASE = 'ADC' OR UN_CLASE = 'RED' OR UN_CLASE = 'TRA' THEN
        --CONSULTA PARA CALCULAR EL VALOR DEL DOCUMENTO PARA EL MOP
        BEGIN
          SELECT   SUM(VALOR_DEBITO)
                  ,SUM(VALOR_CREDITO)
                  ,SUM(DEBITO_AFECTADO)
                  ,SUM(CREDITO_AFECTADO)
                  ,SUM(MODIFICACION_DEBITO)
                  ,SUM(MODIFICACION_CREDITO)
                  ,SUM(DEBITO_AFECTADOCNT)
                  ,SUM(CREDITO_AFECTADOCNT) 
          INTO     MI_CREDITOS
                  ,MI_CONTRACREDITOS
                  ,MI_CREDITOAFECTADO
                  ,MI_CONTRACREDITOAFECTADO
                  ,MI_MODIFICACIONCREDITO
                  ,MI_MODIFICACIONCONTRACREDITO
                  ,MI_CREDITOAFECTADOCNT
                  ,MI_CONTRACREDITOAFECTADOCNT
          FROM     DETALLE_COMPROBANTE_PPTAL
          WHERE    COMPANIA    = UN_COMPANIA
            AND    ANO         = UN_ANIOCPTE
            AND    TIPO_CPTE   = UN_TIPOCPTE
            AND    COMPROBANTE = UN_NROCPTE;
          EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_CREDITOS                  := 0;
            MI_CONTRACREDITOS            := 0;
            MI_CREDITOAFECTADO           := 0;
            MI_CONTRACREDITOAFECTADO     := 0;
            MI_MODIFICACIONCREDITO       := 0;
            MI_MODIFICACIONCONTRACREDITO := 0;
            MI_CREDITOAFECTADOCNT        := 0;
            MI_CONTRACREDITOAFECTADOCNT  := 0;
        END;
        --SE CONSULTA EL HEADER PARA TRAER LOS DATOS  
        <<CONSULTATRAERDATOS>>
        FOR RSPAC IN (
          SELECT FECHA
                ,FECHA_VENCIMIENTO
                ,DESCRIPCION
                ,TEXTO
                ,NRO_DOCUMENTO
                ,FECHA_VCN_DOC
                ,TERCERO
                ,SUCURSAL
                ,CENTRO_COSTO
                ,AUXILIAR
                ,REFERENCIA
                ,FUENTE_RECURSO
                ,DEBITO_AFECTADO
                ,CREDITO_AFECTADO
                ,DEBITO_AFECTADOCNT
                ,CREDITO_AFECTADOCNT
                ,MODIFICACION_DEBITO
                ,MODIFICACION_CREDITO
                ,ABONADO
                ,ENTREGADO
                ,PAGADOBANCO
                ,IMPRESO
                ,ANULADO
                ,CONTRACTUAL
                ,DESTINO
                ,REGISTROAUTOMATICO
                ,CARGO
                ,DEPENDENCIA
                ,PAPELES
                ,TIPOCONTRATO
                ,NUMEROCONTRATO
                ,SITUACIONFONDOS
                ,CODSOLICITANTE
                ,SUCSOLICITANTE
                ,TIPO_DOCUMENTO 
          FROM   COMPROBANTE_PPTAL 
          WHERE  COMPANIA = UN_COMPANIA 
            AND  ANO      = UN_ANIO
            AND  TIPO     = UN_TIPOCPTE 
            AND  NUMERO   = UN_NROCPTE) 
        LOOP
          BEGIN
            SELECT DISTINCT 'X'
            INTO   MI_X
            FROM   COMPROBANTE_PPTAL
            WHERE  COMPANIA    = UN_COMPANIA 
              AND  ANO         = UN_ANIO
              AND  TIPO_GENERA = UN_TIPOCPTE 
              AND  NRO_GENERA  = UN_NROCPTE;
            EXCEPTION WHEN NO_DATA_FOUND THEN
              MI_X := 'N';
          END;
          IF MI_X = 'X' THEN
            BEGIN
              BEGIN
                MI_TABLA := 'COMPROBANTE_PPTAL';
                MI_CAMPOS := 'FECHA = ''' || TO_CHAR(RSPAC.FECHA, 'DD/MM/YYYY') ||
                         ''', FECHA_VENCIMIENTO = ''' || TO_CHAR(RSPAC.FECHA_VENCIMIENTO) || 
                         ''', DESCRIPCION = ''' || RSPAC.DESCRIPCION ||
                         ''', TEXTO = ''' || RSPAC.TEXTO || 
                         ''', NRO_DOCUMENTO = ''' || RSPAC.NRO_DOCUMENTO || 
                         ''', FECHA_VCN_DOC = ''' || RSPAC.FECHA_VCN_DOC ||
                         ''', VLR_DOCUMENTO = CASE SIGN(' || MI_CREDITOS || ') WHEN 1 THEN ' || MI_CREDITOS || ' ELSE ' || MI_CONTRACREDITOS || ' END ' ||
                           ', TERCERO = ''' || RSPAC.TERCERO ||
                         ''', SUCURSAL = ''' || RSPAC.SUCURSAL || 
                         ''', CENTRO_COSTO = ''' || RSPAC.CENTRO_COSTO ||
                         ''', AUXILIAR = ''' || RSPAC.AUXILIAR || 
                         ''', REFERENCIA = ''' || RSPAC.REFERENCIA || 
                         ''', FUENTE_RECURSO = ''' || RSPAC.FUENTE_RECURSO || 
                         ''', DEBITO = ' || UN_DEBITOCPTE || 
                           ', CREDITO = ' || UN_CREDITOCPTE || 
                           ', DEBITO_AFECTADO = ' || RSPAC.DEBITO_AFECTADO || 
                           ', CREDITO_AFECTADO = ' || RSPAC.CREDITO_AFECTADO || 
                           ', DEBITO_AFECTADOCNT = ' || RSPAC.DEBITO_AFECTADOCNT || 
                           ', CREDITO_AFECTADOCNT = ' || RSPAC.CREDITO_AFECTADOCNT || 
                           ', MODIFICACION_DEBITO = ' || RSPAC.MODIFICACION_DEBITO || 
                           ', MODIFICACION_CREDITO = ' || RSPAC.MODIFICACION_CREDITO || 
                           ', ABONADO = ' || RSPAC.ABONADO || 
                           ', ENTREGADO = ' || RSPAC.ENTREGADO || 
                           ', PAGADOBANCO = ' || RSPAC.PAGADOBANCO ||
                           ', IMPRESO = ' || RSPAC.IMPRESO ||
                           ', ANULADO = ' || RSPAC.ANULADO || 
                           ', CONTRACTUAL = ' || RSPAC.CONTRACTUAL || 
                           ', DESTINO = ''' || RSPAC.DESTINO || 
                         ''', REGISTROAUTOMATICO = ' || RSPAC.REGISTROAUTOMATICO ||
                           ', CARGO = ''' || RSPAC.CARGO || 
                         ''', DEPENDENCIA = ''' || RSPAC.DEPENDENCIA || 
                         ''', PAPELES = ' || RSPAC.PAPELES || 
                           ', TIPOCONTRATO = ''' || NVL(RSPAC.TIPOCONTRATO, ' ') || 
                         ''', NUMEROCONTRATO = ' || NVL(RSPAC.NUMEROCONTRATO, 0) || 
                           ', SITUACIONFONDOS = ' || RSPAC.SITUACIONFONDOS || 
                           ', CODSOLICITANTE = ''' || RSPAC.CODSOLICITANTE ||
                         ''', SUCSOLICITANTE = ''' || RSPAC.SUCSOLICITANTE ||'''' ;
                MI_CONDICION := '   COMPANIA        = '''  || UN_COMPANIA ||
                                ''' AND         ANO = '    || UN_ANIO ||
                                '   AND TIPO_GENERA = '''  || UN_TIPOCPTE ||    
                                ''' AND  NRO_GENERA = '    || UN_NROCPTE||'' ;
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                      UN_ACCION    => 'M',      
                                                      UN_CAMPOS    => MI_CAMPOS,      
                                                      UN_CONDICION => MI_CONDICION);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD   => SQLCODE,
                  UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_COMPROBANTE_PPTAL
                );  
            END;
            --VERIFICAR SI EXISTEN DETALLES, PARA ACTUALIZARLOS SINO PARA CREARLOS
            BEGIN
              SELECT DISTINCT 'X'
              INTO   MI_X
              FROM   DETALLE_COMPROBANTE_PPTAL 
              WHERE  COMPANIA       = UN_COMPANIA
                AND  ANO            = UN_ANIO
                AND  TIPO_GENERAMOP = UN_TIPOCPTE
                AND  NRO_GENERAMOP  = UN_NROCPTE
                AND  CUENTA         = UN_CTACPTE;
              EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_X := 'N';
            END;
            IF MI_X = 'X' THEN -- ACTUALIZAR EL REGISTRO 
              BEGIN
                BEGIN
                  MI_TABLA := 'DETALLE_COMPROBANTE_PPTAL';
                  MI_CAMPOS := '    CUENTA        = ''' || UN_CTACPTE || 
                               ''', VALOR_DEBITO  = '   || UN_DEBITOCPTE || 
                               ',   VALOR_CREDITO = '   || UN_CREDITOCPTE; 
                  MI_CONDICION := '   COMPANIA           = ''' || UN_COMPANIA ||
                                  ''' AND  ANO_GENERAMOP = '   || UN_ANIO ||
                                  '   AND TIPO_GENERAMOP = ''' || UN_TIPOCPTE ||    
                                  ''' AND  NRO_GENERAMOP = '   || UN_NROCPTE ||
                                  '   AND         CUENTA = ''' || UN_CTACPTE || '''';
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                        UN_ACCION    => 'M',      
                                                        UN_CAMPOS    => MI_CAMPOS,      
                                                        UN_CONDICION => MI_CONDICION);
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD   => SQLCODE,
                    UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_ACTUA_DETCPTEPPTAL
                  );  
              END; 
            ELSE               -- INSERTAR EL REGISTRO 
              -- SE DEBE OBTENER EL CONSECUTIVO DEL COMPROBANTE 
              BEGIN
                SELECT    MAX(CONSECUTIVO)
                         ,COMPROBANTE
                INTO      MI_CSC
                         ,MI_CPTE
                FROM      DETALLE_COMPROBANTE_PPTAL
                WHERE     COMPANIA       = UN_COMPANIA
                  AND     ANO            = UN_ANIO 
                  AND     TIPO_GENERAMOP = UN_TIPOCPTE
                  AND     NRO_GENERAMOP  = UN_NROCPTE
                GROUP BY  COMPROBANTE;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                  MI_CSC  := 0;
                  MI_CPTE := 0;
              END;
              BEGIN
                BEGIN
                  MI_TABLA := 'DETALLE_COMPROBANTE_PPTAL';
                  MI_CAMPOS := 'COMPANIA
                               ,ANO
                               ,TIPO_CPTE
                               ,COMPROBANTE
                               ,CONSECUTIVO
                               ,CUENTA
                               ,FECHA
                               ,DESCRIPCION
                               ,VALOR_DEBITO
                               ,VALOR_CREDITO
                               ,DEBITO_AFECTADO
                               ,CREDITO_AFECTADO
                               ,DEBITO_AFECTADOCNT
                               ,CREDITO_AFECTADOCNT
                               ,MODIFICACION_DEBITO
                               ,MODIFICACION_CREDITO
                               ,TIPO_DOCUMENTO
                               ,NRO_DOCUMENTO
                               ,CENTRO_COSTO
                               ,TERCERO
                               ,SUCURSAL
                               ,AUXILIAR
                               ,REFERENCIA
                               ,FUENTE_RECURSO
                               ,NATURALEZA
                               ,HORA
                               ,TIPO_GENERAMOP
                               ,NRO_GENERAMOP';
                  MI_CSC := MI_CSC +1;
                  MI_VALORES := ''''          || UN_COMPANIA || 
                                ''','         || UN_ANIO || 
                                ',''MOP'','   || MI_CPTE || 
                                ','           || MI_CSC || 
                                ','''         || UN_CTACPTE || 
                                ''','''       || TO_CHAR(RSPAC.FECHA, 'DD/MM/YYYY') || 
                                ''','''       || RSPAC.DESCRIPCION || 
                                ''','         || UN_DEBITOCPTE ||
                                ','           || UN_CREDITOCPTE || 
                                ','           || RSPAC.DEBITO_AFECTADO || 
                                ','           || RSPAC.CREDITO_AFECTADO || 
                                ','           || RSPAC.DEBITO_AFECTADOCNT ||
                                ','           || RSPAC.CREDITO_AFECTADOCNT || 
                                ','           || RSPAC.MODIFICACION_DEBITO || 
                                ','           || RSPAC.MODIFICACION_CREDITO || 
                                ','''         || RSPAC.TIPO_DOCUMENTO ||
                                ''','''         || RSPAC.NRO_DOCUMENTO || 
                                ''','''         || RSPAC.CENTRO_COSTO || 
                                ''','''       || RSPAC.TERCERO || 
                                ''','''       || RSPAC.SUCURSAL || 
                                ''','''       || RSPAC.AUXILIAR ||
                                ''','''       || RSPAC.REFERENCIA || 
                                ''','''       || RSPAC.FUENTE_RECURSO ||
                                ''',''D'',''' || RSPAC.FECHA || 
                                ''','''       || UN_TIPOCPTE || 
                                ''', '        || UN_NROCPTE ||'';
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                        UN_ACCION  => 'I',      
                                                        UN_CAMPOS  => MI_CAMPOS,      
                                                        UN_VALORES => MI_VALORES);
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD   => SQLCODE,
                    UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_INSER_DETCPTEPPTAL
                  );  
              END; 
            END IF;
          ELSE
            IF UN_CSCCPTE IS NULL THEN
              SELECT NVL(MAX(NUMERO) , 0)
              INTO   MI_CPTE
              FROM   COMPROBANTE_PPTAL
              WHERE  COMPANIA = UN_COMPANIA
                AND  ANO      = UN_ANIO
                AND  TIPO     = 'MOP';
              IF MI_CPTE = 0 THEN
                MI_CPTE := TO_NUMBER(TO_CHAR(SYSDATE, 'YYYY') || LPAD(1, 6, 0));
              ELSE
                MI_CPTE := MI_CPTE + 1;
              END IF;
            ELSE
              MI_CPTE := UN_CSCCPTE + 1;
            END IF;
            --INSERTAR EL MOP EN EL HEADER
            BEGIN
              BEGIN
                MI_TABLA := 'COMPROBANTE_PPTAL';
                MI_CAMPOS :='COMPANIA
                            ,ANO
                            ,TIPO
                            ,NUMERO
                            ,FECHA
                            ,FECHA_VENCIMIENTO
                            ,DESCRIPCION
                            ,TEXTO
                            ,NRO_DOCUMENTO
                            ,FECHA_VCN_DOC
                            ,VLR_DOCUMENTO
                            ,TERCERO
                            ,SUCURSAL
                            ,CENTRO_COSTO
                            ,AUXILIAR
                            ,REFERENCIA
                            ,FUENTE_RECURSO 
                            ,DEBITO
                            ,CREDITO
                            ,DEBITO_AFECTADO
                            ,CREDITO_AFECTADO
                            ,DEBITO_AFECTADOCNT
                            ,CREDITO_AFECTADOCNT
                            ,MODIFICACION_DEBITO
                            ,MODIFICACION_CREDITO
                            ,ABONADO
                            ,ENTREGADO
                            ,PAGADOBANCO
                            ,IMPRESO
                            ,ANULADO
                            ,CONTRACTUAL
                            ,DESTINO
                            ,REGISTROAUTOMATICO
                            ,CARGO
                            ,DEPENDENCIA
                            ,PAPELES
                            ,TIPOCONTRATO
                            ,NUMEROCONTRATO
                            ,SITUACIONFONDOS
                            ,CODSOLICITANTE
                            ,SUCSOLICITANTE
                            ,TIPO_GENERA
                            ,NRO_GENERA';
                MI_VALORES := ''''            || UN_COMPANIA || 
                              ''','           || UN_ANIO || 
                              ',''MOP'', '    || MI_CPTE || 
                              ','''           || TO_CHAR(RSPAC.FECHA, 'DD/MM/YYYY') || 
                              ''','''         || TO_CHAR(RSPAC.FECHA_VENCIMIENTO, 'DD/MM/YYYY') || 
                              ''','''         || RSPAC.DESCRIPCION || 
                              ''','''         || RSPAC.TEXTO ||
                              ''','''         || RSPAC.NRO_DOCUMENTO || 
                              ''','''         || TO_CHAR(RSPAC.FECHA_VCN_DOC, 'DD/MM/YYYY') || 
                              ''',CASE SIGN(' || MI_CREDITOS || ') WHEN 1 THEN ' || MI_CREDITOS || ' ELSE ' || MI_CONTRACREDITOS || ' END ' || 
                              ','''           || RSPAC.TERCERO || 
                              ''','''         || RSPAC.SUCURSAL || 
                              ''','''         || RSPAC.CENTRO_COSTO || 
                              ''','''         ||  RSPAC.AUXILIAR || 
                              ''','''         || RSPAC.REFERENCIA || 
                              ''','''         ||  RSPAC.FUENTE_RECURSO ||   
                              ''','           || UN_DEBITO || 
                              ','             || UN_CREDITO || 
                              ','             || RSPAC.DEBITO_AFECTADO || 
                              ','             || RSPAC.CREDITO_AFECTADO  ||
                              ','             || RSPAC.DEBITO_AFECTADOCNT || 
                              ','             || RSPAC.CREDITO_AFECTADOCNT || 
                              ','             || RSPAC.MODIFICACION_DEBITO || 
                              ','             || RSPAC.MODIFICACION_CREDITO || 
                              ','             || RSPAC.ABONADO ||  
                              ','             || RSPAC.ENTREGADO || 
                              ','             || RSPAC.PAGADOBANCO || 
                              ','             || RSPAC.IMPRESO || 
                              ','             || RSPAC.ANULADO || 
                              ','             || RSPAC.CONTRACTUAL || 
                              ','''           || RSPAC.DESTINO || 
                              ''','           || RSPAC.REGISTROAUTOMATICO || 
                              ','''           || RSPAC.CARGO ||
                              ''','''         || RSPAC.DEPENDENCIA || 
                              ''','           || RSPAC.PAPELES || 
                              ','''           || NVL(RSPAC.TIPOCONTRATO, ' ') || 
                              ''','           || NVL(RSPAC.NUMEROCONTRATO, 0) ||
                              ','             || RSPAC.SITUACIONFONDOS || 
                              ','''           || RSPAC.CODSOLICITANTE || 
                              ''','''         || RSPAC.SUCSOLICITANTE || 
                              ''','''         || UN_TIPOCPTE || 
                              ''','           || UN_NROCPTE||'';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                      UN_ACCION  => 'I',      
                                                      UN_CAMPOS  => MI_CAMPOS,      
                                                      UN_VALORES => MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD   => SQLCODE,
                  UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_INSER_CPTEPPTAL
                );  
            END;
            BEGIN
              BEGIN
                MI_TABLA := 'DETALLE_COMPROBANTE_PPTAL';
                MI_CAMPOS := 'COMPANIA
                             ,ANO
                             ,TIPO_CPTE
                             ,COMPROBANTE
                             ,CONSECUTIVO
                             ,CUENTA
                             ,FECHA
                             ,DESCRIPCION
                             ,VALOR_DEBITO
                             ,VALOR_CREDITO
                             ,DEBITO_AFECTADO
                             ,CREDITO_AFECTADO
                             ,DEBITO_AFECTADOCNT
                             ,CREDITO_AFECTADOCNT
                             ,MODIFICACION_DEBITO
                             ,MODIFICACION_CREDITO
                             ,TIPO_DOCUMENTO
                             ,NRO_DOCUMENTO
                             ,CENTRO_COSTO
                             ,TERCERO
                             ,SUCURSAL
                             ,AUXILIAR
                             ,REFERENCIA
                             ,FUENTE_RECURSO
                             ,NATURALEZA
                             ,HORA
                             ,TIPO_GENERAMOP
                             ,NRO_GENERAMOP';
                MI_CSC := MI_CSC +1;
                MI_VALORES := ''''          || UN_COMPANIA || 
                              ''','         || UN_ANIO || 
                              ',''MOP'','   || MI_CPTE || 
                              ','           || MI_CSC || 
                              ','''         || UN_CTACPTE || 
                              ''','''       || TO_CHAR(RSPAC.FECHA, 'DD/MM/YYYY') || 
                              ''','''       || RSPAC.DESCRIPCION || 
                              ''','         || UN_DEBITOCPTE ||
                              ','           || UN_CREDITOCPTE || 
                              ','           || RSPAC.DEBITO_AFECTADO || 
                              ','           || RSPAC.CREDITO_AFECTADO || 
                              ','           || RSPAC.DEBITO_AFECTADOCNT ||
                              ','           || RSPAC.CREDITO_AFECTADOCNT || 
                              ','           || RSPAC.MODIFICACION_DEBITO || 
                              ','           || RSPAC.MODIFICACION_CREDITO || 
                              ','''         || RSPAC.TIPO_DOCUMENTO ||
                              ''','''       || RSPAC.NRO_DOCUMENTO || 
                              ''','''       || RSPAC.CENTRO_COSTO || 
                              ''','''       || RSPAC.TERCERO || 
                              ''','''       || RSPAC.SUCURSAL || 
                              ''','''       || RSPAC.AUXILIAR ||
                              ''','''       || RSPAC.REFERENCIA || 
                              ''','''       || RSPAC.FUENTE_RECURSO ||
                              ''',''D'',''' || RSPAC.FECHA || 
                              ''','''       || UN_TIPOCPTE || 
                              ''','         || UN_NROCPTE ;
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                      UN_ACCION  => 'I',      
                                                      UN_CAMPOS  => MI_CAMPOS,      
                                                      UN_VALORES => MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD   => SQLCODE,
                  UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_INSER_DETCPTEPPTAL
              );  
            END;
          END IF;
        END LOOP CONSULTATRAERDATOS;
      END IF;
    END IF; -- SI NO SE ENVIA VALORES PARA LOS PAR�?METROS UN_TIPOCTE Y UN_NROCPTE
  END PR_ACTPTO0_MOP;

  -- 7
FUNCTION FC_DOCUMENTOAFECTAR_PPTAL
    /*
      NAME              : FC_DOCUMENTOAFECTAR_PPTAL
      AUTHORS           : SYSMAN LTDA
      AUTHOR MIGRACION  : JAVIER VILLATE
      DATE MIGRADOR     : 13/12/2016
      TIME              : 12:00 PM
      MODIFIER          : JAVIER VILLATE / JOSÉ PASCUAL GOMEZ / JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MODIFIED     : 13/12/2016 / 28/12/2016 / 12/01/2017
      TIME              : 11:00 AM / 10:58 AM / 02:21 PM
      DESCRIPTION       : FUNCIÓN PARA AFECTAR EL COMPROBANTE PRESUPUESTAL OBTENIDO DEL BOTÓN ACEPTAR DEL FORMULARIO PEDIRDOCUMENTOAAFECTAR   
      PARAMETERS        : UN_COMPANIA    => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_ANOH        => ANIO DEL COMPROBANTE.
                          UN_TIPOH       => TIPO DE COMPROBANTE.
                          UN_TIPOC       => TIPO DE COMPROBANTE. 
                          UN_NUMEROH     => NUMERO DE COMPROBANTE.
                          UN_NUMEROC     => NUMERO DE COMPROBANTE.
                          UN_CLASEH      => CLASE DEL COMPROBANTE.
                          UN_CLASEC      => CLASE DEL COMPROBANTE.
                          UN_FECHA_CPTE  => FECHA DEL COMPROBANTE.    
                          UN_FECHA_AUX   => FECHA AUXILIAR DEL COMPROBANTE.
                          UN_USUARIO     => USUARIO QUE ESTA TRABAJANDO.
      MODIFICATIONS     : SE AJUSTA TODO EL PROCESO PARA QUE CUMPLA CON EL ESTANDAR DE PROGRAMACIÓN, CONTROL DE EXCEPCIONES
                          JESSICA : CORRECCION SEGUN ESTANDAR DE PROGRAMACION, REFERENCIACION DE FUNCIONES Y OPTIMIZACION 
                          DE MANEJO DE ERRORES.
      @NAME:  seleccionarDocumentoAfectar
      @METHOD:  GET 
    */
  (
    UN_COMPANIA                     IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_ANOH                         IN PCK_SUBTIPOS.TI_ANIO
   ,UN_TIPOH                        IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL
   ,UN_TIPOC                        IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL
   ,UN_NUMEROH                      IN PCK_SUBTIPOS.TI_DOBLE
   ,UN_NUMEROC                      IN PCK_SUBTIPOS.TI_DOBLE
   ,UN_CLASEH                       IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL
   ,UN_CLASEC                       IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL
   ,UN_FECHA_CPTE                   IN DATE
   ,UN_FECHA_AUX                    IN DATE
   ,UN_USUARIO                      IN PCK_SUBTIPOS.TI_USUARIO
  )
    RETURN VARCHAR2
  AS
    MI_RS_EJECUCIONPPT              PCK_SUBTIPOS.TI_DOBLE;
    MI_RS_PAC_PROGRAMADO            PCK_SUBTIPOS.TI_DOBLE;
    MI_RS_PAC_TESORERIA             PCK_SUBTIPOS.TI_DOBLE;
    MI_RS_PAC_COMPROMETIDO          PCK_SUBTIPOS.TI_DOBLE;
    MI_RS_PAC_EJECUTADO             PCK_SUBTIPOS.TI_DOBLE;
    MI_RS_TOTALPACAPROPIADO         PCK_SUBTIPOS.TI_DOBLE;
    MI_RSPAC_TOTALPAC               PCK_SUBTIPOS.TI_DOBLE;
    MI_RSPAC_TOTALREGISTROS         PCK_SUBTIPOS.TI_DOBLE;
    MI_RSPAC_TOTALOBLIGACIONES      PCK_SUBTIPOS.TI_DOBLE;
    MI_VALORANTERIOR                PCK_SUBTIPOS.TI_DOBLE;
    MI_VALORG                       PCK_SUBTIPOS.TI_DOBLE;
    MI_VALORPAC                     PCK_SUBTIPOS.TI_DOBLE;
    MI_RS_ANTERIOR                  DETALLE_COMPROBANTE_PPTAL%ROWTYPE;
    MI_RSDETALLE_VALOR_DEBITO       PCK_SUBTIPOS.TI_DOBLE;
    MI_RSDETALLE_VALOR_CREDITO      PCK_SUBTIPOS.TI_DOBLE;
    MI_RSDETALLE_DEBITO_AFECTADO    PCK_SUBTIPOS.TI_DOBLE;
    MI_RSDETALLE_CREDITO_AFECTADO   PCK_SUBTIPOS.TI_DOBLE;
    MI_RSDETALLE_MODIFICACION_DEB   PCK_SUBTIPOS.TI_DOBLE;
    MI_RSDETALLE_MODIFICACION_CRE   PCK_SUBTIPOS.TI_DOBLE;
    MI_RSDETALLE_CUENTA             PCK_SUBTIPOS.TI_STRSQL;
    MI_RSDETALLE_TIPO_CPTE_AFECT    PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL;
    MI_RSDETALLE_CMPTE_AFECTADO     PCK_SUBTIPOS.TI_NUMEROCOMPROBANTEPPTAL;
    MI_RSDETALLE_CONSECUTIVOPPTO    PCK_SUBTIPOS.TI_CONSECUTIVOPPTAL;
    MI_RSDETALLE_COMPANIA           PCK_SUBTIPOS.TI_COMPANIA;
    MI_RSDETALLE_CONSECUTIVO        PCK_SUBTIPOS.TI_CONSECUTIVOPPTAL;
    MI_RSDETALLE_CODIGO_CUENTA      PCK_SUBTIPOS.TI_CODIGOPPTAL;
    MI_RSDETALLE_NATURALEZA         PCK_SUBTIPOS.TI_NATURALEZA;
    MI_RSDETALLE_DESCRIPCION        PCK_SUBTIPOS.TI_STRSQL;
    MI_RSDETALLE_TIPO_DOCUMENTO     PCK_SUBTIPOS.TI_TIPO_DOCUMENTO;
    MI_RSDETALLE_NRO_DOCUMENTO      PCK_SUBTIPOS.TI_STRSQL;
    MI_RSDETALLE_CENTRO_COSTO       PCK_SUBTIPOS.TI_CENTRO_COSTO;
    MI_RSDETALLE_TERCERO            PCK_SUBTIPOS.TI_TERCERO;
    MI_RSDETALLE_SUCURSAL           PCK_SUBTIPOS.TI_SUCURSAL;
    MI_RSDETALLE_AUXILIAR           PCK_SUBTIPOS.TI_AUXILIAR;
    MI_RSDETALLE_CONTRACTUAL        PCK_SUBTIPOS.TI_LOGICO;
    MI_RSDETALLE_PAPELES            PCK_SUBTIPOS.TI_LOGICO;
    MI_RSDETALLE_REFERENCIA         PCK_SUBTIPOS.TI_REFERENCIA;

    MI_RSDETALLE_SECTOR             PCK_SUBTIPOS.TI_SECTOR;
    MI_RSDETALLE_PROGRAMA           PCK_SUBTIPOS.TI_PROGRAMA;
    MI_RSDETALLE_SUBPROGRAMA        PCK_SUBTIPOS.TI_SUBPROGRAMA;
    MI_RSDETALLE_COD_PROD_CUIPO     PCK_SUBTIPOS.TI_COD_PROD_CUIPO;
    MI_RSDETALLE_CODIGO_BPIN        PCK_SUBTIPOS.TI_CODIGO_BPIN;
    MI_RSDETALLE_CODIGO_CCPET       PCK_SUBTIPOS.TI_CODIGO_CCPET;
    MI_RSDETALLE_CODIGO_CPC         PCK_SUBTIPOS.TI_CODIGO_CPC;
    MI_RSDETALLE_CODIGOUNIDADEJE    PCK_SUBTIPOS.TI_CODIGOUNIDADEJE;
    MI_RSDETALLE_FUENTE_CUIPO       PCK_SUBTIPOS.TI_FUENTE_CUIPO;
    MI_RSDETALLE_CODIGOCCPETREGA    PCK_SUBTIPOS.TI_CODIGOCCPETREGA;
    MI_RSDETALLE_POLITICA_PUBLICA   PCK_SUBTIPOS.TI_POLITICA_PUBLICA;
    MI_RSDETALLE_DETALLE_SECTORIAL  PCK_SUBTIPOS.TI_DETALLE_SECTORIAL;
    MI_RSDETALLE_TIPO_RECURSO_SGR   PCK_SUBTIPOS.TI_TIPO_RECURSO_SGR;
	MI_RSDETALLE_VALOR_BASE         PCK_SUBTIPOS.TI_VALOR_BASE ;
    MI_RSDETALLE_IMPUESTO           PCK_SUBTIPOS.TI_IMPUESTO;																	  
	
	
    MI_RSDETALLE_FUENTE_RECURSO     PCK_SUBTIPOS.TI_FUENTE_RECURSOS;
    MI_RSDETALLE_CONSITFON          PCK_SUBTIPOS.TI_LOGICO;
    MI_RSDETALLE_PORDIS             PCK_SUBTIPOS.TI_PORCENTAJE;
    MI_RSDETALLE_ANO                PCK_SUBTIPOS.TI_ANIO;
    MI_RSPAC_EJECUCIONPPT           PCK_SUBTIPOS.TI_DOBLE;
    MI_SALDO                        PCK_SUBTIPOS.TI_DOBLE;
    MI_PAC_DISPONIBLE               PCK_SUBTIPOS.TI_DOBLE;
    MI_RSPAC_PACEJECUTADO           PCK_SUBTIPOS.TI_DOBLE;
    MI_RSC_COLUMNA                  PCK_SUBTIPOS.TI_TEXTO1;
    MI_RSC_AFECTACION               PCK_SUBTIPOS.TI_TEXTO1;
    MI_RSC_CODIGO                   PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL;
    MI_RS_VALOR                     PCK_SUBTIPOS.TI_DOBLE;
    MI_TABLA                        PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS                       PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION                    PCK_SUBTIPOS.TI_CONDICION;
    MI_VALORES                      PCK_SUBTIPOS.TI_VALORES;
    MI_EXCLUIDOS                    PCK_SUBTIPOS.TI_EXCLUIDOS;
    MI_TIPO_AFECTA                  PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL;
    MI_CMPT_AFECTA                  PCK_SUBTIPOS.TI_DOBLE;
    MI_TIPO_CPTE_AFECT              PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL;
    MI_CMPTE_AFECTADO               PCK_SUBTIPOS.TI_DOBLE;
    MI_AUX                          PCK_SUBTIPOS.TI_TEXTO1;
    MI_MERGEUSING                   PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE                  PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE                  PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_ANOC                         PCK_SUBTIPOS.TI_ANIO;
    MI_MESC 			            PCK_SUBTIPOS.TI_MES;
    MI_DIAC 			            PCK_SUBTIPOS.TI_DIA;
    MI_FECHA_AUX                    VARCHAR2(50);
    MI_TIPOCONTRATO                 PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL;
    MI_NUMEROCONTRATO               PCK_SUBTIPOS.TI_DOBLE;
    MI_DESCRIPCION                  PCK_SUBTIPOS.TI_STRSQL;          
    MI_TEXTO                        PCK_SUBTIPOS.TI_STRSQL;
    MI_AUXILIAR                     PCK_SUBTIPOS.TI_AUXILIAR;
    MI_COD_PROYECTO_PPTAL           PCK_SUBTIPOS.TI_AUXILIAR;
    MI_MSGERROR                     PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_DETALLES                     NUMBER(5);
    MI_TIPOT                        BPNOVEDADPROYECTO.TIPOT%TYPE;
    MI_CLASET                       BPNOVEDADPROYECTO.CLASET%TYPE;
    MI_CODIGOT                      BPNOVEDADPROYECTO.CODIGO%TYPE;
    MI_DEPENDENCIAT                 BPNOVEDADPROYECTO.DEPENDENCIA%TYPE;
    MI_TERCERO_AFECTADA             PCK_SUBTIPOS.TI_TERCERO;
    MI_SUCURSAL_AFECTADA            PCK_SUBTIPOS.TI_SUCURSAL;
    MI_DEPENDENCIA                  VARCHAR2(12 CHAR);
    MI_CARGO                        VARCHAR2(50 CHAR);
    MI_REEMPLAZOS                   PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CLASECOMMPROBANTE            TIPO_COMPROBPP.CLASE%TYPE;    	 
	MI_RSTIPOT                      DETALLE_COMPROBANTE_PPTAL.TIPOT%TYPE;
    MI_RSCLASET                     DETALLE_COMPROBANTE_PPTAL.CLASET%TYPE;
    MI_RSCMPTE_SOLICI_AFECT         DETALLE_COMPROBANTE_PPTAL.CMPTE_SOLICI_AFECTADO%TYPE;
    MI_RSDEPENDENCIA                DETALLE_COMPROBANTE_PPTAL.DEPENDENCIA%TYPE;		
    MI_FICHA                        COMPROBANTE_PPTAL.FICHA%TYPE;
    MI_CERTIFICADO_PAA				COMPROBANTE_PPTAL.CERTIFICADO_PAA%TYPE;
    MI_DESTINO                      COMPROBANTE_PPTAL.DESTINO%TYPE;
    MI_NRO_DOCUMENTO                PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_TIPO_COMPROMISO              COMPROBANTE_PPTAL.TIPO_COMPROMISO%TYPE;
    MI_PAR_PAC_RUBROS_HIJOS         PCK_SUBTIPOS.TI_PARAMETRO;
    MI_MAN_PAC                      V_PLAN_PRESUPUESTAL.MAN_PAC%TYPE;  										   
  BEGIN
    MI_PAR_PAC_RUBROS_HIJOS := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA, 'MANEJA PAC A RUBROS HIJOS' , PCK_DATOS.MODULOPRESUPUESTO , SYSDATE),'NO');
    BEGIN        
      BEGIN
        --COPIO LOS DATOS DIGITADOS EN EL DIS
        --(ncardenas:21/01/2025) - se agrega el campo ficha
        MI_ANOC := TO_CHAR(UN_FECHA_AUX, 'YYYY');        
        SELECT TIPOCONTRATO
              ,NUMEROCONTRATO 
              ,NVL(DESCRIPCION,'.')
              ,NVL(TEXTO,'.')
              ,NVL(AUXILIAR,PCK_DATOS.FC_CONS_AUXILIAR)
              ,COD_PROYECTO_PPTAL
              ,DEPENDENCIA
              ,CARGO
              ,FICHA
              ,CERTIFICADO_PAA
              ,DESTINO
              ,TIPO_COMPROMISO
        INTO   MI_TIPOCONTRATO
              ,MI_NUMEROCONTRATO
              ,MI_DESCRIPCION
              ,MI_TEXTO
              ,MI_AUXILIAR
              ,MI_COD_PROYECTO_PPTAL
              ,MI_DEPENDENCIA
              ,MI_CARGO
              ,MI_FICHA
              ,MI_CERTIFICADO_PAA
              ,MI_DESTINO
              ,MI_TIPO_COMPROMISO
        FROM   COMPROBANTE_PPTAL 
        WHERE  COMPANIA = UN_COMPANIA
          AND  ANO      = UN_ANOH
          AND  TIPO     = UN_TIPOH  
          AND  NUMERO   = UN_NUMEROH;  
        EXCEPTION WHEN NO_DATA_FOUND THEN
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
        MI_MSGERROR(1).CLAVE := 'NUMERO';
        MI_MSGERROR(1).VALOR := UN_NUMEROH;
        MI_MSGERROR(2).CLAVE := 'TIPO';
        MI_MSGERROR(2).VALOR := UN_TIPOH;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_COMPRO_SIN_HEADER,
          UN_TABLAERROR => 'COMPROBANTE_PPTAL',
          UN_REEMPLAZOS => MI_MSGERROR
        );  
    END;  
    BEGIN
      BEGIN
        MI_TABLA := 'COMPROBANTE_PPTAL';
        MI_CAMPOS:= 'TIPOCONTRATO       =  ''' || NVL(MI_TIPOCONTRATO,'')  || ''',
                     NUMEROCONTRATO     =  ''' || NVL(MI_NUMEROCONTRATO,0) || ''',
                     DESCRIPCION        =''' || MI_DESCRIPCION || ''',        
                     TEXTO              =''' || MI_TEXTO || ''',
                     AUXILIAR           =''' || MI_AUXILIAR || ''',                   
                     COD_PROYECTO_PPTAL = CASE WHEN ' || NVL(MI_COD_PROYECTO_PPTAL,'NULL') || ' IS NULL THEN NULL ELSE ''' || NVL(MI_COD_PROYECTO_PPTAL,'') || ''' END,
                     DEPENDENCIA = '''||MI_DEPENDENCIA||''',
                     CARGO = '''||MI_CARGO||''',
                     CERTIFICADO_PAA = '''||MI_CERTIFICADO_PAA||''',
                     DESTINO = '''||MI_DESTINO||''',
                     TIPO_COMPROMISO = '''||MI_TIPO_COMPROMISO||'''';                                            
        MI_CONDICION:='COMPROBANTE_PPTAL.COMPANIA =''' || UN_COMPANIA ||'''
                   AND COMPROBANTE_PPTAL.ANO      ='   || MI_ANOC     ||'
                   AND COMPROBANTE_PPTAL.TIPO     =''' || UN_TIPOC    ||'''
                   AND COMPROBANTE_PPTAL.NUMERO   ='   || UN_NUMEROC  ||'';          
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                               UN_ACCION    => 'M', 
                                               UN_CAMPOS    => MI_CAMPOS, 
                                               UN_CONDICION => MI_CONDICION); 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_COMPROBANTE_PPTAL,
          UN_TABLAERROR => 'COMPROBANTE_PPTAL'
        ); 
    END;
    BEGIN
      SELECT  COUNT(DETALLE_COMPROBANTE_PPTAL.COMPANIA) DETALLES
      INTO    MI_DETALLES  
      FROM    DETALLE_COMPROBANTE_PPTAL 
      WHERE   COMPANIA    = UN_COMPANIA 
        AND   ANO         = MI_ANOC 
        AND   TIPO_CPTE   = UN_TIPOC
        AND   COMPROBANTE = UN_NUMEROC;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_DETALLES := 0;
    END;
    IF MI_DETALLES =0 THEN
      BEGIN
        BEGIN
          MI_TABLA := 'COMPROBANTE_PPTALAFECTADOS';
          MI_CONDICION := ' COMPANIA                = ''' || UN_COMPANIA || '''
                            AND   ANO               = '   || MI_ANOC || '
                            AND   TIPO_CPTE         = ''' || UN_TIPOC || '''
                            AND   COMPROBANTE       = '   || UN_NUMEROC || '
                            AND   ANO_AFECT         = '   || UN_ANOH || '
                            AND   TIPO_CPTE_AFECT   = ''' || UN_TIPOH || '''
                            AND   COMPROBANTE_AFECT = '   || UN_NUMEROH;
          MI_AUX:=PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA, 
                                    UN_ACCION    => 'E',
                                    UN_CONDICION => MI_CONDICION); 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_BORR_CPTEPPTAFECTADO,
            UN_TABLAERROR => 'COMPROBANTE_PPTALAFECTADOS'
          ); 
      END;
    END IF;

    IF UN_CLASEH ='DIS' AND UN_CLASEC='RES' THEN
       SELECT TERCERO,
              SUCURSAL             
        INTO  MI_TERCERO_AFECTADA,
              MI_SUCURSAL_AFECTADA
        FROM  COMPROBANTE_PPTAL 
        WHERE COMPANIA = UN_COMPANIA
          AND ANO      = MI_ANOC
          AND  TIPO     = UN_TIPOC  
          AND  NUMERO   = UN_NUMEROC;  

    END IF;

    <<DETALLESCOMPROBANTE>>
    FOR RS IN (
      SELECT   VALOR_DEBITO     
              ,VALOR_CREDITO  
              ,DEBITO_AFECTADO    
              ,CREDITO_AFECTADO
              ,MODIFICACION_DEBITO  
              ,MODIFICACION_CREDITO 
              ,ID     
              ,TIPO_CPTE_AFECT    
              ,CMPTE_AFECTADO   
              ,CONSECUTIVOPPTO              
              ,CONSECUTIVO        
              ,CUENTA 
              ,NATURALEZA     
              ,DESCRIPCION        
              ,TIPO_DOCUMENTO     
              ,NRO_DOCUMENTO      
              ,CENTRO_COSTO     
              ,TERCERO            
              ,SUCURSAL       
              ,AUXILIAR       
              ,CONTRACTUAL        
              ,PAPELES        
              ,REFERENCIA
              ,SECTOR
              ,PROGRAMA
              ,SUBPROGRAMA
              ,COD_PROD_CUIPO
              ,CODIGO_BPIN
              ,CODIGO_CCPET
              ,CODIGO_CPC
              ,CODIGOUNIDADEJE
              ,FUENTE_CUIPO 
              ,CODIGOCCPETREGA
              ,POLITICA_PUBLICA
              ,DETALLE_SECTORIAL
              ,RECURSO_SGR
              ,FUENTE_RECURSO
              ,CONSITUACIONFONDOS           
              ,PORCENTAJEDISTRIBUIDO            
              ,ANO
			  ,VALOR_BASE
              ,IMPUESTO
			  ,TIPOT
              ,CLASET
              ,CMPTE_SOLICI_AFECTADO
              ,DEPENDENCIA
      FROM     DETALLE_COMPROBANTE_PPTAL 
      WHERE    COMPANIA    =  UN_COMPANIA
        AND    ANO         =  UN_ANOH 
        AND    TIPO_CPTE   =  UN_TIPOH
        AND    COMPROBANTE =  UN_NUMEROH 
        AND    FECHA       <= UN_FECHA_CPTE 
      ORDER BY COMPANIA
              ,ANO
              ,TIPO_CPTE
              ,COMPROBANTE
              ,CONSECUTIVO)
    LOOP
      MI_RSDETALLE_VALOR_DEBITO      := RS.VALOR_DEBITO;      
      MI_RSDETALLE_VALOR_CREDITO     := RS.VALOR_CREDITO;       
      MI_RSDETALLE_DEBITO_AFECTADO   := RS.DEBITO_AFECTADO;     
      MI_RSDETALLE_CREDITO_AFECTADO  := RS.CREDITO_AFECTADO;  
      MI_RSDETALLE_MODIFICACION_DEB  := RS.MODIFICACION_DEBITO;   
      MI_RSDETALLE_MODIFICACION_CRE  := RS.MODIFICACION_CREDITO;  
      MI_RSDETALLE_CUENTA            := RS.ID;            
      MI_RSDETALLE_TIPO_CPTE_AFECT   := RS.TIPO_CPTE_AFECT;     
      MI_RSDETALLE_CMPTE_AFECTADO    := RS.CMPTE_AFECTADO;    
      MI_RSDETALLE_CONSECUTIVOPPTO   := RS.CONSECUTIVOPPTO;     
      MI_RSDETALLE_CONSECUTIVO       := RS.CONSECUTIVO;         
      MI_RSDETALLE_CODIGO_CUENTA     := RS.CUENTA;      
      MI_RSDETALLE_NATURALEZA        := RS.NATURALEZA;        
      MI_RSDETALLE_DESCRIPCION       := RS.DESCRIPCION;         
      MI_RSDETALLE_TIPO_DOCUMENTO    := RS.TIPO_DOCUMENTO;    
      MI_RSDETALLE_NRO_DOCUMENTO     := RS.NRO_DOCUMENTO;       
      MI_RSDETALLE_CENTRO_COSTO      := RS.CENTRO_COSTO;      
      MI_RSDETALLE_TERCERO           := CASE WHEN UN_CLASEH ='DIS' AND UN_CLASEC='RES' THEN  MI_TERCERO_AFECTADA  ELSE    RS.TERCERO END;             
      MI_RSDETALLE_SUCURSAL          := CASE WHEN UN_CLASEH ='DIS' AND UN_CLASEC='RES' THEN  MI_SUCURSAL_AFECTADA ELSE    RS.SUCURSAL END;          
      MI_RSDETALLE_AUXILIAR          := RS.AUXILIAR;          
      MI_RSDETALLE_CONTRACTUAL       := RS.CONTRACTUAL;         
      MI_RSDETALLE_PAPELES           := RS.PAPELES;             
      MI_RSDETALLE_REFERENCIA        := RS.REFERENCIA;

      MI_RSDETALLE_SECTOR            := RS.SECTOR;
      MI_RSDETALLE_PROGRAMA          := RS.PROGRAMA;
      MI_RSDETALLE_SUBPROGRAMA       := RS.SUBPROGRAMA;
      MI_RSDETALLE_COD_PROD_CUIPO    := RS.COD_PROD_CUIPO;
      MI_RSDETALLE_CODIGO_BPIN       := RS.CODIGO_BPIN;
      MI_RSDETALLE_CODIGO_CCPET      := RS.CODIGO_CCPET;
      MI_RSDETALLE_CODIGO_CPC        := RS.CODIGO_CPC;
      MI_RSDETALLE_CODIGOUNIDADEJE   := RS.CODIGOUNIDADEJE;
      MI_RSDETALLE_FUENTE_CUIPO      := RS.FUENTE_CUIPO;
      MI_RSDETALLE_CODIGOCCPETREGA   := RS.CODIGOCCPETREGA;
      MI_RSDETALLE_POLITICA_PUBLICA  := RS.POLITICA_PUBLICA;
      MI_RSDETALLE_DETALLE_SECTORIAL := RS.DETALLE_SECTORIAL;
      MI_RSDETALLE_TIPO_RECURSO_SGR  := RS.RECURSO_SGR;

      MI_RSDETALLE_FUENTE_RECURSO    := RS.FUENTE_RECURSO;  
      MI_RSDETALLE_CONSITFON         := RS.CONSITUACIONFONDOS;          
      MI_RSDETALLE_PORDIS            := RS.PORCENTAJEDISTRIBUIDO;             
      MI_RSDETALLE_ANO               := RS.ANO;
	  MI_RSDETALLE_VALOR_BASE        := RS.VALOR_BASE;
      MI_RSDETALLE_IMPUESTO 		 := RS.IMPUESTO;					  
      MI_ANOC                        := TO_CHAR(UN_FECHA_AUX, 'YYYY'); 
      MI_MESC                        := TO_CHAR(UN_FECHA_AUX, 'MM');
      MI_DIAC                        := TO_CHAR(UN_FECHA_AUX, 'DD');  
      MI_FECHA_AUX                   := 'TO_DATE(''' || TO_CHAR(UN_FECHA_AUX, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')';
	  MI_RSTIPOT                     := RS.TIPOT;
      MI_RSCLASET                    := RS.CLASET;
      MI_RSCMPTE_SOLICI_AFECT        := RS.CMPTE_SOLICI_AFECTADO;
      MI_RSDEPENDENCIA               := RS.DEPENDENCIA;											   
      BEGIN
        BEGIN    
          SELECT   SUM(CASE 
                         WHEN V_PLAN_PRESUPUESTAL.NATURALEZA = 'D' 
                         THEN (EJE_PPT_DEBITO - EJE_PPT_CREDITO) 
                         ELSE (EJE_PPT_CREDITO - EJE_PPT_DEBITO + MODIF_INGRESOS) 
                         END) EJECUCIONPPT
                  ,SUM(V_SALDO_PLAN_PPTAL.PAC_PROGRAMADO) PAC_PROGRAMADO
                  ,SUM(V_SALDO_PLAN_PPTAL.PACTESORERIA) PAC_TESORERIA
                  ,SUM(V_SALDO_PLAN_PPTAL.PAC_COMPROMETIDO) PAC_COMPROMETIDO
                  ,SUM(V_SALDO_PLAN_PPTAL.PAC_EJECUTADO) PAC_EJECUTADO
                  ,SUM((CASE 
                          WHEN V_PLAN_PRESUPUESTAL.NATURALEZA = 'D' 
                          THEN (MODIF_PAC_DEBITO - MODIF_PAC_CREDITO) 
                          ELSE (MODIF_PAC_CREDITO - MODIF_PAC_DEBITO)
                          END) + V_SALDO_PLAN_PPTAL.PAC_APROPIADO) TOTALPACAPROPIADO
          INTO     MI_RS_EJECUCIONPPT
                  ,MI_RS_PAC_PROGRAMADO 
                  ,MI_RS_PAC_TESORERIA 
                  ,MI_RS_PAC_COMPROMETIDO 
                  ,MI_RS_PAC_EJECUTADO
                  ,MI_RS_TOTALPACAPROPIADO    
          FROM     V_PLAN_PRESUPUESTAL 
            INNER JOIN V_SALDO_PLAN_PPTAL 
               ON      V_PLAN_PRESUPUESTAL.COMPANIA = V_SALDO_PLAN_PPTAL.COMPANIA
              AND      V_PLAN_PRESUPUESTAL.ANO      = V_SALDO_PLAN_PPTAL.ANO
              AND      V_PLAN_PRESUPUESTAL.ID       = V_SALDO_PLAN_PPTAL.ID
          WHERE    V_SALDO_PLAN_PPTAL.COMPANIA =  UN_COMPANIA 
            AND    V_SALDO_PLAN_PPTAL.ANO      =  UN_ANOH 
            AND    V_SALDO_PLAN_PPTAL.MES      <= 13
            AND    V_SALDO_PLAN_PPTAL.ID       =  MI_RSDETALLE_CUENTA 
          GROUP BY V_SALDO_PLAN_PPTAL.COMPANIA,
                   V_SALDO_PLAN_PPTAL.ANO,
                   V_SALDO_PLAN_PPTAL.CODIGO,
                   V_PLAN_PRESUPUESTAL.NATURALEZA;                 
          EXCEPTION WHEN NO_DATA_FOUND THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
          MI_MSGERROR(1).CLAVE := 'CODIGO';
          MI_MSGERROR(1).VALOR := MI_RSDETALLE_CUENTA;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_SIN_SALDOS,
            UN_TABLAERROR => 'SALDO_PLAN_PPTAL',
            UN_REEMPLAZOS => MI_MSGERROR
          );  
      END; 
      MI_VALORANTERIOR := 0;
      IF NVL(MI_RSDETALLE_TIPO_CPTE_AFECT, ' ') <> ' ' AND NVL(MI_RSDETALLE_CMPTE_AFECTADO, 0) <> 0 THEN
        BEGIN
          SELECT * 
          INTO MI_RS_ANTERIOR
          FROM DETALLE_COMPROBANTE_PPTAL 
          WHERE COMPANIA    = UN_COMPANIA
            AND ANO         = UN_ANOH 
            AND TIPO_CPTE   = MI_RSDETALLE_TIPO_CPTE_AFECT
            AND COMPROBANTE = MI_RSDETALLE_CMPTE_AFECTADO
            AND CONSECUTIVO = MI_RSDETALLE_CONSECUTIVOPPTO
            AND CUENTA      = MI_RSDETALLE_CODIGO_CUENTA;
          MI_VALORANTERIOR := (MI_RS_ANTERIOR.VALOR_DEBITO        - MI_RS_ANTERIOR.VALOR_CREDITO) 
                            - (MI_RS_ANTERIOR.DEBITO_AFECTADO     - MI_RS_ANTERIOR.CREDITO_AFECTADO) 
                            + (MI_RS_ANTERIOR.MODIFICACION_DEBITO - MI_RS_ANTERIOR.MODIFICACION_CREDITO);
          EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_RS_ANTERIOR := NULL;
        END;
      END IF;
      IF UN_CLASEH = 'DIS' AND UN_CLASEC = 'ADD' THEN
        MI_VALORG := 0;
      ELSIF UN_CLASEH = 'RES' AND UN_CLASEC = 'ADR' THEN
        MI_VALORG := MI_VALORANTERIOR;
      ELSIF UN_CLASEH = 'REO' AND UN_CLASEC = 'ARO' THEN
        MI_VALORG := MI_VALORANTERIOR;
      ELSIF (UN_CLASEH = 'ING' OR UN_CLASEH = 'ICA') AND (UN_CLASEC = 'AIN' OR UN_CLASEC = 'AIC') THEN
        MI_VALORG := 0;
      ELSIF UN_CLASEH = 'EGR' AND UN_CLASEC = 'AEG' THEN
        MI_VALORG := MI_VALORANTERIOR;
      ELSE  
        MI_VALORG := CASE 
                       WHEN MI_RSDETALLE_NATURALEZA = 'D' 
                       THEN (MI_RSDETALLE_VALOR_DEBITO     - MI_RSDETALLE_VALOR_CREDITO) 
                          - (MI_RSDETALLE_DEBITO_AFECTADO  - MI_RSDETALLE_CREDITO_AFECTADO) 
                          + (MI_RSDETALLE_MODIFICACION_DEB - MI_RSDETALLE_MODIFICACION_CRE)
                       ELSE (MI_RSDETALLE_VALOR_CREDITO    - MI_RSDETALLE_VALOR_DEBITO) 
                          - (MI_RSDETALLE_CREDITO_AFECTADO - MI_RSDETALLE_DEBITO_AFECTADO) 
                          + (MI_RSDETALLE_MODIFICACION_CRE - MI_RSDETALLE_MODIFICACION_DEB)
                     END;    
      END IF;
      IF UN_CLASEC = 'RES' OR UN_CLASEC = 'ADR' THEN
        IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                     UN_NOMBRE    => 'OBLIGA CONTROLAR PAC EN REGISTRO', 
                                     UN_MODULO    => PCK_DATOS.FC_MODULOPRESUPUESTO,
                                     UN_FECHA_PAR => SYSDATE), 'NO') = 'SI' THEN
          BEGIN
            SELECT   SUM((CASE 
                            WHEN V_PLAN_PRESUPUESTAL.NATURALEZA = 'D' 
                            THEN MODIF_PAC_DEBITO  - MODIF_PAC_CREDITO 
                            ELSE MODIF_PAC_CREDITO - MODIF_PAC_DEBITO 
                          END) + V_SALDO_PLAN_PPTAL.PAC_APROPIADO) TOTALPAC                
                    ,SUM(REG_CONTRACT 
                       + REG_NO_CONTRACT 
                       + MODIF_REG_CONT 
                       + MODIF_REG_NOCONT) TOTALREGISTROS
            INTO     MI_RSPAC_TOTALPAC
                    ,MI_RSPAC_TOTALREGISTROS
            FROM     V_PLAN_PRESUPUESTAL 
              INNER JOIN V_SALDO_PLAN_PPTAL 
                 ON      V_PLAN_PRESUPUESTAL.COMPANIA  = V_SALDO_PLAN_PPTAL.COMPANIA
                AND      V_PLAN_PRESUPUESTAL.ANO       = V_SALDO_PLAN_PPTAL.ANO
                AND      V_PLAN_PRESUPUESTAL.ID        = V_SALDO_PLAN_PPTAL.ID
            WHERE    V_SALDO_PLAN_PPTAL.COMPANIA    = UN_COMPANIA
              AND    V_SALDO_PLAN_PPTAL.ANO         = UN_ANOH 
              AND (
                    (NVL(MI_PAR_PAC_RUBROS_HIJOS,'NO') = 'SI'
                    AND V_SALDO_PLAN_PPTAL.ID = MI_RSDETALLE_CUENTA)
                    OR
                    (NVL(MI_PAR_PAC_RUBROS_HIJOS,'NO') <> 'SI'
                    AND LENGTH(V_SALDO_PLAN_PPTAL.ID) <= LENGTH(MI_RSDETALLE_CUENTA)
                    AND V_SALDO_PLAN_PPTAL.ID          = SUBSTR(MI_RSDETALLE_CUENTA, 1, LENGTH(V_SALDO_PLAN_PPTAL.ID)))
                    ) 
              AND    V_SALDO_PLAN_PPTAL.MES        <= TO_NUMBER(TO_CHAR(UN_FECHA_AUX, 'MM'))  
              AND    MAN_PAC = -1
            GROUP BY V_SALDO_PLAN_PPTAL.COMPANIA
                    ,V_SALDO_PLAN_PPTAL.ANO
                    ,V_SALDO_PLAN_PPTAL.ID
                    ,V_PLAN_PRESUPUESTAL.NATURALEZA;
            EXCEPTION WHEN NO_DATA_FOUND THEN
              MI_RSPAC_TOTALREGISTROS := NULL;
          END;
          IF MI_RSPAC_TOTALREGISTROS IS NOT NULL THEN
            IF ROUND((MI_RSPAC_TOTALPAC - MI_RSPAC_TOTALREGISTROS), 2) < ROUND(MI_VALORG, 2) THEN
              MI_VALORPAC := ROUND((MI_RSPAC_TOTALPAC - MI_RSPAC_TOTALREGISTROS), 2);
            ELSE
              MI_VALORPAC := MI_VALORG;
            END IF;
          ELSE
            BEGIN
              RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_PAC_DISPONIBLE,
                    UN_TABLAERROR => 'SALDO_PLAN_PPTAL'
              ); 
            END;
          END IF;
        END IF;
      END IF;
      IF UN_CLASEC = 'REO' OR UN_CLASEC = 'ARO' THEN
        IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                     UN_NOMBRE    => 'OBLIGA CONTROLAR PAC EN REGISTRO DE OBLIGACION', 
                                     UN_MODULO    => PCK_DATOS.FC_MODULOPRESUPUESTO,
                                     UN_FECHA_PAR => SYSDATE), 'NO') = 'SI' THEN
          BEGIN
            SELECT   SUM((CASE 
                            WHEN V_PLAN_PRESUPUESTAL.NATURALEZA = 'D' 
                            THEN (MODIF_PAC_DEBITO - MODIF_PAC_CREDITO) 
                            ELSE (MODIF_PAC_CREDITO - MODIF_PAC_DEBITO) 
                          END) + V_SALDO_PLAN_PPTAL.PAC_APROPIADO) TOTALPAC
                    ,SUM(REGISTRO_OBLIGACION + MODIF_REGISTRO_OBLIGACION) TOTALOBLIGACIONES
            INTO     MI_RSPAC_TOTALPAC
                    ,MI_RSPAC_TOTALOBLIGACIONES
            FROM     V_PLAN_PRESUPUESTAL 
              INNER JOIN V_SALDO_PLAN_PPTAL 
                 ON      V_PLAN_PRESUPUESTAL.COMPANIA = V_SALDO_PLAN_PPTAL.COMPANIA
                AND      V_PLAN_PRESUPUESTAL.ANO      = V_SALDO_PLAN_PPTAL.ANO
                AND      V_PLAN_PRESUPUESTAL.ID       = V_SALDO_PLAN_PPTAL.ID
            WHERE    V_SALDO_PLAN_PPTAL.COMPANIA    = UN_COMPANIA 
              AND    V_SALDO_PLAN_PPTAL.ANO         = UN_ANOH  
              AND (
                    (NVL(MI_PAR_PAC_RUBROS_HIJOS,'NO') = 'SI'
                    AND V_SALDO_PLAN_PPTAL.ID = MI_RSDETALLE_CUENTA)
                    OR
                    (NVL(MI_PAR_PAC_RUBROS_HIJOS,'NO') <> 'SI'
                    AND LENGTH(V_SALDO_PLAN_PPTAL.ID) <= LENGTH(MI_RSDETALLE_CUENTA)
                    AND V_SALDO_PLAN_PPTAL.ID          = SUBSTR(MI_RSDETALLE_CUENTA, 1, LENGTH(V_SALDO_PLAN_PPTAL.ID)))
                    ) 
              AND    V_SALDO_PLAN_PPTAL.MES        <= TO_NUMBER(TO_CHAR(UN_FECHA_AUX, 'MM'))
              AND    MAN_PAC = -1
            GROUP BY V_SALDO_PLAN_PPTAL.COMPANIA
                    ,V_SALDO_PLAN_PPTAL.ANO
                    ,V_SALDO_PLAN_PPTAL.ID
                    ,V_PLAN_PRESUPUESTAL.NATURALEZA;
            EXCEPTION WHEN NO_DATA_FOUND THEN
              MI_RSPAC_TOTALOBLIGACIONES := NULL;
          END;
          IF MI_RSPAC_TOTALOBLIGACIONES IS NOT NULL THEN
            IF ROUND((MI_RSPAC_TOTALPAC - MI_RSPAC_TOTALOBLIGACIONES), 2) < ROUND(MI_VALORG, 2) THEN
              BEGIN
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                  MI_MSGERROR(1).CLAVE := 'RUBRO';
                  MI_MSGERROR(1).VALOR := MI_RSDETALLE_CUENTA;
                  MI_MSGERROR(2).CLAVE := 'PAC';
                  MI_MSGERROR(2).VALOR := TO_CHAR(ROUND((MI_RSPAC_TOTALPAC - MI_RSPAC_TOTALOBLIGACIONES), 2),'000,000,000.00');
                  MI_MSGERROR(3).CLAVE := 'AFECTAR';
                  MI_MSGERROR(3).VALOR := TO_CHAR(ROUND(MI_VALORG, 2),'000,000,000.00');
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_PAC_DISPONIBLE_REO,
                    UN_TABLAERROR => 'SALDO_PLAN_PPTAL',
                    UN_REEMPLAZOS => MI_MSGERROR
                  ); 
              END;  
            ELSE
              BEGIN
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_PAC_DISPONIBLE,
                    UN_TABLAERROR => 'SALDO_PLAN_PPTAL'
                  ); 
              END;
            END IF;
          END IF;
        END IF;
      END IF; 
      --*****************************************************************************************************
      IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                   UN_NOMBRE    => 'OBLIGA CONTROLAR PAC EN EGRESO', 
                                   UN_MODULO    => PCK_DATOS.FC_MODULOPRESUPUESTO,
                                   UN_FECHA_PAR => SYSDATE), 'NO') = 'SI' THEN
        IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                     UN_NOMBRE    => 'CONTROLAR EGRESO CONTRA PAC DE TESORERIA', 
                                     UN_MODULO    => PCK_DATOS.FC_MODULOPRESUPUESTO,
                                     UN_FECHA_PAR => SYSDATE), 'NO') = 'SI' THEN
          IF UN_CLASEC = 'EGR' OR UN_CLASEC = 'AEG' THEN
            IF (MI_RS_PAC_TESORERIA - MI_RS_EJECUCIONPPT) < MI_VALORG THEN
              BEGIN
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                  MI_MSGERROR(1).CLAVE := 'RUBRO';
                  MI_MSGERROR(1).VALOR := MI_RSDETALLE_CUENTA;
                  MI_MSGERROR(2).CLAVE := 'PAC';
                  MI_MSGERROR(2).VALOR := TO_CHAR(MI_RS_PAC_TESORERIA - MI_RS_EJECUCIONPPT, '000,000,000.00');            
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_PAC_DISPONIBLE_TES,
                    UN_TABLAERROR => 'SALDO_PLAN_PPTAL',
                    UN_REEMPLAZOS => MI_MSGERROR
                  );
              END;          
            END IF;
          END IF;
        ELSE
          IF UN_CLASEC = 'EGR' OR UN_CLASEC = 'AEG' THEN
            BEGIN
              SELECT   SUM((CASE 
                              WHEN V_PLAN_PRESUPUESTAL.NATURALEZA = 'D' 
                              THEN (MODIF_PAC_DEBITO - MODIF_PAC_CREDITO) 
                              ELSE (MODIF_PAC_CREDITO - MODIF_PAC_DEBITO) 
                            END) + V_SALDO_PLAN_PPTAL.PAC_APROPIADO) TOTALPAC
                      ,SUM(CASE 
                             WHEN V_PLAN_PRESUPUESTAL.NATURALEZA = 'D' 
                             THEN EJE_PPT_DEBITO  - EJE_PPT_CREDITO
                             ELSE EJE_PPT_CREDITO - EJE_PPT_DEBITO 
                           END) EJECUCIONPPT
              INTO     MI_RSPAC_TOTALPAC 
                      ,MI_RSPAC_EJECUCIONPPT
              FROM     V_PLAN_PRESUPUESTAL 
                INNER JOIN V_SALDO_PLAN_PPTAL 
                   ON      V_PLAN_PRESUPUESTAL.COMPANIA = V_SALDO_PLAN_PPTAL.COMPANIA
                  AND      V_PLAN_PRESUPUESTAL.ANO      = V_SALDO_PLAN_PPTAL.ANO
                  AND      V_PLAN_PRESUPUESTAL.ID       = V_SALDO_PLAN_PPTAL.ID
              WHERE    V_SALDO_PLAN_PPTAL.COMPANIA   =  UN_COMPANIA
                AND    V_SALDO_PLAN_PPTAL.ANO        =  UN_ANOH 
                AND (
                    (NVL(MI_PAR_PAC_RUBROS_HIJOS,'NO') = 'SI'
                    AND V_SALDO_PLAN_PPTAL.ID = MI_RSDETALLE_CUENTA)
                    OR
                    (NVL(MI_PAR_PAC_RUBROS_HIJOS,'NO') <> 'SI'
                    AND LENGTH(V_SALDO_PLAN_PPTAL.ID) <= LENGTH(MI_RSDETALLE_CUENTA)
                    AND V_SALDO_PLAN_PPTAL.ID          = SUBSTR(MI_RSDETALLE_CUENTA, 1, LENGTH(V_SALDO_PLAN_PPTAL.ID)))
                    ) 
                AND    V_SALDO_PLAN_PPTAL.MES        <= TO_NUMBER(TO_CHAR(UN_FECHA_AUX, 'MM'))
                AND    MAN_PAC = -1
              GROUP BY V_SALDO_PLAN_PPTAL.COMPANIA
                      ,V_SALDO_PLAN_PPTAL.ANO
                      ,V_SALDO_PLAN_PPTAL.ID
                      ,V_PLAN_PRESUPUESTAL.NATURALEZA;
              EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_RSPAC_EJECUCIONPPT := NULL;
            END;
            IF MI_RSPAC_EJECUCIONPPT IS NOT NULL THEN
              IF (MI_RSPAC_TOTALPAC - MI_RSPAC_EJECUCIONPPT) < MI_VALORG THEN
                BEGIN
                  RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                  MI_MSGERROR(1).CLAVE := 'RUBRO';
                  MI_MSGERROR(1).VALOR := MI_RSDETALLE_CUENTA;
                  MI_MSGERROR(2).CLAVE := 'PAC';
                  MI_MSGERROR(2).VALOR := TO_CHAR(MI_RSPAC_TOTALPAC - MI_RSPAC_EJECUCIONPPT, '000,000,000.00');            
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_PAC_DISPONIBLE_EGR,
                    UN_TABLAERROR => 'SALDO_PLAN_PPTAL',
                    UN_REEMPLAZOS => MI_MSGERROR
                  );
                END;
              END IF;
            END IF;
          END IF;
        END IF;
      END IF;
      IF UN_CLASEC = 'REO' OR UN_CLASEC = 'ARO' THEN
        IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                     UN_NOMBRE    => 'CONTROLAR PAC COMPROMETIDO CONTRA PAC APROPIADO', 
                                     UN_MODULO    => PCK_DATOS.FC_MODULOPRESUPUESTO,
                                     UN_FECHA_PAR => SYSDATE), 'NO') = 'SI' THEN
          IF (MI_RS_TOTALPACAPROPIADO - MI_RS_PAC_COMPROMETIDO) < MI_VALORG THEN
            BEGIN
              RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
              MI_MSGERROR(1).CLAVE := 'RUBRO';
              MI_MSGERROR(1).VALOR := MI_RSDETALLE_CUENTA;
              MI_MSGERROR(2).CLAVE := 'PAC';
              MI_MSGERROR(2).VALOR := TO_CHAR(MI_RS_TOTALPACAPROPIADO - MI_RS_PAC_COMPROMETIDO, '000,000,000.00');           
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE,
                UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_PAC_DISPONIBLE_REO1,
                UN_TABLAERROR => 'SALDO_PLAN_PPTAL',
                UN_REEMPLAZOS => MI_MSGERROR
              );
            END;        
          END IF;
        END IF;
      END IF;
      IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                   UN_NOMBRE    => 'CONTROLAR PAC EJECUTADO CONTRA PAC COMPROMETIDO', 
                                   UN_MODULO    => PCK_DATOS.FC_MODULOPRESUPUESTO,
                                   UN_FECHA_PAR => SYSDATE), 'NO') = 'SI' THEN
        IF UN_CLASEC = 'EGR' OR UN_CLASEC = 'AEG' THEN
          IF (MI_RS_PAC_COMPROMETIDO - MI_RS_PAC_EJECUTADO) < MI_VALORG THEN
            BEGIN
              RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                MI_MSGERROR(1).CLAVE := 'RUBRO';
                MI_MSGERROR(1).VALOR := MI_RSDETALLE_CUENTA;
                MI_MSGERROR(2).CLAVE := 'PAC';
                MI_MSGERROR(2).VALOR := TO_CHAR(MI_RS_PAC_COMPROMETIDO - MI_RS_PAC_EJECUTADO, '000,000,000.00');           
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE,
                  UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_PAC_DISPONIBLE_EGR1,
                  UN_TABLAERROR => 'SALDO_PLAN_PPTAL',
                  UN_REEMPLAZOS => MI_MSGERROR
                );
            END;  
          END IF;
        END IF;
      END IF;
      IF UN_CLASEC = 'RES' OR UN_CLASEC = 'ADR' OR UN_CLASEC = 'DMR' THEN
        IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                     UN_NOMBRE    => 'CONTROLAR PAC PROGRAMADO CONTRA APROPIADO ANUAL', 
                                     UN_MODULO    => PCK_DATOS.FC_MODULOPRESUPUESTO,
                                     UN_FECHA_PAR => SYSDATE), 'NO') = 'SI' THEN
          BEGIN 
            BEGIN
              SELECT   SUM(V_SALDO_PLAN_PPTAL.PAC_PROGRAMADO) PAC_PROGRAMADO,
                       SUM(V_SALDO_PLAN_PPTAL.PAC_COMPROMETIDO) PAC_COMPROMETIDO,
                       SUM(V_SALDO_PLAN_PPTAL.PAC_EJECUTADO) PAC_EJECUTADO,            
                       SUM((CASE 
                              WHEN V_PLAN_PRESUPUESTAL.NATURALEZA = 'D' 
                              THEN (MODIF_PAC_DEBITO   - MODIF_PAC_CREDITO)   
                              ELSE (MODIF_PAC_CREDITO   - MODIF_PAC_DEBITO)                
                            END) + V_SALDO_PLAN_PPTAL.PAC_APROPIADO) TOTALPACAPROPIADO,
                       MAX(V_PLAN_PRESUPUESTAL.MAN_PAC)
              INTO     MI_RS_PAC_PROGRAMADO             
                      ,MI_RS_PAC_COMPROMETIDO
                      ,MI_RS_PAC_EJECUTADO
                      ,MI_RS_TOTALPACAPROPIADO
                      ,MI_MAN_PAC
              FROM     V_PLAN_PRESUPUESTAL 
                INNER JOIN V_SALDO_PLAN_PPTAL 
                   ON      V_PLAN_PRESUPUESTAL.COMPANIA = V_SALDO_PLAN_PPTAL.COMPANIA
                  AND      V_PLAN_PRESUPUESTAL.ANO      = V_SALDO_PLAN_PPTAL.ANO
                  AND      V_PLAN_PRESUPUESTAL.ID       = V_SALDO_PLAN_PPTAL.ID
              WHERE    V_SALDO_PLAN_PPTAL.COMPANIA  = UN_COMPANIA
                AND    V_SALDO_PLAN_PPTAL.ANO       = UN_ANOH
                AND    V_SALDO_PLAN_PPTAL.MES      <= 12
                AND    V_SALDO_PLAN_PPTAL.ID        = MI_RSDETALLE_CUENTA
              GROUP BY V_SALDO_PLAN_PPTAL.COMPANIA
                      ,V_SALDO_PLAN_PPTAL.ANO
                      ,V_SALDO_PLAN_PPTAL.ID
                      ,V_PLAN_PRESUPUESTAL.NATURALEZA;
              EXCEPTION WHEN NO_DATA_FOUND THEN 
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END;    
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
              MI_MSGERROR(1).CLAVE := 'CODIGO';
              MI_MSGERROR(1).VALOR := MI_RSDETALLE_CUENTA;
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE,
                UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_SIN_SALDOS,
                UN_TABLAERROR => 'SALDO_PLAN_PPTAL',
                UN_REEMPLAZOS => MI_MSGERROR
              );  
          END;
          IF NVL(MI_MAN_PAC, 0) <> 0 THEN
          IF UN_CLASEC = 'RES' OR UN_CLASEC = 'ADR' THEN
            MI_SALDO := MI_RS_TOTALPACAPROPIADO - MI_RS_PAC_PROGRAMADO - MI_VALORG;
            IF MI_SALDO < 0 THEN
              BEGIN
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                  MI_MSGERROR(1).CLAVE := 'CODIGO';
                  MI_MSGERROR(1).VALOR := MI_RSDETALLE_CUENTA;
                  MI_MSGERROR(2).CLAVE := 'SALDO';
                  MI_MSGERROR(2).VALOR := TO_CHAR(MI_SALDO * -1, '000,000,000,00.00');
                  MI_MSGERROR(3).CLAVE := 'APROPIADO';
                  MI_MSGERROR(3).VALOR := TO_CHAR(MI_RS_TOTALPACAPROPIADO, '000,000,000,000.00');
                  MI_MSGERROR(4).CLAVE := 'ACUMULADO';
                  MI_MSGERROR(4).VALOR := TO_CHAR(MI_RS_PAC_PROGRAMADO, '000,000,000,000.00');
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_PAC_DISPONIBLE_RES,
                    UN_TABLAERROR => 'SALDO_PLAN_PPTAL',
                    UN_REEMPLAZOS => MI_MSGERROR
                  );  
              END;
            ELSE
              MI_PAC_DISPONIBLE := MI_SALDO;
            END IF;
          END IF;
          IF UN_CLASEC = 'DMR' THEN
            MI_PAC_DISPONIBLE := MI_RS_TOTALPACAPROPIADO - MI_RS_PAC_PROGRAMADO + MI_VALORG;
          END IF;
          END IF;
        END IF;               
      END IF;
      IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                   UN_NOMBRE    => 'CONTROLAR PAC COMPROMETIDO CONTRA PAC PROGRAMADO', 
                                   UN_MODULO    => PCK_DATOS.FC_MODULOPRESUPUESTO,
                                   UN_FECHA_PAR => SYSDATE), 'NO') = 'SI' THEN
        IF UN_CLASEC = 'REO' OR UN_CLASEC = 'ARO' THEN
          IF (MI_RS_PAC_PROGRAMADO - MI_RS_PAC_COMPROMETIDO) < MI_VALORG THEN
            BEGIN
              RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                MI_MSGERROR(1).CLAVE := 'RUBRO';
                MI_MSGERROR(1).VALOR := MI_RSDETALLE_CUENTA;
                MI_MSGERROR(2).CLAVE := 'PAC';
                MI_MSGERROR(2).VALOR := TO_CHAR(MI_RS_PAC_PROGRAMADO - MI_RS_PAC_COMPROMETIDO, '000,000,000.00');
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE,
                  UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_PAC_DISPONIBLE_EJE,
                  UN_TABLAERROR => 'SALDO_PLAN_PPTAL',
                  UN_REEMPLAZOS => MI_MSGERROR
                );  
            END;
          END IF;
        END IF;
      END IF;
      IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                   UN_NOMBRE    => 'CONTROLAR PAC EJECUTADO CONTRA PAC APROPIADO', 
                                   UN_MODULO    => PCK_DATOS.FC_MODULOPRESUPUESTO,
                                   UN_FECHA_PAR => SYSDATE), 'NO') = 'SI' THEN
        IF UN_CLASEC = 'EGR' OR UN_CLASEC = 'AEG' THEN
          BEGIN
            SELECT   SUM((CASE 
                            WHEN V_PLAN_PRESUPUESTAL.NATURALEZA = 'D' 
                            THEN MODIF_PAC_DEBITO  - MODIF_PAC_CREDITO 
                            ELSE MODIF_PAC_CREDITO - MODIF_PAC_DEBITO 
                          END) + PAC_APROPIADO) TOTALPAC
                    ,SUM(CASE 
                           WHEN V_PLAN_PRESUPUESTAL.NATURALEZA = 'D' 
                           THEN EJE_PPT_DEBITO  - EJE_PPT_CREDITO
                           ELSE EJE_PPT_CREDITO - EJE_PPT_DEBITO 
                         END) EJECUCIONPPT
            INTO     MI_RSPAC_TOTALPAC 
                    ,MI_RSPAC_PACEJECUTADO  
            FROM     V_PLAN_PRESUPUESTAL 
              INNER JOIN V_SALDO_PLAN_PPTAL 
                 ON      V_PLAN_PRESUPUESTAL.COMPANIA  = V_SALDO_PLAN_PPTAL.COMPANIA
                AND      V_PLAN_PRESUPUESTAL.ANO       = V_SALDO_PLAN_PPTAL.ANO
                AND      V_PLAN_PRESUPUESTAL.ID        = V_SALDO_PLAN_PPTAL.ID
            WHERE    V_SALDO_PLAN_PPTAL.COMPANIA    = UN_COMPANIA
              AND    V_SALDO_PLAN_PPTAL.ANO         = UN_ANOH
              AND (
                    (NVL(MI_PAR_PAC_RUBROS_HIJOS,'NO') = 'SI'
                    AND V_SALDO_PLAN_PPTAL.ID = MI_RSDETALLE_CUENTA)
                    OR
                    (NVL(MI_PAR_PAC_RUBROS_HIJOS,'NO') <> 'SI'
                    AND LENGTH(V_SALDO_PLAN_PPTAL.ID) <= LENGTH(MI_RSDETALLE_CUENTA)
                    AND V_SALDO_PLAN_PPTAL.ID          = SUBSTR(MI_RSDETALLE_CUENTA, 1, LENGTH(V_SALDO_PLAN_PPTAL.ID)))
                    ) 
              AND    V_SALDO_PLAN_PPTAL.MES        <= TO_NUMBER(TO_CHAR(UN_FECHA_AUX, 'MM'))
              AND    MAN_PAC                        = -1
            GROUP BY V_SALDO_PLAN_PPTAL.COMPANIA
                    ,V_SALDO_PLAN_PPTAL.ANO
                    ,V_SALDO_PLAN_PPTAL.ID
                    ,V_PLAN_PRESUPUESTAL.NATURALEZA;
            EXCEPTION WHEN NO_DATA_FOUND THEN
              MI_RSPAC_PACEJECUTADO := NULL;
          END;
          IF MI_RSPAC_PACEJECUTADO IS NOT NULL THEN
            IF (MI_RSPAC_TOTALPAC - MI_RSPAC_PACEJECUTADO) < MI_VALORG THEN
              BEGIN
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                  MI_MSGERROR(1).CLAVE := 'RUBRO';
                  MI_MSGERROR(1).VALOR := MI_RSDETALLE_CUENTA;
                  MI_MSGERROR(2).CLAVE := 'PAC';
                  MI_MSGERROR(2).VALOR := TO_CHAR(MI_RSPAC_TOTALPAC - MI_RSPAC_PACEJECUTADO, '000,000,000.00');
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_PAC_DISPONIBLE_EGR,
                    UN_TABLAERROR => 'SALDO_PLAN_PPTAL',
                    UN_REEMPLAZOS => MI_MSGERROR
                  );  
              END;
            END IF;
          END IF;
        END IF;
      END IF;
      MI_VALORG := ROUND(MI_VALORG, 2);
      IF MI_VALORG > 0 OR (MI_VALORG = 0 AND UN_CLASEC = 'ADD') OR (MI_VALORG = 0 AND UN_CLASEC = 'ADR') THEN
        BEGIN
          SELECT   CLASECNTPRES.COLUMNA
                  ,CLASECNTPRES.AFECTACION
                  ,CLASECNTPRES.CODIGO
          INTO     MI_RSC_COLUMNA 
                  ,MI_RSC_AFECTACION 
                  ,MI_RSC_CODIGO
          FROM     TIPO_COMPROBPP 
            INNER JOIN CLASECNTPRES 
               ON      TIPO_COMPROBPP.CLASE     = CLASECNTPRES.CODIGO
          WHERE    TIPO_COMPROBPP.COMPANIA = UN_COMPANIA
            AND    TIPO_COMPROBPP.CODIGO   = UN_TIPOC;
          EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_RSC_CODIGO := NULL;
        END;
        IF MI_RSC_CODIGO IS NOT NULL THEN                      
          BEGIN
            SELECT   CASE 
                       WHEN NATURALEZA='D' 
                       THEN VALOR_DEBITO         - VALOR_CREDITO 
                         - (DEBITO_AFECTADO      - CREDITO_AFECTADO) 
                         + (MODIFICACION_DEBITO  - MODIFICACION_CREDITO)
                       ELSE VALOR_CREDITO        - VALOR_DEBITO 
                         - (CREDITO_AFECTADO     - DEBITO_AFECTADO) 
                         + (MODIFICACION_CREDITO - MODIFICACION_DEBITO) 
                     END VALOR
            INTO     MI_RS_VALOR
            FROM     DETALLE_COMPROBANTE_PPTAL
            WHERE    COMPANIA    = UN_COMPANIA
              AND    ANO         = UN_ANOH            
              AND    TIPO_CPTE   = UN_TIPOH
              AND    COMPROBANTE = UN_NUMEROH
              AND    CUENTA      = MI_RSDETALLE_CODIGO_CUENTA
              AND    CONSECUTIVO = MI_RSDETALLE_CONSECUTIVO;
            EXCEPTION WHEN NO_DATA_FOUND THEN
              MI_RS_VALOR := -1;
          END;
          IF MI_RS_VALOR >= 0 THEN
            BEGIN
              BEGIN
                MI_CAMPOS := 'COMPANIA
                             ,ANO
                             ,TIPO_CPTE
                             ,COMPROBANTE
                             ,CONSECUTIVO
                             ,CUENTA
                             ,FECHA
                             ,NATURALEZA
                             ,DESCRIPCION
                             ,VALOR_DEBITO
                             ,VALOR_CREDITO
                             ,TIPO_DOCUMENTO
                             ,NRO_DOCUMENTO
                             ,CENTRO_COSTO
                             ,TERCERO
                             ,SUCURSAL
                             ,AUXILIAR
                             ,FUENTE_RECURSO
                             ,TIPO_CPTE_AFECT
                             ,ANO_AFECT
                             ,CMPTE_AFECTADO
                             ,CONSECUTIVOPPTO
                             ,CONTRACTUAL
                             ,PAPELES
                             ,PAC_DISPONIBLE
                             ,REFERENCIA
                             ,SECTOR
                             ,PROGRAMA
                             ,SUBPROGRAMA
                             ,COD_PROD_CUIPO
                             ,CODIGO_BPIN
                             ,CODIGO_CCPET
                             ,CODIGO_CPC
                             ,CODIGOUNIDADEJE
                             ,FUENTE_CUIPO 
                             ,CODIGOCCPETREGA
                             ,POLITICA_PUBLICA
                             ,DETALLE_SECTORIAL
                             ,RECURSO_SGR
                             ,IMPUESTO
                             ,VALOR_BASE
                             ,CONSITUACIONFONDOS
                             ,PORCENTAJEDISTRIBUIDO
                             ,MES
                             ,DIA
							 ,TIPOT
                             ,CLASET
                             ,CMPTE_SOLICI_AFECTADO
                             ,DEPENDENCIA';					
                MI_VALORES := '''' || UN_COMPANIA || 
                             ''',' || MI_ANOC || 
                             ',''' || UN_TIPOC || 
                             ''',' || UN_NUMEROC || 
                               ',' || MI_RSDETALLE_CONSECUTIVO || 
                             ',''' || MI_RSDETALLE_CODIGO_CUENTA || 
                             ''',' || MI_FECHA_AUX || 
                             ',''' || MI_RSDETALLE_NATURALEZA || 
                           ''',''' || MI_RSDETALLE_DESCRIPCION || 
                             ''',' || CASE WHEN MI_RSC_COLUMNA = 'D' THEN MI_VALORG ELSE 0 END || 
                               ',' || CASE WHEN MI_RSC_COLUMNA = 'C' THEN MI_VALORG ELSE 0 END || 
                             ',''' || MI_RSDETALLE_TIPO_DOCUMENTO || 
                           ''',''' || MI_RSDETALLE_NRO_DOCUMENTO || 
                           ''',''' || MI_RSDETALLE_CENTRO_COSTO || 
                           ''',''' || MI_RSDETALLE_TERCERO ||
                           ''',''' || MI_RSDETALLE_SUCURSAL || 
                           ''',''' || MI_RSDETALLE_AUXILIAR ||
                           ''',''' || MI_RSDETALLE_FUENTE_RECURSO ||
                           ''',''' || UN_TIPOH ||
                             ''',' || UN_ANOH ||
                               ',' || UN_NUMEROH ||
                               ',' || MI_RSDETALLE_CONSECUTIVO || 
                               ',' || MI_RSDETALLE_CONTRACTUAL || 
                               ',' || MI_RSDETALLE_PAPELES || 
                               ',' || NVL(MI_PAC_DISPONIBLE, 0) || 
                             ',''' || MI_RSDETALLE_REFERENCIA        || ''','
                           || '''' || MI_RSDETALLE_SECTOR            || ''',' 
                           || '''' || MI_RSDETALLE_PROGRAMA          || ''',' 
                           || '''' || MI_RSDETALLE_SUBPROGRAMA       || ''',' 
                           || '''' || MI_RSDETALLE_COD_PROD_CUIPO    || ''',' 
                           || '''' || MI_RSDETALLE_CODIGO_BPIN       || ''',' 
                           || '''' || MI_RSDETALLE_CODIGO_CCPET      || ''',' 
                           || '''' || MI_RSDETALLE_CODIGO_CPC        || ''',' 
                           || '''' || MI_RSDETALLE_CODIGOUNIDADEJE   || ''',' 
                           || '''' || MI_RSDETALLE_FUENTE_CUIPO      || ''',' 
                           || '''' || MI_RSDETALLE_CODIGOCCPETREGA   || ''',' 
                           || '''' || MI_RSDETALLE_POLITICA_PUBLICA  || ''',' 
                           || '''' || MI_RSDETALLE_DETALLE_SECTORIAL || ''','
                           || '''' || MI_RSDETALLE_TIPO_RECURSO_SGR  || '''
                               ,'''|| MI_RSDETALLE_IMPUESTO ||'''
                               ,'''|| MI_RSDETALLE_VALOR_BASE ||''' 
                               ,'  || MI_RSDETALLE_CONSITFON  ||' 
                               ,' || NVL(MI_RSDETALLE_PORDIS, 0) ||'
                               ,' || MI_MESC ||'
                               ,' || MI_DIAC ||'
							,' || '''' || MI_RSTIPOT || '''
                            ,' || '''' || MI_RSCLASET || '''
                               ,''' || MI_RSCMPTE_SOLICI_AFECT ||'''
                            ,' || '''' || MI_RSDEPENDENCIA || '''';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => 'DETALLE_COMPROBANTE_PPTAL', 
                                                      UN_ACCION  => 'I', 
                                                      UN_CAMPOS  => MI_CAMPOS, 
                                                      UN_VALORES => MI_VALORES);                 
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
              END; 
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_DETALLE_AFECTADO,
                    UN_TABLAERROR => 'DETALLE_COMPROBANTE_PPTAL'
                );
            END;
          /*
          IF PCK_DATOS.GL_RTA NOT IN (0) THEN--(INI_CFBARRERA:CC_2605_Cuando se hace una disminucion desmarque el check Afectado de la solicitud)     
              BEGIN
              SELECT NRO_DOCUMENTO 
              INTO MI_NRO_DOCUMENTO
              FROM COMPROBANTE_PPTAL
              WHERE COMPANIA = UN_COMPANIA
                AND ANO = UN_ANOH
                AND TIPO = UN_TIPOH
                AND NUMERO = UN_NUMEROH;
              EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_NRO_DOCUMENTO := 0;
              END;
                
            IF MI_NRO_DOCUMENTO <> 0 THEN
             BEGIN
              MI_TABLA       := 'SOLICITUDDISPONIBILIDAD';
              MI_CAMPOS      := ' AFECTADO = 0';
              MI_CONDICION   := ' NUMERO = ''' || MI_NRO_DOCUMENTO || ''' 
                               AND TIPO_SOLICITUD = 1';
              PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                   UN_ACCION    => 'M',
                                                   UN_CAMPOS    => MI_CAMPOS,
                                                   UN_CONDICION => MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
              END;
            END IF;
        END IF;--(FIN_CFBARRERA:CC_2605)
        */

          END IF;
          --Este bloque de código se comenta porque al insertar en la tabla DETALLE_COMPROBANTE_PPTAL un TRIGER ejecuta la funcion ACTPPTO
          --DECLARE 
          --  MI_TIPOCPTE_INGAIN VARCHAR2(3 , CHAR);
          --BEGIN 
          --  IF UN_CLASEC IN('ING', 'AIN', 'DIN') THEN
          --    IF UN_CLASEC = 'ING' THEN
          --      MI_TIPOCPTE_INGAIN :='OIN';               
          --    ELSIF UN_CLASEC = 'AIN' OR UN_CLASEC = 'DIN' THEN
          --      MI_TIPOCPTE_INGAIN :='MOI';               
          --    END IF;
          --    PCK_PRESUPUESTO1.PR_ACTPPTO(UN_TIPO_CPTE        => MI_TIPOCPTE_INGAIN, 
          --                               UN_COMPANIA         => UN_COMPANIA, 
          --                               UN_FECHA            => UN_FECHA_AUX, 
          --                               UN_CODIGO           => MI_RSDETALLE_CODIGO_CUENTA, 
          --                               UN_CENTRO           => MI_RSDETALLE_CENTRO_COSTO, 
          --                               UN_TERCERO          => MI_RSDETALLE_TERCERO, 
          --                               UN_SUCURSAL         => MI_RSDETALLE_SUCURSAL, 
          --                              UN_AUXILIAR         => MI_RSDETALLE_AUXILIAR, 
          --                               UN_REFERENCIA       => MI_RSDETALLE_REFERENCIA, 
          --                               UN_FUENTE           => MI_RSDETALLE_FUENTE_RECURSO, 
          --                               UN_NATURALEZA       => MI_RSDETALLE_NATURALEZA, 
          --                               UN_DEBITO_ANT       => 0, 
          --                               UN_CREDITO_ANT      => 0, 
          --                               UN_DEBITO           => CASE MI_RSC_COLUMNA WHEN 'D' THEN MI_VALORG ELSE 0 END, 
          --                               UN_CREDITO          => CASE MI_RSC_COLUMNA WHEN 'C' THEN MI_VALORG ELSE 0 END,
          --                               UN_DIFERENCIA       => 0,
          --                              UN_DIFERENCIAANT    => 0, 
          --                               UN_TIPO             => CASE WHEN NVL(MI_RSDETALLE_CONTRACTUAL, 0) <> 0 THEN 'C' ELSE 'N' END, 
          --                               UN_TIPOINGRESO      => CASE WHEN NVL(MI_RSDETALLE_PAPELES    , 0) <> 0 THEN 'P' ELSE 'E' END);
          --  END IF;
          --END;
          --28/12/2016 Eliminado para ver que en trigger realice el proceso
          --IF MI_RSC_AFECTACION = 'R' THEN
          --  PCK_PRESUPUESTO2.PR_AFECTAROTROCOMPROBANTE(UN_COMPANIA, 3, UN_ANOH, UN_TIPOC, UN_TIPOH, UN_NUMEROH, MI_RSDETALLE_CODIGO_CUENTA, 0, 0, CASE MI_RSDETALLE_NATURALEZA WHEN 'C' THEN MI_VALORG ELSE 0 END, CASE (MI_RSDETALLE_NATURALEZA) WHEN 'D' THEN MI_VALORG ELSE 0 END, MI_RSDETALLE_CONSECUTIVO, '0', UN_NUMEROC);
          --ELSE
          --  PCK_PRESUPUESTO2.PR_AFECTAROTROCOMPROBANTE(UN_COMPANIA, 3, UN_ANOH, UN_TIPOC, UN_TIPOH, UN_NUMEROH, MI_RSDETALLE_CODIGO_CUENTA, 0, 0, CASE (MI_RSDETALLE_NATURALEZA) WHEN 'D' THEN MI_VALORG ELSE 0 END, CASE (MI_RSDETALLE_NATURALEZA) WHEN 'C' THEN MI_VALORG ELSE 0 END, MI_RSDETALLE_CONSECUTIVO, '0', UN_NUMEROC);
          --END IF;
          IF MI_RSC_CODIGO = 'RES' OR MI_RSC_CODIGO = 'ADR' THEN
            BEGIN
              BEGIN
                MI_CAMPOS  := 'COMPANIA
                              ,ANO
                              ,TIPO_CPTE
                              ,COMPROBANTE
                              ,CONSECUTIVO
                              ,CUENTA
                              ,FECHA
                              ,MOV_DEBITO
                              ,MOV_CREDITO';
                MI_VALORES := '''' || UN_COMPANIA || 
                              ''','|| MI_ANOC ||
                            ','''  || UN_TIPOC || 
                             ''',' || UN_NUMEROC || 
                             ',''' || MI_RSDETALLE_CONSECUTIVO || 
                          ''','''  || MI_RSDETALLE_CODIGO_CUENTA || 
                            ''','  || MI_FECHA_AUX || 
                              ','  || NVL(MI_VALORPAC,0) || ', 0';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => 'PACPROGRAMADO', 
                                                      UN_ACCION  => 'I', 
                                                      UN_CAMPOS  => MI_CAMPOS, 
                                                      UN_VALORES => MI_VALORES);    
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE,
                  UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_INSER_PACPROGRAMADO,
                  UN_TABLAERROR => 'PACPROGRAMADO'
                );
            END;
            PCK_PRESUPUESTO1.PR_ACTPPTO(UN_TIPO_CPTE        => 'PAP', 
                                        UN_COMPANIA         => UN_COMPANIA, 
                                        UN_FECHA            => UN_FECHA_AUX, 
                                        UN_CODIGO           => MI_RSDETALLE_CODIGO_CUENTA, 
                                        UN_CENTRO           => MI_RSDETALLE_CENTRO_COSTO, 
                                        UN_TERCERO          => MI_RSDETALLE_TERCERO, 
                                        UN_SUCURSAL         => MI_RSDETALLE_SUCURSAL, 
                                        UN_AUXILIAR         => MI_RSDETALLE_AUXILIAR, 
                                        UN_REFERENCIA       => MI_RSDETALLE_REFERENCIA, 
                                        UN_FUENTE           => MI_RSDETALLE_FUENTE_RECURSO, 
                                        UN_NATURALEZA       => MI_RSDETALLE_NATURALEZA, 
                                        UN_DEBITO_ANT       => 0, 
                                        UN_CREDITO_ANT      => 0, 
                                        UN_DEBITO           => NVL(MI_VALORPAC,0), 
                                        UN_CREDITO          => 0,
                                        UN_DIFERENCIA       => NVL(MI_VALORPAC,0),
                                        UN_DIFERENCIAANT    => 0, 
                                        UN_TIPO             => CASE 
                                                                 WHEN NVL(MI_RSDETALLE_CONTRACTUAL, 0) <> 0 
                                                                 THEN 'C' 
                                                                 ELSE 'N' 
                                                               END, 
                                        UN_TIPOINGRESO      => CASE 
                                                                 WHEN NVL(MI_RSDETALLE_PAPELES, 0) <> 0 
                                                                 THEN 'P' 
                                                                 ELSE 'E' 
                                                               END);
          ELSIF MI_RSC_CODIGO = 'DMR' THEN
            BEGIN
              BEGIN
                MI_CAMPOS := 'COMPANIA
                             ,ANO
                             ,TIPO_CPTE
                             ,COMPROBANTE
                             ,CONSECUTIVO
                             ,CUENTA
                             ,FECHA
                             ,MOV_DEBITO
                             ,MOV_CREDITO';
                MI_VALORES := '''' || UN_COMPANIA || 
                             ''',' || MI_ANOC || 
                             ',''' || UN_TIPOC || 
                             ''',' ||UN_NUMEROC || 
                             ',''' || MI_RSDETALLE_CONSECUTIVO || 
                           ''',''' || MI_RSDETALLE_CODIGO_CUENTA || 
                             ''',' || MI_FECHA_AUX || 
                             ',0,' || MI_VALORG;
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => 'PACPROGRAMADO', 
                                                      UN_ACCION  => 'I', 
                                                      UN_CAMPOS  => MI_CAMPOS, 
                                                      UN_VALORES => MI_VALORES); 
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE,
                  UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_INSER_PACPROGRAMADO,
                  UN_TABLAERROR => 'PACPROGRAMADO'
                );
            END;
            PCK_PRESUPUESTO1.PR_ACTPPTO(UN_TIPO_CPTE        => 'PAP', 
                                        UN_COMPANIA         => UN_COMPANIA, 
                                        UN_FECHA            => UN_FECHA_AUX, 
                                        UN_CODIGO           => MI_RSDETALLE_CODIGO_CUENTA, 
                                        UN_CENTRO           => MI_RSDETALLE_CENTRO_COSTO, 
                                        UN_TERCERO          => MI_RSDETALLE_TERCERO, 
                                        UN_SUCURSAL         => MI_RSDETALLE_SUCURSAL, 
                                        UN_AUXILIAR         => MI_RSDETALLE_AUXILIAR, 
                                        UN_REFERENCIA       => MI_RSDETALLE_REFERENCIA, 
                                        UN_FUENTE           => MI_RSDETALLE_FUENTE_RECURSO, 
                                        UN_NATURALEZA       => MI_RSDETALLE_NATURALEZA, 
                                        UN_DEBITO_ANT       => 0, 
                                        UN_CREDITO_ANT      => 0, 
                                        UN_DEBITO           => 0, 
                                        UN_CREDITO          => NVL(MI_VALORG,0),
                                        UN_DIFERENCIA       => NVL(MI_VALORG,0)* -1,
                                        UN_DIFERENCIAANT    => 0, 
                                        UN_TIPO             => CASE 
                                                                 WHEN NVL(MI_RSDETALLE_CONTRACTUAL, 0) <> 0 
                                                                 THEN 'C' 
                                                                 ELSE 'N' 
                                                               END, 
                                        UN_TIPOINGRESO      => CASE 
                                                                 WHEN NVL(MI_RSDETALLE_PAPELES, 0) <> 0 
                                                                 THEN 'P' 
                                                                 ELSE 'E' 
                                                               END);              
          END IF;
          IF MI_RSC_CODIGO = 'REO' OR MI_RSC_CODIGO = 'ARO' THEN
            IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                         UN_NOMBRE    => 'CONTROLAR PAC COMPROMETIDO CONTRA PAC APROPIADO', 
                                         UN_MODULO    => PCK_DATOS.FC_MODULOPRESUPUESTO,
                                         UN_FECHA_PAR => SYSDATE), 'NO') = 'SI' THEN
              IF (MI_RS_TOTALPACAPROPIADO - MI_RS_PAC_COMPROMETIDO) < MI_VALORG THEN
                BEGIN
                  RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                    MI_MSGERROR(1).CLAVE := 'RUBRO';
                    MI_MSGERROR(1).VALOR := MI_RSDETALLE_CUENTA;
                    MI_MSGERROR(2).CLAVE := 'PAC';
                    MI_MSGERROR(2).VALOR := TO_CHAR(MI_RS_TOTALPACAPROPIADO - MI_RS_PAC_COMPROMETIDO, '000,000,000.00');           
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_PAC_DISPONIBLE_REO1,
                      UN_TABLAERROR => 'SALDO_PLAN_PPTAL',
                      UN_REEMPLAZOS => MI_MSGERROR
                    );
                END;  
              END IF;
            END IF;
            IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                         UN_NOMBRE    => 'CONTROLAR PAC COMPROMETIDO CONTRA PAC PROGRAMADO', 
                                         UN_MODULO    => PCK_DATOS.FC_MODULOPRESUPUESTO,
                                         UN_FECHA_PAR => SYSDATE), 'NO') = 'SI' THEN
              IF (MI_RS_PAC_PROGRAMADO - MI_RS_PAC_COMPROMETIDO) < MI_VALORG THEN
                BEGIN
                  RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                    MI_MSGERROR(1).CLAVE := 'RUBRO';
                    MI_MSGERROR(1).VALOR := MI_RSDETALLE_CUENTA;
                    MI_MSGERROR(2).CLAVE := 'PAC';
                    MI_MSGERROR(2).VALOR := TO_CHAR(MI_RS_PAC_PROGRAMADO - MI_RS_PAC_COMPROMETIDO, '000,000,000.00');
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_TABLAERROR => 'SALDO_PLAN_PPTAL',
                      UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_PAC_DISPONIBLE_EJE,
                      UN_REEMPLAZOS => MI_MSGERROR
                    );  
                END;        
              END IF;
            END IF;        
            BEGIN
              BEGIN
                MI_CAMPOS := 'COMPANIA
                             ,ANO
                             ,TIPO_CPTE
                             ,COMPROBANTE
                             ,CONSECUTIVO
                             ,CUENTA
                             ,FECHA
                             ,MOV_DEBITO
                             ,MOV_CREDITO';
                MI_VALORES := '''' || UN_COMPANIA || 
                             ''',' || MI_ANOC || 
                             ',''' || UN_TIPOC || 
                             ''',' || UN_NUMEROC || 
                             ',''' || MI_RSDETALLE_CONSECUTIVO || 
                           ''',''' || MI_RSDETALLE_CODIGO_CUENTA || 
                             ''',' || MI_FECHA_AUX || 
                               ',' || MI_VALORG || ',0';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => 'PACCOMPROMETIDO', 
                                                      UN_ACCION  => 'I', 
                                                      UN_CAMPOS  => MI_CAMPOS, 
                                                      UN_VALORES => MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE,
                  UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_INSER_PACCOMPROMETIDO,
                  UN_TABLAERROR => 'PACCOMPROMETIDO'
                );
            END;
            PCK_PRESUPUESTO1.PR_ACTPPTO(UN_TIPO_CPTE        => 'PAM', 
                                        UN_COMPANIA         => UN_COMPANIA, 
                                        UN_FECHA            => UN_FECHA_AUX, 
                                        UN_CODIGO           => MI_RSDETALLE_CODIGO_CUENTA, 
                                        UN_CENTRO           => MI_RSDETALLE_CENTRO_COSTO, 
                                        UN_TERCERO          => MI_RSDETALLE_TERCERO, 
                                        UN_SUCURSAL         => MI_RSDETALLE_SUCURSAL, 
                                        UN_AUXILIAR         => MI_RSDETALLE_AUXILIAR, 
                                        UN_REFERENCIA       => MI_RSDETALLE_REFERENCIA, 
                                        UN_FUENTE           => MI_RSDETALLE_FUENTE_RECURSO, 
                                        UN_NATURALEZA       => MI_RSDETALLE_NATURALEZA, 
                                        UN_DEBITO_ANT       => 0, 
                                        UN_CREDITO_ANT      => 0, 
                                        UN_DEBITO           => NVL(MI_VALORG,0), 
                                        UN_CREDITO          => 0,
                                        UN_DIFERENCIA       => NVL(MI_VALORG,0),
                                        UN_DIFERENCIAANT    => 0, 
                                        UN_TIPO             => CASE 
                                                                 WHEN NVL(MI_RSDETALLE_CONTRACTUAL, 0) <> 0 
                                                                 THEN 'C' 
                                                                 ELSE 'N' 
                                                               END, 
                                        UN_TIPOINGRESO      => CASE 
                                                                 WHEN NVL(MI_RSDETALLE_PAPELES, 0) <> 0
                                                                 THEN 'P'
                                                                 ELSE 'E'
                                                               END);              
          ELSIF MI_RSC_CODIGO = 'DRO' THEN
            BEGIN
              BEGIN
                MI_CAMPOS := 'COMPANIA
                             ,ANO
                             ,TIPO_CPTE
                             ,COMPROBANTE
                             ,CONSECUTIVO
                             ,CUENTA
                             ,FECHA
                             ,MOV_DEBITO
                             ,MOV_CREDITO';
                MI_VALORES := '''' || UN_COMPANIA || 
                             ''',' || MI_ANOC || 
                             ',''' || UN_TIPOC || 
                             ''',' || UN_NUMEROC || 
                             ',''' || MI_RSDETALLE_CONSECUTIVO || 
                           ''',''' || MI_RSDETALLE_CODIGO_CUENTA || 
                             ''',' || MI_FECHA_AUX || 
                             ',0,' || MI_VALORG;
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => 'PACCOMPROMETIDO', 
                                                      UN_ACCION  => 'I', 
                                                      UN_CAMPOS  => MI_CAMPOS, 
                                                      UN_VALORES => MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE,
                  UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_INSER_PACCOMPROMETIDO,
                  UN_TABLAERROR => 'PACCOMPROMETIDO'
                );
            END; 
            PCK_PRESUPUESTO1.PR_ACTPPTO(UN_TIPO_CPTE        => 'PAM', 
                                         UN_COMPANIA         => UN_COMPANIA, 
                                         UN_FECHA            => UN_FECHA_AUX, 
                                         UN_CODIGO           => MI_RSDETALLE_CODIGO_CUENTA, 
                                         UN_CENTRO           => MI_RSDETALLE_CENTRO_COSTO, 
                                         UN_TERCERO          => MI_RSDETALLE_TERCERO, 
                                         UN_SUCURSAL         => MI_RSDETALLE_SUCURSAL, 
                                         UN_AUXILIAR         => MI_RSDETALLE_AUXILIAR, 
                                         UN_REFERENCIA       => MI_RSDETALLE_REFERENCIA, 
                                         UN_FUENTE           => MI_RSDETALLE_FUENTE_RECURSO, 
                                         UN_NATURALEZA       => MI_RSDETALLE_NATURALEZA, 
                                         UN_DEBITO_ANT       => 0, 
                                         UN_CREDITO_ANT      => 0, 
                                         UN_DEBITO           => 0, 
                                         UN_CREDITO          => NVL(MI_VALORG,0),
                                         UN_DIFERENCIA       => NVL(MI_VALORG,0) * -1,
                                         UN_DIFERENCIAANT    => 0, 
                                         UN_TIPO             => CASE 
                                                                  WHEN NVL(MI_RSDETALLE_CONTRACTUAL, 0) <> 0 
                                                                  THEN 'C' 
                                                                  ELSE 'N' 
                                                                  END, 
                                         UN_TIPOINGRESO      => CASE 
                                                                  WHEN NVL(MI_RSDETALLE_PAPELES, 0) <> 0 
                                                                  THEN 'P' 
                                                                  ELSE 'E' 
                                                                END);  
          END IF;                                
          IF MI_RSC_CODIGO = 'EGR' OR MI_RSC_CODIGO = 'AEG' THEN
            IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                         UN_NOMBRE    => 'CONTROLAR PAC EJECUTADO CONTRA PAC APROPIADO', 
                                         UN_MODULO    => PCK_DATOS.FC_MODULOPRESUPUESTO,
                                         UN_FECHA_PAR => SYSDATE), 'NO') = 'I' THEN
              IF (MI_RS_TOTALPACAPROPIADO - MI_RS_PAC_EJECUTADO) < MI_VALORG THEN
                BEGIN
                  RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                    MI_MSGERROR(1).CLAVE := 'RUBRO';
                    MI_MSGERROR(1).VALOR := MI_RSDETALLE_CUENTA;
                    MI_MSGERROR(2).CLAVE := 'PAC';
                    MI_MSGERROR(2).VALOR := TO_CHAR(MI_RS_TOTALPACAPROPIADO - MI_RS_PAC_EJECUTADO, '000,000,000.00');
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_PAC_DISPONIBLE_EJE1,
                      UN_TABLAERROR => 'SALDO_PLAN_PPTAL',
                      UN_REEMPLAZOS => MI_MSGERROR
                    );  
                END;
              END IF;
            END IF;    
            IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                         UN_NOMBRE    => 'CONTROLAR PAC EJECUTADO CONTRA PAC COMPROMETIDO', 
                                         UN_MODULO    => PCK_DATOS.FC_MODULOPRESUPUESTO,
                                         UN_FECHA_PAR => SYSDATE), 'NO') = 'SI' THEN
              IF (MI_RS_PAC_COMPROMETIDO - MI_RS_PAC_EJECUTADO) < MI_VALORG THEN
                BEGIN
                  RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                    MI_MSGERROR(1).CLAVE := 'RUBRO';
                    MI_MSGERROR(1).VALOR := MI_RSDETALLE_CUENTA;
                    MI_MSGERROR(2).CLAVE := 'PAC';
                    MI_MSGERROR(2).VALOR := TO_CHAR(MI_RS_PAC_COMPROMETIDO - MI_RS_PAC_EJECUTADO, '000,000,000.00');
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_PAC_DISPONIBLE_EJE1,
                      UN_TABLAERROR => 'SALDO_PLAN_PPTAL',
                      UN_REEMPLAZOS => MI_MSGERROR
                    );  
                END;
              END IF;
            END IF;
            BEGIN
              BEGIN
                MI_CAMPOS := 'COMPANIA, ANO, TIPO_CPTE, COMPROBANTE, CONSECUTIVO, CUENTA, FECHA, MOV_DEBITO, MOV_CREDITO';
                MI_VALORES := '''' || UN_COMPANIA || ''',' || MI_ANOC   || '  , 
                              '''  || UN_TIPOC    || ''',' ||UN_NUMEROC || ', ''' || MI_RSDETALLE_CONSECUTIVO || ''',
                              '''  || MI_RSDETALLE_CODIGO_CUENTA  || ''',
                                '  || MI_FECHA_AUX || ',
                                '  || MI_VALORG || ',0';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => 'PACEJECUTADO', 
                                                      UN_ACCION  => 'I', 
                                                      UN_CAMPOS  => MI_CAMPOS, 
                                                      UN_VALORES => MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE,
                  UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_INSER_PACEJECUTADO,
                  UN_TABLAERROR => 'PACEJECUTADO'
                );
            END;
            PCK_PRESUPUESTO1.PR_ACTPPTO(UN_TIPO_CPTE        => 'PAE', 
                                        UN_COMPANIA         => UN_COMPANIA, 
                                        UN_FECHA            => UN_FECHA_AUX, 
                                        UN_CODIGO           => MI_RSDETALLE_CODIGO_CUENTA, 
                                        UN_CENTRO           => MI_RSDETALLE_CENTRO_COSTO, 
                                        UN_TERCERO          => MI_RSDETALLE_TERCERO, 
                                        UN_SUCURSAL         => MI_RSDETALLE_SUCURSAL, 
                                        UN_AUXILIAR         => MI_RSDETALLE_AUXILIAR, 
                                        UN_REFERENCIA       => MI_RSDETALLE_REFERENCIA, 
                                        UN_FUENTE           => MI_RSDETALLE_FUENTE_RECURSO, 
                                        UN_NATURALEZA       => MI_RSDETALLE_NATURALEZA, 
                                        UN_DEBITO_ANT       => 0, 
                                        UN_CREDITO_ANT      => 0, 
                                        UN_DEBITO           => NVL(MI_VALORG,0), 
                                        UN_CREDITO          => 0,
                                        UN_DIFERENCIA       => NVL(MI_VALORG,0),
                                        UN_DIFERENCIAANT    => 0, 
                                        UN_TIPO             => CASE 
                                                                 WHEN NVL(MI_RSDETALLE_CONTRACTUAL, 0) <> 0 
                                                                 THEN 'C' 
                                                                 ELSE 'N' 
                                                               END, 
                                        UN_TIPOINGRESO      => CASE 
                                                                 WHEN NVL(MI_RSDETALLE_PAPELES, 0) <> 0 
                                                                 THEN 'P' 
                                                                 ELSE 'E' 
                                                               END);  
          ELSIF MI_RSC_CODIGO = 'DEG' THEN
            BEGIN
              BEGIN
                MI_CAMPOS := 'COMPANIA
                             ,ANO
                             ,TIPO_CPTE
                             ,COMPROBANTE
                             ,CONSECUTIVO
                             ,CUENTA
                             ,FECHA
                             ,MOV_DEBITO
                             ,MOV_CREDITO';
                MI_VALORES := '''' || UN_COMPANIA || 
                             ''',' || MI_ANOC || 
                             ',''' || UN_TIPOC || 
                             ''',' || UN_NUMEROC || 
                             ',''' || MI_RSDETALLE_CONSECUTIVO || 
                           ''',''' || MI_RSDETALLE_CODIGO_CUENTA || 
                             ''',' || MI_FECHA_AUX || 
                             ',0,' || MI_VALORG;
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => 'PACEJECUTADO', 
                                                      UN_ACCION  => 'I', 
                                                      UN_CAMPOS  => MI_CAMPOS, 
                                                      UN_VALORES => MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE,
                  UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_INSER_PACEJECUTADO,
                  UN_TABLAERROR => 'PACEJECUTADO'
                );
            END; 
            PCK_PRESUPUESTO1.PR_ACTPPTO(UN_TIPO_CPTE        => 'PAE', 
                                        UN_COMPANIA         => UN_COMPANIA, 
                                        UN_FECHA            => UN_FECHA_AUX, 
                                        UN_CODIGO           => MI_RSDETALLE_CODIGO_CUENTA, 
                                        UN_CENTRO           => MI_RSDETALLE_CENTRO_COSTO, 
                                        UN_TERCERO          => MI_RSDETALLE_TERCERO, 
                                        UN_SUCURSAL         => MI_RSDETALLE_SUCURSAL, 
                                        UN_AUXILIAR         => MI_RSDETALLE_AUXILIAR, 
                                        UN_REFERENCIA       => MI_RSDETALLE_REFERENCIA, 
                                        UN_FUENTE           => MI_RSDETALLE_FUENTE_RECURSO, 
                                        UN_NATURALEZA       => MI_RSDETALLE_NATURALEZA, 
                                        UN_DEBITO_ANT       => 0, 
                                        UN_CREDITO_ANT      => 0, 
                                        UN_DEBITO           => 0, 
                                        UN_CREDITO          => NVL(MI_VALORG,0),
                                        UN_DIFERENCIA       => NVL(MI_VALORG,0) * -1,
                                        UN_DIFERENCIAANT    => 0, 
                                        UN_TIPO             => CASE 
                                                                 WHEN NVL(MI_RSDETALLE_CONTRACTUAL, 0) <> 0 
                                                                 THEN 'C' 
                                                                 ELSE 'N' 
                                                               END, 
                                        UN_TIPOINGRESO      => CASE 
                                                                 WHEN NVL(MI_RSDETALLE_PAPELES, 0) <> 0 
                                                                 THEN 'P' 
                                                                 ELSE 'E' 
                                                               END); 
          END IF;
        ELSE
          BEGIN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
            MI_MSGERROR(1).CLAVE := 'CLASE';
            MI_MSGERROR(1).VALOR := MI_RSC_CODIGO;
            MI_MSGERROR(2).CLAVE := 'TIPO';
            MI_MSGERROR(2).VALOR := UN_TIPOC;
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_CLASE_HERRADA,
              UN_TABLAERROR => 'SALDO_PLAN_PPTAL',
              UN_REEMPLAZOS => MI_MSGERROR
            );  
          END;
        END IF;
      END IF;
      IF MI_VALORG = 0 THEN
        BEGIN
          SELECT   CLASECNTPRES.CODIGO
          INTO     MI_RSC_CODIGO
          FROM     TIPO_COMPROBPP 
            INNER JOIN CLASECNTPRES 
               ON      TIPO_COMPROBPP.CLASE = CLASECNTPRES.CODIGO
          WHERE    TIPO_COMPROBPP.COMPANIA = UN_COMPANIA
            AND    TIPO_COMPROBPP.CODIGO   = UN_TIPOC;
          EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_RSC_CODIGO := NULL;
        END;
        IF MI_RSC_CODIGO IS NOT NULL THEN
          IF MI_RSC_CODIGO = 'AIC' OR MI_RSC_CODIGO = 'AIN' THEN
            BEGIN
              BEGIN
                MI_CAMPOS := 'COMPANIA
                             ,ANO
                             ,TIPO_CPTE
                             ,COMPROBANTE
                             ,CONSECUTIVO
                             ,CUENTA
                             ,FECHA
                             ,NATURALEZA
                             ,DESCRIPCION
                             ,VALOR_DEBITO
                             ,VALOR_CREDITO
                             ,TIPO_DOCUMENTO
                             ,NRO_DOCUMENTO
                             ,CENTRO_COSTO
                             ,TERCERO
                             ,SUCURSAL
                             ,AUXILIAR
                             ,FUENTE_RECURSO
                             ,ANO_AFECT
                             ,TIPO_CPTE_AFECT
                             ,CMPTE_AFECTADO
                             ,CONSECUTIVOPPTO
                             ,CONTRACTUAL
                             ,PAPELES
                             ,PAC_DISPONIBLE
                             ,REFERENCIA
                             ,SECTOR
                             ,PROGRAMA
                             ,SUBPROGRAMA
                             ,COD_PROD_CUIPO
                             ,CODIGO_BPIN
                             ,CODIGO_CCPET
                             ,CODIGO_CPC
                             ,CODIGOUNIDADEJE
                             ,FUENTE_CUIPO 
                             ,CODIGOCCPETREGA
                             ,POLITICA_PUBLICA
                             ,DETALLE_SECTORIAL
                             ,RECURSO_SGR
                             ,CONSITUACIONFONDOS
                             ,PORCENTAJEDISTRIBUIDO';
                MI_VALORES := '''' || UN_COMPANIA || 
                             ''',' || MI_ANOC || 
                             ',''' || UN_TIPOC || 
                             ''',' || UN_NUMEROC || 
                               ',' || MI_RSDETALLE_CONSECUTIVO || 
                             ',''' || MI_RSDETALLE_CODIGO_CUENTA || 
                             ''',' || MI_FECHA_AUX || 
                             ',''' || MI_RSDETALLE_NATURALEZA || 
                           ''',''' || MI_RSDETALLE_DESCRIPCION || 
                       ''',0,0,''' || MI_RSDETALLE_TIPO_DOCUMENTO || 
                           ''',''' || MI_RSDETALLE_NRO_DOCUMENTO || 
                           ''',''' || MI_RSDETALLE_CENTRO_COSTO || 
                           ''',''' || MI_RSDETALLE_TERCERO ||
                           ''',''' || MI_RSDETALLE_SUCURSAL || 
                           ''',''' || MI_RSDETALLE_AUXILIAR||
                           ''',''' || MI_RSDETALLE_FUENTE_RECURSO ||
                             ''',' || UN_ANOH ||
                             ',''' || UN_TIPOH ||
                             ''',' || UN_NUMEROH ||
                               ',' || MI_RSDETALLE_CONSECUTIVO || 
                               ',' || MI_RSDETALLE_CONTRACTUAL || 
                               ',' || MI_RSDETALLE_PAPELES || 
                               ',' || NVL(MI_PAC_DISPONIBLE, 0) || 
                             ',''' || MI_RSDETALLE_REFERENCIA || ''',' 
                           || '''' || MI_RSDETALLE_SECTOR            || ''',' 
                           || '''' || MI_RSDETALLE_PROGRAMA          || ''',' 
                           || '''' || MI_RSDETALLE_SUBPROGRAMA       || ''',' 
                           || '''' || MI_RSDETALLE_COD_PROD_CUIPO    || ''',' 
                           || '''' || MI_RSDETALLE_CODIGO_BPIN       || ''',' 
                           || '''' || MI_RSDETALLE_CODIGO_CCPET      || ''',' 
                           || '''' || MI_RSDETALLE_CODIGO_CPC        || ''',' 
                           || '''' || MI_RSDETALLE_CODIGOUNIDADEJE   || ''','
                           || '''' || MI_RSDETALLE_FUENTE_CUIPO      || ''',' 
                           || '''' || MI_RSDETALLE_CODIGOCCPETREGA   || ''',' 
                           || '''' || MI_RSDETALLE_POLITICA_PUBLICA  || ''',' 
                           || '''' || MI_RSDETALLE_DETALLE_SECTORIAL || ''','
                           || '''' || MI_RSDETALLE_TIPO_RECURSO_SGR  || ''',' 
                             || MI_RSDETALLE_CONSITFON || 
                               ',' || NVL(MI_RSDETALLE_PORDIS, 0);
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => 'DETALLE_COMPROBANTE_PPTAL', 
                                                      UN_ACCION  => 'I', 
                                                      UN_CAMPOS  => MI_CAMPOS, 
                                                      UN_VALORES => MI_VALORES);                 
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE,
                  UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_INSER_DETCPTEPPTAL,
                  UN_TABLAERROR => 'DETALLE_COMPROBANTE_PPTAL'
                );
            END;
          END IF;
        END IF;
      END IF;   
      --INSERTAR LAS AFECTACIONES EN LA TABLA COMPROBANTEPPTAL_AFECTADOS
      --PRIMERO VERIFICA SI YA EXISTE EL DATO REGISTRADO EN LA TABLA 
      BEGIN
        SELECT  DISTINCT 'X'
        INTO    MI_AUX
        FROM    COMPROBANTE_PPTALAFECTADOS
        WHERE   COMPANIA          = UN_COMPANIA
          AND   ANO               = MI_ANOC
          AND   TIPO_CPTE         = UN_TIPOC
          AND   COMPROBANTE       = UN_NUMEROC
          AND   ANO_AFECT         = UN_ANOH
          AND   TIPO_CPTE_AFECT   = UN_TIPOH
          AND   COMPROBANTE_AFECT = UN_NUMEROH; 
        EXCEPTION WHEN NO_DATA_FOUND THEN
          BEGIN
            BEGIN
              MI_CAMPOS := 'COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,ANO_AFECT,TIPO_CPTE_AFECT,COMPROBANTE_AFECT,CREATED_BY,DATE_CREATED';
              MI_VALORES := '''' || UN_COMPANIA || ''',' || MI_ANOC || ' , 
                            '''  || UN_TIPOC    || ''',' || UN_NUMEROC || ',' || UN_ANOH || ',
                            '''  || UN_TIPOH    || ''',' || UN_NUMEROH || ',
                            '''  || UN_USUARIO  || ''', SYSDATE';
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => 'COMPROBANTE_PPTALAFECTADOS', 
                                                    UN_ACCION  => 'I', 
                                                    UN_CAMPOS  => MI_CAMPOS, 
                                                    UN_VALORES => MI_VALORES);                 
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE,
                UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_INSER_CPTAFECTADOS,
                UN_TABLAERROR => 'COMPROBANTE_PPTALAFECTADOS'
              );
          END;  
      END;
    END LOOP DETALLESCOMPROBANTE; 

          BEGIN
              BEGIN

                MI_CAMPOS := 'CMPTE_AFECTADO = '||UN_NUMEROH||',
                             TIPO_CPTE_AFECT = ''' ||UN_TIPOH||''' ,
                             DATE_MODIFIED = CURRENT_DATE,
                             MODIFIED_BY = '''||UN_USUARIO||''' ,
                             FICHA = '''||MI_FICHA||''' ' ;

                MI_CONDICION := '   COMPANIA  = '''  || UN_COMPANIA ||''' 
                              AND  ANO = '    || MI_ANOC ||'   
                              AND TIPO = '''  || UN_TIPOC || ''' 
                              AND  NUMERO = '    || UN_NUMEROC||'' ;                                

                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'COMPROBANTE_PPTAL',
                                                      UN_ACCION    => 'M',      
                                                      UN_CAMPOS    => MI_CAMPOS,      
                                                      UN_CONDICION => MI_CONDICION);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD   => SQLCODE,
                  UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_COMPROBANTE_PPTAL
                );  
            END;    
    --***************************************************************************************************
    IF (UN_CLASEC = 'RES' OR UN_CLASEC = 'ADR' OR UN_CLASEC = 'DMR') AND NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                                                                   UN_NOMBRE    => 'MANEJA CONTROL DE SOLICITUD DE DISPONIBILIDAD', 
                                                                                                   UN_MODULO    => PCK_DATOS.FC_MODULOPRESUPUESTO,
                                                                                                   UN_FECHA_PAR => SYSDATE), 'NO') = 'SI' THEN
      IF UN_CLASEC = 'RES' THEN
        MI_TIPO_AFECTA := UN_TIPOH;
        MI_CMPT_AFECTA := UN_NUMEROH;

         BEGIN
          SELECT DISTINCT TIPOT,
                          CLASET,
                          CMPTE_SOLICI_AFECTADO,
                          DEPENDENCIA
          INTO  MI_TIPOT
               ,MI_CLASET
               ,MI_CODIGOT
               ,MI_DEPENDENCIAT
          FROM DETALLE_COMPROBANTE_PPTAL
          WHERE COMPANIA  =   UN_COMPANIA
            AND ANO       =   UN_ANOH
            AND TIPO_CPTE =   UN_TIPOH
            AND COMPROBANTE = UN_NUMEROH
            AND CLASET IS NOT NULL;
              EXCEPTION WHEN NO_DATA_FOUND THEN
           MI_TIPOT:='NE';  
      END ;
    
  IF MI_TIPOT<>'NE' THEN

          MI_TABLA:='BPNOVEDADPROYECTO'; 
          MI_CAMPOS:=' CONCOMPROBANTEPPTAL = -1
                      ,DATE_MODIFIED       = SYSDATE
                      ,MODIFIED_BY         = '''||UN_USUARIO||''' ';

          MI_CONDICION:= '    COMPANIA    = '''||UN_COMPANIA||''' 
                          AND TIPOT       = '''||MI_TIPOT||''' 
                          AND CLASET      = '''||MI_CLASET||''' 
                          AND CODIGO      = '''||MI_CODIGOT||''' 
                          AND DEPENDENCIA = '''||MI_DEPENDENCIAT||''' ';

          BEGIN 
              BEGIN
                  PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(
                                    UN_TABLA     => MI_TABLA
                                   ,UN_ACCION    => 'M'
                                   ,UN_CAMPOS    => MI_CAMPOS
                                   ,UN_CONDICION => MI_CONDICION 
                                   ) ;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                  MI_REEMPLAZOS(0).CLAVE:= 'UN_NOVEDAD';
                  MI_REEMPLAZOS(0).CLAVE:= MI_CODIGOT;
                  MI_REEMPLAZOS(1).CLAVE:= 'UN_TIPOT';
                  MI_REEMPLAZOS(1).CLAVE:= MI_TIPOT;
                  MI_REEMPLAZOS(2).CLAVE:= 'UN_DEPENDENCIA';
                  MI_REEMPLAZOS(2).CLAVE:= MI_DEPENDENCIAT;
                  RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
              END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
               PCK_ERR_MSG.RAISE_WITH_MSG(
                           UN_EXC_COD    => SQLCODE
                          ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PPTO_PEDIR_AFECTA_CPTES_6
                          ,UN_TABLAERROR => MI_TABLA
                          ,UN_REEMPLAZOS => MI_REEMPLAZOS
                          );
          END;

   END IF ;
      ELSE
        MI_TIPO_AFECTA := UN_TIPOC;
        MI_CMPT_AFECTA := UN_NUMEROC;
      END IF;
      BEGIN
        SELECT   TIPO_CPTE_AFECT 
                ,COMPROBANTE_AFECT 
        INTO     MI_TIPO_CPTE_AFECT 
                ,MI_CMPTE_AFECTADO
        FROM     COMPROBANTE_PPTALAFECTADOS
        WHERE    COMPANIA    = UN_COMPANIA
          AND    ANO         = UN_ANOH
          AND    TIPO_CPTE   = MI_TIPO_AFECTA
          AND    COMPROBANTE = MI_CMPT_AFECTA
          AND    ROWNUM      = 1
        ORDER BY TIPO_CPTE_AFECT
                ,COMPROBANTE_AFECT;
        EXCEPTION WHEN NO_DATA_FOUND THEN
          RETURN ' ';
      END;
      BEGIN
        SELECT  TIPOT
               ,CLASET
               ,CODIGO
               ,DEPENDENCIA
        INTO    MI_TIPOT
               ,MI_CLASET
               ,MI_CODIGOT
               ,MI_DEPENDENCIAT
        FROM    BPNOVEDADPROYECTO
        WHERE   COMPANIA = UN_COMPANIA
          AND   TIPOT    = MI_TIPO_CPTE_AFECT
          AND   CODIGO   = MI_CMPTE_AFECTADO
          AND   ROWNUM   = 1;
        BEGIN
          BEGIN
            SELECT 'X' 
            INTO   MI_AUX
            FROM   BPTIPONOVEDAD
            WHERE  COMPANIA = UN_COMPANIA
              AND  TIPOT    = UN_TIPOC
              AND  CLASET   = 'P';
            EXCEPTION WHEN NO_DATA_FOUND THEN
              RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN               
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_EQUIVALENTE_BANCOPR,
              UN_TABLAERROR => 'BPTIPONOVEDAD'
            );  
        END;
        BEGIN
          BEGIN
            MI_TABLA := 'BPNOVEDADPROYECTO';
            MI_EXCLUIDOS := 'TIPOT,CLASET,CODIGO,DOCUMENTO_AFECTAR,DEPENDENCIA_AFECTAR,TIPOT_AFECTAR,CLASET_AFECTAR,VOBOBP,FECHA_VOBO,HORA_VOBO';
            MI_CAMPOS    := PCK_SYSMAN_UTL.FC_LISTA_CAMPOS(UN_TABLA     => MI_TABLA,
                                                           UN_EXCLUIDOS => MI_EXCLUIDOS);
            MI_VALORES := 'SELECT ''' || UN_TIPOC || '''
                                 ,''P''
                                 ,' || UN_NUMEROC || '
                                 ,' || MI_CODIGOT || '
                                 ,''' || MI_DEPENDENCIAT || '''
                                 ,''' || MI_TIPOT || '''
                                 ,''' || MI_CLASET || '''
                                 ,-1
                                 ,' || MI_FECHA_AUX || '
                                 ,SYSDATE
                                 ,' || MI_CAMPOS || '
                           FROM   BPNOVEDADPROYECTO
                           WHERE  COMPANIA = ''' || UN_COMPANIA || ''' 
                             AND  TIPOT    = ''' || MI_TIPO_CPTE_AFECT ||'''  
                             AND  CODIGO   = '   || MI_CMPTE_AFECTADO;
            MI_CAMPOS   := MI_EXCLUIDOS || ',' || MI_CAMPOS;     
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA, 
                                                  UN_ACCION  => 'IS', 
                                                  UN_CAMPOS  => MI_CAMPOS, 
                                                  UN_VALORES => MI_VALORES);     
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN               
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_INSER_BPNOVPROYECTO,
            UN_TABLAERROR => 'BPNOVEDADPROYECTO'
          ); 
        END;
        BEGIN
          BEGIN
            MI_TABLA := 'BP_D_NOVEDADPROYECTO';
            MI_EXCLUIDOS := 'TIPOT,CLASET,NOVEDAD,VALORAPROBADO,MODIFICACIONVALORAPROBADO,TIPO_CPTE_AFECT,CMPTE_AFECTADO';
            MI_CAMPOS := PCK_SYSMAN_UTL.FC_LISTA_CAMPOS(UN_TABLA     => MI_TABLA,
                                                        UN_EXCLUIDOS => MI_EXCLUIDOS);
            MI_VALORES := 'SELECT '''||UN_TIPOC||'''
                                 ,''P''
                                 ,'||UN_NUMEROC||'
                                 ,DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO - DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO VALORAPROBAD
                                 ,0
                                 ,'''||MI_TIPO_CPTE_AFECT||''' 
                                 ,'||MI_CMPTE_AFECTADO||'
                                 ,' || MI_TABLA ||'.'||(REPLACE(MI_CAMPOS,',',','||MI_TABLA||'.')) || '
                           FROM   BP_D_NOVEDADPROYECTO 
                             INNER JOIN DETALLE_COMPROBANTE_PPTAL 
                                ON      BP_D_NOVEDADPROYECTO.CODIGO   = DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO 
                               AND      BP_D_NOVEDADPROYECTO.COMPANIA = DETALLE_COMPROBANTE_PPTAL.COMPANIA
                           WHERE  BP_D_NOVEDADPROYECTO.COMPANIA         = '''||UN_COMPANIA||''' 
                             AND  BP_D_NOVEDADPROYECTO.TIPOT            = '''||MI_TIPO_CPTE_AFECT||''' 
                             AND  BP_D_NOVEDADPROYECTO.NOVEDAD          = '||MI_CMPTE_AFECTADO||' 
                             AND  DETALLE_COMPROBANTE_PPTAL.ANO         = TO_NUMBER(TO_CHAR(SYSDATE, ''YY'')) 
                             AND  DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE   = '''||UN_TIPOC||''' 
                             AND  DETALLE_COMPROBANTE_PPTAL.COMPROBANTE = '||UN_NUMEROC;
            MI_CAMPOS   := MI_EXCLUIDOS || ',' || MI_CAMPOS;  
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA, 
                                                  UN_ACCION  => 'IS', 
                                                  UN_CAMPOS  => MI_CAMPOS, 
                                                  UN_VALORES => MI_VALORES);    
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN               
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_INSER_BPDNOVPROYECTO,
            UN_TABLAERROR => 'BP_D_NOVEDADPROYECTO'
          ); 
        END;
        IF UN_CLASEC = 'RES' THEN
          BEGIN
            BEGIN
              MI_TABLA := 'BPNOVEDADPROYECTO';
              MI_CAMPOS := 'ESTADO   = ''AP''
                           ,AFECTADO = -1'; 
              MI_CONDICION := '    COMPANIA = ''' || UN_COMPANIA ||'''
                               AND CLASET   = ''P''
                               AND TIPOT    = ''' || MI_TIPOT || '''
                               AND CODIGO   = '   || MI_CODIGOT;
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA, 
                                                    UN_ACCION    => 'M', 
                                                    UN_CAMPOS    => MI_CAMPOS, 
                                                    UN_CONDICION => MI_CONDICION);    
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN               
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE,
                UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ACTUA_BPNOVPROYECTO,
                UN_TABLAERROR => 'BPNOVEDADPROYECTO'
              ); 
          END;
        ELSE
          BEGIN
            SELECT   TIPO_CPTE_AFECT
                    ,COMPROBANTE_AFECT 
            INTO     MI_TIPO_CPTE_AFECT
                    ,MI_CMPTE_AFECTADO
            FROM     COMPROBANTE_PPTALAFECTADOS
            WHERE    COMPANIA    = UN_COMPANIA
              AND    ANO         = UN_ANOH
              AND    TIPO_CPTE   = MI_TIPO_AFECTA
              AND    COMPROBANTE = MI_CMPT_AFECTA
              AND    ROWNUM      = 1
            ORDER BY TIPO_CPTE_AFECT
                    ,COMPROBANTE_AFECT;
            EXCEPTION WHEN NO_DATA_FOUND THEN
              MI_CMPTE_AFECTADO := 0;
          END;
          BEGIN
            BEGIN
              MI_TABLA := 'BPNOVEDADPROYECTO';
              MI_CAMPOS := 'ESTADO   = ''AP''
                           ,AFECTADO = -1'; 
              MI_CONDICION := '    COMPANIA = ''' || UN_COMPANIA ||'''
                               AND CLASET   = ''P''
                               AND TIPOT    = ''' || MI_TIPO_CPTE_AFECT ||'''
                               AND CODIGO   = '   || MI_CMPTE_AFECTADO;
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA, 
                                                    UN_ACCION    => 'M', 
                                                    UN_CAMPOS    => MI_CAMPOS, 
                                                    UN_CONDICION => MI_CONDICION);    
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN               
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ACTUA_BPNOVPROYECTO,
              UN_TABLAERROR => 'BPNOVEDADPROYECTO'
            ); 
          END;
        END IF;
        BEGIN 
          BEGIN
            MI_TABLA := 'BP_D_NOVEDADPROYECTO';
            MI_MERGEUSING:='SELECT COMPANIA
                                  ,TIPO_CPTE_AFECT
                                  ,CMPTE_AFECTADO
                                  ,ITEM_AFECT
                                  ,MODIFICACIONVALORAPROBADO
                                  ,VALORAPROBADO
                            FROM   BP_D_NOVEDADPROYECTO
                            WHERE  BP_D_NOVEDADPROYECTO.COMPANIA = ''' || UN_COMPANIA || ''' 
                              AND  BP_D_NOVEDADPROYECTO.CLASET   = ''P''
                              AND  BP_D_NOVEDADPROYECTO.TIPOT    = ''' || UN_TIPOC || '''
                              AND  BP_D_NOVEDADPROYECTO.NOVEDAD  = '   || UN_NUMEROC;
            MI_MERGEENLACE:='    TABLA.CODIGO   = VISTA.ITEM_AFECT 
                             AND TABLA.NOVEDAD  = VISTA.CMPTE_AFECTADO 
                             AND TABLA.TIPOT    = VISTA.TIPO_CPTE_AFECT 
                             AND TABLA.COMPANIA = VISTA.COMPANIA';
            IF UN_CLASEC = 'RES' THEN
              MI_MERGEEXISTE := 'UPDATE 
                                   SET TABLA.VALORAPROBADO = TABLA.MODIFICACIONVALORAPROBADO 
                                                           + VISTA.VALORAPROBADO 
                                                           + VISTA.MODIFICACIONVALORAPROBADO';
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA, 
                                                    UN_ACCION      => 'MM', 
                                                    UN_MERGEUSING  => MI_MERGEUSING, 
                                                    UN_MERGEENLACE => MI_MERGEENLACE,
                                                    UN_MERGEEXISTE => MI_MERGEEXISTE);    
            ELSE
              MI_MERGEEXISTE := 'UPDATE 
                                   SET TABLA.MODIFICACIONVALORAPROBADO = TABLA.MODIFICACIONVALORAPROBADO 
                                                                       + VISTA.VALORAPROBADO 
                                                                       + VISTA.MODIFICACIONVALORAPROBADO';
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA, 
                                                    UN_ACCION      => 'MM', 
                                                    UN_MERGEUSING  => MI_MERGEUSING, 
                                                    UN_MERGEENLACE => MI_MERGEENLACE,
                                                    UN_MERGEEXISTE => MI_MERGEEXISTE);    
              MI_MERGEEXISTE := 'UPDATE 
                                   SET TABLA.VALORAPROBADO = TABLA.VALORAPROBADO 
                                                           + VISTA.MODIFICACIONVALORAPROBADO';
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA, 
                                                    UN_ACCION      => 'MM', 
                                                    UN_MERGEUSING  => MI_MERGEUSING, 
                                                    UN_MERGEENLACE => MI_MERGEENLACE,
                                                    UN_MERGEEXISTE => MI_MERGEEXISTE);                      
            END IF;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
              RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN               
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_MERGE_BPDNOVPROYECTO,
              UN_TABLAERROR => 'BP_D_NOVEDADPROYECTO'
            ); 
        END;        
        EXCEPTION WHEN NO_DATA_FOUND THEN
          RETURN ' ';
      END;
    END IF;

    SELECT CLASE
    INTO MI_CLASECOMMPROBANTE
    FROM TIPO_COMPROBPP
    WHERE COMPANIA = UN_COMPANIA
      AND CODIGO   = UN_TIPOC;

     IF MI_CLASECOMMPROBANTE IN ('DMD') THEN

        BEGIN
         SELECT DISTINCT TIPOT,
                CLASET,
                CMPTE_SOLICI_AFECTADO,
                DEPENDENCIA
          INTO  MI_TIPOT
               ,MI_CLASET
               ,MI_CODIGOT
               ,MI_DEPENDENCIAT
          FROM DETALLE_COMPROBANTE_PPTAL
          WHERE COMPANIA  =   UN_COMPANIA
            AND ANO       =   UN_ANOH
            AND TIPO_CPTE =   UN_TIPOH
            AND COMPROBANTE = UN_NUMEROH
             AND CLASET IS NOT NULL;
   EXCEPTION WHEN NO_DATA_FOUND THEN
           MI_TIPOT:='NE';  
      END ;
  IF MI_TIPOT<>'NE' THEN
          MI_CONDICION:= '    COMPANIA    = '''||UN_COMPANIA||''' 
                          AND TIPOT       = '''||MI_TIPOT||''' 
                          AND CLASET      = '''||MI_CLASET||''' 
                          AND CODIGO      = '''||MI_CODIGOT||''' 
                          AND DEPENDENCIA = '''||MI_DEPENDENCIAT||''' ';

         MI_CAMPOS:= ' AFECTADO = 0 ';


         MI_TABLA:= 'BPNOVEDADPROYECTO';

         MI_CONDICION:= '    COMPANIA    = '''||UN_COMPANIA||''' 
                          AND TIPOT       = '''||MI_TIPOT||''' 
                          AND CLASET      = '''||MI_CLASET||''' 
                          AND CODIGO      = '''||MI_CODIGOT||''' 
                          AND DEPENDENCIA = '''||MI_DEPENDENCIAT||''' ';

                BEGIN 
                    BEGIN
                        PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(
                                          UN_TABLA     => MI_TABLA
                                         ,UN_ACCION    => 'M'
                                         ,UN_CAMPOS    => MI_CAMPOS
                                         ,UN_CONDICION => MI_CONDICION 
                                         ) ;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                        RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD    => SQLCODE
                               ,UN_ERROR_COD  => PCK_ERRORES.ERR_BANCO_ACTCHECKPRES
                               ,UN_TABLAERROR => MI_TABLA
                               );
                END;                  
END IF;
     END IF;

              IF INSTR(MI_CAMPOS,'AFECTADO')>0 THEN
                  MI_TABLA  := 'BP_D_NOVEDADPROYECTO';
                  MI_CAMPOS := 'VALORAFECTADO = 0';
                  MI_CONDICION:='    COMPANIA    = '''|| UN_COMPANIA ||'''
                                 AND TIPOT       = '''|| MI_TIPOT||''' 
                                 AND CLASET      = '''|| MI_CLASET||''' 
                                 AND NOVEDAD     = ''' || MI_CODIGOT||''' 
                                 AND DEPENDENCIA = '''|| MI_DEPENDENCIAT||''' ';
                BEGIN 
                    BEGIN
                        PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(
                                          UN_TABLA     => MI_TABLA
                                         ,UN_ACCION    => 'M'
                                         ,UN_CAMPOS    => MI_CAMPOS
                                         ,UN_CONDICION => MI_CONDICION 
                                         ) ;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                        RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD    => SQLCODE
                               ,UN_ERROR_COD  => PCK_ERRORES.ERR_BANCO_ACTCHECKPRES
                               ,UN_TABLAERROR => MI_TABLA
                               );
                END; 

              END IF;

    RETURN ' ';
  END FC_DOCUMENTOAFECTAR_PPTAL;

  -- 8
  PROCEDURE PR_ACTUALIZAVALORDOCUMENTO
    /*
      NAME              : PR_ACTUALIZAVALORDOCUMENTO
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JAVIER VILLATE/ JOSE PASCUAL GOMEZ
      DATE MIGRADOR     : 15/12/2016 
      TIME              : 8:30 AM    
      SOURCE MODULE     : PRESUPUESTO
      MODIFIER          : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MODIFIED     : 15/12/2016 - 26/12/2016 - 03/01/2017 - 13/01/2017
      TIME              : 04:02 PM  - 08:34 AM - 11:06 AM - 10:17 AM 
      DESCRIPTION       : ACTUALIZA EL VALOR DEL DOCUMENTO,VALOR DEBITO, VALOR CREDITO DE LA TABLA COMPROBANTE_PPTAL
                          --CUANDO ELIMINAN,INSERTAN,ACTUALIZAN UN REGISTRO DEL DETALLE DEL MOVIMIENTO
      MODIFICATIONS     : JP. se ajusta para que mantenga el estandar de programación adecuado, se optimiza UPDATE de detalle a comprobante
                          - Adición de campos a la actualización que se le hace al comprobante 
                          presupuestal, para dejarlos en cero cuando no hay detalles.
                          JESSICA : CORRECCION SEGUN ESTANDAR DE PROGRAMACION.
                          DE MANEJO DE ERRORES.
      MODIFIER          : JOSÉ PASCUAL GOMEZ BLANCO
      DATE MODIFIED     : 08/09/2017
      TIME              : 01:00 PM
      MODIFICATIONS     : Adición de parámetro UN_ACTUALIZA_VALOR para saber si 
                          se cambio el valor crédito o débito del comprobante.

      @NAME:  actualizarValorDocumento
      @METHOD:  PUT

    */
  (
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA
   ,UN_ANO              IN PCK_SUBTIPOS.TI_ANIO
   ,UN_TIPOMOVIMIENTO   IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL
   ,UN_MOVIMIENTO       IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTEPPTAL
   ,UN_ACTUALIZA_VALOR  IN PCK_SUBTIPOS.TI_ENTERO
  )
  AS 
    MI_TABLA            PCK_SUBTIPOS.TI_TABLA; 
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
    MI_DATO             PCK_SUBTIPOS.TI_TEXTO1;
    MI_DETALLES         NUMBER(5) :=0;
  BEGIN

    BEGIN
      SELECT  DISTINCT 'X' 
      INTO    MI_DATO
      FROM    DETALLE_COMPROBANTE_PPTAL
      WHERE   COMPANIA    = UN_COMPANIA
        AND   ANO         = UN_ANO
        AND   TIPO_CPTE   = UN_TIPOMOVIMIENTO
        AND   COMPROBANTE = UN_MOVIMIENTO;  
      EXCEPTION WHEN NO_DATA_FOUND THEN
        BEGIN
          BEGIN
            MI_TABLA := 'COMPROBANTE_PPTAL';
            MI_CAMPOS:= 'VLR_DOCUMENTO        = 0
                        ,DEBITO               = 0             
                        ,CREDITO              = 0
                        ,DEBITO_AFECTADO      = 0
                        ,CREDITO_AFECTADO     = 0
                        ,DEBITO_AFECTADOCNT   = 0
                        ,CREDITO_AFECTADOCNT  = 0
                        ,MODIFICACION_DEBITO  = 0
                        ,MODIFICACION_CREDITO = 0';
            MI_CONDICION:='    COMPROBANTE_PPTAL.COMPANIA =''' || UN_COMPANIA ||'''
                           AND COMPROBANTE_PPTAL.ANO      =''' || UN_ANO ||'''
                           AND COMPROBANTE_PPTAL.TIPO     =''' || UN_TIPOMOVIMIENTO ||'''
                           AND COMPROBANTE_PPTAL.NUMERO   ='   || UN_MOVIMIENTO || '';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA, 
                                                   UN_ACCION     => 'M', 
                                                   UN_CAMPOS     => MI_CAMPOS, 
                                                   UN_CONDICION  => MI_CONDICION);              
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
              RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
          END;
          EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_COMPR_DEBITOCREDITOCERO,
              UN_TABLAERROR => 'COMPROBANTE_PPTAL'
            );
        END;    
    END;     
    BEGIN
      BEGIN
        SELECT COUNT(DETALLE_COMPROBANTE_PPTAL.COMPANIA) DETALLES
        INTO   MI_DETALLES  
        FROM   DETALLE_COMPROBANTE_PPTAL 
        WHERE  COMPANIA    = UN_COMPANIA 
          AND  ANO         = UN_ANO 
          AND  TIPO_CPTE   = UN_TIPOMOVIMIENTO
          AND  COMPROBANTE = UN_MOVIMIENTO;
        IF MI_DETALLES >0 AND UN_ACTUALIZA_VALOR<>0 THEN
          MI_TABLA := 'COMPROBANTE_PPTAL';
          MI_CAMPOS:= '(VLR_DOCUMENTO,DEBITO,CREDITO) = (SELECT CASE WHEN SUM(VALOR_DEBITO)>0 THEN 
                                                                 SUM(VALOR_DEBITO) ELSE
                                                                 SUM(VALOR_CREDITO) END VLR_DOCUMENTO
                                                                 ,SUM(VALOR_DEBITO) DEBITO
                                                                 ,SUM(VALOR_CREDITO) CREDITO
                                                         FROM   DETALLE_COMPROBANTE_PPTAL 
                                                         WHERE  COMPANIA    =''' || UN_COMPANIA ||'''
                                                           AND  ANO         ='   || UN_ANO  || '
                                                           AND  TIPO_CPTE   =''' || UN_TIPOMOVIMIENTO ||'''
                                                           AND  COMPROBANTE ='   || UN_MOVIMIENTO ||')';
          MI_CONDICION:='    COMPROBANTE_PPTAL.COMPANIA    =''' || UN_COMPANIA || '''
                         AND COMPROBANTE_PPTAL.ANO         ='   || UN_ANO      || '
                         AND COMPROBANTE_PPTAL.TIPO        =''' || UN_TIPOMOVIMIENTO || '''
                         AND COMPROBANTE_PPTAL.NUMERO      ='   || UN_MOVIMIENTO || '';
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'COMPROBANTE_PPTAL', 
                                                 UN_ACCION     => 'M', 
                                                 UN_CAMPOS     => MI_CAMPOS, 
                                                 UN_CONDICION  => MI_CONDICION);                          
        END IF;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_COMPR_ACT_DEBITOCREDITO,
          UN_TABLAERROR => 'COMPROBANTE_PPTAL'
        );
    END;
  END PR_ACTUALIZAVALORDOCUMENTO;

  -- 9
  PROCEDURE PR_CREAR_SALDOPLANPPTAL 
    /*
      NAME              : PR_CREAR_SALDOPLANPPTAL
      AUTHORS           : SYSMAN LTDA
      AUTHOR MIGRACION  : JAVIER RODRIGUEZ
      DATE MIGRADOR     : 16/12/2016 
      TIME              : 12:30 PM     
      MODIFIER          : JOSÉ PASCUAL GOMEZ / JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MODIFIED     : 26/12/2016  - 13/01/2017
      TIME              :  - 11:00 AM
      DESCRIPTION       : CREA LAS CUENTAS EN SALDO_PLAN_PPTAL DE ACUERDO A LOS VALORES INGRESADOS POR PARAMETRO 
      MODIFICATIONS     : MODIFICACION DE LA RUTINA DE CREAR UNA CUENTA CONTABLE Y ADAPTARLA PARA CREAR UN  RUBRO PRESUPUESTAL
                          SE INCLUYE LAS EXCEPCIONES DE NEGOCIO BAJO EL ESTANDAR
                          JESSICA : CORRECCION SEGUN ESTANDAR DE PROGRAMACION.
      @NAME:  insertarSaldosPlanPresupuestal
      @METHOD:  POST
    */
  ( 
    UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA  
   ,UN_ANIO                    IN PCK_SUBTIPOS.TI_ANIO     
   ,UN_CODIGO                  IN PCK_SUBTIPOS.TI_CODIGOPPTAL
  )      
  AS
    MI_TABLA     PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS    VARCHAR2(32000 CHAR);
    MI_VALORES   VARCHAR2(32000 CHAR);
    MI_SALDOS    PCK_SUBTIPOS.TI_ENTERO;
    MI_I         PCK_SUBTIPOS.TI_ENTERO :=-1;  
    MI_MSGERROR  PCK_SUBTIPOS.TI_CLAVEVALOR; 
  BEGIN
    --SE DEBEN CREAR LAS 14 CUENTAS EN SALDO_PLAN_PPTAL     
    BEGIN 
      SELECT COUNT(COMPANIA) SALDOS 
      INTO   MI_SALDOS 
      FROM   SALDO_PLAN_PPTAL 
      WHERE  COMPANIA = UN_COMPANIA 
        AND  ANO      = UN_ANIO 
        AND  CODIGO   = UN_CODIGO;
      EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_SALDOS:=0;
    END;
    IF MI_SALDOS = 0 THEN   
      BEGIN
        <<INSERTASALDOS>>
        FOR MI_INDICE IN 0 .. 13 LOOP
          MI_I:=MI_INDICE;
          MI_TABLA := 'SALDO_PLAN_PPTAL';
          MI_CAMPOS :='COMPANIA
                      ,ANO
                      ,CODIGO
                      ,MES';
          MI_VALORES :=''''   || UN_COMPANIA || '''
                         ,'   || UN_ANIO || '
                         ,''' || UN_CODIGO || '''
                         ,'   || MI_I || ''; 
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => MI_TABLA, 
                                                 UN_ACCION  => 'I', 
                                                 UN_CAMPOS  => MI_CAMPOS, 
                                                 UN_VALORES => MI_VALORES);                  
        END LOOP INSERTASALDOS;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
      END;
    END IF;
    EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
      MI_MSGERROR(1).CLAVE := 'MES';
      MI_MSGERROR(1).VALOR := MI_I;
      MI_MSGERROR(2).CLAVE := 'RUBRO';
      MI_MSGERROR(2).CLAVE := UN_CODIGO;
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD    => SQLCODE,
        UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_CREANDO_SALDOPLAN,
        UN_TABLAERROR => 'SALDO_PLAN_PPTAL',
        UN_REEMPLAZOS => MI_MSGERROR
      );
  END PR_CREAR_SALDOPLANPPTAL;

  PROCEDURE PR_ACTUALIZAR_PAC 
    /*
      NAME              : PR_ACTUALIZAR_PAC
      AUTHORS           : SYSMAN LTDA
      AUTHOR MIGRACION  : GERMAN DAVID ROJAS
      DATE CREATION     : 13/01/2023 
      DESCRIPTION       : ACTUALIZA LOS VALORES DEL PAC CUANDO SE ACTUALIZA UN REGISTRO
      TICKET            : 7724476 
      
    */
  
        ( 
          UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA
         ,UN_ANO                 IN PCK_SUBTIPOS.TI_ANIO
         ,UN_TIPO_CPTE           IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL
         ,UN_COMPROBANTE         IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTEPPTAL
         ,UN_CONSECUTIVO         IN PCK_SUBTIPOS.TI_CONSECUTIVOPPTAL
         ,UN_VALORDEBITO         IN NUMBER
         ,UN_VALORCREDITO        IN NUMBER
         ,UN_MES                 IN NUMBER
         ,UN_CODIGO              IN PCK_SUBTIPOS.TI_CODIGOPPTAL
         ,UN_AUXILIAR            IN PCK_SUBTIPOS.TI_AUXILIAR
         ,UN_CENTRO              IN PCK_SUBTIPOS.TI_CENTRO_COSTO
         ,UN_REFERENCIA          IN PCK_SUBTIPOS.TI_REFERENCIA 
         ,UN_FUENTERECURSO       IN PCK_SUBTIPOS.TI_FUENTE_RECURSOS
         ,UN_CLASE               IN COMPROBANTE_PPTAL.TIPO%TYPE
         ,UN_TERCERO             IN COMPROBANTE_PPTAL.TERCERO%TYPE
         ,UN_SUCURSAL            IN COMPROBANTE_PPTAL.SUCURSAL%TYPE 
         ,UN_CONTRACTUAL         IN DETALLE_COMPROBANTE_PPTAL.CONTRACTUAL%TYPE   
         )
         
        AS
  
          MI_CONDICION                  PCK_SUBTIPOS.TI_CONDICION;
          MI_RTA                        PCK_SUBTIPOS.TI_RTA_ACME;
          MI_TABLA                      VARCHAR2(30 CHAR);
          MI_REEMPLAZOS                 PCK_SUBTIPOS.TI_CLAVEVALOR;
          MI_CAMPOS                     PCK_SUBTIPOS.TI_CAMPOS;
          MI_CLASE                      VARCHAR2(30 CHAR);
          MI_DIFERENCIA                 PCK_SUBTIPOS.TI_DOBLE;
          MI_STRCLASEMOV                VARCHAR(3,CHAR);
          MI_RUBRO_ANT                  PCK_PRESUPUESTO1.TYP_RUBRO_AUX;

      BEGIN
        --Se restructura el proceso  ya que se estaba presentado caida del sistema  y es por que  la tabla esta mutando Ticket#7728163 � Caida del sistema sysman web autor:cp
        MI_CLASE := UN_CLASE;      
      
        MI_CONDICION := 'COMPANIA        = ''' || UN_COMPANIA    || ''' 
                         AND ANO         = '   || UN_ANO         || ' 
                         AND TIPO_CPTE   = ''' || UN_TIPO_CPTE   || ''' 
                         AND COMPROBANTE = '   || UN_COMPROBANTE || '
                         AND CONSECUTIVO = '   || UN_CONSECUTIVO;
        -- Actualiza del PAC Programado
        
        IF MI_CLASE = 'DMR' THEN
        
            MI_TABLA     := 'PACPROGRAMADO';
            MI_CAMPOS    := 'MOV_CREDITO = ''' || UN_VALORCREDITO ||''' ';
            MI_RTA       := PCK_DATOS.FC_ACME(
                                               UN_ACCION    => 'M', 
                                               UN_TABLA     => MI_TABLA, 
                                               UN_CAMPOS    => MI_CAMPOS, 
                                               UN_CONDICION => MI_CONDICION
                                              ); 
        ELSIF MI_CLASE IN ('RES','ADR') THEN
            
            MI_TABLA    := 'PACPROGRAMADO';
            MI_CAMPOS   := 'MOV_DEBITO = ''' || UN_VALORDEBITO ||''' ';
            MI_RTA      := PCK_DATOS.FC_ACME(
                                              UN_ACCION     => 'M', 
                                              UN_TABLA      => MI_TABLA, 
                                              UN_CAMPOS     => MI_CAMPOS, 
                                              UN_CONDICION  => MI_CONDICION
                                            );
        -- Actualiza del PAC Comprometido    
        ELSIF MI_CLASE IN ('DRO') THEN
            
            MI_TABLA     := 'PACCOMPROMETIDO';
            MI_CAMPOS    := 'MOV_CREDITO = ''' || UN_VALORCREDITO ||''' ';
            MI_RTA       := PCK_DATOS.FC_ACME(
                                                UN_ACCION    => 'M', 
                                                UN_TABLA     => MI_TABLA, 
                                                UN_CAMPOS    => MI_CAMPOS, 
                                                UN_CONDICION => MI_CONDICION
                                              );
            
        ELSIF MI_CLASE IN ('REO','ARO') THEN
            
            MI_TABLA    := 'PACCOMPROMETIDO';
            MI_CAMPOS   := 'MOV_DEBITO = ''' || UN_VALORDEBITO ||''' ';
            MI_RTA      := PCK_DATOS.FC_ACME(
                                              UN_ACCION    => 'M', 
                                              UN_TABLA     => MI_TABLA, 
                                              UN_CAMPOS    => MI_CAMPOS, 
                                              UN_CONDICION => MI_CONDICION
                                            );
        END IF;

        --Inicia un cuadre de saldos para el rubro actualizando el valor del pac.
         MI_RUBRO_ANT := PCK_PRESUPUESTO1.FC_VERIFICAR_INDICADORES_PPTO(UN_COMPANIA      => UN_COMPANIA,
                                                                        UN_ANIO          => UN_ANO,
                                                                        UN_MES           => UN_MES,
                                                                        UN_CODIGO        => UN_CODIGO,
                                                                        UN_CENTRO        => UN_CENTRO ,
                                                                        UN_TERCERO       => UN_TERCERO,
                                                                        UN_SUCURSAL      => UN_SUCURSAL,
                                                                        UN_AUXILIAR      => UN_AUXILIAR ,
                                                                        UN_REFERENCIA    => UN_REFERENCIA,
                                                                        UN_FUENTERECURSO => UN_FUENTERECURSO
                                                                       ); 
        MI_CAMPOS := 'PAC_PROGRAMADO    = 0';
        MI_CONDICION := 'COMPANIA       = ''' || UN_COMPANIA || '''
                     AND ANO            =   ' || UN_ANO      || '
                     AND MES            =   ' || UN_MES      || '
                     AND CODIGO         = ''' || UN_CODIGO   || '''
                     AND CENTRO_COSTO   = ''' || MI_RUBRO_ANT.MI_CENTROCOSTO   || '''
                     AND REFERENCIA     = ''' || MI_RUBRO_ANT.MI_REFERENCIA    || '''
                     AND FUENTE_RECURSO = ''' || MI_RUBRO_ANT.MI_FUENTERECURSO || '''
                     AND AUXILIAR       = ''' || MI_RUBRO_ANT.MI_AUXILIAR      || '''';
                     
                     
         BEGIN
          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'SALDO_AUX_PPTAL',
                                      UN_ACCION    => 'M',
                                      UN_CAMPOS    => MI_CAMPOS,
                                      UN_CONDICION => MI_CONDICION
                                  );

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;
        
        BEGIN
            FOR RSDETALLE 
                IN (
                                SELECT  PACPROGRAMADO.ANO 
                                      , PACPROGRAMADO.TIPO_CPTE
                                      , PACPROGRAMADO.CUENTA
                                      , EXTRACT(MONTH FROM PACPROGRAMADO.FECHA) MES
                                      , SUM(PACPROGRAMADO.MOV_DEBITO) SUMADEMOV_DEBITO
                                      , SUM(PACPROGRAMADO.MOV_CREDITO) SUMADEMOV_CREDITO
                                      , EXTRACT(YEAR FROM PACPROGRAMADO.FECHA) ANOPAC 
                                      , PLAN_PRESUPUESTAL.NATURALEZA
                                FROM  PACPROGRAMADO  INNER JOIN PLAN_PRESUPUESTAL
                                  ON  PACPROGRAMADO.COMPANIA = PLAN_PRESUPUESTAL.COMPANIA
                                  AND PACPROGRAMADO.ANO      = PLAN_PRESUPUESTAL.ANO
                                  AND PACPROGRAMADO.CUENTA   = PLAN_PRESUPUESTAL.CODIGO
                                WHERE PACPROGRAMADO.COMPANIA                      = UN_COMPANIA
                                  AND PACPROGRAMADO.ANO                           = UN_ANO          
                                  AND PACPROGRAMADO.CUENTA                        = UN_CODIGO
                                  AND PACPROGRAMADO.CENTRO_COSTO                  = UN_CENTRO
                                  AND PACPROGRAMADO.AUXILIAR                      = UN_AUXILIAR
                                  AND  EXTRACT (MONTH FROM PACPROGRAMADO.FECHA)   = UN_MES
                                GROUP BY PACPROGRAMADO.ANO 
                                       , PACPROGRAMADO.TIPO_CPTE 
                                       , PACPROGRAMADO.COMPROBANTE
                                       , PACPROGRAMADO.CUENTA
                                       , EXTRACT(MONTH FROM PACPROGRAMADO.FECHA) 
                                       , EXTRACT(YEAR FROM PACPROGRAMADO.FECHA)
                                       , PLAN_PRESUPUESTAL.NATURALEZA
                    ) LOOP
                          IF RSDETALLE.ANOPAC > RSDETALLE.ANO THEN
                              MI_STRCLASEMOV:= 'VIF';
                          ELSE 
                              MI_STRCLASEMOV:= 'PAP';
                          END IF;
                          MI_DIFERENCIA := CASE WHEN RSDETALLE.NATURALEZA = 'D'
                                                THEN RSDETALLE.SUMADEMOV_DEBITO - RSDETALLE.SUMADEMOV_CREDITO
                                                ELSE RSDETALLE.SUMADEMOV_CREDITO - RSDETALLE.SUMADEMOV_DEBITO
                                            END;
                          PCK_PRESUPUESTO1.PR_ACTPPTO0AUX (UN_CLASE         => MI_STRCLASEMOV, 
                                                           UN_COMPANIA      => UN_COMPANIA,
                                                           UN_ANIO          => UN_ANO, 
                                                           UN_CODIGO        => UN_CODIGO, 
                                                           UN_TERCERO       => UN_TERCERO, 
                                                           UN_SUCURSAL      => UN_SUCURSAL,
                                                           UN_AUXILIAR      => UN_AUXILIAR,
                                                           UN_CENTRO        => UN_CENTRO,
                                                           UN_REFERENCIA    => UN_REFERENCIA,
                                                           UN_FUENTERECURSO => UN_FUENTERECURSO,
                                                           UN_MES           => UN_MES,
                                                           UN_DEBITO        => RSDETALLE.SUMADEMOV_DEBITO,
                                                           UN_CREDITO       => RSDETALLE.SUMADEMOV_CREDITO,
                                                           UN_DEBITO_ANT    => 0, 
                                                           UN_CREDITO_ANT   => 0,
                                                           UN_NATURALEZA    => RSDETALLE.NATURALEZA, 
                                                           UN_DIFERENCIA    => MI_DIFERENCIA, 
                                                           UN_DIFERENCIAANT => 0, 
                                                           UN_TIPO          => CASE WHEN UN_CONTRACTUAL <> 0 THEN 'C' ELSE 'N' END, 
                                                           UN_TIPOINGRESO   => NULL
                                                           );
            END LOOP ACTUALIZA_PAC;
        END;              
  END PR_ACTUALIZAR_PAC;

  PROCEDURE PR_ACT_VLR_ACTUAL
    /*
      NAME              : PR_ACT_VLR_ACTUAL
      AUTHORS           : SYSMAN LTDA
      AUTHOR MIGRACION  : JESUS MILLAN 
      DATE CREATION     : 14/11/2024
      DESCRIPTION       : ACTUALIZA EL SALDO "TIEMPO REAL" PARA EL REPORTE 002343DISPODUITAMA
      TICKET            : 7800751
    */
        (
          UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA
         ,UN_ANO                 IN PCK_SUBTIPOS.TI_ANIO
         ,UN_TIPO_CPTE           IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL
         ,UN_COMPROBANTE         IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTEPPTAL
         ,UN_CONSECUTIVO         IN PCK_SUBTIPOS.TI_CONSECUTIVOPPTAL
         )
        AS
          MI_CONDICION                  PCK_SUBTIPOS.TI_CONDICION;
          MI_CAMPOS                     PCK_SUBTIPOS.TI_CAMPOS;
    BEGIN
      BEGIN
        <<ACTUALIZAR>>
        FOR MI_RS
            IN ( SELECT ID, CONSECUTIVO, (VALOR_DEBITO-VALOR_CREDITO) AS DISPONIBILIDAD
                    FROM   DETALLE_COMPROBANTE_PPTAL
                    WHERE  COMPANIA    = UN_COMPANIA
                    AND  ANO         = UN_ANO
                    AND  TIPO_CPTE   = UN_TIPO_CPTE
                    AND  COMPROBANTE = UN_COMPROBANTE
                    AND  CONSECUTIVO = UN_CONSECUTIVO
                 ) LOOP

          -- MOD INI JM 10-04-2025 FT GR, para que en caso de no existir el rubro 
          MI_CAMPOS := 'F_HORA_SALDO_ACTUAL = TO_DATE(SYSDATE, ''DD/MM/YYYY HH24:MI:SS''), 
                        SALDO_ACTUAL = ( SELECT (NVL(SUM(NVL(APROPIACION_DEBITO,0)),0) + NVL(SUM(NVL(ADICION,0)),0) + NVL(SUM(NVL(REDUCCION,0)),0) + 
                        (NVL(SUM(NVL(TRASLADO_DEBITO,0)),0) - NVL(SUM(NVL(TRASLADO_CREDITO,0)),0)) - (NVL(SUM(NVL(APLAZAM_CREDITO,0)),0) - 
                        NVL(SUM(NVL(APLAZAM_DEBITO,0)),0))) - NVL(SUM(NVL(DISPONIBILIDAD,0)),0) + NVL(' || MI_RS.DISPONIBILIDAD || ', 0)
                            FROM SALDO_AUX_PPTAL
                            WHERE COMPANIA = ''' || UN_COMPANIA || ''' 
                            AND ANO = ' || UN_ANO || ' 
                            AND ID = ''' || MI_RS.ID || ''')';
          -- MOD FIN JM 10-04-2025 FT GR, para que en caso de no existir el rubro 

          MI_CONDICION:='DETALLE_COMPROBANTE_PPTAL.COMPANIA        =''' || UN_COMPANIA || '''
                         AND DETALLE_COMPROBANTE_PPTAL.ANO         ='   || UN_ANO      || '
                         AND DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE   =''' || UN_TIPO_CPTE || '''
                         AND DETALLE_COMPROBANTE_PPTAL.ID          =''' || MI_RS.ID || '''
                         AND DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO =''' || MI_RS.CONSECUTIVO || '''
                         AND DETALLE_COMPROBANTE_PPTAL.COMPROBANTE ='   || UN_COMPROBANTE || '';
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'DETALLE_COMPROBANTE_PPTAL',
                                                 UN_ACCION     => 'M',
                                                 UN_CAMPOS     => MI_CAMPOS,
                                                 UN_CONDICION  => MI_CONDICION);
      END LOOP ACTUALIZAR;
      
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
      END;
  END PR_ACT_VLR_ACTUAL;

END PCK_PRESUPUESTO1;