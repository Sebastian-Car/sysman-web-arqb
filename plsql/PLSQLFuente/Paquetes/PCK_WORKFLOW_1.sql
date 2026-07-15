create or replace PACKAGE BODY PCK_WORKFLOW AS
  
  --1
  PROCEDURE PR_CERRAR_TRAMITE 
  /*
    NAME              : PR_CERRAR_TRAMITE
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR CREATION   : PABLO ANDRES ESPITIA CUCA
    DATE CREATION     : 16/05/2018
    TIME              : 14:58
    SOURCE MODULE     : WORKFLOW (35)
    DESCRIPTION       : Cierra el tramite. 
    MODIFIED BY       : 

    @NAME  : cerrarTramite
    @METHOD: POST
  */
  (
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,                                -- Codigo de la compania.
    UN_PROCESO         IN TRAMITES.PROCESOS%TYPE,                                  -- Codigo del proceso.
    UN_TIPO_TRAMITE    IN TRAMITES.TIPO_TRAMITE%TYPE,                              -- Codigo del tipo de tramite.
    UN_TRAMITE         IN TRAMITES.NUMERO%TYPE,                                    -- Numero del tramite. 
    UN_NODO_ACTUAL     IN NODOS.CODIGO%TYPE,                                       -- Codigo del nodo actual.
    UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO                                  -- Codigo del usuario que desencadena el proceso.
  )
  AS 
    MI_DETALLE     PCK_SUBTIPOS.TI_ENTERO;
    MI_TABLA_DT    PCK_SUBTIPOS.TI_TABLA DEFAULT 'D_TRAMITES';
    MI_TABLA_T     PCK_SUBTIPOS.TI_TABLA DEFAULT 'TRAMITES';
    MI_REEMPLAZOS  PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_RTA_ACME    PCK_SUBTIPOS.TI_RTA_ACME;
    MI_CAMPOS      PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION   PCK_SUBTIPOS.TI_CONDICION;
  BEGIN
    -- Consecutivo del detalle del tramite actual.
    SELECT MAX(CONSECUTIVO)
    INTO MI_DETALLE
    FROM D_TRAMITES
    WHERE COMPANIA     = UN_COMPANIA
      AND PROCESOS     = UN_PROCESO
      AND TIPO_TRAMITE = UN_TIPO_TRAMITE
     AND NUMERO        = UN_TRAMITE;

    IF MI_DETALLE IS NULL THEN
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
        MI_REEMPLAZOS(1).CLAVE := 'TRAMITE';
        MI_REEMPLAZOS(1).VALOR := UN_TRAMITE;
        MI_REEMPLAZOS(2).CLAVE := 'PROCESO';
        MI_REEMPLAZOS(2).VALOR := UN_PROCESO;      

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_CERTRA_MAX_CONSECUTI
                                  ,UN_TABLAERROR => MI_TABLA_DT
                                  ,UN_REEMPLAZOS => MI_REEMPLAZOS);
      END;
    END IF;
