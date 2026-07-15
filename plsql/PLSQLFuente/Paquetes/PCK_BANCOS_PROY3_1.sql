create or replace PACKAGE BODY "PCK_BANCOS_PROY3" AS

--1
FUNCTION FC_SEGUIMIENTO_PLAN
 /*
    NAME              : FC_SEGUIMIENTO_PLAN   antes:PR_SEGUIMIENTO_PLAN
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO
    DATE MIGRADOR     : 29/08/2015
    TIME              : 11:16 AM
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    DESCRIPTION       : PROCEDIMIENTO QUE PERMITE REALIZAR LOS CÃ¿LCULOS CORRESPONDIENTES PARA GENERAR LOS INFORMES DEL PLAN INDICATIVO
    MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED	    : 31/01/2017
    TIME				      : 09:29 AM
    MODIFICATIONS	    : Se cambió el estándar de codificación y llamados por referencia de las funciones.Se agregó manejo de excepciones
                        /Se cambió a función para que retornara un CLOB de registros

   @Name: calculcarSegumientoPlanIndicativo
  */

  (
      UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_VIGENCIA_GUBERNAMENTAL   IN PCK_SUBTIPOS.TI_ENTERO,
      UN_INFORME                  IN PCK_SUBTIPOS.TI_ENTERO,
      UN_VIGENCIA                 IN VARCHAR2,
      UN_CANTIDADNIVELES          IN PCK_SUBTIPOS.TI_ENTERO
  )RETURN CLOB
  AS
      MI_STRSQL            PCK_SUBTIPOS.TI_STRSQL;
      MI_RS                SYS_REFCURSOR;
      MI_CONTADOR          PCK_SUBTIPOS.TI_ENTERO:=1;
      MI_ANIO1             PCK_SUBTIPOS.TI_ENTERO;
      MI_ANIO2             PCK_SUBTIPOS.TI_ENTERO;
      MI_ANIO3             PCK_SUBTIPOS.TI_ENTERO;
      MI_ANIO4             PCK_SUBTIPOS.TI_ENTERO;
      MI_RECANIO1          PCK_SUBTIPOS.TI_ENTERO;
      MI_RECANIO2          PCK_SUBTIPOS.TI_ENTERO;
      MI_RECANIO3          PCK_SUBTIPOS.TI_ENTERO;
      MI_RECANIO4          PCK_SUBTIPOS.TI_ENTERO;
      MI_SQL               VARCHAR2(3000 CHAR);
      MI_CAMPOS            PCK_SUBTIPOS.TI_CAMPOS;
      MI_RETURN            PCK_SUBTIPOS.TI_ENTERO;
      MI_UPDATE1           VARCHAR2(3000 CHAR);
      MI_UPDATE2           VARCHAR2(3000 CHAR);
      MI_UPDATE3           VARCHAR2(3000 CHAR);
      MI_UPDATE4           VARCHAR2(3000 CHAR);
      MI_SEPARADORCOLUMNA  PCK_SUBTIPOS.TI_CONDICION;
      MI_SEPARADORREGISTRO PCK_SUBTIPOS.TI_CONDICION;

      --Variables de la opcion 2 y 3
      MI_VNIVELES       PCK_BANCOS_PROY3.VECNIVELES;
      MI_NIVE           PCK_SUBTIPOS.TI_ENTERO;
      RSCOMPANIA        VARCHAR2(3000 CHAR);
      RSVIGENCIA        PCK_SUBTIPOS.TI_ENTERO;
      RSDIGITOS         PCK_SUBTIPOS.TI_ENTERO;
      RSDESCRIPCION     VARCHAR2(3000 CHAR);
      RSMETA_RESULT     PCK_SUBTIPOS.TI_ENTERO;
      RSMETA_PROYEC     PCK_SUBTIPOS.TI_ENTERO;
      MI_NIVELES        VARCHAR2(3000 CHAR);
      MI_VIGENCIAS      VARCHAR2(3000 CHAR):='';
      MI_VIG_ANIOS      PCK_SUBTIPOS.TI_ENTERO;
      MI_I              PCK_SUBTIPOS.TI_ENTERO:=1;
      MI_CON_TOTAL      PCK_SUBTIPOS.TI_ENTERO:=0;
      MI_VIGENCIA_ANIOS PCK_SUBTIPOS.TI_ENTERO;
      MI_CONTOTAL       BOOLEAN;
      MI_NOMBREVIGENCIA PCK_SUBTIPOS.TI_CONDICION;
      MI_RTA            CLOB;
      MI_REGISTRO       CLOB;


