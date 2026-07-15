create or replace PACKAGE BODY "PCK_PREDIAL" AS
/**@package:  Predial**/
FUNCTION FC_ENCABEZADO_COLUMNA 
  /*
    NAME              : FC_ENCABEZADO_COLUMNA En Access --> Encabezado_Columna
    AUTHORS           : SYSMAN  SAS 
    AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
    DATE MIGRADOR     : 07/07/2016
    TIME              : 2:31 PM
    SOURCE MODULE     : PREDIAL
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA / SANDRA MILENA DAZA LEGUIZAMON
    DATE MODIFIED     : 09/01/2017  -- 18/08/2017
    TIME              : 02:31 PM
    DESCRIPTION       : Retorna el encabezado o el nombre  de acuerdo al número ingresado por parámetro. / Se cambió el estándar de codificación 
                        y se agrego manejo de excepciones. / En la excepción se retorna que el concepto no se ha configurado para no generar interrup
                        ción del proceso por conceptos que no se van a calcular en una entidad pero que tal vez por estandar esta en formularios o 
                        reportes. 
    @NAME:  consultarEncabezadoDeColumna
    @METHOD:  GET     
  */
  (
  UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CONCEPTO IN PCK_SUBTIPOS.TI_ENTERO	
  )
  RETURN VARCHAR2 AS
    MI_ERROR_FUN              PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 1;
    MI_ANO                    PCK_SUBTIPOS.TI_ANIO;
    MI_ENCABEZADO             VARCHAR2(200 CHAR);
    MI_NOMBRE                 VARCHAR2(200 CHAR); 
    MI_VLRRETORNO             VARCHAR2(200 CHAR);
BEGIN 
  BEGIN
    SELECT TO_CHAR(SYSDATE, 'YYYY') 
    INTO   MI_ANO 
    FROM   DUAL;

    SELECT  IP_CONCEPTOS.ENCABEZADO
            ,IP_CONCEPTOS.NOMBRE
    INTO    MI_ENCABEZADO
            ,MI_NOMBRE
    FROM    IP_CONCEPTOS
    WHERE   IP_CONCEPTOS.COMPANIA = UN_COMPANIA
      AND   IP_CONCEPTOS.ANO = MI_ANO
      AND   IP_CONCEPTOS.CODIGO = UN_CONCEPTO;    

    MI_VLRRETORNO := MI_ENCABEZADO; 

    MI_VLRRETORNO := REPLACE (MI_VLRRETORNO, '.' , ' ');
    MI_VLRRETORNO := REPLACE (MI_VLRRETORNO, ',' , ' ');

    RETURN MI_VLRRETORNO;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
      MI_VLRRETORNO := 'Concepto sin configurar';
      RETURN MI_VLRRETORNO;
    END;

END FC_ENCABEZADO_COLUMNA;      

--2

FUNCTION FC_PERMISOACCION
  /*
    NAME              : FC_PERMISOACCION En Access --> PERMISOACCION
    AUTHORS           : SYSMAN  SAS 
    AUTHOR MIGRACION  : SANDRA MILENA DAZA LEGUIZAMÓN
    DATE MIGRADOR     : 07/07/2016
    TIME              : 3:23 PM
    SOURCE MODULE     : PREDIAL
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED     : 10/01/2017
    TIME              : 08:35 AM
    DESCRIPTION       : Retorna verdadero si el usuario esta configurado en el valor del parámetro de sistema definido en la acción / Se cambió el estándar de codificación 
                        y se agrego manejo de excepciones.
    @NAME:  consultarNombreUsuarioEnParametro
    @METHOD:  GET    
  */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_MODULO   IN PCK_SUBTIPOS.TI_MODULO, 
    UN_ACCION   IN VARCHAR2, 
    UN_USUARIO  IN PCK_SUBTIPOS.TI_USUARIO
  )
  RETURN NUMBER AS
    MI_ERROR_FUN              PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 2;  
    MI_EXISTE                 PCK_SUBTIPOS.TI_DOBLE	;
    MI_STRSQL                 PCK_SUBTIPOS.TI_STRSQL;
BEGIN
  BEGIN
    MI_STRSQL := ' SELECT COUNT (0)  ' ||
                 ' FROM   PARAMETRO ' ||
                 ' WHERE  COMPANIA = ''' || UN_COMPANIA || '''' ||
                 '   AND  NOMBRE   = ''' || UN_ACCION || '''' || 
                 '   AND  VALOR    LIKE ''%' || UN_USUARIO || '%''' ||
                 '   AND  MODULO   = ' || UN_MODULO ;

    EXECUTE IMMEDIATE MI_STRSQL INTO MI_EXISTE;

    EXCEPTION WHEN NO_DATA_FOUND THEN 
     RAISE PCK_EXCEPCIONES.EXC_PREDIAL; 
  END ;
   IF MI_EXISTE = 0 THEN    
      RETURN 0; 
    ELSE
      RETURN 1;
    END IF;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_PERMISOACCION
      );

END FC_PERMISOACCION;

--3

FUNCTION FC_PAGOANOVALIDO
 /*
    NAME              : FC_PAGOANOVALIDO En Access --> PagoAnoValido
    AUTHORS           : SYSMAN  SAS 
    AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
    DATE MIGRADOR     : 29/07/2016
    TIME              : 03:12 PM
    SOURCE MODULE     : PREDIAL
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED     : 10/01/2017
    TIME              : 09:54 AM
    DESCRIPTION       : Retorna el año valido para ejecutar un pago./ Se cambió el estándar de codificación 
                        y se agrego manejo de excepciones.
    @NAME:  consultarVigenciaValidaParaPago
    @METHOD:  GET 
  */
(
  UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_PREDIO   IN PCK_SUBTIPOS.TI_CODPREDIO
)
RETURN NUMBER AS 
    MI_ERROR_FUN    PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 3;  
    MI_ANOMENOR     PCK_SUBTIPOS.TI_ANIO;
    MI_PAGO         PCK_SUBTIPOS.TI_DOBLE;
    MI_ANOMAYOR     PCK_SUBTIPOS.TI_ANIO;
    MI_ANOACTUAL    PCK_SUBTIPOS.TI_ANIO;
    MI_ANOVALIDO    PCK_SUBTIPOS.TI_ANIO;

BEGIN 
  BEGIN
    SELECT TO_CHAR(SYSDATE, 'YYYY')
    INTO   MI_ANOACTUAL
    FROM   DUAL;

    SELECT  MIN(IP_FACTURADOS.PREANO) AS MENOR 
            ,MIN(IP_FACTURADOS.PAGADO) AS PAGO
    INTO    MI_ANOMENOR , MI_PAGO
    FROM    IP_FACTURADOS
    WHERE   IP_FACTURADOS.COMPANIA  = UN_COMPANIA
      AND   IP_FACTURADOS.CODIGO    = UN_PREDIO 
      AND   IP_FACTURADOS.PAGADO    IN (0)  
      AND   IP_FACTURADOS.NOCOBRADO IN (0);

   IF MI_ANOMENOR IS NOT NULL THEN    
      IF MI_ANOMENOR =  MI_ANOACTUAL THEN 
         MI_ANOVALIDO := MI_ANOMENOR - 1;
      ELSE 
        IF MI_PAGO = 0 THEN   
          MI_ANOVALIDO := MI_ANOMENOR -1 ;
        ELSE 
          MI_ANOVALIDO := MI_ANOMENOR;
        END IF;  
      END IF; 
    ELSE
      SELECT MAX(IP_FACTURADOS.PREANO) AS MAYOR
      INTO   MI_ANOMAYOR
      FROM   IP_FACTURADOS  
      WHERE  IP_FACTURADOS.COMPANIA = UN_COMPANIA  
        AND  IP_FACTURADOS.CODIGO   = UN_PREDIO 
        AND  IP_FACTURADOS.PAGADO   NOT IN (0);

      IF MI_ANOMAYOR IS NOT NULL THEN 
        MI_ANOVALIDO  :=  MI_ANOMAYOR;
      ELSE 
        MI_ANOVALIDO := 0;
      END IF;
   END IF;
   RETURN MI_ANOVALIDO;

   EXCEPTION WHEN NO_DATA_FOUND THEN 
     RAISE PCK_EXCEPCIONES.EXC_PREDIAL; 

  END; 

   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
      UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_ANOVALIDO
   );

END  FC_PAGOANOVALIDO;

--4

PROCEDURE PR_ACT_ULTVIGCANCELADA_ANT
  (
     /*
    NAME              : PR_ACT_ULTVIGCANCELADA_ANT En Access --> Actualizar_UltimaVigCancelada_Ant
    AUTHORS           : SYSMAN  SAS 
    AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
    DATE MIGRADOR     : 02/08/2016
    TIME              : 10:22 AM
    SOURCE MODULE     : PREDIAL
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED     : 10/01/2017
    TIME              : 11:45 AM
    DESCRIPTION       : Proceso que actualiza la ultima vigencia cancelada.
                        Se cambió el procedimiento, para que solo reciba un parámetro, 
                        debido a que en access se recibian 2 parámetros "CODINICIAL y CODFINAL" y 
                        estos dos parámetros tenian el mismo valor./Se cambió el estándar de codificación 
                        y se agrego manejo de excepciones. 
    @NAME:  actualizarUltimaVigenciaCancelada
    @METHOD:  PUT                         
  */
      UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_CODIGO         IN PCK_SUBTIPOS.TI_CODPREDIO

  ) 

  AS 
  --MI_ERROR_FUN        PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 4;
  MI_ANOVALIDO        PCK_SUBTIPOS.TI_ANIO;
  MI_ACTUALIZARDATOS  VARCHAR2(3 CHAR);
  MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
  MI_PREFECPAG        DATE;
  MI_PAG_BANPAG       VARCHAR2(3 CHAR);
  MI_PAQUETEPAG       VARCHAR2(5 CHAR);
  MI_DOCNUM           VARCHAR2(15 CHAR);
  MI_PREVAL           PCK_SUBTIPOS.TI_ENTERO;
  MI_SQL              PCK_SUBTIPOS.TI_STRSQL;
  MI_SSQL             VARCHAR2(32000 CHAR);
  MI_CODIGO           VARCHAR2(15 CHAR);
  MI_CONTEO           PCK_SUBTIPOS.TI_ENTERO;

