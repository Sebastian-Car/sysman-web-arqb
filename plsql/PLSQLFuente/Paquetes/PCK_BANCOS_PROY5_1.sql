create or replace PACKAGE BODY               "PCK_BANCOS_PROY5" AS

--1
FUNCTION FC_MAYORIZA_AVANCE
 /*
    NAME              : PR_MAYORIZA_AVANCE  --> EN ACCESS MAYORIZA_AVANCE  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÃ?GUEZ

    TIME              : 08:11 AM
    SOURCE MODULE     : BANCO_PROYECTOS_CONSULTA.accdb
    MODIFIER          : DIEGO ALFREDO SUESCA RODRÃ?GUEZ
    DATE MODIFIED     : JUAN SEBASTIAN FORERO NOGUERA
    TIME              : 14:00
    DESCRIPTION       : Permite Mayorizar Avance de la meta producto
    PARAMETERS        : UN_COMPANIA => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                        UN_VIGENCIA => VIGENCIA DEL AVANCE FINANCIERO
                        UN_META_PROD_INI => NO SE UTILIZA
                        UN_META_PROD_FIN => NO SE UTILIZA
                        UN_TOTAL => PARAMETRO QUCONTROLA LA ASIGNACION DE CONDICION
    MODIFICATIONS     : CORRECCION SEGUN ESTANDAR DE PROGRAMACION Y OPTIMIZACION DE MANEJO DE ERRORES.
    @Name: mayorizarAvance
  */
(
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_VIGENCIA       IN PCK_SUBTIPOS.TI_ANIO,
    UN_META_PROD_INI  IN VARCHAR2,
    UN_META_PROD_FIN  IN VARCHAR2,
    UN_TOTAL          IN PCK_SUBTIPOS.TI_DOBLE DEFAULT NULL,
    UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN NUMBER
  AS
  MI_ERROR_FUN      PCK_SUBTIPOS.TI_ERROR_FUN;
  MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
  MI_PORCENTAJE     PCK_SUBTIPOS.TI_DOBLE;
  MI_PORCENTAJE_F   PCK_SUBTIPOS.TI_DOBLE;
  MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
  MI_MERGEUSING     PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE    PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE    PCK_SUBTIPOS.TI_MERGEEXISTE;

  BEGIN

  IF UN_TOTAL!=NULL AND UN_TOTAL<>0 THEN
    BEGIN
    MI_VALORES:='AVANCE=0,
                 AVANCE_FINANCIERO=0,
                 DATE_MODIFIED = SYSDATE,
                 MODIFIED_BY   ='''||UN_USUARIO||''' ';

    MI_CONDICION:='COMPANIA ='''|| UN_COMPANIA || '''
                  AND     VIGENCIA_INICIAL IN(' || UN_VIGENCIA || ')';

    PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA => 'BP_PLAN_INDICATIVO',
                                        UN_ACCION => 'M',
                                        UN_CAMPOS => MI_VALORES,
                                        UN_CONDICION => MI_CONDICION);


    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
    END ;


  END IF;
  <<RC>>
  FOR RC IN(
            SELECT    COMPANIA,
                      ID_PLAN,
                      VIGENCIA_PLAN,
                      SUM (CANTIDAD_EJECUTADA) AS EJECUTADO,
                      SUM (CANTIDAD_PROGRAMADA) AS PROGRAMADO,
                      SUM (VALOR_PROGRAMADO_META)+SUM(VALOR_PROGRAMADO_META_OTROS) AS F_PROGRAMADO,
                      SUM (VALOR_EJECUTADO_META) AS F_EJECUTADO
             FROM     BP_PLAN_INDICATIVO_METAS
            WHERE     BP_PLAN_INDICATIVO_METAS.COMPANIA IN(UN_COMPANIA)
              AND     BP_PLAN_INDICATIVO_METAS.VIGENCIA_PLAN IN(UN_VIGENCIA)
              AND     (BP_PLAN_INDICATIVO_METAS.ID_PLAN BETWEEN '0' AND '999999999999999')
            GROUP BY
                      COMPANIA,
                      ID_PLAN,
                      VIGENCIA_PLAN

          ) LOOP
            IF RC.PROGRAMADO <> 0 THEN
              MI_PORCENTAJE := RC.EJECUTADO / RC.PROGRAMADO;
            ELSE
              MI_PORCENTAJE := 0;
            END IF;
            IF RC.F_PROGRAMADO <> 0 THEN
              MI_PORCENTAJE_F := RC.F_EJECUTADO / RC.F_PROGRAMADO;
            ELSE
              MI_PORCENTAJE_F := 0;
            END IF;
            IF MI_PORCENTAJE <> 0 OR MI_PORCENTAJE_F <> 0 THEN

            MI_VALORES:=      'AVANCE=(AVANCE+0'||
                              REPLACE(TO_CHAR(MI_PORCENTAJE),',','.') ||
                              '* PONDERACION),AVANCE_FINANCIERO=(AVANCE_FINANCIERO+ 0'||
                              REPLACE(TO_CHAR(MI_PORCENTAJE_F),',','.') ||
                              '* PONDERACION),
                              DATE_MODIFIED = SYSDATE,
                              MODIFIED_BY   ='''||UN_USUARIO||''' ';

            MI_CONDICION:=    'BP_PLAN_INDICATIVO.COMPANIA IN(''' || UN_COMPANIA || ''')
                                  AND     BP_PLAN_INDICATIVO.VIGENCIA_INICIAL IN('|| UN_VIGENCIA || ')
                                  AND     BP_PLAN_INDICATIVO.ID IN(''' ||  RC.ID_PLAN || ''')';

            BEGIN
            PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA => 'BP_PLAN_INDICATIVO',
                                                UN_ACCION => 'M',
                                                UN_CAMPOS => MI_VALORES,
                                                UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            END ;


                MI_MERGEUSING :='SELECT ID,AVANCE,AVANCE_FINANCIERO FROM BP_PLAN_INDICATIVO
                              WHERE VIGENCIA_INICIAL IN('|| UN_VIGENCIA || ')
                              AND ID IN (''' ||  RC.ID_PLAN || ''')';

            MI_MERGEENLACE := 'TABLA.COMPANIA IN(''' || UN_COMPANIA || ''')
                                  AND   TABLA.VIGENCIA_INICIAL IN('|| UN_VIGENCIA || ')
                                  AND   VISTA.ID IN (''' ||  RC.ID_PLAN || ''')
                                  AND   TABLA.ID=SUBSTR(''' ||  RC.ID_PLAN || ''',1,LENGTH(TABLA.ID))
                                  AND   LENGTH(TABLA.ID)<LENGTH(''' ||  RC.ID_PLAN || ''')';

            MI_MERGEEXISTE := 'UPDATE SET TABLA.AVANCE  = TABLA.AVANCE+VISTA.AVANCE,
                                         TABLA.AVANCE_FINANCIERO = TABLA.AVANCE_FINANCIERO + VISTA.AVANCE_FINANCIERO';

              BEGIN
            PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA       => 'BP_PLAN_INDICATIVO',
                                                UN_ACCION      => 'MM',
                                                UN_MERGEUSING  => MI_MERGEUSING,
                                                UN_MERGEENLACE => MI_MERGEENLACE,
                                                UN_MERGEEXISTE => MI_MERGEEXISTE);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            END ;
        END IF;
  END LOOP RS;
  RETURN 1;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ER_BANCOSP_MAYORIZA_AVANCE);


END FC_MAYORIZA_AVANCE;


--2
FUNCTION FC_ACT_PLAN_INDICADOR
 /*
    NAME              : FC_ACT_PLAN_INDICADOR  --> EN ACCESS act_plan_indicador  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÃ?GUEZ
    DATE MIGRADOR     : 31/09/2015
    TIME              : 08:11 AM
    SOURCE MODULE     : BANCO_PROYECTOS_CONSULTA.accdb
    MODIFIER          : JUAN SEBASTIAN FORERO NOGUERA
    DATE MODIFIED     : 31/01/2017
    TIME              : 4:00 PM
    DESCRIPTION       : Actualiza en el plan indicativo de metas la correspondiente meta que esta involucrada en la novedad que se va a ejecutar
    PARAMETERS        : UN_COMPANIA => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                        UN_MODULO =>  NO SE UTILIZA
                        UN_NOVEDAD_INICIAL => CODIGO INICIAL PARA LA CONSULTA
                        UN_NOVEDAD_FINAL => CODIGO FINAL PARA LA CONSULTA
                        UN_TIPO  => PARAMETRO QUE DEFINE LA CONDICION
    MODIFICATIONS     : CORRECCION SEGUN ESTANDAR DE PROGRAMACION Y OPTIMIZACION DE MANEJO DE ERRORES.
    @Name: actualizarPlanIndicativo
  */
(
   	UN_COMPANIA				       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_MODULO                IN PCK_SUBTIPOS.TI_MODULO,
    UN_NOVEDAD_INICIAL       IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
    UN_NOVEDAD_FINAL         IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
    UN_TIPO                  IN VARCHAR2,
    UN_USUARIO               IN PCK_SUBTIPOS.TI_USUARIO
)
  RETURN NUMBER
  AS
    MI_ERROR_FUN                     	PCK_SUBTIPOS.TI_ERROR_FUN;

    MI_RESULTADO                      PCK_SUBTIPOS.TI_ENTERO;
    MI_CONDICION                     	PCK_SUBTIPOS.TI_CONDICION;
    MI_COND                         	PCK_SUBTIPOS.TI_CONDICION;
    MI_STRSQL			               	    PCK_SUBTIPOS.TI_STRSQL;
    MI_VALORES                 	      PCK_SUBTIPOS.TI_VALORES;
    MI_CAMPOSACTUALIZADOS             PCK_SUBTIPOS.TI_ENTERO;
    MI_TABLA                          PCK_SUBTIPOS.TI_TABLA;
    RS                                SYS_REFCURSOR;
    RS2                               SYS_REFCURSOR;
    RSCOMPANIA 			                  BPNOVEDADPROYECTO.COMPANIA%TYPE;
    RSVIGENCIA_PLAN		            		BPNOVEDADPROYECTO.VIGENCIA%TYPE;
    RSID_META_PRODUCTO	          		BP_D_NOVEDADPROYECTO.ID_META_PRODUCTO%TYPE;
    RSVIGENCIA_PLAN_INDICATIVO      	BP_D_NOVEDADPROYECTO.VIGENCIA_PLAN_INDICATIVO%TYPE;
    RSCANTIDAD_PLAN			            	BP_D_NOVEDADPROYECTO.CANTIDAD_PLAN%TYPE;
    RSVALORAPROBADO 			          	BP_D_NOVEDADPROYECTO.VALORAPROBADO%TYPE;


  BEGIN

      MI_CAMPOSACTUALIZADOS :=0;
      MI_RESULTADO :=0;
      IF UN_TIPO <> '' THEN
         MI_COND := ' N.TIPOT=''' || UN_TIPO || ''' AND
                            NVL(D.CONPROGRAMACION,0)=0 AND ';

      ELSE
        MI_COND := '';
      END IF;
      MI_COND := 'WHERE   N.COMPANIA IN(''' || UN_COMPANIA || ''')
                  AND 	   N.CLASET IN(''B'',''P'')
                  AND ' || MI_COND ||'
                  (N.CODIGO BETWEEN ' || UN_NOVEDAD_INICIAL ||
                                                ' AND ' || UN_NOVEDAD_FINAL || ')
                  AND 	NVL(VOBOBP,0)<>0
                  AND 	N.ESTADO IN(''V'',''AP'')
                  AND 	D.ID_META_PRODUCTO IS NOT NULL';

      MI_STRSQL := ' SELECT 		N.COMPANIA,
                                N.VIGENCIA AS VIGENCIA_PLAN,
                                D.ID_META_PRODUCTO,
                                D.VIGENCIA_PLAN_INDICATIVO,
                                SUM(D.CANTIDAD_PLAN) AS CANTIDAD_PLAN,
                                SUM(D.VALORAPROBADO) AS VALORAPROBADO
                     FROM 		  (BPNOVEDADPROYECTO N
                                INNER JOIN BPTIPONOVEDAD T
                                    ON   (N.CLASET = T.CLASET)
                                    AND  (N.TIPOT = T.TIPOT)
                                    AND  (N.COMPANIA = T.COMPANIA))
                                    INNER JOIN BP_D_NOVEDADPROYECTO D
                                        ON   N.DEPENDENCIA = D.DEPENDENCIA
                                        AND  N.CODIGO = D.NOVEDAD
                                        AND  N.CLASET = D.CLASET
                                        AND  N.TIPOT = D.TIPOT
                                        AND  N.COMPANIA = D.COMPANIA '
                                || MI_COND ||
                                    ' GROUP BY 	  N.COMPANIA,
                                                  N.VIGENCIA,
                                                  D.ID_META_PRODUCTO,
                                                  D.VIGENCIA_PLAN_INDICATIVO';
      <<RS_STRSQL>>
      OPEN RS FOR MI_STRSQL;
        LOOP
          FETCH RS INTO RSCOMPANIA,
                        RSVIGENCIA_PLAN,
                        RSID_META_PRODUCTO,
                        RSVIGENCIA_PLAN_INDICATIVO,
                        RSCANTIDAD_PLAN,
                        RSVALORAPROBADO;
          EXIT WHEN RS%NOTFOUND;

          MI_VALORES:='BP_PLAN_INDICATIVO_METAS.CANTIDAD_EJECUTADA   = ' || NVL(RSCANTIDAD_PLAN, 0) ||
                     ',BP_PLAN_INDICATIVO_METAS.VALOR_EJECUTADO_META = ' || NVL(RSVALORAPROBADO, 0) ||                      
                     ',DATE_MODIFIED = CURRENT_DATE
                      ,MODIFIED_BY   ='''||UN_USUARIO||'''';
          MI_CONDICION:=          'BP_PLAN_INDICATIVO_METAS.COMPANIA=''' || UN_COMPANIA || '''
                          AND      	BP_PLAN_INDICATIVO_METAS.ID_PLAN=''' || RSID_META_PRODUCTO || '''
                          AND     	BP_PLAN_INDICATIVO_METAS.VIGENCIA_PLAN=' || RSVIGENCIA_PLAN_INDICATIVO || '
                          AND     	BP_PLAN_INDICATIVO_METAS.VIGENCIA_META=' || RSVIGENCIA_PLAN ||'';
          MI_TABLA := 'BP_PLAN_INDICATIVO_METAS';

            BEGIN
            MI_CAMPOSACTUALIZADOS:=PCK_DATOS.FC_ACME( UN_TABLA => MI_TABLA,
                                                      UN_ACCION => 'M',
                                                      UN_CAMPOS => MI_VALORES,
                                                      UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            END ;
      END LOOP RS_STRSQL;
      CLOSE RS;

      MI_STRSQL := ' SELECT   N.COMPANIA,
                              N.VIGENCIA AS VIGENCIA_PLAN,
                              D.VIGENCIA_PLAN_INDICATIVO,
                              D.ID_META_PRODUCTO
                       FROM 	(BPNOVEDADPROYECTO N INNER JOIN
                              BPTIPONOVEDAD T
                                  ON (N.CLASET = T.CLASET)
                                  AND (N.TIPOT = T.TIPOT)
                                  AND (N.COMPANIA = T.COMPANIA))
                                  INNER JOIN BP_D_NOVEDADPROYECTO D
                                      ON (N.DEPENDENCIA = D.DEPENDENCIA)
                                      AND (N.CODIGO = D.NOVEDAD)
                                      AND (N.CLASET = D.CLASET)
                                      AND (N.TIPOT = D.TIPOT)
                                      AND (N.COMPANIA = D.COMPANIA)'
                    || MI_COND ||
                          '  GROUP BY 	N.COMPANIA,
                                        N.VIGENCIA,
                                        D.VIGENCIA_PLAN_INDICATIVO,
                                        D.ID_META_PRODUCTO';

      OPEN RS2 FOR MI_STRSQL;
        LOOP
          FETCH RS2 INTO  RSCOMPANIA,
                          RSVIGENCIA_PLAN,
                          RSVIGENCIA_PLAN_INDICATIVO,
                          RSID_META_PRODUCTO;
           EXIT WHEN RS2%NOTFOUND;

           MI_RESULTADO:=FC_MAYORIZA_AVANCE(UN_COMPANIA => UN_COMPANIA, 
                                            UN_VIGENCIA => RSVIGENCIA_PLAN_INDICATIVO, 
                                            UN_META_PROD_INI => RSID_META_PRODUCTO, 
                                            UN_META_PROD_FIN => RSVIGENCIA_PLAN_INDICATIVO,
                                            UN_USUARIO       => UN_USUARIO);

        END LOOP;
      CLOSE RS2;
      RETURN MI_RESULTADO;

       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ER_BANCOSP_PLAN_INDICADOR);
END FC_ACT_PLAN_INDICADOR;

--3
FUNCTION FC_GENCONSECUTIVOCDP
/*
    NAME              : PR_MAYORIZA_AVANCE  --> EN ACCESS MAYORIZA_AVANCE  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ADRIANA MARITZA CÃ¿CERES BONILLA
    DATE MIGRADOR     : 25/09/2015
    TIME              : 14:39 PM
    SOURCE MODULE     : BANCO_PROYECTOS_CONSULTA.accdb
    MODIFIER          : JUAN SEBASTIAN FORERO NOGUERA
    DATE MODIFIED     : 31/01/2017
    TIME              : 5:00 PM
    DESCRIPTION       : FUNCION QUE GENERA EL CONSECUTIVO PARA UNA SOLICITUD CDP
    PARAMETERS        : UN_NOMBRETABLA => NOMBRE DE LA TABLA PARA GENERAR LA CONSULTA
                        UN_CONDICION => CONDICION PARA GENERAR LA CONSULTA
                        UN_NOMBRECAMPO => EL NOMBRE DEL CAMPO QUE SE VA A OPERAR Y PROYECTAR EN LA CONSULTA
                        UN_INICIAL => INICIAL PARA GENERAR EL CONSECUTIVO
    MODIFICATIONS     : CORRECCION SEGUN ESTANDAR DE PROGRAMACION Y OPTIMIZACION DE MANEJO DE ERRORES.
    @Name: genConsecutivoCDP
  */
  (
    UN_NOMBRETABLA          IN PCK_SUBTIPOS.TI_TABLA,
    UN_CONDICION            IN PCK_SUBTIPOS.TI_CONDICION,
    UN_NOMBRECAMPO          IN VARCHAR2, -- VARCHAR DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
    UN_INICIAL              IN VARCHAR2  -- VARCHAR DEBIDO A QUE NO EXISTE UN TIPO DE DATO CON LAS MISMAS CARACTERISTICAS EN PCK_SUBTIPOS
  )
RETURN VARCHAR2
 AS
    MI_ERROR_FUN            PCK_SUBTIPOS.TI_ERROR_FUN;
    MI_DIGITOS              PCK_SUBTIPOS.TI_DOBLE;
    MI_CEROS                BOOLEAN;
    MI_SQL                  VARCHAR2(3000 CHAR);
    MI_ANIO_RESULT          PCK_SUBTIPOS.TI_ENTERO;
    MI_GENCONSECUTIVOCDP    VARCHAR2(3000 CHAR);
    MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
    MI_INICIAL              VARCHAR2(3000 CHAR);
BEGIN
    IF(UN_CONDICION <> '') THEN
        MI_CONDICION:= 'WHERE' || UN_CONDICION;
    END IF;
    MI_SQL:='SELECT     MAX(TO_NUMBER('   || UN_NOMBRECAMPO || '))' ||
            ' FROM   '  || UN_NOMBRETABLA ||
            MI_CONDICION  ||
            ' ORDER BY ' || UN_NOMBRECAMPO || ' DESC ';
            EXECUTE IMMEDIATE MI_SQL INTO MI_INICIAL;
    IF UN_INICIAL = NULL THEN
       MI_GENCONSECUTIVOCDP:= 1;
    ELSE
        MI_ANIO_RESULT:= SUBSTR(NVL(UN_INICIAL,
                                    TO_CHAR(SYSDATE, 'YYYY')),
                                    1,
                                    4);
        MI_DIGITOS := LENGTH(UN_INICIAL);
        MI_GENCONSECUTIVOCDP := TO_NUMBER(UN_INICIAL) + 1;
        IF SUBSTR(UN_INICIAL,1,1) = 0 THEN
            MI_CEROS := TRUE;
        ELSE
            MI_CEROS := FALSE;
        END IF;
    END IF;
    IF MI_CEROS = TRUE THEN
        MI_GENCONSECUTIVOCDP:= PCK_SYSMAN_UTL.FC_STRZERO( MI_GENCONSECUTIVOCDP,
                                                          MI_DIGITOS);
    END IF;

  RETURN MI_GENCONSECUTIVOCDP;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ER_BANCOSP_GENCONSECUTIVOCDP);

  END FC_GENCONSECUTIVOCDP;

--4
FUNCTION FC_NOM_TIPONOVEDAD
/*
    NAME              : PR_MAYORIZA_AVANCE  --> EN ACCESS MAYORIZA_AVANCE  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ADRIANA MARITZA CÃ¿CERES BONILLA
    DATE MIGRADOR     : 28/09/2015
    TIME              : 16:58 PM
    SOURCE MODULE     : BANCO_PROYECTOS_CONSULTA.accdb
    MODIFIER          : JUAN SEBASTIAN FORERO NOGUERA
    DATE MODIFIED     : 31/01/2017
    TIME              : 5:15 PM
    DESCRIPTION       : FUNCION QUE RETORNA EL NOMBRE DEL TIPO DE NOVEDAD (FORMULARIO SOLICITUD CDP)
    PARAMETERS        : UN_COMPANIA =>
                        UN_STRTIPOT =>
                        UN_STRCLASET =>
    MODIFICATIONS     : CORRECCION SEGUN ESTANDAR DE PROGRAMACION Y OPTIMIZACION DE MANEJO DE ERRORES.
    @Name: getNombreTipoNovedad
  */
 (
  UN_COMPANIA          IN     PCK_SUBTIPOS.TI_COMPANIA,
  UN_STRTIPOT          IN     VARCHAR2,
  UN_STRCLASET         IN     VARCHAR2
 )
RETURN VARCHAR2
  AS
  MI_ERROR_FUN             PCK_SUBTIPOS.TI_ERROR_FUN;
  MI_NOM_TIPONOVEDAD       VARCHAR2(3000 CHAR);

  BEGIN
      <<RS>>
      FOR RS IN (SELECT NOMBRE
                 FROM BPTIPONOVEDAD
                 WHERE COMPANIA=UN_COMPANIA
                 AND TIPOT=UN_STRTIPOT
                 AND CLASET=UN_STRCLASET)
      LOOP
          MI_NOM_TIPONOVEDAD:=NVL(RS.NOMBRE, '');
      END LOOP RS;

  RETURN MI_NOM_TIPONOVEDAD;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ER_BANCOSP_NOM_TIPONOVEDAD);

  END FC_NOM_TIPONOVEDAD;

--5
PROCEDURE PR_AFECTAR_SOLICITUD
/*
    NAME              : PR_MAYORIZA_AVANCE  --> EN ACCESS MAYORIZA_AVANCE  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ADRIANA MARITZA CÃ¿CERES BONILLA
    DATE MIGRADOR     : 28/09/2015
    TIME              : 16:58 PM
    SOURCE MODULE     : BANCO_PROYECTOS_CONSULTA.accdb
    MODIFIER          : JUAN SEBASTIAN FORERO NOGUERA
    DATE MODIFIED     : 31/01/2017
    TIME              : 5:15 PM
    DESCRIPTION       : FUNCION QUE RETORNA EL NOMBRE DEL TIPO DE NOVEDAD (FORMULARIO SOLICITUD CDP)
    PARAMETERS        : UN_COMPANIA =>
                        UN_STRTIPOT =>
                        UN_STRCLASET =>
    MODIFICATIONS     : CORRECCION SEGUN ESTANDAR DE PROGRAMACION Y OPTIMIZACION DE MANEJO DE ERRORES.
    @Name: afectarNovedad
  */
 (
  UN_COMPANIA            IN   PCK_SUBTIPOS.TI_COMPANIA,
  UN_CLASE               IN   VARCHAR2,
  UN_TIPO                IN   VARCHAR2,
  UN_DEPENDENCIA         IN   VARCHAR2,
  UN_CODIGO              IN   PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_NOVEDADANTERIOR     IN   PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_NOVEDADAFECTAR      IN   PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_USUARIO             IN   PCK_SUBTIPOS.TI_USUARIO

  )
 AS
  MI_ERROR_FUN                  PCK_SUBTIPOS.TI_ERROR_FUN;
  MI_SSQL                       PCK_SUBTIPOS.TI_STRSQL;
  MI_RS                         SYS_REFCURSOR;
  MI_VALORES                    PCK_SUBTIPOS.TI_CAMPOS;
  MI_CAMPOS                     PCK_SUBTIPOS.TI_CAMPOS;
  MI_NUM                        PCK_SUBTIPOS.TI_ENTERO;
  MI_CONDICION                  PCK_SUBTIPOS.TI_CONDICION;
  MI_ELIMINAR                   PCK_SUBTIPOS.TI_ENTERO;
  MI_ACTUALIZAR                 PCK_SUBTIPOS.TI_ENTERO;
  MI_CRITERO                    VARCHAR2(3000 CHAR);

  RS_COMPANIA                   BP_D_NOVEDADPROYECTO.COMPANIA%TYPE;
  RS_TIPOT                      BP_D_NOVEDADPROYECTO.TIPOT%TYPE;
  RS_CLASET                     BP_D_NOVEDADPROYECTO.CLASET%TYPE;
  RS_NOVEDAD                    BP_D_NOVEDADPROYECTO.NOVEDAD%TYPE;
  RS_DEPENDENCIA                BP_D_NOVEDADPROYECTO.DEPENDENCIA%TYPE;
  RS_CODIGO                     BP_D_NOVEDADPROYECTO.CODIGO%TYPE;
  RS_PROYECTO                   BP_D_NOVEDADPROYECTO.PROYECTO%TYPE;
  RS_COMPONENTE                 BP_D_NOVEDADPROYECTO.COMPONENTE%TYPE;
  RS_ACTIVIDAD                  BP_D_NOVEDADPROYECTO.ACTIVIDAD%TYPE;
  RS_PERIODO                    BP_D_NOVEDADPROYECTO.PERIODO%TYPE;
  RS_VALORSOLICITADO            BP_D_NOVEDADPROYECTO.VALORSOLICITADO%TYPE;
  RS_VALORAPROBADO              BP_D_NOVEDADPROYECTO.VALORAPROBADO%TYPE;
  RS_ESPECIFICACION             BP_D_NOVEDADPROYECTO.ESPECIFICACION%TYPE;
  RS_SOLOCOMPONENTE             BP_D_NOVEDADPROYECTO.SOLOCOMPONENTE%TYPE;
  RS_PRIORIDAD                  BP_D_NOVEDADPROYECTO.PRIORIDAD%TYPE;
  RS_FUENTERECURSOS             BP_D_NOVEDADPROYECTO.FUENTERECURSOS%TYPE;
  RS_LOCALIZACION               BP_D_NOVEDADPROYECTO.LOCALIZACION%TYPE;
  RS_RUBROPRESUPUESTAL          BP_D_NOVEDADPROYECTO.RUBROPRESUPUESTAL%TYPE;
  RS_ANORUBRO                   BP_D_NOVEDADPROYECTO.ANORUBRO%TYPE;
  RS_ID_META_PRODUCTO           BP_D_NOVEDADPROYECTO.ID_META_PRODUCTO%TYPE;
  RS_CANTIDAD                   BP_D_NOVEDADPROYECTO.CANTIDAD%TYPE;
  RS_VIGENCIA_PLAN_INDICATIVO   BP_D_NOVEDADPROYECTO.VIGENCIA_PLAN_INDICATIVO%TYPE;
  RS_TIPOCOMPONENTE             BP_D_NOVEDADPROYECTO.TIPOCOMPONENTE%TYPE;
  RS_CANTIDAD_PLAN              BP_D_NOVEDADPROYECTO.CANTIDAD_PLAN%TYPE;
  RS_PAIS_L                     BP_D_NOVEDADPROYECTO.PAIS_L%TYPE;
  RS_DEPARTAMENTO_L             BP_D_NOVEDADPROYECTO.DEPARTAMENTO_L%TYPE;
  RS_CIUDAD_L                   BP_D_NOVEDADPROYECTO.CIUDAD_L%TYPE;
  RS_BARRIO_L                   BP_D_NOVEDADPROYECTO.BARRIO_L%TYPE;
  RS_TIPO_INDICADOR             BP_D_NOVEDADPROYECTO.TIPOINDICADOR%TYPE;
  RS_INDICADOR                  BP_D_NOVEDADPROYECTO.INDICADOR%TYPE;
  RS_VALORAFECTADO              BP_D_NOVEDADPROYECTO.VALORAFECTADO%TYPE;
  RS_TIPO_CPTE_AFECT            BP_D_NOVEDADPROYECTO.TIPO_CPTE_AFECT%TYPE;
  RS_CMPTE_AFECTADO             BP_D_NOVEDADPROYECTO.CMPTE_AFECTADO%TYPE;
  RS_ITEM_AFECT                 BP_D_NOVEDADPROYECTO.ITEM_AFECT%TYPE;
  RS_RESPONSABLE                BPNOVEDADPROYECTO.RESPONSABLE%TYPE;
  RS_CARGORESPONSABLE           BPNOVEDADPROYECTO.CARGORESPONSABLE%TYPE;
  RS_ESTADO                     BPNOVEDADPROYECTO.ESTADO%TYPE;
  RS_TIEMPOEJECUCION            BPNOVEDADPROYECTO.TIEMPOEJECUCION%TYPE;


BEGIN
  --Lbotia, este proceso se ejecuta al cambiar el Documento Afectado, ver : PR_HEREDARSOLICITUDSCD

 /** MI_CONDICION := 'TIPOT      = '''||UN_TIPO||'''
                  AND CLASET  = '''||UN_CLASE||'''
                  AND NOVEDAD = '||UN_CODIGO||'';
  BEGIN                
      BEGIN
        MI_ELIMINAR := PCK_DATOS.FC_ACME(UN_TABLA     => 'BP_D_NOVEDADPROYECTO',
                                         UN_ACCION    => 'E',
                                         UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
      END ;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERR_BANCOSP_ELIMINAR_DET);
  END;
   */
  BEGIN
      BEGIN
        MI_ACTUALIZAR:=PCK_DATOS.FC_ACME(UN_TABLA     => 'BPNOVEDADPROYECTO',
                                         UN_ACCION    => 'M',
                                         UN_CAMPOS    => 'AFECTADO      = 0 ,                                                           
                                                          DATE_MODIFIED = SYSDATE,
                                                          MODIFIED_BY   = '''||UN_USUARIO||'''',

                                         UN_CONDICION => 'COMPANIA= '''||UN_COMPANIA||'''
                                                      AND CODIGO= '||UN_NOVEDADANTERIOR||'');

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
      END ;
   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD =>SQLCODE,
                  UN_ERROR_COD=>PCK_ERRORES.ERR_BANCOSP_ACTUA_AFECT);
   END;


  MI_CRITERO:=      'BPNOVEDADPROYECTO.TIPOT      = '''||UN_TIPO||'''
              AND   BPNOVEDADPROYECTO.CLASET      = '''||UN_CLASE||'''
              AND   BPNOVEDADPROYECTO.DEPENDENCIA = '''||UN_DEPENDENCIA||'''';

  MI_SSQL:='SELECT  BP_D_NOVEDADPROYECTO.COMPANIA,
                    BP_D_NOVEDADPROYECTO.TIPOT,
                    BP_D_NOVEDADPROYECTO.CLASET,
                    BP_D_NOVEDADPROYECTO.NOVEDAD,
                    BP_D_NOVEDADPROYECTO.DEPENDENCIA,
                    BP_D_NOVEDADPROYECTO.CODIGO,
                    BP_D_NOVEDADPROYECTO.PROYECTO,
                    BP_D_NOVEDADPROYECTO.COMPONENTE,
                    BP_D_NOVEDADPROYECTO.ACTIVIDAD,
                    BP_D_NOVEDADPROYECTO.PERIODO,
                    BP_D_NOVEDADPROYECTO.VALORSOLICITADO,
                    BP_D_NOVEDADPROYECTO.VALORAPROBADO,
                    BP_D_NOVEDADPROYECTO.ESPECIFICACION,
                    BP_D_NOVEDADPROYECTO.SOLOCOMPONENTE,
                    BP_D_NOVEDADPROYECTO.PRIORIDAD,
                    BP_D_NOVEDADPROYECTO.FUENTERECURSOS,
                    BP_D_NOVEDADPROYECTO.LOCALIZACION,
                    BP_D_NOVEDADPROYECTO.RUBROPRESUPUESTAL,
                    BP_D_NOVEDADPROYECTO.ANORUBRO,
                    BP_D_NOVEDADPROYECTO.ID_META_PRODUCTO,
                    BP_D_NOVEDADPROYECTO.CANTIDAD,
                    BP_D_NOVEDADPROYECTO.VIGENCIA_PLAN_INDICATIVO,
                    BP_D_NOVEDADPROYECTO.TIPOCOMPONENTE,
                    BP_D_NOVEDADPROYECTO.CANTIDAD_PLAN,
                    BP_D_NOVEDADPROYECTO.PAIS_L,
                    BP_D_NOVEDADPROYECTO.DEPARTAMENTO_L,
                    BP_D_NOVEDADPROYECTO.CIUDAD_L,
                    BP_D_NOVEDADPROYECTO.BARRIO_L,
                    BP_D_NOVEDADPROYECTO.TIPOINDICADOR,
                    BP_D_NOVEDADPROYECTO.INDICADOR,
                    BP_D_NOVEDADPROYECTO.VALORAFECTADO,
                    BP_D_NOVEDADPROYECTO.TIPO_CPTE_AFECT,
                    BP_D_NOVEDADPROYECTO.CMPTE_AFECTADO,
                    BP_D_NOVEDADPROYECTO.ITEM_AFECT,
                    BPNOVEDADPROYECTO.RESPONSABLE,
                    BPNOVEDADPROYECTO.CARGORESPONSABLE,
                    BPNOVEDADPROYECTO.ESTADO,
                    BPNOVEDADPROYECTO.TIEMPOEJECUCION
               FROM BP_D_NOVEDADPROYECTO
               LEFT JOIN BPNOVEDADPROYECTO
                 ON (BP_D_NOVEDADPROYECTO.DEPENDENCIA = BPNOVEDADPROYECTO.DEPENDENCIA)
                AND (BP_D_NOVEDADPROYECTO.NOVEDAD     = BPNOVEDADPROYECTO.CODIGO)
                AND (BP_D_NOVEDADPROYECTO.CLASET      = BPNOVEDADPROYECTO.CLASET)
                AND (BP_D_NOVEDADPROYECTO.TIPOT       = BPNOVEDADPROYECTO.TIPOT)
                AND (BP_D_NOVEDADPROYECTO.COMPANIA    = BPNOVEDADPROYECTO.COMPANIA)
              WHERE '||MI_CRITERO||'
                AND BP_D_NOVEDADPROYECTO.COMPANIA='''||UN_COMPANIA||'''
                AND NOVEDAD='||UN_NOVEDADAFECTAR||'';

    <<MI_RS_SSQL>>
  OPEN MI_RS FOR MI_SSQL;
    LOOP
      FETCH MI_RS INTO RS_COMPANIA,
                       RS_TIPOT,
                       RS_CLASET,
                       RS_NOVEDAD,
                       RS_DEPENDENCIA,
                       RS_CODIGO,
                       RS_PROYECTO,
                       RS_COMPONENTE,
                       RS_ACTIVIDAD,
                       RS_PERIODO,
                       RS_VALORSOLICITADO,
                       RS_VALORAPROBADO,
                       RS_ESPECIFICACION,
                       RS_SOLOCOMPONENTE,
                       RS_PRIORIDAD,
                       RS_FUENTERECURSOS,
                       RS_LOCALIZACION,
                       RS_RUBROPRESUPUESTAL,
                       RS_ANORUBRO,
                       RS_ID_META_PRODUCTO,
                       RS_CANTIDAD,
                       RS_VIGENCIA_PLAN_INDICATIVO,
                       RS_TIPOCOMPONENTE,
                       RS_CANTIDAD_PLAN,
                       RS_PAIS_L,
                       RS_DEPARTAMENTO_L,
                       RS_CIUDAD_L,
                       RS_BARRIO_L,
                       RS_TIPO_INDICADOR,
                       RS_INDICADOR,
                       RS_VALORAFECTADO,
                       RS_TIPO_CPTE_AFECT,
                       RS_CMPTE_AFECTADO,
                       RS_ITEM_AFECT,
                       RS_RESPONSABLE,
                       RS_CARGORESPONSABLE,
                       RS_ESTADO,
                       RS_TIEMPOEJECUCION;

      EXIT WHEN MI_RS%NOTFOUND;
       MI_CAMPOS:= 'COMPANIA,
                  TIPOT,
                  CLASET,
                  NOVEDAD,
                  DEPENDENCIA,
                  CODIGO,
                  PROYECTO,
                  COMPONENTE,
                  ACTIVIDAD,
                  PERIODO,
                  VALORSOLICITADO,
                  VALORAPROBADO,
                  ESPECIFICACION,
                  SOLOCOMPONENTE,
                  PRIORIDAD,
                  FUENTERECURSOS,
                  LOCALIZACION,
                  RUBROPRESUPUESTAL,
                  ANORUBRO,
                  ID_META_PRODUCTO,
                  CANTIDAD,
                  VIGENCIA_PLAN_INDICATIVO,
                  TIPOCOMPONENTE,
                  CANTIDAD_PLAN,
                  PAIS_L,
                  DEPARTAMENTO_L,
                  CIUDAD_L,
                  BARRIO_L,
                  TIPOINDICADOR,
                  INDICADORM,
                  DATE_CREATE,
                  CREATE_BY';

      MI_VALORES:=''''||UN_COMPANIA||''',
                  '''||UN_TIPO||''',
                  '''||UN_CLASE||''',
                  '||UN_CODIGO||',
                  '''||RS_DEPENDENCIA||''',
                  '||RS_CODIGO||',
                  '''||RS_PROYECTO||''',
                  '''||RS_COMPONENTE||''',
                  '''||NVL(RS_ACTIVIDAD, '')||''',
                  '''||RS_PERIODO||''',
                  '||NVL(RS_VALORSOLICITADO, 0)||',0,
                  '''||RS_ESPECIFICACION||''',
                  '||RS_SOLOCOMPONENTE||',
                  '||NVL(RS_PRIORIDAD, 0)||',
                  '''||RS_FUENTERECURSOS||''',
                  '''||RS_LOCALIZACION||''',
                  '''||RS_RUBROPRESUPUESTAL||''',
                  '||NVL(RS_ANORUBRO, 0)||',
                  '''||RS_ID_META_PRODUCTO||''',
                  '||NVL(RS_CANTIDAD, 0)||',
                  '||RS_VIGENCIA_PLAN_INDICATIVO||',
                  '''||RS_TIPOCOMPONENTE||''',
                  '||RS_CANTIDAD_PLAN||',
                  '''||RS_PAIS_L||''',
                  '''||RS_DEPARTAMENTO_L||''',
                  '''||RS_CIUDAD_L||''',
                  '''||RS_BARRIO_L||''',
                  '''||RS_TIPO_INDICADOR||''',
                  '''||RS_INDICADOR||''',
                  SYSDATE,
                  '''||UN_USUARIO||'''';

      BEGIN 
          BEGIN
            MI_NUM:=PCK_DATOS.FC_ACME(UN_TABLA   =>  'BP_D_NOVEDADPROYECTO',
                                      UN_ACCION  =>  'I',
                                      UN_CAMPOS  =>  MI_CAMPOS,
                                      UN_VALORES =>  MI_VALORES);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
          END;
     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERR_BANCOSP_INSER_DET);
     END;

      RS_VALORAFECTADO   := NVL(RS_VALORSOLICITADO, 0);
      RS_TIPO_CPTE_AFECT := UN_TIPO;
      RS_CMPTE_AFECTADO  := UN_CODIGO;
      RS_ITEM_AFECT      := RS_CODIGO;
      END LOOP MI_RS_SSQL;
      BEGIN
          BEGIN
           MI_ACTUALIZAR:=PCK_DATOS.FC_ACME(UN_TABLA  =>  'BPNOVEDADPROYECTO',
                                            UN_ACCION =>  'M',
                                            UN_CAMPOS =>  'AFECTADO      = -1,                                                           
                                                           DATE_MODIFIED = SYSDATE,
                                                           MODIFIED_BY   ='''||UN_USUARIO||'''',

                                            UN_CONDICION =>'COMPANIA='||UN_COMPANIA||' 
                                                        AND CODIGO='||UN_NOVEDADAFECTAR||'');

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
          END ;

       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ER_BANCOSP_AFECTAR_SOLICITUD);
      END;


 END PR_AFECTAR_SOLICITUD;

  --6
