create or replace PACKAGE BODY PCK_CAMBIONIT AS

  PROCEDURE PR_INSERTAR_TABLA
       /*  
        NAME              : En Access 
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 06/02/2019
        TIME              : 05:30 PM
        SOURCE MODULE     : 
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Permite crear un nuevo registro con el tercero nuevo en base al tercero anterior
                            Si el tercero ya existe como parte de la llave lo consulta    
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME: insertarTablaTercero
        @METHOD:Post
*/   
    (
    UN_COMPANIA_ANT  IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TERCERO_ANT   IN PCK_SUBTIPOS.TI_TERCERO,  
    UN_SUCURSAL_ANT  IN PCK_SUBTIPOS.TI_SUCURSAL,  
    UN_COMPANIA_NUE  IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TERCERO_NUE   IN PCK_SUBTIPOS.TI_TERCERO,  
    UN_SUCURSAL_NUE  IN PCK_SUBTIPOS.TI_SUCURSAL,
    UN_USUARIO       IN PCK_SUBTIPOS.TI_USUARIO  
    ) AS
    MI_PCKDATOS     PCK_SUBTIPOS.TI_RTA_ACME;
    MI_REEMPLAZOS   PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CONSULTA     PCK_SUBTIPOS.TI_CONSULTA;
    MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICIONNUE PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICIONANT PCK_SUBTIPOS.TI_CAMPOS;
    MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
    MI_PARAMETROS   PCK_SUBTIPOS.TI_CAMPOS;
    MI_EXCLUIDOS    PCK_SUBTIPOS.TI_CAMPOS;
    MI_GRUPO        PCK_SUBTIPOS.TI_CAMPOS;
    MI_TIENEREG     PCK_SUBTIPOS.TI_ENTERO;
  BEGIN
    <<TABLASINSERT>>
    FOR RS IN ( SELECT CAMBIOTERCERO_CONFIG.* 
                FROM CAMBIOTERCERO_CONFIG INNER JOIN USER_TABLES 
                  ON CAMBIOTERCERO_CONFIG.TABLA = USER_TABLES.TABLE_NAME
               WHERE CAMBIOTERCERO_CONFIG.OPERACION =1
               ORDER BY CAMBIOTERCERO_CONFIG.ORDEN
    )LOOP      
        MI_TIENEREG :=0;
        MI_CONDICIONNUE := RS.CAMPO_COMPANIA || ' = ''' || UN_COMPANIA_NUE || '''' ||
                ' AND ' || RS.CAMPO_SUCURSAL || ' = ''' || UN_SUCURSAL_NUE || '''' ||
                ' AND ' || RS.CAMPO_TERCERO  || ' = ''' || UN_TERCERO_NUE  || '''';
        MI_CONDICIONANT := RS.CAMPO_COMPANIA || ' = ''' || UN_COMPANIA_ANT || '''' ||
                ' AND ' || RS.CAMPO_SUCURSAL || ' = ''' || UN_SUCURSAL_ANT || '''' ||
                ' AND ' || RS.CAMPO_TERCERO  || ' = ''' || UN_TERCERO_ANT  || '''';
        /**
        * VALIDA QUE EXISTAN REGISTROS EN LA TABLA CON EL TERCERO ANTERIOR
        **/            
        MI_CONSULTA :=' SELECT COUNT(' || RS.CAMPO_COMPANIA || ')    
                        FROM  ' || RS.TABLA || '
                        WHERE ' || MI_CONDICIONANT;
        EXECUTE IMMEDIATE MI_CONSULTA INTO MI_TIENEREG;
        IF MI_TIENEREG <>0 THEN
            MI_TIENEREG:= 1;
            IF INSTR('''' || REPLACE(TRIM(RS.LLAVES),',',''',''') || '''','''' || RS.CAMPO_TERCERO || '''',1) > 0  THEN
                MI_GRUPO:= ',' || TRIM(RS.LLAVES) || ',';
                MI_GRUPO:= TRIM(REPLACE(MI_GRUPO ,',' || RS.CAMPO_COMPANIA  || ',',','));
                MI_GRUPO:= SUBSTR(MI_GRUPO, 2, LENGTH(MI_GRUPO)-2);
                /*
                * SE REALIZA PARA VALIDAR SI A NIVEL DE LLAVE PRIMARIA YA EXISTE EL REGISTRO Y SE REPITE
                */
                MI_CONSULTA :=' SELECT SUM(CONTADOR)
                                FROM (
                                SELECT COUNT(' || RS.CAMPO_COMPANIA || ') CONTADOR   
                                FROM  ' || RS.TABLA  || '
                                WHERE (' || MI_CONDICIONNUE || ')
                                   OR (' || MI_CONDICIONANT || ')
                                GROUP BY ' || MI_GRUPO || ')';
                EXECUTE IMMEDIATE MI_CONSULTA INTO MI_TIENEREG;
            END IF;
            IF MI_TIENEREG=1 THEN
                MI_EXCLUIDOS := RS.CAMPO_COMPANIA || ',' || RS.CAMPO_SUCURSAL || ',' || RS.CAMPO_TERCERO ||',CREATED_BY,DATE_CREATED,MODIFIED_BY,DATE_MODIFIED';
                MI_CAMPOS := PCK_SYSMAN_UTL.FC_LISTA_CAMPOMERGE(UN_TABLA     => RS.TABLA
                                                               ,UN_EXCLUIDOS => MI_EXCLUIDOS
                                                               ,UN_TIPO      => 'IC');
                MI_CONSULTA:= 'SELECT '||
                                  '''' || UN_COMPANIA_NUE || ''',' ||
                                  '''' || UN_SUCURSAL_NUE || ''',' ||
                                  '''' || UN_TERCERO_NUE  || ''',' ||                                  
                                  '''' || UN_USUARIO      || ''',' ||
                                  ' SYSDATE, ' ||
                                  '''' || UN_USUARIO      || ''',' ||
                                  ' SYSDATE, ' ||
                                  MI_CAMPOS || 
                             ' FROM '      || RS.TABLA  ||
                             ' WHERE '     || MI_CONDICIONANT;
                MI_CAMPOS := MI_EXCLUIDOS || ',' || MI_CAMPOS ;
                BEGIN
                    BEGIN
                        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA   => RS.TABLA,
                                                        UN_ACCION  => 'IS',
                                                        UN_CAMPOS  => MI_CAMPOS,
                                                        UN_VALORES => MI_CONSULTA);
                    EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
                    END;
                EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    ROLLBACK;
                    MI_PARAMETROS:='USUARIO  = ''' || UN_USUARIO      || ''',' ||              
                                   'COMPANIA = ''' || UN_COMPANIA_ANT || ''',' || 
                                   'NIT      = ''' || UN_TERCERO_ANT  || ''',' || 
                                   'SUCURSAL = ''' || UN_SUCURSAL_ANT || '''';
                    MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                    MI_REEMPLAZOS (0).VALOR := RS.TABLA;    
                    MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                    MI_REEMPLAZOS (1).VALOR := MI_PARAMETROS;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                            UN_TABLAERROR  => RS.TABLA,
                                            UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_INSERSELECT,                                   
                                            UN_REEMPLAZOS  => MI_REEMPLAZOS);  
                END;
            END IF;
        END IF;        
    END LOOP TABLASINSERT;
END PR_INSERTAR_TABLA;


  PROCEDURE PR_ACTUALIZAR_TABLA
       /*  
        NAME              : En Access 
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 07/02/2019
        TIME              : 08:30 AM
        SOURCE MODULE     : 
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Permite actualizar el registro de una tabla que tiene un tercero
                            Si el tercero ya existe como aprate de la llave se debe tratar
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:actualizarTablaTercero
        @METHOD:Post
*/   
    (
    UN_COMPANIA_ANT  IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TERCERO_ANT   IN PCK_SUBTIPOS.TI_TERCERO,  
    UN_SUCURSAL_ANT  IN PCK_SUBTIPOS.TI_SUCURSAL,  
    UN_COMPANIA_NUE  IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TERCERO_NUE   IN PCK_SUBTIPOS.TI_TERCERO,  
    UN_SUCURSAL_NUE  IN PCK_SUBTIPOS.TI_SUCURSAL,
    UN_USUARIO       IN PCK_SUBTIPOS.TI_USUARIO  
    ) AS
    MI_PCKDATOS     PCK_SUBTIPOS.TI_RTA_ACME;
    MI_REEMPLAZOS   PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CONSULTA     PCK_SUBTIPOS.TI_CONSULTA;
    MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICIONNUE PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICIONANT PCK_SUBTIPOS.TI_CAMPOS;
    MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
    MI_PARAMETROS   PCK_SUBTIPOS.TI_CAMPOS;
    MI_EXCLUIDOS    PCK_SUBTIPOS.TI_CAMPOS;
    MI_GRUPO        PCK_SUBTIPOS.TI_CAMPOS;
    MI_TIENEREG     PCK_SUBTIPOS.TI_ENTERO;
    MI_TABLA        VARCHAR2(100);
    MI_CAMPO        VARCHAR2(32000):='';
    MI_CAMPOS1      VARCHAR2(32000):='';
    MI_CAMPOS2      VARCHAR2(32000):='';
    MI_CAMPOS3      VARCHAR2(32000):='';
    MI_CAMPOS4      VARCHAR2(32000):='';
    MI_STRSQL       VARCHAR2(32000):='';
    MI_N            NUMERIC(3);
    MI_K            NUMERIC(3);
    MI_I            NUMERIC(3);
    MI_CONDICION    PCK_SUBTIPOS.TI_CAMPOS;
    MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR; 
    L_CURSOR            INTEGER;
    L_DUMMY             NUMBER;
    L_DESCRIPTION_TABLE DBMS_SQL.DESC_TAB3;
    TYPE COLUMN_MAP_TYPE IS TABLE OF NUMBER INDEX BY VARCHAR2(3200);
    L_MAPPING_TABLE COLUMN_MAP_TYPE;
    L_COLUMN_VALUE  VARCHAR2(4000);
    MI_MERGEUSING   PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE  PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE  PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_SALDOINICIAL PCK_SUBTIPOS.TI_DOBLE;
  BEGIN
    <<TABLASACTUALIZA>>
    FOR RS IN ( SELECT CAMBIOTERCERO_CONFIG.* 
                FROM CAMBIOTERCERO_CONFIG INNER JOIN USER_TABLES 
                  ON CAMBIOTERCERO_CONFIG.TABLA = USER_TABLES.TABLE_NAME
               WHERE CAMBIOTERCERO_CONFIG.OPERACION = 2
               ORDER BY CAMBIOTERCERO_CONFIG.ORDEN
    )LOOP  
        BEGIN 
            MI_TIENEREG :=0;
            MI_CAMPOS2 := NULL;
            MI_CONDICIONNUE := RS.CAMPO_COMPANIA || ' = ''' || UN_COMPANIA_NUE || '''' ||
                    ' AND ' || RS.CAMPO_SUCURSAL || ' = ''' || UN_SUCURSAL_NUE || '''' ||
                    ' AND ' || RS.CAMPO_TERCERO  || ' = ''' || UN_TERCERO_NUE  || '''';
            MI_CONDICIONANT := RS.CAMPO_COMPANIA || ' = ''' || UN_COMPANIA_ANT || '''' ||
                    ' AND ' || RS.CAMPO_SUCURSAL || ' = ''' || UN_SUCURSAL_ANT || '''' ||
                    ' AND ' || RS.CAMPO_TERCERO  || ' = ''' || UN_TERCERO_ANT  || '''';
            /**
            * VALIDA QUE EXISTAN REGISTROS EN LA TABLA CON EL TERCERO ANTERIOR
            **/            
            MI_CONSULTA :=' SELECT COUNT(' || RS.CAMPO_COMPANIA || ')    
                            FROM  ' || RS.TABLA || '
                            WHERE ' || MI_CONDICIONANT;
            EXECUTE IMMEDIATE MI_CONSULTA INTO MI_TIENEREG;
            IF MI_TIENEREG <> 0 THEN
                MI_TABLA   := RS.TABLA;
                MI_CAMPOS1 := RS.LLAVES;
                MI_CAMPOS2 := MI_CAMPOS1;
                MI_CAMPOS3 := '';
                MI_CAMPOS4 := '';
                MI_N       := 0;
                WHILE INSTR(MI_CAMPOS2,',') > 0 LOOP
                    MI_N:=MI_N+1;
                    MI_CAMPO:=SUBSTR(MI_CAMPOS2,1,INSTR(MI_CAMPOS2,',')-1);
                    MI_CAMPOS2:=SUBSTR(MI_CAMPOS2,INSTR(MI_CAMPOS2,',')+1,LENGTH(MI_CAMPOS2));
                    MI_CAMPOS3:=MI_CAMPOS3||MI_CAMPO||' CAMPO'||MI_N||' ,';
                    MI_CAMPOS4:=MI_CAMPOS4||MI_CAMPO||'=CAMPO'||MI_N||' AND ';
                END LOOP;
                MI_N:=MI_N+1;
                MI_CAMPO:=MI_CAMPOS2;
                MI_CAMPOS3:=MI_CAMPOS3||MI_CAMPO||' CAMPO'||MI_N||' ';
                MI_CAMPOS4:=MI_CAMPOS4||MI_CAMPO||'=CAMPO'||MI_N||' ';

                MI_STRSQL:='SELECT ' || MI_CAMPOS3  ||
                          ' FROM   ' || RS.TABLA ||
                          ' WHERE COMPANIA                   =' || CHR(39) || UN_COMPANIA_ANT || CHR(39) ||
                            ' AND ' || RS.CAMPO_TERCERO  || '=' || CHR(39) || UN_TERCERO_ANT  || CHR(39) ||
                            ' AND ' || RS.CAMPO_SUCURSAL || '=' || CHR(39) || UN_SUCURSAL_ANT || CHR(39);

                L_CURSOR := DBMS_SQL.OPEN_CURSOR;
                DBMS_SQL.PARSE(L_CURSOR, MI_STRSQL, DBMS_SQL.NATIVE);
                DBMS_SQL.DESCRIBE_COLUMNS3(L_CURSOR, L_DUMMY, L_DESCRIPTION_TABLE);
                <<DESCRIPCIONTABLA>>
                FOR MI_I IN 1 .. L_DESCRIPTION_TABLE.COUNT 
                LOOP
                    L_MAPPING_TABLE(L_DESCRIPTION_TABLE(MI_I).COL_NAME) := MI_I;
                    DBMS_SQL.DEFINE_COLUMN(L_CURSOR, MI_I, L_COLUMN_VALUE, 4000);
                END LOOP DESCRIPCIONTABLA;
                L_DUMMY := DBMS_SQL.EXECUTE(L_CURSOR);
                <<LCURSOR>>
                LOOP
                    EXIT WHEN DBMS_SQL.FETCH_ROWS(L_CURSOR)<=0;
                    MI_CAMPOS2:=MI_CAMPOS4;
                    MI_K:=0;
                    <<CAMPOS>>
                    WHILE MI_K<MI_N LOOP
                        MI_K:=MI_K+1;
                        DBMS_SQL.COLUMN_VALUE(L_CURSOR, MI_K, L_COLUMN_VALUE);
                        /*
                        MI_STRSQL:=L_COLUMN_VALUE;
                        MI_STRSQL:=L_DESCRIPTION_TABLE(MI_K).COL_TYPE;
                        MI_STRSQL:=DBMS_TYPES.TYPECODE_VARCHAR2;
                        MI_STRSQL:=DBMS_TYPES.TYPECODE_DATE;
                        MI_STRSQL:=DBMS_TYPES.TYPECODE_NUMBER;
                        */
                        IF L_DESCRIPTION_TABLE(MI_K).COL_TYPE IN (1,9) THEN
                            MI_CAMPOS2:=REPLACE(MI_CAMPOS2,'CAMPO'||MI_K||' ',CHR(39)||L_COLUMN_VALUE||CHR(39));
                        ELSIF L_DESCRIPTION_TABLE(MI_K).COL_TYPE=12 THEN
                            MI_CAMPOS2:=REPLACE(MI_CAMPOS2,'CAMPO'||MI_K||' ',CHR(39)||L_COLUMN_VALUE||CHR(39));
                        ELSIF L_DESCRIPTION_TABLE(MI_K).COL_TYPE=2 THEN
                            MI_CAMPOS2:=REPLACE(MI_CAMPOS2,'CAMPO'||MI_K||' ',L_COLUMN_VALUE);
                        ELSE
                            MI_CAMPOS2:=REPLACE(MI_CAMPOS2,'CAMPO'||MI_K||' ',CHR(39)||L_COLUMN_VALUE||CHR(39));
                        END IF;
                    END LOOP CAMPOS;
                    /*
                    MI_STRSQL:='UPDATE ' || RS.TABLA ||
                                ' SET '||RS.CAMPO_TERCERO ||'='||CHR(39)||UN_TERCERO_NUE   ||CHR(39)||
                                    ','||RS.CAMPO_SUCURSAL||'='||CHR(39)||UN_SUCURSAL_NUE  ||CHR(39)||
                                ' WHERE '||MI_CAMPOS2;
                    */
                    MI_TABLA:=  RS.TABLA;
                    MI_CAMPOS:=   RS.CAMPO_TERCERO  || '=' || CHR(39) || UN_TERCERO_NUE  ||CHR(39)||
                            ','|| RS.CAMPO_SUCURSAL || '=' || CHR(39) || UN_SUCURSAL_NUE ||CHR(39);
                    MI_CONDICION := MI_CAMPOS2;
                    BEGIN
                        MI_TIENEREG:=0;
                        /**
                        * SE VALIDA SI EXISTEN SALDOS INICIALES PARA EL NUEVO TERCERO CON EL FIN DE ACTUALIZARLO
                        */
                        IF MI_TABLA='SALDOSINICIALES' THEN                                            
                           MI_CONDICIONNUE:=REPLACE(REPLACE(MI_CAMPOS2, '''' || UN_TERCERO_ANT || '''', '''' || UN_TERCERO_NUE || ''''), '''' || UN_SUCURSAL_ANT || '''', '''' || UN_SUCURSAL_NUE || '''');
                           MI_CONSULTA := 'SELECT COUNT(' || RS.CAMPO_TERCERO || ')
                                           FROM SALDOSINICIALES
                                           WHERE ' || MI_CONDICIONNUE;
                            EXECUTE IMMEDIATE MI_CONSULTA INTO MI_TIENEREG;
                        END IF;
                        IF MI_TIENEREG <>0 THEN
                            MI_CONSULTA := 'SELECT NVL(SALDOINICIAL,0)
                                           FROM SALDOSINICIALES
                                           WHERE ' || MI_CAMPOS2;
                            EXECUTE IMMEDIATE MI_CONSULTA INTO MI_SALDOINICIAL;
                            MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                            UN_ACCION    => 'M',      
                                                            UN_CAMPOS    => 'SALDOINICIAL=SALDOINICIAL +' || MI_SALDOINICIAL,      
                                                            UN_CONDICION => MI_CONDICIONNUE);
                            BEGIN
                                BEGIN    
                                    MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                                                      UN_ACCION    => 'E',
                                                                      UN_CONDICION => MI_CAMPOS2);                                 
                                EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                                    RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
                                END;
                            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                                ROLLBACK;
                                 MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                                 MI_REEMPLAZOS (0).VALOR :=  MI_TABLA;    
                                 MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                                 MI_REEMPLAZOS (1).VALOR :=  MI_CAMPOS2;
                                 PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                            UN_TABLAERROR  => RS.TABLA,
                                                            UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ELICAMBNIT,                                   
                                                            UN_REEMPLAZOS  => MI_REEMPLAZOS);
                            END; 
                        ELSE
                            MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                            UN_ACCION    => 'M',      
                                                            UN_CAMPOS    => MI_CAMPOS,      
                                                            UN_CONDICION => MI_CONDICION);
                        END IF;
                        --EXCEPTION WHEN DUP_VAL_ON_INDEX THEN
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        ROLLBACK;
                        MI_MSGERROR(1).CLAVE := 'LLAVES';
                        MI_MSGERROR(1).VALOR := MI_CAMPOS2;
                        MI_MSGERROR(2).CLAVE := 'TABLA';
                        MI_MSGERROR(2).VALOR := RS.TABLA;
                        MI_MSGERROR(3).CLAVE := 'NIT_ANTERIOR';
                        MI_MSGERROR(3).VALOR := UN_TERCERO_ANT;
                        MI_MSGERROR(4).CLAVE := 'NIT_NUEVO';
                        MI_MSGERROR(4).VALOR := UN_TERCERO_NUE;
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTTERCERO,                                   
                                                UN_REEMPLAZOS  => MI_MSGERROR);                    
                    END;
                END LOOP LCURSOR;
                DBMS_SQL.CLOSE_CURSOR(L_CURSOR);
            END IF;     
        EXCEPTION WHEN OTHERS THEN   
                        ROLLBACK;
                        MI_MSGERROR(1).CLAVE := 'LLAVES';
                        MI_MSGERROR(1).VALOR := NVL(MI_CAMPOS2,MI_CONSULTA);
                        MI_MSGERROR(2).CLAVE := 'TABLA';
                        MI_MSGERROR(2).VALOR := RS.TABLA;
                        MI_MSGERROR(3).CLAVE := 'NIT_ANTERIOR';
                        MI_MSGERROR(3).VALOR := UN_TERCERO_ANT;
                        MI_MSGERROR(4).CLAVE := 'NIT_NUEVO';
                        MI_MSGERROR(4).VALOR := UN_TERCERO_NUE;  
             PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => -20000,
                                        UN_TABLAERROR  => RS.TABLA,
                                        UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ACTTERCERO,                                   
                                        UN_REEMPLAZOS  => MI_REEMPLAZOS);
        END ;
    END LOOP TABLASACTUALIZA;

END PR_ACTUALIZAR_TABLA;


  PROCEDURE FC_ELIMINAR_TABLA
           /*  
        NAME              : En Access 
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 07/02/2019
        TIME              : 08:30 AM
        SOURCE MODULE     : 
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Permite eliminar el registro de una tabla que tiene un tercero                          
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:eliminarTablaTercero
        @METHOD:Post
*/   
    (
    UN_COMPANIA_ANT  IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TERCERO_ANT   IN PCK_SUBTIPOS.TI_TERCERO,  
    UN_SUCURSAL_ANT  IN PCK_SUBTIPOS.TI_SUCURSAL,
    UN_USUARIO       IN PCK_SUBTIPOS.TI_USUARIO
    ) AS
    MI_PCKDATOS     PCK_SUBTIPOS.TI_RTA_ACME;
    MI_REEMPLAZOS   PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CONSULTA     PCK_SUBTIPOS.TI_CONSULTA;
    MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICIONNUE PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICIONANT PCK_SUBTIPOS.TI_CAMPOS;
    MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
    MI_PARAMETROS   PCK_SUBTIPOS.TI_CAMPOS;
    MI_EXCLUIDOS    PCK_SUBTIPOS.TI_CAMPOS;
    MI_GRUPO        PCK_SUBTIPOS.TI_CAMPOS;
    MI_TIENEREG     PCK_SUBTIPOS.TI_ENTERO;

  BEGIN
    <<TABLASELIMINA>>
    FOR RS IN ( SELECT CAMBIOTERCERO_CONFIG.* 
                FROM CAMBIOTERCERO_CONFIG INNER JOIN USER_TABLES 
                  ON CAMBIOTERCERO_CONFIG.TABLA = USER_TABLES.TABLE_NAME
               WHERE CAMBIOTERCERO_CONFIG.OPERACION =1
               ORDER BY CAMBIOTERCERO_CONFIG.ORDEN DESC
    )LOOP      
        MI_CONDICIONANT := RS.CAMPO_COMPANIA || ' = ''' || UN_COMPANIA_ANT || '''' ||
                ' AND ' || RS.CAMPO_SUCURSAL || ' = ''' || UN_SUCURSAL_ANT || '''' ||
                ' AND ' || RS.CAMPO_TERCERO  || ' = ''' || UN_TERCERO_ANT  || '''';
        BEGIN
            BEGIN    
                MI_PCKDATOS := PCK_DATOS.FC_ACME (UN_TABLA     => RS.TABLA,
                                                  UN_ACCION    => 'E',
                                                  UN_CONDICION => MI_CONDICIONANT);                                 
            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
            END;
        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            ROLLBACK;
            MI_CAMPOS:='USUARIO = ''' || UN_USUARIO      || ''','||
                       'COMPANIA= ''' || UN_COMPANIA_ANT || ''','||
                       'NIT     = ''' || UN_TERCERO_ANT  || ''','||
                       'SUCURSAL= ''' || UN_SUCURSAL_ANT || '''';
             MI_REEMPLAZOS (0).CLAVE := 'TABLA';
             MI_REEMPLAZOS (0).VALOR :=  RS.TABLA;    
             MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
             MI_REEMPLAZOS (1).VALOR :=  MI_CAMPOS;
             PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                        UN_TABLAERROR  => RS.TABLA,
                                        UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ELICAMBNIT,                                   
                                        UN_REEMPLAZOS  => MI_REEMPLAZOS);
        END;        
    END LOOP TABLASELIMINA;
  END FC_ELIMINAR_TABLA;

END PCK_CAMBIONIT;