BEGIN
 BEGIN
  BEGIN
    MI_SSQL := 'SELECT IP_USUARIOS_PREDIAL.CODIGO 
                FROM   IP_USUARIOS_PREDIAL 
                WHERE  IP_USUARIOS_PREDIAL.COMPANIA         = '''||UN_COMPANIA||''' 
                  AND  IP_USUARIOS_PREDIAL.CODIGO           = '''|| UN_CODIGO || ''' 
                  AND  IP_USUARIOS_PREDIAL.NUMERO_ORDEN     = '''||PCK_DATOS.CONS_NUMERO_ORDEN_PREDIAL||''' 
                  AND  IP_USUARIOS_PREDIAL.INDBORRADO       IN(0) 
                  AND  IP_USUARIOS_PREDIAL.CODIGO_NO_ACTIVO IN(0)';

      EXECUTE IMMEDIATE MI_SSQL INTO MI_CODIGO;

      EXCEPTION WHEN NO_DATA_FOUND THEN 
       RAISE PCK_EXCEPCIONES.EXC_PREDIAL;

  END;    
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE
        ,UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_UPDATE_USUPRED
   ); 
 END;    

        MI_ANOVALIDO := PCK_PREDIAL.FC_PAGOANOVALIDO(UN_COMPANIA => UN_COMPANIA 
                                                     ,UN_PREDIO  => MI_CODIGO);
 BEGIN
  BEGIN
    IF MI_ANOVALIDO <> 0 THEN 
     BEGIN
        BEGIN   
         MI_ACTUALIZARDATOS := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA 
                                                     ,UN_NOMBRE    => 'ACTUALIZAR DATOS PAGO EN RUTINA DE MANTENIMIENTO' 
                                                     ,UN_MODULO    => PCK_DATOS.FC_MODULOPREDIAL
                                                     ,UN_FECHA_PAR =>SYSDATE);

                  EXCEPTION WHEN NO_DATA_FOUND THEN 
                   RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;	
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
            PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD =>SQLCODE
            ,UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_UPDATE_DATPAGO
         ); 
     END; 

			IF MI_ACTUALIZARDATOS = 'NO' THEN 

            MI_CAMPOS := 'IP_USUARIOS_PREDIAL.PAGO_ANO = '||MI_ANOVALIDO||'';
            MI_CONDICION := 'IP_USUARIOS_PREDIAL.COMPANIA = '''||UN_COMPANIA||''' 
                         AND IP_USUARIOS_PREDIAL.CODIGO   = '''||MI_CODIGO||'''';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => 'IP_USUARIOS_PREDIAL'
                                                  ,UN_ACCION    => 'M'
                                                  ,UN_CAMPOS    => MI_CAMPOS
                                                  ,UN_CONDICION => MI_CONDICION);

			ELSIF MI_ACTUALIZARDATOS = 'SI' THEN 
				BEGIN         
            MI_SQL := 'SELECT  IP_RECIBOS_DE_PAGO.PAG_BANPAG 
                              ,IP_RECIBOS_DE_PAGO.PREFECPAG
                              ,IP_RECIBOS_DE_PAGO.PAQUETEPAG 
                              ,IP_RECIBOS_DE_PAGO.DOCNUM 
                              ,IP_RECIBOS_DE_PAGO.PREVAL
                       FROM     IP_RECIBOS_DE_PAGO 
                       WHERE    IP_RECIBOS_DE_PAGO.COMPANIA = '''||UN_COMPANIA||'''
                         AND    IP_RECIBOS_DE_PAGO.PRECOD   = '''||MI_CODIGO||''' 
                         AND    IP_RECIBOS_DE_PAGO.PREANO   = '||MI_ANOVALIDO||'
                         AND    IP_RECIBOS_DE_PAGO.ANULADO  IN(0) 
                         AND    IP_RECIBOS_DE_PAGO.PAGO     NOT IN(0) 
                         AND    ROWNUM = 1
                       ORDER BY IP_RECIBOS_DE_PAGO.PREFECPAG DESC';
                 EXECUTE IMMEDIATE MI_SQL  INTO MI_PAG_BANPAG , MI_PREFECPAG , MI_PAQUETEPAG , MI_DOCNUM, MI_PREVAL;

                  EXCEPTION WHEN NO_DATA_FOUND THEN
                  MI_CONTEO := 0;                    
				END;  

              IF  MI_PAG_BANPAG IS NOT NULL AND MI_PREFECPAG IS NOT NULL THEN 
                  MI_CAMPOS := 'IP_USUARIOS_PREDIAL.PAGO_ANO = '||MI_ANOVALIDO||' 
                               ,IP_USUARIOS_PREDIAL.PAG_FEC = TO_DATE('''||MI_PREFECPAG||''',''DD/MM/YYYY HH24:mi:ss'')
                               ,IP_USUARIOS_PREDIAL.PAG_VAL = '''||MI_PREVAL||'''
                               ,IP_USUARIOS_PREDIAL.NUM_COM = '''||MI_DOCNUM||'''
                               ,IP_USUARIOS_PREDIAL.PAG_BAN = '''||MI_PAG_BANPAG||''' '; 
                  MI_CONDICION := 'IP_USUARIOS_PREDIAL.COMPANIA = '''||UN_COMPANIA||''' 
                               AND IP_USUARIOS_PREDIAL.CODIGO = '''||MI_CODIGO||'''';
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => 'IP_USUARIOS_PREDIAL'
                                                        ,UN_ACCION    => 'M'
                                                        ,UN_CAMPOS    => MI_CAMPOS
                                                        ,UN_CONDICION => MI_CONDICION);

              END IF;

              --si no encuentra registros en la consulta MI_SQL
              IF MI_CONTEO = 0 THEN
                MI_CAMPOS := 'IP_USUARIOS_PREDIAL.PAGO_ANO = '||MI_ANOVALIDO||'';
                MI_CONDICION := 'IP_USUARIOS_PREDIAL.COMPANIA = '''||UN_COMPANIA||''' 
                             AND IP_USUARIOS_PREDIAL.CODIGO   = '''||MI_CODIGO||'''';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => 'IP_USUARIOS_PREDIAL'
                                                      ,UN_ACCION    => 'M'
                                                      ,UN_CAMPOS    => MI_CAMPOS
                                                      ,UN_CONDICION => MI_CONDICION);
              END IF;             
      END IF;
    END IF;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
  END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD     => SQLCODE
                        ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_UPDATE_IPUSPRE
            );
 END; 

END PR_ACT_ULTVIGCANCELADA_ANT;       


--5

FUNCTION FC_REVERSARPAGO_RECIBO
  (
   /*
    NAME              : FC_REVERSARPAGO_RECIBO En Access --> ReversarPago_Recibo
    AUTHORS           : SYSMAN  SAS 
    AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
    DATE MIGRADOR     : 03/08/2016
    TIME              : 4:31 PM
    SOURCE MODULE     : PREDIAL
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA \ JAVIER RODRIGUEZ
    DATE MODIFIED     : 10/01/2017
    TIME              : 2:31 PM
    DESCRIPTION       : Función que anula los pagos realizados a los predios. / Se cambió el estándar de codificación 
                        y se agrego manejo de excepciones.  \  SE SEPARA EL PROCESO DE REVERSAR.
    @NAME:  reversarPagoPredio
    @METHOD:  GET 
  */
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_NUM_FACTURA  IN PCK_SUBTIPOS.TI_DOCNUM,
    UN_COD_PREDIO   IN PCK_SUBTIPOS.TI_CODPREDIO,
    UN_PAG_BAN      IN IP_RECIBOS_DE_PAGO.PAG_BANPAG%TYPE,
    UN_PAQUETE      IN IP_RECIBOS_DE_PAGO.PAQUETEPAG%TYPE,
    UN_FECHAPAGO    IN DATE,
    UN_COD_ACUERDO  IN IP_FACTURADOSACUERDOS.CODIGOACUERDO%TYPE,
    UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO
  )
  RETURN VARCHAR2
  AS 
    MI_SQL               PCK_SUBTIPOS.TI_STRSQL;
    MI_ENTROFAC          PCK_SUBTIPOS.TI_ENTERO;
    MI_ENTROFACAB        PCK_SUBTIPOS.TI_ENTERO;
    MI_ESABONO           PCK_SUBTIPOS.TI_ENTERO;
    MI_ESACUERDO         PCK_SUBTIPOS.TI_ENTERO;
    MI_ESCUOTA           PCK_SUBTIPOS.TI_ENTERO;
    MI_TABLA             PCK_SUBTIPOS.TI_TABLA;  
    MI_CAMPOS            PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION         PCK_SUBTIPOS.TI_CONDICION;
    MI_PREANO            PCK_SUBTIPOS.TI_ANIO;
    MI_PREANOI           PCK_SUBTIPOS.TI_ANIO;
    MI_CUOTAXANULAR      PCK_SUBTIPOS.TI_ENTERO;
    MI_CUOTACERO         PCK_SUBTIPOS.TI_DOBLE;
    MI_CUOTASPAGADAS     PCK_SUBTIPOS.TI_ENTERO;
    MI_RTA               VARCHAR2(500 CHAR);
    MI_CONT              VARCHAR2(300 CHAR);
    MI_EXISTE            PCK_SUBTIPOS.TI_ENTERO;
    MI_ABONOAACUERDO     PCK_SUBTIPOS.TI_LOGICO;
    MI_ESEXCEDENTE       PCK_SUBTIPOS.TI_LOGICO;
BEGIN
    BEGIN
        SELECT COUNT(1) EXISTE 
          INTO MI_EXISTE  
          FROM IP_RECIBOS_DE_PAGO 
         WHERE COMPANIA = UN_COMPANIA 
           AND DOCNUM   = UN_NUM_FACTURA;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_EXISTE:=0;
    END; 

    IF MI_EXISTE NOT IN (0) THEN
        BEGIN
        --Consulta base , para filtros
            SELECT  IP_RECIBOS_DE_PAGO.ESABONO
                   ,IP_RECIBOS_DE_PAGO.PREANO
                   ,IP_RECIBOS_DE_PAGO.PREANOI
                   ,IP_RECIBOS_DE_PAGO.ESACUERDO
                   ,IP_RECIBOS_DE_PAGO.ESCUOTA
                   ,IP_RECIBOS_DE_PAGO.ABONOAACUERDO
                   ,IP_RECIBOS_DE_PAGO.ESEXCEDENTE
              INTO  MI_ESABONO  
                   ,MI_PREANO 
                   ,MI_PREANOI 
                   ,MI_ESACUERDO 
                   ,MI_ESCUOTA
                   ,MI_ABONOAACUERDO
                   ,MI_ESEXCEDENTE 
            FROM   IP_RECIBOS_DE_PAGO
            WHERE  IP_RECIBOS_DE_PAGO.COMPANIA = UN_COMPANIA
              AND  IP_RECIBOS_DE_PAGO.DOCNUM   = UN_NUM_FACTURA;
        EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_ESABONO:=0;  
            MI_PREANO:=0; 
            MI_PREANOI:=0; 
            MI_ESACUERDO:=0; 
            MI_ESCUOTA:=0;
            MI_ABONOAACUERDO:=0;
            MI_ESEXCEDENTE :=0;
        END;
        --Es abono 0 ó -1
        IF MI_ESABONO IS NOT NULL 
           AND MI_ESABONO NOT IN (0) THEN 
              --REVERSIÓN DE ABONOS
              --Al ingresar por el if de "esabono" se asigna valor a la variable MI_ENTROFACAB
              PCK_PREDIAL_COM6.PR_REVERSARABONOS( 
                                 UN_COMPANIA     => UN_COMPANIA
                                ,UN_NUM_FACTURA  => UN_NUM_FACTURA
                                ,UN_COD_PREDIO   => UN_COD_PREDIO
                                ,UN_USUARIO      => UN_USUARIO
                                ,UN_PREANO       => MI_PREANO
                                ,UN_PREANOI      => MI_PREANOI);
        /*
          '(TAR:1000068366; FECHA:17/01/2016; AUTOR:AA)
          'Se incluye validación para anular el recibo de pago de acuerdos de pago que no son excedentes
        */
        ELSIF NVL(MI_ESACUERDO,0) NOT IN (0) 
              AND MI_ESEXCEDENTE IN (0) THEN 
            --REVERSIÓN DE PAGOS DE CUOTAS DE ACUERDOS
            PCK_PREDIAL_COM6.PR_REVERSARPAGOSCUOTASACUERDOS( 
                                           UN_COMPANIA      => UN_COMPANIA
                                          ,UN_NUM_FACTURA   => UN_NUM_FACTURA
                                          ,UN_COD_PREDIO    => UN_COD_PREDIO
                                          ,UN_COD_ACUERDO   => UN_COD_ACUERDO
                                          ,UN_USUARIO       => UN_USUARIO
                                          ,UN_ABONOAACUERDO => MI_ABONOAACUERDO
                                           );

        --'(TAR:1000068366; FECHA:17/01/2017; AUTOR:AA)
        --'Se incluye condición para eliminar acuerdos de pago de excedentes
        ELSIF  MI_ESACUERDO NOT IN (0) 
              AND MI_ESEXCEDENTE NOT IN (0) THEN 
            --'REVERSION DE PAGOS DE CUOTAS DE ACUERDO DE EXCEDENTES  
            PCK_PREDIAL_COM6.PR_REVERSARPAGOSEXCEDENTES(
                    UN_COMPANIA      => UN_COMPANIA,
                    UN_NUM_FACTURA   => UN_NUM_FACTURA,
                    UN_COD_PREDIO    => UN_COD_PREDIO,
                    UN_COD_ACUERDO   => UN_COD_ACUERDO,
                    UN_USUARIO       => UN_USUARIO );
        ELSIF MI_ESCUOTA NOT IN (0) THEN 
            --No es posible revertir el pago. Por que corresponde a una Cuota.-- 
            /*MI_RTA := 'No es posible revertir el pago. Por que corresponde a una Cuota. El pago no fue Revertido Correctamente';
            RETURN MI_RTA;  */
            BEGIN 
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REVRECIBOPAGESCUO
                          );
            END;
        ELSIF MI_ESABONO IS NULL OR MI_ESABONO IN (0) 
                      AND MI_ESCUOTA IS NULL OR MI_ESCUOTA IN (0) 
                      AND MI_ESACUERDO IS NULL OR MI_ESACUERDO IN (0) THEN 
                PCK_PREDIAL_COM6.PR_REVERSARPAGOFINAL(
                    UN_COMPANIA    => UN_COMPANIA ,
                    UN_NUM_FACTURA => UN_NUM_FACTURA,
                    UN_COD_PREDIO  => UN_COD_PREDIO,
                    UN_PREANO      => MI_PREANO,
                    UN_PREANOI     => MI_PREANOI,
                    UN_PAG_BAN     => UN_PAG_BAN,
                    UN_PAQUETE     => UN_PAQUETE,
                    UN_FECHAPAGO   => UN_FECHAPAGO,
                    UN_USUARIO     => UN_USUARIO
                 );
        END IF;
    END IF;
    MI_RTA := 'Proceso Terminado';
    RETURN MI_RTA;    

END  FC_REVERSARPAGO_RECIBO;

-- 6  

PROCEDURE PR_AUDITORIA 
  (
     /*
    NAME              : PR_AUDITORIA En Access --> auditoria
    AUTHORS           : SYSMAN  SAS 
    AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
    DATE MIGRADOR     : 08/08/2016
    TIME              : 7:54 AM
    SOURCE MODULE     : PREDIAL
    MODIFIER          : JORGE ALBERTO LOZANO CHARRY / ELKIN GEOVANNY AMAYA SILVA / LEYDI CORTÉS - JUAN RODRÍGUEZ \ JAVIER RODRIGUEZ
                        \ JAVIER RODRIGUEZ 
    DATE MODIFIED     : 05/09/2016 / 11/01/2017 / 14/02/2017 \ 15/03/2017 \ 27/06/2017
    TIME              : 08:35 AM
    DESCRIPTION       : Procedimiento que inserta a la tabla IP_AUDITORIA, los cambios que se le hacen a un predio. / Se cambió el estándar de codificación 
                        y se agrego manejo de excepciones. 
                        / Formato para insertar fecha y hora de la auditoría. \ Se ajusta para que se guarde el numero de orden del predio auditado.
                        \ SE AJUSTA PARA QUE ALMACENE LOS CAMPOS DE AUDITORIA DE LA BD 
    @NAME:  insertarCambiosEnAuditoria
    @METHOD:  POST     
  */

    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CODMOD         IN IP_AUDITORIA.MODCOD%TYPE,
    UN_OPEMOD         IN IP_AUDITORIA.MODOPE%TYPE,
    UN_CCOMOD         IN IP_AUDITORIA.MODCCO%TYPE,
    UN_VANMOD         IN IP_AUDITORIA.MODVAN%TYPE,
    UN_VNUMOD         IN IP_AUDITORIA.MODVNU%TYPE,
    UN_DESCRIPCION    IN IP_AUDITORIA.DESCRIPCION%TYPE,
    UN_FECMOD         IN TIMESTAMP DEFAULT SYSDATE,
    UN_HORMOD         IN TIMESTAMP DEFAULT SYSDATE,
    UN_NUMERO_ORDEN   IN PCK_SUBTIPOS.TI_NUMORDEN DEFAULT '001'
   )

AS 
  MI_ERROR_FUN        PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 6;
  MI_VALORANTERIOR    IP_AUDITORIA.MODVAN%TYPE;
  MI_VALORNUEVO       IP_AUDITORIA.MODVNU%TYPE;
  MI_DESCRIPCION      IP_AUDITORIA.DESCRIPCION%TYPE;
  MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES          PCK_SUBTIPOS.TI_VALORES;


BEGIN 
    BEGIN
        IF UN_DESCRIPCION IS NULL THEN 
            MI_DESCRIPCION := ' ';
        ELSE 
            MI_DESCRIPCION := UN_DESCRIPCION;
        END IF;
        MI_VALORANTERIOR := NVL(UN_VANMOD,'-');
        MI_VALORANTERIOR := REPLACE(MI_VALORANTERIOR, '''', ' ');
        MI_VALORNUEVO    := NVL(UN_VNUMOD, '-');
        MI_VALORNUEVO    := REPLACE(MI_VALORNUEVO, '''', ' ');

        MI_CAMPOS := 'IP_AUDITORIA.COMPANIA
                     ,IP_AUDITORIA.MODCOD
                     ,IP_AUDITORIA.NUMERO_ORDEN
                     ,IP_AUDITORIA.MODOPE
                     ,IP_AUDITORIA.MODFEC
                     ,IP_AUDITORIA.MODHOR
                     ,IP_AUDITORIA.MODCCO
                     ,IP_AUDITORIA.MODVAN
                     ,IP_AUDITORIA.MODVNU
                     ,IP_AUDITORIA.DESCRIPCION
                     ,IP_AUDITORIA.DATE_CREATED
                     ,IP_AUDITORIA.CREATED_BY';
        MI_VALORES := ''''||UN_COMPANIA||'''
                      ,'''||UN_CODMOD||'''
                      ,'''||UN_NUMERO_ORDEN||'''
                      ,'''||UN_OPEMOD||'''
                      ,TO_DATE(''' || TO_CHAR(UN_FECMOD, 'DD/MM/YYYY HH24:mi:ss') || ''',''DD/MM/YYYY HH24:mi:ss'')
                      ,TO_DATE('''|| TO_CHAR(UN_HORMOD+1/86400000, 'DD/MM/YYYY HH24:mi:ss') || ''',''DD/MM/YYYY HH24:mi:ss'')
                      ,'''||UN_CCOMOD||''' 
                      ,'''||MI_VALORANTERIOR||'''
                      ,'''||MI_VALORNUEVO||'''
                      ,'''||MI_DESCRIPCION||'''
                      ,SYSDATE
                      ,'''||UN_OPEMOD||''' ';

        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                      UN_TABLA    => 'IP_AUDITORIA'
                                     ,UN_ACCION  => 'I'
                                     ,UN_CAMPOS  => MI_CAMPOS
                                     ,UN_VALORES => MI_VALORES);

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;     
    END;
EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
    PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE,
                UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INSERT_IPAUDI,
                UN_TABLAERROR => 'IP_AUDITORIA'
         ); 

END PR_AUDITORIA;

--7

FUNCTION FC_AUSUBAUTOAVALUO
  (
   /*
    NAME              : PR_SUBAUTOAVALUO En Access --> Sub_autoavaluo
    AUTHORS           : SYSMAN  SAS 
    AUTHOR MIGRACION  : JORGE ALBERTO LOZANO CHARRY
    DATE MIGRADOR     : 10/08/2016
    TIME              : 10:26 AM
    SOURCE MODULE     : PREDIAL
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED     : 11/01/2017
    DESCRIPTION       : Evento Despues de actualizar del subformulario Sub_autoavaluo. / Se cambió el estándar de codificación 
                        y se agrego manejo de excepciones.
    @NAME:  actualizarAvaluo
    @METHOD:  GET     
  */
  UN_COMPANIA         IN  PCK_SUBTIPOS.TI_COMPANIA,
  UN_CODIGOPRED       IN  PCK_SUBTIPOS.TI_CODPREDIO,
  UN_VIGENCIA         IN  VARCHAR2,
  UN_MODULO           IN  PCK_SUBTIPOS.TI_MODULO,
  UN_AVALUOIGAG       IN  PCK_SUBTIPOS.TI_DOBLE,
  UN_AVALUO           IN  PCK_SUBTIPOS.TI_DOBLE,
  UN_TRPCOD_ANT       IN  IP_FACTURADOS.TARIFAANT%TYPE,
  UN_TRPCOD           IN  IP_FACTURADOS.TRPCOD%TYPE,
  UN_PREANO           IN  PCK_SUBTIPOS.TI_ANIO,
  UN_ANO              IN  PCK_SUBTIPOS.TI_ANIO,
  UN_CONSECUTIVORAD   IN  IP_AUTOAVALUO.NUMERO_RADICACION%TYPE,
  UN_USUARIO          IN  PCK_SUBTIPOS.TI_USUARIO
  )
RETURN VARCHAR2
AS 
  MI_ULTIMOPAGO             PCK_SUBTIPOS.TI_DOBLE;
  MI_STRSQL                 PCK_SUBTIPOS.TI_STRSQL;
  MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
  MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
  MI_CONSECUTIVO            PCK_SUBTIPOS.TI_ENTERO;
  MI_AFECTADOS              PCK_SUBTIPOS.TI_DOBLE;
  MI_CONSEC                 PCK_SUBTIPOS.TI_ENTERO;
  MI_CONSECUTIVORAD         IP_AUTOAVALUO.NUMERO_RADICACION%TYPE;
  MI_FECHA_CREA_REGISTRO    DATE;
  MI_VLRRETORNO             VARCHAR2(4000 CHAR);
  CURSOR MI_VALIDA IS SELECT DISTINCT CONSECT
                             ,FECHA_CREA_REGISTRO AS FECHA_CREA_REGISTRO
                      FROM   IP_AUTOAVALUO
                      WHERE  ROWNUM            <= 1
                        AND  CODPRE            =  UN_CODIGOPRED
                        AND  PAGO_ANO          =  UN_ANO
                        AND  NUMERO_RADICACION =  UN_CONSECUTIVORAD
                      ORDER BY FECHA_CREA_REGISTRO DESC;
BEGIN
  MI_VLRRETORNO := '0';

  BEGIN
  MI_STRSQL := ' SELECT NVL(PAGO_ANO,0) AS ANIOPAGO
                 FROM   IP_USUARIOS_PREDIAL
                 WHERE  ROWNUM <= 1 
                 AND    CODIGO = '''||UN_CODIGOPRED||'''
                 ORDER BY PAGO_ANO DESC ';
  EXECUTE IMMEDIATE MI_STRSQL INTO MI_ULTIMOPAGO;
  EXECUTE IMMEDIATE MI_STRSQL INTO MI_ULTIMOPAGO;
  EXCEPTION WHEN NO_DATA_FOUND THEN
    MI_ULTIMOPAGO := 0;
  END;

  BEGIN
  MI_CAMPOS := 'AVALUOANT      = '||UN_AVALUOIGAG||'
                ,AVALUO        = '||UN_AVALUO||'
                ,TARIFAANT     = '''||UN_TRPCOD_ANT||'''
                ,TRPCOD        = '''||UN_TRPCOD||'''
                ,PREIND        = ''M''
                ,ES_AUTOAVALUO = -1';
  MI_CONDICION := ' PREANO = '||UN_PREANO||' 
               AND CODIGO  = '''||UN_CODIGOPRED||'''';
  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => 'IP_FACTURADOS'
                                        ,UN_ACCION    => 'M'
                                        ,UN_CAMPOS    => MI_CAMPOS 
                                        ,UN_CONDICION => MI_CONDICION);

  IF UN_VIGENCIA = EXTRACT(YEAR FROM SYSDATE) THEN
    MI_CAMPOS := 'AVALUO_ANO  = '||UN_AVALUO||'
                  ,AVALUO_ANT = '||UN_AVALUOIGAG||'
                  ,TRPCOD     = '''||UN_TRPCOD||'''';
    MI_CONDICION := 'CODIGO = '''||UN_CODIGOPRED||'''';
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => 'IP_USUARIOS_PREDIAL'
                                          ,UN_ACCION    => 'M'
                                          ,UN_CAMPOS    => MI_CAMPOS 
                                          ,UN_CONDICION => MI_CONDICION);

    MI_CAMPOS := ' AUTOAVALUO = -1';
    MI_CONDICION := 'CODIGO = '''||UN_CODIGOPRED||'''';
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => 'IP_USUARIOS_PREDIAL'
                                          ,UN_ACCION    => 'M'
                                          ,UN_CAMPOS    => MI_CAMPOS 
                                          ,UN_CONDICION => MI_CONDICION);
  END IF;

  IF UN_PREANO <= NVL(MI_ULTIMOPAGO,0) THEN
    MI_CAMPOS := ' PAGADO=-1';
    MI_CONDICION := 'CODIGO = '''||UN_CODIGOPRED||''' 
                 AND PREANO = '||UN_PREANO;
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => 'IP_FACTURADOS'
                                          ,UN_ACCION    => 'M'
                                          ,UN_CAMPOS    => MI_CAMPOS 
                                          ,UN_CONDICION => MI_CONDICION);
  ELSE
    MI_CAMPOS := ' PAGADO=0';
    MI_CONDICION := 'CODIGO = '''||UN_CODIGOPRED||''' 
                 AND PREANO = '||UN_PREANO;
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => 'IP_FACTURADOS'
                                          ,UN_ACCION    => 'M'
                                          ,UN_CAMPOS    => MI_CAMPOS 
                                          ,UN_CONDICION => MI_CONDICION);
  END IF;
  MI_STRSQL := ' SELECT NVL(MAX(CONSECT),0)+1 AS CONSECTF
                 FROM IP_AUTOAVALUO ';
  EXECUTE IMMEDIATE MI_STRSQL INTO MI_CONSECUTIVO;

  OPEN MI_VALIDA;
  FETCH MI_VALIDA INTO MI_CONSEC, MI_FECHA_CREA_REGISTRO;

  IF (MI_VALIDA%notfound) THEN
    IF (PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                              ,UN_NOMBRE    => 'GENERA CONSECUTIVO DE RADICADO AUTOAVALUO'
                              ,UN_MODULO    => UN_MODULO
                              ,UN_FECHA_PAR => SYSDATE)) != 'NO' THEN
      MI_CONSECUTIVORAD := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA     => 'IP_AUTOAVALUO'
                                                            ,UN_CRITERIO => ''
                                                            ,UN_CAMPO    => 'NUMERO_RADICACION'
                                                            ,UN_INICIAL  => PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                                                                                  ,UN_NOMBRE    => 'CONSECUTIVO INICIAL DE RADICADO AUTOAVALUO'
                                                                                                  ,UN_MODULO    => UN_MODULO
                                                                                                  ,UN_FECHA_PAR => SYSDATE));
    ELSE
      MI_CONSECUTIVORAD := UN_CONSECUTIVORAD;
    END IF;

    MI_CAMPOS := 'CODPRE
                 ,NUMERO_ORDEN
                 ,PAGO_ANO
                 ,NUMERO_RADICACION
                 ,AVALUO_ANT
                 ,AUTOAVALUO
                 ,USUARIO_CREA_REGISTRO
                 ,FECHA_CREA_REGISTRO
                 ,HORA_CREA_REGISTRO
                 ,TIPO_DECLA
                 ,CONSECT
                 ,TRPCOD
                 ,TRPCOD_ANT
                 ,COMPANIA';
    MI_VALORES := ''''||UN_CODIGOPRED||'''
                  ,''001''
                  ,'||UN_ANO||'
                  ,'''||MI_CONSECUTIVORAD||'''
                  ,'||UN_AVALUOIGAG||'
                  ,'||UN_AVALUO||'
                  ,'''||UN_USUARIO||''''||'
                  ,SYSDATE
                  ,SYSDATE
                  ,'||'''I''
                  ,'||--MI_CONSECUTIVO
                  '1'||'
                  ,'''||UN_TRPCOD||'''
                  ,'||''''||UN_TRPCOD_ANT||'''
                  ,'''||UN_COMPANIA||'''';
    MI_AFECTADOS := PCK_DATOS.FC_ACME(UN_TABLA    => 'IP_AUTOAVALUO'
                                      ,UN_ACCION  => 'I'
                                      ,UN_CAMPOS  => MI_CAMPOS 
                                      ,UN_VALORES => MI_VALORES );

    IF MI_AFECTADOS != 0 AND PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                                   ,UN_NOMBRE    => 'GENERA CONSECUTIVO DE RADICADO AUTOAVALUO'
                                                   ,UN_MODULO    => UN_MODULO
                                                   ,UN_FECHA_PAR => SYSDATE) != 'NO' THEN
      MI_CAMPOS := ' VALOR = '''||MI_CONSECUTIVORAD||'''';
      MI_CONDICION := ' NOMBRE = ''CONSECUTIVO INICIAL DE RADICADO AUTOAVALUO''';
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => 'PARAMETRO'
                                            ,UN_ACCION    => 'M'
                                            ,UN_CAMPOS    => MI_CAMPOS 
                                            ,UN_CONDICION => MI_CONDICION);
    END IF;
    MI_VLRRETORNO := MI_CONSECUTIVORAD;
    /*Autoavalúo registrado correctamente, por favor realice nuevamente el cálculo*/
  ELSE
    MI_CAMPOS := 'AVALUOANT              = '||UN_AVALUOIGAG||'
                  ,AUTOAVALUO            = '||UN_AVALUO||'
                  ,USUARIO_CREA_REGISTRO = '''||UN_USUARIO||'''
                  ,FECHA_CREA_REGISTRO   = TO_CHAR(SYSDATE, "dd/MM/yyyy")
                  ,HORA_CREA_REGISTRO    = TO_CHAR(SYSDATE, "dd/MM/yyyy")
                  ,TIPO_DECLA            = "I"
                  ,TRPCOD='''||UN_TRPCOD||'''
                  ,TRPCOD_ANT='''||UN_TRPCOD_ANT||'''';
    MI_CONDICION := 'CODPRE                = "'||UN_CODIGOPRED||'"
                     AND PAGO_ANO          = '||UN_ANO||'
                     AND NUMERO_RADICACION = "'||MI_CONSECUTIVORAD||'"
                     AND CONSECT           = '||MI_CONSEC;
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => 'IP_AUTOAVALUO'
                                          ,UN_ACCION    => 'M'
                                          ,UN_CAMPOS    => MI_CAMPOS 
                                          ,UN_CONDICION => MI_CONDICION);
  END IF;

  RETURN MI_VLRRETORNO;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
  END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
            PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD =>SQLCODE
            ,UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_AUSUBAUTOAVALUO
         );

