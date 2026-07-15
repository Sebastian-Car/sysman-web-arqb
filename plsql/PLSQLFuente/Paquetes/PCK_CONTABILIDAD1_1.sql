create or replace PACKAGE BODY "PCK_CONTABILIDAD1" AS
/**@package:  Contabilidad **/

  FUNCTION FC_CREARCUENTA_ANIOPREPARAR
    /*
      NAME              : FC_CREARCUENTA_ANIOPREPARAR
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : LEYDI MILENA CORTÉS FORERO
      DATE MIGRADOR     : 16/03/2016
      TIME              : 3:47 PM
      SOURCE MODULE     : Contabilidad
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PERMITE CREAR LAS CUENTAS DEFINIDAS PARA EL AÑO ACTUAL DE LAS RETENCIONES
                          Y QUE NO EXISTEN EN EL AÑO A PREPARAR EN LA TABLA PLAN_CONTABLE.
      @NAME:  insertarCuentasRetenciones 
      @METHOD:  GET
    */
   (
      UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_ANIOACTUAL     IN PCK_SUBTIPOS.TI_ANIO, 
      UN_ANIOPREPARAR   IN PCK_SUBTIPOS.TI_ANIO
    )
      RETURN NUMBER
    AS
      MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
      MI_COLUMNAEVALUAR   VARCHAR2(32 CHAR);
      MI_CUENTA           PCK_SUBTIPOS.TI_CODIGOCONTA;
      MI_ANIO             PCK_SUBTIPOS.TI_ANIO;
      RS                  SYS_REFCURSOR;
      MI_CADENA_QUERY     PCK_SUBTIPOS.TI_STRSQL;
      MI_PCKDATOS         PCK_SUBTIPOS.TI_RTA_ACME ;
      MI_ERROR_FUN        PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 1;
    BEGIN
      FOR I IN 1..4 LOOP 
        IF I       = 1 THEN
          MI_COLUMNAEVALUAR := 'CUENTA_DEBITO';
        ELSIF I    = 2 THEN
          MI_COLUMNAEVALUAR := 'CUENTA_CREDITO';
        ELSIF I    = 3 THEN
          MI_COLUMNAEVALUAR := 'CUENTA_DEBITO1';
        ELSIF I    = 4 THEN
          MI_COLUMNAEVALUAR := 'CUENTA_CREDITO1';
        END IF;
        /*
         * CONSULTA QUE DEVUELVE LAS CUENTAS QUE EXISTEN EN EL AÑO ACTUAL PERO NO EN EL AÑO A PREPARAR
        */
        MI_CADENA_QUERY := 'SELECT DISTINCT ' || MI_COLUMNAEVALUAR || ', ANO
                            FROM RETENCIONES
                            WHERE COMPANIA = ' || UN_COMPANIA || 
                            ' AND ANO = ' || UN_ANIOACTUAL || 
                            ' AND ' || MI_COLUMNAEVALUAR || ' IN (SELECT CODIGO 
                                                      FROM PLAN_CONTABLE 
                                                      WHERE ANO = ' || UN_ANIOACTUAL || 
                                                      ' MINUS
                                                      SELECT CODIGO
                                                      FROM PLAN_CONTABLE
                                                      WHERE ANO = ' || UN_ANIOACTUAL ||
                                                      ' AND CODIGO IN (SELECT CODIGO
                                                                     FROM PLAN_CONTABLE
                                                                     WHERE ANO = ' || UN_ANIOPREPARAR ||'))';
        OPEN RS FOR MI_CADENA_QUERY; 
          LOOP
            FETCH RS INTO MI_CUENTA, MI_ANIO;
            EXIT WHEN RS%NOTFOUND;
              MI_CAMPOS   := 'COMPANIA, ANO, CODIGO, NOMBRE, NATURALEZA, CLASECUENTA, MOVIMIENTO,
                              MAN_CEN_CTO, MAN_AUX_TER, MAN_AUX_GEN, MAN_AUX_REF, MAN_AUX_FUE, OBLIGA_TERCERO,
                              OBLIGA_CENTRO, OBLIGA_AUXILIAR, OBLIGA_REFERENCIA, OBLIGA_FUENTE';
              MI_VALORES  := 'SELECT COMPANIA, ' ||  UN_ANIOPREPARAR || ' ANO, CODIGO, NOMBRE, NATURALEZA, CLASECUENTA, MOVIMIENTO,
                                MAN_CEN_CTO, MAN_AUX_TER, MAN_AUX_GEN, MAN_AUX_REF, MAN_AUX_FUE, OBLIGA_TERCERO,
                                OBLIGA_CENTRO, OBLIGA_AUXILIAR, OBLIGA_REFERENCIA, OBLIGA_FUENTE
                              FROM PLAN_CONTABLE
                              WHERE ANO = '|| UN_ANIOACTUAL || 
                              ' AND CODIGO = ''' || MI_CUENTA || '''' ;
              MI_PCKDATOS := PCK_DATOS.FC_ACME('PLAN_CONTABLE', 'IS', MI_CAMPOS, MI_VALORES, NULL, NULL);              
          END LOOP;
        CLOSE RS;
      END LOOP;
      IF PCK_DATOS.GL_RTA IS NULL THEN
        RETURN 0;
      ELSE 
        RETURN PCK_DATOS.GL_RTA; 
      END IF;
      EXCEPTION WHEN OTHERS THEN
      PCK_DATOS.GL_ERROR_MSG := 'Interrupción durante function Crear Cuenta Año Preparar';
      PCK_DATOS.GL_ERROR_MSG := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'CONTABILIDAD','',SQLERRM );
      RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );
  END FC_CREARCUENTA_ANIOPREPARAR;

--2
FUNCTION FC_CALCULARVLRGIRAR
/*
  NAME              : FC_CALCULARVLRGIRAR
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO
  DATE MIGRADOR     : 23/03/2016
  TIME              : 12:31 PM
  SOURCE MODULE     : Contabilidad
  MODIFIER          :
  DATE MODIFIED     :
  TIME              :
  DESCRIPTION       : permite calcular el valor a girar de un comprobante
  @NAME:  calcularValoraGirar
  @METHOD:  GET
*/
(
  UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO                 IN PCK_SUBTIPOS.TI_ANIO,
  UN_TIPO                 IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
  UN_NUMERO               IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
  UN_CLASE                IN PCK_SUBTIPOS.TI_CLASECUENTACONTA,
  UN_VALORAGIRAR          IN PCK_SUBTIPOS.TI_DOBLE
)RETURN PCK_SUBTIPOS.TI_DOBLE
AS
  MI_TEMP_VALORAGIRAR   PCK_SUBTIPOS.TI_DOBLE;
  MI_CALCULARVLRGIRAR   PCK_SUBTIPOS.TI_DOBLE;
  MI_CREDITO            PCK_SUBTIPOS.TI_DOBLE;
  MI_DEBITO             PCK_SUBTIPOS.TI_DOBLE;
  MI_INDICADOR          PCK_SUBTIPOS.TI_LOGICO;
  MI_ERROR_FUN          PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 2;
BEGIN
  MI_TEMP_VALORAGIRAR:=NVL(UN_VALORAGIRAR, 0);
  IF(UN_TIPO IS NOT NULL AND UN_NUMERO IS NOT NULL AND UN_CLASE IN ('P','V')) THEN
    MI_CALCULARVLRGIRAR:=0;
    FOR MI_RS IN (SELECT DETALLE_COMPROBANTE_CNT.CUENTA,
                    PLAN_CONTABLE.CLASECUENTA,
                    DETALLE_COMPROBANTE_CNT.VALOR_CREDITO,
                    DETALLE_COMPROBANTE_CNT.VALOR_DEBITO
                  FROM DETALLE_COMPROBANTE_CNT
                  INNER JOIN PLAN_CONTABLE
                    ON DETALLE_COMPROBANTE_CNT.COMPANIA = PLAN_CONTABLE.COMPANIA
                    AND DETALLE_COMPROBANTE_CNT.CUENTA = PLAN_CONTABLE.CODIGO
                    AND DETALLE_COMPROBANTE_CNT.ANO = PLAN_CONTABLE.ANO
                  WHERE DETALLE_COMPROBANTE_CNT.COMPANIA=UN_COMPANIA
                    AND DETALLE_COMPROBANTE_CNT.ANO=UN_ANIO
                    AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE=UN_TIPO
                    AND DETALLE_COMPROBANTE_CNT.COMPROBANTE=UN_NUMERO) LOOP
      IF(UN_CLASE = 'P') THEN
      --Para cuentas por pagar
        IF(((((SUBSTR(MI_RS.CUENTA, 1,1) = '1' AND SUBSTR(MI_RS.CUENTA, 1, 2) >= '15' AND MI_RS.CLASECUENTA NOT IN ('I', 'X','Y','C'))
          OR SUBSTR(MI_RS.CUENTA, 1,1) = '2' AND (MI_RS.CLASECUENTA NOT IN ('I', 'X', 'Y') OR MI_RS.CLASECUENTA = NULL))
          AND MI_RS.VALOR_CREDITO <> 0) OR (SUBSTR(MI_RS.CUENTA, 1,1) = '2' AND (MI_RS.CLASECUENTA NOT IN ('I', 'X', 'Y') OR MI_RS.CLASECUENTA = NULL)
          AND MI_RS.VALOR_DEBITO <> 0)) AND MI_RS.CLASECUENTA NOT IN ('V', 'A', 'T', 'O')) THEN
          MI_CALCULARVLRGIRAR:=MI_CALCULARVLRGIRAR+MI_RS.VALOR_CREDITO-MI_RS.VALOR_DEBITO;
        END IF;
      ELSE
      --Para cuentas por cobrar
        IF(((((SUBSTR(MI_RS.CUENTA, 1,1) = '1' AND SUBSTR(MI_RS.CUENTA, 2) >= '15' AND MI_RS.CLASECUENTA NOT IN ('I', 'X', 'Y', 'P')) OR SUBSTR(MI_RS.CUENTA, 1,1) = '1'
          AND (MI_RS.CLASECUENTA NOT IN ('I', 'X', 'Y') OR MI_RS.CLASECUENTA = NULL)) AND MI_RS.VALOR_CREDITO <> 0) OR (SUBSTR(MI_RS.CUENTA, 1,1) = '1' AND (MI_RS.CLASECUENTA NOT IN ('I', 'X', 'Y')
          OR MI_RS.CLASECUENTA = NULL) AND MI_RS.VALOR_DEBITO <> 0))
          AND MI_RS.CLASECUENTA NOT IN ('V', 'A', 'T', 'O')) THEN
          MI_CALCULARVLRGIRAR:=MI_CALCULARVLRGIRAR+MI_RS.VALOR_DEBITO-MI_RS.VALOR_CREDITO;
        END IF;
      END IF;
    END LOOP;
  END IF;

  --Calcular valor a girar para anulaciones o devoluciones
  IF(UN_TIPO IS NOT NULL AND UN_NUMERO IS NOT NULL AND UN_CLASE IN ('D','A','J')) THEN
    MI_CALCULARVLRGIRAR:=0;
    MI_CREDITO:=0;
    MI_DEBITO:=0;
    FOR MI_RS IN (SELECT DETALLE_COMPROBANTE_CNT.CUENTA,
                    PLAN_CONTABLE.CLASECUENTA,
                    DETALLE_COMPROBANTE_CNT.VALOR_CREDITO,
                    DETALLE_COMPROBANTE_CNT.VALOR_DEBITO
                    FROM DETALLE_COMPROBANTE_CNT
                      INNER JOIN PLAN_CONTABLE
                        ON DETALLE_COMPROBANTE_CNT.CUENTA = PLAN_CONTABLE.CODIGO
                        AND DETALLE_COMPROBANTE_CNT.ANO = PLAN_CONTABLE.ANO
                        AND DETALLE_COMPROBANTE_CNT.COMPANIA = PLAN_CONTABLE.COMPANIA
                    WHERE DETALLE_COMPROBANTE_CNT.COMPANIA=UN_COMPANIA
                    AND DETALLE_COMPROBANTE_CNT.ANO=UN_ANIO
                    AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE=UN_TIPO
                    AND DETALLE_COMPROBANTE_CNT.COMPROBANTE=UN_NUMERO) LOOP
      IF(MI_RS.CLASECUENTA = 'B') THEN
        MI_CREDITO:=MI_CREDITO+MI_RS.VALOR_CREDITO;
        MI_DEBITO:=MI_DEBITO+MI_RS.VALOR_DEBITO;
      END IF;
    END LOOP;
    MI_CALCULARVLRGIRAR:=MI_DEBITO-MI_CREDITO;
  END IF;

  --Calcular valor a girar de comprobantes contables
  IF(UN_TIPO IS NOT NULL AND UN_NUMERO IS NOT NULL AND UN_CLASE = 'C') THEN
  MI_DEBITO:=0;
    FOR MI_RS IN (SELECT
                    NVL(SUM(DETALLE_COMPROBANTE_CNT.VALOR_DEBITO),0) VALORDEBITO
                  FROM DETALLE_COMPROBANTE_CNT
                  WHERE DETALLE_COMPROBANTE_CNT.COMPANIA=UN_COMPANIA
                    AND DETALLE_COMPROBANTE_CNT.ANO=UN_ANIO
                    AND TIPO_CPTE=UN_TIPO
                    AND COMPROBANTE=UN_NUMERO) LOOP
      MI_DEBITO:=MI_RS.VALORDEBITO;
    END LOOP;
    IF(MI_TEMP_VALORAGIRAR = 0) THEN
      MI_CALCULARVLRGIRAR:=MI_DEBITO;
    ELSE
      MI_CALCULARVLRGIRAR:=MI_TEMP_VALORAGIRAR;
    END IF;
    RETURN NVL(MI_CALCULARVLRGIRAR,0);
  END IF;

  IF(UN_TIPO IS NOT NULL AND UN_NUMERO IS NOT NULL AND UN_CLASE IN ('E', 'G', 'I','S','B')) THEN
    MI_CREDITO:=0;
    MI_DEBITO:=0;
    MI_INDICADOR:=0;
    FOR MI_RS IN (SELECT PLAN_CONTABLE.CLASECUENTA,
                    DETALLE_COMPROBANTE_CNT.VALOR_CREDITO,
                    DETALLE_COMPROBANTE_CNT.VALOR_DEBITO
                  FROM DETALLE_COMPROBANTE_CNT
                    INNER JOIN PLAN_CONTABLE
                      ON DETALLE_COMPROBANTE_CNT.CUENTA = PLAN_CONTABLE.CODIGO
                      AND DETALLE_COMPROBANTE_CNT.ANO = PLAN_CONTABLE.ANO
                      AND DETALLE_COMPROBANTE_CNT.COMPANIA = PLAN_CONTABLE.COMPANIA
                  WHERE DETALLE_COMPROBANTE_CNT.COMPANIA=UN_COMPANIA
                    AND DETALLE_COMPROBANTE_CNT.ANO=UN_ANIO
                    AND TIPO_CPTE=UN_TIPO
                    AND COMPROBANTE=UN_NUMERO
                  ORDER BY DETALLE_COMPROBANTE_CNT.COMPANIA,DETALLE_COMPROBANTE_CNT.ANO,DETALLE_COMPROBANTE_CNT.TIPO_CPTE,
                    DETALLE_COMPROBANTE_CNT.COMPROBANTE,DETALLE_COMPROBANTE_CNT.CONSECUTIVO) LOOP

     IF (UN_CLASE IN ('E', 'G') AND MI_RS.CLASECUENTA = 'B') THEN
        MI_CREDITO:=MI_CREDITO+MI_RS.VALOR_CREDITO;
        MI_DEBITO:=MI_DEBITO+MI_RS.VALOR_DEBITO;
        MI_INDICADOR:=1;
      ELSIF((UN_CLASE = 'I' AND MI_RS.CLASECUENTA IN ('B', 'J')) OR (UN_CLASE IN ('S','B') AND MI_RS.CLASECUENTA = 'B')) THEN
        MI_DEBITO:=MI_DEBITO+MI_RS.VALOR_DEBITO;
        MI_INDICADOR:=2;
      END IF;
    END LOOP;
  END IF;

  IF(MI_INDICADOR = 1) THEN
    MI_CALCULARVLRGIRAR:=MI_CREDITO-MI_DEBITO;
  ELSIF(MI_INDICADOR = 2) THEN
    MI_CALCULARVLRGIRAR:=MI_DEBITO;
  END IF;

  MI_TEMP_VALORAGIRAR:= NVL(MI_CALCULARVLRGIRAR,0);

  --Esta condición la adicionaron en el módulo de Access cuando utilicen la cuenta 242590 que es utilizada como temporal en almacen
  IF(UN_TIPO IS NOT NULL AND (UN_NUMERO IS NOT NULL OR UN_NUMERO NOT IN (0)) AND UN_CLASE IN ('P','E','I','B','G')) THEN
    IF(MI_TEMP_VALORAGIRAR <= 0) THEN
      BEGIN
        SELECT
          NVL(SUM(DETALLE_COMPROBANTE_CNT.VALOR_CREDITO),0) VALORCREDITO
        INTO MI_TEMP_VALORAGIRAR
        FROM DETALLE_COMPROBANTE_CNT
        INNER JOIN PLAN_CONTABLE
          ON DETALLE_COMPROBANTE_CNT.COMPANIA = PLAN_CONTABLE.COMPANIA
          AND DETALLE_COMPROBANTE_CNT.CUENTA = PLAN_CONTABLE.CODIGO
          AND DETALLE_COMPROBANTE_CNT.ANO = PLAN_CONTABLE.ANO
        WHERE DETALLE_COMPROBANTE_CNT.COMPANIA=UN_COMPANIA
          AND DETALLE_COMPROBANTE_CNT.ANO=UN_ANIO
          AND TIPO_CPTE=UN_TIPO
          AND COMPROBANTE=UN_NUMERO
          AND PLAN_CONTABLE.CLASECUENTA IN ('P','Y');
        EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_TEMP_VALORAGIRAR:=0;
      END;
      IF(MI_TEMP_VALORAGIRAR<=0) THEN
        BEGIN
          SELECT
            NVL(SUM(VALOR_CREDITO/*+VALOR_DEBITO*/),0)
          INTO MI_TEMP_VALORAGIRAR
          FROM DETALLE_COMPROBANTE_CNT
          WHERE DETALLE_COMPROBANTE_CNT.COMPANIA=UN_COMPANIA
            AND DETALLE_COMPROBANTE_CNT.ANO=UN_ANIO
            AND TIPO_CPTE=UN_TIPO
            AND COMPROBANTE=UN_NUMERO;
          EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_TEMP_VALORAGIRAR:=0;
        END;
      -- se comentarea por que no permite actualizar el valor en el CNT
      -- despues de ingresar valores en la imputacion contable para COM con la clase contable 'P'
      --El sistema no pudo determinar el valor a girar
      --Por lo tanto se deja en cero para que el usuario lo digite manualmente.

        /*IF(MI_TEMP_VALORAGIRAR <> 0) THEN
           MI_TEMP_VALORAGIRAR:=0;
          --RETURN '';
        END IF;*/

      END IF;
    END IF;
    MI_CALCULARVLRGIRAR:=MI_TEMP_VALORAGIRAR;
  END IF;
  IF(UN_TIPO IS NOT NULL AND (UN_NUMERO IS NOT NULL OR UN_NUMERO NOT IN (0)) AND UN_CLASE IN ('N')) THEN
      IF(MI_TEMP_VALORAGIRAR<=0) THEN
        BEGIN
          SELECT
            NVL(SUM(VALOR_CREDITO/*+VALOR_DEBITO*/),0)
          INTO MI_TEMP_VALORAGIRAR
          FROM DETALLE_COMPROBANTE_CNT
          WHERE DETALLE_COMPROBANTE_CNT.COMPANIA=UN_COMPANIA
            AND DETALLE_COMPROBANTE_CNT.ANO=UN_ANIO
            AND TIPO_CPTE=UN_TIPO
            AND COMPROBANTE=UN_NUMERO;
          EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_TEMP_VALORAGIRAR:=0;
        END;
        MI_CALCULARVLRGIRAR:=MI_TEMP_VALORAGIRAR;
      END IF;
    END IF;

  RETURN NVL(MI_CALCULARVLRGIRAR,0);
  EXCEPTION WHEN OTHERS THEN
      PCK_DATOS.GL_ERROR_MSG := 'Interrupción durante el cálculo del valor a girar';
      PCK_DATOS.GL_ERROR_MSG := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'CONTABILIDAD','',SQLERRM );
      RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );
END FC_CALCULARVLRGIRAR;

--3
PROCEDURE PR_ELIMINARCOMPROBANTEPPTAL
/*
  NAME              : FC_ELIMINARCOMPROBANTEPPTAL Nombre en Access EliminarComprobantePptal
  AUTHORS           : STEFANINI SYSMAN SAS
  AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO
  DATE MIGRADOR     : 28/03/2016
  TIME              : 10:00 AM
  SOURCE MODULE     : Contabilidad
  MODIFIER          : AURA LILIANA MONROY GARCIA
  DATE MODIFIED     : 01/12/2017
  TIME              : 17:30 
  MODIFICATIONS     : Corrección según estándar de programación y optimización de manejo de errores.
                      Se realiza el cambio de función a procedimiento, se adiciona el parámetro UN_USUARIO para los campos de auditoría y
                      se agregan validaciones relacionadas con la fecha, si el comprobante ya fue impreso o si ya fue afectado, teniendo
                      en cuenta el procedimiento PCK_PRESUPUESTO.PR_ELIMINAR_COMPROBANTEPPTAL, que realiza un proceso similar al que 
                      definido en el presente procedimiento
  DESCRIPTION       : Permite eliminar un comprobante pptal generado si este no está asociado a otras transacciones
  @NAME:  eliminarComprobantePresupuestal 
  @METHOD:  GET
*/
(
  UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO                 IN PCK_SUBTIPOS.TI_ANIO,
  UN_TIPO                 IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
  UN_NUMERO               IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
  UN_USUARIO              IN PCK_SUBTIPOS.TI_USUARIO
)
AS
  MI_AUX                  PCK_SUBTIPOS.TI_RTA_ACME;
  MI_CLASECOMPROBANTE     PCK_SUBTIPOS.TI_CLASECOMPROBANTE;
  MI_TABLA                PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
  MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_ESTADOANIO		        PCK_SUBTIPOS.TI_TEXTO1;
  MI_ESTADOMES		        PCK_SUBTIPOS.TI_TEXTO1;
  MI_ESTADODIA		        PCK_SUBTIPOS.TI_TEXTO1;
  MI_MES                  PCK_SUBTIPOS.TI_MES;
  MI_DIA                  PCK_SUBTIPOS.TI_DIA;
  MI_IMPRESO              PCK_SUBTIPOS.TI_LOGICO;
  MI_AFECTACIONES         PCK_SUBTIPOS.TI_DOBLE;
BEGIN

  SELECT 
    EXTRACT(MONTH FROM FECHA) MES,
    EXTRACT(DAY FROM FECHA) DIA,
    IMPRESO,
    DEBITO_AFECTADO 
    + CREDITO_AFECTADO
    + MODIFICACION_DEBITO
    + MODIFICACION_CREDITO AFECTACIONES
  INTO
    MI_MES,
    MI_DIA,
    MI_IMPRESO,
    MI_AFECTACIONES
  FROM COMPROBANTE_PPTAL
  WHERE COMPANIA   = UN_COMPANIA
    AND ANO        = UN_ANIO
    AND TIPO       = UN_TIPO
    AND NUMERO     = UN_NUMERO
  ORDER BY NUMERO;  
  PCK_PRESUPUESTO.PR_ELIMINAR_COMPROBANTEPPTAL(UN_COMPANIA       => UN_COMPANIA,
                                               UN_ANO            => UN_ANIO,
                                               UN_TIPO           => UN_TIPO,
                                               UN_NUMERO         => UN_NUMERO,  
                                               UN_MES 			 => MI_MES,
                                               UN_DIA 			 => MI_DIA, 
                                               UN_AFECTACIONES   => MI_AFECTACIONES,
                                               UN_USUARIO        => UN_USUARIO,
                                               UN_IMPRESO        => MI_IMPRESO);
EXCEPTION WHEN NO_DATA_FOUND THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                               UN_ERROR_COD  => 'El comprobante no existe',
                               UN_REEMPLAZOS => MI_MSGERROR);    
END PR_ELIMINARCOMPROBANTEPPTAL;

--4
PROCEDURE PR_AFECTAROTROCOMPROBANTE
  /*
    NAME              : PR_AFECTAROTROCOMPROBANTE  
    AUTHORS           : STEFANINI SYSMAN SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO
    DATE MIGRADOR     : 28/03/2016
    TIME              : 12:16 AM
    SOURCE MODULE     : Contabilidad
    MODIFIER          : AURA LILIANA MONROY GARCIA
    DATE MODIFIED     : 04/12/2017
    TIME              : 15:08
    MODIFICATIONS     : Corrección según estándar de programación y optimización de manejo de errores.  
	                    Se adiciona el parámetro UN_USUARIO para los campos de auditoría
    DESCRIPTION       : 
    @NAME:  afectarOtroComprobantePresupuestal
    @METHOD:  PUT
  */
  (
    UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TIPO0              IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
    UN_TIPO               IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
    UN_NUMERO             IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
    UN_CUENTA             IN PCK_SUBTIPOS.TI_CODIGOCONTA,
    UN_CREDITOA           IN PCK_SUBTIPOS.TI_DOBLE,
    UN_CONTRACREDITOA     IN PCK_SUBTIPOS.TI_DOBLE,
    UN_CREDITO            IN PCK_SUBTIPOS.TI_DOBLE,
    UN_CONTRACREDITO      IN PCK_SUBTIPOS.TI_DOBLE,
    UN_CONSECUTIVO        IN PCK_SUBTIPOS.TI_ENTERO,
    UN_CON                IN VARCHAR2 DEFAULT NULL,
    UN_NUMERO0            IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0,
    UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_ANIOCOMPROBANTE    PCK_SUBTIPOS.TI_ANIO;
    MI_AUX                PCK_SUBTIPOS.TI_RTA_ACME;
    MI_PAR                PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CUENTA             PCK_SUBTIPOS.TI_CODIGOCONTA;
    MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    <<OBTENERDETALLES>>
    FOR MI_RS IN (SELECT
                     TIPO_CPTE_AFECT,
                     CMPTE_AFECTADO,
                     CUENTA,
                     CONSECUTIVOPPTO,
                     ANO
                   FROM DETALLE_COMPROBANTE_PPTAL
                   WHERE COMPANIA     = UN_COMPANIA
                     AND TIPO_CPTE    = UN_TIPO
                     AND COMPROBANTE  = UN_NUMERO
                     AND CUENTA       = UN_CUENTA
                     AND CONSECUTIVO  = UN_CONSECUTIVO) 
    LOOP
      MI_ANIOCOMPROBANTE := MI_RS.ANO;
      <<CONSULTARAFECTACION>>
      FOR MI_RSC IN (SELECT CLASECNTPRES.CODIGO,
                            CLASECNTPRES.AFECTACION 
                     FROM TIPO_COMPROBPP 
                       INNER JOIN CLASECNTPRES 
                          ON TIPO_COMPROBPP.CLASE = CLASECNTPRES.CODIGO 
                     WHERE TIPO_COMPROBPP.COMPANIA  = UN_COMPANIA
                       AND TIPO_COMPROBPP.CODIGO    = UN_TIPO0
                     ORDER BY TIPO_COMPROBPP.CODIGO)
      LOOP
        IF(UN_CON IS NOT NULL AND UN_CON = 'E') THEN
        --Actualiza el comprobante       
          BEGIN
            BEGIN
              MI_TABLA     := 'COMPROBANTE_PPTAL';
              MI_CAMPOS    := 'DEBITO_AFECTADOCNT  = DEBITO_AFECTADOCNT  - ' || UN_CREDITOA || ' + ' || UN_CREDITO || ', '||
                              'CREDITO_AFECTADOCNT = CREDITO_AFECTADOCNT - ' || UN_CONTRACREDITOA || ' + ' || UN_CONTRACREDITO || ', '||
                              'DATE_MODIFIED       = SYSDATE, '||
                              'MODIFIED_BY         = ''' || UN_USUARIO || ''' ';
              MI_CONDICION := '    COMPANIA = '''|| UN_COMPANIA ||''' 
                               AND ANO      =   '|| MI_ANIOCOMPROBANTE ||' 
                               AND TIPO     = '''|| UN_TIPO ||''' 
                               AND NUMERO   =   '|| UN_NUMERO;
              MI_AUX       := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                                 UN_ACCION    => 'M', 
                                                 UN_CAMPOS    => MI_CAMPOS, 
                                                 UN_CONDICION => MI_CONDICION);

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;  
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
              MI_MSGERROR(1).CLAVE := 'NUMERO';
              MI_MSGERROR(1).VALOR := UN_NUMERO ;
              PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    =>  SQLCODE,
              UN_ERROR_COD  =>  PCK_ERRORES.ERR_CONTAB_ACTDCAFECT,
              UN_TABLAERROR =>  MI_TABLA,
              UN_REEMPLAZOS =>  MI_MSGERROR
              );
          END;

          --Actualiza el detalle
          BEGIN
            BEGIN
              MI_TABLA     := 'DETALLE_COMPROBANTE_PPTAL';
              MI_CONDICION := '   COMPANIA    = '''||UN_COMPANIA||''' 
                                AND ANO         =   '||MI_ANIOCOMPROBANTE||' 
                                AND TIPO_CPTE   = '''||UN_TIPO||''' 
                                AND COMPROBANTE =  '||UN_NUMERO||' 
                                AND CUENTA      = '''||UN_CUENTA||''' 
                                AND CONSECUTIVO =   '||UN_CONSECUTIVO;                           
              MI_AUX       := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                                 UN_ACCION    => 'M', 
                                                 UN_CAMPOS    => MI_CAMPOS, 
                                                 UN_CONDICION => MI_CONDICION);

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;  
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
              MI_MSGERROR(1).CLAVE := 'NUMERO';
              MI_MSGERROR(1).VALOR := UN_NUMERO ;
              PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    =>  SQLCODE,
              UN_ERROR_COD  =>  PCK_ERRORES.ERR_CONTAB_ACTDCDETAFECT,
              UN_TABLAERROR =>  MI_TABLA,
              UN_REEMPLAZOS =>  MI_MSGERROR
              );
          END;      

        ELSE
          MI_PAR:= NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                             UN_NOMBRE    => 'MANEJA CONTROL DE SOLICITUD DE DISPONIBILIDAD',
                                             UN_MODULO    => PCK_DATOS.FC_MODULOCONTABILIDAD, 
                                             UN_FECHA_PAR => SYSDATE), 'NO');

          IF(MI_RSC.CODIGO = 'DIS' AND MI_PAR = 'SI') THEN

            BEGIN
              BEGIN
                MI_TABLA     := 'BP_D_NOVEDADPROYECTO';
                MI_CAMPOS    := 'VALORAFECTADO = VALORAFECTADO - ' || UN_CREDITOA || ' + ' || UN_CREDITO|| ', '||
                                'DATE_MODIFIED = SYSDATE, '||
                                'MODIFIED_BY   = ''' || UN_USUARIO || ''' ';
                MI_CONDICION := '    COMPANIA = '''|| UN_COMPANIA ||''' 
                                 AND TIPOT    = '''|| UN_TIPO ||''' 
                                 AND NOVEDAD  =   '|| UN_NUMERO ||' 
                                 AND CODIGO   =   '|| UN_CONSECUTIVO;                          
                MI_AUX       := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                                   UN_ACCION    => 'M', 
                                                   UN_CAMPOS    => MI_CAMPOS, 
                                                   UN_CONDICION => MI_CONDICION);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;  
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                MI_MSGERROR(1).CLAVE := 'NUMERO';
                MI_MSGERROR(1).VALOR := UN_NUMERO ;
                MI_MSGERROR(2).CLAVE := 'CONSECUTIVO';
                MI_MSGERROR(2).VALOR := UN_CONSECUTIVO ;              
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    =>  SQLCODE,
                  UN_ERROR_COD  =>  PCK_ERRORES.ERR_CONTAB_ACTVAFECTN,
                  UN_TABLAERROR =>  MI_TABLA,
                  UN_REEMPLAZOS =>  MI_MSGERROR
                );
            END;               


          ELSIF(MI_RSC.AFECTACION IN ('N', 'A', 'R')) THEN
            --Afecta los comprobantes de banco de proyectos
            IF(MI_RSC.CODIGO IN ('RES', 'ADR', 'DMR') AND MI_PAR = 'SI') THEN
              IF(MI_RSC.AFECTACION IN ('N', 'A')) THEN
                --Afectación del comprobante 
                BEGIN
                  BEGIN
                    MI_TABLA     := 'BP_D_NOVEDADPROYECTO';
                    MI_CAMPOS    := 'VALORAPROBADO = VALORAPROBADO - ' || UN_CREDITOA ||' + '||UN_CREDITO|| ', '||
                                    'DATE_MODIFIED = SYSDATE, '||
                                    'MODIFIED_BY   = ''' || UN_USUARIO || ''' ';
                    MI_CONDICION := '    COMPANIA = '''||UN_COMPANIA||''' 
                                     AND TIPOT    = '''||UN_TIPO0||''' 
                                     AND NOVEDAD  =   '||UN_NUMERO0||' 
                                     AND CODIGO   =   '||UN_CONSECUTIVO;                      
                    MI_AUX       := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                                       UN_ACCION    => 'M', 
                                                       UN_CAMPOS    => MI_CAMPOS, 
                                                       UN_CONDICION => MI_CONDICION);

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                         RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;  
                  END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                  MI_MSGERROR(1).CLAVE := 'NUMERO';
                  MI_MSGERROR(1).VALOR := UN_NUMERO0 ;
                  MI_MSGERROR(2).CLAVE := 'CONSECUTIVO';
                  MI_MSGERROR(2).VALOR := UN_CONSECUTIVO ;              
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    =>  SQLCODE,
                    UN_ERROR_COD  =>  PCK_ERRORES.ERR_CONTAB_ACTVAFPROBN,
                    UN_TABLAERROR =>  MI_TABLA,
                    UN_REEMPLAZOS =>  MI_MSGERROR
                  );
                END;    


                --Comprobante afectado
                <<COMPROBANTEAFECTADO>>
                FOR MI_RSB IN (SELECT CMPTE_AFECTADO,
                                TIPO_CPTE_AFECT,
                                ITEM_AFECT 
                              FROM BP_D_NOVEDADPROYECTO
                              WHERE COMPANIA = UN_COMPANIA
                                AND TIPOT=UN_TIPO0
                                AND CLASET='P'
                                AND NOVEDAD = UN_NUMERO0
                                AND CODIGO = UN_CONSECUTIVO) 
                LOOP
                  IF(MI_RSC.CODIGO='RES') THEN 
                    --El comprobante afectado es una solicitud DIS y se afecta el valor aprobado y no la modificación                  
                    BEGIN
                      BEGIN
                        MI_TABLA     := 'BP_D_NOVEDADPROYECTO';
                        MI_CAMPOS    := 'VALORAPROBADO = VALORAPROBADO - '||UN_CREDITOA||' + '||UN_CREDITO|| ', '||
                                        'DATE_MODIFIED = SYSDATE, '||
                                        'MODIFIED_BY   = ''' || UN_USUARIO || ''' ';
                        MI_CONDICION := '    COMPANIA = ''' || UN_COMPANIA || ''' 
                                         AND TIPOT    = ''' || MI_RSB.TIPO_CPTE_AFECT || ''' 
                                         AND NOVEDAD  =   ' || MI_RSB.CMPTE_AFECTADO || ' 
                                         AND CODIGO   =   ' || MI_RSB.ITEM_AFECT;                 
                        MI_AUX       := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                                           UN_ACCION    => 'M', 
                                                           UN_CAMPOS    => MI_CAMPOS, 
                                                           UN_CONDICION => MI_CONDICION);

                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                             RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;  
                      END;
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                        MI_MSGERROR(1).CLAVE := 'NUMERO';
                        MI_MSGERROR(1).VALOR := MI_RSB.CMPTE_AFECTADO ;
                        MI_MSGERROR(2).CLAVE := 'CONSECUTIVO';
                        MI_MSGERROR(2).VALOR := MI_RSB.ITEM_AFECT ;              
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD    =>  SQLCODE,
                          UN_ERROR_COD  =>  PCK_ERRORES.ERR_CONTAB_ACTVAFPROBN,
                          UN_TABLAERROR =>  MI_TABLA,
                          UN_REEMPLAZOS =>  MI_MSGERROR
                        );
                    END;   

                  ELSE
                    --Si el comprobante es ADR o DMR se debe afectar la solicitud DIS
                    BEGIN
                      BEGIN
                        MI_TABLA     := 'BP_D_NOVEDADPROYECTO';
                        MI_CAMPOS    := 'MODIFICACIONVALORAPROBADO = MODIFICACIONVALORAPROBADO - '||UN_CREDITOA||' + '||UN_CREDITO|| ', '||
                                        'DATE_MODIFIED             = SYSDATE, '||
                                        'MODIFIED_BY               = ''' || UN_USUARIO || ''' ';
                        MI_CONDICION := '    COMPANIA = '''|| UN_COMPANIA || ''' 
                                         AND TIPOT    = '''|| MI_RSB.TIPO_CPTE_AFECT || ''' 
                                         AND NOVEDAD  =   '|| MI_RSB.CMPTE_AFECTADO || ' 
                                         AND CODIGO   =   '|| MI_RSB.ITEM_AFECT;                
                        MI_AUX       := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                                           UN_ACCION    => 'M', 
                                                           UN_CAMPOS    => MI_CAMPOS, 
                                                           UN_CONDICION => MI_CONDICION);

                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                               RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;  
                      END;
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                        MI_MSGERROR(1).CLAVE := 'NUMERO';
                        MI_MSGERROR(1).VALOR := MI_RSB.CMPTE_AFECTADO ;             
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD    =>  SQLCODE,
                          UN_ERROR_COD  =>  PCK_ERRORES.ERR_CONTAB_AFECTDIS,
                          UN_TABLAERROR =>  MI_TABLA,
                          UN_REEMPLAZOS =>  MI_MSGERROR
                        );
                    END;                    

                    <<CUENTASPOPRPAGAR>>
                    FOR MI_RSN IN (SELECT TIPO_CPTE_AFECT,  
                                      CMPTE_AFECTADO,
                                      ITEM_AFECT 
                                    FROM BP_D_NOVEDADPROYECTO 
                                    WHERE COMPANIA  = UN_COMPANIA
                                      AND TIPOT     = MI_RSB.TIPO_CPTE_AFECT
                                      AND CLASET    = 'P'
                                      AND NOVEDAD   = MI_RSB.CMPTE_AFECTADO
                                      AND CODIGO    = MI_RSB.ITEM_AFECT) 
                    LOOP
                      BEGIN
                        BEGIN
                          MI_TABLA     := 'BP_D_NOVEDADPROYECTO';
                          MI_CAMPOS    := 'VALORAPROBADO = VALORAPROBADO - '||UN_CREDITOA||' + '||UN_CREDITO|| ', '||
                                          'DATE_MODIFIED = SYSDATE, '||
                                          'MODIFIED_BY   = ''' || UN_USUARIO || ''' ';
                          MI_CONDICION := '   COMPANIA  = ''' || UN_COMPANIA || ''' 
                                          AND TIPOT     = ''' || MI_RSN.TIPO_CPTE_AFECT || ''' 
                                          AND NOVEDAD   =   ' || MI_RSN.CMPTE_AFECTADO || ' 
                                          AND CODIGO    =   ' || MI_RSN.ITEM_AFECT;            
                          MI_AUX       := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                                             UN_ACCION    => 'M', 
                                                             UN_CAMPOS    => MI_CAMPOS, 
                                                             UN_CONDICION => MI_CONDICION);

                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                               RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;  
                        END;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                          MI_MSGERROR(1).CLAVE := 'NUMERO';
                          MI_MSGERROR(1).VALOR := MI_RSN.CMPTE_AFECTADO;             
                          MI_MSGERROR(2).CLAVE := 'CODIGO';
                          MI_MSGERROR(2).VALOR := MI_RSN.ITEM_AFECT;                              
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    =>  SQLCODE,
                            UN_ERROR_COD  =>  PCK_ERRORES.ERR_CONTAB_ACTVAPROBCP,
                            UN_TABLAERROR =>  MI_TABLA,
                            UN_REEMPLAZOS =>  MI_MSGERROR
                          );
                      END;                         

                    END LOOP CUENTASPOPRPAGAR;
                  END IF;
                END LOOP COMPROBANTEAFECTADO;

              ELSIF(MI_RSC.AFECTACION = 'R') THEN
                --Actualiza el comprobante
                BEGIN
                  BEGIN
                      MI_TABLA     := 'BP_D_NOVEDADPROYECTO';
                      MI_CAMPOS    := 'VALORAPROBADO = VALORAPROBADO + '||UN_CONTRACREDITOA||' - '||UN_CONTRACREDITO|| ', '||
                                      'DATE_MODIFIED = SYSDATE, '||
                                      'MODIFIED_BY   = ''' || UN_USUARIO || ''' ';
                      MI_CONDICION := '   COMPANIA  = ''' || UN_COMPANIA || ''' 
                                      AND TIPOT     = ''' || UN_TIPO0 || ''' 
                                      AND NOVEDAD   =   ' || UN_NUMERO0 || ' 
                                      AND CODIGO    =   ' || UN_CONSECUTIVO;           
                      MI_AUX       := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                                         UN_ACCION    => 'M', 
                                                         UN_CAMPOS    => MI_CAMPOS, 
                                                         UN_CONDICION => MI_CONDICION);

                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                           RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;  
                  END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_MSGERROR(1).CLAVE := 'NUMERO';
                    MI_MSGERROR(1).VALOR := UN_NUMERO0;             
                    MI_MSGERROR(2).CLAVE := 'CODIGO';
                    MI_MSGERROR(2).VALOR := UN_CONSECUTIVO;                              
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    =>  SQLCODE,
                      UN_ERROR_COD  =>  PCK_ERRORES.ERR_CONTAB_ACTVAPROBNP,
                      UN_TABLAERROR =>  MI_TABLA,
                      UN_REEMPLAZOS =>  MI_MSGERROR
                    );
                END;                      

                --actualiza el comprobante afectado
                <<ACTUALIZARCPTEAFECTADO>>
                FOR MI_RSB IN (SELECT CMPTE_AFECTADO, 
                                  TIPO_CPTE_AFECT,
                                  ITEM_AFECT 
                               FROM BP_D_NOVEDADPROYECTO  
                              WHERE COMPANIA  = UN_COMPANIA
                                AND TIPOT     = UN_TIPO0
                                AND CLASET    = 'P'
                                AND NOVEDAD   = UN_NUMERO0
                                AND CODIGO    = UN_CONSECUTIVO) 
                LOOP
                  MI_CONDICION  := '    COMPANIA  = ''' || UN_COMPANIA || ''' 
                                    AND TIPOT     = ''' || MI_RSB.TIPO_CPTE_AFECT || ''' 
                                    AND NOVEDAD   =   ' || MI_RSB.CMPTE_AFECTADO || ' 
                                    AND CODIGO    =   ' || MI_RSB.ITEM_AFECT;
                  IF(MI_RSC.CODIGO = 'RES') THEN
                    MI_CAMPOS   := 'VALORAPROBADO = VALORAPROBADO + '||UN_CONTRACREDITOA||' - '||UN_CONTRACREDITO;
                  ELSE
                    MI_CAMPOS   := 'MODIFICACIONVALORAPROBADO = MODIFICACIONVALORAPROBADO + '||UN_CONTRACREDITOA ||' - '||UN_CONTRACREDITO;
                  END IF;

                  BEGIN
                    BEGIN
                    MI_TABLA     := 'BP_D_NOVEDADPROYECTO'; 
                    MI_CAMPOS    :=  MI_CAMPOS || ', '||
                                    'DATE_MODIFIED       = SYSDATE, '||
                                    'MODIFIED_BY         = ''' || UN_USUARIO || ''' ';
                    MI_AUX       := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                                       UN_ACCION    => 'M', 
                                                       UN_CAMPOS    => MI_CAMPOS, 
                                                       UN_CONDICION => MI_CONDICION);

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                         RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;  
                    END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                      MI_MSGERROR(1).CLAVE := 'NUMERO';
                      MI_MSGERROR(1).VALOR := MI_RSB.CMPTE_AFECTADO;             
                      MI_MSGERROR(2).CLAVE := 'CODIGO';
                      MI_MSGERROR(2).VALOR := MI_RSB.ITEM_AFECT;                              
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    =>  SQLCODE,
                        UN_ERROR_COD  =>  PCK_ERRORES.ERR_CONTAB_ACTCPTEAFECT,
                        UN_TABLAERROR =>  MI_TABLA,
                        UN_REEMPLAZOS =>  MI_MSGERROR
                      );
                  END;                   


                  <<ACTUALIZARAFECTADOOTRO>>
                  FOR MI_RSN IN (SELECT TIPO_CPTE_AFECT, 
                                    CMPTE_AFECTADO,
                                    ITEM_AFECT 
                                 FROM BP_D_NOVEDADPROYECTO  
                                WHERE COMPANIA  = UN_COMPANIA
                                  AND TIPOT     = MI_RSB.TIPO_CPTE_AFECT
                                  AND CLASET    = 'P'
                                  AND NOVEDAD   = MI_RSB.CMPTE_AFECTADO
                                  AND CODIGO    = MI_RSB.ITEM_AFECT) 
                  LOOP
                    BEGIN
                    BEGIN
                      MI_TABLA     := 'BP_D_NOVEDADPROYECTO';
                      MI_CAMPOS    := 'VALORAPROBADO = '||UN_CONTRACREDITOA||' - '||UN_CONTRACREDITO|| ', '||
                                      'DATE_MODIFIED = SYSDATE, '||
                                      'MODIFIED_BY   = ''' || UN_USUARIO || ''' ';
                      MI_CONDICION := '   COMPANIA  = ''' || UN_COMPANIA || ''' 
                                      AND TIPOT     = ''' || MI_RSN.TIPO_CPTE_AFECT || ''' 
                                      AND NOVEDAD   =   ' || MI_RSN.CMPTE_AFECTADO || ' 
                                      AND CODIGO    =   ' || MI_RSN.ITEM_AFECT;         
                      MI_AUX       := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                                         UN_ACCION    => 'M', 
                                                         UN_CAMPOS    => MI_CAMPOS, 
                                                         UN_CONDICION => MI_CONDICION);

                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                           RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;  
                    END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                      MI_MSGERROR(1).CLAVE := 'NUMERO';
                      MI_MSGERROR(1).VALOR := MI_RSN.CMPTE_AFECTADO;             
                      MI_MSGERROR(2).CLAVE := 'CODIGO';
                      MI_MSGERROR(2).VALOR := MI_RSN.ITEM_AFECT;                              
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    =>  SQLCODE,
                        UN_ERROR_COD  =>  PCK_ERRORES.ERR_CONTAB_ACTCPTEAFECT,
                        UN_TABLAERROR =>  MI_TABLA,
                        UN_REEMPLAZOS =>  MI_MSGERROR
                      );
                    END;                      

                  END LOOP ACTUALIZARAFECTADOOTRO;
                END LOOP ACTUALIZARCPTEAFECTADO;
              END IF;
            END IF;
          END IF;

          IF(MI_RSC.AFECTACION = 'N') THEN
            BEGIN
              BEGIN
                MI_TABLA     := 'COMPROBANTE_PPTAL';
                MI_CAMPOS    := 'DEBITO_AFECTADO  = DEBITO_AFECTADO  - ' || UN_CREDITOA || ' + '|| UN_CREDITO || ',
                                 CREDITO_AFECTADO = CREDITO_AFECTADO - ' || UN_CONTRACREDITOA ||' + '|| UN_CONTRACREDITO|| ', '||
                                'DATE_MODIFIED    = SYSDATE, '||
                                'MODIFIED_BY      = ''' || UN_USUARIO || ''' ';
                MI_CONDICION := '   COMPANIA  = ''' || UN_COMPANIA ||''' 
                                AND ANO       =   ' || MI_ANIOCOMPROBANTE ||' 
                                AND TIPO      = ''' || UN_TIPO ||''' 
                                AND NUMERO    =   ' || UN_NUMERO;     
                MI_AUX       := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                                   UN_ACCION    => 'M', 
                                                   UN_CAMPOS    => MI_CAMPOS, 
                                                   UN_CONDICION => MI_CONDICION);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;  
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                MI_MSGERROR(1).CLAVE := 'NUMERO';
                MI_MSGERROR(1).VALOR := UN_NUMERO;                             
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    =>  SQLCODE,
                  UN_ERROR_COD  =>  PCK_ERRORES.ERR_CONTAB_ACTDCAFECT,
                  UN_TABLAERROR =>  MI_TABLA,
                  UN_REEMPLAZOS =>  MI_MSGERROR
                );
            END;          

            IF(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                         UN_NOMBRE    => 'ACTUALIZAR CON CENTRO DE COSTO', 
                                         UN_MODULO    => PCK_DATOS.FC_MODULOCONTABILIDAD, 
                                         UN_FECHA_PAR => SYSDATE), 'NO') = 'SI') 
            THEN
              MI_CUENTA     := TRIM(SUBSTR(UN_CUENTA,1,16));
              MI_CONDICION  := '    COMPANIA    = ''' || UN_COMPANIA || ''' 
                                AND ANO         =   ' || MI_ANIOCOMPROBANTE || ' 
                                AND TIPO_CPTE   = ''' || UN_TIPO || ''' 
                                AND COMPROBANTE =   ' || UN_NUMERO || ' 
                                AND CUENTA      = ''' || MI_CUENTA || ''' 
                                AND CONSECUTIVO =   ' || UN_CONSECUTIVO;
            ELSE
              MI_CONDICION  := '    COMPANIA    = ''' || UN_COMPANIA || ''' 
                                AND ANO         =   ' || MI_ANIOCOMPROBANTE || ' 
                                AND TIPO_CPTE   = ''' || UN_TIPO || ''' 
                                AND COMPROBANTE =   ' || UN_NUMERO || ' 
                                AND CUENTA      = ''' || UN_CUENTA || ''' 
                                AND CONSECUTIVO =   ' || UN_CONSECUTIVO;
            END IF;

            BEGIN
              BEGIN
                MI_TABLA     := 'DETALLE_COMPROBANTE_PPTAL';   
                MI_AUX       := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                                   UN_ACCION    => 'M', 
                                                   UN_CAMPOS    => MI_CAMPOS, 
                                                   UN_CONDICION => MI_CONDICION);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;  
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                MI_MSGERROR(1).CLAVE := 'NUMERO';
                MI_MSGERROR(1).VALOR := UN_NUMERO;                             
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    =>  SQLCODE,
                  UN_ERROR_COD  =>  PCK_ERRORES.ERR_CONTAB_ACTDCDETAFECT,
                  UN_TABLAERROR =>  MI_TABLA,
                  UN_REEMPLAZOS =>  MI_MSGERROR
                );
            END;           

          ELSE

            BEGIN
              BEGIN
                MI_TABLA     := 'COMPROBANTE_PPTAL';
                MI_CAMPOS    := 'MODIFICACION_DEBITO  = MODIFICACION_DEBITO  - ' || UN_CREDITOA ||' + '|| UN_CREDITO ||' ,
                                 MODIFICACION_CREDITO = MODIFICACION_CREDITO - ' || UN_CONTRACREDITOA ||' + '|| UN_CONTRACREDITO|| ', '||
                                'DATE_MODIFIED        = SYSDATE, '||
                                'MODIFIED_BY          = ''' || UN_USUARIO || ''' ';
                MI_CONDICION := '    COMPANIA = '''|| UN_COMPANIA || ''' 
                                 AND ANO      =   '|| MI_ANIOCOMPROBANTE || ' 
                                 AND TIPO     = '''|| UN_TIPO || ''' 
                                 AND NUMERO   =   '|| UN_NUMERO;   
                MI_AUX       := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                                   UN_ACCION    => 'M', 
                                                   UN_CAMPOS    => MI_CAMPOS, 
                                                   UN_CONDICION => MI_CONDICION);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;  
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                MI_MSGERROR(1).CLAVE := 'NUMERO';
                MI_MSGERROR(1).VALOR := UN_NUMERO;                             
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    =>  SQLCODE,
                  UN_ERROR_COD  =>  PCK_ERRORES.ERR_CONTAB_ACTMODIFCPTE,
                  UN_TABLAERROR =>  MI_TABLA,
                  UN_REEMPLAZOS =>  MI_MSGERROR
                );
            END;         

            BEGIN
              BEGIN
                MI_TABLA     := 'DETALLE_COMPROBANTE_PPTAL';
                MI_CONDICION := '   COMPANIA    = '''|| UN_COMPANIA || ''' 
                                AND ANO         =   '|| MI_ANIOCOMPROBANTE || ' 
                                AND TIPO_CPTE   = '''|| UN_TIPO || ''' 
                                AND COMPROBANTE =   '|| UN_NUMERO || ' 
                                AND CUENTA      =   '|| UN_CUENTA || ' 
                                AND CONSECUTIVO =   '|| UN_CONSECUTIVO;  
                MI_AUX       := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                                   UN_ACCION    => 'M', 
                                                   UN_CAMPOS    => MI_CAMPOS, 
                                                   UN_CONDICION => MI_CONDICION);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;  
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                MI_MSGERROR(1).CLAVE := 'NUMERO';
                MI_MSGERROR(1).VALOR := UN_NUMERO;                             
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    =>  SQLCODE,
                  UN_ERROR_COD  =>  PCK_ERRORES.ERR_CONTAB_ACTMODIFDCPTE,
                  UN_TABLAERROR =>  MI_TABLA,
                  UN_REEMPLAZOS =>  MI_MSGERROR
                );
            END;            


            IF(MI_RSC.AFECTACION IN ('R','A')) THEN
              <<EVALUARCLASEAFECTAR>>
              FOR MI_RS1 IN (SELECT CLASEAFECTAR 
                               FROM TIPO_COMPROBPP 
                                LEFT JOIN CLASECNTPRES 
                                  ON TIPO_COMPROBPP.CLASE = CLASECNTPRES.CODIGO 
                              WHERE TIPO_COMPROBPP.COMPANIA = UN_COMPANIA
                                AND TIPO_COMPROBPP.CODIGO   = UN_TIPO) 
              LOOP 
                IF(MI_RS1.CLASEAFECTAR IS NOT NULL) THEN
                  <<ACTUALIZARPORCLASEAFECT>>
                  FOR MI_RSD IN (SELECT ANO, 
                                    TIPO_CPTE, 
                                    COMPROBANTE, 
                                    CUENTA, 
                                    CONSECUTIVO
                                 FROM DETALLE_COMPROBANTE_PPTAL
                                WHERE COMPANIA    = UN_COMPANIA
                                  AND TIPO_CPTE   = MI_RS.TIPO_CPTE_AFECT
                                  AND COMPROBANTE = MI_RS.CMPTE_AFECTADO
                                  AND CUENTA      = MI_RS.CUENTA
                                  AND CONSECUTIVO = MI_RS.CONSECUTIVOPPTO) 
                  LOOP

                    BEGIN
                      BEGIN
                        MI_TABLA     := 'COMPROBANTE_PPTAL';
                        MI_CAMPOS    := 'DEBITO_AFECTADO  = DEBITO_AFECTADO - '||UN_CREDITOA||' + '||UN_CREDITO||',
                                        CREDITO_AFECTADO  = CREDITO_AFECTADO - '||UN_CONTRACREDITOA||' + '||UN_CONTRACREDITO|| ', '||
                                        'DATE_MODIFIED    = SYSDATE, '||
                                        'MODIFIED_BY      = ''' || UN_USUARIO || ''' ';
                        MI_CONDICION := '   COMPANIA  = '''|| UN_COMPANIA || ''' 
                                        AND ANO       =   '|| MI_RSD.ANO || ' 
                                        AND TIPO      = '''|| NVL(MI_RSD.TIPO_CPTE, '') || ''' 
                                        AND NUMERO    =   '|| NVL(MI_RSD.COMPROBANTE, 0);
                        MI_AUX       := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                                           UN_ACCION    => 'M', 
                                                           UN_CAMPOS    => MI_CAMPOS, 
                                                           UN_CONDICION => MI_CONDICION);

                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                             RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;  
                      END;
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                        MI_MSGERROR(1).CLAVE := 'NUMERO';
                        MI_MSGERROR(1).VALOR := NVL(MI_RSD.COMPROBANTE, 0);                             
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD    =>  SQLCODE,
                          UN_ERROR_COD  =>  PCK_ERRORES.ERR_CONTAB_ACTDCAFECT,
                          UN_TABLAERROR =>  MI_TABLA,
                          UN_REEMPLAZOS =>  MI_MSGERROR
                        );
                    END;                    

                    BEGIN
                      BEGIN
                        MI_TABLA     := 'DETALLE_COMPROBANTE_PPTAL';
                        MI_CONDICION := '   COMPANIA    = '''|| UN_COMPANIA ||''' 
                                        AND ANO         =   '|| MI_RSD.ANO ||' 
                                        AND TIPO_CPTE   = '''|| NVL(MI_RSD.TIPO_CPTE, '') ||''' 
                                        AND COMPROBANTE =   '|| NVL(MI_RSD.COMPROBANTE, 0) ||' 
                                        AND CUENTA      = '''|| MI_RSD.CUENTA ||''' 
                                        AND CONSECUTIVO =   '|| NVL(MI_RSD.CONSECUTIVO, 0);
                        MI_AUX       := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                         UN_ACCION    => 'M', 
                                         UN_CAMPOS    => MI_CAMPOS, 
                                         UN_CONDICION => MI_CONDICION);

                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                             RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;  
                      END;
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                        MI_MSGERROR(1).CLAVE := 'NUMERO';
                        MI_MSGERROR(1).VALOR := NVL(MI_RSD.COMPROBANTE, 0);                             
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD    =>  SQLCODE,
                          UN_ERROR_COD  =>  PCK_ERRORES.ERR_CONTAB_ACTDCDETAFECT,
                          UN_TABLAERROR =>  MI_TABLA,
                          UN_REEMPLAZOS =>  MI_MSGERROR
                        );
                    END;                 

                  END LOOP ACTUALIZARPORCLASEAFECT;
                END IF;
              END LOOP EVALUARCLASEAFECTAR;
            END IF;
          END IF;
        END IF;
      END LOOP CONSULTARAFECTACION;
    END LOOP OBTENERDETALLES;

  END PR_AFECTAROTROCOMPROBANTE;

--5
FUNCTION FC_VERIFICARDESEMBOLSO
/*
  NAME              : FC_VERIFICARDESEMBOLSO nombre en access VerificarDesembolso
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO
  DATE MIGRADOR     : 28/03/2016
  TIME              : 05:20 PM
  SOURCE MODULE     : Contabilidad
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : establece un indicador para establecer si se puede realiza el desembolso para una cuenta determinada
  @NAME:  revisarDesembolso
  @METHOD:  GET
*/
(
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
  UN_CUENTA         IN PCK_SUBTIPOS.TI_CODIGOCONTA,
  UN_DESEMBOLSO     IN PCK_SUBTIPOS.TI_DOBLE,
  UN_VALOR          IN PCK_SUBTIPOS.TI_DOBLE
)RETURN NUMBER
AS
  MI_SALDODISPONIBLE          PCK_SUBTIPOS.TI_DOBLE;
  MI_ERROR_FUN                PCK_SUBTIPOS.TI_ENTERO:=GL_ERROR_NUM + 5;
BEGIN
  BEGIN
    SELECT NVL(SUM(SALDO),0)
    INTO MI_SALDODISPONIBLE
    FROM DESEMBOLSO 
    WHERE COMPANIA=UN_COMPANIA 
      AND ANO=UN_ANIO 
      AND CUENTA=UN_CUENTA 
      AND DESEMBOLSOAFEC = 0 
      AND SALDO>0 
      AND DESEMBOLSO >= UN_DESEMBOLSO;
  EXCEPTION WHEN NO_DATA_FOUND THEN
    MI_SALDODISPONIBLE:=0;
  END;

  IF(UN_VALOR > MI_SALDODISPONIBLE) THEN
    RETURN 0;
  ELSE
    RETURN -1;
  END IF;
  EXCEPTION WHEN OTHERS THEN
      PCK_DATOS.GL_ERROR_MSG := 'Interrupción durante la función FC_VERIFICARDESEMBOLSO';
      PCK_DATOS.GL_ERROR_MSG := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'CONTABILIDAD','',SQLERRM );
      RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );
END FC_VERIFICARDESEMBOLSO;

--6
FUNCTION FC_DESCARGARDESEMBOLSO
/*
  NAME              : FC_DESCARGARDESEMBOLSO
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO
  DATE MIGRADOR     : 28/03/2016
  TIME              : 05:35 PM
  SOURCE MODULE     : Contabilidad
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : realiza el descargo del desembolso
  @NAME:  descargarDesembolso
  @METHOD:  GET
*/
(
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
  UN_CUENTA         IN PCK_SUBTIPOS.TI_CODIGOCONTA,
  UN_DESEMBOLSO     IN PCK_SUBTIPOS.TI_DOBLE,
  UN_TIPO           IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
  UN_NUMERO         IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
  UN_FECHA          IN DATE,
  UN_VALOR          IN PCK_SUBTIPOS.TI_DOBLE,
  UN_TASADECAMBIO   IN PCK_SUBTIPOS.TI_DOBLE,
  UN_CONSECUTIVO    IN PCK_SUBTIPOS.TI_ENTERO,
  UN_TERCERO        IN PCK_SUBTIPOS.TI_TERCERO,
  UN_SUCURSAL       IN PCK_SUBTIPOS.TI_SUCURSAL,
  UN_CENTROCOSTO    IN PCK_SUBTIPOS.TI_CENTRO_COSTO,
  UN_AUXILIAR       IN PCK_SUBTIPOS.TI_AUXILIAR,
  UN_TIPOAFECT      IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
  UN_NUMEROAFECT    IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
  UN_DESCRIPCION    IN DETALLE_COMPROBANTE_CNT.DESCRIPCION%TYPE,
  UN_PORCRETENCION  IN PCK_SUBTIPOS.TI_PORCENTAJE
)RETURN NUMBER
AS
  MI_SALDOTEMP      PCK_SUBTIPOS.TI_DOBLE;
  MI_VALORREAL      PCK_SUBTIPOS.TI_DOBLE;
  MI_VALORDOL       PCK_SUBTIPOS.TI_DOBLE;
  MI_RETURN         PCK_SUBTIPOS.TI_LOGICO:=0;
  MI_CONSECUTIVO    PCK_SUBTIPOS.TI_ENTERO;
  MI_ERROR_FUN      PCK_SUBTIPOS.TI_ENTERO:=GL_ERROR_NUM + 6;
BEGIN
  MI_SALDOTEMP:=UN_VALOR;
  MI_CONSECUTIVO:=UN_CONSECUTIVO;
  FOR MI_RS IN (SELECT SALDO, DESEMBOLSO 
            FROM DESEMBOLSO 
            WHERE COMPANIA=UN_COMPANIA
              AND ANO=UN_ANIO
              AND CUENTA=UN_CUENTA 
              AND DESEMBOLSOAFEC = 0 
              AND SALDO>0 
              AND DESEMBOLSO >= UN_DESEMBOLSO 
            ORDER BY COMPANIA,ANO,DESEMBOLSO,CUENTA) LOOP
    IF(MI_RS.SALDO >= MI_SALDOTEMP) THEN
      MI_VALORREAL:=MI_SALDOTEMP;
      MI_RETURN:=-1;
    ELSE
      MI_SALDOTEMP:=MI_SALDOTEMP-MI_RS.SALDO;
      MI_VALORREAL:=MI_RS.SALDO;
    END IF;
    MI_VALORDOL:=PCK_SYSMAN_UTL.FC_ROUND(MI_VALORREAL/UN_TASADECAMBIO,8);
    PCK_CONTABILIDAD1.PR_AFECTARDESEMBOLSO(UN_COMPANIA, UN_ANIO, UN_CUENTA, MI_RS.DESEMBOLSO, MI_VALORREAL, 0);
    PCK_CONTABILIDAD1.PR_INSERTAR_DETALLECOMPROBANTE(UN_COMPANIA, UN_ANIO, UN_TIPO, UN_NUMERO, UN_CUENTA, UN_CONSECUTIVO, TO_CHAR(UN_FECHA, 'DD/MM/YYYY'),UN_DESCRIPCION, UN_TERCERO, UN_SUCURSAL,
                      UN_CENTROCOSTO, UN_AUXILIAR, 0, MI_VALORREAL, UN_TIPOAFECT, UN_NUMEROAFECT, 0, MI_VALORDOL, UN_PORCRETENCION);
    MI_CONSECUTIVO:=MI_CONSECUTIVO+1;
    IF (MI_RETURN <> 0) THEN
      RETURN MI_RETURN;
    END IF;
  END LOOP;
  RETURN MI_RETURN;
  EXCEPTION WHEN OTHERS THEN
      PCK_DATOS.GL_ERROR_MSG := 'Interrupción durante el descargo del desembolso';
      PCK_DATOS.GL_ERROR_MSG := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'CONTABILIDAD','',SQLERRM );
      RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );
END FC_DESCARGARDESEMBOLSO;

--7
PROCEDURE PR_AFECTARDESEMBOLSO
/*
  NAME              : PR_AFECTARDESEMBOLSO nombre en access AfectarDesembolso
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO
  DATE MIGRADOR     : 29/03/2016
  TIME              : 08:10 AM
  SOURCE MODULE     : Contabilidad
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : afecta el desembolso
  @NAME:  afectarDesembolso
  @METHOD:  PUT
*/
(
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
  UN_CUENTA         IN PCK_SUBTIPOS.TI_CODIGOCONTA,
  UN_DESEMBOLSO     IN PCK_SUBTIPOS.TI_DOBLE,
  UN_VDESEM         IN PCK_SUBTIPOS.TI_DOBLE,
  UN_VAVDESEM       IN PCK_SUBTIPOS.TI_DOBLE
)
AS

  MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
  MI_AUX            PCK_SUBTIPOS.TI_ENTERO;
  MI_ERROR_FUN      PCK_SUBTIPOS.TI_ENTERO:=GL_ERROR_NUM + 7;
BEGIN
  MI_CAMPOS:='SALDO=SALDO+'||UN_VAVDESEM||'-'||UN_VDESEM||',
              SALDODOL=PCK_SYSMAN_UTL.FC_ROUND((SALDO+'||UN_VAVDESEM||'-'||UN_VDESEM||')/TASAC,8)';
  MI_CONDICION:='COMPANIA='''||UN_COMPANIA||''' AND ANO='||UN_ANIO||' AND DESEMBOLSO='||UN_DESEMBOLSO||' AND CUENTA='''||UN_CUENTA||'''';
  MI_AUX:=PCK_DATOS.FC_ACME('DESEMBOLSO', 'M', MI_CAMPOS, NULL, NULL, MI_CONDICION);
  EXCEPTION WHEN OTHERS THEN
      PCK_DATOS.GL_ERROR_MSG := 'Interrupción durante la afectación del desembolso';
      PCK_DATOS.GL_ERROR_MSG := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'CONTABILIDAD','',SQLERRM );
      RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );
END PR_AFECTARDESEMBOLSO;

--8
PROCEDURE PR_INSERTAR_DETALLECOMPROBANTE
/*
  NAME              : PR_INSERTAR_DETALLECOMPROBANTE
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO
  DATE MIGRADOR     : 29/03/2016
  TIME              : 08:45 AM
  SOURCE MODULE     : Contabilidad
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : inserta un detalle de comprobante presupuestal
  @NAME:  insertarDetallecomprobanteContable
  @METHOD:  POST
*/
(
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
  UN_TIPO           IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
  UN_NUMERO         IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
  UN_CUENTA         IN PCK_SUBTIPOS.TI_CODIGOCONTA,
  UN_CONSECUTIVO    IN PCK_SUBTIPOS.TI_ENTERO,
  UN_FECHA          IN VARCHAR2,
  UN_DESCRIPCION    IN DETALLE_COMPROBANTE_CNT.DESCRIPCION%TYPE,
  UN_TERCERO        IN PCK_SUBTIPOS.TI_TERCERO,
  UN_SUCURSAL       IN PCK_SUBTIPOS.TI_SUCURSAL,
  UN_CENTROCOSTO    IN PCK_SUBTIPOS.TI_CENTRO_COSTO,
  UN_AUXILIAR       IN PCK_SUBTIPOS.TI_AUXILIAR,
  UN_VALORDEBITO    IN PCK_SUBTIPOS.TI_DOBLE,
  UN_VALORCREDITO   IN PCK_SUBTIPOS.TI_DOBLE,
  UN_TIPOAFECT      IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
  UN_NUMEROAFECT    IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
  UN_DEBITOEQUIV    IN PCK_SUBTIPOS.TI_DOBLE,
  UN_CREDITOEQUIV   IN PCK_SUBTIPOS.TI_DOBLE,
  UN_PORCENTAJERET  IN PCK_SUBTIPOS.TI_PORCENTAJE
)
AS
  MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
  MI_AUX            PCK_SUBTIPOS.TI_RTA_ACME;
  MI_ERROR_FUN      PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 8;
BEGIN
  --Invoca la función Actconta
  MI_CAMPOS:='COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CUENTA,CONSECUTIVO,NATURALEZA,FECHA,HORA,DESCRIPCION,TERCERO,SUCURSAL,CENTRO_COSTO,AUXILIAR,
              VALOR_DEBITO,VALOR_CREDITO,TIPO_CPTE_AFECT,CMPTE_AFECTADO,DEBITO_EQUIV,CREDITO_EQUIV,PORCENTAJERETENCION';
  MI_VALORES:=''''||UN_COMPANIA||''', '||UN_ANIO||','''||UN_TIPO||''', '||UN_NUMERO||', '''||UN_CUENTA||''', '||UN_CONSECUTIVO||', ''D'',TO_DATE('''||UN_FECHA||''', ''DD/MM/YYYY''), TO_DATE(TO_CHAR(SYSDATE, ''HH24:MI:ss''), ''HH24:MI:ss''), '''||
  UN_DESCRIPCION||''', '''||UN_TERCERO||''', '''||UN_SUCURSAL||''','''||UN_CENTROCOSTO||''', '''||UN_AUXILIAR||''', '||UN_VALORDEBITO||', '||UN_VALORCREDITO||', '''||UN_TIPOAFECT||''', '||UN_NUMEROAFECT||', '||UN_DEBITOEQUIV||','||
  UN_CREDITOEQUIV||','||UN_PORCENTAJERET;
  MI_AUX:=PCK_DATOS.FC_ACME('DETALLE_COMPROBANTE_CNT', 'I', MI_CAMPOS, MI_VALORES);

   EXCEPTION WHEN OTHERS THEN
      PCK_DATOS.GL_ERROR_MSG := 'Interrupción durante la insercción de un detalle de comprobante prespuestal';
      PCK_DATOS.GL_ERROR_MSG := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'CONTABILIDAD','',SQLERRM );
      RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );
