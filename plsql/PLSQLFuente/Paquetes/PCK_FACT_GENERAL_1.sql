CREATE OR REPLACE PACKAGE BODY PCK_FACT_GENERAL AS

--1

FUNCTION FC_CONCEPTOAFINVENTARIO
/*
      NAME              : FC_CONCEPTOAFINVENTARIO 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 10/11/2017
      TIME              : 10:30 AM
      SOURCE MODULE     : SysmanSF2017.11.01
      DESCRIPTION       : metodo ConceptoAfInventario();
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 

    @NAME:   validarConceptoAfectaInventario
    @METHOD: GET
*/
(
  UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANO                IN PCK_SUBTIPOS.TI_ENTERO,
  UN_TIPOCOBRO          IN PCK_SUBTIPOS.TI_TIPO_COBRO,
  UN_CONCEPTO           IN SF_CONCEPTOS.CODIGO%TYPE
)RETURN PCK_SUBTIPOS.TI_LOGICO 
  AS
  MI_VALOR            PCK_SUBTIPOS.TI_LOGICO;

BEGIN

    SELECT NVL(AFECTAINVENTARIO,0)
      INTO MI_VALOR
      FROM SF_CONCEPTOS
    WHERE COMPANIA  = UN_COMPANIA
      AND ANO       = UN_ANO
      AND TIPOCOBRO = UN_TIPOCOBRO
      AND CODIGO    = UN_CONCEPTO;


    IF MI_VALOR = 0 THEN
    RETURN 0;

    ELSE

    RETURN -1;

    END IF;


END FC_CONCEPTOAFINVENTARIO;

--2
FUNCTION FC_MANEJAINVENTARIO
(
  	/*
      NAME              : FC_MANEJAINVENTARIO 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
      DATE MIGRADOR     : 10/11/2017
      TIME              : 5:29 PM
      SOURCE MODULE     : SysmanSF2017.11.01
      DESCRIPTION       : Función que valida el indicador maneja inventario de la tabla SP_TIPO_COBRO,
                          retorna valor de este campo de la tabla según datos suministrados (Verdadero o falso)
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 

      PARAMETERS        : 	UN_COMPANIA 	=> Compania que ingreso a la aplicación
                            UN_ANO  	    => Ano seleccionado del formulario 
                            UN_TIPOCOBRO 	=> Tipo de cobro seleccionado al ingresar al módulo facturación general

    @NAME:   validarManejaInventario
    @METHOD: GET   
    */

  UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANO        IN PCK_SUBTIPOS.TI_ANIO,
  UN_TIPOCOBRO  IN PCK_SUBTIPOS.TI_TIPO_COBRO
)
RETURN PCK_SUBTIPOS.TI_LOGICO
  AS 
    MI_MANEJAINVENTARIO  PCK_SUBTIPOS.TI_LOGICO;

  BEGIN 
    BEGIN 
      SELECT MANEJA_INVENTARIO
      INTO MI_MANEJAINVENTARIO
      FROM SF_TIPO_COBRO
      WHERE COMPANIA  = UN_COMPANIA
        AND ANO       = UN_ANO
        AND CODIGO    = UN_TIPOCOBRO;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_MANEJAINVENTARIO := 0;
    END;    

    RETURN MI_MANEJAINVENTARIO;

END FC_MANEJAINVENTARIO;

--3
FUNCTION FC_AFECTARINVENTARIO

	/*
      NAME              : PR_AFECTARINVENTARIO 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 10/11/2017
      TIME              : 10:30 AM
      SOURCE MODULE     : SysmanSF2017.11.01
      DESCRIPTION       : Evento CmdAceptar_Click()
      MODIFIER          : ELKIN GEOVANNY AMAYA SILVA
      DATE MODIFIED     : 08/11/2018
      TIME MODIFIED     : 14:00 
      MODIFICATIONS     : 

      PARAMETERS        : 	UN_COMPANIA 	    => Compania que ingreso a la aplicación
                            UN_TIPOASOCIADO   => Tipo de factura seleccionada en la vista
                            UN_NUMEROASOCIADO => Numero de factura seleccionada en la vista
                            UN_TIPOMOVIMIENTO => Tipo de movimiento seleccionado en la vista
                            UN_FECHAMOV       => La fecha actual en la que se realizara el movimiento
                            UN_DEPENDENCIA    => Dependencia seleccionada en la vista 
                            UN_TIPOCOBRO 	    => Tipo de cobro seleccionado al ingresar al módulo facturación general
                            UN_BODEGAORIGEN   => Clase de bodega de origen al seleccionar el tipo de movimiento
                            UN_BODEGADESTINO  => Clase de bodega de destino al seleccionar el tipo de movimiento
                            UN_USUARIO  	    => Usuario que ingreso a la aplicación

    @NAME:   afectarInventario
    @METHOD: PUT   
    */

	(
  UN_COMPANIA 	      IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_TIPOASOCIADO   	IN SF_FACTURA.TIPO_FACTURA%TYPE,
	UN_NUMEROASOCIADO   IN SF_FACTURA.NUMERO_FACTURA%TYPE,
  UN_TIPOMOVIMIENTO   IN MOVIMIENTO.TIPOMOVIMIENTO%TYPE,
  UN_FECHAMOV         IN DATE,
  UN_DEPENDENCIA      IN MOVIMIENTO.DEPENDENCIA_DESTINO%TYPE,
  UN_TIPOCOBRO        IN PCK_SUBTIPOS.TI_TIPO_COBRO,
  UN_BODEGAORIGEN     IN MOVIMIENTO.BODEGA_ORIGEN%TYPE,
  UN_BODEGADESTINO    IN MOVIMIENTO.BODEGA_DESTINO%TYPE,
  UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
  )
  RETURN PCK_SUBTIPOS.TI_LONG
  AS

  MI_NUMEROMOVIMIENTO NUMBER;
  MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
  MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONTADOR         NUMBER;
  MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
  MI_RS               SYS_REFCURSOR;
  MI_CONCEPTOINV      PCK_SUBTIPOS.TI_LOGICO;
  MI_CODIGO           NUMBER;
  MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN

  MI_NUMEROMOVIMIENTO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'MOVIMIENTO',
                                                          UN_CRITERIO => 'COMPANIA = '''||UN_COMPANIA||''' AND TIPOMOVIMIENTO = '''||UN_TIPOMOVIMIENTO||'''',
                                                          UN_CAMPO    => 'NUMERO');

  IF MI_NUMEROMOVIMIENTO = 1 THEN

  MI_NUMEROMOVIMIENTO := TO_CHAR(SYSDATE,'YYYY') ||LPAD(MI_NUMEROMOVIMIENTO,6,'0');
  END IF;

    MI_CAMPOS := 'COMPANIA
                 ,TIPOMOVIMIENTO
                 ,NUMERO
                 ,TERCERO
                 ,SUCURSAL
                 ,DEPENDENCIA_DESTINO
                 ,FECHA
                 ,DESCRIPCION
                 ,TIPOMOVASOCIADO
                 ,MOVASOCIADO
                 ,FECHAMOVASOCIADO
                 ,BODEGA_ORIGEN
                 ,BODEGA_DESTINO
                 ,CLASE_BODEGA_ORIGEN
                 ,CLASE_BODEGA_DESTINO
                 ,CREATED_BY
                 ,DATE_CREATED';  

  MI_VALORES := 'SELECT COMPANIA
                     ,'''||UN_TIPOMOVIMIENTO||'''
                     ,'||MI_NUMEROMOVIMIENTO||'
                     ,TERCERO
                     ,SUCURSAL
                     ,'''||UN_DEPENDENCIA||'''
                     ,SYSDATE
                     ,OBSERVACIONES
                     ,TIPO_MOV
                     ,NUMERO_FACTURA
                     ,FECHA_EXPEDICION
                     ,'''||UN_BODEGAORIGEN||'''
                     ,'''||UN_BODEGADESTINO||'''
                     ,'''||UN_BODEGAORIGEN||'''
                     ,'''||UN_BODEGADESTINO||'''
                     ,'''||UN_USUARIO||'''
                     ,SYSDATE
                  FROM SF_FACTURA
                  WHERE COMPANIA       = '''||UN_COMPANIA||'''
                    AND TIPO_FACTURA   = '''||UN_TIPOASOCIADO||'''
                    AND NUMERO_FACTURA = '||UN_NUMEROASOCIADO||' ';

   BEGIN
    BEGIN
     PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'MOVIMIENTO'
                                            ,UN_ACCION  => 'IS'
                                            ,UN_CAMPOS  => MI_CAMPOS
                                            ,UN_VALORES => MI_VALORES);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;

    END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_MOVIMIENTO
      );

  END;  

    SELECT COUNT(*)
    INTO MI_CONTADOR
    FROM SF_DETALLE_FACTURA
    WHERE COMPANIA     = UN_COMPANIA
      AND TIPO_FACTURA = UN_TIPOASOCIADO
      AND FACTURA      = UN_NUMEROASOCIADO;

  IF MI_CONTADOR  < 1 THEN

  MI_CONDICION := 'COMPANIA       = '''||UN_COMPANIA||''' 
              AND  TIPOMOVIMIENTO = '''||UN_TIPOMOVIMIENTO||''' 
              AND  NUMERO         = '||MI_NUMEROMOVIMIENTO||' ';


  BEGIN
   BEGIN
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      =>'MOVIMIENTO'
                                             ,UN_ACCION    => 'E'
                                             ,UN_CONDICION => MI_CONDICION);

       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;

   END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
          MI_MSGERROR(1).CLAVE := 'MOVIMIENTO';
          MI_MSGERROR(1).VALOR := MI_NUMEROMOVIMIENTO;
            PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD      =>SQLCODE
            ,UN_ERROR_COD   =>PCK_ERRORES.ERR_SYSMANSF_MOVIMIENTO_ELI
            ,UN_REEMPLAZOS  => MI_MSGERROR
          );
  END; 

  END IF;

   <<RECORRER_DETALLES>>

  MI_CODIGO := 1; 

    FOR MI_RS 
    IN (
         SELECT ANO,
          CONCEPTO,
          CENTRO_COSTO,
          CANTIDAD,
          VALOR_COMPRA,
          TERCERO,
          SUCURSAL
        FROM SF_DETALLE_FACTURA
        WHERE COMPANIA   = UN_COMPANIA
        AND TIPO_FACTURA = UN_TIPOASOCIADO
        AND FACTURA      = UN_NUMEROASOCIADO
    )
    LOOP

  MI_CONCEPTOINV := PCK_FACT_GENERAL.FC_CONCEPTOAFINVENTARIO(UN_COMPANIA   => UN_COMPANIA
                                                             ,UN_ANO       => MI_RS.ANO
                                                             ,UN_TIPOCOBRO => UN_TIPOCOBRO
                                                             ,UN_CONCEPTO  => MI_RS.CONCEPTO);

  IF MI_CONCEPTOINV = 0 THEN

  CONTINUE;

  ELSE

    MI_CAMPOS := 'COMPANIA,
                  TIPOMOVIMIENTO,
                  MOVIMIENTO,
                  CODIGO,
                  ELEMENTO,
                  CENTRODECOSTO,
                  CANTIDAD,
                  SALDOCANT,
                  VALORUNITARIO,
                  VALORTOTAL,
                  IND_REG,
                  FECHA,
                  HORA,
                  ORDENDESUMINISTRO,
                  MARCA,
                  AJUSTECENTAVOS,
                  CANTIDADDOCAS,
                  VALORDOCAS,
                  ALMACEN,
                  TERCERO,
                  SUCURSAL,
                  VALORBASE,
                  VALORIVA,
                  PORCIVAUNI';


    MI_VALORES :=''''||UN_COMPANIA||''',
                  '''||UN_TIPOMOVIMIENTO||''',
                  '||MI_NUMEROMOVIMIENTO||',
                  '||MI_CODIGO||',
                  '||MI_RS.CONCEPTO||',
                  '||MI_RS.CENTRO_COSTO||',
                  '||MI_RS.CANTIDAD||',
                  '||MI_RS.CANTIDAD||',
                  '||MI_RS.VALOR_COMPRA/MI_RS.CANTIDAD||',
                  '||MI_RS.VALOR_COMPRA||',
                  0,
                  SYSDATE,
                  SYSDATE,
                  0,
                  '',
                  0,
                  '||MI_RS.CANTIDAD||',
                  '||MI_RS.VALOR_COMPRA||',
                  ''001'',
                  '||MI_RS.TERCERO||',
                  '||MI_RS.SUCURSAL||',
                  '||NVL(MI_RS.VALOR_COMPRA,0)||',
                  0,
                  0';

  BEGIN
    BEGIN
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'D_MOVIMIENTO'
                                                ,UN_ACCION  => 'I'
                                                ,UN_CAMPOS  => MI_CAMPOS
                                                ,UN_VALORES => MI_VALORES);

     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;

    END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_DMOV_INSERT
      );

  END;  

  END IF;


  MI_CODIGO := MI_CODIGO + 1 ;

  END LOOP RECORRER_DETALLES;


  MI_CAMPOS := 'AFECTO_INVENTARIO = -1
                ,TIPO_MOV         = '''||UN_TIPOMOVIMIENTO||'''
                ,NRO_MOV          = '||MI_NUMEROMOVIMIENTO||'
                ,MODIFIED_BY      = '''||UN_USUARIO||'''
                ,DATE_MODIFIED    = SYSDATE ';

  MI_CONDICION := 'COMPANIA      = '''||UN_COMPANIA ||'''
              AND TIPO_FACTURA   = '''||UN_TIPOASOCIADO||''' 
              AND NUMERO_FACTURA = '||UN_NUMEROASOCIADO|| ' ';

  BEGIN
    BEGIN

     PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  'SF_FACTURA', 
                                   UN_ACCION    =>  'M', 
                                   UN_CAMPOS    =>  MI_CAMPOS,
                                   UN_CONDICION =>  MI_CONDICION);  

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;

    END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
        MI_MSGERROR(1).CLAVE := 'NUMERO';
        MI_MSGERROR(1).VALOR := UN_NUMEROASOCIADO;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD =>SQLCODE,
            UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_AFECT_INVENT,
            UN_REEMPLAZOS  => MI_MSGERROR
          );

  END;    
  RETURN MI_NUMEROMOVIMIENTO;
END FC_AFECTARINVENTARIO;

--4
PROCEDURE PR_ELIMINAR_RECAUDO
/*
    NAME              : PR_ELIMINAR_RECAUDO  --> En access CmdAceptar_Click
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
    DATE MIGRADOR     : 10/11/2017
    TIME              : 10:08 AM  
    SOURCE MODULE     : SysmanSF2017.11.01
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED     : 12/10/2018
    TIME              : 16:20 PM
    MODIFICATIONS     : Adicion del parametro UN_TIPOFACTURA ya que la eliminacion 
                        se realiza para todos los tipos de cobro
            
    MODIFIER          : CARLOS MAURICIO ALARCON CASALLAS
    DATE MODIFIED     : 04/10/2022
    TIME              : 15:20 PM
    MODIFICATIONS     : Adicion de la propiedad MI_PAGADOBANCO. La cual obtendrá el 
                        resultado de una consulta hecha a DETALLE_COMPROBANTE_CNT 
                        para verificar si ese movimiento se encuentra conciliado y posteriormente
                        evaluar el resultado.

    DESCRIPTION       : Realiza el proceso correspondiente para eliminar un recaudo.

      @NAME:    eliminarRecaudo 
      @METHOD:  DELETE
*/
(
  UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOFACTURA              IN SF_FACTURA.TIPO_FACTURA%TYPE,
  UN_NUMEROFACTURA            IN SF_FACTURA.NUMERO_FACTURA%TYPE,
  UN_USUARIO                  IN PCK_SUBTIPOS.TI_USUARIO
)
AS
  MI_DIFERIDA                  SF_FACTURA.DIFERIDA%TYPE; 
  MI_ESACUERDO                 SF_FACTURA.ESACUERDO%TYPE;
  MI_AFECTO_INVENTARIO         SF_FACTURA.AFECTO_INVENTARIO%TYPE; 
  MI_TIPOCPTE_PAGO             SF_FACTURA.TIPOCPTE_PAGO%TYPE; 
  MI_ANOCPTE_PAGO              SF_FACTURA.ANOCPTE_PAGO%TYPE;
  MI_NROCPTE_PAGO              SF_FACTURA.NROCPTE_PAGO%TYPE;

  MI_TIPOCPTE                  SF_FACTURA.TIPO_CPTE%TYPE; 
  MI_ANOCPTE                   SF_FACTURA.ANO_CPTE%TYPE;
  MI_NROCPTE                   SF_FACTURA.NRO_CPTE%TYPE;

  MI_CLASECONTABLE             TIPO_COMPROBANTE.CLASE_CONTABLE%TYPE;
  MI_EXISTE                    PCK_SUBTIPOS.TI_ENTERO;
  MI_CODIGO                    TIPO_COMPROBPP.CODIGO%TYPE;
  MI_CLASEPTO                  TIPO_COMPROBPP.CLASE%TYPE;
  MI_CONDICION                 PCK_SUBTIPOS.TI_CONDICION; 
  MI_CAMPOS                    PCK_SUBTIPOS.TI_CAMPOS;
  MI_CODIGO_COBRO              SF_FACTURA.CODIGO_COBRO%TYPE;
  MI_PAGADOBANCO               PCK_SUBTIPOS.TI_ENTERO;

BEGIN

  SELECT  DIFERIDA, 
          ESACUERDO, 
          AFECTO_INVENTARIO, 
          TIPOCPTE_PAGO, 
          ANOCPTE_PAGO,
          NROCPTE_PAGO,
          TIPO_CPTE, 
          ANO_CPTE,
          NRO_CPTE,
          CODIGO_COBRO
  INTO 
          MI_DIFERIDA, 
          MI_ESACUERDO, 
          MI_AFECTO_INVENTARIO, 
          MI_TIPOCPTE_PAGO, 
          MI_ANOCPTE_PAGO,
          MI_NROCPTE_PAGO,
          MI_TIPOCPTE, 
          MI_ANOCPTE,
          MI_NROCPTE,
          MI_CODIGO_COBRO
  FROM SF_FACTURA
  WHERE SF_FACTURA.COMPANIA       = UN_COMPANIA
    AND SF_FACTURA.TIPO_FACTURA   = UN_TIPOFACTURA
    AND SF_FACTURA.NUMERO_FACTURA = UN_NUMEROFACTURA;


  IF MI_DIFERIDA <> 0 THEN
    BEGIN
      RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_SYSMANSF THEN    
                      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   =>  SQLCODE,
                                                 UN_ERROR_COD =>  PCK_ERRORES.ERR_SYSMANSF_FACTDIFERIDA
                                                 );
    END;
  END IF;


  IF MI_ESACUERDO <> 0 OR  MI_AFECTO_INVENTARIO <> 0 THEN
    BEGIN
      RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_SYSMANSF THEN    
                      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   =>  SQLCODE,
                                                 UN_ERROR_COD =>  PCK_ERRORES.ERR_SYSMANSF_FACTACUERDOPAG
                                                 );
    END;
  END IF;

  BEGIN
   SELECT CLASE_CONTABLE 
   INTO   MI_CLASECONTABLE
   FROM   TIPO_COMPROBANTE 
   WHERE  COMPANIA = UN_COMPANIA
     AND  CODIGO   = MI_TIPOCPTE_PAGO;
   EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_CLASECONTABLE:='';
  END;


  SELECT COUNT(1) EXISTE
  INTO MI_EXISTE
  FROM    DETALLE_COMPROBANTE_CNT 
  WHERE   COMPANIA    = UN_COMPANIA
    AND   ANO         = MI_ANOCPTE_PAGO 
    AND   TIPO_CPTE   = MI_TIPOCPTE_PAGO
    AND   COMPROBANTE = MI_NROCPTE_PAGO;

  IF MI_EXISTE > 0 THEN
  
	  SELECT COUNT(1) EXISTE_PAGADOBANCO
	      INTO MI_PAGADOBANCO
	      FROM    DETALLE_COMPROBANTE_CNT 
	      WHERE   COMPANIA    = UN_COMPANIA
	        AND   ANO         = MI_ANOCPTE_PAGO 
	        AND   TIPO_CPTE   = MI_TIPOCPTE_PAGO
	        AND   COMPROBANTE = MI_NROCPTE_PAGO
	        AND   PAGADOBANCO = -1
	        AND   NATURALEZA  = 'D';
	        
	          IF MI_PAGADOBANCO <> 0 THEN
	            BEGIN
	              RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
	                EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_SYSMANSF THEN    
	                              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   =>  SQLCODE,
	                                                         UN_ERROR_COD =>  PCK_ERRORES.ERR_SYSMANSF_EXIS_PAGADOBANC
	                              );
	            END;
	          END IF;
	          
      SELECT COUNT(1) EXISTE
        INTO MI_EXISTE
      FROM   CLASECNT 
      WHERE  CODIGO = MI_CLASECONTABLE;

      IF MI_EXISTE > 0 THEN
         SELECT DISTINCT TIPO_COMPROBPP_1.CODIGO, 
                TIPO_COMPROBPP.CLASE CLASEPTO
         INTO   MI_CODIGO,
                MI_CLASEPTO
         FROM (TIPO_COMPROBPP 
         LEFT JOIN CLASECNTPRES 
          ON TIPO_COMPROBPP.CLASE = CLASECNTPRES.CODIGO) 
          LEFT JOIN TIPO_COMPROBPP TIPO_COMPROBPP_1 
            ON CLASECNTPRES.CLASEAFECTAR = TIPO_COMPROBPP_1.CLASE
         WHERE TIPO_COMPROBPP.CODIGO=MI_TIPOCPTE_PAGO
         ORDER BY TIPO_COMPROBPP_1.CODIGO;

         IF NVL(MI_CODIGO,' ') =' ' THEN
             SELECT COUNT(1) EXISTE
             INTO   MI_EXISTE 
             FROM     COMPROBANTE_PPTAL 
             WHERE    COMPANIA  = UN_COMPANIA
               AND    ANO       = MI_ANOCPTE_PAGO 
               AND    TIPO      = MI_TIPOCPTE_PAGO 
               AND    NUMERO    = MI_NROCPTE_PAGO 
             ORDER BY COMPANIA, ANO, TIPO, NUMERO;

             IF MI_EXISTE > 0 THEN  
                MI_CAMPOS := 'VALOR_CREDITO   = 0, 
                              VALOR_DEBITO    = 0,
                              DATE_MODIFIED   = SYSDATE,
                              MODIFIED_BY     = '''||UN_USUARIO||'''';

                MI_CONDICION := 'COMPANIA           = ''' || UN_COMPANIA ||''' 
                                  AND ANO           = '   || MI_ANOCPTE_PAGO  ||' 
                                  AND TIPO_CPTE     = ''' || MI_TIPOCPTE_PAGO ||'''   
                                  AND COMPROBANTE   = '   || MI_NROCPTE_PAGO  ||'';
                BEGIN 
                  BEGIN
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  'DETALLE_COMPROBANTE_PPTAL', 
                                                           UN_ACCION    =>  'M', 
                                                           UN_CAMPOS    =>  MI_CAMPOS, 
                                                           UN_CONDICION =>  MI_CONDICION );
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                  RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                  END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN    
                                  PCK_ERR_MSG.RAISE_WITH_MSG(
                                  UN_EXC_COD   => SQLCODE,
                                  UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_DELDETCOMPROBCNT
                               );
                END;



                MI_CONDICION :=   'COMPANIA         = ''' || UN_COMPANIA      ||''' 
                                    AND ANO         = '   || MI_ANOCPTE_PAGO  ||' 
                                    AND TIPO_CPTE   = ''' || MI_TIPOCPTE_PAGO ||'''   
                                    AND COMPROBANTE = '   || MI_NROCPTE_PAGO  ||'';
                BEGIN
                  BEGIN
                   PCK_DATOS.GL_RTA :=PCK_DATOS.FC_ACME(UN_TABLA      =>  'DETALLE_COMPROBANTE_PPTAL',
                                                        UN_ACCION     =>  'E',
                                                        UN_CONDICION  =>  MI_CONDICION );
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                                   RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                  END;
                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN    
                                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                    UN_EXC_COD   => SQLCODE,
                                    UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_DELDETCOMPROBANTE
                                  );
                END;

             END IF;
              MI_CONDICION :=  'COMPANIA      = ''' || UN_COMPANIA      ||''' 
                                AND ANO       = '   || MI_ANOCPTE_PAGO  ||' 
                                AND TIPO      = ''' || MI_TIPOCPTE_PAGO ||'''   
                                AND NUMERO    = '   || MI_NROCPTE_PAGO  ||'';
                BEGIN
                  BEGIN
                   PCK_DATOS.GL_RTA :=PCK_DATOS.FC_ACME(UN_TABLA      =>  'COMPROBANTE_PPTAL',
                                                        UN_ACCION     =>  'E',
                                                        UN_CONDICION  =>  MI_CONDICION );
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                                   RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                  END;
                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN    
                                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                    UN_EXC_COD   => SQLCODE,
                                    UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_DELCOMPROBANTE
                                  );
                END;            
         END IF;
      ELSE
         BEGIN
            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
              EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_SYSMANSF THEN    
                            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   =>  SQLCODE,
                                                       UN_ERROR_COD =>  PCK_ERRORES.ERR_SYSMANSF_BUSCOMPROBANTE
                                                       );
          END;
      END IF;
      --   EliminarCpteCnt

      MI_CONDICION :=   'COMPANIA         = ''' || UN_COMPANIA      ||''' 
                          AND ANO         = '   || MI_ANOCPTE_PAGO  ||' 
                          AND TIPO_CPTE   = ''' || MI_TIPOCPTE_PAGO ||'''   
                          AND COMPROBANTE = '   || MI_NROCPTE_PAGO  ||'';
      BEGIN
        BEGIN
         PCK_DATOS.GL_RTA :=PCK_DATOS.FC_ACME(UN_TABLA      =>  'DETALLE_COMPROBANTE_CNT',
                                              UN_ACCION     =>  'E',
                                              UN_CONDICION  =>  MI_CONDICION );
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                         RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        END;
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN    
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD   => SQLCODE,
                          UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_DELDETCOMPROBCNT
                        );
      END;

       MI_CONDICION :=  'COMPANIA       = ''' || UN_COMPANIA      ||''' 
                          AND ANO       = '   || MI_ANOCPTE_PAGO  ||' 
                          AND TIPO      = ''' || MI_TIPOCPTE_PAGO ||'''   
                          AND NUMERO    = '   || MI_NROCPTE_PAGO  ||'';
        BEGIN
          BEGIN
           PCK_DATOS.GL_RTA :=PCK_DATOS.FC_ACME(UN_TABLA      =>  'COMPROBANTE_CNT',
                                                UN_ACCION     =>  'E',
                                                UN_CONDICION  =>  MI_CONDICION );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                           RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
          END;
           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN    
                            PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD   => SQLCODE,
                            UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_DELCOMPROBCNT
                          );
        END;       


        SELECT COUNT(1) EXISTE
        INTO   MI_EXISTE 
        FROM   DETALLE_COMPROBANTE_CNT 
        WHERE  COMPANIA    = UN_COMPANIA
          AND  ANO         = MI_ANOCPTE 
          AND  TIPO_CPTE   = MI_TIPOCPTE
          AND  COMPROBANTE = MI_NROCPTE;

        IF MI_EXISTE > 0 THEN
          MI_CAMPOS := 'DEBITO_AFECTADO = 0, 
                        CREDITO_AFECTADO = 0,
                        DATE_MODIFIED = SYSDATE,
                        MODIFIED_BY = '''||UN_USUARIO||'''';
          MI_CONDICION := 'COMPANIA           = ''' || UN_COMPANIA ||''' 
                            AND ANO           = '   || MI_ANOCPTE  ||' 
                            AND TIPO_CPTE     = ''' || MI_TIPOCPTE ||'''   
                            AND COMPROBANTE   = '   || MI_NROCPTE  ||'';
          BEGIN 
            BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  'DETALLE_COMPROBANTE_CNT', 
                                                     UN_ACCION    =>  'M', 
                                                     UN_CAMPOS    =>  MI_CAMPOS, 
                                                     UN_CONDICION =>  MI_CONDICION );
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN    
                            PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD   => SQLCODE,
                            UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_DELDETCOMPROBCNT
                         );
          END;
        ELSE
          BEGIN
            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
              EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_SYSMANSF THEN    
                            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   =>  SQLCODE,
                                                       UN_ERROR_COD =>  PCK_ERRORES.ERR_SYSMANSF_COMPCAUSACION
                                                       );
          END;
        END IF;

         MI_CAMPOS := ' FECHA_PAGO = NULL, 
                        BANCO_PAGO = NULL, 
                        ANOCPTE_PAGO = NULL, 
                        TIPOCPTE_PAGO = NULL, 
                        NROCPTE_PAGO = NULL,
                        DATE_MODIFIED = SYSDATE,
                        MODIFIED_BY = '''||UN_USUARIO||'''';
         MI_CONDICION :=  'COMPANIA             = ''' || UN_COMPANIA      ||''' 
                            AND TIPO_FACTURA    = '''||UN_TIPOFACTURA||'''   
                            AND NUMERO_FACTURA  = '   || UN_NUMEROFACTURA ||'';
         BEGIN 
           BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  'SF_FACTURA', 
                                                     UN_ACCION    =>  'M', 
                                                     UN_CAMPOS    =>  MI_CAMPOS, 
                                                     UN_CONDICION =>  MI_CONDICION );
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
           END;
           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN    
                            PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD   => SQLCODE,
                            UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_ACTESTADOFACT
                         );
         END;   


         MI_CAMPOS    := 'INDPAGO   = 0,
                          DATE_MODIFIED = SYSDATE,
                          MODIFIED_BY = '''||UN_USUARIO||'''';

         MI_CONDICION :=  'COMPANIA     = ''' || UN_COMPANIA      ||''' 
                       AND TIPOCOBRO    = '''||UN_TIPOFACTURA||'''   
                       AND CODIGO_COBRO = '   || MI_CODIGO_COBRO ||'';
         BEGIN 
           BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  'SF_OBJETO_COBRO', 
                                                     UN_ACCION    =>  'M', 
                                                     UN_CAMPOS    =>  MI_CAMPOS, 
                                                     UN_CONDICION =>  MI_CONDICION );
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
           END;
           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN    
                            PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD   => SQLCODE,
                            UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_ACTUA_OBJETO_COB
                         );
         END; 

  END IF;
END PR_ELIMINAR_RECAUDO;

  --5
  PROCEDURE PR_REVERSARFACTURACION 
  /*
    NAME              : PR_REVERSARFACTURACION -- CmdAceptar_Click() en Form_FRM_REVERSARFACTURACION en Access.
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : Leydi Milena Cortés Forero
    DATE              : 20/11/2017
    TIME              : 08:23 AM
    SOURCE MODULE     : SysmanSF2017.11.01 
    TIME              :
    DESCRIPTION       : Proceso para reversar las facturas.
    PARAMETERS        : UN_COMPANIA  => Compañia de ingreso a la aplicación
                        UN_ANIO      => Año seleccionado al ingresar al módulo.
                        UN_TIPOCOBRO => Tipo de cobro seleccionado al ingresar al módulo.
                        UN_FACTURA   => Número de factura seleccionada que se desea reversar.
                        UN_CODCOBRO  => Número de cobro de la factura a reversar.
                        UN_USUARIO   => Usuario que realiza el registro.
    @NAME:  reversarFacturacion
    @METHOD:  POST 

  */
  (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPOCOBRO      IN SF_OBJETO_COBRO.TIPOCOBRO%TYPE,
    UN_FACTURA        IN SF_FACTURA.NUMERO_FACTURA%TYPE,
    UN_CODCOBRO       IN SF_OBJETO_COBRO.CODIGO_COBRO%TYPE,
    UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS 
    MI_INDPAGO        PCK_SUBTIPOS.TI_LOGICO;
    MI_NUM_INTREC     PCK_SUBTIPOS.TI_ENTERO;
    MI_CONT_FACT      PCK_SUBTIPOS.TI_ENTERO;
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
    MI_RTAACME        PCK_SUBTIPOS.TI_RTA_ACME;
    MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_TABLA          PCK_SUBTIPOS.TI_TABLA;
    MI_PARAMETRO      VARCHAR2(10 CHAR);
  BEGIN


  MI_PARAMETRO :=NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA 	=> UN_COMPANIA,
                                          UN_NOMBRE     => 'SF PERMITE REVERSAR FACTURAS CAUSADAS',
                                          UN_MODULO     => PCK_DATOS.FC_MODULOFACTURACIONGENERAL(),
                                          UN_FECHA_PAR  => SYSDATE,
                                          UN_IND_MAYUS  => 0),'NO');

    BEGIN
      SELECT INDPAGO
        INTO MI_INDPAGO
        FROM SF_OBJETO_COBRO
       WHERE COMPANIA     = UN_COMPANIA
         AND ANO          = UN_ANIO
         AND TIPOCOBRO    = UN_TIPOCOBRO
         AND CODIGO_COBRO = UN_CODCOBRO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_INDPAGO := 0;
    END;

    BEGIN
      IF MI_INDPAGO NOT IN (0)
      THEN
        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
          MI_MSGERROR(1).CLAVE := 'FACTURA';  
          MI_MSGERROR(1).VALOR := UN_FACTURA;
          PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_REVINDPAGOREVFACT,
              UN_REEMPLAZOS => MI_MSGERROR
          ); 
    END; 


  IF MI_PARAMETRO = 'SI' THEN

    PCK_FACT_GENERAL_COM4.PR_REVERSARFACTURACAUSADA(UN_COMPANIA    => UN_COMPANIA,
                                                    UN_ANIO        => UN_ANIO,
                                                    UN_TIPOCOBRO   => UN_TIPOCOBRO,
                                                    UN_FACTURA     => UN_FACTURA,
                                                    UN_CODCOBRO    => UN_CODCOBRO,
                                                    UN_USUARIO     => UN_USUARIO);

  ELSE  
    BEGIN
      SELECT COUNT('X') AS INTERFAZ_RECAUDO 
        INTO MI_NUM_INTREC
        FROM SF_TIPO_COBRO 
       WHERE COMPANIA         = UN_COMPANIA 
         AND INTERFAZ_RECAUDO NOT IN (0);
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_NUM_INTREC := 0;
    END;

    BEGIN
      IF MI_NUM_INTREC IN (0)
      THEN
        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD   => SQLCODE,
              UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_VERIFTIPOCOBRO
          ); 
    END; 

    BEGIN
      SELECT COUNT(INTERFAZADA)
        INTO MI_CONT_FACT
        FROM SF_FACTURA  
       WHERE COMPANIA       = UN_COMPANIA 
         AND INTERFAZADA    NOT IN (0)
         AND NUMERO_FACTURA = UN_FACTURA  ;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_CONT_FACT := 0;  
    END;

    BEGIN
      IF MI_CONT_FACT NOT IN (0)
      THEN
         RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
      ELSE 
        BEGIN
          BEGIN     
            MI_CAMPOS    := 'IMPRESO = 0, INDREVERSO = -1, 
                             MODIFIED_BY = ''' || UN_USUARIO || 
                        ''', DATE_MODIFIED = SYSDATE' ;
            MI_CONDICION := ' COMPANIA       = ''' || UN_COMPANIA || 
                         ''' AND NRO_FACTURA = ' || UN_FACTURA ;            
            MI_RTAACME   := PCK_DATOS.FC_ACME (UN_TABLA     => 'SF_OBJETO_COBRO',
                                               UN_ACCION    => 'M',
                                               UN_CAMPOS    => MI_CAMPOS,
                                               UN_CONDICION => MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN   
            MI_MSGERROR(1).CLAVE := 'FACTURA';  
            MI_MSGERROR(1).VALOR := UN_FACTURA; 
            MI_TABLA := 'SF_OBJETO_COBRO'; 
            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;                                
          END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACT_OBJCOBRO, 
            UN_TABLAERROR => MI_TABLA,
            UN_REEMPLAZOS => MI_MSGERROR
          );
        END;           
        --'TAR: 1000059709
        --'JEG
        --'05/02/2016
        --'Se agrega la sentencia para borrar los registros de las tablas SF_DETALLE_FACTURA y SF_FACTURA de manera que luego se pueda 
        --facturar correctamente.
        BEGIN
          BEGIN
            MI_CONDICION := 'COMPANIA    = ''' || UN_COMPANIA || 
                         ''' AND FACTURA = ' || UN_FACTURA ;
            MI_RTAACME   := PCK_DATOS.FC_ACME(UN_TABLA     => 'SF_DETALLE_FACTURA',
                                              UN_ACCION    => 'E', 
                                              UN_CONDICION => MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN  
            MI_TABLA := 'SF_DETALLE_FACTURA';
            MI_MSGERROR(1).CLAVE := 'FACTURA';  
            MI_MSGERROR(1).VALOR := UN_FACTURA; 
            MI_MSGERROR(2).CLAVE := 'TABLA';  
            MI_MSGERROR(2).VALOR := MI_TABLA; 
            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;                                
          END;  

          BEGIN
            MI_CONDICION := 'COMPANIA           = ''' || UN_COMPANIA || 
                         ''' AND NUMERO_FACTURA = ' || UN_FACTURA ;
            MI_RTAACME   := PCK_DATOS.FC_ACME(UN_TABLA     => 'SF_FACTURA',
                                              UN_ACCION    => 'E', 
                                              UN_CONDICION => MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN   
            MI_TABLA := 'SF_FACTURA';
            MI_MSGERROR(1).CLAVE := 'FACTURA';  
            MI_MSGERROR(1).VALOR := UN_FACTURA; 
            MI_MSGERROR(2).CLAVE := 'TABLA';  
            MI_MSGERROR(2).VALOR := MI_TABLA; 
            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;                                
          END;        
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE,
                UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_DEL_FACTURAS,
                UN_REEMPLAZOS => MI_MSGERROR
            ); 
        END;   
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
        MI_MSGERROR(1).CLAVE := 'FACTURA';  
        MI_MSGERROR(1).VALOR := UN_FACTURA; 
        PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_REVERFACTURACION,
            UN_REEMPLAZOS => MI_MSGERROR
        ); 
    END;

  END IF;  
END PR_REVERSARFACTURACION;


--6
FUNCTION FC_BUSCARABONOSREGISTRAR 
/*
      AUTHORS           : SYSMAN  SAS
      NAME              : FC_BUSCARABONOSREGISTRAR MIGRADO DE ACCESS BuscarAbonoSRegistrar ,
      AUTHOR MIGRACION  : JUAN CAMILO RODRIGUEZ DIAZ
      DATE MIGRADOR     : 23/11/2017
      TIME              : 10:30 AM
      SOURCE MODULE     : SysmanSF2017.11.01
      DESCRIPTION       : 
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 

    @NAME:   buscarAbonoSRegistrar
    @METHOD: PUT
*/
(
UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
UN_NROFACTURA   IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
UN_TIPOFACTURA  IN SF_FACTURA.TIPO_FACTURA%TYPE,
UN_TIPOCOBRO    IN PCK_SUBTIPOS.TI_TIPO_COBRO,
UN_ANOCOBRO     IN PCK_SUBTIPOS.TI_ANIO,
UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN PCK_SUBTIPOS.TI_ENTERO AS 
MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
MI_ACME         PCK_SUBTIPOS.TI_ENTERO;
MI_TIPO_ABONO   SF_DETALLE_CUOTAABONO.TIPO_ABONO%TYPE;
MI_CODIGO_ABONO SF_DETALLE_CUOTAABONO.CODIGO_ABONO%TYPE;
MI_ERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;  
BEGIN

    BEGIN
        SELECT TIPO_ABONO,
          CODIGO_ABONO
          INTO
          MI_TIPO_ABONO,
          MI_CODIGO_ABONO
        FROM SF_DETALLE_CUOTAABONO
        WHERE COMPANIA = UN_COMPANIA
        AND TIPOFACT   = UN_TIPOFACTURA
        AND NROFACT    = UN_NROFACTURA;

        EXCEPTION
             WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
             RETURN -1;

    END;
     BEGIN
         BEGIN
             MI_CAMPOS:='DIFERIDA      = -1,'||
                        'TIPO_ABONO    ='''||NVL(MI_TIPO_ABONO,'')||''','||
                        'NRO_ABONO     ='''||NVL(MI_CODIGO_ABONO,'')||''','||
                        'DATE_MODIFIED =SYSDATE'||','||
                        'MODIFIED_BY   ='''||UN_USUARIO||'''';

             MI_CONDICION:='COMPANIA            ='''||UN_COMPANIA||''''||
                           'AND NUMERO_FACTURA  ='||UN_NROFACTURA||
                           'AND TIPO_FACTURA    ='''||UN_TIPOCOBRO||''''||
                           'AND ANO             ='||UN_ANOCOBRO||'';

             MI_ACME:=PCK_DATOS.FC_ACME(UN_ACCION   =>'M',
                                        UN_TABLA    =>'SF_FACTURA',
                                        UN_CAMPOS   =>MI_CAMPOS,
                                        UN_CONDICION=>MI_CONDICION);
             EXCEPTION
                  WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
         END;

         EXCEPTION
              WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                MI_ERROR(1).CLAVE := 'TABLA';
                MI_ERROR(1).VALOR := 'SF_FACTURA';
                MI_ERROR(2).CLAVE := 'CAMPOS';
                MI_ERROR(2).VALOR :=  MI_CAMPOS;
               PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD    => SQLCODE,
                 UN_TABLAERROR => 'TEMP_SF_ABONOS',
                 UN_ERROR_COD  => PCK_ERRORES.ERR_PR_CALCULARM_INSERTAR,
                 UN_REEMPLAZOS => MI_ERROR);
     END;
  IF MI_ACME > 0 THEN
     BEGIN
      RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_SYSMANSF THEN    
                      MI_ERROR(1).CLAVE := 'TIPO_ABONO';
                      MI_ERROR(1).VALOR := MI_TIPO_ABONO;
                      MI_ERROR(2).CLAVE := 'CODIGO_ABONO';
                      MI_ERROR(2).VALOR := MI_CODIGO_ABONO;
                      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    =>  SQLCODE,
                                                 UN_ERROR_COD  =>  PCK_ERRORES.ERR_SYSMANSF_BUSCARABONO,
                                                 UN_REEMPLAZOS =>  MI_ERROR);
     END;
  END IF;
  RETURN MI_ACME;
END FC_BUSCARABONOSREGISTRAR;

--7
FUNCTION FC_DIFERIRFACTURA
/*
      NAME              : FC_DIFERIRFACTURA METODO MIGRADO EN ACCESS DiferirFactura,
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JUAN CAMILO RODRIGUEZ DIAZ
      DATE MIGRADOR     : 23/11/2017
      TIME              : 10:30 AM
      SOURCE MODULE     : SysmanSF2017.11.01
      DESCRIPTION       : 
      MODIFIER          : ELKIN GEOVANNY AMAYA SILVA
      DATE MODIFIED     : 07/11/2018
      TIME MODIFIED     : 
      MODIFICATIONS     : 

    @NAME:   diferirFactura
    @METHOD: POST
*/
(
  UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOFAC      IN PCK_SUBTIPOS.TI_PARAMETRO,
  UN_NROFACT      IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_TASA         IN PCK_SUBTIPOS.TI_DOBLE,
  UN_TERCERO      IN PCK_SUBTIPOS.TI_TERCERO,
  UN_SUCURSAL     IN PCK_SUBTIPOS.TI_SUCURSAL,
  UN_CENCOSTO     IN PCK_SUBTIPOS.TI_CENTRO_COSTO,
  UN_AUXILIAR     IN PCK_SUBTIPOS.TI_AUXILIAR,
  UN_NCUOTAS      IN PCK_SUBTIPOS.TI_ENTERO,
  UN_CUOTAINICIAL IN PCK_SUBTIPOS.TI_DOBLE,
  UN_TIPOCOBRO    IN PCK_SUBTIPOS.TI_TIPO_COBRO,
  UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO
)RETURN CLOB AS

MI_CAPITAL PCK_SUBTIPOS.TI_DOBLE;
MI_INTERES PCK_SUBTIPOS.TI_DOBLE;
MI_TOTALS  SF_DETALLE_FACTURA.VALOR_NETO%TYPE;
MI_MENSAJE PCK_SUBTIPOS.TI_DESCRIPCION;

MI_DEUDACAPITAL  PCK_SUBTIPOS.TI_DOBLE;
MI_DEUDAINTERES  PCK_SUBTIPOS.TI_DOBLE;
MI_DINTERES_REAL PCK_SUBTIPOS.TI_DOBLE;
MI_DEUDATOTAL    PCK_SUBTIPOS.TI_DOBLE;
MI_NROABONO      PCK_SUBTIPOS.TI_ENTERO_LARGO;
MI_CONDICION     PCK_SUBTIPOS.TI_CONDICION;
MI_CAMPOS        PCK_SUBTIPOS.TI_CAMPOS;
MI_ACME          PCK_SUBTIPOS.TI_ENTERO;
MI_ERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;  
MI_VALORES       PCK_SUBTIPOS.TI_CAMPOS;
MI_CLOB          CLOB:='';
MI_VACIO         PCK_SUBTIPOS.TI_LOGICO:=0;
--MROSERO 7721897 
MI_AJUSTENEGATIVO  PCK_SUBTIPOS.TI_PARAMETRO;
BEGIN
   EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS=''.,''';
   
   --MROSERO 7721897 
      MI_AJUSTENEGATIVO:=NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   =>UN_COMPANIA    ,
                                                  UN_NOMBRE     =>'EXCLUYE VALOR CONCEPTO AJUSTE DESCUENTO',
                                                  UN_MODULO     =>PCK_DATOS.FC_MODULOFACTURACIONGENERAL(),
                                                  UN_FECHA_PAR  =>SYSDATE,
                                                  UN_IND_MAYUS  =>0),'NO');
    BEGIN
    
            BEGIN
   -- MROSERO 7721897          
     IF MI_AJUSTENEGATIVO = 'NO' THEN
     
                SELECT SUM(DECODE(SF_CONCEPTOS.TIPODECONCEPTO,'C',SF_DETALLE_FACTURA.VALOR_BASE+SF_DETALLE_FACTURA.VALOR_IVA-SF_DETALLE_FACTURA.VALOR_RETEFUENTE-SF_DETALLE_FACTURA.VALOR_DESCUENTO+SF_DETALLE_FACTURA.VALOR_ICA,0)) CAPITAL,
                  SUM(DECODE(SF_CONCEPTOS.TIPODECONCEPTO     ,'I',SF_DETALLE_FACTURA.VALOR_BASE+SF_DETALLE_FACTURA.VALOR_IVA-SF_DETALLE_FACTURA.VALOR_RETEFUENTE-SF_DETALLE_FACTURA.VALOR_DESCUENTO+SF_DETALLE_FACTURA.VALOR_ICA,0)) INTERES,
                  SUM(SF_DETALLE_FACTURA.VALOR_NETO) TOTALS INTO
                  MI_CAPITAL,
                  MI_INTERES,
                  MI_TOTALS          
                FROM SF_DETALLE_FACTURA
                INNER JOIN SF_CONCEPTOS
                ON SF_DETALLE_FACTURA.COMPANIA     = SF_CONCEPTOS.COMPANIA
                AND SF_DETALLE_FACTURA.TIPOCOBRO   = SF_CONCEPTOS.TIPOCOBRO
                AND SF_DETALLE_FACTURA.ANO         = SF_CONCEPTOS.ANO
                AND SF_DETALLE_FACTURA.CONCEPTO    = SF_CONCEPTOS.CODIGO
                WHERE SF_DETALLE_FACTURA.COMPANIA   = UN_COMPANIA 
                AND SF_DETALLE_FACTURA.TIPO_FACTURA = UN_TIPOFAC
                AND SF_DETALLE_FACTURA.FACTURA      = UN_NROFACT;
        ELSE         
                SELECT SUM(DECODE(SF_CONCEPTOS.TIPODECONCEPTO,'C',SF_DETALLE_FACTURA.VALOR_BASE+SF_DETALLE_FACTURA.VALOR_IVA-SF_DETALLE_FACTURA.VALOR_RETEFUENTE-SF_DETALLE_FACTURA.VALOR_DESCUENTO+SF_DETALLE_FACTURA.VALOR_ICA,0)) CAPITAL,
                  SUM(DECODE(SF_CONCEPTOS.TIPODECONCEPTO     ,'I',SF_DETALLE_FACTURA.VALOR_BASE+SF_DETALLE_FACTURA.VALOR_IVA-SF_DETALLE_FACTURA.VALOR_RETEFUENTE-SF_DETALLE_FACTURA.VALOR_DESCUENTO+SF_DETALLE_FACTURA.VALOR_ICA,0)) INTERES,
                  SUM(SF_DETALLE_FACTURA.VALOR_NETO) TOTALS INTO
                  MI_CAPITAL,
                  MI_INTERES,
                  MI_TOTALS          
                FROM SF_DETALLE_FACTURA
                INNER JOIN SF_CONCEPTOS
                ON SF_DETALLE_FACTURA.COMPANIA     = SF_CONCEPTOS.COMPANIA
                AND SF_DETALLE_FACTURA.TIPOCOBRO   = SF_CONCEPTOS.TIPOCOBRO
                AND SF_DETALLE_FACTURA.ANO         = SF_CONCEPTOS.ANO
                AND SF_DETALLE_FACTURA.CONCEPTO    = SF_CONCEPTOS.CODIGO
                WHERE SF_DETALLE_FACTURA.COMPANIA   = UN_COMPANIA 
                AND SF_DETALLE_FACTURA.TIPO_FACTURA = UN_TIPOFAC
                AND SF_DETALLE_FACTURA.FACTURA      = UN_NROFACT
                AND SF_CONCEPTOS.DESCUENTOS_FE NOT IN -1;
      END IF;             
          EXCEPTION
               WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
               MI_VACIO:=1;    
            
        END;
      IF  MI_VACIO IN(0) THEN 

          --'evalua si el valor de la sumatoria del total del concepto contra la sumatoria del total de capital e intereses son iguales
          IF NVL(MI_TOTALS, 0) <> NVL(MI_CAPITAL, 0) + NVL(MI_INTERES, 0) THEN
                BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                               UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_SUMATORIA_DEUDA);
                END;                    
          END IF;
          --'asignación de los valores de la deuda capital, de interes y total a los controles correspondientes
          MI_DEUDACAPITAL :=NVL(MI_CAPITAL, 0);
          MI_DEUDAINTERES :=NVL(MI_INTERES, 0);
          MI_DINTERES_REAL:=NVL(MI_INTERES, 0);
          MI_DEUDATOTAL   :=NVL(MI_TOTALS, 0);
      ELSE
          BEGIN
         --' si no se encontraron datos con las facturas seleccionadas se envia un mensaje al usuario y se finaliza el proceso
          RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                               UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_VALOR_TOTALES);
          END;    
      END IF;         

     MI_NROABONO:= PCK_FACT_GENERAL_COM4.FC_ENUMERARABONO(UN_COMPANIA  => UN_COMPANIA,
                                                          UN_TIPOCOBRO => UN_TIPOCOBRO);

    BEGIN
         BEGIN

               MI_CAMPOS:='COMPANIA,'||
                          'TIPO,'||
                          'CODIGO,'||
                          'TERCERO,'||
                          'SUCURSAL,'||
                          'CENTRO_COSTO,'||
                          'AUXILIAR,'||
                          'DEUDA_CAPITAL,'||
                          'DEUDA_INTERES,'||
                          'DEUDA_TOTAL,'||
                          'CUOTAS,'||
                          'FECHA_INICIO,'||
                          'ESTADO,'||
                          'CUOTA_INICIAL,'||
                          'CREADO_POR,'||
                          'FECHA_CREACION,'||
                          'TASA_INTERES,'||
                          'CREATED_BY,'||
                          'DATE_CREATED';
                MI_VALORES:=''''||UN_COMPANIA||''','||
                            ''''||UN_TIPOCOBRO||''','||
                            MI_NROABONO||','||
                            ''''||UN_TERCERO||''','||
                            ''''||UN_SUCURSAL||''','||
                            ''''||UN_CENCOSTO||''','||
                            ''''||UN_AUXILIAR||''','||
                            NVL(MI_DEUDACAPITAL,0)||','||
                            NVL(MI_DINTERES_REAL,0)||','||
                            NVL(MI_DEUDATOTAL,0)||','||
                            UN_NCUOTAS||','||
                            'SYSDATE,'||
                            '''A'''||','||
                            NVL(UN_CUOTAINICIAL,0)||','||
                            ''''||UN_USUARIO||''','||
                            'SYSDATE,'||
                            UN_TASA||','||
                            ''''||UN_USUARIO||''','||
                            'SYSDATE';

                MI_ACME:=PCK_DATOS.FC_ACME(UN_TABLA  =>'TEMP_SF_ABONOS',
                                           UN_ACCION =>'I',
                                           UN_CAMPOS =>MI_CAMPOS,
                                           UN_VALORES=>MI_VALORES);
                EXCEPTION
                     WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                END;

         EXCEPTION
              WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                MI_ERROR(1).CLAVE := 'TABLA';
                MI_ERROR(1).VALOR := 'TEMP_SF_ABONOS';
                MI_ERROR(2).CLAVE := 'CAMPOS';
                MI_ERROR(2).VALOR :=  MI_CAMPOS;
               PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD    => SQLCODE,
                 UN_TABLAERROR => 'TEMP_SF_ABONOS',
                 UN_ERROR_COD  => PCK_ERRORES.ERR_PR_CALCULARM_INSERTAR,
                 UN_REEMPLAZOS => MI_ERROR
                );

      END;     

          IF UN_CUOTAINICIAL > 0 THEN

             BEGIN
                  BEGIN

                     MI_CAMPOS:='COMPANIA,'||
                                'TIPO_ABONO,'||
                                'CODIGO_ABONO,'||
                                'FECHACUOTA,'||
                                'CUOTA,'||
                                'CAPITAL,'||
                                'INTERES,'||
                                'INT_FINANCIACION,'||
                                'INT_RECARGO,'||
                                'TOTAL_CUOTA,'||
                                'ESTADO,'||
                                'CREATED_BY,'||
                                'DATE_CREATED';

                      MI_VALORES:=''''||UN_COMPANIA||''','||
                                  ''''||UN_TIPOCOBRO||''','||
                                  MI_NROABONO||','||
                                  'SYSDATE,'||
                                  '0,'||
                                  UN_CUOTAINICIAL||','||
                                  '0,'||
                                  '0,'||
                                  '0,'||
                                  UN_CUOTAINICIAL||','||
                                  '''A'''||','||
                                  ''''||UN_USUARIO||''','||
                                  'SYSDATE';


                        MI_ACME:=PCK_DATOS.FC_ACME(UN_TABLA  =>'TEMP_SF_DETALLE_ABONO',
                                                   UN_ACCION =>'I',
                                                   UN_CAMPOS =>MI_CAMPOS,
                                                   UN_VALORES=>MI_VALORES);
                      EXCEPTION
                           WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                     END;
                     EXCEPTION
                          WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                            MI_ERROR(1).CLAVE := 'TABLA';
                            MI_ERROR(1).VALOR := 'TEMP_SF_DETALLE_ABONO';
                            MI_ERROR(2).CLAVE := 'CAMPOS';
                            MI_ERROR(2).VALOR :=  MI_CAMPOS;
                           PCK_ERR_MSG.RAISE_WITH_MSG(
                             UN_EXC_COD    => SQLCODE,
                             UN_TABLAERROR => 'TEMP_SF_DETALLE_ABONO',
                             UN_ERROR_COD  => PCK_ERRORES.ERR_PR_CALCULARM_INSERTAR,
                             UN_REEMPLAZOS => MI_ERROR
                            );

            END;           


          END IF; 


            IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA =>UN_COMPANIA,
                                UN_NOMBRE   =>'SF FINANCIACION CUOTAS FIJAS CON AMORTIZACION',
                                UN_MODULO   =>PCK_DATOS.FC_MODULOFACTURACIONGENERAL(),
                                UN_FECHA_PAR=>SYSDATE,
                                UN_IND_MAYUS=>-1) = 'SI' THEN

                PCK_FACT_GENERAL.PR_CALCULARMENSUALIDADABONOCAJ(UN_COMPANIA      =>UN_COMPANIA,
                                                                 UN_TIPOABONO     =>UN_TIPOCOBRO,
                                                                 UN_NROABONO      =>MI_NROABONO,
                                                                 UN_PRIMERFECHA   =>SYSDATE,
                                                                 UN_FECHADECORTE  =>SYSDATE,
                                                                 UN_MONTO         =>MI_DEUDACAPITAL - NVL(UN_CUOTAINICIAL, 0),
                                                                 UN_MONTOINTERES  =>MI_DEUDAINTERES,
                                                                 UN_NCUOTAS       =>UN_NCUOTAS,
                                                                 UN_TASAINTERES   =>UN_TASA,
                                                                 UN_TASARECARGO   =>0,
                                                                 UN_TASAGRADIENTE =>0,
                                                                 UN_BLSIMPLE      =>0,
                                                                 UN_USUARIO       =>UN_USUARIO);    

            ELSE

                   IF  PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA=>UN_COMPANIA,
                                            UN_NOMBRE   =>'SF CALCULO INTERES SIN AMORTIZACION',
                                            UN_MODULO   =>PCK_DATOS.FC_MODULOFACTURACIONGENERAL(),
                                            UN_FECHA_PAR=>SYSDATE,
                                            UN_IND_MAYUS=>-1) = 'SI' THEN

                      PCK_FACT_GENERAL.PR_CALCULARMENSUALIDADSIMPLE(UN_COMPANIA    =>UN_COMPANIA,
                                                                    UN_TIPOABONO   =>UN_TIPOCOBRO,
                                                                    UN_NROABONO    =>MI_NROABONO,
                                                                    UN_MONTOCAPITAL=>MI_DEUDACAPITAL - NVL(UN_CUOTAINICIAL, 0),
                                                                    UN_MONTOINTERES=>MI_DEUDAINTERES,
                                                                    UN_NROCUOTAS   =>UN_NCUOTAS,
                                                                    UN_TASAINTERES =>UN_TASA,
                                                                    UN_FECHAINICIAL=>SYSDATE,
                                                                    UN_USUARIO     =>UN_USUARIO);         

                   ELSIF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA=>UN_COMPANIA,
                                               UN_NOMBRE   =>'SF CALCULO INTERES SIN AMORTIZACION',
                                               UN_MODULO   =>PCK_DATOS.FC_MODULOFACTURACIONGENERAL(),
                                               UN_FECHA_PAR=>SYSDATE,
                                               UN_IND_MAYUS=>-1) = 'S' THEN

                    PCK_FACT_GENERAL.PR_CALCUMENSUALIDADABONOPORC(UN_COMPANIA      =>UN_COMPANIA,
                                                                   UN_TIPOABONO     =>UN_TIPOCOBRO,
                                                                   UN_NROABONO      =>MI_NROABONO,
                                                                   UN_PRIMERFECHA   =>SYSDATE,
                                                                   UN_FECHADECORTE  =>SYSDATE,
                                                                   UN_MONTO         => MI_DEUDACAPITAL - NVL(UN_CUOTAINICIAL, 0),
                                                                   UN_MONTOINTERES  =>MI_DEUDAINTERES,
                                                                   UN_NCUOTAS       =>UN_NCUOTAS,
                                                                   UN_TASAINTERES   =>UN_TASA,
                                                                   UN_TASARECARGO   =>0,
                                                                   UN_TASAGRADIENTE =>0,
                                                                   UN_BLSIMPLE      =>0,
                                                                   UN_USUARIO       =>UN_USUARIO);                                                         
                   ELSE
                      PCK_FACT_GENERAL.PR_CALCULARMENSUALIDADABONO(UN_COMPANIA     =>UN_COMPANIA,
                                                                    UN_TIPOABONO     =>UN_TIPOCOBRO,
                                                                    UN_NROABONO      =>MI_NROABONO,
                                                                    UN_PRIMERFECHA   =>SYSDATE,
                                                                    UN_FECHADECORTE  =>SYSDATE,
                                                                    UN_MONTO         =>MI_DEUDACAPITAL - NVL(UN_CUOTAINICIAL, 0),
                                                                    UN_MONTOINTERES  =>MI_DEUDAINTERES,
                                                                    UN_NCUOTAS       =>UN_NCUOTAS,
                                                                    UN_TASAINTERES   =>UN_TASA,
                                                                    UN_TASARECARGO   =>0,
                                                                    UN_TASAGRADIENTE =>0,
                                                                    UN_BLSIMPLE      =>0,
                                                                    UN_USUARIO       =>UN_USUARIO);        
                   END IF;

            END IF;

            MI_CLOB:=PCK_FACT_GENERAL_COM1.FC_DISTRIBUIRABONO(UN_COMPANIA    =>UN_COMPANIA,
                                                              UN_TIPOABONO   =>UN_TIPOCOBRO,
                                                              UN_NROABONO    =>MI_NROABONO,
                                                              UN_TIPOFACTURA =>UN_TIPOCOBRO,
                                                              UN_FACTURA     =>UN_NROFACT,
                                                              UN_TERCERO     =>UN_TERCERO,
                                                              UN_SUCURSAL    =>UN_SUCURSAL,
                                                              UN_TIPOCOBRO   =>UN_TIPOCOBRO,
                                                              UN_USUARIO     =>UN_USUARIO);

    END;
    RETURN MI_NROABONO||PCK_DATOS.GL_SEPARADOR_COL||MI_CLOB;
