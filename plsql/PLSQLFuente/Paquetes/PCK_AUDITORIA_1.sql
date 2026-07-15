create or replace PACKAGE BODY PCK_AUDITORIA AS

--1
FUNCTION FC_TRIGGER(
  /*
      NAME 			       : FC_TRIGGER -> Access CrearTriggerS
      AUTHORS 			   : STEFANINI SYSMAN
      AUTHOR MIGRACION : HENRY PUERTO 
      DATE MIGRADOR	   : 13/06/2018
      TIME				     : 02:00 PM
      MODULO ORIGEN	   : SysmanAdministracionOra2017.08.02
      DESCRIPTION		   : Funcion para crear trigger de auditoria, dependendiendo tablas seleccionadas
      MODIFIER			   : JOSE PASCUAL GOMEZ/JAVIER VILLATE
      DATE MODIFIED	   : 14/06/2018
      TIME				     : 3:33 PM
      MODIFICATIONS	   : 

    */
  UN_COMPANIA    IN VARCHAR2,
  UN_TABLAS      IN VARCHAR2
)
RETURN VARCHAR2
  --YB se comenta debido a no permite su compilación, 
  --se visualiza AUTHID sólo permite en programas de nivel de esquema
  --AUTHID CURRENT_USER
  --IS 
  -- ADAPTADA POR HPV EN 12/JUN/2018
  AS
  MI_ESQUEMA_AUD  VARCHAR2(32);
  MI_ESQUEMA_IRIS VARCHAR2(32);

  MI_SQLT             CLOB:='';
  MI_SQLTM            CLOB:='';
  MI_SQLTN            CLOB:='';
  MI_SQLTD            CLOB:='';
  MI_SQL              CLOB:='';
  MI_SQLDROP          CLOB:='';
  MI_VBCRLF           VARCHAR2(4):=CHR(13)||CHR(10);
  MI_CAMPO            VARCHAR2(32);
  MI_TIPO             VARCHAR2(1);
  MI_TAMANO           NUMBER(5);
  MI_NOMBRETABLA      VARCHAR2(32);
  MI_IND_EDITAR       NUMBER(1);
  MI_IND_INSERTAR     NUMBER(1);
  MI_IND_ELIMINAR     NUMBER(1);
  MI_NOMBRE_COLUMNA   VARCHAR2(32);
  MI_TIPO_COLUMNA     VARCHAR2(1);
  MI_RS               SYS_REFCURSOR;
  MI_RST              SYS_REFCURSOR;
  MI_CAMPOSM CLOB;
  MI_CAMPOSN CLOB;
  MI_CAMPOSD CLOB;
  MI_LLAVESA VARCHAR2(32000);
  MI_LLAVESN VARCHAR2(32000);
  MI_NOMBRETRIGGER VARCHAR2(32);
  MI_COMPANIA NUMBER(1):=0;
  MI_USUARIOC NUMBER(1):=0;
  MI_USUARIOM NUMBER(1):=0;
  MI_CHR34 VARCHAR2(1) :='?';
  MI_STRCAMPO VARCHAR2(100);
  MI_STRNUEVO VARCHAR2(100);
  MI_STRANTERIOR VARCHAR2(100);
  MI_FIN VARCHAR2(20) :=',';
  MI_INICIO VARCHAR2(100):='MI_TEXTO:="[" || CHR(13); '||MI_VBCRLF;
  MI_DOSPUNTOS VARCHAR2(20);

  BEGIN
  --Se debe crear siempre enlace publico SYSMANAUDITORIA y SYSMANIRIS
    MI_ESQUEMA_AUD := '';--'SYSMANAUDITORIA.';
    MI_ESQUEMA_IRIS :='';-- 'SYSMANIRIS.';
    /*MI_ESQUEMA_AUD :=NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'NOMBRE ESQUEMA AUDITORIA',-1,SYSDATE,-1),'SYSMANAUDITORIA');
      MI_ESQUEMA_IRIS:=NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'NOMBRE ESQUEMA IRIS'     ,-1,SYSDATE,-1),'SYSMANIRISST');
      IF LENGTH(TRIM(MI_ESQUEMA_AUD))<=0 THEN
        RETURN ' ESQUEMA DE AUDITORIA NO DEFINIDO. REVICE EL PARAMETRO -NOMBRE ESQUEMA AUDITORIA';
      END IF;
      IF LENGTH(TRIM(MI_ESQUEMA_IRIS))<=0 THEN
        RETURN ' ESQUEMA DE IRIS NO DEFINIDO. REVICE EL PARAMETRO -NOMBRE ESQUEMA IRIS';
      END IF;
    */
    MI_STRCAMPO    := '"{" || CHR(13) ||' || '"'  ||               MI_CHR34 || 'f'    || MI_CHR34 || ':' || MI_CHR34;
    MI_STRNUEVO    := ' || "' || MI_CHR34 || '," || CHR(13) || "' || MI_CHR34 || 'n'    || MI_CHR34 || ':' || MI_CHR34 || '" ||';
    MI_STRANTERIOR := MI_CHR34 || '," || CHR(13) || "' || MI_CHR34 || 'o' || MI_CHR34 || ':' || MI_CHR34 || '" ||';
    MI_DOSPUNTOS   := MI_CHR34 || ':' || MI_CHR34;
    MI_SQLT:='SELECT 
                NOMBRE_TABLA,
                IND_INSERTAR,
                IND_EDITAR,
                IND_ELIMINAR '||
            ' FROM  '||MI_ESQUEMA_AUD||'TABLAS_AUDITAR'||
            ' WHERE  NOMBRE_TABLA IN ('''||UN_TABLAS||''')';
    OPEN MI_RST FOR MI_SQLT;
      LOOP
        FETCH MI_RST INTO MI_NOMBRETABLA,MI_IND_INSERTAR,MI_IND_EDITAR,MI_IND_ELIMINAR;
        EXIT WHEN MI_RST%NOTFOUND;
        MI_SQL:='/* '||MI_VBCRLF;
        MI_SQL:=MI_SQL||' * COPYRIGHT STEFANINI SYSMAN. ALL RIGHTS RESERVED.'||MI_VBCRLF;
        MI_SQL:=MI_SQL||' * ESTE SCRIPT FUE CREADO POR STEFANINI SYSMAN ...'||MI_VBCRLF;
        MI_SQL:=MI_SQL||' * ... EN LA FECHA '||TO_CHAR(SYSDATE,'DD/MM/YYYY HH:MI:SS')||' ... '||MI_VBCRLF;
        MI_SQL:=MI_SQL||' * ... POR FAVOR CUALQUIER MODIFICACIÓN COMUNICARLA A STEFANINI SYSMAN '||MI_VBCRLF;
        MI_SQL:=MI_SQL||' * ... AUTOR: HENRY DE JESUS PUERTO V.  STEFANINI SYSMAN SAS'||MI_VBCRLF;
        MI_SQL:=MI_SQL||' * ... MODIFICA: JOSE PASCUAL GOMEZ B.  STEFANINI SYSMAN SAS'||MI_VBCRLF;
        MI_SQL:=MI_SQL||' * ... MODIFICACION: SE IMPLEMENTA LA SALIDA DE LAS LLAVES Y LOS CAMBIOS EN FORMATO JSON'||MI_VBCRLF;
        MI_SQL:=MI_SQL||' *'||MI_VBCRLF||'*/'||MI_VBCRLF;

        MI_LLAVESA :='"[" ' ;
        MI_LLAVESN :='"[" ' ;

        MI_SQLT:='SELECT   
                    CAMPO,
                    TIPO,
                    TAMANO'||
                ' FROM   '||MI_ESQUEMA_AUD||'LLAVES_TABLAS'||
                ' WHERE   TABLA='||CHR(39)||TRIM(MI_NOMBRETABLA)||CHR(39);

        OPEN MI_RS FOR MI_SQLT;
          LOOP
            FETCH MI_RS INTO MI_CAMPO,MI_TIPO,MI_TAMANO;
            EXIT WHEN MI_RS%NOTFOUND;
            IF LENGTH(MI_LLAVESA)>0 THEN
      		     MI_LLAVESA:=MI_LLAVESA||'||';
		           MI_LLAVESN:=MI_LLAVESN||'||';
            END IF;
            IF MI_TIPO='D' THEN
          	  MI_LLAVESA  := MI_LLAVESA || 'CHR(13) || ' ||'"{'|| MI_CHR34 || MI_CAMPO|| MI_DOSPUNTOS || '"||TO_CHAR(NVL(:OLD.'||MI_CAMPO||',SYSDATE),"RRRR/MM/DD") || "' || MI_CHR34 || '}" ||CHR(13) || ","';
          	  MI_LLAVESN  := MI_LLAVESN || 'CHR(13) || ' ||'"{'|| MI_CHR34 || MI_CAMPO|| MI_DOSPUNTOS || '"||TO_CHAR(NVL(:NEW.'||MI_CAMPO||',SYSDATE),"RRRR/MM/DD") || "' || MI_CHR34 || '}" ||CHR(13) || ","';
            END IF ;
            IF MI_TIPO='S' THEN
          	  MI_LLAVESA  := MI_LLAVESA || 'CHR(13) || ' ||'"{'|| MI_CHR34 || MI_CAMPO|| MI_DOSPUNTOS || '"||TO_CHAR(NVL(:OLD.'||MI_CAMPO||',"")) || "' || MI_CHR34 || '}" ||CHR(13) || ","';
          	  MI_LLAVESN  := MI_LLAVESN || 'CHR(13) || ' ||'"{'|| MI_CHR34 || MI_CAMPO|| MI_DOSPUNTOS || '"||TO_CHAR(NVL(:NEW.'||MI_CAMPO||',"")) || "' || MI_CHR34 || '}" ||CHR(13) || ","';
            END IF ;
            IF MI_TIPO='N' THEN
          	  MI_LLAVESA  := MI_LLAVESA || 'CHR(13) || ' ||'"{'|| MI_CHR34 || MI_CAMPO|| MI_DOSPUNTOS || '"|| TO_CHAR(NVL(:OLD.'||MI_CAMPO||',0)) || "' || MI_CHR34 || '}" ||CHR(13) || ","';
          	  MI_LLAVESN  := MI_LLAVESN || 'CHR(13) || ' ||'"{'|| MI_CHR34 || MI_CAMPO|| MI_DOSPUNTOS || '"|| TO_CHAR(NVL(:NEW.'||MI_CAMPO||',0)) || "' || MI_CHR34 || '}" ||CHR(13) || ","';
          END IF;
        END LOOP;
        MI_CAMPOSM:= MI_INICIO;
        MI_CAMPOSN:= MI_INICIO;
        MI_CAMPOSD:= MI_INICIO;

        MI_SQLT:='';

        MI_COMPANIA:=0;
        MI_USUARIOC:=0;
        MI_USUARIOM:=0;

        MI_SQLT:='SELECT    
                    NOMBRE_COLUMNA,
                    TIPO'||
                ' FROM     '||MI_ESQUEMA_AUD||'COLUMNAS_AUDITAR'||
                ' WHERE   NOMBRE_TABLA='''||MI_NOMBRETABLA||'''
                    AND TIPO IN (''D'',''S'',''N'')';

        OPEN MI_RS FOR MI_SQLT;
          LOOP
            FETCH MI_RS INTO MI_NOMBRE_COLUMNA,MI_TIPO_COLUMNA;
            EXIT WHEN MI_RS%NOTFOUND;
            IF MI_NOMBRE_COLUMNA='COMPANIA' THEN
                MI_COMPANIA:=-1;
            END IF;
            IF MI_NOMBRE_COLUMNA='CREATED_BY' THEN
                MI_USUARIOC:=-1;
            END IF;
            IF MI_NOMBRE_COLUMNA='MODIFIED_BY' THEN
               MI_USUARIOM:=-1;
            END IF; 


            IF NVL(MI_TIPO_COLUMNA, '') = 'D' THEN
              MI_CAMPOSM := MI_CAMPOSM||'MI_TEXTO:=MI_TEXTO|| CASE WHEN TO_CHAR(NVL(:OLD.'||MI_NOMBRE_COLUMNA||',SYSDATE),"DD/MM/RRRR HH:MI:SS") <> TO_CHAR(NVL(:NEW.'||MI_NOMBRE_COLUMNA||',SYSDATE),"DD/MM/RRRR HH:MI:SS") THEN ' || MI_STRCAMPO || MI_NOMBRE_COLUMNA|| MI_STRANTERIOR ||'TO_CHAR(NVL(:OLD.'||MI_NOMBRE_COLUMNA||',SYSDATE),"DD/MM/RRRR HH:MI:SS") ' ||MI_STRNUEVO || ' TO_CHAR(NVL(:NEW.'||MI_NOMBRE_COLUMNA||',SYSDATE),"DD/MM/RRRR HH:MI:SS")  || "' || MI_CHR34 || '" ||  CHR(13) || "}" || CHR(13) || "' || MI_FIN ||'" ELSE "" END;'||MI_VBCRLF;
              MI_CAMPOSN := MI_CAMPOSN||'MI_TEXTO:=MI_TEXTO|| CASE WHEN :NEW.'||MI_NOMBRE_COLUMNA||' IS NOT NULL THEN ' || MI_STRCAMPO || MI_NOMBRE_COLUMNA|| MI_STRANTERIOR || '""' ||  MI_STRNUEVO || ' TO_CHAR(NVL(:NEW.'||MI_NOMBRE_COLUMNA||',SYSDATE),"DD/MM/RRRR HH:MI:SS") ||"' || MI_CHR34 || '}" || CHR(13) || "' || MI_FIN ||'" END;'||MI_VBCRLF;
              MI_CAMPOSD := MI_CAMPOSD||'MI_TEXTO:=MI_TEXTO|| CASE WHEN :OLD.'||MI_NOMBRE_COLUMNA||' IS NOT NULL THEN ' || MI_STRCAMPO || MI_NOMBRE_COLUMNA|| MI_STRANTERIOR || ' TO_CHAR(NVL(:OLD.'||MI_NOMBRE_COLUMNA||',SYSDATE),"DD/MM/RRRR HH:MI:SS") ' ||MI_STRNUEVO || '"' || MI_CHR34 || '}" || CHR(13) || "' || MI_FIN ||'" END;'||MI_VBCRLF;
            END IF;
            IF NVL(MI_TIPO_COLUMNA, '') = 'S' THEN
              MI_CAMPOSM := MI_CAMPOSM||'MI_TEXTO:=MI_TEXTO|| CASE WHEN NVL(:OLD.'||MI_NOMBRE_COLUMNA||'," ") <> NVL(:NEW.'||MI_NOMBRE_COLUMNA||'," ") THEN ' || MI_STRCAMPO||MI_NOMBRE_COLUMNA|| MI_STRANTERIOR || ' NVL(:OLD.'||MI_NOMBRE_COLUMNA||',"") ' || MI_STRNUEVO || ' NVL(:NEW.'||MI_NOMBRE_COLUMNA||',"") || "' || MI_CHR34 || '" || CHR(13) || "}" || CHR(13) || "' || MI_FIN ||'" ELSE "" END;'||MI_VBCRLF;
              MI_CAMPOSN := MI_CAMPOSN||'MI_TEXTO:=MI_TEXTO|| CASE WHEN :NEW.'||MI_NOMBRE_COLUMNA||' IS NOT NULL THEN ' || MI_STRCAMPO || MI_NOMBRE_COLUMNA|| MI_STRANTERIOR || '""' ||  MI_STRNUEVO || ' NVL(:NEW.'||MI_NOMBRE_COLUMNA||',"") || "' || MI_CHR34 || '}" || CHR(13) || "' || MI_FIN ||'" END;'||MI_VBCRLF;
              MI_CAMPOSD := MI_CAMPOSD||'MI_TEXTO:=MI_TEXTO|| CASE WHEN :OLD.'||MI_NOMBRE_COLUMNA||' IS NOT NULL THEN ' || MI_STRCAMPO || MI_NOMBRE_COLUMNA|| MI_STRANTERIOR || ' NVL(:OLD.'||MI_NOMBRE_COLUMNA||',"") ' ||MI_STRNUEVO ||  '"' || MI_CHR34 || '}" || CHR(13) || "' || MI_FIN ||'" END;'||MI_VBCRLF;
            END IF;
            IF NVL(MI_TIPO_COLUMNA, '') = 'N' THEN
              MI_CAMPOSM := MI_CAMPOSM||'MI_TEXTO:=MI_TEXTO|| CASE WHEN TO_CHAR(NVL(:OLD.'||MI_NOMBRE_COLUMNA||',0)) <> TO_CHAR(NVL(:NEW.'||MI_NOMBRE_COLUMNA||',0)) THEN ' || MI_STRCAMPO||MI_NOMBRE_COLUMNA|| MI_STRANTERIOR || ' TO_CHAR(NVL(:OLD.'||MI_NOMBRE_COLUMNA||',0)) ' ||MI_STRNUEVO || ' TO_CHAR(NVL(:NEW.'||MI_NOMBRE_COLUMNA||',0))  || "' || MI_CHR34 || '" || CHR(13) || "}" || CHR(13) || "' || MI_FIN ||'" ELSE "" END ;'||MI_VBCRLF;
              MI_CAMPOSN := MI_CAMPOSN||'MI_TEXTO:=MI_TEXTO|| CASE WHEN :NEW.'||MI_NOMBRE_COLUMNA||' IS NOT NULL THEN ' || MI_STRCAMPO || MI_NOMBRE_COLUMNA|| MI_STRANTERIOR || '""' || MI_STRNUEVO || ' TO_CHAR(NVL(:NEW.'||MI_NOMBRE_COLUMNA||',0)) || "' || MI_CHR34 || '}" || CHR(13) || "' || MI_FIN ||'" END;'||MI_VBCRLF;
              MI_CAMPOSD := MI_CAMPOSD||'MI_TEXTO:=MI_TEXTO|| CASE WHEN :OLD.'||MI_NOMBRE_COLUMNA||' IS NOT NULL THEN ' || MI_STRCAMPO || MI_NOMBRE_COLUMNA|| MI_STRANTERIOR || ' TO_CHAR(NVL(:OLD.'||MI_NOMBRE_COLUMNA||',0)) ' ||MI_STRNUEVO ||  '"' || MI_CHR34 || '}" || CHR(13) || "' || MI_FIN ||'" END;'||MI_VBCRLF;
            END IF;
            MI_SQLTM:=MI_SQLTM||MI_CAMPOSM;
            MI_SQLTN:=MI_SQLTN||MI_CAMPOSN;
            MI_SQLTD:=MI_SQLTD||MI_CAMPOSD;

        END LOOP;

 --ELIMINO LOS TRIGGER SI YA EXISTEN       
           BEGIN
             MI_SQLDROP:='DROP TRIGGER TRGSDELETE_'||MI_NOMBRETABLA||MI_VBCRLF;
             EXECUTE IMMEDIATE MI_SQLDROP;
             EXCEPTION WHEN OTHERS THEN 
               NULL;
           END;
           BEGIN
             MI_SQLDROP:='DROP TRIGGER TRGSINSERT_'||MI_NOMBRETABLA||MI_VBCRLF;
             EXECUTE IMMEDIATE MI_SQLDROP;
             EXCEPTION WHEN OTHERS THEN 
               NULL;
           END;
           BEGIN
             MI_SQLDROP:='DROP TRIGGER TRGSUPDATE_'||MI_NOMBRETABLA||MI_VBCRLF;
             EXECUTE IMMEDIATE MI_SQLDROP;
             EXCEPTION WHEN OTHERS THEN 
               NULL;
           END;
--FIN DE ELIMINACION DE TRIGGERS

----INICIO TRIGGER ACTUALIZAR  
     IF MI_IND_EDITAR<>0 THEN
        MI_NOMBRETRIGGER :=TRIM(RPAD('TRGSUPDATE_'||MI_NOMBRETABLA,30));
        MI_SQL := MI_SQL ||' CREATE OR REPLACE TRIGGER '||MI_NOMBRETRIGGER||'  '||MI_VBCRLF||
                              ' AFTER INSERT OR UPDATE OR DELETE ON '||MI_NOMBRETABLA||' FOR EACH ROW '||MI_VBCRLF||
                                      ' DECLARE ' ||MI_VBCRLF||
                                      ' MI_TEXTO    VARCHAR2(32000);'||MI_VBCRLF||
                                      ' MI_NIT      VARCHAR2(18);'||MI_VBCRLF||
                                      ' MI_SUCURSAL VARCHAR2(3):="001";'||MI_VBCRLF||
                                      ' BEGIN '||MI_VBCRLF;


          MI_SQL:=MI_SQL||' IF UPDATING THEN '||MI_VBCRLF ||
                            MI_CAMPOSM || 
            			     '    IF LENGTH(MI_TEXTO)>0 THEN '||MI_VBCRLF;
          IF MI_USUARIOM<>0 THEN 
              MI_SQL:=MI_SQL||'       BEGIN ' ||MI_VBCRLF||
                           '          SELECT CEDULA' ||MI_VBCRLF||
                           '          INTO   MI_NIT' ||MI_VBCRLF||
                           '          FROM   '||MI_ESQUEMA_IRIS||'USUARIO' ||MI_VBCRLF||
                           '          WHERE CODIGO=:NEW.MODIFIED_BY;' ||MI_VBCRLF||
                           '          EXCEPTION WHEN NO_DATA_FOUND THEN '||MI_VBCRLF||
                           '          MI_NIT:=" ";'||MI_VBCRLF||
                           '          END;'||MI_VBCRLF;
          END IF;
     		MI_SQL:=MI_SQL||'INSERT INTO '||MI_ESQUEMA_AUD||'LOG_AUDITORIA 
                         (
                          ID,
                          CUK,
                          COMPANIA,
                          USUARIO,
                          NIT,
                          SUCURSAL,
                          FECHA,
                          ACCION,
                          NOMBRE_TABLA,
                          DESCRIPCION_NUEVA,
                          CREATED_BY,
                          DATE_CREATED)'||MI_VBCRLF||
							    '       VALUES
                          (
                          S_LOG_AUDITORIA.NEXTVAL,
                          '||SUBSTR(MI_LLAVESA,1,LENGTH(MI_LLAVESA)-2) || ']"' ||',
                          '||CASE WHEN MI_COMPANIA<>0 THEN ':NEW.COMPANIA' ELSE '"'||UN_COMPANIA||'"' END||',
                          '||CASE WHEN MI_USUARIOM<>0 THEN 'NVL(:NEW.MODIFIED_BY,NULL)' ELSE 'NULL' END ||',
                          NVL(MI_NIT,NULL),MI_SUCURSAL,TO_DATE(TO_CHAR(SYSDATE, "DD/MM/YYYY HH24:mi:SS"), "DD/MM/YYYY HH24:mi:SS"),"MODIFICACION","'||MI_NOMBRETABLA||'",
                          SUBSTR(MI_TEXTO,1,LENGTH(MI_TEXTO)-1) || "]",
                          '||CASE WHEN MI_USUARIOM<>0 THEN 'NVL(:NEW.MODIFIED_BY,NULL)' ELSE 'NULL' END ||',
                          TO_DATE(TO_CHAR(SYSDATE, "DD/MM/YYYY HH24:mi:SS"), "DD/MM/YYYY HH24:mi:SS"));'||MI_VBCRLF||
										 '    END IF;'||MI_VBCRLF||
 										 ' END IF;'||MI_VBCRLF;

                IF MI_IND_EDITAR<>0  OR MI_IND_INSERTAR<>0 OR MI_IND_ELIMINAR<>0 THEN
							      MI_SQL:=MI_SQL||'   EXCEPTION WHEN OTHERS THEN  '||MI_VBCRLF;
                    MI_SQL:=MI_SQL||' NULL;'||MI_VBCRLF;
                    MI_SQL:=MI_SQL||' END;'||MI_VBCRLF;
                ELSE
                   MI_SQL:='DROP TRIGGER '||MI_NOMBRETRIGGER||MI_VBCRLF;
                END IF;     
                BEGIN
                  MI_SQL:=REPLACE(MI_SQL,CHR(34),CHR(39));
                  MI_SQL:=REPLACE(MI_SQL,MI_CHR34,CHR(34));
                  EXECUTE IMMEDIATE MI_SQL;
                EXCEPTION WHEN OTHERS THEN 
                IF MI_IND_EDITAR<>0  OR MI_IND_INSERTAR<>0 OR MI_IND_ELIMINAR<>0 THEN
                  RETURN 'FALLO EN LA CREACION DEL TRIGGER DE LA TABLA '||MI_NOMBRETABLA||'. '||SQLERRM;
                END IF;
                END;
                MI_SQL:='';
       END IF;          
 --FIN TRIGGER ACTUALIZAR               

 --INICIO TRIGGER INSERTAR              
							 IF MI_IND_INSERTAR<>0 THEN
                       MI_NOMBRETRIGGER :=TRIM(RPAD('TRGSINSERT_'||MI_NOMBRETABLA,30));
                       MI_SQL := MI_SQL ||' CREATE OR REPLACE TRIGGER '||MI_NOMBRETRIGGER||'  '||MI_VBCRLF||
                              ' AFTER INSERT OR UPDATE OR DELETE ON '||MI_NOMBRETABLA||' FOR EACH ROW '||MI_VBCRLF||
                                      ' DECLARE ' ||MI_VBCRLF||
                                      ' MI_TEXTO    VARCHAR2(32000);'||MI_VBCRLF||
                                      ' MI_NIT      VARCHAR2(18);'||MI_VBCRLF||
                                      ' MI_SUCURSAL VARCHAR2(3):="001";'||MI_VBCRLF||
                                      ' BEGIN '||MI_VBCRLF;

								 MI_SQL:=MI_SQL||' IF INSERTING THEN '||MI_VBCRLF ||
                             MI_CAMPOSN|| 
   											         '    IF LENGTH(MI_TEXTO)>0 THEN '||MI_VBCRLF;
                             IF MI_USUARIOC<>0 THEN 
                 MI_SQL:=MI_SQL||'       BEGIN ' ||MI_VBCRLF||
                                 '          SELECT CEDULA' ||MI_VBCRLF||
                                 '          INTO   MI_NIT' ||MI_VBCRLF||
                                 '          FROM   '||MI_ESQUEMA_IRIS||'USUARIO' ||MI_VBCRLF||
                                 '          WHERE CODIGO=:NEW.CREATED_BY;' ||MI_VBCRLF||
                                 '       EXCEPTION WHEN NO_DATA_FOUND THEN '||MI_VBCRLF||
                                 '          MI_NIT:=" ";'||MI_VBCRLF||
                                 '       END;'||MI_VBCRLF;
                              END IF;
                  MI_SQL:=MI_SQL|| ' INSERT INTO '||MI_ESQUEMA_AUD||'LOG_AUDITORIA 
                                    (
                                      ID,
                                      CUK,
                                      COMPANIA,
                                      USUARIO,
                                      NIT,
                                      SUCURSAL,
                                      FECHA,
                                      ACCION,
                                      NOMBRE_TABLA,
                                      DESCRIPCION_NUEVA,
                                      CREATED_BY,
                                      DATE_CREATED)'||MI_VBCRLF||
                                  '  VALUES
                                      (
                                        S_LOG_AUDITORIA.NEXTVAL,
                                       '||SUBSTR(MI_LLAVESN,1,LENGTH(MI_LLAVESN)-2) || ']"' ||',
                                       '||CASE WHEN MI_COMPANIA<>0 THEN ':NEW.COMPANIA' ELSE '"'||UN_COMPANIA||'"' END||',
                                       '||CASE WHEN MI_USUARIOC<>0 THEN 'NVL(:NEW.CREATED_BY,NULL)' ELSE 'NULL' END ||',
                                       NVL(MI_NIT,NULL),MI_SUCURSAL,TO_DATE(TO_CHAR(SYSDATE, "DD/MM/YYYY HH24:mi:SS"), "DD/MM/YYYY HH24:mi:SS"),"CREACION","'||MI_NOMBRETABLA||'",
                                       SUBSTR(MI_TEXTO,1,LENGTH(MI_TEXTO)-1) || "]",
                                       '||CASE WHEN MI_USUARIOC<>0 THEN 'NVL(:NEW.CREATED_BY,NULL)' ELSE 'NULL' END ||',
                                       TO_DATE(TO_CHAR(SYSDATE, "DD/MM/YYYY HH24:mi:SS"), "DD/MM/YYYY HH24:mi:SS"));'||MI_VBCRLF||
												         '    END IF;'||MI_VBCRLF||
												         ' END IF;'||MI_VBCRLF;

                IF MI_IND_EDITAR<>0  OR MI_IND_INSERTAR<>0 OR MI_IND_ELIMINAR<>0 THEN
							      MI_SQL:=MI_SQL||'   EXCEPTION WHEN OTHERS THEN  '||MI_VBCRLF;
                    MI_SQL:=MI_SQL||' NULL;'||MI_VBCRLF;
                    MI_SQL:=MI_SQL||' END;'||MI_VBCRLF;
                ELSE
                   MI_SQL:='DROP TRIGGER '||MI_NOMBRETRIGGER|| MI_VBCRLF;
                END IF;     
                BEGIN
                  MI_SQL:=REPLACE(MI_SQL,CHR(34),CHR(39));
                  MI_SQL:=REPLACE(MI_SQL,MI_CHR34,CHR(34));
                  EXECUTE IMMEDIATE MI_SQL;
                EXCEPTION WHEN OTHERS THEN 
                IF MI_IND_EDITAR<>0  OR MI_IND_INSERTAR<>0 OR MI_IND_ELIMINAR<>0 THEN
                  RETURN 'FALLO EN LA CREACION DEL TRIGGER DE LA TABLA '||MI_NOMBRETABLA||'. '||SQLERRM;
                END IF;
                END;
                MI_SQL:='';
        END IF;         
 --FIN TRIGGER INSERTAR

 --INICIO TRIGGER ELIMINAR

							 IF MI_IND_ELIMINAR<>0 THEN
                      MI_NOMBRETRIGGER :=TRIM(RPAD('TRGSDELETE_'||MI_NOMBRETABLA,30));
                       MI_SQL := MI_SQL ||' CREATE OR REPLACE TRIGGER '||MI_NOMBRETRIGGER||'  '||MI_VBCRLF||
                              ' AFTER INSERT OR UPDATE OR DELETE ON '||MI_NOMBRETABLA||' FOR EACH ROW '||MI_VBCRLF||
                                      ' DECLARE ' ||MI_VBCRLF||
                                      ' MI_TEXTO    VARCHAR2(32000);'||MI_VBCRLF||
                                      ' MI_NIT      VARCHAR2(18);'||MI_VBCRLF||
                                      ' MI_SUCURSAL VARCHAR2(3):="001";'||MI_VBCRLF||
                                      ' BEGIN '||MI_VBCRLF;

								 MI_SQL:=MI_SQL||' IF DELETING THEN '||MI_VBCRLF ||
                             MI_CAMPOSD|| 
   											     '    IF LENGTH(MI_TEXTO)>0 THEN '||MI_VBCRLF;
                            IF MI_USUARIOM<>0 THEN 
                              MI_SQL:=MI_SQL||'       BEGIN ' ||MI_VBCRLF||
                                             '          SELECT CEDULA' ||MI_VBCRLF||
                                             '          INTO   MI_NIT' ||MI_VBCRLF||
                                             '          FROM   '||MI_ESQUEMA_IRIS||'USUARIO' ||MI_VBCRLF||
                                             '          WHERE CODIGO=:OLD.MODIFIED_BY;' ||MI_VBCRLF||
                                             '       EXCEPTION WHEN NO_DATA_FOUND THEN '||MI_VBCRLF||
                                             '          MI_NIT:=" ";'||MI_VBCRLF||
                                             '       END;'||MI_VBCRLF;
                  MI_SQL:=MI_SQL||
												     ' INSERT INTO '||MI_ESQUEMA_AUD||'LOG_AUDITORIA 
                                (
                                  ID,
                                  CUK,
                                  COMPANIA,
                                  USUARIO,
                                  NIT,
                                  SUCURSAL,
                                  FECHA,
                                  ACCION,
                                  NOMBRE_TABLA,
                                  DESCRIPCION_NUEVA,
                                  CREATED_BY,
                                  DATE_CREATED
                                )'||MI_VBCRLF||
                          '   VALUES
                                (
                                  S_LOG_AUDITORIA.NEXTVAL,
                                  '||SUBSTR(MI_LLAVESA,1,LENGTH(MI_LLAVESA)-2) || ']"' ||',
                                  '||CASE WHEN MI_COMPANIA<>0 THEN ':OLD.COMPANIA' ELSE '"'||UN_COMPANIA||'"' END||',
                                  '||CASE WHEN MI_USUARIOM<>0 THEN 'NVL(:OLD.MODIFIED_BY,NULL)' ELSE 'NULL' END ||',
                                  NVL(MI_NIT,NULL),
                                  MI_SUCURSAL,
                                  TO_DATE(TO_CHAR(SYSDATE, "DD/MM/YYYY HH24:mi:SS"), "DD/MM/YYYY HH24:mi:SS"),
                                  "ELIMINACION",
                                  "'||MI_NOMBRETABLA||'",
                                  SUBSTR(MI_TEXTO,1,LENGTH(MI_TEXTO)-1) || "]",
                                  '||CASE WHEN MI_USUARIOM<>0 THEN 'NVL(:OLD.MODIFIED_BY,NULL)' ELSE 'NULL' END ||',
                                  TO_DATE(TO_CHAR(SYSDATE, "DD/MM/YYYY HH24:mi:SS"), "DD/MM/YYYY HH24:mi:SS")
                                 );'||MI_VBCRLF||
												    '    END IF;'||MI_VBCRLF||
												    ' END IF;'||MI_VBCRLF;
                IF MI_IND_EDITAR<>0  OR MI_IND_INSERTAR<>0 OR MI_IND_ELIMINAR<>0 THEN
							      MI_SQL:=MI_SQL||'   EXCEPTION WHEN OTHERS THEN  '||MI_VBCRLF;
                    MI_SQL:=MI_SQL||' NULL;'||MI_VBCRLF;
                    MI_SQL:=MI_SQL||' END;'||MI_VBCRLF;
                ELSE
                   MI_SQL:='DROP TRIGGER '||MI_NOMBRETRIGGER||MI_VBCRLF;
                END IF;     
                BEGIN
                  MI_SQL:=REPLACE(MI_SQL,CHR(34),CHR(39));
                  MI_SQL:=REPLACE(MI_SQL,MI_CHR34,CHR(34));
                  EXECUTE IMMEDIATE MI_SQL;
                  MI_SQL:='';

                EXCEPTION WHEN OTHERS THEN 
                IF MI_IND_EDITAR<>0  OR MI_IND_INSERTAR<>0 OR MI_IND_ELIMINAR<>0 THEN
                  RETURN 'FALLO EN LA CREACION DEL TRIGGER DE LA TABLA '||MI_NOMBRETABLA||'. '||SQLERRM;
                END IF;
                END; 
               END IF;
        END IF;
--FIN TRIGGER ELIMINAR

--SI LA TABLA NO TIENE NINGUN INDICADOR O SE LOS QUITAN
--ELIMINA LOS TRIGGER QUE TENIA

                IF MI_IND_EDITAR=0  AND MI_IND_INSERTAR=0 AND MI_IND_ELIMINAR=0 THEN
                   BEGIN
                     MI_SQL:='DROP TRIGGER TRGSDELETE_'||MI_NOMBRETABLA||MI_VBCRLF;
                     EXECUTE IMMEDIATE MI_SQL;
                     EXCEPTION WHEN OTHERS THEN 
                    NULL;
                   END;
                   BEGIN
                     MI_SQL:='DROP TRIGGER TRGSINSERT_'||MI_NOMBRETABLA||MI_VBCRLF;
                     EXECUTE IMMEDIATE MI_SQL;
                     EXCEPTION WHEN OTHERS THEN 
                    NULL;
                   END;
                   BEGIN
                     MI_SQL:='DROP TRIGGER TRGSUPDATE_'||MI_NOMBRETABLA||MI_VBCRLF;
                     EXECUTE IMMEDIATE MI_SQL;
                     EXCEPTION WHEN OTHERS THEN 
                    NULL;
                   END;
                END IF;     
    END LOOP;
    RETURN 'TRIGGER DE AUDITORIA CREADO CORRECTAMENTE PARA LA TABLA '||UN_TABLAS||'.';
  EXCEPTION WHEN OTHERS THEN
    RETURN 'FALLO EN LA FUNCION DE LA CREACION DEL TRIGGER DE LAS TABLAS '||UN_TABLAS||'. '||SQLERRM;
  END FC_TRIGGER;

  PROCEDURE PR_ACT_TABLASAU
  /*
   NAME 			: PR_ACT_TABLASAU
   AUTHORS 			: GROJAS
   DATE MIGRADOR	: 30/12/2025
   TIME				: 09:14 AM
   MODULO ORIGEN	: ALMACÉN
   DESCRIPTION		: Proceso que actualiza la tabla AUDITORIA_TABLAS
   MODIFIER			: 
   DATE MODIFIED	: 
   TIME				: 
   MODIFICATIONS	: 

  */
   (
    UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA
   )
   AS
    MI_TABLA             VARCHAR2(200 CHAR);
    MI_MERGEUSING        PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE       PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGENOEXISTE     PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_RTA               VARCHAR2(32000 CHAR);
BEGIN
  BEGIN

   BEGIN
  MI_TABLA := 'AUDITORIA_TABLAS';
  MI_MERGEUSING := 'SELECT ''' || UN_COMPANIA || ''' AS COMPANIA,
                      U.TABLE_NAME   AS NOMBRE_TABLA
                    FROM USER_TABLES U
                    GROUP BY ''' || UN_COMPANIA || ''', U.TABLE_NAME';

  MI_MERGEENLACE := 'TABLA.COMPANIA     = VISTA.COMPANIA
                        AND TABLA.NOMBRE_TABLA = VISTA.NOMBRE_TABLA';

  MI_MERGENOEXISTE := 'INSERT (COMPANIA, NOMBRE_TABLA,
                               IND_INSERTAR, IND_EDITAR, IND_ELIMINAR)
                              VALUES (VISTA.COMPANIA, VISTA.NOMBRE_TABLA,
                                  0, 0, 0 )';

  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA       => MI_TABLA
                              ,UN_ACCION      => 'IN'
                              ,UN_MERGEUSING  => MI_MERGEUSING
                              ,UN_MERGEENLACE => MI_MERGEENLACE
                              ,UN_MERGENOEXIS => MI_MERGENOEXISTE);

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
   RAISE PCK_EXCEPCIONES.EXC_ALMACEN;

   END;

   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
     PCK_ERR_MSG.RAISE_WITH_MSG(
     UN_EXC_COD =>SQLCODE
     ,UN_ERROR_COD=>PCK_ERRORES.ERRR_ALMACEN_MMAFECTACIONMOV
     );
  END;

END PR_ACT_TABLASAU;

FUNCTION FC_SELECTJSON (
    UN_TABLA      IN VARCHAR2,
    UN_WHERE      IN VARCHAR2
) RETURN CLOB
IS
    V_CURSOR    INTEGER;
    UN_SQL       VARCHAR2(32767);
    V_COLS      INTEGER;
    V_DESC      DBMS_SQL.DESC_TAB;
    V_CLOB      CLOB;
    V_VALUE     VARCHAR2(4000);
    V_ROWS      NUMBER;
    V_FIRST_ROW BOOLEAN := TRUE;
BEGIN
    DBMS_LOB.CREATETEMPORARY(V_CLOB, TRUE);

    -- Construir SQL
    IF UN_WHERE IS NOT NULL AND TRIM(UN_WHERE) IS NOT NULL THEN
        UN_SQL := 'SELECT * FROM ' || UN_TABLA || ' WHERE ' || UN_WHERE;
    ELSE
        UN_SQL := 'SELECT * FROM ' || UN_TABLA;
    END IF;

    V_CURSOR := DBMS_SQL.OPEN_CURSOR;
    DBMS_SQL.PARSE(V_CURSOR, UN_SQL, DBMS_SQL.NATIVE);
    DBMS_SQL.DESCRIBE_COLUMNS(V_CURSOR, V_COLS, V_DESC);

    -- Definir columnas
    FOR I IN 1 .. V_COLS LOOP
        DBMS_SQL.DEFINE_COLUMN(V_CURSOR, I, V_VALUE, 4000);
    END LOOP;

    -- EJECUCIÓN CORRECTA
    V_ROWS := DBMS_SQL.EXECUTE(V_CURSOR);

    DBMS_LOB.WRITEAPPEND(V_CLOB, 1, '[');

    WHILE DBMS_SQL.FETCH_ROWS(V_CURSOR) > 0 LOOP

        IF NOT V_FIRST_ROW THEN
            DBMS_LOB.WRITEAPPEND(V_CLOB, 1, ',');
        END IF;

        DBMS_LOB.WRITEAPPEND(V_CLOB, 1, '{');

        FOR I IN 1 .. V_COLS LOOP
            DBMS_SQL.COLUMN_VALUE(V_CURSOR, I, V_VALUE);

            DBMS_LOB.WRITEAPPEND(
                V_CLOB,
                LENGTH(
                    '"' || V_DESC(I).COL_NAME || '":' ||
                    CASE
                        WHEN V_VALUE IS NULL THEN 'null'
                        ELSE '"' || REPLACE(V_VALUE, '"', '\"') || '"'
                    END || ','
                ),
                '"' || V_DESC(I).COL_NAME || '":' ||
                CASE
                    WHEN V_VALUE IS NULL THEN 'null'
                    ELSE '"' || REPLACE(V_VALUE, '"', '\"') || '"'
                END || ','
            );
        END LOOP;

        -- quitar última coma del objeto
        DBMS_LOB.TRIM(V_CLOB, DBMS_LOB.GETLENGTH(V_CLOB) - 1);
        DBMS_LOB.WRITEAPPEND(V_CLOB, 1, '}');

        V_FIRST_ROW := FALSE;
    END LOOP;

    DBMS_LOB.WRITEAPPEND(V_CLOB, 1, ']');

    DBMS_SQL.CLOSE_CURSOR(V_CURSOR);

    RETURN V_CLOB;

EXCEPTION
    WHEN OTHERS THEN
        IF DBMS_SQL.IS_OPEN(V_CURSOR) THEN
            DBMS_SQL.CLOSE_CURSOR(V_CURSOR);
        END IF;
        RETURN '{"ERROR":"' || REPLACE(SQLERRM, '"', '\"') || '"}';
END FC_SELECTJSON;

PROCEDURE PR_AUDITAR
(
    UN_TABLA      IN VARCHAR2,
    UN_ACCION     IN VARCHAR2 DEFAULT '',
    UN_CONDICION  IN VARCHAR2 DEFAULT '',
    MI_STRSQL     IN CLOB
) IS

    MI_PARAMETRO       VARCHAR2(10);
    MI_IND_INSERTAR    NUMBER := 0;
    MI_IND_EDITAR      NUMBER := 0;
    MI_IND_ELIMINAR    NUMBER := 0;

    UN_ACCION_TXT      VARCHAR2(10);
    UN_JSON_ANTES      CLOB;
    UN_JSON_DESPUES    CLOB;
    UN_JSON_FINAL      CLOB;

    UN_SQL             CLOB;

BEGIN
    --VALIDAR PARAMETRO GENERAL
    MI_PARAMETRO :=
        NVL(PCK_SYSMAN_UTL.FC_PAR('001','MANEJA PROCESO DE AUDITORIA',-1,SYSDATE), 'NO');

    IF MI_PARAMETRO <> 'SI' THEN
        RETURN;
    END IF;

    --VALIDAR TABLA / ACCION
    BEGIN
        SELECT IND_INSERTAR, IND_EDITAR, IND_ELIMINAR
          INTO MI_IND_INSERTAR, MI_IND_EDITAR, MI_IND_ELIMINAR
          FROM AUDITORIA_TABLAS
         WHERE COMPANIA     = '001'
           AND NOMBRE_TABLA = UN_TABLA;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN;
    END;

    CASE UPPER(UN_ACCION)
        WHEN 'I' THEN
            IF MI_IND_INSERTAR = 0 THEN RETURN; END IF;
            UN_ACCION_TXT := 'INSERT';

        WHEN 'M' THEN
            IF MI_IND_EDITAR = 0 THEN RETURN; END IF;
            UN_ACCION_TXT := 'UPDATE';

        WHEN 'E' THEN
            IF MI_IND_ELIMINAR = 0 THEN RETURN; END IF;
            UN_ACCION_TXT := 'DELETE';

        ELSE
            RETURN;
    END CASE;

    -- OBTENER DATOS ANTES (UPDATE / DELETE)
    IF UN_ACCION IN ('M','E') THEN

        UN_SQL :=
            'SELECT * FROM ' || UN_TABLA ||
            ' WHERE ' || UN_CONDICION;

        UN_JSON_ANTES :=
            DBMS_XMLGEN.CONVERT(
                XMLTYPE(
                    DBMS_XMLGEN.GETXML(UN_SQL)
                ).GETCLOBVAL(),
                1
            );
    END IF;

    /* ============================ EJECUTAR SENTENCIA PRINCIPAL  ============================ */
    EXECUTE IMMEDIATE MI_STRSQL;

    -- OBTENER DATOS DESPUES (INSERT / UPDATE)
    IF UN_ACCION IN ('I','M') THEN

        IF UN_CONDICION IS NOT NULL AND TRIM(UN_CONDICION) <> '' THEN
            UN_SQL :=
                'SELECT * FROM ' || UN_TABLA ||
                ' WHERE ' || UN_CONDICION;
        ELSE
            -- INSERT SIN WHERE → ÚLTIMO REGISTRO
            UN_SQL :=
                'SELECT * FROM ' || UN_TABLA ||
                ' WHERE ROWID = (SELECT MAX(ROWID) FROM ' || UN_TABLA || ')';
        END IF;

        UN_JSON_DESPUES :=
            DBMS_XMLGEN.CONVERT(
                XMLTYPE(
                    DBMS_XMLGEN.GETXML(UN_SQL)
                ).GETCLOBVAL(),
                1
            );
    END IF;

    -- ARMAR JSON FINAL 
    UN_JSON_FINAL :=
            '{' ||
            '"ACCION":"'            || UN_ACCION_TXT || '",' ||
            '"TABLA":"'             || UN_TABLA || '",' ||
            '"USUARIO":"'           || SYS_CONTEXT('USERENV','SESSION_USER') || '",' ||
            '"FECHA":"'             || TO_CHAR(SYSDATE,'YYYY-MM-DD HH24:MI:SS') || '",' ||
            '"MAQUINA_INSTANCIA":"' || SYS_CONTEXT('USERENV','HOST') || '",' ||
            '"ANTES":'              || NVL(UN_JSON_ANTES,'null') || ',' ||
            '"DESPUES":'            || NVL(UN_JSON_DESPUES,'null') ||
            '}';

    --SALIDA
    DBMS_OUTPUT.PUT_LINE(UN_JSON_FINAL);

EXCEPTION
    WHEN OTHERS THEN
        -- JAMAS ROMPER LA OPERACION PRINCIPAL
        DBMS_OUTPUT.PUT_LINE('ERROR AUDITORIA: ' || SQLERRM);
END PR_AUDITAR;


END PCK_AUDITORIA;