END FC_AUSUBAUTOAVALUO;

--8

FUNCTION FC_BUSUBAUTOAVALUO
  (
/*  MODIFIER          : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED     : 11/01/2017
    DESCRIPTION       : Se cambió el estándar de codificación y se agrego manejo de excepciones. 
    @NAME:  consultarAbonoEnRecibosDePago
    @METHOD:  GET   
*/    
    UN_COMPANIA         IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_CODIGOPRED       IN  PCK_SUBTIPOS.TI_CODPREDIO,
    UN_VIGENCIA         IN  PCK_SUBTIPOS.TI_ENTERO
  )
RETURN NUMBER
AS
  MI_VLRRETORNO             VARCHAR2(4000 CHAR);
  MI_X                      PCK_SUBTIPOS.TI_ENTERO;
  MI_STRSQL                 PCK_SUBTIPOS.TI_STRSQL; 
  MI_PAGADO                 PCK_SUBTIPOS.TI_DOBLE;
  MI_ANULADO                PCK_SUBTIPOS.TI_DOBLE;
  MI_DOCNUM                 PCK_SUBTIPOS.TI_DOCNUM;
  MI_PRECOD                 PCK_SUBTIPOS.TI_CODPREDIO;
  MI_PREANO                 PCK_SUBTIPOS.TI_ANIO;
BEGIN
  MI_VLRRETORNO := '0';
  MI_STRSQL := 'SELECT COUNT(''X'') AS X 
                FROM   IP_FACTURADOS
                WHERE  CODIGO    = :1
                  AND  FINANCIAR = -1
                  AND  PREANO    = :2';
  EXECUTE IMMEDIATE MI_STRSQL INTO MI_X USING UN_CODIGOPRED, UN_VIGENCIA;
  IF (MI_X > 0) THEN
    /*El predio presenta un acuerdo de pago, no se puede continuar con el proceso*/
    MI_VLRRETORNO := '-1';
  END IF;

  IF MI_VLRRETORNO != '-1' THEN 
  BEGIN
    MI_STRSQL := ' SELECT F.PAGADO
                          ,R.ANULADO 
                          ,R.DOCNUM 
                          ,R.PRECOD
                          ,R.PREANO
                   FROM   IP_FACTURADOSABONOS F 
                    INNER JOIN IP_RECIBOS_DE_PAGO R 
                       ON (F.CODIGO = R.PRECOD)
                      AND (F.DOCNUM = R.DOCNUM)
                   WHERE  F.CODIGO = '''||UN_CODIGOPRED||''' 
                     AND  F.PREANO = '||UN_VIGENCIA;
    EXECUTE IMMEDIATE MI_STRSQL INTO MI_PAGADO, MI_ANULADO, MI_DOCNUM, MI_PRECOD, MI_PREANO;
  EXCEPTION WHEN NO_DATA_FOUND THEN
    MI_PAGADO := NULL;
  END;

    IF (MI_PAGADO IS NOT NULL) THEN
          IF NVL(MI_PAGADO, 0) != 0 Then
            /*El predio presenta pago de un Abono para esta vigencia, no se puede continuar con el proceso*/
            MI_VLRRETORNO := '-2';
          ELSE
            IF NVL(MI_ANULADO, 0) = 0 THEN
              /*If MsgBox("Se encuentra un recibo de pago creado como pago por abono,¿Desea anularlo?", vbYesNo + vbQuestion, "Sysman Software") = vbYes Then*/
              /*db.Execute "UPDATE RECIBOS_DE_PAGO SET ANULADO=-1 WHERE  DOCNUM='" !DOCNUM & "' AND PRECOD='"  & "'"*/
              MI_VLRRETORNO := MI_DOCNUM;
            END IF;
          END IF;
    END IF;
  END IF;

  RETURN MI_VLRRETORNO;
END FC_BUSUBAUTOAVALUO;

--9



-- 10

FUNCTION FC_CUOTASRECIBOS
    /*
    NAME              : FC_CUOTASRECIBOS  --> EN ACCESS CuotasRecibos
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : AURA LILIANA MONROY GARCÍA
    DATE MIGRADOR     : 29/08/2016
    TIME              : 11:35 AM
    SOURCE MODULE     : PredialP2016.05.06
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED     : 11/01/2017
    TIME              : 4:42 PM
    DESCRIPTION       : Retorna el numero de cuotas que posee un usuario / Se cambió el estándar de codificación y se agregó manejo de excepciones. 
    @NAME:  consultarNumeroDeCuotasPorUsuario
    @METHOD:  GET     
    */
(
	UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_ACUERDO  IN IP_RECIBOS_DE_PAGO.ACUERDO%TYPE,
	UN_PREDIO   IN PCK_SUBTIPOS.TI_CODPREDIO,
	UN_RECIBO   IN PCK_SUBTIPOS.TI_DOCNUM
)
RETURN VARCHAR2
AS
	    MI_ERROR_FUN    PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 10;
	    MI_RETORNO      VARCHAR2(50 CHAR);
BEGIN
 BEGIN
	SELECT
	  CASE
        WHEN MIN(NCUOTA_ACUERDO) IS NULL
        THEN 'No Existe'
        WHEN MIN(NCUOTA_ACUERDO) = MAX(NCUOTA_ACUERDO)
        THEN TO_CHAR(MIN(NCUOTA_ACUERDO))
        WHEN MIN(NCUOTA_ACUERDO) <> MAX(NCUOTA_ACUERDO)
        THEN MIN(NCUOTA_ACUERDO) ||' - '|| MAX(NCUOTA_ACUERDO)
	  END
	INTO  MI_RETORNO
	FROM  IP_RECIBOS_DE_PAGO
	WHERE COMPANIA = UN_COMPANIA
    AND ACUERDO  = UN_ACUERDO
	  AND PRECOD   = UN_PREDIO
	  AND DOCNUM   = UN_RECIBO;

	RETURN MI_RETORNO;

     EXCEPTION WHEN NO_DATA_FOUND THEN 
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
 END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
            PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD =>SQLCODE
            ,UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_ACUERDOVIG
         );
END FC_CUOTASRECIBOS;

--11

PROCEDURE PR_APLICACAMBIO_PROYECCION
/*
    NAME              : PR_APLICACAMBIO_PROYECCION --> EN ACCESS APLICACAMBIOPROYECCION
    AUTHORS           : SANDRA MILENA DAZA LEGUIZAMÓN
    DATE              : 01/11/2016
    TIME              : 08:20 AM
    MODIFIER          : SANDRA MILENA DAZA LEGUIZAMON / ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED     : 03/11/2016 / 11/01/2017 
    TIME              : 05:40 PM
    DESCRIPTION       : Funcion que recibo las proyecciones con ajustes por parámetro 
                        , realiza los calculos a partir de los valores facturados y realiza
                        la actualización de estos y almacena el histórico correspondiente
    @name             : aplicarCambioProyeccion. 
    / Se cambió el estándar de codificación y se agregó manejo de excepciones. 
    @method           : POST
    @parameters       : UN_COMPANIA      entidad a la cual pertenece el predio evaluado
                        UN_CODPREDIO     código catastral a evaluar. 
                        UN_ANOINICIAL    vigencia a partir de la cual se tomará los valores facturados
                        UN_PROY_AJUSTE   estructura con los valores proyectados, en access se usaba un parámetro  
                                        ajuste para indicar si trabajaba con la primera o segunda proyeccion, 
                                        en este caso es este parametro se debe enviar la proyección que corresponda. 
  */
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CODPREDIO    IN PCK_SUBTIPOS.TI_CODPREDIO,
    UN_ANOINICIAL   IN PCK_SUBTIPOS.TI_ANIO,
    UN_PROY_AJUSTE  IN TPROYECCION
  )
AS
  MI_ERROR_FUN          PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 11;
  MI_PORCENTAJE_CAMBIO  PCK_SUBTIPOS.TI_PORCENTAJE := 0;
  MI_CONTADOR           PCK_SUBTIPOS.TI_ENTERO := 1;
  MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
