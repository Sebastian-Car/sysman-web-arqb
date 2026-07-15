create or replace PACKAGE BODY "PCK_DATOS" AS
/**@package:  Datos **/
--------------------------------------------------------------------------------------------------------

FUNCTION FC_ACME
  /*
    NAME              : ACME
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : CARLOS ALBERTO ROJAS
    DATE MIGRADOR     : D
    TIME              : 
    MODIFIER          : SANDRA MILENA DAZA LEGUIZAMON
    DATE MODIFIED     : 19/01/2015 - 23/01/2015
    TIME              : 3:30 PM
    DESCRIPTION       : Arma la sentencia SQL según parametros  recibidos
    MODIFICATIONS     : Incluir manejo de errores 
    @NAME:  ejecutarSentenciaSql
    @METHOD:  GET    
  */
  (  
  UN_TABLA       VARCHAR2,
  UN_ACCION      VARCHAR2 :='',      --  'M' MODIFICAR 'I' INSERTAR 'E' ELIMINAR
  UN_CAMPOS      VARCHAR2 :='',      --  CODIGO,NOMBRE
  UN_VALORES     VARCHAR2 :='',      --  '01','OBJETO'
  UN_ROWID       VARCHAR2 :='0',    --  'AAAM1lAABAAAS5PAAa'
  UN_CONDICION   VARCHAR2 :='',
  UN_MERGEUSING  VARCHAR2 :='', 
  UN_MERGEENLACE VARCHAR2 :='',
  UN_MERGEEXISTE VARCHAR2 :='', 
  UN_MERGENOEXIS VARCHAR2 :='',
  UN_LLAVE VARCHAR2 :=''
  )
RETURN VARCHAR2
AS 
  MI_STRSQL     CLOB; --VARCHAR2(32000);
  MI_STRSQL1    CLOB; --VARCHAR2(32000);
  MI_RTA        VARCHAR2(5000);
  --MI_RTA        VARCHAR2(32000);
  MI_ROWID      VARCHAR2(100);
  MI_BD         VARCHAR2(32000);
  MI_TABLA      VARCHAR2(32000);
  MI_VALORES    CLOB; --VARCHAR2(32000);
  MI_ERROR_FUN  NUMBER := GL_ERROR_NUM + 1;