BEGIN

  MI_SEPARADORCOLUMNA := PCK_DATOS.GL_SEPARADOR_COL;  
  MI_SEPARADORREGISTRO := PCK_DATOS.GL_SEPARADOR_REG;

  DELETE FROM TEMP_INFORME_PLANINDI;

   IF(UN_INFORME = 1) THEN
       BEGIN
        BEGIN
          MI_CAMPOS:='COMPANIA
                      , ID
                      , VIGENCIA_INICIAL
                      , NIVEL1
                      , META_PRODUC
                      , CONCEPTO
                      , INDICADORES
                      , PONDERACION
                      , LB
                      , META
                      , DEPENDENCIAS
                      , ANO1
                      , ANO2
                      , ANO3
                      , ANO4
                      , REC_ANO1
                      , REC_ANO2
                      , REC_ANO3
                      , REC_ANO4';

          MI_STRSQL:='SELECT P.COMPANIA,
                        P.ID,
                        P.VIGENCIA_INICIAL,
                        N.DESCRIPCION AS NIVEL,
                        N.META_PRODUC,
                        P.DESCRIPCION AS CONCEPTO,
                        P.DESCRIPCION_INDICADOR AS INDICADORES,
                        P.PONDERACION,
                        P.LB,
                        P.META,
                        D.NOMBRE AS DEPENDENCIA,
                        0 AS ANO1,
                        0 AS ANO2,
                        0 AS ANO3,
                        0 AS ANO4,
                        0 AS REC_ANO1,
                        0 AS REC_ANO2,
                        0 AS REC_ANO3,
                        0 AS REC_ANO4
                    FROM BP_PLAN_INDICATIVO P
                        LEFT JOIN DEPENDENCIA D
                           ON P.DEPENDENCIA = D.CODIGO
                          AND P.COMPANIA    = D.COMPANIA
                         INNER JOIN BP_NIVEL_PLAN_IND N
                             ON P.COMPANIA         = N.COMPANIA
                            AND P.VIGENCIA_INICIAL = N.VIGENCIA
                    WHERE P.COMPANIA         ='||UN_COMPANIA||'
                      AND P.VIGENCIA_INICIAL ='||UN_VIGENCIA_GUBERNAMENTAL||'
                      AND LENGTH(P.ID)       = N.DIGITOS
                    ORDER BY P.ID';

                MI_RETURN:= PCK_DATOS.FC_ACME(UN_TABLA    => 'TEMP_INFORME_PLANINDI'
                                              ,UN_ACCION  => 'IS'
                                              ,UN_CAMPOS  => MI_CAMPOS
                                              ,UN_VALORES => MI_STRSQL);

                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

          END;

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                                 PCK_ERR_MSG.RAISE_WITH_MSG(
                                                  UN_EXC_COD     => SQLCODE
                                                  ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_INSERT_TINFPLINDI
                                                   );
       END;

               <<UPDATE_TEMP_INFORME_PLANINDI_1>>
                FOR I IN 1..4 LOOP
                   BEGIN
                     BEGIN
                          MI_UPDATE1:='(SELECT M.PONDERACION_META
                                        FROM BP_PLAN_INDICATIVO_METAS M
                                        WHERE T.COMPANIA      = M.COMPANIA
                                          AND T.ID            = M.ID_PLAN
                                          AND M.VIGENCIA_PLAN = '||UN_VIGENCIA_GUBERNAMENTAL||'
                                          AND M.VIGENCIA_META = '||(UN_VIGENCIA_GUBERNAMENTAL+I-1)||')';

                          MI_UPDATE2:='(SELECT NVL(M.VALOR_PROGRAMADO_META,0)+ NVL(M.VALOR_PROGRAMADO_META_OTROS,0)
                                        FROM BP_PLAN_INDICATIVO_METAS M
                                        WHERE T.COMPANIA      = M.COMPANIA
                                          AND T.ID            = M.ID_PLAN
                                          AND M.VIGENCIA_PLAN = '||UN_VIGENCIA_GUBERNAMENTAL||'
                                          AND M.VIGENCIA_META = '||(UN_VIGENCIA_GUBERNAMENTAL+I-1)||')';
                      --dbms_output.put_line('UPDATE TEMP_INFORME_PLANINDI T SET ANO'||I||'= '||MI_UPDATE1||' WHERE EXISTS'||MI_UPDATE1);
                      MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA     => 'TEMP_INFORME_PLANINDI T'
                                                  ,UN_ACCION    => 'M'
                                                  ,UN_CAMPOS    => 'ANO'||I||'= '||MI_UPDATE1
                                                  ,UN_CONDICION => 'EXISTS '||MI_UPDATE1);

                      MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA      =>'TEMP_INFORME_PLANINDI T'
                                                   ,UN_ACCION    => 'M'
                                                   ,UN_CAMPOS    => 'REC_ANO'||I||'= '||MI_UPDATE2
                                                   ,UN_CONDICION => 'EXISTS '||MI_UPDATE2);

                         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                  RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

                     END;

                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                                     PCK_ERR_MSG.RAISE_WITH_MSG(
                                                      UN_EXC_COD     => SQLCODE
                                                      ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_TINFPLINDI
                                                       );
                   END;


                END LOOP UPDATE_TEMP_INFORME_PLANINDI_1;


               <<UPDATE_TEMP_INFORME_PLANINDI_2>>
               FOR RS IN (SELECT ID FROM TEMP_INFORME_PLANINDI WHERE NOT META_PRODUC IN(0)) LOOP
                    MI_UPDATE1:='(SELECT P.REC_ANO1+H.REC_ANO1
                                  FROM TEMP_INFORME_PLANINDI H
                                  WHERE P.COMPANIA         = H.COMPANIA
                                    AND P.VIGENCIA_INICIAL = H.VIGENCIA_INICIAL
                                    AND H.ID               ='''||RS.ID||'''
                                    AND P.ID               = SUBSTR('''||RS.ID||''', 1, LENGTH(P.ID))
                                    AND LENGTH(P.ID)       < LENGTH('''||RS.ID||'''))';

                    MI_UPDATE2:='(SELECT T.REC_ANO2+P.REC_ANO2
                                  FROM TEMP_INFORME_PLANINDI P
                                  WHERE T.COMPANIA         = P.COMPANIA
                                    AND T.VIGENCIA_INICIAL = P.VIGENCIA_INICIAL
                                    AND P.ID               = '''||RS.ID||'''
                                    AND T.ID               = SUBSTR('''||RS.ID||''', 1, LENGTH(T.ID))
                                    AND LENGTH(T.ID)       < LENGTH('''||RS.ID||'''))';

                    MI_UPDATE3:='(SELECT T.REC_ANO3+P.REC_ANO3
                                  FROM TEMP_INFORME_PLANINDI P
                                  WHERE T.COMPANIA         = P.COMPANIA
                                    AND T.VIGENCIA_INICIAL = P.VIGENCIA_INICIAL
                                    AND P.ID               = '''||RS.ID||'''
                                    AND T.ID               = SUBSTR('''||RS.ID||''', 1, LENGTH(T.ID))
                                    AND LENGTH(T.ID)       < LENGTH('''||RS.ID||'''))';

                    MI_UPDATE4:='(SELECT T.REC_ANO4+P.REC_ANO4
                                  FROM TEMP_INFORME_PLANINDI P
                                  WHERE T.COMPANIA         = P.COMPANIA
                                    AND T.VIGENCIA_INICIAL = P.VIGENCIA_INICIAL
                                    AND P.ID               = '''||RS.ID||'''
                                    AND T.ID               = SUBSTR('''||RS.ID||''', 1, LENGTH(T.ID))
                                    AND LENGTH(T.ID)       < LENGTH('''||RS.ID||'''))';

                 BEGIN
                  BEGIN

                    MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA      => 'TEMP_INFORME_PLANINDI P'
                                                 ,UN_ACCION    => 'M'
                                                 ,UN_CAMPOS    => 'REC_ANO1='||MI_UPDATE1
                                                 ,UN_CONDICION => 'EXISTS '||MI_UPDATE1);

                    MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA      => 'TEMP_INFORME_PLANINDI T'
                                                 ,UN_ACCION    => 'M'
                                                 ,UN_CAMPOS    => 'REC_ANO2='||MI_UPDATE2
                                                 ,UN_CONDICION => 'EXISTS '||MI_UPDATE2);

                    MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA      => 'TEMP_INFORME_PLANINDI T'
                                                 ,UN_ACCION    => 'M'
                                                 ,UN_CAMPOS    => 'REC_ANO3='||MI_UPDATE3
                                                 ,UN_CONDICION => 'EXISTS '||MI_UPDATE3);

                    MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA      => 'TEMP_INFORME_PLANINDI T'
                                                 ,UN_ACCION    => 'M'
                                                 ,UN_CAMPOS    => 'REC_ANO4='||MI_UPDATE4
                                                 ,UN_CONDICION => 'EXISTS '||MI_UPDATE4);

                         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                  RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

                  END;

                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                                     PCK_ERR_MSG.RAISE_WITH_MSG(
                                                      UN_EXC_COD     => SQLCODE
                                                      ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_TINFPLINDI
                                                       );

                 END;

                END LOOP UPDATE_TEMP_INFORME_PLANINDI_2;



  MI_REGISTRO := '';
  FOR MI_RS IN (SELECT NIVEL1,
                      CONCEPTO,
                      INDICADORES,
                      PONDERACION,
                      LB LINEA_BASE,
                      ANO1,
                      ANO2,
                      ANO3,
                      ANO4,
                      ANO1+ANO2+ANO3+ANO4 TOTAL_FISICO,
                      REC_ANO1,
                      REC_ANO2,
                      REC_ANO3,
                      REC_ANO4,
                      REC_ANO1+REC_ANO2+REC_ANO3+REC_ANO4 TOTAL,
                      DEPENDENCIAS
                    FROM TEMP_INFORME_PLANINDI
                    ORDER BY ID) LOOP

       MI_REGISTRO := MI_REGISTRO||
                      TO_CLOB( MI_RS.NIVEL1||MI_SEPARADORCOLUMNA||
                      MI_RS.CONCEPTO||MI_SEPARADORCOLUMNA||
                      MI_RS.INDICADORES||MI_SEPARADORCOLUMNA||
                      MI_RS.PONDERACION||MI_SEPARADORCOLUMNA||
                      MI_RS.LINEA_BASE||MI_SEPARADORCOLUMNA||
                      MI_RS.ANO1||MI_SEPARADORCOLUMNA||
                      MI_RS.ANO2||MI_SEPARADORCOLUMNA||
                      MI_RS.ANO3||MI_SEPARADORCOLUMNA||
                      MI_RS.ANO4||MI_SEPARADORCOLUMNA||
                      MI_RS.TOTAL_FISICO||MI_SEPARADORCOLUMNA||
                      MI_RS.REC_ANO1||MI_SEPARADORCOLUMNA||
                      MI_RS.REC_ANO2||MI_SEPARADORCOLUMNA||
                      MI_RS.REC_ANO3||MI_SEPARADORCOLUMNA||
                      MI_RS.REC_ANO4||MI_SEPARADORCOLUMNA||
                      MI_RS.TOTAL||MI_SEPARADORCOLUMNA||
                      MI_RS.DEPENDENCIAS||MI_SEPARADORREGISTRO)  ;


  END LOOP ;  
  RETURN    MI_REGISTRO; 

  ELSIF(UN_INFORME= 2 OR UN_INFORME=3) THEN

    MI_STRSQL:= 'SELECT BP_NIVEL_PLAN_IND.COMPANIA,
                    BP_NIVEL_PLAN_IND.VIGENCIA,
                    BP_NIVEL_PLAN_IND.DIGITOS,
                    BP_NIVEL_PLAN_IND.DESCRIPCION,
                    BP_NIVEL_PLAN_IND.META_RESUL,
                    BP_NIVEL_PLAN_IND.META_PRODUC
                FROM BP_NIVEL_PLAN_IND
                WHERE BP_NIVEL_PLAN_IND.COMPANIA ='||UN_COMPANIA||'
                  AND BP_NIVEL_PLAN_IND.VIGENCIA ='||UN_VIGENCIA_GUBERNAMENTAL||'
                ORDER BY BP_NIVEL_PLAN_IND.COMPANIA
                      , BP_NIVEL_PLAN_IND.VIGENCIA
                      , BP_NIVEL_PLAN_IND.DIGITOS asc
                      , BP_NIVEL_PLAN_IND.DESCRIPCION';
    OPEN MI_RS FOR MI_STRSQL;
      LOOP

        FETCH MI_RS INTO RSCOMPANIA, RSVIGENCIA, RSDIGITOS, RSDESCRIPCION, RSMETA_RESULT, RSMETA_PROYEC;
        EXIT WHEN MI_RS%NOTFOUND;
          MI_VNIVELES(MI_I).DIGITOS:=RSDIGITOS;
          MI_VNIVELES(MI_I).DESCRIPCION:=RSDESCRIPCION;
          MI_VNIVELES(MI_I).M_RES:=RSMETA_RESULT;
          MI_VNIVELES(MI_I).M_PRO:=RSMETA_PROYEC;
          IF(RSMETA_PROYEC = 0) THEN
            MI_NIVELES:=MI_NIVELES||''''' AS "'||RSDESCRIPCION||'", ';
          ELSE
            MI_NIVELES:=MI_NIVELES||'P.DESCRIPCION "'||RSDESCRIPCION||'", ';
          END IF;
          MI_CAMPOS          := MI_CAMPOS||'NIVEL'||MI_I||', ';

          MI_I               := MI_I+1;
      END LOOP;
    CLOSE MI_RS;
    MI_NIVE:=MI_I;
    MI_CAMPOS:=MI_CAMPOS||'COMPANIA
                         , ID
                         , VIGENCIA_INICIAL
                         , TIPO_META_INDICADOR
                         , DESCRIPCION_INDICADOR ';
    IF(UN_VIGENCIA = 'TODAS' OR UN_INFORME = 3) THEN
      MI_VIG_ANIOS :=4;
      MI_CON_TOTAL:= -1;
    ELSE
      MI_VIG_ANIOS:=1;
    END IF;
    BEGIN
      BEGIN
        MI_SQL:='SELECT '||MI_NIVELES||'P.COMPANIA
                                        , P.ID, P.VIGENCIA_INICIAL
                                        , P.TIPO_META_INDICADOR
                                        , P.DESCRIPCION_INDICADOR 
                FROM BP_PLAN_INDICATIVO P
                      INNER JOIN BP_NIVEL_PLAN_IND N
                             ON P.COMPANIA         = N.COMPANIA
                            AND P.VIGENCIA_INICIAL = N.VIGENCIA
                WHERE P.Compania       ='||UN_COMPANIA||'
                  AND VIGENCIA_INICIAL = '||UN_VIGENCIA_GUBERNAMENTAL||'
                  AND LENGTH(ID)       = N.DIGITOS AND N.META_PRODUC IN(-1)
                ORDER BY P.ID';
        MI_RETURN:= PCK_DATOS.FC_ACME(UN_TABLA    => 'TEMP_INFORME_PLANINDI'
                                     ,UN_ACCION  => 'IS'
                                     ,UN_CAMPOS  => MI_CAMPOS
                                     ,UN_VALORES => MI_SQL);
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
      END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD     => SQLCODE
                      ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_INSERT_TINFPLINDI
                       );
    END;
    <<UPDATE_TEMP_INFORME_PLANINDI_3>>
    FOR I IN 1..(MI_NIVE-1) LOOP
      BEGIN
        BEGIN
          MI_UPDATE1:='(SELECT M.DESCRIPCION
                        FROM   BP_PLAN_INDICATIVO M
                        WHERE T.COMPANIA                  = M.COMPANIA
                          AND T.VIGENCIA_INICIAL          = M.VIGENCIA_INICIAL
                          AND SUBSTR(T.ID,1,LENGTH(M.ID)) = M.ID
                          AND LENGTH(M.ID)                = '||MI_VNIVELES(I).DIGITOS||')';
          MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA      => 'TEMP_INFORME_PLANINDI T'
                                       ,UN_ACCION    => 'M'
                                       ,UN_CAMPOS    => 'NIVEL'||I||'='||MI_UPDATE1
                                       ,UN_CONDICION => 'EXISTS '||MI_UPDATE1);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
        END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD     => SQLCODE
                            ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_TINFPLINDI
                             );
      END;
    END LOOP UPDATE_TEMP_INFORME_PLANINDI_3;
    <<UPDATE_TEMP_INFORME_PLANINDI_4>>
    FOR I IN 1..MI_VIG_ANIOS LOOP
      BEGIN
        DECLARE VIG NUMBER;
        BEGIN
          IF UN_VIGENCIA= 'TODAS' THEN
            VIG:=(UN_VIGENCIA_GUBERNAMENTAL+I-1);
          ELSE
            VIG:=TO_NUMBER(UN_VIGENCIA);
          END IF;
          MI_UPDATE1:='(SELECT CASE WHEN P.TIPO_META_INDICADOR=''MR'' THEN M.META_BRUTA+M.CANTIDAD_PROGRAMADA ELSE
                                CASE WHEN P.TIPO_META_INDICADOR= ''MI'' THEN M.META_BRUTA-M.CANTIDAD_PROGRAMADA+M.CANTIDAD_EJECUTADA ELSE
                                  CASE WHEN P.TIPO_META_INDICADOR =''MM'' THEN M.CANTIDAD_EJECUTADA ELSE 0 END END END,
                               M.META_BRUTA,
                               CASE WHEN P.TIPO_META_INDICADOR =''MR'' THEN M.META_BRUTA+M.CANTIDAD_PROGRAMADA-M.CANTIDAD_EJECUTADA ELSE
                                  CASE WHEN P.TIPO_META_INDICADOR =''MI'' THEN M.META_BRUTA-M.CANTIDAD_PROGRAMADA+M.CANTIDAD_EJECUTADA ELSE
                                      CASE WHEN P.TIPO_META_INDICADOR =''MM'' THEN M.CANTIDAD_EJECUTADA ELSE 0 END END END,
                               CASE WHEN M.CANTIDAD_PROGRAMADA=0 THEN 0 ELSE M.CANTIDAD_EJECUTADA/M.CANTIDAD_PROGRAMADA END,
                               M.VALOR_PROGRAMADO_META+M.VALOR_PROGRAMADO_META_OTROS,
                               M.VALOR_EJECUTADO_META,
                               CASE WHEN M.VALOR_PROGRAMADO_META+M.VALOR_PROGRAMADO_META_OTROS=0 THEN 0 ELSE M.VALOR_EJECUTADO_META/(M.VALOR_PROGRAMADO_META+M.VALOR_PROGRAMADO_META_OTROS) END
                        FROM BP_PLAN_INDICATIVO_METAS M INNER JOIN BP_PLAN_INDICATIVO P
                          ON M.COMPANIA         = P.COMPANIA
                         AND M.ID_PLAN          = P.ID
                         AND M.VIGENCIA_PLAN    = P.VIGENCIA_INICIAL
                        WHERE T.COMPANIA         = M.COMPANIA
                          AND T.ID               = M.ID_PLAN
                          AND T.VIGENCIA_INICIAL = M.VIGENCIA_PLAN
                          AND M.VIGENCIA_META    = ' || VIG || ')';
          MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA      => 'TEMP_INFORME_PLANINDI T'
                                      ,UN_ACCION    => 'M'
                                      ,UN_CAMPOS    => '(LINEA'||I||',VALOR_ESPERADO'||I||',LOGRO'||I||',CUMPLIMIENTO'||I||',
                                                         R_ASIGNADOS'||I||',R_AJECUTADOS'||I||',R_CUMPLIMIENTO'||I||')= '||MI_UPDATE1
                                      ,UN_CONDICION => ' EXISTS '||MI_UPDATE1);

                     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                              RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

            END;

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                                 PCK_ERR_MSG.RAISE_WITH_MSG(
                                                      UN_EXC_COD     => SQLCODE
                                                      ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_TINFPLINDI
                                                       );
          END;


    END LOOP UPDATE_TEMP_INFORME_PLANINDI_4;

            IF(MI_CON_TOTAL <> 0) THEN
              BEGIN
                BEGIN

                MI_UPDATE1:='VALOR_ESPERADO_TOT=VALOR_ESPERADO1+VALOR_ESPERADO2+VALOR_ESPERADO3+VALOR_ESPERADO4
                             ,LOGRO_TOT=LOGRO1+LOGRO2+LOGRO3+LOGRO4
                             ,R_ASIGNADOS_TOT=R_ASIGNADOS1+R_ASIGNADOS2+R_ASIGNADOS3+R_ASIGNADOS4
                             ,R_AJECUTADOS_TOT=R_AJECUTADOS1+R_AJECUTADOS2+R_AJECUTADOS3+R_AJECUTADOS4';

                MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA   => 'TEMP_INFORME_PLANINDI'
                                             ,UN_ACCION => 'M'
                                             ,UN_CAMPOS => MI_UPDATE1 );

                MI_UPDATE1:='CUMPLIMIENTO_TOT= CASE WHEN VALOR_ESPERADO_TOT=0 THEN 0 ELSE (LOGRO_TOT/VALOR_ESPERADO_TOT) END,
                              R_CUMPLIMIENTO_TOT=CASE WHEN R_AJECUTADOS_TOT=0 THEN 0 ELSE (R_ASIGNADOS_TOT/R_AJECUTADOS_TOT) END';

                MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA => 'TEMP_INFORME_PLANINDI'
                                             ,UN_ACCION => 'M'
                                             ,UN_CAMPOS => MI_UPDATE1);

                                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                      RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

                 END;

                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                                         PCK_ERR_MSG.RAISE_WITH_MSG(
                                                              UN_EXC_COD     => SQLCODE
                                                              ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_TINFPLINDI
                                                               );
              END;
            END IF;

   --Calcular las vigencias 
   MI_NOMBREVIGENCIA:= '';
    IF (UN_VIGENCIA = 'TODAS' OR UN_INFORME = 3) THEN 
      MI_VIG_ANIOS := 4;
      MI_CON_TOTAL := -1;     
    ELSE
      MI_VIG_ANIOS := 1;
    END IF;
    MI_RTA:=''; 

      MI_NIVELES := '';

      FOR i IN 1..UN_CANTIDADNIVELES LOOP
        MI_NIVELES := MI_NIVELES || 
                      'NIVEL'         || i || '|| ''' || MI_SEPARADORCOLUMNA || ''' || ';
      END LOOP;
      MI_NIVELES := MI_NIVELES || ' DESCRIPCION_INDICADOR' || '|| ''' || MI_SEPARADORCOLUMNA || ''' || ' ||  
                                  'CASE WHEN TIPO_META_INDICADOR= ''MI'' 
                                        THEN ''META INCREMENTO'' 
                                        ELSE 
                                          CASE WHEN TIPO_META_INDICADOR=''MM'' 
                                          THEN ''META MANTENIMIENTO'' 
                                          ELSE 
                                            CASE WHEN TIPO_META_INDICADOR=''MR'' 
                                            THEN ''META REDUCCION'' 
                                            ELSE '' '' 
                                            END 
                                          END 
                                        END ' 
                                        || '|| ''' || MI_SEPARADORCOLUMNA || '''  ';

      FOR i IN 1..MI_VIG_ANIOS LOOP
        MI_NIVELES := MI_NIVELES|| '||' ||
                             'LINEA'         || i || '|| ''' || MI_SEPARADORCOLUMNA || ''' || ' ||
                             'VALOR_ESPERADO'|| i || '|| ''' || MI_SEPARADORCOLUMNA || ''' || ' ||
                             'LOGRO'         || i || '|| ''' || MI_SEPARADORCOLUMNA || ''' || ' ||
                             'CUMPLIMIENTO'  || i || '|| ''' || MI_SEPARADORCOLUMNA || ''' || ' ||
                             'R_ASIGNADOS'   || i || '|| ''' || MI_SEPARADORCOLUMNA || ''' || ' ||
                             'R_AJECUTADOS'  || i || '|| ''' || MI_SEPARADORCOLUMNA || ''' || ' || 
                             'R_CUMPLIMIENTO'|| i || '|| ''' || MI_SEPARADORCOLUMNA || '''  ';
      END LOOP;


    IF MI_CON_TOTAL <> 0 THEN
      MI_NIVELES := MI_NIVELES|| '||' ||
                            'VALOR_ESPERADO_TOT'  || '|| ''' || MI_SEPARADORCOLUMNA || ''' || ' ||
                            'LOGRO_TOT'           || '|| ''' || MI_SEPARADORCOLUMNA || ''' || ' ||    
                            'CUMPLIMIENTO_TOT'    || '|| ''' || MI_SEPARADORCOLUMNA || ''' || ' ||
                            'R_ASIGNADOS_TOT'     || '|| ''' || MI_SEPARADORCOLUMNA || ''' || ' ||
                            'R_AJECUTADOS_TOT'    || '|| ''' || MI_SEPARADORCOLUMNA || ''' || ' ||
                            'R_CUMPLIMIENTO_TOT'  || '|| ''' || MI_SEPARADORCOLUMNA || ''' || ' ||
                            'ANEXO_TOT' ; 
    END IF;
    MI_NIVELES := MI_NIVELES || ' AS FILA';



    MI_STRSQL := 'SELECT '|| MI_NIVELES|| ' ' ||
                 ' FROM TEMP_INFORME_PLANINDI ORDER BY ID';


   END IF;

   MI_RTA:='';
   MI_REGISTRO:='';
  OPEN MI_RS FOR MI_STRSQL;
  LOOP
   FETCH MI_RS
  INTO  MI_RTA;              

  EXIT WHEN MI_RS%NOTFOUND;     
    MI_REGISTRO:=MI_REGISTRO || MI_SEPARADORREGISTRO || MI_RTA;

  END LOOP;

  RETURN MI_REGISTRO;   

END FC_SEGUIMIENTO_PLAN;

--2
FUNCTION FC_CREARAUXILIARES
/*
    NAME              : FC_CREARAUXILIARES
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO
    DATE MIGRADOR     : 19/09/2015
    TIME              : 10:07 AM
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    DESCRIPTION       : FUNCION DESARROLLADA QUE PERMITE CREAR LOS AUXILIARES DE UNA COMPAÃ?IA
    MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED	    : 31/01/2017
    TIME				      : 10:50 AM
    MODIFICATIONS	    : Se cambió el estándar de codificación y llamados por referencia de las funciones.Se agregó manejo de excepciones
    @Name: crearAuxiliarDesdeProyecto
*/
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO
  ) RETURN VARCHAR2
  AS
    MI_RETURN         PCK_SUBTIPOS.TI_ENTERO;
    MI_STRSQL         PCK_SUBTIPOS.TI_STRSQL;
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_RS             SYS_REFCURSOR;
    MI_STRPROYECTOS   VARCHAR2(3000 CHAR):= '';
    RSCODIGO          PROYECTOS.CODIGO%TYPE;

  BEGIN
    MI_STRSQL:='SELECT    PROYECTOS.CODIGO
                FROM      PROYECTOS
                LEFT JOIN AUXILIAR
                ON        PROYECTOS.COMPANIA = AUXILIAR.COMPANIA
                AND       PROYECTOS.CODIGO = AUXILIAR.CODIGO
                WHERE     PROYECTOS.COMPANIA='''||UN_COMPANIA||'''
                AND       LENGTH(PROYECTOS.CODIGO) <> 8
                AND       AUXILIAR.COMPANIA IS NULL';

    OPEN MI_RS FOR MI_STRSQL;
     LOOP
      FETCH MI_RS INTO RSCODIGO;
        EXIT WHEN MI_RS%NOTFOUND;
          MI_STRPROYECTOS:=MI_STRPROYECTOS||RSCODIGO||', ';
      END LOOP;
    CLOSE MI_RS;

    MI_CAMPOS := ' COMPANIA
                 , CODIGO
                 , NOMBRE
                 , MOVIMIENTO
                 , CODIGOBP
                 , ANO
                 , REPORTAENREGISTROS
                 , CREATED_BY
                 , DATE_CREATED';

    MI_STRSQL:='SELECT  PROYECTOS.COMPANIA,
                        PROYECTOS.CODIGO,
                        SUBSTR(PROYECTOS.NOMBREPROYECTO,1, 120) NOMBREPROYECTO,
                        -1 MOVIMIENTO,
                        PROYECTOS.CODIGO,
                        PROYECTOS.VIGENCIAINICIO,
                        0 REPORTA, ''' ||
                        UN_USUARIO || ''' ,
                        SYSDATE
                FROM    PROYECTOS
                LEFT JOIN AUXILIAR
                ON      PROYECTOS.COMPANIA = AUXILIAR.COMPANIA
                AND     PROYECTOS.CODIGO   = AUXILIAR.CODIGO
                WHERE   PROYECTOS.COMPANIA       = '''||UN_COMPANIA||'''
                AND     LENGTH(PROYECTOS.CODIGO) = 8
                AND      AUXILIAR.COMPANIA        IS NULL';

   BEGIN
    BEGIN

    MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA    => 'AUXILIAR'
                                 ,UN_ACCION  => 'IS'
                                 ,UN_CAMPOS  => MI_CAMPOS
                                 ,UN_VALORES => MI_STRSQL);

               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

    END;

               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                              PCK_ERR_MSG.RAISE_WITH_MSG(
                              UN_EXC_COD     => SQLCODE
                              ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_INSERT_AUXILIAR
                              );
  END;

    MI_STRPROYECTOS:=  MI_RETURN  || ' ' ||chr(10)||chr(13)||MI_STRPROYECTOS;
    RETURN MI_STRPROYECTOS;

END FC_CREARAUXILIARES;

--3
FUNCTION FC_ELIMINARPROYECTO
/*
    NAME              : FC_ELIMINARPROYECTO NOMBRE EN ACCESS --> ELIMINARPROYECTO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO
    DATE MIGRADOR     : 19/09/2015
    TIME              : 16:45 PM
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    DESCRIPTION       : FUNCION MIGRADA DE ACCESS QUE PERMITE ELIMINAR UN PROYECTO DETERMINADO
    MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
    MODIFIER2         : JONATHAN ENRIQUE GUERRERO TORRES
    DATE MODIFIED	    : 31/01/2017
    DATE MODIFIED2	  : 13/09/2017
    TIME				      : 11:31 AM
    TIME2				      : 09:26 AM
    MODIFICATIONS	    : Se cambió el estándar de codificación y llamados por referencia de las funciones.Se agregó manejo de excepciones
    MODIFICATIONS2    : Se depuro el codigo y se implementaron excepciones que en lugar de retornar un numero se lanza el RAISE. 
    @Name: eliminarProyecto
*/
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROYECTO     IN VARCHAR2,
    UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO
  )RETURN VARCHAR2
  AS
    MI_RETURN       PCK_SUBTIPOS.TI_ENTERO:=0;
    MI_MENSAJE      VARCHAR2(3000 CHAR);
    MI_REGISTRO     VARCHAR2(1 CHAR);
  BEGIN
    --1. Verifica que no se elimine si ya tiene programaciÃ³n.
      BEGIN
      BEGIN
        SELECT COUNT(*)
         INTO MI_REGISTRO
         FROM PROGRAMACION
        WHERE COMPANIA       IN (UN_COMPANIA)
          AND CODIGOPROYECTO IN (UN_PROYECTO);

        IF MI_REGISTRO>0 THEN 
           RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
        END IF;
      END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ER_BANCOSP_EXIST_PROGRAMA);
    END;    

    --2. Verifica que no se elimine si ya existe Rubros
    BEGIN
      BEGIN
        SELECT COUNT(*)
          INTO MI_REGISTRO
          FROM BP_PROYECTOSRUBROS
         WHERE COMPANIA IN (UN_COMPANIA)
           AND PROYECTO IN (UN_PROYECTO);

        IF MI_REGISTRO>0 THEN 
             RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
        END IF;
      END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ER_BANCOSP_EXIST_RUBRO);
    END;    


    --3. Verifica que no se elimine si ya existe Fuentes de FinanciaciÃ³n
    BEGIN
      BEGIN
        SELECT COUNT(*)
          INTO MI_REGISTRO
          FROM BP_PROYFUENTESFINANCIACION
         WHERE COMPANIA IN (UN_COMPANIA)
           AND PROYECTO IN (UN_PROYECTO);

        IF MI_REGISTRO>0 THEN 
          RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
        END IF;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ER_BANCOSP_EXIST_FUEN_FINAN);
    END;


    --4. Verifica que no se elimine si ya existe indicadores
    BEGIN
      BEGIN
        SELECT COUNT(*)
          INTO MI_REGISTRO
          FROM PROYECTOINDICADOR
         WHERE COMPANIA       IN (UN_COMPANIA)
           AND CODIGOPROYECTO IN (UN_PROYECTO);

        IF MI_REGISTRO>0 THEN 
          RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
        END IF;
      END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ER_BANCOSP_EXIST_INDICADORES);
    END;

    --5. Verifica que no se elimine si ya existe novedades tÃ©cnicas
    BEGIN
      BEGIN
        SELECT COUNT(*)
          INTO MI_REGISTRO
          FROM BPPROYECTONOVEDADESTECNICAS
         WHERE COMPANIA       IN (UN_COMPANIA)
           AND CODIGOPROYECTO IN (UN_PROYECTO);

        IF MI_REGISTRO>0 THEN 
          RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
        END IF;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
         PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ER_BANCOSP_EXIST_NOVE_TECN);
    END;

    --6. Verifica que no se elimine si ya existe Responsable Asignados
    BEGIN
      BEGIN
        SELECT COUNT(*)
          INTO MI_REGISTRO
          FROM BPRESPONSABLEPROYECTO
         WHERE COMPANIA IN (UN_COMPANIA)
           AND PROYECTO IN (UN_PROYECTO);

        IF MI_REGISTRO>0 THEN 
          RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
        END IF;
      END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
         PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ER_BANCOSP_EXIST_RESPONSABLES  );
    END; 

   --7. Verifica que no se elimine si ya existe Novedades
    BEGIN
      BEGIN
        SELECT COUNT(*)
          INTO MI_REGISTRO
          FROM BP_D_NOVEDADPROYECTO
         WHERE COMPANIA IN (UN_COMPANIA)
           AND PROYECTO IN (UN_PROYECTO);

        IF MI_REGISTRO>0 THEN 
          RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
        END IF;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
           PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                      UN_ERROR_COD  => PCK_ERRORES.ER_BANCOSP_EXIST_NOVEDADES);
    END; 


    --8. Verifica que no se elimine si ya existe Fichas TÃ©cnicas
    BEGIN
      BEGIN
        SELECT COUNT(*)
          INTO MI_REGISTRO
          FROM BP_FICHA_TECNICA
         WHERE COMPANIA IN (UN_COMPANIA)
           AND PROYECTO IN (UN_PROYECTO);

        IF MI_REGISTRO>0 THEN 
          RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
        END IF;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ER_BANCOSP_EXIST_FICHAS_TEC);
    END;
    --8.1 Verifica que no se elimine si ya existe Fichas TÃ©cnicas
    BEGIN
      BEGIN
        SELECT COUNT(*)
        INTO MI_REGISTRO
        FROM BP_D_FICHA_TECNICA
        WHERE COMPANIA IN (UN_COMPANIA)
          AND PROYECTO IN (UN_PROYECTO);

      IF MI_REGISTRO>0 THEN 
           RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
        END IF;
      END;

     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
           PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                      UN_ERROR_COD  => PCK_ERRORES.ER_BANCOSP_EXIST_DETALL_FICH);
    END; 


    --9. Verifica que no se elimine si ya existe Plan Indicativo
    BEGIN
      BEGIN
        SELECT COUNT(*)
          INTO MI_REGISTRO
          FROM BP_PROYECTO_PLAN_INDICATIVO
         WHERE COMPANIA IN (UN_COMPANIA)
           AND PROYECTO IN (UN_PROYECTO);

        IF MI_REGISTRO>0 THEN 
          RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
        END IF;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ER_BANCOSP_EXIST_RELPLAN_IND);
    END; 

    --10. Verifica que no se elimine si ya existe Modificaciones
    BEGIN
      BEGIN
        SELECT COUNT(*)
          INTO MI_REGISTRO
          FROM BP_PROYECTOS_MODIFICACIONES
         WHERE COMPANIA IN (UN_COMPANIA)
           AND PROYECTO IN (UN_PROYECTO);

        IF MI_REGISTRO>0 THEN 
          RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
        END IF;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ER_BANCOSP_EXIST_MODIFICACI);
    END; 


    --11. Verifica que no se elimine si ya existe Contratos
    BEGIN
      BEGIN
        SELECT COUNT(*)
          INTO MI_REGISTRO
          FROM BP_PROYECTOS_MODIFICACIONES
         WHERE COMPANIA IN (UN_COMPANIA)
           AND PROYECTO IN (UN_PROYECTO);

        IF MI_REGISTRO>0 THEN 
          RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
        END IF;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ER_BANCOSP_EXIST_AFECTAC );
    END;


    -- 12. Esta opciÃ³n se agregÃ³ para verificar que el proyecto no tenga relaciÃ³n con la tabla AUXILIAR
    BEGIN
      BEGIN
        SELECT COUNT(*)
          INTO MI_REGISTRO
          FROM AUXILIAR
         WHERE COMPANIA IN (UN_COMPANIA)
           AND CODIGOBP IN (UN_PROYECTO);

        IF MI_REGISTRO>0 THEN 
          RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
        END IF;
      END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                 UN_ERROR_COD  => PCK_ERRORES.ER_BANCOSP_EXIST_REG_AUXIL);
    END;


    --Elimina Actividades
  BEGIN
    BEGIN
      MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA => 'COMPONENTES_ACTIVIDADES',
                                   UN_ACCION => 'E',
                                   UN_CONDICION => ' COMPANIA        = ''' || UN_COMPANIA || ''' 
                                                 AND CODIGOPROYECTO  = ''' || UN_PROYECTO || '''');

      MI_MENSAJE:=MI_MENSAJE||'Se eliminaron '||MI_RETURN||' Actividades. ';

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                 UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_DELETE_COMPACTV);
  END;

    --Elimina Componentes
  BEGIN
    BEGIN
      MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA      => 'COMPONENTES'
                                   ,UN_ACCION    => 'E'
                                   ,UN_CONDICION => ' COMPANIA        = ''' || UN_COMPANIA || '''
                                                  AND CODIGOPROYECTO  = ''' || UN_PROYECTO || '''' );

     MI_MENSAJE:=MI_MENSAJE||'Se eliminaron '||MI_RETURN||' Componentes. ';

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
     END;

     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
       PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                  UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_DELETE_COMPONENTE);
  END;

    --Elimina Localización
  BEGIN
    BEGIN
      MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA     => 'PROYECTOLOCALIZACION'
                                  ,UN_ACCION    => 'E'
                                  ,UN_CONDICION => ' COMPANIA       = ''' || UN_COMPANIA || ''' 
                                                 AND CODIGOPROYECTO = ''' || UN_PROYECTO || '''');

      MI_MENSAJE:=MI_MENSAJE||'Se eliminaron '||MI_RETURN||' Localización de Proyectos. ';

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
    END; 

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                 UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_DELETE_PROYLOC);
  END;

    --Elimina Concepto de Viabilidad
  BEGIN
    BEGIN
      MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA      => 'BP_CONCEPTOVIABILIDAD'
                                   ,UN_ACCION    => 'E'
                                   ,UN_CONDICION =>  ' COMPANIA = ''' || UN_COMPANIA || ''' 
                                                   AND PROYECTO = ''' || UN_PROYECTO || '''');

      MI_MENSAJE:=MI_MENSAJE||'Se eliminaron '||MI_RETURN||' Conceptos de Viabilidad. ';

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
    END; 

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                   UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_DELETE_BPCONCPT);
  END;

    --Elimina Criterios
  BEGIN
    BEGIN
      MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA      => 'BP_CRITERIOPRIORIZACION'
                                   ,UN_ACCION    => 'E'
                                   ,UN_CONDICION => ' COMPANIA = ''' || UN_COMPANIA || ''' 
                                                  AND PROYECTO = ''' || UN_PROYECTO || '''');
      MI_MENSAJE:=MI_MENSAJE||'Se eliminaron '||MI_RETURN||' Criterios. ';

     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
      RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
    END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                 UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_DELETE_BPCRIPRIO);
  END;

    --Elimina el Proyecto
  BEGIN
    BEGIN
      MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA      => 'PROYECTOS'
                                   ,UN_ACCION    => 'E'
                                   ,UN_CONDICION => ' COMPANIA = ''' || UN_COMPANIA || ''' 
                                                  AND CODIGO   = ''' || UN_PROYECTO || '''');

     MI_MENSAJE:=MI_MENSAJE||'Se eliminó el proyecto '||UN_PROYECTO;

     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
       RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
    END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                 UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_DELETE_PROYECTO);
  END;

    RETURN MI_MENSAJE;
END FC_ELIMINARPROYECTO;

--4
FUNCTION FC_GENERAR_PREDECESOR
/*
    NAME              : FC_GENERAR_PREDECESOR NOMBRE EN ACCESS --> GENERARPREDECESOR
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO
    DATE MIGRADOR     : 21/09/2015
    TIME              : 15:45 PM
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    DESCRIPTION       : FUNCION MIGRADA DE ACCESS QUE PERMITE GENERAR LOS PROCESADOS A PARTIR DE LA CONFIGURACIÃ?N DE NIVELES
    MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED	    : 31/01/2017
    TIME				      : 2:31 PM
    MODIFICATIONS	    : Se cambió el estándar de codificación y llamados por referencia de las funciones.Se agregó manejo de excepciones
    @Name: generarPredecesorIndicativo
*/
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_VIGENCIA     IN PCK_SUBTIPOS.TI_ENTERO,
    UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO
  )RETURN PCK_SUBTIPOS.TI_LOGICO
  AS
    MI_STRSQL         PCK_SUBTIPOS.TI_STRSQL;
    MI_RS             SYS_REFCURSOR;
    MI_DIGITOS        BP_NIVEL_PLAN_IND.DIGITOS%TYPE;
    MI_DESCRIPCION    BP_NIVEL_PLAN_IND.DESCRIPCION%TYPE;
    MI_META_RESULT    BP_NIVEL_PLAN_IND.META_RESUL%TYPE;
    MI_META_PRODUCT   BP_NIVEL_PLAN_IND.META_PRODUC%TYPE;
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;


    MI_NIVELES        VECNIVELES; 
    MI_CONTADOR       PCK_SUBTIPOS.TI_ENTERO:=0;
    MI_RETURN         PCK_SUBTIPOS.TI_ENTERO;


  BEGIN

  MI_STRSQL:='SELECT BP_NIVEL_PLAN_IND.DIGITOS
                    ,BP_NIVEL_PLAN_IND.DESCRIPCION
                    ,BP_NIVEL_PLAN_IND.META_RESUL
                    ,BP_NIVEL_PLAN_IND.META_PRODUC
              FROM  BP_NIVEL_PLAN_IND
              WHERE BP_NIVEL_PLAN_IND.COMPANIA ='''||UN_COMPANIA||'''
                AND BP_NIVEL_PLAN_IND.VIGENCIA ='||UN_VIGENCIA||'
              ORDER BY BP_NIVEL_PLAN_IND.COMPANIA
                    , BP_NIVEL_PLAN_IND.VIGENCIA
                    , BP_NIVEL_PLAN_IND.DIGITOS ASC
                    , BP_NIVEL_PLAN_IND.DESCRIPCION ';

  OPEN MI_RS FOR MI_STRSQL;
      LOOP

      FETCH MI_RS INTO MI_DIGITOS, MI_DESCRIPCION, MI_META_RESULT, MI_META_PRODUCT;
      EXIT WHEN MI_RS%NOTFOUND;
        MI_NIVELES(MI_CONTADOR).DIGITOS          :=MI_DIGITOS;
        MI_NIVELES(MI_CONTADOR).DESCRIPCION      :=MI_DESCRIPCION;
        MI_NIVELES(MI_CONTADOR).M_RES            :=MI_META_RESULT;
        MI_NIVELES(MI_CONTADOR).M_PRO            :=MI_META_PRODUCT;


        MI_CONTADOR:=MI_CONTADOR+1;
      END LOOP;
  CLOSE MI_RS;

  IF(MI_CONTADOR = 0) THEN
      RETURN 0;
  END IF;

   FOR I IN REVERSE 2..MI_CONTADOR LOOP
   BEGIN
    BEGIN

    MI_CAMPOS    := 'BP_PLAN_INDICATIVO.PREDECESOR    = SUBSTR(BP_PLAN_INDICATIVO.ID, 1, '|| MI_NIVELES(I-2).DIGITOS||'),
                     BP_PLAN_INDICATIVO.MODIFIED_BY   = '''||UN_USUARIO||''', 
                     BP_PLAN_INDICATIVO.DATE_MODIFIED = SYSDATE';

    MI_CONDICION :=  'BP_PLAN_INDICATIVO.COMPANIA=''' || UN_COMPANIA || '''
                                                  AND BP_PLAN_INDICATIVO.VIGENCIA_INICIAL='||UN_VIGENCIA||'
                                                  AND LENGTH(BP_PLAN_INDICATIVO.ID)='||MI_NIVELES(I-1).DIGITOS;

    MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA      => 'BP_PLAN_INDICATIVO'
                                 ,UN_ACCION    => 'M'
                                 ,UN_CAMPOS    => MI_CAMPOS
                                 ,UN_CONDICION => MI_CONDICION );

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
    END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                              PCK_ERR_MSG.RAISE_WITH_MSG(
                              UN_EXC_COD     => SQLCODE
                              ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_BPPLANIND
                              );

   END;

  END LOOP;

  RETURN -1;

END FC_GENERAR_PREDECESOR;

--5
FUNCTION FC_F_SCV_17
/*
    NAME              : FC_F_SCV_17
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO
    DATE MIGRADOR     : 30/09/2015
    TIME              : 10:00 AM
    DESCRIPTION       : PERMITE PREPARAR LOS DATOS PARA LA GENERACIÓN DEL INFORME SCV_17
    PARAMETROS DE ENTRADA: 
      UN_COMPANIA     : Código de la compañía
      UN_VIGENCIA     : Año para el que se quiere generar el informe.
      UN_FECHA_INICIO : Fecha inicial
      UN_FECHA_FIN    : Fecha final.
      UN_FUENTE_INICIO: Fuente de financiación inicial.
      UN_FUENTE_FIN   : Fuente de financiación final.
      UN_TODAS_FUENTES: Indica si el informe se va a generar para todas las fuentes de financiación.
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED     : 31/01/2017
    TIME              : 2:31 PM
    MODIFICATIONS     : Se cambió el estándar de codificación y llamados por referencia de las funciones.Se agregó manejo de excepciones
    MODIFIER          : JUAN CARLOS RODRÍGUEZ AMÉZQUITA
    DATE MODIFIED     : 27/09/2017
    TIME MODIFIED     : 03:21 PM
    MODIFICATIONS     : Conversión a función para retornar CLOB con los datos necesarios 
                        para generar la plantilla de Ejecución de Proyectos.

    @NAME:   prepararDatosSCV17
    @METHOD: GET
*/
(
  UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_VIGENCIA           IN PCK_SUBTIPOS.TI_ENTERO,
  UN_FECHA_INICIO       IN DATE,
  UN_FECHA_FIN          IN DATE,
  UN_FUENTE_INICIO      IN VARCHAR2,
  UN_FUENTE_FIN         IN VARCHAR2,
  UN_TODAS_FUENTES      IN PCK_SUBTIPOS.TI_LOGICO
)RETURN CLOB 
AS
  MI_FECHAINICIAL       VARCHAR2(255 CHAR);
  MI_FECHAFIN           VARCHAR2(255 CHAR);
  MI_MESES              VARCHAR2(3000 CHAR);
  MI_STRSQL             PCK_SUBTIPOS.TI_STRSQL;
  MI_RS                 SYS_REFCURSOR;
  MI_DIGITOS            PCK_SUBTIPOS.TI_ENTERO;
  MI_CONTADOR           PCK_SUBTIPOS.TI_ENTERO;
  MI_IDF                VARCHAR2(255 CHAR);
  MI_PROGRAMA           VARCHAR2(3000 CHAR);
  MI_RETURN             PCK_SUBTIPOS.TI_ENTERO;
  MI_FUENTEPROYECTO     VARCHAR2(3000 CHAR);
  MI_ACTIVIDADPROYECTO  VARCHAR2(3000 CHAR);
  MI_RS1                SYS_REFCURSOR;
  RS_NCONTRATO          VARCHAR2(3000 CHAR);
  MI_CONTRATOS          VARCHAR2(3000 CHAR);
  MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
  MI_CADENA_RTA         CLOB;
BEGIN
    DELETE FROM TEMP_F17_ACTIVIDADESPROYECTO;
    MI_FECHAINICIAL:=TO_CHAR(UN_FECHA_INICIO, 'dd/MM/YYYY');
    MI_FECHAFIN    :=TO_CHAR(UN_FECHA_FIN, 'dd/MM/YYYY');
    MI_STRSQL := 'SELECT COMPONENTES_ACTIVIDADES.COMPANIA,
           PROYECTOS.VIGENCIAINICIO,
           PROYECTOS.CODIGO,
           COMPONENTES_ACTIVIDADES.COMPONENTE,
           COMPONENTES_ACTIVIDADES.ACTIVIDAD,
           PROYECTOS.CODIGOBPIM "2-CODIGO",
           PROYECTOS.NOMBREPROYECTO "3-DENOMINACION",
           TMP_ACTIVIDADES_PROYECTO.NOMBRE FUENTES,
           '''' "5PROGRAMA",
           '''' "6SECTOR",
           COMPONENTES_ACTIVIDADES.NOMBREACTIVIDAD "71-DENOMINACION",
           UNIDADPROYECTOS.NOMBRE "72-UNIDAD",
           ROUND(COSTOTOTAL/VALORTOTAL*100,2) "73-PONDERACION",
           TO_CHAR(FECHAREGISTRO,''YYYYMM'') "81-PERIODOINICIO",
           VIGENCIA || ''12'' "82-PERIODOFIN",
           COMPONENTES_ACTIVIDADES.CANTIDAD "83-CANTIDAD",
           COMPONENTES_ACTIVIDADES.COSTOTOTAL "84-VALOR",
           '''' "91NOCONTRATO",
           COMPONENTES_ACTIVIDADES.CANTIDAD_EJE "92-CANTIDADEJECUTADA",
           COMPONENTES_ACTIVIDADES.VALOREJECUTADO "93-VALOREJECUTADO",
           '''' "10PERIODOREPORTE",
           PROYECTOS.FECHAREGISTRO,
           TMP_ACTIVIDADES_PROYECTO.FUENTERECURSOS
      FROM COMPONENTES_ACTIVIDADES
     INNER JOIN PROYECTOS
        ON COMPONENTES_ACTIVIDADES.COMPANIA = PROYECTOS.COMPANIA
       AND COMPONENTES_ACTIVIDADES.CODIGOPROYECTO = PROYECTOS.CODIGO
     INNER JOIN BP_ACTIVIDADES
        ON COMPONENTES_ACTIVIDADES.COMPANIA = BP_ACTIVIDADES.COMPANIA
       AND COMPONENTES_ACTIVIDADES.ACTIVIDAD = BP_ACTIVIDADES.CODIGO
      INNER JOIN UNIDADPROYECTOS
        ON BP_ACTIVIDADES.COMPANIA = UNIDADPROYECTOS.COMPANIA
       AND BP_ACTIVIDADES.UNIDAD = UNIDADPROYECTOS.UNIDAD
      INNER JOIN (
           SELECT BP_D_NOVEDADPROYECTO.COMPANIA,
                  BP_D_NOVEDADPROYECTO.PROYECTO,
                  BP_D_NOVEDADPROYECTO.COMPONENTE,
                  BP_D_NOVEDADPROYECTO.ACTIVIDAD,
                  BP_D_NOVEDADPROYECTO.FUENTERECURSOS,
                  BP_D_NOVEDADPROYECTO.TIPOCOMPONENTE,
                  FUENTE_RECURSOS.NOMBRE,
                  FUENTE_RECURSOS.TIPO
             FROM BP_D_NOVEDADPROYECTO
            INNER JOIN FUENTE_RECURSOS
               ON BP_D_NOVEDADPROYECTO.COMPANIA       = FUENTE_RECURSOS.COMPANIA
              AND BP_D_NOVEDADPROYECTO.FUENTERECURSOS = FUENTE_RECURSOS.CODIGO
            WHERE BP_D_NOVEDADPROYECTO.COMPANIA = ''' || UN_COMPANIA || '''
              AND BP_D_NOVEDADPROYECTO.TIPOT    = ''SCD''
              AND FUENTE_RECURSOS.TIPO         IN (''SGR'',''REL'',''COMP'')
            GROUP BY BP_D_NOVEDADPROYECTO.COMPANIA 
                   , BP_D_NOVEDADPROYECTO.PROYECTO 
                   , BP_D_NOVEDADPROYECTO.COMPONENTE 
                   , BP_D_NOVEDADPROYECTO.ACTIVIDAD 
                   , BP_D_NOVEDADPROYECTO.TIPOCOMPONENTE
                   , BP_D_NOVEDADPROYECTO.FUENTERECURSOS 
                   , FUENTE_RECURSOS.NOMBRE 
                   , FUENTE_RECURSOS.TIPO) TMP_ACTIVIDADES_PROYECTO
        ON COMPONENTES_ACTIVIDADES.COMPANIA       = TMP_ACTIVIDADES_PROYECTO.COMPANIA
       AND COMPONENTES_ACTIVIDADES.CODIGOPROYECTO = TMP_ACTIVIDADES_PROYECTO.PROYECTO
       AND COMPONENTES_ACTIVIDADES.COMPONENTE     = TMP_ACTIVIDADES_PROYECTO.COMPONENTE
       AND COMPONENTES_ACTIVIDADES.TIPOCOMPONENTE = TMP_ACTIVIDADES_PROYECTO.TIPOCOMPONENTE
       AND COMPONENTES_ACTIVIDADES.ACTIVIDAD      = TMP_ACTIVIDADES_PROYECTO.ACTIVIDAD
     WHERE COMPONENTES_ACTIVIDADES.COMPANIA = ''' || UN_COMPANIA || '''
       AND VALORTOTAL <> 0
       AND TRUNC(PROYECTOS.FECHAREGISTRO) BETWEEN TO_DATE(''' || MI_FECHAINICIAL || ''',''DD/MM/YYYY'') 
                                              AND TO_DATE(''' || MI_FECHAFIN || ''',''DD/MM/YYYY'')';
  IF UN_TODAS_FUENTES = 0 THEN
    MI_STRSQL:=MI_STRSQL||'AND TMP_ACTIVIDADES_PROYECTO.FUENTERECURSOS BETWEEN '''||UN_FUENTE_INICIO||''' AND '''||UN_FUENTE_FIN||'''';
  END IF;
  --
  MI_CAMPOS:='COMPANIA
              , VIGENCIAINICIO
              , CODIGO
              , COMPONENTE
              , ACTIVIDAD
              , CODIGOBPIM
              , DENOMINACION
              , FUENTES
              , PROGRAMA
              , SECTOR
              , NOMBREACTIVIDAD
              , UNIDAD
              , PONDERACION
              , PERIODOINICIO
              , PERIODOFIN
              , CANTIDAD
              , VALOR
              , NCONTRATO
              , CANTIDAD_EJE
              , VALOREJECUTADO
              , PERIODOREPORTE
              , FECHAREGISTRO
              , FUENTERECURSOS';
  BEGIN
    BEGIN
      MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA    => 'TEMP_F17_ACTIVIDADESPROYECTO'
                                   ,UN_ACCION  => 'IS'
                                   ,UN_CAMPOS  => MI_CAMPOS
                                   ,UN_VALORES => MI_STRSQL);
    EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
    END;
  EXCEPTION 
    WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
      UN_EXC_COD     => SQLCODE
      ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_INSERT_TF17ACTVPROY
      );
  END;
  --
  FOR MI_RS IN (SELECT CODIGO, FUENTERECURSOS, ACTIVIDAD FROM TEMP_F17_ACTIVIDADESPROYECTO) LOOP
    --- Actualiza el programa
    BEGIN
      SELECT MAX(DIGITOS) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) ULTIMO
      INTO MI_DIGITOS
      FROM BP_NIVEL_PLAN_IND
      WHERE BP_NIVEL_PLAN_IND.COMPANIA=UN_COMPANIA
        AND BP_NIVEL_PLAN_IND.VIGENCIA >= UN_VIGENCIA
        AND BP_NIVEL_PLAN_IND.DESCRIPCION='PROGRAMA';

    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_DIGITOS:=0;
    END;
    IF (MI_DIGITOS IS NOT NULL OR MI_DIGITOS != 0) THEN
      BEGIN
        SELECT MIN(SUBSTR(ID_PLAN_P, 1, MI_DIGITOS)) IDF
          INTO MI_IDF
          FROM BP_PROYECTO_PLAN_INDICATIVO
         WHERE BP_PROYECTO_PLAN_INDICATIVO.COMPANIA = UN_COMPANIA
           AND BP_PROYECTO_PLAN_INDICATIVO.PROYECTO = MI_RS.CODIGO
         GROUP BY BP_PROYECTO_PLAN_INDICATIVO.VIGENCIA_PLAN_P
                , BP_PROYECTO_PLAN_INDICATIVO.COMPANIA
                , BP_PROYECTO_PLAN_INDICATIVO.PROYECTO;
      EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_IDF:=NULL;
      END;
      IF MI_IDF IS NOT NULL THEN
        BEGIN
          SELECT MIN(BP_PLAN_INDICATIVO.DESCRIPCION) PROGRAMA
          INTO MI_PROGRAMA
          FROM BP_PLAN_INDICATIVO
          WHERE BP_PLAN_INDICATIVO.COMPANIA=UN_COMPANIA
            AND BP_PLAN_INDICATIVO.ID=MI_IDF
            AND VIGENCIA_INICIAL = UN_VIGENCIA;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_PROGRAMA:=NULL;
        END;
        --
        BEGIN
          BEGIN
          MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA      => 'TEMP_F17_ACTIVIDADESPROYECTO'
                                       ,UN_ACCION    => 'M'
                                       ,UN_CAMPOS    => 'PROGRAMA = '''||MI_PROGRAMA||''''
                                       ,UN_CONDICION => ' CODIGO = '''||MI_RS.CODIGO||'''');

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
          END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD     => SQLCODE
            , UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_TF17ACTVPROY);
       END;
      END IF;
    END IF;
    -- Actualiza el sector
    BEGIN
      SELECT MAX(DIGITOS) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) ULTIMO
      INTO MI_DIGITOS
      FROM BP_NIVEL_PLAN_IND
      WHERE BP_NIVEL_PLAN_IND.COMPANIA    =  UN_COMPANIA
        AND BP_NIVEL_PLAN_IND.VIGENCIA    >= UN_VIGENCIA
        AND BP_NIVEL_PLAN_IND.DESCRIPCION = 'SECTOR';
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_DIGITOS:=0;
    END;
    IF MI_DIGITOS IS NOT NULL OR MI_DIGITOS != 0 THEN
      BEGIN
        SELECT MIN(SUBSTR(ID_PLAN_P, 1, MI_DIGITOS)) IDF
        INTO MI_IDF
        FROM BP_PROYECTO_PLAN_INDICATIVO
        WHERE BP_PROYECTO_PLAN_INDICATIVO.COMPANIA  = UN_COMPANIA
          AND BP_PROYECTO_PLAN_INDICATIVO.PROYECTO  = MI_RS.CODIGO
        GROUP BY BP_PROYECTO_PLAN_INDICATIVO.VIGENCIA_PLAN_P
                 ,BP_PROYECTO_PLAN_INDICATIVO.COMPANIA
                 ,BP_PROYECTO_PLAN_INDICATIVO.PROYECTO;

      EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_IDF:=NULL;
      END;
      IF(MI_IDF IS NOT NULL) THEN
        BEGIN
          SELECT MIN(BP_PLAN_INDICATIVO.DESCRIPCION) PROGRAMA
          INTO MI_PROGRAMA
          FROM BP_PLAN_INDICATIVO
          WHERE BP_PLAN_INDICATIVO.COMPANIA = UN_COMPANIA
            AND BP_PLAN_INDICATIVO.ID       = MI_IDF
            AND VIGENCIA_INICIAL            = UN_VIGENCIA;

        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_PROGRAMA:=NULL;
        END;
        BEGIN
          BEGIN
            MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA      => 'TMP_F17_ACTIVIDADESPROYECTO'
                                         ,UN_ACCION    => 'M'
                                         ,UN_CAMPOS    => '6SECTOR = '''||MI_PROGRAMA||''
                                         ,UN_CONDICION => 'CODIGO = '''||MI_RS.CODIGO||'');
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
          END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD     => SQLCODE
          , UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_TF17ACTVPROY);
        END;
      END IF;
    END IF;
    --- Actualiza el contrato
    MI_FUENTEPROYECTO     :=  MI_RS.FUENTERECURSOS;
    MI_ACTIVIDADPROYECTO  :=  MI_RS.ACTIVIDAD;
    IF MI_FUENTEPROYECTO IS NULL THEN
        MI_FUENTEPROYECTO:='ND';
    END IF;
    IF MI_ACTIVIDADPROYECTO IS NULL THEN
        MI_ACTIVIDADPROYECTO:='ND';
    END IF;
    MI_STRSQL:='SELECT CLASEORDEN ||''-''|| ORDENDECOMPRA
                FROM ORDENDECOMPRA_AUXILIAR
                WHERE COMPANIA        ='''||UN_COMPANIA||'''
                  AND AUXILIAR        ='''||MI_RS.CODIGO||'''
                  AND FUENTE_RECURSOS ='''||MI_FUENTEPROYECTO||'''
                  AND ACTIVIDAD       ='''||MI_ACTIVIDADPROYECTO||'''';
    OPEN MI_RS1 FOR MI_STRSQL;
    LOOP
      FETCH MI_RS1 INTO RS_NCONTRATO;
      EXIT WHEN MI_RS1%NOTFOUND;
        IF(MI_CONTRATOS IS NULL OR MI_CONTRATOS= '') THEN
          MI_CONTRATOS:=RS_NCONTRATO;
        ELSE
          MI_CONTRATOS:=MI_CONTRATOS||', '||RS_NCONTRATO;
        END IF;
    END LOOP;
    CLOSE MI_RS1;
    --
    BEGIN
      BEGIN
        MI_CAMPOS := 'NCONTRATO = '''||MI_CONTRATOS||'''';
        MI_CONDICION := ' CODIGO          = '''||MI_RS.CODIGO||'''
                      AND ACTIVIDAD       = '''||MI_ACTIVIDADPROYECTO||'''
                      AND FUENTERECURSOS  ='''||MI_FUENTEPROYECTO||'''';
        MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA      => 'TEMP_F17_ACTIVIDADESPROYECTO'
                                     ,UN_ACCION    => 'M'
                                     ,UN_CAMPOS    =>  MI_CAMPOS
                                     ,UN_CONDICION =>  MI_CONDICION);
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
        MI_CONTRATOS:='';
      END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD     => SQLCODE
        , UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_TF17ACTVPROY);
    END;
  END LOOP;
  --Actualiza sector, programa y contrato cuando son null
  BEGIN
    BEGIN
      MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA      => 'TEMP_F17_ACTIVIDADESPROYECTO'
                                   ,UN_ACCION    => 'M'
                                   ,UN_CAMPOS    => 'SECTOR = ''ND'''
                                   ,UN_CONDICION => 'SECTOR = NULL');
      MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA      => 'TEMP_F17_ACTIVIDADESPROYECTO'
                                   ,UN_ACCION    => 'M'
                                   ,UN_CAMPOS    => 'PROGRAMA = ''ND'''
                                   ,UN_CONDICION => 'PROGRAMA = NULL');
      MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA      => 'TEMP_F17_ACTIVIDADESPROYECTO'
                                   ,UN_ACCION    => 'M'
                                   ,UN_CAMPOS    => 'NCONTRATO = ''ND'''
                                   ,UN_CONDICION => 'NCONTRATO = NULL');
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
    END;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
    PCK_ERR_MSG.RAISE_WITH_MSG(
      UN_EXC_COD     => SQLCODE
    , UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_TF17ACTVPROY);
  END;
  -- Generación de cadena con los datos requeridos por la plantilla
  DECLARE
    MI_SEPARADOR_COL                VARCHAR2(10);
    MI_SEPARADOR_REG                VARCHAR2(10);
  BEGIN
    MI_SEPARADOR_COL := PCK_DATOS.GL_SEPARADOR_COL;
    MI_SEPARADOR_REG := PCK_DATOS.GL_SEPARADOR_REG;
    MI_CADENA_RTA := '';
    <<reccorrer_temporal>>
    FOR MI_RS 
    IN (
     SELECT NVL(CODIGOBPIM,'ND') CODIGOBPIM,
            NVL(DENOMINACION, 'ND') DENOMINACION,
            NVL(FUENTES, 'ND') FUENTES,
            NVL(PROGRAMA, 'ND') PROGRAMA,
            NVL(SECTOR, 'ND') SECTOR,
            NVL(NOMBREACTIVIDAD, 'ND') NOMBREACTIVIDAD,
            NVL(UNIDAD,'ND') UNIDAD,
            PONDERACION,
            NVL(PERIODOINICIO, 'ND') PERIODOINICIO,
            NVL(PERIODOFIN, 'ND') PERIODOFIN,
            CANTIDAD,
            VALOR,
            NVL(NCONTRATO, 'ND') NCONTRATO,
            CANTIDAD_EJE,
            VALOREJECUTADO
       FROM TEMP_F17_ACTIVIDADESPROYECTO
    )
    LOOP
      MI_CADENA_RTA := MI_CADENA_RTA || TO_CHAR(MI_RS.CODIGOBPIM) 
        || MI_SEPARADOR_COL || TO_CHAR(MI_RS.DENOMINACION)
        || MI_SEPARADOR_COL || TO_CHAR(MI_RS.FUENTES)
        || MI_SEPARADOR_COL || TO_CHAR(MI_RS.PROGRAMA) 
        || MI_SEPARADOR_COL || TO_CHAR(MI_RS.SECTOR )
        || MI_SEPARADOR_COL || TO_CHAR(MI_RS.NOMBREACTIVIDAD )
        || MI_SEPARADOR_COL || TO_CHAR(MI_RS.UNIDAD )
        || MI_SEPARADOR_COL || TO_CHAR(MI_RS.PONDERACION)
        || MI_SEPARADOR_COL || TO_CHAR(MI_RS.PERIODOINICIO)
        || MI_SEPARADOR_COL || TO_CHAR(MI_RS.PERIODOFIN )
        || MI_SEPARADOR_COL || TO_CHAR(MI_RS.CANTIDAD )
        || MI_SEPARADOR_COL || TO_CHAR(MI_RS.VALOR )
        || MI_SEPARADOR_COL || TO_CHAR(MI_RS.NCONTRATO )
        || MI_SEPARADOR_COL || TO_CHAR(MI_RS.CANTIDAD_EJE )
        || MI_SEPARADOR_COL || TO_CHAR(MI_RS.VALOREJECUTADO)
        || MI_SEPARADOR_REG;
    END LOOP reccorrer_temporal;
  END;
  RETURN MI_CADENA_RTA;