END PR_INSERTAR_DETALLECOMPROBANTE;

--9
FUNCTION FC_PORCENTAJEBANCOSCUENTA
--10.20 29/03/2016
/*
  NAME              : FC_VERIFICARDESEMBOLSO nombre en access VerificarDesembolso
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO
  DATE MIGRADOR     : 28/03/2016
  TIME              : 05:20 PM
  SOURCE MODULE     : Contabilidad
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : retorna la cuenta de los comprobantes_cntbancos que tiene un porcentaje de 100%
  @NAME:  porcentajeCuentaBancos
  @METHOD:  GET
*/
(
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
  UN_TIPO           IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
  UN_NUMERO         IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT
) RETURN VARCHAR2
AS
  MI_CUENTA        PCK_SUBTIPOS.TI_CODIGOCONTA:=NULL;  
  MI_ERROR_FUN     PCK_SUBTIPOS.TI_ENTERO:=GL_ERROR_NUM + 9;
BEGIN
  FOR MI_RS IN (SELECT PORCENTAJERETENCION, CUENTA 
                FROM COMPROBANTE_CNTBANCOS
                WHERE COMPANIA= UN_COMPANIA
                  AND ANO = UN_ANIO
                  AND TIPO = UN_TIPO
                  AND NUMERO = UN_NUMERO) LOOP
    IF(MI_RS.PORCENTAJERETENCION = 100) THEN
      MI_CUENTA:=MI_RS.CUENTA;
    END IF;
  END LOOP;

  RETURN MI_CUENTA;
  EXCEPTION WHEN OTHERS THEN
      PCK_DATOS.GL_ERROR_MSG := 'Interrupción la consulta del porcentaje de una cuenta en comprobantes bancos';
      PCK_DATOS.GL_ERROR_MSG := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'CONTABILIDAD','',SQLERRM );
      RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );
