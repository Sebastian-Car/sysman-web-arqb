create or replace PACKAGE BODY                "MENUK" 
AS
type array_p
IS
  TABLE OF NUMBER INDEX BY binary_integer;
  /**
  *Funcion de generacion de xml de menus
  *
  * @author Sysman LTDA -- Carlos Rojas
  * @version 1.0
  * @param UN_EMPRESA id de la empresa del usuario
  * @param UN_USUARIO id del usuario
  * @return Retorna clob con el XML generado
  */
  FUNCTION MENUS(
      UN_COMPANIA VARCHAR2,
      UN_USUARIO VARCHAR2 )
    RETURN CLOB
  AS
    RTA CLOB;
    XML         VARCHAR2(2000);
    MI_OBJANT   NUMBER;
    MI_NIVELANT NUMBER;
    MI_TIPOANT  VARCHAR2(1);
    MI_ID       NUMBER:=1;
    MI_IDPADRE array_p ;
    MI_LONGITUD NUMBER;
    MI_IND NUMBER:=0;
  BEGIN
    RTA          :=' ';
    XML          :='';
    MI_OBJANT    :=-1;
    MI_NIVELANT  :=-1;
    MI_IDPADRE(0):=0;
  
    FOR RS IN
    (SELECT LEVEL NIVEL,
      MENUS.*
    FROM
      (SELECT GM.MENU,
        NVL(D.MENUPADRE,0) MENUPADRE,
        M.TIPO,
        M.NOMBRE,
        M.COMANDO,
        M.RUTA_ICONO,
        M.PARAMETRO,
        M.FILTRO,
        VER,
        M.APLICACION,
        M.ORDEN
      FROM USUARIO_D US,
        USUARIO U,
        GRUPO_MENU GM,
        MENU M,
        MENU_D D
      WHERE U.CODIGO  =US.GRUPO
      AND U.APLICACION=M.APLICACION
      AND GM.GRUPO    = US.GRUPO
      AND GM.APLICACION  =D.APLICACION
      AND GM.MENU     =D.MENU
      AND M.CODIGO    =D.MENU
      AND US.USUARIO  =UN_USUARIO
      AND GM.COMPANIA =UN_COMPANIA
      AND VER NOT    IN (0)
      AND U.ESTADO='A'
      )MENUS
      START WITH MENUPADRE  =0
      CONNECT BY PRIOR MENU = MENUPADRE
    ORDER SIBLINGS BY ORDEN
    )
    LOOP
      IF RS.NIVEL <MI_NIVELANT AND MI_NIVELANT<>-1 THEN
        FOR MI_I IN RS.NIVEL .. MI_NIVELANT-1
        LOOP
          DBMS_LOB.WRITEAPPEND(RTA,4,'</M>');
        END LOOP;
      END IF;
      IF RS.NIVEL =MI_NIVELANT AND MI_TIPOANT='M' THEN
        DBMS_LOB.WRITEAPPEND(RTA,4,'</M>');
      END IF;
      IF RS.TIPO             ='M' AND RS.PARAMETRO IS NOT NULL THEN
        MI_IDPADRE(RS.NIVEL):=RS.MENU;
        XML                 :='<M id="'||RS.MENU||'" class="'||MI_IDPADRE(RS.NIVEL-1)||'" D="'||RS.MENU||'" N="'||RS.NOMBRE||'" CO="'||RS.COMANDO||'" I="'||RS.RUTA_ICONO||'"  P="'||RS.PARAMETRO||'" A="'||RS.APLICACION||'" >';
        DBMS_LOB.WRITEAPPEND(RTA,LENGTH(XML),XML);
      ELSIF RS.TIPO          ='M' THEN
        MI_IDPADRE(RS.NIVEL):=RS.MENU;
        XML                 :='<M id="'||RS.MENU||'" class="'||MI_IDPADRE(RS.NIVEL-1)||'" D="'||RS.MENU||'" N="'||RS.NOMBRE||'" I="'||RS.RUTA_ICONO||'"  P="'||RS.PARAMETRO||'" A="'||RS.APLICACION||'" >';
        DBMS_LOB.WRITEAPPEND(RTA,LENGTH(XML),XML);
      ELSE
        XML:='<P id="'||RS.MENU||'" class="'||MI_IDPADRE(RS.NIVEL-1)||'" D="'||RS.MENU||'" N="'||RS.NOMBRE||'" CO="'||RS.COMANDO||'"   I="'||RS.RUTA_ICONO||'"   R="'||RS.VER||'"  P="'||RS.PARAMETRO||'"  F="'||RS.FILTRO||'" A="'||RS.APLICACION||'"/>';
        DBMS_LOB.WRITEAPPEND(RTA,LENGTH(XML),XML);
      END IF;
      MI_NIVELANT:=RS.NIVEL;
      MI_OBJANT  :=RS.MENUPADRE;
      MI_TIPOANT :=RS.TIPO;
      MI_ID      :=MI_ID+1;
    END LOOP;
    FOR MI_I IN 1 .. MI_NIVELANT
    LOOP
      DBMS_LOB.WRITEAPPEND(RTA,4,'</M>');
    END LOOP;
    IF RTA =' ' THEN
     RTA:=NULL;
    ELSE
      RTA:='<?xml version="1.0" encoding="utf-8" ?><M id="0"  N="PANEL PRINCIPAL"  I="Home.png" >'||RTA;
    END IF;
    RETURN RTA;
  EXCEPTION
  WHEN OTHERS THEN
    RETURN '<ERROR>El proceso no se llevo a cabo. Por favor revise. ('|| SQLERRM || ') </ERROR>';
  END MENUS;
  