FUNCTION FC_CANT_PLAN_INDICATIVO
     /*
        NAME              : PR_MAYORIZA_AVANCE  --> EN ACCESS MAYORIZA_AVANCE  -
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : ADRIANA MARITZA CÃ¿CERES BONILLA
        DATE MIGRADOR     : 28/09/2015
        TIME              : 16:58 PM
        SOURCE MODULE     : BANCO_PROYECTOS_CONSULTA.accdb
        MODIFIER          : JUAN SEBASTIAN FORERO NOGUERA
        DATE MODIFIED     : 03/02/2017
        TIME              : 14:00
        DESCRIPTION       : Permite Mayorizar Avance de la meta producto
        PARAMETERS        :  UN_COMPANIA => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                             UN_META     => PARAMETRO QUE CONDICIONA EL SELECT
                             UN_VIGENCIA_PLAN   => PARAMETRO QUE CONDICIONA EL SELECT
                             UN_VIGENCIA_META   => PARAMETRO QUE CONDICIONA EL SELECT
        MODIFICATIONS     : CORRECCION SEGUN ESTANDAR DE PROGRAMACION Y OPTIMIZACION DE MANEJO DE ERRORES.
        DESCRIPTION       : FUNCION QUE GENERA LAS CANTIDADES INICIALES DE CADA ACTIVIDAD
        @Name: saldoPlanIndicativoMeta
      */
     (
      UN_COMPANIA               IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_META                   IN VARCHAR2,
      UN_VIGENCIA_PLAN          IN NUMBER,
      UN_VIGENCIA_META          IN NUMBER
     )
     RETURN PCK_SUBTIPOS.TI_DOBLE
     AS         
     MI_CANT_PLAN_INDICATIVO    NUMBER(20,2);
         
     BEGIN
   
      BEGIN  
         SELECT SUM(NVL(CANTIDAD_PROGRAMADA,0)-NVL(CANTIDAD_EJECUTADA,0)) INICIAL
         INTO MI_CANT_PLAN_INDICATIVO
                  FROM BP_PLAN_INDICATIVO_METAS
                  WHERE COMPANIA = UN_COMPANIA
                  AND ID_PLAN = UN_META
                  AND  VIGENCIA_PLAN = UN_VIGENCIA_PLAN
                  AND  VIGENCIA_META = UN_VIGENCIA_META   ;
         
         EXCEPTION WHEN NO_DATA_FOUND     THEN
         MI_CANT_PLAN_INDICATIVO := 0;
     
     END;
     
     RETURN MI_CANT_PLAN_INDICATIVO;

END FC_CANT_PLAN_INDICATIVO;
  --7
  PROCEDURE PR_CREARFICHATECNICA 
       /*
          NAME              : PR_CREARFICHATECNICA
          AUTHORS           : SYSMAN  SAS
          AUTHOR MIGRACION  : SERGIO ESTEBAN PIÑA VARGAS
          DATE MIGRADOR     : 18/09/2017
          TIME              : 16:58 PM
          SOURCE MODULE     : SysmanBP2015.07.01.accdb
          MODIFIER          : 
          DATE MODIFIED     : 
          TIME              : 
          DESCRIPTION       : Registrar un nueva ficha tecnica en BP_FICHA_TECNICA y en BP_D_FICHA_TECNICA
          PARAMETERS        : UN_COMPANIA => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                              UN_PROYECTO => VALOR PROYECTO A REGISTRAR
                              UN_SECTOR   => VALOR SECTOR A REGISTRAR
                              UN_USUARIO  => USUARIO CON SESION EN LA APLICACION
          MODIFICATIONS     : 
          DESCRIPTION       : 
          @Name: crearFichaTecnica
        */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROYECTO IN BP_FICHA_TECNICA.PROYECTO%TYPE,
    UN_SECTOR   IN BP_FICHA_TECNICA.SECTOR%TYPE,
    UN_USUARIO  IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES    PCK_SUBTIPOS.TI_VALORES;
  BEGIN
    MI_CAMPOS         := 'COMPANIA, 
                          PROYECTO, 
                          SECTOR, 
                          CUMPLE, 
                          CREATED_BY, 
                          DATE_CREATED';
    MI_VALORES        := ' '''|| UN_COMPANIA ||''',
                          '''|| UN_PROYECTO ||''',
                          '''|| UN_SECTOR ||''',
                          0,
                          '''|| UN_USUARIO ||''',
                          SYSDATE
                         ';
    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => 'BP_FICHA_TECNICA' ,
                                              UN_ACCION   => 'I' ,
                                              UN_CAMPOS   => MI_CAMPOS ,
                                              UN_VALORES  => MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD     => SQLCODE
              ,UN_ERROR_COD  => PCK_ERRORES.ER_BANCOSP_INSER_FICHA
        );
    END;

    MI_CAMPOS        := 'COMPANIA, 
                         PROYECTO, 
                         SECTOR, 
                         CODIGO_DET,  
                         SECCION, 
                         ITEM, 
                         APLICA, 
                         CUMPLE, 
                         CREATED_BY, 
                         DATE_CREATED';
    MI_VALORES       := ' SELECT M.COMPANIA, 
                          '''|| UN_PROYECTO ||''' AS PROYECTO, 
                          M.SECTOR, 
                          M.CODIGO, 
                          M.SECCION, 
                          M.ITEM, 
                          0 AS APLICA, 
                          0 AS CUMPLE,
                          '''|| UN_USUARIO ||''' AS CREATED_BY, 
                          SYSDATE AS DATE_CREATED
                          FROM BP_MODELO_FICHA_TECNICA M
                          WHERE M.COMPANIA  = '''|| UN_COMPANIA ||'''
                          AND M.SECTOR      = '''|| UN_SECTOR ||'''
                         ';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => 'BP_D_FICHA_TECNICA' ,
                                              UN_ACCION   => 'IS' ,
                                              UN_CAMPOS   => MI_CAMPOS ,
                                              UN_VALORES  => MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD     => SQLCODE
              ,UN_ERROR_COD  => PCK_ERRORES.ER_BANCOSP_INSER_FICHA
        );
    END;
  END PR_CREARFICHATECNICA;


FUNCTION FC_GENERAR_CONSULTACRITICA
  /*
  NAME              : Controlador-java FrmPlanIndxNivelControlador  
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : JUAN CAMILO RODRIGUEZ DIAZ
  DATE MIGRADOR     : 21/09/2017
  TIME              : 08:57 AM
  SOURCE MODULE     : BANCO_PROYECTOS_CONSULTA.accdb
  MODIFIER          : 
  DATE MODIFIED     : 21/09/2017
  TIME              : 14:00
  DESCRIPTION       : Permite enviar una consulta para el reporte consulta 000182RptPlanAvancecriterio
  PARAMETERS        :  
                     UN_COMPANIA => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                     UN_ANO      => ANO DEL NIVEL DE CUMPLIMIENTO
  MODIFICATIONS     :
  DESCRIPTION       : FUNCION QUE GENERA UNA CONSULTA BASE
  @Name: generarConsultaCriticos
  */
  (
    UN_COMPANIA  IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO       IN  PCK_SUBTIPOS.TI_ANIO 
  )
  RETURN PCK_SUBTIPOS.TI_STRSQL
AS 
  MI_LIM_INFERIOR PCK_SUBTIPOS.TI_ENTERO;
  MI_LIM_SUPERIOR PCK_SUBTIPOS.TI_ENTERO;
  MI_NIVEL_CUMPLI PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONCATSQL    PCK_SUBTIPOS.TI_STRSQL;
  MI_SQLAUX       PCK_SUBTIPOS.TI_STRSQL;
  MI_CONTAR       PCK_SUBTIPOS.TI_ENTERO;
  MI_LONGITUD     PCK_SUBTIPOS.TI_ENTERO;
BEGIN
  MI_CONTAR:=1;
  MI_LONGITUD:=0;
  MI_CONCATSQL:= ' SELECT PLAN.COMPANIA,'||
                  ' PLAN.ID,'||
                  ' PLAN.VIGENCIA_INICIAL,'||
                  ' PLAN.PONDERACION,'||
                  ' PLAN.AVANCE,'||
                  ' 1 AS CUENTO, ';
    SELECT COUNT(*) NUMERO INTO MI_LONGITUD
      FROM BP_NIVEL_CUMPLIMIENTO
      WHERE COMPANIA = UN_COMPANIA
      AND VIGENCIA   = UN_ANO;


  <<NIVEL>>
  FOR MI_RS  IN
  (
        SELECT COMPANIA,
        VIGENCIA,
        NVL(LIM_SUPERIOR, 0) LIM_SUPERIOR,
        NVL(LIM_INFERIOR,0) LIM_INFERIOR,
        NIVEL_CUMPLI
      FROM BP_NIVEL_CUMPLIMIENTO
      WHERE COMPANIA = UN_COMPANIA
      AND VIGENCIA   = UN_ANO
      ORDER BY LIM_INFERIOR ASC
  )
  LOOP  
      IF MI_CONTAR!=MI_LONGITUD THEN 
          MI_CONCATSQL:=MI_CONCATSQL||
                        ' CASE WHEN (PLAN.AVANCE >= '||MI_RS.LIM_INFERIOR||
                        ' AND PLAN.AVANCE < '||MI_RS.LIM_SUPERIOR||
                        ' ) THEN '''||MI_RS.NIVEL_CUMPLI ||''' ELSE ';
      END IF;

      MI_SQLAUX := MI_SQLAUX || ' END ';

      MI_LIM_INFERIOR:=MI_RS.LIM_INFERIOR;
      MI_LIM_SUPERIOR:=MI_RS.LIM_SUPERIOR;
      MI_NIVEL_CUMPLI:=MI_RS.NIVEL_CUMPLI;
      MI_CONTAR:=MI_CONTAR+1;

    END LOOP NIVEL;
    MI_CONCATSQL :=MI_CONCATSQL||' CASE WHEN (PLAN.AVANCE >= '||
                   MI_LIM_INFERIOR ||' AND PLAN.AVANCE < '||
                   MI_LIM_SUPERIOR ||') THEN '''||
                   MI_NIVEL_CUMPLI ||'''';

    MI_CONCATSQL :=MI_CONCATSQL||MI_SQLAUX||' CUMPLIMIENTO';

    MI_CONCATSQL := MI_CONCATSQL ||' FROM BP_PLAN_INDICATIVO PLAN '||
                   ' WHERE PLAN.COMPANIA   = '''||UN_COMPANIA||''' AND '||
                   ' PLAN.VIGENCIA_INICIAL ='||UN_ANO||' AND '||
                   ' PLAN.TIPO_META_PLAN=''002''';    

  RETURN MI_CONCATSQL;

END FC_GENERAR_CONSULTACRITICA;


--
FUNCTION FC_VERICAR_REG_SECUNDA_PLANIND
/*
    NAME              : FC_VALIDAR_AFECTADOS  --> 
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JONATHAN ENRIQUE GUERRERO TORRES
    DATE MIGRADOR     : 18/10/2017
    TIME              : 12:35 PM
    SOURCE MODULE     : BANCO_PROYECTOS
    DESCRIPTION       : Valida si un codigo a eliminar tiene hijos
    PARAMETERS        : UN_COMPANIA  => COMPANIA INGRESO DEL SISTEMA
                        UN_ANO       => ANO DE LA SOLICITUD
                        UN_SOLICITUD => SOLICITUD QUE FUE SE QUIERE ELIMINAR

  */
(
 UN_COMPANIA        PCK_SUBTIPOS.TI_COMPANIA,
 UN_ANO             PCK_SUBTIPOS.TI_ANIO,
 UN_ID              BP_PLAN_INDICATIVO.ID%TYPE
)
RETURN PCK_SUBTIPOS.TI_LOGICO AS 
MI_RTA              PCK_SUBTIPOS.TI_ENTERO_LARGO;
MI_CONSULTA         PCK_SUBTIPOS.TI_STRSQL;
BEGIN

  MI_CONSULTA := 'SELECT COUNT (*) CANTIDAD
                    FROM BP_PLAN_INDICATIVO
                   WHERE COMPANIA         = '''|| UN_COMPANIA||'''
                     AND VIGENCIA_INICIAL = '|| UN_ANO||'
                     AND '''||UN_ID||'''  =  SUBSTR(ID,1,LENGTH('''||UN_ID||'''))
                     AND ID               <> '''||UN_ID||'''';
  EXECUTE IMMEDIATE MI_CONSULTA INTO MI_RTA;

  IF MI_RTA >= 1 THEN 
    RETURN -1;
  ELSE 
    RETURN 0;
  END IF;
END FC_VERICAR_REG_SECUNDA_PLANIND;

--10
  FUNCTION FC_GENERARMETABRUTA 
    /*
      NAME              : FC_GENERARMETABRUTA --> En access: Genera_MBruta()
      AUTHORS           : STEFANINI SYSMAN SAS
      AUTHOR MIGRACION  : AURA LILIANA MONROY GARCIA
      DATE MIGRADOR     : 07/11/2017
      TIME              : 16:11 
      SOURCE MODULE     : BANCO_PROYECTOS_PROCESOS
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      MODIFICATIONS     : 
      DESCRIPTION       : Genera el valor de la meta bruta en la meta a registrar 
      PARAMETERS        : UN_COMPANIA        => Compañia de ingreso a la aplicación
                          UN_IDPLAN          => Plan Indicativo en el que se adicionará la meta
                          UN_VIGENCIAPLAN    => Vigencia Inicial del Plan Indicativo que se está trabajando
                          UN_VIGENCIAMETA    => Vigencia de la Meta a registrar 	 
                          UN_APROGRAMAR      => Valor de la Meta Programada																		

      @NAME:    generarMetaBruta
      @METHOD:  GET    
    */ 
