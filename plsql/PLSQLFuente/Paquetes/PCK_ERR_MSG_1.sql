create or replace PACKAGE BODY PCK_ERR_MSG AS



  PROCEDURE RAISE_WITH_MSG(
   /* 
    NAME              : RAISE_WITH_MSG
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : CARLOS ALBERTO MANRIQUE PALACIOS
    DATE MIGRADOR     : 01/11/2016
    TIME              : 17:22 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Este procedimiento sirve como unico punto desde el cual
                        se lanzan las excepciones a la aplicacion.
    PARAMETROS DE ENTRADA:
                UN_EXC_COD:     Codigo de la excepcion que sera lanzada por lo 
                                general se debe invocar con el valor SQLCODE.
                UN_ERROR_COD:   Codigo del error especifico de negocio almacenado
                                en el paquete PCK_ERRORES y en la tabla ERRORES_NEGOCIO
                                con el mensaje especifico para el usuario.
                UN_SQLERROR:    Sql ejecutado al lanzar la excepcion, por lo general 
                                este parametro solo se envia desde la funcion ACME.
                UN_TABLAERROR:  Origen desde el cual se produce el error. 
  */
    UN_EXC_COD    IN PLS_INTEGER ,
    UN_ERROR_COD  IN PLS_INTEGER :=NULL,
    UN_SQLERROR   IN CLOB        :=NULL,
    UN_TABLAERROR IN VARCHAR2    :=NULL,
    UN_REEMPLAZOS IN PCK_SUBTIPOS.TI_CLAVEVALOR :=PCK_SUBTIPOS.MI_CLAVEVALORNULL
    ) AS

    MI_ERR_MSG VARCHAR2(5000 CHAR):=NULL;

  BEGIN
  MI_ERR_MSG:= FC_EVALUAR_ERROR(
                  UN_EXC_COD    => UN_EXC_COD,
                  UN_ERROR_COD  => UN_ERROR_COD,
                  UN_SQLERROR   => UN_SQLERROR,
                  UN_TABLAERROR => UN_TABLAERROR,
                  UN_REEMPLAZOS => UN_REEMPLAZOS
                  );

    RAISE_APPLICATION_ERROR(UN_EXC_COD, MI_ERR_MSG,FALSE);

  END RAISE_WITH_MSG;



  FUNCTION FC_EVALUAR_ERROR 
    /* 
    NAME              : FC_EVALUAR_ERROR
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : CARLOS ALBERTO MANRIQUE PALACIOS
    DATE MIGRADOR     : 01/11/2016
    TIME              : 17:22 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : En esta funcion retorna el error que se debe mostar al 
                        usuario partienda de toda la traza de excepciones 
                        lanzadas. Tambien guarga la informacion del error en la
                        tabla LOG_ERRORES.
    PARAMETROS DE ENTRADA:
                UN_EXC_COD:     Codigo de la excepcion que sera lanzada por lo 
                                general se debe invocar con el valor SQLCODE.
                UN_ERROR_COD:   Codigo del error especifico de negocio almacenado
                                en el paquete PCK_ERRORES y en la tabla ERRORES_NEGOCIO
                                con el mensaje especifico para el usuario.
                UN_SQLERROR:    Sql ejecutado al lanzar la excepcion, por lo general 
                                este parametro solo se envia desde la funcion ACME.
                UN_TABLAERROR:  Origen desde el cual se produce el error. 
  */
  (
   UN_EXC_COD IN PLS_INTEGER, 
   UN_ERROR_COD IN PLS_INTEGER:=NULL, 
   UN_SQLERROR IN CLOB :=NULL,
   UN_TABLAERROR IN VARCHAR2 :=NULL,
   UN_REEMPLAZOS IN PCK_SUBTIPOS.TI_CLAVEVALOR :=PCK_SUBTIPOS.MI_CLAVEVALORNULL
  )