END FC_DIFERIRFACTURA;

--8
PROCEDURE PR_CALCULARMENSUALIDADABONOCAJ(
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
  MI_TASAINTERES      PCK_SUBTIPOS.TI_PORCENTAJE; --' TASA DE INTERES ANUAL
  MI_CUO              PCK_SUBTIPOS.TI_DOBLE;
  MI_NDCS             PCK_SUBTIPOS.TI_ENTERO;
  MI_XX1              PCK_SUBTIPOS.TI_PARAMETRO;
  MI_XX               PCK_SUBTIPOS.TI_ENTERO;
  MI_VALINTERES           PCK_SUBTIPOS.TI_DOBLE;
  MI_VALCAPITAL           PCK_SUBTIPOS.TI_DOBLE;
  MI_FECHAINICIO      DATE;
  MI_NUEVACUOTA       PCK_SUBTIPOS.TI_ENTERO;
  MI_INTDIGITOSREDONDEO PCK_SUBTIPOS.TI_ENTERO;
  MI_TINTERESMENSUAL  PCK_SUBTIPOS.TI_PORCENTAJE; --' TASA DE INTERES MENSUAL
  MI_FECHAINICIO1     DATE;
  MI_INTDIAS          PCK_SUBTIPOS.TI_ENTERO;
  MI_INTMES           PCK_SUBTIPOS.TI_ENTERO;
  MI_INTANIO          PCK_SUBTIPOS.TI_ENTERO;
  MI_PAGO             PCK_SUBTIPOS.TI_DOBLE;
  MI_DIFERENCIA       PCK_SUBTIPOS.TI_DOBLE;
  MI_DIFERENCIACUOTA  NUMBER(20,4);
  MI_POTENCIA         NUMBER(10,8);
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
  MI_CANTDETABONO     PCK_SUBTIPOS.TI_ENTERO;
  MI_NUMCUOTAS        PCK_SUBTIPOS.TI_ENTERO;
  
BEGIN

  EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS=''.,''';
   MI_INTDIGITOSREDONDEO:= 0;       
   --'Se almacena en la variable nc el número de cuotas del acuerdo de pago
   --'Almacena en la variable Vm (Valor Monto) el valor capital de la deuda la cual será la base para calcular los interes de financiación
   MI_VM    := UN_MONTO;-- ' - MontoInteres
   MI_SALDO := MI_VM;--   ' reasigna el valor del saldo teniendo en cuenta que al monto de la deuda se le resta el monto de intereses

   MI_TASAINTERES :=CASE WHEN UN_TASAINTERES = 0 THEN  0.0001 ELSE UN_TASAINTERES END;

--7707082(Mrosero) 11/02/2022
  --MI_TINTERESMENSUAL:= (MI_TASAINTERES / 12) / 100 ; 
   
  MI_TINTERESMENSUAL:= 1+(MI_TASAINTERES/100);
      
  MI_TINTERESMENSUAL:= (POWER((MI_TINTERESMENSUAL),(1/12)));
  
  MI_TINTERESMENSUAL:= ROUND((MI_TINTERESMENSUAL-1),3);

   MI_PAGO:= ROUND(MI_SALDO * (((POWER(1 + MI_TINTERESMENSUAL,UN_NCUOTAS)) * MI_TINTERESMENSUAL) / ((POWER(1 + MI_TINTERESMENSUAL,UN_NCUOTAS)) - 1)),0);   
--7707082(Mrosero) 11/02/2022 

   MI_NDCS          := 1;   
   MI_NUMCUOTAS     := 1;
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

        --  MI_VALINTERES := MI_SALDO * MI_TINTERESMENSUAL;
		
		   MI_VALINTERES := ROUND ((MI_SALDO * MI_TINTERESMENSUAL),0); --INTERESES

          --Tiene en cuenta el abono
          MI_VALCAPITAL       := MI_PAGO - PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR    =>MI_VALINTERES,
                                                               UN_PRECISION=>0);
          MI_SALDO        := MI_SALDO - MI_VALCAPITAL; --calcula el nuevo saldo del monto capital
          MI_SUMAINTERESES:= MI_SUMAINTERESES + PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR    =>MI_VALINTERES,
                                                                        UN_PRECISION=>0);
          MI_SUMACAPITAL  := MI_SUMACAPITAL + MI_VALCAPITAL;
          MI_SUMACUOTAS   := MI_SUMACUOTAS + MI_VALCAPITAL + PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR    =>MI_VALINTERES,
                                                                                 UN_PRECISION=>0);
          BEGIN
             BEGIN

                     MI_CAMPOS:='COMPANIA,'||
                                'TIPO_ABONO,'||
                                'CODIGO_ABONO,'||
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
                                  MI_VALCAPITAL||','||
                                  '0,'||
                                  TO_CHAR(PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR    =>MI_VALINTERES,
                                                                  UN_PRECISION=>0))||','||
                                  '0'||','||
                                  TO_CHAR(PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR    =>MI_VALCAPITAL+ MI_VALINTERES,
                                                                  UN_PRECISION=>0))||','||
                                  '''A'''||','||
                                  ''''||UN_USUARIO||''','||
                                  'SYSDATE';

                      MI_ACME:=PCK_DATOS.FC_ACME(UN_ACCION  =>'I',
                                                 UN_TABLA   =>'TEMP_SF_DETALLE_ABONO',
                                                 UN_CAMPOS  =>MI_CAMPOS,
                                                 UN_VALORES =>MI_VALORES); 


                       EXCEPTION
                                 WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                    END;
                 EXCEPTION
                      WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                           MI_ERROR(1).CLAVE := 'TABLA';
                           MI_ERROR(1).VALOR := 'TEMP_SF_DETALLE_ABONO';
                           MI_ERROR(2).CLAVE := 'CAMPOS';
                           MI_ERROR(2).VALOR :=  MI_CAMPOS;
                           PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                      UN_TABLAERROR => 'TEMP_SF_DETALLE_ABONO',
                                                      UN_ERROR_COD  => PCK_ERRORES.ERR_PR_CALCULARM_INSERTAR,
                                                      UN_REEMPLAZOS => MI_ERROR);
                END;

       MI_NDCS := MI_NDCS + 1;          

    IF MI_NDCS =  (UN_NCUOTAS + 1) THEN       
        EXIT;  
    END IF;    
   END LOOP BUCLECUOTAS;

    MI_DIFERENCIA      := (MI_VM - MI_SUMACAPITAL);--/UN_NCUOTAS;
    MI_DIFERENCIACUOTA := (MI_VM - (MI_SUMACUOTAS - MI_SUMAINTERESES)); --/UN_NCUOTAS;

   FOR MI_RS IN (SELECT CUOTA,
                         CAPITAL,
                         INT_FINANCIACION
                  FROM  TEMP_SF_DETALLE_ABONO
                  WHERE COMPANIA    = UN_COMPANIA
                    AND TIPO_ABONO  = UN_TIPOABONO
                    AND CODIGO_ABONO= UN_NROABONO
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
                          ' AND TIPO_ABONO   ='''||UN_TIPOABONO||''''||
                          ' AND CODIGO_ABONO ='||UN_NROABONO||
                          ' AND CUOTA        ='||MI_RS.CUOTA;                               


                 MI_ACME:=PCK_DATOS.FC_ACME(UN_ACCION    =>'M',
                                            UN_TABLA     =>'TEMP_SF_DETALLE_ABONO',
                                            UN_CAMPOS    =>MI_CAMPOS,
                                            UN_CONDICION =>MI_CONDICION);

                     EXCEPTION
                          WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                         RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                       END;
                EXCEPTION
                     WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                          MI_ERROR(1).CLAVE := 'TABLA';
                          MI_ERROR(1).VALOR := 'TEMP_SF_DETALLE_ABONO';
                          MI_ERROR(2).CLAVE := 'CAMPOS';
                          MI_ERROR(2).VALOR :=  MI_CAMPOS;
                          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                     UN_TABLAERROR => 'TEMP_SF_DETALLE_ABONO',
                                                     UN_ERROR_COD  => PCK_ERRORES.ERR_PR_CALCULARM_ACTUALIZAR,
                                                     UN_REEMPLAZOS => MI_ERROR);
              END;

     END LOOP;

      SELECT SUM(CAPITAL),
             SUM(INT_FINANCIACION)
               INTO 
               MI_TCAPITAL,
               MI_TINTERES
        FROM TEMP_SF_DETALLE_ABONO
        WHERE COMPANIA   = UN_COMPANIA
        AND TIPO_ABONO   = UN_TIPOABONO
        AND CODIGO_ABONO = UN_NROABONO
        AND CUOTA <> 0;

         SELECT MAX(CUOTA)                       
               INTO 
               MI_CUOTAMAX
        FROM TEMP_SF_DETALLE_ABONO
        WHERE COMPANIA   = UN_COMPANIA
        AND TIPO_ABONO   = UN_TIPOABONO
        AND CODIGO_ABONO = UN_NROABONO
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
                            ' AND TIPO_ABONO   ='''||UN_TIPOABONO||''''||
                            ' AND CODIGO_ABONO ='||UN_NROABONO||
                            ' AND CUOTA        ='||MI_CUOTAMAX;                               


             MI_ACME:=PCK_DATOS.FC_ACME(UN_ACCION    =>'M',
                                        UN_TABLA     =>'TEMP_SF_DETALLE_ABONO',
                                        UN_CAMPOS    =>MI_CAMPOS,
                                        UN_CONDICION =>MI_CONDICION);
        END IF;
        
END PR_CALCULARMENSUALIDADABONOCAJ;

--9
PROCEDURE PR_CALCULARMENSUALIDADSIMPLE
/*
      NAME              : PR_CALCULARMENSUALIDADSIMPLE METODO MIGRADO EN ACCESS CalcularMensualidadSimple,
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JUAN CAMILO RODRIGUEZ DIAZ
      DATE MIGRADOR     : 22/11/2017
      TIME              : 10:30 AM
      SOURCE MODULE     : SysmanSF2017.11.01
      DESCRIPTION       : 
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 

    @NAME:   calcularMensualidadSimple
    @METHOD: POST
*/
(
  UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOABONO    IN PCK_SUBTIPOS.TI_PARAMETRO,
  UN_NROABONO     IN PCK_SUBTIPOS.TI_DOBLE,
  UN_MONTOCAPITAL IN PCK_SUBTIPOS.TI_DOBLE,
  UN_MONTOINTERES IN PCK_SUBTIPOS.TI_DOBLE,
  UN_NROCUOTAS    IN PCK_SUBTIPOS.TI_ENTERO,
  UN_TASAINTERES  IN PCK_SUBTIPOS.TI_DOBLE,
  UN_FECHAINICIAL IN DATE,
  UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO
) AS

 MI_VLRCUOTAK        PCK_SUBTIPOS.TI_DOBLE;
 MI_VLRCUOTAI        PCK_SUBTIPOS.TI_DOBLE;
 MI_VLRCUOTAIFIN     PCK_SUBTIPOS.TI_DOBLE;
 MI_VLRINTFIN        PCK_SUBTIPOS.TI_DOBLE;
 MI_REDONDEO         PCK_SUBTIPOS.TI_ENTERO;
 MI_I                PCK_SUBTIPOS.TI_ENTERO;
 MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
 MI_ACME             PCK_SUBTIPOS.TI_ENTERO;
 MI_FECHAINICIAL_AUX DATE;
 MI_ERROR            PCK_SUBTIPOS.TI_CLAVEVALOR; 
 MI_VALORES          PCK_SUBTIPOS.TI_PARAMETRO;
 MI_MONTOCAPITAL     PCK_SUBTIPOS.TI_DOBLE;
 MI_MONTOINTERES     PCK_SUBTIPOS.TI_DOBLE;
BEGIN
 MI_MONTOCAPITAL:=NVL(UN_MONTOCAPITAL,0);
 MI_MONTOINTERES:=NVL(UN_MONTOINTERES,0);
 MI_REDONDEO:=NVL(TO_NUMBER(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA=>UN_COMPANIA,
                                                  UN_NOMBRE   =>'SF REDONDEO VALOR',
                                                  UN_MODULO   =>PCK_DATOS.FC_MODULOFACTURACIONGENERAL(),
                                                  UN_FECHA_PAR=>SYSDATE,
                                                  UN_IND_MAYUS=>0)),0); 

    MI_VLRINTFIN    := ROUND(MI_MONTOCAPITAL * (UN_TASAINTERES / 100), MI_REDONDEO);
    MI_VLRCUOTAK    := ROUND(MI_MONTOCAPITAL / UN_NROCUOTAS, MI_REDONDEO);
    MI_VLRCUOTAI    := ROUND(MI_MONTOINTERES / UN_NROCUOTAS, MI_REDONDEO);
    MI_VLRCUOTAIFIN := ROUND(MI_VLRINTFIN / UN_NROCUOTAS, MI_REDONDEO);
    MI_FECHAINICIAL_AUX:=UN_FECHAINICIAL;

     FOR MI_I IN 1..UN_NROCUOTAS
      LOOP
        IF MI_I = UN_NROCUOTAS THEN
           MI_VLRCUOTAK    := MI_MONTOCAPITAL;
           MI_VLRCUOTAI    := MI_MONTOINTERES;
           MI_VLRCUOTAIFIN := MI_VLRINTFIN;
        END IF;
        BEGIN
             BEGIN
                 MI_CAMPOS:='COMPANIA,'||
                            'TIPO_ABONO,'||
                            'CODIGO_ABONO,'||
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
                            MI_I||','||
                            'TO_DATE('''||MI_FECHAINICIAL_AUX||''',''DD/MM/YYYY HH24:MI:SS''),'||
                            MI_VLRCUOTAK||','||
                            MI_VLRCUOTAI||','||
                            '0,'||
                            MI_VLRCUOTAIFIN||','||
                            TO_CHAR(MI_VLRCUOTAK + MI_VLRCUOTAI + MI_VLRCUOTAIFIN)||','||
                            '''A'''||','||
                            ''''||UN_USUARIO||''','||
                            'SYSDATE';

                MI_ACME:=PCK_DATOS.FC_ACME(UN_ACCION  =>'I',
                                           UN_TABLA   =>'TEMP_SF_DETALLE_ABONO',
                                           UN_CAMPOS  =>MI_CAMPOS,
                                           UN_VALORES =>MI_VALORES);           

                MI_MONTOCAPITAL     := MI_MONTOCAPITAL - MI_VLRCUOTAK;
                MI_MONTOINTERES     := MI_MONTOINTERES - MI_VLRCUOTAI;
                MI_VLRINTFIN        := MI_VLRINTFIN - MI_VLRCUOTAIFIN;
                MI_FECHAINICIAL_AUX := ADD_MONTHS(TO_DATE(MI_FECHAINICIAL_AUX,'DD/MM/YYYY HH24:MI:SS'),1);    

                  EXCEPTION
                       WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                END;
             EXCEPTION
                  WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                       MI_ERROR(1).CLAVE := 'TABLA';
                       MI_ERROR(1).VALOR := 'TEMP_SF_DETALLE_ABONO';
                       MI_ERROR(2).CLAVE := 'CAMPOS';
                       MI_ERROR(2).VALOR :=  MI_CAMPOS;
                       PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                  UN_TABLAERROR => 'TEMP_SF_DETALLE_ABONO',
                                                  UN_ERROR_COD  => PCK_ERRORES.ERR_PR_CALCULARM_INSERTAR,
                                                  UN_REEMPLAZOS => MI_ERROR);

         END;           
    END LOOP;
