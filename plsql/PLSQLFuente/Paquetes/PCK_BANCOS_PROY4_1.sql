create or replace PACKAGE BODY "PCK_BANCOS_PROY4" AS

--01

FUNCTION FC_CREARCCADENASUMATORIA
/*
    NAME              : FC_CREARCCADENASUMATORIA  --> EN ACCESS crearcCadenaSumatoria
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : NICOLAS GOMEZ BARBOSA
    DATE MIGRADOR     : 28/08/2015
    TIME              : 09:00 AM
    SOURCE MODULE     : BANCO_PROYECTOS_PROCESOS
    DESCRIPTION       : Crea cadena sumatoria
    @Name: crearCadenaSumatoria
  */
  (
  UN_PERIOCIDADRTA  IN  VARCHAR2,
  UN_PERIOCIDAD     IN  VARCHAR2,
  UN_STRCAMPO       IN  VARCHAR2
  )
  RETURN VARCHAR2
  AS

  MI_I               NUMBER;
  MI_STRRTA          VARCHAR2(3200 CHAR);


  BEGIN

    BEGIN
      IF UN_PERIOCIDADRTA IS NULL OR UN_PERIOCIDAD IS NULL OR UN_STRCAMPO IS NULL THEN
            RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
      END IF;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                 UN_ERROR_COD  => PCK_ERRORES.ER_BANCO_CREATE_CADENA_SUM);
      END;

    MI_STRRTA := SUBSTR(MI_STRRTA, 1, LENGTH(MI_STRRTA) - 1);

    FOR MI_I IN TO_NUMBER(UN_PERIOCIDADRTA)..TO_NUMBER(UN_PERIOCIDAD) LOOP

        IF MI_I = TO_NUMBER(UN_PERIOCIDADRTA) THEN 
          MI_STRRTA := ' '|| UN_STRCAMPO || MI_I;
        ELSE  
          MI_STRRTA := ' '|| UN_STRCAMPO || MI_I || ' +' || MI_STRRTA;
        END IF;
    END LOOP;

  RETURN MI_STRRTA;


END FC_CREARCCADENASUMATORIA;


--02

