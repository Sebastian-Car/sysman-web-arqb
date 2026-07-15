create or replace PACKAGE BODY "PCK_ALMACEN" AS

--01

 FUNCTION FC_HALLAPREDECESOR
   /*
      NAME              : FC_HALLAPREDECESOR  --> HallaPredecesor
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : NICOLAS GOMEZ BARBOSA
      DATE MIGRADOR     : 26/10/2015
      TIME              : 11:00 AM
      SOURCE MODULE     : ALMACEN
      MODIFIER          : ELKIN GEOVANNY AMAYA SILVA / PESPITIA (26/07/2017)
      DATE MODIFIED     : 12/01/2017
      TIME              : 12:20 AM
      DESCRIPTION       : Halla el predecesor de un registro
      MODIFICATIONS     : Se cambió el estándar de codificación
                          y se agrego manejo de excepciones.
            (26/07/2017): Se ajustó la consulta MI_STR cuando UN_OPCION = 1

  */
  (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CAMPO          IN VARCHAR2,
    UN_VALOR_CAMPO    IN VARCHAR2,
    UN_TABLA          IN VARCHAR2,
    UN_OPCION         IN PCK_SUBTIPOS.TI_ENTERO DEFAULT NULL
  )
  RETURN VARCHAR2
  AS
    MI_STR          PCK_SUBTIPOS.TI_STRSQL;
    MI_VALOR        VARCHAR2(1000 CHAR);
    MI_RTA          VARCHAR2(320 CHAR):=0;
    RS              SYS_REFCURSOR;
    RSCAMPO         VARCHAR2(3200 CHAR);
  BEGIN
   IF(UN_OPCION=1) THEN
     MI_STR:='SELECT MAX('||UN_CAMPO||')
              FROM   '||UN_TABLA||'
              WHERE  COMPANIA       = '''||UN_COMPANIA||'''
                AND  LENGTH('||UN_CAMPO||')<LENGTH('''||UN_VALOR_CAMPO||''')
                AND  '||UN_CAMPO||'  = SUBSTR( '''||UN_VALOR_CAMPO||''',0,LENGTH('||UN_CAMPO||'))
                AND  TIENEMOVIMIENTO IN(0)';

   ELSIF (UN_OPCION=2) THEN
     MI_STR:='SELECT '||UN_CAMPO||'
              FROM   '||UN_TABLA||'
              WHERE  COMPANIA       ='||UN_COMPANIA||'
                AND  LENGTH('||UN_CAMPO||')<LENGTH('''||UN_VALOR_CAMPO||''')
                AND  '||UN_CAMPO||' = SUBSTR( '''||UN_VALOR_CAMPO||''',0,LENGTH('||UN_CAMPO||'))
              ORDER BY COMPANIA, '||UN_CAMPO||' DESC';

   ELSIF (UN_OPCION=3) THEN
     MI_STR:='SELECT '||UN_CAMPO||'
              FROM '||UN_TABLA||'
              WHERE COMPANIA='||UN_COMPANIA||'
                AND LENGTH('||UN_CAMPO||')<LENGTH('''||UN_VALOR_CAMPO||''')
                AND '||UN_CAMPO||' =SUBSTR( '''||UN_VALOR_CAMPO||''',0,LENGTH('||UN_CAMPO||'))
                AND MOVIMIENTO = ''N''
              ORDER BY COMPANIA, '||UN_CAMPO||' DESC';

   ELSIF (UN_OPCION=4) THEN
     MI_STR:='SELECT '||UN_CAMPO||'
              FROM   '||UN_TABLA||'
              WHERE  COMPANIA       = '||UN_COMPANIA||'
                AND  LENGTH('||UN_CAMPO||')<LENGTH('''||UN_VALOR_CAMPO||''')
                AND  '||UN_CAMPO||' = SUBSTR( '''||UN_VALOR_CAMPO||''',0,LENGTH('||UN_CAMPO||' ))
              ORDER BY '||UN_CAMPO||' DESC';

   ELSE
     MI_STR:='SELECT '||UN_CAMPO||'
              FROM   '||UN_TABLA||'
              WHERE  COMPANIA      = '||UN_COMPANIA||'
                AND  LENGTH('||UN_CAMPO||')<LENGTH('''||UN_VALOR_CAMPO||''')
                AND  '||UN_CAMPO||' = SUBSTR( '''||UN_VALOR_CAMPO||''',0,LENGTH('||UN_CAMPO||' ))
                AND  MOVIMIENTO     = IN(0)
              ORDER BY COMPANIA, '||UN_CAMPO||' DESC';

   END IF;

   BEGIN
     BEGIN
       EXECUTE IMMEDIATE MI_STR INTO MI_VALOR;

       EXCEPTION WHEN NO_DATA_FOUND THEN
         RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
     END;

     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERR_ALMACEN_HALLAPREDECESOR
      );

   END;

   RETURN MI_VALOR;
  END FC_HALLAPREDECESOR;

--03
  FUNCTION FC_VERIFICAR_NOMBRE_DEVOLUTIVO
  /*
    NAME              : FC_VERIFICAR_NOMBRE_DEVOLUTIVO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : NICOLAS GOMEZ BARBOSA
    DATE MIGRADOR     : 02/12/2015
    TIME              : 11:00 AM
    SOURCE MODULE     : ALMACEN
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED     : 12/01/2017
    TIME              : 3:05 PM
    DESCRIPTION       : Retorna el nombre de una dependecia(opcion 1) o responsable (opcion 2) con o sin código
    MODIFICATIONS     : Se cambió el estándar de codificación
                        y se agrego manejo de excepciones.

  */

  (
    UN_OPCION       IN PCK_SUBTIPOS.TI_ENTERO,
    UN_CONCODIGO    IN PCK_SUBTIPOS.TI_ENTERO,
    UN_CODIGO       IN VARCHAR2,
    UN_COMPANIA     IN VARCHAR2
  )RETURN VARCHAR2
  AS
    MI_RTA VARCHAR2(3200 CHAR):='';

  BEGIN

       IF UN_OPCION=1 THEN
        IF UN_CONCODIGO=-1 THEN
          BEGIN
            SELECT DEPENDENCIA.NOMBRE
            INTO   MI_RTA
            FROM   DEPENDENCIA
            WHERE  DEPENDENCIA.COMPANIA = UN_COMPANIA
              AND  DEPENDENCIA.CODIGO   = UN_CODIGO;
            MI_RTA:=MI_RTA||' ('||UN_CODIGO||')';

            EXCEPTION WHEN NO_DATA_FOUND THEN  MI_RTA:='('||UN_CODIGO||')';
          END;
        ELSE
          BEGIN
            SELECT DEPENDENCIA.NOMBRE
            INTO   MI_RTA
            FROM   DEPENDENCIA
            WHERE  DEPENDENCIA.COMPANIA = UN_COMPANIA
              AND  DEPENDENCIA.CODIGO   = UN_CODIGO;

            EXCEPTION WHEN NO_DATA_FOUND THEN  MI_RTA:='';
          END;
        END IF;
    ELSIF UN_OPCION=2 THEN
        IF UN_CONCODIGO=-1 THEN
          BEGIN
            SELECT TERCERO.NOMBRE
            INTO   MI_RTA
            FROM   TERCERO
            WHERE  TERCERO.COMPANIA = UN_COMPANIA
              AND  TERCERO.NIT      = UN_CODIGO;

            MI_RTA:=MI_RTA||' ('||UN_CODIGO||')';

           EXCEPTION WHEN NO_DATA_FOUND THEN  MI_RTA:='('||UN_CODIGO||')';
          END;
        ELSE
            BEGIN
              SELECT TERCERO.NOMBRE
              INTO   MI_RTA
              FROM   TERCERO
              WHERE  TERCERO.COMPANIA = UN_COMPANIA
                AND  TERCERO.NIT=UN_CODIGO;

            EXCEPTION WHEN NO_DATA_FOUND THEN  MI_RTA:='';
            END;
        END IF;
    END IF;

    RETURN MI_RTA;

END FC_VERIFICAR_NOMBRE_DEVOLUTIVO;

--05

  FUNCTION FC_VERIFICAR_PLACA
  /*
    NAME              : FC_VERIFICAR_PLACA
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : NICOLAS GOMEZ BARBOSA
    DATE MIGRADOR     : 04/12/2015
    TIME              : 11:00 AM
    SOURCE MODULE     : ALMACEN
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED     : 12/01/2017
    TIME              : 4:00 PM
    DESCRIPTION       : Verifica que las series que están en d_devolutivo esten en devolutivo. Si no verifica y agrega la placa en devolutivo.
    MODIFICATIONS     : REVISAR EL CAMPO DEPENDENCIA DE LA TABLA MOVIMIENTO. NO EXISTE

  */
  (
    UN_COMPANIA  IN   VARCHAR2
  )RETURN CLOB
  AS
    MI_RTA          CLOB:='';
    MI_I            NUMBER;
    MI_RETORNO      VARCHAR2(32000 CHAR);
    MI_CAMPOS       VARCHAR2(32000 CHAR):='';
    MI_CAMPOS2      VARCHAR2(32000 CHAR):='';
    MI_TOTAL        NUMBER:=1;
    MI_VALORES      VARCHAR2(32000 CHAR):='';
    MI_VALORES2     VARCHAR2(32000 CHAR):='';
    MI_RS           SYS_REFCURSOR;
    MI_ERROR_FUN    NUMBER:=GL_ERROR_NUM + 3;
  BEGIN
    SELECT COUNT(*)
    INTO   MI_I
    FROM   MOVIMIENTO
      INNER JOIN INVENTARIO
        INNER JOIN TIPOMOVIMIENTO
          INNER JOIN D_MOVIMIENTO
             LEFT JOIN DEVOLUTIVO ON D_MOVIMIENTO.SERIE     = DEVOLUTIVO.SERIE
              AND D_MOVIMIENTO.ELEMENTO = DEVOLUTIVO.ELEMENTO
              AND D_MOVIMIENTO.COMPANIA = DEVOLUTIVO.COMPANIA
          ON TIPOMOVIMIENTO.CODIGO    = D_MOVIMIENTO.TIPOMOVIMIENTO
          AND TIPOMOVIMIENTO.COMPANIA = D_MOVIMIENTO.COMPANIA
        ON  INVENTARIO.COMPANIA       = D_MOVIMIENTO.COMPANIA
        AND INVENTARIO.CODIGOELEMENTO = D_MOVIMIENTO.ELEMENTO
      ON  MOVIMIENTO.COMPANIA       = D_MOVIMIENTO.COMPANIA
      AND MOVIMIENTO.TIPOMOVIMIENTO = D_MOVIMIENTO.TIPOMOVIMIENTO
      AND MOVIMIENTO.NUMERO         = D_MOVIMIENTO.MOVIMIENTO
      AND TIPOMOVIMIENTO.COMPANIA   = MOVIMIENTO.COMPANIA
      AND TIPOMOVIMIENTO.CODIGO     = MOVIMIENTO.TIPOMOVIMIENTO
      AND MOVIMIENTO.COMPANIA       = D_MOVIMIENTO.COMPANIA
    WHERE  D_MOVIMIENTO.COMPANIA = UN_COMPANIA
      AND DEVOLUTIVO.SERIE       IS NULL
      AND TIPOMOVIMIENTO.CLASE   =  'E'
      AND INVENTARIO.TIPO        =  'D'
      AND D_MOVIMIENTO.IND_REG   =  -1;

  IF MI_I<>0 THEN
    MI_RTA:='************************************RELACION DE REGISTROS PROCESADOS************************************'||CHR(10);
    MI_RTA:=MI_RTA||CHR(10);

    MI_CAMPOS:='COMPANIA
               , ELEMENTO
               , SERIE
               , DEPENDENCIA
               , RESPONSABLE
               , SUCURSAL_RESPONSABLE
               , VALOR
               , ESTADO
               , FECHAADQUISICION
               , FECHAENTRADA
               , ORIGEN
               , NUMEROORIGEN
               , FECHAULTMOV
               , TIPOMOVIMIENTOI
               , MOVIMIENTOI
               , MARCA
               , SERIEDEVOLUTIVO
               , PLACA
               , COSTOAJUSTADO
               , CUANTIAMIN';

    MI_VALORES:='SELECT D_MOVIMIENTO.COMPANIA
                        ,D_MOVIMIENTO.ELEMENTO
                        ,D_MOVIMIENTO.SERIE
                        ,MOVIMIENTO.DEPENDENCIA
                        ,(SELECT MIN(RESPONSABLE) KEEP (DENSE_RANK FIRST ORDER BY ROWNUM)
                          FROM   DEPENDENCIA_RESPONSABLE
                          WHERE  RESPONSABLEALMACEN = -1
                            AND  DEPENDENCIA        = MOVIMIENTO.DEPENDENCIA) RESPONS
                        ,(SELECT SUCURSAL
                          FROM   RESPONSABLE
                          WHERE  CEDULA =(SELECT  MIN(RESPONSABLE) KEEP (DENSE_RANK FIRST ORDER BY ROWNUM)
                                           FROM   DEPENDENCIA_RESPONSABLE
                                           WHERE  RESPONSABLEALMACEN = -1
                                             AND  DEPENDENCIA        = MOVIMIENTO.DEPENDENCIA)) SUCURSAL_RESPON
                        ,D_MOVIMIENTO.VALORTOTAL
                        ,D_MOVIMIENTO.ESTADO
                        ,D_MOVIMIENTO.FECHA
                        ,D_MOVIMIENTO.FECHA
                        ,MOVIMIENTO.TIPOMOVASOCIADO
                        ,MOVIMIENTO.MOVASOCIADO
                        ,D_MOVIMIENTO.FECHA
                        ,D_MOVIMIENTO.TIPOMOVIMIENTO
                        ,D_MOVIMIENTO.MOVIMIENTO
                        ,D_MOVIMIENTO.MARCA
                        ,D_MOVIMIENTO.SERIEDEVOLUTIVO
                        ,D_MOVIMIENTO.IDENTIFICADOR
                        ,D_MOVIMIENTO.VALORUNITARIO
                        ,0 EXPR1
                 FROM    MOVIMIENTO
                  INNER JOIN INVENTARIO
                    INNER JOIN TIPOMOVIMIENTO
                      INNER JOIN D_MOVIMIENTO
                        LEFT JOIN DEVOLUTIVO
                          ON  D_MOVIMIENTO.SERIE   = DEVOLUTIVO.SERIE
                         AND D_MOVIMIENTO.ELEMENTO = DEVOLUTIVO.ELEMENTO
                         AND D_MOVIMIENTO.COMPANIA = DEVOLUTIVO.COMPANIA
                      ON  TIPOMOVIMIENTO.CODIGO   = D_MOVIMIENTO.TIPOMOVIMIENTO
                      AND TIPOMOVIMIENTO.COMPANIA = D_MOVIMIENTO.COMPANIA
                    ON  INVENTARIO.COMPANIA       = D_MOVIMIENTO.COMPANIA
                    AND INVENTARIO.CODIGOELEMENTO = D_MOVIMIENTO.ELEMENTO
                  ON  MOVIMIENTO.COMPANIA       = D_MOVIMIENTO.COMPANIA
                  AND MOVIMIENTO.TIPOMOVIMIENTO = D_MOVIMIENTO.TIPOMOVIMIENTO
                  AND MOVIMIENTO.NUMERO         = D_MOVIMIENTO.MOVIMIENTO
                  AND TIPOMOVIMIENTO.COMPANIA   = MOVIMIENTO.COMPANIA
                  AND TIPOMOVIMIENTO.CODIGO     = MOVIMIENTO.TIPOMOVIMIENTO
                  AND MOVIMIENTO.COMPANIA       = D_MOVIMIENTO.COMPANIA
                 WHERE TIPOMOVIMIENTO.CLASE    = ''E''
                   AND TIPOMOVIMIENTO.CONCEPTO = ''C''
                   AND DEVOLUTIVO.SERIE        IS NULL
                   AND INVENTARIO.TIPO         = ''D''
                   AND D_MOVIMIENTO.IND_REG    = -1';

    MI_RETORNO :=PCK_DATOS.FC_ACME(UN_TABLA => 'DEVOLUTIVO'
                                   ,UN_ACCION  => 'IS'
                                   ,UN_CAMPOS => MI_CAMPOS
                                   ,UN_VALORES => MI_VALORES);

    FOR MI_RS IN (SELECT D_MOVIMIENTO.COMPANIA
                         ,D_MOVIMIENTO.ELEMENTO
                         ,D_MOVIMIENTO.SERIE
                         --,MOVIMIENTO.DEPENDENCIA
                         --,(SELECT MIN(RESPONSABLE) KEEP (DENSE_RANK FIRST ORDER BY ROWNUM) FROM DEPENDENCIA_RESPONSABLE WHERE RESPONSABLEALMACEN = -1 AND DEPENDENCIA = MOVIMIENTO.DEPENDENCIA) RESPONS
                         ,D_MOVIMIENTO.VALORTOTAL
                         ,D_MOVIMIENTO.ESTADO
                         ,D_MOVIMIENTO.FECHA FECHAADQUI
                         ,MOVIMIENTO.TIPOMOVASOCIADO
                         ,MOVIMIENTO.MOVASOCIADO
                         ,D_MOVIMIENTO.FECHA
                         ,D_MOVIMIENTO.TIPOMOVIMIENTO
                         ,D_MOVIMIENTO.MOVIMIENTO
                         ,D_MOVIMIENTO.MARCA
                         ,D_MOVIMIENTO.SERIEDEVOLUTIVO
                         ,D_MOVIMIENTO.IDENTIFICADOR
                         ,D_MOVIMIENTO.VALORUNITARIO
                         ,'' EXPR1
                  FROM   MOVIMIENTO
                  INNER JOIN INVENTARIO
                    INNER JOIN TIPOMOVIMIENTO
                         INNER JOIN D_MOVIMIENTO
                            LEFT JOIN DEVOLUTIVO
                              ON  D_MOVIMIENTO.SERIE    = DEVOLUTIVO.SERIE
                              AND D_MOVIMIENTO.ELEMENTO = DEVOLUTIVO.ELEMENTO
                              AND D_MOVIMIENTO.COMPANIA = DEVOLUTIVO.COMPANIA
                          ON  TIPOMOVIMIENTO.CODIGO   = D_MOVIMIENTO.TIPOMOVIMIENTO
                          AND TIPOMOVIMIENTO.COMPANIA = D_MOVIMIENTO.COMPANIA
                      ON  INVENTARIO.COMPANIA       = D_MOVIMIENTO.COMPANIA
                      AND INVENTARIO.CODIGOELEMENTO = D_MOVIMIENTO.ELEMENTO
                    ON  MOVIMIENTO.COMPANIA       = D_MOVIMIENTO.COMPANIA
                    AND MOVIMIENTO.TIPOMOVIMIENTO = D_MOVIMIENTO.TIPOMOVIMIENTO
                    AND MOVIMIENTO.NUMERO         = D_MOVIMIENTO.MOVIMIENTO
                    AND TIPOMOVIMIENTO.COMPANIA   = MOVIMIENTO.COMPANIA
                    AND TIPOMOVIMIENTO.CODIGO     = MOVIMIENTO.TIPOMOVIMIENTO
                    AND MOVIMIENTO.COMPANIA       = D_MOVIMIENTO.COMPANIA
              WHERE D_MOVIMIENTO.COMPANIA = UN_COMPANIA
                AND DEVOLUTIVO.SERIE      IS NULL
                AND TIPOMOVIMIENTO.CLASE  = 'E'
                AND INVENTARIO.TIPO       = 'D'
                AND D_MOVIMIENTO.IND_REG  = -1)
    LOOP
      MI_RTA:=MI_RTA||MI_TOTAL|| '-- Elemento==> '||MI_RS.ELEMENTO||' Placa==> '||MI_RS.SERIE||' Fecha Adquisición: '||MI_RS.FECHAADQUI||CHR(10);

      MI_CAMPOS2:='COMPANIA
                   ,ELEMENTO
                   ,SERIE
                   ,PLACA
                   ,VALOR
                   ,FECHAENTRADA
                   ,FECHAADQUISICION
                   ,FECHA
                   ,HORA
                   ,DESCRIPCION
                   ,DEPENDENCIA
                   ,RESPONSABLE
                   ,SUCURSAL_RESPONSABLE
                   ,CUANTIAMIN';

      MI_VALORES2:='SELECT D_MOVIMIENTO.COMPANIA
                           ,D_MOVIMIENTO.ELEMENTO
                           ,D_MOVIMIENTO.SERIE
                           ,NVL(INVENTARIO.IDENTIFICADOR,'''') IDEN
                           ,0 EXP1
                           ,D_MOVIMIENTO.FECHA
                           ,D_MOVIMIENTO.FECHA
                           ,D_MOVIMIENTO.FECHA
                           ,D_MOVIMIENTO.HORA
                           ,D_MOVIMIENTO.ESPECIFICACION
                           ,PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => '''||UN_COMPANIA||'''
                                                  ,UN_NOMBRE    =>''BODEGA ALMACEN''
                                                  ,UN_MODULO    => 10
                                                  ,UN_FECHA_PAR => SYSDATE) DEPENDENCIA
                           ,PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => '''||UN_COMPANIA||'''
                                                  ,UN_NOMBRE    => ''CEDULA RESPONSABLE ALMACEN''
                                                  ,UN_MODULO    => 10
                                                  ,UN_FECHA_PAR => SYSDATE) RESPONSABLE
                           ,(SELECT SUCURSAL
                             FROM   RESPONSABLE
                             WHERE  CEDULA = PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => '''||UN_COMPANIA||'''
                                                                   ,UN_NOMBRE    =>''CEDULA RESPONSABLE ALMACEN''
                                                                   ,UN_MODULO    => 10
                                                                   ,UN_FECHA_PAR => SYSDATE))
                           ,-1 EXP2
                    FROM   D_MOVIMIENTO
                      INNER JOIN TIPOMOVIMIENTO
                        ON D_MOVIMIENTO.COMPANIA        = TIPOMOVIMIENTO.COMPANIA
                        AND D_MOVIMIENTO.TIPOMOVIMIENTO = TIPOMOVIMIENTO.CODIGO
                      LEFT JOIN DEVOLUTIVO
                        ON  D_MOVIMIENTO.COMPANIA = DEVOLUTIVO.COMPANIA
                        AND D_MOVIMIENTO.ELEMENTO = DEVOLUTIVO.ELEMENTO
                        AND D_MOVIMIENTO.SERIE    = DEVOLUTIVO.SERIE
                      INNER JOIN INVENTARIO
                        ON  D_MOVIMIENTO.COMPANIA = INVENTARIO.COMPANIA
                        AND D_MOVIMIENTO.ELEMENTO = INVENTARIO.CODIGOELEMENTO
                    WHERE D_MOVIMIENTO.COMPANIA =   '''||UN_COMPANIA||'''
                      AND D_MOVIMIENTO.FECHA    BETWEEN TO_DATE('''||MI_RS.FECHAADQUI||''', ''dd/MM/yyyy'') AND SYSDATE
                      AND D_MOVIMIENTO.ELEMENTO BETWEEN '''||MI_RS.ELEMENTO||''' AND '''||MI_RS.ELEMENTO||'''
                      AND D_MOVIMIENTO.SERIE    = '||MI_RS.SERIE||'
                      AND DEVOLUTIVO.COMPANIA   IS NULL
                      AND INVENTARIO.TIPO       NOT IN (''C'')
                      AND D_MOVIMIENTO.IND_REG  NOT IN (0)';

      MI_RETORNO :=PCK_DATOS.FC_ACME(UN_TABLA    => 'DEVOLUTIVO'
                                     ,UN_ACCION  => 'IS'
                                     ,UN_CAMPOS  => MI_CAMPOS2
                                     ,UN_VALORES => MI_VALORES2);

      IF MI_RETORNO<>0 THEN
        MI_RTA:=MI_RTA||'Placa Revisada: SI'||CHR(10);
      ELSE
        MI_RTA:=MI_RTA||'Placa Revisada: NO'||CHR(10);
      END IF;

      MI_TOTAL:=MI_TOTAL+1;
    END LOOP;

    MI_RETORNO :=PCK_DATOS.FC_ACME(UN_TABLA        => 'DEVOLUTIVO'
                                   ,UN_ACCION      => 'M'
                                   ,UN_CAMPOS      => 'ESTADO=''B'''
                                   ,UN_MERGEUSING  => 'ESTADO IS NULL'
                                   ,UN_MERGEENLACE => NULL
                                   ,UN_MERGEEXISTE => NULL
                                   ,UN_MERGENOEXIS => NULL
                                   ,UN_LLAVE       => NULL);

    MI_RTA:=MI_RTA||CHR(10);
    MI_RTA:=MI_RTA||'Total registros Afectados: '||(MI_TOTAL-1)||CHR(10);
    MI_RTA:=MI_RTA||CHR(10);
    MI_RTA:=MI_RTA||'********************************************Fin De Informe**********************************************'||CHR(10)||CHR(10);
    MI_RTA:=MI_RTA||'Se debe Ejecutar Proceso de revisión de placas para los elementos y placas relacionadas desde fecha relacionada';

  END IF;

  RETURN MI_RTA;
  EXCEPTION WHEN OTHERS THEN
    PCK_DATOS.GL_ERROR_MSG := 'Error al verificar placas';
    PCK_DATOS.GL_ERROR_MSG := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  '','',SQLERRM );
    RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );

  END FC_VERIFICAR_PLACA;

FUNCTION FC_GENCONSECUTIVOPOLIZAS
/*
    NAME              : FC_GENCONSECUTIVOPOLIZAS  --> EN ACCESS GENCONSECUTIVOPOLIZAS
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO FERNANDO MALDONADO MORALES
    DATE MIGRADOR     : 27/11/2015
    TIME              : 04:14 PM
    SOURCE MODULE     : SysmanAl2015.10.01.accdb
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED     : 13/01/2017
    TIME              : 8:00 AM
    DESCRIPTION       : FUNCION QUE GENERA EL CONSECUTIVO PARA UNA POLIZA
    MODIFICATIONS     : Se cambió el estándar de codificación
                        y se agrego manejo de excepciones.

  */
  (
    UN_NOMBRETABLA          IN VARCHAR2,
    UN_CONDICION            IN VARCHAR2,
    UN_NOMBRECAMPO          IN VARCHAR2,
    UN_INICIAL              IN VARCHAR2
  )
  RETURN VARCHAR2
  AS
    MI_DIGITOS                NUMBER;
    MI_CEROS                  BOOLEAN;
    MI_SQL                    VARCHAR2(3000 CHAR);
    MI_ANIO_RESULT            NUMBER;
    MI_GENCONSECUTIVOPOLIZAS  VARCHAR2(3000 CHAR);
    MI_RS                     SYS_REFCURSOR;
    MI_CONDICION              VARCHAR2(3000 CHAR);
    RS_NOMBRECAMPO 					  VARCHAR2(3000 CHAR);
    MI_INICIAL                VARCHAR2(3000 CHAR);
BEGIN
  BEGIN
    BEGIN
    IF(UN_CONDICION IS NOT NULL) THEN
        MI_CONDICION:= ' WHERE ' || UN_CONDICION;
    END IF;

    MI_SQL:='SELECT MAX(TO_NUMBER('   || UN_NOMBRECAMPO || '))' ||
            'FROM   '   || UN_NOMBRETABLA ||
             MI_CONDICION  ||
            'ORDER BY ' || UN_NOMBRECAMPO || ' DESC ';
    EXECUTE IMMEDIATE MI_SQL INTO MI_INICIAL;

    EXCEPTION WHEN NO_DATA_FOUND THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;

    END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERR_ALMACEN_GENCONSPOLIZA
      );
  END;
    IF MI_INICIAL IS NULL THEN
        MI_INICIAL := UN_INICIAL;
        MI_DIGITOS := LENGTH(MI_INICIAL);
        IF MI_INICIAL <> NULL THEN
            IF (SUBSTR(UN_INICIAL, 1, 1)= 0) THEN
                MI_CEROS := TRUE;
            ELSE
                MI_CEROS:= FALSE;
            END IF;
        END IF;
        IF MI_INICIAL IS NULL THEN
            MI_GENCONSECUTIVOPOLIZAS:= 1;
        ELSE
            MI_GENCONSECUTIVOPOLIZAS:=UN_INICIAL;
        END IF;
    ELSE
        MI_ANIO_RESULT:= TO_NUMBER(SUBSTR(NVL(MI_INICIAL, TO_CHAR(SYSDATE, 'YYYY')), 1, 4));
        --IF MI_ANIO_RESULT NOT IN TO_CHAR(SYSDATE,'YYYY') THEN
            MI_DIGITOS := LENGTH(MI_INICIAL);
            MI_GENCONSECUTIVOPOLIZAS := TO_NUMBER(MI_INICIAL) + 1;
            IF SUBSTR(MI_INICIAL,1,1) = 0 THEN
                MI_CEROS := TRUE;
            ELSE
                MI_CEROS := FALSE;
            END IF;
        --ELSE
            --MI_DIGITOS := LENGTH(MI_INICIAL);
            --MI_GENCONSECUTIVOPOLIZAS := TO_NUMBER(MI_INICIAL) + 1;
            --IF SUBSTR(MI_INICIAL,1,1) = 0 THEN
            --    MI_CEROS := TRUE;
            --ELSE
            --    MI_CEROS := FALSE;
            --END IF;
        --END IF;
    END IF;


    IF MI_CEROS = TRUE THEN
        MI_GENCONSECUTIVOPOLIZAS:= PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO    => MI_GENCONSECUTIVOPOLIZAS
                                                             ,UN_LONGITUD => MI_DIGITOS);
    END IF;

  RETURN MI_GENCONSECUTIVOPOLIZAS;

END FC_GENCONSECUTIVOPOLIZAS;


FUNCTION FC_GUARDARPOLIZA
  /*
    NAME              : FC_GUARDARPOLIZA  --> EN ACCESS GUARDAR_CLICK()
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO FERNANDO MALDONADO MORALES
    DATE MIGRADOR     : 01/12/2015
    TIME              : 10:00 AM
    SOURCE MODULE     : SysmanAl2015.10.01.accdb
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED     : 13/01/2017
    TIME              : 9:05 AM
    DESCRIPTION       : FUNCION QUE GUARDA EL DETALLE DE LA PÓLIZA EN LA TABLA D_POLIZAS_ACTIVOS
    MODIFICATIONS     : Se cambió el estándar de codificación
                        y se agrego manejo de excepciones.


  */
  (
    UN_COMPANIA           IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_ASEGURADORA        IN  VARCHAR2,
    UN_NUMPOLIZA          IN  VARCHAR2,
    UN_SUCURSAL           IN  VARCHAR2,
    UN_FECHAI             IN  DATE,
    UN_FECHAF             IN  DATE,
    UN_GRUPO              IN  VARCHAR2,
    UN_ELEMENTO           IN  VARCHAR2,
    UN_PLACA              IN  VARCHAR2,
    UN_RIESGO             IN  VARCHAR2,
    UN_USUARIO            IN  PCK_SUBTIPOS.TI_USUARIO
  )
  RETURN NUMBER
  AS
  MI_STRSQL           PCK_SUBTIPOS.TI_STRSQL;
  MI_STRSQL1          PCK_SUBTIPOS.TI_STRSQL;
  MI_DEVOLUTIVO       VARCHAR2(20 CHAR);
  MI_RS               SYS_REFCURSOR;
  MI_LARGO            VARCHAR2(32000 CHAR);
  MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
  MI_FILTRO           VARCHAR2(30 CHAR);
  MI_NOTFOUND         BOOLEAN;
  RS_ELEMENTO         VARCHAR2(30 CHAR);
  RS_SERIE            VARCHAR2(30 CHAR);
  RS_VALOR_HISTORICO  PCK_SUBTIPOS.TI_DOBLE;
  MI_EXISTE           PCK_SUBTIPOS.TI_ENTERO;
  MI_VALOR_POLIZA     PCK_SUBTIPOS.TI_DOBLE;
  MI_VALOR_DPOLIZA    PCK_SUBTIPOS.TI_DOBLE;
BEGIN
  MI_NOTFOUND := FALSE;

  IF UN_PLACA IS NOT NULL  THEN
          MI_DEVOLUTIVO := UN_PLACA;
          MI_FILTRO := 'SERIE';
  ELSIF UN_ELEMENTO IS NOT NULL  THEN
          MI_DEVOLUTIVO := UN_ELEMENTO;
          MI_FILTRO := 'ELEMENTO';
  ELSIF UN_GRUPO IS NOT NULL THEN
          MI_DEVOLUTIVO := UN_GRUPO;
          MI_FILTRO := 'ELEMENTO';
  ELSE
          MI_DEVOLUTIVO := NULL;
  END IF;

  IF MI_DEVOLUTIVO IS NULL THEN
      RETURN 0;
  END IF;

  MI_STRSQL :=  ' SELECT ELEMENTO, SERIE, VALOR '
              ||' FROM DEVOLUTIVO '
              ||' WHERE COMPANIA      = '''||UN_COMPANIA||''''
              ||'   AND '||MI_FILTRO||' LIKE '''|| MI_DEVOLUTIVO ||'%''';

  OPEN MI_RS FOR MI_STRSQL;
  LOOP
    FETCH MI_RS INTO RS_ELEMENTO, RS_SERIE, RS_VALOR_HISTORICO;
    EXIT WHEN MI_RS%NOTFOUND;
    MI_NOTFOUND := TRUE;
    BEGIN
      BEGIN
        MI_STRSQL1 :=   ' SELECT NOMBRELARGO'
                      ||' FROM INVENTARIO'
                      ||' WHERE COMPANIA     = '''||UN_COMPANIA||''''
                      ||'   AND CODIGOELEMENTO = '''||RS_ELEMENTO||'''';
        EXECUTE IMMEDIATE MI_STRSQL1 INTO MI_LARGO;
      EXCEPTION WHEN NO_DATA_FOUND THEN
          RAISE PCK_EXCEPCIONES.EXC_ALMACEN;

      END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD =>SQLCODE,
          UN_ERROR_COD=>PCK_ERRORES.ERR_ALMACEN_NODATA
        );
    END;
    BEGIN
        SELECT COUNT(COMPANIA) TOTAL
        INTO MI_EXISTE
        FROM D_POLIZAS_ACTIVOS
        WHERE COMPANIA      = UN_COMPANIA
          AND ASEGURADORA   = UN_ASEGURADORA
          AND SUCURSAL      = UN_SUCURSAL
          AND NUMERO_POLIZA = UN_NUMPOLIZA
          AND ELEMENTO      = NVL(UN_ELEMENTO,RS_ELEMENTO)
          AND SERIE         = NVL(UN_PLACA,RS_SERIE);

        IF MI_EXISTE =0 THEN
          BEGIN
          MI_CAMPOS := 'COMPANIA
                       ,ASEGURADORA
                       ,SUCURSAL
                       ,NUMERO_POLIZA
                       ,ELEMENTO,SERIE
                       ,GRUPO
                       ,FECHAI
                       ,FECHAF
                       ,NOMBRE_ELEMENTO
                       ,RIESGO
                       ,VALOR_HISTORICO
                       ,DATE_CREATED
                       ,CREATED_BY';
          MI_VALORES := ''''||UN_COMPANIA||'''
                        ,'''||UN_ASEGURADORA||'''
                        ,'''||UN_SUCURSAL||'''
                        ,'''||UN_NUMPOLIZA||'''
                        ,'''||NVL(UN_ELEMENTO,RS_ELEMENTO)||'''
                        ,'||NVL(UN_PLACA,RS_SERIE)||'
                        ,'''||UN_GRUPO||'''
                        ,'||PCK_SYSMAN_UTL.FC_SDATE(UN_FECHAI)||'
                        ,'||PCK_SYSMAN_UTL.FC_SDATE(UN_FECHAF)||'
                        ,'''||MI_LARGO||'''
                        ,'''||UN_RIESGO||'''
                        ,'||NVL(RS_VALOR_HISTORICO,0)||'
                        ,SYSDATE
                        ,'''||UN_USUARIO||'''';

          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => 'D_POLIZAS_ACTIVOS'
                                                ,UN_ACCION  => 'I'
                                                ,UN_CAMPOS  => MI_CAMPOS
                                                ,UN_VALORES => MI_VALORES);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
        END;
      END IF;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD =>SQLCODE
          ,UN_ERROR_COD=>PCK_ERRORES.ERRR_ALMACEN_INSERT_DPOLIZA
       );
    END;
  END LOOP;
  CLOSE MI_RS;

  IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
      , UN_NOMBRE    => 'CONTROLA POLIZAS DE DEVOLUTIVOS POR VALOR'
      , UN_MODULO    => -1
      , UN_FECHA_PAR => SYSDATE),'NO') = 'SI' 
    THEN
    BEGIN
      BEGIN
        SELECT VALOR 
          INTO MI_VALOR_POLIZA
          FROM POLIZAS_ACTIVOS
         WHERE COMPANIA = UN_COMPANIA
           AND ASEGURADORA = UN_ASEGURADORA
           AND SUCURSAL = SUCURSAL
           AND NUMERO_POLIZA = UN_NUMPOLIZA;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
      END;

      BEGIN
        SELECT NVL(SUM(VALOR_HISTORICO),0) TOTAL_D_POLIZAS   
          INTO MI_VALOR_DPOLIZA
          FROM D_POLIZAS_ACTIVOS   
         WHERE COMPANIA = UN_COMPANIA
           AND ASEGURADORA = UN_ASEGURADORA
           AND NUMERO_POLIZA = UN_NUMPOLIZA;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
      END;
      
      IF MI_VALOR_DPOLIZA > MI_VALOR_POLIZA THEN
        RAISE_APPLICATION_ERROR(-20001, 'Los Amparos exceden el valor de la póliza.');
      END IF;
    END;
  END IF;  

  IF MI_NOTFOUND = FALSE THEN
      RETURN 0;
  ELSE
      RETURN -1;
  END IF;
      RETURN 0;
END FC_GUARDARPOLIZA;

FUNCTION FC_ACTUALIZAELEMENTOINVENTARIO
  /*
      NAME              : FC_ACTUALIZAELEMENTOINVENTARIO
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : LEYDI MILENA CORTÉS FORERO
      DATE MIGRADOR     : 13/01/2016
      TIME              : 3:22 PM
      SOURCE MODULE     : Almacén
      MODIFIER          : ELKIN GEOVANNY AMAYA SILVA
      DATE MODIFIED     : 13/01/2017
      TIME              : 10:07 AM
      DESCRIPTION       : PERMITE ACTUALIZAR EL CODIGOELEMENTO DE LA TABLA INVENTARIO POR UN CÓDIGO NUEVO Y ELIMINAR EL CÓDIGO ANTIGIO.
                          SE ACTUALIZA EN LAS TABLAS D_MOVIMIENTO, D_ORDENDECOMPRA, D_ORDENDESUMINISTRO, DETALLE_PLAN_COMPRAS, PROPUESTA_DETALLE,
                          ALMACENCONTABILIDAD, ACUMULADO y PROVEEDOR.
      MODIFICATIONS     : Se cambió el estándar de codificación
                          y se agrego manejo de excepciones.


    */
   (
    UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CODIGOANT          IN VARCHAR2,
    UN_CODIGONUEVO        IN VARCHAR2,
    UN_TIPOCAMBIO         IN VARCHAR2
   ) RETURN VARCHAR2
   AS
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
    MI_CODNUEVO           NUMBER(2);
    MI_VAL                VARCHAR2(32000 CHAR);
    MI_PCKDATOS           VARCHAR2(300 CHAR);
    MI_CAMPO              VARCHAR2(32000 CHAR);
    MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
    MI_RTA                VARCHAR2(3200 CHAR);
    MI_INDMOV             NUMBER(2);

   BEGIN
      BEGIN

       --Se elimina Validacion según solicitud de Juan Carlos Forero tar 1000090775 teniendo en cuenta que por configuración pueden haber elemento del grupo 2 configurados como consumo y por eso se requiere el cambio
        /*IF SUBSTR(UN_CODIGOANT, 1,1) <> SUBSTR(UN_CODIGONUEVO, 1,1) Then
            MI_RTA := 'Los cambios de código son viables únicamente con elementos del mismo Tipo';
            RETURN MI_RTA;
        END IF;*/

        SELECT TIENEMOVIMIENTO
        INTO   MI_INDMOV
        FROM   INVENTARIO
        WHERE  COMPANIA       = UN_COMPANIA
          AND  CODIGOELEMENTO = UN_CODIGOANT;

      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_RTA := 'El elemento a cambiar ' ||UN_CODIGOANT||' no existe en el inventario. ';
        RETURN MI_RTA;
      END;

     IF UN_TIPOCAMBIO = 'C' THEN
         IF MI_INDMOV = 0 THEN
              MI_RTA:='EL ELEMENTO ' ||UN_CODIGOANT||' NO TIENE MOVIMIENTO';
              RETURN MI_RTA;
         END IF;

         SELECT COUNT(CODIGOELEMENTO)
         INTO   MI_CODNUEVO
         FROM   INVENTARIO
         WHERE  CODIGOELEMENTO = UN_CODIGONUEVO;

         IF MI_CODNUEVO = 0 THEN
           BEGIN
            BEGIN
              -- INSERTA EN NUEVO CÓDIGO CON LOS VALORES DEL CÓDIGO ANTIGUO EN LA TABLA INVENTARIO
              MI_CAMPOS  :=  PCK_SYSMAN_UTL.FC_LISTA_CAMPOS('INVENTARIO','PREDECESOR');

              MI_VALORES := REPLACE(MI_CAMPOS, 'CODIGOELEMENTO',  UN_CODIGONUEVO);

              MI_VALORES := REPLACE(MI_VALORES, 'CODIGOANTERIOR', UN_CODIGOANT);

              MI_VALORES := 'SELECT ' || MI_VALORES ||
                           ' FROM  INVENTARIO' || ' ' ||
                           ' WHERE COMPANIA       = (''' || UN_COMPANIA ||  ''')' ||
                           '   AND CODIGOELEMENTO = (' || UN_CODIGOANT ||  ')' || '';
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => 'INVENTARIO'
                                                    ,UN_ACCION  => 'IS'
                                                    ,UN_CAMPOS  =>  MI_CAMPOS
                                                    ,UN_VALORES =>  MI_VALORES);

              -- ACTUALIZA TABLA D_MOVIMIENTO
              MI_CAMPO      := 'ELEMENTO =  ' || '''' ||  UN_CODIGONUEVO || '''';

              MI_CONDICION := 'COMPANIA = (''' || UN_COMPANIA ||  ''')' ||
                       '   AND ELEMENTO = (' || UN_CODIGOANT ||  ')' || '';

              MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA      => 'D_MOVIMIENTO'
                                                ,UN_ACCION    => 'M'
                                                ,UN_CAMPOS    =>  MI_CAMPO
                                                ,UN_CONDICION =>  MI_CONDICION);

               -- ACTUALIZA TABLA D_ORDENDECOMPRA
              MI_CAMPO      := 'ELEMENTO =  ' || '''' ||  UN_CODIGONUEVO || '''';

              MI_CONDICION := 'COMPANIA = (''' || UN_COMPANIA ||  ''')' ||
                           'AND ELEMENTO = (' || UN_CODIGOANT ||  ')' || '';

              MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA      => 'D_ORDENDECOMPRA'
                                                ,UN_ACCION    => 'M'
                                                ,UN_CAMPOS    => MI_CAMPO
                                                ,UN_CONDICION => MI_CONDICION);

               -- ACTUALIZA TABLA D_ORDENDESUMINISTRO
              MI_CAMPO      := 'ELEMENTO =  ' || '''' ||  UN_CODIGONUEVO || '''';

              MI_CONDICION := 'COMPANIA = (''' || UN_COMPANIA ||  ''')' ||
                       '   AND ELEMENTO = (' || UN_CODIGOANT ||  ')' || '';

              MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA      => 'D_ORDENDESUMINISTRO'
                                                ,UN_ACCION    =>'M'
                                                ,UN_CAMPOS    => MI_CAMPO
                                                ,UN_CONDICION => MI_CONDICION);

               -- ACTUALIZA TABLA DETALLE_PLAN_COMPRAS
              MI_CAMPO      := 'CODIGO =  ' || '''' ||  UN_CODIGONUEVO || '''';

              MI_CONDICION := 'COMPANIA = (''' || UN_COMPANIA ||  ''')' ||
                       '   AND CODIGO   = (' || UN_CODIGOANT ||  ')' || '';

              MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA      => 'DETALLE_PLAN_COMPRAS'
                                                ,UN_ACCION    => 'M'
                                                ,UN_CAMPOS    => MI_CAMPO
                                                ,UN_CONDICION => MI_CONDICION);

               -- ACTUALIZA TABLA PROPUESTA_DETALLE
              MI_CAMPO      := 'ELEMENTO =  ' || '''' ||  UN_CODIGONUEVO || '''';

              MI_CONDICION := 'COMPANIA = (''' || UN_COMPANIA ||  ''')' ||
                       '   AND ELEMENTO = (' || UN_CODIGOANT ||  ')' || '';

              MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA      => 'PROPUESTA_DETALLE'
                                                ,UN_ACCION    => 'M'
                                                ,UN_CAMPOS    => MI_CAMPO
                                                ,UN_CONDICION => MI_CONDICION);

               -- ACTUALIZA TABLA ALMACENCONTABILIDAD
              MI_CAMPO      := 'CODIGOELEMENTO =  ' || '''' ||  UN_CODIGONUEVO || '''';

              MI_CONDICION := 'COMPANIA       = (''' || UN_COMPANIA ||  ''')' ||
                           'AND CODIGOELEMENTO = (' || UN_CODIGOANT ||  ')' || '';

              MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA      => 'ALMACENCONTABILIDAD'
                                                ,UN_ACCION    => 'M'
                                                ,UN_CAMPOS    => MI_CAMPO
                                                ,UN_CONDICION => MI_CONDICION);

               -- ACTUALIZA TABLA ACUMULADO
              MI_CAMPO      := 'CODIGOELEMENTO =  ' || '''' ||  UN_CODIGONUEVO || '''';

              MI_CONDICION := 'COMPANIA       = (''' || UN_COMPANIA ||  ''')' ||
                       '   AND CODIGOELEMENTO = (' || UN_CODIGOANT ||  ')' || '';

              MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA      => 'ACUMULADO'
                                                ,UN_ACCION    => 'M'
                                                ,UN_CAMPOS    => MI_CAMPO
                                                ,UN_CONDICION => MI_CONDICION);

               -- ACTUALIZA TABLA PROVEEDOR
              MI_CAMPO      := 'ELEMENTO =  ' || '''' ||  UN_CODIGONUEVO || '''';

              MI_CONDICION := 'COMPANIA = (''' || UN_COMPANIA ||  ''')' ||
                       '   AND ELEMENTO = (' || UN_CODIGOANT ||  ')' || '';

              MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA      => 'PROVEEDOR'
                                                ,UN_ACCION    => 'M'
                                                ,UN_CAMPOS    => MI_CAMPO
                                                ,UN_CONDICION => MI_CONDICION);
              MI_RTA:= 'OK';

               -- ELIMINA CODIGO ANTERIOR DE LA TABLA INVENTARIO
              MI_CONDICION := 'COMPANIA       = (''' || UN_COMPANIA ||  ''')' ||
                       '   AND CODIGOELEMENTO = (' || UN_CODIGOANT ||  ')' || '';

              MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA   => 'INVENTARIO'
                                                ,UN_ACCION => 'E'
                                                ,UN_CONDICION => MI_CONDICION);
              MI_RTA:= 'OK';

               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                           WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                           WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
            END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD =>SQLCODE
                    ,UN_ERROR_COD=>PCK_ERRORES.ERRR_ALMACEN_ELEMINV
                    );
           END;
         ELSE
            MI_RTA := 'El código nuevo ya existe. NO se puede realizar el cambio de código deseado. ';
         END IF;
     END IF;
   RETURN MI_RTA;

END FC_ACTUALIZAELEMENTOINVENTARIO;

----

PROCEDURE PR_REVISARAFECTACIONMOV
  /*
   NAME 			      : PR_REVISARAFECTACIONMOV
   AUTHORS 			    : SYSMAN SAS
   AUTHOR MIGRACION	: ADRIANA MARITZA C�?CERES BONILLA
   DATE MIGRADOR	  : 14/01/2016
   TIME				      : 09:14 AM
   MODULO ORIGEN	  : ALMACÉN
   DESCRIPTION		  : Proceso que actualiza la tabla D_MOVIMIENTO
   MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
   DATE MODIFIED	  : 13/01/2017
   TIME				      : 10:51 AM
   MODIFICATIONS	  : Se cambió el estándar de codificación
                      y se agrego manejo de excepciones.

  */
   (
    UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA,  -- Código de la compania
    UN_TIPO                IN VARCHAR2,                  -- Tipo de movimiento
    UN_NUMERO              IN PCK_SUBTIPOS.TI_ENTERO_LARGO     -- Número
   )
   AS
    MI_TABLA             VARCHAR2(200 CHAR);
    MI_CONSULTA          PCK_SUBTIPOS.TI_MERGEUSING;
    MI_ENLACE            PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_EXISTE            PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_RTA               VARCHAR2(32000 CHAR);
BEGIN
  BEGIN

   BEGIN
  MI_TABLA := 'D_MOVIMIENTO';
  MI_CONSULTA := 'SELECT DM.COMPANIA, DM.TIPOMOVIMIENTO, DM.MOVIMIENTO, DM.CODIGO, DM.ELEMENTO, NVL(MOV_AFECT.CANTIDAD,0) CANTIDAD
                    FROM D_MOVIMIENTO DM
                    LEFT JOIN (SELECT COMPANIA, TIPOMOVIMIENTO_AFECT, MOVIMIENTO_AFECT, CODIGO_AFECT, ELEMENTO, SUM(CANTIDAD) CANTIDAD
                    FROM D_MOVIMIENTO
                    WHERE COMPANIA              = ''' || UN_COMPANIA || ''' 
                    AND TIPOMOVIMIENTO_AFECT    = '''|| UN_TIPO ||''' 
                    AND MOVIMIENTO_AFECT        = '||UN_NUMERO||'
                    AND D_MOVIMIENTO.IND_REG    NOT IN (0)
                    GROUP BY COMPANIA, TIPOMOVIMIENTO_AFECT, MOVIMIENTO_AFECT, CODIGO_AFECT, ELEMENTO) MOV_AFECT
                    ON MOV_AFECT.COMPANIA = DM.COMPANIA
                    AND MOV_AFECT.TIPOMOVIMIENTO_AFECT = DM.TIPOMOVIMIENTO
                    AND MOV_AFECT.MOVIMIENTO_AFECT = DM.MOVIMIENTO
                    AND MOV_AFECT.CODIGO_AFECT = DM.CODIGO
                    AND MOV_AFECT.ELEMENTO = DM.ELEMENTO
                    WHERE DM.COMPANIA           = ''' || UN_COMPANIA || ''' 
                    AND DM.TIPOMOVIMIENTO       = '''|| UN_TIPO ||''' 
                    AND DM.MOVIMIENTO           = '||UN_NUMERO||'';
  MI_ENLACE := 'TABLA.COMPANIA = VISTA.COMPANIA
                AND TABLA.TIPOMOVIMIENTO = VISTA.TIPOMOVIMIENTO
                AND TABLA.MOVIMIENTO = VISTA.MOVIMIENTO
                AND TABLA.CODIGO = VISTA.CODIGO
                AND TABLA.ELEMENTO = VISTA.ELEMENTO';
  MI_EXISTE := 'UPDATE SET TABLA.CANTIDADAFECTADA = NVL(VISTA.CANTIDAD,0)
                WHERE NVL(TABLA.CANTIDADAFECTADA, 0) <> NVL(VISTA.CANTIDAD, 0)';
  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA
                               ,UN_ACCION      => 'MM'
                               ,UN_MERGEUSING  => MI_CONSULTA
                               ,UN_MERGEENLACE => MI_ENLACE
                               ,UN_MERGEEXISTE => MI_EXISTE);

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
   RAISE PCK_EXCEPCIONES.EXC_ALMACEN;

   END;

   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
     PCK_ERR_MSG.RAISE_WITH_MSG(
     UN_EXC_COD =>SQLCODE
     ,UN_ERROR_COD=>PCK_ERRORES.ERRR_ALMACEN_MMAFECTACIONMOV
     );
  END;

END PR_REVISARAFECTACIONMOV;

--

PROCEDURE PR_REVERSAREQUISICION
  /*
   NAME 			      : PR_REVERSAREQUISICION
   AUTHORS 			    : SYSMAN SAS
   AUTHOR MIGRACION	: ADRIANA MARITZA C�?CERES BONILLA
   DATE MIGRADOR	  : 14/01/2016
   TIME				      : 03:35 AM
   MODULO ORIGEN	  : ALMACÉN
   DESCRIPTION		  : Proceso para reversar una requisición
   MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
   DATE MODIFIED	  : 13/01/2017
   TIME				      : 11:45 AM
   MODIFICATIONS	  : Se cambió el estándar de codificación
                      y se agrego manejo de excepciones.

  */
  (
  UN_COMPANIA              IN PCK_SUBTIPOS.TI_COMPANIA, -- Código de la compania
  UN_NUMERO                IN PCK_SUBTIPOS.TI_ENTERO_LARGO,   -- Número de la requisición
  UN_USUARIO               IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
  MI_TABLA                 VARCHAR2(200);
  MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;
  MI_RTA                   VARCHAR2(32000);
BEGIN
 BEGIN
   BEGIN
    MI_TABLA     := 'D_ORDENDESUMINISTRO';

    MI_CAMPOS    := 'D_ORDENDESUMINISTRO.IND_REG = 0,
                    DATE_MODIFIED = SYSDATE,
                    MODIFIED_BY   = '''||UN_USUARIO||'''';

    MI_CONDICION := 'D_ORDENDESUMINISTRO.COMPANIA          = ''' || UN_COMPANIA || '''
                 AND D_ORDENDESUMINISTRO.ORDENDESUMINISTRO = ' || UN_NUMERO ||'';

    MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA
                                 ,UN_ACCION    => 'M'
                                 ,UN_CAMPOS    => MI_CAMPOS
                                 ,UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;

   END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
      UN_EXC_COD =>SQLCODE
      ,UN_ERROR_COD=>PCK_ERRORES.ERRR_ALMACEN_REVEREQUISICION
      );
 END;

END PR_REVERSAREQUISICION;

  --9
  PROCEDURE PR_CAMBIARHORAMOVIMIENTO
  /*
    NAME 			        : PR_CAMBIARHORAMOVIMIENTO
    AUTHORS 			    : STEFANINI SYSMAN
    AUTHOR MIGRATION	: ADRIANA MARITZA C�?CERES BONILLA
    DATE MIGRATION	  : 18/01/2016
    TIME				      : 12:41 AM
    MODULO ORIGEN	    : ALMACÉN (10)
    DESCRIPTION		    : Proceso para cambiar la hora de todos los items de un determinado tipo y número de movimiento.
    MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA, ADRIANA MARITZA C�?CERES BONILLA,
                        PABLO ANDRES ESPITIA CUCA (16/08/2017 10:01 AM)
    DATE MODIFIED	    : 13/01/2017
    TIME				      : 12:45 PM
    MODIFICATIONS	    : Se cambió el estándar de codificación y se agrego manejo de excepciones.
                      : Se modifican las validaciones para que se tome el formato de la hora
                      : (16/08/2017 10:01 AM) -> Ajustes segun estandar de programacion en PLSQL.
                                                 Manejo de subtipos.
  */
  (
    UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,         --Código de la compania
    UN_TIPOMOVIMIENTO       IN D_MOVIMIENTO.TIPOMOVIMIENTO%TYPE, --Tipo de movimiento
    UN_NUMERO               IN PCK_SUBTIPOS.TI_ENTERO_LARGO,     --Número de movimiento
    UN_HORANUEVA            IN VARCHAR2,                         --Nueva hora asignada al movimiento
    UN_FORMATOHORA          IN VARCHAR2,                         --Formato de fecha asignado al movimiento
    UN_USUARIO              IN PCK_SUBTIPOS.TI_USUARIO           --USUARIO QUE INGRESO A LA APP
  )
  AS
    MI_RS                   SYS_REFCURSOR;
    MI_ACTHORA              PCK_SUBTIPOS.TI_ENTERO;
    MI_NUEVAHORA            VARCHAR2(3200);
    MI_CAMBIARHORAMOV       BOOLEAN;
    MI_TABLA                VARCHAR2(200);
    MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
    MI_FORMATO              VARCHAR2(3200);
    MI_RTA                  VARCHAR2(3200);
    MI_HORA                 VARCHAR2(3200);
    MI_HORATARDE            NUMBER;
  BEGIN
    MI_CAMBIARHORAMOV := FALSE;
    MI_FORMATO := 'DD/MM/YYYY HH24:MI:SS';

    <<ACTUALIZAR_D_MOVIMIENTO>>
    FOR MI_RS IN (SELECT COMPANIA
                         ,TIPOMOVIMIENTO
                         ,MOVIMIENTO
                         ,CODIGO
                         ,TO_CHAR (HORA,'DD/MM/YYYY ')FECHA
                         ,TO_CHAR (HORA,':MI:SS ') PARTE_HORA
                  FROM  D_MOVIMIENTO
                  WHERE COMPANIA       = UN_COMPANIA
                    AND TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
                    AND MOVIMIENTO     = UN_NUMERO)

    LOOP
     IF UN_FORMATOHORA = 'PM' AND UN_HORANUEVA = '12' THEN
          MI_NUEVAHORA := MI_RS.FECHA||UN_HORANUEVA|| MI_RS.PARTE_HORA   ;

     ELSIF UN_FORMATOHORA = 'AM' AND UN_HORANUEVA = '12' THEN
         MI_HORATARDE := 00;
         MI_NUEVAHORA := MI_RS.FECHA||MI_HORATARDE|| MI_RS.PARTE_HORA   ;

     ELSIF UN_FORMATOHORA = 'PM' THEN
       MI_HORATARDE:= TO_NUMBER(UN_HORANUEVA) +12;

       MI_NUEVAHORA := MI_RS.FECHA||TO_CHAR(MI_HORATARDE) || MI_RS.PARTE_HORA   ;
     ELSE

       MI_NUEVAHORA := MI_RS.FECHA||UN_HORANUEVA|| MI_RS.PARTE_HORA   ;
    END IF;



    MI_TABLA := 'D_MOVIMIENTO';
    --MI_HORA := PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA    => MI_NUEVAHORA
      --                                 ,UN_FORMATO => MI_FORMATO);
    MI_CAMPOS := 'HORA = TO_DATE('''||MI_NUEVAHORA||''', ''DD/MM/YYYY HH24:MI:SS'')
                  ,DATE_MODIFIED = SYSDATE
                  ,MODIFIED_BY   = '''||UN_USUARIO||''' ';

    MI_CONDICION := ' D_MOVIMIENTO.COMPANIA       ='''||UN_COMPANIA||'''
                  AND D_MOVIMIENTO.TIPOMOVIMIENTO ='''||UN_TIPOMOVIMIENTO||'''
                  AND D_MOVIMIENTO.MOVIMIENTO     ='||UN_NUMERO||'
                  AND D_MOVIMIENTO.CODIGO         = '||MI_RS.CODIGO||'';
   BEGIN
    BEGIN

      MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA
                                   ,UN_ACCION    => 'M'
                                   ,UN_CAMPOS    => MI_CAMPOS
                                   ,UN_CONDICION => MI_CONDICION);
      --MI_NUEVAHORA := MI_NUEVAHORA+1;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
    END;
           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
             PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD     => SQLCODE
                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_UPDATE_HORAMOV
                  );
    END;
   END LOOP ACTUALIZAR_D_MOVIMIENTO;


   IF MI_ACTHORA > 1 THEN
      MI_CAMBIARHORAMOV := TRUE;
   END IF;

  END PR_CAMBIARHORAMOVIMIENTO;

-------

PROCEDURE PR_CAMBIARFECHAMOVIMIENTO
   /*
   NAME 			      : PR_CAMBIARFECHAMOVIMIENTO
   AUTHORS 			    : SYSMAN SAS
   AUTHOR MIGRACION	: ADRIANA MARITZA C�?CERES BONILLA
   DATE MIGRADOR	  : 19/01/2016
   TIME				      : 09:39 AM
   MODULO ORIGEN	  : ALMACÉN
   DESCRIPTION		  : Proceso para cambiar la fecha de todos los items de un determinado tipo y número de movimiento.
   MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
   DATE MODIFIED	  : 13/01/2017
   TIME				      : 2:40 PM
   MODIFICATIONS	  : Se cambió el estándar de codificación
                      y se agrego manejo de excepciones.


  */
  (
  UN_COMPANIA              IN PCK_SUBTIPOS.TI_COMPANIA,       -- Código de la compania
  UN_TIPOMOVIMIENTO        IN VARCHAR2,                       -- Tipo de movimiento
  UN_NUMERO                IN PCK_SUBTIPOS.TI_ENTERO_LARGO,   -- Número
  UN_FECHAACTUAL           IN DATE,                            -- Nueva fecha que va a tomar el tipo de movimiento
  UN_USUARIO               IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
  MI_TABLA                 VARCHAR2(200 CHAR);
  MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;
  MI_FECHA                 VARCHAR2(200 CHAR);
  MI_RTA                   VARCHAR2(3200 CHAR);
  MI_RTA1                  VARCHAR2(3200 CHAR);
  MI_RTA2                  VARCHAR2(3200 CHAR);
  MI_CAMBIARFECHAMOV       BOOLEAN;
  MI_RS                    SYS_REFCURSOR;
  MI_RSDM                  SYS_REFCURSOR;
  MI_GENERAPLACA           PCK_SUBTIPOS.TI_ENTERO;
  MI_CODIGO                VARCHAR2(200 CHAR);
  MI_TIPOELEMENTO          VARCHAR2(200 CHAR);
  MI_CLASE                 VARCHAR2(200 CHAR);

BEGIN
 BEGIN
  BEGIN

    MI_FECHA:='TO_DATE(''' || TO_CHAR(UN_FECHAACTUAL, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')';

    MI_TABLA   := 'MOVIMIENTO';
    MI_CAMPOS  := 'FECHA = '||MI_FECHA||'
                   ,DATE_MODIFIED = SYSDATE
                   ,MODIFIED_BY   = '''||UN_USUARIO||''' ';
    MI_CONDICION := 'MOVIMIENTO.COMPANIA       ='''||UN_COMPANIA||'''
                 AND MOVIMIENTO.TIPOMOVIMIENTO ='''||UN_TIPOMOVIMIENTO||'''
                 AND MOVIMIENTO.NUMERO         ='''||UN_NUMERO||'''';

     MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA
                                  ,UN_ACCION    => 'M'
                                  ,UN_CAMPOS    => MI_CAMPOS
                                  ,UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
  END;
       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
         PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD     => SQLCODE
                ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_UPDATE_FECHAMOV
              );
 END;

   IF MI_RTA = 1 THEN
       BEGIN
         BEGIN
            MI_TABLA  := 'D_MOVIMIENTO';

            MI_CAMPOS := 'FECHA = '||MI_FECHA||'
                   ,DATE_MODIFIED = SYSDATE
                   ,MODIFIED_BY   = '''||UN_USUARIO||''' ';

            MI_CONDICION := 'D_MOVIMIENTO.COMPANIA       ='''||UN_COMPANIA||'''
                         AND D_MOVIMIENTO.TIPOMOVIMIENTO ='''||UN_TIPOMOVIMIENTO||'''
                         AND D_MOVIMIENTO.MOVIMIENTO     = '||UN_NUMERO||'';

            MI_RTA1 := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA
                                          ,UN_ACCION    => 'M'
                                          ,UN_CAMPOS    => MI_CAMPOS
                                          ,UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
         END;
             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
               PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD     => SQLCODE
                      ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_UPDATE_FECHAMOV
                    );
       END;

      IF MI_RTA1 = 1 THEN
         MI_CAMBIARFECHAMOV := TRUE;
      END IF;
   END IF;
   BEGIN
     SELECT CODIGO
            ,TIPOELEMENTO
            ,GENERAPLACA
            ,CLASE
     INTO   MI_CODIGO
            ,MI_TIPOELEMENTO
            ,MI_GENERAPLACA
            ,MI_CLASE
     FROM  TIPOMOVIMIENTO
     WHERE COMPANIA = UN_COMPANIA
       AND CODIGO   = UN_TIPOMOVIMIENTO;

     EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_CODIGO := 0;
          MI_TIPOELEMENTO:= 0;
          MI_GENERAPLACA:= 0;
          MI_CLASE:= 0;
   END;

  IF MI_TIPOELEMENTO <> 'C' THEN
  <<ACTUALIZA_MOVIMIENTO>>
     FOR MI_RSDM IN (SELECT ELEMENTO
                            ,SERIE
                            ,FECHA
                     FROM   D_MOVIMIENTO
                     WHERE  COMPANIA       = UN_COMPANIA
                       AND  TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
                       AND  MOVIMIENTO     = UN_NUMERO
                     ORDER BY COMPANIA
                              ,TIPOMOVIMIENTO
                              ,MOVIMIENTO
                              ,CODIGO)
     LOOP

      IF MI_GENERAPLACA = -1 THEN
         IF MI_CLASE = 'E' THEN
             BEGIN
               BEGIN
                        MI_TABLA   :='DEVOLUTIVO';

                        MI_CAMPOS  :='FECHA             = '||MI_FECHA || '
                                      ,FECHAENTRADA     = '||MI_FECHA || '
                                      ,FECHAADQUISICION = ' ||MI_FECHA ||'
                                      ,NIIF_FECHAADQUISICION = ' ||MI_FECHA ||'
                                      ,DATE_MODIFIED = SYSDATE
                                      ,MODIFIED_BY   = '''||UN_USUARIO||'''';

                        MI_CONDICION :='COMPANIA = '''||UN_COMPANIA||'''
                                    AND ELEMENTO = '''||MI_RSDM.ELEMENTO||'''
                                    AND SERIE    = '||MI_RSDM.SERIE||'';

                      MI_RTA2 := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA
                                                    ,UN_ACCION    => 'M'
                                                    ,UN_CAMPOS    => MI_CAMPOS
                                                    ,UN_CONDICION => MI_CONDICION);

                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
               END;
                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                     PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD     => SQLCODE
                            ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_UPDATE_FECHAMOV
                          );
             END;
          END IF;
      END IF;
      IF MI_CLASE = 'S' THEN
             BEGIN
               BEGIN
                        MI_TABLA   :='DEVOLUTIVO';
                        MI_CAMPOS  :='FECHA             = '||MI_FECHA || '
                                      ,FECHASALIDASERVICIO = ' ||MI_FECHA ||'
                                      ,FECHASERVICIO = ' ||MI_FECHA ||'
                                      ,DATE_MODIFIED = SYSDATE
                                      ,MODIFIED_BY   = '''||UN_USUARIO||'''';
                        MI_CONDICION :='COMPANIA = '''||UN_COMPANIA||'''
                                    AND ELEMENTO = '''||MI_RSDM.ELEMENTO||'''
                                    AND SERIE    = '||MI_RSDM.SERIE||'';
                      MI_RTA2 := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA
                                                    ,UN_ACCION    => 'M'
                                                    ,UN_CAMPOS    => MI_CAMPOS
                                                    ,UN_CONDICION => MI_CONDICION);
                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
               END;
                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                     PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD     => SQLCODE
                            ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_UPDATE_FECHAMOV
                          );
             END;
      END IF;

    END LOOP ACTUALIZA_MOVIMIENTO;

   END IF;

END PR_CAMBIARFECHAMOVIMIENTO;

--

PROCEDURE PR_REVISADEVOLUTIVO
   /*
   NAME 			      : PR_REVISADEVOLUTIVO
   AUTHORS 			    : SYSMAN SAS
   AUTHOR MIGRACION	: ADRIANA MARITZA C�?CERES BONILLA
   DATE MIGRADOR	  : 19/01/2016
   TIME				      : 11:58 AM
   MODULO ORIGEN	  : ALMACÉN
   DESCRIPTION		  : Proceso para revisar la ubicación de un elemento devolutivo
   MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
   DATE MODIFIED	  : 13/01/2017
   TIME				      : 3:32 PM
   MODIFICATIONS	  : Se cambió el estándar de codificación
                      y se agrego manejo de excepciones.

  */
  (
  UN_COMPANIA              IN  PCK_SUBTIPOS.TI_COMPANIA,
  UN_PLACAINICIAL          IN  PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_PLACAFINAL            IN  PCK_SUBTIPOS.TI_ENTERO_LARGO
  )
  AS
  MI_RS                    SYS_REFCURSOR;          -- Cursor de la tabla devolutivo
  MI_MESF                  PCK_SUBTIPOS.TI_ENTERO; -- Mes actual
  MI_ANOF                  PCK_SUBTIPOS.TI_ENTERO; -- año actual
  MI_ELEMENTO              VARCHAR2(200 CHAR);     -- Elemento de la tabla devolutivo
  MI_SERIE                 NUMBER;                 -- Serie de la tabla devolutivo
  MI_DEPENDENCIA           VARCHAR2(200 CHAR);     -- Dependencia de la tabal devolutivo
  MI_DMTERCERO             VARCHAR2(200 CHAR);     -- Tercero de la tabla D_MOVIMIENTO
  MI_FECHA                 DATE;                   -- Fecha de la tabla D_MOVIMIENTO
  MI_HORA                  DATE;                   -- Hora de la tabla D_MOVIMIENTO
  MI_TIPOMOVIMIENTO        VARCHAR2(200 CHAR);     -- Tipo de movimiento de la tabla D_MOVIMIENTO
  MI_MOVIMIENTO            PCK_SUBTIPOS.TI_ENTERO; -- Movimiento de la tabla D_MOVIMIENTO
  MI_CLASE                 VARCHAR2(200 CHAR);     -- Clase de la tabla TIPOMOVIMIENTO
  MI_MTERCERO              VARCHAR2(200 CHAR);     -- Tercero de la tabla Movimiento
  MI_CONCEPTO              VARCHAR2(200 CHAR);     -- Concepto de la tabla TIPOMOVIMIENTO
  MI_MOV_DEPENDENCIA       VARCHAR2(200 CHAR);     -- Dependencia de la tabla D_MOVIMIENTO
  MI_TABLA                 VARCHAR2(200 CHAR);     -- Almacena el nombre de la tabla a actualizar
  MI_CONDICION             VARCHAR2(3200 CHAR);    -- Almacena la condición con la que se va a actualizar
  MI_CAMPOS                VARCHAR2(3200 CHAR);    -- Almacena los campos de la tabla que se van a actualizar
  MI_RTA                   VARCHAR2(200 CHAR);
  MI_RESPONSABLEFUN        VARCHAR2(200 CHAR);
  MI_SUCURSAL_RESPONSABLE  VARCHAR2(200 CHAR);
  MI_MOVIMIENTOF           VARCHAR2(200 CHAR);
  MI_RESPONSABLE           VARCHAR2(200 CHAR);
  MI_TIPOMOVIMIENTOF       VARCHAR2(200 CHAR);
  MI_COUNT                 PCK_SUBTIPOS.TI_ENTERO:= 1;
  MI_CLASE_BODEGA_ORIGEN   VARCHAR2(16 CHAR);     -- Clase de la tabla TIPOMOVIMIENTO
  MI_CLASE_BODEGA_DESTINO  VARCHAR2(16 CHAR);     -- Clase de la tabla TIPOMOVIMIENTO
  MI_DEPENDENCIA_ALMACEN   VARCHAR2(12 CHAR):='000000000000'; --HPV ABR 10 DE 2018
BEGIN
-- SE DESACTIVO EN 10 ABRIL DE 2018 POR HPV
<<ACTUALIZAR_DEVOLUTIVOS>>
  FOR MI_RS IN (SELECT DEPENDENCIA
                       ,RESPONSABLE
                       ,ELEMENTO
                       ,SERIE
                       ,SUCURSAL_RESPONSABLE
                       ,FECHAENTRADA
                       ,FECHAULTMOV
                       ,TIPOMOVIMIENTOF
                       ,MOVIMIENTOF
                       ,FECHASERVICIO
                       ,FECHAANULADA
                       ,PLACAANULADA
                FROM   DEVOLUTIVO
                WHERE  COMPANIA =  UN_COMPANIA
                  AND  SERIE    >= UN_PLACAINICIAL
                  AND  SERIE    <= UN_PLACAFINAL
                ORDER BY COMPANIA
                         ,ELEMENTO)
  LOOP
 -- HPV ABR 10 DE 2018
  BEGIN
    SELECT CODIGO
    INTO   MI_DEPENDENCIA_ALMACEN
    FROM   DEPENDENCIA
    WHERE  COMPANIA=UN_COMPANIA 
      AND  CLASE_BODEGA='20'
      AND  ROWNUM=1;
  EXCEPTION WHEN NO_DATA_FOUND THEN
     MI_DEPENDENCIA_ALMACEN:='000000000000';
  END;
 -- HPV ABR 10 DE 2018
  MI_ELEMENTO := MI_RS.ELEMENTO;
  MI_SERIE := MI_RS.SERIE;
  MI_DEPENDENCIA := MI_RS.DEPENDENCIA;
  MI_SUCURSAL_RESPONSABLE := MI_RS.SUCURSAL_RESPONSABLE;
  MI_TIPOMOVIMIENTOF := MI_RS.TIPOMOVIMIENTOF;
  MI_MOVIMIENTOF := MI_RS.MOVIMIENTOF;
  MI_RESPONSABLE := MI_RS.RESPONSABLE;
  MI_RESPONSABLEFUN := PCK_ALMACEN.FC_RETRESPONSABLE(UN_COMPANIA, 40, 10);
  MI_MESF := EXTRACT(MONTH FROM SYSDATE);
  MI_ANOF := EXTRACT(YEAR FROM SYSDATE);

BEGIN

  SELECT COUNT (MOVIMIENTO.DEPENDENCIA_DESTINO) MI_COUNT
         ,MOVIMIENTO.DEPENDENCIA_DESTINO
         ,D_MOVIMIENTO.TERCERO
         ,D_MOVIMIENTO.FECHA
         ,D_MOVIMIENTO.TIPOMOVIMIENTO
         ,D_MOVIMIENTO.MOVIMIENTO
         ,TIPOMOVIMIENTO.CLASE
         ,MOVIMIENTO.TERCERO MTERCERO
         ,TIPOMOVIMIENTO.CONCEPTO,
         CLASE_BODEGA_ORIGEN.CODIGO,
         CLASE_BODEGA_DESTINO.CODIGO
  INTO   MI_COUNT
         ,MI_MOV_DEPENDENCIA
         ,MI_DMTERCERO
         ,MI_FECHA
         ,MI_TIPOMOVIMIENTO
         ,MI_MOVIMIENTO
         ,MI_CLASE
         ,MI_MTERCERO
         ,MI_CONCEPTO
         ,MI_CLASE_BODEGA_ORIGEN
         ,MI_CLASE_BODEGA_DESTINO
  FROM  D_MOVIMIENTO
  LEFT JOIN MOVIMIENTO
         ON D_MOVIMIENTO.COMPANIA       = MOVIMIENTO.COMPANIA
        AND D_MOVIMIENTO.TIPOMOVIMIENTO = MOVIMIENTO.TIPOMOVIMIENTO
        AND D_MOVIMIENTO.MOVIMIENTO     = MOVIMIENTO.NUMERO
  LEFT JOIN TIPOMOVIMIENTO
        ON D_MOVIMIENTO.COMPANIA       = TIPOMOVIMIENTO.COMPANIA
       AND D_MOVIMIENTO.TIPOMOVIMIENTO = TIPOMOVIMIENTO.CODIGO

  LEFT JOIN BODEGA BODEGA_ORIGEN ON (MOVIMIENTO.COMPANIA=BODEGA_ORIGEN.COMPANIA) AND (MOVIMIENTO.BODEGA_ORIGEN=BODEGA_ORIGEN.CODIGO) 
  LEFT JOIN CLASE_BODEGA CLASE_BODEGA_ORIGEN ON (BODEGA_ORIGEN.CLASE_BODEGA=CLASE_BODEGA_ORIGEN.CODIGO) 

  LEFT JOIN BODEGA BODEGA_DESTINO ON (MOVIMIENTO.COMPANIA=BODEGA_DESTINO.COMPANIA) AND (MOVIMIENTO.BODEGA_ORIGEN=BODEGA_DESTINO.CODIGO) 
  LEFT JOIN CLASE_BODEGA CLASE_BODEGA_DESTINO ON (BODEGA_DESTINO.CLASE_BODEGA=CLASE_BODEGA_DESTINO.CODIGO) 


  WHERE D_MOVIMIENTO.COMPANIA = UN_COMPANIA
    AND D_MOVIMIENTO.ELEMENTO = MI_ELEMENTO
    AND SERIE = MI_SERIE
    AND IND_REG = -1
    AND ROWNUM = 1
  GROUP BY MOVIMIENTO.DEPENDENCIA_DESTINO
           ,D_MOVIMIENTO.TERCERO
           ,D_MOVIMIENTO.FECHA
           ,D_MOVIMIENTO.HORA
           ,D_MOVIMIENTO.TIPOMOVIMIENTO
           ,D_MOVIMIENTO.MOVIMIENTO
           ,TIPOMOVIMIENTO.CLASE, MOVIMIENTO.TERCERO
           ,TIPOMOVIMIENTO.CONCEPTO
  ORDER BY D_MOVIMIENTO.FECHA
           ,D_MOVIMIENTO.HORA;

    EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_MOV_DEPENDENCIA := 0;
          MI_DMTERCERO:= 0;
          MI_FECHA:= '';
          MI_HORA := '';
          MI_TIPOMOVIMIENTO:= 0;
          MI_MOVIMIENTO := 0;
          MI_CLASE := 0;
          MI_MTERCERO := 0;
          MI_CONCEPTO := 0;
   END ;

  IF MI_DEPENDENCIA <> MI_MOV_DEPENDENCIA THEN
   BEGIN
    BEGIN
       MI_TABLA := 'DEVOLUTIVO';

       MI_CAMPOS := 'DEPENDENCIA     = '''|| MI_MOV_DEPENDENCIA || '''
                    ,RESPONSABLE     = '''|| MI_DMTERCERO || '''
                    ,FECHAENTRADA    = ''' || MI_FECHA || '''
                    ,FECHAULTMOV     = '''|| MI_FECHA || '''
                    ,TIPOMOVIMIENTOF = ''' || MI_TIPOMOVIMIENTO || '''
                    ,MOVIMIENTOF     = ' || MI_MOVIMIENTO;

       MI_CONDICION := 'DEVOLUTIVO.COMPANIA = '''||UN_COMPANIA||'''
                    AND DEVOLUTIVO.ELEMENTO = '''||MI_ELEMENTO||'''
                    AND DEVOLUTIVO.SERIE    = '||MI_SERIE||'';

       MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA
                                   ,UN_ACCION    => 'M'
                                   ,UN_CAMPOS    => MI_CAMPOS
                                   ,UN_CONDICION => MI_CONDICION);

     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
    END;
       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
         PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD     => SQLCODE
                ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_REVISADEVOLUTIVO
              );
   END;

  END IF;

   IF MI_COUNT <> 0 THEN
    IF MI_CLASE = 'E' THEN
       IF MI_CLASE_BODEGA_DESTINO <> '20' THEN
        BEGIN
          BEGIN

            MI_TABLA := 'DEVOLUTIVO';
            MI_CAMPOS := 'DEPENDENCIA      = ''' ||MI_DEPENDENCIA_ALMACEN || '''
                         , RESPONSABLE     = ''' || MI_RESPONSABLEFUN || '''
                         , FECHAENTRADA    = ''' || MI_FECHA || '''
                         , FECHAULTMOV     = ''' || MI_FECHA || '''
                         , TIPOMOVIMIENTOF = ''' || MI_TIPOMOVIMIENTO || '''
                         , MOVIMIENTOF     = ' || MI_MOVIMIENTO;
            MI_CONDICION := 'DEVOLUTIVO.COMPANIA = '''||UN_COMPANIA||'''
                         AND DEVOLUTIVO.ELEMENTO = '''||MI_ELEMENTO||'''
                         AND DEVOLUTIVO.SERIE    = '||MI_SERIE||'';
            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA => MI_TABLA
                                        ,UN_ACCION    => 'M'
                                        ,UN_CAMPOS    => MI_CAMPOS
                                        ,UN_CONDICION => MI_CONDICION);

             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
           END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
         PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD     => SQLCODE
              ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_REVISADEVOLUTIVO
            );
       END;

       ELSIF MI_DEPENDENCIA <> MI_MOV_DEPENDENCIA THEN
       BEGIN
        BEGIN
          MI_TABLA := 'DEVOLUTIVO';
          MI_CAMPOS := 'DEPENDENCIA      = ''' || MI_MOV_DEPENDENCIA ||'''
                       , RESPONSABLE     = ''' || MI_MTERCERO || '''
                       , FECHASERVICIO   = ''' || MI_FECHA || '''
                       , FECHAULTMOV     = ''' || MI_FECHA || '''
                       , TIPOMOVIMIENTOF = ''' || MI_TIPOMOVIMIENTO || '''
                       , MOVIMIENTOF     = ' || MI_MOVIMIENTO;
          MI_CONDICION := 'DEVOLUTIVO.COMPANIA     = '''||UN_COMPANIA||'''
                           AND DEVOLUTIVO.ELEMENTO = '''||MI_ELEMENTO||'''
                           AND DEVOLUTIVO.SERIE    = '||MI_SERIE||'';

          MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA
                                       ,UN_ACCION    => 'M'
                                       ,UN_CAMPOS    => MI_CAMPOS
                                       ,UN_CONDICION => MI_CONDICION);

             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_ALMACEN;

        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
         PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD     => SQLCODE
              ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_REVISADEVOLUTIVO
            );
       END;

       END IF;
     ELSE
       IF MI_DEPENDENCIA <> MI_MOV_DEPENDENCIA THEN
           IF MI_CONCEPTO = 'CM' OR MI_CONCEPTO = 'N' OR MI_CONCEPTO = 'II' THEN
              IF MI_DEPENDENCIA <> MI_MOV_DEPENDENCIA THEN
               BEGIN
                BEGIN
                  MI_TABLA := 'DEVOLUTIVO';
                  MI_CAMPOS := 'DEPENDENCIA      = ''' || MI_MOV_DEPENDENCIA ||'''
                               , RESPONSABLE     = ''' || MI_MTERCERO || '''
                               , FECHASERVICIO   = ''' || MI_FECHA || '''
                               , FECHAULTMOV     = ''' || MI_FECHA || '''
                               , TIPOMOVIMIENTOF = ''' || MI_TIPOMOVIMIENTO || '''
                               , MOVIMIENTOF     = ' || MI_MOVIMIENTO;
                   MI_CONDICION := 'DEVOLUTIVO.COMPANIA     = '''||UN_COMPANIA||'''
                                    AND DEVOLUTIVO.ELEMENTO = '''||MI_ELEMENTO||'''
                                    AND DEVOLUTIVO.SERIE    = '||MI_SERIE||'';

                   MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA
                                                ,UN_ACCION    => 'M'
                                                ,UN_CAMPOS    =>  MI_CAMPOS
                                                ,UN_CONDICION => MI_CONDICION);

                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_ALMACEN;

                END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD     => SQLCODE
                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_REVISADEVOLUTIVO
                );
               END;
              END IF;
           ELSIF MI_CONCEPTO = 'TG' OR MI_CONCEPTO = 'T' OR MI_CONCEPTO = 'RM' OR MI_CONCEPTO = 'DT' THEN
              IF MI_DEPENDENCIA = (CASE WHEN MI_CONCEPTO = 'DT' THEN '40' ELSE MI_MOV_DEPENDENCIA END) THEN
               BEGIN
                BEGIN
                 MI_TABLA := 'DEVOLUTIVO';

                 MI_CAMPOS := 'DEPENDENCIA    = ''' || CASE WHEN MI_CONCEPTO = 'DT' THEN '40' ELSE MI_MOV_DEPENDENCIA END ||'''
                               ,RESPONSABLE   = ''' || CASE WHEN MI_CONCEPTO = 'DT' THEN MI_RESPONSABLEFUN ELSE MI_MTERCERO END || '''
                               ,FECHASERVICIO = ''' || MI_FECHA || '''
                               ,FECHAULTMOV   = ''' || MI_FECHA || '''
                               ,PLACAANULADA  = ' || -1 || '
                               ,MOVIMIENTOF   = ' || MI_MOVIMIENTO;

                 MI_CONDICION := 'DEVOLUTIVO.COMPANIA       = '''||UN_COMPANIA||'''
                                    AND DEVOLUTIVO.ELEMENTO = '''||MI_ELEMENTO||'''
                                    AND DEVOLUTIVO.SERIE    = '||MI_SERIE||'';

                 MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA
                                              ,UN_ACCION    => 'M'
                                              ,UN_CAMPOS    => MI_CAMPOS
                                              ,UN_CONDICION => MI_CONDICION);

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_ALMACEN;


                END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD     => SQLCODE
                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_REVISADEVOLUTIVO
                );
               END;

              END IF;
           END IF;
       END IF;
    END IF;
   END IF;
  END LOOP ACTUALIZAR_DEVOLUTIVOS;

END PR_REVISADEVOLUTIVO;

--

  FUNCTION FC_RETRESPONSABLE
    /*
   NAME 			      : PR_REVISADEVOLUTIVO
   AUTHORS 			    : SYSMAN SAS
   AUTHOR MIGRACION	: ADRIANA MARITZA C�?CERES BONILLA
   DATE MIGRADOR	  : 20/01/2016
   TIME				      : 11:58 AM
   MODULO ORIGEN	  : ALMACÉN
   DESCRIPTION		  : Función que retorna el responsable de una dependencia.
   MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
   DATE MODIFIED	  : 13/01/2017
   TIME				      : 4:44 PM
   MODIFICATIONS	  : Se cambió el estándar de codificación
                      y se agrego manejo de excepciones.

  */
  (
  UN_COMPANIA               IN PCK_SUBTIPOS.TI_COMPANIA,  -- Código de la compania
  UN_DEPENDENCIA            IN VARCHAR2,                  -- Código de la dependencia
  UN_MODULO                 IN PCK_SUBTIPOS.TI_MODULO     -- Módulo
  )RETURN VARCHAR2
  AS
  MI_RETRESPONSABLE         VARCHAR2(200 CHAR);

  BEGIN
   BEGIN
     SELECT MAX(RESPONSABLE) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) AS PRESPONSABLE
     INTO   MI_RETRESPONSABLE
     FROM   DEPENDENCIA_RESPONSABLE
     WHERE  DEPENDENCIA        = UN_DEPENDENCIA
       AND  COMPANIA           = UN_COMPANIA
       AND  RESPONSABLEALMACEN = -1;


     IF MI_RETRESPONSABLE IS NULL THEN
        MI_RETRESPONSABLE := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                                   ,UN_NOMBRE    => 'CEDULA RESPONSABLE ALMACEN'
                                                   ,UN_MODULO    => UN_MODULO
                                                   ,UN_FECHA_PAR => SYSDATE);

      END IF;

   END;

  RETURN MI_RETRESPONSABLE;

END FC_RETRESPONSABLE;

--

  FUNCTION FC_NOMBREINVENTARIO
    /*
   NAME 			    : FC_NOMBREINVENTARIO ACCES -> NOMBREINVENTARIO
   AUTHORS 			  : DIEGO ALFREDO SUESCA RODR�?GUEZ
   DATE MIGRADOR	: 24/01/2016
   TIME				    : 14:45
   MODULO ORIGEN	: ALMACÉN
   DESCRIPTION		: FUNCIÓN QUE RETORNA EL NOMBRE DE UN INVENTARIO.
   MODIFIER			  : ELKIN GEOVANNY AMAYA SILVA
   DATE MODIFIED	: 13/01/2017
   TIME				    : 5:15 PM
   MODIFICATIONS	: Se cambió el estándar de codificación
                    y se agrego manejo de excepciones.


  */
  (
  UN_COMPANIA          IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ELEMENTO          IN VARCHAR2,
  UN_OPCION            IN PCK_SUBTIPOS.TI_LOGICO,
  UN_NOMCORTO          IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0													  
  )
  RETURN VARCHAR2
  AS

  MI_RESULTADO        VARCHAR2(500 CHAR);
  MI_NOMBRELARGO      VARCHAR2(500 CHAR);
  MI_UNIDAD         	VARCHAR2(500 CHAR);


BEGIN
   BEGIN
     SELECT (CASE WHEN UN_NOMCORTO = 1 
                THEN NVL(NOMBRECORTO,' ') 
                ELSE NVL(NOMBRELARGO,' ') 
            END)	
            ,UNIDAD
     INTO   MI_NOMBRELARGO
            ,MI_UNIDAD
     FROM   INVENTARIO
     WHERE  COMPANIA       = UN_COMPANIA
       AND  CODIGOELEMENTO = UN_ELEMENTO;

    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_RESULTADO := ' ';
        MI_UNIDAD:=' ';
   END;

  IF UN_OPCION = 1 THEN
  	MI_RESULTADO:=MI_UNIDAD;
  ELSE
  	MI_RESULTADO:=MI_NOMBRELARGO;
  END IF;

  RETURN MI_RESULTADO;

END FC_NOMBREINVENTARIO;


FUNCTION FC_ULTIMODEFECHASALIDA
    /*
   NAME 			    : ULTIMODEFECHASALIDA ->
   AUTHORS 			  : DIEGO ALFREDO SUESCA RODR�?GUEZ
   DATE MIGRADOR	: 01/01/2016
   TIME				    : 14:45
   MODULO ORIGEN	: ALMACÉN
   DESCRIPTION		: FUNCIÓN QUE RETORNA FECHA DE ULTIMO MOVIMIENTO
   MODIFIER			  : ELKIN GEOVANNY AMAYA SIVA
   DATE MODIFIED	: 13/01/2017
   TIME				    : 5:38 PM
   MODIFICATIONS	: Se cambió el estándar de codificación
                    y se agrego manejo de excepciones.

  */
  (
  UN_COMPANIA               IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ELEMENTO               IN VARCHAR2,
  UN_FECHA                  IN DATE
  )
  RETURN DATE
  AS
  MI_RESULTADO        		  DATE;
BEGIN
  BEGIN

     SELECT MIN(D_MOVIMIENTO.FECHA)  KEEP (DENSE_RANK FIRST ORDER BY D_MOVIMIENTO.FECHA,D_MOVIMIENTO.HORA)
     INTO   MI_RESULTADO
     FROM   D_MOVIMIENTO
     INNER JOIN V_MOVIMIENTO MOVIMIENTO
             ON D_MOVIMIENTO.COMPANIA       = MOVIMIENTO.COMPANIA
            AND D_MOVIMIENTO.TIPOMOVIMIENTO = MOVIMIENTO.TIPOMOVIMIENTO
            AND D_MOVIMIENTO.MOVIMIENTO     = MOVIMIENTO.NUMERO
     WHERE D_MOVIMIENTO.COMPANIA = UN_COMPANIA
       AND D_MOVIMIENTO.ELEMENTO = UN_ELEMENTO
       AND D_MOVIMIENTO.FECHA    <=  UN_FECHA
       AND D_MOVIMIENTO.IND_REG  NOT IN (0)
       AND MOVIMIENTO.CLASE      = 'S';

      IF MI_RESULTADO IS NULL THEN
         RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
       END IF;
  END;

  RETURN MI_RESULTADO;
       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD =>SQLCODE
            ,UN_ERROR_COD=>PCK_ERRORES.ERR_ALMACEN_NODATA
         );

END FC_ULTIMODEFECHASALIDA;

--

FUNCTION FC_VALIDATRANSACCIONALMHW
  /*
    NAME              : FC_VALIDATRANSACCIONALMHW En Access --> ValidaTransaccionAlmHW
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : NICOL�?S GÓMEZ BARBOSA
    DATE MIGRADOR     : 02/02/2016
    TIME              : 10:00 AM
    SOURCE MODULE     : ALMACEN
    DESCRIPTION       : Función para validar si los movimientos de bodega son válidos o no.
    MODIFIER			    : ELKIN GEOVANNY AMAYA SIVA
    DATE MODIFIED	    : 16/01/2017
    TIME				      : 8:38 AM
    MODIFICATIONS	    : Se cambió el estándar de codificación
                        y se agrego manejo de excepciones.

  */
  (
  UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_BODEGAORIGEN     IN VARCHAR2,
  UN_BODEGADESTINO    IN VARCHAR2
  )RETURN NUMBER
  AS
    MI_RTA          NUMBER;
    MI_RS           SYS_REFCURSOR;

BEGIN

    MI_RTA:=-1;
  BEGIN
   <<EXTRAER_BODEGAS>>
    FOR MI_RS IN(SELECT CLASE_BODEGA_ORIGEN
                        ,CLASE_BODEGA_DESTINO
                        ,CONCEPTO
                        ,CLASE
                        ,CONDICION
                        TIPO_ELEMENTO
                 FROM   TRANSACCIONES_VALIDAS
                 WHERE  COMPANIA             = UN_COMPANIA
                   AND  CLASE_BODEGA_ORIGEN  = UN_BODEGAORIGEN
                   AND  CLASE_BODEGA_DESTINO = UN_BODEGADESTINO)
    LOOP

      IF UN_BODEGAORIGEN='20' THEN
        MI_RTA:=0;
      END IF;

      IF UN_BODEGAORIGEN='40' AND UN_BODEGADESTINO='30'  THEN
        MI_RTA:=0;
      END IF;

      IF UN_BODEGAORIGEN='10' AND UN_BODEGADESTINO='10'  THEN
        IF MI_RS.CLASE<>'T' AND MI_RS.CONCEPTO='T' OR (MI_RS.CLASE='S' AND MI_RS.CONCEPTO='DT') OR MI_RS.CLASE<>'E' AND MI_RS.CONCEPTO='CR' THEN
          MI_RTA:=0;
        END IF;
      END IF;

      IF UN_BODEGAORIGEN='20' AND UN_BODEGADESTINO='20'  THEN
        IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                 ,UN_NOMBRE    => 'PERMITE REINTEGRO DE RESPONSABILIDADES A BODEGA DE ALMACEN'
                                 ,UN_MODULO    => 10
                                 ,UN_FECHA_PAR => SYSDATE) = 'NO' THEN
          IF (MI_RS.CLASE='D' OR MI_RS.CLASE='DER') AND (MI_RS.CONCEPTO='DT' OR MI_RS.CONCEPTO='L') THEN
            MI_RTA:=-1;
          ELSE
            MI_RTA:=0;
          END IF;
        ELSE
          IF (MI_RS.CLASE='D' OR MI_RS.CLASE='E') AND (MI_RS.CONCEPTO='R' OR MI_RS.CONCEPTO='DT' OR MI_RS.CONCEPTO='L') THEN
            MI_RTA:=-1;
          ELSE
            MI_RTA:=0;
          END IF;
        END IF;
      END IF;

      IF UN_BODEGAORIGEN='90' AND UN_BODEGADESTINO='90'  THEN
        MI_RTA:=0;
      END IF;

      IF UN_BODEGAORIGEN='90' AND UN_BODEGADESTINO='30'  THEN
        MI_RTA:=0;
      END IF;

      IF UN_BODEGAORIGEN='90' AND UN_BODEGADESTINO='40'  THEN
        MI_RTA:=0;
      END IF;


    END LOOP EXTRAER_BODEGAS;

  END;

  RETURN MI_RTA;

END FC_VALIDATRANSACCIONALMHW;


PROCEDURE PR_GRABAPROVEEDORESGRAL
  /*
    NAME              : PR_GRABAPROVEEDORESGRAL En Access --> GrabaProveedoresGral
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : NICOL�?S GÓMEZ BARBOSA
    DATE MIGRADOR     : 02/02/2016
    TIME              : 02:00 PM
    SOURCE MODULE     : ALMACEN
    DESCRIPTION       : Función para grabar los proveedores de movimientos.
    MODIFIER			    : ELKIN GEOVANNY AMAYA SIVA
    DATE MODIFIED	    : 16/01/2017
    TIME				      : 9:38 AM
    MODIFICATIONS	    : Se cambió el estándar de codificación
                        y se agrego manejo de excepciones.

  */
  (
  UN_COMPANIA               IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOMOVIMIENTO         IN VARCHAR2,
  UN_NUMERO                 IN VARCHAR2
  )
  AS
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
    MI_RS               SYS_REFCURSOR;

  BEGIN
  EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS=''.,''';

  <<INSERTAR_PROVEEDORES>>

      FOR MI_RS IN( SELECT DISTINCT
                    D_MOVIMIENTO.TIPOMOVIMIENTO,
                    D_MOVIMIENTO.MOVIMIENTO,
                    D_MOVIMIENTO.ELEMENTO,
                    MOVIMIENTO.TERCERO,
                    MOVIMIENTO.SUCURSAL
                    FROM D_MOVIMIENTO
                    INNER JOIN MOVIMIENTO
                          ON D_MOVIMIENTO.MOVIMIENTO      = MOVIMIENTO.NUMERO
                          AND D_MOVIMIENTO.TIPOMOVIMIENTO = MOVIMIENTO.TIPOMOVIMIENTO
                          AND D_MOVIMIENTO.COMPANIA       = MOVIMIENTO.COMPANIA
                    WHERE D_MOVIMIENTO.COMPANIA           = UN_COMPANIA
                          AND D_MOVIMIENTO.TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
                          AND D_MOVIMIENTO.MOVIMIENTO     = UN_NUMERO
                          AND D_MOVIMIENTO.ELEMENTO NOT IN(SELECT ELEMENTO
                    FROM PROVEEDOR
                    WHERE PROVEEDOR.COMPANIA     = MOVIMIENTO.COMPANIA
                          AND PROVEEDOR.TERCERO  = MOVIMIENTO.TERCERO
                          AND PROVEEDOR.SUCURSAL = MOVIMIENTO.SUCURSAL))
    LOOP
    BEGIN
     BEGIN
      MI_CAMPOS:='COMPANIA
                  ,ELEMENTO
                  ,TERCERO
                  ,SUCURSAL
                  ,FULTCOMPRA';

      MI_VALORES := ''''||UN_COMPANIA||'''
                    ,'''||MI_RS.ELEMENTO||'''
                    ,'''||MI_RS.TERCERO||'''
                    ,'''||MI_RS.SUCURSAL||'''
                    ,SYSDATE';

      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'PROVEEDOR'
                                             ,UN_ACCION  => 'I'
                                             ,UN_CAMPOS  => MI_CAMPOS
                                             ,UN_VALORES => MI_VALORES);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
         RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
     END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE
            ,UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_GRABAPROVEEDOR
         );

    END;
    END LOOP INSERTAR_PROVEEDORES;

    <<UPDATE_PROVEEDOR_INVENTARIO>>
    FOR MI_RS IN(SELECT MAX(MOVIMIENTO.FECHA) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) ULFECHA
                        ,SUM(D_MOVIMIENTO.CANTIDAD) ULTCANTIDAD
                        ,MAX(D_MOVIMIENTO.VALORUNITARIO) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) VLRUNITARIO
                        ,MAX(D_MOVIMIENTO.PORCDESCUENTO) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) ULTDESC
                        ,MAX(D_MOVIMIENTO.PORCIVA) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) ULTIVA
                        ,D_MOVIMIENTO.ELEMENTO
                        ,MOVIMIENTO.TERCERO
                        ,MOVIMIENTO.SUCURSAL
                 FROM   MOVIMIENTO
                  INNER JOIN D_MOVIMIENTO
                     ON MOVIMIENTO.COMPANIA = D_MOVIMIENTO.COMPANIA
                    AND MOVIMIENTO.TIPOMOVIMIENTO = D_MOVIMIENTO.TIPOMOVIMIENTO
                    AND MOVIMIENTO.NUMERO = D_MOVIMIENTO.MOVIMIENTO
                 WHERE  MOVIMIENTO.COMPANIA       = UN_COMPANIA
                   AND  MOVIMIENTO.TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
                   AND  MOVIMIENTO.NUMERO         = UN_NUMERO
                   AND  D_MOVIMIENTO.CANTIDAD     NOT IN (0)
                  GROUP BY D_MOVIMIENTO.ELEMENTO
                           ,MOVIMIENTO.TERCERO
                           ,MOVIMIENTO.SUCURSAL )
    LOOP
    BEGIN
     BEGIN
      MI_CAMPOS:='FPENCOMPRA   = FULTCOMPRA
                  ,CPENCOMPRA  = CULTCOMPRA
                  ,PPENCOMPRA  = PULTCOMPRA
                  ,PDPENCOMPRA = PDULTCOMPRA
                  ,PIPENCOMPRA = PIULTCOMPRA
                  ,FULTCOMPRA  = '||PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA => MI_RS.ULFECHA)||'
                  ,CULTCOMPRA  = '||MI_RS.ULTCANTIDAD||'
                  ,PULTCOMPRA  = '||MI_RS.VLRUNITARIO||'
                  ,PDULTCOMPRA = '||MI_RS.ULTDESC||'
                  ,PIULTCOMPRA = '||MI_RS.ULTIVA;

      MI_CONDICION := 'COMPANIA ='''||UN_COMPANIA||'''
                   AND ELEMENTO ='''||MI_RS.ELEMENTO||'''
                   AND TERCERO  ='''||MI_RS.TERCERO||'''
                   AND SUCURSAL ='''||MI_RS.SUCURSAL||'''';

      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'PROVEEDOR'
                                             ,UN_ACCION    => 'M'
                                             ,UN_CAMPOS    => MI_CAMPOS
                                             ,UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
         RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
     END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE
            ,UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_ACTUALIZARPROV
         );

    END;
    BEGIN
     BEGIN
      MI_CAMPOS:='PROVULTCOMPRA   = '''||MI_RS.TERCERO||'''
                 ,SUCURSAL        = '''||MI_RS.SUCURSAL||'''
                 ,FECHAULTCOMPRA  = '||PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA => MI_RS.ULFECHA)||'
                 ,PRECIOULTCOMPRA = '||MI_RS.VLRUNITARIO||'
                 ,FECHAULTMOV     = '||PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA => MI_RS.ULFECHA)||'
                 ,CANTULTMOV      = '||MI_RS.ULTCANTIDAD;

      MI_CONDICION := 'COMPANIA       ='''||UN_COMPANIA||'''
                   AND CODIGOELEMENTO ='''||MI_RS.ELEMENTO||'''';

      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'INVENTARIO'
                                            ,UN_ACCION    => 'M'
                                            ,UN_CAMPOS    => MI_CAMPOS
                                            ,UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
         RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
     END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE
            ,UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_ACTUALIZARINV
         );

    END;

    END LOOP UPDATE_PROVEEDOR_INVENTARIO;

END PR_GRABAPROVEEDORESGRAL;


FUNCTION FC_INTERFACEALMACENUNOAUNO
  /*
    NAME              : FC_INTERFACEALMACENUNOAUNO En Access --> InterfaceAlmacenUnoAUno
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : NICOL�?S GÓMEZ BARBOSA
    DATE MIGRADOR     : 02/02/2016
    TIME              : 00:00 PM
    SOURCE MODULE     : ALMACEN
    DESCRIPTION       :
    MODIFIER			    : ELKIN GEOVANNY AMAYA SIVA
    DATE MODIFIED	    : 16/01/2017
    TIME				      : 10:45 AM
    MODIFICATIONS	    : Se cambió el estándar de codificación
                        y se agrego manejo de excepciones.

  */
  (
  UN_COMPANIA               IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOMOVIMIENTO         IN VARCHAR2,
  UN_NUMERO                 IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_FECHA                  IN DATE,
  UN_DESCRIPCION            IN VARCHAR2,
  UN_DIGITOS                IN PCK_SUBTIPOS.TI_ENTERO,
  UN_TERCERO                IN VARCHAR2,
  UN_SUCURSAL               IN VARCHAR2,
  UN_CENTROCOSTO            IN VARCHAR2,
  UN_CREADOR                IN VARCHAR2,
  UN_VALORDOCUMENTO         IN PCK_SUBTIPOS.TI_DOBLE
  )
  RETURN NUMBER
  AS
    MI_RTA              PCK_SUBTIPOS.TI_ENTERO;
    MI_RTA_AUX          PCK_SUBTIPOS.TI_ENTERO;
    MI_TERCERON         VARCHAR2(32000 CHAR);
    MI_SUCURSALN        VARCHAR2(32000 CHAR);
    MI_CENTROCOSTON     VARCHAR2(32000 CHAR);
    MI_AUXILIARN        VARCHAR2(32000 CHAR);
    MI_DESCRIPCIONN     VARCHAR2(32000 CHAR);
    MI_CUENTAS          CUENTAS;
    MI_BASEGRAVABLE     PCK_SUBTIPOS.TI_ENTERO:=0;
    MI_I                PCK_SUBTIPOS.TI_ENTERO;
    MI_RS               SYS_REFCURSOR;

BEGIN

    MI_RTA:=0;
    MI_I:=0;
    MI_AUXILIARN:=PCK_DATOS.FC_CONS_AUXILIAR;

  <<EXTRAER_MOVALMACEN>>
    FOR MI_RS IN(SELECT D_MOVIMIENTO.TIPOMOVIMIENTO
                        ,D_MOVIMIENTO.MOVIMIENTO
                        ,SUBSTR(ELEMENTO,0,UN_DIGITOS) GRUPO
                        ,SUM(D_MOVIMIENTO.VALORTOTAL) TOTAL
                        ,D_MOVIMIENTO.TERCERO
                        ,D_MOVIMIENTO.SUCURSAL
                        ,D_MOVIMIENTO.CENTRODECOSTO
                        ,ALMACENCONTABILIDAD.ANO
                        ,ALMACENCONTABILIDAD.CODIGOELEMENTO
                        ,ALMACENCONTABILIDAD.CUENTADEBITO
                        ,ALMACENCONTABILIDAD.CUENTACREDITO
                 FROM   D_MOVIMIENTO
                   LEFT JOIN ALMACENCONTABILIDAD
                     ON D_MOVIMIENTO.COMPANIA       = ALMACENCONTABILIDAD.COMPANIA
                    AND D_MOVIMIENTO.TIPOMOVIMIENTO = ALMACENCONTABILIDAD.TIPOMOVIMIENTO
                 WHERE D_MOVIMIENTO.COMPANIA       = UN_COMPANIA
                   AND D_MOVIMIENTO.TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
                   AND D_MOVIMIENTO.MOVIMIENTO     = UN_NUMERO
                 GROUP BY D_MOVIMIENTO.TIPOMOVIMIENTO
                          ,D_MOVIMIENTO.MOVIMIENTO
                          ,SUBSTR(ELEMENTO,0,UN_DIGITOS)
                          ,D_MOVIMIENTO.TERCERO
                          ,D_MOVIMIENTO.SUCURSAL
                          ,D_MOVIMIENTO.CENTRODECOSTO
                          ,ALMACENCONTABILIDAD.ANO
                          ,ALMACENCONTABILIDAD.CODIGOELEMENTO
                          ,ALMACENCONTABILIDAD.CUENTADEBITO
                          ,ALMACENCONTABILIDAD.CUENTACREDITO
                HAVING  ALMACENCONTABILIDAD.ANO            = EXTRACT (YEAR FROM UN_FECHA)
                   AND  ALMACENCONTABILIDAD.CODIGOELEMENTO = SUBSTR(ELEMENTO,0,UN_DIGITOS))
    LOOP

      IF MI_RS.TERCERO IS NOT NULL AND MI_RS.SUCURSAL IS NOT NULL THEN
        MI_TERCERON := MI_RS.TERCERO;
        MI_SUCURSALN:= MI_RS.SUCURSAL;
      ELSE
        MI_TERCERON := UN_TERCERO;
        MI_SUCURSALN:= UN_SUCURSAL;
      END IF;

      IF MI_RS.CENTRODECOSTO IS NOT NULL THEN
        MI_CENTROCOSTON := MI_RS.CENTRODECOSTO;
      ELSE
        MI_CENTROCOSTON := UN_CENTROCOSTO;
      END IF;

      IF MI_RS.CUENTADEBITO IS NOT NULL THEN
        MI_CUENTAS(MI_I).CUENTA       :=MI_RS.CUENTADEBITO;
        MI_CUENTAS(MI_I).DEBITO       :=NVL(MI_RS.TOTAL,0);
        MI_CUENTAS(MI_I).CREDITO      :=0;
        MI_CUENTAS(MI_I).TERCERO      :=MI_TERCERON;
        MI_CUENTAS(MI_I).SUCURSAL     :=MI_SUCURSALN;
        MI_CUENTAS(MI_I).CENTROCOSTO  :=MI_CENTROCOSTON;
        MI_CUENTAS(MI_I).AUXILIAR     :=MI_AUXILIARN;
        MI_CUENTAS(MI_I).BASEGRAVABLE :=UN_VALORDOCUMENTO;
        MI_I                          :=MI_I+1;
      END IF;

      IF MI_RS.CUENTACREDITO IS NOT NULL THEN
        MI_CUENTAS(MI_I).CUENTA      :=MI_RS.CUENTACREDITO;
        MI_CUENTAS(MI_I).DEBITO      :=0;
        MI_CUENTAS(MI_I).CREDITO     :=NVL(MI_RS.TOTAL,0);
        MI_CUENTAS(MI_I).TERCERO     :=MI_TERCERON;
        MI_CUENTAS(MI_I).SUCURSAL    :=MI_SUCURSALN;
        MI_CUENTAS(MI_I).CENTROCOSTO :=MI_CENTROCOSTON;
        MI_CUENTAS(MI_I).AUXILIAR    :=MI_AUXILIARN;
        MI_CUENTAS(MI_I).BASEGRAVABLE:=UN_VALORDOCUMENTO;
        MI_I                         :=MI_I+1;
      END IF;

    END LOOP EXTRAER_MOVALMACEN;

    IF UN_DESCRIPCION IS NULL THEN
      MI_DESCRIPCIONN:='Según movimiento de almacén: '||UN_TIPOMOVIMIENTO||' Nº '||UN_NUMERO;
    ELSE
      MI_DESCRIPCIONN:=UN_DESCRIPCION;
    END IF;

    MI_RTA_AUX:=PCK_ALMACEN.FC_INTERFACEUNOAUNO(UN_COMPANIA        => UN_COMPANIA
                                                ,UN_TIPO           => UN_TIPOMOVIMIENTO
                                                ,UN_NUMERO         => UN_NUMERO
                                                ,UN_FECHA          => UN_FECHA
                                                ,UN_DESCRIPCION    => MI_DESCRIPCIONN
                                                ,UN_TERCERO        => UN_TERCERO
                                                ,UN_SUCURSAL       => UN_SUCURSAL
                                                ,UN_CUENTAS        => MI_CUENTAS
                                                ,UN_CREADOR        => UN_CREADOR
                                                ,UN_VALORDOCUMENTO => UN_VALORDOCUMENTO
                                                ,UN_BASEGRAVABLE   =>MI_BASEGRAVABLE);

    IF MI_RTA_AUX=0 THEN
      MI_RTA:=0;
    ELSE
      MI_RTA:=-1;
    END IF;

  RETURN MI_RTA;


END FC_INTERFACEALMACENUNOAUNO;


FUNCTION FC_INTERFACEUNOAUNO
  /*
    NAME              : FC_INTERFACEALMACENUNOAUNO En Access --> InterfaceAlmacenUnoAUno
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : NICOL�?S GÓMEZ BARBOSA
    DATE MIGRADOR     : 02/02/2016
    TIME              : 00:00 PM
    SOURCE MODULE     : ALMACEN
    DESCRIPTION       : Función para grabar en devolutivos
    MODIFIER			    : ELKIN GEOVANNY AMAYA SIVA
    DATE MODIFIED	    : 16/01/2017
    TIME				      : 11:50 AM
    MODIFICATIONS	    : Se cambió el estándar de codificación
                        y se agrego manejo de excepciones.

  */
  (
  UN_COMPANIA               IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPO                   IN VARCHAR2,
  UN_NUMERO                 IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_FECHA                  IN DATE,
  UN_DESCRIPCION            IN VARCHAR2,
  UN_TERCERO                IN VARCHAR2,
  UN_SUCURSAL               IN VARCHAR2,
  UN_CUENTAS                IN CUENTAS,
  UN_CREADOR                IN VARCHAR2,
  UN_VALORDOCUMENTO         IN PCK_SUBTIPOS.TI_DOBLE,
  UN_BASEGRAVABLE           IN PCK_SUBTIPOS.TI_DOBLE
  )
  RETURN NUMBER
  AS
    MI_RTA              PCK_SUBTIPOS.TI_ENTERO;
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
    MI_COUNT            PCK_SUBTIPOS.TI_ENTERO;
    MI_I                PCK_SUBTIPOS.TI_ENTERO;
    MI_ANIO             PCK_SUBTIPOS.TI_ENTERO;

  BEGIN

    MI_RTA  := 0;
    MI_ANIO := EXTRACT (YEAR FROM UN_FECHA);
    MI_I    := 0;

    SELECT DISTINCT COUNT(*)
    INTO   MI_COUNT
    FROM   COMPROBANTE_CNT
    WHERE  COMPANIA = UN_COMPANIA
      AND  ANO      = MI_ANIO
      AND  TIPO     = UN_TIPO
      AND  NUMERO   = UN_NUMERO;

    IF MI_COUNT>0 THEN
      BEGIN
        BEGIN
        MI_CONDICION:='COMPANIA    = '''||UN_COMPANIA||'''
                   AND ANO         = '||MI_ANIO||'
                   AND TIPO_CPTE   = '''||UN_TIPO||'''
                   AND COMPROBANTE = '||UN_NUMERO;

        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'DETALLE_COMPROBANTE_CNT'
                                              ,UN_ACCION    =>  'E'
                                              ,UN_CONDICION => MI_CONDICION);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
        END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE
              ,UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_DELETEDCOMPROCNT
              );
      END;

      BEGIN
        BEGIN
          MI_CONDICION:='COMPANIA = '''||UN_COMPANIA||'''
                     AND ANO      = '||MI_ANIO||'
                     AND TIPO     = '''||UN_TIPO||'''
                     AND NUMERO   = '||UN_NUMERO;

          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'COMPROBANTE_CNT'
                                                 ,UN_ACCION    => 'E'
                                                 ,UN_CONDICION => MI_CONDICION);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
             RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
        END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                        ,UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_DELETECOMPROCNT
                                        );
      END;

    END IF;

    BEGIN
     BEGIN
      MI_CAMPOS:='COMPANIA
                  ,ANO
                  ,TIPO
                  ,NUMERO
                  ,FECHA
                  ,DESCRIPCION
                  ,TERCERO
                  ,SUCURSAL
                  ,CREATED_BY
                  ,DATE_CREATED
                  ,ANULADO
                  ,VLR_DOCUMENTO
                  ,FECHA_VCN_DOC
                  ,VLR_BASE';
      MI_VALORES := ''''||UN_COMPANIA||'''
                    ,'||MI_ANIO||'
                    ,'''||UN_TIPO||'''
                    ,'||UN_NUMERO||'
                    ,'||PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA => UN_FECHA)||'
                    ,'''||UN_DESCRIPCION||'''
                    ,'''||UN_TERCERO||'''
                    ,'''||UN_SUCURSAL||'''
                    ,'''||UN_CREADOR||'''
                    ,SYSDATE
                    ,0
                    ,'||UN_VALORDOCUMENTO||'
                    ,'||PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA => ADD_MONTHS(UN_FECHA,1))||'
                    ,'||UN_BASEGRAVABLE||'';

      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'COMPROBANTE_CNT'
                                             ,UN_ACCION  => 'I'
                                             ,UN_CAMPOS  => MI_CAMPOS
                                             ,UN_VALORES => MI_VALORES);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
         RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
     END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                      ,UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_INSERTCOMPROCNT
                                      );

    END;
   <<INSERT_DETALLE_COMPROBANTE_CNT>>
    FOR MI_I IN UN_CUENTAS.FIRST .. UN_CUENTAS.LAST
    LOOP
    BEGIN
     BEGIN
      MI_CAMPOS:='COMPANIA
                  ,ANO
                  ,TIPO_CPTE
                  ,COMPROBANTE
                  ,CONSECUTIVO
                  ,CUENTA
                  ,FECHA
                  ,DESCRIPCION
                  ,VALOR_DEBITO
                  ,VALOR_CREDITO
                  ,CENTRO_COSTO
                  ,TERCERO,SUCURSAL
                  ,AUXILIAR
                  ,CREATED_BY
                  ,DATE_CREATED
                  ,BASE_GRAVABLE';
      MI_VALORES := ''''||UN_COMPANIA||'''
                    ,'||MI_ANIO||'
                    ,'''||UN_TIPO||'''
                    ,'||UN_NUMERO||'
                    ,'||(MI_I+1)||'
                    ,'''||UN_CUENTAS(MI_I).CUENTA||'''
                    ,'||PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA => UN_FECHA)||'
                    ,'''||UN_DESCRIPCION||'''
                    ,'||UN_CUENTAS(MI_I).DEBITO||'
                    ,'||UN_CUENTAS(MI_I).CREDITO||'
                    ,'''||UN_CUENTAS(MI_I).CENTROCOSTO||'''
                    ,'''||UN_CUENTAS(MI_I).TERCERO||'''
                    ,'''||UN_CUENTAS(MI_I).SUCURSAL||'''
                    ,'''||UN_CUENTAS(MI_I).AUXILIAR||'''
                    ,'''||UN_CREADOR||'''
                    ,SYSDATE
                    ,'''||UN_CUENTAS(MI_I).BASEGRAVABLE||'''';

      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'DETALLE_COMPROBANTE_CNT'
                                             ,UN_ACCION  => 'I'
                                             ,UN_CAMPOS  => MI_CAMPOS
                                             ,UN_VALORES => MI_VALORES);

             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
     END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE
            ,UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_INSERTDCOMPROCNT
         );

    END;

    END LOOP INSERT_DETALLE_COMPROBANTE_CNT;

    MI_RTA:=-1;

  RETURN MI_RTA;

END FC_INTERFACEUNOAUNO;


FUNCTION FC_GRABADEVOLUTIVOS
  /*
    NAME              : FC_GRABADEVOLUTIVOS En Access --> GrabaDevolutivos
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : NICOL�?S GÓMEZ BARBOSA
    DATE MIGRADOR     : 03/02/2016
    TIME              : 08:00 AM
    SOURCE MODULE     : ALMACEN
    DESCRIPTION       : Función para grabar devolutivos de movimientos.
    MODIFIER			    : ELKIN GEOVANNY AMAYA SIVA
    DATE MODIFIED	    : 16/01/2017
    TIME				      : 2:25 PM
    MODIFICATIONS	    : Se cambió el estándar de codificación
                        y se agrego manejo de excepciones.
    MODIFIER			    : JUAN CARLOS RODR�?GUEZ AMÉZQUITA
                        ANA YESSICA SANA
    DATE MODIFIED	    : 17/08/2017
                        23/08/2018
    TIME              : 5:04 PM
                        4:54 PM
    MODIFICATIONS     : Validación de existencia de devolutivo antes de ejecutar 
                        la sentencia de inserción.
                        Al insertar registro en tabla DEVOLUTIVO, en el campo NIIF_VALOR_BASE 
                        se resta a NIIF_VALOR_TOTAL los valores RESIDUAL Y DETERIORO, a demas
                        se da valor a VIDAUTILPLACANIIF.
  */
  (
    UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TIPOMOVIMIENTO           IN VARCHAR2,
    UN_NUMERO                   IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
    UN_TIPO                     IN VARCHAR2,
    UN_DEPENDENCIA_DESTINO      IN VARCHAR2,
    UN_RESPONSABLE_DESTINO      IN VARCHAR2,
    UN_SUCURSAL_RESPONSABLE     IN VARCHAR2,
    UN_INVENTARIO_INICIAL       IN PCK_SUBTIPOS.TI_LOGICO,
    UN_FECHA                    IN DATE
  )
  RETURN NUMBER
  AS
    MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
    MI_CONPARAMETRO         VARCHAR2(32000 CHAR);
    MI_CONPARAMETROC        VARCHAR2(32000 CHAR);
    MI_DEPRECIARMES         PCK_SUBTIPOS.TI_ENTERO := 0;
    MI_PARDEPRECIARMES      VARCHAR2(32000 CHAR);
    MI_SERIAL               PCK_SUBTIPOS.TI_ENTERO;
    MI_SINSERIE             PCK_SUBTIPOS.TI_ENTERO;
    MI_I                    PCK_SUBTIPOS.TI_ENTERO;
    MI_I2                   PCK_SUBTIPOS.TI_ENTERO;
    MI_RTA                  PCK_SUBTIPOS.TI_ENTERO;
    MI_NUEVOBODEGA          VARCHAR2(32000 CHAR);
    MI_NUEVOCLASEBODEGA     VARCHAR2(32000 CHAR);
    MI_RS                   SYS_REFCURSOR;
    MI_VALORTOTAL           PCK_SUBTIPOS.TI_DOBLE;
    MI_CUANTIAMINIMA        PCK_SUBTIPOS.TI_ENTERO := 0;
    MI_MEDIOSALARIOMINIMO   PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_IDENTIFICADOR        VARCHAR(32000 CHAR);
    MI_CONSECUTIVO          VARCHAR(32000 CHAR);
    MI_NIIFVALORBASE        DEVOLUTIVO.NIIF_VALORBASE%TYPE;
	MI_PARCOLGAAPNIIF       VARCHAR2(32000 CHAR);											 
  BEGIN

    MI_CONPARAMETRO:=NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
      , UN_NOMBRE    => 'CON PARAMETRO PLACA INICIAL INVENTARIO'
      , UN_MODULO    => 10
      , UN_FECHA_PAR => SYSDATE),'NO');
    MI_CONPARAMETROC:=NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
      , UN_NOMBRE    => 'CON PARAMETRO CONSECUTIVO INICIAL INVENTARIO'
      , UN_MODULO    => 10
      , UN_FECHA_PAR => SYSDATE),'NO');
    MI_PARDEPRECIARMES:=NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
      , UN_NOMBRE    => 'DEPRECIAR ELEMENTO DE CUANTIA MINIMA EN MES DE COMPRA'
      , UN_MODULO    => 10
      , UN_FECHA_PAR => SYSDATE),'NO');
	MI_PARCOLGAAPNIIF:=NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
      , UN_NOMBRE    => 'EJECUTA COLGAAP y NIIF'
      , UN_MODULO    => 10
      , UN_FECHA_PAR => SYSDATE),'NO');								   
    IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
      , UN_NOMBRE    => 'MANEJA DEPRECIACION PARA CUANTIA MINIMA'
      , UN_MODULO    => 10
      , UN_FECHA_PAR => SYSDATE),'NO') = 'SI' 
    THEN
      MI_DEPRECIARMES:=13-TO_NUMBER(TO_CHAR(UN_FECHA,'MM'));
      SELECT NVL(CUANTIAMINIMADEPRE,0), ROUND(NVL(SALARIOMINIMO,0)/2,2)
        INTO MI_CUANTIAMINIMA, MI_MEDIOSALARIOMINIMO
        FROM ANO
       WHERE COMPANIA = UN_COMPANIA
         AND NUMERO   = TO_NUMBER(TO_CHAR(UN_FECHA,'YYYY'));
    END IF;
    IF UN_INVENTARIO_INICIAL NOT IN (0) AND MI_CONPARAMETRO = 'SI' THEN
      MI_SERIAL:=PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
        , UN_NOMBRE    => 'PLACA INVENTARIO INICIAL'
        , UN_MODULO    => 10
        , UN_FECHA_PAR => SYSDATE)+1;
      MI_SINSERIE:=PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
        , UN_NOMBRE    => 'CONSECUTIVO INVENTARIO INICIAL'
        , UN_MODULO    => 10
        , UN_FECHA_PAR => SYSDATE)+1;
    ELSE
      IF UN_INVENTARIO_INICIAL NOT IN (0) THEN
        SELECT DECODE(MAX(DEVOLUTIVO.SERIE),NULL,1,MAX(DEVOLUTIVO.SERIE)+1)
          INTO MI_SERIAL
          FROM DEVOLUTIVO
         WHERE COMPANIA = UN_COMPANIA
           AND PLACA    = 'P';
      ELSE
        SELECT DECODE(
                 MAX(DEVOLUTIVO.SERIE),NULL,
                 PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                    , UN_NOMBRE    => 'PLACA INICIAL'
                    , UN_MODULO    => 10
                    , UN_FECHA_PAR => SYSDATE),
                 MAX(DEVOLUTIVO.SERIE)+1)
          INTO MI_SERIAL
          FROM DEVOLUTIVO
         WHERE COMPANIA = UN_COMPANIA
           AND PLACA    = 'P';
      END IF;
      SELECT DECODE(
              MAX(DEVOLUTIVO.SERIE),NULL,
              PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                , UN_NOMBRE    => 'CONSECUTIVO INVENTARIO INICIAL'
                , UN_MODULO    => 10
                , UN_FECHA_PAR => SYSDATE),MAX(DEVOLUTIVO.SERIE)+1)
        INTO MI_SINSERIE
        FROM DEVOLUTIVO
       WHERE COMPANIA = UN_COMPANIA
         AND PLACA    = 'C';
    END IF;
    --
    BEGIN
      SELECT NVL(BODEGA_DESTINO,'99'),NVL(CLASE_BODEGA_DESTINO,'99')
        INTO MI_NUEVOBODEGA,MI_NUEVOCLASEBODEGA
        FROM MOVIMIENTO
       WHERE COMPANIA       = UN_COMPANIA
         AND TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
         AND NUMERO         = UN_NUMERO
         AND REGISTRADO     IN (0);
    EXCEPTION 
      WHEN NO_DATA_FOUND THEN
        MI_NUEVOBODEGA:='99';  
    END;
    --
    SELECT COUNT(*)
      INTO MI_I
      FROM D_MOVIMIENTO
     WHERE COMPANIA       = UN_COMPANIA
       AND TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
       AND MOVIMIENTO     =  UN_NUMERO
       AND IND_REG        = 0
       AND CANTIDAD       > 0
    ORDER BY COMPANIA
            ,TIPOMOVIMIENTO
            ,MOVIMIENTO
            ,CODIGO;
    IF MI_I=0 THEN
      RETURN -1;
    END IF;
<<INSERTAR_DEVOLUTIVOS>>
    --JEG 31/01/2024
    --SE AGREGA UBICACION
  FOR MI_RS IN(
    SELECT  ELEMENTO ,
            CANTIDAD ,
            VALORTOTAL ,
            ESTADO ,
            MOVIMIENTO ,
            ESPECIFICACION ,
            MARCA ,
            SERIE ,
            CODIGO ,
            SERIEDEVOLUTIVO ,
            ESCRITURA ,
            NUMEROCATASTRAL ,
            MATINMOBILIARIA ,
            COD_CHIP ,
            AREA,
            PREPARACION ,
            ENTREGA ,
            INSTALACION ,
            COMPROBACION ,
            REVELACIONES ,
            SALVAMENTO ,
            DETERIORO ,
            NIIF_VALOR_TOTAL ,
            NIIF_TIPO_ACTIVO ,
            NIIF_VALOR_BASE,
            VIDAUTILPLACA,
            VIDAUTILPLACANIIF,
            APLICANIIF,
            UBICACION,
			VLR_COLGAAP,
            CENTRODECOSTO,
            REFERENCIA_CNT		   
       FROM D_MOVIMIENTO
      WHERE COMPANIA     = UN_COMPANIA
        AND TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
        AND MOVIMIENTO     = UN_NUMERO
        AND SERIE         IN(0)
        AND CANTIDAD       > 0
      ORDER BY COMPANIA ,
            TIPOMOVIMIENTO ,
            MOVIMIENTO ,
            CODIGO)
    LOOP

      MI_NIIFVALORBASE := MI_RS.NIIF_VALOR_TOTAL - MI_RS.SALVAMENTO - MI_RS.DETERIORO;
      IF MI_RS.CANTIDAD=0 THEN
         MI_VALORTOTAL:=0;
      ELSE
         MI_VALORTOTAL:=MI_RS.VALORTOTAL/MI_RS.CANTIDAD;
      END IF;
      BEGIN
        SELECT IDENTIFICADOR
          INTO MI_IDENTIFICADOR
          FROM INVENTARIO
         WHERE COMPANIA       = UN_COMPANIA
           AND CODIGOELEMENTO = MI_RS.ELEMENTO;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        RETURN 0;
      END;
      --
      IF MI_IDENTIFICADOR = 'P' THEN
        MI_CONSECUTIVO := MI_SERIAL;
      ELSE
        MI_CONSECUTIVO := MI_SINSERIE;
      END IF;
      --
<<ACTUALIZAR_D_MOVIMIENTO>>
      FOR MI_I IN  1..MI_RS.CANTIDAD
      LOOP
        DECLARE
          CREAR_DEVOLUTIVO                     BOOLEAN := FALSE;
        BEGIN
          DECLARE
            MI_EXISTE                           NUMBER;
          BEGIN
            SELECT 1
              INTO MI_EXISTE
              FROM DEVOLUTIVO
             WHERE COMPANIA = UN_COMPANIA
               AND ELEMENTO = MI_RS.ELEMENTO
               AND SERIE    = MI_CONSECUTIVO;
          EXCEPTION
            WHEN NO_DATA_FOUND THEN
              CREAR_DEVOLUTIVO := TRUE;
          END;
          IF CREAR_DEVOLUTIVO THEN
            -- Inserción DEVOLUTIVO

            --@asana, 02/10/2018, se agrega actulizacion del campo NIIF_FECHAADQUISICION con el mismo de FECHAADQUISICION
            BEGIN
              MI_CAMPOS :='COMPANIA
                          , ELEMENTO
                          , SERIE
                          , DEPENDENCIA
                          , RESPONSABLE
                          , SUCURSAL_RESPONSABLE
                          , VALOR
                          , ESTADO
                          , FECHAADQUISICION
                          , NIIF_FECHAADQUISICION
                          , FECHAENTRADA
                          , ORIGEN
                          , NUMEROORIGEN
                          , FECHAULTMOV
                          , TIPOMOVIMIENTOI
                          , MOVIMIENTOI
                          , PLACA
                          , TIPOELEMENTO
                          , DESCRIPCION
                          , MARCA
                          , SERIEDEVOLUTIVO
                          , COSTOAJUSTADO
                          , CUANTIAMIN
                          , ESCRITURA
                          , NUMEROCATASTRAL
                          , MATINMOBILIARIA
                          , COD_CHIP
                          , AREA
                          , PREPARACION
                          , ENTREGA
                          , INSTALACION
                          , COMPROBACION
                          , REVELACIONES
                          , SALVAMENTO
                          , DETERIORO
                          , NIIF_FECHAFUNCIONAMIENTO
                          , NIIF_VLRAJUSTADO
                          , NIIF_COSTOAJUSTADO
                          , NIIF_VLRACUMULADOAJ
                          , NIIF_DEPACUMULADA
                          , NIIF_VLRLIBROS
                          , NIIF_DEPACUMULADAAJ
                          , NIIF_VLRDEPRECIACIONAJ
                          , NIIF_SALDOAJ
                          , NIIF_VLRDEPRECIACION
                          , NIIF_COSTOSALIDA
                          , NIIF_COSTOAJUSSALIDA
                          , NIIF_AJDEPACUMULADA
                          , NIIF_VLRREVERSIONAI
                          , NIIF_VALORULTIMOV
                          , NIIF_VALORANTINI
                          , NIIF_VALOR_TOTAL
                          , NIIF_TIPO_ACTIVO
                          , NIIF_VALOR_BASE
                          , NIIF_VALORBASE
                          , BODEGA
                          , CLASE_BODEGA
                          , MESESVIDAUTILPLACA
                          , NIIF_VIDA_UTIL, APLICA_NIIF
                          , TIPOMOVIMIENTOF,MOVIMIENTOF
                          , UBICACION
						  , VLR_COLGAAP
                          , CENTRODECOSTO
                          , REFERENCIA_CNT';			 
              MI_VALORES := ''''||UN_COMPANIA||'''
                            ,'''||MI_RS.ELEMENTO||'''
                            ,'||MI_CONSECUTIVO||'
                            ,'''||UN_DEPENDENCIA_DESTINO||'''
                            ,'''||UN_RESPONSABLE_DESTINO||'''
                            ,'''||UN_SUCURSAL_RESPONSABLE||'''
                            ,'||MI_VALORTOTAL||'
                            ,'''||NVL(MI_RS.ESTADO,'B')||'''
                            ,'||PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA)||'
                            ,'||PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA)||'
                            ,'||PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA)||'
                            ,'''||UN_TIPOMOVIMIENTO||'''
                            ,'||MI_RS.MOVIMIENTO||'
                            ,'||PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA)||'
                            ,'''||UN_TIPOMOVIMIENTO||'''
                            ,'||UN_NUMERO||'
                            ,'''||MI_IDENTIFICADOR||'''
                            ,'''||UN_TIPO||'''
                            ,'''||NVL(MI_RS.ESPECIFICACION,'.')||'''
                            ,'''||NVL(MI_RS.MARCA,'.')||'''
                            ,'''||NVL(MI_RS.SERIEDEVOLUTIVO,' ')||'''
                            ,'||MI_VALORTOTAL||'
                            ,CASE WHEN '||MI_VALORTOTAL||'<='||MI_CUANTIAMINIMA||' THEN -1 ELSE 0 END
                            ,'''||NVL(MI_RS.ESCRITURA,' ')||'''
                            ,'''||NVL(MI_RS.NUMEROCATASTRAL,' ')||'''
                            ,'''||NVL(MI_RS.MATINMOBILIARIA,' ')||'''
                            ,'''||NVL(MI_RS.COD_CHIP,' ')||'''
                            ,'''||NVL(MI_RS.AREA,' ')||'''
                            ,'||NVL(MI_RS.PREPARACION,0)||'
                            ,'||NVL(MI_RS.ENTREGA,0)||'
                            ,'||NVL(MI_RS.INSTALACION,0)||'
                            ,'||NVL(MI_RS.COMPROBACION,0)||'
                            ,'''||NVL(MI_RS.REVELACIONES,' ')||'''
                            ,'||NVL(MI_RS.SALVAMENTO,0)||'
                            ,'||NVL(MI_RS.DETERIORO,0)||'
                            ,'||PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA)||'
                            ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
                            ,'||NVL(MI_RS.NIIF_VALOR_TOTAL,0)||'
                            ,'''||NVL(MI_RS.NIIF_TIPO_ACTIVO,'01')||'''
                            ,'||NVL(MI_NIIFVALORBASE,0)||'
                            ,'||NVL(MI_VALORTOTAL,0)||'
                            ,'''||MI_NUEVOBODEGA||'''
                            ,'''||MI_NUEVOCLASEBODEGA||'''
                            ,CASE WHEN '''||MI_PARDEPRECIARMES||''' = ''SI'' 
                               THEN CASE WHEN '||MI_VALORTOTAL||'<='||MI_CUANTIAMINIMA||' THEN 1 ELSE 0 END 
                               ELSE CASE WHEN '||MI_VALORTOTAL||'<='||MI_MEDIOSALARIOMINIMO||' THEN 1
                                    ELSE CASE WHEN '||MI_VALORTOTAL||'<='||MI_CUANTIAMINIMA||' THEN '||MI_DEPRECIARMES||' 
                                    ELSE 0 END 
                               END 
                             END
                            ,'||NVL(MI_RS.VIDAUTILPLACANIIF,0)||'
                            ,'||NVL(MI_RS.APLICANIIF,0)||'   
                            ,'''||UN_TIPOMOVIMIENTO||'''
                            ,'||UN_NUMERO||' 
                            ,'''||MI_RS.UBICACION||'''
							,CASE WHEN '''||MI_PARCOLGAAPNIIF||''' = ''SI'' 
                               THEN '||MI_RS.VLR_COLGAAP||' 
                               ELSE 0  
                             END
                             ,'''||MI_RS.CENTRODECOSTO||'''
                             ,'''||MI_RS.REFERENCIA_CNT||''' ';  
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'DEVOLUTIVO'
                , UN_ACCION  => 'I'
                , UN_CAMPOS  => MI_CAMPOS
                , UN_VALORES => MI_VALORES);
            EXCEPTION 
              WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
              WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE
                  , UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_INSERT_DEVOLUTIVO);
            END;
          END IF;
        END;
        -- Actualiza D_MOVIMIENTO
        BEGIN
          MI_CONDICION := 'COMPANIA       ='''||UN_COMPANIA||'''
                     AND TIPOMOVIMIENTO ='''||UN_TIPOMOVIMIENTO||'''
                     AND MOVIMIENTO     =  '||UN_NUMERO||'
                     AND ELEMENTO       ='''||MI_RS.ELEMENTO||'''
                     AND CODIGO         ='||MI_RS.CODIGO||'';
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'D_MOVIMIENTO'
            , UN_ACCION    => 'M'
            , UN_CAMPOS    => 'SERIE='''||(MI_CONSECUTIVO)||''''
            , UN_CONDICION => MI_CONDICION);
          MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
        EXCEPTION 
          WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
          WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
              , UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_UPDATE_DMOV);
        END;
      END LOOP ACTUALIZAR_D_MOVIMIENTO;
    --
    IF MI_IDENTIFICADOR = 'P' THEN
       MI_SERIAL := MI_CONSECUTIVO;
    ELSE
       MI_SINSERIE := MI_CONSECUTIVO;
    END IF;
  END LOOP INSERTAR_DEVOLUTIVOS;
    --
    IF UN_INVENTARIO_INICIAL NOT IN (0) AND MI_CONPARAMETRO = 'SI' THEN

    BEGIN
     BEGIN
      MI_CONDICION:='COMPANIA ='''||UN_COMPANIA||'''
                 AND NOMBRE   =''PLACA INVENTARIO INICIAL''';
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'PARAMETRO'
                                             ,UN_ACCION    => 'M'
                                             ,UN_CAMPOS    => 'VALOR='''||(MI_SERIAL-1)||''''
                                             ,UN_CONDICION => MI_CONDICION);
      MI_CONDICION:= 'COMPANIA ='''||UN_COMPANIA||'''
                  AND NOMBRE   =''CONSECUTIVO INVENTARIO INICIAL''';
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'PARAMETRO'
                                            ,UN_ACCION    => 'M'
                                            ,UN_CAMPOS    => 'VALOR='''||(MI_SINSERIE-1)||''''
                                            ,UN_CONDICION => MI_CONDICION);

       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;

     END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE
                      ,UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_UPDATE_PARAMETRO
          );
    END;
    END IF;

    MI_RTA:=-1;

  RETURN MI_RTA;

END FC_GRABADEVOLUTIVOS;

FUNCTION FC_REVISARPEPS
  /*
  NAME              : FC_REVISARPEPS En Access --> RevisarPEPs
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : NICOL�?S GÓMEZ BARBOSA
  DATE MIGRADOR     : 05/02/2016
  TIME              : 03:00 PM
  SOURCE MODULE     : ALMACEN
  DESCRIPTION       : Revisa PEPS.
  MODIFIER			    : ELKIN GEOVANNY AMAYA SIVA
  DATE MODIFIED	    : 16/01/2017
  TIME				      : 3:42 PM
  MODIFICATIONS	    : Se cambió el estándar de codificación
                      y se agrego manejo de excepciones.

  */
  (
  UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_FECHAINICIAL             IN DATE,
  UN_FECHAFINAL               IN DATE,
  UN_ELEMENTOINICIAL          IN VARCHAR2,
  UN_PLACAINICIAL             IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_ACTUALIZAR               IN PCK_SUBTIPOS.TI_LOGICO,
  UN_INFORMAR                 IN PCK_SUBTIPOS.TI_LOGICO
  )
  RETURN CLOB
  AS
    MI_RTA                    CLOB;
    MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
    MI_SALDOS                 SALDOS;
    MI_I                      PCK_SUBTIPOS.TI_ENTERO;
    MI_L                      PCK_SUBTIPOS.TI_ENTERO;
    MI_INCON                  PCK_SUBTIPOS.TI_ENTERO;
    MI_INCON1                 PCK_SUBTIPOS.TI_ENTERO;
    MI_SALDOQ                 PCK_SUBTIPOS.TI_ENTERO;
    MI_SALDOV                 PCK_SUBTIPOS.TI_ENTERO;
    MI_VU                     PCK_SUBTIPOS.TI_ENTERO;
    MI_VT                     PCK_SUBTIPOS.TI_ENTERO;
    MI_EDIT                   PCK_SUBTIPOS.TI_ENTERO;
    MI_Q                      PCK_SUBTIPOS.TI_ENTERO;
    MI_INDICADOR              PCK_SUBTIPOS.TI_ENTERO;
    MI_INDICADOR2             PCK_SUBTIPOS.TI_ENTERO;
    MI_SS                     PCK_SUBTIPOS.TI_ENTERO;
    MI_VALORTOTALAUX          PCK_SUBTIPOS.TI_ENTERO;
    MI_VALORUNITARIOAUX       PCK_SUBTIPOS.TI_ENTERO;
    MI_VLRUNITARIOPROMAUX     PCK_SUBTIPOS.TI_ENTERO;
    MI_FECHAFINAL             DATE;
    MI_FECHADECORTEII         DATE;
    MI_FECHADECORTEREV        DATE;
    MI_RS                     SYS_REFCURSOR;

  BEGIN

    EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS=''.,''';

    MI_FECHAFINAL     := NVL(UN_FECHAFINAL,SYSDATE);
    MI_FECHADECORTEII := TO_DATE(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                                           ,UN_NOMBRE    => 'FECHA DE CORTE PARA INICIO DEL ALMACEN'
                                                           ,UN_MODULO    => 10
                                                           ,UN_FECHA_PAR => SYSDATE),SYSDATE),'DD/MM/YYYY');

    IF UN_ACTUALIZAR NOT IN (0) THEN
     BEGIN
      BEGIN
        MI_FECHADECORTEREV:=UN_FECHAINICIAL;
        MI_CONDICION:= 'COMPANIA ='''||UN_COMPANIA||'''
                    AND NOMBRE   =''ACTUALIZA PROMEDIO ALMACEN''';
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'PARAMETRO'
                                               ,UN_ACCION    => 'M'
                                               ,UN_CAMPOS    => 'VALOR=''NO'''
                                               ,UN_CONDICION => MI_CONDICION);


       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;

      END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE
                      ,UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_UPDATE_PARAMETRO
          );
     END;

    ELSE
      MI_FECHADECORTEREV:=TO_DATE(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA    => UN_COMPANIA
                                                            ,UN_NOMBRE     => 'FECHA DE CORTE PARA REVISION VALORES DE ALMACEN PEPS'
                                                            ,UN_MODULO     => 10
                                                            ,UN_FECHA_PAR  => SYSDATE),'01/01/'||(EXTRACT (YEAR FROM SYSDATE)-1)||''),'DD/MM/YYYY');
    END IF;

    IF UN_INFORMAR NOT IN (0) THEN
      MI_RTA:=MI_RTA||'INCONSISTENCIAS EN VALORES Y SALDOS DE KARDEX'||CHR(13);
      MI_RTA:=MI_RTA||'--------------------------------------------------------------------------------'||CHR(13);
    END IF;

    MI_INCON:=0;
    MI_INCON1:=0;
    MI_L:=0;
    MI_SALDOQ:=0;
    MI_SALDOV:=0;
    MI_VU:=0;
    MI_VT:=0;

  <<ACTUALIZAR_D_MOVIMIENTO>>
    FOR MI_RS IN (SELECT D_MOVIMIENTO.ELEMENTO
                         ,D_MOVIMIENTO.SERIE
                         ,D_MOVIMIENTO.TIPOMOVIMIENTO
                         ,D_MOVIMIENTO.MOVIMIENTO
                         ,D_MOVIMIENTO.FECHA
                         ,D_MOVIMIENTO.HORA
                         ,D_MOVIMIENTO.CANTIDAD
                         ,D_MOVIMIENTO.VALORUNITARIO
                         ,D_MOVIMIENTO.VALORTOTAL
                         ,D_MOVIMIENTO.CODIGO
                         ,TM.CLASE
                         ,TM.CONCEPTO
                         ,INVENTARIO.TIPO
                         ,D_MOVIMIENTO.SALDOKARDEX
                         ,D_MOVIMIENTO.VALORSALDO
                         ,D_MOVIMIENTO.VLRUNITARIOPROM
                  FROM   D_MOVIMIENTO
                    INNER JOIN TIPOMOVIMIENTO TM
                       ON  D_MOVIMIENTO.COMPANIA       = TM.COMPANIA
                      AND  D_MOVIMIENTO.TIPOMOVIMIENTO = TM.CODIGO
                      INNER JOIN INVENTARIO
                         ON  D_MOVIMIENTO.COMPANIA = INVENTARIO.COMPANIA
                        AND  D_MOVIMIENTO.ELEMENTO = INVENTARIO.CODIGOELEMENTO
                  WHERE D_MOVIMIENTO.COMPANIA = UN_COMPANIA
                    AND D_MOVIMIENTO.ELEMENTO = UN_ELEMENTOINICIAL
                    AND D_MOVIMIENTO.SERIE    = UN_PLACAINICIAL
                    AND D_MOVIMIENTO.FECHA    BETWEEN MI_FECHADECORTEII AND MI_FECHAFINAL
                    AND D_MOVIMIENTO.IND_REG  NOT IN (0)
                    AND INVENTARIO.TIPO       IN ('C')
                  ORDER BY D_MOVIMIENTO.ELEMENTO
                           ,D_MOVIMIENTO.FECHA
                           ,D_MOVIMIENTO.HORA)
    LOOP
      MI_EDIT:=0;
      MI_INDICADOR:=0;
      MI_INDICADOR2:=0;
      MI_Q:=NVL(MI_RS.CANTIDAD,0);

      IF MI_RS.CONCEPTO='CR' THEN
        MI_SALDOS(MI_L).VALORES := MI_SALDOS(MI_L).VALORES + NVL(MI_RS.VALORUNITARIO,0);
      ELSE
        IF MI_RS.CLASE='E' THEN
          MI_L:=MI_L+1;
          MI_SALDOS(MI_L).CANTIDAD:=MI_RS.CANTIDAD;
          MI_SALDOS(MI_L).VALORES:=MI_RS.VALORUNITARIO;
          MI_SALDOQ:= MI_SALDOQ + MI_RS.CANTIDAD;
          MI_SALDOV:= MI_SALDOV + PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => MI_RS.VALORUNITARIO * MI_RS.CANTIDAD
                                                          ,UN_PRECISION => 2);
        ELSIF MI_RS.CLASE='S' THEN
          MI_VT:=0;
          MI_I:=1;
          MI_INDICADOR:=0;
          WHILE MI_INDICADOR IN (0) LOOP
            IF MI_I<=MI_L THEN
              IF MI_SALDOS(MI_I).CANTIDAD > 0 THEN
                IF MI_SALDOS(MI_I).CANTIDAD >= MI_Q THEN
                  MI_SALDOS(MI_I).CANTIDAD:=MI_SALDOS(MI_I).CANTIDAD - MI_Q;
                  MI_VT:=MI_VT + MI_SALDOS(MI_I).VALORES * MI_Q;
                  MI_Q:= 0;
                ELSE
                  MI_VT:=MI_VT + MI_SALDOS(MI_I).VALORES * MI_SALDOS(MI_I).CANTIDAD;
                  MI_Q:=MI_Q - MI_SALDOS(MI_I).CANTIDAD;
                  MI_SALDOS(MI_I).CANTIDAD:=0;
                END IF;
              END IF;
              IF MI_Q = 0 THEN
                MI_INDICADOR:=-1;
              ELSE
                MI_I:=MI_I+1;
              END IF;
            ELSE
              MI_SALDOS(MI_L).CANTIDAD:=MI_SALDOS(MI_L).CANTIDAD-MI_Q;
              MI_VT:=MI_VT+MI_SALDOS(MI_L).VALORES*MI_Q;
              IF PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => MI_SALDOQ-MI_RS.CANTIDAD
                                         ,UN_PRECISION => 2)<0 THEN
                MI_INCON1 := MI_INCON1 + 1;
                IF UN_INFORMAR NOT IN (0) THEN
                  MI_RTA:=MI_RTA||'C-'||LPAD(MI_INCON1,9,'0')||'   '||RPAD(MI_RS.TIPOMOVIMIENTO,12,' ')||'   '||RPAD(MI_RS.MOVIMIENTO,10,' ')||' Código: '||RPAD(MI_RS.ELEMENTO,16,' ')||' Serie: '||RPAD(MI_RS.SERIE,10,' ')||' Item: '||RPAD(MI_RS.CODIGO,5,' ')||' Fecha: '||TO_CHAR(MI_RS.FECHA,'DD/MM/YYYY')||'. Saldo queda en Negativo: '||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => MI_SALDOQ-MI_RS.CANTIDAD
                                                                                                                                                                                                                                                                                                                                                                          ,UN_PRECISION =>  2)||CHR(13);
                END IF;
              END IF;
              MI_INDICADOR:=-1;
            END IF;
          END LOOP;

          MI_VU:=CASE WHEN MI_RS.CANTIDAD>0 THEN PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => MI_VT/MI_RS.CANTIDAD
                                                                         ,UN_PRECISION => 2) ELSE 0 END;

          MI_SALDOQ:=PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => MI_SALDOQ-MI_RS.CANTIDAD
                                             ,UN_PRECISION =>6);

          MI_SALDOV:=MI_SALDOV-PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => MI_VT
                                                       ,UN_PRECISION => 2);

          IF PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => MI_RS.VALORTOTAL
                                     ,UN_PRECISION => 2)<>PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => MI_VT
                                                                                   ,UN_PRECISION => 2) THEN
            MI_INCON:=MI_INCON + 1;
            IF UN_ACTUALIZAR IN (0) THEN
              IF UN_INFORMAR NOT IN (0) THEN
                MI_RTA:=MI_RTA||'V-'||LPAD(MI_INCON,9,'0')||'   '||RPAD(MI_RS.TIPOMOVIMIENTO,12,' ')||'   '||RPAD(MI_RS.MOVIMIENTO,10,' ')||' Código: '||RPAD(MI_RS.ELEMENTO,16,' ')||' Serie: '||RPAD(MI_RS.SERIE,10,' ')||' Item: '||RPAD(MI_RS.CODIGO,5,' ')||' Fecha: '||TO_CHAR(MI_RS.FECHA,'DD/MM/YYYY')||'. Valor total no coincide, debería ser '||MI_VT||' y está en '||MI_RS.VALORTOTAL||CHR(13);
              END IF;
            ELSE
              IF UN_INFORMAR NOT IN (0) THEN
                MI_RTA:=MI_RTA||'V-'||LPAD(MI_INCON,9,'0')||'   '||RPAD(MI_RS.TIPOMOVIMIENTO,12,' ')||'   '||RPAD(MI_RS.MOVIMIENTO,10,' ')||' Código: '||RPAD(MI_RS.ELEMENTO,16,' ')||' Serie: '||RPAD(MI_RS.SERIE,10,' ')||' Item: '||RPAD(MI_RS.CODIGO,5,' ')||' Fecha: '||TO_CHAR(MI_RS.FECHA,'DD/MM/YYYY')||'. Valor total no coincide, debería ser '||MI_VT||' y está en '||MI_RS.VALORTOTAL||'. Se corrigió.'||CHR(13);
              END IF;
              IF UN_ACTUALIZAR NOT IN (0) AND MI_RS.FECHA>MI_FECHADECORTEREV THEN
                MI_VALORTOTALAUX:=MI_VT;
                MI_VALORUNITARIOAUX:=MI_VU;
                MI_VLRUNITARIOPROMAUX:=MI_VU;
                MI_EDIT:=-1;
                MI_INDICADOR2:=-1;
              END IF;
            END IF;
          END IF;
        END IF;
      END IF;

      IF MI_SALDOQ<=0 THEN
       <<EXTRAER_SALDOS>>
        FOR MI_I IN 1..MI_L LOOP
          MI_SALDOS(MI_I).CANTIDAD:=0;
        END LOOP EXTRAER_SALDOS;
      END IF;
      IF MI_SALDOQ=0 THEN
        MI_SALDOV:=0;
      END IF;
      IF MI_RS.SALDOKARDEX <> MI_SALDOQ OR MI_RS.VALORSALDO <> PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => MI_SALDOV
                                                                                       ,UN_PRECISION => 2) OR PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => CASE WHEN MI_INDICADOR2 IN (0) THEN MI_RS.VLRUNITARIOPROM ELSE MI_VLRUNITARIOPROMAUX END
                                                                                                                                      ,UN_PRECISION => 2) <>  PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      =>MI_VU
                                                                                                                                                                                      ,UN_PRECISION => 2) OR PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => MI_RS.SALDOKARDEX
                                                                                                                                                                                                                                     ,UN_PRECISION => 2) <> PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => MI_SALDOQ
                                                                                                                                                                                                                                                                                    ,UN_PRECISION => 2) OR MI_INDICADOR2 NOT IN (0) THEN
        IF MI_EDIT  IN (0) AND UN_ACTUALIZAR NOT IN (0) AND MI_RS.FECHA > MI_FECHADECORTEREV THEN
          MI_EDIT:=-1;
        END IF;
        IF MI_EDIT NOT IN (0) AND UN_ACTUALIZAR NOT IN (0) AND MI_RS.FECHA > MI_FECHADECORTEREV THEN
          IF MI_INDICADOR2 IN (0) THEN
            MI_CAMPOS:= 'SALDOKARDEX       ='||MI_SALDOQ||'
                         , VALORSALDO      ='||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => MI_SALDOV
                                                                 ,UN_PRECISION => 2)||'
                         , VLRUNITARIOPROM ='||MI_VU;
          ELSE
            MI_CAMPOS:= 'SALDOKARDEX  ='||MI_SALDOQ||'
                         , VALORSALDO ='||PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => MI_SALDOV
                                                                 ,UN_PRECISION => 2)||'
                         , VLRUNITARIOPROM  ='||MI_VU||'
                         , VALORTOTAL       ='||MI_VALORTOTALAUX||'
                         , VALORUNITARIO    ='||MI_VALORUNITARIOAUX;
          END IF;
        BEGIN
          BEGIN
          MI_CONDICION:='COMPANIA       ='''||UN_COMPANIA||'''
                     AND TIPOMOVIMIENTO ='''||MI_RS.TIPOMOVIMIENTO||'''
                     AND MOVIMIENTO     ='||MI_RS.MOVIMIENTO||'
                     AND CODIGO         ='||MI_RS.CODIGO;

          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'D_MOVIMIENTO'
                                                 ,UN_ACCION    => 'M'
                                                 ,UN_CAMPOS    => MI_CAMPOS
                                                 ,UN_CONDICION => MI_CONDICION);

                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_ALMACEN;

          END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE
                      ,UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_UPDATE_DMOV
          );

        END;
        END IF;
      END IF;
    END LOOP ACTUALIZAR_D_MOVIMIENTO;

    MI_SS:=CASE WHEN MI_SALDOQ<>0 THEN PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => MI_SALDOV/MI_SALDOQ
                                                               ,UN_PRECISION => 2) ELSE 0 END;

  BEGIN
    BEGIN
    MI_CAMPOS:= 'VLRUNITARIOPROM ='||MI_SS||'
                 , EXISTENCIA    ='||MI_SALDOQ||'
                 , VALORTOTAL    ='||MI_SALDOV;
    MI_CONDICION:='COMPANIA       ='''||UN_COMPANIA||'''
               AND CODIGOELEMENTO ='''||UN_ELEMENTOINICIAL||'''';
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'INVENTARIO'
                                           ,UN_ACCION    => 'M'
                                           ,UN_CAMPOS    => MI_CAMPOS
                                           ,UN_CONDICION => MI_CONDICION);


         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
    END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
               PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                        ,UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_ACTUALIZARINV
          );
  END;

    IF UN_INFORMAR NOT IN (0) THEN
      MI_RTA:=MI_RTA||'INCONSISTENCIAS PEPS ENCONTRADAS EN VALORES: '||MI_INCON||' EN CANTIDADES: '||MI_INCON1||CHR(13);
      MI_RTA:=MI_RTA||'FIN DEL INFORME'||CHR(13);
    END IF;

  RETURN MI_RTA;

END FC_REVISARPEPS;


FUNCTION FC_REVISASIHAYMOVIMIENTO
  /*
  NAME              : FC_REVISASIHAYMOVIMIENTO En Access --> RevisaSiHayMovimiento
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : NICOL�?S GÓMEZ BARBOSA
  DATE MIGRADOR     : 09/02/2016
  TIME              : 11:30 PM
  SOURCE MODULE     : ALMACEN
  DESCRIPTION       : Revisa si hay movimiento.
  MODIFIER			    : ELKIN GEOVANNY AMAYA SIVA
  DATE MODIFIED	    : 16/01/2017
  TIME				      : 4:53 PM
  MODIFICATIONS	    : Se cambió el estándar de codificación
                      y se agrego manejo de excepciones.


  */
  (
  UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOMOVIMIENTO           IN VARCHAR2,
  UN_TIPOMOV                  IN VARCHAR2,
  UN_MOVIMIENTO               IN PCK_SUBTIPOS.TI_ENTERO_LARGO
  )
  RETURN NUMBER
  AS
    MI_RTA                    PCK_SUBTIPOS.TI_ENTERO;
    MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
    MI_I                      PCK_SUBTIPOS.TI_ENTERO;
    MI_CONTADOR               PCK_SUBTIPOS.TI_ENTERO;
    MI_MENSAJE                VARCHAR2(32000 CHAR);
    MI_PREPARACIONAUX         VARCHAR2(32000 CHAR);
    MI_ENTREGAAUX             VARCHAR2(32000 CHAR);
    MI_INSTALACIONAUX         VARCHAR2(32000 CHAR);
    MI_COMPROBACIONAUX        VARCHAR2(32000 CHAR);
    MI_RS                     SYS_REFCURSOR;
    MI_RSS                    SYS_REFCURSOR;

  BEGIN

  EXECUTE IMMEDIATE 'DELETE FROM ERRORALMACEN WHERE COMPANIA='''||UN_COMPANIA||'''';
  MI_PREPARACIONAUX:=NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                               ,UN_NOMBRE    => 'TIPO COMPROBANTE AJUSTE PREPARACION'
                                               ,UN_MODULO    => 10
                                               ,UN_FECHA_PAR => SYSDATE),' ');
  MI_ENTREGAAUX:=NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                           ,UN_NOMBRE    => 'TIPO COMPROBANTE AJUSTE ENTREGA'
                                           ,UN_MODULO    => 10
                                           ,UN_FECHA_PAR => SYSDATE),' ');
  MI_INSTALACIONAUX:=NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                               ,UN_NOMBRE    => 'TIPO COMPROBANTE AJUSTE INSTALACION'
                                               ,UN_MODULO    => 10
                                               ,UN_FECHA_PAR => SYSDATE),' ');
  MI_COMPROBACIONAUX:=NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                                ,UN_NOMBRE    => 'TIPO COMPROBANTE AJUSTE COMPROBACION'
                                                ,UN_MODULO    => 10
                                                ,UN_FECHA_PAR =>SYSDATE),' ');

  IF UN_TIPOMOV='M' THEN
    MI_CONTADOR:=1;
    <<INSERTAR_ERRORALMACEN>>
    FOR MI_RS IN (SELECT TIPOMOVIMIENTO.GENERAPLACA
                         ,D_MOVIMIENTO.ELEMENTO
                         ,D_MOVIMIENTO.TIPOMOVIMIENTO
                         ,D_MOVIMIENTO.MOVIMIENTO
                         ,D_MOVIMIENTO.FECHA
                         ,D_MOVIMIENTO.HORA
                         ,D_MOVIMIENTO.SERIE
                  FROM    D_MOVIMIENTO
                    INNER JOIN TIPOMOVIMIENTO
                           ON  D_MOVIMIENTO.COMPANIA       = TIPOMOVIMIENTO.COMPANIA
                           AND D_MOVIMIENTO.TIPOMOVIMIENTO = TIPOMOVIMIENTO.CODIGO
                  WHERE D_MOVIMIENTO.COMPANIA = UN_COMPANIA
                    AND TIPOMOVIMIENTO        = UN_TIPOMOVIMIENTO
                    AND MOVIMIENTO            = UN_MOVIMIENTO)
    LOOP
      <<INSERTAR_ERRORALMACENF>>
      FOR MI_RSS IN ( SELECT D_MOVIMIENTO.TIPOMOVIMIENTO
                             ,D_MOVIMIENTO.MOVIMIENTO
                      FROM   D_MOVIMIENTO
                        INNER JOIN TIPOMOVIMIENTO
                               ON  D_MOVIMIENTO.COMPANIA       = TIPOMOVIMIENTO.COMPANIA
                               AND D_MOVIMIENTO.TIPOMOVIMIENTO = TIPOMOVIMIENTO.CODIGO
                      WHERE D_MOVIMIENTO.COMPANIA                   = UN_COMPANIA
                        AND TIPOMOVIMIENTO||LPAD(MOVIMIENTO,10,'0') <> UN_TIPOMOVIMIENTO||LPAD(MOVIMIENTO,10,'0')
                        AND ELEMENTO                                = MI_RS.ELEMENTO
                        AND FECHA                                  > MI_RS.FECHA
                         OR FECHA                                  = MI_RS.FECHA
                        AND TO_DATE('01/01/2001 '||TO_CHAR(HORA,'hh24:mi:ss'),'DD/MM/YYYY hh24:mi:ss') >= TO_DATE('01/01/2001 '||TO_CHAR(MI_RS.HORA,'hh24:mi:ss'),'DD/MM/YYYY hh24:mi:ss')
                        AND D_MOVIMIENTO.TIPOMOVIMIENTO NOT IN (MI_PREPARACIONAUX,MI_ENTREGAAUX,MI_INSTALACIONAUX,MI_COMPROBACIONAUX)
                      ORDER BY FECHA
                               ,HORA
                               ,CLASE
                               ,TIPOMOVIMIENTO
                               ,MOVIMIENTO
                               ,D_MOVIMIENTO.CODIGO )
      LOOP
       BEGIN
        BEGIN
        MI_MENSAJE:='El elemento '||MI_RS.ELEMENTO||' no se puede reservar porque ya fue relacionado en otro comprobante '||MI_RSS.TIPOMOVIMIENTO||' - '||MI_RSS.MOVIMIENTO;
        MI_CONTADOR:=MI_CONTADOR+1;
        MI_CAMPOS:='COMPANIA
                    , TIPOMOVIMIENTO
                    , MOVIMIENTO
                    , CODIGO
                    , MENSAJE';
        MI_VALORES := ''''||UN_COMPANIA||'''
                      ,'''||UN_TIPOMOVIMIENTO||'''
                      ,'||UN_MOVIMIENTO||'
                      ,'||MI_CONTADOR||'
                      ,'''||MI_MENSAJE||'''';
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'ERRORALMACEN'
                                               ,UN_ACCION  => 'I'
                                               ,UN_CAMPOS  => MI_CAMPOS
                                               ,UN_VALORES => MI_VALORES);

         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
        END;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
               PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                        ,UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_INSERTERRORALMACEN
          );
       END;

      END LOOP INSERTAR_ERRORALMACENF;

      IF MI_RS.GENERAPLACA NOT IN (0) THEN

        SELECT COUNT(SERIE)
        INTO   MI_I
        FROM   D_MOVIMIENTO
        WHERE  COMPANIA = UN_COMPANIA
          AND  ELEMENTO = MI_RS.ELEMENTO
          AND  SERIE    = MI_RS.SERIE
          AND  IND_REG  NOT IN (0);

        IF MI_I>1 THEN
         BEGIN
          BEGIN
          MI_MENSAJE:='El elemento '||MI_RS.ELEMENTO||' no se puede reservar porque la placa '||MI_RS.SERIE||' ya fue relacionada en otro comprobante';
          MI_CONTADOR:=MI_CONTADOR+1;
          MI_CAMPOS:='COMPANIA
                      , TIPOMOVIMIENTO
                      , MOVIMIENTO
                      , CODIGO
                      , MENSAJE';

          MI_VALORES := ''''||UN_COMPANIA||'''
                        ,'''||UN_TIPOMOVIMIENTO||'''
                        ,'||UN_MOVIMIENTO||'
                        ,'||MI_CONTADOR||'
                        ,'''||MI_MENSAJE||'''';

          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'ERRORALMACEN'
                                                 ,UN_ACCION  => 'I'
                                                 ,UN_CAMPOS  => MI_CAMPOS
                                                 ,UN_VALORES => MI_VALORES);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
          END;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
               PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                        ,UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_INSERTERRORALMACEN
               );
         END;
        END IF;

      END IF;
    END LOOP INSERTAR_ERRORALMACEN;

    IF MI_CONTADOR>1 THEN
      MI_RTA:=0;
    ELSE
      MI_RTA:=-1;
    END IF;

  ELSE
    MI_RTA:=-1;
  END IF;

  RETURN MI_RTA;

END FC_REVISASIHAYMOVIMIENTO;

FUNCTION FC_DATOSBODEGAH
    /*
   NAME 			    : DATOSBODEGAH ->
   AUTHORS 			  : JAVIER RICARDO VILLATE
   DATE MIGRADOR	: 09/02/2016
   TIME				    : 14:45
   MODULO ORIGEN	: ALMACÉN
   DESCRIPTION		: FUNCIÓN QUE RETORNA EL CODIGO DE LA DEPENDENCIA QUE TIENE ASOCIADA LA CLASE DE BODEGA QUE ESTA DEFINIDA
                    CON EL INDICADOR DE PRINCIPAL EN LA TABLA DEPENDENCIA
   MODIFIER			  : ELKIN GEOVANNY AMAYA SILVA
   DATE MODIFIED	: 16/01/2017
   TIME				    : 5:45 PM
   MODIFICATIONS	: Se cambió el estándar de codificación
                    y se agrego manejo de excepciones.

  */
  (
  UN_COMPANIA               IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_OPCION                 IN PCK_SUBTIPOS.TI_ENTERO
  )
  RETURN VARCHAR2
  AS
  MI_DEPENDENCIA      		  VARCHAR2(12 CHAR);

  BEGIN
    BEGIN
      SELECT DEPENDENCIA.CODIGO
        INTO MI_DEPENDENCIA
        FROM CLASE_BODEGA
      INNER JOIN DEPENDENCIA
        ON CLASE_BODEGA.CODIGO = DEPENDENCIA.CLASE_BODEGA
       WHERE DEPENDENCIA.COMPANIA     = UN_COMPANIA
         AND DEPENDENCIA.CLASE_BODEGA = DECODE(UN_OPCION,1,'20',DECODE(UN_OPCION,2,'40',DECODE(UN_OPCION,3,'50',DECODE(UN_OPCION,4,'90',DECODE(UN_OPCION,5,'10',
                                        DECODE(UN_OPCION,6,'30',DECODE(UN_OPCION,7,'60',DECODE(UN_OPCION,8,'70',DECODE(UN_OPCION,9,'80',DECODE(UN_OPCION,10,'99',''))))))))))
         AND DEPENDENCIA.PRINCIPAL    NOT IN (0)
         AND ROWNUM = 1;

      EXCEPTION WHEN NO_DATA_FOUND THEN
      RETURN '';
    END;

  RETURN MI_DEPENDENCIA;

END FC_DATOSBODEGAH;


FUNCTION FC_ALCAMBIAR_VLRDMOV
    /*
   NAME 			    : FC_ALCAMBIAR_VLRDMOV -> Valor_AfterUpdate FORMULARIO SUBD_MOVIMIENTO
   AUTHORS 			  : SANDRA MILENA DAZA LEGUIZAMON
   DATE MIGRADOR	: 11/02/2016
   TIME				    : 08:42
   MODULO ORIGEN	: ALMACÉN
   DESCRIPTION		: PROCEDIMIENTO QUE EJECUTA EVENTO DESPUES DE ACTUALIZAR EL VALOR DEL DETALLE DE UN MOVIMIENTO
   MODIFIER			  : ELKIN GEOVANNY AMAYA SILVA
   DATE MODIFIED	: 17/01/2017
   TIME				    : 8:32 AM
   MODIFICATIONS	: Se cambió el estándar de codificación
                    y se agrego manejo de excepciones.

  */
(
  UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOMOV    IN VARCHAR2,
  UN_NUMMOV     IN PCK_SUBTIPOS.TI_ENTERO_LARGO
)
RETURN VARCHAR2
AS
  MI_DEP_VLRLIBROS        PCK_SUBTIPOS.TI_ENTERO;
  MI_DEP_SERIE            PCK_SUBTIPOS.TI_ENTERO;
  MI_DEP_ELEMENTO         PCK_SUBTIPOS.TI_ENTERO;
  MI_DEP_ULTPERIODO       DATE;
  MI_RTA                  VARCHAR2(3000 CHAR);
BEGIN

  <<EXTRAER_MOVIMIENTOS>>
  FOR RSDMOV IN (
            SELECT    CODIGO
                      , VALORUNITARIO
                      , MAX(FECHA) UFECHA
                      , MAX(HORA) UHORA, SERIE
                      , ELEMENTO
            FROM      D_MOVIMIENTO
            WHERE     COMPANIA        = UN_COMPANIA
              AND     TIPOMOVIMIENTO  = UN_TIPOMOV
              AND     MOVIMIENTO      = UN_NUMMOV
              AND     IND_REG         IN (0)
            GROUP BY  CODIGO
                      , VALORUNITARIO
                      , SERIE
                      , ELEMENTO
          )LOOP
    BEGIN
      SELECT    ELEMENTO
                ,SERIE
                ,MAX(PERIODO) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) ULTPERIODO
                ,MAX(VLRLIBROS) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) ULTVLRLIBROS
      INTO      MI_DEP_ELEMENTO
                ,MI_DEP_SERIE
                ,MI_DEP_ULTPERIODO
                ,MI_DEP_VLRLIBROS
      FROM      DEPRECIAR
      WHERE     COMPANIA = UN_COMPANIA
        AND     SERIE    = RSDMOV.SERIE
      GROUP BY  ELEMENTO
                , SERIE;

      EXCEPTION WHEN OTHERS THEN
        MI_RTA :='No se logro establer el valor en libros para la placa.';
    END;

    IF MI_DEP_VLRLIBROS = 0 AND RSDMOV.VALORUNITARIO < MI_DEP_VLRLIBROS THEN
      MI_RTA := 'No se puede realizar correción de valor a la placa ' || MI_DEP_SERIE || ', debido a que su valor en libros es menor al que se desea asignar';
    ELSIF MI_DEP_VLRLIBROS > 0 AND (RSDMOV.VALORUNITARIO < MI_DEP_VLRLIBROS AND ((RSDMOV.VALORUNITARIO + MI_DEP_VLRLIBROS) < 0)) THEN
      MI_RTA := 'No se puede realizar correción de valor a la placa ' || MI_DEP_SERIE || ', debido a que su valor en libros es menor al que se desea asignar';
    END IF;
  END LOOP EXTRAER_MOVIMIENTOS;
END FC_ALCAMBIAR_VLRDMOV;


PROCEDURE PR_REVISARPEPS
  /*
  NAME              : REVISARPEPS En Access --> RevisarPEPs
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : JAVIER VILLATE
  DATE MIGRADOR     : 22/02/2016
  TIME              : 03:00 PM
  SOURCE MODULE     : ALMACEN
  DESCRIPTION       : PROCEDIMIENTO QUE ACTUALIZA EL CAMPO SALDO_PEPS,VLRUNITARIOPROM_PEPS,VALORTOTAL_PEPS DE LA TABLA D_MOVIMIENTO EL CUAL LLEVA EL VALOR DEL SALDO DE LAS EXISTENCIAS DE LOS ELEMENTOS DE CONSUMO.
                    : ESTE PROCEDIMIENTO SOLO ES LLAMADO EN LOS MOVIMIENTOS DE SALIDA PARA ELEMENTOS DE CONSUMO.
                    : EL SALDO QUE VA ACTUALIZANDO ES EL DEL PRIMER MOVIMIENTO DE ENTRADA DEL ELEMENTO, CUANDO EL SALDO SE AGOTA TOMA EL VALOR DE LA SIGUIENTE ENTRADA.
  MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
  DATE MODIFIED	    : 17/01/2017
  TIME				      : 9:22 AM
  MODIFICATIONS	    : Se cambió el estándar de codificación
                      y se agrego manejo de excepciones

  */
  (
  UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_FECHAINICIAL             IN DATE,
  UN_FECHAFINAL               IN DATE,
  UN_ELEMENTOINICIAL          IN VARCHAR2,
  UN_SALDOQ                   IN PCK_SUBTIPOS.TI_DOBLE,
  UN_TIPOMOVIMIENTO_AFECT     IN VARCHAR2,
  UN_MOVIMIENTO_AFECT         IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_CLASE                    IN VARCHAR2,
  UN_TIPOELEMENTO             IN VARCHAR2									 
  )

  AS
    MI_RTA                    CLOB;
    MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
    MI_RS                     SYS_REFCURSOR;
    MI_CONDI                  VARCHAR2(1000);
    MI_RSCONSULTA             PCK_SUBTIPOS.TI_STRSQL;
    MI_RSCOMPANIA             D_MOVIMIENTO.COMPANIA%TYPE;
    MI_RSCODIGOELEMENTO       INVENTARIO.CODIGOELEMENTO%TYPE;
    MI_RSTIPOMOVIMIENTO       D_MOVIMIENTO.TIPOMOVIMIENTO%TYPE;
    MI_RSMOVIMIENTO           D_MOVIMIENTO.MOVIMIENTO%TYPE;
    MI_RSCODIGO               D_MOVIMIENTO.CODIGO%TYPE;
    MI_RSVALORUNITARIO        D_MOVIMIENTO.VALORUNITARIO%TYPE;
    MI_RSVLRUNITARIOPROM      D_MOVIMIENTO.VLRUNITARIOPROM%TYPE;
    MI_RSVALORTOTAL           D_MOVIMIENTO.VALORTOTAL%TYPE;
    MI_RSSALDO_PEPS           D_MOVIMIENTO.SALDO_PEPS%TYPE;													   

  BEGIN

   EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS=''.,''';
   IF(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'MANEJA PEPS CONSUMO DE ALMACEN IDIPRON',10,SYSDATE),'NO')='SI' AND UN_CLASE='S' AND UN_TIPOELEMENTO='C') THEN
        MI_CONDI := 'AND D_MOVIMIENTO.TIPOMOVIMIENTO = '''||UN_TIPOMOVIMIENTO_AFECT||'''
                     AND D_MOVIMIENTO.MOVIMIENTO = '||UN_MOVIMIENTO_AFECT||'';
   ELSE  
        MI_CONDI := '';
   END IF;

	<<ACTUALIZAR_D_MOVIMIENTO>>
  MI_RSCONSULTA := 'SELECT *
                    FROM
                        (SELECT D_MOVIMIENTO.COMPANIA
                               ,INVENTARIO.CODIGOELEMENTO
                               ,D_MOVIMIENTO.TIPOMOVIMIENTO
                               ,D_MOVIMIENTO.MOVIMIENTO
                               ,D_MOVIMIENTO.CODIGO
                               ,D_MOVIMIENTO.VALORUNITARIO
                               ,D_MOVIMIENTO.VLRUNITARIOPROM
                               ,D_MOVIMIENTO.VALORTOTAL
                               ,D_MOVIMIENTO.SALDO_PEPS
                         FROM INVENTARIO
                         INNER JOIN D_MOVIMIENTO
                            ON INVENTARIO.COMPANIA       = D_MOVIMIENTO.COMPANIA
                            AND INVENTARIO.CODIGOELEMENTO = D_MOVIMIENTO.ELEMENTO
                         INNER JOIN TIPOMOVIMIENTO
                            ON D_MOVIMIENTO.COMPANIA       = TIPOMOVIMIENTO.COMPANIA
                            AND D_MOVIMIENTO.TIPOMOVIMIENTO = TIPOMOVIMIENTO.CODIGO
                         WHERE INVENTARIO.COMPANIA           = '''||UN_COMPANIA||'''
                            AND INVENTARIO.CODIGOELEMENTO   = '||UN_ELEMENTOINICIAL||'
                            AND INVENTARIO.TIPO             IN (''C'')
                            AND TIPOMOVIMIENTO.CLASE        IN (''E'')
                            AND TIPOMOVIMIENTO.TIPOELEMENTO IN (''C'')
                            AND D_MOVIMIENTO.SALDO_PEPS     > 0
                            AND D_MOVIMIENTO.FECHA          BETWEEN '''||UN_FECHAINICIAL||''' AND '''||UN_FECHAFINAL||'''
                            AND INVENTARIO.TIENEMOVIMIENTO  NOT IN (0)
                            AND INVENTARIO.INACTIVO         IN (0) '||MI_CONDI||'
                         ORDER BY D_MOVIMIENTO.COMPANIA
                            ,INVENTARIO.CODIGOELEMENTO
                            ,D_MOVIMIENTO.FECHA
                            ,D_MOVIMIENTO.HORA
                        )
                        WHERE ROWNUM <= 1';
  OPEN MI_RS FOR MI_RSCONSULTA;
  LOOP
    FETCH MI_RS INTO MI_RSCOMPANIA,MI_RSCODIGOELEMENTO,MI_RSTIPOMOVIMIENTO,MI_RSMOVIMIENTO,MI_RSCODIGO,MI_RSVALORUNITARIO,MI_RSVLRUNITARIOPROM,MI_RSVALORTOTAL,MI_RSSALDO_PEPS;
    EXIT WHEN MI_RS%NOTFOUND;  									   
    --MI_CAMPOS:= 'SALDO_PEPS='||(NVL(MI_RS.SALDO_PEPS, 0)-UN_SALDOQ)||', VALORSALDO_PEPS='||((NVL(MI_RS.SALDO_PEPS, 0)-UN_SALDOQ) * MI_RS.VALORUNITARIO)||', VALORTOTAL_PEPS='||MI_RS.VALORTOTAL||', VLRUNITARIOPROM_PEPS='||MI_RS.VLRUNITARIOPROM;
    --MI_CONDICION:='COMPANIA='''||UN_COMPANIA||''' AND TIPOMOVIMIENTO='''||MI_RS.TIPOMOVIMIENTO||''' AND MOVIMIENTO='||MI_RS.MOVIMIENTO||' AND CODIGO='||MI_RS.CODIGO||' AND ELEMENTO='''||MI_RS.CODIGOELEMENTO||'''';
    --PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ('D_MOVIMIENTO', 'M', MI_CAMPOS, NULL, NULL,MI_CONDICION, NULL, NULL, NULL, NULL);
   BEGIN
    BEGIN
    MI_CAMPOS:= 'SALDO_PEPS             = '||(NVL(MI_RSSALDO_PEPS, 0)-UN_SALDOQ)||'
                 , VALORSALDO_PEPS      = '||((NVL(MI_RSSALDO_PEPS, 0)-UN_SALDOQ) * MI_RSVALORUNITARIO)||'
                 , VALORTOTAL_PEPS      = '||MI_RSVALORTOTAL||'
                 , VLRUNITARIOPROM_PEPS = '||MI_RSVALORUNITARIO;

    MI_CONDICION:='COMPANIA       ='''||UN_COMPANIA||'''
               AND TIPOMOVIMIENTO ='''||MI_RSTIPOMOVIMIENTO||'''
               AND MOVIMIENTO     ='||MI_RSMOVIMIENTO||'
               AND CODIGO         ='||MI_RSCODIGO||'
               AND ELEMENTO       ='''||MI_RSCODIGOELEMENTO||'''';

    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'D_MOVIMIENTO'
                                           ,UN_ACCION    => 'M'
                                           ,UN_CAMPOS    => MI_CAMPOS
                                           ,UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;

    END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE
                      ,UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_UPDATE_DMOV
          );
    END;
 END LOOP ACTUALIZAR_D_MOVIMIENTO;

END PR_REVISARPEPS;

FUNCTION FC_INSERTARDETALLES
  /*
  NAME              : PR_INSERTARDETALLES En Access --> NO EXISTE
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : JAVIER VILLATE
  DATE MIGRADOR     : 23/02/2016
  TIME              : 08:00 PM
  SOURCE MODULE     : ALMACEN
  DESCRIPTION       : INSERTA LOS DETALLES SEGÚN LOS SALDOS DE LAS ENTRADAS EXISTENTES CON EL MÉTODO PEPS, BUSCARA LOS SALDOS DEL CAMPO SALDO_PEPS SEGÚN LA FECHA
                    : Y LA HORA DE ENTRADA EN ORDEN ASCENDENTE
  MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
  DATE MODIFIED	    : 17/01/2017
  TIME				      : 10:01 AM
  MODIFICATIONS	    : Se cambió el estándar de codificación
                      y se agrego manejo de excepciones

  */
  (
  UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_FECHAINICIAL             IN DATE,
  UN_FECHAFINAL               IN DATE,
  UN_ELEMENTOINICIAL          IN VARCHAR2,
  UN_CANTIDAD                 IN PCK_SUBTIPOS.TI_DOBLE,
  UN_CODIGO                   IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_SERIE                    IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_TIPOMOVIMIENTO           IN VARCHAR2,
  UN_MOVIMIENTO               IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_ESPECIFICACION           IN VARCHAR2,
  UN_TERCERO                  IN VARCHAR2,
  UN_SUCURSAL                 IN VARCHAR2,
  UN_VALORUNITARIO            IN PCK_SUBTIPOS.TI_DOBLE,
  UN_VALORTOTAL               IN PCK_SUBTIPOS.TI_DOBLE,
  UN_PORCIVA                  IN PCK_SUBTIPOS.TI_DOBLE,
  UN_PORCIMPCONSUMO           IN PCK_SUBTIPOS.TI_DOBLE,
  UN_VALORUNITARIO_ANTESIVA   IN PCK_SUBTIPOS.TI_DOBLE,
  UN_CENTRODECOSTO            IN VARCHAR2,
  UN_TIPOMOVIMIENTO_AFECT     IN VARCHAR2,
  UN_MOVIMIENTO_AFECT         IN PCK_SUBTIPOS.TI_ENTERO_LARGO,										 
  UN_CODIGO_AFECT             IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_FUENTER                  IN D_MOVIMIENTO.FUENTEDERECURSO%TYPE,
  UN_REFERENCIA               IN D_MOVIMIENTO.REFERENCIA_CNT%TYPE,
  UN_AUXILIAR                 IN D_MOVIMIENTO.AUXILIAR%TYPE,
  UN_PROYECTO                 IN D_MOVIMIENTO.CODIGOPROYECTO%TYPE,
  UN_LOTE                     IN D_MOVIMIENTO.LOTE%TYPE
  )
  RETURN VARCHAR2
  AS
    MI_RTA                    VARCHAR2(32000 CHAR);
    MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
    MI_CODIGO                 PCK_SUBTIPOS.TI_ENTERO;
    MI_IND_REG                PCK_SUBTIPOS.TI_ENTERO;
    MI_CANTIDADANT            PCK_SUBTIPOS.TI_DOBLE:=0;
    MI_EXISTENCIA             PCK_SUBTIPOS.TI_DOBLE:=0;
    MI_STRELEMENTO            VARCHAR2(32000 CHAR);
    MI_SALDOPEPS              PCK_SUBTIPOS.TI_DOBLE:=0;
    MI_CLASE                  VARCHAR2(2 CHAR);
    MI_TIPOELEMENTO           VARCHAR2(2 CHAR);
    MI_CONCEPTO               VARCHAR2(5 CHAR);
    MI_GENERAPLACA            PCK_SUBTIPOS.TI_DOBLE;
    MI_FECHA                  DATE;
    MI_HORA                   DATE;
    MI_INVENTARIOINICIAL      PCK_SUBTIPOS.TI_DOBLE;
    MI_CLASEDOCASOCIADO       VARCHAR2(10 CHAR);
    MI_DEPENDENCIA_DESTINO    VARCHAR2(20 CHAR);
    MI_RESPONSABLE_DESTINO    VARCHAR2(20 CHAR);
    MI_SUCURSAL_RESDESTINO    VARCHAR2(3 CHAR);
    MI_BODEGA_DESTINO         VARCHAR2(20 CHAR);
    MI_CLASE_BODEGA_DESTINO   VARCHAR2(20 CHAR);
    MI_CLASE_BODEGA_ORIGEN    VARCHAR2(2 CHAR);
    MI_TIPOPERSONA            VARCHAR2(3 CHAR);
    MI_VALORIVA               PCK_SUBTIPOS.TI_ENTERO:=0;
    MI_PORCIMPCONSUMO         D_MOVIMIENTO.PORC_IMPCONSUMO%TYPE;
    MI_RS                     SYS_REFCURSOR;
	MI_CONDI                  VARCHAR2(1000);
    MI_RSCONSULTA             PCK_SUBTIPOS.TI_STRSQL;
    MI_RSCOMPANIA             D_MOVIMIENTO.COMPANIA%TYPE;
    MI_RSCODIGOELEMENTO       INVENTARIO.CODIGOELEMENTO%TYPE;
    MI_RSTIPO                 INVENTARIO.TIPO%TYPE;
    MI_RSPRECIOULTCOMPRA      INVENTARIO.PRECIOULTCOMPRA%TYPE;
    MI_RSCLASE                TIPOMOVIMIENTO.CLASE%TYPE;
    MI_RSTIPOELEMENTO         TIPOMOVIMIENTO.TIPOELEMENTO%TYPE;
    MI_RSTIPOMOVIMIENTO       D_MOVIMIENTO.TIPOMOVIMIENTO%TYPE;
    MI_RSMOVIMIENTO           D_MOVIMIENTO.MOVIMIENTO%TYPE;
    MI_RSCODIGO               D_MOVIMIENTO.CODIGO%TYPE;
    MI_RSCANTIDAD             D_MOVIMIENTO.CANTIDAD%TYPE;
    MI_RSVALORUNITARIO        D_MOVIMIENTO.VALORUNITARIO%TYPE;
    MI_RSVALORTOTAL           D_MOVIMIENTO.VALORTOTAL%TYPE;
    MI_RSSALDO_PEPS           D_MOVIMIENTO.SALDO_PEPS%TYPE;
    MI_RSVLRUNITARIO_ANTESIVA D_MOVIMIENTO.VLRUNITARIO_ANTESIVA%TYPE;
    MI_RSPORCIVA              D_MOVIMIENTO.PORCIVA%TYPE;
    MI_RSFECHA                D_MOVIMIENTO.FECHA%TYPE;
    MI_RSHORA                 D_MOVIMIENTO.HORA%TYPE;
    MI_RSIDENTIFICADOR        D_MOVIMIENTO.IDENTIFICADOR%TYPE;
    MI_RSESTADO               D_MOVIMIENTO.ESTADO%TYPE;								   
	MI_RSCODPROYECTO          D_MOVIMIENTO.CODIGOPROYECTO%TYPE;
    MI_RSTIPOMOVASOCIADO      D_MOVIMIENTO.TIPOMOVASOCIADO%TYPE;
    MI_RSMOVASOCIADO          D_MOVIMIENTO.MOVASOCIADO%TYPE;
    MI_RSOPERACION            D_MOVIMIENTO.OPERACION%TYPE;
    MI_RSAUX_ALMACEN          D_MOVIMIENTO.AUX_ALMACEN%TYPE;
    MI_RSRUEDA                D_MOVIMIENTO.RUEDA%TYPE;	
    MI_UBICACION              MOVIMIENTO.UBICACION%TYPE;	
    MI_CENTROCOSTO            MOVIMIENTO.CENTRODECOSTO%TYPE;
    MI_REFERENCIA             MOVIMIENTO.REFERENCIA%TYPE;
    MI_REDONDEO               PCK_SUBTIPOS.TI_ENTERO; --JM 19/11/2024 7801979
    MI_PARAMETRO              VARCHAR2(3);
	MI_BODEGA_ORIGEN          VARCHAR2(20 CHAR);	

	--CC_2968(29/12/2025 JCROJAS)
	MIPARFECHASV              PCK_SUBTIPOS.TI_PARAMETRO;
    MIPARINICIAFECHASV        PCK_SUBTIPOS.TI_PARAMETRO;													
    --PRAGMA AUTONOMOUS_TRANSACTION;
	--CC_4063_ALMACEN(28/04/2026 JCROJAS)
    MI_CANTREG                PCK_SUBTIPOS.TI_ENTERO;	
    --CC_4264_ALMACEN(25/05/2026 NCARDENAS)
     MI_PARANULACIONCOMODATO   PARAMETRO.VALOR%TYPE;  
   MI_PERMITE               BOOLEAN;    											 
BEGIN
--JM 19/11/2024 7801979
    MI_REDONDEO := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                  UN_NOMBRE    => 'REDONDEO VALOR',
                                                  UN_MODULO    => '10',
                                                  UN_FECHA_PAR => SYSDATE),'2'));
	--CC_2968(29/12/2025 JCROJAS)
	MIPARFECHASV := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA
                                             ,UN_NOMBRE => 'INICIA DEPRECIACION EN PRIMERA SALIDA AL SERVICIO'
                                             ,UN_MODULO => PCK_DATOS.MODULOALMACEN
                                             ,UN_FECHA_PAR => SYSDATE),'NO');
                                             
    MIPARINICIAFECHASV := TO_DATE(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA
                                                       ,UN_NOMBRE => 'FECHA INICIO CALCULO FECHA DEPRECIACION CON SALIDA AL SERVICIO'
                                                       ,UN_MODULO => PCK_DATOS.MODULOALMACEN
                                                       ,UN_FECHA_PAR => SYSDATE),'DD/MM/YYYY');																  
--EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS=''.,''';
  SELECT TIPOMOVIMIENTO.CLASE ,
    TIPOMOVIMIENTO.TIPOELEMENTO ,
    TIPOMOVIMIENTO.CONCEPTO ,
    TIPOMOVIMIENTO.GENERAPLACA ,
    TIPOMOVIMIENTO.INVENTARIOINICIAL ,
    TIPOMOVIMIENTO.CLASEDOCASOCIADO ,
    MOVIMIENTO.FECHA ,
    MOVIMIENTO.DEPENDENCIA_DESTINO ,
    MOVIMIENTO.RESPONSABLE_DESTINO ,
    MOVIMIENTO.SUCURSAL_RESDESTINO ,
    TIPOMOVIMIENTO.TIPOPESONA ,
    MOVIMIENTO.BODEGA_DESTINO ,
    MOVIMIENTO.CLASE_BODEGA_DESTINO ,
    MOVIMIENTO.CLASE_BODEGA_ORIGEN,
    MOVIMIENTO.UBICACION,
    MOVIMIENTO.CENTRODECOSTO,
    MOVIMIENTO.REFERENCIA,
    MOVIMIENTO.BODEGA_ORIGEN 
  INTO MI_CLASE ,
    MI_TIPOELEMENTO ,
    MI_CONCEPTO ,
    MI_GENERAPLACA ,
    MI_INVENTARIOINICIAL ,
    MI_CLASEDOCASOCIADO ,
    MI_FECHA ,
    MI_DEPENDENCIA_DESTINO ,
    MI_RESPONSABLE_DESTINO ,
    MI_SUCURSAL_RESDESTINO ,
    MI_TIPOPERSONA ,
    MI_BODEGA_DESTINO ,
    MI_CLASE_BODEGA_DESTINO ,
    MI_CLASE_BODEGA_ORIGEN,
    MI_UBICACION,
    MI_CENTROCOSTO,
    MI_REFERENCIA,
    MI_BODEGA_ORIGEN
  FROM TIPOMOVIMIENTO
  INNER JOIN MOVIMIENTO
  ON TIPOMOVIMIENTO.COMPANIA    = MOVIMIENTO.COMPANIA
  AND TIPOMOVIMIENTO.CODIGO     = MOVIMIENTO.TIPOMOVIMIENTO
  WHERE TIPOMOVIMIENTO.COMPANIA = UN_COMPANIA
  AND TIPOMOVIMIENTO.CODIGO     = UN_TIPOMOVIMIENTO
  AND MOVIMIENTO.NUMERO         = UN_MOVIMIENTO;
  
  --LLAMO A GRABAPROVEEDORES SE LLAMA EN CUALQUIER MOVIMIENTO SEA DEVOLUTIVO O CONSUMO
  IF MI_TIPOPERSONA='P' AND MI_CONCEPTO <> 'D' THEN
    PCK_ALMACEN.PR_GRABAPROVEEDORESGRAL (UN_COMPANIA       => UN_COMPANIA ,
                                         UN_TIPOMOVIMIENTO => UN_TIPOMOVIMIENTO ,
                                         UN_NUMERO         => UN_MOVIMIENTO);
  END IF;
  
  --LLAMO LA FUNCION DE DOCUMENTO ASOCIADO
  IF MI_CLASE='E' AND MI_CONCEPTO='C' AND MI_CLASEDOCASOCIADO<>'O' OR MI_CONCEPTO='D' OR MI_CONCEPTO='II' THEN
    MI_RTA  :=PCK_ALMACEN_COM2.FC_ACTUALIZA_DOC_ASOCIADO(UN_COMPANIA       => UN_COMPANIA ,
                                                         UN_TIPOMOVIMIENTO => UN_TIPOMOVIMIENTO ,
                                                         UN_MOVIMIENTO     => UN_MOVIMIENTO);
  END IF;
  
  MI_HORA    := TO_DATE('30/12/1899 ' || TO_CHAR(CURRENT_DATE,'HH24:mi:ss'),'DD/MM/YYYY HH24:mi:ss');
  MI_VALORIVA:=ROUND((UN_VALORUNITARIO_ANTESIVA*(UN_PORCIVA/100))*UN_CANTIDAD,MI_REDONDEO); -- parametro redondeo JM 19/11/2024 7801979
  
  
  IF MI_CLASE='S' AND MI_TIPOELEMENTO='C' THEN
	IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'MANEJA SALDO POR BODEGA Y AUXILIARES EN ALMACEN',PCK_DATOS.MODULOALMACEN,SYSDATE),'NO')='SI' THEN 
        MI_EXISTENCIA :=PCK_ALMACEN_COM2.FC_BUSCAEXISTAUX(UN_COMPANIA => UN_COMPANIA,
                                                          UN_ELEMENTO => UN_ELEMENTOINICIAL,
                                                          UN_BODEGA => MI_BODEGA_ORIGEN,
                                                          UN_FUENTER => UN_FUENTER,
                                                          UN_REFERENCIA => UN_REFERENCIA,
                                                          UN_AUXILIAR => UN_AUXILIAR,
                                                          UN_PROYECTO => UN_PROYECTO,
                                                          UN_CENTROCOSTO => UN_CENTRODECOSTO,
                                                          UN_LOTE => UN_LOTE);
        IF MI_EXISTENCIA-NVL(UN_CANTIDAD,0)<0 THEN
          MI_RTA :='No existe saldo para realizar la salida del elemento a la fecha.';
          MI_RTA :='FALSE';
          RETURN MI_RTA;
        END IF;
    ELSIF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'MANEJA FUENTE DE RECURSOS PARA INTERFAZ ALMACEN',PCK_DATOS.MODULOALMACEN,SYSDATE),'NO') = 'NO' THEN
        MI_EXISTENCIA                      :=PCK_ALMACEN.FC_BUSCAEXISTENCIA(UN_COMPANIA => UN_COMPANIA ,UN_ELEMENTO => UN_ELEMENTOINICIAL);
        IF MI_EXISTENCIA-NVL(UN_CANTIDAD, 0)<0 THEN
          MI_RTA                           :='No existe saldo para realizar la salida del elemento a la fecha.';
          MI_RTA                           :='FALSE';
          RETURN MI_RTA;
        END IF;
    END IF;
    IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA ,UN_NOMBRE => 'MANEJA PEPS EN CONSUMO ALMACEN' ,UN_MODULO => 10 ,UN_FECHA_PAR => SYSDATE),'NO')='SI' THEN
      MI_STRELEMENTO:=UN_ELEMENTOINICIAL;
      BEGIN
        SELECT DISTINCT 'X'
        INTO MI_RTA
        FROM INVENTARIO
        INNER JOIN D_MOVIMIENTO
            ON INVENTARIO.COMPANIA        = D_MOVIMIENTO.COMPANIA
            AND INVENTARIO.CODIGOELEMENTO = D_MOVIMIENTO.ELEMENTO
        INNER JOIN TIPOMOVIMIENTO
            ON D_MOVIMIENTO.COMPANIA         = TIPOMOVIMIENTO.COMPANIA
            AND D_MOVIMIENTO.TIPOMOVIMIENTO  = TIPOMOVIMIENTO.CODIGO
        WHERE   INVENTARIO.COMPANIA         = UN_COMPANIA
        AND     INVENTARIO.CODIGOELEMENTO   = UN_ELEMENTOINICIAL
        AND     INVENTARIO.TIPO             IN ('C')
        AND     TIPOMOVIMIENTO.CLASE        IN ('E')
        AND     TIPOMOVIMIENTO.TIPOELEMENTO IN ('C')
        AND     D_MOVIMIENTO.SALDOKARDEX    > 0
        AND     D_MOVIMIENTO.FECHA          BETWEEN UN_FECHAINICIAL AND UN_FECHAFINAL
        AND     INVENTARIO.TIENEMOVIMIENTO  NOT IN (0)
        AND     INVENTARIO.INACTIVO         IN (0);
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
            MI_RTA :='no existe saldo para realizar la salida del elemento a la fecha.';
            MI_RTA :='FALSE';
        RETURN MI_RTA;
      END;
      
	  IF(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'MANEJA PEPS CONSUMO DE ALMACEN IDIPRON',10,SYSDATE),'NO')='SI' AND MI_CLASE='S' AND MI_TIPOELEMENTO='C') THEN
        MI_CONDI := 'AND D_MOVIMIENTO.TIPOMOVIMIENTO = '''||UN_TIPOMOVIMIENTO_AFECT||'''
                     AND D_MOVIMIENTO.MOVIMIENTO = '||UN_MOVIMIENTO_AFECT||'';
      ELSE  
        MI_CONDI := '';
      END IF;
      
      --BORRAR EL REGISTRO FALSO DE ENTRADA PARA PODER REALIZAR EL KARDEO
      BEGIN
        BEGIN
            MI_CONDICION     := 'COMPANIA             = ''' || UN_COMPANIA || ''' AND TIPOMOVIMIENTO   = ''' || UN_TIPOMOVIMIENTO || ''' AND MOVIMIENTO       =   ' || UN_MOVIMIENTO || '   AND IND_REG          = ' || 0 ||'';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'D_MOVIMIENTO' ,UN_ACCION => 'E' ,UN_CONDICION => MI_CONDICION);
        EXCEPTION
            WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
        END;
      EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE, UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_DELETE_DMOV );
      END;
	  
      <<INSERTAR_D_MOVIMIENTO>>
              MI_PARAMETRO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA,
									          UN_NOMBRE    => 'APLICAR IMPOCONSUMO EN ENTRADAS DE ALMACEN',
									          UN_MODULO    => '10',
									          UN_FECHA_PAR => SYSDATE),'NO');
MI_RSCONSULTA := 'SELECT D_MOVIMIENTO.COMPANIA ,
                               INVENTARIO.CODIGOELEMENTO ,
                               INVENTARIO.TIPO ,
                               INVENTARIO.PRECIOULTCOMPRA ,
                               TIPOMOVIMIENTO.CLASE ,
                               TIPOMOVIMIENTO.TIPOELEMENTO ,
                               D_MOVIMIENTO.TIPOMOVIMIENTO ,
                               D_MOVIMIENTO.MOVIMIENTO ,
                               D_MOVIMIENTO.CODIGO ,
                               D_MOVIMIENTO.CANTIDAD ,
                               D_MOVIMIENTO.VALORUNITARIO ,
                               D_MOVIMIENTO.VALORTOTAL ,
                               D_MOVIMIENTO.SALDO_PEPS ,
                               D_MOVIMIENTO.VLRUNITARIO_ANTESIVA, 
                               D_MOVIMIENTO.PORCIVA,
                               D_MOVIMIENTO.FECHA ,
                               D_MOVIMIENTO.PORC_IMPCONSUMO,
                               D_MOVIMIENTO.HORA,
                               D_MOVIMIENTO.IDENTIFICADOR,
                               D_MOVIMIENTO.ESTADO,
                               D_MOVIMIENTO.CODIGOPROYECTO,
                               D_MOVIMIENTO.TIPOMOVASOCIADO,
                               D_MOVIMIENTO.MOVASOCIADO,
                               D_MOVIMIENTO.OPERACION,
                               D_MOVIMIENTO.AUX_ALMACEN,
                               D_MOVIMIENTO.RUEDA								
                        FROM INVENTARIO
                        INNER JOIN D_MOVIMIENTO
                            ON INVENTARIO.COMPANIA        = D_MOVIMIENTO.COMPANIA
                            AND INVENTARIO.CODIGOELEMENTO = D_MOVIMIENTO.ELEMENTO
                        INNER JOIN TIPOMOVIMIENTO
                            ON D_MOVIMIENTO.COMPANIA         = TIPOMOVIMIENTO.COMPANIA
                            AND D_MOVIMIENTO.TIPOMOVIMIENTO  = TIPOMOVIMIENTO.CODIGO
                        WHERE INVENTARIO.COMPANIA = '''||UN_COMPANIA||'''
                            AND INVENTARIO.CODIGOELEMENTO = '''||UN_ELEMENTOINICIAL||'''
                            AND INVENTARIO.TIPO IN (''C'')
                            AND TIPOMOVIMIENTO.CLASE IN (''E'')
                            AND TIPOMOVIMIENTO.TIPOELEMENTO IN (''C'')
                            AND D_MOVIMIENTO.SALDO_PEPS > 0
                            AND D_MOVIMIENTO.FECHA BETWEEN '''||UN_FECHAINICIAL||''' AND '''||UN_FECHAFINAL||'''
                            AND INVENTARIO.TIENEMOVIMIENTO NOT IN (0)
                            AND INVENTARIO.INACTIVO IN (0) '||MI_CONDI||'
                        ORDER BY INVENTARIO.CODIGOELEMENTO ,
                            D_MOVIMIENTO.FECHA ,
                            D_MOVIMIENTO.HORA';
      OPEN MI_RS FOR MI_RSCONSULTA;
      LOOP
        FETCH MI_RS INTO MI_RSCOMPANIA,MI_RSCODIGOELEMENTO,MI_RSTIPO,MI_RSPRECIOULTCOMPRA,MI_RSCLASE,MI_RSTIPOELEMENTO,MI_RSTIPOMOVIMIENTO,
                         MI_RSMOVIMIENTO,MI_RSCODIGO,MI_RSCANTIDAD,MI_RSVALORUNITARIO,MI_RSVALORTOTAL,MI_RSSALDO_PEPS,MI_RSVLRUNITARIO_ANTESIVA,
                         MI_RSPORCIVA,MI_RSFECHA,MI_PORCIMPCONSUMO,MI_RSHORA,MI_RSIDENTIFICADOR,MI_RSESTADO,MI_RSCODPROYECTO,MI_RSTIPOMOVASOCIADO,MI_RSMOVASOCIADO,
                         MI_RSOPERACION, MI_RSAUX_ALMACEN, MI_RSRUEDA;	  
        EXIT WHEN MI_RS%NOTFOUND;						 
        MI_CODIGO   :=PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA => 'D_MOVIMIENTO' ,UN_CRITERIO =>'COMPANIA='''||UN_COMPANIA||''' AND TIPOMOVIMIENTO='''||UN_TIPOMOVIMIENTO||''' AND MOVIMIENTO='||UN_MOVIMIENTO||'' ,UN_CAMPO =>'CODIGO');
        MI_SALDOPEPS:= NVL(MI_RSSALDO_PEPS, 0) + MI_CANTIDADANT - NVL(UN_CANTIDAD, 0);
        BEGIN
          BEGIN
            MI_CAMPOS:=
                'COMPANIA
                ,TIPOMOVIMIENTO
                ,MOVIMIENTO
                ,CODIGO
                ,ELEMENTO
                ,CANTIDAD
                ,VALORUNITARIO
                ,VALORTOTAL
                ,VALORBASE
                ,SALDOCANT
                ,ESPECIFICACION
                ,TERCERO
                ,SUCURSAL
                ,FECHA
                ,HORA
                ,TIPOMOVIMIENTO_AFECT
                ,MOVIMIENTO_AFECT
                ,CODIGO_AFECT
                ,CANTIDAD_AFECT
                ,IND_REG
                ,PORCIVA
                ,PORC_IMPCONSUMO
                ,VLRUNITARIO_ANTESIVA
                ,VLRUNITARIOPROM_PEPS
                ,VALORTOTAL_PEPS
                ,DATE_CREATED
                ,CENTRODECOSTO
				,IDENTIFICADOR
                ,ESTADO	
                ,CODIGOPROYECTO
                ,TIPOMOVASOCIADO
                ,MOVASOCIADO
                ,OPERACION
                ,AUX_ALMACEN
                ,RUEDA';						
            MI_VALORES:=
                ''''||UN_COMPANIA||'''
                ,'''||UN_TIPOMOVIMIENTO||'''
                ,'||UN_MOVIMIENTO||'
                ,'||MI_CODIGO||'
                ,'''||UN_ELEMENTOINICIAL||'''
                ,'|| CASE
                        WHEN MI_SALDOPEPS <0 THEN
                            MI_RSSALDO_PEPS
                        ELSE
                            MI_RSSALDO_PEPS - MI_SALDOPEPS
                        END ||'
                ,'||MI_RSVALORUNITARIO||'
                ,'|| CASE
                        WHEN MI_SALDOPEPS < 0 THEN
                            MI_RSSALDO_PEPS * MI_RSVALORUNITARIO
                        ELSE
                            (MI_RSSALDO_PEPS - MI_SALDOPEPS) * MI_RSVALORUNITARIO
                        END||'
                ,'|| CASE
                        WHEN MI_SALDOPEPS < 0 THEN
                            MI_RSSALDO_PEPS * MI_RSVALORUNITARIO
                        ELSE
                            (MI_RSSALDO_PEPS - MI_SALDOPEPS) * MI_RSVALORUNITARIO
                        END||'
                ,'|| CASE
                        WHEN MI_SALDOPEPS <0 THEN
                            MI_RSSALDO_PEPS
                        ELSE
                            MI_RSSALDO_PEPS - MI_SALDOPEPS
                        END||'
                ,'''||UN_ESPECIFICACION||'''
                ,'''||UN_TERCERO||'''
                ,'''||UN_SUCURSAL||'''
                ,'||PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA => TO_CHAR(UN_FECHAFINAL,'DD/MM/YYYY') ,UN_FORMATO => 'DD/MM/YYYY')||'
                ,TO_DATE('''||TO_CHAR(MI_HORA,'DD/MM/YYYY HH24:MI:SS')||''',''DD/MM/YYYY HH24:MI:SS'')
                ,'''||MI_RSTIPOMOVIMIENTO||'''
                ,'||MI_RSMOVIMIENTO||'
                ,'''||NVL(UN_CODIGO_AFECT,MI_RSCODIGO)||'''
                ,'|| CASE
                        WHEN MI_SALDOPEPS <0 THEN
                            MI_RSSALDO_PEPS
                        ELSE
                            MI_RSSALDO_PEPS  - MI_SALDOPEPS
                        END||'
                ,'||-1||'
                ,'||MI_RSPORCIVA||'
                ,'|| CASE
                        WHEN   MI_PARAMETRO = 'SI' THEN
                            MI_PORCIMPCONSUMO
                        ELSE
                            0
                        END ||'
                ,'||MI_RSVLRUNITARIO_ANTESIVA||'
                ,'||MI_RSVALORUNITARIO||'
                ,'|| CASE
                        WHEN MI_SALDOPEPS < 0 THEN
                            MI_RSSALDO_PEPS * MI_RSVALORUNITARIO
                        ELSE
                            (MI_RSSALDO_PEPS - MI_SALDOPEPS) * MI_RSVALORUNITARIO
                        END||'
                , SYSDATE ,'''||UN_CENTRODECOSTO||'''
				,'''||MI_RSIDENTIFICADOR||'''
                ,'''||MI_RSESTADO||'''
				,'''||MI_RSCODPROYECTO||'''
                ,'''||MI_RSTIPOMOVASOCIADO||'''
                ,'||MI_RSMOVASOCIADO||'
                ,'''||MI_RSOPERACION||'''
                ,'''||MI_RSAUX_ALMACEN||'''
                ,'''||MI_RSRUEDA||'''';
				
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'D_MOVIMIENTO' ,UN_ACCION => 'I' ,UN_CAMPOS => MI_CAMPOS ,UN_VALORES => MI_VALORES);
          EXCEPTION
            WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
        END;
        EXCEPTION
            WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE, UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_INSERT_DMOVIMIENTO );
        END;
        IF MI_SALDOPEPS  <0 THEN
            MI_CANTIDADANT:= MI_CANTIDADANT + NVL(MI_RSSALDO_PEPS, 0);
            MI_HORA       :=MI_HORA         +(1/24/3600);
        END IF;
        EXIT WHEN MI_SALDOPEPS>=0;
      END LOOP INSERTAR_D_MOVIMIENTO;

      <<KARDEO>>
      FOR MI_RS IN (
        SELECT  COMPANIA ,
                TIPOMOVIMIENTO ,
                MOVIMIENTO ,
                ELEMENTO ,
                SERIE ,
                CANTIDAD ,
                VALORUNITARIO ,
                VALORTOTAL
        FROM    D_MOVIMIENTO
        WHERE   COMPANIA        = UN_COMPANIA
        AND     TIPOMOVIMIENTO  = UN_TIPOMOVIMIENTO
        AND     MOVIMIENTO      = UN_MOVIMIENTO
        AND     ELEMENTO        = MI_STRELEMENTO
        AND     IND_REG         <> 0
		AND     CODIGO          = UN_CODIGO								   
      )
      LOOP
        PCK_ALMACEN.PR_REVISARPEPS (UN_COMPANIA => UN_COMPANIA ,UN_FECHAINICIAL => UN_FECHAINICIAL ,UN_FECHAFINAL => UN_FECHAFINAL ,UN_ELEMENTOINICIAL => MI_RS.ELEMENTO ,UN_SALDOQ => NVL(MI_RS.CANTIDAD, 0) ,UN_TIPOMOVIMIENTO_AFECT => UN_TIPOMOVIMIENTO_AFECT ,UN_MOVIMIENTO_AFECT => UN_MOVIMIENTO_AFECT ,UN_CLASE => MI_CLASE ,UN_TIPOELEMENTO => MI_TIPOELEMENTO);
        MI_RTA:=PCK_ALMACEN_COM3.FC_KARDEXELEMENTOTODOSHALM (UN_COMPANIA => UN_COMPANIA ,UN_INTANOINICIAL => EXTRACT(YEAR FROM UN_FECHAFINAL) ,UN_INTMESINICIAL => EXTRACT(MONTH FROM UN_FECHAFINAL) ,UN_INTANOFINAL => EXTRACT(YEAR FROM UN_FECHAFINAL) ,UN_INTMESFINAL => EXTRACT(MONTH FROM UN_FECHAFINAL) ,UN_STRELEMENTOINICIAL => MI_RS.ELEMENTO ,UN_STRELEMENTOFINAL => MI_RS.ELEMENTO ,UN_KARDEXGENERAL =>0,
								                             UN_FECHAINICIAL => UN_FECHAINICIAL ,UN_FECHAFINAL => UN_FECHAFINAL ,UN_TIPOMOVIMIENTO => UN_TIPOMOVIMIENTO ,UN_MOVIMIENTO => UN_MOVIMIENTO);																																																																										 
      END LOOP KARDEO;
      RETURN ' ';
    ELSE
      --COLOCA EL INDICADOR DE IND_REG PARA CUALQUIER MOVIMIENTO QUE NO MANEJE PEPS
      --MI_CAMPOS:= 'IND_REG=-1'||',SALDO_PEPS= '||UN_CANTIDAD ||',VLRUNITARIOPROM_PEPS= '||UN_VALORUNITARIO ||',VALORTOTAL_PEPS= '||UN_VALORTOTAL ||',VALORSALDO_PEPS= '||UN_VALORTOTAL ||'';
      BEGIN
        BEGIN
            MI_CAMPOS   :=  'IND_REG=-1'||',VALORBASE       = '||UN_VALORTOTAL ||'';
            MI_CONDICION:=  'D_MOVIMIENTO.COMPANIA          = '''||UN_COMPANIA ||'''
                         AND D_MOVIMIENTO.TIPOMOVIMIENTO    = '''||UN_TIPOMOVIMIENTO||'''
                         AND D_MOVIMIENTO.MOVIMIENTO        = '||UN_MOVIMIENTO||'
                         AND D_MOVIMIENTO.CODIGO            ='||UN_CODIGO||'';
            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'D_MOVIMIENTO' ,UN_ACCION => 'M' ,UN_CAMPOS => MI_CAMPOS ,UN_CONDICION => MI_CONDICION);
        EXCEPTION
            WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
        END;
      EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE, UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_UPDATE_DMOV );
      END;
    END IF;
  ELSE
    --LLAMO A GRABARDEVOLUTIVO
    IF MI_CONCEPTO          = 'C'OR (MI_CONCEPTO = 'T' AND MI_GENERAPLACA=-1) OR (MI_CONCEPTO = 'CM' AND MI_GENERAPLACA=-1) OR MI_CONCEPTO = 'P'OR MI_INVENTARIOINICIAL=-1 THEN        
        IF MI_TIPOELEMENTO    = 'D' THEN
            MI_RTA             := PCK_ALMACEN.FC_GRABADEVOLUTIVOS(UN_COMPANIA => UN_COMPANIA ,UN_TIPOMOVIMIENTO => UN_TIPOMOVIMIENTO ,UN_NUMERO => UN_MOVIMIENTO ,UN_TIPO => 'D' ,UN_DEPENDENCIA_DESTINO => MI_DEPENDENCIA_DESTINO ,UN_RESPONSABLE_DESTINO => MI_RESPONSABLE_DESTINO ,UN_SUCURSAL_RESPONSABLE =>MI_SUCURSAL_RESDESTINO ,UN_INVENTARIO_INICIAL => MI_INVENTARIOINICIAL ,UN_FECHA =>MI_FECHA);
        ELSIF MI_TIPOELEMENTO = 'E' THEN
            MI_RTA             := PCK_ALMACEN.FC_GRABADEVOLUTIVOS(UN_COMPANIA => UN_COMPANIA ,UN_TIPOMOVIMIENTO => UN_TIPOMOVIMIENTO ,UN_NUMERO => UN_MOVIMIENTO ,UN_TIPO => 'E' ,UN_DEPENDENCIA_DESTINO => MI_DEPENDENCIA_DESTINO ,UN_RESPONSABLE_DESTINO => MI_RESPONSABLE_DESTINO ,UN_SUCURSAL_RESPONSABLE => MI_SUCURSAL_RESDESTINO ,UN_INVENTARIO_INICIAL => MI_INVENTARIOINICIAL ,UN_FECHA =>MI_FECHA);
        ELSIF MI_TIPOELEMENTO = 'M' THEN
            MI_RTA             := PCK_ALMACEN.FC_GRABADEVOLUTIVOS(UN_COMPANIA => UN_COMPANIA ,UN_TIPOMOVIMIENTO => UN_TIPOMOVIMIENTO ,UN_NUMERO => UN_MOVIMIENTO ,UN_TIPO => 'M' ,UN_DEPENDENCIA_DESTINO => MI_DEPENDENCIA_DESTINO ,UN_RESPONSABLE_DESTINO => MI_RESPONSABLE_DESTINO ,UN_SUCURSAL_RESPONSABLE => MI_SUCURSAL_RESDESTINO ,UN_INVENTARIO_INICIAL => MI_INVENTARIOINICIAL ,UN_FECHA => MI_FECHA);
        ELSIF MI_TIPOELEMENTO = 'N' THEN
            MI_RTA             := PCK_ALMACEN.FC_GRABADEVOLUTIVOS(UN_COMPANIA => UN_COMPANIA ,UN_TIPOMOVIMIENTO => UN_TIPOMOVIMIENTO ,UN_NUMERO => UN_MOVIMIENTO ,UN_TIPO => 'N' ,UN_DEPENDENCIA_DESTINO => MI_DEPENDENCIA_DESTINO ,UN_RESPONSABLE_DESTINO => MI_RESPONSABLE_DESTINO ,UN_SUCURSAL_RESPONSABLE => MI_SUCURSAL_RESDESTINO ,UN_INVENTARIO_INICIAL => MI_INVENTARIOINICIAL ,UN_FECHA => MI_FECHA);
        END IF;
    END IF;
    
    IF MI_TIPOELEMENTO = 'D' AND (MI_CLASE ='E' AND MI_CONCEPTO = 'D' OR MI_CONCEPTO = 'R' OR (MI_CONCEPTO = 'CM' AND MI_GENERAPLACA=0)) THEN
        <<ACTUALIZAR_DEVOLUTIVO>>
        FOR MI_RS IN(
            SELECT  ELEMENTO ,
                    SERIE ,
                    HORA 
            FROM D_MOVIMIENTO
            WHERE COMPANIA     = UN_COMPANIA
            AND TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
            AND MOVIMIENTO     = UN_MOVIMIENTO
        )
        LOOP
            BEGIN
                BEGIN
                    -- AMONROY (29/11/2018) Se adiciona envio del campo SUCURSAL_RESPONSABLE
                    MI_CAMPOS        := 'DEPENDENCIA     = '''||(MI_DEPENDENCIA_DESTINO)||'''
                                        ,RESPONSABLE     = '''||(MI_RESPONSABLE_DESTINO)||'''
                                        ,SUCURSAL_RESPONSABLE = ''' || MI_SUCURSAL_RESDESTINO || '''
                                        ,FECHAENTRADA    = '||PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA => MI_FECHA)||'
                                        ,FECHAULTMOV     = '||PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA => MI_FECHA)||'
                                        ,TIPOMOVIMIENTOF = '''||UN_TIPOMOVIMIENTO||'''
                                        ,MOVIMIENTOF     = '''||UN_MOVIMIENTO||'''
                                        ,HORA            = '||PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA => MI_RS.HORA+(2*(1/24/3600)));
                    MI_CONDICION     :='    COMPANIA ='''||UN_COMPANIA||'''
                                        AND ELEMENTO ='''||MI_RS.ELEMENTO||'''
                                        AND SERIE    =  '||MI_RS.SERIE||'';
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'DEVOLUTIVO' ,UN_ACCION => 'M' ,UN_CAMPOS => MI_CAMPOS ,UN_CONDICION => MI_CONDICION);
                EXCEPTION
                    WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                END;
            EXCEPTION
                WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE, UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_UPDATE_DEVOLUTIVO );
            END;
        END LOOP ACTUALIZAR_DEVOLUTIVO;
    ELSIF MI_CLASE ='S' THEN
        BEGIN
            BEGIN
				--CC_4063_ALMACEN(28/04/2026 JCROJAS)
				BEGIN
                    SELECT COUNT(0) CANTREG INTO MI_CANTREG
                    FROM D_MOVIMIENTO
                    INNER JOIN TIPOMOVIMIENTO 
                    ON D_MOVIMIENTO.COMPANIA = TIPOMOVIMIENTO.COMPANIA
                    AND D_MOVIMIENTO.TIPOMOVIMIENTO = TIPOMOVIMIENTO.CODIGO
                    WHERE D_MOVIMIENTO.COMPANIA = UN_COMPANIA
                        AND D_MOVIMIENTO.ELEMENTO = UN_ELEMENTOINICIAL
                        AND D_MOVIMIENTO.SERIE = UN_SERIE
                        AND TIPOMOVIMIENTO.CLASE = 'S'
                        AND D_MOVIMIENTO.CODIGO <> UN_CODIGO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_CANTREG := 0;
                END;		
				--CC_2968(29/12/2025 JCROJAS): Se agrega el campo PRIMSALIDASERVICIO
				--CC_4063_ALMACEN(28/04/2026 JCROJAS): Se agrega validacion con variable MI_CANTREG																				   
                MI_CAMPOS        := 'DEPENDENCIA           = '''||MI_DEPENDENCIA_DESTINO||'''
                                    ,RESPONSABLE           = '''||MI_RESPONSABLE_DESTINO ||'''
                                    ,SUCURSAL_RESPONSABLE  = '''||MI_SUCURSAL_RESDESTINO||'''
                                    ,FECHASERVICIO         = '||PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA => MI_FECHA)||'
                                    ,FECHASALIDASERVICIO   = '||PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA => MI_FECHA)||'
									,PRIMSALIDASERVICIO    = CASE WHEN (PRIMSALIDASERVICIO IS NULL OR '||MI_CANTREG||'=0)
                                                                    AND '''||MIPARFECHASV||''' = ''SI''
                                                                    AND '''||MIPARINICIAFECHASV||''' IS NOT NULL
                                                                    AND FECHAADQUISICION >= '''||MIPARINICIAFECHASV||'''
                                                                THEN '||PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA => MI_FECHA)||'
                                                                ELSE PRIMSALIDASERVICIO 
                                                             END												   
                                    ,BODEGA                ='''||MI_BODEGA_DESTINO||'''
                                    ,CLASE_BODEGA          ='''||MI_CLASE_BODEGA_DESTINO||'''
                                    ,UBICACION             ='''||MI_UBICACION||''' 
                                    ,CENTRODECOSTO         ='''||MI_CENTROCOSTO||'''
                                    ,REFERENCIA_CNT        ='''||MI_REFERENCIA||''' ';
                                    
                MI_CONDICION     :='COMPANIA ='''||UN_COMPANIA||'''
                                AND ELEMENTO ='''||UN_ELEMENTOINICIAL||'''
                                AND SERIE    =  '||UN_SERIE||'';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'DEVOLUTIVO' 
                                                      ,UN_ACCION => 'M' 
                                                      ,UN_CAMPOS => MI_CAMPOS 
                                                      ,UN_CONDICION => MI_CONDICION);
            EXCEPTION
                WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
            END;
        EXCEPTION
            WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE, UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_UPDATE_DEVOLUTIVO );
        END;
    
    --(EAMAYA:01/10/2019):Se adiciona validacion para actualizar la Fecha de Instalacion y el Acta de Instalacion 
    ELSIF MI_CLASE ='T'  AND MI_CONCEPTO = 'T'  THEN
        <<ACTUALIZAR_INSTALACION>>
        FOR MI_RS IN (
            SELECT  ELEMENTO ,
                    SERIE ,
                    HORA,
                    FECHA_INSTALACION,
                    ACTA_INSTALACION,
                    UBICACION_DESTINO,
                    CENTRODECOSTO,
                    REFERENCIA_CNT
            FROM D_MOVIMIENTO
            WHERE COMPANIA     = UN_COMPANIA
            AND TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
            AND MOVIMIENTO     = UN_MOVIMIENTO
            AND ELEMENTO       = UN_ELEMENTOINICIAL
            AND SERIE          = UN_SERIE
        )
        LOOP
            BEGIN
                BEGIN
                    MI_CAMPOS        := 'DEPENDENCIA           = '''||MI_DEPENDENCIA_DESTINO||'''
                                        ,RESPONSABLE          = '''||MI_RESPONSABLE_DESTINO ||'''
                                        ,SUCURSAL_RESPONSABLE = '''||MI_SUCURSAL_RESDESTINO||'''
                                        ,FECHAULTMOV          = '||PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA => MI_FECHA)||'
                                        ,TIPOMOVIMIENTOF      = '''||UN_TIPOMOVIMIENTO||'''
                                        ,MOVIMIENTOF          = '''||UN_MOVIMIENTO||'''
                                        ,BODEGA               ='''||MI_BODEGA_DESTINO||''' 
                                        ,CLASE_BODEGA         ='''||MI_CLASE_BODEGA_DESTINO||'''
                                        ,FECHA_INSTALACION    ='''||MI_RS.FECHA_INSTALACION||'''
                                        ,ACTA_INSTALACION     ='''||MI_RS.ACTA_INSTALACION||''' 
                                        ,UBICACION     ='''||MI_RS.UBICACION_DESTINO||''' 
                                        ,CENTRODECOSTO ='''||MI_RS.CENTRODECOSTO||''' 
                                        ,REFERENCIA_CNT ='''||MI_RS.REFERENCIA_CNT||''' ';
                    MI_CONDICION     :='COMPANIA ='''||UN_COMPANIA||'''
                                    AND ELEMENTO ='''||MI_RS.ELEMENTO||'''
                                    AND SERIE    =  '||MI_RS.SERIE||'';
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'DEVOLUTIVO' ,UN_ACCION => 'M' ,UN_CAMPOS => MI_CAMPOS ,UN_CONDICION => MI_CONDICION);
                EXCEPTION
                    WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                END;
            EXCEPTION
                WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE, UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_UPDATE_DEVOLUTIVO );
            END;
        END LOOP ACTUALIZAR_INSTALACION;
    --(EAMAYA:01/10/2019):Se adiciona validacion para actualizar la Fecha de Instalacion y el Acta de Instalacion   
    ELSIF MI_CLASE ='T' THEN
        <<ACTUALIZAR_DEVOLUTIVO>>
        FOR MI_RS IN (
            SELECT  ELEMENTO ,
                    SERIE ,
                    HORA
            FROM D_MOVIMIENTO
            WHERE COMPANIA     = UN_COMPANIA
            AND TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
            AND MOVIMIENTO     = UN_MOVIMIENTO
        )
        LOOP
            BEGIN
                BEGIN
                    MI_CAMPOS        := 'DEPENDENCIA           = '''||MI_DEPENDENCIA_DESTINO||'''
                                      ,RESPONSABLE          = '''||MI_RESPONSABLE_DESTINO ||'''
                                      ,SUCURSAL_RESPONSABLE = '''||MI_SUCURSAL_RESDESTINO||'''
                                      ,FECHAULTMOV          = '||PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA => MI_FECHA)||'
                                      ,TIPOMOVIMIENTOF      = '''||UN_TIPOMOVIMIENTO||'''
                                      ,MOVIMIENTOF          = '''||UN_MOVIMIENTO||'''
                                      ,BODEGA               ='''||MI_BODEGA_DESTINO||''' 
                                      ,CLASE_BODEGA         ='''||MI_CLASE_BODEGA_DESTINO||'''';
                    MI_CONDICION     :='COMPANIA ='''||UN_COMPANIA||'''
                                  AND ELEMENTO ='''||MI_RS.ELEMENTO||'''
                                  AND SERIE    =  '||MI_RS.SERIE||'';
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'DEVOLUTIVO' ,UN_ACCION => 'M' ,UN_CAMPOS => MI_CAMPOS ,UN_CONDICION => MI_CONDICION);
                EXCEPTION
                    WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                END;
            EXCEPTION
                WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE, UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_UPDATE_DEVOLUTIVO );
            END;
        END LOOP ACTUALIZAR_DEVOLUTIVO;       

    END IF;
   
    --COLOCA EL INDICADOR DE IND_REG PARA CUALQUIER MOVIMIENTO
    BEGIN
        BEGIN
            MI_CAMPOS        := 'IND_REG=-1'||',SALDO_PEPS = '||UN_CANTIDAD ||'
                              ,VLRUNITARIOPROM_PEPS     = '||UN_VALORUNITARIO ||'
                              ,VALORTOTAL_PEPS          = '||UN_VALORTOTAL ||'
                              ,VALORSALDO_PEPS          = '||UN_VALORTOTAL ||'
                              ,VALORBASE                = '||UN_VALORTOTAL ||'';
            MI_CONDICION     :='D_MOVIMIENTO.COMPANIA       = '''||UN_COMPANIA ||'''
                          AND D_MOVIMIENTO.TIPOMOVIMIENTO = '''||UN_TIPOMOVIMIENTO||'''
                          AND D_MOVIMIENTO.MOVIMIENTO     = '||UN_MOVIMIENTO||'
                          AND D_MOVIMIENTO.CODIGO         = '||UN_CODIGO||'';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'D_MOVIMIENTO' ,UN_ACCION => 'M' ,UN_CAMPOS => MI_CAMPOS ,UN_CONDICION => MI_CONDICION);
        EXCEPTION
            WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
        END;
    EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE, UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_UPDATE_DMOV );
    END;
    --COLOCA EL INDICADOR DE IND_REG PARA CUALQUIER MOVIMIENTO
    --SE COLOCA LA INSTRUCCION APARTE PORQUE DEBE COLOCARLE EL INDICADOR A TODOS LOS ITEMS DE UN MOVIMIENTO
    --CUANDO ES HEREDADO POR ORDEN DE COMPRA ENTONCES HAY QUE ELIMINAR LA CONDICION DEL CODIGO.
    BEGIN
      BEGIN
        MI_CAMPOS        := 'IND_REG=-1';
        MI_CONDICION     :='D_MOVIMIENTO.COMPANIA           = '''||UN_COMPANIA ||'''
                      AND D_MOVIMIENTO.TIPOMOVIMIENTO = '''||UN_TIPOMOVIMIENTO||'''
                      AND D_MOVIMIENTO.MOVIMIENTO     = '||UN_MOVIMIENTO||'';
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'D_MOVIMIENTO' ,UN_ACCION => 'M' ,UN_CAMPOS => MI_CAMPOS ,UN_CONDICION => MI_CONDICION);
      EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
      END;
    EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE, UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_UPDATE_DMOV );
    END;
    
    --CALCULA EL VALOR DEL IVA PARA LAS ENTRADAS DE PROVEEDORES A BODEGA SOLAMENTE
    IF MI_CLASE ='E' AND MI_CLASE_BODEGA_ORIGEN IN('10') THEN
      BEGIN
        BEGIN
          MI_CAMPOS        := 'VALORIVA          = '|| MI_VALORIVA ||'
                            ,VALORBASE        = '||UN_VALORUNITARIO_ANTESIVA * UN_CANTIDAD ||'
                            ,VALORTOTALCONIVA = '|| UN_VALORTOTAL ||'';
          MI_CONDICION     :='D_MOVIMIENTO.COMPANIA       = '''||UN_COMPANIA ||'''
                            AND D_MOVIMIENTO.TIPOMOVIMIENTO = '''||UN_TIPOMOVIMIENTO||'''
                            AND D_MOVIMIENTO.MOVIMIENTO     = '||UN_MOVIMIENTO||'
                            AND D_MOVIMIENTO.CODIGO         = '||UN_CODIGO||'';
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'D_MOVIMIENTO' ,UN_ACCION => 'M' ,UN_CAMPOS => MI_CAMPOS ,UN_CONDICION => MI_CONDICION);
        EXCEPTION
            WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
        END;
      EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE, UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_UPDATE_DMOV );
        END;
    END IF;
  END IF;
  
  --ACTUALIZA EL VALOR TOTAL Y EL TOTAL CON AJUSTE DE LA TABLA MOVIMIENTO
  BEGIN
    BEGIN
      MI_CAMPOS:= 'VALORTOTAL=( SELECT NVL(SUM(VALORBASE),0) AS BASE
                                FROM   D_MOVIMIENTO
                                WHERE  COMPANIA       = '''||UN_COMPANIA||'''
                                AND  TIPOMOVIMIENTO = '''||UN_TIPOMOVIMIENTO||'''
                                AND  MOVIMIENTO     = '||UN_MOVIMIENTO||')'||',
                    VALORIVA=(  SELECT NVL(SUM(VALORIVA),0) AS VALORIVA
                                FROM   D_MOVIMIENTO
                                WHERE  COMPANIA       = '''||UN_COMPANIA||'''
                                AND  TIPOMOVIMIENTO = '''||UN_TIPOMOVIMIENTO||'''
                                AND  MOVIMIENTO     = '||UN_MOVIMIENTO||')'|| ',
                    VALORTOTALCONIVA =( SELECT NVL(SUM(VALORTOTALCONIVA),0) AS TOTALCONIVA
                                        FROM   D_MOVIMIENTO
                                        WHERE  COMPANIA       ='''||UN_COMPANIA||'''
                                        AND  TIPOMOVIMIENTO = '''||UN_TIPOMOVIMIENTO||'''
                                        AND  MOVIMIENTO     = '||UN_MOVIMIENTO||')'||',
                    TOTALCONAJUSTE=( SELECT NVL(SUM(VALORTOTAL),0) AS TOTAL
                                    FROM   D_MOVIMIENTO
                                    WHERE  COMPANIA       = '''||UN_COMPANIA ||'''
                                    AND  TIPOMOVIMIENTO = '''||UN_TIPOMOVIMIENTO||'''
                                    AND  MOVIMIENTO     = '||UN_MOVIMIENTO||')';
      MI_CONDICION     :='MOVIMIENTO.COMPANIA       = '''||UN_COMPANIA ||'''
                        AND MOVIMIENTO.TIPOMOVIMIENTO = '''||UN_TIPOMOVIMIENTO||'''
                        AND MOVIMIENTO.NUMERO         = '||UN_MOVIMIENTO||'';
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'MOVIMIENTO' ,UN_ACCION => 'M' ,UN_CAMPOS => MI_CAMPOS ,UN_CONDICION => MI_CONDICION);
      EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
        END;
    EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE, UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_UPDATE_MOVIMIENTO );
    END;
    
    --PROCESO DE ANULACION DE PLACA
     --CC_4264_ALMACEN(25/05/2026 NCARDENAS) -  Se agrega la clase bodega de comodato(90) - controlado por parametro
       MI_PARANULACIONCOMODATO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                                    ,UN_NOMBRE    => 'PERMITE ANULACION DE PLACAS EN SALIDAS DE COMODATO'
                                                    ,UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN()
                                                    ,UN_FECHA_PAR => SYSDATE), 'NO');
                                                    
    MI_PERMITE :=
           (MI_PARANULACIONCOMODATO = 'SI'
            AND MI_CLASE_BODEGA_DESTINO IN ('60','80','90'))

        OR (MI_PARANULACIONCOMODATO = 'NO'
            AND MI_CLASE_BODEGA_DESTINO IN ('60','80'));
      
    IF MI_PERMITE THEN
        BEGIN
            BEGIN
                MI_CONDICION:='SELECT COMPANIA
                                     ,ELEMENTO
                                     ,SERIE
                                     ,VALORUNITARIO
                               FROM   D_MOVIMIENTO
                               WHERE  COMPANIA       = '''||UN_COMPANIA||'''
                               AND  TIPOMOVIMIENTO = '''||UN_TIPOMOVIMIENTO||'''
                               AND  MOVIMIENTO     = '||UN_MOVIMIENTO||'';
                MI_CAMPOS   :='VISTA.SERIE    = TABLA.SERIE
                              AND VISTA.COMPANIA = TABLA.COMPANIA
                              AND VISTA.ELEMENTO = TABLA.ELEMENTO';
                MI_VALORES  :='UPDATE SET TABLA.DEPENDENCIA = '''||MI_DEPENDENCIA_DESTINO||'''
                              ,TABLA.RESPONSABLE            = '''||MI_RESPONSABLE_DESTINO||'''
                              ,TABLA.SUCURSAL_RESPONSABLE   = '''||MI_SUCURSAL_RESDESTINO||'''
                              ,TABLA.FECHAULTMOV            = '||PCK_SYSMAN_UTL.FC_SDATE(MI_FECHA)||'
                              ,TABLA.FECHAANULADA           = '||PCK_SYSMAN_UTL.FC_SDATE(MI_FECHA)||'
                              ,TABLA.PLACAANULADA           = -1
                              ,TABLA.CLASE_BODEGA            = '||MI_CLASE_BODEGA_DESTINO||'
                              ,TIPOMOVIMIENTOF              = '''||UN_TIPOMOVIMIENTO||'''
                              ,MOVIMIENTOF                  = '''||UN_MOVIMIENTO||'''
                              ,BODEGA                       = '''||MI_BODEGA_DESTINO||'''
                              ,VALORULTIMOV                 = VISTA.VALORUNITARIO';
                MI_RTA      := PCK_DATOS.FC_ACME (UN_TABLA => 'DEVOLUTIVO' ,UN_ACCION => 'MM' ,UN_MERGEUSING => MI_CONDICION ,UN_MERGEENLACE => MI_CAMPOS ,UN_MERGEEXISTE => MI_VALORES);
            EXCEPTION
                WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                    RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
            END;
        EXCEPTION
            WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE, UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_MERGE_DEVOLUTIVO );
        END;
    END IF;
    RETURN ' ';
END FC_INSERTARDETALLES;

FUNCTION FC_BUSCAEXISTENCIA
 /*
    NAME              : BuscaExistencia
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JAVIER VILLATE
    DATE MIGRADOR     : 13/05/2015
    TIME              : 8:30 AM
    SOURCE MODULE     : ALMACEN
    DATE MODIFIED     : 01/03/2016
    TIME              : 04:02 PM
    DESCRIPTION       : MUESTRA EL VALOR DE LAS EXISTENCIAS EN EL INVENTARIO PARA UN ELEMENTO.
    MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED	    : 17/01/2017
    TIME				      : 11:48 AM
    MODIFICATIONS	    : Se cambió el estándar de codificación
                        y se agrego manejo de excepciones

  */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ELEMENTO IN VARCHAR2
  )
  RETURN NUMBER
  AS
   MI_EXISTENCIA  PCK_SUBTIPOS.TI_DOBLE := 0;
BEGIN

  SELECT EXISTENCIA
  INTO   MI_EXISTENCIA
  FROM   INVENTARIO
  WHERE  COMPANIA       = UN_COMPANIA
  AND    CODIGOELEMENTO = UN_ELEMENTO;

  RETURN NVL(MI_EXISTENCIA,0);

  EXCEPTION WHEN NO_DATA_FOUND THEN
  RETURN 0;

END FC_BUSCAEXISTENCIA;

FUNCTION FC_DEVUELVESALDOPEPS
  /*
  NAME              : PR_DEVUELVESALDOPEPS
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : JAVIER VILLATE
  DATE MIGRADOR     : 22/02/2016
  TIME              : 03:00 PM
  SOURCE MODULE     : ALMACEN
  DESCRIPTION       : PROCEDIMIENTO QUE ACTUALIZA EL CAMPO SALDO_PEPS,VALORSALDO_PEPS,CUANDO SE ELIMINA EL REGISTRO
                      PUEDE SUCEDER CUANDO EL USUARIO DESEE ELIMINAR EL REGISTRO POR ALGUNA EQUIVOCACIÓN EN LA DIGITACIÓN DE LOS DATOS SOLICITADOS.
  MODIFIER          : ELKIN GEOVANNY AMAYA SILVA
  DATE MODIFIED     : 17/01/2017
  TIME              : 12:14 AM
  MODIFICATIONS     : Se cambió el estándar de codificación y se agrego manejo de excepciones.

  */
(
  UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOMOVIMIENTO       IN VARCHAR2,
  UN_TIPOMOVIMIENTO_AFECT IN VARCHAR2,
  UN_ELEMENTO             IN VARCHAR2,
  UN_MOVIMIENTO           IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_CODIGO               IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_CANTIDAD_NUEVA       IN PCK_SUBTIPOS.TI_DOBLE,
  UN_CANTIDAD_AFECTADA    IN PCK_SUBTIPOS.TI_DOBLE,
  UN_SERIE                IN PCK_SUBTIPOS.TI_ENTERO_LARGO
)
RETURN VARCHAR2
AS
  MI_CLASE     						VARCHAR2(2 CHAR);
  MI_ELEMENTO  						VARCHAR2(2 CHAR);
  MI_RTA       						VARCHAR2(32000 CHAR);
  MI_DATO      						VARCHAR2(2 CHAR);
  MI_CAMPOS    						PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION 						PCK_SUBTIPOS.TI_CONDICION;
  MI_CONCEPTO  						TIPOMOVIMIENTO.CONCEPTO%TYPE;
BEGIN
  BEGIN
    SELECT CLASE,
      TIPOELEMENTO,
      CONCEPTO
    INTO MI_CLASE,
      MI_ELEMENTO,
      MI_CONCEPTO
    FROM TIPOMOVIMIENTO
    WHERE COMPANIA = UN_COMPANIA
    AND CODIGO     = UN_TIPOMOVIMIENTO;
  EXCEPTION
  WHEN NO_DATA_FOUND THEN
    MI_RTA :='No esta configurado el tipo de movimiento.';
    RETURN MI_RTA;
  END;
  IF MI_CLASE ='S' AND MI_ELEMENTO='C' THEN
    IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'MANEJA PEPS EN CONSUMO ALMACEN',10,SYSDATE),'NO')='SI' THEN
      BEGIN
        BEGIN
          --ACTUALIZA EXISTENCIAS EN INVENTARIO
          MI_CAMPOS        := 'EXISTENCIA=INVENTARIO.EXISTENCIA+'||UN_CANTIDAD_AFECTADA||'';
          MI_CONDICION     :='INVENTARIO.COMPANIA='''||UN_COMPANIA ||''' AND INVENTARIO.CODIGOELEMENTO='''||UN_ELEMENTO||'''';
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'INVENTARIO' , UN_ACCION => 'M' , UN_CAMPOS => MI_CAMPOS , UN_CONDICION => MI_CONDICION);
        EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
        END;
      EXCEPTION
      WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE , UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_ACTUALIZARINV );
      END;
      --ACTUALIZA SALDO_PEPS Y VALORES PEPS EN LA TABLA D_MOVIMIENTO
      IF UN_TIPOMOVIMIENTO_AFECT IS NOT NULL THEN
        BEGIN
          BEGIN
            MI_CAMPOS        := 'SALDO_PEPS       = D_MOVIMIENTO.SALDO_PEPS + '||
              UN_CANTIDAD_AFECTADA||',VALORSALDO_PEPS = D_MOVIMIENTO.VALORSALDO_PEPS + '||
              UN_CANTIDAD_AFECTADA||'* D_MOVIMIENTO.VALORUNITARIO';
            MI_CONDICION     :='D_MOVIMIENTO.COMPANIA       = '''||UN_COMPANIA ||
              ''' AND D_MOVIMIENTO.TIPOMOVIMIENTO = '''||UN_TIPOMOVIMIENTO_AFECT||
              ''' AND D_MOVIMIENTO.MOVIMIENTO     = '||UN_MOVIMIENTO||' AND D_MOVIMIENTO.CODIGO = '||UN_CODIGO||'';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
              UN_TABLA => 'D_MOVIMIENTO'
            , UN_ACCION => 'M'
            , UN_CAMPOS => MI_CAMPOS
            , UN_CONDICION => MI_CONDICION);
            MI_RTA           :='OK';
          EXCEPTION
            WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
          END;
          RETURN MI_RTA;
        EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
          PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE , UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_UPDATE_DMOV );
        END;
      END IF;
    END IF;
    MI_RTA:='OK';
    RETURN MI_RTA;
  ELSE
    -- Solo para entradas de elementos que no estén en la entidad Ej. Por compra
    IF MI_CLASE ='E' AND MI_CONCEPTO IN ('C') THEN
      BEGIN
        SELECT DISTINCT 'X'
        INTO MI_DATO
        FROM DEPRECIAR
        WHERE COMPANIA = UN_COMPANIA
        AND ELEMENTO   = UN_ELEMENTO
        AND SERIE      = UN_SERIE;
      EXCEPTION
      WHEN NO_DATA_FOUND THEN
        --BORRA EL REGISTRO DE LA TABLA DEVOLUTIVO
        BEGIN
          BEGIN
            MI_CONDICION     := 'COMPANIA = ''' || UN_COMPANIA || ''' AND ELEMENTO = ''' || UN_ELEMENTO || ''' AND SERIE =   ' || UN_SERIE ||'';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
              UN_TABLA => 'DEVOLUTIVO'
            , UN_ACCION => 'E'
            , UN_CONDICION => MI_CONDICION);
            MI_RTA           :='OK';
          EXCEPTION
          WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
          END;
          RETURN MI_RTA;
        EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
          PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE , UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_UPDATE_DMOV );
        END;
      END;
      MI_RTA :='FALSE';
      RETURN MI_RTA;
    END IF;
    MI_RTA :='OK';
    RETURN MI_RTA;
  END IF;
END FC_DEVUELVESALDOPEPS;

PROCEDURE PR_CORRECCIONDEVALOR
/*
  NAME              : PR_CORRECCIONDEVALOR
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : JAVIER VILLATE
  DATE MIGRADOR     : 22/02/2016
  TIME              : 03:00 PM
  SOURCE MODULE     : ALMACEN
  DESCRIPTION       : PROCEDIMIENTO QUE ANALIZA LAS ENTRADAS POR CONCEPTO DE CORRECCIÓN DE VALOR, EL CUAL VERIFICA EL VALOR EN LIBROS JUNTO CON EL VALOR INGRESADO
                      SI ESTOS VALORES NO CUMPLEN CON LAS CONDICIONES PARA REALIZAR ESTE MOVIMIENTO SE ELIMINA EL REGISTRO QUE SE ESTA CREANDO.
  MODIFIER          : ELKIN GEOVANNY AMAYA SILVA; JUAN CARLOS RODR�?GUEZ AMÉZQUITA; AURA LILIANA MONROY GARCIA 
  DATE MODIFIED     : 17/01/2017 ; 10/07/2017 ; 15/03/2019 
  TIME              : 3:04 PM  ; 08:41 AM ; 10:50 AM 
  MODIFICATIONS     : Se cambió el estándar de codificación y se agrego manejo de excepciones
                      Aplicación de correción de valor para devolutivos.
                      (AM) Se realiza el cambio de función a procedimiento, se adicionan parámetros que se pueden obtener sin necesidad de calcularlos
                      UN_VALORUNITARIOENTRADA :Parametro usado especificamente para entradas de (E) y concepto de compra (C)
*/
(
  UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOMOVIMIENTO           IN TIPOMOVIMIENTO.CODIGO%TYPE,
  UN_MOVIMIENTO               IN PCK_SUBTIPOS.TI_ENTERO_LARGO, 
  UN_ELEMENTO                 IN PCK_SUBTIPOS.TI_ELEMENTO,
  UN_SERIE                    IN PCK_SUBTIPOS.TI_SERIE,
  UN_VALORUNITARIO            IN PCK_SUBTIPOS.TI_DOBLE,
  UN_VALORUNITARIOENTRADA     IN PCK_SUBTIPOS.TI_DOBLE,
  UN_ESPECIFICACION           IN D_MOVIMIENTO.ESPECIFICACION%TYPE
)
AS
  MI_CLASE                    TIPOMOVIMIENTO.CLASE%TYPE;
  MI_CONCEPTO                 TIPOMOVIMIENTO.CONCEPTO%TYPE;
  MI_TIPOELEMENTO             TIPOMOVIMIENTO.TIPOELEMENTO%TYPE;
  MI_PAR_MANEJA_NIIF          PCK_SUBTIPOS.TI_PARAMETRO;
  MI_ULTIMOVLRLIBROS          PCK_SUBTIPOS.TI_DOBLE;
  MI_NUEVO_VALOR              PCK_SUBTIPOS.TI_DOBLE;
  MI_DESCRIPCION              DEVOLUTIVO.DESCRIPCION%TYPE;
  MI_CAMPOS                   PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION                PCK_SUBTIPOS.TI_CONDICION;
  MI_RTA                      PCK_SUBTIPOS.TI_RTA_ACME;
  MI_MSGERROR                 PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_CONTADOR                 PCK_SUBTIPOS.TI_ENTERO;
  MI_PARCOLGAAPNIIF           VARCHAR2(32000 CHAR);
  MI_DIGPORCVLRRES            NUMBER; --JM CC389 06/02/2025
  MI_PORVLRRES                NUMBER; --JM CC389 06/02/2025
  MI_VLRRES                   NUMBER; --JM CC389 06/02/2025
BEGIN
    SELECT COUNT(COMPANIA)
    INTO MI_CONTADOR
    FROM DEVOLUTIVO
   WHERE COMPANIA = UN_COMPANIA       
     AND ELEMENTO = UN_ELEMENTO
     AND SERIE    = UN_SERIE;

  IF MI_CONTADOR =0 THEN
    RETURN;
  END IF;

  BEGIN
    BEGIN
      SELECT CLASE, CONCEPTO, TIPOELEMENTO
        INTO MI_CLASE, MI_CONCEPTO, MI_TIPOELEMENTO
        FROM TIPOMOVIMIENTO
       WHERE COMPANIA = UN_COMPANIA
         AND CODIGO   = UN_TIPOMOVIMIENTO;
      EXCEPTION WHEN NO_DATA_FOUND 
                THEN RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
    END; 
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN 
              THEN PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                            , UN_ERROR_COD  => PCK_ERRORES.ERR_ALM_TIPOMOV_NOCONF);
  END;

  MI_PAR_MANEJA_NIIF := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                                 ,UN_NOMBRE    =>'MANEJA NIIF EN ALMACEN'
                                                 ,UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN()
                                                 ,UN_FECHA_PAR => SYSDATE),'NO');
  MI_PARCOLGAAPNIIF:=NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
      , UN_NOMBRE    => 'EJECUTA COLGAAP y NIIF'
      , UN_MODULO    => 10
      , UN_FECHA_PAR => SYSDATE),'NO');                                                 

  IF MI_CONCEPTO IN('CR','C') AND MI_CLASE ='E' THEN

    SELECT MAX(CASE WHEN MI_PAR_MANEJA_NIIF = 'SI' 
                        THEN DEPRECIAR.NIIF_VLRLIBROS 
                        ELSE DEPRECIAR.VLRLIBROS 
                   END) KEEP (DENSE_RANK LAST ORDER BY PERIODO) ULTIMOVLRLIBROS
    INTO MI_ULTIMOVLRLIBROS
    FROM DEPRECIAR
    WHERE COMPANIA = UN_COMPANIA
      AND ELEMENTO = UN_ELEMENTO
      AND SERIE    = UN_SERIE;

    IF MI_ULTIMOVLRLIBROS IS NOT NULL THEN 
      IF (UN_VALORUNITARIOENTRADA + MI_ULTIMOVLRLIBROS) < 0 THEN
        BEGIN 
          RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN 
                    THEN PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                                  , UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_CR_VALOR_LIBROS);
        END;
      END IF;
    END IF;

    IF MI_TIPOELEMENTO <> 'C' THEN

            --JM 06/02/2025 CC389
            IF ( NVL(PCK_SYSMAN_UTL.FC_PAR( UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'MANEJA VALOR RESIDUAL POR AGRUPACION EN ALMACEN', UN_MODULO => 10, UN_FECHA_PAR => SYSDATE),'NO') = 'SI' ) THEN
                MI_DIGPORCVLRRES := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR( UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'DIGITOS DE AGRUPACION PARA VALOR RESIDUAL EN ALMACEN', UN_MODULO => 10, UN_FECHA_PAR => SYSDATE),'0'));
                BEGIN
                    SELECT  INVENTARIO.PORC_VALOR_RESIDUAL
                    INTO    MI_PORVLRRES
                    FROM    INVENTARIO
                    WHERE   COMPANIA = UN_COMPANIA
                            AND CODIGOELEMENTO = SUBSTR(UN_ELEMENTO,1,MI_DIGPORCVLRRES) 
                            AND PORC_VALOR_RESIDUAL IS NOT NULL;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_PORVLRRES := 0;
                END;
            ELSE 
                IF ( NVL(PCK_SYSMAN_UTL.FC_PAR( UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'MANEJA VALOR RESIDUAL POR PORCENTAJE EN NIIF', UN_MODULO => 10, UN_FECHA_PAR => SYSDATE),'NO') = 'SI' ) THEN
                    MI_PORVLRRES := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR( UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'PORCENTAJE PARA VALOR RESIDUAL EN NIIF', UN_MODULO => 10, UN_FECHA_PAR => SYSDATE),'0'));
                ELSE
                    MI_PORVLRRES := 0;
                END IF;
            END IF;
            --JM 06/02/2025CC389

      SELECT CASE WHEN MI_CONCEPTO IN('CR') THEN VALOR + UN_VALORUNITARIO ELSE UN_VALORUNITARIOENTRADA END ,
      --SELECT VALOR + UN_VALORUNITARIO,
             CASE WHEN UN_ESPECIFICACION <> ' ' OR UN_ESPECIFICACION IS NOT NULL
                  THEN UN_ESPECIFICACION
                  ELSE DEVOLUTIVO.DESCRIPCION 
              END
        INTO MI_NUEVO_VALOR, MI_DESCRIPCION
        FROM DEVOLUTIVO
       WHERE COMPANIA = UN_COMPANIA       
         AND ELEMENTO = UN_ELEMENTO
         AND SERIE    = UN_SERIE;

      MI_CAMPOS    := ' VALOR              =   ' || MI_NUEVO_VALOR || '
                      , SALVAMENTO         =   ' || NVL((MI_NUEVO_VALOR * MI_PORVLRRES / 100),0) || '  
                      , NIIF_VALOR_BASE    =   ' || MI_NUEVO_VALOR || '  
                      , NIIF_VALORBASE     =   ' || MI_NUEVO_VALOR || '  
                      , NIIF_VALOR_TOTAL   =   ' || MI_NUEVO_VALOR || '  
                      , COSTOAJUSTADO      =   ' || MI_NUEVO_VALOR || '  
                      , NIIF_COSTOAJUSTADO =   ' || MI_NUEVO_VALOR || '  
                      , DESCRIPCION        = ''' || MI_DESCRIPCION || '''
                      , TIPOMOVIMIENTOF    = ''' || UN_TIPOMOVIMIENTO || '''
                      , MOVIMIENTOF        = ''' || UN_MOVIMIENTO || '''
                      , VALORULTIMOV       =   ' || MI_NUEVO_VALOR || '
					  , VLR_COLGAAP        =   CASE WHEN '''||MI_PARCOLGAAPNIIF||''' = ''SI'' 
                                                 THEN '||MI_NUEVO_VALOR||' 
                                                 ELSE 0  
                                                END';								 

      MI_CONDICION := '    COMPANIA = ''' || UN_COMPANIA || '''
                       AND ELEMENTO = ''' || UN_ELEMENTO || '''
                       AND SERIE    =   ' || UN_SERIE || ''; 
      BEGIN
        BEGIN
          MI_RTA := PCK_DATOS.FC_ACME ( UN_TABLA     => 'DEVOLUTIVO'
                                       ,UN_ACCION    => 'M'
                                       ,UN_CAMPOS    => MI_CAMPOS
                                       ,UN_CONDICION => MI_CONDICION);
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                   THEN RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN  
          MI_MSGERROR(1).CLAVE := 'PLACA';
          MI_MSGERROR(1).VALOR := UN_SERIE;
          PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE
                                    , UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_CORRECCIONVALOR
                                    , UN_REEMPLAZOS => MI_MSGERROR);                  
      END;                 

    END IF;

  END IF;

END PR_CORRECCIONDEVALOR;


PROCEDURE PR_KARDEXSALDOPEPS
  /*
  NAME              : REVISARPEPS En Access --> RevisarPEPs
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : JAVIER VILLATE
  DATE MIGRADOR     : 22/02/2016
  TIME              : 03:00 PM
  SOURCE MODULE     : ALMACEN
  MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
  DATE MODIFIED	    : 17/01/2017
  TIME				      : 4:35 PM
  MODIFICATIONS	    : Correccion Merge, Corrreccion consultas,Se cambió el estándar de codificación
                      y se agrego manejo de excepciones

  */
  (
  UN_COMPANIA        IN         VARCHAR2
  )

  AS
    MI_FECHAINICIAL           DATE;
    MI_FECHAFINAL             DATE;
    MI_CAMPOS                 VARCHAR2(32000);
    MI_CONDICION              VARCHAR2(32000);
    MI_VALORES                VARCHAR2(32000);
    MI_RTA                    VARCHAR2(32000);
    MI_CANTIDADANTERIOR       NUMBER:= 0;
    MI_CANTIDADNEGATIVA       NUMBER:= 0;
    MI_SALDOPOSITIVO          NUMBER:= 0;
    MI_DUSALIDAS              NUMBER:= 0;
    MI_RSELEMENTO             SYS_REFCURSOR;
    MI_STRSQL                 VARCHAR2(32000);
    RSELEMENTO SYS_REFCURSOR;
    RSENTRADAS SYS_REFCURSOR;
    RSSALIDAS  SYS_REFCURSOR;

BEGIN

 --MERGE INTO D_MOVIMIENTO TABLA
 --USING (SELECT COMPANIA,CODIGO
--FROM TIPOMOVIMIENTO
--WHERE TIPOMOVIMIENTO.COMPANIA='001'  AND TIPOMOVIMIENTO.TIPOELEMENTO In ('C')) VISTA
-- ON (VISTA.COMPANIA = TABLA.COMPANIA AND VISTA.CODIGO = TABLA.TIPOMOVIMIENTO)
-- WHEN MATCHED THEN
-- UPDATE SET TABLA.SALDO_PEPS= TABLA.CANTIDAD
-- WHERE TABLA.FECHA Between '19/11/2015' And '15/03/2016';

 MI_FECHAINICIAL := TO_DATE(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                                      ,UN_NOMBRE    => 'FECHA DE CORTE PARA INICIO DEL ALMACEN'
                                                      ,UN_MODULO    => 10
                                                      ,UN_FECHA_PAR => SYSDATE),SYSDATE),'DD/MM/YYYY');
 MI_FECHAFINAL := SYSDATE;

    BEGIN
      BEGIN
       MI_CONDICION:='SELECT COMPANIA,CODIGO,CLASE
                      FROM   TIPOMOVIMIENTO
                      WHERE  COMPANIA     = '''||UN_COMPANIA||'''
                      AND    TIPOELEMENTO IN (''C'')';
       MI_CAMPOS:='VISTA.COMPANIA = TABLA.COMPANIA
               AND VISTA.CODIGO   = TABLA.TIPOMOVIMIENTO';
       MI_VALORES:='UPDATE SET TABLA.SALDO_PEPS=DECODE(VISTA.CLASE,''E'',TABLA.CANTIDAD,0)
                    WHERE TABLA.FECHA BETWEEN '''||MI_FECHAINICIAL ||''' AND '''||MI_FECHAFINAL ||'''';

       MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA        => 'D_MOVIMIENTO'
                                    ,UN_ACCION      => 'MM'
                                    ,UN_MERGEUSING  => MI_CONDICION
                                    ,UN_MERGEENLACE => MI_CAMPOS
                                    ,UN_MERGEEXISTE => MI_VALORES);


              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                       RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
      END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                         PCK_ERR_MSG.RAISE_WITH_MSG(
                                    UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_MERGE_DMOVIMIENTO
                              );
    END;


BEGIN

        MI_STRSQL:='SELECT DISTINCT D_MOVIMIENTO.ELEMENTO
                    FROM   TIPOMOVIMIENTO
                    INNER JOIN D_MOVIMIENTO
                        ON TIPOMOVIMIENTO.COMPANIA = D_MOVIMIENTO.COMPANIA
                       AND TIPOMOVIMIENTO.CODIGO   = D_MOVIMIENTO.TIPOMOVIMIENTO
                    WHERE  TIPOMOVIMIENTO.COMPANIA     =''' || UN_COMPANIA || '''
                      AND  D_MOVIMIENTO.FECHA          BETWEEN''' || MI_FECHAINICIAL || ''' AND ''' || MI_FECHAFINAL || '''
                      AND  TIPOMOVIMIENTO.TIPOELEMENTO IN (''C'')
                    ORDER BY D_MOVIMIENTO.ELEMENTO';

      OPEN RSELEMENTO FOR MI_STRSQL;
      LOOP
       FETCH RSELEMENTO INTO MI_CAMPOS;
       EXIT WHEN RSELEMENTO%NOTFOUND;

        MI_STRSQL:='SELECT D_MOVIMIENTO.CANTIDAD
                           ,D_MOVIMIENTO.SALDO_PEPS
                    FROM   TIPOMOVIMIENTO
                      INNER JOIN D_MOVIMIENTO
                            ON TIPOMOVIMIENTO.COMPANIA = D_MOVIMIENTO.COMPANIA
                           AND TIPOMOVIMIENTO.CODIGO   = D_MOVIMIENTO.TIPOMOVIMIENTO
                    WHERE TIPOMOVIMIENTO.COMPANIA     = ''' || UN_COMPANIA || '''
                      AND TIPOMOVIMIENTO.CLASE        IN (''E'')
                      AND D_MOVIMIENTO.ELEMENTO       = ''' || MI_CAMPOS || '''
                      AND D_MOVIMIENTO.FECHA          BETWEEN''' || MI_FECHAINICIAL || ''' AND ''' || MI_FECHAFINAL || '''
                      AND TIPOMOVIMIENTO.TIPOELEMENTO IN (''C'')
                    ORDER BY D_MOVIMIENTO.ELEMENTO
                             , D_MOVIMIENTO.FECHA
                             , D_MOVIMIENTO.HORA';

      OPEN RSENTRADAS FOR MI_STRSQL;
      LOOP
      --FETCH RSENTRADAS INTO MI_CAMPOS;
       EXIT WHEN RSENTRADAS%NOTFOUND;


         MI_STRSQL:='SELECT D_MOVIMIENTO.CANTIDAD
                     FROM   TIPOMOVIMIENTO
                      INNER JOIN D_MOVIMIENTO
                            ON  TIPOMOVIMIENTO.COMPANIA = D_MOVIMIENTO.COMPANIA
                            AND TIPOMOVIMIENTO.CODIGO   = D_MOVIMIENTO.TIPOMOVIMIENTO
                     WHERE TIPOMOVIMIENTO.COMPANIA     =''' || UN_COMPANIA || '''
                       AND TIPOMOVIMIENTO.CLASE        IN (''S'')
                       AND D_MOVIMIENTO.ELEMENTO       =''' || MI_CAMPOS || '''
                       AND D_MOVIMIENTO.FECHA          BETWEEN ''' || MI_FECHAINICIAL || ''' AND ''' || MI_FECHAFINAL || '''
                       AND TIPOMOVIMIENTO.TIPOELEMENTO IN (''C'')
                     ORDER BY D_MOVIMIENTO.ELEMENTO
                           ,D_MOVIMIENTO.FECHA
                           ,D_MOVIMIENTO.HORA';

      OPEN RSSALIDAS FOR MI_STRSQL;

      LOOP

       --FETCH RSSALIDAS INTO MI_CAMPOS;

       EXIT WHEN RSSALIDAS%NOTFOUND;

            --MI_DUSALIDAS:=MI_RSSALIDAS.CANTIDAD;
      END LOOP;
      END LOOP;
      END LOOP;
      CLOSE RSSALIDAS;
      CLOSE RSENTRADAS;
      CLOSE RSELEMENTO;
  END;

    --MI_CAMPOS:= 'SALDO_PEPS='||(NVL(MI_RS.SALDO_PEPS, 0))||', VALORSALDO_PEPS='||((NVL(MI_RS.SALDO_PEPS, 0)) * MI_RS.VALORUNITARIO)||', VALORTOTAL_PEPS='||MI_RS.VALORTOTAL||', VLRUNITARIOPROM_PEPS='||MI_RS.VLRUNITARIOPROM;
    --MI_CONDICION:='COMPANIA='''||UN_COMPANIA||''' AND TIPOMOVIMIENTO='''||MI_RS.TIPOMOVIMIENTO||''' AND MOVIMIENTO='||MI_RS.MOVIMIENTO||' AND CODIGO='||MI_RS.CODIGO||' AND ELEMENTO='''||MI_RS.CODIGOELEMENTO||'''';
    --PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ('D_MOVIMIENTO', 'M', MI_CAMPOS, NULL, NULL,MI_CONDICION, NULL, NULL, NULL, NULL);

 --END LOOP;

/* EXCEPTION WHEN OTHERS THEN
  PCK_DATOS.GL_ERROR_MSG := 'Error al Revisar PEPS';
  PCK_DATOS.GL_ERROR_MSG := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,'','',SQLERRM );
  RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );*/

END PR_KARDEXSALDOPEPS;

PROCEDURE PR_MOVIMIENTOSPOSTERIORES
 /*
    NAME              : MOVIMIENTOSPOSTERIORES
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JAVIER VILLATE
    DATE MIGRADOR     : 19/04/2016
    TIME              : 8:30 AM
    SOURCE MODULE     : ALMACEN
    DATE MODIFIED     : 19/04/2016
    TIME              : 04:02 PM
    DESCRIPTION       : CONTROLA QUE NO SE PUEDAN REALIZAR MOVIMIENTOS ANTERIORES A LA ULTIMA FECHA DE LOS MOVIMIENTOS DE UNA PLACA O UN ELEMENTO.
    MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED	    : 18/01/2017
    TIME				      : 9:15 AM
    MODIFICATIONS	    : ELIMINACION DE LA VARIABLE UN_HORA QUE ESTABA SIN USO, Se cambió el estándar de codificación
                        y se agrego manejo de excepciones


  */
  (
    UN_COMPANIA               IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHA                  IN DATE,
    UN_HORA                   IN DATE,
    UN_ELEMENTO               IN VARCHAR2,
    UN_SERIE                  IN PCK_SUBTIPOS.TI_ENTERO_LARGO
  )
AS
  MI_DATO        VARCHAR2(2 CHAR);  
  MI_ERRORES     VARCHAR2(32000 CHAR);  
  MI_POSTERIORES NUMBER;
  MI_MSGERROR    PCK_SUBTIPOS.TI_CLAVEVALOR;
  VAR1            DATE;
BEGIN
  MI_POSTERIORES:=0;
  MI_ERRORES:=' ';
  VAR1 := TO_DATE(TO_CHAR(UN_FECHA,  'DD/MM/YYYY') || ' ' || TO_CHAR(UN_HORA,'HH24:MI:SS'),'DD/MM/YYYY HH24:MI:SS');
  FOR RS IN(
            SELECT TIPOMOVIMIENTO, MOVIMIENTO, FECHA, HORA
            FROM D_MOVIMIENTO
            WHERE COMPANIA   = UN_COMPANIA
              AND ELEMENTO   = UN_ELEMENTO
              AND SERIE      = UN_SERIE
              AND IND_REG    NOT IN (0)
              AND TO_DATE(TO_CHAR(FECHA,  'DD/MM/YYYY') || ' ' || TO_CHAR(HORA,'HH24:MI:SS'),'DD/MM/YYYY HH24:MI:SS') > 
                  VAR1
            )
  LOOP
    MI_POSTERIORES:= MI_POSTERIORES + 1;  
    MI_ERRORES    := MI_ERRORES || CHR(13) || CHR(10) || RS.TIPOMOVIMIENTO || '-' || RS.MOVIMIENTO || '-' || TO_CHAR(RS.FECHA,  'DD/MM/YYYY') || ' ' || TO_CHAR(RS.HORA,'HH24:MI:SS') ;
  END LOOP;
  IF MI_POSTERIORES>0 THEN
    MI_MSGERROR(1).CLAVE := 'LISTADO';
    MI_MSGERROR(1).VALOR := MI_ERRORES;
    PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => -20000
                   ,UN_TABLAERROR => 'D_MOVIMIENTO'
                   ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_REALIZAR_EXISTMOV
                   ,UN_REEMPLAZOS => MI_MSGERROR
                   );
  END IF;
END PR_MOVIMIENTOSPOSTERIORES;

FUNCTION FC_ACTUALIZADEVOLUTIVO
  /*
    NAME              : FC_ACTUALIZADEVOLUTIVO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JAVIER VILLATE
    DATE MIGRADOR     : 27/05/2016
    TIME              : 8:30 AM
    SOURCE MODULE     : ALMACEN
    DATE MODIFIED     : 27/05/2016
    TIME              : 04:02 PM
    DESCRIPTION       : ESTA FUNCIÓN ACTUALIZA LOS CAMPOS DEPENDENCIA, RESPONSABLE, 
                        MOVIMIENTO FINAL DE LA TABLA DEVOLUTIVO CUANDO SE REALIZA LA 
                        ELIMINACION DE ALGÚN DETALLE DEL MOVIMEINTO, CON LOS DATOS DEL ÚLTIMO MOVIMIENTO.
    MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA, JUAN CARLOS RODR�?GUEZ AMÉZQUITA
    DATE MODIFIED	    : 18/01/2017, 29/06/2017
    TIME				      : 9:42 AM, 12:23 PM, 05:15 PM
    MODIFICATIONS	    : Cambio de estándar de codificación y adición de manejo de excepciones
                        Ajuste tipos de datos para las variables definidas en la función.
                        Activación de la placa una vez que se elimina el registro del detalle según el tipo de movimiento.
  */
  (
    UN_COMPANIA                     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ELEMENTO                     IN VARCHAR2,
    UN_SERIE                        IN PCK_SUBTIPOS.TI_ENTERO_LARGO
  )
  RETURN NUMBER
AS
  MI_RTA                            VARCHAR2(32000 CHAR);
  MI_DATO                           VARCHAR2(2 CHAR);
  MI_SERIE                          PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_COMPANIA                       PCK_SUBTIPOS.TI_COMPANIA;
  MI_TIPOMOVIMIENTOU                MOVIMIENTO.TIPOMOVIMIENTO%TYPE;
  MI_MOVIMIENTOU                    PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_DEPENDENCIA_DESTINO            MOVIMIENTO.DEPENDENCIA_DESTINO%TYPE;
  MI_RESPONSABLE_DESTINO            MOVIMIENTO.RESPONSABLE_DESTINO%TYPE;
  MI_SUCURSAL_RESDESTINO            MOVIMIENTO.SUCURSAL_RESDESTINO%TYPE;
  MI_FECHA                          DATE;
  MI_TIPOMOVIMIENTO                 MOVIMIENTO.TIPOMOVIMIENTO%TYPE;
  MI_NUMERO                         MOVIMIENTO.NUMERO%TYPE;
  MI_CONDICION                      PCK_SUBTIPOS.TI_CONDICION;
  MI_CAMPOS                         PCK_SUBTIPOS.TI_CAMPOS;
  MI_BODEGA_DESTINO                 MOVIMIENTO.BODEGA_DESTINO%TYPE;
  MI_CLASE_BODEGA_DESTINO           MOVIMIENTO.CLASE_BODEGA_DESTINO%TYPE;
BEGIN
  PCK_ENTORNO.PR_SETFECHAHORA(UN_FECHAHORA => SYSDATE);
  -- Extracción de datos del último movimiento para el elemento
  BEGIN
    SELECT *
    INTO MI_SERIE ,
      MI_TIPOMOVIMIENTOU ,
      MI_MOVIMIENTOU,
      MI_BODEGA_DESTINO,
      MI_CLASE_BODEGA_DESTINO
    FROM
      (SELECT V_ULTIMOMOVIMIENTO_ALM_SERIE.SERIE ,
        V_ULTIMOMOVIMIENTO_ALM_SERIE.TIPOMOVIMIENTO ,
        V_ULTIMOMOVIMIENTO_ALM_SERIE.MOVIMIENTO ,
        V_ULTIMOMOVIMIENTO_ALM_SERIE.BODEGA_DESTINO ,
        V_ULTIMOMOVIMIENTO_ALM_SERIE.CLASE_BODEGA_DESTINO 
      FROM V_ULTIMOMOVIMIENTO_ALM_SERIE
      WHERE COMPANIA = UN_COMPANIA
      AND ELEMENTO   = UN_ELEMENTO
      AND SERIE      = UN_SERIE
      )
    WHERE ROWNUM = 1;
  EXCEPTION
  WHEN NO_DATA_FOUND THEN
    RETURN -1;
  END;
  --
  BEGIN
    SELECT DEPENDENCIA_DESTINO ,
      RESPONSABLE_DESTINO ,
      SUCURSAL_RESDESTINO ,
      FECHA ,
      TIPOMOVIMIENTO ,
      NUMERO 
    INTO MI_DEPENDENCIA_DESTINO ,
      MI_RESPONSABLE_DESTINO ,
      MI_SUCURSAL_RESDESTINO ,
      MI_FECHA ,
      MI_TIPOMOVIMIENTO ,
      MI_NUMERO 
    FROM MOVIMIENTO
    WHERE COMPANIA     = UN_COMPANIA
    AND TIPOMOVIMIENTO = MI_TIPOMOVIMIENTOU
    AND NUMERO         = MI_MOVIMIENTOU;
    BEGIN
      BEGIN
        MI_CAMPOS:= ' DEPENDENCIA          = '''||MI_DEPENDENCIA_DESTINO||'''
                    ,RESPONSABLE          = '''||MI_RESPONSABLE_DESTINO ||'''
                    ,SUCURSAL_RESPONSABLE = '''||MI_SUCURSAL_RESDESTINO||'''
                    ,FECHAULTMOV          = '||PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA => MI_FECHA)||'
                    ,TIPOMOVIMIENTOF      = '''||MI_TIPOMOVIMIENTO||'''
                    ,MOVIMIENTOF          = '''||MI_NUMERO||'''';
        MI_CONDICION:=' COMPANIA = '''||UN_COMPANIA||'''
                   AND ELEMENTO = '''||UN_ELEMENTO||'''
                   AND SERIE    =  '||MI_SERIE||'';
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'DEVOLUTIVO'
                                               ,UN_ACCION    => 'M'
                                               ,UN_CAMPOS    => MI_CAMPOS
                                               ,UN_CONDICION => MI_CONDICION);
      EXCEPTION
      WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
      END;
    EXCEPTION
    WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
      PCK_ERR_MSG.RAISE_WITH_MSG( 
        UN_EXC_COD => SQLCODE ,
        UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_UPDATE_DEVOLUTIVO );
    END;
  EXCEPTION
  WHEN NO_DATA_FOUND THEN
    RETURN -1;
  END;
  /* Habilita la placa una vez que se elimina el registro del movimiento, 
  cuando el devolutivo tiene como bodega final: DESTRUIDOS o  DONACIONES*/
  DECLARE
    MI_CLASE_BODEGA                         MOVIMIENTO.CLASE_BODEGA_DESTINO%TYPE;
  BEGIN
    SELECT CLASE_BODEGA
      INTO MI_CLASE_BODEGA
      FROM DEVOLUTIVO
     WHERE COMPANIA = UN_COMPANIA
       AND ELEMENTO = UN_ELEMENTO
       AND SERIE    = UN_SERIE;
    --
    IF MI_CLASE_BODEGA IN('60','80') THEN
      MI_CAMPOS := 'PLACAANULADA = 0
                  , FECHAANULADA = NULL
                  , BODEGA       = '''||MI_BODEGA_DESTINO||'''
                  , CLASE_BODEGA = '''||MI_CLASE_BODEGA_DESTINO||'''';
      MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA ||'''
                   AND ELEMENTO = ''' || UN_ELEMENTO ||'''
                   AND SERIE    =  '  || MI_SERIE    ||'';
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'DEVOLUTIVO'
                                           , UN_ACCION    => 'M'
                                           , UN_CAMPOS    => MI_CAMPOS
                                           , UN_CONDICION => MI_CONDICION);
    END IF;
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      NULL;
    WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
      RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
    WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD => SQLCODE ,
        UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_HABILITANDO_PLACA);
  END;
  RETURN 0;
END FC_ACTUALIZADEVOLUTIVO;

FUNCTION FC_ACTUALIZAVALORTOTAL
 /*
    NAME              : FC_ACTUALIZAVALORTOTAL
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JAVIER VILLATE
    DATE MIGRADOR     : 27/05/2016
    TIME              : 8:30 AM
    SOURCE MODULE     : ALMACEN
    DATE MODIFIED     : 27/05/2016
    TIME              : 04:02 PM
    DESCRIPTION       : Actualiza el valor total y el total con ajuste de la tabla movimiento
                        cuando eliminan un registro del detalle del movimiento, esta función actualiza el valor total.
    MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA ; JUAN CARLOS RODR�?GUEZ AMÉZQUITA;JUAN PABLO ACEVEDO TORRES
    DATE MODIFIED	    : 18/01/2017 / 28/06/2017 / 13/07/2017 / 18/07/2017 / 13/01/2021
    TIME				      : 10:11 AM  / 02:44 PM / 12:38 AM / 09:24 AM
    MODIFICATIONS	    : Se cambió el estándar de codificación y se agrego manejo de excepciones.
                        Ajuste en el cálculo del valor total, el cuál debe ser igual al valor unitario,
                        así la cantidad sea cero, para los conceptos "Corrección de Valor".
                        Actualización del VALORDOCASOCIADO cuando se modifica el detalle de movimiento.
                        Adición de cálculo de Ajuste al Peso Final (AJUSTECENTAVOS1).
                        se creo la validacion por parametro (AJUSTE AL PESO).
  */
(
  UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOMOVIMIENTO           IN VARCHAR2,
  UN_MOVIMIENTO               IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_PORCIVA                  IN PCK_SUBTIPOS.TI_DOBLE,
  UN_VALORUNITARIO            IN PCK_SUBTIPOS.TI_DOBLE,
  UN_CANTIDAD_NUEVA           IN PCK_SUBTIPOS.TI_DOBLE,
  UN_CODIGO                   IN PCK_SUBTIPOS.TI_ENTERO_LARGO

)
RETURN PCK_SUBTIPOS.TI_LOGICO
AS
  MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
  MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORIVA               PCK_SUBTIPOS.TI_DOBLE :=0;
  MI_DATO                   VARCHAR2(3 CHAR);
  MI_REDONDEO               PCK_SUBTIPOS.TI_ENTERO; --JM 19/11/2024 7801979
  MI_PARAMETRO              VARCHAR2(3);
  MI_VALORTOTAL_PEPS        PCK_SUBTIPOS.TI_DOBLE;
  MI_VALORTOTALT            PCK_SUBTIPOS.TI_DOBLE;
  MI_PARAMETRO_PEPS         PCK_SUBTIPOS.TI_PARAMETRO;
  MI_ELEMENTO               D_MOVIMIENTO.ELEMENTO%TYPE;
  MI_PARCORTE               VARCHAR2(3200 CHAR);
  MI_RTA                    CLOB;
  MI_AIU                    MOVIMIENTO.AIU%TYPE;
  MI_PORC_AIU               D_MOVIMIENTO.PORC_AIU%TYPE;
  MI_VLR_AIU                D_MOVIMIENTO.VLR_AIU%TYPE;

BEGIN
  --JM 19/11/2024 7801979
    MI_REDONDEO := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                  UN_NOMBRE    => 'REDONDEO VALOR',
                                                  UN_MODULO    => '10',
                                                  UN_FECHA_PAR => SYSDATE),'2'));
  BEGIN                                               
    SELECT AIU
     INTO MI_AIU
     FROM MOVIMIENTO
    WHERE MOVIMIENTO.COMPANIA     = UN_COMPANIA
      AND MOVIMIENTO.TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
      AND MOVIMIENTO.NUMERO     = UN_MOVIMIENTO;
   EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_AIU := 0;
   END;                                                
  --ACTUALIZA EL VALOR TOTAL Y EL TOTAL CON AJUSTE DE LA TABLA MOVIMIENTO
  --CUANDO ELIMINAN UN REGISTRO DEL DETALLE DEL MOVIMIENTO
    BEGIN
    SELECT DISTINCT 'X'
    INTO MI_DATO
    FROM D_MOVIMIENTO
    WHERE D_MOVIMIENTO.COMPANIA     = UN_COMPANIA
    AND D_MOVIMIENTO.TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
    AND D_MOVIMIENTO.MOVIMIENTO     = UN_MOVIMIENTO;
  EXCEPTION
  WHEN NO_DATA_FOUND THEN
    BEGIN
      BEGIN
        MI_CAMPOS        := 'VALORTOTAL        = 0
          ,VALORIVA         = 0
          ,VALORTOTALCONIVA = 0
          ,TOTALCONAJUSTE   = 0
          ,VALORIMPOCONSUMO = 0';
        MI_CONDICION     :='MOVIMIENTO.COMPANIA       = '''||UN_COMPANIA ||'''
          AND MOVIMIENTO.TIPOMOVIMIENTO = '''||UN_TIPOMOVIMIENTO||'''
          AND MOVIMIENTO.NUMERO         = '||UN_MOVIMIENTO||'';
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'MOVIMIENTO' ,UN_ACCION => 'M' ,UN_CAMPOS => MI_CAMPOS ,UN_CONDICION => MI_CONDICION);
      EXCEPTION
      WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
      END;
      RETURN -1;
    EXCEPTION
    WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
      PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE ,UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_UPDATE_MOVIMIENTO );
    END;
  END;

  IF MI_REDONDEO > 0 THEN 
    MI_VALORIVA := ROUND((UN_VALORUNITARIO - (UN_VALORUNITARIO / (1 + UN_PORCIVA / 100))) * UN_CANTIDAD_NUEVA, MI_REDONDEO); -- parametro redondeo JM 19/11/2024 7801979
  ELSE 
    MI_VALORIVA := ROUND((ROUND((UN_VALORUNITARIO / (1 + UN_PORCIVA / 100)),0) * UN_CANTIDAD_NUEVA) * (UN_PORCIVA/100),0); --JM 20/12/2024 CC328
  END IF;

  DECLARE
    MI_VALORUNITARIO_ANTESIVA     PCK_SUBTIPOS.TI_DOBLE;
    MI_VALORTOTAL                 PCK_SUBTIPOS.TI_DOBLE;
    MI_VALORTOTALCONIVA           PCK_SUBTIPOS.TI_DOBLE;
    MI_CONCEPTO                   TIPOMOVIMIENTO.CONCEPTO%TYPE;
    MI_VLRIMPCONSUMO              NUMBER(15,2);
    MI_PORCIMPCONSUMO             NUMBER(15,2);
    MI_VLRUNITARIO                NUMBER(15,2);
    MI_VLRIMPCONSUMO_UNITARIO     NUMBER(20,4);
    MI_VLRIVA_UNITARIO            NUMBER(20,4);

  BEGIN

       --lvega se agrega calculo de VALOR IMPUESTO AL CONSUMO dependiendo del parametro
        MI_PARAMETRO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA,
									          UN_NOMBRE    => 'APLICAR IMPOCONSUMO EN ENTRADAS DE ALMACEN',
									          UN_MODULO    => '10',
									          UN_FECHA_PAR => SYSDATE),'NO');
        IF MI_PARAMETRO = 'SI' THEN                                           
            BEGIN
            SELECT PORC_IMPCONSUMO, VLRUNITARIO_ANTESIVA INTO MI_PORCIMPCONSUMO, MI_VALORUNITARIO_ANTESIVA FROM D_MOVIMIENTO 
            WHERE D_MOVIMIENTO.COMPANIA       = UN_COMPANIA 
                      AND D_MOVIMIENTO.TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
                      AND D_MOVIMIENTO.MOVIMIENTO     = UN_MOVIMIENTO
                      AND D_MOVIMIENTO.CODIGO         = UN_CODIGO;
             EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_PORCIMPCONSUMO := 0;
                    MI_VALORUNITARIO_ANTESIVA := 0;
             END;
            MI_VLRIMPCONSUMO_UNITARIO := (MI_VALORUNITARIO_ANTESIVA * (MI_PORCIMPCONSUMO / 100));
            MI_VLRIMPCONSUMO := ROUND(MI_VLRIMPCONSUMO_UNITARIO* UN_CANTIDAD_NUEVA , MI_REDONDEO);
        ELSE
            MI_VLRIMPCONSUMO := 0;
            MI_PORCIMPCONSUMO := 0;
            MI_VALORUNITARIO_ANTESIVA := ROUND(UN_VALORUNITARIO / (1 + UN_PORCIVA / 100), MI_REDONDEO); -- parametro redondeo JM 19/11/2024 7801979
        END IF;

        /*
     Se debe tener en cuenta que si es una "Correción de Valor" el valor
     total es igual al valor unitario, así la cantidad sea cero.
     */
    SELECT CONCEPTO
      INTO MI_CONCEPTO
      FROM TIPOMOVIMIENTO
     WHERE COMPANIA = UN_COMPANIA
       AND CODIGO   = UN_TIPOMOVIMIENTO;
    IF MI_REDONDEO > 0 THEN 
		IF MI_PARAMETRO = 'SI' THEN
          MI_VLRIVA_UNITARIO :=  ROUND(MI_VALORUNITARIO_ANTESIVA * (UN_PORCIVA / 100), MI_REDONDEO);
          MI_VALORIVA := ROUND(MI_VLRIVA_UNITARIO * UN_CANTIDAD_NUEVA, MI_REDONDEO);
        	MI_VLRUNITARIO := CASE MI_CONCEPTO WHEN 'CR' THEN UN_VALORUNITARIO
                              ELSE MI_VALORUNITARIO_ANTESIVA + MI_VLRIMPCONSUMO_UNITARIO + MI_VLRIVA_UNITARIO END;
            MI_VALORTOTAL :=  CASE MI_CONCEPTO WHEN 'CR' THEN UN_VALORUNITARIO
                              ELSE MI_VLRUNITARIO * UN_CANTIDAD_NUEVA END;
   	 	ELSE
   	    MI_VALORIVA := ROUND((UN_VALORUNITARIO - (UN_VALORUNITARIO / (1 + UN_PORCIVA / 100))) * UN_CANTIDAD_NUEVA, MI_REDONDEO); -- parametro redondeo JM 19/11/2024 7801979	
        MI_VALORTOTAL := CASE MI_CONCEPTO WHEN 'CR' THEN UN_VALORUNITARIO
                     ELSE UN_VALORUNITARIO  * UN_CANTIDAD_NUEVA
                     END;
          MI_VLRUNITARIO := UN_VALORUNITARIO;           
     END IF; 
    ELSE 
        MI_VALORIVA := ROUND((ROUND((UN_VALORUNITARIO / (1 + UN_PORCIVA / 100)),0) * UN_CANTIDAD_NUEVA) * (UN_PORCIVA/100),0); --JM 20/12/2024 CC328
        MI_VALORTOTAL := CASE MI_CONCEPTO WHEN 'CR' THEN UN_VALORUNITARIO
                     ELSE (ROUND((UN_VALORUNITARIO / (1 + UN_PORCIVA / 100)),0) * UN_CANTIDAD_NUEVA) + MI_VALORIVA + 
                                      CASE WHEN MI_PARAMETRO = 'SI' THEN ROUND(MI_VLRIMPCONSUMO,0) ELSE 0 END 
                         END;
        MI_VLRUNITARIO := CASE WHEN MI_PARAMETRO = 'SI' THEN UN_VALORUNITARIO + ROUND(MI_VLRIMPCONSUMO,0) ELSE UN_VALORUNITARIO END;             
    END IF; --JM 20/12/2024 CC328 -- LVEGA CC481
    
    IF MI_AIU NOT IN (0) THEN 
       BEGIN
        SELECT PORC_IMPCONSUMO, VLRUNITARIO_ANTESIVA, PORC_AIU, VLR_AIU INTO MI_PORCIMPCONSUMO, MI_VALORUNITARIO_ANTESIVA, MI_PORC_AIU, MI_VLR_AIU
         FROM D_MOVIMIENTO
        WHERE D_MOVIMIENTO.COMPANIA       = UN_COMPANIA
                  AND D_MOVIMIENTO.TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
                  AND D_MOVIMIENTO.MOVIMIENTO     = UN_MOVIMIENTO
                  AND D_MOVIMIENTO.CODIGO         = UN_CODIGO;
       EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_PORCIMPCONSUMO := 0;
          MI_VALORUNITARIO_ANTESIVA := 0;
          MI_PORC_AIU := 0;
          MI_VLR_AIU := 0;
       END;
      MI_VLR_AIU := ((NVL(MI_VALORUNITARIO_ANTESIVA,0) * NVL(MI_PORC_AIU,0) /100) * UN_CANTIDAD_NUEVA);
      MI_VLRUNITARIO := ROUND(NVL(MI_VALORUNITARIO_ANTESIVA,0) + (NVL(MI_VALORUNITARIO_ANTESIVA,0) * NVL(MI_PORC_AIU,0) /100) + ((NVL(MI_VALORUNITARIO_ANTESIVA,0) * NVL(MI_PORC_AIU,0) /100) * NVL(UN_PORCIVA,0) / 100) + ((NVL(MI_VALORUNITARIO_ANTESIVA,0) * NVL(MI_PORC_AIU,0) /100) * MI_PORCIMPCONSUMO/100), MI_REDONDEO);
      MI_VALORTOTAL := ROUND(NVL(MI_VLRUNITARIO, 0) * UN_CANTIDAD_NUEVA, MI_REDONDEO);
      MI_VALORIVA := ROUND((NVL(MI_VLR_AIU,0) * NVL(UN_PORCIVA,0) / 100), MI_REDONDEO);
      MI_VLRIMPCONSUMO := ROUND(NVL(MI_VLR_AIU,0) * NVL(MI_PORCIMPCONSUMO,0), MI_REDONDEO);
    END IF;
    MI_VALORTOTALCONIVA := MI_VALORTOTAL;
    -- amonroy, 14/11/2018, Se agregan los campos NIIF_VALOR_TOTAL y NIIF_VALOR_BASE
    MI_CAMPOS:= 'VALORTOTAL       ='|| MI_VALORTOTAL ||'
                ,VALORIVA         ='|| MI_VALORIVA ||'
                ,VALORUNITARIO    ='|| MI_VLRUNITARIO || '
                ,VALORTOTALCONIVA ='|| MI_VALORTOTALCONIVA ||'
                ,VALORBASE        ='|| MI_VALORUNITARIO_ANTESIVA ||' * '||UN_CANTIDAD_NUEVA || '
                ,NIIF_VALOR_TOTAL ='|| MI_VALORTOTAL ||'
                ,NIIF_VALOR_BASE  ='|| MI_VALORUNITARIO_ANTESIVA ||' * '||UN_CANTIDAD_NUEVA || '
                ,VLR_AIU          ='|| NVL(MI_VLR_AIU,0) ||'
                ,VLRIMPCONSUMO    ='|| MI_VLRIMPCONSUMO ||'';
    MI_CONDICION:='D_MOVIMIENTO.COMPANIA       = '''||UN_COMPANIA ||'''
              AND D_MOVIMIENTO.TIPOMOVIMIENTO = '''||UN_TIPOMOVIMIENTO||'''
              AND D_MOVIMIENTO.MOVIMIENTO     = '||UN_MOVIMIENTO||'
              AND D_MOVIMIENTO.CODIGO         = '||UN_CODIGO||'';
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'D_MOVIMIENTO'
                                          ,UN_ACCION    => 'M'
                                          ,UN_CAMPOS    => MI_CAMPOS
                                          ,UN_CONDICION => MI_CONDICION);
  EXCEPTION
    WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
      RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
    WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
      UN_EXC_COD    => SQLCODE,
      UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_UPDATE_DMOV);
  END;
  --
  BEGIN
    DECLARE
      MI_TOTALCONAJUSTE_DEC                 PCK_SUBTIPOS.TI_DOBLE;
      MI_AJUSTEPESOFINAL                    PCK_SUBTIPOS.TI_DOBLE;
      MI_VALORTOTAL                         PCK_SUBTIPOS.TI_DOBLE;
      MI_TOTALAJUSTADO                      PCK_SUBTIPOS.TI_DOBLE;
      MI_VALORIVA                           PCK_SUBTIPOS.TI_DOBLE;
      MI_VALORTOTALCONIVA                   PCK_SUBTIPOS.TI_DOBLE;
      MI_REDONDEOTOTAL                      PCK_SUBTIPOS.TI_ENTERO;
      MI_AJUSTEALPESO                       PCK_SUBTIPOS.TI_VALORES:='SI';
      MI_VLRIMPCONSUMO                      PCK_SUBTIPOS.TI_DOBLE;
      MI_VALORTOTALAIU                      PCK_SUBTIPOS.TI_DOBLE;
    BEGIN

      MI_REDONDEOTOTAL:= TO_NUMBER(PCK_PARST.FC_PAR(UN_PARAMETRO  =>  'DIGITOS REDONDEO VALOR UNITARIO ENTRADA CONSUMO',
                                                  UN_VLOMISION  =>   '0'));
      MI_AJUSTEALPESO:= PCK_PARST.FC_PAR(UN_PARAMETRO  =>  'MANEJA AJUSTE AL PESO',
                                                  UN_VLOMISION  =>   'NO');



      -- Cálculo del Total con Ajuste
      SELECT SUM(D_MOVIMIENTO.VALORTOTAL) + MOVIMIENTO.AJUSTE_DEC_MANUAL
        INTO MI_TOTALCONAJUSTE_DEC
        FROM D_MOVIMIENTO
       INNER JOIN MOVIMIENTO
          ON D_MOVIMIENTO.COMPANIA = MOVIMIENTO.COMPANIA
         AND D_MOVIMIENTO.TIPOMOVIMIENTO = MOVIMIENTO.TIPOMOVIMIENTO
         AND D_MOVIMIENTO.MOVIMIENTO = MOVIMIENTO.NUMERO
       WHERE D_MOVIMIENTO.COMPANIA       = UN_COMPANIA
         AND D_MOVIMIENTO.TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
         AND D_MOVIMIENTO.MOVIMIENTO     = UN_MOVIMIENTO
       GROUP BY MOVIMIENTO.AJUSTE_DEC_MANUAL;    


     SELECT SUM(D_MOVIMIENTO.VALORBASE),
            SUM(D_MOVIMIENTO.VALORIVA),
            SUM(D_MOVIMIENTO.VALORTOTALCONIVA),
            SUM(D_MOVIMIENTO.VLRIMPCONSUMO),
            SUM(D_MOVIMIENTO.VLR_AIU)
       INTO MI_VALORTOTAL, MI_VALORIVA, MI_VALORTOTALCONIVA,MI_VLRIMPCONSUMO, MI_VALORTOTALAIU
       FROM D_MOVIMIENTO
      WHERE D_MOVIMIENTO.COMPANIA       = UN_COMPANIA
        AND D_MOVIMIENTO.TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
        AND D_MOVIMIENTO.MOVIMIENTO     = UN_MOVIMIENTO;


     --valida si la entidad quiere que se ajuste el valor al peso sin decimales en el total 


    IF MI_AJUSTEALPESO='SI' THEN 
     -- Cálculo del Ajuste al Peso Final
     MI_AJUSTEPESOFINAL := ROUND(ROUND(MI_TOTALCONAJUSTE_DEC, 9) - ROUND(MI_TOTALCONAJUSTE_DEC,  MI_REDONDEOTOTAL), 2);
     -- Cálculo del valor total del movimiento
     MI_TOTALAJUSTADO := MI_TOTALCONAJUSTE_DEC - MI_AJUSTEPESOFINAL;
     -- Cálculo valor base, IVA y total con Iva.
     ELSE
     MI_AJUSTEPESOFINAL :=0;
     MI_TOTALAJUSTADO :=MI_TOTALCONAJUSTE_DEC;
     END IF;
     -- Actualización de valores totales del Movimiento.
     MI_CAMPOS := '  VALORTOTAL       = ' || MI_VALORTOTAL           ||
                  ', VALORIVA         = ' || MI_VALORIVA             ||
                  ', VALORTOTALCONIVA = ' || MI_VALORTOTALCONIVA     ||
                  ', AJUSTECENTAVOS1  = ' || ABS(MI_AJUSTEPESOFINAL) ||
                  ', TOTALCONAJUSTE   = ' || MI_TOTALAJUSTADO        ||
                  ', VALORDOCASOCIADO = ' || MI_TOTALAJUSTADO        ||
                  ', VALORIMPOCONSUMO = ' || MI_VLRIMPCONSUMO        ||
                  ', VALORTOTALAIU    = ' || MI_VALORTOTALAIU;
     MI_CONDICION:='COMPANIA           = ''' || UN_COMPANIA         || '''
                    AND TIPOMOVIMIENTO = ''' || UN_TIPOMOVIMIENTO   || '''
                    AND NUMERO         =   ' || UN_MOVIMIENTO       || '';
     PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (  UN_TABLA      => 'MOVIMIENTO'
                                            , UN_ACCION     => 'M'
                                            , UN_CAMPOS     => MI_CAMPOS
                                            , UN_CONDICION  => MI_CONDICION);
    --
    EXCEPTION
      WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
    END;
    --RETURN -1;
  EXCEPTION
    WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
      , UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_UPDATE_MOVIMIENTO);
  END;

  MI_PARAMETRO_PEPS := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA,
									          UN_NOMBRE    => 'MANEJA PEPS EN CONSUMO ALMACEN',
									          UN_MODULO    => '10',
									          UN_FECHA_PAR => SYSDATE),'NO');
                                              
   IF MI_PARAMETRO_PEPS = 'SI' THEN 

      BEGIN 
          SELECT VALORTOTAL, VALORTOTAL_PEPS, ELEMENTO
          INTO MI_VALORTOTALT, MI_VALORTOTAL_PEPS, MI_ELEMENTO
      FROM D_MOVIMIENTO
      WHERE COMPANIA = UN_COMPANIA
      AND TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
      AND MOVIMIENTO = UN_MOVIMIENTO
      AND CODIGO = UN_CODIGO;
      EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_VALORTOTALT := 0;
          MI_VALORTOTAL_PEPS := 0;
      END;
      
        IF MI_VALORTOTALT <> MI_VALORTOTAL_PEPS THEN
                BEGIN
                    MI_CAMPOS:= 'VALORTOTAL       ='|| MI_VALORTOTAL_PEPS ||'
                                ,VALORBASE        ='|| MI_VALORTOTAL_PEPS || '
                                ,NIIF_VALOR_TOTAL ='|| MI_VALORTOTAL_PEPS ||'
                                ,NIIF_VALOR_BASE  ='|| MI_VALORTOTAL_PEPS || '';
                    MI_CONDICION:='D_MOVIMIENTO.COMPANIA        = '''||UN_COMPANIA ||'''
                                AND D_MOVIMIENTO.TIPOMOVIMIENTO = '''||UN_TIPOMOVIMIENTO||'''
                                AND D_MOVIMIENTO.MOVIMIENTO     = '  ||UN_MOVIMIENTO||'
                                AND D_MOVIMIENTO.CODIGO         = '  ||UN_CODIGO||'';
                                
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'D_MOVIMIENTO'
                                                  ,UN_ACCION    => 'M'
                                                  ,UN_CAMPOS    => MI_CAMPOS
                                                  ,UN_CONDICION => MI_CONDICION);
                EXCEPTION
                    WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                    WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_UPDATE_DMOV);
                END;
          END IF;
    
     MI_PARCORTE := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                         UN_NOMBRE    => 'FECHA DE CORTE PARA INICIO DEL ALMACEN',
                                         UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN,
                                         UN_FECHA_PAR => SYSDATE);
                                         
     MI_RTA := FC_ACTUALIZAR_SALDOPEPS(UN_COMPANIA          => UN_COMPANIA,
                                       UN_ANOINICIAL        => PCK_SYSMAN_UTL.FC_ANIO(TO_DATE(MI_PARCORTE,'DD/MM/YYYY')),
                                       UN_MESINICIAL        => PCK_SYSMAN_UTL.FC_MES(TO_DATE(MI_PARCORTE,'DD/MM/YYYY')),
                                       UN_ANOFINAL          => PCK_SYSMAN_UTL.FC_ANIO(SYSDATE),
                                       UN_MESFINAL          => PCK_SYSMAN_UTL.FC_MES(SYSDATE),
                                       UN_ELEMENTOINICIAL   => MI_ELEMENTO,
                                       UN_ELEMENTOFINAL     => MI_ELEMENTO);
    
    END IF;

  RETURN -1;
END FC_ACTUALIZAVALORTOTAL;



FUNCTION FC_REVERSARMOVIMIENTO
 /*
    NAME              : FC_REVERSARMOVIMIENTO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JAVIER VILLATE
    DATE MIGRADOR     : 10/11/2016
    TIME              : 8:30 AM
    SOURCE MODULE     : ALMACEN
    DATE MODIFIED     : 10/11/2016
    TIME              : 04:02 PM
    DESCRIPTION       : --REVERSA EL MOVIMIENTO ENVIADO A LA FUNCION VERIFICANDO QUE NO EXISTAN MOVIMIENTOS POSTERIORES
                        --TANTO PARA ELEMENTOS DEVOLUTIVOS COMO ELEMENTOS DE CONSUMO.
    MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED	    : 18/01/2017
    TIME				      : 11:37 AM
    MODIFICATIONS	    : Se cambió el estándar de codificación
                        y se agrego manejo de excepciones

  */
  (
  UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOMOVIMIENTO           IN VARCHAR2,
  UN_MOVIMIENTO               IN PCK_SUBTIPOS.TI_ENTERO_LARGO
  )
  RETURN CLOB
AS
  MI_RTA                    CLOB;
  MI_ESTADO                 VARCHAR2(5 CHAR);
  MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
  MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
  MI_FECHA                  DATE;
  MI_ANO                    PCK_SUBTIPOS.TI_ENTERO;
  MI_MES                    PCK_SUBTIPOS.TI_ENTERO;
  MI_RS                     SYS_REFCURSOR;
  MI_RSPOSTERIOR            SYS_REFCURSOR;
  MI_TIPO_CNT               VARCHAR2(3);
  MI_COMPR_CNT              NUMBER(20);
  EXCEPTION_ALERT           EXCEPTION;
  MI_PARAMETRO              VARCHAR2(3);
  MI_NUMERO        PCK_SUBTIPOS.TI_ENTERO_LARGO; --JM CC555 24/12/2024  
  MI_CONTAR        PCK_SUBTIPOS.TI_ENTERO_LARGO; --JM CC555 24/12/2024
  MI_PAR_INTERC             VARCHAR2(320 CHAR);
  MI_DEPENDENCIA            MOVIMIENTO.DEPENDENCIA_ORIGEN%TYPE;
  MI_COMPANIA_DES           PCK_SUBTIPOS.TI_COMPANIA;
  MI_MOVIMIENTO_DES         MOVIMIENTO.TIPOMOVIMIENTO%TYPE;
  MI_NUMEROCOMPR            MOVIMIENTO.NUMERO%TYPE;

BEGIN
	
	MI_PARAMETRO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA,
									          UN_NOMBRE    => 'MANEJA CAUSACION AUTOMATICA',
									          UN_MODULO    => '1',
									          UN_FECHA_PAR => SYSDATE),'NO');
                                              
    MI_PAR_INTERC := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                          ,UN_NOMBRE    =>'ALMACEN INTERCOMPANIAS'
                                          ,UN_MODULO    => -1
                                          ,UN_FECHA_PAR => SYSDATE);                                             

    IF MI_PARAMETRO = 'SI' THEN
     BEGIN
          
          SELECT TIPO_CPTE_CONTABLE, 
                 CPTE_CONTABLE 
            INTO MI_TIPO_CNT,
                 MI_COMPR_CNT
             FROM MOVIMIENTO
            WHERE COMPANIA       = UN_COMPANIA
              AND TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
              AND NUMERO         = UN_MOVIMIENTO;
            --JM INI CC555 24/12/2024  
            IF MI_TIPO_CNT IS NOT NULL OR MI_COMPR_CNT IS NOT NULL THEN
                BEGIN
                    SELECT COUNT(NUMERO)
                        INTO MI_CONTAR
                        FROM COMPROBANTE_CNT
                        WHERE COMPANIA = UN_COMPANIA
                        AND TIPO = MI_TIPO_CNT
                        AND NUMERO = MI_COMPR_CNT;
                END;
            
                    IF MI_CONTAR = 0 THEN
                           BEGIN
                            MI_CAMPOS := 'TIPO_CPTE_CONTABLE  = '''',
                                          CPTE_CONTABLE       = '''' ';
                            MI_CONDICION := 'COMPANIA           = '''||UN_COMPANIA||'''
                                            AND TIPOMOVIMIENTO  = '''||UN_TIPOMOVIMIENTO||'''
                                            AND NUMERO          = '||MI_COMPR_CNT||'';
                            BEGIN
                                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'MOVIMIENTO'
                                                                     ,UN_ACCION    => 'M'
                                                                     ,UN_CAMPOS    => MI_CAMPOS
                                                                     ,UN_CONDICION => MI_CONDICION);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                            END;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                                      ,UN_ERROR_COD  => PCK_ERRORES.ERRR_ALMACEN_ACTUALIZA_REGIST);
                        END;
                    ELSE
                        RAISE EXCEPTION_ALERT;
                    END IF;
                
            END IF; --JM FIN CC555 24/12/2024  

      EXCEPTION WHEN EXCEPTION_ALERT THEN
        MI_RTA := 'El movimiento ' || UN_TIPOMOVIMIENTO || ' ' || UN_MOVIMIENTO ||
                                ' tiene interfaz contable con el comprobante ' || MI_TIPO_CNT || ' ' || MI_COMPR_CNT ||
                                '. Primero modifique o elimine el comprobante contable.';
       RETURN MI_RTA;
     END;
  END IF;

  IF MI_PAR_INTERC = 'SI' THEN
  
    BEGIN
         SELECT DEPENDENCIA_DESTINO
            INTO MI_DEPENDENCIA
         FROM MOVIMIENTO
         WHERE COMPANIA = UN_COMPANIA
         AND TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
         AND NUMERO = UN_MOVIMIENTO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_DEPENDENCIA := '';
    END; 
    
    BEGIN
        SELECT COMP_DESTINO, MOV_DESTINO 
            INTO MI_COMPANIA_DES, MI_MOVIMIENTO_DES 
        FROM DEPENDENCIA 
        WHERE COMPANIA = UN_COMPANIA
        AND CODIGO = MI_DEPENDENCIA
        AND MAN_COMPANIADES = -1;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_COMPANIA_DES := ''; 
        MI_MOVIMIENTO_DES := '';
    END;
    
    IF MI_COMPANIA_DES IS NOT NULL OR MI_MOVIMIENTO_DES IS NOT NULL THEN
        
        BEGIN   
            SELECT MOVIMIENTO
                INTO MI_NUMEROCOMPR
            FROM D_MOVIMIENTO
            WHERE COMPANIA = MI_COMPANIA_DES
            AND TIPOMOVASOCIADO = UN_TIPOMOVIMIENTO 
            AND MOVASOCIADO = UN_MOVIMIENTO
            AND TIPOMOVIMIENTO = MI_MOVIMIENTO_DES
            AND ROWNUM = 1;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_NUMEROCOMPR := 0;
        END;
        
        IF MI_NUMEROCOMPR <> 0 THEN
            BEGIN
                BEGIN
                  MI_CONDICION:='COMPANIA = '''||MI_COMPANIA_DES||'''
                             AND TIPOMOVIMIENTO = '''||MI_MOVIMIENTO_DES||'''
                             AND MOVIMIENTO     = '||MI_NUMEROCOMPR;
        
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'D_MOVIMIENTO'
                                                         ,UN_ACCION    => 'E'
                                                         ,UN_CONDICION => MI_CONDICION);
        
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                END;
        
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                                ,UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_ELIM_D_MOVIMIENTO
                                                );
          END;
          
          BEGIN
                BEGIN
                  MI_CONDICION:='COMPANIA = '''||MI_COMPANIA_DES||'''
                             AND TIPOMOVIMIENTO = '''||MI_MOVIMIENTO_DES||'''
                             AND NUMERO     = '||MI_NUMEROCOMPR;
        
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'MOVIMIENTO'
                                                         ,UN_ACCION    => 'E'
                                                         ,UN_CONDICION => MI_CONDICION);
        
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                END;
        
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                                ,UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_ELIM_D_MOVIMIENTO
                                                );
          END;
          
          PCK_ALMACEN_COM2.PR_KARDEXAUXINVBOD(UN_COMPANIA => MI_COMPANIA_DES, UN_ELEMENTO => '');
          
        END IF;
    END IF;
  END IF;
  
  
  BEGIN
    BEGIN
        SELECT FECHA
               ,TO_NUMBER(TO_CHAR(FECHA,'YYYY')) ANO
               ,TO_NUMBER(TO_CHAR(FECHA,'MM')) MES
        INTO   MI_FECHA
              ,MI_ANO
              ,MI_MES
        FROM  MOVIMIENTO
        WHERE COMPANIA       = UN_COMPANIA
          AND TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
          AND NUMERO         = UN_MOVIMIENTO;

            EXCEPTION WHEN NO_DATA_FOUND THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;

    END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERR_ALMACEN_NODATA
      );
  END;

  MI_ESTADO := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADOMES(UN_COMPANIA => UN_COMPANIA
                                                   ,UN_ANO     => MI_ANO
                                                   ,UN_MES     => MI_MES
                                                   ,UN_MODULO  => 10
                                                   ,UN_PROCESO => 1);
  IF MI_ESTADO = 'A' THEN

       <<ACTUALIZAR_MOVIMIENTO>>
        FOR MI_RS IN( SELECT COMPANIA
                             ,TIPOMOVIMIENTO
                             ,MOVIMIENTO
                             ,ELEMENTO
                             ,SERIE
                             ,FECHA
                             ,HORA
                             ,TO_NUMBER(TO_CHAR(FECHA,'YYYY')) ANO
                             ,TO_NUMBER(TO_CHAR(FECHA,'MM')) AS MES
                      FROM    D_MOVIMIENTO
                      WHERE   COMPANIA       = UN_COMPANIA
                        AND   TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
                        AND   MOVIMIENTO     = UN_MOVIMIENTO)
        LOOP
        --REVISAR MOVIMIENTOS POSTERIORES
        <<REVISAR_MOV_POSTERIORES>>
                FOR MI_RSPOSTERIOR IN( SELECT TIPOMOVIMIENTO
                                              ,MOVIMIENTO
                                              ,FECHA
                                              ,TO_CHAR(HORA,'HH24:MM:SS') HORA
                                       FROM   D_MOVIMIENTO
                                        INNER JOIN TIPOMOVIMIENTO
                                           ON D_MOVIMIENTO.COMPANIA       = TIPOMOVIMIENTO.COMPANIA
                                          AND D_MOVIMIENTO.TIPOMOVIMIENTO = TIPOMOVIMIENTO.CODIGO
                                       WHERE  ELEMENTO             IN (MI_RS.ELEMENTO)
                                         AND  D_MOVIMIENTO.COMPANIA = MI_RS.COMPANIA
                                         AND  TIPOMOVIMIENTO.CLASE IN (SELECT DISTINCT CLASE
                                                                       FROM   TIPOMOVIMIENTO
                                                                       WHERE  COMPANIA = MI_RS.COMPANIA)
                                         AND SERIE                 IN (MI_RS.SERIE)
                                         AND FECHA                 >  MI_RS.FECHA
                                         AND TO_DATE(TO_CHAR(HORA,'HH24:MM:SS'),'HH24:MM:SS') >  TO_DATE(TO_CHAR(MI_RS.HORA,'HH24:MM:SS'),'HH24:MM:SS')
                                      ORDER BY FECHA)

                LOOP
                 MI_RTA:=MI_RTA|| 'Tipo Movimiento==> '||MI_RSPOSTERIOR.TIPOMOVIMIENTO|| '-- Movimiento==> '||MI_RSPOSTERIOR.MOVIMIENTO|| '-- Fecha==> '||MI_RSPOSTERIOR.FECHA||' Hora==> '||MI_RSPOSTERIOR.HORA||CHR(10);

                END LOOP REVISAR_MOV_POSTERIORES;

           END LOOP ACTUALIZAR_MOVIMIENTO;

             IF MI_RTA IS NULL THEN
               BEGIN
                 BEGIN
                  MI_CAMPOS:= 'REGISTRADO = 0';

                  MI_CONDICION:='MOVIMIENTO.COMPANIA        = '''||UN_COMPANIA ||'''
                              AND MOVIMIENTO.TIPOMOVIMIENTO = '''||UN_TIPOMOVIMIENTO||'''
                              AND MOVIMIENTO.NUMERO         = '||UN_MOVIMIENTO||'';

                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'MOVIMIENTO'
                                                        ,UN_ACCION    => 'M'
                                                        ,UN_CAMPOS    => MI_CAMPOS
                                                        ,UN_CONDICION => MI_CONDICION);

                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_ALMACEN;

                 END;

                     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                         PCK_ERR_MSG.RAISE_WITH_MSG(
                                         UN_EXC_COD    => SQLCODE
                                         ,UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_UPDATE_MOVIMIENTO
                                                   );
               END;

             ELSE
                RETURN MI_RTA;
             END IF;
  ELSE
             MI_RTA :='El periodo de Almacen se encuentra cerrado.';
             RETURN MI_RTA;
  END IF;

  RETURN 'Proceso ejecutado exitosamente';

END FC_REVERSARMOVIMIENTO;

  --35
  FUNCTION FC_VERIFICAESTADOALM
  /*
    NAME              : FC_VERIFICAESTADOALM -> PCK_PRESUPUESTO.FC_VERIFICAPERIODOPPTAL
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : PABLO ANDRES ESPITIA CUCA
    DATE MIGRADOR     : 15/08/2016
    TIME              : 04:54 PM
    SOURCE MODULE     : ALMACEN (10)
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : Devuelve -1 si el estado está activo y 0 si no existe o está cerrado.
    PARAMETERS        : UN_COMPANIA => Codigo del compania.
                        UN_ANO      => Anio.
                        UN_MES      => Codigo del mes.

    @NAME:    verificarEstadoAlmacen
    @METHOD:  GET
  */
  (
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO         IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES         IN PCK_SUBTIPOS.TI_MES DEFAULT NULL --Enviar 0, para indicar que no se utiliza.
  )
  RETURN PCK_SUBTIPOS.TI_LOGICO 
  AS
    MI_I           PCK_SUBTIPOS.TI_ENTERO;
    MI_ESTADO      VARCHAR2(2 CHAR);
    MI_REEMPLAZOS  PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN
    BEGIN
      SELECT  COUNT(*) 
      INTO    MI_I
      FROM    ANO
      WHERE   COMPANIA = UN_COMPANIA
        AND   NUMERO   = UN_ANO;

      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_REEMPLAZOS(1).CLAVE := 'ANO';
        MI_REEMPLAZOS(1).VALOR := UN_ANO;

        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD => SQLCODE
        , UN_TABLAERROR => 'ANO'
        , UN_ERROR_COD => PCK_ERRORES.ERROR_GRAL_NOEXISTEANO
        , UN_REEMPLAZOS => MI_REEMPLAZOS
        );
    END;
    --VALIDA EL AÑO
    MI_ESTADO := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADOANO(UN_COMPANIA=> UN_COMPANIA, 
                                                       UN_ANO     => UN_ANO, 
                                                       UN_MODULO  => PCK_DATOS.FC_MODULOALMACEN, 
                                                       UN_PROCESO => 1);
    IF MI_ESTADO <> 'A' THEN 
      BEGIN
        RAISE  PCK_EXCEPCIONES.EXC_GENERAL;  

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN 
        MI_REEMPLAZOS(1).CLAVE := 'ANO';
        MI_REEMPLAZOS(1).VALOR := UN_ANO;

        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD => SQLCODE
        , UN_TABLAERROR => 'ANO'
        , UN_ERROR_COD => PCK_ERRORES.ERROR_GRAL_ANOCERRADO
        , UN_REEMPLAZOS => MI_REEMPLAZOS);   
      END;
    END IF;

    IF UN_MES IS NULL THEN
      RETURN -1;
    END IF;

    --Validacion igual a la anterior, determina si se debe verificar el estado del mes
    IF UN_MES IN(0) THEN 
      RETURN -1;
    END IF;

    BEGIN
      SELECT  COUNT(*) 
      INTO    MI_I
      FROM    MES
      WHERE   COMPANIA = UN_COMPANIA
        AND   ANO      = UN_ANO
        AND   NUMERO   = UN_MES;

      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_REEMPLAZOS(1).CLAVE := 'ANO';
        MI_REEMPLAZOS(1).VALOR := UN_ANO;
        MI_REEMPLAZOS(2).CLAVE := 'MES';
        MI_REEMPLAZOS(2).VALOR := UN_MES;

        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE
        , UN_TABLAERROR => 'MES'
        , UN_ERROR_COD  => PCK_ERRORES.ERROR_GRAL_NOEXISTEMES
        , UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;

    MI_ESTADO := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADOMES(UN_COMPANIA=> UN_COMPANIA, 
                                                       UN_ANO     => UN_ANO, 
                                                       UN_MES     => UN_MES, 
                                                       UN_MODULO  => PCK_DATOS.FC_MODULOALMACEN, 
                                                       UN_PROCESO => 1);

    IF MI_ESTADO <>'A' THEN 
      BEGIN
        RAISE  PCK_EXCEPCIONES.EXC_GENERAL;  
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN 
        MI_REEMPLAZOS(1).CLAVE := 'ANO';
        MI_REEMPLAZOS(1).VALOR := UN_ANO;
        MI_REEMPLAZOS(2).CLAVE := 'MES';
        MI_REEMPLAZOS(2).VALOR := UN_MES;

        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD => SQLCODE
        , UN_TABLAERROR => 'ANO'
        , UN_ERROR_COD => PCK_ERRORES.ERROR_GRAL_MESCERRADO
        , UN_REEMPLAZOS => MI_REEMPLAZOS
        ); 
      END;
    END IF; 

    RETURN -1;
  END FC_VERIFICAESTADOALM;  

  FUNCTION FC_ACTUALIZAR_SALDOPEPS
/*
    NAME              : FC_ACTUALIZAR_SALDOPEPS -> ACTUALIZA_PEPS (SYSMANALH)
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : SANDRA MILENA DAZA LEGUIZAMON
    DATE MIGRADOR     : 17/01/2022
    TIME              : 02:30 PM
    SOURCE MODULE     : ALMACEN (10)
    MODIFIER          : GERMAN DAVID ROJAS
    DATE MODIFIED     : 
    TIME              : 09/04/2025
    MODIFICATIONS     : 
    DESCRIPTION       : Se crea validación para que solo se ejecute el proceso para los elementos que tengan diferencia en las existencias.
    

    @NAME:    actualizarSaldoPeps
    @METHOD:  GET
  */
(
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_ANOINICIAL       IN PCK_SUBTIPOS.TI_ANIO,
    UN_MESINICIAL       IN PCK_SUBTIPOS.TI_MES,
    UN_ANOFINAL         IN PCK_SUBTIPOS.TI_ANIO,
    UN_MESFINAL         IN PCK_SUBTIPOS.TI_MES,
    UN_ELEMENTOINICIAL  IN VARCHAR2,
    UN_ELEMENTOFINAL    IN VARCHAR2
)
RETURN VARCHAR
AS
    MI_ESTADOALM    PCK_SUBTIPOS.TI_LOGICO;
    MI_FECHAINICIAL DATE;
    MI_FECHAFINAL   DATE;
    MI_CAMPOS                 VARCHAR2(32000);
    MI_CONDICION              VARCHAR2(32000);
    MI_VALORES                VARCHAR2(32000);
    MI_RTA                    VARCHAR2(32000);
	MI_SALIDA_CANTIDAD        PCK_SUBTIPOS.TI_DOBLE;
    MI_ENTRADA_CANTIDAD       PCK_SUBTIPOS.TI_DOBLE;
    MI_SALDO_PEPS             PCK_SUBTIPOS.TI_DOBLE;
    MI_SALDO_PEPS_ACTUAL      PCK_SUBTIPOS.TI_DOBLE;
BEGIN
    MI_ESTADOALM := FC_VERIFICAESTADOALM
                    (
                      UN_COMPANIA  => UN_COMPANIA,
                      UN_ANO       => UN_ANOINICIAL, 
                      UN_MES       => UN_MESINICIAL
                    );
    IF MI_ESTADOALM = -1 THEN
        MI_FECHAINICIAL := TO_DATE('01/'||UN_MESINICIAL||'/' ||UN_ANOINICIAL, 'DD/MM/YYYY');
        MI_FECHAFINAL := LAST_DAY(TO_DATE('01/'||UN_MESFINAL||'/' ||UN_ANOFINAL, 'DD/MM/YYYY'));
        
        --CC_1331: Se crea validación para que solo se ejecute el proceso para los elementos que tengan diferencia en las existencias.
        FOR RS IN (SELECT DM.COMPANIA, 
                            DM.ELEMENTO,
                            SUM(CASE WHEN TM.CLASE = 'E' THEN DM.CANTIDAD ELSE 0 END) AS ENTRADA,
                            SUM(CASE WHEN TM.CLASE = 'S' THEN DM.CANTIDAD ELSE 0 END) AS SALIDA,
                            SUM(CASE WHEN TM.CLASE = 'E' THEN DM.CANTIDAD ELSE 0 END) - 
                            SUM(CASE WHEN TM.CLASE = 'S' THEN DM.CANTIDAD ELSE 0 END) AS SALDO
                        FROM D_MOVIMIENTO DM
                        INNER JOIN TIPOMOVIMIENTO TM 
                            ON DM.COMPANIA = TM.COMPANIA 
                            AND DM.TIPOMOVIMIENTO = TM.CODIGO
                        WHERE DM.COMPANIA =     UN_COMPANIA
                            AND DM.ELEMENTO     BETWEEN UN_ELEMENTOINICIAL AND UN_ELEMENTOFINAL
                            AND DM.FECHA        BETWEEN MI_FECHAINICIAL AND MI_FECHAFINAL
                            AND TM.TIPOELEMENTO = 'C'
                        GROUP BY DM.COMPANIA, DM.ELEMENTO)
                        
        LOOP
        
            BEGIN
            SELECT SUM(D_MOVIMIENTO.SALDO_PEPS)
                    INTO MI_SALDO_PEPS_ACTUAL
                FROM V_MOVIMIENTO
                INNER JOIN D_MOVIMIENTO
                    ON  V_MOVIMIENTO.COMPANIA = D_MOVIMIENTO.COMPANIA
                    AND V_MOVIMIENTO.TIPOMOVIMIENTO = D_MOVIMIENTO.TIPOMOVIMIENTO
                    AND V_MOVIMIENTO.NUMERO = D_MOVIMIENTO.MOVIMIENTO
                INNER JOIN INVENTARIO 
                    ON D_MOVIMIENTO.COMPANIA = INVENTARIO.COMPANIA
                    AND D_MOVIMIENTO.ELEMENTO = INVENTARIO.CODIGOELEMENTO  
                WHERE D_MOVIMIENTO.COMPANIA = UN_COMPANIA
                    AND INVENTARIO.CODIGOELEMENTO = RS.ELEMENTO
                AND D_MOVIMIENTO.SALDO_PEPS<>0
                AND INVENTARIO.TIENEMOVIMIENTO<>0
                AND V_MOVIMIENTO.CLASE='E'
                AND INVENTARIO.TIPO = 'C';
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_SALDO_PEPS_ACTUAL := 0;
                END;
            
            IF RS.SALDO <> NVL(MI_SALDO_PEPS_ACTUAL,0) THEN
        
            BEGIN
            BEGIN
               MI_CONDICION:='SELECT COMPANIA,CODIGO,CLASE
                              FROM   TIPOMOVIMIENTO
                              WHERE  COMPANIA     = '''||UN_COMPANIA||'''
                              AND    TIPOELEMENTO IN (''C'')';
               MI_CAMPOS:='     TABLA.COMPANIA = VISTA.COMPANIA
                            AND TABLA.TIPOMOVIMIENTO = VISTA.CODIGO';
               MI_VALORES:='UPDATE SET TABLA.SALDO_PEPS     = DECODE(VISTA.CLASE,''E'',TABLA.CANTIDAD,0),
                                       TABLA.CANTENTRADAS   = DECODE(VISTA.CLASE,''E'',TABLA.CANTIDAD,0),
                                       TABLA.CANTSALIDAS    = DECODE(VISTA.CLASE,''S'',TABLA.CANTIDAD,0)
                            WHERE TABLA.COMPANIA    = ''' || UN_COMPANIA || '''
                            AND   TABLA.ELEMENTO    BETWEEN ''' || RS.ELEMENTO || ''' AND ''' || RS.ELEMENTO || '''
                            AND   TABLA.FECHA       BETWEEN '''||MI_FECHAINICIAL ||''' AND '''||MI_FECHAFINAL ||'''';
        
               MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA        => 'D_MOVIMIENTO'
                                            ,UN_ACCION      => 'MM'
                                            ,UN_MERGEUSING  => MI_CONDICION
                                            ,UN_MERGEENLACE => MI_CAMPOS
                                            ,UN_MERGEEXISTE => MI_VALORES);


            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
            END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                                    UN_EXC_COD    => SQLCODE
                                   ,UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_MERGE_DMOVIMIENTO
            );
        END;
        
        FOR RSSALIDAS IN (
                            SELECT  DM.TIPOMOVIMIENTO, 
                                    DM.MOVIMIENTO, 
                                    DM.CODIGO,
                                    DM.ELEMENTO,
                                    DM.CANTIDAD SALIDA
                            FROM D_MOVIMIENTO DM
                                INNER JOIN TIPOMOVIMIENTO TM
                                ON DM.COMPANIA = TM.COMPANIA
                                AND DM.TIPOMOVIMIENTO = TM.CODIGO
                            WHERE DM.COMPANIA   = UN_COMPANIA
                            AND DM.ELEMENTO     BETWEEN RS.ELEMENTO AND RS.ELEMENTO
                            AND DM.FECHA        BETWEEN MI_FECHAINICIAL AND MI_FECHAFINAL
                            AND TM.TIPOELEMENTO = 'C'
                            AND TM.CLASE        = 'S'
                            ORDER BY ELEMENTO, FECHA, HORA    
        )
            LOOP
			MI_SALIDA_CANTIDAD := RSSALIDAS.SALIDA;
            FOR RSENTRADAS IN (
                                SELECT  DM.TIPOMOVIMIENTO, 
                                        DM.MOVIMIENTO, 
                                        DM.CODIGO,
                                        DM.ELEMENTO,
                                        DM.SALDO_PEPS ENTRADA
                                FROM D_MOVIMIENTO DM
                                    INNER JOIN TIPOMOVIMIENTO TM
                                    ON DM.COMPANIA = TM.COMPANIA
                                    AND DM.TIPOMOVIMIENTO = TM.CODIGO
                                WHERE DM.COMPANIA = UN_COMPANIA
                                AND DM.ELEMENTO     = RSSALIDAS.ELEMENTO
                                AND DM.FECHA        BETWEEN MI_FECHAINICIAL AND MI_FECHAFINAL
                                AND DM.SALDO_PEPS   > 0
                                AND TM.TIPOELEMENTO = 'C'
                                AND TM.CLASE        = 'E'
                                ORDER BY ELEMENTO, FECHA, HORA   
            )
            LOOP
                IF RSSALIDAS.SALIDA <= RSENTRADAS.ENTRADA THEN
                    BEGIN
                        BEGIN
						MI_ENTRADA_CANTIDAD := RSENTRADAS.ENTRADA;
						
                           MI_CONDICION:='SELECT COMPANIA,CODIGO,CLASE
                                          FROM   TIPOMOVIMIENTO
                                          WHERE  COMPANIA       = '''||UN_COMPANIA||'''
                                          AND    TIPOELEMENTO   IN (''C'')
                                          AND    CLASE          = ''E''';
                           MI_CAMPOS:='     TABLA.COMPANIA = VISTA.COMPANIA
                                        AND TABLA.TIPOMOVIMIENTO = VISTA.CODIGO';
                           MI_VALORES:='UPDATE SET TABLA.SALDO_PEPS = '||MI_ENTRADA_CANTIDAD||' - ' ||MI_SALIDA_CANTIDAD||'
                                        WHERE TABLA.COMPANIA        = ''' || UN_COMPANIA || '''
                                        AND   TABLA.TIPOMOVIMIENTO  = ''' ||RSENTRADAS.TIPOMOVIMIENTO||'''
                                        AND   TABLA.MOVIMIENTO      = '||RSENTRADAS.MOVIMIENTO||'
                                        AND   TABLA.CODIGO          = ' || RSENTRADAS.CODIGO ||'
                                        AND   TABLA.ELEMENTO        = ''' || RSENTRADAS.ELEMENTO ||  '''
                                        AND   TABLA.FECHA           BETWEEN '''||MI_FECHAINICIAL ||''' AND '''||MI_FECHAFINAL ||'''';
                    
                           MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA        => 'D_MOVIMIENTO'
                                                        ,UN_ACCION      => 'MM'
                                                        ,UN_MERGEUSING  => MI_CONDICION
                                                        ,UN_MERGEENLACE => MI_CAMPOS
                                                        ,UN_MERGEEXISTE => MI_VALORES);
            
            
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                            RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                        END;
                        
						MI_SALDO_PEPS := MI_ENTRADA_CANTIDAD - MI_SALIDA_CANTIDAD;
                        
                        IF MI_SALDO_PEPS < 0 THEN 
                        MI_SALIDA_CANTIDAD := REPLACE(MI_SALDO_PEPS,'-','');
                        ELSE
                        MI_SALIDA_CANTIDAD := 0;
                        END IF;
						
                        BEGIN
                           MI_CONDICION:='SELECT COMPANIA,CODIGO,CLASE
                                          FROM   TIPOMOVIMIENTO
                                          WHERE  COMPANIA       = '''||UN_COMPANIA||'''
                                          AND    TIPOELEMENTO   IN (''C'')
                                          AND    CLASE          = ''S''';
                           MI_CAMPOS:='     TABLA.COMPANIA = VISTA.COMPANIA
                                        AND TABLA.TIPOMOVIMIENTO = VISTA.CODIGO';
                           MI_VALORES:='UPDATE SET TABLA.CANTSALIDAS    = '||RSSALIDAS.SALIDA||'
                                        WHERE TABLA.COMPANIA            = ''' || UN_COMPANIA || '''
                                        AND   TABLA.TIPOMOVIMIENTO      = ''' ||RSSALIDAS.TIPOMOVIMIENTO||'''
                                        AND   TABLA.MOVIMIENTO          = '||RSSALIDAS.MOVIMIENTO||'
                                        AND   TABLA.CODIGO              = ' || RSSALIDAS.CODIGO ||'
                                        AND   TABLA.ELEMENTO            = ''' || RSSALIDAS.ELEMENTO|| ''' 
                                        AND   TABLA.FECHA               BETWEEN '''||MI_FECHAINICIAL ||''' AND '''||MI_FECHAFINAL ||'''';
                    
                           MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA        => 'D_MOVIMIENTO'
                                                        ,UN_ACCION      => 'MM'
                                                        ,UN_MERGEUSING  => MI_CONDICION
                                                        ,UN_MERGEENLACE => MI_CAMPOS
                                                        ,UN_MERGEEXISTE => MI_VALORES);
            
            
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                            RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                        END;
            
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                                UN_EXC_COD    => SQLCODE
                                               ,UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_MERGE_DMOVIMIENTO
                        );
                    END;
                    
                ELSE
                    MI_RTA := 'No hay existencias disponibles para el movimiento ' || RSSALIDAS.TIPOMOVIMIENTO || ' - ' || RSSALIDAS.MOVIMIENTO; 
                END IF;
            END LOOP;
            END LOOP;
            END IF;
        END LOOP;
        MI_RTA := 'Proceso finalizado';
    ELSE
        MI_RTA := 'Proceso cancelado, periodo de almacén cerrado';
    END IF;
    RETURN MI_RTA;
  END FC_ACTUALIZAR_SALDOPEPS;

FUNCTION FC_BUSCAEXISTENCIAFL
 /*
    NAME              : BuscaExistenciaFL
    AUTHORS           : SYSMAN  SAS JM CC 585
    SOURCE MODULE     : ALMACEN
    DESCRIPTION       : VERIFICA EL VALOR DE LAS EXISTENCIAS PARA UN ELEMENTO POR FUENTE Y LOTE.
  */
  (
    UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ELEMENTO                 IN VARCHAR2,
    UN_LOTE                     IN D_MOVIMIENTO.LOTE%TYPE,
    UN_FUENTE                   IN D_MOVIMIENTO.FUENTEDERECURSO%TYPE
  )
  RETURN NUMBER
  AS
    MI_EXISTENCIA  PCK_SUBTIPOS.TI_DOBLE := 0;

BEGIN

    IF UN_LOTE IS NULL OR UN_LOTE = '' THEN 
        BEGIN
         SELECT 
         NVL(V_MOVIMIENTO_VENCIMIENTO.SALDO,0) EXISTENCIA 
         INTO MI_EXISTENCIA
         FROM INVENTARIO 
         LEFT JOIN (SELECT EXISTENCIAS.COMPANIA,
         EXISTENCIAS.ELEMENTO,
         EXISTENCIAS.LOTE,
         EXISTENCIAS.FUENTEDERECURSO,
         EXISTENCIAS.FECHA_VENCIMIENTO                              FECHAVENCIMIENTO,
         SUM(EXISTENCIAS.ENTRADA)                                   ENTRADAS,
         SUM(NVL(EXISTENCIAS.SALIDA,
         0))                            SALIDAS,
         SUM(EXISTENCIAS.ENTRADA) - SUM(NVL(EXISTENCIAS.SALIDA,
         0)) SALDO FROM (SELECT ENTRADAS.COMPANIA,
         ENTRADAS.ELEMENTO,
         ENTRADAS.LOTE,
         ENTRADAS.FUENTEDERECURSO,
         ENTRADAS.ENTRADA,
         ENTRADAS.FECHA_VENCIMIENTO,
         SALIDAS.SALIDA FROM V_D_MOV_ENTRADAS ENTRADAS 
         LEFT JOIN V_D_MOV_SALIDAS  SALIDAS ON ENTRADAS.COMPANIA = SALIDAS.COMPANIA 
         AND ENTRADAS.ELEMENTO = SALIDAS.ELEMENTO 
         AND ENTRADAS.LOTE = SALIDAS.LOTE 
         AND ENTRADAS.FUENTEDERECURSO = SALIDAS.FUENTEDERECURSO ) EXISTENCIAS 
         GROUP BY EXISTENCIAS.COMPANIA,
         EXISTENCIAS.ELEMENTO,
         EXISTENCIAS.LOTE,
         EXISTENCIAS.FUENTEDERECURSO,
         EXISTENCIAS.FECHA_VENCIMIENTO ) V_MOVIMIENTO_VENCIMIENTO ON INVENTARIO.COMPANIA = V_MOVIMIENTO_VENCIMIENTO.COMPANIA 
         AND INVENTARIO.CODIGOELEMENTO = V_MOVIMIENTO_VENCIMIENTO.ELEMENTO,
         (SELECT LISTAGG(TIPO,',') WITHIN GROUP(ORDER BY TIPO ) TIPOS 
          FROM (SELECT DISTINCT INVENTARIO.TIPO FROM INVENTARIO WHERE INVENTARIO.COMPANIA = UN_COMPANIA 
         AND INVENTARIO.TIPO NOT IN ( 'C' ) ) ) TIPOSINV 
         WHERE INVENTARIO.COMPANIA = UN_COMPANIA 
         AND INSTR( 'C' , TIPO,1) > 0 
         AND INVENTARIO.TIENEMOVIMIENTO NOT IN ( 0 ) 
         AND INVENTARIO.INACTIVO IN ( 0 ) 
         AND  INVENTARIO.CODIGOELEMENTO = UN_ELEMENTO
         AND V_MOVIMIENTO_VENCIMIENTO.LOTE = '-1.-'
         AND V_MOVIMIENTO_VENCIMIENTO.FUENTEDERECURSO = UN_FUENTE
         ORDER BY INVENTARIO.CODIGOELEMENTO,
         V_MOVIMIENTO_VENCIMIENTO.LOTE;
         RETURN MI_EXISTENCIA;
            EXCEPTION WHEN NO_DATA_FOUND THEN
            RETURN 0;
         END;
    ELSE 
        BEGIN
         SELECT 
         NVL(V_MOVIMIENTO_VENCIMIENTO.SALDO,0) EXISTENCIA 
         INTO MI_EXISTENCIA
         FROM INVENTARIO 
         LEFT JOIN (SELECT EXISTENCIAS.COMPANIA,
         EXISTENCIAS.ELEMENTO,
         EXISTENCIAS.LOTE,
         EXISTENCIAS.FUENTEDERECURSO,
         EXISTENCIAS.FECHA_VENCIMIENTO                              FECHAVENCIMIENTO,
         SUM(EXISTENCIAS.ENTRADA)                                   ENTRADAS,
         SUM(NVL(EXISTENCIAS.SALIDA,
         0))                            SALIDAS,
         SUM(EXISTENCIAS.ENTRADA) - SUM(NVL(EXISTENCIAS.SALIDA,
         0)) SALDO FROM (SELECT ENTRADAS.COMPANIA,
         ENTRADAS.ELEMENTO,
         ENTRADAS.LOTE,
         ENTRADAS.FUENTEDERECURSO,
         ENTRADAS.ENTRADA,
         ENTRADAS.FECHA_VENCIMIENTO,
         SALIDAS.SALIDA FROM V_D_MOV_ENTRADAS ENTRADAS 
         LEFT JOIN V_D_MOV_SALIDAS  SALIDAS ON ENTRADAS.COMPANIA = SALIDAS.COMPANIA 
         AND ENTRADAS.ELEMENTO = SALIDAS.ELEMENTO 
         AND ENTRADAS.LOTE = SALIDAS.LOTE 
         AND ENTRADAS.FUENTEDERECURSO = SALIDAS.FUENTEDERECURSO ) EXISTENCIAS 
         GROUP BY EXISTENCIAS.COMPANIA,
         EXISTENCIAS.ELEMENTO,
         EXISTENCIAS.LOTE,
         EXISTENCIAS.FUENTEDERECURSO,
         EXISTENCIAS.FECHA_VENCIMIENTO ) V_MOVIMIENTO_VENCIMIENTO ON INVENTARIO.COMPANIA = V_MOVIMIENTO_VENCIMIENTO.COMPANIA 
         AND INVENTARIO.CODIGOELEMENTO = V_MOVIMIENTO_VENCIMIENTO.ELEMENTO,
         (SELECT LISTAGG(TIPO,',') WITHIN GROUP(ORDER BY TIPO ) TIPOS 
          FROM (SELECT DISTINCT INVENTARIO.TIPO FROM INVENTARIO WHERE INVENTARIO.COMPANIA = UN_COMPANIA 
         AND INVENTARIO.TIPO NOT IN ( 'C' ) ) ) TIPOSINV WHERE INVENTARIO.COMPANIA = UN_COMPANIA 
         AND INSTR( 'C' , TIPO,1) > 0 
         AND INVENTARIO.TIENEMOVIMIENTO NOT IN ( 0 ) 
         AND INVENTARIO.INACTIVO IN ( 0 ) 
         AND  INVENTARIO.CODIGOELEMENTO = UN_ELEMENTO
         AND V_MOVIMIENTO_VENCIMIENTO.LOTE = UN_LOTE
         AND V_MOVIMIENTO_VENCIMIENTO.FUENTEDERECURSO = UN_FUENTE
         ORDER BY INVENTARIO.CODIGOELEMENTO,
         V_MOVIMIENTO_VENCIMIENTO.LOTE; 
         RETURN MI_EXISTENCIA;
            EXCEPTION WHEN NO_DATA_FOUND THEN
          RETURN 0;
         END;
      END IF;

  END FC_BUSCAEXISTENCIAFL;

END PCK_ALMACEN;
