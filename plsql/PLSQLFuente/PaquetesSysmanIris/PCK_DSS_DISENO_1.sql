CREATE OR REPLACE PACKAGE BODY                "PCK_DSS_DISENO" AS

FUNCTION REEMPLAZOSSQL 
  (
    STRSQL1 IN CLOB 
  ) RETURN CLOB AS
    STRSQL   VARCHAR2(32000):=STRSQL1;
    STRSQLFIN VARCHAR2(32000);
    BUSCAR   VARCHAR2(10) :='FROM ';
    BUSCART  VARCHAR2(32000):='';
    NPAR     NUMBER:=0;
    NCOM     NUMBER:=0;
    J        NUMBER:=0;
    K        NUMBER:=1;
    MI_CAMPO VARCHAR2(32000):='';
    MI_CAMPO_FIN VARCHAR2(32000):='';
    MI_RTA   VARCHAR2(32000):='';
    TYPE tipoCursor IS REF CURSOR;
    RS tipoCursor;
    MI_CAMPOTIPO TI_ALIASTIPO;
  BEGIN
    STRSQL:=REPLACE(STRSQL,CHR(13)||CHR(10),' ');
    STRSQL:=REPLACE(STRSQL,CHR(13),' ');
    STRSQL:=REPLACE(STRSQL,CHR(10),' ');
    STRSQL:=REPLACE(STRSQL,CHR(9),' ');
    --STRSQL:=REPLACE(STRSQL,'  ',' ');
    --STRSQL:=REPLACE(STRSQL,' '||CHR(39)||CHR(39)||' ','NULL');
    WHILE INSTR(STRSQL, '  ')>0
    LOOP
      STRSQL:=REPLACE(STRSQL,'  ',' ');
    END LOOP;
    
    IF SUBSTR(LTRIM(STRSQL),1,7)<>'SELECT ' AND SUBSTR(LTRIM(STRSQL),1,7)<>'SELECT'||CHR(10) AND SUBSTR(LTRIM(STRSQL),1,7)<>'SELECT'||CHR(13) THEN
       RETURN '';
    END IF;
    
    --BUSCO LOS ALIAS Y TIPOS UNA SOLA VEZ
    MI_CAMPOTIPO :=ALIASTIPO(STRSQL);
    
    /*
    IF MI_CAMPOTIPO.COUNT>0 THEN
      FOR i IN MI_CAMPOTIPO.FIRST..MI_CAMPOTIPO.LAST
      LOOP
         DBMS_OUTPUT.PUT_LINE(MI_CAMPOTIPO(i).CAMPO || ' - ' || MI_CAMPOTIPO(i).TIPO);
      END LOOP;
    END IF;
    */
    
    STRSQL := SUBSTR(STRSQL,8,LENGTH(STRSQL));
    STRSQLFIN:='SELECT ';
        
    -- BUSCA EL FROM  
    FOR J IN 1..LENGTH(STRSQL) LOOP
      --IF SUBSTR(STRSQL,J,1)=CHR(39) AND SUBSTR(STRSQL,J-1,1)<>CHR(39) THEN
      IF SUBSTR(STRSQL,J,1)=CHR(39)  THEN
        IF NCOM=1 THEN
          NCOM:=0;
        ELSE
          NCOM:=1;
        END IF;
      END IF;     
      IF SUBSTR(STRSQL,J,1)='(' AND NCOM=0 THEN
        NPAR:=NPAR+1;
      END IF;  
      IF SUBSTR(STRSQL,J,1)=')' AND NCOM=0 THEN
        NPAR:=NPAR-1;
      END IF;     
      IF  NPAR =0 AND NCOM=0 AND SUBSTR(STRSQL,J,1)=',' THEN
        MI_CAMPO_FIN := CONVERTIRCAMPOSQL(SUBSTR(STRSQL,K,J-K),MI_CAMPOTIPO);  
        STRSQLFIN := STRSQLFIN || MI_CAMPO_FIN || ',';
        K :=J+1;        
      END IF;
      IF NPAR=0 AND NCOM=0 AND SUBSTR(STRSQL,J,1)<>CHR(39) AND SUBSTR(STRSQL,J,1)<>'(' AND SUBSTR(STRSQL,J,1)<>')' THEN
        IF UPPER(SUBSTR(STRSQL,J,LENGTH(BUSCAR)))=BUSCAR THEN
           MI_CAMPO_FIN := CONVERTIRCAMPOSQL(SUBSTR(STRSQL,K,J-K),MI_CAMPOTIPO);  
           STRSQLFIN := STRSQLFIN || MI_CAMPO_FIN || ',';
           K:=J;
           EXIT;
        END IF;
      END IF;
    END LOOP;
    STRSQLFIN := TRIM(STRSQLFIN);
    IF SUBSTR(STRSQLFIN,LENGTH(STRSQLFIN),1)=',' THEN
      STRSQLFIN:= SUBSTR(STRSQLFIN,1,LENGTH(STRSQLFIN)-1);
    END IF;
    RETURN STRSQLFIN || ' ' || SUBSTR(STRSQL,K);
  END REEMPLAZOSSQL;

FUNCTION CONVERTIRCAMPOSQL 
(
  STRSQL IN CLOB,
  MI_CAMPOTIPO TI_ALIASTIPO
) RETURN CLOB AS
    MI_CAMPO_FIN VARCHAR2(32000);
    MI_ALIAS     VARCHAR2(32000) DEFAULT '';
    MI_CAMPO     VARCHAR2(32000);
  BEGIN
    MI_CAMPO:=TRIM(STRSQL);
    MI_CAMPO:=LTRIM(MI_CAMPO);
    IF INSTR(MI_CAMPO,'.')=0 AND INSTR(MI_CAMPO,' ')=0 AND INSTR(MI_CAMPO,'(')=0 AND INSTR(MI_CAMPO,')')=0 THEN
      MI_ALIAS:=MI_CAMPO;      
    ELSIF INSTR(MI_CAMPO,' AS ')>0 THEN
      MI_ALIAS := TRIM(SUBSTR(MI_CAMPO,INSTR(MI_CAMPO,' AS '), LENGTH(MI_CAMPO)));
      MI_CAMPO := SUBSTR(MI_CAMPO,1,INSTR(MI_CAMPO,' AS '));      
    ELSIF INSTR(MI_CAMPO,' ')>0 AND (LENGTH(MI_CAMPO)-LENGTH(REPLACE(MI_CAMPO,' ','')))/LENGTH(' ')=1 THEN
      MI_ALIAS := TRIM(SUBSTR(MI_CAMPO,INSTR(MI_CAMPO,' ') + 1, LENGTH(MI_CAMPO)));      
      MI_CAMPO := TRIM(SUBSTR(MI_CAMPO,1,INSTR(MI_CAMPO,' ')));      
    ELSIF INSTR(MI_CAMPO,'.')>0 AND (LENGTH(MI_CAMPO)-LENGTH(REPLACE(MI_CAMPO,'.','')))/LENGTH('.')=1 THEN
      MI_ALIAS := TRIM(SUBSTR(MI_CAMPO,INSTR(MI_CAMPO,'.') + 1, LENGTH(MI_CAMPO)));
    ELSE
      MI_ALIAS :=SUBSTR(MI_CAMPO,INSTR(MI_CAMPO,' ',-1)+1);
      MI_CAMPO := TRIM(SUBSTR(MI_CAMPO,1,INSTR(MI_CAMPO,' ',-1)));      
    END IF;
    MI_CAMPO_FIN :=STRSQL;
    IF MI_CAMPOTIPO.COUNT>0 THEN
      FOR i IN MI_CAMPOTIPO.FIRST..MI_CAMPOTIPO.LAST
      LOOP
        IF MI_ALIAS = MI_CAMPOTIPO(i).CAMPO THEN
          IF MI_CAMPOTIPO(i).TIPO='boolean' THEN
            MI_CAMPO_FIN:= '(CASE ' || MI_CAMPO || ' WHEN 0 THEN ''false'' ELSE ''true'' END) '  || MI_ALIAS;
          ELSIF MI_CAMPOTIPO(i).TIPO='date' THEN
            MI_CAMPO_FIN:= '(TO_CHAR(' || MI_CAMPO || ',''DD/MM/YYYY HH24:MI:SS'')) '  || MI_ALIAS;
          END IF;
        END IF;
         --DBMS_OUTPUT.PUT_LINE(MI_CAMPOTIPO(i).CAMPO || ' - ' || MI_CAMPOTIPO(i).TIPO);
      END LOOP;
    END IF;
    RETURN MI_CAMPO_FIN;

  END CONVERTIRCAMPOSQL;