(
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_IDPLAN          IN BP_PLAN_INDICATIVO.ID%TYPE,
    UN_VIGENCIAPLAN    IN PCK_SUBTIPOS.TI_ANIO,
    UN_VIGENCIAMETA    IN PCK_SUBTIPOS.TI_ANIO,
    UN_APROGRAMAR      IN PCK_SUBTIPOS.TI_DOBLE
  )
  RETURN VARCHAR2
  AS 
    MI_RS             SYS_REFCURSOR;
    MI_SQLRS          PCK_SUBTIPOS.TI_STRSQL;
    MI_TIPOMETA       PCK_SUBTIPOS.TI_NUMORDEN;
    MI_LINEABASE      PCK_SUBTIPOS.TI_DOBLE;
    MI_META           PCK_SUBTIPOS.TI_DOBLE;
    MI_CNTPROG        PCK_SUBTIPOS.TI_DOBLE;
    MI_VIGMETAAUX     PCK_SUBTIPOS.TI_ANIO;
    MI_METABRUTA      PCK_SUBTIPOS.TI_DOBLE;
    MI_ACUMBRUTA      PCK_SUBTIPOS.TI_DOBLE;
    MI_RTA            PCK_SUBTIPOS.TI_STRSQL;
    MI_CONTADORAUX    PCK_SUBTIPOS.TI_ENTERO := 0;
    MI_ACTUALIZAR     PCK_SUBTIPOS.TI_NUMORDEN;
    MI_TOTALMETAS     PCK_SUBTIPOS.TI_ENTERO;

  BEGIN
    -- Obtiene el total de metas programadas hasta el momento por Plan Indicativo
    SELECT COUNT(*) TOTAL
    INTO MI_TOTALMETAS
    FROM BP_PLAN_INDICATIVO P
      LEFT JOIN BP_PLAN_INDICATIVO_METAS  M
        ON P.COMPANIA         = M.COMPANIA
       AND P.ID               = M.ID_PLAN
       AND P.VIGENCIA_INICIAL = M.VIGENCIA_PLAN
    WHERE P.COMPANIA          = UN_COMPANIA
      AND P.ID                = UN_IDPLAN
    ORDER BY 
      M.VIGENCIA_META;

    MI_SQLRS := ' SELECT  ' ||
                '    P.TIPO_META_INDICADOR, ' ||
                '    P.LB, ' ||
                '    P.META, ' ||
                '    M.CANTIDAD_PROGRAMADA, ' ||
                '    M.VIGENCIA_META, ' ||
                '    M.META_BRUTA ' ||
                ' FROM BP_PLAN_INDICATIVO P ' ||
                '   LEFT JOIN BP_PLAN_INDICATIVO_METAS  M ' ||
                '     ON P.COMPANIA         = M.COMPANIA ' ||
                '    AND P.ID               = M.ID_PLAN ' ||
                '    AND P.VIGENCIA_INICIAL = M.VIGENCIA_PLAN ' ||
                ' WHERE P.COMPANIA          = ''' || UN_COMPANIA || ''' ' ||
                '   AND P.ID                = ''' || UN_IDPLAN   || ''' ' ||
                '   AND P.VIGENCIA_INICIAL  =   ' || UN_VIGENCIAPLAN || ' ' ||
                ' ORDER BY  ' ||
                '   M.VIGENCIA_META ';

    OPEN MI_RS FOR MI_SQLRS;
      LOOP
        FETCH MI_RS INTO MI_TIPOMETA,
                         MI_LINEABASE,
                         MI_META,
                         MI_CNTPROG,
                         MI_VIGMETAAUX,
                         MI_METABRUTA;
        EXIT WHEN MI_RS%NOTFOUND;         

          MI_ACUMBRUTA := CASE MI_TIPOMETA 
                               WHEN 'MI' THEN MI_LINEABASE + UN_APROGRAMAR
                               WHEN 'MR' THEN MI_LINEABASE - UN_APROGRAMAR
                               WHEN 'MM' THEN MI_META 
                               WHEN 'MG' THEN 0
                          END;      

          -- Cuando es la primera meta a registrar
          IF MI_CNTPROG IS NULL THEN
            MI_RTA        := MI_ACUMBRUTA ;
            MI_ACTUALIZAR := '%NO';
          ELSE
            MI_CONTADORAUX := MI_CONTADORAUX + 1;
            IF MI_VIGMETAAUX < UN_VIGENCIAMETA THEN 
              MI_ACUMBRUTA := CASE MI_TIPOMETA 
                               WHEN 'MI' THEN MI_METABRUTA + UN_APROGRAMAR
                               WHEN 'MR' THEN MI_METABRUTA - UN_APROGRAMAR
                               WHEN 'MM' THEN MI_META 
                               WHEN 'MG' THEN 0
                              END;
              MI_RTA := MI_ACUMBRUTA;

              -- Valida si es el ultimo anio(vigencia) a registrar en las metas relacionadas con ese Plan
              -- Debo agregar bandera para actualizar los registros de anios posteriores
              IF MI_TOTALMETAS = MI_CONTADORAUX THEN
                MI_ACTUALIZAR := '%NO';
              ELSE
                MI_ACTUALIZAR := '%SI';
              END IF;

            ELSE            
              IF  MI_ACTUALIZAR = '%NO' THEN 
                MI_RTA        := MI_ACUMBRUTA;
              ELSIF MI_CONTADORAUX = 1 THEN 
                MI_RTA        := MI_ACUMBRUTA;
                MI_ACTUALIZAR := '%SI'; 
              ELSIF MI_CONTADORAUX > 1 THEN 
                 MI_ACTUALIZAR := '%SI';         
              END IF;
            END IF;
          END IF;                        
      END LOOP;    
      MI_RTA := MI_RTA || '' || MI_ACTUALIZAR;
    CLOSE MI_RS;  
    RETURN MI_RTA;
  END FC_GENERARMETABRUTA;

--11
  PROCEDURE PR_ACTUALIZARMETABRUTA 
    /*
      NAME              : PR_ACTUALIZARMETABRUTA  
      AUTHORS           : STEFANINI SYSMAN SAS
      AUTHOR MIGRACION  : AURA LILIANA MONROY GARCIA
      DATE MIGRADOR     : 08/11/2017
      TIME              : 09:32 AM 
      SOURCE MODULE     : BANCO_PROYECTOS_PROCESOS
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      MODIFICATIONS     : 
      DESCRIPTION       : Actualiza el valor de la Meta Bruta para años posteriores a una Meta registrada o actualizada
      PARAMETERS        : UN_COMPANIA        => Compañia de ingreso a la aplicación
                          UN_IDPLAN          => Plan Indicativo en el que se adicionará la meta
                          UN_VIGENCIAPLAN    => Vigencia Inicial del Plan Indicativo que se está trabajando
                          UN_VIGENCIAMETA    => Vigencia de la Meta a registrar 	 
                          UN_USUARIO         => Usuario que ingresa al sistema y ejecuta el procedimiento

      @NAME:    actualizarMetaBruta
      @METHOD:  PUT 
    */ 
  (
    UN_COMPANIA          IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_IDPLAN            IN BP_PLAN_INDICATIVO.ID%TYPE,
    UN_VIGENCIAPLAN      IN PCK_SUBTIPOS.TI_ANIO,
    UN_VIGENCIAMETA      IN PCK_SUBTIPOS.TI_ANIO,
    UN_USUARIO           IN PCK_SUBTIPOS.TI_USUARIO 
  )
  AS 
    MI_RS                 SYS_REFCURSOR;
    MI_TABLA              PCK_SUBTIPOS.TI_TABLA; 
    MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS; 
    MI_RTA                PCK_SUBTIPOS.TI_RTA_ACME;
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_METABRUTA          PCK_SUBTIPOS.TI_DOBLE;
    MI_MBGENERADA         PCK_SUBTIPOS.TI_STRSQL;
  BEGIN
    <<ACTUALIZA_META_BRUTA>>  
    FOR MI_RS IN (  SELECT 
                      M.CANTIDAD_PROGRAMADA,
                      M.VIGENCIA_META 
                    FROM BP_PLAN_INDICATIVO P
                      LEFT JOIN BP_PLAN_INDICATIVO_METAS  M
                        ON P.COMPANIA         = M.COMPANIA
                       AND P.ID               = M.ID_PLAN
                       AND P.VIGENCIA_INICIAL = M.VIGENCIA_PLAN
                    WHERE P.COMPANIA          = UN_COMPANIA 
                      AND P.ID                = UN_IDPLAN
                      AND M.VIGENCIA_META     > UN_VIGENCIAMETA
                    ORDER BY 
                      M.VIGENCIA_META)
    LOOP    
      MI_MBGENERADA := PCK_BANCOS_PROY5.FC_GENERARMETABRUTA( UN_COMPANIA      => UN_COMPANIA,
                                                             UN_IDPLAN        => UN_IDPLAN,
                                                             UN_VIGENCIAPLAN  => UN_VIGENCIAPLAN,
                                                             UN_VIGENCIAMETA  => MI_RS.VIGENCIA_META,
                                                             UN_APROGRAMAR    => MI_RS.CANTIDAD_PROGRAMADA);

      MI_METABRUTA := TO_NUMBER(SUBSTR(MI_MBGENERADA,0,LENGTH(MI_MBGENERADA)-3));

      MI_TABLA      := 'BP_PLAN_INDICATIVO_METAS';
      MI_CAMPOS     := 'META_BRUTA        = ' || MI_METABRUTA || ', '||
                       'DATE_MODIFIED     = SYSDATE, '||
                       'MODIFIED_BY       = ''' || UN_USUARIO || ''' ';    
      MI_CONDICION  := '     COMPANIA      = ''' || UN_COMPANIA || ''' '||
                       ' AND ID_PLAN       = ''' || UN_IDPLAN || ''' '||
                       ' AND VIGENCIA_PLAN =   ' || UN_VIGENCIAPLAN || ' '||
                       ' AND VIGENCIA_META =   ' || MI_RS.VIGENCIA_META;

        BEGIN
          BEGIN
            MI_RTA     := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                             UN_ACCION    => 'M', 
                                             UN_CAMPOS    => MI_CAMPOS, 
                                             UN_CONDICION => MI_CONDICION); 		
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
               MI_MSGERROR(1).CLAVE := 'CODIGO';
               MI_MSGERROR(1).VALOR :=  UN_IDPLAN;
               MI_MSGERROR(2).CLAVE := 'VIGENCIA';
               MI_MSGERROR(2).VALOR :=  MI_RS.VIGENCIA_META;
               RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD 		=>	SQLCODE,
              UN_ERROR_COD	=>	PCK_ERRORES.ERR_BANCOP_ACT_METABRUTA,
              UN_TABLAERROR =>	MI_TABLA,
              UN_REEMPLAZOS =>  MI_MSGERROR );
        END;                   

    END LOOP ACTUALIZA_META_BRUTA;

  END PR_ACTUALIZARMETABRUTA;

--12  

PROCEDURE PR_IMPORTARXML
/*
      NAME              : PR_IMPORTARXML
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE 
      DATE MIGRADOR     : 07/11/2017
      TIME              : 05:50 PM
      SOURCE MODULE     : SysmanBP2017.11.01
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PROCEDIMIENTO QUE GUARDAR EL PROYECTO CARGADO A PARTIR DE UN ARCHIVO XML. 
      PARAMETERS        : UN_COMPANIA         => COMPANIA CON LA QUE SE ESTA TRABAJANDO 
                          UN_PROYECTOS        => CADENA QUE CONTIENE LA INFORMACION DE LOS PROYECTOS  
                          UN_PRODUCTOS        =>  CADENA QUE CONTIENE LA INFORMACION DE LOS PRODUCTOS PARA CADA PROYECTO
                          UN_ACTIVIDADES      =>  CADDENA QUE CONTIENE LA INFORMACION DE LAS ACTIVIDADES DE CADA UNO DE LOS PRODUCTOS
                          UN_ASIGNAR          =>  INDICADOR QUE PERMITE DETERMINAR SI SE DEBE GENERAR UN CODIGO PARA EL PROYECTO O SI YA LO CONTIENE
                          UN_CODIGOPROY       =>  CODIGO DEL PROYECTO
                          UN_DEPENDENCIA      =>  PERMITE DETERMINA EL RESPONSABLE DEL PROYECTO 
                          UN_VIGENCIAINICIAL  =>  ANIO INICIAL
                          UN_VIGENCIAFINAL    =>  ANIO FINAL
      MODIFICATIONS     : 
      @METHOD:  POST
      @NAME:    importarXml
    */
(
  UN_COMPANIA           IN   PCK_SUBTIPOS.TI_COMPANIA,
  UN_PROYECTOS          IN   CLOB,
  UN_PRODUCTOS          IN   CLOB,
  UN_ACTIVIDADES        IN   CLOB,
  UN_ASIGNAR            IN   PCK_SUBTIPOS.TI_LOGICO,
  UN_CODIGOPROY         IN   PROYECTOS.CODIGO%TYPE,
  UN_DEPENDENCIA        IN   DEPENDENCIA_RESPONSABLE.DEPENDENCIA%TYPE,
  UN_VIGENCIAINICIAL    IN   PROYECTOS.VIGENCIAINICIO%TYPE,
  UN_VIGENCIAFINAL      IN   PROYECTOS.VIGENCIAFIN%TYPE,
  UN_USUARIO            IN   PCK_SUBTIPOS.TI_USUARIO,
  UN_CODIGOPROYBPIN     IN   PROYECTOS.CODIGO%TYPE
)
AS
  MI_CODIGOBPMI           PROYECTOS.CODIGOBPIM%TYPE;
  MI_TABLA                PCK_SUBTIPOS.TI_TABLA;
  MI_CRITERIO             PCK_SUBTIPOS.TI_CONDICION;
  MI_CODIGOPROY           PROYECTOS.CODIGO%TYPE;
  MI_TIPO                 PCK_SUBTIPOS.TI_DOCNUM;
  MI_PROYECTOS            PCK_SYSMAN_UTL.T_SPLIT;
  MI_PROYECTOSGEN         PCK_SYSMAN_UTL.T_SPLITCL;
  MI_PRODUCTOSGEN         PCK_SYSMAN_UTL.T_SPLIT;
  MI_ACTIVIDADESGEN       PCK_SYSMAN_UTL.T_SPLIT;
  MI_PRODUCTOS            PCK_SYSMAN_UTL.T_SPLIT;
  MI_ACTIVIDADES          PCK_SYSMAN_UTL.T_SPLIT;
  MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
  MI_RTA                  PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_CODENTIDAD           BP_ENTIDADES.CODIGO%TYPE;
  MI_CONSECUTIVOCOMP      COMPONENTES.CODIGO%TYPE;
  MI_VALORTOTAL           COMPONENTES.VALORTOTAL%TYPE; 
  MI_CONSECUTIVOACT       COMPONENTES_ACTIVIDADES.ACTIVIDAD%TYPE;
  MI_RESPONSABLE          DEPENDENCIA_RESPONSABLE.RESPONSABLE%TYPE;
  MI_SUCURSAL             DEPENDENCIA_RESPONSABLE.SUCURSAL%TYPE;
  MI_VALORTOTALCOM        PROYECTOS.VALORTOTAL%TYPE; 
BEGIN
  MI_TABLA:='PROYECTOS';
  MI_CRITERIO:='COMPANIA = '''|| UN_COMPANIA ||'''';
  IF UN_ASIGNAR <> 0 THEN
    MI_CODIGOPROY:=UN_CODIGOPROY;
    MI_CODIGOBPMI := SUBSTR(UN_CODIGOPROY, 1, 4)|| PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                                         UN_NOMBRE    => 'CODIGO ENTIDAD DNP', 
                                                                         UN_MODULO    => PCK_DATOS.MODULOBANCOPROY,
                                                                         UN_FECHA_PAR => SYSDATE) || SUBSTR(UN_CODIGOPROY, 5, 8);
  ELSE
    MI_CODIGOPROY:=PCK_SYSMAN_UTL.FC_GENCONSECUTIVO( UN_TABLA    =>  MI_TABLA,
                                      UN_CRITERIO =>  MI_CRITERIO,
                                      UN_CAMPO    =>  'CODIGO');
    MI_CODIGOBPMI := SUBSTR(MI_CODIGOPROY, 1, 4)|| PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                                         UN_NOMBRE    => 'CODIGO ENTIDAD DNP', 
                                                                         UN_MODULO    => PCK_DATOS.MODULOBANCOPROY,
                                                                         UN_FECHA_PAR => SYSDATE) || SUBSTR(MI_CODIGOPROY, 5, 8);

  END IF;
  --15/11/2024 7801737
  IF LENGTH(MI_CODIGOPROY) > 12 THEN 
    MI_CODIGOBPMI := UN_CODIGOPROY;
  END IF; 
  IF UN_CODIGOPROYBPIN IS NOT NULL OR UN_CODIGOPROYBPIN <> '' THEN 
    MI_CODIGOBPMI := UN_CODIGOPROYBPIN;
  END IF;
  BEGIN
    BEGIN
      SELECT DISTINCT 'X' 
      INTO  MI_TIPO
      FROM   TIPOSPROYECTO 
      WHERE  COMPANIA = UN_COMPANIA 
      AND CODIGO = '1';
      EXCEPTION WHEN NO_DATA_FOUND THEN
              RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
    END;
     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS  THEN    
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD   => SQLCODE,
                          UN_ERROR_COD => PCK_ERRORES.ERR_BANCOP_CONFTIPOPROYECTO
                        );
  END;

  BEGIN
    BEGIN
      SELECT DISTINCT 'X' 
      INTO  MI_TIPO
       FROM   BP_TIPOSCOMPONENTES 
       WHERE  COMPANIA = UN_COMPANIA 
       AND CODIGO = '001';
      EXCEPTION WHEN NO_DATA_FOUND THEN
                  RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
    END;
     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS  THEN    
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD   => SQLCODE,
                          UN_ERROR_COD => PCK_ERRORES.ERR_BANCOP_CONFTIPOCOMPONENTE
                        );
  END;

  BEGIN
    BEGIN
      SELECT DISTINCT 'X'
      INTO   MI_TIPO
      FROM   UNIDADPROYECTOS 
      WHERE  COMPANIA = UN_COMPANIA 
      AND UNIDAD = '#';
      EXCEPTION WHEN NO_DATA_FOUND THEN
            RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
    END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS  THEN    
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD   => SQLCODE,
                          UN_ERROR_COD => PCK_ERRORES.ERR_BANCOP_CONFUNIDADPROYECTO
                        );
  END;

  MI_VALORTOTAL:=0;
  MI_TABLA := 'PROYECTOS';
  -- PROYECTOS

  MI_PROYECTOSGEN := PCK_SYSMAN_UTL.FC_SPLIT_CL( UN_LISTA        => UN_PROYECTOS ,
                                                  UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_REG);

  <<PROYECTO>>
  FOR RS IN MI_PROYECTOSGEN.FIRST..MI_PROYECTOSGEN.LAST 
  LOOP
    MI_PROYECTOS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_PROYECTOSGEN(RS),
                                                UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);

      IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                               UN_NOMBRE    => 'MANEJA ALCALDIA O GOBERNACION EN BANCO DE PROYECTOS', 
                               UN_MODULO    => PCK_DATOS.MODULOBANCOPROY,
                               UN_FECHA_PAR => SYSDATE) = 'G' THEN
        BEGIN 
          SELECT CODIGO
          INTO MI_CODENTIDAD
          FROM   BP_ENTIDADES 
          WHERE  COMPANIA = UN_COMPANIA
          AND  NOMBRE LIKE '%GOBERNACION%';
          EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_CODENTIDAD:='';
        END;
      ELSE
        BEGIN 
            SELECT CODIGO
            INTO MI_CODENTIDAD
            FROM   BP_ENTIDADES 
            WHERE  COMPANIA = UN_COMPANIA
            AND  NOMBRE LIKE '%ALCALDIA%';
            EXCEPTION WHEN NO_DATA_FOUND THEN
              MI_CODENTIDAD:='';
          END;
      END IF;

  BEGIN
    BEGIN                                             
      SELECT RESPONSABLE, SUCURSAL 
      INTO   MI_RESPONSABLE, MI_SUCURSAL
      FROM   DEPENDENCIA_RESPONSABLE
      WHERE  COMPANIA    = UN_COMPANIA
      AND    DEPENDENCIA = UN_DEPENDENCIA
      AND    JEFEUNIDAD NOT IN (0)
      AND    ROWNUM <=1;
      EXCEPTION WHEN NO_DATA_FOUND  THEN
         RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
    END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS  THEN    
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD   => SQLCODE,
                          UN_ERROR_COD => PCK_ERRORES.ERR_BANCOP_RESPDEPEND
                        );
  END;


    MI_CAMPOS := ' COMPANIA, CODIGO, CODIGOBPIM, NOMBREPROYECTO, OBJETO,
                   TIPOPROYECTO, ENTIDADPROPONENTE, VIGENCIAINICIO, VIGENCIAFIN,
                   DEPENDENCIA,  RESPONSABLE, SUCURSAL, DESC_POBLACION, N_POBLACION, N_POBLACION_OBJ,
                   PERIOCIDAD, DATE_CREATED, CREATED_BY, TIPOREGISTRO,  FECHAREGISTRO';

    MI_VALORES := ''''||  UN_COMPANIA     ||''', 
                  ''' ||  MI_CODIGOPROY   ||''', 
                  ''' ||  MI_CODIGOBPMI   ||''',
                  ''' ||  MI_PROYECTOS(2) || ''',
                  ''' ||  MI_PROYECTOS(6) || ''',
                  ''1'',
                  ''' ||  MI_CODENTIDAD      ||''',
                  '   ||  UN_VIGENCIAINICIAL ||',
                  '   ||  UN_VIGENCIAFINAL   ||',
                  ''' ||  UN_DEPENDENCIA     ||''',
                  ''' ||  MI_RESPONSABLE     ||''',
                  ''' ||  MI_SUCURSAL        ||''',
                  ''' ||  MI_PROYECTOS(3)    ||''',
                  '   ||  MI_PROYECTOS(4)    ||',
                  '   ||  MI_PROYECTOS(5)    ||',
                  ''01'',
                  SYSDATE,
                  '''||UN_USUARIO||''',
                  ''REG'',
                  SYSDATE
                  ';
    BEGIN 
      BEGIN
        MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  MI_TABLA, 
                                               UN_ACCION  =>  'I', 
                                               UN_CAMPOS  =>  MI_CAMPOS, 
                                               UN_VALORES =>  MI_VALORES );
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN    
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD   => SQLCODE,
                    UN_ERROR_COD => PCK_ERRORES.ERR_BANCOP_INSPROYECTO
                  );
    END;



    IF MI_RTA > 0 THEN
     
        MI_PRODUCTOSGEN := PCK_SYSMAN_UTL.FC_SPLIT_SYS( UN_LISTA        => UN_PRODUCTOS ,
                                                       UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_REG); 
          <<PRODUCTO>> 
          FOR RS_PROD IN MI_PRODUCTOSGEN.FIRST..MI_PRODUCTOSGEN.LAST 
          LOOP

            MI_PRODUCTOS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_PRODUCTOSGEN(RS_PROD),
                                                        UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);
            MI_TABLA:='COMPONENTES';
            MI_CRITERIO:='COMPANIA = '''|| UN_COMPANIA ||'''
                          AND CODIGOPROYECTO = '''||MI_CODIGOPROY||'''';
            MI_CONSECUTIVOCOMP:=PCK_SYSMAN_UTL.FC_GENCONSECUTIVO( UN_TABLA    =>  MI_TABLA,
                                                                  UN_CRITERIO =>  MI_CRITERIO,
                                                                  UN_CAMPO    =>  'CODIGO');

             MI_CAMPOS := 'COMPANIA, CODIGOPROYECTO, CODIGO, TIPOCOMPONENTE, 
                           NOMBRECOMPONENTE, UNIDAD,VALORTOTAL,VALORUNITARIO, CANTIDAD, VIGENCIA, OBJETO,
                           VALORPROGRAMADO,VALOREJECUTADO,VALORTOTALSOLICITADO,SALDOCOMPONENTE, DATE_CREATED, CREATED_BY';
             MI_VALORES := '''' ||  UN_COMPANIA         ||''', 
                            ''' ||  MI_CODIGOPROY       ||''', 
                            ''' ||  MI_CONSECUTIVOCOMP  ||''',
                            ''001'',
                            ''' ||  MI_PRODUCTOS(3)     ||''',
                            ''#'',
                            '   ||  MI_PRODUCTOS(9)     ||',
                            '   ||  MI_PRODUCTOS(10)     ||',
                            '   ||  MI_PRODUCTOS(4)     ||',
                            '   ||  MI_PRODUCTOS(8)     ||',
                            ''' ||  MI_PRODUCTOS(6)     ||''',
                            0,
                            0,
                            0,
                            0,
                            SYSDATE,
                            '''||UN_USUARIO||'''';
             BEGIN 
               BEGIN
                  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  MI_TABLA, 
                                                         UN_ACCION  =>  'I', 
                                                         UN_CAMPOS  =>  MI_CAMPOS, 
                                                         UN_VALORES =>  MI_VALORES );
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
               END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN    
                              PCK_ERR_MSG.RAISE_WITH_MSG(
                              UN_EXC_COD   => SQLCODE,
                              UN_ERROR_COD => PCK_ERRORES.ERR_BANCOP_INSCOMPONENTES
                            );
             END;

                  IF MI_RTA > 0 THEN
                     --ACTIVIDADES
                     MI_VALORTOTAL := 0;
                      MI_ACTIVIDADESGEN := PCK_SYSMAN_UTL.FC_SPLIT_SYS( UN_LISTA        => UN_ACTIVIDADES ,
                                                                        UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_REG); 
                      <<ACTIVIDAD>>
                      FOR RS_ACT IN MI_ACTIVIDADESGEN.FIRST..MI_ACTIVIDADESGEN.LAST 
                      LOOP
                        MI_ACTIVIDADES := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => MI_ACTIVIDADESGEN(RS_ACT),
                                                                      UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);

                        IF MI_ACTIVIDADES(3)= MI_PRODUCTOS(2) AND MI_ACTIVIDADES(6)= MI_PRODUCTOS(8) THEN
                            MI_TABLA:='BP_ACTIVIDADES';
                            MI_CRITERIO:='COMPANIA = '''|| UN_COMPANIA ||'''';
                            MI_CONSECUTIVOACT:=PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(  UN_TABLA    =>  MI_TABLA,
                                                                                  UN_CRITERIO =>  MI_CRITERIO,
                                                                                  UN_CAMPO    =>  'CODIGO');
                            MI_CAMPOS := 'COMPANIA, CODIGO, NOMBRE, UNIDAD, DATE_CREATED, CREATED_BY';
                            MI_VALORES := '''' ||  UN_COMPANIA        ||''', 
                                          '''  ||  MI_CONSECUTIVOACT  ||''', 
                                          '''  ||  MI_ACTIVIDADES(4)  ||''',
                                          ''#'',
                                          SYSDATE,
                                          '''  ||  UN_USUARIO         ||'''';
                             BEGIN 
                               BEGIN
                                  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  MI_TABLA, 
                                                                         UN_ACCION  =>  'I', 
                                                                         UN_CAMPOS  =>  MI_CAMPOS, 
                                                                         UN_VALORES =>  MI_VALORES );
                                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                                RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
                               END;
                                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN    
                                              PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD   => SQLCODE,
                                              UN_ERROR_COD => PCK_ERRORES.ERR_BANCOP_INSACTIVIDADES
                                            );
                             END; 

                            MI_TABLA:='COMPONENTES_ACTIVIDADES';
                            MI_CAMPOS := 'COMPANIA, CODIGOPROYECTO, COMPONENTE, 
                                          TIPOCOMPONENTE, ACTIVIDAD, COSTOUNITARIO, 
                                          COSTOTOTAL, CANTIDAD, NOMBREACTIVIDAD, 
                                          DESCRIPCION,VIGENCIA, VALORPROGRAMADO,VALOREJECUTADO,
                                          PORCEJECUTADO,VALOR_SOLICITADO_ACTIVIDAD, DATE_CREATED, CREATED_BY';
                            MI_VALORES := '''' ||  UN_COMPANIA        ||''', 
                                          '''  ||  MI_CODIGOPROY      ||''', 
                                          '''  ||  MI_CONSECUTIVOCOMP ||''',
                                          ''001'',
                                          '''  ||  MI_CONSECUTIVOACT  ||''',
                                          '||MI_ACTIVIDADES(7)||',
                                          '||MI_ACTIVIDADES(7)||',
                                          1,
                                          '''||MI_ACTIVIDADES(4)||''',
                                          '''||MI_ACTIVIDADES(4)||''',
                                          '||MI_ACTIVIDADES(6)||',
                                          0,
                                          0,
                                          0,
                                          0,
                                          SYSDATE,
                                          '''||UN_USUARIO||'''';
                             BEGIN 
                               BEGIN
                                  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  MI_TABLA, 
                                                                         UN_ACCION  =>  'I', 
                                                                         UN_CAMPOS  =>  MI_CAMPOS, 
                                                                         UN_VALORES =>  MI_VALORES );
                                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                                RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
                               END;
                                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN    
                                              PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD   => SQLCODE,
                                              UN_ERROR_COD => PCK_ERRORES.ERR_BANCOP_INSCOMPACTIVIDADES
                                            );
                             END;
                        END IF;                                      
                     END LOOP ACTIVIDAD;           
                  END IF; 
         END LOOP PRODUCTO;
   
      --ACTUALIZACION VALORTOTAL Y PROGRAMADO

        SELECT SUM(VALORTOTAL)
        INTO   MI_VALORTOTALCOM
        FROM COMPONENTES
        WHERE COMPANIA        =  UN_COMPANIA
          AND CODIGOPROYECTO  =  MI_CODIGOPROY 
          AND TIPOCOMPONENTE  =  '001';

         MI_TABLA:='PROYECTOS';
         MI_CAMPOS := ' VALORTOTAL = '||MI_VALORTOTALCOM||',
                        DATE_MODIFIED = SYSDATE,
                        MODIFIED_BY   = '''||UN_USUARIO||'''';
         MI_CONDICION := 'COMPANIA            = '''||UN_COMPANIA ||'''
                          AND CODIGO  = '''||MI_CODIGOPROY||'''';
         BEGIN 
          BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  MI_TABLA, 
                                                   UN_ACCION    =>  'M', 
                                                   UN_CAMPOS    =>  MI_CAMPOS,
                                                   UN_CONDICION =>  MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN    
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERR_BANCO_ACTVALORTOTAL
                      );
        END;

    END IF;                                               
  END LOOP PROYECTO;
END PR_IMPORTARXML;

--13

FUNCTION FC_CREAR_PROG_PROY
/*
    NAME              : PR_CREAR_PROG_PROY
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : JONATHAN ENRIQUE GUERRERO TORRES
    DATE MIGRADOR     : 06/03/2017 
    TIME              : 03:08 PM
    SOURCE MODULE     : BANCO PROYECTIOS
    DESCRIPTION       : Este procedimiento crea la progracion de los proyectos
    PARAMETERS        : UN_COMPANIA	     => Compañia a crear
                        UN_VIGENCIA      => Año de inicio de proceso
                        UN_CODIGO        => codigo del Proyecto
                        UN_USUARIO       => Uduario de ingreso al sistema. 
    @NAME:   crearProgramacionProy
    @METHOD: POST
    */

(
  UN_COMPANIA     IN   PCK_SUBTIPOS.TI_COMPANIA,
  UN_VIGENCIA     IN   PCK_SUBTIPOS.TI_STRSQL,
  UN_CODIGO       IN   PCK_SUBTIPOS.TI_STRSQL,
  UN_USUARIO      IN   PCK_SUBTIPOS.TI_USUARIO
)
RETURN PCK_SUBTIPOS.TI_ENTERO_LARGO
AS 
	MI_TABLA 		      PCK_SUBTIPOS.TI_TABLA;
	MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
	MI_MSGERROR   	  PCK_SUBTIPOS.TI_CLAVEVALOR;
	MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
	MI_VALORES        PCK_SUBTIPOS.TI_VALORES;	
  MI_RTA            PCK_SUBTIPOS.TI_DOBLE;
  MI_RTA_PROG       PCK_SUBTIPOS.TI_DOBLE;
  MI_MERGEUSING     PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE    PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE    PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_MERGENOEXIS    PCK_SUBTIPOS.TI_MERGENOEXISTE;
  MI_RTADOS            CLOB;


BEGIN
MI_RTA_PROG := 0;
     MI_CONDICION := '';
     MI_CAMPOS := ' COMPANIA,
                    CODIGOPROYECTO,
                    CODIGOCOMPONENTE,
                    TIPOCOMPONENTE,
                    CODIGOACTIVIDAD,
                    VIGENCIA,
                    TIPOESTADO,
                    CODIGO,
                    FECHA,
                    CODIGO_REPROGRAMADO,
                    PERIOCIDAD,
                    VALOR1,
                    PORCENTAJE1,
                    VALOR2,
                    PORCENTAJE2,
                    VALOR3,
                    PORCENTAJE3,
                    VALOR4,
                    PORCENTAJE4,
                    VALOR5,
                    PORCENTAJE5,
                    VALOR6,
                    PORCENTAJE6,
                    VALOR7,
                    PORCENTAJE7,
                    VALOR8,
                    PORCENTAJE8,
                    VALOR9,
                    PORCENTAJE9,
                    VALOR10,
                    PORCENTAJE10,
                    VALOR11,
                    PORCENTAJE11,
                    VALOR12,
                    PORCENTAJE12,
                    VALORTOTAL,
                    CANTIDAD,
                    CODIGO_QUEAPRUEBA,
                    CREATED_BY,
                    DATE_CREATED';


     MI_VALORES := ' SELECT CA.COMPANIA,
                            CA.CODIGOPROYECTO,
                            CA.COMPONENTE,
                            CA.TIPOCOMPONENTE,
                            CA.ACTIVIDAD,
                            CA.VIGENCIA,
                            ''P''     AS TIPOESTADO,
                            1       AS CODIGO,
                            SYSDATE AS FECHA,
                            0       AS CODIGO_REPROGRAMADO,
                            P.PERIOCIDAD,
                            CASE WHEN P.PERIOCIDAD       = ''01'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END                AS VALOR1,
                            CASE WHEN P.VALORTOTAL NOT IN (0) THEN ROUND((CASE WHEN P.PERIOCIDAD=''01'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END )/P.VALORTOTAL,2) ELSE 0 END PORCENTAJE1,   
                            CASE WHEN P.PERIOCIDAD       =''02'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END                       AS VALOR2,
                            CASE WHEN P.VALORTOTAL NOT IN (0) THEN ROUND((CASE WHEN  P.PERIOCIDAD=''02'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END )/P.VALORTOTAL,2) ELSE 0 END PORCENTAJE2,  
                            CASE WHEN P.PERIOCIDAD       =''03'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END                      AS VALOR3,
                            CASE WHEN P.VALORTOTAL NOT IN (0) THEN ROUND((CASE WHEN P.PERIOCIDAD=''03'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END )/P.VALORTOTAL,2) ELSE 0 END PORCENTAJE3,  
                            CASE WHEN P.PERIOCIDAD       =''04'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END                      AS VALOR4,
                            CASE WHEN P.VALORTOTAL NOT IN (0) THEN ROUND((CASE WHEN P.PERIOCIDAD=''04'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END )/P.VALORTOTAL,2) ELSE 0 END PORCENTAJE4,  
                            CASE WHEN P.PERIOCIDAD       =''05'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END                   AS VALOR5,
                            CASE WHEN P.VALORTOTAL NOT IN (0) THEN ROUND((CASE WHEN P.PERIOCIDAD=''05'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END )/P.VALORTOTAL,2) ELSE 0 END PORCENTAJE5,  
                            CASE WHEN P.PERIOCIDAD       =''06'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END                       AS VALOR6,
                            CASE WHEN P.VALORTOTAL NOT IN (0) THEN ROUND((CASE WHEN P.PERIOCIDAD=''06'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END )/P.VALORTOTAL,2) ELSE 0 END PORCENTAJE6,  
                            CASE WHEN P.PERIOCIDAD       =''07'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END                       AS VALOR7,
                            CASE WHEN P.VALORTOTAL NOT IN (0) THEN ROUND((CASE WHEN P.PERIOCIDAD=''07'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END )/P.VALORTOTAL,2) ELSE 0 END PORCENTAJE7,  
                            CASE WHEN P.PERIOCIDAD       =''08'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END                       AS VALOR8,
                            CASE WHEN P.VALORTOTAL NOT IN (0) THEN ROUND((CASE WHEN P.PERIOCIDAD=''08'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END )/P.VALORTOTAL,2) ELSE 0 END PORCENTAJE8, 
                            CASE WHEN P.PERIOCIDAD       =''09'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END                       AS VALOR9, 
                            CASE WHEN P.VALORTOTAL NOT IN (0) THEN ROUND((CASE WHEN P.PERIOCIDAD=''09'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END )/P.VALORTOTAL,2) ELSE 0 END PORCENTAJE9, 
                            CASE WHEN P.PERIOCIDAD       =''10'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END                       AS VALOR10,
                            CASE WHEN P.VALORTOTAL NOT IN (0) THEN ROUND((CASE WHEN P.PERIOCIDAD=''10'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END )/P.VALORTOTAL,2) ELSE 0 END PORCENTAJE10,  
                            CASE WHEN P.PERIOCIDAD       =''11'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END                       AS VALOR11,
                            CASE WHEN P.VALORTOTAL NOT IN (0) THEN ROUND((CASE WHEN P.PERIOCIDAD=''11'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END )/P.VALORTOTAL,2) ELSE 0 END PORCENTAJE11,  
                            CASE WHEN P.PERIOCIDAD       =''12'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END                       AS VALOR12,
                            CASE WHEN P.VALORTOTAL NOT IN (0) THEN ROUND((CASE WHEN P.PERIOCIDAD=''12'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END )/P.VALORTOTAL,2) ELSE 0 END PORCENTAJE11,    
                            CA.COSTOTOTAL,
                            CA.CANTIDAD,
                            0                        AS CODIGO_QUEAPRUEBA,
                            '''||UN_USUARIO||'''     AS CREATED_BY,
                            SYSDATE                  AS DATE_CREATED
                       FROM PROYECTOS  P
                      INNER JOIN COMPONENTES_ACTIVIDADES   CA
                         ON P.COMPANIA         = CA.COMPANIA
                        AND P.CODIGO           = CA.CODIGOPROYECTO                        
                       LEFT JOIN PROGRAMACION PROGR
                         ON CA.COMPANIA       = PROGR.COMPANIA
                        AND CA.VIGENCIA       = PROGR.VIGENCIA
                        AND CA.ACTIVIDAD      = PROGR.CODIGOACTIVIDAD
                        AND CA.TIPOCOMPONENTE = PROGR.TIPOCOMPONENTE
                        AND CA.COMPONENTE     = PROGR.CODIGOCOMPONENTE
                        AND CA.CODIGOPROYECTO = PROGR.CODIGOPROYECTO                         
                      WHERE P.COMPANIA        = '''||UN_COMPANIA||''' 
                        AND PROGR.CODIGO   IS NULL' ;    

        IF UN_VIGENCIA  <> 'TODAS'  THEN 
            MI_VALORES := MI_VALORES||' AND P.VIGENCIAINICIO = '||UN_VIGENCIA; 
        END IF;

        IF UN_CODIGO <> 'TODOS' THEN 
            MI_VALORES := MI_VALORES||' AND P.CODIGO = '''||UN_CODIGO||'''';
        END IF;

       	MI_TABLA := 'PROGRAMACION';

        BEGIN 
            BEGIN
                MI_RTA_PROG := PCK_DATOS.FC_ACME(	UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'IS',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_VALORES 	 => MI_VALORES );                 
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS; 
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN               
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                         UN_ERROR_COD => PCK_ERRORES.ERR_BANCOP_INSET_PROGRAMACION); 

        END;       

        /*

        IF MI_RTA = 0 THEN 
          NULL;
            --RAISE  Señor usuario el proyecto " & Me!cmb_proyecto & " ya tenia programados todos los componentes y actividades.
        END IF;

        MI_TABLA       := 'COMPONENTES';
       IF UN_VIGENCIA  <> 'TODAS'  THEN 
            MI_CONDICION := ' AND CA.VIGENCIA = '||UN_VIGENCIA; 
        END IF;

        IF UN_CODIGO <> 'TODOS' THEN 
            MI_CONDICION := MI_CONDICION||' AND CA.CODIGOPROYECTO = '''||UN_CODIGO||'''';
        END IF; 




        MI_MERGEUSING  := 'SELECT CA.COMPANIA,
                                  CA.CODIGOPROYECTO,
                                  COMPONENTE,
                                  CA.TIPOCOMPONENTE,
                                  SUM(CA.VALORPROGRAMADO) AS TOTALPROGRAMADO
                             FROM COMPONENTES_ACTIVIDADES  CA
                            WHERE CA.COMPANIA = '''||UN_COMPANIA||'''
                            '||MI_CONDICION||'
                            GROUP BY CA.COMPANIA,
                                  CA.CODIGOPROYECTO,
                                  COMPONENTE,
                                  CA.TIPOCOMPONENTE
                            ORDER BY COMPONENTE';

         MI_MERGEENLACE :='   TABLA.COMPANIA       = VISTA.COMPANIA 
                          AND TABLA.CODIGO         = VISTA.COMPONENTE
                          AND TABLA.CODIGOPROYECTO = VISTA.CODIGOPROYECTO
                          AND TABLA.TIPOCOMPONENTE = VISTA.TIPOCOMPONENTE';

         MI_MERGEEXISTE := ' UPDATE SET TABLA.VALORPROGRAMADOS = VISTA.TOTALPROGRAMADO,
                                        TABLA.DATE_MODIFIED   = SYSDATE,
                                        TABLA.MODIFIED_BY     = '''||UN_USUARIO||'''';

        BEGIN 
            BEGIN
                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA, 
                                            UN_ACCION      => 'MM', 
                                            UN_MERGEUSING  => MI_MERGEUSING, 
                                            UN_MERGEENLACE => MI_MERGEENLACE,
                                            UN_MERGEEXISTE => MI_MERGEEXISTE);

                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                    RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERR_BANCOP_ACTUAL_COM,
                                     UN_REEMPLAZOS => MI_MSGERROR);
        END;                                

        MI_TABLA       := 'PROGRAMACION';
        MI_MERGEUSING  := 'SELECT CA.COMPANIA,
                                  CA.VIGENCIA,
                                  CA.ACTIVIDAD,
                                  CA.TIPOCOMPONENTE,
                                  CA.COMPONENTE,
                                  CA.CODIGOPROYECTO,
                                  CA.COSTOTOTAL
                             FROM COMPONENTES_ACTIVIDADES CA
                            INNER JOIN PROGRAMACION P
                               ON CA.COMPANIA       = P.COMPANIA          
                              AND CA.VIGENCIA       = P.VIGENCIA         
                              AND CA.ACTIVIDAD      = P.CODIGOACTIVIDAD  
                              AND CA.CODIGOPROYECTO = P.CODIGOPROYECTO  
                              AND CA.TIPOCOMPONENTE = P.TIPOCOMPONENTE   
                              AND CA.COMPONENTE     =   P.CODIGOCOMPONENTE 
                            WHERE CA.COMPANIA      = '''||UN_COMPANIA||'''
                            '||MI_CONDICION||'
                              AND P.VALOR1         <> CA.COSTOTOTAL
                              AND P.PERIOCIDAD     IN (''01'')';

         MI_MERGEENLACE :='   TABLA.COMPANIA         = VISTA.COMPANIA 
                          AND TABLA.VIGENCIA         = VISTA.VIGENCIA
                          AND TABLA.CODIGOACTIVIDAD  = VISTA.ACTIVIDAD
                          AND TABLA.CODIGOPROYECTO   = VISTA.CODIGOPROYECTO
                          AND TABLA.TIPOCOMPONENTE   = VISTA.TIPOCOMPONENTE
                          AND TABLA.CODIGOCOMPONENTE = VISTA.COMPONENTE';


         MI_MERGEEXISTE := ' UPDATE SET     TABLA.VALOR1          = VISTA.COSTOTOTAL,                                        
                                            TABLA.VALORTOTAL      = VISTA.COSTOTOTAL,
                                            TABLA.DATE_MODIFIED   = SYSDATE,
                                            TABLA.MODIFIED_BY     = '''||UN_USUARIO||'''';


        BEGIN 
            BEGIN   
                 MI_RTA:= PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA, 
                                            UN_ACCION      => 'MM', 
                                            UN_MERGEUSING  => MI_MERGEUSING, 
                                            UN_MERGEENLACE => MI_MERGEENLACE,
                                            UN_MERGEEXISTE => MI_MERGEEXISTE);         

             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                    RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERR_BANCOP_ACTUAL_PROGRAMACION,
                                     UN_REEMPLAZOS => MI_MSGERROR);
        END;

        --ROLLBACK;                                    

    /* MI_RTADOS := PCK_BANCOS_PROY5.FC_ACTU_VALORPROGRAMADOMANT
                  (
                    UN_COMPANIA      => UN_COMPANIA,
                    UN_VIGENCIA      => UN_VIGENCIA,
                    UN_PROYECTOINI   => UN_CODIGO,
                    UN_PROYECTOFIN   => UN_CODIGO,
                    UN_OPCION        => 1,
                    UN_USUARIO       => UN_USUARIO 
                );
    */

   -- RETURN MI_RTA_PROG;

END FC_CREAR_PROG_PROY;  

FUNCTION FC_DEFINIRORDEN
  /*
    NAME              : FC_DEFINIRORDEN
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL VENEGAS RODRIGUEZ
    DATE MIGRADOR     : 12/03/2018 
    TIME              : 04:00 PM
    SOURCE MODULE     : BANCO PROYECTIOS

    @NAME:   definirOrden
    @METHOD: GET
    */
(
  UN_NOMBRETABLA IN VARCHAR2, 
  UN_NOMBRECAMPO IN VARCHAR2, 
  UN_CAMPOORDEN  IN VARCHAR2, 
  UN_CONDICION   IN VARCHAR2,
  UN_CON_ALIAS   IN PCK_SUBTIPOS.TI_LOGICO
)
RETURN VARCHAR2
AS
MI_SALIDA    VARCHAR2(3200);
MI_STRSQL    PCK_SUBTIPOS.TI_STRSQL;
MI_NOMBRE    PCK_SUBTIPOS.TI_STRSQL; 
MI_CONTEO    PCK_SUBTIPOS.TI_ENTERO;
MI_RS        SYS_REFCURSOR;
BEGIN
 MI_STRSQL := 'SELECT '||UN_NOMBRECAMPO||'
               FROM     '||UN_NOMBRETABLA||'
               WHERE    '|| UN_CONDICION ||'
              ORDER BY '|| UN_CAMPOORDEN ;

  EXECUTE IMMEDIATE 'SELECT COUNT(1) FROM ('||MI_STRSQL||')' INTO MI_CONTEO;                  

  IF MI_CONTEO >=1 THEN

    OPEN MI_RS FOR MI_STRSQL;
      LOOP
      FETCH MI_RS INTO MI_NOMBRE;
          EXIT WHEN MI_RS%NOTFOUND;
       IF UN_CON_ALIAS NOT IN (0) THEN       
            MI_SALIDA := MI_SALIDA||''''||MI_NOMBRE||''' "'||REPLACE(UPPER(MI_NOMBRE),' ','')||'",';
       ELSE
            MI_SALIDA := MI_SALIDA||'"'||MI_NOMBRE||'",';
       END IF; 
      END LOOP;
 END IF;
 MI_SALIDA := SUBSTR( MI_SALIDA,0,LENGTH(MI_SALIDA)-1);