END FC_PORCENTAJEBANCOSCUENTA;


FUNCTION FC_CONSOLIDARCOMPANIAH
/*
    NAME              : FC_CONSOLIDARCOMPANIAH  En Access--> ConsolidarCompaniaH
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : NICOLAS GOMEZ BARBOSA
    DATE MIGRADOR     : 30/03/2016
    TIME              : 02:00 PM
    SOURCE MODULE     : CONTABILIDAD
    MODIFIER          : JULIO CESAR REINA PANCHE
    DATE MODIFIED     : 25/04/2018
    TIME              : 03:00 PM
    MODIFICATIONS     : CORRECCIÓN SEGÚN ESTÁNDAR DE PROGRAMACIÓN Y OPTIMIZACIÓN DEL MANEJO DE ERRORES.
    DATE MODIFIED     : 09/10/2018
    TIME              : 03:00 PM
    MODIFICATIONS     : Se optimiza la función para que realice losmenos posibles delete, por otro lado se ajusta para que actualice
                        unicamente lo necesario y no ponga en cero todo para luego actualizar
                        por otro lado se incorpora el mayorizar para que quede listo

    DESCRIPTION       : Consolidar compañias
    @NAME:  consolidarCompanias
    @METHOD:  GET
  */
  (
  UN_COMPANIACON                  IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO                         IN PCK_SUBTIPOS.TI_ANIO
  )RETURN CLOB
  AS
    MI_MENSAJE                    CLOB;
    MI_I                          PCK_SUBTIPOS.TI_ENTERO:=0;
    MI_I2                         PCK_SUBTIPOS.TI_ENTERO:=0;
    MI_CONS                       PCK_SUBTIPOS.TI_ENTERO_LARGO:=1;
    MI_ULTIMONIVEL                PCK_SUBTIPOS.TI_PARAMETRO;
    MI_COMPANIAS                  VARCHAR2(320 CHAR):='';
    MI_TERCEROS                   CLOB;
    MI_CAMPOS                     PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                    PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION                  PCK_SUBTIPOS.TI_CONDICION;
    MI_MERGEUSING                 PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE                PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE                PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_MERGENOEXIS                PCK_SUBTIPOS.TI_MERGENOEXISTE;
    MI_RS                         SYS_REFCURSOR;
    MI_RS2                        SYS_REFCURSOR;
    MI_ERROR                      PCK_SUBTIPOS.TI_CLAVEVALOR;  
    MI_NOMBRE                     COMPANIA.NOMBRE%TYPE;
    MI_TABLA                      VARCHAR2(100); 
    MI_TIPOCOMPROBANTE            VARCHAR2(100); 
    MI_DCTO_IDENTIDAD             VARCHAR2(100); 
    MI_CONSOLIDAR                 VARCHAR2(2 CHAR);
    MI_MSGERROR                   PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN

    MI_ULTIMONIVEL:=NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   =>  UN_COMPANIACON,
                                              UN_NOMBRE     =>  'CONSOLIDAR COMPANIAS A ULTIMO NIVEL', 
                                              UN_MODULO     =>  PCK_DATOS.FC_MODULOCONTABILIDAD,
                                              UN_FECHA_PAR  =>  SYSDATE),'NO');
    --VALIDAR QUE LOS TERCEROS ESTEN ADECUADAMENTE CREADOS
    MI_I:=0;
    FOR MI_RS IN( SELECT DISTINCT 
                    TERCERO.COMPANIA,
                    TERCERO.NIT,
                    TERCERO.NOMBRE
                  FROM TERCERO
                  WHERE COMPANIA IN (SELECT NITCOMPANIA
                                      FROM   CONSOLIDADA 
                                      WHERE  COMPANIACON=UN_COMPANIACON) 
                    AND TERCERO.PAIS         IS NULL 
                    AND TERCERO.DEPARTAMENTO IS NULL 
                    AND TERCERO.CIUDAD       IS NULL)
    LOOP
        MI_I:=MI_I+1;
        MI_TERCEROS:= MI_TERCEROS                 ||
                      RPAD(MI_RS.COMPANIA,12,' ') ||
                      RPAD(MI_RS.NIT,20,' ')      ||
                      MI_RS.NOMBRE||CHR(10);

    END LOOP;
    IF MI_I<>0 THEN 

    /*    
      -- Se comenta esta linea de codigo debido que en el catch no alcanza su capacidad
      -- por el tamaño de data, para este caso se manejo que descargue en un archivo txt la descripcion de error 
      -- CFBARRERA 14-08-2024 7750048

        BEGIN
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;
        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
            MI_ERROR(1).CLAVE := 'CODIGOS';
          MI_ERROR(1).VALOR := TO_CHAR(SUBSTR(MI_TERCEROS,1,3500));
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE,
                                       UN_REEMPLAZOS => MI_ERROR,
                                       UN_ERROR_COD=>PCK_ERRORES.ERR_CONTAB_CONFIGREGIONES
                                       );
        END;  
        
        */
        
    MI_TERCEROS := 'No se puede realizar la inserción; los siguientes funcionarios no cuentan con  PAÍS, DEPARTAMENTO o CIUDAD. Por favor, verifique.' || CHR(10) || CHR(10) || MI_TERCEROS;
    RETURN MI_TERCEROS;
    END IF;

    FOR MI_RS IN( SELECT NITCOMPANIA
                  FROM   CONSOLIDADA 
                  WHERE  COMPANIACON=UN_COMPANIACON
                  ORDER BY COMPANIACON,NITCOMPANIA)
    LOOP
        IF MI_COMPANIAS IS NOT NULL THEN
            MI_COMPANIAS :=  MI_COMPANIAS||',';  
        END IF;
        MI_COMPANIAS := MI_COMPANIAS || '''' || MI_RS.NITCOMPANIA || '''';
    END LOOP;

    IF MI_COMPANIAS='' OR MI_COMPANIAS IS NULL THEN
        BEGIN
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;
        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN    
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE,
                                       UN_ERROR_COD=>PCK_ERRORES.ERR_CONTAB_COMPRELACIONADAS
                                       );
        END;
    END IF;

    SELECT COUNT(*) INTO MI_I  
    FROM ANO 
    WHERE COMPANIA=UN_COMPANIACON 
      AND NUMERO=UN_ANIO;

    IF MI_I=0 THEN
        BEGIN 
            BEGIN
                MI_CAMPOS:= 'COMPANIA,
                           NUMERO';
                MI_VALORES:=''''||UN_COMPANIACON||''',
                          '||UN_ANIO;        
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    => 'ANO', 
                                                      UN_ACCION   => 'I', 
                                                      UN_CAMPOS   => MI_CAMPOS, 
                                                      UN_VALORES  => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN    
            MI_ERROR (1).CLAVE := 'ANIO';
            MI_ERROR (1).VALOR := UN_ANIO;
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                       UN_REEMPLAZOS => MI_ERROR,
                                       UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_INSANOCONSOLIDAR
                                       );
        END;
    END IF;

    BEGIN 
        BEGIN
            MI_CAMPOS   := 'DIRECCION=REPLACE(DIRECCION,CHR(39),'''')';
            MI_CONDICION:='INSTR(DIRECCION,CHR(39))<>0';

            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA      => 'TERCERO', 
                                                  UN_ACCION     => 'M',
                                                  UN_CAMPOS     => MI_CAMPOS,
                                                  UN_CONDICION  => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN    
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                   UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_ACTDIRTERCERO
                                   );
    END;

    BEGIN 
        BEGIN
            MI_CAMPOS:= 'DIRECCION=REPLACE(DIRECCION,CHR(34),'''')';
            MI_CONDICION:='INSTR(DIRECCION,CHR(34))<>0';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA      => 'TERCERO',
                                                    UN_ACCION     => 'M',
                                                    UN_CAMPOS     => MI_CAMPOS, 
                                                UN_CONDICION  => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN    
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                   UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_ACTDIRTERCERO
                                   );
    END;
    BEGIN 
        BEGIN 
            MI_CAMPOS:= 'DIRECCION=REPLACE(DIRECCION,CHR(124),'''')';
            MI_CONDICION:='INSTR(DIRECCION,CHR(124))<>0';
            MI_I := PCK_DATOS.FC_ACME ( UN_TABLA      => 'TERCERO',
                                        UN_ACCION     => 'M',
                                        UN_CAMPOS     => MI_CAMPOS,
                                        UN_CONDICION  => MI_CONDICION); 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN    
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                   UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_ACTDIRTERCERO
                                    );
    END;

    MI_DCTO_IDENTIDAD:=' ';
    <<TIPOSDOCUMENTO>>
    FOR MI_RS IN(SELECT TIPOS_DOCUMENTOS.COMPANIA,
                       TIPOS_DOCUMENTOS.DCTO_IDENTIDAD, 
                       TIPOS_DOCUMENTOS.DESCRIPCION,
                       TIPOS_DOCUMENTOS.SIGLA,
                       TIPOS_DOCUMENTOS.SIGLA2,
                       TIPOS_DOCUMENTOS.DOC_SIIF,
                       TIPOS_DOCUMENTOS.CODIGODIAN
                FROM TIPOS_DOCUMENTOS LEFT JOIN (SELECT COMPANIA, DCTO_IDENTIDAD
                                                 FROM TIPOS_DOCUMENTOS
                                                 WHERE COMPANIA=UN_COMPANIACON) TIPOS_DOCANT 
                 ON UN_COMPANIACON                  = TIPOS_DOCANT.COMPANIA
                AND TIPOS_DOCUMENTOS.DCTO_IDENTIDAD = TIPOS_DOCANT.DCTO_IDENTIDAD 
                WHERE TIPOS_DOCUMENTOS.COMPANIA IN (SELECT NITCOMPANIA
                                 FROM   CONSOLIDADA 
                                 WHERE  COMPANIACON= UN_COMPANIACON) 
                AND TIPOS_DOCANT.COMPANIA IS NULL                 
                ORDER BY TIPOS_DOCUMENTOS.DCTO_IDENTIDAD, 
                         TIPOS_DOCUMENTOS.COMPANIA
    )LOOP
        IF MI_DCTO_IDENTIDAD <> MI_RS.DCTO_IDENTIDAD THEN
            MI_CAMPOS  := 'COMPANIA,DCTO_IDENTIDAD,DESCRIPCION,SIGLA,SIGLA2,DOC_SIIF,CODIGODIAN';
            MI_VALORES := '''' || UN_COMPANIACON || ''',''' || MI_RS.DCTO_IDENTIDAD 
                   || ''','''  || MI_RS.DESCRIPCION || ''',''' || MI_RS.SIGLA || ''',''' || MI_RS.SIGLA2 
                   || ''','''  || MI_RS.DOC_SIIF || ''',' || MI_RS.CODIGODIAN;
            BEGIN
                BEGIN            
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    =>  'TIPOS_DOCUMENTOS',
                                                          UN_ACCION   =>  'I', 
                                                          UN_CAMPOS   =>  MI_CAMPOS,
                                                          UN_VALORES  =>  MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                MI_ERROR(1).CLAVE := 'TIPO';
                MI_ERROR(1).VALOR := MI_RS.DCTO_IDENTIDAD;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE,
                                           UN_REEMPLAZOS => MI_ERROR,
                                           UN_ERROR_COD=>PCK_ERRORES.ERR_CONTAB_TIPODOCUMENTO
                                          );
            END;
        END IF;
        MI_DCTO_IDENTIDAD := MI_RS.DCTO_IDENTIDAD;
    END LOOP;

    <<TERCERO>>
    FOR MI_RS IN (SELECT TERCERO.COMPANIA,
                         TERCERO.NIT, 
                         TERCERO.SUCURSAL, 
                         NIT_CEDULA, 
                         PAIS, 
                         DEPARTAMENTO, 
                         CIUDAD, 
                         DIRECCION,
                         REGIMEN, 
                         CLASE, 
                         TIPOID, 
                         NOMBRE1, 
                         NOMBRE2, 
                         APELLIDO1, 
                         APELLIDO2, 
                         CLASEENTIDADOFICIAL,
                         TELEFONOS
                  FROM TERCERO LEFT JOIN (SELECT COMPANIA, SUCURSAL, NIT
                                           FROM TERCERO
                                           WHERE COMPANIA=UN_COMPANIACON
                                         ) TERCEROANT 
                    ON UN_COMPANIACON   = TERCEROANT.COMPANIA
                   AND TERCERO.SUCURSAL = TERCEROANT.SUCURSAL 
                   AND TERCERO.NIT      = TERCEROANT.NIT
                  WHERE TERCERO.COMPANIA IN (SELECT NITCOMPANIA
                                     FROM   CONSOLIDADA 
                                     WHERE  COMPANIACON=UN_COMPANIACON) 
                    AND TERCEROANT.COMPANIA IS NULL                 
                  ORDER BY TERCERO.COMPANIA,TERCERO.NIT,TERCERO.SUCURSAL)
    LOOP
        SELECT COUNT(*) 
        INTO   MI_I
        FROM  TERCERO 
        WHERE COMPANIA  = UN_COMPANIACON
          AND NIT       = MI_RS.NIT
          AND SUCURSAL  = MI_RS.SUCURSAL;
        IF MI_I=0 THEN
            MI_CAMPOS:= 'COMPANIA,
                         NIT,
                         SUCURSAL,
                         NIT_CEDULA,
                         PAIS,
                         DEPARTAMENTO,
                         CIUDAD,
                         DIRECCION,
                         REGIMEN,
                         CLASE,
                         TIPOID,
                         NOMBRE1,
                         NOMBRE2,
                         APELLIDO1,
                         APELLIDO2,
                         CLASEENTIDADOFICIAL,
                         TELEFONOS';
            MI_VALORES:=''''  ||  UN_COMPANIACON              ||''',
                        q''['   ||  MI_RS.NIT                 || ']'',
                        q''['   ||  MI_RS.SUCURSAL            || ']'',
                        q''['   ||  MI_RS.NIT_CEDULA          || ']'',
                        q''['   ||  MI_RS.PAIS                || ']'',
                        q''['   ||  MI_RS.DEPARTAMENTO        || ']'',
                        q''['   ||  MI_RS.CIUDAD              || ']'',
                        q''['   ||  MI_RS.DIRECCION           || ']'',
                        q''['   ||  MI_RS.REGIMEN             || ']'',
                        q''['   ||  MI_RS.CLASE               || ']'',
                        q''['   ||  MI_RS.TIPOID              || ']'',
                        q''['   ||  MI_RS.NOMBRE1             || ']'',
                        q''['   ||  MI_RS.NOMBRE2             || ']'',
                        q''['   ||  MI_RS.APELLIDO1           || ']'',
                        q''['   ||  MI_RS.APELLIDO2           || ']'',
                        '     ||  MI_RS.CLASEENTIDADOFICIAL ||',
                        q''['   ||  MI_RS.TELEFONOS           || ']''';
            BEGIN
                BEGIN            
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    =>  'TERCERO',
                                                          UN_ACCION   =>  'I', 
                                                          UN_CAMPOS   =>  MI_CAMPOS,
                                                          UN_VALORES  =>  MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                MI_ERROR(1).CLAVE := 'TERCERO';
                MI_ERROR(1).VALOR :=  MI_RS.NIT ||' '||MI_RS.APELLIDO1 ||' '|| MI_RS.NOMBRE1;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                           UN_REEMPLAZOS => MI_ERROR,
                                           UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_INSTERCERO
                                       );
            END;
        END IF;
    END LOOP TERCERO;

    -- CC_2267 GROJAS: Actualiza las fuentes de recursos para la compañia consolidada
    
    MI_TABLA:='FUENTE_RECURSOS';
        MI_MERGEUSING := 'SELECT ''' || UN_COMPANIACON || ''' COMPANIA,
                                ANO,
                                CODIGO,
                                MIN(NOMBRE) NOMBRE
                            FROM FUENTE_RECURSOS                           
                            WHERE FUENTE_RECURSOS.COMPANIA IN (SELECT NITCOMPANIA                                                                 
                                                                 FROM CONSOLIDADA                                                                 
                                                                 WHERE COMPANIACON = '''|| UN_COMPANIACON ||''')
                            AND FUENTE_RECURSOS.ANO         = ' || UN_ANIO || '
                            GROUP BY '''|| UN_COMPANIACON ||''',
                                        ANO,
                                        CODIGO';
        
        MI_MERGEENLACE := 'VISTA.COMPANIA        = TABLA.COMPANIA 
                           AND VISTA.ANO         = TABLA.ANO 
                           AND VISTA.CODIGO      = TABLA.CODIGO';
                        
        MI_MERGEEXISTE := 'UPDATE SET TABLA.NOMBRE                       = VISTA.NOMBRE';
                                    
        MI_MERGENOEXIS := 'INSERT ( COMPANIA,
                                    ANO,
                                    CODIGO,
                                    NOMBRE
                        ) VALUES (VISTA.COMPANIA 
                                , VISTA.ANO 
                                , VISTA.CODIGO 
                                , VISTA.NOMBRE
                                )';
                                
        BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA 
                                                , UN_ACCION      => 'IM'
                                                , UN_MERGEUSING  => MI_MERGEUSING
                                                , UN_MERGEENLACE => MI_MERGEENLACE
                                                , UN_MERGEEXISTE => MI_MERGEEXISTE
                                                , UN_MERGENOEXIS => MI_MERGENOEXIS);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END;
    
    -- CC_2267 GROJAS: Actualiza los centros de costo para la compañia consolidada
    
    MI_TABLA:='CENTRO_COSTO';
        MI_MERGEUSING := 'SELECT ''' || UN_COMPANIACON || ''' COMPANIA,
                                ANO,
                                CODIGO,
                                MIN(NOMBRE) NOMBRE,
                                MIN(TIPO) TIPO
                            FROM CENTRO_COSTO                           
                            WHERE CENTRO_COSTO.COMPANIA IN (SELECT NITCOMPANIA                                                                 
                                                                 FROM CONSOLIDADA                                                                 
                                                                 WHERE COMPANIACON = '''|| UN_COMPANIACON ||''')
                            AND CENTRO_COSTO.ANO         = ' || UN_ANIO || '
                            GROUP BY '''|| UN_COMPANIACON ||''',
                                        ANO,
                                        CODIGO';
        
        MI_MERGEENLACE := 'VISTA.COMPANIA        = TABLA.COMPANIA 
                           AND VISTA.ANO         = TABLA.ANO 
                           AND VISTA.CODIGO      = TABLA.CODIGO';
                        
        MI_MERGEEXISTE := 'UPDATE SET TABLA.NOMBRE             = VISTA.NOMBRE 
                                    , TABLA.TIPO               = VISTA.TIPO';
                                    
        MI_MERGENOEXIS := 'INSERT ( COMPANIA,
                                    ANO,
                                    CODIGO,
                                    NOMBRE,
                                    TIPO
                        ) VALUES (VISTA.COMPANIA 
                                , VISTA.ANO 
                                , VISTA.CODIGO 
                                , VISTA.NOMBRE
                                , VISTA.TIPO 
                                )';
                                
        BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA 
                                                , UN_ACCION      => 'IM'
                                                , UN_MERGEUSING  => MI_MERGEUSING
                                                , UN_MERGEENLACE => MI_MERGEENLACE
                                                , UN_MERGEEXISTE => MI_MERGEEXISTE
                                                , UN_MERGENOEXIS => MI_MERGENOEXIS);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END;

   -- CC_2267 GROJAS: Actualiza las referencias para la compañia consolidada
    
    MI_TABLA:='REFERENCIA';
        MI_MERGEUSING := 'SELECT ''' || UN_COMPANIACON || ''' COMPANIA,
                                ANO,  
                                CODIGO, 
                                MIN(NOMBRE) NOMBRE, 
                                MIN(MOVIMIENTO) MOVIMIENTO
                            FROM REFERENCIA                           
                            WHERE REFERENCIA.COMPANIA IN (SELECT NITCOMPANIA                                                                 
                                                                 FROM CONSOLIDADA                                                                 
                                                                 WHERE COMPANIACON = '''|| UN_COMPANIACON ||''')
                            AND REFERENCIA.ANO         = ' || UN_ANIO || '
                            GROUP BY '''|| UN_COMPANIACON ||''',
                                        ANO,  
                                        CODIGO';
        
        MI_MERGEENLACE := 'VISTA.COMPANIA        = TABLA.COMPANIA 
                           AND VISTA.ANO         = TABLA.ANO 
                           AND VISTA.CODIGO      = TABLA.CODIGO';
                        
        MI_MERGEEXISTE := 'UPDATE SET TABLA.NOMBRE      = VISTA.NOMBRE';
                                    
        MI_MERGENOEXIS := 'INSERT ( COMPANIA,
                                    ANO,  
                                    CODIGO, 
                                    NOMBRE, 
                                    MOVIMIENTO
                        ) VALUES (VISTA.COMPANIA 
                                , VISTA.ANO 
                                , VISTA.CODIGO 
                                , VISTA.NOMBRE
                                , VISTA.MOVIMIENTO
                                )';
                                
        BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA 
                                                , UN_ACCION      => 'IM'
                                                , UN_MERGEUSING  => MI_MERGEUSING
                                                , UN_MERGEENLACE => MI_MERGEENLACE
                                                , UN_MERGEEXISTE => MI_MERGEEXISTE
                                                , UN_MERGENOEXIS => MI_MERGENOEXIS);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END;

    -- CC_2267 GROJAS: Actualiza los auxiliares para la compañia consolidada
    
    MI_TABLA:='AUXILIAR';
        MI_MERGEUSING := 'SELECT ''' || UN_COMPANIACON || ''' COMPANIA,
                                ANO,  
                                CODIGO, 
                                MIN(NOMBRE) NOMBRE, 
                                MIN(MOVIMIENTO) MOVIMIENTO
                            FROM AUXILIAR                           
                            WHERE AUXILIAR.COMPANIA IN (SELECT NITCOMPANIA                                                                 
                                                                 FROM CONSOLIDADA                                                                 
                                                                 WHERE COMPANIACON = '''|| UN_COMPANIACON ||''')
                            AND AUXILIAR.ANO         = ' || UN_ANIO || '
                            GROUP BY '''|| UN_COMPANIACON ||''',
                                        ANO,  
                                        CODIGO';
        
        MI_MERGEENLACE := 'VISTA.COMPANIA        = TABLA.COMPANIA 
                           AND VISTA.ANO         = TABLA.ANO 
                           AND VISTA.CODIGO      = TABLA.CODIGO';
                        
        MI_MERGEEXISTE := 'UPDATE SET TABLA.NOMBRE      = VISTA.NOMBRE 
                                    , TABLA.MOVIMIENTO  = VISTA.MOVIMIENTO';
                                    
        MI_MERGENOEXIS := 'INSERT ( COMPANIA,
                                    ANO,  
                                    CODIGO, 
                                    NOMBRE, 
                                    MOVIMIENTO
                        ) VALUES (VISTA.COMPANIA 
                                , VISTA.ANO 
                                , VISTA.CODIGO 
                                , VISTA.NOMBRE
                                , VISTA.MOVIMIENTO
                                )';
                                
        BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA 
                                                , UN_ACCION      => 'IM'
                                                , UN_MERGEUSING  => MI_MERGEUSING
                                                , UN_MERGEENLACE => MI_MERGEENLACE
                                                , UN_MERGEEXISTE => MI_MERGEEXISTE
                                                , UN_MERGENOEXIS => MI_MERGENOEXIS);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END;

    /* OPERACIONES EN TABLAS MAYORES
    */
    MI_CONDICION:='COMPANIA =''' || UN_COMPANIACON || '''
                   AND ANO      ='   || UN_ANIO;  

    BEGIN
        BEGIN        
            PCK_CONTABILIDAD.GL_PASASALDO := -1;
            MI_TABLA := 'SALDOSINICIALES';                            
            MI_I := PCK_DATOS.FC_ACME ( UN_TABLA      => MI_TABLA,
                                        UN_ACCION     => 'E',
                                        UN_CONDICION  => MI_CONDICION);
            MI_TABLA := 'SALDO_AUX_CONTABLE';                            
            MI_I := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA,
                                       UN_ACCION     => 'E',
                                       UN_CONDICION  => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;   
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN     
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_TABLAERROR => MI_TABLA);
    END;

    MI_CAMPOS:= 'COMPANIA,
                 ANO,
                 CODIGO,
                 NATURALEZA,
                 MOVIMIENTO,
                 MAN_AUX_TER,
                 MAN_CEN_CTO,
                 MAN_AUX_GEN,
                 MAN_AUX_REF,
                 MAN_AUX_FUE,
                 NOMBRE,
                 CLASECUENTA ';

    MI_VALORES:='SELECT  '''||UN_COMPANIACON||''',
                  P.ANO,
                  SUBSTR(P.CODIGO,1, 6) COD,
                  MIN(P.NATURALEZA) ,
                  CASE WHEN (SUM(P.MAN_AUX_TER) + SUM(P.MAN_CEN_CTO) + SUM(P.MAN_AUX_GEN) + SUM(P.MAN_AUX_REF) + SUM(P.MAN_AUX_FUE))<>0 THEN 0 ELSE CASE WHEN SUM(P.MOVIMIENTO) NOT IN(0) THEN -1 ELSE 0 END END MOVIMIENTO,  
                  CASE WHEN SUM(P.MAN_AUX_TER)<>0 THEN -1 ELSE 0 END MAN_AUX_TER,
                  CASE WHEN SUM(P.MAN_CEN_CTO)<>0 THEN -1 ELSE 0 END MAN_CEN_CTO,
                  CASE WHEN SUM(P.MAN_AUX_GEN)<>0 THEN -1 ELSE 0 END MAN_AUX_GEN,
                  CASE WHEN SUM(P.MAN_AUX_REF)<>0 THEN -1 ELSE 0 END MAN_AUX_REF,
                  CASE WHEN SUM(P.MAN_AUX_FUE)<>0 THEN -1 ELSE 0 END MAN_AUX_FUE,
                  MIN(P.NOMBRE),
                  MIN(P.CLASECUENTA)';    

    MI_VALORES:=MI_VALORES||' FROM PLAN_CONTABLE P LEFT JOIN (SELECT COMPANIA, ANO, SUBSTR(CODIGO,1,6) CODIGO
                                                              FROM PLAN_CONTABLE 
                                                              WHERE COMPANIA='''||UN_COMPANIACON||''') CO
                                ON '''||UN_COMPANIACON||''' = CO.COMPANIA 
                               AND P.ANO                    = CO.ANO
                               AND SUBSTR(P.CODIGO,1,6)     = CO.CODIGO
                              WHERE P.COMPANIA IN (' || MI_COMPANIAS || ')
                                AND P.ANO        = ' || UN_ANIO      || '
                                AND P.PERMITECONSOLIDAR NOT IN (0)
                                AND CO.COMPANIA IS NULL
                              GROUP BY '''||UN_COMPANIACON||''', 
                                    P.ANO, 
                                    SUBSTR(P.CODIGO,1,6)';
    BEGIN 
        BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'PLAN_CONTABLE', 
                                                   UN_ACCION   => 'IS',
                                                   UN_CAMPOS   =>  MI_CAMPOS,
                                                   UN_VALORES  =>  MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN    
        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD   => SQLCODE,
                                    UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_INSPLANCONTABLE
                                   );
    END;

    MI_CAMPOS:= 'COMPANIA,
                  ANO,
                  CODIGO,
                  TERCERO,
                  SUCURSAL,
                  AUXILIAR,
                  CENTRO_COSTO,
                  REFERENCIA,
                  FUENTE_RECURSO,
                  SALDOINICIAL';

    MI_VALORES:= 'SELECT '''||UN_COMPANIACON||''',
                          SALDOSINICIALES.ANO,
                          SUBSTR(SALDOSINICIALES.CODIGO,1, 6) COD,
                          SALDOSINICIALES.TERCERO,
                          SALDOSINICIALES.SUCURSAL,
                          SALDOSINICIALES.AUXILIAR,
                          SALDOSINICIALES.CENTRO_COSTO,
                          SALDOSINICIALES.REFERENCIA,
                          SALDOSINICIALES.FUENTE_RECURSO,
                          SUM(SALDOSINICIALES.SALDOINICIAL) SALDOINICIAL
                  FROM SALDOSINICIALES INNER JOIN  PLAN_CONTABLE
                    ON SALDOSINICIALES.COMPANIA  = PLAN_CONTABLE.COMPANIA
                   AND SALDOSINICIALES.ANO       = PLAN_CONTABLE.ANO
                   AND SUBSTR(SALDOSINICIALES.CODIGO,1, 6)  = PLAN_CONTABLE.CODIGO
                  WHERE SALDOSINICIALES.COMPANIA IN (SELECT NITCOMPANIA
                                                     FROM   CONSOLIDADA 
                                                     WHERE  COMPANIACON = '''||UN_COMPANIACON||''')
                   AND SALDOSINICIALES.ANO = '||UN_ANIO||'
                   AND PLAN_CONTABLE.PERMITECONSOLIDAR NOT IN (0)
                  GROUP BY '''||UN_COMPANIACON||''',
                    SALDOSINICIALES.ANO,
                    SUBSTR(SALDOSINICIALES.CODIGO,1, 6),
                    SALDOSINICIALES.TERCERO,
                    SALDOSINICIALES.SUCURSAL,
                    SALDOSINICIALES.AUXILIAR,
                    SALDOSINICIALES.CENTRO_COSTO,
                    SALDOSINICIALES.REFERENCIA,
                    SALDOSINICIALES.FUENTE_RECURSO';
    BEGIN 
        BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'SALDOSINICIALES', 
                                                   UN_ACCION   => 'IS',
                                                   UN_CAMPOS   =>  MI_CAMPOS,
                                                   UN_VALORES  =>  MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN    
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                   UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_INSPLANCONTABLE
                                  );
    END;

    PCK_CONTABILIDAD.PR_SUBIR_SALDOS_INICIALES(UN_COMPANIA   => UN_COMPANIACON
                                             , UN_ANIO       => UN_ANIO
                                             , UN_MODIFICADOR  =>' ');

    PCK_CONTABILIDAD.GL_PASASALDO := 0;
    <<TIPOCOMPROBANTE>>
    FOR MI_RS IN (SELECT
                    TIPO_COMPROBANTE.CODIGO,
                    TIPO_COMPROBANTE.NOMBRE,
                    TIPO_COMPROBANTE.PIDE_RETENCION,
                    TIPO_COMPROBANTE.TIPO_DOCUMENTO,
                    TIPO_COMPROBANTE.CLASE_CONTABLE,
                    TIPO_COMPROBANTE.FORMATO,
                    TIPO_COMPROBANTE.COMPRELACIONADO,
                    TIPO_COMPROBANTE.OBLIGA_AFECT_PPTAL,
                    TIPO_COMPROBANTE.TIPO_CRUCECUENTAS 
                  FROM TIPO_COMPROBANTE LEFT JOIN (SELECT COMPANIA, CODIGO
                                                    FROM TIPO_COMPROBANTE
                                                    WHERE COMPANIA = UN_COMPANIACON
                                                    )CONSOLIDADO
                    ON UN_COMPANIACON          = CONSOLIDADO.COMPANIA
                   AND TIPO_COMPROBANTE.CODIGO = CONSOLIDADO.CODIGO
                  WHERE  TIPO_COMPROBANTE.COMPANIA IN (SELECT NITCOMPANIA
                                      FROM   CONSOLIDADA 
                                      WHERE  COMPANIACON=UN_COMPANIACON)
                     AND CONSOLIDADO.COMPANIA IS NULL                  
                  ORDER BY TIPO_COMPROBANTE.CODIGO)
    LOOP
        SELECT  COUNT(*) 
        INTO MI_I
        FROM    TIPO_COMPROBANTE 
        WHERE   COMPANIA  = UN_COMPANIACON
          AND   CODIGO    = MI_RS.CODIGO;

        IF MI_I=0 THEN
            MI_CAMPOS:= 'COMPANIA,
                         CODIGO,
                         NOMBRE,
                         PIDE_RETENCION,
                         TIPO_DOCUMENTO,
                         CLASE_CONTABLE,
                         FORMATO,
                         COMPRELACIONADO';

            MI_VALORES:=''''||  UN_COMPANIACON        ||''',
                        ''' ||  MI_RS.CODIGO          ||''',
                        ''' ||  MI_RS.NOMBRE          ||''',
                        '   ||  MI_RS.PIDE_RETENCION  ||',
                        ''' ||  MI_RS.TIPO_DOCUMENTO  ||''',
                        ''' ||  MI_RS.CLASE_CONTABLE  ||''',
                        ''' ||  MI_RS.FORMATO         ||''',
                        ''' ||  MI_RS.COMPRELACIONADO ||'''';
            BEGIN 
                BEGIN           
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>  'TIPO_COMPROBANTE',
                                                           UN_ACCION   =>  'I',
                                                           UN_CAMPOS   =>  MI_CAMPOS,
                                                           UN_VALORES  =>  MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                MI_ERROR(1).CLAVE := 'CODIGO';
                MI_ERROR(1).VALOR := MI_RS.CODIGO ||' '|| MI_RS.NOMBRE;
                PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD   => SQLCODE,
                                            UN_REEMPLAZOS => MI_ERROR,
                                            UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_INSTIPOCOMPROBANTE
                                           );
            END;
        END IF;
    END LOOP TIPOCOMPROBANTE;

    --Validaciones para relaciones obligatorias que se crearon en Comprobante_CNT

    PCK_CONTABILIDAD1.PR_INSERTARVARIOS (UN_COMPANIA 		 => UN_COMPANIACON,
                       UN_ANO 			 => UN_ANIO,
                       UN_TABLA			 => 'FUENTE_RECURSOS');
    PCK_CONTABILIDAD1.PR_INSERTARVARIOS (UN_COMPANIA 		 => UN_COMPANIACON,
                       UN_ANO 			 => UN_ANIO,
                       UN_TABLA			 => 'REFERENCIA');
    PCK_CONTABILIDAD1.PR_INSERTARVARIOS (UN_COMPANIA 		 => UN_COMPANIACON,
                       UN_ANO 			 => UN_ANIO,
                       UN_TABLA			 => 'CENTRO_COSTO');
    PCK_CONTABILIDAD1.PR_INSERTARVARIOS (UN_COMPANIA 		 => UN_COMPANIACON,
                       UN_ANO 			 => UN_ANIO,
                       UN_TABLA			 => 'AUXILIAR');
    COMMIT;

    <<CICLOMES>>
    FOR MI_MES IN 1 .. 12 
    LOOP
        --19/09/2018 JP Se elimina los repetidos para evitar que se llenen duplicados
        <<ELIMINARREPETIDOS>>
        FOR RS_D IN (SELECT D.COMPANIA, 
                            D.ANO, 
                            D.TIPO_CPTE, 
                            D.COMPROBANTE, 
                            D.CUENTA, 
                            D.TERCERO, 
                            D.SUCURSAL, 
                            D.CENTRO_COSTO, 
                            D.REFERENCIA, 
                            D.AUXILIAR, 
                            D.FUENTE_RECURSO, 
                            COUNT(D.COMPANIA)
                    FROM DETALLE_COMPROBANTE_CNT D 
                    WHERE D.COMPANIA = UN_COMPANIACON
                      AND D.ANO      = UN_ANIO
                      AND TO_NUMBER(TO_CHAR(D.FECHA,'MM'))      = MI_MES
                    GROUP BY  D.COMPANIA, D.ANO, 
                              D.TIPO_CPTE, D.COMPROBANTE, 
                              D.CUENTA, D.TERCERO, 
                              D.SUCURSAL, D.CENTRO_COSTO, 
                              D.REFERENCIA, D.AUXILIAR, D.FUENTE_RECURSO
                    HAVING COUNT(D.COMPANIA)>1
                    )
        LOOP
            MI_CONDICION :=  'COMPANIA    = ''' || RS_D.COMPANIA       ||''' 
                          AND ANO         = '   || RS_D.ANO            ||' 
                          AND TIPO_CPTE   = ''' || RS_D.TIPO_CPTE      ||'''   
                          AND COMPROBANTE = '   || RS_D.COMPROBANTE    ||'
                          AND TO_NUMBER(TO_CHAR(FECHA,''MM'')) = '      || MI_MES    ||'
                          AND CUENTA      = ''' || RS_D.CUENTA         ||'''
                          AND TERCERO     = ''' || RS_D.TERCERO        ||'''
                          AND SUCURSAL    = ''' || RS_D.SUCURSAL       ||'''
                          AND CENTRO_COSTO= ''' || RS_D.CENTRO_COSTO   ||'''
                          AND REFERENCIA  = ''' || RS_D.REFERENCIA     ||'''
                          AND AUXILIAR    = ''' || RS_D.AUXILIAR       ||'''
                          AND FUENTE_RECURSO = '''  || RS_D.FUENTE_RECURSO    ||'''';
            BEGIN
                BEGIN
                    PCK_DATOS.GL_RTA :=PCK_DATOS.FC_ACME(UN_TABLA      =>  'DETALLE_COMPROBANTE_CNT',
                                                        UN_ACCION     =>  'E',
                                                        UN_CONDICION  =>  MI_CONDICION );
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN    
                MI_ERROR(1).CLAVE := 'NUMERO';
                MI_ERROR(1).VALOR := RS_D.COMPROBANTE;
                PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD   => SQLCODE,
                                            UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_ELIMDETCPTE
                                          );
            END;          
        END LOOP ELIMINARREPETIDOS;

        --INSERTA EN LA PLANA AJUSTE LOS VALORES QUE SERAN OBJETO DE CONSOLIDACION POR MES
        MI_CAMPOS  := 'COMPANIA, 
                       ANO, 
                       TIPO_CPTE, 
                       COMPROBANTE, 
                       CUENTA, 
                       FECHA, 
                       TERCERO, 
                       SUCURSAL, 
                       CENTRO_COSTO, 
                       REFERENCIA, 
                       AUXILIAR, 
                       FUENTE_RECURSOS, 
                       VALOR_DEBITO,
                       VALOR_CREDITO,
                       BASE_GRAVABLE,
                       CIERRE,
                       CONSECUTIVO,
                       NATURALEZA';
        MI_VALORES := 'SELECT DET.*, 
                              ROW_NUMBER() OVER (PARTITION BY DET.TIPO_CPTE, DET.NOCOMP ORDER BY  DET.TIPO_CPTE, DET.NOCOMP) CONSECUTIVO,
                              NATU.NATURALEZA 
                        FROM (
                        SELECT 
                            ''' || UN_COMPANIACON || ''' COMPANIA,
                            DETALLE_COMPROBANTE_CNT.ANO,
                            DETALLE_COMPROBANTE_CNT.TIPO_CPTE,
                            DETALLE_COMPROBANTE_CNT.ANO*1000000+TO_NUMBER(TO_CHAR(DETALLE_COMPROBANTE_CNT.FECHA,''MM''))*10000+1 NOCOMP,
                            SUBSTR(DETALLE_COMPROBANTE_CNT.CUENTA,1,6) CTA,
                            LAST_DAY(TO_DATE(TO_CHAR(DETALLE_COMPROBANTE_CNT.FECHA,''DD/MM/YYYY''),''DD/MM/YYYY'')) FECHACOMP,
                            DETALLE_COMPROBANTE_CNT.TERCERO,
                            DETALLE_COMPROBANTE_CNT.SUCURSAL,
                            DETALLE_COMPROBANTE_CNT.CENTRO_COSTO,
                            DETALLE_COMPROBANTE_CNT.REFERENCIA,
                            DETALLE_COMPROBANTE_CNT.AUXILIAR,
                            DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO,
                            SUM(DETALLE_COMPROBANTE_CNT.VALOR_DEBITO) VALOR_DEBITO, 
                            SUM(DETALLE_COMPROBANTE_CNT.VALOR_CREDITO) VALOR_CREDITO,
                            SUM(DETALLE_COMPROBANTE_CNT.BASE_GRAVABLE) BASE_GRAVABLE,
                            DETALLE_COMPROBANTE_CNT.CIERRE
                          FROM  DETALLE_COMPROBANTE_CNT  INNER JOIN PLAN_CONTABLE
                             ON DETALLE_COMPROBANTE_CNT.COMPANIA  = PLAN_CONTABLE.COMPANIA
                            AND DETALLE_COMPROBANTE_CNT.ANO       = PLAN_CONTABLE.ANO
                            AND DETALLE_COMPROBANTE_CNT.CUENTA    = PLAN_CONTABLE.CODIGO
                          INNER JOIN CONSOLIDADA
                             ON DETALLE_COMPROBANTE_CNT.COMPANIA = CONSOLIDADA.NITCOMPANIA  
                          WHERE DETALLE_COMPROBANTE_CNT.COMPANIA = CONSOLIDADA.NITCOMPANIA  
                            AND DETALLE_COMPROBANTE_CNT.ANO = ' || UN_ANIO || '
                            AND TO_NUMBER(TO_CHAR(DETALLE_COMPROBANTE_CNT.FECHA,''MM'')) = ' || MI_MES || '
                            AND PLAN_CONTABLE.PERMITECONSOLIDAR NOT IN (0)
                            AND CONSOLIDADA.COMPANIACON     =''' || UN_COMPANIACON ||'''
                          GROUP BY ''' || UN_COMPANIACON || ''',
                            DETALLE_COMPROBANTE_CNT.ANO,
                            DETALLE_COMPROBANTE_CNT.TIPO_CPTE,
                            DETALLE_COMPROBANTE_CNT.ANO*1000000+TO_NUMBER(TO_CHAR(DETALLE_COMPROBANTE_CNT.FECHA,''MM''))*10000+1,
                            SUBSTR(DETALLE_COMPROBANTE_CNT.CUENTA,1,6),
                            LAST_DAY(TO_DATE(TO_CHAR(DETALLE_COMPROBANTE_CNT.FECHA,''DD/MM/YYYY''),''DD/MM/YYYY'')),
                            DETALLE_COMPROBANTE_CNT.TERCERO,
                            DETALLE_COMPROBANTE_CNT.SUCURSAL,
                            DETALLE_COMPROBANTE_CNT.CENTRO_COSTO,
                            DETALLE_COMPROBANTE_CNT.REFERENCIA,
                            DETALLE_COMPROBANTE_CNT.AUXILIAR,
                            DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO,
                            DETALLE_COMPROBANTE_CNT.CIERRE
                        ) DET
                          INNER JOIN PLAN_CONTABLE NATU
                             ON DET.COMPANIA  = NATU.COMPANIA
                            AND DET.ANO       = NATU.ANO
                            AND DET.CTA       = NATU.CODIGO
                         ';
        BEGIN 
            BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'TEMP_PLANA_AJUSTES', 
                                                       UN_ACCION   => 'IS',
                                                       UN_CAMPOS   =>  MI_CAMPOS,
                                                       UN_VALORES  =>  MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN   
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD   => SQLCODE,
                                        UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_INSPLANCONTABLE
                                       );
        END;

        /*ELIMINA DEL DETALLE DE LA COMPANIA CONCILIADORA LOS DETALLES QUE NO ESTAN EN LAS COMPANIAS A CONCILIAR
          SE DEBE REALIZAR DESPUES DE REGISTRAR LOS DATOS EN LA TEMPORAL  
        */
        <<ELIMINARSOBRANTE>>
        FOR RS_S IN (SELECT D.COMPANIA,
                            D.ANO, 
                            D.TIPO_CPTE, 
                            D.COMPROBANTE, 
                            D.CUENTA, 
                            D.TERCERO, 
                            D.SUCURSAL, 
                            D.CENTRO_COSTO, 
                            D.REFERENCIA, 
                            D.AUXILIAR, 
                            D.FUENTE_RECURSO
                    FROM DETALLE_COMPROBANTE_CNT D LEFT JOIN TEMP_PLANA_AJUSTES P
                      ON D.COMPANIA        = P.COMPANIA 
                     AND D.ANO             = P.ANO 
                     AND D.TIPO_CPTE       = P.TIPO_CPTE 
                     AND D.COMPROBANTE     = P.COMPROBANTE 
                     AND D.CUENTA          = P.CUENTA 
                     AND D.TERCERO         = P.TERCERO 
                     AND D.SUCURSAL        = P.SUCURSAL 
                     AND D.CENTRO_COSTO    = P.CENTRO_COSTO 
                     AND D.REFERENCIA      = P.REFERENCIA 
                     AND D.AUXILIAR        = P.AUXILIAR 
                     AND D.FUENTE_RECURSO  = P.FUENTE_RECURSOS 
                    WHERE D.COMPANIA = UN_COMPANIACON
                      AND D.ANO      = UN_ANIO
                      AND D.MES      = MI_MES 
                      AND P.COMPANIA IS NULL
                      )
        LOOP
            MI_CONDICION :=  'COMPANIA    = ''' || UN_COMPANIACON      ||''' 
                          AND ANO         = '   || RS_S.ANO            ||' 
                          AND TIPO_CPTE   = ''' || RS_S.TIPO_CPTE      ||'''   
                          AND COMPROBANTE = '   || RS_S.COMPROBANTE    ||'
                          AND TO_NUMBER(TO_CHAR(FECHA,''MM'')) = '      || MI_MES    ||'
                          AND CUENTA      = ''' || RS_S.CUENTA         ||'''
                          AND TERCERO     = ''' || RS_S.TERCERO        ||'''
                          AND SUCURSAL    = ''' || RS_S.SUCURSAL       ||'''
                          AND CENTRO_COSTO= ''' || RS_S.CENTRO_COSTO   ||'''
                          AND REFERENCIA  = ''' || RS_S.REFERENCIA     ||'''
                          AND AUXILIAR    = ''' || RS_S.AUXILIAR       ||'''
                          AND FUENTE_RECURSO = '''  || RS_S.FUENTE_RECURSO    ||'''';
            BEGIN
                BEGIN
                    PCK_DATOS.GL_RTA :=PCK_DATOS.FC_ACME(UN_TABLA      =>  'DETALLE_COMPROBANTE_CNT',
                                                        UN_ACCION     =>  'E',
                                                        UN_CONDICION  =>  MI_CONDICION );
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN    
                MI_ERROR(1).CLAVE := 'NUMERO';
                MI_ERROR(1).VALOR := RS_S.COMPROBANTE;
                PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD   => SQLCODE,
                                            UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_ELIMDETCPTE
                                          );
            END;  
        END LOOP ELIMINARSOBRANTE;

        --ACTUALIZA HEADER DE LOS COMPROBANTES POR MES
        MI_MERGEUSING := 'SELECT DISTINCT TEM.COMPANIA,
                                TEM.ANO,
                                TEM.TIPO_CPTE,
                                TEM.COMPROBANTE,
                                TEM.FECHA,
                                TIPO_COMPROBANTE.NOMBRE,
                                PCK_SYSMAN_UTL.FC_NITVALIDAR(UN_COMPANIA => TEM.COMPANIA, 
                                                             UN_NIT      => COMPANIA.NITCOMPANIA) TERCERO,
                                ''001'' SUCURSAL
                        FROM TEMP_PLANA_AJUSTES TEM INNER JOIN TIPO_COMPROBANTE 
                          ON TEM.COMPANIA  = TIPO_COMPROBANTE.COMPANIA
                         AND TEM.TIPO_CPTE = TIPO_COMPROBANTE.CODIGO
                        INNER JOIN  COMPANIA 
                          ON COMPANIA.CODIGO= TEM.COMPANIA
                        WHERE TEM.COMPANIA   =''' || UN_COMPANIACON || '''
                          AND TEM.ANO        ='   || UN_ANIO;
        MI_MERGEENLACE := 'VISTA.COMPANIA        = TABLA.COMPANIA 
                       AND VISTA.ANO             = TABLA.ANO 
                       AND VISTA.TIPO_CPTE       = TABLA.TIPO 
                       AND VISTA.COMPROBANTE     = TABLA.NUMERO';
        MI_MERGEEXISTE := ' UPDATE SET TABLA.FECHA         = VISTA.FECHA,
                                       TABLA.DESCRIPCION   = VISTA.NOMBRE,
                                       TABLA.TERCERO       = VISTA.TERCERO,
                                       TABLA.SUCURSAL      = VISTA.SUCURSAL';        
        MI_MERGENOEXIS := ' INSERT (COMPANIA,
                                    ANO,
                                    TIPO,
                                    NUMERO,
                                    FECHA,
                                    DESCRIPCION,
                                    TERCERO,
                                    SUCURSAL)
                            VALUES(VISTA.COMPANIA,
                                    VISTA.ANO,
                                    VISTA.TIPO_CPTE,
                                    VISTA.COMPROBANTE,
                                    VISTA.FECHA,
                                    VISTA.NOMBRE,
                                    VISTA.TERCERO,
                                    VISTA.SUCURSAL)';
        BEGIN
            BEGIN  
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA       => 'COMPROBANTE_CNT'
                                                       ,UN_ACCION      => 'IM'
                                                       ,UN_MERGEUSING  => MI_MERGEUSING
                                                       ,UN_MERGEENLACE => MI_MERGEENLACE
                                                       ,UN_MERGEEXISTE => MI_MERGEEXISTE
                                                       ,UN_MERGENOEXIS => MI_MERGENOEXIS); 
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD   => SQLCODE,
                    UN_ERROR_COD => PCK_ERRORES.ERR_DETALLE_AFECTADO
               );
        END;

        --ACTUALIZANDO DETALLES DEL COMPROBANTE
        MI_MERGEUSING := 'SELECT  TEMP_PLANA_AJUSTES.COMPANIA,
                                TEMP_PLANA_AJUSTES.ANO,
                                TEMP_PLANA_AJUSTES.TIPO_CPTE,
                                TEMP_PLANA_AJUSTES.COMPROBANTE,
                                ROW_NUMBER() OVER (PARTITION BY TEMP_PLANA_AJUSTES.COMPANIA,
                                                                TEMP_PLANA_AJUSTES.ANO,
                                                                TEMP_PLANA_AJUSTES.TIPO_CPTE, 
                                                                TEMP_PLANA_AJUSTES.COMPROBANTE
                                                    ORDER BY  TEMP_PLANA_AJUSTES.COMPANIA,
                                                              TEMP_PLANA_AJUSTES.ANO,
                                                              TEMP_PLANA_AJUSTES.TIPO_CPTE, 
                                                              TEMP_PLANA_AJUSTES.COMPROBANTE) 
                                + NVL(CONSE.CONSECUTIVO,0) CONSECUTIVO,
                                TEMP_PLANA_AJUSTES.CUENTA,
                                TEMP_PLANA_AJUSTES.FECHA,
                                TEMP_PLANA_AJUSTES.NATURALEZA,
                                TEMP_PLANA_AJUSTES.VALOR_DEBITO,
                                TEMP_PLANA_AJUSTES.VALOR_CREDITO,
                                TEMP_PLANA_AJUSTES.BASE_GRAVABLE,
                                TEMP_PLANA_AJUSTES.TERCERO,
                                TEMP_PLANA_AJUSTES.SUCURSAL,
                                TEMP_PLANA_AJUSTES.CENTRO_COSTO,
                                TEMP_PLANA_AJUSTES.REFERENCIA,
                                TEMP_PLANA_AJUSTES.AUXILIAR,
                                TEMP_PLANA_AJUSTES.FUENTE_RECURSOS
                        FROM TEMP_PLANA_AJUSTES LEFT JOIN ( SELECT COMPANIA, ANO, TIPO_CPTE, COMPROBANTE, MAX(CONSECUTIVO) CONSECUTIVO
                                       FROM DETALLE_COMPROBANTE_CNT
                                       WHERE COMPANIA = ''' || UN_COMPANIACON || '''
                                       GROUP BY COMPANIA, ANO, TIPO_CPTE, COMPROBANTE) CONSE
                             ON CONSE.COMPANIA    = TEMP_PLANA_AJUSTES.COMPANIA
                            AND CONSE.ANO         = TEMP_PLANA_AJUSTES.ANO 
                            AND CONSE.TIPO_CPTE   = TEMP_PLANA_AJUSTES.TIPO_CPTE
                            AND CONSE.COMPROBANTE = TEMP_PLANA_AJUSTES.COMPROBANTE
                        WHERE TEMP_PLANA_AJUSTES.COMPANIA   =''' || UN_COMPANIACON || '''
                          AND TEMP_PLANA_AJUSTES.ANO        ='   || UN_ANIO;
        MI_MERGEENLACE := ' VISTA.COMPANIA        = TABLA.COMPANIA 
                       AND VISTA.ANO             = TABLA.ANO 
                       AND VISTA.TIPO_CPTE       = TABLA.TIPO_CPTE 
                       AND VISTA.COMPROBANTE     = TABLA.COMPROBANTE 
                       AND VISTA.CUENTA          = TABLA.CUENTA
                       AND VISTA.TERCERO         = TABLA.TERCERO
                       AND VISTA.SUCURSAL        = TABLA.SUCURSAL
                       AND VISTA.CENTRO_COSTO    = TABLA.CENTRO_COSTO
                       AND VISTA.REFERENCIA      = TABLA.REFERENCIA
                       AND VISTA.AUXILIAR        = TABLA.AUXILIAR
                       AND VISTA.FUENTE_RECURSOS = TABLA.FUENTE_RECURSO ';
        MI_MERGEEXISTE := ' UPDATE SET TABLA.VALOR_DEBITO  = VISTA.VALOR_DEBITO,
                                       TABLA.VALOR_CREDITO = VISTA.VALOR_CREDITO,
                                       TABLA.BASE_GRAVABLE = VISTA.BASE_GRAVABLE
                            WHERE TABLA.VALOR_DEBITO  <> VISTA.VALOR_DEBITO 
                               OR TABLA.VALOR_CREDITO <> VISTA.VALOR_CREDITO
                               OR TABLA.BASE_GRAVABLE <> VISTA.BASE_GRAVABLE';        
        MI_MERGENOEXIS := ' INSERT (TABLA.COMPANIA,
                                    TABLA.ANO,
                                    TABLA.TIPO_CPTE,
                                    TABLA.COMPROBANTE,
                                    TABLA.CONSECUTIVO,
                                    TABLA.CUENTA,
                                    TABLA.FECHA,
                                    TABLA.NATURALEZA,
                                    TABLA.VALOR_DEBITO,
                                    TABLA.VALOR_CREDITO,
                                    TABLA.BASE_GRAVABLE,
                                    TABLA.TERCERO,
                                    TABLA.SUCURSAL,
                                    TABLA.CENTRO_COSTO,
                                    TABLA.REFERENCIA,
                                    TABLA.AUXILIAR,
                                    TABLA.FUENTE_RECURSO)
                            VALUES(VISTA.COMPANIA,
                                    VISTA.ANO,
                                    VISTA.TIPO_CPTE,
                                    VISTA.COMPROBANTE,
                                    VISTA.CONSECUTIVO,
                                    VISTA.CUENTA,
                                    VISTA.FECHA,
                                    VISTA.NATURALEZA,
                                    VISTA.VALOR_DEBITO,
                                    VISTA.VALOR_CREDITO,
                                    VISTA.BASE_GRAVABLE,
                                    VISTA.TERCERO,
                                    VISTA.SUCURSAL,
                                    VISTA.CENTRO_COSTO,
                                    VISTA.REFERENCIA,
                                    VISTA.AUXILIAR,
                                    VISTA.FUENTE_RECURSOS)';
        BEGIN
            BEGIN  
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA       => 'DETALLE_COMPROBANTE_CNT'
                                                       ,UN_ACCION      => 'IM'
                                                       ,UN_MERGEUSING  => MI_MERGEUSING
                                                       ,UN_MERGEENLACE => MI_MERGEENLACE
                                                       ,UN_MERGEEXISTE => MI_MERGEEXISTE
                                                       ,UN_MERGENOEXIS => MI_MERGENOEXIS); 
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN

                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD   => SQLCODE,
                    UN_ERROR_COD => PCK_ERRORES.ERR_DETALLE_AFECTADO
               );
        END;
        COMMIT;
    END LOOP CICLOMES; 

      BEGIN  
    --CC_2267 GROJAS: Crea las cuentas no existentes en SALDO_AUX_CONTABLE
    MI_CAMPOS := ' COMPANIA, ' ||
                 ' ANO, ' ||
                 ' CODIGO , ' ||
                 ' NATURALEZA, ' || 
                 ' CENTRO_COSTO, ' ||
                 ' TERCERO, ' ||
                 ' SUCURSAL, ' ||
                 ' AUXILIAR, ' ||
                 ' REFERENCIA, ' ||
                 ' FUENTE_RECURSO';
    MI_VALORES := ' SELECT DISTINCT  ' ||
                  '        DET.COMPANIA,   ' ||
                  '        DET.ANO,  ' ||
                  '        DET.CUENTA,   ' ||
                  '        DET.NATURALEZA,  ' ||
                  '        DECODE(PLAN.MAN_CEN_CTO, 0,  ''' || PCK_DATOS.FC_CONS_CENTRO     || '''    ,DET.CENTRO_COSTO) ,    ' || 
                  '        DECODE(PLAN.MAN_AUX_TER, 0,  ''' || PCK_DATOS.FC_CONS_TERCERO    || '''    ,DET.TERCERO)      ,    ' ||
                  '        DECODE(PLAN.MAN_AUX_TER, 0,  ''' || PCK_DATOS.FC_CONS_SUCURSAL   || '''    ,DET.SUCURSAL)     ,    ' ||
                  '        DECODE(PLAN.MAN_AUX_GEN, 0,  ''' || PCK_DATOS.FC_CONS_AUXILIAR   || '''    ,DET.AUXILIAR)     ,    ' ||
                  '        DECODE(PLAN.MAN_AUX_REF, 0,  ''' || PCK_DATOS.FC_CONS_REFERENCIA || '''    ,DET.REFERENCIA)   ,    ' ||
                  '        DECODE(PLAN.MAN_AUX_FUE, 0,  ''' || PCK_DATOS.FC_CONS_FUENTE     || '''    ,DET.FUENTE_RECURSO)    ' ||
                  ' FROM  DETALLE_COMPROBANTE_CNT DET  ' ||
                  ' INNER JOIN PLAN_CONTABLE PLAN ' ||
                  '   ON DET.COMPANIA        = PLAN.COMPANIA   ' ||
                  '  AND DET.ANO             = PLAN.ANO       ' ||
                  '  AND DET.CUENTA          = PLAN.CODIGO     ' ||
                  ' LEFT JOIN SALDO_AUX_CONTABLE SA    ' ||
                  '   ON DET.COMPANIA        = SA.COMPANIA ' ||  
                  '  AND DET.ANO             = SA.ANO       ' ||
                  '  AND DET.CUENTA          = SA.CODIGO     ' ||
                  '  AND DECODE(PLAN.MAN_CEN_CTO, 0, ''' || PCK_DATOS.FC_CONS_CENTRO     || '''   ,DET.CENTRO_COSTO)    =SA.CENTRO_COSTO  ' ||
                  '  AND DECODE(PLAN.MAN_AUX_TER, 0, ''' || PCK_DATOS.FC_CONS_TERCERO    || '''   ,DET.TERCERO)         =SA.TERCERO  ' ||
                  '  AND DECODE(PLAN.MAN_AUX_TER, 0, ''' || PCK_DATOS.FC_CONS_SUCURSAL   || '''   ,DET.SUCURSAL)        =SA.SUCURSAL  ' ||
                  '  AND DECODE(PLAN.MAN_AUX_GEN, 0, ''' || PCK_DATOS.FC_CONS_AUXILIAR   || '''   ,DET.AUXILIAR)        =SA.AUXILIAR  ' ||
                  '  AND DECODE(PLAN.MAN_AUX_REF, 0, ''' || PCK_DATOS.FC_CONS_REFERENCIA || '''   ,DET.REFERENCIA)      =SA.REFERENCIA  ' ||
                  '  AND DECODE(PLAN.MAN_AUX_FUE, 0, ''' || PCK_DATOS.FC_CONS_FUENTE     || '''   ,DET.FUENTE_RECURSO)  =SA.FUENTE_RECURSO  ' ||
                  ' WHERE DET.COMPANIA='''       || UN_COMPANIACON   || '''' ||
                   ' AND DET.ANO='              || UN_ANIO       || 
                   ' AND DET.CUENTA BETWEEN ''0'' AND ''99999999999999999999999999999999''' ||  
                   ' AND SA.COMPANIA IS NULL';
    BEGIN
      PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => 'SALDO_AUX_CONTABLE', 
                                            UN_ACCION  => 'IS', 
                                            UN_CAMPOS  => MI_CAMPOS,
                                            UN_VALORES => MI_VALORES);
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
    END;  
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
          MI_MSGERROR(1).CLAVE := 'ANIO';
          MI_MSGERROR(1).VALOR := UN_ANIO;
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_SALDOACEROINSERTAR,
              UN_REEMPLAZOS => MI_MSGERROR
           ); 
  END;

    --SE CUADRAN SALDOS Y SE MAYORIZA
    IF NVL(PCK_SYSMAN_UTL.FC_PAR( UN_COMPANIA   => ''''||UN_COMPANIACON||'''',
                              UN_NOMBRE     => 'EJECUTAR CUADRE DE SALDOS AL CONSOLIDAR',
                              UN_MODULO     => PCK_DATOS.FC_MODULOCONTABILIDAD,
                              UN_FECHA_PAR  => SYSDATE),'NO') = 'SI' THEN

      PCK_CONTABILIDAD.PR_CUADRECONTA_AUX(UN_COMPANIA   => UN_COMPANIACON,
                                          UN_ANIO       => UN_ANIO,
                                          UN_MES_INI    => 1,
                                          UN_MES_FIN    => 12);    
    END IF;
  RETURN '-1';