END FC_F_SCV_17;

--6
FUNCTION FC_F_SCV_18
/*
    NAME              : PR_F_SCV_18
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO
    DATE MIGRADOR     : 01/10/2015
    TIME              : 10:00 AM
    DESCRIPTION       : PROCEDIMIENTO QUE PERMITE PREPARAR LOS DATOS PARA LA GENERACIÃ?N DEL INFORME SCV_18
    MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED	    : 31/01/2017//12/09/2017
    TIME				      : 3:54 PM
    MODIFICATIONS	    : Se cambió el estándar de codificación y llamados por referencia de las funciones.Se agregó manejo de excepciones.
                       // Se cambió el procedimiento a funcion

    @NAME:    prepararDatosSCV18 
    @METHOD:  POST
*/

(
  UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_VIGENCIA     IN PCK_SUBTIPOS.TI_ENTERO,
  UN_FECHAINI     IN DATE,
  UN_FECHAFIN     IN DATE,
  UN_FUENTEINI    IN VARCHAR2,
  UN_FUENTEFIN    IN VARCHAR2,
  UN_FUENTE       IN PCK_SUBTIPOS.TI_ENTERO
)RETURN CLOB 
AS
  MI_STRSQL       PCK_SUBTIPOS.TI_STRSQL;
  MI_VALORSGR     VARCHAR2(3000 CHAR);
  MI_FECHAINI     VARCHAR2(15 CHAR);
  MI_FECHAFIN     VARCHAR2(15 CHAR);
  MI_RETURN       PCK_SUBTIPOS.TI_ENTERO;
  MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
  MI_RS           SYS_REFCURSOR;
  MI_SALDO        PCK_SUBTIPOS.TI_ENTERO;
  MI_SALDOFINAL   PCK_SUBTIPOS.TI_ENTERO;
  MI_SSQL         VARCHAR2(3000 CHAR);
  MI_VALOR_DEBITO PCK_SUBTIPOS.TI_ENTERO;
  MI_RSRUBRO      SYS_REFCURSOR;
  MI_TIPORECURSO  VARCHAR2(255 CHAR);
  MI_SALDOSGR     PCK_SUBTIPOS.TI_ENTERO:=0;
  MI_SALDOOTROS   PCK_SUBTIPOS.TI_ENTERO:=0;
  MI_STR          CLOB;
  MI_SEPARADOR    PCK_SUBTIPOS.TI_CONDICION;
  MI_SEPARADORREGISTRO PCK_SUBTIPOS.TI_CONDICION;