/**
  *Funcion de generacion de xml de menus
  *
  * @author Sysman LTDA -- José Pascual Gómez
  * @version 1.0
  * @param UN_EMPRESA id de la empresa del usuario
  * @param UN_USUARIO id del usuario
  * @return Retorna clob con el XML generado
  */
  FUNCTION MENUS_NUE(
      UN_COMPANIA VARCHAR2,
      UN_USUARIO VARCHAR2 )
    RETURN CLOB
  AS
    RTA CLOB;    
  BEGIN
    RTA := MENUK.FC_SUBMENU(UN_COMPANIA  => UN_COMPANIA
                           ,UN_USUARIO   => UN_USUARIO
                           ,UN_MENUPADRE => 0);
    IF RTA =' ' OR RTA IS NULL THEN
     RTA:=NULL;
    ELSE
      RTA:='<?xml version="1.0" encoding="utf-8"?><M id="0"  N="PANEL PRINCIPAL"  I="Home.png" >'||RTA||'</M>';
    END IF;
    RETURN RTA;
 /* EXCEPTION
  WHEN OTHERS THEN
    RETURN '<ERROR>El proceso no se llevo a cabo. Por favor revise. ('|| SQLERRM || ') </ERROR>';
    */
END MENUS_NUE;
  
FUNCTION FC_SUBMENU(
  UN_COMPANIA  IN VARCHAR2,
  UN_USUARIO   IN VARCHAR2,
  UN_MENUPADRE IN NUMBER
)
RETURN  CLOB AS 
  RTA CLOB;
  XML CLOB;
  XML_SUB CLOB;