END PR_CALCULARMENSUALIDADSIMPLE;

--10
PROCEDURE PR_AJUSTARCUOTAS
/*
      NAME              : PR_AJUSTARCUOTAS METODO MIGRADO EN ACCESS AjustarCuotas,
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JUAN CAMILO RODRIGUEZ DIAZ
      DATE MIGRADOR     : 22/11/2017
      TIME              : 10:30 AM
      SOURCE MODULE     : SysmanSF2017.11.01
      DESCRIPTION       : 
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 

    @NAME:   ajustarCuotas
    @METHOD: POST
*/
(
  UN_COMPANIA  PCK_SUBTIPOS.TI_COMPANIA,
  UN_USUARIO   PCK_SUBTIPOS.TI_USUARIO
)
AS 
  MI_VLRINTFINAN PCK_SUBTIPOS.TI_DOBLE;  
  MI_CANT        PCK_SUBTIPOS.TI_ENTERO;
  MI_INTFINAN    PCK_SUBTIPOS.TI_DOBLE;
  MI_CAMPOS      PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION   PCK_SUBTIPOS.TI_CAMPOS;
  MI_ACME        PCK_SUBTIPOS.TI_ENTERO;
  MI_ERROR       PCK_SUBTIPOS.TI_CLAVEVALOR; 
  MI_ESTADO      PCK_SUBTIPOS.TI_LOGICO:=0;
BEGIN
    BEGIN
        SELECT SUM(TEMP_SF_DETALLE_ABONO.INT_FINANCIACION) INTFINAN,
          COUNT(TEMP_SF_DETALLE_ABONO.CUOTA)               CANT
          INTO MI_INTFINAN,
          MI_CANT
        FROM TEMP_SF_DETALLE_ABONO
        WHERE TEMP_SF_DETALLE_ABONO.CUOTA NOT IN(0);
       EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
        MI_ESTADO:=1;
    END;

    IF MI_ESTADO = 0 THEN
      IF NVL(MI_INTFINAN,0) NOT IN(0)  THEN      
         MI_VLRINTFINAN := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR=>MI_INTFINAN / MI_CANT,
                                                   UN_PRECISION=> 0);     
      END IF;
    END IF;

    BEGIN
        BEGIN
        MI_CAMPOS   :='INT_FINANCIACION='||NVL(MI_VLRINTFINAN,0)||','||
                      'MODIFIED_BY='''||UN_USUARIO||''','||
                      'DATE_MODIFIED=SYSDATE';
        MI_CONDICION:='CUOTA NOT IN(0)';
        MI_ACME     :=PCK_DATOS.FC_ACME(UN_ACCION   =>'M',
                                        UN_TABLA    =>'TEMP_SF_DETALLE_ABONO',
                                        UN_CAMPOS   =>MI_CAMPOS,
                                        UN_CONDICION=>MI_CONDICION);
        EXCEPTION
             WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;                            
        END;
        EXCEPTION
             WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  MI_ERROR(1).CLAVE := 'TABLA';
                  MI_ERROR(1).VALOR := 'TEMP_SF_DETALLE_ABONO';
                  MI_ERROR(2).CLAVE := 'CAMPOS';
                  MI_ERROR(2).VALOR :=  MI_CAMPOS;
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                             UN_TABLAERROR => 'TEMP_SF_DETALLE_ABONO',
                                             UN_ERROR_COD  => PCK_ERRORES.ERR_PR_CALCULARM_ACTUALIZAR,
                                             UN_REEMPLAZOS => MI_ERROR);
    END;
    BEGIN    
        BEGIN
        MI_CAMPOS   :='TOTAL_CUOTA  =CAPITAL + INTERES + INT_FINANCIACION,'||
                      'MODIFIED_BY  ='''||UN_USUARIO||''','||
                      'DATE_MODIFIED=SYSDATE';

        MI_ACME     :=PCK_DATOS.FC_ACME(UN_ACCION   =>'M',
                                        UN_TABLA    =>'TEMP_SF_DETALLE_ABONO',
                                        UN_CAMPOS   =>MI_CAMPOS);
        EXCEPTION
             WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        END;
     EXCEPTION
             WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                  MI_ERROR(1).CLAVE := 'TABLA';
                  MI_ERROR(1).VALOR := 'TEMP_SF_DETALLE_ABONO';
                  MI_ERROR(2).CLAVE := 'CAMPOS';
                  MI_ERROR(2).VALOR :=  MI_CAMPOS;
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                             UN_TABLAERROR => 'TEMP_SF_DETALLE_ABONO',
                                             UN_ERROR_COD  => PCK_ERRORES.ERR_PR_CALCULARM_ACTUALIZAR,
                                             UN_REEMPLAZOS => MI_ERROR);


    END;
END PR_AJUSTARCUOTAS;

--11
PROCEDURE  PR_CALCUMENSUALIDADABONOPORC
/*
      NAME              : FC_CALCUMENSUALIDADABONOPORC MIGRADO DE ACCESS calcularMensualidadAbonoPorc 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JUAN CAMILO RODRIGUEZ DIAZ 
      DATE MIGRADOR     : 22/11/2017
      TIME              : 10:30 AM
      SOURCE MODULE     : SysmanSF2017.11.01
      DESCRIPTION       : 
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 

    @NAME:   calcularMensualidadAbonoPorc
    @METHOD: GET
*/
(
  UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOABONO     IN PCK_SUBTIPOS.TI_PARAMETRO,
  UN_NROABONO      IN PCK_SUBTIPOS.TI_DOBLE,
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
)
AS  
  MI_SALDO                PCK_SUBTIPOS.TI_DOBLE;
  MI_SALDO1               PCK_SUBTIPOS.TI_DOBLE;
  MI_SALDOI               PCK_SUBTIPOS.TI_DOBLE;
  MI_AI                   PCK_SUBTIPOS.TI_ENTERO;
  MI_MI                   PCK_SUBTIPOS.TI_ENTERO;
  MI_AI0                  PCK_SUBTIPOS.TI_ENTERO;
  MI_MI0                  PCK_SUBTIPOS.TI_ENTERO;
  MI_NC                   PCK_SUBTIPOS.TI_ENTERO;
  MI_NA                   PCK_SUBTIPOS.TI_DOBLE;
  MI_GR                   PCK_SUBTIPOS.TI_DOBLE;
  MI_GR1                  PCK_SUBTIPOS.TI_DOBLE;
  MI_VM                   PCK_SUBTIPOS.TI_DOBLE;
  MI_VMSI                 PCK_SUBTIPOS.TI_DOBLE; -- Valor Monto Sin Interes
  MI_TI                   PCK_SUBTIPOS.TI_DOBLE;  --Tasa de Interes anual
  MI_TI1                  PCK_SUBTIPOS.TI_DOBLE;
  MI_TIE                  PCK_SUBTIPOS.TI_DOBLE;
  MI_VC                   PCK_SUBTIPOS.TI_DOBLE;
  MI_VCI                  PCK_SUBTIPOS.TI_DOBLE;
  MI_VCSI                 PCK_SUBTIPOS.TI_DOBLE; --Valor de la cuota sin intereses
  MI_CUO                  PCK_SUBTIPOS.TI_DOBLE;
  MI_CUOI                 PCK_SUBTIPOS.TI_DOBLE;
  MI_CUOSI                PCK_SUBTIPOS.TI_DOBLE; -- Valor Cuota del componente de capital sin intereses
  MI_CUO1                 PCK_SUBTIPOS.TI_DOBLE;
  MI_NDCS                 PCK_SUBTIPOS.TI_ENTERO;
  MI_NDCR                 PCK_SUBTIPOS.TI_ENTERO;
  MI_XX1                  PCK_SUBTIPOS.TI_PARAMETRO;
  MI_XX                   PCK_SUBTIPOS.TI_ENTERO;
  MI_VALINT               PCK_SUBTIPOS.TI_DOBLE;
  MI_VALREC               PCK_SUBTIPOS.TI_DOBLE;
  MI_VALSAL               PCK_SUBTIPOS.TI_DOBLE;
  MI_INC                  PCK_SUBTIPOS.TI_DOBLE;
  MI_VALCUO               PCK_SUBTIPOS.TI_DOBLE;
  MI_FECHAINICIO          DATE;
  MI_ABONO                PCK_SUBTIPOS.TI_DOBLE;
  MI_ABONO1               PCK_SUBTIPOS.TI_DOBLE;
  MI_NUEVACUOTA           PCK_SUBTIPOS.TI_ENTERO;
  MI_INTDIGITOSREDONDEO   PCK_SUBTIPOS.TI_ENTERO;
  MI_STRPARAMETRO         PCK_SUBTIPOS.TI_PARAMETRO;
  MI_TIM                  PCK_SUBTIPOS.TI_DOBLE;
  MI_DIASCOBREC           PCK_SUBTIPOS.TI_ENTERO;
  MI_FECHACORTETEMP       DATE;
  MI_INTPRIMERAVEZ        PCK_SUBTIPOS.TI_ENTERO;
  MI_FECHAINICIO1         DATE;
  MI_INTDIAS              PCK_SUBTIPOS.TI_ENTERO;
  MI_INTMES               PCK_SUBTIPOS.TI_ENTERO;
  MI_INTANIO              PCK_SUBTIPOS.TI_ENTERO;  
  MI_STRCORTE              PCK_SUBTIPOS.TI_PARAMETRO;
  MI_DIASINICIO            PCK_SUBTIPOS.TI_ENTERO;
  MI_VALCUOPRIM            PCK_SUBTIPOS.TI_DOBLE;
  MI_DIASNOCOBRO1CUOTA     PCK_SUBTIPOS.TI_ENTERO;
  MI_SALDOINTERES          PCK_SUBTIPOS.TI_DOBLE;
  MI_NITDELACOMPANIA       PCK_SUBTIPOS.TI_PARAMETRO;
  MI_VALCUOSI              PCK_SUBTIPOS.TI_DOBLE;
  MI_AIANT                 PCK_SUBTIPOS.TI_ENTERO;
  MI_MANEJAGRADIENTESXANO  PCK_SUBTIPOS.TI_LOGICO;
  MI_VALCUOI               PCK_SUBTIPOS.TI_DOBLE;
  MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
  MI_ACME                  PCK_SUBTIPOS.TI_ENTERO;   
  MI_VALORES               PCK_SUBTIPOS.TI_CAMPOS;
  MI_ERROR                 PCK_SUBTIPOS.TI_CLAVEVALOR; 
BEGIN
   MI_INTDIGITOSREDONDEO :=0;

   --Se almacena en la variable nc el número de cuotas del acuerdo de pago
   MI_NC := CASE WHEN UN_NCUOTAS <= 0 THEN 1 ELSE UN_NCUOTAS END;
   MI_GR := UN_TASAGRADIENTE;

   --'Almacena en la variable Vm (Valor Monto) el valor capital de la deuda la cual será la base para calcular los interes de financiación
   MI_VM    := UN_MONTO; -- -MontoInteres
   MI_SALDO := MI_VM;  -- ' reasigna el valor del saldo teniendo en cuenta que al monto de la deuda se le resta el monto de intereses

   MI_TI := CASE WHEN UN_TASAINTERES = 0 THEN  0.0001 ELSE UN_TASAINTERES END;

   MI_TIM := MI_TI / 100;


   IF MI_GR > 0 THEN
      MI_GR1 := MI_GR / 100;
      MI_NA  := UN_NCUOTAS / 12;
      MI_TI1 := MI_TIM;
      MI_TIE := POWER(1 + MI_TIM,12) - 1; --'interes periodo equivalente
      MI_VC  := (UN_MONTO * (((MI_TIE - MI_GR1) * POWER(1 + MI_TIE,MI_NA)) / (POWER(1 + MI_TIE,MI_NA) - POWER(1 + MI_GR1,MI_NA)))) * (MI_TI1 / (POWER(1 + MI_TI1,12) - 1));
      MI_VALCUOPRIM :=MI_VC;
   ELSE
      IF MI_NC <> 0 THEN
         MI_VC  := MI_VM / MI_NC;
         MI_VCI := UN_MONTOINTERES / MI_NC;-- 'se adiciona para tener en cuenta en la creación de la cuotas el monto de los intereses- 18/01/2011
      ELSE
         MI_VC  := 0;
         MI_VCI := 0;
      END IF;
   END IF;

   MI_VC  := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR    =>MI_VC,
                                     UN_PRECISION=> MI_INTDIGITOSREDONDEO);
   MI_CUO := MI_VC;
   MI_VCI := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR=>MI_VCI,
                                     UN_PRECISION=> MI_INTDIGITOSREDONDEO);
   MI_CUOI:= MI_VCI;

   MI_NDCS       := 0;
   MI_NUEVACUOTA := 1;

   MI_INTPRIMERAVEZ := 1;
   MI_DIASINICIO    := 0;

   WHILE TRUE 
   LOOP
      MI_FECHAINICIO  := ADD_MONTHS(TO_DATE(UN_PRIMERFECHA,'DD/MM/YYYY HH24:MI:SS'), MI_NDCS + MI_NUEVACUOTA);
      MI_FECHAINICIO1 := MI_FECHAINICIO;

      MI_AI  := EXTRACT(YEAR FROM TO_DATE(MI_FECHAINICIO,'DD/MM/YYYY HH24:MI:SS') );
      MI_MI  := EXTRACT(MONTH FROM TO_DATE(MI_FECHAINICIO,'DD/MM/YYYY HH24:MI:SS') );

      MI_CUO1:= MI_CUO;

      IF TO_NUMBER((MI_NDCS + MI_NUEVACUOTA - 1) / 12) * 12 = (MI_NDCS + MI_NUEVACUOTA - 1) AND 
         (MI_NDCS + MI_NUEVACUOTA - 1) > 0 AND
         MI_GR <> 0 THEN
         MI_INC := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR    =>MI_CUO * (MI_GR / 100),
                                           UN_PRECISION=> 0);
         MI_CUO := MI_CUO + MI_INC;
         MI_CUO := ROUND(MI_CUO, MI_INTDIGITOSREDONDEO);
         MI_CUO1:= MI_CUO;
      END IF;

      MI_VALCUO   := MI_CUO;
      MI_VALCUOSI := MI_CUOSI;
      MI_VALCUOI  := MI_CUOI;

      MI_XX := 0;

      If (TO_DATE(MI_FECHAINICIO,'DD/MM/YYYY HH24:MI:SS') <= TO_DATE(UN_FECHADECORTE,'DD/MM/YYYY HH24:MI:SS')) THEN
        -- ' determina la antigudad de la deuda de esta cuota
         --' si los dias son mas de diez toma el mes completo.

         MI_XX := MONTHS_BETWEEN(UN_FECHADECORTE,MI_FECHAINICIO1);
      END IF;

      MI_VALINT := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR    =>MI_SALDO * MI_TIM,
                                           UN_PRECISION=>MI_INTDIGITOSREDONDEO);

     --' If (Gr > 0 And Ndcs + NuevaCuota >= NCUOTAS) Or (Gr <= 0 And Ndcs + NuevaCuota >= NCUOTAS) Then  'ojo con este +1 en la caja de retiros
     --'    ValInt = 0
     --' End If

      MI_VALREC := CASE WHEN  MI_FECHAINICIO >= (TO_DATE(UN_FECHADECORTE,'DD/MM/YYYY HH24:MI:SS') + MI_DIASCOBREC) THEN 0 ELSE  PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR    =>(MI_VALCUO - MI_VCSI) * MI_XX * UN_TASARECARGO / 100 / 12,
                                                                                                                                                        UN_PRECISION=> 2) END;

      --'Tiene en cuenta el abono

      MI_SALDO  := MI_SALDO - MI_VALCUO; -- ' calcula el nuevo saldo del monto capital
      MI_SALDOI := MI_SALDOI - MI_CUOI;

      --'Si son entre 1 y 10 pesos para el saldo, se le suman a la última cuota.

      IF MI_SALDO <= 10 THEN
         MI_VALCUO := MI_VALCUO + MI_SALDO;
         MI_SALDO  := 0;
      END IF;

      IF MI_SALDOI <= 10 THEN
         MI_VALCUOI := MI_VALCUOI + MI_SALDOI;
         MI_SALDOI  := 0;
      END IF;

      BEGIN
           BEGIN
           MI_CAMPOS:='COMPANIA,'||
                      'TIPO_ABONO,'||
                      'CODIGO_ABONO,'||
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
                        TO_CHAR(MI_NDCS + MI_NUEVACUOTA)||','||
                        'TO_DATE('''||MI_FECHAINICIO||''',''DD/MM/YYYY HH24:MI:SS''),'||
                        MI_VALCUO||','||
                        MI_VALCUOI||','||
                        MI_VALINT||','||
                        '0'||','||
                        TO_CHAR(MI_VALCUO + MI_VALCUOI + MI_VALINT)||','||
                        '''A'''||','||
                        ''''||UN_USUARIO||''','||
                        'SYSDATE';

              MI_ACME:=PCK_DATOS.FC_ACME(UN_ACCION  =>'I',
                                         UN_TABLA   =>'TEMP_SF_DETALLE_ABONO',
                                         UN_CAMPOS  =>MI_CAMPOS,
                                         UN_VALORES =>MI_VALORES);                    
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
            END;
           EXCEPTION
                  WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                       MI_ERROR(1).CLAVE := 'TABLA';
                       MI_ERROR(1).VALOR := 'TEMP_SF_DETALLE_ABONO';
                       MI_ERROR(2).CLAVE := 'CAMPOS';
                       MI_ERROR(2).VALOR :=  MI_CAMPOS;
                       PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                  UN_TABLAERROR => 'TEMP_SF_DETALLE_ABONO',
                                                  UN_ERROR_COD  => PCK_ERRORES.ERR_PR_CALCULARM_INSERTAR,
                                                  UN_REEMPLAZOS => MI_ERROR);

         END;           

      MI_NDCS := MI_NDCS + 1;

      IF MI_SALDO <= 1 THEN
         EXIT;
      END IF;
   END LOOP;
   PCK_FACT_GENERAL.PR_AJUSTARCUOTAS(UN_COMPANIA=>UN_COMPANIA,
                                     UN_USUARIO=>UN_USUARIO);

END PR_CALCUMENSUALIDADABONOPORC;

--12
PROCEDURE PR_CALCULARMENSUALIDADABONO
/*
      NAME              : PR_CALCULARMENSUALIDADABONO MIGRADO DE ACCESS calcularMensualidadAbono
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JUAN CAMILO RODRIGUEZ DEIAZ
      DATE MIGRADOR     : 23/11/2017
      TIME              : 10:30 AM
      SOURCE MODULE     : SYSMANSF2017.11.01
      DESCRIPTION       :RTN DISTRIBUYE LAS CUOTAS
                         CALVAL AJUSTADO POR HENRY PUERTO EL MARTES 13 DE SEPTIEMBRE DEL 2005
                         HENRY PUERTO CAJA DE RETIROS
                         FUNCIÓN TOMADA DEL MODULO SYSMANFAB - 07 MARZO 2011 (18/01/2011) - AJUSTADA SEGÚN LA ESTRUCTURA DE LA BASE DE DATOS DE SYSMANSF Y
                         MEJORANDO EL PROCESO EN CUANTO A QUE NO SE ESTABAN TENIENDO EN CUENTA LOS INTERESES GENERADOS POR LA DEUDA ORIGINAL
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 

    @NAME:   calcularMensualidadAbono
    @METHOD: POST
*/
(
 UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
 UN_TIPOABONO     IN PCK_SUBTIPOS.TI_PARAMETRO,
 UN_NROABONO      IN PCK_SUBTIPOS.TI_DOBLE,
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
  MI_SALDO                 PCK_SUBTIPOS.TI_DOBLE;
  MI_SALDO1                PCK_SUBTIPOS.TI_DOBLE;
  MI_SALDOI                PCK_SUBTIPOS.TI_DOBLE;
  MI_AI                    PCK_SUBTIPOS.TI_ENTERO;
  MI_MI                    PCK_SUBTIPOS.TI_ENTERO;
  MI_AI0                   PCK_SUBTIPOS.TI_ENTERO;
  MI_MI0                   PCK_SUBTIPOS.TI_ENTERO;
  MI_NC                    PCK_SUBTIPOS.TI_ENTERO;
  MI_NA                    PCK_SUBTIPOS.TI_DOBLE;
  MI_GR                    PCK_SUBTIPOS.TI_DOBLE;
  MI_GR1                   PCK_SUBTIPOS.TI_DOBLE;
  MI_VM                    PCK_SUBTIPOS.TI_DOBLE;
  MI_VMSI                  PCK_SUBTIPOS.TI_DOBLE; --' VALOR MONTO SIN INTERES
  MI_TI                    PCK_SUBTIPOS.TI_DOBLE; --' TASA DE INTERES ANUAL
  MI_TI1                   PCK_SUBTIPOS.TI_DOBLE;
  MI_TIE                   PCK_SUBTIPOS.TI_DOBLE;
  MI_VC                    PCK_SUBTIPOS.TI_DOBLE;
  MI_VCI                   PCK_SUBTIPOS.TI_DOBLE;
  MI_VCSI                  PCK_SUBTIPOS.TI_DOBLE; --' VALOR DE LA CUOTA SIN INTERESES
  MI_CUO                   PCK_SUBTIPOS.TI_DOBLE;
  MI_CUOI                  PCK_SUBTIPOS.TI_DOBLE;
  MI_CUOSI                 PCK_SUBTIPOS.TI_DOBLE; --' VALOR CUOTA DEL COMPONENTE DE CAPITAL SIN INTERESES
  MI_CUO1                  PCK_SUBTIPOS.TI_DOBLE;
  MI_NDCS                  PCK_SUBTIPOS.TI_ENTERO;
  MI_NDCR                  PCK_SUBTIPOS.TI_ENTERO;
  MI_XX1                   PCK_SUBTIPOS.TI_PARAMETRO;
  MI_XX                    PCK_SUBTIPOS.TI_ENTERO;
  MI_VALINT                PCK_SUBTIPOS.TI_DOBLE;
  MI_VALREC                PCK_SUBTIPOS.TI_DOBLE;
  MI_VALSAL                PCK_SUBTIPOS.TI_DOBLE;
  MI_INC                   PCK_SUBTIPOS.TI_DOBLE;
  MI_FECHAINICIO           DATE;
  MI_ABONO                 PCK_SUBTIPOS.TI_DOBLE;
  MI_ABONO1                PCK_SUBTIPOS.TI_DOBLE;
  MI_NUEVACUOTA            PCK_SUBTIPOS.TI_ENTERO;
  MI_INTDIGITOSREDONDEO    PCK_SUBTIPOS.TI_ENTERO;
  MI_STRPARAMETRO          PCK_SUBTIPOS.TI_PARAMETRO;
  MI_TIM                   PCK_SUBTIPOS.TI_DOBLE;
  MI_DIASCOBREC            PCK_SUBTIPOS.TI_ENTERO;
  MI_FECHACORTETEMP        DATE;
  MI_INTPRIMERAVEZ         PCK_SUBTIPOS.TI_ENTERO;
  MI_FECHAINICIO1          DATE;
  MI_INTDIAS               PCK_SUBTIPOS.TI_ENTERO;
  MI_INTMES                PCK_SUBTIPOS.TI_ENTERO;
  MI_INTANIO               PCK_SUBTIPOS.TI_ENTERO;
  MI_STRCORTE              PCK_SUBTIPOS.TI_PARAMETRO;
  MI_DIASINICIO            PCK_SUBTIPOS.TI_ENTERO;
  MI_VALCUOPRIM            PCK_SUBTIPOS.TI_DOBLE;
  MI_DIASNOCOBRO1CUOTA     PCK_SUBTIPOS.TI_ENTERO;
  MI_SALDOINTERES          PCK_SUBTIPOS.TI_DOBLE;
  MI_NITDELACOMPANIA       PCK_SUBTIPOS.TI_PARAMETRO;
  MI_VALCUOSI              PCK_SUBTIPOS.TI_DOBLE;
  MI_AIANT                 PCK_SUBTIPOS.TI_ENTERO;
  MI_MANEJAGRADIENTESXANO  PCK_SUBTIPOS.TI_LOGICO;
  MI_VALCUO                PCK_SUBTIPOS.TI_DOBLE;
  MI_VALCUOI               PCK_SUBTIPOS.TI_DOBLE;
  MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES               PCK_SUBTIPOS.TI_CAMPOS;
  MI_ACME                  PCK_SUBTIPOS.TI_ENTERO;
  MI_ERROR                 PCK_SUBTIPOS.TI_CLAVEVALOR; 
BEGIN

   MI_INTDIGITOSREDONDEO := 0;

   --'SE ALMACENA EN LA VARIABLE NC EL NÚMERO DE CUOTAS DEL ACUERDO DE PAGO
   MI_NC := CASE WHEN UN_NCUOTAS <= 0 THEN  1 ELSE UN_NCUOTAS END;
   MI_GR := UN_TASAGRADIENTE;

   --'ALMACENA EN LA VARIABLE VM (VALOR MONTO) EL VALOR CAPITAL DE LA DEUDA LA CUAL SER? LA BASE PARA CALCULAR LOS INTERES DE FINANCIACIÓN
   MI_VM    := UN_MONTO;--' - MONTOINTERES
   MI_SALDO := MI_VM; --  ' REASIGNA EL VALOR DEL SALDO TENIENDO EN CUENTA QUE AL MONTO DE LA DEUDA SE LE RESTA EL MONTO DE INTERESES

   MI_TI := CASE WHEN UN_TASAINTERES = 0 THEN  0 ELSE  UN_TASAINTERES END;

   IF UN_BLSIMPLE NOT IN (0) THEN
      MI_TIM := MI_TI / 1200 ;  --' TASA DE INTERES MENSUAL
   ELSE
      MI_TIM :=POWER((1 + (MI_TI / 100)) , (1 / 12)) - 1 ;
   END IF;

   IF MI_GR > 0 THEN
      MI_GR1 := MI_GR / 100;
      MI_NA  := UN_NCUOTAS / 12;
      MI_TI1 := MI_TIM;
      MI_TIE := POWER(1 + MI_TIM, 12) - 1;-- 'INTERES PERIODO EQUIVALENTE
      MI_VC  := (UN_MONTO * (((MI_TIE - MI_GR1) * POWER(1 + MI_TIE, MI_NA)) / (POWER(1 + MI_TIE, MI_NA) - POWER(1 + MI_GR1,MI_NA)))) * (MI_TI1 / (POWER(1 + MI_TI1, 12) - 1));

      MI_VALCUOPRIM := MI_VC;
   ELSE
      IF MI_NC <> 0 THEN
         MI_VC := MI_VM / MI_NC;
         MI_VCI:= UN_MONTOINTERES / MI_NC; --'SE ADICIONA PARA TENER EN CUENTA EN LA CREACIÓN DE LA CUOTAS EL MONTO DE LOS INTERESES- 18/01/2011
      ELSE
         MI_VC := 0;
         MI_VCI:= 0;
      END IF;
   END IF;

   MI_VC   := ROUND(MI_VC, MI_INTDIGITOSREDONDEO);
   MI_CUO  := MI_VC;
   MI_VCI  := ROUND(MI_VCI, MI_INTDIGITOSREDONDEO);
   MI_CUOI := MI_VCI;

   MI_NDCS       := 0;
   MI_NUEVACUOTA := 1;

   MI_INTPRIMERAVEZ := 1;
   MI_DIASINICIO    := 0;

   WHILE TRUE LOOP
      MI_FECHAINICIO  := ADD_MONTHS(TO_DATE(UN_PRIMERFECHA,'DD/MM/YYYY HH24:MI:SS'),MI_NDCS + MI_NUEVACUOTA);
      MI_FECHAINICIO1 := MI_FECHAINICIO;

      MI_AI := EXTRACT(YEAR FROM MI_FECHAINICIO);
      MI_MI := EXTRACT(MONTH FROM MI_FECHAINICIO);      
      MI_CUO1 := MI_CUO;

      IF TO_NUMBER((MI_NDCS + MI_NUEVACUOTA - 1) / 12) * 12 = (MI_NDCS + MI_NUEVACUOTA - 1) AND (MI_NDCS + MI_NUEVACUOTA - 1) > 0 AND MI_GR <> 0 THEN
         MI_INC  := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR    =>MI_CUO * (MI_GR / 100),
                                            UN_PRECISION=>0);
         MI_CUO  := MI_CUO + MI_INC;
         MI_CUO  := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR    =>MI_CUO,
                                            UN_PRECISION=>MI_INTDIGITOSREDONDEO);
         MI_CUO1 := MI_CUO;
      END IF;

      MI_VALCUO  := MI_CUO;
      MI_VALCUOSI:= MI_CUOSI;
      MI_VALCUOI := MI_CUOI;

      MI_XX      := 0;

      IF (MI_FECHAINICIO <= UN_FECHADECORTE) THEN
        -- ' DETERMINA LA ANTIGUDAD DE LA DEUDA DE ESTA CUOTA
         --' SI LOS DIAS SON MAS DE DIEZ TOMA EL MES COMPLETO.

         MI_XX := MONTHS_BETWEEN(UN_FECHADECORTE,MI_FECHAINICIO1);
      END IF;

      MI_VALINT := ROUND(MI_SALDO * MI_TIM, MI_INTDIGITOSREDONDEO);

     -- 'IF (GR > 0 AND NDCS + NUEVACUOTA >= NCUOTAS) OR (GR <= 0 AND NDCS + NUEVACUOTA >= NCUOTAS) THEN  'OJO CON ESTE +1 EN LA CAJA DE RETIROS
      --'   VALINT = 0
      --'END IF

      MI_VALREC := CASE WHEN MI_FECHAINICIO >= UN_FECHADECORTE + MI_DIASCOBREC THEN  0 ELSE PCK_SYSMAN_UTL.FC_ROUND((MI_VALCUO - MI_VCSI) * MI_XX * UN_TASARECARGO / 100 / 12, 2) END;

      --'TIENE EN CUENTA EL ABONO

      MI_SALDO  := MI_SALDO - MI_VALCUO; -- ' CALCULA EL NUEVO SALDO DEL MONTO CAPITAL
      MI_SALDOI := MI_SALDOI - MI_CUOI;

     -- 'SI SON ENTRE 1 Y 10 PESOS PARA EL SALDO, SE LE SUMAN A LA ÚLTIMA CUOTA.

      IF MI_SALDO <= 10 THEN
         MI_VALCUO := MI_VALCUO + MI_SALDO;
         MI_SALDO  := 0;
      END IF;

      IF MI_SALDOI <= 10 THEN
         MI_VALCUOI:= MI_VALCUOI + MI_SALDOI;
         MI_SALDOI := 0;
      END IF;

      BEGIN
           BEGIN
                MI_CAMPOS:='COMPANIA,'||
                           'TIPO_ABONO,'||
                           'CODIGO_ABONO,'||
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
                              TO_CHAR(NVL(MI_NDCS,0) + NVL(MI_NUEVACUOTA,0))||','||
                              'TO_DATE('''||MI_FECHAINICIO||''',''DD/MM/YYYY HH24:MI:SS''),'||
                              MI_VALCUO||','||
                              MI_VALCUOI||','||
                              MI_VALINT||','||
                              '0,'||
                              TO_CHAR(MI_VALCUO + MI_VALCUOI + MI_VALINT)||',
                              ''A'','||
                              ''''||UN_USUARIO||''','||
                              'SYSDATE';

                    MI_ACME:=PCK_DATOS.FC_ACME(UN_ACCION  =>'I',
                                               UN_TABLA   =>'TEMP_SF_DETALLE_ABONO',
                                               UN_CAMPOS  =>MI_CAMPOS,
                                               UN_VALORES =>MI_VALORES);   
                    EXCEPTION
                         WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
             END;

             EXCEPTION
                  WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                       MI_ERROR(1).CLAVE := 'TABLA';
                       MI_ERROR(1).VALOR := 'TEMP_SF_DETALLE_ABONO';
                       MI_ERROR(2).CLAVE := 'CAMPOS';
                       MI_ERROR(2).VALOR :=  MI_CAMPOS;
                       PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                  UN_TABLAERROR => 'TEMP_SF_DETALLE_ABONO',
                                                  UN_ERROR_COD  => PCK_ERRORES.ERR_PR_CALCULARM_INSERTAR,
                                                  UN_REEMPLAZOS => MI_ERROR);
            END;
      MI_NDCS := MI_NDCS + 1;

      IF MI_SALDO <= 1 THEN
         EXIT; 
      END IF;
   END LOOP;

