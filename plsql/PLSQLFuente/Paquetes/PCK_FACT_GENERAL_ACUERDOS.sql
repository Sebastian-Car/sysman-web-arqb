create or replace PACKAGE PCK_FACT_GENERAL_ACUERDOS AS 

  /* TODO enter package declarations (types, exceptions, methods etc) here */ 


  --1
  FUNCTION FC_PREPARARDEUDA_ACUERDOSPAGO 
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA, --Codigo de la compania desde la que se inicio sesion.
    UN_ACUERDONRO   IN VARCHAR2,
    UN_SELECCION    IN VARCHAR2,                 --Codigos de facturas seleccionadas, separadas por coma.
    UN_TIPOCOBRO    IN SF_FACTURA.TIPOCOBRO%TYPE --Tipo de cobro seleccionado al ingresar al modulo.
  )
  RETURN VARCHAR2;

  --2
  PROCEDURE PR_APLICARCONDONACION
  (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,            --> Codigo de la compania.
    UN_TIPOCOBRO      IN SF_DETALLE_FACTURA.TIPO_FACTURA%TYPE,--> Codigo tipo de cobro.
    UN_VALORCONDONADO IN PCK_SUBTIPOS.TI_DOBLE,               --> Valor condonado.
    UN_SELECCION_FACT IN VARCHAR2                             --> Numeros de factura separados por coma.
  );  

  --3
  PROCEDURE PR_ELIMINARACUERDOPAGO 
  (
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,        --> Codigo de la compania.
    UN_TIPOCOBRO  IN SF_TEMP_ACUERDO_PAGO.TIPO%TYPE,  --> Tipo de cobro.
    UN_ACUERDONRO IN SF_TEMP_ACUERDO_PAGO.CODIGO%TYPE --> Numero de acuerdo.
  );   

  --4
  PROCEDURE PR_CALCULARMENSUALIDAD 
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
    UN_USUARIO       IN PCK_SUBTIPOS.TI_USUARIO --> Codigo del usuario que desencadena el proceso.
  );    

  --5  
  PROCEDURE PR_DISTRIBUIRACUERDO 
  (
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,            --> Codigo de la compania.
    UN_TIPOACUERDO IN SF_DETALLE_FACTURA.TIPO_FACTURA%TYPE,--> Codigo tipo de cobro. 
    UN_ACUERDO     IN SF_ACUERDO_PAGO.CODIGO%TYPE,         --> Numero del acuerdo.
    UN_TIPOFACTURA IN SF_DETALLE_FACTURA.TIPO_FACTURA%TYPE,--> Tipo factura.
    UN_LISFACTURAS IN PCK_SUBTIPOS.TI_RTA_ACME,            --> Numeros de factura separadas por coma.
    UN_TERCERO     IN TERCERO.NIT%TYPE,                    --> Codigo del tercero.
    UN_SUCURSAL    IN TERCERO.SUCURSAL%TYPE,               --> Codigo de la sucursal asociada al tercero.
    UN_ANIO        IN PCK_SUBTIPOS.TI_ANIO,   --> Anio del tipo de cobro.
    UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO  --> Usuario que desencadena el proceso.
  );  

  --6  
  FUNCTION FC_GENERARACUERDOPRELIMINAR 
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
    UN_IND_SIMPLE      IN PCK_SUBTIPOS.TI_LOGICO             DEFAULT 0, -->Indicador simple.
    UN_GRADIANTE       IN PCK_SUBTIPOS.TI_PORCENTAJE         DEFAULT 0, --> Tasa Gradiante
    UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO                       --> Codigo del usuario que desencadena el proceso.
  )
  RETURN VARCHAR2;  

  --7
  PROCEDURE PR_VERIFICARDISTRIBUCION 
  (
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,          -- Codigo de la compania
    UN_TIPOACUERDO IN SF_DETALLE_FACTURA.TIPOCOBRO%TYPE, -- Tipo de cobro seleccionado al iniciar el modulo de facturacion general.
    UN_ACUERDO     IN SF_ACUERDO_PAGO.CODIGO%TYPE        -- Numero del acuerdo.
  );  

  --8
  FUNCTION FC_ENUMERARACUERDO 
  (
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA, -- Codigo de la compania.
    UN_TIPOCOBRO  IN SF_FACTURA.TIPOCOBRO%TYPE -- Tipo de cobro seleccionado al ingresar al modulo.
  )
  RETURN NUMBER;   

  --9
  PROCEDURE PR_APROBARACUERDO 
  (
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,         -- Codigo de la compania.
    UN_TIPOCOBRO  IN SF_FACTURA.TIPOCOBRO%TYPE,        -- Tipo de cobro seleccionado al ingresar al modulo.
    UN_ACUERDONRO IN SF_TEMP_ACUERDO_PAGO.CODIGO%TYPE, -- Numero de acuerdo preliminar.
    UN_SELECCION  IN VARCHAR2,                         -- Facturas con las que se preparo la deuda.
    UN_USUARIO    IN PCK_SUBTIPOS.TI_USUARIO           -- Codigo del usuario que desencadena el proceso.
  );  

  --10
  PROCEDURE PR_ELIMINARACUERDOPAGO_TER 
  (
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,        -- Codigo de la compania.
    UN_TIPOCOBRO  IN SF_TEMP_ACUERDO_PAGO.TIPO%TYPE,  -- Tipo de cobro.
    UN_TERCERO    IN TERCERO.NIT%TYPE,                -- Codigo del tercero.
    UN_SUCURSAL   IN TERCERO.SUCURSAL%TYPE            -- Codigo de la sucursal asociada al tercero.
  );

  --11
  PROCEDURE PR_FACTURARACUERDO
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,        --> Compania de Ingreso a la aplicacion
    UN_ANIOCOBRO    IN PCK_SUBTIPOS.TI_ANIO,            --> Anio de trabajo seleccionado al ingresar al modulo de Facturacion General
    UN_TIPOCOBRO    IN PCK_SUBTIPOS.TI_TIPO_COBRO,      --> Tipo de cobro seleccionado al ingresar al modulo de Facturacion General
    UN_TIPOACUERDO  IN SF_DETALLE_ACUERDO.TIPO_AP%TYPE, --> Tipo de Acuerdo que se desea facturar
    UN_NROACUERDO   IN PCK_SUBTIPOS.TI_LONG,            --> Numero de Acuerdo de Pago a facturar
    UN_CUOTA        IN SF_DETALLE_ACUERDO.CUOTA%TYPE,   --> Cuota perteneciente al Acuerdo de pago a facturar
    UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO          --> Usuario que ejecuta el proceso
  );

PROCEDURE PR_CALCULARACUERDOPAGO(
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
);

END PCK_FACT_GENERAL_ACUERDOS;