BEGIN
  DELETE FROM TEMP_F18_CERTCONTRATOS;

  MI_FECHAINI  := TO_CHAR(UN_FECHAINI, 'dd/MM/YYYY');
  MI_FECHAFIN  := TO_CHAR(UN_FECHAFIN, 'dd/MM/YYYY');

  MI_VALORSGR:='SELECT DETALLE_COMPROBANTE_PPTAL.COMPANIA,
                        DETALLE_COMPROBANTE_PPTAL.ANO,
                        DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE,
                        DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO,
                        DETALLE_COMPROBANTE_PPTAL.TIPOCONTRATO,
                        DETALLE_COMPROBANTE_PPTAL.NUMEROCONTRATO,
                        DETALLE_COMPROBANTE_PPTAL.CUENTA, SALDO_AUX_PPTAL.FUENTE_RECURSO,
                        FUENTE_RECURSOS.TIPO_RECURSO
                FROM DETALLE_COMPROBANTE_PPTAL
                 INNER JOIN V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL
                         ON DETALLE_COMPROBANTE_PPTAL.COMPANIA = PLAN_PRESUPUESTAL.COMPANIA
                        AND DETALLE_COMPROBANTE_PPTAL.ANO 	   = PLAN_PRESUPUESTAL.ANO
                        AND DETALLE_COMPROBANTE_PPTAL.CUENTA   = PLAN_PRESUPUESTAL.ID
                    INNER JOIN SALDO_AUX_PPTAL
                            ON PLAN_PRESUPUESTAL.COMPANIA = SALDO_AUX_PPTAL.COMPANIA
                           AND PLAN_PRESUPUESTAL.ANO 	    = SALDO_AUX_PPTAL.ANO
                           AND PLAN_PRESUPUESTAL.ID       = SALDO_AUX_PPTAL.ID
                        INNER JOIN FUENTE_RECURSOS
                                ON SALDO_AUX_PPTAL.COMPANIA       = FUENTE_RECURSOS.COMPANIA
                               AND SALDO_AUX_PPTAL.ANO			      = FUENTE_RECURSOS.ANO
                               AND SALDO_AUX_PPTAL.FUENTE_RECURSO = FUENTE_RECURSOS.CODIGO
                WHERE DETALLE_COMPROBANTE_PPTAL.COMPANIA      = '''||UN_COMPANIA||'''
                  AND DETALLE_COMPROBANTE_PPTAL.ANO			      = '||UN_VIGENCIA||'
                  AND DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE	    = ''RES''
                  AND DETALLE_COMPROBANTE_PPTAL.NUMEROCONTRATO <> 0';

  MI_STRSQL:='(SELECT PROYECTOS.CODIGO,
                      PROYECTOS.CODIGOBPIM,
                      PROYECTOS.NOMBREPROYECTO,
                      ORDENDECOMPRA.NOPROCESO,
                      ORDENDECOMPRAPPTO.RUBRO,
                      ORDENDECOMPRA.NUMERO,
                      ORDENDECOMPRA.NOMBRECONTRATISTA,
                      ORDENDECOMPRA.TERCERO,
                      ORDENDECOMPRA.DESCRIPCION,
                      CASE WHEN ORDENDECOMPRAPPTO.TIPOPPTO=''RES'' THEN ORDENDECOMPRAPPTO.NUMERO END RESERVA,
                      ORDENDECOMPRA.PLAZODEENTREGA,
                      ORDENDECOMPRA.VALORTOTAL,
                      ORDENDECOMPRA.FECHAFIRMA,
                      ORDENDECOMPRA.NUMERO,
                      ORDENDECOMPRA.CLASEORDEN,
                      ORDENDECOMPRA.VALOR_ANTICIPO,
                      ORDENDECOMPRA.VALORDELOSPAGOS-ORDENDECOMPRA.VALOR_ANTICIPO,
                      ORDENDECOMPRA.VALORDELOSPAGOS,
                      ORDENDECOMPRA.INTERVENTOR,
                      ORDENDECOMPRA.CEDULAINTERVENTOR,
                      0 AS SALDODISPONIBLE,
                      0 AS SGR,
                      0 AS OTRASFUENTES
              FROM PROYECTOS
                INNER JOIN ORDENDECOMPRA_AUXILIAR
                        ON PROYECTOS.CODIGO   = ORDENDECOMPRA_AUXILIAR.AUXILIAR
                       AND PROYECTOS.COMPANIA = ORDENDECOMPRA_AUXILIAR.COMPANIA
                    INNER JOIN ORDENDECOMPRA
                            ON ORDENDECOMPRA_AUXILIAR.COMPANIA      = ORDENDECOMPRA.COMPANIA
                           AND ORDENDECOMPRA_AUXILIAR.CLASEORDEN    = ORDENDECOMPRA.CLASEORDEN
                           AND ORDENDECOMPRA_AUXILIAR.ORDENDECOMPRA = ORDENDECOMPRA.NUMERO
                        INNER JOIN ORDENDECOMPRAPPTO
                                ON ORDENDECOMPRA.COMPANIA   = ORDENDECOMPRAPPTO.COMPANIA
                               AND ORDENDECOMPRA.CLASEORDEN = ORDENDECOMPRAPPTO.CLASEORDEN
                               AND ORDENDECOMPRA.NUMERO     = ORDENDECOMPRAPPTO.NUMERO
              WHERE PROYECTOS.COMPANIA       ='''||UN_COMPANIA||'''
                AND ORDENDECOMPRA.FECHAFIRMA BETWEEN '''||MI_FECHAINI||''' AND '''||MI_FECHAFIN||'''';

  MI_CAMPOS:='CODIGO
              , CODIGOBPIM
              , NOMBREPROYECTO
              , NOPROCESO
              , CODIGOS_RUBROS
              , NOCONTRATO
              , NOMBRECONTRATISTA
              , CEDULACONTRATISTA
              , OBJETO
              , REGISTROPRESUPUESTAL
              , PLAZODEENTREGA
              , VALORTOTAL
              , FECHAFIRMA
              , NUMERO
              , CLASEORDEN
              , VALOR_ANTICIPO
              , VALORDIFERENTEANTICIPO
              , VALORDELOSPAGOS
              , INTERVENTOR
              , CEDULAINTERVENTOR
              , SALDODISPONIBLE
              , SGR
              , OTRASFUENTES';

      IF(UN_FUENTE <> 0) THEN
        MI_STRSQL:=MI_STRSQL||')';
      ELSE
        MI_STRSQL:=MI_STRSQL||' AND ORDENDECOMPRA_AUXILIAR.FUENTE_RECURSOS BETWEEN '''||UN_FUENTEINI||''' AND '''||UN_FUENTEFIN||''')';
      END IF;
  BEGIN
    BEGIN
       MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA   => 'TEMP_F18_CERTCONTRATOS'
                                   ,UN_ACCION  => 'IS'
                                   ,UN_CAMPOS  => MI_CAMPOS
                                   ,UN_VALORES => MI_STRSQL);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

    END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                         PCK_ERR_MSG.RAISE_WITH_MSG(
                         UN_EXC_COD     => SQLCODE
                         ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_INSERT_TF18CERTCONT
                         );

  END;
  FOR MI_RS IN (SELECT CLASEORDEN, NOCONTRATO, VALORTOTAL, CODIGOS_RUBROS FROM TEMP_F18_CERTCONTRATOS) LOOP
    --Consulta y actualiza el valor de los saldos
    BEGIN
      SELECT SUM(VALOR_DEBITO)
      INTO MI_SALDO
      FROM DETALLE_COMPROBANTE_CNT
      WHERE COMPANIA        = ''||UN_COMPANIA||''
        AND TIPOCONTRATO    = ''||MI_RS.CLASEORDEN||''
        AND NUMEROCONTRATO  = MI_RS.NOCONTRATO
        AND ANO             = UN_VIGENCIA
        AND TIPO_CPTE       = 'COM'
        AND NATURALEZA      = 'D'
        GROUP BY TIPOCONTRATO
                 , NUMEROCONTRATO
                 , ANO
                 , COMPANIA
                 , TIPO_CPTE
                 , NATURALEZA;

      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_SALDO:=0;
    END;

    MI_SALDOFINAL:=MI_RS.VALORTOTAL-MI_SALDO;

    -- Actualiza saldo disponible de la tabla temporal
   BEGIN
      BEGIN
          MI_CAMPOS := 'SALDODISPONIBLE='||MI_SALDOFINAL;

          MI_CONDICION := ' NOCONTRATO= '''||MI_RS.NOCONTRATO||'''
                        AND CLASEORDEN= '''||MI_RS.CLASEORDEN||'''';

          MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA      => 'TEMP_F18_CERTCONTRATOS'
                                       ,UN_ACCION    => 'M'
                                       ,UN_CAMPOS    =>  MI_CAMPOS
                                       ,UN_CONDICION =>  MI_CONDICION);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

      END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                           PCK_ERR_MSG.RAISE_WITH_MSG(
                           UN_EXC_COD     => SQLCODE
                           ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_TF18CERTCONT
                           );
   END;

    MI_SSQL:='SELECT VALOR_DEBITO, TIPO_RECURSO
              FROM ('||MI_VALORSGR||') TMP_VALORSGR
              WHERE CUENTA         = '''||MI_RS.CODIGOS_RUBROS||'''
                AND NUMEROCONTRATO = '''||MI_RS.NOCONTRATO||'''
                AND TIPOCONTRATO   = '''||MI_RS.CLASEORDEN||'''';

    OPEN MI_RSRUBRO FOR MI_SSQL;
        LOOP
          FETCH MI_RSRUBRO INTO MI_VALOR_DEBITO, MI_TIPORECURSO;
          EXIT WHEN MI_RSRUBRO%NOTFOUND;
            IF(MI_TIPORECURSO = 'SGR' OR MI_TIPORECURSO = 'REL') THEN
              MI_SALDOSGR:=MI_SALDOSGR+MI_VALOR_DEBITO;
            ELSE
              MI_SALDOOTROS:= MI_SALDOOTROS+MI_VALOR_DEBITO;
            END IF;
        END LOOP;
    CLOSE MI_RSRUBRO;

    --Actualiza valores
    BEGIN
      BEGIN
        MI_CAMPOS := ' SGR           = '||MI_SALDOSGR||'
                      , OTRASFUENTES = '||MI_SALDOOTROS;

        MI_CONDICION := ' NOCONTRATO ='''||MI_RS.NOCONTRATO||'''
                      AND CLASEORDEN ='''||MI_RS.CLASEORDEN||'''';

        MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA      => 'TEMP_F18_CERTCONTRATOS'
                                     ,UN_ACCION    => 'M'
                                     ,UN_CAMPOS    => MI_CAMPOS
                                     ,UN_CONDICION => MI_CONDICION);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

      END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                           PCK_ERR_MSG.RAISE_WITH_MSG(
                           UN_EXC_COD     => SQLCODE
                           ,UN_ERROR_COD  =>PCK_ERRORES.ER_BANCOSP_UPDATE_TF18CERTCONT
                           );
    END;

    END LOOP;

  MI_SEPARADOR := PCK_DATOS.GL_SEPARADOR_COL;

  MI_SEPARADORREGISTRO := PCK_DATOS.GL_SEPARADOR_REG;

  --MI_SEPARADOR := ';.;';
 -- MI_SEPARADORREGISTRO := ',.,';


  MI_STR := '';
  FOR MI_RS IN (SELECT CODIGOBPIM,
                      NOMBREPROYECTO,
                      NOPROCESO,
                      CODIGOS_RUBROS,
                      NOCONTRATO,
                      NOMBRECONTRATISTA,
                      CEDULACONTRATISTA,
                      OBJETO,
                      REGISTROPRESUPUESTAL,
                      PLAZODEENTREGA,
                      SALDODISPONIBLE,
                      SGR,
                      OTRASFUENTES,
                      VALORTOTAL,
                      VALOR_ANTICIPO,
                      VALORDIFERENTEANTICIPO,
                      VALORDELOSPAGOS,
                      INTERVENTOR,
                      CEDULAINTERVENTOR
                    FROM TEMP_F18_CERTCONTRATOS) LOOP



    MI_STR := MI_STR||
              MI_RS.CODIGOBPIM ||MI_SEPARADOR||
              MI_RS.NOMBREPROYECTO||MI_SEPARADOR||
              MI_RS.NOPROCESO||MI_SEPARADOR||
              MI_RS.CODIGOS_RUBROS||MI_SEPARADOR||
              MI_RS.NOCONTRATO||MI_SEPARADOR||
              MI_RS.NOMBRECONTRATISTA||MI_SEPARADOR||
              MI_RS.CEDULACONTRATISTA||MI_SEPARADOR||
              MI_RS.OBJETO||MI_SEPARADOR||
              MI_RS.REGISTROPRESUPUESTAL||MI_SEPARADOR||
              MI_RS.PLAZODEENTREGA||MI_SEPARADOR||
              MI_RS.SALDODISPONIBLE||MI_SEPARADOR||
              MI_RS.SGR||MI_SEPARADOR||
              MI_RS.OTRASFUENTES||MI_SEPARADOR||
              MI_RS.VALORTOTAL||MI_SEPARADOR||
              MI_RS.VALOR_ANTICIPO||MI_SEPARADOR||
              MI_RS.VALORDIFERENTEANTICIPO||MI_SEPARADOR||
              MI_RS.VALORDELOSPAGOS||MI_SEPARADOR||
              MI_RS.INTERVENTOR||MI_SEPARADOR||
              MI_RS.CEDULAINTERVENTOR||MI_SEPARADORREGISTRO;              


  END LOOP;

  RETURN  MI_STR;

END FC_F_SCV_18;

--7
FUNCTION FC_PREPARARDATOS
/*
    NAME              : FC_PREPARARDATOS, NOMBRE EN ACCESS PREPARARDATOS
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO
    DATE MIGRADOR     : 08/10/2015
    TIME              : 12:00 AM
    DESCRIPTION       : PROCEDIMIENTO QUE PERMITE PREPARAR LOS DATOS PARA LA GENERACIÃ?N DEL FORMATO CDC-15
    MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED	    : 31/01/2017
    TIME				      : 4:28 PM
    MODIFICATIONS	    : Se cambió el estándar de codificación y llamados por referencia de las funciones.Se agregó manejo de excepciones
    @Name: prepararDatos
*/
(
  UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_VIGENCIAINICIAL  IN VARCHAR2,
  UN_PROYECTOINICIAL  IN VARCHAR2,
  UN_PROYECTOFINAL    IN VARCHAR2
)
RETURN CLOB
AS
  MI_STRSQL             PCK_SUBTIPOS.TI_STRSQL;
  MI_VIGENCIAINICIAL    VARCHAR2(3000 CHAR);
  MI_RS                 SYS_REFCURSOR;
  MI_VIGENCIAMETA       BP_PLAN_INDICATIVO_METAS.VIGENCIA_META%TYPE;
  MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
  MI_USING              PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE        PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE        PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
  MI_RETURN             PCK_SUBTIPOS.TI_ENTERO;
  MI_VIGENCIAS          VARCHAR2(3000 CHAR);
  MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_VIGENCIAMINIMA     BP_PLAN_INDICATIVO_METAS.VIGENCIA_META%TYPE;
  MI_VIGENCIAMAXIMA     BP_PLAN_INDICATIVO_METAS.VIGENCIA_META%TYPE;
  MI_CONTEO             PCK_SUBTIPOS.TI_ENTERO;
  MI_DOMINIO            PCK_SUBTIPOS.TI_CONDICION;
  MI_RETORNO            CLOB;
  MI_RTA            CLOB;

  MI_ANIO1              PCK_SUBTIPOS.TI_ENTERO;
  MI_ANIO2              PCK_SUBTIPOS.TI_ENTERO;
  MI_ANIO3              PCK_SUBTIPOS.TI_ENTERO;
  MI_ANIO4              PCK_SUBTIPOS.TI_ENTERO;
  MI_TOTAL_PROGRAMADO   PCK_SUBTIPOS.TI_ENTERO;



  RSPROYECTO            BP_PROYECTO_PLAN_INDICATIVO.PROYECTO%TYPE;
  RSINDICADOR           BP_PLAN_INDICATIVO_METAS.ID_PLAN%TYPE;
  RSVIGENCIA            BP_PLAN_INDICATIVO.VIGENCIA_INICIAL%TYPE;
  RSCOMPANIA            BP_PROYECTO_PLAN_INDICATIVO.COMPANIA%TYPE;
  RSEJECUTADA_TOTAL     PCK_SUBTIPOS.TI_ENTERO;
  RSEJECUTADA_PERIODO   PCK_SUBTIPOS.TI_ENTERO;
  RSANORUBRO            PCK_SUBTIPOS.TI_ENTERO;
  RSTOTAL_DIS           PCK_SUBTIPOS.TI_ENTERO;
  RSTOTAL_EGR           PCK_SUBTIPOS.TI_ENTERO;

BEGIN

  --Cambia las comas por punto en los valores decimales
  EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS = ''.,''';

  --Borrar todos los registros existentes en la tabla temporal
  DELETE FROM TEMP_BP_INFORME_CD15;

  IF(UN_VIGENCIAINICIAL = 'TODAS') THEN
    BEGIN
      SELECT LISTAGG(VIGENCIA_META,', ') WITHIN GROUP(ORDER BY VIGENCIA_META)
      INTO  MI_VIGENCIAINICIAL
      FROM (SELECT DISTINCT VIGENCIA_META
            FROM  BP_PLAN_INDICATIVO_METAS
            WHERE COMPANIA      = UN_COMPANIA
              AND VIGENCIA_META <> 0
            ORDER BY VIGENCIA_META) BP_PLAN_INDICATIVO_METAS;

      EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_VIGENCIAINICIAL:='';
    END;
  ELSE
    MI_VIGENCIAINICIAL:=UN_VIGENCIAINICIAL;
  END IF;

  --Actualizar vigencias nulas
      MI_TABLA:='BP_D_NOVEDADPROYECTO';

      MI_USING:=' SELECT VIGENCIA
                         , COMPANIA
                         , TIPOT
                         , CLASET
                         , CODIGO
                         , DEPENDENCIA
                  FROM BPNOVEDADPROYECTO
                  WHERE BPNOVEDADPROYECTO.COMPANIA  ='''||UN_COMPANIA||'''
                    AND BPNOVEDADPROYECTO.VIGENCIA NOT IN (NULL)';

      MI_MERGEENLACE:='     TABLA.COMPANIA    = VISTA.COMPANIA
                        AND TABLA.TIPOT       = VISTA.TIPOT
                        AND TABLA.CLASET      = VISTA.CLASET
                        AND TABLA.NOVEDAD     = VISTA.CODIGO
                        AND TABLA.DEPENDENCIA = VISTA.DEPENDENCIA';

      MI_MERGEEXISTE:='UPDATE SET TABLA.ANORUBRO = VISTA.VIGENCIA WHERE TABLA.ANORUBRO IN (0)';

  BEGIN
    BEGIN
      MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA        => MI_TABLA
                                   ,UN_ACCION      => 'MM'
                                   ,UN_MERGEUSING  => MI_USING
                                   ,UN_MERGEENLACE => MI_MERGEENLACE
                                   ,UN_MERGEEXISTE => MI_MERGEEXISTE);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
               RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

    END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                       MI_MSGERROR(1).CLAVE := 'TABLA';
                       MI_MSGERROR(1).VALOR := MI_TABLA;
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD     => SQLCODE
                                        ,UN_ERROR_COD  => PCK_ERRORES.ER_BANCOSP_MERGE_TABLA
                                        ,UN_REEMPLAZOS => MI_MSGERROR
                                        );

  END;

  BEGIN
    BEGIN
        --Proceso de indicadores
        --1. insertar indicadores

        MI_CAMPOS:=' COMPANIA
                     , PROYECTO
                     , INDICADOR
                     , VIGENCIA
                     , VIGENCIA_PLAN
                     , DESCRIPCION_INDICADOR';

        MI_STRSQL:=' (SELECT  DISTINCT
                              BP_PROYECTO_PLAN_INDICATIVO.COMPANIA,
                              BP_PROYECTO_PLAN_INDICATIVO.PROYECTO,
                              BP_PROYECTO_PLAN_INDICATIVO.ID_PLAN_P,
                              BP_PROYECTO_PLAN_INDICATIVO.VIGENCIA_META_P,
                              BP_PROYECTO_PLAN_INDICATIVO.VIGENCIA_PLAN_P,
                              NVL(BP_PLAN_INDICATIVO.DESCRIPCION_INDICADOR, '''')
                    FROM BP_PROYECTO_PLAN_INDICATIVO
                        INNER JOIN BP_PLAN_INDICATIVO
                                ON BP_PROYECTO_PLAN_INDICATIVO.COMPANIA        = BP_PLAN_INDICATIVO.COMPANIA
                               AND BP_PROYECTO_PLAN_INDICATIVO.ID_PLAN_P       = BP_PLAN_INDICATIVO.ID
                               AND BP_PROYECTO_PLAN_INDICATIVO.VIGENCIA_PLAN_P = BP_PLAN_INDICATIVO.VIGENCIA_INICIAL
                    WHERE BP_PROYECTO_PLAN_INDICATIVO.COMPANIA ='''||UN_COMPANIA||'''
                      AND BP_PROYECTO_PLAN_INDICATIVO.PROYECTO BETWEEN '''||UN_PROYECTOINICIAL||''' AND '''||UN_PROYECTOFINAL||''')';

        MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA    => 'TEMP_BP_INFORME_CD15'
                                     ,UN_ACCION  => 'IS'
                                     ,UN_CAMPOS  => MI_CAMPOS
                                     ,UN_VALORES => MI_STRSQL);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
               RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
    END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD     => SQLCODE
                        ,UN_ERROR_COD  => PCK_ERRORES.ER_BANCOSP_INSERT_TBPINFCD15
                         );


  END;

  FOR MI_RS IN (SELECT BP_PROYECTO_PLAN_INDICATIVO.PROYECTO,
                       BP_PLAN_INDICATIVO_METAS.ID_PLAN,
                       BP_PLAN_INDICATIVO.VIGENCIA_INICIAL,
                       BP_PLAN_INDICATIVO.VIGENCIA_FINAL,
                       SUM(CASE WHEN BP_PLAN_INDICATIVO.VIGENCIA_INICIAL = BP_PLAN_INDICATIVO_METAS.VIGENCIA_META   THEN BP_PLAN_INDICATIVO_METAS.CANTIDAD_PROGRAMADA ELSE 0 END) ANIO1,
                       SUM(CASE WHEN BP_PLAN_INDICATIVO.VIGENCIA_INICIAL = BP_PLAN_INDICATIVO_METAS.VIGENCIA_META-1 THEN BP_PLAN_INDICATIVO_METAS.CANTIDAD_PROGRAMADA ELSE 0 END) ANIO2,
                       SUM(CASE WHEN BP_PLAN_INDICATIVO.VIGENCIA_INICIAL = BP_PLAN_INDICATIVO_METAS.VIGENCIA_META-2 THEN BP_PLAN_INDICATIVO_METAS.CANTIDAD_PROGRAMADA ELSE 0 END) ANIO3,
                       SUM(CASE WHEN BP_PLAN_INDICATIVO.VIGENCIA_INICIAL = BP_PLAN_INDICATIVO_METAS.VIGENCIA_META-3 THEN BP_PLAN_INDICATIVO_METAS.CANTIDAD_PROGRAMADA ELSE 0 END) ANIO4,
                       SUM(BP_PLAN_INDICATIVO_METAS.CANTIDAD_PROGRAMADA) TOTAL_PROGRAMADO
                FROM BP_PROYECTO_PLAN_INDICATIVO
                    INNER JOIN BP_PLAN_INDICATIVO_METAS
                            INNER JOIN BP_PLAN_INDICATIVO
                                    ON BP_PLAN_INDICATIVO_METAS.VIGENCIA_PLAN = BP_PLAN_INDICATIVO.VIGENCIA_INICIAL
                                   AND BP_PLAN_INDICATIVO_METAS.ID_PLAN = BP_PLAN_INDICATIVO.ID
                                   AND BP_PLAN_INDICATIVO_METAS.COMPANIA = BP_PLAN_INDICATIVO.COMPANIA
                                    ON BP_PROYECTO_PLAN_INDICATIVO.VIGENCIA_PLAN_P = BP_PLAN_INDICATIVO_METAS.VIGENCIA_PLAN
                                   AND BP_PROYECTO_PLAN_INDICATIVO.VIGENCIA_META_P = BP_PLAN_INDICATIVO_METAS.VIGENCIA_META
                                   AND BP_PROYECTO_PLAN_INDICATIVO.ID_PLAN_P = BP_PLAN_INDICATIVO_METAS.ID_PLAN
                                   AND BP_PROYECTO_PLAN_INDICATIVO.COMPANIA = BP_PLAN_INDICATIVO_METAS.COMPANIA
                WHERE BP_PROYECTO_PLAN_INDICATIVO.COMPANIA = UN_COMPANIA
                  AND BP_PROYECTO_PLAN_INDICATIVO.PROYECTO BETWEEN UN_PROYECTOINICIAL AND UN_PROYECTOFINAL
                GROUP BY BP_PROYECTO_PLAN_INDICATIVO.PROYECTO,
                         BP_PLAN_INDICATIVO_METAS.ID_PLAN,
                         BP_PLAN_INDICATIVO.VIGENCIA_INICIAL,
                         BP_PLAN_INDICATIVO.VIGENCIA_FINAL)
    LOOP

     BEGIN
       BEGIN
        MI_CAMPOS := ' META_PROGRAMADA_ANO1= '||MI_RS.ANIO1||'
                       , META_PROGRAMADA_ANO2='||MI_RS.ANIO2||'
                       , META_PROGRAMADA_ANO3='||MI_RS.ANIO3||'
                       , META_PROGRAMADA_ANO4='||MI_RS.ANIO4||'
                       , META_TOTALPROGRAMADA='||MI_RS.TOTAL_PROGRAMADO;

        MI_CONDICION := ' COMPANIA= '''||UN_COMPANIA||'''
                      AND PROYECTO='''||MI_RS.PROYECTO||'''
                      AND INDICADOR='''||MI_RS.ID_PLAN||'''';

        MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA      => 'TEMP_BP_INFORME_CD15'
                                     ,UN_ACCION    => 'M'
                                     ,UN_CAMPOS    => MI_CAMPOS
                                     ,UN_CONDICION => MI_CONDICION);

       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
               RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

       END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD     => SQLCODE
                        ,UN_ERROR_COD  => PCK_ERRORES.ER_BANCOSP_UPDATE_TBPINFCD15
                         );
     END;
    END LOOP;

  -- Calcular metas totales
  MI_STRSQL:='SELECT  BP_PROYECTO_PLAN_INDICATIVO.COMPANIA,
                      BP_PROYECTO_PLAN_INDICATIVO.PROYECTO,
                      BP_PROYECTO_PLAN_INDICATIVO.ID_PLAN_P,
                      NVL(SUM(BP_PLAN_INDICATIVO_METAS.CANTIDAD_EJECUTADA),0) EJECUTADA_TOTAL,
                      NVL(SUM(CASE WHEN VIGENCIA_META IN ('||MI_VIGENCIAINICIAL||') THEN CANTIDAD_EJECUTADA ELSE 0 END ),0) EJECUTADA_PERIODO
            FROM  BP_PROYECTO_PLAN_INDICATIVO
              INNER JOIN BP_PLAN_INDICATIVO_METAS
                      ON BP_PROYECTO_PLAN_INDICATIVO.COMPANIA        = BP_PLAN_INDICATIVO_METAS.COMPANIA
                     AND BP_PROYECTO_PLAN_INDICATIVO.ID_PLAN_P       = BP_PLAN_INDICATIVO_METAS.ID_PLAN
                     AND BP_PROYECTO_PLAN_INDICATIVO.VIGENCIA_PLAN_P = BP_PLAN_INDICATIVO_METAS.VIGENCIA_PLAN
            WHERE BP_PROYECTO_PLAN_INDICATIVO.COMPANIA = '''||UN_COMPANIA||'''
              AND BP_PROYECTO_PLAN_INDICATIVO.PROYECTO BETWEEN '''||UN_PROYECTOINICIAL||''' AND '''||UN_PROYECTOFINAL||'''
            GROUP BY BP_PROYECTO_PLAN_INDICATIVO.COMPANIA
                     , BP_PROYECTO_PLAN_INDICATIVO.PROYECTO
                     , BP_PROYECTO_PLAN_INDICATIVO.ID_PLAN_P';

  OPEN MI_RS FOR MI_STRSQL;
    LOOP
      BEGIN
       BEGIN
        FETCH MI_RS INTO RSCOMPANIA, RSPROYECTO, RSINDICADOR, RSEJECUTADA_TOTAL, RSEJECUTADA_PERIODO;
        EXIT WHEN MI_RS%NOTFOUND;

          MI_CAMPOS := ' META_TOTAL_CUMPLIDA_ACTUAL     = '||RSEJECUTADA_PERIODO||',
                         META_TOTAL_ACUMULADA_EJECUTADA ='||RSEJECUTADA_TOTAL||',
                         PORC_CUMPLIMIENTO_IND          = CASE WHEN '||RSEJECUTADA_TOTAL||'= 0 THEN 0 ELSE META_TOTALPROGRAMADA/'||RSEJECUTADA_TOTAL||' END';

          MI_CONDICION := ' COMPANIA  ='''||RSCOMPANIA||'''
                        AND PROYECTO  = '''||RSPROYECTO||'''
                        AND INDICADOR ='''||RSINDICADOR||'''';

          MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA      => 'TEMP_BP_INFORME_CD15'
                                       ,UN_ACCION    => 'M'
                                       ,UN_CAMPOS    => MI_CAMPOS
                                       ,UN_CONDICION => MI_CONDICION);

             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

       END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                            PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD     => SQLCODE
                            ,UN_ERROR_COD  => PCK_ERRORES.ER_BANCOSP_UPDATE_TBPINFCD15
                             );
      END;
    END LOOP;
  CLOSE MI_RS;

  --Proceso de ejecución
  --Calcular el ejecutado comprometido: sumatoria de los tipos de solicitud
  MI_STRSQL:='SELECT BP_D_NOVEDADPROYECTO.COMPANIA,
                     BP_D_NOVEDADPROYECTO.PROYECTO,
                     BP_D_NOVEDADPROYECTO.ANORUBRO,
                     SUM(NVL(VALOR_DEBITO,0)-NVL(VALOR_CREDITO,0)) TOTAL_DIS
                FROM BP_D_NOVEDADPROYECTO
                  INNER JOIN DETALLE_COMPROBANTE_PPTAL
                          ON BP_D_NOVEDADPROYECTO.CODIGO   = DETALLE_COMPROBANTE_PPTAL.CONSECUTIVOPPTO
                         AND BP_D_NOVEDADPROYECTO.NOVEDAD  = DETALLE_COMPROBANTE_PPTAL.CMPTE_AFECTADO
                         AND BP_D_NOVEDADPROYECTO.TIPOT    = DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE_AFECT
                         AND BP_D_NOVEDADPROYECTO.ANORUBRO = DETALLE_COMPROBANTE_PPTAL.ANO
                         AND BP_D_NOVEDADPROYECTO.COMPANIA = DETALLE_COMPROBANTE_PPTAL.COMPANIA
                        INNER JOIN BPTIPONOVEDAD
                                ON BP_D_NOVEDADPROYECTO.CLASET   = BPTIPONOVEDAD.CLASET
                               AND BP_D_NOVEDADPROYECTO.TIPOT    = BPTIPONOVEDAD.TIPOT
                               AND BP_D_NOVEDADPROYECTO.COMPANIA = BPTIPONOVEDAD.COMPANIA
                WHERE  BP_D_NOVEDADPROYECTO.COMPANIA='''||UN_COMPANIA||'''
                    AND BPTIPONOVEDAD.CLASENOVEDAD IN (''S'')
                    AND BP_D_NOVEDADPROYECTO.PROYECTO BETWEEN '''||UN_PROYECTOINICIAL||''' AND '''||UN_PROYECTOFINAL||'''
                    AND BP_D_NOVEDADPROYECTO.ANORUBRO IN ('||MI_VIGENCIAINICIAL||')
                GROUP BY BP_D_NOVEDADPROYECTO.COMPANIA
                         , BPTIPONOVEDAD.CLASENOVEDAD
                         , BP_D_NOVEDADPROYECTO.PROYECTO
                         , BP_D_NOVEDADPROYECTO.ANORUBRO';

  OPEN MI_RS FOR MI_STRSQL;
      LOOP
        FETCH MI_RS INTO RSCOMPANIA, RSPROYECTO, RSANORUBRO, RSTOTAL_DIS;
        EXIT WHEN MI_RS%NOTFOUND;

         BEGIN
          BEGIN
            MI_CAMPOS := ' EJECUTADO_COMPROMETIDO='||RSTOTAL_DIS;

            MI_CONDICION := ' COMPANIA = '''||RSCOMPANIA||'''
                          AND PROYECTO = '''||RSPROYECTO||'''
                          AND VIGENCIA = '||RSANORUBRO;

            MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA => 'TEMP_BP_INFORME_CD15'
                                         ,UN_ACCION => 'M'
                                         ,UN_CAMPOS => MI_CAMPOS
                                         ,UN_CONDICION => MI_CONDICION);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

          END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                            PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD     => SQLCODE
                            ,UN_ERROR_COD  => PCK_ERRORES.ER_BANCOSP_UPDATE_TBPINFCD15
                             );
         END;
      END LOOP;
  CLOSE MI_RS;

  --Calcular los totales de los tipo egreso
  MI_STRSQL:='SELECT BP_D_NOVEDADPROYECTO.COMPANIA,
                     BP_D_NOVEDADPROYECTO.PROYECTO,
                     BP_D_NOVEDADPROYECTO.ANORUBRO,
                     SUM(NVL(DETALLE_COMPROBANTE_PPTAL_3.VALOR_DEBITO,0)-NVL(DETALLE_COMPROBANTE_PPTAL_3.VALOR_CREDITO,0)) TOTAL_EGR
              FROM BP_D_NOVEDADPROYECTO
                	INNER JOIN DETALLE_COMPROBANTE_PPTAL
                	        ON BP_D_NOVEDADPROYECTO.COMPANIA = DETALLE_COMPROBANTE_PPTAL.COMPANIA
                    	   AND BP_D_NOVEDADPROYECTO.ANORUBRO = DETALLE_COMPROBANTE_PPTAL.ANO
                         AND BP_D_NOVEDADPROYECTO.TIPOT    = DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE_AFECT
                         AND BP_D_NOVEDADPROYECTO.NOVEDAD  = DETALLE_COMPROBANTE_PPTAL.CMPTE_AFECTADO
                         AND BP_D_NOVEDADPROYECTO.CODIGO   = DETALLE_COMPROBANTE_PPTAL.CONSECUTIVOPPTO
                     INNER JOIN BPTIPONOVEDAD
                             ON BP_D_NOVEDADPROYECTO.COMPANIA = BPTIPONOVEDAD.COMPANIA
                    		    AND BP_D_NOVEDADPROYECTO.TIPOT    = BPTIPONOVEDAD.TIPOT
                    		    AND BP_D_NOVEDADPROYECTO.CLASET   = BPTIPONOVEDAD.CLASET
                  			INNER JOIN DETALLE_COMPROBANTE_PPTAL DETALLE_COMPROBANTE_PPTAL_1
                  					    ON DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO = DETALLE_COMPROBANTE_PPTAL_1.CONSECUTIVOPPTO
                    			     AND DETALLE_COMPROBANTE_PPTAL.COMPANIA    = DETALLE_COMPROBANTE_PPTAL_1.COMPANIA
                               AND DETALLE_COMPROBANTE_PPTAL.ANO         = DETALLE_COMPROBANTE_PPTAL_1.ANO
                    			     AND DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE   = DETALLE_COMPROBANTE_PPTAL_1.TIPO_CPTE_AFECT
                    			     AND DETALLE_COMPROBANTE_PPTAL.COMPROBANTE = DETALLE_COMPROBANTE_PPTAL_1.CMPTE_AFECTADO
                  				INNER JOIN DETALLE_COMPROBANTE_PPTAL DETALLE_COMPROBANTE_PPTAL_2
                                  ON DETALLE_COMPROBANTE_PPTAL_1.COMPANIA    = DETALLE_COMPROBANTE_PPTAL_2.COMPANIA
                                 AND DETALLE_COMPROBANTE_PPTAL_1.ANO         = DETALLE_COMPROBANTE_PPTAL_2.ANO
                                 AND DETALLE_COMPROBANTE_PPTAL_1.TIPO_CPTE   = DETALLE_COMPROBANTE_PPTAL_2.TIPO_CPTE_AFECT
                                 AND DETALLE_COMPROBANTE_PPTAL_1.COMPROBANTE = DETALLE_COMPROBANTE_PPTAL_2.CMPTE_AFECTADO
                                 AND DETALLE_COMPROBANTE_PPTAL_1.CONSECUTIVO = DETALLE_COMPROBANTE_PPTAL_2.CONSECUTIVOPPTO
                  					INNER JOIN DETALLE_COMPROBANTE_PPTAL DETALLE_COMPROBANTE_PPTAL_3
                                    ON DETALLE_COMPROBANTE_PPTAL_2.COMPANIA    = DETALLE_COMPROBANTE_PPTAL_3.COMPANIA
                                   AND DETALLE_COMPROBANTE_PPTAL_2.ANO         = DETALLE_COMPROBANTE_PPTAL_3.ANO
                                   AND DETALLE_COMPROBANTE_PPTAL_2.TIPO_CPTE   = DETALLE_COMPROBANTE_PPTAL_3.TIPO_CPTE_AFECT
                                   AND DETALLE_COMPROBANTE_PPTAL_2.COMPROBANTE = DETALLE_COMPROBANTE_PPTAL_3.CMPTE_AFECTADO
                                   AND DETALLE_COMPROBANTE_PPTAL_2.CONSECUTIVO = DETALLE_COMPROBANTE_PPTAL_3.CONSECUTIVOPPTO
                  						INNER JOIN TIPO_COMPROBPP
                                      ON DETALLE_COMPROBANTE_PPTAL_3.COMPANIA  = TIPO_COMPROBPP.COMPANIA
                    					       AND DETALLE_COMPROBANTE_PPTAL_3.TIPO_CPTE = TIPO_COMPROBPP.CODIGO
              WHERE BP_D_NOVEDADPROYECTO.COMPANIA = '''||UN_COMPANIA||'''
                AND BPTIPONOVEDAD.CLASENOVEDAD    IN (''S'')
                AND BP_D_NOVEDADPROYECTO.PROYECTO BETWEEN '''||UN_PROYECTOINICIAL||''' AND '''||UN_PROYECTOFINAL||'''
                AND BP_D_NOVEDADPROYECTO.ANORUBRO IN ('||MI_VIGENCIAINICIAL||')
                AND TIPO_COMPROBPP.CLASE          IN (''EGR'')
                GROUP BY BP_D_NOVEDADPROYECTO.COMPANIA
                      , BP_D_NOVEDADPROYECTO.PROYECTO
                      , BP_D_NOVEDADPROYECTO.ANORUBRO
                      , TIPO_COMPROBPP.CLASE';

  OPEN MI_RS FOR MI_STRSQL;
      LOOP
      FETCH MI_RS INTO RSCOMPANIA, RSPROYECTO, RSANORUBRO, RSTOTAL_EGR;
      EXIT WHEN MI_RS%NOTFOUND;

        BEGIN
         BEGIN
          MI_CAMPOS := ' EJECUTADO_PAGADO='||RSTOTAL_EGR;

          MI_CONDICION := 'COMPANIA = '''||RSCOMPANIA||'''
                       AND PROYECTO = '''||RSPROYECTO||'''
                       AND VIGENCIA = '||RSANORUBRO;

          MI_RETURN:=PCK_DATOS.FC_ACME(UN_TABLA      => 'TEMP_BP_INFORME_CD15'
                                       ,UN_ACCION    => 'M'
                                       ,UN_CAMPOS    => MI_CAMPOS
                                       ,UN_CONDICION => MI_CONDICION);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

          END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                            PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD     => SQLCODE
                            ,UN_ERROR_COD  => PCK_ERRORES.ER_BANCOSP_UPDATE_TBPINFCD15
                             );
        END;
      END LOOP;
  CLOSE MI_RS;

  MI_CONTEO:=0;
  MI_RTA:='';
  IF UN_VIGENCIAINICIAL = 'TODAS' THEN
      FOR RS IN ( SELECT DISTINCT VIGENCIA_META
                  FROM BP_PLAN_INDICATIVO_METAS
                  WHERE COMPANIA      =   UN_COMPANIA
                    AND VIGENCIA_META <>  0
                  ORDER BY VIGENCIA_META)
      LOOP
        IF MI_CONTEO = 0 THEN
          MI_DOMINIO := RS.VIGENCIA_META; 
          MI_VIGENCIAMINIMA := RS.VIGENCIA_META;
          MI_CONTEO:=1;
        ELSE
          MI_DOMINIO := MI_DOMINIO ||  ',' || RS.VIGENCIA_META;  
        END IF;
        MI_VIGENCIAMAXIMA :=  RS.VIGENCIA_META;
      END LOOP;
      MI_RTA :=  'AND TEMP_BP_INFORME_CD15.VIGENCIA IN ('||MI_DOMINIO||') AND ( '|| MI_VIGENCIAMINIMA ||' >= VIGENCIAINICIO 
                  AND '||MI_VIGENCIAMAXIMA||' <= PROYECTOS.VIGENCIAFIN 
                  OR PROYECTOS.VIGENCIAINICIO IN ('||MI_DOMINIO||'))';
  ELSE
    MI_RTA := 'AND TEMP_BP_INFORME_CD15.VIGENCIA IN ('||UN_VIGENCIAINICIAL||') AND ( '||UN_VIGENCIAINICIAL||' >= VIGENCIAINICIO 
                AND '||UN_VIGENCIAINICIAL||' <= PROYECTOS.VIGENCIAFIN 
                OR '||UN_VIGENCIAINICIAL||' IN PROYECTOS.VIGENCIAINICIO)';
  END IF;

   MI_STRSQL := 'WITH PLAN_PRESUPUESTAL_TMP AS ( 
                SELECT BP_PROYECTOSRUBROS.COMPANIA,  
                  BP_PROYECTOSRUBROS.PROYECTO, 
                  MAX(PLAN_PRESUPUESTAL.NOMBRE) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) NOMBREDIMENSION, 
                  MAX(PLAN_PRESUPUESTAL_1.NOMBRE) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) NOMBRESECTOR, 
                  MAX(PLAN_PRESUPUESTAL_2.NOMBRE) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) NOMBREPROGRAMA, 
                  MAX(PLAN_PRESUPUESTAL_3.NOMBRE) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) NOMBRESUBPROGRAMA 
                FROM BP_PROYECTOSRUBROS  
                  LEFT JOIN V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL ON  
                      BP_PROYECTOSRUBROS.COMPANIA         = PLAN_PRESUPUESTAL.COMPANIA  
                    AND BP_PROYECTOSRUBROS.DIMENSION      = PLAN_PRESUPUESTAL.ID  
                  LEFT JOIN V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL_1 ON  
                      BP_PROYECTOSRUBROS.COMPANIA         = PLAN_PRESUPUESTAL_1.COMPANIA 
                    AND BP_PROYECTOSRUBROS.SECTOR         = PLAN_PRESUPUESTAL_1.ID 
                  LEFT JOIN V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL_2 ON  
                      BP_PROYECTOSRUBROS.COMPANIA         = PLAN_PRESUPUESTAL_2.COMPANIA  
                    AND BP_PROYECTOSRUBROS.PROGRAMA       = PLAN_PRESUPUESTAL_2.ID 
                  LEFT JOIN V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL_3 ON  
                      BP_PROYECTOSRUBROS.COMPANIA         = PLAN_PRESUPUESTAL_3.COMPANIA 
                    AND BP_PROYECTOSRUBROS.SUBPROGRAMA    = PLAN_PRESUPUESTAL_3.ID  
                WHERE BP_PROYECTOSRUBROS.COMPANIA			 = '''||UN_COMPANIA ||''' 
                  AND BP_PROYECTOSRUBROS.PROYECTO BETWEEN '''||UN_PROYECTOINICIAL||''' AND '''||UN_PROYECTOFINAL||'''
                GROUP BY BP_PROYECTOSRUBROS.COMPANIA, BP_PROYECTOSRUBROS.PROYECTO)  
                 SELECT 
                 PROYECTOS.CODIGOBPIM || '''|| PCK_DATOS.GL_SEPARADOR_COL || ''' ||  
                 PROYECTOS.NOMBREPROYECTO || '''|| PCK_DATOS.GL_SEPARADOR_COL || ''' || 
                 NVL(PROYECTOS.VALORTOTAL,0) || '''|| PCK_DATOS.GL_SEPARADOR_COL || ''' || 
                 TEMP_BP_INFORME_CD15.EJECUTADO_COMPROMETIDO || '''|| PCK_DATOS.GL_SEPARADOR_COL || ''' || 
                 TEMP_BP_INFORME_CD15.EJECUTADO_PAGADO || '''|| PCK_DATOS.GL_SEPARADOR_COL || ''' || 
                 TEMP_BP_INFORME_CD15.META_PROGRAMADA_ANO1 || '''|| PCK_DATOS.GL_SEPARADOR_COL || ''' ||  
                 TEMP_BP_INFORME_CD15.META_PROGRAMADA_ANO2 || '''|| PCK_DATOS.GL_SEPARADOR_COL || ''' ||  
                 TEMP_BP_INFORME_CD15.META_PROGRAMADA_ANO3 || '''|| PCK_DATOS.GL_SEPARADOR_COL || ''' ||  
                 TEMP_BP_INFORME_CD15.META_PROGRAMADA_ANO4 || '''|| PCK_DATOS.GL_SEPARADOR_COL || ''' ||  
                 TEMP_BP_INFORME_CD15.META_TOTALPROGRAMADA || '''|| PCK_DATOS.GL_SEPARADOR_COL || ''' ||
                 TEMP_BP_INFORME_CD15.META_TOTAL_CUMPLIDA_ACTUAL || '''|| PCK_DATOS.GL_SEPARADOR_COL || ''' || 
                 TEMP_BP_INFORME_CD15.META_TOTAL_ACUMULADA_EJECUTADA || '''|| PCK_DATOS.GL_SEPARADOR_COL || ''' || 
                 TEMP_BP_INFORME_CD15.DESCRIPCION_INDICADOR || '''|| PCK_DATOS.GL_SEPARADOR_COL || ''' || 
                 TEMP_BP_INFORME_CD15.PORC_CUMPLIMIENTO_IND || '''|| PCK_DATOS.GL_SEPARADOR_COL || ''' || 
                 CASE WHEN '''||UN_VIGENCIAINICIAL||''' = ''TODAS'' THEN '''||MI_VIGENCIAMINIMA||' a '|| MI_VIGENCIAMAXIMA||''' ELSE '''||UN_VIGENCIAINICIAL ||''' END || '''|| PCK_DATOS.GL_SEPARADOR_COL || ''' ||  
                 PLAN_PRESUPUESTAL_TMP.NOMBRESECTOR  || '''|| PCK_DATOS.GL_SEPARADOR_COL || ''' || 
                 PLAN_PRESUPUESTAL_TMP.NOMBREPROGRAMA  || '''|| PCK_DATOS.GL_SEPARADOR_COL || ''' || 
                 PLAN_PRESUPUESTAL_TMP.NOMBRESUBPROGRAMA  AS FILA
                 FROM PROYECTOS  
                  LEFT JOIN RESPONSABLE ON  
                      PROYECTOS.COMPANIA            = RESPONSABLE.COMPANIA 
                    AND PROYECTOS.RESPONSABLE       = RESPONSABLE.CEDULA 
                    AND PROYECTOS.SUCURSAL          = RESPONSABLE.SUCURSAL 
                  LEFT JOIN PLAN_PRESUPUESTAL_TMP ON  
                      PROYECTOS.COMPANIA            = PLAN_PRESUPUESTAL_TMP.COMPANIA 
                    AND PROYECTOS.CODIGO            = PLAN_PRESUPUESTAL_TMP.PROYECTO 
                  LEFT JOIN DEPENDENCIA ON  
                      PROYECTOS.DEPENDENCIA         = DEPENDENCIA.CODIGO 
                    AND PROYECTOS.COMPANIA          = DEPENDENCIA.COMPANIA 
                  INNER JOIN TEMP_BP_INFORME_CD15 ON  
                      PROYECTOS.COMPANIA            = TEMP_BP_INFORME_CD15.COMPANIA 
                    AND PROYECTOS.CODIGO            = TEMP_BP_INFORME_CD15.PROYECTO 
                  INNER JOIN TERCERO ON 
                      RESPONSABLE.COMPANIA          = TERCERO.COMPANIA 
                    AND RESPONSABLE.CEDULA          = TERCERO.NIT 
                    AND RESPONSABLE.SUCURSAL        = TERCERO.SUCURSAL 
                 WHERE  PROYECTOS.COMPANIA          = '''||UN_COMPANIA||''' 
                  AND PROYECTOS.CODIGO BETWEEN '''||UN_PROYECTOINICIAL||''' AND '''||UN_PROYECTOFINAL||''' 
                  ' || MI_RTA;

    MI_RTA:='';
    OPEN MI_RS FOR MI_STRSQL;
    LOOP
    FETCH MI_RS INTO MI_RTA;
    EXIT WHEN MI_RS%NOTFOUND;
      MI_RETORNO := TO_CLOB(MI_RETORNO || MI_RTA  || PCK_DATOS.GL_SEPARADOR_REG) ;
    END LOOP;
    CLOSE MI_RS;
  RETURN MI_RETORNO;
END FC_PREPARARDATOS;

--8
FUNCTION FC_NOMBREPERIODO_BANC
/*
    NAME              : FC_NOMBREPERIODO_BANC
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO
    DATE MIGRADOR     : 29/10/2015
    TIME              : 05:00 PM
    DESCRIPTION       : FUNCIÓN QUE CALCULA EL NOMBRE DEL PERIODO POR MEDIO DE LA PERIOCIDAD Y EL PERIODO, PARA EL MÓDULO BANCO DE PROYECTOS
    MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED	    : 01/02/2017
    TIME				      : 08:20 AM
    MODIFICATIONS	    : Se cambió el estándar de codificación y llamados por referencia de las funciones.
  @Name: nombrePeriodicidadBancoProy
*/
(
  UN_PERIOCIDAD       IN PCK_SUBTIPOS.TI_ENTERO,
  UN_PERIODO          IN PCK_SUBTIPOS.TI_ENTERO
) RETURN VARCHAR2
AS
  MI_PERIODO1         PCK_SUBTIPOS.TI_ENTERO;
  MI_PERIODO2         PCK_SUBTIPOS.TI_ENTERO;
  MI_NOMBREPERIODO    VARCHAR2(3000 CHAR);
  BEGIN
  IF (UN_PERIOCIDAD =12) THEN
    MI_NOMBREPERIODO:=PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => UN_PERIODO);
    RETURN MI_NOMBREPERIODO;
  END IF;

  MI_PERIODO2:=UN_PERIODO*(12/UN_PERIOCIDAD);
  MI_PERIODO1:=MI_PERIODO2 - ((12/UN_PERIOCIDAD)-1);

  MI_NOMBREPERIODO:=PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => MI_PERIODO1)||' - '||PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => MI_PERIODO2);

  RETURN MI_NOMBREPERIODO;

END FC_NOMBREPERIODO_BANC;

END PCK_BANCOS_PROY3;