create or replace PACKAGE PCK_WORKFLOW AS 

  /* TODO enter package declarations (types, exceptions, methods etc) here */ 

  --1
  PROCEDURE PR_CERRAR_TRAMITE 
  (
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,                                -- Codigo de la compania.
    UN_PROCESO         IN TRAMITES.PROCESOS%TYPE,                                  -- Codigo del proceso.
    UN_TIPO_TRAMITE    IN TRAMITES.TIPO_TRAMITE%TYPE,                              -- Codigo del tipo de tramite.
    UN_TRAMITE         IN TRAMITES.NUMERO%TYPE,                                    -- Numero del tramite. 
    UN_NODO_ACTUAL     IN NODOS.CODIGO%TYPE,                                       -- Codigo del nodo actual.
    UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO                                  -- Codigo del usuario que desencadena el proceso.
  );  

  --2
  PROCEDURE PR_PREPARAR_VAR_TRAMITE 
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,    -- Codigo de la compania.
    UN_PROCESO      IN TRAMITES.PROCESOS%TYPE,      -- Codigo del proceso.
    UN_TIPO_TRAMITE IN TRAMITES.TIPO_TRAMITE%TYPE,  -- Codigo del tipo de tramite.
    UN_TRAMITE      IN TRAMITES.NUMERO%TYPE,        -- Numero del tramite.
    UN_NODO         IN NODOS.CODIGO%TYPE,           -- Codigo del nodo/etapa actual.
    UN_D_TRAMITE    IN D_TRAMITES.CONSECUTIVO%TYPE, -- Codigo del detalle del tramite.
    UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO      -- Codigo del usuario que desencadena este proceso.
  );  

  --3
  PROCEDURE PR_CAMBIAR_EJECUTOR 
  (
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,      -- Codigo de la compania.
    UN_PROCESO         IN PROCESOS.CODIGO%TYPE,          -- Codigo del proceso.
    UN_TIPO_TRAMITE    IN TIPOTRAMITES.TIPOTRAMITE%TYPE, -- Codigo del tipo de tramite.
    UN_TRAMITE         IN TRAMITES.NUMERO%TYPE,          -- Numero del tramite.
    UN_D_TRAMITE       IN D_TRAMITES.CONSECUTIVO%TYPE,   -- Codigo del detalle del tramite.
    UN_USUARIO_INTERNO IN TRAMITES.USUARIO_INTERNO%TYPE, -- Codigo del nuevo usuario ejecutor.
    UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO        -- COdigo del usuario que desencadena el proceso (Responsable).
  );

  --4
  PROCEDURE PR_TRAMITAR 
  (
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,                                -- Codigo de la compania.
    UN_PROCESO         IN TRAMITES.PROCESOS%TYPE,                                  -- Codigo del proceso.
    UN_TIPO_TRAMITE    IN TRAMITES.TIPO_TRAMITE%TYPE,                              -- Codigo del tipo de tramite.
    UN_NUMERO          IN TRAMITES.NUMERO%TYPE,                                    -- Numero del tramite.
    UN_NODO_ORIGEN     IN NODOS.CODIGO%TYPE,                                       -- Codigo del nodo/etapa inicial.
    UN_NODO_DESTINO    IN NODOS.CODIGO%TYPE,                                       -- Codigo del nodo/etapa destino.
    UN_TRAMITE_INI     IN PCK_SUBTIPOS.TI_LOGICO  DEFAULT 0,                       -- Indicador que establece si el tramite es inicial.
    UN_USUARIO_DESTINO IN PCK_SUBTIPOS.TI_USUARIO,                                 -- Codigo del usuario que recibe el tramite.
    UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO,                                 -- Codigo del usuario que desencadena este proceso.
    UN_ARCHIVOCENTRAL  IN D_TRAMITES.CODIGO_ARCHIVO_CENTRAL%TYPE  DEFAULT ' ',     -- Indicador que establece si se deben generar la proyecciones.
    UN_PROYECCIONES    IN PCK_SUBTIPOS.TI_LOGICO DEFAULT -1                        -- Indicador que establece si se deben generar la proyecciones.
  );

  --5  
  PROCEDURE PR_VALIDAR_INF_DETALLADA 
  (
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,     -- Codigo de la compania.
    UN_PROCESO     IN PROCESOS.CODIGO%TYPE,         -- Codigo del proceso.
    UN_TIPOTRAMITE IN TIPOTRAMITES.TIPOTRAMITE%TYPE,-- Codigo del tipo de tramite.
    UN_TRAMITE     IN TRAMITES.NUMERO%TYPE,         -- Numero del tramite.
    UN_DTRAMITE    IN D_TRAMITES.CONSECUTIVO%TYPE,  -- Detalle del tramite.
    UN_NODO        IN NODOS.CODIGO%TYPE             -- Codigo del nodo.
  );

  --6
  PROCEDURE PR_ELIMINAR_TRAMITE 
  (
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,     -- Codigo de la compania.
    UN_PROCESO     IN PROCESOS.CODIGO%TYPE,         -- Codigo del proceso.
    UN_TIPOTRAMITE IN TIPOTRAMITES.TIPOTRAMITE%TYPE,-- Codigo del tipo de tramite.
    UN_TRAMITE     IN TRAMITES.NUMERO%TYPE          -- Numero del tramite.
  );

  --7
  PROCEDURE PR_PROYECTAR_NODO 
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,      -- Codigo de la compania.
    UN_TIPO_TRAMITE IN TIPOTRAMITES.TIPOTRAMITE%TYPE, -- Codigo del tipo de tramite.
    UN_PROCESO      IN PROCESOS.CODIGO%TYPE,          -- Codigo del proceso.
    UN_NODO         IN NODOS.CODIGO%TYPE,             -- Codigo del nodo.
    UN_NODO_ANT     IN NODOS.CODIGO%TYPE,             -- Codigo del nodo antecesor.
    UN_TRAMITE      IN TRAMITES.NUMERO%TYPE,          -- Numero del tramite.
    UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO        -- Codigo del usuario.
  );  

  --8
  PROCEDURE PR_PROYECTAR_TRAMITE 
  (
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,         -- Codigo de la compania.
    UN_TIPO_TRAMITE    IN TRAMITES.TIPO_TRAMITE%TYPE,       -- Codigo del tipo de tramite.
    UN_PROCESO         IN TRAMITES.PROCESOS%TYPE,           -- Codigo del proceso.
    UN_TRAMITE         IN TRAMITES.NUMERO%TYPE,             -- Numero del tramite.
    UN_NODO_ORIGEN     IN NODOS.CODIGO%TYPE,                -- Codigo del nodo origen desde el cual se inicia la proyeccion del tramite.
    UN_DIAS_REPROG     IN PCK_SUBTIPOS.TI_ENTERO DEFAULT 0, -- Dias a reprogramar en el nodo origen.
    UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO           -- Codigo del usuario que desencadena el proceso.
  );

  --9
  PROCEDURE PR_REPROGRAMAR_NODO 
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,         -- Codigo de la compania.
    UN_TIPO_TRAMITE IN TIPOTRAMITES.TIPOTRAMITE%TYPE,    -- Codigo del tipo de tramite.
    UN_PROCESO      IN PROCESOS.CODIGO%TYPE,             -- Codigo del proceso.
    UN_NODO         IN NODOS.CODIGO%TYPE,                -- Codigo del nodo.
    UN_NODO_ANT     IN NODOS.CODIGO%TYPE,                -- Codigo del nodo antecesor.
    UN_TRAMITE      IN TRAMITES.NUMERO%TYPE,             -- Numero del tramite.
    UN_DIAS_REPROG  IN PCK_SUBTIPOS.TI_ENTERO DEFAULT 0, -- Cantidad de dias a programar en el nodo.
    UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO           -- Codigo del usuario.
  );

  --10
  PROCEDURE PR_PREPARAR_AMB_WF 
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA -- Codigo de la compania
  );

  --11
  PROCEDURE PR_TRAMITAR_DESDE_ERP 
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
  );

    --4
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
  );

  --5
  PROCEDURE PR_ACTUALIZAR_VARIABLE_WF
  (
    UN_VALOR_TEXTO          IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,               -- Valor correspondiente a la variable
    UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,                           -- Codigo de la compania.
    UN_CODIGO_NODO_VARIABLE IN D_TRAMITE_VARIABLES.CODIGO_NODO_VARIABLE%TYPE,      -- Codigo de la variable (010-090) proceso pqrs
    UN_TIPO_TRAMITE         IN D_TRAMITE_VARIABLES.TIPO_TRAMITE%TYPE,              -- Codigo de tipo tramite enviado para proceso pqrs por defecto 1
    UN_NUMERO_TRAMITE       IN D_TRAMITE_VARIABLES.NUMERO_TRAMITE%TYPE,            -- Numero de radicado
    UN_CODIGO_NODO          IN D_TRAMITE_VARIABLES.CODIGO_NODO%TYPE,               -- Codigo del nodo se envia por defecto 0000 para proceso pqrs 
    UN_CODIGO_PROCESO       IN D_TRAMITE_VARIABLES.CODIGO_PROCESO%TYPE,            -- Codigo de proceso por defecto 00000 para pqrs
    UN_CONSECUTIVO_TRAMITE  IN D_TRAMITE_VARIABLES.CONSECUTIVO_TRAMITE%TYPE,      -- Consecutivo de tramite por defecto se envia en 1
    UN_USUARIO              IN PCK_SUBTIPOS.TI_USUARIO                             -- Codigo del usuario que desencadena este proceso.
  );
  --6
  FUNCTION FC_WORKFLOW_PQRS
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
    UN_TIPO_TRAMITE_USUARIO     IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,
    UN_TELEFONO                 IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,           -- variable telefono
    UN_GENERO                   IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,            -- variable genero-- variable tipo tramite seleccionado por usuario de etapa  
    UN_CODIGO_TRAMITE           IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,           -- variable codigo de tipo de proceso 
    UN_RANGO_EDAD               IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,           -- variable rango de edad
    UN_TIPO_PERSONA             IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,           -- variable tipo de persona
    UN_TIPO_POBLACION           IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,           -- variable tipo de poblacion
    UN_VULNERABILIDAD           IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,           -- variable vulnerabilidad
    UN_OCUPACION                IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,           -- variable ocupacion
    UN_ESCOLARIDAD              IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,           -- variable escolaridad
    UN_DES_TIPO_POBLACION       IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,           -- variable descripcion tipo poblacion, cuando se escoga otros
    UN_DES_OCUPACION            IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,           -- variable descripcion ocupacion, cuando se escoga otros
    UN_ANONIMO                  IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0              -- Indicador que establece si se deben generar la proyecciones.
  )RETURN PCK_SUBTIPOS.TI_ENTERO_LARGO;

  PROCEDURE PR_CARGAR_SERIEDOCUMENTAL 
