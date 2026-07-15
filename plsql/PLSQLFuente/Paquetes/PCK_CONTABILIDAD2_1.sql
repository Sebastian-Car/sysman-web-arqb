create or replace PACKAGE BODY "PCK_CONTABILIDAD2" 
/**@package:  Contabilidad **/
AS
FUNCTION FC_CALCULORETENCIONES(
  /*
    NAME              : FC_CALCULORETENCIONES (En Access, CalculoRetenciones, dentro del c�digo de Comprobante_Cnt)
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : Diego Maldonado
    DATE MIGRADOR     : 04/04/2016
    TIME              : 08:00 AM
    SOURCE MODULE     : Contabilidad - SysmanCT2016.02.06
    MODIFIER          : JUAN CARLOS RODR�GUEZ AM�ZQUITA
    DATE MODIFIED     : 10/08/2017
    TIME              : 01:02 PM
    DESCRIPTION       : Proceso que calcula las retenciones para un comprobante determinado.
    MODIFICATIONS     : Al ingresar el valor de la retenci�n se actualiza el valor del campo CALCULADO. 
                        Esto para que el disparador asociado a la tabla permita modificar el valor de 
                        la retenci�n, independiente del valor del indicador PERMITEMODIFICAR de la tabla RETENCIONES.
                        Aplicaci�n del est�ndar de codificaci�n.
    MODIFIER          : SERGIO ESTEBAN PI�A VARGAS
    TIME              : 16/08/2017
    MODIFICATIONS     : Agrego parametro UN_USUARIO y campos de auditoria al llamar la funcion FC_ACME

    @NAME: calcularRetenciones 
    @METHOD: GET
  */
  UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA, 
  UN_MODULO           IN PCK_SUBTIPOS.TI_MODULO,
  UN_CONSECMENSAJES   IN NUMBER,
  UN_ANO              IN PCK_SUBTIPOS.TI_ANIO,
  UN_FECHA            IN DATE,
  UN_TIPO             IN DETALLE_COMPROBANTE_CNT.TIPO_CPTE%TYPE,
  UN_NUMERO           IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
  UN_AUXILIAR         IN PCK_SUBTIPOS.TI_AUXILIAR,
  UN_TERCERO          IN PCK_SUBTIPOS.TI_TERCERO,
  UN_SUCURSAL         IN PCK_SUBTIPOS.TI_SUCURSAL,
  UN_NOMBRETERCERO    IN PLAN_CONTABLE.NOMBRE%TYPE,
  UN_VALORBASE        IN NUMBER,
  UN_VALORBASEIVA     IN PCK_SUBTIPOS.TI_DOBLE,
  UN_DESCRIPCION      IN DETALLE_COMPROBANTE_CNT.DESCRIPCION%TYPE, --JM 08/11/2024 7801577
  UN_CENTROCOSTO      IN PCK_SUBTIPOS.TI_CENTRO_COSTO,
  UN_NRODOCUMENTO     IN DETALLE_COMPROBANTE_CNT.NRO_DOCUMENTO%TYPE,
  UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO,
  UN_FUENTER          IN VARCHAR2 DEFAULT '0',
  UN_REFERENCIA       IN VARCHAR2 DEFAULT '0',
  UN_CONCEPTO         IN VARCHAR2 DEFAULT '0',
  UN_PROVEEDOR        IN VARCHAR2 DEFAULT '0',
  UN_LIMPIAR_TABLA    IN NUMBER DEFAULT 1)
  RETURN NUMBER
AS
  MI_ERROR_FUN        PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 1;
  MI_FILAS            VARCHAR2(4000 CHAR);
  MI_STRSQL           PCK_SUBTIPOS.TI_STRSQL;
  MI_STRSQL2          PCK_SUBTIPOS.TI_STRSQL;
  MI_VALORB           NUMBER;
  MI_VALORBG          NUMBER;
  MI_VALORRETENER     VARCHAR2(4000 CHAR);
  MI_CODIGO_CUENTA    VARCHAR2(4000 CHAR);
  MI_CUENTA           VARCHAR2(4000 CHAR);
  MI_TIENECONTENIDO   BOOLEAN;
  MI_CONSECUTIVO      DETALLE_COMPROBANTE_CNT.CONSECUTIVO%TYPE;
  MI_STRNAT           VARCHAR2(4000 CHAR);
  MI_CUENTAPPTAL      VARCHAR2(4000 CHAR);
  MI_CUENTAPPTALAUX   VARCHAR2(4000 CHAR);
  MI_DESCRIPCION      VARCHAR2(4000 CHAR);
  MI_CENTROCOSTO      PCK_SUBTIPOS.TI_CENTRO_COSTO;
  MI_RS               SYS_REFCURSOR;
  MI_RSDET            SYS_REFCURSOR;
  MI_RSCDEBITO        RETENCIONES.CUENTA_DEBITO%TYPE;
  MI_RSCCREDITO       RETENCIONES.CUENTA_CREDITO%TYPE;
  MI_RSDETCOMPANIA    PCK_SUBTIPOS.TI_COMPANIA; 
  MI_RSDETANO         PCK_SUBTIPOS.TI_ANIO; 
  MI_RSDETTIPO_CPTE   DETALLE_COMPROBANTE_CNT.TIPO_CPTE%TYPE; 
  MI_RSDETCOMPROBANTE DETALLE_COMPROBANTE_CNT.COMPROBANTE%TYPE; 
  MI_RSDETCONSECUTIVO PCK_SUBTIPOS.TI_CONSECUTIVOCNT;
  MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
  MI_RTA              VARCHAR2(200 CHAR);
  MI_RTAMSG           VARCHAR2(200 CHAR);
  MI_REFERENCIA       PCK_SUBTIPOS.TI_REFERENCIA;
  MI_FUENTE_RECURSO   PCK_SUBTIPOS.TI_FUENTE_RECURSOS;
  MI_MANEJA_REF_FUEN  VARCHAR2(200 CHAR);
  MI_PRORRATEADO      NUMBER;
  MI_VLR_RETENCION    NUMBER;
  MI_SUB_COND         VARCHAR2(200 CHAR);