BEGIN
  <<CARGAR_PROYAJUSTE1>>
  BEGIN 
    FOR RSFAC IN (
                  SELECT PREANO
                         ,C1 + C3 TOTAL_IMPUESTO
                         ,TOTAL ,C1 ,C2 ,C3 ,C4 ,C5 
                         ,C6 ,C7 ,C8 ,C9 ,C10
                         ,C11 ,C12 ,C13 ,C14 ,C15 
                         ,C16 ,C17 ,C18 ,C19 ,C20
                         ,C1_CPY
                         ,C3_CPY
                         ,PAGADO
                  FROM    IP_FACTURADOS
                  WHERE   COMPANIA = UN_COMPANIA
                  AND     CODIGO   = UN_CODPREDIO
                  AND     PREANO   = UN_ANOINICIAL
                  ORDER BY PREANO ASC
                 )
                 LOOP
      BEGIN
          MI_PORCENTAJE_CAMBIO := ABS(1- (((UN_PROY_AJUSTE(MI_CONTADOR).IMPUESTO * 100) / RSFAC.TOTAL_IMPUESTO)/100));
        EXCEPTION WHEN ZERO_DIVIDE THEN
          MI_PORCENTAJE_CAMBIO := 0;
      END;
   BEGIN   
      BEGIN
        IF RSFAC.PAGADO = 0 THEN 
          MI_VALORES := '  C1 = ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => RSFAC.C1 - RSFAC.C1 * MI_PORCENTAJE_CAMBIO
                                                             ,UN_PRECISION => 0) ||
                        ', C2 = ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => RSFAC.C2 - RSFAC.C2 * MI_PORCENTAJE_CAMBIO
                                                             ,UN_PRECISION =>   0) ||
                        ', C3 = ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => RSFAC.C3 - RSFAC.C3 * MI_PORCENTAJE_CAMBIO
                                                             ,UN_PRECISION => 0) ||
                        ', C4 = ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => RSFAC.C4 - RSFAC.C4 * MI_PORCENTAJE_CAMBIO
                                                             ,UN_PRECISION => 0) ||
                        ', C13 = ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => RSFAC.C13 - RSFAC.C13 * MI_PORCENTAJE_CAMBIO
                                                              ,UN_PRECISION => 0) ||
                        ', C1_CPY = ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => RSFAC.C1 - RSFAC.C1 * MI_PORCENTAJE_CAMBIO
                                                                 ,UN_PRECISION => 0) ||
                        ', C3_CPY = ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => RSFAC.C3 - RSFAC.C3 * MI_PORCENTAJE_CAMBIO
                                                                 ,UN_PRECISION => 0) ;
          MI_CONDICION := ' COMPANIA = ''' || UN_COMPANIA ||
                       ''', CODIGO   = ''' || UN_CODPREDIO || 
                       ''', PREANO   = ' || RSFAC.PREANO ;
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                            UN_TABLA       => 'IP_FACTURADOS'
                            ,UN_ACCION     => 'M'
                            ,UN_CAMPOS     =>  MI_VALORES
                            ,UN_CONDICION  =>  MI_CONDICION);
          MI_VALORES := '  TOTAL = C1 + C2 + C3 + C4 + C13 + C14 + C15 + C16 + C17 + C18 + C19 + C20';
          MI_CONDICION := ' COMPANIA = ''' || UN_COMPANIA ||
                       ''', CODIGO   = ''' || UN_CODPREDIO || 
                       ''', PREANO   = ' || RSFAC.PREANO ;
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                            UN_TABLA      => 'IP_FACTURADOS', 
                            UN_ACCION     => 'M', 
                            UN_CAMPOS     =>  MI_VALORES, 
                            UN_CONDICION  =>  MI_CONDICION);              
        END IF;

        IF RSFAC.PREANO > 2015 THEN
          MI_VALORES := '  C5 = ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => RSFAC.C5 - RSFAC.C5 * MI_PORCENTAJE_CAMBIO
                                                             ,UN_PRECISION => 0) ||
                        ', C6 = ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => RSFAC.C6 - RSFAC.C6 * MI_PORCENTAJE_CAMBIO
                                                             ,UN_PRECISION => 0) ||
                        ', C7 = ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => RSFAC.C7 - RSFAC.C7 * MI_PORCENTAJE_CAMBIO
                                                             ,UN_PRECISION => 0) ||
                        ', C8 = ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => RSFAC.C8 - RSFAC.C8 * MI_PORCENTAJE_CAMBIO
                                                             ,UN_PRECISION => 0) ||
                        ', C9 = ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => RSFAC.C9 - RSFAC.C9 * MI_PORCENTAJE_CAMBIO
                                                             ,UN_PRECISION => 0) ||
                        ', C10 = ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => RSFAC.C10 - RSFAC.C10 * MI_PORCENTAJE_CAMBIO
                                                             ,UN_PRECISION => 0) ||
                        ', C11 = ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => RSFAC.C11 - RSFAC.C11 * MI_PORCENTAJE_CAMBIO
                                                              ,UN_PRECISION => 0) ||
                        ', C12 = ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => RSFAC.C12 - RSFAC.C12 * MI_PORCENTAJE_CAMBIO
                                                              ,UN_PRECISION => 0) ||
                        ', C13 = ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => RSFAC.C13 - RSFAC.C13 * MI_PORCENTAJE_CAMBIO
                                                              ,UN_PRECISION => 0) ||
                        ', C14 = ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => RSFAC.C14 - RSFAC.C14 * MI_PORCENTAJE_CAMBIO
                                                              ,UN_PRECISION => 0) ||
                        ', C15 = ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => RSFAC.C15 - RSFAC.C15 * MI_PORCENTAJE_CAMBIO
                                                              ,UN_PRECISION => 0) ||
                        ', C16 = ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => RSFAC.C16 - RSFAC.C16 * MI_PORCENTAJE_CAMBIO
                                                              ,UN_PRECISION => 0) ||
                        ', C17 = ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => RSFAC.C17 - RSFAC.C17 * MI_PORCENTAJE_CAMBIO
                                                              ,UN_PRECISION => 0) ||
                        ', C19 = ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => RSFAC.C19 - RSFAC.C19 * MI_PORCENTAJE_CAMBIO
                                                              ,UN_PRECISION => 0) ||
                        ', C20 = ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => RSFAC.C20 - RSFAC.C20 * MI_PORCENTAJE_CAMBIO
                                                              ,UN_PRECISION => 0) ;
          MI_CONDICION := ' COMPANIA = ''' || UN_COMPANIA ||
                       ''', CODIGO   = ''' || UN_CODPREDIO || 
                       ''', PREANO   = ' || RSFAC.PREANO ;
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                            UN_TABLA       => 'IP_FACTURADOS'
                            ,UN_ACCION     => 'M'
                            ,UN_CAMPOS     =>  MI_VALORES
                            ,UN_CONDICION  =>  MI_CONDICION);      
        END IF;

        EXCEPTION  WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL; 

      END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD     => SQLCODE
              ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_PROY_1
            );
   END;   
      MI_CONTADOR := MI_CONTADOR + 1;
    END LOOP CARGAR_PROYAJUSTE1;
  END ;

  BEGIN
   BEGIN
    MI_CONDICION :=  ' COMPANIA = ''' || UN_COMPANIA ||
                  ''', CODIGO   = ''' || UN_CODPREDIO || 
                  ''', PREANO   = ' || PCK_SYSMAN_UTL.FC_ANIO(UN_FECHA => SYSDATE) ;
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                              UN_TABLA       => 'IP_HISTORICO_PROYECCION'
                              ,UN_ACCION     => 'E'
                              ,UN_CONDICION  =>  MI_CONDICION); 

      EXCEPTION  WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                        

   END;   
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD     => SQLCODE
              ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_PROY_2
            );
  END ;
  BEGIN
   BEGIN
    MI_CAMPOS := 'COMPANIA
                  ,CODIGO 
                  ,ANO
                  ,TOTIPU2014
                  ,TOTIPU2015
                  ,TOTIPU2016
                  ,TOTIPU2017
                  ,TOTIPU2018
                  ,TOTIPU2019
                  ,TOTIPU2020
                  ,TOTIPU2021
                  ,TOTIPU2022
                  ,TOTIPU2023
                  ,TOTIPU2024';

      MI_VALORES := '''' || UN_COMPANIA ||
                    ''', ''' || UN_CODPREDIO ||
                    ''', ' || PCK_SYSMAN_UTL.FC_ANIO(UN_FECHA => SYSDATE) ||
                    ' , ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => UN_PROY_AJUSTE(0).IMPUESTO
                                                     ,UN_PRECISION => 0) ||
                    ' , ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => UN_PROY_AJUSTE(1).IMPUESTO
                                                     ,UN_PRECISION => 0) ||
                    ' , ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => UN_PROY_AJUSTE(2).IMPUESTO
                                                     ,UN_PRECISION => 0) ||
                    ' , ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => UN_PROY_AJUSTE(3).IMPUESTO
                                                     ,UN_PRECISION => 0) ||
                    ' , ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => UN_PROY_AJUSTE(4).IMPUESTO
                                                     ,UN_PRECISION => 0) ||
                    ' , ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => UN_PROY_AJUSTE(5).IMPUESTO
                                                     ,UN_PRECISION => 0) ||
                    ' , ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => UN_PROY_AJUSTE(6).IMPUESTO
                                                     ,UN_PRECISION => 0) ||
                    ' , ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => UN_PROY_AJUSTE(7).IMPUESTO
                                                     ,UN_PRECISION => 0) ||
                    ' , ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => UN_PROY_AJUSTE(8).IMPUESTO
                                                     ,UN_PRECISION => 0) ||
                    ' , ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => UN_PROY_AJUSTE(9).IMPUESTO
                                                     ,UN_PRECISION => 0) ||
                    ' , ' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => UN_PROY_AJUSTE(10).IMPUESTO
                                                     ,UN_PRECISION => 0) ;

         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'IP_HISTORICO_PROYECCION'
                                                ,UN_ACCION  => 'I'
                                                ,UN_CAMPOS  => MI_CAMPOS
                                                ,UN_VALORES => MI_VALORES);

         EXCEPTION  WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                       

   END;                                        
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD     => SQLCODE
              ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_PROY_3
            );
  END ;  
  -- EN LAS LINEAS SIGUIENTES SE CONSULTABA LOS FACTURADOS MAYORES AL PARAMETRO UN_ANOINICIAL Y SE CARGABA
  -- EL VECTOR FACTURADOS , ESTE SE OMITE DEBIDO A QUE NO ES POSIBLE MANEJAR VARIABLES GLOBLALOS. 
  -- LOS DATOS DEBERAN SER CONSULTADO DONDE SEAN REQUERIDOS. 
END PR_APLICACAMBIO_PROYECCION;

--12

PROCEDURE PR_SEGUNDOAJUSTE_PROYECCION
/*
    NAME              : PR_SEGUNDOAJUSTE_PROYECCION --> EN ACCESS SEGUNDOAJUSTEPROYECCION
    AUTHORS           : SANDRA MILENA DAZA LEGUIZAMÓN
    DATE              : 01/11/2016
    TIME              : 09:20 AM
    MODIFIER          : SANDRA MILENA DAZA LEGUIZAMÓN / ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED     : 04/11/2016 / 11/01/2017 
    TIME              : 10:30 AM
    DESCRIPTION       : Función que realiza el calculo del segundo ajuste de la proyección. / Se cambió el estándar de codificación y se agregó manejo de excepciones.
    @name             : calcularSegundoAjusteProyeccion
    @method           : POST
    @parameters       : UN_COMPANIA             entidad a la cual pertenece el predio evaluado
                        UN_CODPREDIO            código catastral a evaluar. 
                        UN_ANOINICIAL           vigencia a partir de la cual se tomará los valores facturados
                        UN_PROY_AJUSTEUNO       estructura con los valores proyectados, tipo de ajuste 1
                        UN_INDLOTE              si esta en -1 indica que el proceso se esta ejecutando en lotes
                        UN_INDVERIFICAR_AVALUO  indicador que si esta en -1, es porque el usuario a seleccionado
                                                verificar los avaluos del IGAC para ejecutar el proceso.
  */
  (
    UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_CODPREDIO            IN PCK_SUBTIPOS.TI_CODPREDIO,
    UN_ANOINICIAL           IN PCK_SUBTIPOS.TI_ANIO,
    UN_PROY_AJUSTEUNO       IN TPROYECCION, 
    UN_INDLOTES             IN PCK_SUBTIPOS.TI_LOGICO,
    UN_INDVERIFICAR_AVALUO  IN PCK_SUBTIPOS.TI_LOGICO
  )
AS
  MI_MEDIAGEOMETRICA    PCK_SUBTIPOS.TI_ENTERO := 1;
  MI_PROY_AJUSTEDOS     TPROYECCION;
BEGIN
 BEGIN
  BEGIN
<<CALCULO_MEDIA_GEOMETRICA>>
  FOR I IN 1 .. UN_PROY_AJUSTEUNO.COUNT LOOP
    MI_MEDIAGEOMETRICA := MI_MEDIAGEOMETRICA * (1 + UN_PROY_AJUSTEUNO(I).VARIACION);
  END LOOP CALCULO_MEDIA_GEOMETRICA;

  MI_MEDIAGEOMETRICA := POWER(MI_MEDIAGEOMETRICA , (1/10)-1);

  MI_PROY_AJUSTEDOS(0).ANO := UN_PROY_AJUSTEUNO(0).ANO;
  MI_PROY_AJUSTEDOS(0).IMPUESTO := UN_PROY_AJUSTEUNO(0).IMPUESTO;
  MI_PROY_AJUSTEDOS(0).VARIACION := 0;

  --EN LA VERSION DE ACCESS RECORRE HASTA 10, SI NO HAY DATOS EN ALGUNA POSICIÓN SE GENERAR ERROR Y NO SE 
  --CONTROLA, EN ESTA VERSIÓN SE RECORRE HASTA LA POSICIÓN QUE TENGA DATOS 

      EXCEPTION WHEN NO_DATA_FOUND THEN 
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;    
  END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_SEGUNDOAJUSTE
      );
 END;
BEGIN
  BEGIN
  <<AJUSTES_ANO_IMPUESTO_VARIACION>>
  FOR I IN 1 .. UN_PROY_AJUSTEUNO.COUNT LOOP
    MI_PROY_AJUSTEDOS(I).ANO := UN_PROY_AJUSTEUNO(I).ANO;
    MI_PROY_AJUSTEDOS(I).IMPUESTO := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_PROY_AJUSTEDOS(I-1).IMPUESTO) * (1 + MI_MEDIAGEOMETRICA)
                                                             ,UN_PRECISION => 1);
    MI_PROY_AJUSTEDOS(I).VARIACION := (MI_PROY_AJUSTEDOS(I).IMPUESTO / PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => MI_PROY_AJUSTEDOS(I-1).IMPUESTO = 0
                                                                                             ,UN_SI       => 1
                                                                                             ,UN_NO       => MI_PROY_AJUSTEDOS(I-1).IMPUESTO))-1;
  END LOOP AJUSTES_ANO_IMPUESTO_VARIACION;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;  
  END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_SEGUNDOAJUSTE
      );
 END;

 BEGIN
  BEGIN 
  <<CAMBIO_PROYECCION>>
  FOR I IN 0 .. MI_PROY_AJUSTEDOS.COUNT LOOP  
    IF MI_PROY_AJUSTEDOS(I).VARIACION > 1 THEN  
      IF UN_INDLOTES <> 0 THEN
        IF UN_INDVERIFICAR_AVALUO <> 0 THEN
          PR_APLICACAMBIO_PROYECCION (
              UN_COMPANIA     => UN_COMPANIA
              ,UN_CODPREDIO   => UN_CODPREDIO
              ,UN_ANOINICIAL  => UN_ANOINICIAL
              ,UN_PROY_AJUSTE => MI_PROY_AJUSTEDOS
            );          
        ELSE
          RETURN;
        END IF;
      END IF;
    END IF;
  END LOOP CAMBIO_PROYECCION;

  EXCEPTION WHEN NO_DATA_FOUND THEN 
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;  
  END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_SEGUNDOAJUSTE
      );
 END;

  PR_APLICACAMBIO_PROYECCION (
              UN_COMPANIA     => UN_COMPANIA
              ,UN_CODPREDIO   => UN_CODPREDIO
              ,UN_ANOINICIAL  => UN_ANOINICIAL
              ,UN_PROY_AJUSTE => MI_PROY_AJUSTEDOS
              );    

END PR_SEGUNDOAJUSTE_PROYECCION;  

--13

PROCEDURE PR_PRIMERAJUSTE_PROYECCION 
/*
    NAME              : PR_PRIMERAJUSTE_PROYECCION --> EN ACCESS PRIMERAJUSTEPROYECCION
    AUTHORS           : SANDRA MILENA DAZA LEGUIZAMÓN
    DATE              : 01/11/2016
    TIME              : 10:00 AM
    MODIFIER          : SANDRA MILENA DAZA LEGUIZAMON / ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED     : 04/11/2016 / 12/01/2017
    TIME              : 11:15 AM
    DESCRIPTION       : Función que realiza el calculo del primer ajuste de la proyección / Se cambió el estándar de codificación y se agregó manejo de excepciones.
    @name             : calcularPrimerAjusteProyeccion
    @method           : POST
    @parameters       : UN_COMPANIA             entidad a la cual pertenece el predio evaluado
                        UN_CODPREDIO            código catastral a evaluar. 
                        UN_ANOINICIAL           vigencia a partir de la cual se tomará los valores facturados
                        UN_INDLOTE              si esta en -1 indica que el proceso se esta ejecutando en lotes
                        UN_INDVERIFICAR_AVALUO  indicador que si esta en -1, es porque el usuario a seleccionado
                                                verificar los avaluos del IGAC para ejecutar el proceso.
                        UN_PROY_INICIAL         estructura con los valores proyectados iniciales
  */
  (
    UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_CODPREDIO            IN PCK_SUBTIPOS.TI_CODPREDIO,
    UN_ANOINICIAL           IN PCK_SUBTIPOS.TI_ANIO,
    UN_INDLOTES             IN PCK_SUBTIPOS.TI_LOGICO,
    UN_INDVERIFICAR_AVALUO  IN PCK_SUBTIPOS.TI_LOGICO,
    UN_PROY_INICIAL         IN TPROYECCION
  )
AS
  MI_PROY_AJUSTEUNO     TPROYECCION;
  MI_FACTOREXP          TFACTOR_EXPONENCIAL;
  MI_AJUSTE             PCK_SUBTIPOS.TI_ENTERO := 1;
  MI_PROY_INICIAL       TPROYECCION;
BEGIN
  MI_PROY_AJUSTEUNO(0).ANO := UN_PROY_INICIAL(0).ANO;
  MI_PROY_AJUSTEUNO(0).IMPUESTO := UN_PROY_INICIAL(0).IMPUESTO;
  MI_PROY_AJUSTEUNO(0).VARIACION := 0;

  --EN ACCESS SE RECORRE HASTA 10, SE MODIFICA PARA QUE SE RECORRA MIENTRAS UN_PROY_INICIAL TENGA DATOS

BEGIN
 BEGIN
 <<VALIDACION_DATOS>>
  FOR I IN 1 .. UN_PROY_INICIAL.COUNT LOOP
    MI_PROY_AJUSTEUNO(I).ANO := UN_PROY_INICIAL(I).ANO;
    MI_PROY_AJUSTEUNO(I).IMPUESTO := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => (UN_PROY_INICIAL(10).IMPUESTO - UN_PROY_INICIAL(0).IMPUESTO) * 
                                                                               MI_FACTOREXP(I-1).FACTOR + MI_PROY_AJUSTEUNO(I -1).IMPUESTO
                                                             ,UN_PRECISION => 1);
    MI_PROY_AJUSTEUNO(I).VARIACION := (MI_PROY_AJUSTEUNO(I).IMPUESTO / PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => MI_PROY_AJUSTEUNO(I-1).IMPUESTO = 0
                                                                                             ,UN_SI       => 1
                                                                                             ,UN_NO       => MI_PROY_AJUSTEUNO(I-1).IMPUESTO)) -1 ;
  END LOOP VALIDACION_DATOS;

  /*IF MI_PROY_AJUSTEUNO.COUNT = 0 THEN 

  END IF;*/
   EXCEPTION WHEN NO_DATA_FOUND THEN 
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL; 
 END;
     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_PRIMERAJUSTE
      );
END;  

BEGIN
 BEGIN
  <<SEGUNDO_AJUSTE_PROYECCION>>
  FOR I IN 1 .. MI_PROY_AJUSTEUNO.COUNT LOOP
    IF MI_PROY_AJUSTEUNO(I).VARIACION > 1 THEN
      PR_SEGUNDOAJUSTE_PROYECCION (
          UN_COMPANIA              => UN_COMPANIA
          ,UN_CODPREDIO            => UN_CODPREDIO
          ,UN_ANOINICIAL           => UN_ANOINICIAL 
          ,UN_PROY_AJUSTEUNO       => MI_PROY_AJUSTEUNO
          ,UN_INDLOTES             => UN_INDLOTES
          ,UN_INDVERIFICAR_AVALUO  => UN_INDVERIFICAR_AVALUO 
        );
    END IF;      
  END LOOP SEGUNDO_AJUSTE_PROYECCION;
   EXCEPTION WHEN NO_DATA_FOUND THEN 
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL; 
 END;
     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_PRIMERAJUSTE
      );
END;
  PR_APLICACAMBIO_PROYECCION (
        UN_COMPANIA      => UN_COMPANIA 
        ,UN_CODPREDIO    => UN_CODPREDIO 
        ,UN_ANOINICIAL   => UN_ANOINICIAL 
        ,UN_PROY_AJUSTE  => MI_PROY_AJUSTEUNO);  
END PR_PRIMERAJUSTE_PROYECCION;

--14

FUNCTION FC_CARGAR_FACTOREXP 
/*
    NAME              : FC_CARGAR_FACTOREXP --> EN ACCESS LLENAFACTOREXP
    AUTHORS           : SANDRA MILENA DAZA LEGUIZAMÓN
    DATE              : 01/11/2016
    TIME              : 02:30 PM
    MODIFIER          : SANDRA MILENA DAZA LEGUIZAMÓN / ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED     : 04/11/2016 / 12/01/2017
    TIME              : 12:40 PM
    DESCRIPTION       : Función que realiza la carga de una estructura con los datos de la tabla. / Se cambió el estándar de codificación y se agregó manejo de excepciones. 
    TIME              : 08:15 AM
                        IP_FACTOR_EXPONENCIAL
    @name             : cargarFactorExponencial
    @method           : GET
    @parameters       : UN_COMPANIA             entidad a la cual pertenece el predio evaluado                        
  */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA
  )
RETURN TFACTOR_EXPONENCIAL
AS
  MI_FACTOREXP      TFACTOR_EXPONENCIAL;
  MI_CONTADOR       PCK_SUBTIPOS.TI_ENTERO := 0;
BEGIN
 BEGIN
<<CARGA_ESTRUCTURA>>
  FOR RSFACTOR IN (
                    SELECT ANO     
                           ,FACTOR
                           ,INCREMENTO
                    FROM   IP_FACTOR_EXPONENCIAL
                    WHERE     COMPANIA = UN_COMPANIA
                    ORDER BY  ANO ASC
                  )
                  LOOP
    MI_FACTOREXP(MI_CONTADOR).ANO        := RSFACTOR.ANO;
    MI_FACTOREXP(MI_CONTADOR).FACTOR     := RSFACTOR.FACTOR;
    MI_FACTOREXP(MI_CONTADOR).INCREMENTO := RSFACTOR.INCREMENTO;
    MI_CONTADOR                          := MI_CONTADOR + 1;
  END LOOP CARGA_ESTRUCTURA;              
  IF MI_FACTOREXP.COUNT = 0 THEN
    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
  END IF;
 END;
   RETURN MI_FACTOREXP;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_FACTOREXP
      );  

END FC_CARGAR_FACTOREXP;

--15

FUNCTION FC_CALCULAR_PROYECCIONINICIAL 
/*
    NAME              : FC_CALCULAR_PROYECCIONINICIAL --> EN ACCESS CALCULOPROYECCIONINICIAL
    AUTHORS           : SANDRA MILENA DAZA LEGUIZAMÓN
    DATE              : 01/11/2016
    TIME              : 02:46 PM
    MODIFIER          : SANDRA MILENA DAZA LEGUIZAMON / ELKIN GEOVANNY AMAYA SILVA 
    DATE MODIFIED     : 04/11/2016 / 12/01/2017
    TIME              : 02:30 PM / 09:00 AM
    DESCRIPTION       : Función que realiza la carga de una estructura con la proyección inicial. / Se cambió el estándar de codificación y se agregó manejo de excepciones.
    @name             : calcularProyecccionInicial
    @method           : GET
    @parameters       : UN_COMPANIA             entidad a la cual pertenece el predio evaluado  
                        UN_CODPREDIO            predio al cual se calculará la proyección inicial
                        UN_ANOINICIAL           vigencia desde la cual se inicia a tomar los datos de facturados
                        UN_ANOFINAL             vigencia desde la cual se finaliza la toma de datos de facturados
  */
  (
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CODPREDIO  IN PCK_SUBTIPOS.TI_CODPREDIO,
    UN_ANOINICIAL IN PCK_SUBTIPOS.TI_ANIO,
    UN_ANOFINAL   IN PCK_SUBTIPOS.TI_ANIO
  )
RETURN TPROYECCION
AS
  MI_PROY_INICIAL   TPROYECCION;
  MI_CONTADOR       PCK_SUBTIPOS.TI_ENTERO := 0;
  MI_FACTOREXP      TFACTOR_EXPONENCIAL;
BEGIN
 BEGIN
    MI_FACTOREXP := FC_CARGAR_FACTOREXP(UN_COMPANIA);  

  <<CARGA_PROYECCION_INICIAL>>

  FOR RSFAC IN (
                SELECT  PREANO
                        ,C1 + C3 TOTAL
                FROM    IP_FACTURADOS
                WHERE   COMPANIA = UN_COMPANIA
                AND     CODIGO   = UN_CODPREDIO
                AND     PREANO   BETWEEN UN_ANOINICIAL AND UN_ANOFINAL
                ORDER BY  PREANO
               )
               LOOP
    MI_PROY_INICIAL(MI_CONTADOR).ANO := RSFAC.PREANO;
    MI_PROY_INICIAL(MI_CONTADOR).IMPUESTO := RSFAC.TOTAL;
    MI_PROY_INICIAL(MI_CONTADOR).VARIACION := 0;
    MI_CONTADOR := MI_CONTADOR + 1;
  END LOOP CARGA_PROYECCION_INICIAL;

  --EN ACCESS SE RECORRE HASTA 10, EN ESTA VERSIÓN SE RECORRE HASTA QUE MI_FACTOREXP TENGA DATOS
  <<PROYECCION_INICIAL>>
  FOR I IN 3 .. MI_PROY_INICIAL.COUNT LOOP
    MI_PROY_INICIAL(I).ANO := MI_FACTOREXP(I-1).ANO;
    MI_PROY_INICIAL(I).IMPUESTO := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => (1 + MI_FACTOREXP(I-1).INCREMENTO) * MI_PROY_INICIAL(I - 1).IMPUESTO
                                                           ,UN_PRECISION => 1);
  END LOOP PROYECCION_INICIAL;
  IF MI_PROY_INICIAL.COUNT = 0 THEN
    RAISE PCK_EXCEPCIONES.EXC_PREDIAL; 
  END IF;

 END;  
  RETURN MI_PROY_INICIAL;

   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD     => SQLCODE
              ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_PROY_4
            );
END FC_CALCULAR_PROYECCIONINICIAL;

--16

PROCEDURE PR_IMPRESORAVIGENCIA 
/*
    NAME              : PR_IMPRESORAVIGENCIA --> EN ACCESS IMPRESORAVIGENCIA
    AUTHORS           : SANDRA MILENA DAZA LEGUIZAMÓN
    DATE              : 02/11/2016
    TIME              : 09:20 AM
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED     : 12/01/2017
    TIME              : 09:41 AM
    DESCRIPTION       : Procedimiento  que realiza la generación de la factura de vigencias. / Se cambió el estándar de codificación y se agregó manejo de excepciones. 
    @name             : generarFacturaVigencia
    @method           : POST
    @parameters       : UN_COMPANIA             entidad a la cual pertenece el predio evaluado  
                        UN_CODPREDIO            predio al cual se calculará la proyección inicial
                        UN_ANOINICIAL           vigencia desde la cual se inicia a tomar los datos de facturados
                        UN_ANOFINAL             vigencia desde la cual se finaliza la toma de datos de facturados
  */
  (
    UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CODPREDIO            IN PCK_SUBTIPOS.TI_CODPREDIO,
    UN_NUMORDEN             IN PCK_SUBTIPOS.TI_NUMORDEN,
    UN_MENORVIGENCIA        IN PCK_SUBTIPOS.TI_ENTERO,
    UN_MAYORVIGENCIA        IN PCK_SUBTIPOS.TI_ENTERO,
    UN_ANOCORTE             IN PCK_SUBTIPOS.TI_ANIO,
    UN_NOMBREPROPFACT       IN IP_USUARIOS_PREDIAL.NOMBRE%TYPE,
    UN_NITPROPFACT          IN IP_USUARIOS_PREDIAL.NIT%TYPE,
    UN_NUMORDENFACTURAR     IN PCK_SUBTIPOS.TI_NUMORDEN,
    UN_FECHALIMITE          IN DATE,
    UN_IMPRIMIR_TOTALCERO   IN PCK_SUBTIPOS.TI_LOGICO,
    UN_MODULO               IN PCK_SUBTIPOS.TI_ENTERO,
    UN_USUARIO              IN PCK_SUBTIPOS.TI_USUARIO
  )
AS
  MI_ERROR_FUN          PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 16;
  MI_CCONCEPTOS         PCK_PREDIAL_COM3.TYPE_CONCEPTOS;
  MI_STR                PCK_SUBTIPOS.TI_STRSQL;
  MI_RSFACTURADOS       SYS_REFCURSOR;
  MI_CPTO               PCK_PREDIAL_COM3.TYPE_VECTORNUM;
  MI_TOTAL              PCK_SUBTIPOS.TI_DOBLE := 0;
  MI_CANT_CPTOS         PCK_SUBTIPOS.TI_DOBLE := 20;
  MI_TIPOFACTURA        IP_NUMEROSDEFACTURA.TIPO%TYPE := 'N'; -- INDICA QUE SE TOMARA EL CONSECUTIVO FACTURACIÓN NORMAL
  MI_NUMRECIBO          IP_NUMEROSDEFACTURA.CONSECUTIVOREAL%TYPE;
  MI_DIGRECIBO          PCK_SUBTIPOS.TI_DOBLE;
  MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
  MI_EXISTE             VARCHAR2(1 CHAR);
  MI_CONTROLRECIBO      PCK_SUBTIPOS.TI_PARAMETRO;
  MI_FACTURARMULTIFECHA PCK_SUBTIPOS.TI_PARAMETRO;
  MI_CPTOREVISAR        VARCHAR2(100 CHAR);
BEGIN
-- EN LAS PRIMERAS LINEAS DE LA FUNCION EN ACCESS SE REALIZAN VALIDACIONES SEGUN PARAMETROS 
-- CON EL FIN DE REACALCULAR EL PREDIO, SE DEBE VERIFICAR EN QUE PUNTO SE DEBE REALIZAR DICHO
-- PROCESO. ESTE PROCEDIMIENTO SOLO SE OCUPARA DE GENERAR LA FACTURA Y SU RESPECTIVO REGISTRO
   --INICIALIZAR MI_CPTO
   <<INICIALIZAR_MI_CPTO>>
  FOR I IN 0 .. MI_CANT_CPTOS LOOP
    MI_CPTO(I) := 0;           
  END LOOP INICIALIZAR_MI_CPTO;
  BEGIN
   BEGIN 
    MI_CCONCEPTOS := PCK_PREDIAL_COM3.FC_CONFIGURARCONCEPTOS(
                                 UN_COMPANIA => UN_COMPANIA
                                 ,UN_ANOCORTE => UN_ANOCORTE
                                );
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;

   END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD     => SQLCODE
              ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_CONFCONCEPTOS
            );
  END;

  BEGIN
   BEGIN
   IF PCK_GENERALES.FC_NOMBRECOMPANIA (
                      UN_COMPANIA => UN_COMPANIA
                    ) = 'MUNICIPIO DE SOGAMOSO' THEN
    MI_STR := ' SELECT 
                  CASE WHEN PREANO = ' || UN_ANOCORTE || ' THEN C1 ELSE 0 END CC1 
                  ,CASE WHEN PREANO = ' || UN_ANOCORTE || ' THEN C2 ELSE 0 END CC2 
                  ,CASE WHEN PREANO = ' || UN_ANOCORTE || ' THEN C3 ELSE 0 END CC3 
                  ,CASE WHEN PREANO = ' || UN_ANOCORTE || ' THEN C4 ELSE 0 END CC4 
                  ,CASE WHEN PREANO = ' || UN_ANOCORTE || '- 1  THEN C1 ELSE 0 END CC5 
                  ,CASE WHEN PREANO = ' || UN_ANOCORTE || '- 1  THEN C2 ELSE 0 END CC6 
                  ,CASE WHEN PREANO = ' || UN_ANOCORTE || '- 1  THEN C3 ELSE 0 END CC7 
                  ,CASE WHEN PREANO = ' || UN_ANOCORTE  || '- 1  THEN C4 ELSE 0 END CC8 
                  ,CASE WHEN PREANO <= ' || UN_ANOCORTE || '- 2  THEN C1 ELSE 0 END CC9 
                  ,CASE WHEN PREANO <= ' || UN_ANOCORTE || '- 2  THEN C2 ELSE 0 END CC10 
                  ,CASE WHEN PREANO <= ' || UN_ANOCORTE || '- 2  THEN C3 ELSE 0 END CC11 
                  ,CASE WHEN PREANO <= ' || UN_ANOCORTE || '- 2  THEN C4 ELSE 0 END CC12 
                  ,SUM(C13) CC13
                  ,SUM(C14) CC14
                  ,SUM(C15) CC15
                  ,SUM(C16) CC16
                  ,CASE WHEN PREANO = ' || UN_ANOCORTE || ' THEN C17 ELSE 0 END CC17 
                  ,CASE WHEN PREANO = ' || UN_ANOCORTE || '- 1 THEN C18 ELSE 0 END CC18 
                  ,CASE WHEN PREANO <= ' || UN_ANOCORTE || '- 2  THEN C19 ELSE 0 END CC19 
                  ,SUM(C20) CC20 
                FROM IP_FACTURADOS
                WHERE COMPANIA      = ''' || UN_COMPANIA || 
              ''' AND CODIGO        = ''' || UN_CODPREDIO || 
              ''' AND NUMERO_ORDEN  = ''' || UN_NUMORDEN || 
              ''' AND PREANO        BETWEEN ' || UN_MENORVIGENCIA || ' AND ' || UN_MAYORVIGENCIA || 
              '   AND PAGADO        = 0   
                  AND NO_COBRADO    = 0 
                  AND INDPAGO_ACPAG = 0';    
   ELSE
    MI_STR := ' SELECT 
                  SUM(CASE WHEN PREANO = ' || UN_ANOCORTE || ' THEN C1 ELSE 0 END) CC1 
                  ,SUM(CASE WHEN PREANO = ' || UN_ANOCORTE || ' THEN C2 ELSE 0 END) CC2 
                  ,SUM(CASE WHEN PREANO = ' || UN_ANOCORTE || ' THEN C3 ELSE 0 END) CC3 
                  ,SUM(CASE WHEN PREANO = ' || UN_ANOCORTE || ' THEN C4 ELSE 0 END) CC4 
                  ,SUM(CASE WHEN PREANO = ' || UN_ANOCORTE || '- 1 THEN C1 ELSE 0 END) CC5 
                  ,SUM(CASE WHEN PREANO = ' || UN_ANOCORTE || '- 1  THEN C2 ELSE 0 END) CC6 
                  ,SUM(CASE WHEN PREANO = ' || UN_ANOCORTE || '- 1  THEN C3 ELSE 0 END) CC7 
                  ,SUM(CASE WHEN PREANO = ' || UN_ANOCORTE || ' - 1  THEN C4 ELSE 0 END) CC8 
                  ,SUM(CASE WHEN PREANO <= ' || UN_ANOCORTE || '- 2  THEN C1 ELSE 0 END) CC9 
                  ,SUM(CASE WHEN PREANO <= ' || UN_ANOCORTE || '- 2  THEN C2 ELSE 0 END) CC10 
                  ,SUM(CASE WHEN PREANO <= ' || UN_ANOCORTE || '- 2  THEN C3 ELSE 0 END) CC11 
                  ,SUM(CASE WHEN PREANO <= ' || UN_ANOCORTE || '- 2  THEN C4 ELSE 0 END) CC12 
                  ,SUM(C13) CC13
                  ,SUM(C14) CC14
                  ,SUM(C15) CC15
                  ,SUM(C16) CC16
                  ,SUM(C17) CC17
                  ,SUM(C18) CC18
                  ,SUM(C19) CC19
                  ,SUM(C20) CC20  
                FROM IP_FACTURADOS
                WHERE COMPANIA      = ''' || UN_COMPANIA || 
              ''' AND CODIGO        = ''' || UN_CODPREDIO || 
              ''' AND NUMERO_ORDEN  = ''' || UN_NUMORDEN || 
              ''' AND PREANO        BETWEEN ' || UN_MENORVIGENCIA || ' AND ' || UN_MAYORVIGENCIA || 
              '   AND PAGADO        = 0   
                  AND NOCOBRADO     = 0 
                  AND INDPAGO_ACPAG = 0';    
  END IF;
   END;
  END;
  <<VALIDACION_FACTURADOS>>
  OPEN MI_RSFACTURADOS FOR MI_STR;
    LOOP
      FETCH MI_RSFACTURADOS INTO MI_CPTO(1), MI_CPTO(2), MI_CPTO(3), MI_CPTO(4), MI_CPTO(5), 
                                 MI_CPTO(6), MI_CPTO(7), MI_CPTO(8), MI_CPTO(9), MI_CPTO(10), 
                                 MI_CPTO(11), MI_CPTO(12), MI_CPTO(13), MI_CPTO(14), MI_CPTO(15),
                                 MI_CPTO(16), MI_CPTO(17), MI_CPTO(18), MI_CPTO(19), MI_CPTO(20);
      EXIT WHEN MI_RSFACTURADOS%NOTFOUND;
        <<CALCULO_CONCEPTOS>>
        FOR I IN MI_CCONCEPTOS.FIRST .. MI_CCONCEPTOS.LAST LOOP
--          IF MI_CCONCEPTOS(I).CODIGO = I THEN
            IF MI_CCONCEPTOS(I).INFORMATIVO = 0 THEN
              --MI_TOTAL := MI_TOTAL + MI_CPTO(I);
              MI_TOTAL := MI_TOTAL + MI_CPTO(MI_CCONCEPTOS(I).CODIGO);
            END IF;
--          END IF;
        END LOOP CALCULO_CONCEPTOS;      
    END LOOP VALIDACION_FACTURADOS;
  CLOSE MI_RSFACTURADOS;

  IF MI_TOTAL = 0 AND UN_IMPRIMIR_TOTALCERO = 0 THEN
    RETURN;
  END IF;

  -- OBTENER CONSECUTIVO DE FACTURACIÓN 
<<OTRO_CONSECUTIVO>>  
  --OBTENER NUMERO DE FACTURA
  BEGIN
    SELECT  CONSECUTIVOREAL
      INTO  MI_NUMRECIBO 
    FROM    IP_NUMEROSDEFACTURA
    WHERE   COMPANIA  = UN_COMPANIA
      AND   TIPO      = MI_TIPOFACTURA
      AND   ACTIVO    <> 0;

    EXCEPTION WHEN OTHERS THEN
      MI_NUMRECIBO := '0';
  END;
  MI_NUMRECIBO := MI_NUMRECIBO + 1;
  --VERIFICAR LA LONGITUD QUE DEBE TENER EL RECIBO DE PAGO
  BEGIN
    SELECT DIGITOS
     INTO  MI_DIGRECIBO
    FROM   IP_TIPOSNUMERACION
    WHERE  CODIGO = MI_TIPOFACTURA;

    EXCEPTION WHEN OTHERS THEN
      MI_DIGRECIBO := 9;
  END;
  --VALIDAR EL CONSECUTIVO A EVALUAR
  MI_NUMRECIBO := PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => TO_CHAR(MI_NUMRECIBO), 
                                            UN_LONGITUD => MI_DIGRECIBO);
  --ACTUALIZAR EL CONSECUTIVO EN LA TABLA IP_NUMEROSDEFACTURA
  BEGIN
    BEGIN
  MI_VALORES := 'CONSECUTIVOREAL = ''' || MI_NUMRECIBO || '''
                 ,MODIFIED_BY    = '''||UN_USUARIO||'''
                 ,DATE_MODIFIED  = SYSDATE ';
  MI_CONDICION := 'COMPANIA  = ''' || UN_COMPANIA ||
              ''' AND TIPO   = ''' || MI_TIPOFACTURA || 
              ''' AND ACTIVO <> 0'; 

    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'IP_NUMEROSDEFACTURA'
                                           ,UN_ACCION    => 'M'
                                           ,UN_CAMPOS    => MI_VALORES
                                           ,UN_CONDICION => MI_CONDICION );

       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                           

    END; 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD     => SQLCODE
              ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_NUMFACTURA
            );
  END ;
  --
  --SE VERIFICA SI EN LA TABLA DE RECIBOS DE PAGO YA EXISTE UNA FACTURA CON ESTE NUMERO
  BEGIN
    SELECT 'X' 
      INTO MI_EXISTE 
    FROM   IP_RECIBOS_DE_PAGO
    WHERE  COMPANIA = UN_COMPANIA
      AND  DOCNUM   = MI_NUMRECIBO;

    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_EXISTE := 'N';
  END;
  --SI EXISTE EL NUMERO DE FACTURA SE DEBE REGRESAR A OBTENER OTRO CONSECUTIVO
  IF MI_EXISTE = 'X' THEN
    GOTO OTRO_CONSECUTIVO;
  ELSE -- SE CREA EL HEADER DEL RECIBO 
    NULL;
  END IF;
 -- VERIFICAR SI EL PREDIO TIENE RECIBOS PENDIENTES - 09/11/2016
  BEGIN
    SELECT  DISTINCT 'X'
      INTO  MI_EXISTE
    FROM    IP_RECIBOS_DE_PAGO
    WHERE   COMPANIA      = UN_COMPANIA
      AND   DOCNUM        <> MI_NUMRECIBO
      AND   PRECOD        = UN_CODPREDIO 
      AND   NUMERO_ORDEN  = UN_NUMORDEN
      AND   PAGO          = 0 
      AND   ANULADO       = 0;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_EXISTE := 'N';
  END ;
  IF MI_EXISTE = 'X' THEN
    MI_CONTROLRECIBO:= PCK_PARST.FC_PAR(UN_PARAMETRO  => 'CONTROLAR RECIBOS POR USUARIO'
                                        ,UN_VLOMISION => 'NO');
    IF MI_CONTROLRECIBO = 'SI' THEN
    BEGIN
     BEGIN
      MI_VALORES := 'ANULADO         =   -1
                     ,FECHAANULACION = TRUNC(SYSDATE)
                     ,ANULADO_POR    = '''||UN_USUARIO||'''
                     ,MODIFIED_BY    = '''||UN_USUARIO||'''
                     ,DATE_MODIFIED  = SYSDATE ';
      MI_CONDICION := 'COMPANIA          = ''' || UN_COMPANIA || 
                   ''' AND  DOCNUM       <> ''' || MI_NUMRECIBO ||
                   ''' AND PRECOD        = ''' || UN_CODPREDIO || 
                   ''' AND NUMERO_ORDEN  = ''' || UN_NUMORDEN || 
                   ''' AND ANULADO       = 0 
                       AND PAGO          = 0 
                       AND ESACUERDO     = 0
                       AND ACUERDO       IS NULL'; 
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                  UN_TABLA      => 'IP_RECIBOS_DE_PAGO' 
                                  ,UN_ACCION     => 'M' 
                                  ,UN_CAMPOS     => MI_VALORES
                                  ,UN_CONDICION  => MI_CONDICION 
                                );
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                           

    END; 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD     => SQLCODE
              ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_RECIBOPAGO
            );                            
    END;                            
    END IF;
  END IF;
  -- CREAR EL REGISTRO DE LA FACTURA
  BEGIN
    BEGIN
    MI_CAMPOS := 'COMPANIA 
                 ,DOCNUM
                 ,PRECOD
                 ,NUMERO_ORDEN
                 ,PREANO
                 ,PREVAL
                 ,PREUSU
                 ,C1  ,C2  ,C3  ,C4  ,C5
                 ,C6  ,C7  ,C8  ,C9  ,C10
                 ,C11 ,C12 ,C13 ,C14 ,C15
                 ,C16 ,C17 ,C18 ,C19 ,C20
                 ,PREANOF
                 ,PREANOI
                 ,UNICO_ANO
                 ,NOMBREPROPFAC
                 ,NITPROPFAC
                 ,ORDENPROPFAC
                 ,CREATED_BY
                 ,DATE_CREATED ';
    MI_VALORES := '''' || UN_COMPANIA ||
                  ''', ''' || MI_NUMRECIBO ||
                  ''', ''' || UN_CODPREDIO ||
                  ''', ''' || UN_NUMORDEN ||
                  ''', ' ||  UN_MAYORVIGENCIA||
                  '  , ' ||  MI_TOTAL ||
                  '  , ''' || UN_USUARIO || 
                  ''', ' || MI_CPTO(1) || ', ' || MI_CPTO(2) || ', ' || MI_CPTO(3) || ', ' || MI_CPTO(4) || ', ' || MI_CPTO(5) || 
                  '  , ' || MI_CPTO(6) || ', ' || MI_CPTO(7) || ', ' || MI_CPTO(8) || ', ' || MI_CPTO(9) || ', ' || MI_CPTO(10) || 
                  '  , ' || MI_CPTO(11) || ', ' || MI_CPTO(12) || ', ' || MI_CPTO(13) || ', ' || MI_CPTO(14) || ', ' || MI_CPTO(15) || 
                  '  , ' || MI_CPTO(16) || ', ' || MI_CPTO(17) || ', ' || MI_CPTO(18) || ', ' || MI_CPTO(19) || ', ' || MI_CPTO(20) || 
                  '  , ' || UN_MAYORVIGENCIA ||
                  '  , ' || UN_MENORVIGENCIA ||
                  '  , ' || PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => UN_MAYORVIGENCIA = UN_MENORVIGENCIA
                                                  ,UN_SI       => -1
                                                  ,UN_NO       => 0) ||
                  '  , ''' || UN_NOMBREPROPFACT ||
                  ''', ''' || UN_NITPROPFACT ||
                  ''', ''' || UN_NUMORDENFACTURAR ||
                  ''', ''' || UN_USUARIO ||
                  ''', SYSDATE ';
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                              UN_TABLA      => 'IP_RECIBOS_DE_PAGO'
                              ,UN_ACCION     => 'I' 
                              ,UN_CAMPOS     => MI_CAMPOS
                              ,UN_VALORES    => MI_VALORES
                             );

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                         

    END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD     => SQLCODE
              ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_CREARFACTURA
            );
  END ;
  -- CREAR DETALLE DE LA FACTURA
  -- ACTUALIZAR 
  -- VERIFICAR SI EL SISTEMA ESTA CONFIGURADO PARA FACTURACIÓN MULTIFECHA
  MI_FACTURARMULTIFECHA := PCK_PARST.FC_PAR(UN_PARAMETRO  => 'FACTURACION MULTIFECHAS'
                                            ,UN_VLOMISION => 'NO');
  IF MI_FACTURARMULTIFECHA = 'SI' THEN
    NULL;
  END IF;


END PR_IMPRESORAVIGENCIA;

--17

PROCEDURE PR_CREARDETALLE_FACTURA
  /*
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED     : 11/01/2017
    TIME              : 10:48 AM
    DESCRIPTION       : Se cambió el estándar de codificación y se agregó manejo de excepciones. 
    @name             : crearDetalleFactura
    @method           : POST
    */
  (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CODPREDIO      IN PCK_SUBTIPOS.TI_CODPREDIO,
    UN_NUMORDEN       IN PCK_SUBTIPOS.TI_NUMORDEN,
    UN_DOCNUM         IN PCK_SUBTIPOS.TI_DOCNUM,
    UN_TIPOFRA        IN VARCHAR2,
    UN_MENORVIGENCIA  IN PCK_SUBTIPOS.TI_ANIO,
    UN_MAYORVIGENCIA  IN PCK_SUBTIPOS.TI_ANIO,
    UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
  )
AS
  MI_CSC        PCK_SUBTIPOS.TI_ENTERO := 0;
  MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES    PCK_SUBTIPOS.TI_VALORES;
BEGIN

  IF UN_TIPOFRA = 'N' THEN      -- SI LOS DETALLES CORRESPONDEN A UNA FACTURA DE VIGENCIAS
    <<CREAR_DETVIGENCIAS>>

    FOR RSFACT IN (
                SELECT COMPANIA
                       ,PREANO
                       ,AVALUO
                       ,TRPPOR
                       ,C1  ,C2  ,C3  ,C4  ,C5  ,C6  ,C7  ,C8  ,C9  ,C10
                       ,C11 ,C12 ,C13 ,C14 ,C15 ,C16 ,C17 ,C18 ,C19 ,C20
                       ,TOTAL
                FROM      IP_FACTURADOS
                WHERE   COMPANIA      = UN_COMPANIA
                AND     CODIGO        = UN_CODPREDIO
                AND     NUMERO_ORDEN  = UN_NUMORDEN 
                AND     PREANO        BETWEEN UN_MENORVIGENCIA AND UN_MAYORVIGENCIA
                AND     PAGADO        = 0
                AND     NOCOBRADO     = 0
                AND     INDPAGO_ACPAG = 0                
              )
    LOOP
     BEGIN 
       BEGIN
          MI_CSC := MI_CSC + 1;
          MI_CAMPOS := 'COMPANIA
                       , DOCNUM
                       , CONSECUTIVO
                       , PREANO
                       , AVALUO
                       , TRPPOR
                       , C1 , C2 , C3 , C4 , C5 , C6 , C7 , C8 , C9, C10
                       , C11 , C12 , C13 , C14 , C15 , C16 , C17 , C18 , C19 , C20 
                       , TOTAL
                       , CREATED_BY
                       , DATE_CREATED';
          MI_VALORES := ''''||RSFACT.COMPANIA||'''
                        ,'''||UN_DOCNUM||'''
                        , '||MI_CSC||'
                        , '||RSFACT.PREANO||'
                        , '||RSFACT.AVALUO||'
                        , '||RSFACT.TRPPOR||'
                        , '||RSFACT.C1||', '||RSFACT.C2||', '||RSFACT.C3||', '||RSFACT.C4||', '||RSFACT.C5||'
                        , '||RSFACT.C6||', '||RSFACT.C7||', '||RSFACT.C8||', '||RSFACT.C9||', '||RSFACT.C10||'
                        , '||RSFACT.C11||', '||RSFACT.C12||', '||RSFACT.C13||', '||RSFACT.C14||', '||RSFACT.C15||'
                        , '||RSFACT.C16||', '||RSFACT.C17||', '||RSFACT.C18||', '||RSFACT.C19||', '||RSFACT.C20||'
                        , '||RSFACT.TOTAL||'
                        , '''||UN_USUARIO||'''
                        , SYSDATE   ';                        

          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'IP_DETALLE_RECIBOPAGO'
                                                 ,UN_ACCION  => 'I'
                                                 ,UN_CAMPOS  => MI_CAMPOS
                                                 ,UN_VALORES => MI_VALORES);                                                 

           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
       END;
           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
           PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD     => SQLCODE
                  ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_CREARDETFRA
                );
      END;                                             

    END LOOP CREAR_DETVIGENCIAS;   


  ELSIF UN_TIPOFRA = 'A' THEN   -- SI LOS DETALLES CORRESPONDEN A UNA FACTURA DE ACUERDO
    NULL;
  ELSIF UN_TIPOFRA = 'B' THEN   -- SI LOS DETALLES CORRESPONDEN A UNA FACTURA DE ABONO
    <<CREAR_DETABONO>>
    FOR RSFACTABONOS IN (
                        SELECT COMPANIA
                               , DOCNUM
                               , PREANO
                               , AVALUO
                               , TRPPOR 
                               , C1 , C2 , C3 , C4 , C5 
                               , C6 , C7 , C8 , C9 , C10
                               , C11, C12, C13, C14, C15
                               , C16, C17, C18, C19, C20 
                               , TOTAL
                        FROM   IP_FACTURADOSABONOS
                        WHERE  COMPANIA      =  UN_COMPANIA
                          AND  CODIGO        =  UN_CODPREDIO
                          AND  NUMERO_ORDEN  =  UN_NUMORDEN
                          AND  DOCNUM        =  UN_DOCNUM
                          AND  PAGADO        =  0
                      )
     LOOP
      MI_CSC := MI_CSC + 1;
      MI_CAMPOS := 'COMPANIA
                   , DOCNUM
                   , CONSECUTIVO
                   , PREANO
                   , AVALUO
                   , TRPPOR
                   , C1 , C2 , C3 , C4 , C5 
                   , C6 , C7 , C8 , C9 , C10
                   , C11, C12, C13, C14, C15
                   , C16, C17, C18, C19, C20 
                   , TOTAL
                   , CREATED_BY
                   , DATE_CREATED';
      MI_VALORES := ''''||RSFACTABONOS.COMPANIA||'''
                    , '''||RSFACTABONOS.DOCNUM||'''
                    , '||MI_CSC||'
                    , '||RSFACTABONOS.PREANO||'
                    , '||RSFACTABONOS.AVALUO||'
                    , '||RSFACTABONOS.TRPPOR||'
                    , '||RSFACTABONOS.C1||' , '||RSFACTABONOS.C2||' , '||RSFACTABONOS.C3||' , '||RSFACTABONOS.C4||' , '||RSFACTABONOS.C5||'
                    , '||RSFACTABONOS.C6||' , '||RSFACTABONOS.C7||' , '||RSFACTABONOS.C8||' , '||RSFACTABONOS.C9||' , '||RSFACTABONOS.C10||'
                    , '||RSFACTABONOS.C11||', '||RSFACTABONOS.C12||', '||RSFACTABONOS.C13||', '||RSFACTABONOS.C14||', '||RSFACTABONOS.C15||'
                    , '||RSFACTABONOS.C16||', '||RSFACTABONOS.C17||', '||RSFACTABONOS.C18||', '||RSFACTABONOS.C19||', '||RSFACTABONOS.C20||'
                    , '||RSFACTABONOS.TOTAL||'
                    , '''||UN_USUARIO||'''
                    , SYSDATE   ';
      --INSENTAR EL DETALLE
      BEGIN
       BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                    UN_TABLA       => 'IP_DETALLE_RECIBOPAGO'
                    ,UN_ACCION     => 'I'
                    ,UN_CAMPOS     => MI_CAMPOS
                    ,UN_VALORES    => MI_VALORES
                    ); 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
       END;
           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
           PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD     => SQLCODE
                  ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_CREARDETFRA
                );
      END;                   

    END LOOP CREAR_DETABONO;
  END IF;