BEGIN
  RTA :=' ';
  FOR RS IN(SELECT GM.MENU,
                  M.TIPO,
                  M.NOMBRE,
                  M.COMANDO,
                  M.RUTA_ICONO,
                  M.PARAMETRO,
                  M.FILTRO,
                  M.APLICACION,
                  M.ORDEN,
                  VER
            FROM USUARIO_D US,
                 USUARIO U,
                 GRUPO_MENU GM,
                 MENU M,
                 MENU_D D
            WHERE U.CODIGO  =US.GRUPO
              AND U.APLICACION=M.APLICACION
              AND GM.GRUPO    = US.GRUPO
              AND GM.MENU     =D.MENU
              AND GM.MENU     =D.MENU
              AND M.CODIGO    =D.MENU
              AND US.USUARIO  = UN_USUARIO
              AND GM.COMPANIA = UN_COMPANIA
              AND NVL(D.MENUPADRE,0)= UN_MENUPADRE
              AND VER NOT    IN (0)
              AND U.ESTADO='A'      
            ORDER BY ORDEN
        ) 
  LOOP
    IF RS.TIPO  ='M' THEN
      XML_SUB := MENUK.FC_SUBMENU(UN_COMPANIA  => UN_COMPANIA
                                 ,UN_USUARIO   => UN_USUARIO
                                 ,UN_MENUPADRE => RS.MENU);
      IF TRIM(SUBSTR(XML_SUB,1,100))  IS NOT NULL THEN
        IF RS.PARAMETRO IS NOT NULL THEN
          XML  :='<M id="'||RS.MENU||'" class="'||UN_MENUPADRE||'" D="'||RS.MENU||'" N="'||RS.NOMBRE||'" CO="'||RS.COMANDO||'" I="'||RS.RUTA_ICONO||'"  P="'||RS.PARAMETRO||'" A="'||RS.APLICACION||'" >';
        ELSE
          XML  :='<M id="'||RS.MENU||'" class="'||UN_MENUPADRE||'" D="'||RS.MENU||'" N="'||RS.NOMBRE||'" I="'||RS.RUTA_ICONO||'"  P="'||RS.PARAMETRO||'" A="'||RS.APLICACION||'" >';
        END IF;
        DBMS_LOB.WRITEAPPEND(RTA,LENGTH(XML),XML);
        --DBMS_LOB.WRITEAPPEND(RTA,LENGTH(XML_SUB),XML_SUB);
        RTA := RTA || XML_SUB;
        DBMS_LOB.WRITEAPPEND(RTA,4,'</M>');
      END IF;
    ELSE
      XML  :='<P id="'||RS.MENU||'" class="'||UN_MENUPADRE||'" D="'||RS.MENU||'" N="'||RS.NOMBRE||'" CO="'||RS.COMANDO||'"   I="'||RS.RUTA_ICONO||'"   R="'||RS.VER||'"  P="'||RS.PARAMETRO||'"  F="'||RS.FILTRO||'" A="'||RS.APLICACION||'"/>';
      DBMS_LOB.WRITEAPPEND(RTA,LENGTH(XML),XML);
    END IF;
  END LOOP;
  RETURN RTA;
  DBMS_OUTPUT.PUT_LINE(CHR(13)||CHR(10));
  DBMS_OUTPUT.PUT_LINE(RTA);
 /* EXCEPTION
  WHEN OTHERS THEN
    RETURN '<ERROR>El proceso no se llevo a cabo. Por favor revise. ('|| SQLERRM || ') </ERROR>';
    */
