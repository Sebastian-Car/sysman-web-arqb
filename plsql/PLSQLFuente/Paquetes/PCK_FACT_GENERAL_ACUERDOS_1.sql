create or replace PACKAGE BODY PCK_FACT_GENERAL_ACUERDOS AS

  --1
  FUNCTION FC_PREPARARDEUDA_ACUERDOSPAGO 
  /*
    NAME              : PR_PREPARARDEUDA_ACUERDOSPAGO -> jEn Access: FRM_ACUERDO_PAGO.CmdPrepararDeuda_Click
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : PABLO ANDRES ESPITIA CUCA
    DATE MIGRATION    : 14/11/2017
    TIME              : 03:17 PM
    SOURCE MODULE     : FACTURACION GENERAL (69) - SysmanSF2017.11.01.accdb
    DESCRIPTION       : Prepara la deuda respecto a las facturas seleccionadas y retorna las deudas separadas por punto y coma.
    MODIFIED BY       : 
    RETURN            : DEUDACAPITAL;DEUDAINTERES;DEUDATOTAL

    @NAME  : prepararDeudaAcuerdosPago
    @METHOD: GET
  */
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA, --Codigo de la compania desde la que se inicio sesion.
    UN_ACUERDONRO   IN VARCHAR2,
    UN_SELECCION    IN VARCHAR2,                 --Codigos de facturas seleccionadas, separadas por coma.
    UN_TIPOCOBRO    IN SF_FACTURA.TIPOCOBRO%TYPE --Tipo de cobro seleccionado al ingresar al modulo.
  )
  RETURN VARCHAR2
  AS 
    MI_CAPITAL    PCK_SUBTIPOS.TI_DOBLE;
    MI_INTERES    PCK_SUBTIPOS.TI_DOBLE;
    MI_TOTAL      PCK_SUBTIPOS.TI_DOBLE;
    MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_FACTURAS   VARCHAR2(4000 CHAR);
    MI_RTA        PCK_SUBTIPOS.TI_RTA_ACME;
    MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION;
  BEGIN
    PR_ELIMINARACUERDOPAGO(UN_COMPANIA   => UN_COMPANIA
                          ,UN_TIPOCOBRO  => UN_TIPOCOBRO
                          ,UN_ACUERDONRO => UN_ACUERDONRO);

    IF UN_SELECCION IS NULL THEN
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE
                                  ,UN_ERROR_COD => PCK_ERRORES.ERR_FG_MSG_PR_PREDEU_NOSELFACT);
      END;
    END IF;

    --Facturas entrecomilladas y separadas por coma
    MI_FACTURAS := PCK_SYSMAN_UTL.FC_COLOCARCOMILLAS(UN_SELECCION);

    BEGIN
      BEGIN
        SELECT    
          SUM(CASE WHEN CONCEPTO.TIPODECONCEPTO IN('C')
                   THEN (DETALLE.VALOR_BASE       + 
                         DETALLE.VALOR_IVA        -
                         DETALLE.VALOR_RETEFUENTE -
                         DETALLE.VALOR_DESCUENTO  +
                         DETALLE.VALOR_ICA)
                   ELSE 0
              END
             )   
         ,SUM(CASE WHEN CONCEPTO.TIPODECONCEPTO IN('I')
                   THEN (DETALLE.VALOR_BASE       + 
                         DETALLE.VALOR_IVA        -
                         DETALLE.VALOR_RETEFUENTE -
                         DETALLE.VALOR_DESCUENTO  +
                         DETALLE.VALOR_ICA)
                   ELSE 0
              END
             ) 
         ,SUM(DETALLE.VALOR_NETO)  
        INTO
          MI_CAPITAL
         ,MI_INTERES
         ,MI_TOTAL
        FROM SF_DETALLE_FACTURA DETALLE
          INNER JOIN SF_CONCEPTOS CONCEPTO
             ON DETALLE.COMPANIA  = CONCEPTO.COMPANIA
            AND DETALLE.ANO       = CONCEPTO.ANO
            AND DETALLE.TIPOCOBRO = CONCEPTO.TIPOCOBRO
            AND DETALLE.CONCEPTO  = CONCEPTO.CODIGO
        WHERE DETALLE.COMPANIA     = UN_COMPANIA
          AND DETALLE.TIPO_FACTURA = UN_TIPOCOBRO  
          AND INSTR(MI_FACTURAS, PCK_SYSMAN_UTL.FC_COLOCARCOMILLAS(FACTURA), 1) > 0;

      EXCEPTION WHEN NO_DATA_FOUND THEN
        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
      MI_REEMPLAZOS(1).CLAVE := 'TIPO';
      MI_REEMPLAZOS(1).VALOR := UN_TIPOCOBRO;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_FG_NDF_PR_PREDEU_VALORTOTS
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;

    IF MI_TOTAL <> (MI_CAPITAL + MI_INTERES) THEN
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
        MI_REEMPLAZOS(1).CLAVE := 'TOTAL';
        MI_REEMPLAZOS(1).VALOR := MI_TOTAL;
        MI_REEMPLAZOS(2).CLAVE := 'CAPITAL';
        MI_REEMPLAZOS(2).VALOR := MI_CAPITAL;
        MI_REEMPLAZOS(3).CLAVE := 'INTERES';
        MI_REEMPLAZOS(3).VALOR := MI_INTERES;      

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_FG_MSG_PR_PREDEU_SUMTOCAIN
                                  ,UN_REEMPLAZOS => MI_REEMPLAZOS);      
      END;
    END IF;

    RETURN NVL(MI_CAPITAL,0) ||
      ';'||NVL(MI_INTERES,0) ||
      ';'||NVL(MI_TOTAL  ,0);

  END FC_PREPARARDEUDA_ACUERDOSPAGO;

  --2
  PROCEDURE PR_APLICARCONDONACION
  /*
    NAME              : PR_APLICARCONDONACION -> FRM_ACUERDO_PAGO.AplicarCondonacion()
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : PABLO ANDRES ESPITIA CUCA
    DATE MIGRATION    : 21/11/2017
    TIME              : 02:05 pm
    SOURCE MODULE     : FACTURACIÓN GENERAL (69) -> SysmanSF2017.11.01.accdb
    DESCRIPTION       : Verifica que existan conceptos de interes para aplicar la condonacion. 
    MODIFIED BY       : 

    @NAME  : aplicarCondonacion
  */
  (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,            --> Codigo de la compania.
    UN_TIPOCOBRO      IN SF_DETALLE_FACTURA.TIPO_FACTURA%TYPE,--> Codigo tipo de cobro.
    UN_VALORCONDONADO IN PCK_SUBTIPOS.TI_DOBLE,               --> Valor condonado.
    UN_SELECCION_FACT IN VARCHAR2                             --> Numeros de factura separados por coma.
  )
  AS
    MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_COUNT      PCK_SUBTIPOS.TI_ENTERO;
    MI_ERROR_COD  PLS_INTEGER;    
  BEGIN
    SELECT 
      COUNT(1)
    INTO 
      MI_COUNT  
    FROM SF_DETALLE_FACTURA DETALLE
      INNER JOIN SF_CONCEPTOS CONCEPTO
         ON DETALLE.COMPANIA  = CONCEPTO.COMPANIA
        AND DETALLE.ANO       = CONCEPTO.ANO
        AND DETALLE.TIPOCOBRO = CONCEPTO.TIPOCOBRO
        AND DETALLE.CONCEPTO  = CONCEPTO.CODIGO
    WHERE DETALLE.COMPANIA     = UN_COMPANIA
      AND DETALLE.TIPO_FACTURA = UN_TIPOCOBRO
      AND CONCEPTO.TIPODECONCEPTO IN('I')
      AND INSTR(PCK_SYSMAN_UTL.FC_COLOCARCOMILLAS(UN_SELECCION_FACT)
               ,PCK_SYSMAN_UTL.FC_COLOCARCOMILLAS(DETALLE.FACTURA  )
               ,1) > 0;            

    IF MI_COUNT IN(0) THEN
      MI_ERROR_COD := PCK_ERRORES.ERR_FG_NDF_PR_APLCON_DETALLESF;

      MI_REEMPLAZOS(1).CLAVE := 'FACTURAS';
      MI_REEMPLAZOS(1).VALOR := UN_SELECCION_FACT;
    ELSIF UN_VALORCONDONADO > 0 THEN
      MI_ERROR_COD := PCK_ERRORES.ERR_FG_MSG_PR_APLCON_VCONDONAD;

      MI_REEMPLAZOS(1).CLAVE := 'VALORCONDONADO';
      MI_REEMPLAZOS(1).VALOR := UN_VALORCONDONADO;
    END IF;  

    BEGIN
      RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;  

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => MI_ERROR_COD
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);  
    END;
  END PR_APLICARCONDONACION;   

  --3
  PROCEDURE PR_ELIMINARACUERDOPAGO 
  /*
    NAME              : PR_ELIMINARACUERDOPAGO -> FRM_ACUERDO_PAGO.CmdAcuerdo_Click, FRM_ACUERDO_PAGO.CmdPrepararDeuda_Click
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : PABLO ANDRES ESPITIA CUCA
    DATE MIGRATION    : 22/11/2017
    TIME              : 09:20 AM
    SOURCE MODULE     : FACTURACIÓN GENERAL (69) -> SysmanSF2017.11.01.accdb
    DESCRIPTION       : Remueve los detalles de cuotas, los detalles de acuerdos y el acuerdo de pago.
    MODIFIED BY       : 

    @NAME  : eliminarAcuerdoPago
    @METHOD: DELETE
  */
  (
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,        --> Codigo de la compania.
    UN_TIPOCOBRO  IN SF_TEMP_ACUERDO_PAGO.TIPO%TYPE,  --> Tipo de cobro.
    UN_ACUERDONRO IN SF_TEMP_ACUERDO_PAGO.CODIGO%TYPE --> Numero de acuerdo.
  )
  AS 
    MI_RTA        PCK_SUBTIPOS.TI_RTA_ACME;
    MI_TABLA_TDC  PCK_SUBTIPOS.TI_TABLA DEFAULT 'SF_TEMP_DETALLE_CUOTA';
    MI_TABLA_TDA  PCK_SUBTIPOS.TI_TABLA DEFAULT 'SF_TEMP_DETALLE_ACUERDO';
    MI_TABLA_TAP  PCK_SUBTIPOS.TI_TABLA DEFAULT 'SF_TEMP_ACUERDO_PAGO';
    MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION;  
    MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    IF UN_ACUERDONRO IS NOT NULL THEN
      MI_CONDICION := 'COMPANIA  = '''||UN_COMPANIA  ||'''
                   AND TIPO_AP   = '''||UN_TIPOCOBRO ||'''
                   AND CODIGO_AP =   '||UN_ACUERDONRO;

      BEGIN
        BEGIN
          MI_RTA := PCK_DATOS.FC_ACME(UN_ACCION    => 'E'
                                     ,UN_TABLA     => MI_TABLA_TDC
                                     ,UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
          RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
        MI_REEMPLAZOS(1).CLAVE := 'TIPO';
        MI_REEMPLAZOS(1).VALOR := UN_TIPOCOBRO;
        MI_REEMPLAZOS(2).CLAVE := 'CODIGO';
        MI_REEMPLAZOS(2).VALOR := UN_ACUERDONRO;

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_FG_E_FC_PREDEU_DETCUOTAS
                                  ,UN_TABLAERROR => MI_TABLA_TDC
                                  ,UN_REEMPLAZOS => MI_REEMPLAZOS);        
      END;

      BEGIN
        BEGIN
          MI_RTA := PCK_DATOS.FC_ACME(UN_ACCION    => 'E'
                                     ,UN_TABLA     => MI_TABLA_TDA
                                     ,UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
          RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        END;      

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
        MI_REEMPLAZOS(1).CLAVE := 'TIPO';
        MI_REEMPLAZOS(1).VALOR := UN_TIPOCOBRO;
        MI_REEMPLAZOS(2).CLAVE := 'CODIGO';
        MI_REEMPLAZOS(2).VALOR := UN_ACUERDONRO;

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_FG_E_FC_PREDEU_DETACUERDOS
                                  ,UN_TABLAERROR => MI_TABLA_TDA
                                  ,UN_REEMPLAZOS => MI_REEMPLAZOS);        
      END;

      MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA  ||'''
                   AND TIPO     = '''||UN_TIPOCOBRO ||'''
                   AND CODIGO   =   '||UN_ACUERDONRO;

      BEGIN
        BEGIN
          MI_RTA := PCK_DATOS.FC_ACME(UN_ACCION    => 'E'
                                     ,UN_TABLA     => MI_TABLA_TAP
                                     ,UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
          RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        END;  

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
        MI_REEMPLAZOS(1).CLAVE := 'TIPO';
        MI_REEMPLAZOS(1).VALOR := UN_TIPOCOBRO;
        MI_REEMPLAZOS(2).CLAVE := 'CODIGO';
        MI_REEMPLAZOS(2).VALOR := UN_ACUERDONRO;

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_FG_E_FC_PREDEU_ACUERDOPAGO
                                  ,UN_TABLAERROR => MI_TABLA_TAP
                                  ,UN_REEMPLAZOS => MI_REEMPLAZOS);        
      END;
    END IF;  
  END PR_ELIMINARACUERDOPAGO;   

  --4
  PROCEDURE PR_CALCULARMENSUALIDAD 
  /*
    NAME              : PR_CALCULARMENSUALIDAD -> ?calcularMensualidad
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : PABLO ANDRES ESPITIA CUCA
    DATE MIGRATION    : 22/11/2017
    TIME              : 02:27 PM
    SOURCE MODULE     : FACTURACIÓN GENERAL (69) -> SysmanSF2017.11.01.accdb
    DESCRIPTION       : Calcula la mensualidad de los detalles del acuerdo y los inserta en la temporal.
    MODIFIED BY       : 

    @NAME  : calcularMensualidad
    @METHOD: POST
  */
  (
    UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,                      --> Codigo de la compania.
    UN_TIPOACUERDO   IN SF_DETALLE_FACTURA.TIPO_FACTURA%TYPE,          --> Codigo tipo de cobro. 
    UN_ACUERDO       IN SF_ACUERDO_PAGO.CODIGO%TYPE,                   --> Numero del acuerdo.
    UN_PRIMERFECHA   IN DATE,
    UN_FECHACORTE    IN DATE, 
    UN_MONTO         IN PCK_SUBTIPOS.TI_DOBLE,                         --> MontoInteres
    UN_MONTOINTERES  IN SF_ACUERDO_PAGO.DEUDA_INTERES%TYPE DEFAULT 0,  --> Deuda interes calculada.
    UN_NCUOTAS       IN PCK_SUBTIPOS.TI_ENTERO,                        --> Cantidad de cuotas
    UN_TASAINTERES   IN PCK_SUBTIPOS.TI_PORCENTAJE         DEFAULT 0,  --> Tasa de interes
    UN_TASARECARGO   IN PCK_SUBTIPOS.TI_PORCENTAJE         DEFAULT 0,
    UN_TASAGRADIENTE IN PCK_SUBTIPOS.TI_PORCENTAJE         DEFAULT 0,  --> Tasa gradiante
    UN_IND_SIMPLE    IN PCK_SUBTIPOS.TI_LOGICO             DEFAULT 0,  --> Indicador maneja tasa de interes simple.
    UN_USUARIO       IN PCK_SUBTIPOS.TI_USUARIO                        --> Codigo del usuario que desencadena el proceso.
  )
  AS
    MI_NC              PCK_SUBTIPOS.TI_ENTERO; --> Numero de cuotas del acuerdo de pago
    MI_GR              PCK_SUBTIPOS.TI_PORCENTAJE; 
    MI_GR_1            PCK_SUBTIPOS.TI_PORCENTAJE;
    MI_VM              PCK_SUBTIPOS.TI_DOBLE;  
    MI_SALDO           PCK_SUBTIPOS.TI_DOBLE;
    MI_SALDOI          PCK_SUBTIPOS.TI_DOBLE;
    MI_TI              PCK_SUBTIPOS.TI_PORCENTAJE; --> Tasa de interes
    MI_TI_1            PCK_SUBTIPOS.TI_PORCENTAJE; 
    MI_TIE             PCK_SUBTIPOS.TI_PORCENTAJE; --> Interes periodo equivalente
    MI_TIM             PCK_SUBTIPOS.TI_PORCENTAJE; --> Tasa de interes mensual
    MI_NA              PCK_SUBTIPOS.TI_PORCENTAJE;
    MI_VC              PCK_SUBTIPOS.TI_DOBLE; 
    MI_VCI             PCK_SUBTIPOS.TI_DOBLE; 
    MI_VALCUO          PCK_SUBTIPOS.TI_DOBLE; 
    MI_VALCUOI         PCK_SUBTIPOS.TI_DOBLE; 
    MI_VALCUOPRIM      PCK_SUBTIPOS.TI_PORCENTAJE;
    MI_DIGITOSREDONDEO PCK_SUBTIPOS.TI_LOGICO DEFAULT 0;
    MI_NDCS            PCK_SUBTIPOS.TI_ENTERO DEFAULT 0; --> Contador
    MI_NUEVACUOTA      PCK_SUBTIPOS.TI_LOGICO DEFAULT 1;
    MI_FECHAINICIO     DATE;
    MI_CUO             PCK_SUBTIPOS.TI_DOBLE; 
    MI_CUO_1           PCK_SUBTIPOS.TI_DOBLE; 
    MI_CUOI            PCK_SUBTIPOS.TI_DOBLE; 
    MI_INC             PCK_SUBTIPOS.TI_PORCENTAJE; 
    MI_XX              NUMBER(2,0);
    MI_VALINT          PCK_SUBTIPOS.TI_DOBLE; 
    MI_RTA             PCK_SUBTIPOS.TI_RTA_ACME;
    MI_TABLA_TDA       PCK_SUBTIPOS.TI_TABLA  DEFAULT 'SF_TEMP_DETALLE_ACUERDO';
    MI_CAMPOS          PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES         PCK_SUBTIPOS.TI_VALORES;
    MI_REEMPLAZOS      PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    --Se almacena en la variable nc el número de cuotas del acuerdo de pago
    MI_NC := CASE WHEN UN_NCUOTAS <= 0
                  THEN 1
                  ELSE UN_NCUOTAS
             END;

    MI_GR := UN_TASAGRADIENTE; 

    --Se almacena en la variable Vm (Valor Monto), el valor capital de la deuda la cual será la base para calcular los interes de financiación
    MI_VM := UN_MONTO;
    MI_SALDO  := UN_MONTO;
    MI_SALDOI := UN_MONTOINTERES;

    MI_TI := CASE WHEN UN_TASAINTERES IN(0) 
                  THEN 0.0001
                  ELSE UN_TASAINTERES
             END;

    --Tasa de interes mensual
    MI_TIM := CASE WHEN UN_IND_SIMPLE IN(0) 
                   THEN (POWER((1 + MI_TI / 100),(1 / 12)) - 1)
                   ELSE (MI_TI / 1000)
              END;

    IF MI_GR > 0 THEN 
      MI_GR_1 := MI_GR / 100;
      MI_NA   := UN_NCUOTAS / 12;
      MI_TI_1 := MI_TIM;
      MI_TIE  := (POWER(1 + MI_TIM, 12) - 1); --Interes periodo equivalente

      MI_VC   := UN_MONTO                                                                                  * 
                 (MI_TIE - MI_GR_1)                                                                        * 
                 (POWER((1 + MI_TIE), MI_NA) / (POWER((1 + MI_TIE), MI_NA) - POWER((1 + MI_GR_1), MI_NA))) *
                 (MI_TI_1 / (POWER((1 + MI_TI_1), 12) - 1));

      MI_VALCUOPRIM := MI_VC;  
    ELSIF MI_NC NOT IN(0) THEN
      MI_VC  := MI_VM / MI_NC;
      MI_VCI := UN_MONTOINTERES / MI_NC;
    ELSE
      MI_VC  := 0;
      MI_VCI := 0;
    END IF;

    MI_CUO  := ROUND(MI_VC , MI_DIGITOSREDONDEO);
    MI_CUOI := ROUND(MI_VCI, MI_DIGITOSREDONDEO);

    LOOP
      MI_FECHAINICIO := ADD_MONTHS(UN_PRIMERFECHA, MI_NDCS + MI_NUEVACUOTA);
      MI_CUO_1 := MI_CUO;

      IF (MI_NDCS + MI_NUEVACUOTA - 1) > 0 AND 
          MI_GR NOT IN(0)       AND 
          TRUNC((MI_NDCS + MI_NUEVACUOTA - 1) / 12) * 12 IN (MI_NDCS + MI_NUEVACUOTA - 1) 
      THEN
        MI_INC := ROUND(MI_CUO * MI_GR / 100);  
        MI_CUO := MI_CUO + MI_INC;
        MI_CUO := ROUND(MI_CUO, MI_DIGITOSREDONDEO);
        MI_CUO_1 := MI_CUO;
      END IF;      

      MI_VALCUO  := MI_CUO;
      MI_VALCUOI := MI_CUOI;

      MI_XX := CASE WHEN MI_FECHAINICIO <= UN_FECHACORTE
                    THEN MONTHS_BETWEEN(MI_FECHAINICIO, UN_FECHACORTE)
                    ELSE 0
               END;

      MI_VALINT := CASE WHEN (MI_NDCS + MI_NUEVACUOTA) >= UN_NCUOTAS 
                        THEN 0
                        ELSE ROUND(MI_SALDO * MI_TIM, MI_DIGITOSREDONDEO)
                   END;

      MI_SALDO  := MI_SALDO  - MI_VALCUO;  --Calcula el nuevo saldo del monto capital
      MI_SALDOI := MI_SALDOI - MI_CUOI;

      IF MI_SALDO <= 10 THEN
        MI_VALCUO := MI_VALCUO + MI_SALDO;
        MI_SALDO  := 0;
      END IF;                   

      IF MI_SALDOI <= 10 THEN
        MI_VALCUOI := MI_VALCUOI + MI_SALDOI;
        MI_SALDOI  := 0;
      END IF;     

      MI_CAMPOS := 'COMPANIA
                   ,TIPO_AP
                   ,CODIGO_AP
                   ,CUOTA
                   ,FECHACUOTA
                   ,CAPITAL
                   ,INTERES
                   ,INT_FINANCIACION
                   ,TOTAL_CUOTA
                   ,ESTADO
                   ,CREATED_BY
                   ,DATE_CREATED';

      MI_VALORES := ''''||UN_COMPANIA                         ||'''
                    ,'''||UN_TIPOACUERDO                      ||'''
                    ,  '||UN_ACUERDO                          ||'
                    ,  '||(MI_NDCS + MI_NUEVACUOTA           )||'
                    ,TO_DATE('''||TO_CHAR(MI_FECHAINICIO,'DD/MM/YYYY')||''',''DD/MM/YYYY'')
                    ,  '||MI_VALCUO                           ||'
                    ,  '||MI_VALCUOI                          ||'
                    ,  '||MI_VALINT                           ||'
                    ,  '||(MI_VALCUO + MI_VALCUOI + MI_VALINT)||'
                    ,''A''
                    ,'''||UN_USUARIO                          ||'''
                    ,SYSDATE';      

      BEGIN
        BEGIN
          MI_RTA := PCK_DATOS.FC_ACME(UN_ACCION  => 'I'
                                     ,UN_TABLA   => MI_TABLA_TDA
                                     ,UN_CAMPOS  => MI_CAMPOS
                                     ,UN_VALORES => MI_VALORES);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
        MI_REEMPLAZOS(1).CLAVE := 'CODIGO';
        MI_REEMPLAZOS(1).VALOR := UN_ACUERDO;
        MI_REEMPLAZOS(2).CLAVE := 'TIPO';
        MI_REEMPLAZOS(2).VALOR := UN_TIPOACUERDO;
        MI_REEMPLAZOS(3).CLAVE := 'CUOTA';
        MI_REEMPLAZOS(3).VALOR := (MI_NDCS + MI_NUEVACUOTA);

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_FG_I_PR_CALMEN_DETACUEPAGO
                                  ,UN_TABLAERROR => MI_TABLA_TDA
                                  ,UN_REEMPLAZOS => MI_REEMPLAZOS);
      END;

      MI_NDCS := MI_NDCS + 1;

      EXIT WHEN MI_SALDO <= 1;
    END LOOP;

    --MI_NDCS := 0;
  END PR_CALCULARMENSUALIDAD;   

  --5
  PROCEDURE PR_DISTRIBUIRACUERDO 
  /*
    NAME              : PR_DISTRIBUIRACUERDO -> En Access: ?DistribuirAcuerdo
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : PABLO ANDRES ESPITIA CUCA
    DATE MIGRATION    : 23/11/2017
    TIME              : 11:27 AM
    SOURCE MODULE     : FACTURACION GENERAL (69) - SysmanSF2017.11.01.accdb
    DESCRIPTION       : Proceso que toma la distribución de las cuotas de un acuerdo y con base en estos valores realizar la distribución 
                        de la deuda por conceptos. Debido a que al tener en cuenta los auxiliares de los conceptos ya no es posible agrupar 
                        la deuda por concepto, se debe realizar la distribución por factura de la de mayor antiguedad a la más reciente.
    MODIFIED BY       : 
    RETURN            : 

    @NAME  : distribuirAcuerdo
    @METHOD: 
  */
  (
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,            --> Codigo de la compania.
    UN_TIPOACUERDO IN SF_DETALLE_FACTURA.TIPO_FACTURA%TYPE,--> Codigo tipo de cobro. 
    UN_ACUERDO     IN SF_ACUERDO_PAGO.CODIGO%TYPE,         --> Numero del acuerdo.
    UN_TIPOFACTURA IN SF_DETALLE_FACTURA.TIPO_FACTURA%TYPE,--> Tipo factura.
    UN_LISFACTURAS IN PCK_SUBTIPOS.TI_RTA_ACME,            --> Numeros de factura separadas por coma.
    UN_TERCERO     IN TERCERO.NIT%TYPE,                    --> Codigo del tercero.
    UN_SUCURSAL    IN TERCERO.SUCURSAL%TYPE,               --> Codigo de la sucursal asociada al tercero.
    UN_ANIO        IN PCK_SUBTIPOS.TI_ANIO,                --> Anio del tipo de cobro.
    UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO              --> Usuario que desencadena el proceso.
  )
  AS 
    MI_PARCONINT        PCK_SUBTIPOS.TI_PARAMETRO; -- Util para el almacenar el valor del concepto: SF CONCEPTO DE INTERES - ACUERDO DE PAGO
    MI_LISFACTURAS      PCK_SUBTIPOS.TI_RTA_ACME;
    MI_REEMPLAZOS       PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_ERROR_COD        PLS_INTEGER;
    MI_TABLA_C          PCK_SUBTIPOS.TI_TABLA DEFAULT 'SF_CONCEPTOS';
    MI_TABLA_TDC        PCK_SUBTIPOS.TI_TABLA DEFAULT 'SF_TEMP_DETALLE_CUOTA';
    MI_RTA              PCK_SUBTIPOS.TI_RTA_ACME;
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
    MI_IND_DER          PCK_SUBTIPOS.TI_LOGICO;    
    MI_IND_INVENTARIO   PCK_SUBTIPOS.TI_LOGICO; 
    MI_IND_NDF          PCK_SUBTIPOS.TI_LOGICO DEFAULT -1; 

    MI_SUM              PCK_SUBTIPOS.TI_DOBLE;

    --Variables RSACUERDO
    MI_TIPOCONCEPTO     SF_CONCEPTOS.TIPODECONCEPTO%TYPE;
    MI_CUENTA_D_BASE    SF_CONCEPTOS.CUENTADEBITOBASE%TYPE;
    MI_CUENTA_C_BASE    SF_CONCEPTOS.CUENTACREDITOBASE%TYPE;
    MI_CUENTA_D_BASE_AV SF_CONCEPTOS.CUENTADEBITOBASE_AV%TYPE;
    MI_CUENTA_C_BASE_AV SF_CONCEPTOS.CUENTACREDITOBASE_AV%TYPE;
    MI_CENTROCOSTO      SF_CONCEPTOS.CENTRO_COSTO%TYPE;
    MI_AUXILIAR         SF_CONCEPTOS.AUXILIAR%TYPE;

    MI_VLRDISTRIBUIR    PCK_SUBTIPOS.TI_DOBLE;
    MI_SALDOCUOTA       PCK_SUBTIPOS.TI_DOBLE;
    MI_VLRNETO          PCK_SUBTIPOS.TI_DOBLE;
    MI_VLRCONDONADO     PCK_SUBTIPOS.TI_DOBLE; 
    MI_VLRACOMETIDA     PCK_SUBTIPOS.TI_DOBLE;
    MI_VLRADMONIMP      PCK_SUBTIPOS.TI_DOBLE;
    MI_VLRUTILIDADAIU   PCK_SUBTIPOS.TI_DOBLE;
    MI_VLRIVAUTILIDAD   PCK_SUBTIPOS.TI_DOBLE;
    MI_SALDOCONDONADO   PCK_SUBTIPOS.TI_DOBLE;
    MI_ANIOTRABAJO      PCK_SUBTIPOS.TI_ANIO;
    MI_CONCEPTOTRABAJO  SF_DETALLE_FACTURA.CONCEPTO%TYPE;
    MI_TIPOFACTRABAJO   SF_DETALLE_FACTURA.TIPO_FACTURA%TYPE;
    MI_FACTURATRABAJO   SF_DETALLE_FACTURA.FACTURA%TYPE;
    MI_TIPOFACTURA      SF_TEMP_DETALLE_CUOTA.TIPOFACT%TYPE;
    MI_NOFACTURA        SF_TEMP_DETALLE_CUOTA.NROFACT%TYPE;
    MI_VLRDESCUENTO     SF_TEMP_DETALLE_CUOTA.VALOR_DESCUENTO%TYPE;
    MI_VLRRETEFUENTE    SF_TEMP_DETALLE_CUOTA.VALOR_RETE%TYPE; 
    MI_VLRICA           SF_TEMP_DETALLE_CUOTA.VALOR_ICA%TYPE; 
    MI_VLRIVA           SF_TEMP_DETALLE_CUOTA.VALOR_IVA%TYPE; 
    MI_VLRBASE          SF_TEMP_DETALLE_CUOTA.VALOR_BASE%TYPE; 
    MI_VLRCOMPRA        SF_TEMP_DETALLE_CUOTA.VALOR_COMPRA%TYPE;
    MI_VLRUTILIDAD      SF_TEMP_DETALLE_CUOTA.VALOR_UTILIDAD%TYPE;
    MI_SALDODESCUENTO   SF_TEMP_DETALLE_CUOTA.VALOR_DESCUENTO%TYPE;
    MI_SALDORETEFUENTE  SF_TEMP_DETALLE_CUOTA.VALOR_RETE%TYPE;
    MI_SALDOICA         SF_TEMP_DETALLE_CUOTA.VALOR_ICA%TYPE;
    MI_SALDOIVA         SF_TEMP_DETALLE_CUOTA.VALOR_IVA%TYPE; 
    MI_SALDOBASE        SF_TEMP_DETALLE_CUOTA.VALOR_BASE%TYPE;
    MI_SALDONETO        SF_TEMP_DETALLE_CUOTA.TOTAL_CUOTA%TYPE;
    MI_SALDOCOMPRA      SF_TEMP_DETALLE_CUOTA.VALOR_COMPRA%TYPE;
    MI_SALDOUTILIDAD    SF_TEMP_DETALLE_CUOTA.VALOR_UTILIDAD%TYPE;
    MI_SALDOACOMETIDA   SF_TEMP_DETALLE_CUOTA.ACOMETIDA_SERVICIO%TYPE;
    MI_SALDOADMONIMP    SF_TEMP_DETALLE_CUOTA.ADMON_IMPREVISTOS%TYPE;
    MI_SALDOUTILIDADAIU SF_TEMP_DETALLE_CUOTA.UTILIDAD_AIU%TYPE;
    MI_SALDOIVAUTILIDAD SF_TEMP_DETALLE_CUOTA.IVA_UTILIDAD%TYPE;
  BEGIN
    MI_PARCONINT := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                         ,UN_NOMBRE    => 'SF CONCEPTO DE INTERES - ACUERDO DE PAGO'
                                         ,UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL
                                         ,UN_FECHA_PAR => SYSDATE);

    IF MI_PARCONINT IS NULL THEN
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
        MI_REEMPLAZOS(1).CLAVE := 'PAR';
        MI_REEMPLAZOS(1).VALOR := 'SF CONCEPTO DE INTERES - ACUERDO DE PAGO';

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE
                                  ,UN_ERROR_COD => PCK_ERRORES.ERR_FG_MSG_PR_DISACU_VALIDAPAR
                                  ,UN_REEMPLAZOS => MI_REEMPLAZOS);
      END;
    END IF;

    BEGIN
      BEGIN
        SELECT 
          TIPODECONCEPTO
         ,CUENTADEBITOBASE
         ,CUENTACREDITOBASE 
         ,CUENTADEBITOBASE_AV
         ,CUENTACREDITOBASE_AV
         ,CENTRO_COSTO
         ,AUXILIAR 
        INTO 
          MI_TIPOCONCEPTO
         ,MI_CUENTA_D_BASE
         ,MI_CUENTA_C_BASE
         ,MI_CUENTA_D_BASE_AV
         ,MI_CUENTA_C_BASE_AV
         ,MI_CENTROCOSTO
         ,MI_AUXILIAR      
        FROM SF_CONCEPTOS
        WHERE COMPANIA  = UN_COMPANIA
          AND ANO       = UN_ANIO
          AND TIPOCOBRO = UN_TIPOACUERDO
          AND CODIGO    = MI_PARCONINT;

      EXCEPTION WHEN NO_DATA_FOUND THEN
        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
      MI_REEMPLAZOS(1).CLAVE := 'CODIGO';
      MI_REEMPLAZOS(1).VALOR := MI_PARCONINT;
      MI_REEMPLAZOS(2).CLAVE := 'ANIO';
      MI_REEMPLAZOS(2).VALOR := UN_ANIO;
      MI_REEMPLAZOS(3).CLAVE := 'TIPO';
      MI_REEMPLAZOS(3).VALOR := UN_TIPOACUERDO;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_FG_NDF_PR_DISACU_CONFCNCPT
                                ,UN_TABLAERROR => MI_TABLA_C
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);  
    END;

    BEGIN
      IF MI_TIPOCONCEPTO NOT IN('I') THEN
        MI_ERROR_COD := PCK_ERRORES.ERR_FG_MSG_PR_DISACU_TIPOCPTOI;

        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
      ELSIF MI_CUENTA_D_BASE    IS NULL OR 
            MI_CUENTA_C_BASE    IS NULL OR 
            MI_CUENTA_D_BASE_AV IS NULL OR
            MI_CUENTA_C_BASE_AV IS NULL
      THEN
        MI_ERROR_COD := PCK_ERRORES.ERR_FG_MSG_PR_DISACU_CNTASCPTO;

        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
      END IF;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
      MI_REEMPLAZOS(1).CLAVE := 'CONCEPTO';
      MI_REEMPLAZOS(1).VALOR := MI_PARCONINT;  

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => MI_ERROR_COD
                                ,UN_TABLAERROR => MI_TABLA_C
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);  
    END;

    MI_ANIOTRABAJO := 0;
    MI_CONCEPTOTRABAJO := NULL;
    MI_TIPOFACTRABAJO := NULL;
    MI_FACTURATRABAJO := 0;
    MI_TIPOFACTURA := NULL;
    MI_NOFACTURA := 0;
    MI_VLRCONDONADO := 0;
    MI_VLRDESCUENTO := 0;
    MI_VLRRETEFUENTE := 0;
    MI_VLRICA := 0;
    MI_VLRIVA := 0;
    MI_VLRBASE := 0;
    MI_VLRCOMPRA := 0;
    MI_VLRUTILIDAD := 0;
    MI_VLRACOMETIDA := 0;
    MI_VLRADMONIMP := 0;
    MI_VLRUTILIDADAIU := 0;
    MI_VLRIVAUTILIDAD := 0;
    MI_SALDOCONDONADO := -1;
    MI_SALDODESCUENTO := -1;
    MI_SALDORETEFUENTE := -1;
    MI_SALDOICA := -1;
    MI_SALDOIVA := -1;
    MI_SALDOBASE := -1;
    MI_SALDONETO := -1;
    MI_SALDOCOMPRA := -1;
    MI_SALDOUTILIDAD := -1;
    MI_SALDOACOMETIDA := -1;
    MI_SALDOADMONIMP := -1;
    MI_SALDOUTILIDADAIU := -1;
    MI_SALDOIVAUTILIDAD := -1;

    <<RSACUERDO>>
    FOR RS_ACUERDO IN(SELECT 
                        CUOTA
                       ,FECHACUOTA
                       ,CAPITAL
                       ,INTERES
                       ,INT_FINANCIACION
                       ,TOTAL_CUOTA
                       ,ESTADO
                      FROM SF_TEMP_DETALLE_ACUERDO 
                      WHERE COMPANIA  = UN_COMPANIA
                        AND TIPO_AP   = UN_TIPOACUERDO
                        AND CODIGO_AP = UN_ACUERDO)
    LOOP    
      MI_VLRDISTRIBUIR := RS_ACUERDO.TOTAL_CUOTA - RS_ACUERDO.INT_FINANCIACION;
      MI_SALDOCUOTA    := MI_VLRDISTRIBUIR;

      MI_LISFACTURAS := PCK_SYSMAN_UTL.FC_COLOCARCOMILLAS(UN_LISFACTURAS);

      <<RSDEUDA>>
      FOR RS_DEUDA IN(SELECT    
                        DETALLE.ANO
                       ,DETALLE.FACTURA
                       ,DETALLE.CONCEPTO
                       ,SUM(DETALLE.VALOR_BASE        ) VALOR_BASE
                       ,SUM(DETALLE.VALOR_IVA         ) VALOR_IVA
                       ,SUM(DETALLE.VALOR_RETEFUENTE  ) VALOR_RETEFUENTE
                       ,SUM(DETALLE.VALOR_DESCUENTO   ) VALOR_DESCUENTO
                       ,SUM(DETALLE.VALOR_ICA         ) VALOR_ICA
                       ,SUM(DETALLE.VALOR_NETO        ) VALOR_NETO
                       ,SUM(DETALLE.VALOR_CONDONADO   ) VALOR_CONDONADO
                       ,SUM(DETALLE.VALOR_COMPRA      ) VALOR_COMPRA
                       ,SUM(DETALLE.VALOR_UTILIDAD    ) VALOR_UTILIDAD
                       ,DETALLE.TERCERO
                       ,DETALLE.SUCURSAL
                       ,DETALLE.CENTRO_COSTO
                       ,DETALLE.AUXILIAR
                       ,CONCEPTO.TIPODECONCEPTO
                       ,SUM(DETALLE.ACOMETIDA_SERVICIO) ACOMETIDA_SERVICIO
                       ,SUM(DETALLE.ADMON_IMPREVISTOS ) ADMON_IMPREVISTOS
                       ,SUM(DETALLE.UTILIDAD_AIU      ) UTILIDAD_AIU
                       ,SUM(DETALLE.IVA_UTILIDAD      ) IVA_UTILIDAD  
                      FROM SF_DETALLE_FACTURA DETALLE
                        INNER JOIN SF_CONCEPTOS CONCEPTO
                           ON DETALLE.COMPANIA = CONCEPTO.COMPANIA
                          AND DETALLE.ANO      = CONCEPTO.ANO
                          AND DETALLE.CONCEPTO = CONCEPTO.CODIGO
                      WHERE DETALLE.COMPANIA     = UN_COMPANIA 
                        AND DETALLE.TIPO_FACTURA = UN_TIPOFACTURA
                        AND INSTR(MI_LISFACTURAS, PCK_SYSMAN_UTL.FC_COLOCARCOMILLAS(DETALLE.FACTURA), 1) > 0
                      GROUP BY 
                        DETALLE.ANO
                       ,DETALLE.FACTURA
                       ,DETALLE.CONCEPTO
                       ,DETALLE.TERCERO
                       ,DETALLE.SUCURSAL
                       ,DETALLE.CENTRO_COSTO
                       ,DETALLE.AUXILIAR
                       ,CONCEPTO.TIPODECONCEPTO 
                      ORDER BY 
                        DETALLE.ANO             ASC
                       ,DETALLE.FACTURA
                       ,CONCEPTO.TIPODECONCEPTO ASC
                       ,DETALLE.CONCEPTO        ASC) 
      LOOP
        IF MI_SALDOCUOTA > 0 THEN
          MI_VLRDISTRIBUIR := MI_SALDOCUOTA; 
        END IF;

        IF MI_TIPOFACTURA IS NULL THEN
          MI_TIPOFACTURA := UN_TIPOFACTURA;
          MI_NOFACTURA   := RS_DEUDA.FACTURA;
        END IF;

        IF MI_ANIOTRABAJO NOT IN(0) THEN
          IF NOT (MI_ANIOTRABAJO     = RS_DEUDA.ANO      AND 
                  MI_CONCEPTOTRABAJO = RS_DEUDA.CONCEPTO AND 
                  MI_TIPOFACTRABAJO  = UN_TIPOFACTURA    AND 
                  MI_FACTURATRABAJO  = RS_DEUDA.FACTURA)
          THEN
            CONTINUE;
          END IF;
        END IF;

        MI_VLRNETO := CASE WHEN MI_SALDONETO >= 0 
                           THEN MI_SALDONETO
                           ELSE (RS_DEUDA.VALOR_NETO - RS_DEUDA.VALOR_CONDONADO)
                      END;

        IF MI_VLRNETO <= MI_SALDOCUOTA THEN                       
          IF MI_SALDOBASE >= 0 THEN
            MI_CAMPOS := 'COMPANIA
                         ,TIPO_AP
                         ,CODIGO_AP
                         ,ANO
                         ,CUOTA
                         ,TIPOFACT
                         ,NROFACT
                         ,CONCEPTO
                         ,VALOR_BASE
                         ,VALOR_IVA
                         ,VALOR_RETE
                         ,VALOR_DESCUENTO
                         ,VALOR_ICA
                         ,TOTAL_CUOTA
                         ,TERCERO
                         ,SUCURSAL
                         ,CENTRO_COSTO
                         ,AUXILIAR
                         ,VALOR_COMPRA
                         ,VALOR_UTILIDAD
                         ,ACOMETIDA_SERVICIO
                         ,ADMON_IMPREVISTOS
                         ,UTILIDAD_AIU
                         ,IVA_UTILIDAD
                         ,CREATED_BY
                         ,DATE_CREATED';

            MI_VALORES := ''''||UN_COMPANIA          ||'''
                          ,'''||UN_TIPOACUERDO       ||'''
                          ,  '||UN_ACUERDO           ||'
                          ,  '||RS_DEUDA.ANO         ||'
                          ,  '||RS_ACUERDO.CUOTA     ||'
                          ,'''||UN_TIPOFACTURA       ||'''
                          ,  '||RS_DEUDA.FACTURA     ||'
                          ,'''||RS_DEUDA.CONCEPTO    ||'''
                          ,  '||MI_SALDOBASE         ||'
                          ,  '||MI_SALDOIVA          ||'
                          ,  '||MI_SALDORETEFUENTE   ||'
                          ,  '||MI_SALDODESCUENTO    ||'
                          ,  '||MI_SALDOICA          ||'
                          ,  '||MI_SALDONETO         ||'
                          ,'''||RS_DEUDA.TERCERO     ||'''
                          ,'''||RS_DEUDA.SUCURSAL    ||'''
                          ,'''||RS_DEUDA.CENTRO_COSTO||'''
                          ,'''||RS_DEUDA.AUXILIAR    ||'''
                          ,  '||MI_SALDOCOMPRA       ||'
                          ,  '||MI_SALDOUTILIDAD     ||'
                          ,  '||MI_SALDOACOMETIDA    ||'
                          ,  '||MI_SALDOADMONIMP     ||'
                          ,  '||MI_SALDOUTILIDADAIU  ||'
                          ,  '||MI_SALDOIVAUTILIDAD  ||'
                          ,'''||UN_USUARIO           ||'''
                          ,SYSDATE';
          ELSE
            MI_CAMPOS := 'COMPANIA
                         ,TIPO_AP
                         ,CODIGO_AP
                         ,ANO
                         ,CUOTA
                         ,TIPOFACT
                         ,NROFACT
                         ,CONCEPTO
                         ,VALOR_BASE
                         ,VALOR_IVA
                         ,VALOR_RETE
                         ,VALOR_DESCUENTO
                         ,VALOR_ICA
                         ,TOTAL_CUOTA
                         ,VALOR_COMPRA
                         ,VALOR_UTILIDAD
                         ,TERCERO
                         ,SUCURSAL
                         ,CENTRO_COSTO
                         ,AUXILIAR
                         ,ACOMETIDA_SERVICIO
                         ,ADMON_IMPREVISTOS
                         ,UTILIDAD_AIU
                         ,IVA_UTILIDAD
                         ,CREATED_BY
                         ,DATE_CREATED';

            MI_VALORES := ''''||UN_COMPANIA                ||'''
                          ,'''||UN_TIPOACUERDO             ||'''
                          ,  '||UN_ACUERDO                 ||'
                          ,  '||RS_DEUDA.ANO               ||'
                          ,  '||RS_ACUERDO.CUOTA           ||'
                          ,'''||UN_TIPOFACTURA             ||'''
                          ,  '||RS_DEUDA.FACTURA           ||'
                          ,'''||RS_DEUDA.CONCEPTO          ||'''
                          ,  '||RS_DEUDA.VALOR_BASE        ||'
                          ,  '||RS_DEUDA.VALOR_IVA         ||'
                          ,  '||RS_DEUDA.VALOR_RETEFUENTE  ||'
                          ,  '||RS_DEUDA.VALOR_DESCUENTO   ||'
                          ,  '||RS_DEUDA.VALOR_ICA         ||'
                          ,  '||RS_DEUDA.VALOR_NETO        ||'
                          ,  '||RS_DEUDA.VALOR_COMPRA      ||'
                          ,  '||RS_DEUDA.VALOR_UTILIDAD    ||'
                          ,'''||RS_DEUDA.TERCERO           ||'''
                          ,'''||RS_DEUDA.SUCURSAL          ||'''
                          ,'''||RS_DEUDA.CENTRO_COSTO      ||'''
                          ,'''||RS_DEUDA.AUXILIAR          ||'''
                          ,  '||RS_DEUDA.ACOMETIDA_SERVICIO||'
                          ,  '||RS_DEUDA.ADMON_IMPREVISTOS ||'
                          ,  '||RS_DEUDA.UTILIDAD_AIU      ||'
                          ,  '||RS_DEUDA.IVA_UTILIDAD      ||'
                          ,'''||UN_USUARIO                 ||'''
                          ,SYSDATE';
          END IF;

          BEGIN
            BEGIN
              MI_RTA := PCK_DATOS.FC_ACME(UN_ACCION  => 'I'
                                         ,UN_TABLA   => MI_TABLA_TDC
                                         ,UN_CAMPOS  => MI_CAMPOS
                                         ,UN_VALORES => MI_VALORES);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
            END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
            MI_REEMPLAZOS(1).CLAVE := 'TIPO';
            MI_REEMPLAZOS(1).VALOR := UN_TIPOACUERDO;
            MI_REEMPLAZOS(2).CLAVE := 'CODIGO';
            MI_REEMPLAZOS(2).VALOR := UN_ACUERDO;
            MI_REEMPLAZOS(3).CLAVE := 'ANIO';
            MI_REEMPLAZOS(3).VALOR := RS_DEUDA.ANO;
            MI_REEMPLAZOS(4).CLAVE := 'CONCEPTO';
            MI_REEMPLAZOS(4).VALOR := RS_DEUDA.CONCEPTO;

            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                      ,UN_ERROR_COD  => PCK_ERRORES.ERR_FG_I_PR_DISACU_DETACUPRELI
                                      ,UN_TABLAERROR => MI_TABLA_TDC
                                      ,UN_REEMPLAZOS => MI_REEMPLAZOS);        
          END;

          MI_SALDOCUOTA := (MI_SALDOCUOTA - MI_VLRNETO);

        ELSE
          MI_VLRCONDONADO := CASE WHEN MI_SALDOCONDONADO > 0 
                                  THEN MI_SALDOCONDONADO
                                  ELSE RS_DEUDA.VALOR_CONDONADO
                             END;

          MI_SALDOCUOTA     := (MI_SALDOCUOTA + MI_VLRCONDONADO);  
          MI_SALDOCONDONADO := 0;

          MI_VLRDESCUENTO := CASE WHEN MI_SALDODESCUENTO > 0 
                                  THEN MI_SALDODESCUENTO
                                  ELSE RS_DEUDA.VALOR_DESCUENTO
                             END;

          MI_SALDOCUOTA     := (MI_SALDOCUOTA + MI_VLRDESCUENTO);
          MI_SALDODESCUENTO :=0;

          MI_VLRRETEFUENTE := CASE WHEN MI_SALDORETEFUENTE >= 0
                                   THEN MI_SALDOICA
                                   ELSE RS_DEUDA.VALOR_RETEFUENTE
                              END;     

          IF MI_VLRRETEFUENTE <= MI_SALDOCUOTA THEN
            MI_SALDOCUOTA      := (MI_SALDOCUOTA - MI_VLRRETEFUENTE);
            MI_SALDORETEFUENTE := 0;

          ELSE
            MI_SALDORETEFUENTE := (MI_VLRRETEFUENTE - MI_SALDOCUOTA);
            MI_VLRRETEFUENTE   := MI_SALDOCUOTA;
            MI_SALDOCUOTA      := 0;
          END IF;

          MI_VLRICA := CASE WHEN MI_SALDOICA >= 0
                            THEN MI_SALDOICA
                            ELSE RS_DEUDA.VALOR_ICA
                       END;

          IF MI_VLRICA <= MI_SALDOCUOTA THEN
            MI_SALDOCUOTA := (MI_SALDOCUOTA - MI_VLRICA);
            MI_SALDOICA   := 0;

          ELSE  
            MI_SALDOICA   := (MI_VLRICA - MI_SALDOCUOTA);
            MI_VLRICA     := MI_SALDOCUOTA;
            MI_SALDOCUOTA := 0;
          END IF;

          MI_VLRIVA := CASE WHEN MI_SALDOIVA >= 0
                            THEN MI_SALDOIVA
                            ELSE RS_DEUDA.VALOR_IVA
                       END;

          IF MI_SALDOCUOTA > 0 AND MI_VLRIVA <= MI_SALDOCUOTA THEN
            MI_SALDOCUOTA := (MI_SALDOCUOTA - MI_VLRIVA);
            MI_SALDOIVA   := 0;

          ELSE
            MI_SALDOIVA   := (MI_VLRIVA - MI_SALDOCUOTA);
            MI_VLRIVA     := MI_SALDOCUOTA;
            MI_SALDOCUOTA := 0;
          END IF;

          MI_IND_DER := PCK_FACT_GENERAL.FC_MANEJADERCONEXIONCONCEPTO(UN_COMPANIA  => UN_COMPANIA
                                                                     ,UN_ANO       => EXTRACT(YEAR FROM SYSDATE)
                                                                     ,UN_TIPOCOBRO => UN_TIPOACUERDO 
                                                                     ,UN_CONCEPTO  => RS_DEUDA.CONCEPTO);

          MI_IND_INVENTARIO := PCK_FACT_GENERAL.FC_MANEJAINVENTARIO(UN_COMPANIA  => UN_COMPANIA
                                                                   ,UN_ANO       => EXTRACT(YEAR FROM SYSDATE)
                                                                   ,UN_TIPOCOBRO => UN_TIPOACUERDO);

          IF MI_IND_DER NOT IN(0) THEN
            MI_VLRACOMETIDA := CASE WHEN MI_SALDOACOMETIDA >= 0 
                                    THEN MI_SALDOACOMETIDA
                                    ELSE RS_DEUDA.ACOMETIDA_SERVICIO
                               END;

            IF MI_SALDOCUOTA > 0 AND MI_VLRACOMETIDA <= MI_SALDOCUOTA THEN
              MI_SALDOCUOTA     := (MI_SALDOCUOTA - MI_VLRACOMETIDA);
              MI_SALDOACOMETIDA := 0;

            ELSE
              MI_SALDOACOMETIDA := (MI_VLRACOMETIDA - MI_SALDOCUOTA);
              MI_VLRACOMETIDA   := MI_SALDOCUOTA;
              MI_SALDOCUOTA     := 0;
            END IF;

            MI_VLRADMONIMP := CASE WHEN MI_SALDOADMONIMP >= 0 
                                   THEN MI_SALDOADMONIMP
                                   ELSE RS_DEUDA.ADMON_IMPREVISTOS
                              END;

            IF MI_SALDOCUOTA > 0 AND MI_VLRADMONIMP <= MI_SALDOCUOTA THEN
              MI_SALDOCUOTA    := (MI_SALDOCUOTA - MI_VLRADMONIMP);
              MI_SALDOADMONIMP := 0;

            ELSE
              MI_SALDOADMONIMP := (MI_VLRADMONIMP - MI_SALDOCUOTA);
              MI_VLRADMONIMP   := MI_SALDOCUOTA;
              MI_SALDOCUOTA    := 0;
            END IF;

            MI_VLRUTILIDADAIU := CASE WHEN MI_SALDOUTILIDADAIU >= 0
                                      THEN MI_SALDOUTILIDADAIU
                                      ELSE RS_DEUDA.UTILIDAD_AIU
                                 END;

            IF MI_SALDOCUOTA > 0 AND MI_VLRUTILIDADAIU <= MI_SALDOCUOTA THEN
              MI_SALDOCUOTA       := (MI_SALDOCUOTA - MI_VLRUTILIDADAIU);
              MI_SALDOUTILIDADAIU := 0;
            ELSE
              MI_SALDOUTILIDADAIU := (MI_VLRUTILIDADAIU - MI_SALDOCUOTA);
              MI_VLRUTILIDADAIU   := MI_SALDOCUOTA;
              MI_SALDOCUOTA       := 0;
            END IF;

            MI_VLRIVAUTILIDAD := CASE WHEN MI_SALDOIVAUTILIDAD >= 0
                                      THEN MI_SALDOIVAUTILIDAD
                                      ELSE RS_DEUDA.IVA_UTILIDAD
                                 END;

            IF MI_SALDOCUOTA > 0 AND MI_VLRIVAUTILIDAD <= MI_SALDOCUOTA THEN
              MI_SALDOCUOTA       := (MI_SALDOCUOTA - MI_VLRIVAUTILIDAD);
              MI_SALDOIVAUTILIDAD := 0;
            ELSE 
              MI_SALDOIVAUTILIDAD := (MI_VLRIVAUTILIDAD - MI_SALDOCUOTA);
              MI_VLRIVAUTILIDAD   := MI_SALDOCUOTA;
              MI_SALDOCUOTA       := 0;
            END IF;

            MI_VLRCOMPRA     := 0;
            MI_VLRUTILIDAD   := 0;
            MI_SALDOCOMPRA   := 0;
            MI_SALDOUTILIDAD := 0;
            MI_VLRBASE       := (MI_VLRACOMETIDA   + MI_VLRADMONIMP   + MI_VLRUTILIDADAIU   + MI_VLRIVAUTILIDAD  );
            MI_SALDOBASE     := (MI_SALDOACOMETIDA + MI_SALDOADMONIMP + MI_SALDOUTILIDADAIU + MI_SALDOIVAUTILIDAD);

          ELSIF MI_IND_INVENTARIO NOT IN(0) THEN
            MI_VLRCOMPRA := CASE WHEN MI_SALDOCOMPRA >= 0 
                                 THEN MI_SALDOCOMPRA
                                 ELSE RS_DEUDA.VALOR_COMPRA
                            END;

            IF MI_SALDOCUOTA > 0 AND MI_VLRCOMPRA <= MI_SALDOCUOTA THEN
              MI_SALDOCUOTA := (MI_SALDOCUOTA - MI_VLRCOMPRA);
              MI_SALDOCOMPRA := 0;
            ELSE
              MI_SALDOCOMPRA := (MI_VLRCOMPRA - MI_SALDOCUOTA);
              MI_VLRCOMPRA   := MI_SALDOCUOTA;
              MI_SALDOCUOTA  := 0; 
            END IF;

            MI_VLRUTILIDAD := CASE WHEN MI_SALDOUTILIDAD >= 0
                                   THEN MI_SALDOUTILIDAD
                                   ELSE RS_DEUDA.VALOR_UTILIDAD
                              END;

            IF MI_SALDOCUOTA > 0 AND MI_VLRUTILIDAD <= MI_SALDOCUOTA THEN
              MI_SALDOCUOTA    := (MI_SALDOCUOTA - MI_VLRUTILIDAD);
              MI_SALDOUTILIDAD := 0;
            ELSE
              MI_SALDOUTILIDAD := (MI_VLRUTILIDAD - MI_SALDOCUOTA);
              MI_VLRUTILIDAD   := MI_SALDOCUOTA;
              MI_SALDOCUOTA    := 0;
            END IF;

            MI_VLRBASE   := (MI_VLRCOMPRA + MI_VLRUTILIDAD);
            MI_SALDOBASE := (MI_SALDOCOMPRA + MI_SALDOUTILIDAD);

            MI_SALDOACOMETIDA   := CASE WHEN MI_SALDOACOMETIDA   IN(-1) THEN 0 ELSE MI_SALDOACOMETIDA   END;
            MI_SALDOADMONIMP    := CASE WHEN MI_SALDOADMONIMP    IN(-1) THEN 0 ELSE MI_SALDOADMONIMP    END;
            MI_SALDOUTILIDADAIU := CASE WHEN MI_SALDOUTILIDADAIU IN(-1) THEN 0 ELSE MI_SALDOUTILIDADAIU END;
            MI_SALDOIVAUTILIDAD := CASE WHEN MI_SALDOIVAUTILIDAD IN(-1) THEN 0 ELSE MI_SALDOIVAUTILIDAD END;

            MI_VLRACOMETIDA   := 0;
            MI_VLRADMONIMP    := 0;
            MI_VLRUTILIDADAIU := 0;
            MI_VLRIVAUTILIDAD := 0;
          ELSE
            MI_VLRBASE := CASE WHEN MI_SALDOBASE >= 0 THEN MI_SALDOBASE ELSE RS_DEUDA.VALOR_BASE END;

            IF MI_SALDOCUOTA > 0 AND MI_VLRBASE <= MI_SALDOCUOTA THEN
              MI_SALDOCUOTA := (MI_SALDOCUOTA - MI_VLRBASE);
              MI_SALDOBASE  := 0;
            ELSE 
              MI_SALDOBASE  := (MI_VLRBASE - MI_SALDOCUOTA);
              MI_VLRBASE    := MI_SALDOCUOTA;
              MI_SALDOCUOTA := 0;
            END IF;

            MI_VLRCOMPRA     := 0;
            MI_VLRUTILIDAD   := 0;
            MI_SALDOCOMPRA   := 0;
            MI_SALDOUTILIDAD := 0;

            MI_VLRBASE   := (MI_VLRACOMETIDA   + MI_VLRADMONIMP   + MI_VLRUTILIDADAIU   + MI_VLRIVAUTILIDAD  );
            MI_SALDOBASE := (MI_SALDOACOMETIDA + MI_SALDOADMONIMP + MI_SALDOUTILIDADAIU + MI_SALDOIVAUTILIDAD);
          END IF;

          MI_SALDONETO := (MI_SALDOBASE + MI_SALDOIVA - MI_SALDORETEFUENTE - MI_SALDODESCUENTO + MI_SALDOICA);
          MI_SUM       := (MI_VLRBASE + MI_VLRIVA - MI_VLRRETEFUENTE - MI_VLRDESCUENTO + MI_VLRICA - MI_VLRCONDONADO);

          IF MI_SUM IN (MI_VLRDISTRIBUIR) THEN
            MI_CAMPOS := 'COMPANIA
                         ,TIPO_AP
                         ,CODIGO_AP
                         ,ANO
                         ,CUOTA
                         ,TIPOFACT
                         ,NROFACT
                         ,CONCEPTO
                         ,VALOR_BASE
                         ,VALOR_IVA
                         ,VALOR_RETE
                         ,VALOR_DESCUENTO
                         ,VALOR_ICA
                         ,TOTAL_CUOTA
                         ,VALOR_COMPRA
                         ,VALOR_UTILIDAD
                         ,TERCERO
                         ,SUCURSAL
                         ,CENTRO_COSTO
                         ,AUXILIAR
                         ,CREATED_BY
                         ,DATE_CREATED';

            MI_VALORES := ''''||UN_COMPANIA          ||'''
                          ,'''||UN_TIPOACUERDO       ||'''
                          ,  '||UN_ACUERDO           ||'
                          ,  '||RS_DEUDA.ANO         ||'
                          ,  '||RS_ACUERDO.CUOTA     ||'
                          ,'''||UN_TIPOFACTURA       ||'''
                          ,  '||RS_DEUDA.FACTURA     ||'
                          ,'''||RS_DEUDA.CONCEPTO    ||'''
                          ,  '||MI_VLRBASE           ||'
                          ,  '||MI_VLRIVA            ||'
                          ,  '||MI_VLRRETEFUENTE     ||'
                          ,  '||MI_VLRDESCUENTO      ||'
                          ,  '||MI_VLRICA            ||'
                          ,  '||MI_SUM               ||'
                          ,  '||MI_VLRCOMPRA         ||'
                          ,  '||MI_VLRUTILIDAD       ||'
                          ,'''||RS_DEUDA.TERCERO     ||'''
                          ,'''||RS_DEUDA.SUCURSAL    ||'''
                          ,'''||RS_DEUDA.CENTRO_COSTO||'''
                          ,'''||RS_DEUDA.AUXILIAR    ||'''
                          ,'''||UN_USUARIO           ||'''
                          ,SYSDATE';    

            BEGIN
              BEGIN
                MI_RTA := PCK_DATOS.FC_ACME(UN_ACCION  => 'I'
                                           ,UN_TABLA   => MI_TABLA_TDC
                                           ,UN_CAMPOS  => MI_CAMPOS
                                           ,UN_VALORES => MI_VALORES);

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
              END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
              MI_REEMPLAZOS(1).CLAVE := 'TIPO';
              MI_REEMPLAZOS(1).VALOR := UN_TIPOACUERDO;
              MI_REEMPLAZOS(2).CLAVE := 'CODIGO';
              MI_REEMPLAZOS(2).VALOR := UN_ACUERDO;
              MI_REEMPLAZOS(3).CLAVE := 'ANIO';
              MI_REEMPLAZOS(3).VALOR := RS_DEUDA.ANO;
              MI_REEMPLAZOS(4).CLAVE := 'CONCEPTO';
              MI_REEMPLAZOS(4).VALOR := RS_DEUDA.CONCEPTO;

              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                        ,UN_ERROR_COD  => PCK_ERRORES.ERR_FG_I_PR_DISACU_DETACUPRELI
                                        ,UN_TABLAERROR => MI_TABLA_TDC
                                        ,UN_REEMPLAZOS => MI_REEMPLAZOS);        
            END;

            IF MI_VLRCONDONADO NOT IN(0) THEN
              MI_CAMPOS := 'COMPANIA
                           ,TIPO_AP
                           ,CODIGO_AP
                           ,ANO
                           ,CUOTA
                           ,CONCEPTO
                           ,VALOR_BASE
                           ,VALOR_IVA
                           ,VALOR_RETE
                           ,VALOR_DESCUENTO
                           ,VALOR_ICA
                           ,TOTAL_CUOTA
                           ,VALOR_COMPRA
                           ,VALOR_UTILIDAD
                           ,CREATED_BY
                           ,DATE_CREATED';

              MI_VALORES := ''''||UN_COMPANIA      ||'''
                            ,'''||UN_TIPOACUERDO   ||'''
                            ,  '||UN_ACUERDO       ||'
                            ,  '||RS_DEUDA.ANO     ||'
                            ,  '||RS_ACUERDO.CUOTA ||'
                            ,''110''
                            ,  '||MI_VLRCONDONADO  ||'
                            ,0
                            ,0
                            ,0
                            ,0
                            ,  '||MI_VLRCONDONADO  ||'
                            ,0
                            ,0
                            ,'''||UN_USUARIO       ||'''
                            ,SYSDATE';

              BEGIN
                BEGIN
                  MI_RTA := PCK_DATOS.FC_ACME(UN_ACCION  => 'I'
                                             ,UN_TABLA   => MI_TABLA_TDC
                                             ,UN_CAMPOS  => MI_CAMPOS
                                             ,UN_VALORES => MI_VALORES);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                END;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                MI_REEMPLAZOS(1).CLAVE := 'TIPO';
                MI_REEMPLAZOS(1).VALOR := UN_TIPOACUERDO;
                MI_REEMPLAZOS(2).CLAVE := 'CODIGO';
                MI_REEMPLAZOS(2).VALOR := UN_ACUERDO;
                MI_REEMPLAZOS(3).CLAVE := 'ANIO';
                MI_REEMPLAZOS(3).VALOR := RS_DEUDA.ANO;
                MI_REEMPLAZOS(4).CLAVE := 'CONCEPTO';
                MI_REEMPLAZOS(4).VALOR := 110;

                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                          ,UN_ERROR_COD  => PCK_ERRORES.ERR_FG_I_PR_DISACU_DETACUPRELI
                                          ,UN_TABLAERROR => MI_TABLA_TDC
                                          ,UN_REEMPLAZOS => MI_REEMPLAZOS);        
              END;          
            END IF;
          /*
          ELSE  
            --Es necesario verificar, si se debe adicionar la exception.
            --DistribuirAcuerdo = "Se presentaron inconvenientes en el proceso de distribución de la cuota No." & RsAcuerdo!CUOTA
          */
          END IF;                       
        END IF;

        IF MI_SALDOCUOTA IN(0) THEN
          MI_ANIOTRABAJO     := RS_DEUDA.ANO;
          MI_CONCEPTOTRABAJO := RS_DEUDA.CONCEPTO;
          MI_TIPOFACTRABAJO  := UN_TIPOFACTURA;
          MI_FACTURATRABAJO  := RS_DEUDA.FACTURA;
        ELSE 
          MI_ANIOTRABAJO      := 0;
          MI_CONCEPTOTRABAJO  := NULL;
          MI_TIPOFACTRABAJO   := NULL;
          MI_FACTURATRABAJO   := 0;
          MI_SALDOCONDONADO   := -1;
          MI_SALDODESCUENTO   := -1;
          MI_SALDORETEFUENTE  := -1;
          MI_SALDOICA         := -1;
          MI_SALDOIVA         := -1;
          MI_SALDOBASE        := -1;
          MI_SALDONETO        := -1;
          MI_SALDOCOMPRA      := -1;
          MI_SALDOUTILIDAD    := -1;
          MI_SALDOACOMETIDA   := -1;
          MI_SALDOADMONIMP    := -1;
          MI_SALDOUTILIDADAIU := -1;
          MI_SALDOIVAUTILIDAD := -1;
        END IF;

        MI_IND_NDF := 0;
      END LOOP RSDEUDA;

      IF MI_IND_NDF NOT IN(0)THEN
        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
          MI_REEMPLAZOS(1).CLAVE := 'FACTURAS';
          MI_REEMPLAZOS(1).VALOR := UN_LISFACTURAS;
          MI_REEMPLAZOS(2).CLAVE := 'TIPO';
          MI_REEMPLAZOS(2).VALOR := UN_TIPOFACTURA;

          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_FG_NDF_PR_DISACU_DETCONFAC
                                    ,UN_REEMPLAZOS => MI_REEMPLAZOS);         
        END;
      END IF;

      IF RS_ACUERDO.TOTAL_CUOTA NOT IN(0) THEN
        MI_CAMPOS := 'COMPANIA
                     ,TIPO_AP
                     ,CODIGO_AP
                     ,ANO
                     ,CUOTA
                     ,TIPOFACT
                     ,NROFACT
                     ,CONCEPTO
                     ,VALOR_BASE
                     ,VALOR_IVA
                     ,VALOR_RETE
                     ,VALOR_DESCUENTO
                     ,VALOR_ICA
                     ,TOTAL_CUOTA
                     ,TERCERO
                     ,SUCURSAL
                     ,CENTRO_COSTO
                     ,AUXILIAR
                     ,VALOR_COMPRA
                     ,VALOR_UTILIDAD
                     ,CREATED_BY
                     ,DATE_CREATED';

        MI_VALORES := ''''||UN_COMPANIA                ||'''
                      ,'''||UN_TIPOACUERDO             ||'''
                      ,  '||UN_ACUERDO                 ||'
                      ,  '||EXTRACT(YEAR FROM SYSDATE) ||'
                      ,  '||RS_ACUERDO.CUOTA           ||'
                      ,'''||MI_TIPOFACTURA             ||'''
                      ,  '||MI_NOFACTURA               ||'
                      ,'''||MI_PARCONINT               ||'''

                      ,  '||CASE WHEN RS_ACUERDO.CUOTA IN (0) 
                      THEN RS_ACUERDO.TOTAL_CUOTA
                      ELSE RS_ACUERDO.INT_FINANCIACION END||'
                      ,0
                      ,0
                      ,0
                      ,0

                      ,  '||CASE WHEN RS_ACUERDO.CUOTA IN (0) 
                      THEN RS_ACUERDO.TOTAL_CUOTA
                      ELSE RS_ACUERDO.INT_FINANCIACION END||'
                      ,'''||UN_TERCERO                 ||'''
                      ,'''||UN_SUCURSAL                ||'''
                      ,'''||MI_CENTROCOSTO             ||'''
                      ,'''||MI_AUXILIAR                ||'''

                      ,  '||CASE WHEN RS_ACUERDO.CUOTA IN (0) 
                      THEN RS_ACUERDO.TOTAL_CUOTA
                      ELSE RS_ACUERDO.INT_FINANCIACION END||'
                      ,0
                      ,'''||UN_USUARIO                 ||'''
                      ,SYSDATE';           

        BEGIN
          BEGIN
            MI_RTA := PCK_DATOS.FC_ACME(UN_ACCION  => 'I'
                                       ,UN_TABLA   => MI_TABLA_TDC
                                       ,UN_CAMPOS  => MI_CAMPOS
                                       ,UN_VALORES => MI_VALORES);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
          END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
          MI_REEMPLAZOS(1).CLAVE := 'TIPO';
          MI_REEMPLAZOS(1).VALOR := UN_TIPOACUERDO;
          MI_REEMPLAZOS(2).CLAVE := 'CODIGO';
          MI_REEMPLAZOS(2).VALOR := UN_ACUERDO;
          MI_REEMPLAZOS(3).CLAVE := 'ANIO';
          MI_REEMPLAZOS(3).VALOR := EXTRACT(YEAR FROM SYSDATE);
          MI_REEMPLAZOS(4).CLAVE := 'CONCEPTO';
          MI_REEMPLAZOS(4).VALOR := MI_PARCONINT;

          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_FG_I_PR_DISACU_DETACUPRELI
                                    ,UN_TABLAERROR => MI_TABLA_TDC
                                    ,UN_REEMPLAZOS => MI_REEMPLAZOS);        
        END;       
      END IF;

      MI_TIPOFACTURA := NULL;
      MI_NOFACTURA   := 0;
    END LOOP RSACUERDO;

    IF MI_IND_NDF NOT IN(0)THEN
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
        MI_REEMPLAZOS(1).CLAVE := 'ACUERDO';
        MI_REEMPLAZOS(1).VALOR := UN_ACUERDO;
        MI_REEMPLAZOS(2).CLAVE := 'TIPO';
        MI_REEMPLAZOS(2).VALOR := UN_TIPOACUERDO;

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_FG_NDF_PR_DISACU_DETACUERD
                                  ,UN_REEMPLAZOS => MI_REEMPLAZOS);         
      END;
    END IF;
  END PR_DISTRIBUIRACUERDO;  

  --6
  FUNCTION FC_GENERARACUERDOPRELIMINAR 
  /*
    NAME              : FC_GENERARACUERDOPRELIMINAR -> FRM_ACUERDO_PAGO.CmdAcuerdo_Click
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : PABLO ANDRES ESPITIA CUCA
    DATE MIGRATION    : 21/11/2017
    TIME              : 11:29 AM
    SOURCE MODULE     : FACTURACIÓN GENERAL (69) -> SysmanSF2017.11.01.accdb
    DESCRIPTION       : GENERA EL ACUERDO PELIMINAR.
    MODIFIED BY       : 

    @NAME  : generarAcuerdoPreliminar
    @METHOD: POST
  */
  (
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,                     --> Codigo de la compania.
    UN_TIPOCOBRO       IN SF_DETALLE_FACTURA.TIPO_FACTURA%TYPE,         --> Codigo tipo de cobro.
    UN_ANIO            IN PCK_SUBTIPOS.TI_ANIO,                         --> ANIO DEL TIPO DE COBRO SELECCIONADO.
    UN_TERCERO         IN TERCERO.NIT%TYPE,                             --> Codigo del tercero.
    UN_SUCURSAL        IN TERCERO.SUCURSAL%TYPE,                        --> Codigo de la sucursal asociada al tercero.
    UN_DEUDACAPITAL    IN SF_ACUERDO_PAGO.DEUDA_CAPITAL%TYPE DEFAULT 0, --> Deuda capitla calculada.
    UN_DEUDAINTERES    IN SF_ACUERDO_PAGO.DEUDA_INTERES%TYPE DEFAULT 0, --> Deuda interes calculada.
    UN_DEUDATOTAL      IN SF_ACUERDO_PAGO.DEUDA_TOTAL%TYPE   DEFAULT 0, --> Deuda total calculada.
    UN_CUOTAINICIAL    IN SF_ACUERDO_PAGO.CUOTA_INICIAL%TYPE DEFAULT 0, --> Cuota inicial.
    UN_IND_CONDONACION IN PCK_SUBTIPOS.TI_LOGICO             DEFAULT 0, --> Indicador de condonar intereses.
    UN_OBS_CONDONACION IN SF_ACUERDO_PAGO.OBSERVACION_CONDONACION%TYPE, --> Descripcion condonacion.
    UN_SELECCION_FACT  IN PCK_SUBTIPOS.TI_RTA_ACME,                     --> Numeros de factura separadas por coma.
    UN_ACUERDONRO      IN VARCHAR2,                                     --> Numero del acuerdo de pago.
    UN_TASA            IN PCK_SUBTIPOS.TI_PORCENTAJE         DEFAULT 0, --> Tasa de interes
    UN_NCUOTAS         IN PCK_SUBTIPOS.TI_ENTERO,                       --> Cantidad de cuotas
    UN_VALORCONDONADO  IN SF_ACUERDO_PAGO.CONDONACION%TYPE,             --> Valor condonado
    UN_IND_SIMPLE      IN PCK_SUBTIPOS.TI_LOGICO             DEFAULT 0, --> Indicador simple.
    UN_GRADIANTE       IN PCK_SUBTIPOS.TI_PORCENTAJE         DEFAULT 0, --> Tasa Gradiante
    UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO                       --> Codigo del usuario que desencadena el proceso.
  )
  RETURN VARCHAR2
  AS
    MI_ACUERDO   SF_ACUERDO_PAGO.CODIGO%TYPE;
    MI_TABLA_TAP PCK_SUBTIPOS.TI_TABLA DEFAULT 'SF_TEMP_ACUERDO_PAGO';  
    MI_TABLA_TDA PCK_SUBTIPOS.TI_TABLA DEFAULT 'SF_TEMP_DETALLE_ACUERDO';
    MI_CAMPOS    PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES   PCK_SUBTIPOS.TI_VALORES;
    MI_RTA       PCK_SUBTIPOS.TI_RTA_ACME;
    MI_PAR_ACUERDO VARCHAR2(100 CHAR);
  BEGIN
    IF UN_VALORCONDONADO NOT IN(0) THEN
      PR_APLICARCONDONACION(UN_COMPANIA       => UN_COMPANIA
                           ,UN_TIPOCOBRO      => UN_TIPOCOBRO
                           ,UN_VALORCONDONADO => UN_VALORCONDONADO
                           ,UN_SELECCION_FACT => UN_SELECCION_FACT);
    END IF;

    PR_ELIMINARACUERDOPAGO(UN_COMPANIA   => UN_COMPANIA
                          ,UN_TIPOCOBRO  => UN_TIPOCOBRO
                          ,UN_ACUERDONRO => UN_ACUERDONRO);

    MI_ACUERDO := FC_ENUMERARACUERDO(UN_COMPANIA  => UN_COMPANIA
                                    ,UN_TIPOCOBRO => UN_TIPOCOBRO);                      

    MI_CAMPOS := 'COMPANIA
                 ,TIPO
                 ,CODIGO
                 ,TERCERO
                 ,SUCURSAL
                 ,DEUDA_CAPITAL
                 ,DEUDA_INTERES
                 ,DEUDA_TOTAL
                 ,CUOTAS
                 ,FECHA_INICIO
                 ,ESTADO
                 ,CUOTA_INICIAL
                 ,APLICACONDONACION
                 ,CONDONACION
                 ,OBSERVACION_CONDONACION
                 ,TASA_INTERES
                 ,CREATED_BY
                 ,DATE_CREATED';

    MI_VALORES := ''''||UN_COMPANIA       ||'''
                  ,'''||UN_TIPOCOBRO      ||'''
                  ,  '||MI_ACUERDO        ||'
                  ,'''||UN_TERCERO        ||'''
                  ,'''||UN_SUCURSAL       ||'''
                  ,  '||UN_DEUDACAPITAL   ||'
                  ,  '||UN_DEUDAINTERES   ||'
                  ,  '||UN_DEUDATOTAL     ||'
                  ,  '||UN_NCUOTAS        ||'
                  ,SYSDATE
                  ,''A''
                  ,  '||UN_CUOTAINICIAL   ||'
                  ,  '||UN_IND_CONDONACION||'
                  ,  '||UN_VALORCONDONADO ||'
                  ,'''||UN_OBS_CONDONACION||'''
                  ,  '||UN_TASA           ||'
                  ,'''||UN_USUARIO        ||'''
                  ,SYSDATE';               

    BEGIN
      BEGIN
        MI_RTA := PCK_DATOS.FC_ACME(UN_ACCION  => 'I'
                                   ,UN_TABLA   => MI_TABLA_TAP
                                   ,UN_CAMPOS  => MI_CAMPOS
                                   ,UN_VALORES => MI_VALORES);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_FG_I_FC_GENACU_ACUERDPRELI
                                ,UN_TABLAERROR => MI_TABLA_TAP);
    END;

    IF UN_CUOTAINICIAL > 0 THEN 
      MI_CAMPOS := 'COMPANIA
                   ,TIPO_AP
                   ,CODIGO_AP
                   ,CUOTA
                   ,CAPITAL
                   ,INTERES
                   ,INT_FINANCIACION
                   ,TOTAL_CUOTA
                   ,ESTADO
                   ,CREATED_BY
                   ,DATE_CREATED';

      MI_VALORES := ''''||UN_COMPANIA    ||'''
                    ,'''||UN_TIPOCOBRO   ||'''
                    ,  '||MI_ACUERDO     ||'
                    ,0
                    ,  '||UN_CUOTAINICIAL||'
                    ,0
                    ,0
                    ,  '||UN_CUOTAINICIAL||'
                    ,''A''
                    ,'''||UN_USUARIO||'''
                    ,SYSDATE';

      BEGIN
        BEGIN
          MI_RTA := PCK_DATOS.FC_ACME(UN_ACCION  => 'I'
                                     ,UN_TABLA   => MI_TABLA_TDA
                                     ,UN_CAMPOS  => MI_CAMPOS
                                     ,UN_VALORES => MI_VALORES);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
          RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_FG_I_FC_GENACU_DETACUERDOP
                                  ,UN_TABLAERROR => MI_TABLA_TDA);
      END;
    END IF;
    
    
       MI_PAR_ACUERDO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                         ,UN_NOMBRE    => 'SF MANEJA AMORTIZACION DE ACUERDOS CON CUOTA FIJA'
                                         ,UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL
                                         ,UN_FECHA_PAR => SYSDATE);
    
    IF MI_PAR_ACUERDO IN ('SI') THEN 
      
 PR_CALCULARACUERDOPAGO(UN_COMPANIA      => UN_COMPANIA
                          ,UN_TIPOABONO   => UN_TIPOCOBRO
                          ,UN_NROABONO       => MI_ACUERDO
                          ,UN_PRIMERFECHA   => SYSDATE
                          ,UN_FECHADECORTE    => SYSDATE
                          ,UN_MONTO         => (UN_DEUDACAPITAL - UN_CUOTAINICIAL)
                          ,UN_MONTOINTERES  => UN_DEUDAINTERES
                          ,UN_NCUOTAS       => UN_NCUOTAS
                          ,UN_TASAINTERES   => UN_TASA
                          ,UN_TASARECARGO   => 0
                          ,UN_TASAGRADIENTE => UN_GRADIANTE
                          ,UN_BLSIMPLE     => UN_IND_SIMPLE
                          ,UN_USUARIO       => UN_USUARIO);
    ELSE
    PR_CALCULARMENSUALIDAD(UN_COMPANIA      => UN_COMPANIA
                          ,UN_TIPOACUERDO   => UN_TIPOCOBRO
                          ,UN_ACUERDO       => MI_ACUERDO
                          ,UN_PRIMERFECHA   => SYSDATE
                          ,UN_FECHACORTE    => SYSDATE
                          ,UN_MONTO         => (UN_DEUDACAPITAL - UN_CUOTAINICIAL)
                          ,UN_MONTOINTERES  => UN_DEUDAINTERES
                          ,UN_NCUOTAS       => UN_NCUOTAS
                          ,UN_TASAINTERES   => UN_TASA
                          ,UN_TASARECARGO   => 0
                          ,UN_TASAGRADIENTE => UN_GRADIANTE
                          ,UN_IND_SIMPLE    => UN_IND_SIMPLE
                          ,UN_USUARIO       => UN_USUARIO);
                        
    END IF;
    
    PR_DISTRIBUIRACUERDO(UN_COMPANIA    => UN_COMPANIA
                        ,UN_TIPOACUERDO => UN_TIPOCOBRO
                        ,UN_ACUERDO     => MI_ACUERDO
                        ,UN_TIPOFACTURA => UN_TIPOCOBRO
                        ,UN_LISFACTURAS => UN_SELECCION_FACT
                        ,UN_TERCERO     => UN_TERCERO
                        ,UN_SUCURSAL    => UN_SUCURSAL
                        ,UN_ANIO        => UN_ANIO
                        ,UN_USUARIO     => UN_USUARIO);

   

    RETURN MI_ACUERDO;  
  END FC_GENERARACUERDOPRELIMINAR;  

  --7
  PROCEDURE PR_VERIFICARDISTRIBUCION 
  /*
    NAME              : PR_VERIFICARDISTRIBUCION -> En Access: ?VerificarDistribucion
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : PABLO ANDRES ESPITIA CUCA
    DATE MIGRATION    : 29/11/2017
    TIME              : 08:56 AM
    SOURCE MODULE     : FACTURACION GENERAL (69) - SysmanSF2017.11.01.accdb
    DESCRIPTION       : En primer instancia verifica que la sumatoria de las cuotas corresponda al total de la deuda. En segunda instancia 
                        verifica que la sumatoria de la distribucion por concepto corresponda al valor de la cuota.
    MODIFIED BY       : 

    @NAME  : verificarDistribucion
  */
  (
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,          -- Codigo de la compania
    UN_TIPOACUERDO IN SF_DETALLE_FACTURA.TIPOCOBRO%TYPE, -- Tipo de cobro seleccionado al iniciar el modulo de facturacion general.
    UN_ACUERDO     IN SF_ACUERDO_PAGO.CODIGO%TYPE        -- Numero del acuerdo.
  )
  AS 
    MI_TABLA_TAP    PCK_SUBTIPOS.TI_TABLA DEFAULT 'SF_TEMP_ACUERDO_PAGO'; 
    MI_TABLA_TDA    PCK_SUBTIPOS.TI_TABLA DEFAULT 'SF_TEMP_DETALLE_ACUERDO';

    MI_REEMPLAZOS   PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_ERROR_COD    PLS_INTEGER;

    MI_DEUDACAPITAL SF_ACUERDO_PAGO.DEUDA_CAPITAL%TYPE;
    MI_DEUDAINTERES SF_ACUERDO_PAGO.DEUDA_INTERES%TYPE;
    MI_DEUDATOTAL   SF_ACUERDO_PAGO.DEUDA_TOTAL%TYPE;
    MI_CONDONACION  SF_ACUERDO_PAGO.CONDONACION%TYPE;

    MI_TCAPITAL     PCK_SUBTIPOS.TI_DOBLE;
    MI_TINTERES     PCK_SUBTIPOS.TI_DOBLE;
    MI_TDEUDA       PCK_SUBTIPOS.TI_DOBLE;
    MI_TINTFIN      PCK_SUBTIPOS.TI_DOBLE;

    MI_DIFERENCIA   PCK_SUBTIPOS.TI_DOBLE;
    MI_IND_NDF      PCK_SUBTIPOS.TI_LOGICO DEFAULT -1;
    MI_STRCUOTAS    PCK_SUBTIPOS.TI_STRSQL;
  BEGIN
    -- Primer verificacion: Acuerdo
    BEGIN
      BEGIN
        SELECT 
          DEUDA_CAPITAL
         ,DEUDA_INTERES
         ,DEUDA_TOTAL
         ,CONDONACION 
        INTO
          MI_DEUDACAPITAL
         ,MI_DEUDAINTERES
         ,MI_DEUDATOTAL
         ,MI_CONDONACION
        FROM SF_TEMP_ACUERDO_PAGO
        WHERE COMPANIA = UN_COMPANIA
          AND TIPO     = UN_TIPOACUERDO
          AND CODIGO   = UN_ACUERDO;

      EXCEPTION WHEN NO_DATA_FOUND THEN
        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
      MI_REEMPLAZOS(1).CLAVE := 'TIPO';
      MI_REEMPLAZOS(1).VALOR := UN_TIPOACUERDO;
      MI_REEMPLAZOS(2).CLAVE := 'ACUERDO';
      MI_REEMPLAZOS(2).VALOR := UN_ACUERDO;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_FG_NDF_PR_VERDIS_VACUERDO
                                ,UN_TABLAERROR => MI_TABLA_TAP
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;

    BEGIN
      BEGIN
        SELECT   
          SUM(CAPITAL                       ) TCAPITAL
         ,SUM(INTERES                       ) TINTERES
         ,SUM(TOTAL_CUOTA - INT_FINANCIACION) TDEUDA
         ,SUM(INT_FINANCIACION              ) TINTFIN  
        INTO 
          MI_TCAPITAL
         ,MI_TINTERES
         ,MI_TDEUDA
         ,MI_TINTFIN
        FROM SF_TEMP_DETALLE_ACUERDO
        WHERE COMPANIA  = UN_COMPANIA
          AND TIPO_AP   = UN_TIPOACUERDO
          AND CODIGO_AP = UN_ACUERDO;  

      EXCEPTION WHEN NO_DATA_FOUND THEN
        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
      MI_REEMPLAZOS(1).CLAVE := 'TIPO';
      MI_REEMPLAZOS(1).VALOR := UN_TIPOACUERDO;
      MI_REEMPLAZOS(2).CLAVE := 'ACUERDO';
      MI_REEMPLAZOS(2).VALOR := UN_ACUERDO;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_FG_NDF_PR_VERDIS_DETACUERD
                                ,UN_TABLAERROR => MI_TABLA_TDA
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);    
    END;

    BEGIN
      IF    ABS(MI_DEUDACAPITAL - MI_TCAPITAL ) > 0.01 THEN
        MI_ERROR_COD := PCK_ERRORES.ERR_FG_MSG_PR_VERDIS_VCAYVCC;

        MI_REEMPLAZOS(1).CLAVE := 'VCAPITALA';
        MI_REEMPLAZOS(1).VALOR := MI_DEUDACAPITAL;
        MI_REEMPLAZOS(2).CLAVE := 'VCAPITALC';
        MI_REEMPLAZOS(2).VALOR := MI_TCAPITAL;    

        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
      ELSIF ABS(MI_DEUDAINTERES - MI_TINTERES ) > 0.01 THEN
        MI_ERROR_COD := PCK_ERRORES.ERR_FG_MSG_PR_VERDIS_VIAYVIC;

        MI_REEMPLAZOS(1).CLAVE := 'VINTERESESA';
        MI_REEMPLAZOS(1).VALOR := MI_DEUDAINTERES;
        MI_REEMPLAZOS(2).CLAVE := 'VINTERESESC';
        MI_REEMPLAZOS(2).VALOR := MI_TINTERES; 

        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
      ELSIF ABS(MI_DEUDATOTAL   - MI_TDEUDA   ) > 0.01 THEN
        MI_ERROR_COD := PCK_ERRORES.ERR_FG_MSG_PR_VERDIS_VDAYVDC;

        MI_REEMPLAZOS(1).CLAVE := 'VDEUDAA';
        MI_REEMPLAZOS(1).VALOR := MI_DEUDATOTAL;
        MI_REEMPLAZOS(2).CLAVE := 'VDEUDAC';
        MI_REEMPLAZOS(2).VALOR := MI_TDEUDA; 

        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
      END IF;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => MI_ERROR_COD
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);    
    END;

    --Segunda verificacion: Tomar el valor de cada una de las cuotas y compararlo con la sumatoria.
    
    /*
    SELECT 
                           DETACUERDO.TOTAL_CUOTA TCUOTA_DET
                          ,SUMCUOTA.TOTALCUOTA    TCUOTA_SUM
                          ,DETACUERDO.CUOTA 
                         FROM SF_TEMP_DETALLE_ACUERDO DETACUERDO 
                           INNER JOIN (SELECT 
                                         COMPANIA
                                        ,TIPO_AP
                                        ,CODIGO_AP
                                        ,CUOTA
                                        ,SUM(TOTAL_CUOTA) TOTALCUOTA
                                       FROM SF_TEMP_DETALLE_CUOTA
                                       WHERE COMPANIA  = UN_COMPANIA
                                         AND TIPO_AP   = UN_TIPOACUERDO
                                         AND CODIGO_AP = UN_ACUERDO
                                       GROUP BY 
                                         COMPANIA 
                                        ,TIPO_AP
                                        ,CODIGO_AP
                                        ,CUOTA) SUMCUOTA
                              ON DETACUERDO.COMPANIA  = SUMCUOTA.COMPANIA
                             AND DETACUERDO.TIPO_AP   = SUMCUOTA.TIPO_AP
                             AND DETACUERDO.CODIGO_AP = SUMCUOTA.CODIGO_AP
                             AND DETACUERDO.CUOTA     = SUMCUOTA.CUOTA
                         WHERE DETACUERDO.COMPANIA  = UN_COMPANIA
                           AND DETACUERDO.TIPO_AP   = UN_TIPOACUERDO
                           AND DETACUERDO.CODIGO_AP = UN_ACUERDO
                         ORDER BY DETACUERDO.CUOTA*/
    <<RSDETACUERDO>>
    FOR RS_DETACUERDO IN(SELECT CUOTA,
                       SUM(TOTAL_CUOTA) - INT_FINANCIACION AS TCUOTA_DET,
                       SUM(CAPITAL) AS TCUOTA_SUM
                       From SF_TEMP_DETALLE_ACUERDO
                         WHERE COMPANIA  = UN_COMPANIA
                           AND TIPO_AP   = UN_TIPOACUERDO
                           AND CODIGO_AP = UN_ACUERDO
                           AND CUOTA NOT IN (0)
                       GROUP BY INT_FINANCIACION,
                       CUOTA
                       ORDER BY CUOTA)
    LOOP
      IF ABS(RS_DETACUERDO.TCUOTA_DET - RS_DETACUERDO.TCUOTA_SUM) >= 0.01 THEN
        IF MI_STRCUOTAS IS NULL THEN
          MI_REEMPLAZOS(1).CLAVE := 'CUOTA';
          MI_REEMPLAZOS(1).VALOR := RS_DETACUERDO.CUOTA;        

          MI_STRCUOTAS := PCK_ERR_MSG.FC_MENSAJE(UN_MENSAJE_COD => PCK_ERRORES.MSG_FG_PR_VERDIS_DIFTOTCUOTAS1
                                                ,UN_REEMPLAZOS  => MI_REEMPLAZOS);
        ELSE
          MI_STRCUOTAS := MI_STRCUOTAS || ', ' || RS_DETACUERDO.CUOTA;
        END IF;
      END IF;

      MI_IND_NDF := 0;
    END LOOP RSDETACUERDO;

    IF MI_IND_NDF NOT IN(0) THEN 
      BEGIN
        MI_REEMPLAZOS(1).CLAVE := 'TIPO';
        MI_REEMPLAZOS(1).VALOR := UN_TIPOACUERDO;
        MI_REEMPLAZOS(2).CLAVE := 'ACUERDO';
        MI_REEMPLAZOS(2).VALOR := UN_ACUERDO;

        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_FG_NDF_PR_VERDIS_DISCUOTAS
                                  ,UN_REEMPLAZOS => MI_REEMPLAZOS);    
      END;    
    END IF;

    IF MI_STRCUOTAS IS NOT NULL THEN
      BEGIN
        MI_REEMPLAZOS(1).CLAVE := 'LISCUOTAS';
        MI_REEMPLAZOS(1).VALOR := MI_STRCUOTAS;

        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.MSG_FG_PR_VERDIS_DIFTOTCUOTAS2
                                  ,UN_REEMPLAZOS => MI_REEMPLAZOS);    
      END;       
    END IF;

   -- MI_IND_NDF := -1;

    --Verificar que la sumatoria de la distribucion de cada concepto sea igual al valor registrado en la factura que se tomo para el acuerdo.
    <<RS_D_ACUERDO>>
    FOR RS_D_ACUERDO IN(WITH SUMDISTCONCEPTOS_ACUERDO AS(
                        SELECT 
                          COMPANIA
                         ,TIPO_AP
                         ,CODIGO_AP
                         ,TIPOFACT
                         ,NROFACT
                         ,CONCEPTO
                         ,SUM(TOTAL_CUOTA) TOTALCONCEPTO
                        FROM SF_TEMP_DETALLE_CUOTA
                        WHERE COMPANIA = UN_COMPANIA
                          AND TIPO_AP  = UN_TIPOACUERDO
                        GROUP BY 
                          COMPANIA
                         ,TIPO_AP
                         ,CODIGO_AP
                         ,TIPOFACT
                         ,NROFACT
                         ,CONCEPTO
                        )
                        SELECT 
                          ACUERDO.CODIGO_AP
                         ,ACUERDO.TIPOFACT
                         ,ACUERDO.NROFACT
                         ,ACUERDO.CONCEPTO
                         ,ACUERDO.TOTALCONCEPTO
                         ,DETALLE.VALOR_NETO
                        FROM SUMDISTCONCEPTOS_ACUERDO ACUERDO
                          INNER JOIN SF_DETALLE_FACTURA DETALLE
                             ON ACUERDO.COMPANIA = DETALLE.COMPANIA
                            AND ACUERDO.TIPOFACT = DETALLE.TIPO_FACTURA
                            AND ACUERDO.NROFACT  = DETALLE.FACTURA
                            AND ACUERDO.CONCEPTO = DETALLE.CONCEPTO
                        WHERE ACUERDO.COMPANIA  = UN_COMPANIA
                          AND ACUERDO.TIPO_AP   = UN_TIPOACUERDO
                          AND ACUERDO.CODIGO_AP = UN_ACUERDO)
    LOOP
     MI_IND_NDF := 0;  

      IF ABS(RS_D_ACUERDO.TOTALCONCEPTO - RS_D_ACUERDO.VALOR_NETO) >= 0.01 THEN
        IF MI_STRCUOTAS IS NULL THEN
          MI_REEMPLAZOS(1).CLAVE := 'CONCEPTO';
          MI_REEMPLAZOS(1).VALOR := RS_D_ACUERDO.CONCEPTO;     
          MI_REEMPLAZOS(2).CLAVE := 'FACTURA';
          MI_REEMPLAZOS(2).VALOR := RS_D_ACUERDO.TIPOFACT||'-'||RS_D_ACUERDO.NROFACT;          

          MI_STRCUOTAS := PCK_ERR_MSG.FC_MENSAJE(UN_MENSAJE_COD => PCK_ERRORES.MSG_FG_PR_VERDIS_DIFCONCEPTOS1
                                                ,UN_REEMPLAZOS  => MI_REEMPLAZOS);
        ELSE
          MI_REEMPLAZOS(1).CLAVE := 'CONCEPTO';
          MI_REEMPLAZOS(1).VALOR := RS_D_ACUERDO.CONCEPTO;     
          MI_REEMPLAZOS(2).CLAVE := 'FACTURA';
          MI_REEMPLAZOS(2).VALOR := RS_D_ACUERDO.TIPOFACT||'-'||RS_D_ACUERDO.NROFACT;  

          MI_STRCUOTAS := MI_STRCUOTAS || PCK_ERR_MSG.FC_MENSAJE(UN_MENSAJE_COD => PCK_ERRORES.MSG_FG_PR_VERDIS_DIFCONCEPTOS2
                                                                ,UN_REEMPLAZOS  => MI_REEMPLAZOS);
        END IF;
      END IF ;
    END LOOP RS_D_ACUERDO;

    IF MI_IND_NDF NOT IN(0) THEN 
      BEGIN
        MI_REEMPLAZOS(1).CLAVE := 'TIPO';
        MI_REEMPLAZOS(1).VALOR := UN_TIPOACUERDO;
        MI_REEMPLAZOS(2).CLAVE := 'ACUERDO';
        MI_REEMPLAZOS(2).VALOR := UN_ACUERDO;

        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;        
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_FG_NDF_PR_VERDIS_DISCONCEP
                                  ,UN_REEMPLAZOS => MI_REEMPLAZOS);    
      END;    
    END IF;

    IF MI_STRCUOTAS IS NOT NULL THEN
      BEGIN
        MI_REEMPLAZOS(1).CLAVE := 'LISCUOTAS';
        MI_REEMPLAZOS(1).VALOR := MI_STRCUOTAS;

        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;        
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.MSG_FG_PR_VERDIS_DIFTOTCUOTAS2
                                  ,UN_REEMPLAZOS => MI_REEMPLAZOS);    
      END;       
    END IF;  
  END PR_VERIFICARDISTRIBUCION;  

  --8
  FUNCTION FC_ENUMERARACUERDO 
  /*
    NAME              : FC_ENUMERARACUERDO -> ?EnumerarAcuerdo
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : PABLO ANDRES ESPITIA CUCA
    DATE MIGRATION    : 05/12/2017
    TIME              : 09:23 AM
    SOURCE MODULE     : FACTURACIÓN GENERAL (69) -> SysmanSF2017.11.01.accdb
    DESCRIPTION       : Genera el consecutivo del codigo del acuerdo preliminar.
    MODIFIED BY       : 

    @NAME  : enumerarAcuerdo
    @METHOD: GET
  */
  (
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA, -- Codigo de la compania.
    UN_TIPOCOBRO  IN SF_FACTURA.TIPOCOBRO%TYPE -- Tipo de cobro seleccionado al ingresar al modulo.
  )
  RETURN NUMBER 
  AS 
    MI_TABLA_AP  PCK_SUBTIPOS.TI_TABLA DEFAULT 'SF_ACUERDO_PAGO';

    MI_CONDICION PCK_SUBTIPOS.TI_CONDICION;

    MI_ACUERDO   SF_ACUERDO_PAGO.CODIGO%TYPE;
  BEGIN
    MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA ||''' 
                 AND TIPO     = '''||UN_TIPOCOBRO||'''';

    MI_ACUERDO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => MI_TABLA_AP
                                                  ,UN_CRITERIO => MI_CONDICION
                                                  ,UN_CAMPO    => 'CODIGO'
                                                  ,UN_INICIAL  => EXTRACT(YEAR FROM SYSDATE)||'000001'); 
    RETURN MI_ACUERDO;                                                
  END FC_ENUMERARACUERDO;  

  --9
  PROCEDURE PR_APROBARACUERDO 
  /*
    NAME              : PR_APROBARACUERDO -> En Access: FRM_ACUERDO_PAGO.CmdAprobar_Click
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : PABLO ANDRES ESPITIA CUCA
    DATE MIGRATION    : 29/11/2017
    TIME              : 08:49 AM
    SOURCE MODULE     : FACTURACION GENERAL (69) - SysmanSF2017.11.01.accdb
    DESCRIPTION       : Proceso para aprobar el acuerdo preliminar.
    MODIFIED BY       : 

    @NAME  : aprobarAcuerdo
    @METHOD: POST
  */
  (
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,         -- Codigo de la compania.
    UN_TIPOCOBRO  IN SF_FACTURA.TIPOCOBRO%TYPE,        -- Tipo de cobro seleccionado al ingresar al modulo.
    UN_ACUERDONRO IN SF_TEMP_ACUERDO_PAGO.CODIGO%TYPE, -- Numero de acuerdo preliminar.
    UN_SELECCION  IN VARCHAR2,                         -- Facturas con las que se preparo la deuda.
    UN_USUARIO    IN PCK_SUBTIPOS.TI_USUARIO           -- Codigo del usuario que desencadena el proceso.
  )
  AS 
    MI_RTA          PCK_SUBTIPOS.TI_RTA_ACME;
    MI_REEMPLAZOS   PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;

    MI_TABLA_AP     PCK_SUBTIPOS.TI_TABLA DEFAULT 'SF_ACUERDO_PAGO';
    MI_TABLA_DA     PCK_SUBTIPOS.TI_TABLA DEFAULT 'SF_DETALLE_ACUERDO';
    MI_TABLA_DC     PCK_SUBTIPOS.TI_TABLA DEFAULT 'SF_DETALLE_CUOTA'; 
    MI_TABLA_F      PCK_SUBTIPOS.TI_TABLA DEFAULT 'SF_FACTURA'; 

    MI_NROACUERDO   SF_ACUERDO_PAGO.CODIGO%TYPE; 
    MI_FACTURAS     VARCHAR2(1000 CHAR);
  BEGIN
    PR_VERIFICARDISTRIBUCION(UN_COMPANIA    => UN_COMPANIA
                            ,UN_TIPOACUERDO => UN_TIPOCOBRO
                            ,UN_ACUERDO     => UN_ACUERDONRO);

    -- Numero de acuerdo.               
    MI_NROACUERDO := FC_ENUMERARACUERDO(UN_COMPANIA  => UN_COMPANIA
                                       ,UN_TIPOCOBRO => UN_TIPOCOBRO);

    MI_CAMPOS := 'COMPANIA
                 ,TIPO
                 ,CODIGO
                 ,TERCERO
                 ,SUCURSAL
                 ,CENTRO_COSTO
                 ,AUXILIAR
                 ,DEUDA_CAPITAL
                 ,DEUDA_INTERES
                 ,DEUDA_TOTAL
                 ,CUOTAS
                 ,FECHA_INICIO
                 ,FECHA_FIN
                 ,ESTADO
                 ,CUOTA_INICIAL
                 ,APLICACONDONACION
                 ,CONDONACION
                 ,OBSERVACION_CONDONACION
                 ,TASA_INTERES
                 ,CREATED_BY
                 ,DATE_CREATED';

    MI_VALORES := 'SELECT      
                     COMPANIA
                    ,TIPO
                    ,'||MI_NROACUERDO||'
                    ,TERCERO
                    ,SUCURSAL
                    ,CENTRO_COSTO
                    ,AUXILIAR
                    ,DEUDA_CAPITAL
                    ,DEUDA_INTERES
                    ,DEUDA_TOTAL
                    ,CUOTAS
                    ,FECHA_INICIO
                    ,FECHA_FIN
                    ,ESTADO
                    ,CUOTA_INICIAL
                    ,APLICACONDONACION
                    ,CONDONACION
                    ,OBSERVACION_CONDONACION
                    ,TASA_INTERES
                    ,'''||UN_USUARIO||'''
                    ,SYSDATE
                  FROM SF_TEMP_ACUERDO_PAGO
                  WHERE COMPANIA = '''||UN_COMPANIA ||'''
                    AND TIPO     = '''||UN_TIPOCOBRO||'''
                    AND CODIGO   =   '||UN_ACUERDONRO;

    BEGIN
      BEGIN
        MI_RTA := PCK_DATOS.FC_ACME(UN_ACCION  => 'IS'
                                   ,UN_TABLA   => MI_TABLA_AP
                                   ,UN_CAMPOS  => MI_CAMPOS
                                   ,UN_VALORES => MI_VALORES);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        MI_REEMPLAZOS(1).CLAVE := 'ACUERDO';
        MI_REEMPLAZOS(1).VALOR := UN_ACUERDONRO;      

        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_FG_IS_PR_APRACU_ACUPAGOPRE
                                ,UN_TABLAERROR => MI_TABLA_AP
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;

    MI_CAMPOS := 'COMPANIA
                 ,TIPO_AP
                 ,CODIGO_AP
                 ,CUOTA
                 ,FECHACUOTA
                 ,CAPITAL
                 ,INTERES
                 ,INT_FINANCIACION
                 ,INT_RECARGO
                 ,TOTAL_CUOTA
                 ,FECHA_PAGO
                 ,FACTURA_PAGO
                 ,BANCO_PAGO
                 ,ESTADO
                 ,CREATED_BY
                 ,DATE_CREATED';

    MI_VALORES := 'SELECT 
                     COMPANIA
                    ,TIPO_AP
                    ,'||MI_NROACUERDO||'
                    ,CUOTA
                    ,FECHACUOTA
                    ,CAPITAL
                    ,INTERES
                    ,INT_FINANCIACION
                    ,INT_RECARGO
                    ,TOTAL_CUOTA
                    ,FECHA_PAGO
                    ,FACTURA_PAGO
                    ,BANCO_PAGO
                    ,ESTADO 
                    ,'''||UN_USUARIO||'''
                    ,SYSDATE
                  FROM SF_TEMP_DETALLE_ACUERDO
                  WHERE COMPANIA  = '''||UN_COMPANIA ||'''
                    AND TIPO_AP   = '''||UN_TIPOCOBRO||'''
                    AND CODIGO_AP =   '||UN_ACUERDONRO;

    BEGIN
      BEGIN
        MI_RTA := PCK_DATOS.FC_ACME(UN_ACCION  => 'IS'
                                   ,UN_TABLA   => MI_TABLA_DA
                                   ,UN_CAMPOS  => MI_CAMPOS
                                   ,UN_VALORES => MI_VALORES);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        MI_REEMPLAZOS(1).CLAVE := 'ACUERDO';
        MI_REEMPLAZOS(1).VALOR := UN_ACUERDONRO;      

        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_FG_IS_PR_APRACU_DETACUPREL
                                ,UN_TABLAERROR => MI_TABLA_DA
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;  

    MI_CAMPOS := 'COMPANIA
                 ,TIPO_AP
                 ,CODIGO_AP
                 ,CUOTA
                 ,ANO
                 ,TIPOFACT
                 ,NROFACT
                 ,CONCEPTO
                 ,VALOR_COMPRA
                 ,VALOR_BASE
                 ,VALOR_IVA
                 ,VALOR_RETE
                 ,VALOR_DESCUENTO
                 ,VALOR_ICA
                 ,TOTAL_CUOTA
                 ,VALOR_UTILIDAD
                 ,TERCERO
                 ,SUCURSAL
                 ,CENTRO_COSTO
                 ,AUXILIAR
                 ,CREATED_BY
                 ,DATE_CREATED';

    MI_VALORES := 'SELECT      
                     COMPANIA
                    ,TIPO_AP
                    ,'||MI_NROACUERDO||'
                    ,CUOTA
                    ,ANO
                    ,TIPOFACT
                    ,NROFACT
                    ,CONCEPTO
                    ,VALOR_COMPRA
                    ,VALOR_BASE
                    ,VALOR_IVA
                    ,VALOR_RETE
                    ,VALOR_DESCUENTO
                    ,VALOR_ICA
                    ,TOTAL_CUOTA
                    ,VALOR_UTILIDAD
                    ,TERCERO
                    ,SUCURSAL
                    ,CENTRO_COSTO
                    ,AUXILIAR 
                    ,'''||UN_USUARIO||'''
                    ,SYSDATE
                  FROM SF_TEMP_DETALLE_CUOTA
                  WHERE COMPANIA  = '''||UN_COMPANIA ||'''
                    AND TIPO_AP   = '''||UN_TIPOCOBRO||'''
                    AND CODIGO_AP =   '||UN_ACUERDONRO; 

    BEGIN
      BEGIN
        MI_RTA := PCK_DATOS.FC_ACME(UN_ACCION  => 'IS'
                                   ,UN_TABLA   => MI_TABLA_DC
                                   ,UN_CAMPOS  => MI_CAMPOS
                                   ,UN_VALORES => MI_VALORES);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        MI_REEMPLAZOS(1).CLAVE := 'ACUERDO';
        MI_REEMPLAZOS(1).VALOR := UN_ACUERDONRO;      

        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_FG_IS_PR_APRACU_DETCUOPREL
                                ,UN_TABLAERROR => MI_TABLA_DC
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;         

    MI_CAMPOS := 'IND_ACUERDO = -1
                 ,TIPO_AP     = '''||UN_TIPOCOBRO||'''
                 ,CODIGO_AP   =   '||MI_NROACUERDO;

    MI_FACTURAS := PCK_SYSMAN_UTL.FC_COLOCARCOMILLAS(UN_SELECCION);               

    MI_CONDICION := 'COMPANIA       = '''||UN_COMPANIA ||'''
                 AND TIPO_FACTURA   = '''||UN_TIPOCOBRO||'''
                 AND INSTR('''''||MI_FACTURAS||''''', PCK_SYSMAN_UTL.FC_COLOCARCOMILLAS(NUMERO_FACTURA), 1) > 0';

    --Actualizar los datos del acuerdo de pago en las facturas que se financieron
    BEGIN
      BEGIN
        MI_RTA := PCK_DATOS.FC_ACME(UN_ACCION    => 'M'
                                   ,UN_TABLA     => MI_TABLA_F
                                   ,UN_CAMPOS    => MI_CAMPOS
                                   ,UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        MI_REEMPLAZOS(1).CLAVE := 'FACTURAS';
        MI_REEMPLAZOS(1).VALOR := UN_SELECCION;      

        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_FG_M_PR_APRACU_FACTFINANCI
                                ,UN_TABLAERROR => MI_TABLA_F
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;    

    PR_ELIMINARACUERDOPAGO(UN_COMPANIA   => UN_COMPANIA
                          ,UN_TIPOCOBRO  => UN_TIPOCOBRO
                          ,UN_ACUERDONRO => UN_ACUERDONRO);

  END PR_APROBARACUERDO;  

  --10
  PROCEDURE PR_ELIMINARACUERDOPAGO_TER 
  /*
    NAME              : PR_ELIMINARACUERDOPAGO_TER
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : PABLO ANDRES ESPITIA CUCA
    DATE MIGRATION    : 07/12/2017
    TIME              : 10:00 AM
    SOURCE MODULE     : FACTURACIÓN GENERAL (69) -> SysmanSF2017.11.01.accdb
    DESCRIPTION       : Remueve los detalles de cuotas, los detalles de acuerdos y el acuerdo de pago asociados a un tercero.
    MODIFIED BY       : 

    @NAME  : eliminarAcuerdoPagoPorTercero
    @METHOD: DELETE
  */
  (
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,        -- Codigo de la compania.
    UN_TIPOCOBRO  IN SF_TEMP_ACUERDO_PAGO.TIPO%TYPE,  -- Tipo de cobro.
    UN_TERCERO    IN TERCERO.NIT%TYPE,                -- Codigo del tercero.
    UN_SUCURSAL   IN TERCERO.SUCURSAL%TYPE            -- Codigo de la sucursal asociada al tercero.
  )
  AS 
    MI_RTA        PCK_SUBTIPOS.TI_RTA_ACME;
    MI_TABLA_TDC  PCK_SUBTIPOS.TI_TABLA DEFAULT 'SF_TEMP_DETALLE_CUOTA';
    MI_TABLA_TDA  PCK_SUBTIPOS.TI_TABLA DEFAULT 'SF_TEMP_DETALLE_ACUERDO';
    MI_TABLA_TAP  PCK_SUBTIPOS.TI_TABLA DEFAULT 'SF_TEMP_ACUERDO_PAGO';
    MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION;  
    MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    IF UN_TERCERO IS NOT NULL THEN
      MI_CONDICION := 'COMPANIA  = '''||UN_COMPANIA  ||'''
                   AND TIPO_AP   = '''||UN_TIPOCOBRO ||'''
                   AND TERCERO   = '''||UN_TERCERO   ||'''
                   AND SUCURSAL  = '''||UN_SUCURSAL  ||'''';

      BEGIN
        BEGIN
          MI_RTA := PCK_DATOS.FC_ACME(UN_ACCION    => 'E'
                                     ,UN_TABLA     => MI_TABLA_TDC
                                     ,UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
          RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
        MI_REEMPLAZOS(1).CLAVE := 'TIPO';
        MI_REEMPLAZOS(1).VALOR := UN_TIPOCOBRO;
        MI_REEMPLAZOS(2).CLAVE := 'TERCERO';
        MI_REEMPLAZOS(2).VALOR := UN_TERCERO;

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_FG_E_PR_ELIACU_DETCUOTXTER
                                  ,UN_TABLAERROR => MI_TABLA_TDC
                                  ,UN_REEMPLAZOS => MI_REEMPLAZOS);        
      END;

      MI_CONDICION := 'COMPANIA  = '''||UN_COMPANIA  ||'''
                   AND TIPO      = '''||UN_TIPOCOBRO ||'''
                   AND TERCERO   = '''||UN_TERCERO   ||'''
                   AND SUCURSAL  = '''||UN_SUCURSAL  ||'''';

      BEGIN
        BEGIN
          MI_RTA := PCK_DATOS.FC_ACME(UN_ACCION    => 'E'
                                     ,UN_TABLA     => MI_TABLA_TAP
                                     ,UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
          RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        END;  

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
        MI_REEMPLAZOS(1).CLAVE := 'TIPO';
        MI_REEMPLAZOS(1).VALOR := UN_TIPOCOBRO;
        MI_REEMPLAZOS(2).CLAVE := 'TERCERO';
        MI_REEMPLAZOS(2).VALOR := UN_TERCERO;

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_FG_E_PR_ELIACU_ACUEPAGXTER
                                  ,UN_TABLAERROR => MI_TABLA_TAP
                                  ,UN_REEMPLAZOS => MI_REEMPLAZOS);        
      END;

      MI_CONDICION := 'COMPANIA  = '''||UN_COMPANIA  ||'''
                   AND TIPO_AP   = '''||UN_TIPOCOBRO ||'''';

      BEGIN
        BEGIN
          MI_RTA := PCK_DATOS.FC_ACME(UN_ACCION    => 'E'
                                     ,UN_TABLA     => MI_TABLA_TDA
                                     ,UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
          RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        END;      

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
        MI_REEMPLAZOS(1).CLAVE := 'TIPO';
        MI_REEMPLAZOS(1).VALOR := UN_TIPOCOBRO;

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_FG_E_PR_ELIACU_DETACUEXTER
                                  ,UN_TABLAERROR => MI_TABLA_TDA
                                  ,UN_REEMPLAZOS => MI_REEMPLAZOS);        
      END;
    END IF;  
  END PR_ELIMINARACUERDOPAGO_TER;

  -- 11
  PROCEDURE PR_FACTURARACUERDO
  /*
    NAME              : PR_FACTURARACUERDO  
    AUTHORS           : STEFANINI SYSMAN 
    AUTHOR MIGRACION  : AURA LILIANA MONROY GARCIA
    DATE MIGRADOR     : 04/01/2018
    TIME              : 15:28 
    SOURCE MODULE     : FACTURACIÓN GENERAL (69) -> SysmanSF2017.11.01.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : Genera una factura por cada cuota de un Acuerdo de Pago que ha sido enviado por parametro

    @NAME:    facturarAcuerdo
    @METHOD:  POST 
  */ 
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,        --> Compania de Ingreso a la aplicacion
    UN_ANIOCOBRO    IN PCK_SUBTIPOS.TI_ANIO,            --> Anio de trabajo seleccionado al ingresar al modulo de Facturacion General
    UN_TIPOCOBRO    IN PCK_SUBTIPOS.TI_TIPO_COBRO,      --> Tipo de cobro seleccionado al ingresar al modulo de Facturacion General
    UN_TIPOACUERDO  IN SF_DETALLE_ACUERDO.TIPO_AP%TYPE, --> Tipo de Acuerdo que se desea facturar
    UN_NROACUERDO   IN PCK_SUBTIPOS.TI_LONG,            --> Numero de Acuerdo de Pago a facturar
    UN_CUOTA        IN SF_DETALLE_ACUERDO.CUOTA%TYPE,   --> Cuota perteneciente al Acuerdo de pago a facturar
    UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO          --> Usuario que ejecuta el proceso
  )
  AS 
    MI_CUOTAS_ACTIVAS PCK_SUBTIPOS.TI_ENTERO;
    MI_LOTE           PCK_SUBTIPOS.TI_LOGICO;
    MI_NROCOBRO       PCK_SUBTIPOS.TI_LONG; 
    MI_CRITERIO       PCK_SUBTIPOS.TI_CONDICION;
    MI_TABLA          PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
    MI_RTA            PCK_SUBTIPOS.TI_RTA_ACME;
    MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_FACT_ACUERDO   PCK_SUBTIPOS.TI_ENTERO;
    MI_FECHA_VENCIMIENTO DATE;
  BEGIN
    -- Evalua las cuotas activas para realizar la facturacion
    SELECT COUNT(CUOTA) CUOTAS_ACTIVAS
      INTO MI_CUOTAS_ACTIVAS
      FROM SF_DETALLE_ACUERDO
     WHERE COMPANIA   = UN_COMPANIA 
       AND TIPO_AP    = UN_TIPOACUERDO 
       AND CODIGO_AP  = UN_NROACUERDO 
       AND CUOTA     <= UN_CUOTA
       AND ESTADO     IN ('A');
       
       

    IF MI_CUOTAS_ACTIVAS NOT IN (0) THEN
       MI_LOTE := CASE WHEN MI_CUOTAS_ACTIVAS IN (1) THEN 0 ELSE -1 END ;
    ELSE 
      BEGIN  
        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF
                  THEN PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                                  UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_NOCUOTASACTIVAS); 
      END; 
    END IF;

    <<CUOTAS_ACUERDO>>
    FOR RSCUOTAS IN ( SELECT CUOTA  
                        FROM SF_DETALLE_ACUERDO
                       WHERE COMPANIA   = UN_COMPANIA 
                         AND TIPO_AP    = UN_TIPOACUERDO 
                         AND CODIGO_AP  = UN_NROACUERDO 
                         AND CUOTA     <= UN_CUOTA
                         AND ESTADO     IN ('A'))

    LOOP
      -- Calcula el numero de cobro
      MI_CRITERIO := '    COMPANIA                 = ''' || UN_COMPANIA  || ''' 
                      AND TIPOCOBRO                = ''' || UN_TIPOCOBRO || ''' 
                      AND SUBSTR(CODIGO_COBRO,1,4) =   ' || UN_ANIOCOBRO; 

      MI_NROCOBRO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'SF_OBJETO_COBRO',
                                                      UN_CRITERIO => MI_CRITERIO,
                                                      UN_CAMPO    => 'CODIGO_COBRO',
                                                      UN_INICIAL  => EXTRACT(YEAR FROM SYSDATE)||'000001'); 


      -- Crear el header de la factura
      BEGIN
        BEGIN
          MI_TABLA   := 'SF_OBJETO_COBRO';

          MI_CAMPOS  := ' COMPANIA, ' ||
                        ' ANO, ' ||  
                        ' TIPOCOBRO, ' ||
                        ' CODIGO_COBRO, ' ||
                        ' TERCERO, ' ||
                        ' SUCURSAL, ' ||
                        ' CENTRO_COSTO, ' ||
                        ' AUXILIAR, ' ||
                        ' FECHA_SOLICITUD, ' ||
                        ' FECHA_REGISTRO, ' ||
                        ' FECHA_VENCIMIENTO, ' ||
                        ' VALOR_TOTAL, ' ||
                        ' OBSERVACIONES, ' ||
                        ' CREATED_BY, ' ||
                        ' DATE_CREATED, ' ||
                        ' TIPO_ACUERDO, ' ||
                        ' NRO_ACUERDO, ' ||
                        ' CUOTA_ACUERDO, ' ||
                        ' IMPRESO ';

          MI_VALORES := 'SELECT     '||
                            'SF_ACUERDO_PAGO.COMPANIA,    '||
                            '  ' || UN_ANIOCOBRO || ',    '||
                            '''' || UN_TIPOCOBRO || ''',  '||
                            '  ' || MI_NROCOBRO  || ',    '||
                            'SF_ACUERDO_PAGO.TERCERO,     '||
                            'SF_ACUERDO_PAGO.SUCURSAL,    '||
                            'SF_ACUERDO_PAGO.CENTRO_COSTO,    '||
                            'SF_ACUERDO_PAGO.AUXILIAR,    '||
                            'TO_CHAR(SYSDATE, ''DD/MM/YYYY'') ,   '||
                            'TO_CHAR(SYSDATE, ''DD/MM/YYYY'') ,   '||
                            'CASE WHEN SF_DETALLE_ACUERDO.CUOTA IN (0) 
                            THEN TO_CHAR(SF_ACUERDO_PAGO.FECHA_INICIO +
                            SF_TIPO_COBRO.DIAS_VALIDEZ,''DD/MM/YYYY'')
                            ELSE 
                            TO_CHAR(SF_DETALLE_ACUERDO.FECHACUOTA +
                            SF_TIPO_COBRO.DIAS_VALIDEZ,''DD/MM/YYYY'') END, '||
                            'SUM(SF_DETALLE_ACUERDO.TOTAL_CUOTA) ,    '||
                            ' '' Factura acuerdo de pago ' || UN_TIPOACUERDO || ' - ' || UN_NROACUERDO || ''',   '||
                            '''' || UN_USUARIO || ''',    '||
                            'TO_CHAR(SYSDATE, ''DD/MM/YYYY''),    '||
                            'SF_ACUERDO_PAGO.TIPO,    '||
                            'SF_ACUERDO_PAGO.CODIGO,  '||
                            'SF_DETALLE_ACUERDO.CUOTA,    '||
                            '0    '||
                        'FROM SF_DETALLE_ACUERDO    '||
                          'INNER JOIN SF_ACUERDO_PAGO   '||
                             'ON SF_DETALLE_ACUERDO.COMPANIA   = SF_ACUERDO_PAGO.COMPANIA    '||
                            'AND SF_DETALLE_ACUERDO.TIPO_AP   = SF_ACUERDO_PAGO.TIPO    '||
                            'AND SF_DETALLE_ACUERDO.CODIGO_AP = SF_ACUERDO_PAGO.CODIGO  '||
                          'INNER JOIN SF_TIPO_COBRO ' ||
                             'ON SF_DETALLE_ACUERDO.COMPANIA = SF_TIPO_COBRO.COMPANIA ' ||
                            'AND SF_DETALLE_ACUERDO.TIPO_AP = SF_TIPO_COBRO.CODIGO ' ||
                        'WHERE SF_ACUERDO_PAGO.COMPANIA      = ''' || UN_COMPANIA    || '''  '||
                          'AND SF_ACUERDO_PAGO.TIPO          = ''' || UN_TIPOACUERDO || '''   '||
                          'AND SF_ACUERDO_PAGO.CODIGO        =   ' || UN_NROACUERDO  || '   '||
                          'AND SF_DETALLE_ACUERDO.CUOTA      =   ' || RSCUOTAS.CUOTA || '   '||
                        'GROUP BY SF_ACUERDO_PAGO.COMPANIA,     '||
                          'SF_ACUERDO_PAGO.TERCERO,     '||
                          'SF_ACUERDO_PAGO.SUCURSAL,    '||
                          'SF_ACUERDO_PAGO.CENTRO_COSTO,    '||
                          'SF_ACUERDO_PAGO.AUXILIAR,    '||
                          'SF_ACUERDO_PAGO.TIPO,    '||
                          'SF_ACUERDO_PAGO.CODIGO,  '||
                          'SF_DETALLE_ACUERDO.CUOTA,
                          CASE WHEN SF_DETALLE_ACUERDO.CUOTA IN (0) 
                            THEN TO_CHAR(SF_ACUERDO_PAGO.FECHA_INICIO +
                            SF_TIPO_COBRO.DIAS_VALIDEZ,''DD/MM/YYYY'')
                            ELSE 
                            TO_CHAR(SF_DETALLE_ACUERDO.FECHACUOTA +
                            SF_TIPO_COBRO.DIAS_VALIDEZ,''DD/MM/YYYY'') END';  

          MI_RTA:= PCK_DATOS.FC_ACME( UN_TABLA   => MI_TABLA,
                                      UN_ACCION  => 'IS',
                                      UN_CAMPOS  => MI_CAMPOS,
                                      UN_VALORES => MI_VALORES); 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
           RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        END;
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
          MI_MSGERROR(1).CLAVE := 'CUOTA';
          MI_MSGERROR(1).VALOR := RSCUOTAS.CUOTA;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    =>  SQLCODE,
            UN_ERROR_COD  =>  PCK_ERRORES.ERR_SYSMANSF_INSOBJCOBRO,
            UN_TABLAERROR =>  MI_TABLA,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END;     

      IF MI_RTA IN (0) THEN 
        IF MI_LOTE NOT IN (0) THEN 
          PCK_FACT_GENERAL_COM1.PR_REGISTRARPROCESO(UN_COMPANIA    => UN_COMPANIA,
                                                    UN_CLASE       => 'F_AP',
                                                    UN_TIPOCOBRO   => UN_TIPOACUERDO,
                                                    UN_COBRO       => UN_NROACUERDO,
                                                    UN_DESCRIPCION => PCK_ERR_MSG.FC_MENSAJE(UN_MENSAJE_COD => 69000150),
                                                    UN_USUARIO     => UN_USUARIO);
          CONTINUE;
        ELSE  
          BEGIN  
            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF
                      THEN MI_MSGERROR(1).CLAVE := 'CUOTA';
                           MI_MSGERROR(1).VALOR := RSCUOTAS.CUOTA;
                           PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                      UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_NOINSCOBRO,
                                                      UN_REEMPLAZOS => MI_MSGERROR); 
            END; 
        END IF;
      END IF;    

      -- Creación Detalles de la Factura
      BEGIN
        BEGIN
          MI_TABLA   := 'SF_DETALLE_COBRO';

          MI_CAMPOS  := ' COMPANIA, '||
                        ' ANO, '||
                        ' TIPOCOBRO, '||
                        ' CODIGO_COBRO, '||
                        ' CONCEPTO, '||
                        ' CANTIDAD, '||
                        ' VALOR_COMPRA, '||
                        ' VALOR_BASE, '||
                        ' VALOR_IVA, '||
                        ' VALOR_DESCUENTO, '||
                        ' VALOR_ICA, '||
                        ' VALOR_UTILIDAD, '||
                        ' VALOR_NETO, '||
                        ' TERCERO, '||
                        ' SUCURSAL, '||
                        ' CENTRO_COSTO, '||
                        ' AUXILIAR, '||
                        ' FECHA_INICIO_COBRO, '||
                        ' FECHA_FIN_COBRO, '||
                        ' VALOR_UNITARIO, '||
                        ' CREATED_BY, '||
                        ' DATE_CREATED ';

          MI_VALORES := 'SELECT  '||
                        '  COMPANIA,  '||
                        '  ANO, '||
                        '  ''' || UN_TIPOCOBRO || ''',  '||
                        '    ' || MI_NROCOBRO  || ', '||
                        '  CONCEPTO,  '||
                        '  1 ,  '||
                        '  VALOR_COMPRA,  '||
                        '  VALOR_BASE,  '||
                        '  VALOR_IVA, '||
                        '  VALOR_DESCUENTO, '||
                        '  VALOR_ICA, '||
                        '  VALOR_UTILIDAD,  '||
                        '  TOTAL_CUOTA, '||
                        '  TERCERO, '||
                        '  SUCURSAL,  '||
                        '  CENTRO_COSTO,  '||
                        '  AUXILIAR,  '||
                        '  SYSDATE,  '||
                        '  SYSDATE,  '||
                        '  0, '||
                        '  ''' || UN_USUARIO || ''',  '||
                        '  SYSDATE      '||
                        'FROM SF_DETALLE_CUOTA  '||
                        'WHERE COMPANIA   = ''' || UN_COMPANIA    || '''  '||
                        '  AND TIPO_AP    = ''' || UN_TIPOACUERDO || '''   '||
                        '  AND CODIGO_AP  =   ' || UN_NROACUERDO  || '   '||
                        '  AND CUOTA      =   ' || RSCUOTAS.CUOTA || '   ';

          MI_RTA:= PCK_DATOS.FC_ACME( UN_TABLA   => MI_TABLA,
                                      UN_ACCION  => 'IS',
                                      UN_CAMPOS  => MI_CAMPOS,
                                      UN_VALORES => MI_VALORES); 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
           RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        END;
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
          MI_MSGERROR(1).CLAVE := 'CUOTA';
          MI_MSGERROR(1).VALOR := RSCUOTAS.CUOTA;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    =>  SQLCODE,
            UN_ERROR_COD  =>  PCK_ERRORES.ERR_SYSMANSF_INSDETOBJCOBRO,
            UN_TABLAERROR =>  MI_TABLA,
            UN_REEMPLAZOS =>  MI_MSGERROR
          );
      END;                         

      -- Cuando no posee detalles se elimina el Header creado previamente
      IF MI_RTA IN (0) THEN
        MI_TABLA     := 'SF_OBJETO_COBRO';

        MI_CONDICION := 'COMPANIA         = ''' || UN_COMPANIA  || '''  '||
                        'AND ANO          =   ' || UN_ANIOCOBRO || '    '||
                        'AND TIPOCOBRO    = ''' || UN_TIPOCOBRO || '''  '||
                        'AND CODIGO_COBRO =   ' || MI_NROCOBRO  || '  ';

        BEGIN
          BEGIN
            MI_RTA := PCK_DATOS.FC_ACME(
                        UN_TABLA     => MI_TABLA,
                        UN_ACCION    => 'E',
                        UN_CONDICION => MI_CONDICION); 
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
             RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
          END;
           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
            MI_MSGERROR(1).CLAVE := 'COBRO';
            MI_MSGERROR(1).VALOR := MI_NROCOBRO;
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    =>  SQLCODE,
              UN_ERROR_COD  =>  PCK_ERRORES.ERR_SYSMANSF_DELOBJCOBRO,
              UN_TABLAERROR =>  MI_TABLA,
              UN_REEMPLAZOS =>  MI_MSGERROR
            );
        END;    

        IF MI_LOTE NOT IN (0) THEN 
          PCK_FACT_GENERAL_COM1.PR_REGISTRARPROCESO(UN_COMPANIA    => UN_COMPANIA,
                                                    UN_CLASE       => 'F_AP',
                                                    UN_TIPOCOBRO   => UN_TIPOACUERDO,
                                                    UN_COBRO       => UN_NROACUERDO,
                                                    UN_DESCRIPCION => PCK_ERR_MSG.FC_MENSAJE(UN_MENSAJE_COD => 69000154),
                                                    UN_USUARIO     => UN_USUARIO);
          CONTINUE;
        ELSE  
          BEGIN  
            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF
                      THEN MI_MSGERROR(1).CLAVE := 'CUOTA';
                           MI_MSGERROR(1).VALOR := RSCUOTAS.CUOTA;
                           PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                      UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_NOINSDETCOBRO,
                                                      UN_REEMPLAZOS => MI_MSGERROR); 
            END; 
        END IF;
      END IF;

      /*  Se realiza el llamado a la funcion FC_FACTURAR para crear la factura de la cuota, si el proceso es exitoso, se realizan algunos 
           ajustes para definir la factura creada como un tipo de Factura para Acuerdo de Pago */
      IF PCK_FACT_GENERAL_COM2.FC_FACTURAR( UN_COMPANIA   => UN_COMPANIA,
                                            UN_TIPOCOBRO  => UN_TIPOCOBRO,
                                            UN_FACINICIAL => MI_NROCOBRO,
                                            UN_FACFINAL   => MI_NROCOBRO,
                                            UN_POR_LOTES  => -1,
                                            UN_NOFACTURA  => 0,
                                            UN_ANIO       => UN_ANIOCOBRO,
                                            UN_USUARIO    => UN_USUARIO) NOT IN (0) THEN

        -- Evalua si fue creada la factura  para la cuota del acuerdo                                         
        SELECT COUNT(NRO_FACTURA) FACT_ACUERDO
         INTO MI_FACT_ACUERDO
         FROM SF_OBJETO_COBRO
        WHERE COMPANIA      = UN_COMPANIA
          AND ANO           = UN_ANIOCOBRO
          AND TIPOCOBRO     = UN_TIPOCOBRO
          AND CODIGO_COBRO  = MI_NROCOBRO
          AND TIPO_ACUERDO  = UN_TIPOACUERDO
          AND NRO_ACUERDO   = UN_NROACUERDO
          AND CUOTA_ACUERDO = RSCUOTAS.CUOTA;

        IF MI_FACT_ACUERDO NOT IN (0) THEN
          <<FACTURA_ACUERDO>>
          FOR RSFACTURA IN (SELECT 
                                  TIPOFACTURA,
                                  NRO_FACTURA,
                                  CUOTA_ACUERDO
                             FROM SF_OBJETO_COBRO
                            WHERE COMPANIA      = UN_COMPANIA
                              AND ANO           = UN_ANIOCOBRO
                              AND TIPOCOBRO     = UN_TIPOCOBRO
                              AND CODIGO_COBRO  = MI_NROCOBRO
                              AND TIPO_ACUERDO  = UN_TIPOACUERDO
                              AND NRO_ACUERDO   = UN_NROACUERDO
                              AND CUOTA_ACUERDO = RSCUOTAS.CUOTA)                   
          LOOP
            -- Actualiza la factura generada como Factura de Acuerdo
            MI_TABLA      := 'SF_FACTURA';
            MI_CAMPOS     := 'ESACUERDO        = -1, '||
                             'TIPO_AP          = ''' || UN_TIPOACUERDO || ''', '||
                             'CODIGO_AP        =   ' || UN_NROACUERDO || ', '||
                             'CUOTA_ACUERDO    =   ' || RSFACTURA.CUOTA_ACUERDO || ', '||
                             'DATE_MODIFIED    = SYSDATE, '||
                             'MODIFIED_BY      = ''' || UN_USUARIO || ''' ';    
            MI_CONDICION  := '     COMPANIA       = ''' || UN_COMPANIA  || ''' '||
                             ' AND ANO            =   ' || UN_ANIOCOBRO || ' '||
                             ' AND TIPO_FACTURA   = ''' || RSFACTURA.TIPOFACTURA || ''' '||
                             ' AND NUMERO_FACTURA =   ' || RSFACTURA.NRO_FACTURA;

            BEGIN
              BEGIN
                MI_RTA     := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                                 UN_ACCION    => 'M', 
                                                 UN_CAMPOS    => MI_CAMPOS, 
                                                 UN_CONDICION => MI_CONDICION);     
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                   MI_MSGERROR(1).CLAVE := 'FACTURA';
                   MI_MSGERROR(1).VALOR :=  RSFACTURA.NRO_FACTURA;
                   RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    =>  SQLCODE,
                  UN_ERROR_COD  =>  PCK_ERRORES.ERR_SYSMANSF_UPDFACTACUERDO,
                  UN_TABLAERROR =>  MI_TABLA,
                  UN_REEMPLAZOS =>  MI_MSGERROR );
            END;  

            -- Actualiza en el detalle del acuerdo con que factura se cobrará la cuota
            MI_TABLA      := 'SF_DETALLE_ACUERDO';
            MI_CAMPOS     := 'TIPOFACTURA      = ''' || RSFACTURA.TIPOFACTURA || ''', '||
                             'FACTURA_PAGO     =   ' || RSFACTURA.NRO_FACTURA || ', '||
                             'ESTADO           =  ''F'', '||
                             'DATE_MODIFIED    = SYSDATE, '||
                             'MODIFIED_BY      = ''' || UN_USUARIO || ''' ';    
            MI_CONDICION  := '     COMPANIA  = ''' || UN_COMPANIA  || ''' '||
                             ' AND TIPO_AP   = ''' || UN_TIPOACUERDO || ''' '||
                             ' AND CODIGO_AP =   ' || UN_NROACUERDO || ' ' ||
                             ' AND CUOTA     =   ' || RSCUOTAS.CUOTA;               
            BEGIN
              BEGIN
                MI_RTA     := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                                 UN_ACCION    => 'M', 
                                                 UN_CAMPOS    => MI_CAMPOS, 
                                                 UN_CONDICION => MI_CONDICION);     
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                   MI_MSGERROR(1).CLAVE := 'ACUERDO';
                   MI_MSGERROR(1).VALOR := UN_NROACUERDO;
                   RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    =>  SQLCODE,
                  UN_ERROR_COD  =>  PCK_ERRORES.ERR_SYSMANSF_UPDDETFACTACUERDO,
                  UN_TABLAERROR =>  MI_TABLA,
                  UN_REEMPLAZOS =>  MI_MSGERROR );
            END;             

          END LOOP FACTURA_ACUERDO;

        ELSE
          MI_MSGERROR(1).CLAVE := 'CUOTA';
          MI_MSGERROR(1).VALOR := RSCUOTAS.CUOTA;
          IF MI_LOTE NOT IN (0) THEN 
            PCK_FACT_GENERAL_COM1.PR_REGISTRARPROCESO(UN_COMPANIA    => UN_COMPANIA,
                                                      UN_CLASE       => 'F_AP',
                                                      UN_TIPOCOBRO   => UN_TIPOACUERDO,
                                                      UN_COBRO       => UN_NROACUERDO,
                                                      UN_DESCRIPCION => PCK_ERR_MSG.FC_MENSAJE(UN_MENSAJE_COD => 69000158,
                                                                                               UN_REEMPLAZOS  => MI_MSGERROR ),
                                                      UN_USUARIO     => UN_USUARIO);
            CONTINUE;
          ELSE  
            BEGIN  
              RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF
                        THEN PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                        UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_MSJNOUPDFACTURA,
                                                        UN_REEMPLAZOS => MI_MSGERROR); 
              END; 
          END IF;              
        END IF;                                                                            
      END IF;

    END LOOP CUOTAS_ACUERDO;
  END PR_FACTURARACUERDO;
  
  
  PROCEDURE PR_CALCULARACUERDOPAGO(
/*
      NAME              : PR_CALCULARMENSUALIDADABONOCAJ METODO MIGRADO EN ACCESS calcularMensualidadAbonoCaj,
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JUAN CAMILO RODRIGUEZ DIAZ
      DATE MIGRADOR     : 22/11/2017
      TIME              : 10:30 AM
      SOURCE MODULE     : SysmanSF2017.11.01
      DESCRIPTION       : Funcio que permite diferirir una factura en cuotas fijas
      MODIFIER          : 
      DATE MODIFIED     : ELKIN GEOVANNY AMAYA SILVA
      TIME MODIFIED     : 06/11/2018
      MODIFICATIONS     : 

    @NAME:   calcularMensualidadAbonoCaj
    @METHOD: POST
*/
  UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOABONO     IN PCK_SUBTIPOS.TI_PARAMETRO,
  UN_NROABONO      IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_PRIMERFECHA   IN DATE,
  UN_FECHADECORTE  IN DATE,
  UN_MONTO         IN PCK_SUBTIPOS.TI_DOBLE,
  UN_MONTOINTERES  IN PCK_SUBTIPOS.TI_DOBLE,
  UN_NCUOTAS       IN PCK_SUBTIPOS.TI_ENTERO,
  UN_TASAINTERES   IN PCK_SUBTIPOS.TI_DOBLE,
  UN_TASARECARGO   IN PCK_SUBTIPOS.TI_DOBLE,
  UN_TASAGRADIENTE IN PCK_SUBTIPOS.TI_DOBLE,
  UN_BLSIMPLE      IN PCK_SUBTIPOS.TI_LOGICO,
  UN_USUARIO       IN PCK_SUBTIPOS.TI_USUARIO
)AS
  MI_SALDO            PCK_SUBTIPOS.TI_DOBLE;
  MI_VM               PCK_SUBTIPOS.TI_DOBLE;
  MI_VMSI             PCK_SUBTIPOS.TI_DOBLE; --' VALOR MONTO SIN INTERES
  MI_TI               PCK_SUBTIPOS.TI_PORCENTAJE; --' TASA DE INTERES ANUAL
  MI_CUO              PCK_SUBTIPOS.TI_DOBLE;
  MI_NDCS             PCK_SUBTIPOS.TI_ENTERO;
  MI_XX1              PCK_SUBTIPOS.TI_PARAMETRO;
  MI_XX               PCK_SUBTIPOS.TI_ENTERO;
  MI_VALINT           PCK_SUBTIPOS.TI_DOBLE;
  MI_VALCUO           PCK_SUBTIPOS.TI_DOBLE;
  MI_FECHAINICIO      DATE;
  MI_NUEVACUOTA       PCK_SUBTIPOS.TI_ENTERO;
  MI_INTDIGITOSREDONDEO PCK_SUBTIPOS.TI_ENTERO;
  MI_TIM              PCK_SUBTIPOS.TI_PORCENTAJE;
  MI_FECHAINICIO1     DATE;
  MI_INTDIAS          PCK_SUBTIPOS.TI_ENTERO;
  MI_INTMES           PCK_SUBTIPOS.TI_ENTERO;
  MI_INTANIO          PCK_SUBTIPOS.TI_ENTERO;
  MI_PAGO             PCK_SUBTIPOS.TI_DOBLE;
  MI_DIFERENCIA       PCK_SUBTIPOS.TI_DOBLE;
  MI_DIFERENCIACUOTA  NUMBER(20,4);
  MI_SUMACAPITAL      PCK_SUBTIPOS.TI_DOBLE;
  MI_SUMACUOTAS       PCK_SUBTIPOS.TI_DOBLE;
  MI_SUMAINTERESES    PCK_SUBTIPOS.TI_DOBLE;
  MI_CUOTA            PCK_SUBTIPOS.TI_ENTERO;
  MI_CAPITAL          PCK_SUBTIPOS.TI_DOBLE;
  MI_INT_FINANCIACION PCK_SUBTIPOS.TI_DOBLE;
  MI_CONDICION        PCK_SUBTIPOS.TI_CAMPOS;
  MI_ERROR            PCK_SUBTIPOS.TI_CLAVEVALOR; 
  MI_VALORES          PCK_SUBTIPOS.TI_CAMPOS; 
  MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS; 
  MI_ACME             PCK_SUBTIPOS.TI_ENTERO; 
  MI_INTFINANCIACION  PCK_SUBTIPOS.TI_DOBLE;
  MI_ESTADO           PCK_SUBTIPOS.TI_LOGICO;
  MI_TCAPITAL         PCK_SUBTIPOS.TI_DOBLE;
  MI_TINTERES         PCK_SUBTIPOS.TI_DOBLE;
  MI_CUOTAMAX         PCK_SUBTIPOS.TI_ENTERO;
BEGIN

  EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS=''.,''';
   MI_INTDIGITOSREDONDEO:= 0;       
   --'Se almacena en la variable nc el número de cuotas del acuerdo de pago
   --'Almacena en la variable Vm (Valor Monto) el valor capital de la deuda la cual será la base para calcular los interes de financiación
   MI_VM    := UN_MONTO;-- ' - MontoInteres
   MI_SALDO := MI_VM;--   ' reasigna el valor del saldo teniendo en cuenta que al monto de la deuda se le resta el monto de intereses

   MI_TI :=CASE WHEN UN_TASAINTERES = 0 THEN  0.0001 ELSE UN_TASAINTERES END;

      
   MI_TI := MI_TI /12;

   MI_TIM := MI_TI /100;
      --'=REDONDEAR(C27*((((1+C28)^C29)*C28)/(((1+C28)^C29)-1)),0)
      
      

   MI_PAGO:= MI_SALDO * (((POWER(1 + MI_TIM,UN_NCUOTAS)) * MI_TIM) / ((POWER(1 + MI_TIM,UN_NCUOTAS)) - 1));

   MI_NDCS          := 1;
   MI_NUEVACUOTA    := 1;
   MI_SUMACAPITAL   := 0;
   MI_SUMACUOTAS    := 0;
   MI_SUMAINTERESES := 0;
   <<BUCLECUOTAS>>

  WHILE TRUE LOOP

       MI_FECHAINICIO:=ADD_MONTHS(TO_DATE(UN_PRIMERFECHA,'DD/MM/YYYY HH24:MI:SS'),MI_NDCS);
       MI_FECHAINICIO1:=MI_FECHAINICIO;
       MI_XX:=0;
       IF MI_FECHAINICIO <= UN_FECHADECORTE Then
             --determina la antigudad de la deuda de esta cuota
             --si los dias son mas de diez toma el mes completo.            
             MI_XX:=MONTHS_BETWEEN(UN_FECHADECORTE,MI_FECHAINICIO1);
       END IF;

          MI_VALINT := MI_SALDO * MI_TIM;

          --Tiene en cuenta el abono
          MI_VALCUO       := MI_PAGO - PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR    =>MI_VALINT,
                                                               UN_PRECISION=>0);
          MI_SALDO        := MI_SALDO - MI_VALCUO; --calcula el nuevo saldo del monto capital
          MI_SUMAINTERESES:= MI_SUMAINTERESES + PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR    =>MI_VALINT,
                                                                        UN_PRECISION=>0);
          MI_SUMACAPITAL  := MI_SUMACAPITAL + MI_VALCUO;
          MI_SUMACUOTAS   := MI_SUMACUOTAS + MI_VALCUO + PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR    =>MI_VALINT,
                                                                                 UN_PRECISION=>0);
          BEGIN
             BEGIN

                     MI_CAMPOS:='COMPANIA,'||
                                'TIPO_AP,'||
                                'CODIGO_AP,'||
                                'CUOTA,'||
                                'FECHACUOTA,'||
                                'CAPITAL,'||
                                'INTERES,'||
                                'INT_FINANCIACION,'||
                                'INT_RECARGO,'||
                                'TOTAL_CUOTA,'||
                                'ESTADO,'||
                                'CREATED_BY,'||
                                'DATE_CREATED';

                      MI_VALORES:=''''||UN_COMPANIA||''','||
                                  ''''||UN_TIPOABONO||''','||
                                  UN_NROABONO||','||
                                  TO_CHAR(MI_NDCS)||','||
                                  'TO_DATE('''||MI_FECHAINICIO||''',''DD/MM/YYYY HH24:MI:SS''),'||
                                  MI_VALCUO||','||
                                  '0,'||
                                  TO_CHAR(PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR    =>MI_VALINT,
                                                                  UN_PRECISION=>0))||','||
                                  '0'||','||
                                  TO_CHAR(PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR    =>MI_VALCUO+ MI_VALINT,
                                                                  UN_PRECISION=>0))||','||
                                  '''A'''||','||
                                  ''''||UN_USUARIO||''','||
                                  'SYSDATE';

                      MI_ACME:=PCK_DATOS.FC_ACME(UN_ACCION  =>'I',
                                                 UN_TABLA   =>'SF_TEMP_DETALLE_ACUERDO',
                                                 UN_CAMPOS  =>MI_CAMPOS,
                                                 UN_VALORES =>MI_VALORES); 


                       EXCEPTION
                                 WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                    END;
                 EXCEPTION
                      WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                           MI_ERROR(1).CLAVE := 'TABLA';
                           MI_ERROR(1).VALOR := 'SF_TEMP_DETALLE_ACUERDO';
                           MI_ERROR(2).CLAVE := 'CAMPOS';
                           MI_ERROR(2).VALOR :=  MI_CAMPOS;
                           PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                      UN_TABLAERROR => 'SF_TEMP_DETALLE_ACUERDO',
                                                      UN_ERROR_COD  => PCK_ERRORES.ERR_PR_CALCULARM_INSERTAR,
                                                      UN_REEMPLAZOS => MI_ERROR);
                END;

       MI_NDCS := MI_NDCS + 1;     

    IF MI_NDCS =  (UN_NCUOTAS+1) THEN       
        EXIT;  
    END IF;    
   END LOOP BUCLECUOTAS;

    MI_DIFERENCIA      := (MI_VM - MI_SUMACAPITAL)/UN_NCUOTAS;
    MI_DIFERENCIACUOTA := (MI_VM - (MI_SUMACUOTAS - MI_SUMAINTERESES))/UN_NCUOTAS;


   FOR MI_RS IN (SELECT CUOTA,
                         CAPITAL,
                         INT_FINANCIACION
                  FROM  SF_TEMP_DETALLE_ACUERDO
                  WHERE COMPANIA    = UN_COMPANIA
                    AND TIPO_AP  = UN_TIPOABONO
                    AND CODIGO_AP= UN_NROABONO
                    AND CUOTA NOT IN (0) ) LOOP

            BEGIN
                BEGIN              

                  MI_CAPITAL := MI_RS.CAPITAL + MI_DIFERENCIA;

                  MI_INT_FINANCIACION := MI_RS.INT_FINANCIACION - MI_DIFERENCIACUOTA; 

                 MI_CAMPOS:='CAPITAL          ='||MI_CAPITAL||','||
                            'INT_FINANCIACION ='||MI_INT_FINANCIACION||','||
                            'TOTAL_CUOTA      = '|| (MI_CAPITAL+ MI_INT_FINANCIACION) ||', '||
                            'MODIFIED_BY      ='''||UN_USUARIO||''','||
                            'DATE_MODIFIED    = SYSDATE';


                 MI_CONDICION:='COMPANIA     ='''||UN_COMPANIA||''''||
                          ' AND TIPO_AP   ='''||UN_TIPOABONO||''''||
                          ' AND CODIGO_AP ='||UN_NROABONO||
                          ' AND CUOTA        ='||MI_RS.CUOTA;                               


                 MI_ACME:=PCK_DATOS.FC_ACME(UN_ACCION    =>'M',
                                            UN_TABLA     =>'SF_TEMP_DETALLE_ACUERDO',
                                            UN_CAMPOS    =>MI_CAMPOS,
                                            UN_CONDICION =>MI_CONDICION);

                     EXCEPTION
                          WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                         RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                       END;
                EXCEPTION
                     WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                          MI_ERROR(1).CLAVE := 'TABLA';
                          MI_ERROR(1).VALOR := 'SF_TEMP_DETALLE_ACUERDO';
                          MI_ERROR(2).CLAVE := 'CAMPOS';
                          MI_ERROR(2).VALOR :=  MI_CAMPOS;
                          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                     UN_TABLAERROR => 'SF_TEMP_DETALLE_ACUERDO',
                                                     UN_ERROR_COD  => PCK_ERRORES.ERR_PR_CALCULARM_ACTUALIZAR,
                                                     UN_REEMPLAZOS => MI_ERROR);
              END;

     END LOOP;



      SELECT SUM(CAPITAL),
             SUM(INT_FINANCIACION)
               INTO 
               MI_TCAPITAL,
               MI_TINTERES
        FROM SF_TEMP_DETALLE_ACUERDO
        WHERE COMPANIA   = UN_COMPANIA
        AND TIPO_AP   = UN_TIPOABONO
        AND CODIGO_AP = UN_NROABONO
        AND CUOTA <> 0;

         SELECT MAX(CUOTA)                       
               INTO 
               MI_CUOTAMAX
        FROM SF_TEMP_DETALLE_ACUERDO
        WHERE COMPANIA   = UN_COMPANIA
        AND TIPO_AP   = UN_TIPOABONO
        AND CODIGO_AP = UN_NROABONO
        AND CUOTA <> 0;

        MI_DIFERENCIA       := (MI_VM - MI_TCAPITAL);

        IF ABS(MI_DIFERENCIA) > 0.001 THEN
            MI_CAMPOS:='CAPITAL          = CAPITAL +'||MI_DIFERENCIA||','||
                        'INT_FINANCIACION = INT_FINANCIACION - '||MI_DIFERENCIA||','||
                        'TOTAL_CUOTA      = ( CAPITAL +'|| MI_DIFERENCIA||') + (INT_FINANCIACION - 
                         '|| MI_DIFERENCIA ||')' ||', '||
                        'MODIFIED_BY      ='''||UN_USUARIO||''','||
                        'DATE_MODIFIED    = SYSDATE';


             MI_CONDICION:='COMPANIA     ='''||UN_COMPANIA||''''||
                            ' AND TIPO_AP   ='''||UN_TIPOABONO||''''||
                            ' AND CODIGO_AP ='||UN_NROABONO||
                            ' AND CUOTA        ='||MI_CUOTAMAX;                               


             MI_ACME:=PCK_DATOS.FC_ACME(UN_ACCION    =>'M',
                                        UN_TABLA     =>'SF_TEMP_DETALLE_ACUERDO',
                                        UN_CAMPOS    =>MI_CAMPOS,
                                        UN_CONDICION =>MI_CONDICION);
        END IF;



END PR_CALCULARACUERDOPAGO;

END PCK_FACT_GENERAL_ACUERDOS;