END PR_CREARDETALLE_FACTURA;

PROCEDURE PR_REALIZAPAGO
  /*
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
  DATE MIGRADOR     : 15/07/2017
  TIME              : 15:20 PM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  SOURCE MODULE     : PREDIAL
  DESCRIPTION       :Procedimieno que se encarga de actualizar las tablas 
                     IP_PAGOS_WEB y IP_PAGOS_BANCOSCAB_PAZ cuando se realiza 
                     una isnercion en el registro de pagos de paz y salvo


  @NAME:  realizarPagoPazySalvo
  @METHOD:  POST    
  */     
(
  UN_COMPANIA          IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_REFERENCIA        IN IP_PAGOS_WEB.REFERENCIA%TYPE,
  UN_FECHAPAGO         IN IP_PAGOS_WEB.FECHA_PAGO%TYPE,
  UN_BANCOPAGO         IN IP_PAGOS_WEB.BANCO_PAGO%TYPE,
  UN_PAQUETE           IN IP_PAGOS_WEB.PAQUETE%TYPE,
  UN_USUARIO           IN PCK_SUBTIPOS.TI_USUARIO,
  UN_NROCUPONESACU     IN IP_PAGOS_BANCOSCAB_PAZ.NROCUPONESACU%TYPE,
  UN_VLACUMALDO        IN IP_PAGOS_BANCOSCAB_PAZ.VLACUMULADO%TYPE
)
AS

  MI_STRSQL             VARCHAR2(3000 CHAR);
  MI_CAMPOS             VARCHAR2(3000 CHAR);
  MI_CONDICION          VARCHAR2(3000 CHAR);
  MI_VALORES            VARCHAR2(3000 CHAR);
  MI_VALOR              NUMBER;
  MI_VALORACUMULADO     NUMBER(20,2);



