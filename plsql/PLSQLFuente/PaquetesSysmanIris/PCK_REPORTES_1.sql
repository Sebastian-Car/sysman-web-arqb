create or replace PACKAGE BODY                "PCK_REPORTES" AS



FUNCTION FC_RESULVECONSULTA
  /*
      NAME              : FC_ACTUALIZAELEMENTOINVENTARIO
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA - JULIO CESAR REINA
      DATE MIGRADOR     : 26/05/2016
      TIME              : 3:22 PM
      SOURCE MODULE     : REPORTES
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : FUNCION QUE ARMA UNA CONSULTA DE ACUERDO AL ORDEN DE LOS CAMPOS Y LOS FILTROS DEL WHERE.
      MODIFICATIONS     : 
      
      @NAME: resolverConsulta
    */
(
  UN_COMPANIA     IN VARCHAR2,
  UN_REPORTE      IN VARCHAR2,
  UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN CLOB
AS
MI_CONSULTA         CLOB;
MI_RETORNO          CLOB;
MI_CODIGOCONSULTA   REPORTES.CODIGO_CONSULTA%TYPE;
MI_CAMPOS           CLOB;
MI_CAMPOSTOTAL      CLOB;
MI_CONDICION        CLOB;
MI_REEMPLAZO        PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_CON   VARCHAR2(32000 CHAR);
BEGIN
    MI_CAMPOSTOTAL := 'SELECT ';
    MI_CAMPOS := 'SELECT ';
    
    SELECT REPORTES.CODIGO_CONSULTA,
           CONSULTAS_RP.SQL,
           REPORTES.CONDICION
           INTO 
           MI_CODIGOCONSULTA,
           MI_CONSULTA,
           MI_CONDICION
    FROM REPORTES
      INNER JOIN CONSULTAS_RP
        ON CONSULTAS_RP.COMPANIA = REPORTES.COMPANIA
        AND CONSULTAS_RP.CODIGO_CONSULTA = REPORTES.CODIGO_CONSULTA
    WHERE REPORTES.COMPANIA = UN_COMPANIA
      AND REPORTES.CODIGO_REPORTE = UN_REPORTE;


    FOR RS IN (SELECT CAMPO ||' '|| REPLACE(ETIQUETA,' ','_') || ', ' RESULTADO,
                      CAMPO || ', ' CAMPOS,
                      TIPO
                FROM D_CONSULTAS
                WHERE D_CONSULTAS.COMPANIA = UN_COMPANIA
                  AND CODIGO               = MI_CODIGOCONSULTA
                  AND CODIGO_REPORTE       = UN_REPORTE
                  AND VISIBLE NOT IN (0)
                  ORDER BY ORDEN)
    LOOP
      IF RS.TIPO = 'D' THEN
      MI_CAMPOSTOTAL := MI_CAMPOSTOTAL || 'TO_CHAR('||RS.CAMPOS || ' ''DD/MM/YYYY'' ) '|| RS.CAMPOS;
      ELSE
      MI_CAMPOSTOTAL := MI_CAMPOSTOTAL || RS.RESULTADO;
      END IF; 
      MI_CAMPOS := MI_CAMPOS || RS.CAMPOS;
    END LOOP;
    
    MI_CAMPOSTOTAL := RTRIM(MI_CAMPOSTOTAL,', ');
    
    FOR RS IN (SELECT  UPPER(NOMBRE_PARAMETRO) NOMBRE_PARAMETRO,
                       TIPO_PARAMETRO,
                       VALOR_FILTRO,
                       ETIQUETA_PARAMETRO
                FROM D_PARAMETROSCONSULTAS
                  WHERE D_PARAMETROSCONSULTAS.COMPANIA        = UN_COMPANIA
                    AND D_PARAMETROSCONSULTAS.CODIGO_CONSULTA = MI_CODIGOCONSULTA)
    LOOP
      IF RS.VALOR_FILTRO IS NULL THEN
         BEGIN
          BEGIN
            RAISE PCK_EXCEPCIONES.EXC_SYSMAN; 
          END;
          EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_SYSMAN THEN   
           MI_REEMPLAZO(1).CLAVE := 'PARAMETRO';
           MI_REEMPLAZO(1).VALOR := RS.ETIQUETA_PARAMETRO;
           MI_REEMPLAZO(2).CLAVE := 'TIPO';
           MI_REEMPLAZO(2).VALOR := 'consulta';
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD =>SQLCODE,
                            UN_ERROR_COD=>PCK_ERRORES.ERRR_REPORTES_VALORFILTRO,
                            UN_REEMPLAZOS =>  MI_REEMPLAZO
                          );
         END;
      END IF;
      
      IF RS.TIPO_PARAMETRO IN ('N') THEN
          MI_CONSULTA := REPLACE(UPPER(MI_CONSULTA),RS.NOMBRE_PARAMETRO,RS.VALOR_FILTRO);
        ELSIF RS.TIPO_PARAMETRO IN ('S') THEN
          MI_CONSULTA := REPLACE(UPPER(MI_CONSULTA),RS.NOMBRE_PARAMETRO,''''||RS.VALOR_FILTRO||'''');
        ELSE
          MI_CONSULTA := REPLACE(UPPER(MI_CONSULTA),RS.NOMBRE_PARAMETRO,'TO_DATE('||RS.VALOR_FILTRO||', ''DD/MM/YYYY'')');
        END IF;
    END LOOP;
    
    
     MI_CONSULTA := MI_CAMPOSTOTAL || 
                       ' FROM ('  || MI_CONSULTA    || ') ';
    IF MI_CONDICION IS NOT NULL THEN 
        MI_CONSULTA := MI_CONSULTA || ' WHERE ' ||
                       MI_CONDICION;
    END IF;
   
    
    
    FOR RS IN (SELECT  UPPER(NOMBRE_PARAMETRO) NOMBRE_PARAMETRO,
                       TIPO_PARAMETRO,
                       VALOR_DEFECTO,
                       ETIQUETA_PARAMETRO
                FROM D_PARAMETROS
                  WHERE D_PARAMETROS.COMPANIA        = UN_COMPANIA
                    AND D_PARAMETROS.CODIGO_REPORTE  = UN_REPORTE
                    AND D_PARAMETROS.PORDEFECTO IN (-1))
    LOOP
      IF RS.VALOR_DEFECTO IS NULL THEN
         BEGIN
          BEGIN
            RAISE PCK_EXCEPCIONES.EXC_SYSMAN; 
          END;
          EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_SYSMAN THEN   
           MI_REEMPLAZO(1).CLAVE := 'PARAMETRO';
           MI_REEMPLAZO(1).VALOR := RS.ETIQUETA_PARAMETRO;
           MI_REEMPLAZO(2).CLAVE := 'TIPO';
           MI_REEMPLAZO(2).VALOR := 'reporte';
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD =>SQLCODE,
                            UN_ERROR_COD=>PCK_ERRORES.ERRR_REPORTES_VALORFILTRO,
                            UN_REEMPLAZOS =>  MI_REEMPLAZO
                          );
         END;
      END IF;    
      IF RS.TIPO_PARAMETRO IN ('N') THEN
          MI_CONSULTA := REPLACE(UPPER(MI_CONSULTA),RS.NOMBRE_PARAMETRO,RS.VALOR_DEFECTO);
        ELSIF RS.TIPO_PARAMETRO IN ('S') THEN
          MI_CONSULTA := REPLACE(UPPER(MI_CONSULTA),RS.NOMBRE_PARAMETRO,''''||RS.VALOR_DEFECTO||'''');
        ELSE
          MI_CONSULTA := REPLACE(UPPER(MI_CONSULTA),RS.NOMBRE_PARAMETRO,'TO_DATE('||RS.VALOR_DEFECTO||', ''DD/MM/YYYY'')');
        END IF;
    END LOOP;
    
    FOR RS IN (SELECT  UPPER(NOMBRE_PARAMETRO) NOMBRE_PARAMETRO, 
                       VALOR_FILTRO, 
                       VALOR_FILTRO_FECHA,
                       TIPO,
                       ETIQUETA_PARAMETRO
                FROM D_PARAMETROSIMP
                  WHERE COMPANIA       = UN_COMPANIA
                    AND CODIGO_REPORTE = UN_REPORTE
                    AND USUARIO        = UN_USUARIO)
    LOOP
       IF RS.VALOR_FILTRO IS NULL AND RS.VALOR_FILTRO_FECHA IS NULL THEN
           BEGIN
            BEGIN
              RAISE PCK_EXCEPCIONES.EXC_SYSMAN; 
            END;
            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_SYSMAN THEN
               MI_REEMPLAZO(1).CLAVE := 'PARAMETRO';
               MI_REEMPLAZO(1).VALOR := RS.ETIQUETA_PARAMETRO;
               MI_REEMPLAZO(2).CLAVE := 'TIPO';
               MI_REEMPLAZO(2).VALOR := 'reporte';
                              PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD =>SQLCODE,
                                UN_ERROR_COD=>PCK_ERRORES.ERRR_REPORTES_VALORFILTRO,
                                UN_REEMPLAZOS =>  MI_REEMPLAZO 
                              );
           END;
        END IF;
        IF RS.TIPO IN ('N') THEN
          MI_CONSULTA := REPLACE(UPPER(MI_CONSULTA),RS.NOMBRE_PARAMETRO,RS.VALOR_FILTRO);
        ELSIF RS.TIPO IN ('S') THEN
          MI_CONSULTA := REPLACE(UPPER(MI_CONSULTA),RS.NOMBRE_PARAMETRO,''''||RS.VALOR_FILTRO||'''');
        ELSE
          MI_CONSULTA := REPLACE(UPPER(MI_CONSULTA),RS.NOMBRE_PARAMETRO,'TO_DATE('''||RS.VALOR_FILTRO_FECHA||''', ''DD/MM/YYYY  HH24:MI:SS'')');
        END IF;
    END LOOP;
     MI_CON := MI_CONSULTA;
     MI_CONSULTA := REPLACE(MI_CONSULTA, 's$compania$s', '''' || UN_COMPANIA || '''');
     MI_CONSULTA := REPLACE(MI_CONSULTA, 'S$COMPANIA$S', '''' || UN_COMPANIA || '''');
   RETURN MI_CONSULTA; 
END FC_RESULVECONSULTA;


PROCEDURE PR_CONFIGURARPARAMETROS
/*
      NAME              : PR_CONFIGURARPARAMETROS
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JULIO CESAR REINA
      DATE MIGRADOR     : 19/06/2018
      TIME              : 11:22 AM
      SOURCE MODULE     : REPORTES
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PROCEDIMIENTO QUE INSERTAR LOS PARAMETROS DE LA CONSULTA Y DE LOS REPORTES A 
                          UN CONSOLIDADO GENERAL.
      MODIFICATIONS     : 
      
      @NAME: configurarParametros
    */
(
  UN_COMPANIA           IN VARCHAR2,
  UN_REPORTE            IN VARCHAR2,
  UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO
)
AS
  MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
  MI_EXISTE             PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_REEMPLAZO        PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN 
    MI_TABLA     := 'D_PARAMETROSIMP';
    MI_CAMPOS  := 'ESTADO = 0, 
                   DATE_MODIFIED = SYSDATE, 
                   MODIFIED_BY = '''||UN_USUARIO||'''';
                   
    MI_CONDICION := '   COMPANIA           = ''' || UN_COMPANIA  ||
                    ''' AND USUARIO        = ''' || UN_USUARIO   || 
                    ''' AND CODIGO_REPORTE = ''' || UN_REPORTE   ||'''';
        BEGIN
          BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                                   UN_ACCION    => 'M', 
                                                   UN_CAMPOS    => MI_CAMPOS, 
                                                   UN_CONDICION => MI_CONDICION ); 
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_SYSMAN;
          END;
             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMAN THEN    
                              PCK_ERR_MSG.RAISE_WITH_MSG(
                              UN_EXC_COD   => SQLCODE,
                              UN_ERROR_COD => PCK_ERRORES.ERRR_REPORTES_INSPARAMETROS
                            );
        END;
    
    
     
     
      -- PARAMETROS DEL REPORTE                
      FOR RS IN ( SELECT D_PARAMETROS.CODIGO_PARAMETRO,
                         D_PARAMETROS.NOMBRE_PARAMETRO,
                         D_PARAMETROS.ETIQUETA_PARAMETRO,
                         D_PARAMETROS.CODIGO_FILTRO,
                         D_PARAMETROS.TIPO_PARAMETRO
                  FROM REPORTES
                  INNER JOIN D_PARAMETROS
                    ON REPORTES.COMPANIA        =  D_PARAMETROS.COMPANIA
                    AND REPORTES.CODIGO_REPORTE =   D_PARAMETROS.CODIGO_REPORTE
                  WHERE REPORTES.COMPANIA= UN_COMPANIA
                  AND REPORTES.CODIGO_REPORTE = UN_REPORTE
                  AND D_PARAMETROS.PORDEFECTO = 0) 
      LOOP
        MI_EXISTE := 0;
        MI_VALORES   := '''' || UN_COMPANIA||''',
                         ''' || UN_USUARIO ||''',
                         ''' || RS.CODIGO_PARAMETRO ||''',
                         ''R'',
                         '''|| RS.TIPO_PARAMETRO || ''',
                         '''|| UN_REPORTE ||''',
                         '''|| RS.NOMBRE_PARAMETRO ||''',
                         '''|| RS.ETIQUETA_PARAMETRO ||''',
                         '''|| RS.CODIGO_FILTRO||''',
                         '''|| UN_USUARIO ||''',
                         SYSDATE';
        
        SELECT COUNT(1)
        INTO MI_EXISTE
        FROM D_PARAMETROSIMP
        WHERE COMPANIA = UN_COMPANIA
          AND USUARIO = UN_USUARIO
          AND CODIGO_REPORTE = UN_REPORTE
          AND CODIGO_PARAMETRO = RS.CODIGO_PARAMETRO;
          
        IF MI_EXISTE = 0 THEN 
          IF RS.CODIGO_FILTRO IS NULL THEN
              BEGIN
                BEGIN                
                 RAISE PCK_EXCEPCIONES.EXC_SYSMAN;
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMAN THEN
              MI_REEMPLAZO(1).CLAVE := 'PARAMETRO';
               MI_REEMPLAZO(1).VALOR := RS.ETIQUETA_PARAMETRO;
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                             UN_ERROR_COD  => PCK_ERRORES.ERRR_REPORTES_CODIGOFILTRO,
                                             UN_REEMPLAZOS =>  MI_REEMPLAZO);
          END;       
          END IF;
        
        MI_CAMPOS    := 'COMPANIA,
                      USUARIO,
                      CODIGO_PARAMETRO,
                      TIPO_PARAMETRO,
                      TIPO,
                      CODIGO_REPORTE,
                      NOMBRE_PARAMETRO,
                      ETIQUETA_PARAMETRO,
                      CODIGO_FILTRO,
                      CREATED_BY,
                      DATE_CREATED';
          BEGIN
              BEGIN
                 PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => MI_TABLA,
                                                        UN_ACCION  => 'I',
                                                        UN_CAMPOS  => MI_CAMPOS,
                                                        UN_VALORES => MI_VALORES);
    
                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_SYSMAN;
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMAN THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                               UN_ERROR_COD  => PCK_ERRORES.ERRR_REPORTES_INSPARAMETROS);
          END;
        ELSE 
           MI_CAMPOS  := 'ESTADO            = -1,
                          NOMBRE_PARAMETRO  = '''||RS.NOMBRE_PARAMETRO||''',
                          ETIQUETA_PARAMETRO  = '''||RS.ETIQUETA_PARAMETRO||''',
                          TIPO              = '''||RS.TIPO_PARAMETRO||''',
                          CODIGO_FILTRO     = '''|| RS.CODIGO_FILTRO ||''',
                          DATE_MODIFIED     = SYSDATE, 
                          MODIFIED_BY       = '''||UN_USUARIO||'''';
                   
                   MI_CONDICION := '   COMPANIA           = ''' || UN_COMPANIA  ||
                    ''' AND USUARIO          = ''' || UN_USUARIO   || 
                    ''' AND CODIGO_REPORTE   = ''' || UN_REPORTE   ||
                    ''' AND CODIGO_PARAMETRO = '''||RS.CODIGO_PARAMETRO||'''';
                   BEGIN
          BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                                   UN_ACCION    => 'M', 
                                                   UN_CAMPOS    => MI_CAMPOS, 
                                                   UN_CONDICION => MI_CONDICION ); 
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_SYSMAN;
          END;
             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMAN THEN    
                              PCK_ERR_MSG.RAISE_WITH_MSG(
                              UN_EXC_COD   => SQLCODE,
                              UN_ERROR_COD => PCK_ERRORES.ERRR_REPORTES_INSPARAMETROS
                            );
        END;
        
        
        END IF;
      END LOOP;
      MI_CONDICION := '     COMPANIA       = ''' || UN_COMPANIA  ||
                    ''' AND USUARIO        = ''' || UN_USUARIO   || 
                    ''' AND CODIGO_REPORTE = ''' || UN_REPORTE   ||'''
                        AND ESTADO         IN (0) ';
      BEGIN
        BEGIN
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      =>  MI_TABLA,
                                               UN_ACCION     =>  'E',
                                               UN_CONDICION  =>  MI_CONDICION );
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                           RAISE PCK_EXCEPCIONES.EXC_SYSMAN;
        END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMAN THEN    
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERRR_REPORTES_INSPARAMETROS
                      );
      END;
    
END PR_CONFIGURARPARAMETROS;

FUNCTION FC_DETECTARCAMPOS

/*
    NAME              : FC_DETECTARCAMPOS
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
    DATE MIGRADOR     : 22/06/2018
    TIME              : 10:21 AM  
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : RETORNA LOS CAMPOS DE UN SELECT CON SU RESPECTIVO TIPO DE DATO 
      @NAME:    detectarCampos 
      @METHOD:  GET
*/  
(
    UN_SQL         CLOB
)
RETURN
VARCHAR2
AS
  MI_I            NUMBER:=0;
  MI_SRC_CUR      SYS_REFCURSOR;
  MI_CURID        NUMBER;
  MI_DESCTAB      DBMS_SQL.DESC_TAB;
  MI_COLCNT       NUMBER;
  MI_PRUEBA       NUMBER;
  MI_PRUEBA2      VARCHAR2(250);
  MI_RETORNO      PCK_SUBTIPOS.TI_VALORES;
BEGIN		

	  -- CAMBIA PARAMETROS DE ENTRADA
	  OPEN MI_SRC_CUR FOR UN_SQL;

	  MI_CURID := DBMS_SQL.TO_CURSOR_NUMBER(MI_SRC_CUR);
	  DBMS_SQL.DESCRIBE_COLUMNS(MI_CURID, MI_COLCNT, MI_DESCTAB);
	  -- DEFINE COLUMNS:
	  FOR MI_I IN 1 .. MI_COLCNT LOOP
        IF MI_DESCTAB(MI_I).COL_TYPE = 2 THEN
          MI_RETORNO := MI_RETORNO || MI_DESCTAB(MI_I).COL_NAME || ',.COL.,' || 'N' || ',.REG.,'; --NUMBER
        ELSIF MI_DESCTAB(MI_I).COL_TYPE = 231 THEN
          MI_RETORNO := MI_RETORNO || MI_DESCTAB(MI_I).COL_NAME || ',.COL.,' || 'D' || ',.REG.,'; --FECHA
        ELSIF MI_DESCTAB(MI_I).COL_TYPE = 1 THEN
          MI_RETORNO := MI_RETORNO || MI_DESCTAB(MI_I).COL_NAME || ',.COL.,' || 'S' || ',.REG.,'; --VARCHAR2
        END IF;
	  END LOOP;
	DBMS_SQL.CLOSE_CURSOR(MI_CURID);
  RETURN MI_RETORNO;
END FC_DETECTARCAMPOS;

END PCK_REPORTES;