FUNCTION FC_CAMBIARPERIODICIDAD
  /*
    NAME              : PR_CAMBIARPERIODICIDAD
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : NICOLAS GOMEZ BARBOSA
    DATE MIGRADOR     : 27/08/2015
    TIME              : 04:00 PM
    SOURCE MODULE     : BANCO_PROYECTOS_PROCESOS
    DESCRIPTION       : Cambia la periodicidad de un proyecto
    @Name: getCambiarPeriodicidad
  */
  (
  UN_COMPANIA         IN  PCK_SUBTIPOS.TI_COMPANIA,
  UN_PROYECTOINICIAL  IN  VARCHAR2,
  UN_PERIOCIDAD       IN  VARCHAR2,
  UN_PERIOCIDADAUX    IN  VARCHAR2,
  UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
  )
  RETURN VARCHAR2
  AS

    MI_STRSUMATORIA          VARCHAR2(3200 CHAR):='';
    MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;
    MI_STRSUMATORIAPORCETAJE VARCHAR2(3200 CHAR):='';
    MI_MSGERROR              PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN

    IF TO_NUMBER(UN_PERIOCIDADAUX)>TO_NUMBER(UN_PERIOCIDAD) THEN
    --haccer suamtoria y actualizar programacion
      MI_STRSUMATORIA:=PCK_BANCOS_PROY4.FC_CREARCCADENASUMATORIA(UN_PERIOCIDADRTA => UN_PERIOCIDAD,
                                                                 UN_PERIOCIDAD    => UN_PERIOCIDADAUX,
                                                                 UN_STRCAMPO      => 'Valor');

      MI_STRSUMATORIA:=',Valor' || TO_NUMBER(UN_PERIOCIDAD) || '= ' || MI_STRSUMATORIA;

      MI_STRSUMATORIA:=MI_STRSUMATORIA || CHR(13) || CHR(10) || PCK_BANCOS_PROY4.FC_CREARCCADENAVALORCERO(UN_PERIOCIDADRTA => UN_PERIOCIDAD,
                                                                                                          UN_PERIOCIDAD    => UN_PERIOCIDADAUX,
                                                                                                          UN_STRCAMPO      => 'Valor');

    --PORCENTAJE
      MI_STRSUMATORIAPORCETAJE:= PCK_BANCOS_PROY4.FC_CREARCCADENASUMATORIA(UN_PERIOCIDADRTA => UN_PERIOCIDAD,
                                                                           UN_PERIOCIDAD    => UN_PERIOCIDADAUX,
                                                                           UN_STRCAMPO      => 'PORCENTAJE');

      MI_STRSUMATORIAPORCETAJE:= ',PORCENTAJE' || TO_NUMBER(UN_PERIOCIDAD) || '= ' || MI_STRSUMATORIAPORCETAJE;

      MI_STRSUMATORIAPORCETAJE:= MI_STRSUMATORIAPORCETAJE || CHR(13) || CHR(10) || PCK_BANCOS_PROY4.FC_CREARCCADENAVALORCERO(UN_PERIOCIDADRTA => UN_PERIOCIDAD,
                                                                                                                             UN_PERIOCIDAD    => UN_PERIOCIDADAUX,
                                                                                                                             UN_STRCAMPO      => 'PORCENTAJE');

    END IF;
      BEGIN
        BEGIN

            MI_CAMPOS    := 'PROYECTOS.PERIOCIDAD          = '''|| UN_PERIOCIDAD||''',
                             PROYECTOS.PERIOCIDAD_ANTERIOR = '''|| UN_PERIOCIDADAUX||''',
                             DATE_MODIFIED = SYSDATE,
                             MODIFIED_BY = '''||UN_USUARIO||'''';

            MI_CONDICION :='    PROYECTOS.COMPANIA = '''||UN_COMPANIA||'''
                            AND PROYECTOS.CODIGO   = '''|| UN_PROYECTOINICIAL||'''';

            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'PROYECTOS',
                                                   UN_ACCION    => 'M',
                                                   UN_CAMPOS    => MI_CAMPOS,
                                                   UN_CONDICION => MI_CONDICION);
           MI_MSGERROR(1).CLAVE := 'PERIOCIDAD';
           MI_MSGERROR(1).VALOR := UN_PERIOCIDAD;

           MI_MSGERROR(2).CLAVE := 'TABLA';
           MI_MSGERROR(2).VALOR := 'PROYECTOS';

           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
           RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
        END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ER_BANCOSP_ACTUAL_PERIOCIDAD,
                                   UN_REEMPLAZOS => MI_MSGERROR);
      END;

      BEGIN
        BEGIN
          MI_CAMPOS    := ' PROGRAMACION.PERIOCIDAD            = '''|| UN_PERIOCIDAD||''',
                            PROGRAMACION.PERIOCIDAD_ANTERIOR_P = '''|| UN_PERIOCIDADAUX||''''|| MI_STRSUMATORIA||''|| MI_STRSUMATORIAPORCETAJE||',
                            DATE_MODIFIED = SYSDATE,
                            MODIFIED_BY = '''||UN_USUARIO||'''';

          MI_CONDICION := '   PROGRAMACION.COMPANIA       = '''||UN_COMPANIA||'''
                          AND PROGRAMACION.CODIGOPROYECTO = '''|| UN_PROYECTOINICIAL||'''';

          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'PROGRAMACION',
                                                 UN_ACCION    => 'M',
                                                 UN_CAMPOS    => MI_CAMPOS,
                                                 UN_CONDICION => MI_CONDICION);

           MI_MSGERROR(1).CLAVE := 'PERIOCIDAD';
           MI_MSGERROR(1).VALOR := UN_PERIOCIDAD;

           MI_MSGERROR(2).CLAVE := 'TABLA';
           MI_MSGERROR(2).VALOR := 'PROGRAMACION';

           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
           RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
        END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ER_BANCOSP_ACTUAL_PERIOCIDAD,
                                   UN_REEMPLAZOS => MI_MSGERROR);
      END;

    RETURN UN_PERIOCIDAD;


END FC_CAMBIARPERIODICIDAD;

--03

FUNCTION FC_CREARCCADENAVALORCERO
    /*
    NAME              : FC_CREARCCADENAVALORCERO --> EN ACCESS crearcCadenaValorCero
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : NICOLAS GOMEZ BARBOSA
    DATE MIGRADOR     : 27/08/2015
    TIME              : 11:00 AM
    SOURCE MODULE     : BANCO_PROYECTOS_PROCESOS
    DESCRIPTION       : Crea cadena valor cero
    @Name: armarSumatoria
  */
  (
  UN_PERIOCIDADRTA IN   VARCHAR2,
  UN_PERIOCIDAD    IN   VARCHAR2,
  UN_STRCAMPO      IN   VARCHAR2
  )
  RETURN VARCHAR2
  AS
    MI_I              PCK_SUBTIPOS.TI_ENTERO;
    MI_STRRTA         VARCHAR2(3200 CHAR);

  BEGIN

   BEGIN
      IF UN_PERIOCIDADRTA IS NULL OR UN_PERIOCIDAD IS NULL OR UN_STRCAMPO IS NULL THEN
            RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
      END IF;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                 UN_ERROR_COD  => PCK_ERRORES.ER_BANCO_CREAT_CADENA_V_0);
      END;

    FOR MI_I IN (TO_NUMBER(UN_PERIOCIDADRTA)+1)..TO_NUMBER(UN_PERIOCIDAD) LOOP

      MI_STRRTA := ' ,' || UN_STRCAMPO || MI_I || '=0' || MI_STRRTA;

    END LOOP;

  RETURN MI_STRRTA;

END FC_CREARCCADENAVALORCERO;

--04
 FUNCTION FC_MAYORIZAR_POND
  /*
    NAME              : PR_MAYORIZAR_POND --> EN ACCESS MAYORIZAR_POND
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : NICOLAS GOMEZ BARBOSA
    DATE MIGRADOR     : 31/08/2015
    TIME              : 11:00 AM
    SOURCE MODULE     : BANCO_PROYECTOS_PROCESOS
    DESCRIPTION       : Toma las Ponderaciones de las Metas Producto y actualiza los niveles mÃ¡s altos.
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA
    DESCRIPTION       : Cambio de procedimiento a función para retornar un CLOB
                        de registros con sus respectivos separadores
    @Name: mayorizarPonderacion
  */
  (
  UN_COMPANIA       IN  PCK_SUBTIPOS.TI_COMPANIA,
  UN_VIGENCIA       IN  PCK_SUBTIPOS.TI_ANIO,
  UN_TEMP           IN  PCK_SUBTIPOS.TI_LOGICO,
  UN_INDICADOR      IN  BP_PLAN_INDICATIVO.ID%TYPE,
  UN_GENERAREPORTE  IN  PCK_SUBTIPOS.TI_LOGICO,
  UN_USUARIO        IN  PCK_SUBTIPOS.TI_USUARIO
  )RETURN CLOB
  AS

  MI_RS              NUMBER;
  MI_VECNIVEL        PCK_SYSMAN_UTL.T_SPLIT;
  MI_MSGERROR        PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_RTA             CLOB;
  MI_SEPARADOR_COL   VARCHAR2(10);
  MI_SEPARADOR_REG   VARCHAR2(10);

  CURSOR C1 IS  SELECT
                       BP_NIVEL_PLAN_IND.DIGITOS
                  FROM
                       BP_NIVEL_PLAN_IND
                 WHERE
                       BP_NIVEL_PLAN_IND.COMPANIA = UN_COMPANIA
                   AND BP_NIVEL_PLAN_IND.VIGENCIA = UN_VIGENCIA
                 ORDER BY
                       BP_NIVEL_PLAN_IND.COMPANIA,
                       BP_NIVEL_PLAN_IND.VIGENCIA,
                       BP_NIVEL_PLAN_IND.DIGITOS ASC,
                       BP_NIVEL_PLAN_IND.DESCRIPCION;

  CURSOR C2 IS  SELECT
                       BP_PLAN_INDICATIVO.COMPANIA,
                       BP_PLAN_INDICATIVO.ID,
                       BP_PLAN_INDICATIVO.VIGENCIA_INICIAL,
                       BP_PLAN_INDICATIVO.PONDERACION
                  FROM
                       BP_PLAN_INDICATIVO
                 WHERE BP_PLAN_INDICATIVO.COMPANIA         = UN_COMPANIA
                   AND BP_PLAN_INDICATIVO.VIGENCIA_INICIAL = UN_VIGENCIA;

  MI_AUX          C1%ROWTYPE;
  MI_AUX2         C2%ROWTYPE;
  MI_VECNIVEL2    VARCHAR2(3200 CHAR):='';
  RS1             SYS_REFCURSOR;
  MI_STRSQL       PCK_SUBTIPOS.TI_STRSQL;
  MI_I            NUMBER:=0;
  MI_NIV          NUMBER;
  MI_T_TEMP       NUMBER;
  MI_IND          NUMBER;
  MI_ACUMULADOR   NUMBER:=1;
  MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
  MI_VALORES2     NUMBER;
  MI_VALORES3     NUMBER;




  /*TYPE TEMP_PLAN_INDICATIVO_FIN_TYPE IS RECORD (
  COMPANIA            BP_PLAN_INDICATIVO.COMPANIA%TYPE,
  VIGENCIA_INICIAL    BP_PLAN_INDICATIVO.VIGENCIA_INICIAL%TYPE,
  ID_SUM              VARCHAR2(3200),
  PONDERACION         BP_PLAN_INDICATIVO.PONDERACION%TYPE);

  TYPE PLAN_INDICATIVO_TABLA_TYPE IS TABLE OF TEMP_PLAN_INDICATIVO_FIN_TYPE INDEX BY BINARY_INTEGER;
  TEMP_PLAN_INDICATIVO PLAN_INDICATIVO_TABLA_TYPE;*/

  RSCOMPANIA  		        BP_PLAN_INDICATIVO.COMPANIA%TYPE;
  RSVIGENCIA_INICIAL      BP_PLAN_INDICATIVO.VIGENCIA_INICIAL%TYPE;
  RSID_SUM                VARCHAR2(3200 CHAR);
  RSPONDERACION           BP_PLAN_INDICATIVO.PONDERACION%TYPE;


  BEGIN
    BEGIN
       BEGIN

           PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'TEMP_PLAN_INDICATIVO',
                                                  UN_ACCION    => 'E',
                                                  UN_CONDICION => '1=1');

           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
           RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
        END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ER_BANCO_DELET_TEMP);
      END;

  IF UN_TEMP IS NULL OR UN_TEMP = 0 THEN
    MI_T_TEMP := 0;
  ELSE
    MI_T_TEMP := -1;
  END IF;

  IF UN_INDICADOR IS NULL OR NVL(UN_INDICADOR, '') = '' THEN
    MI_IND := 0;
  ELSE
    MI_IND := -1;
  END IF;


  --Se revisa que tenga cofigurados los niveles del plan indicativo
  OPEN C1;
    LOOP
      FETCH C1 INTO MI_AUX;
      EXIT WHEN C1%NOTFOUND;
      MI_VECNIVEL2:=MI_VECNIVEL2||','||MI_AUX.DIGITOS;
    END LOOP;
  CLOSE C1;

  MI_VECNIVEL2 := SUBSTR(MI_VECNIVEL2,2,LENGTH(MI_VECNIVEL2)-1);
  MI_VECNIVEL  := PCK_SYSMAN_UTL.FC_SPLIT_SYS(MI_VECNIVEL2,',');

  SELECT
         COUNT(*) INTO MI_NIV
    FROM
         BP_NIVEL_PLAN_IND
   WHERE
         BP_NIVEL_PLAN_IND.COMPANIA = UN_COMPANIA
     AND BP_NIVEL_PLAN_IND.VIGENCIA = UN_VIGENCIA
   ORDER BY
         BP_NIVEL_PLAN_IND.COMPANIA,
         BP_NIVEL_PLAN_IND.VIGENCIA,
         BP_NIVEL_PLAN_IND.DIGITOS ASC,
         BP_NIVEL_PLAN_IND.DESCRIPCION;

  --Se crea temporal para no tocar el plan indicativo
    IF MI_T_TEMP <> 0 THEN
     BEGIN
        BEGIN
            MI_CAMPOS  := 'COMPANIA,
                           ID,
                           VIGENCIA_INICIAL,
                           PONDERACION';

            MI_VALORES := 'SELECT
                                  BP_PLAN_INDICATIVO.COMPANIA,
                                  BP_PLAN_INDICATIVO.ID,
                                  BP_PLAN_INDICATIVO.VIGENCIA_INICIAL,
                                  BP_PLAN_INDICATIVO.PONDERACION
                             FROM
                                  BP_PLAN_INDICATIVO
                            WHERE
                                  BP_PLAN_INDICATIVO.COMPANIA         = '||UN_COMPANIA||'
                              AND BP_PLAN_INDICATIVO.VIGENCIA_INICIAL = '||UN_VIGENCIA||'';

            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'TEMP_PLAN_INDICATIVO',
                                                   UN_ACCION    => 'IS',
                                                   UN_CAMPOS    => MI_CAMPOS,
                                                   UN_VALORES   => MI_VALORES);
            MI_MSGERROR(1).CLAVE := 'VIGENCIA';
            MI_MSGERROR(1).VALOR := UN_VIGENCIA;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
         END;

         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
         PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ER_BANCO_INSERT_VIG_INI,
                                    UN_REEMPLAZOS => MI_MSGERROR);
      END;
      /*OPEN C2;
        LOOP
          FETCH C2 INTO MI_AUX2;
          EXIT WHEN C2%NOTFOUND;
          TEMP_PLAN_INDICATIVO(MI_ACUMULADOR).COMPANIA:=MI_AUX2.COMPANIA;
          TEMP_PLAN_INDICATIVO(MI_ACUMULADOR).VIGENCIA_INICIAL:=MI_AUX2.VIGENCIA_INICIAL;
          TEMP_PLAN_INDICATIVO(MI_ACUMULADOR).ID_SUM:=MI_AUX2.ID;
          TEMP_PLAN_INDICATIVO(MI_ACUMULADOR).PONDERACION:=MI_AUX2.PONDERACION;
          MI_ACUMULADOR:=MI_ACUMULADOR+1;
        END LOOP;
      CLOSE C2;*/
    END IF;

    FOR MI_I IN REVERSE 2..MI_NIV LOOP
        MI_VALORES2 := TO_NUMBER(MI_VECNIVEL(MI_I));
        MI_VALORES3 := TO_NUMBER(MI_VECNIVEL(MI_I-1));

        IF MI_IND=-1 AND MI_VECNIVEL(MI_NIV) = LENGTH(UN_INDICADOR) THEN
          OPEN RS1 FOR
            SELECT
                   BP_PLAN_INDICATIVO.COMPANIA,
                   BP_PLAN_INDICATIVO.VIGENCIA_INICIAL,
                   SUBSTR(ID,1,MI_VALORES3) ID_SUM,
                   SUM(BP_PLAN_INDICATIVO.PONDERACION) PONDERACION
              FROM
                   BP_PLAN_INDICATIVO
             WHERE
                   BP_PLAN_INDICATIVO.COMPANIA         = UN_COMPANIA
               AND BP_PLAN_INDICATIVO.VIGENCIA_INICIAL = UN_VIGENCIA
               AND LENGTH(BP_PLAN_INDICATIVO.ID)       = MI_VALORES2
               AND BP_PLAN_INDICATIVO.ID LIKE SUBSTR(UN_INDICADOR, 1, MI_VALORES3) ||'%'
             GROUP BY
                   BP_PLAN_INDICATIVO.COMPANIA,
                   BP_PLAN_INDICATIVO.VIGENCIA_INICIAL,
                   SUBSTR(ID,1,MI_VALORES3);
            LOOP
            FETCH RS1 INTO RSCOMPANIA,RSVIGENCIA_INICIAL,RSID_SUM,RSPONDERACION;
            EXIT WHEN RS1%NOTFOUND;
              IF MI_T_TEMP <> 0 THEN

              BEGIN
                BEGIN
                  MI_CAMPOS  := 'TEMP_PLAN_INDICATIVO.PONDERACION          = NVL('||REPLACE('0'||RSPONDERACION,',','.')||', 0)';
                  MI_CONDICION := '    TEMP_PLAN_INDICATIVO.COMPANIA         = '||RSCOMPANIA||'
                                 AND TEMP_PLAN_INDICATIVO.VIGENCIA_INICIAL = '||RSVIGENCIA_INICIAL||'
                                 AND TEMP_PLAN_INDICATIVO.ID               = '||RSID_SUM;

                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'TEMP_PLAN_INDICATIVO',
                                                         UN_ACCION    => 'M',
                                                         UN_CAMPOS    => MI_CAMPOS,
                                                         UN_CONDICION => MI_CONDICION);

                   MI_MSGERROR(1).CLAVE := 'PONDERACION';
                   MI_MSGERROR(1).VALOR := RSPONDERACION;

                   MI_MSGERROR(2).CLAVE := 'TABLA';
                   MI_MSGERROR(2).VALOR := 'TEMP_PLAN_INDICATIVO';

                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
                END;

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_ERROR_COD  => PCK_ERRORES.ER_BANCO_UPDA_PONDERAC,
                                           UN_REEMPLAZOS => MI_MSGERROR);
              END;

              ELSE
                BEGIN
                  BEGIN
                    MI_CAMPOS  := 'BP_PLAN_INDICATIVO.PONDERACION   = NVL('||REPLACE('0'||RSPONDERACION,',','.')||', 0),
                                   BP_PLAN_INDICATIVO.DATE_MODIFIED = SYSDATE,
                                   BP_PLAN_INDICATIVO.MODIFIED_BY   = '''||UN_USUARIO||''' ';

                    MI_CONDICION := '   BP_PLAN_INDICATIVO.COMPANIA         = '||RSCOMPANIA||'
                                  AND BP_PLAN_INDICATIVO.VIGENCIA_INICIAL = '||RSVIGENCIA_INICIAL||'
                                  AND BP_PLAN_INDICATIVO.ID               = '||RSID_SUM;

                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'BP_PLAN_INDICATIVO',
                                                           UN_ACCION    => 'M',
                                                           UN_CAMPOS    => MI_CAMPOS,
                                                           UN_CONDICION => MI_CONDICION);
                    MI_MSGERROR(1).CLAVE := 'PONDERACION';
                    MI_MSGERROR(1).VALOR := RSPONDERACION;

                    MI_MSGERROR(2).CLAVE := 'TABLA';
                    MI_MSGERROR(2).VALOR := 'BP_PLAN_INDICATIVO';

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
                 END;

                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ER_BANCO_UPDA_PONDERAC,
                                            UN_REEMPLAZOS => MI_MSGERROR);
               END;

              END IF;
            END LOOP;
          CLOSE RS1;
        ELSE

          OPEN RS1 FOR
            SELECT
                   BP_PLAN_INDICATIVO.COMPANIA,
                   BP_PLAN_INDICATIVO.VIGENCIA_INICIAL,
                   SUBSTR(ID,1,MI_VALORES3) ID_SUM,
                   SUM(BP_PLAN_INDICATIVO.PONDERACION) PONDERACION
              FROM
                   BP_PLAN_INDICATIVO
             WHERE
                   BP_PLAN_INDICATIVO.COMPANIA         = UN_COMPANIA
               AND BP_PLAN_INDICATIVO.VIGENCIA_INICIAL = UN_VIGENCIA
               AND LENGTH(BP_PLAN_INDICATIVO.ID)       = MI_VALORES2
             GROUP BY
                   BP_PLAN_INDICATIVO.COMPANIA,
                   BP_PLAN_INDICATIVO.VIGENCIA_INICIAL,
                   SUBSTR(ID,1,MI_VALORES3);
            LOOP
            FETCH RS1 INTO RSCOMPANIA,RSVIGENCIA_INICIAL,RSID_SUM,RSPONDERACION;
            EXIT WHEN RS1%NOTFOUND;

              IF MI_T_TEMP <> 0 THEN
                BEGIN
                  BEGIN
                      MI_CAMPOS  := 'TEMP_PLAN_INDICATIVO.PONDERACION         = NVL('||REPLACE('0'||RSPONDERACION,',','.')||', 0)';
                      MI_CONDICION := '   TEMP_PLAN_INDICATIVO.COMPANIA         = '||RSCOMPANIA||'
                                    AND TEMP_PLAN_INDICATIVO.VIGENCIA_INICIAL = '||RSVIGENCIA_INICIAL||'
                                    AND TEMP_PLAN_INDICATIVO.ID               = '||RSID_SUM;

                      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'TEMP_PLAN_INDICATIVO',
                                                             UN_ACCION    => 'M',
                                                             UN_CAMPOS    => MI_CAMPOS,
                                                             UN_CONDICION => MI_CONDICION);

                      MI_MSGERROR(1).CLAVE := 'PONDERACION';
                      MI_MSGERROR(1).VALOR := RSPONDERACION;

                      MI_MSGERROR(2).CLAVE := 'TABLA';
                      MI_MSGERROR(2).VALOR := 'TEMP_PLAN_INDICATIVO';

                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
                   END;

                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                   PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                              UN_ERROR_COD  => PCK_ERRORES.ER_BANCO_UPDA_PONDERAC,
                                              UN_REEMPLAZOS => MI_MSGERROR);
                 END;


              ELSE
                BEGIN
                  BEGIN
                      MI_CAMPOS := 'BP_PLAN_INDICATIVO.PONDERACION  = NVL('||REPLACE('0'||RSPONDERACION,',','.')||', 0),
                                   BP_PLAN_INDICATIVO.DATE_MODIFIED = SYSDATE,
                                   BP_PLAN_INDICATIVO.MODIFIED_BY   = '''||UN_USUARIO||''' ';

                      MI_CONDICION:= ' BP_PLAN_INDICATIVO.COMPANIA         = '||RSCOMPANIA||'
                                   AND BP_PLAN_INDICATIVO.VIGENCIA_INICIAL = '||RSVIGENCIA_INICIAL||'
                                   AND BP_PLAN_INDICATIVO.ID               = '||RSID_SUM;

                      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'BP_PLAN_INDICATIVO',
                                                             UN_ACCION    => 'M',
                                                             UN_CAMPOS    => MI_CAMPOS,
                                                             UN_CONDICION => MI_CONDICION);
                      MI_MSGERROR(1).CLAVE := 'PONDERACION';
                      MI_MSGERROR(1).VALOR := RSPONDERACION;

                      MI_MSGERROR(2).CLAVE := 'TABLA';
                      MI_MSGERROR(2).VALOR := 'BP_PLAN_INDICATIVO';

                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
                   END;

                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                   PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                              UN_ERROR_COD  => PCK_ERRORES.ER_BANCO_UPDA_PONDERAC,
                                              UN_REEMPLAZOS => MI_MSGERROR);
                 END;


              END IF;
            END LOOP;
          CLOSE RS1;
        END IF;
    END LOOP;

    MI_RTA := '';

    IF UN_GENERAREPORTE <> 0 THEN

    MI_SEPARADOR_COL := PCK_DATOS.GL_SEPARADOR_COL;
    MI_SEPARADOR_REG := PCK_DATOS.GL_SEPARADOR_REG;


    <<RECORRER_TEMPORAL>>
    FOR MI_RS 
    IN (SELECT BP_PLAN_INDICATIVO.ID,
               BP_PLAN_INDICATIVO.DESCRIPCION,
               BP_PLAN_INDICATIVO.PONDERACION ACTUAL,
               TEMP_PLAN_INDICATIVO.PONDERACION FINAL
        FROM TEMP_PLAN_INDICATIVO
        INNER JOIN BP_PLAN_INDICATIVO
        ON TEMP_PLAN_INDICATIVO.VIGENCIA_INICIAL = BP_PLAN_INDICATIVO.VIGENCIA_INICIAL
        AND TEMP_PLAN_INDICATIVO.ID              = BP_PLAN_INDICATIVO.ID
        AND TEMP_PLAN_INDICATIVO.COMPANIA        = BP_PLAN_INDICATIVO.COMPANIA
        WHERE TEMP_PLAN_INDICATIVO.PONDERACION  <> TO_NUMBER(BP_PLAN_INDICATIVO.PONDERACION))
        LOOP

     MI_RTA := MI_RTA || TO_CHAR(MI_RS.ID) 
        || MI_SEPARADOR_COL || TO_CHAR(MI_RS.DESCRIPCION) 
        || MI_SEPARADOR_COL || TO_CHAR(MI_RS.ACTUAL) 
        || MI_SEPARADOR_COL || TO_CHAR(MI_RS.FINAL) 
        || MI_SEPARADOR_REG;

        END LOOP RECORRER_TEMPORAL;

    END IF;

    RETURN MI_RTA;


END FC_MAYORIZAR_POND;

--5
FUNCTION FC_SUBIRPROYECTO_MGA
  /*
    NAME              : FC_SUBIRPROYECTO_MGA
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : NICOLAS GOMEZ BARBOSA
    DATE MIGRADOR     : 05/01/2016
    TIME              : 11:00 AM
    SOURCE MODULE     : Sysman_UBP2014.09.01.accdb
    DESCRIPTION       : Subir Proyectos desde MGA.
    @Name:  subirMGA
  */

  (
  UN_COMPANIA        IN   PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANO             IN   PCK_SUBTIPOS.TI_ANIO,
  UN_ID              IN   NUMBER,
  UN_VALIDAR_COD     IN   NUMBER,
  UN_NOMBRE          IN   VARCHAR2,
  UN_MODULO          IN   PCK_SUBTIPOS.TI_MODULO,
  UN_OBJETIVOGNRL    IN   VARCHAR2,
  UN_USUARIO         IN  PCK_SUBTIPOS.TI_USUARIO
  )
  RETURN CLOB
  AS
    MI_VALOR_PROY           VARCHAR(32000 CHAR);
    MI_VIGENCIAINICIAL      VARCHAR(32000 CHAR);
    MI_VIGENCIAFINAL        VARCHAR(32000 CHAR);
    MI_CODIGOBPIM           VARCHAR(32000 CHAR);
    MI_CODIGO               VARCHAR(32000 CHAR);
    MI_CODIGO_COMP          VARCHAR(32000 CHAR);
    MI_CODIGO_FINAN         VARCHAR(32000 CHAR);
    MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
    MI_ACTIVIDAD            VARCHAR(32000 CHAR);
    MI_ENTIDAD              VARCHAR(32000 CHAR);
    MI_TIPOP                VARCHAR(32000 CHAR);
    MI_FUENTEFIN            VARCHAR(32000 CHAR);
    MI_DIGITOS              NUMBER;
    MI_VALORUNICOMP         VARCHAR(32000 CHAR);
    MI_ENC                  NUMBER;
    MI_CONSECUTIVOINICIAL   VARCHAR(32000 CHAR);
    MI_CODENTIDAD           VARCHAR(32000 CHAR);
    MI_RTA                  CLOB;
  BEGIN

  MI_CONSECUTIVOINICIAL:=PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                               UN_NOMBRE    => 'CONSECUTIVO INICIAL DE PROYECTOS',
                                               UN_MODULO    => UN_MODULO,
                                               UN_FECHA_PAR => SYSDATE);

  MI_CODENTIDAD:=PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                       UN_NOMBRE    => 'CODIGO ENTIDAD DNP',
                                       UN_MODULO    => UN_MODULO,
                                       UN_FECHA_PAR => SYSDATE);

  MI_RTA:='---------------------------- POR FAVOR REVISAR LOS SIGUIENTES DATOS ------------------------'||CHR(10);

  /*'Se obtiene el Valor Total del del Proyecto, Vigencia inicial y Vigencia Final C'

    StrSql = " SELECT MGA_PRO_F02_Financiacion.IdF02, Min(MGA_PRO_F02_FinanciacionDetalle.Vigencia) AS MÃ­nDeVigencia, Max(MGA_PRO_F02_FinanciacionDetalle.Vigencia) AS MÃ¡xDeVigencia, Sum(MGA_PRO_F02_FinanciacionDetalle.Valor) AS SumaDeValor"
             " FROM MGA_PRO_F02_Financiacion INNER JOIN MGA_PRO_F02_FinanciacionDetalle ON MGA_PRO_F02_Financiacion.Id = MGA_PRO_F02_FinanciacionDetalle.IdF02Financiacion"
             " WHERE (((MGA_PRO_F02_Financiacion.IdF02) = 3))"
             " GROUP BY MGA_PRO_F02_Financiacion.IdF02;"

    Set Rs = Db.OpenRecordset(StrSql, dbOpenSnapshot, dbSeeChanges)
    If Not Rs.EOF Then
        ValorProyecto = Nz(Rs!SumaDeValor, "")
        VIGENCIAINICIAL = Nz(Rs!MÃ­nDeVigencia, "")
        VigenciaFinal = Nz(Rs!MÃ¡xDeVigencia, "")


    Else
        'MsgBox "El Proyecto debe tener un valor, una vigencia inicial y una vigencia final", vbCritical, "Sysman Software"

        Print #1, "Revisar los siguientes datos del proyecto: valor, vigencia inicial y vigencia final"
        enc = 1
        GoTo Salir
        Exit Sub
    End If*/


  IF UN_VALIDAR_COD=0 THEN
    MI_CONDICION:='   COMPANIA='||UN_COMPANIA||'
                  AND SUBSTR('||MI_CODIGO||',1,4)='||UN_ANO;

    MI_CODIGO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'PROYECTOS',
                                                  UN_CRITERIO => MI_CONDICION,
                                                  UN_CAMPO    => 'CODIGO',
                                                  UN_INICIAL  => MI_CONSECUTIVOINICIAL);

  ELSE
    MI_RTA:='';
  END IF;

  RETURN MI_RTA;


END FC_SUBIRPROYECTO_MGA;

--6
FUNCTION FC_BUSCARACTIVIDAD
  /*
    NAME              : FC_BUSCARACTIVIDAD
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : NICOLAS GOMEZ BARBOSA
    DATE MIGRADOR     : 08/01/2016
    TIME              : 11:00 AM
    SOURCE MODULE     : Sysman_UBP2014.09.01.accdb
    DESCRIPTION       : Devuleve el cÃ³digo de una actividad segÃºn su nombre, si no esta la inserta y devuleve el cÃ³digo creado.
    @Name: buscarInsertarActividad
  */
  (
  UN_COMPANIA   IN  PCK_SUBTIPOS.TI_COMPANIA,
  UN_NOMBRE     IN  VARCHAR2,
  UN_USER       IN  VARCHAR2,
  UN_FECHA      IN  VARCHAR2,
  UN_USUARIO    IN  PCK_SUBTIPOS.TI_USUARIO 
  )
  RETURN VARCHAR2
  AS
  MI_RTA          VARCHAR2(32000 CHAR);
  MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
  MI_I            NUMBER;
  BEGIN

  BEGIN

    SELECT
           CODIGO INTO MI_RTA
      FROM
           BP_ACTIVIDADES
     WHERE
           BP_ACTIVIDADES.NOMBRE LIKE UN_NOMBRE
       AND BP_ACTIVIDADES.COMPANIA=UN_COMPANIA
       AND ROWNUM=1;

    EXCEPTION WHEN NO_DATA_FOUND THEN

      MI_I:=PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'BP_ACTIVIDADES',
                                             UN_CRITERIO => 'COMPANIA='''||UN_COMPANIA||'''',
                                             UN_CAMPO    => 'CODIGO',
                                             UN_INICIAL  => '000001');

      BEGIN
        BEGIN
            MI_CAMPOS:='COMPANIA,
                        CODIGO,
                        NOMBRE,
                        UNIDAD,
                        PRODUCTO,
                        TIPOCOMPONENTEFICTICIO,
                        MODIFIED_BY,
                        DATE_MODIFIED,
                        CREATED_BY,
                        DATE_CREATED';

            MI_VALORES:=''''||UN_COMPANIA||''',
                        '''||MI_I||''',
                        '''||UN_NOMBRE||''',
                        ''#'',
                        '''||UN_NOMBRE||''',
                        ''000060'',
                        '''||UN_USER||''',
                        '||UN_FECHA||',
                        '''||UN_USER||''',
                        '||UN_FECHA;

            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'BP_ACTIVIDADES',
                                                   UN_ACCION    => 'I',
                                                   UN_CAMPOS    => MI_CAMPOS,
                                                   UN_VALORES   => MI_VALORES);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
         END;

         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
         PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ER_BANCO_INSERT_ACTIVI);
      END;

      MI_RTA:=MI_I;
  END;

  RETURN MI_RTA;