END FC_CONSOLIDARCOMPANIAH;

FUNCTION FC_SALDOFINALCAJA
    /*
      NAME              : FC_SALDOFINALCAJA nombre en Access saldoFinalDeCaja
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO PACANCHIQUE
      DATE MIGRADOR     : 19/04/2016
      TIME              : 3:47 PM
      SOURCE MODULE     : Contabilidad
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : Esta función retorna el saldo final de una cuenta a partir de una fecha y la naturaleza
      @NAME:  consultarSaldoFinalDeCaja
      @METHOD:  GET                    
    */
(
UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
UN_FECHA          IN DATE,
UN_CUENTA         IN PCK_SUBTIPOS.TI_CODIGOCONTA,
UN_NATURALEZA     IN PCK_SUBTIPOS.TI_NATURALEZACONTA
)RETURN NUMBER
AS
  MI_SALDOANTERIOR      PCK_SUBTIPOS.TI_DOBLE:=0;
  MI_PRIMERAFECHA       DATE;
  MI_MESANTERIOR        PCK_SUBTIPOS.TI_MES;
  MI_STRSQL             PCK_SUBTIPOS.TI_STRSQL;
  MI_ERROR_FUN          PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 11;
BEGIN
  MI_PRIMERAFECHA:=TO_DATE('01/'||EXTRACT(MONTH FROM UN_FECHA)||'/'||EXTRACT(YEAR FROM UN_FECHA));
  IF(EXTRACT (DAY FROM UN_FECHA+1) = 1) THEN
    MI_MESANTERIOR:=EXTRACT(MONTH FROM MI_PRIMERAFECHA);
  ELSE
    MI_MESANTERIOR:=EXTRACT(MONTH FROM MI_PRIMERAFECHA)-1;
  END IF;

  MI_STRSQL:='SELECT SALDO'||MI_MESANTERIOR||' FROM V_PLAN_CONTABLE WHERE COMPANIA ='''||UN_COMPANIA||''' AND ANO='||EXTRACT(YEAR FROM UN_FECHA)||' AND CODIGO='''||UN_CUENTA||'''
  ORDER BY COMPANIA, ANO, CODIGO';

  BEGIN
    EXECUTE IMMEDIATE MI_STRSQL INTO MI_SALDOANTERIOR;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_SALDOANTERIOR:=0;
  END;

  IF(EXTRACT (DAY FROM UN_FECHA+1) > 1) THEN
    FOR MI_RS IN (SELECT SUM(VALOR_DEBITO) VALOR_DEBITO, 
                  SUM(VALOR_CREDITO) VALOR_CREDITO 
                  FROM DETALLE_COMPROBANTE_CNT  
                  WHERE  COMPANIA=UN_COMPANIA 
                  AND ANO=EXTRACT(YEAR FROM UN_FECHA)
                  AND SUBSTR(CUENTA,0, LENGTH(UN_CUENTA))=UN_CUENTA
                  AND FECHA BETWEEN MI_PRIMERAFECHA AND UN_FECHA) LOOP
      IF(UN_NATURALEZA = 'C') THEN
        MI_SALDOANTERIOR:=MI_SALDOANTERIOR+(MI_RS.VALOR_CREDITO-MI_RS.VALOR_DEBITO);
      ELSE
        MI_SALDOANTERIOR:=MI_SALDOANTERIOR+(MI_RS.VALOR_DEBITO-MI_RS.VALOR_CREDITO);
      END IF;
    END LOOP;
  END IF;

  RETURN MI_SALDOANTERIOR;
  EXCEPTION WHEN OTHERS THEN
      PCK_DATOS.GL_ERROR_MSG := 'Interrupción al consultar el saldo final de la caja';
      PCK_DATOS.GL_ERROR_MSG := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'CONTABILIDAD','',SQLERRM );
      RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );

END FC_SALDOFINALCAJA;


FUNCTION FC_SALDOFINALC
    /*
      NAME              : FC_SALDOFINALC nombre en Access saldoFinalC
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO PACANCHIQUE
      DATE MIGRADOR     : 19/04/2016
      TIME              : 2:00 PM
      SOURCE MODULE     : Contabilidad
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : 
      @NAME:  consultarSaldoFinalCajayBancos
      @METHOD:  GET                    
    */
(
UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
UN_FECHA          IN DATE,
UN_CUENTA         IN PCK_SUBTIPOS.TI_CODIGOCONTA,
UN_NATURALEZA     IN PCK_SUBTIPOS.TI_NATURALEZACONTA
) RETURN NUMBER
AS
  MI_SALDOFINALC    PCK_SUBTIPOS.TI_DOBLE;
  MI_ERROR_FUN      PCK_SUBTIPOS.TI_ERROR_FUN :=GL_ERROR_NUM + 12;
BEGIN
  BEGIN

    WITH BOLETINDIARIOF AS (
          SELECT 
                CASE WHEN CLASECUENTA='J' THEN 'CAJAS' ELSE 'BANCOS' END TIPOCUENTA, 
                CODIGO, 
                PCK_CONTABILIDAD1.FC_SALDOFINALCAJA(UN_COMPANIA, UN_FECHA, UN_CUENTA, 'D') SALDOINICIAL,
                SUM(CASE WHEN CLASE_CONTABLE='S' AND CLASECUENTA='B' THEN DEBITO-CREDITO ELSE 0 END) CONSIGNACION, 
                SUM(CASE WHEN CLASE_CONTABLE IN ('A', 'D') THEN DEBITO-CREDITO ELSE 0 END) CHEQUEANULADO, 
                SUM(CASE WHEN CLASE_CONTABLE='E' AND CLASECUENTA='B' THEN CREDITO-DEBITO ELSE 0 END) GIRO, 
                SUM(CASE WHEN CLASE_CONTABLE='G' AND CLASECUENTA NOT IN ('J') THEN CREDITO-DEBITO ELSE 0 END) AS NOTADEBITO, 
                SUM(CASE WHEN CLASECUENTA='J' THEN DEBITO ELSE 0 END)-SUM(CASE WHEN CLASECUENTA='J' AND CLASE_CONTABLE='J' THEN CREDITO ELSE 0 END) INGRESOCAJA, 
                SUM(CASE WHEN CLASECUENTA='J' AND CLASE_CONTABLE <> 'J' THEN CREDITO ELSE 0 END) AS EGRESOCAJA
          FROM (SELECT 
                      PLAN_CONTABLE.COMPANIA, 
                      PLAN_CONTABLE.ANO, 
                      PLAN_CONTABLE.CODIGO, 
                      PLAN_CONTABLE.CLASECUENTA, 
                      TIPO_COMPROBANTE.CLASE_CONTABLE,
                      SUM(DETALLE_COMPROBANTE_CNT.VALOR_DEBITO) DEBITO, 
                      SUM(DETALLE_COMPROBANTE_CNT.VALOR_CREDITO) CREDITO 
                FROM V_PLAN_CONTABLE PLAN_CONTABLE 
                  INNER JOIN DETALLE_COMPROBANTE_CNT 
                    ON PLAN_CONTABLE.COMPANIA = DETALLE_COMPROBANTE_CNT.COMPANIA
                    AND PLAN_CONTABLE.CODIGO = DETALLE_COMPROBANTE_CNT.CUENTA
                    AND PLAN_CONTABLE.ANO = DETALLE_COMPROBANTE_CNT.ANO 
                  INNER JOIN TIPO_COMPROBANTE 
                    ON DETALLE_COMPROBANTE_CNT.COMPANIA = TIPO_COMPROBANTE.COMPANIA 
                    AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = TIPO_COMPROBANTE.CODIGO
                WHERE  DETALLE_COMPROBANTE_CNT.COMPANIA=UN_COMPANIA
                    AND PLAN_CONTABLE.CODIGO = UN_CUENTA
                    AND DETALLE_COMPROBANTE_CNT.ANO=EXTRACT(YEAR FROM UN_FECHA)
                    AND PLAN_CONTABLE.CLASECUENTA IN ('B','J')  
                    AND TO_CHAR(DETALLE_COMPROBANTE_CNT.FECHA, 'DD/MM/YYYY') = TO_CHAR(UN_FECHA, 'DD/MM/YYYY')  
                GROUP BY PLAN_CONTABLE.COMPANIA, PLAN_CONTABLE.ANO, PLAN_CONTABLE.CODIGO, PLAN_CONTABLE.CLASECUENTA, TIPO_COMPROBANTE.CLASE_CONTABLE) 
          GROUP BY CASE WHEN CLASECUENTA='J' THEN 'CAJAS' ELSE 'BANCOS' END, CODIGO )
    SELECT SALDOINICIAL+CONSIGNACION+CHEQUEANULADO-GIRO+NOTADEBITO+INGRESOCAJA-EGRESOCAJA
    INTO MI_SALDOFINALC
    FROM BOLETINDIARIOF
    WHERE SALDOINICIAL+CONSIGNACION+CHEQUEANULADO-GIRO-NOTADEBITO+INGRESOCAJA-EGRESOCAJA<>0;

    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_SALDOFINALC:=0;
    END;

    RETURN PCK_SYSMAN_UTL.FC_ROUND(MI_SALDOFINALC, 2);
    EXCEPTION WHEN OTHERS THEN
      PCK_DATOS.GL_ERROR_MSG := 'Interrupción al consultar el saldo final de la cuenta';
      PCK_DATOS.GL_ERROR_MSG := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'CONTABILIDAD','',SQLERRM );
      RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );
END FC_SALDOFINALC;

FUNCTION FC_PREPARARPIVOT_LIBRODIARIO
/*
  NAME              : FC_PREPARARPIVOT_LIBRODIARIOCOLUMNARIO
  AUTHORS           : SYSMAN  SAS
  AUTHOR            : YESIKA PAOLA BECERRA CASTRO
  DATE              : 29/04/2016
  TIME              : 16:07 AM

  MODIFIER          : JAVIER ANDRES RODRIGUEZ RIOS
  DATE MODIFIED     : 12/10/2016
  TIME              : 02:00 PM
  DESCRIPTION       : Retorna una cadena de texto con los valores de la columna V_PLAN_CONTABLE.CODIGO concatenados para usa dentro de un pivot.
                      Se ajusta para que el nombre de la columna no se repita y el ejecutar la 
                      consulta con el pivot no se presente el error "Columna definida de 
                      forma ambigua". Se optimiza ordenando los join y ajustando
                      el join entre V_PLAN_CONTABLE y DETALLE_COMPROBANTE_CNT por el ID.

  MODIFIER          : PABLO ANDRES ESPITIA CUCA
  DATE MODIFIED     : 12/28/2017
  TIME              : 12:29 PM
  DESCRIPTION       : Estructuración del codigo, buenas practicas.

  @NAME:   generarLibroDiario
  @METHOD: GET                      
*/
(
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPO_INICIAL   IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
  UN_TIPO_FINAL     IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
  UN_FECHA_INICIAL  IN DATE,
  UN_FECHA_FINAL    IN DATE
)
RETURN CLOB
AS
  MI_ERROR_FUN      PCK_SUBTIPOS.TI_ERROR_FUN:= GL_ERROR_NUM + 13;
  MI_RESULTADO      CLOB:=  NULL;
BEGIN 
  FOR RS IN (
    SELECT DISTINCT 
       SUBSTR(DETALLE_COMPROBANTE_CNT.CUENTA,0,15)||
       SUBSTR(PLAN_CONTABLE.NOMBRE          ,0,15) NOMBRECOLUMNA
      ,DETALLE_COMPROBANTE_CNT.CUENTA              CODIGO_CUENTA
    FROM DETALLE_COMPROBANTE_CNT
      INNER JOIN TERCERO  
         ON DETALLE_COMPROBANTE_CNT.COMPANIA = TERCERO.COMPANIA  
        AND DETALLE_COMPROBANTE_CNT.TERCERO  = TERCERO.NIT  
        AND DETALLE_COMPROBANTE_CNT.SUCURSAL = TERCERO.SUCURSAL 
      INNER JOIN PLAN_CONTABLE 
         ON DETALLE_COMPROBANTE_CNT.COMPANIA = PLAN_CONTABLE.COMPANIA  
        AND DETALLE_COMPROBANTE_CNT.ANO      = PLAN_CONTABLE.ANO 
        AND DETALLE_COMPROBANTE_CNT.CUENTA   = PLAN_CONTABLE.CODIGO
    WHERE DETALLE_COMPROBANTE_CNT.COMPANIA      = UN_COMPANIA
      AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE        BETWEEN UN_TIPO_INICIAL AND UN_TIPO_FINAL  
      AND TRUNC(DETALLE_COMPROBANTE_CNT.FECHA)     BETWEEN TO_DATE(UN_FECHA_INICIAL,'DD/MM/YYYY') AND TO_DATE(UN_FECHA_FINAL,'DD/MM/YYYY') 
      AND DETALLE_COMPROBANTE_CNT.VALOR_CREDITO NOT IN (0) 
    ORDER BY NOMBRECOLUMNA
  )  
  LOOP 
    IF MI_RESULTADO IS NULL THEN 
      MI_RESULTADO := ' '''|| RS.CODIGO_CUENTA ||''' "' ||RS.NOMBRECOLUMNA || '",''';
    ELSE
      IF RS.CODIGO_CUENTA IS NOT NULL THEN
        MI_RESULTADO :=MI_RESULTADO|| RS.CODIGO_CUENTA || ''' "' ||RS.NOMBRECOLUMNA || '",''';
      ELSIF RS.CODIGO_CUENTA IS NULL THEN
        MI_RESULTADO :=MI_RESULTADO;
      END IF;
    END IF;
  END LOOP;

  RETURN SUBSTR(MI_RESULTADO,1, LENGTH(MI_RESULTADO)-2);

  EXCEPTION WHEN OTHERS THEN
    PCK_DATOS.GL_ERROR_MSG:= 'Proceso interrumpido generando pivot de Libro Diario Columnario';
    PCK_DATOS.GL_ERROR_MSG:= PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'DETALLE_COMPROBANTE_CNT','',SQLERRM );
    RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );     