RETURN MI_SALIDA;  
END FC_DEFINIRORDEN;

 FUNCTION FC_FRMSIRECI238 
  /*
    NAME              : FC_FRMSIRECI238
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : CRISTHIAN CAMILO RODRIGUEZ PINZON
    DATE MIGRADOR     : 14/03/2018 
    TIME              : 04:00 PM
    SOURCE MODULE     : BANCO PROYECTIOS

    @NAME:   generarProyectosInversion
    @METHOD: GET
    */
(
	UN_COMPANIA		IN 		PCK_SUBTIPOS.TI_COMPANIA,
	UN_VIGENCIA_I	IN 		PROYECTOS.VIGENCIAINICIO%TYPE,
	UN_VIGENCIA_F	IN 		PROYECTOS.VIGENCIAFIN%TYPE
) RETURN CLOB

IS

V_PAR_PROGRAMA	NUMBER(4);
V_PAR_SUBPROGRAMA	NUMBER(4);

MI_SALIDA CLOB;
BEGIN

	V_PAR_PROGRAMA 	:= PCK_SYSMAN_UTL.FC_PAR	(
										UN_COMPANIA 	=> 	UN_COMPANIA,
										UN_NOMBRE		=>	'NUMERO DE DIGITOS PROGRAMA',
										UN_MODULO		=>	PCK_DATOS.MODULOBANCOPROY,
										UN_FECHA_PAR	=>	SYSDATE	
									);

	V_PAR_SUBPROGRAMA := PCK_SYSMAN_UTL.FC_PAR	(
										UN_COMPANIA 	=> 	UN_COMPANIA,
										UN_NOMBRE		=>	'NUMERO DE DIGITOS SUBPROGRAMA',
										UN_MODULO		=>	PCK_DATOS.MODULOBANCOPROY,
										UN_FECHA_PAR	=>	SYSDATE	
									);



	FOR CURSOR_H1 IN (
						WITH QRY_PROGYSUBPROGIND AS (
											    			SELECT COMPANIA,
											    			       ID_PLAN_P,
											    			       VIGENCIA_PLAN_P,
											    			       VIGENCIA_META_P,
											    			       PROYECTO,
											    			       COMPONENTE,
											    			       TIPOCOMPONENTE,
											    			       ACTIVIDAD,
											    			       SUBSTR(ID_PLAN_P,1,V_PAR_PROGRAMA) AS PROGRAMA,       
											    			       SUBSTR(ID_PLAN_P,1,V_PAR_SUBPROGRAMA) AS SUBPROGRAMA       
											    			FROM BP_PROYECTO_PLAN_INDICATIVO
											    			WHERE COMPANIA = UN_COMPANIA
											    		)

						SELECT ROWNUM CONSECUTIVO,
						       'FILA_' || ROWNUM AS CONSECUTIVOFILA,
						--       PR.CODIGO,
						--       QRY.PROYECTO ,
						       '1 SI' AS FCONINF ,
						       '' AS JUST,
						       PR.VIGENCIAINICIO AS VIGENCIA,
						       PR.CODIGOBPIM AS CODBPYPROY,
						       PR.NOMBREPROYECTO AS NOMBPROY,
						       BPPI.DESCRIPCION AS PROGRAMA,
						       BPPI1.DESCRIPCION AS SUBPROGRAMA,
						       (S.CODIGO || ' - ' || S.DESCRIPCION) AS SECTOR,
						       'ENTIDAD TERRITORIAL' AS EJECUTOR,
						       '00618' AS CODDIVIPOLA,
						       CO.NITCOMPANIA AS EJECUTORNIT,
						       '' AS EJECUTORDIGVERIF,
						       E.NOMBRE AS EJECUTORNOMB,
						       '' AS FECHAINI,
						       '' AS FECHAFIN,
						       (CASE 
						            WHEN PR.ESTADOACTUAL = 'S' THEN '1 APROBADO' 
						            WHEN PR.ESTADOACTUAL = 'E' THEN '2 EN EJECUCIÓN'
						            WHEN PR.ESTADOACTUAL = 'T' THEN '3 TERMINADO'
						        END) AS ESTADOPROY,
						        PR.VALORTOTAL,
						        PR.VALOR_REGALIAS,
						        (BPFR.CODIGO || ' - ' || BPFR.NOMBRE) AS FONDORECURSOS,
						        (CASE
						          WHEN (PR.VALORTOTAL - PR.VALOR_REGALIAS) = 0 THEN '2 NO' ELSE '1 SI'
						         END 
						        ) AS OTRASFUENTES,
						        (CASE
						          WHEN (PR.VALORTOTAL - PR.VALOR_REGALIAS) <> 0 THEN FR.NOMBRE
						         END 
						        ) AS OTRASFUENTESDESC,

						        PR.VALORTOTAL - PR.VALOR_REGALIAS AS VLROTRASFUENTES,
						        PR.VALOREJECUTADO AS VLRTOTCONTR,
						        PR.DESC_POBLACION AS DESCPOB,
						        PR.PORCEJECUCION AS PORCENTAJEEJEC,
						        'CEDULA DE CIUDADANIA' AS STIPOIDENT,
						        PR.RESPONSABLE AS SNUMCC,
						        '' AS SNUMNIT,
						        '' AS SDIGVERIF,
						        '' AS SCCEXTR,
						        TER.NOMBRE AS S,
						        '' AS OBSERV  

						FROM PROYECTOS PR 
						LEFT JOIN BP_FONDOSREGALIAS BPFR 
							ON PR.COMPANIA = BPFR.COMPANIA
							AND PR.FONDOS_REGALIAS = BPFR.CODIGO
						INNER JOIN RESPONSABLE R 
							ON PR.COMPANIA = R.COMPANIA
							AND PR.RESPONSABLE = R.CEDULA
						INNER JOIN TERCERO TER 
						  ON  TER.COMPANIA = R.COMPANIA
						  AND TER.SUCURSAL = R.SUCURSAL
						  AND TER.NIT = R.CEDULA
						INNER JOIN BP_ENTIDADES E 
							ON PR.COMPANIA = E.COMPANIA
							AND PR.ENTIDADPROPONENTE = E.CODIGO
						INNER JOIN QRY_PROGYSUBPROGIND QRY
							ON PR.COMPANIA = QRY.COMPANIA
							AND PR.CODIGO = QRY.PROYECTO
						INNER JOIN BP_PLAN_INDICATIVO BPPI 
							ON QRY.COMPANIA = BPPI.COMPANIA
							AND QRY.PROGRAMA = BPPI.ID
							AND QRY.VIGENCIA_PLAN_P = BPPI.VIGENCIA_INICIAL
						INNER JOIN BP_PLAN_INDICATIVO BPPI1
							ON QRY.COMPANIA = BPPI1.COMPANIA
							AND QRY.VIGENCIA_PLAN_P = BPPI1.VIGENCIA_INICIAL
							AND QRY.SUBPROGRAMA = BPPI1.ID
						INNER JOIN SECTORES S 
						  ON  BPPI.COMPANIA = S.COMPANIA
						  AND BPPI.SECTOR = S.CODIGO
						LEFT JOIN BP_PROYECTOSRUBROS BPPR 
						  ON PR.COMPANIA = BPPR.COMPANIA
						  AND PR.VIGENCIAINICIO = BPPR.VIGENCIA
						  AND PR.CODIGO = BPPR.PROYECTO
						LEFT JOIN FUENTE_RECURSOS FR
						  ON FR.COMPANIA = BPPR.COMPANIA
						  AND FR.CODIGO = BPPR.FUENTERECURSOSRUBRO
						  AND FR.ANO = BPPR.VIGENCIA
						INNER JOIN COMPANIA CO 
						  ON PR.COMPANIA = CO.CODIGO
						WHERE PR.COMPANIA = UN_COMPANIA
						  AND PR.VIGENCIAINICIO BETWEEN UN_VIGENCIA_I AND UN_VIGENCIA_F
						GROUP BY 
						       ROWNUM, 
						       PR.CODIGO,
						       QRY.PROYECTO ,
						       '1 SI',
						       '' ,
						       PR.VIGENCIAINICIO ,
						       PR.CODIGOBPIM ,
						       PR.NOMBREPROYECTO ,
						       BPPI.DESCRIPCION ,
						       BPPI1.DESCRIPCION ,
						       (S.CODIGO || ' - ' || S.DESCRIPCION) ,
						       'ENTIDAD TERRITORIAL' ,
						       '00618' ,
						       CO.NITCOMPANIA ,
						       '' ,
						       E.NOMBRE ,
						       '' ,
						       '' ,
						       (CASE 
						            WHEN PR.ESTADOACTUAL = 'S' THEN '1 APROBADO' 
						            WHEN PR.ESTADOACTUAL = 'E' THEN '2 EN EJECUCIÓN'
						            WHEN PR.ESTADOACTUAL = 'T' THEN '3 TERMINADO'
						        END) ,
						        PR.VALORTOTAL,
						        PR.VALOR_REGALIAS,
						        (BPFR.CODIGO || ' - ' || BPFR.NOMBRE) ,
						        (CASE
						          WHEN (PR.VALORTOTAL - PR.VALOR_REGALIAS) = 0 THEN '2 NO' ELSE '1 SI'
						         END 
						        ) ,
						        (CASE
						          WHEN (PR.VALORTOTAL - PR.VALOR_REGALIAS) <> 0 THEN FR.NOMBRE
						         END 
						        ) ,

						        PR.VALORTOTAL - PR.VALOR_REGALIAS ,
						        PR.VALOREJECUTADO ,
						        PR.DESC_POBLACION ,
						        PR.PORCEJECUCION ,
						        'CEDULA DE CIUDADANIA' ,
						        PR.RESPONSABLE ,
						        '' ,
						        '' ,
						        '' ,
						        TER.NOMBRE ,
						        ''  
						ORDER BY ROWNUM

					 )
	LOOP
		MI_SALIDA := MI_SALIDA || TO_CLOB(CURSOR_H1.CONSECUTIVO 
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.CONSECUTIVOFILA
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.FCONINF
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.JUST
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.VIGENCIA
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.CODBPYPROY
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.NOMBPROY
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.PROGRAMA
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.SUBPROGRAMA
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.SECTOR
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.EJECUTOR
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.CODDIVIPOLA
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.EJECUTORNIT
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.EJECUTORDIGVERIF
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.EJECUTORNOMB
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.FECHAINI
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.FECHAFIN
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.ESTADOPROY
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.VALORTOTAL
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.VALOR_REGALIAS
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.FONDORECURSOS
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.OTRASFUENTES
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.OTRASFUENTESDESC
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.VLROTRASFUENTES
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.VLRTOTCONTR
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.DESCPOB
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.PORCENTAJEEJEC
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.STIPOIDENT
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.SNUMCC
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.SNUMNIT
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.SDIGVERIF
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.SCCEXTR
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.S
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.OBSERV
											|| PCK_DATOS.GL_SEPARADOR_REG 
											);
	END LOOP;	



RETURN MI_SALIDA;  
EXCEPTION
  WHEN OTHERS THEN
    RETURN '';


END FC_FRMSIRECI238;

FUNCTION FC_FRMSIRECIEFICACIA 
  /*
    NAME              : FC_FRMSIRECIEFICACIA
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL VENEGAS RODRIGUEZ
    DATE MIGRADOR     : 15/03/2018 
    TIME              : 05:00 PM
    SOURCE MODULE     : BANCO PROYECTIOS

    @NAME:   generarInformeEficacia
    @METHOD: GET
    */
(
	UN_COMPANIA		IN 		PCK_SUBTIPOS.TI_COMPANIA,
	UN_VIGENCIA	  IN 		BPNOVEDADPROYECTO.VIGENCIA%TYPE
) RETURN CLOB

IS

MI_NOMBRE_COLUMNAS	VARCHAR2(32000);
MI_SALIDA CLOB;

BEGIN
	FOR CURSOR_H1 IN (SELECT ID,
       META,
       INDICADOR,
       TIPOMETA,
       VALORESPERADO, 
       VALORLOGRADO,
       SECTOR,
       CODIGOFUT,
       NVL(SGPEDUCACION,0) SGPEDUCACION,
       NVL(SGPSALUD,0) SGPSALUD,
       NVL(SGPCULTURA,0) SGPCULTURA,
       NVL(SGPAPSB,0) SGPAPSB,
       NVL(SGPDEPORTE,0) SGPDEPORTE,
       NVL(RECURSOSPROPIOS,0) RECURSOSPROPIOS,
       NVL(SGPLIBREINVERSION,0) SGPLIBREINVERSION,
       NVL(SGPLIBREDESTINACION,0) SGPLIBREDESTINACION,
       NVL(SGPALIMENTACIONESCOLAR,0) SGPALIMENTACIONESCOLAR,
       NVL(SGPECONOMIAPRIMERAINFANCIA,0) SGPECONOMIAPRIMERAINFANCIA,
       NVL(SGPMUNICIPIOSRIOMAGDALENA,0) SGPMUNICIPIOSRIOMAGDALENA,
       NVL(SGP,0) SGP,
       NVL(CONACION,0) CONACION,
       NVL(CODEPARTAMENTO,0) CODEPARTAMENTO,
       NVL(SGR,0) SGR,
       NVL(CREDITO,0) CREDITO,
       NVL(OTROS,0) OTROS,
       NVL(RECURSOSFUNCIONAMIENTO,0) RECURSOSFUNCIONAMIENTO,
       NVL(RECURSOSGESTIONADOS,0) RECURSOSGESTIONADOS
        FROM (
          SELECT DISTINCT  
          TF.NOMBRE,
          NVL(DCP.VALOR_DEBITO,0) VALOR_DEBITO,
          DN.ID_META_PRODUCTO ID, 
          PI.DESCRIPCION_INDICADOR META, 
          PI.DESCRIPCION INDICADOR, 
          (CASE WHEN TIPO_META_INDICADOR = 'MI' 
            THEN 'INCREMENTO' 
            ELSE (CASE WHEN TIPO_META_INDICADOR = 'MM'
                    THEN 'MANTENIMIENTO' 
                    ELSE (CASE WHEN TIPO_META_INDICADOR = 'MG' 
                            THEN 'GESTION'
                            ELSE 'REDUCCIÓN' END ) END ) END ) TIPOMETA,
          PIM.CANTIDAD_PROGRAMADA VALORESPERADO, 
          PIM.CANTIDAD_EJECUTADA VALORLOGRADO, 
          S.DESCRIPCION SECTOR, 
          S.SECTOR_PI CODIGOFUT
          FROM  BP_D_NOVEDADPROYECTO  DN 
             INNER JOIN BPNOVEDADPROYECTO N 
               ON  DN.COMPANIA    = N.COMPANIA 
               AND DN.TIPOT       = N.TIPOT
               AND DN.CLASET      = N.CLASET 
               AND DN.NOVEDAD     = N.CODIGO 
               AND DN.DEPENDENCIA = N.DEPENDENCIA
             INNER JOIN BPTIPONOVEDAD TN 
               ON  N.TIPOT    = TN.TIPOT 
               AND N.COMPANIA = TN.COMPANIA
               AND N.CLASET   = TN.CLASET
             INNER JOIN COMPROBANTE_PPTALAFECTADOS CP 
                 ON N.COMPANIA = CP.COMPANIA
                AND N.VIGENCIA = CP.ANO_AFECT
                AND N.TIPOT    = CP.TIPO_CPTE_AFECT
                AND N.CODIGO   = CP.COMPROBANTE_AFECT
             INNER JOIN DETALLE_COMPROBANTE_PPTAL DCP 
                ON CP.COMPANIA                      = DCP.COMPANIA
                AND CP.ANO                           = DCP.ANO
                AND CP.TIPO_CPTE                     = DCP.TIPO_CPTE
                AND CP.COMPROBANTE                   = DCP.COMPROBANTE
             INNER JOIN FUENTE_RECURSOS F 
               ON  DN.COMPANIA       = F.COMPANIA
               AND DN.ANORUBRO       = F.ANO 
               AND DN.FUENTERECURSOS = F.CODIGO 
             INNER JOIN BP_PLAN_INDICATIVO PI 
               ON  DN.COMPANIA                 = PI.COMPANIA 
               AND DN.VIGENCIA_PLAN_INDICATIVO = PI.VIGENCIA_INICIAL
               AND DN.ID_META_PRODUCTO         = PI.ID
             INNER JOIN BP_PLAN_INDICATIVO_METAS PIM 
               ON  PI.VIGENCIA_INICIAL = PIM.VIGENCIA_PLAN
               AND PI.ID               = PIM.ID_PLAN 
               AND PI.COMPANIA         = PIM.COMPANIA
               AND N.VIGENCIA          = PIM.VIGENCIA_META
            LEFT  JOIN SECTORES S 
              ON PI.COMPANIA = PI.COMPANIA
              AND  PI.SECTOR = S.CODIGO
            INNER JOIN BP_TIPORECURSOS TF 
               ON  F.COMPANIA     = TF.COMPANIA
               AND F.TIPO_RECURSO = TF.CODIGO 
          WHERE DN.COMPANIA     = UN_COMPANIA
            AND TN.CLASET       = 'B'
            AND TN.CLASENOVEDAD = 'S'
            AND CP.TIPO_CPTE    = 'RES'
            AND N.VIGENCIA      = UN_VIGENCIA
            AND N.ESTADO        = 'V'
        )     
        PIVOT 
        (
        SUM(NVL(VALOR_DEBITO,0))  
        FOR NOMBRE 
        IN ('SGPEducacion' "SGPEDUCACION",
            'SGPSalud' "SGPSALUD",
            'SGPCultura' "SGPCULTURA",
            'SGPAPSB' "SGPAPSB",
            'SGPDeporte' "SGPDEPORTE",
            'RecursosPropios' "RECURSOSPROPIOS",
            'SGPLibreInversion' "SGPLIBREINVERSION",
            'SGPLibreDestinacion' "SGPLIBREDESTINACION",
            'SGPAlimentacionEscolar' "SGPALIMENTACIONESCOLAR",
            'SGPEconomiaPrimeraInfancia' "SGPECONOMIAPRIMERAINFANCIA",
            'SGPMunicipiosRioMagdalena' "SGPMUNICIPIOSRIOMAGDALENA",
            'SGP' "SGP",
            'CoNacion' "CONACION",
            'CoDepartamento' "CODEPARTAMENTO",
            'SGR' "SGR",
            'Credito' "CREDITO",
            'Otros' "OTROS",
            'RecursosFuncionamiento' "RECURSOSFUNCIONAMIENTO",
            'RecursosGestionados' "RECURSOSGESTIONADOS")
            ))
	LOOP
		MI_SALIDA := MI_SALIDA                          || TO_CLOB(CURSOR_H1.ID 
							        || PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.META
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.INDICADOR 
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.TIPOMETA
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.VALORESPERADO
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.VALORLOGRADO
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.SECTOR
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.CODIGOFUT
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.RECURSOSPROPIOS
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.SGPEDUCACION
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.SGPSALUD
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.SGPAPSB
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.SGPDEPORTE
											|| PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.SGPCULTURA
                      || PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.SGPLIBREINVERSION
                      || PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.SGPLIBREDESTINACION
                      || PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.SGPALIMENTACIONESCOLAR
                      || PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.SGPECONOMIAPRIMERAINFANCIA
                      || PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.SGPMUNICIPIOSRIOMAGDALENA
                      || PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.SGP
                      || PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.CONACION
                      || PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.CODEPARTAMENTO
                      || PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.SGR
                      || PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.CREDITO
                      || PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.OTROS
                      || PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.RECURSOSFUNCIONAMIENTO
                      || PCK_DATOS.GL_SEPARADOR_COL || CURSOR_H1.RECURSOSGESTIONADOS
											|| PCK_DATOS.GL_SEPARADOR_REG 
											);
	END LOOP;	



RETURN MI_SALIDA;  
EXCEPTION
  WHEN OTHERS THEN
    RETURN '';


END FC_FRMSIRECIEFICACIA;

PROCEDURE PR_HEREDARSOLICITUDSCD (

/*
    NAME              : PR_HEREDARSOLICITUDSCD  
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : LAURA MELIZA BOTIA PEREZ
    DATE MIGRADOR     : 19/09/2018
    TIME              : 08:00 AM
    SOURCE MODULE     : 
    MODIFIER          : GUSTAVO ANDRÉS FIGUEREDO AVILA
    DATE MODIFIED     : 27/05/2021
    TIME              : 02:00 PM
    MODIFICATIONS     : Se agregan los campos CENTRO_COSTO y REFERENCIA
    DESCRIPTION       : Procedimiento que inserta a la tabla BP_D_NOVEDADPROYECTO los proyectos asociados a el tipoT seleccionado en el formulario.
    PARAMETERS        : COMPANIA             PCK_SUBTIPOS.TI_COMPANIA,               --Código de la compañia
                        TIPOTHIJO            BPNOVEDADPROYECTO.TIPOT%TYPE,           --Código del Tipot de la tabla BP_D_NOVEDADPROYECTO
                        CLASETHIJO           BPNOVEDADPROYECTO.CLASET%TYPE,          --Código de la ClaseT de la tabla BP_D_NOVEDADPROYECTO
                        NOVEDADHIJO          BPNOVEDADPROYECTO.CODIGO%TYPE,          --Código de la Novedad de la tabla BP_D_NOVEDAD_PROYECTO
                        DEPENDENCIAHIJO      BPNOVEDADPROYECTO.DEPENDENCIA%TYPE,     --Código de la dependencia de la tabla BP_NOVEDAD_PROYECTO
                        TIPOT                BPNOVEDADPROYECTO.TIPOT%TYPE,           --Código del TipoT de la tabla BPNOVEDADPROYECTO
                        CLASET               BPNOVEDADPROYECTO.CLASET%TYPE,          --Código de la clase de la tabla BPNOVEDADPROYECTO
                        NOVEDAD              BPNOVEDADPROYECTO.CODIGO%TYPE,          --Código de la novedad de la tabla BPNOVEDADPROYECTO
                        DEPENDENCIA          BPNOVEDADPROYECTO.DEPENDENCIA%TYPE      --Código de la dependencia de la tabla BPNOVEDADPROYECTO

      @NAME:    heredarSolicitudScd
      @METHOD:  POST
  */


  UN_COMPANIA           IN  PCK_SUBTIPOS.TI_COMPANIA,               --Código de la compañia
  UN_TIPOTHIJO          IN  BPNOVEDADPROYECTO.TIPOT%TYPE,           --Código del Tipot de la tabla BP_D_NOVEDADPROYECTO
  UN_CLASETHIJO         IN  BPNOVEDADPROYECTO.CLASET%TYPE,          --Código de la ClaseT de la tabla BP_D_NOVEDADPROYECTO
  UN_NOVEDADHIJO        IN  BPNOVEDADPROYECTO.CODIGO%TYPE,          --Código de la Novedad de la tabla BP_D_NOVEDAD_PROYECTO
  UN_DEPENDENCIAHIJO    IN  BPNOVEDADPROYECTO.DEPENDENCIA%TYPE,     --Código de la dependencia de la tabla BP_NOVEDAD_PROYECTO
  UN_TIPOT              IN  BPNOVEDADPROYECTO.TIPOT%TYPE,           --Código del TipoT de la tabla BPNOVEDADPROYECTO
  UN_CLASET             IN  BPNOVEDADPROYECTO.CLASET%TYPE,          --Código de la clase de la tabla BPNOVEDADPROYECTO
  UN_NOVEDAD            IN  BPNOVEDADPROYECTO.CODIGO%TYPE,          --Código de la novedad de la tabla BPNOVEDADPROYECTO
  UN_DEPENDENCIA        IN  BPNOVEDADPROYECTO.DEPENDENCIA%TYPE,     --Código de la dependencia de la tabla BPNOVEDADPROYECTO
  UN_USUARIO            IN  PCK_SUBTIPOS.TI_USUARIO


)AS 