END PR_CALCULARMENSUALIDADABONO;

--13
FUNCTION FC_MANEJADERCONEXIONCONCEPTO 
/*
      NAME              : FC_MANEJADERCONEXIONCONCEPTO MIGRADO DE ACCESS ManejaDerConexionConcepto
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JUAN CAMILO RODRIGUEZ DIAZ
      DATE MIGRADOR     : 27/11/2017
      TIME              : 10:30 AM
      SOURCE MODULE     : SYSMANSF2017.11.01
      DESCRIPTION       :
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 

    @NAME:   manejarDerConexionConcepto
    @METHOD: GET
*/
(
  UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANO        IN PCK_SUBTIPOS.TI_ANIO,
  UN_TIPOCOBRO  IN PCK_SUBTIPOS.TI_TIPO_COBRO,
  UN_CONCEPTO   IN PCK_SUBTIPOS.TI_PARAMETRO
)
RETURN PCK_SUBTIPOS.TI_LOGICO AS 
MI_RPTA                  PCK_SUBTIPOS.TI_LOGICO;
MI_IND_DERECHOSCONEXION  SF_TIPO_COBRO.IND_DERECHOSCONEXION%TYPE;
MI_FORMULAR_ACOMETIDA    SF_CONCEPTOS.FORMULAR_ACOMETIDA%TYPE;
MI_FORMULAR_ADMONIMP     SF_CONCEPTOS.FORMULAR_ADMONIMP%TYPE;
MI_FORMULAR_UTILIDADAIU  SF_CONCEPTOS.FORMULAR_UTILIDADAIU%TYPE;
MI_FORMULAR_IVAUTILIDAD  SF_CONCEPTOS.FORMULAR_IVAUTILIDAD%TYPE;
BEGIN


    BEGIN
        SELECT IND_DERECHOSCONEXION
        INTO MI_IND_DERECHOSCONEXION
        FROM SF_TIPO_COBRO
        WHERE COMPANIA = UN_COMPANIA
        AND ANO        = UN_ANO
        AND CODIGO     = UN_TIPOCOBRO;


        MI_RPTA := NVL(MI_IND_DERECHOSCONEXION, 0);

        BEGIN
              SELECT FORMULAR_ACOMETIDA,
                FORMULAR_ADMONIMP,
                FORMULAR_UTILIDADAIU,
                FORMULAR_IVAUTILIDAD
                INTO
                MI_FORMULAR_ACOMETIDA,
                MI_FORMULAR_ADMONIMP,
                MI_FORMULAR_UTILIDADAIU,
                MI_FORMULAR_IVAUTILIDAD
              FROM SF_CONCEPTOS
              WHERE COMPANIA = UN_COMPANIA
              AND ANO        = UN_ANO
              AND TIPOCOBRO  = UN_TIPOCOBRO
              AND CODIGO     = UN_CONCEPTO;

              IF MI_FORMULAR_ACOMETIDA NOT IN('0') AND MI_FORMULAR_ADMONIMP NOT IN('0') AND MI_FORMULAR_UTILIDADAIU NOT IN('0') AND MI_FORMULAR_IVAUTILIDAD NOT IN('0') THEN
                  MI_RPTA := -1;
              ELSE
                  MI_RPTA := 0;
              END IF;
             EXCEPTION 
                  WHEN NO_DATA_FOUND THEN
                  MI_RPTA:=0;
                  WHEN TOO_MANY_ROWS THEN
                  MI_RPTA:=0;   
        END;


        EXCEPTION 
          WHEN NO_DATA_FOUND THEN
          MI_RPTA:=0;
          WHEN TOO_MANY_ROWS THEN
          MI_RPTA:=0;           
    END;

  RETURN MI_RPTA;
END FC_MANEJADERCONEXIONCONCEPTO;

--14
FUNCTION FC_CONSULTARABONO
/*
      NAME              : FC_CONSULTARABONO 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 20/11/2017
      TIME              : 10:30 AM
      SOURCE MODULE     : SysmanSF2017.11.01
      DESCRIPTION       : metodo ConsultarAbono();
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 

    @NAME:   consultarAbono
    @METHOD: GET
*/
(
  UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOABONO          IN SF_DETALLE_ABONO.TIPO_ABONO%TYPE,
  UN_ABONO              IN SF_DETALLE_ABONO.CODIGO_ABONO%TYPE

)RETURN VARCHAR2
  AS
  MI_RETORNO          VARCHAR(500 CHAR);
  MI_CUOTA            NUMBER(5,0);
  MI_FECHACUOTA       DATE;
  MI_TOTALCUOTA       NUMBER(20,2);


BEGIN  
      SELECT SF_DETALLE_ABONO.CUOTA,
        SF_DETALLE_ABONO.FECHACUOTA,
        SF_DETALLE_ABONO.TOTAL_CUOTA
      INTO
        MI_CUOTA,
        MI_FECHACUOTA,
        MI_TOTALCUOTA
      FROM SF_DETALLE_ABONO
      WHERE SF_DETALLE_ABONO.COMPANIA     = UN_COMPANIA
        AND SF_DETALLE_ABONO.TIPO_ABONO   = UN_TIPOABONO
        AND SF_DETALLE_ABONO.CODIGO_ABONO = UN_ABONO
        AND SF_DETALLE_ABONO.ESTADO       IN ('A')
      AND ROWNUM <= 1;

   MI_RETORNO :=  MI_CUOTA ||','||MI_FECHACUOTA||','||MI_TOTALCUOTA;

   RETURN MI_RETORNO;

  EXCEPTION WHEN NO_DATA_FOUND THEN
    MI_RETORNO := 'NE';

    RETURN MI_RETORNO;

END FC_CONSULTARABONO;

--15
PROCEDURE PR_ACTPAGOSABONOS
/*
      NAME              : PR_ACTPAGOSABONOS 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 21/11/2017
      TIME              : 09:30 AM
      SOURCE MODULE     : SysmanSF2017.11.01
      DESCRIPTION       : metodo ActPagosAbonos();
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 

    @NAME:   actualizarPagosAbono
    @METHOD: POST
*/
(
  UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOABONO          IN SF_DETALLE_ABONO.TIPO_ABONO%TYPE,
  UN_ABONO              IN SF_DETALLE_ABONO.CODIGO_ABONO%TYPE,
  UN_TIPOFACTURA        IN SF_FACTURA.TIPO_FACTURA%TYPE,
  UN_NOFACTURA          IN SF_FACTURA.NUMERO_FACTURA%TYPE,
  UN_CODIGOCOBRO        IN SF_FACTURA.CODIGO_COBRO%TYPE,    
  UN_FECHAPAGO          IN SF_FACTURA.FECHA_PAGO%TYPE,
  UN_TIPOCPTPAGO        IN SF_FACTURA.TIPOCPTE_PAGO%TYPE,
  UN_CPTEPAGO           IN SF_FACTURA.NROCPTE_PAGO%TYPE,  
  UN_BANCOPAGO          IN SF_FACTURA.BANCO_PAGO%TYPE,
  UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO

)
  AS

  MI_CANT             NUMBER;
  MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
  MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;  

