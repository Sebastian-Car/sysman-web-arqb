create or replace PACKAGE BODY "PCK_CONTABILIDAD6" AS
/**@package:  Contabilidad **/
FUNCTION FC_GENERAREGRESOS
  /*
    NAME              : FC_GENERAREGRESOS --> en Access GENERAREGRESOS
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : JAVIER VILLATE
    DATE MIGRADOR     : 17/04/2017
    TIME              : 09:00 AM
    MODIFIER          : SANDRA MILENA DAZA LEGUIZAMON 
    DATE MODIFIED     : 23/03/2018
    TIME              : 03:20
    DESCRIPTION       : Realiza el proceso de afectación contable entre comprobantes de orden de pago y egresos.
                        - Se valida en la linea 144 la variable MI_VALOR con NVL para que si esta nula tome valor cero. 

    MODIFICATIONS     : 
    @NAME:  generarEgresos
    @METHOD:  put
  */ 
(  
    UN_COMPANIA                     IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_ANO                          IN PCK_SUBTIPOS.TI_ANIO, 
    UN_TIPO_CPTE                    IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE, 
    UN_COMPROBANTE                  IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
    UN_LISTANUMEROAFECTAR           IN PCK_SUBTIPOS.TI_STRSQL, 
    UN_LISTATERCEROAFECTAR          IN PCK_SUBTIPOS.TI_STRSQL,
    UN_TERCEROEGRESO                IN PCK_SUBTIPOS.TI_LOGICO,
    UN_FECHA                        IN DATE,
    UN_CLASE                        IN PCK_SUBTIPOS.TI_CLASECOMPROBANTE,
    UN_TERCERO                      IN PCK_SUBTIPOS.TI_TERCERO,
    UN_SUCURSAL                     IN PCK_SUBTIPOS.TI_SUCURSAL,
    UN_VALORAPAGAR                  IN PCK_SUBTIPOS.TI_DOBLE,
    UN_CANTIDAD                     IN PCK_SUBTIPOS.TI_ENTERO,
    UN_USUARIO                      IN VARCHAR2
    )

  RETURN PCK_SUBTIPOS.TI_ENTERO AS
  MI_RTA                            PCK_SUBTIPOS.TI_STRSQL;
  MI_EXISTE                         PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_VALOR                          PCK_SUBTIPOS.TI_DOBLE:=0;
  MI_CONSECUTIVODET                 PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_TOTALDESCRIPCION               PCK_SUBTIPOS.TI_STRSQL;
  MI_OTRADESCRIPCION                PCK_SUBTIPOS.TI_STRSQL;
  MI_INTVEZ                         PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_VALOR_PARAMETRO                PCK_SUBTIPOS.TI_CAMPOS;
  MI_DESCRIPCION                    PCK_SUBTIPOS.TI_STRSQL;
  MI_VSALDO                         PCK_SUBTIPOS.TI_DOBLE:=0;
  MI_VALORPARCIAL                   PCK_SUBTIPOS.TI_DOBLE:=0;
  MI_VALORDEBITO                    PCK_SUBTIPOS.TI_DOBLE:=0;
  MI_VALORCREDITO                   PCK_SUBTIPOS.TI_DOBLE:=0;
  MI_CAMPOS                         PCK_SUBTIPOS.TI_CAMPOS;
  MI_PCKDATOS                       PCK_SUBTIPOS.TI_RTA_ACME;
  MI_CONDICION                      PCK_SUBTIPOS.TI_CONDICION;
  MI_CONDICION_TERCERO              PCK_SUBTIPOS.TI_CONDICION;
  MI_VALORES                        PCK_SUBTIPOS.TI_VALORES;
  MI_DBLGIRAR                       PCK_SUBTIPOS.TI_DOBLE:=0;
  MI_TABLA                          PCK_SUBTIPOS.TI_TABLA;
  MI_CONSULTA                       PCK_SUBTIPOS.TI_STRSQL;
  RSDATOS                           SYS_REFCURSOR;
  RSBANCO                           SYS_REFCURSOR;
  RSCONTRACUENTAS                   SYS_REFCURSOR;
  MI_AUX                                    PCK_SUBTIPOS.TI_TEXTO1;
  MI_ANO                            PCK_SUBTIPOS.TI_ANIO;
  MI_TIPO                           PCK_SUBTIPOS.TI_TIPOCOMPROBANTE; 
  MI_CENTRO_COSTO                   PCK_SUBTIPOS.TI_CENTRO_COSTO;
  MI_AUXILIAR                       PCK_SUBTIPOS.TI_AUXILIAR;
  MI_TEXTO                          PCK_SUBTIPOS.TI_STRSQL;
  MI_NUMERO                         PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT; 
  MIBANCO_COMPANIA                  PCK_SUBTIPOS.TI_COMPANIA;
  MIBANCO_ANO                       PCK_SUBTIPOS.TI_ANIO;
  MIBANCO_TIPO                      PCK_SUBTIPOS.TI_TIPOCOMPROBANTE; 
  MIBANCO_CUENTA                    PCK_SUBTIPOS.TI_CODIGOCONTA; 
  MIBANCO_NUMERO                    PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT; 
  MIBANCO_VALOR                     PCK_SUBTIPOS.TI_DOBLE:=0; 
  MIBANCO_NUMEROCHEQUE              PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT;
  MIBANCO_REFERENCIA                PCK_SUBTIPOS.TI_REFERENCIA;
  MIBANCO_CENTROCOSTO               PCK_SUBTIPOS.TI_CENTRO_COSTO; 
  MIBANCO_NATURALEZA                PCK_SUBTIPOS.TI_NATURALEZACONTA;
  MICONTRACUENTAS_CLASECUENTA       PCK_SUBTIPOS.TI_CLASECUENTACONTA;
  MICONTRACUENTAS_TIPO_CPTE         PCK_SUBTIPOS.TI_TIPOCOMPROBANTE; 
  MICONTRACUENTAS_COMPROBANTE       PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT; 
  MICONTRACUENTAS_CONSECUTIVO       PCK_SUBTIPOS.TI_CONSECUTIVOCNT; 
  MICONTRACUENTAS_NATURALEZA        PCK_SUBTIPOS.TI_NATURALEZACONTA;
  MICONTRACUENTAS_TERCERO           PCK_SUBTIPOS.TI_TERCERO;
  MICONTRACUENTAS_SUCURSAL          PCK_SUBTIPOS.TI_SUCURSAL;
  MICONTRACUENTAS_CENTRO_COSTO      PCK_SUBTIPOS.TI_CENTRO_COSTO;
  MICONTRACUENTAS_AUXILIAR          PCK_SUBTIPOS.TI_AUXILIAR;
  MICONTRACUENTAS_FUENTE            PCK_SUBTIPOS.TI_FUENTE_RECURSOS;
  MICONTRACUENTAS_REFERENCIA        PCK_SUBTIPOS.TI_REFERENCIA;
  MICONTRACUENTAS_CUENTA            PCK_SUBTIPOS.TI_CODIGOCONTA; 
  MICONTRACUENTAS_ID                VARCHAR2(4000); 
  MICONTRACUENTAS_SVALOR_DEBITO     PCK_SUBTIPOS.TI_DOBLE:=0;
  MICONTRACUENTAS_SVALOR_CREDITO    PCK_SUBTIPOS.TI_DOBLE:=0;
  MICONTRA_SDEBITO_AFECTADO         PCK_SUBTIPOS.TI_DOBLE:=0;
  MICONTRA_SCREDITO_AFECTADO        PCK_SUBTIPOS.TI_DOBLE:=0;
  MICONTRA_SALDO                    PCK_SUBTIPOS.TI_DOBLE:=0;
  MI_SALDOCUENTA                    PCK_SUBTIPOS.TI_ENTERO;
  MI_ACTUALIZACHEQUE                PCK_SUBTIPOS.TI_ENTERO;
  MI_CNTDAD_TERCEROS                PCK_SUBTIPOS.TI_LOGICO; 
  MI_NRODOCUMENTO                   VARCHAR2(30 CHAR);    
  MI_VALOR_PAR_CAUSA_AUTO           VARCHAR2(2 CHAR);
  MI_CONTADOR_PARCIAL               NUMBER; -- JM CC 4311 (pagos parciales solamente si se donde colocar lo parcial)
BEGIN  
    BEGIN

    IF LENGTH(UN_LISTATERCEROAFECTAR)>30 THEN
        MI_CNTDAD_TERCEROS:=-1;
    ELSE
        MI_CNTDAD_TERCEROS:=0;
    END IF;


    IF UN_TERCEROEGRESO  IN (0)THEN

    MI_CONDICION_TERCERO :=  'AND (DETALLE_COMPROBANTE_CNT.TERCERO, DETALLE_COMPROBANTE_CNT.SUCURSAL) IN ('||UN_LISTATERCEROAFECTAR||')';

    ELSE

    MI_CONDICION_TERCERO :=  'AND (DETALLE_COMPROBANTE_CNT.TERCERO, DETALLE_COMPROBANTE_CNT.SUCURSAL, DETALLE_COMPROBANTE_CNT.CONSECUTIVO) IN ('||UN_LISTATERCEROAFECTAR||')';

   END IF;

MI_CONSULTA := '
      SELECT SUM(CASE WHEN V_PLAN_CONTABLE.NATURALEZA=''D'' 
                 THEN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO-DETALLE_COMPROBANTE_CNT.VALOR_CREDITO 
                 ELSE DETALLE_COMPROBANTE_CNT.VALOR_CREDITO-DETALLE_COMPROBANTE_CNT.VALOR_DEBITO END)
            -SUM(DETALLE_COMPROBANTE_CNT.DEBITO_AFECTADO)
            -SUM(DETALLE_COMPROBANTE_CNT.CREDITO_AFECTADO) 
             MI_VALOR
      FROM V_PLAN_CONTABLE INNER JOIN DETALLE_COMPROBANTE_CNT 
             ON  DETALLE_COMPROBANTE_CNT.COMPANIA = V_PLAN_CONTABLE.COMPANIA 
             AND DETALLE_COMPROBANTE_CNT.ANO      = V_PLAN_CONTABLE.ANO 
             AND DETALLE_COMPROBANTE_CNT.ID       = V_PLAN_CONTABLE.ID
      WHERE DETALLE_COMPROBANTE_CNT.COMPANIA='''||UN_COMPANIA||'''
             AND (DETALLE_COMPROBANTE_CNT.ANO, DETALLE_COMPROBANTE_CNT.TIPO_CPTE, DETALLE_COMPROBANTE_CNT.COMPROBANTE) IN ('||UN_LISTANUMEROAFECTAR||')
             '||MI_CONDICION_TERCERO||'
             AND V_PLAN_CONTABLE.CLASECUENTA IN (''C'',''P'',''E'')';

             EXECUTE IMMEDIATE MI_CONSULTA INTO MI_VALOR;

      EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_VALOR:=0;
      END;        

      MI_SALDOCUENTA:= PCK_CONTABILIDAD6.FC_SALDO_CUENTA_EGRESO(UN_COMPANIA            => UN_COMPANIA,
                                                                UN_ANO                 => UN_ANO,
                                                                UN_TIPO                => UN_TIPO_CPTE, 
                                                                UN_NUMERO              => UN_COMPROBANTE,
                                                                UN_VLRGIRAR            => MI_VALOR);

      IF MI_SALDOCUENTA =2 THEN
         RETURN MI_SALDOCUENTA;
      END IF;   

      MI_ACTUALIZACHEQUE:= PCK_CONTABILIDAD6.FC_ACTUALIZA_CHEQUE(UN_COMPANIA            => UN_COMPANIA,
                                                                 UN_ANO                 => UN_ANO,
                                                                 UN_TIPO                => UN_TIPO_CPTE, 
                                                                 UN_NUMERO              => UN_COMPROBANTE,
                                                                 UN_VLRGIRAR            => MI_VALOR);
      IF MI_ACTUALIZACHEQUE =3 THEN
         RETURN MI_ACTUALIZACHEQUE;
      END IF; 

   MI_CONSECUTIVODET := 1; 
   MI_VALOR := ROUND(NVL(MI_VALOR, 0), 2);
   MI_TOTALDESCRIPCION := 'Canc. O.Pago ';
   MI_INTVEZ := 1;
<<datos_comprobante_afectar>>   

    IF UN_TERCEROEGRESO IN (0)THEN

    MI_CONDICION_TERCERO :=  'AND (DETALLE_COMPROBANTE_CNT.TERCERO, DETALLE_COMPROBANTE_CNT.SUCURSAL) IN ('||UN_LISTATERCEROAFECTAR||')';

    ELSE

    MI_CONDICION_TERCERO :=  'AND (DETALLE_COMPROBANTE_CNT.TERCERO, DETALLE_COMPROBANTE_CNT.SUCURSAL, DETALLE_COMPROBANTE_CNT.CONSECUTIVO) IN ('||UN_LISTATERCEROAFECTAR||')';

   END IF;

  MI_CONSULTA:='  SELECT DISTINCT COMPROBANTE_CNT.ANO,
        COMPROBANTE_CNT.TIPO,
        COMPROBANTE_CNT.NUMERO,
        COMPROBANTE_CNT.CENTRO_COSTO,
        COMPROBANTE_CNT.AUXILIAR,
        COMPROBANTE_CNT.DESCRIPCION,
        COMPROBANTE_CNT.TEXTO
        FROM COMPROBANTE_CNT
        INNER JOIN DETALLE_COMPROBANTE_CNT
        ON COMPROBANTE_CNT.COMPANIA=DETALLE_COMPROBANTE_CNT.COMPANIA
        AND COMPROBANTE_CNT.ANO=DETALLE_COMPROBANTE_CNT.ANO
        AND COMPROBANTE_CNT.TIPO=DETALLE_COMPROBANTE_CNT.TIPO_CPTE
        AND COMPROBANTE_CNT.NUMERO=DETALLE_COMPROBANTE_CNT.COMPROBANTE
          WHERE DETALLE_COMPROBANTE_CNT.COMPANIA='''||UN_COMPANIA||''' 
                AND (DETALLE_COMPROBANTE_CNT.ANO, DETALLE_COMPROBANTE_CNT.TIPO_CPTE, DETALLE_COMPROBANTE_CNT.COMPROBANTE) IN ('||UN_LISTANUMEROAFECTAR||')  
                '||MI_CONDICION_TERCERO||'
    ORDER BY NUMERO';

    OPEN RSDATOS FOR MI_CONSULTA;
    LOOP
    FETCH RSDATOS INTO MI_ANO, MI_TIPO, MI_NUMERO, MI_CENTRO_COSTO, MI_AUXILIAR, MI_DESCRIPCION, MI_TEXTO ;
    EXIT WHEN RSDATOS%NOTFOUND;
    BEGIN 

   IF UN_CLASE = 'J' THEN
      MI_OTRADESCRIPCION := 'ING No. ';
   ELSIF UN_CLASE = 'A' THEN
      MI_OTRADESCRIPCION :='EGR No. ';
   ELSIF UN_CLASE IN('E','R','T') THEN
      MI_OTRADESCRIPCION := 'O.P No. ';
   ElSE
      MI_OTRADESCRIPCION := 'Comp No. ';
   End IF;

   MI_VALOR_PARAMETRO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'TRAER DETALLE Y TEXTO DE DOCUMENTO AFECTADO' , PCK_DATOS.MODULOCONTABILIDAD , SYSDATE),'NO');
     IF MI_VALOR_PARAMETRO = 'NO' THEN
        MI_TEXTO := ''; 
     END IF;
    --(APINEDA:25/02/2022) TICKET 7710363 Se eliminan comas, comilla y enter de la descripcion para evitar problemas al insertar
    MI_DESCRIPCION := REPLACE(REPLACE(REPLACE(MI_DESCRIPCION, CHR(10),' '),',',' '), '''',' ');
    MI_VALOR_PARAMETRO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'DESCRIPCION DETALLE = DESCRIPCION COMPROBANTE' , PCK_DATOS.MODULOCONTABILIDAD , SYSDATE),'NO');
    IF MI_VALOR_PARAMETRO = 'NO' THEN
       IF UN_CANTIDAD=1 THEN
         MI_TOTALDESCRIPCION := 'Cancelación Orden de Pago No. ' || MI_NUMERO ||',';
       ELSE
         MI_TOTALDESCRIPCION := MI_DESCRIPCION || ' ' || MI_TOTALDESCRIPCION || MI_NUMERO || ',';  
       END IF;  
    ELSE
         MI_TOTALDESCRIPCION := MI_TOTALDESCRIPCION || ' ' || MI_NUMERO || ' ' || MI_DESCRIPCION || ',';  
         MI_DESCRIPCION      := MI_TOTALDESCRIPCION;  
    END IF;


     IF MI_INTVEZ = 1 THEN

<<procesar_cuentas_bancarias>>
    MI_VALOR_PAR_CAUSA_AUTO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'MANEJA REFERENCIADO EN CAUSACION AUTOMATICA' , PCK_DATOS.MODULOCONTABILIDAD , SYSDATE),'NO');        


    MI_CONSULTA:=' SELECT COMPROBANTE_CNTBANCOS.COMPANIA, 
                   COMPROBANTE_CNTBANCOS.ANO, 
                   COMPROBANTE_CNTBANCOS.TIPO,
                   COMPROBANTE_CNTBANCOS.CUENTA,
                   COMPROBANTE_CNTBANCOS.NUMERO,
                   COMPROBANTE_CNTBANCOS.VALOR,
                   COMPROBANTE_CNTBANCOS.NUMEROCHEQUE,
                   COMPROBANTE_CNTBANCOS.REFERENCIA,
                   COMPROBANTE_CNTBANCOS.CENTRO_COSTO,
                   V_PLAN_CONTABLE.NATURALEZA       
            FROM V_PLAN_CONTABLE INNER JOIN COMPROBANTE_CNTBANCOS 
                   ON  COMPROBANTE_CNTBANCOS.COMPANIA = V_PLAN_CONTABLE.COMPANIA 
                   AND COMPROBANTE_CNTBANCOS.ANO      = V_PLAN_CONTABLE.ANO 
                   AND COMPROBANTE_CNTBANCOS.CUENTA   = V_PLAN_CONTABLE.ID
            WHERE COMPROBANTE_CNTBANCOS.COMPANIA   ='''||UN_COMPANIA||'''
                   AND COMPROBANTE_CNTBANCOS.ANO   ='||UN_ANO||'
                   AND COMPROBANTE_CNTBANCOS.TIPO  ='''||UN_TIPO_CPTE||'''
                   AND COMPROBANTE_CNTBANCOS.NUMERO='||UN_COMPROBANTE||'
            ORDER BY COMPROBANTE_CNTBANCOS.COMPANIA, 
                     COMPROBANTE_CNTBANCOS.ANO, 
                     COMPROBANTE_CNTBANCOS.TIPO, 
                     COMPROBANTE_CNTBANCOS.NUMERO';
             OPEN RSBANCO FOR MI_CONSULTA;
             LOOP
               FETCH RSBANCO INTO MIBANCO_COMPANIA, MIBANCO_ANO, MIBANCO_TIPO, MIBANCO_CUENTA, MIBANCO_NUMERO, MIBANCO_VALOR, MIBANCO_NUMEROCHEQUE, MIBANCO_REFERENCIA, MIBANCO_CENTROCOSTO, MIBANCO_NATURALEZA ;
               EXIT WHEN RSBANCO%NOTFOUND;
               BEGIN         
                  MI_VSALDO := UN_VALORAPAGAR;
                  MI_VALORPARCIAL := NVL(MIBANCO_VALOR, 0);

                IF MI_VALORPARCIAL > MI_VSALDO THEN
                    MI_VALORPARCIAL := MI_VSALDO;
                END IF;
                MI_VSALDO := ROUND(MI_VSALDO - MI_VALORPARCIAL, 2);

          BEGIN            
           IF(MI_VALOR_PAR_CAUSA_AUTO = 'SI') THEN
               MI_CAMPOS := 'COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CONSECUTIVO,CUENTA,
                            FECHA,NATURALEZA,DESCRIPCION,VALOR_DEBITO,VALOR_CREDITO,
                            EJECUCION_DEBITO,BASE_GRAVABLE,REFERENCIA,CENTRO_COSTO,TERCERO,SUCURSAL,
                            AUXILIAR,NRO_DOCUMENTO,CREATED_BY,DATE_CREATED';
               MI_VALORES := '''' || UN_COMPANIA || '''
                                  ,' || UN_ANO || '
                                  ,''' || UN_TIPO_CPTE || '''
                                  ,' || UN_COMPROBANTE || '
                                  ,' || MI_CONSECUTIVODET || '
                                  ,''' || MIBANCO_CUENTA || '''
                                  ,TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                                  ,''' ||  MIBANCO_NATURALEZA || '''
                                  ,''' || MI_DESCRIPCION || '''
                                  ,0
                                  ,' || MI_VALORPARCIAL || '
                                  ,0
                                  ,' || MI_VALORPARCIAL || '
                                  ,''' || MIBANCO_REFERENCIA || '''
                                  ,''' || MIBANCO_CENTROCOSTO || '''
                                  ,''' || UN_TERCERO || '''
                                  ,''' || UN_SUCURSAL || '''
                                  ,''' || MI_AUXILIAR || '''
                                  ,''' || MIBANCO_NUMEROCHEQUE || '''
                                  ,''' || UN_USUARIO || '''
                                  , SYSDATE';
           ELSE
               MI_CAMPOS := 'COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CONSECUTIVO,CUENTA,
                            FECHA,NATURALEZA,DESCRIPCION,VALOR_DEBITO,VALOR_CREDITO,
                            EJECUCION_DEBITO,BASE_GRAVABLE,CENTRO_COSTO,TERCERO,SUCURSAL,
                            AUXILIAR,NRO_DOCUMENTO,CREATED_BY,DATE_CREATED';
               MI_VALORES := '''' || UN_COMPANIA || '''
                              ,' || UN_ANO || '
                              ,''' || UN_TIPO_CPTE || '''
                              ,' || UN_COMPROBANTE || '
                              ,' || MI_CONSECUTIVODET || '
                              ,''' || MIBANCO_CUENTA || '''
                              ,TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                              ,''' ||  MIBANCO_NATURALEZA || '''
                              ,''' || MI_DESCRIPCION || '''
                              ,0
                              ,' || MI_VALORPARCIAL || '
                              ,0
                              ,' || MI_VALORPARCIAL || '
                              ,''' || MI_CENTRO_COSTO || '''
                              ,''' || UN_TERCERO || '''
                              ,''' || UN_SUCURSAL || '''
                              ,''' || MI_AUXILIAR || '''
                              ,''' || MIBANCO_NUMEROCHEQUE || '''
                              ,''' || UN_USUARIO || '''
                              , SYSDATE';
            END IF;

                       MI_PCKDATOS := PCK_DATOS.FC_ACME(
                              UN_TABLA => 'DETALLE_COMPROBANTE_CNT', 
                              UN_ACCION => 'I', 
                              UN_CAMPOS => MI_CAMPOS, 
                              UN_VALORES => MI_VALORES
                            );
                          EXCEPTION
                            WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                              PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD => SQLCODE,
                                UN_TABLAERROR => 'DETALLE_COMPROBANTE_CNT',
                                UN_ERROR_COD => PCK_ERRORES.ERR_CONTABILIDAD_INSERBANCOS
                              ); 
                          END; 

                  MI_CONSECUTIVODET := MI_CONSECUTIVODET + 1;
               END;
            END LOOP procesar_cuentas_bancarias;

            MI_INTVEZ := 2;
     END IF;

<<procesar_contracuentas>>             
 --29/08/2018 @jreina Se agrega la fuente de recurso y la referencia en la consulta y la insercion
 IF UN_TERCEROEGRESO IN (0)THEN

    MI_CONDICION_TERCERO :=  'AND (DETALLE_COMPROBANTE_CNT.TERCERO, DETALLE_COMPROBANTE_CNT.SUCURSAL) IN ('||UN_LISTATERCEROAFECTAR||')';

    ELSE

    MI_CONDICION_TERCERO :=  'AND (DETALLE_COMPROBANTE_CNT.TERCERO, DETALLE_COMPROBANTE_CNT.SUCURSAL, DETALLE_COMPROBANTE_CNT.CONSECUTIVO) IN ('||UN_LISTATERCEROAFECTAR||')';

   END IF;

   IF(MI_VALOR_PAR_CAUSA_AUTO = 'SI') THEN

         MI_CONSULTA:='SELECT V_PLAN_CONTABLE.CLASECUENTA,
                      DETALLE_COMPROBANTE_CNT.TIPO_CPTE,
                      DETALLE_COMPROBANTE_CNT.COMPROBANTE,
                      DETALLE_COMPROBANTE_CNT.CONSECUTIVO,
                      DETALLE_COMPROBANTE_CNT.NATURALEZA,
                      DETALLE_COMPROBANTE_CNT.TERCERO,
                      DETALLE_COMPROBANTE_CNT.SUCURSAL,
                      DETALLE_COMPROBANTE_CNT.CENTRO_COSTO,
                      DETALLE_COMPROBANTE_CNT.AUXILIAR,
                      DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO,
                      DETALLE_COMPROBANTE_CNT.REFERENCIA,
                      DETALLE_COMPROBANTE_CNT.CUENTA,
                      DETALLE_COMPROBANTE_CNT.ID,
                      DETALLE_COMPROBANTE_CNT.NRO_DOCUMENTO,
                      SUM(DETALLE_COMPROBANTE_CNT.VALOR_DEBITO) AS SVALOR_DEBITO,
                      SUM(COMPROBANTE_CNTBANCOS.VALOR) AS SVALOR_CREDITO,  
                      SUM(DEBITO_AFECTADO) AS SDEBITO_AFECTADO,
                      SUM(0) AS SCREDITO_AFECTADO,
                      SUM(COMPROBANTE_CNTBANCOS.VALOR) SALDO
               FROM V_PLAN_CONTABLE INNER JOIN DETALLE_COMPROBANTE_CNT 
                     ON  DETALLE_COMPROBANTE_CNT.COMPANIA = V_PLAN_CONTABLE.COMPANIA 
                     AND DETALLE_COMPROBANTE_CNT.ANO      = V_PLAN_CONTABLE.ANO 
                     AND DETALLE_COMPROBANTE_CNT.ID       = V_PLAN_CONTABLE.ID
                     INNER JOIN COMPROBANTE_CNTBANCOS
                     ON COMPROBANTE_CNTBANCOS.COMPANIA = DETALLE_COMPROBANTE_CNT.COMPANIA
                     AND COMPROBANTE_CNTBANCOS.ANO = DETALLE_COMPROBANTE_CNT.ANO
                     AND COMPROBANTE_CNTBANCOS.TIPO = '''||UN_TIPO_CPTE||'''
                     AND COMPROBANTE_CNTBANCOS.NUMERO = '||UN_COMPROBANTE||'
                     AND COMPROBANTE_CNTBANCOS.REFERENCIA = DETALLE_COMPROBANTE_CNT.REFERENCIA
                     AND COMPROBANTE_CNTBANCOS.CENTRO_COSTO = DETALLE_COMPROBANTE_CNT.CENTRO_COSTO
               WHERE DETALLE_COMPROBANTE_CNT.COMPANIA='''||UN_COMPANIA||'''
                      AND DETALLE_COMPROBANTE_CNT.ANO='||MI_ANO||'
                      AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE='''||MI_TIPO||'''
                      AND DETALLE_COMPROBANTE_CNT.COMPROBANTE='||MI_NUMERO||'
                      '||MI_CONDICION_TERCERO||'
               GROUP BY V_PLAN_CONTABLE.CLASECUENTA,
                      DETALLE_COMPROBANTE_CNT.TIPO_CPTE, 
                      DETALLE_COMPROBANTE_CNT.COMPROBANTE, 
                      DETALLE_COMPROBANTE_CNT.CONSECUTIVO, 
                      DETALLE_COMPROBANTE_CNT.NATURALEZA, 
                      DETALLE_COMPROBANTE_CNT.TERCERO, 
                      DETALLE_COMPROBANTE_CNT.SUCURSAL, 
                      DETALLE_COMPROBANTE_CNT.CENTRO_COSTO, 
                      DETALLE_COMPROBANTE_CNT.AUXILIAR,
                      DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO,
                      DETALLE_COMPROBANTE_CNT.REFERENCIA,
                      DETALLE_COMPROBANTE_CNT.CUENTA, 
                      DETALLE_COMPROBANTE_CNT.ID,
                      DETALLE_COMPROBANTE_CNT.NRO_DOCUMENTO';
        
         ELSE
        
         MI_CONSULTA:='SELECT V_PLAN_CONTABLE.CLASECUENTA,
                      DETALLE_COMPROBANTE_CNT.TIPO_CPTE,
                      DETALLE_COMPROBANTE_CNT.COMPROBANTE,
                      DETALLE_COMPROBANTE_CNT.CONSECUTIVO,
                      DETALLE_COMPROBANTE_CNT.NATURALEZA,
                      DETALLE_COMPROBANTE_CNT.TERCERO,
                      DETALLE_COMPROBANTE_CNT.SUCURSAL,
                      DETALLE_COMPROBANTE_CNT.CENTRO_COSTO,
                      DETALLE_COMPROBANTE_CNT.AUXILIAR,
                      DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO,
                      DETALLE_COMPROBANTE_CNT.REFERENCIA,
                      DETALLE_COMPROBANTE_CNT.CUENTA,
                      DETALLE_COMPROBANTE_CNT.ID,
                      DETALLE_COMPROBANTE_CNT.NRO_DOCUMENTO,
                      SUM(DETALLE_COMPROBANTE_CNT.VALOR_DEBITO) AS SVALOR_DEBITO,
                      SUM(DETALLE_COMPROBANTE_CNT.VALOR_CREDITO) AS SVALOR_CREDITO,
                      SUM(DEBITO_AFECTADO) AS SDEBITO_AFECTADO,
                      SUM(CREDITO_AFECTADO) AS SCREDITO_AFECTADO,
                      SUM(CASE WHEN V_PLAN_CONTABLE.NATURALEZA=''D''
                         THEN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO-DETALLE_COMPROBANTE_CNT.VALOR_CREDITO
                         ELSE DETALLE_COMPROBANTE_CNT.VALOR_CREDITO-DETALLE_COMPROBANTE_CNT.VALOR_DEBITO END)
                      -SUM(DETALLE_COMPROBANTE_CNT.DEBITO_AFECTADO)
                      -SUM(DETALLE_COMPROBANTE_CNT.CREDITO_AFECTADO) SALDO
               FROM V_PLAN_CONTABLE INNER JOIN DETALLE_COMPROBANTE_CNT
                     ON  DETALLE_COMPROBANTE_CNT.COMPANIA = V_PLAN_CONTABLE.COMPANIA
                     AND DETALLE_COMPROBANTE_CNT.ANO      = V_PLAN_CONTABLE.ANO
                     AND DETALLE_COMPROBANTE_CNT.ID       = V_PLAN_CONTABLE.ID
               WHERE DETALLE_COMPROBANTE_CNT.COMPANIA='''||UN_COMPANIA||'''
                      AND DETALLE_COMPROBANTE_CNT.ANO='||MI_ANO||'
                      AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE='''||MI_TIPO||'''
                      AND DETALLE_COMPROBANTE_CNT.COMPROBANTE='||MI_NUMERO||'
                      '||MI_CONDICION_TERCERO||'
               GROUP BY V_PLAN_CONTABLE.CLASECUENTA,
                      DETALLE_COMPROBANTE_CNT.TIPO_CPTE,
                      DETALLE_COMPROBANTE_CNT.COMPROBANTE,
                      DETALLE_COMPROBANTE_CNT.CONSECUTIVO,
                      DETALLE_COMPROBANTE_CNT.NATURALEZA,
                      DETALLE_COMPROBANTE_CNT.TERCERO,
                      DETALLE_COMPROBANTE_CNT.SUCURSAL,
                      DETALLE_COMPROBANTE_CNT.CENTRO_COSTO,
                      DETALLE_COMPROBANTE_CNT.AUXILIAR,
                      DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO,
                      DETALLE_COMPROBANTE_CNT.REFERENCIA,
                      DETALLE_COMPROBANTE_CNT.CUENTA,
                      DETALLE_COMPROBANTE_CNT.ID,
                      DETALLE_COMPROBANTE_CNT.NRO_DOCUMENTO';
    END IF;

    BEGIN -- JM CC 4311 (efectivamente paulis daño lo de abajo, pero tranquilos aqui estoy yo)
        EXECUTE IMMEDIATE 'SELECT COUNT(*) FROM (' || MI_CONSULTA || ') WHERE CLASECUENTA IN (''P'',''E'')' INTO MI_CONTADOR_PARCIAL;
    END;

        OPEN RSCONTRACUENTAS FOR MI_CONSULTA;
    LOOP
        FETCH RSCONTRACUENTAS INTO MICONTRACUENTAS_CLASECUENTA , MICONTRACUENTAS_TIPO_CPTE, MICONTRACUENTAS_COMPROBANTE, MICONTRACUENTAS_CONSECUTIVO,
              MICONTRACUENTAS_NATURALEZA,MICONTRACUENTAS_TERCERO,MICONTRACUENTAS_SUCURSAL,MICONTRACUENTAS_CENTRO_COSTO,MICONTRACUENTAS_AUXILIAR,
               MICONTRACUENTAS_FUENTE, MICONTRACUENTAS_REFERENCIA,
              MICONTRACUENTAS_CUENTA,MICONTRACUENTAS_ID,MI_NRODOCUMENTO,MICONTRACUENTAS_SVALOR_DEBITO,MICONTRACUENTAS_SVALOR_CREDITO,
              MICONTRA_SDEBITO_AFECTADO,MICONTRA_SCREDITO_AFECTADO,MICONTRA_SALDO;
        EXIT WHEN RSCONTRACUENTAS%NOTFOUND;
        BEGIN
         IF (((MICONTRACUENTAS_SVALOR_CREDITO) <> 0) AND ((MICONTRACUENTAS_CLASECUENTA) <> 'I' AND (MICONTRACUENTAS_CLASECUENTA) <> 'X' AND (MICONTRACUENTAS_CLASECUENTA) <> 'Y') AND ((SUBSTR(MICONTRACUENTAS_CUENTA,0, 1)) = '2')) 
            OR (((MICONTRACUENTAS_SVALOR_CREDITO) <> 0) AND ((MICONTRACUENTAS_CLASECUENTA) <> 'I' AND (MICONTRACUENTAS_CLASECUENTA) <> 'X' AND (MICONTRACUENTAS_CLASECUENTA) <> 'Y') AND ((SUBSTR(MICONTRACUENTAS_CUENTA,0, 1)) = '1') AND ((SUBSTR(MICONTRACUENTAS_CUENTA,0, 2)) >= '15')) 
            OR (((MICONTRACUENTAS_SVALOR_CREDITO) <> 0) AND ((MICONTRACUENTAS_CLASECUENTA) IS NULL) AND ((SUBSTR(MICONTRACUENTAS_CUENTA,0, 1)) = '2')) OR (((MICONTRACUENTAS_SVALOR_CREDITO) <> 0) AND ((MICONTRACUENTAS_CLASECUENTA) IS NULL) AND ((SUBSTR(MICONTRACUENTAS_CUENTA,0, 1)) = '1') 
            AND ((SUBSTR(MICONTRACUENTAS_CUENTA,0, 2)) >= '15')) OR (((MICONTRACUENTAS_SVALOR_DEBITO) <> 0) AND ((MICONTRACUENTAS_CLASECUENTA) <> 'I' AND (MICONTRACUENTAS_CLASECUENTA) <> 'X' AND (MICONTRACUENTAS_CLASECUENTA) <> 'Y') AND ((SUBSTR(MICONTRACUENTAS_CUENTA,0, 1)) = '2')) OR 
                (((MICONTRACUENTAS_SVALOR_DEBITO) <> 0) AND ((MICONTRACUENTAS_CLASECUENTA) IS NULL) AND ((SUBSTR(MICONTRACUENTAS_CUENTA,0, 1)) = '2')) THEN

          IF (((SUBSTR(MICONTRACUENTAS_CUENTA,0, 1) = '1' AND SUBSTR(MICONTRACUENTAS_CUENTA,0, 2) >= '15' OR SUBSTR(MICONTRACUENTAS_CUENTA,0, 1) = '2' AND ((MICONTRACUENTAS_CLASECUENTA <> 'I' AND MICONTRACUENTAS_CLASECUENTA <> 'X' AND MICONTRACUENTAS_CLASECUENTA <> 'Y') 
            OR MICONTRACUENTAS_CLASECUENTA IS NULL)) AND MICONTRACUENTAS_SVALOR_CREDITO <> 0) 
            OR (SUBSTR(MICONTRACUENTAS_CUENTA,0, 1) = '2' AND ((MICONTRACUENTAS_CLASECUENTA <> 'I' AND MICONTRACUENTAS_CLASECUENTA <> 'X' AND MICONTRACUENTAS_CLASECUENTA <> 'Y') 
            OR MICONTRACUENTAS_CLASECUENTA IS NULL) AND MICONTRACUENTAS_SVALOR_DEBITO <> 0)) THEN

            IF MICONTRACUENTAS_SVALOR_DEBITO > 0 AND MICONTRACUENTAS_SVALOR_CREDITO > 0 THEN
               IF NVL(MICONTRACUENTAS_SVALOR_CREDITO, 0) - NVL(MICONTRACUENTAS_SVALOR_DEBITO, 0) < 0 THEN
                  MI_VALORDEBITO := NVL(MICONTRACUENTAS_SVALOR_DEBITO, 0) - NVL(MICONTRACUENTAS_SVALOR_CREDITO, 0) - NVL(MICONTRA_SDEBITO_AFECTADO, 0) + NVL(MICONTRA_SCREDITO_AFECTADO, 0);
                  MI_VALORCREDITO := 0;
                  IF MI_VALORDEBITO < 0 THEN
                     MI_VALORDEBITO := 0;
                  END IF;
               ELSE
                  MI_VALORDEBITO := 0;
                  MI_VALORCREDITO := NVL(MICONTRACUENTAS_SVALOR_CREDITO, 0) - NVL(MICONTRACUENTAS_SVALOR_DEBITO, 0) - NVL(MICONTRA_SCREDITO_AFECTADO, 0) + NVL(MICONTRA_SDEBITO_AFECTADO, 0);
                  IF MI_VALORCREDITO < 0 THEN
                     MI_VALORCREDITO := 0;
                  END IF;
               END IF;
            ELSE
               /*
               MICONTRA_SALDO:=CASE WHEN MI_CNTDAD_TERCEROS =-1 THEN MICONTRA_SALDO ELSE UN_VALORAPAGAR END; 
               MI_VALORDEBITO := CASE WHEN UN_CANTIDAD > 1 THEN NVL(MICONTRACUENTAS_SVALOR_CREDITO, 0) - NVL(MICONTRA_SDEBITO_AFECTADO, 0) ELSE 
                                                           CASE WHEN NVL(MICONTRACUENTAS_SVALOR_CREDITO, 0) - NVL(MICONTRA_SDEBITO_AFECTADO, 0) > 0 
                                                                THEN CASE WHEN MICONTRA_SALDO <> NVL(MICONTRACUENTAS_SVALOR_CREDITO, 0) 
                                                                     THEN MICONTRA_SALDO ELSE NVL(MICONTRACUENTAS_SVALOR_CREDITO, 0) END ELSE 0 END END;
               */                                                      
               IF(MI_VALOR_PAR_CAUSA_AUTO = 'SI') THEN
                    MI_VALORDEBITO  := NVL(MICONTRACUENTAS_SVALOR_CREDITO, 0); 
                    MI_VALORCREDITO := NVL(MICONTRACUENTAS_SVALOR_DEBITO , 0);
               ELSE              
                    MI_VALORDEBITO  := NVL(MICONTRACUENTAS_SVALOR_CREDITO, 0) - NVL(MICONTRA_SDEBITO_AFECTADO , 0) + NVL(MICONTRA_SCREDITO_AFECTADO, 0); 
                    MI_VALORCREDITO := NVL(MICONTRACUENTAS_SVALOR_DEBITO , 0) - NVL(MICONTRA_SCREDITO_AFECTADO, 0);
               END IF;

              --(CC:766_CFBARRERA: Ajuste cuando se realice un egreso y se afecta por un valor parcial
              -- tome el valor para la cuenta 24)
               IF  UN_VALORAPAGAR IS NOT NULL AND MI_CONTADOR_PARCIAL = 1 AND UN_VALORAPAGAR < MI_VALORDEBITO THEN -- jm mod cc 4466 cuando hay varios comprobantes 
                    MI_VALORDEBITO := UN_VALORAPAGAR; -- JM mod CC 4311 lo recate del CC original  y si daña algo es culpa de paulis 
                    MI_VALOR := UN_VALORAPAGAR;
               END IF;--(CC:766_CFBARRERA_FIN).

               IF MI_VALORDEBITO < 0 THEN
                  MI_VALORDEBITO := 0;
               END IF;
               IF MI_VALORCREDITO < 0 THEN
                  MI_VALORCREDITO := 0;
               END IF;
            END IF;
            IF MI_VALORDEBITO + MI_VALORCREDITO > 0 THEN
                IF MICONTRACUENTAS_CLASECUENTA = 'E' OR MICONTRACUENTAS_CLASECUENTA = 'P' THEN 
                BEGIN
                      /*IF(MI_VALOR_PAR_CAUSA_AUTO = 'SI')THEN
                        MICONTRACUENTAS_CENTRO_COSTO := MIBANCO_CENTROCOSTO;
                        MICONTRACUENTAS_REFERENCIA := MIBANCO_REFERENCIA;
                      END IF;*/
                      MI_CAMPOS := 'COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CONSECUTIVO,NRO_DOCUMENTO,CUENTA,
                                    FECHA,NATURALEZA,DESCRIPCION,VALOR_DEBITO,VALOR_CREDITO,
                                    EJECUCION_DEBITO,BASE_GRAVABLE,CENTRO_COSTO,TERCERO,SUCURSAL,
                                    AUXILIAR,FUENTE_RECURSO,REFERENCIA,ANO_AFECT,TIPO_CPTE_AFECT,CMPTE_AFECTADO,CONSECUTIVOAFECTADO,CREATED_BY,DATE_CREATED';

                      MI_VALORES := '''' || UN_COMPANIA || '''
                                    ,' || UN_ANO || '
                                    ,''' || UN_TIPO_CPTE || '''
                                    ,' || UN_COMPROBANTE || '
                                    ,' || MI_CONSECUTIVODET || '
                                    ,'''||MI_NRODOCUMENTO||'''
                                    ,''' || MICONTRACUENTAS_CUENTA || '''
                                    ,TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                                    ,''' ||  MICONTRACUENTAS_NATURALEZA || '''
                                    ,''' || MI_DESCRIPCION || '''
                                    ,' || CASE WHEN MI_VALORDEBITO > 0 THEN MI_VALORDEBITO ELSE 0 END ||'
                                    ,' || CASE WHEN MI_VALORCREDITO > 0 THEN MI_VALORCREDITO ELSE 0 END ||'
                                    ,0
                                    ,' || MI_VALORPARCIAL || '
                                    ,''' || MICONTRACUENTAS_CENTRO_COSTO || '''
                                    ,''' || MICONTRACUENTAS_TERCERO || '''
                                    ,''' || MICONTRACUENTAS_SUCURSAL || '''
                                    ,''' || MICONTRACUENTAS_AUXILIAR || '''
                                    ,''' || MICONTRACUENTAS_FUENTE || '''
                                    ,''' || MICONTRACUENTAS_REFERENCIA || '''
                                    ,' || MI_ANO || '
                                    ,''' || MI_TIPO || '''
                                    ,' || MI_NUMERO || '
                                    ,' || MICONTRACUENTAS_CONSECUTIVO || '
                                    ,''' || UN_USUARIO || '''
                                    , SYSDATE';    

                              MI_PCKDATOS := PCK_DATOS.FC_ACME(
                                  UN_TABLA => 'DETALLE_COMPROBANTE_CNT', 
                                  UN_ACCION => 'I', 
                                  UN_CAMPOS => MI_CAMPOS, 
                                  UN_VALORES => MI_VALORES
                                );
                              EXCEPTION
                                WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                  PCK_ERR_MSG.RAISE_WITH_MSG(
                                    UN_EXC_COD => SQLCODE,
                                    UN_TABLAERROR => 'DETALLE_COMPROBANTE_CNT',
                                    UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_CREARDETPPTALVARIOS
                                  ); 
                              END;
                        END IF;
                        MI_CONSECUTIVODET := MI_CONSECUTIVODET + 1;
                      /*
                       IF INSTR('PCEYN', MICONTRACUENTAS_CLASECUENTA) > 0 THEN

                           BEGIN
                              MI_CAMPOS := 'DEBITOSAFECTADOS = '|| CASE WHEN UN_CLASE = 'E' OR UN_CLASE = 'R' OR UN_CLASE = 'G' THEN
                                                                    ' CASE WHEN DEBITOSAFECTADOS IS NULL THEN 0 ELSE DEBITOSAFECTADOS END + '|| MI_VALORDEBITO ||'' ELSE '0' END || '+'

                                                                     ||CASE WHEN (UN_CLASE = 'S' OR UN_CLASE = 'I' OR UN_CLASE = 'N') AND MI_VALORCREDITO  <> 0 THEN
                                                                     ' CASE WHEN DEBITOSAFECTADOS IS NULL THEN 0 ELSE DEBITOSAFECTADOS END + ' || MI_VALORDEBITO ||'' ELSE '0' END ||'    

                                         ,CREDITOSAFECTADOS =  ' || CASE WHEN UN_CLASE = 'E' OR UN_CLASE = 'R' OR UN_CLASE = 'G' THEN 
                                                                   ' CASE WHEN CREDITOSAFECTADOS IS NULL THEN 0 ELSE CREDITOSAFECTADOS END + '|| MI_VALORCREDITO ||'' ELSE '0' END;

                              MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || ''' 
                                              AND ANO    = ' || MI_ANO || ' 
                                              AND TIPO   = ''' || MI_TIPO || ''' 
                                              AND NUMERO = ' || MI_NUMERO;  

                              MI_PCKDATOS := PCK_DATOS.FC_ACME(
                                UN_TABLA => 'COMPROBANTE_CNT', 
                                UN_ACCION => 'M', 
                                UN_CAMPOS => MI_CAMPOS, 
                                UN_CONDICION => MI_CONDICION
                              );
                            EXCEPTION
                              WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                PCK_ERR_MSG.RAISE_WITH_MSG(
                                  UN_EXC_COD => SQLCODE,
                                  UN_TABLAERROR => 'COMPROBANTE_CNT',
                                  UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_ACTPPTO_DETCONTABLE
                                ); 
                            END;

                              BEGIN
                                MI_CAMPOS := 'DEBITO_AFECTADO ='||  CASE WHEN UN_CLASE = 'E' OR UN_CLASE = 'R' OR UN_CLASE = 'G' THEN
                                                                    'CASE WHEN DEBITO_AFECTADO IS NULL THEN 0 ELSE DEBITO_AFECTADO END +' || MI_VALORDEBITO||'' ELSE '0' END || '+'
                                                                    || CASE WHEN UN_CLASE = 'S' OR UN_CLASE = 'I' OR UN_CLASE = 'N' AND MI_VALORCREDITO <> 0 THEN
                                                                    'CASE WHEN DEBITO_AFECTADO IS NULL THEN 0 ELSE DEBITO_AFECTADO END + ' || MI_VALORDEBITO ||'' ELSE '0' END ||'

                                            ,CREDITO_AFECTADO = '|| CASE WHEN UN_CLASE = 'E' OR UN_CLASE = 'R' OR UN_CLASE = 'G' THEN
                                                                    'CASE WHEN CREDITO_AFECTADO IS NULL THEN 0 ELSE CREDITO_AFECTADO END + ' || MI_VALORCREDITO ||'' ELSE '0' END; 

                                MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || ''' 
                                                AND ANO = ' ||MI_ANO || ' 
                                                AND TIPO_CPTE = ''' || MI_TIPO || ''' 
                                                AND COMPROBANTE = ' || MI_NUMERO || ' 
                                                AND CONSECUTIVO = ' || MICONTRACUENTAS_CONSECUTIVO ||'
                                                AND CUENTA      = '|| MICONTRACUENTAS_CUENTA;
                                MI_PCKDATOS := PCK_DATOS.FC_ACME(
                                  UN_TABLA => 'DETALLE_COMPROBANTE_CNT', 
                                  UN_ACCION => 'M', 
                                  UN_CAMPOS => MI_CAMPOS, 
                                  UN_CONDICION => MI_CONDICION
                                );
                              EXCEPTION
                                WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                  PCK_ERR_MSG.RAISE_WITH_MSG(
                                    UN_EXC_COD => SQLCODE,
                                    UN_TABLAERROR => 'DETALLE_COMPROBANTE_CNT',
                                    UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_ACTPPTO_DETCONTABLE
                                  ); 
                             END;
                        END IF;    
                        */
                    END IF;     
               END IF;
           END IF;
       END;
    END LOOP procesar_contracuentas;
 END;

END LOOP datos_comprobante_afectar; 

<<actualizar_cheque>>
 FOR RS 
     IN (
        SELECT COMPROBANTE_CNTBANCOS.CUENTA,NVL(COMPROBANTE_CNTBANCOS.NUMEROCHEQUE,0) CHEQUE
        FROM COMPROBANTE_CNTBANCOS 
        LEFT JOIN PLAN_CONTABLE  
        ON COMPROBANTE_CNTBANCOS.CUENTA = PLAN_CONTABLE.CODIGO 
        AND COMPROBANTE_CNTBANCOS.ANO = PLAN_CONTABLE.ANO 
        AND COMPROBANTE_CNTBANCOS.COMPANIA = PLAN_CONTABLE.COMPANIA
        WHERE COMPROBANTE_CNTBANCOS.COMPANIA   = UN_COMPANIA 
          AND COMPROBANTE_CNTBANCOS.ANO        = UN_ANO 
          AND COMPROBANTE_CNTBANCOS.TIPO       = UN_TIPO_CPTE
          AND COMPROBANTE_CNTBANCOS.NUMERO     = UN_COMPROBANTE
          ORDER BY COMPROBANTE_CNTBANCOS.ANO, 
                   COMPROBANTE_CNTBANCOS.TIPO, 
                   COMPROBANTE_CNTBANCOS.NUMERO, 
                   COMPROBANTE_CNTBANCOS.CUENTA)
       LOOP
         BEGIN         
              MI_CAMPOS :=    'CHEQUE    = ' || RS.CHEQUE ||'';
              MI_CONDICION := 'COMPANIA  = '''||UN_COMPANIA||''' 
                               AND ANO   = ' || UN_ANO || '
                               AND CODIGO=''' || RS.CUENTA || '''';

              MI_RTA := PCK_DATOS.FC_ACME(
                        'PLAN_CONTABLE','M', MI_CAMPOS, NULL, NULL, MI_CONDICION, NULL, NULL, NULL, NULL);                                  
         END;
 END LOOP actualizar_cheque; 

  BEGIN
         MI_TABLA := 'COMPROBANTE_CNT';
         MI_CAMPOS := 'VLRAGIRAR = '||MI_VALOR || ',DESCRIPCION = '''||SUBSTR(MI_TOTALDESCRIPCION, 0,LENGTH(MI_TOTALDESCRIPCION) -1)||''',TEXTO='''|| MI_TEXTO ||''',CUENTA = ''' || MIBANCO_CUENTA || '''';

         MI_CONDICION := '      COMPANIA    = '''||UN_COMPANIA||
                         ''' AND ANO         = '||UN_ANO||
                          '  AND TIPO        = '''||UN_TIPO_CPTE||
                         ''' AND NUMERO      = '||UN_COMPROBANTE;
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOS,
                                                  UN_CONDICION => MI_CONDICION);    
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;     

RETURN -1;

END FC_GENERAREGRESOS;
  -- 2
  PROCEDURE PR_ACTUALIZARAFECTADOS
    /*
      NAME              : PR_ACTUALIZARAFECTADOS 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MIGRADOR     : 20/04/2017
      TIME              : 02:02 PM
      SOURCE MODULE     : CONTABILIDAD
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : FUNCION QUE ACTUALIZA CREDITOS Y DEBITOS AFECTADOS EN COMPROBANTE_CNT Y EN DETALLE_COMPROBANTE_CNT.
      PARAMETERS        : UN_COMPANIA           => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_CTIPOCPTEAFECTACT  => TIPO DE COMPROBANTE AFECTADO DEL REGISTRO ACTIVO
                          UN_CTIPOCPTEAFECT     => TIPO DE COMPROBANTE AFECTADO DEL REGISTRO 
                          UN_CCMPTEAFECTADO     => COMPROBANTE AFECTADO DEL REGISTRO 
                          UN_CCMPTEAFECTADOACT  => COMPROBANTE AFECTADO DEL REGISTRO ACTIVO
                          UN_CVALORDEBITO       => VALOR DEBITO DEL REGISTRO 
                          UN_CVALORDEBITOACT    => VALOR DEBITO DEL REGISTRO ACTIVO
                          UN_CVALORCREDITO      => VALOR CREDITO DEL REGISTRO 
                          UN_CVALORCREDITOACT   => VALOR CREDITO DEL REGISTRO ACTIVO
                          UN_CLASE              => CLASE DEL COMPROBANTE
                          UN_CUENTA             => CUENTA DEL COMPROBANTE
                          UN_CONSECUTIVO        => NUMERO DEL CONSECUTIVO
                          UN_CLASECUENTA        => CLASE DE LA CUENTA
      MODIFICATIONS     : 
      @NAME:  actualizarAfectados
      @METHOD:  PUT
    */ 
    (
      UN_COMPANIA            IN  PCK_SUBTIPOS.TI_COMPANIA
     ,UN_ANO_AFECT           IN  PCK_SUBTIPOS.TI_ANIO
     ,UN_ANO                 IN  PCK_SUBTIPOS.TI_ANIO
     ,UN_COMPROBANTE         IN  PCK_SUBTIPOS.TI_DOBLE 
     ,UN_TIPO_CPTE           IN  DETALLE_COMPROBANTE_CNT.TIPO_CPTE%TYPE 
     ,UN_CTIPOCPTEAFECT      IN  DETALLE_COMPROBANTE_CNT.TIPO_CPTE_AFECT%TYPE 
     ,UN_CCMPTEAFECTADO      IN  PCK_SUBTIPOS.TI_DOBLE 
     ,UN_CVALORDEBITO        IN  PCK_SUBTIPOS.TI_DOBLE 
     ,UN_CVALORDEBITOACT     IN  PCK_SUBTIPOS.TI_DOBLE 
     ,UN_CVALORCREDITO       IN  PCK_SUBTIPOS.TI_DOBLE 
     ,UN_CVALORCREDITOACT    IN  PCK_SUBTIPOS.TI_DOBLE 
     ,UN_CUENTA              IN  VARCHAR2 
     ,UN_CONSECUTIVO         IN PCK_SUBTIPOS.TI_ENTERO_LARGO
     ,UN_USUARIO             IN  PCK_SUBTIPOS.TI_USUARIO
    )
  AS
    MI_ANIO             PCK_SUBTIPOS.TI_ANIO;
    MI_ANO              PCK_SUBTIPOS.TI_ANIO;
    MI_CLASECONTABLE    PCK_SUBTIPOS.TI_LOGICO;
    MI_CLASECONTABLE2   PCK_SUBTIPOS.TI_LOGICO;
    MI_AUX                    PCK_SUBTIPOS.TI_TEXTO1;
    MI_DEB              VARCHAR2(4000);
    MI_CRE              VARCHAR2(4000);
    MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
    MI_MERGEUSING       PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE      PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE      PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_INCUENTA         PCK_SUBTIPOS.TI_ENTERO;
    MI_CLASE            PCK_SUBTIPOS.TI_CLASECOMPROPPTO;
    MI_CLASE_CUENTA     PCK_SUBTIPOS.TI_CLASECUENTACONTA;
    MI_NUMERO           PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT; 
    MI_TIPO             PCK_SUBTIPOS.TI_TIPOCOMPROBANTE; 
    MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
  BEGIN
    BEGIN
      SELECT CLASE_CONTABLE 
      INTO   MI_CLASE
      FROM   TIPO_COMPROBANTE
      WHERE  COMPANIA = UN_COMPANIA
        AND  CODIGO   = UN_TIPO_CPTE;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        NULL;
        --MI_ANIO := 0;
    END;
    BEGIN
      SELECT CLASECUENTA 
      INTO   MI_CLASE_CUENTA
      FROM   PLAN_CONTABLE
      WHERE  COMPANIA  = UN_COMPANIA
        AND  ANO       = UN_ANO_AFECT
        AND  CODIGO    = UN_CUENTA;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        NULL;
        --MI_ANIO := 0;
      END;

    MI_ANIO:=UN_ANO_AFECT;
    MI_INCUENTA := PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                                                   UN_NOMBRE    => 'GIRAR CUENTAS DE IMPUESTOS EN EGRESO',
                                                                                   UN_MODULO    => PCK_DATOS.MODULOCONTABILIDAD,
                                                                                   UN_FECHA_PAR => SYSDATE),'NO') = 'SI',
                                         UN_SI        => INSTR('PCEYNAI',MI_CLASE_CUENTA,1),
                                         UN_NO        => INSTR('PCEYNA',MI_CLASE_CUENTA,1));

    --IF NVL(UN_CTIPOCPTEAFECTACT,'')='' OR NVL(UN_CCMPTEAFECTADOACT,'')='' THEN


     IF UN_CTIPOCPTEAFECT IS NOT NULL OR UN_CCMPTEAFECTADO IS NOT NULL THEN 

      IF (UN_CVALORDEBITO <> UN_CVALORDEBITOACT) OR (UN_CVALORCREDITO <> UN_CVALORCREDITOACT) OR (MI_INCUENTA > 0) THEN


        MI_DEB:= CASE WHEN INSTR('EARG',MI_CLASE)<>0 THEN  UN_CVALORDEBITOACT || '-' || UN_CVALORDEBITO ELSE '0' END;      

        MI_DEB := MI_DEB || '+' || CASE WHEN INSTR('SINJB',MI_CLASE)<>0 AND  UN_CVALORCREDITOACT || '-' || UN_CVALORCREDITO <> '0' THEN    
                  UN_CVALORCREDITOACT ||' - '|| UN_CVALORCREDITO ELSE '0' END; 

        MI_CRE:=CASE WHEN INSTR('EARG',MI_CLASE)<>0 THEN UN_CVALORCREDITOACT||' - '||UN_CVALORCREDITO ELSE '0' END;

        BEGIN
          BEGIN
            MI_TABLA := 'COMPROBANTE_CNT';
            MI_CAMPOS := 'DEBITOSAFECTADOS = DEBITOSAFECTADOS+'||MI_DEB||', CREDITOSAFECTADOS = CREDITOSAFECTADOS +'||MI_CRE;
            MI_CONDICION := '      COMPANIA = '''||UN_COMPANIA||
                            '''AND ANO      = '||MI_ANIO||
                            '  AND TIPO     = '''||UN_CTIPOCPTEAFECT||
                            '''AND NUMERO   = '||UN_CCMPTEAFECTADO ;
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOS,
                                                  UN_CONDICION => MI_CONDICION);    
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
          MI_MSGERROR(1).CLAVE := 'CONDICION';
          MI_MSGERROR(1).VALOR :='COMPANIA = '''||UN_COMPANIA||
                            ''', ANO = '||MI_ANIO||
                            ', TIPO = '''||UN_CTIPOCPTEAFECT||
                            ',CONSECUTIVO ='|| UN_CONSECUTIVO ||
                            ''' y NUMERO = '||UN_CCMPTEAFECTADO;
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_ACT_CPTECNT,
              UN_REEMPLAZOS => MI_MSGERROR
           );
        END;
        -- INI descomentado por JM CC 4311
    IF MI_CLASE NOT IN ('N','I','B') THEN          
        MI_DEB:= CASE WHEN INSTR('EARG',MI_CLASE)<>0 THEN  UN_CVALORDEBITOACT || '-' || UN_CVALORDEBITO ELSE '0' END;
        MI_DEB := MI_DEB || '+' || CASE WHEN INSTR('SINJB',MI_CLASE)<>0 AND  UN_CVALORCREDITOACT || '-' || UN_CVALORCREDITO <> '0' THEN
                  UN_CVALORCREDITOACT ||' - '|| UN_CVALORCREDITO ELSE '0' END;
        MI_CRE:=CASE WHEN INSTR('EARG',MI_CLASE)<>0 THEN UN_CVALORCREDITOACT||' - '||UN_CVALORCREDITO ELSE '0' END;

        BEGIN
          BEGIN
            MI_TABLA := 'DETALLE_COMPROBANTE_CNT';
            MI_CAMPOS := 'DEBITO_AFECTADO = DEBITO_AFECTADO+'||MI_DEB||', CREDITO_AFECTADO = CREDITO_AFECTADO+'||MI_CRE;
            MI_CONDICION := '      COMPANIA    = '''||UN_COMPANIA||
                            '''AND ANO         = '||MI_ANIO||
                            '  AND TIPO_CPTE   = '''||UN_CTIPOCPTEAFECT||
                            '''AND COMPROBANTE = '||UN_CCMPTEAFECTADO||
                            '  AND CUENTA      = '''||UN_CUENTA||
                            '''AND CONSECUTIVO = '''||UN_CONSECUTIVO||'''';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOS,
                                                  UN_CONDICION => MI_CONDICION);    
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
          MI_MSGERROR(1).CLAVE := 'CONDICION';
          MI_MSGERROR(1).VALOR :='COMPANIA = '||UN_COMPANIA||
                            ', ANO = '||MI_ANIO||
                            ', TIPO_CPTE = '||UN_CTIPOCPTEAFECT||
                            ', COMPROBANTE = '||UN_CCMPTEAFECTADO||
                            ', CUENTA = '||UN_CUENTA||
                            ', CONSECUTIVO = '||UN_CONSECUTIVO;
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_DEBCRED,
              UN_REEMPLAZOS => MI_MSGERROR
            );
        END;
    END IF;
        -- FIN descomentado por JM CC 4311 no es por el tiguere es que estaba sumando varias veces el mismo valor 
        -- se comenta esta lineas debido a que en el trigger AIUD_DETALLE_COMPROBANTE_PPTAL se realiza el control del saldo del rubro
        IF MI_CLASE = 'A' OR MI_CLASE = 'J' THEN
          BEGIN
            BEGIN
              MI_TABLA := 'COMPROBANTE_CNT';
              MI_MERGEUSING := 'SELECT COMPANIA,ANO,TIPO_CPTE_AFECT,CMPTE_AFECTADO, CONSECUTIVOAFECTADO
                                FROM   DETALLE_COMPROBANTE_CNT
                                WHERE  COMPANIA        = '''||UN_COMPANIA||'''
                                  AND  ANO             = '  ||MI_ANIO||'
                                  AND  TIPO_CPTE       = '''||UN_CTIPOCPTEAFECT||'''
                                  AND  COMPROBANTE     = '  ||UN_CCMPTEAFECTADO||'
                                  AND  CONSECUTIVO     = '  ||UN_CONSECUTIVO ||'
                                  AND  CUENTA          = '''||UN_CUENTA||'''
                                  AND  TIPO_CPTE_AFECT IS NOT NULL
                                  AND  CMPTE_AFECTADO  IS NOT NULL';
              MI_MERGEENLACE := '    TABLA.COMPANIA    = VISTA.COMPANIA 
                                 AND TABLA.ANO         = VISTA.ANO
                                 AND TABLA.TIPO        = VISTA.TIPO_CPTE_AFECT 
                                 AND TABLA.NUMERO      = VISTA.CMPTE_AFECTADO';
              MI_MERGEEXISTE := 'UPDATE 
                                   SET  TABLA.DEBITOSAFECTADOS  = TABLA.DEBITOSAFECTADOS - ABS('||UN_CVALORDEBITO||' + '||UN_CVALORDEBITOACT||')
                                       ,TABLA.CREDITOSAFECTADOS = TABLA.CREDITOSAFECTADOS - ABS('||UN_CVALORCREDITO||' + '||UN_CVALORCREDITOACT||')';
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA,
                                                    UN_ACCION      => 'MM',
                                                    UN_MERGEUSING  => MI_MERGEUSING,
                                                    UN_MERGEENLACE => MI_MERGEENLACE,
                                                    UN_MERGEEXISTE => MI_MERGEEXISTE);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
              MI_MSGERROR(1).CLAVE := 'CONDICION';
              MI_MSGERROR(1).VALOR :='COMPANIA = '||UN_COMPANIA||
                                     ', ANO = '||MI_ANIO||
                                     ', TIPO_CPTE = '||UN_CTIPOCPTEAFECT||
                                     ', COMPROBANTE = '||UN_CCMPTEAFECTADO||
                                     ', CUENTA = '||UN_CUENTA||
                                     ', TIPO_CPTE_AFECT IS NOT NULL y CMPTE_AFECTADO IS NOT NULL';
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE,
                  UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_ACT_CPTECNT,
                  UN_REEMPLAZOS => MI_MSGERROR
                );
          END;
        /*  BEGIN
            BEGIN
              MI_TABLA := 'DETALLE_COMPROBANTE_CNT';
              MI_MERGEUSING := 'SELECT COMPANIA,ANO,TIPO_CPTE_AFECT,CMPTE_AFECTADO,CUENTA,CONSECUTIVOAFECTADO
                                FROM   DETALLE_COMPROBANTE_CNT
                                WHERE  COMPANIA        = '''||UN_COMPANIA||'''
                                  AND  ANO             = '  ||MI_ANIO||'
                                  AND  TIPO_CPTE       = '''||UN_CTIPOCPTEAFECT||'''
                                  AND  COMPROBANTE     = '  ||UN_CCMPTEAFECTADO||'
                                  AND  CONSECUTIVO     = '  ||UN_CONSECUTIVO ||'
                                  AND  CUENTA          = '''||UN_CUENTA||'''
                                  AND  TIPO_CPTE_AFECT IS NOT NULL
                                  AND  CMPTE_AFECTADO  IS NOT NULL';
              MI_MERGEENLACE := '    TABLA.COMPANIA    = VISTA.COMPANIA 
                                 AND TABLA.ANO         = VISTA.ANO  
                                 AND TABLA.TIPO_CPTE   = VISTA.TIPO_CPTE_AFECT 
                                 AND TABLA.COMPROBANTE = VISTA.CMPTE_AFECTADO 
                                 AND TABLA.CUENTA      = VISTA.CUENTA
                                 AND TABLA.CONSECUTIVO = VISTA.CONSECUTIVOAFECTADO';
              MI_MERGEEXISTE := ' UPDATE 
                                    SET TABLA.DEBITO_AFECTADO  = TABLA.DEBITO_AFECTADO  - ABS(' || UN_CVALORDEBITO  ||' + ' || UN_CVALORDEBITOACT||')
                                       ,TABLA.CREDITO_AFECTADO = TABLA.CREDITO_AFECTADO - ABS(' || UN_CVALORCREDITO ||' + ' || UN_CVALORCREDITOACT||')';
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA,
                                                    UN_ACCION      => 'MM',
                                                    UN_MERGEUSING  => MI_MERGEUSING,
                                                    UN_MERGEENLACE => MI_MERGEENLACE,
                                                    UN_MERGEEXISTE => MI_MERGEEXISTE);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
              MI_MSGERROR(1).CLAVE := 'CONDICION';
              MI_MSGERROR(1).VALOR :='COMPANIA = '||UN_COMPANIA||
                                     ', ANO = '||MI_ANIO||
                                     ', TIPO_CPTE = '||UN_CTIPOCPTEAFECT||
                                     ', COMPROBANTE = '||UN_CCMPTEAFECTADO||
                                     ', CUENTA = '||UN_CUENTA||
                                     ', TIPO_CPTE_AFECT IS NOT NULL y CMPTE_AFECTADO IS NOT NULL';
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE,
                  UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_DEBCRED,
                  UN_REEMPLAZOS => MI_MSGERROR
                );
          END;*/  -- se comenta esta lineas debido a que en el trigger AIUD_DETALLE_COMPROBANTE_PPTAL se realiza el control del saldo del rubro
        ELSIF MI_CLASE = 'X' THEN
        BEGIN
            BEGIN
              MI_TABLA := 'COMPROBANTE_CNT';
              MI_MERGEUSING := 'SELECT COMPANIA,ANO,TIPO_CPTE_AFECT,CMPTE_AFECTADO, CONSECUTIVOAFECTADO
                                FROM   DETALLE_COMPROBANTE_CNT
                                WHERE  COMPANIA        = '''||UN_COMPANIA||'''
                                  AND  ANO             = '  ||MI_ANIO||'
                                  AND  TIPO_CPTE       = '''||UN_TIPO_CPTE||'''
                                  AND  COMPROBANTE     = '  ||UN_COMPROBANTE||'
                                  AND  CONSECUTIVO     = '  ||UN_CONSECUTIVO ||'
                                  AND  CUENTA          = '''||UN_CUENTA||'''
                                  AND  TIPO_CPTE_AFECT IS NOT NULL
                                  AND  CMPTE_AFECTADO  IS NOT NULL';
              MI_MERGEENLACE := '    TABLA.COMPANIA    = VISTA.COMPANIA
                                 AND TABLA.ANO         = VISTA.ANO
                                 AND TABLA.TIPO        = VISTA.TIPO_CPTE_AFECT
                                 AND TABLA.NUMERO      = VISTA.CMPTE_AFECTADO';
              MI_MERGEEXISTE := 'UPDATE
                                   SET  TABLA.DEBITOSAFECTADOS  = TABLA.DEBITOSAFECTADOS + ABS('||UN_CVALORDEBITO||' + '||UN_CVALORDEBITOACT||') + ABS('||UN_CVALORCREDITO||' + '||UN_CVALORCREDITOACT||')';
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA,
                                                    UN_ACCION      => 'MM',
                                                    UN_MERGEUSING  => MI_MERGEUSING,
                                                    UN_MERGEENLACE => MI_MERGEENLACE,
                                                    UN_MERGEEXISTE => MI_MERGEEXISTE);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
              MI_MSGERROR(1).CLAVE := 'CONDICION';
              MI_MSGERROR(1).VALOR :='COMPANIA = '||UN_COMPANIA||
                                     ', ANO = '||MI_ANIO||
                                     ', TIPO_CPTE = '||UN_CTIPOCPTEAFECT||
                                     ', COMPROBANTE = '||UN_CCMPTEAFECTADO||
                                     ', CUENTA = '||UN_CUENTA||
                                     ', TIPO_CPTE_AFECT IS NOT NULL y CMPTE_AFECTADO IS NOT NULL';
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE,
                  UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_ACT_CPTECNT,
                  UN_REEMPLAZOS => MI_MSGERROR
                );
          END;
        END IF;
      END IF;
    --END IF;
      --INSERTAR LAS AFECTACIONES EN LA TABLA COMPROBANTE_CNTAFECTADOS
      --PRIMERO VERIFICA SI YA EXISTE EL DATO REGISTRADO EN LA TABLA 
      BEGIN
        SELECT  DISTINCT 'X'
        INTO    MI_AUX
        FROM    COMPROBANTE_CNTAFECTADOS
        WHERE   COMPANIA          = UN_COMPANIA
          AND   ANO               = UN_ANO
          AND   TIPO_CPTE         = UN_TIPO_CPTE
          AND   COMPROBANTE       = UN_COMPROBANTE
          AND   ANO_AFECT         = UN_ANO_AFECT
          AND   TIPO_CPTE_AFECT   = UN_CTIPOCPTEAFECT
          AND   COMPROBANTE_AFECT = UN_CCMPTEAFECTADO; 
        EXCEPTION WHEN NO_DATA_FOUND THEN
          BEGIN
            BEGIN
              MI_CAMPOS := 'COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,ANO_AFECT,TIPO_CPTE_AFECT,COMPROBANTE_AFECT,CREATED_BY,DATE_CREATED';
              MI_VALORES := '''' || UN_COMPANIA || ''',' || UN_ANO || ' , 
                            '''  || UN_TIPO_CPTE    || ''',' || UN_COMPROBANTE || ',' || UN_ANO_AFECT || ',
                            '''  || UN_CTIPOCPTEAFECT    || ''',' || UN_CCMPTEAFECTADO || ',
                            '''  || UN_USUARIO  || ''', SYSDATE';
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => 'COMPROBANTE_CNTAFECTADOS', 
                                                    UN_ACCION  => 'I', 
                                                    UN_CAMPOS  => MI_CAMPOS, 
                                                    UN_VALORES => MI_VALORES);                 
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE,
                UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_INSER_CPTAFECTADOS,
                UN_TABLAERROR => 'COMPROBANTE_CNTAFECTADOS'
              );
          END;  
      END;

  END IF;  

  END PR_ACTUALIZARAFECTADOS;

  -- 3
  FUNCTION FC_ACTUALIZARCHEQUES
    /*
      NAME              : FC_ACTUALIZARCHEQUES 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICEÑO
      DATE MIGRADOR     : 21/04/2017
      TIME              : 09:31 AM
      SOURCE MODULE     : CONTABILIDAD
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : FUNCION QUE ACTUALIZA CHEQUES EN PLAN_CONTABLE O EN CHEQUERA DEPENDIENDO DE SI SE TIENE 
                          O NO CONTROL DE CHEQUES.
      PARAMETERS        : UN_COMPANIA           => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_CONTROLCHEQUERA    => 'SI' O 'NO' DEPENDIENDO DE SI SE TIENE CONTROL DE CHEQUES
                          UN_CUENTA             => CUENTA DEL DETALLE_COMPROBANTE_CNT
                          UN_ANIO               => ANIO EN EL QUE SE ESTA TRABAJANDO
                          UN_CNRODOCUMENTO      => NUMERO DE DOCUMENTO DEL DETALLE_COMPROBANTE_CNT 
      MODIFICATIONS     : 
      @NAME:  actualizarCheques
      @METHOD:  GET
    */ 
  (

   UN_COMPANIA           IN  PCK_SUBTIPOS.TI_COMPANIA 
   ,UN_CUENTA             IN  DETALLE_COMPROBANTE_CNT.CUENTA%TYPE
   ,UN_ANIO               IN  PCK_SUBTIPOS.TI_ANIO
   ,UN_CNRODOCUMENTO      IN  DETALLE_COMPROBANTE_CNT.NRO_DOCUMENTO%TYPE
  ) RETURN VARCHAR2
  AS
    MI_CHEQUE       PCK_SUBTIPOS.TI_DOBLE;
    MI_CHEQ         PCK_SUBTIPOS.TI_DOBLE;
    MI_CUENTA       CHEQUERA.CUENTA%TYPE;
    MI_NUMACTUAL    CHEQUERA.NUMACTUAL%TYPE;
    MI_DISPONIBLES  CHEQUERA.DISPONIBLES%TYPE;
    MI_ACTUAL       CHEQUERA.NUMCHEQUERA%TYPE;
    MI_ESTARNULL    PCK_SUBTIPOS.TI_LOGICO;
    MI_TABLA        PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
    MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_RETORNO      VARCHAR2(100 CHAR);
    MI_MANEJA_CHEQUERA   VARCHAR2(100 CHAR);
  BEGIN

  MI_MANEJA_CHEQUERA:=  NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                  UN_NOMBRE    => 'MANEJA CONTROL DE CHEQUERAS',
                                                  UN_MODULO    => PCK_DATOS.MODULOCONTABILIDAD,
                                                  UN_FECHA_PAR => SYSDATE),'NO');

    MI_ESTARNULL := 0;
    IF 'SI' = MI_MANEJA_CHEQUERA THEN
      BEGIN
        SELECT   CUENTA, NUMACTUAL,DISPONIBLES
                ,MAX(NUMCHEQUERA) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) ACTUAL
        INTO     MI_CUENTA,MI_NUMACTUAL,MI_DISPONIBLES,MI_ACTUAL
        FROM     CHEQUERA
        WHERE    CHEQUERA.COMPANIA = UN_COMPANIA
          AND    CHEQUERA.ANO      = UN_ANIO
          AND    CHEQUERA.CUENTA   = UN_COMPANIA 
          AND    NUMINICIAL        <> NUMACTUAL
        GROUP BY CUENTA,NUMACTUAL,DISPONIBLES
        ORDER BY MAX(NUMCHEQUERA) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) DESC;
        EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_ESTARNULL := -1;
      END;
      IF MI_ESTARNULL = 0 THEN
        MI_CHEQUE := NVL(MI_NUMACTUAL,0)-1;
        <<CHEQUESANULADOS>>
        FOR MI_RS IN (SELECT NUMANULAR
                      FROM   CHEQUES_ANULADOS
                      WHERE  CHEQUES_ANULADOS.COMPANIA    = UN_COMPANIA
                        AND  CHEQUES_ANULADOS.ANO         = UN_ANIO
                        AND  CHEQUES_ANULADOS.CUENTA      = UN_CUENTA
                        AND  CHEQUES_ANULADOS.NUMCHEQUERA = MI_ACTUAL
                        AND  CHEQUES_ANULADOS.NUMANULAR   = MI_CHEQUE) 
        LOOP
          MI_CHEQUE := MI_CHEQUE - 1;
        END LOOP CHEQUESANULADOS;
        BEGIN
          BEGIN
            MI_TABLA := 'CHEQUERA';
            MI_CAMPOS := 'NUMACTUAL = '||MI_CHEQUE;
            MI_CONDICION := '    COMPANIA    = '''||UN_COMPANIA||'''
                             AND ANO         = '  ||UN_ANIO||'
                             AND CUENTA      = '''||MI_CUENTA||'''
                             AND NUMCHEQUERA = '  ||MI_ACTUAL;
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOS,
                                                  UN_CONDICION => MI_CONDICION);  
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            MI_MSGERROR(1).CLAVE := 'CONDICION';
            MI_MSGERROR(1).VALOR :='COMPANIA = '||UN_COMPANIA||
                                   ', ANO = '||UN_ANIO||
                                   ', CUENTA = '||MI_CUENTA||
                                   ' y NUMCHEQUERA = '||MI_ACTUAL;
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE,
                UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_CHEQUERA,
                UN_REEMPLAZOS => MI_MSGERROR
              );
        END;
      END IF;
      MI_RETORNO := MI_CHEQUE ||','||MI_CUENTA;
    ELSE
      IF 'SI' = NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                          UN_NOMBRE    => 'GIRAR CUENTAS DE IMPUESTOS EN EGRESO',
                                          UN_MODULO    => PCK_DATOS.MODULOCONTABILIDAD,
                                          UN_FECHA_PAR => SYSDATE),'NO') THEN
        BEGIN
          SELECT CHEQUE 
          INTO   MI_CHEQ
          FROM   PLAN_CONTABLE
          WHERE  COMPANIA = UN_COMPANIA
          AND    ANO      = UN_ANIO
          AND    CODIGO   = UN_CUENTA;
          EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_CHEQ := 0;
        END;
        IF MI_CHEQ <> 0 AND UN_CNRODOCUMENTO <> ' ' AND MI_CHEQ = UN_CNRODOCUMENTO THEN
          BEGIN
            BEGIN
              MI_TABLA := 'PLAN_CONTABLE';
              MI_CAMPOS := 'CHEQUE = CHEQUE - 1';
              MI_CONDICION := '    COMPANIA    = '''||UN_COMPANIA||'''
                               AND ANO         = '  ||UN_ANIO||'
                               AND CODIGO      = '''||MI_CUENTA||'''
                               AND CHEQUE      > 0';
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                    UN_ACCION    => 'M',
                                                    UN_CAMPOS    => MI_CAMPOS,
                                                    UN_CONDICION => MI_CONDICION);  
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
              MI_MSGERROR(1).CLAVE := 'CONDICION';
              MI_MSGERROR(1).VALOR :='COMPANIA = '||UN_COMPANIA||
                                     ', ANO = '||UN_ANIO||
                                     ', CODIGO = '||MI_CUENTA||
                                     ' y CHEQUE > 0';
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE,
                  UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_CHEQUEPLANCON,
                  UN_REEMPLAZOS => MI_MSGERROR
                );
          END;
        END IF;
      END IF;
    END IF;
    RETURN MI_RETORNO;
  END FC_ACTUALIZARCHEQUES;

  -- 4
  PROCEDURE PR_CALCULAR_VALORAGIRAR
  /*
    NAME              : PR_CALCULAR_VALORAGIRAR 
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JESSICA LISSETH RAMIREZ BRICEÑO
    DATE MIGRADOR     : 21/04/2017
    TIME              : 11:32 AM
    SOURCE MODULE     : CONTABILIDAD
    MODIFIER          : JAVIER VILLATE
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : FUNCION QUE REALIZA ACTUALIZACIONES DEL VALOR A GIRAR.
    MODIFICATIONS     : SE MODIFICO A PROCEDIMIENTO YA QUE ESTE ES LLAMADO DESDE EL TRIGGER LLAMADO CIUD_DETALLE_COMPR_CNT_AFE    
    @NAME:  calcularValorAGirar
    @METHOD:  GET
    */
  (
    UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO                 IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPO                 IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
    UN_NUMERO               IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
    UN_VLRDOCUMENTO         IN PCK_SUBTIPOS.TI_DOBLE,
    UN_VLRGIRARDG           IN PCK_SUBTIPOS.TI_DOBLE
  )
  AS
    MI_VLRGIRAR         PCK_SUBTIPOS.TI_DOBLE;
    MI_VLRDOCUMENTO     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
    MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CLASE            PCK_SUBTIPOS.TI_CLASECUENTACONTA;
    MI_STRSQL           PCK_SUBTIPOS.TI_STRSQL;
  BEGIN
  IF UN_VLRGIRARDG <> 0 THEN
    MI_VLRGIRAR := UN_VLRGIRARDG;
  ELSE

  BEGIN                                           
       SELECT CLASE_CONTABLE
       INTO MI_CLASE
       FROM TIPO_COMPROBANTE
       WHERE COMPANIA=UN_COMPANIA 
       AND   CODIGO  =UN_TIPO;

      EXCEPTION WHEN NO_DATA_FOUND  THEN
          MI_CLASE := 'P';
      END ;

    MI_VLRGIRAR := PCK_CONTABILIDAD1.FC_CALCULARVLRGIRAR(UN_COMPANIA    => UN_COMPANIA,
                                                         UN_ANIO        => UN_ANIO,
                                                         UN_TIPO        => UN_TIPO,
                                                         UN_NUMERO      => UN_NUMERO,
                                                         UN_CLASE       => MI_CLASE,
                                                         UN_VALORAGIRAR => 0);
  END IF;
    BEGIN
      BEGIN
        MI_TABLA := 'COMPROBANTE_CNT';
        MI_CAMPOS := 'VLRAGIRAR = '||MI_VLRGIRAR ||
                     ', DEBITO = ' || MI_VLRGIRAR || 
                     ', CREDITO = ' || MI_VLRGIRAR || '';
        MI_CONDICION := '      COMPANIA = '''||UN_COMPANIA||
                        '''AND ANO      = '||UN_ANIO||
                        '  AND TIPO     = '''||UN_TIPO||
                        '''AND NUMERO   = '||UN_NUMERO;
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);    
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
      MI_MSGERROR(1).CLAVE := 'CAMPO';
      MI_MSGERROR(1).VALOR := 'VLRAGIRAR';
      MI_MSGERROR(2).CLAVE := 'CONDICION';
      MI_MSGERROR(2).VALOR :='COMPANIA = '''||UN_COMPANIA||
                        ''', ANO = '||UN_ANIO||
                        ', TIPO = '''||UN_TIPO||
                        ''' y NUMERO = '||UN_NUMERO;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_ACT_VLRGIRAR,
          UN_REEMPLAZOS => MI_MSGERROR
        );
    END;
    SELECT NVL(SUM(NVL(DETALLE_COMPROBANTE_CNT.VALOR_DEBITO,0)),0) VLRDOCUMENTO
      INTO MI_VLRDOCUMENTO
      FROM DETALLE_COMPROBANTE_CNT
      WHERE DETALLE_COMPROBANTE_CNT.COMPANIA  = UN_COMPANIA
        AND DETALLE_COMPROBANTE_CNT.ANO       = UN_ANIO
        AND TIPO_CPTE                         = UN_TIPO
        AND COMPROBANTE                       = UN_NUMERO;
     IF 'NO' = NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                        UN_NOMBRE    => 'PERMITIR MODIFICAR EL VALOR DEL DOCUMENTO EN EL HEADER',
                                        UN_MODULO    => PCK_DATOS.MODULOCONTABILIDAD,
                                        UN_FECHA_PAR => SYSDATE),'NO') THEN
--      MI_VLRDOCUMENTO := (REPLACE(UN_TOTALDEBITO,',',''));
--      MI_VLRDOCAUXILIAR := (REPLACE(UN_TOTALDEBITO,',',''));


     BEGIN
        BEGIN
          MI_TABLA := 'COMPROBANTE_CNT';
          MI_CAMPOS := 'VLR_DOCUMENTO     = '   ||NVL(MI_VLRDOCUMENTO,0);
          MI_CONDICION := '      COMPANIA = ''' ||UN_COMPANIA||
                          '''AND ANO      = '   ||UN_ANIO||
                          '  AND TIPO     = ''' ||UN_TIPO||
                          '''AND NUMERO   = '   ||UN_NUMERO;
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                UN_ACCION    => 'M',
                                                UN_CAMPOS    => MI_CAMPOS,
                                                UN_CONDICION => MI_CONDICION);    
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
        MI_MSGERROR(1).CLAVE := 'CAMPO';
        MI_MSGERROR(1).VALOR := 'VLR_DOCUMENTO';
        MI_MSGERROR(2).CLAVE := 'CONDICION';
        MI_MSGERROR(2).VALOR :='COMPANIA = '''||UN_COMPANIA||
                          ''', ANO = '||UN_ANIO||
                          ', TIPO = '''||UN_TIPO||
                          ''' y NUMERO = '||UN_NUMERO;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_ACT_VLRGIRAR,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END;
    END IF;
    IF 'C' = MI_CLASE AND '0' = UN_VLRDOCUMENTO THEN

      BEGIN
        BEGIN
          MI_TABLA := 'COMPROBANTE_CNT';
          MI_CAMPOS := 'VLR_DOCUMENTO = '||NVL(MI_VLRDOCUMENTO,0);
          MI_CONDICION := '      COMPANIA = '''||UN_COMPANIA||
                          '''AND ANO      = '||UN_ANIO||
                          '  AND TIPO     = '''||UN_TIPO||
                          '''AND NUMERO   = '||UN_NUMERO;
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                UN_ACCION    => 'M',
                                                UN_CAMPOS    => MI_CAMPOS,
                                                UN_CONDICION => MI_CONDICION);    
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
        MI_MSGERROR(1).CLAVE := 'CAMPO';
        MI_MSGERROR(1).VALOR := 'VLR_DOCUMENTO';
        MI_MSGERROR(2).CLAVE := 'CONDICION';
        MI_MSGERROR(2).VALOR :='COMPANIA = '''||UN_COMPANIA||
                          ''', ANO = '||UN_ANIO||
                          ', TIPO = '''||UN_TIPO||
                          ''' y NUMERO = '||UN_NUMERO;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_ACT_VLRGIRAR,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END;
    END IF;
  END PR_CALCULAR_VALORAGIRAR;

  FUNCTION FC_GENERARRTACONCILIADOSXPLANO
  /*
      NAME              : FC_GENERARRTACONCILIADOSXPLANO 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : LEYDI MILENA CORTÉS FORERO
      DATE MIGRADO      : 06,07,10,11,12,17,24/04/2017
      TIME              : 10:32 AM
      AUTHOR NODIFIED   : JOSE PASCUAL GOMEZ BLANCO
      DATE MIGRADO      : 06/09/2018
      TIME              : 08:16 AM
      SOURCE MODULE     : GENERAL 
      DESCRIPTION       : Función que permite generar la respuesta para cada fila que definieron conciliar a partir de la información registrada
                          en una plantilla Excel.
                          Se redefine totalmente para que funcione adecuadamente
                          Ruta: Panel Principal\Contabilidad\Movimientos\Conciliación por archivo Excel.
      PARAMETERS        : UN_COMPANIA           => Compañia de ingreso a la aplicación.  
                          UN_CUENTA             => ID de la cuenta a conciliar.
                          UN_CADENA             => Cadena que contiene los datos fila por fial separados por punto y coma (;)
                                                   de la información a conciliar a partir de la plantilla excel seleccionada.
                          UN_ELIMINARPARTIDAS   => Indicador que identifica si se deben eliminar partidas conciliatorias para 
                                                   el periodo seleccionado.
                          UN_MANINGPARTIDASCONC => Indicador definido a partir del valor del parámetro MANEJA INGRESO DE PARTIDAS CONCILIATORIAS.
                          UN_VALIDADOC          => Indicador para identificar si se debe validar el documento.
                          UN_COLVALINGRESO      => Número de la columna seleccionada para el valor ingreso. 
                          UN_COLVALCREDITO      => Número de la columna seleccionada para el valor egreso.
                          UN_COLDOCUMENTO       => Número de la columna seleccionada para el número de documento.
                          UN_COLERROR           => Número de la columna seleccionada para la columna error.
                          UN_COLFECHA           => Número de la columna seleccionada para la columna de fecha. 
                          UN_FECHA              => Fecha  indicada para hacer la concicliacion.
                          UN_FORMATOFECHA       => Es el formato en el que vienen las fechas
                          UN_FILAINI            => Número de la fila inicio de la información.
                          UN_USUARIO            => Código del usuario que realiza la conciliación.
      @NAME:  generarRtaConciliadosXPlano
      @METHOD:  GET
      */  
  (
    UN_COMPANIA               IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CUENTA                 IN VARCHAR2,
    UN_CADENA                 IN CLOB,
    UN_ELIMINARPARTIDAS       IN PCK_SUBTIPOS.TI_LOGICO,
    UN_MANINGPARTIDASCON      IN PCK_SUBTIPOS.TI_LOGICO,
    UN_VALIDADOC              IN PCK_SUBTIPOS.TI_LOGICO,
    UN_COLVALINGRESO          IN PCK_SUBTIPOS.TI_ENTERO, 
    UN_COLVALCREDITO          IN PCK_SUBTIPOS.TI_ENTERO, 
    UN_COLDOCUMENTO           IN PCK_SUBTIPOS.TI_ENTERO,
    UN_COLERROR               IN PCK_SUBTIPOS.TI_ENTERO,
    UN_COLFECHA               IN PCK_SUBTIPOS.TI_ENTERO,
    UN_FECHA                  IN DATE,
    UN_FORMATOFECHA           IN VARCHAR2,
    UN_FILAINI                IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
    UN_USUARIO                IN VARCHAR2    
  )RETURN CLOB 
AS 
    MI_ANIO           PCK_SUBTIPOS.TI_ANIO;
    MI_MES            PCK_SUBTIPOS.TI_MES;
    MI_CONSECUTIVO    PCK_SUBTIPOS.TI_ENTERO; 
    MI_CADENA         CLOB;
    MI_CADENA_REG     CLOB;
    MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
    MI_RTAACME        PCK_SUBTIPOS.TI_RTA_ACME;
    MI_MSGERROREXC    PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_MERGEUSING     PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE    PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE    PCK_SUBTIPOS.TI_MERGEEXISTE;  
    MI_REGISTROS      PCK_SYSMAN_UTL.T_SPLIT;
    MI_REG_CAMPOS     PCK_SYSMAN_UTL.T_SPLIT;
    MI_DEBITO         PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_CREDITO        PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_FECHA_REG      DATE;
    MI_DOCNUMERO      VARCHAR2(2000);
    MI_VALORDOC       PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_INDINGRESO     BOOLEAN;
    MI_FILA           PCK_SUBTIPOS.TI_ENTERO_LARGO;  
    MI_CONTROL        PCK_SUBTIPOS.TI_ENTERO_LARGO;  
    MI_ARCHSALIDA     VARCHAR2(2000);
    MI_FECHATEM       VARCHAR2(2000);
BEGIN
    MI_ANIO := TO_NUMBER(TO_CHAR(TO_DATE(UN_FECHA,'DD/MM/YYYY'),'YYYY'));
    MI_MES  := TO_NUMBER(TO_CHAR(TO_DATE(UN_FECHA,'DD/MM/YYYY'),'MM'));
    MI_FILA := UN_FILAINI;
    MI_CADENA :='';


    BEGIN
        BEGIN
            MI_CONDICION := 'COMPANIA    = ''' || UN_COMPANIA || '''
                         AND CUENTA      = ''' || UN_CUENTA   || '''
                         AND ANO         = '   || MI_ANIO     ;    
            MI_RTAACME := PCK_DATOS.FC_ACME(UN_TABLA     => 'TEMP_CONCILIACION',
                                          UN_ACCION    => 'E', 
                                          UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
        MI_MSGERROREXC(1).CLAVE := 'CUENTA';
        MI_MSGERROREXC(1).VALOR := UN_CUENTA;
        MI_MSGERROREXC(2).CLAVE := 'ANIO';
        MI_MSGERROREXC(2).VALOR := MI_ANIO;
        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_DEL_PARTCONC,
                                    UN_REEMPLAZOS => MI_MSGERROREXC,
                                    UN_TABLAERROR => 'TEMP_CONCILIACION'
        );
    END;

    IF UN_MANINGPARTIDASCON NOT IN (0) THEN
        IF UN_ELIMINARPARTIDAS NOT IN(0) THEN
            BEGIN
                BEGIN
                    MI_CONDICION := 'COMPANIA    = ''' || UN_COMPANIA || '''
                                 AND CUENTA      = ''' || UN_CUENTA   || '''
                                 AND ANO         = '   || MI_ANIO     || '
                                 AND MES_PARTIDA = '   || MI_MES ;    
                    MI_RTAACME := PCK_DATOS.FC_ACME(UN_TABLA     => 'PARTIDAS_CONCILIATORIAS',
                                                  UN_ACCION    => 'E', 
                                                  UN_CONDICION => MI_CONDICION);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                MI_MSGERROREXC(1).CLAVE := 'CUENTA';
                MI_MSGERROREXC(1).VALOR := UN_CUENTA;
                MI_MSGERROREXC(2).CLAVE := 'ANIO';
                MI_MSGERROREXC(2).VALOR := MI_ANIO;
                PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_DEL_PARTCONC,
                                            UN_REEMPLAZOS => MI_MSGERROREXC,
                                            UN_TABLAERROR => 'PARTIDAS_CONCILIATORIAS'
                );
            END;
        END IF;
        MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA     =>  'PARTIDAS_CONCILIATORIAS',
                                                           UN_CRITERIO  =>  'COMPANIA    = ''' || UN_COMPANIA || '''
                                                                         AND ANO         = '   || MI_ANIO     || '
                                                                         AND MES_PARTIDA = '   || MI_MES,  
                                                           UN_CAMPO     =>  'CONSECUTIVO',
                                                           UN_INICIAL   => '1');
        BEGIN
            BEGIN
                MI_MERGEUSING := 'SELECT DETALLE_COMPROBANTE_CNT.COMPANIA, 
                                       DETALLE_COMPROBANTE_CNT.ANO, 
                                       DETALLE_COMPROBANTE_CNT.TIPO_CPTE, 
                                       DETALLE_COMPROBANTE_CNT.COMPROBANTE, 
                                       DETALLE_COMPROBANTE_CNT.CONSECUTIVO, 
                                       COMPROBANTE_CNT.NRO_DOCUMENTO 
                                  FROM COMPROBANTE_CNT 
                                        INNER JOIN DETALLE_COMPROBANTE_CNT  
                                                ON COMPROBANTE_CNT.COMPANIA = DETALLE_COMPROBANTE_CNT.COMPANIA 
                                               AND COMPROBANTE_CNT.ANO      = DETALLE_COMPROBANTE_CNT.ANO 
                                               AND COMPROBANTE_CNT.TIPO     = DETALLE_COMPROBANTE_CNT.TIPO_CPTE  
                                               AND COMPROBANTE_CNT.NUMERO   = DETALLE_COMPROBANTE_CNT.COMPROBANTE 
                                        INNER JOIN V_PLAN_CONTABLE 
                                                ON DETALLE_COMPROBANTE_CNT.COMPANIA = V_PLAN_CONTABLE.COMPANIA 
                                               AND DETALLE_COMPROBANTE_CNT.ANO      = V_PLAN_CONTABLE.ANO
                                               AND DETALLE_COMPROBANTE_CNT.CUENTA   = V_PLAN_CONTABLE.ID
                                 WHERE COMPROBANTE_CNT.COMPANIA    = ''' || UN_COMPANIA || '''
                                   AND V_PLAN_CONTABLE.CLASECUENTA = ''B''  
                                   AND COMPROBANTE_CNT.NRO_DOCUMENTO IS NOT NULL
                                   AND DETALLE_COMPROBANTE_CNT.NRO_DOCUMENTO IS NULL 
                                   AND DETALLE_COMPROBANTE_CNT.CUENTA = ''' || UN_CUENTA || '''';
                MI_MERGEENLACE := '    TABLA.COMPANIA    = VISTA.COMPANIA 
                                 AND TABLA.ANO         = VISTA.ANO 
                                 AND TABLA.TIPO_CPTE   = VISTA.TIPO_CPTE 
                                 AND TABLA.COMPROBANTE = VISTA.COMPROBANTE 
                                 AND TABLA.CONSECUTIVO = VISTA.CONSECUTIVO';
                MI_MERGEEXISTE := 'UPDATE SET TABLA.NRO_DOCUMENTO = VISTA.NRO_DOCUMENTO';
                MI_RTAACME :=  PCK_DATOS.FC_ACME (UN_TABLA       => 'DETALLE_COMPROBANTE_CNT', 
                                                  UN_ACCION      => 'MM', 
                                                  UN_MERGEUSING  => MI_MERGEUSING, 
                                                  UN_MERGEENLACE => MI_MERGEENLACE,
                                                  UN_MERGEEXISTE => MI_MERGEEXISTE);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            MI_MSGERROREXC(1).CLAVE := 'CUENTA';
            MI_MSGERROREXC(1).VALOR := UN_CUENTA;
            MI_MSGERROREXC(2).CLAVE := 'ANIO';
            MI_MSGERROREXC(2).VALOR := MI_ANIO;
            PCK_ERR_MSG.RAISE_WITH_MSG(  UN_EXC_COD    => SQLCODE,
                                         UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_ACT_DETCOMPC,
                                         UN_REEMPLAZOS => MI_MSGERROREXC,
                                         UN_TABLAERROR => 'DETALLE_COMPROBANTE_CNT'
            );                      
        END;   
    END IF;


    MI_REGISTROS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => '' || CASE WHEN SUBSTR(UN_CADENA, LENGTH(UN_CADENA),1) ='#' THEN SUBSTR(UN_CADENA,1,LENGTH(UN_CADENA)-1) ELSE UN_CADENA END || '',
                                                UN_DELIMITADOR  => '#');
    <<RECORRE_REGISTROS>>
    FOR RS IN MI_REGISTROS.FIRST..MI_REGISTROS.LAST 
    LOOP
        MI_CADENA_REG := ' ';
        MI_REG_CAMPOS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => '' || TO_CHAR(MI_REGISTROS(RS)) || '', 
                                                     UN_DELIMITADOR  => ';');
        BEGIN 
            BEGIN 
                BEGIN
                    MI_DEBITO       := TO_NUMBER(NVL(TO_CHAR(MI_REG_CAMPOS(UN_COLVALINGRESO)),0));
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG  || '3; - Incosistencia en el Débito ' || TO_CHAR(MI_REG_CAMPOS(UN_COLVALINGRESO));
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_CREDITO      := TO_NUMBER(NVL(TO_CHAR(MI_REG_CAMPOS(UN_COLVALCREDITO)),0));
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en el credito ' || TO_CHAR(MI_REG_CAMPOS(UN_COLVALCREDITO));
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_FECHATEM := MI_REG_CAMPOS(UN_COLFECHA);
                    IF UN_FORMATOFECHA = 'DDMMYYYY' AND LENGTH(MI_FECHATEM)=7 THEN
                        MI_FECHATEM := '0' || MI_FECHATEM;
                    ELSIF UN_FORMATOFECHA ='DD/MM/YYYY' AND LENGTH(MI_FECHATEM)=9 AND SUBSTR(MI_FECHATEM,2,1)='/' THEN
                        MI_FECHATEM := '0' || MI_FECHATEM;
                    END IF;
                    MI_FECHA_REG    := TO_DATE(MI_FECHATEM,UN_FORMATOFECHA);
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en la fecha ' || MI_REG_CAMPOS(UN_COLFECHA) || ' formato ' || UN_FORMATOFECHA;
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_DOCNUMERO    := NVL(TO_CHAR(MI_REG_CAMPOS(UN_COLDOCUMENTO)),' ');
                    IF UN_VALIDADOC NOT IN(0) AND MI_DOCNUMERO=' ' THEN
                        MI_CADENA_REG  := MI_CADENA_REG || '3; - Obliga a validar el número de documento y esta vacio';
                        GOTO SIGUIENTE;    
                    END IF;
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en el número de documento ' || MI_REG_CAMPOS(UN_COLDOCUMENTO);
                    GOTO SIGUIENTE;
                END;
                MI_VALORDOC     := MI_DEBITO + MI_CREDITO;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
         PCK_ERR_MSG.RAISE_WITH_MSG(
           UN_EXC_COD    => SQLCODE,
           UN_ERROR_COD  => PCK_ERRORES.ERR_CONT_RANGOREGCONCILIACION
         );                      
        END;
        IF MI_DEBITO = 0 AND MI_CREDITO =0 THEN
            MI_CADENA_REG  := MI_CADENA_REG || '3; - Los valores estan en cero ' ;
        END IF;
        <<SIGUIENTE>>      

        --CARGA LA TEMPORAL
        BEGIN
            BEGIN
                MI_CAMPOS    := 'COMPANIA,
                                 ANO,
                                 CONSECUTIVO,
                                 CUENTA,
                                 FECHA,
                                 FECHA_CONCILIA,
                                 VALOR_DEBITO,
                                 VALOR_CREDITO,
                                 NRO_DOCUMENTO,
                                 MENSAJE,
                                 PASA';
                MI_VALORES   :=''''|| UN_COMPANIA         ||''',
                               '   || MI_ANIO             ||',
                               '   || MI_FILA             ||',
                               ''' || UN_CUENTA           ||''',
                                  TO_DATE(''' || TO_CHAR(MI_FECHA_REG, 'DD/MM/YYYY HH24:mi:ss') ||''', ''DD/MM/YYYY HH24:mi:ss''),
                                  TO_DATE(''' || TO_CHAR( LAST_DAY(UN_FECHA), 'DD/MM/YYYY HH24:mi:ss') ||''', ''DD/MM/YYYY HH24:mi:ss''),
                               '   || MI_DEBITO           ||',
                               '   || MI_CREDITO          ||',
                               ''' || MI_DOCNUMERO        ||''',
                               ''' || MI_CADENA_REG       ||''',
                               '   || CASE WHEN MI_CADENA_REG = ' ' THEN -1 ELSE 0 END ; 

                MI_RTAACME       := PCK_DATOS.FC_ACME (UN_TABLA     => 'TEMP_CONCILIACION',
                                                       UN_ACCION    => 'I',
                                                       UN_CAMPOS    => MI_CAMPOS,
                                                       UN_VALORES   => MI_VALORES);                        
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;                    
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            MI_MSGERROREXC(1).CLAVE := 'FECHA';
            MI_MSGERROREXC(1).VALOR := TO_CHAR(MI_FECHA_REG,'DD/MM/YYYY');
            MI_MSGERROREXC(2).CLAVE := 'ANIO';
            MI_MSGERROREXC(2).VALOR := MI_ANIO;
            MI_MSGERROREXC(3).CLAVE := 'COMPROBANTE';
            MI_MSGERROREXC(3).VALOR := 0;            
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_ACTDETCOMCONC,
                                    UN_REEMPLAZOS => MI_MSGERROREXC,
                                    UN_TABLAERROR => 'TEMP_CONCILIACION'
            );                      
        END;
        MI_FILA := MI_FILA +1 ;        
    END LOOP RECORRE_REGISTROS;   

    --ACTUALIZA EL CAMPO DE ERROR Y NUMERO DE REGISTROS PARA GENERAR ERRORES
    BEGIN
        BEGIN
            MI_MERGEUSING := 'SELECT TEM.COMPANIA,
                                   TEM.ANO,
                                   TEM.CONSECUTIVO,
                                   COUNT(DET.COMPANIA) CONTADOR,
                                   MAX(DET.TIPO_CPTE     ) TIPO_CPTE,
                                   MAX(DET.COMPROBANTE   ) COMPROBANTE,
                                   MAX(DET.CONSECUTIVO   ) COMPROBANTE_CONS,
                                   MAX(DET.FECHA_CONCILIA) COMPROBANTE_FECHA_CON,
                                   MAX(DET.CONCILIADOR   ) COMPROBANTE_CONCILIADOR,
                                   MAX(DET.PAGADOBANCO   ) COMPROBANTE_PAGADOBANCO
                            FROM TEMP_CONCILIACION TEM INNER JOIN 
                                (SELECT D.COMPANIA, 
                                        D.ANO, 
                                        D.CUENTA, 
                                        D.VALOR_DEBITO, 
                                        D.VALOR_CREDITO, 
                                        D.NRO_DOCUMENTO,
                                        TO_NUMBER(TO_CHAR(D.FECHA, ''MM'')) MES,
                                        D.TIPO_CPTE,
                                        D.COMPROBANTE,
                                        D.CONSECUTIVO,
                                        D.FECHA_CONCILIA,
                                        D.CONCILIADOR,
                                        D.PAGADOBANCO
                                 FROM TIPO_COMPROBANTE T INNER JOIN DETALLE_COMPROBANTE_CNT D
                                   ON D.COMPANIA    = T.COMPANIA
                                  AND D.TIPO_CPTE   = T.CODIGO
                                 WHERE D.COMPANIA   = ''' || UN_COMPANIA || ''' 
                                   AND D.ANO        = '   || MI_ANIO     || '     
                                   AND D.CUENTA     = ''' || UN_CUENTA   || ''' 
                                   AND T.CLASE_CONTABLE IN(''I'',''S'',''E'',''B'',''G'',''D'',''A'')
                                 ) DET
                               ON TEM.COMPANIA      = DET.COMPANIA                             
                              AND TEM.ANO           = DET.ANO  
                              AND TEM.CUENTA        = DET.CUENTA                              
                              AND TEM.VALOR_DEBITO  = DET.VALOR_DEBITO
                              AND TEM.VALOR_CREDITO = DET.VALOR_CREDITO
                              AND TO_NUMBER(TO_CHAR(TEM.FECHA,''MM''))=DET.MES
                              AND CASE WHEN ' || UN_VALIDADOC || ' NOT IN(0) THEN DET.NRO_DOCUMENTO ELSE NVL(DET.NRO_DOCUMENTO,'' '') END = CASE WHEN ' || UN_VALIDADOC || ' NOT IN(0) THEN TEM.NRO_DOCUMENTO ELSE NVL(DET.NRO_DOCUMENTO,'' '') END 
                            GROUP BY TEM.COMPANIA,
                                   TEM.ANO,
                                   TEM.CONSECUTIVO';                             
            MI_MERGEENLACE := 'TABLA.COMPANIA      = VISTA.COMPANIA
                           AND TABLA.ANO           = VISTA.ANO
                           AND TABLA.CONSECUTIVO   = VISTA.CONSECUTIVO';    
            MI_MERGEEXISTE := 'UPDATE SET CONTADOR         = VISTA.CONTADOR,
                                          TIPO_CPTE        = VISTA.TIPO_CPTE,
                                          COMPROBANTE      = VISTA.COMPROBANTE,
                                          COMPROBANTE_CONS = VISTA.COMPROBANTE_CONS,
                                          COMPROBANTE_FECHA_CON   = VISTA.COMPROBANTE_FECHA_CON,
                                          COMPROBANTE_CONCILIADOR = VISTA.COMPROBANTE_CONCILIADOR,
                                          COMPROBANTE_PAGADOBANCO = VISTA.COMPROBANTE_PAGADOBANCO';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => 'TEMP_CONCILIACION',
                                                UN_ACCION     => 'MM',
                                                UN_MERGEUSING => MI_MERGEUSING,
                                                UN_MERGEENLACE=> MI_MERGEENLACE,
                                                UN_MERGEEXISTE=> MI_MERGEEXISTE); 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;    
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                                 UN_EXC_COD    => SQLCODE,
                                 UN_ERROR_COD  => PCK_ERRORES.ERR_PREPARACONCILIA,
                                 UN_TABLAERROR => 'TEMP_CONCILIACION'
        );  
    END;
    --ACTUALIZA ERRORES POR DUPLICADOS
    BEGIN
        BEGIN
            MI_CAMPOS := 'MENSAJE    = ''3;- Existe '' || CONTADOR || '' registros con los mismos datos no se concilia '',
                          PASA       = 0';
            MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || ''' 
                         AND ANO      = '   || MI_ANIO     || ' 
                         AND CUENTA   = ''' || UN_CUENTA   || ''' 
                         AND PASA       = -1
                         AND CONTADOR   > 1';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                                UN_TABLA => 'TEMP_CONCILIACION', 
                                                UN_ACCION => 'M', 
                                                UN_CAMPOS => MI_CAMPOS, 
                                                UN_CONDICION => MI_CONDICION
                              );
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD => SQLCODE,
                  UN_TABLAERROR => 'COMPROBANTE_CNT',
                  UN_ERROR_COD => PCK_ERRORES.ERR_PREPARACONCILIAERR
                ); 
    END;

    --ACTUALIZA ERRORES POR NO EXISTENCIA
    BEGIN
        BEGIN
            MI_CAMPOS := 'MENSAJE    = ''3;- El número de documento no esta registrado en el sistema; o los valores no coinciden con ningún registro'',
                          PASA       = 0';
            MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || ''' 
                         AND ANO      = '   || MI_ANIO     || ' 
                         AND CUENTA   = ''' || UN_CUENTA   || ''' 
                         AND PASA       = -1
                         AND CONTADOR   = 0';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                                UN_TABLA => 'TEMP_CONCILIACION', 
                                                UN_ACCION => 'M', 
                                                UN_CAMPOS => MI_CAMPOS, 
                                                UN_CONDICION => MI_CONDICION
                              );
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD => SQLCODE,
                  UN_TABLAERROR => 'COMPROBANTE_CNT',
                  UN_ERROR_COD => PCK_ERRORES.ERR_PREPARACONCILIAERR
                ); 
    END;
    --ACTUALIZA ADVETENCIA POR REGISTROS YA CONCILIADOS
    BEGIN
        BEGIN
            MI_CAMPOS := 'MENSAJE    = ''1;- Registro ya conciliado , Tipo comprobante = '' || TIPO_CPTE || '', Comprobante = '' || COMPROBANTE || '', Fecha de Conciliación = '' || TO_CHAR(COMPROBANTE_FECHA_CON,''DD/MM/YYYY'') || '', Consecutivo = '' || COMPROBANTE_CONS || '', Documento F. = '' || NRO_DOCUMENTO,
                          PASA       = 0';
            MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || ''' 
                         AND ANO      = '   || MI_ANIO     || ' 
                         AND CUENTA   = ''' || UN_CUENTA   || ''' 
                         AND COMPROBANTE_PAGADOBANCO NOT IN(0)
                         AND PASA       = -1
                         AND CONTADOR IN(1)';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                                UN_TABLA => 'TEMP_CONCILIACION', 
                                                UN_ACCION => 'M', 
                                                UN_CAMPOS => MI_CAMPOS, 
                                                UN_CONDICION => MI_CONDICION
                              );
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD => SQLCODE,
                  UN_TABLAERROR => 'COMPROBANTE_CNT',
                  UN_ERROR_COD => PCK_ERRORES.ERR_PREPARACONCILIAERR
                ); 
    END;

    --CONCILIA
    BEGIN
        BEGIN
            MI_MERGEUSING := 'SELECT COMPANIA,
                                     ANO,
                                     TIPO_CPTE,
                                     COMPROBANTE,
                                     COMPROBANTE_CONS,
                                     FECHA_CONCILIA
                              FROM TEMP_CONCILIACION                               
                              WHERE TEMP_CONCILIACION.COMPANIA   = ''' || UN_COMPANIA || '''
                                AND TEMP_CONCILIACION.ANO        = '   || MI_ANIO     || '     
                                AND TEMP_CONCILIACION.CUENTA     = ''' || UN_CUENTA   || ''' 
                                AND TEMP_CONCILIACION.PASA NOT IN(0)
                                AND TEMP_CONCILIACION.CONTADOR IN(1)
                                AND TEMP_CONCILIACION.COMPROBANTE_PAGADOBANCO IN(0)';
            MI_MERGEENLACE := ' TABLA.COMPANIA         = VISTA.COMPANIA
                            AND TABLA.ANO              = VISTA.ANO
                            AND TABLA.TIPO_CPTE        = VISTA.TIPO_CPTE
                            AND TABLA.COMPROBANTE      = VISTA.COMPROBANTE
                            AND TABLA.CONSECUTIVO      = VISTA.COMPROBANTE_CONS';
            MI_MERGEEXISTE := 'UPDATE SET CONCILIADOR    = ''' || UN_USUARIO || '''
                                        , FECHA_CONCILIA = VISTA.FECHA_CONCILIA
                                        , PAGADOBANCO    = -1';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => 'DETALLE_COMPROBANTE_CNT',
                                                UN_ACCION     => 'MM',
                                                UN_MERGEUSING => MI_MERGEUSING,
                                                UN_MERGEENLACE=> MI_MERGEENLACE,
                                                UN_MERGEEXISTE=> MI_MERGEEXISTE); 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE 
                            THEN RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;    
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                                 UN_EXC_COD    => SQLCODE,
                                 UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_REG_PARTCONC,
                                 UN_TABLAERROR => 'TEMP_CONCILIACION'
        );  
    END;

    BEGIN
        BEGIN
            MI_CAMPOS := 'MENSAJE    = ''2; - Conciliado. Tipo comprobante = '' || TIPO_CPTE || '', Comprobante = '' || COMPROBANTE || '', Consecutivo = '' || COMPROBANTE_CONS || '', Documento F. = '' || NRO_DOCUMENTO || '',Fecha de Conciliación = '' || TO_CHAR(FECHA,''DD/MM/YYYY'') ,
                          PASA       = 0';
            MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || ''' 
                         AND ANO      = '   || MI_ANIO     || ' 
                         AND CUENTA   = ''' || UN_CUENTA   || ''' 
                         AND PASA NOT IN(0)
                         AND CONTADOR IN(1)
                         AND COMPROBANTE_PAGADOBANCO IN(0)';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                                UN_TABLA => 'TEMP_CONCILIACION', 
                                                UN_ACCION => 'M', 
                                                UN_CAMPOS => MI_CAMPOS, 
                                                UN_CONDICION => MI_CONDICION
                              );
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD => SQLCODE,
                  UN_TABLAERROR => 'COMPROBANTE_CNT',
                  UN_ERROR_COD => PCK_ERRORES.ERR_PREPARACONCILIAERR
                ); 
    END;

    MI_CADENA:='';
    <<SALIDA>>
    FOR RS IN(SELECT *
              FROM TEMP_CONCILIACION
              WHERE TEMP_CONCILIACION.COMPANIA   =  UN_COMPANIA 
                AND TEMP_CONCILIACION.ANO        =  MI_ANIO      
                AND TEMP_CONCILIACION.CUENTA     =  UN_CUENTA
                )
    LOOP
        BEGIN
            MI_CADENA := MI_CADENA || TO_CHAR(RS.CONSECUTIVO) || ';' || TRIM(REPLACE(REPLACE (RS.MENSAJE, CHR(13),' ') , CHR(10) ,' ')) || CHR(13) || CHR(10);
        EXCEPTION WHEN OTHERS 
            THEN RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;    
    END LOOP SALIDA;

    BEGIN
        BEGIN
            MI_CONDICION := 'COMPANIA    = ''' || UN_COMPANIA || '''
                         AND CUENTA      = ''' || UN_CUENTA   || '''
                         AND ANO         = '   || MI_ANIO     ;    
            MI_RTAACME := PCK_DATOS.FC_ACME(UN_TABLA     => 'TEMP_CONCILIACION',
                                          UN_ACCION    => 'E', 
                                          UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
        MI_MSGERROREXC(1).CLAVE := 'CUENTA';
        MI_MSGERROREXC(1).VALOR := UN_CUENTA;
        MI_MSGERROREXC(2).CLAVE := 'ANIO';
        MI_MSGERROREXC(2).VALOR := MI_ANIO;
        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_DEL_PARTCONC,
                                    UN_REEMPLAZOS => MI_MSGERROREXC,
                                    UN_TABLAERROR => 'TEMP_CONCILIACION'
        );
    END;
    RETURN MI_CADENA;
END FC_GENERARRTACONCILIADOSXPLANO;

  FUNCTION FC_CREAR_TOTAL_LISTAAFEC
   /*
      NAME              : FC_CREAR_TOTAL 
      AUTHORS           : STEFANINI SYSMAN 
      AUTHOR MIGRACION  : JONATHAN ENRIQUE GUERRERO TORRES
      DATE MIGRADO      : 05/05/2017
      TIME              : 03:39 PM
      SOURCE MODULE     : GENERAL 
      MODIFIED          : JOSE PASCUAL GOMEZ
      DATE MODIFIED     : 01/08/2018
      TIME              : 03:00 PM
      DESCRIPTION       : Funcion que retorna el valor total de una lista de afectados.
                          Se optimiza la consulta para que no vaya a la vista y no haga sub select                     
      PARAMETERS        : UN_COMPANIA             => Compañia de ingreso a la aplicación.  
                          UN_LISTANUMEROAFECTAR   => Lista seleccionada desde el formulario ComprobantanteAfectar. Esta lista 
                                                     es la concatenación del año, tipo y comprobante de cada registro.


      @NAME:  crearTotalListaAfec
      @METHOD:  GET
      */  
 ( UN_COMPANIA                  IN PCK_SUBTIPOS.TI_COMPANIA,
   UN_LISTANUMEROAFECTAR        IN PCK_SUBTIPOS.TI_STRSQL,
   UN_CLASE                     IN VARCHAR2
 )


RETURN NUMBER 
AS 
 MI_CONSULTA   PCK_SUBTIPOS.TI_CONSULTA;
 MI_TOTAL      NUMBER;
BEGIN

  MI_CONSULTA:='SELECT NVL(SUM(CASE WHEN PLAN_CONTABLE.NATURALEZA = ''D''
                                THEN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO     - DETALLE_COMPROBANTE_CNT.VALOR_CREDITO
                                   + DETALLE_COMPROBANTE_CNT.DEBITO_AFECTADO  - DETALLE_COMPROBANTE_CNT.CREDITO_AFECTADO
                                ELSE DETALLE_COMPROBANTE_CNT.VALOR_CREDITO    - DETALLE_COMPROBANTE_CNT.VALOR_DEBITO
                                   + DETALLE_COMPROBANTE_CNT.CREDITO_AFECTADO - DETALLE_COMPROBANTE_CNT.DEBITO_AFECTADO
                                END),0) TOTAL
                FROM DETALLE_COMPROBANTE_CNT INNER JOIN PLAN_CONTABLE
                  ON DETALLE_COMPROBANTE_CNT.COMPANIA = PLAN_CONTABLE.COMPANIA
                 AND DETALLE_COMPROBANTE_CNT.ANO      = PLAN_CONTABLE.ANO
                 AND DETALLE_COMPROBANTE_CNT.CUENTA   = PLAN_CONTABLE.CODIGO
              WHERE DETALLE_COMPROBANTE_CNT.COMPANIA ='''||UN_COMPANIA||'''
                AND (DETALLE_COMPROBANTE_CNT.ANO, TIPO_CPTE, COMPROBANTE) IN ('||UN_LISTANUMEROAFECTAR||')
                AND PLAN_CONTABLE.CLASECUENTA IN '||UN_CLASE;
  BEGIN 
    EXECUTE IMMEDIATE MI_CONSULTA INTO MI_TOTAL;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
    MI_TOTAL :=0;
  END;
 RETURN MI_TOTAL;
END FC_CREAR_TOTAL_LISTAAFEC;

  FUNCTION FC_CREAR_TOTAL_LISTAAFEC_TER
   /*
      NAME              : FC_CREAR_TOTAL 
      AUTHORS           : STEFANINI SYSMAN 
      AUTHOR MIGRACION  : JONATHAN ENRIQUE GUERRERO TORRES
      DATE MIGRADO      : 05/05/2017
      TIME              : 03:39 PM
      SOURCE MODULE     : GENERAL 
      MODIFIED          : JOSE PASCUAL GOMEZ
      DATE MODIFIED     : 01/08/2018
      TIME              : 03:00 PM
      DESCRIPTION       : Funcion que retorna el valor total de una lista de afectados.
                          Se optimiza la consulta para que no vaya a la vista y no haga sub select                     
      PARAMETERS        : UN_COMPANIA             => Compañia de ingreso a la aplicación.  
                          UN_LISTANUMEROAFECTAR   => Lista seleccionada desde el formulario ComprobantanteAfectar. Esta lista 
                                                     es la concatenación del año, tipo y comprobante de cada registro.


      @NAME:  crearTotalListaAfec
      @METHOD:  GET
      */  
 (  UN_COMPANIA                  IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_LISTANUMEROAFECTAR        IN PCK_SUBTIPOS.TI_STRSQL,
    UN_TERCERO                   IN PCK_SUBTIPOS.TI_TERCERO,
    UN_SUCURSAL                  IN PCK_SUBTIPOS.TI_SUCURSAL,
    UN_CLASE                      IN VARCHAR2
 )


RETURN NUMBER 
AS 
 MI_CONSULTA   PCK_SUBTIPOS.TI_CONSULTA;
 MI_TOTAL      NUMBER;
BEGIN

  MI_CONSULTA:='SELECT NVL(SUM(CASE WHEN PLAN_CONTABLE.NATURALEZA = ''D''
                                THEN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO     - DETALLE_COMPROBANTE_CNT.VALOR_CREDITO
                                   + DETALLE_COMPROBANTE_CNT.DEBITO_AFECTADO  - DETALLE_COMPROBANTE_CNT.CREDITO_AFECTADO
                                ELSE DETALLE_COMPROBANTE_CNT.VALOR_CREDITO    - DETALLE_COMPROBANTE_CNT.VALOR_DEBITO
                                   + DETALLE_COMPROBANTE_CNT.CREDITO_AFECTADO - DETALLE_COMPROBANTE_CNT.DEBITO_AFECTADO
                                END),0) TOTAL
                FROM DETALLE_COMPROBANTE_CNT INNER JOIN PLAN_CONTABLE
                  ON DETALLE_COMPROBANTE_CNT.COMPANIA = PLAN_CONTABLE.COMPANIA
                 AND DETALLE_COMPROBANTE_CNT.ANO      = PLAN_CONTABLE.ANO
                 AND DETALLE_COMPROBANTE_CNT.CUENTA   = PLAN_CONTABLE.CODIGO
              WHERE DETALLE_COMPROBANTE_CNT.COMPANIA ='''||UN_COMPANIA||'''
                AND (DETALLE_COMPROBANTE_CNT.ANO, TIPO_CPTE, COMPROBANTE) IN ('||UN_LISTANUMEROAFECTAR||')
                AND DETALLE_COMPROBANTE_CNT.TERCERO  ='''||UN_TERCERO || '''
                AND DETALLE_COMPROBANTE_CNT.SUCURSAL ='''||UN_SUCURSAL|| ''' 
                AND PLAN_CONTABLE.CLASECUENTA IN '||UN_CLASE;
  BEGIN 
    EXECUTE IMMEDIATE MI_CONSULTA INTO MI_TOTAL;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
    MI_TOTAL :=0;
  END;
 RETURN MI_TOTAL;
END FC_CREAR_TOTAL_LISTAAFEC_TER;


  FUNCTION FC_SALDO_CUENTA_EGRESO
   /*
      NAME              : FC_SALDO_CUENTA_EGRESO 
      AUTHORS           : STEFANINI SYSMAN 
      AUTHOR MIGRACION  : JAVIER RICARDO VILLATE M
      DATE MIGRADO      : 08/05/2017
      TIME              : 03:39 PM
      SOURCE MODULE     : GENERAL 
      DESCRIPTION       : Función que retorna el valor del saldo 13 de la cuenta bancaria utilizada en el egreso.

      PARAMETERS        : 


      @NAME:  traerSaldoCuenta
      @METHOD:  GET
      */  
 (  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO                     IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPO                    IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
    UN_NUMERO                  IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
    UN_VLRGIRAR                IN PCK_SUBTIPOS.TI_DOBLE
 )
RETURN PCK_SUBTIPOS.TI_ENTERO 
AS 
 MI_VALOR_PARAMETRO PCK_SUBTIPOS.TI_CAMPOS;
BEGIN
     MI_VALOR_PARAMETRO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'CONTROLAR SALDO DE CUENTA EN EGRESO' , PCK_DATOS.MODULOCONTABILIDAD , SYSDATE),'NO');
     IF MI_VALOR_PARAMETRO = 'SI' THEN
         FOR RS 
            IN (
        SELECT  
            COMPROBANTE_CNTBANCOS.CUENTA, SALDO13 AS SALDO,
            COMPROBANTE_CNTBANCOS.VALOR AS VALORPAGAR
        FROM COMPROBANTE_CNTBANCOS 
        LEFT JOIN PLAN_CONTABLE  
        ON COMPROBANTE_CNTBANCOS.CUENTA = PLAN_CONTABLE.CODIGO 
        AND COMPROBANTE_CNTBANCOS.ANO = PLAN_CONTABLE.ANO 
        AND COMPROBANTE_CNTBANCOS.COMPANIA = PLAN_CONTABLE.COMPANIA
        WHERE COMPROBANTE_CNTBANCOS.COMPANIA   = UN_COMPANIA 
          AND COMPROBANTE_CNTBANCOS.ANO        = UN_ANO 
          AND COMPROBANTE_CNTBANCOS.TIPO       = UN_TIPO
          AND COMPROBANTE_CNTBANCOS.NUMERO     = UN_NUMERO
          ORDER BY COMPROBANTE_CNTBANCOS.ANO, 
                   COMPROBANTE_CNTBANCOS.TIPO, 
                   COMPROBANTE_CNTBANCOS.NUMERO, 
                   COMPROBANTE_CNTBANCOS.CUENTA)
       LOOP
       BEGIN
          IF NVL(RS.VALORPAGAR,0) > NVL(RS.SALDO, 0) THEN
             RETURN 2;
          END IF;
          END; 
       END LOOP;   
     END IF;      
       RETURN -1;   
END FC_SALDO_CUENTA_EGRESO;

FUNCTION FC_ACTUALIZA_CHEQUE
   /*
      NAME              : FC_ACTUALIZA_CHEQUE 
      AUTHORS           : STEFANINI SYSMAN 
      AUTHOR MIGRACION  : JAVIER RICARDO VILLATE M
      DATE MIGRADO      : 08/05/2017
      TIME              : 03:39 PM
      SOURCE MODULE     : GENERAL 
      DESCRIPTION       : Función que retorna el valor del saldo 13 de la cuenta bancaria utilizada en el egreso.

      PARAMETERS        : 


      @NAME:  actualizarCheque
      @METHOD:  GET
      */  
 (  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO                     IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPO                    IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
    UN_NUMERO                  IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
    UN_VLRGIRAR                IN PCK_SUBTIPOS.TI_DOBLE
 )
  RETURN PCK_SUBTIPOS.TI_ENTERO 
AS 
MI_EXISTE                   VARCHAR2(1) := 'N';
MI_RTA                      PCK_SUBTIPOS.TI_STRSQL;
MI_CAMPOS                   PCK_SUBTIPOS.TI_CAMPOS;
MI_CONDICION                PCK_SUBTIPOS.TI_CONDICION;
MI_REGMODIF                 NUMBER;
MI_ELIMINARCOMPROBANTEPPTAL PCK_SUBTIPOS.TI_ENTERO;
MI_VALOR_PARAMETRO          PCK_SUBTIPOS.TI_CAMPOS;

BEGIN
     MI_VALOR_PARAMETRO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'CONTROLAR SALDO DE CUENTA EN EGRESO' , PCK_DATOS.MODULOCONTABILIDAD , SYSDATE),'NO');
     IF MI_VALOR_PARAMETRO = 'SI' THEN
         FOR RS 
            IN (
        SELECT  
            COMPROBANTE_CNTBANCOS.CUENTA, SALDO13 AS SALDO,
            COMPROBANTE_CNTBANCOS.VALOR AS VALORPAGAR,
            TO_NUMBER(COMPROBANTE_CNTBANCOS.NUMEROCHEQUE) CHEQUE
        FROM COMPROBANTE_CNTBANCOS 
        LEFT JOIN PLAN_CONTABLE  
        ON COMPROBANTE_CNTBANCOS.CUENTA = PLAN_CONTABLE.CODIGO 
        AND COMPROBANTE_CNTBANCOS.ANO = PLAN_CONTABLE.ANO 
        AND COMPROBANTE_CNTBANCOS.COMPANIA = PLAN_CONTABLE.COMPANIA
        WHERE COMPROBANTE_CNTBANCOS.COMPANIA   = UN_COMPANIA 
          AND COMPROBANTE_CNTBANCOS.ANO        = UN_ANO 
          AND COMPROBANTE_CNTBANCOS.TIPO       = UN_TIPO
          AND COMPROBANTE_CNTBANCOS.NUMERO     = UN_NUMERO
          ORDER BY COMPROBANTE_CNTBANCOS.ANO, 
                   COMPROBANTE_CNTBANCOS.TIPO, 
                   COMPROBANTE_CNTBANCOS.NUMERO, 
                   COMPROBANTE_CNTBANCOS.CUENTA)
       LOOP
       BEGIN
          IF NVL(RS.VALORPAGAR,0) > NVL(RS.SALDO, 0) THEN   
 --             MI_CAMPOS := 'CHEQUE      = ' || RS.CHEQUE ||'' || -1  ;
 --             MI_CONDICION := '   COMPANIA = '''||UN_COMPANIA||''' 
 --                                 AND ANO   = ' || UN_ANO || '
 --                                 AND CODIGO=''' || RS.CUENTA || '''';

 --             MI_RTA := PCK_DATOS.FC_ACME(
 --                       'PLAN_CONTABLE','M', MI_CAMPOS, NULL, NULL, MI_CONDICION, NULL, NULL, NULL, NULL);    

              MI_CONDICION := 'COMPANIA         = '''|| UN_COMPANIA ||
                           ''' AND  ANO         = '|| UN_ANO ||
                             ' AND  TIPO        = '''|| UN_TIPO ||
                           ''' AND  NUMERO      = '|| UN_NUMERO ||'';   
            MI_REGMODIF := PCK_DATOS.FC_ACME(UN_TABLA     => 'COMPROBANTE_CNTBANCOS',
                                             UN_ACCION    => 'E',
                                             UN_CONDICION => MI_CONDICION );
              BEGIN                                           
                SELECT DISTINCT 'X'
                INTO MI_EXISTE
                FROM COMPROBANTE_PPTAL 
                WHERE COMPANIA=UN_COMPANIA 
                AND   ANO     =UN_ANO 
                AND   TIPO    =UN_TIPO 
                AND   NUMERO  =UN_NUMERO;

              EXCEPTION WHEN NO_DATA_FOUND  THEN
                MI_EXISTE := 'N';
              END ;
               IF MI_EXISTE NOT IN ('N') THEN
                  RETURN 3;
              END IF;
          END IF;
          END; 
       END LOOP;   
     END IF;  
       RETURN -1;   
END FC_ACTUALIZA_CHEQUE;

PROCEDURE PR_ELIMINARAFECTADOS
   /*
      NAME              : FC_ACTUALIZA_CHEQUE 
      AUTHORS           : STEFANINI SYSMAN 
      AUTHOR MIGRACION  : JAVIER RICARDO VILLATE M
      DATE MIGRADO      : 08/05/2017
      TIME              : 03:39 PM
      SOURCE MODULE     : GENERAL 
      DESCRIPTION       : Función que elimina los comprobantes afectados si los detalles son eliminados.

      PARAMETERS        : 


      @NAME:  eliminarAfectados
      @METHOD:  GET
      */  
 (  UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO                IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPO_CPTE          IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
    UN_COMPROBANTE        IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
    UN_ANO_AFEC           IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPO_CPTE_AFECT    IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
    UN_COMPROBANTE_AFECT  IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT
 )
AS 
MI_EXISTE               VARCHAR2(1) := 'N';
MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
MI_REGMODIF             NUMBER;

BEGIN

  SELECT DISTINCT 'X' 
  INTO MI_EXISTE
  FROM DETALLE_COMPROBANTE_CNT
  WHERE COMPANIA         =UN_COMPANIA
    AND ANO              =UN_ANO
    AND TIPO_CPTE        =UN_TIPO_CPTE
    AND COMPROBANTE      =UN_COMPROBANTE
    AND ANO_AFECT        =UN_ANO_AFEC
    AND TIPO_CPTE_AFECT  =UN_TIPO_CPTE_AFECT
    AND CMPTE_AFECTADO   =UN_COMPROBANTE_AFECT;
  EXCEPTION WHEN NO_DATA_FOUND  THEN
    BEGIN                
        MI_CONDICION := 'COMPANIA             = '''|| UN_COMPANIA ||
                   ''' AND  ANO               = '|| UN_ANO ||
                     ' AND  TIPO_CPTE         = '''|| UN_TIPO_CPTE ||
                   ''' AND  COMPROBANTE       = '|| UN_COMPROBANTE ||
                     ' AND  ANO_AFECT         = '|| UN_ANO_AFEC ||
                     ' AND  TIPO_CPTE_AFECT   = '''|| UN_TIPO_CPTE_AFECT ||
                   ''' AND  COMPROBANTE_AFECT = '|| UN_COMPROBANTE_AFECT ||'';                                
        MI_REGMODIF := PCK_DATOS.FC_ACME(UN_TABLA     => 'COMPROBANTE_CNTAFECTADOS',
                                         UN_ACCION    => 'E',
                                         UN_CONDICION => MI_CONDICION );   
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;                   
END PR_ELIMINARAFECTADOS;


PROCEDURE PR_GENERARINGRESO
  /*
    NAME              : PR_GENERARINGRESO --> en Access GENERARINGRESO
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : JAVIER VILLATE
    DATE MIGRADOR     : 16/05/2017
    TIME              : 09:00 AM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Realiza el proceso de ingreso contable.

    MODIFICATIONS     : 
    @NAME:  generarIngreso
    @METHOD:  put
  */ 
(  
    UN_COMPANIA                     IN PCK_SUBTIPOS.TI_COMPANIA,              --COMPANIA
    UN_ANO                          IN PCK_SUBTIPOS.TI_ANIO,                  --AÑO DEL COMPROBANTE AFECTANTE EJEMPLO NBA
    UN_TIPO_CPTE                    IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,       --TIPO DEL COMPROBANTE EJEMPLO NBA
    UN_COMPROBANTE                  IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,  --NUMERO DEL COMPROBANTE AFECTANTE EJEMPLO 2017000001 
    UN_TERCERO                      IN PCK_SUBTIPOS.TI_TERCERO,               --TERCERO DEL COMPROBANTE NBA EJEMPLO 800021261
    UN_SUCURSAL                     IN PCK_SUBTIPOS.TI_SUCURSAL,              --SUCURSAL DEL COMPROBANTE NBA EJEMPLO 001
    UN_LISTANUMEROAFECTAR           IN PCK_SUBTIPOS.TI_STRSQL,                --LISTA DE LOS COMPROBANTES A AFERCTAR EJEMPLO UNA FACTURA FDS
    UN_FECHA                        IN DATE,                                  --FECHA DEL COMPROBANTE AFECTANTE TIPO NBA EJEMPLO 01/01/2017
    UN_CLASE                        IN PCK_SUBTIPOS.TI_CLASECOMPROBANTE,      --CLASE DEL COMPROBANTE AFECTANTE NBA EJEMPLO B
    UN_CUENTA                       IN PCK_SUBTIPOS.TI_CODIGOCONTA,           --CUENTA DEL BANCO CON EL QUE VA APAGAR LA FACTURA EJEMPLO 1110010101
    UN_USUARIO                      IN VARCHAR2                               --USUARIO QUE CREA EL COMPROBANTE EJEMPLO ADMIN
    )
  AS  
  MI_RTA                            PCK_SUBTIPOS.TI_STRSQL;
  MI_VALOR                          PCK_SUBTIPOS.TI_DOBLE:=0;
  MI_CONSECUTIVODET                 PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_OTRADESCRIPCION                PCK_SUBTIPOS.TI_STRSQL;
  MI_DESCRIPCION                    PCK_SUBTIPOS.TI_STRSQL;
  MI_DESCRIPCION_AFECT              PCK_SUBTIPOS.TI_STRSQL := ' Cancelaci¿n ';
  MI_PCKDATOS                       PCK_SUBTIPOS.TI_RTA_ACME;
  MI_CAMPOS                         PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION                      PCK_SUBTIPOS.TI_CONDICION;
  MI_VALORES                        PCK_SUBTIPOS.TI_VALORES;
  MI_CONSULTA                       PCK_SUBTIPOS.TI_STRSQL;
  RSDATOS                           SYS_REFCURSOR;
  RSCONTRACUENTAS                   SYS_REFCURSOR;
  MI_ANO                            PCK_SUBTIPOS.TI_ANIO;
  MI_TIPO                           PCK_SUBTIPOS.TI_TIPOCOMPROBANTE; 
  MI_NUMERO                         PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT; 
  MI_CENTRO_COSTO                   PCK_SUBTIPOS.TI_CENTRO_COSTO;
  MI_AUXILIAR                       PCK_SUBTIPOS.TI_AUXILIAR;
  MI_TEXTO                          PCK_SUBTIPOS.TI_STRSQL;

  MICONTRACUENTAS_CUENTA            PCK_SUBTIPOS.TI_CODIGOCONTA; 
  MICONTRACUENTAS_CLASECUENTA       PCK_SUBTIPOS.TI_CLASECUENTACONTA;
  MICONTRACUENTAS_TIPO_CPTE         PCK_SUBTIPOS.TI_TIPOCOMPROBANTE;
  MICONTRACUENTAS_NATURALEZA        PCK_SUBTIPOS.TI_NATURALEZACONTA;
  MICONTRACUENTAS_COMPROBANTE       PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT; 
  MICONTRACUENTAS_CONSECUTIVO       PCK_SUBTIPOS.TI_CONSECUTIVOCNT;
  MICONTRACUENTAS_NRO_DOCUMENTO     DETALLE_COMPROBANTE_CNT.NRO_DOCUMENTO%TYPE;
  MICONTRACUENTAS_TERCERO           PCK_SUBTIPOS.TI_TERCERO;
  MICONTRACUENTAS_SUCURSAL          PCK_SUBTIPOS.TI_SUCURSAL;
  MICONTRACUENTAS_CENTRO_COSTO      PCK_SUBTIPOS.TI_CENTRO_COSTO;
  MICONTRACUENTAS_AUXILIAR          PCK_SUBTIPOS.TI_AUXILIAR;
  MICONTRA_CUENTA_PPTAL             PCK_SUBTIPOS.TI_CODIGOPPTAL;
  MICONTRACUENTAS_ID                VARCHAR2(4000); 
  MICONTRACUENTAS_VALOR_DEBITO      PCK_SUBTIPOS.TI_DOBLE:=0;
  MICONTRACUENTAS_VALOR_CREDITO     PCK_SUBTIPOS.TI_DOBLE:=0;
  MICONTRA_DEBITO_AFECTADO          PCK_SUBTIPOS.TI_DOBLE:=0;
  MICONTRA_CREDITO_AFECTADO         PCK_SUBTIPOS.TI_DOBLE:=0;
  MI_TABLA                          PCK_SUBTIPOS.TI_TABLA;
  MI_AUX                            PCK_SUBTIPOS.TI_TEXTO1;
  MI_PARAMETRO                      PARAMETRO.VALOR%TYPE;
  MI_CODEQUICARTERA                 PLAN_CONTABLE.COD_EQUI_CARTERA%TYPE;
  MI_CUENTA                         PCK_SUBTIPOS.TI_ENTERO;
  MI_NUMEROCONTRATO                 NUMBER(20);--TICKET 7742952 MROSERO 
  MI_TIPOCONTRATO                   VARCHAR(3); 
  MIEQUIVSIGEC                      VARCHAR(250);
  MI_VALOR_IVA        PCK_SUBTIPOS.TI_DOBLE;
              
BEGIN  
    BEGIN
    
    MI_PARAMETRO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                  , UN_NOMBRE    => 'MANEJA RECAUDO CON CODIGO EQUIVALENTE'
                  , UN_MODULO    => 1
                  , UN_FECHA_PAR => SYSDATE),'NO');
    
MI_CONSULTA := '
      SELECT NVL(SUM(DETALLE_COMPROBANTE_CNT.VALOR_DEBITO-(DETALLE_COMPROBANTE_CNT.DEBITO_AFECTADO - DETALLE_COMPROBANTE_CNT.CREDITO_AFECTADO)),0) MI_VALOR  
      FROM V_PLAN_CONTABLE INNER JOIN DETALLE_COMPROBANTE_CNT 
             ON  DETALLE_COMPROBANTE_CNT.COMPANIA = V_PLAN_CONTABLE.COMPANIA 
             AND DETALLE_COMPROBANTE_CNT.ANO      = V_PLAN_CONTABLE.ANO 
             AND DETALLE_COMPROBANTE_CNT.ID       = V_PLAN_CONTABLE.ID
      WHERE DETALLE_COMPROBANTE_CNT.COMPANIA='''||UN_COMPANIA||'''
             AND (DETALLE_COMPROBANTE_CNT.ANO, TIPO_CPTE, COMPROBANTE) IN ('||UN_LISTANUMEROAFECTAR||') 
             AND V_PLAN_CONTABLE.CLASECUENTA IN (''C'',''O'')';

             EXECUTE IMMEDIATE MI_CONSULTA INTO MI_VALOR;

      EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_VALOR:=0;
      END;        

   MI_CONSECUTIVODET := 1; 
   MI_VALOR := ROUND(MI_VALOR, 2);

<<datos_comprobante_afectar>>   

  MI_CONSULTA:='  SELECT ANO,
           TIPO,
           NUMERO,
           CENTRO_COSTO,
           AUXILIAR,
           DESCRIPCION,
           TEXTO,
           NVL(NUMEROCONTRATO,0),
           TIPOCONTRATO
    FROM COMPROBANTE_CNT
    WHERE COMPROBANTE_CNT.COMPANIA='''||UN_COMPANIA||''' 
          AND (COMPROBANTE_CNT.ANO, TIPO, NUMERO) IN ('||UN_LISTANUMEROAFECTAR||')  
    ORDER BY NUMERO';

    OPEN RSDATOS FOR MI_CONSULTA;
    LOOP
    FETCH RSDATOS INTO MI_ANO, MI_TIPO, MI_NUMERO, MI_CENTRO_COSTO, MI_AUXILIAR, MI_DESCRIPCION, MI_TEXTO, MI_NUMEROCONTRATO, MI_TIPOCONTRATO;
    EXIT WHEN RSDATOS%NOTFOUND;
    BEGIN 

   MI_OTRADESCRIPCION := 'Cancelación Factura No.  ' || MI_NUMERO;
   MI_DESCRIPCION_AFECT := MI_DESCRIPCION_AFECT || ' '|| MI_TIPO ||' '|| MI_NUMERO ||' '|| MI_DESCRIPCION || CHR(13)||CHR(10);

   
        SELECT COUNT(DETALLE_COMPROBANTE_CNT.CUENTA) CUENTA
            INTO MI_CUENTA
         FROM DETALLE_COMPROBANTE_CNT 
        WHERE DETALLE_COMPROBANTE_CNT.COMPANIA        = UN_COMPANIA
              AND DETALLE_COMPROBANTE_CNT.ANO         = UN_ANO
              AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE   = UN_TIPO_CPTE
              AND DETALLE_COMPROBANTE_CNT.COMPROBANTE = UN_COMPROBANTE
              AND DETALLE_COMPROBANTE_CNT.CUENTA      = UN_CUENTA;
              
        IF MI_CUENTA = 0 THEN
        
    BEGIN            
           MI_CAMPOS := 'COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CONSECUTIVO,CUENTA,
                        FECHA,NATURALEZA,DESCRIPCION,VALOR_DEBITO,VALOR_CREDITO,
                        EJECUCION_DEBITO,BASE_GRAVABLE,CENTRO_COSTO,TERCERO,SUCURSAL,
                        AUXILIAR,CREATED_BY,DATE_CREATED';

           MI_VALORES := '''' || UN_COMPANIA || '''
                          ,' || UN_ANO || '
                          ,''' || UN_TIPO_CPTE || '''
                          ,' || UN_COMPROBANTE || '
                          ,' || MI_CONSECUTIVODET || '
                          ,''' || UN_CUENTA || '''
                          ,TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                          ,''D''
                          ,''' || MI_DESCRIPCION || '''
                          ,' || MI_VALOR || '
                          ,0
                          ,0
                          ,0
                          ,''' || MI_CENTRO_COSTO || '''
                          ,''' || UN_TERCERO || '''
                          ,''' || UN_SUCURSAL || '''
                          ,''' || MI_AUXILIAR || '''
                          ,''' || UN_USUARIO || '''
                          , SYSDATE';

                       MI_PCKDATOS := PCK_DATOS.FC_ACME(
                              UN_TABLA => 'DETALLE_COMPROBANTE_CNT', 
                              UN_ACCION => 'I', 
                              UN_CAMPOS => MI_CAMPOS, 
                              UN_VALORES => MI_VALORES
                            );
                          EXCEPTION
                            WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                              PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD => SQLCODE,
                                UN_TABLAERROR => 'DETALLE_COMPROBANTE_CNT',
                                UN_ERROR_COD => PCK_ERRORES.ERR_CONTABILIDAD_INSERBANCOS
                              ); 
                          END; 
                         
  MI_CONSECUTIVODET:=MI_CONSECUTIVODET+1;
  
  END IF;

<<procesar_contracuentas>>             

     MI_CONSULTA:='SELECT  V_PLAN_CONTABLE.CLASECUENTA,
                    DET.TIPO_CPTE,
                    DET.COMPROBANTE,
                    DET.CONSECUTIVO,
                    DET.NATURALEZA,
                    DET.NRO_DOCUMENTO,
                    DET.TERCERO,
                    DET.SUCURSAL,
                    DET.CENTRO_COSTO,
                    DET.AUXILIAR,
                    CASE WHEN V_PLAN_CONTABLE.COD_RECONOCIMIENTO IS NOT NULL THEN COD_RECONOCIMIENTO ELSE  DET.CUENTA END CUENTA,
                    DET.ID,
                        CASE
                    WHEN ( DET.VALOR_DEBITO - NVL(SUM(DET.DEBITO_AFECTADO-DET.CREDITO_AFECTADO), 0  ) ) < 0 THEN 0
                    ELSE
                    ( DET.VALOR_DEBITO - NVL(SUM(DET.DEBITO_AFECTADO-DET.CREDITO_AFECTADO), 0 ) )
                    END                  AS VALOR_DEBITO,
                    CASE
                    WHEN ( DET.VALOR_CREDITO - NVL(SUM(DET.CREDITO_AFECTADO-DET.DEBITO_AFECTADO), 0 ) ) < 0 THEN  0
                    ELSE
                    ( DET.VALOR_CREDITO - NVL(SUM(DET.CREDITO_AFECTADO-DET.DEBITO_AFECTADO), 0
                    ) )
                    END                  AS VALOR_CREDITO,
                    DET.DEBITO_AFECTADO AS VALOR_DEBITO_AFECTADO,
                    DET.CREDITO_AFECTADO AS VALOR_CREDITO_AFECTADO,
                          DET.EQUIV_SIGEC,
                          V_PLAN_CONTABLE.CUENTA_PPTAL,
                          DET.VALOR_IVA

                FROM 
                    V_PLAN_CONTABLE 
                INNER JOIN 
                    DETALLE_COMPROBANTE_CNT DET
                    ON DET.COMPANIA = V_PLAN_CONTABLE.COMPANIA
                    AND DET.ANO = V_PLAN_CONTABLE.ANO
                    AND DET.ID = V_PLAN_CONTABLE.ID
               WHERE DET.COMPANIA='''||UN_COMPANIA||'''
                      AND DET.ANO='||MI_ANO||'
                      AND DET.TIPO_CPTE='''||MI_TIPO||'''
                      AND DET.COMPROBANTE='||MI_NUMERO||'
                      AND ''1'' =  CASE WHEN V_PLAN_CONTABLE.COD_RECONOCIMIENTO IS NOT NULL THEN SUBSTR(COD_RECONOCIMIENTO,0, 1) ELSE  SUBSTR(DET.ID,0, 1) END
                      AND V_PLAN_CONTABLE.CLASECUENTA IN (''C'',''O'')
                      AND DET.VALOR_DEBITO<>0
                GROUP BY 
                        V_PLAN_CONTABLE.CLASECUENTA,
                        DET.TIPO_CPTE,
                        DET.COMPROBANTE,
                        DET.CONSECUTIVO,
                        DET.VALOR_DEBITO,
                        DET.NATURALEZA,
                        DET.NRO_DOCUMENTO,
                        DET.TERCERO,
                        DET.SUCURSAL,
                        DET.CENTRO_COSTO,
                        DET.AUXILIAR,
                        CASE WHEN V_PLAN_CONTABLE.COD_RECONOCIMIENTO IS NOT NULL THEN COD_RECONOCIMIENTO ELSE  DET.CUENTA END,
                        DET.ID,
                        DET.VALOR_CREDITO,
                        DET.VALOR_DEBITO,
                        DET.DEBITO_AFECTADO,
                        DET.CREDITO_AFECTADO,
                        DET.EQUIV_SIGEC,
                        V_PLAN_CONTABLE.CUENTA_PPTAL,
                        DET.VALOR_IVA';
        OPEN RSCONTRACUENTAS FOR MI_CONSULTA;
    LOOP
        FETCH RSCONTRACUENTAS INTO MICONTRACUENTAS_CLASECUENTA , MICONTRACUENTAS_TIPO_CPTE, MICONTRACUENTAS_COMPROBANTE, MICONTRACUENTAS_CONSECUTIVO,
              MICONTRACUENTAS_NATURALEZA ,MICONTRACUENTAS_NRO_DOCUMENTO , MICONTRACUENTAS_TERCERO,MICONTRACUENTAS_SUCURSAL,MICONTRACUENTAS_CENTRO_COSTO,MICONTRACUENTAS_AUXILIAR,
              MICONTRACUENTAS_CUENTA,MICONTRACUENTAS_ID,MICONTRACUENTAS_VALOR_DEBITO,MICONTRACUENTAS_VALOR_CREDITO,
              MICONTRA_DEBITO_AFECTADO,MICONTRA_CREDITO_AFECTADO,MIEQUIVSIGEC,MICONTRA_CUENTA_PPTAL,MI_VALOR_IVA;
        EXIT WHEN RSCONTRACUENTAS%NOTFOUND;
        BEGIN
         IF SUBSTR(MICONTRACUENTAS_CUENTA,0, 1) = '1' AND MICONTRACUENTAS_VALOR_DEBITO <> 0 THEN  
              
              BEGIN              
                  SELECT NVL(COD_EQUI_CARTERA,0)
                  INTO MI_CODEQUICARTERA
                    FROM PLAN_CONTABLE
                    WHERE COMPANIA = UN_COMPANIA
                          AND CODIGO = MICONTRACUENTAS_CUENTA
                          AND ANO = EXTRACT (YEAR FROM SYSDATE);
                   
                   EXCEPTION WHEN NO_DATA_FOUND THEN
                   MI_CODEQUICARTERA := 0;             
              END;
               
              IF MI_PARAMETRO = 'SI' AND  MI_CODEQUICARTERA != 0 AND  SUBSTR(MICONTRACUENTAS_CUENTA,0,2)= 14  THEN              
                MICONTRACUENTAS_CUENTA := MI_CODEQUICARTERA;              
              END IF;
              
              BEGIN
                  MI_CAMPOS := 'COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CONSECUTIVO,CUENTA,
                                FECHA,NATURALEZA,NRO_DOCUMENTO,DESCRIPCION,VALOR_DEBITO,VALOR_CREDITO,
                                EJECUCION_DEBITO,EJECUCION_CREDITO,BASE_GRAVABLE,CENTRO_COSTO,TERCERO,SUCURSAL,
                                AUXILIAR,ANO_AFECT,TIPO_CPTE_AFECT,CMPTE_AFECTADO,CONSECUTIVOAFECTADO,CUENTAPPTAL,EQUIV_SIGEC,VALOR_IVA,CREATED_BY,DATE_CREATED';
                  MI_VALORES := '''' || UN_COMPANIA || '''
                                ,' || UN_ANO || '
                                ,''' || UN_TIPO_CPTE || '''
                                ,' || UN_COMPROBANTE || '
                                ,' || MI_CONSECUTIVODET || '
                                ,''' || MICONTRACUENTAS_CUENTA || '''
                                ,TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                                ,''' ||  MICONTRACUENTAS_NATURALEZA || '''
                                ,'''||MICONTRACUENTAS_NRO_DOCUMENTO||'''
                                ,''' || MI_OTRADESCRIPCION || '''
                                ,0
                                ,' || MICONTRACUENTAS_VALOR_DEBITO ||'
                                ,0
                                ,' || MICONTRACUENTAS_VALOR_DEBITO ||'
                                ,0
                                ,''' || MICONTRACUENTAS_CENTRO_COSTO || '''
                                ,''' || UN_TERCERO || '''
                                ,''' || UN_SUCURSAL || '''
                                ,''' || MICONTRACUENTAS_AUXILIAR || '''
                                ,' || MI_ANO || '
                                ,''' || MI_TIPO || '''
                                ,' || MI_NUMERO || '
                                ,' || MICONTRACUENTAS_CONSECUTIVO || '
                                ,''' || MICONTRA_CUENTA_PPTAL || '''
                                ,''' || MIEQUIVSIGEC || '''
                                ,'|| MI_VALOR_IVA ||'
                                ,''' || UN_USUARIO || '''
                                , SYSDATE';    

                          MI_PCKDATOS := PCK_DATOS.FC_ACME(
                              UN_TABLA => 'DETALLE_COMPROBANTE_CNT', 
                              UN_ACCION => 'I', 
                              UN_CAMPOS => MI_CAMPOS, 
                              UN_VALORES => MI_VALORES
                            );
                          EXCEPTION
                            WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                              PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD => SQLCODE,
                                UN_TABLAERROR => 'DETALLE_COMPROBANTE_CNT',
                                UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_CREARDETPPTALVARIOS
                              ); 
                          END;

                        MI_CONSECUTIVODET := MI_CONSECUTIVODET + 1;

                    /*   IF INSTR('ERG', UN_CLASE) > 0 THEN

                           BEGIN
                               MI_CAMPOS := 'CREDITOSAFECTADOS = CASE WHEN CREDITOSAFECTADOS IS NULL THEN 0 ELSE CREDITOSAFECTADOS END + CASE WHEN  '|| MICONTRACUENTAS_VALOR_DEBITO ||' >0 THEN '|| MICONTRACUENTAS_VALOR_DEBITO ||'  ELSE 0 END';

                              MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || ''' 
                                              AND ANO    = ' || MI_ANO || ' 
                                              AND TIPO   = ''' || MI_TIPO || ''' 
                                              AND NUMERO = ' || MI_NUMERO;  

                              MI_PCKDATOS := PCK_DATOS.FC_ACME(
                                UN_TABLA => 'COMPROBANTE_CNT', 
                                UN_ACCION => 'M', 
                                UN_CAMPOS => MI_CAMPOS, 
                                UN_CONDICION => MI_CONDICION
                              );
                            EXCEPTION
                              WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                PCK_ERR_MSG.RAISE_WITH_MSG(
                                  UN_EXC_COD => SQLCODE,
                                  UN_TABLAERROR => 'COMPROBANTE_CNT',
                                  UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_ACTPPTO_DETCONTABLE
                                ); 
                            END;
                       END IF;    
                            IF INSTR('SINB', UN_CLASE) > 0 THEN
                             BEGIN
                              MI_CAMPOS := 'DEBITOSAFECTADOS = CASE WHEN DEBITOSAFECTADOS IS NULL THEN 0 ELSE DEBITOSAFECTADOS END + CASE WHEN  '|| MICONTRACUENTAS_VALOR_DEBITO ||' >0 THEN '|| MICONTRACUENTAS_VALOR_DEBITO ||' - '|| MICONTRA_DEBITO_AFECTADO ||'  ELSE 0 END';

                              MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || ''' 
                                              AND ANO    = ' || MI_ANO || ' 
                                              AND TIPO   = ''' || MI_TIPO || ''' 
                                              AND NUMERO = ' || MI_NUMERO;  

                              MI_PCKDATOS := PCK_DATOS.FC_ACME(
                                UN_TABLA => 'COMPROBANTE_CNT', 
                                UN_ACCION => 'M', 
                                UN_CAMPOS => MI_CAMPOS, 
                                UN_CONDICION => MI_CONDICION
                              );
                            EXCEPTION
                              WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                PCK_ERR_MSG.RAISE_WITH_MSG(
                                  UN_EXC_COD => SQLCODE,
                                  UN_TABLAERROR => 'COMPROBANTE_CNT',
                                  UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_ACTPPTO_DETCONTABLE
                                ); 
                            END;

                              BEGIN
                                MI_CAMPOS := 'DEBITO_AFECTADO = CASE WHEN DEBITO_AFECTADO IS NULL THEN 0 ELSE DEBITO_AFECTADO END + CASE WHEN  '|| MICONTRACUENTAS_VALOR_DEBITO ||' >0 THEN '|| MICONTRACUENTAS_VALOR_DEBITO ||' - '|| MICONTRA_DEBITO_AFECTADO ||'  ELSE 0 END';           

                                MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || ''' 
                                                AND ANO = ' ||MI_ANO || ' 
                                                AND TIPO_CPTE = ''' || MI_TIPO || ''' 
                                                AND COMPROBANTE = ' || MI_NUMERO || ' 
                                                AND CONSECUTIVO = ' || MICONTRACUENTAS_CONSECUTIVO ||'
                                                AND CUENTA      = '|| MICONTRACUENTAS_CUENTA;
                                MI_PCKDATOS := PCK_DATOS.FC_ACME(
                                  UN_TABLA => 'DETALLE_COMPROBANTE_CNT', 
                                  UN_ACCION => 'M', 
                                  UN_CAMPOS => MI_CAMPOS, 
                                  UN_CONDICION => MI_CONDICION
                                );
                              EXCEPTION
                                WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                  PCK_ERR_MSG.RAISE_WITH_MSG(
                                    UN_EXC_COD => SQLCODE,
                                    UN_TABLAERROR => 'DETALLE_COMPROBANTE_CNT',
                                    UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_ACTPPTO_DETCONTABLE
                                  ); 
                             END;
                        END IF; */             
               END IF;
       END;
         BEGIN
         MI_TABLA := 'DETALLE_COMPROBANTE_CNT';
            IF MICONTRACUENTAS_NATURALEZA = 'C' THEN 
             MI_CAMPOS := 'CREDITO_AFECTADO = CREDITO_AFECTADO + ' || MICONTRACUENTAS_VALOR_CREDITO || ' ';
            ELSE 
             MI_CAMPOS := 'DEBITO_AFECTADO = DEBITO_AFECTADO  + ' || MICONTRACUENTAS_VALOR_DEBITO || ' ';
            END IF;
            
            MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || 
                            ''' AND ANO = ' || MI_ANO || 
                            ' AND TIPO_CPTE = ''' || MI_TIPO || ''' 
                              AND COMPROBANTE = ''' || MI_NUMERO || 
                            ''' AND CUENTA = ' || MICONTRACUENTAS_CUENTA;
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOS,
                                                  UN_CONDICION => MI_CONDICION);   
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
          END;

                        MI_CONSECUTIVODET := MI_CONSECUTIVODET + 1;  
    END LOOP procesar_contracuentas;
 END;

END LOOP datos_comprobante_afectar; 

  BEGIN
         MI_TABLA := 'COMPROBANTE_CNT';
         MI_CAMPOS := 'VLRAGIRAR = '||MI_VALOR || ',DESCRIPCION = '''|| MI_DESCRIPCION_AFECT ||''',TEXTO='''|| MI_TEXTO ||'''
                       , NUMEROCONTRATO ='||MI_NUMEROCONTRATO || ', TIPOCONTRATO = '''|| MI_TIPOCONTRATO ||''' ';

         MI_CONDICION := '      COMPANIA    = '''||UN_COMPANIA||
                         ''' AND ANO         = '||UN_ANO||
                          '  AND TIPO        = '''||UN_TIPO_CPTE||
                         ''' AND NUMERO      = '||UN_COMPROBANTE;
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOS,
                                                  UN_CONDICION => MI_CONDICION);    
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;     

END PR_GENERARINGRESO;

PROCEDURE PR_ELIMINAR_CNTBANCOS
   /*
      NAME              : PR_ELIMINAR_CNTBANCOS 
      AUTHORS           : STEFANINI SYSMAN 
      AUTHOR MIGRACION  : JAVIER RICARDO VILLATE M
      DATE MIGRADO      : 08/05/2017
      TIME              : 03:39 PM
      SOURCE MODULE     : GENERAL 
      DESCRIPTION       : Función que elimina los comprobantes afectados si los detalles son eliminados.

      PARAMETERS        : 


      @NAME:  eliminarCntBancos
      @METHOD:  GET
      */  
 (  UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO                IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPO_CPTE          IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
    UN_COMPROBANTE        IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT
 )
AS 
MI_EXISTE               VARCHAR2(1) := 'N';
MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
MI_REGMODIF             NUMBER;

BEGIN

 SELECT DISTINCT 'X' 
 INTO MI_EXISTE
 FROM COMPROBANTE_CNTBANCOS
 WHERE COMPANIA         =UN_COMPANIA
   AND ANO              =UN_ANO
   AND TIPO             =UN_TIPO_CPTE
   AND NUMERO           =UN_COMPROBANTE;

 BEGIN               
              IF MI_EXISTE NOT IN ('N') THEN
 BEGIN   
                    MI_CONDICION := 'COMPANIA             = '''|| UN_COMPANIA ||
                               ''' AND  ANO               = '|| UN_ANO ||
                                 ' AND  TIPO              = '''|| UN_TIPO_CPTE ||
                               ''' AND  NUMERO            = '|| UN_COMPROBANTE ||'';                                

                    MI_REGMODIF := PCK_DATOS.FC_ACME(UN_TABLA     => 'COMPROBANTE_CNTBANCOS',
                                                     UN_ACCION    => 'E',
                                                     UN_CONDICION => MI_CONDICION );   
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                    END;                                  
           END IF;
        END;  
  EXCEPTION WHEN NO_DATA_FOUND THEN       
   NULL;
END PR_ELIMINAR_CNTBANCOS;
--12
PROCEDURE PR_VALIDAR_CHEQUERA 
    (
     /*
      NAME              : PR_VALIDAR_CHEQUERA 
      AUTHORS           : STEFANINI SYSMAN 
      AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
      DATE MIGRADO      : 23/05/2017
      TIME              : 03:39 PM
      SOURCE MODULE     : CONTABILIDAD 
      DESCRIPTION       : Función que valida si se puede registrar una chequera con los los rangos digitados.

      PARAMETERS        : UN_COMPANIA -> Codigo de la compañia por la que se ingresa en la aplicacion
                          UN_ANO -> El año por el cual se ingresa al plan contable
                          UN_CUENTA -> Cuenta seleccionada del plan contable
                          UN_CHEQUEINI -> Numero de cheque inicial al registra una chequera
                          UN_CHEQUEFIN -> Numero de cheque final al registrar una chequera

      @NAME:  validarChequera
      @METHOD:  GET
      */  
            UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA, 
            UN_ANO            IN PCK_SUBTIPOS.TI_ANIO,
            UN_CUENTA         IN PCK_SUBTIPOS.TI_CODIGOCONTA,
            UN_CHEQUEINI      IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
            UN_CHEQUEFIN      IN PCK_SUBTIPOS.TI_ENTERO_LARGO
    )

    AS 
      MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN 
        BEGIN
            <<CHEQUERA>>
                FOR RS IN (
                SELECT NUMINICIAL, NUMFINAL 
                FROM CHEQUERA
                WHERE COMPANIA = UN_COMPANIA
                    AND ANO = UN_ANO
                    AND CUENTA = UN_CUENTA)
            LOOP 
                BEGIN
        IF UN_CHEQUEINI >= RS.NUMINICIAL AND UN_CHEQUEINI <= RS.NUMFINAL  
                THEN RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;    
                END IF;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                        MI_MSGERROR(1).CLAVE := 'NUMERO';
                        MI_MSGERROR(1).VALOR := UN_CHEQUEINI;
                        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                                    UN_ERROR_COD  => PCK_ERRORES.ERR_CHEQUEINICIAL,
                                                    UN_TABLAERROR => 'CHEQUERA',
                                                    UN_REEMPLAZOS => MI_MSGERROR); 
        END;
        BEGIN 
                IF UN_CHEQUEFIN >= RS.NUMINICIAL  AND UN_CHEQUEFIN <= RS.NUMFINAL 
                THEN RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;   
                END IF;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                        MI_MSGERROR(1).CLAVE := 'NUMERO';
                        MI_MSGERROR(1).VALOR := UN_CHEQUEFIN;
                        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                                    UN_ERROR_COD  => PCK_ERRORES.ERR_CHEQUEINICIAL,
                                                    UN_TABLAERROR => 'CHEQUERA',
                                                    UN_REEMPLAZOS => MI_MSGERROR); 
        END;
            END LOOP CHEQUERA;
        END;

END PR_VALIDAR_CHEQUERA;


--13
PROCEDURE PR_REFLEJAR_SALDOS
 /*
      NAME              : PR_REFLEJAR_SALDOS
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
      DATE MIGRADOR     : 18/07/2017
      TIME              : 02:00 PM
      SOURCE MODULE     : CONTABILIDAD
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PROCEDIMIENTO QUE ACTUALIZA LOS SALDOS DEL PLAN CONTABLE.
      PARAMETERS        : UN_COMPANIA     =>  COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_ANIO         =>  AÑO EN EL CUAL SE VAN A ACTUALIZAR LOS SALDOS
      MODIFICATIONS     : 
      @NAME:  reflejarSaldos
      @METHOD:  PUT
    */ 
(
  UN_COMPANIA  IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO      IN PLAN_CONTABLE.ANO%TYPE
)
AS
 MI_TABLA          PCK_SUBTIPOS.TI_TABLA;
 MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;
 MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
 MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
 MI_RTA            PCK_SUBTIPOS.TI_RTA_ACME;
 MI_MERGEUSING     PCK_SUBTIPOS.TI_MERGEUSING;
 MI_MERGEENLACE    PCK_SUBTIPOS.TI_MERGEENLACE;
 MI_MERGEEXISTE    PCK_SUBTIPOS.TI_MERGEEXISTE; 
BEGIN

  MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA  ||'''
                   AND ANO  = '   || UN_ANIO      ||'';


    BEGIN
        DECLARE
            RTA NUMBER;
        BEGIN
            MI_MERGEUSING := 'SELECT COMPANIA, ANO, CODIGO, SUM(SALDO0) SALDO0 
                              FROM SALDO_AUX_CONTABLE
                              WHERE ' || MI_CONDICION || '
                              GROUP BY COMPANIA, ANO, CODIGO';                             
            MI_MERGEENLACE := 'TABLA.COMPANIA = VISTA.COMPANIA
                           AND TABLA.ANO      = VISTA.ANO
                           AND TABLA.CODIGO   = VISTA.CODIGO';    
            MI_MERGEEXISTE := 'UPDATE SET SALDO0 = NVL(VISTA.SALDO0,0)
                               WHERE ' || MI_CONDICION;

            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => 'PLAN_CONTABLE',
                                                UN_ACCION     => 'MM',
                                                UN_MERGEUSING => MI_MERGEUSING,
                                                UN_MERGEENLACE=> MI_MERGEENLACE,
                                                UN_MERGEEXISTE=> MI_MERGEEXISTE); 
            RTA:=PCK_DATOS.GL_RTA;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;    
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                                 UN_EXC_COD    => SQLCODE,
                                 UN_ERROR_COD  => PCK_ERRORES.ERR_PREPARACONCILIA,
                                 UN_TABLAERROR => 'PLAN_CONTABLE'
        );  
    END; 
  FOR RS IN 1..13 LOOP
   MI_TABLA := 'SALDO_AUX_CONTABLE';

   MI_CAMPOS := 'SALDO'|| RS || ' = ' || 'SALDO'|| (RS-1) ||' + NETO'|| RS ||'';
   BEGIN
          BEGIN
            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  MI_TABLA, 
                                         UN_ACCION    =>  'M', 
                                         UN_CAMPOS    =>  MI_CAMPOS,
                                         UN_CONDICION =>  MI_CONDICION);                 
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
          END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
             MI_MSGERROR(1).CLAVE := 'NUMSALDO';
              MI_MSGERROR(1).VALOR := RS;
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD   => SQLCODE,
                UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_UPDATE_SALDOS,
                UN_REEMPLAZOS =>  MI_MSGERROR
              );
        END;

   MI_TABLA := 'PLAN_CONTABLE';

        BEGIN
          BEGIN
            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  MI_TABLA, 
                                         UN_ACCION    =>  'M', 
                                         UN_CAMPOS    =>  MI_CAMPOS,
                                         UN_CONDICION =>  MI_CONDICION);                 
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
          END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
             MI_MSGERROR(1).CLAVE := 'NUMSALDO';
              MI_MSGERROR(1).VALOR := RS;
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD   => SQLCODE,
                UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_UPDATE_SALDOS,
                UN_REEMPLAZOS =>  MI_MSGERROR
              );
        END;
  END LOOP;
END PR_REFLEJAR_SALDOS;

--14
FUNCTION FC_CONVFORMATOCHEQUEAVALOR(
    /*
        NAME              : FC_CONVFORMATOCHEQUEAVALOR 
        AUTHORS           : SYSMAN SAS
        AUTHOR MIGRACION  : SERGIO ESTEBAN PIÑA VARGAS
        DATE MIGRADOR     : 06/04/2018
        TIME              : 
        SOURCE MODULE     : Contabilidad - SysmanCT2016.02.06
        MODIFIER          : 
        DATE MODIFIED     : 
        TIME              : 
        DESCRIPTION       : Realiza las concatenaciones para imprimir el formato de cheque de los comprobantes contables.
                            Se basa en PCK_CONTABILIDAD2.FC_LLENARFORMATOCHEQUETOTAL
        @NAME:  convertirFormatoChequeAValor
        @METHOD:  GET
    */        
        UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA, 
        UN_NOMBRECOMPANIA   IN PCK_SUBTIPOS.TI_RTA_ACME,
        UN_MODULO               IN PCK_SUBTIPOS.TI_MODULO,
        UN_FORMATO          IN PCK_SUBTIPOS.TI_RTA_ACME,
        UN_FECHAI           IN PCK_SUBTIPOS.TI_FECHA,
        UN_NITTERCERO       IN PCK_SUBTIPOS.TI_TERCERO,
        UN_NOMBRETERCERO    IN PCK_SUBTIPOS.TI_NOMBRETERCERO,
        UN_VALORCREDITO     IN PCK_SUBTIPOS.TI_DOBLE
    )RETURN VARCHAR2 AS 
        MI_FORMATOCHEQUETOTAL       VARCHAR2(4000 CHAR);
        MI_TEMPORAL                       VARCHAR2(4000 CHAR); 
        MI_MONEDALETRAS                 VARCHAR2(4000 CHAR); 
        MI_MONEDALETRAS1                VARCHAR2(4000 CHAR); 
        MI_MONEDALETRAS2                VARCHAR2(4000 CHAR); 
        MI_I                                  NUMBER; 
        MI_LARGOLETRAS                  NUMBER;
    BEGIN
        MI_LARGOLETRAS := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,
                              'LONGITUD DE CADENA CHEQUE IMPRESION', 
                              UN_MODULO, 
                              SYSDATE), 60);
        IF UN_VALORCREDITO IS NOT NULL THEN
            IF NVL(UN_FORMATO, ' ') <> ' ' THEN
                MI_MONEDALETRAS := PCK_SYSMAN_UTL.FC_VALOR_LETRAS(NVL(UN_VALORCREDITO, 0));
                IF LENGTH(MI_MONEDALETRAS) > MI_LARGOLETRAS THEN
                    MI_MONEDALETRAS1 := SUBSTR(MI_MONEDALETRAS, 0, MI_LARGOLETRAS);
                    MI_TEMPORAL := MI_MONEDALETRAS1;
                    FOR MI_I IN 0..(LENGTH(MI_MONEDALETRAS1)) LOOP
                        IF SUBSTR(MI_TEMPORAL, LENGTH(MI_TEMPORAL), 1) = ' ' THEN
                            MI_MONEDALETRAS1 := SUBSTR(MI_MONEDALETRAS,0, MI_LARGOLETRAS - MI_I);
                            MI_MONEDALETRAS2 := SUBSTR(MI_MONEDALETRAS, LENGTH(MI_TEMPORAL) + 1,LENGTH(MI_MONEDALETRAS) - (MI_LARGOLETRAS - MI_I));
                            EXIT;
                        ELSE
                            MI_TEMPORAL := SUBSTR(MI_TEMPORAL,0, MI_LARGOLETRAS - MI_I);
                        END IF;
                    END LOOP;
                ELSE
                    MI_MONEDALETRAS1 := MI_MONEDALETRAS;
                    MI_MONEDALETRAS2 := '';
                END IF;
                MI_FORMATOCHEQUETOTAL := UN_FORMATO;
                MI_FORMATOCHEQUETOTAL := REGEXP_REPLACE(MI_FORMATOCHEQUETOTAL, 'VALOR', TRIM(TO_CHAR(NVL(UN_VALORCREDITO, 0), '999G999G999G999G999G999D99')),1,0,'i');
                MI_FORMATOCHEQUETOTAL := REGEXP_REPLACE(MI_FORMATOCHEQUETOTAL, 'FECHALARGA', TRIM(TO_CHAR(NVL(UN_FECHAI,SYSDATE),'Day')) ||', ' || TO_CHAR(NVL(UN_FECHAI,SYSDATE),'DD')|| ' de '||TRIM(TO_CHAR(NVL(UN_FECHAI,SYSDATE),'Month','NLS_DATE_LANGUAGE=SPANISH'))|| ' de ' || TO_CHAR(NVL(UN_FECHAI,SYSDATE),'YYYY'),1,0,'i');
                MI_FORMATOCHEQUETOTAL := REGEXP_REPLACE(MI_FORMATOCHEQUETOTAL, 'FECHA', TO_CHAR(NVL(UN_FECHAI,SYSDATE), 'DD/Mon/YYYY','NLS_DATE_LANGUAGE=SPANISH'),1,0,'i');
                MI_FORMATOCHEQUETOTAL := REGEXP_REPLACE(MI_FORMATOCHEQUETOTAL, 'MONTOCOMPLETO', MI_MONEDALETRAS,1,0,'i');
                MI_FORMATOCHEQUETOTAL := REGEXP_REPLACE(MI_FORMATOCHEQUETOTAL, 'MONTOPARTE1', MI_MONEDALETRAS1,1,0,'i');
                MI_FORMATOCHEQUETOTAL := REGEXP_REPLACE(MI_FORMATOCHEQUETOTAL, 'MONTOPARTE2', MI_MONEDALETRAS2,1,0,'i');
                MI_FORMATOCHEQUETOTAL := REGEXP_REPLACE(MI_FORMATOCHEQUETOTAL, 'ANO', TO_CHAR(NVL(UN_FECHAI,SYSDATE), 'YYYY'),1,0,'i');
                MI_FORMATOCHEQUETOTAL := REGEXP_REPLACE(MI_FORMATOCHEQUETOTAL, 'MESNUMERO', TO_CHAR(NVL(UN_FECHAI,SYSDATE), 'MM'),1,0,'i');
                MI_FORMATOCHEQUETOTAL := REGEXP_REPLACE(MI_FORMATOCHEQUETOTAL, 'MES', TO_CHAR(NVL(UN_FECHAI,SYSDATE), 'Mon','NLS_DATE_LANGUAGE=SPANISH'));
        MI_FORMATOCHEQUETOTAL := REGEXP_REPLACE(MI_FORMATOCHEQUETOTAL, 'Mes', TO_CHAR(NVL(UN_FECHAI,SYSDATE), 'Mon','NLS_DATE_LANGUAGE=SPANISH'));
                MI_FORMATOCHEQUETOTAL := REGEXP_REPLACE(MI_FORMATOCHEQUETOTAL, 'DIA', TO_CHAR(NVL(UN_FECHAI,SYSDATE), 'DD'),1,0,'i');
                MI_FORMATOCHEQUETOTAL := REGEXP_REPLACE(MI_FORMATOCHEQUETOTAL, 'NITBENEFICIARIO', NVL(UN_NITTERCERO, ''),1,0,'i');
                MI_FORMATOCHEQUETOTAL := REGEXP_REPLACE(MI_FORMATOCHEQUETOTAL, 'BENEFICIARIO', NVL(UN_NOMBRETERCERO, ''),1,0,'i');
                MI_FORMATOCHEQUETOTAL := REGEXP_REPLACE(MI_FORMATOCHEQUETOTAL, 'TITULAR', UN_NOMBRECOMPANIA,1,0,'i');
            END IF;
        END IF;
        IF INSTR(MI_FORMATOCHEQUETOTAL, '#') > 0 THEN
            MI_FORMATOCHEQUETOTAL := SUBSTR(MI_FORMATOCHEQUETOTAL, 3, LENGTH(MI_FORMATOCHEQUETOTAL));
        END IF;
        RETURN MI_FORMATOCHEQUETOTAL;

END FC_CONVFORMATOCHEQUEAVALOR;

FUNCTION FC_RETENCIONPORTERCERO 

    /*
  NAME              : FC_RETENCIONPORTERCERO
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : ANA YESSICA SANA ROJAS
  DATE MIGRADOR     : 18/06/2018
  TIME              : 03:00 PM  
  SOURCE MODULE     : 
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  MODIFICATIONS     : 
  DESCRIPTION       : Permite ingresar las retenciones por tercero

   @NAME:    retencionPorTercero
   @METHOD:  GET
  */
    (UN_COMPANIA      IN    PCK_SUBTIPOS.TI_COMPANIA,
     UN_ANO           IN    PCK_SUBTIPOS.TI_ANIO,
     UN_TIPOCOMPROBRO IN    COMPROBANTE_CNTRETENCION.TIPORETENCION%TYPE,
     UN_NUMEROCOMPROB IN   COMPROBANTE_CNTRETENCION.NUMERO%TYPE,
     UN_TERCERO       IN   TERCERO_RETENCIONES.TERCERO%TYPE,
     UN_SUCURSAL      IN   TERCERO_RETENCIONES.SUCURSAL%TYPE,
     UN_VALORBASE     IN   COMPROBANTE_CNTRETENCION.VALORBASE%TYPE,
     UN_VALOR         IN   COMPROBANTE_CNTRETENCION.VALOR%TYPE,
     UN_USUARIO       IN   PCK_SUBTIPOS.TI_USUARIO
     )
RETURN PCK_SUBTIPOS.TI_LOGICO AS 
    MI_TABLA      PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION;
    MI_VALORES    PCK_SUBTIPOS.TI_VALORES;
    MI_RTA        PCK_SUBTIPOS.TI_ENTERO;
    MI_CANTIDAD   PCK_SUBTIPOS.TI_ENTERO;
    MI_CADENA     VARCHAR2(500CHAR);
    MI_LIMITE     RETENCIONES.LIMITE_INF%TYPE;
    MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_RTAFUNC    PCK_SUBTIPOS.TI_LOGICO := 0;
BEGIN

    BEGIN
        SELECT COUNT(1)
          INTO MI_CANTIDAD
          FROM COMPROBANTE_CNTRETENCION 
         WHERE COMPANIA = UN_COMPANIA
           AND ANO = UN_ANO
           AND TIPO= UN_TIPOCOMPROBRO
           AND NUMERO = UN_NUMEROCOMPROB;
    END;
    MI_TABLA := 'COMPROBANTE_CNTRETENCION';
    IF MI_CANTIDAD IN (0) THEN
        BEGIN
           FOR MI_RSRETENCION IN (
                    SELECT ANO,TIPO_RETENCION,
                           CODIGO_RETENCION
                      FROM TERCERO_RETENCIONES 
                     WHERE COMPANIA = UN_COMPANIA
                       AND SUCURSAL = UN_SUCURSAL
                       AND ANO      = UN_ANO
                       AND TERCERO  = UN_TERCERO)
            LOOP
                BEGIN
                    BEGIN
                        MI_CAMPOS := 'COMPANIA
                                      ,ANO
                                      ,TIPO
                                      ,NUMERO
                                      ,TIPORETENCION
                                      ,CODIGORETENCION
                                      ,VALOR
                                      ,VALORBASE
                                      ,DATE_CREATED
                                      ,CREATED_BY';

                        MI_VALORES := ''''  || UN_COMPANIA || 
                                      ''',' || UN_ANO ||
                                      ',''' || UN_TIPOCOMPROBRO ||
                                      ''',' || UN_NUMEROCOMPROB ||
                                      ',''' || MI_RSRETENCION.TIPO_RETENCION   || 
                                      ''','''|| MI_RSRETENCION.CODIGO_RETENCION || 
                                      ''',' || 0 ||
                                        ',' || CASE WHEN MI_RSRETENCION.TIPO_RETENCION IN ('IVA') THEN UN_VALOR ELSE UN_VALORBASE END || 
                                        ',SYSDATE, 
                                        ''' || UN_USUARIO || ''''  ;

                        MI_RTA := PCK_DATOS.FC_ACME(
                                          UN_TABLA   => MI_TABLA
                                         ,UN_ACCION  => 'I'
                                         ,UN_CAMPOS  => MI_CAMPOS
                                         ,UN_VALORES => MI_VALORES );
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD    => SQLCODE
                               ,UN_ERROR_COD  => PCK_ERRORES.ER_CONTAB_INSERRETENCVALORINF
                               ,UN_TABLAERROR => MI_TABLA);
                END;
            END LOOP;
        END;
    END IF;

  RETURN MI_RTAFUNC;
END FC_RETENCIONPORTERCERO;

FUNCTION FC_ESTADOCONCILIADO

/*
  NAME              : FC_ESTADOCONCILIADO
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : ANA YESSICA SANA ROJAS
  DATE MIGRADOR     : 23/06/2018
  TIME              : 03:00 PM  
  SOURCE MODULE     : 
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  MODIFICATIONS     : 
  DESCRIPTION       : Permite actualizar indicaddor de estado conciliado en conciliación 

   @NAME:    actualizarEstadoC
   @METHOD:  PUT */

(   UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO         IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES          IN PCK_SUBTIPOS.TI_MES,
    UN_CUENTA       IN DETALLE_COMPROBANTE_CNT.CUENTA%TYPE,
    UN_ULTIMODIA    IN DATE,
    UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO,
    UN_CLASES       IN VARCHAR2,
    UN_SELECCION    IN PCK_SUBTIPOS.TI_LOGICO

)RETURN PCK_SUBTIPOS.TI_LOGICO AS 

    MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
    MI_CLASES           PARAMETRO.VALOR%TYPE;
    MI_DIA              VARCHAR2(200 CHAR);

    BEGIN

    MI_CLASES := PCK_SYSMAN_UTL.FC_COLOCARCOMILLAS(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                  , UN_NOMBRE    => 'CLASES CONTABLES EN CONCILIACION BANCARIA'
                  , UN_MODULO    => 1
                  , UN_FECHA_PAR => SYSDATE),'NO')) ;

    MI_DIA := TO_DATE(UN_ULTIMODIA,'DD/MM/YYYY');          
        FOR MI_RS IN (SELECT D.COMPANIA,
                            D.ANO,
                            D.TIPO_CPTE,
                            D.COMPROBANTE,
                            D.CONSECUTIVO,
                            D.NRO_DOCUMENTO,
                            TO_CHAR(D.FECHA,'DD/MM/YYYY HH24:MI:SS') FECHA,
                            D.VALOR_DEBITO,
                            D.VALOR_CREDITO,
                            D.PAGADOBANCO,
                            TO_CHAR(D.FECHA_CONCILIA,'DD/MM/YYYY HH24:MI:SS') FECHA_CONCILIA,
                            D.DESCRIPCION,
                            D.TERCERO,
                            D.TIPO_CPTE_AFECT,
                            D.CMPTE_AFECTADO,
                            TO_CHAR(D.FECHA_CONSIGNACIONPLANO,'DD/MM/YYYY HH24:MI:SS') FECHA_CONSIGNACIONPLANO,
                            D.SINIDENTIFICAR,
                            TO_CHAR(D.FECHA_IDENTIFICACION,'DD/MM/YYYY HH24:MI:SS') FECHA_IDENTIFICACION,
                            D.CONCILIADOR,
                            C.IMPRESO,
                            C.ENTREGADO
                          FROM DETALLE_COMPROBANTE_CNT D
                          INNER JOIN COMPROBANTE_CNT C
                          ON D.COMPANIA     = C.COMPANIA
                          AND D.ANO         = C.ANO
                          AND D.TIPO_CPTE   = C.TIPO
                          AND D.COMPROBANTE = C.NUMERO
                          INNER JOIN TIPO_COMPROBANTE T
                          ON T.COMPANIA = D.COMPANIA
                          AND T.CODIGO  = D.TIPO_CPTE
                          INNER JOIN PLAN_CONTABLE
                          ON PLAN_CONTABLE.COMPANIA                = D.COMPANIA
                          AND PLAN_CONTABLE.ANO                    = D.ANO
                          AND PLAN_CONTABLE.CODIGO                 = D.CUENTA
                          WHERE D.COMPANIA                         = UN_COMPANIA
                          AND D.CUENTA                             = UN_CUENTA
                          AND INSTR(PCK_SYSMAN_UTL.FC_COLOCARCOMILLAS(UN_CLASES), T.CLASE_CONTABLE, 1) > 0
                          AND (( TRUNC(D.FECHA) <= TO_DATE(MI_DIA, 'DD/MM/YYYY') AND D.PAGADOBANCO IN (0))
                                 OR ( TO_NUMBER(TO_CHAR(D.FECHA_CONCILIA, 'MM')) = UN_MES
                                    AND TO_NUMBER(TO_CHAR(D.FECHA_CONCILIA, 'YYYY')) = UN_ANIO ) )
                          )
    LOOP
        BEGIN
            MI_TABLA := 'DETALLE_COMPROBANTE_CNT';
            --YB 05/12/2018 Se agrega validación de borrar seleccion en el formulario de conciliacion
            IF UN_SELECCION NOT IN(0) THEN 
            MI_CAMPOS :=  ' PAGADOBANCO     = 1, 
                            FECHA_CONCILIA  = TO_DATE(''' || UN_ULTIMODIA || ''',''DD/MM/YYYY'')
                          ,CONCILIADOR = ''' || UN_USUARIO || '''';
            ELSE
             MI_CAMPOS :=  '  PAGADOBANCO     = 0, 
                              FECHA_CONCILIA  = NULL,
                              CONCILIADOR = ''' || UN_USUARIO || '''';
            END IF;              
            MI_CONDICION := '          COMPANIA = '''||UN_COMPANIA      ||
                            ''' AND         ANO = '  ||MI_RS.ANO        ||
                            '   AND   TIPO_CPTE = '''||MI_RS.TIPO_CPTE  ||
                            ''' AND COMPROBANTE = '  ||MI_RS.COMPROBANTE||
                              ' AND CONSECUTIVO = '  ||MI_RS.CONSECUTIVO;
            BEGIN
               PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                                    UN_ACCION    => 'M',
                                                    UN_CAMPOS    => MI_CAMPOS,
                                                    UN_CONDICION => MI_CONDICION);             

                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
             END; 

        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 

            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ER_CONTAB_ACTINDCONCILIACION);                                                    

         END;
    END LOOP;
RETURN -1;
END FC_ESTADOCONCILIADO;

PROCEDURE PR_GENERARANULACION
  /*
    NAME              : PR_GENERARANULACION --> en Access GenerarAnulacion
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 31/07/2018
    TIME              : 14:00 PM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : 

    MODIFICATIONS     : 
    @NAME:  generarAnulacion
    @METHOD:  put
  */ 
(  
    UN_COMPANIA                     IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_ANOACTUAL                    IN PCK_SUBTIPOS.TI_ANIO, 
    UN_TIPOACTUAL                   IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE, 
    UN_NUMEROACTUAL                 IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
    UN_FECHA                        IN DATE, 
    UN_CLASE                        IN VARCHAR2, 
    UN_LISTAAAFECTAR                IN PCK_SUBTIPOS.TI_STRSQL, 
    UN_USUARIO                      IN VARCHAR2
    ) AS 
    MI_STRAFECTAR           VARCHAR2(1000);
    MI_TOTALDESCRIPCION     CLOB;
    MI_DESCRIPCION          VARCHAR2(2000);
    MI_DESCRI               VARCHAR2(2000);
    MI_CONSULTA             PCK_SUBTIPOS.TI_STRSQL;
    RSDATOS                 SYS_REFCURSOR;
    RSCLCUENTA              SYS_REFCURSOR;
    MI_SQLCLCUENTA          PCK_SUBTIPOS.TI_STRSQL;
    MI_ANIO                 PCK_SUBTIPOS.TI_ANIO;     
    MI_TIPO                 PCK_SUBTIPOS.TI_TIPOCOMPROBANTE; 
    MI_NUMERO               PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT; 
    MI_CUENTA               PCK_SUBTIPOS.TI_CODIGOCONTA; 
    MI_CENTRO_COSTO         PCK_SUBTIPOS.TI_CENTRO_COSTO;
    MI_AUXILIAR             PCK_SUBTIPOS.TI_AUXILIAR;
    MI_TERCERO              PCK_SUBTIPOS.TI_TERCERO;
    MI_SUCURSAL             PCK_SUBTIPOS.TI_SUCURSAL;
    MI_REFERENCIA           PCK_SUBTIPOS.TI_REFERENCIA;
    MI_FUENTE_RECURSOS      PCK_SUBTIPOS.TI_FUENTE_RECURSOS;
    MI_CONCEPTO_CUDS        PCK_SUBTIPOS.TI_CONCEPTO_CUDS; 
    MI_VALOR_DEBITO         PCK_SUBTIPOS.TI_DOBLE;
    MI_VALOR_CREDITO        PCK_SUBTIPOS.TI_DOBLE;
    MI_VAL_DEBITO           PCK_SUBTIPOS.TI_DOBLE;
    MI_VAL_CREDITO          PCK_SUBTIPOS.TI_DOBLE;
    MI_DEBITO_AFECTADO      PCK_SUBTIPOS.TI_DOBLE;
    MI_CREDITO_AFECTADO     PCK_SUBTIPOS.TI_DOBLE;
    MI_BASE_GRAVABLE        PCK_SUBTIPOS.TI_DOBLE;
    MI_ABONOINICIAL         PCK_SUBTIPOS.TI_DOBLE;
    MI_CONSECUTIVO          PCK_SUBTIPOS.TI_ENTERO;
    MI_CLASECUENTA          VARCHAR2(10);  
    MI_TEXTO                PCK_SUBTIPOS.TI_STRSQL;
    MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
    MI_PCKDATOS             PCK_SUBTIPOS.TI_RTA_ACME;
    MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
    MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
    MI_CONSECUTIVODET       PCK_SUBTIPOS.TI_ENTERO;
    MI_MENSAJE              VARCHAR2(1000);
    MI_DEBITO_AFECTADOFAC   PCK_SUBTIPOS.TI_DOBLE;
    MI_PARCLCUENTA          VARCHAR2(1000 CHAR);
    MI_CLCUENTA             VARCHAR2(1000 CHAR);
    MI_CLCUENTA_G           VARCHAR2(1000 CHAR);
    MI_REINTEGRO            PCK_SUBTIPOS.TI_ENTERO;
    MI_NATURALEZA           PCK_SUBTIPOS.TI_NATURALEZA;
    MI_PAIS                 DETALLE_COMPROBANTE_CNT.PAIS%TYPE;
    MI_DEPARTAMENTO         DETALLE_COMPROBANTE_CNT.DEPARTAMENTO%TYPE;
    MI_CIUDAD               DETALLE_COMPROBANTE_CNT.CIUDAD%TYPE;    

 -- Ticket 7730349     
BEGIN
    MI_PARCLCUENTA := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                              UN_NOMBRE    => 'CLASE CONTABLE CONCEPTOS DOCUMENTO SOPORTE',
                                              UN_MODULO    => 1,
                                              UN_FECHA_PAR => SYSDATE,
                                              UN_IND_MAYUS => 0
                                              ),'SIN CONFIGURAR');      
                                              
     MI_CLCUENTA := REPLACE(REPLACE(MI_PARCLCUENTA,' ',''),',',''',''');

    BEGIN 
        SELECT REINTEGRO 
          INTO MI_REINTEGRO
          FROM TIPO_COMPROBANTE
         WHERE COMPANIA = UN_COMPANIA
           AND CODIGO = UN_TIPOACTUAL;
    EXCEPTION  WHEN NO_DATA_FOUND THEN 
        MI_REINTEGRO := 0;
    END; 

-- FIN
    MI_CONSECUTIVODET := 0;
    IF UN_CLASE ='A' THEN
        MI_STRAFECTAR := 'Cheques Anulados: ';
    ELSIF UN_CLASE ='T' THEN
        MI_STRAFECTAR := 'Ordenes de pago anulados: ';
    ELSIF UN_CLASE ='U' THEN
        MI_STRAFECTAR := 'Adición orden de pago: '; 
    ELSE
        MI_STRAFECTAR := 'Recibos anulados: ';
    END IF;
    MI_TOTALDESCRIPCION := MI_STRAFECTAR;

    MI_CONSULTA:= 'SELECT DETALLE_COMPROBANTE_CNT.ANO, 
                          DETALLE_COMPROBANTE_CNT.TIPO_CPTE, 
                          DETALLE_COMPROBANTE_CNT.COMPROBANTE, 
                          DETALLE_COMPROBANTE_CNT.CUENTA, 
                          DETALLE_COMPROBANTE_CNT.NATURALEZA,
                          DETALLE_COMPROBANTE_CNT.CONSECUTIVO,
                          DETALLE_COMPROBANTE_CNT.TERCERO,
                          DETALLE_COMPROBANTE_CNT.SUCURSAL,
                          DETALLE_COMPROBANTE_CNT.CENTRO_COSTO,
                          DETALLE_COMPROBANTE_CNT.AUXILIAR,
                          DETALLE_COMPROBANTE_CNT.REFERENCIA,
                          DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO,
                          DETALLE_COMPROBANTE_CNT.CONCEPTO_CUDS,
                          DETALLE_COMPROBANTE_CNT.PAIS,
                          DETALLE_COMPROBANTE_CNT.DEPARTAMENTO,
                          DETALLE_COMPROBANTE_CNT.CIUDAD,
                          DETALLE_COMPROBANTE_CNT.VALOR_DEBITO, 
                          DETALLE_COMPROBANTE_CNT.VALOR_CREDITO, 
                          DETALLE_COMPROBANTE_CNT.DEBITO_AFECTADO, 
                          DETALLE_COMPROBANTE_CNT.CREDITO_AFECTADO, 
                          DETALLE_COMPROBANTE_CNT.ABONOINICIAL,
                          DETALLE_COMPROBANTE_CNT.BASE_GRAVABLE,                          
                          DETALLE_COMPROBANTE_CNT.DESCRIPCION, 
                          PLAN_CONTABLE.CLASECUENTA                          
                   FROM DETALLE_COMPROBANTE_CNT INNER JOIN  PLAN_CONTABLE
                     ON DETALLE_COMPROBANTE_CNT.COMPANIA  = PLAN_CONTABLE.COMPANIA
                    AND DETALLE_COMPROBANTE_CNT.ANO       = PLAN_CONTABLE.ANO
                    AND DETALLE_COMPROBANTE_CNT.CUENTA    = PLAN_CONTABLE.CODIGO
                   WHERE DETALLE_COMPROBANTE_CNT.COMPANIA = '''||UN_COMPANIA||'''
                     AND (DETALLE_COMPROBANTE_CNT.ANO, TIPO_CPTE, COMPROBANTE) IN (' || UN_LISTAAAFECTAR || ')'; 
    <<PRINCIPAL>>
    OPEN RSDATOS FOR MI_CONSULTA;
    LOOP
        FETCH RSDATOS INTO MI_ANIO,   MI_TIPO,        MI_NUMERO, 
                           MI_CUENTA, MI_NATURALEZA, MI_CONSECUTIVO, MI_TERCERO, MI_SUCURSAL,
                           MI_CENTRO_COSTO, MI_AUXILIAR, MI_REFERENCIA, MI_FUENTE_RECURSOS,MI_CONCEPTO_CUDS,
                           MI_PAIS, MI_DEPARTAMENTO, MI_CIUDAD, MI_VALOR_DEBITO, MI_VALOR_CREDITO,
                           MI_DEBITO_AFECTADO, MI_CREDITO_AFECTADO,
                           MI_ABONOINICIAL, MI_BASE_GRAVABLE, MI_DESCRI, MI_CLASECUENTA ;

        EXIT WHEN RSDATOS%NOTFOUND;
        BEGIN   
            BEGIN 
                  SELECT DEBITO_AFECTADO  
                  INTO  MI_DEBITO_AFECTADOFAC
                  FROM DETALLE_COMPROBANTE_CNT 
                  WHERE (COMPANIA   || ANO||    TIPO_CPTE|| COMPROBANTE || CONSECUTIVO ) IN(
                                                SELECT (COMPANIA || ANO_AFECT ||  TIPO_CPTE_AFECT || CMPTE_AFECTADO || CONSECUTIVOAFECTADO ) AS DATO  
                                                FROM DETALLE_COMPROBANTE_CNT  
                                                WHERE COMPANIA  =  UN_COMPANIA
                                                  AND ANO  =   MI_ANIO   
                                                  AND TIPO_CPTE =   MI_TIPO
                                                  AND COMPROBANTE =   MI_NUMERO 
                                                  AND CONSECUTIVO = MI_CONSECUTIVO);
            EXCEPTION  WHEN NO_DATA_FOUND THEN 
                MI_DEBITO_AFECTADOFAC := 0;
            END;
            MI_TOTALDESCRIPCION := MI_TOTALDESCRIPCION || MI_NUMERO || ',';
            MI_DESCRIPCION := MI_STRAFECTAR || MI_NUMERO || ',';
            MI_VAL_DEBITO  := CASE WHEN MI_VALOR_CREDITO>0 THEN MI_VALOR_CREDITO - (MI_DEBITO_AFECTADO + MI_CREDITO_AFECTADO) - MI_ABONOINICIAL ELSE 0 END;
            MI_VAL_CREDITO := CASE WHEN MI_VALOR_DEBITO >0 THEN MI_VALOR_DEBITO  - (MI_DEBITO_AFECTADO + MI_CREDITO_AFECTADO) - MI_ABONOINICIAL ELSE 0 END;
            MI_CONSECUTIVODET := MI_CONSECUTIVO + 1;
            IF UN_CLASE ='U' THEN
                MI_VAL_DEBITO  := 0;
                MI_VAL_CREDITO := 0;
            END IF;
            MI_CAMPOS := 'COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CONSECUTIVO,CUENTA,NATURALEZA,
                          DESCRIPCION,VALOR_DEBITO,VALOR_CREDITO,BASE_GRAVABLE,
                          TERCERO,SUCURSAL,CENTRO_COSTO,AUXILIAR,REFERENCIA,FUENTE_RECURSO,CONCEPTO_CUDS,
                          PAIS, DEPARTAMENTO,CIUDAD, FECHA,
                          EJECUCION_DEBITO,EJECUCION_CREDITO,
                          CREATED_BY,DATE_CREATED';
     
            MI_VALORES := '''' || UN_COMPANIA       || '''
                            ,' || UN_ANOACTUAL      || '
                          ,''' || UN_TIPOACTUAL     || '''
                            ,' || UN_NUMEROACTUAL   || '
                            ,' || MI_CONSECUTIVODET || '
                          ,''' || MI_CUENTA         || '''
                          ,''' || MI_NATURALEZA     || '''
                          ,''' || MI_DESCRIPCION    || '''
                            ,' || MI_VAL_DEBITO     || ' 
                            ,' || MI_VAL_CREDITO    || '
                            ,' || MI_BASE_GRAVABLE  || '
                          ,''' || MI_TERCERO        || '''
                          ,''' || MI_SUCURSAL       || '''
                          ,''' || MI_CENTRO_COSTO   || '''
                          ,''' || MI_AUXILIAR       || '''
                          ,''' || MI_REFERENCIA     || '''
                          ,''' || MI_FUENTE_RECURSOS|| '''
                          ,''' || MI_CONCEPTO_CUDS|| '''
                          ,''' || MI_PAIS|| '''
                          ,''' || MI_DEPARTAMENTO|| '''
                          ,''' || MI_CIUDAD|| '''
                          ,TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                            ,' || MI_VAL_DEBITO     || ' 
                            ,' || MI_VAL_CREDITO    || '
                          ,''' || UN_USUARIO        || '''
                                , SYSDATE';  
           
 -- Ticket 7730349 
   MI_SQLCLCUENTA := 'SELECT PLAN_CONTABLE.CLASECUENTA
                   FROM DETALLE_COMPROBANTE_CNT INNER JOIN PLAN_CONTABLE
                     ON DETALLE_COMPROBANTE_CNT.COMPANIA = PLAN_CONTABLE.COMPANIA
                    AND DETALLE_COMPROBANTE_CNT.ANO = PLAN_CONTABLE.ANO
                    AND DETALLE_COMPROBANTE_CNT.CUENTA = PLAN_CONTABLE.CODIGO
                   WHERE DETALLE_COMPROBANTE_CNT.COMPANIA = ''' || UN_COMPANIA || '''
                     AND (DETALLE_COMPROBANTE_CNT.ANO, TIPO_CPTE, COMPROBANTE) IN (' || UN_LISTAAAFECTAR || ')
                     AND PLAN_CONTABLE.CLASECUENTA IN (''' || MI_CLCUENTA || ''')';                      

        OPEN RSCLCUENTA FOR MI_SQLCLCUENTA;
        
        LOOP
            FETCH RSCLCUENTA INTO MI_CLCUENTA_G;  
            EXIT WHEN RSCLCUENTA%NOTFOUND ;
            
    IF MI_CLASECUENTA IN ('P','C','E','Y','N') OR MI_CLASECUENTA = MI_CLCUENTA_G THEN
                MI_CAMPOS :=MI_CAMPOS || ',ANO_AFECT,TIPO_CPTE_AFECT,CMPTE_AFECTADO,CONSECUTIVOAFECTADO';                
                MI_VALORES := MI_VALORES || '
                            ,''' || CASE WHEN UN_CLASE IN ('U') AND MI_CLASECUENTA NOT IN('P','E') THEN null ELSE MI_ANIO  END         || '''     
                          ,''' || CASE WHEN UN_CLASE IN ('U') AND MI_CLASECUENTA NOT IN('P','E') THEN null ELSE MI_TIPO END || '''
                            ,''' || CASE WHEN UN_CLASE IN ('U') AND MI_CLASECUENTA NOT IN('P','E') THEN null ELSE MI_NUMERO  END || '''
                            ,''' || CASE WHEN UN_CLASE IN ('U') AND MI_CLASECUENTA NOT IN('P','E') THEN null ELSE MI_CONSECUTIVO END || ''' ';
              END IF;
 

           EXIT WHEN MI_CLASECUENTA IN ('P','C','E','Y','N') OR MI_CLASECUENTA = MI_CLCUENTA_G;      
    END LOOP;

            BEGIN              
                MI_PCKDATOS := PCK_DATOS.FC_ACME( UN_TABLA   => 'DETALLE_COMPROBANTE_CNT', 
                                                  UN_ACCION  => 'I', 
                                                  UN_CAMPOS  => MI_CAMPOS, 
                                                  UN_VALORES => MI_VALORES);
            EXCEPTION
            WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD => SQLCODE,
                    UN_TABLAERROR => 'DETALLE_COMPROBANTE_CNT',
                    UN_ERROR_COD => PCK_ERRORES.ERR_DETALLE_AFECTADO
                  );
            END;
  
            MI_MENSAJE:='';
            IF UN_CLASE = 'A' THEN
                IF MI_REINTEGRO <> 0 THEN
                    MI_MENSAJE := 'REINTEGRO '|| MI_DESCRI; 
                ELSE
                    MI_MENSAJE :='Cheque anulado';
                END IF;
            ELSIF UN_CLASE = 'J' THEN
                MI_MENSAJE :='Recibo anulado';    
            ELSIF UN_CLASE = 'T' THEN
                IF MI_REINTEGRO <> 0 THEN
                    MI_MENSAJE := 'REINTEGRO '|| MI_DESCRI; 
                ELSE
                    MI_MENSAJE :='Orden de Pago anulada';        
                END IF;
            END IF;
            IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                         UN_NOMBRE    => 'REEMPLAZAR DESCRIPCION DEL COMPROBANTE AL REALIZAR ANULACION',
                                         UN_MODULO    => PCK_DATOS.MODULOCONTABILIDAD,
                                         UN_FECHA_PAR => SYSDATE),'NO') = 'SI' THEN
                MI_MENSAJE := MI_DESCRI || ', Anulado'; 
            END IF;
            IF MI_CLASECUENTA IN('P','C','E','Y','N') AND UN_CLASE <> 'R' AND UN_CLASE <> 'U' THEN
                BEGIN
                  
 --ACTUALIZA NBA
                    MI_CAMPOS    := ' DEBITO_AFECTADO =  DEBITO_AFECTADO +  ' || MI_VAL_DEBITO   || ' ,
                                     MODIFIED_BY =''' || UN_USUARIO || ''',
                                     DATE_MODIFIED =SYSDATE';

                    MI_CONDICION := 'COMPANIA         = ''' || UN_COMPANIA || 
                                 ''' AND ANO          = '   || MI_ANIO ||
                                   ' AND TIPO_CPTE    = ''' || MI_TIPO || 
                                 ''' AND COMPROBANTE  = '   || MI_NUMERO ||
                                   ' AND CONSECUTIVO  = '   || MI_CONSECUTIVO;            
                    MI_PCKDATOS   := PCK_DATOS.FC_ACME (UN_TABLA     => 'DETALLE_COMPROBANTE_CNT',
                                                           UN_ACCION    => 'M',
                                                           UN_CAMPOS    => MI_CAMPOS,
                                                           UN_CONDICION => MI_CONDICION);
                    
                      
                    MI_CAMPOS    := 'IMPRESO     = -1, 
                                     ANULADO     = -1, 
                                     DESCRIPCION =''' || SUBSTR(MI_MENSAJE,1,250) || ''',
                                     DEBITOSAFECTADOS = DEBITOSAFECTADOS +  ' || MI_VAL_DEBITO   || ' ,
                                     MODIFIED_BY =''' || UN_USUARIO || ''',
                                     DATE_MODIFIED =SYSDATE';
                    MI_CONDICION := 'COMPANIA        = ''' || UN_COMPANIA || 
                                 ''' AND ANO         = '   || MI_ANIO ||
                                   ' AND TIPO        = ''' || MI_TIPO || 
                                 ''' AND NUMERO      = '   || MI_NUMERO;            
                    MI_PCKDATOS   := PCK_DATOS.FC_ACME (UN_TABLA     => 'COMPROBANTE_CNT',
                                                           UN_ACCION    => 'M',
                                                           UN_CAMPOS    => MI_CAMPOS,
                                                           UN_CONDICION => MI_CONDICION);
                                                           COMMIT ;
                      --ACTUALIZA FACTURA
                    MI_CAMPOS :=  'CREDITOSAFECTADOS =  CREDITOSAFECTADOS 
                                               + ' || ' CASE WHEN ' ||  MI_VAL_DEBITO || ' >  DEBITO  
                                                          THEN DEBITO 
                                                          ELSE  ' || MI_VAL_DEBITO  || ' 
                                                         END  '  || '
                                  ,DEBITOSAFECTADOS =  DEBITOSAFECTADOS +  ' || MI_DEBITO_AFECTADOFAC;
                    
                    MI_CONDICION :=  '(COMPANIA || ANO||    TIPO || NUMERO ) IN(
                                          SELECT (COMPANIA || ANO_AFECT ||  TIPO_CPTE_AFECT || CMPTE_AFECTADO  ) AS DATO  
                                          FROM DETALLE_COMPROBANTE_CNT  
                                          WHERE COMPANIA  = ''' || UN_COMPANIA || ''' 
                                            AND ANO  =  ' || MI_ANIO   || ' 
                                            AND TIPO_CPTE =  ''' || MI_TIPO || ''' 
                                            AND COMPROBANTE =  ' || MI_NUMERO || ' 
                                            AND (TIPO_CPTE_AFECT IS NOT NULL OR TIPO_CPTE_AFECT NOT IN('''')) 
                                          ) ' ;
                                   
                     MI_PCKDATOS   := PCK_DATOS.FC_ACME (UN_TABLA     => 'COMPROBANTE_CNT',
                                                           UN_ACCION    => 'M',
                                                           UN_CAMPOS    => MI_CAMPOS,
                                                           UN_CONDICION => MI_CONDICION);
                         
                      MI_CAMPOS :=  'CREDITO_AFECTADO =  CREDITO_AFECTADO + ' || ' 
                                                         CASE WHEN ' ||  MI_VAL_DEBITO || ' >  VALOR_DEBITO  
                                                          THEN VALOR_DEBITO 
                                                          ELSE  ' || MI_VAL_DEBITO  || ' 
                                                         END  '  || '
                                     ,DEBITO_AFECTADO =  DEBITO_AFECTADO +  ' || MI_DEBITO_AFECTADOFAC;
                    --ACTUALIZA FACTURA 
                    MI_CONDICION :=  '(COMPANIA || ANO||    TIPO_CPTE|| COMPROBANTE || CONSECUTIVO ) IN(
                                          SELECT (COMPANIA || ANO_AFECT ||  TIPO_CPTE_AFECT || CMPTE_AFECTADO || CONSECUTIVOAFECTADO ) AS DATO  
                                          FROM DETALLE_COMPROBANTE_CNT  
                                          WHERE COMPANIA  = ''' || UN_COMPANIA || ''' 
                                            AND ANO  =  ' || MI_ANIO   || ' 
                                            AND TIPO_CPTE =  ''' || MI_TIPO || ''' 
                                            AND COMPROBANTE =  ' || MI_NUMERO || ' 
                                            AND (TIPO_CPTE_AFECT IS NOT NULL OR TIPO_CPTE_AFECT NOT IN('''')) 
                                          ) ' ;
                    
                     MI_PCKDATOS   := PCK_DATOS.FC_ACME (UN_TABLA     => 'DETALLE_COMPROBANTE_CNT',
                                                           UN_ACCION    => 'M',
                                                           UN_CAMPOS    => MI_CAMPOS,
                                                           UN_CONDICION => MI_CONDICION);
                                                           
                                                    
                    MI_CAMPOS    := 'DESCRIPCION =''' || SUBSTR(MI_MENSAJE || ':'|| MI_NUMERO,1,250) || ''',
                                     MODIFIED_BY =''' || UN_USUARIO || ''',
                                     DATE_MODIFIED =SYSDATE';
                    MI_CONDICION := 'COMPANIA        = ''' || UN_COMPANIA    || 
                                 ''' AND ANO         = '   || UN_ANOACTUAL   ||
                                   ' AND TIPO        = ''' || UN_TIPOACTUAL  || 
                                 ''' AND NUMERO      = '   || UN_NUMEROACTUAL;            
                    MI_PCKDATOS   := PCK_DATOS.FC_ACME (UN_TABLA     => 'COMPROBANTE_CNT',
                                                           UN_ACCION    => 'M',
                                                           UN_CAMPOS    => MI_CAMPOS,
                                                           UN_CONDICION => MI_CONDICION);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE,
                                                UN_TABLAERROR => 'COMPROBANTE_CNT',
                                                UN_ERROR_COD => PCK_ERRORES.ERR_DETALLE_AFECTADO
                    );
                END;
            END IF;

            IF((UN_CLASE ='A'OR UN_CLASE ='T') AND MI_REINTEGRO <> 0)THEN
                BEGIN 
                    MI_MENSAJE := 'REINTEGRO '|| MI_DESCRI;                        
                    MI_CAMPOS    := 'DESCRIPCION =''' || SUBSTR(MI_MENSAJE,1,250) || ''',
                                 MODIFIED_BY =''' || UN_USUARIO || ''',
                                 DATE_MODIFIED =SYSDATE';
                    MI_CONDICION := 'COMPANIA        = ''' || UN_COMPANIA    || 
                                 ''' AND ANO         = '   || MI_ANIO   ||
                                   ' AND TIPO        = ''' || MI_TIPO  || 
                                 ''' AND NUMERO      = '   || MI_NUMERO;            
                    MI_PCKDATOS   := PCK_DATOS.FC_ACME (UN_TABLA     => 'COMPROBANTE_CNT',
                                                           UN_ACCION    => 'M',
                                                           UN_CAMPOS    => MI_CAMPOS,
                                                           UN_CONDICION => MI_CONDICION);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE,
                                                UN_TABLAERROR => 'COMPROBANTE_CNT',
                                                UN_ERROR_COD => PCK_ERRORES.ERR_DETALLE_AFECTADO
                    );
                END;
            END IF;
        END; 
    END LOOP;
    
       --ACTUALIZA FACTURA
                    MI_CAMPOS :=  'CREDITOSAFECTADOS =   CASE WHEN CREDITOSAFECTADOS  >  DEBITO  
                                                          THEN DEBITO 
                                                          ELSE   CREDITOSAFECTADOS
                                                         END ';
                               
                    
                    MI_CONDICION :=  '(COMPANIA || ANO||    TIPO || NUMERO ) IN(
                                          SELECT (COMPANIA || ANO_AFECT ||  TIPO_CPTE_AFECT || CMPTE_AFECTADO  ) AS DATO  
                                          FROM DETALLE_COMPROBANTE_CNT  
                                          WHERE COMPANIA  = ''' || UN_COMPANIA || ''' 
                                            AND ANO  =  ' || MI_ANIO   || ' 
                                            AND TIPO_CPTE =  ''' || MI_TIPO || ''' 
                                            AND COMPROBANTE =  ' || MI_NUMERO || ' 
                                            AND (TIPO_CPTE_AFECT IS NOT NULL OR TIPO_CPTE_AFECT NOT IN('''')) 
                                          ) ' ;
                                   
                     MI_PCKDATOS   := PCK_DATOS.FC_ACME (UN_TABLA     => 'COMPROBANTE_CNT',
                                                           UN_ACCION    => 'M',
                                                           UN_CAMPOS    => MI_CAMPOS,
                                                           UN_CONDICION => MI_CONDICION);
                         
                      MI_CAMPOS :=  'CREDITO_AFECTADO = CASE WHEN CREDITO_AFECTADO  >  VALOR_DEBITO  
                                                          THEN VALOR_DEBITO 
                                                          ELSE  CREDITO_AFECTADO
                                                         END  ';
                    --ACTUALIZA FACTURA 
                    MI_CONDICION :=  '(COMPANIA || ANO||    TIPO_CPTE|| COMPROBANTE || CONSECUTIVO ) IN(
                                          SELECT (COMPANIA || ANO_AFECT ||  TIPO_CPTE_AFECT || CMPTE_AFECTADO || CONSECUTIVOAFECTADO ) AS DATO  
                                          FROM DETALLE_COMPROBANTE_CNT  
                                          WHERE COMPANIA  = ''' || UN_COMPANIA || ''' 
                                            AND ANO  =  ' || MI_ANIO   || ' 
                                            AND TIPO_CPTE =  ''' || MI_TIPO || ''' 
                                            AND COMPROBANTE =  ' || MI_NUMERO || ' 
                                            AND (TIPO_CPTE_AFECT IS NOT NULL OR TIPO_CPTE_AFECT NOT IN('''')) 
                                          ) ' ;
                    
                     MI_PCKDATOS   := PCK_DATOS.FC_ACME (UN_TABLA     => 'DETALLE_COMPROBANTE_CNT',
                                                           UN_ACCION    => 'M',
                                                           UN_CAMPOS    => MI_CAMPOS,
                                                           UN_CONDICION => MI_CONDICION);
                  
                  
                  PCK_CONTABILIDAD6.PR_CALCULAR_VALORAGIRAR( UN_COMPANIA            => UN_COMPANIA
                                                ,UN_ANIO                 => UN_ANOACTUAL
                                                ,UN_TIPO                 => UN_TIPOACTUAL
                                                ,UN_NUMERO               => UN_NUMEROACTUAL
                                                ,UN_VLRDOCUMENTO         => 0
                                                ,UN_VLRGIRARDG           => 0);
                                                           
    
END PR_GENERARANULACION;
--
PROCEDURE PR_ACTCONTSALDOAUX
(
  /*
    NAME              : PR_ACTCONTSALDOAUX 
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
    DATE MIGRADOR     : 21/08/2018
    TIME              : 9:20 AM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Procedimiento que actualiza los saldos de la 
                        tabla saldo aux al momento de ejecutar la funcion de cambio de nit de terceros

    MODIFICATIONS     : 
    @NAME:  actualizarSaldoAuxCont
    @METHOD:  get
  */ 
    UN_COMPANIA_ANT     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TERCERO_ANT      IN PCK_SUBTIPOS.TI_TERCERO,
    UN_SUCURSAL_ANT     IN PCK_SUBTIPOS.TI_SUCURSAL,
    UN_COMPANIA_NUE     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TERCERO_NUE      IN PCK_SUBTIPOS.TI_TERCERO,
    UN_SUCURSAL_NUE     IN PCK_SUBTIPOS.TI_SUCURSAL,
    UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO
)
AS
    MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
    MI_NETO           PCK_SUBTIPOS.TI_DOBLE;
    MI_NETO_ANT       PCK_SUBTIPOS.TI_DOBLE;  
    MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
    MI_CREA_CUENTA    PCK_SUBTIPOS.TI_ENTERO;
    MI_ERROR          PCK_SUBTIPOS.TI_CLAVEVALOR;


    BEGIN
        MI_TABLA:='SALDO_AUX_CONTABLE';
        MI_CREA_CUENTA:=1;  
        <<SALDO_AUX_TERCERO_ANTERIOR>>
        FOR MI_RSANTERIOR IN (  SELECT  
                              COMPANIA,
                              ANO,
                              CODIGO,
                              CENTRO_COSTO,
                              TERCERO,
                              SUCURSAL,
                              AUXILIAR,
                              REFERENCIA,
                              FUENTE_RECURSO,
                              NATURALEZA,
                              SALDOINICIAL,
                              SALDO0,SALDO1,SALDO2,SALDO3,SALDO4,SALDO5,SALDO6,SALDO7,SALDO8,SALDO9,SALDO10,SALDO11,SALDO12,SALDO13,                                  
                              NETO0,NETO1,NETO2,NETO3,NETO4,NETO5,NETO6,NETO7,NETO8,NETO9,NETO10,NETO11,NETO12,NETO13,
                              DEBITO0,DEBITO1,DEBITO2,DEBITO3,DEBITO4,DEBITO5,DEBITO6,DEBITO7,DEBITO8,DEBITO9,DEBITO10,DEBITO11,DEBITO12,DEBITO13,
                              CREDITO0,CREDITO1,CREDITO2,CREDITO3,CREDITO4,CREDITO5,CREDITO6,CREDITO7,CREDITO8,CREDITO9,CREDITO10,CREDITO11,CREDITO12,CREDITO13,
                              AJUSTE0,AJUSTE1,AJUSTE2,AJUSTE3,AJUSTE4,AJUSTE5,AJUSTE6,AJUSTE7,AJUSTE8,AJUSTE9,AJUSTE10,AJUSTE11,AJUSTE12,AJUSTE13
                          FROM SALDO_AUX_CONTABLE
                          WHERE COMPANIA    = UN_COMPANIA_ANT
                            AND TERCERO     = UN_TERCERO_ANT
                            AND SUCURSAL    = UN_SUCURSAL_ANT)
        LOOP
            SELECT COUNT(COMPANIA)
                INTO MI_CREA_CUENTA
            FROM  SALDO_AUX_CONTABLE 
            WHERE COMPANIA        = UN_COMPANIA_NUE
                AND ANO             = MI_RSANTERIOR.ANO 
                AND CODIGO          = MI_RSANTERIOR.CODIGO
                AND CENTRO_COSTO    = MI_RSANTERIOR.CENTRO_COSTO
                AND TERCERO         = UN_TERCERO_NUE
                AND SUCURSAL        = UN_SUCURSAL_NUE
                AND AUXILIAR        = MI_RSANTERIOR.AUXILIAR
                AND REFERENCIA      = MI_RSANTERIOR.REFERENCIA
                AND FUENTE_RECURSO  = MI_RSANTERIOR.FUENTE_RECURSO;

      IF MI_CREA_CUENTA = 0 THEN 
                BEGIN
          MI_CAMPOS :='COMPANIA,ANO,CODIGO,CENTRO_COSTO,TERCERO,SUCURSAL,AUXILIAR,REFERENCIA,FUENTE_RECURSO,NATURALEZA,SALDOINICIAL,' ||
                      'SALDO0,SALDO1,SALDO2,SALDO3,SALDO4,SALDO5,SALDO6,SALDO7,SALDO8,SALDO9,SALDO10,SALDO11,SALDO12,SALDO13,'||
                      'NETO0,NETO1,NETO2,NETO3,NETO4,NETO5,NETO6,NETO7,NETO8,NETO9,NETO10,NETO11,NETO12,NETO13,'||
                      'DEBITO0,DEBITO1,DEBITO2,DEBITO3,DEBITO4,DEBITO5,DEBITO6,DEBITO7,DEBITO8,DEBITO9,DEBITO10,DEBITO11,DEBITO12,DEBITO13,'||
                      'CREDITO0,CREDITO1,CREDITO2,CREDITO3,CREDITO4,CREDITO5,CREDITO6,CREDITO7,CREDITO8,CREDITO9,CREDITO10,CREDITO11,CREDITO12,CREDITO13,'||
                      'AJUSTE0,AJUSTE1,AJUSTE2,AJUSTE3,AJUSTE4,AJUSTE5,AJUSTE6,AJUSTE7,AJUSTE8,AJUSTE9,AJUSTE10,AJUSTE11,AJUSTE12,AJUSTE13,
                      CREATED_BY,DATE_CREATED';

          MI_VALORES := ''''||UN_COMPANIA_NUE||''',
                          '||MI_RSANTERIOR.ANO||',
                        '''||MI_RSANTERIOR.CODIGO||''',
                        '''||MI_RSANTERIOR.CENTRO_COSTO||''',
                        '''||UN_TERCERO_NUE||''',
                        '''||UN_SUCURSAL_NUE||''',
                        '''||MI_RSANTERIOR.AUXILIAR||''',
                        '''||MI_RSANTERIOR.REFERENCIA||''',
                        '''||MI_RSANTERIOR.FUENTE_RECURSO||''',
                        '''||MI_RSANTERIOR.NATURALEZA||''',
                        '||MI_RSANTERIOR.SALDOINICIAL||',
                        '||MI_RSANTERIOR.SALDO0||',
                        '||MI_RSANTERIOR.SALDO1||',
                        '||MI_RSANTERIOR.SALDO2||',
                        '||MI_RSANTERIOR.SALDO3||',
                        '||MI_RSANTERIOR.SALDO4||',
                        '||MI_RSANTERIOR.SALDO5||',
                        '||MI_RSANTERIOR.SALDO6||',
                        '||MI_RSANTERIOR.SALDO7||',
                        '||MI_RSANTERIOR.SALDO8||',
                        '||MI_RSANTERIOR.SALDO9||',
                        '||MI_RSANTERIOR.SALDO10||',
                        '||MI_RSANTERIOR.SALDO11||',
                        '||MI_RSANTERIOR.SALDO12||',
                        '||MI_RSANTERIOR.SALDO13||',
                        '||MI_RSANTERIOR.NETO0||',
                        '||MI_RSANTERIOR.NETO1||',
                        '||MI_RSANTERIOR.NETO2||',
                        '||MI_RSANTERIOR.NETO3||',
                        '||MI_RSANTERIOR.NETO4||',
                        '||MI_RSANTERIOR.NETO5||',
                        '||MI_RSANTERIOR.NETO6||',
                        '||MI_RSANTERIOR.NETO7||',
                        '||MI_RSANTERIOR.NETO8||',
                        '||MI_RSANTERIOR.NETO9||',
                        '||MI_RSANTERIOR.NETO10||',
                        '||MI_RSANTERIOR.NETO11||',
                        '||MI_RSANTERIOR.NETO12||',
                        '||MI_RSANTERIOR.NETO13||',
                        '||MI_RSANTERIOR.DEBITO0||',
                        '||MI_RSANTERIOR.DEBITO1||',
                        '||MI_RSANTERIOR.DEBITO2||',
                        '||MI_RSANTERIOR.DEBITO3||',
                        '||MI_RSANTERIOR.DEBITO4||',
                        '||MI_RSANTERIOR.DEBITO5||',
                        '||MI_RSANTERIOR.DEBITO6||',
                        '||MI_RSANTERIOR.DEBITO7||',
                        '||MI_RSANTERIOR.DEBITO8||',
                        '||MI_RSANTERIOR.DEBITO9||',
                        '||MI_RSANTERIOR.DEBITO10||',
                        '||MI_RSANTERIOR.DEBITO11||',
                        '||MI_RSANTERIOR.DEBITO12||',
                        '||MI_RSANTERIOR.DEBITO13||',
                        '||MI_RSANTERIOR.CREDITO0||',
                        '||MI_RSANTERIOR.CREDITO1||',
                        '||MI_RSANTERIOR.CREDITO2||',
                        '||MI_RSANTERIOR.CREDITO3||',
                        '||MI_RSANTERIOR.CREDITO4||',
                        '||MI_RSANTERIOR.CREDITO5||',
                        '||MI_RSANTERIOR.CREDITO6||',
                        '||MI_RSANTERIOR.CREDITO7||',
                        '||MI_RSANTERIOR.CREDITO8||',
                        '||MI_RSANTERIOR.CREDITO9||',
                        '||MI_RSANTERIOR.CREDITO10||',
                        '||MI_RSANTERIOR.CREDITO11||',
                        '||MI_RSANTERIOR.CREDITO12||',
                        '||MI_RSANTERIOR.CREDITO13||',
                        '||MI_RSANTERIOR.AJUSTE0||',
                        '||MI_RSANTERIOR.AJUSTE1||',
                        '||MI_RSANTERIOR.AJUSTE2||',
                        '||MI_RSANTERIOR.AJUSTE3||',
                        '||MI_RSANTERIOR.AJUSTE4||',
                        '||MI_RSANTERIOR.AJUSTE5||',
                        '||MI_RSANTERIOR.AJUSTE6||',
                        '||MI_RSANTERIOR.AJUSTE7||',
                        '||MI_RSANTERIOR.AJUSTE8||',
                        '||MI_RSANTERIOR.AJUSTE9||',
                        '||MI_RSANTERIOR.AJUSTE10||',
                        '||MI_RSANTERIOR.AJUSTE11||',
                        '||MI_RSANTERIOR.AJUSTE12||',
                        '||MI_RSANTERIOR.AJUSTE13||',
                        '''||UN_USUARIO||''',
                        SYSDATE';

                    BEGIN

            PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(  UN_TABLA     => MI_TABLA, 
                                                  UN_ACCION    => 'I', 
                                                  UN_CAMPOS    => MI_CAMPOS,
                                                  UN_VALORES   => MI_VALORES);

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                    END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
        MI_ERROR(1).CLAVE := 'TABLA';
                MI_ERROR(1).VALOR := 'SALDO_AUX_CONTABLE';
                MI_ERROR(2).CLAVE := 'CAMPOS';
                MI_ERROR(2).VALOR :=  MI_CAMPOS;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                                            UN_EXC_COD    => SQLCODE,
                                            UN_TABLAERROR => 'SALDO_AUX_CONTABLE',
                                            UN_ERROR_COD  => PCK_ERRORES.ERR_PR_CALCULARM_INSERTAR,
                                            UN_REEMPLAZOS => MI_ERROR);
                END;                                
          ELSE

      BEGIN 
                MI_CAMPOS := '  DEBITO0       = DEBITO0 + '||MI_RSANTERIOR.DEBITO0||',
                        DEBITO1       = DEBITO1 + '||MI_RSANTERIOR.DEBITO1||',
                        DEBITO2       = DEBITO2 + '||MI_RSANTERIOR.DEBITO2||',
                        DEBITO3       = DEBITO3 + '||MI_RSANTERIOR.DEBITO3||',
                        DEBITO4       = DEBITO4 + '||MI_RSANTERIOR.DEBITO4||',
                        DEBITO5       = DEBITO5 + '||MI_RSANTERIOR.DEBITO5||',
                        DEBITO6       = DEBITO6 + '||MI_RSANTERIOR.DEBITO6||',
                        DEBITO7       = DEBITO7 + '||MI_RSANTERIOR.DEBITO7||',
                        DEBITO8       = DEBITO8 + '||MI_RSANTERIOR.DEBITO8||',
                        DEBITO9       = DEBITO9 + '||MI_RSANTERIOR.DEBITO9||',
                        DEBITO10      = DEBITO10 + '||MI_RSANTERIOR.DEBITO10||',
                        DEBITO11      = DEBITO11 + '||MI_RSANTERIOR.DEBITO11||',
                        DEBITO12      = DEBITO12 + '||MI_RSANTERIOR.DEBITO12||',
                        DEBITO13      = DEBITO13 + '||MI_RSANTERIOR.DEBITO13||',
                        CREDITO0      = CREDITO0 + '||MI_RSANTERIOR.CREDITO0||',
                        CREDITO1      = CREDITO1 + '||MI_RSANTERIOR.CREDITO1||',
                        CREDITO2      = CREDITO2 + '||MI_RSANTERIOR.CREDITO2||',
                        CREDITO3      = CREDITO3 + '||MI_RSANTERIOR.CREDITO3||',
                        CREDITO4      = CREDITO4 + '||MI_RSANTERIOR.CREDITO4||',
                        CREDITO5      = CREDITO5 + '||MI_RSANTERIOR.CREDITO5||',
                        CREDITO6      = CREDITO6 + '||MI_RSANTERIOR.CREDITO6||',
                        CREDITO7      = CREDITO7 + '||MI_RSANTERIOR.CREDITO7||',
                        CREDITO8      = CREDITO8 + '||MI_RSANTERIOR.CREDITO8||',
                        CREDITO9      = CREDITO9 + '||MI_RSANTERIOR.CREDITO9||',
                        CREDITO10     = CREDITO10 + '||MI_RSANTERIOR.CREDITO10||',
                        CREDITO11     = CREDITO11 + '||MI_RSANTERIOR.CREDITO11||',
                        CREDITO12     = CREDITO12 + '||MI_RSANTERIOR.CREDITO12||',
                        CREDITO13     = CREDITO13 + '||MI_RSANTERIOR.CREDITO13||',
                        SALDOINICIAL  = SALDOINICIAL + '||MI_RSANTERIOR.SALDOINICIAL||',
                        SALDO0        = SALDO0 + '||MI_RSANTERIOR.SALDO0||',
                        SALDO1        = SALDO1 + '||MI_RSANTERIOR.SALDO1||',
                        SALDO2        = SALDO2 + '||MI_RSANTERIOR.SALDO2||',
                        SALDO3        = SALDO3 + '||MI_RSANTERIOR.SALDO3||',
                        SALDO4        = SALDO4 + '||MI_RSANTERIOR.SALDO4||',
                        SALDO5        = SALDO5 + '||MI_RSANTERIOR.SALDO5||',
                        SALDO6        = SALDO6 + '||MI_RSANTERIOR.SALDO6||',
                        SALDO7        = SALDO7 + '||MI_RSANTERIOR.SALDO7||',
                        SALDO8        = SALDO8 + '||MI_RSANTERIOR.SALDO8||',
                        SALDO9        = SALDO9 + '||MI_RSANTERIOR.SALDO9||',
                        SALDO10       = SALDO10 + '||MI_RSANTERIOR.SALDO10||',
                        SALDO11       = SALDO11 + '||MI_RSANTERIOR.SALDO11||',
                        SALDO12       = SALDO12 + '||MI_RSANTERIOR.SALDO12||',
                        SALDO13       = SALDO13 + '||MI_RSANTERIOR.SALDO13||',
                        NETO0         = NETO0 + '||MI_RSANTERIOR.NETO0||',
                        NETO1         = NETO1 + '||MI_RSANTERIOR.NETO1||',
                        NETO2         = NETO2 + '||MI_RSANTERIOR.NETO2||',
                        NETO3         = NETO3 + '||MI_RSANTERIOR.NETO3||',
                        NETO4         = NETO4 + '||MI_RSANTERIOR.NETO4||',
                        NETO5         = NETO5 + '||MI_RSANTERIOR.NETO5||',
                        NETO6         = NETO6 + '||MI_RSANTERIOR.NETO6||',
                        NETO7         = NETO7 + '||MI_RSANTERIOR.NETO7||',
                        NETO8         = NETO8 + '||MI_RSANTERIOR.NETO8||',
                        NETO9         = NETO9 + '||MI_RSANTERIOR.NETO9||',
                        NETO10        = NETO10 + '||MI_RSANTERIOR.NETO10||',
                        NETO11        = NETO11 + '||MI_RSANTERIOR.NETO11||',
                        NETO12        = NETO12 + '||MI_RSANTERIOR.NETO12||',
                        NETO13        = NETO13 + '||MI_RSANTERIOR.NETO13||',
                        AJUSTE0       = AJUSTE0 + '||MI_RSANTERIOR.AJUSTE0||',
                        AJUSTE1       = AJUSTE1 + '||MI_RSANTERIOR.AJUSTE1||',
                        AJUSTE2       = AJUSTE2 + '||MI_RSANTERIOR.AJUSTE2||',
                        AJUSTE3       = AJUSTE3 + '||MI_RSANTERIOR.AJUSTE3||',
                        AJUSTE4       = AJUSTE4 + '||MI_RSANTERIOR.AJUSTE4||',
                        AJUSTE5       = AJUSTE5 + '||MI_RSANTERIOR.AJUSTE5||',
                        AJUSTE6       = AJUSTE6 + '||MI_RSANTERIOR.AJUSTE6||',
                        AJUSTE7       = AJUSTE7 + '||MI_RSANTERIOR.AJUSTE7||',
                        AJUSTE8       = AJUSTE8 + '||MI_RSANTERIOR.AJUSTE8||',
                        AJUSTE9       = AJUSTE9 + '||MI_RSANTERIOR.AJUSTE9||',
                        AJUSTE10      = AJUSTE10 + '||MI_RSANTERIOR.AJUSTE10||',
                        AJUSTE11      = AJUSTE11 + '||MI_RSANTERIOR.AJUSTE11||',
                        AJUSTE12      = AJUSTE12 + '||MI_RSANTERIOR.AJUSTE12||',
                        AJUSTE13      = AJUSTE13 + '||MI_RSANTERIOR.AJUSTE13||',
                        MODIFIED_BY   = '''||UN_USUARIO||''',
                        DATE_MODIFIED = SYSDATE';
                        MI_CONDICION := '     COMPANIA        = ''' || UN_COMPANIA_NUE    || '''' ||
                          '   AND ANO             =   ' || MI_RSANTERIOR.ANO  ||                
                          '   AND CODIGO          = ''' || MI_RSANTERIOR.CODIGO || '''' ||
                          '   AND TERCERO         = ''' || UN_TERCERO_NUE || '''' ||
                          '   AND SUCURSAL        = ''' || UN_SUCURSAL_NUE || '''' ||
                          '   AND CENTRO_COSTO    = ''' || MI_RSANTERIOR.CENTRO_COSTO || '''' ||
                          '   AND AUXILIAR        = ''' || MI_RSANTERIOR.AUXILIAR || '''' ||
                          '   AND REFERENCIA      = ''' || MI_RSANTERIOR.REFERENCIA || '''' ||
                          '   AND FUENTE_RECURSO  = ''' || MI_RSANTERIOR.FUENTE_RECURSO || '''';   
                        BEGIN 
                            PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(  UN_TABLA     => MI_TABLA, 
                                                    UN_ACCION    => 'M', 
                                                    UN_CAMPOS    => MI_CAMPOS,
                                                    UN_CONDICION => MI_CONDICION);

                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                        END;
                    EXCEPTION
                    WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                            MI_ERROR(1).CLAVE := 'TABLA';
                            MI_ERROR(1).VALOR := 'SALDO_AUX_CONTABLE';
                            MI_ERROR(2).CLAVE := 'CAMPOS';
                            MI_ERROR(2).VALOR :=  MI_CONDICION;
                            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                                        UN_TABLAERROR => 'SALDO_AUX_CONTABLE',
                                                        UN_ERROR_COD  => PCK_ERRORES.ERR_PR_CALCULARM_ACTUALIZAR,
                                                        UN_REEMPLAZOS => MI_ERROR);
                    END; 
        END IF;
                    
                    BEGIN
                    MI_TABLA:= MI_TABLA;        
                    MI_CONDICION:='       SALDO_AUX_CONTABLE.COMPANIA = ''' || UN_COMPANIA_ANT || ''''||
                        '   AND SALDO_AUX_CONTABLE.TERCERO  = ''' || UN_TERCERO_ANT || ''''||
                      '     AND SALDO_AUX_CONTABLE.SUCURSAL = ''' || UN_SUCURSAL_ANT || '''';                      

                    BEGIN
                        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA     => MI_TABLA,
                                                            UN_ACCION    => 'E',
                                                            UN_CONDICION => MI_CONDICION);                                 
                    EXCEPTION
                        WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                        RAISE  PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
                    END;
                EXCEPTION
                WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_CAMPOS:='COMPANIA    =''' || UN_COMPANIA_ANT || ''','||
                               'NIT         = ''' || UN_TERCERO_ANT || ''','||
                               'SUCURSAL    = ''' || UN_SUCURSAL_ANT || '''';
                     MI_ERROR (0).CLAVE := 'TABLA';
                     MI_ERROR (0).VALOR :=  MI_TABLA;    
                     MI_ERROR (1).CLAVE := 'CAMPOS';                    
                     MI_ERROR (1).VALOR :=  MI_CAMPOS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => MI_TABLA,
                                                UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_GENERAL_ELICAMBNIT,                                   
                                                UN_REEMPLAZOS  => MI_ERROR);
                END;    
            END LOOP SALDO_AUX_TERCERO_ANTERIOR;
END PR_ACTCONTSALDOAUX;         

--
FUNCTION FC_CLASECONTABLECPTE
/*
      NAME              : FC_CLASECONTABLECPTE => en Access ClaseContableCpte()
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 07/09/2018
      TIME              : 11:00 AM
      SOURCE MODULE     : SysmanSF2018.07.06
      DESCRIPTION       :
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 

    @NAME:   extraerClaseContableComprobante
    @METHOD: GET
*/
(
  UN_COMPANIA          IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOCPTE          IN SF_OBJETO_COBRO.TIPOCOBRO%TYPE
  )RETURN TIPO_COMPROBANTE.CLASE_CONTABLE%TYPE AS

  MI_CLASE TIPO_COMPROBANTE.CLASE_CONTABLE%TYPE;
BEGIN
    BEGIN  
      SELECT CLASE_CONTABLE
        INTO MI_CLASE  
        FROM TIPO_COMPROBANTE
      WHERE COMPANIA = UN_COMPANIA
        AND CODIGO   = UN_TIPOCPTE;


      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_CLASE := '';
    END;   


  RETURN MI_CLASE; 


END FC_CLASECONTABLECPTE;  


  FUNCTION FC_GENERARRTACONCILIADOSXTIPO
  /*
      NAME              : FC_GENERARRTACONCILIADOSXTIPO 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
      DATE MIGRADO      : 28/09/2018
      TIME              : 16:36 PM
      AUTHOR NODIFIED   : 
      DATE MIGRADO      : 
      TIME              : 
      SOURCE MODULE     : 
      DESCRIPTION       : Función que permite generar la respuesta para cada fila que definieron conciliar a partir de la información registrada
                          en una plantilla Excel.
                          Se redefine totalmente para que funcione adecuadamente

      PARAMETERS        : UN_COMPANIA           => Compañia de ingreso a la aplicación.  
                          UN_CUENTA             => ID de la cuenta a conciliar.
                          UN_CADENA             => Cadena que contiene los datos fila por fial separados por punto y coma (;)
                                                   de la información a conciliar a partir de la plantilla excel seleccionada.
                          UN_COLVALDEBITO       => Número de la columna seleccionada para el valor ingreso. 
                          UN_COLVALCREDITO      => Número de la columna seleccionada para el valor egreso.
                          UN_COLTIPO            => Número de la columna seleccionada para el tipo del comprobante.
                          UN_COLCOMPROBANTE     => Número de la columna seleccionada para el numero del comprobante.
                          UN_COLCONSECUTIVO     => Número de la columna seleccionada para el consecutivo del detalle del comprobante.
                          UN_FECHA              => Fecha  indicada para hacer la concicliacion.
                          UN_FILAINI            => Número de la fila inicio de la información.
                          UN_USUARIO            => Código del usuario que realiza la conciliación.
      @NAME:  generarRtaConciliadosXPlano
      @METHOD:  GET
      */  
  (
    UN_COMPANIA               IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CUENTA                 IN VARCHAR2,
    UN_CADENA                 IN CLOB,
    UN_COLVALDEBITO           IN PCK_SUBTIPOS.TI_ENTERO, 
    UN_COLVALCREDITO          IN PCK_SUBTIPOS.TI_ENTERO, 
    UN_COLTIPO                IN PCK_SUBTIPOS.TI_ENTERO,
    UN_COLCOMPROBANTE         IN PCK_SUBTIPOS.TI_ENTERO,
    UN_COLCONSECUTIVO         IN PCK_SUBTIPOS.TI_ENTERO,
    UN_COLFECHA               IN PCK_SUBTIPOS.TI_ENTERO,
    UN_FECHA                  IN DATE,
    UN_FILAINI                IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
    UN_USUARIO                IN VARCHAR2   
  )RETURN CLOB 
AS 
    MI_CADENA         CLOB;
    MI_CADENA_REG     CLOB;
    MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
    MI_RTAACME        PCK_SUBTIPOS.TI_RTA_ACME;
    MI_MSGERROREXC    PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_MERGEUSING     PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE    PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE    PCK_SUBTIPOS.TI_MERGEEXISTE;  
    MI_REGISTROS      PCK_SYSMAN_UTL.T_SPLIT;
    MI_REG_CAMPOS     PCK_SYSMAN_UTL.T_SPLIT;
    MI_DEBITO         PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_CREDITO        PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_FILA           PCK_SUBTIPOS.TI_ENTERO_LARGO;  
    MI_CONTROL        PCK_SUBTIPOS.TI_ENTERO_LARGO;  
    MI_ARCHSALIDA     VARCHAR2(2000);

    MI_TIPO           VARCHAR2(10);
    MI_COMPROBANTE    PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_CONSECUTIVO    PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_FECHA_REG      DATE; 
BEGIN
    MI_FILA := UN_FILAINI;
    MI_CADENA :='';


    BEGIN
        BEGIN
            MI_CONDICION := 'COMPANIA    = ''' || UN_COMPANIA || '''
                         AND CUENTA      = ''' || UN_CUENTA   || '''';    
            MI_RTAACME := PCK_DATOS.FC_ACME(UN_TABLA     => 'TEMP_CONCILIACION',
                                          UN_ACCION    => 'E', 
                                          UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
        MI_MSGERROREXC(1).CLAVE := 'CUENTA';
        MI_MSGERROREXC(1).VALOR := UN_CUENTA;
        MI_MSGERROREXC(2).CLAVE := 'ANIO';
        MI_MSGERROREXC(2).VALOR := TO_NUMBER(TO_CHAR(TO_DATE(UN_FECHA,'DD/MM/YYYY'),'YYYY'));
        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_DEL_PARTCONC,
                                    UN_REEMPLAZOS => MI_MSGERROREXC,
                                    UN_TABLAERROR => 'TEMP_CONCILIACION'
        );
    END;

    MI_REGISTROS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => '' || CASE WHEN SUBSTR(UN_CADENA, LENGTH(UN_CADENA),1) ='#' THEN SUBSTR(UN_CADENA,1,LENGTH(UN_CADENA)-1) ELSE UN_CADENA END || '',
                                                UN_DELIMITADOR  => '#');
    <<RECORRE_REGISTROS>>
    FOR RS IN MI_REGISTROS.FIRST..MI_REGISTROS.LAST 
    LOOP
        MI_CADENA_REG := ' ';
        MI_REG_CAMPOS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => '' || TO_CHAR(MI_REGISTROS(RS)) || '', 
                                                     UN_DELIMITADOR  => ';');
        BEGIN 
            BEGIN 
                BEGIN
                    MI_DEBITO       := TO_NUMBER(NVL(TO_CHAR(MI_REG_CAMPOS(UN_COLVALDEBITO)),0));
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG  || '3; - Incosistencia en el Débito ' || TO_CHAR(MI_REG_CAMPOS(UN_COLVALDEBITO));
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_CREDITO      := TO_NUMBER(NVL(TO_CHAR(MI_REG_CAMPOS(UN_COLVALCREDITO)),0));
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en el credito ' || TO_CHAR(MI_REG_CAMPOS(UN_COLVALCREDITO));
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_TIPO      := NVL(TO_CHAR(MI_REG_CAMPOS(UN_COLTIPO)),' ');
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en el tipo de comprobante ' || MI_REG_CAMPOS(UN_COLTIPO);
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_COMPROBANTE    := NVL(TO_CHAR(MI_REG_CAMPOS(UN_COLCOMPROBANTE)),' ');                
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en el comprobante ' || MI_REG_CAMPOS(UN_COLCOMPROBANTE);
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_CONSECUTIVO    := TO_NUMBER(NVL(TO_CHAR(MI_REG_CAMPOS(UN_COLCONSECUTIVO)),0));               
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en el consecutivo ' || MI_REG_CAMPOS(UN_COLCONSECUTIVO);
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_FECHA_REG    := TO_DATE(MI_REG_CAMPOS(UN_COLFECHA),'DD/MM/YYYY');
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en la fecha ' || MI_REG_CAMPOS(UN_COLFECHA) || ' formato DD/MM/YYYY';
                    GOTO SIGUIENTE;
                END;
            EXCEPTION WHEN NO_DATA_FOUND THEN
               RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
         PCK_ERR_MSG.RAISE_WITH_MSG(
           UN_EXC_COD    => SQLCODE,
           UN_ERROR_COD  => PCK_ERRORES.ERR_CONT_RANGOREGCONCILIACION
         );                      
        END;
        IF MI_DEBITO = 0 AND MI_CREDITO =0 THEN
            MI_CADENA_REG  := MI_CADENA_REG || '3; - Los valores estan en cero ' ;
        END IF;
        <<SIGUIENTE>>      

        --CARGA LA TEMPORAL
        BEGIN
            BEGIN
                MI_CAMPOS    := 'COMPANIA,
                                 ANO,
                                 CONSECUTIVO,
                                 CUENTA,
                                 FECHA_CONCILIA,
                                 VALOR_DEBITO,
                                 VALOR_CREDITO,
                                 MENSAJE,
                                 PASA,
                                 TIPO_CPTE,
                                 COMPROBANTE,
                                 COMPROBANTE_CONS';

                MI_VALORES   :=''''|| UN_COMPANIA         ||''',
                               '   || TO_CHAR(MI_FECHA_REG,'YYYY')        ||',
                               '   || MI_FILA             ||',
                               ''' || UN_CUENTA           ||''',
                                  TO_DATE(''' || TO_CHAR( LAST_DAY(UN_FECHA), 'DD/MM/YYYY HH24:mi:ss') ||''', ''DD/MM/YYYY HH24:mi:ss''),
                               '   || MI_DEBITO           ||',
                               '   || MI_CREDITO          ||',
                               ''' || MI_CADENA_REG       ||''',
                               '   || CASE WHEN MI_CADENA_REG = ' ' THEN -1 ELSE 0 END || ',
                               ''' || MI_TIPO             || ''',
                               '   || MI_COMPROBANTE      || ',
                               '   || MI_CONSECUTIVO        ; 

                MI_RTAACME       := PCK_DATOS.FC_ACME (UN_TABLA     => 'TEMP_CONCILIACION',
                                                       UN_ACCION    => 'I',
                                                       UN_CAMPOS    => MI_CAMPOS,
                                                       UN_VALORES   => MI_VALORES);           

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
          END  ;      

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
              MI_MSGERROREXC(1).CLAVE := 'FECHA';
              MI_MSGERROREXC(1).VALOR := TO_CHAR(MI_FECHA_REG,'DD/MM/YYYY');
              MI_MSGERROREXC(2).CLAVE := 'ANIO';
              MI_MSGERROREXC(2).VALOR := TO_CHAR(MI_FECHA_REG,'YYYY');
              MI_MSGERROREXC(3).CLAVE := 'COMPROBANTE';
              MI_MSGERROREXC(3).VALOR := 0;            
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                      UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_ACTDETCOMCONC,
                                      UN_REEMPLAZOS => MI_MSGERROREXC,
                                      UN_TABLAERROR => 'TEMP_CONCILIACION'
              ) ;         

        END;

        MI_FILA := MI_FILA +1 ;   

    END LOOP RECORRE_REGISTROS;   

    --ACTUALIZA los existentes en el detalle
    BEGIN
        DECLARE
            RTA NUMBER;
        BEGIN
            MI_MERGEUSING := 'SELECT D.COMPANIA, 
                                        D.ANO, 
                                        D.CUENTA, 
                                        D.VALOR_DEBITO, 
                                        D.VALOR_CREDITO,
                                        D.TIPO_CPTE,
                                        D.COMPROBANTE,
                                        D.CONSECUTIVO,
                                        D.FECHA_CONCILIA,
                                        D.CONCILIADOR,
                                        D.PAGADOBANCO
                                 FROM TIPO_COMPROBANTE T INNER JOIN DETALLE_COMPROBANTE_CNT D
                                   ON D.COMPANIA    = T.COMPANIA
                                  AND D.TIPO_CPTE   = T.CODIGO
                                 WHERE D.COMPANIA   = ''' || UN_COMPANIA || ''' 
                                   AND D.CUENTA     = ''' || UN_CUENTA   || ''' 
                                   AND T.CLASE_CONTABLE IN(''I'',''S'',''E'',''B'',''G'',''D'',''A'')';                             
            MI_MERGEENLACE := 'TABLA.COMPANIA         = VISTA.COMPANIA
                           AND TABLA.ANO              = VISTA.ANO
                           AND TABLA.TIPO_CPTE        = VISTA.TIPO_CPTE
                           AND TABLA.COMPROBANTE      = VISTA.COMPROBANTE
                           AND TABLA.COMPROBANTE_CONS = VISTA.CONSECUTIVO
                           AND TABLA.VALOR_DEBITO     = VISTA.VALOR_DEBITO
                           AND TABLA.VALOR_CREDITO    = VISTA.VALOR_CREDITO';    
            MI_MERGEEXISTE := 'UPDATE SET CONTADOR         = 1,
                                          COMPROBANTE_FECHA_CON   = VISTA.FECHA_CONCILIA,
                                          COMPROBANTE_CONCILIADOR = VISTA.CONCILIADOR,
                                          COMPROBANTE_PAGADOBANCO = VISTA.PAGADOBANCO
                                WHERE PASA NOT IN (0)';

            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => 'TEMP_CONCILIACION',
                                                UN_ACCION     => 'MM',
                                                UN_MERGEUSING => MI_MERGEUSING,
                                                UN_MERGEENLACE=> MI_MERGEENLACE,
                                                UN_MERGEEXISTE=> MI_MERGEEXISTE); 
            RTA:=PCK_DATOS.GL_RTA;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;    
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                                 UN_EXC_COD    => SQLCODE,
                                 UN_ERROR_COD  => PCK_ERRORES.ERR_PREPARACONCILIA,
                                 UN_TABLAERROR => 'TEMP_CONCILIACION'
        );  
    END;

    --ACTUALIZA ERRORES POR NO EXISTENCIA
    BEGIN
        BEGIN

            MI_CAMPOS := 'MENSAJE    = ''3;- No existe.  Tipo comprobante = '' || TIPO_CPTE || '', Comprobante = '' || COMPROBANTE || '', Consecutivo = '' || COMPROBANTE_CONS || '', Cuenta = '' || CUENTA,
                          PASA       = 0';

            MI_CONDICION := 'COMPANIA         = ''' || UN_COMPANIA    || ''' 
                         AND CUENTA           = ''' || UN_CUENTA      || ''' 
                         AND PASA             = -1
                         AND CONTADOR         = 0';

            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                                UN_TABLA => 'TEMP_CONCILIACION', 
                                                UN_ACCION => 'M', 
                                                UN_CAMPOS => MI_CAMPOS, 
                                                UN_CONDICION => MI_CONDICION
                              );

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD => SQLCODE,
                      UN_TABLAERROR => 'COMPROBANTE_CNT',
                      UN_ERROR_COD => PCK_ERRORES.ERR_PREPARACONCILIAERR
                    );
        END;

    --ACTUALIZA ADVETENCIA POR REGISTROS YA CONCILIADOS
    BEGIN
        BEGIN

            MI_CAMPOS := 'MENSAJE    = ''1;- Registro ya conciliado ,  Fecha de Conciliado = '' || TO_CHAR(COMPROBANTE_FECHA_CON,''DD/MM/YYYY'') || '', Conciliador = '' || COMPROBANTE_CONCILIADOR,
                          PASA       = 0';

            MI_CONDICION := 'COMPANIA         = ''' || UN_COMPANIA    || ''' 
                         AND CUENTA           = ''' || UN_CUENTA      || ''' 
                         AND COMPROBANTE_PAGADOBANCO NOT IN(0)
                         AND PASA       = -1
                         AND CONTADOR IN(1)';

            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                                UN_TABLA => 'TEMP_CONCILIACION', 
                                                UN_ACCION => 'M', 
                                                UN_CAMPOS => MI_CAMPOS, 
                                                UN_CONDICION => MI_CONDICION
                              );

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD => SQLCODE,
                  UN_TABLAERROR => 'COMPROBANTE_CNT',
                  UN_ERROR_COD => PCK_ERRORES.ERR_PREPARACONCILIAERR
                ); 
    END;

    --CONCILIA
    BEGIN
        BEGIN
            MI_MERGEUSING := 'SELECT COMPANIA,
                                     ANO,
                                     TIPO_CPTE,
                                     COMPROBANTE,
                                     COMPROBANTE_CONS,
                                     FECHA_CONCILIA
                              FROM TEMP_CONCILIACION                               
                              WHERE TEMP_CONCILIACION.COMPANIA   = ''' || UN_COMPANIA || '''
                                AND TEMP_CONCILIACION.CUENTA     = ''' || UN_CUENTA   || ''' 
                                AND TEMP_CONCILIACION.PASA NOT IN(0)
                                AND TEMP_CONCILIACION.CONTADOR IN(1)
                                AND TEMP_CONCILIACION.COMPROBANTE_PAGADOBANCO IN(0)';
            MI_MERGEENLACE := ' TABLA.COMPANIA         = VISTA.COMPANIA
                            AND TABLA.ANO              = VISTA.ANO
                            AND TABLA.TIPO_CPTE        = VISTA.TIPO_CPTE
                            AND TABLA.COMPROBANTE      = VISTA.COMPROBANTE
                            AND TABLA.CONSECUTIVO      = VISTA.COMPROBANTE_CONS';
            MI_MERGEEXISTE := 'UPDATE SET CONCILIADOR    = ''' || UN_USUARIO || '''
                                        , FECHA_CONCILIA = VISTA.FECHA_CONCILIA
                                        , PAGADOBANCO    = -1';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => 'DETALLE_COMPROBANTE_CNT',
                                                UN_ACCION     => 'MM',
                                                UN_MERGEUSING => MI_MERGEUSING,
                                                UN_MERGEENLACE=> MI_MERGEENLACE,
                                                UN_MERGEEXISTE=> MI_MERGEEXISTE); 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE 
                            THEN RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;    
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                                 UN_EXC_COD    => SQLCODE,
                                 UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_REG_PARTCONC,
                                 UN_TABLAERROR => 'TEMP_CONCILIACION'
        );  
    END;

    BEGIN
        BEGIN
            MI_CAMPOS := 'MENSAJE    = ''2; - Conciliado. Tipo comprobante = '' || TIPO_CPTE || '', Comprobante = '' || COMPROBANTE || '', Consecutivo = '' || COMPROBANTE_CONS || '', Fecha de Conciliación = '' || TO_CHAR(FECHA_CONCILIA,''DD/MM/YYYY'') ,
                          PASA       = 0';
            MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || ''' 
                         AND CUENTA   = ''' || UN_CUENTA   || ''' 
                         AND PASA NOT IN(0)
                         AND CONTADOR IN(1)
                         AND COMPROBANTE_PAGADOBANCO IN(0)';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                                UN_TABLA => 'TEMP_CONCILIACION', 
                                                UN_ACCION => 'M', 
                                                UN_CAMPOS => MI_CAMPOS, 
                                                UN_CONDICION => MI_CONDICION
                              );
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD => SQLCODE,
                  UN_TABLAERROR => 'COMPROBANTE_CNT',
                  UN_ERROR_COD => PCK_ERRORES.ERR_PREPARACONCILIAERR
                ); 
    END;

    MI_CADENA:='';
    <<SALIDA>>
    FOR RS IN(SELECT *
              FROM TEMP_CONCILIACION
              WHERE TEMP_CONCILIACION.COMPANIA   =  UN_COMPANIA 
                AND TEMP_CONCILIACION.CUENTA     =  UN_CUENTA
                )
    LOOP
        BEGIN
            MI_CADENA := MI_CADENA || TO_CHAR(RS.CONSECUTIVO) || ';' || TRIM(REPLACE(REPLACE (RS.MENSAJE, CHR(13),' ') , CHR(10) ,' ')) || CHR(13) || CHR(10);
        EXCEPTION WHEN OTHERS 
            THEN RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;    
    END LOOP SALIDA;

    /*
    BEGIN
        BEGIN
            MI_CONDICION := 'COMPANIA    = ''' || UN_COMPANIA || '''
                         AND CUENTA      = ''' || UN_CUENTA   || '''
                         AND ANO         = '   || MI_ANIO     ;    
            MI_RTAACME := PCK_DATOS.FC_ACME(UN_TABLA     => 'TEMP_CONCILIACION',
                                          UN_ACCION    => 'E', 
                                          UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
        MI_MSGERROREXC(1).CLAVE := 'CUENTA';
        MI_MSGERROREXC(1).VALOR := UN_CUENTA;
        MI_MSGERROREXC(2).CLAVE := 'ANIO';
        MI_MSGERROREXC(2).VALOR := MI_ANIO;
        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_DEL_PARTCONC,
                                    UN_REEMPLAZOS => MI_MSGERROREXC,
                                    UN_TABLAERROR => 'TEMP_CONCILIACION'
        );
    END;
    */
    RETURN MI_CADENA;
END FC_GENERARRTACONCILIADOSXTIPO;

--7713320_CONTABILIDAD (CARENAS)
FUNCTION FC_PREPPIVOTCONSULTADESCUENTOS
/*
    NAME              : NO APLICA
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : CRISTIAN FELIPE ARENAS GALVIS
    DATE MIGRADOR     : 22/04/2022
    TIME              : 11:30 AM
    SOURCE MODULE     : CONTABILIDAD .accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : RETORNA UNA CADENA PARA UN CAMPO EN UN REPORTE
    PARAMETERS        : UN_COMPANIA    => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                        UN_FECHAINICIAL => FECHA INCIAL POR EL CUAL SE VA A FILTRAR LAS CONSULTAS DE DESCUENTOS.
                        UN_FECHAFINAL   => FECHA FINAL POR EL CUAL SE VA A FILTRAR LAS CONSULTAS DE DESCUENTOS.
                        UN_TIPO => TIPO DE COMPROBANTE POR EL CUAL SE VA A FILTRAR LAS CONSULTAS DE DESCUENTOS.
    @NAME: getPivotPlanContable
  */
(
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHAINICIAL   IN DATE,
    UN_FECHAFINAL    IN DATE,
    UN_PARAMETRO    IN PCK_SUBTIPOS.TI_PARAMETRO,
    UN_TIPO           IN DETALLE_COMPROBANTE_CNT.TIPO_CPTE%TYPE
) 
RETURN CLOB
  AS
    MI_RESULTADO  CLOB;
    MI_RES        CLOB;
BEGIN
  BEGIN
  
    select listagg(x.cuenta,',')      
    within group (order by x.cuenta)
    into MI_RESULTADO
from (SELECT
    ''''||detalle_comprobante_cnt.cuenta||'''' cuenta
FROM
    tercero
    INNER JOIN detalle_comprobante_cnt left
    JOIN plan_contable ON detalle_comprobante_cnt.compania = plan_contable.compania
                          AND detalle_comprobante_cnt.ano = plan_contable.ano
                          AND detalle_comprobante_cnt.cuenta = plan_contable.codigo ON tercero.nit = detalle_comprobante_cnt.tercero
                                                                                       AND tercero.sucursal = detalle_comprobante_cnt
                                                                                       .sucursal
                                                                                       AND tercero.compania = detalle_comprobante_cnt
                                                                                       .compania
WHERE
    detalle_comprobante_cnt.compania = UN_COMPANIA 
          AND Plan_contable.CLASECUENTA in UN_PARAMETRO
    AND detalle_comprobante_cnt.tipo_cpte = UN_TIPO
    AND detalle_comprobante_cnt.fecha BETWEEN UN_FECHAINICIAL AND UN_FECHAFINAL
GROUP BY
    detalle_comprobante_cnt.cuenta,
    plan_contable.nombre) x;

    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_RESULTADO:='13058802';
  END;
  dbms_output.put_line(MI_RESULTADO);
 -- MI_RESULTADO:= SUBSTR(MI_RESULTADO,1, LENGTH(MI_RESULTADO)-1);
RETURN MI_RESULTADO;
exception when others then
 MI_RESULTADO:='41058503';
END FC_PREPPIVOTCONSULTADESCUENTOS;

--7713320_CONTABILIDAD (CARENAS)

FUNCTION FC_OBTENERVALORFACTURADO
/*
      NAME              : FC_OBTENERVALORFACTURADO
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR - TICKET7740801
      DATE MIGRADOR     : 06/02/2024
      TIME              : 11:00 AM
      SOURCE MODULE     : 
      DESCRIPTION       :
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 

    @NAME:   Obtener el valor total de la factura
    @METHOD: GET
*/
(
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANOCPTE        IN SF_FACTURA.ANO_CPTE%TYPE, 
  UN_TIPOCPTE       IN SF_FACTURA.TIPO_CPTE%TYPE,
  UN_NROCPTE        IN SF_FACTURA.NRO_CPTE%TYPE
  )
  RETURN SF_FACTURA.VALOR_TOTAL%TYPE AS

  MI_VALORTOTAL SF_FACTURA.VALOR_TOTAL%TYPE;
BEGIN
    BEGIN  
      SELECT SUM(DETALLE_COMPROBANTE_CNT.VALOR_DEBITO) 
        INTO MI_VALORTOTAL  
      FROM DETALLE_COMPROBANTE_CNT
         INNER JOIN PLAN_CONTABLE
         ON PLAN_CONTABLE.COMPANIA = DETALLE_COMPROBANTE_CNT.COMPANIA
         AND PLAN_CONTABLE.ANO = DETALLE_COMPROBANTE_CNT.ANO
         AND PLAN_CONTABLE.CODIGO = DETALLE_COMPROBANTE_CNT.CUENTA
      WHERE DETALLE_COMPROBANTE_CNT.COMPANIA = UN_COMPANIA 
         AND DETALLE_COMPROBANTE_CNT.ANO = UN_ANOCPTE
         AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE= UN_TIPOCPTE
         AND DETALLE_COMPROBANTE_CNT.COMPROBANTE = UN_NROCPTE
         AND PLAN_CONTABLE.CLASECUENTA = 'C';

      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_VALORTOTAL := 0;
    END;   
    
  RETURN MI_VALORTOTAL; 

END FC_OBTENERVALORFACTURADO;

  --INI_7741561_CONTABILIDAD (mrosero)      
PROCEDURE PR_GENERARINGRESONCR
  /*
    NAME              : PR_GENERARINGRESONCR
    AUTHORS           : MARIA CAMILA ROSERO
    AUTHOR MIGRACION  : 
    DATE MIGRADOR     : 27/02/2024
    TIME              : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Realiza el proceso de ingreso contable PARA LAS NOTAS CREDITO.

    MODIFICATIONS     : 
    @NAME:  generarIngresoNotasCliente
    @METHOD:  put
  */ 
(  
    UN_COMPANIA                     IN PCK_SUBTIPOS.TI_COMPANIA,              --COMPANIA
    UN_ANO                          IN PCK_SUBTIPOS.TI_ANIO,                  --AÃ‘O DEL COMPROBANTE AFECTANTE EJEMPLO NBA
    UN_TIPO_CPTE                    IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,       --TIPO DEL COMPROBANTE EJEMPLO NBA
    UN_COMPROBANTE                  IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,  --NUMERO DEL COMPROBANTE AFECTANTE EJEMPLO 2017000001 
    UN_TERCERO                      IN PCK_SUBTIPOS.TI_TERCERO,               --TERCERO DEL COMPROBANTE NBA EJEMPLO 800021261
    UN_SUCURSAL                     IN PCK_SUBTIPOS.TI_SUCURSAL,              --SUCURSAL DEL COMPROBANTE NBA EJEMPLO 001
    UN_LISTANUMEROAFECTAR           IN PCK_SUBTIPOS.TI_STRSQL,                --LISTA DE LOS COMPROBANTES A AFERCTAR EJEMPLO UNA FACTURA FDS
    UN_FECHA                        IN DATE,                                  --FECHA DEL COMPROBANTE AFECTANTE TIPO NBA EJEMPLO 01/01/2017
    UN_CLASE                        IN PCK_SUBTIPOS.TI_CLASECOMPROBANTE,      --CLASE DEL COMPROBANTE AFECTANTE NBA EJEMPLO B   
    UN_USUARIO                      IN VARCHAR2                               --USUARIO QUE CREA EL COMPROBANTE EJEMPLO ADMIN
    )
  AS  
  MI_RTA                            PCK_SUBTIPOS.TI_STRSQL;
  MI_VALOR                          PCK_SUBTIPOS.TI_DOBLE:=0;
  MI_CONSECUTIVODET                 PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_OTRADESCRIPCION                PCK_SUBTIPOS.TI_STRSQL;
  MI_DESCRIPCION                    PCK_SUBTIPOS.TI_STRSQL;
  MI_DESCRIPCION_AFECT              PCK_SUBTIPOS.TI_STRSQL := ' Cancelacion ';
  MI_PCKDATOS                       PCK_SUBTIPOS.TI_RTA_ACME;
  MI_CAMPOS                         PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION                      PCK_SUBTIPOS.TI_CONDICION;
  MI_VALORES                        PCK_SUBTIPOS.TI_VALORES;
  MI_CONSULTA                       PCK_SUBTIPOS.TI_STRSQL;
  RSDATOS                           SYS_REFCURSOR;
  RSCONTRACUENTAS                   SYS_REFCURSOR;
  MI_ANO                            PCK_SUBTIPOS.TI_ANIO;
  MI_TIPO                           PCK_SUBTIPOS.TI_TIPOCOMPROBANTE; 
  MI_NUMERO                         PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT; 
  MI_CENTRO_COSTO                   PCK_SUBTIPOS.TI_CENTRO_COSTO;
  MI_AUXILIAR                       PCK_SUBTIPOS.TI_AUXILIAR;
  MI_TEXTO                          PCK_SUBTIPOS.TI_STRSQL;

  MICONTRACUENTAS_CUENTA            PCK_SUBTIPOS.TI_CODIGOCONTA; 
  MICONTRACUENTAS_CLASECUENTA       PCK_SUBTIPOS.TI_CLASECUENTACONTA;
  MICONTRACUENTAS_TIPO_CPTE         PCK_SUBTIPOS.TI_TIPOCOMPROBANTE;
  MICONTRACUENTAS_NATURALEZA        PCK_SUBTIPOS.TI_NATURALEZACONTA;
  MICONTRACUENTAS_COMPROBANTE       PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT; 
  MICONTRACUENTAS_CONSECUTIVO       PCK_SUBTIPOS.TI_CONSECUTIVOCNT;
  MICONTRACUENTAS_NRO_DOCUMENTO     DETALLE_COMPROBANTE_CNT.NRO_DOCUMENTO%TYPE;
  MICONTRACUENTAS_TERCERO           PCK_SUBTIPOS.TI_TERCERO;
  MICONTRACUENTAS_SUCURSAL          PCK_SUBTIPOS.TI_SUCURSAL;
  MICONTRACUENTAS_CENTRO_COSTO      PCK_SUBTIPOS.TI_CENTRO_COSTO;
  MICONTRACUENTAS_AUXILIAR          PCK_SUBTIPOS.TI_AUXILIAR;
  MICONTRA_CUENTA_PPTAL             PCK_SUBTIPOS.TI_CODIGOPPTAL;
  MICONTRACUENTAS_ID                VARCHAR2(4000); 
  MICONTRACUENTAS_VALOR_DEBITO      PCK_SUBTIPOS.TI_DOBLE:=0;
  MICONTRACUENTAS_VALOR_CREDITO     PCK_SUBTIPOS.TI_DOBLE:=0;
  MICONTRA_DEBITO_AFECTADO          PCK_SUBTIPOS.TI_DOBLE:=0;
  MICONTRA_CREDITO_AFECTADO         PCK_SUBTIPOS.TI_DOBLE:=0;
  MI_TABLA                          PCK_SUBTIPOS.TI_TABLA;
  MI_AUX                            PCK_SUBTIPOS.TI_TEXTO1;
  MI_PARAMETRO                      PARAMETRO.VALOR%TYPE;
  MI_CODEQUICARTERA                 PLAN_CONTABLE.COD_EQUI_CARTERA%TYPE;
  MI_INDICADORDEBITO                PCK_SUBTIPOS.TI_DOBLE:=0;
  MICONTRA_VALOR_IVA                NUMBER;--(CFBARRERA:CC:1481)
  MICONTRA_VALOR_RETEIVA            NUMBER;
  MICONTRA_VALOR_RETEFUENTE         NUMBER;
  MICONTRA_VALOR_ICA                NUMBER;
  MICONTRACUENTAS_REFERENCIA        DETALLE_COMPROBANTE_CNT.REFERENCIA%TYPE;
  MICONTRACUENTAS_FUENTE            DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO%TYPE;
BEGIN
    BEGIN

    MI_PARAMETRO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                  , UN_NOMBRE    => 'MANEJA RECAUDO CON CODIGO EQUIVALENTE'
                  , UN_MODULO    => 1
                  , UN_FECHA_PAR => SYSDATE),'NO');

MI_CONSULTA := '
      SELECT NVL(SUM(DETALLE_COMPROBANTE_CNT.VALOR_DEBITO-(DETALLE_COMPROBANTE_CNT.DEBITO_AFECTADO - DETALLE_COMPROBANTE_CNT.CREDITO_AFECTADO)),0) MI_VALOR  
      FROM V_PLAN_CONTABLE INNER JOIN DETALLE_COMPROBANTE_CNT 
             ON  DETALLE_COMPROBANTE_CNT.COMPANIA = V_PLAN_CONTABLE.COMPANIA 
             AND DETALLE_COMPROBANTE_CNT.ANO      = V_PLAN_CONTABLE.ANO 
             AND DETALLE_COMPROBANTE_CNT.ID       = V_PLAN_CONTABLE.ID
      WHERE DETALLE_COMPROBANTE_CNT.COMPANIA='''||UN_COMPANIA||'''
             AND (DETALLE_COMPROBANTE_CNT.ANO, TIPO_CPTE, COMPROBANTE) IN ('||UN_LISTANUMEROAFECTAR||') 
             AND V_PLAN_CONTABLE.CLASECUENTA IN (''C'')';

             EXECUTE IMMEDIATE MI_CONSULTA INTO MI_VALOR;

      EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_VALOR:=0;
      END;        

   MI_CONSECUTIVODET := 1; 
   MI_VALOR := ROUND(MI_VALOR, 2);

<<datos_comprobante_afectar>>   

  MI_CONSULTA:='  SELECT ANO,
           TIPO,
           NUMERO,
           CENTRO_COSTO,
           AUXILIAR,
           DESCRIPCION,
           TEXTO
    FROM COMPROBANTE_CNT
    WHERE COMPROBANTE_CNT.COMPANIA='''||UN_COMPANIA||''' 
          AND (COMPROBANTE_CNT.ANO, TIPO, NUMERO) IN ('||UN_LISTANUMEROAFECTAR||')  
    ORDER BY NUMERO';

    OPEN RSDATOS FOR MI_CONSULTA;
    LOOP
    FETCH RSDATOS INTO MI_ANO, MI_TIPO, MI_NUMERO, MI_CENTRO_COSTO, MI_AUXILIAR, MI_DESCRIPCION, MI_TEXTO ;
    EXIT WHEN RSDATOS%NOTFOUND;
    BEGIN 

   MI_OTRADESCRIPCION := 'Cancelacion Factura No.  ' || MI_NUMERO;
   MI_DESCRIPCION_AFECT := MI_DESCRIPCION_AFECT || ' '|| MI_TIPO ||' '|| MI_NUMERO ||' '|| MI_DESCRIPCION || CHR(13)||CHR(10);

--
BEGIN
--mrosero CC_1093
    MI_CONSULTA := 'SELECT NVL(SUM(DEBITO_AFECTADO), 0)
                  FROM DETALLE_COMPROBANTE_CNT 
                   WHERE DETALLE_COMPROBANTE_CNT.COMPANIA='''||UN_COMPANIA||'''
                         AND (DETALLE_COMPROBANTE_CNT.ANO, TIPO_CPTE, COMPROBANTE) IN ('||UN_LISTANUMEROAFECTAR||')';
                    EXECUTE IMMEDIATE MI_CONSULTA INTO MI_INDICADORDEBITO;
                
                EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                MI_INDICADORDEBITO := 0;
                  END;
 <<procesar_contracuentas>>   
       IF MI_INDICADORDEBITO<>0 THEN
               MI_CONSULTA:='SELECT V_PLAN_CONTABLE.CLASECUENTA,
                           DCC.TIPO_CPTE,
                           DCC.COMPROBANTE,
                           DCC.CONSECUTIVO,
                           DCC.NATURALEZA,
                           DCC.NRO_DOCUMENTO,
                           DCC.TERCERO,
                           DCC.SUCURSAL,
                           DCC.CENTRO_COSTO,
                           DCC.AUXILIAR,
                           DCC.REFERENCIA,
                           DCC.FUENTE_RECURSO,
                           DCC.CUENTA,
                           DCC.ID,
                           NVL(DCC.VALOR_DEBITO-(DCC.DEBITO_AFECTADO - DCC.CREDITO_AFECTADO),0) VALOR_DEBITO,
                           DCC.VALOR_CREDITO,
                           DCC.DEBITO_AFECTADO,
                           DCC.CREDITO_AFECTADO,
                           DCC.VALOR_IVA,
                           DCC.VALOR_RETEIVA,
                           DCC.VALOR_RETEFUENTE ,
                           DCC.VALOR_ICA,
                           V_PLAN_CONTABLE.CUENTA_PPTAL
                    FROM V_PLAN_CONTABLE
                    INNER JOIN DETALLE_COMPROBANTE_CNT DCC
                            ON DCC.COMPANIA = V_PLAN_CONTABLE.COMPANIA
                           AND DCC.ANO = V_PLAN_CONTABLE.ANO
                           AND DCC.ID = V_PLAN_CONTABLE.ID
                    WHERE DCC.COMPANIA = '''||UN_COMPANIA||'''
                      AND DCC.ANO = '||MI_ANO||'
                      AND DCC.TIPO_CPTE = '''||MI_TIPO||'''
                      AND DCC.COMPROBANTE = '||MI_NUMERO||'
                      AND DCC.NATURALEZA = ''D''
                      AND NVL(DCC.VALOR_DEBITO-(DCC.DEBITO_AFECTADO - DCC.CREDITO_AFECTADO),0) > 0
                      
                     UNION
                     
                     SELECT 
                           NULL AS CLASECUENTA,
                           DCC.TIPO_CPTE,
                           DCC.COMPROBANTE,
                           DCC.CONSECUTIVO,
                           DCC.NATURALEZA,
                           DCC.NRO_DOCUMENTO,
                           DCC.TERCERO,
                           DCC.SUCURSAL,
                           DCC.CENTRO_COSTO,
                           DCC.AUXILIAR,
                           DCC.REFERENCIA,
                           DCC.FUENTE_RECURSO,
                           DCC.CUENTA,
                           DCC.ID,
                           DCC.VALOR_DEBITO,
                           CXC.VALOR_DEBITO VALOR_CREDITO,
                           DCC.DEBITO_AFECTADO,
                           DCC.CREDITO_AFECTADO,
                           DCC.VALOR_IVA,
                           DCC.VALOR_RETEIVA,
                           DCC.VALOR_RETEFUENTE,
                           DCC.VALOR_ICA,
                           NULL AS CUENTA_PPTAL
                    FROM DETALLE_COMPROBANTE_CNT DCC
                    INNER JOIN (
                        SELECT DCC.TIPO_CPTE,
                               DCC.COMPROBANTE,
                               DCC.CONSECUTIVO,
                               DCC.NATURALEZA,
                               DCC.NRO_DOCUMENTO,
                               DCC.TERCERO,
                               DCC.SUCURSAL,
                               DCC.CENTRO_COSTO,
                               DCC.AUXILIAR,
                               DCC.CUENTA,
                               DCC.ID,
                               NVL(DCC.VALOR_DEBITO-(DCC.DEBITO_AFECTADO - DCC.CREDITO_AFECTADO),0) VALOR_DEBITO,
                               DCC.VALOR_CREDITO,
                               DCC.DEBITO_AFECTADO,
                               DCC.CREDITO_AFECTADO,
                               DCC.COMPANIA,
                               DCC.ANO
                        FROM DETALLE_COMPROBANTE_CNT DCC
                        WHERE DCC.COMPANIA = '''||UN_COMPANIA||'''
                          AND DCC.ANO = '||MI_ANO||'
                          AND DCC.TIPO_CPTE = '''||MI_TIPO||'''
                          AND DCC.COMPROBANTE = '||MI_NUMERO||'
                          AND DCC.NATURALEZA = ''D''
                          AND NVL(DCC.VALOR_DEBITO-(DCC.DEBITO_AFECTADO - DCC.CREDITO_AFECTADO),0) > 0
                    ) CXC
                    ON CXC.COMPANIA = DCC.COMPANIA
                    AND CXC.TERCERO = DCC.TERCERO
                    AND CXC.SUCURSAL = DCC.SUCURSAL
                    WHERE DCC.COMPANIA = '''||UN_COMPANIA||'''
                      AND DCC.ANO = '||MI_ANO||'
                      AND DCC.TIPO_CPTE = '''||MI_TIPO||'''
                      AND DCC.COMPROBANTE = '||MI_NUMERO||'
                      AND DCC.NATURALEZA = ''C''';
              
    ELSE
          

         MI_CONSULTA := '
        SELECT 
            V_PLAN_CONTABLE.CLASECUENTA,
            DET.TIPO_CPTE,
            DET.COMPROBANTE,
            DET.CONSECUTIVO,
            DET.NATURALEZA,
            DET.NRO_DOCUMENTO,
            DET.TERCERO,
            DET.SUCURSAL,
            DET.CENTRO_COSTO,
            DET.AUXILIAR,
            DET.REFERENCIA,
            DET.FUENTE_RECURSO,
            DET.CUENTA,
            DET.ID,
            CASE 
                WHEN (DET.VALOR_DEBITO - NVL(SUM(AFECT.VALOR_DEBITO), 0)) < 0 THEN 0
                ELSE (DET.VALOR_DEBITO - NVL(SUM(AFECT.VALOR_DEBITO), 0))
            END AS VALOR_DEBITO,
            CASE 
                WHEN (DET.VALOR_CREDITO - NVL(SUM(AFECT.VALOR_CREDITO), 0)) < 0 THEN 0
                ELSE (DET.VALOR_CREDITO - NVL(SUM(AFECT.VALOR_CREDITO), 0))
            END AS VALOR_CREDITO,
            DET.DEBITO_AFECTADO,
            DET.CREDITO_AFECTADO,
            DET.VALOR_IVA,
            DET.VALOR_RETEIVA,
            DET.VALOR_RETEFUENTE,
            DET.VALOR_ICA,
            V_PLAN_CONTABLE.CUENTA_PPTAL
        FROM 
            V_PLAN_CONTABLE 
        INNER JOIN 
            DETALLE_COMPROBANTE_CNT DET
            ON DET.COMPANIA = V_PLAN_CONTABLE.COMPANIA
            AND DET.ANO = V_PLAN_CONTABLE.ANO
            AND DET.ID = V_PLAN_CONTABLE.ID
        LEFT JOIN 
            COMPROBANTE_CNTAFECTADOS
            ON DET.COMPANIA = COMPROBANTE_CNTAFECTADOS.COMPANIA
            AND DET.COMPROBANTE = COMPROBANTE_CNTAFECTADOS.COMPROBANTE_AFECT
        LEFT JOIN 
            DETALLE_COMPROBANTE_CNT AFECT
            ON AFECT.COMPANIA = COMPROBANTE_CNTAFECTADOS.COMPANIA
            AND AFECT.COMPROBANTE = COMPROBANTE_CNTAFECTADOS.COMPROBANTE
            AND AFECT.TIPO_CPTE = COMPROBANTE_CNTAFECTADOS.TIPO_CPTE
            AND AFECT.TERCERO = DET.TERCERO
        WHERE 
            DET.COMPANIA = ''' || UN_COMPANIA || '''
            AND DET.ANO = ' || MI_ANO || '
            AND DET.TIPO_CPTE = ''' || MI_TIPO || '''
            AND DET.COMPROBANTE = ' || MI_NUMERO || '
        GROUP BY 
            V_PLAN_CONTABLE.CLASECUENTA,
            DET.TIPO_CPTE,
            DET.COMPROBANTE,
            DET.CONSECUTIVO,
            DET.NATURALEZA,
            DET.NRO_DOCUMENTO,
            DET.TERCERO,
            DET.SUCURSAL,
            DET.CENTRO_COSTO,
            DET.AUXILIAR,
            DET.REFERENCIA,
            DET.FUENTE_RECURSO,
            DET.CUENTA,
            DET.ID,
            DET.VALOR_DEBITO,
            DET.VALOR_CREDITO,
            DET.DEBITO_AFECTADO,
            DET.CREDITO_AFECTADO,
            DET.VALOR_IVA,
            DET.VALOR_RETEIVA,
            DET.VALOR_RETEFUENTE,
            DET.VALOR_ICA,
            V_PLAN_CONTABLE.CUENTA_PPTAL';

END IF;

        OPEN RSCONTRACUENTAS FOR MI_CONSULTA;
    LOOP
        FETCH RSCONTRACUENTAS INTO MICONTRACUENTAS_CLASECUENTA , MICONTRACUENTAS_TIPO_CPTE, MICONTRACUENTAS_COMPROBANTE, MICONTRACUENTAS_CONSECUTIVO,
              MICONTRACUENTAS_NATURALEZA ,MICONTRACUENTAS_NRO_DOCUMENTO , MICONTRACUENTAS_TERCERO,MICONTRACUENTAS_SUCURSAL,MICONTRACUENTAS_CENTRO_COSTO,MICONTRACUENTAS_AUXILIAR,
              MICONTRACUENTAS_REFERENCIA, MICONTRACUENTAS_FUENTE, MICONTRACUENTAS_CUENTA,MICONTRACUENTAS_ID,MICONTRACUENTAS_VALOR_DEBITO,MICONTRACUENTAS_VALOR_CREDITO,
              MICONTRA_DEBITO_AFECTADO,MICONTRA_CREDITO_AFECTADO,MICONTRA_VALOR_IVA,MICONTRA_VALOR_RETEIVA,MICONTRA_VALOR_RETEFUENTE,MICONTRA_VALOR_ICA,MICONTRA_CUENTA_PPTAL;
        EXIT WHEN RSCONTRACUENTAS%NOTFOUND;
        BEGIN
        
               BEGIN              
                  SELECT NVL(COD_EQUI_CARTERA,0)
                  INTO MI_CODEQUICARTERA
                    FROM PLAN_CONTABLE
                    WHERE COMPANIA = UN_COMPANIA
                          AND CODIGO = MICONTRACUENTAS_CUENTA
                          AND ANO = EXTRACT (YEAR FROM SYSDATE);

                   EXCEPTION WHEN NO_DATA_FOUND THEN
                   MI_CODEQUICARTERA := 0;             
              END;

              IF MI_PARAMETRO = 'SI' AND  MI_CODEQUICARTERA != 0 AND  SUBSTR(MICONTRACUENTAS_CUENTA,0,2)= 14  THEN              
                MICONTRACUENTAS_CUENTA := MI_CODEQUICARTERA;              
              END IF;

              BEGIN
                  MI_CAMPOS := 'COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CONSECUTIVO,CUENTA,
                                FECHA,NATURALEZA,NRO_DOCUMENTO,DESCRIPCION,VALOR_DEBITO,VALOR_CREDITO,
                                EJECUCION_DEBITO,EJECUCION_CREDITO,BASE_GRAVABLE,CENTRO_COSTO,TERCERO,SUCURSAL,FUENTE_RECURSO,
                                AUXILIAR,ANO_AFECT,TIPO_CPTE_AFECT,CMPTE_AFECTADO,CONSECUTIVOAFECTADO,CUENTAPPTAL,VALOR_IVA,VALOR_RETEIVA,VALOR_RETEFUENTE,VALOR_ICA,CREATED_BY,DATE_CREATED';

                  MI_VALORES := '''' || UN_COMPANIA || '''
                                ,' || UN_ANO || '
                                ,''' || UN_TIPO_CPTE || '''
                                ,' || UN_COMPROBANTE || '
                                ,' || MI_CONSECUTIVODET || '
                                ,''' || MICONTRACUENTAS_CUENTA || '''
                                ,TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                                ,''' ||  MICONTRACUENTAS_NATURALEZA || '''
                                ,'''||MICONTRACUENTAS_NRO_DOCUMENTO||'''
                                ,''' || MI_OTRADESCRIPCION || '''
                                ,''' || CASE WHEN MICONTRACUENTAS_NATURALEZA = 'D' THEN 0 ELSE MICONTRACUENTAS_VALOR_CREDITO END || '''
                                ,''' || CASE WHEN MICONTRACUENTAS_NATURALEZA = 'C' THEN 0 ELSE MICONTRACUENTAS_VALOR_DEBITO END || '''  
                                ,''' || CASE WHEN MICONTRACUENTAS_NATURALEZA = 'D' THEN '0' ELSE TO_CHAR(MICONTRACUENTAS_VALOR_CREDITO - (MICONTRA_CREDITO_AFECTADO - MICONTRA_DEBITO_AFECTADO)) END || '''
                                ,''' || CASE WHEN MICONTRACUENTAS_NATURALEZA = 'C' THEN '0' ELSE TO_CHAR(MICONTRACUENTAS_VALOR_DEBITO - (MICONTRA_DEBITO_AFECTADO - MICONTRA_CREDITO_AFECTADO)) END || '''
                                ,0
                                ,''' || MICONTRACUENTAS_CENTRO_COSTO || '''
                                ,''' || MICONTRACUENTAS_TERCERO || '''
                                ,''' || UN_SUCURSAL || '''
                                ,''' || MICONTRACUENTAS_FUENTE || '''
                                ,''' || MICONTRACUENTAS_AUXILIAR || '''
                                ,''' || CASE  WHEN MICONTRACUENTAS_NATURALEZA = 'D' THEN MI_ANO  END ||'''
                                ,''' || CASE   WHEN MICONTRACUENTAS_NATURALEZA = 'D' THEN MI_TIPO   END || '''
                                ,''' || CASE   WHEN MICONTRACUENTAS_NATURALEZA = 'D' THEN MI_NUMERO  END ||'''
                                ,''' || CASE   WHEN MICONTRACUENTAS_NATURALEZA = 'D' THEN MICONTRACUENTAS_CONSECUTIVO END || '''
                                ,''' || MICONTRA_CUENTA_PPTAL || '''
                                ,''' || MICONTRA_VALOR_IVA || '''
                                ,''' || MICONTRA_VALOR_RETEIVA || '''
                                ,''' || MICONTRA_VALOR_RETEFUENTE || '''
                                ,''' || MICONTRA_VALOR_ICA || '''
                                ,''' || UN_USUARIO || '''
                                , SYSDATE';    

                          MI_PCKDATOS := PCK_DATOS.FC_ACME(
                              UN_TABLA => 'DETALLE_COMPROBANTE_CNT', 
                              UN_ACCION => 'I', 
                              UN_CAMPOS => MI_CAMPOS, 
                              UN_VALORES => MI_VALORES
                            );
                          EXCEPTION
                            WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                              PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD => SQLCODE,
                                UN_TABLAERROR => 'DETALLE_COMPROBANTE_CNT',
                                UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_CREARDETPPTALVARIOS
                              ); 
                          END;

         BEGIN
                 MI_TABLA := 'DETALLE_COMPROBANTE_CNT';
                    IF MICONTRACUENTAS_NATURALEZA = 'C' THEN 
                     MI_CAMPOS := 'CREDITO_AFECTADO = CREDITO_AFECTADO + ' || MICONTRACUENTAS_VALOR_CREDITO || ' ';
                    ELSE 
                     MI_CAMPOS := 'DEBITO_AFECTADO = DEBITO_AFECTADO  + ' || MICONTRACUENTAS_VALOR_DEBITO || ' ';
                    END IF;
                    
                    MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || 
                                    ''' AND ANO = ' || MI_ANO || 
                                    ' AND TIPO_CPTE = ''' || MI_TIPO || ''' 
                                      AND COMPROBANTE = ''' || MI_NUMERO || 
                                    ''' AND CUENTA = ' || MICONTRACUENTAS_CUENTA;
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                          UN_ACCION    => 'M',
                                                          UN_CAMPOS    => MI_CAMPOS,
                                                          UN_CONDICION => MI_CONDICION);   
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;
                        MI_CONSECUTIVODET := MI_CONSECUTIVODET + 1;  
       END;
    END LOOP procesar_contracuentas;
 END;

END LOOP datos_comprobante_afectar; 

  BEGIN
         MI_TABLA := 'COMPROBANTE_CNT';
         MI_CAMPOS := 'VLRAGIRAR = '||MI_VALOR || ',DESCRIPCION = '''|| MI_DESCRIPCION_AFECT ||''',TEXTO='''|| MI_TEXTO ||'''';

         MI_CONDICION := '      COMPANIA    = '''||UN_COMPANIA||
                         ''' AND ANO         = '||UN_ANO||
                          '  AND TIPO        = '''||UN_TIPO_CPTE||
                         ''' AND NUMERO      = '||UN_COMPROBANTE;
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOS,
                                                  UN_CONDICION => MI_CONDICION);    
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;     

END PR_GENERARINGRESONCR;

--Ticket#7744953: (GROJAS) Se crea procedimiento para realizar la afectación cuando es una adición a una orden de pago.
  PROCEDURE PR_AFECTARADICIONES
    /*
      NAME              : PR_AFECTARADICIONES 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : GERMAN DAVID ROJAS GONZALEZ
      DATE MIGRADOR     : 11/04/2024
      TIME              : 
      SOURCE MODULE     : CONTABILIDAD
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : FUNCION QUE ACTUALIZA CREDITOS  AFECTADOS EN COMPROBANTE_CNT Y EN DETALLE_COMPROBANTE_CNT CUANDO ES UNA ADICIÓN.
      PARAMETERS        : UN_COMPANIA             => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_ANO                  => AÑO DEL COMPROBANTE.
                          UN_TIPO_CPTE            => TIPO DE COMPROBANTE AFECTADO DEL REGISTRO 
                          UN_COMPROBANTE          => COMPROBANTE AFECTADO DEL REGISTRO
                          UN_ANO_AFEC             => AÑO DEL COMPROBANTE AFECTADO.
                          UN_TIPO_CPTE_AFECT      => TIPO COMPROBANTE AFECTADO DEL REGISTRO.
                          UN_COMPROBANTE_AFECT    => COMPROBANTE AFECTADO DEL REGISTRO.
                          UN_CONSECUTIVOAFECTADO  => CONSECUTIVO AFECTADO DEL REGISTRO.
                          UN_CVALORDEBITO         => VALOR DEBITO DEL REGISTRO 
                          UN_CVALORDEBITOACT      => VALOR DEBITO DEL REGISTRO ACTIVO
                          UN_CVALORCREDITO        => VALOR CREDITO DEL REGISTRO 
                          UN_CVALORCREDITOACT     => VALOR CREDITO DEL REGISTRO ACTIVO

      MODIFICATIONS     : 
      @NAME:  actualizarAfectadosAdicion
      @METHOD:  PUT
    */ 
    (
      UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_ANO                 IN PCK_SUBTIPOS.TI_ANIO,
      UN_TIPO_CPTE           IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
      UN_COMPROBANTE         IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
      UN_ANO_AFEC            IN PCK_SUBTIPOS.TI_ANIO,
      UN_TIPO_CPTE_AFECT     IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
      UN_COMPROBANTE_AFECT   IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
      UN_CONSECUTIVOAFECTADO IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
      UN_CVALORDEBITO        IN  PCK_SUBTIPOS.TI_DOBLE, 
      UN_CVALORDEBITOACT     IN  PCK_SUBTIPOS.TI_DOBLE, 
      UN_CVALORCREDITO       IN  PCK_SUBTIPOS.TI_DOBLE, 
      UN_CVALORCREDITOACT    IN  PCK_SUBTIPOS.TI_DOBLE,
      UN_USUARIO             IN  PCK_SUBTIPOS.TI_USUARIO
      
    )
  AS
    
  MI_CLASE            PCK_SUBTIPOS.TI_CLASECOMPROPPTO;
  MI_AUX              PCK_SUBTIPOS.TI_TEXTO1;
  MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
  MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
    
  BEGIN
    BEGIN
      SELECT CLASE_CONTABLE 
      INTO   MI_CLASE
      FROM   TIPO_COMPROBANTE
      WHERE  COMPANIA = UN_COMPANIA
        AND  CODIGO   = UN_TIPO_CPTE;
      EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_CLASE := '';
    END;

    IF MI_CLASE = 'U' AND UN_TIPO_CPTE_AFECT IS NOT NULL AND UN_COMPROBANTE_AFECT IS NOT NULL THEN 
    
      IF (UN_CVALORDEBITO <> UN_CVALORDEBITOACT) OR (UN_CVALORCREDITO <> UN_CVALORCREDITOACT) THEN

          BEGIN
            BEGIN
                  MI_TABLA := 'COMPROBANTE_CNT';
                  MI_CAMPOS := 'DEBITOSAFECTADOS = DEBITOSAFECTADOS + ' || UN_CVALORDEBITOACT || ' - ' || UN_CVALORDEBITO ||
                               ', CREDITOSAFECTADOS = CREDITOSAFECTADOS + ' || UN_CVALORCREDITOACT || ' - ' || UN_CVALORCREDITO;
                  MI_CONDICION := '      COMPANIA = '''||UN_COMPANIA||
                                  '''AND ANO      = '||UN_ANO_AFEC||
                                  '  AND TIPO     = '''||UN_TIPO_CPTE_AFECT||
                                  '''AND NUMERO   = '||UN_COMPROBANTE_AFECT ;
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                        UN_ACCION    => 'M',
                                                        UN_CAMPOS    => MI_CAMPOS,
                                                        UN_CONDICION => MI_CONDICION);    
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                MI_MSGERROR(1).CLAVE := 'CONDICION';
                MI_MSGERROR(1).VALOR :='COMPANIA = '''||UN_COMPANIA||
                                  ''', ANO = '||UN_ANO_AFEC||
                                  ', TIPO = '''||UN_TIPO_CPTE_AFECT||
                                  ',CONSECUTIVO ='|| UN_CONSECUTIVOAFECTADO ||
                                  ''' y NUMERO = '||UN_COMPROBANTE_AFECT;
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_ACT_CPTECNT,
                    UN_REEMPLAZOS => MI_MSGERROR
                );
          END;
     
          BEGIN
              BEGIN
                  MI_TABLA := 'DETALLE_COMPROBANTE_CNT';
                  MI_CAMPOS := 'DEBITO_AFECTADO = DEBITO_AFECTADO + ' || UN_CVALORDEBITOACT || ' - ' || UN_CVALORDEBITO ||
                                ', CREDITO_AFECTADO = CREDITO_AFECTADO + ' || UN_CVALORCREDITOACT || ' - ' || UN_CVALORCREDITO;
                  MI_CONDICION := 'COMPANIA    = '''||UN_COMPANIA||
                                  ''' AND ANO         = '||UN_ANO_AFEC||
                                  '  AND TIPO_CPTE   = '''||UN_TIPO_CPTE_AFECT||
                                  ''' AND COMPROBANTE = '||UN_COMPROBANTE_AFECT||
                                  ' AND CONSECUTIVO = '||UN_CONSECUTIVOAFECTADO||'';
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                        UN_ACCION    => 'M',
                                                        UN_CAMPOS    => MI_CAMPOS,
                                                        UN_CONDICION => MI_CONDICION);    
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
              END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                MI_MSGERROR(1).CLAVE := 'CONDICION';
                MI_MSGERROR(1).VALOR :='COMPANIA = '||UN_COMPANIA||
                                  ', ANO = '||UN_ANO_AFEC||
                                  ', TIPO_CPTE = '||UN_TIPO_CPTE_AFECT||
                                  ', COMPROBANTE = '||UN_COMPROBANTE_AFECT||
                                  ', CONSECUTIVO = '||UN_CONSECUTIVOAFECTADO;
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_DEBCRED,
                    UN_REEMPLAZOS => MI_MSGERROR
                  );
          END;
              
            --INSERTAR LAS AFECTACIONES EN LA TABLA COMPROBANTE_CNTAFECTADOS
            --PRIMERO VERIFICA SI YA EXISTE EL DATO REGISTRADO EN LA TABLA 
            BEGIN
              SELECT  DISTINCT 'X'
              INTO    MI_AUX
              FROM    COMPROBANTE_CNTAFECTADOS
              WHERE   COMPANIA          = UN_COMPANIA
                AND   ANO               = UN_ANO
                AND   TIPO_CPTE         = UN_TIPO_CPTE
                AND   COMPROBANTE       = UN_COMPROBANTE
                AND   ANO_AFECT         = UN_ANO_AFEC
                AND   TIPO_CPTE_AFECT   = UN_TIPO_CPTE_AFECT
                AND   COMPROBANTE_AFECT = UN_COMPROBANTE_AFECT; 
              EXCEPTION WHEN NO_DATA_FOUND THEN
                BEGIN
                  BEGIN
                    MI_CAMPOS := 'COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,ANO_AFECT,TIPO_CPTE_AFECT,COMPROBANTE_AFECT,CREATED_BY,DATE_CREATED';
                    MI_VALORES := '''' || UN_COMPANIA || ''',' || UN_ANO || ' , 
                                  '''  || UN_TIPO_CPTE    || ''',' || UN_COMPROBANTE || ',' || UN_ANO_AFEC || ',
                                  '''  || UN_TIPO_CPTE_AFECT    || ''',' || UN_COMPROBANTE_AFECT || ',
                                  '''  || UN_USUARIO  || ''', SYSDATE';
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => 'COMPROBANTE_CNTAFECTADOS', 
                                                          UN_ACCION  => 'I', 
                                                          UN_CAMPOS  => MI_CAMPOS, 
                                                          UN_VALORES => MI_VALORES);                 
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                  END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_INSER_CPTAFECTADOS,
                      UN_TABLAERROR => 'COMPROBANTE_CNTAFECTADOS'
                    );
                END;  
            END;

      END IF;  

    END IF;

  END PR_AFECTARADICIONES;

PROCEDURE PR_CARGAR_MOV_CONTABLES
/*
    NAME              : PR_CARGAR_MOV_CONTABLES
    AUTHOR MIGRACION  : GERMAN DAVID ROJAS GONZALEZ
    DATE MIGRADOR     : 05/06/2024                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : PROCEDIMIENTO QUE CARGA LOS DATOS DE COMPROBANTE Y DETALLE_COMPROBANTE CNT DE UN EXCEL

    @NAME:    'SubirmovcontablesControlador'
    @METHOD:  POST
    */
( 
  UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CADENAH     IN CLOB,
  UN_CADENAD     IN CLOB,
  UN_PROCESO     IN PCK_SUBTIPOS.TI_ENTERO,
  UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO
)  
AS 
MI_DATOS_FILA           PCK_SYSMAN_UTL.T_SPLIT;
MI_DATOS_COLUMNAS       PCK_SYSMAN_UTL.T_SPLIT;
MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_TABLA                PCK_SUBTIPOS.TI_TABLA;
MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
MI_ANIO                 PCK_SUBTIPOS.TI_ANIO;
MI_NUMERO               NUMBER := 0;
MI_CUENTA               DETALLE_COMPROBANTE_CNT.CUENTA%TYPE;
MI_NATURALEZA           PLAN_CONTABLE.NATURALEZA%TYPE;
MI_TERCERO              DETALLE_COMPROBANTE_CNT.TERCERO%TYPE;
MI_SUCURSAL             DETALLE_COMPROBANTE_CNT.SUCURSAL%TYPE;
MI_CENTROCOSTO          DETALLE_COMPROBANTE_CNT.CENTRO_COSTO%TYPE;
MI_FUENTE_RECURSO       DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO%TYPE;
MI_AUXILIAR             DETALLE_COMPROBANTE_CNT.AUXILIAR%TYPE;
MI_REFERENCIA           DETALLE_COMPROBANTE_CNT.REFERENCIA%TYPE;
MI_BANCO_EGR            COMPROBANTE_CNT.BANCO%TYPE; 
MI_CUENTA_EGR           COMPROBANTE_CNT.CUENTA%TYPE;
MI_CLASE_CUENTA         PLAN_CONTABLE.CLASECUENTA%TYPE;
MI_TIPO_RETENCION       RETENCIONES.TIPO%TYPE; 
MI_CODIGO_RETENCION     RETENCIONES.CODIGO%TYPE;
MI_CUENTA_RETENCION     RETENCIONES.CUENTA_CREDITO%TYPE;
MI_LEY1819              RETENCIONES.ALEY1819%TYPE;
MI_COD_CONSECUTIVO      DETALLE_COMPROBANTE_CNT.CONSECUTIVO%TYPE;
MI_RETORNO              CLOB := '';
MI_VALOR_PARAMETRO      PCK_SUBTIPOS.TI_CAMPOS;

BEGIN

    MI_ANIO := EXTRACT(YEAR FROM SYSDATE);
        MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENAH,
                                                  UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);

    --INSERTA COMPROBANTE_CNT
      <<CREAR_HEADER>>
      FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST 
        LOOP
            MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                              UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);

            MI_ANIO :=  EXTRACT(YEAR FROM  TO_DATE(MI_DATOS_COLUMNAS(4),'DD/MM/YYYY')  );

            --Valida que el tercero este creado.
            BEGIN
                SELECT NIT, SUCURSAL
                    INTO MI_TERCERO, MI_SUCURSAL
                    FROM TERCERO
                    WHERE COMPANIA = MI_DATOS_COLUMNAS(1)
                    AND NIT        = MI_DATOS_COLUMNAS(6)
                    AND SUCURSAL   = MI_DATOS_COLUMNAS(7);
            EXCEPTION WHEN NO_DATA_FOUND THEN
                BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                    MI_MSGERROR (1).CLAVE := 'TERCERO';
                    MI_MSGERROR (1).VALOR := MI_DATOS_COLUMNAS(6);
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERR_RETETERCENULO,
                                            UN_TABLAERROR => 'TERCERO',
                                            UN_REEMPLAZOS => MI_MSGERROR);
                END;
            END;            

            MI_CAMPOS := 'COMPANIA
                          ,ANO
                          ,TIPO
                          ,NUMERO
                          ,FECHA
                          ,FECHA_VCN_DOC
                          ,TERCERO
                          ,SUCURSAL
                          ,DESCRIPCION
                          ,TEXTO
                          ,TIPOCONTRATO
                          ,NUMEROCONTRATO
                          ,CONSFACTURA
                          ,CREATED_BY
                          ,DATE_CREATED';

            MI_VALORES := ''''|| MI_DATOS_COLUMNAS(1) ||'''
                          ,'  || MI_ANIO ||'
                          ,'''|| MI_DATOS_COLUMNAS(2) ||'''
                          ,'  || MI_DATOS_COLUMNAS(3) ||'
                          ,'''|| MI_DATOS_COLUMNAS(4) ||'''
                          ,'''|| CASE WHEN MI_DATOS_COLUMNAS(5) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(5) END ||'''
                          ,'''|| MI_DATOS_COLUMNAS(6) ||'''
                          ,'''|| MI_DATOS_COLUMNAS(7) ||'''
                          ,'''|| CASE WHEN MI_DATOS_COLUMNAS(8) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(8) END ||'''
                          ,'''|| CASE WHEN MI_DATOS_COLUMNAS(9) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(9) END ||'''
                          ,'''|| CASE WHEN MI_DATOS_COLUMNAS(10) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(10) END ||'''
                          ,'''|| CASE WHEN MI_DATOS_COLUMNAS(11) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(11) END ||''' 
                          ,'''|| CASE WHEN MI_DATOS_COLUMNAS(12) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(12) END ||'''
                          ,'''|| UN_USUARIO ||'''
                          ,SYSDATE';

            IF UN_PROCESO = 2 THEN 

                MI_CAMPOS := MI_CAMPOS||'
                      ,PREFIJODIAN
                      ,CONSECUTIVODIAN
                      ,RESOLUCIONDIAN';
                      
                MI_VALORES:=MI_VALORES||'
                      ,'''|| CASE WHEN MI_DATOS_COLUMNAS(13) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(13) END ||'''
                      ,'''|| CASE WHEN MI_DATOS_COLUMNAS(14) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(14) END ||'''
                      ,'''|| CASE WHEN MI_DATOS_COLUMNAS(15) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(15) END ||'''';

            ELSIF UN_PROCESO = 3 THEN

                BEGIN
                    SELECT BANCO, CUENTA
                        INTO MI_BANCO_EGR,MI_CUENTA_EGR
                        FROM TERCEROPAGOS
                        WHERE COMPANIA = MI_DATOS_COLUMNAS(1)
                        AND NIT = MI_TERCERO
                        AND SUCURSAL = MI_SUCURSAL
                        AND ACTIVA = -1;
                    EXCEPTION WHEN NO_DATA_FOUND THEN
                        IF MI_DATOS_COLUMNAS(14) = '-1' THEN
                            BEGIN
                                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                                MI_RETORNO := 'Para el tercero ' || MI_TERCERO || ' no existe configuraciÃ³n de banco y cuenta en tercero pagos';
                                MI_MSGERROR (1).CLAVE := 'MENSAJE';
                                MI_MSGERROR (1).VALOR := MI_RETORNO;
                                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                        UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                                                        UN_TABLAERROR => 'TERCEROPAGOS',
                                                        UN_REEMPLAZOS => MI_MSGERROR);
                            END;
                        ELSE
                            MI_BANCO_EGR    := NULL;
                            MI_CUENTA_EGR   := NULL;
                        END IF;    
                END;


                MI_CAMPOS := MI_CAMPOS||'
                      ,CUENTA
                      ,PAGOENPLANO
                      ,BANCO
                      ,CUENTABANCO';
                      
                MI_VALORES:=MI_VALORES||'
                      ,'''|| CASE WHEN MI_DATOS_COLUMNAS(13) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(13) END ||'''
                      ,'''|| CASE WHEN MI_DATOS_COLUMNAS(14) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(14) END ||'''
                      ,'''|| MI_BANCO_EGR ||'''
                      ,'''|| MI_CUENTA_EGR ||'''
                      ';
                    
            END IF;


          BEGIN
              IF  NVL(MI_NUMERO,0) != MI_DATOS_COLUMNAS(3) THEN
                BEGIN

                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'COMPROBANTE_CNT'
                                                        ,UN_ACCION  => 'I'
                                                        ,UN_CAMPOS  => MI_CAMPOS
                                                        ,UN_VALORES => MI_VALORES);

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                END;
              END IF;
              MI_NUMERO := MI_DATOS_COLUMNAS(3);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
              MI_MSGERROR(1).CLAVE := 'NUMERO';
              MI_MSGERROR(1).VALOR := MI_DATOS_COLUMNAS(3);
              MI_MSGERROR(2).CLAVE := 'TIPO';
              MI_MSGERROR(2).VALOR := MI_DATOS_COLUMNAS(2);
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE, UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_INSCOMPCNT, 
                                         UN_TABLAERROR => 'COMPROBANTE_CNT', UN_REEMPLAZOS => MI_MSGERROR );
          END;
      END LOOP CREAR_HEADER;

    --INSERTA DETALLE_PRESUPUESTAL_CNT
    
    MI_COD_CONSECUTIVO := 1;

    MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENAD,
                                               UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);
        
        <<CREAR_DETALLES>>
        FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST 
        LOOP
            MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                            UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);

            MI_ANIO :=  EXTRACT(YEAR FROM  TO_DATE(MI_DATOS_COLUMNAS(4),'DD/MM/YYYY')  );

            --Valida que la cuenta contable este creada y tenga indicador de movimiento o auxiliares.
            BEGIN
                SELECT CODIGO, NATURALEZA, CLASECUENTA 
                INTO MI_CUENTA, MI_NATURALEZA, MI_CLASE_CUENTA
                FROM PLAN_CONTABLE
                WHERE COMPANIA  = MI_DATOS_COLUMNAS(1)
                AND ANO         = MI_ANIO
                AND CODIGO      = MI_DATOS_COLUMNAS(5)
                AND (MOVIMIENTO + MAN_CEN_CTO + MAN_AUX_TER + MAN_AUX_GEN + MAN_AUX_REF + MAN_AUX_FUE) <> 0;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                    MI_MSGERROR (1).CLAVE := 'CUENTA';
                    MI_MSGERROR (1).VALOR := MI_DATOS_COLUMNAS(5);
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTABILIDAD_9,
                                            UN_TABLAERROR => 'PLAN_CONTABLE',
                                            UN_REEMPLAZOS => MI_MSGERROR);

                END;
            END;

            --Valida que el tercero este creado.
            BEGIN
                SELECT NIT, SUCURSAL
                    INTO MI_TERCERO, MI_SUCURSAL
                    FROM TERCERO
                    WHERE COMPANIA = MI_DATOS_COLUMNAS(1)
                    AND NIT        = MI_DATOS_COLUMNAS(8)
                    AND SUCURSAL   = MI_DATOS_COLUMNAS(9);
            EXCEPTION WHEN NO_DATA_FOUND THEN
                BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                    MI_MSGERROR (1).CLAVE := 'TERCERO';
                    MI_MSGERROR (1).VALOR := MI_DATOS_COLUMNAS(8);
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERR_RETETERCENULO,
                                            UN_TABLAERROR => 'TERCERO',
                                            UN_REEMPLAZOS => MI_MSGERROR);
                END;
            END;

            --Valida que el Centro de costos este creado.
            BEGIN
                SELECT CODIGO
                INTO MI_CENTROCOSTO
                FROM CENTRO_COSTO
                WHERE COMPANIA  = MI_DATOS_COLUMNAS(1)
                AND ANO         = MI_ANIO
                AND CODIGO      = MI_DATOS_COLUMNAS(13)
                AND MOVIMIENTO  = -1;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_RETORNO := 'El centro de costo ' || MI_DATOS_COLUMNAS(13) || ' no existe o no tiene movimiento';
                    MI_MSGERROR (1).CLAVE := 'MENSAJE';
                    MI_MSGERROR (1).VALOR := MI_RETORNO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                                            UN_TABLAERROR => 'CENTRO_COSTO',
                                            UN_REEMPLAZOS => MI_MSGERROR);
                END;
            END; 

            --Valida que la fuente este creada.
            BEGIN
                SELECT CODIGO
                INTO MI_FUENTE_RECURSO
                FROM FUENTE_RECURSOS
                WHERE COMPANIA  = MI_DATOS_COLUMNAS(1)
                AND ANO         = MI_ANIO
                AND CODIGO      = MI_DATOS_COLUMNAS(14);
            EXCEPTION WHEN NO_DATA_FOUND THEN
                BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_RETORNO := 'la fuente de recursos ' || MI_DATOS_COLUMNAS(14) || ' no existe';
                    MI_MSGERROR (1).CLAVE := 'MENSAJE';
                    MI_MSGERROR (1).VALOR := MI_RETORNO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                                            UN_TABLAERROR => 'FUENTE_RECURSOS',
                                            UN_REEMPLAZOS => MI_MSGERROR);
                END;
            END;
                                        
            --Valida que el auxiliar este creado.
            BEGIN
                SELECT CODIGO
                INTO MI_AUXILIAR
                FROM AUXILIAR
                WHERE COMPANIA  = MI_DATOS_COLUMNAS(1)
                AND ANO         = MI_ANIO
                AND CODIGO      = MI_DATOS_COLUMNAS(15)
                AND MOVIMIENTO  = -1;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_RETORNO := 'El auxiliar ' || MI_DATOS_COLUMNAS(15) || ' no existe o no tiene movimiento';
                    MI_MSGERROR (1).CLAVE := 'MENSAJE';
                    MI_MSGERROR (1).VALOR := MI_RETORNO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                                            UN_TABLAERROR => 'AUXILIAR',
                                            UN_REEMPLAZOS => MI_MSGERROR);
                END;
            END;

            --Valida que la referencia este creado.
            BEGIN
                SELECT CODIGO
                INTO MI_REFERENCIA
                FROM REFERENCIA
                WHERE COMPANIA  = MI_DATOS_COLUMNAS(1)
                AND ANO         = MI_ANIO
                AND CODIGO      = MI_DATOS_COLUMNAS(16)
                AND MOVIMIENTO  = -1;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_RETORNO := 'la referncia ' || MI_DATOS_COLUMNAS(16) || ' no existe o no tiene movimiento';
                    MI_MSGERROR (1).CLAVE := 'MENSAJE';
                    MI_MSGERROR (1).VALOR := MI_RETORNO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                                            UN_TABLAERROR => 'REFERENCIA',
                                            UN_REEMPLAZOS => MI_MSGERROR);
                END;
            END;                                            

             MI_CAMPOS := 'COMPANIA
                        ,ANO
                        ,TIPO_CPTE
                        ,COMPROBANTE
                        ,CONSECUTIVO
                        ,FECHA
                        ,CUENTA
                        ,VALOR_DEBITO
                        ,VALOR_CREDITO
                        ,TERCERO
                        ,SUCURSAL
                        ,DESCRIPCION
                        ,TIPOCONTRATO
                        ,NUMEROCONTRATO
                        ,CENTRO_COSTO
                        ,FUENTE_RECURSO
                        ,AUXILIAR
                        ,REFERENCIA
                        ,BASE_GRAVABLE 
                        ,CREATED_BY
                        ,DATE_CREATED
                        ';

            MI_VALORES := ''''|| MI_DATOS_COLUMNAS(1) ||'''
                    ,  '||  MI_ANIO ||'
                    ,'''|| MI_DATOS_COLUMNAS(2) ||'''
                    ,  '|| MI_DATOS_COLUMNAS(3) ||'
                    ,  '|| MI_COD_CONSECUTIVO ||'
                    ,'''|| MI_DATOS_COLUMNAS(4) ||'''
                    ,'''|| MI_CUENTA ||'''
                    ,'''|| MI_DATOS_COLUMNAS(6) ||'''
                    ,'''|| MI_DATOS_COLUMNAS(7) ||'''
                    ,'''|| MI_TERCERO ||'''
                    ,'''|| MI_SUCURSAL ||'''
                    ,'''|| MI_DATOS_COLUMNAS(12) ||'''
                    ,'''|| CASE WHEN MI_DATOS_COLUMNAS(10) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(10) END ||'''
                    ,'''|| CASE WHEN MI_DATOS_COLUMNAS(11) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(11) END ||'''
                    ,'''|| MI_CENTROCOSTO ||'''
                    ,'''|| MI_FUENTE_RECURSO ||'''
                    ,'''|| MI_AUXILIAR ||'''
                    ,'''|| MI_REFERENCIA ||'''
                    ,'''|| CASE WHEN MI_DATOS_COLUMNAS(19) = 'NoDato' THEN 0 ELSE MI_DATOS_COLUMNAS(19) END ||'''
                    ,'''|| UN_USUARIO ||'''
                    ,SYSDATE
                    ';

            IF UN_PROCESO = 2 THEN

                MI_CAMPOS := MI_CAMPOS||'
                      ,CONCEPTO_CUDS';
                      
                MI_VALORES:=MI_VALORES||'
                      ,'''|| CASE WHEN MI_DATOS_COLUMNAS(20) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(20) END ||'''';
                    
            ELSIF UN_PROCESO = 3 THEN

                MI_CAMPOS := MI_CAMPOS||'
                      ,ANO_AFECT
                      ,TIPO_CPTE_AFECT
                      ,CMPTE_AFECTADO
                      ,CONSECUTIVOAFECTADO';
                      
                MI_VALORES:=MI_VALORES||'
                      ,'''|| CASE WHEN MI_DATOS_COLUMNAS(20) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(20) END ||'''
                      ,'''|| CASE WHEN MI_DATOS_COLUMNAS(21) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(21) END ||'''
                      ,'''|| CASE WHEN MI_DATOS_COLUMNAS(22) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(22) END ||'''
                      ,'''|| CASE WHEN MI_DATOS_COLUMNAS(23) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(23) END ||'''';

            END IF;
            
        BEGIN
            BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'DETALLE_COMPROBANTE_CNT'
                                                    ,UN_ACCION  => 'I'
                                                    ,UN_CAMPOS  => MI_CAMPOS
                                                    ,UN_VALORES => MI_VALORES);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                MI_MSGERROR(1).CLAVE := 'COMPROBANTE';
                MI_MSGERROR(1).VALOR := MI_DATOS_COLUMNAS(3);
                MI_MSGERROR(2).CLAVE := 'TIPO';
                MI_MSGERROR(2).VALOR := MI_DATOS_COLUMNAS(2);
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE, 
                UN_ERROR_COD => PCK_ERRORES.ER_CONTAB_INSDETALLE, UN_TABLAERROR => 'DETALLE_COMPROBANTE_CNT', UN_REEMPLAZOS => MI_MSGERROR );
        END;

        IF MI_CLASE_CUENTA = 'I' THEN

            --Valida que el tipo y codigo de retención existen.
            BEGIN
                    SELECT TIPO, CODIGO, CUENTA_CREDITO, ALEY1819
                        INTO MI_TIPO_RETENCION, MI_CODIGO_RETENCION, MI_CUENTA_RETENCION, MI_LEY1819
                        FROM RETENCIONES
                        WHERE COMPANIA = MI_DATOS_COLUMNAS(1)
                        AND TIPO = MI_DATOS_COLUMNAS(17)
                        AND ANO = MI_ANIO
                        AND CODIGO = MI_DATOS_COLUMNAS(18);
                EXCEPTION WHEN NO_DATA_FOUND THEN
                BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_RETORNO := 'El comprobante ' || MI_DATOS_COLUMNAS(3) || ' con cuenta ' || MI_CUENTA || ' el tipo de retencion no existe';
                    MI_MSGERROR (1).CLAVE := 'MENSAJE';
                    MI_MSGERROR (1).VALOR := MI_RETORNO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                                            UN_TABLAERROR => 'RETENCIONES',
                                            UN_REEMPLAZOS => MI_MSGERROR);
                END;
            END;                                            

                IF MI_CUENTA_RETENCION = MI_DATOS_COLUMNAS(5) THEN
                
                    MI_VALOR_PARAMETRO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'COMPROBANTES QUE NO REGISTRAN RETENCION EN CNTRETENCION' , PCK_DATOS.MODULOCONTABILIDAD , SYSDATE),'');

                   IF INSTR(',' || MI_VALOR_PARAMETRO || ',', ',' || MI_DATOS_COLUMNAS(2) || ',') = 0 THEN

                    MI_CAMPOS := 'COMPANIA
                                    ,ANO
                                    ,TIPO
                                    ,NUMERO
                                    ,TIPORETENCION
                                    ,CODIGORETENCION
                                    ,VALOR
                                    ,VALORBASE
                                    ,CALCULADO
                                    ,CREATED_BY
                                    ,DATE_CREATED
                                    ,REFERENCIADO
                                    ,CENTROCOSTO
                            ';

                    MI_VALORES := ''''|| MI_DATOS_COLUMNAS(1) ||'''
                                  ,  '|| MI_ANIO ||'
                                  ,'''|| MI_DATOS_COLUMNAS(2) ||'''
                                  ,'''|| MI_DATOS_COLUMNAS(3) ||'''
                                  ,'''|| MI_TIPO_RETENCION ||'''
                                  ,'''|| MI_CODIGO_RETENCION ||'''
                                  ,'''|| MI_DATOS_COLUMNAS(7) ||'''
                                  ,'''|| MI_DATOS_COLUMNAS(19) ||'''
                                  , -1
                                  ,'''|| UN_USUARIO ||'''
                                  ,SYSDATE
                                  ,'''|| MI_REFERENCIA ||'''
                                  ,'''|| MI_CENTROCOSTO ||'''
                                  ';

                    BEGIN
                        BEGIN
                            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'COMPROBANTE_CNTRETENCION'
                                                                ,UN_ACCION  => 'I'
                                                                ,UN_CAMPOS  => MI_CAMPOS
                                                                ,UN_VALORES => MI_VALORES);

                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                        END;
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                            MI_RETORNO := 'Error al crear retenciones en cuenta ' || MI_CUENTA_RETENCION || '';
                        MI_MSGERROR (1).CLAVE := 'MENSAJE';
                        MI_MSGERROR (1).VALOR := MI_RETORNO;
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                                                UN_TABLAERROR => 'COMPROBANTE_CNTRETENCION',
                                                UN_REEMPLAZOS => MI_MSGERROR);
                    END;
                    
                  END IF;

                ELSE 

                    BEGIN
                        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                        MI_RETORNO := 'La cuenta ' || MI_DATOS_COLUMNAS(5) || ' no coincide con la del tipo y codigo retención';
                        MI_MSGERROR (1).CLAVE := 'MENSAJE';
                        MI_MSGERROR (1).VALOR := MI_RETORNO;
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                                                UN_TABLAERROR => 'RETENCIONES',
                                                UN_REEMPLAZOS => MI_MSGERROR);
                    END;

                END IF;
                
            END IF;

        MI_COD_CONSECUTIVO := MI_COD_CONSECUTIVO + 1;
    
    END LOOP CREAR_DETALLES;

END PR_CARGAR_MOV_CONTABLES;

PROCEDURE PR_GENERARINGRESOOST
  /*
    NAME              : PR_GENERARINGRESOOST --> en Access GENERARINGRESO
    AUTHORS           : SYSMAN SAS
    AUTHOR            : MARÍA ALEJANDRA PEREZ SALAZAR
    DATE MIGRADOR     : 05/07/2024
    TIME              : 05:00 PM
    MODIFIER          : CRISTIAN FERNEY SUESCUN BARRERA
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Realiza el proceso de ingreso contable para los comprobantes OST.

    MODIFICATIONS     : Se actualiza el campo modificaciones cuando el valor actual = 0 CC:1666
    @NAME:  generarIngresoOST
    @METHOD:  put
  */ 
(  
    UN_COMPANIA                     IN PCK_SUBTIPOS.TI_COMPANIA,              --COMPANIA
    UN_ANO                          IN PCK_SUBTIPOS.TI_ANIO,                  --AÑO DEL COMPROBANTE AFECTANTE 
    UN_TIPO_CPTE                    IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,       --TIPO DEL COMPROBANTE EJEMPLO 
    UN_COMPROBANTE                  IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,  --NUMERO DEL COMPROBANTE AFECTANTE  
    UN_TERCERO                      IN PCK_SUBTIPOS.TI_TERCERO,               --TERCERO DEL COMPROBANTE
    UN_SUCURSAL                     IN PCK_SUBTIPOS.TI_SUCURSAL,              --SUCURSAL DEL COMPROBANTE 1
    UN_LISTANUMEROAFECTAR           IN PCK_SUBTIPOS.TI_STRSQL,                --LISTA DE LOS COMPROBANTES A AFERCTAR 
    UN_FECHA                        IN DATE,                                  --FECHA DEL COMPROBANTE AFECTANTE TIPO 
    UN_USUARIO                      IN VARCHAR2                               --USUARIO QUE CREA EL COMPROBANTE 
)
  AS  
  MI_VALOR                          PCK_SUBTIPOS.TI_DOBLE:=0;
  MI_CONSECUTIVO                    PCK_SUBTIPOS.TI_ENTERO_LARGO := 1;
  MI_CONSECUTIVOAFECT               PCK_SUBTIPOS.TI_ENTERO_LARGO := 1;
  MI_PCKDATOS                       PCK_SUBTIPOS.TI_RTA_ACME;
  MI_CAMPOS                         PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES                        PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION                      PCK_SUBTIPOS.TI_CONDICION;
  MI_CONSULTA                       PCK_SUBTIPOS.TI_STRSQL;
  RSDATOS                           SYS_REFCURSOR;
  MI_CUENTA                         PCK_SUBTIPOS.TI_CODIGOCONTA;
  MI_ANO                            PCK_SUBTIPOS.TI_ANIO;
  MI_TIPO                           PCK_SUBTIPOS.TI_TIPOCOMPROBANTE; 
  MI_NUMERO                         PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT; 
  MI_NUMEROPROCESO                  DETALLE_COMPROBANTE_CNT.NUMEROPROCESO%TYPE;
  MI_CENTRO_COSTO                   PCK_SUBTIPOS.TI_CENTRO_COSTO;
  MI_AUXILIAR                       PCK_SUBTIPOS.TI_AUXILIAR;
  MI_NATURALEZA                     PCK_SUBTIPOS.TI_NATURALEZACONTA;

BEGIN  
<<datos_comprobante_afectar>> 
      
    MI_CONSULTA := 'SELECT DISTINCT 
        PROCESOS_JUDICIALES.CODIGO,
        PROCESOS_JUDICIALES.TIPO_INGRESO,
        PROCESOS_JUDICIALES.COMPROBANTE_INGRESO,
        PROCESOS_JUDICIALES.VALOR_ACTUAL,
        PROCESOS_JUDICIALES.NUMEROPROCESO,
        EXTRACT(YEAR FROM PROCESOS_JUDICIALES.FECHA_INGRESO) ANIO
    FROM PROCESOS_JUDICIALES
    WHERE (COMPANIA,CODIGO,NUMEROPROCESO) IN ('||UN_LISTANUMEROAFECTAR||')
    ORDER BY NUMEROPROCESO';
    
    OPEN RSDATOS FOR MI_CONSULTA;
    LOOP
    FETCH RSDATOS INTO MI_CUENTA,MI_TIPO, MI_NUMERO, MI_VALOR, MI_NUMEROPROCESO, MI_ANO;
    EXIT WHEN RSDATOS%NOTFOUND;
    BEGIN 
        SELECT NATURALEZA 
          INTO MI_NATURALEZA
          FROM PLAN_CONTABLE 
         WHERE COMPANIA= UN_COMPANIA 
           AND ANO = MI_ANO 
           AND CODIGO = MI_CUENTA;
    END; 
    
    IF(MI_VALOR < 0)THEN
        MI_VALOR := MI_VALOR*(-1);
    END IF;
    
    BEGIN
        SELECT CENTRO_COSTO, AUXILIAR
          INTO MI_CENTRO_COSTO, MI_AUXILIAR
          FROM COMPROBANTE_CNT
         WHERE COMPROBANTE_CNT.COMPANIA=UN_COMPANIA
           AND ANO = MI_ANO
           AND TIPO = MI_TIPO
           AND NUMERO = MI_NUMERO
      ORDER BY NUMERO;
    END;
        
    BEGIN
        MI_CAMPOS := 'COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CONSECUTIVO,CUENTA,
                      FECHA,NATURALEZA,VALOR_DEBITO,VALOR_CREDITO,BASE_GRAVABLE,
                      CENTRO_COSTO,TERCERO,SUCURSAL,AUXILIAR,ANO_AFECT,TIPO_CPTE_AFECT,CMPTE_AFECTADO,
                      CONSECUTIVOAFECTADO,NUMEROPROCESO,CREATED_BY,DATE_CREATED';

        MI_VALORES := '''' || UN_COMPANIA || '''
                      ,' || UN_ANO || '
                      ,''' || UN_TIPO_CPTE || '''
                      ,' || UN_COMPROBANTE || '
                      ,' || MI_CONSECUTIVO || '
                      ,''' || MI_CUENTA || '''
                      ,TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                      ,''' ||  MI_NATURALEZA || '''                      
                      ,' || NVL(MI_VALOR,0) || '
                      ,' || 0 || '
                      ,' || MI_VALOR || '
                      ,''' || MI_CENTRO_COSTO || '''
                      ,''' || UN_TERCERO || '''
                      ,''' || UN_SUCURSAL || '''
                      ,''' || MI_AUXILIAR || '''
                      ,' || MI_ANO || '
                      ,''' || MI_TIPO || '''
                      ,' || MI_NUMERO || '
                      ,' || MI_CONSECUTIVOAFECT || '
                      ,''' || MI_NUMEROPROCESO || '''
                      ,''' || UN_USUARIO || '''
                      , SYSDATE';    

        MI_PCKDATOS := PCK_DATOS.FC_ACME(
                            UN_TABLA => 'DETALLE_COMPROBANTE_CNT', 
                            UN_ACCION => 'I', 
                            UN_CAMPOS => MI_CAMPOS, 
                            UN_VALORES => MI_VALORES
                            );
                          EXCEPTION
                            WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                              PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD => SQLCODE,
                                UN_TABLAERROR => 'DETALLE_COMPROBANTE_CNT',
                                UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_CREARDETPPTALVARIOS
                              );
        END;
        BEGIN
              MI_CAMPOS := 'VALOR_ACTUAL = 0,
              MODIFICACIONES = NVL(MODIFICACIONES,0) - '|| NVL(MI_VALOR,0) ||',
              DATE_MODIFIED = CURRENT_DATE,
              MODIFIED_BY = '''|| UN_USUARIO ||''' ';

            MI_CONDICION := 'COMPANIA = '''|| UN_COMPANIA ||'''
                         AND CODIGO = '''|| MI_CUENTA || '''
                         AND NUMEROPROCESO ='''|| MI_NUMEROPROCESO ||''' ';

  
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'PROCESOS_JUDICIALES'
                                                    ,UN_ACCION    => 'M'
                                                    ,UN_CAMPOS    => MI_CAMPOS
                                                    ,UN_CONDICION => MI_CONDICION);                    
        END;         
        MI_CONSECUTIVO:= MI_CONSECUTIVO+1;
        MI_CONSECUTIVOAFECT:= MI_CONSECUTIVOAFECT+1;

END LOOP datos_comprobante_afectar;      

END PR_GENERARINGRESOOST;

FUNCTION FC_VALIDAR_AUXILIARES_EGRESOS
   /*
    NAME              : FC_VALIDAR_AUXILIARES_EGRESOS
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MIGRADOR     : 16/01/2025
    TIME              : 10:47 AM
    MODIFIED          : MARIA ALEJANDRA PEREZ SALAZAR
      DATE MODIFIED     : 26/02/2025
      TIME              : 03:00 PM
    --NAME:   validarAuxiliaresEgresos
    --METHOD:  GET
    */
(
     UN_COMPANIA            IN  PCK_SUBTIPOS.TI_COMPANIA,
     UN_ANO                 IN  PCK_SUBTIPOS.TI_ANIO,
     UN_TIPO_CPTE           IN  PCK_SUBTIPOS.TI_CLASECOMPROPPTO,
     UN_COMPROBANTE         IN  PCK_SUBTIPOS.TI_NUMEROCOMPROBANTEPPTAL,
     UN_VALORBANCO          IN  PCK_SUBTIPOS.TI_DOBLE,
     UN_REFERENCIA          IN  PCK_SUBTIPOS.TI_REFERENCIA,
     UN_CENTRO_COSTO        IN  PCK_SUBTIPOS.TI_CENTRO_COSTO,
     UN_LISTANUMEROAFECTAR  IN  PCK_SUBTIPOS.TI_STRSQL
)
RETURN CLOB AS
    MI_VALORORDEN       PCK_SUBTIPOS.TI_DOBLE:=0;
    MI_VALORBANCO       PCK_SUBTIPOS.TI_DOBLE:=0;
    MI_EXISTE           PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_RETORNO          CLOB:= '';
    MI_REFERENCIAS      VARCHAR2(2000 CHAR):= '';
    MI_CENTROSCOSTO     VARCHAR2(2000 CHAR):= '';
    MI_CONSULTA         PCK_SUBTIPOS.TI_STRSQL;
    
BEGIN
    /*Valor total de las cuentas banco por referencia y centro de costo*/
    BEGIN
        SELECT NVL(SUM(VALOR),0) VALOR_TOTAL
          INTO MI_VALORBANCO
          FROM COMPROBANTE_CNTBANCOS
         WHERE COMPANIA = UN_COMPANIA
           AND ANO = UN_ANO
           AND TIPO = UN_TIPO_CPTE
           AND NUMERO = UN_COMPROBANTE
           AND REFERENCIA = UN_REFERENCIA
           AND CENTRO_COSTO = UN_CENTRO_COSTO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_VALORBANCO := 0;
    END;
    
    /*Valor total de la orden de pago por referencia y centro de costo*/
    BEGIN
        MI_CONSULTA := '
                SELECT NVL(SUM((VALOR_CREDITO - VALOR_DEBITO ) - (DEBITO_AFECTADO + CREDITO_AFECTADO)),0) valor 
                  FROM DETALLE_COMPROBANTE_CNT
                 INNER JOIN PLAN_CONTABLE
                    ON DETALLE_COMPROBANTE_CNT.COMPANIA = PLAN_CONTABLE.COMPANIA
                   AND DETALLE_COMPROBANTE_CNT.ANO = PLAN_CONTABLE.ANO
                   AND DETALLE_COMPROBANTE_CNT.CUENTA = PLAN_CONTABLE.CODIGO
                 WHERE DETALLE_COMPROBANTE_CNT.COMPANIA='''||UN_COMPANIA||'''
                   AND DETALLE_COMPROBANTE_CNT.REFERENCIA = '''||UN_REFERENCIA||'''
                   AND DETALLE_COMPROBANTE_CNT.CENTRO_COSTO = '''||UN_CENTRO_COSTO||''' 
                   AND PLAN_CONTABLE.CLASECUENTA = ''P''
                   AND (DETALLE_COMPROBANTE_CNT.ANO, DETALLE_COMPROBANTE_CNT.TIPO_CPTE, DETALLE_COMPROBANTE_CNT.COMPROBANTE) IN ('||UN_LISTANUMEROAFECTAR||')';
        EXECUTE IMMEDIATE MI_CONSULTA INTO MI_VALORORDEN;
    EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_VALORORDEN:=0;
    END;
    
    /*Se valida si las referencias a ingresar coinciden con las de los
    comprobantes a afectar*/
    BEGIN
        MI_CONSULTA := '
                SELECT COUNT(*)
                  FROM DETALLE_COMPROBANTE_CNT
                 WHERE COMPANIA = '''||UN_COMPANIA||'''
                   AND REFERENCIA = '''||UN_REFERENCIA||'''
                   AND CENTRO_COSTO = '''||UN_CENTRO_COSTO||'''  
                   AND (ANO, TIPO_CPTE, COMPROBANTE) IN ('||UN_LISTANUMEROAFECTAR||')';
        EXECUTE IMMEDIATE MI_CONSULTA INTO MI_EXISTE;
    EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_EXISTE:=0;
    END;    
    
    IF(MI_EXISTE = 0)THEN
        /*Se obtienen las referencias de los comprobantes a afectar*/
        BEGIN
            MI_CONSULTA := '
                        SELECT LISTAGG(REFERENCIA, '', '' ON OVERFLOW TRUNCATE) WITHIN GROUP (ORDER BY REFERENCIA) REFERENCIAS
                        FROM (
                        SELECT  DISTINCT
                        REFERENCIA       
                        FROM DETALLE_COMPROBANTE_CNT
                        WHERE COMPANIA = '''||UN_COMPANIA||'''  
                        AND (ANO, TIPO_CPTE, COMPROBANTE) IN ('||UN_LISTANUMEROAFECTAR||'))';
            EXECUTE IMMEDIATE MI_CONSULTA INTO MI_REFERENCIAS;
        EXCEPTION WHEN NO_DATA_FOUND THEN
              MI_EXISTE:=0;
        END;
        
        /*Se obtienen los centros de los comprobantes a afectar*/
         BEGIN
            MI_CONSULTA := '
                        SELECT LISTAGG(CENTRO_COSTO, '', '' ON OVERFLOW TRUNCATE) WITHIN GROUP (ORDER BY CENTRO_COSTO) CENTROSCOSTO
                        FROM (
                        SELECT  DISTINCT
                        CENTRO_COSTO       
                        FROM DETALLE_COMPROBANTE_CNT
                        WHERE COMPANIA = '''||UN_COMPANIA||'''  
                        AND (ANO, TIPO_CPTE, COMPROBANTE) IN ('||UN_LISTANUMEROAFECTAR||'))';
            EXECUTE IMMEDIATE MI_CONSULTA INTO MI_CENTROSCOSTO;
        EXCEPTION WHEN NO_DATA_FOUND THEN
              MI_EXISTE:=0;
        END;
        
        MI_RETORNO := 'La referencia '|| UN_REFERENCIA || ' y/o el centro de costo ' || UN_CENTRO_COSTO ||
                       ' No coinciden con las referencias '||MI_REFERENCIAS||' y/o los centros de costo ' ||MI_CENTROSCOSTO|| 
                       ' de los comprobantes a afectar';    
    ELSE    
        IF((MI_VALORBANCO+UN_VALORBANCO)>MI_VALORORDEN)THEN
            MI_RETORNO := 'El valor total de la(s) cuenta(s) supera el valor de la orden de pago';    
        END IF;
    END IF;
    
    RETURN  MI_RETORNO;
END FC_VALIDAR_AUXILIARES_EGRESOS;

FUNCTION FC_VALIDARCSALDOAFECTADO(
/*
    NAME              : FC_VALIDARCSALDOAFECTADO
    AUTHOR            : CRISTIAN FERNEY SUESCUN BARRERA
    MIGRATION DATE    : 06/12/2024
    TICKET            : 7801666
    DESCRIPTION       : Función diseñada para controlar el proceso de actualización de saldos 
                        (crédito y débito) asociados a una factura. Garantiza que, al realizar 
                        una nota contable, los valores no superen los montos originales de la factura.
    --NAME:           'validarcsaldoafectado'
    --METHOD:         PUT
*/


  UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANOCPTE                  IN SF_FACTURA.ANO_CPTE%TYPE, 
  UN_TIPOCPTE                 IN VARCHAR2,
  UN_NROCPTE                  IN VARCHAR2,
  UN_VALORCREDITO_NUEVO       IN DETALLE_COMPROBANTE_CNT.VALOR_CREDITO%TYPE,
  UN_VALORDEBITO_NUEVO        IN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO %TYPE,
  UN_NROCOMPROBANTE_NUEVO     IN DETALLE_COMPROBANTE_CNT.COMPROBANTE%TYPE,
  UN_TIPOCPTE_NUEVO           IN DETALLE_COMPROBANTE_CNT.TIPO_CPTE%TYPE,
  UN_ANONUEVO                 IN DETALLE_COMPROBANTE_CNT.ANO%TYPE, --CC_3320(27/01/2026 JCROJAS)																							
  UN_CUENTA                   IN PCK_SUBTIPOS.TI_CODIGOCONTA
)
RETURN SF_FACTURA.VALOR_TOTAL%TYPE AS
MI_VALOR_CREDITO_ANTERIOR SF_FACTURA.VALOR_TOTAL%TYPE;
MI_VALOR_DEBITO_ANTERIOR SF_FACTURA.VALOR_TOTAL%TYPE;

  MI_VALORTOTAL SF_FACTURA.VALOR_TOTAL%TYPE;
  MI_NATURALEZA DETALLE_COMPROBANTE_CNT.NATURALEZA%TYPE;
  MI_CUE_EQUI_CAR DETALLE_COMPROBANTE_CNT.CUENTA%TYPE;
 
  
BEGIN
BEGIN
        SELECT 
                CASE WHEN NATURALEZA = 'C' THEN NVL(VALOR_DEBITO,0)ELSE NVL(0,0) END VALOR_CREDITO,
                CASE WHEN NATURALEZA = 'D' THEN NVL(VALOR_CREDITO,0)ELSE NVL(0,0) END VALOR_DEBITO,
                NATURALEZA
                INTO MI_VALOR_CREDITO_ANTERIOR,MI_VALOR_DEBITO_ANTERIOR,MI_NATURALEZA
                FROM DETALLE_COMPROBANTE_CNT
                WHERE DETALLE_COMPROBANTE_CNT.COMPANIA = UN_COMPANIA 
                  AND DETALLE_COMPROBANTE_CNT.ANO = UN_ANONUEVO --CC_3320(27/01/2026 JCROJAS)
                  AND DETALLE_COMPROBANTE_CNT.CUENTA = UN_CUENTA
                  AND DETALLE_COMPROBANTE_CNT.COMPROBANTE = UN_NROCOMPROBANTE_NUEVO
                  AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = UN_TIPOCPTE_NUEVO;                
        END;
  
      
    IF UN_TIPOCPTE = 'null' AND UN_NROCPTE IS NULL THEN 
    
        BEGIN
            SELECT 
                NVL(VALOR_DEBITO,0)
                INTO MI_VALOR_DEBITO_ANTERIOR
                FROM DETALLE_COMPROBANTE_CNT
                WHERE DETALLE_COMPROBANTE_CNT.COMPANIA = UN_COMPANIA 
                  AND DETALLE_COMPROBANTE_CNT.ANO = UN_ANONUEVO --CC_3320(27/01/2026 JCROJAS)
                  AND DETALLE_COMPROBANTE_CNT.CUENTA = UN_CUENTA
                  AND DETALLE_COMPROBANTE_CNT.COMPROBANTE = UN_NROCOMPROBANTE_NUEVO
                  AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = UN_TIPOCPTE_NUEVO;                
        END;
        
    
        BEGIN
		    --CC_3320(27/01/2026 JCROJAS): Se cambia parametro que recibe en el filtro del ANO y se agrega filtro de la CUENTA
            SELECT 
              ((AFECT.VALOR_DEBITO - (AFECT.DEBITO_AFECTADO - MI_VALOR_DEBITO_ANTERIOR)) - UN_VALORDEBITO_NUEVO) 
              INTO MI_VALORTOTAL
            FROM DETALLE_COMPROBANTE_CNT DET     
            INNER JOIN 
                COMPROBANTE_CNTAFECTADOS
                ON DET.COMPANIA = COMPROBANTE_CNTAFECTADOS.COMPANIA
                AND DET.COMPROBANTE = COMPROBANTE_CNTAFECTADOS.COMPROBANTE
                AND DET.TIPO_CPTE = COMPROBANTE_CNTAFECTADOS.TIPO_CPTE
            INNER JOIN 
                DETALLE_COMPROBANTE_CNT AFECT
                ON AFECT.COMPANIA = COMPROBANTE_CNTAFECTADOS.COMPANIA
                AND AFECT.COMPROBANTE = COMPROBANTE_CNTAFECTADOS.COMPROBANTE_AFECT
                AND AFECT.TIPO_CPTE = COMPROBANTE_CNTAFECTADOS.TIPO_CPTE_AFECT
                AND AFECT.TERCERO = DET.TERCERO
            WHERE DET.COMPANIA = UN_COMPANIA
              AND DET.ANO= UN_ANONUEVO
              AND DET.TIPO_CPTE= UN_TIPOCPTE_NUEVO
              AND DET.COMPROBANTE= UN_NROCOMPROBANTE_NUEVO
              AND AFECT.NATURALEZA = 'D'
			  AND DET.CUENTA = UN_CUENTA		
            GROUP BY 
                AFECT.VALOR_DEBITO,
                AFECT.DEBITO_AFECTADO;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_VALORTOTAL := -1;
        END; 

    
    ELSE
        BEGIN
            BEGIN
                    --CC_3320(27/01/2026 JCROJAS): Se agrega filtro de la CUENTA
                    SELECT
                        CASE
                            WHEN MI_NATURALEZA = 'C' THEN (NVL(VALOR_CREDITO, 0) - NVL(CREDITO_AFECTADO, 0)) + NVL(ABONOINICIAL, 0) + MI_VALOR_CREDITO_ANTERIOR - UN_VALORDEBITO_NUEVO
                            WHEN MI_NATURALEZA = 'D' THEN (NVL(VALOR_DEBITO, 0) - NVL(DEBITO_AFECTADO, 0)) + NVL(CREDITO_AFECTADO, 0) + NVL(ABONOINICIAL, 0)+ MI_VALOR_DEBITO_ANTERIOR - UN_VALORCREDITO_NUEVO
                        END AS VALOR
                    INTO MI_VALORTOTAL
                    FROM DETALLE_COMPROBANTE_CNT
                    WHERE DETALLE_COMPROBANTE_CNT.COMPANIA = UN_COMPANIA
                      AND DETALLE_COMPROBANTE_CNT.ANO = UN_ANOCPTE
                      AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = UN_TIPOCPTE
                      AND DETALLE_COMPROBANTE_CNT.COMPROBANTE = UN_NROCPTE
                      AND DETALLE_COMPROBANTE_CNT.NATURALEZA = MI_NATURALEZA
                      AND DETALLE_COMPROBANTE_CNT.CUENTA = UN_CUENTA;
                    EXCEPTION WHEN NO_DATA_FOUND THEN
                        BEGIN 
                            SELECT COD_EQUI_CARTERA
                                INTO MI_CUE_EQUI_CAR
                            FROM PLAN_CONTABLE
                            WHERE COMPANIA = UN_COMPANIA 
                            AND ANO = UN_ANONUEVO 
                            AND CODIGO = UN_CUENTA;
                        
                            BEGIN
                                SELECT CASE WHEN MI_NATURALEZA = 'C' THEN (NVL(VALOR_CREDITO, 0) - NVL(CREDITO_AFECTADO, 0)) + NVL(ABONOINICIAL, 0) + MI_VALOR_CREDITO_ANTERIOR - UN_VALORDEBITO_NUEVO
                                            WHEN MI_NATURALEZA = 'D' THEN (NVL(VALOR_DEBITO, 0) - NVL(DEBITO_AFECTADO, 0)) + NVL(CREDITO_AFECTADO, 0) + NVL(ABONOINICIAL, 0)+ MI_VALOR_DEBITO_ANTERIOR - UN_VALORCREDITO_NUEVO
                                        END AS VALOR
                                    INTO MI_VALORTOTAL
                                    FROM DETALLE_COMPROBANTE_CNT
                                    WHERE DETALLE_COMPROBANTE_CNT.COMPANIA = UN_COMPANIA
                                      AND DETALLE_COMPROBANTE_CNT.ANO = UN_ANOCPTE
                                      AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = UN_TIPOCPTE
                                      AND DETALLE_COMPROBANTE_CNT.COMPROBANTE = UN_NROCPTE
                                      AND DETALLE_COMPROBANTE_CNT.NATURALEZA = MI_NATURALEZA
                                      AND DETALLE_COMPROBANTE_CNT.CUENTA = MI_CUE_EQUI_CAR;
                            EXCEPTION WHEN NO_DATA_FOUND THEN
                                MI_VALORTOTAL := -2;
                            END;
                        EXCEPTION
                            WHEN NO_DATA_FOUND THEN
                                MI_VALORTOTAL := -2;
                        END;
            END;
        END;   
    END IF;
    
    RETURN MI_VALORTOTAL;
END FC_VALIDARCSALDOAFECTADO;

PROCEDURE PR_ACTUALIZARSALDOAFECT(
/*
    NAME              : FC_VALIDARCSALDOAFECTADO
    AUTHOR            : CRISTIAN FERNEY SUESCUN BARRERA
    MIGRATION DATE    : 06/12/2024
    TICKET            : 7801666
    DESCRIPTION       : Función diseñada para la actualización de saldos 
                        (crédito y débito) asociados a una factura. Garantiza que, al realizar 
                        los valores no superen los montos originales de la factura.
    --NAME:           'validarcsaldoafectado'
    --METHOD:         PUT */



  UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANOCPTE                  IN SF_FACTURA.ANO_CPTE%TYPE, 
  UN_TIPOCPTE                 IN SF_FACTURA.TIPO_CPTE%TYPE,
  UN_NROCPTE                  IN SF_FACTURA.NRO_CPTE%TYPE,
  UN_VALORCREDITO_NUEVO       IN DETALLE_COMPROBANTE_CNT.VALOR_CREDITO%TYPE,
  UN_VALORDEBITO_NUEVO        IN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO %TYPE,
  UN_NROCOMPROBANTE_NUEVO     IN DETALLE_COMPROBANTE_CNT.COMPROBANTE%TYPE,
  UN_TIPOCPTE_NUEVO           IN DETALLE_COMPROBANTE_CNT.TIPO_CPTE%TYPE,
  UN_ANONUEVO                 IN DETALLE_COMPROBANTE_CNT.ANO%TYPE, --CC_3320(27/01/2026 JCROJAS)																								
  UN_CUENTA                   IN PCK_SUBTIPOS.TI_CODIGOCONTA,
  UN_ACCION                   IN NUMBER, 
  UN_CONSECUTIVO              IN DETALLE_COMPROBANTE_CNT.CONSECUTIVO%TYPE 
)AS
MI_TABLA                          PCK_SUBTIPOS.TI_TABLA;
MI_CONDICION                      PCK_SUBTIPOS.TI_CONDICION;
MI_CAMPOS                         PCK_SUBTIPOS.TI_CAMPOS;
MI_CONSECUTIVO_AFECT              NUMBER; --1737_FACTURACION(MROSERO)


MI_VALOR_CREDITO_ANTERIOR SF_FACTURA.VALOR_TOTAL%TYPE;
MI_VALOR_DEBITO_ANTERIOR SF_FACTURA.VALOR_TOTAL%TYPE;
MI_NATURALEZA DETALLE_COMPROBANTE_CNT.NATURALEZA%TYPE; 




BEGIN
BEGIN
		 --CC_3320(27/01/2026 JCROJAS): Se cambia parametro que recibe en el filtro del ANO
         SELECT 
                CASE WHEN NATURALEZA = 'C' THEN NVL(VALOR_DEBITO,0)ELSE NVL(0,0) END,
                CASE WHEN NATURALEZA = 'D' THEN NVL(VALOR_CREDITO,0)ELSE NVL(0,0) END,
                NATURALEZA, CONSECUTIVOAFECTADO
                INTO MI_VALOR_DEBITO_ANTERIOR,MI_VALOR_CREDITO_ANTERIOR,MI_NATURALEZA,MI_CONSECUTIVO_AFECT
                FROM DETALLE_COMPROBANTE_CNT
                WHERE DETALLE_COMPROBANTE_CNT.COMPANIA = UN_COMPANIA 
                  AND DETALLE_COMPROBANTE_CNT.ANO = UN_ANONUEVO
                  AND DETALLE_COMPROBANTE_CNT.CUENTA = UN_CUENTA
                  AND DETALLE_COMPROBANTE_CNT.COMPROBANTE = UN_NROCOMPROBANTE_NUEVO
                  AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = UN_TIPOCPTE_NUEVO
                  AND DETALLE_COMPROBANTE_CNT.CONSECUTIVO = UN_CONSECUTIVO; --1737_FACTURACION(MROSERO)               
        END;

    BEGIN
         MI_TABLA := 'DETALLE_COMPROBANTE_CNT';
         IF MI_NATURALEZA = 'C' THEN 
               MI_CAMPOS :=  'CREDITO_AFECTADO = (( CREDITO_AFECTADO - ' || MI_VALOR_DEBITO_ANTERIOR  || ') + '|| CASE WHEN UN_ACCION = 0 THEN  UN_VALORDEBITO_NUEVO ELSE 0 END|| ')';
            ELSE
                MI_CAMPOS :='DEBITO_AFECTADO = (( DEBITO_AFECTADO - ' ||  MI_VALOR_CREDITO_ANTERIOR || ' ) + '||  CASE WHEN UN_ACCION = 0 THEN UN_VALORCREDITO_NUEVO ELSE 0 END|| ')';
           END IF;
            MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || 
                            ''' AND ANO = ' || UN_ANOCPTE || 
                            ' AND TIPO_CPTE = ''' || UN_TIPOCPTE || '''
                              AND COMPROBANTE = ''' || UN_NROCPTE || 
                            ''' AND CUENTA = ' || UN_CUENTA ||
                            ' AND CONSECUTIVO = ' || MI_CONSECUTIVO_AFECT; --1737_FACTURACION(MROSERO)
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOS,
                                                  UN_CONDICION => MI_CONDICION);   
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
          END;
          COMMIT;
END PR_ACTUALIZARSALDOAFECT;

PROCEDURE PR_ACTUALIZARSALDOPJ(
/*
    NAME              : PR_ACTUALIZARSALDOPJ
    AUTHOR            : MARIA ALEJANDRA PEREZ SALAZAR
    MIGRATION DATE    : 28/05/2025
    TICKET            : CC1600
    DESCRIPTION       : Función diseñada para la actualización de saldos asociados al proceso judicial
    --NAME:           'actualizasaldoPJ'
    --METHOD:         PUT */



  UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CUENTA                   IN PCK_SUBTIPOS.TI_CODIGOCONTA,
  UN_VALORDEBITO              IN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO %TYPE,
  UN_NUMEROPROCESO            IN PROCESOS_JUDICIALES.NUMEROPROCESO%TYPE 
)AS

    MI_TABLA                          PCK_SUBTIPOS.TI_TABLA;
    MI_CONDICION                      PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS                         PCK_SUBTIPOS.TI_CAMPOS;

BEGIN
      
    BEGIN
        MI_TABLA := 'PROCESOS_JUDICIALES';
        MI_CAMPOS :=    'VALOR_ACTUAL = '''|| UN_VALORDEBITO ||''',
                        DATE_MODIFIED = CURRENT_DATE';

        MI_CONDICION := 'COMPANIA = '''|| UN_COMPANIA ||'''
                         AND CODIGO = '''|| UN_CUENTA || '''
                         AND NUMEROPROCESO ='''|| UN_NUMEROPROCESO ||''' ';
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOS,
                                                  UN_CONDICION => MI_CONDICION);   
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
    END;
    COMMIT;
END PR_ACTUALIZARSALDOPJ;

FUNCTION FC_VAL_CTAS_AFEC
   /*
      NAME              : FC_VAL_CTAS_AFEC
      AUTHORS           : SYSMAN
      DATE_CREATOR      : NYDIA KATHERINE CARDENAS MONGUI
      DATE              : 26/02/2025
      TIME              : 10:10
      SOURCE MODULE     : GENERAL
      DESCRIPTION       : Funcion que valida si las cuentas pertenecen a clase cuenta BANCOS O CAJA de ser asi devuelve un 1.
      PARAMETERS        : UN_COMPANIA             => Compañia que se tiene de la plantilla.
                          UN_ANO                  => ano que se tiene de la plantilla.
                          UN_LISTACODIGOS         => Lista del formulario subir movimientos contables al momento de subir la plantilla se obtienen las cuentas 
                                                     que se deben revisar la clase cuenta.
    --NAME:  evaluarCuenta
   
        
      */
 (  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO                     IN PCK_SUBTIPOS.TI_ANIO,
    UN_LISTACODIGOS            IN PCK_SUBTIPOS.TI_STRSQL
   
 )
RETURN CLOB
AS
 
 MI_CONSULTA         PCK_SUBTIPOS.TI_STRSQL;
 MI_RETORNO          CLOB := '';
  RS                  SYS_REFCURSOR;
 MI_CODIGOS          CLOB := '';
 MI_RETORNOANT       CLOB := '';
 
    BEGIN
 MI_CONSULTA :='
         SELECT CODIGO
         FROM plan_contable
         WHERE compania = '''||UN_COMPANIA||'''
                AND ano = '||UN_ANO||'
                AND codigo in('||UN_LISTACODIGOS||') 
                AND CLASECUENTA IN(''B'',''J'')';
OPEN RS FOR MI_CONSULTA;
 LOOP     
 FETCH RS INTO MI_CODIGOS;
EXIT WHEN RS%NOTFOUND;           
BEGIN
 MI_RETORNO:= MI_RETORNO || MI_CODIGOS||',';
 END;
END LOOP;
IF  MI_RETORNO IS NULL THEN
    MI_RETORNO := '';
END IF;

RETURN MI_RETORNO;
END FC_VAL_CTAS_AFEC;

PROCEDURE PR_GENERARANTICIPO
  /*
    NAME              : PR_GENERARANTICIPO
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : 
    DATE MIGRADOR     : 
    TIME              :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    MODIFICATIONS     :
    --NAME:  generarAnticipo
    --METHOD:  put
  */
(
    UN_COMPANIA                     IN PCK_SUBTIPOS.TI_COMPANIA,              --COMPANIA
    UN_ANO                          IN PCK_SUBTIPOS.TI_ANIO,                  --AÑO DEL COMPROBANTE AFECTANTE EJEMPLO NBA
    UN_TIPO_CPTE                    IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,       --TIPO DEL COMPROBANTE EJEMPLO NBA
    UN_COMPROBANTE                  IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,  --NUMERO DEL COMPROBANTE AFECTANTE EJEMPLO 2017000001
    UN_TERCERO                      IN PCK_SUBTIPOS.TI_TERCERO,               --TERCERO DEL COMPROBANTE NBA EJEMPLO 800021261
    UN_SUCURSAL                     IN PCK_SUBTIPOS.TI_SUCURSAL,              --SUCURSAL DEL COMPROBANTE NBA EJEMPLO 001
    UN_LISTAFACTURAS                IN PCK_SUBTIPOS.TI_STRSQL,                 --LISTA DE LOS COMPROBANTES A AFERCTAR EJEMPLO UNA FACTURA FDS
    UN_LISTAANTICIPOS               IN PCK_SUBTIPOS.TI_STRSQL,                --LISTA DE LOS COMPROBANTES A AFERCTAR EJEMPLO UNA FACTURA NBA
    UN_FECHA                        IN DATE,                                  --FECHA DEL COMPROBANTE AFECTANTE TIPO NBA EJEMPLO 01/01/2017
    UN_CLASE                        IN PCK_SUBTIPOS.TI_CLASECOMPROBANTE,      --CLASE DEL COMPROBANTE AFECTANTE NBA EJEMPLO B
    UN_VALOR                        IN PCK_SUBTIPOS.TI_DOBLE,                 --VALOR DEL COMPROBANTE AFECTANTE NBA EJEMPLO B
    UN_USUARIO                      IN VARCHAR2                               --USUARIO QUE CREA EL COMPROBANTE EJEMPLO ADMIN
    )
  AS
  MI_RTA                            PCK_SUBTIPOS.TI_STRSQL;
  MI_VALOR                          PCK_SUBTIPOS.TI_DOBLE:=0;
  MI_CONSECUTIVODET                 PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_OTRADESCRIPCION                PCK_SUBTIPOS.TI_STRSQL;
  MI_DESCRIPCION                    PCK_SUBTIPOS.TI_STRSQL;
  MI_DESCRIPCION_AFECT              PCK_SUBTIPOS.TI_STRSQL := ' Cancelaci¿n ';
  MI_PCKDATOS                       PCK_SUBTIPOS.TI_RTA_ACME;
  MI_CAMPOS                         PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION                      PCK_SUBTIPOS.TI_CONDICION;
  MI_VALORES                        PCK_SUBTIPOS.TI_VALORES;
  MI_CONSULTA                       PCK_SUBTIPOS.TI_STRSQL;
  RSDATOS                           SYS_REFCURSOR;
  RSCONTRACUENTAS                   SYS_REFCURSOR;
  MI_ANO                            PCK_SUBTIPOS.TI_ANIO;
  MI_TIPO                           PCK_SUBTIPOS.TI_TIPOCOMPROBANTE;
  MI_NUMERO                         PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT;
  MI_TEXTO                          PCK_SUBTIPOS.TI_STRSQL;
  MICONTRACUENTAS_CUENTA            PCK_SUBTIPOS.TI_CODIGOCONTA;
  MICONTRACUENTAS_CLASECUENTA       PCK_SUBTIPOS.TI_CLASECUENTACONTA;
  MICONTRACUENTAS_ANIO              PCK_SUBTIPOS.TI_ANIO;
  MICONTRACUENTAS_TIPO_CPTE         PCK_SUBTIPOS.TI_TIPOCOMPROBANTE;
  MICONTRACUENTAS_NATURALEZA        PCK_SUBTIPOS.TI_NATURALEZACONTA;
  MICONTRACUENTAS_COMPROBANTE       PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT;
  MICONTRACUENTAS_CONSECUTIVO       PCK_SUBTIPOS.TI_CONSECUTIVOCNT;
  MICONTRACUENTAS_NRO_DOCUMENTO     DETALLE_COMPROBANTE_CNT.NRO_DOCUMENTO%TYPE;
  MICONTRACUENTAS_TERCERO           PCK_SUBTIPOS.TI_TERCERO;
  MICONTRACUENTAS_SUCURSAL          PCK_SUBTIPOS.TI_SUCURSAL;
  MICONTRACUENTAS_CENTRO_COSTO      PCK_SUBTIPOS.TI_CENTRO_COSTO;
  MICONTRACUENTAS_AUXILIAR          PCK_SUBTIPOS.TI_AUXILIAR;
  MICONTRA_CUENTA_PPTAL             PCK_SUBTIPOS.TI_CODIGOPPTAL;
  MICONTRACUENTAS_ID                VARCHAR2(4000);
  MICONTRACUENTAS_VALOR_DEBITO      PCK_SUBTIPOS.TI_DOBLE:=0;
  MICONTRACUENTAS_VALOR_CREDITO     PCK_SUBTIPOS.TI_DOBLE:=0;
  MICONTRA_DEBITO_AFECTADO          PCK_SUBTIPOS.TI_DOBLE:=0;
  MICONTRA_CREDITO_AFECTADO         PCK_SUBTIPOS.TI_DOBLE:=0;
  MI_TABLA                          PCK_SUBTIPOS.TI_TABLA;
  MI_AUX                            PCK_SUBTIPOS.TI_TEXTO1;
  MI_PARAMETRO                      PARAMETRO.VALOR%TYPE;
  MI_CODEQUICARTERA                 PLAN_CONTABLE.COD_EQUI_CARTERA%TYPE;
  MI_NUMEROCONTRATO                 NUMBER(20);--TICKET 7742952 MROSERO
  MI_TIPOCONTRATO                   VARCHAR(3);
  MIEQUIVSIGEC                      VARCHAR(250);
  MI_CONSULTA1                      PCK_SUBTIPOS.TI_STRSQL;
  MI_CONSULTA2                      PCK_SUBTIPOS.TI_STRSQL;
  MI_CONDICION_BASE                 PCK_SUBTIPOS.TI_STRSQL;
  MI_FILTRO1                        PCK_SUBTIPOS.TI_STRSQL;
  MI_FILTRO2                        PCK_SUBTIPOS.TI_STRSQL;
  MI_SQL_BASE                       PCK_SUBTIPOS.TI_STRSQL;
  MI_CLASECUENTA		            PCK_SUBTIPOS.TI_CLASECUENTACONTA;
  MI_ANIO                           PCK_SUBTIPOS.TI_ANIO;
  MI_TIPO_CPTE		                PCK_SUBTIPOS.TI_TIPOCOMPROBANTE;
  MI_COMPROBANTE	            	PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT;
  MI_CONSECUTIVO		            PCK_SUBTIPOS.TI_CONSECUTIVOCNT;
  MI_NATURALEZA		                PCK_SUBTIPOS.TI_NATURALEZACONTA;
  MI_NRO_DOCUMENTO	            	DETALLE_COMPROBANTE_CNT.NRO_DOCUMENTO%TYPE;
  MI_TERCERO		                PCK_SUBTIPOS.TI_TERCERO;
  MI_SUCURSAL	                 	PCK_SUBTIPOS.TI_SUCURSAL;
  MI_CENTRO_COSTO		            PCK_SUBTIPOS.TI_CENTRO_COSTO;
  MI_AUXILIAR		                PCK_SUBTIPOS.TI_AUXILIAR;
  MI_CUENTA		                    PCK_SUBTIPOS.TI_CODIGOCONTA;
  MI_ID		                        DETALLE_COMPROBANTE_CNT.ID%TYPE;
  MI_VALOR_DEBITO		            PCK_SUBTIPOS.TI_DOBLE:=0;
  MI_VALOR_CREDITO		            PCK_SUBTIPOS.TI_DOBLE:=0;
  MI_VALOR_DEBITO_AFECTADO		    PCK_SUBTIPOS.TI_DOBLE:=0;
  MI_VALOR_CREDITO_AFECTADO		    PCK_SUBTIPOS.TI_DOBLE:=0;
  MI_EQUIV_SIGEC		            VARCHAR(250);
  MI_CUENTA_PPTAL		            PCK_SUBTIPOS.TI_CODIGOPPTAL;
  MI_VALOR_IVA		                PCK_SUBTIPOS.TI_DOBLE:=0;
  MI_VALOR_FALTANTE                 PCK_SUBTIPOS.TI_DOBLE:=0;
  MI_VALOR_FINAL                    PCK_SUBTIPOS.TI_DOBLE:=0;
  TYPE T_SALDOS IS TABLE OF NUMBER INDEX BY VARCHAR2(100);
  V_SALDOS                          T_SALDOS;
  V_LISTAFACTURAS_ORIG              PCK_SUBTIPOS.TI_STRSQL;
  V_LISTAANTICIPOS_ORIG             PCK_SUBTIPOS.TI_STRSQL;
  MI_DATOS_FACT                     PCK_SYSMAN_UTL.T_SPLIT;
  MI_DATOS_ANT                      PCK_SYSMAN_UTL.T_SPLIT;
BEGIN
    BEGIN
    
   V_LISTAANTICIPOS_ORIG := REGEXP_REPLACE(UN_LISTAANTICIPOS, ',''[0-9]+''\)', ')');
   V_LISTAFACTURAS_ORIG := REGEXP_REPLACE(UN_LISTAFACTURAS, ',''[0-9]+''\)', ')');
   MI_DATOS_ANT := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_LISTAANTICIPOS,
                                               UN_DELIMITADOR  => '),(');
   MI_DATOS_FACT := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_LISTAFACTURAS,
                                               UN_DELIMITADOR  => '),(');
    
    MI_PARAMETRO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                  , UN_NOMBRE    => 'MANEJA RECAUDO CON CODIGO EQUIVALENTE'
                  , UN_MODULO    => 1
                  , UN_FECHA_PAR => SYSDATE),'NO');
MI_CONSULTA := '
      SELECT NVL(SUM(DETALLE_COMPROBANTE_CNT.VALOR_DEBITO-(DETALLE_COMPROBANTE_CNT.DEBITO_AFECTADO - DETALLE_COMPROBANTE_CNT.CREDITO_AFECTADO)),0) MI_VALOR
      FROM V_PLAN_CONTABLE INNER JOIN DETALLE_COMPROBANTE_CNT
             ON  DETALLE_COMPROBANTE_CNT.COMPANIA = V_PLAN_CONTABLE.COMPANIA
             AND DETALLE_COMPROBANTE_CNT.ANO      = V_PLAN_CONTABLE.ANO
             AND DETALLE_COMPROBANTE_CNT.ID       = V_PLAN_CONTABLE.ID
      WHERE DETALLE_COMPROBANTE_CNT.COMPANIA='''||UN_COMPANIA||'''
             AND (DETALLE_COMPROBANTE_CNT.ANO, TIPO_CPTE, COMPROBANTE) IN ('||V_LISTAFACTURAS_ORIG||')
             AND V_PLAN_CONTABLE.CLASECUENTA IN (''C'')';
             EXECUTE IMMEDIATE MI_CONSULTA INTO MI_VALOR;
      EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_VALOR:=0;
      END;
   MI_CONSECUTIVODET := 1;
   MI_VALOR := ROUND(MI_VALOR, 2);
   
    MI_CONDICION_BASE :=
          'WHERE DET.COMPANIA = ''' || UN_COMPANIA || '''';

    IF UN_CLASE = 'P' THEN
        MI_FILTRO1 := ' AND (DET.ANO, TIPO_CPTE, COMPROBANTE) IN ('||V_LISTAFACTURAS_ORIG||')
                        AND V_PLAN_CONTABLE.CLASECUENTA IN (''P'',''E'')
                        AND DET.VALOR_CREDITO<>0';
        MI_FILTRO2 := ' AND (DET.ANO, TIPO_CPTE, COMPROBANTE) IN ('||V_LISTAANTICIPOS_ORIG||')
                        AND V_PLAN_CONTABLE.CLASECUENTA IN (''C'',''A'')
                        AND DET.VALOR_DEBITO <> 0';
    ELSIF UN_CLASE = 'V' THEN
        MI_FILTRO1 := ' AND (DET.ANO, TIPO_CPTE, COMPROBANTE) IN ('||V_LISTAFACTURAS_ORIG||')
                        AND V_PLAN_CONTABLE.CLASECUENTA IN (''C'',''A'')
                        AND DET.VALOR_DEBITO <> 0';
        MI_FILTRO2 := ' AND (DET.ANO, TIPO_CPTE, COMPROBANTE) IN ('||V_LISTAANTICIPOS_ORIG||')
                        AND V_PLAN_CONTABLE.CLASECUENTA IN (''P'',''E'')
                        AND DET.VALOR_CREDITO <> 0';
    ELSE
        MI_FILTRO1 := '';
        MI_FILTRO2 := '';
    END IF;
    
    PR_VALIDAR_COMPROBANTES(UN_COMPANIA, MI_DATOS_ANT, 'ANT', UN_CLASE);
    PR_VALIDAR_COMPROBANTES(UN_COMPANIA, MI_DATOS_FACT, 'FACT', UN_CLASE);
    
    MI_SQL_BASE :=
        'SELECT
            V_PLAN_CONTABLE.CLASECUENTA,
            DET.ANO,
            DET.TIPO_CPTE,
            DET.COMPROBANTE,
            DET.CONSECUTIVO,
            DET.NATURALEZA,
            DET.NRO_DOCUMENTO,
            DET.TERCERO,
            DET.SUCURSAL,
            DET.CENTRO_COSTO,
            DET.AUXILIAR,
            DET.CUENTA,
            DET.ID,
            CASE
                WHEN (DET.VALOR_DEBITO - NVL(SUM(DET.DEBITO_AFECTADO - DET.CREDITO_AFECTADO), 0)) < 0 THEN 0
                ELSE (DET.VALOR_DEBITO - NVL(SUM(DET.DEBITO_AFECTADO - DET.CREDITO_AFECTADO), 0))
            END AS VALOR_DEBITO,
            CASE
                WHEN (DET.VALOR_CREDITO - NVL(SUM(DET.CREDITO_AFECTADO - DET.DEBITO_AFECTADO), 0)) < 0 THEN 0
                ELSE (DET.VALOR_CREDITO - NVL(SUM(DET.CREDITO_AFECTADO - DET.DEBITO_AFECTADO), 0))
            END AS VALOR_CREDITO,
            DET.DEBITO_AFECTADO AS VALOR_DEBITO_AFECTADO,
            DET.CREDITO_AFECTADO AS VALOR_CREDITO_AFECTADO,
            DET.EQUIV_SIGEC,
            V_PLAN_CONTABLE.CUENTA_PPTAL,
            DET.VALOR_IVA
        FROM
            V_PLAN_CONTABLE
            INNER JOIN DETALLE_COMPROBANTE_CNT DET
                ON DET.COMPANIA = V_PLAN_CONTABLE.COMPANIA
                AND DET.ANO = V_PLAN_CONTABLE.ANO
                AND DET.ID = V_PLAN_CONTABLE.ID ';
   
   MI_CONSULTA1 := MI_SQL_BASE || MI_CONDICION_BASE || MI_FILTRO1 ||
        ' GROUP BY
            V_PLAN_CONTABLE.CLASECUENTA,
            DET.ANO,
            DET.TIPO_CPTE,
            DET.COMPROBANTE,
            DET.CONSECUTIVO,
            DET.NATURALEZA,
            DET.NRO_DOCUMENTO,
            DET.TERCERO,
            DET.SUCURSAL,
            DET.CENTRO_COSTO,
            DET.AUXILIAR,
            DET.CUENTA,
            DET.ID,
            DET.VALOR_DEBITO,
            DET.VALOR_CREDITO,
            DET.DEBITO_AFECTADO,
            DET.CREDITO_AFECTADO,
            DET.EQUIV_SIGEC,
            V_PLAN_CONTABLE.CUENTA_PPTAL,
            DET.VALOR_IVA
            ORDER BY TIPO_CPTE, COMPROBANTE,CONSECUTIVO';

    MI_CONSULTA2 := MI_SQL_BASE || MI_CONDICION_BASE || MI_FILTRO2 ||
        ' GROUP BY
            V_PLAN_CONTABLE.CLASECUENTA,
            DET.ANO,
            DET.TIPO_CPTE,
            DET.COMPROBANTE,
            DET.CONSECUTIVO,
            DET.NATURALEZA,
            DET.NRO_DOCUMENTO,
            DET.TERCERO,
            DET.SUCURSAL,
            DET.CENTRO_COSTO,
            DET.AUXILIAR,
            DET.CUENTA,
            DET.ID,
            DET.VALOR_DEBITO,
            DET.VALOR_CREDITO,
            DET.DEBITO_AFECTADO,
            DET.CREDITO_AFECTADO,
            DET.EQUIV_SIGEC,
            V_PLAN_CONTABLE.CUENTA_PPTAL,
            DET.VALOR_IVA
            ORDER BY TIPO_CPTE, COMPROBANTE, CONSECUTIVO';
   MI_VALOR_FALTANTE := UN_VALOR;
    <<datos_comprobante_afectar>>
    OPEN RSDATOS FOR MI_CONSULTA1;
    LOOP
    FETCH RSDATOS INTO MI_CLASECUENTA, MI_ANIO, MI_TIPO_CPTE, MI_COMPROBANTE, MI_CONSECUTIVO, MI_NATURALEZA, MI_NRO_DOCUMENTO, MI_TERCERO, MI_SUCURSAL, MI_CENTRO_COSTO, MI_AUXILIAR, MI_CUENTA, MI_ID, MI_VALOR_DEBITO, MI_VALOR_CREDITO, MI_VALOR_DEBITO_AFECTADO, MI_VALOR_CREDITO_AFECTADO, MI_EQUIV_SIGEC, MI_CUENTA_PPTAL, MI_VALOR_IVA;
    EXIT WHEN RSDATOS%NOTFOUND;
    DECLARE
      V_KEY VARCHAR2(100);
    BEGIN
    
      V_KEY := MI_ANIO || '-' || MI_TIPO_CPTE || '-' || MI_COMPROBANTE;

      IF NOT V_SALDOS.EXISTS(V_KEY) THEN
         V_SALDOS(V_KEY) := FC_OBT_VALOR_ANT(MI_ANIO, MI_TIPO_CPTE, MI_COMPROBANTE, MI_DATOS_FACT);
      END IF;
   MI_OTRADESCRIPCION := 'Cruce anticipo '|| REGEXP_SUBSTR(V_LISTAANTICIPOS_ORIG, '''([^'']*)''', 1, 2, NULL, 1)|| ' - ' || REGEXP_SUBSTR(V_LISTAANTICIPOS_ORIG, '''([^'']*)''', 1, 3, NULL, 1)
        ||' con cuenta por pagar o cobrar '|| REGEXP_SUBSTR(V_LISTAFACTURAS_ORIG, '''([^'']*)''', 1, 2, NULL, 1)|| ' - ' || REGEXP_SUBSTR(V_LISTAFACTURAS_ORIG, '''([^'']*)''', 1, 3, NULL, 1);
   MI_DESCRIPCION_AFECT := MI_DESCRIPCION_AFECT || ' '|| MI_TIPO ||' '|| MI_NUMERO ||' '|| MI_DESCRIPCION || CHR(13)||CHR(10);
      MI_VALOR_FALTANTE := V_SALDOS(V_KEY);
         IF MI_VALOR_DEBITO > 0 THEN
            MI_VALOR_FINAL := LEAST(MI_VALOR_DEBITO, MI_VALOR_FALTANTE);
            DBMS_OUTPUT.PUT_LINE('Tomar ' || MI_VALOR_FINAL || ' del débito.');
        ELSIF MI_VALOR_CREDITO > 0 THEN
            MI_VALOR_FINAL := LEAST(MI_VALOR_CREDITO, MI_VALOR_FALTANTE);
            DBMS_OUTPUT.PUT_LINE('Tomar ' || MI_VALOR_FINAL || ' del crédito.');
        ELSE
            MI_VALOR_FINAL := 0;
        END IF;
        
        IF UN_CLASE = 'V' THEN
            MI_VALOR_DEBITO  := 0;
            MI_VALOR_CREDITO := MI_VALOR_FINAL;
        ELSE
            MI_VALOR_DEBITO  := MI_VALOR_FINAL;
            MI_VALOR_CREDITO := 0;
        END IF;
        IF MI_VALOR_FINAL > 0 THEN
    BEGIN
           MI_CAMPOS := 'COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CONSECUTIVO,CUENTA,
                        FECHA,NATURALEZA,DESCRIPCION,VALOR_DEBITO,VALOR_CREDITO,
                        EJECUCION_DEBITO,BASE_GRAVABLE,CENTRO_COSTO,TERCERO,SUCURSAL,
                        AUXILIAR,ANO_AFECT,TIPO_CPTE_AFECT,CMPTE_AFECTADO,CONSECUTIVOAFECTADO,CREATED_BY,DATE_CREATED';
           MI_VALORES := '''' || UN_COMPANIA || '''
                          ,' || UN_ANO || '
                          ,''' || UN_TIPO_CPTE || '''
                          ,' || UN_COMPROBANTE || '
                          ,' || MI_CONSECUTIVODET || '
                          ,''' || MI_CUENTA || '''
                          ,TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                          ,''D''
                          ,''' || MI_OTRADESCRIPCION || '''
                          ,' || MI_VALOR_DEBITO || '
                          ,' || MI_VALOR_CREDITO || '
                          ,0
                          ,0
                          ,''' || MI_CENTRO_COSTO || '''
                          ,''' || UN_TERCERO || '''
                          ,''' || UN_SUCURSAL || '''
                          ,''' || MI_AUXILIAR || '''
                          ,' || MI_ANIO || '
                          ,''' || MI_TIPO_CPTE || '''
                          ,' || MI_COMPROBANTE || '
                          ,' || MI_CONSECUTIVO || '
                          ,''' || UN_USUARIO || '''
                          , SYSDATE';
                       MI_PCKDATOS := PCK_DATOS.FC_ACME(
                              UN_TABLA => 'DETALLE_COMPROBANTE_CNT',
                              UN_ACCION => 'I',
                              UN_CAMPOS => MI_CAMPOS,
                              UN_VALORES => MI_VALORES
                            );
                          EXCEPTION
                            WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                              PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD => SQLCODE,
                                UN_TABLAERROR => 'DETALLE_COMPROBANTE_CNT',
                                UN_ERROR_COD => PCK_ERRORES.ERR_CONTABILIDAD_INSERBANCOS
                              );
                          END;
                          BEGIN
            MI_TABLA := 'DETALLE_COMPROBANTE_CNT';
            MI_CAMPOS := 'DEBITO_AFECTADO = DEBITO_AFECTADO  + ' || CASE WHEN MI_VALOR_DEBITO <> 0 THEN MI_VALOR_DEBITO ELSE MI_VALOR_CREDITO END || ' ';
            MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA ||
                            ''' AND ANO = ' || MI_ANIO ||
                            ' AND TIPO_CPTE = ''' || MI_TIPO_CPTE || '''
                              AND COMPROBANTE = ''' || MI_COMPROBANTE ||
                            ''' AND CUENTA = ' || MI_CUENTA;
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOS,
                                                  UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
          END;
  V_SALDOS(V_KEY) := MI_VALOR_FALTANTE - MI_VALOR_FINAL;
  MI_CONSECUTIVODET:=MI_CONSECUTIVODET+1;
  END IF;
 END;
END LOOP datos_comprobante_afectar;
MI_VALOR_FALTANTE := UN_VALOR; 
<<procesar_contracuentas>>
        OPEN RSCONTRACUENTAS FOR MI_CONSULTA2;
    LOOP
        FETCH RSCONTRACUENTAS INTO MICONTRACUENTAS_CLASECUENTA , MICONTRACUENTAS_ANIO, MICONTRACUENTAS_TIPO_CPTE, MICONTRACUENTAS_COMPROBANTE, MICONTRACUENTAS_CONSECUTIVO,
              MICONTRACUENTAS_NATURALEZA ,MICONTRACUENTAS_NRO_DOCUMENTO , MICONTRACUENTAS_TERCERO,MICONTRACUENTAS_SUCURSAL,MICONTRACUENTAS_CENTRO_COSTO,MICONTRACUENTAS_AUXILIAR,
              MICONTRACUENTAS_CUENTA,MICONTRACUENTAS_ID,MICONTRACUENTAS_VALOR_DEBITO,MICONTRACUENTAS_VALOR_CREDITO,
              MICONTRA_DEBITO_AFECTADO,MICONTRA_CREDITO_AFECTADO,MIEQUIVSIGEC,MICONTRA_CUENTA_PPTAL,MI_VALOR_IVA;
        EXIT WHEN RSCONTRACUENTAS%NOTFOUND;
         DECLARE
         V_KEY VARCHAR2(100);
        BEGIN
        
         V_KEY := MICONTRACUENTAS_ANIO || '-' || MICONTRACUENTAS_TIPO_CPTE || '-' || MICONTRACUENTAS_COMPROBANTE;

      IF NOT V_SALDOS.EXISTS(V_KEY) THEN
         V_SALDOS(V_KEY) := FC_OBT_VALOR_ANT(MICONTRACUENTAS_ANIO, MICONTRACUENTAS_TIPO_CPTE, MICONTRACUENTAS_COMPROBANTE, MI_DATOS_ANT);
      END IF;
       MI_VALOR_FALTANTE := V_SALDOS(V_KEY);
        
        IF MICONTRACUENTAS_VALOR_DEBITO > 0 THEN
            MI_VALOR_FINAL := LEAST(MICONTRACUENTAS_VALOR_DEBITO, MI_VALOR_FALTANTE);
            DBMS_OUTPUT.PUT_LINE('Tomar ' || MI_VALOR_FINAL || ' del débito.');
        ELSIF MICONTRACUENTAS_VALOR_CREDITO > 0 THEN
            MI_VALOR_FINAL := LEAST(MICONTRACUENTAS_VALOR_CREDITO, MI_VALOR_FALTANTE);
            DBMS_OUTPUT.PUT_LINE('Tomar ' || MI_VALOR_FINAL || ' del crédito.');
        ELSE
            MI_VALOR_FINAL := 0;
        END IF;

        IF UN_CLASE = 'V' THEN
            MICONTRACUENTAS_VALOR_DEBITO  := MI_VALOR_FINAL;
            MICONTRACUENTAS_VALOR_CREDITO := 0;
        ELSE
            MICONTRACUENTAS_VALOR_DEBITO  := 0;
            MICONTRACUENTAS_VALOR_CREDITO := MI_VALOR_FINAL;
        END IF;
        IF MI_VALOR_FINAL > 0 THEN
              BEGIN
                  MI_CAMPOS := 'COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CONSECUTIVO,CUENTA,
                                FECHA,NATURALEZA,NRO_DOCUMENTO,DESCRIPCION,VALOR_DEBITO,VALOR_CREDITO,
                                EJECUCION_DEBITO,EJECUCION_CREDITO,BASE_GRAVABLE,CENTRO_COSTO,TERCERO,SUCURSAL,
                                AUXILIAR,ANO_AFECT,TIPO_CPTE_AFECT,CMPTE_AFECTADO,CONSECUTIVOAFECTADO,CUENTAPPTAL,EQUIV_SIGEC,VALOR_IVA,CREATED_BY,DATE_CREATED';
                  MI_VALORES := '''' || UN_COMPANIA || '''
                                ,' || UN_ANO || '
                                ,''' || UN_TIPO_CPTE || '''
                                ,' || UN_COMPROBANTE || '
                                ,' || MI_CONSECUTIVODET || '
                                ,''' || MICONTRACUENTAS_CUENTA || '''
                                ,TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                                ,''' ||  MICONTRACUENTAS_NATURALEZA || '''
                                ,'''||MICONTRACUENTAS_NRO_DOCUMENTO||'''
                                ,''' || MI_OTRADESCRIPCION || '''
                                ,' || MICONTRACUENTAS_VALOR_DEBITO ||'
                                ,' || MICONTRACUENTAS_VALOR_CREDITO ||'
                                ,0
                                ,0
                                ,0
                                ,''' || MICONTRACUENTAS_CENTRO_COSTO || '''
                                ,''' || UN_TERCERO || '''
                                ,''' || UN_SUCURSAL || '''
                                ,''' || MICONTRACUENTAS_AUXILIAR || '''
                                ,' || MICONTRACUENTAS_ANIO || '
                                ,''' || MICONTRACUENTAS_TIPO_CPTE || '''
                                ,' || MICONTRACUENTAS_COMPROBANTE || '
                                ,' || MICONTRACUENTAS_CONSECUTIVO || '
                                ,''' || MICONTRA_CUENTA_PPTAL || '''
                                ,''' || MIEQUIVSIGEC || '''
                                ,'|| MI_VALOR_IVA ||'
                                ,''' || UN_USUARIO || '''
                                , SYSDATE';
                          MI_PCKDATOS := PCK_DATOS.FC_ACME(
                              UN_TABLA => 'DETALLE_COMPROBANTE_CNT',
                              UN_ACCION => 'I',
                              UN_CAMPOS => MI_CAMPOS,
                              UN_VALORES => MI_VALORES
                            );
                          EXCEPTION
                            WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                              PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD => SQLCODE,
                                UN_TABLAERROR => 'DETALLE_COMPROBANTE_CNT',
                                UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_CREARDETPPTALVARIOS
                              );
                          END;
    
          BEGIN
            MI_TABLA := 'DETALLE_COMPROBANTE_CNT';
            MI_CAMPOS := 'DEBITO_AFECTADO = DEBITO_AFECTADO  + ' || CASE WHEN MICONTRACUENTAS_VALOR_DEBITO <> 0 THEN MICONTRACUENTAS_VALOR_DEBITO ELSE MICONTRACUENTAS_VALOR_CREDITO END || ' ';
            MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA ||
                            ''' AND ANO = ' || MICONTRACUENTAS_ANIO ||
                            ' AND TIPO_CPTE = ''' || MICONTRACUENTAS_TIPO_CPTE || '''
                              AND COMPROBANTE = ''' || MICONTRACUENTAS_COMPROBANTE ||''' 
                              AND CONSECUTIVO = ''' || MICONTRACUENTAS_CONSECUTIVO || '''
                              AND CUENTA = ' || MICONTRACUENTAS_CUENTA;
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOS,
                                                  UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
          END;
      V_SALDOS(V_KEY) := MI_VALOR_FALTANTE - MI_VALOR_FINAL;
      MI_CONSECUTIVODET := MI_CONSECUTIVODET + 1;
      END IF;
    END;
    END LOOP procesar_contracuentas;

    BEGIN
     MI_TABLA := 'COMPROBANTE_CNT';
     MI_CAMPOS := 'VLRAGIRAR = '|| UN_VALOR || ',DESCRIPCION = '''|| MI_OTRADESCRIPCION ||''',TEXTO='''|| MI_TEXTO ||''' ';
     MI_CONDICION := '      COMPANIA    = '''||UN_COMPANIA||
                     ''' AND ANO         = '||UN_ANO||
                      '  AND TIPO        = '''||UN_TIPO_CPTE||
                     ''' AND NUMERO      = '||UN_COMPROBANTE;
     PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
    END;

END PR_GENERARANTICIPO;

FUNCTION FC_OBT_VALOR_ANT(
   UN_ANO            IN VARCHAR2,
   UN_TIPO           IN VARCHAR2,
   UN_NUMERO         IN VARCHAR2,
   UN_LISTA_FACTURAS IN PCK_SYSMAN_UTL.T_SPLIT
) RETURN NUMBER IS
   MI_VAL     NUMBER := 0;
   MI_CADENA  VARCHAR2(4000);
   MI_ANO     VARCHAR2(10);
   MI_TIPO    VARCHAR2(10);
   MI_NUMERO  VARCHAR2(20);
   MI_IMPORTE NUMBER;
BEGIN
   IF UN_LISTA_FACTURAS.COUNT > 0 THEN
      FOR i IN UN_LISTA_FACTURAS.FIRST .. UN_LISTA_FACTURAS.LAST LOOP
         MI_CADENA := UN_LISTA_FACTURAS(i);  
         
         MI_ANO     := REGEXP_SUBSTR(MI_CADENA, '''([^'']*)''', 1, 1, NULL, 1);
         MI_TIPO    := REGEXP_SUBSTR(MI_CADENA, '''([^'']*)''', 1, 2, NULL, 1);
         MI_NUMERO  := REGEXP_SUBSTR(MI_CADENA, '''([^'']*)''', 1, 3, NULL, 1);
         MI_IMPORTE := TO_NUMBER(REGEXP_SUBSTR(MI_CADENA, '''([^'']*)''', 1, 4, NULL, 1));

         -- Validar coincidencia
         IF MI_ANO = UN_ANO AND MI_TIPO = UN_TIPO AND MI_NUMERO = UN_NUMERO THEN
            MI_VAL := NVL(MI_IMPORTE, 0);
            EXIT;
         END IF;
      END LOOP;
   END IF;

   RETURN MI_VAL;
EXCEPTION
   WHEN OTHERS THEN
      RETURN 0;
END FC_OBT_VALOR_ANT;

PROCEDURE PR_VALIDAR_COMPROBANTES(
  UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_DATOS    IN PCK_SYSMAN_UTL.T_SPLIT,
  UN_TIPO     IN VARCHAR2,
  UN_CLASE    IN VARCHAR2
) AS
 MI_MSGERROR PCK_SUBTIPOS.TI_CLAVEVALOR;
 MI_CADENA   VARCHAR2(4000);
 MI_ANO      VARCHAR2(4);
 MI_TIPO     VARCHAR2(10);
 MI_NUMERO   VARCHAR2(30);
 MI_SQL      PCK_SUBTIPOS.TI_STRSQL;
 MI_RTA      NUMBER;
 MI_CLASE    VARCHAR2(100);
 MI_RETORNO  VARCHAR2(4000);
BEGIN
    FOR i IN UN_DATOS.FIRST..UN_DATOS.LAST LOOP
        MI_CADENA := UN_DATOS(i);
        MI_ANO     := REGEXP_SUBSTR(MI_CADENA, '''([^'']*)''', 1, 1, NULL, 1);
        MI_TIPO    := REGEXP_SUBSTR(MI_CADENA, '''([^'']*)''', 1, 2, NULL, 1);
        MI_NUMERO  := REGEXP_SUBSTR(MI_CADENA, '''([^'']*)''', 1, 3, NULL, 1);
    
        MI_SQL := '
            SELECT COUNT(*)
              FROM V_PLAN_CONTABLE
              INNER JOIN DETALLE_COMPROBANTE_CNT DET
                ON DET.COMPANIA = V_PLAN_CONTABLE.COMPANIA
               AND DET.ANO = V_PLAN_CONTABLE.ANO
               AND DET.ID  = V_PLAN_CONTABLE.ID
             WHERE DET.COMPANIA = ''' || UN_COMPANIA || '''
               AND DET.ANO = ''' || MI_ANO || '''
               AND DET.TIPO_CPTE = ''' || MI_TIPO || '''
               AND DET.COMPROBANTE = ''' || MI_NUMERO || '''';
    
        IF UN_TIPO = 'ANT' THEN
            IF UN_CLASE = 'P' THEN
                MI_SQL := MI_SQL ||
                    ' AND V_PLAN_CONTABLE.CLASECUENTA IN (''C'',''A'')
                      AND DET.VALOR_DEBITO <> 0';
                MI_CLASE := 'ACTIVOS - CUENTAS POR COBRAR';
            ELSIF UN_CLASE = 'V' THEN
                MI_SQL := MI_SQL ||
                    ' AND V_PLAN_CONTABLE.CLASECUENTA IN (''P'',''E'')
                      AND DET.VALOR_CREDITO <> 0';
                MI_CLASE := 'PASIVO GENERAL - CUENTAS POR PAGAR';
            END IF;
        ELSE
            IF UN_CLASE = 'P' THEN
                MI_SQL := MI_SQL ||
                    ' AND V_PLAN_CONTABLE.CLASECUENTA IN (''P'',''E'')
                      AND DET.VALOR_CREDITO <> 0';
                MI_CLASE := 'PASIVO GENERAL - CUENTAS POR PAGAR';
            ELSIF UN_CLASE = 'V' THEN
                MI_SQL := MI_SQL ||
                    ' AND V_PLAN_CONTABLE.CLASECUENTA IN (''C'',''A'')
                      AND DET.VALOR_DEBITO <> 0';
                MI_CLASE := 'ACTIVOS - CUENTAS POR COBRAR';
            END IF;
        END IF;
    
        BEGIN
            EXECUTE IMMEDIATE MI_SQL INTO MI_RTA;
        END;
    
        IF MI_RTA = 0 THEN
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            EXCEPTION
                WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_RETORNO :=
                        'No se encontraron registros para el comprobante ' || MI_NUMERO ||
                        ' del tipo ' || MI_TIPO || ' en el año ' || MI_ANO ||
                        '. Es posible que el comprobante no tenga información registrada ' ||
                        'o que las cuentas asociadas no estén configuradas correctamente con una clase contable: ' || MI_CLASE;
                    MI_MSGERROR(1).CLAVE := 'MENSAJE';
                    MI_MSGERROR(1).VALOR := MI_RETORNO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE,
                        UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                        UN_REEMPLAZOS => MI_MSGERROR
                    );
            END;
        END IF;
    END LOOP;
END PR_VALIDAR_COMPROBANTES;

PROCEDURE PR_GENERAR_ADC   
/*
    NAME              : PR_GENERAR_ADC
    AUTHORS           :
    AUTHOR            :
    DATE MIGRADOR     :
    TIME              :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    MODIFICATIONS     :
    --NAME:  generarADC
    --METHOD:  put
  */
(
    UN_COMPANIA                     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO                         IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPO                         IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
    UN_NUMERO                       IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
    UN_FECHA                        IN DATE,
    UN_CLASE                        IN VARCHAR2,
    UN_LISTAAAFECTAR                IN PCK_SUBTIPOS.TI_STRSQL,
    UN_USUARIO                      IN VARCHAR2
    ) AS
    MI_STRAFECTAR           VARCHAR2(1000);
    MI_DESCRIPCION          VARCHAR2(2000);
    MI_DESCRI               VARCHAR2(2000);
    MI_CONSULTA             PCK_SUBTIPOS.TI_STRSQL;
    RSDATOS                 SYS_REFCURSOR;
    RSCLCUENTA              SYS_REFCURSOR;
    MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_RETORNO              VARCHAR2(4000);
    MI_SQLCLCUENTA          PCK_SUBTIPOS.TI_STRSQL;
    MI_ANIO                 PCK_SUBTIPOS.TI_ANIO;
    MI_TIPO                 PCK_SUBTIPOS.TI_TIPOCOMPROBANTE;
    MI_NUMERO               PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT;
    MI_CUENTA               PCK_SUBTIPOS.TI_CODIGOCONTA;
    MI_CENTRO_COSTO         PCK_SUBTIPOS.TI_CENTRO_COSTO;
    MI_AUXILIAR             PCK_SUBTIPOS.TI_AUXILIAR;
    MI_TERCERO              PCK_SUBTIPOS.TI_TERCERO;
    MI_SUCURSAL             PCK_SUBTIPOS.TI_SUCURSAL;
    MI_REFERENCIA           PCK_SUBTIPOS.TI_REFERENCIA;
    MI_FUENTE_RECURSOS      PCK_SUBTIPOS.TI_FUENTE_RECURSOS;
    MI_CONCEPTO_CUDS        PCK_SUBTIPOS.TI_CONCEPTO_CUDS;
    MI_VALOR_DEBITO         PCK_SUBTIPOS.TI_DOBLE;
    MI_VALOR_CREDITO        PCK_SUBTIPOS.TI_DOBLE;
    MI_VAL_DEBITO           PCK_SUBTIPOS.TI_DOBLE;
    MI_VAL_CREDITO          PCK_SUBTIPOS.TI_DOBLE;
    MI_DEBITO_AFECTADO      PCK_SUBTIPOS.TI_DOBLE;
    MI_CREDITO_AFECTADO     PCK_SUBTIPOS.TI_DOBLE;
    MI_BASE_GRAVABLE        PCK_SUBTIPOS.TI_DOBLE;
    MI_ABONOINICIAL         PCK_SUBTIPOS.TI_DOBLE;
    MI_CONSECUTIVO          PCK_SUBTIPOS.TI_ENTERO;
    MI_CLASECUENTA          VARCHAR2(10);
    MI_TEXTO                PCK_SUBTIPOS.TI_STRSQL;
    MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
    MI_PCKDATOS             PCK_SUBTIPOS.TI_RTA_ACME;
    MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
    MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
    MI_CONSECUTIVODET       PCK_SUBTIPOS.TI_ENTERO := 0;
    MI_MENSAJE              VARCHAR2(1000);
    MI_DEBITO_AFECTADOFAC   PCK_SUBTIPOS.TI_DOBLE;
    MI_PARCLCUENTA          VARCHAR2(1000 CHAR);
    MI_CLCUENTA             VARCHAR2(1000 CHAR);
    MI_CLCUENTA_G           VARCHAR2(1000 CHAR);
    MI_REINTEGRO            PCK_SUBTIPOS.TI_ENTERO;
    MI_NATURALEZA           PCK_SUBTIPOS.TI_NATURALEZA;
    MI_PAIS                 DETALLE_COMPROBANTE_CNT.PAIS%TYPE;
    MI_DEPARTAMENTO         DETALLE_COMPROBANTE_CNT.DEPARTAMENTO%TYPE;
    MI_CIUDAD               DETALLE_COMPROBANTE_CNT.CIUDAD%TYPE;
    MI_COD_RECONOCIMIENTO   PLAN_CONTABLE.COD_RECONOCIMIENTO%TYPE;
BEGIN

MI_CONSULTA := 'SELECT DETALLE_COMPROBANTE_CNT.ANO,
                          DETALLE_COMPROBANTE_CNT.TIPO_CPTE,
                          DETALLE_COMPROBANTE_CNT.COMPROBANTE,
                          DETALLE_COMPROBANTE_CNT.CUENTA,
                          DETALLE_COMPROBANTE_CNT.NATURALEZA,
                          DETALLE_COMPROBANTE_CNT.CONSECUTIVO,
                          DETALLE_COMPROBANTE_CNT.TERCERO,
                          DETALLE_COMPROBANTE_CNT.SUCURSAL,
                          DETALLE_COMPROBANTE_CNT.CENTRO_COSTO,
                          DETALLE_COMPROBANTE_CNT.AUXILIAR,
                          DETALLE_COMPROBANTE_CNT.REFERENCIA,
                          DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO,
                          DETALLE_COMPROBANTE_CNT.CONCEPTO_CUDS,
                          DETALLE_COMPROBANTE_CNT.PAIS,
                          DETALLE_COMPROBANTE_CNT.DEPARTAMENTO,
                          DETALLE_COMPROBANTE_CNT.CIUDAD,
                          DETALLE_COMPROBANTE_CNT.VALOR_DEBITO,
                          DETALLE_COMPROBANTE_CNT.VALOR_CREDITO,
                          DETALLE_COMPROBANTE_CNT.DEBITO_AFECTADO,
                          DETALLE_COMPROBANTE_CNT.CREDITO_AFECTADO,
                          DETALLE_COMPROBANTE_CNT.ABONOINICIAL,
                          DETALLE_COMPROBANTE_CNT.BASE_GRAVABLE,
                          DETALLE_COMPROBANTE_CNT.DESCRIPCION,
                          PLAN_CONTABLE.CLASECUENTA,
                          PLAN_CONTABLE.COD_RECONOCIMIENTO
                   FROM DETALLE_COMPROBANTE_CNT
                   INNER JOIN PLAN_CONTABLE
                     ON DETALLE_COMPROBANTE_CNT.COMPANIA = PLAN_CONTABLE.COMPANIA
                    AND DETALLE_COMPROBANTE_CNT.ANO      = PLAN_CONTABLE.ANO
                    AND DETALLE_COMPROBANTE_CNT.CUENTA   = PLAN_CONTABLE.CODIGO
                   WHERE DETALLE_COMPROBANTE_CNT.COMPANIA = '''||UN_COMPANIA||'''
                     AND (DETALLE_COMPROBANTE_CNT.ANO, TIPO_CPTE, COMPROBANTE) IN (' || UN_LISTAAAFECTAR || ')';

<<PRINCIPAL>>
OPEN RSDATOS FOR MI_CONSULTA;
LOOP
    FETCH RSDATOS INTO MI_ANIO,   MI_TIPO,        MI_NUMERO,
                       MI_CUENTA, MI_NATURALEZA, MI_CONSECUTIVO, MI_TERCERO, MI_SUCURSAL,
                       MI_CENTRO_COSTO, MI_AUXILIAR, MI_REFERENCIA, MI_FUENTE_RECURSOS, MI_CONCEPTO_CUDS,
                       MI_PAIS, MI_DEPARTAMENTO, MI_CIUDAD, MI_VALOR_DEBITO, MI_VALOR_CREDITO,
                       MI_DEBITO_AFECTADO, MI_CREDITO_AFECTADO,
                       MI_ABONOINICIAL, MI_BASE_GRAVABLE, MI_DESCRI, MI_CLASECUENTA,
                       MI_COD_RECONOCIMIENTO;
    EXIT WHEN RSDATOS%NOTFOUND;

    BEGIN

       MI_STRAFECTAR := 'Firmeza Factura ';
       MI_DESCRIPCION      := MI_STRAFECTAR || MI_TIPO || ' - ' || MI_NUMERO ||', ' || MI_DESCRI;
       MI_CAMPOS := 'COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CONSECUTIVO,CUENTA,NATURALEZA,DESCRIPCION,
                     VALOR_DEBITO,VALOR_CREDITO,BASE_GRAVABLE,TERCERO,SUCURSAL,CENTRO_COSTO,AUXILIAR,
                     REFERENCIA,FUENTE_RECURSO, FECHA,EJECUCION_DEBITO,EJECUCION_CREDITO,ANO_AFECT,
                     TIPO_CPTE_AFECT,CMPTE_AFECTADO,CONSECUTIVOAFECTADO,CREATED_BY,DATE_CREATED';
        MI_CONSECUTIVODET := MI_CONSECUTIVODET + 1;

        MI_VALORES := '''' || UN_COMPANIA       || '''
                        ,' || UN_ANIO           || '
                      ,''' || UN_TIPO           || '''
                        ,' || UN_NUMERO         || '
                        ,' || MI_CONSECUTIVODET || '
                      ,''' || MI_CUENTA         || '''
                      ,''' || MI_NATURALEZA     || '''
                      ,''' || MI_DESCRIPCION    || '''
                        ,' || MI_VALOR_CREDITO    || '
                        ,' || MI_VALOR_DEBITO   || '
                        ,' || MI_BASE_GRAVABLE  || '
                      ,''' || MI_TERCERO        || '''
                      ,''' || MI_SUCURSAL       || '''
                      ,''' || MI_CENTRO_COSTO   || '''
                      ,''' || MI_AUXILIAR       || '''
                      ,''' || MI_REFERENCIA     || '''
                      ,''' || MI_FUENTE_RECURSOS|| '''
                      ,TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                        ,' || MI_VALOR_CREDITO    || '       -- EJECUCION_DEBITO
                        ,' || MI_VALOR_DEBITO     || '       -- EJECUCION_CREDITO
                      ,''' || MI_ANIO           || '''
                      ,''' || MI_TIPO           || '''
                      ,''' || MI_NUMERO         || '''
                      ,''' || MI_CONSECUTIVO    || '''
                      ,''' || UN_USUARIO        || '''
                            , SYSDATE';

        BEGIN
            MI_PCKDATOS := PCK_DATOS.FC_ACME(
                                UN_TABLA   => 'DETALLE_COMPROBANTE_CNT',
                                UN_ACCION  => 'I',
                                UN_CAMPOS  => MI_CAMPOS,
                                UN_VALORES => MI_VALORES);
        EXCEPTION
            WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD     => SQLCODE,
                    UN_TABLAERROR  => 'DETALLE_COMPROBANTE_CNT',
                    UN_ERROR_COD   => PCK_ERRORES.ERR_DETALLE_AFECTADO
                );
        END;

        IF MI_COD_RECONOCIMIENTO IS NOT NULL THEN

            MI_CONSECUTIVODET := MI_CONSECUTIVODET + 1;
            
            MI_CAMPOS := 'COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CONSECUTIVO,CUENTA,NATURALEZA,DESCRIPCION,
                     VALOR_DEBITO,VALOR_CREDITO,BASE_GRAVABLE,TERCERO,SUCURSAL,CENTRO_COSTO,AUXILIAR,
                     REFERENCIA,FUENTE_RECURSO, FECHA,EJECUCION_DEBITO,EJECUCION_CREDITO,CREATED_BY,DATE_CREATED';

            MI_VALORES := '''' || UN_COMPANIA           || '''
                            ,' || UN_ANIO               || '
                          ,''' || UN_TIPO               || '''
                            ,' || UN_NUMERO             || '
                            ,' || MI_CONSECUTIVODET     || '
                          ,''' || MI_COD_RECONOCIMIENTO || '''  -- CUENTA DE RECONOCIMIENTO
                          ,''' || MI_NATURALEZA         || '''
                          ,''' || MI_DESCRIPCION        || '''
                            ,' || MI_VALOR_DEBITO       || '
                            ,' || MI_VALOR_CREDITO        || '
                            ,' || MI_BASE_GRAVABLE      || '
                          ,''' || MI_TERCERO            || '''
                          ,''' || MI_SUCURSAL           || '''
                          ,''' || MI_CENTRO_COSTO       || '''
                          ,''' || MI_AUXILIAR           || '''
                          ,''' || MI_REFERENCIA         || '''
                          ,''' || MI_FUENTE_RECURSOS    || '''
                          ,TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                            ,' || MI_VALOR_DEBITO       || '       -- EJECUCION_DEBITO
                            ,' || MI_VALOR_CREDITO      || '       -- EJECUCION_CREDITO
                          ,''' || UN_USUARIO            || '''
                                , SYSDATE';

            BEGIN
                MI_PCKDATOS := PCK_DATOS.FC_ACME(
                                    UN_TABLA   => 'DETALLE_COMPROBANTE_CNT',
                                    UN_ACCION  => 'I',
                                    UN_CAMPOS  => MI_CAMPOS,
                                    UN_VALORES => MI_VALORES);
            EXCEPTION
                WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD     => SQLCODE,
                        UN_TABLAERROR  => 'DETALLE_COMPROBANTE_CNT',
                        UN_ERROR_COD   => PCK_ERRORES.ERR_DETALLE_AFECTADO
                    );
            END;
            
        ELSE 
        
         BEGIN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            EXCEPTION
                WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_RETORNO :='La cuenta ' || MI_CUENTA ||
                           ' no tiene configurado el Código Reconicimiento en el Plan Contable. ' ||
                           'Debe registrarlo para permitir la generación del comprobante.';
                    MI_MSGERROR(1).CLAVE := 'MENSAJE';
                    MI_MSGERROR(1).VALOR := MI_RETORNO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE,
                        UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                        UN_REEMPLAZOS => MI_MSGERROR
                    );
            END;
        
        END IF;

    END;

END LOOP;


      BEGIN
        MI_CAMPOS    := 'DESCRIPCION =''' || SUBSTR(MI_DESCRIPCION,1,250) || ''',
                     MODIFIED_BY =''' || UN_USUARIO || ''',
                     DATE_MODIFIED =SYSDATE';
        MI_CONDICION := 'COMPANIA        = ''' || UN_COMPANIA    ||
                     ''' AND ANO         = '   || UN_ANIO        ||
                       ' AND TIPO        = ''' || UN_TIPO        ||
                     ''' AND NUMERO      = '   || UN_NUMERO;
        MI_PCKDATOS   := PCK_DATOS.FC_ACME (UN_TABLA     => 'COMPROBANTE_CNT',
                                               UN_ACCION    => 'M',
                                               UN_CAMPOS    => MI_CAMPOS,
                                               UN_CONDICION => MI_CONDICION);
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE,
                                    UN_TABLAERROR => 'COMPROBANTE_CNT',
                                    UN_ERROR_COD => PCK_ERRORES.ERR_DETALLE_AFECTADO
        );
    END;
END PR_GENERAR_ADC;

FUNCTION FC_SALDO_AUX_CXP
   /*
    NAME              : FC_SALDO_AUX_CXP
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : GERMAN DAVID ROJAS
    DATE MIGRADOR     : 04/05/2026
    TIME              : 
    MODIFIED          : 
    DATE MODIFIED     : 
    TIME              : 
    --NAME:   validarSaldoAuxCxp
    --METHOD:  GET
    */
(
     UN_COMPANIA            IN  PCK_SUBTIPOS.TI_COMPANIA,
     UN_ANO                 IN  PCK_SUBTIPOS.TI_ANIO,
     UN_TIPO_CPTE           IN  PCK_SUBTIPOS.TI_CLASECOMPROPPTO,
     UN_COMPROBANTE         IN  PCK_SUBTIPOS.TI_NUMEROCOMPROBANTEPPTAL,
     UN_REFERENCIA          IN  PCK_SUBTIPOS.TI_REFERENCIA,
     UN_CENTRO_COSTO        IN  PCK_SUBTIPOS.TI_CENTRO_COSTO,
     UN_LISTANUMEROAFECTAR  IN  PCK_SUBTIPOS.TI_STRSQL
)
RETURN NUMBER AS
    MI_VALORORDEN       PCK_SUBTIPOS.TI_DOBLE:=0;
    MI_VALORBANCO       PCK_SUBTIPOS.TI_DOBLE:=0;
    MI_EXISTE           PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_RETORNO          PCK_SUBTIPOS.TI_DOBLE:=0;
    MI_REFERENCIAS      VARCHAR2(2000 CHAR):= '';
    MI_CENTROSCOSTO     VARCHAR2(2000 CHAR):= '';
    MI_CONSULTA         PCK_SUBTIPOS.TI_STRSQL;
    
BEGIN
    /*Valor total de las cuentas banco por referencia y centro de costo*/
    BEGIN
        SELECT NVL(SUM(VALOR),0) VALOR_TOTAL
          INTO MI_VALORBANCO
          FROM COMPROBANTE_CNTBANCOS
         WHERE COMPANIA = UN_COMPANIA
           AND ANO = UN_ANO
           AND TIPO = UN_TIPO_CPTE
           AND NUMERO = UN_COMPROBANTE
           AND REFERENCIA = UN_REFERENCIA
           AND CENTRO_COSTO = UN_CENTRO_COSTO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_VALORBANCO := 0;
    END;
    
    /*Valor total de la orden de pago por referencia y centro de costo*/
    BEGIN
        MI_CONSULTA := '
                SELECT NVL(SUM((VALOR_CREDITO - VALOR_DEBITO ) - (DEBITO_AFECTADO + CREDITO_AFECTADO)),0) valor 
                  FROM DETALLE_COMPROBANTE_CNT
                 INNER JOIN PLAN_CONTABLE
                    ON DETALLE_COMPROBANTE_CNT.COMPANIA = PLAN_CONTABLE.COMPANIA
                   AND DETALLE_COMPROBANTE_CNT.ANO = PLAN_CONTABLE.ANO
                   AND DETALLE_COMPROBANTE_CNT.CUENTA = PLAN_CONTABLE.CODIGO
                 WHERE DETALLE_COMPROBANTE_CNT.COMPANIA='''||UN_COMPANIA||'''
                   AND DETALLE_COMPROBANTE_CNT.REFERENCIA = '''||UN_REFERENCIA||'''
                   AND DETALLE_COMPROBANTE_CNT.CENTRO_COSTO = '''||UN_CENTRO_COSTO||''' 
                   AND PLAN_CONTABLE.CLASECUENTA = ''P''
                   AND (DETALLE_COMPROBANTE_CNT.ANO, DETALLE_COMPROBANTE_CNT.TIPO_CPTE, DETALLE_COMPROBANTE_CNT.COMPROBANTE) IN ('||UN_LISTANUMEROAFECTAR||')';
        EXECUTE IMMEDIATE MI_CONSULTA INTO MI_VALORORDEN;
    EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_VALORORDEN:=0;
    END;
    
    IF (MI_VALORORDEN - MI_VALORBANCO) < 0 THEN
        MI_RETORNO := 0;
    ELSE
        MI_RETORNO := MI_VALORORDEN - MI_VALORBANCO;
    END IF;

    RETURN  MI_RETORNO;
END FC_SALDO_AUX_CXP;

END PCK_CONTABILIDAD6;