/*
    PR_PREPARAR_VAR_TRAMITE(UN_COMPANIA     => UN_COMPANIA
                           ,UN_PROCESO      => UN_PROCESO
                           ,UN_TIPO_TRAMITE => UN_TIPO_TRAMITE
                           ,UN_TRAMITE      => UN_TRAMITE
                           ,UN_NODO         => UN_NODO_ACTUAL
                           ,UN_D_TRAMITE    => MI_DETALLE
                           ,UN_USUARIO      => UN_USUARIO);  */

    MI_CAMPOS := 'ESTADO        = 5
                 ,MODIFIED_BY   = '''||UN_USUARIO||'''
                 ,DATE_MODIFIED = SYSDATE';

    MI_CONDICION := 'COMPANIA     = '''||UN_COMPANIA    ||'''
                 AND PROCESOS     = '''||UN_PROCESO     ||'''
                 AND TIPO_TRAMITE = '''||UN_TIPO_TRAMITE||'''
                 AND NUMERO       =   '||UN_TRAMITE;

    BEGIN
      BEGIN
        MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_T
                                        ,UN_ACCION    => 'M'
                                        ,UN_CAMPOS    => MI_CAMPOS
                                        ,UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
      MI_REEMPLAZOS(1).CLAVE := 'TRAMITE';
      MI_REEMPLAZOS(1).VALOR := UN_TRAMITE;
      MI_REEMPLAZOS(2).CLAVE := 'PROCESO';
      MI_REEMPLAZOS(2).VALOR := UN_PROCESO;    

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_CERRTRAMITE_M_ESTADO
                                ,UN_TABLAERROR => MI_TABLA_T
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);    
    END;

  END PR_CERRAR_TRAMITE;

  --2
  PROCEDURE PR_PREPARAR_VAR_TRAMITE 
  /*
    NAME              : PR_PREPARAR_VAR_TRAMITE
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR CREATION   : PABLO ANDRES ESPITIA CUCA
    DATE CREATION     : 24/04/2018
    TIME              : 11:53 AM
    SOURCE MODULE     : WORKFLOW (35)
    DESCRIPTION       : Adiciona las variables del nodo a la etapa actual del tramite.
    MODIFIED BY       : 

    @NAME  : prepararVariablesTramite
    @METHOD: POST
  */
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,    -- Codigo de la compania.
    UN_PROCESO      IN TRAMITES.PROCESOS%TYPE,      -- Codigo del proceso.
    UN_TIPO_TRAMITE IN TRAMITES.TIPO_TRAMITE%TYPE,  -- Codigo del tipo de tramite.
    UN_TRAMITE      IN TRAMITES.NUMERO%TYPE,        -- Numero del tramite.
    UN_NODO         IN NODOS.CODIGO%TYPE,           -- Codigo del nodo/etapa actual.
    UN_D_TRAMITE    IN D_TRAMITES.CONSECUTIVO%TYPE, -- Codigo del detalle del tramite.
    UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO      -- Codigo del usuario que desencadena este proceso.
  )
  AS 
    MI_RTA_ACME   PCK_SUBTIPOS.TI_RTA_ACME;
    MI_TABLA_DTV  PCK_SUBTIPOS.TI_TABLA DEFAULT 'D_TRAMITE_VARIABLES';
    MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES    PCK_SUBTIPOS.TI_VALORES;
    MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    BEGIN
      BEGIN
        --@pespitia_20190516: Se adiciona el campo ADJUNTO_OBLIGATORIO
        MI_CAMPOS := 'COMPANIA
                     ,CODIGO_NODO_VARIABLE
                     ,CODIGO_NODO
                     ,CODIGO_PROCESO
                     ,TIPO_TRAMITE
                     ,NUMERO_TRAMITE
                     ,CONSECUTIVO_TRAMITE
                     ,ETIQUETA
                     ,TIPO_DATO
                     ,OBLIGATORIO
                     ,MANEJA_ADJUNTO
                     ,ADJUNTO_OBLIGATORIO
                     ,CREATED_BY
                     ,DATE_CREATED';

        -- Estado: Activo (4)
        MI_VALORES := 'SELECT
                         COMPANIA
                        ,CODIGO
                        ,CODIGO_NODO
                        ,CODIGO_PROCESO
                        ,'''||UN_TIPO_TRAMITE||'''
                        ,  '||UN_TRAMITE     ||'
                        ,  '||UN_D_TRAMITE   ||'
                        ,ETIQUETA
                        ,TIPO_DATO
                        ,OBLIGATORIO
                        ,MANEJA_ADJUNTO
                        ,ADJUNTO_OBLIGATORIO
                        ,'''||UN_USUARIO     ||'''
                        ,SYSDATE
                       FROM NODO_VARIABLES
                       WHERE COMPANIA       = '''||UN_COMPANIA||'''
                         AND CODIGO_PROCESO = '''||UN_PROCESO ||'''
                         AND CODIGO_NODO    = '''||UN_NODO    ||'''
                         AND ESTADO         = 4';

        MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA_DTV
                                        ,UN_ACCION  => 'IS'
                                        ,UN_CAMPOS  => MI_CAMPOS
                                        ,UN_VALORES => MI_VALORES);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
      MI_REEMPLAZOS(1).CLAVE := 'NODO';
      MI_REEMPLAZOS(1).VALOR := UN_NODO;
      MI_REEMPLAZOS(2).CLAVE := 'PROCESO';
      MI_REEMPLAZOS(2).VALOR := UN_PROCESO;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_PREVARTRA_IS_DTRAVAR
                                ,UN_TABLAERROR => MI_TABLA_DTV
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;

  END PR_PREPARAR_VAR_TRAMITE;  

  --3
  PROCEDURE PR_CAMBIAR_EJECUTOR 
  /*
    NAME              : PR_CAMBIAR_EJECUTOR
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR CREATION   : PABLO ANDRES ESPITIA CUCA
    DATE CREATION     : 07/06/2018
    TIME              : 09:41
    SOURCE MODULE     : WORKFLOW (35)
    DESCRIPTION       : Cambiar el ejecutor asignado a un nodo durante el tramite.
    MODIFIED BY       : 

    @NAME  : cambiarEjecutor
    @METHOD: POST
  */
  (
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,      -- Codigo de la compania.
    UN_PROCESO         IN PROCESOS.CODIGO%TYPE,          -- Codigo del proceso.
    UN_TIPO_TRAMITE    IN TIPOTRAMITES.TIPOTRAMITE%TYPE, -- Codigo del tipo de tramite.
    UN_TRAMITE         IN TRAMITES.NUMERO%TYPE,          -- Numero del tramite.
    UN_D_TRAMITE       IN D_TRAMITES.CONSECUTIVO%TYPE,   -- Codigo del detalle del tramite.
    UN_USUARIO_INTERNO IN TRAMITES.USUARIO_INTERNO%TYPE, -- Codigo del nuevo usuario ejecutor.
    UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO        -- COdigo del usuario que desencadena el proceso (Responsable).
  )
  AS 
    MI_RTA_ACME         PCK_SUBTIPOS.TI_RTA_ACME;
    MI_TABLA_DT         PCK_SUBTIPOS.TI_TABLA DEFAULT 'D_TRAMITES';
    MI_TABLA_DTV        PCK_SUBTIPOS.TI_TABLA DEFAULT 'D_TRAMITE_VARIABLES';
    MI_TABLA_T          PCK_SUBTIPOS.TI_TABLA DEFAULT 'TRAMITES';
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
    MI_REEMPLAZOS       PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_USUARIO_ANT      PCK_SUBTIPOS.TI_USUARIO;
  BEGIN
    BEGIN
      BEGIN
        SELECT USUARIO_INTERNO
        INTO MI_USUARIO_ANT
        FROM D_TRAMITES
        WHERE COMPANIA     = UN_COMPANIA 
          AND PROCESOS     = UN_PROCESO   
          AND TIPO_TRAMITE = UN_TIPO_TRAMITE
          AND NUMERO       = UN_TRAMITE    
          AND CONSECUTIVO  = UN_D_TRAMITE; 

      EXCEPTION WHEN NO_DATA_FOUND THEN
        RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
      MI_REEMPLAZOS(1).CLAVE := 'D_TRAMITE';
      MI_REEMPLAZOS(1).VALOR := UN_D_TRAMITE;
      MI_REEMPLAZOS(2).CLAVE := 'TRAMITE';
      MI_REEMPLAZOS(2).VALOR := UN_TRAMITE;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_CAMEJE_NDF_USU_INTER
                                ,UN_TABLAERROR => MI_TABLA_DT
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;

    -- Copiar detalle tramite
    MI_CAMPOS := 'COMPANIA 
                 ,PROCESOS 
                 ,TIPO_TRAMITE 
                 ,NUMERO 
                 ,CONSECUTIVO 
                 ,CODIGO_CALIDAD 
                 ,DEPENDENCIA_DESTINO 
                 ,DEPENDENCIA_ORIGEN 
                 ,DESCRIPCION 
                 ,ESTADO 
                 ,FECHA 
                 ,FECHA_PASE 
                 ,FORMATO 
                 ,FORMATO_FECHA 
                 ,HORA 
                 ,HORA_PASE 
                 ,MENU 
                 ,NODO_DESTINO 
                 ,NODO_ORIGEN 
                 ,SERIE_DOCUMENTAL 
                 ,USUARIO_DESTINO 
                 ,USUARIO_EXTERNO 
                 ,USUARIO_INTERNO 
                 ,USUARIO_ORIGEN 
                 ,CREATED_BY 
                 ,DATE_CREATED';

    MI_VALORES := 'SELECT 
                     COMPANIA 
                    ,PROCESOS 
                    ,TIPO_TRAMITE 
                    ,NUMERO 
                    ,(CONSECUTIVO + 1) 
                    ,CODIGO_CALIDAD 
                    ,DEPENDENCIA_DESTINO 
                    ,DEPENDENCIA_ORIGEN 
                    ,DESCRIPCION 
                    ,ESTADO 
                    ,SYSDATE 
                    ,FECHA_PASE 
                    ,FORMATO 
                    ,FORMATO_FECHA 
                    ,TO_DATE(''30/12/1899 ''||'''||TO_CHAR(SYSDATE,'HH24:MI')||''',''DD/MM/YYYY HH24:MI:SS'')
                    ,HORA_PASE 
                    ,MENU 
                    ,NODO_DESTINO 
                    ,NODO_ORIGEN 
                    ,SERIE_DOCUMENTAL 
                    ,USUARIO_DESTINO 
                    ,USUARIO_EXTERNO 
                    ,'''||UN_USUARIO_INTERNO||'''
                    ,USUARIO_ORIGEN 
                    ,'''||UN_USUARIO        ||'''
                    ,SYSDATE
                   FROM D_TRAMITES
                   WHERE COMPANIA     = '''||UN_COMPANIA    ||'''
                     AND PROCESOS     = '''||UN_PROCESO     ||'''
                     AND TIPO_TRAMITE = '''||UN_TIPO_TRAMITE||'''
                     AND NUMERO       =   '||UN_TRAMITE     ||'
                     AND CONSECUTIVO  =   '||UN_D_TRAMITE; 

    BEGIN
      BEGIN
        MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA_DT
                                        ,UN_ACCION  => 'IS'
                                        ,UN_CAMPOS  => MI_CAMPOS
                                        ,UN_VALORES => MI_VALORES);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
      MI_REEMPLAZOS(1).CLAVE := 'D_TRAMITE';
      MI_REEMPLAZOS(1).VALOR := UN_D_TRAMITE;
      MI_REEMPLAZOS(2).CLAVE := 'TRAMITE';
      MI_REEMPLAZOS(2).VALOR := UN_TRAMITE;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_CAMEJE_IS_CDETRAMITE
                                ,UN_TABLAERROR => MI_TABLA_DT
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;

    -- Copiar variables tramite.
    -- @pespitia_20190516: Se adiciona el campo ADJUNTO_OBLIGATORIO
    MI_CAMPOS := 'COMPANIA 
                 ,CODIGO_PROCESO 
                 ,TIPO_TRAMITE 
                 ,NUMERO_TRAMITE 
                 ,CONSECUTIVO_TRAMITE 
                 ,CODIGO_NODO 
                 ,CODIGO_NODO_VARIABLE 
                 ,ADJUNTO 
                 ,ETIQUETA 
                 ,MANEJA_ADJUNTO 
                 ,ADJUNTO_OBLIGATORIO
                 ,OBLIGATORIO 
                 ,TIPO_DATO 
                 ,VALOR 
                 ,VALOR_FECHA 
                 ,VALOR_TEXTO 
                 ,CREATED_BY 
                 ,DATE_CREATED';

    MI_VALORES := 'SELECT 
                     COMPANIA 
                    ,CODIGO_PROCESO 
                    ,TIPO_TRAMITE 
                    ,NUMERO_TRAMITE 
                    ,(CONSECUTIVO_TRAMITE + 1) 
                    ,CODIGO_NODO 
                    ,CODIGO_NODO_VARIABLE 
                    ,ADJUNTO 
                    ,ETIQUETA 
                    ,MANEJA_ADJUNTO 
                    ,ADJUNTO_OBLIGATORIO
                    ,OBLIGATORIO 
                    ,TIPO_DATO 
                    ,VALOR 
                    ,VALOR_FECHA 
                    ,VALOR_TEXTO 
                    ,'''||UN_USUARIO||''' 
                    ,SYSDATE
                   FROM D_TRAMITE_VARIABLES
                   WHERE COMPANIA            = '''||UN_COMPANIA    ||'''
                     AND CODIGO_PROCESO      = '''||UN_PROCESO     ||'''
                     AND TIPO_TRAMITE        = '''||UN_TIPO_TRAMITE||'''
                     AND NUMERO_TRAMITE      =   '||UN_TRAMITE     ||'
                     AND CONSECUTIVO_TRAMITE =   '||UN_D_TRAMITE;

    BEGIN
      BEGIN
        MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA_DTV
                                        ,UN_ACCION  => 'IS'
                                        ,UN_CAMPOS  => MI_CAMPOS
                                        ,UN_VALORES => MI_VALORES);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
      MI_REEMPLAZOS(1).CLAVE := 'D_TRAMITE';
      MI_REEMPLAZOS(1).VALOR := UN_D_TRAMITE;
      MI_REEMPLAZOS(2).CLAVE := 'TRAMITE';
      MI_REEMPLAZOS(2).VALOR := UN_TRAMITE;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_CAMEJE_IS_VDETRAMITE
                                ,UN_TABLAERROR => MI_TABLA_DTV
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;                   

    -- Actualizar tramite
    MI_CAMPOS := 'USUARIO_INTERNO = '''||UN_USUARIO_INTERNO||''' 
                 ,MODIFIED_BY     = '''||UN_USUARIO        ||'''
                 ,DATE_MODIFIED   = SYSDATE';

    MI_CONDICION := 'COMPANIA      = '''||UN_COMPANIA    ||'''
                 AND PROCESOS      = '''||UN_PROCESO     ||'''
                 AND TIPO_TRAMITE  = '''||UN_TIPO_TRAMITE||'''
                 AND NUMERO        =   '||UN_TRAMITE;

    BEGIN
      BEGIN
        MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_T
                                        ,UN_ACCION    => 'M'
                                        ,UN_CAMPOS    => MI_CAMPOS
                                        ,UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
      MI_REEMPLAZOS(1).CLAVE := 'TRAMITE';
      MI_REEMPLAZOS(1).VALOR := UN_TRAMITE;
      MI_REEMPLAZOS(2).CLAVE := 'TIPO_TRAMITE';
      MI_REEMPLAZOS(2).VALOR := UN_TIPO_TRAMITE;
      MI_REEMPLAZOS(3).CLAVE := 'PROCESO';
      MI_REEMPLAZOS(3).VALOR := UN_PROCESO;    

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_CAMEJE_M_USUARIO_INT
                                ,UN_TABLAERROR => MI_TABLA_T
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;  

    MI_CAMPOS := 'ESTADO        = 5
                 ,FECHA_PASE    = SYSDATE
                 ,HORA_PASE     = TO_DATE(''30/12/1899 ''||'''||TO_CHAR(SYSDATE,'HH24:MI')||''',''DD/MM/YYYY HH24:MI:SS'')
                 ,MODIFIED_BY   = '''||UN_USUARIO||'''
                 ,DATE_MODIFIED = SYSDATE';

    MI_CONDICION := 'COMPANIA      = '''||UN_COMPANIA    ||'''
                 AND PROCESOS      = '''||UN_PROCESO     ||'''
                 AND TIPO_TRAMITE  = '''||UN_TIPO_TRAMITE||'''
                 AND NUMERO        =   '||UN_TRAMITE     ||'
                 AND CONSECUTIVO   =   '||UN_D_TRAMITE;

    -- Inactivar estado detalle de tramite anterior
    BEGIN
      BEGIN
        MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_DT
                                        ,UN_ACCION    => 'M'
                                        ,UN_CAMPOS    => MI_CAMPOS
                                        ,UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
      MI_REEMPLAZOS(1).CLAVE := 'TRAMITE';
      MI_REEMPLAZOS(1).VALOR := UN_TRAMITE;
      MI_REEMPLAZOS(2).CLAVE := 'TIPO_TRAMITE';
      MI_REEMPLAZOS(2).VALOR := UN_TIPO_TRAMITE;
      MI_REEMPLAZOS(3).CLAVE := 'PROCESO';
      MI_REEMPLAZOS(3).VALOR := UN_PROCESO;    

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_CAMEJE_M_ESTADODETRA
                                ,UN_TABLAERROR => MI_TABLA_DT
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END; 
  END PR_CAMBIAR_EJECUTOR;

  --4
  PROCEDURE PR_TRAMITAR 
  /*
    NAME              : PR_TRAMITAR
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR CREATION   : PABLO ANDRES ESPITIA CUCA
    DATE CREATION     : 25/04/2018
    TIME              : 15:49
    SOURCE MODULE     : WORKFLOW (35)
    DESCRIPTION       : Prepara el primer detalle del tramite y el pase de tramites.
    MODIFIED BY       : 

    @NAME  : tramitar
    @METHOD: POST
  */
  (
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,                                -- Codigo de la compania.
    UN_PROCESO         IN TRAMITES.PROCESOS%TYPE,                                  -- Codigo del proceso.
    UN_TIPO_TRAMITE    IN TRAMITES.TIPO_TRAMITE%TYPE,                              -- Codigo del tipo de tramite.
    UN_NUMERO          IN TRAMITES.NUMERO%TYPE,                                    -- Numero del tramite.
    UN_NODO_ORIGEN     IN NODOS.CODIGO%TYPE,                                       -- Codigo del nodo/etapa inicial.
    UN_NODO_DESTINO    IN NODOS.CODIGO%TYPE,                                       -- Codigo del nodo/etapa destino.
    UN_TRAMITE_INI     IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0,                        -- Indicador que establece si el tramite es inicial.
    UN_USUARIO_DESTINO IN PCK_SUBTIPOS.TI_USUARIO,                                 -- Codigo del usuario que recibe el tramite.
    UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO,                                 -- Codigo del usuario que desencadena este proceso.
    UN_ARCHIVOCENTRAL  IN D_TRAMITES.CODIGO_ARCHIVO_CENTRAL%TYPE  DEFAULT ' ',     -- Indicador que establece si se deben generar la proyecciones.
    UN_PROYECCIONES    IN PCK_SUBTIPOS.TI_LOGICO DEFAULT -1                        -- Indicador que establece si se deben generar la proyecciones.
  )
  AS 
    MI_CONSECUTIVO         PCK_SUBTIPOS.TI_ENTERO_LARGO; -- Consecutivo del detalle del tramite.
    MI_DEP_NOD_ORIGEN      DEPENDENCIA.CODIGO%TYPE;
    MI_DEP_NOD_DESTINO     DEPENDENCIA.CODIGO%TYPE;
    -- 20180525_1421:@pespitia, Se crea la variable formato y fecha destino.
    MI_FORMATO_DESTINO     NODOS.FORMATO%TYPE;
    MI_FORMATO_FEC_DESTINO DATE := NULL;
    -- 20180606_1109:@pespetia, Se crea la variable menu destino.
    MI_MENU_DESTINO        NODOS.MENU%TYPE;
    MI_TABLA               PCK_SUBTIPOS.TI_TABLA;
    MI_TABLA_T             PCK_SUBTIPOS.TI_TABLA DEFAULT 'TRAMITES';
    MI_TABLA_N             PCK_SUBTIPOS.TI_TABLA DEFAULT 'NODOS';
    MI_TABLA_DT            PCK_SUBTIPOS.TI_TABLA DEFAULT 'D_TRAMITES';
    MI_TABLA_PT            PCK_SUBTIPOS.TI_TABLA DEFAULT 'PROYECCIONES_TRAMITE';
    MI_REEMPLAZOS          PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_RTA_ACME            PCK_SUBTIPOS.TI_RTA_ACME;
    MI_CAMPOS              PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES             PCK_SUBTIPOS.TI_VALORES;
    MI_CRITERIO            PCK_SUBTIPOS.TI_CONDICION;
    MI_CONDICION           PCK_SUBTIPOS.TI_CONDICION;
    MI_DESC_NODO_ACTUAL    TRAMITES.DESC_NODO_ACTUAL%TYPE;
    MI_DESCRIPCION         TRAMITES.DESC_NODO_ACTUAL%TYPE DEFAULT ' ';
    MI_ETAPA_FECHAREAL     DATE;
    MI_ETAPA_DIASREAL      PCK_SUBTIPOS.TI_ENTERO;
    -- 20181003_0922:@pespitia, Se adiciona el numero del contrato
    MI_TRAMITE_CONTRATO    TRAMITES.CONTRATO_NUM%TYPE;

    MI_DIAS_REPROG         PCK_SUBTIPOS.TI_ENTERO;
    MI_MSG_ERROR           PLS_INTEGER;
    MI_CONSECUTIVO_DT      PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_INICIAL          NUMBER(20,0);
    MI_PRORROGA           PCK_SUBTIPOS.TI_LOGICO;
    MI_VALOR_PRORROGA     VARCHAR2(200);
  BEGIN
    MI_CRITERIO := 'COMPANIA     = '''||UN_COMPANIA    ||'''
                AND PROCESOS     = '''||UN_PROCESO     ||'''
                AND TIPO_TRAMITE = '''||UN_TIPO_TRAMITE||'''
                AND NUMERO       =   '||UN_NUMERO;

    MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => MI_TABLA_DT
                                                      ,UN_CRITERIO => MI_CRITERIO
                                                      ,UN_CAMPO    => 'CONSECUTIVO');

    -- Recuperar dependencia nodo origen                                                  
    BEGIN
      BEGIN
        SELECT DEPENDENCIA
        INTO MI_DEP_NOD_ORIGEN
        FROM NODOS
        WHERE COMPANIA       = UN_COMPANIA
          AND CODIGO_PROCESO = UN_PROCESO
          AND CODIGO         = UN_NODO_ORIGEN;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
      MI_REEMPLAZOS(1).CLAVE := 'ETAPA';
      MI_REEMPLAZOS(1).VALOR := UN_NODO_ORIGEN;
      MI_REEMPLAZOS(2).CLAVE := 'PROCESO';
      MI_REEMPLAZOS(3).VALOR := UN_PROCESO;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_PRETRAINI_NDF_NODORI
                                ,UN_TABLAERROR => MI_TABLA_N
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;

    -- Recuperar dependencia del nodo destino   
    BEGIN
      BEGIN
        SELECT 
          DEPENDENCIA
         ,FORMATO
         ,FORMATO_FECHA
         ,MENU
        INTO 
          MI_DEP_NOD_DESTINO
         ,MI_FORMATO_DESTINO
         ,MI_FORMATO_FEC_DESTINO
         ,MI_MENU_DESTINO
        FROM NODOS
        WHERE COMPANIA       = UN_COMPANIA
          AND CODIGO_PROCESO = UN_PROCESO
          AND CODIGO         = UN_NODO_DESTINO;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
      MI_REEMPLAZOS(1).CLAVE := 'ETAPA';
      MI_REEMPLAZOS(1).VALOR := UN_NODO_DESTINO;
      MI_REEMPLAZOS(2).CLAVE := 'PROCESO';
      MI_REEMPLAZOS(3).VALOR := UN_PROCESO;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_PRETRAINI_NDF_NODORI
                                ,UN_TABLAERROR => MI_TABLA_N
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;  

      BEGIN
    SELECT 
          NP.PRORROGA
        INTO 
          MI_PRORROGA
        FROM TRAMITES T
        INNER JOIN NODOS NP
             ON T.COMPANIA = NP.COMPANIA 
            AND T.PROCESOS = NP.CODIGO_PROCESO
           -- AND T.NODO_ACTUAL = NP.CODIGO
                    WHERE T.COMPANIA     = UN_COMPANIA
                      AND T.PROCESOS     = UN_PROCESO
                      AND T.TIPO_TRAMITE = UN_TIPO_TRAMITE
                      AND T.NUMERO       = UN_NUMERO
                      AND NP.CODIGO      = UN_NODO_DESTINO
                      AND NP.PRORROGA NOT IN (0);
                      
     EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_PRORROGA := 0;
      END;
      
    MI_VALOR_PRORROGA := CASE WHEN MI_PRORROGA NOT IN (0) THEN ',FECHA_PRORROGA = SYSDATE' ELSE '' END;

    -- Recuperar descripcion y numero de contrato del nodo actual tramite.
    BEGIN
      BEGIN
        SELECT 
          DESC_NODO_ACTUAL
         ,CONTRATO_NUM
        INTO 
          MI_DESC_NODO_ACTUAL
         ,MI_TRAMITE_CONTRATO
        FROM TRAMITES
        WHERE COMPANIA     = UN_COMPANIA
          AND PROCESOS     = UN_PROCESO
          AND TIPO_TRAMITE = UN_TIPO_TRAMITE
          AND NUMERO       = UN_NUMERO;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
      MI_REEMPLAZOS(1).CLAVE := 'TRAMITE';
      MI_REEMPLAZOS(1).VALOR := UN_NUMERO;
      MI_REEMPLAZOS(2).CLAVE := 'PROCESO';
      MI_REEMPLAZOS(2).VALOR := UN_PROCESO;
      MI_REEMPLAZOS(3).CLAVE := 'TIPO';
      MI_REEMPLAZOS(3).VALOR := UN_TIPO_TRAMITE;  

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_TRAMITAR_NDF_TRAMITE
                                ,UN_TABLAERROR => MI_TABLA_T
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);          
    END;

    MI_CAMPOS := 'NODO_ORIGEN      = '''||UN_NODO_ORIGEN    ||'''
                 ,NODO_ACTUAL      = '''||UN_NODO_DESTINO   ||'''
                 ,HORA             = TO_DATE(''30/12/1899 ''||'''||TO_CHAR(CURRENT_DATE,'HH24:MI')||''',''DD/MM/YYYY HH24:MI:SS'')
                 ,FECHA            = CURRENT_DATE
                 ,USUARIO_INTERNO  = '''||UN_USUARIO_DESTINO||'''
                 ,DESC_NODO_ACTUAL = NULL
                 '|| MI_VALOR_PRORROGA || '
                 ,MODIFIED_BY      = '''||UN_USUARIO        ||'''
                 ,DATE_MODIFIED    = CURRENT_DATE';

    MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_T
                                    ,UN_ACCION    => 'M'
                                    ,UN_CAMPOS    => MI_CAMPOS
                                    ,UN_CONDICION => MI_CRITERIO);

    MI_CAMPOS := 'COMPANIA
                 ,PROCESOS
                 ,TIPO_TRAMITE
                 ,NUMERO
                 ,CONSECUTIVO
                 ,NODO_ORIGEN
                 ,NODO_DESTINO
                 ,DEPENDENCIA_ORIGEN
                 ,DEPENDENCIA_DESTINO
                 ,USUARIO_ORIGEN
                 ,USUARIO_DESTINO
                 ,USUARIO_INTERNO
                 ,USUARIO_EXTERNO
                 ,ESTADO
                 ,CODIGO_CALIDAD
                 ,SERIE_DOCUMENTAL
                 ,HORA
                 ,FECHA
                 ,DESCRIPCION
                 ,FORMATO
                 ,FORMATO_FECHA
                 ,MENU
                 ,CONTRATO_NUM
                 ,CREATED_BY
                 ,CODIGO_ARCHIVO_CENTRAL
                 ,DATE_CREATED';

    -- 20180614_1527:@pespetia             
    IF UN_TRAMITE_INI NOT IN(0) THEN
      MI_DESCRIPCION := MI_DESC_NODO_ACTUAL;
    END IF;

    MI_VALORES := 'SELECT 
                     COMPANIA
                    ,PROCESOS
                    ,TIPO_TRAMITE
                    ,NUMERO
                    ,  '||MI_CONSECUTIVO               ||'
                    ,NODO_ORIGEN
                    ,NODO_ACTUAL
                    ,'''||MI_DEP_NOD_ORIGEN            ||'''
                    ,'''||MI_DEP_NOD_DESTINO           ||'''
                    ,'''||UN_USUARIO                   ||'''
                    ,'''||UN_USUARIO                   ||'''
                    ,USUARIO_INTERNO
                    ,'''||UN_USUARIO                   ||'''
                    ,ESTADO
                    ,CODIGO_CALIDAD
                    ,SERIE_DOCUMENTAL
                    ,HORA
                    ,FECHA
                    ,'''||MI_DESCRIPCION               ||'''
                    ,'''||MI_FORMATO_DESTINO           ||'''
                    ,TO_DATE('''||TRUNC(MI_FORMATO_FEC_DESTINO)||''',''DD/MM/YYYY'')
                    ,'''||MI_MENU_DESTINO              ||'''
                    ,'''||MI_TRAMITE_CONTRATO          ||'''
                    ,'''||UN_USUARIO                   ||'''
                    ,' || CASE WHEN UN_ARCHIVOCENTRAL = ' ' THEN 'NULL' ELSE ''''||UN_ARCHIVOCENTRAL            ||'''' END || '
                    ,SYSDATE
                   FROM TRAMITES
                   WHERE COMPANIA     = '''||UN_COMPANIA    ||'''
                     AND PROCESOS     = '''||UN_PROCESO     ||'''
                     AND TIPO_TRAMITE = '''||UN_TIPO_TRAMITE||'''
                     AND NUMERO       =   '||UN_NUMERO;

    BEGIN
      BEGIN
        MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_ACCION  => 'IS'
                                        ,UN_CAMPOS  => MI_CAMPOS
                                        ,UN_TABLA   => MI_TABLA_DT
                                        ,UN_VALORES => MI_VALORES);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
      MI_REEMPLAZOS(1).CLAVE := 'TRAMITE';
      MI_REEMPLAZOS(1).VALOR := UN_NUMERO;
      MI_REEMPLAZOS(2).CLAVE := 'PROCESO';
      MI_REEMPLAZOS(2).VALOR := UN_PROCESO;
      MI_REEMPLAZOS(3).CLAVE := 'TIPO_TRAMITE';
      MI_REEMPLAZOS(3).VALOR := UN_TIPO_TRAMITE;    

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_PRETRAINI_IS_DTRAMIT
                                ,UN_TABLAERROR => MI_TABLA_T
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;

    PR_PREPARAR_VAR_TRAMITE(UN_COMPANIA     => UN_COMPANIA
                           ,UN_PROCESO      => UN_PROCESO
                           ,UN_TIPO_TRAMITE => UN_TIPO_TRAMITE
                           ,UN_TRAMITE      => UN_NUMERO
                           ,UN_NODO         => UN_NODO_DESTINO
                           ,UN_D_TRAMITE    => MI_CONSECUTIVO
                           ,UN_USUARIO      => UN_USUARIO); 

     PR_PREPARAR_VAR_PROCESO(UN_COMPANIA     => UN_COMPANIA
                           ,UN_PROCESO      => UN_PROCESO
                           ,UN_TIPO_TRAMITE => UN_TIPO_TRAMITE
                           ,UN_TRAMITE      => UN_NUMERO
                           ,UN_USUARIO      => UN_USUARIO);
                           
     PR_PROCEDENCIA_A_ENVIARCORREO(UN_COMPANIA        => UN_COMPANIA,
                               UN_PROCESO         => UN_PROCESO,
                               UN_NODO            => UN_NODO_ORIGEN,
                               UN_NODO_DESTINO    => UN_NODO_DESTINO,
                               UN_TIPO_TRAMITE    => UN_TIPO_TRAMITE,
                               UN_TRAMITE         => UN_NUMERO);

    IF UN_PROCESO IN('20000','20001') THEN 
    IF UN_NODO_DESTINO IN(5300,5200,4800,4900,3100,2000,2005,2300,5700,3000,5400) THEN
     MI_INICIAL := EXTRACT(YEAR FROM SYSDATE) || UN_COMPANIA || '00001';
     MI_CONSECUTIVO_DT := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'D_TRAMITE_VARIABLES',
                                                           UN_CRITERIO => 'COMPANIA    = '''|| UN_COMPANIA ||'''
                                                                           AND ETIQUETA IN (''Consecutivo'')
                                                                           AND CODIGO_PROCESO IN(''20000'',''20001'')
                                                                           AND CODIGO_NODO IN(5300,5200,4800,4900,3100,2005,2300,5700,3000,5400)',
                                                           UN_CAMPO    => 'VALOR',
                                                           UN_INICIAL  => MI_INICIAL);

    MI_CONSECUTIVO_DT := CASE WHEN UN_NODO_DESTINO  IN(2000) THEN UN_NUMERO ELSE MI_CONSECUTIVO_DT END;

     MI_CAMPOS := 'VALOR = CASE WHEN  ETIQUETA IN (''Consecutivo'') THEN '''|| MI_CONSECUTIVO_DT || ''' END,
                  VALOR_TEXTO = CASE WHEN  ETIQUETA IN (''Fecha'') THEN TO_CHAR(SYSDATE) END';

    MI_CONDICION := 'COMPANIA     = '''||UN_COMPANIA    ||'''
                 AND TIPO_TRAMITE = '''||UN_TIPO_TRAMITE||'''
                 AND CODIGO_PROCESO = '''||UN_PROCESO     ||'''
                 AND CODIGO_NODO = '|| UN_NODO_DESTINO ||'
                 AND NUMERO_TRAMITE = '|| UN_NUMERO ||'
                 AND ETIQUETA IN (''Consecutivo'',''Fecha'')';

                        BEGIN
          BEGIN
            MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA     => 'D_TRAMITE_VARIABLES'
                                            ,UN_ACCION    => 'M'
                                            ,UN_CAMPOS    => MI_CAMPOS
                                            ,UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
          END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
          MI_REEMPLAZOS(1).CLAVE := 'NODO';
          MI_REEMPLAZOS(1).VALOR := UN_NODO_ORIGEN;
          MI_REEMPLAZOS(2).CLAVE := 'TRAMITE';
          MI_REEMPLAZOS(2).VALOR := UN_NUMERO;
          MI_REEMPLAZOS(3).CLAVE := 'PROCESO';
          MI_REEMPLAZOS(3).VALOR := UN_PROCESO;     

          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_TRA_M_EJECPROYECCION
                                    ,UN_TABLAERROR => MI_TABLA_DT
                                    ,UN_REEMPLAZOS => MI_REEMPLAZOS);          
        END;
    END IF;
    END IF;
    
    IF UN_PROCESO IN('20000') THEN 
    IF UN_NODO_DESTINO IN(20000) THEN

     MI_CAMPOS := 'VALOR = CASE WHEN  ETIQUETA IN (''Consecutivo'') THEN '''|| UN_NUMERO || ''' END,
                  VALOR_TEXTO = CASE WHEN  ETIQUETA IN (''Fecha'') THEN TO_CHAR(SYSDATE) END';

    MI_CONDICION := 'COMPANIA     = '''||UN_COMPANIA    ||'''
                 AND TIPO_TRAMITE = '''||UN_TIPO_TRAMITE||'''
                 AND CODIGO_PROCESO = '''||UN_PROCESO     ||'''
                 AND CODIGO_NODO = '|| UN_NODO_DESTINO ||'
                 AND NUMERO_TRAMITE = '|| UN_NUMERO ||'
                 AND ETIQUETA IN (''Consecutivo'',''Fecha'')';

       BEGIN
          BEGIN
            MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA     => 'D_TRAMITE_VARIABLES'
                                            ,UN_ACCION    => 'M'
                                            ,UN_CAMPOS    => MI_CAMPOS
                                            ,UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
          END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
          MI_REEMPLAZOS(1).CLAVE := 'NODO';
          MI_REEMPLAZOS(1).VALOR := UN_NODO_ORIGEN;
          MI_REEMPLAZOS(2).CLAVE := 'TRAMITE';
          MI_REEMPLAZOS(2).VALOR := UN_NUMERO;
          MI_REEMPLAZOS(3).CLAVE := 'PROCESO';
          MI_REEMPLAZOS(3).VALOR := UN_PROCESO;     

          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_TRA_M_EJECPROYECCION
                                    ,UN_TABLAERROR => MI_TABLA_DT
                                    ,UN_REEMPLAZOS => MI_REEMPLAZOS);          
        END;
    END IF;
    END IF;

    -- Cambiar estado a inactivo detalle origen.
    IF UN_TRAMITE_INI IN(0) THEN
    --20180524_1422:@pespitia, se adiciona el campo FECHA_PASE y HORA_PASE.
      MI_CAMPOS := 'ESTADO        = 5
                   ,DESCRIPCION   = '||CHR(39)||MI_DESC_NODO_ACTUAL||CHR(39)||CHR(10)
                ||',CONTRATO_NUM  = '||CHR(39)||MI_TRAMITE_CONTRATO||CHR(39)||CHR(10)
                ||',FECHA_PASE    = SYSDATE
                   ,HORA_PASE     = TO_DATE(''30/12/1899 ''||'''||TO_CHAR(SYSDATE,'HH24:MI')||''',''DD/MM/YYYY HH24:MI:SS'')
                   ,MODIFIED_BY   = '''||UN_USUARIO         ||'''
                   ,DATE_MODIFIED = SYSDATE';

      MI_CONDICION := 'COMPANIA     = '''||UN_COMPANIA    ||'''
                   AND PROCESOS     = '''||UN_PROCESO     ||'''
                   AND TIPO_TRAMITE = '''||UN_TIPO_TRAMITE||'''
                   AND NUMERO       =   '||UN_NUMERO      ||'
                   AND CONSECUTIVO  =   '||(MI_CONSECUTIVO - 1);                  

      BEGIN
        BEGIN
          MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_DT
                                          ,UN_ACCION    => 'M'
                                          ,UN_CAMPOS    => MI_CAMPOS
                                          ,UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
        END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
        MI_REEMPLAZOS(1).CLAVE := 'TRAMITE';
        MI_REEMPLAZOS(1).VALOR := UN_NUMERO;
        MI_REEMPLAZOS(2).CLAVE := 'PROCESO';
        MI_REEMPLAZOS(2).VALOR := UN_PROCESO;
        MI_REEMPLAZOS(3).CLAVE := 'DTRAMITE';
        MI_REEMPLAZOS(3).VALOR := (MI_CONSECUTIVO - 1);   

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_TRA_M_ESTADODETANTER
                                  ,UN_TABLAERROR => MI_TABLA_DT
                                  ,UN_REEMPLAZOS => MI_REEMPLAZOS);      
      END;
    END IF;

    --Proceso de Prorroga
    BEGIN
    SELECT 
          NP.PRORROGA
        INTO 
          MI_PRORROGA
        FROM TRAMITES T
        INNER JOIN NODOS NP
             ON T.COMPANIA = NP.COMPANIA 
            AND T.PROCESOS = NP.CODIGO_PROCESO
           -- AND T.NODO_ACTUAL = NP.CODIGO
                    WHERE T.COMPANIA     = UN_COMPANIA
                      AND T.PROCESOS     = UN_PROCESO
                      AND T.TIPO_TRAMITE = UN_TIPO_TRAMITE
                      AND T.NUMERO       = UN_NUMERO
                      AND NP.CODIGO      = UN_NODO_ORIGEN
                      AND NP.PRORROGA NOT IN (0);
                      
     EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_PRORROGA := 0;
      END;
    
    IF MI_PRORROGA NOT IN (0) THEN 
        
         MI_CAMPOS := 'DIAS_PRORROGA    =   '|| FC_DIAS_PRG(UN_COMPANIA, UN_NUMERO) ||'
                      ,MODIFIED_BY   = '''|| UN_USUARIO ||'''
                      ,DATE_MODIFIED = SYSDATE';

        MI_CONDICION := 'COMPANIA     = '''||UN_COMPANIA    ||'''
                     AND PROCESOS     = '''||UN_PROCESO     ||'''
                     AND TIPO_TRAMITE = '''||UN_TIPO_TRAMITE||'''
                     AND NUMERO       = '''||UN_NODO_ORIGEN ||'''';
                     
    BEGIN
          BEGIN
            MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA     => 'TRAMITES'
                                            ,UN_ACCION    => 'M'
                                            ,UN_CAMPOS    => MI_CAMPOS
                                            ,UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
          END;
    END;
    
    END IF;

    -- Proceso proyecciones
    IF UN_PROYECCIONES NOT IN(0) THEN
      IF UN_TRAMITE_INI IN(0) THEN 
        SELECT FECHA
        INTO MI_ETAPA_FECHAREAL
        FROM D_TRAMITES
        WHERE COMPANIA     = UN_COMPANIA
          AND TIPO_TRAMITE = UN_TIPO_TRAMITE
          AND PROCESOS     = UN_PROCESO
          AND NUMERO       = UN_NUMERO
          AND CONSECUTIVO  = (MI_CONSECUTIVO - 1);

        MI_ETAPA_DIASREAL := TRUNC(SYSDATE) - TRUNC(MI_ETAPA_FECHAREAL);

        -- Colocar fecha y dias reales del tramite en la proyeccion
        MI_CAMPOS := 'REAL_FECHA    = SYSDATE
                     ,REAL_DIAS     =   '||MI_ETAPA_DIASREAL||'
                     ,MODIFIED_BY   = '''||UN_USUARIO       ||'''
                     ,DATE_MODIFIED = SYSDATE';

        MI_CONDICION := 'COMPANIA     = '''||UN_COMPANIA    ||'''
                     AND TIPO_TRAMITE = '''||UN_TIPO_TRAMITE||'''
                     AND PROCESO      = '''||UN_PROCESO     ||'''
                     AND TRAMITE      =   '||UN_NUMERO      ||'
                     AND NODO         = '''||UN_NODO_ORIGEN ||'''';             

        BEGIN
          BEGIN
            MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_PT
                                            ,UN_ACCION    => 'M'
                                            ,UN_CAMPOS    => MI_CAMPOS
                                            ,UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
          END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
          MI_REEMPLAZOS(1).CLAVE := 'NODO';
          MI_REEMPLAZOS(1).VALOR := UN_NODO_ORIGEN;
          MI_REEMPLAZOS(2).CLAVE := 'TRAMITE';
          MI_REEMPLAZOS(2).VALOR := UN_NUMERO;
          MI_REEMPLAZOS(3).CLAVE := 'PROCESO';
          MI_REEMPLAZOS(3).VALOR := UN_PROCESO;     

          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_TRA_M_EJECPROYECCION
                                    ,UN_TABLAERROR => MI_TABLA_DT
                                    ,UN_REEMPLAZOS => MI_REEMPLAZOS);          
        END;

        IF MI_ETAPA_DIASREAL > 0 THEN
          PR_REPROGRAMAR_NODO(UN_COMPANIA     => UN_COMPANIA
                             ,UN_TIPO_TRAMITE => UN_TIPO_TRAMITE
                             ,UN_PROCESO      => UN_PROCESO
                             ,UN_NODO         => UN_NODO_ORIGEN
                             ,UN_NODO_ANT     => NULL
                             ,UN_TRAMITE      => UN_NUMERO
                             ,UN_DIAS_REPROG  => MI_ETAPA_DIASREAL
                             ,UN_USUARIO      => UN_USUARIO);        
        END IF;
      END IF;

      -- Cargar proyecciones del tramite.
      IF UN_TRAMITE_INI NOT IN(0) THEN
        PR_PROYECTAR_NODO(UN_COMPANIA     => UN_COMPANIA
                         ,UN_TIPO_TRAMITE => UN_TIPO_TRAMITE
                         ,UN_PROCESO      => UN_PROCESO
                         ,UN_NODO         => UN_NODO_ORIGEN
                         ,UN_NODO_ANT     => NULL
                         ,UN_TRAMITE      => UN_NUMERO
                         ,UN_USUARIO      => UN_USUARIO);
      END IF;
    END IF;

    --PASE A OTROS PROCESOS
    PR_TRAMITAR_A_PROCESO(
    UN_COMPANIA        => UN_COMPANIA,
    UN_PROCESO         => UN_PROCESO,
    UN_TIPO_TRAMITE    => UN_TIPO_TRAMITE, 
    UN_NUMERO          => UN_NUMERO,
    UN_CONSECUTIVO     => MI_CONSECUTIVO,
    UN_NODO_ORIGEN     => UN_NODO_ORIGEN,
    UN_NODO_DESTINO    => UN_NODO_DESTINO,
    UN_USUARIO_DESTINO => UN_USUARIO_DESTINO,
    UN_USUARIO         => UN_USUARIO,
    UN_PROYECCIONES    => UN_PROYECCIONES);

  END PR_TRAMITAR;    

  --5
  PROCEDURE PR_VALIDAR_INF_DETALLADA 
  /*
    NAME              : PR_VALIDAR_INF_DETALLADA
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR CREATION   : PABLO ANDRES ESPITIA CUCA
    DATE CREATION     : 03/05/2018
    TIME              : 16:44
    SOURCE MODULE     : WORKFLOW (35)
    DESCRIPTION       : Valida que las variables del nodo hayan sido diligenciadas y en caso de manejar adjunto tengan asociado un documento. 
    MODIFIED BY       : 

    @NAME  : validarInfDetallada
    @METHOD: POST
  */
  (
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,     -- Codigo de la compania.
    UN_PROCESO     IN PROCESOS.CODIGO%TYPE,         -- Codigo del proceso.
    UN_TIPOTRAMITE IN TIPOTRAMITES.TIPOTRAMITE%TYPE,-- Codigo del tipo de tramite.
    UN_TRAMITE     IN TRAMITES.NUMERO%TYPE,         -- Numero del tramite.
    UN_DTRAMITE    IN D_TRAMITES.CONSECUTIVO%TYPE,  -- Detalle del tramite.
    UN_NODO        IN NODOS.CODIGO%TYPE             -- Codigo del nodo.
  )
  AS 
    MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_ERROR_COD  PLS_INTEGER;
    MI_RURAADJUNTO VARCHAR2(2000);
  BEGIN
    --@pespitia_20190516: Se adiciona el campo ADJUNTO_OBLIGATORIO
    FOR MI_RS IN(SELECT
                   CODIGO_NODO_VARIABLE
                  ,ETIQUETA
                  ,OBLIGATORIO
                  ,TIPO_DATO
                  ,VALOR
                  ,VALOR_TEXTO
                  ,VALOR_FECHA
                  ,MANEJA_ADJUNTO
                  ,ADJUNTO_OBLIGATORIO
                  ,ADJUNTO
                 FROM D_TRAMITE_VARIABLES
                 WHERE COMPANIA            = UN_COMPANIA
                   AND CODIGO_PROCESO      = UN_PROCESO
                   AND TIPO_TRAMITE        = UN_TIPOTRAMITE
                   AND NUMERO_TRAMITE      = UN_TRAMITE
                   AND CONSECUTIVO_TRAMITE = UN_DTRAMITE
                   AND CODIGO_NODO         = UN_NODO)
    LOOP
      BEGIN
        -- Variables obligatorias
        IF MI_RS.OBLIGATORIO NOT IN(0) THEN
          IF (MI_RS.TIPO_DATO IN(6) AND MI_RS.VALOR       IS NULL) OR
             (MI_RS.TIPO_DATO IN(7) AND MI_RS.VALOR_FECHA IS NULL) OR
             (MI_RS.TIPO_DATO IN(8) AND MI_RS.VALOR_TEXTO IS NULL) 
          THEN
            MI_ERROR_COD := PCK_ERRORES.ERR_WF_PR_VALINFDET_MSG_TVALOR;

            RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
          END IF;
        END IF;

        MI_RURAADJUNTO := '/C'|| UN_COMPANIA ||'/P'|| UN_PROCESO ||'/N'|| UN_NODO ||'/'|| UN_TIPOTRAMITE ||'_'|| UN_TRAMITE ||'_'|| UN_DTRAMITE ||'_';

        -- Variables con adjunto
        IF MI_RS.MANEJA_ADJUNTO NOT IN(0) AND MI_RS.ADJUNTO_OBLIGATORIO NOT IN(0) AND MI_RS.ADJUNTO NOT LIKE CONCAT(CONCAT('%',MI_RURAADJUNTO), '%') THEN
          MI_ERROR_COD := PCK_ERRORES.ERR_WF_PR_VALINFDET_MSG_ADJUNT;

          RAISE PCK_EXCEPCIONES.EXC_WORKFLOW; 
        END IF;

        IF REGEXP_COUNT(MI_RS.ADJUNTO,'/') = 1 AND MI_RS.MANEJA_ADJUNTO NOT IN(0) AND MI_RS.ADJUNTO_OBLIGATORIO NOT IN(0) THEN
          MI_ERROR_COD := PCK_ERRORES.ERR_WF_PR_VALINFDET_MSG_ADJUNT;
          RAISE PCK_EXCEPCIONES.EXC_WORKFLOW; 
        END IF;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
        MI_REEMPLAZOS(1).CLAVE := 'ETIQUETA';
        MI_REEMPLAZOS(1).VALOR := MI_RS.ETIQUETA;
        MI_REEMPLAZOS(2).CLAVE := 'CODIGO';
        MI_REEMPLAZOS(2).VALOR := MI_RS.CODIGO_NODO_VARIABLE;

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => MI_ERROR_COD
                                  ,UN_REEMPLAZOS => MI_REEMPLAZOS);
      END;
    END LOOP;
  END PR_VALIDAR_INF_DETALLADA;

  --6
  PROCEDURE PR_ELIMINAR_TRAMITE 
  /*
    NAME              : PR_ELIMINAR_TRAMITE
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR CREATION   : PABLO ANDRES ESPITIA CUCA
    DATE CREATION     : 25/04/2018
    TIME              : 15:49
    SOURCE MODULE     : WORKFLOW (35)
    DESCRIPTION       : Elimina el tramite si no ha sido tramitado (hacer pase a), es decir solo tiene un detalle.
    MODIFIED BY       : 

    @NAME  : eliminarTramite
    @METHOD: POST
  */
  (
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,     -- Codigo de la compania.
    UN_PROCESO     IN PROCESOS.CODIGO%TYPE,         -- Codigo del proceso.
    UN_TIPOTRAMITE IN TIPOTRAMITES.TIPOTRAMITE%TYPE,-- Codigo del tipo de tramite.
    UN_TRAMITE     IN TRAMITES.NUMERO%TYPE          -- Numero del tramite.
  )
  AS 
    MI_RTA_ACME    PCK_SUBTIPOS.TI_RTA_ACME;
    MI_TABLA_DT    PCK_SUBTIPOS.TI_TABLA DEFAULT 'D_TRAMITES';
    MI_TABLA_DTV   PCK_SUBTIPOS.TI_TABLA DEFAULT 'D_TRAMITE_VARIABLES';
    MI_TABLA_PT    PCK_SUBTIPOS.TI_TABLA DEFAULT 'PROYECCIONES_TRAMITE';
    MI_CONDICION   PCK_SUBTIPOS.TI_CONDICION;
    MI_REEMPLAZOS  PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CONSECUTIVO D_TRAMITES.CONSECUTIVO%TYPE;
  BEGIN
    BEGIN
      BEGIN
        SELECT CONSECUTIVO
        INTO MI_CONSECUTIVO
        FROM D_TRAMITES
        WHERE COMPANIA     = UN_COMPANIA
          AND PROCESOS     = UN_PROCESO
          AND TIPO_TRAMITE = UN_TIPOTRAMITE
          AND NUMERO       = UN_TRAMITE;

      EXCEPTION WHEN TOO_MANY_ROWS THEN
                  RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
                WHEN NO_DATA_FOUND THEN
                  NULL; -- Borrar variables y detalles del tramite.
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_ELITRA_MSG_DSTRAMITE
                                ,UN_TABLAERROR => MI_TABLA_DT);      
    END;

    MI_CONDICION := 'COMPANIA            = '''||UN_COMPANIA   ||'''
                 AND CODIGO_PROCESO      = '''||UN_PROCESO    ||'''
                 AND TIPO_TRAMITE        = '''||UN_TIPOTRAMITE||'''
                 AND NUMERO_TRAMITE      =   '||UN_TRAMITE;

    -- Eliminar variables tramite
    BEGIN
      BEGIN
        MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_DTV
                                        ,UN_ACCION    => 'E'
                                        ,UN_CONDICION => MI_CONDICION);  

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN 
      MI_REEMPLAZOS(1).CLAVE := 'TRAMITE';
      MI_REEMPLAZOS(1).VALOR := UN_TRAMITE;
      MI_REEMPLAZOS(2).CLAVE := 'PROCESO';
      MI_REEMPLAZOS(2).VALOR := UN_PROCESO;
      MI_REEMPLAZOS(3).CLAVE := 'TIPO_TRAMITE';
      MI_REEMPLAZOS(3).VALOR := UN_TIPOTRAMITE;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_ELITRAMITE_E_VARDTRA
                                ,UN_TABLAERROR => MI_TABLA_DT
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;    

    MI_CONDICION := 'COMPANIA     = '''||UN_COMPANIA   ||'''
                 AND PROCESOS     = '''||UN_PROCESO    ||'''
                 AND TIPO_TRAMITE = '''||UN_TIPOTRAMITE||'''
                 AND NUMERO       =   '||UN_TRAMITE;

    -- Eliminar detalle tramite
    BEGIN
      BEGIN
        MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_DT
                                        ,UN_ACCION    => 'E'
                                        ,UN_CONDICION => MI_CONDICION);  

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN 
      MI_REEMPLAZOS(1).CLAVE := 'TRAMITE';
      MI_REEMPLAZOS(1).VALOR := UN_TRAMITE;
      MI_REEMPLAZOS(2).CLAVE := 'PROCESO';
      MI_REEMPLAZOS(2).VALOR := UN_PROCESO;
      MI_REEMPLAZOS(3).CLAVE := 'TIPO_TRAMITE';
      MI_REEMPLAZOS(3).VALOR := UN_TIPOTRAMITE;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_ELITRA_E_DETATRAMITE
                                ,UN_TABLAERROR => MI_TABLA_DT
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;

    MI_CONDICION := 'COMPANIA     = '||CHR(39)||UN_COMPANIA   ||CHR(39)
             ||' AND TIPO_TRAMITE = '||CHR(39)||UN_TIPOTRAMITE||CHR(39)
             ||' AND PROCESO      = '||CHR(39)||UN_PROCESO    ||CHR(39)
             ||' AND TRAMITE      = '         ||UN_TRAMITE;

    -- Eliminar proyecciones tramite
    BEGIN
      BEGIN
        MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_PT
                                        ,UN_ACCION    => 'E'
                                        ,UN_CONDICION => MI_CONDICION);  

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN 
      MI_REEMPLAZOS(1).CLAVE := 'TRAMITE';
      MI_REEMPLAZOS(1).VALOR := UN_TRAMITE;
      MI_REEMPLAZOS(2).CLAVE := 'PROCESO';
      MI_REEMPLAZOS(2).VALOR := UN_PROCESO;
      MI_REEMPLAZOS(3).CLAVE := 'TIPO_TRAMITE';
      MI_REEMPLAZOS(3).VALOR := UN_TIPOTRAMITE;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_ELITRA_E_PROYTRAMITE
                                ,UN_TABLAERROR => MI_TABLA_PT
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;
  END PR_ELIMINAR_TRAMITE;  

  --7
  PROCEDURE PR_PROYECTAR_NODO 
  /*
    NAME              : PR_PROYECTAR_NODO
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : PABLO ANDRES ESPITIA CUCA
    DATE MIGRATION    : 26/07/2018
    TIME              : 04:58 PM
    SOURCE MODULE     : WORKFLOW (35)
    DESCRIPTION       : Estima la duracion en dias y la fecha que puede tardar un nodo y sus hijos para cumplir su ciclo de vida.
    MODIFIED BY       : 

    @NAME  : proyectarNodo
    @METHOD: POST
  */
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,      -- Codigo de la compania.
    UN_TIPO_TRAMITE IN TIPOTRAMITES.TIPOTRAMITE%TYPE, -- Codigo del tipo de tramite.
    UN_PROCESO      IN PROCESOS.CODIGO%TYPE,          -- Codigo del proceso.
    UN_NODO         IN NODOS.CODIGO%TYPE,             -- Codigo del nodo.
    UN_NODO_ANT     IN NODOS.CODIGO%TYPE,             -- Codigo del nodo antecesor.
    UN_TRAMITE      IN TRAMITES.NUMERO%TYPE,          -- Numero del tramite.
    UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO        -- Codigo del usuario.
  )
  AS 
    MI_CANT              PCK_SUBTIPOS.TI_ENTERO;
    MI_CONSECUTIVO       PCK_SUBTIPOS.TI_LONG;
    MI_FECHAEST_NODO_ANT DATE; -- Fecha estimada del nodo antecesor.

    MI_RTA_ACME    PCK_SUBTIPOS.TI_RTA_ACME;
    MI_REEMPLAZOS  PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_TABLA_PT    PCK_SUBTIPOS.TI_TABLA DEFAULT 'PROYECCIONES_TRAMITE';
    MI_CAMPOS      PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES     PCK_SUBTIPOS.TI_VALORES;
  BEGIN
    -- Verificar que no exista la proyeccion para el nodo.
    SELECT COUNT(1) CANT 
    INTO MI_CANT
    FROM PROYECCIONES_TRAMITE
    WHERE COMPANIA     = UN_COMPANIA
      AND TIPO_TRAMITE = UN_TIPO_TRAMITE
      AND PROCESO      = UN_PROCESO
      AND TRAMITE      = UN_TRAMITE
      AND NODO         = UN_NODO;
    --  AND NODO_ANT     = UN_NODO_ANT;

    IF MI_CANT > 0 THEN
      RETURN;
    END IF;

    -- Recuperar fecha estimada del nodo antecesor.
    -- Cuando es el primer nodo, colocar SYSDATE.
    BEGIN
      SELECT ESTIMADO_FECHA 
      INTO MI_FECHAEST_NODO_ANT
      FROM PROYECCIONES_TRAMITE
      WHERE COMPANIA     = UN_COMPANIA
        AND TIPO_TRAMITE = UN_TIPO_TRAMITE
        AND PROCESO      = UN_PROCESO
        AND TRAMITE      = UN_TRAMITE
        AND NODO         = UN_NODO_ANT;

    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_FECHAEST_NODO_ANT := SYSDATE;
    END;

    MI_CAMPOS := 'COMPANIA
                 ,TIPO_TRAMITE
                 ,PROCESO
                 ,TRAMITE
                 ,CONSECUTIVO
                 ,NODO
                 ,NODO_ANT
                 ,ESTIMADO_DIAS
                 ,ESTIMADO_FECHA
                 ,CREATED_BY
                 ,DATE_CREATED';

    MI_VALORES := 'SELECT 
                     COMPANIA
                    ,'''||UN_TIPO_TRAMITE          ||'''
                    ,CODIGO_PROCESO
                    ,  '||UN_TRAMITE               ||'
                    ,(SELECT NVL(MAX(CONSECUTIVO),0) + 1
                      FROM PROYECCIONES_TRAMITE
                      WHERE COMPANIA     = '||CHR(39)||UN_COMPANIA    ||CHR(39)
                    ||' AND TIPO_TRAMITE = '||CHR(39)||UN_TIPO_TRAMITE||CHR(39)
                    ||' AND PROCESO      = '||CHR(39)||UN_PROCESO     ||CHR(39)
                    ||' AND TRAMITE      = '         ||UN_TRAMITE     ||')
                    ,CODIGO
                    ,  '||NVL(UN_NODO_ANT,'CODIGO')||'
                    ,TIEMPO_ESTIMADO
                    ,CASE WHEN TIPO_DIA IN(28)
                          THEN PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA => COMPANIA
                                                                ,UN_FECHA    => TO_DATE('''||TO_CHAR(MI_FECHAEST_NODO_ANT, 'DD/MM/YYYY')||''',''DD/MM/YYYY'')
                                                                ,UN_DIAS     => (TIEMPO_ESTIMADO + 1))
                          ELSE (TO_DATE('''||TO_CHAR(MI_FECHAEST_NODO_ANT, 'DD/MM/YYYY')||''',''DD/MM/YYYY'') + TIEMPO_ESTIMADO)
                     END
                    ,'''||UN_USUARIO               ||'''
                    ,SYSDATE
                   FROM NODOS
                   WHERE COMPANIA       = '''||UN_COMPANIA||'''
                     AND CODIGO_PROCESO = '''||UN_PROCESO ||'''
                     AND CODIGO         = '''||UN_NODO    ||'''';             

    BEGIN
      BEGIN
        MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA_PT
                                        ,UN_ACCION  => 'IS'
                                        ,UN_CAMPOS  => MI_CAMPOS
                                        ,UN_VALORES => MI_VALORES);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
      MI_REEMPLAZOS(1).CLAVE := 'NODO';
      MI_REEMPLAZOS(1).VALOR := UN_NODO;
      MI_REEMPLAZOS(2).CLAVE := 'TRAMITE';
      MI_REEMPLAZOS(2).VALOR := UN_TRAMITE;
      MI_REEMPLAZOS(3).CLAVE := 'PROCESO';
      MI_REEMPLAZOS(3).VALOR := UN_PROCESO;    

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_PRONOD_IS_PROYECNODO
                                ,UN_TABLAERROR => MI_TABLA_PT
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;

    <<RS_D_NODOS>>
    FOR RS_D_NODO IN(SELECT NODO_DESTINO
                     FROM D_NODOS
                     WHERE COMPANIA    = UN_COMPANIA
                       AND PROCESO     = UN_PROCESO
                       AND NODO_ORIGEN = UN_NODO) 
    LOOP
      PR_PROYECTAR_NODO(UN_COMPANIA     => UN_COMPANIA
                       ,UN_TIPO_TRAMITE => UN_TIPO_TRAMITE
                       ,UN_PROCESO      => UN_PROCESO
                       ,UN_NODO         => RS_D_NODO.NODO_DESTINO
                       ,UN_NODO_ANT     => UN_NODO
                       ,UN_TRAMITE      => UN_TRAMITE
                       ,UN_USUARIO      => UN_USUARIO);
    END LOOP RS_D_NODOS;

  END PR_PROYECTAR_NODO;  

  --8
  PROCEDURE PR_PROYECTAR_TRAMITE 
  /*
    NAME              : PR_PROYECTAR_TRAMITE
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : PABLO ANDRES ESPITIA CUCA
    DATE MIGRATION    : 26/07/2018
    TIME              : 09:11 AM
    SOURCE MODULE     : WORKFLOW (35)
    DESCRIPTION       : Estima la duracion en dias y la fecha que puede tardar un tramite para cumplir su ciclo de vida.
    MODIFIED BY       : 

    @NAME  : proyectarTramite
    @METHOD: POST
  */
  (
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,         -- Codigo de la compania.
    UN_TIPO_TRAMITE    IN TRAMITES.TIPO_TRAMITE%TYPE,       -- Codigo del tipo de tramite.
    UN_PROCESO         IN TRAMITES.PROCESOS%TYPE,           -- Codigo del proceso.
    UN_TRAMITE         IN TRAMITES.NUMERO%TYPE,             -- Numero del tramite.
    UN_NODO_ORIGEN     IN NODOS.CODIGO%TYPE,                -- Codigo del nodo origen desde el cual se inicia la proyeccion del tramite.
    UN_DIAS_REPROG     IN PCK_SUBTIPOS.TI_ENTERO DEFAULT 0, -- Dias a reprogramar en el nodo origen.
    UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO           -- Codigo del usuario que desencadena el proceso.
  )
  AS 
  BEGIN
    IF UN_DIAS_REPROG IN (0) THEN 
      PR_PROYECTAR_NODO(UN_COMPANIA     => UN_COMPANIA
                       ,UN_TIPO_TRAMITE => UN_TIPO_TRAMITE
                       ,UN_PROCESO      => UN_PROCESO
                       ,UN_NODO         => UN_NODO_ORIGEN
                       ,UN_NODO_ANT     => NULL
                       ,UN_TRAMITE      => UN_TRAMITE
                       ,UN_USUARIO      => UN_USUARIO);
    ELSE
      PR_REPROGRAMAR_NODO(UN_COMPANIA     => UN_COMPANIA
                         ,UN_TIPO_TRAMITE => UN_TIPO_TRAMITE
                         ,UN_PROCESO      => UN_PROCESO
                         ,UN_NODO         => UN_NODO_ORIGEN
                         ,UN_NODO_ANT     => NULL
                         ,UN_TRAMITE      => UN_TRAMITE
                         ,UN_DIAS_REPROG  => UN_DIAS_REPROG
                         ,UN_USUARIO      => UN_USUARIO);
    END IF;
  END PR_PROYECTAR_TRAMITE;  

  --9
  PROCEDURE PR_REPROGRAMAR_NODO 
  /*
    NAME              : PR_REPROGRAMAR_NODO
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : PABLO ANDRES ESPITIA CUCA
    DATE MIGRATION    : 30/07/2018
    TIME              : 08:57 AM
    SOURCE MODULE     : WORKFLOW (35) 
    DESCRIPTION       : Reprograma la proyeccion del nodo y en consecuencia del tramite.
    MODIFIED BY       : 

    @NAME  : reprogramarNodo
  */
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,         -- Codigo de la compania.
    UN_TIPO_TRAMITE IN TIPOTRAMITES.TIPOTRAMITE%TYPE,    -- Codigo del tipo de tramite.
    UN_PROCESO      IN PROCESOS.CODIGO%TYPE,             -- Codigo del proceso.
    UN_NODO         IN NODOS.CODIGO%TYPE,                -- Codigo del nodo.
    UN_NODO_ANT     IN NODOS.CODIGO%TYPE,                -- Codigo del nodo antecesor.
    UN_TRAMITE      IN TRAMITES.NUMERO%TYPE,             -- Numero del tramite.
    UN_DIAS_REPROG  IN PCK_SUBTIPOS.TI_ENTERO DEFAULT 0, -- Cantidad de dias a programar en el nodo.
    UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO           -- Codigo del usuario.
  )
  AS 
    MI_RTA_ACME   PCK_SUBTIPOS.TI_RTA_ACME;
    MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION;
    MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_TABLA_PT   PCK_SUBTIPOS.TI_TABLA DEFAULT 'PROYECCIONES_TRAMITE';
  BEGIN
    -- El codigo del nodo debe ser diferente del nodo anterior para evitar un bucle infinito.
    -- El nodo inicial tiene nodo igual a nodo anterior.
    IF UN_NODO IN (UN_NODO_ANT) THEN
      RETURN;
    END IF;

    MI_CAMPOS := 'ESTIMADO_FECHA = PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA => COMPANIA
                                                                    ,UN_FECHA    => ESTIMADO_FECHA
                                                                    ,UN_DIAS     => '||(UN_DIAS_REPROG + 1)||')
                 ,MODIFIED_BY    = '''||UN_USUARIO||'''
                 ,DATE_MODIFIED  = SYSDATE';

    MI_CONDICION := 'COMPANIA     = '''||UN_COMPANIA    ||'''
                 AND TIPO_TRAMITE = '''||UN_TIPO_TRAMITE||'''
                 AND PROCESO      = '''||UN_PROCESO     ||'''
                 AND TRAMITE      =   '||UN_TRAMITE     ||'
                 AND NODO         = '''||UN_NODO        ||'''
                 AND REAL_DIAS IS NULL
                 AND REAL_FECHA IS NULL';

    -- Actualizar la fecha estimada de las proyecciones con los nuevos dias programados.
    BEGIN
      BEGIN
        MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_PT
                                        ,UN_ACCION    => 'M'
                                        ,UN_CAMPOS    => MI_CAMPOS
                                        ,UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_PRONOD_M_PROYECCIONS
                                ,UN_TABLAERROR => MI_TABLA_PT
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;

    -- Proyecciones en donde el nodo anterior sea igual al nodo del parametro.
    <<RS_NODO_PROY>>
    FOR RS_PROY IN(SELECT NODO
                   FROM PROYECCIONES_TRAMITE
                   WHERE COMPANIA     = UN_COMPANIA
                     AND TIPO_TRAMITE = UN_TIPO_TRAMITE
                     AND PROCESO      = UN_PROCESO
                     AND TRAMITE      = UN_TRAMITE
                     AND NODO_ANT     = UN_NODO)
    LOOP
      PR_REPROGRAMAR_NODO(UN_COMPANIA     => UN_COMPANIA
                         ,UN_TIPO_TRAMITE => UN_TIPO_TRAMITE
                         ,UN_PROCESO      => UN_PROCESO
                         ,UN_NODO         => RS_PROY.NODO
                         ,UN_NODO_ANT     => UN_NODO
                         ,UN_TRAMITE      => UN_TRAMITE
                         ,UN_DIAS_REPROG  => UN_DIAS_REPROG
                         ,UN_USUARIO      => UN_USUARIO);
    END LOOP RS_NODO_PROY;
  END PR_REPROGRAMAR_NODO;

  --10
  PROCEDURE PR_PREPARAR_AMB_WF 
  /*
    NAME              : PR_PREPARAR_AMB_WF
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR CREATION   : PABLO ANDRES ESPITIA CUCA
    DATE CREATION     : 16/11/2018
    TIME              : 08:25
    SOURCE MODULE     : WORKFLOW (35)
    DESCRIPTION       : Prepara las condiciones iniciales para el modulo de worflow.
    MODIFIED BY       : 

    @NAME  : prepararAmbWF
    @METHOD: POST
  */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA -- Codigo de la compania
  )
  AS 
    MI_C_PROTRA     PROCEDENCIA_TRAMITE.CODIGO%TYPE DEFAULT PCK_DATOS.FC_CONS_PROCEDENCIA_TRAMITE; -- Almacena el valor de la constante procedencia tramite.
    MI_C_SERIEDOC   SERIEDOCUMENTAL.CODIGO%TYPE     DEFAULT PCK_DATOS.FC_CONS_SERIE_DOCUMENTAL;    -- Almacena el valor de la constante serie documental.

    MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
    MI_RTA_ACME     PCK_SUBTIPOS.TI_RTA_ACME;
    MI_REEMPLAZOS   PCK_SUBTIPOS.TI_CLAVEVALOR;

    MI_TABLA_PT     PCK_SUBTIPOS.TI_TABLA DEFAULT 'PROCEDENCIA_TRAMITE';
    MI_TABLA_SD     PCK_SUBTIPOS.TI_TABLA DEFAULT 'SERIEDOCUMENTAL';

    MI_CANT         PCK_SUBTIPOS.TI_ENTERO;
  BEGIN
    -- Proceso para adicionar la PROCEDENCIA_TRAMITE interna en todas las companias
    SELECT COUNT(1)
    INTO MI_CANT
    FROM PROCEDENCIA_TRAMITE
    WHERE COMPANIA = UN_COMPANIA
      AND CODIGO   = MI_C_PROTRA;

    IF MI_CANT IN(0) THEN
      MI_CAMPOS := 'COMPANIA
                   ,CODIGO
                   ,DIRECCION
                   ,DIRECCIONE_MAIL
                   ,NOMBRE
                   ,CREATED_BY
                   ,DATE_CREATED';

      MI_VALORES := 'SELECT 
                       CODIGO
                      ,'||CHR(39)||MI_C_PROTRA||CHR(39)||'
                      ,DIRECCION
                      ,NVL(DIRECCIONEMAIL,''internosysman@gmail.com'')
                      ,''PROCEDENCIA INTERNA ERP''
                      ,''ADMIN''
                      ,SYSDATE
                     FROM COMPANIA
                     WHERE CODIGO = '||CHR(39)||MI_C_PROTRA||CHR(39);

      BEGIN
        BEGIN
          MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA_PT
                                          ,UN_ACCION  => 'IS'
                                          ,UN_CAMPOS  => MI_CAMPOS
                                          ,UN_VALORES => MI_VALORES);  

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
        END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
        MI_REEMPLAZOS(1).CLAVE := 'PROCEDENCIA';
        MI_REEMPLAZOS(1).VALOR := MI_C_PROTRA;

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_PREAMBWF_IS_PROCETRA
                                  ,UN_TABLAERROR => MI_TABLA_PT
                                  ,UN_REEMPLAZOS => MI_REEMPLAZOS);    
      END;
    END IF;

    -- Proceso para adicionar la SERIEDOCUMENTAL interna en todas las companias
    SELECT COUNT(1)
    INTO MI_CANT
    FROM SERIEDOCUMENTAL
    WHERE COMPANIA = UN_COMPANIA
      AND CODIGO   = MI_C_SERIEDOC;

    IF MI_CANT IN(0) THEN
      MI_CAMPOS := 'COMPANIA
                   ,CODIGO
                   ,NOMBRE
                   ,ESTADO
                   ,DISPOSICION_FINAL
                   ,MEDIO_RECUPERACION
                   ,ALMACENAMIENTO_FISICO
                   ,ESTADO_FISICO
                   ,CREATED_BY
                   ,DATE_CREATED';

      MI_VALORES := 'SELECT 
                       CODIGO
                      ,'||CHR(39)||MI_C_SERIEDOC||CHR(39)||'
                      ,''SERIE DOCUMENTAL INTERNA ERP''
                      ,4
                      ,''NA''
                      ,''NA''
                      ,''NA''
                      ,''NA''
                      ,''ADMIN''
                      ,SYSDATE
                     FROM COMPANIA
                     WHERE CODIGO NOT IN(SELECT COMPANIA
                                         FROM SERIEDOCUMENTAL
                                         WHERE CODIGO = '||CHR(39)||MI_C_SERIEDOC||CHR(39)||')';

      BEGIN
        BEGIN
          MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA_SD
                                          ,UN_ACCION  => 'IS'
                                          ,UN_CAMPOS  => MI_CAMPOS
                                          ,UN_VALORES => MI_VALORES);  

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
        END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
        MI_REEMPLAZOS(1).CLAVE := 'SERIEDOC';
        MI_REEMPLAZOS(1).VALOR := MI_C_SERIEDOC;

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_PREAMBWF_IS_SERIEDOC
                                  ,UN_TABLAERROR => MI_TABLA_SD
                                  ,UN_REEMPLAZOS => MI_REEMPLAZOS);    
      END;  
    END IF;
  END PR_PREPARAR_AMB_WF;  

  --11
  PROCEDURE PR_TRAMITAR_DESDE_ERP 
  /*
    NAME              : PR_TRAMITAR_DESDE_ERP
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR CREATION   : PABLO ANDRES ESPITIA CUCA
    DATE CREATION     : 16/11/2018
    TIME              : 11:42
    SOURCE MODULE     : WORKFLOW (35)
    DESCRIPTION       : Permite tramitar desde un formulario que no necesariamente pertenezca al modulo de WORKFLOW 
                        y tenga una opcion de menu asociada.
    MODIFIED BY       : 

    @NAME  : tramitarDesdeERP
    @METHOD: POST
  */  
  (
    UN_COMPANIA              IN PCK_SUBTIPOS.TI_COMPANIA,           -- Codigo de la compania.
    UN_TIPO_TRAMITE          IN TRAMITES.TIPO_TRAMITE%TYPE,         -- Codigo del tipo de tramite.
    UN_PROCESO               IN TRAMITES.PROCESOS%TYPE,             -- Codigo del proceso.
    UN_TRAMITE               IN TRAMITES.NUMERO%TYPE,               -- Numero del tramite. 
    UN_DESCRIPCION           IN TRAMITES.DESCRIPCION%TYPE,          -- Descripcion del tramite.
    UN_NODO_DESTINO          IN NODOS.CODIGO%TYPE,                  -- Codigo del nodo a insertar en el tramite.
    UN_FECHA                 IN DATE DEFAULT SYSDATE,               -- Fecha en que se crea o tramita el tramite.
    UN_CODIGO_CALIDAD        IN TRAMITES.CODIGO_CALIDAD%TYPE,       -- Codigo de calidad del tramite 
    UN_USUARIO               IN PCK_SUBTIPOS.TI_USUARIO             -- Codigo del usuario que desencadena el proceso. 
  )
  AS 
    MI_CANT             PCK_SUBTIPOS.TI_ENTERO;
    MI_TRAMITE_INI      PCK_SUBTIPOS.TI_LOGICO DEFAULT 0;

    MI_COM_PAIS         COMPANIA.PAIS%TYPE;
    MI_COM_DEPARTAMENTO COMPANIA.DEPARTAMENTO%TYPE;
    MI_COM_CIUDAD       COMPANIA.CIUDAD%TYPE;

    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
    MI_RTA_ACME         PCK_SUBTIPOS.TI_RTA_ACME;
    MI_REEMPLAZOS       PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_TABLA_T          PCK_SUBTIPOS.TI_TABLA DEFAULT 'TRAMITES';
    MI_TABLA_PT         PCK_SUBTIPOS.TI_TABLA DEFAULT 'PROCEDENCIA_TRAMITE';

    MI_NODO_ORIGEN      NODOS.CODIGO%TYPE;

    MI_PROTRA_DIRECCION PROCEDENCIA_TRAMITE.DIRECCION%TYPE;

    MI_C_PROTRA         PROCEDENCIA_TRAMITE.CODIGO%TYPE DEFAULT PCK_DATOS.FC_CONS_PROCEDENCIA_TRAMITE; -- Almacena el valor de la constante interna procedencia tramite.
  BEGIN
    BEGIN
      SELECT 
       PAIS
      ,DEPARTAMENTO
      ,CIUDAD
      INTO 
       MI_COM_PAIS
      ,MI_COM_DEPARTAMENTO
      ,MI_COM_CIUDAD
      FROM COMPANIA
      WHERE CODIGO = UN_COMPANIA;
    END;

    PR_PREPARAR_AMB_WF(UN_COMPANIA => UN_COMPANIA);

    SELECT COUNT(1)
    INTO MI_CANT
    FROM TRAMITES
    WHERE COMPANIA     = UN_COMPANIA
      AND TIPO_TRAMITE = UN_TIPO_TRAMITE
      AND PROCESOS     = UN_PROCESO
      AND NUMERO       = UN_TRAMITE;

    -- Cuando el tramite no existe
    IF MI_CANT IN(0) THEN
      BEGIN
        BEGIN
          SELECT DIRECCION
          INTO MI_PROTRA_DIRECCION
          FROM PROCEDENCIA_TRAMITE
          WHERE COMPANIA = UN_COMPANIA
            AND CODIGO   = MI_C_PROTRA;

        EXCEPTION WHEN NO_DATA_FOUND THEN
          RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
        END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
        MI_REEMPLAZOS(1).CLAVE := 'PROCEDENCIA';
        MI_REEMPLAZOS(1).VALOR := MI_C_PROTRA;

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_TRADESERP_NDF_PROTRA
                                  ,UN_TABLAERROR => MI_TABLA_PT
                                  ,UN_REEMPLAZOS => MI_REEMPLAZOS);     
      END;

      MI_CAMPOS := 'COMPANIA
                   ,PROCESOS
                   ,TIPO_TRAMITE
                   ,NUMERO
                   ,USUARIO_INTERNO
                   ,USUARIO_EXTERNO
                   ,PAIS_ORIGEN
                   ,DEPARTAMENTO_ORIGEN
                   ,CIUDAD_ORIGEN
                   ,DESCRIPCION
                   ,NODO_ORIGEN
                   ,NODO_ACTUAL
                   ,CODIGO_CALIDAD
                   ,FECHA
                   ,DIRECCION_PROCEDENCIA
                   ,DIRECCION_DESTINO
                   ,CREATED_BY
                   ,DATE_CREATED';

      MI_VALORES := CHR(39)||UN_COMPANIA        ||CHR(39)||CHR(13)
             ||','||CHR(39)||UN_PROCESO         ||CHR(39)||CHR(13)
             ||','||CHR(39)||UN_TIPO_TRAMITE    ||CHR(39)||CHR(13)
             ||','||         UN_TRAMITE                  ||CHR(13)
             ||','||CHR(39)||UN_USUARIO         ||CHR(39)||CHR(13)
             ||','||CHR(39)||UN_USUARIO         ||CHR(39)||CHR(13)
             ||','||CHR(39)||MI_COM_PAIS        ||CHR(39)||CHR(13)
             ||','||CHR(39)||MI_COM_DEPARTAMENTO||CHR(39)||CHR(13)
             ||','||CHR(39)||MI_COM_CIUDAD      ||CHR(39)||CHR(13)
             ||','||CHR(39)||UN_DESCRIPCION     ||CHR(39)||CHR(13)
             ||','||CHR(39)||UN_NODO_DESTINO    ||CHR(39)||CHR(13)
             ||','||CHR(39)||UN_NODO_DESTINO    ||CHR(39)||CHR(13)
             ||','||CHR(39)||UN_CODIGO_CALIDAD  ||CHR(39)||CHR(13)
             ||',TO_DATE('||CHR(39)||TO_CHAR(UN_FECHA,'DD/MM/YYYY')||CHR(39)||',''DD/MM/YYYY'')'||CHR(13)
             ||','||CHR(39)||MI_PROTRA_DIRECCION||CHR(39)||CHR(13)
             ||','||CHR(39)||MI_PROTRA_DIRECCION||CHR(39)||CHR(13)
             ||','||CHR(39)||UN_USUARIO         ||CHR(39)||CHR(13)
             ||',SYSDATE';                  

      BEGIN
        BEGIN
          MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA_T
                                          ,UN_ACCION  => 'I'
                                          ,UN_CAMPOS  => MI_CAMPOS
                                          ,UN_VALORES => MI_VALORES);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
        END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
        MI_REEMPLAZOS(1).CLAVE := 'TRAMITE';
        MI_REEMPLAZOS(1).VALOR := UN_TRAMITE;
        MI_REEMPLAZOS(2).CLAVE := 'PROCESO';
        MI_REEMPLAZOS(2).VALOR := UN_PROCESO;
        MI_REEMPLAZOS(3).CLAVE := 'TIPO_TRAMITE';
        MI_REEMPLAZOS(3).VALOR := UN_TIPO_TRAMITE;  

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_TRADESERP_I_TRAMITE
                                  ,UN_TABLAERROR => MI_TABLA_T
                                  ,UN_REEMPLAZOS => MI_REEMPLAZOS);       
      END;

      MI_TRAMITE_INI := -1;
    END IF;

    -- Recuperar nodo origen del tramite actual.
    BEGIN
      BEGIN
        SELECT 
          NODO_ORIGEN
        INTO 
          MI_NODO_ORIGEN
        FROM TRAMITES
        WHERE COMPANIA     = UN_COMPANIA
          AND PROCESOS     = UN_PROCESO
          AND TIPO_TRAMITE = UN_TIPO_TRAMITE
          AND NUMERO       = UN_TRAMITE;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
      MI_REEMPLAZOS(1).CLAVE := 'TRAMITE';
      MI_REEMPLAZOS(1).VALOR := UN_TRAMITE;
      MI_REEMPLAZOS(2).CLAVE := 'PROCESO';
      MI_REEMPLAZOS(2).VALOR := UN_PROCESO;
      MI_REEMPLAZOS(3).CLAVE := 'TIPO';
      MI_REEMPLAZOS(3).VALOR := UN_TIPO_TRAMITE;  

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_TRAMITAR_NDF_TRAMITE
                                ,UN_TABLAERROR => MI_TABLA_T
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);          
    END;  

    PR_TRAMITAR(UN_COMPANIA        => UN_COMPANIA
               ,UN_PROCESO         => UN_PROCESO
               ,UN_TIPO_TRAMITE    => UN_TIPO_TRAMITE
               ,UN_NUMERO          => UN_TRAMITE
               ,UN_NODO_ORIGEN     => MI_NODO_ORIGEN
               ,UN_NODO_DESTINO    => UN_NODO_DESTINO
               ,UN_TRAMITE_INI     => MI_TRAMITE_INI
               ,UN_USUARIO_DESTINO => UN_USUARIO
               ,UN_USUARIO         => UN_USUARIO
               ,UN_PROYECCIONES    => 0);
  END PR_TRAMITAR_DESDE_ERP;

  PROCEDURE PR_TRAMITAR_A_PROCESO 
  (
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,                                -- Codigo de la compania.
    UN_PROCESO         IN TRAMITES.PROCESOS%TYPE,                                  -- Codigo del proceso.
    UN_TIPO_TRAMITE    IN TRAMITES.TIPO_TRAMITE%TYPE,                              -- Codigo del tipo de tramite.
    UN_NUMERO          IN TRAMITES.NUMERO%TYPE,                                    -- Numero del tramite.
    UN_CONSECUTIVO     IN D_TRAMITES.CONSECUTIVO%TYPE,                             -- Consecutivo del detalle del tramite del cual se genera el nuevo proceso.
    UN_NODO_ORIGEN     IN NODOS.CODIGO%TYPE,                                       -- Codigo del nodo/etapa inicial.
    UN_NODO_DESTINO    IN NODOS.CODIGO%TYPE,                                       -- Codigo del nodo/etapa destino.
    UN_USUARIO_DESTINO IN PCK_SUBTIPOS.TI_USUARIO, 
    UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO,                                 -- Codigo del usuario que desencadena este proceso.
    UN_PROYECCIONES    IN PCK_SUBTIPOS.TI_LOGICO DEFAULT -1                        -- Indicador que establece si se deben generar la proyecciones.
  ) AS
    MI_TABLA         VARCHAR2(200 CHAR);
    MI_CRITERIO      PCK_SUBTIPOS.TI_CONDICION;
    MI_TRAMITE_NUM   NUMBER;
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
    MI_RTA_ACME         PCK_SUBTIPOS.TI_RTA_ACME;
    MI_REEMPLAZOS       PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_USUARIO_DESTINO  PCK_SUBTIPOS.TI_USUARIO;                                 -- Codigo del usuario que recibe el tramite.
    MI_CONSECUTIVO      NUMBER;
    MI_NUMERACION_UNICA NUMBER;
    MI_ANIO             NUMBER;
    MI_INICIAL          NUMBER(20,0);
  BEGIN

   FOR MI_RS IN( 
                SELECT *
                FROM D_NODO_DISPARA
                WHERE COMPANIA     = UN_COMPANIA
                  AND PROCESO      = UN_PROCESO
                  AND NODO_ORIGEN  = UN_NODO_ORIGEN
                  AND NODO_DESTINO = UN_NODO_DESTINO
                  )
    LOOP


                SELECT NUMERACION_UNICA
                INTO MI_NUMERACION_UNICA
            FROM TIPOTRAMITES 
            WHERE COMPANIA = UN_COMPANIA 
              AND PROCESOS = MI_RS.PROCESO_DISPARA
              AND TIPOTRAMITE = MI_RS.TIPO_TRAMITE;

     MI_ANIO := EXTRACT(YEAR FROM SYSDATE);
     MI_INICIAL := MI_ANIO || '00001';
    IF MI_NUMERACION_UNICA IN (0) THEN
        MI_TABLA := 'TRAMITES';
        MI_CRITERIO := 'COMPANIA     = ''' || UN_COMPANIA     || '''
                    AND PROCESOS     = ''' || MI_RS.PROCESO_DISPARA || '''
                    AND TIPO_TRAMITE = ''' || MI_RS.TIPO_TRAMITE    || '''
                    AND LENGTH(NUMERO) = 9
                    AND NUMERO LIKE '''|| MI_ANIO ||'%'' ';
    ELSE 

        MI_TABLA := 'TRAMITES T 
                INNER JOIN TIPOTRAMITES TT 
                ON T.TIPO_TRAMITE=TT.TIPOTRAMITE 
                AND T.COMPANIA=TT.COMPANIA';

        MI_CRITERIO := 'T.COMPANIA = ''' || UN_COMPANIA     || ''' 
                    AND TT.NUMERACION_UNICA IN (0)
                    AND LENGTH(T.NUMERO) = 9
                    AND T.NUMERO LIKE '''|| MI_ANIO ||'%'' ';      
    END IF;

        MI_TRAMITE_NUM := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => MI_TABLA
                                                          ,UN_CRITERIO => MI_CRITERIO
                                                          ,UN_CAMPO    => 'NUMERO'
                                                          ,UN_INICIAL  => MI_INICIAL);
        MI_TABLA := 'TRAMITES';
        MI_CAMPOS := 'COMPANIA
                   ,PROCESOS
                   ,TIPO_TRAMITE
                   ,NUMERO
                   ,USUARIO_INTERNO
                   ,USUARIO_EXTERNO
                   ,PAIS_ORIGEN
                   ,DEPARTAMENTO_ORIGEN
                   ,CIUDAD_ORIGEN
                   ,DESCRIPCION
                   ,NODO_ORIGEN
                   ,NODO_ACTUAL
                   ,CODIGO_CALIDAD
                   ,FECHA
                   ,DIRECCION_PROCEDENCIA
                   ,DIRECCION_DESTINO
                   ,PROCEDENCIA
                   ,DESTINO
                   ,SERIE_DOCUMENTAL
                   ,CREATED_BY
                   ,DATE_CREATED';
        MI_VALORES := ' SELECT COMPANIA
                           ,''' || MI_RS.PROCESO_DISPARA || '''
                           ,''' || MI_RS.TIPO_TRAMITE    || '''
                           ,'   || MI_TRAMITE_NUM     || '                    
                           ,USUARIO_INTERNO
                           ,USUARIO_EXTERNO
                           ,PAIS_ORIGEN
                           ,DEPARTAMENTO_ORIGEN
                           ,CIUDAD_ORIGEN
                           ,DESCRIPCION
                           ,''' || MI_RS.NODO_DISPARA || '''
                           ,''' || MI_RS.NODO_DISPARA || '''
                           ,CODIGO_CALIDAD
                           ,TRUNC(SYSDATE)
                           ,DIRECCION_PROCEDENCIA
                           ,DIRECCION_DESTINO
                           ,PROCEDENCIA
                           ,DESTINO
                           ,SERIE_DOCUMENTAL
                           ,''' || UN_USUARIO || '''
                           ,SYSDATE
                        FROM TRAMITES
                        WHERE COMPANIA     = ''' || UN_COMPANIA     || '''
                          AND PROCESOS     = ''' || UN_PROCESO      || '''
                          AND TIPO_TRAMITE = ''' || UN_TIPO_TRAMITE || '''
                          AND NUMERO       = '   || UN_NUMERO;
        BEGIN
            BEGIN
                MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA
                                              ,UN_ACCION  => 'IS'
                                              ,UN_CAMPOS  => MI_CAMPOS
                                              ,UN_VALORES => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
            MI_REEMPLAZOS(1).CLAVE := 'TRAMITE';
            MI_REEMPLAZOS(1).VALOR := MI_TRAMITE_NUM;
            MI_REEMPLAZOS(2).CLAVE := 'PROCESO';
            MI_REEMPLAZOS(2).VALOR := MI_RS.PROCESO_DISPARA;
            MI_REEMPLAZOS(3).CLAVE := 'TIPO_TRAMITE';
            MI_REEMPLAZOS(3).VALOR := MI_RS.TIPO_TRAMITE; 
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                      ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_TRADESERP_I_TRAMITE
                                      ,UN_TABLAERROR => MI_TABLA
                                      ,UN_REEMPLAZOS => MI_REEMPLAZOS);      
        END;

        MI_TABLA := 'TRAMITE_VARIABLE';
        MI_CAMPOS := 'COMPANIA
                   ,PROCESO
                   ,TIPO_TRAMITE
                   ,TRAMITE
                   ,VARIABLE
                   ,ETIQUETA
                   ,TIPO_DATO
                   ,OBLIGATORIO
                   ,MANEJA_ADJUNTO
                   ,ADJUNTO
                   ,ADJUNTO_OBLIGATORIO
                   ,VALOR
                   ,VALOR_TEXTO
                   ,VALOR_FECHA
                   ,OCR
                   ,CREATED_BY
                   ,DATE_CREATED';
        MI_VALORES := ' SELECT COMPANIA
                           ,''' || MI_RS.PROCESO_DISPARA || '''
                           ,''' || MI_RS.TIPO_TRAMITE    || '''
                           ,'   || MI_TRAMITE_NUM     || '                    
                           ,VARIABLE
                           ,ETIQUETA
                           ,TIPO_DATO
                           ,OBLIGATORIO
                           ,MANEJA_ADJUNTO
                           ,ADJUNTO
                           ,ADJUNTO_OBLIGATORIO
                           ,VALOR
                           ,VALOR_TEXTO
                           ,VALOR_FECHA
                           ,OCR
                           ,''' || UN_USUARIO || '''
                           ,SYSDATE
                        FROM TRAMITE_VARIABLE
                        WHERE COMPANIA     = ''' || UN_COMPANIA     || '''
                          AND PROCESO      = ''' || UN_PROCESO      || '''
                          AND TIPO_TRAMITE = ''' || UN_TIPO_TRAMITE || '''
                          AND TRAMITE      = '   || UN_NUMERO;
        BEGIN
            BEGIN
                MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA
                                              ,UN_ACCION  => 'IS'
                                              ,UN_CAMPOS  => MI_CAMPOS
                                              ,UN_VALORES => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
            MI_REEMPLAZOS(1).CLAVE := 'TRAMITE';
            MI_REEMPLAZOS(1).VALOR := MI_TRAMITE_NUM;
            MI_REEMPLAZOS(2).CLAVE := 'PROCESO';
            MI_REEMPLAZOS(2).VALOR := MI_RS.PROCESO_DISPARA;
            MI_REEMPLAZOS(3).CLAVE := 'TIPO_TRAMITE';
            MI_REEMPLAZOS(3).VALOR := MI_RS.TIPO_TRAMITE; 
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                      ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_TRADESERP_I_TRAM_VA
                                      ,UN_TABLAERROR => MI_TABLA
                                      ,UN_REEMPLAZOS => MI_REEMPLAZOS);      
        END;

        SELECT MIN(U.USUARIO)
        INTO MI_USUARIO_DESTINO
        FROM NODO_RACI NR INNER JOIN ROLES R
          ON NR.COMPANIA   = R.COMPANIA
         AND NR.CODIGO_ROL = R.CODIGO_ROL 
         INNER JOIN ROL_USUARIO U 
          ON R.COMPANIA  = U.COMPANIA
         AND R.CODIGO_ROL= U.CODIGO_ROL 
        WHERE NR.COMPANIA       = UN_COMPANIA
          AND NR.CODIGO_PROCESO = MI_RS.PROCESO_DISPARA
          AND NR.ESTADO         = 4
          AND NR.CODIGO_NODO    = MI_RS.NODO_DISPARA;

  --(EAMAYA:02/10/2020) Se cambia el parametro UN_USUARIO_DESTINO MI_USUARIO_DESTINO
        PCK_WORKFLOW.PR_TRAMITAR(UN_COMPANIA        => UN_COMPANIA
                                ,UN_PROCESO         => MI_RS.PROCESO_DISPARA
                                ,UN_TIPO_TRAMITE    => MI_RS.TIPO_TRAMITE 
                                ,UN_NUMERO          => MI_TRAMITE_NUM
                                ,UN_NODO_ORIGEN     => MI_RS.NODO_DISPARA
                                ,UN_NODO_DESTINO    => MI_RS.NODO_DISPARA
                                ,UN_TRAMITE_INI     => -1
                                ,UN_USUARIO_DESTINO => MI_USUARIO_DESTINO
                                ,UN_USUARIO         => UN_USUARIO
                                ,UN_PROYECCIONES    => UN_PROYECCIONES);

        SELECT MAX(CONSECUTIVO)
        INTO MI_CONSECUTIVO
        FROM D_TRAMITES
        WHERE COMPANIA     = UN_COMPANIA
          AND PROCESOS     = MI_RS.PROCESO_DISPARA
          AND TIPO_TRAMITE = MI_RS.TIPO_TRAMITE 
          AND NUMERO       = MI_TRAMITE_NUM;


        MI_CAMPOS := 'COMPANIA
                    ,PROCESOS
                    ,TIPO_TRAMITE
                    ,NUMERO
                    ,CONSECUTIVO
                    ,COMPANIA_GEN
                    ,PROCESOS_GEN
                    ,TIPO_TRAMITE_GEN
                    ,NUMERO_GEN
                    ,CONSECUTIVO_GEN
                    ,NODO_ORIGEN
                    ,USUARIO_ORIGEN
                    ,NODO_ORIGEN_GEN
                    ,USUARIO_ORIGEN_GEN
                    ,FECHA
                    ,HORA
                    ,CREATED_BY
                    ,DATE_CREATED';

      MI_VALORES := CHR(39)||UN_COMPANIA           ||CHR(39)||CHR(13)
             ||','||CHR(39)||UN_PROCESO            ||CHR(39)||CHR(13)
             ||','||CHR(39)||UN_TIPO_TRAMITE       ||CHR(39)||CHR(13)
             ||','||         UN_NUMERO                      ||CHR(13)
             ||','||         UN_CONSECUTIVO                 ||CHR(13)
             ||','||CHR(39)||UN_COMPANIA           ||CHR(39)||CHR(13)
             ||','||CHR(39)||MI_RS.PROCESO_DISPARA ||CHR(39)||CHR(13)
             ||','||CHR(39)||MI_RS.TIPO_TRAMITE    ||CHR(39)||CHR(13)
             ||','||         MI_TRAMITE_NUM                 ||CHR(13)
             ||','||CHR(39)||MI_CONSECUTIVO        ||CHR(39)||CHR(13)
             ||','||CHR(39)||MI_RS.NODO_DISPARA    ||CHR(39)||CHR(13)
             ||','||CHR(39)||UN_USUARIO            ||CHR(39)||CHR(13)
             ||','||CHR(39)||MI_RS.NODO_DISPARA    ||CHR(39)||CHR(13)
             ||','||CHR(39)||MI_USUARIO_DESTINO    ||CHR(39)||CHR(13)  
             ||',SYSDATE'||CHR(13)  
             ||',TO_DATE(''30/12/1899 '||TO_CHAR(SYSDATE,'HH24:MI')||''',''DD/MM/YYYY HH24:MI:SS'')' || CHR(13)
             ||','||CHR(39)||UN_USUARIO         ||CHR(39)||CHR(13)
             ||',SYSDATE';
      MI_TABLA:='D_TRAMITES_GENERADO';  

      BEGIN

        BEGIN
          MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA
                                          ,UN_ACCION  => 'I'
                                          ,UN_CAMPOS  => MI_CAMPOS
                                          ,UN_VALORES => MI_VALORES);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
        END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
        MI_REEMPLAZOS(1).CLAVE := 'TRAMITE';
        MI_REEMPLAZOS(1).VALOR := UN_NUMERO;
        MI_REEMPLAZOS(2).CLAVE := 'PROCESO';
        MI_REEMPLAZOS(2).VALOR := UN_PROCESO;
        MI_REEMPLAZOS(3).CLAVE := 'TIPO_TRAMITE';
        MI_REEMPLAZOS(3).VALOR := UN_TIPO_TRAMITE;  

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_TRADESERP_I_TRAMITE
                                  ,UN_TABLAERROR => MI_TABLA
                                  ,UN_REEMPLAZOS => MI_REEMPLAZOS);       
      END;
    END LOOP;
  END PR_TRAMITAR_A_PROCESO;

  PROCEDURE PR_ACTUALIZAR_VARIABLE_WF 

/*
    NAME              : FC_ACTUALIZAR_VARIABLE_WF
    AUTHOR MIGRACION  : JHON FREDY HERNANDEZ CASTRO
    DATE MIGRADOR     : 16/12/2019                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : ACTUALIZA EL VALOR_TEXTO DE LA TABLA D_TRAMITE_VARIABLE SEGUN EL CODIGO_NODO_VARIABLE ENVIADO  
    */
(
    UN_VALOR_TEXTO          IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,               -- Valor correspondiente a la variable
    UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,                           -- Codigo de la compania.
    UN_CODIGO_NODO_VARIABLE IN D_TRAMITE_VARIABLES.CODIGO_NODO_VARIABLE%TYPE,      -- Codigo de la variable (010-090) proceso pqrs
    UN_TIPO_TRAMITE         IN D_TRAMITE_VARIABLES.TIPO_TRAMITE%TYPE,              -- Codigo de tipo tramite enviado para proceso pqrs por defecto 1
    UN_NUMERO_TRAMITE       IN D_TRAMITE_VARIABLES.NUMERO_TRAMITE%TYPE,            -- Numero de radicado
    UN_CODIGO_NODO          IN D_TRAMITE_VARIABLES.CODIGO_NODO%TYPE,               -- Codigo del nodo se envia por defecto 0000 para proceso pqrs 
    UN_CODIGO_PROCESO       IN D_TRAMITE_VARIABLES.CODIGO_PROCESO%TYPE,            -- Codigo de proceso por defecto 00000 para pqrs
    UN_CONSECUTIVO_TRAMITE  IN D_TRAMITE_VARIABLES.CONSECUTIVO_TRAMITE%TYPE,      -- Consecutivo de tramite por defecto se envia en 1
    UN_USUARIO              IN PCK_SUBTIPOS.TI_USUARIO                           
)
AS
    MI_TABLA         PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS        PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION     PCK_SUBTIPOS.TI_CONDICION;
    MI_RTA_ACME      VARCHAR2(1000);
BEGIN
    MI_TABLA := 'D_TRAMITE_VARIABLES';
    MI_CAMPOS := 'VALOR_TEXTO      = '''||UN_VALOR_TEXTO||'''
                 ,MODIFIED_BY      = '''||UN_USUARIO||'''
                 ,DATE_MODIFIED    = SYSDATE';

    MI_CONDICION := 'COMPANIA               = '''||UN_COMPANIA    ||'''
                 AND CODIGO_NODO_VARIABLE   = '''||UN_CODIGO_NODO_VARIABLE||'''
                 AND CODIGO_NODO            = '''||UN_CODIGO_NODO||'''
                 AND CODIGO_PROCESO         = '''||UN_CODIGO_PROCESO ||'''
                 AND TIPO_TRAMITE           = '''||UN_TIPO_TRAMITE||'''
                 AND NUMERO_TRAMITE         =  '||UN_NUMERO_TRAMITE||'
                 AND CONSECUTIVO_TRAMITE    =  '||UN_CONSECUTIVO_TRAMITE;
    BEGIN
        MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                        ,UN_ACCION    => 'M'
                                        ,UN_CAMPOS    => MI_CAMPOS
                                        ,UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
    END;

END PR_ACTUALIZAR_VARIABLE_WF;

FUNCTION FC_WORKFLOW_PQRS
  /*
    NAME              : FC_WORKFLOW_PQRS
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : JHON FREDY HERNANDEZ CASTRO
    DATE MIGRADOR     : 12/12/2019
    TIME              : 14:35 PM     
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       : RETORNA CONSECUTIVO DE RADICADO PARA INSERTAR TRAMITE INICIAL,Y ACTUALIZA VARIABLES PARA PQRS
    MODIFICATIONS     : 
    @NAME:  
*/
(
    UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA,                       -- Codigo de la compania.         
    UN_PROCESO                  IN TRAMITES.PROCESOS%TYPE,                         -- Codigo del proceso.
    UN_CONSECUTIVO              IN D_TRAMITES.CONSECUTIVO%TYPE,                    -- Consecutivo del detalle del tramite del cual se genera el nuevo proceso.         
    UN_NODO                     IN NODOS.CODIGO%TYPE,                              -- Codigo del nodo/etapa inicial.              
    UN_USUARIO                  IN PCK_SUBTIPOS.TI_USUARIO,                        -- Codigo del usuario que desencadena este proceso.         
    UN_PROYECCIONES             IN PCK_SUBTIPOS.TI_LOGICO DEFAULT -1,              -- Indicador que establece si se deben generar la proyecciones.
    UN_TIPO_TRAMITE             IN TRAMITES.TIPO_TRAMITE%TYPE,                     -- Codigo del tipo de tramite.         
    UN_IDENTIFICACION           IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,           -- variable (identificacion o cedula) de etapa 
    UN_NOMBRE                   IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,           -- variable nombre de etapa  
    UN_DIRECCION                IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,           -- variable direccion de etapa 
    UN_DESCRIPCION_SOLICITUD    IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,           -- variable descripcion de etapa 
    UN_EMAIL                    IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,           -- variable email de etapa 
    UN_ANEXO1                   IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,           -- variable anexo1 de etapa 
    UN_ANEXO2                   IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,           -- variable anexo2 de etapa 
    UN_ANEXO3                   IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,           -- variable anexo3 de etapa 
    UN_TIPO_TRAMITE_USUARIO     IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,            -- variable tipo tramite seleccionado por usuario de etapa  
    UN_TELEFONO                 IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,           -- variable telefono de etapa 
    UN_GENERO                   IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,           -- variable direccion de etapa 
    UN_CODIGO_TRAMITE           IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,           -- variable codigo de tipo de proceso 
    UN_RANGO_EDAD               IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,           -- variable rango de edad
    UN_TIPO_PERSONA             IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,           -- variable tipo de persona
    UN_TIPO_POBLACION           IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,           -- variable tipo de poblacion
    UN_VULNERABILIDAD           IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,           -- variable vulnerabilidad
    UN_OCUPACION                IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,           -- variable ocupacion
    UN_ESCOLARIDAD              IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,           -- variable escolaridad
    UN_DES_TIPO_POBLACION       IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,           -- variable descripcion tipo poblacion, cuando se escoga otros
    UN_DES_OCUPACION            IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,           -- variable descripcion ocupacion, cuando se escoga otros
    UN_ANONIMO                  IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0              -- Indicador que establece si el registro llega como anonimo.    
  ) RETURN PCK_SUBTIPOS.TI_ENTERO_LARGO AS
   MI_TABLA                 PCK_SUBTIPOS.TI_TABLA;
   MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
   MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;
   MI_VALORES               PCK_SUBTIPOS.TI_VALORES;
   MI_DEPARTAMENTO          COMPANIA.DEPARTAMENTO%TYPE;
   MI_RTA_ACME              VARCHAR2(1000);
   MI_PAIS                  COMPANIA.PAIS%TYPE; 
   MI_CIUDAD                COMPANIA.CIUDAD%TYPE;
   MI_NUMERO_RADICADO       TRAMITE_VARIABLE.TRAMITE%TYPE;
   MI_REEMPLAZOS            PCK_SUBTIPOS.TI_CLAVEVALOR;
   MI_ANIO                  PCK_SUBTIPOS.TI_ANIO;
   MI_NUM_UNICA             TIPOTRAMITES.NUMERACION_UNICA%TYPE; 
   MI_CODIGO_MAX            PROCEDENCIA_TRAMITE.CODIGO%TYPE;
   MI_PROCEDENCIA           PROCEDENCIA_TRAMITE.CODIGO%TYPE;
   MI_EXISTE_REGISTRO       VARCHAR2(2):='SI';
   MI_USUARIO               VARCHAR2(48);
   MI_DEPENDENCIA_INICIAL   VARCHAR2(48);
   MI_RANGO_EDAD            D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE;
   MI_TIPO_PERSONA          D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE;
   MI_TIPO_POBLACION        D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE;
   MI_VULNERABILIDAD        D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE;
   MI_OCUPACION             D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE;
   MI_ESCOLARIDAD           D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE;
   MI_TIPO_MEDIO            TIPO_MEDIO.CODIGO%TYPE;
   MI_PAR_ANONIMO           VARCHAR(2);
   MI_PROCED_ANONIMO        VARCHAR(100);


   BEGIN
    SELECT NUMERACION_UNICA INTO MI_NUM_UNICA 
       FROM  TIPOTRAMITES 
       WHERE TIPOTRAMITES.PROCESOS=UN_PROCESO
       AND TIPOTRAMITES.COMPANIA=UN_COMPANIA
       AND TIPOTRAMITES.TIPOTRAMITE=UN_CODIGO_TRAMITE;

    MI_ANIO := PCK_SYSMAN_UTL.FC_ANIO(SYSDATE);
    MI_PAR_ANONIMO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                                       ,UN_NOMBRE    => 'PERMITE ANONIMOS EN REGISTROS PQRS'
                                                       ,UN_MODULO    => '35'
                                                       ,UN_FECHA_PAR => SYSDATE);
    MI_PROCED_ANONIMO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                                       ,UN_NOMBRE    => 'PROCEDENCIA ANONIMO EN REGISTROS PQRS'
                                                       ,UN_MODULO    => '35'
                                                       ,UN_FECHA_PAR => SYSDATE);
    MI_DEPENDENCIA_INICIAL := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                                   ,UN_NOMBRE    => 'DEPENDENCIA INICIAL PARA PQRS'
                                                   ,UN_MODULO    => '35'
                                                   ,UN_FECHA_PAR => SYSDATE);

    MI_USUARIO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                                   ,UN_NOMBRE    => 'USUARIO QUE RECIBE PQRS'
                                                   ,UN_MODULO    => '35'
                                                   ,UN_FECHA_PAR => SYSDATE);

    MI_TIPO_POBLACION := CASE WHEN UN_TIPO_POBLACION = 'null' THEN 0 ELSE UN_TIPO_POBLACION END;

    IF MI_NUM_UNICA NOT IN(0) THEN 
    MI_NUMERO_RADICADO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'TRAMITES T INNER JOIN TIPOTRAMITES TT ON T.TIPO_TRAMITE=TT.TIPOTRAMITE AND T.COMPANIA=TT.COMPANIA',
                                                           UN_CRITERIO => 'T.COMPANIA    = '''|| UN_COMPANIA ||''' 
                                                                           AND TT.NUMERACION_UNICA NOT IN(0)', 
                                                           UN_CAMPO    => 'T.NUMERO',                                                         
                                                           UN_INICIAL  => MI_ANIO ||'00001');
    ELSE
    MI_NUMERO_RADICADO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'TRAMITES T INNER JOIN TIPOTRAMITES TT ON T.TIPO_TRAMITE=TT.TIPOTRAMITE AND T.COMPANIA=TT.COMPANIA',
                                                           UN_CRITERIO => 'T.COMPANIA    = '''|| UN_COMPANIA ||'''
                                                                           AND T.PROCESOS =  '''|| UN_PROCESO ||''' 
                                                                           AND T.TIPO_TRAMITE=  '''||  UN_CODIGO_TRAMITE ||'''', 

                                                           UN_CAMPO    => 'T.NUMERO',
                                                           UN_INICIAL  => MI_ANIO ||'00001');
    END IF;

    SELECT DEPARTAMENTO,PAIS,CIUDAD
    INTO MI_DEPARTAMENTO,MI_PAIS,MI_CIUDAD
    FROM COMPANIA
    WHERE CODIGO = UN_COMPANIA;
    BEGIN

    SELECT MAX(TO_NUMBER(CODIGO))+1
    INTO MI_CODIGO_MAX
    FROM PROCEDENCIA_TRAMITE ORDER BY  TO_NUMBER(CODIGO) DESC;
    END;
    BEGIN
    SELECT 'SI' 
        INTO MI_EXISTE_REGISTRO
        FROM PROCEDENCIA_TRAMITE 
        WHERE PROCEDENCIA_TRAMITE.DOCUMENTO=UN_IDENTIFICACION 
        AND COMPANIA=UN_COMPANIA AND ROWNUM<=1;    
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_EXISTE_REGISTRO := 'NO';
    END;
    
    BEGIN
    SELECT
    CODIGO
    INTO MI_TIPO_MEDIO
    FROM TIPO_MEDIO
    WHERE COMPANIA = UN_COMPANIA
    AND UPPER(TRANSLATE(NOMBRE,'ELECTR�NICO','ELECTRONICO')) LIKE UPPER('ELECTRONICO%');
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_TIPO_MEDIO := '';
    END;

    BEGIN 
		BEGIN 
				MI_TABLA := 'TRAMITES';
				MI_CAMPOS := 'COMPANIA, 
                    PROCESOS,
                    DEPENDENCIA,
                    TIPO_TRAMITE, 
                    NUMERO, 
                    NODO_ACTUAL,
                    NODO_ORIGEN,
                    DEPARTAMENTO_ORIGEN,
                    PAIS_ORIGEN,
                    CIUDAD_ORIGEN,
                    DESCRIPCION,
                    TIPO_MEDIO,
                    USUARIO_EXTERNO,
                    USUARIO_INTERNO,
                    CREATED_BY,
                    SERIE_DOCUMENTAL,
                    TIPO_POBLACION,
                    DATE_CREATED';
				MI_VALORES := ' '''||UN_COMPANIA||''',
                        '''||UN_PROCESO||''',
                        '''||MI_DEPENDENCIA_INICIAL||''',
                        '''||UN_CODIGO_TRAMITE||''',
                        '||MI_NUMERO_RADICADO||',
                        '''||UN_NODO||''',
                        '''||UN_NODO||''',
                        '''||MI_DEPARTAMENTO||''',
                        '''||MI_PAIS||''',
                        '''||MI_CIUDAD||''',
                        '''||UN_DESCRIPCION_SOLICITUD||''',
						'''||MI_TIPO_MEDIO||''',
                        '''||MI_USUARIO||''',
                        '''||MI_USUARIO||''',
                        '''||MI_USUARIO||''',
                        '''||'9999'||''',
                        '''||MI_TIPO_POBLACION||''',
                        SYSDATE';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    =>  MI_TABLA,
                                                UN_ACCION   =>  'I', 
                                                UN_CAMPOS   =>  MI_CAMPOS, 
                                                UN_VALORES  =>  MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
		END;
		 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
            MI_REEMPLAZOS(1).CLAVE := 'TIPO_TRAMITE';
            MI_REEMPLAZOS(1).VALOR := UN_CODIGO_TRAMITE;  --UN_TIPO_TRAMITE
            MI_REEMPLAZOS(2).CLAVE := 'PROCESO';
            MI_REEMPLAZOS(2).VALOR := UN_PROCESO;    

            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_INSERTAR_TRAMITE_PQR
                                ,UN_TABLAERROR => MI_TABLA
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS); 
	END;

    BEGIN
    PCK_WORKFLOW.PR_TRAMITAR( UN_COMPANIA          => UN_COMPANIA
                              ,UN_PROCESO          => UN_PROCESO
                              ,UN_TIPO_TRAMITE     => UN_CODIGO_TRAMITE 
                              ,UN_NUMERO           => MI_NUMERO_RADICADO
                              ,UN_NODO_ORIGEN      => UN_NODO
                              ,UN_NODO_DESTINO     => UN_NODO
                              ,UN_USUARIO_DESTINO  => MI_USUARIO
                              ,UN_USUARIO          => MI_USUARIO
                              ,UN_TRAMITE_INI      => -1);         
    END;
     IF (MI_EXISTE_REGISTRO='NO' AND MI_PAR_ANONIMO ='NO') THEN
    BEGIN
        BEGIN
    MI_TABLA := 'PROCEDENCIA_TRAMITE';
    MI_CAMPOS := 'COMPANIA,
            CODIGO,
            DOCUMENTO,
            CONTACTO,
            NOMBRE,
            DIRECCIONE_MAIL,
            DIRECCION,
            NIT,
            SUCURSAL,
            REPRESENTANTELEGAL,
            SUCURSAL_REPRESENTANTE,
            PUBLICO,
            EXTERNO,
            CONTRATISTA,
            TELEFONO,
            CREATED_BY,
            MODIFIED_BY,
            DATE_CREATED,
            DATE_MODIFIED';

    MI_VALORES := ' '''||UN_COMPANIA||''',
                    '''||MI_CODIGO_MAX||''',
                    '''||UN_IDENTIFICACION||''',
                    '''||NULL||''',
                    '''||UN_NOMBRE||''',
                    '''||UN_EMAIL||''',
                    '''||UN_DIRECCION||''',
                    '''||'999999999999999999'||''',
                    '''||'999'||''',
                    '''||'999999999999999999'||''',
                    '''||'999'||''',
                    '''||'0'||''',
                    '''||'0'||''',
                    '''||'0'||''',
                    '''||UN_TELEFONO||''',
                    '''||MI_USUARIO||''',
                    '''||MI_USUARIO||''',
                        SYSDATE,
                        SYSDATE';

                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    =>  MI_TABLA,
                                                UN_ACCION   =>  'I', 
                                                UN_CAMPOS   =>  MI_CAMPOS, 
                                                UN_VALORES  =>  MI_VALORES);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
                    END;
                    END;
        END IF;
        BEGIN
            --lvega se agrega validacion para registros de PQRS ANONIMO
            IF MI_PAR_ANONIMO = 'SI' AND UN_ANONIMO NOT IN (0) THEN
                MI_PROCEDENCIA := NVL(MI_PROCED_ANONIMO,MI_PROCEDENCIA);
            ELSE
             SELECT CODIGO INTO MI_PROCEDENCIA
              FROM PROCEDENCIA_TRAMITE
             WHERE DOCUMENTO=UN_IDENTIFICACION AND COMPANIA=UN_COMPANIA;
           END IF;
        END;

         BEGIN

        MI_TABLA := 'TRAMITES';
        MI_CAMPOS := 'PROCEDENCIA      = '''||MI_PROCEDENCIA||''' ';


        MI_CONDICION := 'COMPANIA     = '''||UN_COMPANIA ||'''
                     AND PROCESOS     = '''||UN_PROCESO||'''
                     AND TIPO_TRAMITE = '''||UN_CODIGO_TRAMITE||'''
                     AND NUMERO         = '||MI_NUMERO_RADICADO ||'';
                     END;

     BEGIN
        MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                        ,UN_ACCION    => 'M'
                                        ,UN_CAMPOS    => MI_CAMPOS
                                        ,UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
        END;   

   BEGIN
   
   IF NVL(UN_TIPO_POBLACION,'0') = '102' THEN
    MI_TIPO_POBLACION := UN_DES_TIPO_POBLACION;
   ELSE
    MI_TIPO_POBLACION := PCK_SYSMAN_UTL.FC_VALOR_TIPOS('NOMBRE',UN_TIPO_POBLACION);
   END IF;

   IF NVL(UN_OCUPACION,'0') = '101' THEN
    MI_OCUPACION := UN_DES_OCUPACION;
   ELSE
    MI_OCUPACION := PCK_SYSMAN_UTL.FC_VALOR_TIPOS('NOMBRE',UN_OCUPACION);
   END IF;
   
   MI_TIPO_PERSONA := PCK_SYSMAN_UTL.FC_VALOR_TIPOS('NOMBRE',UN_TIPO_PERSONA);
   MI_RANGO_EDAD := PCK_SYSMAN_UTL.FC_VALOR_TIPOS('NOMBRE',UN_RANGO_EDAD);
   MI_VULNERABILIDAD := PCK_SYSMAN_UTL.FC_VALOR_TIPOS('NOMBRE',UN_VULNERABILIDAD);   
   MI_ESCOLARIDAD:= PCK_SYSMAN_UTL.FC_VALOR_TIPOS('NOMBRE',UN_ESCOLARIDAD);   

    --ACTUALIZACION DE VARIABLE CODIGO_NODO_VARIABLE 010 IDENTIFICACION-CEDULA--
    /*
    PR_ACTUALIZAR_VARIABLE_WF(
        UN_VALOR_TEXTO          => UN_IDENTIFICACION,
        UN_COMPANIA             => UN_COMPANIA,
        UN_CODIGO_NODO_VARIABLE => '010',
        UN_TIPO_TRAMITE        => UN_TIPO_TRAMITE,
        UN_NUMERO_TRAMITE       => MI_NUMERO_RADICADO,
        UN_CODIGO_NODO          => UN_NODO,
        UN_CODIGO_PROCESO       => UN_PROCESO,
        UN_CONSECUTIVO_TRAMITE  => UN_CONSECUTIVO,
        UN_USUARIO => UN_USUARIO
    );
    --ACTUALIZACION DE VARIABLE 020 NOMBRE DE SOLICITANTE
    PR_ACTUALIZAR_VARIABLE_WF(
        UN_VALOR_TEXTO          => UN_NOMBRE,
        UN_COMPANIA             => UN_COMPANIA,
        UN_CODIGO_NODO_VARIABLE => '020',
        UN_TIPO_TRAMITE        => UN_TIPO_TRAMITE,
        UN_NUMERO_TRAMITE       => MI_NUMERO_RADICADO,
        UN_CODIGO_NODO          => UN_NODO,
        UN_CODIGO_PROCESO       => UN_PROCESO,
        UN_CONSECUTIVO_TRAMITE  => UN_CONSECUTIVO,
        UN_USUARIO => UN_USUARIO
    );
    --ACTUALIZACION DE VARIABLE 030 DIRECCION DE SOLICITANTE
    PR_ACTUALIZAR_VARIABLE_WF(
        UN_VALOR_TEXTO          => UN_DIRECCION,
        UN_COMPANIA             => UN_COMPANIA,
        UN_CODIGO_NODO_VARIABLE => '030',
        UN_TIPO_TRAMITE        => UN_TIPO_TRAMITE,
        UN_NUMERO_TRAMITE       => MI_NUMERO_RADICADO,
        UN_CODIGO_NODO          => UN_NODO,
        UN_CODIGO_PROCESO       => UN_PROCESO,
        UN_CONSECUTIVO_TRAMITE  => UN_CONSECUTIVO,
        UN_USUARIO => UN_USUARIO
    );
    --ACTUALIZACION DE VARIABLE 040 CORREEO_ELECTRONICO DE SOLICITANTE
    PR_ACTUALIZAR_VARIABLE_WF(
        UN_VALOR_TEXTO          => UN_EMAIL,
        UN_COMPANIA             => UN_COMPANIA,
        UN_CODIGO_NODO_VARIABLE => '040',
        UN_TIPO_TRAMITE        => UN_TIPO_TRAMITE,
        UN_NUMERO_TRAMITE       => MI_NUMERO_RADICADO,
        UN_CODIGO_NODO          => UN_NODO,
        UN_CODIGO_PROCESO       => UN_PROCESO,
        UN_CONSECUTIVO_TRAMITE  => UN_CONSECUTIVO,
        UN_USUARIO => UN_USUARIO
    );
    --ACTUALIZACION DE VARIABLE 050 DESCRIPCION DE SOLICITUD
    PR_ACTUALIZAR_VARIABLE_WF(
        UN_VALOR_TEXTO          => UN_DESCRIPCION_SOLICITUD,
        UN_COMPANIA             => UN_COMPANIA,
        UN_CODIGO_NODO_VARIABLE => '050',
        UN_TIPO_TRAMITE        => UN_TIPO_TRAMITE,
        UN_NUMERO_TRAMITE       => MI_NUMERO_RADICADO,
        UN_CODIGO_NODO          => UN_NODO,
        UN_CODIGO_PROCESO       => UN_PROCESO,
        UN_CONSECUTIVO_TRAMITE  => UN_CONSECUTIVO,
        UN_USUARIO => UN_USUARIO
    );
  --ACTUALIZACION DE VARIABLE 060 ANEXO1
    PR_ACTUALIZAR_VARIABLE_WF(
        UN_VALOR_TEXTO          => UN_ANEXO1,
        UN_COMPANIA             => UN_COMPANIA,
        UN_CODIGO_NODO_VARIABLE => '060',
        UN_TIPO_TRAMITE        => UN_TIPO_TRAMITE,
        UN_NUMERO_TRAMITE       => MI_NUMERO_RADICADO,
        UN_CODIGO_NODO          => UN_NODO,
        UN_CODIGO_PROCESO       => UN_PROCESO,
        UN_CONSECUTIVO_TRAMITE  => UN_CONSECUTIVO,
        UN_USUARIO => UN_USUARIO
    );
   --ACTUALIZACION DE VARIABLE 070 ANEXO2
    PR_ACTUALIZAR_VARIABLE_WF(
        UN_VALOR_TEXTO          => UN_ANEXO2,
        UN_COMPANIA             => UN_COMPANIA,
        UN_CODIGO_NODO_VARIABLE => '070',
        UN_TIPO_TRAMITE        => UN_TIPO_TRAMITE,
        UN_NUMERO_TRAMITE       => MI_NUMERO_RADICADO,
        UN_CODIGO_NODO          => UN_NODO,
        UN_CODIGO_PROCESO       => UN_PROCESO,
        UN_CONSECUTIVO_TRAMITE  => UN_CONSECUTIVO,
        UN_USUARIO => UN_USUARIO
    );
   --ACTUALIZACION DE VARIABLE 080 ANEXO3
    PR_ACTUALIZAR_VARIABLE_WF(
        UN_VALOR_TEXTO          => UN_ANEXO3,
        UN_COMPANIA             => UN_COMPANIA,
        UN_CODIGO_NODO_VARIABLE => '080',
        UN_TIPO_TRAMITE        => UN_TIPO_TRAMITE,
        UN_NUMERO_TRAMITE       => MI_NUMERO_RADICADO,
        UN_CODIGO_NODO          => UN_NODO,
        UN_CODIGO_PROCESO       => UN_PROCESO,
        UN_CONSECUTIVO_TRAMITE  => UN_CONSECUTIVO,
        UN_USUARIO => UN_USUARIO
    );
   --ACTUALIZACION DE VARIABLE 100 UN_TIPO_TRAMITE_USUARIO REGISTRADO POR EL USUARIO
    PR_ACTUALIZAR_VARIABLE_WF(
        UN_VALOR_TEXTO          => UN_TIPO_TRAMITE_USUARIO,
        UN_COMPANIA             => UN_COMPANIA,
        UN_CODIGO_NODO_VARIABLE => '100',
        UN_TIPO_TRAMITE        => UN_TIPO_TRAMITE,
        UN_NUMERO_TRAMITE       => MI_NUMERO_RADICADO,
        UN_CODIGO_NODO          => UN_NODO,
        UN_CODIGO_PROCESO       => UN_PROCESO,
        UN_CONSECUTIVO_TRAMITE  => UN_CONSECUTIVO,
        UN_USUARIO => UN_USUARIO
    );
    **/
    --ACTUALIZACION DE VARIABLE CODIGO_NODO_VARIABLE identificacion
    PR_ACT_VARIABLE_PROCESO_WF(
        UN_VALOR_TEXTO          => UN_IDENTIFICACION,
        UN_COMPANIA             => UN_COMPANIA,
        UN_CODIGO_NODO_VARIABLE => 'identificacion',
        UN_TIPO_TRAMITE        => UN_CODIGO_TRAMITE,
        UN_NUMERO_TRAMITE       => MI_NUMERO_RADICADO,
        UN_CODIGO_PROCESO       => UN_PROCESO,
        UN_USUARIO => UN_USUARIO
        );
    --ACTUALIZACION DE VARIABLE CODIGO_NODO_VARIABLE solicitante
    PR_ACT_VARIABLE_PROCESO_WF(
        UN_VALOR_TEXTO          => UN_NOMBRE,
        UN_COMPANIA             => UN_COMPANIA,
        UN_CODIGO_NODO_VARIABLE => 'solicitante',
        UN_TIPO_TRAMITE        => UN_CODIGO_TRAMITE,
        UN_NUMERO_TRAMITE       => MI_NUMERO_RADICADO,
        UN_CODIGO_PROCESO       => UN_PROCESO,
        UN_USUARIO => UN_USUARIO
        );
     --ACTUALIZACION DE VARIABLE CODIGO_NODO_VARIABLE direccion_solicitante 
    PR_ACT_VARIABLE_PROCESO_WF(
        UN_VALOR_TEXTO          => UN_DIRECCION,
        UN_COMPANIA             => UN_COMPANIA,
        UN_CODIGO_NODO_VARIABLE => 'direccionsolicitante',
        UN_TIPO_TRAMITE        => UN_CODIGO_TRAMITE,
        UN_NUMERO_TRAMITE       => MI_NUMERO_RADICADO,
        UN_CODIGO_PROCESO       => UN_PROCESO,
        UN_USUARIO => UN_USUARIO
        );
     --ACTUALIZACION DE VARIABLE CODIGO_NODO_VARIABLE 010 IDENTIFICACION-CEDULA--   
    PR_ACT_VARIABLE_PROCESO_WF(
        UN_VALOR_TEXTO          => UN_EMAIL,
        UN_COMPANIA             => UN_COMPANIA,
        UN_CODIGO_NODO_VARIABLE => 'email',
        UN_TIPO_TRAMITE        => UN_CODIGO_TRAMITE,
        UN_NUMERO_TRAMITE       => MI_NUMERO_RADICADO,
        UN_CODIGO_PROCESO       => UN_PROCESO,
        UN_USUARIO => UN_USUARIO
        );
     --ACTUALIZACION DE VARIABLE CODIGO_NODO_VARIABLE descripcion_solicitud  
    PR_ACT_VARIABLE_PROCESO_WF(
        UN_VALOR_TEXTO          => UN_DESCRIPCION_SOLICITUD,
        UN_COMPANIA             => UN_COMPANIA,
        UN_CODIGO_NODO_VARIABLE => 'descripcionsolicitud',
        UN_TIPO_TRAMITE        => UN_CODIGO_TRAMITE,
        UN_NUMERO_TRAMITE       => MI_NUMERO_RADICADO,
        UN_CODIGO_PROCESO       => UN_PROCESO,
        UN_USUARIO => UN_USUARIO
        );
     --ACTUALIZACION DE VARIABLE CODIGO_NODO_VARIABLE 010 IDENTIFICACION-CEDULA--   
    PR_ACT_VARIABLE_PROCESO_WF(
        UN_VALOR_TEXTO          => UN_ANEXO1,
        UN_COMPANIA             => UN_COMPANIA,
        UN_CODIGO_NODO_VARIABLE => 'anexouno',
        UN_TIPO_TRAMITE        => UN_CODIGO_TRAMITE,
        UN_NUMERO_TRAMITE       => MI_NUMERO_RADICADO,
        UN_CODIGO_PROCESO       => UN_PROCESO,
        UN_USUARIO => UN_USUARIO
        );
    --ACTUALIZACION DE VARIABLE CODIGO_NODO_VARIABLE anexo_tres
     PR_ACT_VARIABLE_PROCESO_WF(
        UN_VALOR_TEXTO          => UN_ANEXO2,
        UN_COMPANIA             => UN_COMPANIA,
        UN_CODIGO_NODO_VARIABLE => 'anexodos',
        UN_TIPO_TRAMITE        => UN_CODIGO_TRAMITE,
        UN_NUMERO_TRAMITE       => MI_NUMERO_RADICADO,
        UN_CODIGO_PROCESO       => UN_PROCESO,
        UN_USUARIO => UN_USUARIO
        );
    --ACTUALIZACION DE VARIABLE CODIGO_NODO_VARIABLE anexo_dos
    PR_ACT_VARIABLE_PROCESO_WF(
        UN_VALOR_TEXTO          => UN_ANEXO3,
        UN_COMPANIA             => UN_COMPANIA,
        UN_CODIGO_NODO_VARIABLE => 'anexotres',
        UN_TIPO_TRAMITE        => UN_CODIGO_TRAMITE,
        UN_NUMERO_TRAMITE       => MI_NUMERO_RADICADO,
        UN_CODIGO_PROCESO       => UN_PROCESO,
        UN_USUARIO => UN_USUARIO
        );
    --ACTUALIZACION DE VARIABLE CODIGO_NODO_VARIABLE tramite_usuario
     PR_ACT_VARIABLE_PROCESO_WF(
        UN_VALOR_TEXTO          => UN_TIPO_TRAMITE_USUARIO,
        UN_COMPANIA             => UN_COMPANIA,
        UN_CODIGO_NODO_VARIABLE => 'tramiteusuario',
        UN_TIPO_TRAMITE        => UN_CODIGO_TRAMITE,
        UN_NUMERO_TRAMITE       => MI_NUMERO_RADICADO,
        UN_CODIGO_PROCESO       => UN_PROCESO,
        UN_USUARIO => UN_USUARIO
        );
     --ACTUALIZACION DE VARIABLE CODIGO_NODO_VARIABLE genero 
     PR_ACT_VARIABLE_PROCESO_WF(
        UN_VALOR_TEXTO          => UN_GENERO,
        UN_COMPANIA             => UN_COMPANIA,
        UN_CODIGO_NODO_VARIABLE => 'genero',
        UN_TIPO_TRAMITE        => UN_CODIGO_TRAMITE,
        UN_NUMERO_TRAMITE       => MI_NUMERO_RADICADO,
        UN_CODIGO_PROCESO       => UN_PROCESO,
        UN_USUARIO => UN_USUARIO
        );
      --ACTUALIZACION DE VARIABLE CODIGO_NODO_VARIABLE telefono 
      PR_ACT_VARIABLE_PROCESO_WF(
        UN_VALOR_TEXTO          => UN_TELEFONO,
        UN_COMPANIA             => UN_COMPANIA,
        UN_CODIGO_NODO_VARIABLE => 'telefono',
        UN_TIPO_TRAMITE        => UN_CODIGO_TRAMITE,
        UN_NUMERO_TRAMITE       => MI_NUMERO_RADICADO,
        UN_CODIGO_PROCESO       => UN_PROCESO,
        UN_USUARIO => UN_USUARIO
    );
      --ACTUALIZACION DE VARIABLE CODIGO_NODO_VARIABLE tipo_persona
      PR_ACT_VARIABLE_PROCESO_WF(
        UN_VALOR_TEXTO          => MI_TIPO_PERSONA,
        UN_COMPANIA             => UN_COMPANIA,
        UN_CODIGO_NODO_VARIABLE => 'tipopersona',
        UN_TIPO_TRAMITE        => UN_CODIGO_TRAMITE,
        UN_NUMERO_TRAMITE       => MI_NUMERO_RADICADO,
        UN_CODIGO_PROCESO       => UN_PROCESO,
        UN_USUARIO => UN_USUARIO
    );
    --ACTUALIZACION DE VARIABLE CODIGO_NODO_VARIABLE tipo_poblacion
      PR_ACT_VARIABLE_PROCESO_WF(
        UN_VALOR_TEXTO          => MI_TIPO_POBLACION,
        UN_COMPANIA             => UN_COMPANIA,
        UN_CODIGO_NODO_VARIABLE => 'tipopoblacion',
        UN_TIPO_TRAMITE        => UN_CODIGO_TRAMITE,
        UN_NUMERO_TRAMITE       => MI_NUMERO_RADICADO,
        UN_CODIGO_PROCESO       => UN_PROCESO,
        UN_USUARIO => UN_USUARIO
    );
    --ACTUALIZACION DE VARIABLE CODIGO_NODO_VARIABLE rango_edad
      PR_ACT_VARIABLE_PROCESO_WF(
        UN_VALOR_TEXTO          => MI_RANGO_EDAD,
        UN_COMPANIA             => UN_COMPANIA,
        UN_CODIGO_NODO_VARIABLE => 'rangoedad',
        UN_TIPO_TRAMITE        => UN_CODIGO_TRAMITE,
        UN_NUMERO_TRAMITE       => MI_NUMERO_RADICADO,
        UN_CODIGO_PROCESO       => UN_PROCESO,
        UN_USUARIO => UN_USUARIO
    );
    --ACTUALIZACION DE VARIABLE CODIGO_NODO_VARIABLE vulnerabilidad
      PR_ACT_VARIABLE_PROCESO_WF(
        UN_VALOR_TEXTO          => MI_VULNERABILIDAD,
        UN_COMPANIA             => UN_COMPANIA,
        UN_CODIGO_NODO_VARIABLE => 'vulnerabilidad',
        UN_TIPO_TRAMITE        => UN_CODIGO_TRAMITE,
        UN_NUMERO_TRAMITE       => MI_NUMERO_RADICADO,
        UN_CODIGO_PROCESO       => UN_PROCESO,
        UN_USUARIO => UN_USUARIO
    );
    --ACTUALIZACION DE VARIABLE CODIGO_NODO_VARIABLE ocupacion
      PR_ACT_VARIABLE_PROCESO_WF(
        UN_VALOR_TEXTO          => MI_OCUPACION,
        UN_COMPANIA             => UN_COMPANIA,
        UN_CODIGO_NODO_VARIABLE => 'ocupacion',
        UN_TIPO_TRAMITE        => UN_CODIGO_TRAMITE,
        UN_NUMERO_TRAMITE       => MI_NUMERO_RADICADO,
        UN_CODIGO_PROCESO       => UN_PROCESO,
        UN_USUARIO => UN_USUARIO
    );
    --ACTUALIZACION DE VARIABLE CODIGO_NODO_VARIABLE escolaridad
      PR_ACT_VARIABLE_PROCESO_WF(
        UN_VALOR_TEXTO          => MI_ESCOLARIDAD,
        UN_COMPANIA             => UN_COMPANIA,
        UN_CODIGO_NODO_VARIABLE => 'escolaridad',
        UN_TIPO_TRAMITE        => UN_CODIGO_TRAMITE,
        UN_NUMERO_TRAMITE       => MI_NUMERO_RADICADO,
        UN_CODIGO_PROCESO       => UN_PROCESO,
        UN_USUARIO => UN_USUARIO
    );
    END;   
    RETURN MI_NUMERO_RADICADO;

END FC_WORKFLOW_PQRS;

PROCEDURE PR_CARGAR_SERIEDOCUMENTAL 
/*
    NAME              : PR_CARGAR_SERIEDOCUMENTAL
    AUTHOR MIGRACION  : BRAYAN SEBASTIAN CARDENAS AGUILAR
    DATE MIGRADOR     : 10/08/2020                                                                                              
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     :
    DESCRIPTION       : Procedimiento para el cargue masivo de serie documentales

    @NAME: cargarSerieDocumental
    @METHOD:  POST
    */
( 
  UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CADENASERIES IN CLOB,
  UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO
)  
AS 
MI_DATOS_FILA         PCK_SYSMAN_UTL.T_SPLIT;
MI_DATOS_COLUMNAS     PCK_SYSMAN_UTL.T_SPLIT;
MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
BEGIN

  MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENASERIES,
                                               UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);

  <<CARGAR_SERIE_DOCUMENTAL>>
  FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST 
  LOOP
    MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                      UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);

      MI_CAMPOS := 'COMPANIA,
                    CODIGO,
                    NOMBRE,
                    ESTADO,
                    FECHA_INICIAL,
                    FECHA_FINAL,
                    VIGENCIA,
                    TIEMPO_RETENCION_ARGESTION,
                    UNIDAD_TIEMPO_ARGESTION,
                    TIEMPO_RETENCION_ARCENTRAL,
                    UNIDAD_TIEMPO_ARCENTRAL,
                    TIEMPO_RETENCION_AHISTORICO,
                    UNIDAD_TIEMPO_AHISTORICO,
                    MEDIO_RECUPERACION,
                    ALMACENAMIENTO_FISICO,
                    ESTADO_FISICO,
                    PROCEDIMIENTOS_OBSERVACIONES,
                    CONSERVACION_TOTAL,
                    ELIMINACION,
                    MICROFILMACION,
                    SELECCION,                   
                    CREATED_BY,
                    DATE_CREATED';

    MI_VALORES := ''''|| UN_COMPANIA        ||'''
                  ,'''|| MI_DATOS_COLUMNAS(1) ||'''
                  ,'''|| MI_DATOS_COLUMNAS(2) ||'''
                  ,'''||CASE WHEN  MI_DATOS_COLUMNAS(13) IN ('A') THEN 4 ELSE 5 END ||'''
                  ,'''|| MI_DATOS_COLUMNAS(10) ||'''
                  ,'''|| MI_DATOS_COLUMNAS(11) ||'''
                  ,'''|| MI_DATOS_COLUMNAS(12) ||'''
                  ,  '|| NVL(MI_DATOS_COLUMNAS(3),'0') ||'
                  ,  ''A''
                  ,  '|| NVL(MI_DATOS_COLUMNAS(4),'0') ||'
                  ,  ''A''
                  ,  '|| 0 ||'
                  ,  ''A''
                  ,'''|| MI_DATOS_COLUMNAS(14) ||'''
                  ,'''|| MI_DATOS_COLUMNAS(15) ||'''
                  ,'''|| MI_DATOS_COLUMNAS(16) ||'''
                  ,'''|| MI_DATOS_COLUMNAS(9)  ||'''
                  ,'''|| CASE WHEN MI_DATOS_COLUMNAS(5)IN ('X') OR MI_DATOS_COLUMNAS(5)IN ('x') THEN '-1' ELSE '0' END ||'''
                  ,'''|| CASE WHEN MI_DATOS_COLUMNAS(6)IN ('X') OR MI_DATOS_COLUMNAS(6)IN ('x') THEN '-1' ELSE '0' END ||'''
                  ,'''|| CASE WHEN MI_DATOS_COLUMNAS(7)IN ('X') OR MI_DATOS_COLUMNAS(7)IN ('x') THEN '-1' ELSE '0' END ||'''
                  ,'''|| CASE WHEN MI_DATOS_COLUMNAS(8)IN ('X') OR MI_DATOS_COLUMNAS(8)IN ('x') THEN '-1' ELSE '0' END ||'''
                  ,'''|| UN_USUARIO ||'''
                  ,SYSDATE';
    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'SERIEDOCUMENTAL'
                                               ,UN_ACCION  => 'I'
                                               ,UN_CAMPOS  => MI_CAMPOS
                                               ,UN_VALORES => MI_VALORES);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    MI_MSGERROR(1).CLAVE := 'CUENTA';
              MI_MSGERROR(1).VALOR :=  MI_DATOS_COLUMNAS(1);
              MI_MSGERROR(2).CLAVE := 'ANIO';
              MI_MSGERROR(2).VALOR :=  MI_DATOS_COLUMNAS(12);
              PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD => PCK_ERRORES.ERR_WF_PR_CARGASERIEDOCUMENTAL,
              UN_REEMPLAZOS => MI_MSGERROR
               );
      END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
              MI_MSGERROR(1).CLAVE := 'CODIGO';
              MI_MSGERROR(1).VALOR :=  MI_DATOS_COLUMNAS(1);
              MI_MSGERROR(2).CLAVE := 'ANIO';
              MI_MSGERROR(2).VALOR :=  MI_DATOS_COLUMNAS(12);
              PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD => PCK_ERRORES.ERR_WF_PR_CARGASERIEDOCUMENTAL,
              UN_REEMPLAZOS => MI_MSGERROR
               );
    END;          

  END LOOP CARGAR_SERIE_DOCUMENTAL;