END FC_PREPARARPIVOT_LIBRODIARIO;

FUNCTION FC_PREPARARPIVOT_NVLLIBRO 
/*
  NAME              : FC_PREPARARPIVOT_NVLLIBRO
  AUTHORS           : SYSMAN  SAS
  AUTHOR            : YESIKA PAOLA BECERRA CASTRO
  DATE              : 04/05/2016
  TIME              : 09:07 AM

  MODIFIER          : PABLO ANDRES ESPITIA CUCA
  DATE MODIFIED     : 2017/05/22
  TIME              : 16:33
  DESCRIPTION       : Retorna los NVL de CODIGO_CUENTA 


  @NAME:  prepararLibroDiario
  @METHOD:  GET

*/
(
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPO_INICIAL   IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
  UN_TIPO_FINAL     IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
  UN_FECHA_INICIAL  IN DATE,
  UN_FECHA_FINAL    IN DATE  
)
RETURN CLOB
AS
  MI_RESULTADO      CLOB:=  NULL;  
  MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;  
BEGIN 
  BEGIN
    FOR RS IN (
      SELECT DISTINCT 
         SUBSTR(DETALLE_COMPROBANTE_CNT.CUENTA,0,15)||
         SUBSTR(PLAN_CONTABLE.NOMBRE,0,15) NOMBRECOLUMNA
        ,DETALLE_COMPROBANTE_CNT.CUENTA    NOMBRE_CUENTA
      FROM DETALLE_COMPROBANTE_CNT
        INNER JOIN TERCERO  
           ON DETALLE_COMPROBANTE_CNT.COMPANIA = TERCERO.COMPANIA  
          AND DETALLE_COMPROBANTE_CNT.TERCERO  = TERCERO.NIT  
          AND DETALLE_COMPROBANTE_CNT.SUCURSAL = TERCERO.SUCURSAL 
        INNER JOIN PLAN_CONTABLE 
           ON DETALLE_COMPROBANTE_CNT.COMPANIA = PLAN_CONTABLE.COMPANIA  
          AND DETALLE_COMPROBANTE_CNT.ANO      = PLAN_CONTABLE.ANO 
          AND DETALLE_COMPROBANTE_CNT.CUENTA   = PLAN_CONTABLE.CODIGO
      WHERE DETALLE_COMPROBANTE_CNT.COMPANIA = UN_COMPANIA
        AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE    BETWEEN UN_TIPO_INICIAL AND UN_TIPO_FINAL  
        AND TRUNC(DETALLE_COMPROBANTE_CNT.FECHA) BETWEEN TO_DATE(UN_FECHA_INICIAL,'DD/MM/YYYY') AND TO_DATE(UN_FECHA_FINAL,'DD/MM/YYYY') 
        AND DETALLE_COMPROBANTE_CNT.VALOR_CREDITO NOT IN (0) 
      ORDER BY NOMBRECOLUMNA
    ) LOOP
        IF MI_RESULTADO IS NULL THEN 
          MI_RESULTADO :=  ' NVL("'                                                             || 
                           RS.NOMBRECOLUMNA                                                     || 
                           '", ''DEB CRE'') "'                                                  || 
                           SUBSTR(REPLACE(REPLACE(REPLACE(RS.NOMBRECOLUMNA,'.'),'('),')'),0,30) || 
                           '",';

        ELSE
          IF RS.NOMBRE_CUENTA IS NOT NULL THEN
            MI_RESULTADO := MI_RESULTADO                                                         ||
                            ' NVL("'                                                             || 
                            RS.NOMBRECOLUMNA                                                     || 
                            '", ''DEB CRE'') "'                                                  || 
                            SUBSTR(REPLACE(REPLACE(REPLACE(RS.NOMBRECOLUMNA,'.'),'('),')'),0,30) || 
                            '",';

          ELSIF RS.NOMBRE_CUENTA IS NULL THEN
            MI_RESULTADO := ' NVL("'                                                             || 
                            RS.NOMBRECOLUMNA                                                     || 
                            '", ''DEB CRE'') "'                                                  || 
                            SUBSTR(REPLACE(REPLACE(REPLACE(RS.NOMBRECOLUMNA,'.'),'('),')'),0,30) || 
                            '"';

          END IF;
        END IF;
    END LOOP;

    IF MI_RESULTADO IS NULL THEN 
      BEGIN
        MI_MSGERROR(1).CLAVE := 'COM1';
        MI_MSGERROR(1).VALOR := UN_TIPO_INICIAL;
        MI_MSGERROR(2).CLAVE := 'COM2';
        MI_MSGERROR(2).VALOR := UN_TIPO_FINAL;

        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(
           UN_EXC_COD    => SQLCODE
          ,UN_ERROR_COD  => PCK_ERRORES.ERR_C_NULL_PPNL_DETALLECOMPRO
          ,UN_REEMPLAZOS => MI_MSGERROR
        );
      END;
    END IF;

    MI_RESULTADO:= SUBSTR(MI_RESULTADO,1, LENGTH(MI_RESULTADO)-1);

  END;

  RETURN MI_RESULTADO;

END FC_PREPARARPIVOT_NVLLIBRO;

--

 FUNCTION FC_CONCATENACOMPROBANTES 
 /*
    NAME              : FC_CONCATENACOMPROBANTES 
    AUTHORS           : SYSMAN  SAS
    AUTHOR            : ADRIANA MARITZA CACERES BONILLA
    DATE              : 05/05/2016
    TIME              : 09:25 AM
    MODIFIER          : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Funcion que retorna la cadena concatenada de los valores 
                       no existentes en los consecutivos de contabilidad.  
                       Se modifico para que la funcion retorne valores y 
                       evitar que exceda el limite de caracteres. Se ajusta para
                       que los parametros fecha inicial y fecha final sean de 
                       tipo DATE.
    @NAME:  consultarConsecutivosFaltantes 
    @METHOD:  GET                    
  */
 (
    UN_TIPO           IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,  
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_FECHAINICIAL   IN DATE, 
    UN_FECHAFINAL     IN DATE 
 ) RETURN CLOB AS 
MI_RESULTADO CLOB:=NULL;
MI_ERROR_FUN PCK_SUBTIPOS.TI_ERROR_FUN:= GL_ERROR_NUM + 15;
BEGIN
  FOR RS IN
  (SELECT DISTINCT
    CASE WHEN TO_NUMBER(LAG(NUMERO) OVER (ORDER BY NUMERO)) NOT IN (0)
         THEN CASE WHEN (TO_NUMBER(LAG(NUMERO) OVER (ORDER BY NUMERO))+1) <> TO_NUMBER(NUMERO)
                   THEN CASE WHEN (TO_NUMBER(LAG(NUMERO) OVER (ORDER BY NUMERO))+1) <> (TO_NUMBER(NUMERO) -1)
                             THEN (TO_NUMBER(LAG(NUMERO) OVER (ORDER BY NUMERO))+1)||' - '
                                  || (TO_NUMBER(NUMERO) -1)||''
                             ELSE (TO_NUMBER(LAG(NUMERO) OVER (ORDER BY NUMERO))+1)||''
                             END 
                   END
    END FALTANTES,
    COMPANIA,
    TIPO
  FROM COMPROBANTE_CNT CN2
  WHERE CN2.COMPANIA = UN_COMPANIA
    AND CN2.TIPO     = UN_TIPO
    AND CN2.FECHA    BETWEEN UN_FECHAINICIAL AND UN_FECHAFINAL)
  LOOP
   IF MI_RESULTADO IS NULL THEN 
    MI_RESULTADO :=  RS.FALTANTES;
   ELSE
     IF RS.FALTANTES IS NOT NULL THEN
       MI_RESULTADO     :=MI_RESULTADO||','||RS.FALTANTES;
     ELSIF RS.FALTANTES IS NULL THEN
       MI_RESULTADO     :=MI_RESULTADO;
     END IF;
   END IF; 
  END LOOP;
  RETURN MI_RESULTADO;
  /*EXCEPTION WHEN OTHERS THEN
  PCK_DATOS.GL_ERROR_MSG:= 'Proceso interrumpido generando los consecutivos de los valores no existentes de contabilidad.';
  PCK_DATOS.GL_ERROR_MSG:= PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'COMPROBANTE_CNT','',SQLERRM );
  RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );  */
END FC_CONCATENACOMPROBANTES;


FUNCTION FC_REVISARAFECTACIONESCARTERA 
 /*
    NAME              : PR_REVISARAFECTACIONESCARTERA
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÍGUEZ
    DATE MIGRADOR     : 04/08/2016 
    TIME              : 11:44 AM     
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       : Proceso para realizar el cuadre de cartera partiendo de los historicos de recaudo
    MODIFICATIONS     : Incluir manejo de errores 
    @NAME:  revisarAfectacionesDeCartera 
    @METHOD:  GET                    
  */
  ( 
    UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO                      IN PCK_SUBTIPOS.TI_ANIO,
    UN_FECHA_CORTE              IN DATE

  )  
  RETURN CLOB
  AS
  MI_MES                      PCK_SUBTIPOS.TI_MES;
  MI_MERGEUSING               PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE              PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE              PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_SQL                      PCK_SUBTIPOS.TI_STRSQL;
  MI_CONDICION                PCK_SUBTIPOS.TI_CONDICION;
  MI_RTA                      PCK_SUBTIPOS.TI_RTA_ACME;
  MI_CAMPOS                   PCK_SUBTIPOS.TI_CAMPOS;
  MI_ABONOINICIAL             PCK_SUBTIPOS.TI_DOBLE;
  MI_VALORXAFECTAR            PCK_SUBTIPOS.TI_DOBLE;  
  MI_VALORAAFECTAR            PCK_SUBTIPOS.TI_DOBLE;  
  MI_ACUMULADO                PCK_SUBTIPOS.TI_DOBLE; 
  MI_TIPO_CPTE                PCK_SUBTIPOS.TI_TIPOCOMPROBANTE;  
  MI_COMPROBANTE              PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT;  
  MI_CONSECUTIVO              PCK_SUBTIPOS.TI_CONSECUTIVOCNT;  
  MI_SALDOAAFECTAR            PCK_SUBTIPOS.TI_DOBLE; 
  MI_ERROR_FUN                PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 1;
  RS                          SYS_REFCURSOR;
  RSDETALLE                   SYS_REFCURSOR;
  MI_AUX                      PCK_SUBTIPOS.TI_PARAMETRO;
  RS_SALDOCARTERA             PCK_SUBTIPOS.TI_DOBLE;
  RS_SALDOCONTABILIDAD        PCK_SUBTIPOS.TI_DOBLE;
  RS_CUENTA                   DETALLE_COMPROBANTE_CNT.CUENTA%TYPE;
  RS_DIFERENCIA               PCK_SUBTIPOS.TI_DOBLE;
  RS_NOMBRE                   VARCHAR2(1000);
BEGIN


  MI_RTA := ' ';
  MI_MES := TO_CHAR(UN_FECHA_CORTE, 'MM');
  /*  -*************************************** LINEAS COMENTADAS YA QUE EL PROCESO SE EJECUTA SOLAMENTE PARA YOPAL, POR ENDE LA TABLA HISTORICO_RECAUDO NO EXISTE 
    FOR RS IN (
          SELECT  VALORRECAUDO, 
                  VALORFACTURA,                  
                  VALORDESCUENTO,
                  TIPOFACTURA,
                  NUMEROFACTURA
           FROM HISTORICO_RECAUDO
           WHERE COMPANIA = UN_COMPANIA 
             AND ANO = UN_ANO
           ORDER BY NUMERORECAUDO
      ) LOOP
        IF RS.VALORRECAUDO = (RS.VALORFACTURA + RS.VALORDESCUENTO) THEN
          MI_MERGEUSING := 'SELECT DETALLE_COMPROBANTE_CNT.COMPANIA,
                                   DETALLE_COMPROBANTE_CNT.ANO,
                                   DETALLE_COMPROBANTE_CNT.TIPO_CPTE,
                                   DETALLE_COMPROBANTE_CNT.COMPROBANTE,
                                   DETALLE_COMPROBANTE_CNT.CONSECUTIVO,
                                   DETALLE_COMPROBANTE_CNT.VALOR_DEBITO NUE_DEBITO,
                                   DETALLE_COMPROBANTE_CNT.VALOR_CREDITO NUE_CREDITO,
                                   0 NUE_ABONO
                             FROM DETALLE_COMPROBANTE_CNT
                             LEFT JOIN V_PLAN_CONTABLE PLAN_CONTABLE
                               ON DETALLE_COMPROBANTE_CNT.COMPANIA        = PLAN_CONTABLE.COMPANIA
                               AND DETALLE_COMPROBANTE_CNT.ANO            = PLAN_CONTABLE.ANO
                               AND DETALLE_COMPROBANTE_CNT.CUENTA         = PLAN_CONTABLE.ID

                             WHERE DETALLE_COMPROBANTE_CNT.COMPANIA      ='''|| UN_COMPANIA ||'''
                               AND DETALLE_COMPROBANTE_CNT.ANO           = ' || UN_ANO || '
                               AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE     =''' || RS.TIPOFACTURA || '''
                               AND DETALLE_COMPROBANTE_CNT.COMPROBANTE   = ' || RS.NUMEROFACTURA || '
                               AND PLAN_CONTABLE.CLASECUENTA             =''C''';
          MI_MERGEENLACE := 'TABLA.COMPANIA     = VISTA.COMPANIA
                         AND TABLA.ANO           = VISTA.ANO
                         AND TABLA.TIPO_CPTE     = VISTA.TIPO_CPTE
                         AND TABLA.COMPROBANTE   = VISTA.COMPROBANTE
                         AND TABLA.CONSECUTIVO   = VISTA.CONSECUTIVO';

          MI_MERGEEXISTE := 'UPDATE SET DEBITO_AFECTADO = VISTA.NUE_DEBITO,
                             CREDITO_AFECTADO  = VISTA.NUE_CREDITO,
                             ABONOINICIAL      = NUE_ABONO';


          PCK_DATOS.GL_RTA = PCK_DATOS.FC_ACME('DETALLE_COMPROBANTE_CNT','MM',NULL,NULL,NULL,NULL,MI_MERGEUSING,MI_MERGEENLACE,MI_MERGEEXISTE,NULL); 

        ELSE IF RS.VALORFACTURA <> 0 THEN
                MI_MERGEUSING := 'SELECT DETALLE_COMPROBANTE_CNT.COMPANIA,
                                  DETALLE_COMPROBANTE_CNT.ANO,
                                  DETALLE_COMPROBANTE_CNT.TIPO_CPTE,
                                  DETALLE_COMPROBANTE_CNT.COMPROBANTE,
                                  DETALLE_COMPROBANTE_CNT.CONSECUTIVO,
                               CASE WHEN ('|| NVL(RS.VALORFACTURA,0) ||' >= DETALLE_COMPROBANTE_CNT.VALOR_DEBITO AND ('|| NVL(RS.VALORFACTURA,0) ||' <> 0)) 
                                   THEN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO
                                   ELSE CASE WHEN '|| NVL(RS.VALORFACTURA,0) ||' <> 0 THEN
                                   '|| NVL(RS.VALORFACTURA,0) ||'
                                   END END NUE_ABONO,
                               CASE WHEN ('|| NVL(RS.VALORFACTURA,0) ||' >= DETALLE_COMPROBANTE_CNT.VALOR_DEBITO AND ('|| NVL(RS.VALORFACTURA,0) ||' <> 0)) 
                                   THEN 0
                                   ELSE CASE WHEN ('|| NVL(RS.VALORFACTURA,0) ||' <> 0 ) THEN
                                         0
                                         ELSE DETALLE_COMPROBANTE_CNT.ABONOINICIAL
                                         END 
                               END NUE_DEBITO
                             FROM DETALLE_COMPROBANTE_CNT
                             INNER JOIN V_PLAN_CONTABLE PLAN_CONTABLE
                             ON DETALLE_COMPROBANTE_CNT.COMPANIA   = PLAN_CONTABLE.COMPANIA
                               AND DETALLE_COMPROBANTE_CNT.ANO        = PLAN_CONTABLE.ANO
                               AND  DETALLE_COMPROBANTE_CNT.CUENTA      = PLAN_CONTABLE.ID                            
                             WHERE DETALLE_COMPROBANTE_CNT.COMPANIA      ='''|| UN_COMPANIA ||'''
                               AND DETALLE_COMPROBANTE_CNT.ANO           = ' || UN_ANO || '
                               AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE     =''' || RS.TIPOFACTURA || '''
                               AND DETALLE_COMPROBANTE_CNT.COMPROBANTE   = ' || RS.NUMEROFACTURA || '
                               AND PLAN_CONTABLE.CLASECUENTA             =''C'' 
                               AND DETALLE_COMPROBANTE_CNT.ABONOINICIAL=0 ';
                MI_MERGEENLACE := 'TABLA.COMPANIA     = VISTA.COMPANIA
                               AND TABLA.ANO           = VISTA.ANO
                               AND TABLA.TIPO_CPTE     = VISTA.TIPO_CPTE
                               AND TABLA.COMPROBANTE   = VISTA.COMPROBANTE
                               AND TABLA.CONSECUTIVO   = VISTA.CONSECUTIVO';

                MI_MERGEEXISTE := 'UPDATE SET DEBITO_AFECTADO   = VISTA.NUE_DEBITO,                           
                                              ABONOINICIAL      = NUE_ABONO';


          PCK_DATOS.GL_RTA = PCK_DATOS.FC_ACME('DETALLE_COMPROBANTE_CNT','MM',NULL,NULL,NULL,NULL,MI_MERGEUSING,MI_MERGEENLACE,MI_MERGEEXISTE,NULL); 
              END IF;   
        END IF;

    END LOOP;

*/