RETURN VARCHAR2 
AS
  MI_MSG        VARCHAR2(4000):= NULL;  
  MI_CAMPOS     VARCHAR2(4000);
  MI_VALORES    CLOB;
  MI_RTA        VARCHAR2(4000);
  MI_CODERROR   PLS_INTEGER;
  MI_TXTERROR   VARCHAR2(5000);
  MI_ERROR_NUM  NUMBER;
  MI_ERROR_MSG  VARCHAR2(4000);
  MI_STRSQL     CLOB;
  MI_CODIGO     NUMBER;
  MI_IND        NUMBER;
  MI_I          NUMBER;
  MI_TEM        CLOB;
  MI_ERRINI     NUMBER;


  MI_PILA_ERRORES VARCHAR2(4000 CHAR);
  MI_SALIDA       VARCHAR2(4000 CHAR);
  MI_ERR_ORA      VARCHAR2(4000):=NULL;
  MI_INDRETORNO  NUMBER(1):=0;
  MI_NOMCOMPLETO  VARCHAR2(1000 CHAR);
  MI_TABLA        VARCHAR2(1000 CHAR);
  MI_COLUMNA      VARCHAR2(1000 CHAR);
  MI_REAL         VARCHAR2(50 CHAR);
  MI_MAXIMO       VARCHAR2(50 CHAR);
  MI_MSG_ERROR_I  VARCHAR2(4000 CHAR);
  PRAGMA AUTONOMOUS_TRANSACTION;