BEGIN

  --Limpiar tabla si la novedad no es null


  IF UN_NOVEDAD IS NOT NULL THEN  

  DELETE FROM  BP_D_NOVEDADPROYECTO 
  WHERE COMPANIA = UN_COMPANIA  
      AND  TIPOT = UN_TIPOT
      AND  CLASET = UN_CLASET 
      AND  NOVEDAD = UN_NOVEDAD
      AND  DEPENDENCIA = UN_DEPENDENCIA;

  END IF;



  INSERT INTO BP_D_NOVEDADPROYECTO (  ACTIVIDAD,
                                        ACTUALIZADO_EN_PLAN,
                                        ANORUBRO,
                                        AUXILIAR,
                                        BARRIO_L,
                                        CANTIDAD,
                                        CANTIDAD_PLAN,
                                        CIUDAD_L,
                                        CLASET,
                                        CMPTE_AFECTADO,
                                        CODIGO,
                                        COMPANIA,
                                        COMPONENTE,
                                        CONPROGRAMACION,
                                        CONPROGRAMACION_C,
                                        CONPROGRAMACION_P,
                                        CONTRATO,
                                        CREATED_BY,
                                        DATE_CREATED,
                                        DEPARTAMENTO_L,
                                        DEPENDENCIA,
                                        DEPENDENCIA_FECHAMODIFICADO,
                                        DEPENDENCIA_HORAMODIFICADO,
                                        DEPENDENCIA_MODIFICADOPOR,
                                        ESPECIFICACION,
                                        FUENTERECURSOS,
                                        ID_META_PRODUCTO,
                                        INDICADOR,
                                        ITEM_AFECT,
                                        LOCALIZACION,
                                        MODIFICACIONVALORAPROBADO,
                                        NOVEDAD,
                                        PAIS_L,
                                        PERIODO,
                                        PRIORIDAD,
                                        PROGRAMACION,
                                        PROYECTO,
                                        RUBROPRESUPUESTAL,
                                        SECTOR,
                                        SOLOCOMPONENTE,
                                        TEMP_CANTIDAD,
                                        TEMP_ID_META,
                                        TEMPORAL,
                                        TIPOCOMPONENTE,
                                        TIPO_CONTRATO,
                                        TIPO_CPTE_AFECT,
                                        TIPOINDICADOR,
                                        TIPOT,
                                        VALORAFECTADO,
                                        VALORAPROBADO,
                                        VALOR_APROBADO_TMP,
                                        VALOR_APROBADO_TMPA,
                                        VALORDISMINUIDO,
                                        VALORPROGRAMADO,
                                        VALORSOLICITADO,
                                        VIGENCIA_PLAN_INDICATIVO,
                                        CENTRO_COSTO,
                                        REFERENCIA,
                                        SECTORRUBRO,
                                        PROGRAMARUBRO,
                                        SUBPROGRAMARUBRO,
                                        CODIGOPRODUCTO,
                                        CODIGOBPIN,
                                        CODIGOCCPET,
                                        CODIGOCPCDANE,
                                        CODIGOUNIDADEJECUTORA,
                                        CODIGOFUENTE,
                                        CODIGOCCPETREGALIAS,
                                        CODIGODETALLESECTORIAL
                                      )                                      
                                       SELECT  ACTIVIDAD,
                                        ACTUALIZADO_EN_PLAN,
                                        ANORUBRO,
                                        AUXILIAR,
                                        BARRIO_L,
                                        CANTIDAD,
                                        CANTIDAD_PLAN,
                                        CIUDAD_L,
                                        UN_CLASET,
                                        CMPTE_AFECTADO,
                                        CODIGO,
                                        COMPANIA,
                                        COMPONENTE,
                                        CONPROGRAMACION,
                                        CONPROGRAMACION_C,
                                        CONPROGRAMACION_P,
                                        CONTRATO,
                                        UN_USUARIO,
                                        SYSDATE,
                                        DEPARTAMENTO_L,
                                        UN_DEPENDENCIA,
                                        DEPENDENCIA_FECHAMODIFICADO,
                                        DEPENDENCIA_HORAMODIFICADO,
                                        DEPENDENCIA_MODIFICADOPOR,
                                        ESPECIFICACION,
                                        FUENTERECURSOS,
                                        ID_META_PRODUCTO,
                                        INDICADOR,
                                        ITEM_AFECT,
                                        LOCALIZACION,
                                        MODIFICACIONVALORAPROBADO,
                                        UN_NOVEDAD,
                                        PAIS_L,
                                        PERIODO,
                                        PRIORIDAD,
                                        PROGRAMACION,
                                        PROYECTO,
                                        RUBROPRESUPUESTAL,
                                        SECTOR,
                                        SOLOCOMPONENTE,
                                        TEMP_CANTIDAD,
                                        TEMP_ID_META,
                                        TEMPORAL,
                                        TIPOCOMPONENTE,
                                        TIPO_CONTRATO,
                                        TIPO_CPTE_AFECT,
                                        TIPOINDICADOR,
                                        UN_TIPOT,
                                        VALORAFECTADO,
                                        VALORAPROBADO,
                                        VALOR_APROBADO_TMP,
                                        VALOR_APROBADO_TMPA,
                                        VALORDISMINUIDO,
                                        VALORPROGRAMADO,
                                        VALORSOLICITADO,
                                        VIGENCIA_PLAN_INDICATIVO,
                                        CENTRO_COSTO,
                                        REFERENCIA,
                                        SECTORRUBRO,
                                        PROGRAMARUBRO,
                                        SUBPROGRAMARUBRO,
                                        CODIGOPRODUCTO,
                                        CODIGOBPIN,
                                        CODIGOCCPET,
                                        CODIGOCPCDANE,
                                        CODIGOUNIDADEJECUTORA,
                                        CODIGOFUENTE,
                                        CODIGOCCPETREGALIAS,
                                        CODIGODETALLESECTORIAL

                                              FROM BP_D_NOVEDADPROYECTO
                                              WHERE COMPANIA = UN_COMPANIA
                                              AND TIPOT      =
                                                CASE WHEN UN_TIPOTHIJO LIKE 'T'
                                                  THEN TIPOT
                                                  ELSE NVL(UN_TIPOTHIJO, TIPOT)
                                              END
                                              AND DEPENDENCIA =
                                                CASE
                                                  WHEN UN_DEPENDENCIAHIJO LIKE 'T'
                                                  THEN DEPENDENCIA
                                                  ELSE NVL(UN_DEPENDENCIAHIJO ,DEPENDENCIA )
                                                END
                                              AND CLASET  = UN_CLASETHIJO
                                              AND NOVEDAD = UN_NOVEDADHIJO
                                              ORDER BY CODIGO  ;    


          PCK_BANCOS_PROY5.PR_ACTUALIZARESTADONOVEDAD(  UN_COMPANIA       => UN_COMPANIA,        
                                                        UN_TIPOT          => UN_TIPOT,       
                                                        UN_CLASET         => UN_CLASET,      
                                                        UN_DOCUMENTO      => UN_NOVEDAD,      
                                                        UN_DEPENDENCIA    => UN_DEPENDENCIA,       
                                                        UN_INDACTUALIZAR  => -1,      
                                                        UN_USUARIO        => UN_USUARIO);                              





END PR_HEREDARSOLICITUDSCD;

PROCEDURE PR_CONSULTARDOCUMENTOAFECTAR
(
/*
    NAME              : PR_CONSULTARDOCUMENTOAFECTAR  
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : LAURA MELIZA BOTIA PEREZ
    DATE MIGRADOR     : 20/09/2018
    TIME              : 11:00 AM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : Procedimiento que inserta a la tabla BP_D_NOVEDADPROYECTO los proyectos asociados a el tipoT seleccionado en el formulario.
    PARAMETERS        : COMPANIA              PCK_SUBTIPOS.TI_COMPANIA,               --Código de la compañia
                        TIPOT                 BPNOVEDADPROYECTO.TIPOT%TYPE,           --Código del Tipot de la tabla BP_D_NOVEDADPROYECTO
                        CLASET                BPNOVEDADPROYECTO.CLASET%TYPE,          --Código de la ClaseT de la tabla BP_D_NOVEDADPROYECTO
                        DOCUMENTO             BPNOVEDADPROYECTO.CODIGO%TYPE,          --Código de la Novedad de la tabla BP_D_NOVEDAD_PROYECTO
                        DEPENDENCIAHIJO       BPNOVEDADPROYECTO.DEPENDENCIA%TYPE,     --Código de la dependencia de la tabla BP_NOVEDAD_PROYECTO


      @NAME:    consultadDcumentoAfectar
      @METHOD:  POST
  */
  UN_COMPANIA               IN  PCK_SUBTIPOS.TI_COMPANIA,                       --Código de la compañia
  UN_TIPOT                  IN  BPNOVEDADPROYECTO.TIPOT%TYPE,                   --Código del Tipot de la tabla BP_D_NOVEDADPROYECTO
  UN_CLASET                 IN  BPNOVEDADPROYECTO.CLASET%TYPE,                  --Código de la ClaseT de la tabla BP_D_NOVEDADPROYECTO
  UN_DOCUMENTO              IN  BPNOVEDADPROYECTO.CODIGO%TYPE,                  --Código de el documento afectar de la tabla BP_D_NOVEDAD_PROYECTO
  UN_DEPENDENCIA            IN  BPNOVEDADPROYECTO.DEPENDENCIA%TYPE ,            --Código de la dependencia de la tabla BP_NOVEDAD_PROYECTO
  UN_USUARIO                IN  PCK_SUBTIPOS.TI_USUARIO

)AS 

  MI_TIPOT_AFECTAR          BPNOVEDADPROYECTO.TIPOT_AFECTAR%TYPE;
  MI_CLASET_AFECTAR         BPNOVEDADPROYECTO.CLASET_AFECTAR%TYPE;
  MI_DOCUMENTO_AFECTAR      BPNOVEDADPROYECTO.DOCUMENTO_AFECTAR%TYPE;
  MI_DEPENDENCIA_AFECTAR    BPNOVEDADPROYECTO.DEPENDENCIA_AFECTAR%TYPE;
  MI_ACTUALIZAR             PCK_SUBTIPOS.TI_ENTERO;
  MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
  MI_TABLA_NP               PCK_SUBTIPOS.TI_TABLA DEFAULT 'BPNOVEDADPROYECTO';
  MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
  MI_REEMPLAZOS             PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_TIPOT                  BP_D_NOVEDADPROYECTO.TIPOT%TYPE;
  MI_CLASET                 BP_D_NOVEDADPROYECTO.CLASET%TYPE;
  MI_NOVEDAD                BP_D_NOVEDADPROYECTO.NOVEDAD%TYPE;
  MI_DEPENDENCIA            BP_D_NOVEDADPROYECTO.DEPENDENCIA%TYPE;
  MI_ELIMINAR               PCK_SUBTIPOS.TI_ENTERO;


BEGIN


    BEGIN
    BEGIN


    MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||'''
                       AND TIPOT = '''||UN_TIPOT||'''
                       AND CLASET = '''||UN_CLASET||'''
                       AND NOVEDAD = '||UN_DOCUMENTO||'
                       AND DEPENDENCIA = '''||UN_DEPENDENCIA||''' ';

    MI_ELIMINAR := PCK_DATOS.FC_ACME(UN_TABLA     => 'BP_D_NOVEDADPROYECTO',
                                     UN_ACCION    => 'E',
                                     UN_CONDICION => MI_CONDICION);

                                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
        MI_REEMPLAZOS(1).CLAVE := 'NOVEDAD';
        MI_REEMPLAZOS(1).VALOR := UN_DOCUMENTO;

        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
      END;
      --MODIFICAR EL ERROR
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS  THEN    
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD    => SQLCODE,
                          UN_ERROR_COD  => PCK_ERRORES.ERR_BANCO_CONSULTARSCD,
                          UN_TABLAERROR => MI_TABLA_NP,
                          UN_REEMPLAZOS => MI_REEMPLAZOS 
                        );
      END;

      PCK_BANCOS_PROY5.PR_ACTUALIZARESTADONOVEDAD(  UN_COMPANIA       => UN_COMPANIA,        
                                                    UN_TIPOT          => UN_TIPOT,       
                                                    UN_CLASET         => UN_CLASET,      
                                                    UN_DOCUMENTO      => UN_DOCUMENTO,      
                                                    UN_DEPENDENCIA    => UN_DEPENDENCIA,       
                                                    UN_INDACTUALIZAR  => 0,      
                                                    UN_USUARIO        => UN_USUARIO);


END PR_CONSULTARDOCUMENTOAFECTAR;

--19
FUNCTION FC_SOLI_ORIGINALMONITOR1(
/*
    NAME              : Función FC_SOLI_ORIGINALMONITOR-> En Access: SOLICITUD_ORIGINALMONITOR
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : JHON FREDY HERNANDEZ CASTRO
    DATE MIGRATION    : 21/03/2018
    TIME              : 04:00 AM
    SOURCE MODULE     : BANCO DE PROYECTOS/PROCESOS/MONITORES/MONITORES NOVEDADES PROYECTOS (SysmanBP2018.02.02)
    DESCRIPTION       : Permite obtener el codigo de la tabla BPNOVEDADPROYECTO  segun los parametros ingresados de TIPOT
    ,SOLICITUD Y MODIFICACION
    MODIFIED BY       :
    PARAMETERS        : UN_TIPOT => tipo de solicitud (CDP, SCD...)
    UN_SOLICITUD => codigo de la solicitud
    UN_MODIFICACION =>  Valor booleano con valores (0,-1) que se trae desde el campo MODIFICACION de BPNOVEDADPROYECTO
    @NAME  : solicitudOriginalMonitor
    @METHOD: GET
    */
    UN_COMPANIA     IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_TIPOT        IN BPNOVEDADPROYECTO.TIPOT%TYPE,
    UN_SOLICITUD    IN BPNOVEDADPROYECTO.CODIGO%TYPE)


    RETURN VARCHAR2 
    AS
       MI_RTA            VARCHAR2(12CHAR);
    BEGIN
  BEGIN 
       FOR MI_RS IN( SELECT BPNPMOD.CODIGO,
                            BPNPMOD.TIPOT,
                            BPNPMOD.DOCUMENTO_AFECTAR,  
                            BPNPMOD.DEPENDENCIA_AFECTAR
                       FROM BPNOVEDADPROYECTO BPNPCDP
                            LEFT JOIN BPNOVEDADPROYECTO BPNPMOD
                                   ON BPNPCDP.COMPANIA            = BPNPMOD.COMPANIA
                                  AND BPNPCDP.TIPOT_AFECTAR       = BPNPMOD.TIPOT
                                --AND BPNPCDP.CLASET_AFECTAR    = BPNPMOD.CLASET_AFECTAR
                                  AND BPNPCDP.DOCUMENTO_AFECTAR   = BPNPMOD.CODIGO
                                  AND BPNPCDP.DEPENDENCIA_AFECTAR = BPNPMOD.DEPENDENCIA_AFECTAR
                      WHERE BPNPCDP.COMPANIA    = UN_COMPANIA    
                        AND BPNPCDP.CODIGO      = UN_SOLICITUD 
                        AND  BPNPCDP.TIPOT      = UN_TIPOT
                        AND  BPNPCDP.MODIFICACION IN (-1) )   
           LOOP

                  SELECT BPNOVEDADPROYECTO.CODIGO
                    INTO MI_RTA
                    FROM BPNOVEDADPROYECTO 
                   WHERE BPNOVEDADPROYECTO.COMPANIA            = UN_COMPANIA
                     AND BPNOVEDADPROYECTO.TIPOT       = 'CDP'
                     AND BPNOVEDADPROYECTO.TIPOT_AFECTAR NOT IN 'MOD'
                     AND BPNOVEDADPROYECTO.DOCUMENTO_AFECTAR   = MI_RS.DOCUMENTO_AFECTAR
                     AND BPNOVEDADPROYECTO.DEPENDENCIA         = MI_RS.DEPENDENCIA_AFECTAR;   

          END LOOP;    
           RETURN MI_RTA;
     END;
END FC_SOLI_ORIGINALMONITOR1;


PROCEDURE PR_ACTUALIZARESTADONOVEDAD(

/*
    NAME              : PR_ACTUALIZARESTADONOVEDAD  
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : LAURA MELIZA BOTIA PEREZ
    DATE MIGRADOR     : 21/09/2018
    TIME              : 05:00 PM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : Procedimiento que inserta a la tabla BP_D_NOVEDADPROYECTO los proyectos asociados a el tipoT seleccionado en el formulario.
    PARAMETERS        : COMPANIA              PCK_SUBTIPOS.TI_COMPANIA,               --Código de la compañia
                        TIPOT                 BPNOVEDADPROYECTO.TIPOT%TYPE,           --Código del Tipot de la tabla BP_D_NOVEDADPROYECTO
                        CLASET                BPNOVEDADPROYECTO.CLASET%TYPE,          --Código de la ClaseT de la tabla BP_D_NOVEDADPROYECTO
                        DOCUMENTO             BPNOVEDADPROYECTO.CODIGO%TYPE,          --Código de la Novedad de la tabla BP_D_NOVEDAD_PROYECTO
                        DEPENDENCIAHIJO       BPNOVEDADPROYECTO.DEPENDENCIA%TYPE,     --Código de la dependencia de la tabla BP_NOVEDAD_PROYECTO


      @NAME:    actualizarestado
      @METHOD:  POST
  */



  UN_COMPANIA               IN  PCK_SUBTIPOS.TI_COMPANIA,                       --Código de la compañia
  UN_TIPOT                  IN  BPNOVEDADPROYECTO.TIPOT%TYPE,                   --Código del Tipot de la tabla BP_D_NOVEDADPROYECTO
  UN_CLASET                 IN  BPNOVEDADPROYECTO.CLASET%TYPE,                  --Código de la ClaseT de la tabla BP_D_NOVEDADPROYECTO
  UN_DOCUMENTO              IN  BPNOVEDADPROYECTO.CODIGO%TYPE,                  --Código de el documento afectar de la tabla BP_D_NOVEDAD_PROYECTO
  UN_DEPENDENCIA            IN  BPNOVEDADPROYECTO.DEPENDENCIA%TYPE ,            --Código de la dependencia de la tabla BP_NOVEDAD_PROYECTO
  UN_INDACTUALIZAR          IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0,                --Cuando es 0 el proceso es de eliminación de lo contrario es de actualización.
  UN_USUARIO                IN  PCK_SUBTIPOS.TI_USUARIO


)AS
  MI_TIPOT_AFECTAR          BPNOVEDADPROYECTO.TIPOT_AFECTAR%TYPE;
  MI_CLASET_AFECTAR         BPNOVEDADPROYECTO.CLASET_AFECTAR%TYPE;
  MI_DOCUMENTO_AFECTAR      BPNOVEDADPROYECTO.DOCUMENTO_AFECTAR%TYPE;
  MI_DEPENDENCIA_AFECTAR    BPNOVEDADPROYECTO.DEPENDENCIA_AFECTAR%TYPE;
  MI_ACTUALIZAR             PCK_SUBTIPOS.TI_ENTERO;
  MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
  MI_TABLA_NP               PCK_SUBTIPOS.TI_TABLA DEFAULT 'BPNOVEDADPROYECTO';
  MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
  MI_REEMPLAZOS             PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN
  BEGIN
    BEGIN

        SELECT TIPOT_AFECTAR, CLASET_AFECTAR, DOCUMENTO_AFECTAR, DEPENDENCIA_AFECTAR 
        INTO MI_TIPOT_AFECTAR,MI_CLASET_AFECTAR,MI_DOCUMENTO_AFECTAR,MI_DEPENDENCIA_AFECTAR
        FROM BPNOVEDADPROYECTO
        WHERE COMPANIA = UN_COMPANIA
        AND TIPOT  = UN_TIPOT 
        AND CLASET = UN_CLASET
        AND CODIGO = UN_DOCUMENTO
        AND DEPENDENCIA = UN_DEPENDENCIA;

    EXCEPTION WHEN NO_DATA_FOUND THEN

        MI_REEMPLAZOS(1).CLAVE := 'CODIGO';
        MI_REEMPLAZOS(1).VALOR := UN_DOCUMENTO;

    RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
    END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS  THEN    
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD   => SQLCODE,
                          UN_ERROR_COD => PCK_ERRORES.ERR_BANCO_CONSULTARSOL,
                          UN_TABLAERROR => MI_TABLA_NP,
                          UN_REEMPLAZOS => MI_REEMPLAZOS 
                        );
    END;

  BEGIN
    BEGIN      

      --ELIMINAR
      IF UN_INDACTUALIZAR IN (0) THEN

        MI_CAMPOS := ' AFECTADO      = '|| CASE WHEN UN_TIPOT IN ('MOD') THEN -1 ELSE 0 END||
                     ',DATE_MODIFIED  = SYSDATE,
                       MODIFIED_BY    = '''||UN_USUARIO||''' ';

        MI_CAMPOS :=MI_CAMPOS||  ',ESTADO       = ''V'' ';  

      END IF;
      --ACTUALIZAR

      IF UN_INDACTUALIZAR NOT IN (0) THEN

          MI_CAMPOS := ' AFECTADO      =  -1,
                     DATE_MODIFIED     = SYSDATE,
                     MODIFIED_BY       = '''||UN_USUARIO||''' ,';

          MI_CAMPOS := MI_CAMPOS ||' ESTADO = '''|| CASE WHEN UN_TIPOT IN ('MOD') THEN 'A' ELSE 'V' END|| '''';



      END IF;


      MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||'''
                       AND TIPOT = '''||MI_TIPOT_AFECTAR||'''
                       AND CLASET = '''||MI_CLASET_AFECTAR||'''
                       AND CODIGO = '||MI_DOCUMENTO_AFECTAR||'
                       AND DEPENDENCIA = '''||MI_DEPENDENCIA_AFECTAR||''' ';


      MI_ACTUALIZAR := PCK_DATOS.FC_ACME( UN_TABLA     => MI_TABLA_NP,
                                          UN_ACCION    => 'M',
                                          UN_CAMPOS    => MI_CAMPOS,                                                            
                                          UN_CONDICION => MI_CONDICION);



      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
        MI_REEMPLAZOS(1).CLAVE := 'CODIGO';
        MI_REEMPLAZOS(1).VALOR := MI_DOCUMENTO_AFECTAR;

        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
      END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS  THEN    
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD    => SQLCODE,
                          UN_ERROR_COD  => PCK_ERRORES.ERR_BANCO_CONSULTARSCD,
                          UN_TABLAERROR => MI_TABLA_NP,
                          UN_REEMPLAZOS => MI_REEMPLAZOS 
                        );
  END;

  BEGIN
    BEGIN  
   --ACTUALIZAR



 IF UN_TIPOT IN ('MOD') AND UN_INDACTUALIZAR NOT IN (0) THEN

  MI_CAMPOS := 'ESTADO  = ''A'',
               DATE_MODIFIED  = SYSDATE,
               MODIFIED_BY    = '''||UN_USUARIO||'''';

  MI_CONDICION := ' COMPANIA = '''||UN_COMPANIA||'''
                    AND TIPOT = ''CDP''
                    AND TIPOT_AFECTAR = '''||MI_TIPOT_AFECTAR||'''
                    AND CLASET_AFECTAR = '''||MI_CLASET_AFECTAR||'''
                    AND DOCUMENTO_AFECTAR = '||MI_DOCUMENTO_AFECTAR||'
                    AND DEPENDENCIA_AFECTAR = '''||MI_DEPENDENCIA_AFECTAR||''' ';

   MI_ACTUALIZAR := PCK_DATOS.FC_ACME( UN_TABLA     => MI_TABLA_NP,
                                          UN_ACCION    => 'M',
                                          UN_CAMPOS    => MI_CAMPOS,                                                            
                                          UN_CONDICION => MI_CONDICION);



  END IF;



   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
        MI_REEMPLAZOS(1).CLAVE := 'CODIGO';
        MI_REEMPLAZOS(1).VALOR := MI_DOCUMENTO_AFECTAR;

        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
      END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS  THEN    
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD    => SQLCODE,
                          UN_ERROR_COD  => PCK_ERRORES.ERR_BANCO_CONSULTARSCD,
                          UN_TABLAERROR => MI_TABLA_NP,
                          UN_REEMPLAZOS => MI_REEMPLAZOS 
                        );
  END;

/**
BEGIN
    BEGIN      


      IF UN_TIPOT IN ('MOD') THEN

      MI_CAMPOS := ' ESTADO      =  ''A'',
                     DATE_MODIFIED  = SYSDATE,
                     MODIFIED_BY    = '''||UN_USUARIO||''' ';  


      MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||'''
                       AND TIPOT = '''||UN_TIPOT||'''
                       AND CLASET = '''||UN_CLASET||'''
                       AND CODIGO = '||UN_DOCUMENTO||'
                       AND DEPENDENCIA = '''||UN_DEPENDENCIA||''' ';

      END IF;
      MI_ACTUALIZAR := PCK_DATOS.FC_ACME( UN_TABLA     => MI_TABLA_NP,
                                          UN_ACCION    => 'M',
                                          UN_CAMPOS    => MI_CAMPOS,                                                            
                                          UN_CONDICION => MI_CONDICION);



      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
        MI_REEMPLAZOS(1).CLAVE := 'CODIGO';
        MI_REEMPLAZOS(1).VALOR := MI_DOCUMENTO_AFECTAR;

        RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
      END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS  THEN    
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD    => SQLCODE,
                          UN_ERROR_COD  => PCK_ERRORES.ERR_BANCO_CONSULTARSCD,
                          UN_TABLAERROR => MI_TABLA_NP,
                          UN_REEMPLAZOS => MI_REEMPLAZOS 
                        );
  END;
**/


END PR_ACTUALIZARESTADONOVEDAD;


FUNCTION FC_CREAR_MANTENIMIENTO

  /*
   NAME              : PR_ACTUALIZARESTADONOVEDAD  
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : LAURA MELIZA BOTIA PEREZ
    DATE MIGRADOR     : 21/09/2018
    TIME              : 05:00 PM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : 
    PARAMETERS        : 


      @NAME:    crearMante
      @METHOD:  POST
  */
 (
  UN_COMPANIA     IN   PCK_SUBTIPOS.TI_COMPANIA,
  UN_VIGENCIA     IN   PCK_SUBTIPOS.TI_STRSQL,
  UN_CODIGO       IN   PCK_SUBTIPOS.TI_STRSQL,
  UN_USUARIO      IN   PCK_SUBTIPOS.TI_USUARIO
)
RETURN PCK_SUBTIPOS.TI_ENTERO_LARGO
AS 
	MI_TABLA 		      PCK_SUBTIPOS.TI_TABLA;
	MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
	MI_MSGERROR   	  PCK_SUBTIPOS.TI_CLAVEVALOR;
	MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
	MI_VALORES        PCK_SUBTIPOS.TI_VALORES;	
  MI_RTA            PCK_SUBTIPOS.TI_DOBLE;
  MI_RTA_PROG       PCK_SUBTIPOS.TI_DOBLE;
  MI_MERGEUSING     PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE    PCK_SUBTIPOS.TI_MERGEENLACE; 
  MI_MERGEEXISTE    PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_MERGENOEXIS    PCK_SUBTIPOS.TI_MERGENOEXISTE;
  MI_RTADOS            CLOB;


