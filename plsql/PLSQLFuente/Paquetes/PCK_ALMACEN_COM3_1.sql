create or replace PACKAGE BODY PCK_ALMACEN_COM3 AS

  -- 1
  FUNCTION FC_KARDEXELEMENTO
    /*
      NAME              : FC_KARDEXELEMENTO En Access --> KardexElemento
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRIGUEZ
      DATE MIGRADOR     : 03/02/2016
      TIME              : 10:00 AM
      MODIFIER          : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MODIFIED     : 13/01/2017
      MODIFIER          : YESSICA SANA ROJAS
      DATE MODIFIED     : 05/04/2018
      TIME              : 04:00 PM

      SOURCE MODULE     : ALMACEN
      DESCRIPTION       : FUNCION EN LA QUE ANALIZAN LAS INCONSISTENCIAS EN SALDOS DE KARDEX.
      PARAMETERS        : UN_COMPANIA       => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_TIPOMOVIMIENTO => TIPO DE MOVIMIENTO.
                          UN_MOVIMIENTO     => MOVIMIENTO.
                          UN_ELEMENTO       => ELEMENTO IMPLICADO DEL ULTIMO MOVIMIENTO.
                          UN_CODIGO         => CODIGO DEL MOVIMIENTO
                          UN_ACTPROMEDIO    => PROMEDIO ACTUAL
                          UN_ANO            => ANIO DEL QUE SE QUIEREN ANALIZAR LAS INCONSISTENCIAS.
                          UN_MES            => MES DEL QUE SE QUIEREN ANALIZAR LAS INCONSISTENCIAS.
      MODIFICATION      : CORRECCION SEGUN ESTANDAR DE PROGRAMACION, REFERENCIACION DE FUNCIONES Y OPTIMIZACION
                          DE MANEJO DE ERRORES.
      MODIFICACION      : SE REALIZA ASIGNACION DE VALOR A PR_SETFECHAHORA() PARA QUE MUESTRE MOVIMIENTOS ANTERIORES Y ASI 
                          OBTENER LOS DATOS DE CANTIDAD QUE SE ACTUALIZAN EN TABLA INVENTARIO Y D_MOVIMIENTO
    */
  (
    UN_COMPANIA         IN  PCK_SUBTIPOS.TI_COMPANIA
   ,UN_TIPOMOVIMIENTO   IN  D_MOVIMIENTO.TIPOMOVIMIENTO%TYPE
   ,UN_MOVIMIENTO       IN  D_MOVIMIENTO.MOVIMIENTO%TYPE
   ,UN_ELEMENTO         IN  PCK_SUBTIPOS.TI_ELEMENTO
   ,UN_CODIGO           IN  D_MOVIMIENTO.CODIGO%TYPE
   ,UN_ACTPROMEDIO      IN  VARCHAR2
   ,UN_ANO              IN  PCK_SUBTIPOS.TI_ANIO DEFAULT NULL
   ,UN_MES              IN  PCK_SUBTIPOS.TI_MES  DEFAULT NULL
  )
    RETURN CLOB
  AS
    MI_TABLA                  PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
    MI_HORA                   DATE;
    MI_FECHA                  DATE;
    MI_FECHA1                 DATE;
    MI_PROMEDIO               PCK_SUBTIPOS.TI_DOBLE;
    MI_PROMEDIO1              PCK_SUBTIPOS.TI_DOBLE;
    MI_AJUST                  PCK_SUBTIPOS.TI_DOBLE;
    MI_AJUSTANT               PCK_SUBTIPOS.TI_DOBLE;
    MI_CANTIDAD               PCK_SUBTIPOS.TI_DOBLE;
    MI_VALORSALDO             PCK_SUBTIPOS.TI_DOBLE;
    MI_CANTIDADANT            PCK_SUBTIPOS.TI_DOBLE;
    MI_VALORSALDOANT          PCK_SUBTIPOS.TI_DOBLE;
    MI_ANIO                   PCK_SUBTIPOS.TI_ANIO;
    MI_MES                    PCK_SUBTIPOS.TI_MES;
    MI_RTA                    CLOB;
    MI_RTA1                   CLOB;
    MI_AUX_TEXTO              VARCHAR2(4000 CHAR);
    MI_AUX_TEXTOERROR         CLOB;  
    MI_VLR1                   PCK_SUBTIPOS.TI_DOBLE;
    MI_VLR2                   PCK_SUBTIPOS.TI_DOBLE;
    MI_MESFINAL               PCK_SUBTIPOS.TI_MES;
    MI_MESINICIAL             PCK_SUBTIPOS.TI_MES;
    MI_VALORSALDONEG          PCK_SUBTIPOS.TI_DOBLE;
    MI_RSCLON_COSTOSALIDAAJ   PCK_SUBTIPOS.TI_DOBLE;
    MI_CODIGOELEMENTO         PCK_SUBTIPOS.TI_ELEMENTO;
    MI_EXISTENCIA             INVENTARIO.EXISTENCIA%TYPE;
    MI_VALORTOTAL             INVENTARIO.VALORTOTAL%TYPE;
    MI_VLRUNITARIOPROM        INVENTARIO.VLRUNITARIOPROM%TYPE;
    MI_DBLOTROVALOR           PCK_SUBTIPOS.TI_DOBLE;
    MI_DBLVALOR               PCK_SUBTIPOS.TI_DOBLE;
    MI_RSCLON_VALORTOTAL      PCK_SUBTIPOS.TI_DOBLE;
    MI_RSCLON_VLRUNITARIOPROM PCK_SUBTIPOS.TI_DOBLE;
    MI_RSCLON_VLRAJUSTADO     PCK_SUBTIPOS.TI_DOBLE;
    MI_RSCLON_SALDOKARDEX     PCK_SUBTIPOS.TI_DOBLE;
    MI_RSCLON_VALORSALDO      PCK_SUBTIPOS.TI_DOBLE;
    MI_RSCLON_COSTOSALIDA     PCK_SUBTIPOS.TI_DOBLE;
    MI_FECHA_CONCT            VARCHAR2(20 CHAR);
    MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR;
    --MI_FECHAULTIMOMOV         VARCHAR2(20 CHAR);
    MI_FECHAULTIMOMOV         DATE;
    MI_CONSECULTIMO           PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_FECHACONSECUTIVO       DATE;
    MI_MANAUXILIARES          PCK_SUBTIPOS.TI_PARAMETRO;
    MI_ENTRADA_ELM            PCK_SUBTIPOS.TI_DOBLE;
    MI_SALIDA_ELM             PCK_SUBTIPOS.TI_DOBLE;
    MI_SALDO_ELM              PCK_SUBTIPOS.TI_DOBLE;
    MI_EXISTENCIA_ELM         PCK_SUBTIPOS.TI_DOBLE;
  BEGIN
	MI_MANAUXILIARES := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA,
												  UN_NOMBRE => 'MANEJA SALDO POR BODEGA Y AUXILIARES EN ALMACEN',
												  UN_MODULO => PCK_DATOS.MODULOALMACEN,
												  UN_FECHA_PAR => SYSDATE),'NO');
                                                  
    MI_AUX_TEXTO := 'INCONSISTENCIAS EN SALDOS DE KARDEX. NO DEBEN QUEDAR NEGATIVOS. POR FAVOR REVISE' || CHR(10) || CHR(10);
    MI_AUX_TEXTO := MI_AUX_TEXTO || 'Deben corregirse todos de lo contrario no se incluirán en el informe de Cuenta Almacén.' || CHR(10);
    MI_RTA       := TO_CLOB(MI_AUX_TEXTO || '--------------------------------------------------------------------------------' || CHR(10));
    BEGIN
      SELECT  TO_DATE(TO_CHAR(TO_DATE(TO_CHAR(D_MOVIMIENTO.FECHA,'DD/MM/RRRR'),'DD/MM/RRRR')
                            + NUMTODSINTERVAL(TO_CHAR(D_MOVIMIENTO.HORA,'hh24'),'HOUR')
                            + NUMTODSINTERVAL(TO_CHAR(D_MOVIMIENTO.HORA,'mi'),'MINUTE')
                            + NUMTODSINTERVAL(TO_CHAR(D_MOVIMIENTO.HORA,'ss'),'SECOND'), 'DD/MM/YYYY hh24:mi:ss')
                     ,'DD/MM/RRRR hh24:mi:ss')
             ,TO_DATE(TO_CHAR(D_MOVIMIENTO.FECHA,'DD/MM/RRRR'),'DD/MM/RRRR') FECHA
             ,HORA
      INTO    MI_FECHA_CONCT
             ,MI_FECHA
             ,MI_HORA
      FROM    D_MOVIMIENTO
      WHERE   COMPANIA       =      UN_COMPANIA
        AND   TIPOMOVIMIENTO =      UN_TIPOMOVIMIENTO
        AND   MOVIMIENTO     =      UN_MOVIMIENTO
        AND   CODIGO         =      UN_CODIGO
        AND   IND_REG        Not In (0)
        AND   ROWNUM = 1;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        RETURN '0';
    END;

    BEGIN
    
    /*
    MI_FECHAULTIMOMOV := TO_DATE(TO_CHAR(MI_FECHA
                            + NUMTODSINTERVAL(TO_CHAR(MI_HORA,'hh24'),'HOUR')
                            + NUMTODSINTERVAL(TO_CHAR(MI_HORA,'mi'),'MINUTE')
                            + NUMTODSINTERVAL(TO_CHAR(MI_HORA,'ss'),'SECOND'), 'DD/MM/YYYY hh24:mi:ss')
                     ,'DD/MM/YYYY hh24:mi:ss');   
    MI_FECHAULTIMOMOV :=TO_DATE(MI_FECHAULTIMOMOV, 'DD/MM/YYYY HH24:MI:SS') - NUMTODSINTERVAL(1, 'SECOND');
    */
    MI_FECHAULTIMOMOV := MI_FECHA
                            + NUMTODSINTERVAL(TO_CHAR(MI_HORA,'hh24'),'HOUR')
                            + NUMTODSINTERVAL(TO_CHAR(MI_HORA,'mi'),'MINUTE')
                            + NUMTODSINTERVAL(TO_CHAR(MI_HORA,'ss'),'SECOND');
    MI_FECHAULTIMOMOV :=MI_FECHAULTIMOMOV - NUMTODSINTERVAL(1, 'SECOND');
                            
                     
    
    
   PCK_ENTORNO.PR_SETFECHAHORA(UN_FECHAHORA =>MI_FECHAULTIMOMOV);
   PCK_ENTORNO.PR_SETCOMPANIA (UN_COMPANIA   => UN_COMPANIA)  ;
   END;
    BEGIN
       WITH MOVIMIENTOS AS (
            SELECT 
                M.COMPANIA,
                M.ELEMENTO,
                M.SALDOKARDEX,
                M.VLRUNITARIOPROM,
                M.VLRAJUSTADO,
                M.FECHA,
                M.VALORSALDO,
                CAST(M.FECHA AS TIMESTAMP) + NUMTODSINTERVAL(TO_NUMBER(TO_CHAR(M.HORA, 'HH24')), 'HOUR') +
                    NUMTODSINTERVAL(TO_NUMBER(TO_CHAR(M.HORA, 'MI')), 'MINUTE') +
                    NUMTODSINTERVAL(TO_NUMBER(TO_CHAR(M.HORA, 'SS')), 'SECOND') AS FECHA_HORA_COMPLETA
            FROM D_MOVIMIENTO M
            INNER JOIN TIPOMOVIMIENTO TM
                ON M.COMPANIA = TM.COMPANIA AND M.TIPOMOVIMIENTO = TM.CODIGO
            WHERE M.COMPANIA = PCK_ENTORNO.FC_GETCOMPANIA AND
                (TM.CONCEPTO NOT IN ('T', 'DS', 'L', 'DT') OR TM.CLASE NOT IN ('T', 'D')) AND M.IND_REG <> 0),
        ULTIMO_MOV AS (
            SELECT COMPANIA, ELEMENTO, MAX(FECHA_HORA_COMPLETA) AS MAX_FECHA_HORA
            FROM MOVIMIENTOS
            WHERE FECHA_HORA_COMPLETA <= MI_FECHAULTIMOMOV
            GROUP BY COMPANIA, ELEMENTO
        )
        SELECT 
            M.SALDOKARDEX,
            M.VLRUNITARIOPROM,
            M.VLRAJUSTADO,
            M.FECHA,
            M.VALORSALDO
        INTO 
            MI_CANTIDAD,
            MI_PROMEDIO,
            MI_AJUST,
            MI_FECHA1,
            MI_VALORSALDO
        FROM MOVIMIENTOS M
        JOIN ULTIMO_MOV U
            ON M.COMPANIA = U.COMPANIA 
           AND M.ELEMENTO = U.ELEMENTO 
           AND M.FECHA_HORA_COMPLETA = U.MAX_FECHA_HORA
        WHERE M.COMPANIA = UN_COMPANIA 
          AND M.ELEMENTO = UN_ELEMENTO 
          AND ROWNUM = 1;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_PROMEDIO      := 0;
        MI_AJUST         := 0;
        MI_CANTIDAD      := 0;
        MI_MES           := 0;
        MI_ANIO          := 0;
        MI_AJUSTANT      := 0;
        MI_CANTIDADANT   := 0;
        MI_VALORSALDO    := 0;
        MI_VALORSALDOANT := 0;
    END;

    MI_RSCLON_VALORTOTAL := 0;
    MI_PROMEDIO      := NVL(MI_PROMEDIO,0);
    MI_PROMEDIO1     := NVL(MI_PROMEDIO,0);
    MI_AJUST         := NVL(MI_AJUST,0);
    MI_VALORSALDO    := NVL(MI_VALORSALDO,0);
    MI_CANTIDAD      := PCK_SYSMAN_UTL.FC_ROUND(NVL(MI_CANTIDAD,0),6);
    MI_CANTIDADANT   := MI_CANTIDAD;
    MI_VALORSALDOANT := MI_VALORSALDO;
    MI_ANIO := EXTRACT(YEAR FROM MI_FECHA1);

    IF MI_FECHA1 IS NULL THEN
      MI_MES:=0;
    ELSE
      MI_MES := EXTRACT(MONTH FROM MI_FECHA1);
    END IF;
      MI_AJUSTANT      :=MI_AJUST;
      MI_CANTIDADANT   :=MI_CANTIDAD;
      MI_VALORSALDOANT :=MI_VALORSALDO;
    <<MOVIMIENTOS>>
    FOR RSD_MOVIMIENTO IN (
      SELECT   INVENTARIO.TIPO
              ,INVENTARIO.VLRUNITARIOPROM
              ,INVENTARIO.VALORTOTAL VT
              ,TIPOMOVIMIENTO.CLASE
              ,TIPOMOVIMIENTO.COSTEA
              ,INCREMENAJUS
              ,D_MOVIMIENTO.TIPOMOVIMIENTO
              ,D_MOVIMIENTO.MOVIMIENTO
              ,D_MOVIMIENTO.CODIGO
              ,D_MOVIMIENTO.FECHA
              ,D_MOVIMIENTO.VLRAJUSTADO
              ,D_MOVIMIENTO.SALDOKARDEX
              ,D_MOVIMIENTO.CANTIDAD
              ,D_MOVIMIENTO.COSTOSALIDA
              ,D_MOVIMIENTO.VALORSALDO
              ,D_MOVIMIENTO.VALORUNITARIO
              ,MOVIMIENTO.DEPENDENCIA_DESTINO
              ,D_MOVIMIENTO.VALORTOTAL
              ,D_MOVIMIENTO.COSTOSALIDAAJ
              ,D_MOVIMIENTO.ELEMENTO
              ,TIPOMOVIMIENTO.CONCEPTO
              ,MOVIMIENTO.CLASE_BODEGA_ORIGEN
              ,MOVIMIENTO.CLASE_BODEGA_DESTINO
      FROM     D_MOVIMIENTO
        INNER JOIN TIPOMOVIMIENTO
           ON      D_MOVIMIENTO.TIPOMOVIMIENTO = TIPOMOVIMIENTO.CODIGO
          AND      D_MOVIMIENTO.COMPANIA       = TIPOMOVIMIENTO.COMPANIA
        INNER JOIN INVENTARIO
           ON      D_MOVIMIENTO.COMPANIA = INVENTARIO.COMPANIA
          AND      D_MOVIMIENTO.ELEMENTO = INVENTARIO.CODIGOELEMENTO
        INNER JOIN V_MOVIMIENTO MOVIMIENTO
           ON      D_MOVIMIENTO.COMPANIA       = MOVIMIENTO.COMPANIA
          AND      D_MOVIMIENTO.TIPOMOVIMIENTO = MOVIMIENTO.TIPOMOVIMIENTO
          AND      D_MOVIMIENTO.MOVIMIENTO     = MOVIMIENTO.NUMERO
      WHERE    D_MOVIMIENTO.COMPANIA                  = UN_COMPANIA
        AND    ELEMENTO                               = UN_ELEMENTO
        AND    D_MOVIMIENTO.IND_REG                      NOT IN(0)
       AND (TO_CHAR(D_MOVIMIENTO.FECHA,'RRRR/MM/DD') > TO_CHAR(MI_FECHA,'RRRR/MM/DD') 
       OR (TO_CHAR(D_MOVIMIENTO.FECHA,'RRRR/MM/DD') = TO_CHAR(MI_FECHA,'RRRR/MM/DD') 
       AND  TO_CHAR(D_MOVIMIENTO.HORA,'HH24:MI:SS') >= TO_CHAR(MI_HORA,'HH24:MI:SS')))
      ORDER BY TO_CHAR(D_MOVIMIENTO.FECHA,'RRRR/MM/DD')
              ,TO_CHAR(D_MOVIMIENTO.HORA,'hh24:mi:ss')
              ,CLASE
              ,D_MOVIMIENTO.TIPOMOVIMIENTO
              ,MOVIMIENTO
              ,D_MOVIMIENTO.CODIGO)
    LOOP


      MI_RSCLON_VALORTOTAL := NVL(RSD_MOVIMIENTO.VALORTOTAL, 0);
      MI_AJUST := MI_PROMEDIO;

      IF RSD_MOVIMIENTO.CLASE = 'E' AND RSD_MOVIMIENTO.CONCEPTO = 'CR' AND RSD_MOVIMIENTO.CLASE_BODEGA_DESTINO NOT IN ('20') THEN 

        MI_RSCLON_VLRUNITARIOPROM := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_PROMEDIO,
                                                             UN_PRECISION => 2);
        MI_RSCLON_VLRAJUSTADO     := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_PROMEDIO,
                                                             UN_PRECISION => 2);
        MI_RSCLON_SALDOKARDEX     := MI_CANTIDAD;
        MI_RSCLON_VALORSALDO      := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_VALORSALDO,
                                                             UN_PRECISION => 2);
        MI_ANIO := EXTRACT(YEAR FROM RSD_MOVIMIENTO.FECHA);
        MI_MES := EXTRACT(MONTH FROM RSD_MOVIMIENTO.FECHA);
        -- SE PUSO CONTROL PARA VERIFICAR SI HAY NECESIDAD DE HACER EL CAMIO PARA AGILIZAR, POR HPV EN MARZO 6 DE 2008
        IF RSD_MOVIMIENTO.VLRUNITARIOPROM <> MI_RSCLON_VLRUNITARIOPROM OR RSD_MOVIMIENTO.VLRAJUSTADO <> MI_RSCLON_VLRAJUSTADO OR RSD_MOVIMIENTO.SALDOKARDEX <> MI_RSCLON_SALDOKARDEX OR RSD_MOVIMIENTO.VALORSALDO <> MI_RSCLON_VALORSALDO THEN
          BEGIN
            BEGIN
              MI_TABLA       :='D_MOVIMIENTO';
              MI_CAMPOS      := ' D_MOVIMIENTO.VLRUNITARIOPROM  = ' || MI_RSCLON_VLRUNITARIOPROM ||
                                ',D_MOVIMIENTO.VLRAJUSTADO  = ' || MI_RSCLON_VLRAJUSTADO ||
                                ',D_MOVIMIENTO.SALDOKARDEX  = ' || MI_RSCLON_SALDOKARDEX ||
                                ',D_MOVIMIENTO.VALORSALDO  = ' || MI_RSCLON_VALORSALDO;

              MI_CONDICION   := ' D_MOVIMIENTO.COMPANIA=''' || UN_COMPANIA || '''
                            AND D_MOVIMIENTO.TIPOMOVIMIENTO = ''' || RSD_MOVIMIENTO.TIPOMOVIMIENTO || '''
                            AND D_MOVIMIENTO.MOVIMIENTO = ' || RSD_MOVIMIENTO.MOVIMIENTO || '
                            AND D_MOVIMIENTO.CODIGO = ' || RSD_MOVIMIENTO.CODIGO;
              PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                   UN_ACCION    => 'M',
                                                   UN_CAMPOS    => MI_CAMPOS,
                                                   UN_CONDICION => MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD   => SQLCODE,
                UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_INSERMOVIMIENTO
              );
          END;
        END IF;
      ELSE
        IF RSD_MOVIMIENTO.CLASE = 'E' THEN
          IF (RSD_MOVIMIENTO.CANTIDAD+MI_CANTIDAD) <> 0 THEN
            IF RSD_MOVIMIENTO.COSTEA <> 0 THEN
              IF MI_CANTIDAD + RSD_MOVIMIENTO.CANTIDAD <> 0 THEN
                MI_PROMEDIO := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => (MI_CANTIDAD * MI_PROMEDIO + RSD_MOVIMIENTO.CANTIDAD * RSD_MOVIMIENTO.VALORUNITARIO) / (MI_CANTIDAD + RSD_MOVIMIENTO.CANTIDAD),
                                                       UN_PRECISION => 2);
              ELSIF RSD_MOVIMIENTO.CANTIDAD <> 0 THEN
                MI_PROMEDIO := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => (MI_CANTIDAD * MI_PROMEDIO + RSD_MOVIMIENTO.VALORUNITARIO / MI_CANTIDAD) / MI_CANTIDAD,
                                                       UN_PRECISION => 2);
              END IF;
              IF MI_CANTIDAD + RSD_MOVIMIENTO.CANTIDAD <> 0  THEN
                MI_AJUST := (((MI_CANTIDAD * MI_AJUST + RSD_MOVIMIENTO.CANTIDAD * RSD_MOVIMIENTO.VALORUNITARIO) / (MI_CANTIDAD + RSD_MOVIMIENTO.CANTIDAD)) * 100) / 100;
              ELSE
                MI_AJUST := (((MI_CANTIDAD * MI_AJUST + RSD_MOVIMIENTO.VALORUNITARIO / MI_CANTIDAD) / MI_CANTIDAD) * 100) / 100;
              END IF;
              MI_CANTIDAD := MI_CANTIDAD + RSD_MOVIMIENTO.CANTIDAD;
              MI_CANTIDAD := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_CANTIDAD,
                                                     UN_PRECISION => 6);
              IF RSD_MOVIMIENTO.TIPO <> 'C' THEN
                MI_VALORSALDO := MI_VALORSALDO + NVL(RSD_MOVIMIENTO.VALORTOTAL, 0);
              ELSE
                MI_VALORSALDO := MI_PROMEDIO * MI_CANTIDAD;
              END IF;
            ELSE
              MI_CANTIDAD := MI_CANTIDAD + RSD_MOVIMIENTO.CANTIDAD;
              MI_CANTIDAD := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_CANTIDAD,
                                                     UN_PRECISION => 6);
              MI_VALORSALDONEG := MI_VALORSALDO;
              MI_VALORSALDO := MI_VALORSALDO + NVL(RSD_MOVIMIENTO.VALORTOTAL, 0);
              IF MI_CANTIDAD = 0 OR MI_VALORSALDONEG < 0 THEN
                MI_PROMEDIO := NVL(RSD_MOVIMIENTO.VALORUNITARIO, 0);
              ELSE
                IF MI_VALORSALDO > 0 OR MI_PROMEDIO = 0 THEN
                  MI_PROMEDIO := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => ABS(MI_VALORSALDO) / ABS(MI_CANTIDAD),
                                                         UN_PRECISION => 2);
                END IF;
              END IF;
            END IF;
          ELSE
            MI_PROMEDIO := RSD_MOVIMIENTO.VALORUNITARIO;
            MI_AJUST := RSD_MOVIMIENTO.VALORUNITARIO;
            MI_CANTIDAD := MI_CANTIDAD + RSD_MOVIMIENTO.CANTIDAD;
            MI_CANTIDAD := PCK_SYSMAN_UTL.FC_ROUND(MI_CANTIDAD,6);
            MI_VALORSALDO := MI_VALORSALDO + NVL(RSD_MOVIMIENTO.VALORTOTAL,0);
          END IF;
        ELSE
          MI_CANTIDAD := MI_CANTIDAD - RSD_MOVIMIENTO.CANTIDAD;
          MI_CANTIDAD := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_CANTIDAD,
                                                 UN_PRECISION => 6);
          IF UN_ACTPROMEDIO = 'SI' AND RSD_MOVIMIENTO.CLASE = 'S' AND RSD_MOVIMIENTO.TIPO = 'C' THEN
            IF MI_CANTIDAD <> 0 THEN
              MI_RSCLON_VALORTOTAL := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_PROMEDIO * RSD_MOVIMIENTO.CANTIDAD,
                                                              UN_PRECISION => 2);
            ELSE
              IF RSD_MOVIMIENTO.CANTIDAD <> 0 THEN
                MI_RSCLON_VALORTOTAL := MI_VALORSALDO;
              ELSE
                MI_RSCLON_VALORTOTAL:= 0;
              END IF;
            END IF;
          END IF;
          IF UN_ACTPROMEDIO = 'SI' AND RSD_MOVIMIENTO.COSTEA <> 0 AND RSD_MOVIMIENTO.TIPO = 'C' THEN
            MI_VALORSALDO := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_PROMEDIO * MI_CANTIDAD,
                                                     UN_PRECISION => 2);
            MI_RSCLON_COSTOSALIDA := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => RSD_MOVIMIENTO.CANTIDAD * MI_PROMEDIO,
                                                             UN_PRECISION => 2);
          ELSE
            MI_VALORSALDO := MI_VALORSALDO - MI_RSCLON_VALORTOTAL;
            MI_RSCLON_COSTOSALIDA := MI_RSCLON_VALORTOTAL;
          END IF;
          MI_RSCLON_COSTOSALIDAAJ := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => RSD_MOVIMIENTO.CANTIDAD * MI_AJUST,
                                                             UN_PRECISION => 2);
          IF RSD_MOVIMIENTO.COSTOSALIDA <> MI_RSCLON_COSTOSALIDA OR RSD_MOVIMIENTO.COSTOSALIDAAJ<> MI_RSCLON_COSTOSALIDAAJ THEN
            BEGIN
              BEGIN
                MI_TABLA := 'D_MOVIMIENTO';
                MI_CAMPOS := ' D_MOVIMIENTO.COSTOSALIDA    = ' || MI_RSCLON_COSTOSALIDA ||
                             ',D_MOVIMIENTO.COSTOSALIDAAJ  = ' || MI_RSCLON_COSTOSALIDAAJ;
                MI_CONDICION := '   D_MOVIMIENTO.COMPANIA       ='''  || UN_COMPANIA || '''
                                AND D_MOVIMIENTO.TIPOMOVIMIENTO = ''' || RSD_MOVIMIENTO.TIPOMOVIMIENTO || '''
                                AND D_MOVIMIENTO.MOVIMIENTO     = '   || RSD_MOVIMIENTO.MOVIMIENTO || '
                                AND D_MOVIMIENTO.CODIGO         = '   || RSD_MOVIMIENTO.CODIGO;
                PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                     UN_ACCION    => 'M',
                                                     UN_CAMPOS    => MI_CAMPOS,
                                                     UN_CONDICION => MI_CONDICION);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                MI_MSGERROR(1).CLAVE := 'COSTOSALIDA';
                MI_MSGERROR(1).VALOR := MI_RSCLON_COSTOSALIDA;
                MI_MSGERROR(2).CLAVE := 'COSTOSALIDAAJ';
                MI_MSGERROR(2).CLAVE := MI_RSCLON_COSTOSALIDAAJ;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD   => SQLCODE,
                  UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_ACTUAMOVIMIENTO,
                  UN_REEMPLAZOS => MI_MSGERROR
                );
            END;
          END IF;
        END IF;
        MI_RSCLON_VLRUNITARIOPROM := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_PROMEDIO,
                                                             UN_PRECISION => 2);
        MI_RSCLON_VLRAJUSTADO := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_AJUST,
                                                         UN_PRECISION => 2);
        MI_RSCLON_SALDOKARDEX := MI_CANTIDAD;
        MI_RSCLON_VALORSALDO := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_VALORSALDO,
                                                        UN_PRECISION => 2);
        IF RSD_MOVIMIENTO.VLRUNITARIOPROM <> MI_RSCLON_VLRUNITARIOPROM OR RSD_MOVIMIENTO.VLRAJUSTADO <> MI_RSCLON_VLRAJUSTADO OR RSD_MOVIMIENTO.SALDOKARDEX <> MI_RSCLON_SALDOKARDEX OR RSD_MOVIMIENTO.VALORSALDO <> MI_RSCLON_VALORSALDO THEN
          BEGIN
            BEGIN
              MI_TABLA :=' D_MOVIMIENTO ';
              MI_CAMPOS := ' D_MOVIMIENTO.VLRUNITARIOPROM  = ' || MI_RSCLON_VLRUNITARIOPROM ||
                         ',D_MOVIMIENTO.VLRAJUSTADO  = ' || MI_RSCLON_VLRAJUSTADO ||
                         ',D_MOVIMIENTO.SALDOKARDEX  = ' || MI_RSCLON_SALDOKARDEX ||
                         ',D_MOVIMIENTO.VALORSALDO  = ' || MI_RSCLON_VALORSALDO;

              MI_CONDICION := ' D_MOVIMIENTO.COMPANIA=''' || UN_COMPANIA || '''
                          AND D_MOVIMIENTO.TIPOMOVIMIENTO = ''' || RSD_MOVIMIENTO.TIPOMOVIMIENTO || '''
                          AND D_MOVIMIENTO.MOVIMIENTO = ' || RSD_MOVIMIENTO.MOVIMIENTO || '
                          AND D_MOVIMIENTO.CODIGO = ' || RSD_MOVIMIENTO.CODIGO ;
              PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                   UN_ACCION    => 'M',
                                                   UN_CAMPOS    => MI_CAMPOS,
                                                   UN_CONDICION => MI_CONDICION);
               --MI_RTA1 := MI_RTA1 || RSD_MOVIMIENTO.TIPOMOVIMIENTO || '-' ||RSD_MOVIMIENTO.MOVIMIENTO ||' (' ||MI_RSCLON_SALDOKARDEX||'), ';                                                                           
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                MI_MSGERROR(1).CLAVE := 'VLRUNITARIOPROM';
                MI_MSGERROR(1).VALOR := MI_RSCLON_VLRUNITARIOPROM;
                MI_MSGERROR(2).CLAVE := 'VLRAJUSTADO';
                MI_MSGERROR(2).VALOR := MI_RSCLON_VLRAJUSTADO;
                MI_MSGERROR(3).CLAVE := 'SALDOKARDEX';
                MI_MSGERROR(3).VALOR := MI_RSCLON_SALDOKARDEX;
                MI_MSGERROR(4).CLAVE := 'VALORSALDO';
                MI_MSGERROR(5).VALOR := MI_RSCLON_VALORSALDO;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD   => SQLCODE,
                  UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_ACTUADMOVIMIENTO,
                  UN_REEMPLAZOS => MI_MSGERROR
                );
            END;
        END IF;
        MI_ANIO := EXTRACT(YEAR FROM RSD_MOVIMIENTO.FECHA);
        MI_MES := EXTRACT(MONTH FROM RSD_MOVIMIENTO.FECHA);
        IF MI_RSCLON_SALDOKARDEX < 0 THEN
          MI_AUX_TEXTOERROR := RSD_MOVIMIENTO.TIPOMOVIMIENTO
                          || ' - '               || RSD_MOVIMIENTO.MOVIMIENTO
                          || ' Código: '         || RSD_MOVIMIENTO.ELEMENTO
                          || ' Item: '           || RSD_MOVIMIENTO.CODIGO
                          || ' Fecha: '          || TO_CHAR(RSD_MOVIMIENTO.FECHA,'DD/MM/YYYY')
                          || ' Saldo Negativo: ' || RSD_MOVIMIENTO.SALDOKARDEX
                          || CHR(10);
          
          MI_RTA := TO_CLOB(MI_RTA || MI_AUX_TEXTOERROR);                

        END IF;
      END IF;
    END LOOP MOVIMIENTOS;
    BEGIN
      SELECT  EXISTENCIA
             ,VALORTOTAL
             ,VLRUNITARIOPROM
      INTO    MI_EXISTENCIA
             ,MI_VALORTOTAL
             ,MI_VLRUNITARIOPROM
      FROM    INVENTARIO
      WHERE   COMPANIA       = UN_COMPANIA
        AND   CODIGOELEMENTO = UN_ELEMENTO;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_EXISTENCIA := 0;
        MI_VALORTOTAL := 0;
        MI_VLRUNITARIOPROM := 0;
    END;
    MI_DBLOTROVALOR := MI_VALORSALDO;
    MI_CANTIDAD := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_CANTIDAD,
                                           UN_PRECISION => 2);
    IF MI_CANTIDAD = 0 THEN
      MI_DBLVALOR := 0;
    ELSE
      MI_DBLVALOR := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_VALORSALDO / MI_CANTIDAD,
                                             UN_PRECISION => 2);
    END IF;
    IF MI_VLRUNITARIOPROM <> MI_DBLVALOR OR MI_EXISTENCIA <> MI_CANTIDAD OR MI_VALORTOTAL <> MI_DBLOTROVALOR THEN
      BEGIN
        BEGIN
          MI_TABLA :='INVENTARIO';
          MI_CAMPOS := ' INVENTARIO.VLRUNITARIOPROM  = ' || MI_DBLVALOR ||
                         ',INVENTARIO.EXISTENCIA  = ' || MI_CANTIDAD ||
                         ',INVENTARIO.VALORTOTAL  = ' || MI_DBLOTROVALOR;

          MI_CONDICION := '   INVENTARIO.COMPANIA       = ''' || UN_COMPANIA || '''
                          AND INVENTARIO.CODIGOELEMENTO = ''' || UN_ELEMENTO || '''';
          PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                               UN_ACCION    => 'M',
                                               UN_CAMPOS    => MI_CAMPOS,
                                               UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
            MI_MSGERROR(1).CLAVE := 'VLRUNITARIOPROM';
            MI_MSGERROR(1).VALOR := MI_DBLVALOR;
            MI_MSGERROR(2).CLAVE := 'EXISTENCIA';
            MI_MSGERROR(2).CLAVE := MI_CANTIDAD;
            MI_MSGERROR(3).CLAVE := 'VALORTOTAL';
            MI_MSGERROR(3).VALOR := MI_DBLOTROVALOR;
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD   => SQLCODE,
              UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_ACTUAINVENTARIO,
              UN_REEMPLAZOS => MI_MSGERROR
            );
        END;
    END IF;
    IF MI_AUX_TEXTOERROR IS NULL THEN
        MI_RTA := '';
        --MI_RTA := MI_RTA1;
    ELSE    
    MI_RTA := MI_RTA || TO_CLOB('--------------------------------------------------------------------------------') || CHR(10);
    MI_RTA := TO_CLOB(MI_RTA || 'FIN DEL INFORME') || CHR(10);
    END IF;
--   DBMS_OUTPUT.PUT_LINE(MI_RTA);
	IF MI_MANAUXILIARES = 'SI' THEN
		MI_RTA := PCK_ALMACEN_COM3.FC_KARDEXELEMENTO_PORAUX(UN_COMPANIA => UN_COMPANIA,
                                                            UN_TIPOMOVIMIENTO => UN_TIPOMOVIMIENTO,
                                                            UN_MOVIMIENTO => UN_MOVIMIENTO,
                                                            UN_ELEMENTO => UN_ELEMENTO,
                                                            UN_CODIGO => UN_CODIGO,
                                                            UN_ACTPROMEDIO => UN_ACTPROMEDIO,
                                                            UN_ANO => UN_ANO,
                                                            UN_MES => UN_MES);
    END IF;
    RETURN MI_RTA;
  END FC_KARDEXELEMENTO;

  -- 2
  FUNCTION FC_KARDEXELEMENTOTODOSHALM
    /*
      NAME              : FC_KARDEXELEMENTOTODOSHALM En Access --> KardexElementohalm
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRIGUEZ
      DATE MIGRADOR     : 9/02/2016
      TIME              : 2:00 PM
      MODIFIER          : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MODIFIED     : 16/01/2017
      TIME              : 08:02 AM
      SOURCE MODULE     : ALMACEN
      DESCRIPTION       : LA FUNCIÓN LLAMA A LA FUNCIÓN FC_KARDEXELEMENTO LA CUAL A SU VEZ DEVUELVE EL CLOB CON LAS INCONSISTENCIAS,
                          POR LO CUAL LA FUNCIÓN DEVUELVE UN CLOB CON LAS INCONSISTENCIAS. SI LA FUNCIÓN NO ALCANZA A LLAMAR A KARDEXELEMENTO POR ALGÚN ERROR,
                          DEVUELVE UN CLOB DE UNA SOLA LÍNEA CON EL MENSAJE DE ERROR.
                        : EL PARÁMETRO UN_KARDEXGENERAL SE ADICIONO PARA PEMITIR SI EL KARDEO SE GENERA EN UN AÑO Y UN MES EN PARTICULAR O SI SE GENERA
                          EN TODOS LOS PERIODOS
                        : SI EL PARÁMETRO NO SE ENVÍA EN EL LLAMADO DE LA FUNCIÓN TOMARA POR DEFECTO EL VALOR -1 Y KARDEARA TODO EL PERIODO ENVIADO.
      PARAMETERS        : UN_COMPANIA           => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_INTANOINICIAL      => ANIO INICIAL DEL QUE SE QUIERES OBTENER LAS INCONSISTENCIAS.
                          UN_INTMESINICIAL      => MES INICIAL DEL QUE SE QUIERES OBTENER LAS INCONSISTENCIAS.
                          UN_STRELEMENTOINICIAL => ELEMENTO INICIAL DEL INVENTARIO.
                          UN_STRELEMENTOFINAL   => ELEMENTO FINAL DEL INVENTARIO.
                          UN_KARDEXGENERAL      => NUMERO DEL KARDEO.
      MODIFICATION      : CORRECCION SEGUN ESTANDAR DE PROGRAMACION Y REFERENCIACION DE FUNCIONES.
      REVISADO POR HPV EN 10 ABR DE 2018
    */
  (
    UN_COMPANIA             IN  PCK_SUBTIPOS.TI_COMPANIA
   ,UN_INTANOINICIAL        IN  PCK_SUBTIPOS.TI_ANIO
   ,UN_INTMESINICIAL        IN  PCK_SUBTIPOS.TI_MES
   ,UN_INTANOFINAL          IN  PCK_SUBTIPOS.TI_ANIO
   ,UN_INTMESFINAL          IN  PCK_SUBTIPOS.TI_MES
   ,UN_STRELEMENTOINICIAL   IN  PCK_SUBTIPOS.TI_ELEMENTO DEFAULT NULL
   ,UN_STRELEMENTOFINAL     IN  PCK_SUBTIPOS.TI_ELEMENTO DEFAULT NULL
   ,UN_KARDEXGENERAL        IN  PCK_SUBTIPOS.TI_ENTERO DEFAULT -1
   ,UN_FECHAINICIAL         IN  DATE
   ,UN_FECHAFINAL           IN  DATE
   ,UN_TIPOMOVIMIENTO       IN  VARCHAR2
   ,UN_MOVIMIENTO           IN  PCK_SUBTIPOS.TI_ENTERO_LARGO
  )
    RETURN CLOB
  AS
    MI_RTA                        CLOB;
    MI_ACTPROMEDIO                VARCHAR2(100 CHAR);
    MI_STRELEMENTOINICIAL1        PCK_SUBTIPOS.TI_ELEMENTO;
    MI_STRELEMENTOFINAL1          PCK_SUBTIPOS.TI_ELEMENTO;
    MI_PARCORTE                   VARCHAR2(3200 CHAR);
    MI_STRTIPO                    D_MOVIMIENTO.TIPOMOVIMIENTO%TYPE;
    MI_DBLNUMERO                  D_MOVIMIENTO.MOVIMIENTO%TYPE;
    MI_FILA                       PCK_SUBTIPOS.TI_ENTERO;
    MI_DBLCOD                     D_MOVIMIENTO.CODIGO%TYPE;
    MI_STRELEMENTO                PCK_SUBTIPOS.TI_ELEMENTO;
    MI_ANIOSCERRADOS              PCK_SUBTIPOS.TI_ANIO;
    MI_MESESCERRADOS              PCK_SUBTIPOS.TI_MES;
    MI_INTANIO                    PCK_SUBTIPOS.TI_ANIO;
    MI_MANEJAPEPSENCONSUMOALMACEN VARCHAR2(3200 CHAR);
    MI_MSGERROR                   PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_ESTADO                     VARCHAR2(2 CHAR);
    MI_MESFIN                     PCK_SUBTIPOS.TI_MES;
    MI_RTAKARDEX                  CLOB;
	MI_CLASE                      VARCHAR2(2 CHAR);
    MI_TIPOELEMENTO               VARCHAR2(2 CHAR);
	MI_MANAUXILIARES              PCK_SUBTIPOS.TI_PARAMETRO;
    MI_PAR_PEPS_IDIPRON           PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CONSULTA                   PCK_SUBTIPOS.TI_STRSQL;
    MI_RS_ELEMENTO                PCK_SUBTIPOS.TI_ELEMENTO;
    RSINVENTARIO                  SYS_REFCURSOR;
    MI_ID_PROCESO                 PCK_SUBTIPOS.TI_ENTERO;
    MI_USUARIO                    VARCHAR2(200 CHAR);
    MI_TABLA                      PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS                     PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION                  PCK_SUBTIPOS.TI_CONDICION;
  BEGIN
	MI_MANAUXILIARES := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA,
												  UN_NOMBRE => 'MANEJA SALDO POR BODEGA Y AUXILIARES EN ALMACEN',
												  UN_MODULO => PCK_DATOS.MODULOALMACEN,
												  UN_FECHA_PAR => SYSDATE),'NO');
    MI_ANIOSCERRADOS:=0;
    MI_MESESCERRADOS:=0;
    
    BEGIN
        SELECT MAX(ID_PROCESO), CREATED_BY 
        INTO MI_ID_PROCESO, MI_USUARIO
        FROM CONTROL_PROCESOS
        WHERE ESTADO_EJECUCION IN ('INICIADO') AND COMPANIA =  UN_COMPANIA AND  NOMBRE_PROCESO = 'KARDEAR'
        GROUP BY CREATED_BY;
    EXCEPTION WHEN NO_DATA_FOUND THEN
       MI_ID_PROCESO := 0;
    END;
      
    <<VERIFICARANIOS>>
    FOR MI_I IN UN_INTANOINICIAL..EXTRACT(YEAR FROM SYSDATE)
    LOOP
      BEGIN
      BEGIN
        SELECT NUMERO
        INTO   MI_INTANIO
        FROM   ANO
        WHERE  COMPANIA = UN_COMPANIA
          AND  NUMERO   = MI_I;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
      END ;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                    MI_MSGERROR(1).CLAVE := 'ANO';
                    MI_MSGERROR(1).VALOR := MI_I;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                               UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ANO_CERRADO,
                                               UN_TABLAERROR => 'ANO',
                                               UN_REEMPLAZOS => MI_MSGERROR
                                               );
        END;
    END LOOP VERIFICARANIOS;
    --CC_2215: Se comenta la validación de año y mes, evitando que se aplique durante la ejecución del kardear.
    /*<<VERIFICARESTADOS>>
    FOR MI_I IN UN_INTANOINICIAL..EXTRACT(YEAR FROM SYSDATE)
    LOOP
      MI_ESTADO := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADOANO(UN_COMPANIA=> UN_COMPANIA,
                                                         UN_ANO     => MI_I,
                                                         UN_MODULO  => 10,
                                                         UN_PROCESO => 1);

      IF MI_ESTADO<>'A' THEN
         MI_ANIOSCERRADOS := MI_ANIOSCERRADOS + 1;
      END IF;
      MI_MESFIN := (CASE WHEN MI_I = EXTRACT(YEAR FROM SYSDATE) THEN EXTRACT(MONTH FROM SYSDATE) ELSE 12 END);
      <<VERIFICARESTADOS>>
      FOR MI_J IN UN_INTMESINICIAL..MI_MESFIN
      LOOP
        MI_ESTADO := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADOMES(UN_COMPANIA=> UN_COMPANIA,
                                                           UN_ANO     => MI_I,
                                                           UN_MES     => MI_J,
                                                           UN_MODULO  => 10,
                                                           UN_PROCESO => 1);

        IF MI_ESTADO<>'A' THEN
           MI_MESESCERRADOS := MI_MESESCERRADOS + 1;
        END IF;
      END LOOP VERIFICARESTADOS;

    END LOOP VERIFICARESTADOS;
    IF MI_ANIOSCERRADOS > 0 OR MI_MESESCERRADOS > 0 THEN
       BEGIN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                               UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_PERIODOS_CERRADOS,
                                               UN_TABLAERROR => 'ANO',
                                               UN_REEMPLAZOS => MI_MSGERROR
                                               );
        END;
    END IF;
    */


    MI_MANEJAPEPSENCONSUMOALMACEN := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                                              ,UN_NOMBRE    => 'MANEJA PEPS EN CONSUMO ALMACEN'
                                                              ,UN_MODULO    => 10--PCK_DATOS.FC_MODULOALMACEN
                                                              ,UN_FECHA_PAR => SYSDATE)
                                        ,'NO');

    IF MI_MANEJAPEPSENCONSUMOALMACEN = 'SI' THEN
      MI_ACTPROMEDIO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                  UN_NOMBRE    => 'ACTUALIZA PROMEDIO ALMACEN',
                                                  UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN,
                                                  UN_FECHA_PAR => SYSDATE),'NO');
    ELSE
      MI_ACTPROMEDIO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                  UN_NOMBRE    => 'ACTUALIZA PROMEDIO ALMACEN',
                                                  UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN,
                                                  UN_FECHA_PAR => SYSDATE),'SI');
    END IF;
    MI_PARCORTE := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                         UN_NOMBRE    => 'FECHA DE CORTE PARA INICIO DEL ALMACEN',
                                         UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN,
                                         UN_FECHA_PAR => SYSDATE);
                                         
    MI_PAR_PEPS_IDIPRON := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                  UN_NOMBRE    => 'MANEJA PEPS CONSUMO DE ALMACEN IDIPRON',
                                                  UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN,
                                                  UN_FECHA_PAR => SYSDATE),'NO');
                                         
    IF LENGTH(MI_PARCORTE) = 0 THEN
       BEGIN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                               UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_FECHA_CORTE,
                                               UN_TABLAERROR => 'PARAMETRO',
                                               UN_REEMPLAZOS => MI_MSGERROR
                                               );
       END;
      --RETURN 'La fecha para corte de inventario inicial no ha sido definida. Revise en el menú de utilidades, en parámetros del sistema, el parámetro "FECHA DE CORTE PARA INICIO DEL ALMACEN", Ej:31/12/2015 ';
    END IF;
    MI_STRELEMENTOINICIAL1 := CASE
                                WHEN UN_STRELEMENTOINICIAL IS NULL
                                THEN '0'
                                ELSE UN_STRELEMENTOINICIAL
                              END;
    MI_STRELEMENTOFINAL1 := CASE
                              WHEN UN_STRELEMENTOFINAL IS NULL
                              THEN 'zzz'
                              ELSE UN_STRELEMENTOFINAL
                            END;
---ACTUALIZA EL CAMPO EXISTENCIA A CERO A LOS ELEMENTOS QUE NO TIENE NINGUN  REGISTRO EN LA TABLA D_MOVIMIENTO
UPDATE INVENTARIO
SET EXISTENCIA=0
WHERE COMPANIA = UN_COMPANIA
  AND CODIGOELEMENTO IN(
                  SELECT INVENTARIO.CODIGOELEMENTO
                  FROM INVENTARIO LEFT JOIN D_MOVIMIENTO 
                    ON INVENTARIO.COMPANIA=D_MOVIMIENTO.COMPANIA
                    AND INVENTARIO.CODIGOELEMENTO=D_MOVIMIENTO.ELEMENTO
                  WHERE INVENTARIO.COMPANIA= UN_COMPANIA
                  AND INVENTARIO.TIENEMOVIMIENTO NOT IN(0)
                  AND INVENTARIO.EXISTENCIA<>0
                  AND D_MOVIMIENTO.COMPANIA IS NULL
                  );
--- REALIZA EL PROCESO DE KARDEO A LOS ELEMENTOS QUE TIENEN REGISTRO EN LA TABLA D_MOVIMIENTO
--CC_2493: Se crea validación para que solo se ejecute el proceso para los elementos que tengan diferencia en las existencias
--para cuando el parámetro de peps idipron este en No para mantener el proceso de idipron como esta

    IF MI_PAR_PEPS_IDIPRON = 'NO' THEN 
    
        MI_CONSULTA := 'SELECT I.CODIGOELEMENTO
                        FROM INVENTARIO I
                        INNER JOIN D_MOVIMIENTO DM 
                               ON I.COMPANIA = DM.COMPANIA
                              AND I.CODIGOELEMENTO = DM.ELEMENTO
                        INNER JOIN TIPOMOVIMIENTO TM
                               ON DM.COMPANIA = TM.COMPANIA
                              AND DM.TIPOMOVIMIENTO = TM.CODIGO
                        WHERE I.COMPANIA        = ''' || UN_COMPANIA || '''
                          AND I.TIENEMOVIMIENTO NOT IN (0)
                          AND I.CODIGOELEMENTO  BETWEEN ''' || MI_STRELEMENTOINICIAL1 || ''' AND ''' || MI_STRELEMENTOFINAL1 || '''
                        GROUP BY I.CODIGOELEMENTO, I.EXISTENCIA
                        HAVING I.EXISTENCIA <> (SUM(CASE WHEN TM.CLASE = ''E'' THEN DM.CANTIDAD ELSE 0 END) -
                                                SUM(CASE WHEN TM.CLASE = ''S'' THEN DM.CANTIDAD ELSE 0 END))
                              OR SUM(DM.SALDOCANT) <> SUM(DM.SALDOKARDEX)
                        ORDER BY I.CODIGOELEMENTO';
    
    ELSE 
    
        MI_CONSULTA := 'SELECT DISTINCT  INVENTARIO.CODIGOELEMENTO
                          FROM     INVENTARIO  INNER JOIN D_MOVIMIENTO 
                                        ON INVENTARIO.COMPANIA=D_MOVIMIENTO.COMPANIA
                                        AND INVENTARIO.CODIGOELEMENTO=D_MOVIMIENTO.ELEMENTO 
                          WHERE    INVENTARIO.COMPANIA        =   ''' || UN_COMPANIA || '''
                            AND    INVENTARIO.TIENEMOVIMIENTO NOT IN(0)
                            AND    INVENTARIO.CODIGOELEMENTO BETWEEN ''' || MI_STRELEMENTOINICIAL1 || ''' AND ''' || MI_STRELEMENTOFINAL1 || '''
                          ORDER BY INVENTARIO.CODIGOELEMENTO';
    
    END IF;

    <<CODIGOINVENTARIO>>
    OPEN RSINVENTARIO FOR MI_CONSULTA;
    LOOP
        FETCH RSINVENTARIO INTO MI_RS_ELEMENTO;
        EXIT WHEN RSINVENTARIO%NOTFOUND;
      BEGIN
        SELECT TIPOMOVIMIENTO
              ,MOVIMIENTO
              ,ELEMENTO
              ,CODIGO
              ,ROWNUM FILA 
        INTO     MI_STRTIPO
                ,MI_DBLNUMERO
                ,MI_STRELEMENTO
                ,MI_DBLCOD
                ,MI_FILA
        FROM(
              -- 21/06/2018  @amonroy, Se elimina el casteo TO_CHAR definido en el ORDER BY de la consulta debido a que 
              -- no estaba realizando el ordenamiento de manera adecuada
              SELECT   TIPOMOVIMIENTO
                      ,MOVIMIENTO
                      ,ELEMENTO
                      ,CODIGO       
              FROM     D_MOVIMIENTO
              WHERE    COMPANIA = UN_COMPANIA
                AND    ELEMENTO = MI_RS_ELEMENTO
                AND    FECHA    BETWEEN TO_DATE(LPAD(UN_INTANOINICIAL, 4) ||'/' ||
                                                LPAD(UN_INTMESINICIAL, 2) ||'/01' ,'YYYY/MM/DD')
                                    AND TO_DATE('5000/12/31','YYYY/MM/DD')
                AND    IND_REG  NOT IN (0)          
              ORDER BY TO_DATE(TO_CHAR(FECHA,'DD/MM/YYYY') || ' ' || TO_CHAR(HORA,'HH24:MI:SS'), 'DD/MM/YYYY HH24:MI:SS')
            ) TABLA
            WHERE ROWNUM=1;

    
        IF UN_KARDEXGENERAL = 0 THEN
            --Ticket#7736969: Se agrega validaci�n para determinar si es un movimiento de traspaso y no ejecutar el kardear para esta clase de movimiento
            BEGIN
                    SELECT TIPOMOVIMIENTO.CLASE ,
                         TIPOMOVIMIENTO.TIPOELEMENTO 
                    INTO MI_CLASE ,
                         MI_TIPOELEMENTO 
                    FROM TIPOMOVIMIENTO
                    INNER JOIN MOVIMIENTO
                        ON TIPOMOVIMIENTO.COMPANIA    = MOVIMIENTO.COMPANIA
                        AND TIPOMOVIMIENTO.CODIGO     = MOVIMIENTO.TIPOMOVIMIENTO
                    WHERE TIPOMOVIMIENTO.COMPANIA = UN_COMPANIA
                        AND TIPOMOVIMIENTO.CODIGO = UN_TIPOMOVIMIENTO
                        AND MOVIMIENTO.NUMERO     = UN_MOVIMIENTO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_CLASE := '';
                    MI_TIPOELEMENTO := '';
            END;
                
            IF MI_CLASE NOT IN ('T') OR MI_CLASE IS NULL THEN
        
                MI_RTAKARDEX := PCK_ALMACEN_COM3.FC_KARDEXELEMENTO(UN_COMPANIA       => UN_COMPANIA,
                                                       UN_TIPOMOVIMIENTO => MI_STRTIPO,
                                                       UN_MOVIMIENTO     => MI_DBLNUMERO,
                                                       UN_ELEMENTO       => MI_STRELEMENTO,
                                                       UN_CODIGO         => MI_DBLCOD,
                                                       UN_ACTPROMEDIO    => MI_ACTPROMEDIO,
                                                       UN_ANO            => UN_INTANOINICIAL,
                                                       UN_MES            => UN_INTMESINICIAL);
                                                       
            END IF;

          IF MI_RTAKARDEX IS NOT NULL THEN                                           
            MI_RTA := MI_RTA || NVL(MI_RTAKARDEX,'')|| CHR(10);
          END IF;
            
          IF(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'MANEJA PEPS CONSUMO DE ALMACEN IDIPRON',10,SYSDATE),'NO')='SI') THEN
                BEGIN
                    SELECT TIPOMOVIMIENTO.CLASE ,
                         TIPOMOVIMIENTO.TIPOELEMENTO 
                    INTO MI_CLASE ,
                         MI_TIPOELEMENTO 
                    FROM TIPOMOVIMIENTO
                    INNER JOIN MOVIMIENTO
                        ON TIPOMOVIMIENTO.COMPANIA    = MOVIMIENTO.COMPANIA
                        AND TIPOMOVIMIENTO.CODIGO     = MOVIMIENTO.TIPOMOVIMIENTO
                    WHERE TIPOMOVIMIENTO.COMPANIA = UN_COMPANIA
                        AND TIPOMOVIMIENTO.CODIGO = UN_TIPOMOVIMIENTO
                        AND MOVIMIENTO.NUMERO     = UN_MOVIMIENTO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_CLASE := '';
                    MI_TIPOELEMENTO := '';
                END;
                IF(MI_CLASE='S' AND MI_TIPOELEMENTO='C') THEN
                    PCK_ALMACEN_COM3.PR_KARDEXELEMENTOESPIDI(UN_COMPANIA        => UN_COMPANIA
                                                    ,UN_FECHAINICIAL    => UN_FECHAINICIAL
                                                    ,UN_FECHAFINAL      => UN_FECHAFINAL
                                                    ,UN_ELEMENTO        => UN_STRELEMENTOINICIAL
                                                    ,UN_TIPOMOVIMIENTO  => UN_TIPOMOVIMIENTO





                                                    ,UN_MOVIMIENTO      => UN_MOVIMIENTO);
                END IF;
          END IF;
        ELSE
          --Ticket#7736969: Se agrega validaci�n para determinar si es un movimiento de traspaso y no ejecutar el kardear para esta clase de movimiento
            BEGIN
                    SELECT TIPOMOVIMIENTO.CLASE ,
                         TIPOMOVIMIENTO.TIPOELEMENTO 
                    INTO MI_CLASE ,
                         MI_TIPOELEMENTO 
                    FROM TIPOMOVIMIENTO
                    INNER JOIN MOVIMIENTO
                        ON TIPOMOVIMIENTO.COMPANIA    = MOVIMIENTO.COMPANIA
                        AND TIPOMOVIMIENTO.CODIGO     = MOVIMIENTO.TIPOMOVIMIENTO
                    WHERE TIPOMOVIMIENTO.COMPANIA = UN_COMPANIA
                        AND TIPOMOVIMIENTO.CODIGO = UN_TIPOMOVIMIENTO
                        AND MOVIMIENTO.NUMERO     = UN_MOVIMIENTO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_CLASE := '';
                    MI_TIPOELEMENTO := '';
            END;
                
            IF MI_CLASE NOT IN ('T') OR MI_CLASE IS NULL THEN
        
                MI_RTAKARDEX := PCK_ALMACEN_COM3.FC_KARDEXELEMENTO(UN_COMPANIA       => UN_COMPANIA,
                                                       UN_TIPOMOVIMIENTO => MI_STRTIPO,
                                                       UN_MOVIMIENTO     => MI_DBLNUMERO,
                                                       UN_ELEMENTO       => MI_STRELEMENTO,
                                                       UN_CODIGO         => MI_DBLCOD,
                                                       UN_ACTPROMEDIO    => MI_ACTPROMEDIO);
                                                    
            END IF;

            IF MI_RTAKARDEX IS NOT NULL THEN                                           
                MI_RTA := MI_RTA || NVL(MI_RTAKARDEX,'')|| CHR(10);
            END IF;
        END IF;
		
        EXCEPTION WHEN NO_DATA_FOUND THEN
         CONTINUE;
         -- MI_RTA := MI_RTA || CHR(10);
      END;
    END LOOP CODIGOINVENTARIO;
    
    IF(MI_MANAUXILIARES = 'SI' AND UN_MOVIMIENTO = '0' AND (UN_TIPOMOVIMIENTO IS NULL OR UN_TIPOMOVIMIENTO = '')) THEN
            PCK_ALMACEN_COM2.PR_KARDEXAUXINVBOD(UN_COMPANIA => UN_COMPANIA
                                               ,UN_ELEMENTO => MI_STRELEMENTO);
    END IF;
    
    BEGIN
                MI_TABLA  := 'CONTROL_PROCESOS';
                MI_CAMPOS := 'ESTADO_EJECUCION = '|| CASE WHEN MI_RTA IS NULL THEN '''FINALIZADO''' ELSE '''ERRORES''' END ||',
                              FECHA_FINAL = SYSDATE,
                              LOG_RESULTADO = '''|| NVL(DBMS_LOB.SUBSTR(MI_RTA, 3500, 1), 'OK') ||''',
                              DATE_MODIFIED = SYSDATE,
                              MODIFIED_BY = ''' || MI_USUARIO ||''' ';
                MI_CONDICION := 'ID_PROCESO = ''' || MI_ID_PROCESO || ''' ';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                                       UN_ACCION    => 'M',
                                                       UN_CAMPOS    => MI_CAMPOS,
                                                       UN_CONDICION => MI_CONDICION);
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
      RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
    END;
    
    IF MI_RTA IS NULL THEN         
        MI_RTA:='0';
    END IF;
    RETURN MI_RTA;
  END FC_KARDEXELEMENTOTODOSHALM;

  -- 3
  FUNCTION FC_NUMREQUISICIONES
    /*
      NAME              : FC_NUMREQUISICIONES En Access --> NumRequisiciones
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRIGUEZ
      DATE MIGRADOR     : 01/04/2016
      TIME              : 8:34 PM
      MODIFIER          : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MODIFIED     : 16/01/2017
      TIME              : 09:00 AM
      SOURCE MODULE     : ALMACEN
      DESCRIPTION       : FUNCION QUE RETORNA UNA CADENA CON LOS NUMEROS DE ORDEN DE SUMINISTRO, TENIENDO EN CUENTA EL
                          TIPO Y NUMERO DE ORDEN DE COMPRA.
      PARAMETERS        : UN_COMPANIA  => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_TIPOORDEN => TIPO DE ORDEN DE COMPRA.
                          UN_NUMORDEN  => NUMERO DE ORDEN DE COMPRA.
      MODIFICATIONS     : CORRECCION SEGUN ESTANDAR DE PROGRAMACION.
    */
  (
    UN_COMPANIA    IN  PCK_SUBTIPOS.TI_COMPANIA
   ,UN_TIPOORDEN   IN  D_ORDENDECOMPRA.CLASEORDEN%TYPE
   ,UN_NUMORDEN    IN  D_ORDENDECOMPRA.ORDENDECOMPRA%TYPE
  )
    RETURN CLOB
  AS
    MI_RTA    CLOB;
  BEGIN
    MI_RTA := ' ';
    <<ORDENESDECOMPRA>>
    FOR RS IN (
      SELECT   D_ORDENDECOMPRA.ORDENDESUMINISTRO
              ,D_ORDENDECOMPRA.CLASEORDEN
              ,D_ORDENDECOMPRA.ORDENDECOMPRA
      FROM     D_ORDENDECOMPRA
      WHERE    D_ORDENDECOMPRA.COMPANIA      = UN_COMPANIA
        AND    D_ORDENDECOMPRA.CLASEORDEN    = UN_TIPOORDEN
        AND    D_ORDENDECOMPRA.ORDENDECOMPRA = UN_NUMORDEN
      GROUP BY D_ORDENDECOMPRA.ORDENDESUMINISTRO
              ,D_ORDENDECOMPRA.COMPANIA
              ,D_ORDENDECOMPRA.CLASEORDEN
              ,D_ORDENDECOMPRA.ORDENDECOMPRA)
    LOOP
      MI_RTA := MI_RTA || RS.ORDENDESUMINISTRO || ' ,';
    END LOOP ORDENESDECOMPRA;
    IF LENGTH(MI_RTA)>2 THEN
      MI_RTA := SUBSTR(MI_RTA, 1, LENGTH(MI_RTA)-2);
    END IF;
    RETURN MI_RTA;
  END FC_NUMREQUISICIONES;

  -- 4
  FUNCTION FC_REVISAHORAS
    /*
      NAME              : FC_REVISAHORAS En Access --> RevisaHoras
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRIGUEZ
      DATE MIGRADOR     : 05/04/2016
      TIME              : 4:39 PM
      MODIFIER          : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MODIFIED     : 16/01/2017
      TIME              : 09:20 AM
      SOURCE MODULE     : ALMACEN
      DESCRIPTION       : FUNCION QUE REVISA LA HORA DEL D_MOVIMIENTO, PARA LOS ELEMENTOS Y SERIES INGRESADAS.
      PARAMETERS        : UN_COMPANIA     => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_ELEMENTOINI  => ELEMENTO INICIAL DE D_MOVIMIENTO.
                          UN_ELEMENTOFIN  => ELEMENTO FINAL DE D_MOVIMIENTO.
                          UN_PLACAINICIAL => NUMERO DE SERIE INICIAL DE D_MOVIMIENTO.
                          UN_PLACAFINAL   => NUMERO DE SERIE FINAL DE D_MOVIMIENTO.
      MODIFICATIONS     : CORRECCION SEGUN ESTANDAR DE PROGRAMACION, REFERENCIACION DE FUNCIONES Y OPTIMIZACION
                          DE MANEJO DE ERRORES.
      @NAME  : revisarHoras                            
    */
  (
    UN_COMPANIA           IN  PCK_SUBTIPOS.TI_COMPANIA
   ,UN_ELEMENTOINI        IN  PCK_SUBTIPOS.TI_ELEMENTO
   ,UN_ELEMENTOFIN        IN  PCK_SUBTIPOS.TI_ELEMENTO
   ,UN_PLACAINICIAL       IN  PCK_SUBTIPOS.TI_SERIE
   ,UN_PLACAFINAL         IN  PCK_SUBTIPOS.TI_SERIE
  )
    RETURN NUMBER
  AS
    MI_RTA              PCK_SUBTIPOS.TI_ENTERO := 1;
    MI_DTEHORA          DATE;
    MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
    MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    MI_RTA := 0;
    <<MOVIMIENTOS>>
    FOR RS IN (
      SELECT   DISTINCT COMPANIA
              ,TIPOMOVIMIENTO
              ,MOVIMIENTO
              ,ELEMENTO
              ,SERIE
      FROM     D_MOVIMIENTO
      WHERE    COMPANIA = UN_COMPANIA
        AND    ELEMENTO BETWEEN UN_ELEMENTOINI AND UN_ELEMENTOFIN
        AND    SERIE    BETWEEN UN_PLACAINICIAL AND UN_PLACAFINAL
      GROUP BY COMPANIA
              ,TIPOMOVIMIENTO
              ,MOVIMIENTO
              ,ELEMENTO
              ,FECHA
              ,TO_CHAR(D_MOVIMIENTO.HORA, 'HH24:MI:SS')
              ,SERIE
      HAVING   COUNT(1) > 1)
    LOOP
      MI_DTEHORA := NULL;
      <<MOVIMIENTOSTIPO>>
      FOR RS1 IN (
        SELECT   COMPANIA
                ,FECHA
                ,HORA
                ,TIPOMOVIMIENTO
                ,MOVIMIENTO
                ,CODIGO
                ,ELEMENTO
                ,SERIE
        FROM     D_MOVIMIENTO
        WHERE    COMPANIA       = UN_COMPANIA
          AND    TIPOMOVIMIENTO = RS.TIPOMOVIMIENTO
          AND    MOVIMIENTO     = RS.MOVIMIENTO
          AND    ELEMENTO       = RS.ELEMENTO
          AND    SERIE          = RS.SERIE
        ORDER BY FECHA
                ,TO_CHAR(HORA, 'HH24:MI:SS'))
      LOOP
        IF MI_DTEHORA IS NOT NULL THEN
          IF TO_CHAR(RS1.HORA, 'HH24:MI:SS') = TO_CHAR(MI_DTEHORA, 'HH24:MI:SS') THEN
            BEGIN
              BEGIN
                MI_TABLA := 'D_MOVIMIENTO';
                MI_CAMPOS := 'D_MOVIMIENTO.HORA = TO_DATE(''30/12/1899' || TO_CHAR(MI_DTEHORA + (1*(1/24/3600)), ' HH24:MI:SS') || ''', ''DD/MM/YYYY HH24:MI:SS'')';
                MI_CONDICION := '    COMPANIA       = ''' || UN_COMPANIA || '''
                                 AND TIPOMOVIMIENTO = ''' || RS1.TIPOMOVIMIENTO || '''
                                 AND MOVIMIENTO     = '   || RS1.MOVIMIENTO || '
                                 AND CODIGO         = '   || RS1.CODIGO || '
                                 AND ELEMENTO       = ''' || RS1.ELEMENTO  || '''';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                                       UN_ACCION    => 'M',
                                                       UN_CAMPOS    => MI_CAMPOS,
                                                       UN_CONDICION => MI_CONDICION);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                MI_MSGERROR(1).CLAVE := 'HORA';
                MI_MSGERROR(1).VALOR := MI_DTEHORA;
                MI_MSGERROR(1).CLAVE := 'TABLA';
                MI_MSGERROR(1).VALOR := MI_TABLA;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD   => SQLCODE,
                  UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_ACTUALIZA_HORAMOV,
                  UN_REEMPLAZOS => MI_MSGERROR
                );
            END;
            MI_DTEHORA := RS1.HORA + (1*(1/24/3600));
          ELSE
            MI_DTEHORA := RS1.HORA;
          END IF;
        ELSE
          MI_DTEHORA:=RS1.HORA;
        END IF;
      END LOOP MOVIMIENTOSTIPO;
    END LOOP MOVIMIENTOS;
    MI_RTA:=1;
    RETURN MI_RTA;
  END FC_REVISAHORAS;

  -- 5
  PROCEDURE PR_REVISAHORAH
    /*
      NAME              : PR_REVISAHORAH En Access --> RevisahoraH
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRIGUEZ
      DATE MIGRADOR     : 06/04/2016
      TIME              : 10:34 AM
      MODIFIER          : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MODIFIER     : 16/01/2017
      TIME              : 10:05 AM
      SOURCE MODULE     : ALMACEN
      DESCRIPTION       : FUNCION QUE REVISA LA HORA DEL D_MOVIMIENTO, PARA LOS ELEMENTOS Y SERIES INGRESADAS.
      PARAMETERS        : UN_COMPANIA     => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
      MODIFICATIONS     : CORRECCION SEGUN ESTANDAR DE PROGRAMACION, REFERENCIACION DE FUNCIONES Y OPTIMIZACION
                          DE MANEJO DE ERRORES.

      @NAME  : revisarHoraH                          
    */
  (
    UN_COMPANIA     IN  PCK_SUBTIPOS.TI_COMPANIA
  )
  AS
    MI_STRELEMENTOPLACAANT  VARCHAR(100 CHAR) := NULL;
    MI_FECHAANT             DATE := NULL;
    MI_HORAANT              DATE := NULL;
    MI_STRELEMENTOPLACA     VARCHAR(100) := NULL;
    MI_FECHA                DATE := NULL;
    MI_HORA                 DATE := NULL;
    MI_TABLA                PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
    MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_DATE_FORMAT = ''DD/MM/YYYY HH24:MI:SS''';
    <<MOVIMIENTOSTIPO>>
    FOR RS IN (
      SELECT D_MOVIMIENTO.COMPANIA
            ,D_MOVIMIENTO.TIPOMOVIMIENTO
            ,D_MOVIMIENTO.MOVIMIENTO
            ,D_MOVIMIENTO.CODIGO
            ,D_MOVIMIENTO.ELEMENTO
            ,D_MOVIMIENTO.SERIE
            ,D_MOVIMIENTO.FECHA
            ,TIPOMOVIMIENTO.CLASE
            ,TIPOMOVIMIENTO.CONCEPTO
            ,D_MOVIMIENTO.HORA
            ,'MV' TDM
      FROM   D_MOVIMIENTO
        INNER JOIN TIPOMOVIMIENTO
           ON D_MOVIMIENTO.COMPANIA       = TIPOMOVIMIENTO.COMPANIA
           AND D_MOVIMIENTO.TIPOMOVIMIENTO = TIPOMOVIMIENTO.CODIGO
        INNER JOIN
              ( SELECT   D_MOVIMIENTO.COMPANIA
                      ,D_MOVIMIENTO.TIPOMOVIMIENTO
                      ,D_MOVIMIENTO.MOVIMIENTO
                      ,D_MOVIMIENTO.ELEMENTO
                      ,D_MOVIMIENTO.SERIE
                      ,D_MOVIMIENTO.FECHA
                      ,D_MOVIMIENTO.HORA
                      ,TIPOMOVIMIENTO.CLASE
                      ,TIPOMOVIMIENTO.CONCEPTO
                      ,COUNT(D_MOVIMIENTO.COMPANIA) CUENTADECOMPANIA
              FROM     D_MOVIMIENTO
               INNER JOIN TIPOMOVIMIENTO
                  ON D_MOVIMIENTO.COMPANIA       = TIPOMOVIMIENTO.COMPANIA
                 AND D_MOVIMIENTO.TIPOMOVIMIENTO = TIPOMOVIMIENTO.CODIGO
              WHERE    D_MOVIMIENTO.COMPANIA = UN_COMPANIA
              GROUP BY D_MOVIMIENTO.COMPANIA
                      ,D_MOVIMIENTO.TIPOMOVIMIENTO
                      ,D_MOVIMIENTO.MOVIMIENTO
                      ,D_MOVIMIENTO.ELEMENTO
                      ,D_MOVIMIENTO.SERIE
                      ,D_MOVIMIENTO.FECHA
                      ,D_MOVIMIENTO.HORA
                      ,TIPOMOVIMIENTO.CLASE
                      ,TIPOMOVIMIENTO.CONCEPTO
                HAVING COUNT(D_MOVIMIENTO.COMPANIA)>1
                ORDER BY COMPANIA
                    ,ELEMENTO
                    ,SERIE
                    ,FECHA
                    ,CLASE
                    ,CONCEPTO DESC)PR
           ON      D_MOVIMIENTO.COMPANIA       = PR.COMPANIA
          AND      D_MOVIMIENTO.MOVIMIENTO     = PR.MOVIMIENTO
          AND      D_MOVIMIENTO.TIPOMOVIMIENTO = PR.TIPOMOVIMIENTO
          AND      D_MOVIMIENTO.ELEMENTO       = PR.ELEMENTO
          AND      D_MOVIMIENTO.FECHA          = PR.FECHA 
          AND      D_MOVIMIENTO.HORA           = PR.HORA
      WHERE    D_MOVIMIENTO.COMPANIA = UN_COMPANIA
      ORDER BY COMPANIA
              ,ELEMENTO
              ,SERIE
              ,FECHA
              ,CLASE
              ,CONCEPTO DESC
              ,TO_CHAR(HORA,'HH24:MI:SS'))
    LOOP
      IF MI_STRELEMENTOPLACAANT IS NOT NULL THEN
        MI_STRELEMENTOPLACA := RS.ELEMENTO || LPAD(RS.SERIE,15);
        MI_FECHA := RS.FECHA;
        MI_HORA := TO_DATE(TO_CHAR(RS.HORA, 'DD/MM/YYYY HH24:MI:SS'), 'DD/MM/YYYY HH24:MI:SS');
        IF MI_STRELEMENTOPLACA = MI_STRELEMENTOPLACAANT THEN
          IF MI_FECHA = MI_FECHAANT AND MI_HORA <= MI_HORAANT THEN
            MI_HORA := MI_HORAANT+(1*(1/24/3600));
            BEGIN
              BEGIN
                MI_TABLA := 'D_MOVIMIENTO';
                MI_CAMPOS := ' D_MOVIMIENTO.HORA = TO_DATE(''30/12/1899' || TO_CHAR(MI_HORA, ' HH24:MI:SS') || ''', ''DD/MM/YYYY HH24:MI:SS'')';
                MI_CONDICION := '    COMPANIA       = ''' || UN_COMPANIA || '''
                                 AND TIPOMOVIMIENTO = ''' || RS.TIPOMOVIMIENTO || '''
                                 AND MOVIMIENTO     = '   || RS.MOVIMIENTO || '
                                 AND CODIGO         = '   || RS.CODIGO;
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS,
                                                      UN_CONDICION => MI_CONDICION);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                MI_MSGERROR(1).CLAVE := 'HORA';
                MI_MSGERROR(1).VALOR := MI_HORA;
                MI_MSGERROR(1).CLAVE := 'TABLA';
                MI_MSGERROR(1).VALOR := MI_TABLA;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD   => SQLCODE,
                  UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_ACTUALIZA_HORAMOV,
                  UN_REEMPLAZOS => MI_MSGERROR
                );
            END;
            MI_HORAANT := MI_HORA;
          ELSE
            MI_HORAANT := MI_HORA;
          END IF;
        ELSE
          MI_HORAANT := MI_HORA;
        END IF;
        MI_STRELEMENTOPLACAANT := RS.ELEMENTO || LPAD(RS.SERIE,15);
        MI_FECHAANT := RS.FECHA;
      ELSE
        MI_STRELEMENTOPLACAANT := RS.ELEMENTO || LPAD(RS.SERIE,15);
        MI_FECHAANT := RS.FECHA;
        MI_HORAANT := RS.HORA;
      END IF;
    END LOOP MOVIMIENTOSTIPO;
  END PR_REVISAHORAH;

  -- 6
  FUNCTION FC_CEDULARESPONSABLEH
    /*
      NAME              : FC_CEDULARESPONSABLEH En Access --> CedulaResponsableH
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRIGUEZ
      DATE MIGRADOR     : 06/04/2016
      TIME              : 12:44 PM
      MODIFIER          : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MODIFIED     : 16/01/2017
      TIME              : 10:45 AM
      SOURCE MODULE     : ALMACEN
      DESCRIPTION       : FUNCION QUE RETORNA LA CEDULA DEL RESPONSABLE DEL ALMACEN.
      PARAMETERS        : UN_COMPANIA => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
      MODIFICATIONS     : CORRECCION SEGUN ESTANDAR DE PROGRAMACION, REFERENCIACION DE FUNCIONES Y OPTIMIZACION
                          DE MANEJO DE ERRORES.
    */
  (
    UN_COMPANIA       IN  PCK_SUBTIPOS.TI_COMPANIA
  )
    RETURN VARCHAR2
  AS
    MI_RTA                 VARCHAR2(100 CHAR);
    MI_PARAMETROBODEGA     DEPENDENCIA_RESPONSABLE.DEPENDENCIA%TYPE;
    MI_TABLA               PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS              PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES             PCK_SUBTIPOS.TI_VALORES;
  BEGIN
    MI_PARAMETROBODEGA := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                UN_NOMBRE    => 'BODEGA ALMACEN',
                                                UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN,
                                                UN_FECHA_PAR => SYSDATE);
    MI_PARAMETROBODEGA := NVL(MI_PARAMETROBODEGA,' ');
    BEGIN
      SELECT   RESPONSABLE.CEDULA
      INTO   MI_RTA
      FROM     RESPONSABLE
      INNER JOIN DEPENDENCIA_RESPONSABLE
         ON      RESPONSABLE.CEDULA   = DEPENDENCIA_RESPONSABLE.RESPONSABLE
        AND      RESPONSABLE.COMPANIA = DEPENDENCIA_RESPONSABLE.COMPANIA
      WHERE    DEPENDENCIA_RESPONSABLE.COMPANIA = UN_COMPANIA
        AND    DEPENDENCIA_RESPONSABLE.DEPENDENCIA = MI_PARAMETROBODEGA
        AND    DEPENDENCIA_RESPONSABLE.RESPONSABLEALMACEN<>0
        AND    ROWNUM=1
      ORDER BY DEPENDENCIA_RESPONSABLE.COMPANIA
              ,DEPENDENCIA_RESPONSABLE.DEPENDENCIA
              ,DEPENDENCIA_RESPONSABLE.RESPONSABLE;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        BEGIN
          BEGIN
            -- 29/06/2018  @amonroy Se ajustan los campos y valores que se envian para crear el responsable
            MI_TABLA := 'RESPONSABLE';
            MI_CAMPOS := 'COMPANIA
                         ,CEDULA
                         ,SUCURSAL 
                         ,CARGO';
            MI_VALORES := ''''||UN_COMPANIA||'''
                         ,''999999999999999999''
                         ,''999''
                         ,''RESPONSABLE''';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                  UN_ACCION  => 'I',
                                                  UN_CAMPOS  => MI_CAMPOS,
                                                  UN_VALORES => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD   => SQLCODE,
              UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_INSERRESPON
            );
        END;
        MI_RTA := '99999999999';
    END;
    RETURN MI_RTA;
  END FC_CEDULARESPONSABLEH;

  -- 7
  PROCEDURE PR_REVISARFECHASSALIDASERVICIO
    /*
      NAME              : PR_REVISARFECHASSALIDASERVICIO En Access --> REVISARFECHASSALIDASERVICIO
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRIGUEZ
      DATE MIGRADOR     : 06/04/2016
      TIME              : 10:34 AM
      MODIFIER          : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MODIFIED     : 16/01/2017
      TIME              : 11:12 AM
      SOURCE MODULE     : ALMACEN
      DESCRIPTION       : PROCEDIMIENTO QUE REVISA QUE LAS FECHAS DE SALIDA DE SERVICIO DEL DEVOLUTIVO NO ESTEN NULAS,
                          DE ESTARLO SE REALIZA UN UPDATE DE ESTAS FECHAS.
      PARAMETERS        : UN_COMPANIA     => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_ELEMENTOINI  => ELEMENTO INICIAL DE D_MOVIMIENTO.
                          UN_ELEMENTOFIN  => ELEMENTO FINAL DE D_MOVIMIENTO.
                          UN_PLACAINICIAL => NUMERO DE SERIE INICIAL DE D_MOVIMIENTO.
                          UN_PLACAFINAL   => NUMERO DE SERIE FINAL DE D_MOVIMIENTO.
      MODIFICATIONS     : CORRECCION SEGUN ESTANDAR DE PROGRAMACION, REFERENCIACION DE FUNCIONES Y OPTIMIZACION
                          DE MANEJO DE ERRORES.
    */
  (
    UN_COMPANIA              IN  PCK_SUBTIPOS.TI_COMPANIA
   ,UN_ELEMENTOINICIAL       IN  PCK_SUBTIPOS.TI_ELEMENTO
   ,UN_ELEMENTOFINAL         IN  PCK_SUBTIPOS.TI_ELEMENTO
   ,UN_PLACAINICIAL          IN  PCK_SUBTIPOS.TI_SERIE
   ,UN_PLACAFINAL            IN  PCK_SUBTIPOS.TI_SERIE
  )
  AS
    MI_ELEMENTOINICIAL         PCK_SUBTIPOS.TI_ELEMENTO;
    MI_ELEMENTOFINAL           PCK_SUBTIPOS.TI_ELEMENTO;
    MI_TABLA                   PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS                  PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION               PCK_SUBTIPOS.TI_CONDICION;
  BEGIN
    IF UN_ELEMENTOINICIAL = 0 THEN
       MI_ELEMENTOINICIAL := 1;
    ELSE
       MI_ELEMENTOINICIAL := UN_ELEMENTOINICIAL;
    END IF;
    IF UN_ELEMENTOFINAL = 0 THEN
       MI_ELEMENTOFINAL := 99999999;
     ELSE
       MI_ELEMENTOFINAL:=UN_ELEMENTOFINAL;
    END IF;
    BEGIN
      BEGIN
        MI_TABLA := 'DEVOLUTIVO';
        MI_CAMPOS := 'FECHASALIDASERVICIO = (
                        SELECT   MIN(D_MOVIMIENTO.FECHA)
                        FROM     D_MOVIMIENTO
                          INNER JOIN TIPOMOVIMIENTO
                             ON      D_MOVIMIENTO.TIPOMOVIMIENTO = TIPOMOVIMIENTO.CODIGO
                            AND      D_MOVIMIENTO.COMPANIA       = TIPOMOVIMIENTO.COMPANIA
                        WHERE    DEVOLUTIVO.SERIE            = D_MOVIMIENTO.SERIE
                          AND    DEVOLUTIVO.ELEMENTO         = D_MOVIMIENTO.ELEMENTO
                          AND    DEVOLUTIVO.COMPANIA         = D_MOVIMIENTO.COMPANIA
                          AND    TIPOMOVIMIENTO.TIPOELEMENTO = ''D''
                          AND    TIPOMOVIMIENTO.CONCEPTO     IN (''CM'',''II'',''N'')
                          AND    TIPOMOVIMIENTO.CLASE        = ''S''
                          AND    D_MOVIMIENTO.COMPANIA       = ''' || UN_COMPANIA || '''
                          AND    D_MOVIMIENTO.ELEMENTO       BETWEEN ''' || MI_ELEMENTOINICIAL || ''' AND ''' || MI_ELEMENTOFINAL || '''
                          AND    D_MOVIMIENTO.SERIE          BETWEEN  ' || UN_PLACAINICIAL || '  AND  ' || UN_PLACAFINAL || '
                        GROUP BY D_MOVIMIENTO.COMPANIA
                                ,D_MOVIMIENTO.ELEMENTO
                                ,D_MOVIMIENTO.SERIE )';
        MI_CONDICION := 'DEVOLUTIVO.FECHASALIDASERVICIO IS NULL ';
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                               UN_ACCION    => 'M',
                                               UN_CAMPOS    => MI_CAMPOS,
                                               UN_CONDICION => MI_CONDICION);
        MI_CAMPOS := 'FECHASALIDASERVICIO = (
                        SELECT   MIN(D_MOVIMIENTO.FECHA) PRIMERAFECHA
                        FROM     D_MOVIMIENTO
                          INNER JOIN TIPOMOVIMIENTO
                             ON      D_MOVIMIENTO.TIPOMOVIMIENTO = TIPOMOVIMIENTO.CODIGO
                             AND     D_MOVIMIENTO.COMPANIA       = TIPOMOVIMIENTO.COMPANIA
                        WHERE    DEVOLUTIVO.SERIE            = D_MOVIMIENTO.SERIE
                          AND    DEVOLUTIVO.ELEMENTO         = D_MOVIMIENTO.ELEMENTO
                          AND    DEVOLUTIVO.COMPANIA         = D_MOVIMIENTO.COMPANIA
                          AND    TIPOMOVIMIENTO.TIPOELEMENTO = ''D''
                          AND    TIPOMOVIMIENTO.CONCEPTO     IN (''CM'',''II'',''N'')
                          AND    TIPOMOVIMIENTO.CLASE        = ''S''
                          AND    D_MOVIMIENTO.COMPANIA       = ''' || UN_COMPANIA || '''
                          AND    D_MOVIMIENTO.ELEMENTO       BETWEEN ''' || MI_ELEMENTOINICIAL || ''' AND ''' || MI_ELEMENTOFINAL || '''
                          AND    D_MOVIMIENTO.SERIE          BETWEEN  ' || UN_PLACAINICIAL || ' AND ' || UN_PLACAFINAL || '
                          AND    D_MOVIMIENTO.COMPANIA       IS NOT NULL
                        GROUP BY D_MOVIMIENTO.COMPANIA
                                ,D_MOVIMIENTO.ELEMENTO
                                ,D_MOVIMIENTO.SERIE)
                         WHERE  DEVOLUTIVO.COMPANIA       = ''' || UN_COMPANIA || '''
                          AND    DEVOLUTIVO.ELEMENTO       BETWEEN ''' || MI_ELEMENTOINICIAL || ''' AND ''' || MI_ELEMENTOFINAL || '''
                          AND    DEVOLUTIVO.SERIE          BETWEEN  ' || UN_PLACAINICIAL || ' AND ' || UN_PLACAFINAL || '';
                        --HAVING   MIN(D_MOVIMIENTO.FECHA) < DEVOLUTIVO.FECHASALIDASERVICIO)';
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                               UN_ACCION    => 'M',
                                               UN_CAMPOS    => MI_CAMPOS);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD   => SQLCODE,
          UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_ACTUALIZA_DEVOLUT
        );
    END;
  END PR_REVISARFECHASSALIDASERVICIO;

  -- 8
  FUNCTION FC_REVISAREGISTRODEPRECIACION
    /*
      NAME              : PR_REVISAHORAH En Access --> RevisaRegistroDepreciacion
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRIGUEZ
      DATE MIGRADOR     : 08/04/2016
      TIME              : 14:12
      MODIFIER          : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MODIFIED     : 16/01/2017
      TIME              : 11:47 AM
      SOURCE MODULE     : ALMACEN
      DESCRIPTION       :
      PARAMETERS        : UN_COMPANIA        => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_ANIOINICIAL     => ANIO INICIAL DEL PERIODO EN EL QUE SE QUIERE REVISAR EL
                                                REGISTRO DE DEPRECIACION.
                          UN_MESINICIAL      => MES INICIAL DEL PERIODO EN EL QUE SE QUIERE REVISAR EL
                                                REGISTRO DE DEPRECIACION.
                          UN_ANIOFINAL       => ANIO FINAL DEL PERIODO EN EL QUE SE QUIERE REVISAR EL
                                                REGISTRO DE DEPRECIACION.
                          UN_MESFINAL        => MES FINAL DEL PERIODO EN EL QUE SE QUIERE REVISAR EL
                                                REGISTRO DE DEPRECIACION.
                          UN_ELEMENTOINICIAL => ELEMENTO INICIAL DE DEPRECIAR.
                          UN_ELEMENTOFINAL   => ELEMENTO FINAL DE DEPRECIAR.
                          UN_PLACAINICIAL    => NUMERO DE SERIE INICIAL DE DEPRECIAR.
                          UN_PLACAFINAL      => NUMERO DE SERIE FINAL DE DEPRECIAR.
      MODIFICATIONS     : CORRECCION SEGUN ESTANDAR DE PROGRAMACION, REFERENCIACION DE FUNCIONES Y OPTIMIZACION
                          DE MANEJO DE ERRORES.
    */
  (
    UN_COMPANIA           IN  PCK_SUBTIPOS.TI_COMPANIA
   ,UN_ANIOINICIAL        IN  PCK_SUBTIPOS.TI_ANIO
   ,UN_MESINICIAL         IN  PCK_SUBTIPOS.TI_MES
   ,UN_ANIOFINAL          IN  PCK_SUBTIPOS.TI_ANIO
   ,UN_MESFINAL           IN  PCK_SUBTIPOS.TI_MES
   ,UN_ELEMENTOINICIAL    IN  PCK_SUBTIPOS.TI_ELEMENTO
   ,UN_ELEMENTOFINAL      IN  PCK_SUBTIPOS.TI_ELEMENTO
   ,UN_PLACAINICIAL       IN  PCK_SUBTIPOS.TI_SERIE
   ,UN_PLACAFINAL         IN  PCK_SUBTIPOS.TI_SERIE
  )
    RETURN CLOB
  AS
    MI_RTA                CLOB;
    MI_INTCON             PCK_SUBTIPOS.TI_ENTERO;
    MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
  BEGIN
    MI_INTCON := 0;
    MI_RTA := 'REGISTROS DOBLES EN LA TABLA DEPRECIACION ' || CHR(10) ||
              'CALCULE LA DEPRECIACION PARA LAS SIGUIENTES PLACAS TENIENDO EN CUENTA EL PERIODO ' || CHR(10);
    <<PERIODODEPRECIAR>>
    FOR RS IN (
      SELECT   DISTINCT COMPANIA
              ,PERIODO
      FROM     DEPRECIAR
      WHERE    COMPANIA = UN_COMPANIA
        AND    PERIODO  BETWEEN TO_DATE('01/'|| LPAD(UN_MESINICIAL,2,'0') || '/' || LPAD(UN_ANIOINICIAL,4,'0'),'DD/MM/YYYY') AND LAST_DAY(TO_DATE('01/'|| LPAD(UN_MESFINAL,2,'0') || '/' || LPAD(UN_ANIOFINAL,4,'0'),'DD/MM/YYYY'))
        AND    ELEMENTO BETWEEN UN_ELEMENTOINICIAL AND UN_ELEMENTOFINAL
        AND    SERIE    BETWEEN UN_PLACAINICIAL AND  UN_PLACAFINAL
      GROUP BY COMPANIA
              ,PERIODO)
    LOOP
      IF RS.PERIODO >= TO_DATE(TO_DATE('01/'|| LPAD(UN_MESINICIAL,2,'0') || '/'
                                            || LPAD(UN_ANIOINICIAL,4,'0'),'DD/MM/YYYY'))
         AND RS.PERIODO <= LAST_DAY(TO_DATE('01/'|| LPAD(UN_MESFINAL,2,'0') || '/'
                                                 || LPAD(UN_ANIOFINAL,4,'0'),'DD/MM/YYYY')) THEN
        <<ELEMENTODEPRECIAR>>
        FOR RSDEPRECIACION IN (
          SELECT   COMPANIA
                  ,ELEMENTO
                  ,SERIE
                  ,PERIODO
                  ,COUNT(SERIE) CONT
          FROM     DEPRECIAR
          WHERE    COMPANIA = UN_COMPANIA
          AND      PERIODO  = RS.PERIODO
          AND      ELEMENTO BETWEEN UN_ELEMENTOINICIAL AND UN_ELEMENTOFINAL
          AND      SERIE    BETWEEN UN_PLACAINICIAL AND UN_PLACAFINAL
          GROUP BY COMPANIA
                  ,ELEMENTO
                  ,SERIE
                  ,PERIODO
          HAVING   COUNT(SERIE) > 1)
        LOOP
          MI_RTA := MI_RTA || LPAD(MI_INTCON,10,'0')
                           || ' ELEMENTO' || RSDEPRECIACION.ELEMENTO
                           || ' PLACA '   || LPAD(RSDEPRECIACION.SERIE,12,'0')
                           || ' PERIODO '  || RSDEPRECIACION.PERIODO
                           || ' ' || CHR(10);
          BEGIN
            BEGIN
              MI_TABLA := 'DEPRECIAR';
              MI_CONDICION := '    COMPANIA = ''' || UN_COMPANIA || '''
                               AND PERIODO  = TO_DATE(''' || RSDEPRECIACION.PERIODO ||''',''DD/MM/YYYY'')
                               AND SERIE    = '   || RSDEPRECIACION.SERIE;
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                                     UN_ACCION    => 'E',
                                                    UN_CONDICION => MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD   => SQLCODE,
                UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_ELIMINARDEPRECIAR
              );
          END;
          MI_INTCON := MI_INTCON +1;
        END LOOP ELEMENTODEPRECIAR;
      END IF;
    END LOOP PERIODODEPRECIAR;
    IF MI_INTCON = 0 THEN
      MI_RTA := MI_RTA || 'Total registros dobles encontrados --> 0' || CHR(10) || CHR(10);
      RETURN 'TRUE';
    ELSE
      MI_RTA := MI_RTA || 'Total registros dobles encontrados --> ' || MI_INTCON || CHR(10) || CHR(10);
      RETURN MI_RTA;
    END IF;
  END FC_REVISAREGISTRODEPRECIACION;

  -- 9
  FUNCTION FC_REVISAPLACASSINMOVIMIENTOH
  /*
    NAME              : FC_REVISAPLACASSINMOVIMIENTOH En Access --> RevisaPlacasSinMovimientoH
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRIGUEZ
    DATE MIGRADOR     : 08/04/2016
    TIME              : 17:55
    MODIFIER          : (16/01/2017) JESSICA LISSETH RAMIREZ BRICEÑO
                        (26/02/2018) PABLO ANDRES ESPITIA CUCA
    TIME              : 12:20 PM
    SOURCE MODULE     : ALMACEN
    DESCRIPTION       : FUNCION QUE RETORNA UNA CADENA CON LOS RESULTADOS DE LA REVISION DE LAS PLACAS SIN MOVIMIENTO.
    PARAMETERS        : UN_COMPANIA        => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                        UN_ANIOINICIAL     => ANIO INICIAL DEL PERIODO EN EL QUE SE QUIERE REVISAR LAS PLACAS SIN MOVIMIENTO.
                        UN_MESINICIAL      => MES INICIAL DEL PERIODO EN EL QUE SE QUIERE REVISAR LAS PLACAS SIN MOVIMIENTO.
                        UN_ANIOFINAL       => ANIO FINAL DEL PERIODO EN EL QUE SE QUIERE REVISAR LAS PLACAS SIN MOVIMIENTO.
                        UN_MESFINAL        => MES FINAL DEL PERIODO EN EL QUE SE QUIERE REVISAR LAS PLACAS SIN MOVIMIENTO.
                        UN_ELEMENTOINICIAL => ELEMENTO INICIAL DEL DEVOLUTIVO.
                        UN_ELEMENTOFINAL   => ELEMENTO FINAL DEL DEVOLUTIVO.
                        UN_PLACAINICIAL    => NUMERO DE SERIE INICIAL DEL DEVOLUTIVO.
                        UN_PLACAFINAL      => NUMERO DE SERIE FINAL DEL DEVOLUTIVO.
    MODIFICATIONS     : (16/01/2017) CORRECCION SEGUN ESTANDAR DE PROGRAMACION, REFERENCIACION DE FUNCIONES Y OPTIMIZACION DE MANEJO DE ERRORES.
                        (26/02/2018) ADICION DE CAMPOS DE AUDITORIA.
                                     INDENTACION DE PL/SQL.
  */
  (
    UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIOINICIAL        IN PCK_SUBTIPOS.TI_ANIO,
    UN_MESINICIAL         IN PCK_SUBTIPOS.TI_MES,
    UN_ANIOFINAL          IN PCK_SUBTIPOS.TI_ANIO,
    UN_MESFINAL           IN PCK_SUBTIPOS.TI_MES,
    UN_ELEMENTOINICIAL    IN PCK_SUBTIPOS.TI_ELEMENTO,
    UN_ELEMENTOFINAL      IN PCK_SUBTIPOS.TI_ELEMENTO,
    UN_PLACAINICIAL       IN PCK_SUBTIPOS.TI_SERIE,
    UN_PLACAFINAL         IN PCK_SUBTIPOS.TI_SERIE,
    UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO DEFAULT PCK_CONEXION.FC_GETUSER() -- Codigo del usuario que desencadena el proceso.
  )
  RETURN CLOB
  AS
    MI_RTA              CLOB;
    MI_INTCON           PCK_SUBTIPOS.TI_ENTERO := 0;
    MI_STRESPEC         DEVOLUTIVO.DESCRIPCION%TYPE;
    MI_TABLA_D          PCK_SUBTIPOS.TI_TABLA DEFAULT 'DEVOLUTIVO';
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
    MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    MI_RTA:='PLACAS SIN MOVIMIENTOS' || CHR(10);

    <<DEVOLUTIVOMOV>>
    FOR RSDEVOLMOV IN (SELECT   
                         DEVOLUTIVO.COMPANIA
                        ,DEVOLUTIVO.ELEMENTO
                        ,DEVOLUTIVO.SERIE PLACA
                        ,DEVOLUTIVO.DESCRIPCION
                        ,DEVOLUTIVO.FECHAADQUISICION
                       FROM DEVOLUTIVO
                        LEFT JOIN D_MOVIMIENTO
                          ON DEVOLUTIVO.COMPANIA = D_MOVIMIENTO.COMPANIA
                         AND DEVOLUTIVO.SERIE    = D_MOVIMIENTO.SERIE
                         AND DEVOLUTIVO.ELEMENTO = D_MOVIMIENTO.ELEMENTO
                      WHERE DEVOLUTIVO.COMPANIA     = UN_COMPANIA
                        AND D_MOVIMIENTO.SERIE      IS NULL
                        AND DEVOLUTIVO.PLACAANULADA = 0
                        AND DEVOLUTIVO.ELEMENTO BETWEEN UN_ELEMENTOINICIAL AND UN_ELEMENTOFINAL
                        AND DEVOLUTIVO.SERIE    BETWEEN UN_PLACAINICIAL    AND UN_PLACAFINAL)
    LOOP
      MI_STRESPEC := RSDEVOLMOV.DESCRIPCION || '. PLACA ANULADA, SIN MOVIMIENTO';

      BEGIN
        BEGIN
          MI_CAMPOS := 'VALOR         = 0
                       ,PLACAANULADA  = -1
                       ,FECHAANULADA  = TO_DATE(''' || TO_CHAR(NVL(RSDEVOLMOV.FECHAADQUISICION,SYSDATE),'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                       ,DESCRIPCION   = '''||MI_STRESPEC||'''
                       ,MODIFIED_BY   = '''||UN_USUARIO ||'''
                       ,DATE_MODIFIED = SYSDATE';

          MI_CONDICION := 'COMPANIA     = ''' || UN_COMPANIA         || '''
                       AND ELEMENTO     = ''' || RSDEVOLMOV.ELEMENTO || '''
                       AND SERIE        =   ' || RSDEVOLMOV.PLACA    || '
                       AND PLACAANULADA IN(0) ';

          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA_D,
                                                 UN_ACCION    => 'M',
                                                 UN_CAMPOS    => MI_CAMPOS,
                                                 UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
        END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
        MI_MSGERROR(1).CLAVE := 'CAMPOS';
        MI_MSGERROR(1).VALOR := 'VALOR, PLACAANULADA, FECHAANULADA y DESCRIPCION';

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ACTUALIZARDEVOLUT
                                  ,UN_TABLAERROR => MI_TABLA_D
                                  ,UN_REEMPLAZOS => MI_MSGERROR);
      END;

      IF PCK_DATOS.GL_RTA >= 0 THEN
        MI_INTCON := MI_INTCON + 1;

        MI_RTA := MI_RTA                                 || 
                  LPAD(TO_CHAR(MI_INTCON),11,'0')        || 
                  ' ELEMENTO '                           || 
                  RSDEVOLMOV.ELEMENTO                    || 
                  ' PLACA '                              || 
                  LPAD(RSDEVOLMOV.PLACA,12,'0')          || 
                  ' ANULADA NO TIENE MOVIMIENTO  FECHA ' || 
                  TO_CHAR(SYSDATE,'DD/MM/YYYY')          || 
                  CHR(10);
      END IF;
    END LOOP DEVOLUTIVOMOV;

    IF MI_INTCON > 0 THEN
      MI_RTA := MI_RTA                                   || 
                'Total Errores Encontrados y corregidos '|| 
                TO_CHAR(MI_INTCON)                       || 
                CHR(10);

      RETURN MI_RTA;
    ELSE
      MI_RTA:= MI_RTA               || 
               'Total Errores --> 0'|| 
               CHR(10)              || 
               CHR(10);

      RETURN 'TRUE';
    END IF;
  END FC_REVISAPLACASSINMOVIMIENTOH;

  -- 10
   FUNCTION FC_REVISADEVOLUTIVOSH
    /*
      NAME              : FC_REVISADEVOLUTIVOSH En Access --> RevisaDevolutivosH
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRATION  : DIEGO ALFREDO SUESCA RODRIGUEZ
      DATE MIGRADOR     : 06/04/2016
      TIME              : 12:07 AM
      MODIFIER          : (16/01/2017) JESSICA LISSETH RAMIREZ BRICEÑO
                          (22/02/2018) PABLO ANDRES ESPITIA CUCA
      SOURCE MODULE     : ALMACEN (10)
      DESCRIPTION       : FUNCION QUE RETORNA UNA CADENA CON LOS RESULTADOS DE LA REVISION DE DEVOLUTIVOS.
      PARAMETERS        : UN_COMPANIA        => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_ANIOINICIAL     => ANIO INICIAL DEL PERIODO EN EL QUE SE QUIEREN REVISAR LOS DEVOLUTIVOS.
                          UN_MESINICIAL      => MES INICIAL DEL PERIODO EN EL QUE SE QUIEREN REVISAR LOS DEVOLUTIVOS.
                          UN_ELEMENTOINICIAL => ELEMENTO INICIAL DEL DEVOLUTIVO.
                          UN_ANIOFINAL       => ANIO FINAL DEL PERIODO EN EL QUE SE QUIEREN REVISAR LOS DEVOLUTIVOS.
                          UN_MESFINAL        => MES FINAL DEL PERIODO EN EL QUE SE QUIEREN REVISAR LOS DEVOLUTIVOS.
                          UN_ELEMENTOFINAL   => ELEMENTO FINAL DEL DEVOLUTIVO.
                          UN_SERIEINICIAL    => NUMERO DE SERIE INICIAL DEL DEVOLUTIVO.
                          UN_SERIEFINAL      => NUMERO DE SERIE FINAL DEL DEVOLUTIVO.
      MODIFICATIONS     : (16/01/2017) CORRECCION SEGUN ESTANDAR DE PROGRAMACION, REFERENCIACION DE FUNCIONES Y OPTIMIZACION
                                       DE MANEJO DE ERRORES.
                          (22/02/2018) ADICION DE CAMPOS DE AUDITORIA.   
                                       INDENTACION DE PL/SQL.

      @NAME  : revisarDevolutivos   
      @METHOD: POST
    */
  (
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIOINICIAL     IN PCK_SUBTIPOS.TI_ANIO,
    UN_MESINICIAL      IN PCK_SUBTIPOS.TI_MES,
    UN_ELEMENTOINICIAL IN PCK_SUBTIPOS.TI_ELEMENTO,
    UN_SERIEINICIAL    IN PCK_SUBTIPOS.TI_SERIE,
    UN_ANIOFINAL       IN PCK_SUBTIPOS.TI_ANIO,
    UN_MESFINAL        IN PCK_SUBTIPOS.TI_MES,
    UN_ELEMENTOFINAL   IN PCK_SUBTIPOS.TI_ELEMENTO,
    UN_SERIEFINAL      IN PCK_SUBTIPOS.TI_SERIE,
    UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO -- Codigo del usuario.
  )
  RETURN CLOB
  AS
    MI_RTA                  CLOB;
    MI_RTA_ACME             PCK_SUBTIPOS.TI_RTA_ACME;
    MI_FECHADECORTEII       DATE;
    MI_STRFECHADECORTEII    VARCHAR2(200 CHAR);
    MI_STRBODEGA            VARCHAR2(200 CHAR);
    MI_STRRESPONSABLE       VARCHAR2(200 CHAR);
    MI_DTEFECHA             DATE;
    MI_SERIEANT             PCK_SUBTIPOS.TI_ENTERO;
    MI_COUNT                PCK_SUBTIPOS.TI_ENTERO;
    MI_MESFINAL             PCK_SUBTIPOS.TI_MES;
    MI_ANIOFINAL            PCK_SUBTIPOS.TI_ANIO;
    MI_TABLA_D              PCK_SUBTIPOS.TI_TABLA DEFAULT 'DEVOLUTIVO';
    MI_TABLA_DM             PCK_SUBTIPOS.TI_TABLA DEFAULT 'D_MOVIMIENTO';
    MI_TABLA_M              PCK_SUBTIPOS.TI_TABLA DEFAULT 'MOVIMIENTO';
    MI_TABLA                PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
    MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
    MI_MERGEUSING           PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE          PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE          PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    MI_STRFECHADECORTEII := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                                     ,UN_NOMBRE    => 'FECHA DE CORTE PARA INICIO DEL ALMACEN'
                                                     ,UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN
                                                     ,UN_FECHA_PAR => SYSDATE)
                               ,TO_CHAR(SYSDATE,'DD/MM/YYYY'));

    MI_FECHADECORTEII := TO_DATE(MI_STRFECHADECORTEII,'DD/MM/YYYY');

    MI_STRBODEGA := PCK_ALMACEN.FC_DATOSBODEGAH(UN_COMPANIA => UN_COMPANIA,
                                                UN_OPCION   => 1);

    MI_STRRESPONSABLE := FC_CEDULARESPONSABLEH(UN_COMPANIA => UN_COMPANIA);

    IF LPAD(UN_ANIOINICIAL,4,'0') || LPAD(UN_MESINICIAL,2,'0') < TO_CHAR(MI_FECHADECORTEII,'YYYYMM') THEN
      RETURN MI_RTA;
    END IF;

    MI_MESFINAL := CASE WHEN UN_MESFINAL IN(0) 
                        THEN EXTRACT(MONTH FROM SYSDATE)
                        ELSE UN_MESFINAL
                   END;

    MI_ANIOFINAL := CASE WHEN UN_ANIOFINAL IN(0)
                         THEN EXTRACT(YEAR FROM SYSDATE)
                         ELSE UN_ANIOFINAL
                    END;

    MI_DTEFECHA := TO_DATE(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                                ,UN_NOMBRE    => 'FECHA DE CORTE PARA INICIO DEL ALMACEN'
                                                ,UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN
                                                ,UN_FECHA_PAR => SYSDATE)
                          ,'DD/MM/YYYY');
    BEGIN
      BEGIN
      -- Modificar - Aqui se actualiza la fecha de entrada de los movimientos que tienen fecha menor a la fecha de corte
      --( )
        MI_MERGEUSING := 'SELECT   
                            TO_DATE(''' || TO_CHAR(MI_DTEFECHA - 1,'DD/MM/YYYY') || ''',''DD/MM/YYYY'') FECHAA
                           ,D_MOVIMIENTO.CODIGO
                           ,D_MOVIMIENTO.MOVIMIENTO
                           ,D_MOVIMIENTO.TIPOMOVIMIENTO
                           ,D_MOVIMIENTO.COMPANIA
                          FROM D_MOVIMIENTO
                              ,TIPOMOVIMIENTO
                          WHERE D_MOVIMIENTO.TIPOMOVIMIENTO = TIPOMOVIMIENTO.CODIGO
                            AND D_MOVIMIENTO.COMPANIA       = TIPOMOVIMIENTO.COMPANIA
                            AND D_MOVIMIENTO.COMPANIA       = '''||UN_COMPANIA||'''
                            AND D_MOVIMIENTO.FECHA < TO_DATE(''' || TO_CHAR(MI_DTEFECHA - 1,'DD/MM/YYYY') ||''',''DD/MM/YYYY'')
                            AND TIPOMOVIMIENTO.CLASE        = ''E''
                            AND TIPOMOVIMIENTO.TIPOELEMENTO = ''D''';

        MI_MERGEENLACE:='VISTA.COMPANIA       = TABLA.COMPANIA
                     AND VISTA.TIPOMOVIMIENTO = TABLA.TIPOMOVIMIENTO
                     AND VISTA.MOVIMIENTO     = TABLA.MOVIMIENTO
                     AND VISTA.CODIGO         = TABLA.CODIGO';

        MI_MERGEEXISTE := 'UPDATE SET FECHA         = VISTA.FECHAA
                                     ,MODIFIED_BY   = '''||UN_USUARIO||'''
                                     ,DATE_MODIFIED = SYSDATE';

        MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA_DM
                                        ,UN_ACCION      => 'MM'
                                        ,UN_MERGEUSING  => MI_MERGEUSING
                                        ,UN_MERGEENLACE => MI_MERGEENLACE
                                        ,UN_MERGEEXISTE => MI_MERGEEXISTE);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ACTU_FECHADMOV
                                ,UN_TABLAERROR => MI_TABLA_DM);
    END;
    BEGIN
      BEGIN
        MI_MERGEUSING := 'SELECT 
                            DISTINCT TO_DATE(''' || TO_CHAR(MI_DTEFECHA - 1,'DD/MM/YYYY') || ''',''DD/MM/YYYY'') FECHAA
                           ,D_MOVIMIENTO.MOVIMIENTO
                           ,D_MOVIMIENTO.TIPOMOVIMIENTO
                           ,D_MOVIMIENTO.COMPANIA
                          FROM D_MOVIMIENTO
                              ,TIPOMOVIMIENTO
                          WHERE D_MOVIMIENTO.TIPOMOVIMIENTO = TIPOMOVIMIENTO.CODIGO
                            AND D_MOVIMIENTO.COMPANIA       = TIPOMOVIMIENTO.COMPANIA
                            AND D_MOVIMIENTO.COMPANIA       = ''' || UN_COMPANIA || '''
                            AND D_MOVIMIENTO.FECHA          < TO_DATE(''' || TO_CHAR(MI_DTEFECHA - 1,'DD/MM/YYYY') ||''',''DD/MM/YYYY'')
                            AND TIPOMOVIMIENTO.CLASE        = ''E''
                            AND TIPOMOVIMIENTO.TIPOELEMENTO = ''D''';

        MI_MERGEENLACE :='VISTA.COMPANIA       = TABLA.COMPANIA
                      AND VISTA.TIPOMOVIMIENTO = TABLA.TIPOMOVIMIENTO
                      AND VISTA.MOVIMIENTO     = TABLA.NUMERO';

        MI_MERGEEXISTE := 'UPDATE SET FECHA         = VISTA.FECHAA
                                     ,MODIFIED_BY   = '''||UN_USUARIO||'''
                                     ,DATE_MODIFIED = SYSDATE';

        MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA      => MI_TABLA_M
                                        ,UN_ACCION      => 'MM'
                                        ,UN_MERGEUSING  => MI_MERGEUSING
                                        ,UN_MERGEENLACE => MI_MERGEENLACE
                                        ,UN_MERGEEXISTE => MI_MERGEEXISTE);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ACTU_FECHAMOV
                                ,UN_TABLAERROR => MI_TABLA_M);
    END;
    BEGIN
      BEGIN
         MI_MERGEUSING := 'SELECT 
                             TO_DATE(''' || TO_CHAR(MI_DTEFECHA,'DD/MM/YYYY') || ''',''DD/MM/YYYY'') FECHAA
                            ,D_MOVIMIENTO.CODIGO
                            ,D_MOVIMIENTO.MOVIMIENTO
                            ,D_MOVIMIENTO.TIPOMOVIMIENTO
                            ,D_MOVIMIENTO.COMPANIA
                           FROM D_MOVIMIENTO
                               ,TIPOMOVIMIENTO
                           WHERE D_MOVIMIENTO.TIPOMOVIMIENTO = TIPOMOVIMIENTO.CODIGO
                             AND D_MOVIMIENTO.COMPANIA       = TIPOMOVIMIENTO.COMPANIA
                             AND D_MOVIMIENTO.COMPANIA       = '''||UN_COMPANIA||'''
                             AND D_MOVIMIENTO.FECHA < TO_DATE('''|| TO_CHAR(MI_DTEFECHA,'DD/MM/YYYY') ||''',''DD/MM/YYYY'')
                             AND TIPOMOVIMIENTO.CLASE        = ''S''
                             AND TIPOMOVIMIENTO.TIPOELEMENTO = ''D''';

        MI_MERGEENLACE:='VISTA.COMPANIA       = TABLA.COMPANIA
                     AND VISTA.TIPOMOVIMIENTO = TABLA.TIPOMOVIMIENTO
                     AND VISTA.MOVIMIENTO     = TABLA.MOVIMIENTO
                     AND VISTA.CODIGO         = TABLA.CODIGO';

        MI_MERGEEXISTE := 'UPDATE SET FECHA         = VISTA.FECHAA
                                     ,MODIFIED_BY   = '''||UN_USUARIO||'''
                                     ,DATE_MODIFIED = SYSDATE';

        MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA_DM
                                        ,UN_ACCION      => 'MM'
                                        ,UN_MERGEUSING  => MI_MERGEUSING
                                        ,UN_MERGEENLACE => MI_MERGEENLACE
                                        ,UN_MERGEEXISTE => MI_MERGEEXISTE);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ACTU_FECHADMOV
                                ,UN_TABLAERROR => MI_TABLA_DM);
    END;
    BEGIN
      BEGIN
        MI_MERGEUSING := 'SELECT DISTINCT 
                            TO_DATE(''' || TO_CHAR(MI_DTEFECHA,'DD/MM/YYYY') || ''',''DD/MM/YYYY'') FECHAA
                           ,D_MOVIMIENTO.MOVIMIENTO
                           ,D_MOVIMIENTO.TIPOMOVIMIENTO
                           ,D_MOVIMIENTO.COMPANIA
                          FROM D_MOVIMIENTO, TIPOMOVIMIENTO
                          WHERE D_MOVIMIENTO.TIPOMOVIMIENTO = TIPOMOVIMIENTO.CODIGO
                            AND D_MOVIMIENTO.COMPANIA       = TIPOMOVIMIENTO.COMPANIA
                            AND D_MOVIMIENTO.COMPANIA       = '''||UN_COMPANIA||'''
                            AND D_MOVIMIENTO.FECHA < TO_DATE('''||TO_CHAR(MI_DTEFECHA,'DD/MM/YYYY')||''',''DD/MM/YYYY'')
                            AND TIPOMOVIMIENTO.CLASE        = ''S''
                            AND TIPOMOVIMIENTO.TIPOELEMENTO = ''D''';

        MI_MERGEENLACE := 'VISTA.MOVIMIENTO     = TABLA.NUMERO
                       AND VISTA.TIPOMOVIMIENTO = TABLA.TIPOMOVIMIENTO
                       AND VISTA.COMPANIA       = TABLA.COMPANIA';

        MI_MERGEEXISTE := 'UPDATE SET FECHA         = VISTA.FECHAA
                                     ,MODIFIED_BY   = '''||UN_USUARIO||'''
                                     ,DATE_MODIFIED = SYSDATE';

        MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA_M
                                        ,UN_ACCION      => 'MM'
                                        ,UN_MERGEUSING  => MI_MERGEUSING
                                        ,UN_MERGEENLACE => MI_MERGEENLACE
                                        ,UN_MERGEEXISTE => MI_MERGEEXISTE);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ACTU_FECHAMOV
                                ,UN_TABLAERROR => MI_TABLA_M);
    END;

    -- Se modifica filtro por concepto y se quita filtro por codigo para excluir inventario inicial ojo se quita consulta for y se coloca en consulta

    MI_SERIEANT := 0;
    --Se actualiza las fechas de entrada y adquisicion en el devolutivo cuando estas sean diferentes a la fecha del movimiento de entrada, SE ELIMINA loop <<elemento devolutivo>>
    <<DEVOLUTIVOS>> -- amonroy (18/01/2019) esto si y agregarle niif o revisar boton niif
    FOR RSTIPO IN(SELECT   
                    DEVOLUTIVO.SERIE
                   ,DEVOLUTIVO.FECHAENTRADA
                   ,DEVOLUTIVO.FECHAADQUISICION
                   ,D_MOVIMIENTO.FECHA
                  FROM DEVOLUTIVO
                    INNER JOIN D_MOVIMIENTO
                      ON DEVOLUTIVO.COMPANIA = D_MOVIMIENTO.COMPANIA
                     AND DEVOLUTIVO.SERIE    = D_MOVIMIENTO.SERIE
                     AND DEVOLUTIVO.ELEMENTO = D_MOVIMIENTO.ELEMENTO
                  WHERE DEVOLUTIVO.COMPANIA = UN_COMPANIA
                    AND (DEVOLUTIVO.FECHAADQUISICION > MI_FECHADECORTEII  
                                OR DEVOLUTIVO.FECHAADQUISICION IS NULL)
                    AND D_MOVIMIENTO.TIPOMOVIMIENTO IN (SELECT CODIGO
                                                          FROM TIPOMOVIMIENTO
                                                          WHERE COMPANIA = UN_COMPANIA
                                                            AND CONCEPTO NOT IN ('II','R')
                                                            AND CLASE    IN ('E'))
                    AND D_MOVIMIENTO.FECHA < NVL(DEVOLUTIVO.FECHAADQUISICION, '31/12/3000')
                    AND DEVOLUTIVO.SERIE BETWEEN UN_SERIEINICIAL AND UN_SERIEFINAL
                    AND DEVOLUTIVO.ELEMENTO BETWEEN UN_ELEMENTOINICIAL AND UN_ELEMENTOFINAL
                  ORDER BY 
                    DEVOLUTIVO.COMPANIA
                   ,DEVOLUTIVO.ELEMENTO
                   ,DEVOLUTIVO.SERIE
                   ,DEVOLUTIVO.FECHAENTRADA
                   ,DEVOLUTIVO.FECHAADQUISICION
                   ,D_MOVIMIENTO.FECHA)
    LOOP
      IF MI_SERIEANT <> RSTIPO.SERIE THEN
        IF RSTIPO.FECHAENTRADA <> RSTIPO.FECHA OR RSTIPO.FECHAADQUISICION <> RSTIPO.FECHA THEN
          BEGIN
            BEGIN
              MI_CAMPOS := 'FECHAENTRADA     = TO_DATE(''' || TO_CHAR(RSTIPO.FECHA,'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                           ,FECHAADQUISICION = TO_DATE(''' || TO_CHAR(RSTIPO.FECHA,'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                           ,MODIFIED_BY      = '''||UN_USUARIO||'''
                           ,DATE_MODIFIED    = SYSDATE';

              MI_CONDICION := 'DEVOLUTIVO.COMPANIA = '''|| UN_COMPANIA  ||''' 
                           AND DEVOLUTIVO.SERIE    =   '|| RSTIPO.SERIE ||' ';

              MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_D
                                              ,UN_ACCION    => 'M'
                                              ,UN_CAMPOS    => MI_CAMPOS
                                              ,UN_CONDICION => MI_CONDICION);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
            END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
            MI_MSGERROR(1).CLAVE := 'SERIE';
            MI_MSGERROR(1).VALOR := RSTIPO.SERIE;

            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE
                                      ,UN_ERROR_COD => PCK_ERRORES.ERR_ALM_ACTFECHAS_ENT_ADQ
                                      ,UN_REEMPLAZOS => MI_MSGERROR
                                      ,UN_TABLAERROR => MI_TABLA_D);
          END;
        END IF;
      END IF;

      MI_SERIEANT := RSTIPO.SERIE;
    END LOOP DEVOLUTIVOS;

   --eN ESTE Recorrido identifica los elelentos que tuvieron movimiento y no existe en tabla devolutivo
    <<INVENTARIOMOVDEVOLUT>> -- esta si
    FOR RSDEVOLUTIVO IN (SELECT 
                           D_MOVIMIENTO.COMPANIA
                          ,D_MOVIMIENTO.ELEMENTO
                          ,D_MOVIMIENTO.SERIE
                          ,D_MOVIMIENTO.VALORUNITARIO
                          ,D_MOVIMIENTO.FECHA
                          ,D_MOVIMIENTO.HORA
                          ,D_MOVIMIENTO.TIPOMOVIMIENTO
                          ,D_MOVIMIENTO.MOVIMIENTO
                          ,D_MOVIMIENTO.ESPECIFICACION
                          ,INVENTARIO.IDENTIFICADOR IDEN
                         FROM D_MOVIMIENTO
                         INNER JOIN INVENTARIO
                              ON D_MOVIMIENTO.COMPANIA = INVENTARIO.COMPANIA
                             AND D_MOVIMIENTO.ELEMENTO = INVENTARIO.CODIGOELEMENTO
                            LEFT JOIN DEVOLUTIVO
                              ON D_MOVIMIENTO.COMPANIA = DEVOLUTIVO.COMPANIA
                             AND D_MOVIMIENTO.ELEMENTO = DEVOLUTIVO.ELEMENTO
                             AND D_MOVIMIENTO.SERIE    = DEVOLUTIVO.SERIE
                        WHERE D_MOVIMIENTO.COMPANIA = UN_COMPANIA
                          AND D_MOVIMIENTO.FECHA    BETWEEN TO_DATE('01/'|| LPAD(UN_MESINICIAL,2,'0') || '/' || LPAD(UN_ANIOINICIAL,4,'0'),'DD/MM/YYYY')
                                                     AND LAST_DAY(TO_DATE('01/'|| LPAD(MI_MESFINAL,2,'0') || '/' || LPAD(MI_ANIOFINAL,4,'0'),'DD/MM/YYYY'))
                          AND D_MOVIMIENTO.ELEMENTO BETWEEN UN_ELEMENTOINICIAL AND UN_ELEMENTOFINAL
                          AND D_MOVIMIENTO.SERIE    BETWEEN UN_SERIEINICIAL AND UN_SERIEFINAL
                          AND DEVOLUTIVO.COMPANIA IS NULL
                          AND INVENTARIO.TIPO       NOT IN ('C')
                          AND D_MOVIMIENTO.IND_REG  NOT IN (0))
    LOOP
        IF RSDEVOLUTIVO.ELEMENTO IS NULL THEN
        BEGIN
          BEGIN
            MI_CAMPOS := 'COMPANIA
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
                         ,CUANTIAMIN
                         ,MODIFIED_BY
                         ,DATE_MODIFIED';

            MI_VALORES := ''''|| RSDEVOLUTIVO.COMPANIA                ||'''
                         ,'''|| RSDEVOLUTIVO.ELEMENTO                ||'''
                         ,  '|| RSDEVOLUTIVO.SERIE                   ||'
                         ,'''|| NVL(RSDEVOLUTIVO.IDEN,' ')           ||'''
                         ,0
                         ,  '|| RSDEVOLUTIVO.FECHA                   ||'
                         ,  '|| RSDEVOLUTIVO.FECHA                   ||'
                         ,  '|| RSDEVOLUTIVO.FECHA                   ||'
                         ,TO_DATE('''||TO_CHAR(RSDEVOLUTIVO.HORA,'DD/MM/YYYY HH24:MI:SS')||''',''DD/MM/YYYY HH24:MI:SS'')
                         ,'''|| NVL(RSDEVOLUTIVO.ESPECIFICACION,' ') ||'''
                         ,'''|| MI_STRBODEGA                         ||'''
                         ,'''|| MI_STRRESPONSABLE                    ||'''
                         ,-1
                         ,'''||UN_USUARIO                            ||'''
                         ,SYSDATE';

            MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA  => MI_TABLA_D
                                            ,UN_ACCION => 'I'
                                            ,UN_CAMPOS =>  MI_CAMPOS
                                            ,UN_VALORES=> MI_VALORES);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
          END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
          MI_MSGERROR(1).CLAVE := 'ELEMENTO';
          MI_MSGERROR(1).VALOR := RSDEVOLUTIVO.ELEMENTO;
          MI_MSGERROR(2).CLAVE := 'SERIE';
          MI_MSGERROR(2).VALOR := RSDEVOLUTIVO.SERIE;

          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALM_INS_DEVOLUTIVO_SM
                                    ,UN_REEMPLAZOS => MI_MSGERROR
                                    ,UN_TABLAERROR => MI_TABLA_D);
        END;
        END IF;
    END LOOP INVENTARIOMOVDEVOLUT;

    BEGIN
      BEGIN
      --SI LA FECHA o plAca anulada no son correctpos por defecto se asume que la placa no es anulada
      -- amonroy (18/01/2019) No debería actualizar todo a nulo. 
      -- 1.Evaluar las placas que en movimientos están anuladas y en el devolutivo no tienen ni fecha ni el indicador de anulado
      -- 2.Que en la devolutivo tienen el indicador y fecha pero no tiene movimiento de anulación
      -- Anuladas descargo, destruccion directa, donación (traslado a otras entidades) Bodegas 40,60,80
      --YB,RM (10/04/2019) Se modifica UPDATE por MERGE debido a que solo se deben actualizar las placas que existan en DEVOLUTIVO y D_MOVIMIENTO 
        MI_TABLA_D := 'DEVOLUTIVO';
        MI_MERGEUSING := 'SELECT DISTINCT 
                            DEVOLUTIVO.COMPANIA ,
                            DEVOLUTIVO.ELEMENTO,
                            DEVOLUTIVO.SERIE
                          FROM DEVOLUTIVO
                            INNER JOIN D_MOVIMIENTO 
                              ON DEVOLUTIVO.COMPANIA = D_MOVIMIENTO.COMPANIA
                              AND DEVOLUTIVO.ELEMENTO = D_MOVIMIENTO.ELEMENTO 
                              AND DEVOLUTIVO.SERIE = D_MOVIMIENTO.SERIE
                          WHERE DEVOLUTIVO.COMPANIA = '''||UN_COMPANIA||'''
                            AND (DEVOLUTIVO.FECHAANULADA IS NOT NULL OR DEVOLUTIVO.PLACAANULADA NOT IN (0))
                            AND DEVOLUTIVO.ELEMENTO BETWEEN ' || UN_ELEMENTOINICIAL || ' AND ' || UN_ELEMENTOFINAL || '
                            AND DEVOLUTIVO.SERIE BETWEEN ' || UN_SERIEINICIAL || ' AND ' || UN_SERIEFINAL;
        MI_MERGEENLACE := ' VISTA.COMPANIA  = TABLA.COMPANIA
                        AND VISTA.ELEMENTO  = TABLA.ELEMENTO
                        AND VISTA.SERIE     = TABLA.SERIE'  ;
        MI_MERGEEXISTE :=  ' UPDATE SET   FECHAANULADA = NULL , 
                                          PLACAANULADA = 0 , 
                                          MODIFIED_BY = '''||UN_USUARIO||''', 
                                          DATE_MODIFIED = SYSDATE';

        MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA_D
                                        ,UN_ACCION      => 'MM'
                                        ,UN_MERGEUSING  => MI_MERGEUSING
                                        ,UN_MERGEENLACE => MI_MERGEENLACE
                                        ,UN_MERGEEXISTE => MI_MERGEEXISTE);


      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
      MI_MSGERROR(1).CLAVE := 'CAMPOS';
      MI_MSGERROR(1).VALOR := 'FECHAANULADA y PLACAANULADA';
      MI_MSGERROR(2).CLAVE := 'TABLA';
      MI_MSGERROR(2).VALOR := MI_TABLA_D;      

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ACTUALIZARDMOVI
                                ,UN_REEMPLAZOS => MI_MSGERROR
                                ,UN_TABLAERROR => MI_TABLA_D);
    END;

    --(22/02/2018) @pespitia: Agrupar por compania, elemento y serie, seleccionando el registro con fecha mas reciente por grupo.
    -- pERMITE ACTUALIZAR  LA FECha e indicador dfe anulado en el devolutivo cuando el elemento ha tenido un movimiento que implica anular la placa
    -- Se reemplaa la validacion de clase y concepto por validacion de clase codiga destino para anulñar la placa
    BEGIN
      BEGIN
        MI_MERGEUSING := 'SELECT 
                            MAX(D.FECHA) FECHA
                           ,D.COMPANIA
                           ,D.ELEMENTO
                           ,D.SERIE
                          FROM D_MOVIMIENTO D
                          INNER JOIN MOVIMIENTO M
                                  ON  D.COMPANIA = M.COMPANIA
                                AND D.TIPOMOVIMIENTO = M.TIPOMOVIMIENTO
                                AND D.MOVIMIENTO = M.NUMERO
                            INNER JOIN TIPOMOVIMIENTO T
                               ON D.COMPANIA       = T.COMPANIA
                              AND D.TIPOMOVIMIENTO = T.CODIGO
                          WHERE D.COMPANIA = '''||UN_COMPANIA||'''
                            AND T.TIPOELEMENTO NOT IN (''C''        ) 
                            AND (M.CLASE_BODEGA_DESTINO IN (''60'',''80'') OR (M.CLASE_BODEGA_DESTINO IN (''90'') AND T.TIPOELEMENTO IN (''E'')))
                            AND D.IND_REG      NOT IN (0            )
                            AND  D.ELEMENTO BETWEEN ' || UN_ELEMENTOINICIAL || ' AND ' || UN_ELEMENTOFINAL || '
                            AND D.SERIE    BETWEEN ' || UN_SERIEINICIAL || ' AND ' || UN_SERIEFINAL || '
                          GROUP BY 
                            D.COMPANIA
                           ,D.ELEMENTO
                           ,D.SERIE';

        MI_MERGEENLACE := 'VISTA.COMPANIA = TABLA.COMPANIA
                       AND VISTA.ELEMENTO = TABLA.ELEMENTO
                       AND VISTA.SERIE    = TABLA.SERIE';

        MI_MERGEEXISTE := 'UPDATE SET FECHAANULADA  = VISTA.FECHA
                                     ,PLACAANULADA  = -1
                                     ,MODIFIED_BY   = '''||UN_USUARIO||'''
                                     ,DATE_MODIFIED = SYSDATE';

        MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA_D
                                        ,UN_ACCION      => 'MM'
                                        ,UN_MERGEUSING  => MI_MERGEUSING
                                        ,UN_MERGEENLACE => MI_MERGEENLACE
                                        ,UN_MERGEEXISTE => MI_MERGEEXISTE);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_A_FCRDH_MM_PLACAYFECHAANU
                                ,UN_TABLAERROR => MI_TABLA_D);
    END;


    PR_ACTUALIZARVLR(UN_COMPANIA        => UN_COMPANIA
                    ,UN_ELEMENTOINICIAL => UN_ELEMENTOINICIAL
                    ,UN_ELEMENTOFINAL   => UN_ELEMENTOFINAL
                    ,UN_PLACAINICIAL    => UN_SERIEINICIAL
                    ,UN_PLACAFINAL      => UN_SERIEFINAL
                    ,UN_USUARIO         => UN_USUARIO);
    BEGIN
      BEGIN
      -- actualiza el identificador en el devolutivo de acuerdo al configurado en la tabla inventario
      --amonroy este si
        MI_MERGEUSING := 'SELECT 
                            INVENTARIO.IDENTIFICADOR
                           ,DEVOLUTIVO.COMPANIA
                           ,DEVOLUTIVO.ELEMENTO
                          FROM DEVOLUTIVO 
                            INNER JOIN INVENTARIO
                               ON DEVOLUTIVO.COMPANIA = INVENTARIO.COMPANIA
                              AND DEVOLUTIVO.ELEMENTO = INVENTARIO.CODIGOELEMENTO
                          WHERE DEVOLUTIVO.COMPANIA = ''' || UN_COMPANIA || '''
                            AND DEVOLUTIVO.PLACA IS NULL';

        MI_MERGEENLACE := 'VISTA.COMPANIA = TABLA.COMPANIA
                       AND VISTA.ELEMENTO = TABLA.ELEMENTO';

        MI_MERGEEXISTE := 'UPDATE SET PLACA         = VISTA.IDENTIFICADOR
                                     ,MODIFIED_BY   = '''||UN_USUARIO||'''
                                     ,DATE_MODIFIED = SYSDATE';

        MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA_D
                                        ,UN_ACCION      => 'MM'
                                        ,UN_MERGEUSING  => MI_MERGEUSING
                                        ,UN_MERGEENLACE => MI_MERGEENLACE
                                        ,UN_MERGEEXISTE => MI_MERGEEXISTE);                                     

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
      END;      

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN      
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALM_ACT_IDENTIFICADOR
                                ,UN_TABLAERROR => MI_TABLA_D);
    END;
   /* BEGIN
      BEGIN
      -- actualizar la fecha de adquisicion del devolutivo en base a la fecha de adquisicion de la orden de compra del inventario inicial ('ODI')
      -- amonroy esto NO  debe ir 
        MI_MERGEUSING := 'SELECT  
                            D_ORDENDECOMPRA.FECHAADQUISICION
                           ,DEVOLUTIVO.COMPANIA
                           ,DEVOLUTIVO.ELEMENTO
                           ,DEVOLUTIVO.SERIE
                          FROM DEVOLUTIVO
                            INNER JOIN D_ORDENDECOMPRA
                               ON DEVOLUTIVO.COMPANIA = D_ORDENDECOMPRA.COMPANIA
                              AND DEVOLUTIVO.ELEMENTO = D_ORDENDECOMPRA.ELEMENTO
                              AND DEVOLUTIVO.SERIE    = D_ORDENDECOMPRA.SERIE
                          WHERE DEVOLUTIVO.COMPANIA = '''||UN_COMPANIA||'''
                            AND DEVOLUTIVO.SERIE BETWEEN '||UN_SERIEINICIAL||' AND '||UN_SERIEFINAL||' 
                            AND D_ORDENDECOMPRA.SERIE NOT IN (0)
                            AND D_ORDENDECOMPRA.CLASEORDEN = ''ODI''
                            AND D_ORDENDECOMPRA.FECHAADQUISICION IS NOT NULL
                            AND (   TRUNC(DEVOLUTIVO.FECHAADQUISICION) > TRUNC(D_ORDENDECOMPRA.FECHAADQUISICION)
                                 OR DEVOLUTIVO.FECHAADQUISICION IS NULL)';

        MI_MERGEENLACE := 'TABLA.COMPANIA = VISTA.COMPANIA
                       AND TABLA.ELEMENTO = VISTA.ELEMENTO
                       AND TABLA.SERIE    = VISTA.SERIE';

        MI_MERGEEXISTE := 'UPDATE SET FECHAADQUISICION = VISTA.FECHAADQUISICION
                                     ,MODIFIED_BY      = '''||UN_USUARIO||'''
                                     ,DATE_MODIFIED    = SYSDATE';

        MI_RTA_ACME := PCK_DATOS.FC_ACME (UN_TABLA       => MI_TABLA_D
                                         ,UN_ACCION      => 'MM'
                                         ,UN_MERGEUSING  => MI_MERGEUSING
                                         ,UN_MERGEENLACE => MI_MERGEENLACE
                                         ,UN_MERGEEXISTE => MI_MERGEEXISTE);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN      
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALM_ACT_FECHAADQ_DEV
                                ,UN_TABLAERROR => MI_TABLA_D);
    END;*/
 /*   BEGIN
      BEGIN
      ---- actualizar la fecha de salida del devolutivo en base a la fecha de salida de la orden de compra del inventario inicial ('ODI')
      -- amonroy esto no debe ir 
        MI_MERGEUSING := 'SELECT DISTINCT 
                            D_ORDENDECOMPRA.FECHASALIDASERVICIO
                           ,DEVOLUTIVO.COMPANIA
                           ,DEVOLUTIVO.ELEMENTO
                           ,DEVOLUTIVO.SERIE
                          FROM    DEVOLUTIVO
                            INNER JOIN D_ORDENDECOMPRA
                               ON DEVOLUTIVO.COMPANIA = D_ORDENDECOMPRA.COMPANIA
                              AND DEVOLUTIVO.ELEMENTO = D_ORDENDECOMPRA.ELEMENTO
                              AND DEVOLUTIVO.SERIE    = D_ORDENDECOMPRA.SERIE
                          WHERE DEVOLUTIVO.COMPANIA        = ''' || UN_COMPANIA || '''
                            AND DEVOLUTIVO.SERIE BETWEEN ' || UN_SERIEINICIAL || ' AND ' || UN_SERIEFINAL || '
                            AND DEVOLUTIVO.ELEMENTO BETWEEN ' || UN_ELEMENTOINICIAL || ' AND ' || UN_ELEMENTOFINAL || '
                            AND D_ORDENDECOMPRA.SERIE NOT IN (0)
                            AND D_ORDENDECOMPRA.CLASEORDEN = ''ODI''
                            AND D_ORDENDECOMPRA.FECHASALIDASERVICIO IS NOT NULL
                            AND (   DEVOLUTIVO.FECHASALIDASERVICIO > D_ORDENDECOMPRA.FECHASALIDASERVICIO
                                 OR DEVOLUTIVO.FECHASALIDASERVICIO IS NULL)';

        MI_MERGEENLACE := 'TABLA.COMPANIA = VISTA.COMPANIA
                       AND TABLA.ELEMENTO = VISTA.ELEMENTO
                       AND TABLA.SERIE    = VISTA.SERIE';

        MI_MERGEEXISTE := 'UPDATE SET FECHASALIDASERVICIO = VISTA.FECHASALIDASERVICIO
                                     ,MODIFIED_BY         = '''||UN_USUARIO||'''
                                     ,DATE_MODIFIED       = SYSDATE';

        MI_RTA_ACME := PCK_DATOS.FC_ACME (UN_TABLA       => MI_TABLA_D
                                         ,UN_ACCION      => 'MM'
                                         ,UN_MERGEUSING  => MI_MERGEUSING
                                         ,UN_MERGEENLACE => MI_MERGEENLACE
                                         ,UN_MERGEEXISTE => MI_MERGEEXISTE);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN       
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALM_ACT_FECHASERV_DEV
                                ,UN_TABLAERROR => MI_TABLA_D);
    END;*/

    BEGIN
      BEGIN
        MI_CAMPOS := 'VALORTOTAL    = VALORUNITARIO
                     ,MODIFIED_BY   = '''||UN_USUARIO||'''
                     ,DATE_MODIFIED = SYSDATE';

        MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || '''
                     AND SERIE NOT IN (0)
                     AND VALORTOTAL <> D_MOVIMIENTO.VALORUNITARIO';

        MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_DM
                                        ,UN_ACCION    => 'M'
                                        ,UN_CAMPOS    => MI_CAMPOS
                                        ,UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
      MI_MSGERROR(1).CLAVE := 'CAMPOS';
      MI_MSGERROR(1).VALOR := 'VALORTOTAL';
      MI_MSGERROR(2).CLAVE := 'TABLA';
      MI_MSGERROR(2).VALOR := MI_TABLA_DM;            

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ACTUALIZARDMOVI
                                ,UN_TABLAERROR => MI_TABLA_DM
                                ,UN_REEMPLAZOS => MI_MSGERROR);
    END;

   --ACTUALIZA FECHA DE ADQUISICION PLACAS CON MOVIMIENTO DE INVENTARIO INICIAL 
   --PERO SIN ORDEN DE COMPRA DE INVENTARIO INICIAL
   -- amonroy, esto no va 
  /*  BEGIN
      BEGIN
        MI_MERGEUSING := 'SELECT 
                            D_MOVIMIENTO.COMPANIA
                           ,D_MOVIMIENTO.ELEMENTO
                           ,D_MOVIMIENTO.SERIE
                           ,D_MOVIMIENTO.FECHA FROM D_MOVIMIENTO
                           INNER JOIN TIPOMOVIMIENTO 
                           ON D_MOVIMIENTO.COMPANIA = TIPOMOVIMIENTO.COMPANIA
                           AND D_MOVIMIENTO.TIPOMOVIMIENTO = TIPOMOVIMIENTO.CODIGO
                          WHERE D_MOVIMIENTO.COMPANIA = ''' || UN_COMPANIA || '''
                            AND TIPOMOVIMIENTO.CONCEPTO IN (''II'')
                            AND D_MOVIMIENTO.ELEMENTO || D_MOVIMIENTO.SERIE IN(SELECT
                                                     DEVOLUTIVO.ELEMENTO || DEVOLUTIVO.SERIE
                                                   FROM DEVOLUTIVO
                                                     INNER JOIN TIPOMOVIMIENTO 
                                                       ON DEVOLUTIVO.COMPANIA = TIPOMOVIMIENTO.COMPANIA
                                                       AND DEVOLUTIVO.TIPOMOVIMIENTOI = TIPOMOVIMIENTO.CODIGO
                                                       LEFT JOIN D_ORDENDECOMPRA
                                                       ON DEVOLUTIVO.COMPANIA = D_ORDENDECOMPRA.COMPANIA
                                                      AND DEVOLUTIVO.ELEMENTO = D_ORDENDECOMPRA.ELEMENTO
                                                      AND DEVOLUTIVO.SERIE    = D_ORDENDECOMPRA.SERIE
                                                    WHERE DEVOLUTIVO.COMPANIA        = '''||UN_COMPANIA||'''
                                                     AND DEVOLUTIVO.SERIE BETWEEN ' || UN_SERIEINICIAL || ' AND ' || UN_SERIEFINAL || '
                                                    AND DEVOLUTIVO.ELEMENTO BETWEEN ' || UN_ELEMENTOINICIAL || ' AND ' || UN_ELEMENTOFINAL || '
                                                     AND D_ORDENDECOMPRA.COMPANIA    IS NULL
                                                     AND DEVOLUTIVO.FECHAADQUISICION IS NULL
                                                     AND DEVOLUTIVO.PLACAANULADA IN (0)
                                                     AND TIPOMOVIMIENTO.CONCEPTO IN (''II''))';

        MI_MERGEENLACE := 'VISTA.COMPANIA = TABLA.COMPANIA
                       AND VISTA.ELEMENTO = TABLA.ELEMENTO
                       AND VISTA.SERIE    = TABLA.SERIE';

        MI_MERGEEXISTE := 'UPDATE SET FECHAADQUISICION = VISTA.FECHA
                                     ,MODIFIED_BY      = '''||UN_USUARIO||'''
                                     ,DATE_MODIFIED    = SYSDATE';

        MI_RTA_ACME := PCK_DATOS.FC_ACME (UN_TABLA       => MI_TABLA_D
                                         ,UN_ACCION      => 'MM'
                                         ,UN_MERGEUSING  => MI_MERGEUSING
                                         ,UN_MERGEENLACE => MI_MERGEENLACE
                                         ,UN_MERGEEXISTE => MI_MERGEEXISTE);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN 

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALM_ACT_FECHAADQ_DEVS
                                ,UN_TABLAERROR => MI_TABLA_D);
    END;*/

    --PR_REVISARFECHASSALIDASERVICIO(UN_COMPANIA        => UN_COMPANIA,
    --                               UN_ELEMENTOINICIAL => UN_ELEMENTOINICIAL,
    --                               UN_ELEMENTOFINAL   => UN_ELEMENTOFINAL,
    --                               UN_PLACAINICIAL    => UN_SERIEINICIAL,
    --                               UN_PLACAFINAL      => UN_SERIEFINAL);
    MI_RTA := MI_RTA || FC_REVISAREGISTRODEPRECIACION(UN_COMPANIA        => UN_COMPANIA
                                                     ,UN_ANIOINICIAL     => UN_ANIOINICIAL
                                                     ,UN_MESINICIAL      => UN_MESINICIAL
                                                     ,UN_ANIOFINAL       => MI_ANIOFINAL
                                                     ,UN_MESFINAL        => MI_MESFINAL
                                                     ,UN_ELEMENTOINICIAL => UN_ELEMENTOINICIAL
                                                     ,UN_ELEMENTOFINAL   => UN_ELEMENTOFINAL
                                                     ,UN_PLACAINICIAL    => UN_SERIEINICIAL
                                                     ,UN_PLACAFINAL      => UN_SERIEFINAL);

    MI_RTA := MI_RTA || FC_REVISAPLACASSINMOVIMIENTOH(UN_COMPANIA        => UN_COMPANIA
                                                     ,UN_ANIOINICIAL     => UN_ANIOINICIAL
                                                     ,UN_MESINICIAL      => UN_MESINICIAL
                                                     ,UN_ANIOFINAL       => UN_ANIOFINAL
                                                     ,UN_MESFINAL        => UN_MESFINAL
                                                     ,UN_ELEMENTOINICIAL => UN_ELEMENTOINICIAL
                                                     ,UN_ELEMENTOFINAL   => UN_ELEMENTOFINAL
                                                     ,UN_PLACAINICIAL    => UN_SERIEINICIAL
                                                     ,UN_PLACAFINAL      => UN_SERIEFINAL
                                                     ,UN_USUARIO         => UN_USUARIO);
    BEGIN
      BEGIN
        MI_CAMPOS := 'ESTADO        = ''B''
                     ,MODIFIED_BY   = '''||UN_USUARIO||'''
                     ,DATE_MODIFIED = SYSDATE';

        MI_CONDICION := 'ESTADO = NULL ';

        MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_D
                                        ,UN_ACCION    => 'M'
                                        ,UN_CAMPOS    => MI_CAMPOS
                                        ,UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
      MI_MSGERROR(1).CLAVE := 'CAMPOS';
      MI_MSGERROR(1).VALOR := 'ESTADO';
      MI_MSGERROR(2).CLAVE := 'TABLA';
      MI_MSGERROR(2).VALOR := MI_TABLA_D;       

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ACTUALIZARDMOVI
                                ,UN_TABLAERROR => MI_TABLA_D
                                ,UN_REEMPLAZOS => MI_MSGERROR);
    END;

    RETURN MI_RTA;
  END FC_REVISADEVOLUTIVOSH;

  -- 11
  PROCEDURE PR_RECTIFICARDEVOLUTIVOS
    /*
      NAME              : PR_RECTIFICARDEVOLUTIVOS En Access --> RectificarDevolutivos
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRIGUEZ
      DATE MIGRADOR     : 12/04/2016
      TIME              : 08:22 AM
      MODIFIER          : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MODIFIED     : 16/01/2017
      TIME              : 05:31 PM
      SOURCE MODULE     : ALMACEN
      DESCRIPTION       : PROCEDIMIENTO QUE RECTIFICA LOS VALORES DEL DEVOLUTIVO.
      PARAMETERS        : UN_COMPANIA     => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_PLACAINICIAL => NUMERO DE SERIE INICIAL DE D_MOVIMIENTO.
                          UN_PLACAFINAL   => NUMERO DE SERIE FINAL DE D_MOVIMIENTO.
      MODIFICATIONS     : CORRECCION SEGUN ESTANDAR DE PROGRAMACION, REFERENCIACION DE FUNCIONES Y OPTIMIZACION
                          DE MANEJO DE ERRORES.

      @NAME : rectificarDevolutivos                          
    */
  (
    UN_COMPANIA       IN  PCK_SUBTIPOS.TI_COMPANIA
   ,UN_PLACAINICIAL   IN  PCK_SUBTIPOS.TI_SERIE
   ,UN_PLACAFINAL     IN  PCK_SUBTIPOS.TI_SERIE
  )
  AS
    MI_RTA                 CLOB;
    MI_TABLA               PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS              PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION           PCK_SUBTIPOS.TI_CONDICION;
    MI_VALOR               NUMBER;
    MI_MSGERROR            PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    MI_VALOR:=0;
    <<ELEMENTOSDMOV>>
    FOR RSVALORES IN (
      SELECT   D_MOVIMIENTO.SERIE
              ,D_MOVIMIENTO.ELEMENTO
              ,SUM(D_MOVIMIENTO.VALORTOTAL) VALORUNITARIO
      FROM     D_MOVIMIENTO
        INNER JOIN TIPOMOVIMIENTO
           ON      D_MOVIMIENTO.TIPOMOVIMIENTO = TIPOMOVIMIENTO.CODIGO
          AND      D_MOVIMIENTO.COMPANIA       = TIPOMOVIMIENTO.COMPANIA
      WHERE    D_MOVIMIENTO.COMPANIA       IN (UN_COMPANIA)
        AND    TIPOMOVIMIENTO.CLASE        = 'E'
        AND    TIPOMOVIMIENTO.CONCEPTO     IN ('C','CR','II','T')
        AND    TIPOMOVIMIENTO.TIPOELEMENTO IN ('D','N')
        AND    D_MOVIMIENTO.SERIE          BETWEEN UN_PLACAINICIAL AND UN_PLACAFINAL
      GROUP BY D_MOVIMIENTO.SERIE
              ,D_MOVIMIENTO.ELEMENTO)
    LOOP
      BEGIN
        SELECT VALOR
        INTO   MI_VALOR
          FROM DEVOLUTIVO
         WHERE COMPANIA IN (UN_COMPANIA)
           AND SERIE    = RSVALORES.SERIE
           AND ELEMENTO = RSVALORES.ELEMENTO;
        IF PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => NVL(MI_VALOR,0),
                                   UN_PRECISION => 2) <> PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => NVL(RSVALORES.VALORUNITARIO,0),
                                                                                 UN_PRECISION => 2) THEN
          BEGIN
            BEGIN
              MI_TABLA := 'DEVOLUTIVO';
              MI_CAMPOS := 'VALOR = ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => RSVALORES.VALORUNITARIO,
                                                                 UN_PRECISION => 2);
              MI_CONDICION := '    COMPANIA = ''' || UN_COMPANIA || '''
                               AND SERIE    = '   || RSVALORES.SERIE || '
                               AND ELEMENTO  = ''' || RSVALORES.ELEMENTO || '''';
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                    UN_ACCION    => 'M',
                                                    UN_CAMPOS    => MI_CAMPOS,
                                                    UN_CONDICION => MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
              MI_MSGERROR(1).CLAVE := 'CAMPOS';
              MI_MSGERROR(1).VALOR := 'VALOR';
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD   => SQLCODE,
                UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_ACTUALIZARDEVOLUT,
                UN_REEMPLAZOS => MI_MSGERROR
              );
          END;
        END IF;
        EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_VALOR := 0;
      END;
    END LOOP ELEMENTOSDMOV;
  END PR_RECTIFICARDEVOLUTIVOS;

  -- 12
  PROCEDURE PR_ACTUALIZARVLR
  /*
    NAME              : PR_ACTUALIZARVLR En Access --> ActualizarVlr
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRATION  : DIEGO ALFREDO SUESCA RODRIGUEZ
    DATE MIGRADOR     : 12/04/2016
    TIME              : 04:14 AM
    MODIFIER          : (16/01/2017) JESSICA LISSETH RAMIREZ BRICEÑO
                        (23/02/2018) PABLO ANDRES ESPITIA CUCA
    TIME              : 05:53 PM
    SOURCE MODULE     : ALMACEN
    DESCRIPTION       : PROCEDIMIENTO QUE ACTUALIZA EL VALOR DEL DEVOLUTIVO.
    PARAMETERS        : UN_COMPANIA         => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                        UN_ELEMENTOINICIAL  => ELEMENTO INICIAL DE D_MOVIMIENTO.
                        UN_ELEMENTOFINAL    => ELEMENTO FINAL DE D_MOVIMIENTO.
                        UN_PLACAINICIAL     => NUMERO DE SERIE INICIAL DE D_MOVIMIENTO.
                        UN_PLACAFINAL       => NUMERO DE SERIE FINAL DE D_MOVIMIENTO.
    MODIFICATIONS     : (16/01/2017) CORRECCION SEGUN ESTANDAR DE PROGRAMACION, REFERENCIACION DE FUNCIONES Y OPTIMIZACION
                                     DE MANEJO DE ERRORES.
                        (23/02/2018) ADICION DE CAMPOS DE AUDITORIA.
                                     INDENTACION DE PL/SQL.

    @NAME  : actualizarValor                    
    @METHOD: PUT

  */
  (
    UN_COMPANIA        IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_ELEMENTOINICIAL IN  PCK_SUBTIPOS.TI_ELEMENTO,
    UN_ELEMENTOFINAL   IN  PCK_SUBTIPOS.TI_ELEMENTO,
    UN_PLACAINICIAL    IN  PCK_SUBTIPOS.TI_SERIE,
    UN_PLACAFINAL      IN  PCK_SUBTIPOS.TI_SERIE,
    UN_USUARIO         IN  PCK_SUBTIPOS.TI_USUARIO DEFAULT PCK_CONEXION.GL_USER  -- Codigo del usuario que desencadena el proceso.
  )
  AS
    MI_TABLA               PCK_SUBTIPOS.TI_TABLA;
    MI_TABLA_D             PCK_SUBTIPOS.TI_TABLA DEFAULT 'DEVOLUTIVO';
    MI_RTA_ACME            PCK_SUBTIPOS.TI_RTA_ACME;
    MI_CAMPOS              PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION           PCK_SUBTIPOS.TI_CONDICION;
    MI_STRANT              PCK_SUBTIPOS.TI_STRSQL := NULL;
    MI_MSGERROR            PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN

    <<MOVIMIENTOS>>
    FOR RS1 IN (SELECT   
                  D_MOVIMIENTO.COMPANIA
                 ,D_MOVIMIENTO.ELEMENTO
                 ,D_MOVIMIENTO.SERIE
                 ,D_MOVIMIENTO.FECHA
                 ,D_MOVIMIENTO.HORA
                 ,D_MOVIMIENTO.VALORUNITARIO
                 ,TIPOMOVIMIENTO.CONCEPTO
                 ,TIPOMOVIMIENTO.CLASE
                 ,TIPOMOVIMIENTO.TIPOELEMENTO
                 ,MOVIMIENTO.DEPENDENCIA_DESTINO
                 ,MOVIMIENTO.TERCERO RESPONSABLE
                 ,'M' MOV
                 ,D_MOVIMIENTO.TIPOMOVIMIENTO
                 ,D_MOVIMIENTO.MOVIMIENTO
                 ,D_MOVIMIENTO.CODIGO
                FROM D_MOVIMIENTO
                  INNER JOIN TIPOMOVIMIENTO
                     ON D_MOVIMIENTO.COMPANIA       = TIPOMOVIMIENTO.COMPANIA
                    AND D_MOVIMIENTO.TIPOMOVIMIENTO = TIPOMOVIMIENTO.CODIGO
                  INNER JOIN MOVIMIENTO
                     ON D_MOVIMIENTO.COMPANIA       = MOVIMIENTO.COMPANIA
                    AND D_MOVIMIENTO.TIPOMOVIMIENTO = MOVIMIENTO.TIPOMOVIMIENTO
                    AND D_MOVIMIENTO.MOVIMIENTO     = MOVIMIENTO.NUMERO
                WHERE D_MOVIMIENTO.COMPANIA   = UN_COMPANIA
                  AND TIPOMOVIMIENTO.TIPOELEMENTO IN ('D', 'N')
                  AND IND_REG <> 0
                  AND TIPOMOVIMIENTO.CONCEPTO = 'DT'
                  AND D_MOVIMIENTO.ELEMENTO BETWEEN UN_ELEMENTOINICIAL AND UN_ELEMENTOFINAL
                  AND D_MOVIMIENTO.SERIE    BETWEEN UN_PLACAINICIAL    AND UN_PLACAFINAL
                ORDER BY 
                  D_MOVIMIENTO.COMPANIA
                 ,D_MOVIMIENTO.ELEMENTO
                 ,D_MOVIMIENTO.SERIE
                 ,D_MOVIMIENTO.FECHA
                 ,TO_CHAR(D_MOVIMIENTO.HORA,'HH24:MI:SS'))
    LOOP
      IF MI_STRANT IS NOT NULL THEN

        <<ELEMENTOS>>
        FOR RS IN (SELECT DISTINCT 
                     D_MOVIMIENTO.COMPANIA
                    ,D_MOVIMIENTO.ELEMENTO
                    ,D_MOVIMIENTO.SERIE
                   FROM D_MOVIMIENTO
                     INNER JOIN TIPOMOVIMIENTO
                        ON D_MOVIMIENTO.COMPANIA       = TIPOMOVIMIENTO.COMPANIA
                       AND D_MOVIMIENTO.TIPOMOVIMIENTO = TIPOMOVIMIENTO.CODIGO
                   WHERE D_MOVIMIENTO.COMPANIA       = UN_COMPANIA
                     AND TIPOMOVIMIENTO.TIPOELEMENTO = 'D'
                     AND TIPOMOVIMIENTO.CONCEPTO     = 'CR'
                     AND D_MOVIMIENTO.ELEMENTO BETWEEN UN_ELEMENTOINICIAL AND UN_ELEMENTOFINAL
                     AND D_MOVIMIENTO.SERIE    BETWEEN UN_PLACAINICIAL AND UN_PLACAFINAL
                     AND (D_MOVIMIENTO.ELEMENTO || LPAD(D_MOVIMIENTO.SERIE,15)) >= (RS1.ELEMENTO || LPAD(RS1.SERIE,15)))
        LOOP
          IF (RS1.ELEMENTO || LPAD(RS1.SERIE,15)) <> MI_STRANT THEN
            BEGIN
              BEGIN
                MI_CAMPOS := 'VALOR = ' || NVL(RS1.VALORUNITARIO,0);

                MI_CONDICION := 'COMPANIA = ''' || RS1.COMPANIA || '''
                             AND ELEMENTO = ''' || RS1.ELEMENTO || '''
                             AND SERIE    = '   || RS1.SERIE  ;

                MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_D
                                                ,UN_ACCION    => 'M'
                                                ,UN_CAMPOS    => MI_CAMPOS
                                                ,UN_CONDICION => MI_CONDICION);

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
              END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
              MI_MSGERROR(1).CLAVE := 'CAMPOS';
              MI_MSGERROR(1).VALOR := 'VALOR';

              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                        ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ACTUALIZARDEVOLUT
                                        ,UN_TABLAERROR => MI_TABLA_D
                                        ,UN_REEMPLAZOS => MI_MSGERROR);
            END;
          ELSE
            IF RS1.CONCEPTO = 'CR' THEN
              BEGIN
                BEGIN
                  MI_CAMPOS := ' DEVOLUTIVO.VALOR = DEVOLUTIVO.VALOR + ' || NVL(RS1.VALORUNITARIO,0);

                  MI_CONDICION := 'DEVOLUTIVO.COMPANIA = ''' || RS1.COMPANIA || '''
                               AND DEVOLUTIVO.ELEMENTO = ''' || RS1.ELEMENTO || '''
                               AND DEVOLUTIVO.SERIE    = '   || RS1.SERIE  ;

                  MI_RTA_ACME := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_D
                                                  ,UN_ACCION    => 'M'
                                                  ,UN_CAMPOS    => MI_CAMPOS
                                                  ,UN_CONDICION => MI_CONDICION);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                MI_MSGERROR(1).CLAVE := 'CAMPOS';
                MI_MSGERROR(1).VALOR := 'VALOR';

                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                          ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_ACTUALIZARDEVOLUT
                                          ,UN_TABLAERROR => MI_TABLA_D
                                          ,UN_REEMPLAZOS => MI_MSGERROR);
              END;
            END IF;
          END IF;
        END LOOP ELEMENTOS;

        MI_STRANT := RS1.ELEMENTO || LPAD(RS1.SERIE,15);
      ELSE
        MI_STRANT := RS1.ELEMENTO || LPAD(RS1.SERIE,15);
      END IF;
    END LOOP MOVIMIENTOS;
  END PR_ACTUALIZARVLR;

  -- 13
 PROCEDURE PR_ACTUALIZADEPENDENCIA
    /*
      NAME              : PR_ACTUALIZADEPENDENCIA En Access --> ActualizaDependencia
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRIGUEZ
      DATE MIGRADOR     : 28/04/2016
      TIME              : 15:48
      MODIFIER          : JESSICA LISSETH RAMIREZ BRICEÑO  -- JOSE PASCUAL GÓMEZ
      DATE MODIFIED     : 17/01/2017                       -- 31/01/2017
      TIME              : 08:42 AM                         -- 11:48
      MODIFIER          : JOSE PASCUAL GÓMEZ
      DATE MODIFIED     : 22/11/2018
      TIME              : 12:42 PM  
      SOURCE MODULE     : ALMACEN
      DESCRIPTION       : PROCEDIMIENTO QUE ACTUALIZA DEPENDENCIAS DE DEVOLUTIVO.
      PARAMETERS        : UN_COMPANIA         => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_ELEMENTOINICIAL  => ELEMENTO INICIAL DE DEVOLUTIVO.
                          UN_ELEMENTOFINAL    => ELEMENTO FINAL DE DEVOLUTIVO.
                          UN_PLACAINICIAL     => NUMERO DE SERIE INICIAL DE DEVOLUTIVO.
                          UN_PLACAFINAL       => NUMERO DE SERIE FINAL DE DEVOLUTIVO.
                          UN_MESFINAL         => MES FINAL DEL PERIODO EN EL QUE SE DESEA ACTUALIZAR DEPENDENCIAS.
                          UN_ANIOFINAL        => ANIO FINAL DEL PERIODO EN EL QUE SE DESEA ACTUALIZAR DEPENDENCIAS.
      MODIFICATIONS     : CORRECCION SEGUN ESTANDAR DE PROGRAMACION, REFERENCIACION DE FUNCIONES Y OPTIMIZACION
                          DE MANEJO DE ERRORES.
                          Se ajusta la consulta de RS con el fin de que sea un solo recorrido y no con una consulta interna
                          que demora demasiado el proceso
                        : Se ajusta la consulta para que solo actualice los que no tienen los datos del ùltimo movimiento
      @NAME: actualizarDependencia                           
    */
  (
    UN_COMPANIA             IN  PCK_SUBTIPOS.TI_COMPANIA
   ,UN_ELEMENTOINICIAL      IN  PCK_SUBTIPOS.TI_ELEMENTO
   ,UN_ELEMENTOFINAL        IN  PCK_SUBTIPOS.TI_ELEMENTO
   ,UN_PLACAINICIAL         IN  PCK_SUBTIPOS.TI_SERIE
   ,UN_PLACAFINAL           IN  PCK_SUBTIPOS.TI_SERIE
   ,UN_MESFINAL             IN  PCK_SUBTIPOS.TI_MES
   ,UN_ANIOFINAL            IN  PCK_SUBTIPOS.TI_ANIO
  )
  AS
    MI_DIAFINAL             DATE;
    MI_ULTIMAFECHA          DATE;
    MI_TABLA                PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
    MI_SUCURSAL             PCK_SUBTIPOS.TI_SUCURSAL;
    MI_REGISTROS            PCK_SUBTIPOS.TI_ENTERO;
    MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN    
    --22/11/2018 JP Se envia el SYSDATE pues esta funcion se utiliza solamente para actualizar el devolutivo; 
    --              el cual se debe actualizar siempre al ultimo movimiento. Si se cambia se debe ajustar el 
    --              controlador de depreciar(ParametrosAlmacenControlador) para que no dañe los datos
    --MI_DIAFINAL := LAST_DAY(TO_DATE('01/'|| LPAD(UN_MESFINAL,2,'0') || '/' || UN_ANIOFINAL,'DD/MM/YYYY'));
    --PCK_ENTORNO.PR_SETFECHAHORA(UN_FECHAHORA => TO_DATE(TO_CHAR(MI_DIAFINAL,'DD/MM/YYYY') ||' 23:59:59', 'DD/MM/YYYY hh24:mi:ss'));
    PCK_ENTORNO.PR_SETFECHAHORA(UN_FECHAHORA => SYSDATE);
    <<ELEMENTOSDEVOLUTIVO>>
    FOR RS IN(
     SELECT  DEVOLUTIVO.COMPANIA
            ,DEVOLUTIVO.ELEMENTO
            ,DEVOLUTIVO.SERIE
            ,ULTIMOMOVIMIENTO_ALM_SERIE.TIPOMOVIMIENTO
            ,ULTIMOMOVIMIENTO_ALM_SERIE.MOVIMIENTO
            ,ULTIMOMOVIMIENTO_ALM_SERIE.FECHA
            ,ULTIMOMOVIMIENTO_ALM_SERIE.RESPONSABLE_DESTINO
            ,ULTIMOMOVIMIENTO_ALM_SERIE.DEPENDENCIA_DESTINO
            ,ULTIMOMOVIMIENTO_ALM_SERIE.SUCURSAL_RESDESTINO
        FROM  DEVOLUTIVO 
        INNER JOIN (WITH D_MOV AS (
                      SELECT D.*, (D.FECHA + NUMTODSINTERVAL(TO_CHAR(D.HORA,'hh24'),'HOUR') +
                              NUMTODSINTERVAL(TO_CHAR(D.HORA,'mi'),'MINUTE') +
                              NUMTODSINTERVAL(TO_CHAR(D.HORA,'ss'),'SECOND')) AS FECHA_HORA
                        FROM D_MOVIMIENTO D
                       WHERE D.COMPANIA = UN_COMPANIA
                            AND D.ELEMENTO BETWEEN UN_ELEMENTOINICIAL AND UN_ELEMENTOFINAL
                            AND D.SERIE    BETWEEN UN_PLACAINICIAL    AND UN_PLACAFINAL
                            AND D.IND_REG NOT IN (0))
                    , ULTIMO AS (
                      SELECT COMPANIA, ELEMENTO, SERIE, MAX(FECHA_HORA) AS FECHA_HORA
                        FROM D_MOV
                       WHERE FECHA_HORA <= SYSDATE
                       GROUP BY COMPANIA, ELEMENTO, SERIE)
                      SELECT D.COMPANIA,D.TIPOMOVIMIENTO,D.MOVIMIENTO,D.FECHA,D.ELEMENTO, D.SERIE,
                           M.RESPONSABLE_DESTINO, M.DEPENDENCIA_DESTINO, M.SUCURSAL_RESDESTINO
                      FROM D_MOV D
                      JOIN MOVIMIENTO M
                        ON D.COMPANIA = M.COMPANIA
                       AND D.MOVIMIENTO = M.NUMERO
                       AND D.TIPOMOVIMIENTO = M.TIPOMOVIMIENTO
                      JOIN ULTIMO U
                        ON D.COMPANIA = U.COMPANIA
                       AND D.ELEMENTO = U.ELEMENTO
                       AND D.SERIE = U.SERIE
                       AND D.FECHA_HORA = U.FECHA_HORA) ULTIMOMOVIMIENTO_ALM_SERIE
         ON  DEVOLUTIVO.COMPANIA = ULTIMOMOVIMIENTO_ALM_SERIE.COMPANIA
         AND  DEVOLUTIVO.ELEMENTO = ULTIMOMOVIMIENTO_ALM_SERIE.ELEMENTO
         AND  DEVOLUTIVO.SERIE    = ULTIMOMOVIMIENTO_ALM_SERIE.SERIE
       WHERE  DEVOLUTIVO.COMPANIA = UN_COMPANIA
         AND  DEVOLUTIVO.ELEMENTO BETWEEN UN_ELEMENTOINICIAL AND UN_ELEMENTOFINAL
         AND  DEVOLUTIVO.SERIE    BETWEEN UN_PLACAINICIAL    AND UN_PLACAFINAL
         AND  (DEVOLUTIVO.TIPOMOVIMIENTOF <> ULTIMOMOVIMIENTO_ALM_SERIE.TIPOMOVIMIENTO
            OR DEVOLUTIVO.MOVIMIENTOF     <> ULTIMOMOVIMIENTO_ALM_SERIE.MOVIMIENTO
            OR DEVOLUTIVO.FECHAULTMOV     <> ULTIMOMOVIMIENTO_ALM_SERIE.FECHA
            OR DEVOLUTIVO.RESPONSABLE     <> ULTIMOMOVIMIENTO_ALM_SERIE.RESPONSABLE_DESTINO
            OR DEVOLUTIVO.DEPENDENCIA     <> ULTIMOMOVIMIENTO_ALM_SERIE.DEPENDENCIA_DESTINO
            OR DEVOLUTIVO.SUCURSAL_RESPONSABLE <> ULTIMOMOVIMIENTO_ALM_SERIE.SUCURSAL_RESDESTINO)
      )
    LOOP
        BEGIN
          BEGIN
            MI_TABLA := 'DEVOLUTIVO';
            MI_CAMPOS := 'TIPOMOVIMIENTOF      = ''' || RS.TIPOMOVIMIENTO || ''' ,
                          MOVIMIENTOF          = '   || RS.MOVIMIENTO || ' ,
                          FECHAULTMOV          = TO_DATE(''' || TO_CHAR(RS.FECHA ,'DD/MM/YYYY') || ''',''DD/MM/YYYY'') ,
                          RESPONSABLE          = ''' || RS.RESPONSABLE_DESTINO || ''' ,
                          DEPENDENCIA          = ''' || RS.DEPENDENCIA_DESTINO || ''' ,
                          SUCURSAL_RESPONSABLE = ''' || RS.SUCURSAL_RESDESTINO || '''' ;
            MI_CONDICION := '    COMPANIA = ''' || UN_COMPANIA || '''
                             AND ELEMENTO = ''' || RS.ELEMENTO || '''
                             AND SERIE    = '   || RS.SERIE;
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOS,
                                                  UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
            MI_MSGERROR(1).CLAVE := 'ELEMENTO';
            MI_MSGERROR(1).VALOR := RS.ELEMENTO ;
            MI_MSGERROR(2).CLAVE := 'SERIE';
            MI_MSGERROR(2).VALOR := RS.SERIE ;
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD   => SQLCODE,
              UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_ACTUALIZARDEVOLUT,
              UN_REEMPLAZOS => MI_MSGERROR
            );
        END;
    END LOOP ELEMENTOSDEVOLUTIVO;
  END PR_ACTUALIZADEPENDENCIA;

PROCEDURE PR_KARDEXELEMENTOESPIDI
  /*
      NAME              : PR_KARDEXELEMENTOESPIDI 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JEIMMY CAROLINA ROJAS GUERRERO
      DATE MIGRADOR     : 19/08/2022
      TIME              : 
      SOURCE MODULE     : ALMACEN
      DESCRIPTION       : Procedimiento que actualiza los campos COSTOSALIDA,COSTOSALIDAAJ,VLRUNITARIOPROM,VLRAJUSTADO,SALDOKARDEX y VALORSALDO de la tabla D_MOVIMIENTO 
                          cuando el valor del parametro MANEJA PEPS CONSUMO DE ALMACEN IDIPRON se encuentre con valor SI.

  */
  (
    UN_COMPANIA             IN  PCK_SUBTIPOS.TI_COMPANIA
   ,UN_FECHAINICIAL         IN  DATE
   ,UN_FECHAFINAL           IN  DATE
   ,UN_ELEMENTO             IN  VARCHAR2
   ,UN_TIPOMOVIMIENTO       IN  VARCHAR2
   ,UN_MOVIMIENTO           IN  PCK_SUBTIPOS.TI_ENTERO_LARGO
  )
  AS
    MI_RS                   SYS_REFCURSOR;
    MI_TABLA                VARCHAR2(200 CHAR);
    MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
  BEGIN
      <<KARDEX_ESPIDI>>      
      FOR MI_RS IN (
        SELECT D_MOVIMIENTO.COMPANIA 
              ,D_MOVIMIENTO.TIPOMOVIMIENTO 
              ,D_MOVIMIENTO.MOVIMIENTO 
              ,D_MOVIMIENTO.ELEMENTO 
              ,D_MOVIMIENTO.VALORUNITARIO 
              ,D_MOVIMIENTO.VALORTOTAL
              ,D_MOVIMIENTO.CANTIDAD
              ,D_MOVIMIENTO.CODIGO
              ,D_MOVIMIENTO.SALDOKARDEX
              ,D_MOV.SALDO_PEPS
        FROM D_MOVIMIENTO
        INNER JOIN D_MOVIMIENTO D_MOV
        ON D_MOVIMIENTO.COMPANIA = D_MOV.COMPANIA
        AND D_MOVIMIENTO.TIPOMOVIMIENTO_AFECT = D_MOV.TIPOMOVIMIENTO
        AND D_MOVIMIENTO.MOVIMIENTO_AFECT = D_MOV.MOVIMIENTO
        WHERE D_MOVIMIENTO.COMPANIA         = UN_COMPANIA
            AND D_MOVIMIENTO.TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
            AND D_MOVIMIENTO.MOVIMIENTO     = UN_MOVIMIENTO
            AND D_MOVIMIENTO.ELEMENTO       = UN_ELEMENTO
            AND D_MOVIMIENTO.IND_REG        <> 0
      )
      LOOP
        BEGIN
        BEGIN
            MI_TABLA       := 'D_MOVIMIENTO';
            MI_CAMPOS      := 'D_MOVIMIENTO.VLRUNITARIOPROM    = '||MI_RS.VALORUNITARIO||
                              ',D_MOVIMIENTO.VLRAJUSTADO       = '||MI_RS.VALORUNITARIO||
                              ',D_MOVIMIENTO.SALDOKARDEX       = '||MI_RS.SALDOKARDEX||
                              ',D_MOVIMIENTO.VALORSALDO        = '||(MI_RS.SALDO_PEPS*MI_RS.VALORUNITARIO)||
                              ',D_MOVIMIENTO.COSTOSALIDA       = '||MI_RS.VALORTOTAL||
                              ',D_MOVIMIENTO.COSTOSALIDAAJ     = '||MI_RS.VALORTOTAL;

            MI_CONDICION   := 'D_MOVIMIENTO.COMPANIA           = '''||UN_COMPANIA||'''
                               AND D_MOVIMIENTO.TIPOMOVIMIENTO = '''||MI_RS.TIPOMOVIMIENTO||'''
                               AND D_MOVIMIENTO.MOVIMIENTO     = '||MI_RS.MOVIMIENTO||'
                               AND D_MOVIMIENTO.CODIGO         = '||MI_RS.CODIGO;
                                
            PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                 UN_ACCION    => 'M',
                                                 UN_CAMPOS    => MI_CAMPOS,
                                                 UN_CONDICION => MI_CONDICION);
                                                 
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
            END;
            
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                           UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_INSERMOVIMIENTO
                                           );
            END;
      END LOOP KARDEX_ESPIDI; 
  END PR_KARDEXELEMENTOESPIDI; 
  
  FUNCTION FC_KARDEXELEMENTO_PORAUX
    /*
        NAME              : FC_KARDEXELEMENTO_PORAUX
        AUTHORS           : JEIMMY CAROLINA ROJAS GUERRERO
        DESCRIPTION       : FUNCION EN LA QUE SE ACTUALIZA LA INFORMACION DE LA TABLA D_MOVIMIENTO
                            TENIENDO EN CUENTA LOS AUXILIARES: FUENTEDERECURSO,REFERENCIA_CNT,AUXILIAR,CODIGOPROYECTO,CENTRODECOSTO y LOTE
    */
    (
        UN_COMPANIA         IN  PCK_SUBTIPOS.TI_COMPANIA
       ,UN_TIPOMOVIMIENTO   IN  D_MOVIMIENTO.TIPOMOVIMIENTO%TYPE
       ,UN_MOVIMIENTO       IN  D_MOVIMIENTO.MOVIMIENTO%TYPE
       ,UN_ELEMENTO         IN  PCK_SUBTIPOS.TI_ELEMENTO
       ,UN_CODIGO           IN  D_MOVIMIENTO.CODIGO%TYPE
       ,UN_ACTPROMEDIO      IN  VARCHAR2
       ,UN_ANO              IN  PCK_SUBTIPOS.TI_ANIO DEFAULT NULL
       ,UN_MES              IN  PCK_SUBTIPOS.TI_MES  DEFAULT NULL
    )
    RETURN CLOB
    AS
        MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
        MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
        MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
        MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
        MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;
        MI_AUX_TEXTO        VARCHAR2(4000 CHAR);
        MI_RTA              CLOB;
        MI_FECHA_CONCT      VARCHAR2(20 CHAR);
        MI_FECHA            DATE;
        MI_HORA             DATE;
        MI_FECHAULTIMOMOV   DATE;
        MI_CANTIDAD         PCK_SUBTIPOS.TI_DOBLE;
        MI_PROMEDIO         PCK_SUBTIPOS.TI_DOBLE;
        MI_AJUST            PCK_SUBTIPOS.TI_DOBLE;
        MI_FECHA1           DATE;
        MI_VALORSALDO       PCK_SUBTIPOS.TI_DOBLE;
        MI_VALORSALDOANT    PCK_SUBTIPOS.TI_DOBLE;
        MI_RSCLON_VALORTOTAL PCK_SUBTIPOS.TI_DOBLE;
        MI_PROMEDIO1        PCK_SUBTIPOS.TI_DOBLE;
        MI_ANIO             PCK_SUBTIPOS.TI_ANIO;
        MI_MES              PCK_SUBTIPOS.TI_MES;
        MI_AJUSTANT         PCK_SUBTIPOS.TI_DOBLE;
        MI_CANTIDADANT      PCK_SUBTIPOS.TI_DOBLE;
        MI_RSCLON_VLRUNITARIOPROM PCK_SUBTIPOS.TI_DOBLE;
        MI_RSCLON_VLRAJUSTADO     PCK_SUBTIPOS.TI_DOBLE;
        MI_RSCLON_SALDOKARDEX     PCK_SUBTIPOS.TI_DOBLE;
        MI_RSCLON_VALORSALDO      PCK_SUBTIPOS.TI_DOBLE;
        MI_VALORSALDONEG    PCK_SUBTIPOS.TI_DOBLE;
        MI_RSCLON_COSTOSALIDA     PCK_SUBTIPOS.TI_DOBLE;
        MI_RSCLON_COSTOSALIDAAJ   PCK_SUBTIPOS.TI_DOBLE;
        MI_AUX_TEXTOERROR   CLOB;  
        MI_EXISTENCIA       INVENTARIO.EXISTENCIA%TYPE;
        MI_VALORTOTAL       INVENTARIO.VALORTOTAL%TYPE;
        MI_VLRUNITARIOPROM  INVENTARIO.VLRUNITARIOPROM%TYPE;
        MI_DBLOTROVALOR     PCK_SUBTIPOS.TI_DOBLE;
        MI_DBLVALOR         PCK_SUBTIPOS.TI_DOBLE;
    BEGIN
        MI_AUX_TEXTO := 'INCONSISTENCIAS EN SALDOS DE KARDEX. NO DEBEN QUEDAR NEGATIVOS. POR FAVOR REVISE' || CHR(10) || CHR(10);
        MI_AUX_TEXTO := MI_AUX_TEXTO || 'Deben corregirse todos de lo contrario no se incluirán en el informe de Cuenta Almacén.' || CHR(10);
        MI_RTA       := TO_CLOB(MI_AUX_TEXTO || '--------------------------------------------------------------------------------' || CHR(10));
        
        BEGIN
          SELECT TO_DATE(TO_CHAR(TO_DATE(TO_CHAR(D_MOVIMIENTO.FECHA,'DD/MM/RRRR'),'DD/MM/RRRR')
                               + NUMTODSINTERVAL(TO_CHAR(D_MOVIMIENTO.HORA,'hh24'),'HOUR')
                               + NUMTODSINTERVAL(TO_CHAR(D_MOVIMIENTO.HORA,'mi'),'MINUTE')
                               + NUMTODSINTERVAL(TO_CHAR(D_MOVIMIENTO.HORA,'ss'),'SECOND'), 'DD/MM/YYYY hh24:mi:ss')
                        ,'DD/MM/RRRR hh24:mi:ss')
            ,TO_DATE(TO_CHAR(D_MOVIMIENTO.FECHA,'DD/MM/RRRR'),'DD/MM/RRRR') FECHA
            ,HORA
          INTO MI_FECHA_CONCT
            ,MI_FECHA
            ,MI_HORA
          FROM D_MOVIMIENTO
          WHERE COMPANIA = UN_COMPANIA
            AND TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
            AND MOVIMIENTO = UN_MOVIMIENTO
            AND CODIGO = UN_CODIGO
            AND IND_REG NOT IN(0)
            AND ROWNUM = 1;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            RETURN '0';
        END;
        
        BEGIN
            MI_FECHAULTIMOMOV := MI_FECHA
                                    + NUMTODSINTERVAL(TO_CHAR(MI_HORA,'hh24'),'HOUR')
                                    + NUMTODSINTERVAL(TO_CHAR(MI_HORA,'mi'),'MINUTE')
                                    + NUMTODSINTERVAL(TO_CHAR(MI_HORA,'ss'),'SECOND');
            MI_FECHAULTIMOMOV := MI_FECHAULTIMOMOV - NUMTODSINTERVAL(1, 'SECOND');
            
            PCK_ENTORNO.PR_SETFECHAHORA(UN_FECHAHORA => MI_FECHAULTIMOMOV);
            PCK_ENTORNO.PR_SETCOMPANIA (UN_COMPANIA => UN_COMPANIA)  ;
        END;
        
        BEGIN
       WITH MOVIMIENTOS AS (
            SELECT 
                M.COMPANIA,
                M.ELEMENTO,
                M.SALDOKARDEX,
                M.VLRUNITARIOPROM,
                M.VLRAJUSTADO,
                M.FECHA,
                M.VALORSALDO,
                CAST(M.FECHA AS TIMESTAMP) + NUMTODSINTERVAL(TO_NUMBER(TO_CHAR(M.HORA, 'HH24')), 'HOUR') +
                    NUMTODSINTERVAL(TO_NUMBER(TO_CHAR(M.HORA, 'MI')), 'MINUTE') +
                    NUMTODSINTERVAL(TO_NUMBER(TO_CHAR(M.HORA, 'SS')), 'SECOND') AS FECHA_HORA_COMPLETA
            FROM D_MOVIMIENTO M
            INNER JOIN TIPOMOVIMIENTO TM
                ON M.COMPANIA = TM.COMPANIA AND M.TIPOMOVIMIENTO = TM.CODIGO
            WHERE M.COMPANIA = PCK_ENTORNO.FC_GETCOMPANIA AND
                (TM.CONCEPTO NOT IN ('T', 'DS', 'L', 'DT') OR TM.CLASE NOT IN ('T', 'D')) AND M.IND_REG <> 0),
        ULTIMO_MOV AS (
            SELECT COMPANIA, ELEMENTO, MAX(FECHA_HORA_COMPLETA) AS MAX_FECHA_HORA
            FROM MOVIMIENTOS
            WHERE FECHA_HORA_COMPLETA <= MI_FECHAULTIMOMOV
            GROUP BY COMPANIA, ELEMENTO
        )
        SELECT 
            M.SALDOKARDEX,
            M.VLRUNITARIOPROM,
            M.VLRAJUSTADO,
            M.FECHA,
            M.VALORSALDO
        INTO 
            MI_CANTIDAD,
            MI_PROMEDIO,
            MI_AJUST,
            MI_FECHA1,
            MI_VALORSALDO
        FROM MOVIMIENTOS M
        JOIN ULTIMO_MOV U
            ON M.COMPANIA = U.COMPANIA 
           AND M.ELEMENTO = U.ELEMENTO 
           AND M.FECHA_HORA_COMPLETA = U.MAX_FECHA_HORA
        WHERE M.COMPANIA = UN_COMPANIA 
          AND M.ELEMENTO = UN_ELEMENTO 
          AND ROWNUM = 1;
      EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_PROMEDIO := 0;
            MI_AJUST := 0;
            MI_CANTIDAD := 0;
            MI_MES := 0;
            MI_ANIO := 0;
            MI_AJUSTANT := 0;
            MI_CANTIDADANT := 0;
            MI_VALORSALDO := 0;
            MI_VALORSALDOANT := 0;
        END;
        
        MI_RSCLON_VALORTOTAL := 0;
        MI_PROMEDIO := NVL(MI_PROMEDIO,0);
        MI_PROMEDIO1 := NVL(MI_PROMEDIO,0);
        MI_AJUST := NVL(MI_AJUST,0);
        MI_VALORSALDO := NVL(MI_VALORSALDO,0);
        MI_CANTIDAD := PCK_SYSMAN_UTL.FC_ROUND(NVL(MI_CANTIDAD,0),6);
        MI_CANTIDADANT := MI_CANTIDAD;
        MI_VALORSALDOANT := MI_VALORSALDO;
        MI_ANIO := EXTRACT(YEAR FROM MI_FECHA1);
        
        IF MI_FECHA1 IS NULL THEN
            MI_MES := 0;
        ELSE
            MI_MES := EXTRACT(MONTH FROM MI_FECHA1);
        END IF;
        
        MI_AJUSTANT := MI_AJUST;
        MI_CANTIDADANT := MI_CANTIDAD;
        MI_VALORSALDOANT := MI_VALORSALDO;  
        
        <<MOVIMIENTOS>>
        FOR RSD_MOVIMIENTO IN (
          SELECT FUENTEDERECURSO,REFERENCIA_CNT,D_MOVIMIENTO.AUXILIAR,CODIGOPROYECTO,D_MOVIMIENTO.CENTRODECOSTO,LOTE, BODEGA_ORIGEN, BODEGA_DESTINO
          FROM D_MOVIMIENTO
          INNER JOIN TIPOMOVIMIENTO
          ON D_MOVIMIENTO.TIPOMOVIMIENTO = TIPOMOVIMIENTO.CODIGO
          AND D_MOVIMIENTO.COMPANIA = TIPOMOVIMIENTO.COMPANIA
          INNER JOIN INVENTARIO
          ON D_MOVIMIENTO.COMPANIA = INVENTARIO.COMPANIA
          AND D_MOVIMIENTO.ELEMENTO = INVENTARIO.CODIGOELEMENTO
          INNER JOIN V_MOVIMIENTO MOVIMIENTO
          ON D_MOVIMIENTO.COMPANIA = MOVIMIENTO.COMPANIA
          AND D_MOVIMIENTO.TIPOMOVIMIENTO = MOVIMIENTO.TIPOMOVIMIENTO
          AND D_MOVIMIENTO.MOVIMIENTO = MOVIMIENTO.NUMERO
          WHERE D_MOVIMIENTO.COMPANIA = UN_COMPANIA
            AND ELEMENTO = UN_ELEMENTO
            AND D_MOVIMIENTO.IND_REG NOT IN(0)
            AND (TO_CHAR(D_MOVIMIENTO.FECHA,'RRRR/MM/DD') > TO_CHAR(MI_FECHA,'RRRR/MM/DD')
                OR (TO_CHAR(D_MOVIMIENTO.FECHA,'RRRR/MM/DD') = TO_CHAR(MI_FECHA,'RRRR/MM/DD')
            AND TO_CHAR(D_MOVIMIENTO.HORA,'HH24:MI:SS') >= TO_CHAR(MI_HORA,'HH24:MI:SS')))
          ORDER BY TO_CHAR(D_MOVIMIENTO.FECHA,'RRRR/MM/DD')
            ,TO_CHAR(D_MOVIMIENTO.HORA,'hh24:mi:ss')
            ,TIPOMOVIMIENTO.CLASE
            ,D_MOVIMIENTO.TIPOMOVIMIENTO
            ,MOVIMIENTO
            ,D_MOVIMIENTO.CODIGO)
        LOOP
            MI_CANTIDAD := 0;
            MI_PROMEDIO := 0;
            MI_VALORSALDO := 0;
            <<MOVIMIENTOS_PORAUX>>
            FOR RSD_MOVIMIENTO_PORAUX IN (
              SELECT INVENTARIO.TIPO,INVENTARIO.VLRUNITARIOPROM,INVENTARIO.VALORTOTAL VT,TIPOMOVIMIENTO.CLASE,TIPOMOVIMIENTO.COSTEA
                ,INCREMENAJUS,D_MOVIMIENTO.TIPOMOVIMIENTO,D_MOVIMIENTO.MOVIMIENTO,D_MOVIMIENTO.CODIGO,D_MOVIMIENTO.FECHA
                ,D_MOVIMIENTO.VLRAJUSTADO,D_MOVIMIENTO.SALDOKARDEX,D_MOVIMIENTO.CANTIDAD,D_MOVIMIENTO.COSTOSALIDA
                ,D_MOVIMIENTO.VALORSALDO,D_MOVIMIENTO.VALORUNITARIO,MOVIMIENTO.DEPENDENCIA_DESTINO
                ,D_MOVIMIENTO.VALORTOTAL,D_MOVIMIENTO.COSTOSALIDAAJ,D_MOVIMIENTO.ELEMENTO
                ,TIPOMOVIMIENTO.CONCEPTO,MOVIMIENTO.CLASE_BODEGA_ORIGEN,MOVIMIENTO.CLASE_BODEGA_DESTINO
              FROM D_MOVIMIENTO
              INNER JOIN TIPOMOVIMIENTO
              ON D_MOVIMIENTO.TIPOMOVIMIENTO = TIPOMOVIMIENTO.CODIGO
              AND D_MOVIMIENTO.COMPANIA = TIPOMOVIMIENTO.COMPANIA
              INNER JOIN INVENTARIO
              ON D_MOVIMIENTO.COMPANIA = INVENTARIO.COMPANIA
              AND D_MOVIMIENTO.ELEMENTO = INVENTARIO.CODIGOELEMENTO
              INNER JOIN V_MOVIMIENTO MOVIMIENTO
              ON D_MOVIMIENTO.COMPANIA = MOVIMIENTO.COMPANIA
              AND D_MOVIMIENTO.TIPOMOVIMIENTO = MOVIMIENTO.TIPOMOVIMIENTO
              AND D_MOVIMIENTO.MOVIMIENTO = MOVIMIENTO.NUMERO
              WHERE D_MOVIMIENTO.COMPANIA = UN_COMPANIA
                AND ELEMENTO = UN_ELEMENTO
                AND D_MOVIMIENTO.IND_REG NOT IN(0)
                AND FUENTEDERECURSO = RSD_MOVIMIENTO.FUENTEDERECURSO
                AND REFERENCIA_CNT = RSD_MOVIMIENTO.REFERENCIA_CNT
                AND D_MOVIMIENTO.AUXILIAR = RSD_MOVIMIENTO.AUXILIAR
                AND CODIGOPROYECTO = RSD_MOVIMIENTO.CODIGOPROYECTO
                AND D_MOVIMIENTO.CENTRODECOSTO = RSD_MOVIMIENTO.CENTRODECOSTO
                AND LOTE = RSD_MOVIMIENTO.LOTE
                AND (TO_CHAR(D_MOVIMIENTO.FECHA,'RRRR/MM/DD') > TO_CHAR(MI_FECHA,'RRRR/MM/DD')
                    OR (TO_CHAR(D_MOVIMIENTO.FECHA,'RRRR/MM/DD') = TO_CHAR(MI_FECHA,'RRRR/MM/DD')
                AND  TO_CHAR(D_MOVIMIENTO.HORA,'HH24:MI:SS') >= TO_CHAR(MI_HORA,'HH24:MI:SS')))
              ORDER BY TO_CHAR(D_MOVIMIENTO.FECHA,'RRRR/MM/DD')
                ,TO_CHAR(D_MOVIMIENTO.HORA,'hh24:mi:ss')
                ,CLASE
                ,D_MOVIMIENTO.TIPOMOVIMIENTO
                ,MOVIMIENTO
                ,D_MOVIMIENTO.CODIGO)
            LOOP
                MI_RSCLON_VALORTOTAL := NVL(RSD_MOVIMIENTO_PORAUX.VALORTOTAL, 0);
                MI_AJUST := MI_PROMEDIO;
                
                IF RSD_MOVIMIENTO_PORAUX.CLASE = 'E' AND RSD_MOVIMIENTO_PORAUX.CONCEPTO = 'CR' AND RSD_MOVIMIENTO_PORAUX.CLASE_BODEGA_DESTINO NOT IN ('20') THEN 
                    MI_RSCLON_VLRUNITARIOPROM := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => MI_PROMEDIO,
                                                                         UN_PRECISION => 2);
                    MI_RSCLON_VLRAJUSTADO := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => MI_PROMEDIO,
                                                                     UN_PRECISION => 2);
                    MI_RSCLON_SALDOKARDEX := MI_CANTIDAD;
                    MI_RSCLON_VALORSALDO := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => MI_VALORSALDO,
                                                                    UN_PRECISION => 2);
                    MI_ANIO := EXTRACT(YEAR FROM RSD_MOVIMIENTO_PORAUX.FECHA);
                    MI_MES := EXTRACT(MONTH FROM RSD_MOVIMIENTO_PORAUX.FECHA);
                    
                    IF RSD_MOVIMIENTO_PORAUX.VLRUNITARIOPROM <> MI_RSCLON_VLRUNITARIOPROM OR RSD_MOVIMIENTO_PORAUX.VLRAJUSTADO <> MI_RSCLON_VLRAJUSTADO OR RSD_MOVIMIENTO_PORAUX.SALDOKARDEX <> MI_RSCLON_SALDOKARDEX OR RSD_MOVIMIENTO_PORAUX.VALORSALDO <> MI_RSCLON_VALORSALDO THEN
                      BEGIN
                        BEGIN
                          MI_TABLA :='D_MOVIMIENTO';
                          MI_CAMPOS := 'D_MOVIMIENTO.VLRUNITARIOPROM  = '||MI_RSCLON_VLRUNITARIOPROM||
                                       ',D_MOVIMIENTO.VLRAJUSTADO = '||MI_RSCLON_VLRAJUSTADO||
                                       ',D_MOVIMIENTO.SALDOCANT = '||MI_RSCLON_SALDOKARDEX||
                                       ',D_MOVIMIENTO.SALDOKARDEX = '||MI_RSCLON_SALDOKARDEX||
                                       ',D_MOVIMIENTO.VALORSALDO = '||MI_RSCLON_VALORSALDO;
            
                          MI_CONDICION := 'D_MOVIMIENTO.COMPANIA = '''||UN_COMPANIA||'''
                                           AND D_MOVIMIENTO.TIPOMOVIMIENTO = '''||RSD_MOVIMIENTO_PORAUX.TIPOMOVIMIENTO||'''
                                           AND D_MOVIMIENTO.MOVIMIENTO = '||RSD_MOVIMIENTO_PORAUX.MOVIMIENTO||'
                                           AND D_MOVIMIENTO.CODIGO = '||RSD_MOVIMIENTO_PORAUX.CODIGO;
                                           
                          PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA,
                                                               UN_ACCION => 'M',
                                                               UN_CAMPOS => MI_CAMPOS,
                                                               UN_CONDICION => MI_CONDICION);
                                                               
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                        END;
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                                                   UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_INSERMOVIMIENTO);
                      END;
                    END IF;
                ELSE
                    IF RSD_MOVIMIENTO_PORAUX.CLASE = 'E' THEN
                        IF (RSD_MOVIMIENTO_PORAUX.CANTIDAD+MI_CANTIDAD) <> 0 THEN
                            IF RSD_MOVIMIENTO_PORAUX.COSTEA <> 0 THEN
                                IF (MI_CANTIDAD + RSD_MOVIMIENTO_PORAUX.CANTIDAD) <> 0 THEN
                                    MI_PROMEDIO := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_CANTIDAD * MI_PROMEDIO + RSD_MOVIMIENTO_PORAUX.CANTIDAD * RSD_MOVIMIENTO_PORAUX.VALORUNITARIO) / (MI_CANTIDAD + RSD_MOVIMIENTO_PORAUX.CANTIDAD),
                                                                           UN_PRECISION => 2);
                                ELSIF RSD_MOVIMIENTO_PORAUX.CANTIDAD <> 0 THEN
                                    MI_PROMEDIO := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_CANTIDAD * MI_PROMEDIO + RSD_MOVIMIENTO_PORAUX.VALORUNITARIO / MI_CANTIDAD) / MI_CANTIDAD,
                                                                           UN_PRECISION => 2);
                                END IF;
                                IF (MI_CANTIDAD + RSD_MOVIMIENTO_PORAUX.CANTIDAD) <> 0 THEN
                                    MI_AJUST := (((MI_CANTIDAD * MI_AJUST + RSD_MOVIMIENTO_PORAUX.CANTIDAD * RSD_MOVIMIENTO_PORAUX.VALORUNITARIO) / (MI_CANTIDAD + RSD_MOVIMIENTO_PORAUX.CANTIDAD)) * 100) / 100;
                                ELSE
                                    MI_AJUST := (((MI_CANTIDAD * MI_AJUST + RSD_MOVIMIENTO_PORAUX.VALORUNITARIO / MI_CANTIDAD) / MI_CANTIDAD) * 100) / 100;
                                END IF;
                                
                                MI_CANTIDAD := MI_CANTIDAD + RSD_MOVIMIENTO_PORAUX.CANTIDAD;
                                MI_CANTIDAD := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => MI_CANTIDAD,
                                                                       UN_PRECISION => 6);
                                                                       
                                IF RSD_MOVIMIENTO_PORAUX.TIPO <> 'C' THEN
                                    MI_VALORSALDO := MI_VALORSALDO + NVL(RSD_MOVIMIENTO_PORAUX.VALORTOTAL, 0);
                                ELSE
                                    MI_VALORSALDO := MI_PROMEDIO * MI_CANTIDAD;
                                END IF;   
                            ELSE
                                MI_CANTIDAD := MI_CANTIDAD + RSD_MOVIMIENTO_PORAUX.CANTIDAD;
                                MI_CANTIDAD := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => MI_CANTIDAD,    
                                                                       UN_PRECISION => 6);
                                                                       
                                MI_VALORSALDONEG := MI_VALORSALDO;
                                MI_VALORSALDO := MI_VALORSALDO + NVL(RSD_MOVIMIENTO_PORAUX.VALORTOTAL, 0);
                                
                                IF MI_CANTIDAD = 0 OR MI_VALORSALDONEG < 0 THEN
                                    MI_PROMEDIO := NVL(RSD_MOVIMIENTO_PORAUX.VALORUNITARIO, 0);
                                ELSE
                                    IF MI_VALORSALDO > 0 OR MI_PROMEDIO = 0 THEN
                                        MI_PROMEDIO := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => ABS(MI_VALORSALDO) / ABS(MI_CANTIDAD),
                                                                               UN_PRECISION => 2);
                                    END IF;
                                END IF;
                            END IF;                                                                   
                        ELSE
                            MI_PROMEDIO := RSD_MOVIMIENTO_PORAUX.VALORUNITARIO;
                            MI_AJUST := RSD_MOVIMIENTO_PORAUX.VALORUNITARIO;
                            MI_CANTIDAD := MI_CANTIDAD + RSD_MOVIMIENTO_PORAUX.CANTIDAD;
                            MI_CANTIDAD := PCK_SYSMAN_UTL.FC_ROUND(MI_CANTIDAD,6);
                            MI_VALORSALDO := MI_VALORSALDO + NVL(RSD_MOVIMIENTO_PORAUX.VALORTOTAL,0);
                        END IF;  
                    ELSE
                    
                        BEGIN
                            SELECT CANTIDAD
                                INTO MI_CANTIDAD
                            FROM INVENTARIO_BODEGA
                            WHERE COMPANIA = UN_COMPANIA
                            AND ELEMENTO = UN_ELEMENTO
                            AND BODEGA = RSD_MOVIMIENTO.BODEGA_ORIGEN
                            AND FUENTEDERECURSO = RSD_MOVIMIENTO.FUENTEDERECURSO
                            AND REFERENCIA = RSD_MOVIMIENTO.REFERENCIA_CNT
                            AND AUXILIAR = RSD_MOVIMIENTO.AUXILIAR
                            AND PROYECTO = RSD_MOVIMIENTO.CODIGOPROYECTO
                            AND CENTRODECOSTO = RSD_MOVIMIENTO.CENTRODECOSTO
                            AND LOTE = RSD_MOVIMIENTO.LOTE;
                        EXCEPTION
                            WHEN NO_DATA_FOUND THEN
                                MI_CANTIDAD := 0;
                            WHEN TOO_MANY_ROWS THEN
                                MI_CANTIDAD := 0;
                        END;
                        
                        MI_CANTIDAD := MI_CANTIDAD - RSD_MOVIMIENTO_PORAUX.CANTIDAD;
                        MI_CANTIDAD := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => MI_CANTIDAD,
                                                               UN_PRECISION => 6);
                                                               
                        IF UN_ACTPROMEDIO = 'SI' AND RSD_MOVIMIENTO_PORAUX.CLASE = 'S' AND RSD_MOVIMIENTO_PORAUX.TIPO = 'C' THEN
                            IF MI_CANTIDAD <> 0 THEN
                                MI_RSCLON_VALORTOTAL := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => MI_PROMEDIO * RSD_MOVIMIENTO_PORAUX.CANTIDAD,
                                                                                UN_PRECISION => 2);
                            ELSE
                                IF RSD_MOVIMIENTO_PORAUX.CANTIDAD <> 0 THEN
                                    MI_RSCLON_VALORTOTAL := MI_VALORSALDO;
                                ELSE
                                    MI_RSCLON_VALORTOTAL := 0;
                                END IF;
                            END IF;
                        END IF;    
                        
                        IF UN_ACTPROMEDIO = 'SI' AND RSD_MOVIMIENTO_PORAUX.COSTEA <> 0 AND RSD_MOVIMIENTO_PORAUX.TIPO = 'C' THEN
                            MI_VALORSALDO := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => MI_PROMEDIO * MI_CANTIDAD,
                                                                     UN_PRECISION => 2);
                            MI_RSCLON_COSTOSALIDA := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => RSD_MOVIMIENTO_PORAUX.CANTIDAD * MI_PROMEDIO,
                                                                             UN_PRECISION => 2);
                        ELSE
                            MI_VALORSALDO := MI_VALORSALDO - MI_RSCLON_VALORTOTAL;
                            MI_RSCLON_COSTOSALIDA := MI_RSCLON_VALORTOTAL;
                        END IF;
                        
                        MI_RSCLON_COSTOSALIDAAJ := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => RSD_MOVIMIENTO_PORAUX.CANTIDAD * MI_AJUST,
                                                                           UN_PRECISION => 2);
                                                                           
                        IF RSD_MOVIMIENTO_PORAUX.COSTOSALIDA <> MI_RSCLON_COSTOSALIDA OR RSD_MOVIMIENTO_PORAUX.COSTOSALIDAAJ<> MI_RSCLON_COSTOSALIDAAJ THEN
                            BEGIN
                              BEGIN
                                MI_TABLA := 'D_MOVIMIENTO';
                                MI_CAMPOS := 'D_MOVIMIENTO.COSTOSALIDA = '||MI_RSCLON_COSTOSALIDA||
                                             ',D_MOVIMIENTO.COSTOSALIDAAJ = '||MI_RSCLON_COSTOSALIDAAJ;
                                             
                                MI_CONDICION := 'D_MOVIMIENTO.COMPANIA = '''||UN_COMPANIA||'''
                                                 AND D_MOVIMIENTO.TIPOMOVIMIENTO = '''||RSD_MOVIMIENTO_PORAUX.TIPOMOVIMIENTO||'''
                                                 AND D_MOVIMIENTO.MOVIMIENTO = '||RSD_MOVIMIENTO_PORAUX.MOVIMIENTO||'
                                                 AND D_MOVIMIENTO.CODIGO = '||RSD_MOVIMIENTO_PORAUX.CODIGO;
                                                 
                                PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA,
                                                                     UN_ACCION => 'M',
                                                                     UN_CAMPOS => MI_CAMPOS,
                                                                     UN_CONDICION => MI_CONDICION);
                                                                     
                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                              END;
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                                MI_MSGERROR(1).CLAVE := 'COSTOSALIDA';
                                MI_MSGERROR(1).VALOR := MI_RSCLON_COSTOSALIDA;
                                MI_MSGERROR(2).CLAVE := 'COSTOSALIDAAJ';
                                MI_MSGERROR(2).CLAVE := MI_RSCLON_COSTOSALIDAAJ;
                                
                                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                                           UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_ACTUAMOVIMIENTO,
                                                           UN_REEMPLAZOS => MI_MSGERROR);
                            END;
                        END IF;
                    END IF;  
                    MI_RSCLON_VLRUNITARIOPROM := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => MI_PROMEDIO,
                                                                         UN_PRECISION => 2);
                    MI_RSCLON_VLRAJUSTADO := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => MI_AJUST,
                                                                     UN_PRECISION => 2);
                    MI_RSCLON_SALDOKARDEX := MI_CANTIDAD;
                    MI_RSCLON_VALORSALDO := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => MI_VALORSALDO,
                                                                    UN_PRECISION => 2);
                                                                    
                    IF RSD_MOVIMIENTO_PORAUX.VLRUNITARIOPROM <> MI_RSCLON_VLRUNITARIOPROM OR RSD_MOVIMIENTO_PORAUX.VLRAJUSTADO <> MI_RSCLON_VLRAJUSTADO OR RSD_MOVIMIENTO_PORAUX.SALDOKARDEX <> MI_RSCLON_SALDOKARDEX OR RSD_MOVIMIENTO_PORAUX.VALORSALDO <> MI_RSCLON_VALORSALDO THEN
                        BEGIN
                          BEGIN
                            MI_TABLA :=' D_MOVIMIENTO ';
                            MI_CAMPOS := 'D_MOVIMIENTO.VLRUNITARIOPROM  = '||MI_RSCLON_VLRUNITARIOPROM||
                                         ',D_MOVIMIENTO.VLRAJUSTADO = '||MI_RSCLON_VLRAJUSTADO||
                                         ',D_MOVIMIENTO.SALDOCANT = '||MI_RSCLON_SALDOKARDEX||
                                         ',D_MOVIMIENTO.SALDOKARDEX = '||MI_RSCLON_SALDOKARDEX||
                                         ',D_MOVIMIENTO.VALORSALDO = '||MI_RSCLON_VALORSALDO;
            
                            MI_CONDICION := 'D_MOVIMIENTO.COMPANIA = '''||UN_COMPANIA||'''
                                            AND D_MOVIMIENTO.TIPOMOVIMIENTO = '''||RSD_MOVIMIENTO_PORAUX.TIPOMOVIMIENTO||'''
                                            AND D_MOVIMIENTO.MOVIMIENTO = '||RSD_MOVIMIENTO_PORAUX.MOVIMIENTO||'
                                            AND D_MOVIMIENTO.CODIGO = '||RSD_MOVIMIENTO_PORAUX.CODIGO;
                                            
                            PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA,
                                                                 UN_ACCION => 'M',
                                                                 UN_CAMPOS => MI_CAMPOS,
                                                                 UN_CONDICION => MI_CONDICION);
                   
                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                          END;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                            MI_MSGERROR(1).CLAVE := 'VLRUNITARIOPROM';
                            MI_MSGERROR(1).VALOR := MI_RSCLON_VLRUNITARIOPROM;
                            MI_MSGERROR(2).CLAVE := 'VLRAJUSTADO';
                            MI_MSGERROR(2).VALOR := MI_RSCLON_VLRAJUSTADO;
                            MI_MSGERROR(3).CLAVE := 'SALDOKARDEX';
                            MI_MSGERROR(3).VALOR := MI_RSCLON_SALDOKARDEX;
                            MI_MSGERROR(4).CLAVE := 'VALORSALDO';
                            MI_MSGERROR(5).VALOR := MI_RSCLON_VALORSALDO;
                            
                            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                                                       UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_ACTUADMOVIMIENTO,
                                                       UN_REEMPLAZOS => MI_MSGERROR);
                        END;
                    END IF; 
                    MI_ANIO := EXTRACT(YEAR FROM RSD_MOVIMIENTO_PORAUX.FECHA);
                    MI_MES := EXTRACT(MONTH FROM RSD_MOVIMIENTO_PORAUX.FECHA);
            
                    IF MI_RSCLON_SALDOKARDEX < 0 THEN
                        MI_AUX_TEXTOERROR := RSD_MOVIMIENTO_PORAUX.TIPOMOVIMIENTO
                                              || ' - '               || RSD_MOVIMIENTO_PORAUX.MOVIMIENTO
                                              || ' Código: '         || RSD_MOVIMIENTO_PORAUX.ELEMENTO
                                              || ' Item: '           || RSD_MOVIMIENTO_PORAUX.CODIGO
                                              || ' Fecha: '          || TO_CHAR(RSD_MOVIMIENTO_PORAUX.FECHA,'DD/MM/YYYY')
                                              || ' Saldo Negativo: ' || RSD_MOVIMIENTO_PORAUX.SALDOKARDEX
                                              || CHR(10);
              
                        MI_RTA := TO_CLOB(MI_RTA || MI_AUX_TEXTOERROR);                
                    END IF;
                END IF;
            END LOOP MOVIMIENTOS_PORAUX;    
        END LOOP MOVIMIENTOS;
  
        IF MI_AUX_TEXTOERROR IS NULL THEN
            MI_RTA := '';
        ELSE    
            MI_RTA := MI_RTA || TO_CLOB('--------------------------------------------------------------------------------') || CHR(10);
            MI_RTA := TO_CLOB(MI_RTA || 'FIN DEL INFORME') || CHR(10);
        END IF;
        RETURN MI_RTA;
    END FC_KARDEXELEMENTO_PORAUX;
END PCK_ALMACEN_COM3;