BEGIN

    BEGIN
      IF  UN_ERROR_COD IS NOT NULL THEN
        SELECT CHR(10) || MSG_INTERFAZ
        INTO MI_MSG
        FROM ERROR_NEGOCIO
        WHERE ID_CODIGO=UN_ERROR_COD;
      END IF;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      --NULL;
      RETURN 'Error: ' || UN_ERROR_COD || ' Info: ' || UN_SQLERROR;
    END;

    DECLARE
      MI_INDINF PLS_INTEGER;
      MI_INDSUP PLS_INTEGER;
      MI_ERROR_AUX  PLS_INTEGER;
    BEGIN
      MI_TXTERROR := NULL;
      MI_PILA_ERRORES:=DBMS_UTILITY.FORMAT_ERROR_STACK;
      MI_MSG_ERROR_I := MI_PILA_ERRORES;
      IF INSTR(MI_PILA_ERRORES,'@#INI#@Log:') > 0 THEN 
        --RETURN MI_PILA_ERRORES;
        MI_TXTERROR:= MI_PILA_ERRORES;   
        MI_INDRETORNO :=-1;
        --MI_TXTERROR := MI_PILA_ERRORES || CHR(10) || '@#FIN#@';
      END IF;

    IF MI_INDRETORNO=0 THEN 
      WHILE INSTR(MI_PILA_ERRORES,'ORA')>0 LOOP
        MI_INDINF:=INSTR(MI_PILA_ERRORES,'ORA')+3;
        MI_INDSUP := INSTR(MI_PILA_ERRORES,':')-MI_INDINF;
        MI_ERROR_AUX:=TO_NUMBER(SUBSTR(MI_PILA_ERRORES,MI_INDINF,MI_INDSUP));

        SELECT MI_MSG||
        CASE WHEN NVL(MSG_INTERFAZ,MSG_ERROR) IS NOT NULL THEN
        CHR(10)||NVL(MSG_INTERFAZ,MSG_ERROR) ELSE '' END
        INTO MI_MSG
        FROM ERROR
        WHERE ID_CODIGO=MI_ERROR_AUX;
        MI_PILA_ERRORES:=SUBSTR(MI_PILA_ERRORES,INSTR(MI_PILA_ERRORES,CHR(10))+1,LENGTH(MI_PILA_ERRORES));
      END LOOP;
     END IF; 
    END;

  IF UN_EXC_COD IS NOT NULL THEN           --Evalua el error cuando este corresponde a un proceso PL/SQL
    MI_CODERROR := UN_EXC_COD;
  ELSE
    RETURN 'No es posible evaluar el evento sin un codigo de error';
  END IF;

  --verificar si el codigo de error se encuentra ya en la base de datos
  BEGIN



    SELECT   MSG_ERROR    
      INTO   MI_ERR_ORA
      FROM   ERROR    
      WHERE  ID_CODIGO = MI_CODERROR;   

    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_MSG:='N';
  END;  
    --Esta linea es para que me respete el mensaje de cara del cliente que se envia cuando se genera un error por control de un trigger
    -- MI_ERR_ORA
    IF MI_INDRETORNO=0 AND INSTR(SQLERRM,'ORA-04088:',1)>0 THEN
        IF INSTR(MI_PILA_ERRORES,'@#INI#@Log:') > 0 THEN 
          MI_MSG :=SUBSTR(SQLERRM,INSTR(SQLERRM,'@#INI#@')+7,INSTR(SQLERRM,'@#FIN#@')-INSTR(SQLERRM,'@#INI#')-7);
        END IF;
    END IF;

    IF MI_MSG = 'N' THEN 
      MI_MSG := NULL;
      MI_CAMPOS := 'ID_CODIGO,  MSG_ERROR';
      MI_VALORES := '' || MI_CODERROR || ',''' || MI_TXTERROR || '''';
      MI_STRSQL:=' INSERT INTO ERROR '|| '(' || MI_CAMPOS || ') VALUES (' || MI_VALORES || ') '  ;
      EXECUTE IMMEDIATE MI_STRSQL ;
      COMMIT;
    END IF;
    MI_CODIGO:=0;



    --Registar el error en el log de errores
    MI_CAMPOS := 'CODERROR,  LOG_USUARIO, LOG_FECHA, LOG_TABLA_ORIGEN, LOG_COMPT,  DESC_SQL, ID_FORM_MENU, MSG_CAPTURADO, MSG_ORACLE,TRAZA';
    MI_VALORES := REPLACE(UN_SQLERROR,CHR(39),CHR(39) || CHR(39));
    MI_IND  :=LENGTH(MI_VALORES)/3000;
    IF TRUNC(MI_IND)<>MI_IND THEN
      MI_IND := TRUNC(MI_IND)+1;
    END IF;
    MI_TEM :=''' ''';
    IF UN_SQLERROR IS NOT NULL THEN
      FOR MI_I IN 1..MI_IND LOOP
        MI_TEM := MI_TEM || ' || TO_CLOB(q''[' || SUBSTR(MI_VALORES,((MI_I*3000)-3000)+1, 3000) || ']'') ';    
      END LOOP;
    END IF;  
    MI_VALORES := MI_CODERROR || ', ''' || PCK_CONEXION.FC_GETUSER || ''', SYSDATE, ''' || UN_TABLAERROR || ''',''' || PCK_CONEXION.FC_GETIP || ''',' || MI_TEM || ',''' || PCK_CONEXION.FC_GETFORM_MENU || ''',q''[' || SUBSTR(MI_PILA_ERRORES,1,4000) || ']'',q''[' || SQLERRM|| ']'','''||DBMS_UTILITY.FORMAT_ERROR_BACKTRACE|| '''' ;
    MI_STRSQL := ' INSERT INTO LOG_ERROR '|| '(' || MI_CAMPOS || ') VALUES (' || MI_VALORES || ') RETURNING LOG_IDENT INTO :1 ';
    EXECUTE IMMEDIATE MI_STRSQL RETURNING INTO MI_CODIGO;
    COMMIT;

    --IF MI_MSG IS NOT  NULL THEN
    IF MI_TXTERROR IS  NULL THEN
      MI_TXTERROR := '@#INI#@Log: ' || MI_CODIGO  || MI_MSG || CHR(10) || '@#FIN#@';
      IF UN_REEMPLAZOS.COUNT>0 THEN
        FOR i IN UN_REEMPLAZOS.FIRST..UN_REEMPLAZOS.LAST
        LOOP
           MI_TXTERROR :=  REPLACE(MI_TXTERROR,'--' || UN_REEMPLAZOS(i).CLAVE || '--', REPLACE(UN_REEMPLAZOS(i).VALOR, CHR(39), CHR(34)));
        END LOOP;
      END IF;
      --MI_TXTERROR := '@#INI#@Log: ' || MI_CODIGO  || MI_MSG || CHR(10) || '@#FIN#@';         
    END IF;
    --LVEGA 2746
IF MI_MSG_ERROR_I LIKE '%ORA-12899%' THEN
   BEGIN
      DECLARE
         MI_LINEA12899 VARCHAR2(4000);
      BEGIN
         MI_LINEA12899 := REGEXP_SUBSTR(MI_MSG_ERROR_I, 'ORA-12899:[^' || CHR(10) || ']+');
         MI_TXTERROR := REPLACE(MI_TXTERROR,'(real: , máximo: )','');
         MI_TXTERROR := REPLACE(MI_TXTERROR,'@#FIN#@','');
         MI_TXTERROR := MI_TXTERROR || '' || MI_LINEA12899 || '' || '@#FIN#@';
         END;
      END;
END IF;
    --LVEGA 2746
    UPDATE LOG_ERROR SET MSG_CAPTURADO=SUBSTR(MI_TXTERROR,1,4000) WHERE LOG_IDENT=MI_CODIGO;
    COMMIT;

    --IF MI_SALIDA = NULL THEN
      RETURN MI_TXTERROR;  
    --ELSE 
    --  RETURN MI_SALIDA;
    --END IF;
  -- EXCEPTION WHEN OTHERS THEN
      -- SE DEBE VERIFICAR QUE HACER CUANDO SE PRESENTAN EXCEPCIONES EN LA RUTINA DE ERRORES
    --  MI_ERROR_NUM := SQLCODE;
     -- MI_ERROR_MSG := SQLERRM;
     -- RETURN MI_ERROR_MSG || ' - ' || PCK_CONEXION.FC_GETFORM_MENU;  

END FC_EVALUAR_ERROR;  

FUNCTION FC_MENSAJE 
  /* 
    NAME              : FC_MENSAJE
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSÉ PASCUAL GÓMEZ
    DATE MIGRADOR     : 10/10/2017
    TIME              : 10:15 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Este procedimiento sirve como unico punto desde el cual
                        se llaman mensajes con el fin de que no queden quemados en el código de PLSQL
    PARAMETROS DE ENTRADA:
                UN_ERROR_COD:   Codigo del error especifico de negocio almacenado
                                en el paquete PCK_ERRORES y en la tabla ERRORES_NEGOCIO
                                con el mensaje especifico para el usuario.
                UN_REEMPLAZOS:  Listado de parametros que se deben reemplazar de acuerdo a lo 
                                guardado en la tabla ERRORES_NEGOCIO
  */
  (
   UN_MENSAJE_COD IN PLS_INTEGER :=NULL,    
   UN_REEMPLAZOS  IN PCK_SUBTIPOS.TI_CLAVEVALOR :=PCK_SUBTIPOS.MI_CLAVEVALORNULL
  ) 
RETURN VARCHAR2
AS 
MI_MSG   VARCHAR2(32000);
BEGIN
    MI_MSG :='';
    SELECT  MSG_INTERFAZ
    INTO MI_MSG
    FROM ERROR_NEGOCIO
    WHERE ID_CODIGO  = UN_MENSAJE_COD;

    IF MI_MSG IS NOT NULL THEN
      IF UN_REEMPLAZOS.COUNT>0 THEN
      FOR i IN UN_REEMPLAZOS.FIRST..UN_REEMPLAZOS.LAST
        LOOP
          MI_MSG :=  REPLACE(MI_MSG,'--' || UN_REEMPLAZOS(i).CLAVE || '--', REPLACE(UN_REEMPLAZOS(i).VALOR, CHR(39), CHR(34)));
        END LOOP;
      END IF;
    END IF;
    RETURN MI_MSG;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      RETURN '';
END FC_MENSAJE;


END PCK_ERR_MSG;