BEGIN
  IF UN_DESCRIPCION IS NULL THEN
      MI_DESCRIPCION := '';
  ELSE
      MI_DESCRIPCION := UN_DESCRIPCION;
  END IF;
  --
   IF UN_CENTROCOSTO IS NULL THEN
    MI_CENTROCOSTO := PCK_DATOS.FC_CONS_CENTRO();
  ELSE
    MI_CENTROCOSTO := UN_CENTROCOSTO;
  END IF;
  --
            IF PCK_CONTABILIDAD3.GL_VARCAUSACION = 1 THEN
                   BEGIN                                           
                        SELECT NVL(MAX(CONSECUTIVO),0)
                             INTO MI_CONSECUTIVO
                            FROM TEMP_DETALLE_COMPROBANTE_CNT
                           WHERE COMPANIA    = UN_COMPANIA
                             AND ANO         = UN_ANO
                             AND TIPO_CPTE   = UN_TIPO 
                             AND COMPROBANTE = UN_NUMERO;
                      EXCEPTION 
                        WHEN NO_DATA_FOUND THEN
                          MI_CONSECUTIVO := 0;
                   END;
                ELSE
                   IF PCK_CONTABILIDAD3.GL_VARCAUSACION = 0 OR PCK_CONTABILIDAD3.GL_VARCAUSACION = 2 THEN
                       BEGIN
                           SELECT NVL(MAX(CONSECUTIVO),0)
                             INTO MI_CONSECUTIVO
                            FROM DETALLE_COMPROBANTE_CNT
                           WHERE COMPANIA    = UN_COMPANIA
                             AND ANO         = UN_ANO
                             AND TIPO_CPTE   = UN_TIPO 
                             AND COMPROBANTE = UN_NUMERO;
                          EXCEPTION 
                            WHEN NO_DATA_FOUND THEN
                              MI_CONSECUTIVO := 0;
                       END;
                   END IF;
            END IF;
  IF MI_CONSECUTIVO = 0 THEN
    MI_TIENECONTENIDO := FALSE;
  ELSE
    MI_TIENECONTENIDO := TRUE;
  END IF;
  --
  
  IF PCK_CONTABILIDAD3.GL_VARCAUSACION <> 0 THEN 
    MI_SUB_COND := 'AND RETENCIONES.ALEY1819 = 0 AND COMPROBANTE_CNTRETENCION.CONCEPTO='|| UN_CONCEPTO ||' AND COMPROBANTE_CNTRETENCION.REFERENCIADO='|| UN_REFERENCIA ||' ';
  ELSE 
    MI_SUB_COND := '';
  END IF;
  
  MI_STRSQL := ' SELECT 
                  RETENCIONES.CUENTA_DEBITO, 
                  RETENCIONES.CUENTA_CREDITO' ||
              ' FROM   COMPROBANTE_CNTRETENCION LEFT JOIN RETENCIONES ON 
                  COMPROBANTE_CNTRETENCION.CODIGORETENCION = RETENCIONES.CODIGO 
                  AND COMPROBANTE_CNTRETENCION.ANO = RETENCIONES.ANO 
                  AND COMPROBANTE_CNTRETENCION.TIPORETENCION = RETENCIONES.TIPO 
                  AND COMPROBANTE_CNTRETENCION.COMPANIA = RETENCIONES.COMPANIA ' ||
              ' WHERE  COMPROBANTE_CNTRETENCION.COMPANIA=''' || UN_COMPANIA || ''' 
                  AND COMPROBANTE_CNTRETENCION.ANO=' || UN_ANO || ' 
                  AND COMPROBANTE_CNTRETENCION.TIPO=''' || UN_TIPO || ''' 
                  AND COMPROBANTE_CNTRETENCION.NUMERO=' || UN_NUMERO || ' ' ||MI_SUB_COND||
              ' ORDER  BY COMPROBANTE_CNTRETENCION.COMPANIA,
                  COMPROBANTE_CNTRETENCION.ANO,
                  COMPROBANTE_CNTRETENCION.TIPO,
                  COMPROBANTE_CNTRETENCION.NUMERO,
                  COMPROBANTE_CNTRETENCION.TIPORETENCION,
                  COMPROBANTE_CNTRETENCION.CODIGORETENCION';
  OPEN MI_RS FOR MI_STRSQL;
  LOOP
    IF MI_RS%NOTFOUND THEN
      --"No se han codificado retenciones para este comprobante."
      -- NOTA: No se envía mensaje informativo, para no interrumpir el proceso.
      RETURN -2;
    END IF;
    EXIT WHEN 0 = 0;
  END LOOP;
  CLOSE MI_RS;
  --
  IF MI_TIENECONTENIDO = TRUE THEN
    MI_STRSQL2 := NULL;
    OPEN MI_RS FOR MI_STRSQL;
    LOOP
      FETCH MI_RS INTO MI_RSCDEBITO, MI_RSCCREDITO;
      EXIT
    WHEN MI_RS%NOTFOUND;
      IF MI_STRSQL2 IS NOT NULL THEN
        MI_STRSQL2  := MI_STRSQL2 || ',';
      END IF;
      MI_STRSQL2 := MI_STRSQL2 || ''''||MI_RSCDEBITO||''','''||MI_RSCCREDITO||'''';
    END LOOP;
    CLOSE MI_RS;
    --
    IF MI_STRSQL2 IS NOT NULL THEN

                  IF PCK_CONTABILIDAD3.GL_VARCAUSACION = 1 THEN 
             OPEN MI_RSDET FOR ' SELECT TEMP_DETALLE_COMPROBANTE_CNT.COMPANIA, 
                    TEMP_DETALLE_COMPROBANTE_CNT.ANO, 
                    TIPO_CPTE, 
                    COMPROBANTE, 
                    CONSECUTIVO '||
                ' FROM   TEMP_DETALLE_COMPROBANTE_CNT 
                  LEFT JOIN V_PLAN_CONTABLE 
                      ON TEMP_DETALLE_COMPROBANTE_CNT.COMPANIA  = V_PLAN_CONTABLE.COMPANIA 
                      AND TEMP_DETALLE_COMPROBANTE_CNT.ANO      = V_PLAN_CONTABLE.ANO 
                      AND TEMP_DETALLE_COMPROBANTE_CNT.CUENTA   = V_PLAN_CONTABLE.CODIGO' ||
                ' WHERE  TEMP_DETALLE_COMPROBANTE_CNT.COMPANIA  =''' || UN_COMPANIA || ''' 
                      AND TEMP_DETALLE_COMPROBANTE_CNT.ANO      =' || UN_ANO || ' 
                      AND TIPO_CPTE     =''' || UN_TIPO || ''' 
                      AND COMPROBANTE   =' || UN_NUMERO ||'
                      AND CUENTA IN (' || MI_STRSQL2 || ')' ||
                  ' ORDER BY TEMP_DETALLE_COMPROBANTE_CNT.COMPANIA,
                      TEMP_DETALLE_COMPROBANTE_CNT.ANO,
                      TIPO_CPTE,COMPROBANTE,
                      CONSECUTIVO';     
                ELSE 
                    IF PCK_CONTABILIDAD3.GL_VARCAUSACION = 0 OR PCK_CONTABILIDAD3.GL_VARCAUSACION = 2 THEN																									  
      OPEN MI_RSDET FOR ' SELECT DETALLE_COMPROBANTE_CNT.COMPANIA, 
                            DETALLE_COMPROBANTE_CNT.ANO, 
                            TIPO_CPTE, 
                            COMPROBANTE, 
                            CONSECUTIVO '||
                        ' FROM   DETALLE_COMPROBANTE_CNT 
                          LEFT JOIN V_PLAN_CONTABLE 
                              ON DETALLE_COMPROBANTE_CNT.COMPANIA  = V_PLAN_CONTABLE.COMPANIA 
                              AND DETALLE_COMPROBANTE_CNT.ANO      = V_PLAN_CONTABLE.ANO 
                              AND DETALLE_COMPROBANTE_CNT.CUENTA   = V_PLAN_CONTABLE.CODIGO' ||
                        ' WHERE  DETALLE_COMPROBANTE_CNT.COMPANIA  =''' || UN_COMPANIA || ''' 
                              AND DETALLE_COMPROBANTE_CNT.ANO      =' || UN_ANO || ' 
                              AND TIPO_CPTE     =''' || UN_TIPO || ''' 
                              AND COMPROBANTE   =' || UN_NUMERO ||'
                              AND CUENTA IN (' || MI_STRSQL2 || ')' ||
                          ' ORDER BY DETALLE_COMPROBANTE_CNT.COMPANIA,
                              DETALLE_COMPROBANTE_CNT.ANO,
                              TIPO_CPTE,COMPROBANTE,
                              CONSECUTIVO';
                    END IF;
                END IF; 						   
	LOOP
        FETCH MI_RSDET 
        INTO MI_RSDETCOMPANIA, 
             MI_RSDETANO, 
             MI_RSDETTIPO_CPTE, 
             MI_RSDETCOMPROBANTE, 
             MI_RSDETCONSECUTIVO;
               EXIT WHEN MI_RSDET%NOTFOUND;
               
                  IF PCK_CONTABILIDAD3.GL_VARCAUSACION = 1 THEN 
                    MI_TABLA   := 'TEMP_DETALLE_COMPROBANTE_CNT';
                ELSE 
                    IF PCK_CONTABILIDAD3.GL_VARCAUSACION = 0 OR PCK_CONTABILIDAD3.GL_VARCAUSACION = 2 THEN
                        MI_TABLA   := 'DETALLE_COMPROBANTE_CNT';
                    END IF;
                END IF; 
	IF UN_LIMPIAR_TABLA <> 0 THEN									
       DECLARE
          MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
        BEGIN
          MI_CONDICION := ' COMPANIA          = '''|| MI_RSDETCOMPANIA||''' 
                            AND ANO           = ' || MI_RSDETANO ||' 
                            AND TIPO_CPTE     = ''' || MI_RSDETTIPO_CPTE ||'''
                            AND COMPROBANTE   =' || MI_RSDETCOMPROBANTE || 
                            ' AND CONSECUTIVO = ' || MI_RSDETCONSECUTIVO;
          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA, 
                                      UN_ACCION    => 'E',
                                      UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_LIMPIAR_DETALLE_CNT
          );
        END;
	END IF;		
      END LOOP;
      CLOSE MI_RSDET;
    END IF;
  END IF;
  --Si hay retenciones entra a calcular los valores
  <<CALCULAVALORES>>
  FOR MI_RS IN (
     SELECT COMPROBANTE_CNTRETENCION.TIPORETENCION, 
            COMPROBANTE_CNTRETENCION.CODIGORETENCION, 
            COMPROBANTE_CNTRETENCION.VALOR,
            COMPROBANTE_CNTRETENCION.VALORBASE,
            COMPROBANTE_CNTRETENCION.PORCIVA, 
            RETENCIONES.CUENTA_DEBITO, 
            RETENCIONES.CUENTA_CREDITO, 
            RETENCIONES.PCT_BASE, 
            RETENCIONES.LIMITE_INF, 
            RETENCIONES.PCT_APLICAR, 
            RETENCIONES.VALOR_APLICAR, 
            RETENCIONES.FACTORREDONDEO,
            RETENCIONES.PERMITEMODIFICAR,
			CASE WHEN PCK_CONTABILIDAD3.GL_VARCAUSACION <> 0 THEN COMPROBANTE_CNTRETENCION.CENTROCOSTO ELSE RETENCIONES.CENTRO_COSTO END CENTRO_COSTO,
            RETENCIONES.COD_AUXILIAR,
			CASE WHEN PCK_CONTABILIDAD3.GL_VARCAUSACION <> 0 THEN COMPROBANTE_CNTRETENCION.REFERENCIADO ELSE RETENCIONES.COD_REFERENCIA END COD_REFERENCIA,
			RETENCIONES.COD_FUENTER
       FROM COMPROBANTE_CNTRETENCION 
            LEFT JOIN RETENCIONES 
             ON COMPROBANTE_CNTRETENCION.CODIGORETENCION = RETENCIONES.CODIGO 
            AND COMPROBANTE_CNTRETENCION.ANO = RETENCIONES.ANO 
            AND COMPROBANTE_CNTRETENCION.TIPORETENCION = RETENCIONES.TIPO 
            AND COMPROBANTE_CNTRETENCION.COMPANIA = RETENCIONES.COMPANIA 
      WHERE COMPROBANTE_CNTRETENCION.COMPANIA=UN_COMPANIA
        AND COMPROBANTE_CNTRETENCION.ANO= UN_ANO 
        AND COMPROBANTE_CNTRETENCION.TIPO= UN_TIPO 
        AND COMPROBANTE_CNTRETENCION.NUMERO= UN_NUMERO 
		AND  COMPROBANTE_CNTRETENCION.CONCEPTO = CASE WHEN PCK_CONTABILIDAD3.GL_VARCAUSACION <> 0 THEN UN_CONCEPTO ELSE '0' END 
        AND COMPROBANTE_CNTRETENCION.CENTROCOSTO = CASE WHEN PCK_CONTABILIDAD3.GL_VARCAUSACION <> 0 THEN UN_CENTROCOSTO ELSE '0' END
        AND COMPROBANTE_CNTRETENCION.REFERENCIADO = CASE WHEN PCK_CONTABILIDAD3.GL_VARCAUSACION <> 0 THEN UN_REFERENCIA ELSE '0' END
        AND COMPROBANTE_CNTRETENCION.PROVEEDOR = CASE WHEN PCK_CONTABILIDAD3.GL_VARCAUSACION <> 0 THEN UN_PROVEEDOR ELSE '0' END
        AND RETENCIONES.ALEY1819 = 0
		ORDER BY COMPROBANTE_CNTRETENCION.COMPANIA,
            COMPROBANTE_CNTRETENCION.ANO,
            COMPROBANTE_CNTRETENCION.TIPO,
            COMPROBANTE_CNTRETENCION.NUMERO,
            COMPROBANTE_CNTRETENCION.TIPORETENCION,
            COMPROBANTE_CNTRETENCION.CODIGORETENCION) 
  LOOP
     --
    MI_CUENTA := '';
    --Coge el respectivo valor base, dependiendo si es IVA o no.
    --Modificacion al valor base del iva por AdrianaS el 04/09/2007, Por manejo de iva segun Corporinoquia, que tienen 2 cuentas iva y dividen 50 y 50%, se crea el siguiente parametro
    /*DM: En Access, el parámetro MANEJA PROCESO ESPECIAL RETENCIONES IVA, no estaba haciendo nada, por lo que se removieron los condicionales.*/
    MI_VALORB := NVL(MI_RS.VALORBASE, 0);
    MI_VALORBG := MI_VALORB;
    --Empieza con el proceso de cálculo de retenciones.
    IF MI_VALORB <= NVL(MI_RS.LIMITE_INF, 0) THEN
        CONTINUE;
    END IF;
    IF NVL(MI_RS.VALOR_APLICAR, 0) = 0 THEN
        MI_VALORB := PCK_SYSMAN_UTL.FC_ROUND((MI_VALORB * NVL(MI_RS.PCT_BASE,0))/100,2);
        MI_VALORRETENER := PCK_SYSMAN_UTL.FC_ROUND((MI_VALORB * NVL(MI_RS.PCT_APLICAR,0))/100,2);
    ELSE
        MI_VALORRETENER := NVL(MI_RS.VALOR_APLICAR, 0);
    END IF;
    IF (NVL(MI_RS.FACTORREDONDEO, 0) NOT IN (0)) THEN
        MI_VALORRETENER := PCK_SYSMAN_UTL.FC_ROUND(TRUNC(MI_VALORRETENER / NVL(MI_RS.FACTORREDONDEO,0)+0.50001) * NVL(MI_RS.FACTORREDONDEO,0),2);
    END IF;
    IF MI_RS.CUENTA_CREDITO IS NOT NULL THEN
        MI_CODIGO_CUENTA := NVL(MI_RS.CUENTA_CREDITO, '');
    ELSIF MI_RS.CUENTA_DEBITO IS NOT NULL THEN
        MI_CODIGO_CUENTA := NVL(MI_RS.CUENTA_DEBITO, '');
    ELSE
        MI_TABLA    := 'TEMP_CALCULO_RETENCIONES';
        MI_CAMPOS   := 'CODIGO,ORDEN,MENSAJE';
        MI_VALORES  := UN_CONSECMENSAJES||' , '||
                       PCK_CONTABILIDAD2.FC_ORDENMENSAJES(UN_CONSECMENSAJES)||',
                       ''El código de retención ' || NVL(MI_RS.TIPORETENCION,'') || ' - ' || NVL(MI_RS.CODIGORETENCION,'') || ' NO tiene cuenta contable relacionada.''';

        MI_RTAMSG := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA, 
                                       UN_ACCION  => 'I', 
                                       UN_CAMPOS  => MI_CAMPOS,
                                       UN_VALORES => MI_VALORES
                                      ) ;
        CONTINUE;
    END IF;
    --
    MI_STRNAT := SUBSTR('CDCCCDDDDC', TO_NUMBER(LPAD(2234234,1))+1 ,1);
    IF NVL(MI_RS.PERMITEMODIFICAR, 0) = 0 AND NVL(MI_RS.VALOR,0) <> 0 THEN
        MI_VALORRETENER := NVL(MI_RS.VALOR, 0);
    END IF;
    IF MI_VALORRETENER <> 0 THEN
      MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
      --AQUI SE AGREGA EL PARAMETRO PARA TRAER LA RETENCION CON EL CENTRO DE COSTO CODIFICADO EN LA CODIFICACION DE RETENCIONES
      IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA, 
                               'MANEJA RETENCIONES POR CENTRO DE COSTO', 
                               UN_MODULO, 
                               SYSDATE) = 'SI' THEN
          MI_CENTROCOSTO := MI_RS.CENTRO_COSTO;
      END IF;
      IF PCK_SYSMAN_UTL.FC_PAR(''''||UN_COMPANIA||'''', 
                               'MANEJA DISTRIBUCION DE BANCOS POR CENTRO DE COSTO EN EGRESO', 
                               UN_MODULO, 
                               SYSDATE) = 'SI' THEN
          MI_RTA := PCK_CONTABILIDAD2.FC_DISTRIBUIRCENTROS(UN_COMPANIA, UN_CONSECMENSAJES, UN_ANO, 
                                                           UN_AUXILIAR, UN_TERCERO, UN_SUCURSAL, 
                                                           MI_VALORRETENER, MI_CODIGO_CUENTA, FALSE, 
                                                           TRUE, UN_NUMERO, UN_TIPO, UN_FECHA, UN_DESCRIPCION,
                                                           UN_USUARIO);
      END IF;
	  
	   MI_MANEJA_REF_FUEN := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                              UN_NOMBRE    => 'MANEJA REFERENCIA Y FUENTE EN RETENCIONES',
                                              UN_MODULO    => PCK_DATOS.FC_MODULOCONTABILIDAD,
                                              UN_FECHA_PAR => SYSDATE);
	  
	  IF MI_MANEJA_REF_FUEN = 'SI' THEN
		MI_FUENTE_RECURSO := MI_RS.COD_FUENTER;
        MI_REFERENCIA     := MI_RS.COD_REFERENCIA;
		
	  ELSE
		MI_FUENTE_RECURSO := NVL(UN_FUENTER,'99999999999999999999');
        MI_REFERENCIA     := NVL(UN_REFERENCIA, '99999999999999999999');
	  END IF;
      --
      BEGIN
      SELECT RUBRO
        INTO MI_CUENTAPPTALAUX
        FROM PLAN_PPTAL_CUENTACNT
       WHERE COMPANIA = UN_COMPANIA
         AND ANO      = UN_ANO
         AND CUENTA_CONTABLE   = MI_CODIGO_CUENTA
         AND ROWNUM = 1;
         EXCEPTION WHEN NO_DATA_FOUND THEN
         MI_CUENTAPPTALAUX := '';
      END;
      
      IF MI_CUENTAPPTALAUX IS NOT NULL THEN 
          MI_CUENTAPPTAL := MI_CUENTAPPTALAUX;
      END IF;

      BEGIN
      SELECT PRORRATEADO
      INTO MI_PRORRATEADO
            FROM RETENCIONES
       WHERE COMPANIA = UN_COMPANIA
         AND ANO = UN_ANO
         AND TIPO = MI_RS.TIPORETENCION
         AND CODIGO = MI_RS.CODIGORETENCION;
         EXCEPTION WHEN NO_DATA_FOUND THEN
         MI_PRORRATEADO := 0;
      END;
      
      IF MI_PRORRATEADO NOT IN (0) THEN
      
      <<PRORRATEO>>
       FOR RS IN (SELECT COMPANIA, 
            TIPO, 
            ANO, 
            CODIGO, 
            CUENTA_CREDITO, 
            PCT_APLICAR
            FROM PRORRATEADOS
            WHERE COMPANIA = UN_COMPANIA
            AND ANO = UN_ANO
            AND TIPO = MI_RS.TIPORETENCION
            AND CODIGO = MI_RS.CODIGORETENCION) LOOP
            
            
      MI_VLR_RETENCION := PCK_SYSMAN_UTL.FC_ROUND((MI_VALORRETENER * NVL(RS.PCT_APLICAR,0))/100,2);
      BEGIN
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
                      TERCERO,
                      SUCURSAL,
                      AUXILIAR,
                      DESCRIPCION,
                      CUENTAPPTAL,
                      EJECUCION_DEBITO,
                      EJECUCION_CREDITO,
                      BASE_IVA,
                      NRO_DOCUMENTO,
                      CREATED_BY,
                      DATE_CREATED,
					  FUENTE_RECURSO,
                      REFERENCIA';
                      
        MI_VALORES:=' ''' || UN_COMPANIA || ''',
                        ' || UN_ANO || ',
                      ''' || UN_TIPO || ''',
                        ' || UN_NUMERO || ',
                        ' || MI_CONSECUTIVO || ',
                      ''' || RS.CUENTA_CREDITO || ''',
                      ''' || TO_DATE('' || UN_FECHA || '','DD/MM/YYYY') || ''',
                      ''' || MI_STRNAT || ''',
                        ' || 0 || ',
                        ' || MI_VLR_RETENCION|| ',
                        ' || MI_VALORBG || ',
                      ''' || (CASE WHEN MI_CENTROCOSTO IS NULL OR MI_CENTROCOSTO = '' THEN NVL(UN_CENTROCOSTO,'9999999999999999') ELSE MI_CENTROCOSTO  END)||''',
                      ''' || NVL(UN_TERCERO, '99999999999') || ''',
                      ''' || NVL(UN_SUCURSAL,'999') || ''',
                        ' || (CASE WHEN MI_RS.COD_AUXILIAR IS NULL OR MI_RS.COD_AUXILIAR = '' THEN '9999999999999999' ELSE MI_RS.COD_AUXILIAR  END)||',
                      ''' || MI_DESCRIPCION || ''',
                      ''' || NVL(MI_CUENTAPPTAL,'') || ''',
                        ' || 0 || ',
                        ' || MI_VLR_RETENCION || ',
                        ' || NVL(UN_VALORBASEIVA,0) || ',
                      ''' || UN_NRODOCUMENTO || ''',
                      ''' || UN_USUARIO || ''',
                      SYSDATE,
					  ''' || MI_FUENTE_RECURSO || ''',
                      ''' || MI_REFERENCIA || ''' ';
              MI_CONSECUTIVO := MI_CONSECUTIVO + 1;        
              MI_TABLA   := 'DETALLE_COMPROBANTE_CNT';


        MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA,
                                    UN_ACCION => 'I',
                                    UN_CAMPOS => MI_CAMPOS,
                                    UN_VALORES => MI_VALORES);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD => SQLCODE,
          UN_TABLAERROR => MI_TABLA,
          UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_INS_RETENCIONES
        );
      END;
      
      
      END LOOP;
 
      ELSE

      BEGIN
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
                      TERCERO,
                      SUCURSAL,
                      AUXILIAR,
                      DESCRIPCION,
                      CUENTAPPTAL,
                      EJECUCION_DEBITO,
                      EJECUCION_CREDITO,
                      BASE_IVA,
                      NRO_DOCUMENTO,
                      CREATED_BY,
                      DATE_CREATED,
					  FUENTE_RECURSO,
                      REFERENCIA,
                      CONCEPTO,
                      PROVEEDOR';
        MI_VALORES:=' ''' || UN_COMPANIA || ''',
                        ' || UN_ANO || ',
                      ''' || UN_TIPO || ''',
                        ' || UN_NUMERO || ',
                        ' || MI_CONSECUTIVO || ',
                      ''' || MI_CODIGO_CUENTA || ''',
                      ''' || TO_DATE('' || UN_FECHA || '','DD/MM/YYYY') || ''',
                      ''' || MI_STRNAT || ''',
                        ' || (CASE WHEN(MI_RS.CUENTA_CREDITO IS NOT NULL) THEN 0 ELSE MI_VALORRETENER END)|| ',
                        ' || (CASE WHEN(MI_RS.CUENTA_DEBITO IS NOT NULL) THEN 0 ELSE MI_VALORRETENER END)|| ',
                        ' || MI_VALORBG || ',
                      ''' || (CASE WHEN MI_CENTROCOSTO IS NULL OR MI_CENTROCOSTO = '' THEN NVL(UN_CENTROCOSTO,'9999999999999999') ELSE MI_CENTROCOSTO  END)||''',
                      ''' || NVL(UN_TERCERO, '99999999999') || ''',
                      ''' || NVL(UN_SUCURSAL,'999') || ''',
                      ''' || (CASE WHEN MI_RS.COD_AUXILIAR IS NULL OR MI_RS.COD_AUXILIAR = '' THEN '9999999999999999' ELSE MI_RS.COD_AUXILIAR  END)||''',
                      ''' || MI_DESCRIPCION || ''',
                      ''' || NVL(MI_CUENTAPPTAL,'') || ''',
                        ' || (CASE WHEN (MI_RS.CUENTA_CREDITO IS NOT NULL) THEN 0 ELSE MI_VALORRETENER END) || ',
                        ' || (CASE WHEN (MI_RS.CUENTA_DEBITO IS NOT NULL) THEN 0 ELSE MI_VALORRETENER END) || ',
                        ' || NVL(UN_VALORBASEIVA,0) || ',
                      ''' || UN_NRODOCUMENTO || ''',
                      ''' || UN_USUARIO || ''',
                      SYSDATE,
					  ''' || MI_FUENTE_RECURSO || ''',
                      ''' || (CASE WHEN PCK_CONTABILIDAD3.GL_VARCAUSACION =1 OR PCK_CONTABILIDAD3.GL_VARCAUSACION =2 THEN UN_REFERENCIA ELSE MI_REFERENCIA END ) || ''' ,
                      ''' || NVL(UN_CONCEPTO,'0') || ''',
                      ''' || NVL(UN_PROVEEDOR,'0') || '''';
                      
       IF PCK_CONTABILIDAD3.GL_VARCAUSACION = 1 THEN 
                    MI_TABLA   := 'TEMP_DETALLE_COMPROBANTE_CNT';
                ELSE 
                    IF PCK_CONTABILIDAD3.GL_VARCAUSACION = 0 OR PCK_CONTABILIDAD3.GL_VARCAUSACION = 2 THEN
                        MI_TABLA   := 'DETALLE_COMPROBANTE_CNT';
                    END IF;
                END IF; 

        MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA, 
                                    UN_ACCION => 'I', 
                                    UN_CAMPOS => MI_CAMPOS, 
                                    UN_VALORES => MI_VALORES);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD => SQLCODE,
          UN_TABLAERROR => MI_TABLA,
          UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_INS_RETENCIONES
        ); 
      END;
    END IF;
    END IF;
    --
IF PCK_CONTABILIDAD3.GL_VARCAUSACION = 0 OR PCK_CONTABILIDAD3.GL_VARCAUSACION = 2 THEN    
    DECLARE
      MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
    BEGIN
      MI_CAMPOS := 'VALOR         = '|| MI_VALORRETENER || ', 
                    CALCULADO     = -1,
                    MODIFIED_BY   = '''|| UN_USUARIO ||''',
                    DATE_MODIFIED = SYSDATE
                    ';
      MI_CONDICION := 'COMPROBANTE_CNTRETENCION.COMPANIA              =''' ||UN_COMPANIA||'''
                        AND COMPROBANTE_CNTRETENCION.ANO              =   '||UN_ANO ||'
                        AND COMPROBANTE_CNTRETENCION.TIPO             = '''||UN_TIPO ||'''
                        AND COMPROBANTE_CNTRETENCION.NUMERO           =   '||UN_NUMERO||'
                        AND COMPROBANTE_CNTRETENCION.TIPORETENCION    = '''||MI_RS.TIPORETENCION||'''
                        AND COMPROBANTE_CNTRETENCION.CODIGORETENCION  = '''||MI_RS.CODIGORETENCION||'''';

      MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'COMPROBANTE_CNTRETENCION', 
                                  UN_ACCION    => 'M', 
                                  UN_CAMPOS    => MI_CAMPOS, 
                                  UN_CONDICION => MI_CONDICION );
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD    => SQLCODE,
        UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_ACT_VALOR_RETENCION
      );
    END;
   
   END IF;			   
  END LOOP CALCULAVALORES;
  RETURN 1;
END FC_CALCULORETENCIONES;

   FUNCTION FC_DISTRIBUIRCENTROS
   /*
       NAME              : FC_DISTRIBUIRCENTROS (En Access - distribuircentros dentro del módulo distribución de Contabilidad)
       AUTHORS           : SYSMAN  SAS
       AUTHOR MIGRACION  : Diego Maldonado
       DATE MIGRADOR     : 05/04/2016
       TIME              : 02:00 PM
       SOURCE MODULE     : Contabilidad - SysmanCT2016.02.06
       MODIFIER          : SERGIO ESTEBAN PIÑA VARGAS
       DATE MODIFIED     : 16/08/2017
       MODIFIED          : Agrego parámetro UN_USUARIO y campos de auditoria en los llamados a la función FC_ACME
       TIME              : 
       DESCRIPTION       : Proceso para distribuir en porcentajes el "ValorADistribuir ingresado por parámetro, en los centros de costo.
       @NAME:  distribuirCentrosDeCosto
       @METHOD:  GET
       */
   (
        UN_COMPANIA 		    IN PCK_SUBTIPOS.TI_COMPANIA,
        UN_CONSECMENSAJES   IN NUMBER,
        UN_ANO  			      IN PCK_SUBTIPOS.TI_ANIO,
        UN_AUXILIAR 		    IN PCK_SUBTIPOS.TI_AUXILIAR,
        UN_TERCERO 			    IN PCK_SUBTIPOS.TI_TERCERO,
        UN_SUCURSAL 		    IN PCK_SUBTIPOS.TI_SUCURSAL,
      	UN_VALORDISTRIBUIR 	IN NUMBER, 
        UN_CUENTA 			    IN PCK_SUBTIPOS.TI_CODIGOCONTA, 
        UN_DEBITO 			    IN BOOLEAN, 
        UN_CREDITO 			    IN BOOLEAN,
        UN_NUMERO 			    IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
        UN_TIPO   			    IN DETALLE_COMPROBANTE_CNT.TIPO_CPTE%TYPE,
        UN_FECHA  			    IN DATE, 
        UN_DESCRIPCION 		  IN DETALLE_COMPROBANTE_CNT.DESCRIPCION%TYPE,
        UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
    )RETURN NUMBER AS 
        MI_ERROR_FUN 		      PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 3;
        MI_DISTRIBUIRCENTROS  NUMBER;
        MI_RS 				        SYS_REFCURSOR; 
        MI_RS1 				        SYS_REFCURSOR; 
        MI_RSPLAN 			      SYS_REFCURSOR; 
        MI_STRSQL 			      PCK_SUBTIPOS.TI_STRSQL; 
        MI_MAX 				        PCK_SUBTIPOS.TI_CONSECUTIVOCNT;
        MI_VALORCUEN 		      NUMBER; 
        MI_NATURALEZA 		    PCK_SUBTIPOS.TI_NATURALEZACONTA; 
        MI_CONSECUTIVO 		    NUMBER; 
        MI_COMPANIA  		      PCK_SUBTIPOS.TI_COMPANIA; 
        MI_ANO  			        PCK_SUBTIPOS.TI_ANIO; 
        MI_ETAPA 			        VARCHAR2(300 CHAR); 
        MI_CLASECUENTA 		    PCK_SUBTIPOS.TI_CLASECUENTACONTA; 
        MI_CAMPOS 			      PCK_SUBTIPOS.TI_CAMPOS;
        MI_VALORES 			      PCK_SUBTIPOS.TI_VALORES;
        MI_RSCENTROCOSTO 	    PCK_SUBTIPOS.TI_CENTRO_COSTO;
        MI_RSPORCENTAJE  	    PCK_SUBTIPOS.TI_PORCENTAJE;
    BEGIN          
        --SELECCIONO LOS CENTROS DE COSTO A DISTRIBUIR
        MI_STRSQL :=  ' SELECT CENTRO_COSTO,
                              PORCENTAJE' ||
                      ' FROM DISTRIBUCION_CENTROCOSTO' ||
                      ' WHERE COMPANIA=''' || UN_COMPANIA || ''' AND ANO=' || UN_ANO;
        MI_ETAPA := '1';
        EXECUTE IMMEDIATE MI_STRSQL INTO MI_RSCENTROCOSTO,MI_RSPORCENTAJE;
        IF SQL%NOTFOUND THEN
           MI_ETAPA := '1 - NO SE ENCONTRARON CENTROS DE COSTO DISTRIBUIDOS. POR FAVOR VERIFIQUE NUEVAMENTE';
           MI_DISTRIBUIRCENTROS := 0;
           RETURN MI_DISTRIBUIRCENTROS;
        END IF;
        OPEN MI_RS FOR MI_STRSQL;
          LOOP
            EXIT WHEN MI_RS%NOTFOUND;
            FETCH MI_RS INTO MI_RSCENTROCOSTO,MI_RSPORCENTAJE;
            MI_ETAPA := '2';
            --BUSCO EL ÚLTIMO CONSECUTIVO
            MI_STRSQL := ' SELECT  MAX(CONSECUTIVO) AS CONSEC' ||
                         ' FROM DETALLE_COMPROBANTE_CNT  ' ||
                         ' WHERE COMPANIA = ''' || UN_COMPANIA || '''
                           AND   ANO      = ' || UN_ANO || ' 
                           AND TIPO_CPTE  = ''' || UN_TIPO || ''' 
                           AND COMPROBANTE= ' || UN_NUMERO || ' ';
            EXECUTE IMMEDIATE MI_STRSQL INTO MI_MAX;
            MI_ETAPA := '3';
            IF MI_MAX IS NOT NULL THEN
                MI_CONSECUTIVO := NVL(MI_MAX + 1, 1);
            END IF;
            MI_ETAPA := '4';
            MI_STRSQL := 'SELECT NVL(NATURALEZA,'''''') 
                          FROM V_PLAN_CONTABLE 
                          WHERE COMPANIA = '''||UN_COMPANIA|| '''
                              AND ANO    = '||UN_ANO||'
                              AND CODIGO = '''||UN_CUENTA||'''';
            EXECUTE IMMEDIATE MI_STRSQL INTO MI_NATURALEZA;

            MI_VALORCUEN := UN_VALORDISTRIBUIR * MI_RSPORCENTAJE;
            MI_CAMPOS := 'COMPANIA,
                          ANO,
                          TIPO_CPTE,
                          COMPROBANTE,
                          CUENTA,
                          CONSECUTIVO,
                          NATURALEZA,
                          FECHA,
                          DESCRIPCION,
                          TERCERO,
                          SUCURSAL,
                          CENTRO_COSTO,
                          AUXILIAR,
                          VALOR_DEBITO,
                          VALOR_CREDITO,
                          DATE_CREATED,
                          CREATED_BY,
                          DEBITOSAFECTADOS_CXP,
                          CREDITOSAFECTADOS_CXP,
                          PORDISTRIBUIDOCNT' ;
            MI_VALORES :='''' ||  UN_COMPANIA         || ''',
                          '   ||  UN_ANO              || ',
                          ''' ||  UN_TIPO             || ''',
                          '   ||  UN_NUMERO           || ',
                          ''' ||  UN_CUENTA           || ''',
                          '   ||  MI_CONSECUTIVO      || ',
                          ''' ||  MI_NATURALEZA       || ''',
                          ''' ||  UN_FECHA            || ''',
                          ''' ||  UN_DESCRIPCION      || ''',
                          ''' ||  UN_TERCERO          || ''',
                          ''' ||  UN_SUCURSAL         || ''',
                          ''' ||  MI_RSCENTROCOSTO  || ''',
                          ''' ||  UN_AUXILIAR         || ''',
                          '   ||  (CASE WHEN UN_DEBITO  THEN MI_VALORCUEN ELSE 0 END) || ' ,
                          '   ||  (CASE WHEN UN_CREDITO THEN MI_VALORCUEN ELSE 0 END) || ',
                                  SYSDATE                ,
                          ''' ||  UN_USUARIO || ''',
                                  0,
                                  0,
                          '   ||  NVL(MI_RSPORCENTAJE,0) || ')';
            --MI_ETAPA := '7'; SE ELIMINA PUES NO SE REQUIEREN HACER LAS INSERCIONES DEPENDIENDO DE LA NATURALEZA, PUES EL ACT_CONTA SE REALIZA AUTOMATICAMENTE
            MI_ETAPA := '8'; 
            MI_DISTRIBUIRCENTROS:= PCK_DATOS.FC_ACME(UN_TABLA   => 'DETALLE_COMPROBANTE_CNT', 
                                                     UN_ACCION  => 'I', 
                                                     UN_CAMPOS  => MI_CAMPOS, 
                                                     UN_VALORES => MI_VALORES);
          END LOOP;
        CLOSE MI_RS;
        RETURN MI_DISTRIBUIRCENTROS;
        EXCEPTION WHEN OTHERS THEN
            PCK_DATOS.GL_ERROR_MSG := 'Interrupción durante la distribución de centros en la etapa ' || MI_ETAPA;
            PCK_DATOS.GL_ERROR_MSG := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, 
                                                                     PCK_DATOS.GL_ERROR_MSG,  
                                                                     'CONTABILIDAD2',
                                                                     '',
                                                                     SQLERRM );
            RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );
    END FC_DISTRIBUIRCENTROS;

    FUNCTION FC_CONRETENCIONESLEY1450(
        /*
        NAME              : FC_CONRETENCIONESLEY1450 (En Access - Calculoconretencionesley1450 dentro del módulo distribución de Contabilidad)
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : Diego Maldonado
        DATE MIGRADOR     : 11/04/2016
        TIME              : 08:00 AM
        SOURCE MODULE     : Contabilidad - SysmanCT2016.02.06
        MODIFIER          : 
        DATE MODIFIED     : 
        TIME              : 
        DESCRIPTION       : Calculo de retenciones con la ley 1450. Se llama en la función CalculoRetencionesley1607
        @NAME:  calcularRetencionesTrabajadoresIndepedientes
        @METHOD:  GET
        */
        UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
        UN_MODULO             IN PCK_SUBTIPOS.TI_MODULO,
        UN_CONSECMENSAJES     IN NUMBER,
        UN_ANO                IN PCK_SUBTIPOS.TI_ANIO,
        UN_FECHA              IN DATE,
        UN_TIPO               IN COMPROBANTE_CNT.TIPO%TYPE,
        UN_NUMERO             IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
        UN_TERCERO            IN PCK_SUBTIPOS.TI_TERCERO,
        UN_SUCURSAL           IN PCK_SUBTIPOS.TI_SUCURSAL,
        UN_NOMBRETERCERO      IN PLAN_CONTABLE.NOMBRE%TYPE,
        UN_VALORBASE          IN NUMBER,
        UN_VALORBASEIVA       IN PCK_SUBTIPOS.TI_DOBLE,
        UN_CONSECUTIVO        IN NUMBER,
        UN_TIENECONTENIDO     IN NUMBER,
        UN_CUENTA_DEBITO1     IN VARCHAR2,
        UN_CUENTA_CREDITO1    IN VARCHAR2,
        UN_TIPORETENCION      IN COMPROBANTE_CNTRETENCION.TIPORETENCION%TYPE,
        UN_CUENTA_DEBITO      IN VARCHAR2,
        UN_CUENTA_CREDITO     IN VARCHAR2,
        UN_FACTORREDONDEO     IN NUMBER,
        UN_CODIGORETENCION    IN COMPROBANTE_CNTRETENCION.CODIGORETENCION%TYPE,
        UN_DESCRIPCION        IN DETALLE_COMPROBANTE_CNT.DESCRIPCION%TYPE,
        UN_STRCENTRO_COSTO    IN PCK_SUBTIPOS.TI_CENTRO_COSTO,
        UN_NRO_DOCUMENTO      IN DETALLE_COMPROBANTE_CNT.NRO_DOCUMENTO%TYPE)
      RETURN NUMBER
      AS
        MI_ERROR_FUN                    PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 4;
        MI_CONRETENCIONESLEY1450        NUMBER;
        MI_STRSQL                       PCK_SUBTIPOS.TI_STRSQL;
        MI_RS                           SYS_REFCURSOR;
        MI_RSRESUMEN                    SYS_REFCURSOR;
        MI_RSCONCEPTOS                  SYS_REFCURSOR;
        MI_IBC                          PCK_SUBTIPOS.TI_DOBLE;
        MI_AFC                          PCK_SUBTIPOS.TI_DOBLE;
        MI_APORTES                      PCK_SUBTIPOS.TI_DOBLE;
        MI_UVT                          PCK_SUBTIPOS.TI_DOBLE;
        MI_PORCENSALUD                  PCK_SUBTIPOS.TI_PORCENTAJE;
        MI_PORCENPENSION                PCK_SUBTIPOS.TI_PORCENTAJE;
        MI_VLRFONDO_PENSIONAL           PCK_SUBTIPOS.TI_DOBLE;
        MI_RSVALOR                      SYS_REFCURSOR;
        MI_VALORB                       PCK_SUBTIPOS.TI_DOBLE;
        MI_VALORBG                      NUMBER;
        MI_RSDET                        SYS_REFCURSOR;
        MI_CUENTA                       VARCHAR2(4000 CHAR);
        MI_SALUD                        NUMBER;
        MI_PENSION                      NUMBER;
        MI_PORCENTAJE                   NUMBER;
        MI_RETENCION                    NUMBER;
        MI_VALORRETENER                 NUMBER;
        MI_SALMINIMO                    NUMBER;
        MI_CODIGO_CUENTA                VARCHAR2(4000 CHAR);
        MI_STRNAT                       VARCHAR2(4000 CHAR);
        MI_RSCUENTAPPTAL                SYS_REFCURSOR;
        MI_CUENTAPPTAL                  VARCHAR2(4000 CHAR);
        MI_MES                           PCK_SUBTIPOS.TI_MES;
        MI_VALORACUMULADO               NUMBER;
        MI_FONDOSPENSIONAL              NUMBER;
        MI_BASERETENCION                NUMBER;
        MI_DEPENDIENTES                 NUMBER;
        MI_MEDICINAPREPAGADAYSEGUROS    NUMBER;
        MI_DEPENDIENTE                  NUMBER;
        MI_DEDUCIBLEVIVIENDA            NUMBER;
        MI_APORTESARL                   NUMBER;
        MI_INGRESOBASEDERETENCION       NUMBER;
        MI_INGRESOBASEDERETENCION1      NUMBER;
        MI_INGRESOBASEDERETENCION2      NUMBER;
        MI_RENTASEXENTAS                NUMBER;
        MI_RENTASEXENTASDETRABAJO       NUMBER;
        MI_RENTAEXENTADETRABAJO         NUMBER;
        MI_VALORUVTRENTASEXENTAS        NUMBER;
        MI_VALORRETEFUENTE              NUMBER;
        MI_DECLARARENTA                 NUMBER;
        MI_VALORRETENER2                NUMBER;
        MI_BASEREAL                     NUMBER;
        MI_ANO1                         VARCHAR2(4000 CHAR);
        MI_PORCENTAJEREAL               NUMBER;
        MI_APORTASALUD                  NUMBER;
        MI_APORTAPENSION                NUMBER;
        MI_RSDETCOMPANIA                DETALLE_COMPROBANTE_CNT.COMPANIA%TYPE;
        MI_RSDETANO                     NUMBER;
        MI_RSDETTIPO                    DETALLE_COMPROBANTE_CNT.TIPO_CPTE%TYPE;
        MI_RSDETCOMPROBANTE             NUMBER;
        MI_RSDETCONSECUTIVO             NUMBER;
        MI_DATORETENCION                NUMBER;
        MI_CONSECUTIVO                  NUMBER := UN_CONSECUTIVO;
        MI_RTA                          VARCHAR2(4000 CHAR);
        MI_CAMPOS                       PCK_SUBTIPOS.TI_CAMPOS;
        MI_VALORES                      PCK_SUBTIPOS.TI_VALORES;
        MI_CONDICION                    PCK_SUBTIPOS.TI_CONDICION;
        MI_RTAMSG                       VARCHAR2(200 CHAR);
    BEGIN
        --TRAIGO EL VALOR IBC Y APORTES VOLUNTARIOS DEL TERCERO
        BEGIN
            MI_STRSQL :=' SELECT  NVL(IBC,0),  
                                  NVL(AFC,  0),  
                                  NVL(APORTESVOLUNTARIOS, 0),  
                                  NVL(MEDPREPAGADA, 0),  
                                  NVL(DEPENDIENTE, 0),  
                                  NVL(DEDUCIBLE_VIVIENDA, 0),  
                                  NVL(DECLARARENTA, 0),  
                                  NVL(APORTESARL, 0),  
                                  NVL(NOAPORTASALUD, 0),  
                                  NVL(NOAPORTAPENSION, 0)  ' || 
                        ' FROM TERCERO' || 
                        ' WHERE COMPANIA = ''' || UN_COMPANIA || '''
                            AND NIT = ''' || UN_TERCERO || '''
                            AND SUCURSAL = ''' || UN_SUCURSAL || '''' ;

            EXECUTE IMMEDIATE MI_STRSQL INTO  MI_IBC, 
                                              MI_AFC, 
                                              MI_APORTES, 
                                              MI_MEDICINAPREPAGADAYSEGUROS, 
                                              MI_DEPENDIENTE, 
                                              MI_DEDUCIBLEVIVIENDA, 
                                              MI_DECLARARENTA, 
                                              MI_APORTESARL, 
                                              MI_APORTASALUD, 
                                              MI_APORTAPENSION;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_IBC := NULL;
                MI_AFC := NULL;
                MI_APORTES := NULL;
                MI_MEDICINAPREPAGADAYSEGUROS := NULL;
                MI_DEPENDIENTE := NULL;
                MI_DEDUCIBLEVIVIENDA := NULL;
                MI_DECLARARENTA := NULL;
                MI_APORTESARL := NULL;
                MI_APORTASALUD := NULL;
                MI_APORTAPENSION := NULL;
        END;

        --TRAIGO EL VALOR DEL %SALUD Y %PENSION DEL AÑO EN CURSO;
        BEGIN
            MI_STRSQL :=' SELECT  PORCENTAJESALUD,  
                                  PORCENTAJEPENSION,  
                                  VALORUVT,  
                                  VLRFONDOSOL_PENSIONAL' || 
                        ' FROM ANO' || 
                        ' WHERE COMPANIA = ''' || UN_COMPANIA || ''' 
                            AND NUMERO=' || UN_ANO || ')';
            EXECUTE IMMEDIATE MI_STRSQL INTO MI_PORCENSALUD, MI_PORCENPENSION, MI_UVT, MI_VLRFONDO_PENSIONAL;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_PORCENSALUD := NULL;
                MI_PORCENPENSION := NULL; 
                MI_UVT := NULL; 
                MI_VLRFONDO_PENSIONAL := NULL;
        END;
        MI_PORCENSALUD    := (CASE WHEN MI_APORTASALUD <> 0 THEN 0 ELSE MI_PORCENSALUD END);
        MI_PORCENPENSION  := (CASE WHEN MI_APORTAPENSION <> 0 THEN 0 ELSE MI_PORCENPENSION END);
        MI_MES            := TO_CHAR(UN_FECHA, 'MM');
        MI_ANO1           := UN_ANO;
        MI_SALMINIMO      := PCK_SYSMAN_UTL.FC_PAR(''''||UN_COMPANIA||'''','VALOR SALARIO MINIMO', UN_MODULO, SYSDATE);
        --TRAIGO EL VALOR ACUMULADO DE LAS RETENCIONES;

        BEGIN
            MI_STRSQL := 'SELECT VALORACUMULADO 
                          FROM (SELECT  COMPROBANTE_CNT.COMPANIA,  
                                        COMPROBANTE_CNT.TIPO,
                                        COMPROBANTE_CNT.ANO,  
                                        SUM(COMPROBANTE_CNTRETENCION.VALOR) AS VALORACUMULADO,  
                                        COMPROBANTE_CNT.TERCERO,  
                                        COMPROBANTE_CNT.SUCURSAL 
                                FROM (COMPROBANTE_CNT INNER JOIN COMPROBANTE_CNTRETENCION ON 
                                        COMPROBANTE_CNT.COMPANIA = COMPROBANTE_CNTRETENCION.COMPANIA 
                                            AND COMPROBANTE_CNT.ANO = COMPROBANTE_CNTRETENCION.ANO 
                                            AND COMPROBANTE_CNT.TIPO = COMPROBANTE_CNTRETENCION.TIPO 
                                            AND COMPROBANTE_CNT.NUMERO = COMPROBANTE_CNTRETENCION.NUMERO) INNER JOIN RETENCIONES ON 
                                    COMPROBANTE_CNTRETENCION.COMPANIA = RETENCIONES.COMPANIA 
                                        AND COMPROBANTE_CNTRETENCION.TIPORETENCION = RETENCIONES.TIPO 
                                        AND COMPROBANTE_CNTRETENCION.ANO = RETENCIONES.ANO AND COMPROBANTE_CNTRETENCION.CODIGORETENCION = RETENCIONES.CODIGO 
                                WHERE COMPROBANTE_CNT.COMPANIA =''' || UN_COMPANIA || '''
                                    AND COMPROBANTE_CNT.TIPO =''' || UN_TIPO || '''
                                    AND COMPROBANTE_CNT.ANO =' || UN_ANO || '
                                    AND COMPROBANTE_CNT.TERCERO =''' || UN_TERCERO || '''
                                    AND COMPROBANTE_CNT.SUCURSAL =''' || UN_SUCURSAL || ''' 
                                    AND RETENCIONES.ALEY1450 IN -1
                                    AND TO_CHAR(FECHA) = ' || MI_MES || 
                                ' GROUP BY  COMPROBANTE_CNT.COMPANIA, 
                                            COMPROBANTE_CNT.TIPO, 
                                            COMPROBANTE_CNT.ANO, 
                                            COMPROBANTE_CNT.TERCERO, 
                                            COMPROBANTE_CNT.SUCURSAL';
            EXECUTE IMMEDIATE MI_STRSQL INTO MI_VALORACUMULADO;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_VALORACUMULADO := 0;
        END;
        BEGIN
            MI_STRSQL :=  ' SELECT SUM(VLR_BASE) AS SVLR_BASE ' ||
                          ' FROM COMPROBANTE_CNT ' ||
                          ' WHERE COMPANIA = ''' || UN_COMPANIA || '''
                                AND ANO =' || UN_ANO || '
                                AND TIPO = ''' || UN_TIPO || '''
                                AND TO_CHAR(FECHA) =' || MI_MES || '
                                AND LEY1450 = -1 
                                AND TERCERO = ''' || UN_TERCERO || ''' 
                                AND SUCURSAL = ''' || UN_SUCURSAL || '''
                            GROUP BY COMPANIA, ANO, TIPO';

        EXECUTE IMMEDIATE MI_STRSQL INTO MI_VALORB;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_VALORB := NULL;
        END;

        MI_VALORB := NVL(MI_VALORB,0);
        --    IF ROUND(VALORB / UVT, 2) > MAXUVT THEN
        --       MSGBOX 'EL VALOR ACUMULADO DE LOS HONORARIOS DEL TERCERO SELECCIONADO SUPERAN LAS ' || MAXUVT || ' UVT. ' || VBCRLF ||
        --     'POR LO TANTO NO SE APLICA LEY 1527 DE 2012', VBINFORMATION + VBDEFAULTBUTTON1, 'SYSMAN SOFTWARE';
        --SE REALIZA EL CALCULO NORMAL;
        --    RTA = CALCULOSINRETENCIONESLEY1450(COMPANIA, ANO, FECHA, TIPO, NUMERO, TERCERO, SUCURSAL, NOMBRETERCERO, NVL(VALORBASE, 0), NVL(VALORBASEIVA, 0), CONSECUTIVO, TIENECONTENIDO, NVL(CUENTA_DEBITO, ''), NVL(CUENTA_CREDITO, ''), TIPORETENCION, LIMITE_INF, PCT_BASE, PCT_APLICAR, VALOR_APLICAR, FACTORREDONDEO, CODIGORETENCION, INDILEY, DESCRIPCION, NVL(STRCENTRO_COSTO, '9999999999'), NVL(NRO_DOCUMENTO, ''));
        --          CALCULOCONRETENCIONESLEY1450 = TRUE;
        --          EXIT FUNCTION;
        --   END IF;
        --TODAVIA NO MAS ADELANTE;
        --   PORCENTAJE = RETEF2007(ROUND(VALORB / UVT, 2), (ANO), UVT);
        --  IF PORCENTAJE = 0 THEN
        --     GOTO SIGUE:;
        --  END IF;

        IF MI_DATORETENCION = 0 THEN
            IF UN_TIENECONTENIDO <> 0 THEN
                MI_STRSQL := '''' || UN_CUENTA_DEBITO1 || ''',''' || UN_CUENTA_CREDITO1 || '''';
                IF MI_STRSQL <> '' THEN
                    MI_DATORETENCION := MI_DATORETENCION + 1;
                    OPEN MI_RSDET FOR 'SELECT DETALLE_COMPROBANTE_CNT.COMPANIA,
                                          DETALLE_COMPROBANTE_CNT.ANO,
                                          TIPO_CPTE,
                                          COMPROBANTE,
                                          CONSECUTIVO
                                  FROM   DETALLE_COMPROBANTE_CNT 
                                  WHERE  DETALLE_COMPROBANTE_CNT.COMPANIA= '''||UN_COMPANIA ||
                                      '''AND DETALLE_COMPROBANTE_CNT.ANO =' ||UN_ANO||
                                      'AND TIPO_CPTE = ''' ||UN_TIPO||
                                      '''AND COMPROBANTE= '|| UN_NUMERO ||
                                      '''AND  DETALLE_COMPROBANTE_CNT.CUENTA IN (' || MI_STRSQL || ')' ||
                                  ' ORDER BY DETALLE_COMPROBANTE_CNT.COMPANIA,DETALLE_COMPROBANTE_CNT.ANO,TIPO_CPTE,COMPROBANTE,CONSECUTIVO';
                        LOOP
                        EXIT WHEN MI_RSDET%NOTFOUND;
                        FETCH MI_RSDET INTO MI_RSDETCOMPANIA,MI_RSDETANO,MI_RSDETTIPO,MI_RSDETCOMPROBANTE,MI_RSDETCONSECUTIVO;
                        MI_RTA := PCK_DATOS.FC_ACME('DETALLE_COMPROBANTE_CNT', 'E', NULL, NULL, 
                                                    NULL, 'COMPANIA=''' || MI_RSDETCOMPANIA || 
                                                        ''' AND ANO= ' || MI_RSDETANO ||
                                                          ' AND TIPO_CPTE=''' || MI_RSDETTIPO || 
                                                        ''' AND COMPROBANTE=' || MI_RSDETCOMPROBANTE || 
                                                          ' AND CONSECUTIVO=' || MI_RSDETCONSECUTIVO, NULL, NULL, NULL, NULL);
                        END LOOP;
                END IF;
            END IF;
        END IF;
        --SI HAY RETENCIONES ENTRA A CALCULAR LOS VALORES;
        MI_CUENTA := '';

        MI_VALORB := ROUND(MI_VALORB, 2);
        MI_VALORBG := ROUND(MI_VALORB, 2);
        MI_SALUD := ROUND((MI_IBC * MI_PORCENSALUD) / 100, 2);
        MI_PENSION := ROUND((MI_IBC * MI_PORCENPENSION) / 100, 2);

        IF MI_IBC >= MI_SALMINIMO * 4 THEN
            --NO ES EL 1% DE LA BASE SI NO EL 1% DEL IBC;
            --FONDOSPENSIONAL = ROUND((VALORB * VLRFONDO_PENSIONAL) / 100, 2);
            MI_FONDOSPENSIONAL := ROUND((MI_IBC * MI_VLRFONDO_PENSIONAL) / 100, 2);
        ELSE
            MI_FONDOSPENSIONAL := 0;
        END IF;

        IF MI_DEPENDIENTE <> 0 THEN
            MI_DEPENDIENTES := ROUND((MI_VALORB * PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'VALOR PORCENTAJE DEDUCIBLE POR DEPENDIENTES', UN_MODULO, SYSDATE)) / 100, 2);
            IF MI_DEPENDIENTES > (PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'M�XIMO DEDUCIBLE POR DEPENDIENTES', UN_MODULO, SYSDATE) * MI_UVT) THEN
                MI_DEPENDIENTES := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'M�XIMO DEDUCIBLE POR DEPENDIENTES', UN_MODULO, SYSDATE) * MI_UVT;
            END IF;
        ELSE
            MI_DEPENDIENTE := 0;
        END IF;

        IF MI_MEDICINAPREPAGADAYSEGUROS > PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'M�XIMO DEDUCIBLE MEDICINA PEPAGADA Y SEGUROS', UN_MODULO, SYSDATE) * MI_UVT THEN
            MI_MEDICINAPREPAGADAYSEGUROS := ROUND(TRUNC(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'M�XIMO DEDUCIBLE MEDICINA PEPAGADA Y SEGUROS', UN_MODULO, SYSDATE)  * MI_UVT / NVL(UN_FACTORREDONDEO, 0) + 0.5001) * NVL(UN_FACTORREDONDEO, 0), 2);
        END IF;

        IF MI_DEDUCIBLEVIVIENDA > PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'M�XIMO DEDUCIBLE POR INTERESES DE VIVIENDA', UN_MODULO, SYSDATE) * MI_UVT THEN
            MI_DEDUCIBLEVIVIENDA := ROUND(TRUNC(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'M�XIMO DEDUCIBLE POR INTERESES DE VIVIENDA', UN_MODULO, SYSDATE)  * MI_UVT / NVL(UN_FACTORREDONDEO, 0) + 0.5001) * NVL(UN_FACTORREDONDEO, 0), 2);
        END IF;

        MI_INGRESOBASEDERETENCION1 := ROUND(MI_VALORB, 2) - MI_DEDUCIBLEVIVIENDA - MI_MEDICINAPREPAGADAYSEGUROS - MI_DEPENDIENTES - MI_SALUD - MI_APORTESARL - MI_PENSION;

        --RENTAS EXCENTAS NO DEBEN SUPERAR EL 30% DE LOS INGRESOS BRUTOS DEL MES;
        --INGRESOBASEDERETENCION2 = INGRESOBASEDERETENCION1 - APORTES - AFC - PENSION - FONDOSPENSIONAL;
        MI_RENTASEXENTAS := MI_APORTES + MI_AFC + MI_PENSION + MI_FONDOSPENSIONAL;

        IF MI_RENTASEXENTAS > (MI_VALORB * TO_NUMBER(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'VALOR PORCENTAJE DEDUCIBLE POR RENTAS EXCENTAS', UN_MODULO, SYSDATE)) / 100) THEN
            MI_RENTASEXENTAS := MI_VALORB * TO_NUMBER(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'VALOR PORCENTAJE DEDUCIBLE POR RENTAS EXCENTAS', UN_MODULO, SYSDATE)) / 100;
        END IF;
        --INGRESOBASEDERETENCION2 = INGRESOBASEDERETENCION1 - RENTASEXENTAS;
        MI_INGRESOBASEDERETENCION2 := MI_INGRESOBASEDERETENCION1;

        --RENTAS EXCENTAS DE TRABAJO NO DEBEN SUPERAR 240 UVT;

        MI_VALORUVTRENTASEXENTAS := ROUND(TRUNC(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'VALOR MI_UVT MAXIMO PARA RENTA EXCENTA DE TRABAJO', UN_MODULO, SYSDATE) * MI_UVT / NVL(UN_FACTORREDONDEO, 0) + 0.5001) * NVL(UN_FACTORREDONDEO, 0), 2);
        MI_RENTAEXENTADETRABAJO := ROUND(MI_INGRESOBASEDERETENCION2 * PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'PORCENTAJE DE RENTAS DE TRABAJO EXENTAS', UN_MODULO, SYSDATE) / 100, 2);

        IF MI_RENTAEXENTADETRABAJO > MI_VALORUVTRENTASEXENTAS THEN
            MI_RENTASEXENTASDETRABAJO := MI_VALORUVTRENTASEXENTAS;
        ELSE 
            MI_RENTASEXENTASDETRABAJO := MI_RENTAEXENTADETRABAJO;
        END IF;
        MI_BASEREAL := MI_INGRESOBASEDERETENCION2 - MI_RENTASEXENTASDETRABAJO;
        MI_VALORRETENER := MI_INGRESOBASEDERETENCION2 - MI_RENTASEXENTASDETRABAJO;
        MI_VALORRETEFUENTE := ROUND(TRUNC(PCK_CONTABILIDAD2.FC_RETEF2007(UN_COMPANIA,UN_CONSECMENSAJES, ROUND(MI_VALORRETENER / MI_UVT, 2), UN_ANO, MI_UVT) / NVL(UN_FACTORREDONDEO, 0) + 0.5001) * NVL(UN_FACTORREDONDEO, 0), 2);
        MI_PORCENTAJEREAL := ROUND(TRUNC(PCK_CONTABILIDAD2.FC_RETEF20071(UN_COMPANIA,UN_CONSECMENSAJES, ROUND(MI_VALORRETENER / MI_UVT, 2), UN_ANO)), 2);
        IF MI_VALORRETEFUENTE = 0 THEN
            GOTO SIGUE;
        END IF;
        --REVISAR SI EL TERCERO ES DECLARANTE, CUANDO SUS INGRESOS TOTALES EN AÑO GRAVABLE ANTERIOR;
        --SEAN IGUALES A 4073 UVT;
        IF MI_DECLARARENTA <> 0 THEN
            MI_VALORRETENER2 := PCK_CONTABILIDAD2.FC_RETENCIONES_MINIMAS(UN_COMPANIA,UN_MODULO, UN_CONSECMENSAJES, MI_VALORB, MI_SALUD, MI_PENSION, MI_FONDOSPENSIONAL, MI_UVT, MI_MES, MI_ANO1,MI_DECLARARENTA, UN_FACTORREDONDEO);
        END IF;

        IF MI_VALORRETEFUENTE > MI_VALORRETENER2 THEN
            MI_VALORRETENER := MI_VALORRETEFUENTE;
        ELSE
            MI_RTAMSG := PCK_DATOS.FC_ACME('TEMP_CALCULO_RETENCIONES', 'I', 'CODIGO,ORDEN,MENSAJE',
                                              UN_CONSECMENSAJES||' , '||PCK_CONTABILIDAD2.FC_ORDENMENSAJES(UN_CONSECMENSAJES)||',
                                              ''El valor calculado de retención mínima es superior al valor calculado de retención,' || CHR(13) || 'por lo tanto se dejara la retención mínima'''
                                              , NULL, NULL, NULL, NULL, NULL, NULL) ;
            MI_VALORRETENER := MI_VALORRETENER2;
        END IF;
        --CAMBIA;
        --              VALORRETENER = ROUND(VALORB * 80 / 100, 2) - SALUD - PENSION - FONDOSPENSIONAL;
        --              VALORRETENER = (VALORRETENER * PORCENTAJE) / 100;
        --               DEDUCIBLE = SALUD + PENSION;
        --               VALORB = VALORB - DEDUCIBLE;
        --               VALORBG = VALORB;
        --EMPIEZA CON EL PROCESO DE C�LCULO DE RETENCIONES;
        --            PORCENTAJE = ROUND((RETEF2007(ROUND(VALORB / UVT, 2), (ANO), UVT) / UVT) / ROUND(VALORB / UVT, 2) * 100, 2);
        --           VALORRETENER = ROUND(VALORB * PORCENTAJE / 100, 0);
        --CALCULO EL VALOR A RETENER DESCONTANDO EL ACUMULADO YA RETENIDO;
        IF MI_VALORRETENER - MI_VALORACUMULADO <= 0 THEN
            MI_RTAMSG := PCK_DATOS.FC_ACME('TEMP_CALCULO_RETENCIONES', 'I', 'CODIGO,ORDEN,MENSAJE',
                                              UN_CONSECMENSAJES||' , '||PCK_CONTABILIDAD2.FC_ORDENMENSAJES(UN_CONSECMENSAJES)||',
                                              ''El valor a retener ' || TRIM(TO_CHAR(MI_VALORRETENER, '999G999G999G999G999G999D99')) ||
                                              ' es menor o igual que el valor retenido acumulado del mes ' 
                                              || TRIM(TO_CHAR(MI_VALORACUMULADO, '999G999G999G999G999G999D99')) ||
                                              ' por lo tanto no se calculara retención'''
                                              , NULL, NULL, NULL, NULL, NULL, NULL) ;
        END IF;
        MI_VALORRETENER := MI_VALORRETENER - MI_VALORACUMULADO;
        IF MI_VALORRETENER < 0 THEN
            MI_VALORRETENER := 0;
        END IF;
        --FIN DEL CALCULO DEL VALOR A RETENER;
        --FACTOR DE REDONDEO;
        IF NVL (UN_FACTORREDONDEO, 0) <> 0 THEN
            MI_VALORRETENER := ROUND(TRUNC(MI_VALORRETENER / NVL (UN_FACTORREDONDEO, 0) + 0.5001) * NVL (UN_FACTORREDONDEO, 0), 2);
        END IF;
        IF NVL (UN_CUENTA_CREDITO1, '') <> '' THEN
            MI_CODIGO_CUENTA := NVL (UN_CUENTA_CREDITO1, '');
        ELSIF NVL(UN_CUENTA_DEBITO1, '') <> '' THEN
            MI_CODIGO_CUENTA := NVL (UN_CUENTA_DEBITO1, '');
        ELSE
            MI_RTAMSG := PCK_DATOS.FC_ACME('TEMP_CALCULO_RETENCIONES', 'I', 'CODIGO,ORDEN,MENSAJE',
                                              UN_CONSECMENSAJES||' , '||PCK_CONTABILIDAD2.FC_ORDENMENSAJES(UN_CONSECMENSAJES)||',
                                              ''El código de retención: ' || NVL(UN_TIPO, '') || ' - ' || NVL(UN_NUMERO, '') || ' no tiene cuenta contable relacionada.'''
                                              , NULL, NULL, NULL, NULL, NULL, NULL) ;

            MI_CONRETENCIONESLEY1450 := -1;
            RETURN MI_CONRETENCIONESLEY1450;
        END IF;
        MI_STRNAT := SUBSTR('CDCCCDDDDC', TO_NUMBER(LPAD(MI_CODIGO_CUENTA, 1)) + 1, 1);
        --IF NVL(RS!PERMITEMODIFICAR, FALSE) = FALSE AND NVL(RSVALOR!SVLR_DOCUMENTO, 0) <> 0 THEN
        --   VALORRETENER = NVL(RSVALOR!SVLR_DOCUMENTO, 0);
        --END IF;
        IF MI_VALORRETENER <> 0 THEN
            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
            --AQUI SE AGREGA EL PARAMETRO PARA TRAER LA RETENCION CON EL CENTRO DE COSTO CODIFICADO EN LA CODIFICACION DE RETENCIONES;
            /*INICIO DMALDONADO: Se añadió el comentario a las 3 líneas siguientes, pues en sì no estaban haciendo nada.*/
            --IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'MANEJA RETENCIONES POR CENTRO DE COSTO', UN_MODULO, SYSDATE) = 'SI' THEN
                --MI_STRCENTRO_COSTO := UN_STRCENTRO_COSTO;
            --END IF;

            BEGIN
                MI_STRSQL := 'SELECT CUENTA_PPTAL 
                              FROM V_PLAN_CONTABLE 
                              WHERE COMPANIA=''' || UN_COMPANIA|| ''' 
                                  AND ANO =' || UN_ANO|| ' 
                                  AND CODIGO=''' || MI_CODIGO_CUENTA|| '''';
                EXECUTE IMMEDIATE MI_STRSQL INTO MI_CUENTAPPTAL;
                MI_CUENTAPPTAL := NVL(MI_CUENTAPPTAL, ''); 
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_CUENTAPPTAL := '';
            END; 
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
                          TERCERO,
                          SUCURSAL,
                          AUXILIAR,
                          DESCRIPCION,
                          CUENTAPPTAL,
                          EJECUCION_DEBITO,
                          EJECUCION_CREDITO,
                          BASE_IVA,
                          NRO_DOCUMENTO';
            MI_VALORES :='''' || UN_COMPANIA|| ''',
                            ' || UN_ANO|| ',
                          ''' || UN_TIPO|| ''',
                            ' || UN_NUMERO|| ',
                            ' || MI_CONSECUTIVO|| ',
                          ''' || MI_CODIGO_CUENTA|| ''',
                            ' || UN_FECHA || ',
                          ''' || MI_STRNAT|| ''',
                            ' || (CASE WHEN NVL(UN_CUENTA_CREDITO1, '') <> '' THEN 0 ELSE MI_VALORRETENER END) || ', 
                            ' || (CASE WHEN NVL(UN_CUENTA_DEBITO1, '') <> '' THEN 0 ELSE MI_VALORRETENER END) || ',
                            ' || MI_BASEREAL|| ',
                          ''' || UN_STRCENTRO_COSTO|| ''',
                          ''' || NVL(UN_TERCERO, ''''||PCK_DATOS.FC_CONS_TERCERO()||'''') || ''', 
                          ''' || NVL(UN_SUCURSAL, ''''||PCK_DATOS.FC_CONS_SUCURSAL()||'''') || ''',
                          ''' || PCK_DATOS.FC_CONS_AUXILIAR()||''',
                          ''' || UN_DESCRIPCION || ''',
                          ''' || NVL(MI_CUENTAPPTAL, '') || ''',
                            ' || (CASE WHEN NVL(UN_CUENTA_CREDITO1, '') <> '' THEN 0 ELSE MI_VALORRETENER END) || ', 
                            ' || (CASE WHEN NVL(UN_CUENTA_DEBITO1, '') <> '' THEN 0 ELSE MI_VALORRETENER END) || ', 
                            ' || NVL(UN_VALORBASEIVA, 0) || ', 
                            ''' || UN_NRO_DOCUMENTO|| ''')';
               MI_RTA := PCK_DATOS.FC_ACME('DETALLE_COMPROBANTE_CNT', 'I', MI_CAMPOS, MI_VALORES, NULL, NULL, NULL, NULL, NULL, NULL);

        END IF;
        <<SIGUE>>
            MI_CAMPOS := 'VALOR = ' ||(CASE WHEN MI_VALORRETEFUENTE <> 0 THEN MI_VALORRETENER ELSE 0 END)||',
                          VALORBASELEY1607 = '||(CASE WHEN MI_VALORRETEFUENTE <> 0 THEN MI_BASEREAL ELSE 0 END)||',
                          PCT_APLICA1607 = '||(CASE WHEN MI_PORCENTAJEREAL <> 0 THEN MI_PORCENTAJEREAL  ELSE 0 END);
            MI_CONDICION := 'COMPANIA =''' || UN_COMPANIA || '''
                                AND ANO =' || UN_ANO || '
                                AND TIPO =''' || UN_TIPO || '''
                                AND NUMERO =' || UN_NUMERO || '
                                AND TIPORETENCION =''' || UN_TIPORETENCION || '''
                                AND CODIGORETENCION =''' || UN_CODIGORETENCION || '''';
            MI_RTA := PCK_DATOS.FC_ACME('COMPROBANTE_CNTRETENCION','M', MI_CAMPOS, NULL, NULL, MI_CONDICION, NULL, NULL, NULL, NULL);
            MI_CAMPOS := 'VDEPENDIENTE = ' || NVL (MI_DEPENDIENTES, 0);
            MI_CONDICION := 'COMPANIA    = ''' || UN_COMPANIA || '''
                            AND NIT      = ''' || NVL (UN_TERCERO, '' )|| '''
                            AND SUCURSAL = ''' || NVL (UN_SUCURSAL, '' )|| '''';
            MI_RTA := PCK_DATOS.FC_ACME('TERCERO', 'M', MI_CAMPOS,
                                  NULL, NULL, MI_CONDICION,
                                  NULL,NULL,NULL,NULL);
            MI_CAMPOS := 'VISTA.PCT_APLICARLEY1607 = ' || MI_PORCENTAJEREAL;
            MI_CONDICION := 'VISTA.COMPANIA = '''||UN_COMPANIA||''' 
                                AND VISTA.ANO= '||UN_ANO||' 
                                AND VISTA.TIPO = '''|| UN_TIPO||'''
                                AND VISTA.NUMERO = ' || UN_NUMERO ||'
                                AND VISTA.ALEY1450 NOT IN (0)';
            MI_RTA := PCK_DATOS.FC_ACME(
                '(SELECT R.PCT_APLICARLEY1607, 
                    R.ALEY1450,
                    C.COMPANIA,
                    C.ANO,
                    C.TIPO, 
                    C.NUMERO
                FROM RETENCIONES R INNER JOIN COMPROBANTE_CNTRETENCION C ON
                    R.COMPANIA = C.COMPANIA 
                    AND R.CODIGO = C.CODIGORETENCION
                    AND R.TIPO = C.TIPORETENCION
                    AND R.ANO = C.ANO) VISTA',
                'M', MI_CAMPOS, NULL, NULL, MI_CONDICION, NULL, NULL, NULL, NULL);

            MI_RTA := PCK_DATOS.FC_ACME('RESUMEN_RENTAS', 'IM', NULL, NULL, NULL, NULL,
                'SELECT COMPANIA,ANO,TIPO,NUMERO
                  FROM RESUMEN_RENTAS',
                'TABLA.COMPANIA = VISTA.COMPANIA
                    AND TABLA.ANO = VISTA.ANO
                    AND TABLA.TIPO = VISTA.TIPO
                    AND TABLA.NUMERO =  VISTA.VISTANUMERO', 
                'UPDATE SET TOTALINGRESO = NVL('||MI_VALORB||',0), 
                    DEDUCCION = NVL('||MI_DEDUCIBLEVIVIENDA||',0),
                    PAGOS_SALUD = NVL('||MI_SALUD||', 0),
                    APORTES_PENSION = NVL('||MI_PENSION||',0),
                    DEDUCCION_DEPENDIENTE = NVL('||MI_DEPENDIENTES||',0),
                    ARL = NVL('||MI_APORTESARL||',0),
                    APORTE_VOLUNTARIO = NVL('||MI_APORTES||',0),
                    DEDUCCION_PREPAGADA = NVL('||MI_MEDICINAPREPAGADAYSEGUROS||', 0),
                    EXENCION = NVL('||MI_RENTAEXENTADETRABAJO||',0),
                    VALORUVT = NVL('||MI_UVT||',0),
                    VALORRETENIDO = (CASE 
                                        WHEN '||MI_VALORRETEFUENTE||'<> 0
                                        THEN '||MI_VALORRETEFUENTE||'
                                        ELSE 0
                                    END)
                WHERE COMPANIA = '''||UN_COMPANIA ||'''
                    AND ANO = '||UN_ANO ||'
                    AND TIPO = '''||UN_TIPO||'''
                    AND NUMERO = '||UN_NUMERO,
                'INSERT INTO TABLA(
                    COMPANIA,
                    ANO, TIPO,
                    NUMERO, TERCERO, 
                    SUCURSAL,
                    FECHA, 
                    TOTALINGRESO, 
                    DEDUCCION, 
                    PAGOS_SALUD, 
                    APORTES_PENSION,
                    DEDUCCION_DEPENDIENTE, 
                    ARL, APORTE_VOLUNTARIO, 
                    DEDUCCION_PREPAGADA, 
                    EXENCION,VALORUVT,
                    VALORRETENIDO)
  --VOY ACAAAAAAAAAAA
              VALUES(
                    '''||UN_COMPANIA||''',
                    '||UN_ANO||',
                    '''||UN_TIPO||''',
                    '||UN_NUMERO||',
                    NVL('''||UN_TERCERO||''', ''''),
                    NVL('''||UN_SUCURSAL||''', ''''),
                    '||UN_FECHA||',
                    NVL('||MI_VALORB||',0),
                    NVL('||MI_DEDUCIBLEVIVIENDA||',0),
                    NVL('||MI_SALUD||',0),
                    NVL('||MI_PENSION||',0),
                    NVL('||MI_DEPENDIENTES||',0),
                    NVL('||MI_APORTESARL||',0),
                    NVL('||MI_APORTES||',0),
                    NVL('||MI_MEDICINAPREPAGADAYSEGUROS||',0),
                    NVL('||MI_RENTASEXENTASDETRABAJO||',0),
                    NVL('||MI_UVT||',0),
                (CASE 
                    WHEN '||MI_VALORRETEFUENTE||'<> 0
                    THEN '||MI_VALORRETEFUENTE||'
                    ELSE 0
                END)');
            MI_CONRETENCIONESLEY1450 := -1;
            RETURN MI_CONRETENCIONESLEY1450;            
        EXCEPTION WHEN OTHERS THEN
            PCK_DATOS.GL_ERROR_MSG := 'Interrupción durante el cálculo de retenciones con ley 1450.';
            PCK_DATOS.GL_ERROR_MSG := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'CONTABILIDAD2','',SQLERRM );
            RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );
    END FC_CONRETENCIONESLEY1450;

FUNCTION FC_RETEF2007 (
/*
  NAME              : RETEF2007
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : SANDRA MILENA DAZA LEGUIZAMON
  DATE MIGRADOR     : 04/03/2015
  TIME              : 2:15 PM
  SOURCE MODULE     : Contabilidad - SysmanCT2016.02.06
  MODIFIER          : DIEGO FERNANDO MALDONADO MORALES,YESIKA PAOLA BECERRA
  DATE MODIFIED     : 11/04/2016,16/01/2019
  TIME              : 02:30 PM,02:52 PM
  DESCRIPTION       : Se tomó la función RETEF2007 del paquete de nómina,
                      la cual se modificó para ser adaptada al paquete de contabilidad,se ajusta a función de nomina actualizada el 15/01/2019     
    MODIFIER          : CARLOS MAURICIO ALARCON CASALLAS
    TIMER             : 13/04/2022
    TICKET:           : 7711635
    MODIFICATIONS     : En FC_RETEF2007 se comenta MI_PARUVTACTUAL := NVL(PCK_SYSMAN_UTL.FC_PAR) dado a que La UVT se estaba 
                        tomando del parametro 'VALOR UVT ACTUAL', pero debido a que no se actualiza, se decide tomar el UVT 
                        consultando la tabla ANO segun el parametro recibido en UN_ANIO.
    
  @NAME:  buscarPorcentajeRetencionEnLaFuente
  @METHOD:  GET
*/
  UN_COMPANIA   		IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO       		IN PCK_SUBTIPOS.TI_ANIO,
  UN_BASE       		IN NUMBER,
  UN_UVT        		IN NUMBER,
  UN_OPCION     		IN VARCHAR2 DEFAULT NULL
)
RETURN NUMBER
  AS
    MI_ERROR_FUN   PCK_SUBTIPOS.TI_ERROR_FUN:= GL_ERROR_NUM + 5;
    MI_TOPE        NUMBER := 0;
    MI_PORMAX      NUMBER := 0;
    MI_RETEF       NUMBER := 0;
    MI_VLRAPLICAR  NUMBER := 0;
    MI_PORAPLICAR  NUMBER := 0;
    MI_LIMINFERIOR NUMBER := 0;
    MI_PARUVTACTUAL  NUMBER :=0;
  BEGIN
  
   BEGIN
        SELECT VALORUVT               
        INTO MI_PARUVTACTUAL
        FROM ANO
        WHERE COMPANIA = UN_COMPANIA
          AND NUMERO   = UN_ANIO;      
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_PARUVTACTUAL := 0; 
    END;
    
    /*
    MI_PARUVTACTUAL := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, 
                                       UN_NOMBRE   => 'VALOR UVT ACTUAL', 
                                       UN_MODULO   => PCK_DATOS.FC_MODULOCONTABILIDAD, 
                                       UN_FECHA_PAR=> SYSDATE), 0);  
    */
    
    BEGIN
      SELECT NVL(MAX(LIMITE_INFERIOR),0), NVL(MAX(POR_APLICAR),0)
      INTO  MI_TOPE, MI_PORMAX
      FROM   RETEFUENTEUVT  -- RETEFUENTE
      WHERE  COMPANIA       = UN_COMPANIA 
        AND  ANO          = UN_ANIO ;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      RETURN 0;  
    END; 

    FOR RS IN (
                SELECT  VALOR_APLICAR, 
                        POR_APLICAR, 
                        LIMITE_INFERIOR,
                        POR_BASE
                FROM   RETEFUENTEUVT  -- RETEFUENTE
                WHERE  COMPANIA     = UN_COMPANIA 
                  AND  ANO          = UN_ANIO 
                  AND  UN_BASE   BETWEEN LIMITE_INFERIOR AND LIMITE_SUPERIOR
                ORDER BY LIMITE_INFERIOR )
    LOOP  

      IF NOT UN_OPCION  IS NULL THEN 
        IF UN_OPCION = 'V' THEN
          MI_RETEF := MI_VLRAPLICAR;
        ELSE
          IF MI_PORAPLICAR = MI_PORMAX THEN
            MI_RETEF := ((UN_BASE - (CASE WHEN RS.POR_BASE > 0 THEN RS.POR_BASE ELSE RS.LIMITE_INFERIOR END - 1)) * RS.POR_APLICAR / 100) * MI_PARUVTACTUAL + (RS.VALOR_APLICAR * MI_PARUVTACTUAL);
            MI_RETEF := PCK_SYSMAN_UTL.FC_ROUND(MI_RETEF, 0);
          ELSE
            MI_RETEF := MI_PORAPLICAR;
          END IF;
        END IF;
      ELSE
      --IF MIPORAPLICAR = MI_PORMAX THEN
        MI_RETEF := ((UN_BASE - (TRUNC(CASE WHEN RS.POR_BASE > 0 THEN RS.POR_BASE ELSE RS.LIMITE_INFERIOR END))) * RS.POR_APLICAR / 100) * MI_PARUVTACTUAL + (RS.VALOR_APLICAR * MI_PARUVTACTUAL);
        MI_RETEF := PCK_SYSMAN_UTL.FC_ROUND(MI_RETEF, 0);
      --ELSE 
      --END IF;
      END IF;
    END LOOP;
        RETURN MI_RETEF;
  RETURN MI_RETEF;
END FC_RETEF2007;

    FUNCTION FC_RETEF20071
        /*
        NAME              : RETEF20071
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : DIEGO FERNANDO MALDONADO MORALES
        DATE MIGRADOR     : 11/04/2016
        TIME              : 03:35 PM
        SOURCE MODULE     : Contabilidad - SysmanCT2016.02.06
        MODIFIER          : 
        DATE MODIFIED     : 
        TIME              : 
        DESCRIPTION       : "Retorna el valor del monto de la retencion que se le debe aplicar al trabajador con el salario devengado 'salario'"
        @NAME:  buscarValorParaRetencionEnLaFuente
        @METHOD:  GET
        */
    (
        UN_COMPANIA   		IN PCK_SUBTIPOS.TI_COMPANIA,
        UN_CONSECMENSAJES   IN NUMBER,
        UN_ANIO       		IN PCK_SUBTIPOS.TI_ANIO,
        UN_BASE       		IN NUMBER
    )
    RETURN NUMBER
    AS
        MI_ERROR_FUN   PCK_SUBTIPOS.TI_ERROR_FUN:= GL_ERROR_NUM + 6;
        MI_TOPE        NUMBER := 0;
        MI_RETEF       NUMBER := 0;
        MI_PORAPLICAR  NUMBER := 0;
        MI_MINIMO      NUMBER := 0;
        MI_RTAMSG      VARCHAR2(200);
    BEGIN
        BEGIN
            SELECT NVL(MAX(LIMITE_INFERIOR),0), NVL(MIN(LIMITE_INFERIOR) ,0)
            INTO  MI_TOPE, MI_MINIMO
            FROM   RETEFUENTEUVT  -- RETEFUENTE
            WHERE  COMPANIA       = UN_COMPANIA 
                AND  ANO          = UN_ANIO ;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            RETURN 0;  
        END; 
        BEGIN
            SELECT POR_APLICAR
            INTO MI_PORAPLICAR 
            FROM   RETEFUENTEUVT  -- RETEFUENTE
            WHERE  COMPANIA    = UN_COMPANIA 
                AND  ANO       = UN_ANIO 
                AND  UN_BASE   BETWEEN LIMITE_INFERIOR AND LIMITE_SUPERIOR
            ORDER BY LIMITE_INFERIOR;

            MI_RETEF := MI_PORAPLICAR;           

            EXCEPTION WHEN NO_DATA_FOUND THEN
                IF UN_BASE < MI_MINIMO THEN
                    MI_RTAMSG := PCK_DATOS.FC_ACME('TEMP_CALCULO_RETENCIONES', 'I', 'CODIGO,ORDEN,MENSAJE',
                                              UN_CONSECMENSAJES||' , '||PCK_CONTABILIDAD2.FC_ORDENMENSAJES(UN_CONSECMENSAJES)||',
                                              ''La base para retención por honorarios es inferior a ' || MI_MINIMO || ' UVT. ' || CHR(13) || ', por lo tanto no se calculará retención.'''
                                              , NULL, NULL, NULL, NULL, NULL, NULL) ;                    
                END IF;
                RETURN 0;
        END;
        RETURN MI_RETEF;

        EXCEPTION WHEN OTHERS THEN
            PCK_DATOS.GL_ERROR_MSG := 'Se presentó una interrupción al ejecutar RETEF20071';
            PCK_DATOS.GL_ERROR_MSG := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'CONTABILIDAD2','',SQLERRM );
            RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );
    END FC_RETEF20071;

    FUNCTION FC_RETEF2013_384
        /*
        NAME              : RETEF2013_384
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : SANDRA MILENA DAZA LEGUIZAMON
        DATE MIGRADOR     : 09/03/2015
        TIME              : 11:00 AM
        SOURCE MODULE     : Contabilidad - SysmanCT2016.02.06
        MODIFIER          : SANDRA MILENA DAZA LEGUIZAMON - DIEGO FERNANDO MALDONADO MORALES
        DATE MODIFIED     :                    16/04/2015 - 11/04/2016
        TIME              :                      12:50 PM - 04:15 PM
        DESCRIPTION       : Se tomó la función FC_RETEF2013_384 del paquete de nómina,
                            la cual se modificó para ser adaptada al paquete de contabilidad.
        @NAME:  consultarValorUvt
        @METHOD:  GET                            
        */
    (
        UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
        UN_CONSECMENSAJES IN NUMBER,
        UN_BASE           IN NUMBER,
        UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO
    )
    RETURN NUMBER
    AS  
        MI_ERROR_FUN   PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 7;
        MI_VLR         NUMBER:=0;  
    BEGIN
        SELECT POR_APLICAR
        INTO MI_VLR
        FROM  RETEFUENTEUVT --RETEFUENTE_IMAN
        WHERE COMPANIA   = UN_COMPANIA
            AND ANO        = UN_ANIO
            AND UN_BASE    BETWEEN LIMITE_INFERIOR AND LIMITE_SUPERIOR
        ORDER BY LIMITE_INFERIOR;

        RETURN MI_VLR;
        EXCEPTION 
        WHEN NO_DATA_FOUND THEN
            RETURN 0;
        WHEN OTHERS THEN
            PCK_DATOS.GL_ERROR_MSG := 'Interrupción en la función de retefuente 2013 / 384' ;
            PCK_DATOS.GL_ERROR_MSG := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'CONTABILIDAD2','',SQLERRM );
            RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );   
    END FC_RETEF2013_384;

    FUNCTION FC_RETENCIONES_MINIMAS(
        /*
        NAME              : RETENCIONES_MINIMAS (En Access: RETENCIONES_MINIMAS, del código del módulo NuevasRutinas, perteneciente a Contabilidad.)
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : DIEGO FERNANDO MALDONADO MORALES
        DATE MIGRADOR     : 11/04/2016
        TIME              : 04:30 PM
        SOURCE MODULE     : Contabilidad - SysmanCT2016.02.06
        MODIFIER          : 
        DATE MODIFIED     : 
        TIME              : 
        DESCRIPTION       : 
        @NAME:  generarValorRetencionPagosLaborales
        @METHOD:  GET        
        */
        UN_COMPANIA 			IN PCK_SUBTIPOS.TI_COMPANIA,
        UN_MODULO   			IN PCK_SUBTIPOS.TI_MODULO,
        UN_CONSECMENSAJES   	IN NUMBER,
        UN_VALORB 				IN NUMBER, 
        UN_SALUD 				IN NUMBER, 
        UN_PENSION 				IN NUMBER, 
        UN_FONDOSPENSIONAL 		IN NUMBER, 
        UN_UVT 					IN NUMBER, 
        UN_MES 					IN PCK_SUBTIPOS.TI_MES, 
        UN_ANO 					IN VARCHAR2, 
        UN_DECLARARENTA 		IN NUMBER, 
        UN_FACTORREDONDEO 		IN NUMBER)
    RETURN NUMBER AS 
        MI_ERROR_FUN            PCK_SUBTIPOS.TI_ERROR_FUN:= GL_ERROR_NUM + 8;
        MI_RETENCIONES_MINIMAS  NUMBER;
        MI_BASEFINAL384         NUMBER; 
        MI_RETEFUENTEMINIMA     NUMBER; 
        MI_BASEFINAL            NUMBER; 
        MI_VALORBASEENUVT       NUMBER;
    BEGIN
        IF ( (UN_MES >= 4 AND UN_ANO = 2013) OR UN_ANO > 2013) AND UN_DECLARARENTA NOT IN (0) THEN
        --AQUI SE DEBE REVISAR PROCEDIMIENTOS DE LIQUIDACION CON TABLA DE RETEFUENTE MINIMA;
            MI_BASEFINAL384 := UN_VALORB - UN_SALUD - UN_PENSION - UN_FONDOSPENSIONAL;
            MI_VALORBASEENUVT := ROUND(MI_BASEFINAL384 / UN_UVT, 2);
            IF MI_VALORBASEENUVT <= PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'ULTIMO RANGO RETENCION MINIMA DE PAGOS EN UVT', UN_MODULO, SYSDATE) THEN
                MI_BASEFINAL := PCK_CONTABILIDAD2.FC_RETEF2013_384(UN_COMPANIA, UN_CONSECMENSAJES, MI_VALORBASEENUVT, UN_ANO);
                MI_RETEFUENTEMINIMA := ROUND(TRUNC(MI_BASEFINAL * UN_UVT / NVL(UN_FACTORREDONDEO, 0) + 0.5001) * NVL(UN_FACTORREDONDEO, 0), 2);
                MI_RETENCIONES_MINIMAS := MI_RETEFUENTEMINIMA;
            ELSE
                MI_RETEFUENTEMINIMA := ( (UN_VALORB / UN_UVT) * PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'PORCENTAJE A APLICAR ULTIMO RANGO DE RETENCION MINIMA', UN_MODULO, SYSDATE) / 100) - PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'UVT APLICADAS EN EL ULTIMO RANGO DE RETENCION MINIMA', UN_MODULO, SYSDATE);
                MI_RETENCIONES_MINIMAS := MI_RETEFUENTEMINIMA;
            END IF;
        END IF;
        RETURN MI_RETENCIONES_MINIMAS;

        EXCEPTION WHEN OTHERS THEN
            PCK_DATOS.GL_ERROR_MSG := 'Interrupción durante el cálculo de retenciones mínimas.';
            PCK_DATOS.GL_ERROR_MSG := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'CONTABILIDAD2','',SQLERRM );
            RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );
    END FC_RETENCIONES_MINIMAS;