MI_SQL := 'SELECT TMP_DESCUADRE.SALDO        AS SALDOCARTERA,
                   PLAN_CONTABLE.SALDO' || MI_MES || '       AS SALDOCONTABILIDAD,
                  TMP_DESCUADRE.CUENTA,
                  SALDO-PLAN_CONTABLE.SALDO' || MI_MES || ' AS DIFERENCIA,
                  TMP_DESCUADRE.NOMBRE   
                FROM (SELECT  '''|| UN_COMPANIA ||''' COMPANIA,
                              ' || UN_ANO || ' ANO,
                              DETALLE_COMPROBANTE_CNT.CUENTA,
                              SUM(VALOR_DEBITO   +VALOR_CREDITO) TOTALDEUDA,
                              SUM(((VALOR_DEBITO +VALOR_CREDITO)-(DEBITO_AFECTADO+CREDITO_AFECTADO))-ABONOINICIAL) SALDO,
                              DETALLE_COMPROBANTE_CNT.TERCERO,
                              DETALLE_COMPROBANTE_CNT.SUCURSAL,
                              TERCERO.NOMBRE
                            FROM ((DETALLE_COMPROBANTE_CNT
                            INNER JOIN TERCERO
                            ON DETALLE_COMPROBANTE_CNT.COMPANIA  = TERCERO.COMPANIA
                            AND DETALLE_COMPROBANTE_CNT.TERCERO  = TERCERO.NIT
                            AND DETALLE_COMPROBANTE_CNT.SUCURSAL = TERCERO.SUCURSAL)
                            INNER JOIN TIPO_COMPROBANTE
                            ON DETALLE_COMPROBANTE_CNT.COMPANIA   = TIPO_COMPROBANTE.COMPANIA
                            AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = TIPO_COMPROBANTE.CODIGO)
                            INNER JOIN V_PLAN_CONTABLE PLAN_CONTABLE
                            ON DETALLE_COMPROBANTE_CNT.COMPANIA    = PLAN_CONTABLE.COMPANIA
                            AND DETALLE_COMPROBANTE_CNT.ANO        = PLAN_CONTABLE.ANO
                            AND DETALLE_COMPROBANTE_CNT.CUENTA     = PLAN_CONTABLE.ID
                            WHERE DETALLE_COMPROBANTE_CNT.COMPANIA = ''' || UN_COMPANIA || '''
                            AND DETALLE_COMPROBANTE_CNT.ANO       <= ' || UN_ANO || '
                            AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE IN
                                                                    (SELECT CODIGO
                                                                      FROM TIPO_COMPROBANTE
                                                                     WHERE COMPANIA     IN (''' || UN_COMPANIA || ''') 
                                                                       AND CLASE_CONTABLE IN (''V'')
                                          )
  AND DETALLE_COMPROBANTE_CNT.FECHA     <= TO_DATE(''' || TO_CHAR(UN_FECHA_CORTE,'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
  --AND PLAN_CONTABLE.CLASECUENTA =''C''
  GROUP BY DETALLE_COMPROBANTE_CNT.TERCERO,DETALLE_COMPROBANTE_CNT.SUCURSAL,TERCERO.NOMBRE,DETALLE_COMPROBANTE_CNT.CUENTA)TMP_DESCUADRE
                INNER JOIN V_PLAN_CONTABLE PLAN_CONTABLE
                ON (TMP_DESCUADRE.CUENTA     = PLAN_CONTABLE.ID)
                AND (TMP_DESCUADRE.ANO       = PLAN_CONTABLE.ANO)
                AND (TMP_DESCUADRE.COMPANIA  = PLAN_CONTABLE.COMPANIA)
                AND (TMP_DESCUADRE.TERCERO   = PLAN_CONTABLE.TERCERO)
                AND (TMP_DESCUADRE.SUCURSAL  = PLAN_CONTABLE.SUCURSAL)
                WHERE TMP_DESCUADRE.SALDO   <>PLAN_CONTABLE.SALDO' || MI_MES || '
                AND PLAN_CONTABLE.ANO        = '|| UN_ANO ||'
                AND PLAN_CONTABLE.BLOQUEACUENTA IN (''NO'')
                --AND PLAN_CONTABLE.CLASECUENTA=''C''
                ORDER BY TMP_DESCUADRE.NOMBRE,
                  TMP_DESCUADRE.CUENTA';

OPEN RS FOR MI_SQL;
  LOOP
    FETCH RS INTO RS_SALDOCARTERA, RS_SALDOCONTABILIDAD,RS_CUENTA ,RS_DIFERENCIA, RS_NOMBRE;
      EXIT WHEN RS%NOTFOUND;



    IF RS_SALDOCARTERA >= RS_SALDOCONTABILIDAD THEN
       MI_SQL := 'SELECT   DETALLE_COMPROBANTE_CNT.TIPO_CPTE,
                        DETALLE_COMPROBANTE_CNT.COMPROBANTE,
                        DETALLE_COMPROBANTE_CNT.CONSECUTIVO,
                        DETALLE_COMPROBANTE_CNT.ABONOINICIAL,
                        CASE WHEN DETALLE_COMPROBANTE_CNT.NATURALEZA = ''D'' THEN
                          (DETALLE_COMPROBANTE_CNT.VALOR_DEBITO-DETALLE_COMPROBANTE_CNT.VALOR_CREDITO)-(DETALLE_COMPROBANTE_CNT.DEBITO_AFECTADO-DETALLE_COMPROBANTE_CNT.CREDITO_AFECTADO)-DETALLE_COMPROBANTE_CNT.ABONOINICIAL ELSE
                          (DETALLE_COMPROBANTE_CNT.VALOR_CREDITO-DETALLE_COMPROBANTE_CNT.VALOR_DEBITO)-(DETALLE_COMPROBANTE_CNT.CREDITO_AFECTADO-DETALLE_COMPROBANTE_CNT.DEBITO_AFECTADO)-DETALLE_COMPROBANTE_CNT.ABONOINICIAL END SALDOAFECTAR
                      FROM DETALLE_COMPROBANTE_CNT 
                      WHERE DETALLE_COMPROBANTE_CNT.COMPANIA = ''' || UN_COMPANIA || '''
                      AND DETALLE_COMPROBANTE_CNT.ANO        = ' || UN_ANO || '
                      AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE                         IN (SELECT CODIGO
                                                                  FROM TIPO_COMPROBANTE
                                                                 WHERE COMPANIA     IN (''' || UN_COMPANIA || ''') 
                                                                   AND CLASE_CONTABLE IN (''V''))
                       AND DETALLE_COMPROBANTE_CNT.CUENTA     = ''' || RS_CUENTA || '''                    
                      AND CASE WHEN DETALLE_COMPROBANTE_CNT.NATURALEZA = ''D'' THEN
                                (DETALLE_COMPROBANTE_CNT.VALOR_DEBITO-DETALLE_COMPROBANTE_CNT.VALOR_CREDITO)-(DETALLE_COMPROBANTE_CNT.DEBITO_AFECTADO-DETALLE_COMPROBANTE_CNT.CREDITO_AFECTADO)-DETALLE_COMPROBANTE_CNT.ABONOINICIAL ELSE
                                (DETALLE_COMPROBANTE_CNT.VALOR_CREDITO-DETALLE_COMPROBANTE_CNT.VALOR_DEBITO)-(DETALLE_COMPROBANTE_CNT.CREDITO_AFECTADO-DETALLE_COMPROBANTE_CNT.DEBITO_AFECTADO)-DETALLE_COMPROBANTE_CNT.ABONOINICIAL END <> 0 
                      ORDER BY DETALLE_COMPROBANTE_CNT.COMPROBANTE';
      MI_VALORXAFECTAR := ABS(RS_DIFERENCIA);
      MI_ACUMULADO := 0;
      MI_VALORAAFECTAR := 0;  

      OPEN RSDETALLE FOR MI_SQL;
        LOOP
          FETCH RSDETALLE INTO MI_TIPO_CPTE , MI_COMPROBANTE,MI_CONSECUTIVO,MI_ABONOINICIAL , MI_SALDOAAFECTAR;
          EXIT WHEN RSDETALLE%NOTFOUND OR MI_ACUMULADO >= RS_DIFERENCIA;
            IF MI_SALDOAAFECTAR > 0 THEN
              IF MI_SALDOAAFECTAR <= MI_VALORXAFECTAR THEN
                MI_VALORAAFECTAR := MI_SALDOAAFECTAR;
              ELSE 
                MI_VALORAAFECTAR := MI_VALORXAFECTAR;
              END IF;

              MI_ACUMULADO := MI_ACUMULADO + MI_VALORAAFECTAR;
              MI_VALORXAFECTAR := RS_DIFERENCIA - MI_ACUMULADO;
              MI_CAMPOS:='ABONOINICIAL = '|| TO_CHAR(MI_ABONOINICIAL - MI_VALORAAFECTAR);

              MI_CONDICION:='COMPANIA           = '''|| UN_COMPANIA ||''' 
                               AND ANO          = '|| UN_ANO ||' 
                               AND TIPO_CPTE    = '''|| MI_TIPO_CPTE ||'''
                               AND COMPROBANTE  = ' || MI_COMPROBANTE || '
                               AND CONSECUTIVO  = '|| MI_CONSECUTIVO;
              MI_AUX := PCK_DATOS.FC_ACME('DETALLE_COMPROBANTE_CNT', 'M', MI_CAMPOS, NULL, NULL, MI_CONDICION);
            END IF;  

        END LOOP;
      CLOSE RSDETALLE;                 

    ELSE
      MI_SQL := 'SELECT DETALLE_COMPROBANTE_CNT.ABONOINICIAL,
                        DETALLE_COMPROBANTE_CNT.TIPO_CPTE,
                        DETALLE_COMPROBANTE_CNT.COMPROBANTE,
                        DETALLE_COMPROBANTE_CNT.CONSECUTIVO
                   FROM DETALLE_COMPROBANTE_CNT
                      WHERE DETALLE_COMPROBANTE_CNT.COMPANIA = ''' || UN_COMPANIA || '''
                      AND DETALLE_COMPROBANTE_CNT.ANO        = ' || UN_ANO || '
                      AND TIPO_CPTE                         IN (SELECT CODIGO
                                                                  FROM TIPO_COMPROBANTE
                                                                 WHERE COMPANIA     IN (''' || UN_COMPANIA || ''') 
                                                                   AND CLASE_CONTABLE IN (''V''))
                      AND DETALLE_COMPROBANTE_CNT.CUENTA     = ''' || RS_CUENTA || '''
                      AND ABONOINICIAL <> 0 
                      ORDER BY DETALLE_COMPROBANTE_CNT.COMPROBANTE';
      MI_VALORXAFECTAR := ABS(RS_DIFERENCIA);
      MI_ACUMULADO := 0;
      MI_VALORAAFECTAR := 0;                

      OPEN RSDETALLE FOR MI_SQL;
        LOOP
          FETCH RSDETALLE INTO MI_ABONOINICIAL , MI_TIPO_CPTE , MI_COMPROBANTE, MI_CONSECUTIVO;
          EXIT WHEN RSDETALLE%NOTFOUND OR MI_VALORXAFECTAR = 0;
            IF MI_VALORXAFECTAR <= MI_ABONOINICIAL THEN
              MI_VALORAAFECTAR := MI_VALORXAFECTAR;
            ELSE 
              MI_VALORAAFECTAR := MI_ABONOINICIAL;
            END IF;

            MI_ACUMULADO := MI_ACUMULADO + 1;
            MI_VALORXAFECTAR := MI_VALORXAFECTAR - MI_VALORAAFECTAR;
            MI_CAMPOS:='ABONOINICIAL = '|| TO_CHAR(MI_ABONOINICIAL - MI_VALORAAFECTAR);

            MI_CONDICION:='COMPANIA           = '''|| UN_COMPANIA ||''' 
                             AND ANO          = '|| UN_ANO ||' 
                             AND TIPO_CPTE    = '''|| MI_TIPO_CPTE ||'''
                             AND COMPROBANTE  = ' || MI_COMPROBANTE || ' 
                             AND CONSECUTIVO  = '|| MI_CONSECUTIVO;
            MI_AUX:=PCK_DATOS.FC_ACME('DETALLE_COMPROBANTE_CNT', 'M', MI_CAMPOS, NULL, NULL, MI_CONDICION);


        END LOOP;

        IF MI_ACUMULADO = 0 THEN 
          MI_RTA := MI_RTA ||' No existe registros para afectar en SRV... ' || CHR(13)  || RS_NOMBRE || ' Veririfcar manualmente el tercero...'|| CHR(10) ||  CHR(13);
        END IF;
      CLOSE RSDETALLE;




    END IF;
  END LOOP;
CLOSE RS;    

    MI_RTA := MI_RTA || ' Proceso de revisión de afectaciones de cartera terminado...';
    RETURN MI_RTA;
    --EXCEPTION WHEN OTHERS THEN
    --PCK_DATOS.GL_ERROR_MSG := ' ' ;
    --PCK_DATOS.GL_ERROR_RTA := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'PROCESO','',SQLERRM );
    --RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_RTA || CHR(10) || PCK_DATOS.GL_ERROR_MSG );
END FC_REVISARAFECTACIONESCARTERA;


FUNCTION FC_AFECTARCOMPROBANTEPPTAL
 /*
    NAME              : FC_AFECTARCOMPROBANTEPPTAL
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÍGUEZ
    DATE MIGRADOR     : 06/09/2016 
    TIME              : 11:44 AM     
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       : Desde un registro de comprobantes contables, 
                        y habiendo iniciado la generación de un comprobante presupuestal, 
                        en el momento de haber creado el header, 
                        realiza la afectación del comprobante actual.
    MODIFICATIONS     : 
    @NAME:  afectarComprobantePresupuestal
    @METHOD:  GET                    
  */
(
	UN_COMPANIA  		  IN PCK_SUBTIPOS.TI_COMPANIA, 
	UN_TIPOAFEC  		  IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL, 
	UN_NUMEROAFEC  		IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTEPPTAL, 
	UN_ANOAFEC  		  IN PCK_SUBTIPOS.TI_ANIO, 
	UN_FECHA  			  IN DATE, 
	UN_TIPO  			    IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE, 
	UN_NUMERO  			  IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT, 
	UN_CLASE  			  IN PCK_SUBTIPOS.TI_CLASECUENTACONTA,
  UN_ANO            IN PCK_SUBTIPOS.TI_ANIO,       
  UN_PACPROPORCIONALGIRO IN VARCHAR2 --NTIFICACION!PACProporcionalGiro En la versión de acces se hacía referencia a esta función, debe pasarse por parametro
)RETURN VARCHAR2
AS
	MI_INTESTRUCTURA    PCK_SUBTIPOS.TI_ENTERO;
	MI_VALORANTERIOR 	  PCK_SUBTIPOS.TI_DOBLE;
	MI_VALORG 			    PCK_SUBTIPOS.TI_DOBLE;
	MI_SQL 				      PCK_SUBTIPOS.TI_STRSQL;
	MI_STRCENTRO 		    VARCHAR2(100 CHAR);
	MI_STRAUXILIAR 		  VARCHAR2(100 CHAR);
	MI_MODIFPAC 		    PCK_SUBTIPOS.TI_DOBLE;
	MI_TOTALPAC			    PCK_SUBTIPOS.TI_DOBLE;
	MI_EJECUCIONPPT 	  PCK_SUBTIPOS.TI_DOBLE;
	MI_PACEJECUTADO 	  PCK_SUBTIPOS.TI_DOBLE;
	MI_RTA 				      VARCHAR2(4000 CHAR);
	MI_COLUMNA 			    CLASECNTPRES.COLUMNA%TYPE;
	MI_AFECTACION 		  CLASECNTPRES.AFECTACION%TYPE;
	MI_CODIGO 			    CLASECNTPRES.CODIGO%TYPE;
	MI_COMPANIA 		    PCK_SUBTIPOS.TI_COMPANIA;
  MI_RS               SYS_REFCURSOR;
  MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
  MI_PCKDATOS         PCK_SUBTIPOS.TI_RTA_ACME;
	BEGIN 

MI_INTESTRUCTURA := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA, 'MAXIMO DIGITOS ESTRUCTURA PRESUPUESTAL' , PCK_DATOS.MODULOPRESUPUESTO , SYSDATE),16));

IF UN_NUMEROAFEC IS NOT NULL AND UN_NUMEROAFEC <> 0 THEN 
	FOR RSDETALLE IN (SELECT  COMPANIA, 
							  VALOR_DEBITO,
							  VALOR_CREDITO,
							  DEBITO_AFECTADO,
							  CREDITO_AFECTADO,
							  MODIFICACION_DEBITO,
							  MODIFICACION_CREDITO,
							  TIPO_CPTE_AFECT,
							  CMPTE_AFECTADO,
							  CONSECUTIVOPPTO,
							  NATURALEZA,
							  AUXILIAR,
							  CENTRO_COSTO,
							  CONSECUTIVO,
							  CUENTA,
							  DESCRIPCION,
							  TIPO_DOCUMENTO,
							  NRO_DOCUMENTO,
							  TERCERO,
							  SUCURSAL
							 -- MONEDA
							FROM DETALLE_COMPROBANTE_PPTAL
							WHERE COMPANIA                                             = UN_COMPANIA
							  AND ANO                                                  = UN_ANOAFEC
							  AND TIPO_CPTE                                            = UN_TIPOAFEC
							  AND COMPROBANTE                                          = UN_NUMEROAFEC
							  AND TO_DATE(TO_CHAR(DETALLE_COMPROBANTE_PPTAL.FECHA,'DD/MM/YYYY'),'DD/MM/YYYY') <= UN_FECHA
							ORDER BY 
							  CONSECUTIVO) LOOP
		MI_COMPANIA := RSDETALLE.COMPANIA;
		PCK_ENTORNO.PR_SETCOMPANIA(UN_COMPANIA);
		PCK_ENTORNO.PR_SETYEAR(TO_CHAR(SYSDATE,'YYYY'));
		PCK_ENTORNO.PR_SETMESINICIAL(0);
		PCK_ENTORNO.PR_SETMESFINAL(13);
		PCK_ENTORNO.PR_SETRUBRO(RSDETALLE.CUENTA);

		FOR RS IN (				
				SELECT	APROPIADO,
						ADICION,
						REDUCCION,
						TRASLADO,
						APLAZAMIENTO,
						DISPONIBILIDAD,
						PACTESORERIA,
						EJECUCIONCNT,
						PACTOTAL TOTALPACAPROPIADO,
					 	PAC_COMPROMETIDO,
						PAC_PROGRAMADO,
						PACEJECUTADO
				  FROM 	V_RESUMENPPTO_P
			)LOOP
      BEGIN
			SELECT (NVL(VALOR_DEBITO, 0) - NVL(VALOR_CREDITO, 0)) - (NVL(DEBITO_AFECTADO, 0) - NVL(CREDITO_AFECTADO, 0)) + (NVL(MODIFICACION_DEBITO, 0) - NVL(MODIFICACION_CREDITO, 0))
				 INTO MI_VALORANTERIOR
				 FROM DETALLE_COMPROBANTE_PPTAL 
				WHERE COMPANIA= UN_COMPANIA   
				  AND ANO=  UN_ANOAFEC
				  AND TIPO_CPTE=  RSDETALLE.TIPO_CPTE_AFECT 
				  AND COMPROBANTE =  RSDETALLE.CMPTE_AFECTADO    
				  AND CONSECUTIVO =  RSDETALLE.CONSECUTIVOPPTO   
				  AND CUENTA = RSDETALLE.CUENTA;

      EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_VALORANTERIOR := 0;
      END;

			IF UN_TIPOAFEC = 'DIS' AND  UN_TIPO = 'ADD' THEN
				MI_VALORG := 0;
			ELSIF UN_TIPOAFEC = 'RES' AND  UN_TIPO = 'ADR' THEN
				MI_VALORG := MI_VALORANTERIOR;
			ELSIF UN_TIPOAFEC = 'REO' AND  UN_TIPO = 'ARO' THEN
				MI_VALORG := MI_VALORANTERIOR;
			ELSIF (UN_TIPOAFEC = 'ING' OR  UN_TIPOAFEC = 'ICA') AND (UN_TIPO = 'AIN' AND UN_TIPO = 'AIC') THEN
				MI_VALORG := 0;
			ELSIF UN_TIPOAFEC = 'EGR' AND  UN_TIPO = 'AEG' THEN
				MI_VALORG := MI_VALORANTERIOR;
			ELSE 
				IF NVL(RSDETALLE.NATURALEZA,'D') = 'D' THEN
					MI_VALORG := (NVL(RSDETALLE.VALOR_DEBITO, 0) - NVL(RSDETALLE.VALOR_CREDITO, 0)) - (NVL(RSDETALLE.DEBITO_AFECTADO, 0) - NVL(RSDETALLE.CREDITO_AFECTADO, 0)) + (NVL(RSDETALLE.MODIFICACION_DEBITO, 0) - NVL(RSDETALLE.MODIFICACION_CREDITO, 0));
				ELSE
					MI_VALORG := (NVL(RSDETALLE.VALOR_CREDITO, 0) - NVL(RSDETALLE.VALOR_DEBITO, 0)) - (NVL(RSDETALLE.CREDITO_AFECTADO, 0) - NVL(RSDETALLE.DEBITO_AFECTADO, 0)) + (NVL(RSDETALLE.MODIFICACION_CREDITO, 0) - NVL(RSDETALLE.MODIFICACION_DEBITO, 0));
				END IF;

			END IF;

			IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA, 'OBLIGA CONTROLAR PAC EN EGRESO' , PCK_DATOS.MODULOCONTABILIDAD , SYSDATE),'NO') = 'SI' THEN
				IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA, 'CONTROLAR EGRESO CONTRA PAC DE TESORERIA' , PCK_DATOS.MODULOCONTABILIDAD , SYSDATE),'NO') = 'SI' THEN
					IF UN_TIPO = 'EGR' OR UN_TIPOAFEC = 'AEG' THEN
						IF (RS.PACTESORERIA - RS.EJECUCIONCNT) < MI_VALORG THEN
							MI_RTA := 'No hay PAC de tesorería disponible para realizar este egreso presupuestal.<br> PAC de tesorería disponible: ' || TO_CHAR(RS.PACTESORERIA - RS.EJECUCIONCNT,'999,999,999,999.00');
							-- RETURN MI_RTA;

						END IF;
					END IF;
				ELSE
          IF UN_TIPO = 'EGR' OR UN_TIPO = 'AEG' THEN
            MI_SQL := 'SELECT SUM(
									  CASE
									    WHEN PLAN_PRESUPUESTAL.NATURALEZA=''D''
									    THEN MODIF_PAC_DEBITO -MODIF_PAC_CREDITO
									    ELSE MODIF_PAC_CREDITO-MODIF_PAC_DEBITO END) MODIFPAC,
									  --SUM(PAC_APROPIADO) PACAPROPIADO,
									  SUM(PAC_APROPIADO) + SUM(
									  CASE
									    WHEN PLAN_PRESUPUESTAL.NATURALEZA=''D''
									    THEN MODIF_PAC_DEBITO -MODIF_PAC_CREDITO
									    ELSE MODIF_PAC_CREDITO-MODIF_PAC_DEBITO END) TOTALPAC,
									  SUM(
									  CASE
									    WHEN PLAN_PRESUPUESTAL.NATURALEZA=''D''
									    THEN EJE_PPT_DEBITO -EJE_PPT_CREDITO
									    ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO END ) EJECUCIONPPT
									    FROM V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL
									    INNER JOIN V_SALDO_PLAN_PPTAL SALDO_PLAN_PPTAL
									       ON PLAN_PRESUPUESTAL.COMPANIA       = SALDO_PLAN_PPTAL.COMPANIA
									      AND PLAN_PRESUPUESTAL.ANO            = SALDO_PLAN_PPTAL.ANO
									      AND PLAN_PRESUPUESTAL.ID              = SALDO_PLAN_PPTAL.ID
									    WHERE PLAN_PRESUPUESTAL.COMPANIA     = '''|| UN_COMPANIA ||'''
									      AND PLAN_PRESUPUESTAL.ANO            = ' || UN_ANO || '
									      AND LENGTH(PLAN_PRESUPUESTAL.ID)<= LENGTH(TRIM(SUBSTR('''|| RSDETALLE.CUENTA ||''',0,'|| MI_INTESTRUCTURA ||')))
									      AND PLAN_PRESUPUESTAL.ID         = SUBSTR(TRIM(SUBSTR('''|| RSDETALLE.CUENTA ||''',0,'|| MI_INTESTRUCTURA ||')),0, LENGTH(PLAN_PRESUPUESTAL.ID))
									      AND PLAN_PRESUPUESTAL.TERCERO       IS NULL
									      AND SALDO_PLAN_PPTAL.MES     <= ' || EXTRACT (MONTH FROM UN_FECHA) || '	-- FORMS!COMPROBANTE_PPTAL!FECHA PENDIENTE POR SABER DE DONDE VIENE LA FECHA
									      AND MAN_PAC                         <> 0 ';
            IF RSDETALLE.CENTRO_COSTO IS NULL THEN
              MI_STRCENTRO := ' IS NULL ';
            ELSE
              MI_STRCENTRO := ' = ''' || RSDETALLE.CENTRO_COSTO || ''' ';
            END IF;
            IF RSDETALLE.AUXILIAR IS NULL THEN
              MI_STRAUXILIAR := ' IS NULL ';
            ELSE
              MI_STRAUXILIAR := ' = ''' || RSDETALLE.AUXILIAR || ''' ';
            END IF;         

            MI_SQL := MI_SQL || 'AND ((PLAN_PRESUPUESTAL.CENTRO_COSTO '|| MI_STRCENTRO || ' AND PLAN_PRESUPUESTAL.AUXILIAR IS NULL)  
                        OR (PLAN_PRESUPUESTAL.CENTRO_COSTO '|| MI_STRCENTRO || '  AND PLAN_PRESUPUESTAL.AUXILIAR ' || MI_STRAUXILIAR || ')  
                        OR (PLAN_PRESUPUESTAL.CENTRO_COSTO IS NULL AND PLAN_PRESUPUESTAL.AUXILIAR ' || MI_STRAUXILIAR || ' )  
                        OR (PLAN_PRESUPUESTAL.CENTRO_COSTO IS NULL AND PLAN_PRESUPUESTAL.AUXILIAR IS NULL)  
                        OR (PLAN_PRESUPUESTAL.CENTRO_COSTO IS NULL AND PLAN_PRESUPUESTAL.AUXILIAR IS NOT NULL AND PLAN_PRESUPUESTAL.AUXILIAR= SUBSTR(''' || NVL( RSDETALLE.AUXILIAR,'' ) || ''',LENGTH(PLAN_PRESUPUESTAL.AUXILIAR))))
                        GROUP BY SALDO_PLAN_PPTAL.COMPANIA, SALDO_PLAN_PPTAL.ANO, SALDO_PLAN_PPTAL.ID, PLAN_PRESUPUESTAL.NATURALEZA';

            OPEN MI_RS FOR MI_SQL;
              LOOP
                FETCH MI_RS INTO MI_MODIFPAC, MI_TOTALPAC, MI_EJECUCIONPPT;
                EXIT WHEN MI_RS%NOTFOUND;
                IF MI_MODIFPAC IS NOT NULL THEN
                  IF (MI_TOTALPAC - MI_EJECUCIONPPT) < MI_VALORG THEN
                    MI_RTA := 'No hay PAC disponible para realizar este egreso presupuestal. <BR> PAC disponible: ' || TO_CHAR(MI_TOTALPAC - MI_EJECUCIONPPT,'999,999,999,999.00');
                    RETURN MI_RTA;
                  END IF;
                END IF;
              END LOOP;
            CLOSE MI_RS;
          END IF;
				END IF;
			END IF;
      IF UN_TIPO = 'REO' OR UN_TIPO = 'ARO' THEN
				IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA, 'CONTROLAR PAC COMPROMETIDO CONTRA PAC APROPIADO' , PCK_DATOS.MODULOCONTABILIDAD , SYSDATE),'NO') = 'SI' THEN
					IF (RS.TOTALPACAPROPIADO - RS.PAC_COMPROMETIDO) < MI_VALORG THEN
						MI_RTA := 'No hay PAC apropiado disponible para realizar este pac comprometido.<BR> PAC disponible: ' || TO_CHAR(RS.TOTALPACAPROPIADO - RS.PAC_COMPROMETIDO,'999,999,999,999.00');
						RETURN MI_RTA;
					END IF;
				END IF;
			END IF;
			IF UN_TIPO = 'REO' OR UN_TIPO = 'ARO' THEN
				IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA, 'CONTROLAR PAC COMPROMETIDO CONTRA PAC PROGRAMADO' , PCK_DATOS.MODULOCONTABILIDAD , SYSDATE),'NO') = 'SI' THEN
					IF (RS.PAC_PROGRAMADO - RS.PAC_COMPROMETIDO) < MI_VALORG THEN
						MI_RTA := 'No hay PAC programado disponible para realizar este pac ejecutado. <BR> PAC programado disponible: ' || TO_CHAR(RS.PAC_PROGRAMADO - RS.PAC_COMPROMETIDO,'999,999,999,999.00');
						RETURN MI_RTA;
					END IF;
				END IF;
			END IF;

			IF UN_PACPROPORCIONALGIRO <> 'SI' THEN
				IF UN_TIPO = 'EGR' OR UN_TIPO = 'AEG' THEN
					IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA, 'CONTROLAR PAC EJECUTADO CONTRA PAC COMPROMETIDO' , PCK_DATOS.MODULOCONTABILIDAD , SYSDATE),'NO') = 'SI' THEN
						IF (RS.PAC_COMPROMETIDO - RS.PACEJECUTADO) < MI_VALORG THEN
							MI_RTA := 'No hay PAC comprometido disponible para realizar esta ejecución de pac. <BR> PAC comprometido disponible: ' || TO_CHAR(RS.PAC_COMPROMETIDO - RS.PACEJECUTADO,'999,999,999,999.00');
							RETURN MI_RTA;
						END IF;
					END IF;

					IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA, 'CONTROLAR PAC EJECUTADO CONTRA PAC APROPIADO' , PCK_DATOS.MODULOCONTABILIDAD , SYSDATE),'NO') = 'SI' THEN
						MI_SQL := 'SELECT SUM(
									  CASE
									    WHEN PLAN_PRESUPUESTAL.NATURALEZA=''D''
									    THEN MODIF_PAC_DEBITO -MODIF_PAC_CREDITO
									    ELSE MODIF_PAC_CREDITO-MODIF_PAC_DEBITO END) MODIFPAC,
									  -- SUM(PAC_APROPIADO) PACAPROPIADO,
									  SUM(PAC_APROPIADO) + SUM(
									  CASE
									    WHEN PLAN_PRESUPUESTAL.NATURALEZA=''D''
									    THEN MODIF_PAC_DEBITO -MODIF_PAC_CREDITO
									    ELSE MODIF_PAC_CREDITO-MODIF_PAC_DEBITO END) TOTALPAC,
									  SUM(
									  CASE
									    WHEN PLAN_PRESUPUESTAL.NATURALEZA=''D''
									    THEN EJE_PPT_DEBITO -EJE_PPT_CREDITO
									    ELSE EJE_PPT_CREDITO-EJE_PPT_DEBITO END ) EJECUCIONPPT,
										SUM(PAC_EJECUTADO) AS PACEJECUTADO
									    FROM V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL
									    INNER JOIN V_SALDO_PLAN_PPTAL SALDO_PLAN_PPTAL
									       ON PLAN_PRESUPUESTAL.COMPANIA       = SALDO_PLAN_PPTAL.COMPANIA
									      AND PLAN_PRESUPUESTAL.ANO            = SALDO_PLAN_PPTAL.ANO
									      AND PLAN_PRESUPUESTAL.ID              = SALDO_PLAN_PPTAL.ID
									    WHERE PLAN_PRESUPUESTAL.COMPANIA     = '''|| UN_COMPANIA ||'''
									      AND PLAN_PRESUPUESTAL.ANO            = ' || UN_ANO || '
									      AND LENGTH(PLAN_PRESUPUESTAL.ID)<= LENGTH(TRIM(SUBSTR('''|| RSDETALLE.CUENTA ||''',0,'|| MI_INTESTRUCTURA ||')))
									      AND PLAN_PRESUPUESTAL.ID         = SUBSTR(TRIM(SUBSTR('''|| RSDETALLE.CUENTA ||''',0,'|| MI_INTESTRUCTURA ||')),0, LENGTH(PLAN_PRESUPUESTAL.ID))
									      AND PLAN_PRESUPUESTAL.TERCERO       IS NULL
									      AND SALDO_PLAN_PPTAL.MES     <= ' || EXTRACT(MONTH FROM UN_FECHA) || '	-- FORMS!COMPROBANTE_PPTAL!FECHA PENDIENTE POR SABER DE DONDE VIENE LA FECHA
									      AND MAN_PAC                         <> 0 ';
              IF RSDETALLE.CENTRO_COSTO IS NULL THEN
                MI_STRCENTRO := ' IS NULL ';
              ELSE
                MI_STRCENTRO := ' = ''' || RSDETALLE.CENTRO_COSTO || ''' ';
              END IF;
              IF RSDETALLE.AUXILIAR IS NULL THEN
                MI_STRAUXILIAR := ' IS NULL ';
              ELSE
                MI_STRAUXILIAR := ' = ''' || RSDETALLE.AUXILIAR || ''' ';
              END IF;    

              MI_SQL := MI_SQL || 'AND ((PLAN_PRESUPUESTAL.CENTRO_COSTO '|| MI_STRCENTRO || ' AND PLAN_PRESUPUESTAL.AUXILIAR IS NULL)  
                        OR (PLAN_PRESUPUESTAL.CENTRO_COSTO '|| MI_STRCENTRO || '  AND PLAN_PRESUPUESTAL.AUXILIAR ' || MI_STRAUXILIAR || ')  
                        OR (PLAN_PRESUPUESTAL.CENTRO_COSTO IS NULL AND PLAN_PRESUPUESTAL.AUXILIAR '|| MI_STRAUXILIAR || ')  
                        OR (PLAN_PRESUPUESTAL.CENTRO_COSTO IS NULL AND PLAN_PRESUPUESTAL.AUXILIAR IS NULL)  
                        OR (PLAN_PRESUPUESTAL.CENTRO_COSTO IS NULL AND PLAN_PRESUPUESTAL.AUXILIAR IS NOT NULL AND PLAN_PRESUPUESTAL.AUXILIAR= SUBSTR(''' || NVL( RSDETALLE.AUXILIAR, '') || ''',LENGTH(PLAN_PRESUPUESTAL.AUXILIAR))))
                        GROUP BY SALDO_PLAN_PPTAL.COMPANIA, SALDO_PLAN_PPTAL.ANO, SALDO_PLAN_PPTAL.ID, PLAN_PRESUPUESTAL.NATURALEZA';
 						OPEN MI_RS FOR MI_SQL;
	 						LOOP
	 							FETCH MI_RS INTO MI_MODIFPAC, MI_TOTALPAC, MI_EJECUCIONPPT,MI_PACEJECUTADO;
	 							EXIT WHEN MI_RS%NOTFOUND;
	 							IF MI_MODIFPAC IS NOT NULL THEN
	 								IF (MI_TOTALPAC - MI_PACEJECUTADO) < MI_VALORG THEN
		 								MI_RTA := 'No hay PAC disponible para realizar esta ejecución de pac.' || TO_CHAR(MI_TOTALPAC - MI_PACEJECUTADO,'999,999,999,999.00');
		 								RETURN MI_RTA;
		 							END IF;
	 							END IF;
	 						END LOOP;
 						CLOSE MI_RS;	
          END IF;
				END IF;
			END IF;

			IF MI_VALORG >0 OR (MI_VALORG = 0 AND UN_TIPO = 'ADD') THEN
				BEGIN 
				SELECT CLASECNTPRES.COLUMNA, CLASECNTPRES.AFECTACION,CLASECNTPRES.CODIGO
				  INTO MI_COLUMNA,MI_AFECTACION,MI_CODIGO
				  FROM TIPO_COMPROBPP
				LEFT JOIN CLASECNTPRES
				   ON TIPO_COMPROBPP.CLASE    	= CLASECNTPRES.CODIGO
				WHERE TIPO_COMPROBPP.CODIGO		= UN_TIPO
				  AND TIPO_COMPROBPP.COMPANIA 	= UN_COMPANIA;

          BEGIN
					MI_CAMPOS :='COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CONSECUTIVO,CUENTA,FECHA,NATURALEZA,DESCRIPCION,VALOR_DEBITO,VALOR_CREDITO,TIPO_DOCUMENTO,NRO_DOCUMENTO,CENTRO_COSTO,TERCERO,SUCURSAL,AUXILIAR,TIPO_CPTE_AFECT,CMPTE_AFECTADO,CONSECUTIVOPPTO';
					MI_VALORES:= '''' || RSDETALLE.COMPANIA || ''',
					' || UN_ANO || ',
					''' || UN_TIPO || ''',
					' || UN_NUMERO || ',
					' || RSDETALLE.CONSECUTIVO || ',
					''' || RSDETALLE.CUENTA || ''',					
					TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY''),
					''' || RSDETALLE.NATURALEZA || ''',
					''' || RSDETALLE.DESCRIPCION ||''',
					' || CASE WHEN MI_COLUMNA = 'D' THEN MI_VALORG ELSE 0 END || ',
                    ' || CASE WHEN MI_COLUMNA = 'C' THEN MI_VALORG ELSE 0 END || ',
                    ''' || RSDETALLE.TIPO_DOCUMENTO || ''',
                    ''' || RSDETALLE.NRO_DOCUMENTO || ''',
                    ''' || NVL(RSDETALLE.CENTRO_COSTO, '9999999999') || ''',
                    ''' || RSDETALLE.TERCERO || ''',
                    ''' || RSDETALLE.SUCURSAL || ''',
                    ''' || NVL(RSDETALLE.AUXILIAR, '9999999999999999') || ''',
                    ''' || UN_TIPOAFEC || ''',
                    ' || UN_NUMEROAFEC || ',

                    ' || RSDETALLE.CONSECUTIVO || ')';
					MI_PCKDATOS:= PCK_DATOS.FC_ACME('DETALLE_COMPROBANTE_PPTAL', 'I', MI_CAMPOS, MI_VALORES, NULL, NULL);

          EXCEPTION WHEN OTHERS THEN
            MI_PCKDATOS:=0;
          END;

					IF MI_PCKDATOS = 0 THEN
						MI_RTA := 'Los detalles NO se copiaron con éxito. <BR> Revise e intente de Nuevo.';
						RETURN MI_RTA;
					END IF;
				EXCEPTION WHEN NO_DATA_FOUND THEN
					MI_COLUMNA := NULL;
				END;
        --No se migra pues todo lo realiza el trigger AIUD_DETALLE_COMPROBANTE_PPTAL

        IF MI_CODIGO = 'RES' OR MI_CODIGO = 'ADR' THEN
          MI_CAMPOS :='COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CONSECUTIVO,CUENTA,FECHA,MOV_DEBITO,MOV_CREDITO';
          MI_VALORES:= '''' || RSDETALLE.COMPANIA || ''',
          ' || UN_ANO || ',
          ''' || UN_TIPO || ''',
          ' || UN_NUMERO || ',
          ' || RSDETALLE.CONSECUTIVO || ',
          ''' || RSDETALLE.CUENTA || ''',			
          TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY''),
          ' || MI_VALORG || ',
          0';
          MI_PCKDATOS:= PCK_DATOS.FC_ACME('PACPROGRAMADO', 'I', MI_CAMPOS, MI_VALORES, NULL, NULL);
          -- xx = ActPto0(cnsPACProgramado, Compania, GetYear(), GetMonth(), rsdetalle!CUENTA, 0, 0, 0, ValorG, 0, ValorG)
        ELSIF MI_CODIGO = 'DMR' THEN
          MI_CAMPOS :='COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CONSECUTIVO,CUENTA,FECHA,MOV_DEBITO,MOV_CREDITO';
          MI_VALORES:= '''' || RSDETALLE.COMPANIA || ''',
          ' || UN_ANO || ',
          ''' || UN_TIPO || ''',
          ' || UN_NUMERO || ',
          ' || RSDETALLE.CONSECUTIVO || ',
          ''' || RSDETALLE.CUENTA || ''',			
          TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY''),					
          0,
          ' || MI_VALORG;
          MI_PCKDATOS:= PCK_DATOS.FC_ACME('PACPROGRAMADO', 'I', MI_CAMPOS, MI_VALORES, NULL, NULL);
          -- xx = ActPto0(cnsPACProgramado, Compania, GetYear(), GetMonth(), rsdetalle!CUENTA, 0, 0, 0, 0, ValorG, ValorG * -1)
        END IF;

        IF MI_CODIGO = 'REO' OR MI_CODIGO = 'ARO' THEN
          IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA, 'CONTROLAR PAC COMPROMETIDO CONTRA PAC APROPIADO' , PCK_DATOS.MODULOCONTABILIDAD , SYSDATE),'NO') = 'SI' THEN
            IF (RS.TOTALPACAPROPIADO - RS.PAC_COMPROMETIDO) < MI_VALORG THEN
              MI_RTA := 'No hay PAC apropiado disponible para realizar este pac comprometido.<br> PAC disponible: ' || TO_CHAR(RS.TOTALPACAPROPIADO - RS.PAC_COMPROMETIDO,'999,999,999,999.00');
              RETURN MI_RTA;
            END IF;
          END IF;

          IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA, 'CONTROLAR PAC COMPROMETIDO CONTRA PAC PROGRAMADO' , PCK_DATOS.MODULOCONTABILIDAD , SYSDATE),'NO') = 'SI' THEN
            IF (RS.PAC_PROGRAMADO - RS.PAC_COMPROMETIDO) < MI_VALORG THEN
              MI_RTA := 'No hay PAC programado disponible para realizar este pac comprometido.<br> PAC programado disponible: ' || TO_CHAR(RS.PAC_PROGRAMADO - RS.PAC_COMPROMETIDO,'999,999,999,999.00');
              RETURN MI_RTA;
            END IF;
          END IF;

          MI_CAMPOS :='COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CONSECUTIVO,CUENTA,FECHA,MOV_DEBITO,MOV_CREDITO';
					MI_VALORES:= '''' || RSDETALLE.COMPANIA || ''',
					' || UN_ANO || ',
					''' || UN_TIPO || ''',
					' || UN_NUMERO || ',
					' || RSDETALLE.CONSECUTIVO || ',
					''' || RSDETALLE.CUENTA || ''',			
					TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY''),					
					' || MI_VALORG || ',
					0';
					MI_PCKDATOS:= PCK_DATOS.FC_ACME('PACCOMPROMETIDO', 'I', MI_CAMPOS, MI_VALORES, NULL, NULL);
					-- xx = ActPto0(cnsPACComprometido, Getcompany(), GetYear(), GetMonth(), rsdetalle!CUENTA, 0, 0, 0, ValorG, 0, ValorG)
				ELSIF MI_CODIGO = 'DRO' THEN
					MI_CAMPOS :='COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CONSECUTIVO,CUENTA,FECHA,MOV_DEBITO,MOV_CREDITO';
					MI_VALORES:= '''' || RSDETALLE.COMPANIA || ''',
					' || UN_ANO || ',
					''' || UN_TIPO || ''',
					' || UN_NUMERO || ',
					' || RSDETALLE.CONSECUTIVO || ',
					''' || RSDETALLE.CUENTA || ''',			
					TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY''),					
					0,
					' || MI_VALORG;
					MI_PCKDATOS:= PCK_DATOS.FC_ACME('PACCOMPROMETIDO', 'I', MI_CAMPOS, MI_VALORES, NULL, NULL);
					--xx = ActPto0(cnsPACComprometido, Getcompany(), GetYear(), GetMonth(), rsdetalle!CUENTA, 0, 0, 0, 0, ValorG, ValorG * -1)
        END IF;

        IF UN_PACPROPORCIONALGIRO <> 'SI' THEN
          IF MI_CODIGO = 'EGR' OR MI_CODIGO = 'AEG' THEN
						IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA, 'CONTROLAR PAC EJECUTADO CONTRA PAC APROPIADO' , PCK_DATOS.MODULOCONTABILIDAD , SYSDATE),'NO') = 'SI' THEN
							IF (RS.TOTALPACAPROPIADO - RS.PACEJECUTADO) < MI_VALORG THEN
								MI_RTA := 'No hay PAC apropiado disponible para realizar este pac ejecutado. <br> PAC disponible: ' || TO_CHAR(RS.TOTALPACAPROPIADO - RS.PACEJECUTADO,'999,999,999,999.00');
								RETURN MI_RTA;
							END IF;
						END IF;
						IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA, 'CONTROLAR PAC EJECUTADO CONTRA PAC COMPROMETIDO' , PCK_DATOS.MODULOCONTABILIDAD , SYSDATE),'NO') = 'SI' THEN
							IF (RS.PAC_COMPROMETIDO - RS.PACEJECUTADO) < MI_VALORG THEN
								MI_RTA := 'No hay PAC comprometido disponible para realizar este pac ejecutado. <br> PAC comprometido disponible: ' || TO_CHAR(RS.PAC_COMPROMETIDO - RS.PACEJECUTADO,'999,999,999,999.00');
								RETURN MI_RTA;
							END IF;
						END IF;
						MI_CAMPOS :='COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CONSECUTIVO,CUENTA,FECHA,MOV_DEBITO,MOV_CREDITO';
						MI_VALORES:= '''' || RSDETALLE.COMPANIA || ''',
						' || UN_ANO || ',
						''' || UN_TIPO || ''',
						' || UN_NUMERO || ',
						' || RSDETALLE.CONSECUTIVO || ',
						''' || RSDETALLE.CUENTA || ''',			
						TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY''),
						' || MI_VALORG || ',					
						0';

						MI_PCKDATOS:= PCK_DATOS.FC_ACME('PACEJECUTADO', 'I', MI_CAMPOS, MI_VALORES, NULL, NULL);
						-- xx = ActPto0(cnsPACEjecutado, Getcompany(), GetYear(), GetMonth(), rsdetalle!CUENTA, 0, 0, 0, ValorG, 0, ValorG)
					ELSIF MI_CODIGO = 'DEG' THEN
						MI_CAMPOS :='COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CONSECUTIVO,CUENTA,FECHA,MOV_DEBITO,MOV_CREDITO';
						MI_VALORES:= '''' || RSDETALLE.COMPANIA || ''',
						' || UN_ANO || ',
						''' || UN_TIPO || ''',
						' || UN_NUMERO || ',
						' || RSDETALLE.CONSECUTIVO || ',
						''' || RSDETALLE.CUENTA || ''',			
						TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY''),
						0,
						' || MI_VALORG;

						MI_PCKDATOS:= PCK_DATOS.FC_ACME('PACEJECUTADO', 'I', MI_CAMPOS, MI_VALORES, NULL, NULL);
						--  xx = ActPto0(cnsPACEjecutado, Getcompany(), GetYear(), GetMonth(), rsdetalle!CUENTA, 0, 0, 0, 0, ValorG, ValorG * -1)
					END IF;	
        END IF;
			END IF;
		END LOOP;
	END LOOP;
	IF MI_COMPANIA IS NULL THEN
		MI_RTA := 'No hay Detalles del Comprobante para Copiar. Revise e intente de Nuevo.';
		RETURN MI_RTA;
	END IF;