BEGIN 

      SELECT COUNT(ESTADO) CANT
      INTO MI_CANT
      FROM SF_DETALLE_ABONO
      WHERE COMPANIA   = UN_COMPANIA
      AND TIPO_ABONO   = UN_TIPOABONO
      AND CODIGO_ABONO = UN_ABONO
      AND ESTADO       = 'A' ;

      IF MI_CANT > 0 THEN
        BEGIN
         BEGIN
           RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;     
         END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD =>SQLCODE,
              UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_FACTABONOPEND
            );
        END;

      ELSE



        --Actualización indicador de recaudo
        BEGIN
            BEGIN

                MI_CAMPOS    := 'INDPAGO   = -1';

                MI_CONDICION := ' COMPANIA     = '''||UN_COMPANIA||'''
                              AND TIPOCOBRO    = '''||UN_TIPOFACTURA||'''
                              AND CODIGO_COBRO = '||UN_CODIGOCOBRO;

                PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME(UN_TABLA    => 'SF_OBJETO_COBRO',
                                           UN_ACCION    => 'M',
                                           UN_CAMPOS    => MI_CAMPOS,
                                           UN_CONDICION => MI_CONDICION);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                      UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTUA_OBJETO_COB);
        END;

       BEGIN
        BEGIN
          MI_CAMPOS := ' ANOCPTE_PAGO   = '||EXTRACT(YEAR FROM UN_FECHAPAGO)||',
                         TIPOCPTE_PAGO  = '''||UN_TIPOCPTPAGO||''' ,
                         NROCPTE_PAGO   = '||UN_CPTEPAGO||',
                         FECHA_PAGO     = '''||UN_FECHAPAGO||''',
                         BANCO_PAGO     = '''||UN_BANCOPAGO||''' ';

          MI_CONDICION := 'COMPANIA       = ''' || UN_COMPANIA ||''' 
                       AND TIPO_FACTURA   = ''' || UN_TIPOFACTURA ||'''   
                       AND NUMERO_FACTURA = '   || UN_NOFACTURA  ||'';

          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  'SF_FACTURA', 
                                                 UN_ACCION    =>  'M', 
                                                 UN_CAMPOS    =>  MI_CAMPOS, 
                                                 UN_CONDICION =>  MI_CONDICION );

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;

        END;
           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN    
               MI_MSGERROR(1).CLAVE := 'FACTURA';
               MI_MSGERROR(1).VALOR := UN_NOFACTURA;

                    PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD   => SQLCODE,
                    UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_ACTPAGOFACTURA,
                    UN_REEMPLAZOS  => MI_MSGERROR
                 );
       END;

       BEGIN
        BEGIN
          MI_CAMPOS := 'ESTADO = ''P'' ';

          MI_CONDICION := 'COMPANIA  = ''' || UN_COMPANIA ||''' 
                       AND TIPO      = ''' || UN_TIPOFACTURA ||'''   
                       AND CODIGO    = '   || UN_NOFACTURA  ||'';

          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  'SF_ABONOS', 
                                                 UN_ACCION    =>  'M', 
                                                 UN_CAMPOS    =>  MI_CAMPOS, 
                                                 UN_CONDICION =>  MI_CONDICION );

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN    
               MI_MSGERROR(1).CLAVE := 'FACTURA';
               MI_MSGERROR(1).VALOR := UN_NOFACTURA;

                    PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD   => SQLCODE,
                    UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_ACTESTADOFACTURA,
                    UN_REEMPLAZOS  => MI_MSGERROR
                 );

       END;


      END IF;

END PR_ACTPAGOSABONOS;


 --17
  FUNCTION FC_CONSULTARTIPOCOMP 
  /*
      NAME              : FC_CONSULTARTIPOCOMP -- CpteRecaudo_TipoCobro en Asobancaria en Access 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : LEYDI MILENA CORTÉS FORERO
      DATE MIGRADO      : 22/11/2017
      TIME              : 03:12 PM
      SOURCE MODULE     : SysmanSF2017.11.01
      DESCRIPTION       : Función que permite consultar el codigo de tipo comprobante para retornarlo.
                          Ruta: Panel Principal\Facturacion General\Procesos\Registro de pagos.
      PARAMETERS        : UN_COMPANIA  => Compañia de ingreso a la aplicación.  
                          UN_ANIO      => Año seleccionado al ingresar al módulo.
                          UN_TIPOCOBRO => Tipo de cobro seleccionado al ingresar al módulo.
                          UN_MODULO    => Código del módulo de acceso a la aplicación, para el caso, el código asigando a sysmansf.

      @NAME:  consultarTipoComprobante
      @METHOD:  GET
      */  
  (
    UN_COMPANIA               IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO                   IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPOCOBRO              IN PCK_SUBTIPOS.TI_TIPO_COBRO,
    UN_MODULO                 IN PCK_SUBTIPOS.TI_MODULO
  )
  RETURN VARCHAR2 AS 
    MI_RTACPTERECAUDO        TIPO_COMPROBANTE.CODIGO%TYPE;
    MI_CPTERECAUDO           SF_TIPO_COBRO.CPTE_RECAUDO%TYPE;
    MI_PARCPTERECAUDO        PCK_SUBTIPOS.TI_PARAMETRO; 
    MI_TIPOCOMPROBANTE       TIPO_COMPROBANTE.CODIGO%TYPE;
  BEGIN
    --' Consulta si al tipo de cobro ingresado por parámetro se le ha asignado un tipo de comprobante de recaudo para cargarlo 
    --  en el formulario
    --' Abril 07/2011
    MI_RTACPTERECAUDO := '';
    BEGIN
      SELECT CPTE_RECAUDO
        INTO MI_CPTERECAUDO
        FROM SF_TIPO_COBRO
       WHERE COMPANIA = UN_COMPANIA
         AND ANO      = UN_ANIO
         AND CODIGO   = UN_TIPOCOBRO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_CPTERECAUDO := NULL;
    END;

    IF MI_CPTERECAUDO IS NULL
    THEN
      MI_RTACPTERECAUDO := '';
      MI_PARCPTERECAUDO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                     UN_NOMBRE 	  => 'SF TIPO COMPROBANTE RECAUDO',
                                                     UN_MODULO    => UN_MODULO,
                                                     UN_FECHA_PAR => SYSDATE,
                                                     UN_IND_MAYUS => -1), 'SIN CONFIGURAR');
      --'Evaluar la validez del valor del parametro 
      IF MI_PARCPTERECAUDO = 'SIN CONFIGURAR'
      THEN --' Si la configuración no es valida 
        MI_PARCPTERECAUDO := 'P';
      ELSE --' El parámetro ha sido configurado, pero se debe evaluar si el tipo corresponde a uno de ingreso 
        BEGIN
          SELECT CODIGO
            INTO MI_TIPOCOMPROBANTE
            FROM TIPO_COMPROBANTE
           WHERE COMPANIA       = UN_COMPANIA
             AND CODIGO         = MI_CPTERECAUDO
             AND CLASE_CONTABLE = 'I';
        EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_TIPOCOMPROBANTE := NULL;
        END;

        IF MI_TIPOCOMPROBANTE IS NOT NULL
        THEN
          MI_RTACPTERECAUDO := MI_TIPOCOMPROBANTE;
        ELSE
          MI_RTACPTERECAUDO := '';
        END IF;
      END IF;
    ELSE 
      BEGIN
        SELECT CODIGO
          INTO MI_TIPOCOMPROBANTE
          FROM TIPO_COMPROBANTE
         WHERE COMPANIA       = UN_COMPANIA
           AND CODIGO         = MI_CPTERECAUDO
           AND CLASE_CONTABLE = 'I';
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_TIPOCOMPROBANTE := NULL;
      END;

      IF MI_TIPOCOMPROBANTE IS NOT NULL
      THEN
        MI_RTACPTERECAUDO := MI_TIPOCOMPROBANTE;
      ELSE
        MI_RTACPTERECAUDO := '';
      END IF;
    END IF;

    RETURN MI_RTACPTERECAUDO;
  END FC_CONSULTARTIPOCOMP;


--18
 FUNCTION FC_ACT_VALOR_ELEMENTO
/*
    NAME              : PR_ACT_VALOR_ELEMENTO  --> ACCESS  ActValorElemento
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : JONATHAN ENRIQUE GUERRERO TORRES
    DATE MIGRADOR     : 23/11/2017 
    TIME              : 08:34 AM
    SOURCE MODULE     : FACTURACION GENERAL
    DESCRIPTION       : Este procedimiento evalua si el concepto se encuentra en inventario si es asi, calcula el valor
                        de la compra y de la venta y la inserta  en el concepto.
                        Si el retorno es -1, el elemento no existe en el inventario
    PARAMETERS        : UN_COMPANIA	     => Compañia a crear
                        UN_ANIO          => Año para el cual se va a crear
                        UN_ELEMENTO      => Elemento o concepto a actualizar.
                        UN_PORC_UTILIDAD => Porcentaje de utilidad con el que se calcula el valor de la compra
                        UN_USUARIO       => Usuario que ejecuta el proceso
                        UN_TIPOCOBRO
    @NAME:   insertarElementoConcepto
    @METHOD: POST
    */
( UN_COMPANIA      IN  PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO          IN PCK_SUBTIPOS.TI_ANIO,
  UN_TIPOCOBRO     IN VARCHAR2,
  UN_ELEMENTO      IN VARCHAR2,
  UN_PORC_UTILIDAD IN PCK_SUBTIPOS.TI_PORCENTAJE,
  UN_USUARIO       IN PCK_SUBTIPOS.TI_USUARIO )

  RETURN PCK_SUBTIPOS.TI_ENTERO_LARGO AS 

 MI_VLR_EN             VARCHAR2(10 CHAR);
 MI_DIGITO_EN          PCK_SUBTIPOS.TI_ENTERO;
 MI_CONSULTA           PCK_SUBTIPOS.TI_STRSQL;
 MI_VLRUNITARIO_PROM   INVENTARIO.VLRUNITARIOPROM%TYPE;
 MI_EXISTENCIA         INVENTARIO.EXISTENCIA%TYPE;
 MI_PARAMETRO          VARCHAR2(2 CHAR);
 MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
 MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
 MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
 MI_VALOR_VENTA        INVENTARIO.VLRUNITARIOPROM%TYPE; 
 MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
 MI_RTA                PCK_SUBTIPOS.TI_RTA_ACME;
 MI_RETORNO            PCK_SUBTIPOS.TI_ENTERO_LARGO;
BEGIN
  MI_RETORNO := 0;

  MI_VLR_EN := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                     UN_NOMBRE    => 'SF REDONDEAR VALOR DEL CONCEPTO CON UTILIDAD', 
                                     UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                     UN_FECHA_PAR => SYSDATE);

   IF MI_VLR_EN = 'CIENES' THEN 
    MI_DIGITO_EN := -2;
   ELSE IF MI_VLR_EN = 'MILES' THEN 
          MI_DIGITO_EN := -3;
        ELSE 
          MI_DIGITO_EN := 2;
        END IF;
   END IF;

   BEGIN
     SELECT VLRUNITARIOPROM,
            EXISTENCIA
       INTO     
            MI_VLRUNITARIO_PROM,
            MI_EXISTENCIA
       FROM INVENTARIO
      WHERE COMPANIA       = UN_COMPANIA
        AND CODIGOELEMENTO = UN_ELEMENTO;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
      MI_VLRUNITARIO_PROM := NULL;
      MI_EXISTENCIA       := NULL;
      MI_VALOR_VENTA      := NULL;

    END;

    BEGIN 
      IF MI_VLRUNITARIO_PROM  = 0 THEN 
         RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
      END IF;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                   UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_FACTURAR_CONCEP);  
    END;
    BEGIN 
      IF MI_EXISTENCIA = 0 THEN 
          RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
      END IF;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                   UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_EXISTENCIAS_CERO);  
    END;
    MI_VALOR_VENTA := MI_VLRUNITARIO_PROM+(MI_VLRUNITARIO_PROM*UN_PORC_UTILIDAD);
    MI_VALOR_VENTA := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_VALOR_VENTA ,
                                              UN_PRECISION => MI_DIGITO_EN);

    MI_PARAMETRO :=  PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                        UN_NOMBRE    => 'SF USAR PORC UTILIDAD PARA CONCEPTOS DE ALMANCEN', 
                                        UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                        UN_FECHA_PAR => SYSDATE);

    IF MI_EXISTENCIA IS NULL THEN 
      MI_RETORNO := -1;
      MI_VLRUNITARIO_PROM := 0;
      MI_EXISTENCIA       := 0;
      MI_VALOR_VENTA      := 0;
    ELSE 
      MI_RETORNO := MI_EXISTENCIA;
    END IF; 

    IF MI_PARAMETRO = 'NO' THEN 
      BEGIN 
        BEGIN 
            MI_CAMPOS := 'VALOR_COMPRA = '||MI_VLRUNITARIO_PROM||',
                          EXISTENCIA   = '||MI_EXISTENCIA ||',
                          MODIFIED_BY   = '''||UN_USUARIO||''', 
                          DATE_MODIFIED = SYSDATE ';

            MI_CONDICION := ' COMPANIA  = ''' ||UN_COMPANIA||'''
                          AND ANO       = '||UN_ANIO ||'
                          AND TIPOCOBRO = '''||UN_TIPOCOBRO ||'''
                          AND CODIGO    = '''||UN_ELEMENTO||'''';

            MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'SF_CONCEPTOS', 
                                        UN_ACCION    => 'M', 
                                        UN_CAMPOS    => MI_CAMPOS, 
                                        UN_CONDICION => MI_CONDICION );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
	      END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_TABLAERROR => MI_TABLA,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTUAL_CONCEPTO);
      END;

    ELSE 
      BEGIN 
        BEGIN 
            MI_CAMPOS := 'VALOR_COMPRA = '||MI_VLRUNITARIO_PROM||',
                          VALOR_BASE   = '||MI_VALOR_VENTA||',
                          EXISTENCIA   = '||MI_EXISTENCIA ||',
                          MODIFIED_BY   = '''||UN_USUARIO||''', 
                          DATE_MODIFIED = SYSDATE ';

            MI_CONDICION := ' COMPANIA  = ''' ||UN_COMPANIA||'''
                          AND ANO       = '||UN_ANIO ||'
                          AND TIPOCOBRO = '''||UN_TIPOCOBRO ||'''
                          AND CODIGO    = '''||UN_ELEMENTO||'''';

            MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'SF_CONCEPTOS', 
                                        UN_ACCION    => 'M', 
                                        UN_CAMPOS    => MI_CAMPOS, 
                                        UN_CONDICION => MI_CONDICION );  
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
	      END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_TABLAERROR => MI_TABLA,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTUAL_CONCEPTO);
      END;                      
    END IF;

    RETURN MI_RETORNO;
END FC_ACT_VALOR_ELEMENTO;

PROCEDURE PR_FACTURAR_SERIE(
/*
  NAME              : PR_FACTURAR_SERIE
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
  DATE MIGRADOR     : 07/05/2020
  TIME              : 04:30 PM
  SOURCE MODULE     : SYSMASF
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : Procedimiento que se encarga de realizar la facuracion en serie de contratos de facturacion.
  
  @NAME:  facturarEnSerie
  @METHOD:  GET
*/
    UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_ANIO                    IN SF_CONTRATOS.ANO%TYPE, 
    UN_TIPOCOBRO               IN SF_CONTRATOS.TIPO%TYPE,
    UN_CONTRATOINICIAL         IN SF_CONTRATOS.NUMERO%TYPE,
    UN_CONTRATOFINAL           IN SF_CONTRATOS.NUMERO%TYPE,
    UN_LUGAR                   IN VARCHAR2,
    UN_FECHAFACTURA            IN SF_OBJETO_COBRO.FECHA_SOLICITUD%TYPE,
    UN_USUARIO                 IN PCK_SUBTIPOS.TI_USUARIO 
)
AS
    MI_TABLA               PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS              PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES             PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION           PCK_SUBTIPOS.TI_CONDICION;
    MI_CONSECUTIVO         SF_OBJETO_COBRO.CODIGO_COBRO%TYPE;
    MI_OBSERVACIONES       SF_OBJETO_COBRO.OBSERVACIONES%TYPE;
    MI_MSGERROR            PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_FECHAVENCIMIENTO    SF_OBJETO_COBRO.FECHA_VENCIMIENTO%TYPE;
    MI_INDPRELIQUIDACION   NUMBER(1,0);
    MI_MANEJACARTERA       NUMBER(1,0); 
    MI_INTERESCARTERA      NUMBER(1,0); 
    MI_TASAINTERES         SF_TASAS_INTERES.TASAINTERES%TYPE;
    MI_CONTADOR            PCK_SUBTIPOS.TI_ENTERO := 0; 
    MI_TOTALINTERES        SF_CARTERA.INTERES%TYPE := 0;
    MI_FACTURAACTUAL       SF_FACTURA.NUMERO_FACTURA%TYPE;   
    MI_CANTIDADFACTURASDEUDA  PCK_SUBTIPOS.TI_ENTERO := 0; 
    MI_NUMEROFACTURA       SF_FACTURA.NUMERO_FACTURA%TYPE;
    MI_FACTURASDEUDA       VARCHAR2(254 CHAR); 
     
BEGIN             

EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS = ''.,''';
  EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_TIMESTAMP_FORMAT =''DD/MM/YYYY HH24:MI:SS''';
  EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_DATE_FORMAT =''DD/MM/YYYY HH24:MI:SS''';
  EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_TIMESTAMP_TZ_FORMAT =''DD/MM/YYYY HH24:MI:SS''';
  EXECUTE IMMEDIATE 'ALTER SESSION SET TIME_ZONE=''-05:00''';

    <<CREAR_OBJETO_COBRO>>
    FOR MI_RS IN(SELECT SF_CONTRATOS.NUMERO,
                        SF_CONTRATOS.TERCERO,
                        SF_CONTRATOS.SUCURSAL,
                        SF_LOCALES.CODIGO CODIGOLOCAL,
                        SF_LUGAR.CODIGO CODIGOLUGAR,
                        SF_LUGAR.NOMBRE NOMBRELUGAR,
                        TERCERO.NOMBRE NOMBRETERCERO
                 FROM SF_CONTRATOS 
                  LEFT JOIN SF_LOCALES ON SF_CONTRATOS.COMPANIA = SF_LOCALES.COMPANIA  
                                      AND SF_CONTRATOS.LOCAL = SF_LOCALES.CODIGO     
                  LEFT JOIN SF_LUGAR ON SF_LOCALES.COMPANIA = SF_LUGAR.COMPANIA 
                                    AND SF_LOCALES.LUGAR = SF_LUGAR.CODIGO     
                  LEFT JOIN SF_UBICACION ON SF_LOCALES.COMPANIA = SF_UBICACION.COMPANIA                                
                                        AND SF_LOCALES.UBICACION = SF_UBICACION.CODIGO
                  LEFT JOIN TERCERO 
                      ON  SF_CONTRATOS.COMPANIA=TERCERO.COMPANIA
                      AND SF_CONTRATOS.TERCERO=TERCERO.NIT
                      AND SF_CONTRATOS.SUCURSAL=TERCERO.SUCURSAL
                          
                 WHERE SF_CONTRATOS.COMPANIA = UN_COMPANIA 
                   AND SF_CONTRATOS.TIPO = UN_TIPOCOBRO
                   AND SF_CONTRATOS.NUMERO BETWEEN UN_CONTRATOINICIAL AND UN_CONTRATOFINAL 
                   AND SF_LUGAR.CODIGO IN CASE WHEN UN_LUGAR = 'TODOS' THEN SF_LUGAR.CODIGO ELSE UN_LUGAR END 
                   AND SF_CONTRATOS.ESTADO = 2
                   AND SF_CONTRATOS.ANO = UN_ANIO
                   ORDER BY SF_CONTRATOS.NUMERO ) 
       LOOP    
           
           --Calcular consecutivo que tendra el objeto cobro
           MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    =>'SF_OBJETO_COBRO',
                                                              UN_CRITERIO => 'COMPANIA = '''||UN_COMPANIA||''' 
                                                                         AND TIPOCOBRO = '''||UN_TIPOCOBRO||'''
                                                                         AND SUBSTR(CODIGO_COBRO,1,4) = '|| UN_ANIO,
                                                              UN_CAMPO    => 'CODIGO_COBRO');   
                                                              
           --Observaciones que tendra el objeto cobro
           MI_OBSERVACIONES :=   'LIQUIDACION DEL MES ' || PCK_SYSMAN_UTL.FC_NOMBRE_MES(EXTRACT(MONTH FROM UN_FECHAFACTURA))|| ' DE ' || EXTRACT(YEAR FROM UN_FECHAFACTURA) ||
                                 ' DEL LOCAL No ' || MI_RS.CODIGOLOCAL || ' GALERIA '|| MI_RS.CODIGOLUGAR || ' ' || MI_RS.NOMBRELUGAR;  
                                 
        
           
           MI_CAMPOS    :=   'COMPANIA
                              ,ANO
                              ,TIPOCOBRO
                              ,CODIGO_COBRO
                              ,TERCERO
                              ,SUCURSAL
                              ,CONTRATO
                              ,TIPOCONTRATO
                              ,FECHA_SOLICITUD
                              ,FECHA_VENCIMIENTO
                              ,OBSERVACIONES
                              ,CREATED_BY
                              ,DATE_CREATED
                              ,NIT_TERCEROFACTURADO
                              ,SUCURSAL_TERCEROFACTURADO
                              ,NOMBRE_TERCEROFACTURADO';                        
                              
           MI_VALORES := ''''||UN_COMPANIA||''',
                            '||UN_ANIO||',
                          '''||UN_TIPOCOBRO||''',
                            '||MI_CONSECUTIVO||',
                          '''||MI_RS.TERCERO||''',
                          '''||MI_RS.SUCURSAL||''', 
                            '||MI_RS.NUMERO||',
                          '''||UN_TIPOCOBRO||''',  
                          '''||UN_FECHAFACTURA||''',
                          '''||MI_FECHAVENCIMIENTO||''',
                          '''||MI_OBSERVACIONES||''',
                          '''||UN_USUARIO||''',
                          SYSDATE,
                          '''||MI_RS.TERCERO||''',
                          '''||MI_RS.SUCURSAL||''',
                          '''||MI_RS.NOMBRETERCERO||'''';
           BEGIN
            BEGIN
               PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA => 'SF_OBJETO_COBRO',
                                                UN_ACCION    => 'I', 
                                                UN_CAMPOS    => MI_CAMPOS, 
                                                UN_VALORES => MI_VALORES);
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
               RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
            END;
               
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
                 MI_MSGERROR(0).CLAVE := 'CONTRATO';
                 MI_MSGERROR(0).VALOR := MI_RS.NUMERO;               
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_TABLAERROR => MI_TABLA,
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INCOBRCONTR,
                                           UN_REEMPLAZOS => MI_MSGERROR);  
            
           END;
           
           
           --Calcular campo FECHA_VENCIMIENTO                                      
           
           SELECT INDPRELIQUIDACION
             INTO MI_INDPRELIQUIDACION
           FROM SF_TIPO_COBRO 
           WHERE COMPANIA = UN_COMPANIA
             AND ANO      = UN_ANIO
             AND CODIGO   = UN_TIPOCOBRO;
             
           MI_FECHAVENCIMIENTO :=PCK_FACT_GENERAL_COM2.FC_CALCULAR_FECHA_VENCIMIENTO  (UN_COMPANIA     => UN_COMPANIA,
                                                                                       UN_TIPOCOBRO    => UN_TIPOCOBRO,
                                                                                       UN_APLICA_REL   => MI_INDPRELIQUIDACION,
                                                                                       UN_CODIGO_COBRO => MI_CONSECUTIVO);
           
           --Actualizar FECHA_VENCIMIENTO
           
            MI_CAMPOS    := 'FECHA_VENCIMIENTO = '''||MI_FECHAVENCIMIENTO||''' ';
            
            MI_CONDICION := '   COMPANIA     = '''||UN_COMPANIA||'''
                            AND CODIGO_COBRO = '||MI_CONSECUTIVO||'
                            AND ANO          = '||UN_ANIO||'
                            AND TIPOCOBRO    = '''||UN_TIPOCOBRO||'''';
            
           BEGIN
                BEGIN               
                    PCK_DATOS.GL_RTA :=PCK_DATOS.FC_ACME(UN_ACCION    =>'M',
                                                         UN_TABLA     =>'SF_OBJETO_COBRO',
                                                         UN_CAMPOS    =>MI_CAMPOS,
                                                         UN_CONDICION =>MI_CONDICION);   
                   
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                            RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                  END;
                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
                    MI_MSGERROR(0).CLAVE := 'CONTRATO';
                    MI_MSGERROR(0).VALOR := MI_RS.NUMERO;
                   
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,                                           
                                               UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTFECHAVCOBR,
                                               UN_REEMPLAZOS => MI_MSGERROR);  
            
           END;                  
           
            <<INSERTAR_DETALLES_CONCEPTO>>
            
             MI_VALORES := ' SELECT COMPANIA,
                                    '||UN_ANIO||',
                                    '''||UN_TIPOCOBRO||''',
                                    '||MI_CONSECUTIVO||',
                                     CONCEPTO,
                                     TARIFA,
                                     CANTIDAD,
                                     VALOR_BASE,
                                     VALOR_IVA,
                                     VALOR_RETE,
                                     VALOR_ICA,
                                     VALOR_DESCUENTO,
                                     VALOR_NETO,
                                     VALOR_COMPRA,
                                     VALOR_UTILIDAD,
                                     TERCERO,
                                     SUCURSAL,
                                     CENTRO_COSTO,
                                     AUXILIAR,
                                     VALOR_UNITARIO,
                                     ESTRATO,
                                     FUENTE_RECURSO,
                                     REFERENCIA,
                                     CUENTA_BANCO,
                                     VALOR_IMPOCONSUMO,
                                     BASE_FIJA,
                                     SYSDATE,
                                     SYSDATE,
                                     '''||UN_USUARIO||''',
                                     SYSDATE
                              FROM SF_DETALLE_CONTRATO
                              WHERE COMPANIA = '''||UN_COMPANIA||'''
                                AND TIPO     = '''||UN_TIPOCOBRO||'''
                                AND ANO      = '||UN_ANIO||'
                                AND NUMERO   = '||MI_RS.NUMERO;
                                
                                
                MI_CAMPOS := 'COMPANIA 
                              ,ANO
                              ,TIPOCOBRO
                              ,CODIGO_COBRO
                              ,CONCEPTO
                              ,TARIFA
                              ,CANTIDAD
                              ,VALOR_BASE
                              ,VALOR_IVA
                              ,VALOR_RETEFUENTE
                              ,VALOR_ICA
                              ,VALOR_DESCUENTO
                              ,VALOR_NETO
                              ,VALOR_COMPRA
                              ,VALOR_UTILIDAD
                              ,TERCERO
                              ,SUCURSAL
                              ,CENTRO_COSTO
                              ,AUXILIAR
                              ,VALOR_UNITARIO
                              ,ESTRATO
                              ,FUENTE_RECURSO
                              ,REFERENCIA
                              ,CUENTA_BANCO
                              ,VALOR_IMPOCONSUMO
                              ,BASE_FIJA
                              ,FECHA_FIN_COBRO
                              ,FECHA_INICIO_COBRO
                              ,CREATED_BY
                              ,DATE_CREATED';                              
                 
           BEGIN
                BEGIN              
                 PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => 'SF_DETALLE_COBRO',
                                                       UN_ACCION   => 'IS',
                                                       UN_CAMPOS   => MI_CAMPOS,
                                                       UN_VALORES  => MI_VALORES);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                END;
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
                MI_MSGERROR(0).CLAVE := 'CONTRATO';
                MI_MSGERROR(0).VALOR := MI_RS.NUMERO;
               
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,                                           
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INDECOBRCONTR,
                                           UN_REEMPLAZOS => MI_MSGERROR);  
            
           END;                
                    
            <<LLAMAR_FACTURACION>>     
                PCK_FACT_GENERAL_COM2.PR_FACTURAR_CONCEPTOS(UN_COMPANIA    => UN_COMPANIA,
                                                          UN_TIPOCOBRO     => UN_TIPOCOBRO,
                                                          UN_CODIGO_COBRO  => MI_CONSECUTIVO,
                                                          UN_NRO_FACTURA   => 0,  
                                                          UN_ANIO          => UN_ANIO,
                                                          UN_USUARIO       => UN_USUARIO);
                    
       END LOOP CREAR_OBJETO_COBRO;      
       
    --Consultar si maneja cartera e interes
    
    SELECT  MANEJA_CARTERA, MANEJA_INTERES_CARTERA
    INTO MI_MANEJACARTERA, MI_INTERESCARTERA
    FROM SF_TIPO_COBRO
    WHERE COMPANIA = UN_COMPANIA
    AND ANO        = UN_ANIO
    AND CODIGO     = UN_TIPOCOBRO;
    
    
    IF MI_MANEJACARTERA NOT IN (0) AND MI_INTERESCARTERA NOT IN (0) THEN
    --Calculo de cartera e intereses
    
    SELECT TASAINTERES
    INTO MI_TASAINTERES
    FROM SF_TASAS_INTERES
    WHERE COMPANIA = UN_COMPANIA
      AND UN_FECHAFACTURA BETWEEN FECHA_INICIAL AND FECHA_FINAL;
      
      --MENSAJE DE ERROR CUANDO NO HAY TASAS CONFIGURADAS
      
      --Recorrer terceros
        FOR MI_RS IN(SELECT SF_CONTRATOS.TERCERO,
                        SF_CONTRATOS.SUCURSAL           
                 FROM SF_CONTRATOS 
                  LEFT JOIN SF_LOCALES ON SF_CONTRATOS.COMPANIA = SF_LOCALES.COMPANIA  
                                      AND SF_CONTRATOS.LOCAL = SF_LOCALES.CODIGO     
                  LEFT JOIN SF_LUGAR ON SF_LOCALES.COMPANIA = SF_LUGAR.COMPANIA 
                                    AND SF_LOCALES.LUGAR = SF_LUGAR.CODIGO                                       
                 WHERE SF_CONTRATOS.COMPANIA = UN_COMPANIA
                   AND SF_CONTRATOS.TIPO = UN_TIPOCOBRO
                   AND SF_CONTRATOS.NUMERO BETWEEN UN_CONTRATOINICIAL AND UN_CONTRATOFINAL 
                   AND SF_LUGAR.CODIGO IN CASE WHEN UN_LUGAR = 'TODOS' THEN SF_LUGAR.CODIGO ELSE UN_LUGAR END 
                   AND SF_CONTRATOS.ESTADO = 2
                   AND SF_CONTRATOS.ANO = UN_ANIO
                   ORDER BY SF_CONTRATOS.NUMERO  ) 
       LOOP  
        
                BEGIN 
                
                    SELECT COUNT (*)    CANTIDADFACTURAS   
                     INTO MI_CANTIDADFACTURASDEUDA
                        FROM SF_CONTRATOS INNER JOIN SF_OBJETO_COBRO ON SF_CONTRATOS.NUMERO = SF_OBJETO_COBRO.CONTRATO 
                        AND SF_CONTRATOS.TIPO = SF_OBJETO_COBRO.TIPOCONTRATO 
                        AND SF_CONTRATOS.COMPANIA = SF_OBJETO_COBRO.COMPANIA 
                        INNER JOIN SF_FACTURA ON SF_OBJETO_COBRO.CODIGO_COBRO = SF_FACTURA.CODIGO_COBRO 
                            AND SF_OBJETO_COBRO.TIPOCOBRO = SF_FACTURA.TIPOCOBRO 
                            AND SF_OBJETO_COBRO.COMPANIA = SF_FACTURA.COMPANIA 
                        INNER JOIN SF_DETALLE_FACTURA ON SF_FACTURA.NUMERO_FACTURA = SF_DETALLE_FACTURA.FACTURA 
                        AND SF_FACTURA.TIPO_FACTURA = SF_DETALLE_FACTURA.TIPO_FACTURA 
                        AND SF_FACTURA.ANO = SF_DETALLE_FACTURA.ANO 
                        AND SF_FACTURA.COMPANIA = SF_DETALLE_FACTURA.COMPANIA
                        WHERE SF_FACTURA.COMPANIA= UN_COMPANIA
                            AND SF_FACTURA.TIPO_FACTURA=UN_TIPOCOBRO
                            AND SF_FACTURA.TERCERO=MI_RS.TERCERO
                            AND SF_OBJETO_COBRO.INDPAGO IN (0) 
                            AND SF_FACTURA.FECHA_EXPEDICION <= UN_FECHAFACTURA
                        AND SF_FACTURA.DIFERIDA IN (0)  ;
                       
                       
                    EXCEPTION WHEN NO_DATA_FOUND THEN  
                      MI_CANTIDADFACTURASDEUDA := 0; 
                    END;

                       
        
        MI_CONTADOR := 0;
        MI_FACTURASDEUDA := '';
        --Recorrer Facturas del Tercero
        FOR MI_RS2 IN (SELECT SF_FACTURA.COMPANIA,
                               SF_FACTURA.ANO,
                               SF_FACTURA.TIPO_FACTURA,
                               SF_FACTURA.NUMERO_FACTURA,
                               SF_FACTURA.TERCERO,      
                               SF_FACTURA.VALOR_TOTAL,
                               Round(SF_FACTURA.VALOR_TOTAL * ((TO_DATE(TO_CHAR(UN_FECHAFACTURA,'DD/MM/YYYY'))- TO_DATE(TO_CHAR(SF_FACTURA.FECHA_VENCIMIENTO,'DD/MM/YYYY'))) *MI_TASAINTERES)/365,0)  TOTALINT                                                     
                        FROM SF_CONTRATOS INNER JOIN SF_OBJETO_COBRO ON SF_CONTRATOS.NUMERO = SF_OBJETO_COBRO.CONTRATO 
                        AND SF_CONTRATOS.TIPO = SF_OBJETO_COBRO.TIPOCONTRATO 
                        AND SF_CONTRATOS.COMPANIA = SF_OBJETO_COBRO.COMPANIA 
                        INNER JOIN SF_FACTURA ON SF_OBJETO_COBRO.CODIGO_COBRO = SF_FACTURA.CODIGO_COBRO 
                            AND SF_OBJETO_COBRO.TIPOCOBRO = SF_FACTURA.TIPOCOBRO 
                            AND SF_OBJETO_COBRO.COMPANIA = SF_FACTURA.COMPANIA 
                        INNER JOIN SF_DETALLE_FACTURA ON SF_FACTURA.NUMERO_FACTURA = SF_DETALLE_FACTURA.FACTURA 
                        AND SF_FACTURA.TIPO_FACTURA = SF_DETALLE_FACTURA.TIPO_FACTURA 
                        AND SF_FACTURA.ANO = SF_DETALLE_FACTURA.ANO 
                        AND SF_FACTURA.COMPANIA = SF_DETALLE_FACTURA.COMPANIA
                        WHERE SF_FACTURA.COMPANIA= UN_COMPANIA
                            AND SF_FACTURA.TIPO_FACTURA=UN_TIPOCOBRO
                            AND SF_FACTURA.TERCERO=MI_RS.TERCERO
                            AND SF_OBJETO_COBRO.INDPAGO IN (0) 
                            AND SF_FACTURA.FECHA_EXPEDICION <= UN_FECHAFACTURA
                        AND SF_FACTURA.DIFERIDA IN (0)   
                        ORDER BY SF_FACTURA.NUMERO_FACTURA DESC,
                                    SF_FACTURA.FECHA_EXPEDICION,
                                    SF_CONTRATOS.LOCAL
        )LOOP           
                    
           MI_CONTADOR := MI_CONTADOR + 1;         
          
         IF MI_CONTADOR = 1 THEN
           MI_FACTURAACTUAL := MI_RS2.NUMERO_FACTURA ;     
           IF MI_CANTIDADFACTURASDEUDA  = 1 THEN
            MI_FACTURAACTUAL := 0;
           
           END IF;
         END IF;
         
         
         MI_CAMPOS    :=   'COMPANIA,
                            ANO,
                            TIPO_FACTURA,
                            NUMERO_FACTURA,
                            ANO_DEUDA,
                            TIPO_FACTURA_DEUDA,
                            NUMERO_FACTURA_DEUDA,
                            VALOR_CAPITAL,
                            INTERES,
                            CREATED_BY,                            
                            DATE_CREATED';                        
                              
           MI_VALORES := ''''||UN_COMPANIA||''',
                            '||MI_RS2.ANO||',
                          '''||MI_RS2.TIPO_FACTURA||''', 
                            '||MI_FACTURAACTUAL||',                     
                          '''||MI_RS2.ANO||''',
                          '''||MI_RS2.TIPO_FACTURA||''',
                            '||MI_RS2.NUMERO_FACTURA||',                        
                          '''||MI_RS2.VALOR_TOTAL||''',  
                          '''||MI_RS2.TOTALINT||''',                  
                          '''||UN_USUARIO||''',
                          SYSDATE    ';
           BEGIN
            BEGIN
               PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA => 'SF_CARTERA',
                                                UN_ACCION    => 'I', 
                                                UN_CAMPOS    => MI_CAMPOS, 
                                                UN_VALORES => MI_VALORES);
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
               RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
            END;
               
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN                         
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_TABLAERROR => MI_TABLA,
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INCOBRCONTR,
                                           UN_REEMPLAZOS => MI_MSGERROR);  
            
           END;         
         
         
        
        END LOOP;      
        
        --Actualizar detalle
        SELECT MAX(SF_CARTERA.NUMERO_FACTURA)
        INTO MI_NUMEROFACTURA
        FROM SF_CARTERA 
               INNER JOIN SF_FACTURA ON SF_CARTERA.COMPANIA = SF_FACTURA.COMPANIA           
            AND SF_CARTERA.TIPO_FACTURA = SF_FACTURA.TIPO_FACTURA
            AND SF_CARTERA.NUMERO_FACTURA = SF_FACTURA.NUMERO_FACTURA
            AND SF_CARTERA.ANO  = SF_FACTURA.ANO
               WHERE SF_FACTURA.COMPANIA= UN_COMPANIA
                            AND SF_FACTURA.TIPO_FACTURA=UN_TIPOCOBRO
                            AND SF_FACTURA.TERCERO=MI_RS.TERCERO;
                            
       
         IF MI_NUMEROFACTURA NOT IN (0) THEN      
                       
              
                  FOR MI_RS3 IN(  SELECT COMPANIA,TIPO_FACTURA,TERCERO,CONCEPTO, SUM(SUMA) TOTALCONCEPTO,SUM(TOTALINT) TOTALINTERES FROM (SELECT SF_FACTURA.COMPANIA,       
                           SF_FACTURA.TIPO_FACTURA,
                           SF_FACTURA.TERCERO,
                           SF_DETALLE_FACTURA.CONCEPTO,  
                           SF_DETALLE_FACTURA.VALOR_NETO SUMA,             
                          Round(SF_DETALLE_FACTURA.VALOR_NETO * ((TO_DATE(TO_CHAR(UN_FECHAFACTURA,'DD/MM/YYYY'))- TO_DATE(TO_CHAR(SF_FACTURA.FECHA_VENCIMIENTO,'DD/MM/YYYY')))*MI_TASAINTERES)/365,0)  TOTALINT                                                                              
                    FROM SF_CONTRATOS INNER JOIN SF_OBJETO_COBRO ON SF_CONTRATOS.NUMERO = SF_OBJETO_COBRO.CONTRATO 
                    AND SF_CONTRATOS.TIPO = SF_OBJETO_COBRO.TIPOCONTRATO 
                    AND SF_CONTRATOS.COMPANIA = SF_OBJETO_COBRO.COMPANIA 
                    INNER JOIN SF_FACTURA ON SF_OBJETO_COBRO.CODIGO_COBRO = SF_FACTURA.CODIGO_COBRO 
                        AND SF_OBJETO_COBRO.TIPOCOBRO = SF_FACTURA.TIPOCOBRO 
                        AND SF_OBJETO_COBRO.COMPANIA = SF_FACTURA.COMPANIA 
                    INNER JOIN SF_DETALLE_FACTURA ON SF_FACTURA.NUMERO_FACTURA = SF_DETALLE_FACTURA.FACTURA 
                    AND SF_FACTURA.TIPO_FACTURA = SF_DETALLE_FACTURA.TIPO_FACTURA 
                    AND SF_FACTURA.ANO = SF_DETALLE_FACTURA.ANO 
                    AND SF_FACTURA.COMPANIA = SF_DETALLE_FACTURA.COMPANIA
                    WHERE SF_FACTURA.COMPANIA= UN_COMPANIA
                        AND SF_FACTURA.TIPO_FACTURA=UN_TIPOCOBRO
                        AND SF_FACTURA.TERCERO=MI_RS.TERCERO
                        AND SF_OBJETO_COBRO.INDPAGO IN (0) 
                        AND SF_FACTURA.FECHA_EXPEDICION <= UN_FECHAFACTURA
                        AND TO_CHAR(SF_FACTURA.NUMERO_FACTURA) IN ( SELECT NUMERO_FACTURA_DEUDA                
                                                                      FROM SF_CARTERA
                                                                      WHERE COMPANIA  = UN_COMPANIA
                                                                      AND ANO = UN_ANIO
                                                                      AND TIPO_FACTURA = UN_TIPOCOBRO
                                                                      AND NUMERO_FACTURA = MI_NUMEROFACTURA)
                                                                            AND SF_FACTURA.DIFERIDA IN (0) )
                        GROUP BY COMPANIA,TIPO_FACTURA,TERCERO,CONCEPTO  ) LOOP
                        
                    BEGIN
                     BEGIN
                             
                            MI_CAMPOS    := 'DEUDA_ANTERIOR   = '||MI_RS3.TOTALCONCEPTO||' -  VALOR_NETO  +'||MI_RS3.TOTALINTERES||',
                                             VALOR_NETO      = VALOR_NETO + ('||MI_RS3.TOTALCONCEPTO||'- VALOR_NETO +'||MI_RS3.TOTALINTERES||'),
                                             MODIFIED_BY   = '''||UN_USUARIO||''',
                                             DATE_MODIFIED = SYSDATE';

                            MI_CONDICION := ' COMPANIA     = '''||UN_COMPANIA||'''
                                          AND ANO          = '||UN_ANIO||'  
                                          AND TIPOCOBRO    = '''||UN_TIPOCOBRO||'''
                                          AND CODIGO_COBRO = (SELECT CODIGO_COBRO FROM SF_OBJETO_COBRO 
                                                               WHERE COMPANIA   =   '''||UN_COMPANIA||''' 
                                                               AND ANO          = '||UN_ANIO||'  
                                                               AND TIPOCOBRO    = '''||UN_TIPOCOBRO||''' 
                                                               AND NRO_FACTURA  = '||MI_NUMEROFACTURA||')
                                          AND CONCEPTO     = '''||MI_RS3.CONCEPTO||''' ';

                                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'SF_DETALLE_COBRO',
                                                       UN_ACCION    => 'M',
                                                       UN_CAMPOS    => MI_CAMPOS,
                                                       UN_CONDICION => MI_CONDICION);        
                                                       
                                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;													
                          END;
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF  THEN 
                            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                       UN_TABLAERROR => MI_TABLA,
                                                       UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTA_DET_FAC);                                                        
                       END;   
                       
                      BEGIN
                         BEGIN    
                            MI_CAMPOS    := 'DEUDA_ANTERIOR   = '||MI_RS3.TOTALCONCEPTO||'- VALOR_NETO +'||MI_RS3.TOTALINTERES||',
                                             VALOR_NETO      = VALOR_NETO + ('||MI_RS3.TOTALCONCEPTO||'- VALOR_NETO +'||MI_RS3.TOTALINTERES||'),
                                             MODIFIED_BY   = '''||UN_USUARIO||''',
                                             DATE_MODIFIED = SYSDATE';

                            MI_CONDICION := ' COMPANIA     = '''||UN_COMPANIA||'''
                                          AND ANO          = '||UN_ANIO||'  
                                          AND TIPO_FACTURA    = '''||UN_TIPOCOBRO||'''
                                          AND FACTURA = '||MI_NUMEROFACTURA||'
                                          AND CONCEPTO     = '''||MI_RS3.CONCEPTO||''' ';

                                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'SF_DETALLE_FACTURA',
                                                       UN_ACCION    => 'M',
                                                       UN_CAMPOS    => MI_CAMPOS,
                                                       UN_CONDICION => MI_CONDICION);        
                                                       
                                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;													
                          END;
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF  THEN 
                            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                       UN_TABLAERROR => MI_TABLA,
                                                       UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTA_DET_FAC);                                                        
                       END;                          
                        
                  END LOOP;
              
            
         END IF;
          
       
       
       END LOOP;
        
    
    END IF;
       
END PR_FACTURAR_SERIE;

--20
PROCEDURE PR_FINANCIARPREDIAL(
/*
  NAME              : PR_FINANCIARPREDIAL
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
  DATE MIGRADOR     : 21/05/2020
  TIME              : 04:30 PM
  SOURCE MODULE     : SYSMASF
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : Procedimiento que se encarga de realizar la facuracion de las deudas por vigencia de predial

  @NAME:  financiarPredial
  @METHOD:  GET
*/
    UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_ANIO                    IN SF_CONTRATOS.ANO%TYPE, 
    UN_TIPOCOBRO               IN SF_CONTRATOS.TIPO%TYPE,
    UN_CODIGOPREDIO            IN TEMPORALDEUDAPREDIO.CODIGO_PREDIO%TYPE,
    UN_ANIOSFINANCIAR          IN VARCHAR2,
    UN_TERCERO                 IN SF_OBJETO_COBRO.TERCERO%TYPE,
    UN_SUCURSAL                IN SF_OBJETO_COBRO.SUCURSAL%TYPE,
    UN_USUARIO                 IN PCK_SUBTIPOS.TI_USUARIO 
)
AS
    MI_TABLA               PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS              PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES             PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION           PCK_SUBTIPOS.TI_CONDICION;
    MI_CONSECUTIVO         SF_OBJETO_COBRO.CODIGO_COBRO%TYPE;
    MI_OBSERVACIONES       SF_OBJETO_COBRO.OBSERVACIONES%TYPE;
    MI_MSGERROR            PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_FECHAVENCIMIENTO    SF_OBJETO_COBRO.FECHA_VENCIMIENTO%TYPE;
    MI_STRSQL              PCK_SUBTIPOS.TI_STRSQL ;
    MI_CONTEO              PCK_SUBTIPOS.TI_ENTERO ;
    MI_RS                  SYS_REFCURSOR;
    MI_RSCONCEPRO          TEMPORALDEUDAPREDIO.CONCEPTO%TYPE;
    MI_RSTOTALCONCEPTO     NUMBER(20,2) ;
    MI_INDPRELIQUIDACION   SF_TIPO_COBRO.INDPRELIQUIDACION%TYPE;  
           
BEGIN     

    <<ACTUALIZAR_CONCEPTOS>>
    
    MI_STRSQL := 'SELECT CONCEPTO, SUM(VALOR_CONCEPTO)TOTAL_CONCEPTO 
                  FROM TEMPORALDEUDAPREDIO 
                  WHERE CODIGO_PREDIO = '''||UN_CODIGOPREDIO||'''
                  AND ANO IN ('||UN_ANIOSFINANCIAR||')
                    GROUP BY CONCEPTO';
                    
    EXECUTE IMMEDIATE 'SELECT COUNT(1) FROM ('||MI_STRSQL||')' INTO MI_CONTEO;      
    
   IF MI_CONTEO >=1 THEN   
   
    OPEN MI_RS FOR MI_STRSQL;
    LOOP  
         FETCH MI_RS INTO MI_RSCONCEPRO,MI_RSTOTALCONCEPTO; 
                  EXIT WHEN MI_RS%NOTFOUND;
      BEGIN 
        BEGIN 
            MI_CAMPOS := 'VALOR_BASE = '||MI_RSTOTALCONCEPTO||',                          
                          MODIFIED_BY   = '''||UN_USUARIO||''', 
                          DATE_MODIFIED = SYSDATE ';

            MI_CONDICION := ' COMPANIA  = ''' ||UN_COMPANIA||'''
                          AND ANO       = '||UN_ANIO ||'
                          AND TIPOCOBRO = '''||UN_TIPOCOBRO ||'''
                          AND CODIGO    = '''||MI_RSCONCEPRO||'''';

            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'SF_CONCEPTOS', 
                                        UN_ACCION    => 'M', 
                                        UN_CAMPOS    => MI_CAMPOS, 
                                        UN_CONDICION => MI_CONDICION );                                        
                                        
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
	      END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_TABLAERROR => MI_TABLA,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTUAL_CONCEPTO);
      END;                  

    END LOOP ACTUALIZAR_CONCEPTOS;     
    
    --Crear objeto cobro    

     --Calcular consecutivo que tendra el objeto cobro
     MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    =>'SF_OBJETO_COBRO',
                                                      UN_CRITERIO => 'COMPANIA = '''||UN_COMPANIA||''' 
                                                                 AND TIPOCOBRO = '''||UN_TIPOCOBRO||'''
                                                                 AND SUBSTR(CODIGO_COBRO,1,4) = '|| UN_ANIO,
                                                      UN_CAMPO    => 'CODIGO_COBRO');

     --Observaciones que tendra el objeto cobro
     MI_OBSERVACIONES :=   'ACUERDO DE PAGO IMPUESTO PREDIAL';  

           MI_CAMPOS    :=   'COMPANIA
                              ,ANO
                              ,TIPOCOBRO
                              ,CODIGO_COBRO
                              ,TERCERO
                              ,SUCURSAL                        
                              ,FECHA_SOLICITUD
                              ,FECHA_VENCIMIENTO
                              ,OBSERVACIONES
                              ,CREATED_BY
                              ,DATE_CREATED';                        
                              
           MI_VALORES := ''''||UN_COMPANIA||''',
                            '||UN_ANIO||',
                          '''||UN_TIPOCOBRO||''',
                            '||MI_CONSECUTIVO||',
                          '''||UN_TERCERO||''',
                          '''||UN_SUCURSAL||''',                       
                          SYSDATE,
                          '''||MI_FECHAVENCIMIENTO||''',
                          '''||MI_OBSERVACIONES||''',
                          '''||UN_USUARIO||''',
                          SYSDATE';
           BEGIN
            BEGIN
               PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA => 'SF_OBJETO_COBRO',
                                                UN_ACCION    => 'I', 
                                                UN_CAMPOS    => MI_CAMPOS, 
                                                UN_VALORES => MI_VALORES);
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
               RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
            END;
               
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN                          
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_TABLAERROR => MI_TABLA,
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSOBJETOCOBRO,
                                           UN_REEMPLAZOS => MI_MSGERROR);  
            
           END;
           
           --Calcular campo FECHA_VENCIMIENTO                                      
           
           SELECT INDPRELIQUIDACION
             INTO MI_INDPRELIQUIDACION
           FROM SF_TIPO_COBRO 
           WHERE COMPANIA = UN_COMPANIA
             AND ANO      = UN_ANIO
             AND CODIGO   = UN_TIPOCOBRO;
             
           MI_FECHAVENCIMIENTO :=PCK_FACT_GENERAL_COM2.FC_CALCULAR_FECHA_VENCIMIENTO  (UN_COMPANIA     => UN_COMPANIA,
                                                                                       UN_TIPOCOBRO    => UN_TIPOCOBRO,
                                                                                       UN_APLICA_REL   => MI_INDPRELIQUIDACION,
                                                                                       UN_CODIGO_COBRO => MI_CONSECUTIVO);           
          --Actualizar FECHA_VENCIMIENTO
           
            MI_CAMPOS    := 'FECHA_VENCIMIENTO = '''||MI_FECHAVENCIMIENTO||''' ';
            
            MI_CONDICION := '   COMPANIA     = '''||UN_COMPANIA||'''
                            AND CODIGO_COBRO = '||MI_CONSECUTIVO||'
                            AND ANO          = '||UN_ANIO||'
                            AND TIPOCOBRO    = '''||UN_TIPOCOBRO||'''';
            
           BEGIN
                BEGIN               
                    PCK_DATOS.GL_RTA :=PCK_DATOS.FC_ACME(UN_ACCION    =>'M',
                                                         UN_TABLA     =>'SF_OBJETO_COBRO',
                                                         UN_CAMPOS    =>MI_CAMPOS,
                                                         UN_CONDICION =>MI_CONDICION);   
                   
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                            RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                  END;
                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
                    MI_MSGERROR(0).CLAVE := 'CONTRATO';
                    MI_MSGERROR(0).VALOR := MI_CONSECUTIVO;
                   
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,                                           
                                               UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTFECHAVCOBR,
                                               UN_REEMPLAZOS => MI_MSGERROR);  
            
           END;    
           
           --Insertar Detalle Objeto Cobro con los detalles a financiar
           
       MI_VALORES := ' SELECT '''||UN_COMPANIA||''',
                                '||UN_ANIO||',
                              '''||UN_TIPOCOBRO||''',
                                '||MI_CONSECUTIVO||',
                              CODIGO,
                              SYSDATE,
                              SYSDATE,
                              1,
                              VALOR_BASE,
                              VALOR_BASE,
                              '''||UN_USUARIO||''',
                              SYSDATE
                       FROM SF_CONCEPTOS
                       WHERE COMPANIA = '''||UN_COMPANIA||'''
                        AND TIPOCOBRO = '''||UN_TIPOCOBRO||'''
                        AND ANO       = '||UN_ANIO||'
                        AND CODIGO IN (SELECT  UPPER(CONCEPTO)
                                       FROM TEMPORALDEUDAPREDIO 
                                       WHERE CODIGO_PREDIO = '''||UN_CODIGOPREDIO||'''
                                         AND ANO IN ('||UN_ANIOSFINANCIAR||')
                                       GROUP BY CONCEPTO)';              
              
              MI_CAMPOS := 'COMPANIA 
                            ,ANO
                            ,TIPOCOBRO
                            ,CODIGO_COBRO
                            ,CONCEPTO
                            ,FECHA_INICIO_COBRO
                            ,FECHA_FIN_COBRO 
                            ,CANTIDAD
                            ,VALOR_BASE
                            ,VALOR_NETO
                            ,CREATED_BY
                            ,DATE_CREATED';             
           
           BEGIN
                BEGIN              
                 PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => 'SF_DETALLE_COBRO',
                                                       UN_ACCION   => 'IS',
                                                       UN_CAMPOS   => MI_CAMPOS,
                                                       UN_VALORES  => MI_VALORES);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                END;
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN            
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,                                           
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INDECOBRCONTR,
                                           UN_REEMPLAZOS => MI_MSGERROR);  
            
           END;        
           
           
      <<LLAMAR_FACTURACION>>     
            PCK_FACT_GENERAL_COM2.PR_FACTURAR_CONCEPTOS(UN_COMPANIA    => UN_COMPANIA,
                                                      UN_TIPOCOBRO     => UN_TIPOCOBRO,
                                                      UN_CODIGO_COBRO  => MI_CONSECUTIVO,
                                                      UN_NRO_FACTURA   => 0,  
                                                      UN_ANIO          => UN_ANIO,
                                                      UN_USUARIO       => UN_USUARIO);         
  END IF;             
END PR_FINANCIARPREDIAL;

FUNCTION FC_CARGAR_RECUADOCAUSACION
/*
    NAME              : PR_CARGAR_RECUADOCAUSACION
    AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
    DATE MIGRADOR     : 10/07/2020                                                                                              
    TIME              :
    SOURCE MODULE     :
    MODIFIER          : GUSTAVO ANDRES FIGUEREDO AVILA
    DATE MODIFIED     : 03/11/2021
    TIME              : 12:41 PM
    MODIFICATIONS     : Se ajusta la funcion , para traer los valores de AUXILIAR, CENTRO_COSTO, REFERENCIA Y FUENTE DE RECURSO 
            de la tabla SF_CONCEPTOS.
            Se ajusta la tabla TEMP_REPORTERECAUDO para anadir las columnas AUXILIAR, CENTRO_COSTO, REFERENCIA Y FUENTE DE RECURSO y almacenar los valores
            consultados de la tabla SF_CONCEPTOS.
            Se ajustan los INSERT a la tabla TEMP_PLANA_AJUSTES, para enviar los valores de AUXILIAR, CENTRO_COSTO, REFERENCIA Y FUENTE DE RECURSO almacenados
            en la tabla SF_CONCEPTOS.
  MODIFIER          : GUSTAVO ANDRES FIGUEREDO AVILA
    DATE MODIFIED     : 18/01/2022
    TIME              : 16:17
    MODIFICATIONS     : Ticket 7707509  Se anade validacion para que los valores debito y credito en el recaudo,
              se asignen se forma invertida si el concepto no es de descuento.
  MODIFIER          : GUSTAVO ANDRES FIGUEREDO AVILA
    DATE MODIFIED     : 16/02/2022
    TIME              : 10:28
    MODIFICATIONS     : Ticket 7709556  Se asigna el valor de la cuenta de caudo al debito cuando aplica descuento es 0.

    MODIFIER          : LUIS JACOBO DIAZ MUÑOZ
    DATE MODIFIED     : 04/05/2022
    TIME              : 16:28
    MODIFICATIONS     : Ticket 7712290  Se corrige la  funionc para que al momento de ver que la data de llegada es de un 
                        comprobante existente, este no sea borrado y creado de nuevo con solo los datos nuevos, si no que 
                        se le agregara la data nueva a los detalles del comprobante teniendo en cuenta el proceso por la 
                        inteface grafica
    DESCRIPTION       : PROCEDIMIENTO QUE CARGA LOS DATOS DE RECAUDO Y CAUSACION

    MODIFIER          : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MODIFIED     : 20/10/2022
    TIME              : 16:00
    MODIFICATIONS     : Ticket 7720232  Se corrige la  función para crear adecuadamente los detalles comprobantes pptal
                        cuando hay descuentos y cuando el comprobante pptal ya existe actualice adecuadamente la información
    DESCRIPTION       : PROCEDIMIENTO QUE CARGA LOS DATOS DE RECAUDO Y CAUSACION

    @NAME:    cargarRecaudoCausacion
    @METHOD:  GET
    */
( 
  UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CADENAPLANO  IN CLOB,
  UN_TIPOCOBRO    IN PCK_SUBTIPOS.TI_TIPO_COBRO,  
  UN_ANIO         IN PCK_SUBTIPOS.TI_ANIO,
  UN_TERCERO      IN SF_DETALLE_FACTURA.TERCERO%TYPE,
  UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO,
  UN_LOTE         IN NUMBER DEFAULT 0 --JMILLAN 17/09/2024
)RETURN CLOB
AS 
    MI_DATOS_FILA         PCK_SYSMAN_UTL.T_SPLIT;
    MI_DATOS_COLUMNAS     PCK_SYSMAN_UTL.T_SPLIT;
    MI_DATOS_COLUMNAS_NEXT PCK_SYSMAN_UTL.T_SPLIT;
    MI_DATOS_COLUMNAS_NEXT_NEXT PCK_SYSMAN_UTL.T_SPLIT;
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;    
    MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
    MI_NUMERO             DETALLE_COMPROBANTE_CNT.COMPROBANTE%TYPE;
    MI_NUMERORECAUDO      DETALLE_COMPROBANTE_CNT.COMPROBANTE%TYPE;
    MI_DEBITO             VARCHAR2(32 CHAR); 
    MI_CREDITO            VARCHAR2(32 CHAR);
    MI_CLASECONTABLE      TIPO_COMPROBANTE.CLASE_CONTABLE%TYPE;
    MI_CUENTADEBITOBASE   SF_CONCEPTOS.CUENTADEBITOBASE%TYPE;
    MI_CUENTADEBITOBASE_NEXT SF_CONCEPTOS.CUENTADEBITOBASE%TYPE;
    MI_CUENTADEBITOBASE_NEXT_NEXT SF_CONCEPTOS.CUENTADEBITOBASE%TYPE;
    MI_CUENTACREDITOBASE  SF_CONCEPTOS.CUENTACREDITOBASE%TYPE;
    MI_CUENTACREDITOBASE_NEXT SF_CONCEPTOS.CUENTACREDITOBASE%TYPE;
    MI_CUENTACREDITOBASE_NEXT_NEXT SF_CONCEPTOS.CUENTACREDITOBASE%TYPE;
    MI_NATURALEZA         PCK_SUBTIPOS.TI_NATURALEZACONTA;
    MI_SUCURSAL           SF_DETALLE_FACTURA.SUCURSAL%TYPE;
    MI_FECHACOMPROBANTE   DATE;
    MI_FECHAVALIDACION    DATE;
    MI_CONSECUTIVO        COMPROBANTE_CNT.NUMERO%TYPE;
    MI_CONSECUTIVORECAUDO COMPROBANTE_CNT.NUMERO%TYPE;
    MI_CTAPPTAL           DETALLE_COMPROBANTE_CNT.CUENTAPPTAL%TYPE; 
    MI_CONSC              BOOLEAN; 
    MI_RPTA               PCK_SUBTIPOS.TI_LOGICO;
    MI_RTAINTERFAZ        PCK_SUBTIPOS.TI_RTA_ACME;
    MI_RETORNO            CLOB;
    MI_RETORNORECAUDO     CLOB;
    MI_RETORNOCAUSACION   CLOB;
    MI_CPTERECAUDO        SF_TIPO_COBRO.CPTE_RECAUDO%TYPE;
    MI_STRSQL             PCK_SUBTIPOS.TI_STRSQL;  
    MI_RSCREDITO          SYS_REFCURSOR;
    MI_CONSECUTIVOAFECT   DETALLE_COMPROBANTE_CNT.CONSECUTIVO%TYPE;
    MI_CUENTACAUSADA      DETALLE_COMPROBANTE_CNT.CUENTA%TYPE;
    MI_VALOR              PCK_SUBTIPOS.TI_DOBLE:=0;
    MI_CUENTA             DETALLE_COMPROBANTE_CNT.CUENTA%TYPE;
    MI_CUENTARECAUDO      SF_CONCEPTOS.CUENTA_RECAUDO%TYPE;
    MI_CONTADOR           NUMBER(20,0)  ;
    MI_INCONSISTENCIA     BOOLEAN;
    MI_FECHA              VARCHAR2(20) ;
    MI_DESCUENTO          NUMBER(1,0);
    MI_AUXILIAR       SF_CONCEPTOS.AUXILIAR%TYPE;
    MI_CENTRO       SF_CONCEPTOS.CENTRO_COSTO%TYPE;
    MI_REFERENCIA     SF_CONCEPTOS.REFERENCIA%TYPE;
    MI_FUENTE       SF_CONCEPTOS.FUENTE_RECURSO%TYPE;
    --VARIABLES DESARROLLO TICKET 7701326
    V_DESCRIPCION_PPTAL      VARCHAR2(2000);
    V_NUMCPTEEXISTE_CNT      VARCHAR2(250);
    V_NUMCPTEEXISTE_CNT_RECAUDO VARCHAR2(250);
    V_CONTCONSECDETPPTAL     NUMBER;
    V_NUMCPTEEXISTE_CNT_AUX  NUMBER;
    V_COMPROBANTESACTUALZIADOS VARCHAR2(4000);
    V_COMPROBANTESACTUALZIADOS_RIP VARCHAR2(4000);
    V_COMPROBANTESNOACTUALZIADOS varchar2(4000);
    V_CANTCOMPRSEXISTE_CNT   NUMBER;
    V_CANTCOMPRSEXISTE_RIP_CNT NUMBER;
    V_EXISTEDETALLE          NUMBER;
    V_VALORTEMP              NUMBER;
    V_VALORTEMPACUM          NUMBER;
    V_PUNTODECIMALINDEX      NUMBER;
    V_CUENTAPPTAL            VARCHAR2(100);
    V_NUMCPTEEXISTE_PPTAL    NUMBER;
    MI_YAINSERTADO           BOOLEAN;
    MI_RTA                   VARCHAR2(2000);
    V_VLRBASE_CNT            NUMBER;
    V_SIGNOINDEX             NUMBER;

    MI_CTAPPTALDEBITO        DETALLE_COMPROBANTE_CNT.CUENTAPPTAL%TYPE; 
    MI_CTAPPTALCREDITO       DETALLE_COMPROBANTE_CNT.CUENTAPPTAL%TYPE; 

    V_MIRETORNOEXISTE_CNT_AUX NUMBER;
    MI_CONTINUE NUMBER;
    MI_EXISTE_AFECT_WS        NUMBER; 
    MI_EXISTE_TEMP_REPO       NUMBER;
    MI_CLASE_BANCO          VARCHAR2(1);  --JM 7749239 22/07/2024
    MI_LOTE_EXISTE          NUMBER;
    MI_LOTE_FECHAS          PCK_SYSMAN_UTL.T_SPLIT;
    MI_LOTE_FECHAS_TEMP     CLOB;
    MI_CINTADOR_VUELTAS NUMBER;
    MI_CONTADOR_TIPOCPT VARCHAR2(3200);
BEGIN   

    IF UN_LOTE = 0 THEN
        MI_RETORNO := 'Error: El numero de lote, no es un numero valido'|| CHR(13) || CHR(10);
        RETURN MI_RETORNO;
   END IF;

   MI_INCONSISTENCIA := FALSE;
   MI_FECHAVALIDACION := TO_DATE('01/01/1900','DD/MM/YYYY'); -- MPEREZ
   MI_NUMERO := 0; 
   MI_CONSECUTIVO := 0; 
   MI_RETORNO := '';
   MI_RETORNORECAUDO := '';
   MI_RETORNOCAUSACION := '';
   MI_CONTADOR := 0;
   MI_YAINSERTADO := FALSE;
   MI_CINTADOR_VUELTAS := 0;


  --Se consulta el sucursal del tercero
  SELECT SUCURSAL 
  INTO  MI_SUCURSAL      
  FROM TERCERO 
  WHERE COMPANIA = UN_COMPANIA
    AND NIT = UN_TERCERO; 

  --Se consulta la clase contable del tipo comprobante
  SELECT CLASE_CONTABLE 
  INTO   MI_CLASECONTABLE
  FROM   TIPO_COMPROBANTE 
  WHERE  COMPANIA = UN_COMPANIA
    AND  CODIGO = UN_TIPOCOBRO; 

    SELECT CPTE_RECAUDO
    INTO MI_CPTERECAUDO
    FROM SF_TIPO_COBRO
    WHERE SF_TIPO_COBRO.COMPANIA = UN_COMPANIA
      AND SF_TIPO_COBRO.ANO      = UN_ANIO
      AND SF_TIPO_COBRO.CODIGO   = UN_TIPOCOBRO; 


  MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENAPLANO,
                                               UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);
                                               
                                               
 FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST LOOP
    IF(MI_DATOS_FILA(RS) IS NOT NULL)THEN
        MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                         UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);
        MI_LOTE_FECHAS_TEMP := MI_LOTE_FECHAS_TEMP || MI_DATOS_COLUMNAS(1)||',';
    END IF;
END LOOP;

MI_LOTE_FECHAS_TEMP := SUBSTR(MI_LOTE_FECHAS_TEMP, 1, LENGTH(MI_LOTE_FECHAS_TEMP) - 1);
MI_LOTE_FECHAS_TEMP := PCK_SYSMAN_UTL.FC_QUITAR_REPETIDOS(UN_LISTA => MI_LOTE_FECHAS_TEMP);
MI_LOTE_FECHAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_LOTE_FECHAS_TEMP,
                                                         UN_DELIMITADOR  =>  ',');
  
  
MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENAPLANO,
                                               UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG); 
                                               
                                               
<<RECORRER_POR_FECHAS>>
FOR RSI IN MI_LOTE_FECHAS.FIRST..MI_LOTE_FECHAS.LAST LOOP
IF(MI_LOTE_FECHAS(RSI) IS NOT NULL)THEN
        IF UN_LOTE <> 0 THEN
          SELECT COUNT(1) 
          INTO  MI_LOTE_EXISTE      
          FROM DETALLE_COMPROBANTE_CNT 
          WHERE COMPANIA = UN_COMPANIA
          AND LOTE = UN_LOTE 
          AND FECHA = TO_DATE(MI_LOTE_FECHAS(RSI),'DD/MM/YYYY'); 
          
            IF MI_LOTE_EXISTE <> 0 THEN 
                MI_RETORNO := MI_RETORNO|| 'El lote con numero  '||UN_LOTE||' ha sido procesado anteriormente'|| CHR(13) || CHR(10);
                RETURN MI_RETORNO;
                ELSE 

SELECT TO_CHAR(RPAD(UN_LOTE,4,0))||TO_CHAR(TO_DATE(MI_LOTE_FECHAS(RSI),'DD/MM/YYYY'),'YY')||TO_CHAR(TO_DATE(MI_LOTE_FECHAS(RSI),'DD/MM/YYYY'),'MM')||TO_CHAR(TO_DATE(MI_LOTE_FECHAS(RSI),'DD/MM/YYYY'),'DD') INTO MI_NUMERO FROM DUAL;              
                --AQUI LLENAMOS LA TABLA 
<<INSERTAR_TEMPORAL>>
  FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST LOOP
    IF(MI_DATOS_FILA(RS) IS NOT NULL)THEN
        MI_INCONSISTENCIA := FALSE;
        MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                         UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL); 
        IF( MI_LOTE_FECHAS(RSI) = MI_DATOS_COLUMNAS(1) ) THEN              
      
            MI_CONTADOR := MI_CONTADOR + 1;
    
            BEGIN
                SELECT CUENTADEBITOBASE,
                       CUENTACREDITOBASE,
                       CUENTA_RECAUDO,
                       DESCUENTO,
                       AUXILIAR,
                       CENTRO_COSTO,
                       REFERENCIA,
                       FUENTE_RECURSO
                INTO   MI_CUENTADEBITOBASE, --MI_CUENTADEBITOBASE '13050701'  VARCHAR2(32)
                       MI_CUENTACREDITOBASE, --MI_CUENTACREDITOBASE '410507'  VARCHAR2(32)
                       MI_CUENTARECAUDO, -- MI_CUENTARECAUDO  '110501'  VARCHAR2(50)
                       MI_DESCUENTO,
                       MI_AUXILIAR,
                       MI_CENTRO,
                       MI_REFERENCIA,
                       MI_FUENTE
                FROM SF_CONCEPTOS 
                WHERE COMPANIA = UN_COMPANIA
                AND ANO = UN_ANIO
                AND TIPOCOBRO = UN_TIPOCOBRO
                AND CODIGO = MI_DATOS_COLUMNAS(2);   
             EXCEPTION WHEN NO_DATA_FOUND THEN 
                MI_RETORNO := MI_RETORNO|| 'El concepto '||MI_DATOS_COLUMNAS(2)||' no ha sido configurado en el formulario de conceptos. '|| CHR(13) || CHR(10);
                MI_INCONSISTENCIA := TRUE; 
                   /* BEGIN
                       RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
                       EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_SYSMANSF THEN    
                              MI_MSGERROR(1).CLAVE := 'CONCEPTO';
                              MI_MSGERROR(1).VALOR :=  MI_DATOS_COLUMNAS(2);
                       PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    =>SQLCODE,
                                                  UN_ERROR_COD  =>PCK_ERRORES.ERR_SYSMANSF_CPTONOCUENTAS,
                                                  UN_REEMPLAZOS => MI_MSGERROR);
                     END;*/
              END;  
            IF MI_CUENTADEBITOBASE IS NULL OR MI_CUENTACREDITOBASE IS NULL THEN
              MI_RETORNO := MI_RETORNO|| 'No se han configurado las cuentas Debito o Credito del concepto '||MI_DATOS_COLUMNAS(2)|| CHR(13) || CHR(10);
                    MI_INCONSISTENCIA := TRUE;         
            END IF;
    
            /*Se obtiene la cuenta presupuestal para la cuenta contable débito - MPEREZ*/
            BEGIN
                SELECT RUBRO
                  INTO MI_CTAPPTALDEBITO --MI_CTAPPTALDEBITO  '1.1.01.01.200.01'  VARCHAR2(100)
                  FROM PLAN_PPTAL_CUENTACNT          
                 WHERE COMPANIA = UN_COMPANIA
                   AND ANO      = UN_ANIO
                   AND CUENTA_CONTABLE    = MI_CUENTADEBITOBASE;
            EXCEPTION WHEN NO_DATA_FOUND THEN 
                MI_CTAPPTALDEBITO := '';
            END;
    
            /*Se obtiene la cuenta presupuestal para la cuenta contable crédito - MPEREZ*/              
            BEGIN
                SELECT RUBRO
                  INTO MI_CTAPPTALCREDITO--null
                  FROM PLAN_PPTAL_CUENTACNT          
                 WHERE COMPANIA = UN_COMPANIA
                   AND ANO      = UN_ANIO
                   AND CUENTA_CONTABLE    = MI_CUENTACREDITOBASE;
            EXCEPTION WHEN NO_DATA_FOUND THEN 
                MI_CTAPPTALCREDITO := '';
            END;
    
            BEGIN
            SELECT  IDCONTABLE
            INTO MI_CUENTARECAUDO --MI_CUENTARECAUDO  '1110060301'  VARCHAR2(50)
            FROM CUENTABANCOS WHERE COMPANIA = UN_COMPANIA
            AND ANO = UN_ANIO
            AND CUENTANUMERO = MI_DATOS_COLUMNAS(4);
    
            EXCEPTION WHEN NO_DATA_FOUND THEN 
    
            BEGIN
                SELECT  CODIGO, CLASECUENTA
                INTO MI_CUENTARECAUDO, MI_CLASE_BANCO  --JM
                FROM PLAN_CONTABLE WHERE COMPANIA = UN_COMPANIA
                AND ANO = UN_ANIO
                AND CODIGO = MI_DATOS_COLUMNAS(4);
    
                EXCEPTION WHEN NO_DATA_FOUND THEN 
    
                  MI_RETORNO := MI_RETORNO|| 'La cuenta '||MI_DATOS_COLUMNAS(4)||' no esta configurada ni como cuenta banco o cuenta contable en el sistema. '|| CHR(13) || CHR(10);
                  MI_INCONSISTENCIA := TRUE;
            END;
            WHEN TOO_MANY_ROWS THEN --Ticket 7710273 gfigueredo
              MI_RETORNO := MI_RETORNO|| 'La cuenta '||MI_DATOS_COLUMNAS(4)||' está configurada en diferentes cuentas contables. Por favor valide la información en la ruta PANEL PRINCIPAL - ADIMISTRACION SISTEMA - GENERADOR DE REPORTES (FACTURACION) - INFORMES. Realice el ajuste pertinente en plan contable. '|| CHR(13) || CHR(10);
              MI_INCONSISTENCIA := TRUE;
            END; 
            /*************************************************************** INICIO DESARROLLO COMPROBANTES 7712290 ***************************************************
             * buscamos los comprobantes antes de existir
            */
            MI_FECHA := MI_DATOS_COLUMNAS(1);
            -- TICKET 7712209: Se realiza el conteo de los comprobantes y detalles de comprobantes de la fecha para no eliminarlos, sino actualizarlos.
            -- SE CONSULTA EL ESTADO DEL PERIODO EN CASO DE QUE NO ESTE ACTIVO NO CONTINUA, MUESTRA ERROR.
            MI_RTA := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADODIA(UN_COMPANIA   => UN_COMPANIA,
                                        UN_ANO        => TO_NUMBER(TO_CHAR(TO_DATE(MI_FECHA,'DD/MM/YYYY'),'YYYY')),
                                        UN_MES        => TO_NUMBER(TO_CHAR(TO_DATE(MI_FECHA,'DD/MM/YYYY'),'MM')),
                                        UN_DIA        => TO_NUMBER(TO_CHAR(TO_DATE(MI_FECHA,'DD/MM/YYYY'),'DD')),
                                        UN_MODULO     => 1,
                                        UN_PROCESO    => 1);
            IF MI_RTA <> 'A' THEN
                  DECLARE
                  MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR; 
                  BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;           
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                     MI_RETORNO := MI_RETORNO|| 'El periodo de la fecha  '||MI_FECHA||' está cerrado, por favor verifique las fechas que se estan enviando '|| CHR(13) || CHR(10);
                     MI_INCONSISTENCIA := TRUE;
                  END;   
            END IF;
            -- consultamos si para la fecha ya existe un comprobante.
            -- CIP
            /* SELECT COUNT(NUMERO) INTO V_CANTCOMPRSEXISTE_CNT FROM COMPROBANTE_CNT WHERE COMPANIA = UN_COMPANIA
                          AND ANO       = UN_ANIO
                          AND TIPO     = UN_TIPOCOBRO
                          AND TO_CHAR(FECHA,'DD/MM/YYYY')     = MI_FECHA
                          AND TERCERO   = UN_TERCERO
                          AND SUCURSAL  = MI_SUCURSAL; */
                          
                          V_CANTCOMPRSEXISTE_CNT := 0;
            -- RIP
            /* SELECT COUNT(NUMERO) INTO V_CANTCOMPRSEXISTE_RIP_CNT FROM COMPROBANTE_CNT WHERE COMPANIA = UN_COMPANIA
                          AND ANO       = UN_ANIO
                          AND TIPO     = MI_CPTERECAUDO
                          AND TO_CHAR(FECHA,'DD/MM/YYYY')     = MI_FECHA
                          AND TERCERO   = UN_TERCERO
                          AND SUCURSAL  = MI_SUCURSAL; */
                          
                          V_CANTCOMPRSEXISTE_RIP_CNT := 0;
            -- si el comprobante existe la varaiable V_CANTCOMPRSEXISTE_CNT es mayor a 0, por que si tiene registros de la fecha entrante.                            
            IF( NOT MI_INCONSISTENCIA AND V_CANTCOMPRSEXISTE_CNT > 0 AND V_CANTCOMPRSEXISTE_RIP_CNT > 0)THEN  -- evaluamos si existe un comprobante para la fecha creada.
              -- evaluamos si existe un comprobante para la fecha creada.   
                 -- OBTENEMOS EL NUMERO DEL COMPROBANTE CNT CIP
                SELECT NUMERO, VLR_BASE
                INTO 
                    V_NUMCPTEEXISTE_CNT, V_VLRBASE_CNT 
                FROM COMPROBANTE_CNT 
                          WHERE COMPANIA = UN_COMPANIA
                          AND ANO       = UN_ANIO
                          AND TIPO     = UN_TIPOCOBRO
                          AND TO_CHAR(FECHA,'DD/MM/YYYY')     = MI_FECHA
                          AND TERCERO   = UN_TERCERO
                          AND SUCURSAL  = MI_SUCURSAL
                          AND ROWNUM <= 1;
                -- obtenemos el numero del consecutivo del comprbante_cnt tipo RIP
                    SELECT NUMERO
                        INTO V_NUMCPTEEXISTE_CNT_RECAUDO 
                    FROM COMPROBANTE_CNT 
                          WHERE COMPANIA = UN_COMPANIA
                          AND ANO       = UN_ANIO
                          AND TIPO     = MI_CPTERECAUDO
                          AND TO_CHAR(FECHA,'DD/MM/YYYY')     = MI_FECHA
                          AND TERCERO   = UN_TERCERO
                          AND SUCURSAL  = MI_SUCURSAL
                          AND ROWNUM <= 1;
                -- AJUSTAMOS EL VALOR PARA PASARLO A LA CONSULTA
                SELECT INSTR(MI_DATOS_COLUMNAS(3),'.') INTO V_PUNTODECIMALINDEX FROM DUAL;
                SELECT INSTR(MI_DATOS_COLUMNAS(3),'-') INTO V_SIGNOINDEX FROM DUAL;
                IF(V_SIGNOINDEX = 0 AND V_PUNTODECIMALINDEX <> 0)THEN
                    V_PUNTODECIMALINDEX := V_PUNTODECIMALINDEX - 1;
                    V_SIGNOINDEX := 0;
                    V_VALORTEMP := TO_NUMBER(SUBSTR(MI_DATOS_COLUMNAS(3),V_SIGNOINDEX,V_PUNTODECIMALINDEX));
                ELSIF(V_SIGNOINDEX <> 0 AND V_PUNTODECIMALINDEX <> 0)THEN
                    V_PUNTODECIMALINDEX := V_PUNTODECIMALINDEX - 2;
                    V_SIGNOINDEX := V_SIGNOINDEX + 1;
                    V_VALORTEMP := TO_NUMBER(SUBSTR(MI_DATOS_COLUMNAS(3),V_SIGNOINDEX,V_PUNTODECIMALINDEX));
                END IF;
    
                 -- en esta seccion verificamos si los conceptos tienen la misma cuenta debito y credito
                 IF(NOT MI_INCONSISTENCIA AND MI_DESCUENTO IN (0)) THEN
                    IF(RS != MI_DATOS_FILA.LAST)THEN
                        MI_DATOS_COLUMNAS_NEXT := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS+1),
                                                             UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);            
                        BEGIN                                                
                            MI_DATOS_COLUMNAS_NEXT_NEXT := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS+2),
                                                             UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);
                        EXCEPTION WHEN OTHERS THEN
                            MI_CONTINUE := 1;
                        END;
                        IF(MI_CONTINUE IS NULL) THEN
                          BEGIN
                          SELECT CUENTADEBITOBASE,
                                CUENTACREDITOBASE                   
                          INTO   MI_CUENTADEBITOBASE_NEXT,
                                MI_CUENTACREDITOBASE_NEXT                   
                          FROM SF_CONCEPTOS 
                          WHERE COMPANIA = UN_COMPANIA
                          AND ANO = UN_ANIO
                          AND TIPOCOBRO = UN_TIPOCOBRO
                          AND CODIGO = MI_DATOS_COLUMNAS_NEXT(2);
                          SELECT CUENTADEBITOBASE,
                                CUENTACREDITOBASE                   
                          INTO   MI_CUENTADEBITOBASE_NEXT_NEXT,
                                MI_CUENTACREDITOBASE_NEXT_NEXT                   
                          FROM SF_CONCEPTOS 
                          WHERE COMPANIA = UN_COMPANIA
                          AND ANO = UN_ANIO
                          AND TIPOCOBRO = UN_TIPOCOBRO
                          AND CODIGO = MI_DATOS_COLUMNAS_NEXT_NEXT(2);
                          EXCEPTION WHEN NO_DATA_FOUND THEN 
                              MI_RETORNO := MI_RETORNO|| 'El concepto '||MI_DATOS_COLUMNAS_NEXT_NEXT(2)||' no ha sido configurado en el formulario de conceptos. '|| CHR(13) || CHR(10);
                              MI_INCONSISTENCIA := TRUE; 
                          -- SE VERIFICA SI LA CUENTA DEL SIGUENTE REGISTRO ES IGUAL A LA DEL CONCEPTO ACTUAL, PARA ACUMLAR EL VALOR Y ASI PODER BUSCARLO EN LA CONTABILIDAD.
                          -- ESTO SE HACE POR QUE LOS DOS CONCEPTOS EN LA CONTABILIDAD VAN ACUMULADOS COMO SI, ES CASI EL MISMO CONCEPTO.
                          -- SE IDENTIFICAN QUE SON DEL MISMO CONCEPTO, POR QUE TRAN LAS CUENTAS IGUALES TANTO EN DEBITOS COMO EN CREDITOS. 
                          END;
                        END IF;
                        IF(MI_CUENTADEBITOBASE_NEXT = MI_CUENTADEBITOBASE AND MI_CUENTADEBITOBASE = MI_CUENTADEBITOBASE_NEXT_NEXT AND MI_CUENTACREDITOBASE_NEXT = MI_CUENTACREDITOBASE AND MI_CUENTACREDITOBASE = MI_CUENTACREDITOBASE_NEXT_NEXT )THEN
                            V_VALORTEMP := V_VALORTEMP + ABS(TO_NUMBER(MI_DATOS_COLUMNAS_NEXT(3))) + ABS(TO_NUMBER(MI_DATOS_COLUMNAS_NEXT_NEXT(3)));
                            MI_DATOS_FILA(RS+1) := NULL;
                            MI_DATOS_FILA(RS+2) := NULL;
                        ELSE
                            IF(MI_CUENTADEBITOBASE_NEXT = MI_CUENTADEBITOBASE AND MI_CUENTACREDITOBASE_NEXT = MI_CUENTACREDITOBASE)THEN
                               V_VALORTEMP := V_VALORTEMP + TO_NUMBER(MI_DATOS_COLUMNAS_NEXT(3));
                               MI_DATOS_FILA(RS+1) := NULL;
                            END IF;
                        END IF;
                    END IF;
                 END IF;
    
              IF(MI_DESCUENTO IN (0)) THEN
                  -- COMPROBAMOS QUE LOS DETALLES NO EXISTAN
                  SELECT COUNT(CONSECUTIVO) INTO V_EXISTEDETALLE 
                  FROM DETALLE_COMPROBANTE_CNT 
                  WHERE DETALLE_COMPROBANTE_CNT.LOTE = UN_LOTE
                  AND DETALLE_COMPROBANTE_CNT.COMPANIA = UN_COMPANIA;
                  
                  /*AND DETALLE_COMPROBANTE_CNT.ANO = UN_ANIO
                  AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = UN_TIPOCOBRO
                  AND DETALLE_COMPROBANTE_CNT.COMPROBANTE = V_NUMCPTEEXISTE_CNT 
                  AND TRIM(DETALLE_COMPROBANTE_CNT.DESCRIPCION) = TRIM(MI_DATOS_COLUMNAS(5)) 
                  AND (TRIM(DETALLE_COMPROBANTE_CNT.VALOR_DEBITO) = TRIM(TRUNC(V_VALORTEMP,0)) OR TRIM(DETALLE_COMPROBANTE_CNT.VALOR_CREDITO) = TRIM(TRUNC(V_VALORTEMP,0))); */
                  IF(V_EXISTEDETALLE = 0) THEN
                    -- AUMENTAMOS EL VALOR DE COMPROBANTE
                    V_VLRBASE_CNT := V_VLRBASE_CNT + V_VALORTEMP;
                    -- OBTENEMOS EL CONSECUTIVO PROXIMO DETALLE_COMPROBANTE_CNT TIPO CIP.            
                    /*MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO('DETALLE_COMPROBANTE_CNT', 'DETALLE_COMPROBANTE_CNT.COMPANIA ='''
                                        ||UN_COMPANIA||''''
                                        ||' AND DETALLE_COMPROBANTE_CNT.ANO = '||UN_ANIO
                                        ||' AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = '''
                                        ||UN_TIPOCOBRO
                                        ||''' AND DETALLE_COMPROBANTE_CNT.COMPROBANTE='||V_NUMCPTEEXISTE_CNT, 'CONSECUTIVO', '1'); */
                    MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO('TEMP_PLANA_AJUSTES', 'TEMP_PLANA_AJUSTES.COMPANIA ='''
                                        ||UN_COMPANIA||''''
                                        ||' AND TEMP_PLANA_AJUSTES.ANO = '||UN_ANIO
                                        ||' AND TEMP_PLANA_AJUSTES.TIPO_CPTE = '''
                                        ||UN_TIPOCOBRO
                                        ||''' AND TEMP_PLANA_AJUSTES.COMPROBANTE='||MI_NUMERO, 'CONSECUTIVO', '1');
                    -- se agrega validacion para la cuenta 
                    IF MI_CLASECONTABLE IN ('V')THEN          
                          MI_DEBITO   := MI_CUENTADEBITOBASE;
                          MI_CREDITO  := MI_CUENTACREDITOBASE;        
                   ELSE  
                          MI_CREDITO  := MI_CUENTADEBITOBASE;
                          MI_DEBITO   := MI_CUENTACREDITOBASE;        
    
                   END IF;
                    -- SI EXISTE ,agregamos los detalles al comprobante, tanto para CIP, RIP y comprobantepptal.
                    -- se inserta el debito CIP en tabla cnt
                      
                    MI_CAMPOS := 'LOTE,
                                  COMPANIA, 
                                  ANO,
                                  TIPO_CPTE,
                                  COMPROBANTE,
                                  CUENTA,
                                  CONSECUTIVO,
                                  NATURALEZA,
                                  CUENTAPPTAL,
                                  FECHA,
                                  DESCRIPCION,
                                  TERCERO,
                                  SUCURSAL,
                                  CENTRO_COSTO,
                                  AUXILIAR,
                                  FUENTE_RECURSOS,
                                  VALOR_DEBITO,
                                  VALOR_CREDITO,
                                  EJECUCION_DEBITO,
                                  EJECUCION_CREDITO
                                  ';
                    MI_VALORES :=''   ||  UN_LOTE             || ',
                                  ''' ||  UN_COMPANIA         || ''',
                                  '   ||  UN_ANIO              || ',
                                  ''' ||  UN_TIPOCOBRO        || ''',
                                  '   ||  MI_NUMERO || ',
                                  ''' ||  MI_DEBITO || ''',
                                  '   ||  MI_CONSECUTIVO      || ',
                                  ''D'',
                                  ''' ||  MI_CTAPPTALDEBITO || ''',
                                  ''' ||  TO_DATE(MI_FECHA,'DD/MM/YYYY') || ''',
    
                                  ''' ||  MI_DATOS_COLUMNAS(5)|| ''',
                                  ''' ||  UN_TERCERO          || ''',
                                  ''' ||  MI_SUCURSAL         || ''',
                                  ''' ||  MI_CENTRO           || ''',
                                  ''' ||  MI_AUXILIAR         || ''',
                                  ''' ||  CASE WHEN MI_CLASE_BANCO IN('B') THEN PCK_DATOS.CONS_FUENTE ELSE MI_FUENTE END           || ''', --JM 7749239 22/07/2024
                                  '   ||  V_VALORTEMP || ' ,
                                  '   ||  0 || ',
                                  '   ||  V_VALORTEMP || ' ,
                                  '   ||  0 || ' ';
                          
                    BEGIN
                        BEGIN
                            PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA   => 'TEMP_PLANA_AJUSTES', 
                                                              UN_ACCION  => 'I', 
                                                              UN_CAMPOS  => MI_CAMPOS, 
                                                              UN_VALORES => MI_VALORES);
                            MI_YAINSERTADO := TRUE;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
                    END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                                            UN_EXC_COD =>SQLCODE
                                            ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                            ,UN_REEMPLAZOS  => MI_MSGERROR);
                        MI_YAINSERTADO := FALSE;
                    END;
                   
                    -- se inserta el credito CIP en tabla cnt
                    MI_CONSECUTIVO:=MI_CONSECUTIVO + 1;
                    MI_CAMPOS := 'LOTE,
                                  COMPANIA,
                                  ANO,
                                  TIPO_CPTE,
                                  COMPROBANTE,
                                  CUENTA,
                                  CONSECUTIVO,
                                  NATURALEZA,
                                  CUENTAPPTAL,
                                  FECHA,
                                  DESCRIPCION,
                                  TERCERO,
                                  SUCURSAL,
                                  CENTRO_COSTO,
                                  AUXILIAR,
                                  FUENTE_RECURSOS,
                                  VALOR_DEBITO,
                                  VALOR_CREDITO,
                                  EJECUCION_DEBITO,
                                  EJECUCION_CREDITO
                                  ';
                    MI_VALORES :=''   ||  UN_LOTE             || ',
                                  ''' ||  UN_COMPANIA         || ''',
                                  '   ||  UN_ANIO             || ',
                                  ''' ||  UN_TIPOCOBRO        || ''',
                                  '   ||  MI_NUMERO || ',
                                  ''' ||  MI_CREDITO|| ''',
                                  '   ||  MI_CONSECUTIVO      || ',
                                  ''C'',
                                  ''' ||  MI_CTAPPTALCREDITO || ''',
                                  ''' ||  TO_DATE(MI_FECHA,'DD/MM/YYYY')|| ''',

                                  ''' ||  MI_DATOS_COLUMNAS(5)|| ''',
                                  ''' ||  UN_TERCERO          || ''',
                                  ''' ||  MI_SUCURSAL         || ''',
                                  ''' ||  MI_CENTRO           || ''',
                                  ''' ||  MI_AUXILIAR         || ''',
                                  ''' ||   CASE WHEN MI_CLASE_BANCO IN('B') THEN PCK_DATOS.CONS_FUENTE ELSE MI_FUENTE END           || ''', --JM 7749239 22/07/2024
                                  '   ||  0 || ',
                                  '   ||  V_VALORTEMP || ',
                                  '   ||  0 || ',
                                  '   ||  V_VALORTEMP || ' ';
                                  
                    BEGIN 
                        BEGIN
                            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => 'TEMP_PLANA_AJUSTES', 
                                                              UN_ACCION  => 'I', 
                                                              UN_CAMPOS  => MI_CAMPOS, 
                                                              UN_VALORES => MI_VALORES);
                            MI_YAINSERTADO := TRUE;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
                        END;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                              PCK_ERR_MSG.RAISE_WITH_MSG(
                                            UN_EXC_COD =>SQLCODE
                                            ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                            ,UN_REEMPLAZOS  => MI_MSGERROR);
                                MI_YAINSERTADO := FALSE;
                    END;
                  
       
                    MI_CONSECUTIVO:=0;
    
                    -- OBTENEMOS EL CONSECUTIVO PROXIMO DETALLE_COMPROBANTE_CNT TIPO RIP.            
                    MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO('TEMP_PLANA_AJUSTES', 'TEMP_PLANA_AJUSTES.COMPANIA ='''
                                        ||UN_COMPANIA||''''
                                        ||' AND TEMP_PLANA_AJUSTES.ANO = '||UN_ANIO
                                        ||' AND TEMP_PLANA_AJUSTES.TIPO_CPTE = '''
                                        ||MI_CPTERECAUDO
                                        ||''' AND TEMP_PLANA_AJUSTES.COMPROBANTE='||MI_NUMERO, 'CONSECUTIVO', '1');
                    -- se inserta el debito RIP en tabla cnt
                    MI_CAMPOS := 'LOTE,
                                  COMPANIA,
                                  ANO,
                                  TIPO_CPTE,
                                  COMPROBANTE,
                                  CUENTA,
                                  CONSECUTIVO,
                                  NATURALEZA,
                                  CUENTAPPTAL,
                                  FECHA,
                                  DESCRIPCION,
                                  TERCERO,
                                  SUCURSAL,
                                  CENTRO_COSTO,
                                  AUXILIAR,
                                  FUENTE_RECURSOS,
                                  VALOR_DEBITO,
                                  VALOR_CREDITO,
                                  EJECUCION_DEBITO,
                                  EJECUCION_CREDITO
                                  ';
                    MI_VALORES := ''   ||  UN_LOTE             || ',
                                  ''' ||  UN_COMPANIA         || ''',
                                  '   ||  UN_ANIO             || ',
                                  ''' ||  MI_CPTERECAUDO      || ''',
                                  '   ||  MI_NUMERO || ',
                                  ''' ||  MI_DEBITO || ''',
                                  '   ||  MI_CONSECUTIVO      || ',
                                  ''D'',
                                  ''' ||  MI_CTAPPTALDEBITO || ''',
                                  ''' ||  TO_DATE(MI_FECHA,'DD/MM/YYYY')|| ''',
    
                                  ''' ||  MI_DATOS_COLUMNAS(5)|| ''',
                                  ''' ||  UN_TERCERO          || ''',
                                  ''' ||  MI_SUCURSAL         || ''',
                                  ''' ||  MI_CENTRO           || ''',
                                  ''' ||  MI_AUXILIAR         || ''',
                                  ''' ||   CASE WHEN MI_CLASE_BANCO IN('B') THEN PCK_DATOS.CONS_FUENTE ELSE MI_FUENTE END           || ''', --JM 7749239 22/07/2024
                                  '   ||  0 || ' ,
                                  '   ||  V_VALORTEMP || ',
                                  '   ||  0  || ' ,
                                  '   ||  V_VALORTEMP || ' ';
                                  
                    BEGIN    
                        BEGIN
                            PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA   => 'TEMP_PLANA_AJUSTES', 
                                                              UN_ACCION  => 'I', 
                                                              UN_CAMPOS  => MI_CAMPOS, 
                                                              UN_VALORES => MI_VALORES);
                            MI_YAINSERTADO := TRUE;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
                        END;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                                  PCK_ERR_MSG.RAISE_WITH_MSG(
                                            UN_EXC_COD =>SQLCODE
                                            ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                            ,UN_REEMPLAZOS  => MI_MSGERROR);
                                MI_YAINSERTADO := FALSE;
                    END;           
                  
                    /*Se obtiene la cuenta presupuestal para la cuenta de recaudo crédito - MPEREZ*/              
                    BEGIN
                        SELECT RUBRO
                          INTO MI_CTAPPTALCREDITO
                          FROM PLAN_PPTAL_CUENTACNT          
                         WHERE COMPANIA = UN_COMPANIA
                           AND ANO      = UN_ANIO
                           AND CUENTA_CONTABLE    = MI_CUENTARECAUDO;
                    EXCEPTION WHEN NO_DATA_FOUND THEN 
                        MI_CTAPPTALCREDITO := '';
                    END;
    
                    --AQUI JACO
                    -- se inserta el credito RIP en tabla cnt
                    MI_CONSECUTIVO:=MI_CONSECUTIVO + 1;
                    MI_CAMPOS := 'LOTE,
                                  COMPANIA,
                                  ANO,
                                  TIPO_CPTE,
                                  COMPROBANTE,
                                  CUENTA,
                                  CONSECUTIVO,
                                  NATURALEZA,
                                  CUENTAPPTAL,
                                  FECHA,
                                  DESCRIPCION,
                                  TERCERO,
                                  SUCURSAL,
                                  CENTRO_COSTO,
                                  AUXILIAR,
                                  FUENTE_RECURSOS,
                                  VALOR_DEBITO,
                                  VALOR_CREDITO,
                                  EJECUCION_DEBITO,
                                  EJECUCION_CREDITO ';
                    MI_VALORES :=''   ||  UN_LOTE             || ',
                                  ''' ||  UN_COMPANIA         || ''',
                                  '   ||  UN_ANIO             || ',
                                  ''' ||  MI_CPTERECAUDO      || ''',
                                  '   ||  MI_NUMERO || ',
                                  ''' ||  MI_CUENTARECAUDO    || ''',
                                  '   ||  MI_CONSECUTIVO      || ',
                                  ''C'',
                                  ''' ||  MI_CTAPPTALCREDITO || ''',
                                  ''' ||  TO_DATE(MI_FECHA,'DD/MM/YYYY')|| ''',

                                  ''' ||  MI_DATOS_COLUMNAS(5)|| ''',
                                  ''' ||  UN_TERCERO          || ''',
                                  ''' ||  MI_SUCURSAL         || ''',
                                  ''' ||  MI_CENTRO           || ''',
                                  ''' ||  MI_AUXILIAR         || ''',
                                  ''' ||   CASE WHEN MI_CLASE_BANCO IN('B') THEN PCK_DATOS.CONS_FUENTE ELSE MI_FUENTE END           || ''', --JM 7749239 22/07/2024
                                  '   ||  V_VALORTEMP || ' ,
                                  '   ||  0 || ',
                                  '   ||  V_VALORTEMP || ' ,
                                  '   ||  0 || ' ';
                                  
                    BEGIN    
                        BEGIN
                            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => 'TEMP_PLANA_AJUSTES', 
                                                              UN_ACCION  => 'I', 
                                                              UN_CAMPOS  => MI_CAMPOS, 
                                                              UN_VALORES => MI_VALORES);
                            MI_YAINSERTADO := TRUE;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
                      END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                        ,UN_REEMPLAZOS  => MI_MSGERROR);
                        MI_YAINSERTADO := FALSE;
                    END;                    
                    
                      BEGIN
                      SELECT 
                              PPTAL.NUMERO, PPTAL.DESCRIPCION--, DPPTAL.CUENTA 
                          INTO 
                              V_NUMCPTEEXISTE_PPTAL, V_DESCRIPCION_PPTAL--, V_CUENTAPPTAL
                      FROM COMPROBANTE_PPTAL PPTAL JOIN DETALLE_COMPROBANTE_PPTAL DPPTAL ON (PPTAL.NUMERO = DPPTAL.COMPROBANTE)
                        WHERE PPTAL.COMPANIA = UN_COMPANIA
                        AND PPTAL.ANO       = UN_ANIO
                        AND PPTAL.TIPO     = MI_CPTERECAUDO
                        AND TO_CHAR(PPTAL.FECHA,'DD/MM/YYYY') = MI_FECHA
                        AND PPTAL.TERCERO   = UN_TERCERO
                        AND PPTAL.SUCURSAL  = MI_SUCURSAL AND ROWNUM = 1;
                    EXCEPTION WHEN NO_DATA_FOUND THEN 
                        V_NUMCPTEEXISTE_PPTAL := '';
                      END;  
                      IF(V_NUMCPTEEXISTE_PPTAL > 0)THEN 
                        SELECT COUNT(CONSECUTIVO) INTO V_CONTCONSECDETPPTAL
                        FROM DETALLE_COMPROBANTE_PPTAL 
                        WHERE COMPANIA = UN_COMPANIA 
                        AND LOTE = UN_LOTE ;
                        --AND TIPO_CPTE = MI_CPTERECAUDO 
                        --AND COMPROBANTE = V_NUMCPTEEXISTE_PPTAL
                        --AND TRIM(DETALLE_COMPROBANTE_PPTAL.DESCRIPCION) = TRIM(MI_DATOS_COLUMNAS(5)) -- MPEREZ
                        --AND TRIM(VALOR_CREDITO) = TRIM(TRUNC(V_VALORTEMP,0));
                        IF(V_CONTCONSECDETPPTAL = 0)THEN
                              MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO('TEMP_PLANA_AJUSTES', 'TEMP_PLANA_AJUSTES.COMPANIA ='''
                                                      ||UN_COMPANIA||''''
                                                      ||' AND TEMP_PLANA_AJUSTES.ANO = '||UN_ANIO
                                                      ||' AND TEMP_PLANA_AJUSTES.TIPO_CPTE = '''
                                                      ||MI_CPTERECAUDO
                                                      ||''' AND TEMP_PLANA_AJUSTES.COMPROBANTE='||MI_NUMERO, 'CONSECUTIVO', '1');
                              -- se inserta el CREDITO DEL TIPO RIP en tabla PPTAL
                                 IF(MI_CTAPPTALDEBITO IS NOT NULL)THEN
    
                                  MI_CAMPOS := 'LOTE,
                                                COMPANIA,
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
                                                FUENTE_RECURSOS,
                                                VALOR_DEBITO,
                                                VALOR_CREDITO';
                                  MI_VALORES :=''   ||  UN_LOTE             || ',
                                                ''' ||  UN_COMPANIA         || ''',
                                                '   ||  UN_ANIO             || ',
                                                ''' ||  MI_CPTERECAUDO      || ''',
                                                '   ||  MI_NUMERO || ',
                                                ''' ||  MI_CTAPPTALDEBITO       || ''',
                                                '   ||  MI_CONSECUTIVO      || ',
                                                ''D'', 
                                                ''' ||  TO_DATE(MI_FECHA,'DD/MM/YYYY')|| ''',
                                                ''' ||  MI_DATOS_COLUMNAS(5)||''',
                                                ''' ||  UN_TERCERO          || ''',
                                                ''' ||  MI_SUCURSAL         || ''',
                                                ''' ||  MI_CENTRO           || ''',
                                                ''' ||  MI_AUXILIAR         || ''',
                                                ''' ||  MI_FUENTE           || ''',
                                                '   ||  0 || ' ,
                                                '   ||  V_VALORTEMP|| ''; 
                                                  
                                                
                                  BEGIN    
                                      BEGIN
                                          PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA   => 'TEMP_PLANA_AJUSTES', 
                                                                                  UN_ACCION  => 'I', 
                                                                                  UN_CAMPOS  => MI_CAMPOS, 
                                                                                  UN_VALORES => MI_VALORES);
                                          MI_YAINSERTADO := TRUE;
                                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                          RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
                                  END;
    
                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                                  PCK_ERR_MSG.RAISE_WITH_MSG(
                                                          UN_EXC_COD =>SQLCODE
                                                          ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                          ,UN_REEMPLAZOS  => MI_MSGERROR);
                                  MI_YAINSERTADO := FALSE;
                              END;
                               
    
                              END IF;
                    ELSE
    
                      MI_YAINSERTADO := TRUE;
                    END IF;
    
                  ELSE 
                      MI_YAINSERTADO := TRUE;      
                  END IF;
                  -- para comprobantes CIP
                  SELECT INSTR(V_COMPROBANTESACTUALZIADOS, V_NUMCPTEEXISTE_CNT) INTO V_NUMCPTEEXISTE_CNT_AUX FROM DUAL;
                  IF(V_NUMCPTEEXISTE_CNT_AUX = 0 OR V_NUMCPTEEXISTE_CNT_AUX IS NULL)THEN
                      V_COMPROBANTESACTUALZIADOS := V_COMPROBANTESACTUALZIADOS||','||V_NUMCPTEEXISTE_CNT;
                  END IF;
                  -- para comprobantes RIP
                  SELECT INSTR(V_COMPROBANTESACTUALZIADOS_RIP, V_NUMCPTEEXISTE_CNT_RECAUDO) INTO V_NUMCPTEEXISTE_CNT_AUX FROM DUAL;
                  IF(V_NUMCPTEEXISTE_CNT_AUX = 0 OR V_NUMCPTEEXISTE_CNT_AUX IS NULL)THEN
                      V_COMPROBANTESACTUALZIADOS_RIP := V_COMPROBANTESACTUALZIADOS_RIP||','||V_NUMCPTEEXISTE_CNT_RECAUDO;
                  END IF;
                ELSE
                  MI_YAINSERTADO := TRUE;
                  SELECT INSTR(V_COMPROBANTESNOACTUALZIADOS, V_NUMCPTEEXISTE_CNT) INTO V_NUMCPTEEXISTE_CNT_AUX FROM DUAL;
                  IF(V_NUMCPTEEXISTE_CNT_AUX = 0 OR V_NUMCPTEEXISTE_CNT_AUX IS NULL)THEN
                      V_COMPROBANTESNOACTUALZIADOS := V_COMPROBANTESNOACTUALZIADOS||','||V_NUMCPTEEXISTE_CNT;
                  END IF;
                  -- para comprobantes RIP
                  SELECT INSTR(V_COMPROBANTESACTUALZIADOS_RIP, V_NUMCPTEEXISTE_CNT_RECAUDO) INTO V_NUMCPTEEXISTE_CNT_AUX FROM DUAL;
                  IF(V_NUMCPTEEXISTE_CNT_AUX = 0 OR V_NUMCPTEEXISTE_CNT_AUX IS NULL)THEN
                      V_COMPROBANTESACTUALZIADOS_RIP := V_COMPROBANTESACTUALZIADOS_RIP||','||V_NUMCPTEEXISTE_CNT_RECAUDO;
                  END IF;
                END IF;
              ELSE
                IF NOT MI_INCONSISTENCIA THEN
                  -- VERIFICAMOS QUE EL DESCUENTO NO EXISTA COMO RIP EN CNT
                  SELECT COUNT(CONSECUTIVO) INTO V_EXISTEDETALLE 
                  FROM DETALLE_COMPROBANTE_CNT 
                  WHERE DETALLE_COMPROBANTE_CNT.COMPANIA = UN_COMPANIA
                  AND DETALLE_COMPROBANTE_CNT.LOTE = UN_LOTE;
                 -- AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = MI_CPTERECAUDO
                 -- AND DETALLE_COMPROBANTE_CNT.COMPROBANTE = V_NUMCPTEEXISTE_CNT_RECAUDO 
                 -- AND TRIM(DETALLE_COMPROBANTE_CNT.DESCRIPCION) = TRIM(MI_DATOS_COLUMNAS(5))
                 -- AND (TRIM(DETALLE_COMPROBANTE_CNT.VALOR_CREDITO) = TRIM(TRUNC(V_VALORTEMP,0)));
                  IF(V_EXISTEDETALLE = 0)THEN
                      MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO('TEMP_PLANA_AJUSTES', 'TEMP_PLANA_AJUSTES.COMPANIA ='''
                                              ||UN_COMPANIA||''''
                                              ||' AND TEMP_PLANA_AJUSTES.ANO = '||UN_ANIO
                                              ||' AND TEMP_PLANA_AJUSTES.TIPO_CPTE = '''
                                              ||MI_CPTERECAUDO
                                              ||''' AND TEMP_PLANA_AJUSTES.COMPROBANTE='||MI_NUMERO, 'CONSECUTIVO', '1');
                      IF  MI_CUENTADEBITOBASE = '110501' OR MI_CUENTADEBITOBASE = '1110' THEN
                              MI_DEBITO   := MI_CUENTARECAUDO;
                              MI_CREDITO  := MI_CUENTACREDITOBASE;   
                      ELSE
                              MI_DEBITO   := MI_CUENTADEBITOBASE;
                              MI_CREDITO  := MI_CUENTARECAUDO;   
                      END IF; 
    
                      /*Se obtiene la cuenta presupuestal para la cuenta contable débito - MPEREZ*/
                        BEGIN
                            SELECT RUBRO
                              INTO MI_CTAPPTALDEBITO
                              FROM PLAN_PPTAL_CUENTACNT          
                             WHERE COMPANIA = UN_COMPANIA
                               AND ANO      = UN_ANIO
                               AND CUENTA_CONTABLE    = MI_DEBITO;
                        EXCEPTION WHEN NO_DATA_FOUND THEN 
                            MI_CTAPPTALDEBITO := '';
                        END;
    
                        /*Se obtiene la cuenta presupuestal para la cuenta contable crédito - MPEREZ*/              
                        BEGIN
                            SELECT RUBRO
                              INTO MI_CTAPPTALCREDITO
                              FROM PLAN_PPTAL_CUENTACNT          
                             WHERE COMPANIA = UN_COMPANIA
                               AND ANO      = UN_ANIO
                               AND CUENTA_CONTABLE    = MI_CREDITO;
                        EXCEPTION WHEN NO_DATA_FOUND THEN 
                            MI_CTAPPTALCREDITO := '';
                        END;
                          -- se inserta el debito RIP en tabla cnt
                          MI_CAMPOS := 'LOTE,
                                        COMPANIA,
                                        ANO,
                                        TIPO_CPTE,
                                        COMPROBANTE,
                                        CUENTA,
                                        CONSECUTIVO,
                                        NATURALEZA,
                                        CUENTAPPTAL,
                                        FECHA,
                                        DESCRIPCION,
                                        TERCERO,
                                        SUCURSAL,
                                        CENTRO_COSTO,
                                        AUXILIAR,
                                        FUENTE_RECURSOS,
                                        VALOR_DEBITO,
                                        VALOR_CREDITO,
                                        EJECUCION_DEBITO,
                                        EJECUCION_CREDITO
                                        ';
                          MI_VALORES :=''   ||  UN_LOTE             || ',
                                        ''' ||  UN_COMPANIA         || ''',
                                        '   ||  UN_ANIO             || ',
                                        ''' ||  MI_CPTERECAUDO      || ''',
                                        '   ||  V_NUMCPTEEXISTE_CNT_RECAUDO || ',
                                        ''' ||  MI_DEBITO || ''',
                                        '   ||  MI_CONSECUTIVO      || ',
                                        ''D'',
                                        ''' ||  MI_CTAPPTALDEBITO || ''',
                                        ''' ||  TO_DATE(MI_FECHA,'DD/MM/YYYY')|| ''',
    
                                        ''' ||  MI_DATOS_COLUMNAS(5)|| ''',
                                        ''' ||  UN_TERCERO          || ''',
                                        ''' ||  MI_SUCURSAL         || ''',
                                        ''' ||  MI_CENTRO           || ''',
                                        ''' ||  MI_AUXILIAR         || ''',
                                        ''' ||   CASE WHEN MI_CLASE_BANCO IN('B') THEN PCK_DATOS.CONS_FUENTE ELSE MI_FUENTE END           || ''', --JM 7749239 22/07/2024
                                        '   ||  V_VALORTEMP || ' ,
                                        '   ||  0 || ',
                                        '   ||  V_VALORTEMP || ' ,
                                        '   ||  0 || ' ';
                                        
                          BEGIN    
                              BEGIN
                                  PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA   => 'TEMP_PLANA_AJUSTES', 
                                                                    UN_ACCION  => 'I', 
                                                                    UN_CAMPOS  => MI_CAMPOS, 
                                                                    UN_VALORES => MI_VALORES);
                                  MI_YAINSERTADO := TRUE;
                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                  RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
                              END;
                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
                                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                                  UN_EXC_COD =>SQLCODE
                                                  ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                  ,UN_REEMPLAZOS  => MI_MSGERROR);
                                      MI_YAINSERTADO := FALSE;
                          END;
                          
                          MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
                          -- SE INSERTA EL CREDITO RIP EN LA TABLA DETALLE CNT 
                          MI_CAMPOS := 'LOTE,
                                        COMPANIA,
                                        ANO,
                                        TIPO_CPTE,
                                        COMPROBANTE,
                                        CUENTA,
                                        CONSECUTIVO,
                                        NATURALEZA,
                                        CUENTAPPTAL,
                                        FECHA,
                                        DESCRIPCION,
                                        TERCERO,
                                        SUCURSAL,
                                        CENTRO_COSTO,
                                        AUXILIAR,
                                        FUENTE_RECURSOS,
                                        VALOR_DEBITO,
                                        VALOR_CREDITO,
                                        EJECUCION_DEBITO,
                                        EJECUCION_CREDITO';
                          MI_VALORES :=''   ||  UN_LOTE             || ',
                                        ''' ||  UN_COMPANIA         || ''',
                                        '   ||  UN_ANIO             || ',
                                        ''' ||  MI_CPTERECAUDO      || ''',
                                        '   ||  MI_NUMERO || ',
                                        ''' ||  MI_CREDITO || ''',
                                        '   ||  MI_CONSECUTIVO      || ',
                                        ''C'',
                                        ''' ||  MI_CTAPPTALCREDITO || ''',
                                        ''' ||  TO_DATE(MI_FECHA,'DD/MM/YYYY')|| ''',
    
                                        ''' ||  MI_DATOS_COLUMNAS(5)|| ''',
                                        ''' ||  UN_TERCERO          || ''',
                                        ''' ||  MI_SUCURSAL         || ''',
                                        ''' ||  MI_CENTRO           || ''',
                                        ''' ||  MI_AUXILIAR         || ''',
                                        ''' ||   CASE WHEN MI_CLASE_BANCO IN('B') THEN PCK_DATOS.CONS_FUENTE ELSE MI_FUENTE END           || ''', --JM 7749239 22/07/2024
                                        '   ||  0 || ' ,
                                        '   ||  V_VALORTEMP || ',
                                        '   || 0 || ' ,
                                        '   || V_VALORTEMP || ' ';
                                        
                          BEGIN    
                              BEGIN
                                  PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA   => 'TEMP_PLANA_AJUSTES', 
                                                                    UN_ACCION  => 'I', 
                                                                    UN_CAMPOS  => MI_CAMPOS, 
                                                                    UN_VALORES => MI_VALORES);
                                  MI_YAINSERTADO := TRUE;
                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                  RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
                              END;
                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
                                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                                  UN_EXC_COD =>SQLCODE
                                                  ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                  ,UN_REEMPLAZOS  => MI_MSGERROR);
                                      MI_YAINSERTADO := FALSE;
                          END;
                        
                    ELSE
                          MI_YAINSERTADO := TRUE; 
                    END IF;
                    -----------------------------------SE CREA EL DETALLE COMPROBANTE PPTAL MPEREZ------------------------------------
                  -- se consulta si existe el comprobante ppptal 
                      BEGIN
                      SELECT 
                              PPTAL.NUMERO, PPTAL.DESCRIPCION--, DPPTAL.CUENTA 
                          INTO 
                              V_NUMCPTEEXISTE_PPTAL, V_DESCRIPCION_PPTAL--, V_CUENTAPPTAL
                      FROM COMPROBANTE_PPTAL PPTAL JOIN DETALLE_COMPROBANTE_PPTAL DPPTAL ON (PPTAL.NUMERO = DPPTAL.COMPROBANTE)
                        WHERE PPTAL.COMPANIA = UN_COMPANIA
                        AND PPTAL.ANO       = UN_ANIO
                        AND PPTAL.TIPO     = MI_CPTERECAUDO
                        AND TO_CHAR(PPTAL.FECHA,'DD/MM/YYYY') = MI_FECHA
                        AND PPTAL.TERCERO   = UN_TERCERO
                        AND PPTAL.SUCURSAL  = MI_SUCURSAL AND ROWNUM = 1;
                    EXCEPTION WHEN NO_DATA_FOUND THEN 
                        V_NUMCPTEEXISTE_PPTAL := '';
                      END;  
                      IF(V_NUMCPTEEXISTE_PPTAL > 0)THEN 
                        SELECT COUNT(CONSECUTIVO) INTO V_CONTCONSECDETPPTAL
                        FROM DETALLE_COMPROBANTE_PPTAL 
                        WHERE COMPANIA = UN_COMPANIA 
                        AND LOTE = UN_LOTE;
                      --AND TIPO_CPTE = MI_CPTERECAUDO 
                       -- AND COMPROBANTE = V_NUMCPTEEXISTE_PPTAL
                       -- AND TRIM(DETALLE_COMPROBANTE_PPTAL.DESCRIPCION) = TRIM(MI_DATOS_COLUMNAS(5)) -- MPEREZ
                       -- AND TRIM(VALOR_CREDITO) = TRIM(TRUNC(V_VALORTEMP,0));
                        IF(V_CONTCONSECDETPPTAL = 0)THEN 
                              MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO('TEMP_PLANA_AJUSTES', 'TEMP_PLANA_AJUSTES.COMPANIA ='''
                                                      ||UN_COMPANIA||''''
                                                      ||' AND TEMP_PLANA_AJUSTES.ANO = '||UN_ANIO
                                                      ||' AND TEMP_PLANA_AJUSTES.TIPO_CPTE = '''
                                                      ||MI_CPTERECAUDO
                                                      ||''' AND TEMP_PLANA_AJUSTES.COMPROBANTE='||MI_NUMERO, 'CONSECUTIVO', '1');
                              -- se inserta el CREDITO DEL TIPO RIP en tabla PPTAL
                                  IF(MI_CTAPPTALCREDITO IS NOT NULL)THEN                              
                                      MI_CAMPOS := 'LOTE,
                                                    COMPANIA,
                                                    ANO,
                                                    TIPO_CPTE,
                                                    COMPROBANTE,
                                                    CUENTA,
                                                    CONSECUTIVO,
                                                    FECHA,
                                                    DESCRIPCION,
                                                    TERCERO,
                                                    SUCURSAL,
                                                    CENTRO_COSTO,
                                                    AUXILIAR,
                                                    FUENTE_RECURSOS,
                                                    VALOR_DEBITO,
                                                    VALOR_CREDITO
                                                    ';
                                      MI_VALORES :=''   ||  UN_LOTE             || ',
                                                    ''' ||  UN_COMPANIA         || ''',
                                                    '   ||  UN_ANIO             || ',
                                                    ''' ||  MI_CPTERECAUDO      || ''',
                                                    '   ||  MI_NUMERO || ',
                                                    ''' ||  MI_CTAPPTALCREDITO    || ''',
                                                    '   ||  MI_CONSECUTIVO      || ',
                                                    ''' ||  TO_DATE(MI_FECHA,'DD/MM/YYYY')|| ''',
                                                    ''' ||  MI_DATOS_COLUMNAS(5)|| ''',
                                                    ''' ||  UN_TERCERO          || ''',
                                                    ''' ||  MI_SUCURSAL         || ''',
                                                    ''' ||  MI_CENTRO           || ''',
                                                    ''' ||  MI_AUXILIAR         || ''',
                                                    ''' ||  MI_FUENTE           || ''',
                                                    '   ||  V_VALORTEMP || ' ,
                                                    '   ||  0 || ' ';         
                                                    
                                  BEGIN    
                                      BEGIN
                                          PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA   => 'TEMP_PLANA_AJUSTES', 
                                                                                  UN_ACCION  => 'I', 
                                                                                  UN_CAMPOS  => MI_CAMPOS, 
                                                                                  UN_VALORES => MI_VALORES);
                                          MI_YAINSERTADO := TRUE;
                                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                          RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
                                  END;                          
                                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                                              UN_EXC_COD =>SQLCODE
                                                              ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_DELDETCOMPROBANTE
                                                              ,UN_REEMPLAZOS  => MI_MSGERROR);
                                      MI_YAINSERTADO := FALSE;
                                  END; 
                                  
                              END IF;
                    ELSE
                  MI_YAINSERTADO := TRUE;
                END IF;
              ELSE 
                  MI_YAINSERTADO := TRUE;      
              END IF;                
              END IF;
            END IF;
          ELSE
            MI_YAINSERTADO := FALSE;
          END IF;
        --END IF;
            /********************************************************FIN DESARROLLO ELIMINANDO COMPROBANTES *****************************/
            -- FIN
            -- hasta aqui probe no cargaria inconsistencias.
            IF NOT MI_INCONSISTENCIA AND NOT MI_YAINSERTADO THEN
                MI_CAMPOS := 'LOTE,
                              FECHA_REPORTE, 
                              CONCEPTO, 
                              VALOR,
                              DESCRIPCION,
                              CUENTA_DEBITO,
                              CUENTA_CREDITO,
                              CUENTA_RECAUDO,
                              DESCUENTO,
                              AUXILIAR,
                              CENTRO_COSTO,
                              FUENTE_RECURSO,
                              REFERENCIA,
                              RECIBOUNICO';
    
    
                MI_VALORES := '    '||UN_LOTE||', 
                            '''||TO_DATE(MI_DATOS_COLUMNAS(1),'DD/MM/YYYY')||''',
                                '''||MI_DATOS_COLUMNAS(2)||''',
                                  '||MI_DATOS_COLUMNAS(3)||',
                                '''||MI_DATOS_COLUMNAS(5)||''',   
                                '''||MI_CUENTADEBITOBASE||''',
                                '''||MI_CUENTACREDITOBASE||''',
                                '''||MI_CUENTARECAUDO||''' ,
                                  '||MI_DESCUENTO||' ,
                                '''||MI_AUXILIAR||''' ,
                                '''||MI_CENTRO||''' ,
                                '''||MI_FUENTE||''' ,
                                '''||MI_REFERENCIA||''',
                                '''||  MI_DATOS_COLUMNAS(6)||''' ';                            
    
                BEGIN    
                  BEGIN
                        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'TEMP_REPORTERECAUDO'
                                                    ,UN_ACCION  => 'I'
                                                    ,UN_CAMPOS  => MI_CAMPOS
                                                    ,UN_VALORES => MI_VALORES);  
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;                                   
                  END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                              ,UN_REEMPLAZOS  => MI_MSGERROR);
                END;                                      
           END IF; 
       -- ELSE
           -- MI_YAINSERTADO := TRUE;
           -- MI_RETORNO := MI_RETORNO|| 'El registro con recibo  '||MI_DATOS_COLUMNAS(6)||' ha sido procesado anteriormente'|| CHR(13) || CHR(10);
         END IF;
     END IF;
  END LOOP INSERTAR_TEMPORAL;
                
             END IF;
         END IF;
 END IF;
END LOOP RECORRER_POR_FECHAS; 


  


  IF NOT MI_INCONSISTENCIA  THEN

   /*MI_NUMERO := PCK_CONTABILIDAD_CPTE.FC_ENUMERARCOMPROBANTECNT(UN_COMPANIA     => UN_COMPANIA
                                                               ,UN_ANIO         => UN_ANIO
                                                               ,UN_TIPO         => UN_TIPOCOBRO
                                                               ,UN_NUMERO       => 0 
                                                               ,UN_CENTRO_COSTO => '');

   MI_NUMERORECAUDO := PCK_CONTABILIDAD_CPTE.FC_ENUMERARCOMPROBANTECNT(UN_COMPANIA     => UN_COMPANIA
                                                                               ,UN_ANIO         => UN_ANIO
                                                                               ,UN_TIPO         => MI_CPTERECAUDO
                                                                               ,UN_NUMERO       => 0
                                                                               ,UN_CENTRO_COSTO => '');*/ 


    FOR MI_RS IN (SELECT LOTE,
                        FECHA_REPORTE, 
                         CONCEPTO, 
                         VALOR,
                         DESCRIPCION,
                         CUENTA_DEBITO,
                         CUENTA_CREDITO,
                         CUENTA_RECAUDO,
                         DESCUENTO,
                         AUXILIAR,
                         CENTRO_COSTO,
                         REFERENCIA,
                         FUENTE_RECURSO,
                         RECIBOUNICO
                  FROM TEMP_REPORTERECAUDO
                  ORDER BY FECHA_REPORTE)LOOP
    --||'00'
    --TO_CHAR(MI_RS.FECHA_REPORTE,'YYYY')
      SELECT TO_CHAR(RPAD(UN_LOTE,4,0))||TO_CHAR(MI_RS.FECHA_REPORTE,'YY')||TO_CHAR(MI_RS.FECHA_REPORTE,'MM')||TO_CHAR(MI_RS.FECHA_REPORTE,'DD') INTO MI_NUMERO FROM DUAL;
      --SELECT TO_CHAR(MI_RS.FECHA_REPORTE,'YYYY')||TO_CHAR(MI_RS.FECHA_REPORTE,'MM')||'00'||TO_CHAR(MI_RS.FECHA_REPORTE,'DD') INTO MI_NUMERO FROM DUAL; 
      MI_NUMERORECAUDO := MI_NUMERO;
      MI_FECHACOMPROBANTE := MI_RS.FECHA_REPORTE;

    --Validar por fecha
     IF MI_FECHACOMPROBANTE <> MI_FECHAVALIDACION THEN

            --MI_NUMERO := MI_NUMERO + 1;

            --MI_NUMERORECAUDO := MI_NUMERORECAUDO + 1;

            MI_RETORNOCAUSACION := MI_RETORNOCAUSACION ||' '|| MI_NUMERO||',';
            MI_RETORNORECAUDO :=MI_RETORNORECAUDO||' '|| MI_NUMERORECAUDO||',';

            MI_CONSECUTIVO := 0;
            MI_CONSECUTIVORECAUDO := 0;

    END IF;

       IF MI_CLASECONTABLE IN ('V')THEN          
              MI_DEBITO   := MI_RS.CUENTA_DEBITO;
              MI_CREDITO  := MI_RS.CUENTA_CREDITO;        
       ELSE  
              MI_CREDITO  := MI_RS.CUENTA_DEBITO;
              MI_DEBITO   := MI_RS.CUENTA_CREDITO;        

       END IF;
      /*Se obtiene la cuenta presupuestal para la cuenta contable débito - LJDIAZ TOMADO DE MPEREZ*/
        BEGIN
            SELECT RUBRO
              INTO MI_CTAPPTALDEBITO
              FROM PLAN_PPTAL_CUENTACNT          
             WHERE COMPANIA = UN_COMPANIA
               AND ANO      = UN_ANIO
               AND CUENTA_CONTABLE    = MI_DEBITO;
        EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_CTAPPTALDEBITO := '';
        END;

        /*Se obtiene la cuenta presupuestal para la cuenta contable crédito - LJDIAZ TOMADO DE MPEREZ*/              
        BEGIN
            SELECT RUBRO
              INTO MI_CTAPPTALCREDITO
              FROM PLAN_PPTAL_CUENTACNT          
             WHERE COMPANIA = UN_COMPANIA
               AND ANO      = UN_ANIO
               AND CUENTA_CONTABLE    = MI_CREDITO;
        EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_CTAPPTALCREDITO := '';
        END;

     --CAUSACION
     IF MI_RS.DESCUENTO IN (0) THEN       

        --DEBITO_CAUSACION

       IF MI_DEBITO IS NOT NULL THEN

             MI_CTAPPTAL := '';
             MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CREDITO);         

             MI_CONSECUTIVO := MI_CONSECUTIVO + 1; 

                MI_CAMPOS := 'LOTE,
                            COMPANIA
                            , ANO
                            , TIPO_CPTE
                            , COMPROBANTE
                            , CONSECUTIVO
                            , CUENTA
                            , FECHA
                            , NATURALEZA                            
                            , VALOR_DEBITO
                            , VALOR_CREDITO
                            , EJECUCION_DEBITO
                            , EJECUCION_CREDITO
                            , CENTRO_COSTO
                            , TERCERO
                            , SUCURSAL
                            , AUXILIAR
                            , CUENTAPPTAL      
                            , DESCRIPCION
                            , BASE_GRAVABLE
                            , REFERENCIA
                            , FUENTE_RECURSOS';

              MI_VALORES := ' '||MI_RS.LOTE||',
                           '''||UN_COMPANIA||'''
                          ,'||UN_ANIO||'
                          ,'''||UN_TIPOCOBRO||'''
                          ,'||MI_NUMERO||'
                          ,'||MI_CONSECUTIVO||'
                          ,'''||MI_DEBITO||'''
                          ,'''||MI_FECHACOMPROBANTE||'''
                          ,'''||MI_NATURALEZA||'''                          
                          ,'||MI_RS.VALOR||'
                          ,0
                          ,0
                          ,0
                          ,'''||MI_RS.CENTRO_COSTO||'''
                          ,'''||UN_TERCERO||'''
                          ,'''||MI_SUCURSAL||'''
                          ,'''||MI_RS.AUXILIAR||'''
                          ,'''||MI_CTAPPTAL||'''  
                          ,'''||MI_RS.DESCRIPCION||'''
                          ,0
                          ,'''||MI_RS.REFERENCIA||'''
                          ,'''||MI_RS.FUENTE_RECURSO||'''';

          BEGIN    
            BEGIN

                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'TEMP_PLANA_AJUSTES'
                                              ,UN_ACCION  => 'I'
                                              ,UN_CAMPOS  => MI_CAMPOS
                                              ,UN_VALORES => MI_VALORES);    

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;                                   

             END;

             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
               MI_MSGERROR(1).CLAVE := 'NUMERO';
               MI_MSGERROR(1).VALOR := MI_NUMERO;
               MI_MSGERROR(2).CLAVE := 'TIPO';
               MI_MSGERROR(2).VALOR := UN_TIPOCOBRO;

                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                        ,UN_REEMPLAZOS  => MI_MSGERROR);

          END;                                      

          MI_CONSC := TRUE;
            -- LJDIAZ 29/06/2023
    
       ELSE
         MI_RPTA := 0;

      END IF;

          -- CREDITO CAUSACION
       IF MI_CREDITO IS NOT NULL THEN

        BEGIN
             SELECT CUENTA_CONTABLE
              INTO MI_CTAPPTAL
            FROM PLAN_PPTAL_CUENTACNT          
            WHERE COMPANIA = UN_COMPANIA
              AND ANO      = UN_ANIO
              AND RUBRO    = MI_CREDITO;

         EXCEPTION WHEN NO_DATA_FOUND THEN  
          MI_CTAPPTAL := NULL; 
        END;


             MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CREDITO);
     
        
             IF MI_CONSC THEN
                MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
                MI_CONSC := FALSE;
             END IF ;


                MI_CAMPOS := 'LOTE,
                            COMPANIA
                            , ANO
                            , TIPO_CPTE
                            , COMPROBANTE
                            , CONSECUTIVO
                            , CUENTA
                            , FECHA
                            , NATURALEZA                            
                            , VALOR_DEBITO
                            , VALOR_CREDITO
                            , EJECUCION_DEBITO
                            , EJECUCION_CREDITO
                            , CENTRO_COSTO
                            , TERCERO
                            , SUCURSAL
                            , AUXILIAR
                            , CUENTAPPTAL      
                            , DESCRIPCION
                            , BASE_GRAVABLE
                            , REFERENCIA 
                            , FUENTE_RECURSOS';

                MI_VALORES := ' '||MI_RS.LOTE||',
                             '''||UN_COMPANIA||'''
                             , '||UN_ANIO||'
                             ,'''||UN_TIPOCOBRO||'''
                             ,'||MI_NUMERO||'
                             ,'||MI_CONSECUTIVO||'
                             ,'''||MI_CREDITO||'''
                             ,'''||MI_FECHACOMPROBANTE||'''
                             ,'''||MI_NATURALEZA||'''                             
                             ,0
                             ,'||MI_RS.VALOR||'
                             ,0
                             ,0
                             ,'''||MI_RS.CENTRO_COSTO||'''
                             ,'''||UN_TERCERO||'''
                             ,'''||MI_SUCURSAL||'''
                             ,'''||MI_RS.AUXILIAR||'''
                             ,''''                         
                             ,'''||MI_RS.DESCRIPCION||'''
                             ,0
                             ,'''||MI_RS.REFERENCIA||'''
                             ,'''||MI_RS.FUENTE_RECURSO||'''';          


          BEGIN    
            BEGIN

                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'TEMP_PLANA_AJUSTES'
                                              ,UN_ACCION  => 'I'
                                              ,UN_CAMPOS  => MI_CAMPOS
                                              ,UN_VALORES => MI_VALORES);    

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;                                   

             END;

             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
               MI_MSGERROR(1).CLAVE := 'NUMERO';
               MI_MSGERROR(1).VALOR := MI_NUMERO;
               MI_MSGERROR(2).CLAVE := 'TIPO';
               MI_MSGERROR(2).VALOR := UN_TIPOCOBRO;

                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                        ,UN_REEMPLAZOS  => MI_MSGERROR);

          END;            

            MI_CONSC := TRUE;
             -- LJDIAZ 29/06/2023

         ELSE

          MI_RPTA := 0;                    

         END IF;
      END IF;   


   --GFIGUEREDO
      --Ticket 7707509, se invierte la asignacion de la cuenta si el concepto
      --no es de descuento
      IF      MI_RS.DESCUENTO IN (0) THEN
              MI_CREDITO  := MI_RS.CUENTA_DEBITO;
              MI_DEBITO   := MI_RS.CUENTA_RECAUDO; --Ticket 7709556 gfigueredo
    ELSIF     MI_RS.CUENTA_DEBITO = '110501' OR MI_RS.CUENTA_DEBITO = '1110' THEN
          MI_DEBITO   := MI_RS.CUENTA_RECAUDO;
              MI_CREDITO  := MI_RS.CUENTA_CREDITO;   
      ELSE
        MI_DEBITO   := MI_RS.CUENTA_DEBITO;
              MI_CREDITO  := MI_RS.CUENTA_RECAUDO;   
    END IF;
        --DEBITO RECAUDO        

             MI_CONSECUTIVORECAUDO := MI_CONSECUTIVORECAUDO + 1; 

                BEGIN
                     SELECT CUENTA_CONTABLE
                      INTO MI_CTAPPTAL
                    FROM PLAN_PPTAL_CUENTACNT          
                    WHERE COMPANIA = UN_COMPANIA
                      AND ANO      = UN_ANIO
                      AND RUBRO    = MI_CREDITO;

                 EXCEPTION WHEN NO_DATA_FOUND THEN  
                  MI_CTAPPTAL := NULL; 
                END;


                MI_CAMPOS := 'LOTE,
                             COMPANIA
                            , ANO
                            , TIPO_CPTE
                            , COMPROBANTE
                            , CONSECUTIVO
                            , CUENTA
                            , FECHA
                            , NATURALEZA                            
                            , VALOR_DEBITO
                            , VALOR_CREDITO
                            , EJECUCION_DEBITO
                            , EJECUCION_CREDITO
                            , CENTRO_COSTO
                            , TERCERO
                            , SUCURSAL
                            , AUXILIAR
                            , CUENTAPPTAL      
                            , DESCRIPCION
                            , BASE_GRAVABLE
                            , REFERENCIA
                            , FUENTE_RECURSOS';

              MI_VALORES := ''||MI_RS.LOTE||',
                            '''||UN_COMPANIA||'''
                          ,'||UN_ANIO||'
                          ,'''||MI_CPTERECAUDO||'''
                          ,'||MI_NUMERORECAUDO||'
                          ,'||MI_CONSECUTIVORECAUDO||'
                          ,'''||MI_DEBITO||'''
                          ,'''||MI_FECHACOMPROBANTE||'''
                          ,''C''                          
                          ,'||MI_RS.VALOR||'
                          ,0
                          ,0
                          ,0
                          ,'''||MI_RS.CENTRO_COSTO||'''
                          ,'''||UN_TERCERO||'''
                          ,'''||MI_SUCURSAL||'''
                          ,'''||MI_RS.AUXILIAR||'''
                          ,'''||MI_CTAPPTAL||'''  
                          ,'''||MI_RS.DESCRIPCION||'''
                          ,0
                          ,'''||MI_RS.REFERENCIA||'''
                          ,'''||MI_RS.FUENTE_RECURSO||'''';

          BEGIN    
            BEGIN

                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'TEMP_PLANA_AJUSTES'
                                              ,UN_ACCION  => 'I'
                                              ,UN_CAMPOS  => MI_CAMPOS
                                              ,UN_VALORES => MI_VALORES);    

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;                                   

             END;

             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
               MI_MSGERROR(1).CLAVE := 'NUMERO';
               MI_MSGERROR(1).VALOR := MI_NUMERO;
               MI_MSGERROR(2).CLAVE := 'TIPO';
               MI_MSGERROR(2).VALOR := UN_TIPOCOBRO;

                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                        ,UN_REEMPLAZOS  => MI_MSGERROR);

          END;        
            -- LJDIAZ 29/06/2023
 
        -- CREDITO RECUADO  

             MI_CONSECUTIVORECAUDO := MI_CONSECUTIVORECAUDO + 1; 

                    BEGIN
                         SELECT CUENTA_CONTABLE
                          INTO MI_CTAPPTAL
                        FROM PLAN_PPTAL_CUENTACNT          
                        WHERE COMPANIA = UN_COMPANIA
                          AND ANO      = UN_ANIO
              AND RUBRO    = MI_RS.CUENTA_DEBITO;
                          --AND RUBRO    = MI_DEBITO;--GFIGUEREDO, Ticket 7707509, se asigna el valor de la variable MI_DEBITO

                     EXCEPTION WHEN NO_DATA_FOUND THEN  
                      MI_CTAPPTAL := NULL; 
                    END;


                MI_CAMPOS := 'LOTE
                            ,COMPANIA
                            , ANO
                            , TIPO_CPTE
                            , COMPROBANTE
                            , CONSECUTIVO
                            , CUENTA
                            , FECHA
                            , NATURALEZA                            
                            , VALOR_DEBITO
                            , VALOR_CREDITO
                            , EJECUCION_DEBITO
                            , EJECUCION_CREDITO
                            , CENTRO_COSTO
                            , TERCERO
                            , SUCURSAL
                            , AUXILIAR      
                            , CUENTAPPTAL    
                            , DESCRIPCION
                            , BASE_GRAVABLE
                            , REFERENCIA
                            , FUENTE_RECURSOS';

              MI_VALORES := ''||MI_RS.LOTE||',
                            '''||UN_COMPANIA||'''
                          ,'||UN_ANIO||'
                          ,'''||MI_CPTERECAUDO||'''
                          ,'||MI_NUMERORECAUDO||'
                          ,'||MI_CONSECUTIVORECAUDO||'
                          ,'''||MI_CREDITO||'''
                          ,'''||MI_FECHACOMPROBANTE||'''
                          ,''D''      
                          ,0
                          ,'||MI_RS.VALOR||'
                          ,0
                          ,0
                          ,'''||MI_RS.CENTRO_COSTO||'''
                          ,'''||UN_TERCERO||'''
                          ,'''||MI_SUCURSAL||'''
                          ,'''||MI_RS.AUXILIAR||'''  
                          ,'''||MI_CTAPPTAL||'''  
                          ,'''||MI_RS.DESCRIPCION||'''
                          ,0
                          ,'''||MI_RS.REFERENCIA||'''
                          ,'''||MI_RS.FUENTE_RECURSO||'''';

          BEGIN    
            BEGIN

                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'TEMP_PLANA_AJUSTES'
                                              ,UN_ACCION  => 'I'
                                              ,UN_CAMPOS  => MI_CAMPOS
                                              ,UN_VALORES => MI_VALORES);    

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;                                   

             END;

             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
               MI_MSGERROR(1).CLAVE := 'NUMERO';
               MI_MSGERROR(1).VALOR := MI_NUMERO;
               MI_MSGERROR(2).CLAVE := 'TIPO';
               MI_MSGERROR(2).VALOR := UN_TIPOCOBRO;

                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                        ,UN_REEMPLAZOS  => MI_MSGERROR);

          END;             

        MI_FECHAVALIDACION := MI_FECHACOMPROBANTE; 
            -- LJDIAZ 29/06/2023

    END LOOP;              

    --Creacion de Comprobantes 
    FOR MI_RS IN (SELECT DISTINCT TIPO_CPTE,
                         COMPROBANTE,FECHA
                 FROM TEMP_PLANA_AJUSTES) LOOP  
                 --,FECHA, DESCRIPCION
        MI_CINTADOR_VUELTAS := MI_CINTADOR_VUELTAS + 1;
        MI_CONTADOR_TIPOCPT := '  '||MI_CONTADOR_TIPOCPT||' COMPROBANTE->'||MI_RS.COMPROBANTE||'  TIPO-->'||MI_RS.TIPO_CPTE||'  VUELTAS ->'||MI_CINTADOR_VUELTAS;
         MI_RTAINTERFAZ := PCK_CONTABILIZAR.FC_CONTABILIZAR( UN_COMPANIA         => UN_COMPANIA
                                              ,UN_TIPOCOMPROBANTE  => MI_RS.TIPO_CPTE
                                              ,UN_NUMERO           => MI_RS.COMPROBANTE
                                              ,UN_ANO              => UN_ANIO
                                              ,UN_FECHA            => MI_RS.FECHA
                                              ,UN_TERCERO          => UN_TERCERO
                                              ,UN_SUCURSAL         => MI_SUCURSAL
                                              ,UN_DESCRIPCION      => 'Contabilizar'                                        
                                              ,UN_SIMPLE           => -1
                                              ,UN_INDIMPRESION     => 0                                            
                                              ,UN_INTOMITIRPPTAL   => -1
                                              ,UN_CONCILIAR        => 0
                                              ,UN_PLANO            => 0
                                              ,UN_NONETEA          => -1
                                              ,UN_CONTRATISTA      => 0
                                              ,UN_TEXTO            => ''
                                              ,UN_CONTRATO         => 0
                                              ,UN_TIPOCONTRATO     => ''
                                              ,UN_NRO_DOCUMENTO    => ''
                                              ,UN_ALMDEP           => 0
                                              ,UN_TERCE            => 1
                                              ,UN_RESAUXGEN        => 0
                                              ,UN_USUARIO          => UN_USUARIO); 

         IF MI_CPTERECAUDO = MI_RS.TIPO_CPTE THEN

          PCK_FACT_GENERAL_COM3.PR_CREARCOMPROBANTEPPTAL(UN_COMPANIA   => UN_COMPANIA,
                                                    UN_TIPOCPTE        => MI_RS.TIPO_CPTE,
                                                    UN_ANIO            => UN_ANIO,
                                                    UN_NUMERO          => MI_RS.COMPROBANTE,
                                                    UN_NUMERO_AFEC     => '',
                                                    UN_TIPO_PTE_AFEC   => '',
                                                    UN_DESCRIPCION     => 'MI_RS.DESCRIPCION',
                                                    UN_USUARIO         => UN_USUARIO,
                                                    UN_VALORRECAUDO    => 0)     ;

         END IF;                                                 


    END LOOP;
        IF(MI_RETORNOCAUSACION IS NOT NULL AND MI_RETORNORECAUDO IS NOT NULL)THEN
            MI_RETORNO := MI_RETORNO || ' Se insertaron '||MI_CONTADOR||' registros '||chr(10)
            ||'Se crearon los comprobantes de causacion '||UN_TIPOCOBRO||' '||LPAD(MI_RETORNOCAUSACION,LENGTH(MI_RETORNOCAUSACION)-1)||chr(10)
            ||'Se crearon los comprobantes de recaudo '||MI_CPTERECAUDO||' '||LPAD(MI_RETORNORECAUDO,LENGTH(MI_RETORNORECAUDO)-1); 
        ELSE
            IF(V_COMPROBANTESNOACTUALZIADOS IS NOT NULL) THEN
                SELECT INSTR(MI_RETORNO, V_COMPROBANTESNOACTUALZIADOS) INTO V_MIRETORNOEXISTE_CNT_AUX FROM DUAL;
                 IF(V_MIRETORNOEXISTE_CNT_AUX = 0 OR V_MIRETORNOEXISTE_CNT_AUX IS NULL)THEN
                    MI_RETORNO := MI_RETORNO || ' No se insertaron registros nuevos, los registros ya pertenecen a los comprobantes  : '||V_COMPROBANTESNOACTUALZIADOS;
                END IF;
            END IF;
            IF(V_COMPROBANTESACTUALZIADOS_RIP IS NOT NULL) THEN
                SELECT INSTR(MI_RETORNO, V_COMPROBANTESACTUALZIADOS_RIP) INTO V_MIRETORNOEXISTE_CNT_AUX FROM DUAL;
                 IF(V_MIRETORNOEXISTE_CNT_AUX = 0 OR V_MIRETORNOEXISTE_CNT_AUX IS NULL)THEN
                    MI_RETORNO := MI_RETORNO||' No se insertaron registros nuevos, los registros ya pertenecen a los comprobantes : '||V_COMPROBANTESACTUALZIADOS_RIP;
                END IF;
            END IF;
            V_COMPROBANTESACTUALZIADOS := NULL;
            V_COMPROBANTESACTUALZIADOS_RIP := NULL;
        END IF;
        IF(V_COMPROBANTESACTUALZIADOS IS NOT NULL)THEN
            MI_RETORNO := MI_RETORNO||' Se actualizaron los comprobantes  : '||V_COMPROBANTESACTUALZIADOS;
        END IF;
        IF(V_COMPROBANTESACTUALZIADOS_RIP IS NOT NULL) THEN
            MI_RETORNO := MI_RETORNO||' Se actualizaron los comprobantes  : '||V_COMPROBANTESACTUALZIADOS_RIP;
        END IF;
        
                            
   ELSE

    MI_RETORNO := 'Las siguientes inconsistencias se presentan en el archivo que se está tratando de subir a Sysman:' || CHR(13) || CHR(10)
            ||MI_RETORNO; 

    --ROLLBACK;
    RAISE_APPLICATION_ERROR(-20111, SUBSTR(MI_RETORNO,1,2050),FALSE);

  END IF; 

 RETURN MI_RETORNO; --||'-----'||MI_CONTADOR_TIPOCPT;