( 
  UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CADENASERIES IN CLOB,
  UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO
);

  PROCEDURE PR_CARGAR_TRAMITES
( 
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CADENA_TRAMITE IN CLOB,
  UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
);

PROCEDURE PR_PREPARAR_VAR_PROCESO 
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,    -- Codigo de la compania.
    UN_PROCESO      IN TRAMITES.PROCESOS%TYPE,      -- Codigo del proceso.
    UN_TIPO_TRAMITE IN TRAMITES.TIPO_TRAMITE%TYPE,  -- Codigo del tipo de tramite.
    UN_TRAMITE      IN TRAMITES.NUMERO%TYPE,        -- Numero del tramite.
    UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO      -- Codigo del usuario que desencadena este proceso.
  );


  --5
  PROCEDURE PR_ACT_VARIABLE_PROCESO_WF
  (
    UN_VALOR_TEXTO          IN D_TRAMITE_VARIABLES.VALOR_TEXTO%TYPE,               -- Valor correspondiente a la variable
    UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,                           -- Codigo de la compania.
    UN_CODIGO_NODO_VARIABLE IN D_TRAMITE_VARIABLES.CODIGO_NODO_VARIABLE%TYPE,      -- Codigo de la variable (010-090) proceso pqrs
    UN_TIPO_TRAMITE         IN D_TRAMITE_VARIABLES.TIPO_TRAMITE%TYPE,              -- Codigo de tipo tramite enviado para proceso pqrs por defecto 1
    UN_NUMERO_TRAMITE       IN D_TRAMITE_VARIABLES.NUMERO_TRAMITE%TYPE,            -- Numero de radicado
    UN_CODIGO_PROCESO       IN D_TRAMITE_VARIABLES.CODIGO_PROCESO%TYPE,            -- Codigo de proceso por defecto 00000 para pqrs
    UN_USUARIO              IN PCK_SUBTIPOS.TI_USUARIO                             -- Codigo del usuario que desencadena este proceso.
  );
  
  PROCEDURE PR_ACT_INF_PROD 