END IF;
RETURN MI_RTA;
END FC_AFECTARCOMPROBANTEPPTAL;


PROCEDURE PR_GENERARCOMPROBANTEPPTAL
/*
NAME              : PR_GENERARCOMPROBANTEPPTAL
AUTHORS           : SYSMAN LTDA
AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÍGUEZ/JAVIER VILLATE
DATE MIGRADOR     : 06/09/2016--23/05/2017
TIME              : 11:44 AM
DESCRIPTION       : Desde un registro de comprobantes contables,
realiza la generación de un comprobante presupuestal asociado.
MODIFIER          : JUAN CARLOS RODRÍGUEZ AMÉZQUITA/MIGUEL ZANGUÑA
DATE MODIFIED     : 27/10/2017 09:36 AM
MODIFICATIONS     : Mejora en el mensaje que se visualiza en caso de que el
comprobante presupuestal ya esté creado/ Se adiciona parametro Desde interfaz
MODIFIER          : MARIA ALEJANDRA PEREZ SALAZAR
DATE MODIFIED     : 14/08/2025 09:36 AM
MODIFICATIONS     : Se modifica para que se actualice la cuentapptal en el detalle
                    del comprobante contable

@NAME:  generarComprobantePresupuestal
@METHOD:  GET
*/
(
UN_COMPANIA 			 IN PCK_SUBTIPOS.TI_COMPANIA,              --compañia
UN_ANO 				 		 IN PCK_SUBTIPOS.TI_ANIO,                  --año del comprobante que se esta creando
UN_TIPO 				 	 IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE DEFAULT ' ',       --Tipo comprobante de cruce de cuentas es decir el que se va a crear
UN_NUMERO 				 IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,  --Numero del comprobante que se esta creando
UN_FECHA 				 	 IN DATE,                                  --Fecha del comprobante que se esta creando
UN_TERCERO 				 IN PCK_SUBTIPOS.TI_TERCERO,               --Tercero del comprobante que se esta creando
UN_SUCURSAL 			 IN PCK_SUBTIPOS.TI_SUCURSAL,              --Sucursal del comprobante que se esta creando
UN_DESCRIPCION 		 IN COMPROBANTE_PPTAL.DESCRIPCION%TYPE,    --Fecha del comprobante que se esta creando
UN_NUMERODOC 			 IN COMPROBANTE_PPTAL.NRO_DOCUMENTO%TYPE , --Numero documento del comprobante que se esta creando
UN_VALORDOC 			 IN PCK_SUBTIPOS.TI_DOBLE,                 --valor del documento del comprobante que se esta creando
UN_TIPOPPTAL 			 IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL DEFAULT ' ',--El Tipo de comprobante desde donde se esta creando el ingreso
UN_CADENAINSERTAR  IN CLOB,                         --Cadena con las cuentas equivalentes presupuestales para crear los detalles.
UN_CANTIDAD        IN PCK_SUBTIPOS.TI_ENTERO,                          --Cantidad de inserciones que se deben hacer.
UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO,                          --Usuario que realiza el proceso.
UN_DESDEINTERFAZ	 IN PCK_SUBTIPOS.TI_LOGICO := 0
)
AS  
   MI_COUNT  				  NUMBER(10,0);
	MI_RTA 							VARCHAR2(3200);
	MI_STRCLASE 				TIPO_COMPROBPP.CLASE%TYPE;
	MI_CREAR 						BOOLEAN;
	MI_IND 	 						BOOLEAN;
	MI_TIPOPPTAL        PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL;
	MI_PCKDATOS         PCK_SUBTIPOS.TI_RTA_ACME;
	MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
	MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
	MI_REEMPLAZOS       PCK_SUBTIPOS.TI_CLAVEVALOR;
	MI_T_SPLIT		      PCK_SYSMAN_UTL.T_SPLIT;
	MI_T_SPLITV         PCK_SYSMAN_UTL.T_SPLIT;
	MI_CONSECUTIVO      PCK_SUBTIPOS.TI_ENTERO;
	MI_CUENTA           PCK_SUBTIPOS.TI_CODIGOCONTA;
	MI_VALOR            PCK_SUBTIPOS.TI_DOBLE;
	MI_RUBRO            PCK_SUBTIPOS.TI_CODIGOCONTA;
	MIVALOR_DEBITO      PCK_SUBTIPOS.TI_DOBLE;
	MIVALOR_CREDITO     PCK_SUBTIPOS.TI_DOBLE;
	MI_VALOR_DEBITO     PCK_SUBTIPOS.TI_DOBLE;
	MI_VALOR_CREDITO    PCK_SUBTIPOS.TI_DOBLE;
	MIEJECUCION_DEBITO  PCK_SUBTIPOS.TI_DOBLE;
	MIEJECUCION_CREDITO PCK_SUBTIPOS.TI_DOBLE;
	MICENTRO_COSTO      PCK_SUBTIPOS.TI_CENTRO_COSTO;
	MIAUXILIAR          PCK_SUBTIPOS.TI_AUXILIAR;
  MIFUENTERECURSO     PCK_SUBTIPOS.TI_FUENTE_RECURSOS;
  MIREFERENCIA        PCK_SUBTIPOS.TI_REFERENCIA;
	MIRECONOCIMIENTO    PCK_SUBTIPOS.TI_ENTERO;
	MI_PARAMETRO        PCK_SUBTIPOS.TI_PARAMETRO;
	MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;
	MI_TIPO             PCK_SUBTIPOS.TI_TIPOCOMPROBANTE;
	MI_NOMBRE_MES       VARCHAR2(15 CHAR);
	MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
	MI_CONDICIONACME    PCK_SUBTIPOS.TI_CONDICION;
  MI_PARAMETRODISTRI  PCK_SUBTIPOS.TI_PARAMETRO;
  MI_VALORESREMPLA    PCK_SUBTIPOS.TI_VALORES;
  MI_VALORESDC        PCK_SUBTIPOS.TI_VALORES;
  MI_VALORESFT        PCK_SUBTIPOS.TI_VALORES;
  MI_EXISTE_RUBRO     PCK_SUBTIPOS.TI_ENTERO;
  MI_CONSECUTIVOPAR   PCK_SUBTIPOS.TI_ENTERO;
  CODIGOCCPET         VARCHAR2(39);
  CODIGOUNIDADEJE     VARCHAR2(39);
  FUENTE_CUIPO        VARCHAR2(39);
  DETALLE_SECTORIAL   VARCHAR2(39);
  MI_CONDICION        CLOB;
  MI_PARAMETRO1       VARCHAR2(4000 CHAR);
  MI_TERCERODETALLE   PCK_SUBTIPOS.TI_TERCERO;
  MI_SUCURSALDETALLE   PCK_SUBTIPOS.TI_SUCURSAL;
  MI_VALOR_IVA        PCK_SUBTIPOS.TI_DOBLE;
  MI_TIPO_COMPROBANTE       VARCHAR2(2);
  MI_RETORNO                CLOB := '';
