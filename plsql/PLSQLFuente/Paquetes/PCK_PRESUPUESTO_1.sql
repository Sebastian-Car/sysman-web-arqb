create or replace PACKAGE BODY "PCK_PRESUPUESTO" AS
/**@package:  Presupuesto **/
  --1
  FUNCTION FC_PROYECTARPRESUPUESTO
  /*
    NAME              : FC_PROYECTARPRESUPUESTO En Access --> ProyectarPresupuesto
    AUTHORS           : SYSMAN  SAS 
    AUTHOR MIGRACION  : NICOLÁS GÓMEZ BARBOSA
    DATE MIGRADOR     : 27/06/2016
    TIME              : 15:30 PM
    SOURCE MODULE     : PRESUPUESTO
    MODIFIER          : YEISSON ALEJANDRO ROJAS RUIZ
    DATE MODIFIED     : 11/01/2017
    TIME              : 10:00 AM
    MODIFICATIONS     : CORRECCIÓN SEGÚN ESTÁNDAR DE PROGRAMACIÓN.
    DESCRIPTION       : Proyecta presupuesto.
    PARAMETERS        : UN_COMPANIA     => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                        UN_ANOFUENTE    => AÑO FUENTE DE LA OPERACIÓN.
                        UN_ANODESTINO   => AÑO DESTINO DE LA OPERACIÓN.
                        UN_INC          => NÚMERO QUE SIRVE PARA EL MANEJO DE PORCENTAJE.
                        UN_SA           => CONDICIÓN BOOLEANA.
                        UN_REGALIAS     => CONDICIÓN BOOLEANA.
                        UN_SOLOREGALIAS => CONDICIÓN BOOLEANA.
    @NAME:    proyectarPresupuestoSiguienteVigencia
    @METHOD:  GET
  */
  (
    UN_COMPANIA               IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANOFUENTE              IN PCK_SUBTIPOS.TI_ANIO,
    UN_ANODESTINO             IN PCK_SUBTIPOS.TI_ANIO,
    UN_INC                    IN PCK_SUBTIPOS.TI_DOBLE,
    UN_SA                     IN PCK_SUBTIPOS.TI_LOGICO,
    UN_REGALIAS               IN PCK_SUBTIPOS.TI_LOGICO,
    UN_SOLOREGALIAS           IN PCK_SUBTIPOS.TI_LOGICO
  )
  RETURN NUMBER AS
    MI_RTA                    PCK_SUBTIPOS.TI_LOGICO;
    MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
    MI_I                      PCK_SUBTIPOS.TI_ENTERO;
    MI_APRDEBITO              PCK_SUBTIPOS.TI_DOBLE;
    MI_APRCREDITO             PCK_SUBTIPOS.TI_DOBLE;
    MI_NOMBRER                PLAN_PRESUPUESTAL.NOMBRE%TYPE;
    MI_RS                     SYS_REFCURSOR;
    MI_RS2                    SYS_REFCURSOR;  
    MI_SALDO                  PCK_SUBTIPOS.TI_ENTERO;
    MI_PLAN                   PCK_SUBTIPOS.TI_ENTERO;
    MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN  
   MI_RTA := -1;
    <<PLANPRESUPUESTAL>>
    FOR MI_RS IN (
      SELECT  SALDO_PLAN_PPTAL.COMPANIA, 
              SALDO_PLAN_PPTAL.ANO,
              PLAN_PRESUPUESTAL.CODIGO,
              PLAN_PRESUPUESTAL.NOMBRE, 
              SUM(CASE WHEN NATURALEZA='D' 
                       THEN APROPIACION_DEBITO-APROPIACION_CREDITO 
                       ELSE APROPIACION_CREDITO-APROPIACION_DEBITO 
                  END) APROPIADO,
              SUM(SALDO_PLAN_PPTAL.ADICION) ADI,
              SUM(SALDO_PLAN_PPTAL.REDUCCION) RED, 
              SUM(CASE WHEN NATURALEZA='D' 
                       THEN SALDO_PLAN_PPTAL.TRASLADO_DEBITO-SALDO_PLAN_PPTAL.TRASLADO_CREDITO 
                       ELSE SALDO_PLAN_PPTAL.TRASLADO_CREDITO-SALDO_PLAN_PPTAL.TRASLADO_DEBITO 
                  END) TRASLADO, 
              SUM(CASE WHEN NATURALEZA='D' 
                       THEN SALDO_PLAN_PPTAL.APLAZAM_DEBITO-SALDO_PLAN_PPTAL.APLAZAM_CREDITO 
                       ELSE SALDO_PLAN_PPTAL.APLAZAM_CREDITO-SALDO_PLAN_PPTAL.APLAZAM_DEBITO 
                  END) APLAZAM, 
              ((SUM(CASE WHEN NATURALEZA='D' 
                         THEN APROPIACION_DEBITO-APROPIACION_CREDITO 
                         ELSE APROPIACION_CREDITO-APROPIACION_DEBITO 
                    END))+
              (SUM(SALDO_PLAN_PPTAL.ADICION))+
              (SUM(SALDO_PLAN_PPTAL.REDUCCION))+
              (SUM(CASE WHEN NATURALEZA='D' 
                        THEN SALDO_PLAN_PPTAL.TRASLADO_DEBITO-SALDO_PLAN_PPTAL.TRASLADO_CREDITO 
                        ELSE SALDO_PLAN_PPTAL.TRASLADO_CREDITO-SALDO_PLAN_PPTAL.TRASLADO_DEBITO 
                   END))+
              (SUM(CASE WHEN NATURALEZA='D' 
                        THEN SALDO_PLAN_PPTAL.APLAZAM_DEBITO-SALDO_PLAN_PPTAL.APLAZAM_CREDITO 
                        ELSE SALDO_PLAN_PPTAL.APLAZAM_CREDITO-SALDO_PLAN_PPTAL.APLAZAM_DEBITO 
                  END))) APRDEFINITIVA,
              (((SUM(CASE WHEN NATURALEZA='D' 
                          THEN APROPIACION_DEBITO-APROPIACION_CREDITO 
                          ELSE APROPIACION_CREDITO-APROPIACION_DEBITO 
                     END))+
              (SUM(SALDO_PLAN_PPTAL.ADICION))+
              (SUM(SALDO_PLAN_PPTAL.REDUCCION))+
              (SUM(CASE WHEN NATURALEZA='D' 
                        THEN SALDO_PLAN_PPTAL.TRASLADO_DEBITO-SALDO_PLAN_PPTAL.TRASLADO_CREDITO 
                        ELSE SALDO_PLAN_PPTAL.TRASLADO_CREDITO-SALDO_PLAN_PPTAL.TRASLADO_DEBITO 
                  END))+
              (SUM(CASE WHEN NATURALEZA='D' 
                        THEN SALDO_PLAN_PPTAL.APLAZAM_DEBITO-SALDO_PLAN_PPTAL.APLAZAM_CREDITO 
                        ELSE SALDO_PLAN_PPTAL.APLAZAM_CREDITO-SALDO_PLAN_PPTAL.APLAZAM_DEBITO 
                   END))))+
              ((((SUM(CASE WHEN NATURALEZA='D' 
                           THEN APROPIACION_DEBITO-APROPIACION_CREDITO 
                           ELSE APROPIACION_CREDITO-APROPIACION_DEBITO 
                      END))+
              (SUM(SALDO_PLAN_PPTAL.ADICION))+
              (SUM(SALDO_PLAN_PPTAL.REDUCCION))+
              (SUM(CASE WHEN NATURALEZA='D' 
                        THEN SALDO_PLAN_PPTAL.TRASLADO_DEBITO-SALDO_PLAN_PPTAL.TRASLADO_CREDITO 
                        ELSE SALDO_PLAN_PPTAL.TRASLADO_CREDITO-SALDO_PLAN_PPTAL.TRASLADO_DEBITO
                   END))+
              (SUM(CASE WHEN NATURALEZA='D' 
                        THEN SALDO_PLAN_PPTAL.APLAZAM_DEBITO-SALDO_PLAN_PPTAL.APLAZAM_CREDITO 
                        ELSE SALDO_PLAN_PPTAL.APLAZAM_CREDITO-SALDO_PLAN_PPTAL.APLAZAM_DEBITO 
                   END))))*(UN_INC/100)) INCXX, 
              PLAN_PRESUPUESTAL.CODIGO_EQUIV,
              PLAN_PRESUPUESTAL.NATURALEZA,
              PLAN_PRESUPUESTAL.MOVIMIENTO,
              PLAN_PRESUPUESTAL.MAN_CEN_CTO,
              PLAN_PRESUPUESTAL.MAN_AUX_TER,
              PLAN_PRESUPUESTAL.MAN_AUX_GEN, 
              PLAN_PRESUPUESTAL.MAN_AUX_REF,
              PLAN_PRESUPUESTAL.MAN_AUX_FUE,
              PLAN_PRESUPUESTAL.MAN_PAC, 
              PLAN_PRESUPUESTAL.VIGENCIA,
              PLAN_PRESUPUESTAL.INDECONOMICO,
              PLAN_PRESUPUESTAL.DESTINO,
              PLAN_PRESUPUESTAL.APROPIACIONINICIAL, 
              PLAN_PRESUPUESTAL.RESERVADECAJA,
              PLAN_PRESUPUESTAL.RESERVADEAPROPIACION, 
              PLAN_PRESUPUESTAL.NIVEL1,
              PLAN_PRESUPUESTAL.NIVEL2,
              PLAN_PRESUPUESTAL.NIVEL3,
              PLAN_PRESUPUESTAL.NIVEL4, 
              PLAN_PRESUPUESTAL.NIVEL5,
              PLAN_PRESUPUESTAL.NIVEL6,
              PLAN_PRESUPUESTAL.NIVEL7,
              PLAN_PRESUPUESTAL.TIPOVIGENCIA,
              PLAN_PRESUPUESTAL.CONSITUACIONFONDOS,
              PLAN_PRESUPUESTAL.REGALIAS 
      FROM    PLAN_PRESUPUESTAL 
        LEFT JOIN SALDO_PLAN_PPTAL 
          ON  PLAN_PRESUPUESTAL.COMPANIA = SALDO_PLAN_PPTAL.COMPANIA
         AND  PLAN_PRESUPUESTAL.ANO      = SALDO_PLAN_PPTAL.ANO
         AND  PLAN_PRESUPUESTAL.CODIGO   = SALDO_PLAN_PPTAL.CODIGO
      WHERE   SALDO_PLAN_PPTAL.COMPANIA = UN_COMPANIA
        AND   SALDO_PLAN_PPTAL.ANO      = UN_ANOFUENTE
        AND   SALDO_PLAN_PPTAL.MES      <= 12 
        AND   ((UN_REGALIAS NOT IN (0) 
                AND PLAN_PRESUPUESTAL.REGALIAS NOT IN (0)) 
                OR (UN_REGALIAS IN (0) 
                AND ((PLAN_PRESUPUESTAL.REGALIAS IN (0)) 
                OR (PLAN_PRESUPUESTAL.REGALIAS NOT IN (0)))))
      GROUP BY SALDO_PLAN_PPTAL.COMPANIA,
              SALDO_PLAN_PPTAL.ANO,
              PLAN_PRESUPUESTAL.CODIGO, 
              PLAN_PRESUPUESTAL.NOMBRE, 
              PLAN_PRESUPUESTAL.CODIGO_EQUIV, 
              PLAN_PRESUPUESTAL.NATURALEZA, 
              PLAN_PRESUPUESTAL.MOVIMIENTO, 
              PLAN_PRESUPUESTAL.MAN_CEN_CTO, 
              PLAN_PRESUPUESTAL.MAN_AUX_TER, 
              PLAN_PRESUPUESTAL.MAN_AUX_GEN,
              PLAN_PRESUPUESTAL.MAN_AUX_REF,
              PLAN_PRESUPUESTAL.MAN_AUX_FUE,
              PLAN_PRESUPUESTAL.MAN_PAC,  
              PLAN_PRESUPUESTAL.VIGENCIA, 
              PLAN_PRESUPUESTAL.INDECONOMICO, 
              PLAN_PRESUPUESTAL.DESTINO, 
              PLAN_PRESUPUESTAL.APROPIACIONINICIAL, 
              PLAN_PRESUPUESTAL.RESERVADECAJA, 
              PLAN_PRESUPUESTAL.RESERVADEAPROPIACION, 
              PLAN_PRESUPUESTAL.NIVEL1, 
              PLAN_PRESUPUESTAL.NIVEL2, 
              PLAN_PRESUPUESTAL.NIVEL3, 
              PLAN_PRESUPUESTAL.NIVEL4, 
              PLAN_PRESUPUESTAL.NIVEL5, 
              PLAN_PRESUPUESTAL.NIVEL6, 
              PLAN_PRESUPUESTAL.NIVEL7,
              PLAN_PRESUPUESTAL.TIPOVIGENCIA, 
              PLAN_PRESUPUESTAL.CONSITUACIONFONDOS,
              PLAN_PRESUPUESTAL.REGALIAS
    )
    LOOP
      --COORECCION DE CARECTERES ESPECIALES
      MI_NOMBRER := '';
      MI_NOMBRER := UPPER(REPLACE (MI_RS.NOMBRE,'*',' '));
      MI_NOMBRER := UPPER(REPLACE (MI_NOMBRER,'$',' '));
      MI_NOMBRER := UPPER(REPLACE (MI_NOMBRER,'%',' '));
      MI_NOMBRER := UPPER(REPLACE (MI_NOMBRER,'&',' '));
      MI_NOMBRER := UPPER(REPLACE (MI_NOMBRER,'+',' '));
      MI_NOMBRER := UPPER(REPLACE (MI_NOMBRER,'-',' '));
      MI_NOMBRER := UPPER(REPLACE (MI_NOMBRER,'¡',' '));
      MI_NOMBRER := UPPER(REPLACE (MI_NOMBRER,'?',' '));
      MI_NOMBRER := UPPER(REPLACE (MI_NOMBRER,'¿',' '));
      MI_NOMBRER := UPPER(REPLACE (MI_NOMBRER,'!',' '));
      MI_NOMBRER := UPPER(REPLACE (MI_NOMBRER,'<',' '));
      MI_NOMBRER := UPPER(REPLACE (MI_NOMBRER,'>',' '));
      MI_NOMBRER := UPPER(REPLACE (MI_NOMBRER,',',' '));
      MI_NOMBRER := UPPER(REPLACE (MI_NOMBRER,'.',' '));
      MI_NOMBRER := TRIM(MI_NOMBRER);

      MI_CAMPOS := 'COMPANIA,
                    ANO,
                    CODIGO,
                    NOMBRE,
                    CODIGO_EQUIV,
                    NATURALEZA,
                    MOVIMIENTO,
                    MAN_CEN_CTO,
                    MAN_AUX_TER,
                    MAN_AUX_GEN,
                    MAN_AUX_REF,
                    MAN_AUX_FUE,
                    MAN_PAC,
                    VIGENCIA,
                    INDECONOMICO,
                    DESTINO,
                    APROPIACIONINICIAL,
                    RESERVADECAJA,
                    RESERVADEAPROPIACION,
                    NIVEL1, 
                    NIVEL2, 
                    NIVEL3, 
                    NIVEL4, 
                    NIVEL5, 
                    NIVEL6, 
                    NIVEL7,
                    TIPOVIGENCIA, 
                    CONSITUACIONFONDOS,
                    REGALIAS';
      MI_VALORES := ''''|| UN_COMPANIA || ''',
                    '   || UN_ANODESTINO || ',
                    ''' || MI_RS.CODIGO || ''',
                    ''' || MI_NOMBRER || ''',
                    ''' || NVL(MI_RS.CODIGO_EQUIV,'') || ''',
                    ''' || MI_RS.NATURALEZA || ''',
                    '   || MI_RS.MOVIMIENTO || ',
                    '   || MI_RS.MAN_CEN_CTO || ',
                    '   || MI_RS.MAN_AUX_TER || ',
                    '   || MI_RS.MAN_AUX_GEN || ',
                    '   || MI_RS.MAN_AUX_REF || ',
                    '   || MI_RS.MAN_AUX_FUE || ',
                    '   || MI_RS.MAN_PAC || ',
                    '   || MI_RS.VIGENCIA || ',
                    '   || MI_RS.INDECONOMICO || ',
                    ''' || MI_RS.DESTINO || ''',
                    '   || NVL(MI_RS.APROPIADO,0) || ',
                    '   || NVL(MI_RS.RESERVADECAJA,0) || ',
                    '   || NVL(MI_RS.RESERVADEAPROPIACION,0) || ',
                    ''' || NVL(MI_RS.NIVEL1,'') || ''',
                    ''' || NVL(MI_RS.NIVEL2,'') || ''',
                    ''' || NVL(MI_RS.NIVEL3,'') || ''',
                    ''' || NVL(MI_RS.NIVEL4,'') || ''',
                    ''' || NVL(MI_RS.NIVEL5,'') || ''',
                    ''' || NVL(MI_RS.NIVEL6,'') || ''',
                    ''' || NVL(MI_RS.NIVEL7,'') || ''',
                    ''' || NVL(MI_RS.TIPOVIGENCIA,'') || ''',
                    '   || NVL(MI_RS.CONSITUACIONFONDOS,0) || ',
                    '   || NVL(MI_RS.REGALIAS,0) || '';   

      --VERIFICA LA EXISTENCIA DEL PLAN_PRESUPUESTAL          
      BEGIN
        MI_PLAN := 1;

        SELECT  COUNT(1) PLANES
        INTO    MI_PLAN
        FROM    PLAN_PRESUPUESTAL
        WHERE   COMPANIA = UN_COMPANIA
          AND   ANO      = UN_ANODESTINO
          AND   CODIGO   = MI_RS.CODIGO;

        EXCEPTION WHEN NO_DATA_FOUND THEN 
          MI_PLAN :=0;
      END;

      --EXCEPCION DE INSERCION EN PLAN_PRESUPUESTAL

        IF MI_PLAN = 0 THEN
          BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'PLAN_PRESUPUESTAL', 
                                                     UN_ACCION  => 'I', 
                                                     UN_CAMPOS  => MI_CAMPOS, 
                                                     UN_VALORES => MI_VALORES);


               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
          END;
        END IF;


      <<APROPIACIONES>>
      FOR MI_I IN 0..13 LOOP        

        --VERIFICA LA EXISTENCIA DE LOS SALDOS EN SALDO_PLAN_PPTAL
        BEGIN
          SELECT COUNT(1) SALDOS 
          INTO MI_SALDO
          FROM SALDO_PLAN_PPTAL
          WHERE COMPANIA = UN_COMPANIA
            AND ANO      = UN_ANODESTINO
            AND CODIGO   = MI_RS.CODIGO
            AND MES      = MI_I;

          EXCEPTION WHEN NO_DATA_FOUND 
                    THEN MI_SALDO:=0;
        END;

        IF MI_SALDO = 0 THEN   

          --EXCEPCIONES DE INSERTAR EN SALDO_PLAN_PPTAL
          BEGIN 
            IF MI_I=0 THEN          
              IF UN_SA NOT IN (0) THEN
                MI_APRDEBITO  := 0;  
                MI_APRCREDITO := 0;  
              ELSE
                IF MI_RS.NATURALEZA = 'D' THEN
                  MI_APRDEBITO := NVL(MI_RS.APROPIADO,0);  
                  MI_APRCREDITO := 0;  
                ELSE
                  MI_APRDEBITO := 0;  
                  MI_APRCREDITO := NVL(MI_RS.APROPIADO,0);  
                END IF;          
              END IF;

              MI_CAMPOS        := 'COMPANIA,
                                   ANO,
                                   CODIGO,
                                   MES,
                                   APROPIACION_DEBITO,
                                   APROPIACION_CREDITO';
              MI_VALORES       := ''''|| UN_COMPANIA ||''',
                                  '   || UN_ANODESTINO ||',
                                  ''' || MI_RS.CODIGO ||''',
                                  '   || MI_I||',
                                  '   || CASE WHEN UN_SA NOT IN (0) AND UN_REGALIAS NOT IN (0) 
                                              THEN CASE WHEN MI_RS.REGALIAS NOT IN (0) 
                                                        THEN CASE WHEN MI_RS.NATURALEZA = 'D' 
                                                                  THEN MI_APRDEBITO 
                                                                  ELSE 0 
                                                              END 
                                                        ELSE 0 
                                                   END 
                                              ELSE CASE WHEN UN_SA NOT IN (0) AND UN_REGALIAS IN (0) 
                                                        THEN 0 
                                                        ELSE CASE WHEN UN_SA IN (0) AND UN_REGALIAS IN (0) 
                                                                  THEN CASE WHEN MI_RS.NATURALEZA = 'D' 
                                                                            THEN MI_APRDEBITO 
                                                                            ELSE 0 
                                                                       END 
                                                                  ELSE 0 
                                                             END 
                                                   END 
                                         END||',
                                  '   || CASE WHEN UN_SA NOT IN (0) AND UN_REGALIAS NOT IN (0) 
                                              THEN CASE WHEN MI_RS.REGALIAS NOT IN (0) 
                                                        THEN CASE WHEN MI_RS.NATURALEZA = 'C' 
                                                                  THEN MI_APRCREDITO 
                                                                  ELSE 0 
                                                             END 
                                                        ELSE 0 
                                                   END 
                                              ELSE CASE WHEN UN_SA NOT IN (0) AND UN_REGALIAS IN (0) 
                                                        THEN 0 
                                                        ELSE CASE WHEN UN_SA IN (0) AND UN_REGALIAS IN (0) 
                                                                  THEN CASE WHEN MI_RS.NATURALEZA = 'C' 
                                                                            THEN MI_APRCREDITO 
                                                                            ELSE 0 
                                                                       END 
                                                                  ELSE 0 
                                                             END 
                                                   END 
                                         END||'';

              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'SALDO_PLAN_PPTAL', 
                                                     UN_ACCION  => 'I', 
                                                     UN_CAMPOS  => MI_CAMPOS, 
                                                     UN_VALORES => MI_VALORES);
            ELSE
              MI_CAMPOS        := 'COMPANIA,
                                   ANO,
                                   CODIGO,
                                   MES';
              MI_VALORES       := ''''|| UN_COMPANIA ||''',
                                  '   || UN_ANODESTINO ||',
                                  ''' || MI_RS.CODIGO || ''',
                                  '   || MI_I || '';
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'SALDO_PLAN_PPTAL', 
                                                     UN_ACCION  => 'I', 
                                                     UN_CAMPOS  => MI_CAMPOS, 
                                                     UN_VALORES => MI_VALORES);
            END IF;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
             MI_RTA := 0;
              RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;

          END;
        END IF; 
      END LOOP APROPIACIONES;       
    END LOOP PLANPRESUPUESTAL;

    MI_CAMPOS  := 'COMPANIA, 
                   ANO, 
                   CODIGO, 
                   APROPIACIONINICIAL, 
                   DEBITO, 
                   CREDITO, 
                   AUXILIAR, 
                   CENTRO_COSTO, 
                   FUENTE_RECURSO, 
                   REFERENCIA, 
                   TERCERO, 
                   SUCURSAL';
    MI_VALORES := 'SELECT DISTINCT PLAN.COMPANIA,
                          PLAN.ANO,
                          PLAN.CODIGO, 
                          PLAN.APROPIACIONINICIAL,
                          CASE WHEN PLAN.NATURALEZA=''D'' THEN PLAN.APROPIACIONINICIAL ELSE 0 END,
                          CASE WHEN PLAN.NATURALEZA=''C'' THEN PLAN.APROPIACIONINICIAL ELSE 0 END,
                          SALDO.AUXILIAR,
                          SALDO.CENTRO_COSTO,
                          SALDO.FUENTE_RECURSO,
                          SALDO.REFERENCIA,
                          SALDO.TERCERO,
                          SALDO.SUCURSAL
                   FROM   PLAN_PRESUPUESTAL PLAN
                    INNER JOIN SALDO_AUX_PPTAL SALDO
                      ON  PLAN.COMPANIA = SALDO.COMPANIA
                      AND PLAN.ANO      = SALDO.ANO
                      AND PLAN.CODIGO   = SALDO.CODIGO
                  WHERE   PLAN.COMPANIA = '''|| UN_COMPANIA ||''' 
                    AND   PLAN.ANO      = '  || UN_ANODESTINO ||'
                    AND   PLAN.REGALIAS NOT IN (0)';

    --EXCEPCION DE INSERTAR EN APROCIACIONESINICIALES
    BEGIN
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'APROPIACIONESINICIALES', 
                                             UN_ACCION  => 'IS', 
                                             UN_CAMPOS  => MI_CAMPOS, 
                                             UN_VALORES => MI_VALORES);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
        MI_RTA := 0;
        RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
    END;

    --EXCEPCION DE ACTUALIZAR EN PLAN_PRESUPUESTAL Y APROPIACIONESINICIALES
    BEGIN

      IF UN_SOLOREGALIAS IN (0) THEN 
        <<APROPIACIONANO>>
        FOR MI_RS2 IN (
          SELECT  COMPANIA, 
                  ANO, 
                  CODIGO, 
                  APROPIACIONINICIAL, 
                  DEBITO, 
                  CREDITO
          FROM    APROPIACIONESINICIALES
          WHERE   COMPANIA = UN_COMPANIA 
            AND   ANO      = UN_ANODESTINO)
        LOOP
          MI_CAMPOS        := 'APROPIACIONINICIAL = '||NVL(MI_RS2.APROPIACIONINICIAL,0);
          MI_CONDICION     := '    COMPANIA = '''|| UN_COMPANIA ||''' 
                               AND ANO      = '  || UN_ANODESTINO ||' 
                               AND CODIGO   = '''|| MI_RS2.CODIGO ||'''';
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'PLAN_PRESUPUESTAL', 
                                                 UN_ACCION    => 'M', 
                                                 UN_CAMPOS    => MI_CAMPOS, 
                                                 UN_CONDICION => MI_CONDICION);

          MI_CAMPOS        := 'CONTABILIZADO=-1';
          MI_CONDICION     := '    COMPANIA = '''|| UN_COMPANIA ||''' 
                               AND ANO      = '  || UN_ANODESTINO ||' 
                               AND CODIGO   = '''|| MI_RS2.CODIGO ||'''';
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'APROPIACIONESINICIALES', 
                                                 UN_ACCION    => 'M', 
                                                 UN_CAMPOS    => MI_CAMPOS, 
                                                 UN_CONDICION => MI_CONDICION);
        END LOOP APROPIACIONANO;
      END IF;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
    END;

  RETURN MI_RTA;
    --Excepcion general de la funcion
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
      MI_MSGERROR(1).CLAVE := 'ANIOFUENTE';
      MI_MSGERROR(1).VALOR := UN_ANOFUENTE;
      MI_MSGERROR(2).CLAVE := 'ANIODESTINO';
      MI_MSGERROR(2).VALOR := UN_ANODESTINO;
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD    => SQLCODE,
        UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_PROYECTARPPTO,
        UN_REEMPLAZOS => MI_MSGERROR);   
    MI_RTA:=-1;
    RETURN MI_RTA;


  END FC_PROYECTARPRESUPUESTO;

  --2
  FUNCTION FC_VERIFICAPERIODOPPTAL
    /*
      NAME              : FC_VERIFICAPERIODOPPTAL En Access --> VerificaPeriodoPptal
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : NICOLÁS GÓMEZ BARBOSA
      DATE MIGRADOR     : 28/06/2016
      TIME              : 10:30 AM
      SOURCE MODULE     : PRESUPUESTO
      MODIFIER          : YEISSON ALEJANDRO ROJAS RUIZ
      DATE MODIFIED     : 11/01/2017
      TIME              : 10:00 AM
      MODIFICATIONS     : CORRECCIÓN SEGÚN ESTÁNDAR DE PROGRAMACIÓN.
      DESCRIPTION       : Devuelve -1 si un determinado periodo pptal está activo y 0 si no existe o está cerrado.
      PARAMETERS        : UN_COMPANIA => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                          UN_ANO      => AÑO POR EL QUE SE VA A FILTRAR EN LAS CONSULTAS.
                          UN_MES      => MES POR EL QUE SE VA A FILTRAR EN LAS CONSULTAS.

      @NAME:    verificarPeriodoPresupuestal
      @METHOD:  GET
    */
  (
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO         IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES         IN PCK_SUBTIPOS.TI_MES DEFAULT NULL
  )
  RETURN NUMBER AS
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
    --VALIDA EL AÑO DE CONTABILIDAD POR QUE NO SE
    MI_ESTADO := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADOANO(UN_COMPANIA=> UN_COMPANIA, 
                                                       UN_ANO     => UN_ANO, 
                                                       UN_MODULO  => 3, 
                                                       UN_PROCESO => 1);
    IF MI_ESTADO <>'A' THEN 
      BEGIN
        RAISE  PCK_EXCEPCIONES.EXC_GENERAL;  
        EXCEPTION
           WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN 
                MI_REEMPLAZOS(1).CLAVE := 'ANO';
                MI_REEMPLAZOS(1).VALOR := UN_ANO;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD => SQLCODE
                , UN_TABLAERROR => 'ANO'
                , UN_ERROR_COD => PCK_ERRORES.ERROR_GRAL_ANOCERRADO
                , UN_REEMPLAZOS => MI_REEMPLAZOS
                );   
      END;
    END IF;

    IF UN_MES IS NULL THEN
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
            UN_EXC_COD => SQLCODE
          , UN_TABLAERROR => 'MES'
          , UN_ERROR_COD => PCK_ERRORES.ERROR_GRAL_NOEXISTEMES
          , UN_REEMPLAZOS => MI_REEMPLAZOS
          );
    END;

    MI_ESTADO := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADOMES(UN_COMPANIA=> UN_COMPANIA, 
                                                       UN_ANO     => UN_ANO, 
                                                       UN_MES     => UN_MES, 
                                                       UN_MODULO  => 3, 
                                                       UN_PROCESO => 1);

    IF MI_ESTADO <>'A' THEN 
      BEGIN
        RAISE  PCK_EXCEPCIONES.EXC_GENERAL;  
        EXCEPTION
           WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN 
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
  END FC_VERIFICAPERIODOPPTAL;

  --3
  PROCEDURE PR_MAYORIZARCUENTASHPTO
    /*
      NAME              : PR_MAYORIZARCUENTASHPTO En Access --> MayorizarCuentasHPto
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : NICOLÁS GÓMEZ BARBOSA \ JAVIER RODRIGUEZ
      DATE MIGRADOR     : 28/06/2016 \ 06/12/2016
      TIME              : 11:30 AM
      SOURCE MODULE     : PRESUPUESTO
      MODIFIER          : YEISSON ALEJANDRO ROJAS RUIZ
      DATE MODIFIED     : 11/01/2017
      TIME              : 10:00 AM
      MODIFICATIONS     : SE AJUSTO EL PROCESO PARA QUE SE REALICE MEDIANTE EL USO DE LA SENTENCIA MERGE\ CORRECCIÓN SEGÚN ESTÁNDAR DE PROGRAMACIÓN.
      DESCRIPTION       : Proceso encargado de mayorizar los saldos del Plan Presupuestal en un periodo enviado \ Se ajusta para que mayorice
                          los saldos auxiliares.
      PARAMETERS        : UN_COMPANIA   => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                          UN_ANO        => AÑO POR EL QUE SE VA A FILTRAR EN LAS CONSULTAS.
                          UN_MESINICIAL => MES INICIAL POR EL QUE SE VA A FILTRAR EN LAS CONSULTAS.
                          UN_MESFINAL   => MES FINAL POR EL QUE SE VA A FILTRAR EN LAS CONSULTAS.

      @NAME:    mayorizarRubrosPresupuestales
      @METHOD:  POST
    */
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO          IN PCK_SUBTIPOS.TI_ANIO,
    UN_MESINICIAL   IN PCK_SUBTIPOS.TI_MES,
    UN_MESFINAL     IN PCK_SUBTIPOS.TI_MES
  )
  AS
    MI_REEMPLAZOS   PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
  /*PRIMERO COLOCA LA APROPIACION INICIAL DEL ENCABEZADO EN CEROS*/
    BEGIN 
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'PLAN_PRESUPUESTAL', 
                                               UN_ACCION    => 'M', 
                                               UN_CAMPOS    => 'APROPIACIONINICIAL = 0',
                                               UN_CONDICION => '       COMPANIA           = '''|| UN_COMPANIA ||
                                                               ''' AND ANO                = '  || UN_ANO ||
                                                               '   AND MOVIMIENTO         IN (0) ' ||  
                                                               '   AND MAN_CEN_CTO        IN (0) ' ||
                                                               '   AND MAN_AUX_TER        IN (0) ' ||
                                                               '   AND MAN_AUX_GEN        IN (0) ' ||
                                                               '   AND MAN_AUX_FUE        IN (0) ' ||
                                                               '   AND MAN_AUX_REF        IN (0) ' ||
                                                               '   AND APROPIACIONINICIAL NOT IN (0)');

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
        MI_REEMPLAZOS(0).CLAVE:='COMPANIA';
        MI_REEMPLAZOS(0).VALOR:=UN_COMPANIA;
        MI_REEMPLAZOS(1).CLAVE:='ANIO';
        MI_REEMPLAZOS(1).VALOR:=UN_ANO;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_MAYORIZA_APRO_CEROS,
          UN_TABLAERROR => 'SALDO_PLAN_PPTAL',
          UN_REEMPLAZOS => MI_REEMPLAZOS  
        );
    END;                                         
      /*Para los saldos presupuestales. Coloca todas las Cuentas que No tienen movimiento en cero
      Menos la de Pac apropiado*/
    BEGIN 
      BEGIN 
      --<TICKET: 7723626 AUTOR:CP FECHA:22/12/2022  Se agrega creación de saldos pptal al proceso ya que no está creando el registro de la cuenta cuando esta ya existe >
       PCK_DATOS.GL_RTA := FC_CREARSALDOSPPTALES(
                                UN_COMPANIA      => UN_COMPANIA,
                                UN_ANIO          => UN_ANO
                              );
      --<TICKET/>
        PCK_DATOS.GL_RTA :=  PCK_DATOS.FC_ACME(
                             UN_TABLA       => 'SALDO_PLAN_PPTAL',
                             UN_ACCION      => 'MM',
                             UN_MERGEUSING  => 'SELECT COMPANIA,
                                                       ANO,
                                                       CODIGO
                                                FROM   PLAN_PRESUPUESTAL
                                                WHERE  COMPANIA   = '''|| UN_COMPANIA ||'''
                                                  AND  ANO        = '  || UN_ANO ||'
                                                  AND  (MOVIMIENTO       IN (0)
                                                         AND MAN_CEN_CTO IN (0) 
                                                         AND MAN_AUX_TER IN (0) 
                                                         AND MAN_AUX_GEN IN (0) 
                                                         AND MAN_AUX_FUE IN (0) 
                                                         AND MAN_AUX_REF IN (0))',
                             UN_MERGEENLACE => '    TABLA.COMPANIA = VISTA.COMPANIA
                                                AND TABLA.ANO      = VISTA.ANO 
                                                AND TABLA.CODIGO   = VISTA.CODIGO',
                             UN_MERGEEXISTE => 'UPDATE 
                                                  SET TABLA.ADICION                   = 0, 
                                                      TABLA.REDUCCION                 = 0, 
                                                      TABLA.PAC_PROGRAMADO            = 0, 
                                                      TABLA.DISPONIBILIDAD            = 0, 
                                                      TABLA.REG_NO_CONTRACT           = 0, 
                                                      TABLA.REG_CONTRACT              = 0, 
                                                      TABLA.REG_REVERSION             = 0, 
                                                      TABLA.MODIF_PAC_DEBITO          = 0, 
                                                      TABLA.MODIF_PAC_CREDITO         = 0, 
                                                      TABLA.MODIF_REG_CONT            = 0, 
                                                      TABLA.MODIF_REG_NOCONT          = 0, 
                                                      TABLA.REINTEGRO                 = 0, 
                                                      TABLA.VIGENCIAANTERIOR          = 0,
                                                      TABLA.VIGENCIAFUTURA            = 0, 
                                                      TABLA.TRASLADO_DEBITO           = 0, 
                                                      TABLA.TRASLADO_CREDITO          = 0, 
                                                      TABLA.APLAZAM_DEBITO            = 0, 
                                                      TABLA.APLAZAM_CREDITO           = 0, 
                                                      TABLA.EJE_CNT_DEBITO            = 0, 
                                                      TABLA.EJE_CNT_CREDITO           = 0,
                                                      TABLA.APROPIACION_DEBITO        = 0, 
                                                      TABLA.APROPIACION_CREDITO       = 0, 
                                                      TABLA.EJE_PPT_DEBITO            = 0, 
                                                      TABLA.EJE_PPT_CREDITO           = 0,
                                                      TABLA.INGRESOS_PAPELES          = 0, 
                                                      TABLA.INGRESOS_EFECTIVO         = 0, 
                                                      TABLA.MODIF_INGRESOS            = 0, 
                                                      TABLA.INGRESOS_CAUSADOS         = 0, 
                                                      TABLA.MODIF_INGRESOS_CAUSADOS   = 0, 
                                                      TABLA.REGISTRO_OBLIGACION       = 0, 
                                                      TABLA.MODIF_REGISTRO_OBLIGACION = 0,
                                                      TABLA.MODIF_INGRESOS_PAPELES    = 0, 
                                                      TABLA.MODIF_INGRESOS_EFECTIVO   = 0 
                                                WHERE TABLA.COMPANIA = '''|| UN_COMPANIA ||'''
                                                  AND TABLA.ANO      = '  || UN_ANO ||'
                                                  AND TABLA.MES      BETWEEN '|| UN_MESINICIAL||' AND '|| UN_MESFINAL); 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
        MI_REEMPLAZOS(0).CLAVE := 'COMPANIA';
        MI_REEMPLAZOS(0).VALOR := UN_COMPANIA;
        MI_REEMPLAZOS(1).CLAVE := 'ANIO';
        MI_REEMPLAZOS(1).VALOR := UN_ANO;
        MI_REEMPLAZOS(2).CLAVE := 'MESINICIAL';
        MI_REEMPLAZOS(2).VALOR := UN_MESINICIAL;
        MI_REEMPLAZOS(3).CLAVE := 'MESFINAL';
        MI_REEMPLAZOS(3).VALOR := UN_MESFINAL;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD     => SQLCODE,
          UN_ERROR_COD   => PCK_ERRORES.ERR_PPTO_MAYORIZA_SALDO_CEROS,
          UN_TABLAERROR  => 'SALDO_PLAN_PPTAL',
          UN_REEMPLAZOS => MI_REEMPLAZOS  
        );
    END;

    BEGIN 
      BEGIN
        PCK_DATOS.GL_RTA :=  PCK_DATOS.FC_ACME(
                             UN_TABLA       => 'SALDO_PLAN_PPTAL',
                             UN_ACCION      => 'MM',
                             UN_MERGEUSING  => 'SELECT  SALDO_AUX_PPTAL.COMPANIA,
                                                        SALDO_AUX_PPTAL.NATURALEZA,
                                                        SALDO_AUX_PPTAL.CODIGO,
                                                        SALDO_AUX_PPTAL.ANO,
                                                        SALDO_AUX_PPTAL.MES,
                                                        SUM(SALDO_AUX_PPTAL.ADICION)                      ADICION,
                                                        SUM(SALDO_AUX_PPTAL.REDUCCION)                    REDUCCION,
                                                        SUM(SALDO_AUX_PPTAL.PAC_APROPIADO)                PAC_APROPIADO,
                                                        SUM(SALDO_AUX_PPTAL.PAC_PROGRAMADO)               PAC_PROGRAMADO,
                                                        SUM(SALDO_AUX_PPTAL.DISPONIBILIDAD)               DISPONIBILIDAD,
                                                        SUM(SALDO_AUX_PPTAL.DISPONIBILIDADADD)            DISPONIBILIDADADD,
                                                        SUM(SALDO_AUX_PPTAL.DISPONIBILIDADDMD)            DISPONIBILIDADDMD,
                                                        SUM(SALDO_AUX_PPTAL.REG_NO_CONTRACT)              REG_NO_CONTRACT,
                                                        SUM(SALDO_AUX_PPTAL.REG_CONTRACT)                 REG_CONTRACT,
                                                        SUM(SALDO_AUX_PPTAL.REG_REVERSION)                REG_REVERSION,
                                                        SUM(SALDO_AUX_PPTAL.MODIF_PAC_DEBITO)             MODIF_PAC_DEBITO,
                                                        SUM(SALDO_AUX_PPTAL.MODIF_PAC_CREDITO)            MODIF_PAC_CREDITO,
                                                        SUM(SALDO_AUX_PPTAL.MODIF_REG_CONT)               MODIF_REG_CONT,
                                                        SUM(SALDO_AUX_PPTAL.MODIF_REG_NOCONT)             MODIF_REG_NOCONT,
                                                        SUM(SALDO_AUX_PPTAL.MODIF_REG_CONTADR)            MODIF_REG_CONTADR,
                                                        SUM(SALDO_AUX_PPTAL.MODIF_REG_NOCONTADR)          MODIF_REG_NOCONTADR,
                                                        SUM(SALDO_AUX_PPTAL.MODIF_REG_CONTDMR)            MODIF_REG_CONTDMR,
                                                        SUM(SALDO_AUX_PPTAL.MODIF_REG_NOCONTDMR)          MODIF_REG_NOCONTDMR,
                                                        SUM(SALDO_AUX_PPTAL.REINTEGRO)                    REINTEGRO,
                                                        SUM(SALDO_AUX_PPTAL.VIGENCIAANTERIOR)             VIGENCIAANTERIOR,
                                                        SUM(SALDO_AUX_PPTAL.VIGENCIAFUTURA)               VIGENCIAFUTURA,
                                                        SUM(SALDO_AUX_PPTAL.TRASLADO_DEBITO)              TRASLADO_DEBITO,
                                                        SUM(SALDO_AUX_PPTAL.TRASLADO_CREDITO)             TRASLADO_CREDITO,
                                                        SUM(SALDO_AUX_PPTAL.APLAZAM_DEBITO)               APLAZAM_DEBITO,
                                                        SUM(SALDO_AUX_PPTAL.APLAZAM_CREDITO)              APLAZAM_CREDITO,
                                                        SUM(SALDO_AUX_PPTAL.EJE_CNT_DEBITO)               EJE_CNT_DEBITO,
                                                        SUM(SALDO_AUX_PPTAL.EJE_CNT_CREDITO)              EJE_CNT_CREDITO,
                                                        SUM(SALDO_AUX_PPTAL.APROPIACION_DEBITO)           APROPIACION_DEBITO,
                                                        SUM(SALDO_AUX_PPTAL.APROPIACION_CREDITO)          APROPIACION_CREDITO,
                                                        SUM(SALDO_AUX_PPTAL.EJE_PPT_DEBITO)               EJE_PPT_DEBITO,
                                                        SUM(SALDO_AUX_PPTAL.EJE_PPT_CREDITO)              EJE_PPT_CREDITO,
                                                        SUM(SALDO_AUX_PPTAL.EJE_PPT_DEBITOAEG)            EJE_PPT_DEBITOAEG,
                                                        SUM(SALDO_AUX_PPTAL.EJE_PPT_DEBITODEG)            EJE_PPT_DEBITODEG,
                                                        SUM(SALDO_AUX_PPTAL.REGISTRO_OBLIGACION)          REGISTRO_OBLIGACION,
                                                        SUM(SALDO_AUX_PPTAL.MODIF_REGISTRO_OBLIGACION)    MODIF_REGISTRO_OBLIGACION,
                                                        SUM(SALDO_AUX_PPTAL.MODIF_REGISTRO_OBLIGACIONARO) MODIF_REGISTRO_OBLIGACIONARO,
                                                        SUM(SALDO_AUX_PPTAL.MODIF_REGISTRO_OBLIGACIONDRO) MODIF_REGISTRO_OBLIGACIONDRO,
                                                        SUM(SALDO_AUX_PPTAL.INGRESOS_EFECTIVO)            INGRESOS_EFECTIVO,
                                                        SUM(SALDO_AUX_PPTAL.INGRESOS_PAPELES)             INGRESOS_PAPELES,
                                                        SUM(SALDO_AUX_PPTAL.MODIF_INGRESOS_EFECTIVO)      MODIF_INGRESOS_EFECTIVO,
                                                        SUM(SALDO_AUX_PPTAL.MODIF_INGRESOS_PAPELES)       MODIF_INGRESOS_PAPELES,
                                                        SUM(SALDO_AUX_PPTAL.INGRESOS_CAUSADOS)            INGRESOS_CAUSADOS,
                                                        SUM(SALDO_AUX_PPTAL.MODIF_INGRESOS_CAUSADOS)      MODIF_INGRESOS_CAUSADOS,
                                                        SUM(SALDO_AUX_PPTAL.MODIF_INGRESOS)               MODIF_INGRESOS,
                                                        SUM(SALDO_AUX_PPTAL.NETOEGRESO)                   NETOEGRESO,
                                                        SUM(SALDO_AUX_PPTAL.RECONOCIMIENTOS)              RECONOCIMIENTOS,
                                                        SUM(SALDO_AUX_PPTAL.PACTESORERIA)                 PACTESORERIA,
                                                        SUM(SALDO_AUX_PPTAL.PAC_EJECUTADO)                PAC_EJECUTADO,
                                                        SUM(SALDO_AUX_PPTAL.PAC_COMPROMETIDO)             PAC_COMPROMETIDO  
                                                FROM    SALDO_PLAN_PPTAL
                                                  INNER JOIN SALDO_AUX_PPTAL 
                                                    ON  SALDO_PLAN_PPTAL.COMPANIA = SALDO_AUX_PPTAL.COMPANIA
                                                    AND SALDO_PLAN_PPTAL.ANO      = SALDO_AUX_PPTAL.ANO
                                                    AND SALDO_PLAN_PPTAL.CODIGO   = SALDO_AUX_PPTAL.CODIGO
                                                    AND SALDO_PLAN_PPTAL.MES      = SALDO_AUX_PPTAL.MES
                                                WHERE   SALDO_PLAN_PPTAL.COMPANIA = '''|| UN_COMPANIA ||'''
                                                  AND   SALDO_PLAN_PPTAL.ANO      = '  || UN_ANO ||'
                                                  AND   SALDO_PLAN_PPTAL.MES      BETWEEN '|| UN_MESINICIAL||' AND '|| UN_MESFINAL||' 
                                                GROUP BY SALDO_AUX_PPTAL.COMPANIA, 
                                                        SALDO_AUX_PPTAL.NATURALEZA, 
                                                        SALDO_AUX_PPTAL.CODIGO, 
                                                        SALDO_AUX_PPTAL.ANO, 
                                                        SALDO_AUX_PPTAL.MES',
                             UN_MERGEENLACE => '    TABLA.COMPANIA = VISTA.COMPANIA
                                                AND TABLA.ANO      = VISTA.ANO 
                                                AND TABLA.CODIGO   = VISTA.CODIGO
                                                AND TABLA.MES      = VISTA.MES ',
                             UN_MERGEEXISTE => 'UPDATE 
                                                  SET TABLA.ADICION                      = VISTA.ADICION,
                                                      TABLA.REDUCCION                    = VISTA.REDUCCION,
                                                      TABLA.PAC_APROPIADO                = VISTA.PAC_APROPIADO,
                                                      TABLA.PAC_PROGRAMADO               = VISTA.PAC_PROGRAMADO,
                                                      TABLA.DISPONIBILIDAD               = VISTA.DISPONIBILIDAD,
                                                      TABLA.DISPONIBILIDADADD            = VISTA.DISPONIBILIDADADD,
                                                      TABLA.DISPONIBILIDADDMD            = VISTA.DISPONIBILIDADDMD,
                                                      TABLA.REG_NO_CONTRACT              = VISTA.REG_NO_CONTRACT,
                                                      TABLA.REG_CONTRACT                 = VISTA.REG_CONTRACT,
                                                      TABLA.REG_REVERSION                = VISTA.REG_REVERSION,
                                                      TABLA.MODIF_PAC_DEBITO             = VISTA.MODIF_PAC_DEBITO,
                                                      TABLA.MODIF_PAC_CREDITO            = VISTA.MODIF_PAC_CREDITO,
                                                      TABLA.MODIF_REG_CONT               = VISTA.MODIF_REG_CONT,
                                                      TABLA.MODIF_REG_NOCONT             = VISTA.MODIF_REG_NOCONT,
                                                      TABLA.MODIF_REG_CONTADR            = VISTA.MODIF_REG_CONTADR,
                                                      TABLA.MODIF_REG_NOCONTADR          = VISTA.MODIF_REG_NOCONTADR,
                                                      TABLA.MODIF_REG_CONTDMR            = VISTA.MODIF_REG_CONTDMR,
                                                      TABLA.MODIF_REG_NOCONTDMR          = VISTA.MODIF_REG_NOCONTDMR,
                                                      TABLA.REINTEGRO                    = VISTA.REINTEGRO,
                                                      TABLA.VIGENCIAANTERIOR             = VISTA.VIGENCIAANTERIOR,
                                                      TABLA.VIGENCIAFUTURA               = VISTA.VIGENCIAFUTURA,
                                                      TABLA.TRASLADO_DEBITO              = VISTA.TRASLADO_DEBITO,
                                                      TABLA.TRASLADO_CREDITO             = VISTA.TRASLADO_CREDITO,
                                                      TABLA.APLAZAM_DEBITO               = VISTA.APLAZAM_DEBITO,
                                                      TABLA.APLAZAM_CREDITO              = VISTA.APLAZAM_CREDITO,
                                                      TABLA.EJE_CNT_DEBITO               = VISTA.EJE_CNT_DEBITO,
                                                      TABLA.EJE_CNT_CREDITO              = VISTA.EJE_CNT_CREDITO,
                                                      TABLA.APROPIACION_DEBITO           = VISTA.APROPIACION_DEBITO,
                                                      TABLA.APROPIACION_CREDITO          = VISTA.APROPIACION_CREDITO,
                                                      TABLA.EJE_PPT_DEBITO               = VISTA.EJE_PPT_DEBITO,
                                                      TABLA.EJE_PPT_CREDITO              = VISTA.EJE_PPT_CREDITO,
                                                      TABLA.EJE_PPT_DEBITOAEG            = VISTA.EJE_PPT_DEBITOAEG,
                                                      TABLA.EJE_PPT_DEBITODEG            = VISTA.EJE_PPT_DEBITODEG,
                                                      TABLA.REGISTRO_OBLIGACION          = VISTA.REGISTRO_OBLIGACION,
                                                      TABLA.MODIF_REGISTRO_OBLIGACION    = VISTA.MODIF_REGISTRO_OBLIGACION,
                                                      TABLA.MODIF_REGISTRO_OBLIGACIONARO = VISTA.MODIF_REGISTRO_OBLIGACIONARO,
                                                      TABLA.MODIF_REGISTRO_OBLIGACIONDRO = VISTA.MODIF_REGISTRO_OBLIGACIONDRO,
                                                      TABLA.INGRESOS_EFECTIVO            = VISTA.INGRESOS_EFECTIVO,
                                                      TABLA.INGRESOS_PAPELES             = VISTA.INGRESOS_PAPELES,
                                                      TABLA.MODIF_INGRESOS_EFECTIVO      = VISTA.MODIF_INGRESOS_EFECTIVO,
                                                      TABLA.MODIF_INGRESOS_PAPELES       = VISTA.MODIF_INGRESOS_PAPELES,
                                                      TABLA.INGRESOS_CAUSADOS            = VISTA.INGRESOS_CAUSADOS,
                                                      TABLA.MODIF_INGRESOS_CAUSADOS      = VISTA.MODIF_INGRESOS_CAUSADOS,
                                                      TABLA.MODIF_INGRESOS               = VISTA.MODIF_INGRESOS,
                                                      TABLA.NETOEGRESO                   = VISTA.NETOEGRESO,
                                                      TABLA.RECONOCIMIENTOS              = VISTA.RECONOCIMIENTOS,
                                                      TABLA.PACTESORERIA                 = VISTA.PACTESORERIA,
                                                      TABLA.PAC_EJECUTADO                = VISTA.PAC_EJECUTADO,
                                                      TABLA.PAC_COMPROMETIDO             = VISTA.PAC_COMPROMETIDO  
                                                WHERE TABLA.COMPANIA = '''|| UN_COMPANIA ||'''
                                                  AND TABLA.ANO      = '  || UN_ANO ||'
                                                  AND TABLA.MES      BETWEEN '|| UN_MESINICIAL||' AND '|| UN_MESFINAL); 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
        MI_REEMPLAZOS(0).CLAVE := 'COMPANIA';
        MI_REEMPLAZOS(0).VALOR := UN_COMPANIA;
        MI_REEMPLAZOS(1).CLAVE := 'ANIO';
        MI_REEMPLAZOS(1).VALOR := UN_ANO;
        MI_REEMPLAZOS(2).CLAVE := 'MESINICIAL';
        MI_REEMPLAZOS(2).VALOR := UN_MESINICIAL;
        MI_REEMPLAZOS(3).CLAVE := 'MESFINAL';
        MI_REEMPLAZOS(3).VALOR := UN_MESFINAL;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD     => SQLCODE,
          UN_ERROR_COD   => PCK_ERRORES.ERR_PPTO_MAYORIZA_SALDO_PLAN,
          UN_TABLAERROR  => 'SALDO_PLAN_PPTAL',
          UN_REEMPLAZOS  => MI_REEMPLAZOS  
        );
    END;

    BEGIN 
      BEGIN
        PCK_DATOS.GL_RTA :=  PCK_DATOS.FC_ACME(
                             UN_TABLA       => 'PLAN_PRESUPUESTAL',
                             UN_ACCION      => 'MM',
                             UN_MERGEUSING  => 'SELECT  PLAN_PRESUPUESTAL.COMPANIA,
                                                        PLAN_PRESUPUESTAL.ANO, 
                                                        PLAN_PRESUPUESTAL.CODIGO,
                                                        SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA=APROPIACIONES.NATURALEZA  
                                                                 THEN NVL(APROPIACIONES.APROPIACIONINICIAL,0)
                                                                 ELSE NVL(APROPIACIONES.APROPIACIONINICIAL,0)*-1
                                                            END) APROPIACIONINICIAL
                                                FROM    PLAN_PRESUPUESTAL 
                                                  INNER JOIN (
                                                    SELECT COMPANIA,
                                                           ANO,
                                                           CODIGO,
                                                           NATURALEZA, 
                                                           APROPIACIONINICIAL 
                                                    FROM   PLAN_PRESUPUESTAL 
                                                    WHERE  COMPANIA = '''|| UN_COMPANIA||'''
                                                      AND  ANO      = '  || UN_ANO||' 
                                                      AND  (MOVIMIENTO      NOT IN (0)
                                                             OR MAN_CEN_CTO NOT IN (0) 
                                                             OR MAN_AUX_TER NOT IN (0) 
                                                             OR MAN_AUX_GEN NOT IN (0) 
                                                             OR MAN_AUX_FUE NOT IN (0) 
                                                             OR MAN_AUX_REF NOT IN (0))) APROPIACIONES
                                                    ON  PLAN_PRESUPUESTAL.COMPANIA = APROPIACIONES.COMPANIA    
                                                    AND PLAN_PRESUPUESTAL.ANO      = APROPIACIONES.ANO
                                                    AND SUBSTR(APROPIACIONES.CODIGO,
                                                          0,
                                                          LENGTH(PLAN_PRESUPUESTAL.CODIGO)) = PLAN_PRESUPUESTAL.CODIGO
                                                WHERE   PLAN_PRESUPUESTAL.COMPANIA = '''|| UN_COMPANIA||''' 
                                                  AND   PLAN_PRESUPUESTAL.ANO      = '  || UN_ANO||'
                                                  AND   (PLAN_PRESUPUESTAL.MOVIMIENTO  IN (0)
                                                          AND PLAN_PRESUPUESTAL.MAN_CEN_CTO IN (0) 
                                                          AND PLAN_PRESUPUESTAL.MAN_AUX_TER IN (0) 
                                                          AND PLAN_PRESUPUESTAL.MAN_AUX_GEN IN (0) 
                                                          AND PLAN_PRESUPUESTAL.MAN_AUX_FUE IN (0) 
                                                          AND PLAN_PRESUPUESTAL.MAN_AUX_REF IN (0)) 
                                                GROUP BY PLAN_PRESUPUESTAL.COMPANIA, 
                                                        PLAN_PRESUPUESTAL.CODIGO, 
                                                        PLAN_PRESUPUESTAL.ANO',
                             UN_MERGEENLACE => '    TABLA.COMPANIA = VISTA.COMPANIA
                                                AND TABLA.ANO      = VISTA.ANO 
                                                AND TABLA.CODIGO   = VISTA.CODIGO',
                             UN_MERGEEXISTE => 'UPDATE 
                                                  SET TABLA.APROPIACIONINICIAL = VISTA.APROPIACIONINICIAL ');  
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
        MI_REEMPLAZOS(0).CLAVE := 'COMPANIA';
        MI_REEMPLAZOS(0).VALOR := UN_COMPANIA;
        MI_REEMPLAZOS(1).CLAVE := 'ANIO';
        MI_REEMPLAZOS(1).VALOR := UN_ANO;

        PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_MAYORIZA_APRO_INI,
            UN_TABLAERROR => 'SALDO_PLAN_PPTAL',
            UN_REEMPLAZOS => MI_REEMPLAZOS  
        );
    END;
           --'Primero la apropiacion de saldo_plan_pptal
    BEGIN 
      BEGIN 
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => 'SALDO_PLAN_PPTAL',
                                              UN_ACCION      => 'MM',
                                              UN_MERGEUSING  => 'SELECT COMPANIA,
                                                                        ANO,
                                                                        CODIGO,
                                                                        CASE WHEN NATURALEZA=''D'' 
                                                                             THEN APROPIACIONINICIAL 
                                                                             ELSE 0 
                                                                        END APROPIACION_DEBITO,
                                                                        CASE WHEN NATURALEZA=''C'' 
                                                                             THEN APROPIACIONINICIAL 
                                                                             ELSE 0 
                                                                        END APROPIACION_CREDITO
                                                                 FROM   PLAN_PRESUPUESTAL
                                                                 WHERE  COMPANIA  = '''|| UN_COMPANIA||'''
                                                                   AND  ANO       = '  || UN_ANO||'
                                                                   AND  (MOVIMIENTO      NOT IN (0)
                                                                          OR MAN_CEN_CTO NOT IN (0) 
                                                                          OR MAN_AUX_TER NOT IN (0) 
                                                                          OR MAN_AUX_GEN NOT IN (0) 
                                                                          OR MAN_AUX_FUE NOT IN (0) 
                                                                          OR MAN_AUX_REF NOT IN (0))',
                                              UN_MERGEENLACE => '    TABLA.COMPANIA = VISTA.COMPANIA
                                                                 AND TABLA.ANO      = VISTA.ANO 
                                                                 AND TABLA.CODIGO   = VISTA.CODIGO',
                                              UN_MERGEEXISTE => 'UPDATE 
                                                                   SET    TABLA.APROPIACION_DEBITO  = VISTA.APROPIACION_DEBITO, 
                                                                          TABLA.APROPIACION_CREDITO = VISTA.APROPIACION_CREDITO
                                                                   WHERE  TABLA.COMPANIA = '''|| UN_COMPANIA ||'''
                                                                     AND  TABLA.MES      = 0'); 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
        MI_REEMPLAZOS(0).CLAVE:='COMPANIA';
        MI_REEMPLAZOS(0).VALOR:=UN_COMPANIA;
        MI_REEMPLAZOS(1).CLAVE:='ANIO';
        MI_REEMPLAZOS(1).VALOR:=UN_ANO;

        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_MAYORIZA_APRO_INI_SAL,
          UN_TABLAERROR => 'SALDO_PLAN_PPTAL',
          UN_REEMPLAZOS => MI_REEMPLAZOS  
        );
    END;

    BEGIN 
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => 'SALDO_PLAN_PPTAL',
                                              UN_ACCION      => 'MM',
                                              UN_MERGEUSING  => 'SELECT COMPANIA,
                                                                        ANO,
                                                                        CODIGO,
                                                                        CASE WHEN NATURALEZA=''D'' 
                                                                             THEN APROPIACIONINICIAL 
                                                                             ELSE 0 
                                                                        END APROPIACION_DEBITO,
                                                                        CASE WHEN NATURALEZA=''C'' 
                                                                             THEN APROPIACIONINICIAL 
                                                                             ELSE 0 
                                                                        END APROPIACION_CREDITO
                                                                 FROM   PLAN_PRESUPUESTAL
                                                                 WHERE  COMPANIA = '''|| UN_COMPANIA||'''
                                                                 AND    ANO      = '  || UN_ANO||'
                                                                 AND    (MOVIMIENTO  IN (0)
                                                                          AND MAN_CEN_CTO IN (0) 
                                                                          AND MAN_AUX_TER IN (0) 
                                                                          AND MAN_AUX_GEN IN (0) 
                                                                          AND MAN_AUX_FUE IN (0) 
                                                                          AND MAN_AUX_REF IN (0))',
                                              UN_MERGEENLACE => '    TABLA.COMPANIA = VISTA.COMPANIA
                                                                 AND TABLA.ANO      = VISTA.ANO 
                                                                 AND TABLA.CODIGO   = VISTA.CODIGO',
                                              UN_MERGEEXISTE => 'UPDATE 
                                                                   SET    TABLA.APROPIACION_DEBITO  = VISTA.APROPIACION_DEBITO, 
                                                                          TABLA.APROPIACION_CREDITO = VISTA.APROPIACION_CREDITO
                                                                   WHERE  TABLA.COMPANIA = '''|| UN_COMPANIA ||'''
                                                                     AND  TABLA.MES      = 0'); 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
        MI_REEMPLAZOS(0).CLAVE := 'COMPANIA';
        MI_REEMPLAZOS(0).VALOR := UN_COMPANIA;
        MI_REEMPLAZOS(1).CLAVE := 'ANIO';
        MI_REEMPLAZOS(1).VALOR := UN_ANO;

        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_MAYORIZA_APRO_INI2,
          UN_TABLAERROR => 'SALDO_PLAN_PPTAL',
          UN_REEMPLAZOS => MI_REEMPLAZOS  
        );
    END;

    BEGIN 
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => 'SALDO_PLAN_PPTAL',
                                              UN_ACCION      => 'MM',
                                              UN_MERGEUSING  => 'SELECT  SALDO_PLAN_PPTAL.COMPANIA,
                                                                         SALDO_PLAN_PPTAL.CODIGO,
                                                                         SALDO_PLAN_PPTAL.ANO,
                                                                         SALDO_PLAN_PPTAL.MES,
                                                                         SUM(SALDOS.ADICION)                      ADICION,
                                                                         SUM(SALDOS.REDUCCION)                    REDUCCION,
                                                                         SUM(SALDOS.PAC_PROGRAMADO)               PAC_PROGRAMADO,
                                                                         SUM(SALDOS.DISPONIBILIDAD)               DISPONIBILIDAD,
                                                                         SUM(SALDOS.DISPONIBILIDADADD)            DISPONIBILIDADADD,
                                                                         SUM(SALDOS.DISPONIBILIDADDMD)            DISPONIBILIDADDMD,
                                                                         SUM(SALDOS.REG_NO_CONTRACT)              REG_NO_CONTRACT,
                                                                         SUM(SALDOS.REG_CONTRACT)                 REG_CONTRACT,
                                                                         SUM(SALDOS.REG_REVERSION)                REG_REVERSION,
                                                                         SUM(SALDOS.MODIF_PAC_DEBITO)             MODIF_PAC_DEBITO,
                                                                         SUM(SALDOS.MODIF_PAC_CREDITO)            MODIF_PAC_CREDITO,
                                                                         SUM(SALDOS.MODIF_REG_CONT)               MODIF_REG_CONT,
                                                                         SUM(SALDOS.MODIF_REG_NOCONT)             MODIF_REG_NOCONT,
                                                                         SUM(SALDOS.MODIF_REG_CONTADR)            MODIF_REG_CONTADR,
                                                                         SUM(SALDOS.MODIF_REG_NOCONTADR)          MODIF_REG_NOCONTADR,
                                                                         SUM(SALDOS.MODIF_REG_CONTDMR)            MODIF_REG_CONTDMR,
                                                                         SUM(SALDOS.MODIF_REG_NOCONTDMR)          MODIF_REG_NOCONTDMR,
                                                                         SUM(SALDOS.REINTEGRO)                    REINTEGRO,
                                                                         SUM(SALDOS.VIGENCIAANTERIOR)             VIGENCIAANTERIOR,
                                                                         SUM(SALDOS.VIGENCIAFUTURA)               VIGENCIAFUTURA,
                                                                         SUM(SALDOS.TRASLADO_DEBITO)              TRASLADO_DEBITO,
                                                                         SUM(SALDOS.TRASLADO_CREDITO)             TRASLADO_CREDITO,
                                                                         SUM(SALDOS.APLAZAM_DEBITO)               APLAZAM_DEBITO,
                                                                         SUM(SALDOS.APLAZAM_CREDITO)              APLAZAM_CREDITO, 
                                                                         SUM(SALDOS.APROPIACION_DEBITO)           APROPIACION_DEBITO,
                                                                         SUM(SALDOS.APROPIACION_CREDITO)          APROPIACION_CREDITO,
                                                                         SUM(SALDOS.EJE_CNT_DEBITO)               EJE_CNT_DEBITO,
                                                                         SUM(SALDOS.EJE_CNT_CREDITO)              EJE_CNT_CREDITO,
                                                                         SUM(SALDOS.EJE_PPT_DEBITO)               EJE_PPT_DEBITO,
                                                                         SUM(SALDOS.EJE_PPT_CREDITO)              EJE_PPT_CREDITO,
                                                                         SUM(SALDOS.EJE_PPT_DEBITOAEG)            EJE_PPT_DEBITOAEG,
                                                                         SUM(SALDOS.EJE_PPT_DEBITODEG)            EJE_PPT_DEBITODEG,
                                                                         SUM(SALDOS.REGISTRO_OBLIGACION)          REGISTRO_OBLIGACION,
                                                                         SUM(SALDOS.MODIF_REGISTRO_OBLIGACION)    MODIF_REGISTRO_OBLIGACION,
                                                                         SUM(SALDOS.MODIF_REGISTRO_OBLIGACIONARO) MODIF_REGISTRO_OBLIGACIONARO,
                                                                         SUM(SALDOS.MODIF_REGISTRO_OBLIGACIONDRO) MODIF_REGISTRO_OBLIGACIONDRO,
                                                                         SUM(SALDOS.INGRESOS_EFECTIVO)            INGRESOS_EFECTIVO,
                                                                         SUM(SALDOS.INGRESOS_PAPELES)             INGRESOS_PAPELES,
                                                                         SUM(SALDOS.MODIF_INGRESOS_EFECTIVO)      MODIF_INGRESOS_EFECTIVO,
                                                                         SUM(SALDOS.MODIF_INGRESOS_PAPELES)       MODIF_INGRESOS_PAPELES,
                                                                         SUM(SALDOS.INGRESOS_CAUSADOS)            INGRESOS_CAUSADOS,
                                                                         SUM(SALDOS.MODIF_INGRESOS_CAUSADOS)      MODIF_INGRESOS_CAUSADOS,
                                                                         SUM(SALDOS.MODIF_INGRESOS)               MODIF_INGRESOS,
                                                                         SUM(SALDOS.NETOEGRESO)                   NETOEGRESO,
                                                                         SUM(SALDOS.RECONOCIMIENTOS)              RECONOCIMIENTOS,
                                                                         SUM(SALDOS.PACTESORERIA)                 PACTESORERIA,
                                                                         SUM(SALDOS.PAC_EJECUTADO)                PAC_EJECUTADO,
                                                                         SUM(SALDOS.PAC_COMPROMETIDO)             PAC_COMPROMETIDO 
                                                                 FROM    SALDO_PLAN_PPTAL 
                                                                   INNER JOIN (
                                                                     SELECT  PLAN_PRESUPUESTAL.NATURALEZA,
                                                                             PLAN_PRESUPUESTAL.MOVIMIENTO,
                                                                             SALDO_PLAN_PPTAL.COMPANIA,
                                                                             SALDO_PLAN_PPTAL.CODIGO,
                                                                             SALDO_PLAN_PPTAL.ANO,
                                                                             SALDO_PLAN_PPTAL.MES,
                                                                             SALDO_PLAN_PPTAL.ADICION,
                                                                             SALDO_PLAN_PPTAL.REDUCCION,
                                                                             SALDO_PLAN_PPTAL.PAC_PROGRAMADO,
                                                                             SALDO_PLAN_PPTAL.DISPONIBILIDAD,
                                                                             SALDO_PLAN_PPTAL.DISPONIBILIDADADD,
                                                                             SALDO_PLAN_PPTAL.DISPONIBILIDADDMD,
                                                                             SALDO_PLAN_PPTAL.REG_NO_CONTRACT,
                                                                             SALDO_PLAN_PPTAL.REG_CONTRACT,
                                                                             SALDO_PLAN_PPTAL.REG_REVERSION,
                                                                             SALDO_PLAN_PPTAL.MODIF_PAC_DEBITO,
                                                                             SALDO_PLAN_PPTAL.MODIF_PAC_CREDITO,
                                                                             SALDO_PLAN_PPTAL.MODIF_REG_CONT,
                                                                             SALDO_PLAN_PPTAL.MODIF_REG_NOCONT,
                                                                             SALDO_PLAN_PPTAL.MODIF_REG_CONTADR,
                                                                             SALDO_PLAN_PPTAL.MODIF_REG_NOCONTADR,
                                                                             SALDO_PLAN_PPTAL.MODIF_REG_CONTDMR,
                                                                             SALDO_PLAN_PPTAL.MODIF_REG_NOCONTDMR,
                                                                             SALDO_PLAN_PPTAL.REINTEGRO,
                                                                             SALDO_PLAN_PPTAL.VIGENCIAANTERIOR,
                                                                             SALDO_PLAN_PPTAL.VIGENCIAFUTURA,
                                                                             SALDO_PLAN_PPTAL.TRASLADO_DEBITO,
                                                                             SALDO_PLAN_PPTAL.TRASLADO_CREDITO,
                                                                             SALDO_PLAN_PPTAL.APLAZAM_DEBITO,
                                                                             SALDO_PLAN_PPTAL.APLAZAM_CREDITO,
                                                                             SALDO_PLAN_PPTAL.EJE_CNT_DEBITO,
                                                                             SALDO_PLAN_PPTAL.EJE_CNT_CREDITO,
                                                                             SALDO_PLAN_PPTAL.APROPIACION_DEBITO,
                                                                             SALDO_PLAN_PPTAL.APROPIACION_CREDITO,
                                                                             SALDO_PLAN_PPTAL.EJE_PPT_DEBITO,
                                                                             SALDO_PLAN_PPTAL.EJE_PPT_CREDITO,
                                                                             SALDO_PLAN_PPTAL.EJE_PPT_DEBITOAEG,
                                                                             SALDO_PLAN_PPTAL.EJE_PPT_DEBITODEG,
                                                                             SALDO_PLAN_PPTAL.REGISTRO_OBLIGACION,
                                                                             SALDO_PLAN_PPTAL.MODIF_REGISTRO_OBLIGACION,
                                                                             SALDO_PLAN_PPTAL.MODIF_REGISTRO_OBLIGACIONARO,
                                                                             SALDO_PLAN_PPTAL.MODIF_REGISTRO_OBLIGACIONDRO,
                                                                             SALDO_PLAN_PPTAL.INGRESOS_EFECTIVO,
                                                                             SALDO_PLAN_PPTAL.INGRESOS_PAPELES,
                                                                             SALDO_PLAN_PPTAL.MODIF_INGRESOS_EFECTIVO,
                                                                             SALDO_PLAN_PPTAL.MODIF_INGRESOS_PAPELES,
                                                                             SALDO_PLAN_PPTAL.INGRESOS_CAUSADOS,
                                                                             SALDO_PLAN_PPTAL.MODIF_INGRESOS_CAUSADOS,
                                                                             SALDO_PLAN_PPTAL.MODIF_INGRESOS,
                                                                             SALDO_PLAN_PPTAL.NETOEGRESO,
                                                                             SALDO_PLAN_PPTAL.RECONOCIMIENTOS,
                                                                             SALDO_PLAN_PPTAL.PACTESORERIA,
                                                                             SALDO_PLAN_PPTAL.PAC_EJECUTADO,
                                                                             SALDO_PLAN_PPTAL.PAC_COMPROMETIDO  
                                                                     FROM    PLAN_PRESUPUESTAL
                                                                       INNER JOIN SALDO_PLAN_PPTAL 
                                                                         ON  PLAN_PRESUPUESTAL.COMPANIA = SALDO_PLAN_PPTAL.COMPANIA
                                                                         AND PLAN_PRESUPUESTAL.ANO      = SALDO_PLAN_PPTAL.ANO
                                                                         AND PLAN_PRESUPUESTAL.CODIGO   = SALDO_PLAN_PPTAL.CODIGO
                                                                     WHERE   PLAN_PRESUPUESTAL.COMPANIA = '''|| UN_COMPANIA ||'''
                                                                       AND   PLAN_PRESUPUESTAL.ANO      = '  || UN_ANO ||'
                                                                       AND   (MOVIMIENTO                NOT IN (0)
                                                                       OR    MAN_CEN_CTO                NOT IN (0) 
                                                                       OR    MAN_AUX_TER                NOT IN (0) 
                                                                       OR    MAN_AUX_GEN                NOT IN (0) 
                                                                       OR    MAN_AUX_FUE                NOT IN (0) 
                                                                       OR    MAN_AUX_REF                NOT IN (0))
                                                                       AND   MES                        BETWEEN '|| UN_MESINICIAL ||' AND '|| UN_MESFINAL||') SALDOS
                                                                     ON  SALDO_PLAN_PPTAL.COMPANIA         = SALDOS.COMPANIA    
                                                                     AND SALDO_PLAN_PPTAL.ANO              = SALDOS.ANO
                                                                     AND SALDO_PLAN_PPTAL.MES              = SALDOS.MES
                                                                     AND SUBSTR(SALDOS.CODIGO,
                                                                           1,
                                                                           LENGTH(SALDO_PLAN_PPTAL.CODIGO)) = SALDO_PLAN_PPTAL.CODIGO 
                                                                       INNER JOIN PLAN_PRESUPUESTAL 
                                                                         ON   SALDO_PLAN_PPTAL.COMPANIA = PLAN_PRESUPUESTAL.COMPANIA
                                                                         AND  SALDO_PLAN_PPTAL.ANO      = PLAN_PRESUPUESTAL.ANO
                                                                         AND  SALDO_PLAN_PPTAL.CODIGO   = PLAN_PRESUPUESTAL.CODIGO
                                                                 WHERE   SALDO_PLAN_PPTAL.COMPANIA     = '''|| UN_COMPANIA ||'''
                                                                   AND   SALDO_PLAN_PPTAL.ANO          = '  || UN_ANO ||'
                                                                   AND   (PLAN_PRESUPUESTAL.MOVIMIENTO IN (0)
                                                                   AND   PLAN_PRESUPUESTAL.MAN_CEN_CTO IN (0) 
                                                                   AND   PLAN_PRESUPUESTAL.MAN_AUX_TER IN (0) 
                                                                   AND   PLAN_PRESUPUESTAL.MAN_AUX_GEN IN (0) 
                                                                   AND   PLAN_PRESUPUESTAL.MAN_AUX_FUE IN (0) 
                                                                   AND   PLAN_PRESUPUESTAL.MAN_AUX_REF IN (0))
                                                                GROUP BY SALDO_PLAN_PPTAL.CODIGO, 
                                                                         SALDO_PLAN_PPTAL.COMPANIA, 
                                                                         SALDO_PLAN_PPTAL.ANO, 
                                                                         SALDO_PLAN_PPTAL.MES',
                                              UN_MERGEENLACE => '     TABLA.COMPANIA = VISTA.COMPANIA    
                                                                 AND  TABLA.ANO      = VISTA.ANO
                                                                 AND  TABLA.MES      = VISTA.MES
                                                                 AND  TABLA.CODIGO   = VISTA.CODIGO ',
                                              UN_MERGEEXISTE => 'UPDATE 
                                                                   SET  TABLA.ADICION                      = VISTA.ADICION,
                                                                        TABLA.REDUCCION                    = VISTA.REDUCCION,
                                                                        TABLA.PAC_PROGRAMADO               = VISTA.PAC_PROGRAMADO,
                                                                        TABLA.DISPONIBILIDAD               = VISTA.DISPONIBILIDAD,
                                                                        TABLA.DISPONIBILIDADADD            = VISTA.DISPONIBILIDADADD,
                                                                        TABLA.DISPONIBILIDADDMD            = VISTA.DISPONIBILIDADDMD,
                                                                        TABLA.REG_NO_CONTRACT              = VISTA.REG_NO_CONTRACT,
                                                                        TABLA.REG_CONTRACT                 = VISTA.REG_CONTRACT,
                                                                        TABLA.REG_REVERSION                = VISTA.REG_REVERSION,
                                                                        TABLA.MODIF_PAC_DEBITO             = VISTA.MODIF_PAC_DEBITO,
                                                                        TABLA.MODIF_PAC_CREDITO            = VISTA.MODIF_PAC_CREDITO,
                                                                        TABLA.MODIF_REG_CONT               = VISTA.MODIF_REG_CONT,
                                                                        TABLA.MODIF_REG_NOCONT             = VISTA.MODIF_REG_NOCONT,
                                                                        TABLA.MODIF_REG_CONTADR            = VISTA.MODIF_REG_CONTADR,
                                                                        TABLA.MODIF_REG_NOCONTADR          = VISTA.MODIF_REG_NOCONTADR,
                                                                        TABLA.MODIF_REG_CONTDMR            = VISTA.MODIF_REG_CONTDMR,
                                                                        TABLA.MODIF_REG_NOCONTDMR          = VISTA.MODIF_REG_NOCONTDMR,
                                                                        TABLA.REINTEGRO                    = VISTA.REINTEGRO,
                                                                        TABLA.VIGENCIAANTERIOR             = VISTA.VIGENCIAANTERIOR,
                                                                        TABLA.VIGENCIAFUTURA               = VISTA.VIGENCIAFUTURA,
                                                                        TABLA.TRASLADO_DEBITO              = VISTA.TRASLADO_DEBITO,
                                                                        TABLA.TRASLADO_CREDITO             = VISTA.TRASLADO_CREDITO,
                                                                        TABLA.APLAZAM_DEBITO               = VISTA.APLAZAM_DEBITO,
                                                                        TABLA.APLAZAM_CREDITO              = VISTA.APLAZAM_CREDITO,
                                                                        TABLA.APROPIACION_DEBITO           = VISTA.APROPIACION_DEBITO,
                                                                        TABLA.APROPIACION_CREDITO          = VISTA.APROPIACION_CREDITO,
                                                                        TABLA.EJE_CNT_DEBITO               = VISTA.EJE_CNT_DEBITO,
                                                                        TABLA.EJE_CNT_CREDITO              = VISTA.EJE_CNT_CREDITO,
                                                                        TABLA.EJE_PPT_DEBITO               = VISTA.EJE_PPT_DEBITO,
                                                                        TABLA.EJE_PPT_CREDITO              = VISTA.EJE_PPT_CREDITO,
                                                                        TABLA.EJE_PPT_DEBITOAEG            = VISTA.EJE_PPT_DEBITOAEG,
                                                                        TABLA.EJE_PPT_DEBITODEG            = VISTA.EJE_PPT_DEBITODEG,
                                                                        TABLA.REGISTRO_OBLIGACION          = VISTA.REGISTRO_OBLIGACION,
                                                                        TABLA.MODIF_REGISTRO_OBLIGACION    = VISTA.MODIF_REGISTRO_OBLIGACION,
                                                                        TABLA.MODIF_REGISTRO_OBLIGACIONARO = VISTA.MODIF_REGISTRO_OBLIGACIONARO,
                                                                        TABLA.MODIF_REGISTRO_OBLIGACIONDRO = VISTA.MODIF_REGISTRO_OBLIGACIONDRO,
                                                                        TABLA.INGRESOS_EFECTIVO            = VISTA.INGRESOS_EFECTIVO,
                                                                        TABLA.INGRESOS_PAPELES             = VISTA.INGRESOS_PAPELES,
                                                                        TABLA.MODIF_INGRESOS_EFECTIVO      = VISTA.MODIF_INGRESOS_EFECTIVO,
                                                                        TABLA.MODIF_INGRESOS_PAPELES       = VISTA.MODIF_INGRESOS_PAPELES,
                                                                        TABLA.INGRESOS_CAUSADOS            = VISTA.INGRESOS_CAUSADOS,
                                                                        TABLA.MODIF_INGRESOS_CAUSADOS      = VISTA.MODIF_INGRESOS_CAUSADOS,
                                                                        TABLA.MODIF_INGRESOS               = VISTA.MODIF_INGRESOS,
                                                                        TABLA.NETOEGRESO                   = VISTA.NETOEGRESO,
                                                                        TABLA.RECONOCIMIENTOS              = VISTA.RECONOCIMIENTOS,
                                                                        TABLA.PACTESORERIA                 = VISTA.PACTESORERIA,
                                                                        TABLA.PAC_EJECUTADO                = VISTA.PAC_EJECUTADO,
                                                                        TABLA.PAC_COMPROMETIDO             = VISTA.PAC_COMPROMETIDO ');                                             
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
      END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
        MI_REEMPLAZOS(0).CLAVE := 'COMPANIA';
        MI_REEMPLAZOS(0).VALOR := UN_COMPANIA;
        MI_REEMPLAZOS(1).CLAVE := 'ANIO';
        MI_REEMPLAZOS(1).VALOR := UN_ANO;
        MI_REEMPLAZOS(2).CLAVE := 'MESINICIAL';
        MI_REEMPLAZOS(2).VALOR := UN_MESINICIAL;
        MI_REEMPLAZOS(3).CLAVE := 'MESFINAL';
        MI_REEMPLAZOS(3).VALOR := UN_MESFINAL;

        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_MAYOR_SALDOPPTO,
          UN_TABLAERROR => 'SALDO_PLAN_PPTAL',
          UN_REEMPLAZOS => MI_REEMPLAZOS  
        );
    END;

    BEGIN 
      BEGIN 
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => 'SALDO_PLAN_PPTAL',
                                              UN_ACCION      => 'MM',
                                              UN_MERGEUSING  => 'SELECT COMPANIA,
                                                                        ANO,
                                                                        CODIGO                          
                                                                 FROM   PLAN_PRESUPUESTAL
                                                                 WHERE  COMPANIA = '''|| UN_COMPANIA ||'''
                                                                   AND  ANO      = '  || UN_ANO ||'
                                                                   AND  MAN_PAC  IN (0)',
                                              UN_MERGEENLACE => '    TABLA.COMPANIA = VISTA.COMPANIA
                                                                 AND TABLA.ANO      = VISTA.ANO 
                                                                 AND TABLA.CODIGO   = VISTA.CODIGO',
                                              UN_MERGEEXISTE => 'UPDATE 
                                                                   SET    TABLA.PAC_APROPIADO = 0
                                                                   WHERE  TABLA.COMPANIA = '''|| UN_COMPANIA||''' 
                                                                     AND  ANO            = '  || UN_ANO||'
                                                                     AND  TABLA.MES      BETWEEN '|| UN_MESINICIAL ||' AND '|| UN_MESFINAL); 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
        MI_REEMPLAZOS(0).CLAVE := 'COMPANIA';
        MI_REEMPLAZOS(0).VALOR := UN_COMPANIA;
        MI_REEMPLAZOS(1).CLAVE := 'ANIO';
        MI_REEMPLAZOS(1).VALOR := UN_ANO;
        MI_REEMPLAZOS(2).CLAVE := 'MESINICIAL';
        MI_REEMPLAZOS(2).VALOR := UN_MESINICIAL;
        MI_REEMPLAZOS(3).CLAVE := 'MESFINAL';
        MI_REEMPLAZOS(3).VALOR := UN_MESFINAL;

        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_MAYORIZA_PAC_CEROS,
          UN_TABLAERROR => 'SALDO_PLAN_PPTAL',
          UN_REEMPLAZOS => MI_REEMPLAZOS  
          );
    END;

    BEGIN 
      BEGIN 
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => 'SALDO_PLAN_PPTAL',
                                              UN_ACCION      => 'MM',
                                              UN_MERGEUSING  => 'SELECT SALDO_PLAN_PPTAL.COMPANIA,
                                                                        SALDO_PLAN_PPTAL.ANO,
                                                                        SALDO_PLAN_PPTAL.MES,
                                                                        SALDO_PLAN_PPTAL.CODIGO,
                                                                        SUM(PACAPROPIADO.PAC_APROPIADO) PAC_APROPIADO
                                                                 FROM   SALDO_PLAN_PPTAL
                                                                   INNER JOIN(
                                                                     SELECT SALDO_PLAN_PPTAL.COMPANIA,
                                                                            SALDO_PLAN_PPTAL.ANO,
                                                                            SALDO_PLAN_PPTAL.CODIGO,
                                                                            SALDO_PLAN_PPTAL.MES,
                                                                            PLAN_PRESUPUESTAL.MAN_PAC,
                                                                            SALDO_PLAN_PPTAL.PAC_APROPIADO
                                                                     FROM   SALDO_PLAN_PPTAL
                                                                       INNER JOIN PLAN_PRESUPUESTAL
                                                                         ON   SALDO_PLAN_PPTAL.COMPANIA = PLAN_PRESUPUESTAL.COMPANIA
                                                                         AND  SALDO_PLAN_PPTAL.ANO      = PLAN_PRESUPUESTAL.ANO
                                                                         AND  SALDO_PLAN_PPTAL.CODIGO   = PLAN_PRESUPUESTAL.CODIGO
                                                                     WHERE  SALDO_PLAN_PPTAL.COMPANIA = '''|| UN_COMPANIA||'''
                                                                       AND  SALDO_PLAN_PPTAL.ANO      = '  || UN_ANO||'
                                                                       AND  SALDO_PLAN_PPTAL.MES      BETWEEN '|| UN_MESINICIAL||' AND '|| UN_MESFINAL||'
                                                                       AND  PLAN_PRESUPUESTAL.MAN_PAC NOT IN (0)) PACAPROPIADO 
                                                                     ON  SALDO_PLAN_PPTAL.COMPANIA                                    = PACAPROPIADO.COMPANIA
                                                                     AND SALDO_PLAN_PPTAL.ANO                                         = PACAPROPIADO.ANO
                                                                     AND SALDO_PLAN_PPTAL.MES                                         = PACAPROPIADO.MES
                                                                     AND SUBSTR(PACAPROPIADO.CODIGO,0,LENGTH(SALDO_PLAN_PPTAL.CODIGO))= SALDO_PLAN_PPTAL.CODIGO
                                                                 GROUP BY SALDO_PLAN_PPTAL.COMPANIA,
                                                                        SALDO_PLAN_PPTAL.ANO,
                                                                        SALDO_PLAN_PPTAL.MES,
                                                                        SALDO_PLAN_PPTAL.CODIGO',
                                              UN_MERGEENLACE => '    TABLA.COMPANIA = VISTA.COMPANIA 
                                                                 AND TABLA.ANO      = VISTA.ANO
                                                                 AND TABLA.MES      = VISTA.MES
                                                                 AND TABLA.CODIGO = VISTA.CODIGO',
                                              UN_MERGEEXISTE => 'UPDATE 
                                                                   SET  TABLA.PAC_APROPIADO = VISTA.PAC_APROPIADO'); 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
      END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
        MI_REEMPLAZOS(0).CLAVE := 'COMPANIA';
        MI_REEMPLAZOS(0).VALOR := UN_COMPANIA;
        MI_REEMPLAZOS(1).CLAVE := 'ANIO';
        MI_REEMPLAZOS(1).VALOR := UN_ANO;
        MI_REEMPLAZOS(2).CLAVE := 'MESINICIAL';
        MI_REEMPLAZOS(2).VALOR := UN_MESINICIAL;
        MI_REEMPLAZOS(3).CLAVE := 'MESFINAL';
        MI_REEMPLAZOS(3).VALOR := UN_MESFINAL;

        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_MAYORIZA_PAC_APRO,
          UN_TABLAERROR => 'SALDO_PLAN_PPTAL',
          UN_REEMPLAZOS => MI_REEMPLAZOS  
        );
     END;
  END PR_MAYORIZARCUENTASHPTO;			 

  --6
FUNCTION FC_REVISARAFECTACIONESHR
  /*
    NAME              : FC_REVISARAFECTACIONESHR En Access --> RevisarAfectacionesHR
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ
    DATE MIGRADOR     : 04/07/2019
    TIME              : 08:00 AM
    SOURCE MODULE     : PRESUPUESTO
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : PERMITE VERIFICAR CUALES COMPROBANTES PRESUPUESTALES ESTAN MAL AFECTADOS Y AJUSTA
                        LOS AFECTADOS TANTO EL DETALLE COMO DEL HEADER, ADEMAS REVISA LA TABLA COMPROBANTE_PPTALAFECTADOS
                        PARA VALIDAR QUE ESTEN CORRECTAMENTE LAS AFECTACIONES                        
    PARAMETERS        : UN_COMPANIA   => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                        UN_ANO        => ANO POR EL QUE SE VA A FILTRAR EN LAS CONSULTAS
                        UN_USUARIO    => USUARIO QUE EJECUTA LA REVISON 
    @NAME:    revisarAfectacionesPresupuestales
    @METHOD:  GET  
  */
  (
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO             IN PCK_SUBTIPOS.TI_ANIO,
    UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO
  )
  RETURN NUMBER AS
    MI_CAMPOS          PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION       PCK_SUBTIPOS.TI_CONDICION;
    MI_VALORES         PCK_SUBTIPOS.TI_VALORES;
    MI_TABLA           PCK_SUBTIPOS.TI_TABLA;
    MI_RTA             PCK_SUBTIPOS.TI_RTA_ACME;
    MI_MSGERROR        PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN

    --SE ACTUALIZA EL AFECTADO DEL DETALLE COMPROBANTE PRESUPUESTAL

      MERGE INTO DETALLE_COMPROBANTE_PPTAL TABLA
      USING(
           SELECT MOV.COMPANIA,
                 MOV.ANO,
                 MOV.TIPO_CPTE,
                 MOV.COMPROBANTE, 
                 MOV.CONSECUTIVO,
                 CASE WHEN T.CLASE IN('DIS') 
                      THEN 1 
                      ELSE CASE WHEN T.CLASE IN('RES') 
                                THEN 2
                                ELSE CASE WHEN T.CLASE IN('REO') 
                                          THEN 3 
                                          ELSE CASE WHEN T.CLASE IN('EGR') THEN 4
                                                    ELSE 5    
                                                    END 
                                          END
                                END
                       END ORDEN,
                 NVL(SUM(DEBITOS),0) DEBITO_AFECTADO_CAL,
                 NVL(SUM(CREDITOS),0) CREDITO_AFECTADO_CAL,                 
                 NVL(SUM(MDEBITOS),0) MODIFICACION_DEBITO_CAL,
                 NVL(SUM(MCREDITOS),0) MODIFICACION_CREDITO_CAL  
        FROM DETALLE_COMPROBANTE_PPTAL MOV INNER JOIN TIPO_COMPROBPP  T  
          ON MOV.COMPANIA  = T.COMPANIA 
         AND MOV.TIPO_CPTE = T.CODIGO
        INNER JOIN CLASECNTPRES C 
          ON T.CLASE = C.CODIGO
            LEFT JOIN 
            (
                --IDENTIFICA LOS VALORES DE LAS AFECTACIONES DIRECTAS SEPARANDO DE ACUERDO A LA AFECTACION SI SON MODIFICACIONES
                SELECT X.COMPANIA,
                       X.ANO_AFECT,
                       X.TIPO_CPTE_AFECT,
                       X.CMPTE_AFECTADO, 
                       X.CONSECUTIVOPPTO,
                       SUM(CASE WHEN C.AFECTACION     IN('A','R') THEN 0 ELSE VALOR_DEBITO  END) DEBITOS,
                       SUM(CASE WHEN C.AFECTACION     IN('A','R') THEN 0 ELSE VALOR_CREDITO END) CREDITOS,
                       SUM(CASE WHEN C.AFECTACION NOT IN('A','R') THEN 0 ELSE VALOR_DEBITO  END) MDEBITOS,
                       SUM(CASE WHEN C.AFECTACION NOT IN('A','R') THEN 0 ELSE VALOR_CREDITO END) MCREDITOS       
                FROM DETALLE_COMPROBANTE_PPTAL X INNER JOIN TIPO_COMPROBPP  T 
                  ON X.COMPANIA  = T.COMPANIA 
                 AND X.TIPO_CPTE = T.CODIGO
                INNER JOIN CLASECNTPRES C 
                  ON T.CLASE = C.CODIGO
                WHERE X.COMPANIA        = UN_COMPANIA
                  AND X.ANO_AFECT       = UN_ANO
                  AND X.NATURALEZA      = 'D'  
                GROUP BY X.COMPANIA,
                         X.ANO_AFECT,
                         X.TIPO_CPTE_AFECT,
                         X.CMPTE_AFECTADO, 
                         X.CONSECUTIVOPPTO
            UNION ALL    
                -- IDENTIFICA LOS VALORES DE LAS AFECTACIONES INDIRECTAS(SEGUNDO GRADO) LAS CUALES AFECTAN DIRECTAMENTE EL DEBITO Y CREDITO
                SELECT NIVEL1.COMPANIA,
                       NIVEL1.ANO,
                       NIVEL1.TIPO_CPTE,
                       NIVEL1.COMPROBANTE, 
                       NIVEL1.CONSECUTIVO,
                       SUM(NIVEL3.VALOR_DEBITO)  DEBITOS,
                       SUM(NIVEL3.VALOR_CREDITO) CREDITOS,
                       0  MDEBITOS,
                       0  MCREDITOS
                FROM (
                    SELECT COMPANIA,
                           ANO ,
                           TIPO_CPTE ,
                           COMPROBANTE,
                           CONSECUTIVO, 
                           ANO_AFECT,
                           TIPO_CPTE_AFECT,
                           CMPTE_AFECTADO,
                           CONSECUTIVOPPTO,
                           NATURALEZA
                    FROM DETALLE_COMPROBANTE_PPTAL
                    WHERE COMPANIA        = UN_COMPANIA
                      AND ANO             = UN_ANO
                ) NIVEL1 
                INNER JOIN (SELECT COMPANIA,
                                   ANO,
                                   TIPO_CPTE ,
                                   COMPROBANTE,
                                   CONSECUTIVO, 
                                   ANO_AFECT,
                                   TIPO_CPTE_AFECT,
                                   CMPTE_AFECTADO,
                                   CONSECUTIVOPPTO
                            FROM DETALLE_COMPROBANTE_PPTAL
                            WHERE COMPANIA        = UN_COMPANIA
                              AND ANO_AFECT       = UN_ANO
                ) NIVEL2
                      ON NIVEL1.COMPANIA    = NIVEL2.COMPANIA
                     AND NIVEL1.ANO         = NIVEL2.ANO_AFECT
                     AND NIVEL1.TIPO_CPTE   = NIVEL2.TIPO_CPTE_AFECT
                     AND NIVEL1.COMPROBANTE = NIVEL2.CMPTE_AFECTADO
                     AND NIVEL1.CONSECUTIVO = NIVEL2.CONSECUTIVOPPTO 
                INNER JOIN (    
                            SELECT X.COMPANIA,
                                   X.ANO_AFECT,
                                   X.TIPO_CPTE_AFECT,
                                   X.CMPTE_AFECTADO,
                                   X.CONSECUTIVOPPTO,
                                   X.VALOR_DEBITO,
                                   X.VALOR_CREDITO,
                                   C.AFECTACION
                            FROM DETALLE_COMPROBANTE_PPTAL X INNER JOIN TIPO_COMPROBPP  T 
                              ON X.COMPANIA=T.COMPANIA 
                             AND X.TIPO_CPTE=T.CODIGO
                            INNER JOIN CLASECNTPRES C 
                              ON T.CLASE=C.CODIGO
                            WHERE X.COMPANIA        = UN_COMPANIA
                              AND X.ANO_AFECT       = UN_ANO
                              AND C.AFECTACION      IN('A','R')  
                              AND X.TIPO_CPTE_AFECT IS NOT NULL 
                              AND X.CMPTE_AFECTADO  IS NOT NULL
                ) NIVEL3
                      ON NIVEL2.COMPANIA    = NIVEL3.COMPANIA
                     AND NIVEL2.TIPO_CPTE   = NIVEL3.TIPO_CPTE_AFECT
                     AND NIVEL2.COMPROBANTE = NIVEL3.CMPTE_AFECTADO
                     AND NIVEL2.CONSECUTIVO = NIVEL3.CONSECUTIVOPPTO 
                WHERE NIVEL1.COMPANIA    = UN_COMPANIA
                  AND NIVEL1.ANO         = UN_ANO
                GROUP BY NIVEL1.COMPANIA,
                         NIVEL1.ANO,
                         NIVEL1.TIPO_CPTE,
                         NIVEL1.COMPROBANTE, 
                         NIVEL1.CONSECUTIVO,
                         0
            ) AFECTA
              ON MOV.COMPANIA    = AFECTA.COMPANIA
             AND MOV.ANO         = AFECTA.ANO_AFECT
             AND MOV.TIPO_CPTE   = AFECTA.TIPO_CPTE_AFECT
             AND MOV.COMPROBANTE = AFECTA.CMPTE_AFECTADO
             AND MOV.CONSECUTIVO = AFECTA.CONSECUTIVOPPTO 
        WHERE MOV.COMPANIA    = UN_COMPANIA
          AND MOV.ANO         = UN_ANO
          AND MOV.NATURALEZA = 'D'     
          AND T.CLASE IN('DIS', 'RES', 'REO', 'EGR')
        GROUP BY MOV.COMPANIA,
                 MOV.ANO,
                 MOV.TIPO_CPTE,
                 MOV.COMPROBANTE, 
                 MOV.CONSECUTIVO,
                 MOV.DEBITO_AFECTADO,
                 MOV.CREDITO_AFECTADO,
                 MOV.MODIFICACION_DEBITO,
                 MOV.MODIFICACION_CREDITO,
                 CASE WHEN T.CLASE IN('DIS') 
                      THEN 1 
                      ELSE CASE WHEN T.CLASE IN('RES') 
                                THEN 2
                                ELSE CASE WHEN T.CLASE IN('REO') 
                                          THEN 3 
                                          ELSE CASE WHEN T.CLASE IN('EGR') THEN 4
                                                    ELSE 5    
                                                    END 
                                          END
                                END
                       END
        HAVING MOV.DEBITO_AFECTADO      <> NVL(SUM(DEBITOS),0)
            OR MOV.CREDITO_AFECTADO     <> NVL(SUM(CREDITOS),0)
            OR MOV.MODIFICACION_DEBITO  <> NVL(SUM(MDEBITOS),0)
            OR MOV.MODIFICACION_CREDITO <> NVL(SUM(MCREDITOS),0)
        ORDER BY CASE WHEN T.CLASE IN('DIS') 
                      THEN 1 
                      ELSE CASE WHEN T.CLASE IN('RES') 
                                THEN 2
                                ELSE CASE WHEN T.CLASE IN('REO') 
                                          THEN 3 
                                          ELSE CASE WHEN T.CLASE IN('EGR') THEN 4
                                                    ELSE 5    
                                                    END 
                                          END
                                END
                       END
        ) VISTA
        ON (TABLA.COMPANIA    = VISTA.COMPANIA
       AND TABLA.ANO         = VISTA.ANO
       AND TABLA.TIPO_CPTE   = VISTA.TIPO_CPTE
       AND TABLA.COMPROBANTE = VISTA.COMPROBANTE
       AND TABLA.CONSECUTIVO = VISTA.CONSECUTIVO)
       
       WHEN MATCHED THEN 
       UPDATE SET   TABLA.DEBITO_AFECTADO      =  VISTA.DEBITO_AFECTADO_CAL,
            TABLA.CREDITO_AFECTADO     =  VISTA.CREDITO_AFECTADO_CAL,
            TABLA.MODIFICACION_DEBITO  =  VISTA.MODIFICACION_DEBITO_CAL,
            TABLA.MODIFICACION_CREDITO =  VISTA.MODIFICACION_CREDITO_CAL ;
    --ACTUALIZA EL HEADER EN BASE A LOS DETALLES
    MERGE INTO COMPROBANTE_PPTAL TABLA
    USING (
    SELECT COM.COMPANIA,
                       COM.ANO,
                       COM.TIPO,
                       COM.NUMERO,
                       COM.DEBITO_AFECTADO, 
                       COM.CREDITO_AFECTADO, 
                       COM.MODIFICACION_DEBITO, 
                       COM.MODIFICACION_CREDITO,
                       SUM(NVL(DET.DEBITO_AFECTADO,0))      DEBITO_AFECTADO_CAL, 
                       SUM(NVL(DET.CREDITO_AFECTADO,0))     CREDITO_AFECTADO_CAL,  
                       SUM(NVL(DET.MODIFICACION_DEBITO,0))  MODIFICACION_DEBITO_CAL,  
                       SUM(NVL(DET.MODIFICACION_CREDITO,0)) MODIFICACION_CREDITO_CAL
                FROM COMPROBANTE_PPTAL COM LEFT JOIN DETALLE_COMPROBANTE_PPTAL DET
                  ON COM.COMPANIA  = DET.COMPANIA
                 AND COM.ANO       = DET.ANO
                 AND COM.TIPO      = DET.TIPO_CPTE
                 AND COM.NUMERO    = DET.COMPROBANTE
                WHERE COM.COMPANIA    = UN_COMPANIA
                  AND COM.ANO         = UN_ANO
                  AND DET.NATURALEZA = 'D'      
                GROUP BY COM.COMPANIA,
                         COM.ANO,
                         COM.TIPO,
                         COM.NUMERO,
                         COM.DEBITO_AFECTADO, 
                         COM.CREDITO_AFECTADO, 
                         COM.MODIFICACION_DEBITO, 
                         COM.MODIFICACION_CREDITO
                HAVING  COM.DEBITO_AFECTADO      <> SUM(NVL(DET.DEBITO_AFECTADO,0))
                     OR COM.CREDITO_AFECTADO     <> SUM(NVL(DET.CREDITO_AFECTADO,0))
                     OR COM.MODIFICACION_DEBITO  <> SUM(NVL(DET.MODIFICACION_DEBITO,0))
                     OR COM.MODIFICACION_CREDITO <> SUM(NVL(DET.MODIFICACION_CREDITO,0))
    ) VISTA
        ON (TABLA.COMPANIA  = VISTA.COMPANIA
       AND TABLA.ANO        = VISTA.ANO
       AND TABLA.TIPO   = VISTA.TIPO
       AND TABLA.NUMERO = VISTA.NUMERO
       )
       
       WHEN MATCHED THEN 
       UPDATE SET   TABLA.DEBITO_AFECTADO      =  VISTA.DEBITO_AFECTADO_CAL,
            TABLA.CREDITO_AFECTADO     =  VISTA.CREDITO_AFECTADO_CAL,
            TABLA.MODIFICACION_DEBITO  =  VISTA.MODIFICACION_DEBITO_CAL,
            TABLA.MODIFICACION_CREDITO =  VISTA.MODIFICACION_CREDITO_CAL ;
    
    

    --ELIMINAR LOS REGISTROS DE COMPROBANTE_PPTALAFECTADOS  QUE NO ESTAN AFECTADOS EN EL DETALLE_COMPROBANTE_PPTAL
    BEGIN
        BEGIN        
            DELETE COMPROBANTE_PPTALAFECTADOS
            WHERE (COMPANIA, ANO, TIPO_CPTE, COMPROBANTE, ANO_AFECT, TIPO_CPTE_AFECT, COMPROBANTE_AFECT)
                         IN(SELECT AFEC.COMPANIA, AFEC.ANO, AFEC.TIPO_CPTE, AFEC.COMPROBANTE, AFEC.ANO_AFECT, 
                                   AFEC.TIPO_CPTE_AFECT, AFEC.COMPROBANTE_AFECT
                            FROM COMPROBANTE_PPTALAFECTADOS AFEC LEFT JOIN
                                (   SELECT DISTINCT COMPANIA,
                                           ANO,
                                           TIPO_CPTE,
                                           COMPROBANTE,
                                           CONSECUTIVO,
                                           ANO_AFECT,
                                           TIPO_CPTE_AFECT,
                                           CMPTE_AFECTADO 
                                    FROM DETALLE_COMPROBANTE_PPTAL
                                    WHERE COMPANIA =  UN_COMPANIA
                                      AND ANO      =  UN_ANO      
                                ) DET
                                ON AFEC.COMPANIA         = DET.COMPANIA
                               AND AFEC.ANO              = DET.ANO
                               AND AFEC.TIPO_CPTE        = DET.TIPO_CPTE
                               AND AFEC.COMPROBANTE      = DET.COMPROBANTE
                               AND AFEC.ANO_AFECT        = DET.ANO_AFECT
                               AND AFEC.TIPO_CPTE_AFECT  = DET.TIPO_CPTE_AFECT
                               AND AFEC.COMPROBANTE_AFECT= DET.CMPTE_AFECTADO
                            WHERE AFEC.COMPANIA =  UN_COMPANIA
                              AND AFEC.ANO      =  UN_ANO 
                              AND DET.COMPANIA IS NULL
                              );
    
                     
        EXCEPTION WHEN OTHERS THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;   
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN     
        MI_MSGERROR(1).CLAVE := 'TIPO';
        MI_MSGERROR(1).VALOR := 'eliminar sobrantes';
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD => PCK_ERRORES.ERR_PPTOAFECTACIONES
                                ,UN_REEMPLAZOS  => MI_MSGERROR  );
    END;


    --INSERTAR LOS REGISTROS A COMPROBANTE_PPTALAFECTADOS QUE ESTAN AFECTADOS EN EL DETALLE_COMPROBANTE_PPTAL 
    -- Y NO ESTA EL REGISTRO COMPROBANTE_PPTALAFECTADOS
    BEGIN 
        BEGIN
                INSERT INTO COMPROBANTE_PPTALAFECTADOS(COMPANIA,
                                                         ANO,
                                                         TIPO_CPTE,
                                                         COMPROBANTE,
                                                         ANO_AFECT,
                                                         TIPO_CPTE_AFECT,
                                                         COMPROBANTE_AFECT,
                                                         CREATED_BY,
                                                         DATE_CREATED)
                SELECT DET.COMPANIA, DET.ANO, DET.TIPO_CPTE, DET.COMPROBANTE, DET.ANO_AFECT, 
                       DET.TIPO_CPTE_AFECT, DET.CMPTE_AFECTADO COMPROBANTE_AFECT,
                        UN_USUARIO, SYSDATE
                FROM (SELECT DISTINCT COMPANIA,
                               ANO,
                               TIPO_CPTE,
                               COMPROBANTE,
                               ANO_AFECT,
                               TIPO_CPTE_AFECT,
                               CMPTE_AFECTADO 
                        FROM DETALLE_COMPROBANTE_PPTAL
                        WHERE COMPANIA =  UN_COMPANIA 
                          AND ANO      =  UN_ANO      
                          AND ANO_AFECT       IS NOT NULL
                          AND TIPO_CPTE_AFECT IS NOT NULL
                          AND CMPTE_AFECTADO  IS NOT NULL
                    ) DET
                   LEFT JOIN COMPROBANTE_PPTALAFECTADOS AFEC  
                    ON AFEC.COMPANIA         = DET.COMPANIA
                   AND AFEC.ANO              = DET.ANO
                   AND AFEC.TIPO_CPTE        = DET.TIPO_CPTE
                   AND AFEC.COMPROBANTE      = DET.COMPROBANTE
                   AND AFEC.ANO_AFECT        = DET.ANO_AFECT
                   AND AFEC.TIPO_CPTE_AFECT  = DET.TIPO_CPTE_AFECT
                   AND AFEC.COMPROBANTE_AFECT= DET.CMPTE_AFECTADO
                WHERE DET.COMPANIA = UN_COMPANIA 
                  AND DET.ANO      =  UN_ANO 
                  AND AFEC.COMPANIA IS NULL ; 
    
            
        EXCEPTION WHEN OTHERS THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN    
        MI_MSGERROR(1).CLAVE := 'TIPO';
        MI_MSGERROR(1).VALOR := 'insertar faltantes';
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD => PCK_ERRORES.ERR_PPTOAFECTACIONES
                                ,UN_REEMPLAZOS  => MI_MSGERROR  );
    END;

    BEGIN
        BEGIN 
             --ACTUALIZA EL VLR_DOCUMENTO Y VLOR_DEBITO DEL HEADER
            MERGE INTO COMPROBANTE_PPTAL TABLA
            USING (
                    SELECT COMPROBANTE_PPTAL.COMPANIA,
                           COMPROBANTE_PPTAL.ANO,
                           COMPROBANTE_PPTAL.TIPO,
                           COMPROBANTE_PPTAL.NUMERO,
                           COMPROBANTE_PPTAL.VLR_DOCUMENTO, 
                           COMPROBANTE_PPTAL.DEBITO,
                           SUM(NVL(DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO,0)) DEBITO_DET
                    FROM COMPROBANTE_PPTAL INNER JOIN TIPO_COMPROBPP  T 
                      ON COMPROBANTE_PPTAL.COMPANIA =T.COMPANIA 
                     AND COMPROBANTE_PPTAL.TIPO     =T.CODIGO
                    INNER JOIN CLASECNTPRES C 
                          ON T.CLASE=C.CODIGO
                     LEFT JOIN DETALLE_COMPROBANTE_PPTAL
                      ON COMPROBANTE_PPTAL.COMPANIA =DETALLE_COMPROBANTE_PPTAL.COMPANIA 
                     AND COMPROBANTE_PPTAL.ANO      =DETALLE_COMPROBANTE_PPTAL.ANO
                     AND COMPROBANTE_PPTAL.TIPO     =DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE
                     AND COMPROBANTE_PPTAL.NUMERO   =DETALLE_COMPROBANTE_PPTAL.COMPROBANTE
                    WHERE COMPROBANTE_PPTAL.COMPANIA = UN_COMPANIA
                      AND COMPROBANTE_PPTAL.ANO      = UN_ANO   
                      AND C.AFECTACION   NOT IN('A','R') 
                      AND C.CODIGO IN('DIS','RES','REO','EGR')
                    GROUP BY COMPROBANTE_PPTAL.COMPANIA,
                           COMPROBANTE_PPTAL.ANO,
                           COMPROBANTE_PPTAL.TIPO,
                           COMPROBANTE_PPTAL.NUMERO,
                           COMPROBANTE_PPTAL.VLR_DOCUMENTO, 
                           COMPROBANTE_PPTAL.DEBITO
                    HAVING COMPROBANTE_PPTAL.VLR_DOCUMENTO <> SUM(NVL(DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO,0)) 
                        OR COMPROBANTE_PPTAL.DEBITO        <> SUM(NVL(DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO,0))
                ) VISTA
                ON (TABLA.COMPANIA    = VISTA.COMPANIA
                AND TABLA.ANO         = VISTA.ANO
                AND TABLA.TIPO        = VISTA.TIPO  
                AND TABLA.NUMERO      = VISTA.NUMERO)
                WHEN MATCHED THEN
                UPDATE SET TABLA.VLR_DOCUMENTO = VISTA.DEBITO_DET,
                           TABLA.DEBITO        = VISTA.DEBITO_DET;
        
            EXCEPTION WHEN OTHERS THEN 
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
            MI_MSGERROR(1).CLAVE := 'TIPO';
            MI_MSGERROR(1).VALOR := 'comprobante(Valor Documento)';
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD => PCK_ERRORES.ERR_PPTOAFECTACIONES
                                    ,UN_REEMPLAZOS  => MI_MSGERROR  );
        END; 
    

    RETURN -1;
  END FC_REVISARAFECTACIONESHR;
  
  
  -- 8
  FUNCTION FC_PREDECESORPPTAL 
  /*
    MODIFIER          : YEISSON ALEJANDRO ROJAS RUIZ
    DATE MODIFIED     : 12/01/2017
    TIME              : 10:00 AM
    MODIFICATIONS     : CORRECCIÓN SEGÚN ESTÁNDAR DE PROGRAMACIÓN Y OPTIMIZACIÓN DE MANEJO DE ERRORES.
    DESCRIPTION       : Retorna 0 si no encuentra problemas de afectación y en caso contrario genera informe y actualiza la información a comprobantes.
    PARAMETERS        : UN_COMPANIA   => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                        UN_ANIO       => AÑO POR EL QUE SE VA A FILTRAR EN LAS CONSULTAS.
                        UN_CODIGO     => CÓDIGO PRESUPUESTAL.

    @NAME:    consultarPredecesorPresupuestal
    @METHOD:  GET
  */
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO         IN PCK_SUBTIPOS.TI_ANIO, 
    UN_CODIGO       IN PCK_SUBTIPOS.TI_CODIGOPPTAL
  )
  RETURN VARCHAR2 AS
    MI_PREDECESOR   VARCHAR2(3200 CHAR); -- VARCHAR2 DEBIDO A QUE NO HAY UN SUBTIPO EN PCK_SUBTIPOS QUE SE AJUSTE A ESTA VARIABLE.

  BEGIN
    BEGIN
      IF LENGTH(UN_CODIGO) = 1 THEN
        MI_PREDECESOR := UN_CODIGO;

      ELSE
        SELECT  MAX(V_PLAN_PRESUPUESTAL.ID) 
                  KEEP (DENSE_RANK LAST ORDER BY ROWNUM) AS PRED
        INTO    MI_PREDECESOR
        FROM    V_PLAN_PRESUPUESTAL
        WHERE   V_PLAN_PRESUPUESTAL.COMPANIA   = UN_COMPANIA
          AND   V_PLAN_PRESUPUESTAL.ANO        = UN_ANIO
          AND   LENGTH(V_PLAN_PRESUPUESTAL.ID) < LENGTH(UN_CODIGO)
          AND   V_PLAN_PRESUPUESTAL.ID         = SUBSTR(UN_CODIGO,1,LENGTH(V_PLAN_PRESUPUESTAL.ID));
      END IF;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
    END;
    RETURN MI_PREDECESOR;        

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD   => SQLCODE,
        UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_PREDECESORPPTAL
      );
  END FC_PREDECESORPPTAL;

  -- 9
  FUNCTION FC_CONCATENACOMPROBANTESPPTO 
  /*
    NAME              : FC_CONCATENACOMPROBANTESPPTO 
    AUTHORS           : SYSMAN  SAS
    AUTHOR            : ANDREA PINEDA OVALLE
    DATE              : 19/07/2016
    TIME              : 08:45 AM
    MODIFIER          : YEISSON ALEJANDRO ROJAS RUIZ
    DATE MODIFIED     : 12/01/2017
    TIME              : 10:00 AM
    MODIFICATIONS     : CORRECCIÓN SEGÚN ESTÁNDAR DE PROGRAMACIÓN.
    DESCRIPTION       : Funcion que retorna la cadena concatenada de los valores no existentes en los 
                        consecutivos de presupuesto. 
    PARAMETERS        : UN_TIPO         => TIPO POR EL CUAL FILTRA EN LA CONSULTA.
                        UN_COMPANIA     => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                        UN_FECHAINICIAL => FECHA INICIAL POR EL CUAL FILTRA EN LA CONSULTA.
                        UN_FECHAINICIAL => FECHA FINAL POR EL CUAL FILTRA EN LA CONSULTA.

    @NAME:    consultarConsecutivosPresupuestalesFaltantes
    @METHOD:  GET                      
  */
  (
    UN_TIPO                 IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL, 
    UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHAINICIAL         IN DATE,
    UN_FECHAFINAL           IN DATE 
  ) 
  RETURN CLOB AS 
    MI_RESULTADO            CLOB;
  BEGIN
    BEGIN 
      SELECT  LISTAGG(FALTAN.FALTANTES ,',') 
                WITHIN GROUP(ORDER BY FALTAN.FALTANTES) NOEXISTENTES 
      INTO    MI_RESULTADO
      FROM    (SELECT FALTANTES, 
                      COMPANIA, 
                      TIPO
               FROM   (SELECT DISTINCT CASE WHEN TO_NUMBER(LAG(Numero) 
                                         OVER (ORDER BY Numero)) NOT IN (0)
                                            THEN CASE 
                                                 WHEN (TO_NUMBER(LAG(Numero) 
                                              OVER (ORDER BY Numero))+1) <> TO_NUMBER(NUMERO)
                                                 THEN CASE WHEN (TO_NUMBER(LAG(Numero) 
                                                       OVER (ORDER BY Numero))+1) <> (TO_NUMBER(NUMERO) -1)
                                                           THEN (TO_NUMBER(LAG(Numero) OVER (ORDER BY Numero))+1) ||' - '|| (TO_NUMBER(NUMERO) -1) ||''
                                                           ELSE (TO_NUMBER(LAG(Numero) 
                                                             OVER (ORDER BY Numero))+1) ||''
                                                      END 
                                                 END
                                       END  FALTANTES,
                              COMPANIA, 
                              TIPO
                       FROM   COMPROBANTE_PPTAL CN2
                       WHERE  CN2.COMPANIA = UN_COMPANIA
                         AND  CN2.TIPO     = UN_TIPO
                         AND  CN2.FECHA    BETWEEN UN_FECHAINICIAL AND UN_FECHAFINAL
               ORDER BY FALTANTES) RESUMEN  
      WHERE   ROWNUM <= 180
      ORDER BY FALTANTES) FALTAN;

      EXCEPTION WHEN NO_DATA_FOUND THEN 
      MI_RESULTADO := '';
    END ;

    RETURN MI_RESULTADO;

  END FC_CONCATENACOMPROBANTESPPTO;

  -- 10
  FUNCTION FC_CONTABILIZARAPROPINICIAL 
  /*
    NAME              : FC_CONTABILIZARAPROPINICIAL -- ContabilizarApropiacionInicial en Access
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : LEYDI MILENA CORTÉS FORERO
    DATE MIGRADOR     : 26/07/2016
    TIME              : 05:30 PM
    MODIFIER          : LEYDI MILENA CORTÉS FORERO \ YEISSON ALEJANDRO ROJAS RUIZ
    DATE MODIFIED     : 03/08/2016 \ 13/01/2017
    TIME              : 09:00 AM
    MODIFICATIONS     : SE INCLUYE CONSULTA PARA VERIFICAR QUE LAS CUENTAS QUE ESTÁN EN LA TABLA APROPIACIONESINICIALES Y NO ESTÁN CONFIGURADAS EN LA TABLA
                        PLAN_PRESUPUESTAL, DE LO CONTRARIO GENERA UN ARCHIVO TXT QUE INCLUYE LAS CUENTAS QUE SE DEBEN REVISAR.\ CORRECCIÓN SEGÚN ESTÁNDAR 
                        DE PROGRAMACIÓN Y OPTIMIZACIÓN DE MANEJO DE ERRORES.
    DESCRIPTION       : PERMITE REALIZAR LA ACTUALIZACIÓN DE LOS CAMPOS APROPIACIONINICIAL DE LAS TABLAS PLAN_PRESUPUESTAL Y SALDO_AUX_PPTAL, 
                        LOS CAMPOS APROPIACION_DEBITO Y APROPIACIONINICIAL  DE LAS TABLAS SALDO_PLAN_PPTAL  Y SALDO_AUX_PPTAL  Y EL CAMPO CONTABILIZADO 
                        EN LA TABLA APROPIACIONESINICIALES. HACE USO DEL PROCEDIMIENTO PCK_PRESUPUESTO1.PR_ACTPPTO.
    PARAMETERS        : UN_COMPANIA => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                        UN_ANIO     => AÑO QUE SIRVE PARA LA CONDICIÓN DE LOS UPDATES.

    @NAME:    contabilizarApropiacionesIniciales
    @METHOD:  GET 
  */
  (
    UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO          IN PCK_SUBTIPOS.TI_ANIO
  )
  RETURN CLOB AS
    MI_PCKDATOS      PCK_SUBTIPOS.TI_RTA_ACME;
    MI_CAMPOS        PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES       PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION     PCK_SUBTIPOS.TI_CONDICION;
    MI_DEBITO        PCK_SUBTIPOS.TI_DOBLE;
    MI_CREDITO       PCK_SUBTIPOS.TI_DOBLE;
    MI_CLASE         TIPO_COMPROBPP.CLASE%TYPE;
    MI_FECHA         DATE;
    MI_RTA           CLOB;

  BEGIN   
   -- Verifica las cuentas que están en la tabla APROPIACIONESINICIALES y no están configurdas en la tabla PLAN_PRESUPUESTAL
   <<VERIFICACUENTA>>
    FOR RS_VERIF_CUENTAS IN (
      SELECT  CODIGO 
      FROM    APROPIACIONESINICIALES
      WHERE   COMPANIA = UN_COMPANIA
        AND   ANO      = UN_ANIO
        AND   CODIGO   NOT IN ( SELECT  CODIGO
                                FROM    PLAN_PRESUPUESTAL
                                WHERE   COMPANIA = UN_COMPANIA
                                  AND   ANO      = UN_ANIO)
      GROUP BY CODIGO
      ORDER BY CODIGO)
    LOOP
      MI_RTA := MI_RTA || 'Cuenta: ' || RS_VERIF_CUENTAS.CODIGO || CHR(13);              
    END LOOP VERIFICACUENTA;
    -- Verifica las cuentas que no tienen configurados indicadores en la tabla PLAN_PRESUPUESTAL y están como APROPIACIONESINICIALES     
		<<CUENTASININDICADORES>>
    FOR RS_VERIF_INDICADORES IN (
      SELECT  DISTINCT CODIGO 
      FROM    APROPIACIONESINICIALES
      WHERE   COMPANIA = UN_COMPANIA
        AND   ANO      = UN_ANIO
        AND   CODIGO  IN (SELECT  CODIGO
                          FROM    PLAN_PRESUPUESTAL
                          WHERE   COMPANIA        = UN_COMPANIA
                            AND   ANO             = UN_ANIO
                            AND   (MAN_CEN_CTO 
                                    + MAN_AUX_TER 
                                    + MAN_AUX_GEN 
                                    + MAN_AUX_FUE 
                                    + MAN_AUX_REF 
                                    + MOVIMIENTO) = 0)
    )

    LOOP
      MI_RTA := MI_RTA || 'Cuenta: ' || RS_VERIF_INDICADORES.CODIGO || CHR(13);              
    END LOOP CUENTASININDICADORES;    
    -- Si encuentra cuentas sin configurar, retorna las cuentas y termina el proceso.
    IF MI_RTA IS NOT NULL THEN
      MI_RTA := 'No es posible contabilizar la apropiación inicial. '
                || CHR(13) || CHR(13)
                || 'Las siguientes cuentas no tienen movimiento ni auxiliar y están configuradas como apropiación inicial para el año '
                || UN_ANIO || '.'
                || CHR(13) || CHR(13)
                || MI_RTA;
      RETURN MI_RTA;
    ELSE
      PR_LIMPIA_SALDO_AUX_PPTAL(UN_COMPANIA      => UN_COMPANIA,
                                UN_ANIO          => UN_ANIO,
                                UN_CODIGOINICIAL => '0',
                                UN_CODIGOFINAL   => PCK_DATOS.FC_CONS_MAX_ID);

      MI_CONDICION := '    COMPANIA = ''' || UN_COMPANIA || ''' 
                       AND ANO      = '   || UN_ANIO;

      BEGIN
        BEGIN
          MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => 'PLAN_PRESUPUESTAL',
                                            UN_ACCION    => 'M',
                                            UN_CAMPOS    => ' APROPIACIONINICIAL = 0', 
                                            UN_CONDICION => MI_CONDICION);

          MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA     => 'SALDO_AUX_PPTAL',
                                           UN_ACCION    => 'M',
                                           UN_CAMPOS    => ' APROPIACIONINICIAL=0, APROPIACION_DEBITO = 0, APROPIACION_CREDITO=0', 
                                           UN_CONDICION => MI_CONDICION);

          MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA     => 'SALDO_PLAN_PPTAL',
                                           UN_ACCION    => 'M',
                                           UN_CAMPOS    => ' APROPIACION_DEBITO = 0, APROPIACION_CREDITO=0', 
                                           UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD => SQLCODE,
            UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_CONT_APROP_ACTL
          );      
      END;

BEGIN 
    BEGIN
      SELECT  CLASE 
      INTO    MI_CLASE
      FROM    TIPO_COMPROBPP
      WHERE   COMPANIA = UN_COMPANIA
        AND   CODIGO   = 'APR';
        
      EXCEPTION WHEN NO_DATA_FOUND THEN 
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
      END;  
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD => SQLCODE,
            UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_NO_COMP_APR
          ); 
END;    

      <<DATOS_APROPIACIONES>>    
      FOR RS_APROPIACIONES IN (
        SELECT  APROPIACIONESINICIALES.COMPANIA, 
                APROPIACIONESINICIALES.ANO,
                APROPIACIONESINICIALES.CODIGO, 
                PLAN_PRESUPUESTAL.NATURALEZA,
                CASE WHEN PLAN_PRESUPUESTAL.MAN_CEN_CTO  IN(0) 
                     THEN PCK_DATOS.FC_CONS_CENTRO     
                     ELSE APROPIACIONESINICIALES.CENTRO_COSTO 
                     END CENTRO_COSTO, 
                CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_TER  IN(0) 
                     THEN PCK_DATOS.FC_CONS_TERCERO    
                     ELSE APROPIACIONESINICIALES.TERCERO      
                     END TERCERO, 
                CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_TER  IN(0) 
                     THEN PCK_DATOS.FC_CONS_SUCURSAL   
                     ELSE APROPIACIONESINICIALES.SUCURSAL     
                     END SUCURSAL, 
                CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_GEN  IN(0) 
                     THEN PCK_DATOS.FC_CONS_AUXILIAR   
                     ELSE APROPIACIONESINICIALES.AUXILIAR     
                     END AUXILIAR, 
                CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_FUE  IN(0) 
                     THEN PCK_DATOS.FC_CONS_FUENTE     
                     ELSE APROPIACIONESINICIALES.FUENTE_RECURSO 
                     END FUENTE_RECURSO, 
                CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_REF  IN(0) 
                     THEN PCK_DATOS.FC_CONS_REFERENCIA 
                     ELSE APROPIACIONESINICIALES.REFERENCIA   
                     END REFERENCIA, 
                SUM(APROPIACIONESINICIALES.DEBITO)               DEBITO, 
                SUM(APROPIACIONESINICIALES.CREDITO)              CREDITO                                      
        FROM    APROPIACIONESINICIALES 
          INNER JOIN PLAN_PRESUPUESTAL 
            ON  APROPIACIONESINICIALES.COMPANIA = PLAN_PRESUPUESTAL.COMPANIA 
            AND APROPIACIONESINICIALES.ANO      = PLAN_PRESUPUESTAL.ANO 
            AND APROPIACIONESINICIALES.CODIGO   = PLAN_PRESUPUESTAL.CODIGO
        WHERE   APROPIACIONESINICIALES.COMPANIA = UN_COMPANIA  
          AND   APROPIACIONESINICIALES.ANO      = UN_ANIO
        -- Ticket#7725385: Se comentan las lineas para que el sistema contabilice y cree las apropiaciones para los rubros que esten en 0
        --  AND   (APROPIACIONESINICIALES.DEBITO  <>0  
        --  OR    APROPIACIONESINICIALES.CREDITO<>0)
        GROUP BY APROPIACIONESINICIALES.COMPANIA, 
                APROPIACIONESINICIALES.ANO,
                APROPIACIONESINICIALES.CODIGO, 
                PLAN_PRESUPUESTAL.NATURALEZA,
                CASE WHEN PLAN_PRESUPUESTAL.MAN_CEN_CTO  IN(0) 
                     THEN PCK_DATOS.FC_CONS_CENTRO     
                     ELSE APROPIACIONESINICIALES.CENTRO_COSTO 
                     END, 
                CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_TER  IN(0) 
                     THEN PCK_DATOS.FC_CONS_TERCERO    
                     ELSE APROPIACIONESINICIALES.TERCERO      
                     END, 
                CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_TER  IN(0) 
                     THEN PCK_DATOS.FC_CONS_SUCURSAL   
                     ELSE APROPIACIONESINICIALES.SUCURSAL     
                     END, 
                CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_GEN  IN(0) 
                     THEN PCK_DATOS.FC_CONS_AUXILIAR   
                     ELSE APROPIACIONESINICIALES.AUXILIAR     
                     END, 
                CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_FUE  IN(0) 
                     THEN PCK_DATOS.FC_CONS_FUENTE     
                     ELSE APROPIACIONESINICIALES.FUENTE_RECURSO 
                     END, 
                CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_REF  IN(0) 
                     THEN PCK_DATOS.FC_CONS_REFERENCIA 
                     ELSE APROPIACIONESINICIALES.REFERENCIA   
                     END)                        
      LOOP                        
        PCK_PRESUPUESTO1.PR_ACTPPTO0AUX(UN_CLASE         => MI_CLASE, 
                                        UN_COMPANIA      => UN_COMPANIA, 
                                        UN_ANIO          => UN_ANIO,
                                        UN_CODIGO        => RS_APROPIACIONES.CODIGO,
                                        UN_TERCERO       => RS_APROPIACIONES.TERCERO,
                                        UN_SUCURSAL      => RS_APROPIACIONES.SUCURSAL,
                                        UN_AUXILIAR      => RS_APROPIACIONES.AUXILIAR,
                                        UN_CENTRO        => RS_APROPIACIONES.CENTRO_COSTO,
                                        UN_REFERENCIA    => RS_APROPIACIONES.REFERENCIA,
                                        UN_FUENTERECURSO => RS_APROPIACIONES.FUENTE_RECURSO,
                                        UN_MES           => 0,
                                        UN_DEBITO        => RS_APROPIACIONES.DEBITO,
                                        UN_CREDITO       => RS_APROPIACIONES.CREDITO,
                                        UN_DEBITO_ANT    => 0,
                                        UN_NATURALEZA    => RS_APROPIACIONES.NATURALEZA,
                                        UN_CREDITO_ANT   => 0,
                                        UN_DIFERENCIA    => 0,
                                        UN_DIFERENCIAANT => 0 ); 

      END LOOP DATOS_APROPIACIONES;

      BEGIN
        BEGIN
          MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA       => 'PLAN_PRESUPUESTAL', 
                                           UN_ACCION      => 'MM', 
                                           UN_MERGEUSING  => 'SELECT  SUM(AI.APROPIACIONINICIAL) APROPINICIAL,
                                                                      AI.COMPANIA,
                                                                      AI.CODIGO,
                                                                      AI.ANO
                                                              FROM    APROPIACIONESINICIALES AI 
                                                                INNER JOIN PLAN_PRESUPUESTAL PP
                                                                  ON  AI.COMPANIA = PP.COMPANIA
                                                                  AND AI.ANO      = PP.ANO
                                                                  AND AI.CODIGO   = PP.CODIGO
                                                              WHERE   PP.COMPANIA = ''' || UN_COMPANIA || '''
                                                                AND   PP.ANO      = '   || UN_ANIO || '
                                                              GROUP BY AI.COMPANIA,
                                                                      AI.CODIGO,
                                                                      AI.ANO', 
                                           UN_MERGEENLACE => '    TABLA.COMPANIA = VISTA.COMPANIA
                                                              AND TABLA.ANO      = VISTA.ANO
                                                              AND TABLA.CODIGO   = VISTA.CODIGO',
                                           UN_MERGEEXISTE => 'UPDATE 
                                                                SET TABLA.APROPIACIONINICIAL = VISTA.APROPINICIAL');

         --ACTUALIZO EL CAMPO APROPIACION INICIAL EN LA TABLA PLAN_PPTAL_CONFIG          

          MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA       => 'PLAN_PPTAL_CONFIG', 
                                           UN_ACCION      => 'MM', 
                                           UN_MERGEUSING  => 'SELECT APROPIACIONESINICIALES.COMPANIA,APROPIACIONESINICIALES.ANO,
                                                              APROPIACIONESINICIALES.CODIGO,APROPIACIONESINICIALES.TERCERO,
                                                              APROPIACIONESINICIALES.SUCURSAL,APROPIACIONESINICIALES.AUXILIAR,
                                                              APROPIACIONESINICIALES.CENTRO_COSTO,APROPIACIONESINICIALES.FUENTE_RECURSO, 
                                                              APROPIACIONESINICIALES.REFERENCIA,APROPIACIONESINICIALES.APROPIACIONINICIAL 
                                                              FROM APROPIACIONESINICIALES
                                                              INNER JOIN PLAN_PPTAL_CONFIG 
                                                                ON APROPIACIONESINICIALES.COMPANIA       = PLAN_PPTAL_CONFIG.COMPANIA
                                                               AND APROPIACIONESINICIALES.ANO            = PLAN_PPTAL_CONFIG.ANO
                                                               AND APROPIACIONESINICIALES.CODIGO         = PLAN_PPTAL_CONFIG.CODIGO
                                                               AND APROPIACIONESINICIALES.TERCERO        = PLAN_PPTAL_CONFIG.TERCERO
                                                               AND APROPIACIONESINICIALES.SUCURSAL       = PLAN_PPTAL_CONFIG.SUCURSAL
                                                               AND APROPIACIONESINICIALES.AUXILIAR       = PLAN_PPTAL_CONFIG.AUXILIAR
                                                               AND APROPIACIONESINICIALES.CENTRO_COSTO   = PLAN_PPTAL_CONFIG.CENTRO_COSTO
                                                               AND APROPIACIONESINICIALES.REFERENCIA     = PLAN_PPTAL_CONFIG.REFERENCIA
                                                               AND APROPIACIONESINICIALES.FUENTE_RECURSO = PLAN_PPTAL_CONFIG.FUENTE_RECURSO', 
                                           UN_MERGEENLACE => '     VISTA.COMPANIA           = TABLA.COMPANIA 
                                                                   AND VISTA.ANO            = TABLA.ANO 
                                                                   AND VISTA.CODIGO         = TABLA.CODIGO
                                                                   AND VISTA.TERCERO        = TABLA.TERCERO
                                                                   AND VISTA.SUCURSAL       = TABLA.SUCURSAL
                                                                   AND VISTA.AUXILIAR       = TABLA.AUXILIAR
                                                                   AND VISTA.CENTRO_COSTO   = TABLA.CENTRO_COSTO
                                                                   AND VISTA.REFERENCIA     = TABLA.REFERENCIA
                                                                   AND VISTA.FUENTE_RECURSO = TABLA.FUENTE_RECURSO',
                                           UN_MERGEEXISTE => 'UPDATE 
                                                                SET TABLA.APROPIACIONINICIAL = VISTA.APROPIACIONINICIAL');

          MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA     => 'APROPIACIONESINICIALES',
                                           UN_ACCION    => 'M', 
                                           UN_CAMPOS    => ' CONTABILIZADO = -1', 
                                           UN_CONDICION => MI_CONDICION);

          MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA     => 'SALDO_AUX_PPTAL',
                                           UN_ACCION    => 'M', 
                                           UN_CAMPOS    => ' APROPIACIONINICIAL = ABS(APROPIACION_DEBITO- APROPIACION_CREDITO) ', 
                                           UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
          WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD   => SQLCODE,
            UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_CONT_APROP_MERGE
          );      

      END;
    END IF;

    IF MI_PCKDATOS IS NULL  
    THEN
      MI_RTA := '0';
    ELSE
      MI_RTA := '1';
    END IF;

    RETURN MI_RTA;      
  END FC_CONTABILIZARAPROPINICIAL;

  -- 11  
  FUNCTION FC_CREARSALDOSPPTALES 
  /*
    NAME              : FC_CREARSALDOSPPTALES (En Access --> CrearSaldosPptales, procedimiento que modifica los saldos presupuestales. 
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : Adriana Caceres
    DATE MIGRADOR     : 02/09/2016
    TIME              : 14:31 AM
    SOURCE MODULE     : SysmanPR2016.03.02.accdb
    MODIFIER          : YEISSON ALEJANDRO ROJAS RUIZ
    DATE MODIFIED     : 13/01/2017
    TIME              : 10:00 AM
    MODIFICATIONS     : CORRECCIÓN SEGÚN ESTÁNDAR DE PROGRAMACIÓN Y OPTIMIZACIÓN DE MANEJO DE ERRORES.
    DESCRIPTION       : Permite modificar los saldos presupuestales.  
                        EN LA TABLA APROPIACIONESINICIALES. HACE USO DEL PROCEDIMIENTO PCK_PRESUPUESTO1.PR_ACTPPTO.
    PARAMETERS        : UN_COMPANIA => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                        UN_ANIO     => AÑO QUE SIRVE PARA LA CONDICIÓN DEL INSERTAR.

    @NAME:    insertarSaldosPresupuestalesIniciales
    @METHOD:  GET         
  */
  (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO
  )
  RETURN NUMBER AS 
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS; 
    MI_VALORES        PCK_SUBTIPOS.TI_VALORES; 
    MI_INT            PCK_SUBTIPOS.TI_ENTERO; 
    MI_RESPUESTA      PCK_SUBTIPOS.TI_RTA_ACME;  
    MI_I              PCK_SUBTIPOS.TI_ENTERO; 

  BEGIN
    MI_INT:=0; 
    MI_I:=0;
    MI_RESPUESTA:=0; 

    <<SALDOPLANPPTAL>>
     -- 7725748 Se modifica en el select el origen del campo con alias ID para que lo tome del campo CODIGO  de la vista y no del ID 
    FOR RS IN (
      SELECT  V_PLAN_PRESUPUESTAL.COMPANIA, 
              V_PLAN_PRESUPUESTAL.ANO, 
              V_PLAN_PRESUPUESTAL.CODIGO ID, 
              COUNT(V_SALDO_PLAN_PPTAL.MES) AS CUENTADEMES 
      FROM    V_PLAN_PRESUPUESTAL  
        LEFT JOIN V_SALDO_PLAN_PPTAL
          ON  V_PLAN_PRESUPUESTAL.COMPANIA = V_SALDO_PLAN_PPTAL.COMPANIA
          AND V_PLAN_PRESUPUESTAL.ANO      = V_SALDO_PLAN_PPTAL.ANO
          AND V_PLAN_PRESUPUESTAL.ID       = V_SALDO_PLAN_PPTAL.ID
      WHERE   V_PLAN_PRESUPUESTAL.COMPANIA = UN_COMPANIA
        AND   V_PLAN_PRESUPUESTAL.ANO      = UN_ANIO
      GROUP BY V_PLAN_PRESUPUESTAL.COMPANIA,
              V_PLAN_PRESUPUESTAL.ANO,
              V_PLAN_PRESUPUESTAL.CODIGO,
              V_SALDO_PLAN_PPTAL.ID
      HAVING COUNT(V_SALDO_PLAN_PPTAL.MES) NOT IN (14))

    LOOP
      <<SELECCIONACUENTA>>
      FOR MI_INT IN 0..13
      LOOP
        MI_CAMPOS  := 'COMPANIA, 
                       ANO, 
                       CODIGO, 
                       MES'; 
        MI_VALORES := ''''|| RS.COMPANIA ||''', 
                      '   || RS.ANO ||', 
                      ''' || RS.ID ||''',  
                      '   || MI_INT ||''; 
        BEGIN
          SELECT  COUNT(*) CUENTA 
          INTO    MI_I
          FROM    SALDO_PLAN_PPTAL 
          WHERE   COMPANIA = RS.COMPANIA
            AND   ANO      = RS.ANO 
            AND   CODIGO   = RS.ID
            AND   MES      = MI_INT; 

          EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_I:=0; 
        END;

        IF MI_I=0 THEN
          BEGIN
            BEGIN
              MI_RESPUESTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'SALDO_PLAN_PPTAL',
                                                 UN_ACCION  => 'I',
                                                 UN_CAMPOS  => MI_CAMPOS,
                                                 UN_VALORES => MI_VALORES);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD   => SQLCODE,
                UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_CREAR_SALDO_PPTALES
              );  
          END;
        END IF; 
      END LOOP SELECCIONACUENTA;  
    END LOOP SALDOPLANPPTAL; 
    RETURN TO_NUMBER(MI_RESPUESTA);

  END FC_CREARSALDOSPPTALES;

  -- 12
  FUNCTION FC_ACTUALIZABANCOPROYECTOS 
  /*
    NAME              : FC_ACTUALIZABANCOPROYECTOS (En Access --> ActualizaBancoProyectos.
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : Adriana Caceres
    DATE MIGRADOR     : 02/09/2016
    TIME              : 15:52 PM
    SOURCE MODULE     : SysmanPR2016.03.02.accdb
    MODIFIER          : YEISSON ALEJANDRO ROJAS RUIZ
    DATE MODIFIED     : 13/01/2017
    TIME              : 10:00 AM
    MODIFICATIONS     : CORRECCIÓN SEGÚN ESTÁNDAR DE PROGRAMACIÓN Y OPTIMIZACIÓN DE MANEJO DE ERRORES.
    DESCRIPTION       : Modifica el VALOR_DISMINUIDO en la tabla BPNOVEDADPROYECTO. 
                        EN LA TABLA APROPIACIONESINICIALES. HACE USO DEL PROCEDIMIENTO PCK_PRESUPUESTO1.PR_ACTPPTO.
    PARAMETERS        : UN_COMPANIA    => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                        UN_ANO         => AÑO QUE SIRVE PARA LA CONDICIÓN DEL INSERTAR.
                        UN_TIPO        => TIPO COMPROBANTE AFECTACIÓN.
                        UN_COMPROBANTE => COMPROBANTE AFECTADO.

    @NAME:    actualizarBancoDeProyectos
    @METHOD:  GET         
  */
  (
    UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_ANO                IN PCK_SUBTIPOS.TI_ANIO,   
    UN_TIPO               IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL, 
    UN_COMPROBANTE        IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTEPPTAL
  )
  RETURN NUMBER AS 
     MI_STRSQL            PCK_SUBTIPOS.TI_STRSQL; 
     MI_DISMINUCION       PCK_SUBTIPOS.TI_DOBLE; 
     MI_TIPOCPTEAFECT     PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL; 
     MI_CMPTEAFECTADO     DETALLE_COMPROBANTE_PPTAL.CMPTE_AFECTADO%TYPE; 
     MI_ACTUALIZABP       PCK_SUBTIPOS.TI_RTA_ACME;

  BEGIN
    MI_ACTUALIZABP:= 0; 
    BEGIN
      BEGIN
        SELECT  SUM(DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO) DISMINUCION, 
                DETALLE_COMPROBANTE_PPTAL_1.TIPO_CPTE_AFECT,
                DETALLE_COMPROBANTE_PPTAL_1.CMPTE_AFECTADO 
        INTO    MI_DISMINUCION, 
                MI_TIPOCPTEAFECT, 
                MI_CMPTEAFECTADO
        FROM    DETALLE_COMPROBANTE_PPTAL 
          INNER JOIN DETALLE_COMPROBANTE_PPTAL DETALLE_COMPROBANTE_PPTAL_1
            ON  DETALLE_COMPROBANTE_PPTAL.COMPANIA        = DETALLE_COMPROBANTE_PPTAL_1.COMPANIA
            AND DETALLE_COMPROBANTE_PPTAL.CUENTA          = DETALLE_COMPROBANTE_PPTAL_1.CUENTA
            AND DETALLE_COMPROBANTE_PPTAL.ANO             = DETALLE_COMPROBANTE_PPTAL_1.ANO 
            AND DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE_AFECT = DETALLE_COMPROBANTE_PPTAL_1.TIPO_CPTE
            AND DETALLE_COMPROBANTE_PPTAL.CMPTE_AFECTADO  = DETALLE_COMPROBANTE_PPTAL_1.COMPROBANTE 
          INNER JOIN TIPO_COMPROBPP 
            ON  DETALLE_COMPROBANTE_PPTAL.COMPANIA  = TIPO_COMPROBPP.COMPANIA 
            AND DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE = TIPO_COMPROBPP.CODIGO
        WHERE   DETALLE_COMPROBANTE_PPTAL.COMPANIA          = UN_COMPANIA
          AND   DETALLE_COMPROBANTE_PPTAL.ANO               = UN_ANO
          AND   DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE_AFECT   = UN_TIPO
          AND   DETALLE_COMPROBANTE_PPTAL.CMPTE_AFECTADO    = UN_COMPROBANTE
          AND   DETALLE_COMPROBANTE_PPTAL_1.TIPO_CPTE_AFECT IS NOT NULL 
          AND   DETALLE_COMPROBANTE_PPTAL_1.CMPTE_AFECTADO  NOT IN (0) 
          AND   TIPO_COMPROBPP.CLASE                        IN ('DMD')
        GROUP BY DETALLE_COMPROBANTE_PPTAL_1.TIPO_CPTE_AFECT,
                DETALLE_COMPROBANTE_PPTAL_1.CMPTE_AFECTADO; 

        EXCEPTION WHEN NO_DATA_FOUND THEN 
          MI_DISMINUCION   := 0; 
          MI_TIPOCPTEAFECT := NULL;
          MI_CMPTEAFECTADO := NULL; 
      END; 

      IF MI_TIPOCPTEAFECT IS NOT NULL THEN
        MI_ACTUALIZABP := PCK_DATOS.FC_ACME(UN_TABLA     => 'BPNOVEDADPROYECTO',
                                            UN_ACCION    => 'M',
                                            UN_CAMPOS    => ' VALOR_DISMINUIDO = ''' || MI_DISMINUCION || '''',
                                            UN_CONDICION => '    COMPANIA = ''' || UN_COMPANIA || ''' 
                                                             AND VIGENCIA = '   || UN_ANO || ' 
                                                             AND TIPOT    = ''' || MI_TIPOCPTEAFECT || '''   
                                                             AND CODIGO   = '   || MI_CMPTEAFECTADO || '');
      END IF;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
    END;

    RETURN TO_NUMBER(MI_ACTUALIZABP);

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD   => SQLCODE,
        UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_ACT_BANCO_PROY
      );

  END FC_ACTUALIZABANCOPROYECTOS;

  -- 13
  PROCEDURE PR_ELIMINAR_COMPROBANTEPPTAL
  /*
    NAME              : PR_ELIMINAR_COMPROBANTEPPTAL
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : NICOLÁS GÓMEZ BARBOSA
    DATE MIGRADOR     : 13/09/2016
    TIME              : 16:00 
    SOURCE MODULE     : PRESUPUESTO
    MODIFIER          : YEISSON ALEJANDRO ROJAS RUIZ \
                        AURA LILIANA MONROY GARCÍA
    DATE MODIFIED     : 13/01/2017 \
                        15/07/2017
    TIME              : 10:00 AM \
                        10:22 AM
    MODIFICATIONS     : CORRECCIÓN SEGÚN ESTÁNDAR DE PROGRAMACIÓN Y OPTIMIZACIÓN DE MANEJO DE ERRORES.  \
                        SE ADICIONAN LAS VALIDACIONES DE VALOR DEL PARAMETRO, COMPROBANTE CONTABLE SIMILAR,
                        VALIDACION DEL ESTADO DE LA FECHA DEL COMPROBANTE, VALIDACIONES CUANDO EL COMPROBANTE
                        ES TIPO "ADI" O "TRA", EVALUAR SI POSEE AFECTACIONES Y ACTUALIZACION DE VALORES CREDITO
                        O DEBITO A LOS DETALLES DEL COMPROBANTE PARA PODER ELIMINAR EL COMPROBANTE
    DESCRIPTION       : Revisa saldos e ingresa los auxiliares presupuestales para cada mes que falte.
    PARAMETERS        : UN_COMPANIA 	   => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                        UN_ANO      	   => AÑO DEL COMPROBANTE.
                        UN_TIPO     	   => TIPO COMPROBANTE.
                        UN_NUMERO   	   => NÚMERO COMPROBANTE.
                        UN_MES 			     => MES DE CREACION DEL COMPROBANTE.
                        UN_DIA 			     => DIA DE CREACION DEL COMPROBANTE.
                        UN_AFECTACIONES  => AFECTACIONES DEL COMPROBANTE A ELIMINAR.
                        UN_USUARIO       => USUARIO QUE ACCEDE AL SISTEMA						

    @NAME:    eliminarComprobantePresupuestal
    @METHOD:  GET  
  */
  (
    UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO                IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPO               IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL,
    UN_NUMERO             IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTEPPTAL,  
    UN_MES 			          IN PCK_SUBTIPOS.TI_MES,
    UN_DIA 			          IN PCK_SUBTIPOS.TI_DIA, 
    UN_AFECTACIONES       IN PCK_SUBTIPOS.TI_DOBLE,
    UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO,
    UN_IMPRESO            IN PCK_SUBTIPOS.TI_LOGICO
  )
  AS
    MI_RETURN             PCK_SUBTIPOS.TI_RTA_ACME;
    MI_CONDICION1         PCK_SUBTIPOS.TI_CONDICION;
    MI_CONDICION2         PCK_SUBTIPOS.TI_CONDICION;
    MI_PARAMETROELIMINAR  PCK_SUBTIPOS.TI_PARAMETRO;
    MI_COMPROBANTESCNT    PCK_SUBTIPOS.TI_LOGICO;
    MI_ESTADOANIO		      PCK_SUBTIPOS.TI_TEXTO1;
    MI_ESTADOMES		      PCK_SUBTIPOS.TI_TEXTO1;
    MI_ESTADODIA		      PCK_SUBTIPOS.TI_TEXTO1;
    MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
    MI_RTA                PCK_SUBTIPOS.TI_RTA_ACME;
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN

    MI_PARAMETROELIMINAR := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                  UN_NOMBRE    => 'PERMITE ELIMINAR COMPROBANTE PRESUPUESTAL',
                                                  UN_MODULO    => PCK_DATOS.MODULOPRESUPUESTO,
                                                  UN_FECHA_PAR => SYSDATE);

    -- Revisa si existe un comprobante contable asociado
    IF MI_PARAMETROELIMINAR = 'NO' THEN
      SELECT COUNT(1) COMPROBANTESCNT
        INTO MI_COMPROBANTESCNT
        FROM COMPROBANTE_CNT
       WHERE COMPANIA  = UN_COMPANIA
         AND ANO       = UN_ANO
         AND TIPO      = UN_TIPO
         AND NUMERO    = UN_NUMERO;

      IF MI_COMPROBANTESCNT NOT IN(0) THEN
        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO
                THEN PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD   => SQLCODE,
                  UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_ECP_COMPROBANTECNT);
        END; 
      END IF;		
    END IF;

    -- Realiza la verificacion de estado presupuestal para el anio, el mes y el dia del comprobante
    MI_ESTADOANIO := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADOANO(UN_COMPANIA	=> UN_COMPANIA,
                                                           UN_ANO		    => UN_ANO,
                                                           UN_MODULO	  => PCK_DATOS.FC_MODULOPRESUPUESTO,
                                                           UN_PROCESO	  => 1);

    MI_ESTADOMES  := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADOMES(UN_COMPANIA	=> UN_COMPANIA,
                                                           UN_ANO		    => UN_ANO,
                                                           UN_MES 		  => UN_MES,
                                                           UN_MODULO	  => PCK_DATOS.FC_MODULOPRESUPUESTO,
                                                           UN_PROCESO	  => 1);	

    MI_ESTADODIA  := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADODIA(UN_COMPANIA	=> UN_COMPANIA,
                                                           UN_ANO		    => UN_ANO,
                                                           UN_MES 		  => UN_MES,
                                                           UN_DIA 		  => UN_DIA,
                                                           UN_MODULO	  => PCK_DATOS.FC_MODULOPRESUPUESTO,
                                                           UN_PROCESO	  => 1);		

    IF MI_ESTADOANIO LIKE 'E' OR MI_ESTADOMES LIKE 'E' THEN
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO
              THEN PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD   => SQLCODE,
                UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_ECP_ANIOMESNOCONFIG);
      END; 
    ELSIF MI_ESTADOANIO LIKE 'C' THEN
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO
              THEN PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD   => SQLCODE,
                UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_ECP_ANIOCERRADO);
      END; 	
    ELSIF MI_ESTADOANIO LIKE 'A' AND MI_ESTADOMES LIKE 'C' THEN
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO
              THEN PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD   => SQLCODE,
                UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_ECP_MESCERRADO);
      END; 	
    ELSIF MI_ESTADOANIO LIKE 'A' AND MI_ESTADOMES LIKE 'A'  AND MI_ESTADODIA LIKE 'C' THEN
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO
              THEN PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD   => SQLCODE,
                UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_ECP_DIACERRADO);
      END; 		
    END IF;	  

    -- Si el comprobante ya fue impreso NO se puede eliminar
    IF UN_IMPRESO NOT IN (0) THEN
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO
                 THEN PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD   => SQLCODE,
                  UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_ECP_IMPRESO);
      END; 		
    END IF;

  -- Verifica si es tipo ADI(adicion) o TRA(traslado) que no tenga disponibilidad por rubro en la saldo plan_pptal
  IF UN_TIPO LIKE 'TRA' OR UN_TIPO LIKE 'ADI' THEN

    <<VERIFICAAFECTACIONES>>
    FOR MI_RS IN 
    (
      SELECT DETALLE_COMPROBANTE_PPTAL.CUENTA,
             SUM(SALDO_PLAN_PPTAL.DISPONIBILIDAD) DISPONIBLE,
             SUM(SALDO_PLAN_PPTAL.ADICION) ADICION	
        FROM DETALLE_COMPROBANTE_PPTAL
          INNER JOIN SALDO_PLAN_PPTAL
             ON DETALLE_COMPROBANTE_PPTAL.COMPANIA    = SALDO_PLAN_PPTAL.COMPANIA
            AND DETALLE_COMPROBANTE_PPTAL.ANO         = SALDO_PLAN_PPTAL.ANO
            AND DETALLE_COMPROBANTE_PPTAL.CUENTA      = SALDO_PLAN_PPTAL.CODIGO
       WHERE DETALLE_COMPROBANTE_PPTAL.COMPANIA    = UN_COMPANIA
         AND DETALLE_COMPROBANTE_PPTAL.ANO         = UN_ANO
         AND DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE   = UN_TIPO
         AND DETALLE_COMPROBANTE_PPTAL.COMPROBANTE = UN_NUMERO
       GROUP BY DETALLE_COMPROBANTE_PPTAL.COMPANIA,
          DETALLE_COMPROBANTE_PPTAL.ANO,
          DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE,
          DETALLE_COMPROBANTE_PPTAL.COMPROBANTE,
          DETALLE_COMPROBANTE_PPTAL.CUENTA
    ) 
    LOOP 
      IF CASE WHEN UN_TIPO LIKE 'TRA' THEN MI_RS.DISPONIBLE ELSE MI_RS.ADICION END NOT IN(0) THEN 
        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO
                THEN PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD   => SQLCODE,
                  UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_ECP_AFECTACIONES);
        END; 		
      END IF;
    END LOOP VERIFICAAFECTACIONES;

  END IF;  

  -- Evalua si el comprobante esta afectado por otro comprobante
  IF UN_AFECTACIONES NOT IN (0) THEN
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO
              THEN PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD   => SQLCODE,
                UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_ECP_AFECTACIONES);
      END; 		  
  END IF;

  -- Evalua los detalles del comprobante, y dependiendo su naturaleza verifica el valor del debito o credito    
  <<EVALUARVALORES>>
  FOR MI_RSDETALLES IN 
  (
    SELECT DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO,
             CLASECNTPRES.COLUMNA NATURALEZA
      FROM DETALLE_COMPROBANTE_PPTAL
        INNER JOIN TIPO_COMPROBPP
           ON DETALLE_COMPROBANTE_PPTAL.COMPANIA  = TIPO_COMPROBPP.COMPANIA
          AND DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE = TIPO_COMPROBPP.CODIGO
        INNER JOIN CLASECNTPRES
           ON TIPO_COMPROBPP.CLASE = CLASECNTPRES.CODIGO       
      WHERE DETALLE_COMPROBANTE_PPTAL.COMPANIA    =  UN_COMPANIA 
        AND DETALLE_COMPROBANTE_PPTAL.ANO         =  UN_ANO
        AND DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE   =  UN_TIPO 
        AND DETALLE_COMPROBANTE_PPTAL.COMPROBANTE =  UN_NUMERO     
  )
  LOOP  
    BEGIN
      DECLARE 
        MI_NATURALEZA VARCHAR2(7 CHAR);
      BEGIN
        MI_NATURALEZA := CASE MI_RSDETALLES.NATURALEZA 
                          WHEN 'C' THEN 'CREDITO'
                          WHEN 'D' THEN 'DEBITO'
                         END;

        MI_TABLA      := 'DETALLE_COMPROBANTE_PPTAL';

        MI_CAMPOS     := CASE MI_RSDETALLES.NATURALEZA 
                          WHEN 'C' THEN ' VALOR_CREDITO = 0 '
                          WHEN 'D' THEN ' VALOR_DEBITO  = 0 '
                         END || ', ' ||
                         'DATE_MODIFIED      = SYSDATE, '||
                         'MODIFIED_BY        = ''' || UN_USUARIO || ''' ';

        MI_CONDICION  :=  '     COMPANIA      = ''' || UN_COMPANIA ||''''||
                          ' AND ANO           =   ' || UN_ANO ||' '||
                          ' AND TIPO_CPTE     = ''' || UN_TIPO || '''' ||
                          ' AND COMPROBANTE   =   ' || UN_NUMERO || 
                          ' AND CONSECUTIVO   =   ' || MI_RSDETALLES.CONSECUTIVO;

        MI_RTA        := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                            UN_ACCION    => 'M', 
                                            UN_CAMPOS    => MI_CAMPOS, 
                                            UN_CONDICION => MI_CONDICION);                    

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
           MI_MSGERROR(1).CLAVE := 'NATURALEZA';
           MI_MSGERROR(1).VALOR :=  MI_NATURALEZA;
           MI_MSGERROR(2).CLAVE := 'COMPROBANTE';
           MI_MSGERROR(2).VALOR :=  UN_NUMERO;
           MI_MSGERROR(3).CLAVE := 'ANIO';
           MI_MSGERROR(3).VALOR :=  UN_ANO;
           RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;                                        
        END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    =>  SQLCODE,
          UN_ERROR_COD  =>  PCK_ERRORES.ERR_PPTO_ECP_ACTDETALLES,
          UN_TABLAERROR =>  MI_TABLA,
          UN_REEMPLAZOS =>  MI_MSGERROR
        );
    END;    

  END LOOP EVALUARVALORES;

  -- Elimina los detalles del comprobante
  BEGIN  
    DECLARE
      REG NUMBER;
    BEGIN     
      SELECT COUNT(COMPANIA) REGISTROS
       INTO REG
       FROM DETALLE_COMPROBANTE_PPTAL 
      WHERE COMPANIA    =  UN_COMPANIA 
        AND ANO         =  UN_ANO
        AND TIPO_CPTE   =  UN_TIPO 
        AND COMPROBANTE =  UN_NUMERO;

      MI_TABLA      := 'DETALLE_COMPROBANTE_PPTAL';

      MI_CONDICION2 := '    COMPANIA    = '''|| UN_COMPANIA ||''' 
                        AND ANO         = '  || UN_ANO ||' 
                        AND TIPO_CPTE   = '''|| UN_TIPO ||''' 
                        AND COMPROBANTE = '  || UN_NUMERO;                

      IF REG > 0 THEN
      --Se hace un update de los valores de debito y credito para que estos valores sean cero y se puedaelimnar el comprobante presupúestal--
        UPDATE DETALLE_COMPROBANTE_PPTAL SET VALOR_DEBITO=0,VALOR_CREDITO =0
        WHERE 
        ANO=UN_ANO
        AND COMPANIA=UN_COMPANIA
        AND COMPROBANTE =UN_NUMERO
        AND TIPO_CPTE   =UN_TIPO;
        MI_RTA   :=PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                     UN_ACCION    => 'E',
                                     UN_CONDICION => MI_CONDICION2);
      END IF;        

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
      RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
    END;        

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(
      UN_EXC_COD    => SQLCODE,
      UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ECP_ELIDETALLES, 
      UN_TABLAERROR => MI_TABLA
    );
  END;

   -- Elimina el comprobante presupuestal    
   BEGIN
    BEGIN
      MI_TABLA      := 'COMPROBANTE_PPTAL';
      MI_CONDICION1 := '    COMPANIA = '''|| UN_COMPANIA ||''' 
                        AND ANO      = '  || UN_ANO ||' 
                        AND TIPO     = '''|| UN_TIPO  ||''' 
                        AND NUMERO   = '  || UN_NUMERO;
  IF PCK_PRESUPUESTO.GL_DES_KEY NOT IN(0) THEN                        
     PCK_SYSMAN_UTL.PR_DISABLE_KEY_IN_DELETE( UN_TABLE_NAME        => 'DETALLE_COMPROBANTE_PPTAL');
  END IF; 
    
      MI_RTA        := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                         UN_ACCION    => 'E',
                                         UN_CONDICION => MI_CONDICION1);
  IF PCK_PRESUPUESTO.GL_DES_KEY NOT IN(0) THEN                                         
    PCK_SYSMAN_UTL.PR_ENABLE_KEY_IN_DELETE( UN_TABLE_NAME        => 'DETALLE_COMPROBANTE_PPTAL');
  END IF;   
  
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;                                
    END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD    => SQLCODE,
        UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ELIM_COMP_PPTAL, 
        UN_TABLAERROR => MI_TABLA
      );
  END;

  END PR_ELIMINAR_COMPROBANTEPPTAL;

  -- 14
  PROCEDURE PR_VALIDARDISPONIBLE  
  /*
    NAME              : PR_VALIDARDISPONIBLE -- Form_BeforeUpdate en Form_ApropiacionesIniciales en Access
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : LEYDI MILENA CORTÉS FORERO
    DATE MIGRADOR     : 12/10/2016
    TIME              : 10:50 AM
    MODIFIER          : YEISSON ALEJANDRO ROJAS RUIZ
    DATE MODIFIED     : 13/01/2017
    TIME              : 10:00 AM
    DESCRIPTION       : 
    MODIFICATIONS     : CORRECCIÓN SEGÚN ESTÁNDAR DE PROGRAMACIÓN Y OPTIMIZACIÓN DE MANEJO DE ERRORES.
    PARAMETERS        : UN_COMPANIA           => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                        UN_ANIO               => AÑO QUE SIRVE PARA LA CONDICIÓN DEL MODIFICAR.
                        UN_CODIGO             => CÓDIGO DE LA APROPIACIÓN INICIAL.
                        UN_APROPIACIONINICIAL => VALOR DE LA APROPIACIÓN INICIAL.
                        UN_TERCERO            => CÓDIGO DEL TERCERO.
                        UN_SUCURSAL           => CÓDIGO DE LA SUCURSAL.
                        UN_AUXILIAR           => CÓDIGO DEL AUXILIAR.
                        UN_CENTRO_COSTO       => CÓDIGO DEL CENTRO DE COSTO.
                        UN_REFERENCIA         => NÚMERO DE LA REFERENCIA.
                        UN_FUENTE_RECURSO     => TIPO DE FUENTE RECURSO.
    @NAME:    validarDisponible
    @METHOD:  GET 
   */
  (
    UN_COMPANIA       	    IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO           	    IN PCK_SUBTIPOS.TI_ANIO,
    UN_CODIGO		  	        IN PCK_SUBTIPOS.TI_CODIGOPPTAL,
    UN_APROPIACIONINICIAL	  IN PCK_SUBTIPOS.TI_DOBLE,
    UN_TERCERO			        IN PCK_SUBTIPOS.TI_TERCERO,
    UN_SUCURSAL			        IN PCK_SUBTIPOS.TI_SUCURSAL,
    UN_AUXILIAR			        IN PCK_SUBTIPOS.TI_AUXILIAR,
    UN_CENTRO_COSTO		      IN PCK_SUBTIPOS.TI_CENTRO_COSTO,	
    UN_REFERENCIA		        IN PCK_SUBTIPOS.TI_REFERENCIA,
    UN_FUENTE_RECURSO	      IN PCK_SUBTIPOS.TI_FUENTE_RECURSOS
  )AS
    MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    BEGIN
      <<RESPUESTA>>
      FOR RS_DISP IN (
        SELECT  SUM(SALDO_AUX_PPTAL.DISPONIBILIDAD)               SUMADISP, 
                SUM(SALDO_AUX_PPTAL.PAC_APROPIADO)                PAC, 
                SUM(REGISTRO_OBLIGACION 
                  + MODIF_REGISTRO_OBLIGACION)                    REO,
                SUM(EJE_PPT_DEBITO 
                  - EJE_PPT_CREDITO)                              EGR,
                SUM(REG_CONTRACT 
                  + REG_NO_CONTRACT 
                  + MODIF_REG_CONT 
                  + MODIF_REG_NOCONT)                             REG, 
                SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA = 'D' 
                         THEN APLAZAM_DEBITO - APLAZAM_CREDITO 
                         ELSE APLAZAM_CREDITO - APLAZAM_DEBITO 
                    END)                                           APLAZAMIENTO, 
                SUM(SALDO_AUX_PPTAL.ADICION)                       ADICION, 
                SUM(SALDO_AUX_PPTAL.REDUCCION)                     REDUCCION, 
                SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA = 'D' 
                         THEN TRASLADO_DEBITO - TRASLADO_CREDITO 
                         ELSE TRASLADO_CREDITO - TRASLADO_DEBITO 
                    END)                                           TRASLADO, 
                (SUM(SALDO_AUX_PPTAL.ADICION) 
                  + SUM(SALDO_AUX_PPTAL.REDUCCION) 
                  + SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA = 'D' 
                             THEN TRASLADO_DEBITO - TRASLADO_CREDITO 
                             ELSE TRASLADO_CREDITO - TRASLADO_DEBITO 
                        END)
                  + SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA = 'D' 
                             THEN APLAZAM_DEBITO - APLAZAM_CREDITO 
                             ELSE APLAZAM_CREDITO - APLAZAM_DEBITO 
                        END))                                       MODIFICACIONES  
        FROM   PLAN_PRESUPUESTAL 
          INNER JOIN SALDO_AUX_PPTAL 
            ON  PLAN_PRESUPUESTAL.COMPANIA = SALDO_AUX_PPTAL.COMPANIA  
           AND PLAN_PRESUPUESTAL.ANO       = SALDO_AUX_PPTAL.ANO  
           AND PLAN_PRESUPUESTAL.CODIGO    = SALDO_AUX_PPTAL.CODIGO 
        WHERE   PLAN_PRESUPUESTAL.COMPANIA      = UN_COMPANIA 
          AND   PLAN_PRESUPUESTAL.ANO           = UN_ANIO  
          AND   PLAN_PRESUPUESTAL.CODIGO        = UN_CODIGO 
          AND   SALDO_AUX_PPTAL.AUXILIAR      = CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_GEN IN (0)
                                                     THEN '99999999999999999999'
                                                     ELSE UN_AUXILIAR
                                                END 
          AND   SALDO_AUX_PPTAL.TERCERO       = CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_TER IN (0)
                                                     THEN '999999999999999999'
                                                     ELSE UN_TERCERO
                                                END 
           AND   SALDO_AUX_PPTAL.SUCURSAL       = CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_TER IN (0)
                                                     THEN '999'
                                                     ELSE UN_SUCURSAL
                                                END                                       
          AND   SALDO_AUX_PPTAL.CENTRO_COSTO   = CASE WHEN PLAN_PRESUPUESTAL.MAN_CEN_CTO IN (0)
                                                      THEN '99999999999999999999'
                                                      ELSE UN_CENTRO_COSTO
                                                 END 
          AND   SALDO_AUX_PPTAL.REFERENCIA     = CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_REF IN (0)
                                                      THEN '99999999999999999999'
                                                      ELSE UN_REFERENCIA
                                                 END 
          AND   SALDO_AUX_PPTAL.FUENTE_RECURSO = CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_FUE IN (0)
                                                      THEN '99999999999999999999'
                                                      ELSE UN_FUENTE_RECURSO
                                                 END
          AND   PLAN_PRESUPUESTAL.NATURALEZA = 'D')
      LOOP
        BEGIN  
          IF (UN_APROPIACIONINICIAL - RS_DISP.SUMADISP + RS_DISP.MODIFICACIONES) < 0 THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
          END IF;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
          MI_MSGERROR(1).CLAVE := 'APROPINICIAL';
          MI_MSGERROR(1).VALOR := UN_APROPIACIONINICIAL;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ACTAPROPINICASO1,
            UN_REEMPLAZOS => MI_MSGERROR
        );
        END;
        BEGIN
          IF RS_DISP.PAC > 0 THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
          END IF;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
          MI_MSGERROR(1).CLAVE := 'APROPINICIAL';
          MI_MSGERROR(1).VALOR := UN_APROPIACIONINICIAL;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ACTAPROPINICASO2,
            UN_REEMPLAZOS => MI_MSGERROR
        );
        END;
        BEGIN
          IF RS_DISP.REG > UN_APROPIACIONINICIAL + RS_DISP.MODIFICACIONES THEN 
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
          END IF;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
          MI_MSGERROR(1).CLAVE := 'APROPINICIAL';
          MI_MSGERROR(1).VALOR := UN_APROPIACIONINICIAL;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ACTAPROPINICASO3,
            UN_REEMPLAZOS => MI_MSGERROR
        );
        END;
        BEGIN
          IF RS_DISP.REO > UN_APROPIACIONINICIAL + RS_DISP.MODIFICACIONES THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
          END IF;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
          MI_MSGERROR(1).CLAVE := 'APROPINICIAL';
          MI_MSGERROR(1).VALOR := UN_APROPIACIONINICIAL;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ACTAPROPINICASO4,
            UN_REEMPLAZOS => MI_MSGERROR
        );
        END;
        BEGIN
          IF RS_DISP.EGR > UN_APROPIACIONINICIAL + RS_DISP.MODIFICACIONES THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
          END IF;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
          MI_MSGERROR(1).CLAVE := 'APROPINICIAL';
          MI_MSGERROR(1).VALOR := UN_APROPIACIONINICIAL;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ACTAPROPINICASO5,
            UN_REEMPLAZOS => MI_MSGERROR
        );
        END;    
      END LOOP RESPUESTA;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
    END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD   => SQLCODE,
        UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_ACT_APR_INIC
    );

	END PR_VALIDARDISPONIBLE;

  -- 15
  PROCEDURE PR_GENERAR_AUXILIAR
    /*
      NAME              : PR_GENERAR_AUXILIAR En access --> evento del boton Generar Auxiliar 
      AUTHORS           : SYSMAN  SAS 
      AUTHOR MIGRACION  : ADRIANA MARITZA CÁCERES BONILLA
      DATE MIGRADOR     : 15/06/2016
      TIME              : 02:30
      SOURCE MODULE     : PRESUPUESTO
      MODIFIER          : YEISSON ALEJANDRO ROJAS RUIZ/YESIKA PAOLA BECERRA CASTRO
      DATE MODIFIED     : 13/01/2017 --- 25/04/2017
      TIME              : 5:30 PM
      DESCRIPTION       : 
      MODIFICATIONS     : CORRECCIÓN SEGÚN ESTÁNDAR DE PROGRAMACIÓN Y OPTIMIZACIÓN DE MANEJO DE ERRORES./
      Cambio de nombre en el @Name, se agregan los campos DATE_CREATED,CREATED_BY por cuestiones de auditoria del registro
      PARAMETERS        : UN_COMPANIA     => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                          UN_USUARIO     => Usuario identificado en la aplicacion

      @NAME:  insertarAuxiliarenPresupuesto
      @METHOD:  GET   
    */
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO          IN PCK_SUBTIPOS.TI_ANIO,
    UN_USUARIO      IN VARCHAR2
  )
  AS
    MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES      PCK_SUBTIPOS.TI_VALORES;

  BEGIN
    BEGIN
      FOR MI_RS IN ( 
        SELECT  FUENTE_RECURSOS.COMPANIA,
                FUENTE_RECURSOS.CODIGO,
                FUENTE_RECURSOS.NOMBRE,
                FUENTE_RECURSOS.ANO
        FROM    FUENTE_RECURSOS
          WHERE FUENTE_RECURSOS.COMPANIA = UN_COMPANIA
            AND FUENTE_RECURSOS.ANO = UN_ANO
            AND FUENTE_RECURSOS.CODIGO NOT IN ( SELECT AUXILIAR.CODIGO
                                                FROM AUXILIAR
                                                WHERE AUXILIAR.COMPANIA = UN_COMPANIA
                                                  AND AUXILIAR.ANO = UN_ANO)
        )

      LOOP
        MI_CAMPOS        := 'COMPANIA, 
                             CODIGO, 
                             NOMBRE, 
                             ANO, 
                             MOVIMIENTO,
                             DATE_CREATED,
                             CREATED_BY'; 
        MI_VALORES       := ''''|| MI_RS.COMPANIA || ''', 
                            ''' || MI_RS.CODIGO || ''', 
                            ''' || MI_RS.NOMBRE || ''',
                            '   || MI_RS.ANO || ',-1,
                            SYSDATE,'''||UN_USUARIO||''''; 
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'AUXILIAR',
                                               UN_ACCION  => 'I',
                                               UN_CAMPOS  => MI_CAMPOS,
                                               UN_VALORES => MI_VALORES);
      END LOOP;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;    
    END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD   => SQLCODE,
        UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_GENERAR_AUX
    );

  END PR_GENERAR_AUXILIAR;

  -- 16
  PROCEDURE PR_LIMPIA_SALDO_AUX_PPTAL
    /*
      NAME              : PR_LIMPIA_SALDO_AUX_PPTAL
      AUTHORS           : SYSMAN  SAS 
      AUTHOR MIGRACION  : José Pascual Gómez Blanco
      DATE MIGRADOR     : 21/12/2016
      TIME              : 02:44
      SOURCE MODULE     : PRESUPUESTO
      MODIFIER          : YEISSON ALEJANDRO ROJAS RUIZ
      DATE MODIFIED     : 13/01/2017
      TIME              : 12:00 PM
      DESCRIPTION       : Procedimiento que limpia el SALDO_AUX_PPTAL de cuentas mayores y cuentas con movimiento y registros de auxiliares.
      MODIFICATIONS     : CORRECCIÓN SEGÚN ESTÁNDAR DE PROGRAMACIÓN Y OPTIMIZACIÓN DE MANEJO DE ERRORES.
      PARAMETERS        : UN_COMPANIA      => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                          UN_ANIO          => AÑO DE LA CONDICIÓN PARA ELIMINAR.
                          UN_CODIGOINICIAL => CODIGO INICIAL DE LA CONDICIÓN PARA ELIMINAR.
                          UN_CODIGOFINAL   => CODIGO FINAL DE LA CONDICIÓN PARA ELIMINAR.

      @NAME:    limpiarSaldoPPtal
      @METHOD:  GET   
    */
  (
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO            IN PCK_SUBTIPOS.TI_ANIO,
    UN_CODIGOINICIAL	 IN PCK_SUBTIPOS.TI_CODIGOPPTAL,
    UN_CODIGOFINAL 		 IN PCK_SUBTIPOS.TI_CODIGOPPTAL
  )
  AS
    MI_CONDICION       PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS          PCK_SUBTIPOS.TI_CAMPOS;
    MI_PCKDATOS        PCK_SUBTIPOS.TI_RTA_ACME;
    MI_VALORES         PCK_SUBTIPOS.TI_VALORES;    
  BEGIN
    BEGIN
      MI_CONDICION := '(COMPANIA, 
                        ANO, 
                        CODIGO) IN (
                          SELECT  COMPANIA, 
                                  ANO, 
                                  CODIGO 
                          FROM    PLAN_PRESUPUESTAL
                          WHERE   PLAN_PRESUPUESTAL.COMPANIA  = ''' || UN_COMPANIA || '''
                            AND   PLAN_PRESUPUESTAL.ANO       = '   || UN_ANIO || '
                            AND   PLAN_PRESUPUESTAL.CODIGO    BETWEEN ''' || UN_CODIGOINICIAL || ''' AND ''' || UN_CODIGOFINAL || '''
                            AND   ( MAN_CEN_CTO 
                                  + MAN_AUX_TER 
                                  + MAN_AUX_GEN 
                                  + MAN_AUX_FUE 
                                  + MAN_AUX_REF + MOVIMIENTO) = 0
                      )';
      MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => 'SALDO_AUX_PPTAL', 
                                        UN_ACCION    => 'E', 
                                        UN_CONDICION => MI_CONDICION);

      MI_CONDICION := '(COMPANIA, 
                        ANO, 
                        CODIGO, 
                        MES, 
                        CENTRO_COSTO, 
                        TERCERO, 
                        SUCURSAL, 
                        AUXILIAR, 
                        REFERENCIA, 
                        FUENTE_RECURSO) IN (
                          SELECT SALDO_AUX_PPTAL.COMPANIA,     
                                 SALDO_AUX_PPTAL.ANO, 
                                 SALDO_AUX_PPTAL.CODIGO  ,     
                                 SALDO_AUX_PPTAL.MES, 
                                 SALDO_AUX_PPTAL.CENTRO_COSTO, 
                                 SALDO_AUX_PPTAL.TERCERO, 
                                 SALDO_AUX_PPTAL.SUCURSAL,     
                                 SALDO_AUX_PPTAL.AUXILIAR, 
                                 SALDO_AUX_PPTAL.REFERENCIA,   
                                 SALDO_AUX_PPTAL.FUENTE_RECURSO
                          FROM   SALDO_AUX_PPTAL 
                            INNER JOIN PLAN_PRESUPUESTAL
                              ON SALDO_AUX_PPTAL.COMPANIA = PLAN_PRESUPUESTAL.COMPANIA
                             AND SALDO_AUX_PPTAL.ANO      = PLAN_PRESUPUESTAL.ANO
                             AND SALDO_AUX_PPTAL.CODIGO   = PLAN_PRESUPUESTAL.CODIGO
                          WHERE  PLAN_PRESUPUESTAL.COMPANIA = ''' || UN_COMPANIA || '''
                            AND  PLAN_PRESUPUESTAL.ANO      = '   || UN_ANIO || '
                            AND  PLAN_PRESUPUESTAL.CODIGO   BETWEEN ''' || UN_CODIGOINICIAL || ''' AND ''' || UN_CODIGOFINAL || '''
                            AND  (SALDO_AUX_PPTAL.CENTRO_COSTO  <> CASE WHEN PLAN_PRESUPUESTAL.MAN_CEN_CTO    IN(0) THEN PCK_DATOS.FC_CONS_CENTRO     ELSE SALDO_AUX_PPTAL.CENTRO_COSTO   END
                            OR   SALDO_AUX_PPTAL.TERCERO        <> CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_TER    IN(0) THEN PCK_DATOS.FC_CONS_TERCERO    ELSE SALDO_AUX_PPTAL.TERCERO        END
                            OR   SALDO_AUX_PPTAL.SUCURSAL       <> CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_TER    IN(0) THEN PCK_DATOS.FC_CONS_SUCURSAL   ELSE SALDO_AUX_PPTAL.SUCURSAL       END
                            OR   SALDO_AUX_PPTAL.AUXILIAR       <> CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_GEN    IN(0) THEN PCK_DATOS.FC_CONS_AUXILIAR   ELSE SALDO_AUX_PPTAL.AUXILIAR       END
                            OR   SALDO_AUX_PPTAL.FUENTE_RECURSO <> CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_FUE    IN(0) THEN PCK_DATOS.FC_CONS_FUENTE     ELSE SALDO_AUX_PPTAL.FUENTE_RECURSO END
                            OR   SALDO_AUX_PPTAL.REFERENCIA     <> CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_REF    IN(0) THEN PCK_DATOS.FC_CONS_REFERENCIA ELSE SALDO_AUX_PPTAL.REFERENCIA     END )
                      )';

      MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => 'SALDO_AUX_PPTAL', 
                                        UN_ACCION    => 'E',
                                        UN_CONDICION => MI_CONDICION);      

      MI_CONDICION := '(COMPANIA, 
                        ANO, 
                        CODIGO) IN (
                          SELECT  COMPANIA, 
                                  ANO, 
                                  CODIGO 
                          FROM    PLAN_PRESUPUESTAL
                          WHERE   PLAN_PRESUPUESTAL.COMPANIA  = ''' || UN_COMPANIA || '''
                            AND   PLAN_PRESUPUESTAL.ANO       = '   || UN_ANIO || '
                            AND   PLAN_PRESUPUESTAL.CODIGO    BETWEEN ''' || UN_CODIGOINICIAL || ''' AND ''' || UN_CODIGOFINAL || '''
                            AND   ( MAN_CEN_CTO 
                                  + MAN_AUX_TER 
                                  + MAN_AUX_GEN 
                                  + MAN_AUX_FUE 
                                  + MAN_AUX_REF + MOVIMIENTO) = 0
                      )';
      MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => 'PLAN_PPTAL_CONFIG', 
                                        UN_ACCION    => 'E', 
                                        UN_CONDICION => MI_CONDICION);

      MI_CONDICION := '(COMPANIA, 
                        ANO, 
                        CODIGO, 
                        CENTRO_COSTO, 
                        TERCERO, 
                        SUCURSAL, 
                        AUXILIAR, 
                        REFERENCIA, 
                        FUENTE_RECURSO) IN (
                          SELECT PLAN_PPTAL_CONFIG.COMPANIA,     
                                 PLAN_PPTAL_CONFIG.ANO, 
                                 PLAN_PPTAL_CONFIG.CODIGO,    
                                 PLAN_PPTAL_CONFIG.CENTRO_COSTO, 
                                 PLAN_PPTAL_CONFIG.TERCERO, 
                                 PLAN_PPTAL_CONFIG.SUCURSAL,     
                                 PLAN_PPTAL_CONFIG.AUXILIAR, 
                                 PLAN_PPTAL_CONFIG.REFERENCIA,   
                                 PLAN_PPTAL_CONFIG.FUENTE_RECURSO
                          FROM   PLAN_PPTAL_CONFIG 
                            INNER JOIN PLAN_PRESUPUESTAL
                              ON PLAN_PPTAL_CONFIG.COMPANIA = PLAN_PRESUPUESTAL.COMPANIA
                             AND PLAN_PPTAL_CONFIG.ANO      = PLAN_PRESUPUESTAL.ANO
                             AND PLAN_PPTAL_CONFIG.CODIGO   = PLAN_PRESUPUESTAL.CODIGO
                          WHERE  PLAN_PRESUPUESTAL.COMPANIA = ''' || UN_COMPANIA || '''
                            AND  PLAN_PRESUPUESTAL.ANO      = '   || UN_ANIO || '
                            AND  PLAN_PRESUPUESTAL.CODIGO   BETWEEN ''' || UN_CODIGOINICIAL || ''' AND ''' || UN_CODIGOFINAL || '''
                            AND  (PLAN_PPTAL_CONFIG.CENTRO_COSTO   <> CASE WHEN PLAN_PRESUPUESTAL.MAN_CEN_CTO    IN(0) THEN PCK_DATOS.FC_CONS_CENTRO     ELSE PLAN_PPTAL_CONFIG.CENTRO_COSTO   END
                             OR   PLAN_PPTAL_CONFIG.TERCERO        <> CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_TER    IN(0) THEN PCK_DATOS.FC_CONS_TERCERO    ELSE PLAN_PPTAL_CONFIG.TERCERO        END
                             OR   PLAN_PPTAL_CONFIG.SUCURSAL       <> CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_TER    IN(0) THEN PCK_DATOS.FC_CONS_SUCURSAL   ELSE PLAN_PPTAL_CONFIG.SUCURSAL       END
                             OR   PLAN_PPTAL_CONFIG.AUXILIAR       <> CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_GEN    IN(0) THEN PCK_DATOS.FC_CONS_AUXILIAR   ELSE PLAN_PPTAL_CONFIG.AUXILIAR       END
                             OR   PLAN_PPTAL_CONFIG.FUENTE_RECURSO <> CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_FUE    IN(0) THEN PCK_DATOS.FC_CONS_FUENTE     ELSE PLAN_PPTAL_CONFIG.FUENTE_RECURSO END
                             OR   PLAN_PPTAL_CONFIG.REFERENCIA     <> CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_REF    IN(0) THEN PCK_DATOS.FC_CONS_REFERENCIA ELSE PLAN_PPTAL_CONFIG.REFERENCIA     END )
                      )';


      MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => 'PLAN_PPTAL_CONFIG', 
                                        UN_ACCION    => 'E',
                                        UN_CONDICION => MI_CONDICION);


      MI_CAMPOS        := 'ID = ID ';
      MI_CONDICION     := ' COMPANIA = ''' || UN_COMPANIA || '''
                        AND ANO      = '   || UN_ANIO || '
                        AND CODIGO   BETWEEN ''' || UN_CODIGOINICIAL || ''' AND ''' || UN_CODIGOFINAL || '''';
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'SALDO_AUX_PPTAL', 
                                             UN_ACCION    => 'M', 
                                             UN_CAMPOS    => MI_CAMPOS, 
                                             UN_CONDICION => MI_CONDICION);
       --JP Se incorpora para que elimine las auxiliares que nunca se han utilizado en el año no se puede evaluar mes por mes por que daña las consultas
       MI_CONDICION :=' (COMPANIA, ANO, CODIGO, CENTRO_COSTO, TERCERO, SUCURSAL, AUXILIAR, REFERENCIA, FUENTE_RECURSO) IN 
                       ( SELECT COMPANIA, ANO, CODIGO, CENTRO_COSTO, TERCERO, SUCURSAL, AUXILIAR, REFERENCIA, FUENTE_RECURSO
                      FROM SALDO_AUX_PPTAL
                      WHERE COMPANIA  = ''' || UN_COMPANIA || '''
                        AND ANO       = '   || UN_ANIO || '
                        AND CODIGO   BETWEEN ''' || UN_CODIGOINICIAL || ''' AND ''' || UN_CODIGOFINAL || '''
                      GROUP BY COMPANIA, ANO, CODIGO, CENTRO_COSTO, TERCERO, SUCURSAL, AUXILIAR, REFERENCIA, FUENTE_RECURSO
                      HAVING SUM(ADICION) =0
                         AND SUM(REDUCCION) =0
                         AND SUM(PAC_APROPIADO) =0
                         AND SUM(PAC_PROGRAMADO) =0
                         AND SUM(DISPONIBILIDAD) =0
                         AND SUM(DISPONIBILIDADADD) =0
                         AND SUM(DISPONIBILIDADDMD) =0
                         AND SUM(REG_NO_CONTRACT) =0
                         AND SUM(REG_CONTRACT) =0
                         AND SUM(REG_REVERSION) =0
                         AND SUM(MODIF_PAC_DEBITO) =0
                         AND SUM(MODIF_PAC_CREDITO) =0
                         AND SUM(MODIF_REG_CONT) =0
                         AND SUM(MODIF_REG_NOCONT) =0
                         AND SUM(MODIF_REG_CONTADR) =0
                         AND SUM(MODIF_REG_NOCONTADR) =0
                         AND SUM(MODIF_REG_CONTDMR) =0
                         AND SUM(MODIF_REG_NOCONTDMR) =0
                         AND SUM(REINTEGRO) =0
                         AND SUM(VIGENCIAANTERIOR) =0
                         AND SUM(VIGENCIAFUTURA) =0
                         AND SUM(TRASLADO_DEBITO) =0
                         AND SUM(TRASLADO_CREDITO) =0
                         AND SUM(APLAZAM_DEBITO) =0
                         AND SUM(APLAZAM_CREDITO) =0
                         AND SUM(EJE_CNT_DEBITO) =0
                         AND SUM(EJE_CNT_CREDITO) =0
                         AND SUM(APROPIACION_DEBITO) =0
                         AND SUM(APROPIACION_CREDITO) =0
                         AND SUM(EJE_PPT_DEBITO) =0
                         AND SUM(EJE_PPT_CREDITO) =0
                         AND SUM(EJE_PPT_DEBITOAEG) =0
                         AND SUM(EJE_PPT_DEBITODEG) =0
                         AND SUM(REGISTRO_OBLIGACION) =0
                         AND SUM(MODIF_REGISTRO_OBLIGACION) =0
                         AND SUM(MODIF_REGISTRO_OBLIGACIONARO) =0
                         AND SUM(MODIF_REGISTRO_OBLIGACIONDRO) =0
                         AND SUM(INGRESOS_EFECTIVO) =0
                         AND SUM(INGRESOS_PAPELES) =0
                         AND SUM(MODIF_INGRESOS_EFECTIVO) =0
                         AND SUM(MODIF_INGRESOS_PAPELES) =0
                         AND SUM(INGRESOS_CAUSADOS) =0
                         AND SUM(MODIF_INGRESOS_CAUSADOS) =0
                         AND SUM(MODIF_INGRESOS) =0
                         AND SUM(NETOEGRESO) =0
                         AND SUM(RECONOCIMIENTOS) =0
                         AND SUM(PACTESORERIA) =0
                         AND SUM(PAC_EJECUTADO) =0
                         AND SUM(PAC_COMPROMETIDO) =0
                         AND SUM(APROPIACIONINICIAL) =0
                         )';
      MI_PCKDATOS  := PCK_DATOS.FC_ACME(UN_TABLA     => 'SALDO_AUX_PPTAL', 
                                        UN_ACCION    => 'E',
                                        UN_CONDICION => MI_CONDICION); 

      --Se crea bucle para construir los auxiliares faltantes por mes
      FOR RS IN(SELECT COMPANIA, ANO, CODIGO, CENTRO_COSTO, TERCERO, SUCURSAL, AUXILIAR, REFERENCIA, FUENTE_RECURSO, NATURALEZA, COUNT(COMPANIA)
                FROM SALDO_AUX_PPTAL
                WHERE COMPANIA = UN_COMPANIA
                  AND ANO      = UN_ANIO
                  AND CODIGO   BETWEEN UN_CODIGOINICIAL AND UN_CODIGOFINAL
                GROUP BY COMPANIA, ANO, CODIGO, CENTRO_COSTO, TERCERO, SUCURSAL, AUXILIAR, REFERENCIA, FUENTE_RECURSO, NATURALEZA
                HAVING COUNT(COMPANIA)<>14)
      LOOP

        MI_CAMPOS  := 'COMPANIA, ANO, CODIGO, CENTRO_COSTO, TERCERO, SUCURSAL, AUXILIAR, REFERENCIA, FUENTE_RECURSO, NATURALEZA, MES';
        MI_VALORES := 'SELECT '''   || RS.COMPANIA 
                       || ''','   || RS.ANO 
                       ||   ',''' || RS.CODIGO   
                       || ''',''' || RS.CENTRO_COSTO 
                       || ''',''' || RS.TERCERO 
                       || ''',''' || RS.SUCURSAL 
                       || ''',''' || RS.AUXILIAR
                       || ''',''' || RS.REFERENCIA
                       || ''',''' || RS.FUENTE_RECURSO
                       || ''',''' || RS.NATURALEZA
                       || ''',       MES.NUMERO 
                        FROM MES 
                        WHERE COMPANIA = ''' || UN_COMPANIA || '''
                          AND ANO      = '   || UN_ANIO     || ' 
                        AND NUMERO NOT IN(
                                        SELECT MES
                                        FROM   SALDO_AUX_PPTAL
                                        WHERE  COMPANIA     =''' || RS.COMPANIA || '''
                                          AND  ANO          ='   || RS.ANO      || '
                                          AND  CODIGO       =''' || RS.CODIGO   || ''' 
                                          AND  CENTRO_COSTO =''' || RS.CENTRO_COSTO || '''
                                          AND  TERCERO      =''' || RS.TERCERO     || '''
                                          AND  SUCURSAL     =''' || RS.SUCURSAL    || ''' 
                                          AND  AUXILIAR     =''' || RS.AUXILIAR    || '''
                                          AND  REFERENCIA   =''' || RS.REFERENCIA  || ''' 
                                          AND  FUENTE_RECURSO =''' ||  RS.FUENTE_RECURSO || '''
                                        )';

              BEGIN
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'SALDO_AUX_PPTAL', 
                                                         UN_ACCION  => 'IS', 
                                                         UN_CAMPOS  => MI_CAMPOS, 
                                                         UN_VALORES => MI_VALORES);

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
             END;
      END LOOP;



      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;

    END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD   => SQLCODE,
        UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_LIMPIA_AUX_PPTAL
    );

  END PR_LIMPIA_SALDO_AUX_PPTAL;  

  FUNCTION FC_VALIDA_AFECTACIONES_PPTAL
   /*
      NAME              : 
      AUTHORS           : SYSMAN  SAS 
      AUTHOR MIGRACION  : José Pascual Gómez Blanco
      DATE MIGRADOR     : 31/08/2017
      TIME              : 05:44 PM
      SOURCE MODULE     : PRESUPUESTO
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : Función que valida si un comprobante tiene afectaciones
      MODIFICATIONS     : 
      PARAMETERS        : 

      @NAME:    
      @METHOD:  
    */
  (
    UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_ANO_AFECT           IN PCK_SUBTIPOS.TI_ANIO, 
    UN_TIPO_CPTE_AFECT     IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL, 
    UN_COMPROBANTE_AFECT   IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTEPPTAL
  )
  RETURN PCK_SUBTIPOS.TI_LOGICO
  AS
    MI_AFECTADOS PCK_SUBTIPOS.TI_ENTERO_LARGO DEFAULT 0;
  BEGIN
    SELECT NVL(COUNT(COMPANIA),0)
    INTO MI_AFECTADOS
    FROM COMPROBANTE_PPTALAFECTADOS
    WHERE COMPANIA          = UN_COMPANIA
      AND ANO_AFECT         = UN_ANO_AFECT
      AND TIPO_CPTE_AFECT   = UN_TIPO_CPTE_AFECT
      AND COMPROBANTE_AFECT = UN_COMPROBANTE_AFECT;
    RETURN MI_AFECTADOS;
  END;

 --18
PROCEDURE PR_CONTABILIZARCOMPPPTAL
  /*
      NAME              : PR_ACTCONFEQUIV
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 19/06/2018
      TIME              : 11:30 AM
      SOURCE MODULE     : SysmanCT2018.06.06
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : Procedimiento que realiza la contabilización del comprobante presupuestal                          
      MODIFICATIONS     : 

      @NAME: contabilizarComprobantePptal
    */
  (
  UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO          IN DETALLE_COMPROBANTE_CNT.ANO%TYPE,
  UN_TIPOCPTE      IN DETALLE_COMPROBANTE_CNT.TIPO_CPTE%TYPE,
  UN_COMPROBANTE   IN DETALLE_COMPROBANTE_CNT.COMPROBANTE%TYPE,
  /*UN_CUENTADEBITO  IN DETALLE_COMPROBANTE_PPTAL.CUENTA_DEBITO%TYPE,
  UN_CUENTACREDITO IN DETALLE_COMPROBANTE_PPTAL.CUENTA_CREDITO%TYPE,*/
  UN_USUARIO       IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS

  MI_CONTADOR      NUMBER(5,0);  
  MI_CAMPOS        PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES       PCK_SUBTIPOS.TI_VALORES; 
  MI_MSGERROR      PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN

 <<RECORER_DETALLES>> 
 FOR MI_RS IN (SELECT COMPANIA ,
                  ANO,
                  CUENTA,
                  TIPO_CPTE,
                  COMPROBANTE,
                  FECHA,
                  DESCRIPCION,
                  NRO_DOCUMENTO,
                  VALOR_DEBITO,
                  VALOR_CREDITO,
                  TERCERO,
                  SUCURSAL,
                  AUXILIAR,
                  CENTRO_COSTO,
                  FUENTE_RECURSO,
                  REFERENCIA,
                  CUENTA_CREDITO,
                  CUENTA_DEBITO
                FROM DETALLE_COMPROBANTE_PPTAL
                WHERE COMPANIA       = UN_COMPANIA
                  AND ANO            = UN_ANIO
                  AND TIPO_CPTE      = UN_TIPOCPTE
                  AND COMPROBANTE    = UN_COMPROBANTE
                  /*AND CUENTA_CREDITO = UN_CUENTACREDITO
                  AND CUENTA_DEBITO  = UN_CUENTADEBITO*/
                ORDER BY COMPANIA,
                  ANO,
                  TIPO_CPTE,
                  COMPROBANTE,
                  CONSECUTIVO
 )LOOP

  <<CREAR_DETALLES>>
  FOR MI_RS2 IN (  SELECT CUENTA_DEBITO,
                          CUENTA_CREDITO
                   FROM EQUIVALENTECNT_PRESUPUESTAL
                   WHERE COMPANIA       = UN_COMPANIA
                     AND ANO            = UN_ANIO
                     AND RUBRO_PPTAL    = MI_RS.CUENTA
                     AND CUENTA_CREDITO = MI_RS.CUENTA_CREDITO
                     AND CUENTA_DEBITO  = MI_RS.CUENTA_DEBITO
                     AND AUXILIAR       = MI_RS.AUXILIAR
                     AND REFERENCIA     = MI_RS.REFERENCIA
                     AND CENTRO_COSTO   = MI_RS.CENTRO_COSTO
                     AND FUENTE_RECURSO = MI_RS.FUENTE_RECURSO
    )LOOP

        --20180917_3635:@asana
        PCK_PRESUPUESTO.PR_TRANSAUTOMATICA( UN_COMPANIA        => MI_RS.COMPANIA,
                            UN_ANIO            => MI_RS.ANO,
                            UN_TIPOCPTE        => MI_RS.TIPO_CPTE,
                            UN_COMPROBANTE     => MI_RS.COMPROBANTE,
                            UN_FECHA           => MI_RS.FECHA,
                            UN_DESCRIPCION     => MI_RS.DESCRIPCION,
                            UN_NRO_DOCUMENTO   => MI_RS.NRO_DOCUMENTO,
                            UN_CUENTA_DEBITO   => MI_RS2.CUENTA_DEBITO,
                            UN_CUENTA_CREDITO  => MI_RS2.CUENTA_CREDITO,
                            UN_VALOR_DEBITO    => MI_RS.VALOR_DEBITO,
                            UN_VALOR_CREDITO   => MI_RS.VALOR_CREDITO,
                            UN_TERCERO         => MI_RS.TERCERO,
                            UN_SUCURSAL        => MI_RS.SUCURSAL,
                            UN_AUXILIAR        => MI_RS.AUXILIAR,
                            UN_CENTRO_COSTO    => MI_RS.CENTRO_COSTO,
                            UN_FUENTE_RECURSO  => MI_RS.FUENTE_RECURSO,
                            UN_REFERENCIA      => MI_RS.REFERENCIA,
                            UN_USUARIO         => UN_USUARIO);

    END LOOP CREAR_DETALLES;

 END LOOP RECORER_DETALLES;
END PR_CONTABILIZARCOMPPPTAL;


PROCEDURE PR_TRANSAUTOMATICA
  /*
      NAME              : PR_TRANSAUTOMATICA
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 20/06/2018
      TIME              : 12:20 PM
      SOURCE MODULE     : SysmanCT2018.06.06
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : Procedimiento que inserta las cuentas debitos y creditos equivalentes
                          en el detalle contable 
      MODIFICATIONS     : 

      @NAME: realizarTramsaccionAutomatica
    */
  (
  UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO            IN DETALLE_COMPROBANTE_CNT.ANO%TYPE,
  UN_TIPOCPTE        IN DETALLE_COMPROBANTE_CNT.TIPO_CPTE%TYPE,
  UN_COMPROBANTE     IN DETALLE_COMPROBANTE_CNT.COMPROBANTE%TYPE,
  UN_FECHA           IN DETALLE_COMPROBANTE_CNT.FECHA%TYPE,
  UN_DESCRIPCION     IN DETALLE_COMPROBANTE_CNT.DESCRIPCION%TYPE,
  UN_NRO_DOCUMENTO   IN DETALLE_COMPROBANTE_CNT.NRO_DOCUMENTO%TYPE,
  UN_CUENTA_DEBITO   IN DETALLE_COMPROBANTE_PPTAL.CUENTA_DEBITO%TYPE,
  UN_CUENTA_CREDITO  IN DETALLE_COMPROBANTE_PPTAL.CUENTA_CREDITO%TYPE,
  UN_VALOR_DEBITO    IN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO%TYPE,
  UN_VALOR_CREDITO   IN DETALLE_COMPROBANTE_CNT.VALOR_CREDITO%TYPE,
  UN_TERCERO         IN DETALLE_COMPROBANTE_CNT.TERCERO%TYPE,
  UN_SUCURSAL        IN DETALLE_COMPROBANTE_CNT.SUCURSAL%TYPE,
  UN_AUXILIAR        IN DETALLE_COMPROBANTE_CNT.AUXILIAR%TYPE,
  UN_CENTRO_COSTO    IN DETALLE_COMPROBANTE_CNT.CENTRO_COSTO%TYPE,
  UN_FUENTE_RECURSO  IN DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO%TYPE,
  UN_REFERENCIA      IN DETALLE_COMPROBANTE_CNT.REFERENCIA%TYPE,
  UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS

  MI_CONSECUTIVO   NUMBER(5,0);  
  MI_CAMPOS        PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES       PCK_SUBTIPOS.TI_VALORES; 
  MI_MSGERROR      PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_NATURALEZA    DETALLE_COMPROBANTE_CNT.NATURALEZA%TYPE;

BEGIN


  MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'DETALLE_COMPROBANTE_CNT',
                                                     UN_CRITERIO => 'COMPANIA ='''||UN_COMPANIA||''' AND ANO='||UN_ANIO||'  AND TIPO_CPTE='''||UN_TIPOCPTE||''' AND COMPROBANTE= '||UN_COMPROBANTE||'' ,
                                                     UN_CAMPO    => 'CONSECUTIVO');

  MI_NATURALEZA :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => UN_CUENTA_DEBITO);


  MI_CAMPOS := 'COMPANIA,
                ANO,
                TIPO_CPTE,
                COMPROBANTE,
                CONSECUTIVO,
                CUENTA,
                FECHA,
                NATURALEZA,
                VALOR_DEBITO,
                VALOR_CREDITO,
                BASE_GRAVABLE,
                CENTRO_COSTO,
                FUENTE_RECURSO,
                TERCERO,
                SUCURSAL,
                AUXILIAR,
                REFERENCIA,
                DESCRIPCION,
                EJECUCION_DEBITO,
                EJECUCION_CREDITO,
                BASE_IVA,
                NRO_DOCUMENTO,
                DATE_CREATED,
                CREATED_BY';


  MI_VALORES := ' '''||UN_COMPANIA||''',
                  '||UN_ANIO||' ,
                  '''||UN_TIPOCPTE||''',
                  '||UN_COMPROBANTE||',
                  '||MI_CONSECUTIVO||',
                  '||UN_CUENTA_DEBITO||',
                  '''||UN_FECHA||''' ,
                  '''||MI_NATURALEZA||''',
                  '||UN_VALOR_DEBITO||',
                  '||UN_VALOR_CREDITO||',
                   0,
                   '''||UN_CENTRO_COSTO||''' ,
                   '''||UN_FUENTE_RECURSO||''' ,
                   '''||UN_TERCERO||''',
                   '''||UN_SUCURSAL||''',
                   '''||UN_AUXILIAR||''',                   
                   '''||UN_REFERENCIA||''',
                   '''||UN_DESCRIPCION||''',
                   '||UN_VALOR_DEBITO||',
                   '||UN_VALOR_CREDITO||',
                   0,
                  '''||UN_NRO_DOCUMENTO||''',
                  SYSDATE,
                  '''||UN_USUARIO||''' ' ;                  
    BEGIN                           
      BEGIN
         PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       =>'DETALLE_COMPROBANTE_CNT',
                                                    UN_ACCION      =>'I', 
                                                    UN_CAMPOS  =>MI_CAMPOS, 
                                                    UN_VALORES =>MI_VALORES
                                                    );

            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                        MI_MSGERROR(1).CLAVE := 'COMPROBANTE';
                        MI_MSGERROR(1).VALOR := UN_COMPROBANTE;
                        MI_MSGERROR(2).CLAVE := 'TIPO';
                        MI_MSGERROR(2).VALOR := UN_TIPOCPTE;


                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_CONTAB_INSDETALLE
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );     
      END;   



   MI_NATURALEZA :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => UN_CUENTA_CREDITO);

   MI_CONSECUTIVO := MI_CONSECUTIVO + 1; 

    MI_CAMPOS := 'COMPANIA,
                  ANO,
                  TIPO_CPTE,
                  COMPROBANTE,
                  CONSECUTIVO,
                  CUENTA,
                  FECHA,
                  NATURALEZA,
                  VALOR_DEBITO,
                  VALOR_CREDITO,
                  BASE_GRAVABLE,
                  CENTRO_COSTO,
                  FUENTE_RECURSO,
                  TERCERO,
                  SUCURSAL,
                  AUXILIAR,
                  REFERENCIA,
                  DESCRIPCION,
                  EJECUCION_DEBITO,
                  EJECUCION_CREDITO,
                  BASE_IVA,
                  NRO_DOCUMENTO,
                  DATE_CREATED,
                  CREATED_BY';


MI_VALORES := ' '''||UN_COMPANIA||''',
                    '||UN_ANIO||' ,
                    '''||UN_TIPOCPTE||''',
                    '||UN_COMPROBANTE||',
                    '||MI_CONSECUTIVO||',
                    '||UN_CUENTA_CREDITO||',
                    '''||UN_FECHA||''' ,
                    '''||MI_NATURALEZA||''',
                    '||UN_VALOR_CREDITO||',
                    '||UN_VALOR_DEBITO||',                   
                     0,
                     '''||UN_CENTRO_COSTO||''' ,
                     '''||UN_FUENTE_RECURSO||''' ,
                     '''||UN_TERCERO||''',
                      '''||UN_SUCURSAL||''',
                      '''||UN_AUXILIAR||''',                    
                     '''||UN_REFERENCIA||''',
                     '''||UN_DESCRIPCION||''',
                     '||UN_VALOR_CREDITO||',
                     '||UN_VALOR_DEBITO||',
                     0,
                    '''||UN_NRO_DOCUMENTO||''',
                    SYSDATE,
                    '''||UN_USUARIO||''' ' ;                   
    BEGIN                           
      BEGIN
         PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       =>'DETALLE_COMPROBANTE_CNT',
                                                    UN_ACCION      =>'I', 
                                                    UN_CAMPOS  =>MI_CAMPOS, 
                                                    UN_VALORES =>MI_VALORES
                                                    );

            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                        MI_MSGERROR(1).CLAVE := 'COMPROBANTE';
                        MI_MSGERROR(1).VALOR := UN_COMPROBANTE;
                        MI_MSGERROR(2).CLAVE := 'TIPO';
                        MI_MSGERROR(2).VALOR := UN_TIPOCPTE;


                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_CONTAB_INSDETALLE
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );     
      END;
    END PR_TRANSAUTOMATICA;
END PCK_PRESUPUESTO;