END FC_BUSCARACTIVIDAD;

  PROCEDURE PR_INGRESAR_RUBROS
  /*
    NAME              : PR_INGRESAR_RUBROS
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : JULIAN ESTEVEN GUERRERO GUERRERO
    DATE MIGRATION    : 02/10/2017
    TIME              : 08:55 AM
    SOURCE MODULE     : SysmanBP2015.07.01.accdb
    DESCRIPTION       : PROCEDIMIENTO QUE AGREGAR LOS RUBROS PRESUPUESTALES DE INVERSION.
    MODIFIER          : PABLO ANDRÉS ESPITIA CUCA
    MODIFICATIONS     : (2017/10/17) -> Indentación de PLSQL.
    PARAMETERS        : UN_COMPANIA			=> COMPANIA CON LA QUE SE ESTA TRABAJANDO.
                        UN_RUBRO        => RUBRO QUE SE VA A AGREGAR.
                        UN_VIGENCIA			=> VIGENCIA QUE SE VA A AGREGAR.
                        UN_USUARIO			=> USUARIO QUE REALIZA EL PROCESO.

    @NAME: ingresarRubros 
    @METHOD: POST
  */ 
	(
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_RUBRO        IN BP_RUBRO_INVERSION_DET.RUBRO_INVER%TYPE,      
	  UN_VIGENCIA	    IN PCK_SUBTIPOS.TI_ANIO,
    UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO 
  )
	AS
    MI_TABLA_DET  PCK_SUBTIPOS.TI_TABLA DEFAULT 'BP_RUBRO_INVERSION_DET';
    MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION;
    MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES    PCK_SUBTIPOS.TI_VALORES;
    MI_RTA_ACME   PCK_SUBTIPOS.TI_RTA_ACME;
  BEGIN 
    --@pespitia: Eliminar detalles del rubro de inversion
    MI_CONDICION := 'COMPANIA    = '''||UN_COMPANIA||'''
                 AND VIGENCIA    =   '||UN_VIGENCIA||'
                 AND RUBRO_INVER = '''||UN_RUBRO   ||'''';

    BEGIN
      BEGIN
        MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_ACCION    => 'E'
                                        ,UN_TABLA     => MI_TABLA_DET
                                        ,UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        MI_REEMPLAZOS(1).CLAVE := 'VIGENCIA';
        MI_REEMPLAZOS(1).VALOR := UN_VIGENCIA;
        MI_REEMPLAZOS(2).CLAVE := 'RUBRO';
        MI_REEMPLAZOS(2).VALOR := UN_RUBRO;        

        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_BDP_E_PR_INGRUB_PORVIGYRUB
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS
                                ,UN_TABLAERROR => MI_TABLA_DET);
    END; 

    MI_VALORES := 'SELECT 
                     COMPANIA
                    ,ANO 
                    ,'''||UN_RUBRO   ||'''
                    ,CODIGO
                    ,0
                    ,''SIN DEFINIR''
                    ,UPPER(NOMBRE) 
                    ,'''||UN_USUARIO ||'''
                    ,SYSDATE
                   FROM PLAN_PRESUPUESTAL
                   WHERE COMPANIA = '''||UN_COMPANIA||'''
                     AND ANO      =   '||UN_VIGENCIA||'
                     AND CODIGO LIKE ('''||(UN_RUBRO||'%')||''')
                     AND LENGTH(CODIGO) > LENGTH('''||UN_RUBRO||''')
                     AND DESTINO IN (''I'')';

    MI_CAMPOS := 'COMPANIA
                 ,VIGENCIA
                 ,RUBRO_INVER
                 ,RUBRO
                 ,NIVEL_NUM
                 ,NIVEL
                 ,DESCRIPCION
                 ,CREATED_BY
                 ,DATE_CREATED';     

     BEGIN
       BEGIN
         MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA_DET
                                         ,UN_ACCION  => 'IS'
                                         ,UN_CAMPOS  => MI_CAMPOS
                                         ,UN_VALORES => MI_VALORES);

       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
         MI_REEMPLAZOS(1).CLAVE := 'RUBRO';
         MI_REEMPLAZOS(1).VALOR := UN_RUBRO;
         MI_REEMPLAZOS(2).CLAVE := 'VIGENCIA';
         MI_REEMPLAZOS(2).VALOR := UN_VIGENCIA;

         RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
       END;    

     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
       PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                 ,UN_ERROR_COD  => PCK_ERRORES.ERR_BDP_IS_PR_INGRUB_DETRUBINV
                                 ,UN_REEMPLAZOS => MI_REEMPLAZOS
                                 ,UN_TABLAERROR => MI_TABLA_DET);
     END;     
  END PR_INGRESAR_RUBROS;

  --8  
  FUNCTION FC_VERIFICARNUEVOSRUBROS
  /*
    NAME              : FC_VERIFICARNUEVOSRUBROS 
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRATION  : JULIAN ESTEVEN GUERRERO GUERRERO
    DATE MIGRATION    : 03/10/2017
    TIME              : 11:19 AM
    SOURCE MODULE     : SysmanBP2015.07.01.accdb
    DESCRIPTION       : Busca los rubros nuevos y los adiciona en la tabla BP_RUBRO_INVERSION_DET.
    MODIFIED BY       : (19/10/2017) PABLO ANDRES ESPITIA CUCA
    MODIFICATIONS     : (19/10/2017) Indentación de PLSQL.
    PARAMETERS        : UN_COMPANIA	 => Compañia a crear.
                        UN_RUBRO     => Rubro que se va a insertar.
                        UN_VIGENCIA  => Vigencia a verificar e insertar .
                        UN_USUARIO   => Usuario con el que se va a insertar.

    RETURN            : Cantidad de datalles del rubro de inversion adicionados en la tabla BP_RUBRO_INVERSION_DET.

    @NAME  :  verificarNuevosRubros
    @METHOD:  POST
  */
  (
    UN_COMPANIA  IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_RUBRO     IN BP_RUBRO_INVERSION_DET.RUBRO_INVER%TYPE,      
    UN_VIGENCIA	 IN PCK_SUBTIPOS.TI_ANIO,
    UN_USUARIO   IN PCK_SUBTIPOS.TI_USUARIO 
  )
  RETURN VARCHAR2 
  AS 
    MI_RTA_ACME   PCK_SUBTIPOS.TI_RTA_ACME;
    MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;
    MI_TABLA_BRID PCK_SUBTIPOS.TI_TABLA DEFAULT 'BP_RUBRO_INVERSION_DET';
    MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_VALORES    PCK_SUBTIPOS.TI_VALORES;
  BEGIN 
    MI_CAMPOS := 'COMPANIA
                 ,VIGENCIA
                 ,RUBRO_INVER
                 ,RUBRO
                 ,NIVEL_NUM
                 ,NIVEL
                 ,DESCRIPCION
                 ,CREATED_BY
                 ,DATE_CREATED'; 

    --(19/10/2017) @pespitia : Planes presupuestales que no estan en los detalles del rubro de inversion
    MI_VALORES := 'SELECT 
                     PLAN_PRESUPUESTAL.COMPANIA
                    ,PLAN_PRESUPUESTAL.VIGENCIA 
                    ,'''||UN_RUBRO  ||'''
                    ,PLAN_PRESUPUESTAL.CODIGO
                    ,0
                    ,''SIN DEFINIR''
                    ,UPPER(PLAN_PRESUPUESTAL.NOMBRE) 
                    ,'''||UN_USUARIO||'''
                    ,SYSDATE
                  FROM PLAN_PRESUPUESTAL
                  LEFT JOIN BP_RUBRO_INVERSION_DET
                    ON PLAN_PRESUPUESTAL.COMPANIA = BP_RUBRO_INVERSION_DET.COMPANIA
                   AND PLAN_PRESUPUESTAL.VIGENCIA = BP_RUBRO_INVERSION_DET.VIGENCIA
                   AND PLAN_PRESUPUESTAL.CODIGO   = BP_RUBRO_INVERSION_DET.RUBRO    
                  WHERE PLAN_PRESUPUESTAL.COMPANIA = '''||UN_COMPANIA||'''
                    AND PLAN_PRESUPUESTAL.VIGENCIA =   '||UN_VIGENCIA||'
                    AND PLAN_PRESUPUESTAL.CODIGO LIKE ('''||(UN_RUBRO||'%')||''')
                    AND LENGTH(PLAN_PRESUPUESTAL.CODIGO) > LENGTH('''||UN_RUBRO||''')
                    AND PLAN_PRESUPUESTAL.DESTINO IN (''I'')
                    AND BP_RUBRO_INVERSION_DET.COMPANIA IS NULL
                    AND BP_RUBRO_INVERSION_DET.VIGENCIA IS NULL
                    AND BP_RUBRO_INVERSION_DET.RUBRO    IS NULL';

    BEGIN
      BEGIN
         MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_ACCION  => 'IS'
                                         ,UN_CAMPOS  => MI_CAMPOS
                                         ,UN_TABLA   => MI_TABLA_BRID
                                         ,UN_VALORES => MI_VALORES);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
      MI_REEMPLAZOS(1).CLAVE := 'VIGENCIA';
      MI_REEMPLAZOS(1).VALOR := UN_VIGENCIA;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_BDP_IS_FC_VERNUEV_PLANPPTO
                                ,UN_TABLAERROR => MI_TABLA_BRID
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;

    --Version de Julian
 /*   FOR RS IN (SELECT VIGENCIA,
                      ID,
                      CODIGO,
                      NVL(AUXILIAR,'99999999999999999999') AUXILIAR,
                      UPPER(NOMBRE) NOMBRE_R
               FROM   V_PLAN_PRESUPUESTAL
               WHERE  COMPANIA  = UN_COMPANIA
                 AND  VIGENCIA  = UN_VIGENCIA
                 AND  ID LIKE ('' || UN_RUBRO || '%')
                 AND  DESTINO  IN ('I')
                 AND  LENGTH(ID) >LENGTH('' || UN_RUBRO || '')
                 AND  MOVIMIENTO NOT IN (0)
              )
    LOOP

      SELECT COUNT(*) CUENTA
      INTO MI_EXISTE_RUBRO
      FROM BP_RUBRO_INVERSION_DET
      WHERE COMPANIA    = UN_COMPANIA                   
        AND VIGENCIA    = UN_VIGENCIA       
        AND RUBRO_INVER = UN_RUBRO
        AND RUBRO       = RS.CODIGO
        AND AUXILIAR    = RS.AUXILIAR;

    IF MI_EXISTE_RUBRO = 0 THEN
      DECLARE 
        MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;                
          BEGIN 		
            BEGIN            
              MI_CAMPOS := 'COMPANIA,VIGENCIA,RUBRO_INVER,RUBRO,DESCRIPCION,NIVEL,NIVEL_NUM,AUXILIAR';
              MI_NIVEL  := 'SIN DEFINIR';

              MI_VALORES := ''''|| UN_COMPANIA ||''' 
                           , ' || UN_VIGENCIA || '
                           , ''' || UN_RUBRO || '''                     
                           , ''' || RS.CODIGO || '''
                           , ''' || RS.NOMBRE_R || '''
                           , ''' || MI_NIVEL || '''   
                           ,0
                           , ''' || RS.AUXILIAR || '''';

              MI_RTA:=PCK_DATOS.FC_ACME( UN_TABLA   => 'BP_RUBRO_INVERSION_DET'
                                        ,UN_ACCION  => 'I'
                                        ,UN_CAMPOS  => MI_CAMPOS
                                        ,UN_VALORES => MI_VALORES
                                       ); 

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN                 
                RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
              END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN 
                MI_REEMPLAZOS(0).CLAVE := 'CODIGO';
                MI_REEMPLAZOS(0).VALOR := MI_I;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                          ,UN_TABLAERROR => 'BP_RUBRO_INVERSION_DET'
                                          ,UN_ERROR_COD  => PCK_ERRORES.ERR_BANCOSP_VERIFICAR_DET
                                          ,UN_REEMPLAZOS => MI_REEMPLAZOS
                                          );
            END;
            MI_I := MI_I + 1;
            MI_RTA := MI_I;
          END IF;          
      END LOOP;	*/

    RETURN MI_RTA_ACME;
  END FC_VERIFICARNUEVOSRUBROS;

  --9
  FUNCTION FC_VALIDARRUBROINVERSION
    /*
      NAME              : FC_VALIDARRUBROINVERSION - Access: FRM_RUBRO_INVERSION.RUBRO_AfterUpdate
      AUTHORS           : STEFANINI SYSMAN
      AUTHOR MIGRATION  : PABLO ANDRES ESPITIA CUCA
      DATE MIGRATION    : 12/10/2017
      TIME              : 03:24 PM
      SOURCE MODULE     : BANCO DE PROYECTOS (52)
      DESCRIPTION       : Verifica si el rubro esta configurado al anio o tiene un rubro mayor configurado.
      PARAMETERS        : UN_COMPANIA  => Compañia de ingreso a la aplicación.
                          UN_VIGENCIA  => Anio en el que se va a configurara el rubro.
                          UN_CODRUBRO  => Codigo del nuevo rubro.
                          UN_ACCION    => I -> Inserción
                                          M -> Actualización

      RETURN            : -1 -> Al insertar si la vigencia no tiene asociado un rubro de inversion.

      @NAME  : validarRubroInversion 
      @METHOD: GET
    */
  (
     UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
     UN_VIGENCIA IN PCK_SUBTIPOS.TI_ANIO,
     UN_CODRUBRO IN BP_RUBRO_INVERSION.RUBRO%TYPE,
     UN_ACCION   IN VARCHAR2
  )
  RETURN PCK_SUBTIPOS.TI_LOGICO
  AS
    MI_CODRUBRO   BP_RUBRO_INVERSION.RUBRO%TYPE;
    MI_CANT       PCK_SUBTIPOS.TI_LOGICO;
    MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_TABLA_BRI  PCK_SUBTIPOS.TI_TABLA DEFAULT 'BP_RUBRO_INVERSION';
  BEGIN
    --Valida que el codigo tenga un valor diferente de nulo
    IF UN_CODRUBRO IS NULL THEN
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_BDP_MSG_FC_VRI_RUBRONONULL);      
      END;
    END IF;

    --Validacion del anio
    BEGIN
      BEGIN
        SELECT RUBRO
        INTO MI_CODRUBRO
        FROM BP_RUBRO_INVERSION
        WHERE COMPANIA    = UN_COMPANIA
          AND VIGENCIA    = UN_VIGENCIA;

      EXCEPTION 
        WHEN NO_DATA_FOUND THEN
          MI_CODRUBRO := NULL;
        WHEN TOO_MANY_ROWS THEN 
          MI_REEMPLAZOS(1).CLAVE := 'ANO';
          MI_REEMPLAZOS(1).VALOR := UN_VIGENCIA;        

          RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_BDP_FC_VALRUB_TMR_RUBROANO
                                ,UN_TABLAERROR => MI_TABLA_BRI
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;

    --Al insertar
    IF UN_ACCION IN('I') THEN
      IF MI_CODRUBRO IS NULL THEN  
        RETURN -1;
      ELSE --Si la vigencia tiene rubro asociado
        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
          MI_REEMPLAZOS(1).CLAVE := 'ANO';
          MI_REEMPLAZOS(1).VALOR := UN_VIGENCIA;       
          MI_REEMPLAZOS(2).CLAVE := 'RUBRO';
          MI_REEMPLAZOS(2).VALOR := MI_CODRUBRO; 

          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_BDP_FC_VALRUB_MSG_RUBROANO
                                    ,UN_TABLAERROR => MI_TABLA_BRI
                                    ,UN_REEMPLAZOS => MI_REEMPLAZOS);      
        END;
      END IF;
    END IF;

    IF MI_CODRUBRO IN(SUBSTR(UN_CODRUBRO,1,LENGTH(MI_CODRUBRO))) THEN
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
        MI_REEMPLAZOS(1).CLAVE := 'ANIO';
        MI_REEMPLAZOS(1).VALOR := UN_VIGENCIA;         

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_BDP_MSG_FC_VALRUBINV_RUBMY
                                  ,UN_REEMPLAZOS => MI_REEMPLAZOS);      
      END;      
    END IF;

    RETURN -1;
  END FC_VALIDARRUBROINVERSION;

 PROCEDURE PR_REVISAR_IND_PLANINDICATIVO