FUNCTION ALIASTIPO 
  (
  STRSQL CLOB
  )RETURN TI_ALIASTIPO AS
  l_columnValue   varchar2(4000);
  l_status        integer;
  l_colCnt        number default 0;
  l_cnt           number default 0;
  l_line          long;
  l_descTbl       dbms_sql.desc_tab;
  l_theCursor     integer default dbms_sql.open_cursor;
  --MI_RTA          CLOB;
  MI_TIPO         VARCHAR2(1000);
  MI_RTA          TI_ALIASTIPO;
  BEGIN
    dbms_sql.parse(l_theCursor,  STRSQL, dbms_sql.native );
    dbms_sql.describe_columns( c       => l_theCursor,
                              col_cnt => l_colCnt,
                              desc_t  => l_descTbl );
    for i in 1 .. l_colCnt
    loop
      MI_TIPO := NULL;
      IF  (l_descTbl(i).col_type=2 AND l_descTbl(i).col_precision=1 AND l_descTbl(i).col_scale=0) THEN
          MI_TIPO:= 'boolean';       
       ELSIF  l_descTbl(i).col_type=2 AND l_descTbl(i).col_precision<9 AND l_descTbl(i).col_scale=0 THEN
          MI_TIPO:= 'integer';
       ELSIF  l_descTbl(i).col_type=2 AND l_descTbl(i).col_precision>=9 AND l_descTbl(i).col_precision>18 AND l_descTbl(i).col_scale=0 THEN
          MI_TIPO:= 'long';
       ELSIF  l_descTbl(i).col_type=2 AND l_descTbl(i).col_precision<9 AND l_descTbl(i).col_scale<=6 THEN           
          MI_TIPO:= 'double';          
       ELSIF  l_descTbl(i).col_type IN(180,181,231) THEN
          MI_TIPO:= 'date';          
       ELSE 
          MI_TIPO:= 'string';          
       END IF;
       /*
      IF  (l_descTbl(i).col_type=2 AND l_descTbl(i).col_precision=1 AND l_descTbl(i).col_scale=0) THEN
        MI_TIPO:= 'boolean';       
      ELSIF  l_descTbl(i).col_type=2 AND l_descTbl(i).col_precision<=9 AND l_descTbl(i).col_scale=0 THEN
        MI_TIPO:= 'integer';
      ELSIF  l_descTbl(i).col_type=2 AND l_descTbl(i).col_precision>9 AND l_descTbl(i).col_scale=0 THEN
        MI_TIPO:= 'long';
      ELSIF  l_descTbl(i).col_type=2  THEN           
        MI_TIPO:= 'double';          
      ELSIF  l_descTbl(i).col_type IN(180,181,231) THEN
        MI_TIPO:= 'date';          
      ELSE 
        MI_TIPO:= 'string';          
      END IF;*/
      --MI_RTA:= MI_RTA || l_descTbl(i).col_name || ';' ||  MI_TIPO || CHR(13) || CHR(10);
      MI_RTA(i).CAMPO  := l_descTbl(i).col_name ;
      MI_RTA(i).TIPO   := MI_TIPO;
    end loop;
    dbms_sql.close_cursor(l_theCursor);
    RETURN MI_RTA;
  exception when others then dbms_sql.close_cursor( l_theCursor );
    raise;
END ALIASTIPO;

FUNCTION SQLTIPOS(UN_STRSQL CLOB)
RETURN CLOB
AS
l_columnValue   varchar2(4000);
       l_status        integer;
       l_colCnt        number default 0;
       l_cnt           number default 0;
       l_line          long;
       l_descTbl       dbms_sql.desc_tab;
       l_theCursor     integer default dbms_sql.open_cursor;
       MI_RTA          CLOB;
       MI_TIPO         VARCHAR2(1000);
   begin
       dbms_sql.parse(l_theCursor,  UN_STRSQL, dbms_sql.native );
       dbms_sql.describe_columns( c       => l_theCursor,
                                  col_cnt => l_colCnt,
                                  desc_t  => l_descTbl );
        
       for i in 1 .. l_colCnt
       loop
           MI_TIPO := NULL;
           IF  (l_descTbl(i).col_type=2 AND l_descTbl(i).col_precision=1 AND l_descTbl(i).col_scale=0) THEN
              MI_TIPO:= 'boolean';       
           ELSIF  l_descTbl(i).col_type=2 AND l_descTbl(i).col_precision<9 AND l_descTbl(i).col_scale=0 THEN
              MI_TIPO:= 'integer';
           ELSIF  l_descTbl(i).col_type=2 AND l_descTbl(i).col_precision>=9 AND l_descTbl(i).col_precision>18 AND l_descTbl(i).col_scale=0 THEN
              MI_TIPO:= 'long';
           ELSIF  l_descTbl(i).col_type=2 AND l_descTbl(i).col_precision<9 AND l_descTbl(i).col_scale<=6 THEN           
              MI_TIPO:= 'double';          
           ELSIF  l_descTbl(i).col_type IN(180,181,231) THEN
              MI_TIPO:= 'date';          
           ELSE 
              MI_TIPO:= 'string';          
           END IF;
           /*
           IF  (l_descTbl(i).col_type=2 AND l_descTbl(i).col_precision=1 AND l_descTbl(i).col_scale=0) THEN
              MI_TIPO:= 'boolean';       
           ELSIF  l_descTbl(i).col_type=2 AND l_descTbl(i).col_precision<=9 AND l_descTbl(i).col_scale=0 THEN
              MI_TIPO:= 'integer';
           ELSIF  l_descTbl(i).col_type=2 AND l_descTbl(i).col_precision>9 AND l_descTbl(i).col_scale=0 THEN
              MI_TIPO:= 'long';
           ELSIF  l_descTbl(i).col_type=2  THEN           
              MI_TIPO:= 'double';          
           ELSIF  l_descTbl(i).col_type IN(180,181,231) THEN
              MI_TIPO:= 'date';          
           ELSE 
              MI_TIPO:= 'string';          
           END IF;*/
           MI_RTA:= MI_RTA || l_descTbl(i).col_name || ';' ||  MI_TIPO || CHR(13) || CHR(10);
        end loop;
       dbms_sql.close_cursor(l_theCursor);
       RETURN MI_RTA;
   exception
       when others then dbms_sql.close_cursor( l_theCursor );
           raise;
   end ;