END PR_CARGAR_SERIEDOCUMENTAL;

PROCEDURE PR_CARGAR_TRAMITES
  /*
    NAME              : PR_CARGAR_TRAMITES
    AUTHOR MIGRACION  : BRAYAN SEBASTIAN CARDENAS AGUILAR
    DATE MIGRADOR     : 06/10/2020                                                                                              
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     :
    DESCRIPTION       : Procedimiento para el cargue masivo de tramites

    @NAME:    cargarTramites
    @METHOD:  POST
    */
( 
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CADENA_TRAMITE IN CLOB,
  UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
)  AS
MI_DATOS_FILA         PCK_SYSMAN_UTL.T_SPLIT;
MI_DATOS_COLUMNAS     PCK_SYSMAN_UTL.T_SPLIT;
MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
MI_PROCESO            PROCESOS.CODIGO%TYPE;
MI_TIPOTRAMITE        TIPOTRAMITES.TIPOTRAMITE%TYPE;
MI_PAIS               COMPANIA.PAIS%TYPE;
MI_DEPARTAMENTO       COMPANIA.DEPARTAMENTO%TYPE;
MI_CIUDAD             COMPANIA.CIUDAD%TYPE;
MI_RS 				  SYS_REFCURSOR;
MI_VAR_FECHA          VARCHAR2(500 CHAR);
MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
MI_CANTIDAD           NUMERIC(2,0);
MI_TRAMITE            VARCHAR2(500 CHAR);--TRAMITES.NUMERO%TYPE;
MI_EXISTE             PCK_SUBTIPOS.TI_ENTERO;
MI_PAR_MANEJA         PCK_SUBTIPOS.TI_PARAMETRO;
MI_ETAPA_ORIGEN       NODOS.CODIGO%TYPE;
MI_ETAPA_ACTUAL       NODOS.CODIGO%TYPE;
MI_COUNT_ETAPA_ORIGEN NUMERIC;
MI_COUNT_ETAPA_ACTUAL NUMERIC;
MI_REEMPLAZO          VARCHAR2(500 CHAR);