(
  /*
    NAME              : FC_REVISAR_IND_PLANINDICATIVO (metodo para verificar indicadores en el formulario NivelplanindsControlador)
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : Leydi Milena Cortes Forero
    DATE              : 03,07,09/11/2017
    TIME              : 10:23 AM
    SOURCE MODULE     : Banco Proyectos
    TIME              :
    DESCRIPTION       : Proceso que verifica que dos niveles de plan indicativo no manejen meta de producto y meta resultado en la vigencia,
                        igualmente que un nivel no maneje dichos indicadores al mismo tiempo, tambi¿¿¿¿n revisa que si en el plan indicativo ya
                        existen registros con el numero de digitos del nivel que se quiere modificar, no se podra realizar la modificacion.

  */
      UN_COMPANIA               IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_VIGENCIA               IN PCK_SUBTIPOS.TI_ANIO,
      UN_DIGITOS                IN BP_NIVEL_PLAN_IND.DIGITOS%TYPE,
      UN_AUXRESUL               IN PCK_SUBTIPOS.TI_LOGICO,
      UN_AUXPRODUCT             IN PCK_SUBTIPOS.TI_LOGICO,
      UN_MAN_TRAZADOR           IN PCK_SUBTIPOS.TI_LOGICO,-- valor actual del trazador(CC:1773_CFBARRERA)
      UN_AUXRESULANT            IN PCK_SUBTIPOS.TI_LOGICO DEFAULT NULL,
      UN_AUXPRODUCTANT          IN PCK_SUBTIPOS.TI_LOGICO DEFAULT NULL,
      UN_MAN_TRAZADOR_ANT       IN PCK_SUBTIPOS.TI_LOGICO DEFAULT NULL,---- valor actual del trazador anterior(CC:1773_CFBARRERA)
      UN_DIGITOSANT             IN BP_NIVEL_PLAN_IND.DIGITOS%TYPE DEFAULT NULL,
      UN_OPCION                 IN VARCHAR2 DEFAULT NULL
)
AS
   MI_VALIDARESUL    PCK_SUBTIPOS.TI_ENTERO;
   MI_VALIDAPRODUC   PCK_SUBTIPOS.TI_ENTERO;
   MI_VALIDARESPRO   PCK_SUBTIPOS.TI_ENTERO;
   MI_VALIDAR_TRAZADOR PCK_SUBTIPOS.TI_ENTERO;
   MI_VALIDACAMBIOMP PCK_SUBTIPOS.TI_ENTERO;
   MI_CANTIDAD       PCK_SUBTIPOS.TI_ENTERO;
   MI_NUMDIGITOS     BP_NIVEL_PLAN_IND.DIGITOS%TYPE;
   MI_DIGITOS        BP_NIVEL_PLAN_IND.DIGITOS%TYPE;
   MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;
   MI_RTAFC          PCK_SUBTIPOS.TI_LOGICO;