FUNCTION SQLTIPOS_SALIDA(UN_STRSQL CLOB)
RETURN CLOB
AS
l_columnValue   varchar2(4000);
       l_status        integer;
       l_colCnt        number default 0;
       l_cnt           number default 0;
       l_line          long;
       l_descTbl       dbms_sql.desc_tab;
       l_theCursor     integer default dbms_sql.open_cursor;
       MI_RTA          CLOB;
       MI_TIPO         VARCHAR2(1000);
   begin
       dbms_sql.parse(l_theCursor,  UN_STRSQL, dbms_sql.native );
       dbms_sql.describe_columns( c       => l_theCursor,
                                  col_cnt => l_colCnt,
                                  desc_t  => l_descTbl );
 
       for i in 1 .. l_colCnt
       loop
           MI_TIPO := NULL;
           IF  (l_descTbl(i).col_type=2 AND l_descTbl(i).col_precision=1 AND l_descTbl(i).col_scale=0) THEN
              MI_TIPO:= 'boolean';       
           ELSIF  l_descTbl(i).col_type=2 AND l_descTbl(i).col_precision<9 AND l_descTbl(i).col_scale=0 THEN
              MI_TIPO:= 'integer';
           ELSIF  l_descTbl(i).col_type=2 AND l_descTbl(i).col_precision>=9 AND l_descTbl(i).col_precision>18 AND l_descTbl(i).col_scale=0 THEN
              MI_TIPO:= 'long';
           ELSIF  l_descTbl(i).col_type=2 AND l_descTbl(i).col_precision<9 AND l_descTbl(i).col_scale<=6 THEN           
              MI_TIPO:= 'double';          
           ELSIF  l_descTbl(i).col_type IN(180,181,231) THEN
              MI_TIPO:= 'date';          
           ELSE 
              MI_TIPO:= 'string';          
           END IF;
           MI_RTA:= MI_RTA || l_descTbl(i).col_name || ';' ||  MI_TIPO || CHR(13) || CHR(10);
        end loop;
       dbms_sql.close_cursor(l_theCursor);
       RETURN MI_RTA;
   exception
       when others then dbms_sql.close_cursor( l_theCursor );
           raise;
   end ;