BEGIN
MI_RTA_PROG := 0;
     MI_CONDICION := '';
     MI_CAMPOS := ' COMPANIA,
                    CODIGOPROYECTO,
                    CODIGOCOMPONENTE,
                    TIPOCOMPONENTE,
                    CODIGOACTIVIDAD,
                    VIGENCIA,
                    TIPOESTADO,
                    CODIGO,
                    FECHA,
                    CODIGO_REPROGRAMADO,
                    PERIOCIDAD,
                    VALOR1,
                    PORCENTAJE1,
                    VALOR2,
                    PORCENTAJE2,
                    VALOR3,
                    PORCENTAJE3,
                    VALOR4,
                    PORCENTAJE4,
                    VALOR5,
                    PORCENTAJE5,
                    VALOR6,
                    PORCENTAJE6,
                    VALOR7,
                    PORCENTAJE7,
                    VALOR8,
                    PORCENTAJE8,
                    VALOR9,
                    PORCENTAJE9,
                    VALOR10,
                    PORCENTAJE10,
                    VALOR11,
                    PORCENTAJE11,
                    VALOR12,
                    PORCENTAJE12,
                    VALORTOTAL,
                    CANTIDAD,
                    CODIGO_QUEAPRUEBA,
                    CREATED_BY,
                    DATE_CREATED';


     MI_VALORES := ' SELECT CA.COMPANIA,
                            CA.CODIGOPROYECTO,
                            CA.COMPONENTE,
                            CA.TIPOCOMPONENTE,
                            CA.ACTIVIDAD,
                            CA.VIGENCIA,
                            ''P''     AS TIPOESTADO,
                            1       AS CODIGO,
                            SYSDATE AS FECHA,
                            0       AS CODIGO_REPROGRAMADO,
                            P.PERIOCIDAD,
                            CASE WHEN P.PERIOCIDAD       = ''01'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END                AS VALOR1,
                            CASE WHEN P.VALORTOTAL NOT IN (0) THEN ROUND((CASE WHEN P.PERIOCIDAD=''01'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END )/P.VALORTOTAL,2) ELSE 0 END PORCENTAJE1,   
                            CASE WHEN P.PERIOCIDAD       =''02'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END                       AS VALOR2,
                            CASE WHEN P.VALORTOTAL NOT IN (0) THEN ROUND((CASE WHEN  P.PERIOCIDAD=''02'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END )/P.VALORTOTAL,2) ELSE 0 END PORCENTAJE2,  
                            CASE WHEN P.PERIOCIDAD       =''03'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END                      AS VALOR3,
                            CASE WHEN P.VALORTOTAL NOT IN (0) THEN ROUND((CASE WHEN P.PERIOCIDAD=''03'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END )/P.VALORTOTAL,2) ELSE 0 END PORCENTAJE3,  
                            CASE WHEN P.PERIOCIDAD       =''04'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END                      AS VALOR4,
                            CASE WHEN P.VALORTOTAL NOT IN (0) THEN ROUND((CASE WHEN P.PERIOCIDAD=''04'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END )/P.VALORTOTAL,2) ELSE 0 END PORCENTAJE4,  
                            CASE WHEN P.PERIOCIDAD       =''05'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END                   AS VALOR5,
                            CASE WHEN P.VALORTOTAL NOT IN (0) THEN ROUND((CASE WHEN P.PERIOCIDAD=''05'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END )/P.VALORTOTAL,2) ELSE 0 END PORCENTAJE5,  
                            CASE WHEN P.PERIOCIDAD       =''06'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END                       AS VALOR6,
                            CASE WHEN P.VALORTOTAL NOT IN (0) THEN ROUND((CASE WHEN P.PERIOCIDAD=''06'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END )/P.VALORTOTAL,2) ELSE 0 END PORCENTAJE6,  
                            CASE WHEN P.PERIOCIDAD       =''07'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END                       AS VALOR7,
                            CASE WHEN P.VALORTOTAL NOT IN (0) THEN ROUND((CASE WHEN P.PERIOCIDAD=''07'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END )/P.VALORTOTAL,2) ELSE 0 END PORCENTAJE7,  
                            CASE WHEN P.PERIOCIDAD       =''08'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END                       AS VALOR8,
                            CASE WHEN P.VALORTOTAL NOT IN (0) THEN ROUND((CASE WHEN P.PERIOCIDAD=''08'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END )/P.VALORTOTAL,2) ELSE 0 END PORCENTAJE8, 
                            CASE WHEN P.PERIOCIDAD       =''09'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END                       AS VALOR9, 
                            CASE WHEN P.VALORTOTAL NOT IN (0) THEN ROUND((CASE WHEN P.PERIOCIDAD=''09'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END )/P.VALORTOTAL,2) ELSE 0 END PORCENTAJE9, 
                            CASE WHEN P.PERIOCIDAD       =''10'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END                       AS VALOR10,
                            CASE WHEN P.VALORTOTAL NOT IN (0) THEN ROUND((CASE WHEN P.PERIOCIDAD=''10'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END )/P.VALORTOTAL,2) ELSE 0 END PORCENTAJE10,  
                            CASE WHEN P.PERIOCIDAD       =''11'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END                       AS VALOR11,
                            CASE WHEN P.VALORTOTAL NOT IN (0) THEN ROUND((CASE WHEN P.PERIOCIDAD=''11'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END )/P.VALORTOTAL,2) ELSE 0 END PORCENTAJE11,  
                            CASE WHEN P.PERIOCIDAD       =''12'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END                       AS VALOR12,
                            CASE WHEN P.VALORTOTAL NOT IN (0) THEN ROUND((CASE WHEN P.PERIOCIDAD=''12'' THEN CA.COSTOTOTAL/P.PERIOCIDAD ELSE 0 END )/P.VALORTOTAL,2) ELSE 0 END PORCENTAJE11,    
                            CA.COSTOTOTAL,
                            CA.CANTIDAD,
                            0                        AS CODIGO_QUEAPRUEBA,
                            '''||UN_USUARIO||'''     AS CREATED_BY,
                            SYSDATE                  AS DATE_CREATED
                       FROM PROYECTOS  P
                      INNER JOIN COMPONENTES_ACTIVIDADES   CA
                         ON P.COMPANIA         = CA.COMPANIA
                        AND P.CODIGO           = CA.CODIGOPROYECTO                        
                       LEFT JOIN PROGRAMACION PROGR
                         ON CA.COMPANIA       = PROGR.COMPANIA
                        AND CA.VIGENCIA       = PROGR.VIGENCIA
                        AND CA.ACTIVIDAD      = PROGR.CODIGOACTIVIDAD
                        AND CA.TIPOCOMPONENTE = PROGR.TIPOCOMPONENTE
                        AND CA.COMPONENTE     = PROGR.CODIGOCOMPONENTE
                        AND CA.CODIGOPROYECTO = PROGR.CODIGOPROYECTO                         
                      WHERE P.COMPANIA        = '''||UN_COMPANIA||''' 
                        AND PROGR.CODIGO     IS NULL' ;






        IF UN_VIGENCIA  <> 'TODAS'  THEN 
            MI_VALORES := MI_VALORES||' AND P.VIGENCIAINICIO = '||UN_VIGENCIA; 
        END IF;

        IF UN_CODIGO <> 'TODOS' THEN 
            MI_VALORES := MI_VALORES||' AND P.CODIGO = '''||UN_CODIGO||'''';
        END IF;

       	MI_TABLA := 'PROGRAMACION';

        BEGIN 
            BEGIN
                MI_RTA_PROG := PCK_DATOS.FC_ACME(	UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'IS',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_VALORES 	 => MI_VALORES );                 
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS; 
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN               
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                         UN_ERROR_COD => PCK_ERRORES.ERR_BANCOP_INSET_PROGRAMACION); 

        END;



        MI_TABLA       := 'PROGRAMACION';
        MI_MERGEUSING  := 'SELECT DISTINCT CA.COMPANIA,
                                  CA.VIGENCIA,
                                  CA.ACTIVIDAD,
                                  CA.TIPOCOMPONENTE,
                                  CA.COMPONENTE,
                                  CA.CODIGOPROYECTO,
                                  CA.COSTOTOTAL
                             FROM COMPONENTES_ACTIVIDADES CA
                            INNER JOIN PROGRAMACION P
                               ON CA.COMPANIA       = P.COMPANIA          
                              AND CA.VIGENCIA       = P.VIGENCIA         
                              AND CA.ACTIVIDAD      = P.CODIGOACTIVIDAD  
                              AND CA.CODIGOPROYECTO = P.CODIGOPROYECTO  
                              AND CA.TIPOCOMPONENTE = P.TIPOCOMPONENTE   
                              AND CA.COMPONENTE     =   P.CODIGOCOMPONENTE 
                            WHERE CA.COMPANIA      = '''||UN_COMPANIA||'''
                            '||MI_CONDICION||'
                            AND P.VALOR1         <> CA.COSTOTOTAL
                            AND P.PERIOCIDAD     IN (''01'')';



         MI_MERGEENLACE :='   TABLA.COMPANIA         = VISTA.COMPANIA 
                          AND TABLA.VIGENCIA         = VISTA.VIGENCIA
                          AND TABLA.CODIGOACTIVIDAD  = VISTA.ACTIVIDAD
                          AND TABLA.CODIGOPROYECTO   = VISTA.CODIGOPROYECTO
                          AND TABLA.TIPOCOMPONENTE   = VISTA.TIPOCOMPONENTE
                          AND TABLA.CODIGOCOMPONENTE = VISTA.COMPONENTE';


         MI_MERGEEXISTE := ' UPDATE SET     TABLA.VALOR1          = VISTA.COSTOTOTAL,                                        
                                            TABLA.VALORTOTAL      = VISTA.COSTOTOTAL,
                                            TABLA.DATE_MODIFIED   = SYSDATE,
                                            TABLA.MODIFIED_BY     = '''||UN_USUARIO||'''';


        BEGIN 
            BEGIN   
                 MI_RTA:= PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA, 
                                            UN_ACCION      => 'MM', 
                                            UN_MERGEUSING  => MI_MERGEUSING, 
                                            UN_MERGEENLACE => MI_MERGEENLACE,
                                            UN_MERGEEXISTE => MI_MERGEEXISTE);         

             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                    RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
            END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERR_BANCOP_ACTUAL_PROGRAMACION,
                                     UN_REEMPLAZOS => MI_MSGERROR);
        END;



        MI_MERGEUSING  := 'SELECT  COMPONENTES_ACTIVIDADES.VIGENCIA,
                               COMPONENTES_ACTIVIDADES.COMPANIA,
                               COMPONENTES_ACTIVIDADES.CODIGOPROYECTO,
                               COMPONENTES_ACTIVIDADES.TIPOCOMPONENTE,
                               COMPONENTES_ACTIVIDADES.COMPONENTE,
                               COMPONENTES_ACTIVIDADES.ACTIVIDAD,
                               SUM(NVL(PROG.VALORPROGRAMADOACT,0)) VALORPROGRAMADOACT
                      FROM COMPONENTES_ACTIVIDADES
                        LEFT JOIN (SELECT PROGRAMACION.COMPANIA,
                                  PROGRAMACION.CODIGOPROYECTO,
                                  PROGRAMACION.TIPOCOMPONENTE,
                                  PROGRAMACION.CODIGOCOMPONENTE,
                                  PROGRAMACION.CODIGOACTIVIDAD,
                                  (NVL(VALOR1,0) + NVL(VALOR2,0) + NVL(VALOR3,0) + NVL(VALOR4,0) + NVL(VALOR5,0) + NVL(VALOR6,0) + NVL(VALOR7,0) + NVL(VALOR8,0) + NVL(VALOR9,0) + NVL(VALOR10,0) + NVL(VALOR11,0) + NVL(VALOR12,0)) VALORPROGRAMADOACT
                                FROM PROGRAMACION
                                INNER JOIN PROYECTOS
                                ON PROGRAMACION.COMPANIA                   = PROYECTOS.COMPANIA
                                AND PROGRAMACION.CODIGOPROYECTO            = PROYECTOS.CODIGO
                                INNER JOIN COMPONENTES_ACTIVIDADES
                                ON PROGRAMACION.CODIGOPROYECTO            = COMPONENTES_ACTIVIDADES.CODIGOPROYECTO
                                AND PROGRAMACION.CODIGOCOMPONENTE         = COMPONENTES_ACTIVIDADES.COMPONENTE
                                AND PROGRAMACION.TIPOCOMPONENTE           = COMPONENTES_ACTIVIDADES.TIPOCOMPONENTE
                                AND PROGRAMACION.CODIGOACTIVIDAD          = COMPONENTES_ACTIVIDADES.ACTIVIDAD
                                  WHERE   PROGRAMACION.COMPANIA    = '''||UN_COMPANIA||'''
                                    '||CASE WHEN UN_VIGENCIA  = ('TODAS') THEN '' ELSE 'AND PROYECTOS.VIGENCIAINICIO =''' ||  UN_VIGENCIA ||''''  END||'
                                    AND   PROGRAMACION.TIPOESTADO  IN (''P'', ''RP'')) PROG
                          ON  COMPONENTES_ACTIVIDADES.COMPANIA = PROG.COMPANIA
                          AND COMPONENTES_ACTIVIDADES.CODIGOPROYECTO = PROG.CODIGOPROYECTO
                          AND COMPONENTES_ACTIVIDADES.TIPOCOMPONENTE = PROG.TIPOCOMPONENTE
                          AND COMPONENTES_ACTIVIDADES.COMPONENTE = PROG.CODIGOCOMPONENTE
                          AND COMPONENTES_ACTIVIDADES.ACTIVIDAD = PROG.CODIGOACTIVIDAD
                      WHERE   COMPONENTES_ACTIVIDADES.COMPANIA = '''||UN_COMPANIA||'''
                      '||CASE WHEN UN_CODIGO  = ('TODOS') THEN '' ELSE 'AND   COMPONENTES_ACTIVIDADES.CODIGOPROYECTO =''' ||  UN_CODIGO ||''''  END||'

                      GROUP BY COMPONENTES_ACTIVIDADES.VIGENCIA,
                              COMPONENTES_ACTIVIDADES.COMPANIA,
                              COMPONENTES_ACTIVIDADES.CODIGOPROYECTO,
                              COMPONENTES_ACTIVIDADES.TIPOCOMPONENTE,
                              COMPONENTES_ACTIVIDADES.COMPONENTE,
                              COMPONENTES_ACTIVIDADES.ACTIVIDAD';

    MI_MERGEENLACE := '    TABLA.COMPANIA       = VISTA.COMPANIA
                       AND TABLA.CODIGOPROYECTO = VISTA.CODIGOPROYECTO
                       AND TABLA.TIPOCOMPONENTE = VISTA.TIPOCOMPONENTE
                       AND TABLA.COMPONENTE     = VISTA.COMPONENTE
                       AND TABLA.ACTIVIDAD      = VISTA.ACTIVIDAD';
                       --AND TABLA.COSTOTOTAL     >= VISTA.VALORPROGRAMADOACT';

    MI_MERGEEXISTE := 'UPDATE
                        SET   TABLA.VALORPROGRAMADO = VISTA.VALORPROGRAMADOACT,
                              TABLA.MODIFIED_BY     =''' || UN_USUARIO|| ''',
                              TABLA.DATE_MODIFIED   = SYSDATE 
                        WHERE TABLA.COMPANIA = '''||UN_COMPANIA||'''';

    BEGIN
      BEGIN
        MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => 'COMPONENTES_ACTIVIDADES',
                                         UN_ACCION      => 'MM',
                                         UN_MERGEUSING  => MI_MERGEUSING,
                                         UN_MERGEENLACE => MI_MERGEENLACE,
                                         UN_MERGEEXISTE => MI_MERGEEXISTE);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
          RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD =>SQLCODE,
          UN_ERROR_COD=>PCK_ERRORES.ER_BANCO_VLR_COMP_ACTVD_MERGE
      );
    END;
    /*

        IF UN_VIGENCIA  = 'TODAS'  THEN 

            MI_MERGEUSING  := 'SELECT CA.COMPANIA,
                                  CA.CODIGOPROYECTO,
                                  COMPONENTE,
                                  CA.TIPOCOMPONENTE,
                                  SUM(CA.VALORPROGRAMADO) AS TOTALPROGRAMADO
                             FROM COMPONENTES_ACTIVIDADES  CA
                            WHERE CA.COMPANIA = '''||UN_COMPANIA||'''
                            '||CASE WHEN UN_VIGENCIA  = ('TODAS') THEN '' ELSE 'AND   PROYECTOS.VIGENCIAINICIO =''' ||  UN_VIGENCIA ||''''  END||'
                            GROUP BY CA.COMPANIA,
                                  CA.CODIGOPROYECTO,
                                  COMPONENTE,
                                  CA.TIPOCOMPONENTE
                            ORDER BY COMPONENTE';

         MI_MERGEENLACE :='   TABLA.COMPANIA       = VISTA.COMPANIA 
                          AND TABLA.CODIGO         = VISTA.COMPONENTE
                          AND TABLA.CODIGOPROYECTO = VISTA.CODIGOPROYECTO
                          AND TABLA.TIPOCOMPONENTE = VISTA.TIPOCOMPONENTE';

         MI_MERGEEXISTE := ' UPDATE SET TABLA.VALORPROGRAMADO = VISTA.TOTALPROGRAMADO,
                                        TABLA.DATE_MODIFIED   = SYSDATE,
                                        TABLA.MODIFIED_BY     = '''||UN_USUARIO||'''';


         -- ACTUALIZACION DE VALORPROGRAMADO EN LA TABLA COMPONENTES
    BEGIN
      BEGIN
        MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => 'COMPONENTES',
                                         UN_ACCION      => 'MM',
                                         UN_MERGEUSING  => MI_MERGEUSING,
                                         UN_MERGEENLACE => MI_MERGEENLACE,
                                         UN_MERGEEXISTE => MI_MERGEEXISTE);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
          RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ER_BANCO_VLR_COMPONENTE_MERGE
      );
    END;    


      ELSE
      */

       MI_MERGEUSING  := 'SELECT CA.COMPANIA,
                             CA.CODIGOPROYECTO,
                             COMPONENTE,
                             CA.TIPOCOMPONENTE,
                             Sum(CA.VALORPROGRAMADO) AS TOTALPROGRAMADO           
                            FROM   COMPONENTES_ACTIVIDADES  CA  
                             WHERE COMPANIA = '''||UN_COMPANIA||'''
                              '||CASE WHEN UN_CODIGO = ('TODOS') THEN '' ELSE 'AND CODIGOPROYECTO = '''||UN_CODIGO||'''' END||'
                             GROUP BY CA.COMPANIA,
                            CA.CODIGOPROYECTO,
                            COMPONENTE,
                            CA.TIPOCOMPONENTE  
                            ORDER BY COMPONENTE';

    MI_MERGEENLACE := '    TABLA.COMPANIA       = VISTA.COMPANIA
                       AND TABLA.CODIGOPROYECTO = VISTA.CODIGOPROYECTO
                       AND TABLA.TIPOCOMPONENTE = VISTA.TIPOCOMPONENTE
                       AND TABLA.CODIGO        = VISTA.COMPONENTE';

    MI_MERGEEXISTE := 'UPDATE
                        SET   TABLA.VALORPROGRAMADO = VISTA.TOTALPROGRAMADO,
                              TABLA.MODIFIED_BY     =''' || UN_USUARIO|| ''',
                              TABLA.DATE_MODIFIED   = SYSDATE
                        WHERE TABLA.COMPANIA = '''|| UN_COMPANIA ||'''
                        ';
                       -- AND   TABLA.VIGENCIA = '''||CASE WHEN UN_VIGENCIA  = ('TODAS') THEN '' ELSE   UN_VIGENCIA  END||''' ';

    -- ACTUALIZACION DE VALORPROGRAMADO EN LA TABLA COMPONENTES
    BEGIN
      BEGIN
        MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => 'COMPONENTES',
                                         UN_ACCION      => 'MM',
                                         UN_MERGEUSING  => MI_MERGEUSING,
                                         UN_MERGEENLACE => MI_MERGEENLACE,
                                         UN_MERGEEXISTE => MI_MERGEEXISTE);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
          RAISE PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ER_BANCO_VLR_COMPONENTE_MERGE
      );
    END;



    RETURN MI_RTA_PROG;

  END FC_CREAR_MANTENIMIENTO;

FUNCTION FC_VERIFICAR_SALDO_RUBRO(
/*
    NAME              : PR_VERIFICAR_SALDO_RUBRO
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR CREATION   : JONATHAN MALAVER
    DATE CREATION     : 
    TIME              : 
    SOURCE MODULE     : BANCO DE PROYECTOS
    DESCRIPTION       : -----
    MODIFIED BY       : GUSTAVO ANDRÉS FIGUEREDO AVILA
    DATE			  :16/06/2021
   	DESCRIPCION		  : Se añade filtro por centro de costo, referencia y auxiliar,
   						para consultar el saldo mas especifico.

    @NAME  : verificarSaldoRubro
    @METHOD: GET

    */

 UN_COMPANIA          IN BP_D_NOVEDADPROYECTO.COMPANIA%TYPE,
 UN_CLASET            IN BP_D_NOVEDADPROYECTO.CLASET%TYPE,
 UN_NOVEDAD           IN BP_D_NOVEDADPROYECTO.NOVEDAD%TYPE,
 UN_DEPENDENCIA       IN BP_D_NOVEDADPROYECTO.DEPENDENCIA%TYPE,
 UN_RUBROPRESUPUESTAL IN BP_D_NOVEDADPROYECTO.RUBROPRESUPUESTAL%TYPE,
 UN_PROYECTO          IN BP_PROYECTOSRUBROS.PROYECTO%TYPE,
 UN_FUENTERECURSOS    IN BP_PROYECTOSRUBROS.FUENTERECURSOSRUBRO%TYPE,     
 UN_IDMETAPRODUCTO    IN BP_ARMONIZACIONPD.ID_META%TYPE,
 UN_VALORSOLICITADO   IN NUMBER,
 UN_VALORDISMINUIDO   IN NUMBER,
 UN_CENTRO_COSTO	  IN BP_PROYECTOSRUBROS.CENTRO_COSTO%TYPE,
 UN_REFERENCIA		  IN BP_PROYECTOSRUBROS.REFERENCIA%TYPE,
 UN_AUXILIAR		  IN BP_PROYECTOSRUBROS.AUXILIAR%TYPE
 ) RETURN VARCHAR2

AS
    MI_SALDORUBRO             NUMBER;
    MI_VALORSOLICITADO        NUMBER;
    MI_VALORDISMINUIDO        NUMBER;
    MI_SALDOPROYECTADORUBRO   NUMBER;
    MI_VIGENCIA               NUMBER;
    MI_VALORSOLICITADOTOTAL   NUMBER;
    MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_SALDO_ACTUALRUBRO      NUMBER;
    MI_SALIDA                 VARCHAR2(500);

BEGIN
    --AMONROY (04/01/2019) Se realiza la consulta de la vigencia de forma independiente basandose en la tabla BPNOVEDADPROYECTO
    SELECT VIGENCIA
      INTO MI_VIGENCIA
      FROM BPNOVEDADPROYECTO
    WHERE  COMPANIA    = UN_COMPANIA
      AND  TIPOT       IN ('SCD','MOD')
      AND  CLASET      = UN_CLASET
      AND  CODIGO      = UN_NOVEDAD
      AND  DEPENDENCIA = UN_DEPENDENCIA;

      BEGIN

         SELECT  SUM(BP_D_NOVEDADPROYECTO.VALORSOLICITADO) VALORSOLICITADO,
                 SUM(BP_D_NOVEDADPROYECTO.VALORDISMINUIDO) VALORDISMINUIDO
         INTO MI_VALORSOLICITADO, MI_VALORDISMINUIDO 
         FROM BP_D_NOVEDADPROYECTO
         INNER JOIN BPNOVEDADPROYECTO
           ON BP_D_NOVEDADPROYECTO.COMPANIA           = BPNOVEDADPROYECTO.COMPANIA
           AND BP_D_NOVEDADPROYECTO.TIPOT             = BPNOVEDADPROYECTO.TIPOT
           AND BP_D_NOVEDADPROYECTO.CLASET            = BPNOVEDADPROYECTO.CLASET
           AND BP_D_NOVEDADPROYECTO.NOVEDAD           = BPNOVEDADPROYECTO.CODIGO
           AND BP_D_NOVEDADPROYECTO.DEPENDENCIA       = BPNOVEDADPROYECTO.DEPENDENCIA
         WHERE BP_D_NOVEDADPROYECTO.COMPANIA        = UN_COMPANIA
           AND BP_D_NOVEDADPROYECTO.CLASET            = UN_CLASET
           AND BP_D_NOVEDADPROYECTO.NOVEDAD           = UN_NOVEDAD
           AND BP_D_NOVEDADPROYECTO.DEPENDENCIA       = UN_DEPENDENCIA
           AND BP_D_NOVEDADPROYECTO.RUBROPRESUPUESTAL = UN_RUBROPRESUPUESTAL
           AND BP_D_NOVEDADPROYECTO.FUENTERECURSOS    = UN_FUENTERECURSOS
           AND BP_D_NOVEDADPROYECTO.CENTRO_COSTO 	  = UN_CENTRO_COSTO
		   AND BP_D_NOVEDADPROYECTO.REFERENCIA 	      = UN_REFERENCIA
		   AND BP_D_NOVEDADPROYECTO.AUXILIAR 		  = UN_AUXILIAR
           AND BPNOVEDADPROYECTO.TIPOT               IN ('SCD','MOD')
           AND BPNOVEDADPROYECTO.ESTADO              IN ('V')
           AND BPNOVEDADPROYECTO.AFECTADO            IN (0)
         GROUP BY BP_D_NOVEDADPROYECTO.RUBROPRESUPUESTAL,
                  BP_D_NOVEDADPROYECTO.COMPANIA,
                  BP_D_NOVEDADPROYECTO.TIPOT,
                  BP_D_NOVEDADPROYECTO.CLASET,
                  BP_D_NOVEDADPROYECTO.NOVEDAD,
                  BP_D_NOVEDADPROYECTO.DEPENDENCIA,
                  BPNOVEDADPROYECTO.VIGENCIA;

       EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_VALORSOLICITADO  := 0;
          MI_VALORDISMINUIDO  := 0;
       END;  


     BEGIN
        /*
        SELECT SUM(V_RESUMENPPTO_BASE.APROPIACIONVIGENTE - V_RESUMENPPTO_BASE.DISPONIBILIDAD + V_RESUMENPPTO_BASE.REINTEGRO) SALDORUBRO
        INTO MI_SALDORUBRO
        FROM V_RESUMENPPTO_BASE
        INNER JOIN BP_PROYECTOSRUBROS
          ON V_RESUMENPPTO_BASE.COMPANIA = BP_PROYECTOSRUBROS.COMPANIA
          AND V_RESUMENPPTO_BASE.ANO     = BP_PROYECTOSRUBROS.VIGENCIA
          AND V_RESUMENPPTO_BASE.ID      = BP_PROYECTOSRUBROS.RUBROPPTALES
        INNER JOIN BP_ARMONIZACIONPD
          ON BP_PROYECTOSRUBROS.COMPANIA             = BP_ARMONIZACIONPD.COMPANIA
          AND BP_PROYECTOSRUBROS.RUBROPPTALES        = BP_ARMONIZACIONPD.ID_RUBRO
          AND BP_PROYECTOSRUBROS.FUENTERECURSOSRUBRO = BP_ARMONIZACIONPD.FUENTE
          AND BP_PROYECTOSRUBROS.VIGENCIA            = BP_ARMONIZACIONPD.VIGENCIA_RUBRO
        LEFT JOIN FUENTE_RECURSOS
          ON V_RESUMENPPTO_BASE.COMPANIA             = FUENTE_RECURSOS.COMPANIA
          AND V_RESUMENPPTO_BASE.ANO                 = FUENTE_RECURSOS.ANO
        WHERE V_RESUMENPPTO_BASE.COMPANIA            = UN_COMPANIA
          AND V_RESUMENPPTO_BASE.ANO                 = MI_VIGENCIA
          AND V_RESUMENPPTO_BASE.DESTINO            IN ('I','L')
          AND V_RESUMENPPTO_BASE.NATURALEZA         IN('D')
          AND V_RESUMENPPTO_BASE.MES                <= TO_CHAR(SYSDATE, 'MM')
          AND BP_PROYECTOSRUBROS.PROYECTO            = UN_PROYECTO
          AND BP_PROYECTOSRUBROS.FUENTERECURSOSRUBRO = UN_FUENTERECURSOS
          AND BP_ARMONIZACIONPD.ID_META              = UN_IDMETAPRODUCTO
        GROUP BY  V_RESUMENPPTO_BASE.ID,
                  V_RESUMENPPTO_BASE.NOMBRE,
                  BP_PROYECTOSRUBROS.FUENTERECURSOSRUBRO; */
          --AMONROY (08/01/2019) Se adiciona el filtro por rubro presupuestal en las dos consultas
          IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'CONTROLA FUENTE SEGUN META PRODUCTO EN SOLICITUD DE CDP',PCK_DATOS.FC_MODULOBANCOPROY(),SYSDATE,-1) = 'NO' 
            THEN
              SELECT    SUM(V_RESUMENPPTO_BASE.APROPIACIONVIGENTE - V_RESUMENPPTO_BASE.DISPONIBILIDAD + V_RESUMENPPTO_BASE.REINTEGRO) SALDO
              INTO      MI_SALDORUBRO
              FROM      V_RESUMENPPTO_BASE 
                INNER JOIN PLAN_PRESUPUESTAL
                    ON V_RESUMENPPTO_BASE.COMPANIA             = PLAN_PRESUPUESTAL.COMPANIA
                    AND V_RESUMENPPTO_BASE.ANO                 = PLAN_PRESUPUESTAL.ANO
                    AND V_RESUMENPPTO_BASE.CODIGO              = PLAN_PRESUPUESTAL.CODIGO
                INNER JOIN BP_PROYECTOSRUBROS 
                    ON V_RESUMENPPTO_BASE.COMPANIA        = BP_PROYECTOSRUBROS.COMPANIA 
                    AND V_RESUMENPPTO_BASE.ANO            = BP_PROYECTOSRUBROS.VIGENCIA 
                    AND V_RESUMENPPTO_BASE.CODIGO         = BP_PROYECTOSRUBROS.RUBROPPTALES 
                    AND V_RESUMENPPTO_BASE.FUENTE_RECURSO = CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_FUE NOT IN(0) THEN BP_PROYECTOSRUBROS.FUENTERECURSOSRUBRO ELSE PCK_DATOS.FC_CONS_FUENTE END
                    AND V_RESUMENPPTO_BASE.CENTRO_COSTO = CASE WHEN PLAN_PRESUPUESTAL.MAN_CEN_CTO NOT IN(0) THEN BP_PROYECTOSRUBROS.CENTRO_COSTO ELSE PCK_DATOS.CONS_CENTRO END
                    AND V_RESUMENPPTO_BASE.REFERENCIA = CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_REF NOT IN(0) THEN BP_PROYECTOSRUBROS.REFERENCIA ELSE PCK_DATOS.CONS_REFERENCIA END
                    AND V_RESUMENPPTO_BASE.AUXILIAR = CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_GEN NOT IN(0) THEN BP_PROYECTOSRUBROS.AUXILIAR ELSE PCK_DATOS.CONS_AUXILIAR END
                INNER JOIN FUENTE_RECURSOS 
                    ON V_RESUMENPPTO_BASE.COMPANIA        = FUENTE_RECURSOS.COMPANIA 
                    AND V_RESUMENPPTO_BASE.ANO            = FUENTE_RECURSOS.ANO 
                    AND V_RESUMENPPTO_BASE.FUENTE_RECURSO= CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_FUE NOT IN(0) THEN FUENTE_RECURSOS.CODIGO ELSE PCK_DATOS.FC_CONS_FUENTE END
              WHERE V_RESUMENPPTO_BASE.COMPANIA         = UN_COMPANIA
                AND V_RESUMENPPTO_BASE.ANO              = MI_VIGENCIA 
                AND V_RESUMENPPTO_BASE.DESTINO          IN ('I','L') 
                AND V_RESUMENPPTO_BASE.NATURALEZA       IN('D') 
                AND V_RESUMENPPTO_BASE.MES              <= TO_CHAR(SYSDATE, 'MM') 
                AND BP_PROYECTOSRUBROS.PROYECTO         = UN_PROYECTO 
                AND BP_PROYECTOSRUBROS.FUENTERECURSOSRUBRO = UN_FUENTERECURSOS
                AND BP_PROYECTOSRUBROS.RUBROPPTALES        = UN_RUBROPRESUPUESTAL
                AND BP_PROYECTOSRUBROS.CENTRO_COSTO 	   = UN_CENTRO_COSTO
				AND BP_PROYECTOSRUBROS.REFERENCIA 	       = UN_REFERENCIA
				AND BP_PROYECTOSRUBROS.AUXILIAR 		   = UN_AUXILIAR
                GROUP BY V_RESUMENPPTO_BASE.CODIGO,   
                  V_RESUMENPPTO_BASE.NOMBRE,   
                  BP_PROYECTOSRUBROS.FUENTERECURSOSRUBRO;
            ELSE      
              SELECT    SUM(V_RESUMENPPTO_BASE.APROPIACIONVIGENTE - V_RESUMENPPTO_BASE.DISPONIBILIDAD + V_RESUMENPPTO_BASE.REINTEGRO) SALDO
              INTO      MI_SALDORUBRO
              FROM      V_RESUMENPPTO_BASE
                INNER JOIN PLAN_PRESUPUESTAL
                    ON V_RESUMENPPTO_BASE.COMPANIA             = PLAN_PRESUPUESTAL.COMPANIA
                    AND V_RESUMENPPTO_BASE.ANO                 = PLAN_PRESUPUESTAL.ANO
                    AND V_RESUMENPPTO_BASE.CODIGO              = PLAN_PRESUPUESTAL.CODIGO
                INNER JOIN BP_PROYECTOSRUBROS
                    ON V_RESUMENPPTO_BASE.COMPANIA        = BP_PROYECTOSRUBROS.COMPANIA
                    AND V_RESUMENPPTO_BASE.ANO            = BP_PROYECTOSRUBROS.VIGENCIA
                    AND V_RESUMENPPTO_BASE.CODIGO         = BP_PROYECTOSRUBROS.RUBROPPTALES
                    AND V_RESUMENPPTO_BASE.FUENTE_RECURSO = CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_FUE NOT IN(0) THEN BP_PROYECTOSRUBROS.FUENTERECURSOSRUBRO ELSE PCK_DATOS.FC_CONS_FUENTE END
                    AND V_RESUMENPPTO_BASE.CENTRO_COSTO = CASE WHEN PLAN_PRESUPUESTAL.MAN_CEN_CTO NOT IN(0) THEN BP_PROYECTOSRUBROS.CENTRO_COSTO ELSE PCK_DATOS.FC_CONS_CENTRO END
                    AND V_RESUMENPPTO_BASE.REFERENCIA = CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_REF NOT IN(0) THEN BP_PROYECTOSRUBROS.REFERENCIA ELSE PCK_DATOS.FC_CONS_REFERENCIA END
                    AND V_RESUMENPPTO_BASE.AUXILIAR = CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_GEN NOT IN(0) THEN BP_PROYECTOSRUBROS.AUXILIAR ELSE PCK_DATOS.FC_CONS_AUXILIAR END
                INNER  JOIN FUENTE_RECURSOS
                    ON V_RESUMENPPTO_BASE.COMPANIA       = FUENTE_RECURSOS.COMPANIA
                    AND V_RESUMENPPTO_BASE.ANO           = FUENTE_RECURSOS.ANO
                    AND V_RESUMENPPTO_BASE.FUENTE_RECURSO= CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_FUE NOT IN(0) THEN FUENTE_RECURSOS.CODIGO ELSE PCK_DATOS.FC_CONS_FUENTE END
                INNER JOIN BP_ARMONIZACIONPD
                    ON BP_PROYECTOSRUBROS.COMPANIA             =BP_ARMONIZACIONPD.COMPANIA
                    AND BP_PROYECTOSRUBROS.VIGENCIA            =BP_ARMONIZACIONPD.VIGENCIA_RUBRO
                    AND BP_PROYECTOSRUBROS.RUBROPPTALES        =BP_ARMONIZACIONPD.ID_RUBRO
                    AND BP_PROYECTOSRUBROS.FUENTERECURSOSRUBRO =BP_ARMONIZACIONPD.FUENTE
                WHERE V_RESUMENPPTO_BASE.COMPANIA            = UN_COMPANIA
                AND V_RESUMENPPTO_BASE.ANO                 = MI_VIGENCIA
                AND V_RESUMENPPTO_BASE.DESTINO            IN ('I','L')
                AND V_RESUMENPPTO_BASE.NATURALEZA         IN('D')
                AND V_RESUMENPPTO_BASE.MES                <= TO_CHAR(SYSDATE, 'MM')
                AND BP_PROYECTOSRUBROS.PROYECTO            = UN_PROYECTO
                AND BP_PROYECTOSRUBROS.FUENTERECURSOSRUBRO = UN_FUENTERECURSOS
                AND BP_ARMONIZACIONPD.ID_META              = UN_IDMETAPRODUCTO
                AND BP_PROYECTOSRUBROS.RUBROPPTALES        = UN_RUBROPRESUPUESTAL
                AND BP_PROYECTOSRUBROS.CENTRO_COSTO 	   = UN_CENTRO_COSTO
				AND BP_PROYECTOSRUBROS.REFERENCIA 		   = UN_REFERENCIA
				AND BP_PROYECTOSRUBROS.AUXILIAR 		   = UN_AUXILIAR
                GROUP BY V_RESUMENPPTO_BASE.CODIGO,
                V_RESUMENPPTO_BASE.NOMBRE,
                BP_PROYECTOSRUBROS.FUENTERECURSOSRUBRO; 
            END IF;    

     EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_SALDORUBRO := 0;
     END;  

     MI_SALDO_ACTUALRUBRO     := MI_SALDORUBRO - MI_VALORSOLICITADO;
     MI_VALORSOLICITADOTOTAL    := UN_VALORSOLICITADO + MI_VALORSOLICITADO;
     MI_VALORDISMINUIDO       := UN_VALORDISMINUIDO + MI_VALORDISMINUIDO;          
     MI_SALDOPROYECTADORUBRO  := MI_SALDORUBRO - (MI_VALORSOLICITADOTOTAL + MI_VALORDISMINUIDO);

     IF MI_SALDOPROYECTADORUBRO < 0 THEN 
          MI_SALIDA := 'No se puede realizar la Solicitud. El rubro ' ||UN_RUBROPRESUPUESTAL || ' tiene un saldo actual de ' ||MI_SALDO_ACTUALRUBRO || ' y el valor solicitado es  ' ||UN_VALORSOLICITADO;
      ELSE
          MI_SALIDA := '-1';          
     END IF;     

     RETURN MI_SALIDA;

END FC_VERIFICAR_SALDO_RUBRO;

FUNCTION FC_EXPORTAR_NUEVOSRUBROS
  /*
    NAME              : FC_EXPORTAR_NUEVOSRUBROS 
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRATION  : BRAYAN SEBASTIAN CARDENAS AGUILAR
    DATE MIGRATION    : 08/05/2019
    TIME              : 12:16 PM
    SOURCE MODULE     : 
    DESCRIPTION       : Busca los rubros nuevos y los adiciona en la tabla BP_RUBRO_INVERSION_DET y los genra en un EXCEL.
    MODIFIED BY       : 
    MODIFICATIONS     : 
    PARAMETERS        :                         
    RETURN            : Detalles del rubro de inversion adicionados en la tabla BP_RUBRO_INVERSION_DET.

    @NAME  :  exportarNuevosRubros
    @METHOD:  GET
  */
(
    UN_COMPANIA  IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_RUBRO     IN BP_RUBRO_INVERSION_DET.RUBRO_INVER%TYPE,      
    UN_VIGENCIA	 IN PCK_SUBTIPOS.TI_ANIO
)
RETURN CLOB
AS 
 MI_RTA    CLOB;
 MI_RS     SYS_REFCURSOR;
 MI_STRSQL PCK_SUBTIPOS.TI_CONSULTA;
 MI_ANIO   PCK_SUBTIPOS.TI_ANIO;
 MI_CODIGO BP_RUBRO_INVERSION_DET.RUBRO_INVER%TYPE;
 MI_NOMBRE BP_RUBRO_INVERSION_DET.DESCRIPCION%TYPE;
BEGIN

 MI_STRSQL :=  'SELECT 
                VIGENCIA,
                RUBRO,
                DESCRIPCION
                FROM BP_RUBRO_INVERSION_DET
                WHERE COMPANIA  = '''|| UN_COMPANIA ||'''
                AND VIGENCIA    =   '|| UN_VIGENCIA ||'
                AND RUBRO_INVER = '''|| UN_RUBRO    ||'''';

 OPEN MI_RS FOR MI_STRSQL;
    LOOP
    FETCH MI_RS INTO MI_ANIO,
                     MI_CODIGO, 
                     MI_NOMBRE;
    EXIT WHEN MI_RS%NOTFOUND;

      MI_RTA     := MI_RTA      || TO_CLOB(   
                    MI_ANIO     || PCK_DATOS.GL_SEPARADOR_COL ||
                    MI_CODIGO   || PCK_DATOS.GL_SEPARADOR_COL || 
                    MI_NOMBRE   ||PCK_DATOS.GL_SEPARADOR_REG);

    END LOOP;
    CLOSE MI_RS;

  RETURN MI_RTA;
END FC_EXPORTAR_NUEVOSRUBROS;

FUNCTION FC_F20_2PROYECTOSDESTINADOS
/*
    NAME              : Exportar_Click() Formulario FRMINF_F20_2
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
    DATE MIGRADOR     : 13/03/2018
    TIME              : 16:30 PM
    DESCRIPTION       : PERMITE PREPARAR LOS DATOS PARA LA GENERACIÓN DEL INFORME F20.2 PROYECTOS DESTINADOS
    PARAMETROS DE ENTRADA: 
      UN_COMPANIA        : Código de la compañía
      UN_VIGENCIAINICIAL : Fecha Inicial por la cual se quiere generar el informe.
      UN_VIGENCIAFINAL   : Fecha Final por la cual se quiere generar el informe.
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 


    @NAME:   prepararDatosF202ProyectosDestinados
    @METHOD: GET
*/
(
  UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_VIGENCIAINICIAL  IN DATE,
  UN_VIGENCIAFINAL    IN DATE
)RETURN CLOB 
AS
 QRYINFVANT20_2        PCK_SUBTIPOS.TI_STRSQL;
 QRYINF20_2            PCK_SUBTIPOS.TI_STRSQL; 
 MI_PARAMETROALIAS     VARCHAR(500 CHAR);
 MI_PARAMETROSINALIAS  VARCHAR(500 CHAR);
 MI_DATOSINVA          VARCHAR(500 CHAR); 
 MI_DATOSINVANT        VARCHAR(500 CHAR);
 MI_SALIDA             CLOB := '';

BEGIN

 MI_PARAMETROALIAS := PCK_BANCOS_PROY5.FC_DEFINIRORDEN(UN_NOMBRETABLA => 'BP_TIPOSIRECI_APSB', 
                                                  UN_NOMBRECAMPO => 'NOMBRE', 
                                                  UN_CAMPOORDEN  => 'ORDEN', 
                                                  UN_CONDICION   => 'COMPANIA = '''||UN_COMPANIA||''' AND ORDEN > 0',
                                                  UN_CON_ALIAS   => 1); 

 MI_PARAMETROSINALIAS := PCK_BANCOS_PROY5.FC_DEFINIRORDEN(UN_NOMBRETABLA => 'BP_TIPOSIRECI_APSB', 
                                                  UN_NOMBRECAMPO => 'NOMBRE', 
                                                  UN_CAMPOORDEN  => 'ORDEN', 
                                                  UN_CONDICION   => 'COMPANIA = '''||UN_COMPANIA||''' AND ORDEN > 0',
                                                  UN_CON_ALIAS   => 0); 

 MI_DATOSINVA  :=  ','||MI_PARAMETROSINALIAS;
 MI_DATOSINVA  :=  REPLACE(MI_DATOSINVA,',"',',"VA.');                                                
 MI_DATOSINVA  := LTRIM(MI_DATOSINVA,',');

 MI_DATOSINVANT :=  ','||MI_PARAMETROSINALIAS;
 MI_DATOSINVANT :=  REPLACE(MI_DATOSINVANT,',"',',"VANT.'); 
 MI_DATOSINVANT := LTRIM(MI_DATOSINVANT,',');


 FOR MI_RS IN (SELECT ROWNUM CONSECUTIVO,
                    'FILA_'||ROWNUM CONSECUTIVOFILA,
                    VA.INFORMACION,
                    VA.JUSTIFICACION,
                    VA.NOMBREPROYECTO,
                    VA.OBJETO,
                    VA.PDA,
                    VA.TERRITORIAL,
                    VA.POBLACIONBENEFICIADA,
                    VA.MUNIBENEFICIADOS,
                    VA.EJECUCION,
                    VA.FREGISTRO,
                    VA.FINICIO,
                    VA.FECHAFINPREVISTA,
                    VA.FECHAFIN,
                    VA.PAGOVIGANT,
                    NVL(VANT.RECURSOSDELSGP,0)VANTRECURSOSDELSGP ,
                    NVL(VANT.REGALIAS,0)VANTREGALIAS,
                    NVL(VANT.OTRASFUENTESDEORIGENNAL,0)VANTOTRASFUENTESDEORIGENNAL,
                    NVL(VANT.OTROSESQUEMASFIDUCIARIOS,0)VANTOTROSESQUEMASFIDUCIARIOS,
                    NVL(VANT.CREDITOS,0)VANTCREDITOS,
                    NVL(VANT.OTRASFUENTESANTERIORES,0)VANTOTRASFUENTESANTERIORES,
                    '0' PAGOVIG,
                    NVL(VA.RECURSOSDELSGP,0)VARECURSOSDELSGP ,
                    NVL(VA.REGALIAS,0)VAREGALIAS,
                    NVL(VA.OTRASFUENTESDEORIGENNAL,0)VAOTRASFUENTESDEORIGENNAL,
                    NVL(VA.OTROSESQUEMASFIDUCIARIOS,0)VAOTROSESQUEMASFIDUCIARIOS,
                    NVL(VA.CREDITOS,0)VACREDITOS,
                    NVL(VA.OTRASFUENTESANTERIORES,0)VAOTRASFUENTESANTERIORES,
                    '' OBSERVACIONES
                  FROM
                    (SELECT *
                    FROM
                      (SELECT 'SI' INFORMACION,
                        '' JUSTIFICACION,
                        P.NOMBREPROYECTO,
                        P.OBJETO,
                        CASE
                          WHEN IND_PDA IN (0)
                          OR IND_PDA   IS NULL
                          THEN 'NO'
                          ELSE 'SI'
                        END PDA,
                        CASE
                          WHEN IND_PDA IN (0)
                          OR IND_PDA   IS NULL
                          THEN 'NO'
                          ELSE 'SI'
                        END TERRITORIAL,
                        P.N_POBLACION POBLACIONBENEFICIADA,
                        '1' MUNIBENEFICIADOS,
                        P.PORCEJECUCION AS EJECUCION,
                        TO_CHAR(FECHAREGISTRO,'YYYY/MM/DD') FREGISTRO,
                        TO_CHAR(FECHAREGISTRO,'YYYY/MM/DD') FINICIO,
                        TO_CHAR(UN_VIGENCIAFINAL,'YYYY/MM/DD') FECHAFINPREVISTA,
                        TO_CHAR(UN_VIGENCIAFINAL,'YYYY/MM/DD') FECHAFIN,
                        0 PAGOVIGANT,
                        SUM(NVL(DCP.VALOR_DEBITO,0)) TOTAL_VLRDEBITO,
                        TSA.NOMBRE NOMBRES
                      FROM BP_D_NOVEDADPROYECTO DN
                      INNER JOIN BPNOVEDADPROYECTO N
                      ON DN.COMPANIA     = N.COMPANIA
                      AND DN.TIPOT       = N.TIPOT
                      AND DN.CLASET      = N.CLASET
                      AND DN.NOVEDAD     = N.CODIGO
                      AND DN.DEPENDENCIA = N.DEPENDENCIA
                      INNER JOIN BPTIPONOVEDAD TN
                      ON N.TIPOT     = TN.TIPOT
                      AND N.COMPANIA = TN.COMPANIA
                      AND N.CLASET   = TN.CLASET
                      INNER JOIN COMPROBANTE_PPTALAFECTADOS CP
                      ON N.COMPANIA  = CP.COMPANIA
                      AND N.VIGENCIA = CP.ANO_AFECT
                      AND N.TIPOT    = CP.TIPO_CPTE_AFECT
                      AND N.CODIGO   = CP.COMPROBANTE_AFECT
                      INNER JOIN COMPROBANTE_PPTAL CPT
                      ON CP.COMPANIA     = CPT.COMPANIA
                      AND CP.ANO         = CPT.ANO
                      AND CP.TIPO_CPTE   = CPT.TIPO
                      AND CP.COMPROBANTE = CPT.NUMERO
                      INNER JOIN DETALLE_COMPROBANTE_PPTAL DCP
                      ON CP.COMPANIA     = DCP.COMPANIA
                      AND CP.ANO         = DCP.ANO
                      AND CP.TIPO_CPTE   = DCP.TIPO_CPTE
                      AND CP.COMPROBANTE = DCP.COMPROBANTE
                      INNER JOIN FUENTE_RECURSOS F
                      ON DN.ANORUBRO        = F.ANO
                      AND DN.FUENTERECURSOS = F.CODIGO
                      AND DN.COMPANIA       = F.COMPANIA
                      LEFT JOIN BP_TIPORECURSOS TF
                      ON F.COMPANIA = TF.COMPANIA
                      LEFT JOIN BP_TIPOSIRECI_APSB TSA
                      ON TF.COMPANIA          = TSA.COMPANIA
                      AND TF.TIPO_SIRECI_APSB = TSA.CODIGO
                      INNER JOIN PROYECTOS P
                      ON DN.PROYECTO      = P.CODIGO
                      AND DN.COMPANIA     = P.COMPANIA
                      WHERE DN.COMPANIA   = UN_COMPANIA
                      AND TN.CLASET       = 'B'
                      AND TN.ClaseNovedad = 'S'
                      AND CP.TIPO_CPTE    = 'RES'
                      AND CPT.FECHA BETWEEN UN_VIGENCIAINICIAL AND UN_VIGENCIAFINAL
                      AND N.ESTADO              = 'V'
                      AND P.DESTINO_SIRECI NOT IN (0)
                      GROUP BY 'SI' ,
                        P.NOMBREPROYECTO,
                        P.OBJETO,
                        CASE
                          WHEN IND_PDA IN (0)
                          OR IND_PDA   IS NULL
                          THEN 'NO'
                          ELSE 'SI'
                        END ,
                        CASE
                          WHEN IND_PDA IN (0)
                          OR IND_PDA   IS NULL
                          THEN 'NO'
                          ELSE 'SI'
                        END ,
                        P.N_POBLACION ,
                        '1' ,
                        P.PORCEJECUCION ,
                        FECHAREGISTRO ,
                        FECHAREGISTRO ,
                        TO_CHAR(UN_VIGENCIAFINAL,'DD/MM/YYYY') ,
                        TO_CHAR(UN_VIGENCIAFINAL,'DD/MM/YYYY') ,
                        0 ,
                        TSA.NOMBRE
                      ) PIVOT ( MIN (TOTAL_VLRDEBITO) FOR NOMBRES IN ('RECURSOS DEL SGP' "RECURSOSDELSGP" ,
                                                                      'REGALÍAS' "REGALIAS" , 
                                                                      'OTRAS FUENTES DE ORIGEN NAL' "OTRASFUENTESDEORIGENNAL" , 
                                                                      'OTROS ESQUEMAS FIDUCIARIOS' "OTROSESQUEMASFIDUCIARIOS" , 
                                                                      'CRÉDITOS' "CREDITOS" , 
                                                                      'OTRAS FUENTES DIFER DE LAS ANTERIORES' "OTRASFUENTESANTERIORES") )
                    ) VA
                  LEFT JOIN
                    (SELECT *
                    FROM
                      (SELECT P.NOMBREPROYECTO,
                        P.OBJETO,
                        0 AS PAGOVIGANT,
                        TSA.NOMBRE,
                        NVL(DCP.VALOR_DEBITO,0) VALOR_DEBITO
                      FROM BP_D_NOVEDADPROYECTO DN
                      INNER JOIN BPNOVEDADPROYECTO N
                      ON DN.COMPANIA     = N.COMPANIA
                      AND DN.TIPOT       = N.TIPOT
                      AND DN.CLASET      = N.CLASET
                      AND DN.NOVEDAD     = N.CODIGO
                      AND DN.DEPENDENCIA = N.DEPENDENCIA
                      INNER JOIN BPTIPONOVEDAD TN
                      ON N.TIPOT     = TN.TIPOT
                      AND N.COMPANIA = TN.COMPANIA
                      AND N.CLASET   = TN.CLASET
                      INNER JOIN COMPROBANTE_PPTALAFECTADOS CP
                      ON N.COMPANIA  = CP.COMPANIA
                      AND N.VIGENCIA = CP.ANO_AFECT
                      AND N.TIPOT    = CP.TIPO_CPTE_AFECT
                      AND N.CODIGO   = CP.COMPROBANTE_AFECT
                      INNER JOIN DETALLE_COMPROBANTE_PPTAL DCP
                      ON CP.COMPANIA     = DCP.COMPANIA
                      AND CP.ANO         = DCP.ANO
                      AND CP.TIPO_CPTE   = DCP.TIPO_CPTE
                      AND CP.COMPROBANTE = DCP.COMPROBANTE
                      INNER JOIN FUENTE_RECURSOS F
                      ON DN.ANORUBRO        = F.ANO
                      AND DN.FUENTERECURSOS = F.CODIGO
                      AND DN.COMPANIA       = F.COMPANIA
                      LEFT JOIN BP_TIPORECURSOS TF
                      ON F.COMPANIA = TF.COMPANIA
                      LEFT JOIN BP_TIPOSIRECI_APSB TSA
                      ON TF.COMPANIA          = TSA.COMPANIA
                      AND TF.TIPO_SIRECI_APSB = TSA.CODIGO
                      INNER JOIN PROYECTOS P
                      ON DN.PROYECTO            = P.CODIGO
                      AND DN.COMPANIA           = P.COMPANIA
                      WHERE DN.COMPANIA         = UN_COMPANIA
                      AND TN.CLASET             = 'B'
                      AND TN.ClaseNovedad       = 'S'
                      AND CP.TIPO_CPTE          = 'RES'
                      AND DCP.FECHA             < UN_VIGENCIAINICIAL
                      AND N.Estado              = 'V'
                      AND P.DESTINO_SIRECI NOT IN (0)
                      GROUP BY P.NOMBREPROYECTO,
                        P.OBJETO,
                        0 ,
                        TSA.NOMBRE,
                        NVL(DCP.VALOR_DEBITO,0)
                      ) PIVOT (SUM(VALOR_DEBITO) FOR NOMBRE IN ('RECURSOS DEL SGP' "RECURSOSDELSGP" , 
                                                                'REGALÍAS' "REGALIAS" , 
                                                                'OTRAS FUENTES DE ORIGEN NAL' "OTRASFUENTESDEORIGENNAL" , 
                                                                'OTROS ESQUEMAS FIDUCIARIOS' "OTROSESQUEMASFIDUCIARIOS" , 
                                                                'CRÉDITOS' "CREDITOS" , 
                                                                'OTRAS FUENTES DIFER DE LAS ANTERIORES' "OTRASFUENTESANTERIORES") )
                    )VANT ON VA.OBJETO                       = VANT.OBJETO
                  AND VA.NOMBREPROYECTO                      = VANT.NOMBREPROYECTO)
        LOOP

        MI_SALIDA  :=  MI_SALIDA                    || TO_CLOB(MI_RS.CONSECUTIVO 
							        || PCK_DATOS.GL_SEPARADOR_COL || MI_RS.CONSECUTIVOFILA
											|| PCK_DATOS.GL_SEPARADOR_COL || MI_RS.INFORMACION 
											|| PCK_DATOS.GL_SEPARADOR_COL || MI_RS.JUSTIFICACION
											|| PCK_DATOS.GL_SEPARADOR_COL || MI_RS.NOMBREPROYECTO
											|| PCK_DATOS.GL_SEPARADOR_COL || MI_RS.OBJETO
											|| PCK_DATOS.GL_SEPARADOR_COL || MI_RS.PDA
											|| PCK_DATOS.GL_SEPARADOR_COL || MI_RS.TERRITORIAL
											|| PCK_DATOS.GL_SEPARADOR_COL || MI_RS.POBLACIONBENEFICIADA
											|| PCK_DATOS.GL_SEPARADOR_COL || MI_RS.MUNIBENEFICIADOS
											|| PCK_DATOS.GL_SEPARADOR_COL || MI_RS.EJECUCION
											|| PCK_DATOS.GL_SEPARADOR_COL || MI_RS.FREGISTRO
											|| PCK_DATOS.GL_SEPARADOR_COL || MI_RS.FINICIO
											|| PCK_DATOS.GL_SEPARADOR_COL || MI_RS.FECHAFINPREVISTA
                      || PCK_DATOS.GL_SEPARADOR_COL || MI_RS.FECHAFIN
                      || PCK_DATOS.GL_SEPARADOR_COL || MI_RS.PAGOVIGANT
                      || PCK_DATOS.GL_SEPARADOR_COL || MI_RS.VANTRECURSOSDELSGP
                      || PCK_DATOS.GL_SEPARADOR_COL || MI_RS.VANTREGALIAS
                      || PCK_DATOS.GL_SEPARADOR_COL || MI_RS.VANTOTRASFUENTESDEORIGENNAL
                      || PCK_DATOS.GL_SEPARADOR_COL || MI_RS.VANTOTROSESQUEMASFIDUCIARIOS
                      || PCK_DATOS.GL_SEPARADOR_COL || MI_RS.PAGOVIG
                      || PCK_DATOS.GL_SEPARADOR_COL || MI_RS.VARECURSOSDELSGP
                      || PCK_DATOS.GL_SEPARADOR_COL || MI_RS.VAREGALIAS
                      || PCK_DATOS.GL_SEPARADOR_COL || MI_RS.VAOTRASFUENTESDEORIGENNAL
                      || PCK_DATOS.GL_SEPARADOR_COL || MI_RS.VAOTROSESQUEMASFIDUCIARIOS
                      || PCK_DATOS.GL_SEPARADOR_COL || MI_RS.VACREDITOS
                      || PCK_DATOS.GL_SEPARADOR_COL || MI_RS.VAOTRASFUENTESANTERIORES
                      || PCK_DATOS.GL_SEPARADOR_COL || MI_RS.OBSERVACIONES
                      || PCK_DATOS.GL_SEPARADOR_REG 
											);

        END LOOP;

 RETURN MI_SALIDA;


END FC_F20_2PROYECTOSDESTINADOS;

FUNCTION FC_VALIDAR_VOBO 
/*
      NAME              : FC_VALIDAR_VOBO
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : BRAYAN SEBASTIAN CARDENAS AGUILAR
      DATE MIGRADOR     : 07/02/2020
      TIME              : 05:00 p.m.
      SOURCE MODULE     : 
      DESCRIPTION       : VALIDA LOS SALDOS SOLICITADOS DE LA ACTIVIDAD
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 

    @NAME:   validarVoBo
    @METHOD: GET
*/(
    UN_COMPANIA   IN   PCK_SUBTIPOS.TI_COMPANIA,
    UN_NOVEDAD    IN   BP_D_NOVEDADPROYECTO.NOVEDAD%TYPE,
    UN_TIPO       IN   BP_D_NOVEDADPROYECTO.TIPOT%TYPE,
    UN_CLASE      IN   BP_D_NOVEDADPROYECTO.CLASET%TYPE,
    UN_VOBO       IN   BPNOVEDADPROYECTO.VOBOBP%TYPE
)RETURN CLOB AS
    MI_RTA CLOB := 'false';
BEGIN
    IF UN_TIPO <> 'MOD' THEN
        IF UN_VOBO <> 0 THEN
            FOR MI_RS IN(
                SELECT
                    BP_D_NOVEDADPROYECTO.PROYECTO,
                    BP_D_NOVEDADPROYECTO.COMPONENTE,
                    BP_D_NOVEDADPROYECTO.ACTIVIDAD,
                    BP_D_NOVEDADPROYECTO.VALORSOLICITADO,
                    (COMPONENTES_ACTIVIDADES.COSTOTOTAL - COMPONENTES_ACTIVIDADES.VALOR_SOLICITADO_ACTIVIDAD) + COMPONENTES_ACTIVIDADES.VALOR_DISMINUIDO AS SALDO,
                    CASE
                        WHEN VALORSOLICITADO > (COMPONENTES_ACTIVIDADES.COSTOTOTAL - COMPONENTES_ACTIVIDADES.VALOR_SOLICITADO_ACTIVIDAD) + COMPONENTES_ACTIVIDADES.VALOR_DISMINUIDO THEN
                            'SI'
                        ELSE
                            'NO'
                    END AS INCONSISTENCIA
                FROM BP_D_NOVEDADPROYECTO
                    INNER JOIN COMPONENTES_ACTIVIDADES 
                     ON BP_D_NOVEDADPROYECTO.COMPANIA = COMPONENTES_ACTIVIDADES.COMPANIA
                    AND BP_D_NOVEDADPROYECTO.COMPONENTE = COMPONENTES_ACTIVIDADES.COMPONENTE
                    AND BP_D_NOVEDADPROYECTO.ACTIVIDAD = COMPONENTES_ACTIVIDADES.ACTIVIDAD
                    AND BP_D_NOVEDADPROYECTO.PROYECTO = COMPONENTES_ACTIVIDADES.CODIGOPROYECTO
                WHERE
                    BP_D_NOVEDADPROYECTO.COMPANIA = UN_COMPANIA
                    AND BP_D_NOVEDADPROYECTO.TIPOT = UN_TIPO
                    AND BP_D_NOVEDADPROYECTO.CLASET = UN_CLASE
                    AND BP_D_NOVEDADPROYECTO.NOVEDAD = UN_NOVEDAD
                GROUP BY
                    BP_D_NOVEDADPROYECTO.PROYECTO,
                    BP_D_NOVEDADPROYECTO.COMPONENTE,
                    BP_D_NOVEDADPROYECTO.ACTIVIDAD,
                    BP_D_NOVEDADPROYECTO.VALORSOLICITADO,
                    (COMPONENTES_ACTIVIDADES.COSTOTOTAL - COMPONENTES_ACTIVIDADES.VALOR_SOLICITADO_ACTIVIDAD) + COMPONENTES_ACTIVIDADES.VALOR_DISMINUIDO
                HAVING
                        CASE
                            WHEN VALORSOLICITADO > (COMPONENTES_ACTIVIDADES.COSTOTOTAL - COMPONENTES_ACTIVIDADES.VALOR_SOLICITADO_ACTIVIDAD) + COMPONENTES_ACTIVIDADES.VALOR_DISMINUIDO THEN
                                'SI'
                            ELSE
                                'NO'
                        END
                    = 'SI'
            )LOOP
                IF MI_RS.VALORSOLICITADO > MI_RS.SALDO THEN
                    MI_RTA := 'No se puede dar visto bueno a la solicitud ya que el valor solicitado sobrepasa el saldo de la actividad. 
                       Está intentando hacer una solicitud por ' || MI_RS.VALORSOLICITADO || ' y el saldo de la actividad es: ' || MI_RS.SALDO || '
                       Para el proyecto: ' || MI_RS.PROYECTO || ' el componente: ' || MI_RS.COMPONENTE || ' y la actividad ' || MI_RS.ACTIVIDAD || '.';

                    RETURN MI_RTA;
                END IF;
                
            END LOOP;

        END IF;
    END IF;

RETURN MI_RTA;
END FC_VALIDAR_VOBO;

PROCEDURE PR_ACTUALIZARMETAPRODUCTO 
    /*
      NAME              : PR_ACTUALIZARMETAPRODUCTO
      AUTHORS           : NCARDENAS
      AUTHOR MIGRACION  : 
      DATE MIGRADOR     : 19/03/2026
      TIME              : 02:30 PM
      SOURCE MODULE     : BANCO_PROYECTOS_PROCESOS
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      MODIFICATIONS     :
      DESCRIPTION       : Actualizacion del indicativo metas.por medio del boton PROGRAMAR
      --NAME:    actualizarMetaProducto
      --METHOD:  PUT
    */
  (
    UN_COMPANIA          IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_VIGENCIAPLAN      IN PCK_SUBTIPOS.TI_ANIO,
    UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_TABLA      PCK_SUBTIPOS.TI_TABLA;
    MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
    MI_RTA                  PCK_SUBTIPOS.TI_ENTERO;
    MI_VALORES              CLOB;
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN

 MI_TABLA := 'BP_PLAN_INDICATIVO_METAS';
 MI_CONDICION := ' SELECT '''||UN_COMPANIA||''' COMPANIA ,PI.ID ID_PLAN,PI.vigencia_inicial VIGENCIA_PLAN, (PI.vigencia_inicial + LEVEL - 1) VIGENCIA_META,
                        PI.DESCRIPCION,PI.DEPENDENCIA,PI.PONDERACION PONDERACION_META,0 VALOR_PROGRAMADO_META,0  VALOR_EJECUTADO_META,
                       CASE  WHEN PI.PONDERACION * PI.META < 0 THEN 0 ELSE PI.PONDERACION * PI.META END AS CANTIDAD_PROGRAMADA , 0  CANTIDAD_EJECUTADA,
                        PI.DESCRIPCION || '' ('' || NVL(PI.vigencia_inicial + LEVEL - 1,0) || '')''  DESCRIPCION2,0 VALOR_PROGRAMADO_META_OTROS, 
                        PI.META META_BRUTA , null CODIGO_FUT, ''' || UN_USUARIO || ''' CREATED_BY, 0 META, 0 PRESUPUESTO, 0 COMPROMETIDO,0  PAGADO ,
                        SYSDATE DATE_CREATED , 0 VALOR_OBLIGACIONES ,0  VALOR_DISPONIBLE
                    FROM  BP_PLAN_INDICATIVO PI
                    WHERE PI.COMPANIA ='''||UN_COMPANIA||''' AND PI.vigencia_inicial = ' || UN_VIGENCIAPLAN || ' AND PI.TIPO_META_PLAN = 002 
                    CONNECT BY 
                        LEVEL <= (PI.VIGENCIA_FINAL - PI.vigencia_inicial + 1)
                        AND PRIOR PI.ROWID = PI.ROWID
                        AND PRIOR SYS_GUID() IS NOT NULL ';
            MI_CAMPOS := 'TABLA.COMPANIA   = VISTA.COMPANIA
                            AND TABLA.ID_PLAN         = VISTA.ID_PLAN
                            AND TABLA.VIGENCIA_PLAN   = VISTA.VIGENCIA_PLAN
                            AND TABLA.VIGENCIA_META = VISTA.VIGENCIA_META ';
            MI_VALORES :='INSERT (COMPANIA,ID_PLAN, VIGENCIA_PLAN,  VIGENCIA_META, DESCRIPCION,DEPENDENCIA,
                                        PONDERACION_META,  VALOR_PROGRAMADO_META, VALOR_EJECUTADO_META,CANTIDAD_PROGRAMADA,
                                        CANTIDAD_EJECUTADA,DESCRIPCION2,VALOR_PROGRAMADO_META_OTROS,
                                         META_BRUTA, CODIGO_FUT, CREATED_BY, META, PRESUPUESTO,
                                        COMPROMETIDO, PAGADO, DATE_CREATED, VALOR_OBLIGACIONES,  VALOR_DISPONIBLE)
                                VALUES (VISTA.COMPANIA, VISTA.ID_PLAN, VISTA.VIGENCIA_PLAN, VISTA.VIGENCIA_META, VISTA.DESCRIPCION,VISTA.DEPENDENCIA,
                                        VISTA.PONDERACION_META, VISTA.VALOR_PROGRAMADO_META, VISTA.VALOR_EJECUTADO_META,VISTA.CANTIDAD_PROGRAMADA,
                                        VISTA.CANTIDAD_EJECUTADA, VISTA.DESCRIPCION2,
                                        VISTA.VALOR_PROGRAMADO_META_OTROS,
                                        VISTA.META_BRUTA,VISTA.CODIGO_FUT,
                                        VISTA.CREATED_BY,
                                        VISTA.META,
                                        VISTA.PRESUPUESTO,
                                        VISTA.COMPROMETIDO, VISTA.PAGADO, VISTA.DATE_CREATED, VISTA.VALOR_OBLIGACIONES,
                                        VISTA.VALOR_DISPONIBLE)';
            BEGIN
                MI_RTA := PCK_DATOS.FC_ACME(UN_ACCION   => 'IN',
                                         UN_TABLA       => MI_TABLA,
                                         UN_MERGEUSING  => MI_CONDICION,
                                         UN_MERGEENLACE => MI_CAMPOS,
                                         UN_MERGENOEXIS => MI_VALORES);
             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    =>  SQLCODE,
              UN_ERROR_COD  =>  PCK_ERRORES.ERR_BANCOP_ACT_METABRUTA,
              UN_TABLAERROR =>  MI_TABLA,
              UN_REEMPLAZOS =>  MI_MSGERROR );

            END;


END PR_ACTUALIZARMETAPRODUCTO;

FUNCTION FC_CARGAR_PLAN_INDICATIVO
/*
    NAME              : FC_CARGAR_PLAN_INDICATIVO
    DESCRIPTION       : Carga masiva de Plan Indicativo desde Excel.
    --NAME:    cargarPlanIndicativo
    --METHOD:  POST
*/
(
    UN_COMPANIA  IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CADENA    IN CLOB,
    UN_USUARIO   IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN CLOB
AS
    MI_DATOS_FILA         PCK_SYSMAN_UTL.T_SPLIT;
    MI_DATOS_COLUMNAS     PCK_SYSMAN_UTL.T_SPLIT;
    MI_MENSAJE            CLOB    := '';
    MI_LOG                CLOB    := '';
    MI_INSERTADOS         NUMBER  := 0;
    MI_ACTUALIZADOS       NUMBER  := 0;
    MI_OMITIDOS           NUMBER  := 0;
    CONTADOR              NUMBER  := 0;
    MI_CODIGO_PLAN        VARCHAR2(32   CHAR);
    MI_VIG_GUBE_STR       VARCHAR2(200  CHAR);
    MI_VIG_FINAL_STR      VARCHAR2(200  CHAR);
    MI_DESCRIPCION        VARCHAR2(4000  CHAR);
    MI_TIPO_INDICADOR     VARCHAR2(10   CHAR);
    MI_UNIDAD_MEDIDA      VARCHAR2(12   CHAR);
    MI_INDICADOR_MEDIDA   VARCHAR2(255  CHAR);
    MI_NOMBRE_INDICADOR   VARCHAR2(255  CHAR);
    MI_META_CUATRIE_STR   VARCHAR2(200  CHAR);
    MI_LINEA_BASE_STR     VARCHAR2(200  CHAR);
    MI_CODIGO_DEPENDENCIA VARCHAR2(12   CHAR);
    MI_DESCRIPCION_META   VARCHAR2(4000  CHAR);
    MI_TEXTO_META         VARCHAR2(4000 CHAR);
    MI_CODIGO_SECTOR      VARCHAR2(10   CHAR);
    MI_VIG_GUBE_NUM       NUMBER(4);
    MI_VIG_FINAL_NUM      NUMBER(4);
    MI_META_CUATRIE_NUM   NUMBER(20,2);
    MI_LINEA_BASE_NUM     NUMBER(20,2);
    MI_EXISTE_PLAN        NUMBER := 0;
    MI_EXISTE_DEP         NUMBER := 0;
    MI_EXISTE_SECTOR      NUMBER := 0;
    MI_EXISTE_UNIDAD      NUMBER := 0;
    MI_EXISTE_VIG         NUMBER := 0;
    MI_EXISTE_PREDECESOR  NUMBER := 0;
    MI_DIGITOS_PLAN       NUMBER  := 0;
    MI_META_RESUL         NUMBER  := 0;
    MI_META_PRODUC        NUMBER  := 0;
    MI_MANEJA_DEPEN       NUMBER  := 0;
    MI_ES_NIVEL_META      BOOLEAN := FALSE;
    MI_DIGITOS_NIVEL_ANT  NUMBER  := 0;
    MI_PREDECESOR         VARCHAR2(32 CHAR);
    MI_TIPO_META_PLAN     VARCHAR2(3  CHAR);
    MI_ES_INDICADOR       NUMBER  := 0;
    MI_CAMPOS             VARCHAR2(32000 CHAR) := '';
    MI_VALORES            VARCHAR2(32000 CHAR) := '';
    MI_TABLA              VARCHAR2(200   CHAR);
    MI_RTA                CLOB := '';
    MI_OBSERVACION        VARCHAR2(4000 CHAR);
    CN_NODATA    CONSTANT VARCHAR2(6)  := 'NoData';
    CN_OK        CONSTANT VARCHAR2(7)  := 'Cargado';

BEGIN

    IF UN_CADENA IS NULL OR TRIM(UN_CADENA) IS NULL THEN
        RETURN 'ERROR|No se recibió información para procesar.';
    END IF;

    MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(
                         UN_LISTA       => UN_CADENA,
                         UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_REG
                     );

    IF MI_DATOS_FILA IS NULL OR MI_DATOS_FILA.COUNT = 0 THEN
        RETURN 'ERROR|La cadena no contiene registros válidos.';
    END IF;

    MI_LOG := '========== LOG DE CARGA - PLAN INDICATIVO ==========' || CHR(10);

    <<DATOS_PLAN_INDICATIVO>>
    FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST
    LOOP
        CONTADOR := CONTADOR + 1;
        MI_CODIGO_PLAN        := NULL;
        MI_VIG_GUBE_STR       := NULL;
        MI_VIG_FINAL_STR      := NULL;
        MI_DESCRIPCION        := NULL;
        MI_TIPO_INDICADOR     := NULL;
        MI_UNIDAD_MEDIDA      := NULL;
        MI_INDICADOR_MEDIDA   := NULL;
        MI_NOMBRE_INDICADOR   := NULL;
        MI_META_CUATRIE_STR   := NULL;
        MI_LINEA_BASE_STR     := NULL;
        MI_CODIGO_DEPENDENCIA := NULL;
        MI_DESCRIPCION_META   := NULL;
        MI_TEXTO_META         := NULL;
        MI_CODIGO_SECTOR      := NULL;
        MI_VIG_GUBE_NUM       := NULL;
        MI_VIG_FINAL_NUM      := NULL;
        MI_META_CUATRIE_NUM   := NULL;
        MI_LINEA_BASE_NUM     := NULL;
        MI_EXISTE_PLAN        := 0;
        MI_EXISTE_DEP         := 0;
        MI_EXISTE_SECTOR      := 0;
        MI_EXISTE_UNIDAD      := 0;
        MI_EXISTE_VIG         := 0;
        MI_EXISTE_PREDECESOR  := 0;
        MI_DIGITOS_PLAN       := 0;
        MI_META_RESUL         := 0;
        MI_META_PRODUC        := 0;
        MI_MANEJA_DEPEN       := 0;
        MI_ES_NIVEL_META      := FALSE;
        MI_DIGITOS_NIVEL_ANT  := 0;
        MI_PREDECESOR         := NULL;
        MI_TIPO_META_PLAN     := NULL;
        MI_ES_INDICADOR       := 0;
        MI_OBSERVACION        := CN_OK;

        IF TRIM(MI_DATOS_FILA(RS)) IS NULL THEN
            CONTINUE;
        END IF;

        MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(
                                 UN_LISTA       => MI_DATOS_FILA(RS),
                                 UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_COL
                             );

        IF MI_DATOS_COLUMNAS.COUNT < 14 THEN
            MI_OBSERVACION := 'Número de columnas incorrecto. Se esperaban 14, llegaron ' ||
                              MI_DATOS_COLUMNAS.COUNT || '.';
            MI_OMITIDOS := MI_OMITIDOS + 1;
            MI_LOG := MI_LOG ||
                'CODIGO_PLAN: '  || RPAD(NVL(TRIM(MI_DATOS_COLUMNAS(1)), '(vacío)'), 12) ||
                '| VIGENCIA: ---- ' ||
                '| DESCRIPCION: (no disponible)              ' ||
                '| RESULTADO: ' || MI_OBSERVACION || CHR(10);
            CONTINUE;
        END IF;

        MI_CODIGO_PLAN        := TRIM(MI_DATOS_COLUMNAS(1));
        MI_VIG_GUBE_STR       := TRIM(MI_DATOS_COLUMNAS(2));
        MI_VIG_FINAL_STR      := TRIM(MI_DATOS_COLUMNAS(3));
        MI_DESCRIPCION        := TRIM(MI_DATOS_COLUMNAS(4));
        MI_TIPO_INDICADOR     := TRIM(MI_DATOS_COLUMNAS(5));
        MI_UNIDAD_MEDIDA      := TRIM(MI_DATOS_COLUMNAS(6));
        MI_INDICADOR_MEDIDA   := TRIM(MI_DATOS_COLUMNAS(7));
        MI_NOMBRE_INDICADOR   := TRIM(MI_DATOS_COLUMNAS(8));
        MI_META_CUATRIE_STR   := TRIM(MI_DATOS_COLUMNAS(9));
        MI_LINEA_BASE_STR     := TRIM(MI_DATOS_COLUMNAS(10));
        MI_CODIGO_DEPENDENCIA := TRIM(MI_DATOS_COLUMNAS(11));
        MI_DESCRIPCION_META   := TRIM(MI_DATOS_COLUMNAS(12));
        MI_TEXTO_META         := TRIM(MI_DATOS_COLUMNAS(13));
        MI_CODIGO_SECTOR      := TRIM(MI_DATOS_COLUMNAS(14));
        
        IF MI_TIPO_INDICADOR     = CN_NODATA THEN MI_TIPO_INDICADOR     := NULL; END IF;
        IF MI_UNIDAD_MEDIDA      = CN_NODATA THEN MI_UNIDAD_MEDIDA      := NULL; END IF;
        IF MI_INDICADOR_MEDIDA   = CN_NODATA THEN MI_INDICADOR_MEDIDA   := NULL; END IF;
        IF MI_NOMBRE_INDICADOR   = CN_NODATA THEN MI_NOMBRE_INDICADOR   := NULL; END IF;
        IF MI_META_CUATRIE_STR   = CN_NODATA THEN MI_META_CUATRIE_STR   := NULL; END IF;
        IF MI_LINEA_BASE_STR     = CN_NODATA THEN MI_LINEA_BASE_STR     := NULL; END IF;
        IF MI_CODIGO_DEPENDENCIA = CN_NODATA THEN MI_CODIGO_DEPENDENCIA := NULL; END IF;
        IF MI_DESCRIPCION_META   = CN_NODATA THEN MI_DESCRIPCION_META   := NULL; END IF;
        IF MI_TEXTO_META         = CN_NODATA THEN MI_TEXTO_META         := NULL; END IF;
        IF MI_CODIGO_SECTOR      = CN_NODATA THEN MI_CODIGO_SECTOR      := NULL; END IF;

        IF MI_CODIGO_PLAN IS NULL OR MI_CODIGO_PLAN = CN_NODATA THEN
            MI_OBSERVACION := 'CODIGO_PLAN es obligatorio y está vacío.';
            MI_OMITIDOS := MI_OMITIDOS + 1;
            GOTO AGREGAR_LOG;
        END IF;

        IF LENGTH(MI_CODIGO_PLAN) > 32 THEN
            MI_OBSERVACION := 'CODIGO_PLAN supera la longitud máxima permitida (32 caracteres). Valor: "' ||
                              MI_CODIGO_PLAN || '".';
            MI_OMITIDOS := MI_OMITIDOS + 1;
            GOTO AGREGAR_LOG;
        END IF;

        IF MI_VIG_GUBE_STR IS NULL OR MI_VIG_GUBE_STR = CN_NODATA THEN
            MI_OBSERVACION := 'VIGENCIA_GUBERNAMENTAL es obligatoria y está vacía.';
            MI_OMITIDOS := MI_OMITIDOS + 1;
            GOTO AGREGAR_LOG;
        END IF;

        BEGIN
            MI_VIG_GUBE_NUM := TO_NUMBER(MI_VIG_GUBE_STR);
        EXCEPTION
            WHEN VALUE_ERROR THEN
                MI_OBSERVACION := 'VIGENCIA_GUBERNAMENTAL "' || MI_VIG_GUBE_STR ||
                                  '" no es un año numérico válido (NUMBER 4 dígitos).';
                MI_OMITIDOS := MI_OMITIDOS + 1;
                GOTO AGREGAR_LOG;
        END;

        IF LENGTH(TO_CHAR(MI_VIG_GUBE_NUM)) <> 4 THEN
            MI_OBSERVACION := 'VIGENCIA_GUBERNAMENTAL debe ser un año de 4 dígitos. Valor recibido: ' ||
                              MI_VIG_GUBE_STR || '.';
            MI_OMITIDOS := MI_OMITIDOS + 1;
            GOTO AGREGAR_LOG;
        END IF;

        IF MI_VIG_FINAL_STR IS NULL OR MI_VIG_FINAL_STR = CN_NODATA THEN
            MI_OBSERVACION := 'VIGENCIA_FINAL es obligatoria y está vacía.';
            MI_OMITIDOS := MI_OMITIDOS + 1;
            GOTO AGREGAR_LOG;
        END IF;

        BEGIN
            MI_VIG_FINAL_NUM := TO_NUMBER(MI_VIG_FINAL_STR);
        EXCEPTION
            WHEN VALUE_ERROR THEN
                MI_OBSERVACION := 'VIGENCIA_FINAL "' || MI_VIG_FINAL_STR ||
                                  '" no es un año numérico válido (NUMBER 4 dígitos).';
                MI_OMITIDOS := MI_OMITIDOS + 1;
                GOTO AGREGAR_LOG;
        END;

        IF LENGTH(TO_CHAR(MI_VIG_FINAL_NUM)) <> 4 THEN
            MI_OBSERVACION := 'VIGENCIA_FINAL debe ser un año de 4 dígitos. Valor recibido: ' ||
                              MI_VIG_FINAL_STR || '.';
            MI_OMITIDOS := MI_OMITIDOS + 1;
            GOTO AGREGAR_LOG;
        END IF;

        IF MI_VIG_FINAL_NUM < MI_VIG_GUBE_NUM THEN
            MI_OBSERVACION := 'VIGENCIA_FINAL (' || MI_VIG_FINAL_NUM ||
                              ') no puede ser menor que VIGENCIA_GUBERNAMENTAL (' ||
                              MI_VIG_GUBE_NUM || ').';
            MI_OMITIDOS := MI_OMITIDOS + 1;
            GOTO AGREGAR_LOG;
        END IF;

        IF MI_DESCRIPCION IS NULL OR MI_DESCRIPCION = CN_NODATA THEN
            MI_OBSERVACION := 'DESCRIPCION es obligatoria y está vacía.';
            MI_OMITIDOS := MI_OMITIDOS + 1;
            GOTO AGREGAR_LOG;
        END IF;

        IF LENGTH(MI_DESCRIPCION) > 300 THEN
            MI_DESCRIPCION := SUBSTR(MI_DESCRIPCION, 1, 300);
        END IF;

        BEGIN
            SELECT COUNT(1)
              INTO MI_EXISTE_VIG
              FROM BP_NIVEL_PLAN_IND
             WHERE COMPANIA = UN_COMPANIA
               AND VIGENCIA = MI_VIG_GUBE_NUM;
        EXCEPTION
            WHEN OTHERS THEN
                MI_EXISTE_VIG := 0;
        END;

        IF MI_EXISTE_VIG = 0 THEN
            MI_OBSERVACION := 'VIGENCIA_GUBERNAMENTAL "' || MI_VIG_GUBE_NUM ||
                              '" no tiene niveles de plan configurados en BP_NIVEL_PLAN_IND.';
            MI_OMITIDOS := MI_OMITIDOS + 1;
            GOTO AGREGAR_LOG;
        END IF;

        MI_DIGITOS_PLAN := LENGTH(MI_CODIGO_PLAN);

        BEGIN
            SELECT NVL(META_RESUL,   0),
                   NVL(META_PRODUC,  0),
                   NVL(MANEJA_DEPEN, 0)
              INTO MI_META_RESUL,
                   MI_META_PRODUC,
                   MI_MANEJA_DEPEN
              FROM BP_NIVEL_PLAN_IND
             WHERE COMPANIA = UN_COMPANIA
               AND VIGENCIA = MI_VIG_GUBE_NUM
               AND DIGITOS  = MI_DIGITOS_PLAN;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                MI_OBSERVACION := 'El CODIGO_PLAN "' || MI_CODIGO_PLAN ||
                                  '" tiene ' || MI_DIGITOS_PLAN ||
                                  ' dígito(s) pero no existe un nivel configurado para esa ' ||
                                  'cantidad en BP_NIVEL_PLAN_IND para la vigencia ' ||
                                  MI_VIG_GUBE_NUM || '.';
                MI_OMITIDOS := MI_OMITIDOS + 1;
                GOTO AGREGAR_LOG;
            WHEN OTHERS THEN
                MI_OBSERVACION := 'Error al consultar BP_NIVEL_PLAN_IND: ' || SQLERRM;
                MI_OMITIDOS := MI_OMITIDOS + 1;
                GOTO AGREGAR_LOG;
        END;

        MI_ES_NIVEL_META := (MI_META_RESUL = -1 OR MI_META_PRODUC = -1);

        IF MI_META_RESUL = -1 THEN
            MI_TIPO_META_PLAN := '001';
            MI_ES_INDICADOR   := 0;
        ELSIF MI_META_PRODUC = -1 THEN
            MI_TIPO_META_PLAN := '002';
            MI_ES_INDICADOR   := 1;
        ELSE
            MI_TIPO_META_PLAN := NULL;
            MI_ES_INDICADOR   := 0;
        END IF;

        IF MI_ES_NIVEL_META THEN

            IF MI_TIPO_INDICADOR IS NULL OR MI_TIPO_INDICADOR = CN_NODATA THEN
                MI_OBSERVACION := 'TIPO_INDICADOR es obligatorio para este nivel (' ||
                                  MI_DIGITOS_PLAN || ' dígitos) y está vacío.';
                MI_OMITIDOS := MI_OMITIDOS + 1;
                GOTO AGREGAR_LOG;
            END IF;

            IF MI_TIPO_INDICADOR NOT IN ('MI','MM','MR','MG') THEN
                MI_OBSERVACION := 'TIPO_INDICADOR "' || MI_TIPO_INDICADOR ||
                                  '" no es válido. Valores permitidos: MI, MM, MR, MG.';
                MI_OMITIDOS := MI_OMITIDOS + 1;
                GOTO AGREGAR_LOG;
            END IF;

            IF MI_UNIDAD_MEDIDA IS NULL OR MI_UNIDAD_MEDIDA = CN_NODATA THEN
                MI_OBSERVACION := 'UNIDAD_MEDIDA es obligatoria para este nivel (' ||
                                  MI_DIGITOS_PLAN || ' dígitos) y está vacía.';
                MI_OMITIDOS := MI_OMITIDOS + 1;
                GOTO AGREGAR_LOG;
            END IF;

            IF LENGTH(MI_UNIDAD_MEDIDA) > 12 THEN
                MI_OBSERVACION := 'UNIDAD_MEDIDA supera la longitud máxima permitida (12 caracteres). Valor: "' ||
                                  MI_UNIDAD_MEDIDA || '".';
                MI_OMITIDOS := MI_OMITIDOS + 1;
                GOTO AGREGAR_LOG;
            END IF;

            IF MI_META_CUATRIE_STR IS NULL OR MI_META_CUATRIE_STR = CN_NODATA THEN
                MI_OBSERVACION := 'META_CUATRIENIO es obligatoria para este nivel (' ||
                                  MI_DIGITOS_PLAN || ' dígitos) y está vacía.';
                MI_OMITIDOS := MI_OMITIDOS + 1;
                GOTO AGREGAR_LOG;
            END IF;

            BEGIN
                MI_META_CUATRIE_NUM := TO_NUMBER(REPLACE(MI_META_CUATRIE_STR, ',', '.'));
            EXCEPTION
                WHEN VALUE_ERROR THEN
                    MI_OBSERVACION := 'META_CUATRIENIO "' || MI_META_CUATRIE_STR ||
                                      '" no es un valor numérico válido.';
                    MI_OMITIDOS := MI_OMITIDOS + 1;
                    GOTO AGREGAR_LOG;
            END;

            IF MI_LINEA_BASE_STR IS NULL OR MI_LINEA_BASE_STR = CN_NODATA THEN
                MI_OBSERVACION := 'LINEA_BASE es obligatoria para este nivel (' ||
                                  MI_DIGITOS_PLAN || ' dígitos) y está vacía.';
                MI_OMITIDOS := MI_OMITIDOS + 1;
                GOTO AGREGAR_LOG;
            END IF;

            BEGIN
                MI_LINEA_BASE_NUM := TO_NUMBER(REPLACE(MI_LINEA_BASE_STR, ',', '.'));
            EXCEPTION
                WHEN VALUE_ERROR THEN
                    MI_OBSERVACION := 'LINEA_BASE "' || MI_LINEA_BASE_STR ||
                                      '" no es un valor numérico válido.';
                    MI_OMITIDOS := MI_OMITIDOS + 1;
                    GOTO AGREGAR_LOG;
            END;

            IF MI_CODIGO_DEPENDENCIA IS NULL OR MI_CODIGO_DEPENDENCIA = CN_NODATA THEN
                MI_OBSERVACION := 'CODIGO_DEPENDENCIA es obligatorio para este nivel (' ||
                                  MI_DIGITOS_PLAN || ' dígitos) y está vacío.';
                MI_OMITIDOS := MI_OMITIDOS + 1;
                GOTO AGREGAR_LOG;
            END IF;

            BEGIN
                SELECT COUNT(1)
                  INTO MI_EXISTE_DEP
                  FROM DEPENDENCIA
                 WHERE COMPANIA = UN_COMPANIA
                   AND CODIGO   = MI_CODIGO_DEPENDENCIA;
            EXCEPTION
                WHEN OTHERS THEN MI_EXISTE_DEP := 0;
            END;

            IF MI_EXISTE_DEP = 0 THEN
                MI_OBSERVACION := 'CODIGO_DEPENDENCIA "' || MI_CODIGO_DEPENDENCIA ||
                                  '" no existe en la tabla DEPENDENCIA.';
                MI_OMITIDOS := MI_OMITIDOS + 1;
                GOTO AGREGAR_LOG;
            END IF;

            IF MI_CODIGO_SECTOR IS NULL OR MI_CODIGO_SECTOR = CN_NODATA THEN
                MI_OBSERVACION := 'CODIGO_SECTOR es obligatorio para este nivel (' ||
                                  MI_DIGITOS_PLAN || ' dígitos) y está vacío.';
                MI_OMITIDOS := MI_OMITIDOS + 1;
                GOTO AGREGAR_LOG;
            END IF;

            BEGIN
                SELECT COUNT(1)
                  INTO MI_EXISTE_SECTOR
                  FROM SECTORES
                 WHERE COMPANIA = UN_COMPANIA
                   AND CODIGO   = MI_CODIGO_SECTOR;
            EXCEPTION
                WHEN OTHERS THEN MI_EXISTE_SECTOR := 0;
            END;

            IF MI_EXISTE_SECTOR = 0 THEN
                MI_OBSERVACION := 'CODIGO_SECTOR "' || MI_CODIGO_SECTOR ||
                                  '" no existe en la tabla SECTORES.';
                MI_OMITIDOS := MI_OMITIDOS + 1;
                GOTO AGREGAR_LOG;
            END IF;

        ELSE
            MI_CODIGO_DEPENDENCIA := '999999999999';
            MI_LINEA_BASE_NUM     := 0;
            MI_META_CUATRIE_NUM   := 0;
            MI_CODIGO_SECTOR      := NULL;
            MI_TIPO_INDICADOR     := NULL;
            MI_UNIDAD_MEDIDA      := NULL;
        END IF;

        IF LENGTH(NVL(MI_DESCRIPCION_META, '')) > 255 THEN
            MI_DESCRIPCION_META := SUBSTR(MI_DESCRIPCION_META, 1, 255);
        END IF;

        IF LENGTH(NVL(MI_TEXTO_META, '')) > 255 THEN
            MI_TEXTO_META := SUBSTR(MI_TEXTO_META, 1, 255);
        END IF;

        IF LENGTH(NVL(MI_INDICADOR_MEDIDA, '')) > 255 THEN
            MI_INDICADOR_MEDIDA := SUBSTR(MI_INDICADOR_MEDIDA, 1, 255);
        END IF;

        IF LENGTH(NVL(MI_NOMBRE_INDICADOR, '')) > 255 THEN
            MI_NOMBRE_INDICADOR := SUBSTR(MI_NOMBRE_INDICADOR, 1, 255);
        END IF;

        BEGIN
            SELECT MAX(DIGITOS)
              INTO MI_DIGITOS_NIVEL_ANT
              FROM BP_NIVEL_PLAN_IND
             WHERE COMPANIA = UN_COMPANIA
               AND VIGENCIA = MI_VIG_GUBE_NUM
               AND DIGITOS  < MI_DIGITOS_PLAN;
        EXCEPTION
            WHEN OTHERS THEN
                MI_DIGITOS_NIVEL_ANT := NULL;
        END;

        IF MI_DIGITOS_NIVEL_ANT IS NULL THEN
            MI_PREDECESOR := NULL;
        ELSE
            
            MI_PREDECESOR := SUBSTR(MI_CODIGO_PLAN, 1, MI_DIGITOS_NIVEL_ANT);

            BEGIN
                SELECT COUNT(1)
                  INTO MI_EXISTE_PREDECESOR
                  FROM BP_PLAN_INDICATIVO
                 WHERE COMPANIA         = UN_COMPANIA
                   AND ID               = MI_PREDECESOR
                   AND VIGENCIA_INICIAL = MI_VIG_GUBE_NUM;
            EXCEPTION
                WHEN OTHERS THEN
                    MI_EXISTE_PREDECESOR := 0;
            END;

            IF MI_EXISTE_PREDECESOR = 0 THEN
                MI_OBSERVACION := 'El predecesor "' || MI_PREDECESOR ||
                                  '" del CODIGO_PLAN "' || MI_CODIGO_PLAN ||
                                  '" no existe en BP_PLAN_INDICATIVO. ' ||
                                  'Debe cargar primero el nivel padre.';
                MI_OMITIDOS := MI_OMITIDOS + 1;
                GOTO AGREGAR_LOG;
            END IF;

        END IF;

        BEGIN
            SELECT COUNT(1)
              INTO MI_EXISTE_PLAN
              FROM BP_PLAN_INDICATIVO
             WHERE COMPANIA         = UN_COMPANIA
               AND ID               = MI_CODIGO_PLAN
               AND VIGENCIA_INICIAL = MI_VIG_GUBE_NUM;
        EXCEPTION
            WHEN OTHERS THEN
                MI_OBSERVACION := 'Error al consultar BP_PLAN_INDICATIVO: ' || SQLERRM;
                MI_OMITIDOS := MI_OMITIDOS + 1;
                GOTO AGREGAR_LOG;
        END;

        IF MI_EXISTE_PLAN > 0 THEN
            MI_OBSERVACION := 'Ya existe un registro en BP_PLAN_INDICATIVO para CODIGO_PLAN "' ||
                              MI_CODIGO_PLAN || '" con VIGENCIA_INICIAL ' ||
                              MI_VIG_GUBE_NUM || '. No se realizó ninguna acción.';
            MI_OMITIDOS := MI_OMITIDOS + 1;
            GOTO AGREGAR_LOG;
        END IF;

        BEGIN
            MI_TABLA  := 'BP_PLAN_INDICATIVO';

            MI_CAMPOS := 'COMPANIA, ID, VIGENCIA_INICIAL, VIGENCIA_FINAL, DESCRIPCION, ' ||
                         'TIPO_META_PLAN, UNIDAD_MEDIDA, DEPENDENCIA, PONDERACION, ' ||
                         'DESCRIPCION_INDICADOR_MEDIDA, ES_INDICADOR, PREDECESOR, ' ||
                         'DESCRIPCION_TEXTO_BP, LB, META, DESCRIPCION_INDICADOR, ' ||
                         'NOMBRE_INDICADOR, AVANCE, AVANCE_FINANCIERO, SECTOR, ' ||
                         'PRESUPUESTO, COMPROMETIDO, PAGADO, ' ||
                         'VALOR_OBLIGACIONES, VALOR_OBLIGACIONES_FIN, ' ||
                         'CREATED_BY, DATE_CREATED';

            MI_VALORES :=
                '''' || UN_COMPANIA                              || ''',' ||
                '''' || MI_CODIGO_PLAN                           || ''',' ||
                       MI_VIG_GUBE_NUM                           || ','   ||
                       MI_VIG_FINAL_NUM                          || ','   ||
                '''' || MI_DESCRIPCION                           || ''',' ||
                CASE WHEN MI_TIPO_META_PLAN IS NOT NULL
                     THEN '''' || MI_TIPO_META_PLAN || ''''
                     ELSE 'NULL' END                             || ','   ||
                CASE WHEN MI_UNIDAD_MEDIDA IS NOT NULL
                     THEN '''' || MI_UNIDAD_MEDIDA || ''''
                     ELSE 'NULL' END                             || ','   ||
                '''' || MI_CODIGO_DEPENDENCIA                    || ''',' ||
                       0                                         || ','   ||
                '''' || NVL(MI_INDICADOR_MEDIDA, '')             || ''',' ||
                       MI_ES_INDICADOR                           || ','   ||
                CASE WHEN MI_PREDECESOR IS NOT NULL
                     THEN '''' || MI_PREDECESOR || ''''
                     ELSE 'NULL' END                             || ','   ||
                '''' || NVL(MI_TEXTO_META, '')                   || ''',' ||
                       NVL(MI_LINEA_BASE_NUM, 0)                 || ','   ||
                       NVL(MI_META_CUATRIE_NUM, 0)               || ','   ||
                '''' || NVL(MI_DESCRIPCION_META, '')             || ''',' ||
                '''' || NVL(MI_NOMBRE_INDICADOR, '')             || ''',' ||
                       0                                         || ','   ||
                       0                                         || ','   ||
                CASE WHEN MI_CODIGO_SECTOR IS NOT NULL
                     THEN '''' || MI_CODIGO_SECTOR || ''''
                     ELSE 'NULL' END                             || ','   ||
                       0                                         || ','   ||
                       0                                         || ','   ||
                       0                                         || ','   ||
                       0                                         || ','   ||
                       0                                         || ','   ||
                '''' || UN_USUARIO                               || ''',' ||
                'SYSDATE';

             MI_RTA := PCK_DATOS.FC_ACME(
                           UN_TABLA   => MI_TABLA,
                           UN_ACCION  => 'I',
                           UN_CAMPOS  => MI_CAMPOS,
                           UN_VALORES => MI_VALORES
                       ); 

            MI_INSERTADOS  := MI_INSERTADOS + 1;
            MI_OBSERVACION := CN_OK;

        EXCEPTION
            WHEN OTHERS THEN
                MI_OBSERVACION := 'Error al insertar en BP_PLAN_INDICATIVO: ' || SQLERRM;
                MI_OMITIDOS := MI_OMITIDOS + 1;
        END;

        <<AGREGAR_LOG>>
        MI_LOG := MI_LOG ||
            'CODIGO_PLAN: '   || RPAD(NVL(MI_CODIGO_PLAN, '(vacío)'), 12) ||
            '| VIGENCIA: '    || NVL(MI_VIG_GUBE_STR, '----') ||
            '-'               || NVL(MI_VIG_FINAL_STR, '----') || ' ' ||
            '| DESCRIPCION: ' || SUBSTR(NVL(MI_DESCRIPCION, '(vacía)'), 1, 40) ||
            CASE WHEN LENGTH(NVL(MI_DESCRIPCION, '')) > 40 THEN '...' ELSE '   ' END ||
            '| RESULTADO: '   ||
            CASE WHEN MI_OBSERVACION = CN_OK THEN 'Cargado'
                 ELSE MI_OBSERVACION
            END || CHR(10);

    END LOOP DATOS_PLAN_INDICATIVO;

    MI_MENSAJE :=
        '╔══════════════════════════════════╗'  || CHR(10) ||
        '║     RESUMEN DE CARGA             ║'  || CHR(10) ||
        '╠══════════════════════════════════╣'  || CHR(10) ||
        '║ PROCESADOS:   ' || LPAD(CONTADOR,        5) || '              ║' || CHR(10) ||
        '║ INSERTADOS:   ' || LPAD(MI_INSERTADOS,   5) || '              ║' || CHR(10) ||
        '║ ACTUALIZADOS: ' || LPAD(MI_ACTUALIZADOS, 5) || '              ║' || CHR(10) ||
        '║ OMITIDOS:     ' || LPAD(MI_OMITIDOS,     5) || '              ║' || CHR(10) ||
        '╚══════════════════════════════════╝'  || CHR(10) ||
        CHR(10) || MI_LOG;

    RETURN MI_MENSAJE;

EXCEPTION
    WHEN OTHERS THEN
        RETURN 'ERROR_GENERAL|Fila ' || CONTADOR || ' - ' || SQLERRM;

END FC_CARGAR_PLAN_INDICATIVO;

END PCK_BANCOS_PROY5;