END FC_SUBMENU;

  --4
  PROCEDURE PR_ASIGNARACCESOMENUS 
  /*
    NAME              : PR_ASIGNARACCESOMENUS 
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : PABLO ANDRES ESPITIA CUCA
    DATE MIGRATION    : 08/03/2018
    TIME              : 11:40 AM
    SOURCE MODULE     : GENERAL
    DESCRIPTION       : ASIGNA O QUITA LOS PERMISOS DE VISUALIZACION A LOS MENUS CUYO NIVEL DE JERARQUIA INICIA CON EL CODIGO DEL PARAMETRO MENU. 
    MODIFIED BY       : 
    
    @NAME  : asignarAccesoMenus
    @METHOD: PUT
    */
  (
    UN_COMPANIA IN GRUPO_MENU.COMPANIA%TYPE, -- Codigo de la compania.
    UN_GRUPO    IN GRUPO_MENU.GRUPO%TYPE,    -- Coidigo del grupo.
    UN_MENU     IN VARCHAR2,                 -- Valor menu padre.
    UN_USUARIO  IN PCK_SUBTIPOS.TI_USUARIO,  -- Codigo del usuario.
    UN_IND_VER  IN PCK_SUBTIPOS.TI_LOGICO,   -- Indicador ver.
    UN_MODULO   IN PCK_SUBTIPOS.TI_MODULO    -- Codigo del modulo.
  )
  AS 
    MI_RTA_ACME   PCK_SUBTIPOS.TI_RTA_ACME;
    MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION;
    MI_TABLA_GM   PCK_SUBTIPOS.TI_TABLA DEFAULT 'GRUPO_MENU';
    MI_TABLA_MD   PCK_SUBTIPOS.TI_TABLA DEFAULT 'MENU_D';
    MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;
    MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CANT       PCK_SUBTIPOS.TI_ENTERO;
    MI_MENUPADRE  MENU_D.MENUPADRE%TYPE;
  BEGIN
    IF UN_MENU IS NOT NULL THEN
      BEGIN
        SELECT MENUPADRE
        INTO MI_MENUPADRE
        FROM MENU_D
        WHERE APLICACION = UN_MODULO
          AND MENU       = UN_MENU;      
      END;
    END IF;
    
    IF MI_MENUPADRE IS NOT NULL THEN
      --Cantidad de hijos menu padre.    
      SELECT COUNT(MENU) CANT
      INTO MI_CANT
      FROM MENU_D
      WHERE APLICACION = UN_MODULO
        AND MENUPADRE  = MI_MENUPADRE;
      
      IF MI_CANT IN (1) THEN
        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_GENERAL;
          
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN
          MI_REEMPLAZOS(1).CLAVE := 'MENU';
          MI_REEMPLAZOS(1).VALOR := UN_MENU;
          
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_G_BU_GM_MSG_MENUHIJOUNICO
                                    ,UN_TABLAERROR => MI_TABLA_MD
                                    ,UN_REEMPLAZOS => MI_REEMPLAZOS);
        END;
      END IF;
    END IF;    
    
    MI_CONDICION := 'COMPANIA  = '''||UN_COMPANIA||'''
                 AND GRUPO     = '''||UN_GRUPO   ||'''
                 AND MENU LIKE '''||UN_MENU||'%'||'''';
    
    MI_CAMPOS := 'VER           =   '||UN_IND_VER||'
                 ,MODIFIED_BY   = '''||UN_USUARIO||'''
                 ,DATE_MODIFIED = SYSDATE';             
    
    BEGIN
      BEGIN
        MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_ACCION    => 'M'
                                        ,UN_CONDICION => MI_CONDICION
                                        ,UN_TABLA     => MI_TABLA_GM
                                        ,UN_CAMPOS    => MI_CAMPOS);
                                        
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_GENERAL;
      END;
    
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN
      MI_REEMPLAZOS(1).CLAVE := 'MENU';
      MI_REEMPLAZOS(1).VALOR := UN_MENU;
      
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_G_PR_M_INDICADORVER
                                ,UN_TABLAERROR => MI_TABLA_GM
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;
  END PR_ASIGNARACCESOMENUS;  
--5
FUNCTION FC_GENERAR_CLAVE
  (
   /*
    NAME              : FC_GENERAR_CLAVE
    AUTHORS           : HENRY PUERTO VASQUEZ
    DATE MIGRADOR     : 26/JUN/2018
    TIME              : 16:33
    MODIFIER          : 
    VERSION           : 1.0
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : GENERA CLAVE DE SEGUNDA CONTRASEÑA QUE ES ENVIADA VIA CORREO ELECTRONICO
    NOTES             : 
    @NAME: generarClave
  */
    UN_LON NUMBER DEFAULT 6
  )
  RETURN VARCHAR2 AS
 
  MI_CLAVE VARCHAR2(256);
  MI_NUM   NUMBER(3);
  BEGIN
    WHILE LENGTH(MI_CLAVE)<UN_LON OR MI_CLAVE IS NULL
    LOOP
      MI_NUM:=TRUNC(DBMS_RANDOM.VALUE(47,123));
      IF MI_NUM>=48 AND MI_NUM<=57 OR MI_NUM>=65 AND MI_NUM<=90 OR MI_NUM>=97 AND MI_NUM<=122  THEN
        MI_CLAVE:=MI_CLAVE||CHR(MI_NUM);
      END IF;
    END LOOP;
    RETURN MI_CLAVE;
  END FC_GENERAR_CLAVE;
  
FUNCTION FC_GENERAR_CLAVE_USUARIO
  (
  /*
    NAME              : FC_GENERAR_CLAVE_USUARIO
    AUTHORS           : HENRY PUERTO VASQUEZ
    DATE MIGRADOR     : 26/JUN/2018
    TIME              : 16:33
    MODIFIER          : 
    VERSION           : 1.0
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : GENERA CLAVE DE SEGUNDA CONTRASEÑA QUE ES ENVIADA VIA CORREO ELECTRONICO
    PARAMETERS        : UN_TIEMPO TIEMPO EN MINUTOS QUE ES VALIDA LA CONTRASEÑA
                        UN_LON    LONGITUD DE LA CONTRASEÑA
    NOTES             : 
    @NAME: generarClaveUsuario
  */
    UN_USUARIO  IN VARCHAR2, 
    UN_TIEMPO   IN NUMBER DEFAULT 20, 
    UN_LON      IN NUMBER DEFAULT 6
  )
  RETURN NUMBER AS
  
    MI_CLAVE   VARCHAR2(16);
    MI_TIEMPO  DATE;
    MI_EXISTE VARCHAR2(1);
    MI_CORREO VARCHAR2(60 CHAR);
    MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    BEGIN
      SELECT DISTINCT 'X'
      INTO  MI_EXISTE
      FROM  USUARIO
      WHERE CODIGO=UN_USUARIO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      RAISE_APPLICATION_ERROR (-20020,'Usuario' || UN_USUARIO|| 'no existe en el sistema.');
    END;
    --
    SELECT DISTINCT CORREOELECTRONICO
    INTO  MI_CORREO
    FROM  USUARIO
    WHERE CODIGO=UN_USUARIO;
    --    
    IF MI_CORREO IS NULL THEN 
      BEGIN 
        RAISE PCK_EXCEPCIONES.EXC_GENERAL;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN
            MI_REEMPLAZOS(1).CLAVE := 'USUARIO';
            MI_REEMPLAZOS(1).VALOR := UN_USUARIO;      
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                      ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CORREO_USUARIO
                                      ,UN_TABLAERROR => 'USUARIO'
                                      ,UN_REEMPLAZOS => MI_REEMPLAZOS);
      END;
    END IF;
    --
    BEGIN
      MI_CLAVE  :=  MENUK.FC_GENERAR_CLAVE(UN_LON);
      MI_TIEMPO :=  SYSDATE + (UN_TIEMPO/24/60);
      UPDATE USUARIO
      SET    FECHA_VENCIMIENTO  = MI_TIEMPO,
             SEGUNDA_CLAVE  = MI_CLAVE 
      WHERE  CODIGO = UN_USUARIO;
    EXCEPTION WHEN OTHERS THEN
      RAISE_APPLICATION_ERROR (-20020,'No fue posible actualizar el usuario del sistema por'||SQLERRM);
    END;   
    RETURN -1;
  END FC_GENERAR_CLAVE_USUARIO; 
--7  
  FUNCTION FC_AUTORIZAR_ACCESO_USUARIO
  /*
    NAME              : FC_AUTORIZAR_ACCESO_USUARIO
    AUTHORS           : HENRY PUERTO VASQUEZ
    DATE MIGRADOR     : 13/SEP/2018
    TIME              : 16:33
    MODIFIER          : 
    VERSION           : 1.0
    DATE MODIFIED     : 09/10/2018
    TIME              : 09:00
    DESCRIPTION       : VERIFIA SI EL USUARIO TIENE ACCESO AL SISTEMA DE ACUERDO CON LA POLITICA DE ACCESO
                        RETORNA CERO SI EL USUARIO NO TIENE ACCESO, Y OTRO VALOR SI PUEDE ACCEDER AL SISTEMA
    PARAMETERS        : UN_COMPANIA => CODIGO DE LA COMPANIA
                        UN_USUARIO => CODIGO DE USUARIO
    NOTES             : POLITICA DE ACCESO
    @NAME: autorizarAccesoUsuario
  */
  (
    UN_COMPANIA           IN COMPANIA.CODIGO%TYPE
  , UN_USUARIO            IN USUARIO.CODIGO%TYPE
  )
  RETURN NUMBER AS
    MI_GRUPO              VARCHAR2(32);
    MI_FECHA              DATE      := SYSDATE;
    MI_HORAI              DATE      := SYSDATE;
    MI_HORAF              DATE      := SYSDATE;
    MI_DIA                NUMBER(5) := TO_NUMBER(TO_CHAR(MI_FECHA,'D'));
    MI_FESTIVO            DATE      := NULL;
    MI_ACCESO             NUMBER(1) := 0;
    MI_ACCESOE            NUMBER(1) := 0;
  BEGIN
    -- Valida si el usuario pertenece aL menos a un grupo
    BEGIN
      SELECT GRUPO
      INTO  MI_GRUPO
      FROM  USUARIO_D
      WHERE USUARIO=UN_USUARIO
      AND   ROWNUM =1;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      RAISE_APPLICATION_ERROR (-20020, 'Usuario ' || UN_USUARIO|| ' no registrado o sin grupos en el sistema.');
    END;
    -- Verifica si el día actual es festivo
    BEGIN
      SELECT ID_DE_FESTIVO
      INTO   MI_FESTIVO
      FROM   FESTIVOS
      WHERE  TO_CHAR(ID_DE_FESTIVO,'DD/MM/YYYY') = TO_CHAR(MI_FECHA,'DD/MM/YYYY');
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_FESTIVO:=NULL;
    END;
    -- Verifica si se permite acceso en el horario actual para cualquier grupo al que pertenezca el usuarIo
    BEGIN
      SELECT HORA_INICIAL, HORA_FINAL, CUENTA
      INTO   MI_HORAI, MI_HORAF, MI_GRUPO
      FROM   ACCESOS
      WHERE  COMPANIA = UN_COMPANIA 
        AND  CUENTA IN (SELECT GRUPO
                        FROM   USUARIO_D
                        WHERE  USUARIO=UN_USUARIO)
        AND  NUMERO = MI_DIA
        AND  TO_CHAR(MI_FECHA,'HH24:MI:SS') BETWEEN TO_CHAR(HORA_INICIAL, 'HH24:MI:SS') AND TO_CHAR(HORA_FINAL, 'HH24:MI:SS')
        AND  1 = CASE WHEN MI_FESTIVO IS NOT NULL AND INCLUYE_FESTIVO <> 0 OR MI_FESTIVO IS NULL THEN 1 ELSE 0 END  
        AND  ROWNUM <= 1;
        MI_ACCESO:=-1;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_ACCESO:=0;
    END;
    -- Valida las excepciones configuradas para los grupos a los que pertenezca el usuario
    BEGIN
      SELECT   ACCESO, CUENTA 
      INTO     MI_ACCESOE, MI_GRUPO
      FROM     EXCEPCIONES
      WHERE    COMPANIA = UN_COMPANIA 
        AND    CUENTA IN (SELECT GRUPO
                          FROM   USUARIO_D
                          WHERE  USUARIO=UN_USUARIO)
        AND    FECHA = TO_CHAR(MI_FECHA, 'DD/MM/YYYY')
        AND    TO_CHAR(MI_FECHA,'HH24:MI:SS') BETWEEN TO_CHAR(HORA_INICIAL,'HH24:MI:SS') AND TO_CHAR(HORA_FINAL,'HH24:MI:SS')
        AND    ROWNUM <= 1;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_ACCESOE:=MI_ACCESO;
    END;
    IF MI_ACCESOE=0 THEN
      --RAISE_APPLICATION_ERROR (-20020, 'Acceso no autorizado para el usuario ' || UN_USUARIO || ' con grupo ' || MI_GRUPO || '.');
      RETURN 0;
    END IF;
    RETURN MI_ACCESO;  
  END FC_AUTORIZAR_ACCESO_USUARIO;  
--8
FUNCTION FC_VALIDAR_SEGUNDA_CLAVE
  (
   /*
    NAME              :  FC_VALIDAR_SEGUNDA_CLAVE
    AUTHORS           : HENRY PUERTO VASQUEZ
    DATE MIGRADOR     : 13/SEP/2018
    TIME              : 16:33
    MODIFIER          : 
    VERSION           : 1.0
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : VERIFICA SI LA SEGUNDA CLAVE ES CORRECTA
    PARAMETERS        : CODIGO DE USUARIO
    NOTES             : 
    @NAME: validarSegundaClave
  */
    UN_USUARIO IN VARCHAR2,
    UN_CLAVE   IN VARCHAR2
  )
RETURN VARCHAR2 AS
 
  MI_CLAVE VARCHAR2(240);
  MI_FECHA DATE := SYSDATE;
  BEGIN
    IF UN_USUARIO IS NULL THEN
      RETURN 'Debe especificar el nombre del usuario para definir acceso.';
    END IF;
    IF UN_CLAVE IS NULL THEN
      RETURN 'Debe especificar la segunda clave.';
    END IF;
    BEGIN
      SELECT DISTINCT SEGUNDA_CLAVE
      INTO  MI_CLAVE
      FROM  USUARIO
      WHERE CODIGO=UN_USUARIO
        AND MI_FECHA<=FECHA_VENCIMIENTO
        AND SEGUNDA_CLAVE= UN_CLAVE;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      RETURN 'Usuario '||UN_USUARIO||'no registrado en el sistema.';
    END;
   RETURN 'Ok';  
END;
--9
FUNCTION FC_RETORNAR_CORREO
  (
  /*
    NAME              : FC_RETORNAR_CORREO
    AUTHORS           : HENRY PUERTO VASQUEZ
    DATE MIGRADOR     : 13/SEP/2018
    TIME              : 16:33
    MODIFIER          : 
    VERSION           : 1.0
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : RETORNA CORREO INSTITUCIONAL
    PARAMETERS        : CODIGO DE USUARIO
    NOTES             : 
    @NAME: retornarCorreo
  */
    UN_USUARIO VARCHAR2
  )
  RETURN VARCHAR2 AS
  
  MI_CORREO VARCHAR2(240);
  MI_FECHA DATE:=SYSDATE;
  BEGIN
       IF UN_USUARIO IS NULL THEN
          RETURN 'DEBE ESPECIFICAR EL NOMBRE DEL USUARIO PARA DEFINIR ACCESO.';
      END IF;
   BEGIN
      SELECT DISTINCT CORREOELECTRONICO
      INTO  MI_CORREO
      FROM  USUARIO
      WHERE CODIGO=UN_USUARIO;
   EXCEPTION WHEN NO_DATA_FOUND THEN
      RETURN 'USUARIO '||UN_USUARIO||' NO REGISTRADO EN EL SISTEMA.';
   END;
   RETURN MI_CORREO; 
END;
END MENUK;