FUNCTION SQLINSERTDEFAULT 
  (
    STRSQL1 IN CLOB 
  ) RETURN CLOB AS
    STRSQL   VARCHAR2(32000):=STRSQL1;
    STRSQLFIN CLOB;
    J        NUMBER:=0;
    TOT      NUMBER:=0;
    REG      NUMBER:=0;
    MI_TABLA VARCHAR2(32000):='';
    MI_CAMPOS VARCHAR2(32000):='';
    MI_CAMPO VARCHAR2(32000):='';
    MI_DEFAULT VARCHAR2(32000):='';
    MI_PERMITENULL VARCHAR2(32000):='';
    MI_RTA   VARCHAR2(32000):='';
    RS SYS_REFCURSOR;
  BEGIN
    STRSQL:=STRSQL1;
    STRSQL:=REPLACE(STRSQL,CHR(13)||CHR(10),' ');
    STRSQL:=REPLACE(STRSQL,CHR(13),' ');
    STRSQL:=REPLACE(STRSQL,CHR(10),' ');
    STRSQL:=REPLACE(STRSQL,CHR(9),' ');
    WHILE INSTR(STRSQL, '  ')>0
    LOOP
      STRSQL:=REPLACE(STRSQL,'  ',' ');
    END LOOP;
    
    IF SUBSTR(LTRIM(STRSQL),1,12)<>'INSERT INTO ' THEN
       RETURN '';
    END IF;
    
    J        :=INSTR(STRSQL,'(');
    MI_TABLA := SUBSTR(STRSQL,13,J-14);
    MI_TABLA := LTRIM(TRIM(MI_TABLA));
    J        :=J+1;
    MI_CAMPOS:= SUBSTR(STRSQL,J,INSTR(STRSQL,')')-J);
    MI_CAMPOS:=REPLACE(MI_CAMPOS,' ','');
    MI_CAMPOS:= '''' || MI_CAMPOS || '''';
    MI_CAMPOS:= REPLACE( MI_CAMPOS,',', ''',''');
    --Número de campos por repetición de la coma
    TOT:= (LENGTH(MI_CAMPOS)-LENGTH(REPLACE(MI_CAMPOS,',','')))/LENGTH(',')+1;
      
    STRSQL   := 'SELECT COLUMN_NAME, DATA_DEFAULT, NULLABLE
                 FROM USER_TAB_COLUMNS
                 WHERE TABLE_NAME  =''' || MI_TABLA || '''
                   AND COLUMN_NAME IN (' || MI_CAMPOS || ')';
    BEGIN
      OPEN RS FOR STRSQL;
      LOOP
        FETCH RS INTO   MI_CAMPO
                      , MI_DEFAULT
                      ,MI_PERMITENULL;
        EXIT WHEN RS%NOTFOUND;
          REG :=RS%ROWCOUNT;  
          IF MI_CAMPO <>'CREATED_BY' AND MI_CAMPO<>'DATE_CREATED' THEN
            IF MI_DEFAULT IS NOT NULL  AND TRIM(MI_DEFAULT)<>'NULL' THEN
              STRSQLFIN:= STRSQLFIN ||MI_CAMPO || ';' || MI_DEFAULT || CHR(13) || CHR(10);
            ELSIF MI_PERMITENULL='Y' THEN
              STRSQLFIN:= STRSQLFIN ||MI_CAMPO || ';nullesnull' || CHR(13) || CHR(10);
            END IF;
          END IF;
        END LOOP RS;
      CLOSE RS;
      IF REG<>TOT THEN
        STRSQLFIN:='#@#'; 
      END IF;
  
    EXCEPTION WHEN NO_DATA_FOUND THEN
      RETURN '#@#';
    END ;        
    RETURN STRSQLFIN;
  END SQLINSERTDEFAULT;
  
  FUNCTION FC_CLASE_JAVA 
  /*
    NAME              : FC_PAQUETES
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ
    DATE MIGRADOR     : 11/04/2017
    TIME              : 8:05 AM     
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       : GENERA LA CADENA PARA CREAR LA CLASE BASE DE LOS LLAMADOS DE LOS PROCEDIMIENTO Y FUNCIONES EN JAVA
    MODIFICATIONS     : 
    @NAME:  generaTipoJava
*/
  (
    UN_OWNER   IN VARCHAR2,
    UN_PAQUETE IN VARCHAR2,
    UN_PAQUETEJAVA IN VARCHAR2,
    UN_NOMBRE_CLASEJAVA IN VARCHAR2 
  ) RETURN CLOB AS
    STRSQL   VARCHAR2(32000):=UN_PAQUETE;
    
    MI_LINEANUM  NUMBER(20,0); 
    MI_LINEA     CLOB; 
    MI_NOMBRE    VARCHAR2(200);
    MI_PAQUETE   VARCHAR2(200);
    MI_TEXTO     CLOB; 
    MI_SALIDA    CLOB; 
    MI_POS       INTEGER;
    MI_DEFINICION VARCHAR2(32000);
    MI_RETORNO    VARCHAR2(32000);
    MI_PARAMETROS VARCHAR2(32000);
    MI_TIPO       VARCHAR2(2);
    MI_I          INTEGER;
    
  BEGIN  
    MI_PAQUETE := UN_PAQUETE;
    MI_PAQUETE := SUBSTR(MI_PAQUETE,5,LENGTH(MI_PAQUETE));
    MI_PAQUETE := INITCAP(REPLACE(MI_PAQUETE,'_',' '));
    MI_PAQUETE := REPLACE(MI_PAQUETE,' ','');
    
    MI_PAQUETE :='Ejb' || REPLACE(MI_PAQUETE,' ','');
    
    MI_SALIDA :='package com.sysman.' || UN_PAQUETEJAVA || '.ejb.impl;' || CHR(13)||CHR(10) ||
                'import com.sysman.exception.SystemException;' || CHR(13)||CHR(10) ||
                'import com.sysman.persistencia.AccionesImp;' || CHR(13)||CHR(10) ||
                'import com.sysman.persistencia.ConectorPool;' || CHR(13)||CHR(10) ||
                'import com.sysman.' || UN_PAQUETEJAVA || '.ejb.Ejb' || UN_NOMBRE_CLASEJAVA || 'Local;' || CHR(13)||CHR(10) ||
                'import com.sysman.' || UN_PAQUETEJAVA || '.ejb.Ejb' || UN_NOMBRE_CLASEJAVA || 'Remote;' || CHR(13)||CHR(10) ||
                 'EJB_ETAD_EJB' ||             
                'import java.sql.Types;' || CHR(13)||CHR(10) ||                    
                'import javax.ejb.LocalBean;' || CHR(13)||CHR(10) ||
                'import javax.ejb.Stateless;' || CHR(13)||CHR(10) || CHR(13)||CHR(10) ||
                '/**' || CHR(13)||CHR(10) ||
                ' * Session Bean implementation class ' || UN_NOMBRE_CLASEJAVA || '' || CHR(13)||CHR(10) ||
                ' */' || CHR(13)||CHR(10) ||
                '@Stateless' || CHR(13)||CHR(10) ||
                '@LocalBean' || CHR(13)||CHR(10) ||
                'public class Ejb' || UN_NOMBRE_CLASEJAVA || ' implements Ejb' || UN_NOMBRE_CLASEJAVA || 'Remote, Ejb' || UN_NOMBRE_CLASEJAVA || 'Local {' || CHR(13)||CHR(10) ||
                '    /**' || CHR(13)||CHR(10) ||
                '     * Default constructor.' || CHR(13)||CHR(10) ||
                '     */' || CHR(13)||CHR(10) ||
                '    public ' || UN_NOMBRE_CLASEJAVA || '() {' || CHR(13)||CHR(10) ||
                '    }' || CHR(13)||CHR(10) ;
    <<RecorrerFuncionesProcedimiento>>
    FOR RS IN(  
              SELECT LINE EMPIEZA,
                     LEAD(LINE) OVER (ORDER BY LINE)-1 FINAL,
                     TEXT       
              FROM ALL_SOURCE
              WHERE OWNER = UN_OWNER
                AND NAME  = UN_PAQUETE
                AND TYPE  = 'PACKAGE BODY'
                AND (TRIM(TEXT) LIKE 'FUNCTION %' OR TRIM(TEXT) LIKE 'PROCEDURE %')
              ORDER BY LINE
              )
    LOOP
      --Se deja un solo Espacio
      MI_LINEA:= RS.TEXT;
      WHILE INSTR(MI_LINEA, '  ')>0
      LOOP
        MI_LINEA:=REPLACE(MI_LINEA,'  ',' ');
      END LOOP;
      MI_LINEA :=TRIM(MI_LINEA);
      --Se consulta donde inicia procedimiento o función
      IF SUBSTR(MI_LINEA,1,9) = 'FUNCTION ' THEN
        MI_NOMBRE:= SUBSTR(MI_LINEA,10,LENGTH(MI_LINEA));
        MI_TIPO  :='F';
      ELSIF SUBSTR(MI_LINEA,1,10) = 'PROCEDURE ' THEN
        MI_NOMBRE:= SUBSTR(MI_LINEA,11,LENGTH(MI_LINEA));
        MI_TIPO  :='P';
      END IF;
      
      IF INSTR(MI_NOMBRE, ' ',1)>0 THEN
        MI_NOMBRE:= SUBSTR(MI_NOMBRE,1,INSTR(MI_NOMBRE, ' ',1));
      END IF;
      IF INSTR(MI_NOMBRE, '--',1)>0 THEN
        MI_NOMBRE:= SUBSTR(MI_NOMBRE,1,INSTR(MI_NOMBRE, '--',1));
      END IF;
      IF INSTR(MI_NOMBRE, '(',1)>0 THEN
        MI_NOMBRE:= SUBSTR(MI_NOMBRE,1,INSTR(MI_NOMBRE, '(',1));
      END IF;
      MI_NOMBRE:= replace(MI_NOMBRE,CHR(13),'');
      MI_NOMBRE:= replace(MI_NOMBRE,CHR(10),'');
      MI_NOMBRE:=UN_PAQUETE || '.' || TRIM(MI_NOMBRE);
      
      --Identificar la definición del metodo
      MI_DEFINICION := FC_DEFINICION_JAVA(UN_OWNER, UN_PAQUETE, RS.EMPIEZA, NVL(RS.FINAL,RS.EMPIEZA+50));      
      --Identificar el retorno de la función
      MI_RETORNO    := FC_RETORNO_JAVA(UN_OWNER, UN_PAQUETE, RS.EMPIEZA, NVL(RS.FINAL,RS.EMPIEZA+50), MI_TIPO);           
      --Generar parametros de  la función  o procedimiento
      MI_PARAMETROS:= FC_PARAMETROS_JAVA(UN_OWNER, UN_PAQUETE, RS.EMPIEZA, NVL(RS.FINAL,RS.EMPIEZA+50));
      
      MI_DEFINICION :=  CHR(13)||CHR(10) ||
                   '    @Override' || CHR(13)||CHR(10) ||
                   '    public ' || ' ' || MI_RETORNO || ' ' || MI_DEFINICION || '(' ||
                   MI_PARAMETROS ||') ' || CHR(13)||CHR(10) ||
                   '                      throws SystemException {'  || CHR(13)||CHR(10);
                   
      
      IF MI_TIPO='F' THEN
        IF MI_RETORNO = 'Clob' THEN
          MI_DEFINICION := MI_DEFINICION || '         return Acciones.clobToString((' || MI_RETORNO || ') ';           
        ELSE  
          MI_DEFINICION := MI_DEFINICION || '         return (' || MI_RETORNO || ') ';           
        END IF;
        MI_DEFINICION := MI_DEFINICION || ' AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, ';           
      ELSE
        MI_DEFINICION := MI_DEFINICION || '         AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, ';      
      END IF;
      MI_DEFINICION := MI_DEFINICION || CHR(34) || MI_NOMBRE || CHR(34) || ','  || CHR(13)||CHR(10) ;    
      --Se incorporan los parmetros del llamado de la fucnión
      MI_DEFINICION := MI_DEFINICION || FC_PARAMETROS_ORACLE(UN_OWNER, UN_PAQUETE, RS.EMPIEZA, NVL(RS.FINAL,RS.EMPIEZA+50));
      MI_DEFINICION := MI_DEFINICION ;
      
      IF MI_TIPO='F' THEN
        MI_DEFINICION := MI_DEFINICION || ',' || CHR(13)||CHR(10);
        IF FC_TIPOS_JAVA(MI_RETORNO,-1) = 'String' THEN
          MI_DEFINICION := MI_DEFINICION || '                Types.VARCHAR';               
        ELSIF FC_TIPOS_JAVA(MI_RETORNO,-1) = 'Clob' THEN
          MI_DEFINICION := MI_DEFINICION || '                Types.CLOB';               
        ELSIF FC_TIPOS_JAVA(MI_RETORNO,-1) = 'double' THEN
          MI_DEFINICION := MI_DEFINICION || '                Types.NUMERIC';               
        ELSIF FC_TIPOS_JAVA(MI_RETORNO,-1) = 'long' THEN
          MI_DEFINICION := MI_DEFINICION || '                Types.LONG';               
        ELSIF  FC_TIPOS_JAVA(MI_RETORNO,-1) = 'int' THEN
          MI_DEFINICION := MI_DEFINICION || '                Types.INTEGER';               
        ELSIF FC_TIPOS_JAVA(MI_RETORNO,-1) = 'Date' THEN
          MI_DEFINICION := MI_DEFINICION || '                Types.DATE';
        ELSIF FC_TIPOS_JAVA(MI_RETORNO,-1) = 'boolean' THEN
          MI_DEFINICION := MI_DEFINICION || '                Types.BOOLEAN';
        ELSE
          MI_DEFINICION := MI_DEFINICION || '                Types. ';
        END IF;
      END IF;
      IF MI_RETORNO = 'Clob' THEN
         MI_DEFINICION := MI_DEFINICION || '));'  || CHR(13)||CHR(10);
      ELSE  
         MI_DEFINICION := MI_DEFINICION || ');'  || CHR(13)||CHR(10);
      END IF;
     
      MI_DEFINICION := MI_DEFINICION || '    }';
      
      MI_SALIDA:=MI_SALIDA || MI_DEFINICION || CHR(13)||CHR(10);
    END LOOP RecorrerFuncionesProcedimiento;  
    IF INSTR(MI_SALIDA, 'Date',1)>0 THEN
      MI_SALIDA:= REPLACE(MI_SALIDA,'EJB_ETAD_EJB','import java.util.Date;'  || CHR(13)||CHR(10));
    ELSE
      MI_SALIDA:= REPLACE(MI_SALIDA,'EJB_ETAD_EJB','') ;
    END IF;
    RETURN MI_SALIDA   || '}';