END FC_CARGAR_RECUADOCAUSACION;

FUNCTION FC_TASAANUAL
/*
    NAME              : FC_TASAANUAL
    AUTHOR MIGRACION  : SANDRA MILENA DAZA LEGUIZAMON
    DATE MIGRADOR     : 30/08/2021                                                                                              
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : FUNCION QUE RETORNA LA TASA CONFIGURADA EN LA TABLA SF_TASAS_INTERES

    @NAME:    retonarTasaAnual
    @METHOD:  GET
    */
(
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO         IN PCK_SUBTIPOS.TI_ANIO, 
    UN_TIPOCOBRO    IN PCK_SUBTIPOS.TI_TIPO_COBRO,
    UN_FECHACORTE   IN DATE
)RETURN NUMBER
AS 
    MI_TASA           NUMBER := 0;
    MI_TIPOTASA       VARCHAR2(1 CHAR);
BEGIN

    BEGIN
        SELECT  TASAINTERES
        INTO    MI_TASA
        FROM    SF_TASAS_INTERES
        WHERE   COMPANIA                = UN_COMPANIA
        AND     ANO                     = UN_ANIO
        AND     TIPOCOBRO               = UN_TIPOCOBRO
        AND     UN_FECHACORTE  BETWEEN TO_DATE(TO_CHAR(FECHA_INICIAL, 'DD/MM/YYYY')) AND TO_DATE(TO_CHAR(FECHA_FINAL, 'DD/MM/YYYY'));
        
        EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_TASA :=0;
                  WHEN TOO_MANY_ROWS THEN
                    RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                  WHEN OTHERS THEN
                    MI_TASA :=0;
        
        
    END ;
    IF MI_TASA > 1 THEN
        MI_TASA := MI_TASA/100;
    END IF;
    RETURN MI_TASA;
    
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
           PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD =>SQLCODE
                        ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_VARIASTASAANUAL);