(
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,      -- Codigo de la compania.
    UN_PROCESO         IN PROCESOS.CODIGO%TYPE,          -- Codigo del proceso.
    UN_NODO            IN NODOS.CODIGO%TYPE,          -- Codigo del proceso.
    UN_TIPO_TRAMITE    IN TIPOTRAMITES.TIPOTRAMITE%TYPE, -- Codigo del tipo de tramite.
    UN_TRAMITE         IN TRAMITES.NUMERO%TYPE,          -- Numero del tramite.
    UN_DIRECCION       IN PROCESOS.CODIGO%TYPE,          -- Codigo del proceso.
    UN_PROCEDENCIA     IN TIPOTRAMITES.TIPOTRAMITE%TYPE, 
    UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO        -- COdigo del usuario que desencadena el proceso (Responsable).
  );
  
PROCEDURE PR_PROCEDENCIA_A_ENVIARCORREO
   (
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO          IN D_TRAMITE_VARIABLES.CODIGO_PROCESO%TYPE,
    UN_NODO             IN D_TRAMITE_VARIABLES.CODIGO_NODO%TYPE,
    UN_NODO_DESTINO     IN D_TRAMITE_VARIABLES.CODIGO_NODO%TYPE,
    UN_TIPO_TRAMITE     IN D_TRAMITE_VARIABLES.TIPO_TRAMITE%TYPE,
    UN_TRAMITE          IN D_TRAMITE_VARIABLES.NUMERO_TRAMITE%TYPE
   );
   
PROCEDURE PR_CARGAR_HISTORIAL_VAR 
( 
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CADENA_TRAMITE IN CLOB,
  UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
);

FUNCTION FC_CALCULAR_COLOR 
(
UN_COMPANIA              IN VARCHAR2,
UN_FECHA_INICIO          IN DATE,
UN_FECHA_FINAL           IN DATE
)RETURN VARCHAR2;

FUNCTION FC_DIAS_PRG
 (
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,                                -- Codigo de la compania.
    UN_TRAMITE         IN TRAMITES.NUMERO%TYPE--,                                     -- Numero del tramite. 
  )RETURN NUMBER;

END PCK_WORKFLOW;