END FC_CLASE_JAVA;



 FUNCTION FC_CLASE_INTERFAZ 
  /*
    NAME              : FC_CLASE_INTERFAZ
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ
    DATE MIGRADOR     : 11/04/2017
    TIME              : 8:05 AM     
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       : GENERA LA CADENA PARA CREAR LA CLASE DE LA INTERFAZ BIEN SEA LOCAL O REMOTA
    MODIFICATIONS     : 
    @NAME:  generaTipoJava
*/
  (
    UN_OWNER   IN VARCHAR2,
    UN_PAQUETE IN VARCHAR2,
    UN_CLASE   IN VARCHAR2,
    UN_PAQUETEJAVA IN VARCHAR2,
    UN_NOMBRE_CLASEJAVA IN VARCHAR2 
  ) RETURN CLOB AS
    STRSQL   VARCHAR2(32000):=UN_PAQUETE;
    MI_LINEA     CLOB; 
    MI_NOMBRE    VARCHAR2(200);
    MI_PAQUETE   VARCHAR2(200);
    MI_SALIDA    CLOB; 
    MI_DEFINICION VARCHAR2(32000);
    MI_RETORNO    VARCHAR2(32000);
    MI_PARAMETROS VARCHAR2(32000);
    MI_TIPO       VARCHAR2(2);
    
  BEGIN  
    MI_PAQUETE := UN_PAQUETE;
    MI_PAQUETE := SUBSTR(MI_PAQUETE,5,LENGTH(MI_PAQUETE));
    MI_PAQUETE := INITCAP(REPLACE(MI_PAQUETE,'_',' '));
    MI_PAQUETE := REPLACE(MI_PAQUETE,' ','');
    
    
    MI_PAQUETE :='Ejb' || REPLACE(MI_PAQUETE,' ','');
    
    MI_SALIDA :='package com.sysman.' || UN_PAQUETEJAVA || '.ejb;' || CHR(13)||CHR(10) ||
                'import com.sysman.exception.SystemException;' || CHR(13)||CHR(10) ||
                'EJB_ETAD_EJB' ||                
                'import javax.ejb.' || UN_CLASE || ';' || CHR(13)||CHR(10) ||
                '@' || UN_CLASE || CHR(13)||CHR(10) ||
                'public interface Ejb' || REPLACE(UN_NOMBRE_CLASEJAVA,' ','') || UN_CLASE || ' {' || CHR(13)||CHR(10);
    <<RecorrerFuncionesProcedimiento>>
    FOR RS IN(  
              SELECT LINE EMPIEZA,
                     LEAD(LINE) OVER (ORDER BY LINE)-1 FINAL,
                     TEXT       
              FROM ALL_SOURCE
              WHERE OWNER = UN_OWNER
                AND NAME  = UN_PAQUETE
                AND TYPE  = 'PACKAGE BODY'
                AND (TRIM(TEXT) LIKE 'FUNCTION %' OR TRIM(TEXT) LIKE 'PROCEDURE %')
              ORDER BY LINE
              )
    LOOP
      --Se deja un solo Espacio
      MI_LINEA:= RS.TEXT;
      WHILE INSTR(MI_LINEA, '  ')>0
      LOOP
        MI_LINEA:=REPLACE(MI_LINEA,'  ',' ');
      END LOOP;
      MI_LINEA :=TRIM(MI_LINEA);
      --Se consulta donde inicia procedimiento o función
      IF SUBSTR(MI_LINEA,1,9) = 'FUNCTION ' THEN
        MI_TIPO  :='F';
      ELSIF SUBSTR(MI_LINEA,1,10) = 'PROCEDURE ' THEN
        MI_TIPO  :='P';
      END IF;
      
      --Identificar la definición del metodo
      MI_DEFINICION := FC_DEFINICION_JAVA(UN_OWNER, UN_PAQUETE, RS.EMPIEZA, NVL(RS.FINAL,RS.EMPIEZA+50));      
      --Identificar el retorno de la función
      MI_RETORNO    := FC_RETORNO_JAVA(UN_OWNER, UN_PAQUETE, RS.EMPIEZA, NVL(RS.FINAL,RS.EMPIEZA+50), MI_TIPO);           
      --Generar parametros de  la función  o procedimiento
      MI_PARAMETROS:= FC_PARAMETROS_JAVA(UN_OWNER, UN_PAQUETE, RS.EMPIEZA, NVL(RS.FINAL,RS.EMPIEZA+50));
      
      MI_DEFINICION :=  CHR(13)||CHR(10) ||
                   '    ' || MI_RETORNO || ' ' || MI_DEFINICION || '(' ||
                   MI_PARAMETROS ||') ' || CHR(13)||CHR(10) ||
                   '                      throws SystemException;'  || CHR(13)||CHR(10);
                   
      MI_SALIDA:=MI_SALIDA || MI_DEFINICION;
    END LOOP RecorrerFuncionesProcedimiento;  
    IF INSTR(MI_SALIDA, 'Date',1)>0 THEN
      MI_SALIDA:= REPLACE(MI_SALIDA,'EJB_ETAD_EJB','import java.util.Date;'  || CHR(13)||CHR(10));
    ELSE
      MI_SALIDA:= REPLACE(MI_SALIDA,'EJB_ETAD_EJB','') ;
    END IF;
    RETURN MI_SALIDA || '}';
  END FC_CLASE_INTERFAZ;


