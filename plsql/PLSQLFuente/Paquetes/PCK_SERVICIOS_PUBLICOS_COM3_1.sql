create or replace PACKAGE BODY PCK_SERVICIOS_PUBLICOS_COM3 AS
  
  -- 1
  PROCEDURE PR_ACTUALIZARESTADO
    /*
      NAME              : PR_ACTUALIZAESTADO
      AUTHORS           : STEFANINI SYSMAN
      AUTHOR MIGRACION  : PABLO ANDRES ESPITIA CUCA
      DATE MIGRADOR     : 12/07/2017
      TIME              : 02:52 PM
      DESCRIPTION       : Actualizar el estado asociado al codigo de ruta del usuario.
      PARAMETERS        : UN_COMPANIA            => Codigo de la compania.
                          UN_CICLO               => Codigo del ciclo.
                          UN_CODIGORUTA          => Codigo de ruta del usuario.
                          UN_ESTADONUEVO         => Valor del nuevo estado en la operacion.
                          UN_USUARIO             => Usuario que inicio sesion
                          UN_CODIGOAUDITORIA     => Concatenacion de CONSECUTIVO^UN_COMPANIA^UN_CICLO^UN_CODIGORUTA
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      DESCRIPTION:      :

      @NAME: actualizarEstado
      @METHOD: POST
    */
  (
     UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_CICLO               IN SP_USUARIO.CICLO%TYPE
    ,UN_CODIGORUTA          IN SP_USUARIO.CODIGORUTA%TYPE
    ,UN_ESTADONUEVO         IN SP_USUARIO.ESTADO%TYPE
    ,UN_USUARIO             IN PCK_SUBTIPOS.TI_USUARIO
    ,UN_CODIGOAUDITORIA     IN SP_AUDITORIAMNUMOD.CODIGO%TYPE
  )
  AS 
    MI_ANIO        PCK_SUBTIPOS.TI_ANIO;
    MI_MSGERROR    PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_TABLA_F     PCK_SUBTIPOS.TI_TABLA;
    MI_TABLA_U     PCK_SUBTIPOS.TI_TABLA;
    MI_TABLA_A     PCK_SUBTIPOS.TI_TABLA;
    MI_TABLA_AM    PCK_SUBTIPOS.TI_TABLA;
    MI_TABLA_T     PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS      PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION   PCK_SUBTIPOS.TI_CONDICION;
    MI_VALORES     PCK_SUBTIPOS.TI_VALORES;
    MI_CANT        PCK_SUBTIPOS.TI_ENTERO;
    MI_SUMA        PCK_SUBTIPOS.TI_DOBLE;
    MI_CONSECUTIVO PCK_SUBTIPOS.TI_ENTERO;
    MI_IND_PAGO    PCK_SUBTIPOS.TI_LOGICO;
    MI_USUARIO     SP_USUARIO%ROWTYPE;
    MI_DESC        SP_TBL_HIST_NOVEDADES_USU.DESCRIPCION%TYPE;
  BEGIN
    MI_TABLA_F  := 'SP_FACTURADO';
    MI_TABLA_U  := 'SP_USUARIO';
    MI_TABLA_A  := 'SP_AUDITORIAMNUMOD';
    MI_TABLA_AM := 'SP_AUDITORIAMSG';
    MI_TABLA_T  := 'SP_TBL_HIST_NOVEDADES_USU';

    BEGIN
      BEGIN
        SELECT *
        INTO MI_USUARIO
        FROM SP_USUARIO
        WHERE COMPANIA   = UN_COMPANIA
          AND CICLO      = UN_CICLO
          AND CODIGORUTA = UN_CODIGORUTA;

      EXCEPTION WHEN NO_DATA_FOUND THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      MI_MSGERROR(1).CLAVE := 'CODRUTA';
      MI_MSGERROR(1).VALOR := UN_CODIGORUTA;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_NDF_PRAE_VERIFICACODRUT
                                ,UN_REEMPLAZOS => MI_MSGERROR);
    END;

    IF UN_ESTADONUEVO IN('A') THEN
      BEGIN
        SELECT MAX(ANO)
        INTO MI_ANIO
        FROM SP_FACTURADO
        WHERE COMPANIA   = UN_COMPANIA
          AND CICLO      = UN_CICLO
          AND CODIGORUTA = UN_CODIGORUTA;

        IF MI_ANIO IS NULL THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END IF;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CODRUTA';
        MI_MSGERROR(1).VALOR := UN_CODIGORUTA;

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_NDF_PRAE_VALIDA_MAXANIO
                                  ,UN_REEMPLAZOS => MI_MSGERROR);
      END;

      MI_CAMPOS := 'ANO           =   '||MI_USUARIO.ANO     ||'
                   ,PERIODO       = '''||MI_USUARIO.PERIODO ||'''
                   ,MODIFIED_BY   = '''||UN_USUARIO         ||'''
                   ,DATE_MODIFIED = SYSDATE';

      MI_CONDICION := 'COMPANIA   = '''||UN_COMPANIA  ||'''
                   AND CICLO      =   '||UN_CICLO     ||'
                   AND CODIGORUTA = '''||UN_CODIGORUTA||'''
                   AND ANO        =   '||MI_ANIO      ||'';

      BEGIN
        BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_F
                                               ,UN_ACCION    => 'M'
                                               ,UN_CAMPOS    => MI_CAMPOS
                                               ,UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        MI_MSGERROR(1).CLAVE := 'CODRUTA';
        MI_MSGERROR(1).VALOR := UN_CODIGORUTA;
        MI_MSGERROR(2).CLAVE := 'ANIO';
        MI_MSGERROR(2).VALOR := MI_ANIO;

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_M_PRAE_ACTUALIZAFACTURA
                                  ,UN_REEMPLAZOS => MI_MSGERROR
                                  ,UN_TABLAERROR => MI_TABLA_F);
      END;

      IF MI_USUARIO.BANCOPERPROCESO IS NOT NULL THEN
        SELECT COUNT(1)
        INTO MI_CANT
        FROM SP_PAGO
        WHERE COMPANIA      = UN_COMPANIA
          AND CODIGOINTERNO = MI_USUARIO.CODIGOINTERNO
          AND PERIODO       = MI_USUARIO.PERIODO
          AND ANO           = MI_USUARIO.ANO;

        MI_IND_PAGO := CASE WHEN MI_CANT IN(0) THEN -1 ELSE 0 END;
      END IF;
    END IF;

    IF UN_ESTADONUEVO IN('R','S') THEN
      MI_CAMPOS := 'DATE_MODIFIED     = SYSDATE
                   ,ESTADO            = '''||UN_ESTADONUEVO||'''
                   ,FECHACAMBIOEST    = SYSDATE
                   ,FECHASALIOUSUARIO = SYSDATE
                   ,MODIFIED_BY       = '''||UN_USUARIO    ||'''';

    ELSIF MI_IND_PAGO NOT IN(0) THEN
      MI_CAMPOS := 'BANCOPERPROCESO       = NULL
                   ,DATE_MODIFIED         = SYSDATE
                   ,ESTADO                = '''||UN_ESTADONUEVO||'''
                   ,FECHACAMBIOEST        = SYSDATE
                   ,FECHAPAGOPERPROCESO   = NULL
                   ,MODIFIED_BY           = '''||UN_USUARIO    ||'''
                   ,NOFECHAPAGOPERPROCESO = NULL
                   ,PAQUETEPAGOPERPROCESO = NULL
                   ,RECAUDADOPROCESO      = 0
                   ,TOTFACTURAPERACTUAL   = 0';

    ELSE
      MI_CAMPOS := 'DATE_MODIFIED  = SYSDATE
                   ,ESTADO         = '''||UN_ESTADONUEVO||'''
                   ,FECHACAMBIOEST = SYSDATE
                   ,MODIFIED_BY    = '''||UN_USUARIO    ||'''';

    END IF;

    MI_CONDICION := 'COMPANIA   = '''||UN_COMPANIA  ||'''
                 AND CICLO      =   '||UN_CICLO     ||'
                 AND CODIGORUTA = '''||UN_CODIGORUTA||'''';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_U
                                             ,UN_ACCION    => 'M'
                                             ,UN_CAMPOS    => MI_CAMPOS
                                             ,UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      MI_MSGERROR(1).CLAVE := 'USUARIO';
      MI_MSGERROR(1).VALOR := UN_CODIGORUTA;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_M_PRAE_ACTUALIZARESTADO
                                ,UN_REEMPLAZOS => MI_MSGERROR
                                ,UN_TABLAERROR => MI_TABLA_U);
    END;



    --Auditoria MnuMod
    MI_CONDICION := 'COMPANIA   = '''||UN_COMPANIA  ||''' 
                 AND CICLO      =   '||UN_CICLO     ||'
                 AND CODIGORUTA = '''||UN_CODIGORUTA||'''';

    MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => MI_TABLA_A
                                                      ,UN_CRITERIO => MI_CONDICION
                                                      ,UN_CAMPO    => 'CONSECUTIVO');

    MI_CAMPOS := 'COMPANIA
                 ,CICLO
                 ,CODIGORUTA
                 ,CONSECUTIVO
                 ,PERIODO
                 ,ANO
                 ,CODIGO
                 ,HORA
                 ,CUENTA
                 ,CREATED_BY
                 ,DATE_CREATED
                 ,CAMPO
                 ,FORMULARIO
                 ,VALORINICIAL
                 ,VALORFINAL';

    MI_VALORES := ''''||UN_COMPANIA        ||'''
                  ,  '||UN_CICLO           ||'
                  ,'''||UN_CODIGORUTA      ||'''
                  ,  '||MI_CONSECUTIVO     ||'
                  ,'''||MI_USUARIO.PERIODO ||'''
                  ,  '||MI_USUARIO.ANO     ||'
                  ,'''||UN_CODIGOAUDITORIA ||'''
                  ,TO_DATE(''30/12/1899 ''||'''||TO_CHAR(SYSDATE,'HH24:MI')||''',''DD/MM/YYYY HH24:MI:SS'')
                  ,'''||UN_USUARIO         ||'''
                  ,'''||UN_USUARIO         ||'''
                  ,SYSDATE
                  ,''Estado del usuario''
                  ,''REGISTRO DE OPERACIONES''
                  ,'''||MI_USUARIO.ESTADO  ||'''
                  ,'''||UN_ESTADONUEVO     ||'''';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_A
                                             ,UN_ACCION    => 'I'
                                             ,UN_CAMPOS    => MI_CAMPOS
                                             ,UN_VALORES   => MI_VALORES);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      MI_MSGERROR(1).CLAVE := 'USUARIO';
      MI_MSGERROR(1).VALOR := UN_CODIGORUTA;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_I_PRAE_REGICAMBIOESTADO
                                ,UN_REEMPLAZOS => MI_MSGERROR
                                ,UN_TABLAERROR => MI_TABLA_A);
    END;  

    --Auditoria Msg
    MI_CONDICION := 'COMPANIA   = '''||UN_COMPANIA  ||'''';

    MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => MI_TABLA_AM
                                                      ,UN_CRITERIO => MI_CONDICION
                                                      ,UN_CAMPO    => 'CONSECUTIVO');

    MI_CAMPOS := 'COMPANIA
                 ,CONSECUTIVO
                 ,CUENTA
                 ,HORA
                 ,DESCLARGA
                 ,CREATED_BY
                 ,DATE_CREATED';

    MI_VALORES := ''''||UN_COMPANIA   ||'''
                  ,  '||MI_CONSECUTIVO||'
                  ,'''||UN_USUARIO    ||'''
                  ,TO_DATE(''30/12/1899 ''||'''||TO_CHAR(SYSDATE,'HH24:MI')||''',''DD/MM/YYYY HH24:MI:SS'')
                  ,''Modificación en REGISTRO DE OPERACIONES. Para el registro: Estado del usuario''
                  ,'''||UN_USUARIO    ||'''
                  ,SYSDATE';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_AM
                                             ,UN_ACCION    => 'I'
                                             ,UN_CAMPOS    => MI_CAMPOS
                                             ,UN_VALORES   => MI_VALORES);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      MI_MSGERROR(1).CLAVE := 'USUARIO';
      MI_MSGERROR(1).VALOR := UN_CODIGORUTA;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_I_PRAE_REGICAMBIOESTADO
                                ,UN_REEMPLAZOS => MI_MSGERROR
                                ,UN_TABLAERROR => MI_TABLA_AM);
    END;    

    --Auditoria HistNovedadesUsu
    SELECT SUM(SALDOFINANCIABLE) 
    INTO MI_SUMA
    FROM SP_FINANCIABLES 
    WHERE COMPANIA   = UN_COMPANIA
      AND CICLO      = UN_CICLO
      AND CODIGORUTA = UN_CODIGORUTA
      AND ANO        = MI_USUARIO.ANO
      AND PERIODO    = MI_USUARIO.PERIODO
      AND SALDOFINANCIABLE NOT IN(0);

    MI_DESC := '. Deuda: '          ||MI_USUARIO.DEUDA              ||
               '. Total Facturado: '||MI_USUARIO.TOTFACTURAPERACTUAL||
               CASE WHEN MI_USUARIO.BANCOPERPROCESO IS NULL
                    THEN '. Valor en Mora. Saldo Financiables: '||NVL(MI_SUMA,0)
                    ELSE '. Ultima Factura pagada'
               END;

    MI_CONDICION := 'COMPANIA   = '''||UN_COMPANIA||''' 
                 AND CODIGORUTA = '''||UN_CODIGORUTA||'''';

    MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => MI_TABLA_T
                                                      ,UN_CRITERIO => MI_CONDICION
                                                      ,UN_CAMPO    => 'IDNOVEDAD');

    MI_CAMPOS := 'COMPANIA
                 ,CODIGORUTA
                 ,IDNOVEDAD
                 ,CICLO
                 ,ESTADO
                 ,DESCRIPCION
                 ,CREATED_BY
                 ,DATE_CREATED
                 ,FECHA';

    MI_VALORES := ''''||UN_COMPANIA                          ||'''
                  ,'''||UN_CODIGORUTA                        ||'''
                  ,'''||MI_CONSECUTIVO                       ||'''
                  ,  '||UN_CICLO                             ||'
                  ,'''||MI_USUARIO.ESTADO                    ||'''
                  ,'''||'Cambiado por: '||UN_USUARIO||MI_DESC||'''
                  ,'''||UN_USUARIO                           ||'''
                  ,SYSDATE
                  ,SYSDATE';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_T
                                             ,UN_ACCION    => 'I'
                                             ,UN_CAMPOS    => MI_CAMPOS
                                             ,UN_VALORES   => MI_VALORES);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      MI_MSGERROR(1).CLAVE := 'USUARIO';
      MI_MSGERROR(1).VALOR := UN_CODIGORUTA;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_I_PRAE_REGICAMBIOESTADO
                                ,UN_REEMPLAZOS => MI_MSGERROR
                                ,UN_TABLAERROR => MI_TABLA_AM);
    END;   

    MI_DESC := 'Estado Inicial: '          ||MI_USUARIO.ESTADO||
               '; Estado Final: '          ||UN_ESTADONUEVO   ||
               '; Consecutivo de Novedad: '||MI_CONSECUTIVO   ||
               '; Operación: '             ||MI_DESC;

    PCK_DATOS.GL_RTA := PCK_SERVICIOS_PUBLICOS_COM3.FC_AUDITORIAGENERAL(UN_COMPANIA     => UN_COMPANIA		   
                                                                       ,UN_USUARIO 		  => UN_USUARIO   
                                                                       ,UN_MACROPROCESO => 'REGISTRO DE OPERACIONES'
                                                                       ,UN_SUBPROCESO 	=> 'Modificacion'
                                                                       ,UN_ANIO 			  => MI_USUARIO.ANO
                                                                       ,UN_PERIODO 		  => MI_USUARIO.PERIODO
                                                                       ,UN_CODINTERNO   => MI_USUARIO.CODIGOINTERNO 
                                                                       ,UN_DESCRIPCION  => MI_DESC);                       
  END PR_ACTUALIZARESTADO;

  -- 2
  FUNCTION FC_AUDITORIAGENERAL
  /*
        NAME              : FC_AUDITORIAGENERAL -- AuditoriaGeneral en ModAuditoria en Access
        AUTHORS           : SYSMAN LTDA
        AUTHOR MIGRACION  : LEYDI MILENA CORTÉS FORERO
        DATE MIGRADOR     : 06/09/2016
        TIME              : 05:27 PM
        MODIFIER          : AURA LILIANA MONROY GARCÍA
        DATE MODIFIED     : 14/09/2016
        TIME              : 03:07 PM
        DESCRIPTION       : 
        PARAMETERS        : UN_COMPANIA             => Compañia de ingreso a la aplicación
                            UN_USUARIO              => Nombre del usuario que realiza el subproceso de la factura
                            UN_MACROPROCESO         => Describe la acción principal que se quiere registrar en la auditoría
                            UN_SUBPROCESO           => Define la creación, edición o eliminación del macroproceso
                            UN_ANIO                 => Año en que se realiza el proceso que se desea registrar en la auditoría
                            UN_PERIODO              => Mes en que se realiza el proceso que se registrará en la auditoría
                            UN_CODINTERNO           => Código interno asignado al usuario 
                            UN_DESCRIPCION          => Detalles del proceso que se está registando en la auditoría     
        @NAME:  auditoriaGeneral
        @METHOD:  GET
    */
    (
      UN_COMPANIA  		   IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_USUARIO 		     IN PCK_SUBTIPOS.TI_USUARIO,
      UN_MACROPROCESO    IN SP_AUDIGEN.MACROPROCESO%TYPE,
      UN_SUBPROCESO 		 IN SP_AUDIGEN.SUBPROCESO%TYPE,	
      UN_ANIO 			     IN PCK_SUBTIPOS.TI_ANIO, 
      UN_PERIODO 		     IN PCK_SUBTIPOS.TI_PERIODO,
      UN_CODINTERNO      IN SP_AUDIGEN.CODINTERNO%TYPE,
      UN_DESCRIPCION 	   IN SP_AUDIGEN.RESUMEN%TYPE
    )
    RETURN VARCHAR2
    AS
      MI_ERROR_FUN       PCK_SUBTIPOS.TI_ERROR_FUN := GL_ERROR_NUM + 2;
      MI_CAMPOS          PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES         PCK_SUBTIPOS.TI_VALORES;
      MI_PCKDATOS        PCK_SUBTIPOS.TI_RTA_ACME; 
      MI_MSGERROR        PCK_SUBTIPOS.TI_CLAVEVALOR;
    BEGIN
      MI_CAMPOS   := 'COMPANIA, 
                       USUARIO, 
                       MACROPROCESO, 
                       SUBPROCESO, 
                       FECHA, 
                       HORA, 
                       ANO, 
                       PERIODO, 
                       CODINTERNO, 
                       RESUMEN, 
                       CREATED_BY, 
                       DATE_CREATED';
      MI_VALORES  := '''' || UN_COMPANIA || ''', 
                      ''' || UN_USUARIO || ''', 
                      ''' || UN_MACROPROCESO || ''', 
                      ''' || UN_SUBPROCESO || ''', 
                      TO_DATE(''' || SYSDATE || ''',''DD/MM/YYYY HH24:mi:ss''), 
                      TO_DATE(''' || SYSDATE || ''',''DD/MM/YYYY HH24:mi:ss''), 
                        ' || UN_ANIO || ', 
                      ''' || UN_PERIODO || ''', 
                      ''' || UN_CODINTERNO || ''', 
                      ''' || UN_DESCRIPCION||''', 
                      ''' || UN_USUARIO ||''', 
                      SYSDATE';

      BEGIN
        MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA   => 'SP_AUDIGEN', 
                                         UN_ACCION  => 'I', 
                                         UN_CAMPOS  => MI_CAMPOS, 
                                         UN_VALORES => MI_VALORES);
        RETURN MI_PCKDATOS;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
     END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      RETURN '0';
  END FC_AUDITORIAGENERAL;

  -- 3  
  FUNCTION FC_TIENECONCEPTOFACTURADO
  (
    /*
        NAME              : FC_TIENECONCEPTOFACTURADO -- En Access TieneConceptoFacturado
        AUTHORS           : SYSMAN LTDA
        AUTHOR MIGRACION  : ADRIANA MARITZA CÁCERES BONILLA
        DATE MIGRADOR     : 15/09/2016
        TIME              : 09:01 AM
        MODIFIER          : 
        DATE MODIFIED     : 
        TIME              : 
        DESCRIPTION       : Función para consultar si el concepto fue facturado en el periodo actual. 
                          : Retorna 0 para false y 1 cuando el valor es verdadero. 
        PARAMETERS        : UN_COMPANIA             => Compañia de ingreso a la aplicación
                            UN_CODIGORUTA           => Código de ruta asignado a un usuario
                            UN_CICLO                => Ciclo al que pertenece el usuario que se desea consultar
                            UN_CONCEPTO             => Número de concepto del que se desea conocer si ha sido facturado
                            UN_ANO                  => Año de facturación que se desea consultar
                            UN_PERIODO              => Periodo de facturación que se desea consultar
        @NAME:  validarConceptoFacturado
        @METHOD:  GET
    */
    UN_COMPANIA          IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CODIGORUTA        IN PCK_SUBTIPOS.TI_CODIGORUTA,
    UN_CICLO             IN PCK_SUBTIPOS.TI_CICLO,
    UN_CONCEPTO          IN SP_FINANCIABLES.CONCEPTO%TYPE,
    UN_ANO               IN PCK_SUBTIPOS.TI_ANIO, 
    UN_PERIODO           IN PCK_SUBTIPOS.TI_PERIODO
  )
  RETURN NUMBER
  AS
    MI_ERROR_FUN         PCK_SUBTIPOS.TI_ERROR_FUN := GL_ERROR_NUM + 3;
    MI_STRSQL            PCK_SUBTIPOS.TI_STRSQL;
    MI_CUENTA            VARCHAR2(1 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
  BEGIN
    MI_STRSQL:='SELECT 
                       DISTINCT ''X''
                FROM   
                       SP_FACTURADO 
                WHERE 
                       COMPANIA ='''||UN_COMPANIA||''' 
                  AND  CICLO ='''||UN_CICLO||'''  
                  AND  CODIGORUTA ='''||UN_CODIGORUTA||''' 
                  AND  ANO = '||UN_ANO||'
                  AND  PERIODO ='''||UN_PERIODO ||''' 
                  AND  CONCEPTO = '||UN_CONCEPTO||''; 

    BEGIN  
      EXECUTE IMMEDIATE MI_STRSQL INTO MI_CUENTA; 
      EXCEPTION WHEN NO_DATA_FOUND THEN 
        RETURN 0; 
    END; 
      RETURN 1; 
  END FC_TIENECONCEPTOFACTURADO;    

  -- 4
  PROCEDURE PR_AUDITARMODIF
  /*
    NAME              : PR_AUDITARMODIF -- AuditarModif en ModAuditoria en Access
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : LEYDI MILENA CORTÉS FORERO
    DATE MIGRADOR     : 21/10/2016
    TIME              : 12:19 PM
    DESCRIPTION       : PROCEDIMIENTO QUE PERMITE INSERTAR EN LA TABLA SP_AUDITORIAMNU LAS MODIFICACIONES 
                        REALIZADAS EN LOS FORMULARIOS DEL MÓDULO SERVICIOS PÚBLICOS.
    PARAMETERS        : UN_COMPANIA   => Compania en la que se está trabajando
                        UN_FORMORIGEN => Nombre del formulario en el cual se realizó una modificación.
                        UN_INTTIPO    => Identifica el tipo de modificación que se está realizando
                                          1: Inserción.
                                          2: Eliminación.
                        UN_CAMPO      => Campo compuesto de la concatenación del Consecutivo, Compañía, Ciclo y Código de
                                         Ruta del registro modificado.
                        UN_USUARIO    => Usuario que realiza la modificación.

    MODIFIED BY       : PABLO ANDRES ESPITIA CUCA
    DATE MODIFIED     : 10/07/2017
    TIME MODIFIED     : 09:42 AM
    DESCRIPTION:      : Ajustes segun el estandar de programacion. 
                        Adicion de los campos de auditoria.
                        Manejo de excepciones.

    @NAME: auditarModif
    @METHOD: POST
  */
  (
    UN_COMPANIA  			IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_FORMORIGEN 		IN SP_AUDITORIAMNU.FORMULARIO%TYPE,
    UN_INTTIPO				IN PCK_SUBTIPOS.TI_ENTERO,
    UN_CAMPO  				IN SP_AUDITORIAMNU.CODIGO%TYPE,
    UN_USUARIO 			  IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
    MI_TABLA_AM       PCK_SUBTIPOS.TI_TABLA;
    MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_FORMORIGEN     SP_AUDITORIAMNU.FORMULARIO%TYPE;
    MI_TIPOTRANS		  SP_AUDITORIAMNU.TIPOTRANSACCION%TYPE;
    MI_CONSECUTIVO    SP_AUDITORIAMNU.CONSECUTIVO%TYPE;
  BEGIN 
    MI_TABLA_AM := 'SP_AUDITORIAMNU';

    -- Define el valor del campo FORMULARIO  
    MI_FORMORIGEN := CASE UN_FORMORIGEN WHEN 'FRM_CORRECCIONCRITICA_PROB' 
                                        THEN 'PROBLEMA AFORO'
                                        WHEN 'FRM_CORRECCIONCRITICA_LIS' 
                                        THEN 'CRITICA'
                                        ELSE UN_FORMORIGEN
                     END;

    -- Define el Tipo de Transacción de acuerdo al valor del parámetro UN_INTTIPO
    MI_TIPOTRANS := CASE WHEN UN_INTTIPO IN(1)
                         THEN 'Creacion'
                         ELSE 'Eliminacion'
                    END;

    MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => MI_TABLA_AM
                                                      ,UN_CRITERIO => 'COMPANIA = '''||UN_COMPANIA||''''
                                                      ,UN_CAMPO    => 'CONSECUTIVO');

    MI_CAMPOS := 'COMPANIA
                 ,CONSECUTIVO
                 ,CUENTA
                 ,HORA
                 ,FORMULARIO
                 ,TIPOTRANSACCION
                 ,CODIGO
                 ,CREATED_BY
                 ,DATE_CREATED';

    MI_VALORES := ''''||UN_COMPANIA   ||'''
                  ,  '||MI_CONSECUTIVO||'
                  ,'''||UN_USUARIO    ||'''
                  ,SYSDATE
                  ,'''||MI_FORMORIGEN ||'''
                  ,'''||MI_TIPOTRANS  ||'''
                  ,'''||UN_CAMPO      ||'''
                  ,'''||UN_USUARIO    ||'''
                  ,SYSDATE'; 

    BEGIN  
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA_AM
                                           ,UN_ACCION  => 'I'
                                           ,UN_CAMPOS  => MI_CAMPOS
                                           ,UN_VALORES => MI_VALORES);

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
      RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
    END;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
    MI_MSGERROR(1).CLAVE := 'CRUD';
    MI_MSGERROR(1).VALOR := MI_TIPOTRANS;
    MI_MSGERROR(2).CLAVE := 'CODE';
    MI_MSGERROR(2).VALOR := MI_CONSECUTIVO;

    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                              ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_I_PRAM_AUDITARPROCESO
                              ,UN_TABLAERROR => MI_TABLA_AM
                              ,UN_REEMPLAZOS => MI_MSGERROR);
  END PR_AUDITARMODIF;

  -- 5
  PROCEDURE PR_AUDITARREGCOMPARAR
  /*
    NAME              : AUDITARREGCOMPARAR -- AudRegistroComparar en ModAuditoria en Access
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : LEYDI MILENA CORTÉS FORERO
    DATE MIGRADOR     : 27/10/2016
    TIME              : 02:19 PM
    DESCRIPTION       : PROCEDIMIENTO QUE PERMITE INSERTAR EN LA TABLA SP_AUDITORIAMNUMOD Y SP_AUDITORIAMSG LAS MODIFICACIONES 
                        REALIZADAS A LOS CAMPOS EN LOS FORMULARIOS DEL MÓDULO SERVICIOS PÚBLICOS.
    PARAMETERS        : UN_COMPANIA     => Compania en la que se está trabajando
                        UN_FORMORIGEN   => Nombre del formulario en el cual se realizó una modificación.
                        UN_STRCAMPO     => Campo compuesto de la concatenación del Consecutivo, Compañía, Ciclo y Código de
                                           Ruta del registro modificado.
                        UN_CAMPOSMODIF  => Campo compuesto de la concatenación de los campos y sus respectivos valores anterior y final
                                           concatenados con punto y coma (;), el nombre del campo modificado, el valor inicial y el 
                                           valor final deben venir separados por coma(,). 
                                           Por ejemplo, si se modifican los campos periodo y estado_ope la cadena debe estar de la siguiente
                                           manera:
                                           'PERIODO,VALORINICIALPERIODO,VALORFINALPERIODO;ESTADO_OPE,VALORINICIALESTADO_OPE,VALORFINALESTADO_OPE;'.
                        UN_USUARIO      => Usuario que realiza la modificación.
                        UN_CICLO        => Ciclo al que pertenece la operación que se está modificando.   
                        UN_CODIGORUTA   => Código de Ruta al cual pertenece la operación que se está modificando. 
                        UN_ANIO         => Año en el que está habilitado el código de ruta.  
                        UN_PERIODO			=> Periodo en el que está habilitado el código de ruta.  
                        UN_CONT         => Cantidad de veces que se deberá recorrer la cadena UN_CAMPOSMODIF para registrar el campo, valor anterior
                                           y el nuevo valor de los campos modificados en la tabla SP_AUDITORIAMNUMOD.

    MODIFIED BY       : PABLO ANDRES ESPITIA CUCA
    DATE MODIFIED     : 15/07/2017
    TIME              : 08:31 AM
    DESCRIPTION       : Adicion de campos de auditoria. 
                        Formateado de procedimiento y ajustes de programacion PL.

    @NAME:   auditarRegistroComparar
    @METHOD: POST
  */
  (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_FORMORIGEN     IN SP_AUDITORIAMNU.FORMULARIO%TYPE,
    UN_STRCAMPO       IN SP_AUDITORIAMNU.CODIGO%TYPE,
    UN_CAMPOSMODIF    IN CLOB, 
    UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO,
    UN_CICLO          IN PCK_SUBTIPOS.TI_CICLO,
    UN_CODIGORUTA     IN PCK_SUBTIPOS.TI_CODIGORUTA,
    UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO, 
    UN_PERIODO			  IN PCK_SUBTIPOS.TI_PERIODO,
    UN_CONT           IN PCK_SUBTIPOS.TI_ENTERO
  ) 
  AS
    MI_ERROR_FUN      PCK_SUBTIPOS.TI_ERROR_FUN := GL_ERROR_NUM + 5;
    MI_CAMPO          VARCHAR2(100 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_CAMPOSI        PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
    MI_VALORINI       VARCHAR2(500 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_VALORFIN       VARCHAR2(500 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_VALORINIINS    VARCHAR2(500 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_VALORFININS    VARCHAR2(500 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_FORMORIGEN     SP_AUDITORIAMNU.FORMULARIO%TYPE;
    MI_STRMENSAJE		  VARCHAR2(2000 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_STRDATOS		    VARCHAR2(500 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    MI_PCKDATOS       PCK_SUBTIPOS.TI_RTA_ACME; 
    MI_I              PCK_SUBTIPOS.TI_ENTERO := 0;
    MI_T_SPLIT		    PCK_SYSMAN_UTL.T_SPLIT;
    MI_T_SPLITV		    PCK_SYSMAN_UTL.T_SPLIT;
    MI_PARAUDITAR     PCK_SUBTIPOS.TI_PARAMETRO; /*Permite almacenar el valor del parametro: AUDITAR MODIFICACIONES A CAMPOS*/
    MI_CONSECUTIVO    PCK_SUBTIPOS.TI_ENTERO;
    MI_TABLA_A        PCK_SUBTIPOS.TI_TABLA;
    MI_TABLA_AM       PCK_SUBTIPOS.TI_TABLA;
    MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    MI_TABLA_A  := 'SP_AUDITORIAMNUMOD';
    MI_TABLA_AM := 'SP_AUDITORIAMSG';

    MI_PARAUDITAR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                              ,UN_NOMBRE     => 'AUDITAR MODIFICACIONES A CAMPOS'
                                              ,UN_MODULO     => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS
                                              ,UN_FECHA_PAR  => SYSDATE)
                        ,'NO');

    -- Define el valor del campo FORMULARIO  		
    MI_FORMORIGEN := CASE UN_FORMORIGEN WHEN 'FRM_CORRECCIONCRITICA_PROB'
                                        THEN 'PROBLEMA AFORO'
                                        WHEN 'FRM_CORRECCIONCRITICA_LIS'
                                        THEN 'CRITICA'
                                        WHEN 'FRM_FRAUDES_CARTA'
                                        THEN 'FRAUDES CARTA'
                                        ELSE UN_FORMORIGEN
                     END;

    MI_T_SPLIT := PCK_SYSMAN_UTL.FC_SPLIT_SYS (UN_LISTA        => ''||UN_CAMPOSMODIF||''
                                              ,UN_DELIMITADOR  =>  ';');

    IF MI_PARAUDITAR IN('SI') THEN
      MI_STRMENSAJE := 'Se cambiaron los datos : ';
      MI_STRDATOS   := 'Modificación en ' || MI_FORMORIGEN || CHR(10) || ' Para el registro: ' || UN_STRCAMPO || '.' || CHR(10);

      <<AUDITORIAMNUMOD>>  
      FOR MI_I IN 1..UN_CONT 
      LOOP
        MI_T_SPLITV := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA       => ''||TO_CHAR(MI_T_SPLIT(MI_I))||''
                                                  ,UN_DELIMITADOR =>  ',');

        MI_CAMPO      := TO_CHAR(MI_T_SPLITV(1));
        MI_VALORINI   := NVL(TO_CHAR(MI_T_SPLITV(2)), 'nulo');
        MI_VALORFIN   := TO_CHAR(MI_T_SPLITV(3));

        IF MI_VALORINI = 'nulo' THEN
          MI_VALORINIINS := NULL;
          MI_VALORFININS := MI_VALORFIN;
        ELSIF MI_VALORFIN = 'nulo' THEN
          MI_VALORFININS := NULL;
          MI_VALORINIINS := MI_VALORINI;
        ELSE
          MI_VALORINIINS := MI_VALORINI;
          MI_VALORFININS := MI_VALORFIN;
        END IF;

        MI_STRMENSAJE := MI_STRMENSAJE || MI_CAMPO || ' ( de ' || MI_VALORINI || ' a ' || MI_VALORFIN || ') ' ||  CHR(10); 


        MI_CAMPOSI := 'COMPANIA
                      ,CONSECUTIVO
                      ,CICLO
                      ,CODIGO
                      ,CODIGORUTA
                      ,FORMULARIO
                      ,CAMPO
                      ,CUENTA
                      ,CREATED_BY
                      ,DATE_CREATED
                      ,HORA
                      ,VALORINICIAL
                      ,VALORFINAL
                      ,ANO
                      ,PERIODO';  

        MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => MI_TABLA_A
                                                          ,UN_CRITERIO => 'COMPANIA = ''' || UN_COMPANIA || ''''
                                                          ,UN_CAMPO    => 'CONSECUTIVO'
                                                          ,UN_INICIAL  => '1');

        MI_VALORES := '''' || UN_COMPANIA    || '''
                      ,  ' || MI_CONSECUTIVO || '
                      ,  ' || UN_CICLO       || '
                      ,''' || UN_STRCAMPO    || '''
                      ,''' || UN_CODIGORUTA  || '''
                      ,''' || UN_FORMORIGEN  || '''
                      ,''' || MI_CAMPO       || '''
                      ,''' || UN_USUARIO     || '''
                      ,''' || UN_USUARIO     || '''
                      ,SYSDATE
                      ,TO_DATE(''30/12/1899 ''||'''||TO_CHAR(SYSDATE,'HH24:MI')||''',''DD/MM/YYYY HH24:MI:SS'')
                      ,''' || MI_VALORINIINS || '''
                      ,''' || MI_VALORFININS || '''
                      ,  ' || UN_ANIO        || '
                      ,''' || UN_PERIODO     || '''';

        BEGIN
          BEGIN
            MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA_A
                                             ,UN_ACCION   => 'I'
                                             ,UN_CAMPOS   => MI_CAMPOSI
                                             ,UN_VALORES  => MI_VALORES);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
         MI_MSGERROR(1).CLAVE := 'USUARIO';
         MI_MSGERROR(1).VALOR := UN_CODIGORUTA;
         MI_MSGERROR(2).CLAVE := 'TIPO';
         MI_MSGERROR(2).VALOR := 'mnumod';

         PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                   ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_I_PRARC_IAUDITORIAMNUMD
                                   ,UN_TABLAERROR => MI_TABLA_A
                                   ,UN_REEMPLAZOS => MI_MSGERROR);
        END;
      END LOOP AUDITORIAMNUMOD;

      IF MI_STRMENSAJE NOT IN('Se cambiaron los datos : ') THEN
        MI_CAMPOSI := 'COMPANIA
                      ,CONSECUTIVO
                      ,CUENTA
                      ,CREATED_BY
                      ,DATE_CREATED
                      ,HORA
                      ,DESCLARGA';

        MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => MI_TABLA_AM
                                                          ,UN_CRITERIO => 'COMPANIA = ''' || UN_COMPANIA || ''''
                                                          ,UN_CAMPO    => 'CONSECUTIVO'
                                                          ,UN_INICIAL  => '1');

        MI_VALORES := '''' || UN_COMPANIA                 || '''
                      ,  ' || MI_CONSECUTIVO              || '
                      ,''' || UN_USUARIO                  || '''
                      ,''' || UN_USUARIO                  || '''
                      ,SYSDATE
                      ,TO_DATE(''30/12/1899 ''||'''||TO_CHAR(SYSDATE,'HH24:MI')||''',''DD/MM/YYYY HH24:MI:SS'')
                      ,''' || MI_STRDATOS || MI_STRMENSAJE|| '''';

        BEGIN
          BEGIN
            MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA_AM
                                             ,UN_ACCION   => 'I'
                                             ,UN_CAMPOS   => MI_CAMPOSI
                                             ,UN_VALORES  => MI_VALORES);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
          END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR(1).CLAVE := 'USUARIO';
          MI_MSGERROR(1).VALOR := UN_CODIGORUTA;
          MI_MSGERROR(2).CLAVE := 'TIPO';
          MI_MSGERROR(2).VALOR := 'msg';

          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_SP_I_PRARC_IAUDITORIAMNUMD
                                    ,UN_TABLAERROR => MI_TABLA_AM
                                    ,UN_REEMPLAZOS => MI_MSGERROR);
        END;
      END IF;
    END IF;
  END PR_AUDITARREGCOMPARAR;

  -- 6
  PROCEDURE PR_PREPARAEXCLUIRCARTERA
      /*
        NAME              : PR_PREPARAEXCLUIRCARTERA
        AUTHORS           : SYSMAN  
        AUTHOR MIGRACION  : AURA LILIANA MONROY GARCIA
        DATE MIGRADOR     : 11/11/2016
        TIME              : 12:36 PM
        SOURCE MODULE     : SysmanSp2016.05.04
        MODIFIER          : AURA LILIANA MONROY GARCIA
        DATE MODIFIED     : 16/06/2017 
        TIME              :    
        DESCRIPTION       : Actualiza los usuarios que poseen PQR's abiertas a excluidos a cartera
        MODIFICATIONS     : Se adicionan los campos de auditoría para las actualizaciones que se realizan en el procedimiento 
        PARAMETERS        : UN_COMPANIA      => Compania de ingreso a la aplicacion
                            UN_CICLOINICIAL  => Ciclo desde el cual se va a realizar la actualización del indicador de cartera
                            UN_CICLOFINAL    => Ciclo final para actualizar a cartera
                            UN_USUARIO       => Usuario que realiza el proceso 
        @NAME:  preparaExcluirCartera
        @METHOD:  POST     
      */ 
    (
      UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA, 
      UN_CICLOINICIAL     IN PCK_SUBTIPOS.TI_CICLO,
      UN_CICLOFINAL       IN PCK_SUBTIPOS.TI_CICLO,
      UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
    )
    AS 

      MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;  
      MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
      MI_RTA              PCK_SUBTIPOS.TI_RTA_ACME;
      MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
      MI_MERGEUSING       PCK_SUBTIPOS.TI_MERGEUSING;  
      MI_MERGEENLACE      PCK_SUBTIPOS.TI_MERGEENLACE;
      MI_MERGEEXISTE      PCK_SUBTIPOS.TI_MERGEEXISTE;
      MI_PARFECHACORTE    VARCHAR2(10 CHAR); -- VARCHAR2 DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS

    BEGIN
      BEGIN
        -- Limpia el campo excluir a cartera en la tabla SP_USUARIO
        MI_TABLA      := 'SP_USUARIO';
        MI_CAMPOS     := 'EXCLUIRCARTERA = 0,'||
                         'MODIFIED_BY    = '''|| UN_USUARIO || ''', '||
                         'DATE_MODIFIED  = SYSDATE';
        MI_CONDICION  := 'COMPANIA =''' ||UN_COMPANIA|| '''' ||
                         ' AND CICLO BETWEEN '|| UN_CICLOINICIAL||' AND '|| UN_CICLOFINAL || 
                         ' AND EXCLUIRCARTERA NOT IN(0)';
        MI_RTA        := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                            UN_ACCION    => 'M', 
                                            UN_CAMPOS    => MI_CAMPOS, 
                                            UN_CONDICION => MI_CONDICION);

        -- Se verifica que el parámetro "FECHA CORTE DE PQR PARA SUI" posea una fecha definida
        MI_PARFECHACORTE   := PCK_SYSMAN_UTL.FC_PAR (UN_COMPANIA  => '' || UN_COMPANIA || '', 
                                                     UN_NOMBRE    => 'FECHA CORTE DE PQR PARA SUI', 
                                                     UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS, 
                                                     UN_FECHA_PAR => SYSDATE);

        IF MI_PARFECHACORTE IS NULL THEN
          MI_TABLA         := 'PARAMETRO';
          MI_CAMPOS        := 'VALOR          =  ''01/01/1900'' '||
                              'MODIFIED_BY    = '''|| UN_USUARIO || ''', '||
                              'DATE_MODIFIED  = SYSDATE';
          MI_CONDICION     := ' COMPANIA =''' ||UN_COMPANIA|| '''' ||
                              ' AND NOMBRE = ''FECHA CORTE DE PQR PARA SUI''';  
          MI_RTA           := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                                 UN_ACCION    => 'M', 
                                                 UN_CAMPOS    => MI_CAMPOS, 
                                                 UN_CONDICION => MI_CONDICION);             
        END IF;        

        -- Actualiza los usuarios que tiene PQR's abiertas a excluidos a cartera
        MI_TABLA      := 'SP_USUARIO';
        MI_MERGEUSING := '  SELECT DISTINCT   SP_USUARIO.COMPANIA,
                                              SP_USUARIO.CICLO,
                                              SP_USUARIO.CODIGORUTA,  
                                              -1 EXCLUIRCARTERA
                            FROM SP_USUARIO
                            INNER JOIN SP_ORDENTRABAJO
                               ON SP_USUARIO.COMPANIA   = SP_ORDENTRABAJO.COMPANIA
                              AND SP_USUARIO.CICLO      = SP_ORDENTRABAJO.CICLO
                              AND SP_USUARIO.CODIGORUTA = SP_ORDENTRABAJO.CODIGORUTA
                            INNER JOIN SP_D_ORDENTRABAJO 
                               ON SP_ORDENTRABAJO.COMPANIA = SP_D_ORDENTRABAJO.COMPANIA 
                              AND SP_ORDENTRABAJO.CLASEDOC = SP_D_ORDENTRABAJO.CLASEDOC 
                              AND SP_ORDENTRABAJO.NUMORDEN = SP_D_ORDENTRABAJO.ORDENTRABAJO 
                            WHERE SP_USUARIO.COMPANIA      = '''||UN_COMPANIA||''' 
                              AND SP_USUARIO.CICLO BETWEEN '''||UN_CICLOINICIAL||''' AND '''||UN_CICLOFINAL||''' 
                              AND SP_ORDENTRABAJO.CLASEDOC = ''PQR'' 
                              AND SP_ORDENTRABAJO.ACTIVO NOT IN (0) 
                              AND SP_ORDENTRABAJO.FECHASOLICITUD >= TO_DATE('''||MI_PARFECHACORTE||''')
                              AND SP_D_ORDENTRABAJO.FECHASOLUCION IS NULL';
        MI_MERGEENLACE := '     TABLA.COMPANIA     = VISTA.COMPANIA 
                            AND TABLA.CICLO        = VISTA.CICLO         
                            AND TABLA.CODIGORUTA   = VISTA.CODIGORUTA';   
        MI_MERGEEXISTE := ' UPDATE SET TABLA.EXCLUIRCARTERA  = VISTA.EXCLUIRCARTERA,
                                       TABLA.MODIFIED_BY     = '''||UN_USUARIO||''',
                                       TABLA.DATE_MODIFIED   = SYSDATE ';                    

        MI_RTA         := PCK_DATOS.FC_ACME (UN_TABLA       => MI_TABLA,
                                             UN_ACCION      => 'MM',
                                             UN_MERGEUSING  => MI_MERGEUSING, 
                                             UN_MERGEENLACE => MI_MERGEENLACE, 
                                             UN_MERGEEXISTE => MI_MERGEEXISTE);                  
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                  THEN RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD  =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERRR_SERV_PUBLICOS_CUENTANIIF
      );

    END PR_PREPARAEXCLUIRCARTERA;

    --7   
    PROCEDURE PR_PRECARGARPROMEDIOS 
    /*
        NAME              : PR_PRECARGARPROMEDIOS --> EN ACCESS precargarPromedios llamada en la función CargaTempLectura
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : LEYDI MILENA CORTÉS FORERO
        DATE MIGRADOR     : 18/01/2017
        TIME              : 11:12 AM
        SOURCE MODULE     : SERVICIOS PUBLICOS
        DESCRIPTION       : Permite actualizar el valor de los consumos de los periodos anteriores al periodo actual y 
                            el valor del consumo calculado promedio entre dichos periodos.  
        PARAMETERS        : UN_COMPANIA      => Compañia de ingreso a la aplicación
                            UN_CODIGOINTERNO => Código interno del código de ruta seleccionado.
                            UN_CICLO         => Ciclo al que pertenece el código de ruta.
                            UN_CODIGORUTA    => Código de ruta seleccionado.
        @NAME:  precargarPromedios
        @METHOD:  PUT
        */
     (
        UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
        UN_CODIGOINTERNO    IN SP_USUARIO.CODIGOINTERNO%TYPE,
        UN_CICLO            IN PCK_SUBTIPOS.TI_CICLO,
        UN_CODIGORUTA       IN PCK_SUBTIPOS.TI_CODIGORUTA
      ) 
      AS
        MI_PERIODO1          PCK_SUBTIPOS.TI_ENTERO; 
        MI_FRECUENCIA        VARCHAR2(10 CHAR);
        MI_STRCONSUMOP       VARCHAR2(100 CHAR);
        MI_CAMPOS            PCK_SUBTIPOS.TI_CAMPOS;
        MI_CONDICION         PCK_SUBTIPOS.TI_CONDICION;
        MI_RTA               PCK_SUBTIPOS.TI_RTA_ACME;
        MI_MSGERROR          PCK_SUBTIPOS.TI_CLAVEVALOR;

        BEGIN
          BEGIN
            MI_FRECUENCIA := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                   UN_NOMBRE    => 'FRECUENCIA PERIODOS DE FACTURACION' , 
                                                   UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS , 
                                                   UN_FECHA_PAR => SYSDATE); 

            MI_PERIODO1 := 6; 
            IF MI_FRECUENCIA = 'M' 
            THEN 
               MI_PERIODO1 := 6; 
            ELSIF MI_FRECUENCIA = 'B' 
            THEN 
               MI_PERIODO1 := 3; 
            ELSIF MI_FRECUENCIA = 'C'
            THEN 
               MI_PERIODO1 := 3; 
            ELSIF MI_FRECUENCIA = 'T' 
            THEN 
               MI_PERIODO1 := 2; 
            END IF;   
          EXCEPTION
          WHEN NO_DATA_FOUND THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
               UN_EXC_COD    => SQLCODE,
               UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_PERIODO   
             ); 
          END;

          BEGIN
            MI_STRCONSUMOP := 'CONSUMO';
            <<CONSUMOS>>
            FOR MI_INT IN 1..(MI_PERIODO1-1) LOOP
               MI_STRCONSUMOP := MI_STRCONSUMOP ||  ' + CONSUMO'  || MI_INT;
               MI_CAMPOS      := 'CONSUMO'  || MI_INT || ' = 0';
               MI_CONDICION   := ' COMPANIA        = ''' || UN_COMPANIA || '''
                                AND CICLO          = ' || UN_CICLO || 
                               ' AND CONSUMO' || MI_INT || ' IS NULL 
                                AND CODTOTALIZADOR = ''' || UN_CODIGOINTERNO || '''';
               MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_USUARIO',
                                            UN_ACCION    => 'M',
                                            UN_CAMPOS    => MI_CAMPOS,
                                            UN_CONDICION => MI_CONDICION);
            END LOOP CONSUMOS;

            MI_STRCONSUMOP := '((' || MI_STRCONSUMOP || ')/' || MI_PERIODO1 || ')';
            MI_CAMPOS      := 'TEMP_CONSUMOPROMCAL = '  || MI_STRCONSUMOP;
            MI_CONDICION   := ' COMPANIA           = ''' || UN_COMPANIA || '''
                                AND CICLO          = ' || UN_CICLO || 
                               ' AND TEMP_CONSUMOPROMCAL IS NULL 
                                AND CODTOTALIZADOR = ''' || UN_CODIGOINTERNO || '''';
            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_USUARIO',
                                         UN_ACCION    => 'M',
                                         UN_CAMPOS    => MI_CAMPOS,
                                         UN_CONDICION => MI_CONDICION);

            MI_CONDICION   := ' COMPANIA            = ''' || UN_COMPANIA || '''
                                 AND CICLO          = ' || UN_CICLO || 
                               ' AND CODTOTALIZADOR = ''' || UN_CODIGOINTERNO || '''' ||
                               ' AND (TEMP_CONSUMOPROMCAL - (' || MI_STRCONSUMOP || ')) < -0.001
                                 OR (TEMP_CONSUMOPROMCAL - (' || MI_STRCONSUMOP || ')) > 0.001' ;
            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_USUARIO',
                                         UN_ACCION    => 'M',
                                         UN_CAMPOS    => MI_CAMPOS,
                                         UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
          END;
        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION THEN
           MI_MSGERROR(1).CLAVE := 'CODRUTA';
           MI_MSGERROR(1).VALOR := UN_CODIGORUTA;
           PCK_ERR_MSG.RAISE_WITH_MSG(
               UN_EXC_COD    => SQLCODE,
               UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACT_USUREGPROM,
               UN_REEMPLAZOS  => MI_MSGERROR,
               UN_TABLAERROR => 'SP_USUARIO'
             );

    END PR_PRECARGARPROMEDIOS;

    --8
    PROCEDURE PR_OPERACONSUMOMANUAL 
    /*
        NAME              : PR_OPERACONSUMOMANUAL --> EN ACCESS OperaConsumoManual 
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : LEYDI MILENA CORTÉS FORERO
        DATE MIGRADOR     : 18/01/2017
        TIME              : 02:12 PM
        SOURCE MODULE     : SERVICIOS PUBLICOS
        MODIFIER          : SERGIO ESTEBAN PIÑA VARGAS
        DATE MODIFIED     : 14/07/2017
                            SE AGREGAN ACTUALIZACIONES EN LOS CAMPOS DE AUDITORÍA PARAMETRO UN_USUARIO
        TIME              :
        DESCRIPTION       : Permite operar los consumos de los macromedidores en las novedades Aforo consumo manuales.  
        PARAMETERS        : UN_COMPANIA      => Compañia de ingreso a la aplicación
                            UN_CICLO         => Ciclo al que pertenece el código de ruta.
                            UN_CODIGOINTERNO => Código interno del código de ruta seleccionado.
                            UN_OPCION        => Operación seleccionada.
                            UN_CONSUMO       => Valor del consumo del totalizador.
                            UN_USUARIO       => Código del usuario que realiza las modificaciones
        @NAME:  operarConsumoManual
        @METHOD:  PUT
        */
     (
        UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
        UN_CICLO            IN PCK_SUBTIPOS.TI_CICLO,
        UN_CODIGOINTERNO    IN SP_USUARIO.CODIGOINTERNO%TYPE,
        UN_OPCION           IN PCK_SUBTIPOS.TI_ENTERO,
        UN_CONSUMO          IN PCK_SUBTIPOS.TI_DOBLE,
        UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
      ) 
      AS 
        MI_SUMAMICRO         VARCHAR2(300 CHAR);
        MI_VALOR             VARCHAR2(300 CHAR);
        MI_VALORACU          VARCHAR2(300 CHAR);
        MI_CAMPOS            PCK_SUBTIPOS.TI_CAMPOS;
        MI_CONDICION         PCK_SUBTIPOS.TI_CONDICION;
        MI_RTA               PCK_SUBTIPOS.TI_RTA_ACME;

        BEGIN
          BEGIN
            IF UN_OPCION = 1
            THEN
              MI_CAMPOS := 'CONSUMOACU = CONSUMO,
                            AFOROCONSMANUAL=-1,
                            DATE_MODIFIED = SYSDATE,
                            MODIFIED_BY = '''||UN_USUARIO||'''
                            ';
              MI_CONDICION   := ' COMPANIA           = ''' || UN_COMPANIA || '''
                                  AND CICLO          = ' || UN_CICLO || 
                                ' AND CODTOTALIZADOR = ''' || UN_CODIGOINTERNO || '''';
              BEGIN
                BEGIN
                  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_USUARIO',
                                             UN_ACCION    => 'M',
                                             UN_CAMPOS    => MI_CAMPOS,
                                             UN_CONDICION => MI_CONDICION);

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                END;
                EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACT_USUREGPROM,
                    UN_TABLAERROR => 'SP_USUARIO'
                );
              END;

            ELSIF UN_OPCION = 2
            THEN   
              MI_CAMPOS := 'CONSUMOALC = CONSUMO,
                            AFOROCONSMANUAL=-1 ,
                            DATE_MODIFIED = SYSDATE,
                            MODIFIED_BY = '''||UN_USUARIO||'''
                            ';
              MI_CONDICION   := ' COMPANIA           = ''' || UN_COMPANIA || '''
                                  AND CICLO          = ' || UN_CICLO || 
                                ' AND CODTOTALIZADOR = ''' || UN_CODIGOINTERNO || '''';
              BEGIN
                BEGIN
                  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_USUARIO',
                                             UN_ACCION    => 'M',
                                             UN_CAMPOS    => MI_CAMPOS,
                                             UN_CONDICION => MI_CONDICION);

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                END;
                EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACT_USUREGPROM,
                    UN_TABLAERROR => 'SP_USUARIO'
                );
              END;                                                  
            ELSIF UN_OPCION = 3
            OR UN_OPCION = 4
            THEN  
              <<CONSUMOS>>
              FOR RS_USUARIOS IN (SELECT CODIGORUTA, 
                                         LECTURAAFORO, 
                                         CONSUMO,  
                                         CONSUMOALC, 
                                         CONSUMOACU 
                                    FROM SP_USUARIO 
                                   WHERE COMPANIA       = UN_COMPANIA 
                                     AND CICLO          = UN_CICLO
                                     AND CODTOTALIZADOR = UN_CODIGOINTERNO)
              LOOP
                MI_SUMAMICRO := MI_SUMAMICRO || '+' || NVL(RS_USUARIOS.LECTURAAFORO, 0);
              END LOOP CONSUMOS;
              <<CONSUMOS>>
              FOR RS_USUARIOS IN (SELECT CODIGORUTA, 
                                         LECTURAAFORO, 
                                         CONSUMO,  
                                         CONSUMOALC, 
                                         CONSUMOACU 
                                    FROM SP_USUARIO 
                                   WHERE COMPANIA       = UN_COMPANIA 
                                     AND CICLO          = UN_CICLO
                                     AND CODTOTALIZADOR = UN_CODIGOINTERNO)
              LOOP
                IF UN_CONSUMO > 0 
                THEN
                  MI_VALOR := 'ROUND((' || NVL(RS_USUARIOS.LECTURAAFORO, 0) || '/' || MI_SUMAMICRO || ') * ' || UN_CONSUMO || ', 0)';
                ELSE
                  MI_VALOR := '0';
                END IF;

                MI_VALORACU := MI_VALORACU || '+' || MI_VALOR;
                IF UN_OPCION = 3
                THEN
                  MI_CAMPOS := 'CONSUMOACU = ' || MI_VALOR || ',
                                AFOROCONSMANUAL=-1,';
                ELSE 
                  MI_CAMPOS := 'CONSUMOALC = ' || MI_VALOR || ',
                                AFOROCONSMANUAL=-1,';
                END IF;

                MI_CAMPOS := MI_CAMPOS || 'DATE_MODIFIED = SYSDATE,
                            MODIFIED_BY = '''||UN_USUARIO||''' 
                            ';

                MI_CONDICION   := ' COMPANIA     = ''' || UN_COMPANIA || '''
                                  AND CICLO      = ' || UN_CICLO || 
                                ' AND CODIGORUTA = ''' || RS_USUARIOS.CODIGORUTA || '''';

                BEGIN
                  BEGIN
                    MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'SP_USUARIO',
                                                 UN_ACCION    => 'M',
                                                 UN_CAMPOS    => MI_CAMPOS,
                                                 UN_CONDICION => MI_CONDICION);

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                  END;
                  EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_FACTURACION THEN
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                       UN_EXC_COD    => SQLCODE,
                       UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_ACT_USUREGPROM,
                       UN_TABLAERROR => 'SP_USUARIO'
                  );
                END;          
              END LOOP CONSUMOS;
            END IF;
          END;
    END PR_OPERACONSUMOMANUAL;

--10
PROCEDURE PR_ANULARFINANCIABLEDEDEUDA
	(
   /*
      NAME              : PR_ANULARFINANCIABLEDEDEUDA EN ACCESS Evento del checkAnular del Formulario Financiables de Deuda
      AUTHORS           : SYSMAN  
      AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
      DATE MIGRADOR     : 02/02/2017
      TIME              : 10:44 AM
      SOURCE MODULE     : SysmanSp2016.05.04
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              :    
      DESCRIPTION       : 
      MODIFICATIONS     : 
      PARAMETERS        :   UN_COMPANIA: Código de la compañia.
                            UN_CICLO: Número de ciclo del usuario.
                            UN_CODIGORUTA: Código de ruta del usuario. 
                            UN_ANO: Número de año del ciclo.
      @NAME:  anularFinanciabledeDeuda
      @METHOD:  PUT    
    */ 

	UN_COMPANIA 	    IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_CICLO 		      IN PCK_SUBTIPOS.TI_CICLO,
	UN_CODIGORUTA 	  IN PCK_SUBTIPOS.TI_CODIGORUTA,
	UN_ANO 			      IN PCK_SUBTIPOS.TI_ANIO,
	UN_PERIODO 		    IN PCK_SUBTIPOS.TI_PERIODO,
	UN_VLRAFINANCIAR  IN PCK_SUBTIPOS.TI_DOBLE
	)
	AS
		MI_TABLA 		    PCK_SUBTIPOS.TI_TABLA;
		MI_MERGEUSING 	PCK_SUBTIPOS.TI_MERGEUSING;
		MI_MERGEENLACE 	PCK_SUBTIPOS.TI_MERGEENLACE;
		MI_MERGEEXISTE 	PCK_SUBTIPOS.TI_MERGEEXISTE;
		MI_CAMPOS 		  PCK_SUBTIPOS.TI_CAMPOS;
		MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
		MI_DEUDA        PCK_SUBTIPOS.TI_DOBLE;
    MI_STRSQL       PCK_SUBTIPOS.TI_STRSQL;

	BEGIN 
		-- Actualiza Tabla Facturado
		MI_TABLA := 'SP_FACTURADO';
		MI_MERGEUSING := 'SELECT  
							SP_D_DEUDAFACTURADAFINANCIADA.COMPANIA,
							SP_D_DEUDAFACTURADAFINANCIADA.CICLO,
							SP_D_DEUDAFACTURADAFINANCIADA.CODIGORUTA,
							SP_D_DEUDAFACTURADAFINANCIADA.ANO,
							SP_D_DEUDAFACTURADAFINANCIADA.PERIODO,
							SP_D_DEUDAFACTURADAFINANCIADA.CONCEPTO, 
							SP_D_DEUDAFACTURADAFINANCIADA.DEUDA,
							SP_D_DEUDAFACTURADAFINANCIADA.VALOR_FACTURADO,
							0 VALORFINACT,
							0 VALORFINANT
						FROM SP_FACTURADO 
							INNER JOIN SP_D_DEUDAFACTURADAFINANCIADA
								ON SP_FACTURADO.COMPANIA    = SP_D_DEUDAFACTURADAFINANCIADA.COMPANIA
								AND SP_FACTURADO.CICLO      = SP_D_DEUDAFACTURADAFINANCIADA.CICLO 
								AND SP_FACTURADO.CODIGORUTA = SP_D_DEUDAFACTURADAFINANCIADA.CODIGORUTA
								AND SP_FACTURADO.ANO        = SP_D_DEUDAFACTURADAFINANCIADA.ANO 
								AND SP_FACTURADO.PERIODO    = SP_D_DEUDAFACTURADAFINANCIADA.PERIODO
								AND SP_FACTURADO.CONCEPTO   = SP_D_DEUDAFACTURADAFINANCIADA.CONCEPTO 
						WHERE SP_FACTURADO.COMPANIA = ''' || UN_COMPANIA ||''' 
							AND SP_FACTURADO.CICLO =''' || UN_CICLO ||'''
							AND SP_FACTURADO.CODIGORUTA = ''' || UN_CODIGORUTA ||''' 
							AND SP_FACTURADO.ANO = ' ||UN_ANO|| 
						'	AND SP_FACTURADO.PERIODO = ''' || UN_CICLO ||'''';

		MI_MERGEENLACE	:= 'TABLA.COMPANIA    = VISTA.COMPANIA
						AND TABLA.CICLO       = VISTA.CICLO
						AND TABLA.CODIGORUTA  = VISTA.CODIGORUTA
						AND TABLA.ANO         = VISTA.ANO
						AND TABLA.PERIODO     = VISTA.PERIODO
						AND TABLA.CONCEPTO    = VISTA.CONCEPTO'	;

		MI_MERGEEXISTE :=  'UPDATE SET 
								TABLA.DEUDA 			= VISTA.DEUDA,
								TABLA.VALOR_FACTURADO 	= VISTA.VALOR_FACTURADO,
								TABLA.VALORFINACT 		= VISTA.VALORFINACT,
								TABLA.VALORFINANT 		= VISTA.VALORFINANT';
		BEGIN 
			PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA 		=> MI_TABLA,
													UN_ACCION 		=> 'MM',
													UN_MERGEUSING 	=> MI_MERGEUSING,
													UN_MERGEENLACE 	=> MI_MERGEENLACE,
													UN_MERGEEXISTE 	=> MI_MERGEEXISTE);
		EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
			RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
		END;	
 -- Actualiza tabla sp_usuario
		MI_TABLA := 'SP_USUARIO';
		MI_CAMPOS := 'SP_USUARIO.TOTFACTURAPERACTUAL = ' ||UN_VLRAFINANCIAR|| ', SP_USUARIO.PERIODOSNOCOBROFIN = 0,SP_USUARIO.PERIODOSNOCOBROFAC = 0';
		MI_CONDICION := 'SP_USUARIO.COMPANIA = '''||UN_COMPANIA||''' AND SP_USUARIO.CICLO = '''||UN_CICLO||''' AND SP_USUARIO.CODIGORUTA = '''||UN_CODIGORUTA||''' AND SP_USUARIO.ANO = '||UN_ANO|| ' AND SP_USUARIO.PERIODO = '''||UN_PERIODO||'''';

		BEGIN
			PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME( 	UN_TABLA  => MI_TABLA,
													UN_ACCION => 'M',
													UN_CAMPOS => MI_CAMPOS,
													UN_CONDICION => MI_CONDICION);
		EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
			RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
		END;
		--Actualiza tabla sp_facturado
		MI_TABLA := 'SP_FACTURADO';
		MI_CAMPOS := 'SP_FACTURADO.DEUDA = 0 , SP_FACTURADO.VALOR_FACTURADO = 0';
		MI_CONDICION := 'SP_FACTURADO.COMPANIA = '''||UN_COMPANIA||''' AND SP_FACTURADO.CICLO = '''||UN_CICLO||''' AND SP_FACTURADO.CODIGORUTA = '''||UN_CODIGORUTA||''' AND SP_FACTURADO.ANO = '||UN_ANO|| ' AND SP_FACTURADO.PERIODO = '''||UN_PERIODO||''' AND SP_FACTURADO.CONCEPTO = 12';

		BEGIN
			PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (	UN_TABLA 	 => MI_TABLA,
													UN_ACCION 	 => 'M',
													UN_CAMPOS 	 => MI_CAMPOS,
													UN_CONDICION => MI_CONDICION);
		EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
			RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
		END;
		--Elimina registro en sp_financiables
		MI_TABLA := 'SP_FINANCIABLES';
		MI_CONDICION := 'SP_FINANCIABLES.COMPANIA = '''||UN_COMPANIA||''' AND SP_FINANCIABLES.CICLO = '''||UN_CICLO||''' AND SP_FINANCIABLES.CODIGORUTA = '''||UN_CODIGORUTA||''' AND SP_FINANCIABLES.ANO >= ' ||UN_ANO|| ' AND SP_FINANCIABLES.PERIODO >= '''||UN_PERIODO||'''';

		BEGIN 
			PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA => MI_TABLA,
													UN_ACCION => 'E',
													UN_CONDICION => MI_CONDICION);
		EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
			RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
		END;											

		 MI_STRSQL  := 'SELECT SUM(SP_D_DEUDAFACTURADAFINANCIADA.DEUDA) SUMADEUDA 
                    FROM SP_D_DEUDAFACTURADAFINANCIADA
                    WHERE SP_D_DEUDAFACTURADAFINANCIADA.COMPANIA = '''||UN_COMPANIA||''' 
                      AND SP_D_DEUDAFACTURADAFINANCIADA.CICLO = '''||UN_CICLO||''' 
                      AND SP_D_DEUDAFACTURADAFINANCIADA.CODIGORUTA ='''||UN_CODIGORUTA||''' 
                      AND SP_D_DEUDAFACTURADAFINANCIADA.ANO = '||UN_ANO|| 
                    ' AND SP_D_DEUDAFACTURADAFINANCIADA.PERIODO ='''||UN_PERIODO||'''';
		BEGIN
      EXECUTE IMMEDIATE MI_STRSQL INTO MI_DEUDA; 
        EXCEPTION WHEN NO_DATA_FOUND THEN 
      BEGIN  
       --Actualiza tabla sp_facturado
        MI_TABLA := 'SP_FACTURADO';
        MI_CAMPOS := 'SP_FACTURADO.VALOR_FACTURADO = ' || MI_DEUDA;
        MI_CONDICION := 'SP_FACTURADO.COMPANIA = '''||UN_COMPANIA||''' AND SP_FACTURADO.CICLO = '''||UN_CICLO||''' AND SP_FACTURADO.CODIGORUTA = '''||UN_CODIGORUTA||''' AND SP_FACTURADO.ANO = ' ||UN_ANO|| ' AND SP_FACTURADO.PERIODO = '''||UN_PERIODO||''' AND SP_FACTURADO.CONCEPTO = 250';
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
      END;
    END;
		BEGIN 
    --Elimina registro en sp_d_deudafacturadafinanciada
		MI_TABLA := 'SP_D_DEUDAFACTURADAFINANCIADA';
		MI_CONDICION := 'SP_D_DEUDAFACTURADAFINANCIADA.COMPANIA = '''||UN_COMPANIA||''' AND SP_D_DEUDAFACTURADAFINANCIADA.CICLO = '''||UN_CICLO||''' AND SP_D_DEUDAFACTURADAFINANCIADA.CODIGORUTA = '''||UN_CODIGORUTA||''' AND SP_D_DEUDAFACTURADAFINANCIADA.ANO = '||UN_ANO|| ' AND SP_D_DEUDAFACTURADAFINANCIADA.PERIODO = '''||UN_PERIODO||'''';

			PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA => MI_TABLA,
													UN_ACCION => 'E',
													UN_CONDICION => MI_CONDICION);
		EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
			RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
		END;
	EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
		PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE
									,UN_ERROR_COD  => PCK_ERRORES.ERR_ANULARFINANCIABLE
                                    ,UN_TABLAERROR => 'SP_FACTURADO');          	
	END PR_ANULARFINANCIABLEDEDEUDA;

FUNCTION FC_EXISTEPERIODO
(

   /*
      NAME              : FC_EXISTEPERIODO  --> EN ACCESS ExistePeriodo
      AUTHORS           : SYSMAN  
      AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
      DATE MIGRADOR     : 06/02/2017
      TIME              : 08:55 AM
      SOURCE MODULE     : SysmanSp2016.05.04
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              :    
      DESCRIPTION       : Verifica si el período esta creado, cuando se manejan períodos siguientes no hay certeza de que el período exista
      MODIFICATIONS     : 
      PARAMETERS        :   UN_COMPANIA: Código de la compañia.
                            UN_ANO: Número del año del período.
                            UN_PERIODO: Número del mes del Período. 
      @NAME:  existePeriodo
      @METHOD:  GET 
    */ 

	UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_ANO      IN PCK_SUBTIPOS.TI_ANIO,
	UN_PERIODO  IN PCK_SUBTIPOS.TI_PERIODO 
)
  RETURN NUMBER
	AS 
		MI_EXISTEPERIODO  PCK_SUBTIPOS.TI_LOGICO := -1;
		MI_STRSQL   	    PCK_SUBTIPOS.TI_STRSQL;
		MI_EXISTE         VARCHAR(1 CHAR);
	BEGIN 	
		MI_STRSQL := 'SELECT ''X'' 
					  FROM SP_PERIODO
					  WHERE SP_PERIODO.COMPANIA = '''||UN_COMPANIA||'''
						AND ANO = ' ||UN_ANO||
					'	AND MES = '''||UN_PERIODO||'''';
		BEGIN 
			EXECUTE IMMEDIATE MI_STRSQL INTO MI_EXISTE; 				
      EXCEPTION WHEN NO_DATA_FOUND THEN 
				MI_EXISTEPERIODO := 0;
				RETURN MI_EXISTEPERIODO;
		END;
	RETURN MI_EXISTEPERIODO;
	EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
		PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE
									,UN_ERROR_COD  => PCK_ERRORES.ERR_EXISTENCIAPERIODO
                                    ,UN_TABLAERROR => 'SP_PERIODO');	
  END FC_EXISTEPERIODO;

PROCEDURE PR_DISCRIMINARFINANCIACION
(
    /*
      NAME              : PR_DISCRIMINARFINANCIACION  --> EN ACCESS DiscriminarFinanciacion
      AUTHORS           : SYSMAN  
      AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
      DATE MIGRADOR     : 06/02/2017
      TIME              : 04:44 PM
      SOURCE MODULE     : SysmanSp2016.05.04
      MODIFIER          : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MODIFIED     : 23/05/2017
      TIME              :    
      DESCRIPTION       : 
      MODIFICATIONS     : AGREGAR EL PARAMETROS UN_USUSARIO PARA LLEVAR LA AUDITORIA DE LAS OPERACIONES DML REALIZADAS EN LA FUNCION
      PARAMETERS        :   UN_COMPANIA: Código de la compañia.
                            UN_CICLO: Número de ciclo del usuario.
                            UN_CODIGORUTA: Código de ruta del usuario. 
                            UN_ANO: Número de año del ciclo.
                            UN_PERIODO = Número de período del ciclo. 
                            UN_VALORABONO = Valor del Abono Inicial del Usuario.
                            UN_NROCUOTAS = Número de cuotas en el que el Usuario pagara su deuda.	
                            UN_FACTURAPERACTUAL : Valor de la factura actual del usuario 
                            UN_CONSECUTIVO: Valor del consecutivo con el que se va a generar el financiable de deuda
    @NAME:  discriminarFinanciacion
    @METHOD:  PUT 
    */ 

	UN_COMPANIA  	      IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_CICLO  	 	      IN PCK_SUBTIPOS.TI_CICLO,
	UN_CODIGORUTA       IN PCK_SUBTIPOS.TI_CODIGORUTA,
	UN_ANO  		        IN PCK_SUBTIPOS.TI_ANIO,
	UN_PERIODO          IN PCK_SUBTIPOS.TI_PERIODO,
	UN_VALORABONO  	    IN PCK_SUBTIPOS.TI_DOBLE,
	UN_NROCUOTAS	      IN PCK_SUBTIPOS.TI_ENTERO,
  UN_VRAFINANCIAR     IN PCK_SUBTIPOS.TI_DOBLE,
  UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO,
  UN_CONSECUTIVO      IN PCK_SUBTIPOS.TI_ENTERO_LARGO
)
	AS 
		MI_ETAPA 		  VARCHAR(2 CHAR);
		MI_STRSQL 		PCK_SUBTIPOS.TI_STRSQL;
		MI_COMPANIA  	PCK_SUBTIPOS.TI_COMPANIA;
		MI_CICLO      PCK_SUBTIPOS.TI_CICLO;
		MI_CODIGORUTA	PCK_SUBTIPOS.TI_CODIGORUTA;
		MI_ANO        PCK_SUBTIPOS.TI_ANIO;
		MI_PERIODO    PCK_SUBTIPOS.TI_PERIODO;
		MI_TOTALFACT  PCK_SUBTIPOS.TI_DOBLE;
		MI_TOTALDEUDA PCK_SUBTIPOS.TI_DOBLE;
		MI_TOTALFIN   PCK_SUBTIPOS.TI_DOBLE;
		MI_SUMAFINACT PCK_SUBTIPOS.TI_DOBLE;
		MI_SUMAFINANT PCK_SUBTIPOS.TI_DOBLE;
		MI_TABLA      PCK_SUBTIPOS.TI_TABLA;
		MI_CAMPOS     PCK_SUBTIPOS.TI_STRSQL;
		MI_CONDICION  PCK_SUBTIPOS.TI_STRSQL;
		MI_VALORES    PCK_SUBTIPOS.TI_STRSQL;
    MI_SUMAFAC    PCK_SUBTIPOS.TI_DOBLE;
    MI_MSGERROR   PCK_SUBTIPOS.TI_CLAVEVALOR;

	BEGIN 
		MI_ETAPA := '0';

        MI_STRSQL := ' SELECT NVL(SUM(SP_FACTURADO.VALOR_FACTURADO + SP_FACTURADO.DEUDA),0) AS SUMAFAC 
                      FROM SP_FACTURADO
                      WHERE SP_FACTURADO.COMPANIA     = '''||UN_COMPANIA||'''  
                        AND SP_FACTURADO.CICLO        = '''||UN_CICLO||'''  
                        AND SP_FACTURADO.CODIGORUTA   ='''||UN_CODIGORUTA||'''
                        AND SP_FACTURADO.ANO          = ' ||UN_ANO|| '
                        AND SP_FACTURADO.PERIODO      ='''||UN_PERIODO||'''  
                        AND (SP_FACTURADO.CONCEPTO BETWEEN 0 AND 50 OR SP_FACTURADO.CONCEPTO IN (201,202,203,204,205,206,207,246,247,248,249))';
      EXECUTE IMMEDIATE MI_STRSQL INTO MI_SUMAFAC;
      IF UN_VRAFINANCIAR <> MI_SUMAFAC THEN 
        BEGIN
         RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
         MI_MSGERROR (1).CLAVE := 'CODIGORUTA';
         MI_MSGERROR (1).VALOR := UN_CODIGORUTA;
                  PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                              UN_ERROR_COD  => PCK_ERRORES.ERR_FACTURAACTUAL,
                                              UN_REEMPLAZOS  => MI_MSGERROR);  

        END;
      END IF;  

		MI_STRSQL := 'SELECT  SP_FACTURADO.COMPANIA, 
                          SP_FACTURADO.CICLO, 
                          SP_FACTURADO.CODIGORUTA, 
                          SP_FACTURADO.ANO, 
                          SP_FACTURADO.PERIODO, 
                          SUM(SP_FACTURADO.VALOR_FACTURADO - SP_FACTURADO.VALORABONOACT)  TOTFACT, 
                          SUM(SP_FACTURADO.DEUDA - SP_FACTURADO.VALORABONOANT)  TOTDEUDA, 
                          SUM(SP_FACTURADO.VALOR_FACTURADO + SP_FACTURADO.DEUDA - SP_FACTURADO.VALORABONOANT - SP_FACTURADO.VALORABONOACT)  TOTALFIN
                  FROM SP_FACTURADO 
                  WHERE SP_FACTURADO.COMPANIA   = '''||UN_COMPANIA||''' 
                    AND SP_FACTURADO.CICLO      = '''||UN_CICLO||''' 
                    AND SP_FACTURADO.CODIGORUTA = '''||UN_CODIGORUTA||''' 
                    AND SP_FACTURADO.ANO        = ' ||UN_ANO|| '
                    AND SP_FACTURADO.PERIODO    = '''||UN_PERIODO||''' 
                    AND (SP_FACTURADO.CONCEPTO BETWEEN 1 AND 48 
                      OR (SP_FACTURADO.CONCEPTO) IN (201,202,203,204,205,206,207,246,247,248,249))
                  GROUP BY  SP_FACTURADO.COMPANIA, 
                            SP_FACTURADO.CICLO, 
                            SP_FACTURADO.CODIGORUTA, 
                            SP_FACTURADO.ANO, 
                            SP_FACTURADO.PERIODO';
		BEGIN
			EXECUTE IMMEDIATE MI_STRSQL INTO MI_COMPANIA,MI_CICLO,MI_CODIGORUTA,MI_ANO,MI_PERIODO,MI_TOTALFACT,MI_TOTALDEUDA,MI_TOTALFIN; 			
				EXCEPTION WHEN NO_DATA_FOUND THEN
				RETURN;
		END;	

		IF UN_VALORABONO <= MI_TOTALDEUDA THEN 
			MI_ETAPA := '1';
  		BEGIN
        BEGIN 
          MI_TABLA:= 'SP_D_DEUDAFACTURADAFINANCIADA';
          MI_CONDICION := '    COMPANIA   = '''||UN_COMPANIA||''' 
                           AND CICLO      = '''||UN_CICLO||''' 
                           AND CODIGORUTA = '''||UN_CODIGORUTA||''' 
                           AND ANO        = '||UN_ANO||' 
                           AND PERIODO    = '''||UN_PERIODO||''' ';
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => MI_TABLA,
                                                UN_ACCION     => 'E',
                                                UN_CONDICION  => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
          MI_MSGERROR (1).CLAVE := 'ETAPA';
          MI_MSGERROR (1).VALOR := MI_ETAPA;
          PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_TABLAERROR => MI_TABLA,
                      UN_ERROR_COD  => PCK_ERRORES.ERR_ELIMINAR_D_DEUDA,
                      UN_REEMPLAZOS => MI_MSGERROR);  
      END;
			MI_ETAPA:='2';
			BEGIN
        BEGIN
          MI_TABLA := 'SP_D_DEUDAFACTURADAFINANCIADA';
          MI_CAMPOS:= 'COMPANIA,CICLO,CODIGORUTA,ANO,PERIODO,CONCEPTO,VALOR_FACTURADO
                       ,DEUDA,VALOR_FACTURADOANT,DEUDAANT,VALOR_FACTURADOIN,DEUDAIN,RECAUDADOPERIODO
                       ,DOBLEPAGO,SALDOCREDITO,ABONO,CREDITOABONADO,VALORFINACT,VALORFINANT,VALORFINDEUDA
                       ,VALORFINPERIODO,CUOTASSALDO,SALDOFINACT,SALDOFINANT,CREATED_BY,DATE_CREATED,CONSECUTIVO';
          MI_VALORES:= 'SELECT  SP_FACTURADO.COMPANIA, 
                                SP_FACTURADO.CICLO, 
                                SP_FACTURADO.CODIGORUTA, 
                                SP_FACTURADO.ANO, 
                                SP_FACTURADO.PERIODO, 
                                SP_FACTURADO.CONCEPTO, 
                                SP_FACTURADO.VALOR_FACTURADO - SP_FACTURADO.VALORABONOACT, 
                                SP_FACTURADO.DEUDA - SP_FACTURADO.VALORABONOANT,0,0,0,0,0,0,0,0,0,0,
                                PCK_SYSMAN_UTL.FC_ROUND(((SP_FACTURADO.DEUDA-SP_FACTURADO.VALORABONOANT)*'||UN_VALORABONO||'/'||MI_TOTALDEUDA||'),0),
                                SP_FACTURADO.DEUDA - SP_FACTURADO.VALORABONOANT, 
                                SP_FACTURADO.VALOR_FACTURADO - SP_FACTURADO.VALORABONOACT,
                               '||UN_NROCUOTAS||', 0, 0,'''||UN_USUARIO||''', SYSDATE, ' || UN_CONSECUTIVO || ' 
                        FROM SP_FACTURADO
                        WHERE SP_FACTURADO.COMPANIA 		= '''||UN_COMPANIA||''' 
                          AND SP_FACTURADO.CICLO 		  	= '''||UN_CICLO||''' 
                          AND SP_FACTURADO.CODIGORUTA 	= '''||UN_CODIGORUTA||''' 
                          AND SP_FACTURADO.ANO 			    = '||UN_ANO|| 
                        '	AND SP_FACTURADO.PERIODO 		  = '''||UN_PERIODO||''' 
                          AND (SP_FACTURADO.CONCEPTO BETWEEN 1 AND 48 
                            OR SP_FACTURADO.CONCEPTO In (201,202,203,204,205,206,207,246,247,248,249))';
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA    => MI_TABLA,
                                                  UN_ACCION   => 'IS',
                                                  UN_CAMPOS   => MI_CAMPOS,
                                                  UN_VALORES  => MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
        MI_MSGERROR (1).CLAVE := 'ETAPA';
        MI_MSGERROR (1).VALOR := MI_ETAPA;
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                             UN_ERROR_COD  => PCK_ERRORES.ERR_INSERCION_D_DEUDA,
                                             UN_REEMPLAZOS  => MI_MSGERROR);  
      END;
			MI_ETAPA := '3';
			MI_STRSQL := 'SELECT  SP_D_DEUDAFACTURADAFINANCIADA.COMPANIA, 
                            SP_D_DEUDAFACTURADAFINANCIADA.CICLO, 
                            SP_D_DEUDAFACTURADAFINANCIADA.CODIGORUTA, 
                            SP_D_DEUDAFACTURADAFINANCIADA.ANO, 
                            SP_D_DEUDAFACTURADAFINANCIADA.PERIODO, 
                            SUM(SP_D_DEUDAFACTURADAFINANCIADA.VALORFINACT) SUMAFINACT, 
                            SUM(SP_D_DEUDAFACTURADAFINANCIADA.VALORFINANT) SUMAFINANT
                    FROM SP_D_DEUDAFACTURADAFINANCIADA
                    WHERE SP_D_DEUDAFACTURADAFINANCIADA.COMPANIA 		= '''||UN_COMPANIA||''' 
                      AND SP_D_DEUDAFACTURADAFINANCIADA.CICLO 		  ='''||UN_CICLO||''' 
                      AND SP_D_DEUDAFACTURADAFINANCIADA.CODIGORUTA 	='''||UN_CODIGORUTA||''' 
                      AND SP_D_DEUDAFACTURADAFINANCIADA.ANO 			  = '||UN_ANO||  
                    '	AND SP_D_DEUDAFACTURADAFINANCIADA.PERIODO 		= '''||UN_PERIODO||''' 
                      AND (SP_D_DEUDAFACTURADAFINANCIADA.CONCEPTO BETWEEN 1 AND 48 
                        OR SP_D_DEUDAFACTURADAFINANCIADA.CONCEPTO IN (201,202,203,204,205,206,207,246,247,248,249))
                    GROUP BY  SP_D_DEUDAFACTURADAFINANCIADA.COMPANIA, 
                              SP_D_DEUDAFACTURADAFINANCIADA.CICLO, 
                              SP_D_DEUDAFACTURADAFINANCIADA.CODIGORUTA,
                              SP_D_DEUDAFACTURADAFINANCIADA.ANO,
                              SP_D_DEUDAFACTURADAFINANCIADA.PERIODO';
			BEGIN
			MI_ETAPA := '4';
				EXECUTE IMMEDIATE MI_STRSQL INTO MI_COMPANIA,MI_CICLO,MI_CODIGORUTA,MI_ANO,MI_PERIODO,MI_SUMAFINACT,MI_SUMAFINANT; 	
				IF MI_SUMAFINANT <> UN_VALORABONO THEN 
       	<<DEUDAFACTURADA>>
					FOR RS_CON IN (	SELECT	SP_D_DEUDAFACTURADAFINANCIADA.CONCEPTO,
                                  SP_D_DEUDAFACTURADAFINANCIADA.VALORFINANT
                          FROM SP_D_DEUDAFACTURADAFINANCIADA 
                          WHERE SP_D_DEUDAFACTURADAFINANCIADA.COMPANIA 		= UN_COMPANIA 
                            AND SP_D_DEUDAFACTURADAFINANCIADA.CICLO 		  = UN_CICLO 
                            AND SP_D_DEUDAFACTURADAFINANCIADA.CODIGORUTA 	= UN_CODIGORUTA 
                            AND SP_D_DEUDAFACTURADAFINANCIADA.ANO 			  = UN_ANO
                            AND SP_D_DEUDAFACTURADAFINANCIADA.PERIODO 		= UN_PERIODO 
                            AND (SP_D_DEUDAFACTURADAFINANCIADA.CONCEPTO BETWEEN 1 AND 48 
                              OR SP_D_DEUDAFACTURADAFINANCIADA.CONCEPTO IN (201,202,203,204,205,206,207,246,247,248,249)))

					LOOP
						BEGIN
              BEGIN 
                MI_ETAPA := '5';
                MI_TABLA := 'SP_D_DEUDAFACTURADAFINANCIADA';
                MI_CAMPOS := '  VALORFINANT   = ' ||RS_CON.VALORFINANT||'+ (' ||UN_VALORABONO|| '-' ||MI_SUMAFINANT||')
                              , MODIFIED_BY   = '''||UN_USUARIO||'''
                              , DATE_MODIFIED = SYSDATE ';
                MI_CONDICION := '    COMPANIA   = '''||UN_COMPANIA||''' 
                                 AND CICLO      = '''||UN_CICLO||''' 
                                 AND CODIGORUTA = '''||UN_CODIGORUTA||''' 
                                 AND ANO        = ' ||UN_ANO||' 
                                 AND PERIODO    = '''||UN_PERIODO||''' 
                                 AND CONCEPTO   = '||RS_CON.CONCEPTO;

                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA 		  => MI_TABLA,
                                                        UN_ACCION 		=> 'M',
                                                        UN_CAMPOS 		=> MI_CAMPOS,
                                                        UN_CONDICION 	=> MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;	
              END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
                MI_MSGERROR (1).CLAVE := 'ETAPA';
                MI_MSGERROR (1).VALOR := MI_ETAPA;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE,
                            UN_TABLAERROR => MI_TABLA, 
                            UN_ERROR_COD  => PCK_ERRORES.ERR_EDICION_D_DEUDA,
                            UN_REEMPLAZOS  => MI_MSGERROR);  

            END;
					END LOOP DEUDAFACTURADA;			
				END IF;
			END;	
		ELSE
			MI_ETAPA := '6';
			BEGIN
        BEGIN 
          MI_TABLA := 'SP_D_DEUDAFACTURADAFINANCIADA';
          MI_CONDICION := '    COMPANIA   = '''||UN_COMPANIA||''' 
                           AND CICLO      = '''||UN_CICLO||''' 
                           AND CODIGORUTA = '''||UN_CODIGORUTA||''' 
                           AND ANO        = '||UN_ANO|| ' 
                           AND PERIODO    = '''||UN_PERIODO||'''';

          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA 	 => MI_TABLA,
                                                UN_ACCION 	 => 'E',
                                                UN_CONDICION => MI_CONDICION);

      	EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
          MI_MSGERROR (1).CLAVE := 'ETAPA';
          MI_MSGERROR (1).VALOR := MI_ETAPA;
          PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE
                     ,UN_TABLAERROR => MI_TABLA 
                     ,UN_ERROR_COD  => PCK_ERRORES.ERR_ELIMINAR_D_DEUDA 
                     ,UN_REEMPLAZOS  => MI_MSGERROR);  
      END;
			MI_ETAPA := '7';
			BEGIN
        BEGIN    
          MI_TABLA := 'SP_D_DEUDAFACTURADAFINANCIADA';
          MI_CAMPOS := 'COMPANIA,CICLO,CODIGORUTA,ANO,PERIODO,CONCEPTO,VALOR_FACTURADO
                        ,DEUDA,VALOR_FACTURADOANT,DEUDAANT,VALOR_FACTURADOIN,DEUDAIN
                        ,RECAUDADOPERIODO,DOBLEPAGO,SALDOCREDITO,ABONO,CREDITOABONADO
                        ,VALORFINACT,VALORFINANT,VALORFINDEUDA,VALORFINPERIODO
                        ,CUOTASSALDO,SALDOFINACT,SALDOFINANT,CREATED_BY,DATE_CREATED';
          MI_VALORES := 'SELECT SP_FACTURADO.COMPANIA, 
                                SP_FACTURADO.CICLO, 
                                SP_FACTURADO.CODIGORUTA,
                                SP_FACTURADO.ANO, 
                                SP_FACTURADO.PERIODO, 
                                SP_FACTURADO.CONCEPTO, 
                                SP_FACTURADO.VALOR_FACTURADO - SP_FACTURADO.VALORABONOACT,
                                SP_FACTURADO.DEUDA - SP_FACTURADO.VALORABONOANT,0,0,0,0,0,0,0,0,0,
                                PCK_SYSMAN_UTL.FC_ROUND(((SP_FACTURADO.VALOR_FACTURADO - SP_FACTURADO.VALORABONOACT)* '||UN_VALORABONO||' - '||MI_TOTALDEUDA||')/ '||MI_TOTALFACT||',0), 
                                SP_FACTURADO.DEUDA - SP_FACTURADO.VALORABONOANT, 
                                SP_FACTURADO.DEUDA -SP_FACTURADO.VALORABONOANT, 
                                SP_FACTURADO.VALOR_FACTURADO - SP_FACTURADO.VALORABONOACT,
                                '||UN_NROCUOTAS||', 0,0, '''||UN_USUARIO||''',SYSDATE
                        FROM SP_FACTURADO
                        WHERE SP_FACTURADO.COMPANIA     = '''||UN_COMPANIA||''' 
                          AND SP_FACTURADO.CICLO        = '''||UN_CICLO||'''
                          AND SP_FACTURADO.CODIGORUTA   = '''||UN_CODIGORUTA||''' 
                          AND SP_FACTURADO.ANO          = '||UN_ANO||' 
                          AND SP_FACTURADO.PERIODO      = '''||UN_PERIODO||''' 
                          AND (SP_FACTURADO.CONCEPTO BETWEEN 1 AND 48 
                            OR SP_FACTURADO.CONCEPTO IN (201,202,203,204,205,206,207,246,247,248,249))';
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA => MI_TABLA,
                                                  UN_ACCION => 'IS',
                                                  UN_CAMPOS => MI_CAMPOS,
                                                  UN_VALORES => MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
          MI_MSGERROR (1).CLAVE := 'ETAPA';
          MI_MSGERROR (1).VALOR := MI_ETAPA;
          PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD     => SQLCODE,
                      UN_TABLAERROR  => MI_TABLA,
                      UN_ERROR_COD   => PCK_ERRORES.ERR_INSERCION_D_DEUDA,
                      UN_REEMPLAZOS  => MI_MSGERROR);  
      END;
			MI_ETAPA := '8';
			MI_STRSQL := 'SELECT  SP_D_DEUDAFACTURADAFINANCIADA.COMPANIA, 
                            SP_D_DEUDAFACTURADAFINANCIADA.CICLO, 
                            SP_D_DEUDAFACTURADAFINANCIADA.CODIGORUTA, 
                            SP_D_DEUDAFACTURADAFINANCIADA.ANO, 
                            SP_D_DEUDAFACTURADAFINANCIADA.PERIODO, 
                            SUM(SP_D_DEUDAFACTURADAFINANCIADA.VALORFINACT) SUMAFINACT, 
                            SUM(SP_D_DEUDAFACTURADAFINANCIADA.VALORFINANT) SUMAFINANT 
                    FROM SP_D_DEUDAFACTURADAFINANCIADA
                    WHERE SP_D_DEUDAFACTURADAFINANCIADA.COMPANIA 		= '''||UN_COMPANIA||''' 
                      AND SP_D_DEUDAFACTURADAFINANCIADA.CICLO			  = '''||UN_CICLO||''' 
                      AND SP_D_DEUDAFACTURADAFINANCIADA.CODIGORUTA	= '''||UN_CODIGORUTA||''' 
                      AND SP_D_DEUDAFACTURADAFINANCIADA.ANO 			  =   '||UN_ANO||'
                      AND SP_D_DEUDAFACTURADAFINANCIADA.PERIODO		  = '''||UN_PERIODO||''' 
                      AND (SP_D_DEUDAFACTURADAFINANCIADA.CONCEPTO BETWEEN 1 AND 48 
                        OR SP_D_DEUDAFACTURADAFINANCIADA.CONCEPTO IN (201,202,203,204,205,206,207,246,247,248,249))
                    GROUP BY  SP_D_DEUDAFACTURADAFINANCIADA.COMPANIA, 
                              SP_D_DEUDAFACTURADAFINANCIADA.CICLO, 
                              SP_D_DEUDAFACTURADAFINANCIADA.CODIGORUTA, 
                              SP_D_DEUDAFACTURADAFINANCIADA.ANO,
                              SP_D_DEUDAFACTURADAFINANCIADA.PERIODO';
				EXECUTE IMMEDIATE MI_STRSQL INTO MI_COMPANIA,MI_CICLO,MI_CODIGORUTA,MI_ANO,MI_PERIODO,MI_SUMAFINACT,MI_SUMAFINANT; 	
				IF MI_SUMAFINACT <> (UN_VALORABONO - MI_TOTALDEUDA) THEN 
				MI_ETAPA := '9';
					<<DEUDAFACTURADAFINANCIADA>>
					FOR RS_CON IN(	SELECT
                            SP_D_DEUDAFACTURADAFINANCIADA.CONCEPTO,
                            SP_D_DEUDAFACTURADAFINANCIADA.VALORFINACT
                          FROM SP_D_DEUDAFACTURADAFINANCIADA 
                          WHERE SP_D_DEUDAFACTURADAFINANCIADA.COMPANIA 		= UN_COMPANIA 
                            AND SP_D_DEUDAFACTURADAFINANCIADA.CICLO 		  = UN_CICLO
                            AND SP_D_DEUDAFACTURADAFINANCIADA.CODIGORUTA 	= UN_CODIGORUTA 
                            AND SP_D_DEUDAFACTURADAFINANCIADA.ANO 			  = UN_ANO 
                            AND SP_D_DEUDAFACTURADAFINANCIADA.PERIODO 		= UN_PERIODO 
                            AND (SP_D_DEUDAFACTURADAFINANCIADA.CONCEPTO BETWEEN 1 AND 48 
                              OR SP_D_DEUDAFACTURADAFINANCIADA.CONCEPTO IN (201,202,203,204,205,206,207,246,247,248,249)))

					LOOP
						MI_ETAPA := '10';
						MI_TABLA := 'SP_D_DEUDAFACTURADAFINANCIADA';

						MI_CAMPOS := 'VALORFINACT   = '||(NVL(RS_CON.VALORFINACT,0)+(UN_VALORABONO-(MI_TOTALDEUDA))-MI_SUMAFINACT)||'
                         ,MODIFIED_BY   = '''||UN_USUARIO||'''
                         ,DATE_MODIFIED = SYSDATE';
						MI_CONDICION := '    COMPANIA   = '''||UN_COMPANIA||''' 
                             AND CICLO      = '''||UN_CICLO||''' 
                             AND CODIGORUTA = '''||UN_CODIGORUTA||''' 
                             AND ANO        = '||UN_ANO||'
                             AND PERIODO    = '''||UN_PERIODO||''' 
                             AND CONCEPTO   = '||RS_CON.CONCEPTO||'';
						BEGIN 
              BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA      => MI_TABLA,
                                                      UN_ACCION     => 'M',
                                                      UN_CAMPOS     => MI_CAMPOS,
                                                      UN_CONDICION  => MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;	
              END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
                MI_MSGERROR (1).CLAVE := 'ETAPA';
                MI_MSGERROR (1).VALOR := MI_ETAPA;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE,
                            UN_TABLAERROR => MI_TABLA,
                            UN_ERROR_COD  => PCK_ERRORES.ERR_EDICION_D_DEUDA,
                            UN_REEMPLAZOS  => MI_MSGERROR);  
            END;  
          END LOOP DEUDAFACTURADAFINANCIADA;
				END IF;	
		END IF;
		MI_ETAPA := '11';
		BEGIN 
      BEGIN
        MI_TABLA := 'SP_D_DEUDAFACTURADAFINANCIADA';
        MI_CAMPOS := ' SALDOFINACT   = (VALOR_FACTURADO - VALORFINACT)
                     , SALDOFINANT   = (DEUDA - SP_D_DEUDAFACTURADAFINANCIADA.VALORFINANT)
                     , MODIFIED_BY   = '''||UN_USUARIO||'''
                     , DATE_MODIFIED = SYSDATE';
        MI_CONDICION := '    COMPANIA   = '''||UN_COMPANIA||''' 
                         AND CICLO      = '''||UN_CICLO||''' 
                         AND CODIGORUTA = '''||UN_CODIGORUTA||''' 
                         AND ANO        = '||UN_ANO||' 
                         AND PERIODO    = '''||UN_PERIODO||'''';
		    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA      => MI_TABLA,
                                                UN_ACCION     => 'M',
                                                UN_CAMPOS     => MI_CAMPOS,
                                                UN_CONDICION  => MI_CONDICION);
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;	
      END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
        MI_MSGERROR (1).CLAVE := 'ETAPA';
        MI_MSGERROR (1).VALOR := MI_ETAPA;
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE,
                    UN_TABLAERROR => MI_TABLA,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_EDICION_D_DEUDA,
                    UN_REEMPLAZOS  => MI_MSGERROR);  
    END;
	EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
		  PCK_ERR_MSG.RAISE_WITH_MSG (
                  UN_EXC_COD    => SQLCODE
                 ,UN_ERROR_COD  => PCK_ERRORES.ERR_DISCRIMINARFINANCIACION
                 ,UN_TABLAERROR => 'SP_D_DEUDAFACTURADAFINANCIADA');			
END PR_DISCRIMINARFINANCIACION;

--13
FUNCTION FC_ACTUALIZAFINANCIABLEDEUDA
(
  /*
      NAME              : FC_REGISTRAFINANCIABLEDEUDA  --> EN ACCESS Evento actualizar despues del formulario FinanciabledeDeuda
      AUTHORS           : SYSMAN  
      AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
      DATE MIGRADOR     : 07/02/2017
      TIME              : 11:34 AM
      SOURCE MODULE     : SysmanSp2016.05.04
      MODIFIER          : JAVIER ANDRES RODRIGUEZ RIOS - JAVIER ANDRES RODRIGUEZ RIOS
      DATE MODIFIED     : 23/05/2017 - 07/07/2017
      TIME              :    
      DESCRIPTION       : 
      MODIFICATIONS     : AGREGAR EL PARAMETROS UN_USUSARIO PARA LLEVAR LA AUDITORIA DE LAS OPERACIONES DML REALIZADAS EN LA FUNCION.
                          SE AJUSTA PARA QUE SE DESACOPLE TOTALMENTE DEL CONTROLADOR YA QUE SE ENCUENTRAN TEXTOS EN BEAN DEFINIDOS EN EL 
                          RETORNO DE LA FUNCION.
      PARAMETERS        :     UN_COMPANIA: Código de la compañia.
                              UN_CICLO: Número de ciclo del usuario.
                              UN_CODIGORUTA: Código de ruta del usuario. 
                              UN_ANO: Número de año del ciclo.
                              UN_PERIODO: Número de período del ciclo. 
                              UN_VALORABONO: Valor del Abono Inicial del Usuario.
                              UN_VRAFINANCIAR: Valor a Financiar de la Deuda
                              UN_NROCUOTAS: Número de cuotas en el que el Usuario pagara su deuda.	
                              UN_USUARIO: Usuario que registra el financiable.
                              UN_SINABONOIN: Toma el valor de falso cuando el valor del abono inicial es diferente de 0
                              UN_PERNOCOBRO: Valor del periodo de cobro no financiable
                              UN_FACTURAPERACTUAL : Valor de la factura actual del usuario 
                              UN_CONSECUTIVO: Valor del consecutivo con el que se va a generar el financiable de deuda

      @NAME:  actualizaFinanciabledeDeuda
      @METHOD:  PUT 
    */ 
    UN_COMPANIA 	   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CICLO         IN PCK_SUBTIPOS.TI_CICLO,
    UN_CODIGORUTA    IN PCK_SUBTIPOS.TI_CODIGORUTA,
    UN_ANO      	   IN PCK_SUBTIPOS.TI_ANIO,
    UN_PERIODO  	   IN PCK_SUBTIPOS.TI_PERIODO,
    UN_VALORABONO 	 IN PCK_SUBTIPOS.TI_DOBLE,
    UN_VRAFINANCIAR  IN PCK_SUBTIPOS.TI_DOBLE,
    UN_NROCUOTAS 	   IN PCK_SUBTIPOS.TI_ENTERO,
    UN_USUARIO       IN PCK_SUBTIPOS.TI_USUARIO,
    UN_SINABONOIN    IN PCK_SUBTIPOS.TI_LOGICO,
    UN_PERNOCOBRO    IN PCK_SUBTIPOS.TI_ENTERO,
    UN_CONSECUTIVO   IN PCK_SUBTIPOS.TI_ENTERO_LARGO
)
RETURN PCK_SUBTIPOS.TI_LOGICO
AS 
    MI_ETAPA 		          VARCHAR(2 CHAR);
    MI_MSJ 					      VARCHAR2(100 CHAR);
    MI_EXISTE 				    PCK_SUBTIPOS.TI_LOGICO;
    MI_ANOSTE 				    PCK_SUBTIPOS.TI_ANIO;
    MI_PERIODOSTE 			  PCK_SUBTIPOS.TI_PERIODO;
    MI_NOMBREPER    		  VARCHAR2(20 CHAR);
    MI_STRSQL 				    PCK_SUBTIPOS.TI_STRSQL;
    MI_PERIODOSNOCOBROFAC PCK_SUBTIPOS.TI_DOBLE;
    MI_PERIODOSATRASO 		PCK_SUBTIPOS.TI_ENTERO;
    MI_RTA                VARCHAR2(1 CHAR);
    MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION			    PCK_SUBTIPOS.TI_CONDICION;
    MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
    MI_MERGEUSING         PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE        PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE        PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_SALDOFINANCIABLE   PCK_SUBTIPOS.TI_DOBLE;
    MI_VALORCUOTA         PCK_SUBTIPOS.TI_DOBLE;
    MI_MONTO              PCK_SUBTIPOS.TI_DOBLE;
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
    MI_ANOSTE     := PCK_SERVICIOS_PUBLICOS.FC_ANO_PERIODO_SIGUIENTE(UN_COMPANIA,UN_ANO,UN_PERIODO,0,NULL);
    MI_PERIODOSTE := PCK_SERVICIOS_PUBLICOS.FC_ANO_PERIODO_SIGUIENTE(UN_COMPANIA,UN_ANO,UN_PERIODO,1,NULL);
    MI_EXISTE     := PCK_SERVICIOS_PUBLICOS_COM3.FC_EXISTEPERIODO(UN_COMPANIA,MI_ANOSTE,MI_PERIODOSTE);
    IF MI_EXISTE IN (0) THEN 
        BEGIN  
            MI_MSGERROR(0).CLAVE:='NOMBREPERIODO';
            MI_MSGERROR(0).VALOR:=PCK_SERVICIOS_PUBLICOS_COM2.FC_NOMBREPERIODOSIGDE(
                                                              UN_COMPANIA   => UN_COMPANIA
                                                             ,UN_ANO        => MI_ANOSTE
                                                             ,UN_PERIODO    => MI_PERIODOSTE
                                                             ,UN_FRECUENCIA => NULL);
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE,
                        UN_TABLAERROR => MI_TABLA,
                        UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_TIENE_FINAN_DEUDA2,
                        UN_REEMPLAZOS => MI_MSGERROR);

        END;
    END IF;
    BEGIN
        MI_STRSQL := 'SELECT  SP_USUARIO.PERIODOSNOCOBROFAC,
                              SP_USUARIO.PERIODOSATRASO
                      FROM SP_USUARIO
                      WHERE SP_USUARIO.COMPANIA  		='''||UN_COMPANIA||''' 
                        AND SP_USUARIO.CICLO 		    = '''||UN_CICLO||''' 
                        AND SP_USUARIO.CODIGORUTA 	= '''||UN_CODIGORUTA||'''';
        EXECUTE IMMEDIATE MI_STRSQL INTO MI_PERIODOSNOCOBROFAC , MI_PERIODOSATRASO;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_PERIODOSNOCOBROFAC:=NULL;
        MI_PERIODOSATRASO    :=NULL;
    END;
    BEGIN
        SELECT 'X' EXISTE
          INTO MI_EXISTE
          FROM SP_FINANCIABLESDEDEUDA
          WHERE SP_FINANCIABLESDEDEUDA.COMPANIA = UN_COMPANIA
            AND SP_FINANCIABLESDEDEUDA.USUARIO  = UN_CODIGORUTA
            AND SP_FINANCIABLESDEDEUDA.ANO      = UN_ANO
            AND SP_FINANCIABLESDEDEUDA.PERIODO  = UN_PERIODO
            AND SP_FINANCIABLESDEDEUDA.INDANULADO  IN(0);	
    EXCEPTION WHEN NO_DATA_FOUND THEN  
        MI_EXISTE:=NULL;
    END;
    IF MI_EXISTE IS NOT NULL THEN 
        BEGIN 
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
        	  PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE,
                        UN_TABLAERROR => MI_TABLA,
                        UN_ERROR_COD  => PCK_ERRORES.ERR_FACSERP_TIENE_FINAN_DEUDA1,
                        UN_REEMPLAZOS => MI_MSGERROR);	 
        END;
    END IF;
    IF MI_EXISTE IS NULL THEN 
        PCK_SERVICIOS_PUBLICOS_COM3.PR_DISCRIMINARFINANCIACION(
                                    UN_COMPANIA       => UN_COMPANIA,
                                    UN_CICLO          => UN_CICLO,
                                    UN_CODIGORUTA     => UN_CODIGORUTA,
                                    UN_ANO            => UN_ANO,
                                    UN_PERIODO        => UN_PERIODO,
                                    UN_VALORABONO     => UN_VALORABONO, 
                                    UN_NROCUOTAS      => UN_NROCUOTAS,
                                    UN_VRAFINANCIAR   => UN_VRAFINANCIAR,
                                    UN_USUARIO        => UN_USUARIO,
                                    UN_CONSECUTIVO    => UN_CONSECUTIVO);
        MI_ETAPA := '1';
        MI_TABLA := 'SP_MODIFICACIONESDEUDA';
        MI_CAMPOS := ' COMPANIA
                      ,CICLO
                      ,CODIGORUTA
                      ,CONCEPTO
                      ,FECHA
                      ,HORA
                      ,USUARIO
                      ,VRANT
                      ,VRNUE
                      ,TIPOMODIFICACION
                      ,CAUSAMODIFICACION
                      ,ANO
                      ,PERIODO
                      ,CREATED_BY
                      ,DATE_CREATED';
        BEGIN 
            BEGIN 
                MI_VALORES := '	SELECT 	SP_FACTURADO.COMPANIA,
                                        SP_FACTURADO.CICLO,
                                        SP_FACTURADO.CODIGORUTA,
                                        SP_FACTURADO.CONCEPTO,
                                        SYSDATE  NFECHA,
                                        SYSDATE  NHORA,
                                        '''||UN_USUARIO||'''  NUSUARIO,
                                        SP_FACTURADO.DEUDA,0,1,2,
                                        SP_FACTURADO.ANO,
                                        SP_FACTURADO.PERIODO,
                                        '''||UN_USUARIO||''',
                                        SYSDATE
                                FROM  SP_FACTURADO
                                WHERE SP_FACTURADO.COMPANIA   ='''||UN_COMPANIA||''' 
                                  AND SP_FACTURADO.CICLO      ='''||UN_CICLO||''' 
                                  AND SP_FACTURADO.CODIGORUTA ='''||UN_CODIGORUTA||''' 
                                  AND SP_FACTURADO.ANO        = '||UN_ANO||' 
                                  AND SP_FACTURADO.PERIODO    ='''||UN_PERIODO||'''';

                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA    => MI_TABLA,
                                                        UN_ACCION   => 'IS',
                                                        UN_CAMPOS   => MI_CAMPOS,
                                                        UN_VALORES  => MI_VALORES);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
            MI_MSGERROR (0).CLAVE := 'ETAPA';
            MI_MSGERROR (0).VALOR := MI_ETAPA;
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE,
                        UN_TABLAERROR => MI_TABLA,
                        UN_ERROR_COD  => PCK_ERRORES.ERR_INSERMODIDEUDA,
                        UN_REEMPLAZOS => MI_MSGERROR);  
        END;  
        MI_ETAPA := '2';  
        BEGIN 	
            BEGIN 
                MI_VALORES := '	SELECT 	SP_FACTURADO.COMPANIA,
                                        SP_FACTURADO.CICLO,
                                        SP_FACTURADO.CODIGORUTA,
                                        SP_FACTURADO.CONCEPTO,
                                        SYSDATE NFECHA,
                                        SYSDATE + 5/86400 NHORA,
                                        '''||UN_USUARIO||''' NUSUARIO,
                                        SP_FACTURADO.VALOR_FACTURADO,0,2,2,
                                        SP_FACTURADO.ANO,
                                        SP_FACTURADO.PERIODO,
                                        '''||UN_USUARIO||''',
                                        SYSDATE 
                                FROM SP_FACTURADO 
                                WHERE SP_FACTURADO.COMPANIA   ='''||UN_COMPANIA||''' 
                                  AND SP_FACTURADO.CICLO      ='''||UN_CICLO||''' 
                                  AND SP_FACTURADO.CODIGORUTA ='''||UN_CODIGORUTA||''' 
                                  AND SP_FACTURADO.ANO        = '||UN_ANO||' 
                                  AND SP_FACTURADO.PERIODO    ='''||UN_PERIODO||'''';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA    => MI_TABLA,
                                                        UN_ACCION   => 'IS',
                                                        UN_CAMPOS   => MI_CAMPOS,
                                                        UN_VALORES  => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
            MI_MSGERROR (0).CLAVE := 'ETAPA';
            MI_MSGERROR (0).VALOR := MI_ETAPA;
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE,
                        UN_TABLAERROR => MI_TABLA,
                        UN_ERROR_COD  => PCK_ERRORES.ERR_INSERMODIDEUDA,
                        UN_REEMPLAZOS => MI_MSGERROR);  
        END;    
        MI_ETAPA := '3';
        MI_TABLA := 'SP_FACTURADO';		
        MI_CONDICION := '    COMPANIA   = '''||UN_COMPANIA||''' 
                         AND CICLO      = '''||UN_CICLO||''' 
                         AND CODIGORUTA = '''||UN_CODIGORUTA||''' 
                         AND ANO        = '||UN_ANO|| '
                         AND PERIODO    = '''||UN_PERIODO||'''';				
        BEGIN
            BEGIN 
                MI_CAMPOS := '  VALOR_FACTURADOIN = VALOR_FACTURADO 
                               ,DEUDAIN           = DEUDA
                               ,MODIFIED_BY       = '''||UN_USUARIO||'''
                               ,DATE_MODIFIED     = SYSDATE';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA 	   => MI_TABLA,
                                                        UN_ACCION 	 => 'M',
                                                        UN_CAMPOS 	 => MI_CAMPOS,
                                                        UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION;											
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
            MI_MSGERROR (0).CLAVE := 'ETAPA';
            MI_MSGERROR (0).VALOR := MI_ETAPA;
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE,
                        UN_TABLAERROR => MI_TABLA,
                        UN_ERROR_COD  => PCK_ERRORES.ERR_EDIFACTURADO,
                        UN_REEMPLAZOS => MI_MSGERROR);  
        END;    
        MI_ETAPA := '4';
        BEGIN  
            BEGIN
                MI_CAMPOS := '  VALOR_FACTURADO = 0 
                              , DEUDA           = 0 
                              , VALORABONOACT   = 0 
                              , VALORABONOANT   = 0
                              , MODIFIED_BY     = '''||UN_USUARIO||'''
                              , DATE_MODIFIED   = SYSDATE';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA 	    => MI_TABLA,
                                                        UN_ACCION 	  => 'M',
                                                        UN_CAMPOS 	  => MI_CAMPOS,
                                                        UN_CONDICION  => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION;	
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
            MI_MSGERROR (0).CLAVE := 'ETAPA';
            MI_MSGERROR (0).VALOR := MI_ETAPA;
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE,
                        UN_TABLAERROR => MI_TABLA,
                        UN_ERROR_COD  => PCK_ERRORES.ERR_EDIFACTURADO,
                        UN_REEMPLAZOS  => MI_MSGERROR);  
        END;   
        MI_ETAPA := '5';
        BEGIN
            BEGIN
                MI_MERGEUSING := '  SELECT 	SP_D_DEUDAFACTURADAFINANCIADA.COMPANIA,
                                            SP_D_DEUDAFACTURADAFINANCIADA.CICLO,
                                            SP_D_DEUDAFACTURADAFINANCIADA.CODIGORUTA,
                                            SP_D_DEUDAFACTURADAFINANCIADA.ANO,
                                            SP_D_DEUDAFACTURADAFINANCIADA.PERIODO,
                                            SP_D_DEUDAFACTURADAFINANCIADA.CONCEPTO,
                                            SP_D_DEUDAFACTURADAFINANCIADA.VALORFINANT,
                                            SP_D_DEUDAFACTURADAFINANCIADA.VALORFINACT
                                    FROM SP_FACTURADO 
                                      INNER JOIN SP_D_DEUDAFACTURADAFINANCIADA 
                                        ON SP_FACTURADO.COMPANIA    = SP_D_DEUDAFACTURADAFINANCIADA.COMPANIA
                                        AND SP_FACTURADO.CICLO      = SP_D_DEUDAFACTURADAFINANCIADA.CICLO
                                        AND SP_FACTURADO.CODIGORUTA = SP_D_DEUDAFACTURADAFINANCIADA.CODIGORUTA 
                                        AND SP_FACTURADO.ANO        = SP_D_DEUDAFACTURADAFINANCIADA.ANO 
                                        AND SP_FACTURADO.PERIODO    = SP_D_DEUDAFACTURADAFINANCIADA.PERIODO 
                                        AND SP_FACTURADO.CONCEPTO   = SP_D_DEUDAFACTURADAFINANCIADA.CONCEPTO 
                                    WHERE  SP_FACTURADO.COMPANIA  ='''||UN_COMPANIA||''' 
                                      AND SP_FACTURADO.CICLO      ='''||UN_CICLO||''' 
                                      AND SP_FACTURADO.CODIGORUTA ='''||UN_CODIGORUTA||''' 
                                      AND SP_FACTURADO.ANO        ='||UN_ANO||' 
                                      AND SP_FACTURADO.PERIODO    ='''||UN_PERIODO||'''';
                MI_MERGEENLACE := ' TABLA.COMPANIA 		= VISTA.COMPANIA
                                AND TABLA.CICLO 		  = VISTA.CICLO
                                AND TABLA.CODIGORUTA 	= VISTA.CODIGORUTA
                                AND TABLA.ANO 			  = VISTA.ANO
                                AND TABLA.PERIODO 		= VISTA.PERIODO
                                AND TABLA.CONCEPTO 		= VISTA.CONCEPTO';
                MI_MERGEEXISTE := 'UPDATE SET 
                                      TABLA.VALORFINANT   = VISTA.VALORFINANT,
                                      TABLA.VALORFINACT   = VISTA.VALORFINACT,
                                      TABLA.MODIFIED_BY   = '''||UN_USUARIO||''',
                                      TABLA.DATE_MODIFIED = SYSDATE ';

                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA 		    => MI_TABLA,
                                                        UN_ACCION 		  => 'MM',
                                                        UN_MERGEUSING 	=> MI_MERGEUSING,
                                                        UN_MERGEENLACE 	=> MI_MERGEENLACE,
                                                        UN_MERGEEXISTE 	=> MI_MERGEEXISTE);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;					
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
            MI_MSGERROR (0).CLAVE := 'ETAPA';
            MI_MSGERROR (0).VALOR := MI_ETAPA;
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE,
                        UN_TABLAERROR => MI_TABLA,
                        UN_ERROR_COD  => PCK_ERRORES.ERR_EDIFACTURADO,
                        UN_REEMPLAZOS => MI_MSGERROR);  
        END;    
        MI_SALDOFINANCIABLE := PCK_SYSMAN_UTL.FC_ROUND((UN_VRAFINANCIAR-UN_VALORABONO),0);
        IF UN_SINABONOIN <> 0 THEN 
            MI_VALORCUOTA := PCK_SYSMAN_UTL.FC_ROUND((UN_VRAFINANCIAR/UN_NROCUOTAS),0);
            MI_MONTO := 0;
        ELSE
            MI_VALORCUOTA := PCK_SYSMAN_UTL.FC_ROUND((MI_SALDOFINANCIABLE/UN_NROCUOTAS),0);
            MI_MONTO := UN_VALORABONO;
        END IF;	
        MI_ETAPA := '6';
        MI_TABLA := 'SP_FINANCIABLES';
        BEGIN
            BEGIN
                MI_CAMPOS := 'COMPANIA
                             ,CICLO
                             ,CODIGORUTA
                             ,ANO
                             ,PERIODO
                             ,CONCEPTO
                             ,MONTOFINANCIAR
                             ,NUMEROCUOTAS
                             ,SALDOFINANCIABLE
                             ,VALORCUOTA
                             ,ANOINICIAL
                             ,PERIODOINICIAL
                             ,HORA
                             ,CREATED_BY
                             ,DATE_CREATED';
                MI_VALORES := ''''||UN_COMPANIA||'''
                              ,'''||UN_CICLO||'''
                              ,'''||UN_CODIGORUTA||'''
                              ,'||MI_ANOSTE||'
                              ,'''||MI_PERIODOSTE||'''
                              ,12
                              ,('||UN_VRAFINANCIAR||'- '||MI_MONTO||')
                              , '||UN_NROCUOTAS||'
                              ,('||UN_VRAFINANCIAR||'-'||UN_VALORABONO||')
                              ,('||UN_VRAFINANCIAR||'-'||MI_MONTO||')/'||UN_NROCUOTAS||'
                              ,'||UN_ANO||'
                              ,'''||UN_PERIODO||'''
                              ,SYSDATE
                              ,'''||UN_USUARIO||'''
                              ,SYSDATE';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA    => MI_TABLA,
                                                        UN_ACCION   => 'I',
                                                        UN_CAMPOS   => MI_CAMPOS,
                                                        UN_VALORES  => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;													
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
            MI_MSGERROR (0).CLAVE := 'ETAPA';
            MI_MSGERROR (0).VALOR := MI_ETAPA;
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE,
                        UN_TABLAERROR => MI_TABLA,
                        UN_ERROR_COD  => PCK_ERRORES.ERR_INSERFINANCIABLE,
                        UN_REEMPLAZOS  => MI_MSGERROR);  
        END;   
        MI_ETAPA := '7';
        BEGIN
            BEGIN
                MI_TABLA := 'SP_FINANCIABLES';
                MI_CONDICION := '    COMPANIA     = '''||UN_COMPANIA||''' 
                                 AND CICLO        = '''||UN_CICLO||''' 
                                 AND CODIGORUTA   = '''||UN_CODIGORUTA||''' 
                                 AND ANO          = '||UN_ANO||'
                                 AND PERIODO      = '''||UN_PERIODO||'''
                                 AND NUMEROCUOTAS = 1';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (	UN_TABLA     => MI_TABLA,
                                                        UN_ACCION    => 'E',
                                                        UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;										
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
            MI_MSGERROR (0).CLAVE := 'ETAPA';
            MI_MSGERROR (0).VALOR := MI_ETAPA;
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE,
                        UN_TABLAERROR => MI_TABLA,
                        UN_ERROR_COD  => PCK_ERRORES.ERR_ELIFINANCIABLE,
                        UN_REEMPLAZOS  => MI_MSGERROR);  
        END;  
        MI_ETAPA := '8';
        MI_TABLA := 'SP_FACTURADO';
        BEGIN
            MI_STRSQL := '	SELECT ''X'' EXISTE
                            FROM SP_FACTURADO
                            WHERE   SP_FACTURADO.COMPANIA   ='''||UN_COMPANIA||''' 
                              AND   SP_FACTURADO.CICLO      ='''||UN_CICLO||''' 
                              AND   SP_FACTURADO.CODIGORUTA ='''||UN_CODIGORUTA||''' 
                              AND   SP_FACTURADO.ANO        ='||UN_ANO||'  
                              AND   SP_FACTURADO.PERIODO    ='''||UN_PERIODO||''' 
                              AND   SP_FACTURADO.CONCEPTO   = 12';
            EXECUTE IMMEDIATE MI_STRSQL INTO MI_RTA;
        EXCEPTION WHEN NO_DATA_FOUND THEN 
            BEGIN
                BEGIN
                    MI_CAMPOS := ' COMPANIA
                                  ,CICLO
                                  ,CODIGORUTA
                                  ,ANO
                                  ,PERIODO
                                  ,CONCEPTO
                                  ,VALOR_FACTURADO
                                  ,DEUDA
                                  ,VALORFINACT
                                  ,VALORFINANT
                                  ,PORFINANCIACION
                                  ,VALORABONOACT
                                  ,VALORABONOANT
                                  ,CREATED_BY
                                  ,DATE_CREATED';
                    MI_VALORES := ''''||UN_COMPANIA||'''
                                  ,'''||UN_CICLO||'''
                                  ,'''||UN_CODIGORUTA||'''
                                  ,'||UN_ANO||'
                                  ,'''||UN_PERIODO||'''
                                  ,12
                                  ,'||UN_VALORABONO||'
                                  ,0,0,0,0,0,0
                                  ,'''||UN_USUARIO||'''
                                  ,SYSDATE ';
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA    => MI_TABLA,
                                                          UN_ACCION   => 'I',
                                                          UN_CAMPOS   => MI_CAMPOS,
                                                          UN_VALORES  => MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;										
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
                MI_MSGERROR (0).CLAVE := 'ETAPA';
                MI_MSGERROR (0).VALOR := MI_ETAPA;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE,
                            UN_TABLAERROR => MI_TABLA,
                            UN_ERROR_COD  => PCK_ERRORES.ERR_INSERFACTURADO,
                            UN_REEMPLAZOS => MI_MSGERROR);  

            END;   
        END;
        MI_ETAPA := '9';
        BEGIN 
            BEGIN
                MI_TABLA  := 'SP_FACTURADO';
                MI_CAMPOS := ' VALOR_FACTURADO = '||UN_VALORABONO||' 
                              ,DEUDA           = 0
                              ,MODIFIED_BY     = '''||UN_USUARIO||'''
                              ,DATE_MODIFIED   = SYSDATE';
                MI_CONDICION := '    COMPANIA   = '''||UN_COMPANIA||''' 
                                 AND CICLO      = '''||UN_CICLO||''' 
                                 AND CODIGORUTA = '''||UN_CODIGORUTA||''' 
                                 AND ANO        = '||UN_ANO||' 
                                 AND PERIODO    = '''||UN_PERIODO||''' 
                                 AND CONCEPTO   = 12';

                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA    => MI_TABLA,
                                                        UN_ACCION   => 'M',
                                                        UN_CAMPOS   => MI_CAMPOS,
                                                        UN_CONDICION  => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;													
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
            MI_MSGERROR (0).CLAVE := 'ETAPA';
            MI_MSGERROR (0).VALOR := MI_ETAPA;
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE,
                        UN_TABLAERROR => MI_TABLA,
                        UN_ERROR_COD  => PCK_ERRORES.ERR_EDIFACTURADO,
                        UN_REEMPLAZOS => MI_MSGERROR); 
        END;   
        MI_ETAPA := '10';
        BEGIN
            BEGIN
                MI_TABLA := 'SP_USUARIO';
                MI_CAMPOS := 'PERIODOSNOCOBROFAC  = '||MI_PERIODOSNOCOBROFAC||' + 1 
                             ,PERIODOSNOCOBROFIN  = '||UN_PERNOCOBRO||' 
                             ,INDFINANINICIAL     = -1 
                             ,TOTFACTURAPERACTUAL = '||UN_VALORABONO||' 
                             ,TOTFACTURAPAGO2     = '||UN_VALORABONO||'
                             ,MODIFIED_BY         = '''||UN_USUARIO||''' 
                             ,DATE_MODIFIED       = SYSDATE';
                MI_CONDICION := '    COMPANIA   = '''||UN_COMPANIA||''' 
                                 AND CICLO      = '''||UN_CICLO||''' 
                                 AND CODIGORUTA = '''||UN_CODIGORUTA||'''';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA      => MI_TABLA,
                                                        UN_ACCION     => 'M',
                                                        UN_CAMPOS     => MI_CAMPOS,
                                                        UN_CONDICION  => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
            MI_MSGERROR (1).CLAVE := 'ETAPA';
            MI_MSGERROR (1).VALOR := MI_ETAPA;
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE,
                        UN_TABLAERROR => MI_TABLA,
                        UN_ERROR_COD  => PCK_ERRORES.ERR_EDIUSUARIO,
                        UN_REEMPLAZOS => MI_MSGERROR);  
        END; 
        BEGIN
            IF PCK_SYSMAN_UTL.FC_PAR(	
                              UN_COMPANIA  => UN_COMPANIA,
                              UN_MODULO    => PCK_DATOS.FC_MODULOSERVICIOSPUBLICOS,
                              UN_NOMBRE    => 'RECONOCER ABONOS SOBRE ACUERDOS DE PAGO',
                              UN_FECHA_PAR => SYSDATE) = 'SI' THEN
                MI_ETAPA := '11';
                BEGIN
                    BEGIN
                        MI_TABLA := 'SP_FINANCIABLES';
                        MI_CAMPOS := 'PERIODOSATRASOINI = '||MI_PERIODOSATRASO||'
                                     ,MODIFIED_BY       = '''||UN_USUARIO||'''
                                     ,DATE_MODIFIED     = SYSDATE  ';
                        MI_CONDICION := '    COMPANIA   = '''||UN_COMPANIA||''' 
                                         AND CODIGORUTA = '''||UN_CODIGORUTA||''' 
                                         AND ANO        = '||MI_ANOSTE||'
                                         AND PERIODO    = '''||MI_PERIODOSTE||''' 
                                         AND CONCEPTO   = 12';
                        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA      => MI_TABLA,
                                                                  UN_ACCION     => 'M',
                                                                  UN_CAMPOS     => MI_CAMPOS,
                                                                  UN_CONDICION  => MI_CONDICION);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;										
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
                    MI_MSGERROR (1).CLAVE := 'ETAPA';
                    MI_MSGERROR (1).VALOR := MI_ETAPA;
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD    => SQLCODE,
                                UN_TABLAERROR => MI_TABLA,
                                UN_ERROR_COD  => PCK_ERRORES.ERR_EDIFINANCIABLE,
                                UN_REEMPLAZOS  => MI_MSGERROR);  
                END;   
                MI_ETAPA := '12';
                BEGIN
                     BEGIN
                        MI_TABLA := 'SP_USUARIO';
                        MI_CAMPOS := ' PERIODOSATRASO = 0
                                      ,MODIFIED_BY       = '''||UN_USUARIO||'''
                                      ,DATE_MODIFIED     = SYSDATE  ' ;
                        MI_CONDICION := '    COMPANIA   = '''||UN_COMPANIA||''' 
                                         AND CICLO      = '''||UN_CICLO||''' 
                                         AND CODIGORUTA = '''||UN_CODIGORUTA||''' ';
                        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA      => MI_TABLA,
                                                                UN_ACCION     => 'M',
                                                                UN_CAMPOS     => MI_CAMPOS,
                                                                UN_CONDICION  => MI_CONDICION);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                           RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
                    MI_MSGERROR (1).CLAVE := 'ETAPA';
                    MI_MSGERROR (1).VALOR := MI_ETAPA;
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                               UN_EXC_COD    => SQLCODE,
                               UN_TABLAERROR => MI_TABLA,
                               UN_ERROR_COD  => PCK_ERRORES.ERR_EDIFINANCIABLE,
                               UN_REEMPLAZOS => MI_MSGERROR);  
                END;   
            ELSE
                MI_ETAPA := '13';
                BEGIN
                    BEGIN
                        MI_TABLA := 'SP_USUARIO';
                        MI_CAMPOS := ' PERIODOSATRASO = '||(MI_PERIODOSATRASO-(MI_PERIODOSATRASO*UN_VALORABONO/UN_VRAFINANCIAR))||'
                                      ,MODIFIED_BY    = '''||UN_USUARIO||'''
                                      ,DATE_MODIFIED  = SYSDATE  ' ;
                        MI_CONDICION := '    COMPANIA   = '''||UN_COMPANIA||''' 
                                         AND CICLO      = '''||UN_CICLO||''' 
                                         AND CODIGORUTA = '''||UN_CODIGORUTA||''' ';
                        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA      => MI_TABLA,
                                                                UN_ACCION     => 'M',
                                                                UN_CAMPOS     => MI_CAMPOS,
                                                                UN_CONDICION  => MI_CONDICION);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                        RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
                    MI_MSGERROR (0).CLAVE := 'ETAPA';
                    MI_MSGERROR (0).VALOR := MI_ETAPA;
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD    => SQLCODE,
                                UN_TABLAERROR => MI_TABLA,
                                UN_ERROR_COD  => PCK_ERRORES.ERR_EDIFINANCIABLE,
                                UN_REEMPLAZOS => MI_MSGERROR);   
                END;   
            END IF;					
        END;
    END IF;
        --'Ya puede imprimir la factura';

    RETURN -1;	
END FC_ACTUALIZAFINANCIABLEDEUDA;	

END PCK_SERVICIOS_PUBLICOS_COM3;