END FC_TASAANUAL;    


FUNCTION FC_TASAMENSUAL
/*
    NAME              : FC_TASAMENSUAL
    AUTHOR MIGRACION  : SANDRA MILENA DAZA LEGUIZAMON
    DATE MIGRADOR     : 16/09/2021                                                                                              
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : FUNCION QUE RETORNA LA TASA  CONFIGURADA EN LA TABLA SF_TASAS_INTERES, DEPENDIENDO LA CONFIGURACION
                        DEL PARAMETRO SF TIPO TASA INTERES NOMINAL(N)/ EFECTIVA(E) SE CALCULA LA TASA MENSUAL A RETORNAR

    @NAME:    retonarTasaMensual
    @METHOD:  GET
    */
(
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO         IN PCK_SUBTIPOS.TI_ANIO, 
    UN_TIPOCOBRO    IN PCK_SUBTIPOS.TI_TIPO_COBRO,
    UN_FECHACORTE   IN DATE
)RETURN NUMBER
AS 
    MI_TASA           NUMBER:=0;
    MI_TIPOTASA       VARCHAR2(1 CHAR);
BEGIN
    -- Consultar la tasa configurada para el mes ingresado por parámetro
    MI_TIPOTASA  := PCK_SYSMAN_UTL.FC_VALIDARPARAMETRO
                             (UN_COMPANIA  => UN_COMPANIA
                             ,UN_PARAMETRO => 'SF TIPO TASA INTERES NOMINAL(N)/ EFECTIVA(E)'
                             ,UN_MODULO    => PCK_DATOS.MODULOFACTURACIONGENERAL ); 
    
    BEGIN
        SELECT  TASAINTERES
        INTO    MI_TASA
        FROM    SF_TASAS_INTERES
        WHERE   COMPANIA                = UN_COMPANIA
        AND     ANO                     = UN_ANIO
        AND     TIPOCOBRO               = UN_TIPOCOBRO
        AND     UN_FECHACORTE  BETWEEN TO_DATE(TO_CHAR(FECHA_INICIAL, 'DD/MM/YYYY')) AND TO_DATE(TO_CHAR(FECHA_FINAL, 'DD/MM/YYYY'));
        
        EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_TASA :=0;
                  WHEN TOO_MANY_ROWS THEN
                    RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                  WHEN OTHERS THEN
                    MI_TASA :=0; 
        
        
    END ;
    
     BEGIN            
            IF  MI_TASA <> 0 THEN
                IF MI_TASA > 1 THEN
                    MI_TASA := MI_TASA/100;
                END IF;
                IF MI_TIPOTASA = 'N' THEN
                    MI_TASA := MI_TASA/12;
                ELSIF MI_TIPOTASA = 'E' THEN
                    MI_TASA :=  POWER((1+MI_TASA) , (1/12))-1;
                ELSE
                    RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
                END IF;        
            END IF;
            
           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                            PCK_ERR_MSG.RAISE_WITH_MSG(
                                 UN_EXC_COD =>SQLCODE
                                ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_PARTIPOTASAERR);
        END;
    RETURN MI_TASA;
    
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
           PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD =>SQLCODE
                        ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_VARIASTASAANUAL);