BEGIN

  SELECT  VALOR
  INTO MI_VALOR
  FROM IP_PAGOS_WEB  
  WHERE COMPANIA = UN_COMPANIA
    AND TIPO       IN('PS') 
    AND REFERENCIA IN(UN_REFERENCIA);


    IF MI_VALOR IS NOT NULL THEN
     BEGIN 
      BEGIN
        MI_CAMPOS := 'PAGO          = -1, 
                      FECHA_PAGO    = '''||UN_FECHAPAGO||''',
                      BANCO_PAGO    = '''||UN_BANCOPAGO||''',
                      PAQUETE       = '''||UN_PAQUETE||''',
                      DATE_MODIFIED = SYSDATE,
                      MODIFIED_BY   ='''||UN_USUARIO||'''';

        MI_CONDICION := 'COMPANIA IN('''||UN_COMPANIA||''')
                        AND TIPO IN ('''||'PS'||''')
                        AND REFERENCIA IN ('''||UN_REFERENCIA||''')';

        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'IP_PAGOS_WEB'
                                              ,UN_ACCION    => 'M'
                                              ,UN_CAMPOS    => MI_CAMPOS
                                              ,UN_CONDICION => MI_CONDICION);    

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL; 

      END;

         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                                    UN_EXC_COD =>SQLCODE
                                    ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_UPDTRECIBOPAGO
                                   );

     END;  

     BEGIN
      BEGIN

          MI_VALORACUMULADO:= UN_VLACUMALDO + MI_VALOR ;

          MI_CAMPOS :='NROCUPONESACU = '||UN_NROCUPONESACU||',
                      VLACUMULADO    = '||MI_VALORACUMULADO|| ',
                      DATE_MODIFIED = SYSDATE,
                      MODIFIED_BY   ='''||UN_USUARIO||''''; 

          MI_CONDICION := 'PREFEC   ='''||UN_FECHAPAGO||'''
                        AND PAQUETE ='''||UN_PAQUETE||'''
                        AND PAG_BAN ='''||UN_BANCOPAGO||''' ';  

         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'IP_PAGOS_BANCOSCAB_PAZ'
                                              ,UN_ACCION    => 'M'
                                              ,UN_CAMPOS    => MI_CAMPOS
                                              ,UN_CONDICION => MI_CONDICION); 

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                         
      END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
           PCK_ERR_MSG.RAISE_WITH_MSG(
                              UN_EXC_COD =>SQLCODE
                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_UPDTRECIBOPAGO
                             );
     END; 

     PCK_PREDIAL.PR_AUDITORIA(UN_COMPANIA => UN_COMPANIA ,
                              UN_CODMOD =>'602',
                              UN_OPEMOD => UN_USUARIO,
                              UN_CCOMOD => UN_REFERENCIA,
                              UN_VANMOD => '0',
                              UN_VNUMOD => MI_VALOR,
                              UN_DESCRIPCION =>'Registra Recaudo de Pago de Paz y Salvo',
                              UN_FECMOD  => SYSDATE,
                              UN_HORMOD => SYSDATE,
                              UN_NUMERO_ORDEN => PCK_DATOS.CONS_NUMERO_ORDEN_PREDIAL);

    ELSE

    BEGIN 
     BEGIN
      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
     END;
       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                          UN_EXC_COD =>SQLCODE
                                          ,UN_ERROR_COD=>PCK_ERRORES.ER_PREDIAL_FACTNOTFOUND
                                         );
   END;  

    END IF;

 END PR_REALIZAPAGO;

END PCK_PREDIAL;