BEGIN

   MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENA_TRAMITE,
                                               UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);

  <<CARGAR_TRAMITES>>
  FOR MI_RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST 
  LOOP
    MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(MI_RS),
                                                      UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);
                                                      
       MI_TRAMITE := MI_DATOS_COLUMNAS(1);

        BEGIN
          BEGIN
             SELECT DISTINCT
            PROCESOS.CODIGO, 
            TIPOTRAMITES.TIPOTRAMITE
            INTO MI_PROCESO,MI_TIPOTRAMITE
            FROM PROCESOS
            INNER JOIN TRAMITES
             ON PROCESOS. COMPANIA = TRAMITES.COMPANIA
            AND PROCESOS.CODIGO    = TRAMITES.PROCESOS
            INNER JOIN TIPOTRAMITES
             ON TRAMITES.COMPANIA     = TIPOTRAMITES.COMPANIA
            AND TRAMITES.PROCESOS     = TIPOTRAMITES.PROCESOS
            AND TRAMITES.TIPO_TRAMITE = TIPOTRAMITES.TIPOTRAMITE
            WHERE PROCESOS.CODIGO =  MI_DATOS_COLUMNAS(2);
             EXCEPTION WHEN NO_DATA_FOUND THEN
          RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
        END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
        MI_MSGERROR(1).CLAVE := 'PROCESO';
        MI_MSGERROR(1).VALOR := MI_PROCESO;
        MI_MSGERROR(2).CLAVE := 'NOMBRE_PROCESO';
        MI_MSGERROR(2).VALOR := MI_DATOS_COLUMNAS(2);

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_NO_EXISTE_PROCESO
                                  ,UN_TABLAERROR => 'PROCESOS'
                                  ,UN_REEMPLAZOS => MI_MSGERROR);     
          END;
          
        /*            BEGIN
             SELECT DISTINCT
            COUNT('X')
            INTO MI_EXISTE
            FROM TRAMITES
             WHERE COMPANIA = UN_COMPANIA
               AND NUMERO = MI_TRAMITE
               AND PROCESOS = MI_PROCESO
               AND TIPO_TRAMITE = MI_TIPOTRAMITE;
             EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_EXISTE := 0;
        END;
    IF MI_EXISTE > 0 THEN
    BEGIN
     RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
        MI_MSGERROR(1).CLAVE := 'PROCESO';
        MI_MSGERROR(1).VALOR := MI_PROCESO;
        MI_MSGERROR(2).CLAVE := 'TRAMITE';
        MI_MSGERROR(2).VALOR := MI_TRAMITE;

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_EXISTE_TRAMITE
                                  ,UN_TABLAERROR => 'PROCESOS'
                                  ,UN_REEMPLAZOS => MI_MSGERROR);     
          END;
          END IF;*/
          
        MI_ETAPA_ORIGEN := MI_DATOS_COLUMNAS(4);
        MI_ETAPA_ACTUAL := MI_DATOS_COLUMNAS(6);

       --Se valida que las etapas tengan un dato asignado
      IF MI_ETAPA_ORIGEN IN (0) OR MI_ETAPA_ACTUAL IN (0) THEN
        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
              MI_MSGERROR(1).CLAVE := 'TRAMITE';
              MI_MSGERROR(1).VALOR :=  MI_DATOS_COLUMNAS(1);
              PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD => PCK_ERRORES.ERR_WF_PR_NO_EXISTEN_ETAPAS,
              UN_REEMPLAZOS => MI_MSGERROR
               );
        END;
        END IF;  
        
        
        SELECT DISTINCT COUNT(NODOS1.CODIGO) AS NODO_ORIGEN, 
        COUNT(NODOS2.CODIGO) AS NODO_ACTUAL 
        INTO MI_COUNT_ETAPA_ORIGEN,
             MI_COUNT_ETAPA_ACTUAL
        FROM PROCESOS 
        LEFT JOIN NODOS NODOS1
         ON PROCESOS.COMPANIA = NODOS1.COMPANIA
        AND PROCESOS.CODIGO = NODOS1.CODIGO_PROCESO
        AND MI_ETAPA_ORIGEN = NODOS1.CODIGO
        LEFT JOIN NODOS NODOS2
         ON PROCESOS.COMPANIA = NODOS2.COMPANIA
        AND PROCESOS.CODIGO = NODOS2.CODIGO_PROCESO
        AND MI_ETAPA_ACTUAL = NODOS2.CODIGO
        WHERE PROCESOS.COMPANIA = UN_COMPANIA
        AND PROCESOS.CODIGO = MI_PROCESO
        ORDER BY NODOS1.CODIGO,
        NODOS2.CODIGO;
        
        IF MI_COUNT_ETAPA_ORIGEN IN (0) OR MI_COUNT_ETAPA_ACTUAL IN (0) THEN 
        
        IF MI_COUNT_ETAPA_ORIGEN IN (0) AND MI_COUNT_ETAPA_ACTUAL IN (0) THEN   
        MI_REEMPLAZO := MI_ETAPA_ORIGEN|| ', '|| MI_ETAPA_ACTUAL;
        ELSIF MI_COUNT_ETAPA_ORIGEN IN (0) THEN 
        MI_REEMPLAZO := MI_ETAPA_ORIGEN;
        ELSIF  MI_COUNT_ETAPA_ACTUAL IN (0) THEN 
        MI_REEMPLAZO := MI_ETAPA_ACTUAL;
        END IF;
        
        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
              MI_MSGERROR(1).CLAVE := 'ETAPAS';
              MI_MSGERROR(1).VALOR :=  MI_REEMPLAZO;
              MI_MSGERROR(2).CLAVE := 'PROCESO';
              MI_MSGERROR(2).VALOR :=  MI_PROCESO;
              PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD => PCK_ERRORES.ERR_WF_NO_EXISTEN_ETAPAS,
              UN_REEMPLAZOS => MI_MSGERROR
               );
        END;
        END IF;
        --validar etapas

            SELECT 
            PAIS,
            DEPARTAMENTO,
            CIUDAD
            INTO MI_PAIS,
            MI_DEPARTAMENTO,
            MI_CIUDAD
            FROM COMPANIA
            WHERE CODIGO = UN_COMPANIA;
            
            
    SELECT COUNT(*) CANTIDAD
    INTO MI_CANTIDAD
    FROM TRAMITES
    WHERE COMPANIA = UN_COMPANIA
    AND PROCESOS = MI_PROCESO
    AND TIPO_TRAMITE = MI_TIPOTRAMITE
    AND NUMERO = MI_TRAMITE;

            
      IF MI_CANTIDAD IN (0) THEN 

      MI_CAMPOS := '  COMPANIA,
                      PROCESOS,
                      TIPO_TRAMITE,
                      NUMERO,
                      USUARIO_INTERNO,
                      USUARIO_EXTERNO,
                      PAIS_ORIGEN,
                      DEPARTAMENTO_ORIGEN,
                      CIUDAD_ORIGEN,
                      DESCRIPCION,
                      ESTADO,
                      NODO_ACTUAL,
                      NODO_ORIGEN,
                      FECHA_REAL,
                      FECHA,
                      HORA,
                      SERIE_DOCUMENTAL,
                      CODIGO_CALIDAD,
                      DESTINO,
                      CREATED_BY,
                      DATE_CREATED';

    MI_VALORES := ''''|| UN_COMPANIA           ||'''
                  ,'''|| MI_PROCESO            ||'''
                  ,'''|| MI_TIPOTRAMITE        ||'''
                  ,'''|| MI_DATOS_COLUMNAS(1)  ||'''
                  ,'''|| MI_DATOS_COLUMNAS(9)  ||'''
                  ,'''|| MI_DATOS_COLUMNAS(9)  ||'''
                  ,'''|| MI_PAIS               ||'''
                  ,'''|| MI_DEPARTAMENTO       ||'''
                  ,'''|| MI_CIUDAD             ||'''
                  ,'''|| MI_DATOS_COLUMNAS(8)  ||'''
                  ,'''|| CASE WHEN MI_DATOS_COLUMNAS(10) IN ('ACTIVO') THEN 4 ELSE 5 END ||'''
                  ,'''|| MI_DATOS_COLUMNAS(6) ||'''
                  ,'''|| MI_DATOS_COLUMNAS(4) ||'''
                  ,SYSDATE
                  ,SYSDATE
                  ,TO_CHAR(SYSDATE,''DD/MM/YYYY HH:MI:SS'')
                  ,''000.00.00''
                  ,''00''
                  ,''9999999999''
                  ,'''|| UN_USUARIO ||'''
                  ,SYSDATE';
    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'TRAMITES'
                                               ,UN_ACCION  => 'I'
                                               ,UN_CAMPOS  => MI_CAMPOS
                                               ,UN_VALORES => MI_VALORES);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
           RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
              MI_MSGERROR(1).CLAVE := 'TRAMITE';
              MI_MSGERROR(1).VALOR :=  MI_DATOS_COLUMNAS(1);
              PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD => PCK_ERRORES.ERR_WF_PR_CARGAR_TRAMITES,
              UN_REEMPLAZOS => MI_MSGERROR
               );

    END; 


      PCK_WORKFLOW.PR_TRAMITAR(UN_COMPANIA        => UN_COMPANIA
                              ,UN_PROCESO         => MI_PROCESO
                              ,UN_TIPO_TRAMITE    => MI_TIPOTRAMITE
                              ,UN_NUMERO          => MI_DATOS_COLUMNAS(1) 
                              ,UN_NODO_ORIGEN     => MI_DATOS_COLUMNAS(4)
                              ,UN_NODO_DESTINO    => MI_DATOS_COLUMNAS(6)
                              ,UN_TRAMITE_INI     => -1
                              ,UN_USUARIO_DESTINO => MI_DATOS_COLUMNAS(9)
                              ,UN_USUARIO         => MI_DATOS_COLUMNAS(9));
    ELSE 
    
    PCK_WORKFLOW.PR_TRAMITAR(UN_COMPANIA        => UN_COMPANIA
                              ,UN_PROCESO         => MI_PROCESO
                              ,UN_TIPO_TRAMITE    => MI_TIPOTRAMITE
                              ,UN_NUMERO          => MI_DATOS_COLUMNAS(1) 
                              ,UN_NODO_ORIGEN     => MI_DATOS_COLUMNAS(4)
                              ,UN_NODO_DESTINO    => MI_DATOS_COLUMNAS(6)
                              ,UN_TRAMITE_INI     => 0
                              ,UN_USUARIO_DESTINO => MI_DATOS_COLUMNAS(9)
                              ,UN_USUARIO         => MI_DATOS_COLUMNAS(9));
                              
    END IF;
    
        MI_PAR_MANEJA := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                                   ,UN_NOMBRE    => 'MANEJA VARIABLES DE PROCESO AL CARGAR TRAMITES'
                                                   ,UN_MODULO    => '35'
                                                   ,UN_FECHA_PAR => SYSDATE);
    
    IF MI_PAR_MANEJA IN ('SI') THEN

   FOR RS IN (SELECT VARIABLE AS CODIGO_VARIABLE
                FROM TRAMITE_VARIABLE
                WHERE COMPANIA = UN_COMPANIA
                AND TRAMITE = MI_DATOS_COLUMNAS(1)
                AND PROCESO = MI_PROCESO
                AND TIPO_TRAMITE = MI_TIPOTRAMITE) LOOP


    MI_TABLA     := 'TRAMITE_VARIABLE';

    MI_CAMPOS    := 'VALOR_TEXTO = CASE '''|| RS.CODIGO_VARIABLE ||''' WHEN ''nombre'' THEN '''|| MI_DATOS_COLUMNAS(11) ||'''
                                                                            WHEN ''nit'' THEN '''|| MI_DATOS_COLUMNAS(12) ||''' 
                                                                            WHEN ''matriculaInmobiliaria'' THEN '''|| MI_DATOS_COLUMNAS(13) ||'''
                                                                            WHEN ''codCatastral'' THEN '''|| MI_DATOS_COLUMNAS(14) ||'''
                                                                            WHEN ''direccion'' THEN '''|| MI_DATOS_COLUMNAS(15) ||''' END,
                    VALOR = CASE '''|| RS.CODIGO_VARIABLE ||'''        WHEN ''vigenciaInicial'' THEN '''|| SUBSTR(MI_DATOS_COLUMNAS(16),1,4) ||'''
                                                                            WHEN ''vigenciaFinal'' THEN '''|| SUBSTR(MI_DATOS_COLUMNAS(16),6,9) ||'''
                                                                            WHEN ''total'' THEN '''|| MI_DATOS_COLUMNAS(17) ||''' END,
                    DATE_MODIFIED = SYSDATE,
                    MODIFIED_BY   = '''||UN_USUARIO||'''';


       MI_CONDICION := 'COMPANIA = '''|| UN_COMPANIA ||'''
                    AND TRAMITE = '''|| MI_DATOS_COLUMNAS(1) ||'''
                    AND PROCESO = '''|| MI_PROCESO ||'''
                    AND VARIABLE = CASE '''|| RS.CODIGO_VARIABLE ||''' WHEN ''nombre'' THEN ''nombre''
                                                                                        WHEN ''nit'' THEN ''nit'' 
                                                                                        WHEN ''matriculaInmobiliaria'' THEN ''matriculaInmobiliaria''
                                                                                        WHEN ''codCatastral'' THEN ''codCatastral''
                                                                                        WHEN ''direccion'' THEN ''direccion''
                                                                                        WHEN ''vigenciaInicial'' THEN ''vigenciaInicial''
                                                                                        WHEN ''vigenciaFinal'' THEN ''vigenciaFinal''
                                                                                        WHEN ''total'' THEN ''total'' END';


   PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA
                                         ,UN_ACCION    => 'M'
                                         ,UN_CAMPOS    => MI_CAMPOS
                                         ,UN_CONDICION => MI_CONDICION);

    END LOOP; 
     END IF;

  END LOOP CARGAR_TRAMITES;
END PR_CARGAR_TRAMITES;

PROCEDURE PR_PREPARAR_VAR_PROCESO 
  /*
    NAME              : PR_PREPARAR_VAR_PROCESO
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR CREATION   : BRAYAN SEBASTIAN CARDENAS AGUILAR
    DATE CREATION     : 10/12/2020
    TIME              : 12:09 P.M
    SOURCE MODULE     : WORKFLOW (35)
    DESCRIPTION       : Adiciona las variables del proceso al tramite.
    MODIFIED BY       : 

    @NAME  : prepararVariablesProceso
    @METHOD: POST
  */
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,    -- Codigo de la compania.
    UN_PROCESO      IN TRAMITES.PROCESOS%TYPE,      -- Codigo del proceso.
    UN_TIPO_TRAMITE IN TRAMITES.TIPO_TRAMITE%TYPE,  -- Codigo del tipo de tramite.
    UN_TRAMITE      IN TRAMITES.NUMERO%TYPE,        -- Numero del tramite.
    UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO      -- Codigo del usuario que desencadena este proceso.
  )
  AS 
    MI_RTA_ACME   PCK_SUBTIPOS.TI_RTA_ACME;
    MI_TABLA_TV  PCK_SUBTIPOS.TI_TABLA DEFAULT 'TRAMITE_VARIABLE';
    MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES    PCK_SUBTIPOS.TI_VALORES;
    MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    BEGIN
      BEGIN

        MI_CAMPOS := 'COMPANIA,
                      VARIABLE,
                      PROCESO,
                      TIPO_TRAMITE,
                      TRAMITE,
                      ETIQUETA,
                      TIPO_DATO,
                      OBLIGATORIO,
                      MANEJA_ADJUNTO,
                      ADJUNTO_OBLIGATORIO,
                      CREATED_BY,
                      DATE_CREATED';

        -- Estado: Activo (4)
        MI_VALORES := 'SELECT DISTINCT
                         P.COMPANIA
                        ,P.CODIGO
                        ,P.CODIGO_PROCESO
                        ,'''||UN_TIPO_TRAMITE||'''
                        ,  '||UN_TRAMITE     ||'
                        ,P.ETIQUETA
                        ,P.TIPO_DATO
                        ,P.OBLIGATORIO
                        ,P.MANEJA_ADJUNTO
                        ,P.ADJUNTO_OBLIGATORIO
                        ,'''||UN_USUARIO     ||'''
                        ,SYSDATE
                       FROM PROCESO_VARIABLE P
                       LEFT JOIN TRAMITE_VARIABLE T
                        ON P.COMPANIA       = T.COMPANIA
                       AND P.CODIGO_PROCESO = T.PROCESO
                       AND P.CODIGO         = T.VARIABLE
                       WHERE p.COMPANIA       = '''||UN_COMPANIA||'''
                         AND p.CODIGO_PROCESO = '''||UN_PROCESO ||'''
                         AND p.ESTADO         = 4
                         AND P.CODIGO NOT IN ( SELECT VARIABLE 
                                                 FROM TRAMITE_VARIABLE P
                                                  WHERE p.COMPANIA = '''||UN_COMPANIA||'''
                                                 AND p.PROCESO = '''||UN_PROCESO||'''
                                                 AND P.TRAMITE = '||UN_TRAMITE||')';

        MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA_TV
                                        ,UN_ACCION  => 'IS'
                                        ,UN_CAMPOS  => MI_CAMPOS
                                        ,UN_VALORES => MI_VALORES);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
    END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
      MI_REEMPLAZOS(1).CLAVE := 'PROCESO';
      MI_REEMPLAZOS(1).VALOR := UN_PROCESO;


      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_PREVARTRA_IS_DTRAVAR
                                ,UN_TABLAERROR => MI_TABLA_TV
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;

  END PR_PREPARAR_VAR_PROCESO;  

    PROCEDURE PR_ACT_VARIABLE_PROCESO_WF 

/*
    NAME              : FC_ACTUALIZAR_VARIABLE_WF
    AUTHOR MIGRACION  : JHON FREDY HERNANDEZ CASTRO
    DATE MIGRADOR     : 16/12/2019                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : ACTUALIZA EL VALOR_TEXTO DE LA TABLA D_TRAMITE_VARIABLE SEGUN EL CODIGO_NODO_VARIABLE ENVIADO  
    */
(
    UN_VALOR_TEXTO          IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,               -- Valor correspondiente a la variable
    UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,                           -- Codigo de la compania.
    UN_CODIGO_NODO_VARIABLE IN D_TRAMITE_VARIABLES.CODIGO_NODO_VARIABLE%TYPE,      -- Codigo de la variable (010-090) proceso pqrs
    UN_TIPO_TRAMITE         IN D_TRAMITE_VARIABLES.TIPO_TRAMITE%TYPE,              -- Codigo de tipo tramite enviado para proceso pqrs por defecto 1
    UN_NUMERO_TRAMITE       IN D_TRAMITE_VARIABLES.NUMERO_TRAMITE%TYPE,            -- Numero de radicado
    UN_CODIGO_PROCESO       IN D_TRAMITE_VARIABLES.CODIGO_PROCESO%TYPE,            -- Codigo de proceso por defecto 00000 para pqrs
    UN_USUARIO              IN PCK_SUBTIPOS.TI_USUARIO                           
)
AS
    MI_TABLA         PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS        PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION     PCK_SUBTIPOS.TI_CONDICION;
    MI_RTA_ACME      VARCHAR2(1000);
BEGIN
    MI_TABLA := 'TRAMITE_VARIABLE';
    MI_CAMPOS := 'VALOR_TEXTO      = '''||UN_VALOR_TEXTO||'''
                 ,MODIFIED_BY      = '''||UN_USUARIO||'''
                 ,DATE_MODIFIED    = SYSDATE';

    MI_CONDICION := 'COMPANIA               = '''||UN_COMPANIA    ||'''
                 AND VARIABLE   = '''||UN_CODIGO_NODO_VARIABLE||'''
                 AND PROCESO         = '''||UN_CODIGO_PROCESO ||'''
                 AND TIPO_TRAMITE           = '''||UN_TIPO_TRAMITE||'''
                 AND TRAMITE         =  '||UN_NUMERO_TRAMITE||' ';
    BEGIN
        MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                        ,UN_ACCION    => 'M'
                                        ,UN_CAMPOS    => MI_CAMPOS
                                        ,UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
    END;
END PR_ACT_VARIABLE_PROCESO_WF;

PROCEDURE PR_ACT_INF_PROD 
  /*
    NAME              : PR_ACT_INF_PROD
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR CREATION   : SEBASTIAN CARDENAS
    DATE CREATION     : 
    TIME              : 
    SOURCE MODULE     : 
    DESCRIPTION       : 
    MODIFIED BY       : 

    @NAME  : actualizarProcedencia
    @METHOD: PUT
  */
(
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,      -- Codigo de la compania.
    UN_PROCESO         IN PROCESOS.CODIGO%TYPE,          -- Codigo del proceso.
    UN_NODO            IN NODOS.CODIGO%TYPE,          -- Codigo del proceso.
    UN_TIPO_TRAMITE    IN TIPOTRAMITES.TIPOTRAMITE%TYPE, -- Codigo del tipo de tramite.
    UN_TRAMITE         IN TRAMITES.NUMERO%TYPE,          -- Numero del tramite.
    UN_DIRECCION       IN PROCESOS.CODIGO%TYPE,          -- Codigo del proceso.
    UN_PROCEDENCIA     IN TIPOTRAMITES.TIPOTRAMITE%TYPE, 
    UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO        -- COdigo del usuario que desencadena el proceso (Responsable).
  )
  AS 
    MI_RTA_ACME         PCK_SUBTIPOS.TI_RTA_ACME;
    MI_TABLA_DT         PCK_SUBTIPOS.TI_TABLA DEFAULT 'D_TRAMITES';
    MI_TABLA_DTV        PCK_SUBTIPOS.TI_TABLA DEFAULT 'D_TRAMITE_VARIABLES';
    MI_TABLA_T          PCK_SUBTIPOS.TI_TABLA DEFAULT 'TRAMITES';
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
    MI_REEMPLAZOS       PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_USUARIO_ANT      PCK_SUBTIPOS.TI_USUARIO;
    MI_NOMBRE_PROCEDENCIA   PROCEDENCIA_TRAMITE.NOMBRE%TYPE;
    MI_EMAIL_PROCEDENCIA    PROCEDENCIA_TRAMITE.DIRECCIONE_MAIL%TYPE;
    MI_TELEFONO_PROCEDENCIA PROCEDENCIA_TRAMITE.TELEFONO%TYPE;
    MI_VALOR                VARCHAR2(300 CHAR);
BEGIN

          BEGIN
            SELECT NOMBRE,
            DIRECCIONE_MAIL,
            TELEFONO
            INTO MI_NOMBRE_PROCEDENCIA,
            MI_EMAIL_PROCEDENCIA,
            MI_TELEFONO_PROCEDENCIA
            FROM PROCEDENCIA_TRAMITE
            WHERE COMPANIA = UN_COMPANIA 
              AND CODIGO   = UN_PROCEDENCIA; 
    
          EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_NOMBRE_PROCEDENCIA   := '';
            MI_EMAIL_PROCEDENCIA    := '';
            MI_TELEFONO_PROCEDENCIA := '';
          END;


    IF MI_NOMBRE_PROCEDENCIA IS NOT NULL OR MI_EMAIL_PROCEDENCIA IS NOT NULL OR MI_TELEFONO_PROCEDENCIA IS NOT NULL THEN 
    
     FOR MI_RS IN(SELECT *
            FROM D_TRAMITE_VARIABLES
            WHERE  COMPANIA    = UN_COMPANIA
            AND CODIGO_PROCESO = UN_PROCESO
            AND CODIGO_NODO    = UN_NODO
            AND NUMERO_TRAMITE = UN_TRAMITE
            AND ETIQUETA NOT LIKE '%Adjunto%'
            ORDER BY 2)
    LOOP
    
     
    IF MI_RS.CODIGO_NODO_VARIABLE = '006' THEN
    MI_VALOR := MI_NOMBRE_PROCEDENCIA;
    ELSIF MI_RS.CODIGO_NODO_VARIABLE = '007' THEN 
    MI_VALOR := MI_EMAIL_PROCEDENCIA;
    ELSIF MI_RS.CODIGO_NODO_VARIABLE = '008' THEN 
    MI_VALOR := UN_DIRECCION;
    ELSIF MI_RS.CODIGO_NODO_VARIABLE = '009' THEN
    MI_VALOR := MI_TELEFONO_PROCEDENCIA;
    END IF;
        
    
     MI_CAMPOS := 'VALOR_TEXTO = '''||MI_VALOR||''' 
                 ,MODIFIED_BY     = '''||UN_USUARIO||'''
                 ,DATE_MODIFIED   = SYSDATE';

    MI_CONDICION := 'COMPANIA      = '''||UN_COMPANIA    ||'''
                 AND CODIGO_PROCESO = '''||UN_PROCESO     ||'''
                 AND CODIGO_NODO = '''||UN_NODO ||'''
                 AND TIPO_TRAMITE  = '''||UN_TIPO_TRAMITE||'''
                 AND NUMERO_TRAMITE =   '||UN_TRAMITE||'
                 AND CODIGO_NODO_VARIABLE = '''||MI_RS.CODIGO_NODO_VARIABLE||'''';
    
    BEGIN
      BEGIN
        MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_DTV
                                        ,UN_ACCION    => 'M'
                                        ,UN_CAMPOS    => MI_CAMPOS
                                        ,UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
      MI_REEMPLAZOS(1).CLAVE := 'TRAMITE';
      MI_REEMPLAZOS(1).VALOR := UN_TRAMITE;
      MI_REEMPLAZOS(2).CLAVE := 'TIPO_TRAMITE';
      MI_REEMPLAZOS(2).VALOR := UN_TIPO_TRAMITE;
      MI_REEMPLAZOS(3).CLAVE := 'PROCESO';
      MI_REEMPLAZOS(3).VALOR := UN_PROCESO;    

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_CAMEJE_M_USUARIO_INT
                                ,UN_TABLAERROR => MI_TABLA_T
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;  
    END LOOP;
    END IF;

END PR_ACT_INF_PROD;

PROCEDURE PR_PROCEDENCIA_A_ENVIARCORREO
  /*
   NAME 			: PR_PROCEDENCIA_A_ENVIARCORREO
   AUTHORS 			: SEBASTIAN CARDENAS
   AUTHOR MIGRACION	:
   DATE MIGRADOR	:
   TIME				:
   MODULO ORIGEN	: 
   DESCRIPTION		:
   MODIFIER			:
   DATE MODIFIED	:
   TIME				:
   MODIFICATIONS	:

    @NAME  : procEnviarCorreo
    @METHOD: PUT
  */
   (
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO          IN D_TRAMITE_VARIABLES.CODIGO_PROCESO%TYPE,
    UN_NODO             IN D_TRAMITE_VARIABLES.CODIGO_NODO%TYPE,
    UN_NODO_DESTINO     IN D_TRAMITE_VARIABLES.CODIGO_NODO%TYPE,
    UN_TIPO_TRAMITE     IN D_TRAMITE_VARIABLES.TIPO_TRAMITE%TYPE,
    UN_TRAMITE          IN D_TRAMITE_VARIABLES.NUMERO_TRAMITE%TYPE
   )
   AS
    MI_TABLA             VARCHAR2(200 CHAR);
    MI_CONSULTA          PCK_SUBTIPOS.TI_MERGEUSING;
    MI_ENLACE            PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_EXISTE            PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_RTA               VARCHAR2(32000 CHAR);
    MI_ENVIA_CORREO      PCK_SUBTIPOS.TI_LOGICO;
BEGIN
BEGIN
    SELECT DISTINCT
    NODOS.ENVIA_CORREO
    INTO MI_ENVIA_CORREO
    FROM D_TRAMITE_VARIABLES DTV
    INNER JOIN NODOS
     ON NODOS.COMPANIA = DTV.COMPANIA
    AND NODOS.CODIGO_PROCESO = DTV.CODIGO_PROCESO
    AND NODOS.CODIGO = DTV.CODIGO_NODO
    WHERE DTV.COMPANIA       = UN_COMPANIA
      AND DTV.CODIGO_PROCESO = UN_PROCESO
      AND DTV.CODIGO_NODO    = UN_NODO_DESTINO
      AND DTV.TIPO_TRAMITE   = UN_TIPO_TRAMITE
      AND DTV.NUMERO_TRAMITE = UN_TRAMITE;
       EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_ENVIA_CORREO   := 0;
      END;
      
 IF MI_ENVIA_CORREO NOT IN (0) THEN 
  BEGIN

BEGIN
  MI_TABLA := 'D_TRAMITE_VARIABLES';
  MI_CONSULTA := '(SELECT DTV.COMPANIA,
            DTV.CODIGO_PROCESO,
            DTV.CODIGO_NODO,
            DTV.TIPO_TRAMITE,
            DTV.NUMERO_TRAMITE,
            CODIGO_NODO_VARIABLE,
            VALOR_TEXTO,
            ADJUNTO
            FROM D_TRAMITE_VARIABLES DTV
            INNER JOIN NODOS
             ON NODOS.COMPANIA = DTV.COMPANIA
            AND NODOS.CODIGO_PROCESO = DTV.CODIGO_PROCESO
            AND NODOS.CODIGO = DTV.CODIGO_NODO
            WHERE  DTV.COMPANIA    = '''|| UN_COMPANIA ||'''
            AND DTV.CODIGO_PROCESO = '''|| UN_PROCESO ||'''
            AND DTV.TIPO_TRAMITE   = '''|| UN_TIPO_TRAMITE ||'''
            AND DTV.NUMERO_TRAMITE = '''|| UN_TRAMITE ||'''
            AND NODOS.PROCEDENCIA_AUT NOT IN (0))';

  MI_ENLACE := '    TABLA.COMPANIA           = VISTA.COMPANIA
                AND TABLA.CODIGO_PROCESO = VISTA.CODIGO_PROCESO
                AND TABLA.TIPO_TRAMITE     = VISTA.TIPO_TRAMITE
                AND TABLA.NUMERO_TRAMITE     = VISTA.NUMERO_TRAMITE
                AND TABLA.CODIGO_NODO_VARIABLE     = VISTA.CODIGO_NODO_VARIABLE';

  MI_EXISTE := 'UPDATE  SET
                TABLA.VALOR_TEXTO  =  VISTA.VALOR_TEXTO,
                TABLA.ADJUNTO      =  VISTA.ADJUNTO
                WHERE TABLA.CODIGO_NODO = '''|| UN_NODO_DESTINO||''' ' ;

  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA
                               ,UN_ACCION      => 'MM'
                               ,UN_MERGEUSING  => MI_CONSULTA
                               ,UN_MERGEENLACE => MI_ENLACE
                               ,UN_MERGEEXISTE => MI_EXISTE);

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
   RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;

   END;

   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
     PCK_ERR_MSG.RAISE_WITH_MSG(
     UN_EXC_COD =>SQLCODE
     ,UN_ERROR_COD=>PCK_ERRORES.ERRR_ALMACEN_MMAFECTACIONMOV
     );
  END;
 END IF;
END PR_PROCEDENCIA_A_ENVIARCORREO;

PROCEDURE PR_CARGAR_HISTORIAL_VAR 
/*
    NAME              : PR_CARGAR_HISTORIAL_VAR
    AUTHOR MIGRACION  : BRAYAN SEBASTIAN CARDENAS AGUILAR
    DATE MIGRADOR     : 06/10/2020                                                                                              
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     :
    DESCRIPTION       : Procedimiento para el cargue masivo del historial del tramite

    @NAME:    cargarHistorialVar
    @METHOD:  POST
    */
( 
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CADENA_TRAMITE IN CLOB,
  UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
)  AS
MI_DATOS_FILA         PCK_SYSMAN_UTL.T_SPLIT;
MI_DATOS_COLUMNAS     PCK_SYSMAN_UTL.T_SPLIT;
MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
MI_PROCESO            PROCESOS.CODIGO%TYPE;
MI_TIPOTRAMITE        TIPOTRAMITES.TIPOTRAMITE%TYPE;
MI_PAIS               COMPANIA.PAIS%TYPE;
MI_DEPARTAMENTO       COMPANIA.DEPARTAMENTO%TYPE;
MI_CIUDAD             COMPANIA.CIUDAD%TYPE;
MI_RS 				  SYS_REFCURSOR;
MI_VAR_FECHA          VARCHAR2(500 CHAR);
MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
MI_CANTIDAD           NUMERIC(2,0);
MI_TRAMITE            VARCHAR2(500 CHAR);--TRAMITES.NUMERO%TYPE;
MI_VALOR_TEXTO_PROC   VARCHAR2(500 CHAR);
MI_VALOR_FECHA_PROC   DATE;
MI_VALOR_NUM_PROC     VARCHAR2(500 CHAR);
MI_VALOR_TEXTO_NOD    VARCHAR2(500 CHAR);
MI_VALOR_ADJUNTO_NOD  VARCHAR2(900 CHAR);
MI_VALOR_FECHA_NOD    DATE;
MI_VALOR_NUM_NOD      VARCHAR2(500 CHAR);
MI_CONTEO             PCK_SUBTIPOS.TI_ENTERO := 0;
MI_TIPO_DATO          PCK_SUBTIPOS.TI_ENTERO;
MI_MANEJA_ADJUNTO     PCK_SUBTIPOS.TI_LOGICO;

BEGIN

   MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENA_TRAMITE,
                                               UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);

  <<CARGAR_TRAMITES>>
  FOR MI_RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST 
  LOOP
    MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(MI_RS),
                                                      UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);
                                                      
       MI_TRAMITE := MI_DATOS_COLUMNAS(1);

        BEGIN
          BEGIN
             SELECT DISTINCT
            PROCESOS.CODIGO, 
            TIPOTRAMITES.TIPOTRAMITE
            INTO MI_PROCESO,MI_TIPOTRAMITE
            FROM PROCESOS
            INNER JOIN TRAMITES
             ON PROCESOS. COMPANIA = TRAMITES.COMPANIA
            AND PROCESOS.CODIGO    = TRAMITES.PROCESOS
            INNER JOIN TIPOTRAMITES
             ON TRAMITES.COMPANIA     = TIPOTRAMITES.COMPANIA
            AND TRAMITES.PROCESOS     = TIPOTRAMITES.PROCESOS
            AND TRAMITES.TIPO_TRAMITE = TIPOTRAMITES.TIPOTRAMITE
            WHERE TRAMITES.COMPANIA = UN_COMPANIA
             AND PROCESOS.CODIGO =  MI_DATOS_COLUMNAS(2);
             EXCEPTION WHEN NO_DATA_FOUND THEN
          RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
        END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
        MI_MSGERROR(1).CLAVE := 'PROCESO';
        MI_MSGERROR(1).VALOR := MI_PROCESO;
        MI_MSGERROR(2).CLAVE := 'NOMBRE_PROCESO';
        MI_MSGERROR(2).VALOR := MI_DATOS_COLUMNAS(2);

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_WF_PR_NO_EXISTE_PROCESO
                                  ,UN_TABLAERROR => 'PROCESOS'
                                  ,UN_REEMPLAZOS => MI_MSGERROR);     
          END;

       --Se valida que las etapas tengan un dato asignado
      IF MI_DATOS_COLUMNAS(4) IN (0) THEN
        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_WORKFLOW;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_WORKFLOW THEN
              MI_MSGERROR(1).CLAVE := 'TRAMITE';
              MI_MSGERROR(1).VALOR :=  MI_DATOS_COLUMNAS(1);
              PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD => PCK_ERRORES.ERR_WF_PR_NO_EXISTEN_ETAPAS,
              UN_REEMPLAZOS => MI_MSGERROR
               );
        END;
        END IF;                                                    
        --validar etapas   
            
            
            
    FOR RS IN (SELECT VARIABLE AS CODIGO_VARIABLE
        FROM TRAMITE_VARIABLE
        WHERE COMPANIA = UN_COMPANIA
        AND TRAMITE = MI_DATOS_COLUMNAS(1)
        AND PROCESO = MI_PROCESO
        AND TIPO_TRAMITE = MI_TIPOTRAMITE) LOOP


    MI_TABLA     := 'TRAMITE_VARIABLE';
    
    MI_VALOR_TEXTO_PROC := CASE RS.CODIGO_VARIABLE WHEN 'nombre' THEN  MI_DATOS_COLUMNAS(6)
                                              WHEN 'nit' THEN  MI_DATOS_COLUMNAS(7) 
                                              WHEN 'direccion' THEN  MI_DATOS_COLUMNAS(10) 
                                              WHEN 'matriculaInmobiliaria' THEN MI_DATOS_COLUMNAS(9)
                                              WHEN 'codCatastral' THEN MI_DATOS_COLUMNAS(8)
                                              WHEN 'codEquivalente' THEN MI_DATOS_COLUMNAS(11) END;
        
      MI_VALOR_NUM_PROC := CASE RS.CODIGO_VARIABLE WHEN 'total' THEN MI_DATOS_COLUMNAS(13)
                                              WHEN 'vigenciaInicial' THEN SUBSTR(MI_DATOS_COLUMNAS(12),1,4)
                                              WHEN 'vigenciaFinal' THEN SUBSTR(MI_DATOS_COLUMNAS(12),6,9) END;

    MI_CAMPOS    := 'VALOR_TEXTO   = '''|| NVL(MI_VALOR_TEXTO_PROC,null) ||''' ,
                     VALOR         = '''|| NVL(MI_VALOR_NUM_PROC,null)   ||''',
                     DATE_MODIFIED = SYSDATE,
                     MODIFIED_BY   = '''||UN_USUARIO||'''';


       MI_CONDICION := 'COMPANIA = '''|| UN_COMPANIA ||'''
                    AND TRAMITE  = '''|| MI_DATOS_COLUMNAS(1) ||'''
                    AND PROCESO  = '''|| MI_PROCESO ||'''
                    AND VARIABLE = '''|| RS.CODIGO_VARIABLE ||'''';

   PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA
                                         ,UN_ACCION    => 'M'
                                         ,UN_CAMPOS    => MI_CAMPOS
                                         ,UN_CONDICION => MI_CONDICION);
                                         
   -- MI_CONTEO := MI_CONTEO + 1;                                         
    END LOOP;
BEGIN
          SELECT TIPO_DATO,
             MANEJA_ADJUNTO
          INTO MI_TIPO_DATO,
           MI_MANEJA_ADJUNTO
        FROM D_TRAMITE_VARIABLES
        WHERE COMPANIA = UN_COMPANIA
        AND CODIGO_PROCESO = MI_PROCESO
        AND NUMERO_TRAMITE = MI_DATOS_COLUMNAS(1)
        AND CODIGO_NODO = MI_DATOS_COLUMNAS(4)
        AND CODIGO_NODO_VARIABLE = MI_DATOS_COLUMNAS(14);
          EXCEPTION WHEN NO_DATA_FOUND THEN
         MI_TIPO_DATO := '';
           MI_MANEJA_ADJUNTO := '';
        END;
  
    
    IF MI_TIPO_DATO = 6 AND MI_MANEJA_ADJUNTO IN (0) THEN 
    
    MI_VALOR_NUM_NOD   :=  MI_DATOS_COLUMNAS(15);
    
    ELSIF MI_TIPO_DATO = 7 AND MI_MANEJA_ADJUNTO IN (0) THEN
        
    MI_VALOR_FECHA_NOD :=  MI_DATOS_COLUMNAS(15);
      
    ELSIF MI_TIPO_DATO = 8 AND MI_MANEJA_ADJUNTO IN (0)  THEN
    
    MI_VALOR_TEXTO_NOD := MI_DATOS_COLUMNAS(15);
    
    ELSIF MI_TIPO_DATO = 8 AND MI_MANEJA_ADJUNTO NOT IN (0) THEN
    
    MI_VALOR_ADJUNTO_NOD := MI_DATOS_COLUMNAS(15);
    
    END IF;
   
    MI_TABLA     := 'D_TRAMITE_VARIABLES';
    MI_CAMPOS    := 'VALOR_TEXTO   = '''|| NVL(MI_VALOR_TEXTO_NOD,'') ||''' ,
                     VALOR         = '''|| NVL(MI_VALOR_NUM_NOD,'')   ||''',
                     ADJUNTO       = '''|| NVL(MI_VALOR_ADJUNTO_NOD,'')   ||''',
                     DATE_MODIFIED = SYSDATE,
                     MODIFIED_BY   = '''||UN_USUARIO||'''';


       MI_CONDICION := 'COMPANIA             = '''|| UN_COMPANIA ||'''
                    AND CODIGO_PROCESO       = '''|| MI_PROCESO ||'''
                    AND NUMERO_TRAMITE       = '''|| MI_DATOS_COLUMNAS(1) ||'''
                    AND CODIGO_NODO          = '''|| MI_DATOS_COLUMNAS(4) ||'''
                    AND CODIGO_NODO_VARIABLE = '''|| MI_DATOS_COLUMNAS(14) ||'''';
                    
                    

   PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA
                                         ,UN_ACCION    => 'M'
                                         ,UN_CAMPOS    => MI_CAMPOS
                                         ,UN_CONDICION => MI_CONDICION); 
                                         
   MI_VALOR_NUM_NOD := NULL;
   MI_VALOR_FECHA_NOD := NULL;
   MI_VALOR_TEXTO_NOD :=NULL;
   

  END LOOP CARGAR_TRAMITES;
END PR_CARGAR_HISTORIAL_VAR;



FUNCTION FC_CALCULAR_COLOR 

/*
      NAME              : 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  :
      DATE MIGRADOR     : 
      TIME              : 
      SOURCE MODULE     :
      MODIFIER          :  
      DATE MODIFIED     :  
      TIME              : 
      DESCRIPTION       :                      
*/
(
UN_COMPANIA              IN VARCHAR2,
UN_FECHA_INICIO          IN DATE,
UN_FECHA_FINAL           IN DATE
)
RETURN VARCHAR2
AS
MI_DIAS_BASE    NUMBER(20) := 0;
MI_DIAS         NUMBER(20) := 0;
MI_SALIDA       NUMBER(20) := 0;
MI_COLOR        VARCHAR2(11);
BEGIN

 MI_DIAS_BASE :=  PCK_SYSMAN_UTL.FC_DIASHABIL(UN_COMPANIA, UN_FECHA_INICIO, UN_FECHA_FINAL, 0);
 
 MI_DIAS :=  PCK_SYSMAN_UTL.FC_DIASHABIL(UN_COMPANIA,UN_FECHA_INICIO, SYSDATE,0);
 
 MI_SALIDA := ROUND((MI_DIAS * 100)/MI_DIAS_BASE);
 
 IF MI_SALIDA < '50' THEN
 
 MI_COLOR := '#00CC66';
 
 ELSIF MI_SALIDA >= '50' AND MI_SALIDA <= '79' THEN
 
 MI_COLOR := '#3399FF';
 
 ELSIF MI_SALIDA >= '80' AND MI_SALIDA <= '99'  THEN
 
 MI_COLOR := '#FFFF33';
 
 ELSE
 
 MI_COLOR := '#FF0000';
 
 END IF;

RETURN MI_COLOR;
END FC_CALCULAR_COLOR;

FUNCTION FC_DIAS_PRG
 (
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,                                -- Codigo de la compania.
  --UN_PROCESO         IN TRAMITES.PROCESOS%TYPE,                                  -- Codigo del proceso.
  --UN_TIPO_TRAMITE    IN TRAMITES.TIPO_TRAMITE%TYPE,                              -- Codigo del tipo de tramite.
    UN_TRAMITE         IN TRAMITES.NUMERO%TYPE--,                                     -- Numero del tramite. 
  --UN_NODO_ORIGEN     IN NODOS.CODIGO%TYPE
  )
RETURN NUMBER 
AS 
MI_FECHA_PRG DATE;
MI_FECHA DATE;
MI_DIAS_PRG NUMBER(4);
BEGIN
   BEGIN
    SELECT FECHA_PRORROGA, FECHA,
    PCK_SYSMAN_UTL.FC_DIASHABIL(T.COMPANIA,FECHA_PRORROGA, FECHA)
    INTO MI_FECHA_PRG,
    MI_FECHA,
    MI_DIAS_PRG
    FROM TRAMITES T
    INNER JOIN NODOS NP
     ON T.COMPANIA = NP.COMPANIA 
    AND T.PROCESOS = NP.CODIGO_PROCESO
    AND T.NODO_ORIGEN = NP.CODIGO
    WHERE T.COMPANIA = UN_COMPANIA
    AND T.NUMERO = UN_TRAMITE
    AND NP.PRORROGA NOT IN (0);
 EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_DIAS_PRG := 0;
  END;  

  RETURN MI_DIAS_PRG;
END FC_DIAS_PRG;

END PCK_WORKFLOW;