FUNCTION FC_SINRETENCIONESLEY1450(
/*
  NAME              : FC_SINRETENCIONESLEY1450 (En Access - Calculosinretencionesley1450 dentro del módulo distribución de Contabilidad)
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : Diego Maldonado
  DATE MIGRADOR     : 12/04/2016
  TIME              : 08:00 AM
  SOURCE MODULE     : Contabilidad - SysmanCT2016.02.06
  MODIFIER          : YESIKA PAOLA BECERRA  
  DATE MODIFIED     : 16/01/2019 se agrega estandar de excepciones
  TIME              : 10:50 am
  DESCRIPTION       : Se llama en la función CalculoRetencionesley1607
  @NAME:  generarValorDeRetencionesEmpleados
  @METHOD:  GET        
*/
  UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA, 
  UN_CONSECMENSAJES   IN NUMBER,
  UN_ANO              IN PCK_SUBTIPOS.TI_ANIO, 
  UN_FECHA            IN DATE,
  UN_TIPO             IN DETALLE_COMPROBANTE_CNT.TIPO_CPTE%TYPE, 
  UN_NUMERO           IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT, 
  UN_TERCERO          IN PCK_SUBTIPOS.TI_TERCERO, 
  UN_SUCURSAL         IN PCK_SUBTIPOS.TI_SUCURSAL, 
  UN_VALORBASE        IN NUMBER, 
  UN_VALORBASEIVA     IN PCK_SUBTIPOS.TI_DOBLE, 
  UN_CONSECUTIVO      IN NUMBER, 
  UN_TIENECONTENIDO   IN NUMBER, 
  UN_CUENTA_DEBITO    IN VARCHAR2, 
  UN_CUENTA_CREDITO   IN VARCHAR2, 
  UN_TIPORETENCION    IN COMPROBANTE_CNTRETENCION.TIPORETENCION%TYPE, 
  UN_LIMITE_INF       IN NUMBER, 
  UN_PCT_BASE         IN NUMBER, 
  UN_PCT_APLICAR      IN NUMBER, 
  UN_VALOR_APLICAR    IN NUMBER, 
  UN_FACTORREDONDEO   IN NUMBER, 
  UN_CODIGORETENCION  IN COMPROBANTE_CNTRETENCION.CODIGORETENCION%TYPE, 
  UN_INDILEY          IN NUMBER, 
  UN_DESCRIPCION      IN VARCHAR2 DEFAULT NULL, 
  UN_STRCENTRO_COSTO  IN PCK_SUBTIPOS.TI_CENTRO_COSTO DEFAULT NULL, 
  UN_NRO_DOCUMENTO    IN DETALLE_COMPROBANTE_CNT.NRO_DOCUMENTO%TYPE DEFAULT NULL,
  UN_AUXILIAR         IN PCK_SUBTIPOS.TI_AUXILIAR DEFAULT NULL,
  UN_REFERENCIA       IN PCK_SUBTIPOS.TI_REFERENCIA DEFAULT NULL,
  UN_FUENTE_RECURSO   IN PCK_SUBTIPOS.TI_FUENTE_RECURSOS DEFAULT NULL
)
  RETURN NUMBER AS 
    MI_ERROR_FUN              PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 9;
    MI_RS                     SYS_REFCURSOR; 
    MI_RSDET                  SYS_REFCURSOR; 
    MI_STRSQL                 PCK_SUBTIPOS.TI_STRSQL; 
    MI_VALORB                 NUMBER; 
    MI_VALORBG                NUMBER; 
    MI_VALORBASE              NUMBER;
    MI_VALORRETENER           NUMBER; 
    MI_CODIGO_CUENTA          VARCHAR2(4000 CHAR); 
    MI_CUENTA                 VARCHAR2(4000 CHAR); 
    MI_STRNAT                 VARCHAR2(4000 CHAR); 
    MI_CUENTAPPTAL            VARCHAR2(4000 CHAR); 
    MI_MES                     PCK_SUBTIPOS.TI_MES; 
    MI_VALORACUMULADO         NUMBER;
    MI_DATORETENCION          NUMBER;
    MI_DESCRIPCION            VARCHAR2(4000 CHAR);
    MI_STRCENTRO_COSTO        VARCHAR2(4000 CHAR);
    MI_RSDETCOMPANIA          DETALLE_COMPROBANTE_CNT.COMPANIA%TYPE;
    MI_RSDETANO               NUMBER;
    MI_RSDETTIPO              DETALLE_COMPROBANTE_CNT.TIPO_CPTE%TYPE;
    MI_RSDETCOMPROBANTE       NUMBER;
    MI_RSDETCONSECUTIVO       NUMBER;
    MI_RTA                    VARCHAR2(4000 CHAR);
    MI_CONSECUTIVO            NUMBER;
    MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                PCK_SUBTIPOS.TI_VALORES;  
    MI_RTAMSG                 VARCHAR2(200 CHAR);
    MI_REEMPLAZO              PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_STR_AUXILIAR           VARCHAR2(4000 CHAR);
    MI_STR_REFERENCIA         VARCHAR2(4000 CHAR);
    MI_STR_FUENTE_RECURSO     VARCHAR2(4000 CHAR);
  BEGIN
    MI_VALORBASE := UN_VALORBASE;
    MI_CONSECUTIVO := UN_CONSECUTIVO;
    IF NVL(UN_DESCRIPCION, ' ') = ' ' THEN
      MI_DESCRIPCION := ' ';
     ELSE
      MI_DESCRIPCION := UN_DESCRIPCION;
    END IF;
    IF NVL(UN_STRCENTRO_COSTO, ' ') = ' ' THEN
      MI_STRCENTRO_COSTO := PCK_DATOS.FC_CONS_CENTRO();
    ELSE 
      MI_STRCENTRO_COSTO := UN_STRCENTRO_COSTO;
    END IF;
    IF NVL(UN_AUXILIAR, ' ') = ' ' THEN 
    MI_STR_AUXILIAR := PCK_DATOS.FC_CONS_AUXILIAR;
    ELSE
    MI_STR_AUXILIAR := UN_AUXILIAR;
    END IF;
    IF NVL(UN_REFERENCIA, ' ') = ' ' THEN 
    MI_STR_REFERENCIA := PCK_DATOS.FC_CONS_REFERENCIA;
    ELSE
    MI_STR_REFERENCIA  := UN_REFERENCIA;
    END IF;
    IF NVL(UN_FUENTE_RECURSO,' ') = ' ' THEN 
    MI_STR_FUENTE_RECURSO := PCK_DATOS.FC_CONS_FUENTE;
    ELSE
    MI_STR_FUENTE_RECURSO := UN_FUENTE_RECURSO;
    END IF;
    IF UN_TIENECONTENIDO <> 0 THEN
      MI_STRSQL := MI_STRSQL || '''' || UN_CUENTA_DEBITO || ''',''' || UN_CUENTA_CREDITO || '''';
      IF MI_STRSQL <> '' THEN
        MI_DATORETENCION := MI_DATORETENCION + 1;
          OPEN MI_RSDET FOR 
                    'SELECT DETALLE_COMPROBANTE_CNT.COMPANIA,
                            DETALLE_COMPROBANTE_CNT.ANO,
                            TIPO_CPTE,
                            COMPROBANTE,
                            CONSECUTIVO
                    FROM   DETALLE_COMPROBANTE_CNT 
                    WHERE  DETALLE_COMPROBANTE_CNT.COMPANIA= '''||UN_COMPANIA ||
                        '''AND DETALLE_COMPROBANTE_CNT.ANO =' ||UN_ANO||
                          'AND TIPO_CPTE = ''' ||UN_TIPO||
                        '''AND COMPROBANTE= '|| UN_NUMERO ||
                        '''AND  DETALLE_COMPROBANTE_CNT.CUENTA IN (' || MI_STRSQL || ')' ||
                    ' ORDER BY DETALLE_COMPROBANTE_CNT.COMPANIA,DETALLE_COMPROBANTE_CNT.ANO,TIPO_CPTE,COMPROBANTE,CONSECUTIVO';
          LOOP
            EXIT WHEN MI_RSDET%NOTFOUND;
            FETCH MI_RSDET 
              INTO  MI_RSDETCOMPANIA,
                    MI_RSDETANO,
                    MI_RSDETTIPO,
                    MI_RSDETCOMPROBANTE,
                    MI_RSDETCONSECUTIVO;
            BEGIN 
              BEGIN 
                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA  => 'DETALLE_COMPROBANTE_CNT', 
                                            UN_ACCION => 'E',
                                            UN_CONDICION => 'COMPANIA         = ''' || MI_RSDETCOMPANIA || 
                                                          ''' AND ANO         =   ' || MI_RSDETANO ||
                                                            ' AND TIPO_CPTE   = ''' || MI_RSDETTIPO || 
                                                          ''' AND COMPROBANTE =   ' || MI_RSDETCOMPROBANTE || 
                                                            ' AND CONSECUTIVO =   ' || MI_RSDETCONSECUTIVO);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                END; 
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN    
                PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD   => SQLCODE,
                UN_TABLAERROR => 'DETALLE_COMPROBANTE_CNT');
            END;  
          END LOOP;
      END IF;
    END IF;
    IF UN_INDILEY <> 0 THEN
    --TRAIGO LOS ACUMULADOS DE LOSPAGOS Y DE LO YA RETENIDO;
      MI_MES := TO_NUMBER(TO_CHAR(UN_FECHA, 'MM'));
      --TRAIGO EL VALOR ACUMULADO DE LAS RETENCIONES;
      BEGIN
        MI_STRSQL := 'SELECT VALORACUMULADO 
                      FROM (SELECT  COMPROBANTE_CNT.COMPANIA,  
                                    COMPROBANTE_CNT.TIPO,
                                    COMPROBANTE_CNT.ANO,  
                                    SUM(COMPROBANTE_CNTRETENCION.VALOR) AS VALORACUMULADO,  
                                    COMPROBANTE_CNT.TERCERO,  
                                    COMPROBANTE_CNT.SUCURSAL 
                            FROM COMPROBANTE_CNT 
                              INNER JOIN COMPROBANTE_CNTRETENCION 
                                ON  COMPROBANTE_CNT.COMPANIA  = COMPROBANTE_CNTRETENCION.COMPANIA 
                                AND COMPROBANTE_CNT.ANO       = COMPROBANTE_CNTRETENCION.ANO 
                                AND COMPROBANTE_CNT.TIPO      = COMPROBANTE_CNTRETENCION.TIPO 
                                AND COMPROBANTE_CNT.NUMERO    = COMPROBANTE_CNTRETENCION.NUMERO
                              INNER JOIN RETENCIONES 
                                ON  COMPROBANTE_CNTRETENCION.COMPANIA         = RETENCIONES.COMPANIA 
                                AND COMPROBANTE_CNTRETENCION.TIPORETENCION    = RETENCIONES.TIPO 
                                AND COMPROBANTE_CNTRETENCION.ANO              = RETENCIONES.ANO 
                                AND COMPROBANTE_CNTRETENCION.CODIGORETENCION  = RETENCIONES.CODIGO 
                      WHERE COMPROBANTE_CNT.COMPANIA =''' || UN_COMPANIA || '''
                        AND COMPROBANTE_CNT.TIPO =''' || UN_TIPO || '''
                        AND COMPROBANTE_CNT.ANO =' || UN_ANO || '
                        AND COMPROBANTE_CNT.TERCERO =''' || UN_TERCERO || '''
                        AND COMPROBANTE_CNT.SUCURSAL =''' || UN_SUCURSAL || ''' 
                        AND RETENCIONES.ALEY1450 NOT IN (0)
                        AND TO_CHAR(FECHA) = ' || MI_MES ||'
                      GROUP BY  COMPROBANTE_CNT.COMPANIA, 
                                COMPROBANTE_CNT.TIPO, 
                                COMPROBANTE_CNT.ANO, 
                                COMPROBANTE_CNT.TERCERO, 
                                COMPROBANTE_CNT.SUCURSAL';
        EXECUTE IMMEDIATE MI_STRSQL INTO MI_VALORACUMULADO;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_VALORACUMULADO := 0;
      END;

      BEGIN
        MI_STRSQL :=  ' SELECT SUM(VLR_DOCUMENTO) AS SVLR_DOCUMENTO ' ||
                      ' FROM COMPROBANTE_CNT ' ||
                      ' WHERE COMPANIA        = ''' || UN_COMPANIA || '''
                          AND ANO             =   ' || UN_ANO || '
                          AND TIPO            = ''' || UN_TIPO || '''
                          AND TO_CHAR(FECHA)  =   ' || MI_MES || '
                          AND LEY1450 NOT IN(0) 
                          AND TERCERO         = ''' || UN_TERCERO || ''' 
                          AND SUCURSAL        = ''' || UN_SUCURSAL || '''
                        GROUP BY COMPANIA, ANO, TIPO';

      EXECUTE IMMEDIATE MI_STRSQL INTO MI_VALORB;
        EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_VALORBASE := NULL;
      END;
    END IF;
    --SI HAY RETENCIONES ENTRA A CALCULAR LOS VALORES;
    MI_CUENTA := '';
    --COGE EL RESPECTIVO VALOR BASE, DEPENDIENDO SI ES IVA O NO.';
    -- MODIFICACION AL VALOR BASE DEL IVA POR ADRIANAS EL 04/09/2007, POR MANEJO DE IVA SEGUN CORPORINOQUIA, QUE TIENEN 2 CUENTAS IVA Y DIVIDEN 50 Y 50%, SE CREA EL SIGUIENTE PARAMENTRO;
    /*DM: En Access, el parámetro MANEJA PROCESO ESPECIAL RETENCIONES IVA, no estaba haciendo nada, por lo que se removieron los conidiconales.*/

    MI_VALORB   := NVL(MI_VALORBASE, 0);
    MI_VALORBG  := NVL (MI_VALORBASE, 0);
    --EMPIEZA CON EL PROCESO DE C�LCULO DE RETENCIONES;
    IF MI_VALORB <= NVL(UN_LIMITE_INF, 0) THEN
      RETURN 0;            
    END IF;
    IF NVL(UN_VALOR_APLICAR, 0) = 0 THEN
      MI_VALORB := ROUND( (MI_VALORB * NVL(UN_PCT_BASE, 0)) / 100, 2);
      MI_VALORRETENER := ROUND((MI_VALORB * NVL(UN_PCT_APLICAR, 0)) / 100, 2);
    ELSE
      MI_VALORRETENER := NVL (UN_VALOR_APLICAR, 0);
    END IF;

    IF NVL(UN_FACTORREDONDEO, 0) <> 0 THEN
      MI_VALORRETENER := ROUND(TRUNC(MI_VALORRETENER / NVL(UN_FACTORREDONDEO, 0) + 0.5001) * NVL(UN_FACTORREDONDEO, 0), 2);
    END IF;

    MI_VALORRETENER := MI_VALORRETENER - NVL(MI_VALORACUMULADO, 0);

    IF MI_VALORRETENER < 0 THEN
      MI_VALORRETENER := 0;
    END IF;
    IF NVL(UN_CUENTA_CREDITO, ' ') <> ' ' THEN
      MI_CODIGO_CUENTA := NVL(UN_CUENTA_CREDITO, ' ');
    ELSIF NVL(UN_CUENTA_DEBITO, ' ') <> ' ' THEN
      MI_CODIGO_CUENTA := NVL(UN_CUENTA_DEBITO, ' ');
    ELSE
      BEGIN 
        BEGIN 
          MI_CAMPOS := 'CODIGO,ORDEN,MENSAJE';
          MI_VALORES :=   UN_CONSECMENSAJES||' , '||
                          PCK_CONTABILIDAD2.FC_ORDENMENSAJES(UN_CONSECMENSAJES)||',
                          ''El código de retención: ' || CHR(13) || 
                          NVL(UN_TIPORETENCION, '') || ' - ' || NVL(UN_CODIGORETENCION, '') || 
                          ' no tiene cuenta contable relacionada''';
          MI_RTAMSG := PCK_DATOS.FC_ACME( UN_TABLA => 'TEMP_CALCULO_RETENCIONES', 
                                          UN_ACCION => 'I',
                                          UN_CAMPOS => MI_CAMPOS,
                                          UN_VALORES => MI_VALORES); 

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END; 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN    
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD   => SQLCODE,
                  UN_ERROR_COD => PCK_ERRORES.ERR_INSERTCONT
              );
      END;  
      RETURN 0;
    END IF;
    MI_STRNAT := SUBSTR('CDCCCDDDDC', TO_NUMBER(LPAD(MI_CODIGO_CUENTA, 1)) + 1, 1);
    --IF NVL(PERMITEMODIFICAR, FALSE) = FALSE AND NVL(VALOR, 0) <> 0 THEN
    --VALORRETENER = NVL(VALOR, 0);
    --END IF;
    IF MI_VALORRETENER <> 0 THEN
            --AQUI SE AGREGA EL PARAMETRO PARA TRAER LA RETENCION CON EL CENTRO DE COSTO CODIFICADO EN LA CODIFICACION DE RETENCIONES;
            /*DM: Las 3 líneas siguientes se pusieron en comentario, pues no estaban realizando cambio alguno.*/
            --IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'MANEJA RETENCIONES POR CENTRO DE COSTO', UN_MODULO, SYSDATE) = 'SI' THEN
                --MI_STRCENTRO_COSTO := STRCENTRO_COSTO;
            --END IF;
            /*FIN DM.*/

      BEGIN
        SELECT DISTINCT CUENTA_PPTAL 
        INTO MI_CUENTAPPTAL
        FROM V_PLAN_CONTABLE 
        WHERE COMPANIA  = UN_COMPANIA 
          AND ANO       = UN_ANO 
          AND CODIGO    = MI_CODIGO_CUENTA;
        EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_CUENTAPPTAL := '';
        END;

         MI_CONSECUTIVO:= PCK_SYSMAN_UTL.FC_GENCONSECUTIVO( UN_TABLA    => 'DETALLE_COMPROBANTE_CNT', 
                                                            UN_CRITERIO => 'COMPANIA    = '''||UN_COMPANIA||'''
                                                                        AND ANO         = '||UN_ANO||' 
                                                                        AND TIPO_CPTE   = '''||UN_TIPO||'''
                                                                        AND COMPROBANTE = '||UN_NUMERO||'', 
                                                            UN_CAMPO    => 'CONSECUTIVO');         

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
                      TERCERO,
                      SUCURSAL,
                      AUXILIAR,
                      DESCRIPCION, 
                      CUENTAPPTAL,
                      EJECUCION_DEBITO, 
                      EJECUCION_CREDITO,
                      BASE_IVA, 
                      NRO_DOCUMENTO,
                      FUENTE_RECURSO,
                      REFERENCIA';
        MI_VALORES:= '''' || UN_COMPANIA|| ''', 
                        ' || UN_ANO|| ',
                      ''' || UN_TIPO|| ''', 
                        ' || UN_NUMERO|| ',
                        ' || MI_CONSECUTIVO|| ', 
                      ''' || MI_CODIGO_CUENTA|| ''', 
               TO_DATE('''||UN_FECHA||''',''DD/MM/YYYY''),
                      ''' || MI_STRNAT|| ''',
                         '||(CASE WHEN NVL(UN_CUENTA_CREDITO, ' ') <> ' ' THEN 0 ELSE MI_VALORRETENER END) || ', 
                        ' ||(CASE WHEN NVL(UN_CUENTA_DEBITO, ' ') <> ' ' THEN 0 ELSE MI_VALORRETENER END) || ',
                        ' ||MI_VALORBG|| ',
                      ''' || MI_STRCENTRO_COSTO || ''',
                      ''' || NVL(UN_TERCERO, PCK_DATOS.FC_CONS_TERCERO()) || ''', 
                      ''' || NVL(UN_SUCURSAL, PCK_DATOS.FC_CONS_SUCURSAL()) || ''',
                         '||MI_STR_AUXILIAR||',
                      ''' || MI_DESCRIPCION || ''',
                      ''' || NVL(MI_CUENTAPPTAL, '') || ''',
                        ' ||(CASE WHEN NVL(UN_CUENTA_CREDITO, ' ') <> ' ' THEN 0 ELSE MI_VALORRETENER END) || ', 
                        ' ||(CASE WHEN NVL(UN_CUENTA_DEBITO, ' ') <> ' ' THEN 0 ELSE MI_VALORRETENER END) || ', 
                        ' || NVL(UN_VALORBASEIVA, 0) || ', 
                      ''' || UN_NRO_DOCUMENTO || ''',
                      ''' || MI_STR_FUENTE_RECURSO || ''',
                      ''' || MI_STR_REFERENCIA || '''';
        BEGIN 
          BEGIN 
            MI_RTA := PCK_DATOS.FC_ACME(  UN_TABLA    => 'DETALLE_COMPROBANTE_CNT', 
                                          UN_ACCION   => 'I',
                                          UN_CAMPOS   => MI_CAMPOS,
                                          UN_VALORES  => MI_VALORES);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
          END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN   
          MI_REEMPLAZO(1).CLAVE := 'TIPO';
          MI_REEMPLAZO(1).VALOR := UN_TIPO;
          MI_REEMPLAZO(2).CLAVE := 'UN_NUMERO';
          MI_REEMPLAZO(2).VALOR := UN_NUMERO;
          MI_REEMPLAZO(3).CLAVE := 'RET';
          MI_REEMPLAZO(3).VALOR := UN_CODIGORETENCION;
          MI_REEMPLAZO(4).CLAVE := 'TIPO_RET';
          MI_REEMPLAZO(4).VALOR := UN_TIPORETENCION;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD   => SQLCODE,
                UN_REEMPLAZOS => MI_REEMPLAZO,
                UN_ERROR_COD => PCK_ERRORES.ERR_DETAINSRET
              );
        END; 
    END IF;
  --      RS.EDIT;
  --      RS!VALOR = VALORRETENER;
  --      RS.UPDATE;
    BEGIN 
      BEGIN 
        MI_RTA := PCK_DATOS.FC_ACME(  UN_TABLA => 'COMPROBANTE_CNTRETENCION',
                                      UN_ACCION => 'M', 
                                      UN_CAMPOS => 'VALOR = ' || MI_VALORRETENER,
                                      UN_CONDICION => ' COMPANIA   = ''' || UN_COMPANIA || '''
                                                    AND ANO              =   ' || UN_ANO || '
                                                    AND TIPO             = ''' || UN_TIPO || '''
                                                    AND NUMERO           =   ' || UN_NUMERO || '
                                                    AND TIPORETENCION    = ''' || UN_TIPORETENCION || '''
                                                    AND CODIGORETENCION  = ''' || UN_CODIGORETENCION || '''');
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;    
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN    
          MI_REEMPLAZO(1).CLAVE := 'TIPO_RET';
          MI_REEMPLAZO(1).VALOR := UN_TIPORETENCION;
          MI_REEMPLAZO(2).CLAVE := 'RET';
          MI_REEMPLAZO(2).VALOR := UN_CODIGORETENCION;
     PCK_ERR_MSG.RAISE_WITH_MSG(
             UN_EXC_COD   => SQLCODE,
             UN_REEMPLAZOS => MI_REEMPLAZO,
             UN_ERROR_COD => PCK_ERRORES.ERR_ACTCPTERETE
             );
    END;
    RETURN -1;
END FC_SINRETENCIONESLEY1450;

    FUNCTION FC_CALCULORETENCIONESLEY1607(
        /*
        NAME              : FC_CALCULORETENCIONESLEY1607 (En Access - CalculoRetencionesley1607 dentro del módulo distribución de Contabilidad)
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : Diego Maldonado
        DATE MIGRADOR     : 12/04/2016
        TIME              : 04:00 PM
        SOURCE MODULE     : Contabilidad - SysmanCT2016.02.06
        MODIFIER          : 
        DATE MODIFIED     : 
        TIME              : 
        DESCRIPTION       : Se llama en la función calcularley1450
        @NAME:  calcularRetencionesPagosLaborales1607
        @METHOD:  GET
        */    
        UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
        UN_MODULO           IN PCK_SUBTIPOS.TI_MODULO,
        UN_CONSECMENSAJES   IN NUMBER,
        UN_ANO              IN PCK_SUBTIPOS.TI_ANIO,
        UN_FECHA            IN DATE,
        UN_TIPO             IN DETALLE_COMPROBANTE_CNT.TIPO_CPTE%TYPE,
        UN_NUMERO           IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
        UN_TERCERO          IN PCK_SUBTIPOS.TI_TERCERO,
        UN_SUCURSAL         IN PCK_SUBTIPOS.TI_SUCURSAL,
        UN_NOMBRETERCERO    IN PLAN_CONTABLE.NOMBRE%TYPE,
        UN_VALORBASE        IN NUMBER,
        UN_VALORBASEIVA     IN PCK_SUBTIPOS.TI_DOBLE,
        UN_DESCRIPCION      IN VARCHAR2 DEFAULT NULL,
        UN_STRCENTRO_COSTO  IN PCK_SUBTIPOS.TI_CENTRO_COSTO DEFAULT NULL,
        UN_NRO_DOCUMENTO    IN DETALLE_COMPROBANTE_CNT.NRO_DOCUMENTO%TYPE DEFAULT NULL )
    RETURN NUMBER AS
        MI_ERROR_FUN                  PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 10;
        MI_CALCULORETENCIONESLEY1607  NUMBER := 0;
        MI_RS                         SYS_REFCURSOR;
        MI_STRSQL                     PCK_SUBTIPOS.TI_STRSQL;
        MI_RTA                        VARCHAR2(4000 CHAR);
        MI_CONSECUTIVO                NUMBER;
        MI_STRCENTRO_COSTO            VARCHAR2(30 CHAR);
        MI_TIENECONTENIDO             NUMBER;
        MI_INDILEY                    NUMBER;
        MI_DESCRIPCION                VARCHAR2(4000 CHAR);
        MI_CONTEO                     NUMBER;
        MI_RTAMSG                     VARCHAR2(200 CHAR);
    BEGIN
        IF NVL(UN_DESCRIPCION, '') = '' THEN
            MI_DESCRIPCION := '';
        ELSE
            MI_DESCRIPCION := UN_DESCRIPCION;
        END IF;

        IF NVL(MI_STRCENTRO_COSTO, '') = '' THEN
            MI_STRCENTRO_COSTO       := PCK_DATOS.FC_CONS_CENTRO();
        ELSE
            MI_STRCENTRO_COSTO := UN_STRCENTRO_COSTO;
        END IF;

        MI_CONSECUTIVO   := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO( UN_TABLA    => 'DETALLE_COMPROBANTE_CNT', 
                                                              UN_CRITERIO => 'COMPANIA    = '''||UN_COMPANIA||'''
                                                                         AND ANO          = '||UN_ANO||' 
                                                                         AND TIPO_CPTE    = '''||UN_TIPO||'''
                                                                         AND COMPROBANTE  = '||UN_NUMERO||'', 
                                                                UN_CAMPO    => 'CONSECUTIVO', UN_INICIAL => '1');


        IF MI_CONSECUTIVO <> 0 THEN
            MI_TIENECONTENIDO  := -1;
        ELSE
            MI_TIENECONTENIDO := 0;
        END IF;

        SELECT COUNT('X')
        INTO MI_CONTEO
        FROM COMPROBANTE_CNTRETENCION LEFT JOIN RETENCIONES ON 
            COMPROBANTE_CNTRETENCION.COMPANIA = RETENCIONES.COMPANIA 
            AND COMPROBANTE_CNTRETENCION.ANO = RETENCIONES.ANO 
            AND COMPROBANTE_CNTRETENCION.TIPORETENCION = RETENCIONES.TIPO
            AND COMPROBANTE_CNTRETENCION.CODIGORETENCION = RETENCIONES.CODIGO 
        WHERE  COMPROBANTE_CNTRETENCION.COMPANIA  = UN_COMPANIA 
            AND COMPROBANTE_CNTRETENCION.ANO      = UN_ANO 
            AND COMPROBANTE_CNTRETENCION.TIPO     = UN_TIPO 
            AND COMPROBANTE_CNTRETENCION.NUMERO   = UN_NUMERO;

        IF MI_CONTEO IS NULL OR MI_CONTEO <= 0 THEN
            MI_RTAMSG := PCK_DATOS.FC_ACME('TEMP_CALCULO_RETENCIONES', 'I', 'CODIGO,ORDEN,MENSAJE',
                                              UN_CONSECMENSAJES||' , '||PCK_CONTABILIDAD2.FC_ORDENMENSAJES(UN_CONSECMENSAJES)||',
                                              ''No se han codificado retenciones para este comprobante.'''
                                              , NULL, NULL, NULL, NULL, NULL, NULL) ; 
            MI_CALCULORETENCIONESLEY1607 := 0;
            RETURN MI_CALCULORETENCIONESLEY1607;
        END IF;

		<<VERIFICARETENCION>>
        FOR MI_RS IN (SELECT COMPROBANTE_CNTRETENCION.TIPORETENCION,  
                          COMPROBANTE_CNTRETENCION.CODIGORETENCION,  
                          COMPROBANTE_CNTRETENCION.VALOR, 
                          COMPROBANTE_CNTRETENCION.VALORBASE, 
                          COMPROBANTE_CNTRETENCION.PORCIVA,  
                          RETENCIONES.CUENTA_DEBITO,  
                          RETENCIONES.CUENTA_CREDITO,  
                          RETENCIONES.PCT_BASE,  
                          RETENCIONES.LIMITE_INF,  
                          RETENCIONES.PCT_APLICAR,  
                          RETENCIONES.VALOR_APLICAR,  
                          RETENCIONES.FACTORREDONDEO, 
                          RETENCIONES.PERMITEMODIFICAR, 
                          RETENCIONES.CENTRO_COSTO ,  
                          RETENCIONES.ALEY1450,  
                          RETENCIONES.CUENTA_DEBITO1,  
                          RETENCIONES.CUENTA_CREDITO1 
                      FROM COMPROBANTE_CNTRETENCION LEFT JOIN RETENCIONES ON 
                          COMPROBANTE_CNTRETENCION.COMPANIA = RETENCIONES.COMPANIA 
                          AND COMPROBANTE_CNTRETENCION.ANO = RETENCIONES.ANO 
                          AND COMPROBANTE_CNTRETENCION.TIPORETENCION = RETENCIONES.TIPO
                          AND COMPROBANTE_CNTRETENCION.CODIGORETENCION = RETENCIONES.CODIGO 
                      WHERE  COMPROBANTE_CNTRETENCION.COMPANIA  = UN_COMPANIA 
                          AND COMPROBANTE_CNTRETENCION.ANO      = UN_ANO 
                          AND COMPROBANTE_CNTRETENCION.TIPO     = UN_TIPO 
                          AND COMPROBANTE_CNTRETENCION.NUMERO   = UN_NUMERO
                      ORDER  BY COMPROBANTE_CNTRETENCION.COMPANIA,
                          COMPROBANTE_CNTRETENCION.ANO,
                          COMPROBANTE_CNTRETENCION.TIPO,
                          COMPROBANTE_CNTRETENCION.NUMERO,
                          COMPROBANTE_CNTRETENCION.TIPORETENCION,
                          COMPROBANTE_CNTRETENCION.CODIGORETENCION) 
        LOOP
            IF MI_RS.ALEY1450 <> 0 THEN
                MI_INDILEY := -1;
                MI_CONSECUTIVO := MI_CONSECUTIVO + 1; 
                MI_RTA     := PCK_CONTABILIDAD2.FC_CONRETENCIONESLEY1450( UN_COMPANIA, UN_MODULO, UN_CONSECMENSAJES, 
                                                                          UN_ANO, UN_FECHA, UN_TIPO, 
                                                                          UN_NUMERO, UN_TERCERO, 
                                                                          UN_SUCURSAL, UN_NOMBRETERCERO,
                                                                          NVL(MI_RS.VALORBASE, 0),
                                                                          NVL(UN_VALORBASEIVA, 0),
                                                                          MI_CONSECUTIVO, 
                                                                          MI_TIENECONTENIDO, 
                                                                          NVL(MI_RS.CUENTA_DEBITO1, ''), 
                                                                          NVL(MI_RS.CUENTA_CREDITO1, ''), 
                                                                          NVL(MI_RS.TIPORETENCION, ''), 
                                                                          NVL(MI_RS.CUENTA_DEBITO, ''), 
                                                                          NVL(MI_RS.CUENTA_CREDITO, ''), 
                                                                          NVL(MI_RS.FACTORREDONDEO, 0), 
                                                                          NVL(MI_RS.CODIGORETENCION, ''), 
                                                                          MI_DESCRIPCION, 
                                                                          MI_STRCENTRO_COSTO, 
                                                                          NVL(UN_NRO_DOCUMENTO, ''));
            ELSE
                MI_INDILEY := 0;
                MI_CONSECUTIVO := MI_CONSECUTIVO + 1; 
                MI_RTA     := PCK_CONTABILIDAD2.FC_SINRETENCIONESLEY1450( UN_COMPANIA,  UN_CONSECMENSAJES, 
                                                                          UN_ANO, UN_FECHA, UN_TIPO, 
                                                                          UN_NUMERO, UN_TERCERO, 
                                                                          UN_SUCURSAL,  
                                                                          NVL(MI_RS.VALORBASE, 0), 
                                                                          NVL(UN_VALORBASEIVA, 0), 
                                                                          MI_CONSECUTIVO, 
                                                                          MI_TIENECONTENIDO, 
                                                                          NVL(MI_RS.CUENTA_DEBITO, ''), 
                                                                          NVL(MI_RS.CUENTA_CREDITO, ''), 
                                                                          NVL(MI_RS.TIPORETENCION, ''), 
                                                                          NVL(MI_RS.LIMITE_INF, 0), 
                                                                          NVL(MI_RS.PCT_BASE, 0), 
                                                                          NVL(MI_RS.PCT_APLICAR, 0), 
                                                                          NVL(MI_RS.VALOR_APLICAR, 0), 
                                                                          NVL(MI_RS.FACTORREDONDEO, 0), 
                                                                          NVL(MI_RS.CODIGORETENCION, ''), 
                                                                          MI_INDILEY, MI_DESCRIPCION, 
                                                                          MI_STRCENTRO_COSTO, 
                                                                          NVL(UN_NRO_DOCUMENTO, ''));
            END IF;
        END LOOP VERIFICARETENCION;
        MI_CALCULORETENCIONESLEY1607 := -1;
        RETURN MI_CALCULORETENCIONESLEY1607;
        EXCEPTION WHEN OTHERS THEN
            PCK_DATOS.GL_ERROR_MSG := 'Interrupción durante el cálculo de retenciones con ley 1607.';
            PCK_DATOS.GL_ERROR_MSG := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'CONTABILIDAD2','',SQLERRM );
            RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );

    END FC_CALCULORETENCIONESLEY1607;

   FUNCTION FC_CALCULARLEY1450(
        /*
        NAME              : FC_CALCULARLEY1450 (En Access - calcularley1450 dentro del módulo distribución de Contabilidad)
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : Diego Maldonado
        DATE MIGRADOR     : 13/04/2016
        TIME              : 11:00 AM
        SOURCE MODULE     : Contabilidad - SysmanCT2016.02.06
        AUTHOR MODIFIED   : LAURA MELIZA BOTIA PEREZ 
        MODIFIER          : Se agrego el calculo de la retencion de la ley 1819
        DATE MODIFIED     : 22/08/2018
        TIME              : 08:00 AM
        AUTHOR MODIFIED   : JOSE PASCUAL GOMEZ
        MODIFIER          : Se optimiza el funcionamiento con un cursor implicito
        DATE MODIFIED     : 28/08/2018
        TIME              : 02:00 PM
        DESCRIPTION       : Se llama en el evento del botón CalcularRetenciones
        @NAME:  calcularRetencionesPagosLaboralesLey1450
        @METHOD:  GET
        */
        UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA, 
        UN_MODULO           IN PCK_SUBTIPOS.TI_MODULO,
        UN_CONSECMENSAJES   IN NUMBER,
        UN_ANO              IN PCK_SUBTIPOS.TI_ANIO, 
        UN_FECHA            IN DATE,
        UN_TIPO             IN COMPROBANTE_CNT.TIPO%TYPE, 
        UN_NUMERO           IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT, 
        UN_TERCERO          IN PCK_SUBTIPOS.TI_TERCERO, 
        UN_SUCURSAL         IN PCK_SUBTIPOS.TI_SUCURSAL, 
      	UN_NOMBRETERCERO    IN PLAN_CONTABLE.NOMBRE%TYPE, 
        UN_VALORBASE        IN NUMBER, 
        UN_VALORBASEIVA     IN PCK_SUBTIPOS.TI_DOBLE, 
        UN_DESCRIPCION      IN VARCHAR2 DEFAULT NULL, 
        UN_STRCENTRO_COSTO  IN PCK_SUBTIPOS.TI_CENTRO_COSTO DEFAULT NULL, 
        UN_NRO_DOCUMENTO    IN DETALLE_COMPROBANTE_CNT.NRO_DOCUMENTO%TYPE DEFAULT NULL,
        UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO

)RETURN NUMBER AS 

      MI_ERROR_FUN          PCK_SUBTIPOS.TI_ERROR_FUN := PCK_DATOS.GL_ERROR_NUM +11;
      MI_CALCULARLEY1450    NUMBER := 0;
      MI_RS                 SYS_REFCURSOR;
      MI_RTA                NUMBER;
      MI_RTAMSG             VARCHAR2(200 CHAR);
      MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
      MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
      MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