BEGIN

  IF UN_ACCION = 'I' THEN   
    BEGIN 
      IF(UN_ROWID ='0' OR UN_ROWID IS NULL)THEN
        IF UN_LLAVE IS NOT NULL THEN
          MI_STRSQL:=' INSERT INTO ' || UN_TABLA || '(' || UN_CAMPOS || ') VALUES (' || UN_VALORES || ') RETURNING '||UN_LLAVE||' INTO :1 '  ;  
          EXECUTE IMMEDIATE MI_STRSQL RETURNING INTO MI_RTA;    
        ELSE
          MI_STRSQL:=' INSERT INTO ' || UN_TABLA || '(' || UN_CAMPOS || ') VALUES (' || UN_VALORES || ') '  ;
          EXECUTE IMMEDIATE MI_STRSQL ;
        --COMMIT;
          MI_RTA :=SQL%ROWCOUNT;
        END IF;  
       ELSE 
        MI_STRSQL:=' INSERT INTO ' || UN_TABLA || '(' || UN_CAMPOS || ') VALUES (' || UN_VALORES || ') RETURNING ROWID INTO :1 '  ;
        EXECUTE IMMEDIATE MI_STRSQL RETURNING INTO MI_RTA;
      END IF;
      RETURN MI_RTA;
      EXCEPTION WHEN OTHERS THEN
        --DBMS_OUTPUT.PUT_LINE(MI_STRSQL);
        MI_RTA:= PCK_ERR_MSG.FC_EVALUAR_ERROR(
                UN_EXC_COD =>  SQLCODE,
                UN_SQLERROR => MI_STRSQL,
                UN_TABLAERROR =>UN_TABLA       
        );
        RAISE PCK_EXCEPCIONES.EXC_INSERTAR;            
    END;
  END IF;

  IF UN_ACCION = 'IS' THEN   
    BEGIN       
      IF UN_CAMPOS IS NOT NULL THEN
          MI_STRSQL:=' INSERT INTO ' || UN_TABLA || '(' || UN_CAMPOS || ') ' || UN_VALORES || ' '  ;
      ELSE
          MI_STRSQL:=' INSERT INTO ' || UN_TABLA || ' ' || UN_VALORES || ' '  ;
      END IF;
      EXECUTE IMMEDIATE MI_STRSQL ;
      MI_RTA :=SQL%ROWCOUNT;
      --COMMIT;
      RETURN MI_RTA;
      EXCEPTION WHEN OTHERS THEN
        --DBMS_OUTPUT.PUT_LINE(MI_STRSQL);
        MI_RTA:= PCK_ERR_MSG.FC_EVALUAR_ERROR(
                UN_EXC_COD =>  SQLCODE,
                UN_SQLERROR => MI_STRSQL,
                UN_TABLAERROR =>UN_TABLA       
        );
        RAISE PCK_EXCEPCIONES.EXC_INSERTAR;       
    END;
  END IF;

  IF UN_ACCION = 'M' THEN
    BEGIN
      IF UN_ROWID IS NOT NULL AND UN_ROWID <> '0' THEN
        MI_STRSQL:=' UPDATE ' || UN_TABLA || ' SET ' || UN_CAMPOS || ' WHERE  ROWID='''|| UN_ROWID || ''''  ;
      ELSIF UN_CONDICION IS NOT NULL THEN
        MI_STRSQL:=' UPDATE ' || UN_TABLA || ' SET ' || UN_CAMPOS || ' WHERE ' || UN_CONDICION || ''  ;
      ELSE
        MI_STRSQL:=' UPDATE ' || UN_TABLA || ' SET ' || UN_CAMPOS;
      END IF;  
      EXECUTE IMMEDIATE MI_STRSQL;
      --COMMIT;
      MI_RTA :=SQL%ROWCOUNT; 
      RETURN MI_RTA;

    EXCEPTION WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE(MI_STRSQL);
        MI_RTA:= PCK_ERR_MSG.FC_EVALUAR_ERROR(
                UN_EXC_COD =>  SQLCODE, 
                UN_SQLERROR => MI_STRSQL,
                UN_TABLAERROR =>UN_TABLA       
        );
        RAISE PCK_EXCEPCIONES.EXC_ACTUALIZAR;       
    END;
  END IF;

  IF UN_ACCION = 'E' THEN
      BEGIN
        IF UN_ROWID IS NOT NULL AND UN_ROWID <> '0' THEN
          MI_STRSQL:=' DELETE FROM  ' || UN_TABLA || ' WHERE ROWID='''|| UN_ROWID || '''' ;
        ELSIF UN_CONDICION IS NOT NULL THEN
          MI_STRSQL:=' DELETE FROM  ' || UN_TABLA || ' WHERE '|| UN_CONDICION || '' ;
        ELSE
          RETURN 'FALLO - NO SE DEFINE SI EL FILTRO ES POR ID O ALGUNA CONDICION';
        END IF;  

        EXECUTE IMMEDIATE MI_STRSQL;
        --COMMIT;
        MI_RTA :=SQL%ROWCOUNT;
        RETURN MI_RTA;
      EXCEPTION WHEN OTHERS THEN
        MI_RTA:= PCK_ERR_MSG.FC_EVALUAR_ERROR(
                UN_EXC_COD =>  SQLCODE, 
                UN_SQLERROR => MI_STRSQL,
                UN_TABLAERROR =>UN_TABLA       
        );
        RAISE PCK_EXCEPCIONES.EXC_ELIMINAR;   
      END;
  END IF;
  --11/10/2015 JP ACCIONES COM MERGE PARA MEJORAR LAS CONSULTAS
  --ACTUALIZACION DESDE 
  IF UN_ACCION = 'MM' THEN
       BEGIN
        IF UN_TABLA IS NULL OR UN_MERGEUSING IS NULL OR UN_MERGEENLACE IS NULL OR UN_MERGEEXISTE IS NULL THEN
          RETURN 'FALLO - NO SE DEFINE LOS PARAMETROS NECESARIOS';
        END IF;  
        MI_STRSQL := ' MERGE INTO ' || UN_TABLA || ' TABLA'       || CHR(13) || CHR(10) ||
                     ' USING (' || UN_MERGEUSING || ') VISTA '    || CHR(13) || CHR(10) ||
                     ' ON (' || UN_MERGEENLACE || ')'             || CHR(13) || CHR(10) ||
                     ' WHEN MATCHED THEN '                        || CHR(13) || CHR(10) ||
                     ' ' || UN_MERGEEXISTE;
        EXECUTE IMMEDIATE MI_STRSQL;
        --COMMIT;
        MI_RTA :=SQL%ROWCOUNT;
        RETURN MI_RTA;
      EXCEPTION WHEN OTHERS THEN
      --DBMS_OUTPUT.PUT_LINE(MI_STRSQL);
       MI_RTA:= PCK_ERR_MSG.FC_EVALUAR_ERROR(
                UN_EXC_COD =>  SQLCODE, 
                UN_SQLERROR => MI_STRSQL,
                UN_TABLAERROR =>UN_TABLA       
        );
        RAISE PCK_EXCEPCIONES.EXC_MERGE;
      END;
  END IF;

  --ACTUALIZACION E INSERCCION 
  IF UN_ACCION = 'IM' THEN
      BEGIN
        IF UN_TABLA IS NULL OR UN_MERGEUSING IS NULL OR UN_MERGEENLACE IS NULL OR UN_MERGEEXISTE IS NULL OR UN_MERGENOEXIS IS NULL THEN
          RETURN 'FALLO - NO SE DEFINE LOS PARAMETROS NECESARIOS';
        END IF;
        IF INSTR(UN_MERGEEXISTE,'DELETE WHERE')>0 THEN
          RETURN 'FALLO - NO ES UNA CONSULTA DE ACTUALIZACIÓN E INSERCCIÓN';
        END IF;
        MI_STRSQL := ' MERGE INTO ' || UN_TABLA || ' TABLA '      || CHR(13) || CHR(10) ||
                     ' USING (' || UN_MERGEUSING || ') VISTA '    || CHR(13) || CHR(10) ||
                     ' ON(' || UN_MERGEENLACE || ')'              || CHR(13) || CHR(10);
        MI_STRSQL1:= ' WHEN MATCHED THEN '                        || CHR(13) || CHR(10) ||
                     ' ' || UN_MERGEEXISTE                        || CHR(13) || CHR(10) ||
                     ' WHEN NOT MATCHED THEN '                    || CHR(13) || CHR(10) ||
                     ' ' || UN_MERGENOEXIS;
        --MI_STRSQL := MI_STRSQL || MI_STRSQL1;
        EXECUTE IMMEDIATE MI_STRSQL || MI_STRSQL1;
        --COMMIT;
        MI_RTA :=SQL%ROWCOUNT;
        RETURN MI_RTA;
      EXCEPTION WHEN OTHERS THEN
        MI_RTA:= PCK_ERR_MSG.FC_EVALUAR_ERROR(
                UN_EXC_COD =>  SQLCODE, 
                UN_SQLERROR => MI_STRSQL || MI_STRSQL1,
                UN_TABLAERROR =>UN_TABLA       
        );
        RAISE PCK_EXCEPCIONES.EXC_MERGE;
      END;
  END IF;

  --SI NO HAY REGISTROS
  IF UN_ACCION = 'IN' THEN
      BEGIN
        IF UN_TABLA IS NULL OR UN_MERGEUSING IS NULL OR UN_MERGEENLACE IS NULL OR UN_MERGENOEXIS IS NULL THEN
          RETURN 'FALLO - NO SE DEFINE LOS PARAMETROS NECESARIOS';
        END IF;
        IF INSTR(UN_MERGEEXISTE,'DELETE WHERE')>0 THEN
          RETURN 'FALLO - NO ES UNA CONSULTA DE ACTUALIZACIÓN E INSERCCIÓN';
        END IF;
        MI_STRSQL := ' MERGE INTO ' || UN_TABLA || ' TABLA '      || CHR(13) || CHR(10) ||
                     ' USING (' || UN_MERGEUSING || ') VISTA '    || CHR(13) || CHR(10) ||
                     ' ON(' || UN_MERGEENLACE || ')'              || CHR(13) || CHR(10) ||
                     ' WHEN NOT MATCHED THEN '                    || CHR(13) || CHR(10) ||
                     ' ' || UN_MERGENOEXIS;
        EXECUTE IMMEDIATE MI_STRSQL;
        --COMMIT;
        MI_RTA :=SQL%ROWCOUNT;
        RETURN MI_RTA;
      EXCEPTION WHEN OTHERS THEN
        MI_RTA:= PCK_ERR_MSG.FC_EVALUAR_ERROR(
                UN_EXC_COD =>  SQLCODE, 
                UN_SQLERROR => MI_STRSQL,
                UN_TABLAERROR =>UN_TABLA       
        );
        RAISE PCK_EXCEPCIONES.EXC_MERGE;
      END;
  END IF;

  --ELIMINACION DESDE 
  IF UN_ACCION = 'EM' THEN
      BEGIN
        IF UN_TABLA IS NULL OR UN_MERGEUSING IS NULL OR UN_MERGEENLACE IS NULL OR UN_MERGEEXISTE IS NULL THEN
          RETURN 'FALLO - NO SE DEFINE LOS PARAMETROS NECESARIOS';
        END IF;  
        IF INSTR(UN_MERGEEXISTE,'DELETE WHERE')<1 THEN
          RETURN 'FALLO - NO ES UNA CONSULTA DE ELIMINACIÓN';
        END IF;
        MI_STRSQL := ' MERGE INTO ' || UN_TABLA || ' TABLA'       || CHR(13) || CHR(10) ||
                     ' USING (' || UN_MERGEUSING || ') VISTA '    || CHR(13) || CHR(10) ||
                     ' ON (' || UN_MERGEENLACE || ')'             || CHR(13) || CHR(10) ||
                     ' WHEN MATCHED THEN '                        || CHR(13) || CHR(10) ||
                     ' ' || UN_MERGEEXISTE;
        EXECUTE IMMEDIATE MI_STRSQL;
        --COMMIT;
        MI_RTA :=SQL%ROWCOUNT;
        RETURN MI_RTA;

      EXCEPTION WHEN OTHERS THEN
        MI_RTA:= PCK_ERR_MSG.FC_EVALUAR_ERROR(
                UN_EXC_COD =>  SQLCODE, 
                UN_SQLERROR => MI_STRSQL,
                UN_TABLAERROR =>UN_TABLA       
        );
        RAISE PCK_EXCEPCIONES.EXC_MERGE;
      END;
  END IF;


RETURN MI_RTA;

END FC_ACME;



--FUNCTION ACME
  /*
    NAME              : ACME
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : CARLOS ALBERTO ROJAS
    DATE MIGRADOR     : 
    TIME              : 
    MODIFIER          : SANDRA MILENA DAZA LEGUIZAMON
    DATE MODIFIED     : 19/01/2015 - 23/01/2015
    TIME              : 3:30 PM
    DESCRIPTION       : Arma la sentencia SQL seg¿os parametros  recibidos
    MODIFICATIONS     : Incluir manejo de errores    
  */
 /* (
  UN_TABLA     VARCHAR2,
  UN_ACCION    VARCHAR2:='',      --  'M' MODIFICAR 'I' INSERTAR 'E' ELIMINAR
  UN_CAMPOS    VARCHAR2:='',      --  CODIGO,NOMBRE
  UN_VALORES   VARCHAR2:='',      --  '01','OBJETO'
  UN_ROWID     VARCHAR2 := '',       --  'AAAM1lAABAAAS5PAAa'
  UN_CONDICION VARCHAR2:=''
  )
RETURN NUMBER AS 

  MI_STRSQL     VARCHAR2(32000);
  MI_RTA           NUMBER;
  MI_RTA        VARCHAR2(32000);
  MI_ROWID      VARCHAR2(32000);
  MI_BD         VARCHAR2(32000);
  MI_TABLA      VARCHAR2(32000);
  MI_VALORES    VARCHAR2(32000);
BEGIN

  IF UN_ACCION = 'I' THEN   
    BEGIN       
      MI_STRSQL:=' INSERT INTO ' || UN_TABLA || '(' || UN_CAMPOS || ') VALUES (' || UN_VALORES || ') '  ;
      EXECUTE IMMEDIATE MI_STRSQL ;
      --COMMIT;
      MI_RTA :=SQL%ROWCOUNT;
      RETURN MI_RTA;
      EXCEPTION WHEN OTHERS THEN
        MI_ERROR_NUM := SQLCODE;
        MI_ERROR_MSG := SQLERRM;
        MI_RTA := SYSMAN_UTL.EVALUAR_ERROR(MI_ERROR_NUM, MI_ERROR_MSG, CONEXION.GETUSER, UN_TABLA, CONEXION.GETIP, 'INSERTAR', 'CONTABILIDAD', MI_STRSQL, '0');
        RAISE_APPLICATION_ERROR(-20000, MI_ERROR_NUM || ' ' || MI_RTA);
        RETURN -1;
    END;
  END IF;

  IF UN_ACCION = 'IS' THEN   
    BEGIN       
      MI_STRSQL:=' INSERT INTO ' || UN_TABLA || '(' || UN_CAMPOS || ') ' || UN_VALORES || ' '  ;
      EXECUTE IMMEDIATE MI_STRSQL ;
      MI_RTA :=SQL%ROWCOUNT;
      --COMMIT;
      RETURN MI_RTA;
      EXCEPTION WHEN OTHERS THEN
        MI_ERROR_NUM := SQLCODE;
        MI_ERROR_MSG := SQLERRM;
        MI_RTA := SYSMAN_UTL.EVALUAR_ERROR(MI_ERROR_NUM, MI_ERROR_MSG, CONEXION.GETUSER, UN_TABLA, CONEXION.GETIP, 'INSERTAR', 'CONTABILIDAD', MI_STRSQL, '0');
        RAISE_APPLICATION_ERROR(-20000, MI_ERROR_NUM || ' ' || MI_RTA);
        RETURN -1;
    END;
  END IF;

  IF UN_ACCION = 'M' THEN
    BEGIN
      IF UN_ROWID IS NOT NULL THEN
        MI_STRSQL:=' UPDATE ' || UN_TABLA || ' SET ' || UN_CAMPOS || ' WHERE  ROWID='''|| UN_ROWID || ''''  ;
      ELSIF UN_CONDICION IS NOT NULL THEN
        MI_STRSQL:=' UPDATE ' || UN_TABLA || ' SET ' || UN_CAMPOS || ' WHERE ' || UN_CONDICION || ''  ;
      ELSE
        MI_STRSQL:=' UPDATE ' || UN_TABLA || ' SET ' || UN_CAMPOS;
      END IF;  
      EXECUTE IMMEDIATE MI_STRSQL;
      --COMMIT;
      MI_RTA :=SQL%ROWCOUNT;
      RETURN MI_RTA;

    EXCEPTION WHEN OTHERS THEN
        MI_ERROR_NUM := SQLCODE;
        MI_ERROR_MSG := SQLERRM;
        MI_RTA := SYSMAN_UTL.EVALUAR_ERROR(MI_ERROR_NUM, MI_ERROR_MSG, CONEXION.GETUSER, UN_TABLA, CONEXION.GETIP, 'ACTUALIZAR', 'CONTABILIDAD', MI_STRSQL, '0');
        RAISE_APPLICATION_ERROR(-20000, MI_ERROR_NUM || ' ' || MI_RTA);
        RETURN -1;
    END;
  END IF;

  IF UN_ACCION = 'E' THEN
      BEGIN
        IF UN_ROWID IS NOT NULL THEN
          MI_STRSQL:=' DELETE FROM  ' || UN_TABLA || ' WHERE ROWID='''|| UN_ROWID || '''' ;
        ELSIF UN_CONDICION IS NOT NULL THEN
          MI_STRSQL:=' DELETE FROM  ' || UN_TABLA || ' WHERE '|| UN_CONDICION || '' ;
        ELSE
          RETURN 'FALLO - NO SE DEFINE SI EL FILTRO ES POR ID O ALGUNA CONDICION';
        END IF;  

        EXECUTE IMMEDIATE MI_STRSQL;
        --COMMIT;
        MI_RTA :=SQL%ROWCOUNT;
        RETURN MI_RTA;
      EXCEPTION WHEN OTHERS THEN
        MI_ERROR_NUM := SQLCODE;
        MI_ERROR_MSG := SQLERRM;
        MI_RTA := SYSMAN_UTL.EVALUAR_ERROR(MI_ERROR_NUM, MI_ERROR_MSG, CONEXION.GETUSER, UN_TABLA, CONEXION.GETIP, 'ELIMINAR', 'CONTABILIDAD', MI_STRSQL, '0');
        RAISE_APPLICATION_ERROR(-20000, MI_ERROR_NUM || ' ' || MI_RTA);
        RETURN -1;
      END;
  END IF;

RETURN MI_RTA;

END ACME;*/

FUNCTION FC_MODULONOMINA
/*
    NAME              : FC_MODULONOMINA
    AUTHORS           : JOSE PASCUAL GOMEZ BLANCO
    DATE              : 15/08/2015
    TIME              : 9:04 AM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       :  Para que no sigan dejando el modulo quemado                    
    @NAME:  retornarCodigoAplicacionNomina
    @METHOD:  GET        
  */
RETURN NUMBER
AS
  MI_ERROR_FUN  NUMBER:=GL_ERROR_NUM + 2;
BEGIN
  RETURN MODULONOMINA;
END FC_MODULONOMINA;

  FUNCTION FC_MODULOHOJASDEVIDA
  /*
    NAME              : FC_MODULOHOJASDEVIDA
    AUTHORS           : PABLO ANDRES ESPITIA CUCA
    DATE              : 19/12/2017
    TIME              : 09:45 AM
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       :  Retorna el codigo del modulo de hojas de vida.                    
    @NAME:  retornarCodigoAplicacionHojasDeVida
    @METHOD:  GET        
    */
  RETURN NUMBER
  AS
  BEGIN
    RETURN MODULOHOJASDEVIDA;
  END FC_MODULOHOJASDEVIDA;

FUNCTION FC_MODULOBANCOPROY
/*
    NAME              : FC_MODULOBANCOPROY
    AUTHORS           : JOSE PASCUAL GOMEZ BLANCO
    DATE              : 15/08/2015
    TIME              : 9:04 AM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       :  Para que no sigan dejando el modulo quemado                    
    @NAME:  retornarCodigoAplicacionBancoDeProyectos
    @METHOD:  GET        
  */
RETURN NUMBER
AS
  MI_ERROR_FUN  NUMBER:=GL_ERROR_NUM + 3;
BEGIN
  RETURN MODULOBANCOPROY;
END FC_MODULOBANCOPROY;

FUNCTION FC_CONS_AUXILIAR
/*
    NAME              : FC_MODULOBANCOPROY
    AUTHORS           : JOSE PASCUAL GOMEZ BLANCO
    DATE              : 24/11/2015
    TIME              : 03:34 PM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       :  REtorna la constante de variso para la auxiliar                   
    @NAME:  retornarConstanteVariosParaAuxiliar
    @METHOD:  GET        
  */
RETURN VARCHAR2
AS
  MI_ERROR_FUN  NUMBER:=GL_ERROR_NUM + 4;
BEGIN
  RETURN CONS_AUXILIAR;
END FC_CONS_AUXILIAR;

FUNCTION FC_CONS_FUENTE
/*
    NAME              : FC_CONS_FUENTE
    AUTHORS           : SANDRA MILENA DAZA LEGUIZAMON
    DATE              : 25/11/2015
    TIME              : 08:39 AM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       :  Retorna la constante de varios para la fuente de recurso
    @NAME:  retornarConstanteVariosParaFuenteDeRecursos
    @METHOD:  GET            
  */
RETURN VARCHAR2
AS
  MI_ERROR_FUN  NUMBER:=GL_ERROR_NUM + 5;
BEGIN
  RETURN CONS_FUENTE;
END FC_CONS_FUENTE;

FUNCTION FC_MODULOALMACEN
/*
    NAME              : FC_MODULOALMACEN
    AUTHORS           : JOSE PASCUAL GOMEZ BLANCO
    DATE              : 22/12/2015
    TIME              : 9:04 AM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       :  Para que no sigan dejando el modulo quemado                    
    @NAME:  retornarCodigoAplicacionAlmacen
    @METHOD:  GET            
  */
RETURN NUMBER
AS
  MI_ERROR_FUN  NUMBER:=GL_ERROR_NUM + 6;
BEGIN
  RETURN MODULOALMACEN;
END FC_MODULOALMACEN;

FUNCTION FC_MODULOPRECONTRACTUAL
/*
    NAME              : FC_MODULOPRECONTRACTUAL
    AUTHORS           : JOSE PASCUAL GOMEZ BLANCO
    DATE              : 22/12/2015
    TIME              : 9:04 AM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       :  Para que no sigan dejando el modulo quemado                    
    @NAME:  retornarCodigoAplicacionPrecontractual
    @METHOD:  GET                
  */
RETURN NUMBER
AS
  MI_ERROR_FUN  NUMBER:=GL_ERROR_NUM + 7;
BEGIN
  RETURN MODULOPRECONTRACTUAL;
END FC_MODULOPRECONTRACTUAL;

FUNCTION FC_MODULOCONTRATOS 
/*
    NAME              : FC_MODULOCONTRATOS
    AUTHORS           : DIEGO ALFREDO SUESCA
    DATE              : 29/12/2015
    TIME              : 15:08 AM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       :  Para que no sigan dejando el modulo quemado                    
    @NAME:  retornarCodigoAplicacionContratacion
    @METHOD:  GET                
  */
RETURN NUMBER
AS
  MI_ERROR_FUN  NUMBER:=GL_ERROR_NUM + 11;
BEGIN
  RETURN MODULOCONTRATOS;
END FC_MODULOCONTRATOS; 

FUNCTION FC_CONS_CENTRO
/*
    @NAME:  retornarConstanteVariosParaCentroDeCosto
    @METHOD:  GET            
*/
RETURN VARCHAR2
AS
  MI_ERROR_FUN  NUMBER:=GL_ERROR_NUM + 4;
BEGIN
  RETURN CONS_CENTRO;
END FC_CONS_CENTRO;

FUNCTION FC_CONS_TERCERO
/*
    @NAME:  retornarConstanteVariosParaTercero
    @METHOD:  GET            
*/    
RETURN VARCHAR2
AS
  MI_ERROR_FUN  NUMBER:=GL_ERROR_NUM + 4;
BEGIN
  RETURN CONS_TERCERO;
END FC_CONS_TERCERO;

FUNCTION FC_CONS_SUCURSAL
/*
    @NAME:  retornarConstanteVariosParaSucursalDeTercero
    @METHOD:  GET            
*/    
RETURN VARCHAR2
AS
  MI_ERROR_FUN  NUMBER:=GL_ERROR_NUM + 4;
BEGIN
  RETURN CONS_SUCURSAL;
END FC_CONS_SUCURSAL;

FUNCTION FC_CONS_REFERENCIA
/*
    @NAME:  retornarConstanteVariosParaReferencia
    @METHOD:  GET            
*/
RETURN VARCHAR2
AS
  MI_ERROR_FUN  NUMBER:=GL_ERROR_NUM + 4;
BEGIN
  RETURN CONS_REFERENCIA;
END FC_CONS_REFERENCIA;

FUNCTION FC_CONS_PROCEDENCIA_TRAMITE
/*
  NAME              : FC_CONS_PROCEDENCIA_TRAMITE
  AUTHORS           : STEFANINI SYSMAN
  AUTHOR CREATION   : PABLO ANDRES ESPITIA CUCA
  DATE CREATION     : 16/11/2018
  TIME              : 08:44
  SOURCE MODULE     : WORKFLOW (35)
  DESCRIPTION       : Get de la procedencia tramite interna.
  MODIFIED BY       : 

  @NAME  : retornarProcedenciaTramite
  @METHOD: GET
*/
RETURN VARCHAR2
AS
BEGIN
  RETURN CONS_PROCEDENCIA_TRAMITE;
END FC_CONS_PROCEDENCIA_TRAMITE;

FUNCTION FC_CONS_SERIE_DOCUMENTAL
/*
  NAME              : FC_CONS_SERIE_DOCUMENTAL
  AUTHORS           : STEFANINI SYSMAN
  AUTHOR CREATION   : PABLO ANDRES ESPITIA CUCA
  DATE CREATION     : 16/11/2018
  TIME              : 10:26
  SOURCE MODULE     : WORKFLOW (35)
  DESCRIPTION       : Get de la serie documental interna.
  MODIFIED BY       : 

  @NAME  : retornarSerieDocumental
  @METHOD: GET
*/
RETURN VARCHAR2
AS
BEGIN
  RETURN CONS_PROCEDENCIA_TRAMITE;
END FC_CONS_SERIE_DOCUMENTAL;

FUNCTION FC_MODULOCONTABILIDAD
/*
    NAME              : FC_MODULOCONTABILIDAD
    AUTHORS           : EDGAR LEONARDO SARMIENTO
    DATE              : 28/03/2016
    TIME              : 12:50 PM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       :  Retorna el código del módulo de contabilidad
    @NAME:  retornarCodigoAplicacionContabilidad
    @METHOD:  GET                
  */
RETURN NUMBER
AS
BEGIN
  RETURN MODULOCONTABILIDAD;
END FC_MODULOCONTABILIDAD;


FUNCTION FC_CONS_MAX_ID
/*
    @NAME:  retornarConstanteMaximaDelId
    @METHOD:  GET                
*/
RETURN VARCHAR2
AS
BEGIN
  RETURN CONS_MAX_ID;
END;

FUNCTION FC_MODULOPREDIAL
/*
    NAME              : FC_MODULOPREDIAL
    AUTHORS           : LEYDI MILENA CORTÉS FORERO
    DATE              : 08/06/2016
    TIME              : 16:33 PM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       :  Retorna el código del módulo de predial
    @NAME:  retornarCodigoAplicacionPredial
    @METHOD:  GET                    
  */
RETURN NUMBER
AS
BEGIN
  RETURN MODULOPREDIAL;
END FC_MODULOPREDIAL;

FUNCTION FC_MODULOSERVICIOSPUBLICOS
/*
    NAME              : FC_MODULOSERVICIOSPUBLICOS
    AUTHORS           : YESIKA PAOLA BECERRA CASTRO
    DATE              : 20/08/2016
    TIME              : 17:33 PM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       :  Retorna el código del módulo de Servicios publicos
    @NAME:  retornarCodigoAplicacionServiciosPublicos
    @METHOD:  GET                    
  */
RETURN NUMBER
AS
BEGIN
  RETURN MODULOSERVICIOSPUBLICOS;
END FC_MODULOSERVICIOSPUBLICOS;

FUNCTION FC_MODULOPRESUPUESTO
/*
    NAME              : FC_MODULOPRESUPUESTO
    AUTHORS           : JESSICA LISSETH RAMIREZ BRICEÑO
    DATE              : 12/01/2017
    TIME              : 03:47 PM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       :  RETORNA EL CODIGO DEL MODULO DE PRESUPUESTO
    @NAME:  retornarCodigoAplicacionPresupuesto
    @METHOD:  GET                    
  */
RETURN NUMBER
AS
BEGIN
  RETURN MODULOPRESUPUESTO;
END FC_MODULOPRESUPUESTO;

FUNCTION FC_MODULOPLANEACION
/*
    NAME              : FC_MODULOPLANEACION
    AUTHORS           : PABLO ANDRES ESPITIA CUCA
    DATE              : 02/08/2017
    TIME              : 04:20 PM
    MODIFIED BY       : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       :  RETORNA EL CODIGO DEL MODULO DE PLANEACION
    @NAME:  retornarCodigoAplicacionPlaneacion
    @METHOD:  GET                    
  */
RETURN NUMBER
AS
BEGIN
  RETURN MODULOPLANEACION;
END FC_MODULOPLANEACION;

FUNCTION FC_MODULOENTESDECONTROL
/*
    NAME              : FC_MODULOENTESDECONTROL
    AUTHORS           : LEYDI MILENA CORTÉS FORERO
    DATE              : 09/03/2017
    TIME              : 02:55 PM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       :  RETORNA EL CODIGO DEL MODULO DE ENTES DE CONTROL
    @NAME:  retornarCodigoAplicacionEntesDeControl
    @METHOD:  GET                    
  */
RETURN NUMBER
AS
BEGIN
  RETURN MODULOENTESDECONTROL;
END FC_MODULOENTESDECONTROL;

FUNCTION FC_MODULOPLANDESARROLLO
/*
    NAME              : FC_MODULOPLANDESARROLLO
    AUTHORS           : LAURA MELIZA BOTIA PEREZ  
    DATE              : 20/03/2018
    TIME              : 02:14 PM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       :  RETORNA EL CODIGO DEL MODULO PLAN DE DESARROLLO
    @NAME:  retornarCodigoAplicacionPlanDesarrollo
    @METHOD:  GET                    
  */
RETURN NUMBER
AS
BEGIN
  RETURN MODULOPLANDESARROLLO;
END FC_MODULOPLANDESARROLLO;

FUNCTION FC_MODULOFACTURACIONGENERAL
/*
    NAME              : FC_MODULOFACTURACIONGENERAL
    AUTHORS           : YESIKA PAOLA BECERRA CASTRO
    DATE              : 10/11/2017
    TIME              : 09:42 AM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       :  RETORNA EL CODIGO DEL MODULO FACTURACION GENERAL -> SYSMANSF
    @NAME:  retornarCodigoAplicacionFacturacionGeneral
    @METHOD:  GET                    
  */
RETURN NUMBER
AS
BEGIN
  RETURN MODULOFACTURACIONGENERAL;
END FC_MODULOFACTURACIONGENERAL;

END PCK_DATOS;