BEGIN
--INI_7729161_INTERFAZ(29/03/2023 CPEREZ)
    MI_PARAMETRODISTRI := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA, 'MANEJA LEY 99 ART 111', PCK_DATOS.MODULOPRESUPUESTO , SYSDATE),'NO');
  --FIN_7729161_INTERFAZ(29/03/2023 CPEREZ)
  IF UN_TIPO = ' ' OR UN_TIPO IS NULL THEN
    BEGIN
      SELECT TIPO_CRUCECUENTAS
      INTO MI_TIPO
      FROM TIPO_COMPROBANTE
      WHERE COMPANIA=UN_COMPANIA
      AND CODIGO=UN_TIPOPPTAL;

      IF MI_TIPO = ' ' OR MI_TIPO IS NULL THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END IF;
    EXCEPTION
      WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
      UN_EXC_COD => SQLCODE,
      UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_CRUCECUENTAS
      );
    END;
  ELSE
    MI_TIPO:=UN_TIPO;
  END IF;


  --VERIFICAR QUE EL COMPROBANTE TENGA DETALLES PARA CREAR EL OTRO COMPROBANTE
  SELECT COUNT(*)
  INTO   MI_COUNT
  FROM   DETALLE_COMPROBANTE_CNT
  WHERE  COMPANIA = UN_COMPANIA
    AND  ANO        = UN_ANO
    AND  TIPO_CPTE  = UN_TIPO
    AND  COMPROBANTE= UN_NUMERO;
  BEGIN
    IF MI_COUNT = 0 THEN
      RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
    END IF;
  EXCEPTION
    WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
    PCK_ERR_MSG.RAISE_WITH_MSG(
    UN_EXC_COD => SQLCODE,
    UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_GENCPTEPPTALVARIOS2
    );
  END;

  BEGIN
    IF PCK_PRESUPUESTO.FC_VERIFICAPERIODOPPTAL(UN_COMPANIA,UN_ANO,EXTRACT(MONTH FROM UN_FECHA)) = 0 THEN
      RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
    END IF;
  EXCEPTION
    WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
    PCK_ERR_MSG.RAISE_WITH_MSG(
    UN_EXC_COD => SQLCODE,
    UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_GENCPTEPPTALVARIOS3
    );
  END;

  --Se revisa si el tipo de comprobante en presupuesto está creado
  BEGIN
    SELECT CLASE
    INTO MI_STRCLASE
    FROM TIPO_COMPROBPP
    WHERE COMPANIA=UN_COMPANIA
    AND CODIGO= UN_TIPOPPTAL;

  EXCEPTION WHEN NO_DATA_FOUND THEN
    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
    WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
    MI_REEMPLAZOS(1).CLAVE := 'TIPO';
    MI_REEMPLAZOS(1).VALOR := UN_TIPOPPTAL;
    PCK_ERR_MSG.RAISE_WITH_MSG(
    UN_EXC_COD => SQLCODE,
    UN_TABLAERROR=> 'TIPO_COMPROBPP',
    UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_GENCPTEPPTALVARIOS4,
    UN_REEMPLAZOS => MI_REEMPLAZOS
    );
  END;

  -- Verificar que el comprobante presupuestal NO esté creado
  BEGIN
    SELECT COUNT(*)
    INTO   MI_COUNT
    FROM   COMPROBANTE_PPTAL
    WHERE  COMPANIA   = UN_COMPANIA
      AND  ANO       = UN_ANO
      AND  TIPO      = UN_TIPOPPTAL
      AND  NUMERO    = UN_NUMERO;

    IF MI_COUNT > 0 AND UN_DESDEINTERFAZ <> 0 THEN  --Elimina el detalle y el comprobante
      BEGIN
        MI_TABLA := 'DETALLE_COMPROBANTE_PPTAL';
        MI_CONDICIONACME := '   COMPANIA  = ''' || UN_COMPANIA || '''
                  AND ANO = '|| UN_ANO ||'
                  AND TIPO_CPTE ='''|| MI_TIPO ||'''
                  AND COMPROBANTE = '||UN_NUMERO ||'  ';

        BEGIN
          PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME( UN_TABLA      => MI_TABLA,
                           UN_ACCION     => 'E',
                            UN_CONDICION => MI_CONDICIONACME);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;

        MI_TABLA := 'COMPROBANTE_PPTAL';
        MI_CONDICIONACME := '   COMPANIA  = ''' || UN_COMPANIA || '''
                  AND ANO = '|| UN_ANO ||'
                  AND TIPO ='''|| MI_TIPO ||'''
                  AND NUMERO = '||UN_NUMERO ||' ';

        BEGIN
          PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME( UN_TABLA      => MI_TABLA,
                           UN_ACCION     => 'E',
                            UN_CONDICION => MI_CONDICIONACME);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
        MI_REEMPLAZOS(1).CLAVE := 'CODIGO';
        MI_REEMPLAZOS(1).VALOR := UN_NUMERO;
        MI_REEMPLAZOS(2).CLAVE := 'MES';
        MI_REEMPLAZOS(2).VALOR := MI_NOMBRE_MES;

        PCK_ERR_MSG.RAISE_WITH_MSG
          ( UN_EXC_COD => SQLCODE
          , UN_ERROR_COD => PCK_ERRORES.ERRR_BORRARCOMPPPTALINTER
          , UN_REEMPLAZOS => MI_REEMPLAZOS);
      END;

      ELSIF MI_COUNT > 0 THEN
      SELECT PCK_SYSMAN_UTL.FC_NOMBRE_MES(MES) MES
      INTO MI_NOMBRE_MES
      FROM COMPROBANTE_PPTAL
      WHERE COMPANIA = UN_COMPANIA
      AND ANO        = UN_ANO
      AND TIPO       = MI_TIPO
      AND NUMERO     = UN_NUMERO;
      RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
    END IF;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
    MI_REEMPLAZOS(1).CLAVE := 'CODIGO';
    MI_REEMPLAZOS(1).VALOR := UN_NUMERO;
    MI_REEMPLAZOS(2).CLAVE := 'MES';
    MI_REEMPLAZOS(2).VALOR := MI_NOMBRE_MES;
    PCK_ERR_MSG.RAISE_WITH_MSG(
    UN_EXC_COD => SQLCODE
    , UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_GENCPTEPPTALVARIOS5
    , UN_REEMPLAZOS => MI_REEMPLAZOS
    );
  END;

  MI_CREAR := CASE WHEN MI_COUNT > 0 AND UN_DESDEINTERFAZ = 0 THEN FALSE ELSE TRUE END;

  IF MI_CREAR THEN

    MI_CAMPOS :='COMPANIA,ANO,TIPO,NUMERO,FECHA,TERCERO,SUCURSAL,DESCRIPCION,VLR_DOCUMENTO, NRO_DOCUMENTO,FECHA_VCN_DOC,CREATED_BY,DATE_CREATED';
    MI_VALORES:= '''' || UN_COMPANIA || ''',
          ' || UN_ANO || ',
          ''' || UN_TIPOPPTAL || ''',
          ' || UN_NUMERO || ',
          TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY''),
          ''' || UN_TERCERO || ''',
          ''' || UN_SUCURSAL || ''',
          ''' || UN_DESCRIPCION || ''',
          ' || UN_VALORDOC || ',
          ''' || UN_NUMERODOC ||''',
          TO_DATE(''' || TO_CHAR(ADD_MONTHS(UN_FECHA,1), 'DD/MM/YYYY') || ''',''DD/MM/YYYY''),'''
          || UN_USUARIO || ''', SYSDATE';

    MI_PCKDATOS:= PCK_DATOS.FC_ACME('COMPROBANTE_PPTAL', 'I', MI_CAMPOS, MI_VALORES, NULL, NULL);
    BEGIN
      IF NOT MI_PCKDATOS >0 THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END IF;
    EXCEPTION
      WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
      UN_EXC_COD => SQLCODE,
      UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_GENCPTEPPTALVARIOS7
      );
    END;
  END IF;

  --DE AQUI EN ADELANTE CREA LOS DETALLES DEL COMPROBANTE
  MI_CONSECUTIVO :=  0;
  MI_CONSECUTIVOPAR :=0;
  IF MI_CREAR OR UN_TIPOPPTAL <> MI_TIPO THEN
    MI_T_SPLIT := PCK_SYSMAN_UTL.FC_SPLIT_SYS(
            UN_LISTA        => '' || UN_CADENAINSERTAR || '',
            UN_DELIMITADOR  => ';');
    FOR MI_I IN 1..UN_CANTIDAD 
    LOOP
      MI_T_SPLITV := PCK_SYSMAN_UTL.FC_SPLIT_SYS(
                UN_LISTA   => '' || TO_CHAR(MI_T_SPLIT(MI_I)) || '',
                UN_DELIMITADOR  => ',');
      BEGIN
        
        --INI_7729161_INTERFAZ(29/03/2023 CPEREZ)
         MI_CONSECUTIVO    :=  NVL(TO_NUMBER(MI_T_SPLITV(1)),1);
         MI_CONSECUTIVOPAR :=  MI_CONSECUTIVOPAR + 1;
        --FIN_7729161_INTERFAZ(29/03/2023 CPEREZ)

        MI_CUENTA           := NVL(TO_CHAR(MI_T_SPLITV(2)),0);
        MI_VALOR            :=         NVL(TO_NUMBER(MI_T_SPLITV(3)),0);
        MI_RUBRO            := NVL(TO_CHAR(MI_T_SPLITV(4)),0);
      END;
      -- PCK_CONTABILIDAD5.PR_CREARDETALLESPPTALESVARIOS( UN_COMPANIA, UN_ANO, UN_TIPO, UN_NUMERO, MI_STRCLASE, UN_CADENAINSERTAR);
      -- Débito y crédito
      --YB 19/06/2018 Se agregó variable para enviar FUENTE_RECURSO
      --29/08/2018 @jreina Se agregó variable para enviar REFERENCIA
      --06/03/2025 lvega se agrega VALOR_IVA
      BEGIN
        SELECT VALOR_DEBITO,VALOR_CREDITO,EJECUCION_DEBITO,EJECUCION_CREDITO,CENTRO_COSTO,AUXILIAR,FUENTE_RECURSO,REFERENCIA,RECONOCIMIENTO, TERCERO, SUCURSAL, VALOR_IVA
        INTO   MIVALOR_DEBITO,MIVALOR_CREDITO,MIEJECUCION_DEBITO,MIEJECUCION_CREDITO,MICENTRO_COSTO,MIAUXILIAR,MIFUENTERECURSO,MIREFERENCIA,MIRECONOCIMIENTO,MI_TERCERODETALLE, MI_SUCURSALDETALLE, MI_VALOR_IVA
        FROM   DETALLE_COMPROBANTE_CNT
        WHERE  COMPANIA    = UN_COMPANIA
          AND  ANO         = UN_ANO
          AND  TIPO_CPTE   = UN_TIPO
          AND  COMPROBANTE = UN_NUMERO
          AND  CONSECUTIVO = MI_CONSECUTIVO;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        BEGIN
          MI_MSGERROR(1).CLAVE := 'ANO';
          MI_MSGERROR(1).VALOR := UN_ANO;
          MI_MSGERROR(2).CLAVE := 'TIPO';
          MI_MSGERROR(2).VALOR := UN_TIPOPPTAL;
          MI_MSGERROR(3).CLAVE := 'COMPROBANTE';
          MI_MSGERROR(3).VALOR := UN_NUMERO;
          MI_MSGERROR(4).CLAVE := 'CONSECUTIVO';
          MI_MSGERROR(4).VALOR := MI_CONSECUTIVO;
          RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE
          ,UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_NOREGISTRO
          ,UN_REEMPLAZOS => MI_MSGERROR
          );
        END;
      END;
        
      MI_PARAMETRO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA, 'MANEJA EQUIVALENTE PRESUPUESTAL FIJO AUTOMATICO', PCK_DATOS.MODULOCONTABILIDAD , SYSDATE),'NO');
        IF MI_PARAMETRO = 'SI' THEN
            BEGIN
                SELECT CENTRO_COSTO,AUXILIAR,FUENTE_RECURSO,REFERENCIA
                INTO   MICENTRO_COSTO,MIAUXILIAR,MIFUENTERECURSO,MIREFERENCIA
                FROM   PLAN_PPTAL_CUENTACNT
                WHERE  COMPANIA    = UN_COMPANIA
                  AND  ANO         = UN_ANO
                  AND  RUBRO   = MI_RUBRO
                  AND CUENTA_CONTABLE = MI_CUENTA;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                BEGIN RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                 MI_RETORNO := 'La cuenta  ' || MI_CUENTA || ' no tiene asignado un equivalente válido ' || MI_RUBRO || '. 
                 				Por favor, configure el equivalente correcto.';
                  MI_MSGERROR (1).CLAVE := 'MENSAJE';
                  MI_MSGERROR (1).VALOR := MI_RETORNO;
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                             UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                                             UN_TABLAERROR => 'PLAN_CONTABLE',
                                             UN_REEMPLAZOS => MI_MSGERROR);
                END;
            END;
        END IF;
        
      MI_PARAMETRO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA, 'INGRESO PRESUPUESTAL DESDE ORDEN DE PAGO', PCK_DATOS.MODULOCONTABILIDAD , SYSDATE);
      IF MI_PARAMETRO = 'SI' THEN
        MI_VALOR_DEBITO := MIVALOR_DEBITO;
        MI_VALOR_CREDITO := MIVALOR_CREDITO;
      ELSE
        MI_VALOR_DEBITO := MIEJECUCION_DEBITO;
        MI_VALOR_CREDITO := MIEJECUCION_CREDITO;
      END IF;
      -- Inserción del detalle presupuestal
      --YB 19/06/2018 Se agregó campo FUENTE_RECURSO, para inserción
      --INI_7729161_INTERFAZ(29/03/2023 CPEREZ)
        BEGIN
          MI_EXISTE_RUBRO := 0;
          SELECT  SUM(PORCENTAJE) EXISTE 
            INTO MI_EXISTE_RUBRO
          FROM DISTRIBUCION_ICLD 
          WHERE COMPANIA = UN_COMPANIA
            AND ANO      = UN_ANO 
            AND RUBRO    = MI_RUBRO
           HAVING  SUM(PORCENTAJE) =  100
          GROUP BY COMPANIA ,  ANO ,RUBRO;

        EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_EXISTE_RUBRO := 0;
        END;
        MI_CAMPOS := 'COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CONSECUTIVO,CUENTA,FECHA,DESCRIPCION
              ,VALOR_DEBITO,VALOR_CREDITO,TERCERO,SUCURSAL,CENTRO_COSTO,AUXILIAR,FUENTE_RECURSO,REFERENCIA,RECONOCIMIENTO,CREATED_BY,DATE_CREATED';
        MI_VALORES := '''' || UN_COMPANIA || '''
                        ,' || UN_ANO || '
                      ,''' || UN_TIPOPPTAL || '''
                        ,' || UN_NUMERO || '
                        ,--MI_CONSECUTIVO--
                      ,''' || MI_RUBRO || '''
                      ,TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                      ,''' || UN_DESCRIPCION || ''' ';
        MI_VALORES :=  MI_VALORES || '--MI_VALORESDC--' ;
        MI_VALORES :=  MI_VALORES || '
                          ,''' || MI_TERCERODETALLE || '''
                          ,''' || MI_SUCURSALDETALLE || '''
                          ,''' || MICENTRO_COSTO || '''
                          ,''' || MIAUXILIAR || ''' ';
        MI_VALORES :=  MI_VALORES || '--MI_VALORESFT--';
        MI_VALORES :=  MI_VALORES || '
              ,''' || MIREFERENCIA    || '''
              ,'   || MIRECONOCIMIENTO ||'
              ,''' || UN_USUARIO || ''', SYSDATE';
        IF NVL(MI_PARAMETRODISTRI,'NO') ='SI' AND MI_EXISTE_RUBRO = 100 THEN --Distribucion ICLD
       
            FOR MI_RS IN (
              SELECT FUENTE,PORCENTAJE
              FROM DISTRIBUCION_ICLD 
              WHERE COMPANIA = UN_COMPANIA
                AND ANO      = UN_ANO 
                AND RUBRO    = MI_RUBRO
              ) LOOP
                MI_VALORESDC := '
                               ,' || (MI_VALOR_DEBITO * (NVL(MI_RS.PORCENTAJE,0) / 100)) || '
                               ,' || (MI_VALOR_CREDITO * (NVL(MI_RS.PORCENTAJE,0) / 100)) || ' ';
                MI_VALORESFT :=  '
                            ,''' || NVL(MI_RS.FUENTE,'') || ''' ';
                
                BEGIN
                  MI_VALORESREMPLA :=   REPLACE(MI_VALORES,'--MI_CONSECUTIVO--',MI_CONSECUTIVOPAR);
                  MI_VALORESREMPLA :=   REPLACE(MI_VALORESREMPLA,'--MI_VALORESDC--',MI_VALORESDC);
                  MI_VALORESREMPLA :=   REPLACE(MI_VALORESREMPLA,'--MI_VALORESFT--',MI_VALORESFT);
                  MI_PCKDATOS := PCK_DATOS.FC_ACME(
                                                    UN_TABLA => 'DETALLE_COMPROBANTE_PPTAL',
                                                    UN_ACCION => 'I',
                                                    UN_CAMPOS => MI_CAMPOS,
                                                    UN_VALORES => MI_VALORESREMPLA 
                                                  );
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD => SQLCODE,
                  UN_TABLAERROR => 'DETALLE_COMPROBANTE_PPTAL',
                  UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_CREARDETPPTALVARIOS
                  );
                END;
                MI_CONSECUTIVOPAR := MI_CONSECUTIVOPAR + 1;
            END LOOP;
        ELSE --anterior proceso
          MI_VALORESDC := '
                            ,' || MI_VALOR_DEBITO || '
                            ,' || MI_VALOR_CREDITO || ' ';
          MI_VALORESFT :=  '
                          ,''' || MIFUENTERECURSO || ''' ';
          
          BEGIN
            IF NVL(MI_PARAMETRODISTRI,'NO') ='SI' THEN
              MI_VALORESREMPLA :=   REPLACE(MI_VALORES,'--MI_CONSECUTIVO--',MI_CONSECUTIVOPAR);
            ELSE
              MI_VALORESREMPLA :=   REPLACE(MI_VALORES,'--MI_CONSECUTIVO--',MI_CONSECUTIVO);
            END IF;
              MI_VALORESREMPLA :=   REPLACE(MI_VALORESREMPLA,'--MI_VALORESDC--',MI_VALORESDC);
              MI_VALORESREMPLA :=   REPLACE(MI_VALORESREMPLA,'--MI_VALORESFT--',MI_VALORESFT);
              MI_PCKDATOS := PCK_DATOS.FC_ACME(
                                                UN_TABLA => 'DETALLE_COMPROBANTE_PPTAL',
                                                UN_ACCION => 'I',
                                                UN_CAMPOS => MI_CAMPOS,
                                                UN_VALORES => MI_VALORESREMPLA 
                                              );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD => SQLCODE,
              UN_TABLAERROR => 'DETALLE_COMPROBANTE_PPTAL',
              UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_CREARDETPPTALVARIOS
              );
            END;
              --lvega
             BEGIN
                SELECT CLASE_CONTABLE
                INTO MI_TIPO_COMPROBANTE
                FROM TIPO_COMPROBANTE
                WHERE  COMPANIA = UN_COMPANIA
                AND CODIGO  =  MI_TIPO;
             EXCEPTION  WHEN NO_DATA_FOUND THEN
                    MI_TIPO_COMPROBANTE := '';
             END;
              MI_PARAMETRO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA, 'MANEJA FACTURAS CON IVA', 69 , SYSDATE),'NO');
              IF MI_PARAMETRO = 'SI' AND MI_VALOR_IVA > 0 AND MI_TIPO_COMPROBANTE IN('I','B') THEN
                BEGIN
                MI_VALOR_CREDITO := MI_VALOR_CREDITO  -  MI_VALOR_IVA;
                MI_CAMPOS := 'VALOR_CREDITO  = '|| MI_VALOR_CREDITO ||'';
                MI_CONDICION :=' COMPANIA = '''||UN_COMPANIA||'''
                              AND ANO      =  '||UN_ANO||'
                              AND TIPO_CPTE     = '''||UN_TIPO||'''
                              AND COMPROBANTE   = '||UN_NUMERO||'
                              AND CONSECUTIVO   = '|| MI_CONSECUTIVO ||'';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'DETALLE_COMPROBANTE_PPTAL',
                                            UN_ACCION    => 'M',
                                            UN_CAMPOS    => MI_CAMPOS,
                                            UN_CONDICION => MI_CONDICION);
                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                 END;
              END IF;
              IF NVL(MI_PARAMETRODISTRI,'NO') ='SI' THEN
                MI_CONSECUTIVOPAR :=  MI_CONSECUTIVOPAR +1 ;
              END IF ;
        END IF;
      --FIN_7729161_INTERFAZ(29/03/2023 CPEREZ)
    END LOOP;
  END IF;
  
  -- Ini - Ticket#7726563: Se agrega procesos para actualizar los clasificadores cuipo al momento de generar el comprobante pptal.

    BEGIN 
        FOR RS IN (
                    SELECT COMPANIA, ANO, TIPO_CPTE, COMPROBANTE, CUENTA
                    FROM DETALLE_COMPROBANTE_PPTAL
                    WHERE   COMPANIA    = UN_COMPANIA
                    AND     ANO         = UN_ANO
                    AND     TIPO_CPTE   = UN_TIPO
                    AND     COMPROBANTE = UN_NUMERO
                    GROUP BY COMPANIA, ANO, TIPO_CPTE, COMPROBANTE, CUENTA
                  ) 
        LOOP 

        BEGIN 

            FOR RS1 IN (SELECT MOV.COMPANIA,
                            MOV.ANO,                
                            MOV.CODIGO,               
                            MAX(CASE WHEN PADRE.CLASECLASIFICADOR ='006' THEN
                            PADRE.TIPOCLASIFICADOR 
                            ELSE 
                            NULL END) CODIGOCCPET_PAD,
                            MOV.CODIGOCCPET CODIGOCCPET_MAN,
                            MAX(CASE WHEN PADRE.CLASECLASIFICADOR ='008' THEN
                            PADRE.TIPOCLASIFICADOR 
                            ELSE
                            NULL END) CODIGOUNIDADEJE_PAD, 
                            MOV.CODIGOUNIDADEJE CODIGOUNIDADEJE_MAN,
                            MAX(CASE WHEN PADRE.CLASECLASIFICADOR ='009' THEN
                            PADRE.TIPOCLASIFICADOR 
                            ELSE
                            NULL END) FUENTE_PAD, 
                            MOV.CODIGOFUENTE FUENTE_MAN,
                            MAX(CASE WHEN PADRE.CLASECLASIFICADOR ='012' THEN
                            PADRE.TIPOCLASIFICADOR 
                            ELSE
                            NULL END) DET_SECTORIAL_PAD, 
                            MOV.DETALLE_SECTORIAL DET_SECTORIAL_MAN
                            FROM PLAN_PRESUPUESTAL MOV 
                            LEFT JOIN PLAN_PRESUPUESTAL PADRE 
                            ON PADRE.COMPANIA = MOV.COMPANIA 
                            AND PADRE.ANO = MOV.ANO 
                            AND PADRE.CODIGO = SUBSTR(MOV.CODIGO,1,LENGTH(PADRE.CODIGO))
                            WHERE MOV.COMPANIA = UN_COMPANIA  
                            AND MOV.ANO      = UN_ANO
                            AND MOV.REGALIAS IN(0)
                            AND MOV.CODIGO = RS.CUENTA
                            AND (MOV.MOVIMIENTO<>0             
                            OR MOV.MAN_AUX_FUE<>0            
                            OR MOV.MAN_AUX_GEN<>0            
                            OR MOV.MAN_AUX_REF<>0           
                            OR MOV.MAN_AUX_TER<>0            
                            OR MOV.MAN_CEN_CTO<>0            
                            )        
                            GROUP BY MOV.COMPANIA,
                            MOV.ANO,
                            MOV.CODIGO,
                            MOV.CODIGOCCPET,
                            MOV.CODIGOUNIDADEJE,
                            MOV.CODIGOFUENTE,
                            MOV.DETALLE_SECTORIAL
                        )
                LOOP 

                    IF RS1.CODIGOCCPET_PAD IS NOT NULL THEN
                        CODIGOCCPET := RS1.CODIGOCCPET_PAD;
                    ELSE
                        CODIGOCCPET := RS1.CODIGOCCPET_MAN;
                    END IF;

                    IF RS1.CODIGOUNIDADEJE_PAD IS NOT NULL THEN
                        CODIGOUNIDADEJE := RS1.CODIGOUNIDADEJE_PAD;
                    ELSE
                        CODIGOUNIDADEJE := RS1.CODIGOUNIDADEJE_MAN;
                    END IF;

                    IF RS1.FUENTE_PAD IS NOT NULL THEN
                        FUENTE_CUIPO := RS1.FUENTE_PAD;
                    ELSE
                        FUENTE_CUIPO := RS1.FUENTE_MAN;
                    END IF;
                    
                    IF RS1.DET_SECTORIAL_PAD IS NOT NULL THEN
                        DETALLE_SECTORIAL := RS1.DET_SECTORIAL_PAD;
                    ELSE
                        DETALLE_SECTORIAL := RS1.DET_SECTORIAL_MAN;
                    END IF;

                    MI_CAMPOS:= 'CODIGO_CCPET       ='''|| CODIGOCCPET ||'''
                                ,CODIGOUNIDADEJE    ='''|| CODIGOUNIDADEJE ||'''
                                ,FUENTE_CUIPO       ='''|| FUENTE_CUIPO ||'''
                                ,DETALLE_SECTORIAL  ='''|| DETALLE_SECTORIAL ||'''
                                ';

                    MI_CONDICION:='COMPANIA         ='''|| UN_COMPANIA ||'''
                                    AND ANO         ='|| RS.ANO ||'
                                    AND TIPO_CPTE   ='''|| RS.TIPO_CPTE ||'''
                                    AND COMPROBANTE ='|| RS.COMPROBANTE||'
                                    AND CUENTA      ='''|| RS.CUENTA||'''
                                ';

                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'DETALLE_COMPROBANTE_PPTAL'
                                                            ,UN_ACCION    => 'M'
                                                            ,UN_CAMPOS    => MI_CAMPOS
                                                            ,UN_CONDICION => MI_CONDICION);
                END LOOP;
                                            
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        
        END;
        
        END LOOP ;
        
        MI_PARAMETRO1    := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA =>UN_COMPANIA
                                                , UN_NOMBRE =>'HEREDA FUENTE CUIPO DESDE FUENTE DE RECURSO'
                                                , UN_MODULO =>PCK_DATOS.MODULOPRESUPUESTO
                                                , UN_FECHA_PAR=>SYSDATE ), 'NO');
        IF MI_PARAMETRO1 =  'SI' THEN

        BEGIN

            FOR RS IN  (SELECT DCP.COMPANIA, DCP.ANO, DCP.TIPO_CPTE, DCP.COMPROBANTE,
                        DCP.FUENTE_RECURSO, FR.EQUIVALENTECUIPO
                        FROM DETALLE_COMPROBANTE_PPTAL DCP 
                        INNER JOIN  FUENTE_RECURSOS FR
                        ON DCP.COMPANIA =  FR.COMPANIA
                        AND DCP.ANO =  FR.ANO
                        AND DCP.FUENTE_RECURSO =  FR.CODIGO
                        WHERE DCP.COMPANIA =  UN_COMPANIA
                        AND DCP.ANO  = UN_ANO
                        AND DCP.TIPO_CPTE  = UN_TIPO
                        AND DCP.COMPROBANTE  = UN_NUMERO
                        GROUP BY DCP.COMPANIA, DCP.ANO, DCP.TIPO_CPTE, DCP.COMPROBANTE,
                        DCP.FUENTE_RECURSO, FR.EQUIVALENTECUIPO
                        )
            LOOP

            MI_CAMPOS:= 'FUENTE_CUIPO     ='''|| RS.EQUIVALENTECUIPO ||'''
                        ';

            MI_CONDICION:='COMPANIA             ='''|| UN_COMPANIA ||'''
                            AND ANO             ='|| RS.ANO ||'
                            AND TIPO_CPTE       ='''|| RS.TIPO_CPTE ||'''
                            AND COMPROBANTE     ='|| RS.COMPROBANTE||'
                            AND FUENTE_RECURSO  ='''|| RS.FUENTE_RECURSO||'''
                            ';
            
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'DETALLE_COMPROBANTE_PPTAL'
                                                            ,UN_ACCION    => 'M'
                                                            ,UN_CAMPOS    => MI_CAMPOS
                                                            ,UN_CONDICION => MI_CONDICION);
                                                        
            END LOOP;
                                            
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;
                        
        END IF;

      /*INI_CC2086 MPEREZ Se actualiza lacuenta pptal en el detalle del comprobante contable*/
      BEGIN
        FOR RS IN  (SELECT DETALLE_COMPROBANTE_PPTAL.COMPANIA, 
                    DETALLE_COMPROBANTE_PPTAL.ANO, 
                    DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE, 
                    DETALLE_COMPROBANTE_PPTAL.COMPROBANTE, 
                    DETALLE_COMPROBANTE_PPTAL.CUENTA RUBRO,
                    DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO,
                    PLAN_PPTAL_CUENTACNT.CUENTA_CONTABLE
                    FROM DETALLE_COMPROBANTE_PPTAL
                    INNER JOIN PLAN_PPTAL_CUENTACNT
                    ON DETALLE_COMPROBANTE_PPTAL.COMPANIA = PLAN_PPTAL_CUENTACNT.COMPANIA 
                    AND DETALLE_COMPROBANTE_PPTAL.ANO = PLAN_PPTAL_CUENTACNT.ANO
                    AND DETALLE_COMPROBANTE_PPTAL.CUENTA = PLAN_PPTAL_CUENTACNT.RUBRO                    
                    WHERE   DETALLE_COMPROBANTE_PPTAL.COMPANIA    = UN_COMPANIA
                    AND     DETALLE_COMPROBANTE_PPTAL.ANO         = UN_ANO
                    AND     DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE   = UN_TIPO
                    AND     DETALLE_COMPROBANTE_PPTAL.COMPROBANTE = UN_NUMERO
                    GROUP BY DETALLE_COMPROBANTE_PPTAL.COMPANIA, 
                    DETALLE_COMPROBANTE_PPTAL.ANO, 
                    DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE,
                    DETALLE_COMPROBANTE_PPTAL.CUENTA, 
                    DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO,
                    DETALLE_COMPROBANTE_PPTAL.COMPROBANTE, 
                    PLAN_PPTAL_CUENTACNT.CUENTA_CONTABLE
                        )
        LOOP

          MI_CAMPOS:= 'TIPOPPTAL       ='''|| RS.TIPO_CPTE ||'''
                        ,NUMEROPPTAL    ='''|| RS.COMPROBANTE ||'''
                        ,CUENTAPPTAL       ='''|| RS.RUBRO ||'''
                        ,CONSECUTIVOPPTO  ='''|| RS.CONSECUTIVO ||'''
                                ';

          MI_CONDICION:='COMPANIA             ='''|| UN_COMPANIA ||'''
                            AND ANO             ='|| RS.ANO ||'
                            AND TIPO_CPTE       ='''|| RS.TIPO_CPTE ||'''
                            AND COMPROBANTE     ='|| RS.COMPROBANTE||'
                            AND CUENTA  ='''|| RS.CUENTA_CONTABLE||'''
                            ';
            
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'DETALLE_COMPROBANTE_CNT'
                                                            ,UN_ACCION    => 'M'
                                                            ,UN_CAMPOS    => MI_CAMPOS
                                                            ,UN_CONDICION => MI_CONDICION);
                                                        
        END LOOP;
                                            
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
      END;
            
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            
    END;  
 -- Fin - Ticket#7726563
END PR_GENERARCOMPROBANTEPPTAL;

PROCEDURE PR_INSERTARVARIOS 
(
  UN_COMPANIA 		 IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANO 			 IN PCK_SUBTIPOS.TI_ANIO,
  UN_TABLA			 IN VARCHAR2
) AS
    MI_VARIOS VARCHAR2(100);
    MI_CLAVE  VARCHAR2(100);
    MI_ERRORCOD  PLS_INTEGER;
    MI_I     NUMBER; 
    MI_CAMPOS                     PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                    PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION                  PCK_SUBTIPOS.TI_CONDICION;
    MI_ERROR                      PCK_SUBTIPOS.TI_CLAVEVALOR;  
BEGIN


    IF UN_TABLA='FUENTE_RECURSOS' THEN
        MI_VARIOS   := PCK_DATOS.FC_CONS_FUENTE;
        MI_CLAVE    := 'FUENTE';
        MI_ERRORCOD := PCK_ERRORES.ERR_CONTAB_INSFUENTERECURSO;        
        SELECT COUNT(*) 
        INTO MI_I
        FROM FUENTE_RECURSOS
        WHERE COMPANIA = UN_COMPANIA
          AND ANO      = UN_ANO
          AND CODIGO   = MI_VARIOS;
    ELSIF UN_TABLA='REFERENCIA' THEN
        MI_VARIOS   := PCK_DATOS.FC_CONS_REFERENCIA;
        MI_CLAVE    := 'REFERENCIA';
        MI_ERRORCOD := PCK_ERRORES.ERR_CONTAB_INSREFERENCIA;        
        SELECT COUNT(*) 
        INTO MI_I
        FROM REFERENCIA
        WHERE COMPANIA = UN_COMPANIA
          AND ANO      = UN_ANO
          AND CODIGO   = MI_VARIOS;
    ELSIF UN_TABLA='CENTRO_COSTO' THEN
        MI_VARIOS   := PCK_DATOS.FC_CONS_CENTRO;
        MI_CLAVE    := 'CENTRO_COSTO';
        MI_ERRORCOD := PCK_ERRORES.ERR_CONTAB_INSCENTROCOSTO;        
        SELECT COUNT(*) 
        INTO MI_I
        FROM CENTRO_COSTO
        WHERE COMPANIA = UN_COMPANIA
          AND ANO      = UN_ANO
          AND CODIGO   = MI_VARIOS;
    ELSIF UN_TABLA='AUXILIAR' THEN
        MI_VARIOS   := PCK_DATOS.FC_CONS_AUXILIAR;
        MI_CLAVE    := 'AUXILIAR';
        MI_ERRORCOD := PCK_ERRORES.ERR_CONTAB_INSAUXILIAR;        
        SELECT COUNT(*) 
        INTO MI_I
        FROM AUXILIAR
        WHERE COMPANIA = UN_COMPANIA
          AND ANO      = UN_ANO
          AND CODIGO   = MI_VARIOS;
    END  IF;
    IF MI_I=0 THEN
        MI_CAMPOS:= 'COMPANIA,
                     ANO,
                     CODIGO,
                     NOMBRE';
        MI_VALORES:='''' ||  UN_COMPANIA ||''',
                    '    ||  UN_ANO      ||',
                    '''  ||  MI_VARIOS   ||''',
                    ''VARIOS''';
        BEGIN
            BEGIN   
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    =>  UN_TABLA,
                                                        UN_ACCION   =>  'I',
                                                        UN_CAMPOS   =>  MI_CAMPOS,
                                                        UN_VALORES  =>  MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
            MI_ERROR(1).CLAVE := MI_CLAVE;
            MI_ERROR(1).VALOR := MI_VARIOS ||' VARIOS ';
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_REEMPLAZOS => MI_ERROR,
                                       UN_ERROR_COD  => MI_ERRORCOD
                                   );
        END;
    END IF;
END PR_INSERTARVARIOS;

FUNCTION FC_CARGAR_FLUJO_EFEC
/*
    NAME              : FC_CARGAR_FLUJO_EFEC
    AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MIGRADOR     : 07/10/2022
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : CARGA EL FLUJO EFECTIVO EN LA COMFIGURACION DE MOVIMIENTOS DEL FLUO DE CAJA
    @NAME:    cargarFlujoEfectivo
    @METHOD:  POST
    */
(
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CADENA     IN CLOB,
    UN_USUARIO    IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN CLOB
AS
    MI_DATOS_FILA         PCK_SYSMAN_UTL.T_SPLIT;
    MI_DATOS_COLUMNAS     PCK_SYSMAN_UTL.T_SPLIT;
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
    MI_EXISTE             NUMBER;
    CONTADOR              NUMBER:=0;
    MI_RETORNO            CLOB;
    MI_NATURALEZA         PCK_SUBTIPOS.TI_NATURALEZA;
    MI_EXISTENATU         NUMBER;
BEGIN

    MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENA,
                                                 UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);
    <<CREAR_PLAN_PRESUPUESTAL>>
    FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST
    LOOP
        MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => MI_DATOS_FILA(RS),
                                                          UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_COL);
       IF (MI_DATOS_COLUMNAS(7) <> '0')  THEN
            MI_NATURALEZA := 'D';
           ELSE 
            MI_NATURALEZA := 'C';
       END IF;
          
        BEGIN
            SELECT COUNT('X') EXISTE
            INTO MI_EXISTE FROM
            PLAN_FLUJO_EFECTIVO
             WHERE ANO = MI_DATOS_COLUMNAS(1)  
                AND CODIGO = MI_DATOS_COLUMNAS(9);
            EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_EXISTE := 0;
        END;  
        
         BEGIN
            SELECT COUNT('X') EXISTE
            INTO MI_EXISTENATU FROM
            PLAN_FLUJO_EFECTIVO
             WHERE ANO = MI_DATOS_COLUMNAS(1)  
                AND CODIGO = MI_DATOS_COLUMNAS(9)                
                AND NATURALEZA = MI_NATURALEZA;
            EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_EXISTENATU := 0;
        END; 

        CONTADOR:= CONTADOR +1;

        IF (MI_EXISTE = 1 AND MI_EXISTENATU = 1) THEN
            IF (MI_DATOS_COLUMNAS(9) > 0) THEN
                MI_CAMPOS := 'CODIGO_FLUJO_EFECTIVO = ''' || MI_DATOS_COLUMNAS(9) || ''',
                             MODIFIED_BY = ''' || UN_USUARIO || '''';
            ELSE
                MI_CAMPOS := 'CODIGO_FLUJO_EFECTIVO = '' '',
                             MODIFIED_BY = ''' || UN_USUARIO || '''';
            END IF;
        
            MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || ''' 
                             AND ANO = ' || MI_DATOS_COLUMNAS(1) || ' 
                             AND TIPO_CPTE = ''' || MI_DATOS_COLUMNAS(2) || '''   
                             AND COMPROBANTE = ' || MI_DATOS_COLUMNAS(3) || '
                             AND CUENTA = ''' || MI_DATOS_COLUMNAS(4) || '''';
        
            BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>  'DETALLE_COMPROBANTE_CNT', 
                                                       UN_ACCION    =>  'M', 
                                                       UN_CAMPOS    =>  MI_CAMPOS, 
                                                       UN_CONDICION =>  MI_CONDICION );
            EXCEPTION
                WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;
        
        ELSIF MI_EXISTE = 1 AND MI_EXISTENATU = 0 AND MI_NATURALEZA = 'C' THEN
            MI_RETORNO := MI_RETORNO || 'El Código Flujo Efectivo ' || MI_DATOS_COLUMNAS(9) || ' de la Fila ' || TO_CHAR(CONTADOR) ||' No corresponde para el concepto de naturaleza Crédito ' || CHR(13) || CHR(10)|| '/n';
       
        ELSIF MI_EXISTE = 1 AND MI_EXISTENATU = 0 AND MI_NATURALEZA = 'D' THEN
            MI_RETORNO := MI_RETORNO || 'El Código Flujo Efectivo ' || MI_DATOS_COLUMNAS(9) || ' de la Fila ' || TO_CHAR(CONTADOR) ||' No corresponde para el concepto de naturaleza Debito ' || CHR(13) || CHR(10)|| '/n';
                        
        ELSE
            MI_RETORNO := MI_RETORNO || 'El Código Flujo Efectivo ' || MI_DATOS_COLUMNAS(9) || ' de la Fila ' || TO_CHAR(CONTADOR) ||' No Existe, Valide nuevamente la informacion.' || CHR(13) || CHR(10)|| '/n';  
        END IF;

    END LOOP CREAR_PLAN_PRESUPUESTAL;
    MI_RETORNO := MI_RETORNO||' PROCESO TERMINADO';
    RETURN MI_RETORNO;
    
END FC_CARGAR_FLUJO_EFEC;

PROCEDURE PR_MASIVA_COM_PPTAL
/*
   NAME            : PR_MASIVA_COM_PPTAL
   AUTHORS         : CRISTIAN FERNEY SUESCUN BARRERA
   DATE            : 06/01/2026
   MODULO ORIGEN   : CONTABILIDAD
   DESCRIPTION     : Regenera masivamente comprobantes presupuestales eliminando los existentes y recreandolos
                     desde la información contable, garantizando la sincronización entre ambos módulos.
   MODIFIER        :
   DATE MODIFIED   :
   MODIFICATIONS   :
   
    --NAME:    generarMasivoComPptal
    --CC:3221
  */
   (
    UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA,     -- Compania
    UN_TIPO                IN VARCHAR2,                     -- Tipo de comprobante   
    UN_ANIO                IN PCK_SUBTIPOS.TI_ANIO,         -- Anio seleccionado
    UN_MES                 IN PCK_SUBTIPOS.TI_MES,          -- Mes  seleccionado     
    UN_NUMERO_INI          IN PCK_SUBTIPOS.TI_ENTERO_LARGO, -- Numero de comprobante Inicial  
    UN_NUMERO_FIN          IN PCK_SUBTIPOS.TI_ENTERO_LARGO, -- Numero de comprobante Final 
    UN_USUARIO             IN VARCHAR2                      -- Usuario de inicio de sesion
   )
   AS
    MI_TABLA             VARCHAR2(200 CHAR);
    MI_CONSULTA          PCK_SUBTIPOS.TI_MERGEUSING;
    MI_ENLACE            PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_EXISTE            PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_RTA               VARCHAR2(32000 CHAR);
    MI_CUENTADETALLE      PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_CADENAINSERTAR     CLOB DEFAULT ' ';
BEGIN
BEGIN
EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS=''.,''';

    FOR RS1 IN (SELECT DISTINCT 
                C.COMPANIA, C.ANO, C.TIPO, C.NUMERO, C.TERCERO, C.SUCURSAL, 
                C.FECHA, C.DESCRIPCION, C.NRO_DOCUMENTO, C.VLR_DOCUMENTO,
                NVL(CP.CREATED_BY, UN_USUARIO) AS CREATED_BY
                FROM COMPROBANTE_CNT C
                LEFT JOIN COMPROBANTE_PPTAL CP 
                    ON C.COMPANIA = CP.COMPANIA
                    AND C.ANO = CP.ANO
                    AND C.TIPO = CP.TIPO
                    AND C.NUMERO = CP.NUMERO
                WHERE C.COMPANIA = UN_COMPANIA
                AND C.ANO = UN_ANIO
                AND C.MES = UN_MES
                AND C.TIPO = UN_TIPO
                AND C.NUMERO BETWEEN UN_NUMERO_INI AND UN_NUMERO_FIN
                AND (CP.NUMERO IS NULL  
                     OR (CP.DEBITO = 0 AND CP.CREDITO = 0)) 
               )
    LOOP
    
    UPDATE DETALLE_COMPROBANTE_CNT 
    SET EJECUCION_CREDITO = VALOR_CREDITO 
    WHERE COMPANIA = RS1.COMPANIA 
    AND ANO = RS1.ANO 
    AND TIPO_CPTE = RS1.TIPO 
    AND COMPROBANTE = RS1.NUMERO;
    
    DELETE FROM DETALLE_COMPROBANTE_PPTAL 
    WHERE COMPANIA = RS1.COMPANIA 
    AND ANO = RS1.ANO 
    AND TIPO_CPTE = RS1.TIPO 
    AND COMPROBANTE = RS1.NUMERO;
    
    DELETE FROM COMPROBANTE_PPTAL 
    WHERE COMPANIA = RS1.COMPANIA 
    AND ANO = RS1.ANO 
    AND TIPO = RS1.TIPO 
    AND NUMERO = RS1.NUMERO;
    
    BEGIN
        MI_CADENAINSERTAR := '';
        MI_CUENTADETALLE := 0;
        
        FOR RS IN(
            SELECT CONSECUTIVO || ',' || CUENTA || ',' || VALOR || ',' || RUBRO_PPTAL ||  ';' LISTA_CNT
            FROM(
                SELECT D.COMPANIA, D.TIPO_CPTE, D.COMPROBANTE, D.CONSECUTIVO, P.CODIGO CUENTA,
                       CASE WHEN P.NATURALEZA='D' THEN D.VALOR_DEBITO - D.VALOR_CREDITO 
                            ELSE D.VALOR_CREDITO - D.VALOR_DEBITO END VALOR,
                       MIN(C.RUBRO) RUBRO_PPTAL, COUNT(D.COMPANIA) CONTADOR
                FROM DETALLE_COMPROBANTE_CNT D 
                INNER JOIN PLAN_CONTABLE P
                    ON D.COMPANIA = P.COMPANIA
                    AND D.ANO = P.ANO
                    AND D.CUENTA = P.CODIGO
                INNER JOIN PLAN_PPTAL_CUENTACNT C
                    ON P.COMPANIA = C.COMPANIA
                    AND P.ANO = C.ANO
                    AND P.CODIGO = C.CUENTA_CONTABLE
                WHERE D.COMPANIA = RS1.COMPANIA
                AND D.ANO = RS1.ANO
                AND D.TIPO_CPTE = RS1.TIPO
                AND D.COMPROBANTE = RS1.NUMERO
                GROUP BY D.COMPANIA, D.TIPO_CPTE, D.COMPROBANTE, D.CONSECUTIVO, P.CODIGO,
                CASE WHEN P.NATURALEZA='D' THEN D.VALOR_DEBITO - D.VALOR_CREDITO 
                     ELSE D.VALOR_CREDITO - D.VALOR_DEBITO END
                ORDER BY CONSECUTIVO)
        )
        LOOP
            MI_CUENTADETALLE := MI_CUENTADETALLE + 1;
            MI_CADENAINSERTAR := MI_CADENAINSERTAR || TO_CLOB(RS.LISTA_CNT);
        END LOOP;
        
        IF MI_CADENAINSERTAR = '' THEN
            MI_CADENAINSERTAR := ' ';
        END IF;
        
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_CADENAINSERTAR := ' ';
        MI_CUENTADETALLE := 0;
    END;
    
    IF MI_CUENTADETALLE > 0 THEN
        PCK_CONTABILIDAD1.PR_GENERARCOMPROBANTEPPTAL
            (UN_COMPANIA        => RS1.COMPANIA
            ,UN_ANO             => RS1.ANO
            ,UN_TIPO            => RS1.TIPO
            ,UN_NUMERO          => RS1.NUMERO
            ,UN_FECHA           => RS1.FECHA
            ,UN_TERCERO         => RS1.TERCERO
            ,UN_SUCURSAL        => RS1.SUCURSAL
            ,UN_DESCRIPCION     => RS1.DESCRIPCION
            ,UN_NUMERODOC       => RS1.NRO_DOCUMENTO
            ,UN_VALORDOC        => RS1.VLR_DOCUMENTO
            ,UN_TIPOPPTAL       => RS1.TIPO
            ,UN_CADENAINSERTAR  => MI_CADENAINSERTAR
            ,UN_CANTIDAD        => MI_CUENTADETALLE
            ,UN_USUARIO         => RS1.CREATED_BY
            ,UN_DESDEINTERFAZ   => -1  );
    END IF;
    
    COMMIT;
    END LOOP;
    
END;
END PR_MASIVA_COM_PPTAL;

END PCK_CONTABILIDAD1;