BEGIN
    FOR RS IN (SELECT LEY1450, LEY1819
               FROM TERCERO
               WHERE COMPANIA = UN_COMPANIA
                 AND NIT      = UN_TERCERO
                 AND SUCURSAL = UN_SUCURSAL
                 AND (LEY1450 NOT IN(0) OR LEY1819 NOT IN(0))
             )
    LOOP
        IF RS.LEY1450 <>0 THEN
            MI_VALORES := UN_CONSECMENSAJES||' , '||PCK_CONTABILIDAD2.FC_ORDENMENSAJES(UN_CONSECMENSAJES)||','' Al tercero seleccionado se le aplicara retención según ley 1607.' || CHR(13) || CHR(13) || 'la retención de este pago será calculada basada en esta norma ''';
        ELSIF RS.LEY1819<>0 THEN
            MI_VALORES := UN_CONSECMENSAJES||' , '||PCK_CONTABILIDAD2.FC_ORDENMENSAJES(UN_CONSECMENSAJES)||','' Al tercero seleccionado se le aplicara retención según ley 1819.' || CHR(13) || CHR(13) || 'la retención de este pago será calculada basada en esta norma ''';
        END IF;
        MI_TABLA   := 'TEMP_CALCULO_RETENCIONES';
        MI_CAMPOS  := 'CODIGO,ORDEN,MENSAJE';
        BEGIN
            BEGIN
                MI_RTAMSG := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA, 
                                               UN_ACCION  => 'I', 
                                               UN_CAMPOS  => MI_CAMPOS,
                                               UN_VALORES => MI_VALORES
                                               );                                            
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    =>  SQLCODE,
                                        UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_MENSAJETEMP,
                                        UN_TABLAERROR => MI_TABLA);
        END;
        IF RS.LEY1450 <>0 THEN
            MI_TABLA        := 'COMPROBANTE_CNT';     
            MI_CAMPOS       := 'LEY1450 = -1';
            MI_CONDICION := ' COMPANIA    =''' || UN_COMPANIA|| '''
                               AND ANO    =  ' || UN_ANO     || ' 
                               AND TIPO   =''' || UN_TIPO    || '''
                               AND NUMERO =  ' || UN_NUMERO;
            MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                            UN_ACCION    => 'M', 
                                            UN_CAMPOS    => MI_CAMPOS,
                                            UN_CONDICION => MI_CONDICION);
            MI_RTA := PCK_CONTABILIDAD2.FC_CALCULORETENCIONESLEY1607( UN_COMPANIA, 
                                                                      UN_MODULO, 
                                                                      UN_CONSECMENSAJES, 
                                                                      UN_ANO, 
                                                                      UN_FECHA, 
                                                                      UN_TIPO, 
                                                                      UN_NUMERO, 
                                                                      UN_TERCERO, 
                                                                      UN_SUCURSAL, 
                                                                      UN_NOMBRETERCERO, 
                                                                      NVL(UN_VALORBASE, 0), 
                                                                      NVL(UN_VALORBASEIVA, 0), 
                                                                      UN_DESCRIPCION, 
                                                                      NVL(UN_STRCENTRO_COSTO, 
                                                                      PCK_DATOS.FC_CONS_CENTRO()), 
                                                                      NVL(UN_NRO_DOCUMENTO, '')
                                                                         );    

            IF MI_RTA = 0 THEN
                MI_CALCULARLEY1450 := 0;
            ELSE
                MI_CALCULARLEY1450 := -1;
            END IF; 
        ELSIF RS.LEY1819<>0 THEN
            MI_RTA := PCK_CONTABILIDAD7.FC_CALCULORETENCIONESLEY1819 (UN_COMPANIA       => UN_COMPANIA, 
                                                                      UN_MODULO         => UN_MODULO, 
                                                                      UN_CONSECMENSAJES => UN_CONSECMENSAJES, 
                                                                      UN_ANO            => UN_ANO, 
                                                                      UN_FECHA          => UN_FECHA, 
                                                                      UN_TIPO           => UN_TIPO, 
                                                                      UN_NUMERO         => UN_NUMERO, 
                                                                      UN_TERCERO        => UN_TERCERO, 
                                                                      UN_SUCURSAL       => UN_SUCURSAL, 
                                                                      UN_NOMBRETERCERO  => UN_NOMBRETERCERO, 
                                                                      UN_VALORBASE      => NVL(UN_VALORBASE, 0), 
                                                                      UN_VALORBASEIVA   => NVL(UN_VALORBASEIVA, 0), 
                                                                      UN_DESCRIPCION    => UN_DESCRIPCION, 
                                                                      UN_STRCENTRO_COSTO => NVL(UN_STRCENTRO_COSTO, PCK_DATOS.FC_CONS_CENTRO()), 
                                                                      UN_NRO_DOCUMENTO   => NVL(UN_NRO_DOCUMENTO, ''),
                                                                      UN_USUARIO         => UN_USUARIO 
                                                                    );
            IF MI_RTA = 0 THEN 
                MI_CALCULARLEY1450 := 0;
            ELSE
                MI_CALCULARLEY1450 := -1;
            END IF; 
        END IF;
    END LOOP;
    RETURN MI_CALCULARLEY1450; 
    /*
EXCEPTION WHEN OTHERS THEN
    PCK_DATOS.GL_ERROR_MSG := 'Interrupción durante el cálculo de ley 1450.';
    PCK_DATOS.GL_ERROR_MSG := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, 
                                                              PCK_DATOS.GL_ERROR_MSG,  
                                                              'CONTABILIDAD2',
                                                              '',
                                                              SQLERRM );
    RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );  
*/
END FC_CALCULARLEY1450;

    FUNCTION FC_CONSECUTIVOMENSAJES
        /*
        NAME              : FC_CONSECUTIVOMENSAJES 
        AUTHORS           : Diego Maldonado
        AUTHOR MIGRACION  : Diego Maldonado
        DATE MIGRADOR     : 13/04/2016
        TIME              : 03:00 PM
        SOURCE MODULE     : 
        MODIFIER          : 
        DATE MODIFIED     : 
        TIME              : 
        DESCRIPTION       : Función creada con el fin de manejar los consecutivos de mensajes 
                            informativos o de error en el proceso de cálculo de retenciones
                            desde el formulario Comprobante_Cnt
        @NAME:  generarConsecutivoParaMensajes
        @METHOD:  GET                            
        */
    RETURN NUMBER AS
    MI_RTA NUMBER;
    BEGIN
        SELECT NVL(MAX(CODIGO),0)+1
        INTO MI_RTA
        FROM TEMP_CALCULO_RETENCIONES;

        RETURN MI_RTA;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_RTA := 1;
            RETURN MI_RTA;
    END FC_CONSECUTIVOMENSAJES;

    FUNCTION FC_ORDENMENSAJES(
        /*
        NAME              : FC_ORDENMENSAJES 
        AUTHORS           : Diego Maldonado
        AUTHOR MIGRACION  : Diego Maldonado
        DATE MIGRADOR     : 13/04/2016
        TIME              : 04:30 PM
        SOURCE MODULE     : 
        MODIFIER          : 
        DATE MODIFIED     : 
        TIME              : 
        DESCRIPTION       : Función creada con el fin de manejar los ordenes de mensajes 
                            informativos o de error con un mismo consecutivo en el proceso de cálculo de retenciones
                            desde el formulario Comprobante_Cnt
        @NAME:  ordenarConsecutivoParaMensajes
        @METHOD:  GET                            
        */
        UN_CONSECUTIVO IN NUMBER
    )RETURN NUMBER AS
    MI_RTA NUMBER;
    BEGIN
        SELECT NVL(MAX(ORDEN),0)+1
        INTO MI_RTA
        FROM TEMP_CALCULO_RETENCIONES
        WHERE CODIGO = UN_CONSECUTIVO;

        RETURN MI_RTA;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_RTA := 1;
            RETURN MI_RTA;
    END FC_ORDENMENSAJES;

   PROCEDURE FC_COMPACOPIAR_AFTERUPDATE(
        /*
        NAME              : FC_COMPACOPIAR_AFTERUPDATE 
        AUTHORS           : SYSMAN SAS
        AUTHOR MIGRACION  : Diego Maldonado
        DATE MIGRADOR     : 18/04/2016
        TIME              : 08:10 AM
        SOURCE MODULE     : Contabilidad - SysmanCT2016.02.06
        MODIFIER          : Sandra Milena Daza Leguizamon
        DATE MODIFIED     : 03/11/2022
        TIME              : 
        DESCRIPTION       : Función que realiza las inserciones indicadas 
                            en el evento 'después de actualizar' 
                            del combo CompACopiar dentro del formulario Comprobante_Cnt
                            03/11/2022 - se adiciona control de error para informar si la cuenta que tiene el detalle se encuentra
                            bloqueada para la vigencia en la que se va a usar. Adicionalmente se modifica el control de error de los 
                            insert
        @NAME:  copiarComprobanteContable
        @METHOD:  POST
        */
        UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA, 
        UN_ANOCOPIAR      IN PCK_SUBTIPOS.TI_ANIO, 
        UN_TIPOCOPIAR     IN DETALLE_COMPROBANTE_CNT.TIPO_CPTE%TYPE, 
        UN_NUMEROCOPIAR   IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
        UN_TERCERO        IN PCK_SUBTIPOS.TI_TERCERO,
        UN_SUCURSAL       IN PCK_SUBTIPOS.TI_SUCURSAL,
        UN_ANO            IN PCK_SUBTIPOS.TI_ANIO,
        UN_TIPO           IN DETALLE_COMPROBANTE_CNT.TIPO_CPTE%TYPE,
        UN_NUMERO         IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
        UN_FECHA          IN DATE,
        UN_DESCRIPCION    IN DETALLE_COMPROBANTE_CNT.DESCRIPCION%TYPE,
        UN_NRO_DOCUMENTO  IN DETALLE_COMPROBANTE_CNT.NRO_DOCUMENTO%TYPE,
        UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
    ) AS
        MI_ERROR_FUN                PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 14;
        MI_RSDETALLE                SYS_REFCURSOR; 
        MI_INTCONTADOR              NUMBER; 
        MI_STRSQL                   PCK_SUBTIPOS.TI_STRSQL; 
        MI_TERCEROT                 VARCHAR2(4000 CHAR); 
        MI_SUCURSALT                VARCHAR2(4000 CHAR); 
        MI_STRETAPA                 VARCHAR2(4000 CHAR); 
        MI_RSVERIFICACION           SYS_REFCURSOR; 
        MI_CADENA                   VARCHAR2(4000 CHAR);
        MI_CAMPOS                   PCK_SUBTIPOS.TI_CAMPOS;
        MI_VALORES                  PCK_SUBTIPOS.TI_VALORES;
        MI_EXISTE_DETALLE           NUMBER := 0;
        MI_RTA                      NUMBER;
        MI_CUENTABLOQUEADA          PLAN_CONTABLE.BLOQUEACUENTA%TYPE;
        MI_MSGERROR                 PCK_SUBTIPOS.TI_CLAVEVALOR; 
    BEGIN
        GL_MSGLOG := NULL;
        MI_STRETAPA := '04';

		<<VERIFICADETALLE>>
    --(CC:932_CFBARRERA)Se adiciona el campo referencia, para ser tomado en cuenta en el proceso de copiar comprobante
        FOR MI_RSDETALLE IN (SELECT DETALLE_COMPROBANTE_CNT.COMPANIA,
                                DETALLE_COMPROBANTE_CNT.ANO,
                                DETALLE_COMPROBANTE_CNT.TIPO_CPTE,
                                DETALLE_COMPROBANTE_CNT.CONSECUTIVO,
                                DETALLE_COMPROBANTE_CNT.CUENTA,
                                DETALLE_COMPROBANTE_CNT.NATURALEZA,
                                DETALLE_COMPROBANTE_CNT.CUENTAPPTAL,
                                DETALLE_COMPROBANTE_CNT.VALOR_DEBITO,
                                DETALLE_COMPROBANTE_CNT.VALOR_CREDITO,
                                DETALLE_COMPROBANTE_CNT.EJECUCION_DEBITO,
                                DETALLE_COMPROBANTE_CNT.EJECUCION_CREDITO,
                                DETALLE_COMPROBANTE_CNT.BASE_GRAVABLE,
                                DETALLE_COMPROBANTE_CNT.TIPO_DOCUMENTO,
                                DETALLE_COMPROBANTE_CNT.NRO_DOCUMENTO,
                                DETALLE_COMPROBANTE_CNT.CENTRO_COSTO,
                                DETALLE_COMPROBANTE_CNT.AUXILIAR,
                                DETALLE_COMPROBANTE_CNT.TIPO_CPTE_AFECT,
                                DETALLE_COMPROBANTE_CNT.CMPTE_AFECTADO,
                                DETALLE_COMPROBANTE_CNT.CONCEPTO_EX,
                                DETALLE_COMPROBANTE_CNT.TERCERO,
                                DETALLE_COMPROBANTE_CNT.SUCURSAL,
                                DETALLE_COMPROBANTE_CNT.REFERENCIA,
                                COMPROBANTE_CNT.TERCERO AS TERCEROH,
                                COMPROBANTE_CNT.SUCURSAL AS SUCURSALH,
                                DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO,
                                DETALLE_COMPROBANTE_CNT.CONCEPTO_CUDS
                            FROM DETALLE_COMPROBANTE_CNT 
                            LEFT JOIN COMPROBANTE_CNT 
                                ON  DETALLE_COMPROBANTE_CNT.COMPANIA    = COMPROBANTE_CNT.COMPANIA 
                                AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE   = COMPROBANTE_CNT.TIPO 
                                AND DETALLE_COMPROBANTE_CNT.ANO         = COMPROBANTE_CNT.ANO
                                AND DETALLE_COMPROBANTE_CNT.COMPROBANTE = COMPROBANTE_CNT.NUMERO 
                            WHERE DETALLE_COMPROBANTE_CNT.COMPANIA      = UN_COMPANIA 
                                AND DETALLE_COMPROBANTE_CNT.ANO         = UN_ANOCOPIAR 
                                AND TIPO_CPTE                           = UN_TIPOCOPIAR
                                AND COMPROBANTE                         = UN_NUMEROCOPIAR  
                            ORDER BY  DETALLE_COMPROBANTE_CNT.COMPANIA,
                                      DETALLE_COMPROBANTE_CNT.ANO, 
                                      TIPO_CPTE,COMPROBANTE,CONSECUTIVO) LOOP
            MI_EXISTE_DETALLE := -1;
            IF MI_RSDETALLE.TERCEROH = MI_RSDETALLE.TERCERO THEN
                MI_TERCEROT  := UN_TERCERO;
                MI_SUCURSALT := UN_SUCURSAL;
            ELSE
                MI_TERCEROT  := MI_RSDETALLE.TERCERO;
                MI_SUCURSALT := MI_RSDETALLE.SUCURSAL;
            END IF;
            
               /*7722597 se adiciona control para verificar que la cuenta del detalle a insertar no este bloquedada*/
            BEGIN
                BEGIN
                    SELECT  BLOQUEACUENTA 
                    INTO    MI_CUENTABLOQUEADA
                    FROM    PLAN_CONTABLE
                    WHERE   COMPANIA    = UN_COMPANIA
                    AND     ANO         = UN_ANO
                    AND     CODIGO      = MI_RSDETALLE.CUENTA;
                    
                    IF MI_CUENTABLOQUEADA = 'SI' THEN
                        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                    END IF;
                END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_MSGERROR(1).CLAVE := 'CUENTA';
                    MI_MSGERROR(1).VALOR := MI_RSDETALLE.CUENTA;
                    MI_MSGERROR(2).CLAVE := 'VIGENCIA';
                    MI_MSGERROR(2).VALOR := UN_ANO;
                         
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE,
                        UN_TABLAERROR => 'DETALLE_COMPROBANTE_CNT',
                        UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_CUENTABLOQUEADA,
                        UN_REEMPLAZOS => MI_MSGERROR
                    );
                    
                    
   
            END;
            MI_STRETAPA := '05';
            MI_CAMPOS := 'COMPANIA,
                          ANO,
                          TIPO_CPTE,
                          COMPROBANTE,
                          CONSECUTIVO,
                          CUENTA,
                          FECHA,
                          NATURALEZA,
                          CUENTAPPTAL,
                          DESCRIPCION,
                          VALOR_DEBITO,
                          VALOR_CREDITO,
                          BASE_GRAVABLE,
                          TIPO_DOCUMENTO,
                          NRO_DOCUMENTO,
                          CENTRO_COSTO,
                          TERCERO,
                          SUCURSAL,
                          REFERENCIA,
                          AUXILIAR,
                          CHEQUEPARAANULAR,
                          CONCEPTO_EX,
                          FUENTE_RECURSO,
                          CONCEPTO_CUDS,
                          CREATED_BY, 
                          DATE_CREATED';
            MI_VALORES := '''' || MI_RSDETALLE.COMPANIA || ''',
                          ' || UN_ANO || ',
                          ''' || UN_TIPO || ''',
                          ' || UN_NUMERO || ',
                          ' || MI_RSDETALLE.CONSECUTIVO || ',
                          ''' || MI_RSDETALLE.CUENTA || ''',
                          TO_DATE(''' || TO_CHAR(UN_FECHA,'DD/MM/YYYY')|| ''',''DD/MM/YYYY''),
                          ''' || MI_RSDETALLE.NATURALEZA || ''', 
                          ''' || MI_RSDETALLE.CUENTAPPTAL || ''',
                          ''' || UN_DESCRIPCION || ''',
                          ' || NVL(MI_RSDETALLE.VALOR_DEBITO, 0) || ',
                          ' || NVL(MI_RSDETALLE.VALOR_CREDITO, 0) || ',
                          ' || NVL(MI_RSDETALLE.BASE_GRAVABLE, 0) || ',
                          ''' || MI_RSDETALLE.TIPO_DOCUMENTO || ''',
                          ''' || NVL(UN_NRO_DOCUMENTO, MI_RSDETALLE.NRO_DOCUMENTO) || ''',
                          ''' || MI_RSDETALLE.CENTRO_COSTO || ''',
                          ''' || MI_TERCEROT|| ''',
                          ''' || MI_SUCURSALT || ''',
                          ''' || MI_RSDETALLE.REFERENCIA || ''',
                          ''' || MI_RSDETALLE.AUXILIAR || ''',
                          ''Cheque para Anular'',
                          ''' || MI_RSDETALLE.CONCEPTO_EX || ''', 
                          '''||MI_RSDETALLE.FUENTE_RECURSO||''',
                          '''||MI_RSDETALLE.CONCEPTO_CUDS||''',
                          '''|| UN_USUARIO ||''', 
                          SYSDATE
                          ';
            BEGIN    
                BEGIN
                    MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => 'DETALLE_COMPROBANTE_CNT',
                                        UN_ACCION  => 'I',
                                        UN_CAMPOS  => MI_CAMPOS, 
                                        UN_VALORES => MI_VALORES);
                    IF MI_RTA = 0 THEN
                        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
                    END IF;
                    EXCEPTION WHEN OTHERS THEN
                        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;                    
                END ;
                
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                     MI_MSGERROR(1).CLAVE := 'CUENTA';
                     MI_MSGERROR(1).VALOR := MI_RSDETALLE.CUENTA;
                     PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE,
                        UN_TABLAERROR => 'DETALLE_COMPROBANTE_CNT',
                        UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_NOCREADETALLE,
                        UN_REEMPLAZOS => MI_MSGERROR
                    );
            END;
           
        END LOOP VERIFICADETALLE;

        IF MI_EXISTE_DETALLE <> 0 THEN
            MI_STRETAPA := '09';
            MI_CAMPOS := 'COMPANIA,ANO,
                          TIPO,NUMERO,
                          TIPORETENCION,
                          CODIGORETENCION,
                          VALOR,
                          VALORBASE,
                          CREATED_BY,
                          DATE_CREATED,
                          CALCULADO';
            MI_VALORES := 'SELECT COMPROBANTE_CNTRETENCION.COMPANIA,
                                  COMPROBANTE_CNTRETENCION.ANO,
                                  ''' || UN_TIPO || ''',
                                  ' || UN_NUMERO || ',
                                  COMPROBANTE_CNTRETENCION.TIPORETENCION,
                                  COMPROBANTE_CNTRETENCION.CODIGORETENCION,
                                  COMPROBANTE_CNTRETENCION.VALOR,
                                  COMPROBANTE_CNTRETENCION.VALORBASE,
                                  '''|| UN_USUARIO ||''',
                                  SYSDATE,
                                  CALCULADO ' ||
                          'FROM COMPROBANTE_CNTRETENCION ' ||
                          'WHERE COMPROBANTE_CNTRETENCION.COMPANIA=''' || UN_COMPANIA || '''
                              AND COMPROBANTE_CNTRETENCION.ANO=' || UN_ANO || '
                              AND COMPROBANTE_CNTRETENCION.TIPO=''' || UN_TIPOCOPIAR || '''
                              AND COMPROBANTE_CNTRETENCION.NUMERO=' || UN_NUMEROCOPIAR;
            BEGIN    
                BEGIN
                    MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => 'COMPROBANTE_CNTRETENCION', 
                                        UN_ACCION  => 'IS', 
                                        UN_CAMPOS  => MI_CAMPOS, 
                                        UN_VALORES => MI_VALORES);

                    EXCEPTION WHEN OTHERS THEN
                        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;                    
                END ;
                
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                     PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE,
                        UN_TABLAERROR => 'COMPROBANTE_CNTRETENCION',
                        UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_NOCREARETENCIONES
                    );
            END;
        END IF;   
    END FC_COMPACOPIAR_AFTERUPDATE;

    FUNCTION FC_NATURALEZACUENTA(
        /*
        NAME              : FC_NATURALEZACUENTA (En Access, NaturalezaCuenta dentro del módulo Contable en Contabilidad)
        AUTHORS           : SYSMAN SAS
        AUTHOR MIGRACION  : Diego Maldonado
        DATE MIGRADOR     : 20/04/2016
        TIME              : 02:10 PM
        SOURCE MODULE     : Contabilidad - SysmanCT2016.02.06
        MODIFIER          : 
        DATE MODIFIED     : 
        TIME              : 
        DESCRIPTION       : Retorna la naturaleza de una determinada cuenta contable.
        @NAME:  consultarNaturalezaCuentaContable
        @METHOD:  GET
        */
        UN_CUENTA     IN VARCHAR2
    )RETURN VARCHAR2 AS
        MI_GRUPO VARCHAR2(1);
    BEGIN
        MI_GRUPO := SUBSTR(UN_CUENTA, 1, 1);
        IF MI_GRUPO IN ('1','5','6','7','8') THEN
            RETURN 'D';
        ELSE
            RETURN 'C';
        END IF;
    END FC_NATURALEZACUENTA;

    FUNCTION FC_DISTRIBUYECONCEPTO(
        /*
        NAME              : FC_NATURALEZACUENTA (En Access, NaturalezaCuenta dentro del módulo Contable en Contabilidad)
        AUTHORS           : SYSMAN SAS
        AUTHOR MIGRACION  : Diego Maldonado
        DATE MIGRADOR     : 20/04/2016
        TIME              : 02:10 PM
        SOURCE MODULE     : Contabilidad - SysmanCT2016.02.06
        MODIFIER          : SERGIO ESTEBAN PIÑA VARGAS
        DATE MODIFIED     : 17/08/2017
        DESCRIPTION       : Agrego parametro UN_USUARIO y campos auditoria
        TIME              : 
        DESCRIPTION       : Retorna la naturaleza de una determinada cuenta contable.
        @NAME:  distribuirPorConceptosComprobanteContable
        @METHOD:  GET
        */
        UN_COMPANIA     		  IN PCK_SUBTIPOS.TI_COMPANIA, 
        UN_TIPO         		  IN DETALLE_COMPROBANTE_CNT.TIPO_CPTE%TYPE, 
        UN_NUMERO       		  IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT, 
        UN_ANO          		  IN PCK_SUBTIPOS.TI_ANIO, 
        UN_STRTIPOCOBRO 		  IN VARCHAR2, 
        UN_STRCONCEPTO  		  IN VARCHAR2,
        UN_REGIMEN      		  IN NUMBER,
        UN_VLRDOCUMENTO 		  IN NUMBER,
        UN_TIPOCOBROCONCEPTO 	IN VARCHAR2,
        UN_CONCEPTO     		  IN VARCHAR2,
        UN_FECHA        		  IN DATE,
        UN_DESCRIPCION  		  IN DETALLE_COMPROBANTE_CNT.DESCRIPCION%TYPE,
        UN_VLRBASE      		  IN NUMBER,
        UN_VLRBASEIVA   		  IN NUMBER,
        UN_CENTRO_COSTO 		  IN PCK_SUBTIPOS.TI_CENTRO_COSTO,
        UN_TERCERO      		  IN PCK_SUBTIPOS.TI_TERCERO,
        UN_SUCURSAL     		  IN PCK_SUBTIPOS.TI_SUCURSAL,
        UN_AUXILIAR     		  IN PCK_SUBTIPOS.TI_AUXILIAR,
        UN_REFERENCIA   		  IN PCK_SUBTIPOS.TI_REFERENCIA,
        UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO
    )RETURN NUMBER AS 
        MI_ERROR_FUN          PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 14;
        MI_DISTRIBUYECONCEPTO NUMBER;
        MI_STRSQL             PCK_SUBTIPOS.TI_STRSQL; 
        MI_RS                 SYS_REFCURSOR; 
        MI_RSDETALLE          SYS_REFCURSOR; 
        MI_VALORDEB           NUMBER; 
        MI_VALORCRE           NUMBER; 
        MI_VALORDEBORDEN      NUMBER; 
        MI_VALORCREORDEN      NUMBER; 
        MI_CONSECUTIVO        NUMBER; 
        MI_STRETAPA           VARCHAR2(4000 CHAR); 
        MI_TDEBITO            NUMBER;
        MI_TCREDITO           NUMBER;
        MI_DEBITOS            NUMBER;
        MI_CREDITOS           NUMBER;
        MI_RSCUENTA           VARCHAR2(50 CHAR);
        MI_RSTIPO             VARCHAR2(50 CHAR);
        MI_RTA                VARCHAR2(4000 CHAR);
        MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
        MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
    BEGIN
        MI_STRSQL := 'SELECT MAX(CONSECUTIVO) 
                      FROM DETALLE_COMPROBANTE_CNT' ||
                    ' WHERE COMPANIA        =''' || UN_COMPANIA || 
                        ''' AND ANO         =  ' || UN_ANO ||
                          ' AND TIPO_CPTE   =''' || UN_TIPO || ''' ' ||
                          ' AND COMPROBANTE =  ' || UN_NUMERO;
        EXECUTE IMMEDIATE MI_STRSQL INTO MI_CONSECUTIVO;

        MI_CONSECUTIVO := NVL(MI_CONSECUTIVO+1, 0);

        MI_STRSQL := 'SELECT NVL(SUM(CASE WHEN NVL(RETENCIONES.CUENTA_DEBITO,'') = '' 
                                          THEN 0 
                                          ELSE COMPROBANTE_CNTRETENCION.VALOR 
                                          END),0) AS TDEBITO,
                            NVL(SUM(CASE WHEN NVL(RETENCIONES.CUENTA_CREDITO,'') = '' 
                                          THEN 0 
                                          ELSE COMPROBANTE_CNTRETENCION.VALOR 
                                          END),0) AS TCREDITO ' ||
                    ' FROM RETENCIONES 
                      INNER JOIN COMPROBANTE_CNTRETENCION 
                          ON RETENCIONES.COMPANIA = COMPROBANTE_CNTRETENCION.COMPANIA 
                          AND RETENCIONES.ANO     = COMPROBANTE_CNTRETENCION.ANO 
                          AND RETENCIONES.TIPO    = COMPROBANTE_CNTRETENCION.TIPORETENCION 
                          AND RETENCIONES.CODIGO  = COMPROBANTE_CNTRETENCION.CODIGORETENCION ' ||
                    ' WHERE RETENCIONES.COMPANIA  =''' || UN_COMPANIA || ''' ' ||
                        (CASE WHEN UN_REGIMEN NOT IN (0) 
                              THEN ' AND RETENCIONES.TIPO <> ''IVA''' 
                              ELSE '' 
                              END ) ||
                    '   AND COMPROBANTE_CNTRETENCION.ANO    =  ' || UN_ANO ||
                    '   AND COMPROBANTE_CNTRETENCION.TIPO   =''' || UN_TIPO || 
                  '''   AND COMPROBANTE_CNTRETENCION.NUMERO =  ' || UN_NUMERO;

        MI_VALORDEB := UN_VLRDOCUMENTO;
        MI_VALORCRE := UN_VLRDOCUMENTO;

        EXECUTE IMMEDIATE MI_STRSQL INTO MI_TDEBITO, MI_TCREDITO;
        IF NVL(MI_TDEBITO, 0) > 0 THEN
            MI_VALORDEB := UN_VLRDOCUMENTO - MI_TDEBITO;
        END IF;
        IF NVL(MI_TCREDITO, 0) > 0 THEN
            MI_VALORCRE := UN_VLRDOCUMENTO - MI_TCREDITO;
        END IF;
        IF NVL(MI_TDEBITO, 0) > 0 OR NVL(MI_TCREDITO, 0) > 0 THEN
            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
        END IF;


        MI_STRSQL := 'SELECT NVL(SUM(VALOR_DEBITO),0) AS DEBITOS,
                              NVL(SUM(VALOR_CREDITO),0) AS CREDITOS ' ||
                    ' FROM  DETALLE_COMPROBANTE_CNT ' ||
                    ' WHERE COMPANIA        =''' || UN_COMPANIA || ''' ' ||
                    '     AND ANO           =  ' || UN_ANO ||
                    '     AND TIPO_CPTE     =''' || UN_TIPO || ''' ' ||
                    '     AND COMPROBANTE   =  ' || UN_NUMERO;

        MI_VALORDEBORDEN := MI_VALORDEB;
        MI_VALORCREORDEN := MI_VALORCRE;
        EXECUTE IMMEDIATE MI_STRSQL INTO MI_DEBITOS, MI_CREDITOS;
        MI_VALORDEBORDEN := MI_VALORDEBORDEN + ROUND(MI_DEBITOS, 2);
        MI_VALORCREORDEN := MI_VALORCREORDEN + ROUND(MI_CREDITOS, 2);       

        MI_STRSQL := 'SELECT CUENTADEBITOBASE AS CUENTA,
                          ''DEBITO'' AS TIPO ' ||
                    ' FROM CONCEPTOS_SF' ||
                    ' WHERE COMPANIA    = ''' || UN_COMPANIA || '''
                          AND ANO       = ' || UN_ANO || '
                          AND TIPOCOBRO IN(''' || UN_TIPOCOBROCONCEPTO || ''')
                          AND CODIGO    = ''' || UN_CONCEPTO || '''' ||
                    ' UNION ' ||
                    ' SELECT CUENTACREDITOBASE,
                          ''CREDITO'' AS TIPO ' ||
                    ' FROM CONCEPTOS_SF' ||
                    ' WHERE COMPANIA    = ''' || UN_COMPANIA || '''
                          AND ANO       = ' || UN_ANO || '
                          AND TIPOCOBRO IN(''' || UN_TIPOCOBROCONCEPTO || ''')
                          AND CODIGO    = ''' || UN_CONCEPTO || '''' ||
                    ' UNION' ||
                    ' SELECT CTAORDENDEUDORA_DEBITO,
                          ''CORDENDEBITO'' AS TIPO ' ||
                    ' FROM CONCEPTOS_SF' ||
                    ' WHERE COMPANIA    = ''' || UN_COMPANIA || '''
                          AND ANO       = ' || UN_ANO || '
                          AND TIPOCOBRO IN(''' || UN_TIPOCOBROCONCEPTO || ''')
                          AND CODIGO    = ''' || UN_CONCEPTO || '''' ||
                    ' UNION' ||
                    ' SELECT CTAORDENDEUDORA_CREDITO,
                          ''CORDENCREDITO'' AS TIPO ' ||
                    ' FROM CONCEPTOS_SF' ||
                    ' WHERE COMPANIA    = ''' || UN_COMPANIA || '''
                          AND ANO       = ' || UN_ANO || '
                          AND TIPOCOBRO IN(''' || UN_TIPOCOBROCONCEPTO || ''')
                          AND CODIGO    = ''' || UN_CONCEPTO || '''' ||
                    ' UNION' ||
                    ' SELECT CUENTADEBITOUTILIDAD,
                          '' AS TIPO' ||
                    ' FROM CONCEPTOS_SF' ||
                    ' WHERE COMPANIA    = ''' || UN_COMPANIA || '''
                          AND ANO       = ' || UN_ANO || '
                          AND TIPOCOBRO IN(''' || UN_TIPOCOBROCONCEPTO || ''')
                          AND CODIGO    = ''' || UN_CONCEPTO || '''' ||
                    ' UNION' ||
                    ' SELECT CUENTACREDITOUTILIDAD,
                          '' AS TIPO' ||
                    ' FROM CONCEPTOS_SF' ||
                    ' WHERE COMPANIA    = ''' || UN_COMPANIA || '''
                          AND ANO       = ' || UN_ANO || '
                          AND TIPOCOBRO IN(''' || UN_TIPOCOBROCONCEPTO || ''')
                          AND CODIGO    = ''' || UN_CONCEPTO || '''';

        MI_STRSQL := ' SELECT CUENTA, TIPO FROM ('||MI_STRSQL||') ORDER BY TIP DESC ';

        OPEN MI_RSDETALLE FOR MI_STRSQL; 
        LOOP
            EXIT WHEN MI_RSDETALLE%NOTFOUND;
            FETCH MI_RSDETALLE INTO MI_RSCUENTA, MI_RSTIPO;
            MI_STRETAPA := '04';

            IF MI_RSCUENTA IS NOT NULL AND NVL(MI_RSCUENTA, '') <> '' THEN
                MI_STRETAPA := '05';
                MI_CAMPOS := 'COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CONSECUTIVO,
                              CUENTA,FECHA,NATURALEZA,CUENTAPPTAL,DESCRIPCION,
                              VALOR_DEBITO,VALOR_CREDITO,EJECUCION_DEBITO,
                              EJECUCION_CREDITO,BASE_GRAVABLE,TIPO_DOCUMENTO,
                              NRO_DOCUMENTO,CENTRO_COSTO,TERCERO,SUCURSAL,
                              AUXILIAR,REFERENCIA,TIPO_CPTE_AFECT,CMPTE_AFECTADO,
                              CHEQUEPARAANULAR,CONCEPTO_EX,TIPOPPTAL,BASE_IVA,
                              DESEMBOLSO,SALDOCUENTA,DATE_CREATED,CREATED_BY,
                              PORCENTAJERETENCION,HORA,REVELACIONES';
                MI_VALORES :='''' || UN_COMPANIA || ''',
                                ' || UN_ANO || ',''' || UN_TIPO || ''',
                                ' || UN_NUMERO || ', ' || MI_CONSECUTIVO || ',
                              ''' || MI_RSCUENTA || ''',
                                ' || UN_FECHA|| ',''' || PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(MI_RSCUENTA) || ''', 
                                     NULL, ''' || UN_DESCRIPCION || ''',
                                ' || CASE WHEN(MI_RSTIPO = 'DEBITO') THEN MI_VALORDEB ELSE (CASE WHEN (MI_RSTIPO = 'CORDENDEBITO') THEN MI_VALORDEBORDEN ELSE 0 END) END || ',
                                ' || CASE WHEN(MI_RSTIPO = 'CREDITO') THEN MI_VALORCRE ELSE (CASE WHEN (MI_RSTIPO = 'CORDENCREDITO') THEN MI_VALORCREORDEN ELSE 0 END) END  || ',
                                     0,0,' || NVL(UN_VLRBASE, 0) || ','''','''',
                              ''' || NVL(UN_CENTRO_COSTO, PCK_DATOS.FC_CONS_CENTRO()) || ''',''' || NVL(UN_TERCERO, PCK_DATOS.FC_CONS_TERCERO()) || ''',
                              ''' || NVL(UN_SUCURSAL, PCK_DATOS.FC_CONS_SUCURSAL()) || ''',''' || NVL(UN_AUXILIAR, PCK_DATOS.FC_CONS_AUXILIAR()) || ''',
                              ''' || NVL(UN_REFERENCIA, PCK_DATOS.FC_CONS_REFERENCIA()) || ''','''',0,'''','''','''',
                              ' || NVL(UN_VLRBASEIVA, 0) || ',0,0,SYSDATE, ''' || UN_USUARIO || ''',0, SYSDATE,''''
                              ';

                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => 'DETALLE_COMPROBANTE_CNT', 
                                            UN_ACCION   => 'I', 
                                            UN_CAMPOS   => MI_CAMPOS, 
                                            UN_VALORES  => MI_VALORES);
                MI_STRETAPA := '06';
                IF SQL%ROWCOUNT <= 0 THEN 
                    GL_MSGLOG := 'LOS DETALLES NO SE COPIARON CON ÉXITO.' || CHR(13) || 'REVISE E INTENTE DE NUEVO.';
                    RETURN MI_DISTRIBUYECONCEPTO;
                END IF;
                MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
                MI_STRETAPA := '07';
                MI_STRETAPA := '08';
            END IF;
        END LOOP;
        MI_STRETAPA := '09';
        MI_STRETAPA := '12';
        IF MI_CONSECUTIVO = 0 THEN
            MI_DISTRIBUYECONCEPTO := 1;
        ELSE
            MI_DISTRIBUYECONCEPTO := 2;
        END IF;
        RETURN MI_DISTRIBUYECONCEPTO;

        EXCEPTION WHEN OTHERS THEN
            IF GL_MSGLOG IS NULL THEN            
                PCK_DATOS.GL_ERROR_MSG := 'Interrupción durante la etapa ' || MI_STRETAPA || ' de la distribución de concepto.' ;
            ELSE 
                PCK_DATOS.GL_ERROR_MSG := GL_MSGLOG || ' en la etapa'||MI_STRETAPA||'.' || CHR(13) || 'Por favor revise e intente de nuevo.';
            END IF;
            PCK_DATOS.GL_ERROR_MSG := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, 
                                                                      PCK_DATOS.GL_ERROR_MSG,  
                                                                      'CONTABILIDAD2',
                                                                      '',
                                                                      SQLERRM );
            RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );
    END FC_DISTRIBUYECONCEPTO;

    FUNCTION FC_LLENARFORMATOCHEQUETOTAL(
    /*
        NAME              : FC_LLENARFORMATOCHEQUETOTAL 
        AUTHORS           : SYSMAN SAS
        AUTHOR MIGRACION  : Diego Maldonado
        DATE MIGRADOR     : 
        TIME              : 
        SOURCE MODULE     : Contabilidad - SysmanCT2016.02.06
        MODIFIER          : 
        DATE MODIFIED     : 
        TIME              : 
        DESCRIPTION       : Realiza las concatenaciones para imprimir el formato de cheque de los comprobantes contables.
        @NAME:  llenarFormatoDeCheque
        @METHOD:  GET
    */        
        UN_COMPANIA 		    IN PCK_SUBTIPOS.TI_COMPANIA, 
        UN_NOMBRECOMPANIA 	IN VARCHAR2,
        UN_MODULO 			    IN PCK_SUBTIPOS.TI_MODULO,
        UN_ANO 				      IN PCK_SUBTIPOS.TI_ANIO, 
        UN_TIPO 			      IN DETALLE_COMPROBANTE_CNT.TIPO_CPTE%TYPE, 
        UN_NUMERO 			    IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT
    )RETURN VARCHAR2 AS 
        MI_LLENARFORMATOCHEQUETOTAL VARCHAR2(4000 CHAR);
        MI_FORMATOCHEQUETOTAL 		VARCHAR2(4000 CHAR);
        MI_RSBANCOS 				SYS_REFCURSOR; 
        MI_STRSQL 					PCK_SUBTIPOS.TI_STRSQL; 
        MI_TEMPORAL 				VARCHAR2(4000 CHAR); 
        MI_MONEDALETRAS 			VARCHAR2(4000 CHAR); 
        MI_MONEDALETRAS1 			VARCHAR2(4000 CHAR); 
        MI_MONEDALETRAS2 			VARCHAR2(4000 CHAR); 
        MI_RSVARIOSB 				SYS_REFCURSOR; 
        MI_I 						NUMBER; 
        MI_LARGOLETRAS 				NUMBER;
        MI_FORMATO 					VARCHAR2(4000 CHAR);
    BEGIN
        MI_LARGOLETRAS := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'LONGITUD DE CADENA CHEQUE IMPRESION', UN_MODULO, SYSDATE), 60);
        --   FORMATOCHEQUETOTAL = '';
        -- CONSULTA ANTIGUA SE DEMORABA 16 SEGUNDOS LA NUEVA SE DEMORA 0 SEGUNDOS;
        -- STRSQL = 'SELECT FORMATO,PLAN_CONTABLE.NOMBRE AS NOMBRECTA,CUENTA, FECHA,DETALLE_COMPROBANTE_CNT.TERCERO,DETALLE_COMPROBANTE_CNT.SUCURSAL,VALOR_CREDITO, NRO_DOCUMENTO, PLAN_CONTABLE.CLASECUENTA AS CLASECTA,TERCERO.NOMBRE AS NOMBRETERCERO ' ||
        --'FROM (DETALLE_COMPROBANTE_CNT LEFT JOIN PLAN_CONTABLE ON (DETALLE_COMPROBANTE_CNT.COMPANIA = PLAN_CONTABLE.COMPANIA) AND (DETALLE_COMPROBANTE_CNT.ANO = PLAN_CONTABLE.ANO) AND (DETALLE_COMPROBANTE_CNT.CUENTA = PLAN_CONTABLE.ID)) LEFT JOIN TERCERO ON (DETALLE_COMPROBANTE_CNT.SUCURSAL = TERCERO.SUCURSAL) AND (DETALLE_COMPROBANTE_CNT.TERCERO = TERCERO.NIT) AND (DETALLE_COMPROBANTE_CNT.COMPANIA = TERCERO.COMPANIA) ' ||
        --'WHERE DETALLE_COMPROBANTE_CNT.COMPANIA=''' || UN_COMPANIA || '''
        --AND DETALLE_COMPROBANTE_CNT.ANO=' || UN_ANO || '
        --AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE=''' || UN_TIPO || '''
        --AND DETALLE_COMPROBANTE_CNT.COMPROBANTE=' || UN_NUMERO || ' 
        --AND PLAN_CONTABLE.CLASECUENTA='B'
        --AND DETALLE_COMPROBANTE_CNT.VALOR_CREDITO>0 ORDER BY DETALLE_COMPROBANTE_CNT.COMPANIA,DETALLE_COMPROBANTE_CNT.ANO,DETALLE_COMPROBANTE_CNT.TIPO_CPTE,DETALLE_COMPROBANTE_CNT.COMPROBANTE,DETALLE_COMPROBANTE_CNT.CONSECUTIVO,DETALLE_COMPROBANTE_CNT.CUENTA';
        --SE MODIFICA PARA QUE TOTALICE CUANDO MANEJA GIROS POR VARIOS BANCOS;

        MI_STRSQL := 'SELECT MIN(PLAN_CONTABLE.FORMATO) KEEP (DENSE_RANK FIRST ORDER BY ROWNUM) AS FORMATO' ||
                    ' FROM DETALLE_COMPROBANTE_CNT INNER JOIN PLAN_CONTABLE ON 
                          DETALLE_COMPROBANTE_CNT.COMPANIA = PLAN_CONTABLE.COMPANIA 
                              AND DETALLE_COMPROBANTE_CNT.ANO = PLAN_CONTABLE.ANO 
                              AND DETALLE_COMPROBANTE_CNT.CUENTA = PLAN_CONTABLE.CODIGO 
                      INNER JOIN COMPROBANTE_CNT ON 
                          DETALLE_COMPROBANTE_CNT.COMPANIA = COMPROBANTE_CNT.COMPANIA 
                              AND DETALLE_COMPROBANTE_CNT.ANO = COMPROBANTE_CNT.ANO 
                              AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = COMPROBANTE_CNT.TIPO 
                              AND DETALLE_COMPROBANTE_CNT.COMPROBANTE = COMPROBANTE_CNT.NUMERO 
                      INNER JOIN TERCERO ON 
                          COMPROBANTE_CNT.COMPANIA = TERCERO.COMPANIA 
                              AND COMPROBANTE_CNT.TERCERO = TERCERO.NIT 
                              AND COMPROBANTE_CNT.SUCURSAL = TERCERO.SUCURSAL  ' ||
                    ' WHERE DETALLE_COMPROBANTE_CNT.COMPANIA=''' || UN_COMPANIA || '''
                          AND DETALLE_COMPROBANTE_CNT.ANO=' || UN_ANO || '
                          AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE=''' || UN_TIPO || '''
                          AND DETALLE_COMPROBANTE_CNT.COMPROBANTE=' || UN_NUMERO || '
                          AND DETALLE_COMPROBANTE_CNT.VALOR_CREDITO>0
                          AND PLAN_CONTABLE.CLASECUENTA=''B''  ';

        EXECUTE IMMEDIATE MI_STRSQL INTO MI_FORMATO;

		<<FORMATOCHEQUE>>
        FOR MI_RSBANCOS  IN (SELECT 
                            MIN(PLAN_CONTABLE.NOMBRE) KEEP (DENSE_RANK FIRST ORDER BY ROWNUM) AS NOMBRECTA,
                            MIN(DETALLE_COMPROBANTE_CNT.CUENTA) KEEP (DENSE_RANK FIRST ORDER BY ROWNUM) AS CUENTAI,
                            MIN(DETALLE_COMPROBANTE_CNT.FECHA) KEEP (DENSE_RANK FIRST ORDER BY ROWNUM)AS FECHAI,
                            MIN(COMPROBANTE_CNT.TERCERO) KEEP (DENSE_RANK FIRST ORDER BY ROWNUM)AS TERCEROI,
                            MIN(COMPROBANTE_CNT.TERCERO) KEEP (DENSE_RANK FIRST ORDER BY ROWNUM)AS NITTERCERO ,
                            MIN(COMPROBANTE_CNT.SUCURSAL) KEEP (DENSE_RANK FIRST ORDER BY ROWNUM) AS SUCURSAL,
                            MIN(DETALLE_COMPROBANTE_CNT.NRO_DOCUMENTO) KEEP (DENSE_RANK FIRST ORDER BY ROWNUM)AS NRO_DOCUMENTOI,
                            MIN(PLAN_CONTABLE.CLASECUENTA) KEEP (DENSE_RANK FIRST ORDER BY ROWNUM) AS CLASECTA,
                            MIN(TERCERO.NOMBRE) KEEP (DENSE_RANK FIRST ORDER BY ROWNUM)AS NOMBRETERCERO,
                            SUM(DETALLE_COMPROBANTE_CNT.VALOR_CREDITO)AS VALORCREDITO
                      FROM DETALLE_COMPROBANTE_CNT INNER JOIN PLAN_CONTABLE ON 
                          DETALLE_COMPROBANTE_CNT.COMPANIA = PLAN_CONTABLE.COMPANIA 
                              AND DETALLE_COMPROBANTE_CNT.ANO = PLAN_CONTABLE.ANO 
                              AND DETALLE_COMPROBANTE_CNT.CUENTA = PLAN_CONTABLE.CODIGO 
                      INNER JOIN COMPROBANTE_CNT ON 
                          DETALLE_COMPROBANTE_CNT.COMPANIA = COMPROBANTE_CNT.COMPANIA 
                              AND DETALLE_COMPROBANTE_CNT.ANO = COMPROBANTE_CNT.ANO 
                              AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = COMPROBANTE_CNT.TIPO 
                              AND DETALLE_COMPROBANTE_CNT.COMPROBANTE = COMPROBANTE_CNT.NUMERO 
                      INNER JOIN TERCERO ON 
                          COMPROBANTE_CNT.COMPANIA = TERCERO.COMPANIA 
                              AND COMPROBANTE_CNT.TERCERO = TERCERO.NIT 
                              AND COMPROBANTE_CNT.SUCURSAL = TERCERO.SUCURSAL
                      WHERE DETALLE_COMPROBANTE_CNT.COMPANIA = UN_COMPANIA
                          AND DETALLE_COMPROBANTE_CNT.ANO=  UN_ANO 
                          AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE=  UN_TIPO 
                          AND DETALLE_COMPROBANTE_CNT.COMPROBANTE = UN_NUMERO 
                          AND DETALLE_COMPROBANTE_CNT.VALOR_CREDITO>0
                          AND PLAN_CONTABLE.CLASECUENTA='B') 
        LOOP
            IF MI_RSBANCOS.VALORCREDITO IS NOT NULL THEN
                IF NVL(MI_FORMATO, ' ') <> ' ' THEN
                    MI_MONEDALETRAS := PCK_SYSMAN_UTL.FC_VALOR_LETRAS(NVL(MI_RSBANCOS.VALORCREDITO, 0));
                    IF LENGTH(MI_MONEDALETRAS) > MI_LARGOLETRAS THEN
                        MI_MONEDALETRAS1 := SUBSTR(MI_MONEDALETRAS, 0, MI_LARGOLETRAS);
                        MI_TEMPORAL := MI_MONEDALETRAS1;
                        FOR MI_I IN 0..(LENGTH(MI_MONEDALETRAS1)) LOOP
                            IF MI_TEMPORAL IS NULL THEN
                                MI_MONEDALETRAS1 := SUBSTR(MI_MONEDALETRAS,0, MI_LARGOLETRAS);
                                MI_MONEDALETRAS2 := SUBSTR(MI_MONEDALETRAS, LENGTH(MI_MONEDALETRAS1),LENGTH(MI_MONEDALETRAS) );
                                EXIT;
                            ELSE
                                MI_TEMPORAL := SUBSTR(MI_TEMPORAL,0, MI_LARGOLETRAS - MI_I);
                            END IF;
                        END LOOP;
                    ELSE
                        MI_MONEDALETRAS1 := MI_MONEDALETRAS;
                        MI_MONEDALETRAS2 := '';
                    END IF;
                    MI_FORMATOCHEQUETOTAL := MI_FORMATO;
                    MI_FORMATOCHEQUETOTAL := REGEXP_REPLACE(MI_FORMATOCHEQUETOTAL, 'VALOR', TRIM(TO_CHAR(NVL(MI_RSBANCOS.VALORCREDITO, 0), '999G999G999G999G999G999D99')),1,0,'i');
                    MI_FORMATOCHEQUETOTAL := REGEXP_REPLACE(MI_FORMATOCHEQUETOTAL, 'FECHALARGA', TRIM(TO_CHAR(NVL(MI_RSBANCOS.FECHAI,SYSDATE),'Day')) ||', ' || TO_CHAR(NVL(MI_RSBANCOS.FECHAI,SYSDATE),'DD')|| ' de '||TRIM(TO_CHAR(NVL(MI_RSBANCOS.FECHAI,SYSDATE),'Month'))|| ' de ' || TO_CHAR(NVL(MI_RSBANCOS.FECHAI,SYSDATE),'YYYY'),1,0,'i');
                    MI_FORMATOCHEQUETOTAL := REGEXP_REPLACE(MI_FORMATOCHEQUETOTAL, 'FECHA', TO_CHAR(NVL(MI_RSBANCOS.FECHAI,SYSDATE), 'DD/Mon/YYYY'),1,0,'i');
                    MI_FORMATOCHEQUETOTAL := REGEXP_REPLACE(MI_FORMATOCHEQUETOTAL, 'MONTOCOMPLETO', MI_MONEDALETRAS,1,0,'i');
                    MI_FORMATOCHEQUETOTAL := REGEXP_REPLACE(MI_FORMATOCHEQUETOTAL, 'MONTOPARTE1', MI_MONEDALETRAS1,1,0,'i');
                    MI_FORMATOCHEQUETOTAL := REGEXP_REPLACE(MI_FORMATOCHEQUETOTAL, 'MONTOPARTE2', MI_MONEDALETRAS2,1,0,'i');
                    MI_FORMATOCHEQUETOTAL := REGEXP_REPLACE(MI_FORMATOCHEQUETOTAL, 'ANO', TO_CHAR(NVL(MI_RSBANCOS.FECHAI,SYSDATE), 'YYYY'),1,0,'i');
                    MI_FORMATOCHEQUETOTAL := REGEXP_REPLACE(MI_FORMATOCHEQUETOTAL, 'MESNUMERO', TO_CHAR(NVL(MI_RSBANCOS.FECHAI,SYSDATE), 'MM'),1,0,'i');
                    MI_FORMATOCHEQUETOTAL := REGEXP_REPLACE(MI_FORMATOCHEQUETOTAL, 'MES', TO_CHAR(NVL(MI_RSBANCOS.FECHAI,SYSDATE), 'Mon'));
                    MI_FORMATOCHEQUETOTAL := REGEXP_REPLACE(MI_FORMATOCHEQUETOTAL, 'DIA', TO_CHAR(NVL(MI_RSBANCOS.FECHAI,SYSDATE), 'DD'),1,0,'i');
                    MI_FORMATOCHEQUETOTAL := REGEXP_REPLACE(MI_FORMATOCHEQUETOTAL, 'NITBENEFICIARIO', NVL(MI_RSBANCOS.NITTERCERO, ''),1,0,'i');
                    MI_FORMATOCHEQUETOTAL := REGEXP_REPLACE(MI_FORMATOCHEQUETOTAL, 'BENEFICIARIO', NVL(MI_RSBANCOS.NOMBRETERCERO, ''),1,0,'i');
                    MI_FORMATOCHEQUETOTAL := REGEXP_REPLACE(MI_FORMATOCHEQUETOTAL, 'TITULAR', UN_NOMBRECOMPANIA,1,0,'i');
                END IF;
            END IF;
        END LOOP FORMATOCHEQUE;
        IF INSTR(MI_FORMATOCHEQUETOTAL, '#') > 0 THEN
            MI_LLENARFORMATOCHEQUETOTAL := SUBSTR(MI_FORMATOCHEQUETOTAL, 3, LENGTH(MI_FORMATOCHEQUETOTAL));
        ELSE
            MI_LLENARFORMATOCHEQUETOTAL := MI_FORMATOCHEQUETOTAL;
        END IF;
        RETURN MI_LLENARFORMATOCHEQUETOTAL;

    END FC_LLENARFORMATOCHEQUETOTAL;
--18    
FUNCTION FC_ACTUALIZARDETALLECNT
/*
    NAME              : FC_ACTUALIZARDETALLECNT (En Access, metodos cambiar en el módulo del formulario Comprobante_Cnt.
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : Diego Maldonado
    DATE MIGRADOR     : 11/10/2016
    TIME              : 05:10 PM
    DESCRIPTION       : Permite realizar las actualizaciones pertinentes al detalle del Comprobante Contable.
    SOURCE MODULE     : SysmanCT2016.02.06.accdb
    MODIFIER          : YESIKA PAOLA BECERRA CASTRO 
    DATE MODIFIED     : 16/06/2018
    DESCRIPTION       : Se habilita la sección comentareada, en la que se actualiza el tercero, 
                        se agregan excepciones correspondientes
    TIME              : 08:24AM

  @NAME:  modificarAuxiliaresEnDetallesContables
  @METHOD:  GET        
*/
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO          IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPO         IN DETALLE_COMPROBANTE_CNT.TIPO_CPTE%TYPE,
    UN_COMPROBANTE  IN DETALLE_COMPROBANTE_CNT.COMPROBANTE%TYPE,
    UN_FECHAA       IN VARCHAR2,
    UN_TERCEROA     IN DETALLE_COMPROBANTE_CNT.TERCERO%TYPE,
    UN_SUCURSALA    IN DETALLE_COMPROBANTE_CNT.SUCURSAL%TYPE,
    UN_DESCRIPCIONA IN VARCHAR2,
    UN_NUMERODOCA   IN VARCHAR2,
    UN_REFERENCIAA  IN DETALLE_COMPROBANTE_CNT.REFERENCIA%TYPE,
    UN_AUXILIARA    IN DETALLE_COMPROBANTE_CNT.AUXILIAR%TYPE,
    UN_FECHAN       IN VARCHAR2,
    UN_TERCERON     IN DETALLE_COMPROBANTE_CNT.TERCERO%TYPE,
    UN_SUCURSALN    IN DETALLE_COMPROBANTE_CNT.SUCURSAL%TYPE,
    UN_DESCRIPCIONN IN VARCHAR2,
    UN_NUMERODOCN   IN VARCHAR2,
    UN_REFERENCIAN  IN DETALLE_COMPROBANTE_CNT.REFERENCIA%TYPE,
    UN_AUXILIARN    IN DETALLE_COMPROBANTE_CNT.AUXILIAR%TYPE
  )
  RETURN NUMBER
  AS
  MI_ACTUALIZARDETALLECNT     NUMBER := 0;
  MI_STRSQL                   PCK_SUBTIPOS.TI_STRSQL;
  MI_CONTEO                   PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_PARAMETRO                VARCHAR2(10 CHAR);   
  MI_RTA                      PCK_SUBTIPOS.TI_RTA_ACME;
  MI_MODULO                   PCK_SUBTIPOS.TI_MODULO;
  MI_TABLA                    PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS                   PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION                PCK_SUBTIPOS.TI_CONDICION;
  MI_REEMPLAZOS               PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN
    MI_MODULO   := PCK_DATOS.MODULOCONTABILIDAD;
    MI_TABLA    := 'DETALLE_COMPROBANTE_CNT';

    IF (UN_TERCERON <> UN_TERCEROA) OR (UN_SUCURSALN <> UN_SUCURSALA) THEN
      MI_STRSQL := 'SELECT COUNT(''X'') CONTEO
                    FROM DETALLE_COMPROBANTE_CNT
                    WHERE COMPANIA      = ''' || UN_COMPANIA || '''
                      AND ANO           =   ' || UN_ANO || '
                      AND TIPO_CPTE     = ''' || UN_TIPO || '''
                      AND COMPROBANTE   =   ' || UN_COMPROBANTE || '
                      AND TERCERO       = ''' || UN_TERCEROA || '''
                      AND SUCURSAL      = ''' || UN_SUCURSALA || '''';

      EXECUTE IMMEDIATE MI_STRSQL INTO MI_CONTEO;

      IF MI_CONTEO > 0 THEN
        MI_CAMPOS := 'TERCERO         = '''||UN_TERCERON||''',
                      SUCURSAL        = '''||UN_SUCURSALN||''',
                      DATE_MODIFIED   = SYSDATE';
        MI_CONDICION := ' COMPANIA        = '''||UN_COMPANIA||'''   
                      AND ANO             = '||UN_ANO||' 
                      AND TIPO_CPTE       = '''||UN_TIPO||'''
                      AND COMPROBANTE     = '||UN_COMPROBANTE||'
                      AND TERCERO         = '''||UN_TERCEROA||'''
                      AND SUCURSAL        = '''||UN_SUCURSALA||'''';
        BEGIN 
          BEGIN 
            MI_RTA := PCK_DATOS.FC_ACME(  UN_TABLA        => MI_TABLA, 
                                          UN_ACCION       => 'M',
                                          UN_CAMPOS       => MI_CAMPOS,
                                          UN_CONDICION    => MI_CONDICION
                                        );
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
          END;    
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN 
          MI_REEMPLAZOS(0).CLAVE:='DETALLE';
          MI_REEMPLAZOS(0).VALOR:='el Tercero';
          PCK_ERR_MSG.RAISE_WITH_MSG ( 
                                      UN_EXC_COD    => SQLCODE,
                                      UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_ACTDET,
                                      UN_TABLAERROR => MI_TABLA,
                                      UN_REEMPLAZOS => MI_REEMPLAZOS
                                      );    
        END;           
      END IF;
    END IF;
    IF UN_FECHAA <> UN_FECHAN THEN
      MI_CAMPOS := '  FECHA           = '||UN_FECHAN||' ,
                      DATE_MODIFIED   = SYSDATE';

      MI_CONDICION := ' COMPANIA        = '''||UN_COMPANIA||'''
                    AND ANO             = '||UN_ANO||'
                    AND TIPO_CPTE       = '''||UN_TIPO||'''
                    AND COMPROBANTE     = '||UN_COMPROBANTE||'';
      BEGIN 
        BEGIN 
          MI_RTA := PCK_DATOS.FC_ACME(  UN_TABLA        => MI_TABLA, 
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_CAMPOS,
                                        UN_CONDICION    => MI_CONDICION
                                     );
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
        END;    
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN 
        MI_REEMPLAZOS(0).CLAVE:='DETALLE';
        MI_REEMPLAZOS(0).VALOR:='la Fecha';
        PCK_ERR_MSG.RAISE_WITH_MSG ( 
                                    UN_EXC_COD    => SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_ACTDET,
                                    UN_TABLAERROR => MI_TABLA,
                                    UN_REEMPLAZOS => MI_REEMPLAZOS
                                    );    
      END;       
    END IF;

    IF UN_NUMERODOCN <> NVL(UN_NUMERODOCA,'1')  THEN
      MI_CAMPOS := 'NRO_DOCUMENTO   = '''||UN_NUMERODOCN||''',
                    DATE_MODIFIED   = SYSDATE';

      MI_CONDICION := ' COMPANIA        = '''||UN_COMPANIA||'''
                    AND ANO             = '||UN_ANO||'
                    AND TIPO_CPTE       = '''||UN_TIPO||'''
                    AND COMPROBANTE     = '||UN_COMPROBANTE||'';   
      BEGIN 
        BEGIN 
          MI_RTA := PCK_DATOS.FC_ACME(  UN_TABLA        => MI_TABLA, 
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_CAMPOS,
                                        UN_CONDICION    => MI_CONDICION
                                     );
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
        END;    
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN 
        MI_REEMPLAZOS(0).CLAVE:='DETALLE';
        MI_REEMPLAZOS(0).VALOR:='el Nro de Documento';
        PCK_ERR_MSG.RAISE_WITH_MSG ( 
                                    UN_EXC_COD    => SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_ACTDET,
                                    UN_TABLAERROR => MI_TABLA,
                                    UN_REEMPLAZOS => MI_REEMPLAZOS
                                    );   
        END;                     
      END IF;
      IF UN_DESCRIPCIONN <> NVL(UN_DESCRIPCIONA,'1') THEN
        MI_PARAMETRO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA, 'PERMITE ACTUALIZAR DESCRIPCIÓN DE DETALLE', MI_MODULO, SYSDATE);
        MI_PARAMETRO := NVL(MI_PARAMETRO, 'NO');        

        MI_CAMPOS := 'DESCRIPCION     = '''||UN_DESCRIPCIONN||''',
                      DATE_MODIFIED   = SYSDATE';

        IF MI_PARAMETRO = 'SI' THEN
          MI_CONDICION := ' COMPANIA        = '''||UN_COMPANIA||'''
                        AND ANO             = '||UN_ANO||'
                        AND TIPO_CPTE       = '''||UN_TIPO||'''
                        AND COMPROBANTE     = '||UN_COMPROBANTE||'';
          BEGIN 
            BEGIN 
              MI_RTA := PCK_DATOS.FC_ACME( UN_TABLA        => MI_TABLA, 
                                           UN_ACCION       => 'M',
                                           UN_CAMPOS       => MI_CAMPOS,
                                           UN_CONDICION    => MI_CONDICION
                                          );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
              RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
            END;    
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN 
            MI_REEMPLAZOS(0).CLAVE:='DETALLE';
            MI_REEMPLAZOS(0).VALOR:='la Descripción';
            PCK_ERR_MSG.RAISE_WITH_MSG( 
                                      UN_EXC_COD    => SQLCODE,
                                      UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_ACTDET,
                                      UN_TABLAERROR => MI_TABLA,
                                      UN_REEMPLAZOS => MI_REEMPLAZOS
                                      );   
          END;                            
          ELSE
            MI_CONDICION := ' COMPANIA        = '''||UN_COMPANIA||'''
                          AND ANO             = '||UN_ANO||'
                          AND TIPO_CPTE       = '''||UN_TIPO||'''
                          AND COMPROBANTE     = '||UN_COMPROBANTE||'
                          AND DESCRIPCION     = '''||UN_DESCRIPCIONA||'''';
            BEGIN 
              BEGIN 
                MI_RTA := PCK_DATOS.FC_ACME(  UN_TABLA        => MI_TABLA, 
                                              UN_ACCION       => 'M',
                                              UN_CAMPOS       => MI_CAMPOS,
                                              UN_CONDICION    => MI_CONDICION
                                            );
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
              END;    
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN 
              MI_REEMPLAZOS(0).CLAVE:='DETALLE';
              MI_REEMPLAZOS(0).VALOR:='la Descripción';
              PCK_ERR_MSG.RAISE_WITH_MSG( 
                                        UN_EXC_COD    => SQLCODE,
                                        UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_ACTDET,
                                        UN_TABLAERROR => MI_TABLA,
                                        UN_REEMPLAZOS => MI_REEMPLAZOS
                                        );   
            END;      
          END IF;
        END IF;

        IF UN_REFERENCIAN <> UN_REFERENCIAA THEN
          MI_CAMPOS := '  REFERENCIA = '''||UN_REFERENCIAN||''',
                          DATE_MODIFIED = SYSDATE';

          MI_CONDICION := ' COMPANIA    = '''||UN_COMPANIA||'''
                        AND ANO         = '||UN_ANO||'
                        AND TIPO_CPTE   = '''||UN_TIPO||'''
                        AND COMPROBANTE = '||UN_COMPROBANTE||'
                        AND REFERENCIA  = '''||UN_REFERENCIAA||'''';            

          BEGIN 
            BEGIN 
              MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA        => MI_TABLA, 
                                          UN_ACCION       => 'M',
                                          UN_CAMPOS       => MI_CAMPOS,
                                          UN_CONDICION    => MI_CONDICION
                                          );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
              RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
            END;    
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN 
            MI_REEMPLAZOS(0).CLAVE:='DETALLE';
            MI_REEMPLAZOS(0).VALOR:='la Referencia';
            PCK_ERR_MSG.RAISE_WITH_MSG( 
                                        UN_EXC_COD    => SQLCODE,
                                        UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_ACTDET,
                                        UN_TABLAERROR => MI_TABLA,
                                        UN_REEMPLAZOS => MI_REEMPLAZOS
                                      );   
          END;                   
        END IF;
        IF UN_AUXILIARN <> UN_AUXILIARA THEN
          MI_CAMPOS := 'AUXILIAR = '''||UN_AUXILIARN||''',
                        DATE_MODIFIED = SYSDATE';
          MI_CONDICION := ' COMPANIA            = '''||UN_COMPANIA||'''
                        AND ANO             = '||UN_ANO||'
                        AND TIPO_CPTE       = '''||UN_TIPO||'''
                        AND COMPROBANTE     = '||UN_COMPROBANTE||'
                        AND AUXILIAR        = '''||UN_AUXILIARA||'''';

          BEGIN 
            BEGIN 
              MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA        => MI_TABLA, 
                                          UN_ACCION       => 'M',
                                          UN_CAMPOS       => MI_CAMPOS,
                                          UN_CONDICION    => MI_CONDICION
                                           );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
              RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
            END;    
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN 
            MI_REEMPLAZOS(0).CLAVE:='DETALLE';
            MI_REEMPLAZOS(0).VALOR:='el Auxiliar';
            PCK_ERR_MSG.RAISE_WITH_MSG ( 
                                        UN_EXC_COD    => SQLCODE,
                                        UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_ACTDET,
                                        UN_TABLAERROR => MI_TABLA,
                                        UN_REEMPLAZOS => MI_REEMPLAZOS
                                        );   
          END;    
        END IF; 

        IF MI_ACTUALIZARDETALLECNT <> 1 THEN
            MI_ACTUALIZARDETALLECNT := -1;
        END IF;
     RETURN MI_ACTUALIZARDETALLECNT;        
END FC_ACTUALIZARDETALLECNT;   


    PROCEDURE PR_ACTUALIZAR_VALOR_PAGOS
      /*
        NAME              : PR_ACTUALIZAR_VALOR_PAGOS (En Access, parte del evento Impresora_Click del formulario de Comprobante_Cnt.
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : Diego Maldonado
        DATE MIGRADOR     : 20/10/2016
        TIME              : 10:35 AM
        SOURCE MODULE     : SysmanCT2016.02.06.accdb
        MODIFIER          : 
        DATE MODIFIED     : 
        TIME              : 
        DESCRIPTION       : Procedimiento de actualización del valor pagado en contratos. 
                            Hace parte del evento del botón Imprimir en el formulario 
                            principal de Comprobantes Contables.
        @NAME:  actualizarValorPagosEnComprobantesContables
        @METHOD:  PUT        
      */
    (
      UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_ANO                IN PCK_SUBTIPOS.TI_ANIO,
      UN_TIPO               IN VARCHAR2,
      UN_NUMERO             IN NUMBER,
      UN_FECHA              IN VARCHAR2,
      UN_FECHA_VCN_DOC      IN VARCHAR2,
      UN_VLR_DOCUMENTO      IN NUMBER,
      UN_VLR_DOCUMENTO_ANT  IN NUMBER,
      UN_TIPO_CONTRATO      IN VARCHAR2,
      UN_NUMERO_CONTRATO    IN NUMBER,
      UN_CLASE              IN VARCHAR2,
      UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO
    )AS
      MI_VERIFICAR_VALOR_ANTERIOR NUMBER(1) := 0;
      MI_VALOR_CONTRATO           PCK_SUBTIPOS.TI_DOBLE := 0;
      MI_STRSQL                   PCK_SUBTIPOS.TI_STRSQL;
      MI_PAGOS                    PCK_SUBTIPOS.TI_DOBLE := 0;
      MI_RTA                      NUMBER(1) := 0;
      MI_CAMPOS                   PCK_SUBTIPOS.TI_CAMPOS;
      MI_CONDICION                PCK_SUBTIPOS.TI_CONDICION;
	  MI_AUTOMATICA               VARCHAR2(1 CHAR);											   
    BEGIN
      <<VerificarValorAnterior>>
      BEGIN
        IF UN_VLR_DOCUMENTO_ANT <> UN_VLR_DOCUMENTO 
          THEN 
            MI_VALOR_CONTRATO := UN_VLR_DOCUMENTO - UN_VLR_DOCUMENTO_ANT;
            MI_VERIFICAR_VALOR_ANTERIOR := -1;
          ELSE 
            MI_VERIFICAR_VALOR_ANTERIOR := 0;
        END IF;
      EXCEPTION WHEN VALUE_ERROR THEN
        MI_VERIFICAR_VALOR_ANTERIOR := 0;
      END VerificarValorAnterior;

      BEGIN
        IF UN_TIPO_CONTRATO IS NOT NULL AND NVL(UN_NUMERO_CONTRATO,0) <> 0 
          THEN
		  --TICKET 7723881(JCROJAS)25/01/2024: Se agrega validación para que el registro en la tabla NOVEDADCONTRATO solo se inserte cuando la clase del tipo de novedad sea Automatica.
          BEGIN
            SELECT TAUTOMATICA  
            INTO MI_AUTOMATICA
            FROM CLASETRANSACCIONC  
            WHERE TIPOT = UN_TIPO 
                AND CLASET = 'C';
          EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_AUTOMATICA := '';  
          END;
          
          IF MI_AUTOMATICA = 'A' THEN					 
            MI_RTA := PCK_GENERALES.FC_DISPARANOVEDAD(UN_COMPANIA           => UN_COMPANIA, 
                                                      UN_SMODULO            => 'C',
                                                      UN_STIPONOVEDAD       => UN_TIPO,
                                                      UN_LANO               => UN_ANO,
                                                      UN_DNUMERO            => UN_NUMERO,
                                                      UN_DFECHAINICIAL      => UN_FECHA,
                                                      UN_DFECHAFINAL        => UN_FECHA,
                                                      UN_DFECHAVENCIMIENTO  => UN_FECHA_VCN_DOC,
                                                      UN_DVALOR             => UN_VLR_DOCUMENTO,
                                                      UN_STIPOCONTRATO      => UN_TIPO_CONTRATO,
                                                      UN_DNUMEROCONTRATO    => UN_NUMERO_CONTRATO,
                                                      UN_USUARIO            => UN_USUARIO);
		  END IF;		 
            IF UN_CLASE = 'E' 
              THEN
                BEGIN
                  MI_STRSQL := 'SELECT SUM(VLRAGIRAR) AS PAGOS
                                FROM COMPROBANTE_CNT
                                WHERE COMPANIA = ''' || UN_COMPANIA || '''
                                    AND ANO = ' || UN_ANO || '
                                    AND TIPOCONTRATO = ''' || UN_TIPO_CONTRATO || '''
                                    AND NUMEROCONTRATO = ' || UN_NUMERO_CONTRATO;
                  EXECUTE IMMEDIATE MI_STRSQL INTO MI_PAGOS;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                  MI_PAGOS := 0;
                END;

                MI_CAMPOS := 'VALORPAGOS        = ' || MI_PAGOS ||', 
                              MODIFIED_BY       = '''|| UN_USUARIO ||''' ,
                              DATE_MODIFIED     = SYSDATE
                              ';
                MI_CONDICION := 'COMPANIA       = ''' || UN_COMPANIA || '''
                                AND CLASEORDEN  = ''' || UN_TIPO_CONTRATO || '''
                                AND NUMERO      = ' || UN_NUMERO_CONTRATO;

                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => 'ORDENDECOMPRA', 
                                                      UN_ACCION     => 'M', 
                                                      UN_CAMPOS     => MI_CAMPOS, 
                                                      UN_CONDICION  => MI_CONDICION);

            END IF;
        END IF;  
      END;

    END PR_ACTUALIZAR_VALOR_PAGOS;


  FUNCTION FC_NIIF_LOTES
  (
    /*
    NAME              : FC_NIIF_LOTES (En Access, niif_lotes, dentro del código de Contabilizar comprobantes NIIF)
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : Oscar Torres Corredor
    DATE MIGRADOR     : 14/04/2016
    TIME              : 08:00 AM
    SOURCE MODULE     : Contabilidad - SysmanCT2016.02.06
    MODIFIER          : Adriana Caceres Bonilla
    DATE MODIFIED     : 16-17/11/2016
    TIME              :
    DESCRIPTION       : Proceso que contabiliza los comprobantes NIIF.
    MODIFIER          : Leydi Milena Cortés Forero
    DATE MODIFIED     : 05,15,12,16,19,20/12/2016
    TIME              :
    MODIFICATIONS     : Se realizan los cambios pertinentes para implementar la función niif_lotes del módulo MGC.
                        Módulo: Contabilidad - SYSMANMGC2016.09.01
    PARAMETERS        : UN_COMPANIA    	=> Compañia de ingreso a la aplicación
                        UN_MES_INICIAL	=> Código del mes inicial desde el cual se desea contabilizar los comprobantes.
                        UN_MES_FINAL	  => Código del mes final hasta el cual se desea contabilizar los comprobantes.
                        UN_TIPO_INICIAL => Tipo de comprobante inicial desde el cual se desea contabilizar los comprobantes.
                        UN_TIPO_FINAL   => Tipo de comprobante final desde el cual se desea contabilizar los comprobantes.
                        UN_ANIO    	    => Año en el cual se realiza el proceso.
                        UN_USUARIO      => Usuario que realiza el registro.

    @NAME:  contabilizarComprobantesContablesNiif
    @METHOD:  GET
    */
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_MES_INICIAL    IN PCK_SUBTIPOS.TI_MES,
    UN_MES_FINAL      IN PCK_SUBTIPOS.TI_MES,
    UN_TIPO_INICIAL   IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
    UN_TIPO_FINAL     IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
    UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
    UN_USUARIO        IN VARCHAR2
  )
  RETURN CLOB
  AS

    MI_DESCRIPCION      VARCHAR2(250 CHAR);
    MI_NRODOCUMENTO     VARCHAR2(30 CHAR);
    MI_IDCUENTA         VARCHAR2(200 CHAR);
    MI_RESULTADO        PCK_SUBTIPOS.TI_RTA_ACME;
    MI_RESPUESTA        CLOB;
    MI_COMPANIA_NIIF    PCK_SUBTIPOS.TI_COMPANIA; 
    MI_COMPANIA		      VARCHAR2(20 CHAR);	
    MI_PLAN			        VARCHAR2(20 CHAR);
    MI_STRETAPA         VARCHAR2(200 CHAR); 
    MI_TEXTO            VARCHAR2(3200 CHAR); 
    MI_CAMPOS           VARCHAR2(3200 CHAR); 
    MI_VALORES          VARCHAR2(3200 CHAR); 
    MI_CONDICION        VARCHAR2(5000 CHAR);
    MI_FECHA_VCN        DATE; 
    MI_RTA              CLOB;
    MI_RTA1             CLOB;
    MI_INTE			        PCK_SUBTIPOS.TI_ENTERO := 0;
    MI_MERGEUSING       PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE      PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE      PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR; 
    MI_TABLA            PCK_SUBTIPOS.TI_TABLA;

  BEGIN

    MI_COMPANIA_NIIF := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                              UN_NOMBRE    => 'COMPAÑIA EQUIVALENTE NIIF',
                                              UN_MODULO    => PCK_DATOS.FC_MODULOCONTABILIDAD,
                                              UN_FECHA_PAR => SYSDATE);
    BEGIN
     SELECT DISTINCT 'X'
       INTO MI_COMPANIA
       FROM COMPANIA 
      WHERE CODIGO = MI_COMPANIA_NIIF;
    EXCEPTION WHEN NO_DATA_FOUND
    THEN 
      MI_RESPUESTA := '-1, ' || MI_COMPANIA_NIIF;
      RETURN MI_RESPUESTA;
    END;

    BEGIN
     SELECT DISTINCT 'X'
       INTO MI_PLAN 
       FROM PLAN_CONTABLE 
      WHERE COMPANIA = MI_COMPANIA_NIIF
        AND ANO 	   = UN_ANIO;
    EXCEPTION WHEN NO_DATA_FOUND
    THEN 
       MI_RESPUESTA := '-2, ' || MI_COMPANIA_NIIF;
       RETURN MI_RESPUESTA;
    END;

  --'*********************
  --' CREA DATOS FALTANTES
  --'*********************'
    BEGIN
      BEGIN
        MI_STRETAPA := 'Creación de terceros';
        PCK_PREPARAR_ANO.PR_COPIAR_TERCERO(UN_COMPANIA         => UN_COMPANIA, 
                                           UN_COMPANIA_DESTINO => MI_COMPANIA_NIIF);
        MI_STRETAPA := 'Creación Centros de costo';
        PCK_PREPARAR_ANO.PR_COPIAR_CENTRO_COSTO(UN_COMPANIA         => UN_COMPANIA,
                                                UN_ANO_DESTINO      => UN_ANIO,
                                                UN_ANO_ORIGEN       => UN_ANIO,
                                                UN_COMPANIA_DESTINO => MI_COMPANIA_NIIF);
        MI_STRETAPA := 'Creación de auxiliares Niif';
        PCK_PREPARAR_ANO.PR_COPIAR_AUXILIAR(UN_COMPANIA         => UN_COMPANIA,
                                            UN_ANO_DESTINO      => UN_ANIO,
                                            UN_ANO_ORIGEN       => UN_ANIO,
                                            UN_COMPANIA_DESTINO => MI_COMPANIA_NIIF);
        MI_STRETAPA := 'Creación Tipos de comprobantes';
        PCK_PREPARAR_ANO.PR_COPIAR_TIPO_COMPROBANTE(UN_COMPANIA         => UN_COMPANIA,
                                                    UN_COMPANIA_DESTINO => MI_COMPANIA_NIIF); 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
        MI_MSGERROR(1).CLAVE := 'MENSAJE';
        MI_MSGERROR(1).VALOR := MI_STRETAPA;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD =>SQLCODE,
          UN_ERROR_COD=>PCK_ERRORES.ERR_CONTABILIDAD_CREARDATFALT,
          UN_REEMPLAZOS  => MI_MSGERROR
        ); 
    END; 
  --'******************************
  --' TERMINA: CREA DATOS FALTANTES
  --'******************************'

	  BEGIN
      BEGIN 
        MI_TABLA := 'PLAN_CONTABLE';
        MI_STRETAPA := 'al Código Niif';
        MI_RESULTADO := PCK_DATOS.FC_ACME(UN_TABLA     => 'PLAN_CONTABLE',
                                          UN_ACCION    => 'M',
                                          UN_CAMPOS    => ' CODIGO_NIIF = NULL',
                                          UN_CONDICION => ' COMPANIA = ''' || UN_COMPANIA || 
                                                         ''' AND ANO = ' || UN_ANIO || 
                                                          '  AND CODIGO_NIIF = '' ''');
        MI_TABLA := 'TIPO_COMPROBANTE';
        MI_STRETAPA := 'a excluir Niif y aplica Niif';
        MI_RESULTADO := PCK_DATOS.FC_ACME(UN_TABLA     => 'TIPO_COMPROBANTE',
                                          UN_ACCION    => 'M',
                                          UN_CAMPOS    => ' EXCLUIR_NIIF = 0, APLICA_NIIF = 0 ',
                                          UN_CONDICION => ' COMPANIA = ''' || UN_COMPANIA || ''' AND EXCLUIR_NIIF IS NULL'); 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
        MI_MSGERROR(1).CLAVE := 'MENSAJE';
        MI_MSGERROR(1).VALOR := MI_STRETAPA;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_ACTINFONIIF,
          UN_REEMPLAZOS => MI_MSGERROR,
          UN_TABLAERROR => MI_TABLA
        ); 
    END;

    --'*********************************************
    --' CONFIGURA CODIGO NIIF A TODOS LOS AUXILIARES
    --'*********************************************
    BEGIN
      MI_MERGEUSING := 'SELECT SALDO_AUX_CONTABLE.COMPANIA,
                               SALDO_AUX_CONTABLE.ANO,
                               SALDO_AUX_CONTABLE.CODIGO,
                               SALDO_AUX_CONTABLE.CENTRO_COSTO,
                               SALDO_AUX_CONTABLE.TERCERO,
                               SALDO_AUX_CONTABLE.SUCURSAL,
                               SALDO_AUX_CONTABLE.AUXILIAR,
                               SALDO_AUX_CONTABLE.FUENTE_RECURSO,
                               SALDO_AUX_CONTABLE.REFERENCIA,
                               PLAN_CONTABLE.CODIGO_NIIF
                          FROM SALDO_AUX_CONTABLE
                              INNER  JOIN PLAN_CONTABLE
                                 ON SALDO_AUX_CONTABLE.COMPANIA = PLAN_CONTABLE.COMPANIA
                                AND SALDO_AUX_CONTABLE.ANO      = PLAN_CONTABLE.ANO 
                                AND SALDO_AUX_CONTABLE.CODIGO   = PLAN_CONTABLE.CODIGO 
                         WHERE SALDO_AUX_CONTABLE.COMPANIA = ''' || UN_COMPANIA || '''
                           AND SALDO_AUX_CONTABLE.ANO      = ' || UN_ANIO || '
                           AND SALDO_AUX_CONTABLE.CODIGO_NIIF IS NULL';

        MI_MERGEENLACE := 'TABLA.COMPANIA       = VISTA.COMPANIA
                       AND TABLA.ANO            = VISTA.ANO
                       AND TABLA.CODIGO         = VISTA.CODIGO
                       AND TABLA.CENTRO_COSTO   = VISTA.CENTRO_COSTO
                       AND TABLA.TERCERO        = VISTA.TERCERO
                       AND TABLA.SUCURSAL       = VISTA.SUCURSAL
                       AND TABLA.AUXILIAR       = VISTA.AUXILIAR
                       AND TABLA.FUENTE_RECURSO = VISTA.FUENTE_RECURSO
                       AND TABLA.REFERENCIA     = VISTA.REFERENCIA';

        MI_MERGEEXISTE := 'UPDATE SET TABLA.CODIGO_NIIF = VISTA.CODIGO_NIIF '; 
        BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA       => 'SALDO_AUX_CONTABLE',
                                                 UN_ACCION      => 'MM',
                                                 UN_MERGEUSING  => MI_MERGEUSING,
                                                 UN_MERGEENLACE => MI_MERGEENLACE,
                                                 UN_MERGEEXISTE => MI_MERGEEXISTE);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
          RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
        MI_MSGERROR(1).CLAVE := 'COMPANIA';
        MI_MSGERROR(1).VALOR := UN_COMPANIA;
        MI_MSGERROR(2).CLAVE := 'ANIO';
        MI_MSGERROR(2).VALOR := UN_ANIO;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_ACTCODNIIFAUX,
          UN_REEMPLAZOS => MI_MSGERROR,
          UN_TABLAERROR => 'SALDO_AUX_CONTABLE'
        ); 
    END;  
	  --'******************************************************
	  --' TERMINA: CONFIGURA CODIGO NIIF A TODOS LOS AUXILIARES
	  --'******************************************************


  --'***********************************
  --' GENERA TXT CON LAS INCONSISTENCIAS
  --'***********************************
    --'*********************************************************************
    --      ' REVISA CUENTAS QUE NO TIENEN CONFIGURACION NIIF Y TIENEN MOVIMIENTOS   
    --'*********************************************************************' 
    <<CUENTAS_SIN_NIIF>>
    FOR RS_CUENTASSINNIIF IN (WITH FALTANTES AS 
                              ( SELECT COMPANIA,
                                      ANO,
                                      CODIGO 
                                  FROM PLAN_CONTABLE
                                  WHERE COMPANIA = '001'
                                  AND ANO      = 2015
                                  AND CODIGO_NIIF IS NULL
                                  AND MOVIMIENTO  NOT IN (0)
                                  GROUP BY COMPANIA,
                                      ANO,
                                      CODIGO,
                                      CODIGO_NIIF)
                                  SELECT DETALLE_COMPROBANTE_CNT.COMPANIA,
                                         DETALLE_COMPROBANTE_CNT.ANO,
                                         DETALLE_COMPROBANTE_CNT.CUENTA
                                    FROM DETALLE_COMPROBANTE_CNT
                                      INNER JOIN COMPROBANTE_CNT
                                             ON DETALLE_COMPROBANTE_CNT.COMPANIA    = COMPROBANTE_CNT.COMPANIA 
                                            AND DETALLE_COMPROBANTE_CNT.ANO         = COMPROBANTE_CNT.ANO 
                                            AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE   = COMPROBANTE_CNT.TIPO
                                            AND DETALLE_COMPROBANTE_CNT.COMPROBANTE = COMPROBANTE_CNT.NUMERO
                                      INNER JOIN TIPO_COMPROBANTE
                                             ON COMPROBANTE_CNT.COMPANIA        = TIPO_COMPROBANTE.COMPANIA 
                                            AND COMPROBANTE_CNT.TIPO            = TIPO_COMPROBANTE.CODIGO 
                                      INNER JOIN FALTANTES  
                                        ON  DETALLE_COMPROBANTE_CNT.COMPANIA = FALTANTES.COMPANIA 
                                        AND DETALLE_COMPROBANTE_CNT.ANO      = FALTANTES.ANO
                                        AND DETALLE_COMPROBANTE_CNT.CUENTA   = FALTANTES.CODIGO
                                   WHERE DETALLE_COMPROBANTE_CNT.COMPANIA  = UN_COMPANIA
                                     AND DETALLE_COMPROBANTE_CNT.ANO       = UN_ANIO
                                     AND TIPO_COMPROBANTE.EXCLUIR_NIIF IN (0)
                                     AND COMPROBANTE_CNT.NONIIF        IN (0)
                                   GROUP BY DETALLE_COMPROBANTE_CNT.COMPANIA,
                                            DETALLE_COMPROBANTE_CNT.ANO,
                                            DETALLE_COMPROBANTE_CNT.CUENTA
                                        ORDER BY DETALLE_COMPROBANTE_CNT.COMPANIA,
                                            DETALLE_COMPROBANTE_CNT.ANO,
                                            DETALLE_COMPROBANTE_CNT.CUENTA)
      LOOP
        MI_INTE := MI_INTE + 1;
        MI_RTA  := MI_RTA || 'La cuenta: ' || RPAD(RS_CUENTASSINNIIF.CUENTA, 18, ' ') || 'no tiene equivalente NIIF' || CHR(13); 
      END LOOP CUENTAS_SIN_NIIF;   
       -- Si encuentra cuentas sin configurar, retorna las cuentas y termina el proceso.
       IF MI_RTA IS NOT NULL 
       THEN
			 --Envia a un txt las cuentas que no tengan configurado codigo NIIF
        MI_RTA := 'INCONSISTENCIAS DETECTADAS EN EL PLAN DE CUENTAS DE LAS CUENTAS QUE NO ESTAN CONFIGURADAS EN NIIIF '
                  || CHR(13) || CHR(13) || MI_RTA;
       END IF; 
	  --'*************************************************************************************
    --'TERMINA: REVISA CUENTAS QUE TIENEN CONFIGURACION NIIF Y ESTAS NO EXISTEN EN PLAN NIIF
    --'*************************************************************************************

	  --'******************************************************************************
    --' REVISA CUENTAS QUE TIENEN CONFIGURACION NIIF Y ESTAS NO EXISTEN EN PLAN NIIF
    --'******************************************************************************
	  <<CUENTAS_NO_NIIF>>
	  FOR RS_CUENTAS IN (SELECT DISTINCT NIIF1.CODIGO,
                              NIIF1.CODIGO_NIIF,
                              'No existe en NIIF' OBSERVACION
                         FROM (SELECT DISTINCT CODIGO,
                                      CODIGO_NIIF,
                                      SALDO13
                                 FROM PLAN_CONTABLE
                                WHERE PLAN_CONTABLE.COMPANIA = UN_COMPANIA
                                  AND ANO                    = UN_ANIO
                                  AND CODIGO_NIIF IS NOT NULL
                                  AND MAN_CEN_CTO + MAN_AUX_TER + MAN_AUX_GEN + MOVIMIENTO NOT IN (0)
                              ) NIIF1
                            LEFT JOIN (SELECT DISTINCT CODIGO
                                         FROM PLAN_CONTABLE
                                        WHERE PLAN_CONTABLE.COMPANIA = MI_COMPANIA_NIIF
                                          AND ANO                    = UN_ANIO
                                          AND CODIGO                 = CODIGO
                                          AND MAN_CEN_CTO+MAN_AUX_TER+MAN_AUX_GEN+MOVIMIENTO NOT IN (0)
                                      ) NIIF2
                            ON (NIIF1.CODIGO_NIIF = NIIF2.CODIGO)
                        WHERE NIIF2.CODIGO   IS NULL )
	  LOOP
      MI_INTE := MI_INTE+1;
      MI_RTA1 := MI_RTA1 || 'La cuenta: ' || RPAD(RS_CUENTAS.CODIGO, 15, ' ') || ' tiene configurado como código NIIF ' 
                 || RPAD(RS_CUENTAS.CODIGO_NIIF, 15, ' ') || '  y esta cuenta no existe en plan NIIF' || CHR(13);
	  END LOOP;
    IF MI_RTA1 IS NOT NULL 
	  THEN 
		  IF MI_RTA IS NULL
		  THEN
					MI_RTA1 := 'INCONSISTENCIAS DETECTADAS EN EL PLAN DE CUENTAS DE LAS CUENTAS QUE NO ESTAN CONFIGURADAS EN NIIIF '
							  || CHR(13) || CHR(13)
							  || 'CUENTAS QUE NO EXISTEN EN EL PLAN NIIF'
							  || CHR(13) || CHR(13)
							  || MI_RTA1;
		  ELSE
        MI_RTA1 := CHR(13) || CHR(13)
							  ||  'CUENTAS QUE NO EXISTEN EN EL PLAN NIIF'
							  || CHR(13) || CHR(13)
							  || MI_RTA1;
		  END IF;
	 END IF;

	 IF MI_INTE > 0
	 THEN
		MI_RTA := MI_INTE || ',' || MI_RTA || MI_RTA1;
		RETURN MI_RTA;
	 END IF;
	--'*************************************************************************************
  --'TERMINA: REVISA CUENTAS QUE TIENEN CONFIGURACION NIIF Y ESTAS NO EXISTEN EN PLAN NIIF
  --'*************************************************************************************

	--'********************************************
	--' TERMINA: GENERA TXT CON LAS INCONSISTENCIAS'
	--'********************************************

	--'********************************************************************************************'
	--'ELIMINA INFORMACION DE LA DETALLE_COMPROBANTE_CNT, COMPROBANTE_CNTRETENCION Y COMROBANTE_CNT'
	--'********************************************************************************************'
    BEGIN       
      BEGIN
        MI_TABLA := 'DETALLE_COMPROBANTE_CNT';
        MI_STRETAPA := 'los detalles de los comprobantes contables ';
        MI_CONDICION := ' (COMPANIA, ANO, TIPO_CPTE, FECHA) 
                          IN( SELECT DETALLE_COMPROBANTE_CNT.COMPANIA,
                                     DETALLE_COMPROBANTE_CNT.ANO, 
                                     DETALLE_COMPROBANTE_CNT.TIPO_CPTE,
                                     DETALLE_COMPROBANTE_CNT.FECHA  
                                FROM DETALLE_COMPROBANTE_CNT          
                                    LEFT JOIN COMPROBANTE_CNT             
                                        ON DETALLE_COMPROBANTE_CNT.COMPANIA     = COMPROBANTE_CNT.COMPANIA             
                                        AND DETALLE_COMPROBANTE_CNT.ANO         = COMPROBANTE_CNT.ANO             
                                        AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE   = COMPROBANTE_CNT.TIPO            
                                        AND DETALLE_COMPROBANTE_CNT.COMPROBANTE = COMPROBANTE_CNT.NUMERO          
                                    LEFT JOIN TIPO_COMPROBANTE             
                                         ON COMPROBANTE_CNT.COMPANIA = TIPO_COMPROBANTE.COMPANIA             
                                        AND COMPROBANTE_CNT.TIPO     = TIPO_COMPROBANTE.CODIGO       
                                WHERE DETALLE_COMPROBANTE_CNT.COMPANIA = ''' || MI_COMPANIA_NIIF || '''        
                                  AND DETALLE_COMPROBANTE_CNT.ANO      = '|| UN_ANIO ||'        
                                  AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE BETWEEN '''|| UN_TIPO_INICIAL ||''' AND '''||UN_TIPO_FINAL||'''        
                                  AND EXTRACT (MONTH FROM DETALLE_COMPROBANTE_CNT.FECHA) BETWEEN '||UN_MES_INICIAL||' AND '||UN_MES_FINAL||'       
                                  AND TIPO_COMPROBANTE.APLICA_NIIF IN (0) 
                                   OR TIPO_COMPROBANTE.APLICA_NIIF IS NULL )';

        MI_RESULTADO := PCK_DATOS.FC_ACME(UN_TABLA      => 'DETALLE_COMPROBANTE_CNT', 
                                          UN_ACCION     => 'E', 
                                          UN_CONDICION  => MI_CONDICION);

        MI_TABLA := 'COMPROBANTE_CNTRETENCION';   
        MI_STRETAPA := 'los comprobantes contables de retención ';
        MI_CONDICION := '  (COMPANIA, ANO, TIPO) 
                              IN( SELECT COMPROBANTE_CNTRETENCION.COMPANIA,
                                         COMPROBANTE_CNTRETENCION.ANO, 
                                         COMPROBANTE_CNTRETENCION.TIPO
                                    FROM COMPROBANTE_CNT 
                                      INNER JOIN COMPROBANTE_CNTRETENCION 
                                          ON COMPROBANTE_CNT.COMPANIA = COMPROBANTE_CNTRETENCION.COMPANIA 
                                         AND COMPROBANTE_CNT.ANO      = COMPROBANTE_CNTRETENCION.ANO
                                         AND COMPROBANTE_CNT.TIPO     = COMPROBANTE_CNTRETENCION.TIPO 
                                         AND COMPROBANTE_CNT.NUMERO   = COMPROBANTE_CNTRETENCION.NUMERO 
                                      INNER JOIN TIPO_COMPROBANTE 
                                         ON COMPROBANTE_CNTRETENCION.COMPANIA = TIPO_COMPROBANTE.COMPANIA 
                                        AND COMPROBANTE_CNTRETENCION.TIPO     = TIPO_COMPROBANTE.CODIGO
                                   WHERE COMPROBANTE_CNT.COMPANIA = '''|| MI_COMPANIA_NIIF ||''' 
                                     AND COMPROBANTE_CNT.ANO      = ' || UN_ANIO          ||' 
                                     AND COMPROBANTE_CNT.TIPO BETWEEN '''|| UN_TIPO_INICIAL  || ''' AND ''' || UN_TIPO_FINAL || ''' 
                                     AND EXTRACT (MONTH FROM COMPROBANTE_CNT.FECHA) BETWEEN ' || UN_MES_INICIAL|| ' AND ' || UN_MES_FINAL ||
                                  '  AND TIPO_COMPROBANTE.APLICA_NIIF IN (0) OR TIPO_COMPROBANTE.APLICA_NIIF IS NULL )';

        MI_RESULTADO := PCK_DATOS.FC_ACME(UN_TABLA     => 'COMPROBANTE_CNTRETENCION', 
                                         UN_ACCION     => 'E', 
                                         UN_CONDICION  => MI_CONDICION);


        MI_TABLA := 'COMPROBANTE_CNT';
        MI_STRETAPA := 'los comprobantes contables ';
        MI_CONDICION := ' (COMPANIA, ANO, TIPO, FECHA) 
                             IN (SELECT COMPROBANTE_CNT.COMPANIA, 
                                        COMPROBANTE_CNT.ANO, 
                                        COMPROBANTE_CNT.TIPO, 
                                        COMPROBANTE_CNT.FECHA 
                                   FROM COMPROBANTE_CNT 
                                    INNER JOIN TIPO_COMPROBANTE 
                                          ON COMPROBANTE_CNT.COMPANIA = TIPO_COMPROBANTE.COMPANIA  
                                         AND COMPROBANTE_CNT.TIPO     = TIPO_COMPROBANTE.CODIGO 
                                  WHERE COMPROBANTE_CNT.COMPANIA  = ''' || MI_COMPANIA_NIIF || '''  
                                    AND COMPROBANTE_CNT.ANO       = ' || UN_ANIO          || ' 
                                    AND COMPROBANTE_CNT.TIPO BETWEEN '''|| UN_TIPO_INICIAL  ||''' AND '''||UN_TIPO_FINAL||''' 
                                    AND EXTRACT (MONTH FROM COMPROBANTE_CNT.FECHA) BETWEEN '||UN_MES_INICIAL||' AND '||UN_MES_FINAL || ' 
                                    AND TIPO_COMPROBANTE.APLICA_NIIF IN (0) OR TIPO_COMPROBANTE.APLICA_NIIF IS NULL)';

        MI_RESULTADO := PCK_DATOS.FC_ACME(UN_TABLA     => 'COMPROBANTE_CNT', 
                                         UN_ACCION     => 'E', 
                                         UN_CONDICION  => MI_CONDICION);	                          
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END; 
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
        MI_MSGERROR(1).CLAVE := 'MENSAJE';
        MI_MSGERROR(1).VALOR := MI_STRETAPA;
        MI_MSGERROR(2).CLAVE := 'COMNIIF';
        MI_MSGERROR(2).VALOR := MI_COMPANIA_NIIF;
        MI_MSGERROR(3).CLAVE := 'ANIO';
        MI_MSGERROR(3).VALOR := UN_ANIO;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_ELIMINFOCOMP,
          UN_REEMPLAZOS => MI_MSGERROR,
          UN_TABLAERROR => MI_TABLA
        ); 
    END;
    --'*****************************************************************************************************
    --'TERMINA: ELIMINA INFORMACION DE LA DETALLE_COMPROBANTE_CNT, COMPROBANTE_CNTRETENCION Y COMROBANTE_CNT
    --'*****************************************************************************************************

	  --'*******************************************************************************************************************************
	  --' QUITA ' DE LA TABLA COMPROBANTE_CNT  CAMPO TEXTO Y DESCRIPCIÓN, TAMBIEN DE LA TABLA  DETALLE_COMPROBANTE_CNT CAMPO DESCRIPCIÓN
	  --'*******************************************************************************************************************************
    BEGIN
      BEGIN
        MI_TABLA := 'COMPROBANTE_CNT';
        MI_STRETAPA := ' al texto de los comprobantes de la compañía ' || UN_COMPANIA || ' para el año ' || UN_ANIO;
        MI_RESULTADO := PCK_DATOS.FC_ACME(UN_TABLA     => 'COMPROBANTE_CNT',
                                          UN_ACCION    => 'M',
                                          UN_CAMPOS    => ' TEXTO = REPLACE(COMPROBANTE_CNT.TEXTO, ''' || CHR(39) || CHR(39) || ''', '' '') ', 
                                          UN_CONDICION => ' COMPROBANTE_CNT.COMPANIA         = ''' || UN_COMPANIA || 
                                                         ''' AND COMPROBANTE_CNT.ANO         = ''' || UN_ANIO || 
                                                         ''' AND COMPROBANTE_CNT.DESCRIPCION = ''' || CHR(39) || CHR(39) || '''
                                                              OR COMPROBANTE_CNT.TEXTO       = ''' || CHR(39) || CHR(39) || '''');

        MI_TABLA := 'DETALLE_COMPROBANTE_CNT';
        MI_STRETAPA := ' a la descripción de los detalles de los comprobantes de la compañía ' || UN_COMPANIA || ' para el año ' || UN_ANIO;                             
        MI_RESULTADO := PCK_DATOS.FC_ACME(UN_TABLA     => 'DETALLE_COMPROBANTE_CNT',
                                          UN_ACCION    => 'M',
                                          UN_CAMPOS    => ' DESCRIPCION = REPLACE(DETALLE_COMPROBANTE_CNT.DESCRIPCION, ''' || CHR(39) || CHR(39)|| ''', '' '') ', 
                                          UN_CONDICION => ' DETALLE_COMPROBANTE_CNT.COMPANIA = ''' || UN_COMPANIA || 
                                                         ''' AND DETALLE_COMPROBANTE_CNT.ANO = ''' || UN_ANIO || 
                                                         ''' AND DETALLE_COMPROBANTE_CNT.DESCRIPCION = ''' || CHR(39) || CHR(39) || '''');
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
        MI_MSGERROR(1).CLAVE := 'MENSAJE';
        MI_MSGERROR(1).VALOR := MI_STRETAPA;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_ACTINFONIIF,
          UN_REEMPLAZOS => MI_MSGERROR,
          UN_TABLAERROR => MI_TABLA
        ); 
    END;  
    --****************************************************************************************************************************************
    --' TERMINA: QUITA ' DE LA TABLA COMPROBANTE_CNT  CAMPO TEXTO Y DESCRIPCIÓN, TAMBIEN DE LA TABLA  DETALLE_COMPROBANTE_CNT CAMPO DESCRIPCIÓN
    --'****************************************************************************************************************************************		

    --****************************************************************************************
    --'CREA INFORMACION EN LA COMPROBANTE_CNT,DETALLE_COMPROBANTE_CNT, Comprobante_cntRetencion
    --'****************************************************************************************	
    BEGIN  
      BEGIN
        MI_STRETAPA := 'al número Niif de los comprobantes de la compañía ' || UN_COMPANIA || ' para el año ' || UN_ANIO || 
                       ' para los tipos de comprobante y meses seleccionados. ';
        MI_RESULTADO := PCK_DATOS.FC_ACME
                                (UN_TABLA       => 'COMPROBANTE_CNT', 
                                 UN_ACCION      => 'MM', 
                                 UN_MERGEUSING  => 'SELECT COMPROBANTE_CNT.COMPANIA,
                                                           COMPROBANTE_CNT.ANO, 
                                                           COMPROBANTE_CNT.TIPO,
                                                           COMPROBANTE_CNT.NUMERO,
                                                           ''0'' NONIIF
                                                      FROM COMPROBANTE_CNT                   
                                                         INNER JOIN TIPO_COMPROBANTE             
                                                             ON COMPROBANTE_CNT.COMPANIA = TIPO_COMPROBANTE.COMPANIA             
                                                            AND COMPROBANTE_CNT.TIPO     = TIPO_COMPROBANTE.CODIGO       
                                                     WHERE COMPROBANTE_CNT.COMPANIA = ''' || UN_COMPANIA || 
                                                   ''' AND COMPROBANTE_CNT.ANO = ''' || UN_ANIO || 
                                                   ''' AND COMPROBANTE_CNT.TIPO BETWEEN '''|| UN_TIPO_INICIAL  ||''' AND '''||UN_TIPO_FINAL||
                                                   ''' AND EXTRACT (MONTH FROM COMPROBANTE_CNT.FECHA) BETWEEN '||UN_MES_INICIAL||' AND '||UN_MES_FINAL || 
                                                   '   AND COMPROBANTE_CNT.NONIIF        IS NULL
                                                       AND TIPO_COMPROBANTE.EXCLUIR_NIIF IN (0)
                                                       AND TIPO_COMPROBANTE.APLICA_NIIF  IN (0) ', 
                                 UN_MERGEENLACE => ' TABLA.COMPANIA  = VISTA.COMPANIA
                                                 AND TABLA.ANO       = VISTA.ANO
                                                 AND TABLA.TIPO      = VISTA.TIPO
                                                 AND TABLA.NUMERO    = VISTA.NUMERO ',
                                 UN_MERGEEXISTE => ' UPDATE SET TABLA.NONIIF  = VISTA.NONIIF') ;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;  
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
        MI_MSGERROR(1).CLAVE := 'MENSAJE';
        MI_MSGERROR(1).VALOR := MI_STRETAPA;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_ACTINFONIIF,
          UN_REEMPLAZOS => MI_MSGERROR,
          UN_TABLAERROR => MI_TABLA
        ); 
    END;

    BEGIN
        <<COMPROBANTES>>
        FOR RS_COMPROBANTE IN (SELECT  COMPROBANTE_CNT.COMPANIA,
                                       COMPROBANTE_CNT.ANO,
                                       COMPROBANTE_CNT.TIPO,
                                       COMPROBANTE_CNT.NUMERO,
                                       TO_DATE(TO_CHAR(COMPROBANTE_CNT.FECHA, 'DD/MM/YYYY HH24:mi:ss'), 'DD/MM/YYYY HH24:mi:ss') FECHA,
                                       SUBSTR(COMPROBANTE_CNT.DESCRIPCION,0,66) DESCRIPCION,
                                       SUBSTR(COMPROBANTE_CNT.TEXTO,1,67) TEXTO,
                                       COMPROBANTE_CNT.TERCERO,
                                       COMPROBANTE_CNT.SUCURSAL,
                                       COMPROBANTE_CNT.VLR_DOCUMENTO,
                                       COMPROBANTE_CNT.CREATED_BY, 
                                       COMPROBANTE_CNT.VLR_BASE,
                                       COMPROBANTE_CNT.VLR_BASEIVA,
                                       TO_DATE(TO_CHAR(COMPROBANTE_CNT.FECHA_VCN_DOC, 'DD/MM/YYYY HH24:mi:ss'), 'DD/MM/YYYY HH24:mi:ss') FECHA_VCN_DOC,
                                       COMPROBANTE_CNT.DEBITO,
                                       COMPROBANTE_CNT.CREDITO,
                                       COMPROBANTE_CNT.MODIFIED_BY, 
                                       COMPROBANTE_CNT.VLRAGIRAR,
                                       COMPROBANTE_CNT.DEBITOSAFECTADOS,
                                       COMPROBANTE_CNT.CREDITOSAFECTADOS,
                                       TO_DATE(TO_CHAR(COMPROBANTE_CNT.DATE_CREATED, 'DD/MM/YYYY HH24:mi:ss'), 'DD/MM/YYYY HH24:mi:ss') DATE_CREATED, 
                                       TO_DATE(TO_CHAR(COMPROBANTE_CNT.DATE_MODIFIED, 'DD/MM/YYYY HH24:mi:ss'), 'DD/MM/YYYY HH24:mi:ss') DATE_MODIFIED, 
                                       COMPROBANTE_CNT.PORCIVA,
                                       COMPROBANTE_CNT.CENTRO_COSTO,
                                       COMPROBANTE_CNT.AUXILIAR,
                                       TO_DATE(TO_CHAR(COMPROBANTE_CNT.HORA, 'DD/MM/YYYY HH24:mi:ss'), 'DD/MM/YYYY HH24:mi:ss') HORA
                                 FROM COMPROBANTE_CNT 
                                     INNER JOIN TIPO_COMPROBANTE
                                      ON COMPROBANTE_CNT.COMPANIA = TIPO_COMPROBANTE.COMPANIA
                                     AND COMPROBANTE_CNT.TIPO     = TIPO_COMPROBANTE.CODIGO
                                WHERE COMPROBANTE_CNT.COMPANIA = UN_COMPANIA
                                  AND COMPROBANTE_CNT.ANO      = UN_ANIO
                                  AND COMPROBANTE_CNT.TIPO BETWEEN UN_TIPO_INICIAL AND UN_TIPO_FINAL
                                  AND EXTRACT (MONTH FROM COMPROBANTE_CNT.FECHA) BETWEEN UN_MES_INICIAL AND UN_MES_FINAL
                                  AND (TIPO_COMPROBANTE.EXCLUIR_NIIF IN (0)
                                   OR TIPO_COMPROBANTE.EXCLUIR_NIIF  IS NULL)
                                  AND (TIPO_COMPROBANTE.APLICA_NIIF  IN (0)
                                   OR TIPO_COMPROBANTE.APLICA_NIIF   IS NULL)
                                  AND (COMPROBANTE_CNT.NONIIF        IN (0)
                                   OR COMPROBANTE_CNT.NONIIF         IS NULL)
                                ORDER BY COMPROBANTE_CNT.TIPO,
                                         COMPROBANTE_CNT.NUMERO)
        LOOP
          BEGIN           
            MI_STRETAPA := 'los comprobantes contables';
            MI_TABLA := 'COMPROBANTE_CNT';
            MI_CAMPOS  := 'COMPANIA, 
                           ANO, 
                           TIPO, 
                           NUMERO, 
                           FECHA, 
                           DESCRIPCION, 
                           TEXTO, 
                           TERCERO, 
                           SUCURSAL,
                           VLR_DOCUMENTO, 
                           CREATED_BY, 
                           VLR_BASE, 
                           VLR_BASEIVA,
                           FECHA_VCN_DOC, 
                           DEBITO, 
                           CREDITO, 
                           MODIFIED_BY, 
                           VLRAGIRAR, 
                           DEBITOSAFECTADOS,  
                           CREDITOSAFECTADOS, 
                           DATE_CREATED, 
                           DATE_MODIFIED, 
                           PORCIVA,
                           CENTRO_COSTO,
                           AUXILIAR, 
                           HORA'; 

            MI_VALORES:=  '''' || MI_COMPANIA_NIIF || 
                          ''', ' || RS_COMPROBANTE.ANO ||
                          ', ''' || RS_COMPROBANTE.TIPO ||
                          ''', ' || RS_COMPROBANTE.NUMERO || 
                          ',  TO_DATE(''' || RS_COMPROBANTE.FECHA || ''', ''DD/MM/YYYY HH24:mi:ss'')' ||
                          ', q''' || '[''' || NVL(RS_COMPROBANTE.DESCRIPCION, '') || ''']' ||
                          ''', q''' || '[''' || NVL(RS_COMPROBANTE.TEXTO, '') || ''']' || 
                          ''', ''' || RS_COMPROBANTE.TERCERO ||
                          ''', ''' ||  RS_COMPROBANTE.SUCURSAL ||
                          ''', ''' || RS_COMPROBANTE.VLR_DOCUMENTO ||
                          ''', ''' || UN_USUARIO  ||
                          ''', ''' ||  TO_NUMBER(NVL(RS_COMPROBANTE.VLR_BASE,0)) ||
                          ''', ''' || TO_NUMBER(NVL(RS_COMPROBANTE.VLR_BASEIVA,0)) ||
                          ''', TO_DATE(''' || NVL(RS_COMPROBANTE.FECHA_VCN_DOC, RS_COMPROBANTE.FECHA + 30) || ''', ''DD/MM/YYYY HH24:mi:ss'')' ||
                          ', ''' || RS_COMPROBANTE.DEBITO ||
                          ''', ''' || RS_COMPROBANTE.CREDITO ||
                          ''', ''' || RS_COMPROBANTE.MODIFIED_BY || 
                          ''', ''' || NVL(RS_COMPROBANTE.VLRAGIRAR, 0) ||
                          ''', ''' || RS_COMPROBANTE.DEBITOSAFECTADOS || 
                          ''', ''' || RS_COMPROBANTE.CREDITOSAFECTADOS ||
                          ''', TO_DATE(''' || NVL(RS_COMPROBANTE.DATE_CREATED, RS_COMPROBANTE.FECHA) || ''', ''DD/MM/YYYY HH24:mi:ss'')' ||
                          ', TO_DATE(''' || NVL(RS_COMPROBANTE.DATE_MODIFIED, RS_COMPROBANTE.FECHA) || ''', ''DD/MM/YYYY HH24:mi:ss'')' ||
                          ', ''' || NVL(RS_COMPROBANTE.PORCIVA, 0) ||
                          ''', ''' || RS_COMPROBANTE.CENTRO_COSTO || 
                          ''', ''' || RS_COMPROBANTE.AUXILIAR ||
                          ''', TO_DATE(''' || SYSDATE || ''', ''DD/MM/YYYY HH24:mi:ss'') ';    

            MI_RESULTADO := PCK_DATOS.FC_ACME(UN_TABLA   => 'COMPROBANTE_CNT', 
                                              UN_ACCION  => 'I', 
                                              UN_CAMPOS  => MI_CAMPOS, 
                                              UN_VALORES => MI_VALORES);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
          END;    
        END LOOP COMPROBANTES; 

        <<CONTABILIZA>>
        FOR RS_DETALLE IN (SELECT DETALLE_COMPROBANTE_CNT.COMPANIA,
                                  DETALLE_COMPROBANTE_CNT.ANO,
                                  DETALLE_COMPROBANTE_CNT.TIPO_CPTE,
                                  DETALLE_COMPROBANTE_CNT.COMPROBANTE,
                                  DETALLE_COMPROBANTE_CNT.CONSECUTIVO,
                                  DETALLE_COMPROBANTE_CNT.CUENTA,
                                  DETALLE_COMPROBANTE_CNT.CUENTA CODIGO_CUENTA,
                                  TO_DATE(TO_CHAR(DETALLE_COMPROBANTE_CNT.FECHA, 'DD/MM/YYYY HH24:mi:ss'), 'DD/MM/YYYY HH24:mi:ss') FECHA, 
                                  DETALLE_COMPROBANTE_CNT.NATURALEZA,
                                  DETALLE_COMPROBANTE_CNT.CUENTAPPTAL,
                                  DETALLE_COMPROBANTE_CNT.DESCRIPCION,
                                  DETALLE_COMPROBANTE_CNT.VALOR_DEBITO,
                                  DETALLE_COMPROBANTE_CNT.VALOR_CREDITO,
                                  DETALLE_COMPROBANTE_CNT.EJECUCION_DEBITO,
                                  DETALLE_COMPROBANTE_CNT.EJECUCION_CREDITO,
                                  DETALLE_COMPROBANTE_CNT.BASE_GRAVABLE,
                                  DETALLE_COMPROBANTE_CNT.TIPO_DOCUMENTO,
                                  DETALLE_COMPROBANTE_CNT.NRO_DOCUMENTO,
                                  DETALLE_COMPROBANTE_CNT.CENTRO_COSTO,
                                  DETALLE_COMPROBANTE_CNT.TERCERO,
                                  DETALLE_COMPROBANTE_CNT.SUCURSAL,
                                  DETALLE_COMPROBANTE_CNT.AUXILIAR,
                                  DETALLE_COMPROBANTE_CNT.TIPO_CPTE_AFECT,
                                  DETALLE_COMPROBANTE_CNT.CMPTE_AFECTADO,
                                  DETALLE_COMPROBANTE_CNT.CHEQUEPARAANULAR,
                                  DETALLE_COMPROBANTE_CNT.CONCEPTO_EX,
                                  DETALLE_COMPROBANTE_CNT.TIPOPPTAL,
                                  DETALLE_COMPROBANTE_CNT.BASE_IVA,
                                  DETALLE_COMPROBANTE_CNT.DESEMBOLSO,
                                  DETALLE_COMPROBANTE_CNT.SALDOCUENTA,
                                  TO_DATE(TO_CHAR(DETALLE_COMPROBANTE_CNT.DATE_CREATED, 'DD/MM/YYYY HH24:mi:ss'), 'DD/MM/YYYY HH24:mi:ss') DATE_CREATED, 
                                  DETALLE_COMPROBANTE_CNT.CREATED_BY,
                                  DETALLE_COMPROBANTE_CNT.MODIFIED_BY,
                                  DETALLE_COMPROBANTE_CNT.PORCENTAJERETENCION,
                                  TO_DATE(TO_CHAR(DETALLE_COMPROBANTE_CNT.HORA, 'DD/MM/YYYY HH24:mi:ss'), 'DD/MM/YYYY HH24:mi:ss') HORA, 
                                  DETALLE_COMPROBANTE_CNT.REVELACIONES,
                                  DETALLE_COMPROBANTE_CNT.ANO_AFECT,
                                  PLAN_CONTABLE.CODIGO_NIIF AS COD_NIIF
                            FROM COMPROBANTE_CNT
                              INNER JOIN DETALLE_COMPROBANTE_CNT
                                LEFT JOIN PLAN_CONTABLE
                                   ON DETALLE_COMPROBANTE_CNT.COMPANIA = PLAN_CONTABLE.COMPANIA
                                  AND DETALLE_COMPROBANTE_CNT.ANO      = PLAN_CONTABLE.ANO
                                  AND DETALLE_COMPROBANTE_CNT.CUENTA   = PLAN_CONTABLE.CODIGO
                                 ON COMPROBANTE_CNT.COMPANIA    = DETALLE_COMPROBANTE_CNT.COMPANIA 
                                AND COMPROBANTE_CNT.ANO         = DETALLE_COMPROBANTE_CNT.ANO 
                                AND COMPROBANTE_CNT.TIPO        = DETALLE_COMPROBANTE_CNT.TIPO_CPTE
                                AND COMPROBANTE_CNT.NUMERO      = DETALLE_COMPROBANTE_CNT.COMPROBANTE
                              INNER JOIN TIPO_COMPROBANTE
                                ON COMPROBANTE_CNT.COMPANIA     = TIPO_COMPROBANTE.COMPANIA
                                AND COMPROBANTE_CNT.TIPO        = TIPO_COMPROBANTE.CODIGO
                          WHERE DETALLE_COMPROBANTE_CNT.COMPANIA = UN_COMPANIA
                            AND DETALLE_COMPROBANTE_CNT.ANO      = UN_ANIO
                            AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE BETWEEN UN_TIPO_INICIAL AND UN_TIPO_FINAL
                            AND EXTRACT (MONTH FROM DETALLE_COMPROBANTE_CNT.FECHA) BETWEEN UN_MES_INICIAL AND UN_MES_FINAL
                            AND TIPO_COMPROBANTE.EXCLUIR_NIIF IN (0)
                             OR TIPO_COMPROBANTE.EXCLUIR_NIIF IS NULL
                            AND COMPROBANTE_CNT.NONIIF        IN (0)
                             OR COMPROBANTE_CNT.NONIIF        IS NULL
                          ORDER BY DETALLE_COMPROBANTE_CNT.TIPO_CPTE,
                                   DETALLE_COMPROBANTE_CNT.COMPROBANTE,
                                   DETALLE_COMPROBANTE_CNT.CONSECUTIVO )
        LOOP 
          BEGIN
            MI_STRETAPA := 'los detalles de los comprobantes contables';
            MI_TABLA := 'DETALLE_COMPROBANTE_CNT';
            MI_CAMPOS    := 'COMPANIA, 
                             ANO, 
                             TIPO_CPTE, 
                             COMPROBANTE,
                             CONSECUTIVO, 
                             CUENTA,
                             FECHA,
                             NATURALEZA, 
                             CUENTAPPTAL, 
                             DESCRIPCION, 
                             VALOR_DEBITO, 
                             VALOR_CREDITO, 
                             EJECUCION_DEBITO,
                             EJECUCION_CREDITO, 
                             BASE_GRAVABLE, 
                             TIPO_DOCUMENTO, 
                             NRO_DOCUMENTO, 
                             CENTRO_COSTO, 
                             TERCERO,
                             SUCURSAL, 
                             AUXILIAR, 
                             TIPO_CPTE_AFECT, 
                             CMPTE_AFECTADO, 
                             CHEQUEPARAANULAR, 
                             CONCEPTO_EX, 
                             TIPOPPTAL, 
                             BASE_IVA, 
                             DESEMBOLSO, 
                             SALDOCUENTA, 
                             DATE_CREATED, 
                             CREATED_BY, 
                             MODIFIED_BY, 
                             PORCENTAJERETENCION, 
                             HORA,
                             REVELACIONES';
            MI_VALORES   := '''' || MI_COMPANIA_NIIF ||
                            ''', ' || UN_ANIO || 
                            ', ''' || RS_DETALLE.TIPO_CPTE ||
                            ''', ' || RS_DETALLE.COMPROBANTE || 
                            ', ' || RS_DETALLE.CONSECUTIVO || 
                            ', ''' || RS_DETALLE.CUENTA || 
                            ''', TO_DATE(''' || RS_DETALLE.FECHA || ''', ''DD/MM/YYYY HH24:mi:ss'')' ||
                            ', ''' || RS_DETALLE.NATURALEZA || 
                            ''', ''' || RS_DETALLE.CUENTAPPTAL || 
                            ''', q''' || '[''' || RS_DETALLE.DESCRIPCION || ''']' ||
                            ''', ''' || NVL(RS_DETALLE.VALOR_DEBITO, 0) || 
                            ''', ''' || NVL(RS_DETALLE.VALOR_CREDITO, 0) || 
                            ''', ''' || NVL(RS_DETALLE.EJECUCION_DEBITO, 0) || 
                            ''', ''' || NVL(RS_DETALLE.EJECUCION_CREDITO, 0) || 
                            ''', ''' || NVL(RS_DETALLE.BASE_GRAVABLE, 0) || 
                            ''', ''' || RS_DETALLE.TIPO_DOCUMENTO || 
                            ''', ''' || RS_DETALLE.NRO_DOCUMENTO || 
                            ''', ''' || RS_DETALLE.CENTRO_COSTO || 
                            ''', ''' || NVL(RS_DETALLE.TERCERO, PCK_DATOS.FC_CONS_TERCERO) || 
                            ''', ''' || NVL(RS_DETALLE.SUCURSAL, PCK_DATOS.FC_CONS_SUCURSAL)  || 
                            ''', ''' || RS_DETALLE.AUXILIAR || 
                            ''', ''' || RS_DETALLE.TIPO_CPTE_AFECT || 
                            ''', ''' || NVL(RS_DETALLE.CMPTE_AFECTADO, 0) || 
                            ''', ''' || RS_DETALLE.CHEQUEPARAANULAR || 
                            ''', ''' || RS_DETALLE.CONCEPTO_EX || 
                            ''', ''' || RS_DETALLE.TIPOPPTAL ||
                            ''', ''' || NVL(RS_DETALLE.BASE_IVA, 0) || 
                            ''', ''' || NVL(RS_DETALLE.DESEMBOLSO, 0) || 
                            ''', ''' || NVL(RS_DETALLE.SALDOCUENTA, 0) || 
                            ''', TO_DATE(''' || NVL(RS_DETALLE.DATE_CREATED, RS_DETALLE.FECHA) || ''', ''DD/MM/YYYY HH24:mi:ss'')' ||
                            ', ''' || RS_DETALLE.CREATED_BY || 
                            ''', ''' || RS_DETALLE.MODIFIED_BY || 
                            ''', ''' || NVL(RS_DETALLE.PORCENTAJERETENCION, 0) ||
                            ''', TO_DATE(''' || SYSDATE || ''', ''DD/MM/YYYY HH24:mi:ss'')' ||
                            ', ''' || NVL(RS_DETALLE.REVELACIONES, '.') || '''';
            MI_RESULTADO := PCK_DATOS.FC_ACME(UN_TABLA   => 'DETALLE_COMPROBANTE_CNT', 
                                              UN_ACCION  => 'I', 
                                              UN_CAMPOS  => MI_CAMPOS, 
                                              UN_VALORES => MI_VALORES);

            IF MI_RESULTADO <= 0
            THEN 
              MI_RESPUESTA := 'Comprobante:  ' || RS_DETALLE.TIPO_CPTE || ' Número: ' || RS_DETALLE.COMPROBANTE || 
                              ' Consecutivo: ' || RS_DETALLE.CONSECUTIVO || '.' || CHR(13); 
            END IF;
            IF MI_RESPUESTA IS NOT NULL
            THEN
              MI_RESPUESTA := '-3, ' || 'No se pudo crear el detalle de los siguientes comprobantes:  ' || CHR(13)
                              || CHR(13) || MI_RESPUESTA;
            END IF;
            MI_CAMPOS := ' COMPANIA, 
                           ANO, 
                           TIPO_CPTE, 
                           COMPROBANTE, 
                           ANO_AFECT, 
                           TIPO_CPTE_AFECT, 
                           COMPROBANTE_AFECT, 
                           CREATED_BY, 
                           MODIFIED_BY, 
                           DATE_CREATED ';
            MI_VALORES := ''''|| MI_COMPANIA_NIIF ||
                          ''', '|| UN_ANIO || 
                          ', ''' || RS_DETALLE.TIPO_CPTE || 
                          ''', ' || RS_DETALLE.COMPROBANTE || 
                          ', ' || NVL(RS_DETALLE.ANO_AFECT,0) ||
                          ', ''' || NVL(RS_DETALLE.TIPO_CPTE_AFECT, 0) || 
                          ''', ' || NVL(RS_DETALLE.CMPTE_AFECTADO,0) || 
                          ', ''' || RS_DETALLE.CREATED_BY || 
                          ''', ''' || RS_DETALLE.MODIFIED_BY || 
                          ''', ''' || RS_DETALLE.DATE_CREATED || '''' ;
            MI_STRETAPA := 'los comprobantes contables afectados';
            MI_TABLA := 'COMPROBANTE_CNTAFECTADOS';
            MI_RESULTADO := PCK_DATOS.FC_ACME(UN_TABLA   => 'COMPROBANTE_CNTAFECTADOS', 
                                              UN_ACCION  => 'I', 
                                              UN_CAMPOS  => MI_CAMPOS, 
                                              UN_VALORES => MI_VALORES);
          END;
        END LOOP CONTABILIZA;
        --'****************************************************************************************
        --TERMINA: CREA INFORMACION EN LA COMPROBANTE_CNT,DETALLE_COMPROBANTE_CNT, Comprobante_cntRetencion
        --'**************************************************************************************** 
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
        MI_MSGERROR(1).CLAVE := 'MENSAJE';
        MI_MSGERROR(1).VALOR := MI_STRETAPA;
        MI_MSGERROR(2).CLAVE := 'COMPNIIF';
        MI_MSGERROR(2).VALOR := MI_COMPANIA_NIIF;
        MI_MSGERROR(3).CLAVE := 'ANIO';
        MI_MSGERROR(3).VALOR := UN_ANIO;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_REGCOMPCNT,
          UN_REEMPLAZOS => MI_MSGERROR,
          UN_TABLAERROR => MI_TABLA
        ); 
    END;    
    IF MI_RESPUESTA IS NULL 
    AND MI_RESULTADO >= 0
    THEN
      MI_RESPUESTA := 'OK';
    END IF;

    RETURN MI_RESPUESTA;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
      UN_EXC_COD    => SQLCODE,
      UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTABILIDAD_NIIFLOTES,
      UN_TABLAERROR => 'COMPROBANTE_CNT'
    );

  END FC_NIIF_LOTES;

  FUNCTION FC_CREARCOMPANIA_NIIF 
  (
  /*
    NAME              : FC_CREARCOMPANIA_NIIF (En Access, niif_crearcompania, dentro del evento del botón CrearCompaniaNiif, form niif_lotes)
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : Leydi Milena Cortés Forero
    DATE MIGRADOR     : 30/12/2016 - 02/01/2017
    TIME              : 01:56 PM
    SOURCE MODULE     : Contabilidad - SysmanMGC2016.09.01
    DESCRIPTION       : Proceso que permite crear la Compañía NIIF.
    PARAMETERS        : UN_COMPANIA    	  => Compañia de ingreso a la aplicación
                        UN_COMPANIA_NIIF	=> Código de la compañía Niif.
    @NAME:  crearCompaniaNiifLotes
    @METHOD:  GET
    */
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_COMPANIA_NIIF    IN PCK_SUBTIPOS.TI_COMPANIA
  )
  RETURN VARCHAR2
  AS
    MI_RESULTADO      PCK_SUBTIPOS.TI_RTA_ACME; 
    MI_CAMPOS         VARCHAR2(3200 CHAR); 
    MI_VALORES        VARCHAR2(3200 CHAR);
    MI_COMPANIA		    PCK_SUBTIPOS.TI_COMPANIA;
    MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR; 
  BEGIN
    BEGIN
      BEGIN
        --' CREA AÑOS PARA NIIF
        MI_RESULTADO := PCK_DATOS.FC_ACME(UN_TABLA       => 'ANO', 
                                          UN_ACCION      => 'IN', 
                                          UN_MERGEUSING  => 'SELECT ''' || UN_COMPANIA_NIIF || ''' COMPANIA ,
                                                                    ANO.NUMERO, 
                                                                    ANO.ASOCIADOPC,
                                                                    ANO.PLANAPROBADO,
                                                                    ANO.SALARIOMINIMO,
                                                                    ANO.CUANTIAMINIMA,
                                                                    ANO.VALORUVT,
                                                                    ANO.MAXIMOUVT,
                                                                    ANO.PORCENTAJESALUD,
                                                                    ANO.PORCENTAJEPENSION,
                                                                    ANO.VLRFONDOSOL_PENSIONAL
                                                               FROM ANO  
                                                              WHERE ANO.COMPANIA = ''' || UN_COMPANIA||  '''', 
                                          UN_MERGEENLACE => ' TABLA.COMPANIA  = VISTA.COMPANIA
                                                          AND TABLA.NUMERO    = VISTA.NUMERO ',
                                          UN_MERGENOEXIS => ' INSERT (TABLA.COMPANIA, 
                                                                      TABLA.NUMERO,
                                                                      TABLA.ASOCIADOPC,
                                                                      TABLA.PLANAPROBADO, 
                                                                      TABLA.SALARIOMINIMO, 
                                                                      TABLA.CUANTIAMINIMA,
                                                                      TABLA.VALORUVT,
                                                                      TABLA.MAXIMOUVT, 
                                                                      TABLA.PORCENTAJESALUD,
                                                                      TABLA.PORCENTAJEPENSION,
                                                                      TABLA.VLRFONDOSOL_PENSIONAL) 
                                                              VALUES (VISTA.COMPANIA, 
                                                                      VISTA.NUMERO, 
                                                                      VISTA.ASOCIADOPC, 
                                                                      VISTA.PLANAPROBADO, 
                                                                      VISTA.SALARIOMINIMO, 
                                                                      VISTA.CUANTIAMINIMA,
                                                                      VISTA.VALORUVT, 
                                                                      VISTA.MAXIMOUVT, 
                                                                      VISTA.PORCENTAJESALUD, 
                                                                      VISTA.PORCENTAJEPENSION, 
                                                                      VISTA.VLRFONDOSOL_PENSIONAL)') ;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;
    EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
        MI_MSGERROR(1).CLAVE := 'COMPANIA_NIIF';
        MI_MSGERROR(1).VALOR := UN_COMPANIA_NIIF;
        MI_MSGERROR(2).CLAVE := 'CADENA';
        MI_MSGERROR(2).VALOR := 'para el año seleccionado';
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_EXTISTE_COMPNIIF,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    BEGIN  
      BEGIN
        --'CREA MES PARA NIIF
        MI_RESULTADO := PCK_DATOS.FC_ACME
                                (UN_TABLA       => 'MES', 
                                 UN_ACCION      => 'IN', 
                                 UN_MERGEUSING  => 'SELECT ''' || UN_COMPANIA_NIIF || ''' COMPANIA ,
                                                          MES.ANO,
                                                          MES.NUMERO,
                                                          MES.PAAG_MENSUAL,
                                                          MES.PAAG_ACUMULADO,
                                                          MES.PAAG_ANUAL
                                                     FROM MES  
                                                    WHERE MES.COMPANIA = ''' || UN_COMPANIA ||  '''', 
                                 UN_MERGEENLACE => ' TABLA.COMPANIA  = VISTA.COMPANIA
                                                 AND TABLA.ANO       = VISTA.ANO
                                                 AND TABLA.NUMERO    = VISTA.NUMERO ',
                                 UN_MERGENOEXIS => ' INSERT (TABLA.COMPANIA, 
                                                             TABLA.ANO, 
                                                             TABLA.NUMERO,
                                                             TABLA.PAAG_MENSUAL, 
                                                             TABLA.PAAG_ACUMULADO, 
                                                             TABLA.PAAG_ANUAL) 
                                                     VALUES (VISTA.COMPANIA, 
                                                             VISTA.ANO, 
                                                             VISTA.NUMERO, 
                                                             VISTA.PAAG_MENSUAL, 
                                                             VISTA.PAAG_ACUMULADO, 
                                                             VISTA.PAAG_ANUAL)') ;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;
    EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
        MI_MSGERROR(1).CLAVE := 'COMPANIA_NIIF';
        MI_MSGERROR(1).VALOR := UN_COMPANIA_NIIF;
        MI_MSGERROR(2).CLAVE := 'CADENA';
        MI_MSGERROR(2).VALOR := 'para el año seleccionado';
      PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_EXTISTE_COMPNIIF,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;              
    BEGIN
      SELECT DISTINCT 'X'
        INTO MI_COMPANIA
        FROM COMPANIA 
       WHERE CODIGO = UN_COMPANIA_NIIF;
    EXCEPTION WHEN NO_DATA_FOUND
      THEN 
        MI_COMPANIA := '0'; 
    END;

    IF MI_COMPANIA = '0'
    THEN
      BEGIN
        BEGIN
          MI_CAMPOS  := 'CODIGO, 
                         NOMBRE, 
                         SIGLACOMPANIA,
                         RETENEDOR_FTE,
                         RETENEDOR_IVA, 
                         RETENEDOR_ICA, 
                         RETENEDOR_TBRE, 
                         DIRECCION,
                         TELEFONO, 
                         CONSOLIDADA,
                         LICENCIA,
                         PAIS,
                         DEPARTAMENTO,
                         CIUDAD, 
                         NITCOMPANIA, 
                         FAX, 
                         CODIGODANE, 
                         CLAVE,
                         DIRECCIONEMAIL, 
                         TIPOENTIDAD, 
                         CODIGOSCHIP';
          MI_VALORES := 'SELECT ''' || UN_COMPANIA_NIIF || ''' ,
                              NOMBRE,
                              SIGLACOMPANIA,
                              RETENEDOR_FTE,
                              RETENEDOR_IVA,
                              RETENEDOR_ICA,
                              RETENEDOR_TBRE,
                              DIRECCION,
                              TELEFONO,
                              CONSOLIDADA,
                              LICENCIA,
                              PAIS,
                              DEPARTAMENTO,
                              CIUDAD,
                              NITCOMPANIA,
                              FAX,
                              CODIGODANE,
                              CLAVE,
                              DIRECCIONEMAIL,
                              TIPOENTIDAD,
                              CODIGOSCHIP
                         FROM COMPANIA
                        WHERE COMPANIA.CODIGO = ''' || UN_COMPANIA ||  '''';

          MI_RESULTADO := PCK_DATOS.FC_ACME(UN_TABLA   => 'COMPANIA', 
                                            UN_ACCION  => 'IS', 
                                            UN_CAMPOS  => MI_CAMPOS, 
                                            UN_VALORES => MI_VALORES);
          RETURN MI_RESULTADO;           
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
      EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
        MI_MSGERROR(1).CLAVE := 'COMPANIA_NIIF';
        MI_MSGERROR(1).VALOR := UN_COMPANIA_NIIF;
        MI_MSGERROR(2).CLAVE := 'CADENA';
        MI_MSGERROR(2).VALOR := 'en la tabla COMPANIA';
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_EXTISTE_COMPNIIF,
          UN_REEMPLAZOS => MI_MSGERROR
        );
      END;
    END IF;

  END;

  RETURN MI_RESULTADO;

END FC_CREARCOMPANIA_NIIF;

END PCK_CONTABILIDAD2;