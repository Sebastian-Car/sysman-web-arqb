create or replace PACKAGE BODY PCK_FACT_GENERAL_COM3 AS

--1
FUNCTION FC_INTERFAZAR_FACTURA
 /*
    NAME              : FC_INTERFAZAR_FACTURA   ACCES => InterfazarFactura()
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
    DATE MIGRADOR     : 13/08/2018
    TIME              : 11:00 AM
    DESCRIPTION       : 
    PARAMETROS        : UN_COMPANIA  => COMPANIA DE INGRESO A LA COMPANIA 
                        UN_ANIO      => ANO DE FACTURACION 
                        UN_TIPOCOBRO => TIPO DE COBRO A FACTURAR
                        UN_CONCEPTO  => CONCEPTO A EVAUALR SI TIENE CONEXION

   @NAME: interfazarFactura
  */

(
 UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
 UN_TIPOFACTURA      IN SF_FACTURA.TIPO_FACTURA%TYPE,
 UN_NOFACTURA        IN SF_FACTURA.NUMERO_FACTURA%TYPE ,
 UN_FECHAPAGO        IN DATE,
 UN_VERMENSAJE       IN PCK_SUBTIPOS.TI_LOGICO,
 UN_MANEJAINVENTARIO IN PCK_SUBTIPOS.TI_LOGICO,
 UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN PCK_SUBTIPOS.TI_LOGICO AS 

  MI_PARFORCAUSACION            PCK_SUBTIPOS.TI_LOGICO;
  MI_SIMPLINTERFAZ              PCK_SUBTIPOS.TI_LOGICO;
  MI_STROBS                     PCK_SUBTIPOS.TI_RTA_ACME;
  MI_STRSQL                     PCK_SUBTIPOS.TI_STRSQL;   
  MI_RSTMP                      SYS_REFCURSOR;
  MI_CAMPOS                     PCK_SUBTIPOS.TI_CAMPOS;   
  MI_CONDICION                  PCK_SUBTIPOS.TI_CONDICION;    
  MI_CONTEO                     PCK_SUBTIPOS.TI_ENTERO; 
  MI_RSINTERFAZADA              PCK_SUBTIPOS.TI_LOGICO;  
  MI_RSFECHAPAGO                VARCHAR(20 CHAR);
  MI_RSBANCOPAGO                SF_FACTURA.BANCO_PAGO%TYPE;
  MI_RSOBSERVACIONES            SF_FACTURA.OBSERVACIONES%TYPE;
  MI_RSFECHAEXPEDICION          DATE;
  MI_RSCODIGOCOBRO              SF_FACTURA.CODIGO_COBRO%TYPE; 
  MI_RSTERCERO                  SF_FACTURA.TERCERO%TYPE;
  MI_RSSUCURSAL                 SF_FACTURA.SUCURSAL%TYPE;
  MI_RSTIPOCPTE                 SF_FACTURA.TIPO_CPTE%TYPE;
  MI_RSNROCPTE                  SF_FACTURA.NRO_CPTE%TYPE;
  MI_INTERFAZADA                PCK_SUBTIPOS.TI_LOGICO;  
  MI_RTA                        PCK_SUBTIPOS.TI_LOGICO; 
  MI_MSGERROR                   PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN

  MI_RTA := -1;

  MI_PARFORCAUSACION := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                              UN_NOMBRE    => 'SF GENERAR FORMATO DE CAUSACION',
                                              UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                              UN_FECHA_PAR => SYSDATE,
                                              UN_IND_MAYUS => 0
                                              );

  MI_STROBS := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                              UN_NOMBRE    => 'SF SIMPLIFICAR COMPROBANTES',
                                              UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                              UN_FECHA_PAR => SYSDATE,
                                              UN_IND_MAYUS => 0
                                              ),'NO');


  IF  MI_STROBS  <> 'SI' THEN
    MI_SIMPLINTERFAZ := 0;

  ELSE 
    MI_SIMPLINTERFAZ := -1;

  END IF;  

   MI_STRSQL := 'SELECT INTERFAZADA,
      NRO_CPTE,
      TO_CHAR(FECHA_PAGO,''DD/MM/YYYY'')FECHA_PAGO,
      BANCO_PAGO,
      OBSERVACIONES,
      CODIGO_COBRO,
      FECHA_EXPEDICION,
      TERCERO,
      SUCURSAL
    FROM SF_FACTURA
    WHERE COMPANIA   = '''||UN_COMPANIA||'''
    AND TIPO_FACTURA = '''||UN_TIPOFACTURA||'''
    AND NUMERO_FACTURA = '||UN_NOFACTURA||' ';

     EXECUTE IMMEDIATE 'SELECT COUNT(1) FROM ('||MI_STRSQL||')' INTO MI_CONTEO;                  
      BEGIN
          IF MI_CONTEO >=1 THEN 
              OPEN MI_RSTMP FOR MI_STRSQL;
              LOOP        
               FETCH MI_RSTMP INTO MI_RSINTERFAZADA,MI_RSNROCPTE,MI_RSFECHAPAGO,MI_RSBANCOPAGO,MI_RSOBSERVACIONES,MI_RSCODIGOCOBRO,MI_RSFECHAEXPEDICION,MI_RSTERCERO,MI_RSSUCURSAL; 
                 EXIT WHEN MI_RSTMP%NOTFOUND; 


               IF MI_RSFECHAPAGO <> NULL THEN
                  MI_MSGERROR(1).CLAVE := 'FECHA';
                  MI_MSGERROR(1).VALOR := MI_RSFECHAPAGO;
                  MI_MSGERROR(1).CLAVE := 'BANCO';
                  MI_MSGERROR(1).VALOR := MI_RSBANCOPAGO;

                  PCK_ERR_MSG.RAISE_WITH_MSG(
                              UN_EXC_COD =>SQLCODE
                              ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_FACTURA_NOEXISTE
                               ,UN_REEMPLAZOS  => MI_MSGERROR);

               END IF;


               IF  MI_RSINTERFAZADA IN (0)THEN
                MI_STROBS := 'Factura '|| UN_TIPOFACTURA || ' - ' || UN_NOFACTURA || ' - '||MI_RSOBSERVACIONES;

                MI_INTERFAZADA := 0;

                IF PCK_FACT_GENERAL_COM3.FC_MANEJADERCONEXION( UN_COMPANIA   => UN_COMPANIA,
                                         UN_ANO        => TO_NUMBER(EXTRACT(YEAR FROM MI_RSFECHAEXPEDICION)),
                                         UN_TIPOCOBRO  => UN_TIPOFACTURA) NOT IN (0) THEN


                   MI_INTERFAZADA := PCK_FACT_GENERAL_COM3.FC_INTERFAZCNTDERCONEXION(UN_COMPANIA    => UN_COMPANIA,  
                                                               UN_TIPO        => UN_TIPOFACTURA,
                                                               UN_NUMERO      => MI_RSCODIGOCOBRO,
                                                               UN_FECHA       => UN_FECHAPAGO,
                                                               UN_TERCERO     => MI_RSTERCERO,
                                                               UN_SUCURSAL    => MI_RSSUCURSAL,
                                                               UN_DESCRIPCION => MI_STROBS,
                                                               UN_USUARIO     => UN_USUARIO);                      

                ELSE

                  MI_INTERFAZADA := PCK_FACT_GENERAL_COM3.FC_INTERFAZCONTABLE(UN_COMPANIA        => UN_COMPANIA,  
                                                       UN_TIPO             => UN_TIPOFACTURA,
                                                       UN_NUMERO           => MI_RSCODIGOCOBRO,
                                                       UN_FECHA            => UN_FECHAPAGO,
                                                       UN_TERCERO          => MI_RSTERCERO,
                                                       UN_SUCURSAL         => MI_RSSUCURSAL,
                                                       UN_DESCRIPCION      => MI_STROBS,
                                                       UN_MANEJAINVENTARIO => UN_MANEJAINVENTARIO,
                                                       UN_USUARIO          => UN_USUARIO);

                END IF;


                IF MI_INTERFAZADA NOT IN (0) THEN                

                  MI_CAMPOS := 'INTERFAZADA = -1
                               ,ANO_CPTE = '||EXTRACT(YEAR FROM TO_DATE(UN_FECHAPAGO))||'
                               ,TIPO_CPTE = '''||UN_TIPOFACTURA||'''
                               ,NRO_CPTE = '||MI_RSCODIGOCOBRO||' ';                  

                  MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||'''
                             AND   TIPO_FACTURA = '''||UN_TIPOFACTURA||'''
                             AND   NUMERO_FACTURA = '||UN_NOFACTURA||' ';


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
                                    MI_MSGERROR(1).CLAVE := 'NUMERO';
                                    MI_MSGERROR(1).VALOR := UN_NOFACTURA;
                                    MI_MSGERROR(1).CLAVE := 'TIPO';
                                    MI_MSGERROR(1).VALOR := UN_TIPOFACTURA;
                                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                    UN_EXC_COD   => SQLCODE,
                                    UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_ACTINTERFACT
                                 );
                 END;                               


                ELSE

                  MI_CAMPOS := 'INTERFAZADA = 0 
                               ,ANO_CPTE = 0
                               ,TIPO_CPTE = ''''
                               ,NRO_CPTE = 0 ';


                  MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||'''
                             AND   TIPO_FACTURA = '''||UN_TIPOFACTURA||'''
                             AND   NUMERO_FACTURA = '||UN_NOFACTURA||' ';


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
                                    MI_MSGERROR(1).CLAVE := 'NUMERO';
                                    MI_MSGERROR(1).VALOR := UN_NOFACTURA;
                                    MI_MSGERROR(1).CLAVE := 'TIPO';
                                    MI_MSGERROR(1).VALOR := UN_TIPOFACTURA;
                                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                    UN_EXC_COD   => SQLCODE,
                                    UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_ACTINTERFACT
                                 );
                 END;                    

                END IF;

                MI_RTA := -1;
               ELSE

                  MI_CAMPOS := 'INTERFAZADA = 0
                               ,ANO_CPTE = 0
                               ,TIPO_CPTE = ''''
                               ,NRO_CPTE = 0 ';


                  MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||'''
                             AND   TIPO_FACTURA = '''||UN_TIPOFACTURA||'''
                             AND   NUMERO_FACTURA = '||UN_NOFACTURA||' ';

                   MI_RTA := 0;

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
                                    MI_MSGERROR(1).CLAVE := 'NUMERO';
                                    MI_MSGERROR(1).VALOR := UN_NOFACTURA;
                                    MI_MSGERROR(1).CLAVE := 'TIPO';
                                    MI_MSGERROR(1).VALOR := UN_TIPOFACTURA;
                                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                    UN_EXC_COD   => SQLCODE,
                                    UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_ACTINTERFACT
                                 );
                 END;                


               END IF;

              END LOOP;

            ELSE
                 MI_RTA := 0;
                 RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
          END IF;       

                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
                            PCK_ERR_MSG.RAISE_WITH_MSG(
                                   UN_EXC_COD =>SQLCODE
                                   ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_FACTURA_NOEXISTE);

      END;

  RETURN MI_RTA;

END FC_INTERFAZAR_FACTURA;

--2

FUNCTION FC_MANEJADERCONEXION
/*
      NAME              : FC_MANEJADERCONEXIONCONCEPTO MIGRADO DE ACCESS ManejaDerConexion
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 13/08/2018
      TIME              : 12:55 PM
      SOURCE MODULE     : SysmanSF2018.07.06
      DESCRIPTION       :
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 

    @NAME:   manejarDerConexion
    @METHOD: GET
*/
(
  UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANO        IN PCK_SUBTIPOS.TI_ANIO,
  UN_TIPOCOBRO  IN SF_FACTURA.TIPO_FACTURA%TYPE
)
RETURN PCK_SUBTIPOS.TI_LOGICO AS 
  MI_RPTA                  PCK_SUBTIPOS.TI_LOGICO;
  MI_IND_DERECHOSCONEXION  SF_TIPO_COBRO.IND_DERECHOSCONEXION%TYPE;
  MI_FORMULAR_ACOMETIDA    SF_CONCEPTOS.FORMULAR_ACOMETIDA%TYPE;
  MI_FORMULAR_ADMONIMP     SF_CONCEPTOS.FORMULAR_ADMONIMP%TYPE;
  MI_FORMULAR_UTILIDADAIU  SF_CONCEPTOS.FORMULAR_UTILIDADAIU%TYPE;
  MI_FORMULAR_IVAUTILIDAD  SF_CONCEPTOS.FORMULAR_IVAUTILIDAD%TYPE;
BEGIN

        SELECT IND_DERECHOSCONEXION
        INTO MI_IND_DERECHOSCONEXION
        FROM SF_TIPO_COBRO
        WHERE COMPANIA = UN_COMPANIA
        AND ANO        = UN_ANO
        AND CODIGO     = UN_TIPOCOBRO;

        MI_RPTA := NVL(MI_IND_DERECHOSCONEXION, 0);

  RETURN MI_RPTA;

END FC_MANEJADERCONEXION;

--3
FUNCTION FC_INTERFAZCNTDERCONEXION
/*
      NAME              : FC_INTERFAZCNTDERCONEXION MIGRADO DE ACCESS InterfazContableDerConexion
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 13/08/2018
      TIME              : 12:55 PM
      SOURCE MODULE     : SysmanSF2018.07.06
      DESCRIPTION       :
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 


    @NAME:   manejarInterfazaCntDerConexion
    @METHOD: GET
*/
(
  UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,  
  UN_TIPO          IN PCK_SUBTIPOS.TI_TIPO_COBRO,
  UN_NUMERO        IN DETALLE_COMPROBANTE_CNT.COMPROBANTE%TYPE,
  UN_FECHA         IN DATE,
  UN_TERCERO       IN SF_DETALLE_FACTURA.TERCERO%TYPE,
  UN_SUCURSAL      IN SF_DETALLE_FACTURA.SUCURSAL%TYPE,
  UN_DESCRIPCION   IN COMPROBANTE_CNT.DESCRIPCION%TYPE,
  UN_USUARIO       IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN PCK_SUBTIPOS.TI_LOGICO AS 
    MI_RPTA                  PCK_SUBTIPOS.TI_LOGICO;
    MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES               PCK_SUBTIPOS.TI_VALORES;
    MI_MSGERROR              PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CONSC                 BOOLEAN; 
    MI_DEBITO                VARCHAR2(32 CHAR); 
    MI_CREDITO               VARCHAR2(32 CHAR);
    MI_CLASECONTABLE         TIPO_COMPROBANTE.CLASE_CONTABLE%TYPE;
    MI_CONSECUTIVO           NUMBER(5,0);
    MI_ANIO                  NUMBER(4,0);
    MI_MES                   NUMBER(4,0);
    MI_NATURALEZA            PCK_SUBTIPOS.TI_NATURALEZACONTA;
    MI_RTAINTERFAZ           PCK_SUBTIPOS.TI_RTA_ACME; 
BEGIN

  MI_CONSECUTIVO := 0;
  MI_ANIO := EXTRACT (YEAR FROM UN_FECHA);
  MI_MES := EXTRACT (YEAR FROM UN_FECHA);

  SELECT CLASE_CONTABLE 
  INTO   MI_CLASECONTABLE
  FROM   TIPO_COMPROBANTE 
  WHERE  COMPANIA = UN_COMPANIA
    AND  CODIGO = UN_TIPO;

  MI_RPTA := -1;

   FOR MI_RS IN (SELECT SF_FACTURA.COMPANIA,
        SF_FACTURA.TIPOCOBRO,
        SF_FACTURA.CODIGO_COBRO,
        SF_FACTURA.VALOR_TOTAL,
        SF_FACTURA.OBSERVACIONES,
        SF_FACTURA.TERCERO      TERCEROH,
        SF_FACTURA.SUCURSAL     SUCURSALH,
        SF_FACTURA.CENTRO_COSTO CENTRO_COSTOH,
        SF_FACTURA.AUXILIAR     AUXILIARH,
        SF_CONCEPTOS.CODIGO,
        SF_DETALLE_FACTURA.VALOR_BASE,
        SF_CONCEPTOS.CUENTADEBITOBASE,
        SF_CONCEPTOS.CUENTACREDITOBASE,
        SF_CONCEPTOS.APLICAIVA,
        SF_DETALLE_FACTURA.VALOR_IVA,
        SF_CONCEPTOS.CUENTADEBITOIVA,
        SF_CONCEPTOS.CUENTACREDITOIVA,
        SF_CONCEPTOS.APLICARETEFUENTE,
        SF_DETALLE_FACTURA.VALOR_RETEFUENTE,
        SF_CONCEPTOS.CUENTADEBITORETE,
        SF_CONCEPTOS.CUENTACREDITORETE,
        SF_CONCEPTOS.APLICADESCUENTO,
        SF_DETALLE_FACTURA.VALOR_DESCUENTO,
        SF_CONCEPTOS.CUENTADEBITODESCUENTO,
        SF_CONCEPTOS.CUENTACREDITODESCUENTO,
        SF_CONCEPTOS.APLICAICA,
        SF_CONCEPTOS.TIPODECONCEPTO,
        SF_DETALLE_FACTURA.VALOR_ICA,
        SF_CONCEPTOS.CUENTADEBITOICA,
        SF_CONCEPTOS.CUENTACREDITOICA,
        CTADEBACOMETIDA,
        CTACREDACOMETIDA,
        CTADEBADMONIMP,
        CTACREDADMONIMP,
        CTADEBUTILIDADAIU,
        CTACREDUTILIDADAIU,
        CTADEBIVAUTILIDAD,
        CTACREDIVAUTILIDAD,
        SF_DETALLE_FACTURA.VALOR_COMPRA,
        SF_DETALLE_FACTURA.VALOR_UTILIDAD,
        SF_CONCEPTOS.CUENTADEBITOUTILIDAD,
        SF_CONCEPTOS.CUENTACREDITOUTILIDAD,
        SF_DETALLE_FACTURA.TERCERO,
        SF_DETALLE_FACTURA.SUCURSAL,
        SF_DETALLE_FACTURA.CENTRO_COSTO,
        SF_DETALLE_FACTURA.AUXILIAR,
        SF_DETALLE_FACTURA.ACOMETIDA_SERVICIO,
        SF_DETALLE_FACTURA.ADMON_IMPREVISTOS,
        SF_DETALLE_FACTURA.UTILIDAD_AIU,
        SF_DETALLE_FACTURA.IVA_UTILIDAD ,
        SF_DETALLE_FACTURA.REFERENCIA
      FROM SF_DETALLE_FACTURA
      INNER JOIN SF_CONCEPTOS
      ON SF_DETALLE_FACTURA.COMPANIA   = SF_CONCEPTOS.COMPANIA
      AND SF_DETALLE_FACTURA.TIPOCOBRO = SF_CONCEPTOS.TIPOCOBRO
      AND SF_DETALLE_FACTURA.CONCEPTO  = SF_CONCEPTOS.CODIGO
      AND SF_DETALLE_FACTURA.ANO       = SF_CONCEPTOS.ANO
      INNER JOIN SF_FACTURA
      ON SF_DETALLE_FACTURA.COMPANIA      = SF_FACTURA.COMPANIA
      AND SF_DETALLE_FACTURA.FACTURA      = SF_FACTURA.NUMERO_FACTURA
      AND SF_DETALLE_FACTURA.TIPO_FACTURA = SF_FACTURA.TIPO_FACTURA
      WHERE SF_FACTURA.COMPANIA      = UN_COMPANIA
        AND SF_FACTURA.TIPOCOBRO     = UN_TIPO
        AND SF_FACTURA.CODIGO_COBRO  = UN_NUMERO
        AND SF_FACTURA.TERCERO       = UN_TERCERO
        AND SF_FACTURA.SUCURSAL      = UN_SUCURSAL)LOOP


        FOR MI_RS2  IN (SELECT COMPANIA, ANO, CUENTA, VALOR_DEBITO, VALOR_CREDITO, NATURALEZA 
                         FROM  DETALLE_COMPROBANTE_CNT 
                         WHERE COMPANIA    = UN_COMPANIA 
                           AND ANO         = MI_ANIO
                           AND TIPO_CPTE   = UN_TIPO
                           AND COMPROBANTE = UN_NUMERO
        )LOOP

          IF FC_ACTCONTA0(UN_COMPANIA    => MI_RS2.COMPANIA,  
                          UN_ANO         => MI_RS2.ANO,
                          UN_MES         => MI_MES,
                          UN_A_ID        => MI_RS2.CUENTA,
                          UN_DEB_ANT     => MI_RS2.VALOR_DEBITO,
                          UN_CRE_ANT     => MI_RS2.VALOR_CREDITO,
                          UN_DIF_ANT     => CASE WHEN MI_RS2.NATURALEZA = 'D' THEN  MI_RS2.VALOR_DEBITO - MI_RS2.VALOR_CREDITO ELSE MI_RS2.VALOR_CREDITO - MI_RS2.VALOR_DEBITO END,
                          UN_DEBITO      => 0,
                          UN_CREDITO     => 0,
                          UN_DIFERENCIA  => 0,
                          UN_NATURALEZA  => MI_RS2.NATURALEZA,                          
                          UN_USUARIO     => UN_USUARIO ) IN (0) THEN


            RETURN 0;
          END IF;

        MI_CONDICION :=' COMPANIA  = '''||UN_COMPANIA||'''
                   AND ANO         = '||EXTRACT(YEAR FROM UN_FECHA)||'
                   AND TIPO_CPTE   = '''||UN_TIPO||'''
                   AND COMPROBANTE = '||UN_NUMERO;

      BEGIN
       BEGIN
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'DETALLE_COMPROBANTE_CNT'
                                              ,UN_ACCION  => 'E'
                                              ,UN_CONDICION => MI_CONDICION);                                                           

           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;                                   

       END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
               MI_MSGERROR(1).CLAVE := 'NUMERO';
               MI_MSGERROR(1).VALOR := UN_NUMERO;
               MI_MSGERROR(2).CLAVE := 'TIPO';
               MI_MSGERROR(2).VALOR := UN_TIPO;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_ELIMIDETALLECNT
                                        ,UN_REEMPLAZOS  => MI_MSGERROR);

      END;

        MI_CONDICION :='COMPANIA = '''||UN_COMPANIA||'''
                   AND  ANO      = '||EXTRACT(YEAR FROM UN_FECHA)||'
                   AND  TIPO     = '''||UN_TIPO||'''
                   AND  NUMERO   = '||UN_NUMERO;

      BEGIN
       BEGIN
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'COMPROBANTE_CNT'
                                              ,UN_ACCION  => 'E'
                                              ,UN_CONDICION => MI_CONDICION);                                                           

           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;                                   

       END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
               MI_MSGERROR(1).CLAVE := 'NUMERO';
               MI_MSGERROR(1).VALOR := UN_NUMERO;
               MI_MSGERROR(2).CLAVE := 'TIPO';
               MI_MSGERROR(2).VALOR := UN_TIPO;

                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_ELIMIDETALLECNT
                                        ,UN_REEMPLAZOS  => MI_MSGERROR);

      END;   


        END LOOP;

      --Etapa 02

        MI_CONSC := FALSE;

        IF MI_CLASECONTABLE IN ('V')THEN          
          MI_DEBITO   := NVL(MI_RS.CUENTADEBITOBASE,'');
          MI_CREDITO  := NVL(MI_RS.CUENTACREDITOBASE,'');        
        ELSE  
          MI_CREDITO  := NVL(MI_RS.CUENTADEBITOBASE,'');
          MI_DEBITO   := NVL(MI_RS.CUENTACREDITOBASE,'');

        END IF;


      -- '********************************************************INICIO INVENTARIO ***************************************************** 

      IF MI_RS.CTADEBACOMETIDA IS NULL THEN
      -- '*************   REGISTRO DEL VALOR DE ACOMETIDA  *******************
      -- '*****************          DEBITO                *******************
        IF MI_CLASECONTABLE IN ('V')THEN

          MI_DEBITO   := NVL(MI_RS.CTADEBACOMETIDA,'');
          MI_CREDITO  := NVL(MI_RS.CTACREDACOMETIDA,'');

        ELSE  
          MI_CREDITO  := NVL(MI_RS.CTADEBACOMETIDA,'');
          MI_DEBITO   := NVL(MI_RS.CTACREDACOMETIDA,'');

        END IF;  



        IF MI_DEBITO IS NOT NULL THEN 

            MI_CONSECUTIVO  :=  MI_CONSECUTIVO + 1 ;
            MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_DEBITO);

            MI_CAMPOS := 'COMPANIA
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
                          , REFERENCIA';

            MI_VALORES:= ' '''||UN_COMPANIA||''',
                         '||MI_ANIO||',
                         '''||UN_TIPO||'''
                         ,'||UN_NUMERO||'
                         ,'||MI_CONSECUTIVO||'
                         ,'''||MI_DEBITO||'''
                         ,'''||UN_FECHA||'''
                         ,'''||MI_NATURALEZA||'''
                         ,'||NVL(MI_RS.ACOMETIDA_SERVICIO,0)||'
                         ,0
                         ,'||NVL(MI_RS.ACOMETIDA_SERVICIO,0)||'
                         ,0
                         ,'''||NVL(MI_RS.CENTRO_COSTO,9999999999)||'''
                         ,'''||MI_RS.TERCERO||'''
                         ,'''||MI_RS.SUCURSAL||'''
                         ,'''||NVL(MI_RS.AUXILIAR,9999999999999999)||'''
                         ,''
                         ,'''||UN_DESCRIPCION||'''
                         ,'''||MI_RS.REFERENCIA||''' ';

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
               MI_MSGERROR(1).VALOR := UN_NUMERO;
               MI_MSGERROR(2).CLAVE := 'TIPO';
               MI_MSGERROR(2).VALOR := UN_TIPO;

                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                        ,UN_REEMPLAZOS  => MI_MSGERROR);

          END;


           MI_CONSC := TRUE;             

        ELSE

          MI_RPTA := 0;
          CONTINUE;          

        END IF;

        --Etapa 04

        IF NVL(MI_RS.ACOMETIDA_SERVICIO,0) NOT IN (0)THEN

        --Etapa 05
          MI_RPTA := 0;
          CONTINUE;          

        END IF;

      --'***************** CREDITO *******************  
        IF MI_CREDITO IS NOT NULL THEN

          IF MI_CONSC THEN

            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
            MI_CONSC := FALSE;

          END IF;
          MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CREDITO);
          MI_CAMPOS := 'COMPANIA
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
                      , REFERENCIA ';

           MI_VALORES := ''''||UN_COMPANIA||'''
                         , '||MI_ANIO||'
                         ,'''||UN_TIPO||'''
                         ,'||UN_NUMERO||'
                         ,'||MI_CONSECUTIVO||'
                         ,'''||MI_CREDITO||'''
                         ,'''||UN_FECHA||'''
                         ,'''||MI_NATURALEZA||'''
                         ,0
                         ,'||MI_RS.ACOMETIDA_SERVICIO||'
                         ,0
                         ,'||NVL(MI_RS.ACOMETIDA_SERVICIO, 0)||'
                         ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                         ,'''||MI_RS.TERCERO||'''
                         ,'''||MI_RS.SUCURSAL||'''
                         ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||''' 
                         ,''
                         ,'''||UN_DESCRIPCION||'''
                         ,'''||MI_RS.REFERENCIA||''' ';    

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
               MI_MSGERROR(1).VALOR := UN_NUMERO;
               MI_MSGERROR(2).CLAVE := 'TIPO';
               MI_MSGERROR(2).VALOR := UN_TIPO;

                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                        ,UN_REEMPLAZOS  => MI_MSGERROR);

          END;                         

        MI_CONSC := TRUE;

        ELSE 

         MI_RPTA := 0;
         CONTINUE;                 

        END IF;

        --ETAPA 06

            IF NVL(MI_RS.ACOMETIDA_SERVICIO,0) NOT IN (0)THEN

              --Etapa 07
              MI_RPTA := 0;
              CONTINUE;

            END IF;

          --'*************   REGISTRO DEL VALOR DE ADMON E IMPREVISTOS  *******************
          --'***************                DEBITO                    *******************

            IF MI_CLASECONTABLE IN ('V') THEN
                MI_DEBITO := NVL(MI_RS.CTADEBADMONIMP, '');
                MI_CREDITO := NVL(MI_RS.CTACREDADMONIMP,'');
            ELSE
                MI_CREDITO := NVL(MI_RS.CTADEBADMONIMP,'');
                MI_DEBITO := NVL(MI_RS.CTACREDADMONIMP,'');
            END IF;

            IF MI_DEBITO IS NOT NULL THEN

            MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_DEBITO);

                MI_CAMPOS := 'COMPANIA
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
                            , REFERENCIA ';

              MI_VALORES := ' '''||UN_COMPANIA||'''
                          ,'||MI_ANIO||'
                          ,'''||UN_TIPO||'''
                          ,'||UN_NUMERO||'
                          ,'||MI_CONSECUTIVO||'
                          ,'''||MI_DEBITO||'''
                          ,'''||UN_FECHA||'''
                          ,'''||MI_NATURALEZA||'''
                          ,'||MI_RS.ADMON_IMPREVISTOS||'
                          ,0
                          ,'||NVL(MI_RS.ADMON_IMPREVISTOS, 0)||'
                          ,0
                          ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                          ,'''||MI_RS.TERCERO||'''
                          ,'''||MI_RS.SUCURSAL||'''
                          ,'''||NVL(MI_RS.AUXILIAR,9999999999999999)||'''
                          ,''
                          ,'''||UN_DESCRIPCION||'''
                          ,'''||MI_RS.REFERENCIA||''' ';


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
                         MI_MSGERROR(1).VALOR := UN_NUMERO;
                         MI_MSGERROR(2).CLAVE := 'TIPO';
                         MI_MSGERROR(2).VALOR := UN_TIPO;

                          PCK_ERR_MSG.RAISE_WITH_MSG(
                                                  UN_EXC_COD =>SQLCODE
                                                  ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                  ,UN_REEMPLAZOS  => MI_MSGERROR);

                    END;                                         
              MI_CONSC := TRUE;
            ELSE
             MI_RPTA := 0;
             CONTINUE;             
            END IF;

        --Etapa 04

        IF NVL(MI_RS.ADMON_IMPREVISTOS, 0) <> 0 THEN          
        --Etapa 05
         MI_RPTA := 0;
         CONTINUE;         
        END IF;

     --   '***************** CREDITO *******************

              IF MI_CREDITO IS NOT NULL THEN

                 IF MI_CONSC  THEN

                    MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
                    MI_CONSC := FALSE;
                 END IF;

                 MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CREDITO);

                    MI_CAMPOS := 'COMPANIA
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
                                , REFERENCIA ';

                 MI_VALORES := ' '''||UN_COMPANIA||'''
                                ,'||MI_ANIO||'
                                ,'''||UN_TIPO||'''
                                ,'||UN_NUMERO||'
                                ,'||MI_CONSECUTIVO||'
                                ,'''||MI_CREDITO||'''
                                ,'''||UN_FECHA||'''
                                ,'''||MI_NATURALEZA||'''
                                ,0
                                ,'||MI_RS.ADMON_IMPREVISTOS||'
                                ,0
                                ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                                ,'''||MI_RS.TERCERO||'''
                                ,'''||MI_RS.SUCURSAL||'''
                                ,'''||NVL(MI_RS.AUXILIAR,9999999999999999)||'''
                                ,''
                                ,'''||UN_DESCRIPCION||'''
                                ,'''||MI_RS.REFERENCIA||''' ';

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
                         MI_MSGERROR(1).VALOR := UN_NUMERO;
                         MI_MSGERROR(2).CLAVE := 'TIPO';
                         MI_MSGERROR(2).VALOR := UN_TIPO;

                          PCK_ERR_MSG.RAISE_WITH_MSG(
                                                  UN_EXC_COD =>SQLCODE
                                                  ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                  ,UN_REEMPLAZOS  => MI_MSGERROR);

                    END;              

               MI_CONSC := TRUE;

              ELSE
                MI_RPTA := 0;
                CONTINUE;                
              END IF;

             --Etapa 06

            IF NVL(MI_RS.ADMON_IMPREVISTOS, 0) <> 0 THEN            
             --Etapa 07
              MI_RPTA := 0;
              CONTINUE;              
            END IF;

     --'*************   REGISTRO DEL VALOR DE UTILIDAD AIU  *******************
     --'*****************             DEBITO                *******************

          IF MI_CLASECONTABLE IN ('V') THEN
               MI_DEBITO := NVL(MI_RS.CTADEBUTILIDADAIU, '');
               MI_CREDITO := NVL(MI_RS.CTACREDUTILIDADAIU,'');
          ELSE
               MI_CREDITO := NVL(MI_RS.CTADEBUTILIDADAIU, '');
               MI_DEBITO := NVL(MI_RS.CTACREDUTILIDADAIU, '');
          END IF;


          IF MI_DEBITO IS NOT NULL THEN

            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
            MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_DEBITO);

            MI_CAMPOS := 'COMPANIA
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
                        , REFERENCIA ';

            MI_VALORES := ''''||UN_COMPANIA||'''
                            ,'||MI_ANIO||'
                            ,'''||UN_TIPO||'''
                            ,'||UN_NUMERO||'
                            ,'||MI_CONSECUTIVO||'
                            ,'''||MI_DEBITO||'''
                            ,'''||UN_FECHA||'''
                            ,'''||MI_NATURALEZA||'''
                            ,'''||MI_RS.UTILIDAD_AIU||'''
                            ,0
                            ,'||NVL(MI_RS.UTILIDAD_AIU, 0)||'
                            ,0
                            ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                            ,'''||MI_RS.TERCERO||'''
                            ,'''||MI_RS.SUCURSAL||'''
                            ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||'''
                            ,''
                            ,'''||UN_DESCRIPCION||'''
                            ,'''||MI_RS.REFERENCIA||''' ';

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
                         MI_MSGERROR(1).VALOR := UN_NUMERO;
                         MI_MSGERROR(2).CLAVE := 'TIPO';
                         MI_MSGERROR(2).VALOR := UN_TIPO;

                          PCK_ERR_MSG.RAISE_WITH_MSG(
                                                  UN_EXC_COD =>SQLCODE
                                                  ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                  ,UN_REEMPLAZOS  => MI_MSGERROR);

                    END;              

            MI_CONSC := TRUE;
          ELSE
            MI_RPTA := 0;
            CONTINUE;            
          END IF;

            --Etapa 04
         IF NVL(MI_RS.UTILIDAD_AIU, 0) <> 0 THEN         
          --Etapa 05         
          MI_RPTA := 0;
          CONTINUE;          
         END IF;

      --'***************** CREDITO *******************

        IF MI_CREDITO IS NOT NULL THEN

          IF MI_CONSC THEN

            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
            MI_CONSC := FALSE;

          END IF;

          MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CREDITO);

            MI_CAMPOS := 'COMPANIA
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
                        , REFERENCIA ';

            MI_VALORES := ''''||UN_COMPANIA||'''
                            ,'||MI_ANIO||'
                            ,'''||UN_TIPO||'''
                            ,'||UN_NUMERO||'
                            ,'||MI_CONSECUTIVO||'
                            ,'''||MI_CREDITO||'''
                            ,'''||UN_FECHA||'''
                            ,'''||MI_NATURALEZA||'''
                            ,0
                            ,'''||MI_RS.UTILIDAD_AIU||'''
                            ,0
                            ,'||NVL(MI_RS.UTILIDAD_AIU, 0)||'
                            ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                            ,'''||MI_RS.TERCERO||'''
                            ,'''||MI_RS.SUCURSAL||'''
                            ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||'''
                            ,''
                            ,'''||UN_DESCRIPCION||'''
                            ,'''||MI_RS.REFERENCIA||''' ';


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
                         MI_MSGERROR(1).VALOR := UN_NUMERO;
                         MI_MSGERROR(2).CLAVE := 'TIPO';
                         MI_MSGERROR(2).VALOR := UN_TIPO;

                          PCK_ERR_MSG.RAISE_WITH_MSG(
                                                  UN_EXC_COD =>SQLCODE
                                                  ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                  ,UN_REEMPLAZOS  => MI_MSGERROR);

                    END; 

          MI_CONSC := TRUE;
        ELSE
          MI_RPTA := 0;
          CONTINUE;         
        END IF;

         --Etapa 06


         IF NVL(MI_RS.UTILIDAD_AIU, 0) <> 0 THEN
            MI_RPTA := 0;
            CONTINUE;
         END IF;



        --'*************   REGISTRO DEL VALOR DE IVA DE LA UTILIDAD  *******************
        --'***************                 DEBITO                ******************* 

          IF MI_CLASECONTABLE IN ('V') THEN
              MI_DEBITO := NVL(MI_RS.CTADEBIVAUTILIDAD,'');
              MI_CREDITO := NVL(MI_RS.CTACREDIVAUTILIDAD,'');
          ELSE
              MI_CREDITO := NVL(MI_RS.CTADEBIVAUTILIDAD, '');
              MI_DEBITO := NVL(MI_RS.CTACREDIVAUTILIDAD, '');
          END IF;


          IF MI_DEBITO IS NOT NULL THEN

            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
            MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_DEBITO);

            MI_CAMPOS := 'COMPANIA
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
                        , REFERENCIA ';


            MI_VALORES := ' '''||UN_COMPANIA||'''
                          , '||MI_ANIO||'
                          ,'''||UN_TIPO||'''
                          ,'||UN_NUMERO||'
                          ,'||MI_CONSECUTIVO||'
                          ,'''||MI_DEBITO||'''
                          ,'''||UN_FECHA||'''
                          ,'''||MI_NATURALEZA||'''
                          ,'||MI_RS.IVA_UTILIDAD||'
                          ,0
                          ,'||NVL(MI_RS.IVA_UTILIDAD, 0)||'
                          ,0
                          ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                          ,'''||MI_RS.TERCERO||'''
                          ,'''||MI_RS.SUCURSAL||'''
                          ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||'''
                          ,''
                          ,'''||UN_DESCRIPCION||'''
                          ,'''||MI_RS.REFERENCIA||''' ';  

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
                         MI_MSGERROR(1).VALOR := UN_NUMERO;
                         MI_MSGERROR(2).CLAVE := 'TIPO';
                         MI_MSGERROR(2).VALOR := UN_TIPO;

                          PCK_ERR_MSG.RAISE_WITH_MSG(
                                                  UN_EXC_COD =>SQLCODE
                                                  ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                  ,UN_REEMPLAZOS  => MI_MSGERROR);

                    END; 

             MI_CONSC := TRUE;

          ELSE 
           MI_RPTA := 0;
           CONTINUE;
          END IF;

        --Etapa04

           IF NVL(MI_RS.IVA_UTILIDAD, 0) <> 0 THEN

            --Etapa 05
            MI_RPTA := 0;
            CONTINUE;            
           END IF;

          --'***************** CREDITO *******************

          IF MI_CREDITO IS NOT NULL THEN

            IF MI_CONSC THEN

              MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
              MI_CONSC := FALSE;
            END IF;

            MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CREDITO);
            MI_CAMPOS := 'COMPANIA
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
                        , REFERENCIA ';

            MI_VALORES := ''''||UN_COMPANIA||'''
                        ,'||MI_ANIO||'
                        ,'''||UN_TIPO||'''
                        ,'||UN_NUMERO||'
                        ,'||MI_CONSECUTIVO||'
                        ,'''||MI_CREDITO||'''
                        ,'''||UN_FECHA||'''
                        ,'''||MI_NATURALEZA||'''
                        ,0
                        ,'||MI_RS.IVA_UTILIDAD||'
                        ,0
                        ,'||NVL(MI_RS.IVA_UTILIDAD, 0)||'
                        ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                        ,'''||MI_RS.TERCERO||'''
                        ,'''||MI_RS.SUCURSAL||'''
                        ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||'''
                        ,''
                        ,'''||UN_DESCRIPCION||'''
                        ,'''||MI_RS.REFERENCIA||''' ';                    

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
                         MI_MSGERROR(1).VALOR := UN_NUMERO;
                         MI_MSGERROR(2).CLAVE := 'TIPO';
                         MI_MSGERROR(2).VALOR := UN_TIPO;

                          PCK_ERR_MSG.RAISE_WITH_MSG(
                                                  UN_EXC_COD =>SQLCODE
                                                  ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                  ,UN_REEMPLAZOS  => MI_MSGERROR);

                    END; 

            MI_CONSC := TRUE;

          ELSE
            MI_RPTA := 0;
            CONTINUE;
          END IF;

         --Etapa 06 

         IF NVL(MI_RS.IVA_UTILIDAD,0)<> 0 THEN
          --Etapa 07
          MI_RPTA := 0;
          CONTINUE;          
         END IF;
      END IF;--Fin del interfaz de valores de derecho de conexión


    --'*************   REGISTRO DEL VALOR COMPRA   *******************
    --'*****************        DEBITO         *******************

     IF MI_DEBITO IS NOT NULL THEN

      MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
      MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_DEBITO);
            MI_CAMPOS := 'COMPANIA
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
                        , REFERENCIA ';


            MI_VALORES:= ' '''||UN_COMPANIA||''' 

                       ,'||MI_ANIO||'
                       ,'''||UN_TIPO||'''
                       ,'||UN_NUMERO||'
                       ,'||MI_CONSECUTIVO||' 
                       ,'''||MI_DEBITO||'''
                       ,'''||UN_FECHA||'''
                       ,'''||MI_NATURALEZA||'''
                       ,'||MI_RS.VALOR_COMPRA||' 
                       ,0
                       ,'||NVL(MI_RS.VALOR_COMPRA, 0)||'
                       ,0 
                       ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                       ,'''||MI_RS.TERCERO||'''
                       ,'''||MI_RS.SUCURSAL||'''
                       ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||'''
                       ,''
                       ,'''||UN_DESCRIPCION||''' 
                       ,'''||MI_RS.REFERENCIA||''' ';      

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
                         MI_MSGERROR(1).VALOR := UN_NUMERO;
                         MI_MSGERROR(2).CLAVE := 'TIPO';
                         MI_MSGERROR(2).VALOR := UN_TIPO;

                          PCK_ERR_MSG.RAISE_WITH_MSG(
                                                  UN_EXC_COD =>SQLCODE
                                                  ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                  ,UN_REEMPLAZOS  => MI_MSGERROR);

                    END; 

            MI_CONSC := TRUE;
     ELSE
       MI_RPTA := 0;
       CONTINUE;           
     END IF;

     --Etapa 04

      IF NVL(MI_RS.VALOR_COMPRA, 0) <> 0 THEN
      --Etapa 05
       MI_RPTA := 0;
       CONTINUE;       
      END IF; 

  --'***************** CREDITO *******************

    IF MI_CREDITO IS NOT NULL THEN

        IF MI_CONSC THEN
          MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
          MI_CONSC := FALSE;
        END IF;

        MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CREDITO);
        MI_CAMPOS := 'COMPANIA
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
                        , REFERENCIA '; 


          MI_VALORES := ''''||UN_COMPANIA||'''
                      ,'||MI_ANIO||'
                      ,'''||UN_TIPO||'''
                      ,'||UN_NUMERO||'
                      ,'||MI_CONSECUTIVO||'
                      ,'''||MI_CREDITO||'''
                      ,'''||UN_FECHA||'''
                      ,'''||MI_NATURALEZA||'''
                      ,0
                      ,'||MI_RS.VALOR_COMPRA||'
                      ,0
                      ,'||NVL(MI_RS.VALOR_COMPRA, 0)||'
                      ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                      ,'''||MI_RS.TERCERO||'''
                      ,'''||MI_RS.SUCURSAL||'''
                      ,'''||NVL(MI_RS.AUXILIAR,9999999999999999)||'''
                      ,''
                      ,'''||UN_DESCRIPCION||'''
                      ,'''||MI_RS.REFERENCIA||''' ';              

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
                         MI_MSGERROR(1).VALOR := UN_NUMERO;
                         MI_MSGERROR(2).CLAVE := 'TIPO';
                         MI_MSGERROR(2).VALOR := UN_TIPO;

                          PCK_ERR_MSG.RAISE_WITH_MSG(
                                                  UN_EXC_COD =>SQLCODE
                                                  ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                  ,UN_REEMPLAZOS  => MI_MSGERROR);

                    END; 

            MI_CONSC := TRUE;

    ELSE
     MI_RPTA := 0;
     CONTINUE;            
    END IF;

    --Etapa 06

    IF NVL(MI_RS.VALOR_COMPRA, 0) <> 0 THEN    
    --Etapa 07 
      MI_RPTA := 0;
      CONTINUE;      
    END IF;

 --'*************   REGISTRO DEL VALOR DE LA UTILIDAD PARA EL CASO DONDE SE MANEJA ALMACEN   *******************
 --                              '***************** DEBITO *******************   

    IF MI_RS.TIPODECONCEPTO IN ('C') THEN  -- Esta condición debido a que los conceptos de interes no tiene utilidad por lo tanto no se configuran cuentas

        IF MI_CLASECONTABLE IN ('V') THEN
           MI_DEBITO := NVL(MI_RS.CUENTADEBITOUTILIDAD, '');
           MI_CREDITO := NVL(MI_RS.CUENTACREDITOUTILIDAD, '');
        ELSE
           MI_CREDITO := NVL(MI_RS.CUENTADEBITOUTILIDAD, '');
           MI_DEBITO := NVL(MI_RS.CUENTACREDITOUTILIDAD, '');
        END IF;

       IF MI_DEBITO IS NOT NULL THEN 

        MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
        MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_DEBITO);
          MI_CAMPOS := 'COMPANIA
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
                      , REFERENCIA ';

          MI_VALORES := ' '''||UN_COMPANIA||'''
                          ,'||MI_ANIO||'
                          ,'''||UN_TIPO||'''
                          ,'||UN_NUMERO||'
                          ,'||MI_CONSECUTIVO||'
                          , '||MI_DEBITO||'
                          ,'''||UN_FECHA||'''
                          ,'||MI_NATURALEZA||'
                          ,'||MI_RS.VALOR_UTILIDAD||'
                          ,0
                          ,'||NVL(MI_RS.VALOR_UTILIDAD, 0)||'
                          ,0
                          ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                          ,'''||MI_RS.TERCERO||'''
                          ,'''||MI_RS.SUCURSAL||'''
                          ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||'''
                          ,''
                          ,'''||UN_DESCRIPCION||'''
                          ,'''||MI_RS.REFERENCIA||''' ';  

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
                         MI_MSGERROR(1).VALOR := UN_NUMERO;
                         MI_MSGERROR(2).CLAVE := 'TIPO';
                         MI_MSGERROR(2).VALOR := UN_TIPO;

                          PCK_ERR_MSG.RAISE_WITH_MSG(
                                                  UN_EXC_COD =>SQLCODE
                                                  ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                  ,UN_REEMPLAZOS  => MI_MSGERROR);

                    END; 

            MI_CONSC := TRUE;                          
       ELSE
        MI_RPTA := 0;
        CONTINUE;
       END IF;

       -- Etapa 04


       IF NVL(MI_RS.VALOR_UTILIDAD, 0) <> 0 THEN       
        --Etapa 05
        MI_RPTA := 0;
        CONTINUE;
       END IF;

    --'***************** CREDITO *******************

      IF MI_CREDITO IS NOT NULL THEN

          IF MI_CONSC THEN
              MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
              MI_CONSC := FALSE;
          END IF;        

          MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CREDITO);

          MI_CAMPOS := 'COMPANIA
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
                      , REFERENCIA ';

          MI_VALORES := ' '''||UN_COMPANIA||'''
                          ,'||MI_ANIO||'
                          ,'''||UN_TIPO||'''
                          ,'||UN_NUMERO||'
                          ,'||MI_CONSECUTIVO||'
                          ,'''||MI_CREDITO||'''
                          ,'''||UN_FECHA||'''
                          ,'''||MI_NATURALEZA||'''
                          ,0
                          ,'||MI_RS.VALOR_UTILIDAD||'
                          ,0
                          ,'||NVL(MI_RS.VALOR_UTILIDAD, 0)||'
                          ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                          ,'''||MI_RS.TERCERO||'''
                          ,'''||MI_RS.SUCURSAL||'''
                          ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||'''
                          , ''
                          ,'''||UN_DESCRIPCION||'''
                          ,'''||MI_RS.REFERENCIA||''' ';    

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
                         MI_MSGERROR(1).VALOR := UN_NUMERO;
                         MI_MSGERROR(2).CLAVE := 'TIPO';
                         MI_MSGERROR(2).VALOR := UN_TIPO;

                          PCK_ERR_MSG.RAISE_WITH_MSG(
                                                  UN_EXC_COD =>SQLCODE
                                                  ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                  ,UN_REEMPLAZOS  => MI_MSGERROR);

                    END; 

        MI_CONSC := TRUE;

      ELSE
        MI_RPTA := 0;
        CONTINUE;
      END IF;

      --Etapa 06

      IF NVL(MI_RS.VALOR_UTILIDAD,0) <> 0 THEN
        --Etapa 07
        MI_RPTA := 0;
        CONTINUE;
      END IF;

    END IF;


    --******************************************************** FIN INVENTARIO *******************************************************
    --                        *************   REGISTRO DEL VALOR IVA SI INDICADOR SE ENCUENTRA ACTIVO   *******************

     IF MI_RS.APLICAIVA NOT IN (0) THEN  
      -- ***************** DEBITO *******************
          IF MI_CLASECONTABLE IN  ('V') THEN
            MI_DEBITO := NVL(MI_RS.CUENTADEBITOIVA, '');
            MI_CREDITO := NVL(MI_RS.CUENTACREDITOIVA, '');
          ELSE
            MI_CREDITO := NVL(MI_RS.CUENTADEBITOIVA, '');
            MI_DEBITO := NVL(MI_RS.CUENTACREDITOIVA, '');
          END IF;

          IF MI_DEBITO IS NOT NULL THEN 

              MI_CONSECUTIVO := MI_CONSECUTIVO + 1 ;
              MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_DEBITO);

              MI_CAMPOS := 'COMPANIA
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
                          , REFERENCIA '; 

              MI_VALORES := ' '''||UN_COMPANIA||'''
                            ,'||MI_ANIO||'
                            ,'''||UN_TIPO||'''
                            ,'||UN_NUMERO||'
                            ,'||MI_CONSECUTIVO||'
                            ,'''||MI_DEBITO||'''
                            ,'''||UN_FECHA||'''
                            ,'''||MI_NATURALEZA||'''
                            ,'||MI_RS.VALOR_IVA||'
                            ,0
                            ,'||NVL(MI_RS.VALOR_IVA, 0)||'
                            ,0
                            ,'||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'
                            ,'''||MI_RS.TERCERO||'''
                            ,'''||MI_RS.SUCURSAL||'''
                            ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||'''
                            ,''
                            ,'''||UN_DESCRIPCION||'''
                            ,'''||MI_RS.REFERENCIA||''' '; 

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
                         MI_MSGERROR(1).VALOR := UN_NUMERO;
                         MI_MSGERROR(2).CLAVE := 'TIPO';
                         MI_MSGERROR(2).VALOR := UN_TIPO;

                          PCK_ERR_MSG.RAISE_WITH_MSG(
                                                  UN_EXC_COD =>SQLCODE
                                                  ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                  ,UN_REEMPLAZOS  => MI_MSGERROR);

                    END; 


                MI_CONSC := TRUE;            


          ELSE
              MI_RPTA := 0;
              CONTINUE;               
          END IF;

          --Etapa 08


          IF NVL(MI_RS.VALOR_IVA, 0) <> 0 THEN            
            --Etapa 09
            MI_RPTA := 0;
            CONTINUE;
          END IF;

          --***************** CREDITO *******************


          IF MI_CREDITO IS NOT NULL THEN

            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
            MI_CONSC := FALSE;
            MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CREDITO);

            MI_CAMPOS := 'COMPANIA
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
                          , REFERENCIA '; 


            MI_VALORES := ' '''||UN_COMPANIA||'''
                          ,'||MI_ANIO||'
                          ,'''||UN_TIPO||'''
                          ,'||UN_NUMERO||'
                          ,'||MI_CONSECUTIVO||'
                          ,'''||MI_CREDITO||'''
                          ,'''||UN_FECHA||'''
                          ,'''||MI_NATURALEZA||'''
                          ,0
                          ,'||MI_RS.VALOR_IVA||'
                          ,
                          0
                          ,'||NVL(MI_RS.VALOR_IVA, 0)||'
                          ,'''||NVL(MI_RS.CENTRO_COSTO,9999999999)||'''
                          ,'''||MI_RS.TERCERO||'''
                          ,'''||MI_RS.SUCURSAL||'''
                          ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||'''
                          ,''
                          ,'''||UN_DESCRIPCION||'''
                          ,'''||MI_RS.REFERENCIA||''' ';    

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
                         MI_MSGERROR(1).VALOR := UN_NUMERO;
                         MI_MSGERROR(2).CLAVE := 'TIPO';
                         MI_MSGERROR(2).VALOR := UN_TIPO;

                          PCK_ERR_MSG.RAISE_WITH_MSG(
                                                  UN_EXC_COD =>SQLCODE
                                                  ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                  ,UN_REEMPLAZOS  => MI_MSGERROR);

                    END; 


                MI_CONSC := TRUE;                            

          ELSE
           MI_RPTA := 0;
           CONTINUE;
          END IF;

          --Etapa 10

          IF NVL(MI_RS.VALOR_IVA, 0) <> 0 THEN
            --Etapa 11
            MI_RPTA := 0;
            CONTINUE;

          END IF;

     END IF;
     --*************   REGISTRO DEL VALOR RETEFUENTE SI INDICADOR SE ENCUENTRA ACTIVO   *******************

     IF MI_RS.APLICARETEFUENTE NOT IN (0) THEN
      --***************** DEBITO *******************
         IF MI_CLASECONTABLE IN ('V') THEN
              MI_DEBITO := NVL(MI_RS.CUENTADEBITORETE, '');
              MI_CREDITO := NVL(MI_RS.CUENTACREDITORETE, '');
         ELSE
              MI_CREDITO := NVL(MI_RS.CUENTADEBITORETE, '');
              MI_DEBITO := NVL(MI_RS.CUENTACREDITORETE, '');
         END IF;


         IF MI_DEBITO IS NOT NULL THEN

          MI_CONSECUTIVO := MI_CONSECUTIVO + 1 ;
          MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_DEBITO);

            MI_CAMPOS := 'COMPANIA
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
                          , REFERENCIA '; 

            MI_VALORES:= ''''||UN_COMPANIA||'''
                          ,'||MI_ANIO||' 
                          ,'''||UN_TIPO||'''
                          ,'||UN_NUMERO||'
                          ,'||MI_CONSECUTIVO||'
                          ,'''||MI_DEBITO||'''
                          ,'''||UN_FECHA||'''
                          ,'''||MI_NATURALEZA||'''
                          ,'||MI_RS.VALOR_RETEFUENTE||'
                          ,0
                          ,'||NVL(MI_RS.VALOR_RETEFUENTE, 0)||'
                          ,0
                          ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                          ,'''||MI_RS.TERCERO||'''
                          ,'''||MI_RS.SUCURSAL||'''
                          ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||'''
                          ,''
                          ,'''||UN_DESCRIPCION||'''
                          ,'''||MI_RS.REFERENCIA||''' ';   

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
                         MI_MSGERROR(1).VALOR := UN_NUMERO;
                         MI_MSGERROR(2).CLAVE := 'TIPO';
                         MI_MSGERROR(2).VALOR := UN_TIPO;

                          PCK_ERR_MSG.RAISE_WITH_MSG(
                                                  UN_EXC_COD =>SQLCODE
                                                  ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                  ,UN_REEMPLAZOS  => MI_MSGERROR);

                    END;    

          MI_CONSC := TRUE;      


         ELSE
           MI_RPTA := 0;
           CONTINUE;         
         END IF;

         --Etapa 12

        IF NVL(MI_RS.VALOR_RETEFUENTE, 0) <> 0 THEN          
          --Etapa 13
          MI_RPTA := 0;
          CONTINUE;
        END IF;


        --***************** CREDITO *******************

        IF MI_CREDITO IS NOT NULL THEN

         IF MI_CONSC THEN
             MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
            MI_CONSC := FALSE;
          END IF;


          MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CREDITO);

            MI_CAMPOS := 'COMPANIA
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
                          , REFERENCIA '; 


            MI_VALORES := ' '''||UN_COMPANIA||'''
                            ,'||MI_ANIO||'
                            ,'''||UN_TIPO||'''
                            ,'||UN_NUMERO||'
                            ,'||MI_CONSECUTIVO||'
                            ,'''||MI_CREDITO||'''
                            ,'''||UN_FECHA||'''
                            ,'||MI_NATURALEZA||'
                            ,0
                            ,'||MI_RS.VALOR_RETEFUENTE||'
                            ,0
                            ,'||NVL(MI_RS.VALOR_RETEFUENTE, 0)||'
                            ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                            ,'''||MI_RS.TERCERO||'''
                            ,'''||MI_RS.SUCURSAL||'''
                            ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||'''
                            ,''
                            ,'''||UN_DESCRIPCION||'''
                            ,'''||MI_RS.REFERENCIA||''' '; 

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
                         MI_MSGERROR(1).VALOR := UN_NUMERO;
                         MI_MSGERROR(2).CLAVE := 'TIPO';
                         MI_MSGERROR(2).VALOR := UN_TIPO;

                          PCK_ERR_MSG.RAISE_WITH_MSG(
                                                  UN_EXC_COD =>SQLCODE
                                                  ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                  ,UN_REEMPLAZOS  => MI_MSGERROR);

                    END;    

          MI_CONSC := TRUE;                               


        ELSE
          MI_RPTA := 0;  
          CONTINUE; 
        END IF;
        --Etapa 14

        IF NVL(MI_RS.VALOR_RETEFUENTE, 0) <> 0 THEN
          MI_RPTA := 0;
          CONTINUE;
        END IF;

     END IF;

     --*************   REGISTRO DEL VALOR DESCUENTO SI INDICADOR SE ENCUENTRA ACTIVO   *******************

     IF MI_RS.APLICADESCUENTO NOT IN (0) THEN

      --***************** DEBITO *******************

         IF MI_CLASECONTABLE IN ('V') THEN
              MI_DEBITO := NVL(MI_RS.CUENTADEBITODESCUENTO, '');
              MI_CREDITO := NVL(MI_RS.CUENTACREDITODESCUENTO, '');
            ELSE
              MI_CREDITO := NVL(MI_RS.CUENTADEBITODESCUENTO, '');
              MI_DEBITO := NVL(MI_RS.CUENTACREDITODESCUENTO, '');
         END IF;

         IF MI_DEBITO IS NOT NULL THEN
            MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_DEBITO);


            MI_CAMPOS := 'COMPANIA
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
                          , REFERENCIA ';

            MI_VALORES := ' ''' ||UN_COMPANIA||'''
                            ,'||MI_ANIO||'
                            ,'''||UN_TIPO||'''
                            ,'||UN_NUMERO||'
                            ,'||MI_CONSECUTIVO||'
                            ,'||MI_DEBITO||'
                            ,'''||UN_FECHA||'''
                            ,'''||MI_NATURALEZA||'''
                            ,'||MI_RS.VALOR_DESCUENTO||'
                            ,
                            0
                            ,'||NVL(MI_RS.VALOR_DESCUENTO, 0)||'
                            ,0
                            ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                            ,'''||MI_RS.TERCERO||'''
                            ,'''||MI_RS.SUCURSAL||'''
                            ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||'''
                            ,''
                            ,'''||UN_DESCRIPCION||'''
                            ,'''||MI_RS.REFERENCIA||''' ';              

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
                         MI_MSGERROR(1).VALOR := UN_NUMERO;
                         MI_MSGERROR(2).CLAVE := 'TIPO';
                         MI_MSGERROR(2).VALOR := UN_TIPO;

                          PCK_ERR_MSG.RAISE_WITH_MSG(
                                                  UN_EXC_COD =>SQLCODE
                                                  ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                  ,UN_REEMPLAZOS  => MI_MSGERROR);

                    END;    

          MI_CONSC := TRUE;         


         ELSE
          MI_RPTA := 0;
          CONTINUE;          
         END IF;

        --Etapa 16

        IF NVL(MI_RS.VALOR_DESCUENTO, 0) <> 0 THEN        
          --Etapa 17
          MI_RPTA := 0;
          CONTINUE;          
        END IF;

       --***************** CREDITO ******************* 

       IF MI_CREDITO IS NOT NULL THEN

         IF MI_CONSC  THEN
            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
            MI_CONSC := FALSE;
         END IF;

         MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CREDITO);

            MI_CAMPOS := 'COMPANIA
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
                          , REFERENCIA ';


            MI_VALORES := ' '''||UN_COMPANIA||'''
                            ,'||MI_ANIO||'
                            ,'''||UN_TIPO||'''
                            ,'||UN_NUMERO||'
                            ,'||MI_CONSECUTIVO||'
                            ,'''||MI_CREDITO||'''
                            ,'''||UN_FECHA||'''
                            ,'''||MI_NATURALEZA||'''
                            ,0
                            ,'||MI_RS.VALOR_DESCUENTO||'
                            ,0
                            ,'||NVL(MI_RS.VALOR_DESCUENTO, 0)||'
                            ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                            ,'''||MI_RS.TERCERO||'''
                            ,'''||MI_RS.SUCURSAL||'''
                            ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||'''
                            ,''
                            ,'''||UN_DESCRIPCION||'''
                            ,'''||MI_RS.REFERENCIA||''' ';

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
                         MI_MSGERROR(1).VALOR := UN_NUMERO;
                         MI_MSGERROR(2).CLAVE := 'TIPO';
                         MI_MSGERROR(2).VALOR := UN_TIPO;

                          PCK_ERR_MSG.RAISE_WITH_MSG(
                                                  UN_EXC_COD =>SQLCODE
                                                  ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                  ,UN_REEMPLAZOS  => MI_MSGERROR);

                    END;    

          MI_CONSC := TRUE;                               


       ELSE       
        MI_RPTA := 0;
        CONTINUE;        
       END IF;

       --Etapa 18

       IF NVL(MI_RS.VALOR_DESCUENTO, 0) <> 0 THEN       
        --Etapa 19        
          MI_RPTA := 0;
          CONTINUE;          
       END IF;

     END IF;

       --*************   REGISTRO DEL VALOR ICA SI INDICADOR SE ENCUENTRA ACTIVO   *******************

      IF MI_RS.APLICAICA NOT IN (0) THEN

        --***************** DEBITO *******************
          IF MI_CLASECONTABLE IN ('V') THEN
            MI_DEBITO := NVL(MI_RS.CUENTADEBITOICA, '');
            MI_CREDITO := NVL(MI_RS.CUENTACREDITOICA, '');
          ELSE
            MI_CREDITO := NVL(MI_RS.CUENTADEBITOICA, '');
            MI_DEBITO := NVL(MI_RS.CUENTACREDITOICA, '');
          END IF;

            IF MI_DEBITO IS NOT NULL Then

              MI_CONSECUTIVO := MI_CONSECUTIVO + 1;

              MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_DEBITO);

                  MI_CAMPOS := 'COMPANIA
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
                                , REFERENCIA ';


                  MI_VALORES := ' '''||UN_COMPANIA||'''
                                ,'||MI_ANIO||'
                                ,'''||UN_TIPO||'''
                                ,'||UN_NUMERO||',
                                ,'||MI_CONSECUTIVO||'
                                ,'''||MI_DEBITO||'''
                                ,'''||UN_FECHA||'''
                                ,'''||MI_NATURALEZA||'''
                                ,'||MI_RS.VALOR_ICA||'
                                ,0
                                ,'||NVL(MI_RS.VALOR_ICA, 0)||'
                                ,0
                                ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                                ,'''||MI_RS.TERCERO||'''
                                ,'''||MI_RS.SUCURSAL||'''
                                ,'''||NVL(MI_RS.AUXILIAR,9999999999999999)||'''
                                ,''
                                ,'''||UN_DESCRIPCION||'''
                                ,'''||MI_RS.REFERENCIA||''' ';   


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
                         MI_MSGERROR(1).VALOR := UN_NUMERO;
                         MI_MSGERROR(2).CLAVE := 'TIPO';
                         MI_MSGERROR(2).VALOR := UN_TIPO;

                          PCK_ERR_MSG.RAISE_WITH_MSG(
                                                  UN_EXC_COD =>SQLCODE
                                                  ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                  ,UN_REEMPLAZOS  => MI_MSGERROR);

                    END;    

          MI_CONSC := TRUE;                                    

            ELSE
              MI_RPTA := 0;
              CONTINUE;              
            END IF;

          --Etapa 20


        IF NVL(MI_RS.VALOR_ICA, 0) <> 0 THEN
          --Etapa 21
          MI_RPTA := 0;
          CONTINUE;          
        END IF;

       --***************** CREDITO *******************   

       IF MI_CREDITO IS NOT NULL THEN

         IF MI_CONSC THEN
           MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
           MI_CONSC := FALSE;
         END IF;

         MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CREDITO);

            MI_CAMPOS := 'COMPANIA
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
                        , REFERENCIA ';

            MI_VALORES := ' '''||UN_COMPANIA||'''
                            ,'||MI_ANIO||'
                            ,'''||UN_TIPO||'''
                            ,'||UN_NUMERO||'
                            ,'||MI_CONSECUTIVO||'
                            ,'''||MI_CREDITO||'''
                            ,'''||UN_FECHA||'''
                            ,'''||MI_NATURALEZA||'''
                            ,0
                            ,'||MI_RS.VALOR_ICA||'
                            ,0
                            ,'||NVL(MI_RS.VALOR_ICA, 0)||'
                            ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                            ,'''||MI_RS.TERCERO||'''
                            ,'''||MI_RS.SUCURSAL||'''
                            ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||'''
                            ,''
                            ,'''||UN_DESCRIPCION||'''
                            ,'''||MI_RS.REFERENCIA||''' ';        

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
                         MI_MSGERROR(1).VALOR := UN_NUMERO;
                         MI_MSGERROR(2).CLAVE := 'TIPO';
                         MI_MSGERROR(2).VALOR := UN_TIPO;

                          PCK_ERR_MSG.RAISE_WITH_MSG(
                                                  UN_EXC_COD =>SQLCODE
                                                  ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                  ,UN_REEMPLAZOS  => MI_MSGERROR);

                    END;    

          MI_CONSC := TRUE;                                     

       ELSE
        MI_RPTA := 0;
        CONTINUE;        
       END IF;

       --Etapa 22

       IF NVL(MI_RS.VALOR_ICA, 0) <> 0 THEN       
        --Etapa 23       
        MI_RPTA := 0;
        CONTINUE;        
       END IF;

      END IF;
   END LOOP;

    MI_RTAINTERFAZ :=   PCK_CONTABILIZAR.FC_CONTABILIZAR( UN_COMPANIA         => UN_COMPANIA
                                              ,UN_TIPOCOMPROBANTE  => UN_TIPO
                                              ,UN_NUMERO           => UN_NUMERO
                                              ,UN_ANO              => MI_ANIO
                                              ,UN_FECHA            => UN_FECHA
                                              ,UN_TERCERO          => UN_TERCERO
                                              ,UN_SUCURSAL         => UN_SUCURSAL
                                              ,UN_DESCRIPCION      => UN_DESCRIPCION                                            
                                              ,UN_SIMPLE           => 0
                                              ,UN_INDIMPRESION     => 0                                            
                                              ,UN_INTOMITIRPPTAL   => -1
                                              ,UN_CONCILIAR        => 0
                                              ,UN_PLANO            => 0
                                              ,UN_NONETEA          => 0
                                              ,UN_CONTRATISTA      => 0
                                              ,UN_TEXTO            => 'Interface'
                                              ,UN_CONTRATO         => 0
                                              ,UN_TIPOCONTRATO     => ''
                                              ,UN_NRO_DOCUMENTO    => ''
                                              ,UN_ALMDEP           => 0
                                              ,UN_TERCE            => 0
                                              ,UN_RESAUXGEN        => 0
                                              ,UN_USUARIO          => UN_USUARIO);

      MI_RPTA := -1;                                              

  RETURN MI_RPTA;

END FC_INTERFAZCNTDERCONEXION;

--4

FUNCTION FC_INTERFAZCONTABLE
/*
      NAME              : FC_INTERFAZCONTABLE MIGRADO DE ACCESS InterfazContableDerConexion
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 15/08/2018
      TIME              : 11:54 AM
      SOURCE MODULE     : SysmanSF2018.07.06
      DESCRIPTION       :
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 


    @NAME:   manejarInterfazContable
    @METHOD: GET
*/
(
  UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,  
  UN_TIPO             IN PCK_SUBTIPOS.TI_TIPO_COBRO,
  UN_NUMERO           IN DETALLE_COMPROBANTE_CNT.COMPROBANTE%TYPE,
  UN_FECHA            IN DATE,
  UN_TERCERO          IN SF_DETALLE_FACTURA.TERCERO%TYPE,
  UN_SUCURSAL         IN SF_DETALLE_FACTURA.SUCURSAL%TYPE,
  UN_DESCRIPCION      IN COMPROBANTE_CNT.DESCRIPCION%TYPE,
  UN_MANEJAINVENTARIO IN PCK_SUBTIPOS.TI_LOGICO,
  UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN PCK_SUBTIPOS.TI_LOGICO AS 
    MI_RPTA                  PCK_SUBTIPOS.TI_LOGICO;
    MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES               PCK_SUBTIPOS.TI_VALORES;
    MI_MSGERROR              PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CONSC                 BOOLEAN; 
    MI_DEBITO                VARCHAR2(32 CHAR); 
    MI_CREDITO               VARCHAR2(32 CHAR);
    MI_CLASECONTABLE         TIPO_COMPROBANTE.CLASE_CONTABLE%TYPE;
    MI_CONSECUTIVO           NUMBER(5,0);
    MI_NUMERO                DETALLE_COMPROBANTE_CNT.COMPROBANTE%TYPE;
    MI_NUMERO_FACTURA        DETALLE_COMPROBANTE_CNT.COMPROBANTE%TYPE;
    MI_NROCPTE               DETALLE_COMPROBANTE_CNT.COMPROBANTE%TYPE;
    MI_ANIO                  NUMBER(4,0);
    MI_MES                   NUMBER(4,0);
    MI_NATURALEZA            PCK_SUBTIPOS.TI_NATURALEZACONTA;
    MI_RTAINTERFAZ           PCK_SUBTIPOS.TI_RTA_ACME;
    MI_PARMANEJATERCERO      VARCHAR(2 CHAR);
    MI_MANEQUIV4             VARCHAR(2 CHAR);
    MI_STRTERCEROAUX         SF_DETALLE_FACTURA.TERCERO%TYPE;
    MI_STRSUCURSALAUX        SF_DETALLE_FACTURA.SUCURSAL%TYPE;
    MI_CTAPPTAL              DETALLE_COMPROBANTE_CNT.CUENTAPPTAL%TYPE; 
	MI_TIPOENTIDAD           VARCHAR2(32 CHAR); 
    MI_TARIFATERCERO         VARCHAR(2 CHAR);	
    MI_NETEAR                NUMBER(1);										

BEGIN

    MI_CONSECUTIVO := 0;
    MI_ANIO := EXTRACT (YEAR FROM UN_FECHA);
    MI_MES := EXTRACT (MONTH FROM UN_FECHA);

    MI_PARMANEJATERCERO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                    UN_NOMBRE    => 'SF MANEJA CONFIGURACION DE CTA POR TERCERO',
                                    UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                    UN_FECHA_PAR => SYSDATE,
                                    UN_IND_MAYUS =>0);


    MI_MANEQUIV4 := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                    UN_NOMBRE    => 'SF MANEJAR EQUIVALENCIA PRESUPUESTAL EN LA CUENTA 4',
                                    UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                    UN_FECHA_PAR => SYSDATE,
                                    UN_IND_MAYUS =>0);  

    MI_TARIFATERCERO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                    UN_NOMBRE    => 'SF MANEJA TARIFA POR TERCERO',
                                    UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                    UN_FECHA_PAR => SYSDATE,
                                    UN_IND_MAYUS =>0),'NO'); 									

    SELECT CLASE_CONTABLE 
    INTO   MI_CLASECONTABLE
    FROM   TIPO_COMPROBANTE 
    WHERE  COMPANIA = UN_COMPANIA
      AND  CODIGO = UN_TIPO;

    MI_RPTA := -1;

    FOR MI_RS IN (SELECT SF_FACTURA.COMPANIA,
        SF_FACTURA.TIPOCOBRO,
        SF_FACTURA.NUMERO_FACTURA,
        SF_FACTURA.CODIGO_COBRO,
        SF_FACTURA.VALOR_TOTAL,
        SF_FACTURA.OBSERVACIONES,
        SF_FACTURA.TERCERO      TERCEROH,
        SF_FACTURA.SUCURSAL     SUCURSALH,
        SF_FACTURA.CENTRO_COSTO CENTRO_COSTOH,
        SF_FACTURA.AUXILIAR     AUXILIARH,
        SF_CONCEPTOS.CODIGO,
        SF_DETALLE_FACTURA.VALOR_BASE,
        SF_CONCEPTOS.CUENTADEBITOBASE,
        SF_CONCEPTOS.CUENTACREDITOBASE,
        SF_CONCEPTOS.APLICAIVA,
        SF_DETALLE_FACTURA.VALOR_IVA,
        SF_CONCEPTOS.CUENTADEBITOIVA,
        SF_CONCEPTOS.CUENTACREDITOIVA,
        SF_CONCEPTOS.APLICARETEFUENTE,
        SF_DETALLE_FACTURA.VALOR_RETEFUENTE,
        SF_CONCEPTOS.CUENTADEBITORETE,
        SF_CONCEPTOS.CUENTACREDITORETE,
        SF_CONCEPTOS.APLICADESCUENTO,
        SF_DETALLE_FACTURA.VALOR_DESCUENTO,
        SF_CONCEPTOS.CUENTADEBITODESCUENTO,
        SF_CONCEPTOS.CUENTACREDITODESCUENTO,
        SF_CONCEPTOS.APLICAICA,
        SF_CONCEPTOS.TIPODECONCEPTO,
        SF_DETALLE_FACTURA.VALOR_ICA,
        SF_CONCEPTOS.CUENTADEBITOICA,
        SF_CONCEPTOS.CUENTACREDITOICA,
        CTADEBACOMETIDA,
        CTACREDACOMETIDA,
        CTADEBADMONIMP,
        CTACREDADMONIMP,
        CTADEBUTILIDADAIU,
        CTACREDUTILIDADAIU,
        CTADEBIVAUTILIDAD,
        CTACREDIVAUTILIDAD,
        SF_DETALLE_FACTURA.VALOR_COMPRA,
        SF_DETALLE_FACTURA.VALOR_UTILIDAD,
        SF_CONCEPTOS.CUENTADEBITOUTILIDAD,
        SF_CONCEPTOS.CUENTACREDITOUTILIDAD,
        SF_DETALLE_FACTURA.TERCERO,
        SF_DETALLE_FACTURA.SUCURSAL,
        SF_DETALLE_FACTURA.CENTRO_COSTO,
        SF_DETALLE_FACTURA.AUXILIAR,
        SF_DETALLE_FACTURA.REFERENCIA,
        SF_DETALLE_FACTURA.FUENTE_RECURSO,  
        SF_DETALLE_FACTURA.ACOMETIDA_SERVICIO,
        SF_DETALLE_FACTURA.ADMON_IMPREVISTOS,
        SF_DETALLE_FACTURA.UTILIDAD_AIU,
        SF_DETALLE_FACTURA.IVA_UTILIDAD ,
        SF_DETALLE_FACTURA.BASE_GRAVABLE,
        SF_CONCEPTOS.CUENTADEBITOBASE CTADEBITOCREE,
        SF_CONCEPTOS.CUENTACREDITOBASE CTACREDITOCREE,
        SF_CONCEPTOS.APLICAIMPOCONSUMO,
        SF_CONCEPTOS.CTACREDIMPOCONSUMO,
        SF_CONCEPTOS.CTADEBIMPOCONSUMO,
        SF_DETALLE_FACTURA.VALOR_IMPOCONSUMO,
        SF_CONCEPTOS.APLICAAUTORENTA,
        SF_DETALLE_FACTURA.VALOR_AUTORENTA,
        SF_CONCEPTOS.CUENTADEBITOAUTORENTA,
        SF_CONCEPTOS.CUENTACREDITOAUTORENTA,
        SF_CONCEPTOS.APLICAAUTOICA,
        SF_DETALLE_FACTURA.VALOR_AUTOICA,
        SF_CONCEPTOS.CUENTADEBITOAUTOICA,
        SF_CONCEPTOS.CUENTACREDITOAUTOICA,
        SF_CONCEPTOS.CUENTADEBITOPUBLICO,
        SF_CONCEPTOS.CUENTACREDITOPUBLICO,
        SF_CONCEPTOS.CUENTADEBITOPRIVADO,
        SF_CONCEPTOS.CUENTACREDITOPRIVADO,
        SF_CONCEPTOS.CTADEBITORETEIVAACTUAL,
        SF_CONCEPTOS.CTACREDITORETEIVAACTUAL,
        SF_CONCEPTOS.APLICARETEIVA,
        SF_DETALLE_FACTURA.VALOR_RETEIVA, 
        SF_DETALLE_FACTURA.NROCONTRATOSIGEC,
        SF_DETALLE_FACTURA.TIPOCONTRATOSIGEC
      FROM SF_DETALLE_FACTURA
      INNER JOIN SF_CONCEPTOS
      ON SF_DETALLE_FACTURA.COMPANIA   = SF_CONCEPTOS.COMPANIA
      AND SF_DETALLE_FACTURA.TIPOCOBRO = SF_CONCEPTOS.TIPOCOBRO
      AND SF_DETALLE_FACTURA.CONCEPTO  = SF_CONCEPTOS.CODIGO
      AND SF_DETALLE_FACTURA.ANO       = SF_CONCEPTOS.ANO
      INNER JOIN SF_FACTURA
      ON SF_DETALLE_FACTURA.COMPANIA      = SF_FACTURA.COMPANIA
      AND SF_DETALLE_FACTURA.FACTURA      = SF_FACTURA.NUMERO_FACTURA
      AND SF_DETALLE_FACTURA.TIPO_FACTURA = SF_FACTURA.TIPO_FACTURA
      WHERE SF_FACTURA.COMPANIA      = UN_COMPANIA
        AND SF_FACTURA.TIPOCOBRO     = UN_TIPO
        AND SF_FACTURA.CODIGO_COBRO  = UN_NUMERO
        AND SF_FACTURA.TERCERO       = UN_TERCERO
        AND SF_FACTURA.SUCURSAL      = UN_SUCURSAL)LOOP

        MI_NUMERO_FACTURA :=   MI_RS.NUMERO_FACTURA;      

        IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                    UN_NOMBRE    => 'SF NRO FACTURA MANUAL COMO NRO COMPROBANTE',
                                    UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                    UN_FECHA_PAR => SYSDATE,
                                    UN_IND_MAYUS =>0),'NO') = 'SI' THEN

            MI_NUMERO := MI_RS.NUMERO_FACTURA;
        ELSE   
            MI_NUMERO := UN_NUMERO;                      
        END IF;                                    


        FOR MI_RS2  IN (SELECT COMPANIA, ANO, CUENTA, VALOR_DEBITO, VALOR_CREDITO, NATURALEZA 
                         FROM  DETALLE_COMPROBANTE_CNT 
                         WHERE COMPANIA    = UN_COMPANIA 
                           AND ANO         = MI_ANIO
                           AND TIPO_CPTE   = UN_TIPO
                           AND COMPROBANTE = MI_NUMERO
        )LOOP

            IF PCK_FACT_GENERAL_COM3.FC_ACTCONTA0(UN_COMPANIA    => MI_RS2.COMPANIA,  
                          UN_ANO         => MI_RS2.ANO,
                          UN_MES         => MI_MES,
                          UN_A_ID        => MI_RS2.CUENTA,
                          UN_DEB_ANT     => MI_RS2.VALOR_DEBITO,
                          UN_CRE_ANT     => MI_RS2.VALOR_CREDITO,
                          UN_DIF_ANT     => CASE WHEN MI_RS2.NATURALEZA = 'D' THEN  MI_RS2.VALOR_DEBITO - MI_RS2.VALOR_CREDITO ELSE MI_RS2.VALOR_CREDITO - MI_RS2.VALOR_DEBITO END,
                          UN_DEBITO      => 0,
                          UN_CREDITO     => 0,
                          UN_DIFERENCIA  => 0,
                          UN_NATURALEZA  => MI_RS2.NATURALEZA,                          
                          UN_USUARIO     => UN_USUARIO ) IN (0) THEN


                RETURN 0;
            END IF;

            MI_CONDICION :=' COMPANIA  = '''||UN_COMPANIA||'''
                   AND ANO         = '||EXTRACT(YEAR FROM UN_FECHA)||'
                   AND TIPO_CPTE   = '''||UN_TIPO||'''
                   AND COMPROBANTE = '||MI_NUMERO;

            BEGIN
                BEGIN
                   PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'DETALLE_COMPROBANTE_CNT'
                                                        ,UN_ACCION  => 'E'
                                                        ,UN_CONDICION => MI_CONDICION);                                                           

                     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;                                   

                END;

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                         MI_MSGERROR(1).CLAVE := 'NUMERO';
                         MI_MSGERROR(1).VALOR := MI_NUMERO;
                         MI_MSGERROR(2).CLAVE := 'TIPO';
                         MI_MSGERROR(2).VALOR := UN_TIPO;
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                                                  UN_EXC_COD =>SQLCODE
                                                  ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_ELIMIDETALLECNT
                                                  ,UN_REEMPLAZOS  => MI_MSGERROR);

            END;

            MI_CONDICION :='COMPANIA = '''||UN_COMPANIA||'''
                         AND  ANO      = '||EXTRACT(YEAR FROM UN_FECHA)||'
                         AND  TIPO     = '''||UN_TIPO||'''
                         AND  NUMERO   = '||MI_NUMERO;

            BEGIN
                BEGIN
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'COMPROBANTE_CNT'
                                                    ,UN_ACCION  => 'E'
                                                    ,UN_CONDICION => MI_CONDICION);                                                           

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;                                   

                END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                     MI_MSGERROR(1).CLAVE := 'NUMERO';
                     MI_MSGERROR(1).VALOR := MI_NUMERO;
                     MI_MSGERROR(2).CLAVE := 'TIPO';
                     MI_MSGERROR(2).VALOR := UN_TIPO;

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_ELIMIDETALLECNT
                                              ,UN_REEMPLAZOS  => MI_MSGERROR);

            END;   


        END LOOP;
 /*VALIDA QUE EL TERCERO DE LA FACTURA Y OBJETO COBRO SEA PUBLICO O PRIVADO*/    
        BEGIN    
  IF MI_TARIFATERCERO = 'SI' THEN 										 
            SELECT CASE 
                    WHEN TIPOENTIDAD = 'Privada' THEN 1 
                    WHEN TIPOENTIDAD = 'Publica' THEN 2 
                    ELSE 0  END AS TIPOENTIDAD  
                    INTO MI_TIPOENTIDAD
                    FROM TERCERO
            INNER JOIN SF_OBJETO_COBRO
                  ON SF_OBJETO_COBRO.COMPANIA = TERCERO.COMPANIA
                  AND SF_OBJETO_COBRO.TERCERO = TERCERO.NIT
                  AND SF_OBJETO_COBRO.SUCURSAL = TERCERO.SUCURSAL
            INNER JOIN SF_FACTURA
                  ON SF_OBJETO_COBRO.COMPANIA      = SF_FACTURA.COMPANIA
                  AND SF_OBJETO_COBRO.ANO      = SF_FACTURA.ANO
                  AND SF_OBJETO_COBRO.TERCERO      = SF_FACTURA.TERCERO  
                  AND SF_OBJETO_COBRO.CODIGO_COBRO = SF_FACTURA.CODIGO_COBRO
                  AND SF_OBJETO_COBRO.TIPOCOBRO = SF_FACTURA.TIPOCOBRO
                 WHERE SF_FACTURA.COMPANIA      = UN_COMPANIA
                    AND SF_FACTURA.TIPOCOBRO     = UN_TIPO
                    AND SF_FACTURA.CODIGO_COBRO  = UN_NUMERO
                    AND SF_FACTURA.TERCERO       = UN_TERCERO
                    AND SF_FACTURA.SUCURSAL      = UN_SUCURSAL;
             PCK_FACT_GENERAL_COM3.GL_TIPOENTIDAD :=  MI_TIPOENTIDAD;
     END IF;             
        END;
      --Etapa 02

        MI_CONSC := FALSE;
        
            IF MI_CLASECONTABLE IN ('V')THEN  
                IF MI_TARIFATERCERO = 'SI' THEN
                    IF MI_TIPOENTIDAD = 1 THEN
                        MI_DEBITO   := NVL(MI_RS.CUENTADEBITOPRIVADO,'');
                        MI_CREDITO  := NVL(MI_RS.CUENTACREDITOPRIVADO,'');       
                    ELSIF MI_TIPOENTIDAD = 2 THEN   
                        MI_DEBITO   := NVL(MI_RS.CUENTADEBITOPUBLICO,'');
                        MI_CREDITO  := NVL(MI_RS.CUENTACREDITOPUBLICO,''); 
                    ELSE
                        MI_DEBITO   := NVL(MI_RS.CUENTADEBITOBASE,'');
                        MI_CREDITO  := NVL(MI_RS.CUENTACREDITOBASE,'');  
                    END IF;
                ELSE
                    MI_DEBITO   := NVL(MI_RS.CUENTADEBITOBASE,'');
                    MI_CREDITO  := NVL(MI_RS.CUENTACREDITOBASE,'');
                END IF;            
            ELSE  
                IF MI_TARIFATERCERO = 'SI' THEN
                    IF MI_TIPOENTIDAD = 1 THEN
                        MI_CREDITO  := NVL(MI_RS.CUENTADEBITOPRIVADO,'');
                        MI_DEBITO   := NVL(MI_RS.CUENTACREDITOPRIVADO,'');       
                    ELSIF MI_TIPOENTIDAD = 2 THEN   
                        MI_CREDITO   := NVL(MI_RS.CUENTADEBITOPUBLICO,'');
                        MI_DEBITO  := NVL(MI_RS.CUENTACREDITOPUBLICO,''); 
                    ELSE
                        MI_CREDITO   := NVL(MI_RS.CUENTADEBITOBASE,'');
                        MI_DEBITO  := NVL(MI_RS.CUENTACREDITOBASE,'');  
                    END IF;    
                ELSE
                    MI_CREDITO  := NVL(MI_RS.CUENTADEBITOBASE,'');
                    MI_DEBITO   := NVL(MI_RS.CUENTACREDITOBASE,'');
                END IF;
            END IF;


      -- '********************************************************INICIO INVENTARIO ***************************************************** 

    IF UN_MANEJAINVENTARIO NOT IN (0) THEN
      -- '*************   REGISTRO DEL VALOR DE ACOMETIDA  *******************
      -- '*****************          DEBITO                *******************
        IF NVL(MI_RS.VALOR_COMPRA,0) NOT IN (0)THEN        
            IF MI_DEBITO IS NOT NULL THEN 
                BEGIN
                    BEGIN
                        SELECT RUBRO
                        INTO MI_CTAPPTAL
                        FROM PLAN_PPTAL_CUENTACNT          
                        WHERE COMPANIA          = UN_COMPANIA
                        AND ANO               = MI_ANIO
                        AND CUENTA_CONTABLE   = MI_DEBITO;

                        
                        EXCEPTION   WHEN NO_DATA_FOUND THEN
                                        MI_CTAPPTAL := NULL;
                                    WHEN TOO_MANY_ROWS THEN
                                        RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                    END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
                        MI_MSGERROR(1).CLAVE := 'CUENTA';
                        MI_MSGERROR(1).VALOR := MI_DEBITO;
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                   UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_CTACNT_VARIOSRU
                                                    ,UN_REEMPLAZOS  => MI_MSGERROR);
                END;
                MI_CONSECUTIVO  :=  MI_CONSECUTIVO + 1 ;
                MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_DEBITO);

                IF MI_PARMANEJATERCERO  = 'SI' THEN

        
                      SELECT TERCERO, SUCURSAL 
                      INTO  MI_STRTERCEROAUX,
                            MI_STRSUCURSALAUX
                      FROM SF_CUENTAS_AUXTERCERO 
                      WHERE  COMPANIA = UN_COMPANIA 
                        AND ANO = MI_ANIO 
                        AND TIPOCOBRO = UN_TIPO
                        AND CONCEPTO = MI_RS.CODIGO
                        AND CUENTA = MI_DEBITO ;

        
                 ELSE
                    MI_STRTERCEROAUX  := MI_RS.TERCERO;
                    MI_STRSUCURSALAUX := MI_RS.SUCURSAL;
                END IF;

                MI_CAMPOS := 'COMPANIA
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

                MI_VALORES:= ' '''||UN_COMPANIA||''',
                         '||MI_ANIO||',
                         '''||UN_TIPO||'''
                         ,'||MI_NUMERO||'
                         ,'||MI_CONSECUTIVO||'
                         ,'''||MI_DEBITO||'''
                         ,'''||UN_FECHA||'''
                         ,'''||MI_NATURALEZA||'''
                         ,'||NVL(MI_RS.VALOR_COMPRA,0)||'
                         ,0
                         ,'||NVL(MI_RS.VALOR_COMPRA,0)||'
                         ,0
                         ,'''||NVL(MI_RS.CENTRO_COSTO,9999999999)||'''
                         ,'''||MI_RS.TERCERO||'''
                         ,'''||MI_RS.SUCURSAL||'''
                         ,'''||NVL(MI_RS.AUXILIAR,9999999999999999)||'''
                         ,'''||MI_CTAPPTAL||'''
                         ,'''||UN_DESCRIPCION||'''
                         ,'||NVL(MI_RS.BASE_GRAVABLE,0)||'
                         ,'''||MI_RS.REFERENCIA||'''
                         ,'''||MI_RS.FUENTE_RECURSO||''' ';

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
                   MI_MSGERROR(2).VALOR := UN_TIPO;

                   PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                        ,UN_REEMPLAZOS  => MI_MSGERROR);

                END;


                MI_CONSC := TRUE;             

            ELSE       

                MI_RPTA := 0;

            END IF;


            --'***************** CREDITO *******************  
            IF MI_CREDITO IS NOT NULL THEN
                BEGIN
                    BEGIN
                        SELECT RUBRO
                        INTO MI_CTAPPTAL
                        FROM PLAN_PPTAL_CUENTACNT          
                        WHERE COMPANIA          = UN_COMPANIA
                        AND ANO               = MI_ANIO
                        AND CUENTA_CONTABLE   = MI_CREDITO;

                        
                        EXCEPTION   WHEN NO_DATA_FOUND THEN
                                        MI_CTAPPTAL := NULL;
                                    WHEN TOO_MANY_ROWS THEN
                                        RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                    END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
                        MI_MSGERROR(1).CLAVE := 'CUENTA';
                        MI_MSGERROR(1).VALOR := MI_CREDITO;
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                   UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_CTACNT_VARIOSRU
                                                    ,UN_REEMPLAZOS  => MI_MSGERROR);
                END;


               
          
                IF MI_PARMANEJATERCERO  = 'SI'THEN

                  SELECT TERCERO, SUCURSAL 
                  INTO  MI_STRTERCEROAUX,
                        MI_STRSUCURSALAUX
                  FROM SF_CUENTAS_AUXTERCERO 
                  WHERE  COMPANIA = UN_COMPANIA 
                    AND ANO = MI_ANIO 
                    AND TIPOCOBRO = UN_TIPO
                    AND CONCEPTO = MI_RS.CODIGO
                    AND CUENTA = MI_DEBITO ;

                ELSE
                    MI_STRTERCEROAUX :=  MI_RS.TERCERO;
                    MI_STRSUCURSALAUX := MI_RS.SUCURSAL;
                END IF;

                IF MI_CONSC THEN
                    MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
                    MI_CONSC := FALSE;
                END IF;

                MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CREDITO);
                MI_CAMPOS := 'COMPANIA
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

                MI_VALORES := ''''||UN_COMPANIA||'''
                         , '||MI_ANIO||'
                         ,'''||UN_TIPO||'''
                         ,'||MI_NUMERO||'
                         ,'||MI_CONSECUTIVO||'
                         ,'''||MI_CREDITO||'''
                         ,'''||UN_FECHA||'''
                         ,'''||MI_NATURALEZA||'''
                         ,0
                         ,'||MI_RS.VALOR_COMPRA||'
                         ,0
                         ,'||NVL(MI_RS.VALOR_COMPRA, 0)||'
                         ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                         ,'''||MI_RS.TERCERO||'''
                         ,'''||MI_RS.SUCURSAL||'''
                         ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||''' 
                         ,'''||CASE WHEN MI_MANEQUIV4 = 'SI' THEN NVL(MI_CTAPPTAL,'') ELSE '' END||'''
                         ,'''||UN_DESCRIPCION||'''
                         ,'||NVL(MI_RS.BASE_GRAVABLE,0)||'
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
                       MI_MSGERROR(2).VALOR := UN_TIPO;

        
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                                UN_EXC_COD =>SQLCODE
                                                ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                ,UN_REEMPLAZOS  => MI_MSGERROR);

                END;                         

                MI_CONSC := TRUE;

            ELSE 
                MI_RPTA := 0;              
            END IF;
        ELSE
            MI_RPTA := 0;
        END IF;

          --*************   REGISTRO DEL VALOR DE LA UTILIDAD PARA EL CASO DONDE SE MANEJA ALMACEN  *******************
          --***************                                   DEBITO                               *******************

          
        IF NVL(MI_RS.VALOR_UTILIDAD,0) <> 0 THEN    
            IF MI_RS.TIPODECONCEPTO IN ('C') THEN
                IF MI_CLASECONTABLE IN ('V') THEN
                    MI_DEBITO := NVL(MI_RS.CUENTADEBITOUTILIDAD, '');
                    MI_CREDITO := NVL(MI_RS.CUENTACREDITOUTILIDAD,'');
                ELSE
                    MI_CREDITO := NVL(MI_RS.CUENTADEBITOUTILIDAD,'');
                    MI_DEBITO := NVL(MI_RS.CUENTACREDITOUTILIDAD,'');
                END IF;

                IF MI_DEBITO IS NOT NULL THEN
                    BEGIN
                        BEGIN
                           SELECT RUBRO
                                  INTO MI_CTAPPTAL
                                FROM PLAN_PPTAL_CUENTACNT          
                                WHERE COMPANIA          = UN_COMPANIA
                                  AND ANO               = MI_ANIO
                                  AND CUENTA_CONTABLE   = MI_DEBITO;
                            EXCEPTION WHEN NO_DATA_FOUND THEN
                                    MI_CTAPPTAL := NULL;
                                  WHEN TOO_MANY_ROWS THEN
                                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                        END;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
                        MI_MSGERROR(1).CLAVE := 'CUENTA';
                        MI_MSGERROR(1).VALOR := MI_DEBITO;
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                   UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_CTACNT_VARIOSRU
                                                    ,UN_REEMPLAZOS  => MI_MSGERROR);
                    END;

              
                    MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_DEBITO);

                    IF MI_PARMANEJATERCERO  = 'SI' THEN

        
                        SELECT TERCERO, SUCURSAL 
                        INTO  MI_STRTERCEROAUX,
                              MI_STRSUCURSALAUX
                        FROM SF_CUENTAS_AUXTERCERO 
                        WHERE  COMPANIA = UN_COMPANIA 
                          AND ANO = MI_ANIO 
                          AND TIPOCOBRO = UN_TIPO
                          AND CONCEPTO = MI_RS.CODIGO
                          AND CUENTA = MI_DEBITO ;

        
                    ELSE
                        MI_STRTERCEROAUX :=  MI_RS.TERCERO;
                        MI_STRSUCURSALAUX := MI_RS.SUCURSAL;
                    END IF;

                    MI_CONSECUTIVO := MI_CONSECUTIVO + 1; 

                    MI_CAMPOS := 'COMPANIA
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

                    MI_VALORES := ' '''||UN_COMPANIA||'''
                          ,'||MI_ANIO||'
                          ,'''||UN_TIPO||'''
                          ,'||MI_NUMERO||'
                          ,'||MI_CONSECUTIVO||'
                          ,'''||MI_DEBITO||'''
                          ,'''||UN_FECHA||'''
                          ,'''||MI_NATURALEZA||'''
                          ,'||MI_RS.ADMON_IMPREVISTOS||'
                          ,0
                          ,'||NVL(MI_RS.ADMON_IMPREVISTOS, 0)||'
                          ,0
                          ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                          ,'''||MI_RS.TERCERO||'''
                          ,'''||MI_RS.SUCURSAL||'''
                          ,'''||NVL(MI_RS.AUXILIAR,9999999999999999)||'''
                          ,'''||MI_CTAPPTAL||'''
                          ,'''||UN_DESCRIPCION||'''
                          ,'||NVL(MI_RS.BASE_GRAVABLE, 0)||'
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
                           MI_MSGERROR(2).VALOR := UN_TIPO;

            
                            PCK_ERR_MSG.RAISE_WITH_MSG(
                                                    UN_EXC_COD =>SQLCODE
                                                    ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                    ,UN_REEMPLAZOS  => MI_MSGERROR);

            
                    END;                                      

                    MI_CONSC := TRUE;

                ELSE
                    MI_RPTA := 0;
                END IF;

                --Etapa 04

                --***************** CREDITO *******************
                IF MI_CREDITO IS NOT NULL THEN

                    BEGIN
                        BEGIN
                           SELECT RUBRO
                                  INTO MI_CTAPPTAL
                                FROM PLAN_PPTAL_CUENTACNT          
                                WHERE COMPANIA          = UN_COMPANIA
                                  AND ANO               = MI_ANIO
                                  AND CUENTA_CONTABLE   = MI_CREDITO;
                            EXCEPTION WHEN NO_DATA_FOUND THEN
                                MI_CTAPPTAL := NULL;
                                     WHEN TOO_MANY_ROWS THEN
                                RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                        END;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
                        MI_MSGERROR(1).CLAVE := 'CUENTA';
                        MI_MSGERROR(1).VALOR := MI_CREDITO;
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                   UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_CTACNT_VARIOSRU
                                                    ,UN_REEMPLAZOS  => MI_MSGERROR);
                    END;

                    MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CREDITO);

                    IF MI_PARMANEJATERCERO  = 'SI'THEN

                        SELECT TERCERO, SUCURSAL 
                        INTO  MI_STRTERCEROAUX,
                              MI_STRSUCURSALAUX
                        FROM SF_CUENTAS_AUXTERCERO 
                        WHERE  COMPANIA = UN_COMPANIA 
                          AND ANO = MI_ANIO 
                          AND TIPOCOBRO = UN_TIPO
                          AND CONCEPTO = MI_RS.CODIGO
                          AND CUENTA = MI_DEBITO ;

                    ELSE
                        MI_STRTERCEROAUX :=  MI_RS.TERCERO;
                        MI_STRSUCURSALAUX := MI_RS.SUCURSAL;
                    END IF;

                     IF MI_CONSC THEN
                        MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
                        MI_CONSC := FALSE;
                     END IF ;


                    MI_CAMPOS := 'COMPANIA
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

                    MI_VALORES := ' '''||UN_COMPANIA||'''
                             , '||MI_ANIO||'
                             ,'''||UN_TIPO||'''
                             ,'||MI_NUMERO||'
                             ,'||MI_CONSECUTIVO||'
                             ,'''||MI_CREDITO||'''
                             ,'''||UN_FECHA||'''
                             ,'''||MI_NATURALEZA||'''
                             ,0
                             ,'||MI_RS.VALOR_UTILIDAD||'
                             ,0
                             ,'||NVL(MI_RS.VALOR_UTILIDAD, 0)||'
                             ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                             ,'''||MI_STRTERCEROAUX||'''
                             ,'''||MI_STRSUCURSALAUX||'''
                             ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||'''
                             ,'''||CASE WHEN MI_MANEQUIV4 = 'SI' THEN NVL(MI_CTAPPTAL,'') ELSE '' END||'''
                             ,'''||UN_DESCRIPCION||'''
                             ,'||NVL(MI_RS.BASE_GRAVABLE, 0)||'
                             ,'''||MI_RS.REFERENCIA||'''
                             ,'''||MI_RS.FUENTE_RECURSO||'''' ;            


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
                       MI_MSGERROR(2).VALOR := UN_TIPO;

        
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                                UN_EXC_COD =>SQLCODE
                                                ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                ,UN_REEMPLAZOS  => MI_MSGERROR);

                    END;            

                    MI_CONSC := TRUE;
                ELSE
                    MI_RPTA := 0;                    
                END IF;
            END IF;    
        END IF;
    ELSE

      
            IF MI_CLASECONTABLE IN ('V')  THEN   
                IF MI_TARIFATERCERO = 'SI' THEN
                    IF MI_TIPOENTIDAD = 1 THEN
                        MI_DEBITO   := NVL(MI_RS.CUENTADEBITOPRIVADO,'');
                        MI_CREDITO  := NVL(MI_RS.CUENTACREDITOPRIVADO,'');       
                    ELSIF MI_TIPOENTIDAD = 2 THEN   
                        MI_DEBITO   := NVL(MI_RS.CUENTADEBITOPUBLICO,'');
                        MI_CREDITO  := NVL(MI_RS.CUENTACREDITOPUBLICO,''); 
                    ELSE
                        MI_DEBITO   := NVL(MI_RS.CUENTADEBITOBASE,'');
                        MI_CREDITO  := NVL(MI_RS.CUENTACREDITOBASE,'');  
                    END IF;
                ELSE
                    MI_DEBITO   := NVL(MI_RS.CUENTADEBITOBASE,'');
                    MI_CREDITO  := NVL(MI_RS.CUENTACREDITOBASE,'');
                END IF;            
            ELSE  
                IF MI_TARIFATERCERO = 'SI' THEN
                    IF MI_TIPOENTIDAD = 1 THEN
                        MI_CREDITO  := NVL(MI_RS.CUENTADEBITOPRIVADO,'');
                        MI_DEBITO   := NVL(MI_RS.CUENTACREDITOPRIVADO,'');       
                    ELSIF MI_TIPOENTIDAD = 2 THEN   
                        MI_CREDITO   := NVL(MI_RS.CUENTADEBITOPUBLICO,'');
                        MI_DEBITO  := NVL(MI_RS.CUENTACREDITOPUBLICO,''); 
                    ELSE
                        MI_CREDITO   := NVL(MI_RS.CUENTADEBITOBASE,'');
                        MI_DEBITO  := NVL(MI_RS.CUENTACREDITOBASE,'');  
                    END IF;    
                ELSE
                    MI_CREDITO  := NVL(MI_RS.CUENTADEBITOBASE,'');
                    MI_DEBITO   := NVL(MI_RS.CUENTACREDITOBASE,'');
                END IF;
            END IF;

       --*************   REGISTRO DEL VALOR BASE   *******************
       --*****************        DEBITO        *******************
      IF NVL(MI_RS.VALOR_BASE, 0) <> 0 THEN
          IF MI_DEBITO IS NOT NULL THEN

             --MI_CTAPPTAL := '';
             BEGIN
                    BEGIN
                       SELECT RUBRO
                              INTO MI_CTAPPTAL
                            FROM PLAN_PPTAL_CUENTACNT          
                            WHERE COMPANIA          = UN_COMPANIA
                              AND ANO               = MI_ANIO
                              AND CUENTA_CONTABLE   = MI_DEBITO
                              AND ROWNUM = 1;
                    EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_CTAPPTAL := NULL;
                             WHEN TOO_MANY_ROWS THEN
                        RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
                MI_MSGERROR(1).CLAVE := 'CUENTA';
                MI_MSGERROR(1).VALOR := MI_DEBITO;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_CTACNT_VARIOSRU
                                            ,UN_REEMPLAZOS  => MI_MSGERROR);
            END;

              
             MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_DEBITO);

             IF MI_PARMANEJATERCERO  = 'SI' THEN

                SELECT TERCERO, SUCURSAL 
                INTO  MI_STRTERCEROAUX,
                      MI_STRSUCURSALAUX
                FROM SF_CUENTAS_AUXTERCERO 
                WHERE  COMPANIA = UN_COMPANIA 
                  AND ANO = MI_ANIO 
                  AND TIPOCOBRO = UN_TIPO
                  AND CONCEPTO = MI_RS.CODIGO
                  AND CUENTA = MI_DEBITO ;

             ELSE
                MI_STRTERCEROAUX :=  MI_RS.TERCERO;
                MI_STRSUCURSALAUX := MI_RS.SUCURSAL;
             END IF;

             MI_CONSECUTIVO := MI_CONSECUTIVO + 1; 

                MI_CAMPOS := 'COMPANIA
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

                MI_VALORES := ' '''||UN_COMPANIA||'''
                               ,'||MI_ANIO||'
                               ,'''||UN_TIPO||'''
                               ,'||MI_NUMERO||'
                               ,'||MI_CONSECUTIVO||'
                               ,'''||MI_DEBITO||'''
                               ,'''||UN_FECHA||'''
                               ,'''||MI_NATURALEZA||'''
                               ,'||(NVL(MI_RS.VALOR_BASE, 0) - NVL(MI_RS.VALOR_DESCUENTO, 0))||'
                               ,0
                               ,'||(NVL(MI_RS.VALOR_BASE, 0) - NVL(MI_RS.VALOR_DESCUENTO, 0))||'
                               ,0
                               ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                               ,'''||MI_STRTERCEROAUX||'''
                               ,'''||MI_STRSUCURSALAUX||''' 
                               ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||'''
                               ,'''||NVL(MI_CTAPPTAL, '')||'''
                               ,'''||UN_DESCRIPCION||'''
                               ,'||NVL(MI_RS.BASE_GRAVABLE, 0)||'
                               ,'''||MI_RS.REFERENCIA||'''
                               ,'''||MI_RS.FUENTE_RECURSO||'''';            
                               --MOD JM CC 3896 (TOMAR EN CUENTA LOS DESCUENTOS)

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
               MI_MSGERROR(2).VALOR := UN_TIPO;

                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                        ,UN_REEMPLAZOS  => MI_MSGERROR);

          END;       

          MI_CONSC := TRUE;                                        

        ELSE

          MI_RPTA := 0;    

        END IF;

       --***************** CREDITO *******************

          IF MI_CREDITO IS NOT NULL THEN

                BEGIN
                    BEGIN
                       SELECT RUBRO
                              INTO MI_CTAPPTAL
                            FROM PLAN_PPTAL_CUENTACNT          
                            WHERE COMPANIA          = UN_COMPANIA
                              AND ANO               = MI_ANIO
                              AND CUENTA_CONTABLE   = MI_CREDITO;
                    EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_CTAPPTAL := NULL;
                             WHEN TOO_MANY_ROWS THEN
                        RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
                MI_MSGERROR(1).CLAVE := 'CUENTA';
                MI_MSGERROR(1).VALOR := MI_CREDITO;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_CTACNT_VARIOSRU
                                            ,UN_REEMPLAZOS  => MI_MSGERROR);
                END;

             MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CREDITO);

             IF MI_PARMANEJATERCERO  = 'SI'THEN

                SELECT TERCERO, SUCURSAL 
                INTO  MI_STRTERCEROAUX,
                      MI_STRSUCURSALAUX
                FROM SF_CUENTAS_AUXTERCERO 
                WHERE  COMPANIA = UN_COMPANIA 
                  AND ANO = MI_ANIO 
                  AND TIPOCOBRO = UN_TIPO
                  AND CONCEPTO = MI_RS.CODIGO
                  AND CUENTA = MI_DEBITO ;

             ELSE
                MI_STRTERCEROAUX :=  MI_RS.TERCERO;
                MI_STRSUCURSALAUX := MI_RS.SUCURSAL;
             END IF;

             IF MI_CONSC THEN
                MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
                MI_CONSC := FALSE;
             END IF ;

                MI_CAMPOS := 'COMPANIA
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

                MI_VALORES := ' '''||UN_COMPANIA||'''
                              ,'||MI_ANIO||'
                              ,'''||UN_TIPO||'''
                              ,'||MI_NUMERO||'
                              ,'||MI_CONSECUTIVO||'
                              ,'''||MI_CREDITO||'''
                              ,'''||UN_FECHA||'''
                              ,'''||MI_NATURALEZA||'''
                              ,0
                              ,'||(NVL(MI_RS.VALOR_BASE, 0) - NVL(MI_RS.VALOR_DESCUENTO, 0))||'
                              ,0
                              ,'||(NVL(MI_RS.VALOR_BASE, 0) - NVL(MI_RS.VALOR_DESCUENTO, 0))||'
                              ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                              ,'''||MI_STRTERCEROAUX||'''
                              ,'''||MI_STRSUCURSALAUX||'''
                              ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||'''
                              ,'''||CASE WHEN MI_MANEQUIV4 = 'SI' THEN NVL(MI_CTAPPTAL,'') ELSE '' END||'''
                              ,'''||UN_DESCRIPCION||'''
                              ,'||NVL(MI_RS.BASE_GRAVABLE, 0)||'
                              ,'''||MI_RS.REFERENCIA||'''
                              ,'''||MI_RS.FUENTE_RECURSO||'''' ;
                              -- MOD JM CC 3896 tomar en cuenta los descuentos             

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
               MI_MSGERROR(2).VALOR := UN_TIPO;

                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                        ,UN_REEMPLAZOS  => MI_MSGERROR);

          END;              

          MI_CONSC := TRUE;

        ELSE

          MI_RPTA := 0;

        END IF;                   

        --Etapa 06

      ELSE 

          MI_RPTA := 0;

      END IF;

 END IF;--FIN DE LA EVALUACION DE SI EL TIPO DE COBRO MANEJA INVENTARIO

      --******************************************************** FIN INVENTARIO *******************************************************

      --*************   REGISTRO DEL VALOR IVA SI INDICADOR SE ENCUENTRA ACTIVO   *******************

   IF MI_RS.APLICAIVA NOT IN (0)THEN
      IF NVL(MI_RS.VALOR_IVA, 0) <> 0 THEN 
        --***************** DEBITO *******************
           IF MI_CLASECONTABLE IN ('V') THEN
              MI_DEBITO := NVL(MI_RS.CUENTADEBITOIVA, '');
              MI_CREDITO := NVL(MI_RS.CUENTACREDITOIVA, '');
           ELSE
              MI_CREDITO := NVL(MI_RS.CUENTADEBITOIVA, '');
              MI_DEBITO := NVL(MI_RS.CUENTACREDITOIVA, '');
           END IF;     

           IF MI_DEBITO IS NOT NULL THEN

             --MI_CTAPPTAL := '';
                BEGIN
                    BEGIN
                       SELECT RUBRO
                              INTO MI_CTAPPTAL
                            FROM PLAN_PPTAL_CUENTACNT          
                            WHERE COMPANIA          = UN_COMPANIA
                              AND ANO               = MI_ANIO
                              AND CUENTA_CONTABLE   = MI_DEBITO;
                    EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_CTAPPTAL := NULL;
                             WHEN TOO_MANY_ROWS THEN
                        RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
                MI_MSGERROR(1).CLAVE := 'CUENTA';
                MI_MSGERROR(1).VALOR := MI_DEBITO;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_CTACNT_VARIOSRU
                                            ,UN_REEMPLAZOS  => MI_MSGERROR);
                END;

              
             MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_DEBITO);

             IF MI_PARMANEJATERCERO  = 'SI' THEN

                SELECT TERCERO, SUCURSAL 
                INTO  MI_STRTERCEROAUX,
                      MI_STRSUCURSALAUX
                FROM SF_CUENTAS_AUXTERCERO 
                WHERE  COMPANIA = UN_COMPANIA 
                  AND ANO = MI_ANIO 
                  AND TIPOCOBRO = UN_TIPO
                  AND CONCEPTO = MI_RS.CODIGO
                  AND CUENTA = MI_DEBITO ;

             ELSE
                MI_STRTERCEROAUX :=  MI_RS.TERCERO;
                MI_STRSUCURSALAUX := MI_RS.SUCURSAL;
             END IF;

             MI_CONSECUTIVO := MI_CONSECUTIVO + 1;             

                MI_CAMPOS := 'COMPANIA
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

                MI_VALORES := ' '''||UN_COMPANIA||'''
                               ,'||MI_ANIO||'
                               ,'''||UN_TIPO||'''
                               ,'||MI_NUMERO||'
                               ,'||MI_CONSECUTIVO||'
                               ,'||MI_DEBITO||'
                               ,'''||UN_FECHA||'''
                               ,'''||MI_NATURALEZA||'''
                               ,'||MI_RS.VALOR_IVA||'
                               ,0
                               ,'||NVL(MI_RS.VALOR_IVA, 0)||'
                               ,0
                               ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                               ,'''||MI_STRTERCEROAUX||'''
                               ,'''||MI_STRSUCURSALAUX||'''
                               ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||''' 
                               ,'''||NVL(MI_CTAPPTAL, '')||'''
                               ,'''||UN_DESCRIPCION||'''
                               ,'||NVL(MI_RS.BASE_GRAVABLE, 0)||'
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
                       MI_MSGERROR(2).VALOR := UN_TIPO;

                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                                UN_EXC_COD =>SQLCODE
                                                ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                ,UN_REEMPLAZOS  => MI_MSGERROR);

                  END;               

            MI_CONSC := TRUE;

           ELSE

            MI_RPTA := 0;

           END IF;


          --***************** CREDITO ******************* 

       IF MI_CREDITO IS NOT NULL THEN
          BEGIN
            BEGIN
               SELECT RUBRO
                      INTO MI_CTAPPTAL
                    FROM PLAN_PPTAL_CUENTACNT          
                    WHERE COMPANIA          = UN_COMPANIA
                      AND ANO               = MI_ANIO
                      AND CUENTA_CONTABLE   = MI_CREDITO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_CTAPPTAL := NULL;
                         WHEN TOO_MANY_ROWS THEN
                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
            MI_MSGERROR(1).CLAVE := 'CUENTA';
            MI_MSGERROR(1).VALOR := MI_CREDITO;
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_CTACNT_VARIOSRU
                                        ,UN_REEMPLAZOS  => MI_MSGERROR);
          END;         

             MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CREDITO);

             IF MI_PARMANEJATERCERO  = 'SI'THEN

                SELECT TERCERO, SUCURSAL 
                INTO  MI_STRTERCEROAUX,
                      MI_STRSUCURSALAUX
                FROM SF_CUENTAS_AUXTERCERO 
                WHERE  COMPANIA = UN_COMPANIA 
                  AND ANO = MI_ANIO 
                  AND TIPOCOBRO = UN_TIPO
                  AND CONCEPTO = MI_RS.CODIGO
                  AND CUENTA = MI_DEBITO ;

             ELSE
                MI_STRTERCEROAUX :=  MI_RS.TERCERO;
                MI_STRSUCURSALAUX := MI_RS.SUCURSAL;
             END IF;

             IF MI_CONSC THEN
                MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
                MI_CONSC := FALSE;
             END IF ;       

                MI_CAMPOS := 'COMPANIA
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

                MI_VALORES := ' '''||UN_COMPANIA||'''
                              ,'||MI_ANIO||'
                              ,'''||UN_TIPO||'''
                              ,'||MI_NUMERO||'
                              ,'||MI_CONSECUTIVO||'
                              ,'''||MI_CREDITO||'''
                              ,'''||UN_FECHA||'''
                              ,'''||MI_NATURALEZA||'''
                              ,0
                              ,'||MI_RS.VALOR_IVA||'
                              ,0
                              ,'||NVL(MI_RS.VALOR_IVA, 0)||'
                              ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                              ,'''||MI_STRTERCEROAUX||'''
                              ,'''||MI_STRSUCURSALAUX||'''
                              ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||'''
                              ,'''||CASE WHEN MI_MANEQUIV4 = 'SI' THEN NVL(MI_CTAPPTAL,'') ELSE '' END||'''
                              ,'''||UN_DESCRIPCION||'''
                              ,'||NVL(MI_RS.BASE_GRAVABLE, 0)||'
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
                       MI_MSGERROR(2).VALOR := UN_TIPO;

                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                                UN_EXC_COD =>SQLCODE
                                                ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                ,UN_REEMPLAZOS  => MI_MSGERROR);

                  END;                             

             MI_CONSC := TRUE;       
       ELSE

        MI_RPTA := 0;

       END IF;

       --Etapa 10  


      ELSE  
          --Etapa 11
          MI_RPTA := 0;

      END IF;

   END IF;      

  /*INI_TICKET7723036 MPEREZ*/
   --*************   REGISTRO DEL VALOR AUTORENTA SI INDICADOR SE ENCUENTRA ACTIVO   *******************
   --***************** DEBITO *******************
   IF MI_RS.APLICAAUTORENTA NOT IN (0)THEN
      IF NVL(MI_RS.VALOR_AUTORENTA, 0) <> 0 THEN 
        --***************** DEBITO *******************
           IF MI_CLASECONTABLE IN ('V') THEN
              MI_DEBITO := NVL(MI_RS.CUENTADEBITOAUTORENTA, '');
              MI_CREDITO := NVL(MI_RS.CUENTACREDITOAUTORENTA, '');
           ELSE
              MI_CREDITO := NVL(MI_RS.CUENTADEBITOAUTORENTA, '');
              MI_DEBITO := NVL(MI_RS.CUENTACREDITOAUTORENTA, '');
           END IF;     

           IF MI_DEBITO IS NOT NULL THEN

             --MI_CTAPPTAL := '';
                BEGIN
                    BEGIN
                       SELECT RUBRO
                              INTO MI_CTAPPTAL
                            FROM PLAN_PPTAL_CUENTACNT          
                            WHERE COMPANIA          = UN_COMPANIA
                              AND ANO               = MI_ANIO
                              AND CUENTA_CONTABLE   = MI_DEBITO;
                    EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_CTAPPTAL := NULL;
                             WHEN TOO_MANY_ROWS THEN
                        RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
                MI_MSGERROR(1).CLAVE := 'CUENTA';
                MI_MSGERROR(1).VALOR := MI_DEBITO;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_CTACNT_VARIOSRU
                                            ,UN_REEMPLAZOS  => MI_MSGERROR);
                END;

             MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_DEBITO);

             IF MI_PARMANEJATERCERO  = 'SI' THEN

                SELECT TERCERO, SUCURSAL 
                INTO  MI_STRTERCEROAUX,
                      MI_STRSUCURSALAUX
                FROM SF_CUENTAS_AUXTERCERO 
                WHERE  COMPANIA = UN_COMPANIA 
                  AND ANO = MI_ANIO 
                  AND TIPOCOBRO = UN_TIPO
                  AND CONCEPTO = MI_RS.CODIGO
                  AND CUENTA = MI_DEBITO ;

             ELSE
                MI_STRTERCEROAUX :=  MI_RS.TERCERO;
                MI_STRSUCURSALAUX := MI_RS.SUCURSAL;
             END IF;

             MI_CONSECUTIVO := MI_CONSECUTIVO + 1;             

                MI_CAMPOS := 'COMPANIA
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

                MI_VALORES := ' '''||UN_COMPANIA||'''
                               ,'||MI_ANIO||'
                               ,'''||UN_TIPO||'''
                               ,'||MI_NUMERO||'
                               ,'||MI_CONSECUTIVO||'
                               ,'||MI_DEBITO||'
                               ,'''||UN_FECHA||'''
                               ,'''||MI_NATURALEZA||'''
                               ,'||MI_RS.VALOR_AUTORENTA||'
                               ,0
                               ,'||NVL(MI_RS.VALOR_AUTORENTA, 0)||'
                               ,0
                               ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                               ,'''||MI_STRTERCEROAUX||'''
                               ,'''||MI_STRSUCURSALAUX||'''
                               ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||''' 
                               ,'''||NVL(MI_CTAPPTAL, '')||'''
                               ,'''||UN_DESCRIPCION||'''
                               ,'||NVL(MI_RS.BASE_GRAVABLE, 0)||'
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
                       MI_MSGERROR(2).VALOR := UN_TIPO;

                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                                UN_EXC_COD =>SQLCODE
                                                ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                ,UN_REEMPLAZOS  => MI_MSGERROR);

                  END;               

            MI_CONSC := TRUE;

           ELSE

            MI_RPTA := 0;

           END IF;


          --***************** CREDITO ******************* 

       IF MI_CREDITO IS NOT NULL THEN
          BEGIN
            BEGIN
               SELECT RUBRO
                      INTO MI_CTAPPTAL
                    FROM PLAN_PPTAL_CUENTACNT          
                    WHERE COMPANIA          = UN_COMPANIA
                      AND ANO               = MI_ANIO
                      AND CUENTA_CONTABLE   = MI_CREDITO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_CTAPPTAL := NULL;
                         WHEN TOO_MANY_ROWS THEN
                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
            MI_MSGERROR(1).CLAVE := 'CUENTA';
            MI_MSGERROR(1).VALOR := MI_CREDITO;
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_CTACNT_VARIOSRU
                                        ,UN_REEMPLAZOS  => MI_MSGERROR);
          END;         

             MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CREDITO);

             IF MI_PARMANEJATERCERO  = 'SI'THEN

                SELECT TERCERO, SUCURSAL 
                INTO  MI_STRTERCEROAUX,
                      MI_STRSUCURSALAUX
                FROM SF_CUENTAS_AUXTERCERO 
                WHERE  COMPANIA = UN_COMPANIA 
                  AND ANO = MI_ANIO 
                  AND TIPOCOBRO = UN_TIPO
                  AND CONCEPTO = MI_RS.CODIGO
                  AND CUENTA = MI_DEBITO ;

             ELSE
                MI_STRTERCEROAUX :=  MI_RS.TERCERO;
                MI_STRSUCURSALAUX := MI_RS.SUCURSAL;
             END IF;

             IF MI_CONSC THEN
                MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
                MI_CONSC := FALSE;
             END IF ;       

                MI_CAMPOS := 'COMPANIA
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

                MI_VALORES := ' '''||UN_COMPANIA||'''
                              ,'||MI_ANIO||'
                              ,'''||UN_TIPO||'''
                              ,'||MI_NUMERO||'
                              ,'||MI_CONSECUTIVO||'
                              ,'''||MI_CREDITO||'''
                              ,'''||UN_FECHA||'''
                              ,'''||MI_NATURALEZA||'''
                              ,0
                              ,'||MI_RS.VALOR_AUTORENTA||'
                              ,0
                              ,'||NVL(MI_RS.VALOR_AUTORENTA, 0)||'
                              ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                              ,'''||MI_STRTERCEROAUX||'''
                              ,'''||MI_STRSUCURSALAUX||'''
                              ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||'''
                              ,'''||CASE WHEN MI_MANEQUIV4 = 'SI' THEN NVL(MI_CTAPPTAL,'') ELSE '' END||'''
                              ,'''||UN_DESCRIPCION||'''
                              ,'||NVL(MI_RS.BASE_GRAVABLE, 0)||'
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
                       MI_MSGERROR(2).VALOR := UN_TIPO;

                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                                UN_EXC_COD =>SQLCODE
                                                ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                ,UN_REEMPLAZOS  => MI_MSGERROR);

                  END;                             

             MI_CONSC := TRUE;       
       ELSE

        MI_RPTA := 0;

       END IF;

       --Etapa 10  


      ELSE  
          --Etapa 11
          MI_RPTA := 0;

      END IF;

   END IF;
   
   --*************   REGISTRO DEL VALOR AUTOICA SI INDICADOR SE ENCUENTRA ACTIVO   *******************
   --***************** DEBITO *******************
   IF MI_RS.APLICAAUTOICA NOT IN (0)THEN
      IF NVL(MI_RS.VALOR_AUTOICA, 0) <> 0 THEN 
        --***************** DEBITO *******************
           IF MI_CLASECONTABLE IN ('V') THEN
              MI_DEBITO := NVL(MI_RS.CUENTADEBITOAUTOICA, '');
              MI_CREDITO := NVL(MI_RS.CUENTACREDITOAUTOICA, '');
           ELSE
              MI_CREDITO := NVL(MI_RS.CUENTADEBITOAUTOICA, '');
              MI_DEBITO := NVL(MI_RS.CUENTACREDITOAUTOICA, '');
           END IF;     

           IF MI_DEBITO IS NOT NULL THEN

             --MI_CTAPPTAL := '';
                BEGIN
                    BEGIN
                       SELECT RUBRO
                              INTO MI_CTAPPTAL
                            FROM PLAN_PPTAL_CUENTACNT          
                            WHERE COMPANIA          = UN_COMPANIA
                              AND ANO               = MI_ANIO
                              AND CUENTA_CONTABLE   = MI_DEBITO;
                    EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_CTAPPTAL := NULL;
                             WHEN TOO_MANY_ROWS THEN
                        RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
                MI_MSGERROR(1).CLAVE := 'CUENTA';
                MI_MSGERROR(1).VALOR := MI_DEBITO;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_CTACNT_VARIOSRU
                                            ,UN_REEMPLAZOS  => MI_MSGERROR);
                END;

             MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_DEBITO);

             IF MI_PARMANEJATERCERO  = 'SI' THEN

                SELECT TERCERO, SUCURSAL 
                INTO  MI_STRTERCEROAUX,
                      MI_STRSUCURSALAUX
                FROM SF_CUENTAS_AUXTERCERO 
                WHERE  COMPANIA = UN_COMPANIA 
                  AND ANO = MI_ANIO 
                  AND TIPOCOBRO = UN_TIPO
                  AND CONCEPTO = MI_RS.CODIGO
                  AND CUENTA = MI_DEBITO ;

             ELSE
                MI_STRTERCEROAUX :=  MI_RS.TERCERO;
                MI_STRSUCURSALAUX := MI_RS.SUCURSAL;
             END IF;

             MI_CONSECUTIVO := MI_CONSECUTIVO + 1;             

                MI_CAMPOS := 'COMPANIA
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

                MI_VALORES := ' '''||UN_COMPANIA||'''
                               ,'||MI_ANIO||'
                               ,'''||UN_TIPO||'''
                               ,'||MI_NUMERO||'
                               ,'||MI_CONSECUTIVO||'
                               ,'||MI_DEBITO||'
                               ,'''||UN_FECHA||'''
                               ,'''||MI_NATURALEZA||'''
                               ,'||MI_RS.VALOR_AUTOICA||'
                               ,0
                               ,'||NVL(MI_RS.VALOR_AUTOICA, 0)||'
                               ,0
                               ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                               ,'''||MI_STRTERCEROAUX||'''
                               ,'''||MI_STRSUCURSALAUX||'''
                               ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||''' 
                               ,'''||NVL(MI_CTAPPTAL, '')||'''
                               ,'''||UN_DESCRIPCION||'''
                               ,'||NVL(MI_RS.BASE_GRAVABLE, 0)||'
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
                       MI_MSGERROR(2).VALOR := UN_TIPO;

                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                                UN_EXC_COD =>SQLCODE
                                                ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                ,UN_REEMPLAZOS  => MI_MSGERROR);

                  END;               

            MI_CONSC := TRUE;

           ELSE

            MI_RPTA := 0;

           END IF;

          --***************** CREDITO ******************* 

       IF MI_CREDITO IS NOT NULL THEN
          BEGIN
            BEGIN
               SELECT RUBRO
                      INTO MI_CTAPPTAL
                    FROM PLAN_PPTAL_CUENTACNT          
                    WHERE COMPANIA          = UN_COMPANIA
                      AND ANO               = MI_ANIO
                      AND CUENTA_CONTABLE   = MI_CREDITO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_CTAPPTAL := NULL;
                         WHEN TOO_MANY_ROWS THEN
                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
            MI_MSGERROR(1).CLAVE := 'CUENTA';
            MI_MSGERROR(1).VALOR := MI_CREDITO;
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_CTACNT_VARIOSRU
                                        ,UN_REEMPLAZOS  => MI_MSGERROR);
          END;         

             MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CREDITO);

             IF MI_PARMANEJATERCERO  = 'SI'THEN

                SELECT TERCERO, SUCURSAL 
                INTO  MI_STRTERCEROAUX,
                      MI_STRSUCURSALAUX
                FROM SF_CUENTAS_AUXTERCERO 
                WHERE  COMPANIA = UN_COMPANIA 
                  AND ANO = MI_ANIO 
                  AND TIPOCOBRO = UN_TIPO
                  AND CONCEPTO = MI_RS.CODIGO
                  AND CUENTA = MI_DEBITO ;

             ELSE
                MI_STRTERCEROAUX :=  MI_RS.TERCERO;
                MI_STRSUCURSALAUX := MI_RS.SUCURSAL;
             END IF;

             IF MI_CONSC THEN
                MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
                MI_CONSC := FALSE;
             END IF ;       

                MI_CAMPOS := 'COMPANIA
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

                MI_VALORES := ' '''||UN_COMPANIA||'''
                              ,'||MI_ANIO||'
                              ,'''||UN_TIPO||'''
                              ,'||MI_NUMERO||'
                              ,'||MI_CONSECUTIVO||'
                              ,'''||MI_CREDITO||'''
                              ,'''||UN_FECHA||'''
                              ,'''||MI_NATURALEZA||'''
                              ,0
                              ,'||MI_RS.VALOR_AUTOICA||'
                              ,0
                              ,'||NVL(MI_RS.VALOR_AUTOICA, 0)||'
                              ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                              ,'''||MI_STRTERCEROAUX||'''
                              ,'''||MI_STRSUCURSALAUX||'''
                              ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||'''
                              ,'''||CASE WHEN MI_MANEQUIV4 = 'SI' THEN NVL(MI_CTAPPTAL,'') ELSE '' END||'''
                              ,'''||UN_DESCRIPCION||'''
                              ,'||NVL(MI_RS.BASE_GRAVABLE, 0)||'
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
                       MI_MSGERROR(2).VALOR := UN_TIPO;

                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                                UN_EXC_COD =>SQLCODE
                                                ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                ,UN_REEMPLAZOS  => MI_MSGERROR);

                  END;                             

             MI_CONSC := TRUE;       
       ELSE

        MI_RPTA := 0;

       END IF;

       --Etapa 10  


      ELSE  
          --Etapa 11
          MI_RPTA := 0;

      END IF;

   END IF;    
   /*FIN_TICKET7723036 MPEREZ*/

     --*************   REGISTRO DEL VALOR RETEFUENTE SI INDICADOR SE ENCUENTRA ACTIVO   ******************* 

  IF MI_RS.APLICARETEFUENTE NOT IN (0) THEN
    IF NVL(MI_RS.VALOR_RETEFUENTE, 0) <> 0 THEN
       --***************** DEBITO *******************
       IF MI_CLASECONTABLE IN ('V') THEN
          MI_DEBITO := NVL(MI_RS.CUENTADEBITORETE, '');
          MI_CREDITO := NVL(MI_RS.CUENTACREDITORETE, '');
        ELSE
          MI_CREDITO := NVL(MI_RS.CUENTADEBITORETE, '');
          MI_DEBITO := NVL(MI_RS.CUENTACREDITORETE,'');
        END IF;

      IF MI_DEBITO IS NOT NULL THEN

           --MI_CTAPPTAL := '';
           BEGIN
                BEGIN
                   SELECT RUBRO
                          INTO MI_CTAPPTAL
                        FROM PLAN_PPTAL_CUENTACNT          
                        WHERE COMPANIA          = UN_COMPANIA
                          AND ANO               = MI_ANIO
                          AND CUENTA_CONTABLE   = MI_DEBITO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_CTAPPTAL := NULL;
                         WHEN TOO_MANY_ROWS THEN
                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
                MI_MSGERROR(1).CLAVE := 'CUENTA';
                MI_MSGERROR(1).VALOR := MI_DEBITO;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_CTACNT_VARIOSRU
                                            ,UN_REEMPLAZOS  => MI_MSGERROR);
            END;

              
           MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_DEBITO);

           IF MI_PARMANEJATERCERO  = 'SI' THEN

              SELECT TERCERO, SUCURSAL 
              INTO  MI_STRTERCEROAUX,
                    MI_STRSUCURSALAUX
              FROM SF_CUENTAS_AUXTERCERO 
              WHERE  COMPANIA = UN_COMPANIA 
                AND ANO = MI_ANIO 
                AND TIPOCOBRO = UN_TIPO
                AND CONCEPTO = MI_RS.CODIGO
                AND CUENTA = MI_DEBITO ;

           ELSE
              MI_STRTERCEROAUX :=  MI_RS.TERCERO;
              MI_STRSUCURSALAUX := MI_RS.SUCURSAL;
           END IF;

           MI_CONSECUTIVO := MI_CONSECUTIVO + 1;    

                MI_CAMPOS := 'COMPANIA
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


                MI_VALORES := ' '''||UN_COMPANIA||'''
                                 ,'||MI_ANIO||'
                                 ,'''||UN_TIPO||'''
                                 ,'||MI_NUMERO||'
                                 ,'||MI_CONSECUTIVO||'
                                 ,'''||MI_DEBITO||'''
                                 ,'''||UN_FECHA||'''
                                 ,'''||MI_NATURALEZA||'''
                                 ,'||MI_RS.VALOR_RETEFUENTE||'
                                 ,0
                                 ,'||NVL(MI_RS.VALOR_RETEFUENTE, 0)||'
                                 ,0
                                 ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                                 ,'''||MI_STRTERCEROAUX||'''
                                 ,'''||MI_STRSUCURSALAUX||'''
                                 ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||'''
                                 ,'''||NVL(MI_CTAPPTAL, '')||'''
                                 ,'''||UN_DESCRIPCION||'''
                                 ,'||NVL(MI_RS.BASE_GRAVABLE, 0)||'
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
                       MI_MSGERROR(2).VALOR := UN_TIPO;

                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                                UN_EXC_COD =>SQLCODE
                                                ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                ,UN_REEMPLAZOS  => MI_MSGERROR);

                  END;                            

             MI_CONSC := TRUE;



      ELSE

        MI_RPTA := 0;

      END IF;

      --Etapa 12  

      --***************** CREDITO *******************

      IF MI_CREDITO IS NOT NULL THEN

          BEGIN
            BEGIN
               SELECT RUBRO
                      INTO MI_CTAPPTAL
                    FROM PLAN_PPTAL_CUENTACNT          
                    WHERE COMPANIA          = UN_COMPANIA
                      AND ANO               = MI_ANIO
                      AND CUENTA_CONTABLE   = MI_CREDITO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_CTAPPTAL := NULL;
                         WHEN TOO_MANY_ROWS THEN
                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
            MI_MSGERROR(1).CLAVE := 'CUENTA';
            MI_MSGERROR(1).VALOR := MI_CREDITO;
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_CTACNT_VARIOSRU
                                        ,UN_REEMPLAZOS  => MI_MSGERROR);
          END;

             MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CREDITO);

             IF MI_PARMANEJATERCERO  = 'SI' THEN

                SELECT TERCERO, SUCURSAL 
                INTO  MI_STRTERCEROAUX,
                      MI_STRSUCURSALAUX
                FROM SF_CUENTAS_AUXTERCERO 
                WHERE  COMPANIA = UN_COMPANIA 
                  AND ANO = MI_ANIO 
                  AND TIPOCOBRO = UN_TIPO
                  AND CONCEPTO = MI_RS.CODIGO
                  AND CUENTA = MI_DEBITO ;

             ELSE
                MI_STRTERCEROAUX :=  MI_RS.TERCERO;
                MI_STRSUCURSALAUX := MI_RS.SUCURSAL;
             END IF;

             IF MI_CONSC THEN
                MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
                MI_CONSC := FALSE;
             END IF ;       

                MI_CAMPOS := 'COMPANIA
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

                MI_VALORES := ' '''||UN_COMPANIA||'''
                                ,'||MI_ANIO||'
                                ,'''||UN_TIPO||'''
                                ,'||MI_NUMERO||'
                                ,'||MI_CONSECUTIVO||'
                                ,'''||MI_CREDITO||'''
                                ,'''||UN_FECHA||'''
                                ,'''||MI_NATURALEZA||'''
                                ,0
                                ,'||MI_RS.VALOR_RETEFUENTE||'
                                ,0
                                ,'||NVL(MI_RS.VALOR_RETEFUENTE, 0)||'
                                ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                                ,'''||MI_STRTERCEROAUX||'''
                                ,'''||MI_STRSUCURSALAUX||'''
                                ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||''' 
                                ,'''||CASE WHEN MI_MANEQUIV4 = 'SI' THEN NVL(MI_CTAPPTAL,'') ELSE '' END||'''
                                ,'''||UN_DESCRIPCION||'''
                                ,'||NVL(MI_RS.BASE_GRAVABLE, 0)||'
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
                       MI_MSGERROR(2).VALOR := UN_TIPO;

                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                                UN_EXC_COD =>SQLCODE
                                                ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                ,UN_REEMPLAZOS  => MI_MSGERROR);

                  END;                            

             MI_CONSC := TRUE;      

      ELSE        

        MI_RPTA := 0;

      END IF;



    ELSE  
      --Etapa 15
      MI_RPTA := 0;

    END IF;

  END IF; 

     --*************   REGISTRO DEL VALOR DESCUENTO SI INDICADOR SE ENCUENTRA ACTIVO   *******************

  IF MI_RS.APLICADESCUENTO NOT IN (0) THEN
    IF NVL(MI_RS.VALOR_DESCUENTO, 0) <> 0 THEN

      --***************** DEBITO *******************
        IF MI_CLASECONTABLE IN ('V') THEN
            MI_DEBITO := NVL(MI_RS.CUENTADEBITODESCUENTO, '');
            MI_CREDITO := NVL(MI_RS.CUENTACREDITODESCUENTO, '');
        ELSE
            MI_CREDITO := NVL(MI_RS.CUENTADEBITODESCUENTO, '');
            MI_DEBITO := NVL(MI_RS.CUENTACREDITODESCUENTO, '');
        END IF;

        IF MI_DEBITO IS NOT NULL THEN

           --MI_CTAPPTAL := '';               
           BEGIN
                BEGIN
                   SELECT RUBRO
                          INTO MI_CTAPPTAL
                        FROM PLAN_PPTAL_CUENTACNT          
                        WHERE COMPANIA          = UN_COMPANIA
                          AND ANO               = MI_ANIO
                          AND CUENTA_CONTABLE   = MI_DEBITO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_CTAPPTAL := NULL;
                         WHEN TOO_MANY_ROWS THEN
                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
                MI_MSGERROR(1).CLAVE := 'CUENTA';
                MI_MSGERROR(1).VALOR := MI_DEBITO;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_CTACNT_VARIOSRU
                                            ,UN_REEMPLAZOS  => MI_MSGERROR);
            END;

              
           MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_DEBITO);

           IF MI_PARMANEJATERCERO  = 'SI'THEN

              SELECT TERCERO, SUCURSAL 
              INTO  MI_STRTERCEROAUX,
                    MI_STRSUCURSALAUX
              FROM SF_CUENTAS_AUXTERCERO 
              WHERE  COMPANIA = UN_COMPANIA 
                AND ANO = MI_ANIO 
                AND TIPOCOBRO = UN_TIPO
                AND CONCEPTO = MI_RS.CODIGO
                AND CUENTA = MI_DEBITO ;

           ELSE
              MI_STRTERCEROAUX :=  MI_RS.TERCERO;
              MI_STRSUCURSALAUX := MI_RS.SUCURSAL;
           END IF;

           MI_CONSECUTIVO := MI_CONSECUTIVO + 1;   

                MI_CAMPOS := 'COMPANIA
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

                MI_VALORES := ' '''||UN_COMPANIA||'''
                                ,'||MI_ANIO||'
                                ,'''||UN_TIPO||'''
                                ,'||MI_NUMERO||'
                                ,'||MI_CONSECUTIVO||'
                                ,'''||MI_DEBITO||'''
                                ,'''||UN_FECHA||'''
                                ,'''||MI_NATURALEZA||'''
                                ,'||MI_RS.VALOR_DESCUENTO||'
                                ,0
                                ,'||NVL(MI_RS.VALOR_DESCUENTO, 0)||'
                                ,0
                                ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                                ,'''||MI_STRTERCEROAUX||'''
                                ,'''||MI_STRSUCURSALAUX||'''
                                ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||'''
                                ,'''||NVL(MI_CTAPPTAL, '')||'''
                                ,'''||UN_DESCRIPCION||'''
                                ,'||NVL(MI_RS.BASE_GRAVABLE, 0)||'
                                ,'''||MI_RS.REFERENCIA|| '''
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
                       MI_MSGERROR(2).VALOR := UN_TIPO;

                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                                UN_EXC_COD =>SQLCODE
                                                ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                ,UN_REEMPLAZOS  => MI_MSGERROR);

                  END;                                    

            MI_CONSC := TRUE;              

        ELSE

          MI_RPTA := 0;

        END IF;

       --***************** CREDITO *******************
        IF MI_CREDITO IS NOT NULL THEN

            BEGIN
                BEGIN
                   SELECT RUBRO
                          INTO MI_CTAPPTAL
                        FROM PLAN_PPTAL_CUENTACNT          
                        WHERE COMPANIA          = UN_COMPANIA
                          AND ANO               = MI_ANIO
                          AND CUENTA_CONTABLE   = MI_CREDITO;
                    EXCEPTION WHEN NO_DATA_FOUND THEN
                                MI_CTAPPTAL := '';
                              WHEN TOO_MANY_ROWS THEN
                                RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
                MI_MSGERROR(1).CLAVE := 'CUENTA';
                MI_MSGERROR(1).VALOR := MI_CREDITO;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_CTACNT_VARIOSRU
                                            ,UN_REEMPLAZOS  => MI_MSGERROR);
            END;

             MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CREDITO);

             IF MI_PARMANEJATERCERO  = 'SI' THEN

                SELECT TERCERO, SUCURSAL 
                INTO  MI_STRTERCEROAUX,
                      MI_STRSUCURSALAUX
                FROM SF_CUENTAS_AUXTERCERO 
                WHERE  COMPANIA = UN_COMPANIA 
                  AND ANO = MI_ANIO 
                  AND TIPOCOBRO = UN_TIPO
                  AND CONCEPTO = MI_RS.CODIGO
                  AND CUENTA = MI_DEBITO ;

             ELSE
                MI_STRTERCEROAUX :=  MI_RS.TERCERO;
                MI_STRSUCURSALAUX := MI_RS.SUCURSAL;
             END IF;

             IF MI_CONSC THEN
                MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
                MI_CONSC := FALSE;
             END IF ;       

                MI_CAMPOS := 'COMPANIA
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

                MI_VALORES := ' '''||UN_Compania||'''
                            ,'||MI_ANIO||'
                            ,'''||UN_TIPO||'''
                            ,'||MI_NUMERO||'
                            ,'||MI_CONSECUTIVO||'
                            ,'''||MI_CREDITO||'''
                            ,'''||UN_FECHA||'''
                            ,'''||MI_NATURALEZA||'''
                            ,0
                            ,'||MI_RS.VALOR_DESCUENTO||'
                            ,0
                            ,'||NVL(MI_RS.VALOR_DESCUENTO, 0)||'
                            ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                            ,'''||MI_STRTERCEROAUX||'''
                            ,'''||MI_STRSUCURSALAUX||'''
                            ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||''' 
                            ,'''||CASE WHEN MI_MANEQUIV4 = 'SI' THEN NVL(MI_CTAPPTAL,'') ELSE '' END||'''
                            ,'''||UN_DESCRIPCION||'''
                            ,'||NVL(MI_RS.BASE_GRAVABLE, 0)||'
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
                       MI_MSGERROR(2).VALOR := UN_TIPO;

                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                                UN_EXC_COD =>SQLCODE
                                                ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                ,UN_REEMPLAZOS  => MI_MSGERROR);

                  END;                                    

            MI_CONSC := TRUE;                              

       ELSE

        MI_RPTA := 0;

       END IF; 

       --Etapa 18


    ELSE    
      --Etapa 19
      MI_RPTA := 0;      

    END IF;

  END IF;
     --*************   REGISTRO DEL VALOR ICA SI INDICADOR SE ENCUENTRA ACTIVO   *******************

   IF MI_RS.APLICAICA NOT IN (0) THEN
     IF NVL(MI_RS.VALOR_ICA, 0) <> 0 THEN
      --***************** DEBITO *******************
        IF MI_CLASECONTABLE IN ('V') THEN
          MI_DEBITO := NVL(MI_RS.CUENTADEBITOICA, '');
          MI_CREDITO := NVL(MI_RS.CUENTACREDITOICA, '');
        ELSE
          MI_CREDITO := NVL(MI_RS.CUENTADEBITOICA, '');
          MI_DEBITO := NVL(MI_RS.CUENTACREDITOICA, '');
        END IF;

       IF MI_DEBITO IS NOT NULL THEN

           --MI_CTAPPTAL := '';               
            BEGIN
                BEGIN
                   SELECT RUBRO
                          INTO MI_CTAPPTAL
                        FROM PLAN_PPTAL_CUENTACNT          
                        WHERE COMPANIA          = UN_COMPANIA
                          AND ANO               = MI_ANIO
                          AND CUENTA_CONTABLE   = MI_DEBITO;
                    EXCEPTION WHEN NO_DATA_FOUND THEN
                                MI_CTAPPTAL := NULL;
                              WHEN TOO_MANY_ROWS THEN
                                RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
                MI_MSGERROR(1).CLAVE := 'CUENTA';
                MI_MSGERROR(1).VALOR := MI_DEBITO;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_CTACNT_VARIOSRU
                                            ,UN_REEMPLAZOS  => MI_MSGERROR);
            END;

              
           MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_DEBITO);

           IF MI_PARMANEJATERCERO  = 'SI'THEN

              SELECT TERCERO, SUCURSAL 
              INTO  MI_STRTERCEROAUX,
                    MI_STRSUCURSALAUX
              FROM SF_CUENTAS_AUXTERCERO 
              WHERE  COMPANIA = UN_COMPANIA 
                AND ANO = MI_ANIO 
                AND TIPOCOBRO = UN_TIPO
                AND CONCEPTO = MI_RS.CODIGO
                AND CUENTA = MI_DEBITO ;

           ELSE
              MI_STRTERCEROAUX :=  MI_RS.TERCERO;
              MI_STRSUCURSALAUX := MI_RS.SUCURSAL;
           END IF;

           MI_CONSECUTIVO := MI_CONSECUTIVO + 1;   

                MI_CAMPOS := 'COMPANIA
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

                MI_VALORES := ' '''||UN_COMPANIA||'''
                                ,'||MI_ANIO||'
                                ,'''||UN_TIPO||'''
                                ,'||MI_NUMERO||'
                                ,'||MI_CONSECUTIVO||'
                                ,'''||MI_DEBITO||'''
                                ,'''||UN_FECHA||'''
                                ,'''||MI_NATURALEZA||'''
                                ,'||MI_RS.VALOR_ICA||'
                                ,0
                                ,'||NVL(MI_RS.VALOR_ICA, 0)||'
                                ,0
                                ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                                ,'''||MI_STRTERCEROAUX||'''
                                ,'''||MI_STRSUCURSALAUX||'''
                                ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||'''
                                ,'''||NVL(MI_CTAPPTAL, '')||'''
                                ,'''||UN_DESCRIPCION||'''
                                ,'||NVL(MI_RS.BASE_GRAVABLE, 0)||'
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
                       MI_MSGERROR(2).VALOR := UN_TIPO;

                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                                UN_EXC_COD =>SQLCODE
                                                ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                ,UN_REEMPLAZOS  => MI_MSGERROR);

                  END;                                    

            MI_CONSC := TRUE;          

       ELSE

        MI_RPTA := 0;

       END IF;

    --***************** CREDITO *******************
      IF MI_CREDITO IS NOT NULL THEN       

          BEGIN
            BEGIN
               SELECT RUBRO
                      INTO MI_CTAPPTAL
                    FROM PLAN_PPTAL_CUENTACNT          
                    WHERE COMPANIA          = UN_COMPANIA
                      AND ANO               = MI_ANIO
                      AND CUENTA_CONTABLE   = MI_CREDITO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                                MI_CTAPPTAL := NULL;
                          WHEN TOO_MANY_ROWS THEN
                                RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
            MI_MSGERROR(1).CLAVE := 'CUENTA';
            MI_MSGERROR(1).VALOR := MI_CREDITO;
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_CTACNT_VARIOSRU
                                        ,UN_REEMPLAZOS  => MI_MSGERROR);
          END;

             MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CREDITO);

             IF MI_PARMANEJATERCERO  = 'SI' THEN

                SELECT TERCERO, SUCURSAL 
                INTO  MI_STRTERCEROAUX,
                      MI_STRSUCURSALAUX
                FROM SF_CUENTAS_AUXTERCERO 
                WHERE  COMPANIA = UN_COMPANIA 
                  AND ANO = MI_ANIO 
                  AND TIPOCOBRO = UN_TIPO
                  AND CONCEPTO = MI_RS.CODIGO
                  AND CUENTA = MI_DEBITO ;

             ELSE
                MI_STRTERCEROAUX :=  MI_RS.TERCERO;
                MI_STRSUCURSALAUX := MI_RS.SUCURSAL;
             END IF;

             IF MI_CONSC THEN
                MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
                MI_CONSC := FALSE;
             END IF ;       

                MI_CAMPOS := 'COMPANIA
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

                MI_VALORES := ' '''||UN_COMPANIA||'''
                            ,'||MI_ANIO||'
                            ,'''||UN_TIPO||'''
                            ,'||MI_NUMERO||'
                            ,'||MI_CONSECUTIVO||'
                            ,'''||MI_CREDITO||'''
                            ,'''||UN_FECHA||'''
                            ,'''||MI_NATURALEZA||'''
                            ,0
                            ,'||MI_RS.VALOR_ICA||'
                            ,0
                            ,'||NVL(MI_RS.VALOR_ICA, 0)||'
                            ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                            ,'''||MI_STRTERCEROAUX||'''
                            ,'''||MI_STRSUCURSALAUX||'''
                            ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||''' 
                            ,'''||CASE WHEN MI_MANEQUIV4 = 'SI' THEN NVL(MI_CTAPPTAL,'') ELSE '' END||'''
                            ,'''||UN_DESCRIPCION||'''
                            ,'||NVL(MI_RS.BASE_GRAVABLE, 0)||'
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
                       MI_MSGERROR(2).VALOR := UN_TIPO;

                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                                UN_EXC_COD =>SQLCODE
                                                ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                ,UN_REEMPLAZOS  => MI_MSGERROR);

                  END;                                    

            MI_CONSC := TRUE;                               

      ELSE

        MI_RPTA := 0;          

      END IF;

     ELSE 

        MI_RPTA := 0;      

     END IF;

   END IF;

    --*************   REGISTRO DEL VALOR IMPOCONSNMO SI INDICADOR SE ENCUENTRA ACTIVO   *******************

  IF MI_RS.APLICAIMPOCONSUMO NOT IN (0) THEN
     IF NVL(MI_RS.VALOR_IMPOCONSUMO, 0) <> 0 THEN
      --***************** DEBITO *******************
        IF MI_CLASECONTABLE IN ('V') THEN
          MI_DEBITO := NVL(MI_RS.CTADEBIMPOCONSUMO, '');
          MI_CREDITO := NVL(MI_RS.CTACREDIMPOCONSUMO, '');
        ELSE
          MI_CREDITO := NVL(MI_RS.CTADEBIMPOCONSUMO, '');
          MI_DEBITO := NVL(MI_RS.CTACREDIMPOCONSUMO, '');
        END IF;

       IF MI_DEBITO IS NOT NULL THEN

           --MI_CTAPPTAL := '';               
            BEGIN
                BEGIN
                   SELECT RUBRO
                          INTO MI_CTAPPTAL
                        FROM PLAN_PPTAL_CUENTACNT          
                        WHERE COMPANIA          = UN_COMPANIA
                          AND ANO               = MI_ANIO
                          AND CUENTA_CONTABLE   = MI_DEBITO;
                    EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_CTAPPTAL := '';
                             WHEN TOO_MANY_ROWS THEN
                        RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
            MI_MSGERROR(1).CLAVE := 'CUENTA';
            MI_MSGERROR(1).VALOR := MI_DEBITO;
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_CTACNT_VARIOSRU
                                        ,UN_REEMPLAZOS  => MI_MSGERROR);
        END;

              
           MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_DEBITO);

           IF MI_PARMANEJATERCERO  = 'SI'THEN

              SELECT TERCERO, SUCURSAL 
              INTO  MI_STRTERCEROAUX,
                    MI_STRSUCURSALAUX
              FROM SF_CUENTAS_AUXTERCERO 
              WHERE  COMPANIA = UN_COMPANIA 
                AND ANO = MI_ANIO 
                AND TIPOCOBRO = UN_TIPO
                AND CONCEPTO = MI_RS.CODIGO
                AND CUENTA = MI_DEBITO ;

           ELSE
              MI_STRTERCEROAUX :=  MI_RS.TERCERO;
              MI_STRSUCURSALAUX := MI_RS.SUCURSAL;
           END IF;

           MI_CONSECUTIVO := MI_CONSECUTIVO + 1;   

                MI_CAMPOS := 'COMPANIA
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

                MI_VALORES := ' '''||UN_COMPANIA||'''
                                ,'||MI_ANIO||'
                                ,'''||UN_TIPO||'''
                                ,'||MI_NUMERO||'
                                ,'||MI_CONSECUTIVO||'
                                ,'''||MI_DEBITO||'''
                                ,'''||UN_FECHA||'''
                                ,'''||MI_NATURALEZA||'''
                                ,'||MI_RS.VALOR_IMPOCONSUMO||'
                                ,0
                                ,'||NVL(MI_RS.VALOR_IMPOCONSUMO, 0)||'
                                ,0
                                ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                                ,'''||MI_STRTERCEROAUX||'''
                                ,'''||MI_STRSUCURSALAUX||'''
                                ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||'''
                                ,'''||NVL(MI_CTAPPTAL, '')||'''
                                ,'''||UN_DESCRIPCION||'''
                                ,'||NVL(MI_RS.BASE_GRAVABLE, 0)||'
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
                       MI_MSGERROR(2).VALOR := UN_TIPO;

                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                                UN_EXC_COD =>SQLCODE
                                                ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                ,UN_REEMPLAZOS  => MI_MSGERROR);

                  END;                                    

            MI_CONSC := TRUE;          

       ELSE

        MI_RPTA := 0;

       END IF;

    --***************** CREDITO *******************
      IF MI_CREDITO IS NOT NULL THEN       

            BEGIN
                BEGIN
                   SELECT RUBRO
                          INTO MI_CTAPPTAL
                        FROM PLAN_PPTAL_CUENTACNT          
                        WHERE COMPANIA          = UN_COMPANIA
                          AND ANO               = MI_ANIO
                          AND CUENTA_CONTABLE   = MI_CREDITO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_CTAPPTAL := NULL;
                         WHEN TOO_MANY_ROWS THEN
                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
                MI_MSGERROR(1).CLAVE := 'CUENTA';
                MI_MSGERROR(1).VALOR := MI_CREDITO;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_CTACNT_VARIOSRU
                                            ,UN_REEMPLAZOS  => MI_MSGERROR);
            END;

             MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CREDITO);

             IF MI_PARMANEJATERCERO  = 'SI' THEN

                SELECT TERCERO, SUCURSAL 
                INTO  MI_STRTERCEROAUX,
                      MI_STRSUCURSALAUX
                FROM SF_CUENTAS_AUXTERCERO 
                WHERE  COMPANIA = UN_COMPANIA 
                  AND ANO = MI_ANIO 
                  AND TIPOCOBRO = UN_TIPO
                  AND CONCEPTO = MI_RS.CODIGO
                  AND CUENTA = MI_DEBITO ;

             ELSE
                MI_STRTERCEROAUX :=  MI_RS.TERCERO;
                MI_STRSUCURSALAUX := MI_RS.SUCURSAL;
             END IF;

             IF MI_CONSC THEN
                MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
                MI_CONSC := FALSE;
             END IF ;       

                MI_CAMPOS := 'COMPANIA
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

                MI_VALORES := ' '''||UN_COMPANIA||'''
                            ,'||MI_ANIO||'
                            ,'''||UN_TIPO||'''
                            ,'||MI_NUMERO||'
                            ,'||MI_CONSECUTIVO||'
                            ,'''||MI_CREDITO||'''
                            ,'''||UN_FECHA||'''
                            ,'''||MI_NATURALEZA||'''
                            ,0
                            ,'||MI_RS.VALOR_IMPOCONSUMO||'
                            ,0
                            ,'||NVL(MI_RS.VALOR_IMPOCONSUMO, 0)||'
                            ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                            ,'''||MI_STRTERCEROAUX||'''
                            ,'''||MI_STRSUCURSALAUX||'''
                            ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||''' 
                            ,'''||CASE WHEN MI_MANEQUIV4 = 'SI' THEN NVL(MI_CTAPPTAL,'') ELSE '' END||'''
                            ,'''||UN_DESCRIPCION||'''
                            ,'||NVL(MI_RS.BASE_GRAVABLE, 0)||'
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
                       MI_MSGERROR(2).VALOR := UN_TIPO;

                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                                UN_EXC_COD =>SQLCODE
                                                ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                ,UN_REEMPLAZOS  => MI_MSGERROR);

                  END;                                    

            MI_CONSC := TRUE;                               

      ELSE

        MI_RPTA := 0;          

      END IF;

     ELSE 

        MI_RPTA := 0;      

     END IF;

   END IF;    
--
--
     --*************   REGISTRO DEL VALOR RETEIVA SI INDICADOR SE ENCUENTRA ACTIVO   ******************* 

  IF MI_RS.APLICARETEIVA NOT IN (0) THEN
    IF NVL(MI_RS.VALOR_RETEIVA, 0) <> 0 THEN
       --***************** DEBITO *******************
       IF MI_CLASECONTABLE IN ('V') THEN
          MI_DEBITO := NVL(MI_RS.CTADEBITORETEIVAACTUAL, '');
          MI_CREDITO := NVL(MI_RS.CTACREDITORETEIVAACTUAL, '');
        ELSE
          MI_CREDITO := NVL(MI_RS.CTADEBITORETEIVAACTUAL, '');
          MI_DEBITO := NVL(MI_RS.CTACREDITORETEIVAACTUAL,'');
        END IF;

      IF MI_DEBITO IS NOT NULL THEN

           --MI_CTAPPTAL := '';
           BEGIN
                BEGIN
                   SELECT RUBRO
                          INTO MI_CTAPPTAL
                        FROM PLAN_PPTAL_CUENTACNT          
                        WHERE COMPANIA          = UN_COMPANIA
                          AND ANO               = MI_ANIO
                          AND CUENTA_CONTABLE   = MI_DEBITO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_CTAPPTAL := NULL;
                         WHEN TOO_MANY_ROWS THEN
                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
                MI_MSGERROR(1).CLAVE := 'CUENTA';
                MI_MSGERROR(1).VALOR := MI_DEBITO;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_CTACNT_VARIOSRU
                                            ,UN_REEMPLAZOS  => MI_MSGERROR);
            END;


           MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_DEBITO);

           IF MI_PARMANEJATERCERO  = 'SI' THEN

              SELECT TERCERO, SUCURSAL 
              INTO  MI_STRTERCEROAUX,
                    MI_STRSUCURSALAUX
              FROM SF_CUENTAS_AUXTERCERO 
              WHERE  COMPANIA = UN_COMPANIA 
                AND ANO = MI_ANIO 
                AND TIPOCOBRO = UN_TIPO
                AND CONCEPTO = MI_RS.CODIGO
                AND CUENTA = MI_DEBITO ;

           ELSE
              MI_STRTERCEROAUX :=  MI_RS.TERCERO;
              MI_STRSUCURSALAUX := MI_RS.SUCURSAL;
           END IF;

           MI_CONSECUTIVO := MI_CONSECUTIVO + 1;    

                MI_CAMPOS := 'COMPANIA
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


                MI_VALORES := ' '''||UN_COMPANIA||'''
                                 ,'||MI_ANIO||'
                                 ,'''||UN_TIPO||'''
                                 ,'||MI_NUMERO||'
                                 ,'||MI_CONSECUTIVO||'
                                 ,'''||MI_DEBITO||'''
                                 ,'''||UN_FECHA||'''
                                 ,'''||MI_NATURALEZA||'''
                                 ,'||MI_RS.VALOR_RETEIVA||'
                                 ,0
                                 ,'||NVL(MI_RS.VALOR_RETEIVA, 0)||'
                                 ,0
                                 ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                                 ,'''||MI_STRTERCEROAUX||'''
                                 ,'''||MI_STRSUCURSALAUX||'''
                                 ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||'''
                                 ,'''||NVL(MI_CTAPPTAL, '')||'''
                                 ,'''||UN_DESCRIPCION||'''
                                 ,'||NVL(MI_RS.BASE_GRAVABLE, 0)||'
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
                       MI_MSGERROR(2).VALOR := UN_TIPO;

                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                                UN_EXC_COD =>SQLCODE
                                                ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                ,UN_REEMPLAZOS  => MI_MSGERROR);

                  END;                            

             MI_CONSC := TRUE;



      ELSE

        MI_RPTA := 0;

      END IF;

      --Etapa 12  

      --***************** CREDITO *******************

      IF MI_CREDITO IS NOT NULL THEN

          BEGIN
            BEGIN
               SELECT RUBRO
                      INTO MI_CTAPPTAL
                    FROM PLAN_PPTAL_CUENTACNT          
                    WHERE COMPANIA          = UN_COMPANIA
                      AND ANO               = MI_ANIO
                      AND CUENTA_CONTABLE   = MI_CREDITO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_CTAPPTAL := NULL;
                         WHEN TOO_MANY_ROWS THEN
                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
            MI_MSGERROR(1).CLAVE := 'CUENTA';
            MI_MSGERROR(1).VALOR := MI_CREDITO;
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_CTACNT_VARIOSRU
                                        ,UN_REEMPLAZOS  => MI_MSGERROR);
          END;

             MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CREDITO);

             IF MI_PARMANEJATERCERO  = 'SI' THEN

                SELECT TERCERO, SUCURSAL 
                INTO  MI_STRTERCEROAUX,
                      MI_STRSUCURSALAUX
                FROM SF_CUENTAS_AUXTERCERO 
                WHERE  COMPANIA = UN_COMPANIA 
                  AND ANO = MI_ANIO 
                  AND TIPOCOBRO = UN_TIPO
                  AND CONCEPTO = MI_RS.CODIGO
                  AND CUENTA = MI_DEBITO ;

             ELSE
                MI_STRTERCEROAUX :=  MI_RS.TERCERO;
                MI_STRSUCURSALAUX := MI_RS.SUCURSAL;
             END IF;

             IF MI_CONSC THEN
                MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
                MI_CONSC := FALSE;
             END IF ;       

                MI_CAMPOS := 'COMPANIA
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

                MI_VALORES := ' '''||UN_COMPANIA||'''
                                ,'||MI_ANIO||'
                                ,'''||UN_TIPO||'''
                                ,'||MI_NUMERO||'
                                ,'||MI_CONSECUTIVO||'
                                ,'''||MI_CREDITO||'''
                                ,'''||UN_FECHA||'''
                                ,'''||MI_NATURALEZA||'''
                                ,0
                                ,'||MI_RS.VALOR_RETEIVA||'
                                ,0
                                ,'||NVL(MI_RS.VALOR_RETEIVA, 0)||'
                                ,'''||NVL(MI_RS.CENTRO_COSTO, 9999999999)||'''
                                ,'''||MI_STRTERCEROAUX||'''
                                ,'''||MI_STRSUCURSALAUX||'''
                                ,'''||NVL(MI_RS.AUXILIAR, 9999999999999999)||''' 
                                ,'''||CASE WHEN MI_MANEQUIV4 = 'SI' THEN NVL(MI_CTAPPTAL,'') ELSE '' END||'''
                                ,'''||UN_DESCRIPCION||'''
                                ,'||NVL(MI_RS.BASE_GRAVABLE, 0)||'
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
                       MI_MSGERROR(2).VALOR := UN_TIPO;

                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                                UN_EXC_COD =>SQLCODE
                                                ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                ,UN_REEMPLAZOS  => MI_MSGERROR);

                  END;                            

             MI_CONSC := TRUE;      

      ELSE 
      MI_RPTA := 0;

      END IF;
    ELSE  
      MI_RPTA := 0;
    END IF;

  END IF;
 -- 
   IF  NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                    UN_NOMBRE    => 'SF MANEJA CALCULO CREE',
                                    UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                    UN_FECHA_PAR => SYSDATE,
                                    UN_IND_MAYUS =>0),'NO') = 'SI' THEN


      FOR MI_RS2 IN (SELECT SF_FACTURA.COMPANIA,
                            SF_FACTURA.NUMERO_FACTURA,
                            SF_FACTURA.TIPOCOBRO,
                            SF_FACTURA.CODIGO_COBRO,
                            SF_FACTURA.VALOR_TOTAL,
                            SF_FACTURA.TERCERO       TERCEROH,
                            SF_FACTURA.SUCURSAL     SUCURSALH,
                            SF_FACTURA.CENTRO_COSTO CENTRO_COSTOH,
                            SF_FACTURA.AUXILIAR     AUXILIARH,
                            SF_CONCEPTOS.CODIGO,
                            SF_DETALLE_FACTURA.TERCERO,
                            SF_DETALLE_FACTURA.SUCURSAL,
                            SF_DETALLE_FACTURA.CENTRO_COSTO,
                            SF_DETALLE_FACTURA.AUXILIAR,
                            SF_CREE_CONCEPTOS.APLICACREE,
                            SF_DETALLE_FACTURA.VALOR_CREE,
                            SF_CREE_CONCEPTOS.CUENTADEBITOBASE  CTADEBITOCREE,
                            SF_CREE_CONCEPTOS.CUENTACREDITOBASE CTACREDITOCREE,
                            SF_DETALLE_FACTURA.BASE_GRAVABLE,
                            SF_DETALLE_FACTURA.REFERENCIA,
                            SF_DETALLE_FACTURA.NROCONTRATOSIGEC,
                            SF_DETALLE_FACTURA.TIPOCONTRATOSIGEC
						  FROM SF_DETALLE_FACTURA
                          INNER JOIN SF_CONCEPTOS
                          ON SF_DETALLE_FACTURA.COMPANIA   = SF_CONCEPTOS.COMPANIA
                          AND SF_DETALLE_FACTURA.ANO       = SF_CONCEPTOS.ANO
                          AND SF_DETALLE_FACTURA.TIPOCOBRO = SF_CONCEPTOS.TIPOCOBRO
                          AND SF_DETALLE_FACTURA.CONCEPTO  = SF_CONCEPTOS.CODIGO
                          INNER JOIN SF_FACTURA
                          ON SF_DETALLE_FACTURA.COMPANIA      = SF_FACTURA.COMPANIA
                          AND SF_DETALLE_FACTURA.TIPO_FACTURA = SF_FACTURA.TIPO_FACTURA
                          AND SF_DETALLE_FACTURA.FACTURA      = SF_FACTURA.NUMERO_FACTURA
                          INNER JOIN SF_CREE_CONCEPTOS
                          ON SF_CONCEPTOS.COMPANIA   = SF_CREE_CONCEPTOS.COMPANIA
                          AND SF_CONCEPTOS.ANO       = SF_CREE_CONCEPTOS.ANO
                          AND SF_CONCEPTOS.TIPOCOBRO = SF_CREE_CONCEPTOS.TIPOCOBRO
                          AND SF_CONCEPTOS.CODIGO    = SF_CREE_CONCEPTOS.CONCEPTO
                          WHERE SF_FACTURA.COMPANIA   = UN_COMPANIA
                            AND SF_FACTURA.TIPOCOBRO    = UN_TIPO
                            AND SF_FACTURA.CODIGO_COBRO = UN_NUMERO
                            AND SF_FACTURA.TERCERO      = UN_TERCERO
                            AND SF_FACTURA.SUCURSAL     = UN_SUCURSAL

      )LOOP

          IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                    UN_NOMBRE    => 'SF NRO FACTURA MANUAL COMO NRO COMPROBANTE',
                                    UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                    UN_FECHA_PAR => SYSDATE,
                                    UN_IND_MAYUS =>0),'NO') = 'SI' THEN

             MI_NROCPTE := MI_RS2.NUMERO_FACTURA;

          ELSE   

             MI_NROCPTE := UN_NUMERO;                      

          END IF; 

          IF MI_CLASECONTABLE IN ('V') THEN
           MI_DEBITO := NVL(MI_RS2.CTADEBITOCREE, '');
           MI_CREDITO := NVL(MI_RS2.CTACREDITOCREE, '');
          ELSE
           MI_CREDITO := NVL(MI_RS2.CTADEBITOCREE, '');
           MI_DEBITO := NVL(MI_RS2.CTACREDITOCREE, '');
          END IF;

     IF NVL(MI_RS2.VALOR_CREE, 0) <> 0 THEN

        IF MI_DEBITO IS NOT NULL THEN

             MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_DEBITO);

             IF MI_PARMANEJATERCERO  = 'SI'THEN

                SELECT TERCERO, SUCURSAL 
                INTO  MI_STRTERCEROAUX,
                      MI_STRSUCURSALAUX
                FROM SF_CUENTAS_AUXTERCERO 
                WHERE  COMPANIA = UN_COMPANIA 
                  AND ANO = MI_ANIO 
                  AND TIPOCOBRO = UN_TIPO
                  AND CONCEPTO = MI_RS.CODIGO
                  AND CUENTA = MI_DEBITO ;

             ELSE
                MI_STRTERCEROAUX :=  MI_RS.TERCERO;
                MI_STRSUCURSALAUX := MI_RS.SUCURSAL;
             END IF;

             MI_CONSECUTIVO := MI_CONSECUTIVO + 1;               

             MI_CAMPOS := 'COMPANIA
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
                         , BASE_GRAVABLE';

             MI_VALORES := ' '''||UN_COMPANIA||'''
                          ,'||MI_ANIO||'
                          ,'''||UN_TIPO||'''
                          ,'||MI_NROCPTE||'
                          ,'||MI_CONSECUTIVO||'
                          ,'''||MI_DEBITO||'''
                          ,'''||UN_FECHA||'''
                          ,'''||MI_NATURALEZA||'''
                          ,'||MI_RS2.VALOR_CREE||' 
                          ,0
                          ,'||NVL(MI_RS2.VALOR_CREE, 0)||'
                          ,0
                          ,'''||NVL(MI_RS2.CENTRO_COSTO, 9999999999)||'''
                          ,'''||MI_STRTERCEROAUX||'''
                          ,'''||MI_STRSUCURSALAUX||'''
                          ,'''||Nvl(MI_RS2.AUXILIAR, 9999999999999999)||'''
                          , ''
                          ,'''||UN_DESCRIPCION||'''
                          ,'||NVL(MI_RS2.BASE_GRAVABLE, 0)||' ';


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
                       MI_MSGERROR(2).VALOR := UN_TIPO;

                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                                UN_EXC_COD =>SQLCODE
                                                ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                ,UN_REEMPLAZOS  => MI_MSGERROR);

                  END;                                    

             MI_CONSC := TRUE;  

          END IF;

     --***************** CREDITO *******************

        IF MI_CREDITO IS NOT NULL THEN

       MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_DEBITO);

             IF MI_PARMANEJATERCERO  = 'SI'THEN

                SELECT TERCERO, SUCURSAL 
                INTO  MI_STRTERCEROAUX,
                      MI_STRSUCURSALAUX
                FROM SF_CUENTAS_AUXTERCERO 
                WHERE  COMPANIA = UN_COMPANIA 
                  AND ANO = MI_ANIO 
                  AND TIPOCOBRO = UN_TIPO
                  AND CONCEPTO = MI_RS.CODIGO
                  AND CUENTA = MI_DEBITO ;

             ELSE
                MI_STRTERCEROAUX :=  MI_RS.TERCERO;
                MI_STRSUCURSALAUX := MI_RS.SUCURSAL;
             END IF;


             IF MI_CONSC  THEN
              MI_CONSECUTIVO := MI_CONSECUTIVO + 1;               
              MI_CONSC := FALSE;
             END IF;


             MI_CAMPOS := 'COMPANIA
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
                         , NROCONTRATOSIGEC
                         , TIPOCONTRATOSIGEC';   

             MI_VALORES := ' '''||UN_COMPANIA||'''
                          ,'||MI_ANIO||'
                          ,'''||UN_TIPO||'''
                          ,'||MI_NROCPTE||'
                          ,'||MI_CONSECUTIVO||'
                          ,'''||MI_CREDITO||'''
                          ,'''||UN_FECHA||'''
                          ,'''||MI_NATURALEZA||'''
                          ,0
                          ,'||MI_RS2.VALOR_CREE||' 
                          ,0
                          ,'||NVL(MI_RS2.VALOR_CREE, 0)||'
                          ,'''||NVL(MI_RS2.CENTRO_COSTO, 9999999999)||'''
                          ,'''||MI_STRTERCEROAUX||'''
                          ,'''||MI_STRSUCURSALAUX||'''
                          ,'''||NVL(MI_RS2.AUXILIAR, 9999999999999999)||'''
                          ,''
                          ,'''||UN_DESCRIPCION||'''
                          ,'||NVL(MI_RS2.BASE_GRAVABLE, 0)||' 
                          ,'''||NVL(MI_RS2.NROCONTRATOSIGEC, 0)||'''
                          ,'''||NVL(MI_RS2.TIPOCONTRATOSIGEC, 0)||'''';

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
                       MI_MSGERROR(2).VALOR := UN_TIPO;

                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                                UN_EXC_COD =>SQLCODE
                                                ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                ,UN_REEMPLAZOS  => MI_MSGERROR);

                  END;                                    

             MI_CONSC := TRUE;    
    END IF; 

      ELSE  

         CONTINUE;
         MI_CONSC := TRUE; 

      END IF;

    END LOOP;   

   END IF;


   END LOOP;
   
    -- TICKET 7748471 EFCM: SE ADICIONA PARAMETRO PARA NETEAR POR CUENTA CONTABLE, SOLO INICIALMENTE PARA FACTURACION
    MI_NETEAR := 0;
    IF ( NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,  UN_NOMBRE    => 'AGRUPAR Y CALCULAR VALOR NETO POR CUENTA CONTABLE',
                                    UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                    UN_FECHA_PAR => SYSDATE,
                                    UN_IND_MAYUS =>0),'NO') = 'SI' AND MI_CLASECONTABLE = 'V' ) THEN
        MI_NETEAR := -1;
    END IF;
    -- TICKET 7748471 FIN -- 

    MI_RTAINTERFAZ :=   PCK_CONTABILIZAR.FC_CONTABILIZAR( UN_COMPANIA         => UN_COMPANIA
                                              ,UN_TIPOCOMPROBANTE  => UN_TIPO
                                              ,UN_NUMERO           => MI_NUMERO
                                              ,UN_ANO              => MI_ANIO
                                              ,UN_FECHA            => UN_FECHA
                                              ,UN_TERCERO          => UN_TERCERO
                                              ,UN_SUCURSAL         => UN_SUCURSAL
                                              ,UN_DESCRIPCION      => UN_DESCRIPCION                                            
                                              ,UN_SIMPLE           => -1
                                              ,UN_INDIMPRESION     => 0                                            
                                              ,UN_INTOMITIRPPTAL   => -1
                                              ,UN_CONCILIAR        => 0
                                              ,UN_PLANO            => 0
                                              ,UN_NONETEA          => MI_NETEAR -- EFCM -1
                                              ,UN_CONTRATISTA      => 0
                                              ,UN_TEXTO            => 'Contabilizar'
                                              ,UN_CONTRATO         => 0
                                              ,UN_TIPOCONTRATO     => ''
                                              ,UN_NRO_DOCUMENTO    => ''
                                              ,UN_ALMDEP           => 0
                                              ,UN_TERCE            => 0
                                              ,UN_RESAUXGEN        => 0
                                              ,UN_USUARIO          => UN_USUARIO);



           --04/09/2018 @eamaya 

        BEGIN 
            BEGIN

                MI_CAMPOS := 'HORA    = SYSDATE ';            

                MI_CONDICION := ' COMPANIA = '''||UN_COMPANIA||'''
                              AND ANO      =  '||MI_ANIO||'
                              AND TIPO     = '''||UN_TIPO||'''
                              AND NUMERO   = '||MI_NUMERO||' ';

                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'COMPROBANTE_CNT',
                                            UN_ACCION    => 'M',
                                            UN_CAMPOS    => MI_CAMPOS,
                                            UN_CONDICION => MI_CONDICION);

             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
               RAISE PCK_EXCEPCIONES.EXC_FACTURACION;                         
             END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTUA_NROFAC); 
        END;

        --LVEGA 09/01/2025
         FOR MI_RS IN (SELECT SC.CUENTADEBITOBASE AS CUENTA, PC.EQUIV_SIGEC, SOC.NROCONTRATOSIGEC, SOC.TIPOCONTRATOSIGEC
                        FROM SF_DETALLE_COBRO SDC
                        INNER JOIN SF_OBJETO_COBRO SOC
                        ON SOC.COMPANIA = SDC.COMPANIA
                        AND SOC.ANO = SDC.ANO
                        AND SOC.TIPOCOBRO = SDC.TIPOCOBRO
                        AND SOC.CODIGO_COBRO = SDC.CODIGO_COBRO
                        INNER JOIN SF_CONCEPTOS SC
                         ON SC.COMPANIA = SDC.COMPANIA
                        AND SC.ANO = SDC.ANO
                        AND SC.TIPOCOBRO = SDC.TIPOCOBRO
                        AND SC.CODIGO = SDC.CONCEPTO
                        INNER JOIN PLAN_CONTABLE PC
                         ON PC.COMPANIA = SC.COMPANIA
                        AND PC.ANO = SC.ANO 
                        AND PC.CODIGO = SC.CUENTADEBITOBASE
                        WHERE SDC.COMPANIA = UN_COMPANIA
                        AND SDC.ANO = MI_ANIO
                        AND SDC.TIPOCOBRO = UN_TIPO
                        AND SDC.CODIGO_COBRO = UN_NUMERO)
         LOOP
         BEGIN
            BEGIN
                MI_CAMPOS := 'EQUIV_SIGEC  =  '''|| MI_RS.EQUIV_SIGEC ||'''
                              ,NROCONTRATOSIGEC = '''|| MI_RS.NROCONTRATOSIGEC ||'''
                              ,TIPOCONTRATOSIGEC = '''|| MI_RS.TIPOCONTRATOSIGEC ||'''';
                MI_CONDICION := ' COMPANIA = '''||UN_COMPANIA||'''
                              AND ANO      =  '||MI_ANIO||'
                              AND TIPO_CPTE     = '''||UN_TIPO||'''
                              AND COMPROBANTE   = '||MI_NUMERO||' 
                              AND CUENTA   = ''' || MI_RS.CUENTA ||''' ';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'DETALLE_COMPROBANTE_CNT',
                                            UN_ACCION    => 'M',
                                            UN_CAMPOS    => MI_CAMPOS,
                                            UN_CONDICION => MI_CONDICION);
             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
               RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
             END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTUA_NROFAC);
        END;
        END LOOP;
                        --LVEGA 09/01/2025

        BEGIN
                BEGIN 
                MI_CAMPOS := 'TIPO_SIGEC  =  2';--1;Anulación Liquidación;2;Liquidación;3;Anulación Pago;4;Pago
                MI_CONDICION :=' COMPANIA = '''||UN_COMPANIA||'''
                              AND ANO      =  '||MI_ANIO||'
                              AND TIPO     = '''||UN_TIPO||'''
                              AND NUMERO   = '||MI_NUMERO||' ';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'COMPROBANTE_CNT',
                                            UN_ACCION    => 'M',
                                            UN_CAMPOS    => MI_CAMPOS,
                                            UN_CONDICION => MI_CONDICION);
             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
               RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
             END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTUA_NROFAC);
        END;
                --LVEGA 09/01/2025
                FOR MI_RS IN (SELECT SC.CUENTADEBITOBASE AS CUENTA,SDC.VALOR_IVA,SDC.VALOR_RETEFUENTE,SDC.VALOR_ICA,SDC.VALOR_RETEIVA
                        FROM SF_DETALLE_COBRO SDC
                        INNER JOIN SF_OBJETO_COBRO SOC
                        ON SOC.COMPANIA = SDC.COMPANIA
                        AND SOC.ANO = SDC.ANO
                        AND SOC.TIPOCOBRO = SDC.TIPOCOBRO
                        AND SOC.CODIGO_COBRO = SDC.CODIGO_COBRO
                        INNER JOIN SF_CONCEPTOS SC
                        ON SC.COMPANIA = SDC.COMPANIA
                        AND SC.ANO = SDC.ANO
                        AND SC.TIPOCOBRO = SDC.TIPOCOBRO
                        AND SC.CODIGO = SDC.CONCEPTO
                        WHERE SDC.COMPANIA = UN_COMPANIA
                        AND SDC.ANO = MI_ANIO
                        AND SDC.TIPOCOBRO = UN_TIPO
                        AND SDC.CODIGO_COBRO = UN_NUMERO)
                LOOP
         BEGIN
            BEGIN
                MI_CAMPOS := 'VALOR_IVA = ''' || MI_RS.VALOR_IVA || ''',' ||
               'VALOR_RETEFUENTE = ''' || MI_RS.VALOR_RETEFUENTE || ''',' ||
               'VALOR_ICA = ''' || MI_RS.VALOR_ICA || ''',' ||
               'VALOR_RETEIVA = ''' || MI_RS.VALOR_RETEIVA || '''';--(CFBARRERA:CC:1481)

                MI_CONDICION := ' COMPANIA = '''||UN_COMPANIA||'''
                              AND ANO      =  '||MI_ANIO||'
                              AND TIPO_CPTE     = '''||UN_TIPO||'''
                              AND COMPROBANTE   = '||MI_NUMERO||' 
                              AND CUENTA   = ''' || MI_RS.CUENTA ||''' ';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'DETALLE_COMPROBANTE_CNT',
                                            UN_ACCION    => 'M',
                                            UN_CAMPOS    => MI_CAMPOS,
                                            UN_CONDICION => MI_CONDICION);
             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
               RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
             END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTUA_NROFAC);
        END;
        END LOOP;
               
         BEGIN 
               BEGIN 

                  MI_CAMPOS := 'ANO_CPTE    = '||MI_ANIO||',
                                TIPO_CPTE   = '''||UN_TIPO||''',
                                NRO_CPTE    = '||MI_NUMERO||',
                                INTERFAZADA = -1';

                  MI_CONDICION := 'COMPANIA       = '''||UN_COMPANIA||'''
                               AND TIPO_FACTURA   = '''||UN_TIPO||'''
                               AND NUMERO_FACTURA = '||MI_NUMERO_FACTURA||'
                               AND CODIGO_COBRO   = '||UN_NUMERO; --JM CC 3015

                   PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'SF_FACTURA',
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);               

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
               END;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                   UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTUA_NROFAC);  
            END;


        BEGIN 
            BEGIN


             --09/08/2018 @eamaya
             --MOD JM CC 3015 (el codigo cobro puede ser diferente del numero de factura)
                MI_CAMPOS := 'CPTE_CTBLE    = '''||UN_TIPO||''',
                              NROCPTE_CTBLE    = '||MI_NUMERO||' ';


                MI_CONDICION := ' COMPANIA     = '''||UN_COMPANIA||'''
                              AND TIPOCOBRO    = '''||UN_TIPO||'''
                              AND CODIGO_COBRO = '||UN_NUMERO||'
                              AND ANO          = '||MI_ANIO;
                 PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'SF_OBJETO_COBRO',
                                            UN_ACCION    => 'M',
                                            UN_CAMPOS    => MI_CAMPOS,
                                            UN_CONDICION => MI_CONDICION);
             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
               RAISE PCK_EXCEPCIONES.EXC_FACTURACION;                         
             END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,                                  
                                     UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTUA_NROFAC); 
        END;

-- CC_287 MROSERO Rutina para actualizar los consecutivos de la Detalle_comprobante a la Sf_detalle_cobro
	BEGIN

	    FOR MI_RS IN ( SELECT 
	                    DETALLE_COMPROBANTE_CNT.CONSECUTIVO,
	                    DETALLE_COMPROBANTE_CNT.CUENTA,
	                    SF_DETALLE_COBRO.CONCEPTO
	                FROM DETALLE_COMPROBANTE_CNT 
	                INNER JOIN PLAN_CONTABLE
	                    ON DETALLE_COMPROBANTE_CNT.COMPANIA = PLAN_CONTABLE.COMPANIA
	                    AND DETALLE_COMPROBANTE_CNT.ANO = PLAN_CONTABLE.ANO
	                    AND DETALLE_COMPROBANTE_CNT.CUENTA = PLAN_CONTABLE.CODIGO 
	                INNER JOIN SF_DETALLE_COBRO
	                    ON DETALLE_COMPROBANTE_CNT.COMPANIA = SF_DETALLE_COBRO.COMPANIA
	                    AND DETALLE_COMPROBANTE_CNT.ANO = SF_DETALLE_COBRO.ANO
	                    AND DETALLE_COMPROBANTE_CNT.COMPROBANTE = SF_DETALLE_COBRO.CODIGO_COBRO 
	                    AND DETALLE_COMPROBANTE_CNT.VALOR_DEBITO=SF_DETALLE_COBRO.VALOR_NETO
	                WHERE
	                    DETALLE_COMPROBANTE_CNT.COMPANIA = UN_COMPANIA
	                    AND DETALLE_COMPROBANTE_CNT.ANO = MI_ANIO
	                    AND DETALLE_COMPROBANTE_CNT.COMPROBANTE = MI_NUMERO
	                    AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = UN_TIPO
	                    AND PLAN_CONTABLE.CLASECUENTA = 'C' 
	                    ORDER BY CONSECUTIVO DESC) LOOP
	                    
	        MI_CAMPOS := 'CONSECUTIVO = ' || MI_RS.CONSECUTIVO;
	        MI_CONDICION := 'COMPANIA     = ''' || UN_COMPANIA || '''
	                         AND TIPOCOBRO    = ''' || UN_TIPO || '''
	                         AND CODIGO_COBRO = ' || MI_NUMERO || '
	                         AND CONCEPTO     = ''' || MI_RS.CONCEPTO || '''
	                         AND ANO          = ' || MI_ANIO;

	        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(  UN_TABLA     => 'SF_DETALLE_COBRO',
								                    UN_ACCION    => 'M',
								                    UN_CAMPOS    => MI_CAMPOS,
								                    UN_CONDICION => MI_CONDICION);
	
	    END LOOP; 
	
			EXCEPTION  WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
			        RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
	
	END;
-- fin de la rutina CC_287 MROSERO

        --09/08/2018 @eamaya

           --04/09/2018 @eamaya                                                    
        MI_RPTA := -1;

  RETURN MI_RPTA;

END FC_INTERFAZCONTABLE;

--5

FUNCTION FC_ACTCONTA0
/*
      NAME              : FC_ACTCONTA0 MIGRADO DE ACCESS actconta0
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 13/08/2018
      TIME              : 12:55 PM
      SOURCE MODULE     : SysmanSF2018.07.06
      DESCRIPTION       : Actualiza los saldos en la tabla de saldos del plan contable de la jerarquía de un código determinado,
                          incluyendo el código si el origen es 1 o si el indicador de actualizar datos en línea es SI
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 

    @NAME:   actualizarConta0
    @METHOD: GET
*/
(
  UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,  
  UN_ANO         IN PCK_SUBTIPOS.TI_TIPO_COBRO,
  UN_MES         IN PCK_SUBTIPOS.TI_ANIO,
  UN_A_ID        IN PLAN_CONTABLE.CODIGO%TYPE,
  UN_DEB_ANT     IN PLAN_CONTABLE.DEBITO0%TYPE,
  UN_CRE_ANT     IN PLAN_CONTABLE.CREDITO0%TYPE,
  UN_DIF_ANT     IN PLAN_CONTABLE.NETO0%TYPE,
  UN_DEBITO      IN PLAN_CONTABLE.DEBITO0%TYPE,
  UN_CREDITO     IN PLAN_CONTABLE.CREDITO0%TYPE,
  UN_DIFERENCIA  IN PLAN_CONTABLE.NETO0%TYPE,
  UN_NATURALEZA  IN PLAN_CONTABLE.NATURALEZA%TYPE,
  UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO  
)
RETURN PCK_SUBTIPOS.TI_LOGICO AS 
    MI_RPTA        PCK_SUBTIPOS.TI_LOGICO;
    MI_CAMPOS      PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION   PCK_SUBTIPOS.TI_CONDICION;
BEGIN

  IF UN_DEB_ANT = 0 AND UN_CRE_ANT = 0 AND UN_DEBITO = 0 AND UN_CREDITO = 0 THEN

      RETURN 0;

  END IF ;

   MI_CAMPOS := 'DEBITO'||UN_MES||' = DEBITO'||UN_MES||' + '||UN_DEBITO||'-'||UN_DEB_ANT||',
                CREDITO'||UN_MES||' = CREDITO'||UN_MES||' + '||UN_CREDITO||'-'||UN_CRE_ANT||',
                NETO'||UN_MES||' = NETO'||UN_MES||' + CASE WHEN NATURALEZA = '''||UN_NATURALEZA||''' THEN '||UN_DIFERENCIA||' ELSE '||-UN_DIFERENCIA||' END
                                                     - CASE WHEN NATURALEZA = '''||UN_NATURALEZA||''' THEN '||UN_DIF_ANT||' ELSE '||-UN_DIF_ANT||' END ';


    FOR I IN  UN_MES..13 LOOP

        MI_CAMPOS := MI_CAMPOS ||',';

        MI_CAMPOS := MI_CAMPOS ||'SALDO'||I||' = '||'SALDO'||I||' + CASE WHEN NATURALEZA = '''||UN_NATURALEZA||''' THEN '||UN_DIFERENCIA||' ELSE '||-UN_DIFERENCIA||' END
                                                                  - CASE WHEN NATURALEZA = '''||UN_NATURALEZA||''' THEN '||UN_DIF_ANT||' ELSE '||-UN_DIF_ANT||' END ';

    END LOOP;            

    MI_CONDICION := 'COMPANIA ='''||UN_COMPANIA||'''
                 AND ANO = '||UN_ANO||'
                 AND CODIGO BETWEEN '''||SUBSTR(UN_A_ID, 1)||'''
                 AND '''||UN_A_ID||'''
                 AND CODIGO = SUBSTR('''||UN_A_ID||''',LENGTH(CODIGO))';

   BEGIN
    BEGIN
               PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  'PLAN_CONTABLE', 
                                                 UN_ACCION    =>  'M', 
                                                 UN_CAMPOS    =>  MI_CAMPOS, 
                                                 UN_CONDICION =>  MI_CONDICION );

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;

        END;
           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN    

                    PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD   => SQLCODE,
                    UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_ACTCONTA0

                 );

            RETURN 0;     
       END;

  RETURN -1;

END FC_ACTCONTA0;

FUNCTION FC_SALARIOMINIMO
/*
      NAME              : FC_SALARIOMINIMO
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 23/08/2018
      TIME              : 12:48 PM
      SOURCE MODULE     : SysmanSF2018.07.06
      DESCRIPTION       :
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 

    @NAME:   extraerSalarioMinimo
    @METHOD: GET
*/
(
  UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANO        IN PCK_SUBTIPOS.TI_ANIO
)
RETURN PCK_SUBTIPOS.TI_DOBLE AS 
  MI_SALARIOMINIMO  ANO.SALARIOMINIMO%TYPE;
  MI_RPTA           ANO.SALARIOMINIMO%TYPE;

BEGIN

     SELECT SALARIOMINIMO
       INTO MI_SALARIOMINIMO
      FROM ANO
     WHERE COMPANIA = UN_COMPANIA
       AND NUMERO   = UN_ANO;

     RETURN MI_SALARIOMINIMO;

      EXCEPTION WHEN NO_DATA_FOUND THEN  

     MI_RPTA := 0;

  RETURN MI_RPTA;

END FC_SALARIOMINIMO;

--7

FUNCTION FC_REEMPLAZARFORMULA
/*
      NAME              : FC_REEMPLAZARFORMULA
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 23/08/2018
      TIME              : 12:48 PM
      SOURCE MODULE     : SysmanSF2018.07.06
      DESCRIPTION       :
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 

    @NAME:   reemplazarFormula
    @METHOD: GET
*/
(
  UN_FORMULA        IN PCK_SUBTIPOS.TI_STRSQL
)
RETURN PCK_SUBTIPOS.TI_DOBLE AS 

  MI_RPTA           ANO.SALARIOMINIMO%TYPE;
  MI_STRSQL         PCK_SUBTIPOS.TI_STRSQL;
BEGIN

  MI_STRSQL := 'SELECT '||UN_FORMULA||'                 
                FROM DUAL';

  EXECUTE IMMEDIATE MI_STRSQL INTO MI_RPTA;

  RETURN MI_RPTA;

     EXCEPTION WHEN NO_DATA_FOUND THEN 

       MI_RPTA := 0; 

  RETURN MI_RPTA;

END FC_REEMPLAZARFORMULA;

--8

FUNCTION FC_VERIFICARDESCCOBRO
/*
      NAME              : FC_VERIFICARDESCCOBRO => en Access VerificaDescuentoCobro()
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 07/09/2018
      TIME              : 12:48 AM
      SOURCE MODULE     : SysmanSF2018.07.06
      DESCRIPTION       :
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 

    @NAME:   verificarDescuentoCobro
    @METHOD: GET
*/
(
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOFACTURA    IN SF_OBJETO_COBRO.TIPOCOBRO%TYPE,
  UN_NUMEROFACTURA  IN SF_OBJETO_COBRO.NRO_FACTURA%TYPE
)
RETURN PCK_SUBTIPOS.TI_LOGICO AS

  MI_CODIGOCOBRO    SF_OBJETO_COBRO.CODIGO_COBRO%TYPE;
  MI_RTA            PCK_SUBTIPOS.TI_LOGICO;
  MI_CANT           PCK_SUBTIPOS.TI_ENTERO; 
  MI_DESCUENTO      VARCHAR2(10 CHAR); 
BEGIN

  MI_RTA := -1;
  MI_DESCUENTO := NULL;
  BEGIN
  SELECT CODIGO_COBRO
    INTO MI_CODIGOCOBRO
    FROM SF_OBJETO_COBRO
  WHERE  COMPANIA    = UN_COMPANIA
    AND  TIPOFACTURA = UN_TIPOFACTURA
    AND  NRO_FACTURA = UN_NUMEROFACTURA;

  EXCEPTION WHEN NO_DATA_FOUND THEN 
     MI_RTA := 0;
     RETURN MI_RTA;
  END ; 

  BEGIN

  SELECT 'X' DESCUENTO 
   INTO MI_DESCUENTO 
    FROM SF_DETALLE_COBRO 
  WHERE SF_DETALLE_COBRO.COMPANIA     = UN_COMPANIA
    AND SF_DETALLE_COBRO.TIPOCOBRO    = UN_TIPOFACTURA
    AND SF_DETALLE_COBRO.CODIGO_COBRO = MI_CODIGOCOBRO
    AND SF_DETALLE_COBRO.DESCUENTO NOT IN(0);

   EXCEPTION WHEN NO_DATA_FOUND THEN 
     MI_RTA := 0;  
     RETURN MI_RTA;
  END ; 

  IF MI_DESCUENTO IS NOT NULL THEN 
    MI_RTA := -1;    
  END IF;

  RETURN MI_RTA;

END FC_VERIFICARDESCCOBRO;

--9

FUNCTION FC_RECAUDARFACTURA
/*
      NAME              : FC_RECAUDARFACTURA => en Access RecaudarFactura()
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 07/09/2018
      TIME              : 12:10 AM
      SOURCE MODULE     : SysmanSF2018.07.06
      DESCRIPTION       :
      MODIFIER          :
      DATE MODIFIED     :
      TIME MODIFIED     :
      MODIFICATIONS     :

    @NAME:   recaudarFactura
    @METHOD: GET
*/
(
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOFACTURA    IN SF_OBJETO_COBRO.TIPOCOBRO%TYPE,
  UN_NUMEROFACTURA  IN SF_OBJETO_COBRO.NRO_FACTURA%TYPE,
  UN_OBSERVACION    IN SF_FACTURA.OBSERVACIONES%TYPE,
  UN_CUENTA         IN DETALLE_COMPROBANTE_CNT.CUENTA%TYPE,
  UN_FECHA          IN DATE,
  UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,  
  UN_DIFERIDA       IN SF_OBJETO_COBRO.DIFERIDA%TYPE,
  UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO,
  UN_VALORRECAUDO   IN PCK_SUBTIPOS.TI_DOBLE := 0   --(MZANGUNA:15/04/2019)-Para pagos parciales desde wsRentasCaqueta
)
RETURN VARCHAR2 AS

  MI_CODIGOCOBRO    SF_OBJETO_COBRO.CODIGO_COBRO%TYPE;
  MI_RTA            VARCHAR2(100 CHAR);
  MI_DESCRIPCION    DETALLE_COMPROBANTE_CNT.DESCRIPCION%TYPE;
  MI_NOCAUSAR       SF_TIPO_COBRO.NOAPLICACAUSACION%TYPE;
  MI_VERCOMPAGOS    VARCHAR2(100 CHAR);
  MI_STRSQL         PCK_SUBTIPOS.TI_STRSQL;
  MI_CONTEO         PCK_SUBTIPOS.TI_ENTERO;
  MI_CLASE          PCK_SUBTIPOS.TI_CLASECOMPROBANTE;
  MI_RSFACTURADOS   SYS_REFCURSOR;
  MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
  MI_CAMPOS1        PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
  MI_CUENTA         DETALLE_COMPROBANTE_CNT.CUENTA%TYPE;
  MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_CPTERECAUDO    SF_TIPO_COBRO.CPTE_RECAUDO%TYPE;
  MI_CONSECUTIVO    COMPROBANTE_CNT.NUMERO%TYPE;
  MI_APLICAREC      BOOLEAN DEFAULT FALSE;
  MI_FACRECAUDOF    COMPROBANTE_CNT.NUMERO%TYPE;
  MI_APLCUENTAREC   SF_TIPO_COBRO.APLCUENTAREC%TYPE;
  MI_CAUSARFACTRECAUDO     SF_TIPO_COBRO.INTERFAZ_RECAUDO%TYPE;
  MI_CONCEPTO       SF_DETALLE_FACTURA.CONCEPTO%TYPE;
  MI_PAR_PAGOPAR     VARCHAR2(100 CHAR);
  MI_APLICA_PAGOPAR  BOOLEAN DEFAULT FALSE;
  MI_EQUIV_SIGEC                    DETALLE_COMPROBANTE_CNT.EQUIV_SIGEC%TYPE;
  MI_NROCONTRATOSIGEC               DETALLE_COMPROBANTE_CNT.NROCONTRATOSIGEC%TYPE;
  MI_TIPOCONTRATOSIGEC              DETALLE_COMPROBANTE_CNT.TIPOCONTRATOSIGEC%TYPE;
  MI_CUENTASIGEC                    DETALLE_COMPROBANTE_CNT.CUENTA%TYPE;
  MI_RS_SIGEC                             SYS_REFCURSOR;
   MI_RECAUDADA                      DATE; -- JM CC 4110 (otra validacion)
BEGIN
    --INI JM CC 4110 otra validacion para los que tienen varias pestañas abiertas y el DSS ya cargado en cache 
    BEGIN 
        SELECT FECHA_PAGO 
            INTO MI_RECAUDADA
            FROM SF_FACTURA
            WHERE COMPANIA   = UN_COMPANIA
            AND TIPO_FACTURA = UN_TIPOFACTURA
            AND NUMERO_FACTURA = UN_NUMEROFACTURA;
            EXCEPTION WHEN OTHERS THEN
            MI_MSGERROR(1).CLAVE := 'FACTURA';
            MI_MSGERROR(1).VALOR := UN_NUMEROFACTURA;
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTFACTURA,
              UN_REEMPLAZOS  => MI_MSGERROR);
    END;
    IF MI_RECAUDADA IS NOT NULL THEN 
         BEGIN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
                    MI_MSGERROR(1).CLAVE := 'FACTURA';
                    MI_MSGERROR(1).VALOR := UN_NUMEROFACTURA;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_COMPRELACIONADO,
                      UN_REEMPLAZOS  => MI_MSGERROR);
        END;
    END IF; 
    --FIN JM CC 4110

    MI_CUENTA := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                              UN_NOMBRE    => 'SF CUENTA DE RECAUDO',
                                              UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                              UN_FECHA_PAR => SYSDATE,
                                              UN_IND_MAYUS => 0
                                              );

    MI_DESCRIPCION := 'Recaudo de la factura '||UN_TIPOFACTURA||' - '||UN_NUMEROFACTURA||' -- '||UN_OBSERVACION ;

    MI_VERCOMPAGOS := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                              UN_NOMBRE    => 'SF PERMITE VERIFICAR SI YA EXISTE PAGOS A LA FACTURA',
                                              UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                              UN_FECHA_PAR => SYSDATE,
                                              UN_IND_MAYUS => 0
                                              );
                                              
    MI_PAR_PAGOPAR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                          UN_NOMBRE    => 'SF APLICA PAGOS PARCIALES',
                                          UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                          UN_FECHA_PAR => SYSDATE,
                                          UN_IND_MAYUS => 0
                                                ),'NO');

    SELECT SF_TIPO_COBRO.NOAPLICACAUSACION,TIPO_COMPROBANTE.CLASE_CONTABLE,CPTE_RECAUDO,SF_TIPO_COBRO.APLCUENTAREC      
    INTO MI_NOCAUSAR, MI_CLASE,MI_CPTERECAUDO,MI_APLCUENTAREC
    FROM SF_TIPO_COBRO
    INNER JOIN TIPO_COMPROBANTE
    ON SF_TIPO_COBRO.COMPANIA    = TIPO_COMPROBANTE.COMPANIA
    AND SF_TIPO_COBRO.CODIGO     = TIPO_COMPROBANTE.CODIGO
    WHERE SF_TIPO_COBRO.COMPANIA = UN_COMPANIA
      AND SF_TIPO_COBRO.ANO      = UN_ANIO
      AND SF_TIPO_COBRO.CODIGO   = UN_TIPOFACTURA;

    FOR MI_RS IN (SELECT F.TIPOCOBRO
                       ,F.NUMERO_FACTURA
                       ,F.CODIGO_COBRO
                       ,F.NRO_CPTE NRO_FACTURA
                       ,F.OBSERVACIONES
                       ,F.TERCERO
                       ,F.SUCURSAL
                       ,F.FECHA_EXPEDICION
                       ,F.VALOR_TOTAL
                       ,F.GRUPO_CONCEPTOS
                       ,F.FECHA_REGISTRO
                       ,F.CANTIDAD_GRUPO
                       ,SO.FECHA_SOLICITUD
                       ,SO.IND_PAGOPARCIAL
                 FROM  SF_FACTURA F INNER JOIN SF_OBJETO_COBRO  SO
                         ON F.COMPANIA =  SO.COMPANIA
                        AND F.TIPOCOBRO = SO.TIPOCOBRO
                        AND F.CODIGO_COBRO = SO.CODIGO_COBRO
                 WHERE F.COMPANIA       = UN_COMPANIA
                   AND F.TIPO_CPTE      = UN_TIPOFACTURA
                   AND F.NUMERO_FACTURA = UN_NUMEROFACTURA
                  -- AND F.ANO            = UN_ANIO
    )
    LOOP
    BEGIN
    
    MI_APLICA_PAGOPAR := CASE WHEN (UN_VALORRECAUDO <> 0 AND UN_VALORRECAUDO > 0 AND UN_VALORRECAUDO <> MI_RS.VALOR_TOTAL AND MI_PAR_PAGOPAR = 'SI') OR (MI_RS.IND_PAGOPARCIAL = -1) THEN TRUE ELSE FALSE END;
    IF MI_APLICA_PAGOPAR THEN
                    
        PCK_DATOS.GL_RTA  :=   PCK_FACT_GENERAL_COM4.FC_RECAUDAR_PAGO_PARCIAL(UN_COMPANIA           => UN_COMPANIA,
                                                                                          UN_TIPOFACTURA        => UN_TIPOFACTURA,
                                                                                          UN_NUMEROFACTURA      => UN_NUMEROFACTURA, 
                                                                                          UN_OBSERVACION        => UN_OBSERVACION,
                                                                                          UN_CUENTA             => UN_CUENTA,
                                                                                          UN_FECHA              => UN_FECHA,
                                                                                          UN_ANIO               => UN_ANIO,
                                                                                          UN_DIFERIDA           => UN_DIFERIDA,
                                                                                          UN_USUARIO            => UN_USUARIO,
                                                                                          UN_VALORRECAUDO       => UN_VALORRECAUDO);
                    
        MI_CONSECUTIVO := PCK_DATOS.GL_RTA;
                    
      ELSE
    
        BEGIN
            BEGIN
                --(MZANGUNA:03/05/2019)
                MI_APLICAREC := CASE WHEN UN_VALORRECAUDO <> 0 AND UN_VALORRECAUDO > 0 AND UN_VALORRECAUDO <> MI_RS.VALOR_TOTAL THEN TRUE ELSE FALSE END;
                IF MI_APLICAREC THEN    --Saca la distribuci? del comprobante de recaudo.
                    MI_FACRECAUDOF := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO
                        (UN_TABLA       => 'SF_OBJETO_COBRO'
                        ,UN_CRITERIO    => 'COMPANIA = '''|| UN_COMPANIA || ''' AND TIPOCOBRO = '''|| UN_TIPOFACTURA ||''' AND ANO = '|| UN_ANIO||''
                        ,UN_CAMPO       => 'CODIGO_COBRO'
                        ,UN_INICIAL     => '000001'
                        );
                    BEGIN
                        BEGIN
                            MI_CAMPOS1 :=  'COMPANIA,ANO,TIPOCOBRO,TERCERO,SUCURSAL,CENTRO_COSTO,AUXILIAR,FECHA_SOLICITUD,FECHA_REGISTRO,NIT_ANUNCIANTE,SUCURSAL_ANUNCIANTE,NIT_PROPIETARIO
                                        ,SUCURSAL_PROPIETARIO,VALOR_TOTAL,REFERENCIA_OBJCOBRO,ESTRATO,TIPOFACTURA,NRO_FACTURA,IMPRESO,TIPOCONTRATO,CONTRATO,CCOSTO_CONTRATO,PERIODOCONTRATO
                                        ,TIPO_ACUERDO,NRO_ACUERDO,CUOTA_ACUERDO,DIFERIDA,TIPO_ABONO,NRO_ABONO,VLR_EFECTIVO,VLR_CREDITO,CUOTAS_DIFERIDAS,VLR_PROM_CUOTA,CUENTA_RECAUDO,TIPOCPTE_RECAUDO,NIT_TERCEROFACTURADO
                                        ,SUCURSAL_TERCEROFACTURADO,NOMBRE_TERCEROFACTURADO,FORMULARIO_DELURBANA,TASA_INTERES,DIRECCION,CODIGOPOSTAL,GRUPO_CONCEPTOS,TOTAL_GRUPO,CANTIDAD_GRUPO,FECHA_VENCIMIENTO
                                        ,CPTE_CTBLE,NROCPTE_CTBLE,BASEORIGINAL,NUMERO_LIQUIDACION,FECHA_PRELIQUIDACION,COD_DESCRIPCION,COD_TARIFABASE,OBSERVACIONES,FUENTE_RECURSO,REVERSO_N_COMPROBANTE,REVERSO_TIPO_COMPROBANTE
                                        ,REVERSO_N_DOCUMENTO,REVERSO_FECHA_CREACION,NRO_DOCUMENTO';
                            MI_CAMPOS := MI_CAMPOS1 || ', CODIGO_COBRO, BASE_FIJA_ANT ';

                            MI_VALORES := ' SELECT ' || MI_CAMPOS1 || ',' || MI_FACRECAUDOF || ' CODIGO_COBRO, '|| UN_VALORRECAUDO || ' BASE_FIJA_ANT
                                            FROM SF_OBJETO_COBRO
                                            WHERE COMPANIA     = '''|| UN_COMPANIA ||'''
                                              AND ANO          = '|| UN_ANIO ||'
                                              AND TIPOCOBRO    = '''|| MI_RS.TIPOCOBRO ||'''
                                              AND CODIGO_COBRO = '|| MI_RS.CODIGO_COBRO ||'';



                            MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA  => 'SF_OBJETO_COBRO',
                                                              UN_ACCION   => 'IS',
                                                              UN_CAMPOS   => MI_CAMPOS,
                                                              UN_VALORES  => MI_VALORES);
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                        END;

                        PCK_FACT_GENERAL_COM1.PR_CARGAR_CONCEPTOS
                            (UN_COMPANIA          => UN_COMPANIA
                            ,UN_ANIO              => UN_ANIO
                            ,UN_TIPOCOBRO         => MI_RS.TIPOCOBRO
                            ,UN_CODIGO_COBRO      => MI_FACRECAUDOF
                            ,UN_GRUPO_CONCEPTOS   => MI_RS.GRUPO_CONCEPTOS
                            ,UN_FECHA_SOLICITUD   => MI_RS.FECHA_SOLICITUD
                            ,UN_CANTIDAD_GRUPO    => MI_RS.CANTIDAD_GRUPO
                            ,UN_USUARIO           => UN_USUARIO
                            );

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
                        MI_MSGERROR(1).CLAVE := 'NUMERO';
                        MI_MSGERROR(1).VALOR := MI_RS.CODIGO_COBRO;
                        MI_MSGERROR(2).CLAVE := 'TIPO';
                        MI_MSGERROR(2).VALOR :=  MI_RS.TIPOCOBRO ;
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                       UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSCOMPRINGRESO
                                                        ,UN_REEMPLAZOS  => MI_MSGERROR);
                    END;

                END IF; --Fin_(MZANGUNA:03/05/2019)

                MI_CONSECUTIVO := PCK_CONTABILIDAD_CPTE.FC_ENUMERARCOMPROBANTECNT(UN_COMPANIA     => UN_COMPANIA
                                                                                       ,UN_ANIO         => PCK_SYSMAN_UTL.FC_ANIO(UN_FECHA)
                                                                                       ,UN_TIPO         => MI_CPTERECAUDO
                                                                                       ,UN_NUMERO       => 0
                                                                                       ,UN_CENTRO_COSTO => '');


                MI_CAMPOS :='COMPANIA ,
                                  ANO ,
                                  TIPO ,
                                  NUMERO ,
                                  FECHA ,
                                  HORA,
                                  DESCRIPCION ,
                                  TERCERO ,
                                  SUCURSAL ,
                                  VLR_BASE ,
                                  ANULADO ,
                                  VLR_DOCUMENTO ,
                                  TEXTO ,
                                  NUMEROCONTRATO ,
                                  TIPOCONTRATO ,
                                  VLRAGIRAR ,
                                  NRO_DOCUMENTO ,
                                  FECHA_VCN_DOC ,
                                  CREATED_BY ,
                                  DATE_CREATED';

              SELECT INTERFAZ_RECAUDO
              INTO MI_CAUSARFACTRECAUDO
              FROM SF_TIPO_COBRO
             WHERE COMPANIA   = UN_COMPANIA
               AND ANO        = UN_ANIO
               AND CODIGO     = UN_TIPOFACTURA;       


                MI_VALORES := 'SELECT COMPANIA ,
                                          '||PCK_SYSMAN_UTL.FC_ANIO(UN_FECHA)||' ,
                                          '''||MI_CPTERECAUDO||''' TIPO ,
                                          '||MI_CONSECUTIVO||' ,
                                          '''||UN_FECHA||''' ,
                                          SYSDATE,
                                          '''||MI_DESCRIPCION||''' ,
                                          TERCERO ,
                                          SUCURSAL ,
                                          '|| CASE WHEN MI_APLICAREC THEN ''|| UN_VALORRECAUDO ||'' ELSE 'VLR_BASE' END ||'  VLR_BASE,
                                          ANULADO ,
                                          '|| CASE WHEN MI_APLICAREC THEN ''||  UN_VALORRECAUDO ||'' ELSE 'VLR_DOCUMENTO' END ||'  VLR_DOCUMENTO,
                                          TEXTO ,
                                          NUMEROCONTRATO ,
                                          TIPOCONTRATO ,
                                          '|| CASE WHEN MI_APLICAREC THEN ''|| UN_VALORRECAUDO ||'' ELSE 'VLRAGIRAR' END ||'  VLRAGIRAR,
                                          NRO_DOCUMENTO ,
                                          FECHA_VCN_DOC ,
                                          '''||UN_USUARIO||''' ,
                                          DATE_CREATED
                                     FROM COMPROBANTE_CNT
                                     WHERE COMPANIA = '''||UN_COMPANIA||'''
                                       AND ANO      = '|| CASE WHEN MI_CAUSARFACTRECAUDO <> 0 THEN PCK_SYSMAN_UTL.FC_ANIO(UN_FECHA) ELSE UN_ANIO END ||'
                                       AND TIPO     = '''||UN_TIPOFACTURA||'''
                                       AND NUMERO   = '||CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, UN_NOMBRE    => 'SF NRO FACTURA MANUAL COMO NRO COMPROBANTE', UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL, UN_FECHA_PAR => SYSDATE, UN_IND_MAYUS =>0),'NO') = 'SI' THEN MI_RS.NUMERO_FACTURA ELSE MI_RS.CODIGO_COBRO END||' '; --MOD JM CC 3015


                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA  => 'COMPROBANTE_CNT',
                                                      UN_ACCION   => 'IS',
                                                      UN_CAMPOS   => MI_CAMPOS,
                                                      UN_VALORES  => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
            MI_MSGERROR(1).CLAVE := 'NUMERO';
            MI_MSGERROR(1).VALOR := CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, UN_NOMBRE    => 'SF NRO FACTURA MANUAL COMO NRO COMPROBANTE', UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL, UN_FECHA_PAR => SYSDATE, UN_IND_MAYUS =>0),'NO') = 'SI' THEN MI_RS.NUMERO_FACTURA ELSE MI_RS.CODIGO_COBRO END; --mod JM CC 3015
            MI_MSGERROR(2).CLAVE := 'TIPO';
            MI_MSGERROR(2).VALOR := MI_CPTERECAUDO;
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSCOMPRINGRESO
                                            ,UN_REEMPLAZOS  => MI_MSGERROR);
        END;

        IF MI_NOCAUSAR NOT IN (0) THEN
            MI_DESCRIPCION := 'Aqui hace falta realizar interfazcontablenofacturado()';
        ELSE
            IF MI_RS.TIPOCOBRO IS NOT NULL AND MI_RS.CODIGO_COBRO IS NOT NULL THEN
                IF UN_DIFERIDA NOT IN (0) THEN
                    MI_DESCRIPCION := 'Aqui hace falta realizar generarIngresoAbono()()';
                ELSE
                    --Antes de realiza el ingreso se debe validar si ya existe un comprobante que afecta  esta factura
                    IF MI_VERCOMPAGOS = 'SI' THEN
                        MI_STRSQL := 'SELECT COUNT(*) CANTIDAD
                                    FROM DETALLE_COMPROBANTE_CNT
                                    WHERE COMPANIA         = '''||UN_COMPANIA||'''
                                      AND ANO_AFECT        = '||UN_ANIO||'
                                      AND TIPO_CPTE_AFECT  = '''||UN_TIPOFACTURA||'''
                                      AND CMPTE_AFECTADO   = '||MI_RS.CODIGO_COBRO|| ' ';

                        EXECUTE IMMEDIATE 'SELECT COUNT(1) FROM ('||MI_STRSQL||')' INTO MI_CONTEO;

                        IF MI_CONTEO >= 1 THEN
                            OPEN MI_RSFACTURADOS FOR MI_STRSQL;
                            LOOP

                                FETCH MI_RSFACTURADOS INTO MI_CONTEO;
                                EXIT WHEN MI_RSFACTURADOS%NOTFOUND;
                                PCK_DATOS.GL_RTA  :=   PCK_FACT_GENERAL_COM3.FC_GENERAINGRESO(UN_COMPANIA      => UN_COMPANIA,
                                                                                    UN_ANIO              => UN_ANIO,
                                                                                    UN_TIPO_CPTE         => MI_CPTERECAUDO,
                                                                                    UN_COMPROBANTE       => MI_CONSECUTIVO,
                                                                                    UN_COMPROBANTE_AFEC  => CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, UN_NOMBRE    => 'SF NRO FACTURA MANUAL COMO NRO COMPROBANTE', UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL, UN_FECHA_PAR => SYSDATE, UN_IND_MAYUS =>0),'NO') = 'SI' THEN MI_RS.NUMERO_FACTURA ELSE MI_RS.CODIGO_COBRO END, --MOD JM CC 3015
                                                                                    UN_TIPO_AFEC         => UN_TIPOFACTURA,
                                                                                    UN_FECHA             => UN_FECHA,
                                                                                    UN_CUENTA            => UN_CUENTA,
                                                                                    UN_TERCERO           => MI_RS.TERCERO,
                                                                                    UN_SUCURSAL          => MI_RS.SUCURSAL,
                                                                                    UN_DESCRIPCION       => MI_DESCRIPCION,
                                                                                    UN_USUARIO           => UN_USUARIO,
                                                                                    UN_VALORRECAUDO      => CASE WHEN MI_APLICAREC THEN UN_VALORRECAUDO ELSE 0 END,
                                                                                    UN_TIPOCOBROFSIM     => CASE WHEN MI_APLICAREC OR UN_CUENTA IS NULL THEN MI_RS.TIPOCOBRO ELSE ' ' END,
                                                                                    UN_NUMEROFACTURASIM  => CASE WHEN MI_APLICAREC THEN MI_FACRECAUDOF WHEN UN_CUENTA IS NULL THEN MI_RS.CODIGO_COBRO ELSE 0 END);

                            END LOOP;
                        ELSE
                            BEGIN
                                BEGIN
                                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                                END;

                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
                                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                     UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_COMPRELACIONADO);
                            END;
                        END IF;
                    ELSE
                        PCK_DATOS.GL_RTA:=PCK_FACT_GENERAL_COM3.FC_GENERAINGRESO(UN_COMPANIA          => UN_COMPANIA,
                                                                           UN_ANIO              => UN_ANIO,
                                                                           UN_TIPO_CPTE         => MI_CPTERECAUDO,
                                                                           UN_COMPROBANTE       => MI_CONSECUTIVO,
                                                                           UN_COMPROBANTE_AFEC  => CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, UN_NOMBRE    => 'SF NRO FACTURA MANUAL COMO NRO COMPROBANTE', UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL, UN_FECHA_PAR => SYSDATE, UN_IND_MAYUS =>0),'NO') = 'SI' THEN MI_RS.NUMERO_FACTURA ELSE MI_RS.CODIGO_COBRO END, --MOD JM CC 3015
                                                                           UN_TIPO_AFEC         => UN_TIPOFACTURA,
                                                                           UN_FECHA             => UN_FECHA,
                                                                           UN_CUENTA            => UN_CUENTA,
                                                                           UN_TERCERO           => MI_RS.TERCERO,
                                                                           UN_SUCURSAL          => MI_RS.SUCURSAL,
                                                                           UN_DESCRIPCION       => MI_DESCRIPCION,
                                                                           UN_USUARIO           => UN_USUARIO,
                                                                           UN_VALORRECAUDO      => CASE WHEN MI_APLICAREC THEN UN_VALORRECAUDO ELSE 0 END,
                                                                           UN_TIPOCOBROFSIM     => CASE WHEN MI_APLICAREC OR UN_CUENTA IS NULL THEN MI_RS.TIPOCOBRO ELSE ' ' END,
                                                                           UN_NUMEROFACTURASIM  => CASE WHEN MI_APLICAREC THEN MI_FACRECAUDOF WHEN UN_CUENTA IS NULL THEN MI_RS.CODIGO_COBRO ELSE 0 END);

                    END IF;
                      MI_STRSQL := 'SELECT SC.CUENTADEBITOBASE AS CUENTA,PC.EQUIV_SIGEC, SOC.NROCONTRATOSIGEC, SOC.TIPOCONTRATOSIGEC
                        FROM SF_DETALLE_COBRO SDC
                        INNER JOIN SF_OBJETO_COBRO SOC
                        ON SOC.COMPANIA = SDC.COMPANIA
                        AND SOC.ANO = SDC.ANO
                        AND SOC.TIPOCOBRO = SDC.TIPOCOBRO
                        AND SOC.CODIGO_COBRO = SDC.CODIGO_COBRO
                        INNER JOIN SF_CONCEPTOS SC
                         ON SC.COMPANIA = SDC.COMPANIA
                        AND SC.ANO = SDC.ANO
                        AND SC.TIPOCOBRO = SDC.TIPOCOBRO
                        AND SC.CODIGO = SDC.CONCEPTO
                        INNER JOIN PLAN_CONTABLE PC
                         ON PC.COMPANIA = SC.COMPANIA
                        AND PC.ANO = SC.ANO 
                        AND PC.CODIGO = SC.CUENTADEBITOBASE
                        WHERE SOC.COMPANIA = '''||UN_COMPANIA||'''
                        AND SOC.ANO = '||UN_ANIO||'
                        AND SOC.TIPOFACTURA = '''||UN_TIPOFACTURA||'''
                        AND SOC.NRO_FACTURA = '''||UN_NUMEROFACTURA||'''';
    OPEN MI_RS_SIGEC FOR MI_STRSQL;
    LOOP
        FETCH MI_RS_SIGEC INTO MI_CUENTASIGEC,MI_EQUIV_SIGEC,MI_NROCONTRATOSIGEC,MI_TIPOCONTRATOSIGEC;
        EXIT WHEN MI_RS_SIGEC%NOTFOUND;
        BEGIN
                            --lvega SE AGREGAN CAMPOS DE EQUIVALENTE SIGEC, NUMERO Y TIPO CONTRATO SIGEC A LA DETALLE en PAGOS
                                MI_CAMPOS := 'EQUIV_SIGEC  =  '''|| MI_EQUIV_SIGEC ||'''
                                              ,NROCONTRATOSIGEC = '''|| MI_NROCONTRATOSIGEC ||'''
                                              ,TIPOCONTRATOSIGEC = '''|| MI_TIPOCONTRATOSIGEC ||'''';
                                              
                                MI_CONDICION := ' COMPANIA = '''|| UN_COMPANIA ||'''
                                              AND ANO      =  '|| PCK_SYSMAN_UTL.FC_ANIO(UN_FECHA) ||'
                                              AND TIPO_CPTE     = '''|| MI_CPTERECAUDO ||'''
                                              AND COMPROBANTE   = '|| MI_CONSECUTIVO ||' 
                                              AND CUENTA   = ''' || MI_CUENTASIGEC ||''' ';
                                              
                                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'DETALLE_COMPROBANTE_CNT',
                                                            UN_ACCION    => 'M',
                                                            UN_CAMPOS    => MI_CAMPOS,
                                                            UN_CONDICION => MI_CONDICION);
        END;
    END LOOP;
                        BEGIN
                            --lvega SE AGREGA TIPO PAGO (4) EN CAMPO TIPO_SIGEC EN RECAUDO
                             MI_CAMPOS := 'TIPO_SIGEC  =  4';
                                              
                                MI_CONDICION := ' COMPANIA = '''|| UN_COMPANIA ||'''
                                              AND ANO      =  '|| PCK_SYSMAN_UTL.FC_ANIO(UN_FECHA) ||'
                                              AND TIPO    = '''|| MI_CPTERECAUDO ||'''
                                              AND NUMERO   = '|| MI_CONSECUTIVO ||'';
                                              
                                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'COMPROBANTE_CNT',
                                                            UN_ACCION    => 'M',
                                                            UN_CAMPOS    => MI_CAMPOS,
                                                            UN_CONDICION => MI_CONDICION);
                                                            
                         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                           RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                         END;
                END IF;
            END IF;
        END IF;

        --Actualizaci? indicador de recaudo
        BEGIN
            BEGIN

                MI_CAMPOS    := 'INDPAGO   = -1';

                MI_CONDICION := ' COMPANIA     = '''||UN_COMPANIA||'''
                                  AND TIPOCOBRO    = '''||UN_TIPOFACTURA||'''
                                  AND CODIGO_COBRO = '||MI_RS.CODIGO_COBRO;

                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'SF_OBJETO_COBRO',
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

        --Actualizacion cpte pago en factura

        BEGIN
            BEGIN

                MI_CAMPOS    := 'ANOCPTE_PAGO   = '||PCK_SYSMAN_UTL.FC_ANIO(UN_FECHA)||',
                                 TIPOCPTE_PAGO  = '''||MI_CPTERECAUDO||''' ,
                                 NROCPTE_PAGO   = '||MI_CONSECUTIVO||',
                                 FECHA_PAGO     = '''||UN_FECHA||'''';

                MI_CONDICION := ' COMPANIA            ='''||UN_COMPANIA||'''
                                   AND TIPO_CPTE      ='''||UN_TIPOFACTURA||'''
                                   AND NUMERO_FACTURA = '||UN_NUMEROFACTURA||'';

                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'SF_FACTURA',
                                       UN_ACCION    => 'M',
                                       UN_CAMPOS    => MI_CAMPOS,
                                       UN_CONDICION => MI_CONDICION);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
            MI_MSGERROR(1).CLAVE := 'FACTURA';
            MI_MSGERROR(1).VALOR := UN_NUMEROFACTURA;
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                      UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTFACTURA
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
        END;

        
        --(EAMAYA:24/06/2020) Actualiza cuenta banco del detalle de la factura para cuando se recauda a una sola cuenta      
        IF MI_APLCUENTAREC IN (0) THEN
            FOR MI_RS IN (SELECT SF_CONCEPTOS.CODIGO, SF_CONCEPTOS.CUENTA_RECAUDO 
                            FROM SF_FACTURA  
                            INNER JOIN SF_TIPO_COBRO 
                            ON SF_FACTURA.COMPANIA      = SF_TIPO_COBRO.COMPANIA 
                            AND SF_FACTURA.ANO          = SF_TIPO_COBRO.ANO 
                            AND SF_FACTURA.TIPO_FACTURA = SF_TIPO_COBRO.CODIGO 
                            INNER JOIN SF_DETALLE_FACTURA 
                            ON SF_FACTURA.COMPANIA      = SF_DETALLE_FACTURA.COMPANIA 
                            AND SF_FACTURA.ANO          = SF_DETALLE_FACTURA.ANO 
                            AND SF_FACTURA.NUMERO_FACTURA = SF_DETALLE_FACTURA.FACTURA 
                            AND SF_TIPO_COBRO.CODIGO = SF_DETALLE_FACTURA.TIPOCOBRO 
                            LEFT JOIN SF_CONCEPTOS 
                            ON SF_DETALLE_FACTURA.COMPANIA      = SF_CONCEPTOS.COMPANIA 
                            AND SF_DETALLE_FACTURA.ANO = SF_CONCEPTOS.ANO 
                            AND SF_DETALLE_FACTURA.CONCEPTO = SF_CONCEPTOS.CODIGO 
                            AND SF_TIPO_COBRO.CODIGO = SF_CONCEPTOS.TIPOCOBRO 
                            WHERE SF_FACTURA.NUMERO_FACTURA LIKE UN_NUMEROFACTURA 
                            AND SF_TIPO_COBRO.CODIGO = UN_TIPOFACTURA)
            LOOP
              BEGIN
                  BEGIN

                      MI_CAMPOS    := 'CUENTA_BANCO   = '''||MI_RS.CUENTA_RECAUDO||''' ';

                      MI_CONDICION := ' COMPANIA          = '''||UN_COMPANIA||'''
                                        AND TIPO_FACTURA = '''||UN_TIPOFACTURA||'''
                                        AND FACTURA      = '||UN_NUMEROFACTURA||'
                                        AND TIPOCOBRO    = '''||UN_TIPOFACTURA||'''
                                                AND CONCEPTO    = '''||MI_RS.CODIGO||''' ';

                      MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'SF_DETALLE_FACTURA',
                                            UN_ACCION    => 'M',
                                            UN_CAMPOS    => MI_CAMPOS,
                                            UN_CONDICION => MI_CONDICION);

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                  END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
                  MI_MSGERROR(1).CLAVE := 'FACTURA';
                  MI_MSGERROR(1).VALOR := UN_NUMEROFACTURA;
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTFACTURA
                                            ,UN_REEMPLAZOS  => MI_MSGERROR);
              END;        
            END LOOP;
        END IF;
        --(EAMAYA:24/06/2020) Actualiza cuenta banco del detalle de la factura para cuando se recauda a una sola cuenta

        IF MI_APLICAREC THEN --(MZANGUNA:03/05/2019)-Elimina la factura simulada para el recaudo parcial.
            BEGIN
                BEGIN
                    MI_CONDICION := 'COMPANIA = '''|| UN_COMPANIA ||'''
                                 AND TIPOCOBRO = '''|| MI_RS.TIPOCOBRO ||'''
                                 AND ANO = '''|| UN_ANIO||'''
                                 AND CODIGO_COBRO = '|| MI_FACRECAUDOF ||' ';
                    MI_RTA:= PCK_DATOS.FC_ACME(UN_TABLA      =>  'SF_DETALLE_COBRO'
                                              ,UN_ACCION     =>  'E'
                                              ,UN_CONDICION  =>  MI_CONDICION);

                    MI_RTA:= PCK_DATOS.FC_ACME(UN_TABLA    => 'SF_OBJETO_COBRO'
                                            ,UN_ACCION     => 'E'
                                            ,UN_CONDICION  => MI_CONDICION);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                          UN_ERROR_COD   => PCK_ERRORES.ERR_DELETESIMULAFAC);
            END;
        END IF;
    END IF;
    END;

    END LOOP;
    RETURN MI_CONSECUTIVO;

END FC_RECAUDARFACTURA;



--10

PROCEDURE PR_ACT_VALOR_TOTAL_FACTURA
 /*
    NAME              : PR_ACTUALIZAR_VALOR_TOTAL
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : JONATHAN ENRIQUE GUERRERO TORRES
    DATE MIGRADOR     : 11/12/2017
    TIME              : 09:35 AM
    DESCRIPTION       : FUNCION ENCARGADA DE CALCULAR LA SUMATORIA DEL VALOR NETO Y ACUTALIZAR EL VALOR TOTAL DEL HEADER. 

    PARAMETROS        : UN_COMPANIA      => COMPANIA DE INGRESO A LA COMPANIA 
                        UN_TIPOCOBRO     => TIPO DE COBRO A FACTURAR
                        UN_CODIGO_COBRO  => CODIGO COBRO DEL PROCESO DE FACTURACION
                        UN_ANIO          => ANO DE INGRESO AL MODULO DE FACTURACION

    @NAME actualizarValorTotalFactura                    

  */

(
  UN_COMPANIA        PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOCOBRO       SF_FACTURA.TIPO_FACTURA%TYPE,  
  UN_NUMERO_FACTURA  SF_FACTURA.NUMERO_FACTURA%TYPE, 
  UN_ANIO            PCK_SUBTIPOS.TI_ANIO
)
AS 
 MI_VALORTOTAL           PCK_SUBTIPOS.TI_DOBLE;
 MI_TABLA                PCK_SUBTIPOS.TI_TABLA;
 MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
 MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
 MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR; 
 MI_RTA                  PCK_SUBTIPOS.TI_PARAMETRO;
 MI_CODIGOCOBRO          SF_FACTURA.CODIGO_COBRO%TYPE;

BEGIN
    SELECT SUM(NVL(VALOR_NETO,0)) VALOR_NETO 
      INTO MI_VALORTOTAL
      FROM SF_DETALLE_FACTURA
     WHERE COMPANIA       = UN_COMPANIA
       AND ANO            = UN_ANIO
       AND TIPO_FACTURA   = UN_TIPOCOBRO
       AND FACTURA        = UN_NUMERO_FACTURA;

      BEGIN 
          BEGIN 
              MI_TABLA     := 'SF_FACTURA';
              MI_CAMPOS    := 'VALOR_TOTAL       = '||MI_VALORTOTAL;
              MI_CONDICION := '   COMPANIA       = '''||UN_COMPANIA||'''
                              AND NUMERO_FACTURA = '||UN_NUMERO_FACTURA||'
                              AND ANO            = '||UN_ANIO||'
                              AND TIPO_FACTURA   = '''||UN_TIPOCOBRO||'''';

              PCK_DATOS.GL_RTA :=PCK_DATOS.FC_ACME(UN_ACCION    =>'M',
                                                   UN_TABLA     =>MI_TABLA,
                                                   UN_CAMPOS    =>MI_CAMPOS,
                                                   UN_CONDICION =>MI_CONDICION);                

         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
              MI_MSGERROR(1).CLAVE := 'CODIGO_COBRO';
              MI_MSGERROR(1).VALOR := UN_NUMERO_FACTURA;  
             RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_TABLAERROR => MI_TABLA, 
                                   UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTUA_VAL_TOT,
                                    UN_REEMPLAZOS => MI_MSGERROR);
    END;          


    --Se adiciona este bloque para asegurar que el objeto cobro este relacionado a la factura creada

     SELECT CODIGO_COBRO
       INTO MI_CODIGOCOBRO
     FROM SF_FACTURA
     WHERE COMPANIA = UN_COMPANIA
       AND NUMERO_FACTURA = UN_NUMERO_FACTURA
       AND ANO            = UN_ANIO
       AND TIPO_FACTURA   = UN_TIPOCOBRO;

     SELECT SUM(NVL(VALOR_NETO,0)) VALOR_NETO
       INTO MI_VALORTOTAL
       FROM SF_DETALLE_COBRO
      WHERE COMPANIA     = UN_COMPANIA
        AND ANO          = UN_ANIO
        AND TIPOCOBRO    = UN_TIPOCOBRO
        AND CODIGO_COBRO = MI_CODIGOCOBRO;

        BEGIN 
            BEGIN

                MI_CAMPOS := 'NRO_FACTURA    = '||UN_NUMERO_FACTURA||',
                              TIPOFACTURA    = '''||UN_TIPOCOBRO||''',
                              VALOR_TOTAL    = '||MI_VALORTOTAL||' ';

                MI_CONDICION := ' COMPANIA     = '''||UN_COMPANIA||'''
                              AND TIPOCOBRO    = '''||UN_TIPOCOBRO||'''
                              AND CODIGO_COBRO = '||MI_CODIGOCOBRO||'
                              AND ANO          = '||UN_ANIO;

                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'SF_OBJETO_COBRO',
                                            UN_ACCION    => 'M',
                                            UN_CAMPOS    => MI_CAMPOS,
                                            UN_CONDICION => MI_CONDICION);
             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
               RAISE PCK_EXCEPCIONES.EXC_FACTURACION;                         
             END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_TABLAERROR => MI_TABLA,
                                     UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTUA_NROFAC); 
        END;


END PR_ACT_VALOR_TOTAL_FACTURA;

--11

FUNCTION FC_GENERAINGRESO
/*
      NAME              : FC_GENERAINGRESO
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 21/09/2018
      TIME              : 12:10 AM
      SOURCE MODULE     :
      DESCRIPTION       : FUNCION QUE CREA LOS DETALLES DEL COMPROBANTE DE INGRESO
                          PARA LA FACTURA A RECAUDAR
      MODIFIER          : MARIA ALEJANDRA PEREZ SALAZAR
      DATE MODIFIED     : 08/03/2023
      TIME MODIFIED     : 02:00 PM
      MODIFICATIONS     : Se ajusta para validar los indicadores  de la cuenta contable 
            						  (maneja centro de costo, maneja fuente de recurso, maneja auxiliar
            						  general o maneja referencia) y guardar dichos campos segun sea el caso.
      MODIFIER          : MARIA ALEJANDRA PEREZ SALAZAR
      DATE MODIFIED     : 29/03/2023
      TIME              : 2:00 PM
      DESCRIPTION       : Se reversan los cambios para realizar el proceso desde 
                          otra opcion para que no se vea afectado el recaudo.
                          TICKET7729810

    @NAME:   generarIngreso
    @METHOD: GET
*/
(
    UN_COMPANIA                     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO                         IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPO_CPTE                    IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
    UN_COMPROBANTE                  IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
    UN_COMPROBANTE_AFEC             IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
    UN_TIPO_AFEC                    IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
    UN_FECHA                        IN DATE,
    UN_CUENTA                       IN DETALLE_COMPROBANTE_CNT.CUENTA%TYPE,
    UN_TERCERO                      IN PCK_SUBTIPOS.TI_TERCERO,
    UN_SUCURSAL                     IN PCK_SUBTIPOS.TI_SUCURSAL,
    UN_DESCRIPCION                  IN DETALLE_COMPROBANTE_CNT.DESCRIPCION%TYPE,
    UN_USUARIO                      IN PCK_SUBTIPOS.TI_USUARIO,
    UN_VALORRECAUDO                 IN PCK_SUBTIPOS.TI_DOBLE := 0,   --(MZANGUNA:15/04/2019)-Para pagos parciales desde wsRentasCaqueta
    UN_TIPOCOBROFSIM                IN SF_OBJETO_COBRO.TIPOCOBRO%TYPE := ' ', --(MZANGUNA:07/05/2019)-Tipo de cobro de la factura de recaudo simulada
    UN_NUMEROFACTURASIM             IN SF_OBJETO_COBRO.NRO_FACTURA%TYPE := 0 --(MZANGUNA:07/05/2019)-Número de factura de recaudo simulada
)
  RETURN PCK_SUBTIPOS.TI_ENTERO AS
  MI_RTA                            PCK_SUBTIPOS.TI_STRSQL;
  MI_VALOR                          PCK_SUBTIPOS.TI_DOBLE:=0;
  MI_RS                             SYS_REFCURSOR;
  MI_CONSECUTIVODET                 PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_CAMPOS                         PCK_SUBTIPOS.TI_CAMPOS;
  MI_PCKDATOS                       PCK_SUBTIPOS.TI_RTA_ACME;
  MI_CONDICION                      PCK_SUBTIPOS.TI_CONDICION;
  MI_VALORES                        PCK_SUBTIPOS.TI_VALORES;
  MI_NOCAUSAR                       SF_TIPO_COBRO.NOAPLICACAUSACION%TYPE;
  MI_CENTRO_COSTO                   PCK_SUBTIPOS.TI_CENTRO_COSTO;
  MI_FUENTERECURSO                  PCK_SUBTIPOS.TI_FUENTE_RECURSOS;
  MI_AUXILIAR                       PCK_SUBTIPOS.TI_AUXILIAR;
  MI_REFERENCIA                     PCK_SUBTIPOS.TI_REFERENCIA;
  MI_CLASECUENTA                    VARCHAR2(100 CHAR);
  MI_CUENTA                         DETALLE_COMPROBANTE_CNT.CUENTA%TYPE;
  MI_CLCUENTA                       VARCHAR2(100 CHAR);
  MI_APLICAREC                      SF_TIPO_COBRO.APLCUENTAREC%TYPE;
  MI_PARAMETRO                      VARCHAR2(100 CHAR);
  MI_EQUIVALENTEPPTAL               PLAN_CONTABLE.CUENTA_PPTAL%TYPE;
  MI_MSGERROR                       PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_STRSQL                         PCK_SUBTIPOS.TI_STRSQL;
  MI_CONSECUTIVOAFECT               DETALLE_COMPROBANTE_CNT.CONSECUTIVO%TYPE;
  MI_ANIOAFECT                      DETALLE_COMPROBANTE_CNT.ANO%TYPE;
  MI_APLICARECAUDOPARCIAL           BOOLEAN DEFAULT FALSE;
  MI_CUENTACAUSADA                  DETALLE_COMPROBANTE_PPTAL.CUENTA%TYPE;
  --CC_520(14/03/2025 JCROJAS)
  MI_MANEJARET                      VARCHAR2(100 CHAR);
  MI_VALORRET                       RETENCIONESVENTACNT.VALOR_RETENIDO%TYPE;
  MI_BASEG                          PCK_SUBTIPOS.TI_DOBLE:=0;
  MI_EXISTE                         NUMBER:=0;		
  MI_PARAGRUPAR                     VARCHAR2(100 CHAR); --MROSERO CC1019 
  MI_PARAMETRO_IVA                      VARCHAR2(100 CHAR);
  MI_VALOR_IVA                      DETALLE_COMPROBANTE_CNT.VALOR_IVA%TYPE;
  MI_PAR_REDONDEO                    VARCHAR2(100 CHAR);
BEGIN

    MI_CONSECUTIVODET := 1;
    MI_APLICARECAUDOPARCIAL := CASE WHEN UN_VALORRECAUDO <> 0 AND UN_VALORRECAUDO > 0 AND NVL(UN_TIPOCOBROFSIM, ' ') <> ' ' AND UN_NUMEROFACTURASIM <> 0  THEN TRUE ELSE FALSE END;    --(MZANGUNA:15/04/2019)

    MI_CLASECUENTA := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                              UN_NOMBRE    => 'SF CLASE CUENTA PARA EL RECAUDO',
                                              UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                              UN_FECHA_PAR => SYSDATE,
                                              UN_IND_MAYUS => 0
                                              ),'SIN CONFIGURAR');


    MI_PARAMETRO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                            UN_NOMBRE    => 'SF RECAUDAR FACTURAS DE VIGENCIAS ANTERIORES',
                                            UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                            UN_FECHA_PAR => SYSDATE,
                                            UN_IND_MAYUS => 0
                                            ),'NO');
	--INI CC_520(14/03/2025 JCROJAS)
    MI_MANEJARET := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                              UN_NOMBRE    => 'SF REGISTRAR RETENCIONES EN RECAUDO',
                                              UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                              UN_FECHA_PAR => SYSDATE),'NO'); 
    
    --MROSERO CC1019                                           
    MI_PARAGRUPAR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,  
                                             UN_NOMBRE    => 'AGRUPAR Y CALCULAR VALOR NETO POR CUENTA CONTABLE',
                                             UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                             UN_FECHA_PAR => SYSDATE,
                                             UN_IND_MAYUS =>0),'NO');
                                             
    MI_PAR_REDONDEO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                             UN_NOMBRE    => 'SF INCLUYE CONCEPTOS DE REDONDEO',
                                             UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                             UN_FECHA_PAR => SYSDATE,
                                             UN_IND_MAYUS =>0),'SI'); 
                                                       
    BEGIN
        SELECT NVL(SUM(VALOR_RETENIDO),0) VALOR 
        INTO MI_VALORRET
        FROM RETENCIONESVENTACNT
        WHERE COMPANIA = UN_COMPANIA
            AND ANIO = PCK_SYSMAN_UTL.FC_ANIO(UN_FECHA)
            AND TIPOCOMP = UN_TIPO_CPTE
            AND NUMEROCOMP = UN_COMPROBANTE;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_VALORRET := 0;
    END;        
    --FIN CC_520(14/03/2025 JCROJAS)		
    IF MI_CLASECUENTA =  'SIN CONFIGURAR' THEN
        BEGIN
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
            END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN

            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSFPARACLASECUENTA);
        END;
    ELSE
        MI_CLCUENTA := REPLACE(REPLACE(MI_CLASECUENTA,' ',''),',',''',''');
    END IF;

    <<procesar_cuentas_bancarias>>
    SELECT NOAPLICACAUSACION,APLCUENTAREC
      INTO MI_NOCAUSAR,MI_APLICAREC
    FROM SF_TIPO_COBRO
    WHERE COMPANIA = UN_COMPANIA
      AND ANO      = UN_ANIO
      AND CODIGO   = UN_TIPO_AFEC;

    IF MI_APLICAREC NOT IN (0) THEN

        MI_STRSQL := 'SELECT SUM(D.VALOR_NETO) VALOR,
                            D.AUXILIAR,
                            D.CUENTA_BANCO CUENTA,
                            C.CENTRO_COSTO,
                            D.FUENTE_RECURSO,
                            D.REFERENCIA
                       FROM SF_DETALLE_COBRO D
                         INNER JOIN SF_CONCEPTOS C
                            ON D.COMPANIA   = C.COMPANIA
                           AND D.CONCEPTO  = C.CODIGO
                           AND D.TIPOCOBRO = C.TIPOCOBRO
                           AND D.ANO       = C.ANO
                       LEFT JOIN PLAN_CONTABLE
                         ON D.COMPANIA      = PLAN_CONTABLE.COMPANIA
                        AND D.CUENTA_BANCO = PLAN_CONTABLE.CODIGO
                        AND D.ANO          = PLAN_CONTABLE.ANO
                      WHERE D.COMPANIA   = '''||UN_COMPANIA||'''
                        AND D.TIPOCOBRO   IN('''||UN_TIPO_AFEC||''')
                        AND D.CODIGO_COBRO = '||UN_COMPROBANTE_AFEC||'
                        AND PLAN_CONTABLE.CLASECUENTA IN('''||MI_CLCUENTA||''')
                        AND D.ANO =
                            CASE
                              WHEN '''||MI_PARAMETRO||''' = ''NO''
                              THEN '||UN_ANIO||'
                              ELSE D.ANO
                            END
                        AND C.SINSITUACIONFONDOS IN (0)
                       GROUP BY D.AUXILIAR,
                            D.REFERENCIA,
                            D.CUENTA_BANCO,
                            C.CENTRO_COSTO,
                            D.FUENTE_RECURSO,
                            D.REFERENCIA' ;

    ELSIF MI_PAR_REDONDEO = 'SI' THEN
--MROSERO CC1019 SE ANEXA LA CLASE CUENTA P DEPENDIENDO DEL PARAMETRO
--MROSERO CC1718 SE AGREGA UNION A LA CONSULTA, CON EL FIN DE SEPARAR EL VALOR DE LA CUENTA DE BANCO CON EL AJUSTE DE REDONDEO 888
--GROJAS CC2266 SE AGREGA VALIDACION POR PARAMETRO PARA DETERMINAR SI SE DEBE TENER EN CUENTA O NO EL CONCEPTO DE REDONDEO
        MI_STRSQL := 'SELECT SUM(VALOR),
                              CASE WHEN PLAN_CONTABLE.MAN_AUX_GEN= ''0''  AND PLAN_CONTABLE.OBLIGA_AUXILIAR= ''0''
                              THEN ''99999999999999999999''
                              ELSE VALORBANCO.AUXILIAR END AUXILIAR,
                              PLAN_CONTABLE.CODIGO CUENTA,
                              CASE WHEN PLAN_CONTABLE.MAN_CEN_CTO = ''0'' AND PLAN_CONTABLE.OBLIGA_CENTRO= ''0''
                              THEN ''99999999999999999999''
                              ELSE VALORBANCO.CENTRO_COSTO END CENTRO_COSTO,
                              CASE WHEN PLAN_CONTABLE.MAN_AUX_FUE = ''0'' AND PLAN_CONTABLE.OBLIGA_FUENTE= ''0''
                              THEN ''99999999999999999999''
                              ELSE VALORBANCO.FUENTE_RECURSO END FUENTE_RECURSO,
                              CASE WHEN PLAN_CONTABLE.MAN_AUX_REF = ''0''  AND PLAN_CONTABLE.OBLIGA_REFERENCIA= ''0''
                              THEN ''99999999999999999999''
                              ELSE VALORBANCO.REFERENCIA END REFERENCIA
                    FROM PLAN_CONTABLE
                    INNER JOIN(
                       		SELECT SUM(DETALLE_COMPROBANTE_CNT.VALOR_DEBITO - DETALLE_COMPROBANTE_CNT.VALOR_CREDITO) AS VALOR,
                                 DETALLE_COMPROBANTE_CNT.COMPANIA,DETALLE_COMPROBANTE_CNT.ANO,
                                      DETALLE_COMPROBANTE_CNT.AUXILIAR,
                                      '''||UN_CUENTA||''' CUENTA,
                                      DETALLE_COMPROBANTE_CNT.CENTRO_COSTO,
                                     DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO,
                                      DETALLE_COMPROBANTE_CNT.REFERENCIA
                              FROM DETALLE_COMPROBANTE_CNT
                                LEFT JOIN PLAN_CONTABLE
                                  ON DETALLE_COMPROBANTE_CNT.COMPANIA    = PLAN_CONTABLE.COMPANIA
                                 AND DETALLE_COMPROBANTE_CNT.ANO        = PLAN_CONTABLE.ANO
                                 AND DETALLE_COMPROBANTE_CNT.CUENTA     = PLAN_CONTABLE.CODIGO
                             WHERE DETALLE_COMPROBANTE_CNT.COMPANIA = '''||UN_COMPANIA||'''
                               AND DETALLE_COMPROBANTE_CNT.ANO        =
                                CASE
                                  WHEN '''||MI_PARAMETRO||''' = ''NO''
                                  THEN '||UN_ANIO||'
                                  ELSE DETALLE_COMPROBANTE_CNT.ANO
                                END
                               AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE    IN('''||UN_TIPO_AFEC||''')
                               AND DETALLE_COMPROBANTE_CNT.COMPROBANTE  = '||UN_COMPROBANTE_AFEC||'
                               AND ( ('''||MI_PARAGRUPAR||''' = ''SI'' AND CLASECUENTA IN (''C'', ''Y'', ''P''))
                                    OR
                                    ('''||MI_PARAGRUPAR||''' = ''NO'' AND CLASECUENTA IN (''C'', ''Y'')) )
                             GROUP BY DETALLE_COMPROBANTE_CNT.AUXILIAR,
                                    DETALLE_COMPROBANTE_CNT.CENTRO_COSTO,
                                    DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO,
                                    DETALLE_COMPROBANTE_CNT.REFERENCIA,DETALLE_COMPROBANTE_CNT.COMPANIA,DETALLE_COMPROBANTE_CNT.ANO) VALORBANCO
                       ON    PLAN_CONTABLE.COMPANIA=VALORBANCO.COMPANIA
                       AND   PLAN_CONTABLE.ANO=VALORBANCO.ANO
                       AND   PLAN_CONTABLE.CODIGO=VALORBANCO.CUENTA
                           GROUP BY PLAN_CONTABLE.CODIGO,
                            CASE WHEN PLAN_CONTABLE.MAN_AUX_GEN= ''0''  AND PLAN_CONTABLE.OBLIGA_AUXILIAR= ''0''
                              THEN ''99999999999999999999''
                              ELSE VALORBANCO.AUXILIAR END ,
                              CASE WHEN PLAN_CONTABLE.MAN_CEN_CTO = ''0'' AND PLAN_CONTABLE.OBLIGA_CENTRO= ''0''
                              THEN ''99999999999999999999''
                              ELSE VALORBANCO.CENTRO_COSTO END ,
                              CASE WHEN PLAN_CONTABLE.MAN_AUX_FUE = ''0'' AND PLAN_CONTABLE.OBLIGA_FUENTE= ''0''
                              THEN ''99999999999999999999''
                              ELSE VALORBANCO.FUENTE_RECURSO END ,
                              CASE WHEN PLAN_CONTABLE.MAN_AUX_REF = ''0''  AND PLAN_CONTABLE.OBLIGA_REFERENCIA= ''0''
                              THEN ''99999999999999999999''
                              ELSE VALORBANCO.REFERENCIA END   
                                           
                UNION ALL                
                
	                SELECT SUM(DETALLE_COMPROBANTE_CNT.VALOR_CREDITO) AS VALOR,
	                    CASE WHEN PLAN_CONTABLE.MAN_AUX_GEN = ''0'' AND PLAN_CONTABLE.OBLIGA_AUXILIAR = ''0''
	                        THEN ''99999999999999999999''
	                        ELSE DETALLE_COMPROBANTE_CNT.AUXILIAR 
	                    END AS AUXILIAR,
	                    PLAN_CONTABLE.CODIGO AS CUENTA,
	                    CASE WHEN PLAN_CONTABLE.MAN_CEN_CTO = ''0'' AND PLAN_CONTABLE.OBLIGA_CENTRO = ''0''
	                        THEN ''99999999999999999999''
	                        ELSE DETALLE_COMPROBANTE_CNT.CENTRO_COSTO 
	                    END AS CENTRO_COSTO,
	                    CASE WHEN PLAN_CONTABLE.MAN_AUX_FUE = ''0'' AND PLAN_CONTABLE.OBLIGA_FUENTE = ''0''
	                        THEN ''99999999999999999999''
	                        ELSE DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO 
	                    END AS FUENTE_RECURSO,
	                    CASE WHEN PLAN_CONTABLE.MAN_AUX_REF = ''0'' AND PLAN_CONTABLE.OBLIGA_REFERENCIA = ''0''
	                        THEN ''99999999999999999999''
	                        ELSE DETALLE_COMPROBANTE_CNT.REFERENCIA 
	                    END AS REFERENCIA
	                FROM DETALLE_COMPROBANTE_CNT
	                LEFT JOIN PLAN_CONTABLE ON 
	                    DETALLE_COMPROBANTE_CNT.COMPANIA = PLAN_CONTABLE.COMPANIA AND
	                    DETALLE_COMPROBANTE_CNT.ANO = PLAN_CONTABLE.ANO AND
	                    DETALLE_COMPROBANTE_CNT.CUENTA = PLAN_CONTABLE.CODIGO
	                WHERE 
	                    DETALLE_COMPROBANTE_CNT.COMPANIA = ''' || UN_COMPANIA || ''' AND
	                    DETALLE_COMPROBANTE_CNT.ANO = 
	                        CASE WHEN ''' || MI_PARAMETRO || ''' = ''NO'' THEN ' || UN_ANIO || '
	                            ELSE DETALLE_COMPROBANTE_CNT.ANO
	                        END AND
	                    DETALLE_COMPROBANTE_CNT.TIPO_CPTE IN (''' || UN_TIPO_AFEC || ''') AND
	                    DETALLE_COMPROBANTE_CNT.COMPROBANTE = ' || UN_COMPROBANTE_AFEC || ' AND
	                    (
	                        (''' || MI_PARAGRUPAR || ''' = ''NO'' AND CLASECUENTA IN (''C'', ''Y'', ''P'')) OR
	                        (''' || MI_PARAGRUPAR || ''' = ''SI'' AND CLASECUENTA IN (''C'', ''Y''))
	                    ) AND
	                    DETALLE_COMPROBANTE_CNT.VALOR_CREDITO > 0
	                GROUP BY 
	                    PLAN_CONTABLE.CODIGO,
	                    CASE WHEN PLAN_CONTABLE.MAN_AUX_GEN = ''0'' AND PLAN_CONTABLE.OBLIGA_AUXILIAR = ''0''
	                        THEN ''99999999999999999999''
	                        ELSE DETALLE_COMPROBANTE_CNT.AUXILIAR END,
	                    CASE WHEN PLAN_CONTABLE.MAN_CEN_CTO = ''0'' AND PLAN_CONTABLE.OBLIGA_CENTRO = ''0''
	                        THEN ''99999999999999999999''
	                        ELSE DETALLE_COMPROBANTE_CNT.CENTRO_COSTO END,
	                    CASE WHEN PLAN_CONTABLE.MAN_AUX_FUE = ''0'' AND PLAN_CONTABLE.OBLIGA_FUENTE = ''0''
	                        THEN ''99999999999999999999''
	                        ELSE DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO END,
	                    CASE WHEN PLAN_CONTABLE.MAN_AUX_REF = ''0'' AND PLAN_CONTABLE.OBLIGA_REFERENCIA = ''0''
	                        THEN ''99999999999999999999''
	                        ELSE DETALLE_COMPROBANTE_CNT.REFERENCIA END';
    ELSE
        MI_STRSQL := 'SELECT SUM(VALOR),
                              CASE WHEN PLAN_CONTABLE.MAN_AUX_GEN= ''0''  AND PLAN_CONTABLE.OBLIGA_AUXILIAR= ''0''
                              THEN ''99999999999999999999''
                              ELSE VALORBANCO.AUXILIAR END AUXILIAR,
                              PLAN_CONTABLE.CODIGO CUENTA,
                              CASE WHEN PLAN_CONTABLE.MAN_CEN_CTO = ''0'' AND PLAN_CONTABLE.OBLIGA_CENTRO= ''0''
                              THEN ''99999999999999999999''
                              ELSE VALORBANCO.CENTRO_COSTO END CENTRO_COSTO,
                              CASE WHEN PLAN_CONTABLE.MAN_AUX_FUE = ''0'' AND PLAN_CONTABLE.OBLIGA_FUENTE= ''0''
                              THEN ''99999999999999999999''
                              ELSE VALORBANCO.FUENTE_RECURSO END FUENTE_RECURSO,
                              CASE WHEN PLAN_CONTABLE.MAN_AUX_REF = ''0''  AND PLAN_CONTABLE.OBLIGA_REFERENCIA= ''0''
                              THEN ''99999999999999999999''
                              ELSE VALORBANCO.REFERENCIA END REFERENCIA
                    FROM PLAN_CONTABLE
                    INNER JOIN(
                                 SELECT SUM(DETALLE_COMPROBANTE_CNT.VALOR_DEBITO)VALOR,DETALLE_COMPROBANTE_CNT.COMPANIA,DETALLE_COMPROBANTE_CNT.ANO,
                                      DETALLE_COMPROBANTE_CNT.AUXILIAR,
                                      '''||UN_CUENTA||''' CUENTA,
                                      DETALLE_COMPROBANTE_CNT.CENTRO_COSTO,
                                     DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO,
                                      DETALLE_COMPROBANTE_CNT.REFERENCIA
                              FROM DETALLE_COMPROBANTE_CNT
                                LEFT JOIN PLAN_CONTABLE
                                  ON DETALLE_COMPROBANTE_CNT.COMPANIA    = PLAN_CONTABLE.COMPANIA
                                 AND DETALLE_COMPROBANTE_CNT.ANO        = PLAN_CONTABLE.ANO
                                 AND DETALLE_COMPROBANTE_CNT.CUENTA     = PLAN_CONTABLE.CODIGO
                             WHERE DETALLE_COMPROBANTE_CNT.COMPANIA = '''||UN_COMPANIA||'''
                               AND DETALLE_COMPROBANTE_CNT.ANO        =
                                CASE
                                  WHEN '''||MI_PARAMETRO||''' = ''NO''
                                  THEN '||UN_ANIO||'
                                  ELSE DETALLE_COMPROBANTE_CNT.ANO
                                END
                               AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE    IN('''||UN_TIPO_AFEC||''')
                               AND DETALLE_COMPROBANTE_CNT.COMPROBANTE  = '||UN_COMPROBANTE_AFEC||'
                               AND ( ('''||MI_PARAGRUPAR||''' = ''NO'' AND CLASECUENTA IN (''C'', ''Y'', ''P''))
                                    OR
                                    ('''||MI_PARAGRUPAR||''' = ''SI'' AND CLASECUENTA IN (''C'', ''Y'')) )
                             GROUP BY DETALLE_COMPROBANTE_CNT.AUXILIAR,
                                    DETALLE_COMPROBANTE_CNT.CENTRO_COSTO,
                                    DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO,
                                    DETALLE_COMPROBANTE_CNT.REFERENCIA,DETALLE_COMPROBANTE_CNT.COMPANIA,DETALLE_COMPROBANTE_CNT.ANO) VALORBANCO
                       ON    PLAN_CONTABLE.COMPANIA=VALORBANCO.COMPANIA
                       AND   PLAN_CONTABLE.ANO=VALORBANCO.ANO
                       AND   PLAN_CONTABLE.CODIGO=VALORBANCO.CUENTA
                           GROUP BY PLAN_CONTABLE.CODIGO,
                            CASE WHEN PLAN_CONTABLE.MAN_AUX_GEN= ''0''  AND PLAN_CONTABLE.OBLIGA_AUXILIAR= ''0''
                              THEN ''99999999999999999999''
                              ELSE VALORBANCO.AUXILIAR END ,
                              CASE WHEN PLAN_CONTABLE.MAN_CEN_CTO = ''0'' AND PLAN_CONTABLE.OBLIGA_CENTRO= ''0''
                              THEN ''99999999999999999999''
                              ELSE VALORBANCO.CENTRO_COSTO END ,
                              CASE WHEN PLAN_CONTABLE.MAN_AUX_FUE = ''0'' AND PLAN_CONTABLE.OBLIGA_FUENTE= ''0''
                              THEN ''99999999999999999999''
                              ELSE VALORBANCO.FUENTE_RECURSO END ,
                              CASE WHEN PLAN_CONTABLE.MAN_AUX_REF = ''0''  AND PLAN_CONTABLE.OBLIGA_REFERENCIA= ''0''
                              THEN ''99999999999999999999''
                              ELSE VALORBANCO.REFERENCIA END';
    END IF;

    IF MI_APLICARECAUDOPARCIAL OR UN_CUENTA IS NULL THEN --(MZANGUNA:07/05/2019)-Toma el valor distribuido de la factura de recuado.
        MI_STRSQL := '  SELECT CASE WHEN C.APLICADESCUENTO IN 0 THEN 
                              SUM(D.VALOR_BASE)
                          ELSE 
                              SUM(D.VALOR_BASE) - D.VALOR_DESCUENTO
                          END VALOR,
                          D.AUXILIAR,
                          C.CUENTA_RECAUDO CUENTA,
                          D.CENTRO_COSTO,
                          D.FUENTE_RECURSO,
                          D.REFERENCIA
                        FROM SF_DETALLE_COBRO D INNER JOIN SF_CONCEPTOS C
                         ON D.COMPANIA = C.COMPANIA
                        AND D.TIPOCOBRO = C.TIPOCOBRO
                        AND D.ANO = C.ANO
                        AND D.CONCEPTO = C.CODIGO
                        WHERE D.COMPANIA = '''|| UN_COMPANIA ||'''
                          AND D.TIPOCOBRO = '''|| UN_TIPOCOBROFSIM || '''
                          AND D.ANO = '|| UN_ANIO || '
                          AND D.CODIGO_COBRO = '|| UN_NUMEROFACTURASIM ||'
                        GROUP BY C.APLICADESCUENTO ,D.VALOR_DESCUENTO, D.AUXILIAR, C.CUENTA_RECAUDO, D.CENTRO_COSTO, D.FUENTE_RECURSO, D.REFERENCIA ';
    END IF;

    OPEN MI_RS FOR MI_STRSQL;
    LOOP
        FETCH MI_RS INTO MI_VALOR,MI_AUXILIAR,MI_CUENTA,MI_CENTRO_COSTO,MI_FUENTERECURSO,MI_REFERENCIA;
        EXIT WHEN MI_RS%NOTFOUND;

        BEGIN
            BEGIN
                MI_CAMPOS := 'COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CONSECUTIVO,CUENTA,
                            FECHA,NATURALEZA,DESCRIPCION,VALOR_DEBITO,VALOR_CREDITO,
                            EJECUCION_DEBITO,BASE_GRAVABLE,CENTRO_COSTO,TERCERO,SUCURSAL,
                            AUXILIAR,FUENTE_RECURSO,REFERENCIA,CREATED_BY,DATE_CREATED';
				/*CC_520(14/03/2025 JCROJAS): Se agrega resta del campo VALOR_RETENIDO de la tabla RETENCIONESVENTACNT en los campos VALOR_DEBITO y EJECUCION_DEBITO                           
                cuando el parametro SF REGISTRAR RETENCIONES EN RECAUDO este en SI*/																		
                MI_VALORES := '''' || UN_COMPANIA || '''
                                  ,' || PCK_SYSMAN_UTL.FC_ANIO(UN_FECHA) || '
                                  ,''' || UN_TIPO_CPTE || '''
                                  ,' || UN_COMPROBANTE || '
                                  ,' || MI_CONSECUTIVODET || '
                                  ,''' || MI_CUENTA || '''
                                  ,'''||UN_FECHA||'''
                                  ,''C''
                                  ,''' || UN_DESCRIPCION || '''
                                  ,' || CASE WHEN MI_MANEJARET = 'SI' THEN (MI_VALOR-MI_VALORRET) ELSE MI_VALOR END || '
                                  ,0
                                  ,' || CASE WHEN MI_MANEJARET = 'SI' THEN (MI_VALOR-MI_VALORRET) ELSE MI_VALOR END || '
                                  ,0
                                  ,''' || MI_CENTRO_COSTO || '''
                                  ,''' || UN_TERCERO || '''
                                  ,''' || UN_SUCURSAL || '''
                                  ,''' || MI_AUXILIAR || '''
                                  ,''' || MI_FUENTERECURSO || '''
                                  ,''' || MI_REFERENCIA || '''
                                  ,''' || UN_USUARIO || '''
                                  , SYSDATE';

                MI_PCKDATOS := PCK_DATOS.FC_ACME(
                                      UN_TABLA => 'DETALLE_COMPROBANTE_CNT',
                                      UN_ACCION => 'I',
                                      UN_CAMPOS => MI_CAMPOS,
                                      UN_VALORES => MI_VALORES
                                    );

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
            MI_MSGERROR(1).CLAVE := 'NUMERO';
            MI_MSGERROR(1).VALOR := UN_COMPROBANTE;
            MI_MSGERROR(2).CLAVE := 'TIPO';
            MI_MSGERROR(2).VALOR := UN_TIPO_CPTE;
            PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD => SQLCODE,
                UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_INSDETACOMPCNT
                );
        END;
        MI_CONSECUTIVODET := MI_CONSECUTIVODET + 1;

    END LOOP procesar_cuentas_bancarias;
    <<procesar_cuentas_credito>>
--MROSERO CC1019 SE ANEXA LA CLASE CUENTA P DEPENDIENDO DEL PARAMETRO 
    IF MI_PAR_REDONDEO = 'SI' THEN
    MI_STRSQL := 'SELECT SUM(DETALLE_COMPROBANTE_CNT.VALOR_DEBITO)- SUM(NVL(DETALLE_COMPROBANTE_CNT.DEBITO_AFECTADO,0)) VALOR,
                          DETALLE_COMPROBANTE_CNT.VALOR_IVA,
                          DETALLE_COMPROBANTE_CNT.ANO,
						  DETALLE_COMPROBANTE_CNT.AUXILIAR , 
                          DETALLE_COMPROBANTE_CNT.CUENTA,
                          DETALLE_COMPROBANTE_CNT.CENTRO_COSTO ,
                          DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO,
                          DETALLE_COMPROBANTE_CNT.REFERENCIA ,
                          DETALLE_COMPROBANTE_CNT.CONSECUTIVO,
                          DETALLE_COMPROBANTE_CNT.CUENTAPPTAL
                  FROM DETALLE_COMPROBANTE_CNT
					 LEFT JOIN PLAN_CONTABLE
                      ON DETALLE_COMPROBANTE_CNT.COMPANIA  = PLAN_CONTABLE.COMPANIA
                      AND DETALLE_COMPROBANTE_CNT.ANO      = PLAN_CONTABLE.ANO
                      AND DETALLE_COMPROBANTE_CNT.CUENTA   = PLAN_CONTABLE.CODIGO
                  WHERE DETALLE_COMPROBANTE_CNT.COMPANIA  = '''||UN_COMPANIA||'''
                    AND DETALLE_COMPROBANTE_CNT.ANO       =
                    CASE
                      WHEN '''||MI_PARAMETRO||''' = ''NO''
                      THEN '||UN_ANIO||'
                      ELSE DETALLE_COMPROBANTE_CNT.ANO
                    END
                    AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE   IN('''||UN_TIPO_AFEC||''')
                    AND DETALLE_COMPROBANTE_CNT.COMPROBANTE = '||UN_COMPROBANTE_AFEC||'
                    AND ( ('''||MI_PARAGRUPAR||''' = ''SI'' AND CLASECUENTA IN (''C'', ''Y'', ''P''))
                                    OR
                        ('''||MI_PARAGRUPAR||''' = ''NO'' AND CLASECUENTA IN (''C'', ''Y'')) )
                  GROUP BY DETALLE_COMPROBANTE_CNT.ANO,
                           DETALLE_COMPROBANTE_CNT.AUXILIAR ,
                           DETALLE_COMPROBANTE_CNT.REFERENCIA ,
                           DETALLE_COMPROBANTE_CNT.CUENTA,
                           DETALLE_COMPROBANTE_CNT.CENTRO_COSTO ,
                           DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO ,
                           DETALLE_COMPROBANTE_CNT.CONSECUTIVO,
                           DETALLE_COMPROBANTE_CNT.CUENTAPPTAL,
                           DETALLE_COMPROBANTE_CNT.VALOR_IVA';
    ELSE 
        MI_STRSQL := 'SELECT SUM(DETALLE_COMPROBANTE_CNT.VALOR_DEBITO)- SUM(NVL(DETALLE_COMPROBANTE_CNT.DEBITO_AFECTADO,0)) VALOR,
                          DETALLE_COMPROBANTE_CNT.VALOR_IVA,
                          DETALLE_COMPROBANTE_CNT.ANO,
						  DETALLE_COMPROBANTE_CNT.AUXILIAR , 
                          DETALLE_COMPROBANTE_CNT.CUENTA,
                          DETALLE_COMPROBANTE_CNT.CENTRO_COSTO ,
                          DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO,
                          DETALLE_COMPROBANTE_CNT.REFERENCIA ,
                          DETALLE_COMPROBANTE_CNT.CONSECUTIVO,
                          DETALLE_COMPROBANTE_CNT.CUENTAPPTAL
                  FROM DETALLE_COMPROBANTE_CNT
					 LEFT JOIN PLAN_CONTABLE
                      ON DETALLE_COMPROBANTE_CNT.COMPANIA  = PLAN_CONTABLE.COMPANIA
                      AND DETALLE_COMPROBANTE_CNT.ANO      = PLAN_CONTABLE.ANO
                      AND DETALLE_COMPROBANTE_CNT.CUENTA   = PLAN_CONTABLE.CODIGO
                  WHERE DETALLE_COMPROBANTE_CNT.COMPANIA  = '''||UN_COMPANIA||'''
                    AND DETALLE_COMPROBANTE_CNT.ANO       =
                    CASE
                      WHEN '''||MI_PARAMETRO||''' = ''NO''
                      THEN '||UN_ANIO||'
                      ELSE DETALLE_COMPROBANTE_CNT.ANO
                    END
                    AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE   IN('''||UN_TIPO_AFEC||''')
                    AND DETALLE_COMPROBANTE_CNT.COMPROBANTE = '||UN_COMPROBANTE_AFEC||'
                    AND ( ('''||MI_PARAGRUPAR||''' = ''NO'' AND CLASECUENTA IN (''C'', ''Y'', ''P''))
                                    OR
                        ('''||MI_PARAGRUPAR||''' = ''SI'' AND CLASECUENTA IN (''C'', ''Y'')) )
                  GROUP BY DETALLE_COMPROBANTE_CNT.ANO,
                           DETALLE_COMPROBANTE_CNT.AUXILIAR ,
                           DETALLE_COMPROBANTE_CNT.REFERENCIA ,
                           DETALLE_COMPROBANTE_CNT.CUENTA,
                           DETALLE_COMPROBANTE_CNT.CENTRO_COSTO ,
                           DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO ,
                           DETALLE_COMPROBANTE_CNT.CONSECUTIVO,
                           DETALLE_COMPROBANTE_CNT.CUENTAPPTAL,
                           DETALLE_COMPROBANTE_CNT.VALOR_IVA
                           HAVING (SUM(DETALLE_COMPROBANTE_CNT.VALOR_DEBITO) - SUM(NVL(DETALLE_COMPROBANTE_CNT.DEBITO_AFECTADO,0))) > 0';
    END IF;
    IF MI_APLICARECAUDOPARCIAL OR UN_CUENTA IS NULL THEN --(MZANGUNA:07/05/2019)
        MI_STRSQL := 'SELECT CASE WHEN C.APLICADESCUENTO IN 0 THEN 
                            SUM(D.VALOR_BASE)
                        ELSE 
                            SUM(D.VALOR_BASE) - D.VALOR_DESCUENTO
                        END VALOR,
                        D.VALOR_IVA,
                      '|| UN_ANIO ||' ANO,
                      D.AUXILIAR,
                      C.CUENTADEBITOBASE CUENTA,
                      D.CENTRO_COSTO,
                      D.FUENTE_RECURSO,
                      D.REFERENCIA,
                        NULL CONSECUTIVO,
                        C.CUENTACREDITOBASE CUENTAPPTAL
                    FROM SF_DETALLE_COBRO D INNER JOIN SF_CONCEPTOS C
                     ON D.COMPANIA = C.COMPANIA
                    AND D.TIPOCOBRO = C.TIPOCOBRO
                    AND D.ANO = C.ANO
                    AND D.CONCEPTO = C.CODIGO
                    WHERE D.COMPANIA = '''|| UN_COMPANIA ||'''
                      AND D.TIPOCOBRO = '''|| UN_TIPOCOBROFSIM || '''
                      AND D.ANO = '|| UN_ANIO || '
                      AND D.CODIGO_COBRO = '|| UN_NUMEROFACTURASIM ||'
                    GROUP BY C.APLICADESCUENTO ,D.VALOR_DESCUENTO,D.ANO, D.AUXILIAR, C.CUENTADEBITOBASE , D.CENTRO_COSTO, D.FUENTE_RECURSO, D.REFERENCIA, C.CUENTACREDITOBASE, D.VALOR_IVA';
    END IF;
    OPEN MI_RS FOR MI_STRSQL;
    LOOP
        FETCH MI_RS INTO MI_VALOR,MI_VALOR_IVA,MI_ANIOAFECT, MI_AUXILIAR,MI_CUENTA,MI_CENTRO_COSTO,MI_FUENTERECURSO,MI_REFERENCIA,MI_CONSECUTIVOAFECT,MI_CUENTACAUSADA;
        EXIT WHEN MI_RS%NOTFOUND;

        BEGIN
            BEGIN
                BEGIN
                /*TICKET 7716154 MPEREZ*/
                /*Se modifica para obtener la Equivalencia Presupuestal del plan contable y no del comprobante contable*/
                --MOD por JM 02/01/2025 CC 5839
                  SELECT MAX(RUBRO) RUBRO
                    INTO MI_EQUIVALENTEPPTAL
                    FROM PLAN_PPTAL_CUENTACNT
                    WHERE COMPANIA = UN_COMPANIA
                      AND ANO = PCK_SYSMAN_UTL.FC_ANIO(UN_FECHA)
                      AND CUENTA_CONTABLE = MI_CUENTA;
                  EXCEPTION WHEN NO_DATA_FOUND THEN
                      MI_EQUIVALENTEPPTAL := '';
                  END; 
				IF (MI_PARAGRUPAR = 'SI' AND MI_VALOR > 0) OR (MI_PARAGRUPAR = 'NO') THEN
                --LVEGA 03/2025
                    MI_PARAMETRO_IVA := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                            UN_NOMBRE    => 'MANEJA FACTURAS CON IVA',
                                            UN_MODULO    => '69',
                                            UN_FECHA_PAR => SYSDATE,
                                            UN_IND_MAYUS => -1
                                            ),'NO');
                                            
                MI_CAMPOS := 'COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CONSECUTIVO,CUENTA,
                                FECHA,NATURALEZA,DESCRIPCION,VALOR_DEBITO,VALOR_CREDITO,
                                EJECUCION_DEBITO,BASE_GRAVABLE,CENTRO_COSTO,TERCERO,SUCURSAL,
                                AUXILIAR,FUENTE_RECURSO,REFERENCIA,CUENTAPPTAL,TIPO_CPTE_AFECT,
                                CMPTE_AFECTADO,ANO_AFECT,CONSECUTIVOAFECTADO,EQUIV_SIGEC,VALOR_IVA,CREATED_BY,DATE_CREATED';
             
                MI_VALORES := '''' || UN_COMPANIA || '''
                                  ,' || PCK_SYSMAN_UTL.FC_ANIO(UN_FECHA) || '
                                  ,''' || UN_TIPO_CPTE || '''
                                  ,' || UN_COMPROBANTE || '
                                  ,' || MI_CONSECUTIVODET || '
                                  ,''' || MI_CUENTA || '''
                                  ,'''||UN_FECHA||'''
                                  ,''D''
                                  ,''' || UN_DESCRIPCION || '''
                                  ,0
                                  ,' || MI_VALOR || '
                                  ,0
                                  ,' || MI_VALOR || '
                                  ,''' || MI_CENTRO_COSTO || '''
                                  ,''' || UN_TERCERO || '''
                                  ,''' || UN_SUCURSAL || '''
                                  ,''' || MI_AUXILIAR || '''
                                  ,''' || MI_FUENTERECURSO || '''
                                  ,''' || MI_REFERENCIA || '''
                                  ,'''||MI_EQUIVALENTEPPTAL||'''
                                  ,'''||UN_TIPO_AFEC||'''
                                  ,'||UN_COMPROBANTE_AFEC||'
                                  ,'||MI_ANIOAFECT||'
                                  ,'|| CASE WHEN MI_APLICARECAUDOPARCIAL OR UN_CUENTA IS NULL THEN 1 ELSE MI_CONSECUTIVOAFECT END ||'
                                  ,'''||MI_EQUIVALENTEPPTAL||'''
                                  ,'|| CASE WHEN MI_PARAMETRO_IVA = 'SI' THEN MI_VALOR_IVA ELSE 0 END ||'
                                  ,''' || UN_USUARIO || '''
                                  , SYSDATE';

                MI_PCKDATOS := PCK_DATOS.FC_ACME(
                                      UN_TABLA => 'DETALLE_COMPROBANTE_CNT',
                                      UN_ACCION => 'I',
                                      UN_CAMPOS => MI_CAMPOS,
                                      UN_VALORES => MI_VALORES
                                    );
END IF;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
            END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
            MI_MSGERROR(1).CLAVE := 'NUMERO';
            MI_MSGERROR(1).VALOR := UN_COMPROBANTE;
            MI_MSGERROR(2).CLAVE := 'TIPO';
            MI_MSGERROR(2).VALOR := UN_TIPO_CPTE;
            PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD => SQLCODE,
                UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_INSDETACOMPCNT);
        END;
        MI_CONSECUTIVODET := MI_CONSECUTIVODET + 1;
    END LOOP procesar_cuentas_credito;

    --INI CC_520(14/03/2025 JCROJAS): Inserta las cuentas de la tabla RETENCIONESVENTACNT
    IF MI_MANEJARET = 'SI' THEN
        <<procesar_cuentas_ret>>
        BEGIN
            SELECT COUNT(0) 
            INTO MI_EXISTE
            FROM COMPROBANTE_CNT 
            WHERE COMPANIA = UN_COMPANIA
                AND ANO = PCK_SYSMAN_UTL.FC_ANIO(UN_FECHA)
                AND TIPO = UN_TIPO_CPTE
                AND NUMERO = UN_COMPROBANTE;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_EXISTE := 0;
        END; 
        IF MI_EXISTE > 0 THEN 
            MI_STRSQL := 'SELECT CONSECUTIVO,ANIO,CUENTA,
                            SUM(VALOR_RETENIDO) VALOR_RETENIDO,
                            SUM(BASEGRAVABLE) BASEGRAVABLE
                          FROM RETENCIONESVENTACNT
                          WHERE COMPANIA = '''||UN_COMPANIA||'''
                            AND ANIO = '||PCK_SYSMAN_UTL.FC_ANIO(UN_FECHA)||'
                            AND TIPOCOMP = '''||UN_TIPO_CPTE||'''
                            AND NUMEROCOMP = '||UN_COMPROBANTE||'
                          GROUP BY CONSECUTIVO,ANIO,CUENTA';
            OPEN MI_RS FOR MI_STRSQL;
            LOOP
                FETCH MI_RS INTO MI_CONSECUTIVOAFECT,MI_ANIOAFECT,MI_CUENTA,MI_VALOR,MI_BASEG;
                EXIT WHEN MI_RS%NOTFOUND;
                BEGIN
                    BEGIN
                        MI_CAMPOS := 'COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CONSECUTIVO,CUENTA,
                                      FECHA,NATURALEZA,DESCRIPCION,VALOR_DEBITO,VALOR_CREDITO,
                                      EJECUCION_DEBITO,BASE_GRAVABLE,TERCERO,SUCURSAL,CREATED_BY,
                                      DATE_CREATED';
                        MI_VALORES := '''' || UN_COMPANIA || '''
                                      ,' || PCK_SYSMAN_UTL.FC_ANIO(UN_FECHA) || '
                                      ,''' || UN_TIPO_CPTE || '''
                                      ,' || UN_COMPROBANTE || '
                                      ,' || MI_CONSECUTIVODET || '
                                      ,''' || MI_CUENTA || '''
                                      ,'''||UN_FECHA||'''
                                      ,''C''
                                      ,''' || UN_DESCRIPCION || '''
                                      ,' || MI_VALOR ||'
                                      ,0
                                      ,' || MI_VALOR || '
                                      ,' || MI_BASEG || '
                                      ,''' || UN_TERCERO || '''
                                      ,''' || UN_SUCURSAL || '''
                                      ,''' || UN_USUARIO || '''
                                      , SYSDATE';
                        
                        MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA => 'DETALLE_COMPROBANTE_CNT',
                                                         UN_ACCION => 'I',
                                                         UN_CAMPOS => MI_CAMPOS,
                                                         UN_VALORES => MI_VALORES);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                    END;   
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
                    MI_MSGERROR(1).CLAVE := 'NUMERO';
                    MI_MSGERROR(1).VALOR := UN_COMPROBANTE;
                    MI_MSGERROR(2).CLAVE := 'TIPO';
                    MI_MSGERROR(2).VALOR := UN_TIPO_CPTE;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                                               UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_INSDETACOMPCNT);
                END;    
                MI_CONSECUTIVODET := MI_CONSECUTIVODET + 1;
            END LOOP procesar_cuentas_ret;
        END IF;
    END IF;
    --FIN CC_520(14/03/2025 JCROJAS)
	
    IF(UN_CUENTA IS NULL) THEN
        PCK_FACT_GENERAL_COM3.PR_NEWCOMPRPPTALARQC(UN_COMPANIA        => UN_COMPANIA,
                                                    UN_TIPOCPTE        => UN_TIPO_CPTE,
                                                    UN_ANIO            => PCK_SYSMAN_UTL.FC_ANIO(UN_FECHA),
                                                    UN_NUMERO          => UN_COMPROBANTE,
                                                    UN_NUMERO_AFEC     => UN_COMPROBANTE_AFEC,
                                                    UN_TIPO_PTE_AFEC   => UN_TIPO_AFEC,
                                                    UN_DESCRIPCION     => UN_DESCRIPCION,
                                                    UN_USUARIO         => UN_USUARIO,
                                                    UN_VALORRECAUDO    => CASE WHEN MI_APLICARECAUDOPARCIAL THEN UN_VALORRECAUDO ELSE 0 END);
    ELSE
        PCK_FACT_GENERAL_COM3.PR_CREARCOMPROBANTEPPTAL(UN_COMPANIA        => UN_COMPANIA,
                                                    UN_TIPOCPTE        => UN_TIPO_CPTE,
                                                    UN_ANIO            => PCK_SYSMAN_UTL.FC_ANIO(UN_FECHA),
                                                    UN_NUMERO          => UN_COMPROBANTE,
                                                    UN_NUMERO_AFEC     => UN_COMPROBANTE_AFEC,
                                                    UN_TIPO_PTE_AFEC   => UN_TIPO_AFEC,
                                                    UN_DESCRIPCION     => UN_DESCRIPCION,
                                                    UN_USUARIO         => UN_USUARIO,
                                                    UN_VALORRECAUDO    => CASE WHEN MI_APLICARECAUDOPARCIAL THEN UN_VALORRECAUDO ELSE 0 END);
   
    END IF;
    RETURN -1;

END FC_GENERAINGRESO;



--12

PROCEDURE PR_CREARCOMPROBANTEPPTAL
  /*
  NAME              : PR_CREARCOMPROBANTEPPTAL En Access --> CrearcomprobantePptal
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
  DATE MIGRADOR     : 21/08/2018
  TIME              : 14:22 PM
  MODIFIER          : MARIA ALEJANDRA PEREZ SALAZAR
  DATE MODIFIED     : 08/03/2023
  TIME              : 2:00 PM
  DESCRIPTION       : Se ajusta para generar el comprobante presupuestal
                      con los auxiliares de acuerdo a los indicadores del plan pptal
                      ademas de guardar la informaciÃ³n cuipo.TICKET7716154
  MODIFIER          : MARIA ALEJANDRA PEREZ SALAZAR
  DATE MODIFIED     : 29/03/2023
  TIME              : 2:00 PM
  DESCRIPTION       : Se reversan los cambios para realizar el proceso desde 
                      otra opcion para que no se vea afectado el recaudo.
                      TICKET7729810


  @NAME:  crearComprobantePptal
  @METHOD:  POST
  */
(
  UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOCPTE        IN TIPO_COMPROBPP.CODIGO%TYPE,
  UN_ANIO            IN PCK_SUBTIPOS.TI_ENTERO,
  UN_NUMERO          IN COMPROBANTE_PPTAL.NUMERO%TYPE,
  UN_NUMERO_AFEC     IN COMPROBANTE_PPTAL.NUMERO%TYPE,
  UN_TIPO_PTE_AFEC   IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
  UN_DESCRIPCION     IN COMPROBANTE_PPTAL.DESCRIPCION%TYPE,
  UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO,
  UN_VALORRECAUDO    IN PCK_SUBTIPOS.TI_DOBLE := 0   --(MZANGUNA:16/04/2019)-Para pagos parciales desde wsRentasCaqueta

)AS
  MI_STRSQL           PCK_SUBTIPOS.TI_STRSQL;
  MI_CONTEO           PCK_SUBTIPOS.TI_ENTERO;
  MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
  MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
  MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_MANEQUIV4        PCK_SUBTIPOS.TI_STRSQL;
  MI_CONSECUTIVO      PCK_SUBTIPOS.TI_ENTERO;
  MI_RS               SYS_REFCURSOR;
  MI_CUENTA           DETALLE_COMPROBANTE_CNT.CUENTAPPTAL%TYPE;
  MI_VALOR            PCK_SUBTIPOS.TI_DOBLE;
  MI_CENTRO_COSTO     PCK_SUBTIPOS.TI_CENTRO_COSTO;
  MI_TERCERO          PCK_SUBTIPOS.TI_TERCERO;
  MI_SUCURSAL         PCK_SUBTIPOS.TI_SUCURSAL;
  MI_AUXILIAR         PCK_SUBTIPOS.TI_AUXILIAR;
  MI_REFERENCIA       PCK_SUBTIPOS.TI_REFERENCIA;
  MI_NATURALEZA       PCK_SUBTIPOS.TI_NATURALEZACONTA;
  MI_FUENTERECURSO    PCK_SUBTIPOS.TI_FUENTE_RECURSOS;
  MI_FECHA            DATE;
  MI_APLICARECAUDO    BOOLEAN DEFAULT FALSE;

  /*INI TICKET 7716154 MPEREZ*/
  MI_CODIGOCCPET     DETALLE_COMPROBANTE_PPTAL.CODIGO_CCPET%TYPE;
  MI_FUENTECUIPO     DETALLE_COMPROBANTE_PPTAL.FUENTE_CUIPO%TYPE;
  MI_UNIDADEJE       DETALLE_COMPROBANTE_PPTAL.CODIGOUNIDADEJE%TYPE;
  /*FIN TICKET 7716154 MPEREZ*/
  /*INI TICKET 7732652  JMILLAN (considerar las cuentas tipo clase Y)*/
  MI_PARAMETRO_Y                    PCK_SUBTIPOS.TI_STRSQL;
  /*FIN TICKET 7732652  JMILLAN */
  MI_AFECTAPPTALWS    PCK_SUBTIPOS.TI_STRSQL;
  MI_YA_EXISTE NUMBER DEFAULT 0; --JM 7749239
  MI_LOTE         NUMBER DEFAULT 0;
  MI_DESCRIPCIONCNT  COMPROBANTE_PPTAL.DESCRIPCION%TYPE;
  MI_VALOR_IVA        PCK_SUBTIPOS.TI_DOBLE;
  MI_TIPO_COMPROBANTE       VARCHAR2(2);
  MI_PARAMETRO        PCK_SUBTIPOS.TI_PARAMETRO;
  MI_PARAGRUPAR       PCK_SUBTIPOS.TI_PARAMETRO;     --MROSERO CC1731

BEGIN
    MI_CONSECUTIVO := 1;
    MI_MANEQUIV4   := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                          UN_NOMBRE    => 'SF MANEJAR EQUIVALENCIA PRESUPUESTAL EN LA CUENTA 4',
                                          UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                          UN_FECHA_PAR => SYSDATE,
                                          UN_IND_MAYUS => 0
                                                ),'NO');

  /*Parametro que indica si se debe realizar afectaci¿¿n presupuestal con descuentos en recaudo desde el WS
     Ticket7720232 - MPEREZ*/
    MI_AFECTAPPTALWS   := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                          UN_NOMBRE    => 'GENERA AFECTACION PRESUPUESTAL EN LAS CUENTAS 4 Y 13 DE RECAUDO (WEB SERVICE)',
                                          UN_MODULO    => PCK_DATOS.FC_MODULOPRESUPUESTO,
                                          UN_FECHA_PAR => SYSDATE,
                                          UN_IND_MAYUS => 0
                                                ),'NO');

    MI_PARAMETRO_Y := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                            UN_NOMBRE    => 'SF PERMITA CONFIGURAR PASIVOS EN RECAUDO',
                                            UN_MODULO    => PCK_DATOS.FC_MODULOCONTABILIDAD,
                                            UN_FECHA_PAR => SYSDATE,
                                            UN_IND_MAYUS => 0
                                            ),'NO');
                                            
    --MROSERO CC1731
    MI_PARAGRUPAR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                             UN_NOMBRE    => 'AGRUPAR Y CALCULAR VALOR NETO POR CUENTA CONTABLE',
                                             UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                             UN_FECHA_PAR => SYSDATE,
                                             UN_IND_MAYUS =>0),'NO'); 
                                                                                     
    MI_APLICARECAUDO := CASE WHEN UN_VALORRECAUDO <> 0 AND UN_VALORRECAUDO > 0 THEN TRUE ELSE FALSE END;    --(MZANGUNA:16/04/2019)
    BEGIN
        BEGIN
            --Creacion comprobante pptal
            MI_CAMPOS := 'COMPANIA ,
                            ANO ,
                            TIPO ,
                            NUMERO ,
                            FECHA ,
                            TERCERO,
                            SUCURSAL,
                            DESCRIPCION ,
                            ANULADO ,
                            VLR_DOCUMENTO ,
                            TEXTO ,
                            NUMEROCONTRATO ,
                            TIPOCONTRATO ,
                            NRO_DOCUMENTO ,
                            FECHA_VCN_DOC ,
                            CREATED_BY ,
                            DATE_CREATED';

            MI_VALORES:='SELECT COMPANIA ,
                          ANO ,
                          TIPO ,
                          NUMERO ,
                          FECHA ,
                          TERCERO,
                          SUCURSAL,
                          DESCRIPCION ,
                          ANULADO ,
                          VLR_DOCUMENTO ,
                          TEXTO ,
                          NUMEROCONTRATO ,
                          TIPOCONTRATO ,
                          NRO_DOCUMENTO ,
                          FECHA_VCN_DOC ,
                          '''||UN_USUARIO||''' ,
                          SYSDATE
                   FROM COMPROBANTE_CNT
                   WHERE COMPANIA = '''||UN_COMPANIA||'''
                     AND ANO        = '||UN_ANIO||'
                     AND TIPO       = '''||UN_TIPOCPTE||'''
                     AND NUMERO     = '||UN_NUMERO||'';

            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA  => 'COMPROBANTE_PPTAL',
                                                  UN_ACCION   => 'IS',
                                                  UN_CAMPOS   => MI_CAMPOS,
                                                  UN_VALORES  => MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
        MI_MSGERROR(1).CLAVE := 'NUMERO';
        MI_MSGERROR(1).VALOR := UN_NUMERO;
        MI_MSGERROR(2).CLAVE := 'TIPO';
        MI_MSGERROR(2).VALOR := UN_TIPOCPTE;
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSCOMPPPTAL
                                    ,UN_REEMPLAZOS  => MI_MSGERROR);
    END;


    IF MI_MANEQUIV4 = 'SI' AND NOT MI_APLICARECAUDO THEN    
        MI_STRSQL := 'WITH COMPROBANTE AS (SELECT COMPANIA, ANO, TIPO, NUMERO, FECHA, TERCERO, SUCURSAL,
                          DESCRIPCION, ANULADO, VLR_DOCUMENTO, TEXTO, NUMEROCONTRATO,
                          TIPOCONTRATO, NRO_DOCUMENTO, FECHA_VCN_DOC
                   FROM COMPROBANTE_CNT
                   WHERE COMPANIA = '''||UN_COMPANIA||'''
                     AND ANO        = '||UN_ANIO||'
                     AND TIPO       = '''||UN_TIPOCPTE||'''
                     AND NUMERO     = '||UN_NUMERO||') 
            SELECT PLAN_PPTAL_CUENTACNT.RUBRO,
                        SUM(DETALLE_COMPROBANTE_CNT.VALOR_CREDITO-DETALLE_COMPROBANTE_CNT.VALOR_DEBITO) VALOR,
                        DETALLE_COMPROBANTE_CNT.CENTRO_COSTO,
                        DETALLE_COMPROBANTE_CNT.TERCERO,
                        DETALLE_COMPROBANTE_CNT.SUCURSAL,
                        DETALLE_COMPROBANTE_CNT.AUXILIAR,
                        DETALLE_COMPROBANTE_CNT.REFERENCIA,
                        DETALLE_COMPROBANTE_CNT.NATURALEZA,
                        DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO ,
                        COMPROBANTE.FECHA,
                        DETALLE_COMPROBANTE_CNT.VALOR_IVA
                      FROM DETALLE_COMPROBANTE_CNT
            LEFT JOIN PLAN_CONTABLE
                      ON DETALLE_COMPROBANTE_CNT.COMPANIA             = PLAN_CONTABLE.COMPANIA
                      AND DETALLE_COMPROBANTE_CNT.ANO                 = PLAN_CONTABLE.ANO
                      AND DETALLE_COMPROBANTE_CNT.CUENTA              = PLAN_CONTABLE.CODIGO
            LEFT JOIN PLAN_PPTAL_CUENTACNT
                      ON DETALLE_COMPROBANTE_CNT.COMPANIA             = PLAN_PPTAL_CUENTACNT.COMPANIA
                      AND '||UN_ANIO||'                               = PLAN_PPTAL_CUENTACNT.ANO
                      AND DETALLE_COMPROBANTE_CNT.CUENTA              = PLAN_PPTAL_CUENTACNT.CUENTA_CONTABLE
            INNER JOIN COMPROBANTE
                      ON COMPROBANTE.COMPANIA = DETALLE_COMPROBANTE_CNT.COMPANIA
                      WHERE DETALLE_COMPROBANTE_CNT.COMPANIA          ='''||UN_COMPANIA||'''
                      AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE           ='''||UN_TIPO_PTE_AFEC||'''
                      AND DETALLE_COMPROBANTE_CNT.COMPROBANTE         ='||UN_NUMERO_AFEC||'
                      AND SUBSTR(DETALLE_COMPROBANTE_CNT.CUENTA,1,1) IN (4)
                      AND ROWNUM = 1
                      GROUP BY PLAN_PPTAL_CUENTACNT.RUBRO,
                        DETALLE_COMPROBANTE_CNT.CENTRO_COSTO ,
                        DETALLE_COMPROBANTE_CNT.TERCERO,
                        DETALLE_COMPROBANTE_CNT.SUCURSAL,
                        DETALLE_COMPROBANTE_CNT.AUXILIAR ,
                        DETALLE_COMPROBANTE_CNT.REFERENCIA ,
                        DETALLE_COMPROBANTE_CNT.NATURALEZA,
                        DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO ,
                        COMPROBANTE.FECHA,
                        DETALLE_COMPROBANTE_CNT.VALOR_IVA';

   ELSE IF MI_AFECTAPPTALWS = 'SI' THEN
            /*BEGIN  -- JM 7749239 22/07/2024 INI 
                SELECT COUNT(*) INTO MI_YA_EXISTE FROM DETALLE_COMPROBANTE_PPTAL WHERE DETALLE_COMPROBANTE_PPTAL.COMPANIA = UN_COMPANIA
                AND DETALLE_COMPROBANTE_PPTAL.ANO  = UN_ANIO
                AND DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE  = UN_TIPOCPTE
                AND DETALLE_COMPROBANTE_PPTAL.COMPROBANTE = UN_NUMERO;
            EXCEPTION WHEN  NO_DATA_FOUND THEN
                MI_YA_EXISTE := 0;
            END;
            IF MI_YA_EXISTE <> 0 THEN 
                BEGIN 
                        BEGIN 
                            MI_CONDICION := ' COMPANIA = '''||UN_COMPANIA||'''
                                             AND ANO = '||UN_ANIO||'
                                             AND TIPO_CPTE =  '''||UN_TIPOCPTE||'''
                                             AND COMPROBANTE   = '||UN_NUMERO;

                            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'DETALLE_COMPROBANTE_PPTAL',
                                              UN_ACCION    => 'E',
                                              UN_CONDICION => MI_CONDICION);   
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;                                
                        END;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE,
                            UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ELIM_COMP_PPTAL, 
                            UN_TABLAERROR => 'DETALLE_COMPROBANTE_PPTAL'
                          );
                      END; 
          END IF; -- JM 7749239 22/07/2024 FIN  */          
        MI_STRSQL := 'SELECT DETALLE_COMPROBANTE_CNT.LOTE,DETALLE_COMPROBANTE_CNT.CUENTAPPTAL,
                        (DETALLE_COMPROBANTE_CNT.VALOR_CREDITO-DETALLE_COMPROBANTE_CNT.VALOR_DEBITO) VALOR,
                        DETALLE_COMPROBANTE_CNT.CENTRO_COSTO,
                        DETALLE_COMPROBANTE_CNT.TERCERO,
                        DETALLE_COMPROBANTE_CNT.SUCURSAL,
                        DETALLE_COMPROBANTE_CNT.AUXILIAR,
                        DETALLE_COMPROBANTE_CNT.REFERENCIA,
                        DETALLE_COMPROBANTE_CNT.NATURALEZA,
                        DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO,
                        DETALLE_COMPROBANTE_CNT.FECHA,
                        DETALLE_COMPROBANTE_CNT.DESCRIPCION
                      FROM DETALLE_COMPROBANTE_CNT
                      LEFT JOIN PLAN_CONTABLE
                      ON DETALLE_COMPROBANTE_CNT.COMPANIA     = PLAN_CONTABLE.COMPANIA
                      AND DETALLE_COMPROBANTE_CNT.ANO         = PLAN_CONTABLE.ANO
                      AND DETALLE_COMPROBANTE_CNT.CUENTA      = PLAN_CONTABLE.CODIGO
                      WHERE DETALLE_COMPROBANTE_CNT.COMPANIA  ='''||UN_COMPANIA||'''
                      AND DETALLE_COMPROBANTE_CNT.ANO         ='||UN_ANIO||'
                      AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE   ='''||UN_TIPOCPTE||'''
                      AND DETALLE_COMPROBANTE_CNT.COMPROBANTE ='||UN_NUMERO||'
                      AND CLASECUENTA                        IN (''C'',''N'',''I'',''P'')
                      AND DETALLE_COMPROBANTE_CNT.CUENTAPPTAL IS NOT NULL';
    ELSE IF MI_PARAMETRO_Y = 'NO' AND MI_PARAGRUPAR = 'NO' THEN
    --LVEGA se agrega VALOR_IVA para llevarlo a presupuesto
      MI_STRSQL := 'SELECT DETALLE_COMPROBANTE_CNT.CUENTAPPTAL,
                        SUM(DETALLE_COMPROBANTE_CNT.VALOR_CREDITO-DETALLE_COMPROBANTE_CNT.VALOR_DEBITO) VALOR,
                        DETALLE_COMPROBANTE_CNT.CENTRO_COSTO,
                        DETALLE_COMPROBANTE_CNT.TERCERO,
                        DETALLE_COMPROBANTE_CNT.SUCURSAL,
                        DETALLE_COMPROBANTE_CNT.AUXILIAR,
                        DETALLE_COMPROBANTE_CNT.REFERENCIA,
                        DETALLE_COMPROBANTE_CNT.NATURALEZA,
                        DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO,
                        DETALLE_COMPROBANTE_CNT.FECHA,
                        DETALLE_COMPROBANTE_CNT.VALOR_IVA
                      FROM DETALLE_COMPROBANTE_CNT
            LEFT JOIN PLAN_CONTABLE 
                      ON DETALLE_COMPROBANTE_CNT.COMPANIA     = PLAN_CONTABLE.COMPANIA
                      AND DETALLE_COMPROBANTE_CNT.ANO         = PLAN_CONTABLE.ANO
                      AND DETALLE_COMPROBANTE_CNT.CUENTA      = PLAN_CONTABLE.CODIGO                      
                      WHERE DETALLE_COMPROBANTE_CNT.COMPANIA  ='''||UN_COMPANIA||'''
                      AND DETALLE_COMPROBANTE_CNT.ANO         ='||UN_ANIO||'
                      AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE   ='''||UN_TIPOCPTE||'''
                      AND DETALLE_COMPROBANTE_CNT.COMPROBANTE ='||UN_NUMERO||'
                      AND CLASECUENTA                        IN (''C'',''Y'',''P'')
                      AND DETALLE_COMPROBANTE_CNT.CUENTAPPTAL IS NOT NULL
                      GROUP BY
                        DETALLE_COMPROBANTE_CNT.CUENTAPPTAL,
                        DETALLE_COMPROBANTE_CNT.CENTRO_COSTO ,
                        DETALLE_COMPROBANTE_CNT.TERCERO,
                        DETALLE_COMPROBANTE_CNT.SUCURSAL,
                        DETALLE_COMPROBANTE_CNT.AUXILIAR,
                        DETALLE_COMPROBANTE_CNT.REFERENCIA,
                        DETALLE_COMPROBANTE_CNT.NATURALEZA,
                        DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO,
                        DETALLE_COMPROBANTE_CNT.FECHA,
                        DETALLE_COMPROBANTE_CNT.VALOR_IVA';
        ELSE IF MI_PARAGRUPAR = 'SI' THEN      
               MI_STRSQL := 'SELECT
                        D.CUENTAPPTAL,
                        G.VALOR,
                        G.CENTRO_COSTO,
                        G.TERCERO,
                        G.SUCURSAL,
                        G.AUXILIAR,
                        G.REFERENCIA,
                        G.NATURALEZA,
                        G.FUENTE_RECURSO,
                        G.FECHA,
                        G.VALOR_IVA
                    FROM ( 
                        SELECT 
                            SUM(VALOR_CREDITO - VALOR_DEBITO) AS VALOR,
                            CENTRO_COSTO,
                            CUENTAPPTAL,  
                            TERCERO,
                            SUCURSAL,
                            AUXILIAR,
                            REFERENCIA,
                            NATURALEZA,
                            FUENTE_RECURSO,
                            FECHA,
                            VALOR_IVA
                        FROM DETALLE_COMPROBANTE_CNT D
                        WHERE D.COMPANIA = '''||UN_COMPANIA||'''
                          AND D.ANO = '||UN_ANIO||'
                          AND D.TIPO_CPTE = '''||UN_TIPOCPTE||'''
                          AND D.COMPROBANTE = '||UN_NUMERO||'
                          AND D.CUENTAPPTAL IS NOT NULL
                        GROUP BY
                            CENTRO_COSTO, 
                            TERCERO, 
                            SUCURSAL, 
                            AUXILIAR,
                            REFERENCIA, 
                            NATURALEZA, 
                            FUENTE_RECURSO, 
                            FECHA, 
                            VALOR_IVA,
                            CUENTAPPTAL 
                    ) G
                    LEFT JOIN DETALLE_COMPROBANTE_CNT D ON
                        D.CUENTAPPTAL = G.CUENTAPPTAL AND 
                        D.CENTRO_COSTO = G.CENTRO_COSTO AND
                        D.TERCERO = G.TERCERO AND
                        D.SUCURSAL = G.SUCURSAL AND
                        D.AUXILIAR = G.AUXILIAR AND
                        D.REFERENCIA = G.REFERENCIA AND
                        D.NATURALEZA = G.NATURALEZA AND
                        D.FUENTE_RECURSO = G.FUENTE_RECURSO AND
                        D.FECHA = G.FECHA AND
                        D.VALOR_IVA = G.VALOR_IVA AND
                        D.COMPANIA = '''||UN_COMPANIA||''' AND
                        D.ANO = '||UN_ANIO||' AND
                        D.TIPO_CPTE = '''||UN_TIPOCPTE||''' AND
                        D.COMPROBANTE = '||UN_NUMERO||' AND
                        D.CUENTAPPTAL IS NOT NULL';                       
        ELSE 

         MI_STRSQL := 'SELECT DETALLE_COMPROBANTE_CNT.CUENTAPPTAL,
                        SUM(DETALLE_COMPROBANTE_CNT.VALOR_CREDITO-DETALLE_COMPROBANTE_CNT.VALOR_DEBITO) VALOR,
                        DETALLE_COMPROBANTE_CNT.CENTRO_COSTO,
                        DETALLE_COMPROBANTE_CNT.TERCERO,
                        DETALLE_COMPROBANTE_CNT.SUCURSAL,
                        DETALLE_COMPROBANTE_CNT.AUXILIAR,
                        DETALLE_COMPROBANTE_CNT.REFERENCIA,
                        DETALLE_COMPROBANTE_CNT.NATURALEZA,
                        DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO,
                        DETALLE_COMPROBANTE_CNT.FECHA,
                        DETALLE_COMPROBANTE_CNT.VALOR_IVA
                      FROM DETALLE_COMPROBANTE_CNT
            LEFT JOIN PLAN_CONTABLE 
                      ON DETALLE_COMPROBANTE_CNT.COMPANIA     = PLAN_CONTABLE.COMPANIA
                      AND DETALLE_COMPROBANTE_CNT.ANO         = PLAN_CONTABLE.ANO
                      AND DETALLE_COMPROBANTE_CNT.CUENTA      = PLAN_CONTABLE.CODIGO                      
                      WHERE DETALLE_COMPROBANTE_CNT.COMPANIA  ='''||UN_COMPANIA||'''
                      AND DETALLE_COMPROBANTE_CNT.ANO         ='||UN_ANIO||'
                      AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE   ='''||UN_TIPOCPTE||'''
                      AND DETALLE_COMPROBANTE_CNT.COMPROBANTE ='||UN_NUMERO||'
                      AND CLASECUENTA                        IN (''C'', ''Y'')
                      AND DETALLE_COMPROBANTE_CNT.CUENTAPPTAL IS NOT NULL
                      GROUP BY
                        DETALLE_COMPROBANTE_CNT.CUENTAPPTAL,
                        DETALLE_COMPROBANTE_CNT.CENTRO_COSTO ,
                        DETALLE_COMPROBANTE_CNT.TERCERO,
                        DETALLE_COMPROBANTE_CNT.SUCURSAL,
                        DETALLE_COMPROBANTE_CNT.AUXILIAR,
                        DETALLE_COMPROBANTE_CNT.REFERENCIA,
                        DETALLE_COMPROBANTE_CNT.NATURALEZA,
                        DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO,
                        DETALLE_COMPROBANTE_CNT.FECHA,
                        DETALLE_COMPROBANTE_CNT.VALOR_IVA';
         END IF;
       END IF;
     END IF;
    END IF;

    <<CREACION_DETALLE_PPTAL>>
    OPEN MI_RS FOR MI_STRSQL;
    LOOP
       IF(MI_AFECTAPPTALWS = 'SI') THEN 
        FETCH MI_RS INTO MI_LOTE,MI_CUENTA,MI_VALOR,MI_CENTRO_COSTO,MI_TERCERO,MI_SUCURSAL,MI_AUXILIAR,MI_REFERENCIA,MI_NATURALEZA,MI_FUENTERECURSO,MI_FECHA,MI_DESCRIPCIONCNT ; 
      ELSE 
        FETCH MI_RS INTO MI_CUENTA,MI_VALOR,MI_CENTRO_COSTO,MI_TERCERO,MI_SUCURSAL,MI_AUXILIAR,MI_REFERENCIA,MI_NATURALEZA,MI_FUENTERECURSO,MI_FECHA,MI_VALOR_IVA ;
      END IF;
        EXIT WHEN MI_RS%NOTFOUND;
        BEGIN
            BEGIN
            IF(MI_AFECTAPPTALWS = 'SI')THEN --MPEREZ TICKET_7716154
            MI_CAMPOS := 'LOTE,COMPANIA,
                            ANO,
                            TIPO_CPTE,
                            COMPROBANTE,
                            CONSECUTIVO,
                            CUENTA,
                            FECHA,
                            DESCRIPCION,
                            VALOR_DEBITO,
                            VALOR_CREDITO,
                            TERCERO,
                            SUCURSAL,
                            CENTRO_COSTO,
                            AUXILIAR,
                            REFERENCIA,
                            FUENTE_RECURSO,
                            NATURALEZA,
                            CREATED_BY,
                            DATE_CREATED';
            ELSE
                MI_CAMPOS := 'COMPANIA,
                            ANO,
                            TIPO_CPTE,
                            COMPROBANTE,
                            CONSECUTIVO,
                            CUENTA,
                            FECHA,
                            DESCRIPCION,
                            VALOR_DEBITO,
                            VALOR_CREDITO,
                            TERCERO,
                            SUCURSAL,
                            CENTRO_COSTO,
                            AUXILIAR,
                            REFERENCIA,
                            FUENTE_RECURSO,
                            NATURALEZA,
                            CREATED_BY,
                            DATE_CREATED';
          END IF;
          --MI_NATURALEZA = 'C' AND
        IF( MI_AFECTAPPTALWS = 'SI')THEN --MPEREZ TICKET7720232
                    IF(MI_VALOR < 0) THEN --JM INI CC528 26/12/2024
                        MI_VALOR := MI_VALOR*(-1);
                                  MI_VALORES := ''||MI_LOTE||',
                                  '''||UN_COMPANIA||'''
                                  ,'||UN_ANIO||'
                                  ,'''||UN_TIPOCPTE||'''
                                  ,'||UN_NUMERO||'
                                  ,'||MI_CONSECUTIVO||'
                                  ,'''||MI_CUENTA||'''
                                  ,'''||MI_FECHA||'''
                                  ,'''||MI_DESCRIPCIONCNT||'''
                                  ,'||MI_VALOR||'
                                  ,0
                                  ,'''||MI_TERCERO||'''
                                  ,'''||MI_SUCURSAL||'''
                                  ,'''||MI_CENTRO_COSTO||'''
                                  ,'''||MI_AUXILIAR||'''
                                  ,'''||MI_REFERENCIA||'''
                                  ,'''||MI_FUENTERECURSO||'''
                                  ,'''||MI_NATURALEZA||'''
                                  ,'''||UN_USUARIO||'''
                                  ,SYSDATE' ;
                                  
                        ELSE 
                                   MI_VALORES := ''||MI_LOTE||',
                                  '''||UN_COMPANIA||'''
                                  ,'||UN_ANIO||'
                                  ,'''||UN_TIPOCPTE||'''
                                  ,'||UN_NUMERO||'
                                  ,'||MI_CONSECUTIVO||'
                                  ,'''||MI_CUENTA||'''
                                  ,'''||MI_FECHA||'''
                                  ,'''||MI_DESCRIPCIONCNT||'''
                                  ,0
                                  ,'||MI_VALOR||'
                                  ,'''||MI_TERCERO||'''
                                  ,'''||MI_SUCURSAL||'''
                                  ,'''||MI_CENTRO_COSTO||'''
                                  ,'''||MI_AUXILIAR||'''
                                  ,'''||MI_REFERENCIA||'''
                                  ,'''||MI_FUENTERECURSO||'''
                                  ,'''||MI_NATURALEZA||'''
                                  ,'''||UN_USUARIO||'''
                                  ,SYSDATE' ;
                        END IF; --JM FIN CC528 26/12/2024
                ELSE
                    MI_VALORES := ' '''||UN_COMPANIA||'''
                                  ,'||UN_ANIO||'
                                  ,'''||UN_TIPOCPTE||'''
                                  ,'||UN_NUMERO||'
                                  ,'||MI_CONSECUTIVO||'
                                  ,'''||MI_CUENTA||'''
                                  ,'''||MI_FECHA||'''
                                  ,'''||UN_DESCRIPCION||'''
                                  ,0
                                  ,'||MI_VALOR||'
                                  ,'''||MI_TERCERO||'''
                                  ,'''||MI_SUCURSAL||'''
                                  ,'''||MI_CENTRO_COSTO||'''
                                  ,'''||MI_AUXILIAR||'''
                                  ,'''||MI_REFERENCIA||'''
                                  ,'''||MI_FUENTERECURSO||'''
                                  ,'''||MI_NATURALEZA||'''
                                  ,'''||UN_USUARIO||'''
                                  ,SYSDATE' ;
                END IF;
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                        UN_TABLA => 'DETALLE_COMPROBANTE_PPTAL',
                        UN_ACCION => 'I',
                        UN_CAMPOS => MI_CAMPOS,
                        UN_VALORES => MI_VALORES
                      );

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
            MI_MSGERROR(1).CLAVE := 'NUMERO';
            MI_MSGERROR(1).VALOR := UN_NUMERO;
            MI_MSGERROR(2).CLAVE := 'TIPO';
            MI_MSGERROR(2).VALOR := UN_TIPOCPTE;
            PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD     => SQLCODE,
                          UN_ERROR_COD   => PCK_ERRORES.ERR_SYSMANSF_INSDETACOMPPPTAL,
                          UN_REEMPLAZOS  => MI_MSGERROR
                        );
        END;
          --lvega se actualiza el valor credito en las notas clientes o ingresos
             BEGIN
                SELECT CLASE_CONTABLE
                INTO MI_TIPO_COMPROBANTE
                FROM TIPO_COMPROBANTE
                WHERE  COMPANIA = UN_COMPANIA
                AND CODIGO  =  UN_TIPOCPTE;
             EXCEPTION  WHEN NO_DATA_FOUND THEN
                    MI_TIPO_COMPROBANTE := '';
             END;
              MI_PARAMETRO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA, 'MANEJA FACTURAS CON IVA', 69 , SYSDATE),'NO');
              IF MI_PARAMETRO = 'SI' AND MI_AFECTAPPTALWS = 'NO' AND MI_VALOR_IVA > 0 AND MI_TIPO_COMPROBANTE IN('I','B') THEN
                BEGIN
                MI_VALOR := MI_VALOR  -  MI_VALOR_IVA;
                MI_CAMPOS := 'VALOR_CREDITO  = '|| MI_VALOR ||'';
                MI_CONDICION :=' COMPANIA = '''||UN_COMPANIA||'''
                              AND ANO      =  '||UN_ANIO||'
                              AND TIPO_CPTE     = '''||UN_TIPOCPTE||'''
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
        MI_CONSECUTIVO := MI_CONSECUTIVO +1 ;

    END LOOP CREACION_DETALLE_PPTAL;

END PR_CREARCOMPROBANTEPPTAL;

PROCEDURE PR_ACTUALIZARVLRTOTALFACXTRM
 /*
    NAME              : PR_ACTUALIZARVLRTOTALFACXTRM
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : MARÃ�A ALEJANDRA PEREZ SALAZAR
    DATE MIGRADOR     : 13/01/2023
    TIME              : 09:35 AM
    DESCRIPTION       : PROCEDIMIENTO ENCARGADO DE ACUTALIZAR EL VALOR TOTAL DE LA FACTURA 
                        CUANDO SE MANEJA FACTURACION EN DOLARES Y LA TRM CAMBIA AL MOMENTO EL RECAUDO. 

    PARAMETROS        : UN_COMPANIA      => COMPANIA DE INGRESO A LA COMPANIA 
                        UN_TIPOCOBRO     => TIPO DE COBRO A FACTURAR
                        UN_CODIGO_COBRO  => CODIGO COBRO DEL PROCESO DE FACTURACION
                        UN_ANIO          => ANO DE INGRESO AL MODULO DE FACTURACION

    @NAME actualizarVlrTotalFacxTRM                    

  */

(
  UN_COMPANIA        PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOCOBRO       SF_FACTURA.TIPO_FACTURA%TYPE,
  UN_NUMERO_FACTURA  SF_FACTURA.NUMERO_FACTURA%TYPE, 
  UN_VALORFACTURA    PCK_SUBTIPOS.TI_DOBLE,
  UN_VALORTRM        PCK_SUBTIPOS.TI_DOBLE,
  UN_ANIO            PCK_SUBTIPOS.TI_ANIO
)
AS 
 MI_VALORTOTAL           PCK_SUBTIPOS.TI_DOBLE;
 MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
 MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
 MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR; 
 MI_RTA                  PCK_SUBTIPOS.TI_PARAMETRO;
 MI_CODIGOCOBRO          SF_FACTURA.CODIGO_COBRO%TYPE;

BEGIN
    SELECT CODIGO_COBRO
      INTO MI_CODIGOCOBRO 
      FROM SF_FACTURA
     WHERE COMPANIA       = UN_COMPANIA
       AND ANO            = UN_ANIO
       AND TIPO_FACTURA   = UN_TIPOCOBRO
       AND NUMERO_FACTURA = UN_NUMERO_FACTURA;   
    
    BEGIN 
        BEGIN 
            MI_CAMPOS    := 'TRM_DOLARES_REC       = '||UN_VALORTRM;
            
            MI_CONDICION := 'COMPANIA          = '''||UN_COMPANIA||'''
                          AND NUMERO_FACTURA = '||UN_NUMERO_FACTURA||'
                          AND ANO            = '||UN_ANIO||'
                          AND TIPO_FACTURA   = '''||UN_TIPOCOBRO||'''';

            PCK_DATOS.GL_RTA :=PCK_DATOS.FC_ACME(UN_ACCION    =>'M',
                                               UN_TABLA     =>'SF_FACTURA',
                                               UN_CAMPOS    =>MI_CAMPOS,
                                               UN_CONDICION =>MI_CONDICION);                

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
          MI_MSGERROR(1).CLAVE := 'NUMERO_FACTURA';
          MI_MSGERROR(1).VALOR := UN_NUMERO_FACTURA;  
         RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                               UN_TABLAERROR => 'SF_FACTURA', 
                               UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTUA_VAL_TOT,
                                UN_REEMPLAZOS => MI_MSGERROR);
    END;      
    BEGIN 
        BEGIN
            MI_CONDICION := ' COMPANIA     = '''||UN_COMPANIA||'''
                          AND TIPOCOBRO    = '''||UN_TIPOCOBRO||'''
                          AND CODIGO_COBRO = '||MI_CODIGOCOBRO||'
                          AND ANO          = '||UN_ANIO;

            MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'SF_OBJETO_COBRO',
                                        UN_ACCION    => 'M',
                                        UN_CAMPOS    => MI_CAMPOS,
                                        UN_CONDICION => MI_CONDICION);
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
           RAISE PCK_EXCEPCIONES.EXC_FACTURACION;                         
         END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                 UN_TABLAERROR => 'SF_OBJETO_COBRO',
                                 UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTUA_NROFAC); 
    END;    

END PR_ACTUALIZARVLRTOTALFACXTRM;

PROCEDURE PR_ACTUALIZARSALDOCONCEPTO
 /*
    NAME              : PR_ACTUALIZARSALDOCONCEPTO
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : MARÃ�A ALEJANDRA PEREZ SALAZAR
    DATE MIGRADOR     : 01/06/2023
    TIME              : 09:35 AM
    DESCRIPTION       : PROCEDIMIENTO ENCARGADO DE ACTUALIZAR EL SALDO ACTUAL EN DETALLE DEL CONTRATO
                        POR CONCEPTO

    PARAMETROS        : UN_COMPANIA         => COMPANIA DE INGRESO A LA COMPANIA 
                        UN_TIPO             => TIPO DE COBRO A FACTURAR
                        UN_CONCEPTO         => CONCEPTO DEL PROCESO DE FACTURACION
                        UN_NUMERO_CONTRATO  => CONTRATO ASOCIADO A LA FACTURACION
                        UN_SALDO_ACTUAL     => SALDO A ACTUALIZAR

    @NAME actualizarSaldoConcepto                    

  */

(
  UN_COMPANIA        PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPO            SF_DETALLE_CONTRATO.TIPO%TYPE,
  UN_CONCEPTO        SF_DETALLE_CONTRATO.CONCEPTO%TYPE,
  UN_NUMERO_CONTRATO SF_DETALLE_CONTRATO.NUMERO%TYPE,
  UN_SALDO_ACTUAL    PCK_SUBTIPOS.TI_DOBLE
)
AS 
 MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
 MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
 MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR; 
 MI_RTA                  PCK_SUBTIPOS.TI_PARAMETRO;

BEGIN    
    BEGIN 
        BEGIN 
            MI_CAMPOS    := 'SALDO_ACTUAL      = '||UN_SALDO_ACTUAL;
            
            MI_CONDICION := 'COMPANIA  = '''||UN_COMPANIA||'''
                          AND TIPO     = '''||UN_TIPO||'''  
                          AND NUMERO   = '||UN_NUMERO_CONTRATO||'
                          AND CONCEPTO = '''||UN_CONCEPTO||'''';

            PCK_DATOS.GL_RTA :=PCK_DATOS.FC_ACME(UN_ACCION    =>'M',
                                               UN_TABLA     =>'SF_DETALLE_CONTRATO',
                                               UN_CAMPOS    =>MI_CAMPOS,
                                               UN_CONDICION =>MI_CONDICION);                

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
          MI_MSGERROR(1).CLAVE := 'TIPO';
          MI_MSGERROR(1).VALOR := UN_TIPO;  
          MI_MSGERROR(2).CLAVE := 'NUMERO';
          MI_MSGERROR(2).VALOR := UN_NUMERO_CONTRATO;
          MI_MSGERROR(3).CLAVE := 'CONCEPTO';
          MI_MSGERROR(3).VALOR := UN_CONCEPTO;
         RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                               UN_TABLAERROR => 'SF_DETALLE_CONTRATO', 
                               UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTUA_VAL_TOT,
                               UN_REEMPLAZOS => MI_MSGERROR);
    END;          

END PR_ACTUALIZARSALDOCONCEPTO;

FUNCTION FC_OBTENERSALDOCONCEPTO
/*
      NAME              : FC_OBTENERSALDOCONCEPTO
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
      DATE MIGRADOR     : 02/06/2023
      TIME              : 04:48 PM
      DESCRIPTION       :
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 

    @NAME:   obtenerSaldoConcepto
    @METHOD: GET
*/
(
  UN_COMPANIA        PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPO            SF_DETALLE_CONTRATO.TIPO%TYPE,
  UN_CONCEPTO        SF_DETALLE_CONTRATO.CONCEPTO%TYPE,
  UN_NUMERO_CONTRATO SF_DETALLE_CONTRATO.NUMERO%TYPE
)
RETURN PCK_SUBTIPOS.TI_DOBLE AS 

  MI_RPTA           PCK_SUBTIPOS.TI_DOBLE;
BEGIN

    SELECT SALDO_ACTUAL
      INTO MI_RPTA 
      FROM SF_DETALLE_CONTRATO
     WHERE COMPANIA = UN_COMPANIA
       AND TIPO     = UN_TIPO
       AND NUMERO   = UN_NUMERO_CONTRATO
       AND CONCEPTO = UN_CONCEPTO;

  RETURN MI_RPTA;

     EXCEPTION WHEN NO_DATA_FOUND THEN 

       MI_RPTA := 0; 

  RETURN MI_RPTA;

END FC_OBTENERSALDOCONCEPTO;

FUNCTION FC_OBTENERVALORNETOANT
/*
      NAME              : FC_OBTENERVALORNETOANT
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
      DATE MIGRADOR     : 02/06/2023
      TIME              : 04:48 PM
      DESCRIPTION       :
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 

    @NAME:   obtenerValorNetoAnt
    @METHOD: GET
*/
(
  UN_COMPANIA        PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO            SF_DETALLE_COBRO.ANO%TYPE, 
  UN_TIPO            SF_DETALLE_COBRO.TIPOCOBRO%TYPE,
  UN_CONCEPTO        SF_DETALLE_COBRO.CONCEPTO%TYPE,
  UN_CODIGO_COBRO    SF_DETALLE_COBRO.CODIGO_COBRO%TYPE
)
RETURN PCK_SUBTIPOS.TI_DOBLE AS 

  MI_RPTA           PCK_SUBTIPOS.TI_DOBLE;
BEGIN

    SELECT VALOR_NETO
      INTO MI_RPTA 
      FROM SF_DETALLE_COBRO
     WHERE COMPANIA     = UN_COMPANIA
       AND ANO          = UN_ANIO
       AND TIPOCOBRO    = UN_TIPO
       AND CODIGO_COBRO = UN_CODIGO_COBRO
       AND CONCEPTO     = UN_CONCEPTO;

  RETURN MI_RPTA;

     EXCEPTION WHEN NO_DATA_FOUND THEN 

       MI_RPTA := 0; 

  RETURN MI_RPTA;

END FC_OBTENERVALORNETOANT;

PROCEDURE PR_NEWCOMPRPPTALARQC
  /*
  NAME              : PR_NEWCOMPRPPTALARQC 
  AUTHORS           : LJDIAZ
  TIME              : 14:22 PM
  
  DATE              : 11/09/2023
  DESCRIPTION       : Se crea version para el caso en el que el recaudo venga desde la arq c

  */
(
  UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOCPTE        IN TIPO_COMPROBPP.CODIGO%TYPE,
  UN_ANIO            IN PCK_SUBTIPOS.TI_ENTERO,
  UN_NUMERO          IN COMPROBANTE_PPTAL.NUMERO%TYPE,
  UN_NUMERO_AFEC     IN COMPROBANTE_PPTAL.NUMERO%TYPE,
  UN_TIPO_PTE_AFEC   IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
  UN_DESCRIPCION     IN COMPROBANTE_PPTAL.DESCRIPCION%TYPE,
  UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO,
  UN_VALORRECAUDO    IN PCK_SUBTIPOS.TI_DOBLE := 0   --(MZANGUNA:16/04/2019)-Para pagos parciales desde wsRentasCaqueta

)AS
  MI_STRSQL           PCK_SUBTIPOS.TI_STRSQL;
  MI_CONTEO           PCK_SUBTIPOS.TI_ENTERO;
  MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
  MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
  MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_MANEQUIV4        PCK_SUBTIPOS.TI_STRSQL;
  MI_CONSECUTIVO      PCK_SUBTIPOS.TI_ENTERO;
  MI_RS               SYS_REFCURSOR;
  MI_CUENTA           DETALLE_COMPROBANTE_CNT.CUENTAPPTAL%TYPE;
  MI_VALOR            PCK_SUBTIPOS.TI_DOBLE;
  MI_CENTRO_COSTO     PCK_SUBTIPOS.TI_CENTRO_COSTO;
  MI_TERCERO          PCK_SUBTIPOS.TI_TERCERO;
  MI_SUCURSAL         PCK_SUBTIPOS.TI_SUCURSAL;
  MI_AUXILIAR         PCK_SUBTIPOS.TI_AUXILIAR;
  MI_REFERENCIA       PCK_SUBTIPOS.TI_REFERENCIA;
  MI_NATURALEZA       PCK_SUBTIPOS.TI_NATURALEZACONTA;
  MI_FUENTERECURSO    PCK_SUBTIPOS.TI_FUENTE_RECURSOS;
  MI_FECHA            DATE;
  MI_APLICARECAUDO    BOOLEAN DEFAULT FALSE;

  /*INI TICKET 7716154 MPEREZ*/
  MI_CODIGOCCPET     DETALLE_COMPROBANTE_PPTAL.CODIGO_CCPET%TYPE;
  MI_FUENTECUIPO     DETALLE_COMPROBANTE_PPTAL.FUENTE_CUIPO%TYPE;
  MI_UNIDADEJE       DETALLE_COMPROBANTE_PPTAL.CODIGOUNIDADEJE%TYPE;
  /*FIN TICKET 7716154 MPEREZ*/
  /*INI TICKET 7732652  JMILLAN (considerar las cuentas tipo clase Y)*/
  MI_PARAMETRO_Y                    PCK_SUBTIPOS.TI_STRSQL;
  /*FIN TICKET 7732652  JMILLAN */
BEGIN

    MI_CONSECUTIVO := 1;
   
    MI_APLICARECAUDO := CASE WHEN UN_VALORRECAUDO <> 0 AND UN_VALORRECAUDO > 0 THEN TRUE ELSE FALSE END;    --(MZANGUNA:16/04/2019)
    BEGIN
        BEGIN
            --Creacion comprobante pptal
            MI_CAMPOS := 'COMPANIA ,
                            ANO ,
                            TIPO ,
                            NUMERO ,
                            FECHA ,
                            TERCERO,
                            SUCURSAL,
                            DESCRIPCION ,
                            ANULADO ,
                            VLR_DOCUMENTO ,
                            TEXTO ,
                            NUMEROCONTRATO ,
                            TIPOCONTRATO ,
                            NRO_DOCUMENTO ,
                            FECHA_VCN_DOC ,
                            CREATED_BY ,
                            DATE_CREATED';

            MI_VALORES:='SELECT COMPANIA ,
                          ANO ,
                          TIPO ,
                          NUMERO ,
                          FECHA ,
                          TERCERO,
                          SUCURSAL,
                          DESCRIPCION ,
                          ANULADO ,
                          VLR_DOCUMENTO ,
                          TEXTO ,
                          NUMEROCONTRATO ,
                          TIPOCONTRATO ,
                          NRO_DOCUMENTO ,
                          FECHA_VCN_DOC ,
                          '''||UN_USUARIO||''' ,
                          SYSDATE
                   FROM COMPROBANTE_CNT
                   WHERE COMPANIA = '''||UN_COMPANIA||'''
                     AND ANO        = '||UN_ANIO||'
                     AND TIPO       = '''||UN_TIPOCPTE||'''
                     AND NUMERO     = '||UN_NUMERO||'';

            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA  => 'COMPROBANTE_PPTAL',
                                                  UN_ACCION   => 'IS',
                                                  UN_CAMPOS   => MI_CAMPOS,
                                                  UN_VALORES  => MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
        MI_MSGERROR(1).CLAVE := 'NUMERO';
        MI_MSGERROR(1).VALOR := UN_NUMERO;
        MI_MSGERROR(2).CLAVE := 'TIPO';
        MI_MSGERROR(2).VALOR := UN_TIPOCPTE;
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSCOMPPPTAL
                                    ,UN_REEMPLAZOS  => MI_MSGERROR);
    END;

     MI_STRSQL := 'SELECT DETALLE_COMPROBANTE_CNT.CUENTAPPTAL,
                        ABS(SUM(DETALLE_COMPROBANTE_CNT.VALOR_CREDITO-DETALLE_COMPROBANTE_CNT.VALOR_DEBITO)) VALOR,
                        DETALLE_COMPROBANTE_CNT.CENTRO_COSTO,
                        DETALLE_COMPROBANTE_CNT.TERCERO,
                        DETALLE_COMPROBANTE_CNT.SUCURSAL,
                        DETALLE_COMPROBANTE_CNT.AUXILIAR,
                        DETALLE_COMPROBANTE_CNT.REFERENCIA,
                        DETALLE_COMPROBANTE_CNT.NATURALEZA,
                        DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO ,
                        DETALLE_COMPROBANTE_CNT.FECHA
                      FROM DETALLE_COMPROBANTE_CNT
            LEFT JOIN PLAN_CONTABLE
                      ON DETALLE_COMPROBANTE_CNT.COMPANIA             = PLAN_CONTABLE.COMPANIA
                      AND DETALLE_COMPROBANTE_CNT.ANO                 = PLAN_CONTABLE.ANO
                      AND DETALLE_COMPROBANTE_CNT.CUENTA              = PLAN_CONTABLE.CODIGO            
                      WHERE DETALLE_COMPROBANTE_CNT.COMPANIA          ='''||UN_COMPANIA||'''
                      AND DETALLE_COMPROBANTE_CNT.ANO                 ='||UN_ANIO||'
                      AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE           ='''||UN_TIPO_PTE_AFEC||'''
                      AND DETALLE_COMPROBANTE_CNT.COMPROBANTE         ='||UN_NUMERO_AFEC||'
                      AND DETALLE_COMPROBANTE_CNT.CUENTAPPTAL IS NOT NULL
                      GROUP BY DETALLE_COMPROBANTE_CNT.CUENTAPPTAL,
                        DETALLE_COMPROBANTE_CNT.CENTRO_COSTO ,
                        DETALLE_COMPROBANTE_CNT.TERCERO,
                        DETALLE_COMPROBANTE_CNT.SUCURSAL,
                        DETALLE_COMPROBANTE_CNT.AUXILIAR ,
                        DETALLE_COMPROBANTE_CNT.REFERENCIA ,
                        DETALLE_COMPROBANTE_CNT.NATURALEZA,
                        DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO ,
                        DETALLE_COMPROBANTE_CNT.FECHA';
                        
    <<CREACION_DETALLE_PPTAL>>
    OPEN MI_RS FOR MI_STRSQL;
    LOOP
        FETCH MI_RS INTO MI_CUENTA,MI_VALOR,MI_CENTRO_COSTO,MI_TERCERO,MI_SUCURSAL,MI_AUXILIAR,MI_REFERENCIA,MI_NATURALEZA,MI_FUENTERECURSO,MI_FECHA ; 
        EXIT WHEN MI_RS%NOTFOUND;
        BEGIN
            BEGIN
                MI_CAMPOS := 'COMPANIA,
                            ANO,
                            TIPO_CPTE,
                            COMPROBANTE,
                            CONSECUTIVO,
                            CUENTA,
                            FECHA,
                            DESCRIPCION,
                            VALOR_DEBITO,
                            VALOR_CREDITO,
                            TERCERO,
                            SUCURSAL,
                            CENTRO_COSTO,
                            AUXILIAR,
                            REFERENCIA,
                            FUENTE_RECURSO,
                            NATURALEZA,
                            CREATED_BY,
                            DATE_CREATED';

                MI_VALORES := ' '''||UN_COMPANIA||'''
                              ,'||UN_ANIO||'
                              ,'''||UN_TIPOCPTE||'''
                              ,'||UN_NUMERO||'
                              ,'||MI_CONSECUTIVO||'
                              ,'''||MI_CUENTA||'''
                              ,'''||MI_FECHA||'''
                              ,'''||UN_DESCRIPCION||'''
                              ,0
                              ,'||MI_VALOR||'
                              ,'''||MI_TERCERO||'''
                              ,'''||MI_SUCURSAL||'''
                              ,'''||MI_CENTRO_COSTO||'''
                              ,'''||MI_AUXILIAR||'''
                              ,'''||MI_REFERENCIA||'''
                              ,'''||MI_FUENTERECURSO||'''
                              ,'''||MI_NATURALEZA||'''
                ,'''||UN_USUARIO||'''
                              ,SYSDATE' ;


                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                        UN_TABLA => 'DETALLE_COMPROBANTE_PPTAL',
                        UN_ACCION => 'I',
                        UN_CAMPOS => MI_CAMPOS,
                        UN_VALORES => MI_VALORES
                      );

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
            MI_MSGERROR(1).CLAVE := 'NUMERO';
            MI_MSGERROR(1).VALOR := UN_NUMERO;
            MI_MSGERROR(2).CLAVE := 'TIPO';
            MI_MSGERROR(2).VALOR := UN_TIPOCPTE;
            PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD     => SQLCODE,
                          UN_ERROR_COD   => PCK_ERRORES.ERR_SYSMANSF_INSDETACOMPPPTAL,
                          UN_REEMPLAZOS  => MI_MSGERROR
                        );
        END;
        MI_CONSECUTIVO := MI_CONSECUTIVO +1 ;

    END LOOP CREACION_DETALLE_PPTAL;

END PR_NEWCOMPRPPTALARQC;
FUNCTION FC_DEL_DESTALLESCONTRATO(
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TIPO    IN SF_CONTRATOS.TIPO%TYPE,
    UN_NUMERO  IN SF_CONTRATOS.NUMERO%TYPE
)
RETURN PCK_SUBTIPOS.TI_LOGICO
IS
    MI_RTA PCK_SUBTIPOS.TI_LOGICO := -1;
BEGIN
    BEGIN
        DELETE FROM SF_DETALLE_CONTRATO
        WHERE COMPANIA = UN_COMPANIA
        AND TIPO = UN_TIPO
        AND NUMERO = UN_NUMERO;
        COMMIT; -- Commit the transaction
        MI_RTA := 1; -- Indicate successful deletion
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK; -- Rollback the transaction in case of error
            MI_RTA := 0;
            -- Log the error message or raise a specific exception
            RAISE_APPLICATION_ERROR(-20001, 'Error deleting contract details: ' || SQLERRM);
    END;

    RETURN MI_RTA;
END FC_DEL_DESTALLESCONTRATO;
END PCK_FACT_GENERAL_COM3;