END FC_TASAMENSUAL;    

FUNCTION FC_TASADIARIA
/*
    NAME              : FC_TASADIARIA
    AUTHOR MIGRACION  : SANDRA MILENA DAZA LEGUIZAMON
    DATE MIGRADOR     : 16/09/2021                                                                                              
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : FUNCION QUE RETORNA LA TASA CONFIGURADA EN LA TABLA SF_TASAS_INTERES, DEPENDIENDO LA CONFIGURACION
                        DEL PARAMETRO SF TIPO TASA INTERES NOMINAL(N)/ EFECTIVA(E) SE CALCULA LA TASA DIARIA A RETORNAR

    @NAME:    retonarTasaDiaria
    @METHOD:  GET
    */
(
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO         IN PCK_SUBTIPOS.TI_ANIO, 
    UN_TIPOCOBRO    IN PCK_SUBTIPOS.TI_TIPO_COBRO,
    UN_FECHACORTE   IN DATE
)RETURN NUMBER
AS 
    MI_TASA           NUMBER:=0;
    MI_TIPOTASA       VARCHAR2(1 CHAR);
    MI_BISIESTO       NUMBER:=0;
BEGIN
    -- Consultar la tasa configurada para el mes ingresado por parámetro
    MI_TIPOTASA  := PCK_SYSMAN_UTL.FC_VALIDARPARAMETRO
                             (UN_COMPANIA  => UN_COMPANIA
                             ,UN_PARAMETRO => 'SF TIPO TASA INTERES NOMINAL(N)/ EFECTIVA(E)'
                             ,UN_MODULO    => PCK_DATOS.MODULOFACTURACIONGENERAL ); 
    
    BEGIN
        SELECT  TASAINTERES
        INTO    MI_TASA
        FROM    SF_TASAS_INTERES
        WHERE   COMPANIA                = UN_COMPANIA
        AND     ANO                     = UN_ANIO
        AND     TIPOCOBRO               = UN_TIPOCOBRO
        AND     UN_FECHACORTE  BETWEEN TO_DATE(TO_CHAR(FECHA_INICIAL, 'DD/MM/YYYY')) AND TO_DATE(TO_CHAR(FECHA_FINAL, 'DD/MM/YYYY'));
        
        EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_TASA :=0;
                  WHEN TOO_MANY_ROWS THEN
                    RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
                  WHEN OTHERS THEN
                    MI_TASA :=0;  
    END ;
    
    --7744576: Se agrega proceso para determinar si el año es bisiesto, en caso que si, la tasa se calcula dividiendo en 366
    BEGIN
      IF (UN_ANIO MOD 4 = 0) THEN
          MI_BISIESTO := 1;
      ELSE
          MI_BISIESTO := 0;
      END IF;
    END;

    BEGIN
        IF  MI_TASA <> 0 THEN
            IF MI_TASA > 1 THEN
                MI_TASA := MI_TASA/100;
            END IF;
            IF MI_TIPOTASA = 'N' THEN
                IF MI_BISIESTO = 0 THEN
                    MI_TASA := MI_TASA/365;
                ELSE
                    MI_TASA := MI_TASA/366;
                END IF;
            ELSIF MI_TIPOTASA = 'E' THEN
                MI_TASA :=  POWER((1+MI_TASA) , (1/360))-1;
            ELSE
                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
            END IF;
        END IF;
        
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                            PCK_ERR_MSG.RAISE_WITH_MSG(
                                 UN_EXC_COD =>SQLCODE
                                ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_PARTIPOTASAERR);
    END ;
    RETURN MI_TASA;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
           PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD =>SQLCODE
                        ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_VARIASTASAANUAL);
END FC_TASADIARIA;




--25
FUNCTION FC_FACTURAR_CONCEPTOSLIQUIDA
/*
      NAME              : PR_FACTURAR_CONCEPTOSLIQUIDA 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : CAMILO ANDRES PEREZ DUEÑAS
      DATE MIGRADOR     : 12/12/2022
      TIME              : 08:47 AM
      SOURCE MODULE     : SysmanSF
      DESCRIPTION       : Realiza el proceso de facturación recibiendo los siguientes parámetros
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 

    @NAME:   FC_FACTURAR_CONCEPTOSLIQUIDA
    @METHOD: GET
*/
(
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO         IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPOFACTURA  IN SF_FACTURA.TIPO_FACTURA%TYPE,
    UN_TERCERO      IN PCK_SUBTIPOS.TI_TERCERO,
    UN_CONCEPTO     IN VARCHAR2,
    UN_DESCRIPCION  IN SF_FACTURA.OBSERVACIONES%TYPE,
    UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO

)RETURN VARCHAR2
  AS
  MI_RETORNO             VARCHAR2(2000);
  MI_SUCURSAL            SF_DETALLE_FACTURA.SUCURSAL%TYPE;
  MI_CONCEPTOS           PCK_SYSMAN_UTL.T_SPLIT;
  MI_CONCEPTO            SF_CONCEPTOS.CODIGO%TYPE;
  MI_CONSECUTIVO         PCK_SUBTIPOS.TI_ENTERO_LARGO := 0;
  MI_INICIAL             PCK_SUBTIPOS.TI_ENTERO_LARGO := 1;
  MI_CONDICION           PCK_SUBTIPOS.TI_CONDICION;
  MI_CODIGO_COBRO        SF_FACTURA.CODIGO_COBRO%TYPE; 
  MI_REF_FACTURACION     SF_OBJETO_COBRO.REF_FACTURACION%TYPE;
  MI_TIPOCOBRO           SF_CONTRATOS.TIPO%TYPE;
  MI_INDPRELIQUIDACION   NUMBER(1,0);
  MI_TABLA               PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS              PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES             PCK_SUBTIPOS.TI_VALORES;
  MI_MSGERROR            PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_FECHAVENCIMIENTO    SF_OBJETO_COBRO.FECHA_VENCIMIENTO%TYPE;
  MI_NOMBRETERCERO       VARCHAR2(2000);

  MI_ANO                 SF_OBJETO_COBRO.ANO%TYPE;
  MI_TIPOFACTURA         SF_OBJETO_COBRO.TIPOFACTURA%TYPE;
  MI_NRO_FACTURA         SF_OBJETO_COBRO.NRO_FACTURA%TYPE;
  MI_FECHA_VENCIMIENTO   SF_OBJETO_COBRO.FECHA_VENCIMIENTO%TYPE;
  MI_VALOR_TOTAL         SF_OBJETO_COBRO.VALOR_TOTAL%TYPE;
  MI_TERCERO             SF_OBJETO_COBRO.TERCERO%TYPE;
  MI_TIPOFACTURA_RECAUDO SF_TIPO_COBRO.TIPOFACTURA_RECAUDO%TYPE;
  MI_REFERNCIA           VARCHAR2(24);
  MI_PARAMETRO           VARCHAR2(3 CHAR); -- JM CC 2911
BEGIN  

  --JM INI CC 2911
    MI_PARAMETRO :=NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
    UN_NOMBRE     => 'SF MANEJA REFERENCIA DE LIQUIDACION POR API SERVICIO CON CODIGO DE COBRO',
    UN_MODULO     => PCK_DATOS.FC_MODULOFACTURACIONGENERAL(),
    UN_FECHA_PAR  => SYSDATE,
    UN_IND_MAYUS  => 0),'NO');
  --JM FIN CC 2911
  
  --Se consulta el sucursal del tercero
  SELECT SUCURSAL, (NOMBRE1 || NOMBRE2 || APELLIDO1 || APELLIDO2) NOMBRES
  INTO  MI_SUCURSAL,  MI_NOMBRETERCERO  
  FROM TERCERO 
  WHERE COMPANIA = UN_COMPANIA
    AND NIT = UN_TERCERO
  GROUP BY SUCURSAL,(NOMBRE1 || NOMBRE2 || APELLIDO1 || APELLIDO2);


  MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA ||''' 
               AND TIPOCOBRO = ''' || UN_TIPOFACTURA ||''' 
               AND SUBSTR(CODIGO_COBRO,1,4) = ' || UN_ANIO ||'   ';
  MI_INICIAL := UN_ANIO || '000001';
  MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO (UN_TABLA             => 'SF_OBJETO_COBRO'
                                                     , UN_CRITERIO         => MI_CONDICION
                                                     , UN_CAMPO            => 'CODIGO_COBRO'
                                                     , UN_INICIAL          => MI_INICIAL);
  MI_CODIGO_COBRO:=MI_CONSECUTIVO;
  MI_REF_FACTURACION:= UN_TIPOFACTURA;
  MI_TIPOCOBRO:= UN_TIPOFACTURA;
  

  MI_CAMPOS    :=   'COMPANIA
                    ,ANO
                    ,TIPOCOBRO
                    ,CODIGO_COBRO
                    ,TERCERO
                    ,SUCURSAL
                    ,CONTRATO
                    ,TIPOCONTRATO
                    ,FECHA_SOLICITUD
                    ,FECHA_VENCIMIENTO
                    ,OBSERVACIONES
                    ,CREATED_BY
                    ,DATE_CREATED
                    ,NIT_TERCEROFACTURADO
                    ,SUCURSAL_TERCEROFACTURADO
                    ,NOMBRE_TERCEROFACTURADO
                    ,TIPO_MEDIOPAGO
                    ,TIPO_PAGO';                        

  MI_VALORES := ''''||UN_COMPANIA||''',
                            '|| UN_ANIO ||',
                          '''|| MI_TIPOCOBRO ||''',
                            '|| MI_CODIGO_COBRO ||',
                          '''|| UN_TERCERO  ||''',
                          '''|| MI_SUCURSAL ||''',
                             null ,
                             null ,  
                             SYSDATE,
                             null,
                          '''||UN_DESCRIPCION||''',
                          '''||UN_USUARIO||''',
                             SYSDATE,
                            999999999999999999,
                            999,
                          '''|| MI_NOMBRETERCERO ||''',
                          10,
                          1
                          ';
           BEGIN
            BEGIN
               PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA => 'SF_OBJETO_COBRO',
                                                UN_ACCION    => 'I', 
                                                UN_CAMPOS    => MI_CAMPOS, 
                                                UN_VALORES => MI_VALORES);
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
               RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
            END;

               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
                 MI_MSGERROR(0).CLAVE := 'CONTRATO';
                 MI_MSGERROR(0).VALOR := MI_CODIGO_COBRO;               
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_TABLAERROR => MI_TABLA,
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INCOBRCONTR,
                                           UN_REEMPLAZOS => MI_MSGERROR);  

           END;



 --Calcular campo FECHA_VENCIMIENTO                                      

  SELECT INDPRELIQUIDACION
    INTO MI_INDPRELIQUIDACION
  FROM SF_TIPO_COBRO 
  WHERE COMPANIA = UN_COMPANIA
    AND ANO      = UN_ANIO
    AND CODIGO   = MI_TIPOCOBRO
  GROUP BY INDPRELIQUIDACION;

  MI_FECHAVENCIMIENTO :=PCK_FACT_GENERAL_COM2.FC_CALCULAR_FECHA_VENCIMIENTO  (UN_COMPANIA     => UN_COMPANIA,
                                                                              UN_TIPOCOBRO    => MI_TIPOCOBRO,
                                                                              UN_APLICA_REL   => MI_INDPRELIQUIDACION,
                                                                              UN_CODIGO_COBRO => MI_CODIGO_COBRO);
  --Actualizar FECHA_VENCIMIENTO

  MI_CAMPOS    := 'FECHA_VENCIMIENTO = '''||MI_FECHAVENCIMIENTO||''' ';

  MI_CONDICION := '   COMPANIA     = '''||UN_COMPANIA||'''
                  AND CODIGO_COBRO = '||MI_CODIGO_COBRO||'
                  AND ANO          = '||UN_ANIO||'
                  AND TIPOCOBRO    = '''||MI_TIPOCOBRO||'''';
  BEGIN
        BEGIN               
                    PCK_DATOS.GL_RTA :=PCK_DATOS.FC_ACME(UN_ACCION    =>'M',
                                                         UN_TABLA     =>'SF_OBJETO_COBRO',
                                                         UN_CAMPOS    =>MI_CAMPOS,
                                                         UN_CONDICION =>MI_CONDICION);   

                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                            RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                  END;
                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
                    MI_MSGERROR(0).CLAVE := 'CONTRATO';
                    MI_MSGERROR(0).VALOR := MI_CODIGO_COBRO;

                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,                                           
                                               UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTFECHAVCOBR,
                                               UN_REEMPLAZOS => MI_MSGERROR);  

  END;      



  MI_CONCEPTOS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CONCEPTO,
                                               UN_DELIMITADOR  => ',');

  <<REGISTROS>>
  FOR RS IN MI_CONCEPTOS.FIRST..MI_CONCEPTOS.LAST 
      LOOP
        BEGIN   
          MI_CONCEPTO := MI_CONCEPTOS(RS);
          --Insertar Detalle Objeto Cobro con los detalles a financiar

          MI_VALORES := ' SELECT  ''' || UN_COMPANIA || ''',
                                    ' || UN_ANIO || ',
                                  ''' || MI_TIPOCOBRO || ''',
                                    ' || MI_CODIGO_COBRO || ',
                                  CODIGO,
                                  SYSDATE,
                                  SYSDATE,
                                  1,
                                  VALOR_BASE,
                                  VALOR_BASE,
                                  ''' || UN_USUARIO || ''',
                                  SYSDATE
                           FROM SF_CONCEPTOS
                           WHERE COMPANIA = ''' || UN_COMPANIA || '''
                            AND TIPOCOBRO = ''' || MI_TIPOCOBRO || '''
                            AND ANO       = ' ||UN_ANIO||'
                            AND CODIGO   IN ( ''' || MI_CONCEPTO || ''' )';              

                  MI_CAMPOS := 'COMPANIA 
                                ,ANO
                                ,TIPOCOBRO
                                ,CODIGO_COBRO
                                ,CONCEPTO
                                ,FECHA_INICIO_COBRO
                                ,FECHA_FIN_COBRO 
                                ,CANTIDAD
                                ,VALOR_BASE
                                ,VALOR_NETO
                                ,CREATED_BY
                                ,DATE_CREATED';             

               BEGIN
                    BEGIN              
                     PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => 'SF_DETALLE_COBRO',
                                                           UN_ACCION   => 'IS',
                                                           UN_CAMPOS   => MI_CAMPOS,
                                                           UN_VALORES  => MI_VALORES);
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                        RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                    END;
                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN            
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,                                           
                                               UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INDECOBRCONTR,
                                               UN_REEMPLAZOS => MI_MSGERROR);  

               END;        


        END;
  END LOOP REGISTROS;

   <<LLAMAR_FACTURACION>>     
            PCK_FACT_GENERAL_COM2.PR_FACTURAR_CONCEPTOS(UN_COMPANIA    => UN_COMPANIA,
                                                      UN_TIPOCOBRO     => MI_TIPOCOBRO,
                                                      UN_CODIGO_COBRO  => MI_CODIGO_COBRO,
                                                      UN_NRO_FACTURA   => 0,  
                                                      UN_ANIO          => UN_ANIO,
                                                      UN_USUARIO       => UN_USUARIO);     
  BEGIN
    SELECT SFOC.ANO    ,SFOC.TIPOFACTURA    ,SFOC.NRO_FACTURA    ,SFOC.FECHA_VENCIMIENTO    ,SFOC.VALOR_TOTAL    ,SFOC.TERCERO    ,SFOC.SUCURSAL, SFTC.TIPOFACTURA_RECAUDO
    INTO   MI_ANO ,MI_TIPOFACTURA ,MI_NRO_FACTURA ,MI_FECHA_VENCIMIENTO ,MI_VALOR_TOTAL ,MI_TERCERO ,MI_SUCURSAL, MI_TIPOFACTURA_RECAUDO
    FROM SF_OBJETO_COBRO  SFOC INNER JOIN SF_TIPO_COBRO SFTC
    ON  SFOC.COMPANIA = SFTC.COMPANIA
    AND SFOC.ANO = SFTC.ANO
    AND SFOC.TIPOCOBRO = SFTC.CODIGO
    WHERE SFOC.COMPANIA     = UN_COMPANIA
      AND SFOC.ANO          = UN_ANIO
      AND SFOC.CODIGO_COBRO = MI_CODIGO_COBRO
      AND SFOC.TIPOCOBRO    = MI_TIPOCOBRO;
  EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_ANO := 0 ;
        MI_TIPOFACTURA := '' ;
        MI_NRO_FACTURA := '' ;
        MI_FECHA_VENCIMIENTO := '' ;
        MI_VALOR_TOTAL := 0 ;
        MI_TERCERO := '' ;
        MI_SUCURSAL:= '' ;
        MI_TIPOFACTURA_RECAUDO :=  '';
  END;
  MI_REFERNCIA := LPAD(MI_TIPOFACTURA_RECAUDO,5,'0') || LPAD( CASE WHEN MI_PARAMETRO <> 'SI' THEN MI_NRO_FACTURA ELSE MI_CODIGO_COBRO END,19,'0') ;
  MI_RETORNO := '';
  MI_RETORNO :=   MI_RETORNO  ||  'ANO'              ||  ',V,' || UN_ANIO
                     || ',R,' || 'TIPOFACTURA'       ||  ',V,' || MI_TIPOFACTURA
                     || ',R,' || 'NRO_FACTURA'       ||  ',V,' || MI_NRO_FACTURA
                     || ',R,' || 'REFERENCIA'        ||  ',V,' || MI_REFERNCIA
                     || ',R,' || 'FECHA_VENCIMIENTO' ||  ',V,' || MI_FECHA_VENCIMIENTO
                     || ',R,' || 'VALOR_TOTAL'       ||  ',V,' || MI_VALOR_TOTAL
                     || ',R,' || 'TERCERO'           ||  ',V,' || MI_TERCERO
                     || ',R,' || 'SUCURSAL'          ||  ',V,' || MI_SUCURSAL
                     || CASE WHEN MI_PARAMETRO <> 'SI' THEN 
                         '' 
                     ELSE 
                         ',R,' || 'CODIGO_COBRO'      ||  ',V,' || MI_CODIGO_COBRO END; --mod JM CC 2911
  RETURN MI_RETORNO;

END FC_FACTURAR_CONCEPTOSLIQUIDA;

END PCK_FACT_GENERAL;