BEGIN
    --Verifica si los indicadores META_RESUL y META_PRODUC están activos para una misma etapa.
    IF UN_AUXRESUL NOT IN (0) 
    AND UN_AUXPRODUCT NOT IN (0)
    THEN
      MI_VALIDARESPRO := 0;
    ELSE
      MI_VALIDARESPRO := 1;
    END IF;

     --Mensaje META_RESUL 
    BEGIN
      IF MI_VALIDARESPRO = 0
      THEN
        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD =>SQLCODE,
            UN_ERROR_COD=>PCK_ERRORES.ERR_BANCOP_METASNIVPLANIND
          ); 
    END; 

    -- Consulta longitud ID plan indicativo.
   IF UN_AUXPRODUCT NOT IN (0)
    THEN
      BEGIN
        SELECT LENGTH(BP_PLAN_INDICATIVO.ID) AS LARGO
          INTO MI_NUMDIGITOS
          FROM BP_PLAN_INDICATIVO_METAS
               INNER JOIN BP_PLAN_INDICATIVO
                  ON BP_PLAN_INDICATIVO_METAS.COMPANIA      = BP_PLAN_INDICATIVO.COMPANIA
                 AND BP_PLAN_INDICATIVO_METAS.ID_PLAN       = BP_PLAN_INDICATIVO.ID
                 AND BP_PLAN_INDICATIVO_METAS.VIGENCIA_PLAN = BP_PLAN_INDICATIVO.VIGENCIA_INICIAL
         WHERE BP_PLAN_INDICATIVO.COMPANIA         = UN_COMPANIA
           AND BP_PLAN_INDICATIVO.VIGENCIA_INICIAL = UN_VIGENCIA
           AND LENGTH(BP_PLAN_INDICATIVO.ID)       = UN_DIGITOS
         GROUP BY LENGTH(BP_PLAN_INDICATIVO.ID);
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_NUMDIGITOS   := -1;
      END;
    END IF;

    BEGIN
      IF MI_NUMDIGITOS NOT IN (-1)
      AND MI_NUMDIGITOS <> UN_DIGITOS
      THEN
        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
        MI_MSGERROR(1).CLAVE := 'LARGO';
        MI_MSGERROR(1).VALOR := MI_NUMDIGITOS;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD =>SQLCODE,
          UN_ERROR_COD=>PCK_ERRORES.ERR_BANCOP_LARGO_NIVPLANIND,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;

    --Consulta MANEJA_TRAZADOR para la vigencia
    BEGIN
      SELECT META_RESUL
        INTO MI_VALIDARESUL
        FROM BP_NIVEL_PLAN_IND
       WHERE COMPANIA   = UN_COMPANIA
         AND VIGENCIA   = UN_VIGENCIA
         AND META_RESUL NOT IN(0)
         AND DIGITOS    NOT IN(UN_DIGITOS);
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_VALIDARESUL := 0;
    END;

    BEGIN
      IF MI_VALIDARESUL NOT IN (0)
      AND UN_AUXRESUL NOT IN (0)
      THEN
        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD =>SQLCODE,
            UN_ERROR_COD=>PCK_ERRORES.ERR_BANCOP_METARES_NIVPLANIND
          );
    END;

    --Consulta META_PRODUC para la vigencia
    BEGIN
      SELECT META_PRODUC
        INTO MI_VALIDAPRODUC
        FROM BP_NIVEL_PLAN_IND
       WHERE COMPANIA   = UN_COMPANIA
         AND VIGENCIA   = UN_VIGENCIA
         AND META_PRODUC NOT IN(0)
         AND DIGITOS     NOT IN(UN_DIGITOS);
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_VALIDAPRODUC := 0;
    END;

     BEGIN
      IF MI_VALIDAPRODUC NOT IN (0)
      AND UN_AUXPRODUCT NOT IN (0)
      THEN
        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD =>SQLCODE,
            UN_ERROR_COD=>PCK_ERRORES.ERR_BANCOP_METAPRODNIVPLANIND
          );
    END;
   --Consulta MANEJA_TRAZADOR para la vigencia--(INICIO_CC:1773_CFBARRERA)
    BEGIN
      SELECT MANEJA_TRAZADOR
        INTO MI_VALIDAR_TRAZADOR
        FROM BP_NIVEL_PLAN_IND
       WHERE COMPANIA   = UN_COMPANIA
         AND VIGENCIA   = UN_VIGENCIA
         AND MANEJA_TRAZADOR NOT IN(0)
         AND DIGITOS    NOT IN(UN_DIGITOS);
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_VALIDAR_TRAZADOR := 0;
    END;

    BEGIN
      IF MI_VALIDAR_TRAZADOR NOT IN (0)
      AND UN_MAN_TRAZADOR NOT IN (0)
      THEN
        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD =>SQLCODE,
            UN_ERROR_COD=>PCK_ERRORES.ERR_BANCOP_METARES_NIVPLANIND
          );
    END;--(FIN_CC:1773_CFBARRERA)
    ------------***************------------

    IF MI_NUMDIGITOS = 0
    THEN
      MI_VALIDACAMBIOMP := 1;
    END IF;

    ------------***************------------

    --Revisa si hubo cambio de indicadores y si es posible realizar el cambio en el nivel.
    IF (UN_AUXRESULANT IS NOT NULL
    AND UN_AUXRESUL <> UN_AUXRESULANT)
    OR (UN_AUXPRODUCTANT IS NOT NULL
    AND UN_AUXPRODUCT <> UN_AUXPRODUCTANT)
    OR (UN_DIGITOSANT IS NOT NULL
    AND UN_DIGITOS <> UN_DIGITOSANT)
    OR (UN_MAN_TRAZADOR_ANT IS NOT NULL
    AND UN_MAN_TRAZADOR <> UN_MAN_TRAZADOR_ANT)--(CC:1773_CFBARRERA)
    OR (UN_OPCION = 'BORRAR')
    THEN
      IF UN_DIGITOS <> UN_DIGITOSANT
      THEN
        MI_DIGITOS := UN_DIGITOSANT;
      ELSE
        MI_DIGITOS := UN_DIGITOS;
      END IF;

      SELECT COUNT(ID) CANTIDAD
      INTO MI_CANTIDAD
      FROM BP_PLAN_INDICATIVO
     WHERE COMPANIA         = UN_COMPANIA
       AND VIGENCIA_INICIAL = UN_VIGENCIA
       AND LENGTH(ID)       = MI_DIGITOS;

      BEGIN
        IF MI_CANTIDAD NOT IN (0)
        THEN
          RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
        END IF;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
            IF UN_OPCION = 'BORRAR'
            THEN
              MI_MSGERROR(1).CLAVE := 'ACCION';
              MI_MSGERROR(1).VALOR := 'eliminar';
            ELSE
              MI_MSGERROR(1).CLAVE := 'ACCION';
              MI_MSGERROR(1).VALOR := 'modificar';
            END IF;
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD =>SQLCODE,
              UN_ERROR_COD=>PCK_ERRORES.ERR_BANCOP_NIV_ID_PLANIND,
              UN_REEMPLAZOS => MI_MSGERROR
            );
      END;
    END IF;