FUNCTION FC_TIPOS_JAVA
/*
    NAME              : FC_TIPOS_JAVA
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ
    DATE MIGRADOR     : 11/04/2017
    TIME              : 8:05 AM     
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       : RECIBE UN TIPO DE DATO DE ORACLE Y O CONVIERTE EN TIPO DE DATO DE JAVA
    MODIFICATIONS     : 
    @NAME:  generaTipoJava
*/
(
  UN_TIPOORACLE  IN VARCHAR2,
  UN_CLOBAPARTE  IN NUMBER
)RETURN VARCHAR2 AS 
  MI_SALIDA VARCHAR2(100);
  MI_ENTRADA VARCHAR2(200);
  MI_TFINAL  VARCHAR2(200);
  MI_SUBTIPO VARCHAR2(200);
  MI_STRSQL   VARCHAR2(32000);
  MI_FILTRO VARCHAR2(200);
BEGIN
  MI_SALIDA := TRIM(UN_TIPOORACLE);
  MI_ENTRADA:= TRIM(UN_TIPOORACLE);
  
  
  
  
  
  IF SUBSTR(MI_ENTRADA,1,13) ='PCK_SUBTIPOS.' THEN
    MI_SUBTIPO :=TRIM(SUBSTR(MI_ENTRADA,INSTR(MI_ENTRADA,'.',1)+1));
    IF MI_SUBTIPO='TI_DOBLE' THEN
      MI_SALIDA := 'BigDecimal';
       RETURN MI_SALIDA;
    ELSIF MI_SUBTIPO='TI_ENTERO' or MI_SUBTIPO='TI_MODULO' THEN
      MI_SALIDA := 'int';
       RETURN MI_SALIDA;
    ELSIF MI_SUBTIPO='TI_ENTERO_LARGO' THEN
      MI_SALIDA := 'long';
       RETURN MI_SALIDA;
    ELSIF MI_SUBTIPO='TI_PORCENTAJE' THEN
      MI_SALIDA := 'double';
       RETURN MI_SALIDA;
    ELSIF MI_SUBTIPO='TI_LOGICO' THEN
      MI_SALIDA := 'boolean';
       RETURN MI_SALIDA;
    END IF;
    BEGIN    
      MI_STRSQL:= ' SELECT TEXT 
                    FROM ALL_SOURCE
                    WHERE OWNER = ''' || 'SYSMANDSUNIST' || '''
                      AND NAME  = ''PCK_SUBTIPOS''
                      AND TYPE  = ''PACKAGE''
                      AND TRIM(TEXT) LIKE ''SUBTYPE ' || MI_SUBTIPO || ' %''';
      EXECUTE IMMEDIATE MI_STRSQL INTO MI_ENTRADA;
      MI_ENTRADA:= TRIM(SUBSTR(MI_ENTRADA,INSTR(MI_ENTRADA, ' IS ',1)+4,INSTR(MI_ENTRADA, ';',1)-1));
      MI_ENTRADA:= REPLACE(MI_ENTRADA,' ','');
      
      IF INSTR(MI_ENTRADA,'(',1)>0 THEN
        MI_ENTRADA:= SUBSTR(MI_ENTRADA,1,INSTR(MI_ENTRADA,'(',1)-1);
      END IF;      
      --IF INSTR(MI_ENTRADA, '%TYPE',1)>0 THEN
      --  MI_FILTRO:= SUBSTR(MI_ENTRADA,1,INSTR(MI_ENTRADA, '%TYPE',1)-1);
      --END IF;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
      MI_ENTRADA := 'SINIDENTIFICAR';  
    END;
  END IF;
  IF INSTR(MI_ENTRADA, '%TYPE',1)>0 THEN
      MI_FILTRO:= SUBSTR(MI_ENTRADA,1,INSTR(MI_ENTRADA, '%TYPE',1)-1);
      SELECT  CASE WHEN DATA_TYPE='NUMBER' AND DATA_PRECISION =1 AND DATA_SCALE=0 
              THEN 'boolean' 
              ELSE CASE WHEN DATA_TYPE='NUMBER' AND DATA_PRECISION <9 AND DATA_SCALE=0 
                   THEN 'int' 
                   ELSE CASE WHEN DATA_TYPE='NUMBER' AND DATA_PRECISION >=9 AND DATA_PRECISION >=18 AND DATA_SCALE=0 
                        THEN 'long' 
                        ELSE CASE WHEN DATA_TYPE='NUMBER'  AND DATA_PRECISION <9 AND DATA_SCALE<=6 
                             THEN 'BigDecimal' 
                             ELSE CASE WHEN DATA_TYPE='DATE' OR DATA_TYPE LIKE 'TIMESTAMP%' 
                                THEN 'date' 
                                ELSE CASE WHEN DATA_TYPE='CLOB' AND UN_CLOBAPARTE <>0 
                                      THEN 'clob'
                                      ELSE 'String' 
                                      END
                                 END 
                            END 
                       END 
                 END 
             END 
      INTO MI_SALIDA
      FROM USER_TAB_COLUMNS
      WHERE TABLE_NAME || '.' || COLUMN_NAME = MI_FILTRO ;
      MI_ENTRADA:= REPLACE(MI_ENTRADA,CHR(13),'');
      MI_ENTRADA:= REPLACE(MI_ENTRADA,CHR(10),'');
    END IF;
    
  IF MI_ENTRADA='VARCHAR2' OR MI_ENTRADA='CLOB' THEN
    MI_SALIDA:='String';
  ELSIF MI_ENTRADA='DATE' OR MI_ENTRADA='TIMESTAMP' OR MI_ENTRADA='ZONE' THEN
    MI_SALIDA:='Date';
  ELSIF MI_ENTRADA='NUMBER' THEN
    MI_SALIDA:='BigDecimal';
  ELSIF MI_ENTRADA='BOOLEAN' THEN
    MI_SALIDA:='boolean';
  ELSIF MI_ENTRADA='INT' OR MI_ENTRADA='INTEGER'  THEN
    MI_SALIDA:='int';
  END IF;

  RETURN MI_SALIDA;
END;

FUNCTION FC_PARAMETROS
/*
    NAME              : FC_PARAMETROS
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ
    DATE MIGRADOR     : 11/04/2017
    TIME              : 8:05 AM     
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       : GENERA EL LISTADO DE PARAMETROS ORIGINAL DE LAS FUNCIONES O PROCEDIMIENTOS DE UN PAQUETE
                        SE USA EN LA FUNCION GENERAR PARAMATROS DE JAVA, Y LA FUNCION
    MODIFICATIONS     : 
    @NAME:  generaParametros 
*/
(
  UN_OWNER     IN VARCHAR2,
  UN_PAQUETE   IN VARCHAR2,
  UN_LINEA_INI IN INTEGER,
  UN_LINEA_FIN IN INTEGER
)RETURN TI_ALIASTIPO AS
  MI_TEXTO VARCHAR2(32000);
  MI_I     INTEGER DEFAULT 0;
  MI_CAMPOTIPO TI_ALIASTIPO;
BEGIN
  MI_I:=0;
      <<ListarParametros>>
      FOR RSD IN(
              SELECT TEXT 
              FROM ALL_SOURCE 
              WHERE REGEXP_LIKE(TRIM(REPLACE(TEXT,CHR(9),' ')), '(^(,| |UN_)((.* IN )))')
                AND OWNER = UN_OWNER
                AND NAME  = UN_PAQUETE
                AND TYPE  = 'PACKAGE BODY'
                AND LINE BETWEEN UN_LINEA_INI AND UN_LINEA_FIN
              )
      LOOP
        
        MI_TEXTO := RSD.TEXT;
        MI_TEXTO := SUBSTR(MI_TEXTO,INSTR(MI_TEXTO,'UN_'));
        MI_TEXTO :=REPLACE(MI_TEXTO, CHR(13)||CHR(10),'');
        MI_TEXTO :=REPLACE(MI_TEXTO, CHR(13),'');
        MI_TEXTO :=REPLACE(MI_TEXTO, CHR(10),'');
        MI_TEXTO :=REPLACE(MI_TEXTO, CHR(9),'');
        IF INSTR(MI_TEXTO, ',',1)>0 THEN
          MI_TEXTO:= SUBSTR(MI_TEXTO,1,INSTR(MI_TEXTO, ',',1)-1);
        END IF;
        IF INSTR(MI_TEXTO, '--',1)>0 THEN
          MI_TEXTO:= SUBSTR(MI_TEXTO,1,INSTR(MI_TEXTO, '--',1)-1);
        END IF;
        IF INSTR(MI_TEXTO, ' DEFAULT ',1)>0 THEN
          MI_TEXTO:= SUBSTR(MI_TEXTO,1,INSTR(MI_TEXTO, ' DEFAULT ',1)-1);
        END IF;
        IF INSTR(MI_TEXTO, ')',1)>0 THEN
          MI_TEXTO:= SUBSTR(MI_TEXTO,1,INSTR(MI_TEXTO, ')',1)-1);
        END IF;
        IF INSTR(MI_TEXTO, ':=',1)>0 THEN
          MI_TEXTO:= SUBSTR(MI_TEXTO,1,INSTR(MI_TEXTO, ':=',1)-1);
        END IF;
        
        WHILE INSTR(MI_TEXTO, '  ')>0
        LOOP
          MI_TEXTO:=REPLACE(MI_TEXTO,'  ',' ');
        END LOOP;
        
        MI_TEXTO :=RTRIM(TRIM(MI_TEXTO));
        
        MI_I:=MI_I+1;
        IF INSTR(MI_TEXTO, ' ',1)>0 THEN
          MI_CAMPOTIPO(MI_I).CAMPO:= TRIM(SUBSTR(MI_TEXTO,1,INSTR(MI_TEXTO, ' ',1)));
        END IF;        
        IF INSTR(MI_TEXTO, ' ',-1)>0 THEN
          MI_CAMPOTIPO(MI_I).TIPO:= TRIM(SUBSTR(MI_TEXTO,INSTR(MI_TEXTO, ' ',-1)));
        END IF;
      END LOOP ListarParametros;
      RETURN MI_CAMPOTIPO;
END;

FUNCTION FC_PARAMETROS_JAVA
/*
    NAME              : FC_PARAMETROS_JAVA
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ
    DATE MIGRADOR     : 11/04/2017
    TIME              : 8:05 AM     
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       : GENERA LA LISTA DE PARAMETROS QUE LLEVARA EL METODO EN JAVA EN BASE A LOS PARAMETROS DE LA FUNCIÓN
    MODIFICATIONS     : 
    @NAME:  generaParametroJava    
*/
(
  UN_OWNER     IN VARCHAR2,
  UN_PAQUETE   IN VARCHAR2,
  UN_LINEA_INI IN INTEGER,
  UN_LINEA_FIN IN INTEGER
)RETURN VARCHAR2 AS 
  MI_CAMPOTIPO TI_ALIASTIPO;
  MI_SALIDA    VARCHAR2(32000) DEFAULT '';
  MI_PARA      VARCHAR2(32000) DEFAULT '';
BEGIN
  --Generar parametros de  la función  o procedimiento
      MI_CAMPOTIPO:= FC_PARAMETROS(UN_OWNER, UN_PAQUETE, UN_LINEA_INI,UN_LINEA_FIN );
      IF MI_CAMPOTIPO.COUNT>0 THEN
        FOR i IN MI_CAMPOTIPO.FIRST..MI_CAMPOTIPO.LAST
        LOOP
           MI_PARA := MI_CAMPOTIPO(i).CAMPO;
           MI_PARA := SUBSTR(MI_PARA,4);
           MI_PARA := INITCAP(REPLACE(MI_PARA,'_',' '));
           MI_PARA := REPLACE(MI_PARA,' ','');
           MI_PARA := LOWER(SUBSTR(MI_PARA,1,1)) || SUBSTR(MI_PARA,2);
           MI_SALIDA := MI_SALIDA  || CHR(13)||CHR(10) ||  FC_TIPOS_JAVA(MI_CAMPOTIPO(i).TIPO,0)  || ' ' || MI_PARA || ', ';            
        END LOOP;
        MI_SALIDA := SUBSTR(MI_SALIDA,1,LENGTH(MI_SALIDA)-2);
      END IF;
      RETURN MI_SALIDA;
END FC_PARAMETROS_JAVA;

FUNCTION FC_DEFINICION_JAVA
/*
    NAME              : FC_DEFINICION_JAVA
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ
    DATE MIGRADOR     : 11/04/2017
    TIME              : 8:05 AM     
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       : gENERA EL NOMBRE DE LA DEFINICIÓN DEL METODO DE ACUERDO A LA ANOTACIÓN @NAME:
    MODIFICATIONS     : 
    @NAME:  generaParametroJava    
*/
(
  UN_OWNER     IN VARCHAR2,
  UN_PAQUETE   IN VARCHAR2,
  UN_LINEA_INI IN INTEGER,
  UN_LINEA_FIN IN INTEGER
)RETURN VARCHAR2 AS
  MI_DEFINICION  VARCHAR2(32000);
BEGIN
  --Identificar la definición del metodo
      BEGIN
        SELECT TEXT 
        INTO MI_DEFINICION
        FROM ALL_SOURCE 
        WHERE REGEXP_LIKE(UPPER(TRIM(TEXT)), '^@NAME')
          AND OWNER = UN_OWNER
          AND NAME  = UN_PAQUETE
          AND TYPE  = 'PACKAGE BODY'
          AND LINE BETWEEN UN_LINEA_INI AND UN_LINEA_FIN;
      EXCEPTION WHEN NO_DATA_FOUND THEN 
          MI_DEFINICION := 'SINIDENTIFICAR';    
      END;     
     MI_DEFINICION := SUBSTR(MI_DEFINICION,INSTR(MI_DEFINICION,':',1)+1);
      --MI_DEFINICION := INITCAP(REPLACE(TRIM(MI_DEFINICION),'_',' '));
      --MI_DEFINICION := REPLACE(MI_DEFINICION,' ','');
      --MI_DEFINICION := LOWER(SUBSTR(MI_DEFINICION,1,1)) || SUBSTR(MI_DEFINICION,2);
      MI_DEFINICION := REPLACE(MI_DEFINICION,CHR(13),'');
      MI_DEFINICION := REPLACE(MI_DEFINICION,CHR(10),'');
      RETURN MI_DEFINICION;
END;

FUNCTION FC_RETORNO_JAVA
/*
    NAME              : FC_DEFINICION_JAVA
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ
    DATE MIGRADOR     : 11/04/2017
    TIME              : 8:05 AM     
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       : GENERA EL RETORNO VALIDO PARA JAVA DE ACUERDO AL RETORNO DE PLSQL
    MODIFICATIONS     : 
    @NAME:  generaRetornoJava    
*/
(
  UN_OWNER     IN VARCHAR2,
  UN_PAQUETE   IN VARCHAR2,
  UN_LINEA_INI IN INTEGER,
  UN_LINEA_FIN IN INTEGER, 
  UN_TIPO      IN VARCHAR2
)RETURN VARCHAR2 AS
  MI_RETORNO VARCHAR2(32000);
BEGIN
  --Identificar el retorno de la función
      IF UN_TIPO ='F' THEN
        BEGIN
          SELECT TEXT 
          INTO MI_RETORNO
          FROM ALL_SOURCE 
          WHERE REGEXP_LIKE(TRIM(TEXT), 'RETURN [^;]+$')
            AND OWNER = UN_OWNER
            AND NAME  = UN_PAQUETE
            AND TYPE  = 'PACKAGE BODY'
            AND LINE BETWEEN UN_LINEA_INI AND UN_LINEA_FIN;        
          MI_RETORNO := SUBSTR(MI_RETORNO,INSTR(MI_RETORNO, 'RETURN ',1)+7);
          IF INSTR(MI_RETORNO, ' ',1)>0 THEN
            MI_RETORNO := SUBSTR(MI_RETORNO, 1, INSTR(MI_RETORNO, ' ',1));
          END IF;
          MI_RETORNO :=TRIM(MI_RETORNO);
        EXCEPTION WHEN NO_DATA_FOUND THEN 
          MI_RETORNO := 'SINIDENTIFICAR';    
        END;           
      ELSE
        MI_RETORNO:='void';        
      END IF;      
      MI_RETORNO:= REPLACE(MI_RETORNO,CHR(13),'');
      MI_RETORNO:= REPLACE(MI_RETORNO,CHR(10),'');
      MI_RETORNO:= FC_TIPOS_JAVA( MI_RETORNO,-1);
      RETURN MI_RETORNO;
END FC_RETORNO_JAVA;


FUNCTION FC_PARAMETROS_ORACLE
(
  UN_OWNER     IN VARCHAR2,
  UN_PAQUETE   IN VARCHAR2,
  UN_LINEA_INI IN INTEGER,
  UN_LINEA_FIN IN INTEGER  
)RETURN VARCHAR2 AS 
  MI_CAMPOTIPO TI_ALIASTIPO;
  MI_SALIDA    VARCHAR2(32000) DEFAULT '';
  MI_PARA      VARCHAR2(32000) DEFAULT '';
  MI_TIPO      VARCHAR2(32000) DEFAULT '';
BEGIN
  --Generar parametros de  la función  o procedimiento
  MI_CAMPOTIPO:= FC_PARAMETROS(UN_OWNER, UN_PAQUETE, UN_LINEA_INI,UN_LINEA_FIN );
  MI_SALIDA:='                   ';
  IF MI_CAMPOTIPO.COUNT>0 THEN
    FOR i IN MI_CAMPOTIPO.FIRST..MI_CAMPOTIPO.LAST
    LOOP
      IF FC_TIPOS_JAVA(MI_CAMPOTIPO(i).TIPO,0) = 'String' THEN
        MI_TIPO:= CHR(39);
      ELSE
        MI_TIPO:= '';
      END IF;
      MI_PARA := MI_CAMPOTIPO(i).CAMPO;
      MI_PARA := SUBSTR(MI_PARA,4);
      MI_PARA := INITCAP(REPLACE(MI_PARA,'_',' '));
      MI_PARA := REPLACE(MI_PARA,' ','');
      MI_PARA := LOWER(SUBSTR(MI_PARA,1,1)) || SUBSTR(MI_PARA,2);
      MI_SALIDA := MI_SALIDA || '' || CHR(34) || RPAD(MI_CAMPOTIPO(i).CAMPO,20,' ')  || ' =>' ;
      
      IF FC_TIPOS_JAVA(MI_CAMPOTIPO(i).TIPO,0) = 'Date' THEN
        MI_SALIDA := MI_SALIDA || 'TO_DATE(''' || CHR(34) || ' + SysmanFunciones.convertirAFechaCadena('  || MI_PARA ||  ') + ' || CHR(34)  || ''',''DD/MM/YYYY HH24:MI:SS'')' ||  ', ';
      ELSIF FC_TIPOS_JAVA(MI_CAMPOTIPO(i).TIPO,0) = 'boolean' THEN
        MI_SALIDA := MI_SALIDA ||  CHR(34) || '('  || MI_PARA ||  '?' || CHR(34) || '-1'  || CHR(34) || ':' || CHR(34) || '0' || CHR(34) || ')  + '  || CHR(34) ||  ', ';
      ELSE
        MI_SALIDA := MI_SALIDA || MI_TIPO || CHR(34) || ' + '  || MI_PARA ||  ' + ' || CHR(34)  || MI_TIPO ||  ', ';
      END IF;
      MI_SALIDA := MI_SALIDA || CHR(34) || CHR(13)||CHR(10) || '                 + ' ;            
    END LOOP;
    MI_SALIDA := SUBSTR(MI_SALIDA,1,INSTR(MI_SALIDA, ', ' ||CHR(34),-1 )-1) || CHR(34);
  END IF;
  RETURN MI_SALIDA;
END;

END PCK_DSS_DISENO;