END PR_REVISAR_IND_PLANINDICATIVO;

PROCEDURE PR_ELIMINARACTIVIDADES
/*
    NAME              : PR_ELIMINARACTIVIDADES  --> EN ACCESS Aceptar_Click() Elimina actividades del proyecto
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
    DATE MIGRADOR     : 31/05/2015
    TIME              : 11:45 AM
    SOURCE MODULE     : SysmanBP2018.05.02
    DESCRIPTION       : Elimina actividades del proyecto

    @Name: eliminarActividades
  */
  (
  UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_PROYECTO      IN  PROYECTOS.CODIGO%TYPE,
  UN_ACTIVIDAD     IN  COMPONENTES_ACTIVIDADES.ACTIVIDAD%TYPE,
  UN_USUARIO       IN  PCK_SUBTIPOS.TI_USUARIO
  )

  AS

  MI_I               NUMBER;
  MI_STRRTA          VARCHAR2(3200 CHAR);
  MI_STRSQL          PCK_SUBTIPOS.TI_STRSQL;
  MI_MSGERROR        PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_CONDICION       PCK_SUBTIPOS.TI_CONDICION;
  MI_CAMPOS          PCK_SUBTIPOS.TI_CAMPOS;


  BEGIN

  <<SOLI_RELACIONADA>>
  FOR MI_RS IN(SELECT N.CODIGO,
            N.CLASET,
            DN.PROYECTO,
            DN.ACTIVIDAD
     FROM BPNOVEDADPROYECTO N
       INNER JOIN BP_D_NOVEDADPROYECTO DN
          ON (N.DEPENDENCIA = DN.DEPENDENCIA)
          AND (N.COMPANIA   = DN.COMPANIA)
          AND (N.CODIGO     = DN.NOVEDAD)
          AND (N.CLASET     = DN.CLASET)
          AND (N.TIPOT      = DN.TIPOT)
    WHERE N.COMPANIA   = UN_COMPANIA
      AND N.ESTADO     IN ('V', 'A')
      AND DN.PROYECTO  = UN_PROYECTO
      AND DN.ACTIVIDAD = UN_ACTIVIDAD) LOOP

      BEGIN
       BEGIN
        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;

       END;

         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                MI_MSGERROR(1).CLAVE := 'CODIGO';
                MI_MSGERROR(1).VALOR := MI_RS.CODIGO;
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE
                                             ,UN_ERROR_COD=>PCK_ERRORES.ER_BANCO_SOLIRELACIONADA
                                             ,UN_REEMPLAZOS  => MI_MSGERROR);
      END;  

  END LOOP SOLI_RELACIONADA;

  MI_CONDICION := 'COMPANIA          = '''||UN_COMPANIA||''' 
                AND  CODIGOPROYECTO  = '''||UN_PROYECTO||'''
                AND  CODIGOACTIVIDAD = '''||UN_ACTIVIDAD||''' ';

   BEGIN
       BEGIN
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'PROGRAMACION'
                                               ,UN_ACCION    => 'E'
                                               ,UN_CONDICION => MI_CONDICION);                                                           

           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;                                   

       END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
               MI_MSGERROR(1).CLAVE := 'ACTIVIDAD';
               MI_MSGERROR(1).VALOR := UN_ACTIVIDAD;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ER_BANCO_DELEPRGRAACTIVIDAD
                                        ,UN_REEMPLAZOS  => MI_MSGERROR);

   END;


   MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||''' 
              AND  PROYECTO  = '''||UN_PROYECTO||'''
              AND  ACTIVIDAD = '''||UN_ACTIVIDAD||''' ';

      BEGIN
       BEGIN
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'BP_PROYECTO_PLAN_INDICATIVO'
                                               ,UN_ACCION    => 'E'
                                               ,UN_CONDICION => MI_CONDICION);                                                           

           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;                                   

       END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
               MI_MSGERROR(1).CLAVE := 'ACTIVIDAD';
               MI_MSGERROR(1).VALOR := UN_ACTIVIDAD;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ER_BANCO_DELEPACTIVIDADPI
                                        ,UN_REEMPLAZOS  => MI_MSGERROR);

   END;


     MI_CONDICION := 'COMPANIA      = '''||UN_COMPANIA||''' 
                AND  CODIGOPROYECTO = '''||UN_PROYECTO||'''
                AND  ACTIVIDAD      = '''||UN_ACTIVIDAD||''' ';

   BEGIN
       BEGIN
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'COMPONENTES_ACTIVIDADES'
                                               ,UN_ACCION    => 'E'
                                               ,UN_CONDICION => MI_CONDICION);                                                           

           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;                                   

       END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
               MI_MSGERROR(1).CLAVE := 'ACTIVIDAD';
               MI_MSGERROR(1).VALOR := UN_ACTIVIDAD;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ER_BANCO_DELECOMPACTACTIVIDAD
                                        ,UN_REEMPLAZOS  => MI_MSGERROR);

   END;

  <<CONSULTAR_COMPONENTES>> 
  FOR MI_RS IN(SELECT C.COMPANIA,
              C.CODIGOPROYECTO,
              C.TIPOCOMPONENTE,
              C.CODIGO,
              SUM(ROUND(CA.VALORPROGRAMADO,0)) AS TOTALPROG
            FROM COMPONENTES C
            LEFT JOIN COMPONENTES_ACTIVIDADES CA
                ON C.COMPANIA        = CA.COMPANIA
                AND C.CODIGO         = CA.COMPONENTE
                AND C.TIPOCOMPONENTE = CA.TIPOCOMPONENTE
                AND C.CODIGOPROYECTO = CA.CODIGOPROYECTO
            WHERE C.COMPANIA        = UN_COMPANIA
              AND C.CODIGOPROYECTO  = UN_PROYECTO
            GROUP BY C.COMPANIA,
              C.CODIGOPROYECTO,
              C.TIPOCOMPONENTE,
              C.CODIGO
  )LOOP

    MI_CAMPOS := 'VALORPROGRAMADO = '||NVL(MI_RS.TOTALPROG,0)||', 
                  SALDOCOMPONENTE = '||NVL(MI_RS.TOTALPROG, 0)||'  -  VALORTOTALSOLICITADO';

    MI_CONDICION := 'COMPANIA         = '''||UN_COMPANIA||'''
                AND  CODIGOPROYECTO = '''||MI_RS.CODIGOPROYECTO||'''
                AND  TIPOCOMPONENTE = '''||MI_RS.TIPOCOMPONENTE||'''
                AND  CODIGO         = '''||MI_RS.CODIGO||''' ';


     BEGIN
       BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'COMPONENTES'
                                              ,UN_ACCION    => 'M'
                                              ,UN_CAMPOS    => MI_CAMPOS
                                              ,UN_CONDICION => MI_CONDICION);


           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;                                   

       END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
               MI_MSGERROR(1).CLAVE := 'ACTIVIDAD';
               MI_MSGERROR(1).VALOR := UN_ACTIVIDAD;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ER_BANCO_ACTCOMPACTIVIDAD
                                        ,UN_REEMPLAZOS  => MI_MSGERROR);

     END; 
  END LOOP CONSULTAR_COMPONENTES;
END PR_ELIMINARACTIVIDADES;


END PCK_BANCOS_PROY4;