create or replace PACKAGE BODY PCK_FACT_GENERAL_COM4 AS

--1

FUNCTION FC_INTERFAZCONTABLENOFACT
/*
      NAME              : FC_INTERFAZCONTABLENOFACT MIGRADO DE ACCESS InterfazContableNoFacturado()
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 11/10/2018
      TIME              : 11:00 AM
      SOURCE MODULE     : SysmanSF2018.07.06
      DESCRIPTION       :
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 


    @NAME:   manejarInterfazContableNoFacturado
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

BEGIN

  MI_CONSECUTIVO := 0;
  MI_ANIO := EXTRACT (YEAR FROM UN_FECHA);
  MI_MES := EXTRACT (MONTH FROM UN_FECHA);

  MI_PARMANEJATERCERO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                    UN_NOMBRE 	 => 'SF MANEJA CONFIGURACION DE CTA POR TERCERO',
                                    UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                    UN_FECHA_PAR => SYSDATE,
                                    UN_IND_MAYUS =>0);


  MI_MANEQUIV4 := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                    UN_NOMBRE 	 => 'SF MANEJAR EQUIVALENCIA PRESUPUESTAL EN LA CUENTA 4',
                                    UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                    UN_FECHA_PAR => SYSDATE,
                                    UN_IND_MAYUS =>0);                                                 

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
        SF_CONCEPTOS.CUENTACREDITOBASE CTACREDITOCREE
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

        MI_NUMERO := PCK_CONTABILIDAD_CPTE.FC_ENUMERARCOMPROBANTECNT(UN_COMPANIA     => UN_COMPANIA
                                                                     ,UN_ANIO         => MI_ANIO
                                                                     ,UN_TIPO         => UN_TIPO
                                                                     ,UN_NUMERO       => 0
                                                                     ,UN_CENTRO_COSTO => '');                      


      --Etapa 02

        MI_CONSC := FALSE;

        IF MI_CLASECONTABLE IN ('V','I')THEN          
          MI_DEBITO   := NVL(MI_RS.CUENTADEBITOBASE,'');
          MI_CREDITO  := NVL(MI_RS.CUENTACREDITOBASE,'');        
        ELSE  
          MI_CREDITO  := NVL(MI_RS.CUENTADEBITOBASE,'');
          MI_DEBITO   := NVL(MI_RS.CUENTACREDITOBASE,'');

        END IF;


      -- '********************************************************INICIO INVENTARIO ***************************************************** 

 IF UN_MANEJAINVENTARIO NOT IN (0) THEN
      -- '*************   REGISTRO DEL VALOR DE ACOMETIDA  *******************
      -- '*****************          DEBITO                *******************
    IF NVL(MI_RS.VALOR_COMPRA,0) NOT IN (0)THEN        

      IF MI_DEBITO IS NOT NULL THEN 

          MI_CTAPPTAL := '';

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

          SELECT CUENTA_PPTAL
            INTO MI_CTAPPTAL
          FROM PLAN_CONTABLE          
          WHERE COMPANIA = UN_COMPANIA
            AND ANO      = MI_ANIO
            AND CODIGO   = MI_CREDITO;

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

                IF MI_CLASECONTABLE IN ('V','I') THEN
                    MI_DEBITO := NVL(MI_RS.CUENTADEBITOUTILIDAD, '');
                    MI_CREDITO := NVL(MI_RS.CUENTACREDITOUTILIDAD,'');
                ELSE
                    MI_CREDITO := NVL(MI_RS.CUENTADEBITOUTILIDAD,'');
                    MI_DEBITO := NVL(MI_RS.CUENTACREDITOUTILIDAD,'');
                END IF;

       IF MI_DEBITO IS NOT NULL THEN

             MI_CTAPPTAL := '';
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

             SELECT CUENTA_PPTAL
             INTO MI_CTAPPTAL
             FROM PLAN_CONTABLE          
             WHERE COMPANIA = UN_COMPANIA
               AND ANO      = MI_ANIO
               AND CODIGO   = MI_CREDITO;

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

       IF MI_CLASECONTABLE IN ('V','I') THEN
            MI_DEBITO := NVL(MI_RS.CUENTADEBITOBASE, '');
            MI_CREDITO := NVL(MI_RS.CUENTACREDITOBASE, '');
       ELSE
            MI_CREDITO := NVL(MI_RS.CUENTADEBITOBASE, '');
            MI_DEBITO := NVL(MI_RS.CUENTACREDITOBASE, '');
       END IF;


       --*************   REGISTRO DEL VALOR BASE   *******************
       --*****************        DEBITO        *******************
      IF NVL(MI_RS.VALOR_BASE, 0) <> 0 THEN
          IF MI_DEBITO IS NOT NULL THEN

             MI_CTAPPTAL := '';
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
                               ,'||MI_RS.VALOR_BASE||'
                               ,0
                               ,'||NVL(MI_RS.VALOR_BASE, 0)||'
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

             SELECT CUENTA_PPTAL
             INTO MI_CTAPPTAL
             FROM PLAN_CONTABLE          
             WHERE COMPANIA = UN_COMPANIA
               AND ANO      = MI_ANIO
               AND CODIGO   = MI_CREDITO;

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
                              ,'||MI_RS.VALOR_BASE||'
                              ,0
                              ,'||NVL(MI_RS.VALOR_BASE, 0)||'
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
           IF MI_CLASECONTABLE IN ('V','I') THEN
              MI_DEBITO := NVL(MI_RS.CUENTADEBITOIVA, '');
              MI_CREDITO := NVL(MI_RS.CUENTACREDITOIVA, '');
           ELSE
              MI_CREDITO := NVL(MI_RS.CUENTADEBITOIVA, '');
              MI_DEBITO := NVL(MI_RS.CUENTACREDITOIVA, '');
           END IF;     

           IF MI_DEBITO IS NOT NULL THEN

             MI_CTAPPTAL := '';
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

             SELECT CUENTA_PPTAL
             INTO MI_CTAPPTAL
             FROM PLAN_CONTABLE          
             WHERE COMPANIA = UN_COMPANIA
               AND ANO      = MI_ANIO
               AND CODIGO   = MI_CREDITO;

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
     --*************   REGISTRO DEL VALOR RETEFUENTE SI INDICADOR SE ENCUENTRA ACTIVO   ******************* 

  IF MI_RS.APLICARETEFUENTE NOT IN (0) THEN
    IF NVL(MI_RS.VALOR_RETEFUENTE, 0) <> 0 THEN
       --***************** DEBITO *******************
       IF MI_CLASECONTABLE IN ('V','I') THEN
          MI_DEBITO := NVL(MI_RS.CUENTADEBITORETE, '');
          MI_CREDITO := NVL(MI_RS.CUENTACREDITORETE, '');
        ELSE
          MI_CREDITO := NVL(MI_RS.CUENTADEBITORETE, '');
          MI_DEBITO := NVL(MI_RS.CUENTACREDITORETE,'');
        END IF;

      IF MI_DEBITO IS NOT NULL THEN

           MI_CTAPPTAL := '';
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

         SELECT CUENTA_PPTAL
             INTO MI_CTAPPTAL
             FROM PLAN_CONTABLE          
             WHERE COMPANIA = UN_COMPANIA
               AND ANO      = MI_ANIO
               AND CODIGO   = MI_CREDITO;

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
        IF MI_CLASECONTABLE IN ('V','I') THEN
            MI_DEBITO := NVL(MI_RS.CUENTADEBITODESCUENTO, '');
            MI_CREDITO := NVL(MI_RS.CUENTACREDITODESCUENTO, '');
        ELSE
            MI_CREDITO := NVL(MI_RS.CUENTADEBITODESCUENTO, '');
            MI_DEBITO := NVL(MI_RS.CUENTACREDITODESCUENTO, '');
        END IF;

        IF MI_DEBITO IS NOT NULL THEN

           MI_CTAPPTAL := '';               
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

         SELECT CUENTA_PPTAL
             INTO MI_CTAPPTAL
             FROM PLAN_CONTABLE          
             WHERE COMPANIA = UN_COMPANIA
               AND ANO      = MI_ANIO
               AND CODIGO   = MI_CREDITO;

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
        IF MI_CLASECONTABLE IN ('V','I') THEN
          MI_DEBITO := NVL(MI_RS.CUENTADEBITOICA, '');
          MI_CREDITO := NVL(MI_RS.CUENTACREDITOICA, '');
        ELSE
          MI_CREDITO := NVL(MI_RS.CUENTADEBITOICA, '');
          MI_DEBITO := NVL(MI_RS.CUENTACREDITOICA, '');
        END IF;

       IF MI_DEBITO IS NOT NULL THEN

           MI_CTAPPTAL := '';               
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

         SELECT CUENTA_PPTAL
             INTO MI_CTAPPTAL
             FROM PLAN_CONTABLE          
             WHERE COMPANIA = UN_COMPANIA
               AND ANO      = MI_ANIO
               AND CODIGO   = MI_CREDITO;

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


   IF  NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                    UN_NOMBRE 	 => 'SF MANEJA CALCULO CREE',
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
                            SF_DETALLE_FACTURA.REFERENCIA
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
                                    UN_NOMBRE 	 => 'SF NRO FACTURA MANUAL COMO NRO COMPROBANTE',
                                    UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                    UN_FECHA_PAR => SYSDATE,
                                    UN_IND_MAYUS =>0),'NO') = 'SI' THEN

             MI_NROCPTE := MI_RS2.NUMERO_FACTURA;

          ELSE   

             MI_NROCPTE := UN_NUMERO;                      

          END IF; 

          IF MI_CLASECONTABLE IN ('V','I') THEN
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
                         , BASE_GRAVABLE'; 

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

      ELSE  

         CONTINUE;
         MI_CONSC := TRUE; 

      END IF;

    END LOOP;   

   END IF;


   END LOOP;

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
                                              ,UN_NONETEA          => -1
                                              ,UN_CONTRATISTA      => 0
                                              ,UN_TEXTO            => 'Contabilizar'
                                              ,UN_CONTRATO         => 0
                                              ,UN_TIPOCONTRATO     => ''
                                              ,UN_NRO_DOCUMENTO    => ''
                                              ,UN_ALMDEP           => 0
                                              ,UN_TERCE            => 0
                                              ,UN_RESAUXGEN        => 0
                                              ,UN_USUARIO          => UN_USUARIO);

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

         BEGIN 
               BEGIN 

                  MI_CAMPOS := 'ANO_CPTE    = '||MI_ANIO||',
                                TIPO_CPTE   = '''||UN_TIPO||''',
                                NRO_CPTE    = '||MI_NUMERO||',
                                INTERFAZADA = -1';

                  MI_CONDICION := 'COMPANIA       = '''||UN_COMPANIA||'''
                               AND TIPO_FACTURA   = '''||UN_TIPO||'''
                               AND NUMERO_FACTURA = '||MI_NUMERO_FACTURA||'
                               AND CODIGO_COBRO   = '||MI_NUMERO;

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

                MI_CAMPOS := 'CPTE_CTBLE    = '''||UN_TIPO||''',
                              NROCPTE_CTBLE    = '||MI_NUMERO||' ';


                MI_CONDICION := ' COMPANIA     = '''||UN_COMPANIA||'''
                              AND TIPOCOBRO    = '''||UN_TIPO||'''
                              AND CODIGO_COBRO = '||MI_NUMERO||'
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

 --Crear comprobante pptal

   PCK_FACT_GENERAL_COM3.PR_CREARCOMPROBANTEPPTAL(UN_COMPANIA        => UN_COMPANIA,
                                                      UN_TIPOCPTE        => UN_TIPO,
                                                      UN_ANIO            => MI_ANIO,
                                                      UN_NUMERO          => MI_NUMERO,
                                                      UN_NUMERO_AFEC     => MI_NUMERO, 
                                                      UN_TIPO_PTE_AFEC   => UN_TIPO, 
                                                      UN_DESCRIPCION     => UN_DESCRIPCION,
                                                      UN_USUARIO         => UN_USUARIO );

        MI_RPTA := -1;

         --Actualización indicador de recaudo    
           BEGIN
                BEGIN

                    MI_CAMPOS    := 'INDPAGO      = -1,
                                    CPTE_CTBLE    = '''||UN_TIPO||''',
                                    NROCPTE_CTBLE ='||MI_NUMERO;

                    MI_CONDICION := ' COMPANIA     = '''||UN_COMPANIA||'''
                                  AND TIPOCOBRO    = '''||UN_TIPO||'''
                                  AND CODIGO_COBRO = '||UN_NUMERO;

                   PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'SF_OBJETO_COBRO',
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

                    MI_CAMPOS    := 'ANOCPTE_PAGO   = '||MI_ANIO||',
                                     TIPOCPTE_PAGO  = '''||UN_TIPO||''' ,
                                     NROCPTE_PAGO   = '||MI_NUMERO||',
                                     FECHA_PAGO     = '''||UN_FECHA||'''';

                    MI_CONDICION := ' COMPANIA            ='''||UN_COMPANIA||'''
                                       AND TIPO_CPTE      ='''||UN_TIPO||'''
                                       AND NUMERO_FACTURA = '||MI_NUMERO_FACTURA||'
                                       AND ANO            = '||MI_ANIO;

                       PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'SF_FACTURA',
                                           UN_ACCION    => 'M',
                                           UN_CAMPOS    => MI_CAMPOS,
                                           UN_CONDICION => MI_CONDICION);

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION;													
                END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 

                 MI_MSGERROR(1).CLAVE := 'FACTURA';
                 MI_MSGERROR(1).VALOR := MI_NUMERO_FACTURA;

              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,                                      
                                      UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTFACTURA
                                      ,UN_REEMPLAZOS  => MI_MSGERROR); 
            END;  


  RETURN MI_RPTA;

END FC_INTERFAZCONTABLENOFACT;

--2

PROCEDURE PR_REVERSARFACTURACAUSADA
  /*
    NAME              : PR_REVERSARFACTURACAUSADA -- ReversarFacturacion() en Access.
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : Elkin Geovanny Amaya Silva
    DATE              : 20/11/2017
    TIME              : 08:23 AM
    SOURCE MODULE     : SysmanSF2018.07.06
    TIME              :
    DESCRIPTION       : Proceso para reversar las facturas.
    PARAMETERS        : UN_COMPANIA  => Compañia de ingreso a la aplicación
                        UN_ANIO      => Año seleccionado al ingresar al módulo.
                        UN_TIPOCOBRO => Tipo de cobro seleccionado al ingresar al módulo.
                        UN_FACTURA   => Número de factura seleccionada que se desea reversar.
                        UN_CODCOBRO  => Número de cobro de la factura a reversar.
                        UN_USUARIO   => Usuario que realiza el registro.

    @NAME:  reversarFacturaCausada
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
    MI_INDDIFERIDA      PCK_SUBTIPOS.TI_LOGICO;
    MI_NUM_INTREC       PCK_SUBTIPOS.TI_ENTERO;
    MI_CONT_FACT        PCK_SUBTIPOS.TI_ENTERO;
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
    MI_RTAACME          PCK_SUBTIPOS.TI_RTA_ACME;
    MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
    MI_PARAMETRO        VARCHAR2(10 CHAR);
    MI_CONTEO           PCK_SUBTIPOS.TI_ENTERO;
    MI_FECHA_SOLICITUD  SF_OBJETO_COBRO.FECHA_SOLICITUD%TYPE;
BEGIN

   BEGIN
      SELECT DIFERIDA,
             FECHA_SOLICITUD
        INTO MI_INDDIFERIDA,
             MI_FECHA_SOLICITUD
        FROM SF_OBJETO_COBRO
       WHERE COMPANIA     = UN_COMPANIA
         AND ANO          = UN_ANIO
         AND TIPOCOBRO    = UN_TIPOCOBRO
         AND CODIGO_COBRO = UN_CODCOBRO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_INDDIFERIDA := 0;
    END;
  BEGIN
    IF MI_INDDIFERIDA NOT IN (0)
      THEN
        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
          MI_MSGERROR(1).CLAVE := 'FACTURA';  
          MI_MSGERROR(1).VALOR := UN_FACTURA;
          PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_FACTREDIFERIDA,
              UN_REEMPLAZOS => MI_MSGERROR
          ); 
  END;     
   SELECT  COUNT(*) EXISTE
     INTO MI_CONTEO
   FROM   COMPROBANTE_CNT
   WHERE  COMPANIA = UN_COMPANIA
     AND  TIPO     = UN_TIPOCOBRO   
     AND  NUMERO   = UN_CODCOBRO;

  BEGIN
    IF MI_CONTEO IN (0)
      THEN
        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
          MI_MSGERROR(1).CLAVE := 'FACTURA';  
          MI_MSGERROR(1).VALOR := UN_FACTURA;
          PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_NOCAUSACION,
              UN_REEMPLAZOS => MI_MSGERROR
          ); 
  END;   BEGIN
        BEGIN
        MI_CONDICION :=   'COMPANIA         = ''' || UN_COMPANIA  ||''' 
                          AND ANO         = '   || UN_ANIO      ||' 
                          AND TIPO_CPTE   = ''' || UN_TIPOCOBRO ||'''   
                          AND COMPROBANTE = '   || UN_CODCOBRO   ||'';
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

  BEGIN      
       MI_CONDICION :=  'COMPANIA       = ''' || UN_COMPANIA      ||''' 
                          AND ANO       = '   || UN_ANIO  ||' 
                          AND TIPO      = ''' || UN_TIPOCOBRO ||'''   
                          AND NUMERO    = '   || UN_CODCOBRO  ||'';
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
     
        BEGIN
          BEGIN

            MI_CONDICION := 'COMPANIA    = ''' || UN_COMPANIA ||''' 
                        AND FACTURA      = ' || UN_FACTURA||'';

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
          BEGIN
            MI_CONDICION := 'COMPANIA       = ''' || UN_COMPANIA || '''
                         AND NUMERO_FACTURA = ' || UN_FACTURA ||'
                         AND CODIGO_COBRO   = '||UN_CODCOBRO||' ';

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
        
        END;
        BEGIN
            BEGIN
           MI_CAMPOS    := 'IMPRESO                   = 0,
                            INDREVERSO                = -1,
                            REVERSO_N_COMPROBANTE     = '||UN_CODCOBRO||'    ,
                            REVERSO_TIPO_COMPROBANTE  = '''||UN_TIPOCOBRO||''',
                            REVERSO_N_DOCUMENTO       = '||UN_FACTURA||',
                            REVERSO_FECHA_CREACION    = '''||MI_FECHA_SOLICITUD||''',
                            MODIFIED_BY               = ''' || UN_USUARIO || ''',
                            DATE_MODIFIED             = SYSDATE' ;
    
            MI_CONDICION := ' COMPANIA     = ''' || UN_COMPANIA ||''' 
                        AND TIPOCOBRO    = '''||UN_TIPOCOBRO ||'''
                        AND CODIGO_COBRO = '||UN_CODCOBRO;          
    
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'SF_OBJETO_COBRO',
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
END;

END PR_REVERSARFACTURACAUSADA;

--3

FUNCTION FC_ENUMERARABONO
 /*
    NAME              : FC_ENUMERARABONO -->En Acces()
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
    DATE MIGRADOR     : 06/11/2018
    TIME              : 16:00 PM
    SOURCE MODULE     : NominaP2015.04.02UNIFICADA.accdb
    MODIFIER          :                         
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : GENERA EL SIGUIENTE CONSECUTIVO DEL ABONO TENIENDO EN CUENTA EL ANIO ACTUAL 
    MODIFICATIONS     : 

    @NAME:  enumerarAbono
    @METHOD:  GET                     
  */
  (
    UN_COMPANIA  IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TIPOCOBRO IN PCK_SUBTIPOS.TI_TIPO_COBRO
  )
RETURN NUMBER
AS
  MI_STRSQL     PCK_SUBTIPOS.TI_STRSQL;
  MI_VLRETORNO  PCK_SUBTIPOS.TI_ENTERO_LARGO := 0;

BEGIN

  MI_STRSQL := 'SELECT MAX(CODIGO)
                FROM   SF_ABONOS 
                WHERE COMPANIA = '''||UN_COMPANIA||''' 
                  AND TIPO     = '''||UN_TIPOCOBRO||''' ';

  EXECUTE IMMEDIATE MI_STRSQL INTO MI_VLRETORNO;

  IF  MI_VLRETORNO IS NULL THEN
    MI_VLRETORNO := CONCAT(EXTRACT (YEAR FROM SYSDATE) ,'000000');
  ELSE
   MI_VLRETORNO := CONCAT(EXTRACT (YEAR FROM SYSDATE) , SUBSTR(MI_VLRETORNO,5,LENGTH(MI_VLRETORNO)));
  END IF;    
  RETURN NVL(MI_VLRETORNO,0) + 1;  

END FC_ENUMERARABONO;

--4

FUNCTION FC_CARGARINTFINANCIERAFRA
/*
      NAME              : FC_CARGARINTFINANCIERAFRA en Access => CargarIntFinanciacionFra
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 07/11/2018
      TIME              : 16:30 AM
      SOURCE MODULE     : SysmanSF2018.07.06
      DESCRIPTION       : 
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 

    @NAME:   cargarConceptoInteresesFactura
    @METHOD: POST
*/
(
  UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOFRA    IN SF_FACTURA.TIPO_FACTURA%TYPE,
  UN_NROFRA     IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_TIPOFINAN  IN SF_FACTURA.TIPO_FACTURA%TYPE,
  UN_NROFINAN   IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_TABLAFIN   IN PCK_SUBTIPOS.TI_TABLA,
  UN_USUARIO    IN PCK_SUBTIPOS.TI_USUARIO
)RETURN CLOB AS

  MI_CIFINANCIACION PCK_SUBTIPOS.TI_PARAMETRO;
  MI_STRSQL         PCK_SUBTIPOS.TI_STRSQL ;
  MI_CONTEO         PCK_SUBTIPOS.TI_ENTERO ;
  MI_RS             SYS_REFCURSOR;
  MI_RSTINTFIN      PCK_SUBTIPOS.TI_DOBLE ;
  MI_RSTIVAINTFIN   PCK_SUBTIPOS.TI_DOBLE ;
  MI_CENTROCOSTO    SF_CONCEPTOS.CENTRO_COSTO%TYPE;
  MI_AUXILIAR       SF_CONCEPTOS.AUXILIAR%TYPE;
  MI_ESTADO         PCK_SUBTIPOS.TI_LOGICO:=0;
  MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS ;
  MI_VALORES        PCK_SUBTIPOS.TI_VALORES ;
  MI_CUENTABANCO    SF_CONCEPTOS.CUENTA_RECAUDO%TYPE;
  MI_CLOB           CLOB:='';    

BEGIN


  MI_CIFINANCIACION :=NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   =>UN_COMPANIA 	  ,
                                                  UN_NOMBRE     =>'SF CONCEPTO DE INTERES - ACUERDO DE PAGO',
                                                  UN_MODULO     =>PCK_DATOS.FC_MODULOFACTURACIONGENERAL(),
                                                  UN_FECHA_PAR  =>SYSDATE,
                                                  UN_IND_MAYUS  =>0),'SIN CONFIGURAR');

  MI_STRSQL := 'SELECT SUM(TABLAFIN.VALOR_BASE) TINTFIN
                      ,SUM(TABLAFIN.VALOR_IVA)  TIVAINTFIN  
               FROM  '||UN_TABLAFIN||'   TABLAFIN 
              WHERE  TABLAFIN.COMPANIA     = '''||UN_COMPANIA||''' 
                AND  TABLAFIN.TIPO_ABONO   = '''||UN_TIPOFINAN||''' 
                AND  TABLAFIN.CODIGO_ABONO = '||UN_NROFINAN||'
                AND  TABLAFIN.CONCEPTO     = '''||MI_CIFINANCIACION||''' 
              GROUP BY  TABLAFIN.COMPANIA, 
                        TABLAFIN.TIPO_ABONO,
                        TABLAFIN.CODIGO_ABONO, 
                        TABLAFIN.CONCEPTO  ';

    EXECUTE IMMEDIATE 'SELECT COUNT(1) FROM ('||MI_STRSQL||')' INTO MI_CONTEO;                         

       IF MI_CONTEO >=1 THEN           
            OPEN MI_RS FOR MI_STRSQL;
              LOOP                    

                FETCH MI_RS INTO MI_RSTINTFIN,MI_RSTIVAINTFIN; 
                  EXIT WHEN MI_RS%NOTFOUND;

                 IF MI_RSTINTFIN > 0 THEN
                  MI_ESTADO := 0;
                  BEGIN
                    BEGIN
                      SELECT CENTRO_COSTO, AUXILIAR ,CUENTA_RECAUDO
                      INTO MI_CENTROCOSTO,MI_AUXILIAR , MI_CUENTABANCO
                       FROM SF_CONCEPTOS
                      WHERE COMPANIA  = UN_COMPANIA
                        AND ANO       = EXTRACT(YEAR FROM SYSDATE)
                        AND TIPOCOBRO = UN_TIPOFRA
                        AND CODIGO    = MI_CIFINANCIACION;

                     EXCEPTION WHEN NO_DATA_FOUND THEN
                      MI_ESTADO := 1;
                      RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;         

                    END;

                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                           PCK_ERR_MSG.RAISE_WITH_MSG(
                             UN_EXC_COD    => SQLCODE,
                             UN_TABLAERROR => 'SF_CONCEPTOS',
                             UN_ERROR_COD  => PCK_ERRORES.ERR_CONCINTFINANOTFOUND
                            );          
                  END;       

                     BEGIN 
                        BEGIN 

                            MI_CAMPOS  := 'COMPANIA, ANO, TIPO_FACTURA, FACTURA, TIPOCOBRO, CONCEPTO, FECHA_INICIO_COBRO, FECHA_FIN_COBRO,
                                          TERCERO, SUCURSAL, CENTRO_COSTO, AUXILIAR, VALOR_BASE,VALOR_IVA,  VALOR_COMPRA, VALOR_UTILIDAD, VALOR_NETO, 
                                          VALOR_UNITARIO, ACOMETIDA_SERVICIO, AIU, ADMON_IMPREVISTOS, UTILIDAD_AIU, IVA_UTILIDAD,CUENTA_BANCO, CREATED_BY, DATE_CREATED, CANTIDAD';

                            MI_VALORES := 'SELECT COMPANIA,
                                            MAX(ANO) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) ANO,
                                            TIPO_FACTURA,
                                            FACTURA,
                                            MAX(TIPOCOBRO) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) TIPOCOBRO,
                                            '''||MI_CIFINANCIACION||''',
                                            MAX(FECHA_INICIO_COBRO) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) FECHA_INICIO_COBRO,
                                            MAX(FECHA_FIN_COBRO) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) FECHA_FIN_COBRO,
                                            MAX(TERCERO) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) TERCERO,
                                            MAX(SUCURSAL) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) SUCURSAL,
                                            '''||MI_CENTROCOSTO||''',
                                            '''||MI_AUXILIAR||''',
                                            '||MI_RSTINTFIN||',
                                            '||MI_RSTIVAINTFIN||',
                                             '||MI_RSTINTFIN||',
                                             0,
                                            '||TO_CHAR(MI_RSTINTFIN + MI_RSTIVAINTFIN)||',
                                            '||MI_RSTINTFIN||',
                                            0,
                                            0,
                                            0,
                                            0,
                                            0,
                                            '''||MI_CUENTABANCO||''',
                                            '''||UN_USUARIO||''',
                                            SYSDATE,
                                            1
                                       FROM SF_DETALLE_FACTURA
                                       WHERE COMPANIA     = '''||UN_COMPANIA||'''
                                         AND TIPO_FACTURA = '''||UN_TIPOFRA||'''
                                         AND FACTURA      = '||UN_NROFRA||'
                                       GROUP BY COMPANIA,
                                                TIPO_FACTURA,
                                                FACTURA,
                                                '''||MI_CIFINANCIACION||''',
                                                '''||MI_CENTROCOSTO||''',
                                                '''||MI_AUXILIAR||''',
                                                '||MI_RSTINTFIN||',
                                                '||MI_RSTIVAINTFIN||',
                                                '||MI_RSTINTFIN||',
                                                0,
                                                '||TO_CHAR(MI_RSTINTFIN + MI_RSTIVAINTFIN)||',
                                                '||MI_RSTINTFIN||',
                                                0,
                                                0,
                                                0,
                                                0,
                                                0,
                                                '''||MI_CUENTABANCO||''',
                                                '''||UN_USUARIO||''',
                                                SYSDATE';

                            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => 'SF_DETALLE_FACTURA',
                                                       UN_ACCION   => 'IS',
                                                       UN_CAMPOS   => MI_CAMPOS,
                                                       UN_VALORES  => MI_VALORES);

                           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                           RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                         END;
                             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
                                 PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,                                                           
                                                            UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_REGISTRAR_FACTURA);  
                       END;                     

                 ELSE 

                   BEGIN
                   RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
                                 PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,                                                           
                                                            UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INTERESNEGATIVO);  

                   END;     

                 END IF;

          END LOOP;

       ELSE
          MI_CLOB := PCK_ERR_MSG.FC_MENSAJE(UN_MENSAJE_COD =>PCK_ERRORES.ERR_SYSMANSF_NO_EXITE_ABONO);

       END IF;  

   RETURN MI_CLOB;

END FC_CARGARINTFINANCIERAFRA;

--5

PROCEDURE PR_REGISTRAMOVIMIENTO
    /*
      NAME              : PR_REGISTRAMOVIMIENTO En Access --> RegistraMovimiento
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 13/11/2018
      TIME              : 11:50 AM
      MODIFIER          : 
      DATE MODIFIER     : 
      TIME              : 
      SOURCE MODULE     : 
      DESCRIPTION       : 
      PARAMETERS        : 
      MODIFICATIONS     : 

      @NAME  : registrarMovimiento                          
    */
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TIPOMOV      IN MOVIMIENTO.TIPOMOVIMIENTO%TYPE,
    UN_NROMOV       IN MOVIMIENTO.NUMERO%TYPE, 
    UN_FECHA        IN DATE,
    UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_ACTPROMEDIO        VARCHAR(5 CHAR);
    MI_STRKARDEAR         VARCHAR(5 CHAR);
    MI_STRKARDEARSAL      VARCHAR(5 CHAR);  
    MI_REGISTRADO         PCK_SUBTIPOS.TI_LOGICO;
    MI_RSDMOV             SYS_REFCURSOR;  
    MI_RSELEMENTO         D_MOVIMIENTO.ELEMENTO%TYPE;  
    MI_RSTIPOMOVIMIENTO   D_MOVIMIENTO.TIPOMOVIMIENTO%TYPE;  
    MI_RSMOVIMIENTO       D_MOVIMIENTO.MOVIMIENTO%TYPE;  
    MI_RSCODIGO           D_MOVIMIENTO.CODIGO%TYPE;  
    MI_CONTEO             PCK_SUBTIPOS.TI_ENTERO; 
    MI_STRSQL             PCK_SUBTIPOS.TI_STRSQL; 
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS; 
    MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION; 
    MI_VALORES            PCK_SUBTIPOS.TI_VALORES; 
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;    
    MI_RTA                CLOB;      

BEGIN

    SELECT REGISTRADO 
      INTO MI_REGISTRADO
     FROM  MOVIMIENTO 
     WHERE COMPANIA       = UN_COMPANIA
       AND TIPOMOVIMIENTO = UN_TIPOMOV
       AND NUMERO         = UN_NROMOV;

   EXCEPTION WHEN NO_DATA_FOUND THEN
       BEGIN
         RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;

         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                 MI_MSGERROR(1).CLAVE := 'NUMERO';
                 MI_MSGERROR(1).VALOR := UN_NROMOV;
                 MI_MSGERROR(2).CLAVE := 'TIPO';
                 MI_MSGERROR(2).VALOR := UN_TIPOMOV;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                               UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_NO_MOVIMIENTO);
       END;   

  IF MI_REGISTRADO NOT IN (0)THEN
      BEGIN
         RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;

         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                 MI_MSGERROR(1).CLAVE := 'NUMERO';
                 MI_MSGERROR(1).VALOR := UN_NROMOV;
                 MI_MSGERROR(2).CLAVE := 'TIPO';
                 MI_MSGERROR(2).VALOR := UN_TIPOMOV;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                               UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_MOVIMIENTO_REGIS);
      END;   

  END IF;

  <<DETALLES_MOVIMIENTO>>
  MI_STRSQL := 'SELECT  TIPOMOVIMIENTO
                        ,MOVIMIENTO
                        ,CODIGO
                        ,ELEMENTO 
                FROM  D_MOVIMIENTO 
                WHERE COMPANIA       = '''||UN_COMPANIA||''' 
                  AND TIPOMOVIMIENTO = '''||UN_TIPOMOV||''' 
                  AND MOVIMIENTO '||UN_NROMOV||' ';

      EXECUTE IMMEDIATE 'SELECT COUNT(1) FROM ('||MI_STRSQL||')' INTO MI_CONTEO;     

     IF MI_CONTEO >=1 THEN
       OPEN MI_RSDMOV FOR MI_STRSQL;
          LOOP
             EXIT WHEN MI_RSDMOV%NOTFOUND;
             FETCH MI_RSDMOV INTO MI_RSTIPOMOVIMIENTO,MI_RSMOVIMIENTO,MI_RSCODIGO,MI_RSELEMENTO; 

             MI_CAMPOS := 'IND_REG       = -1
                          ,MODIFIED_BY   = '''||UN_USUARIO||''' 
                          ,DATE_MODIFIED = SYSDATE';


             MI_CONDICION := ' COMPANIA       = '''||UN_COMPANIA||''' 
                           AND TIPOMOVIMIENTO = '''||UN_TIPOMOV||''' 
                           AND MOVIMIENTO     = '||UN_NROMOV||' ';   

              BEGIN
                 BEGIN
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'D_MOVIMIENTO'
                                                           ,UN_ACCION    => 'M'
                                                           ,UN_CAMPOS    => MI_CAMPOS
                                                           ,UN_CONDICION => MI_CONDICION);


                         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                  RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;                                   

                 END;

                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
                               MI_MSGERROR(1).CLAVE := 'NUMERO';
                               MI_MSGERROR(1).VALOR := UN_NROMOV;
                               MI_MSGERROR(2).CLAVE := 'TIPO';
                               MI_MSGERROR(2).VALOR := UN_TIPOMOV;
                                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                                    UN_EXC_COD =>SQLCODE
                                                    ,UN_ERROR_COD=>PCK_ERRORES.ER_DETALLE_KARMOVIMIENTO);

              END;             


            IF MI_STRKARDEARSAL = 'NO' THEN
                MI_RTA := PCK_ALMACEN_COM3.FC_KARDEXELEMENTO(UN_COMPANIA       => UN_COMPANIA
                                               ,UN_TIPOMOVIMIENTO => MI_RSTIPOMOVIMIENTO
                                               ,UN_MOVIMIENTO     => MI_RSMOVIMIENTO
                                               ,UN_ELEMENTO       => MI_RSELEMENTO
                                               ,UN_CODIGO         => MI_RSCODIGO
                                               ,UN_ACTPROMEDIO    => MI_ACTPROMEDIO
                                               ,UN_ANO            => NULL
                                               ,UN_MES            => NULL);
            ELSE
               MI_RTA := PCK_ALMACEN_COM3.FC_KARDEXELEMENTOTODOSHALM( UN_COMPANIA           => UN_COMPANIA
                                                           ,UN_INTANOINICIAL      => EXTRACT(YEAR FROM UN_FECHA)
                                                           ,UN_INTMESINICIAL      => EXTRACT(MONTH FROM UN_FECHA)
                                                           ,UN_INTANOFINAL        => NULL
                                                           ,UN_INTMESFINAL        => NULL
                                                           ,UN_STRELEMENTOINICIAL => MI_RSELEMENTO
                                                           ,UN_STRELEMENTOFINAL   => MI_RSELEMENTO
                                                           ,UN_KARDEXGENERAL      => -1
														   ,UN_FECHAINICIAL       => SYSDATE
                                                           ,UN_FECHAFINAL         => SYSDATE
                                                           ,UN_TIPOMOVIMIENTO     => ''
                                                           ,UN_MOVIMIENTO         => 0);
            END IF;


             MI_CAMPOS := 'REGISTRADO = -1
                          ,MODIFIED_BY   = '''||UN_USUARIO||''' 
                          ,DATE_MODIFIED = SYSDATE';

             MI_CONDICION := ' COMPANIA       = '''||UN_COMPANIA||''' 
                           AND TIPOMOVIMIENTO = '''||UN_TIPOMOV||''' 
                           AND MOVIMIENTO     = '||UN_NROMOV||' ';   

              BEGIN
                 BEGIN
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'MOVIMIENTO'
                                                           ,UN_ACCION    => 'M'
                                                           ,UN_CAMPOS    => MI_CAMPOS
                                                           ,UN_CONDICION => MI_CONDICION);


                         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                  RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;                                   

                 END;

                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
                               MI_MSGERROR(1).CLAVE := 'NUMERO';
                               MI_MSGERROR(1).VALOR := UN_NROMOV;
                               MI_MSGERROR(2).CLAVE := 'TIPO';
                               MI_MSGERROR(2).VALOR := UN_TIPOMOV;
                                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                                    UN_EXC_COD =>SQLCODE
                                                    ,UN_ERROR_COD=>PCK_ERRORES.ER_DETALLE_KARMOVIMIENTO);

              END;              

          END LOOP;    

     ELSE

       BEGIN
         RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;

         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                 MI_MSGERROR(1).CLAVE := 'NUMERO';
                 MI_MSGERROR(1).VALOR := UN_NROMOV;
                 MI_MSGERROR(2).CLAVE := 'TIPO';
                 MI_MSGERROR(2).VALOR := UN_TIPOMOV;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                               UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_NO_DETALLE);
       END;       

     END IF;


END  PR_REGISTRAMOVIMIENTO;

PROCEDURE PR_INTERFAZCONTABLERENTASWS
/*
      NAME              :
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
      DATE MIGRADOR     : 08/02/2019
      TIME              : 10:37 AM
      SOURCE MODULE     :
      DESCRIPTION       :
      MODIFIER          :
      DATE MODIFIED     :
      TIME MODIFIED     :
      MODIFICATIONS     :


    @NAME:   interfazRentasWs
    @METHOD: GET
*/
(
  UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPO             IN PCK_SUBTIPOS.TI_TIPO_COBRO,
  UN_NUMERO           IN DETALLE_COMPROBANTE_CNT.COMPROBANTE%TYPE,
  UN_FECHA            IN VARCHAR2,
  UN_TERCERO          IN SF_DETALLE_FACTURA.TERCERO%TYPE,
  UN_SUCURSAL         IN SF_DETALLE_FACTURA.SUCURSAL%TYPE,
  UN_DESCRIPCION      IN COMPROBANTE_CNT.DESCRIPCION%TYPE,
  UN_MANEJAINVENTARIO IN PCK_SUBTIPOS.TI_LOGICO,
  UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
)
AS
    MI_RTA  PCK_SUBTIPOS.TI_LOGICO;

BEGIN
    MI_RTA := PCK_FACT_GENERAL_COM3.FC_INTERFAZCONTABLE(
             UN_COMPANIA            =>  UN_COMPANIA
            ,UN_TIPO                =>  UN_TIPO
            ,UN_NUMERO              =>  UN_NUMERO
            ,UN_FECHA               =>  TO_DATE(UN_FECHA, 'YYYY/MM/DD')
            ,UN_TERCERO             =>  UN_TERCERO
            ,UN_SUCURSAL            =>  UN_SUCURSAL
            ,UN_DESCRIPCION         =>  UN_DESCRIPCION
            ,UN_MANEJAINVENTARIO    =>  UN_MANEJAINVENTARIO
            ,UN_USUARIO             =>  UN_USUARIO );
END PR_INTERFAZCONTABLERENTASWS;

PROCEDURE PR_RECAUDARFACTURAWS
/*
      NAME              : PR_RECAUDARFACTURAWS
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
      DATE MIGRADOR     : 15/04/2019
      TIME              : 11:26 AM
      SOURCE MODULE     :
      DESCRIPTION       : Procedimiento para realizar recaudo desde el web Service de rentas caqueta.
      MODIFIER          :
      DATE MODIFIED     :
      TIME MODIFIED     :
      MODIFICATIONS     :

    @NAME:   recaudarFacturaWs
    @METHOD: PUT
*/
(
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TIPOFACTURA    IN SF_OBJETO_COBRO.TIPOCOBRO%TYPE,
    UN_NUMEROFACTURA  IN SF_OBJETO_COBRO.NRO_FACTURA%TYPE,
    UN_OBSERVACION    IN SF_FACTURA.OBSERVACIONES%TYPE,
    UN_CUENTA         IN DETALLE_COMPROBANTE_CNT.CUENTA%TYPE,
    UN_FECHA          IN VARCHAR2,
    UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
    UN_ANOFACTURA     IN PCK_SUBTIPOS.TI_ANIO,
    UN_VALORRECAUDO   IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0,
    UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
)
AS
    MI_RTA  VARCHAR2(500 CHAR);
BEGIN
    MI_RTA := PCK_FACT_GENERAL_COM3.FC_RECAUDARFACTURA
        (UN_COMPANIA       => UN_COMPANIA
        ,UN_TIPOFACTURA    => UN_TIPOFACTURA
        ,UN_NUMEROFACTURA  => UN_NUMEROFACTURA
        ,UN_OBSERVACION    => UN_OBSERVACION
        ,UN_CUENTA         => UN_CUENTA
        ,UN_FECHA          => TO_DATE(UN_FECHA, 'YYYY/MM/DD')
        ,UN_ANIO           => UN_ANIO
        ,UN_DIFERIDA       => 0
        ,UN_USUARIO        => UN_USUARIO
        ,UN_VALORRECAUDO   => UN_VALORRECAUDO);


END PR_RECAUDARFACTURAWS;


PROCEDURE PR_ELIMINARFACTDIFERIDA 
/*
      AUTHORS           : SYSMAN  SAS
      NAME              : PR_ELIMINARFACTDIFERIDA 
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 31/05/2018
      TIME              : 10:30 AM
      SOURCE MODULE     : 
      DESCRIPTION       : 
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 

    @NAME:   eliminarFacturaDiferida
    @METHOD: POST
*/
(
UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
UN_TIPOABONO     IN SF_ABONOS.TIPO%TYPE,
UN_ABONO         IN SF_ABONOS.CODIGO%TYPE,
UN_NUMEROFACTURA IN SF_FACTURA.NUMERO_FACTURA%TYPE,
UN_ANIO          IN SF_FACTURA.ANO%TYPE,   
UN_USUARIO       IN PCK_SUBTIPOS.TI_USUARIO
)
AS 
MI_CAMPOS      PCK_SUBTIPOS.TI_CAMPOS;
MI_CONDICION   PCK_SUBTIPOS.TI_CONDICION;
MI_VALORES     PCK_SUBTIPOS.TI_VALORES;
MI_MSGERROR    PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN

 BEGIN
  BEGIN  
       MI_CONDICION := ' COMPANIA = '''||UN_COMPANIA|| '''
                   AND TIPO_ABONO = '''||UN_TIPOABONO||'''
                 AND CODIGO_ABONO = '||UN_ABONO||'';


       PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_ACCION    => 'E'
                                         ,UN_TABLA     => 'SF_DETALLE_CUOTAABONO'
                                         ,UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
     END;

         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
            MI_MSGERROR(1).CLAVE := 'ABONO';
            MI_MSGERROR(1).VALOR := UN_ABONO;
            MI_MSGERROR(2).CLAVE := 'TIPO';
            MI_MSGERROR(2).VALOR := UN_TIPOABONO;
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_ELIMIDETCUOTAABONO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);    
   END;  


    BEGIN
        BEGIN

       MI_CONDICION := ' COMPANIA = '''||UN_COMPANIA|| '''
                   AND TIPO_ABONO = '''||UN_TIPOABONO||'''
                 AND CODIGO_ABONO = '||UN_ABONO||'';


       PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_ACCION    => 'E'
                                         ,UN_TABLA     => 'SF_DETALLE_ABONO'
                                         ,UN_CONDICION => MI_CONDICION);     

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
     END;     
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
            MI_MSGERROR(1).CLAVE := 'ABONO';
            MI_MSGERROR(1).VALOR := UN_ABONO;
            MI_MSGERROR(2).CLAVE := 'TIPO';
            MI_MSGERROR(2).VALOR := UN_TIPOABONO;         
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_ELIMIDETALLEABONO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);      
   END;                                     

   BEGIN
    BEGIN 
       MI_CONDICION := ' COMPANIA = '''||UN_COMPANIA|| '''
                         AND TIPO = '''||UN_TIPOABONO||'''
                       AND CODIGO = '||UN_ABONO||'';


       PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_ACCION    => 'E'
                                         ,UN_TABLA     => 'SF_ABONOS'
                                         ,UN_CONDICION => MI_CONDICION);   

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
     END;

         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
            MI_MSGERROR(1).CLAVE := 'ABONO';
            MI_MSGERROR(1).VALOR := UN_ABONO;
            MI_MSGERROR(2).CLAVE := 'TIPO';
            MI_MSGERROR(2).VALOR := UN_TIPOABONO;         
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_ELIMIABONO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);       
   END;                                         

   BEGIN 
        BEGIN

            MI_CAMPOS := 'DIFERIDA  = 0
                        ,TIPO_ABONO = NULL
                        ,NRO_ABONO  = NULL
                     ,MODIFIED_BY   ='''||UN_USUARIO||'''
                     ,DATE_MODIFIED = SYSDATE';

            MI_CONDICION := ' COMPANIA            ='''||UN_COMPANIA||'''
                               AND TIPO_FACTURA   ='''||UN_TIPOABONO||'''
                               AND NUMERO_FACTURA = '||UN_NUMEROFACTURA||'
                               AND ANO            = '||UN_ANIO;            

            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'SF_FACTURA',
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

END PR_ELIMINARFACTDIFERIDA;

FUNCTION FC_INTERFAZCONTABLEABONO
/*
      NAME              : FC_INTERFAZCONTABLEABONO MIGRADO DE ACCESS InterfazContableRecaudo
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 11/06/2019
      TIME              : 02:54 PM
      SOURCE MODULE     : SysmanSF2019.01.04
      DESCRIPTION       :
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 


    @NAME:   manejarInterfazContableAbono
    @METHOD: GET
*/
(
  UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,    
  UN_TIPOCOBRO        IN PCK_SUBTIPOS.TI_TIPO_COBRO,
  UN_NUMERO           IN DETALLE_COMPROBANTE_CNT.COMPROBANTE%TYPE,
  UN_TIPOABONO        IN SF_ABONOS.TIPO%TYPE,
  UN_NUMEROABONO      IN SF_ABONOS.CODIGO%TYPE,  
  UN_CUOTABONO        IN SF_DETALLE_CUOTAABONO.CUOTA%TYPE,  
  UN_FECHA            IN DATE,
  UN_TERCERO          IN SF_DETALLE_FACTURA.TERCERO%TYPE,
  UN_SUCURSAL         IN SF_DETALLE_FACTURA.SUCURSAL%TYPE,  
  UN_MANEJAINVENTARIO IN PCK_SUBTIPOS.TI_LOGICO := 0,
  UN_CUENTA           IN DETALLE_COMPROBANTE_CNT.CUENTA%TYPE,
  UN_VLRABONO         IN PCK_SUBTIPOS.TI_DOBLE :=0,  
  UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN VARCHAR2 AS
  MI_RPTA                  VARCHAR2(32 CHAR); 
  MI_CLASECONTABLE         TIPO_COMPROBANTE.CLASE_CONTABLE%TYPE;
  MI_DESCRIPCION           COMPROBANTE_CNT.DESCRIPCION%TYPE;
  MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS; 
  MI_VALORES               PCK_SUBTIPOS.TI_VALORES;
  MI_DEBITO                VARCHAR2(32 CHAR); 
  MI_CREDITO               VARCHAR2(32 CHAR);
  MI_CONSC                 BOOLEAN;   
  MI_CONSECUTIVO           NUMBER(5,0); 
  MI_ANIO                  NUMBER(4,0);
  MI_ANIO_FAC              NUMBER(4,0);
  MI_MES                   NUMBER(4,0);  
  MI_NUMERO                DETALLE_COMPROBANTE_CNT.COMPROBANTE%TYPE;
  MI_PARMANEJATERCERO      VARCHAR(2 CHAR);
  MI_NATURALEZA            PCK_SUBTIPOS.TI_NATURALEZACONTA;  
  MI_STRTERCEROAUX         SF_DETALLE_FACTURA.TERCERO%TYPE;
  MI_STRSUCURSALAUX        SF_DETALLE_FACTURA.SUCURSAL%TYPE; 
  MI_FACTURA               SF_FACTURA.NUMERO_FACTURA%TYPE; 
  MI_CTAPPTAL              DETALLE_COMPROBANTE_CNT.CUENTAPPTAL%TYPE; 
  MI_CAUSARFACTRECAUDO     SF_TIPO_COBRO.INTERFAZ_RECAUDO%TYPE;
  MI_CPTCAUSADIFERIDA      PCK_SUBTIPOS.TI_TIPO_COBRO;
  MI_RTAINTERFAZ           PCK_SUBTIPOS.TI_RTA_ACME;
  MI_MSGERROR              PCK_SUBTIPOS.TI_CLAVEVALOR;  

BEGIN 

  MI_RPTA := -1;
  MI_CONSECUTIVO := 0;
  MI_ANIO := EXTRACT (YEAR FROM UN_FECHA);
  MI_MES := EXTRACT (MONTH FROM UN_FECHA);
  MI_CPTCAUSADIFERIDA := UN_TIPOCOBRO;
  MI_NUMERO := UN_NUMERO;

      SELECT INTERFAZ_RECAUDO
      INTO MI_CAUSARFACTRECAUDO
      FROM SF_TIPO_COBRO
     WHERE COMPANIA   = UN_COMPANIA
       AND ANO        = MI_ANIO
       AND CODIGO     = UN_TIPOCOBRO ;       

       SELECT NUMERO_FACTURA, ANO
       INTO MI_FACTURA, MI_ANIO_FAC  
       FROM SF_FACTURA
       WHERE COMPANIA = UN_COMPANIA
       AND TIPOCOBRO = UN_TIPOCOBRO
       AND CODIGO_COBRO = UN_NUMERO;

  MI_PARMANEJATERCERO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                    UN_NOMBRE 	 => 'SF MANEJA CONFIGURACION DE CTA POR TERCERO',
                                    UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                    UN_FECHA_PAR => SYSDATE,
                                    UN_IND_MAYUS =>0);

  SELECT CLASE_CONTABLE 
  INTO   MI_CLASECONTABLE
  FROM   TIPO_COMPROBANTE 
  WHERE  COMPANIA = UN_COMPANIA
    AND  CODIGO = UN_TIPOCOBRO;


  IF MI_CAUSARFACTRECAUDO NOT IN (0) THEN  

     SELECT CPTE_DIFERIDA
      INTO  MI_CPTCAUSADIFERIDA
      FROM SF_TIPO_COBRO
     WHERE COMPANIA   = UN_COMPANIA
       AND ANO        = MI_ANIO
       AND CODIGO     = UN_TIPOCOBRO ;

   MI_NUMERO := PCK_CONTABILIDAD_CPTE.FC_ENUMERARCOMPROBANTECNT(UN_COMPANIA     => UN_COMPANIA
                                                               ,UN_ANIO         => MI_ANIO
                                                               ,UN_TIPO         => MI_CPTCAUSADIFERIDA
                                                               ,UN_NUMERO       => 0
                                                               ,UN_CENTRO_COSTO => ''); 
  <<RECORRER_CUOTAS>>   
  FOR MI_RS IN(SELECT SF_ABONOS.COMPANIA,
                         SF_ABONOS.TIPO,
                         SF_ABONOS.CODIGO,
                         SF_ABONOS.NRO_FACT FACTURA,
                         SF_DETALLE_CUOTAABONO.CUOTA,
                         SF_DETALLE_CUOTAABONO.TOTAL_CUOTA,
                         SF_ABONOS.TERCERO        TERCEROH,
                         SF_ABONOS.SUCURSAL       SUCURSALH,
                         SF_ABONOS.CENTRO_COSTO   CENTRO_COSTOH,
                         SF_ABONOS.AUXILIAR       AUXILIARH,
                         SF_CONCEPTOS.CODIGO      CONCEPTO,
                         SF_DETALLE_CUOTAABONO.VALOR_BASE,
                         SF_CONCEPTOS.CUENTADEBITOBASE,
                         SF_CONCEPTOS.CUENTACREDITOBASE,
                         SF_CONCEPTOS.APLICAIVA,
                         SF_DETALLE_CUOTAABONO.VALOR_IVA,
                         SF_CONCEPTOS.CUENTADEBITOIVA,
                         SF_CONCEPTOS.CUENTACREDITOIVA,
                         SF_CONCEPTOS.APLICARETEFUENTE,
                         SF_DETALLE_CUOTAABONO.VALOR_RETE,
                         SF_CONCEPTOS.CUENTADEBITORETE,
                         SF_CONCEPTOS.CUENTACREDITORETE,
                         SF_CONCEPTOS.APLICADESCUENTO,
                         SF_DETALLE_CUOTAABONO.VALOR_DESCUENTO,
                         SF_CONCEPTOS.CUENTADEBITODESCUENTO,
                         SF_CONCEPTOS.CUENTACREDITODESCUENTO,
                         SF_CONCEPTOS.APLICAICA,
                         SF_CONCEPTOS.TIPODECONCEPTO,
                         SF_DETALLE_CUOTAABONO.VALOR_ICA,
                         SF_CONCEPTOS.CUENTADEBITOICA,
                         SF_CONCEPTOS.CUENTACREDITOICA,
                         SF_DETALLE_CUOTAABONO.VALOR_COMPRA,
                         SF_DETALLE_CUOTAABONO.VALOR_UTILIDAD,
                         SF_CONCEPTOS.CUENTADEBITOUTILIDAD,
                         SF_CONCEPTOS.REFERENCIA,
                         SF_CONCEPTOS.FUENTE_RECURSO,
                         SF_CONCEPTOS.CUENTACREDITOUTILIDAD,
                         SF_DETALLE_CUOTAABONO.TERCERO,
                         SF_DETALLE_CUOTAABONO.SUCURSAL,
                         SF_DETALLE_CUOTAABONO.CENTRO_COSTO,
                         SF_DETALLE_CUOTAABONO.AUXILIAR
                     FROM SF_DETALLE_CUOTAABONO
                       INNER JOIN SF_ABONOS ON SF_DETALLE_CUOTAABONO.COMPANIA       = SF_ABONOS.COMPANIA
                            AND SF_DETALLE_CUOTAABONO.TIPO_ABONO     = SF_ABONOS.TIPO
                            AND SF_DETALLE_CUOTAABONO.CODIGO_ABONO   = SF_ABONOS.CODIGO
                       INNER JOIN SF_CONCEPTOS ON SF_DETALLE_CUOTAABONO.COMPANIA   = SF_CONCEPTOS.COMPANIA
                            AND SF_DETALLE_CUOTAABONO.ANO        = SF_CONCEPTOS.ANO
                            AND SF_DETALLE_CUOTAABONO.CONCEPTO   = SF_CONCEPTOS.CODIGO
                            AND SF_DETALLE_CUOTAABONO.TIPO_ABONO = SF_CONCEPTOS.TIPOCOBRO  
                     WHERE SF_ABONOS.COMPANIA            = UN_COMPANIA
                       AND SF_ABONOS.TIPO                = UN_TIPOABONO
                       AND SF_ABONOS.CODIGO              = UN_NUMEROABONO
                       AND SF_DETALLE_CUOTAABONO.CUOTA   = UN_CUOTABONO
                       AND SF_ABONOS.TERCERO             = UN_TERCERO
                       AND SF_ABONOS.SUCURSAL            = UN_SUCURSAL  )LOOP      


           MI_FACTURA := MI_RS.FACTURA;

           MI_DESCRIPCION := 'Fra '||UN_TIPOCOBRO||' - '||MI_FACTURA||' Cuota '|| UN_CUOTABONO;

         IF MI_CLASECONTABLE IN ('V') THEN
           MI_DEBITO := NVL(MI_RS.CUENTADEBITOBASE, '');
           MI_CREDITO := NVL(MI_RS.CUENTACREDITOBASE, '');
         ELSE
           MI_CREDITO := NVL(MI_RS.CUENTADEBITOBASE, '');
           MI_DEBITO := NVL(MI_RS.CUENTACREDITOBASE, '');
         END IF        ;                    


         --***********************************INICIO INVENTARIO ********************************************
         --                       ***************** DEBITO *******************

     IF UN_MANEJAINVENTARIO NOT IN (0) THEN
        IF MI_DEBITO IS NOT NULL THEN

            MI_CTAPPTAL := '';            
            MI_CONSECUTIVO  :=  MI_CONSECUTIVO + 1 ;
            MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_DEBITO);

            IF MI_PARMANEJATERCERO  = 'SI' THEN

                  SELECT TERCERO, SUCURSAL 
                  INTO  MI_STRTERCEROAUX,
                        MI_STRSUCURSALAUX
                  FROM SF_CUENTAS_AUXTERCERO 
                  WHERE  COMPANIA = UN_COMPANIA 
                    AND ANO = MI_ANIO 
                    AND TIPOCOBRO = UN_TIPOCOBRO
                    AND CONCEPTO = MI_RS.CODIGO
                    AND CUENTA = MI_DEBITO ;

            ELSE
                MI_STRTERCEROAUX  := MI_RS.TERCEROH;
                MI_STRSUCURSALAUX := MI_RS.SUCURSALH;
            END IF;            

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
                          EJECUCION_DEBITO,
                          EJECUCION_CREDITO,
                          CENTRO_COSTO,
                          TERCERO,
                          SUCURSAL, 
                          AUXILIAR,
                          CUENTAPPTAL,                          
                          DESCRIPCION, 
                          REFERENCIA,
                          FUENTE_RECURSOS';


            MI_VALORES := ''''||UN_COMPANIA||''',
                           '||MI_ANIO||',
                           '''||MI_CPTCAUSADIFERIDA||''',
                           '||MI_NUMERO||',
                           '||MI_CONSECUTIVO||',
                           '''||MI_DEBITO||''',
                           '''||UN_FECHA||''',
                           '''||MI_NATURALEZA||''',
                           '||NVL(MI_RS.VALOR_COMPRA,0)||',
                           0,
                           '||NVL(MI_RS.VALOR_COMPRA,0)||',
                           0,
                           '''||NVL(MI_RS.CENTRO_COSTO,9999999999)||''',
                           '''||MI_STRTERCEROAUX||''',
                           '''||MI_STRSUCURSALAUX||''',
                           '''||NVL(MI_RS.AUXILIAR,9999999999999999)||''',
                           '''||MI_CTAPPTAL||''',
                           '''||MI_DESCRIPCION||''',
                           '''||MI_RS.REFERENCIA||''',
                           '''||MI_RS.FUENTE_RECURSO||''' ';

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
               MI_MSGERROR(2).VALOR := UN_TIPOCOBRO;

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

         MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CREDITO);           

           SELECT CUENTA_CONTABLE
              INTO MI_CTAPPTAL
            FROM PLAN_PPTAL_CUENTACNT          
            WHERE COMPANIA = UN_COMPANIA
              AND ANO        = MI_ANIO
              AND RUBRO      = MI_CREDITO;        

          IF MI_PARMANEJATERCERO  = 'SI'THEN

              SELECT TERCERO, SUCURSAL 
              INTO  MI_STRTERCEROAUX,
                    MI_STRSUCURSALAUX
              FROM SF_CUENTAS_AUXTERCERO 
              WHERE  COMPANIA = UN_COMPANIA 
                AND ANO = MI_ANIO 
                AND TIPOCOBRO = UN_TIPOCOBRO
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
                          EJECUCION_DEBITO,
                          EJECUCION_CREDITO,
                          CENTRO_COSTO,
                          TERCERO,
                          SUCURSAL, 
                          AUXILIAR,
                          CUENTAPPTAL,                          
                          DESCRIPCION, 
                          REFERENCIA,
                          FUENTE_RECURSOS';

          MI_VALORES := ''''||UN_COMPANIA||''',
                           '||MI_ANIO||',
                           '''||MI_CPTCAUSADIFERIDA||''',
                           '||MI_NUMERO||',
                           '||MI_CONSECUTIVO||',
                           '''||MI_CREDITO||''',
                           '''||UN_FECHA||''',
                           '''||MI_NATURALEZA||''',                           
                           0,
                           '||NVL(MI_RS.VALOR_COMPRA,0)||',                           
                           0,
                           '||NVL(MI_RS.VALOR_COMPRA,0)||',
                           '''||NVL(MI_RS.CENTRO_COSTO,9999999999)||''',
                           '''||MI_STRTERCEROAUX||''',
                           '''||MI_STRSUCURSALAUX||''',
                           '''||NVL(MI_RS.AUXILIAR,9999999999999999)||''',
                           '''||MI_CTAPPTAL||''',
                           '''||MI_DESCRIPCION||''',
                           '''||MI_RS.REFERENCIA||''',
                           '''||MI_RS.FUENTE_RECURSO||''' ';

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
               MI_MSGERROR(2).VALOR := UN_TIPOCOBRO;

                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                        ,UN_REEMPLAZOS  => MI_MSGERROR);

          END;                  
            MI_CONSC := TRUE;
        ELSE 
          MI_RPTA := 0;  
        END IF;

         --*************   REGISTRO DEL VALOR DE LA UTILIDAD PARA EL CASO DONDE SE MANEJA ALMACEN   *******************
             --***************** DEBITO *******************   

      IF MI_RS.TIPODECONCEPTO IN ('C') THEN

         IF MI_CLASECONTABLE IN ('V') THEN
           MI_DEBITO := NVL(MI_RS.CUENTADEBITOUTILIDAD, '');
           MI_CREDITO := NVL(MI_RS.CUENTACREDITOUTILIDAD, '');
         ELSE
           MI_CREDITO := NVL(MI_RS.CUENTADEBITOUTILIDAD, '');
           MI_DEBITO := NVL(MI_RS.CUENTACREDITOUTILIDAD, '');
         END IF;

            IF MI_DEBITO IS NOT NULL THEN

                MI_CTAPPTAL := '';            
                MI_CONSECUTIVO  :=  MI_CONSECUTIVO + 1 ;
                MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_DEBITO);

                IF MI_PARMANEJATERCERO  = 'SI' THEN

                      SELECT TERCERO, SUCURSAL 
                      INTO  MI_STRTERCEROAUX,
                            MI_STRSUCURSALAUX
                      FROM SF_CUENTAS_AUXTERCERO 
                      WHERE  COMPANIA = UN_COMPANIA 
                        AND ANO = MI_ANIO 
                        AND TIPOCOBRO = UN_TIPOCOBRO
                        AND CONCEPTO = MI_RS.CODIGO
                        AND CUENTA = MI_DEBITO ;

                ELSE
                    MI_STRTERCEROAUX  := MI_RS.TERCEROH;
                    MI_STRSUCURSALAUX := MI_RS.SUCURSALH;
                END IF;              

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
                          EJECUCION_DEBITO,
                          EJECUCION_CREDITO,
                          CENTRO_COSTO,
                          TERCERO,
                          SUCURSAL, 
                          AUXILIAR,
                          CUENTAPPTAL,                          
                          DESCRIPCION, 
                          REFERENCIA,
                          FUENTE_RECURSOS';              


                        MI_VALORES := ''''||UN_COMPANIA||''',
                                       '||MI_ANIO||',
                                       '''||MI_CPTCAUSADIFERIDA||''',
                                       '||MI_NUMERO||',
                                       '||MI_CONSECUTIVO||',
                                       '''||MI_DEBITO||''',
                                       '''||UN_FECHA||''',
                                       '''||MI_NATURALEZA||''',
                                       '||NVL(MI_RS.VALOR_UTILIDAD,0)||',
                                       0,
                                       '||NVL(MI_RS.VALOR_UTILIDAD,0)||',
                                       0,
                                       '''||NVL(MI_RS.CENTRO_COSTO,9999999999)||''',
                                       '''||MI_STRTERCEROAUX||''',
                                       '''||MI_STRSUCURSALAUX||''',
                                       '''||NVL(MI_RS.AUXILIAR,9999999999999999)||''',
                                       '''||MI_CTAPPTAL||''',
                                       '''||MI_DESCRIPCION||''',
                                       '''||MI_RS.REFERENCIA||''',
                                       '''||MI_RS.FUENTE_RECURSO||''' ';

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
                           MI_MSGERROR(2).VALOR := UN_TIPOCOBRO;

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

               MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CREDITO);           

                   SELECT CUENTA_CONTABLE
                      INTO MI_CTAPPTAL
                    FROM PLAN_PPTAL_CUENTACNT          
                    WHERE COMPANIA = UN_COMPANIA
                      AND ANO        = MI_ANIO
                      AND RUBRO      = MI_CREDITO;        

                  IF MI_PARMANEJATERCERO  = 'SI'THEN

                      SELECT TERCERO, SUCURSAL 
                      INTO  MI_STRTERCEROAUX,
                            MI_STRSUCURSALAUX
                      FROM SF_CUENTAS_AUXTERCERO 
                      WHERE  COMPANIA = UN_COMPANIA 
                        AND ANO = MI_ANIO 
                        AND TIPOCOBRO = UN_TIPOCOBRO
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
                          EJECUCION_DEBITO,
                          EJECUCION_CREDITO,
                          CENTRO_COSTO,
                          TERCERO,
                          SUCURSAL, 
                          AUXILIAR,
                          CUENTAPPTAL,                          
                          DESCRIPCION, 
                          REFERENCIA,
                          FUENTE_RECURSOS';  

                  MI_VALORES := ''''||UN_COMPANIA||''',
                                   '||MI_ANIO||',
                                   '''||MI_CPTCAUSADIFERIDA||''',
                                   '||MI_NUMERO||',
                                   '||MI_CONSECUTIVO||',
                                   '''||MI_CREDITO||''',
                                   '''||UN_FECHA||''',
                                   '''||MI_NATURALEZA||''',                           
                                   0,
                                   '||NVL(MI_RS.VALOR_UTILIDAD,0)||',                           
                                   0,
                                   '||NVL(MI_RS.VALOR_UTILIDAD,0)||',
                                   '''||NVL(MI_RS.CENTRO_COSTO,9999999999)||''',
                                   '''||MI_STRTERCEROAUX||''',
                                   '''||MI_STRSUCURSALAUX||''',
                                   '''||NVL(MI_RS.AUXILIAR,9999999999999999)||''',
                                   '''||MI_CTAPPTAL||''',
                                   '''||MI_DESCRIPCION||''',
                                   '''||MI_RS.REFERENCIA||''',
                                   '''||MI_RS.FUENTE_RECURSO||''' ';

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
                       MI_MSGERROR(2).VALOR := UN_TIPOCOBRO;

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

     ELSE 

        IF MI_CLASECONTABLE IN ('V') THEN
          MI_DEBITO := NVL(MI_RS.CUENTADEBITOBASE, '');
          MI_CREDITO := NVL(MI_RS.CUENTACREDITOBASE, '');
        ELSE
          MI_CREDITO := NVL(MI_RS.CUENTADEBITOBASE, '');
          MI_DEBITO := NVL(MI_RS.CUENTACREDITOBASE, '');
        END IF;    

             --'*************   REGISTRO DEL VALOR BASE   *******************
             --'***************** DEBITO *******************     

        IF MI_DEBITO IS NOT NULL THEN

            MI_CTAPPTAL := '';            
            MI_CONSECUTIVO  :=  MI_CONSECUTIVO + 1 ;
            MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_DEBITO);

            IF MI_PARMANEJATERCERO  = 'SI' THEN

                  SELECT TERCERO, SUCURSAL 
                  INTO  MI_STRTERCEROAUX,
                        MI_STRSUCURSALAUX
                  FROM SF_CUENTAS_AUXTERCERO 
                  WHERE  COMPANIA = UN_COMPANIA 
                    AND ANO = MI_ANIO 
                    AND TIPOCOBRO = UN_TIPOCOBRO
                    AND CONCEPTO = MI_RS.CODIGO
                    AND CUENTA = MI_DEBITO ;

            ELSE
                MI_STRTERCEROAUX  := MI_RS.TERCEROH;
                MI_STRSUCURSALAUX := MI_RS.SUCURSALH;
            END IF;           

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
                          EJECUCION_DEBITO,
                          EJECUCION_CREDITO,
                          CENTRO_COSTO,
                          TERCERO,
                          SUCURSAL, 
                          AUXILIAR,
                          CUENTAPPTAL,                          
                          DESCRIPCION, 
                          REFERENCIA,
                          FUENTE_RECURSOS';  


                    MI_VALORES := ''''||UN_COMPANIA||''',
                                   '||MI_ANIO||',
                                   '''||MI_CPTCAUSADIFERIDA||''',
                                   '||MI_NUMERO||',
                                   '||MI_CONSECUTIVO||',
                                   '''||MI_DEBITO||''',
                                   '''||UN_FECHA||''',
                                   '''||MI_NATURALEZA||''',
                                   '||NVL(MI_RS.VALOR_BASE,0)||',
                                   0,
                                   '||NVL(MI_RS.VALOR_BASE,0)||',
                                   0,
                                   '''||NVL(MI_RS.CENTRO_COSTO,9999999999)||''',
                                   '''||MI_STRTERCEROAUX||''',
                                   '''||MI_STRSUCURSALAUX||''',
                                   '''||NVL(MI_RS.AUXILIAR,9999999999999999)||''',
                                   '''||MI_CTAPPTAL||''',
                                   '''||MI_DESCRIPCION||''',
                                   '''||MI_RS.REFERENCIA||''',
                                   '''||MI_RS.FUENTE_RECURSO||''' ';

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
                       MI_MSGERROR(2).VALOR := UN_TIPOCOBRO;

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

               MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CREDITO);           

                BEGIN   
                   SELECT CUENTA_CONTABLE
                      INTO MI_CTAPPTAL
                    FROM PLAN_PPTAL_CUENTACNT          
                    WHERE COMPANIA = UN_COMPANIA
                      AND ANO        = MI_ANIO
                      AND RUBRO      = MI_CREDITO;        
                   EXCEPTION WHEN NO_DATA_FOUND THEN  
                        MI_CTAPPTAL := NULL; 
                END;

                  IF MI_PARMANEJATERCERO  = 'SI'THEN

                      SELECT TERCERO, SUCURSAL 
                      INTO  MI_STRTERCEROAUX,
                            MI_STRSUCURSALAUX
                      FROM SF_CUENTAS_AUXTERCERO 
                      WHERE  COMPANIA = UN_COMPANIA 
                        AND ANO = MI_ANIO 
                        AND TIPOCOBRO = UN_TIPOCOBRO
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
                          EJECUCION_DEBITO,
                          EJECUCION_CREDITO,
                          CENTRO_COSTO,
                          TERCERO,
                          SUCURSAL, 
                          AUXILIAR,
                          CUENTAPPTAL,                          
                          DESCRIPCION, 
                          REFERENCIA,
                          FUENTE_RECURSOS';  

                  MI_VALORES := ''''||UN_COMPANIA||''',
                                   '||MI_ANIO||',
                                '''||MI_CPTCAUSADIFERIDA||''',
                                  '||MI_NUMERO||',
                                   '||MI_CONSECUTIVO||',
                                   '''||MI_CREDITO||''',
                                   '''||UN_FECHA||''',
                                   '''||MI_NATURALEZA||''',                           
                                   0,
                                   '||NVL(MI_RS.VALOR_BASE,0)||',                           
                                   0,
                                   '||NVL(MI_RS.VALOR_BASE,0)||',
                                   '''||NVL(MI_RS.CENTRO_COSTO,9999999999)||''',
                                   '''||MI_STRTERCEROAUX||''',
                                   '''||MI_STRSUCURSALAUX||''',
                                   '''||NVL(MI_RS.AUXILIAR,9999999999999999)||''',
                                   '''||MI_CTAPPTAL||''',
                                   '''||MI_DESCRIPCION||''',
                                   '''||MI_RS.REFERENCIA||''',
                                   '''||MI_RS.FUENTE_RECURSO||''' ';

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
                       MI_MSGERROR(2).VALOR := UN_TIPOCOBRO;

                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                                UN_EXC_COD =>SQLCODE
                                                ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                ,UN_REEMPLAZOS  => MI_MSGERROR);

            END;                  
                MI_CONSC := TRUE;
            ELSE 
                 MI_RPTA := 0;              

            END IF;

     END IF; --FIN DE LA EVALUACION DE SI EL TIPO DE COBRO MANEJA INVENTARIO
        --*******************************************************************************************************************************
        --******************************************************** FIN INVENTARIO *******************************************************
        --*******************************************************************************************************************************

    IF MI_RS.APLICAIVA NOT IN (0)THEN
        --    '***************** DEBITO *******************
        IF MI_CLASECONTABLE IN ('V') THEN
          MI_DEBITO := NVL(MI_RS.CUENTADEBITOIVA, '');
          MI_CREDITO := NVL(MI_RS.CUENTACREDITOIVA,'');
        ELSE
          MI_CREDITO := NVL(MI_RS.CUENTADEBITOIVA, '');
          MI_DEBITO := NVL(MI_RS.CUENTACREDITOIVA, '');
        END IF;  

       IF MI_DEBITO IS NOT NULL THEN

            MI_CTAPPTAL := '';            
            MI_CONSECUTIVO  :=  MI_CONSECUTIVO + 1 ;
            MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_DEBITO);

            IF MI_PARMANEJATERCERO  = 'SI' THEN

                  SELECT TERCERO, SUCURSAL 
                  INTO  MI_STRTERCEROAUX,
                        MI_STRSUCURSALAUX
                  FROM SF_CUENTAS_AUXTERCERO 
                  WHERE  COMPANIA = UN_COMPANIA 
                    AND ANO = MI_ANIO 
                    AND TIPOCOBRO = UN_TIPOCOBRO
                    AND CONCEPTO = MI_RS.CODIGO
                    AND CUENTA = MI_DEBITO ;

            ELSE
                MI_STRTERCEROAUX  := MI_RS.TERCEROH;
                MI_STRSUCURSALAUX := MI_RS.SUCURSALH;
            END IF;              

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
                          EJECUCION_DEBITO,
                          EJECUCION_CREDITO,
                          CENTRO_COSTO,
                          TERCERO,
                          SUCURSAL, 
                          AUXILIAR,
                          CUENTAPPTAL,                          
                          DESCRIPCION, 
                          REFERENCIA,
                          FUENTE_RECURSOS';               


                    MI_VALORES := ''''||UN_COMPANIA||''',
                                   '||MI_ANIO||',
                                  '''||MI_CPTCAUSADIFERIDA||''',
                                   '||MI_NUMERO||',
                                   '||MI_CONSECUTIVO||',
                                   '''||MI_DEBITO||''',
                                   '''||UN_FECHA||''',
                                   '''||MI_NATURALEZA||''',
                                   '||NVL(MI_RS.VALOR_IVA,0)||',
                                   0,
                                   '||NVL(MI_RS.VALOR_IVA,0)||',
                                   0,
                                   '''||NVL(MI_RS.CENTRO_COSTO,9999999999)||''',
                                   '''||MI_STRTERCEROAUX||''',
                                   '''||MI_STRSUCURSALAUX||''',
                                   '''||NVL(MI_RS.AUXILIAR,9999999999999999)||''',
                                   '''||MI_CTAPPTAL||''',
                                   '''||MI_DESCRIPCION||''',
                                   '''||MI_RS.REFERENCIA||''',
                                   '''||MI_RS.FUENTE_RECURSO||''' ';

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
                       MI_MSGERROR(2).VALOR := UN_TIPOCOBRO;

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

           MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CREDITO);           

               SELECT CUENTA_CONTABLE
                  INTO MI_CTAPPTAL
                FROM PLAN_PPTAL_CUENTACNT          
                WHERE COMPANIA = UN_COMPANIA
                  AND ANO        = MI_ANIO
                  AND RUBRO      = MI_CREDITO;        

              IF MI_PARMANEJATERCERO  = 'SI'THEN

                  SELECT TERCERO, SUCURSAL 
                  INTO  MI_STRTERCEROAUX,
                        MI_STRSUCURSALAUX
                  FROM SF_CUENTAS_AUXTERCERO 
                  WHERE  COMPANIA = UN_COMPANIA 
                    AND ANO = MI_ANIO 
                    AND TIPOCOBRO = UN_TIPOCOBRO
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
                          EJECUCION_DEBITO,
                          EJECUCION_CREDITO,
                          CENTRO_COSTO,
                          TERCERO,
                          SUCURSAL, 
                          AUXILIAR,
                          CUENTAPPTAL,                          
                          DESCRIPCION, 
                          REFERENCIA,
                          FUENTE_RECURSOS'; 

              MI_VALORES := ''''||UN_COMPANIA||''',
                               '||MI_ANIO||',
                             '''||MI_CPTCAUSADIFERIDA||''',
                                '||MI_NUMERO||',
                               '||MI_CONSECUTIVO||',
                               '''||MI_CREDITO||''',
                               '''||UN_FECHA||''',
                               '''||MI_NATURALEZA||''',                           
                               0,
                               '||NVL(MI_RS.VALOR_IVA,0)||',                           
                               0,
                               '||NVL(MI_RS.VALOR_IVA,0)||',
                               '''||NVL(MI_RS.CENTRO_COSTO,9999999999)||''',
                               '''||MI_STRTERCEROAUX||''',
                               '''||MI_STRSUCURSALAUX||''',
                               '''||NVL(MI_RS.AUXILIAR,9999999999999999)||''',
                               '''||MI_CTAPPTAL||''',
                               '''||MI_DESCRIPCION||'''  ,
                               '''||MI_RS.REFERENCIA||''',
                               '''||MI_RS.FUENTE_RECURSO||''' ';                      

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
                   MI_MSGERROR(2).VALOR := UN_TIPOCOBRO;

                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                            UN_EXC_COD =>SQLCODE
                                            ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                            ,UN_REEMPLAZOS  => MI_MSGERROR);

        END;                  
            MI_CONSC := TRUE;
        ELSE 
             MI_RPTA := 0;              

        END IF;    

    END IF; --APLICAIVA

    --         '*************   REGISTRO DEL VALOR RETEFUENTE SI INDICADOR SE ENCUENTRA ACTIVO   *******************  

    IF MI_RS.APLICARETEFUENTE NOT IN (0) THEN
            --'***************** DEBITO *******************
        IF MI_CLASECONTABLE IN ('V') THEN
          MI_DEBITO := NVL(MI_RS.CUENTADEBITORETE, '');
          MI_CREDITO := NVL(MI_RS.CUENTACREDITORETE, '');
        ELSE
          MI_CREDITO := NVL(MI_RS.CUENTADEBITORETE, '');
          MI_DEBITO := NVL(MI_RS.CUENTACREDITORETE, '');
        END IF;

       IF MI_DEBITO IS NOT NULL THEN

            MI_CTAPPTAL := '';            
            MI_CONSECUTIVO  :=  MI_CONSECUTIVO + 1 ;
            MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_DEBITO);

            IF MI_PARMANEJATERCERO  = 'SI' THEN

                  SELECT TERCERO, SUCURSAL 
                  INTO  MI_STRTERCEROAUX,
                        MI_STRSUCURSALAUX
                  FROM SF_CUENTAS_AUXTERCERO 
                  WHERE  COMPANIA = UN_COMPANIA 
                    AND ANO = MI_ANIO 
                    AND TIPOCOBRO = UN_TIPOCOBRO
                    AND CONCEPTO = MI_RS.CODIGO
                    AND CUENTA = MI_DEBITO ;

            ELSE
                MI_STRTERCEROAUX  := MI_RS.TERCEROH;
                MI_STRSUCURSALAUX := MI_RS.SUCURSALH;
            END IF;              

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
                          EJECUCION_DEBITO,
                          EJECUCION_CREDITO,
                          CENTRO_COSTO,
                          TERCERO,
                          SUCURSAL, 
                          AUXILIAR,
                          CUENTAPPTAL,                          
                          DESCRIPCION, 
                          REFERENCIA,
                          FUENTE_RECURSOS';                


                    MI_VALORES := ''''||UN_COMPANIA||''',
                                   '||MI_ANIO||',
                                    '''||MI_CPTCAUSADIFERIDA||''',
                                    '||MI_NUMERO||',
                                   '||MI_CONSECUTIVO||',
                                   '''||MI_DEBITO||''',
                                   '''||UN_FECHA||''',
                                   '''||MI_NATURALEZA||''',
                                   '||NVL(MI_RS.VALOR_RETE,0)||',
                                   0,
                                   '||NVL(MI_RS.VALOR_RETE,0)||',
                                   0,
                                   '''||NVL(MI_RS.CENTRO_COSTO,9999999999)||''',
                                   '''||MI_STRTERCEROAUX||''',
                                   '''||MI_STRSUCURSALAUX||''',
                                   '''||NVL(MI_RS.AUXILIAR,9999999999999999)||''',
                                   '''||MI_CTAPPTAL||''',
                                   '''||MI_DESCRIPCION||''' ,
                                   '''||MI_RS.REFERENCIA||''',
                                   '''||MI_RS.FUENTE_RECURSO||''' ';

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
                       MI_MSGERROR(2).VALOR := UN_TIPOCOBRO;

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

           MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CREDITO);           

               SELECT CUENTA_CONTABLE
                  INTO MI_CTAPPTAL
                FROM PLAN_PPTAL_CUENTACNT          
                WHERE COMPANIA = UN_COMPANIA
                  AND ANO        = MI_ANIO
                  AND RUBRO      = MI_CREDITO;        

              IF MI_PARMANEJATERCERO  = 'SI'THEN

                  SELECT TERCERO, SUCURSAL 
                  INTO  MI_STRTERCEROAUX,
                        MI_STRSUCURSALAUX
                  FROM SF_CUENTAS_AUXTERCERO 
                  WHERE  COMPANIA = UN_COMPANIA 
                    AND ANO = MI_ANIO 
                    AND TIPOCOBRO = UN_TIPOCOBRO
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
                          EJECUCION_DEBITO,
                          EJECUCION_CREDITO,
                          CENTRO_COSTO,
                          TERCERO,
                          SUCURSAL, 
                          AUXILIAR,
                          CUENTAPPTAL,                          
                          DESCRIPCION, 
                          REFERENCIA,
                          FUENTE_RECURSOS'; 

              MI_VALORES := ''''||UN_COMPANIA||''',
                               '||MI_ANIO||',
                             '''||MI_CPTCAUSADIFERIDA||''',
                                '||MI_NUMERO||',
                               '||MI_CONSECUTIVO||',
                               '''||MI_CREDITO||''',
                               '''||UN_FECHA||''',
                               '''||MI_NATURALEZA||''',                           
                               0,
                               '||NVL(MI_RS.VALOR_RETE,0)||',                           
                               0,
                               '||NVL(MI_RS.VALOR_RETE,0)||',
                               '''||NVL(MI_RS.CENTRO_COSTO,9999999999)||''',
                               '''||MI_STRTERCEROAUX||''',
                               '''||MI_STRSUCURSALAUX||''',
                               '''||NVL(MI_RS.AUXILIAR,9999999999999999)||''',
                               '''||MI_CTAPPTAL||''',
                               '''||MI_DESCRIPCION||'''   ,
                               '''||MI_RS.REFERENCIA||''',
                               '''||MI_RS.FUENTE_RECURSO||''' ';                 

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
                   MI_MSGERROR(2).VALOR := UN_TIPOCOBRO;

                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                            UN_EXC_COD =>SQLCODE
                                            ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                            ,UN_REEMPLAZOS  => MI_MSGERROR);

        END;                  
            MI_CONSC := TRUE;
       ELSE 
             MI_RPTA := 0;              

       END IF;               

    END IF ; --APLICARETEFUENTE   

    --*************   REGISTRO DEL VALOR DESCUENTO SI INDICADOR SE ENCUENTRA ACTIVO   *******************

    IF MI_RS.APLICADESCUENTO NOT IN (0) THEN
            --'***************** DEBITO *******************
        IF MI_CLASECONTABLE IN ('V') THEN
          MI_DEBITO := NVL(MI_RS.CUENTADEBITODESCUENTO, '');
          MI_CREDITO := NVL(MI_RS.CUENTACREDITODESCUENTO, '');
        ELSE
          MI_CREDITO := NVL(MI_RS.CUENTADEBITODESCUENTO, '');
          MI_DEBITO := NVL(MI_RS.CUENTACREDITODESCUENTO, '');
        END IF ;

       IF MI_DEBITO IS NOT NULL THEN

            MI_CTAPPTAL := '';            
            MI_CONSECUTIVO  :=  MI_CONSECUTIVO + 1 ;
            MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_DEBITO);

            IF MI_PARMANEJATERCERO  = 'SI' THEN

                  SELECT TERCERO, SUCURSAL 
                  INTO  MI_STRTERCEROAUX,
                        MI_STRSUCURSALAUX
                  FROM SF_CUENTAS_AUXTERCERO 
                  WHERE  COMPANIA = UN_COMPANIA 
                    AND ANO = MI_ANIO 
                    AND TIPOCOBRO = UN_TIPOCOBRO
                    AND CONCEPTO = MI_RS.CODIGO
                    AND CUENTA = MI_DEBITO ;

            ELSE
                MI_STRTERCEROAUX  := MI_RS.TERCEROH;
                MI_STRSUCURSALAUX := MI_RS.SUCURSALH;
            END IF;              

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
                          EJECUCION_DEBITO,
                          EJECUCION_CREDITO,
                          CENTRO_COSTO,
                          TERCERO,
                          SUCURSAL, 
                          AUXILIAR,
                          CUENTAPPTAL,                          
                          DESCRIPCION, 
                          REFERENCIA,
                          FUENTE_RECURSOS';               


                    MI_VALORES := ''''||UN_COMPANIA||''',
                                   '||MI_ANIO||',
                                  '''||MI_CPTCAUSADIFERIDA||''',
                                   '||MI_NUMERO||',
                                   '||MI_CONSECUTIVO||',
                                   '''||MI_DEBITO||''',
                                   '''||UN_FECHA||''',
                                   '''||MI_NATURALEZA||''',
                                   '||NVL(MI_RS.VALOR_DESCUENTO,0)||',
                                   0,
                                   '||NVL(MI_RS.VALOR_DESCUENTO,0)||',
                                   0,
                                   '''||NVL(MI_RS.CENTRO_COSTO,9999999999)||''',
                                   '''||MI_STRTERCEROAUX||''',
                                   '''||MI_STRSUCURSALAUX||''',
                                   '''||NVL(MI_RS.AUXILIAR,9999999999999999)||''',
                                   '''||MI_CTAPPTAL||''',
                                   '''||MI_DESCRIPCION||'''  ,
                                   '''||MI_RS.REFERENCIA||''',
                                   '''||MI_RS.FUENTE_RECURSO||''' ';

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
                       MI_MSGERROR(2).VALOR := UN_TIPOCOBRO;

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

           MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CREDITO);           

               SELECT CUENTA_CONTABLE
                  INTO MI_CTAPPTAL
                FROM PLAN_PPTAL_CUENTACNT          
                WHERE COMPANIA = UN_COMPANIA
                  AND ANO        = MI_ANIO
                  AND RUBRO      = MI_CREDITO;        

              IF MI_PARMANEJATERCERO  = 'SI'THEN

                  SELECT TERCERO, SUCURSAL 
                  INTO  MI_STRTERCEROAUX,
                        MI_STRSUCURSALAUX
                  FROM SF_CUENTAS_AUXTERCERO 
                  WHERE  COMPANIA = UN_COMPANIA 
                    AND ANO = MI_ANIO 
                    AND TIPOCOBRO = UN_TIPOCOBRO
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
                          EJECUCION_DEBITO,
                          EJECUCION_CREDITO,
                          CENTRO_COSTO,
                          TERCERO,
                          SUCURSAL, 
                          AUXILIAR,
                          CUENTAPPTAL,                          
                          DESCRIPCION, 
                          REFERENCIA,
                          FUENTE_RECURSOS';  

              MI_VALORES := ''''||UN_COMPANIA||''',
                               '||MI_ANIO||',
                                '''||MI_CPTCAUSADIFERIDA||''',
                                '||MI_NUMERO||',
                               '||MI_CONSECUTIVO||',
                               '''||MI_CREDITO||''',
                               '''||UN_FECHA||''',
                               '''||MI_NATURALEZA||''',                           
                               0,
                               '||NVL(MI_RS.VALOR_DESCUENTO,0)||',                           
                               0,
                               '||NVL(MI_RS.VALOR_DESCUENTO,0)||',
                               '''||NVL(MI_RS.CENTRO_COSTO,9999999999)||''',
                               '''||MI_STRTERCEROAUX||''',
                               '''||MI_STRSUCURSALAUX||''',
                               '''||NVL(MI_RS.AUXILIAR,9999999999999999)||''',
                               '''||MI_CTAPPTAL||''',
                               '''||MI_DESCRIPCION||'''     ,
                               '''||MI_RS.REFERENCIA||''',
                               '''||MI_RS.FUENTE_RECURSO||''' ';                     

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
                   MI_MSGERROR(2).VALOR := UN_TIPOCOBRO;

                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                            UN_EXC_COD =>SQLCODE
                                            ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                            ,UN_REEMPLAZOS  => MI_MSGERROR);

        END;                  
            MI_CONSC := TRUE;
       ELSE 
             MI_RPTA := 0;              

       END IF;

    END IF;  --APLICADESCUENTO 

    --*************   REGISTRO DEL VALOR ICA SI INDICADOR SE ENCUENTRA ACTIVO   *******************

    IF MI_RS.APLICAICA NOT IN (0)THEN
            --***************** DEBITO *******************
        IF MI_CLASECONTABLE IN ('V') THEN
          MI_DEBITO := NVL(MI_RS.CUENTADEBITOICA, '');
          MI_CREDITO := NVL(MI_RS.CUENTACREDITOICA, '');
        ELSE
          MI_CREDITO := NVL(MI_RS.CUENTADEBITOICA, '');
          MI_DEBITO := NVL(MI_RS.CUENTACREDITOICA, '');
        END IF;

       IF MI_DEBITO IS NOT NULL THEN

            MI_CTAPPTAL := '';            
            MI_CONSECUTIVO  :=  MI_CONSECUTIVO + 1 ;
            MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_DEBITO);

            IF MI_PARMANEJATERCERO  = 'SI' THEN

                  SELECT TERCERO, SUCURSAL 
                  INTO  MI_STRTERCEROAUX,
                        MI_STRSUCURSALAUX
                  FROM SF_CUENTAS_AUXTERCERO 
                  WHERE  COMPANIA = UN_COMPANIA 
                    AND ANO = MI_ANIO 
                    AND TIPOCOBRO = UN_TIPOCOBRO
                    AND CONCEPTO = MI_RS.CODIGO
                    AND CUENTA = MI_DEBITO ;

            ELSE
                MI_STRTERCEROAUX  := MI_RS.TERCEROH;
                MI_STRSUCURSALAUX := MI_RS.SUCURSALH;
            END IF;              

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
                          EJECUCION_DEBITO,
                          EJECUCION_CREDITO,
                          CENTRO_COSTO,
                          TERCERO,
                          SUCURSAL, 
                          AUXILIAR,
                          CUENTAPPTAL,                          
                          DESCRIPCION, 
                          REFERENCIA,
                          FUENTE_RECURSOS';                


                    MI_VALORES := ''''||UN_COMPANIA||''',
                                   '||MI_ANIO||',
                                 '''||MI_CPTCAUSADIFERIDA||''',
                                   '||MI_NUMERO||',
                                   '||MI_CONSECUTIVO||',
                                   '''||MI_DEBITO||''',
                                   '''||UN_FECHA||''',
                                   '''||MI_NATURALEZA||''',
                                   '||NVL(MI_RS.VALOR_ICA,0)||',
                                   0,
                                   '||NVL(MI_RS.VALOR_ICA,0)||',
                                   0,
                                   '''||NVL(MI_RS.CENTRO_COSTO,9999999999)||''',
                                   '''||MI_STRTERCEROAUX||''',
                                   '''||MI_STRSUCURSALAUX||''',
                                   '''||NVL(MI_RS.AUXILIAR,9999999999999999)||''',
                                   '''||MI_CTAPPTAL||''',
                                   '''||MI_DESCRIPCION||''',
                                   '''||MI_RS.REFERENCIA||''',
                                   '''||MI_RS.FUENTE_RECURSO||''' ';

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
                       MI_MSGERROR(2).VALOR := UN_TIPOCOBRO;

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

           MI_NATURALEZA   :=  PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_CREDITO);           

               SELECT CUENTA_CONTABLE
                  INTO MI_CTAPPTAL
                FROM PLAN_PPTAL_CUENTACNT          
                WHERE COMPANIA = UN_COMPANIA
                  AND ANO        = MI_ANIO
                  AND RUBRO      = MI_CREDITO;        

              IF MI_PARMANEJATERCERO  = 'SI'THEN

                  SELECT TERCERO, SUCURSAL 
                  INTO  MI_STRTERCEROAUX,
                        MI_STRSUCURSALAUX
                  FROM SF_CUENTAS_AUXTERCERO 
                  WHERE  COMPANIA = UN_COMPANIA 
                    AND ANO = MI_ANIO 
                    AND TIPOCOBRO = UN_TIPOCOBRO
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
                          EJECUCION_DEBITO,
                          EJECUCION_CREDITO,
                          CENTRO_COSTO,
                          TERCERO,
                          SUCURSAL, 
                          AUXILIAR,
                          CUENTAPPTAL,                          
                          DESCRIPCION, 
                          REFERENCIA,
                          FUENTE_RECURSOS';   

              MI_VALORES := ''''||UN_COMPANIA||''',
                               '||MI_ANIO||',
                              '''||MI_CPTCAUSADIFERIDA||''',
                                '||MI_NUMERO||',
                               '||MI_CONSECUTIVO||',
                               '''||MI_CREDITO||''',
                               '''||UN_FECHA||''',
                               '''||MI_NATURALEZA||''',                           
                               0,
                               '||NVL(MI_RS.VALOR_ICA,0)||',                           
                               0,
                               '||NVL(MI_RS.VALOR_ICA,0)||',
                               '''||NVL(MI_RS.CENTRO_COSTO,9999999999)||''',
                               '''||MI_STRTERCEROAUX||''',
                               '''||MI_STRSUCURSALAUX||''',
                               '''||NVL(MI_RS.AUXILIAR,9999999999999999)||''',
                               '''||MI_CTAPPTAL||''',
                               '''||MI_DESCRIPCION||''',
                               '''||MI_RS.REFERENCIA||''',
                               '''||MI_RS.FUENTE_RECURSO||''' ';                    

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
                   MI_MSGERROR(2).VALOR := UN_TIPOCOBRO;

                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                            UN_EXC_COD =>SQLCODE
                                            ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                            ,UN_REEMPLAZOS  => MI_MSGERROR);

        END;                  
            MI_CONSC := TRUE;
       ELSE 
             MI_RPTA := 0;              

       END IF;       

    END IF; --APLICAICA
  END LOOP  RECORRER_CUOTAS; 

    MI_RTAINTERFAZ := PCK_CONTABILIZAR.FC_CONTABILIZAR( UN_COMPANIA         => UN_COMPANIA
                                              ,UN_TIPOCOMPROBANTE  => MI_CPTCAUSADIFERIDA
                                              ,UN_NUMERO           => MI_NUMERO
                                              ,UN_ANO              => MI_ANIO
                                              ,UN_FECHA            => UN_FECHA
                                              ,UN_TERCERO          => UN_TERCERO
                                              ,UN_SUCURSAL         => UN_SUCURSAL
                                              ,UN_DESCRIPCION      => MI_DESCRIPCION                                            
                                              ,UN_SIMPLE           => -1
                                              ,UN_INDIMPRESION     => 0                                            
                                              ,UN_INTOMITIRPPTAL   => -1
                                              ,UN_CONCILIAR        => 0
                                              ,UN_PLANO            => 0
                                              ,UN_NONETEA          => -1
                                              ,UN_CONTRATISTA      => 0
                                              ,UN_TEXTO            => 'Contabilizar'
                                              ,UN_CONTRATO         => 0
                                              ,UN_TIPOCONTRATO     => ''
                                              ,UN_NRO_DOCUMENTO    => ''
                                              ,UN_ALMDEP           => 0
                                              ,UN_TERCE            => 0
                                              ,UN_RESAUXGEN        => 0
                                              ,UN_USUARIO          => UN_USUARIO);  

  END IF;                                            
    MI_RPTA := FC_GENERARINGRESOABONO(UN_COMPANIA         => UN_COMPANIA,    
                          UN_ANIO             => MI_ANIO_FAC,      
                          UN_TIPOCOBRO        => UN_TIPOCOBRO,
                          UN_NUMERO           => MI_NUMERO,
                          UN_TIPOABONO        => UN_TIPOABONO,
                          UN_NUMEROABONO      => UN_NUMEROABONO,   
                          UN_FECHA            => UN_FECHA,
                          UN_TERCERO          => UN_TERCERO,
                          UN_SUCURSAL         => UN_SUCURSAL,
                          UN_CUENTA           => UN_CUENTA,
                          UN_DESCRIPCION      => 'Pago parcial fra '||UN_TIPOCOBRO||' - '||MI_FACTURA||' con abono '||UN_TIPOABONO||' - '||UN_NUMEROABONO||' Cuota '||UN_CUOTABONO,
                          UN_CUOTABONO        => UN_CUOTABONO,
                          UN_VLRABONO         => UN_VLRABONO,
                          UN_USUARIO          => UN_USUARIO);                                              

     RETURN MI_RPTA;

END FC_INTERFAZCONTABLEABONO;

FUNCTION FC_GENERARINGRESOABONO
/*
      NAME              : FC_GENERARINGRESOABONO MIGRADO DE ACCESS GenerarIngresoAbono
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 11/06/2019
      TIME              : 02:54 PM
      SOURCE MODULE     : SysmanSF2019.01.04
      DESCRIPTION       :
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 


    @NAME:   generarIngresoAbono
    @METHOD: GET
*/
(
  UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,    
  UN_ANIO             IN PCK_SUBTIPOS.TI_ANIO,      
  UN_TIPOCOBRO        IN PCK_SUBTIPOS.TI_TIPO_COBRO,
  UN_NUMERO           IN DETALLE_COMPROBANTE_CNT.COMPROBANTE%TYPE,
  UN_TIPOABONO        IN SF_ABONOS.TIPO%TYPE,
  UN_NUMEROABONO      IN SF_ABONOS.CODIGO%TYPE,     
  UN_FECHA            IN DATE,
  UN_TERCERO          IN SF_DETALLE_FACTURA.TERCERO%TYPE,
  UN_SUCURSAL         IN SF_DETALLE_FACTURA.SUCURSAL%TYPE,  
  UN_CUENTA           IN DETALLE_COMPROBANTE_CNT.CUENTA%TYPE,
  UN_DESCRIPCION      IN DETALLE_COMPROBANTE_CNT.DESCRIPCION%TYPE,
  UN_CUOTABONO        IN SF_DETALLE_ABONO.CUOTA%TYPE,
  UN_VLRABONO         IN PCK_SUBTIPOS.TI_DOBLE , 
  UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
)RETURN VARCHAR2
AS
  MI_NUMERO            COMPROBANTE_CNT.NUMERO%TYPE;
  MI_ANIOAFECT         PCK_SUBTIPOS.TI_ANIO; 
  MI_CPTERECAUDO       SF_TIPO_COBRO.CPTE_RECAUDO%TYPE;
  MI_CPTECAUSACION     SF_TIPO_COBRO.CODIGO%TYPE;
  MI_CAMPOS            PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES           PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION         PCK_SUBTIPOS.TI_CONDICION;    
  MI_MSGERROR          PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_CONSECUTIVODET    PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_CONSECUTIVO       PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_CENTROCOSTO       PCK_SUBTIPOS.TI_CENTRO_COSTO;
  MI_CLASECUENTA       VARCHAR2(100 CHAR);
  MI_CLCUENTA          VARCHAR2(100 CHAR);
  MI_CUENTA            DETALLE_COMPROBANTE_CNT.CUENTA%TYPE;
  MI_VALOR             PCK_SUBTIPOS.TI_DOBLE:=0;
  MI_VALORINTERES      PCK_SUBTIPOS.TI_DOBLE:=0; 
  MI_CENTRO_COSTO      PCK_SUBTIPOS.TI_CENTRO_COSTO;
  MI_FUENTERECURSO     PCK_SUBTIPOS.TI_FUENTE_RECURSOS;
  MI_REFERENCIA        PCK_SUBTIPOS.TI_REFERENCIA;
  MI_AUXILIAR          PCK_SUBTIPOS.TI_AUXILIAR;
  MI_NATURALEZA        DETALLE_COMPROBANTE_CNT.NATURALEZA%TYPE; 
  MI_CUENTAPPTAL       DETALLE_COMPROBANTE_CNT.CUENTAPPTAL%TYPE;
  MI_CAUSARFACTRECAUDO SF_TIPO_COBRO.INTERFAZ_RECAUDO%TYPE;
  MI_CI_FINANCIACION   PCK_SUBTIPOS.TI_PARAMETRO;-- ' CONCEPTO CONFIGURADO PARA MANEJAR LOS INTERESES DE FINANCIACION
  MI_CPTCAUSADIFERIDA  PCK_SUBTIPOS.TI_TIPO_COBRO;   
  MI_STRSQL            PCK_SUBTIPOS.TI_STRSQL;
  MI_RS                SYS_REFCURSOR;
  MI_CUENTA4           VARCHAR2(100 CHAR);-- PARAMETRO CREADO PARA CONFIGURAR EL DETALLE DE INTERESES EN LA CUENTA 4

BEGIN

    SELECT CPTE_RECAUDO,CPTE_DIFERIDA,INTERFAZ_RECAUDO           
    INTO MI_CPTERECAUDO,MI_CPTCAUSADIFERIDA,MI_CAUSARFACTRECAUDO        
    FROM SF_TIPO_COBRO
    WHERE COMPANIA = UN_COMPANIA
      AND ANO      = UN_ANIO
      AND CODIGO   = UN_TIPOCOBRO; 

      IF MI_CAUSARFACTRECAUDO NOT IN (0)THEN
        MI_CPTECAUSACION := MI_CPTCAUSADIFERIDA;        
      ELSE
       MI_CPTECAUSACION := UN_TIPOCOBRO;      
      END IF;

    MI_CONSECUTIVODET := 1;

    MI_NUMERO := PCK_CONTABILIDAD_CPTE.FC_ENUMERARCOMPROBANTECNT(UN_COMPANIA     => UN_COMPANIA
                                                                       ,UN_ANIO         => EXTRACT (YEAR FROM UN_FECHA)
                                                                       ,UN_TIPO         => MI_CPTERECAUDO
                                                                       ,UN_NUMERO       => 0
                                                                       ,UN_CENTRO_COSTO => '');  


    MI_CLASECUENTA := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                              UN_NOMBRE    => 'SF CLASE CUENTA PARA EL RECAUDO',
                                              UN_MODULO 	 =>	PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                              UN_FECHA_PAR => SYSDATE,
                                              UN_IND_MAYUS => 0
                                              ),'SIN CONFIGURAR');    

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

    BEGIN
     BEGIN

        MI_CAMPOS :='COMPANIA ,
                          ANO ,
                          TIPO ,
                          NUMERO ,
                          FECHA ,
                          HORA,
                          DESCRIPCION ,
                          TERCERO ,
                          SUCURSAL ,
                          CENTRO_COSTO,
                          AUXILIAR,
                          VLR_BASE ,
                          ANULADO ,
                          VLR_DOCUMENTO, 
                          FECHA_VCN_DOC ,
                          CREATED_BY ,
                          DATE_CREATED';

        MI_VALORES := 'SELECT COMPANIA ,
                                  '||EXTRACT (YEAR FROM UN_FECHA)||' ,
                                  '''||MI_CPTERECAUDO||''' TIPO ,
                                  '||MI_NUMERO||' ,
                                  '''||UN_FECHA||''' ,
                                  SYSDATE,
                                  '''||UN_DESCRIPCION||''' ,
                                  TERCERO ,
                                  SUCURSAL ,
                                  CENTRO_COSTO,
                                  AUXILIAR,
                                  0 ,
                                  0 ,
                                  VLR_DOCUMENTO,
                                  FECHA_VCN_DOC ,
                                  CREATED_BY ,
                                  SYSDATE
                             FROM COMPROBANTE_CNT
                             WHERE COMPANIA = '''||UN_COMPANIA||'''
                               AND ANO      = '||UN_ANIO||'
                               AND TIPO     = '''||MI_CPTECAUSACION||'''
                               AND NUMERO   = '||UN_NUMERO||' ';


                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA  => 'COMPROBANTE_CNT',
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
            MI_MSGERROR(2).VALOR := MI_CPTERECAUDO;
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSCOMPRINGRESO
                                            ,UN_REEMPLAZOS  => MI_MSGERROR);
        END;                                                                       

   MI_CI_FINANCIACION :=NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   =>UN_COMPANIA 	  ,
                                                  UN_NOMBRE     =>'SF CONCEPTO DE INTERES - ACUERDO DE PAGO',
                                                  UN_MODULO     =>PCK_DATOS.FC_MODULOFACTURACIONGENERAL(),
                                                  UN_FECHA_PAR  =>SYSDATE,
                                                  UN_IND_MAYUS  =>0),'SIN CONFIGURAR');



   IF MI_CI_FINANCIACION = 'SIN CONFIGURAR' THEN
      BEGIN
        BEGIN
           RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        END;
           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                                     UN_ERROR_COD =>PCK_ERRORES.MENS_DISTRI_PARAMACUERDOPAGO);

      END;
   END IF;



 IF MI_CAUSARFACTRECAUDO IN (0)THEN
   BEGIN
       SELECT ABS(TOTAL_CUOTA)
       INTO MI_VALORINTERES
       FROM SF_DETALLE_CUOTAABONO
       WHERE COMPANIA = UN_COMPANIA
         AND TIPO_ABONO = UN_TIPOABONO
         AND CODIGO_ABONO = UN_NUMEROABONO
         AND CUOTA = UN_CUOTABONO
         AND CONCEPTO = MI_CI_FINANCIACION;     

      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_VALORINTERES := 0;
   END;
 END IF;

---7710297_FACGENERAL (11/05/2022 MROSERO)
-- UN_VLRABONO Ya trae la informacion con los intereses
  --MI_VALOR := UN_VLRABONO + MI_VALORINTERES;
  MI_VALOR := UN_VLRABONO;     
    --Crea el detalle de la partida debito del comprobante

    SELECT CENTRO_COSTO,AUXILIAR
    INTO   MI_CENTROCOSTO,MI_AUXILIAR
    FROM COMPROBANTE_CNT
    WHERE COMPANIA = UN_COMPANIA
      AND ANO      = UN_ANIO
      AND TIPO     = MI_CPTECAUSACION
      AND NUMERO   = UN_NUMERO;


    BEGIN
     BEGIN
        MI_CAMPOS := 'COMPANIA,
                      ANO,
                      TIPO_CPTE,
                      COMPROBANTE,
                      CUENTA,
                      CONSECUTIVO,
                      NATURALEZA,
                      FECHA,
                      HORA, 
                      DESCRIPCION,
                      TERCERO,
                      SUCURSAL,
                      CENTRO_COSTO,
                      AUXILIAR,
                      VALOR_DEBITO,
                      VALOR_CREDITO';

        MI_VALORES := ''''||UN_COMPANIA||''',
                       '||EXTRACT(YEAR FROM UN_FECHA)||',
                       '''||MI_CPTERECAUDO||''',
                       '||MI_NUMERO||',
                       '''||UN_CUENTA||''',
                       '||MI_CONSECUTIVODET||',
                       ''D'',
                       '''||UN_FECHA||''',
                       '''||UN_FECHA||''',
                       '''||UN_DESCRIPCION||''',
                       '''||UN_TERCERO||''',
                       '''||UN_SUCURSAL||''',
                       '''||MI_CENTROCOSTO||''',
                       '''||MI_AUXILIAR||''',
                       '||MI_VALOR||',
                       0';         

                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                      UN_TABLA => 'DETALLE_COMPROBANTE_CNT',
                                      UN_ACCION => 'I',
                                      UN_CAMPOS => MI_CAMPOS,
                                      UN_VALORES => MI_VALORES
                                    );                       

                MI_CONSECUTIVODET := MI_CONSECUTIVODET + 1;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
            END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
            MI_MSGERROR(1).CLAVE := 'NUMERO';
            MI_MSGERROR(1).VALOR := MI_NUMERO;
            MI_MSGERROR(2).CLAVE := 'TIPO';
            MI_MSGERROR(2).VALOR := MI_CPTERECAUDO;
            PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD => SQLCODE,
                UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_INSDETACOMPCNT);
        END;    

   --*Se toma el comprobante que se desea afectar para obtener los detalles y finalizar la construcción de comprobante de ingreso     

  IF MI_CAUSARFACTRECAUDO NOT IN (0)THEN 

      MI_STRSQL := 'SELECT VALOR_DEBITO - NVL(DEBITO_AFECTADO, 0) VALOR,                     
                         DETALLE_COMPROBANTE_CNT.ANO,
                         DETALLE_COMPROBANTE_CNT.CUENTA,     
                         DETALLE_COMPROBANTE_CNT.NATURALEZA   ,
                         DETALLE_COMPROBANTE_CNT.CUENTAPPTAL  ,                  
                         DETALLE_COMPROBANTE_CNT.CONSECUTIVO  ,
                         DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO,
                         DETALLE_COMPROBANTE_CNT.REFERENCIA
                     FROM DETALLE_COMPROBANTE_CNT 
                        LEFT JOIN PLAN_CONTABLE 
                              ON  DETALLE_COMPROBANTE_CNT.COMPANIA   = PLAN_CONTABLE.COMPANIA 
                             AND  DETALLE_COMPROBANTE_CNT.ANO        = PLAN_CONTABLE.ANO 
                             AND  DETALLE_COMPROBANTE_CNT.CUENTA     = PLAN_CONTABLE.CODIGO 
                     WHERE  DETALLE_COMPROBANTE_CNT.COMPANIA    = '''||UN_COMPANIA||'''
                       AND  DETALLE_COMPROBANTE_CNT.TIPO_CPTE   = '''||MI_CPTECAUSACION||''' 
                       AND  DETALLE_COMPROBANTE_CNT.COMPROBANTE = '||UN_NUMERO||'
                       AND  CLASECUENTA                         IN ('''||MI_CLCUENTA||''') 
                       AND  VALOR_DEBITO                        <> 0';

       OPEN MI_RS FOR MI_STRSQL;                   
       LOOP
            FETCH MI_RS INTO MI_VALOR,MI_ANIOAFECT,MI_CUENTA,MI_NATURALEZA,MI_CUENTAPPTAL,MI_CONSECUTIVO,MI_FUENTERECURSO,MI_REFERENCIA;
            EXIT WHEN MI_RS%NOTFOUND;

          MI_CAMPOS := 'COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CUENTA,CONSECUTIVO,NATURALEZA,VALOR_DEBITO, 
                        VALOR_CREDITO,FECHA,HORA,EJECUCION_DEBITO,EJECUCION_CREDITO,DESCRIPCION,CUENTAPPTAL,TERCERO, 
                        SUCURSAL,CENTRO_COSTO,AUXILIAR,ANO_AFECT,TIPO_CPTE_AFECT,CMPTE_AFECTADO,CONSECUTIVOAFECTADO,FUENTE_RECURSO,REFERENCIA';



          MI_VALORES := ' '''||UN_COMPANIA||''',
                          '||EXTRACT(YEAR FROM UN_FECHA)||',
                          '''||MI_CPTERECAUDO||''',
                          '||MI_NUMERO||',
                          '''||MI_CUENTA||''',
                          '||MI_CONSECUTIVODET||',
                          '''||MI_NATURALEZA||''',
                          0,
                          '||MI_VALOR||',
                          '''||UN_FECHA||''',
                          SYSDATE,
                          0,
                          '||MI_VALOR||',
                          '''||UN_DESCRIPCION||''',
                          '''||MI_CUENTAPPTAL||''',
                          '''||UN_TERCERO||''',
                          '''||UN_SUCURSAL||''',
                          '''||MI_CENTROCOSTO||''',
                          '''||MI_AUXILIAR||''',
                          '||MI_ANIOAFECT||',
                          '''||MI_CPTECAUSACION||''',
                          '||UN_NUMERO||',
                          '||MI_CONSECUTIVO||',
                          ''' || MI_FUENTERECURSO || ''',
                          ''' || MI_REFERENCIA || ''' ';
           BEGIN
              BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
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
                    MI_MSGERROR(1).VALOR := MI_NUMERO;
                    MI_MSGERROR(2).CLAVE := 'TIPO';
                    MI_MSGERROR(2).VALOR := MI_CPTERECAUDO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_INSDETACOMPCNT);
            END;

            MI_CONSECUTIVODET := MI_CONSECUTIVODET + 1;             
       END LOOP;     

  ELSE 
--- 7710297_FACGENERAL (11/05/2022 MROSERO)
  MI_STRSQL := 'SELECT ABS(SF_DETALLE_CUOTAABONO.TOTAL_CUOTA) VALOR,
                       DETALLE_COMPROBANTE_CNT.ANO, 
                       DETALLE_COMPROBANTE_CNT.CUENTA,     
                       DETALLE_COMPROBANTE_CNT.NATURALEZA   ,
                       DETALLE_COMPROBANTE_CNT.CUENTAPPTAL  ,                  
                       DETALLE_COMPROBANTE_CNT.CONSECUTIVO  ,
                       DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO,
                       DETALLE_COMPROBANTE_CNT.REFERENCIA
                FROM SF_DETALLE_CUOTAABONO 
                    INNER JOIN SF_CONCEPTOS 
                       ON SF_DETALLE_CUOTAABONO.COMPANIA   = SF_CONCEPTOS.COMPANIA
                      AND SF_DETALLE_CUOTAABONO.CONCEPTO   = SF_CONCEPTOS.CODIGO
                      AND SF_DETALLE_CUOTAABONO.TIPOFACT   = SF_CONCEPTOS.TIPOCOBRO
                      AND SF_DETALLE_CUOTAABONO.ANO        = SF_CONCEPTOS.ANO
                    INNER JOIN SF_FACTURA 
                       ON SF_DETALLE_CUOTAABONO.COMPANIA   = SF_FACTURA.COMPANIA                          
                      AND SF_DETALLE_CUOTAABONO.ANO        = SF_FACTURA.ANO 
                      AND SF_DETALLE_CUOTAABONO.TIPOFACT   = SF_FACTURA.TIPO_FACTURA
                      AND SF_DETALLE_CUOTAABONO.NROFACT    = SF_FACTURA.NUMERO_FACTURA
                    INNER JOIN DETALLE_COMPROBANTE_CNT 
                       ON DETALLE_COMPROBANTE_CNT.COMPANIA    = SF_FACTURA.COMPANIA
                      AND DETALLE_COMPROBANTE_CNT.ANO         = SF_FACTURA.ANO
                      AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE   = SF_FACTURA.TIPO_CPTE
                      AND DETALLE_COMPROBANTE_CNT.COMPROBANTE = SF_FACTURA.NRO_CPTE
                      AND DETALLE_COMPROBANTE_CNT.CUENTA      = SF_CONCEPTOS.CUENTADEBITOBASE
                WHERE SF_DETALLE_CUOTAABONO.COMPANIA      = '''||UN_COMPANIA||'''
                  AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE   = '''||MI_CPTECAUSACION||'''
                  AND DETALLE_COMPROBANTE_CNT.COMPROBANTE ='||UN_NUMERO||'
                  AND SF_DETALLE_CUOTAABONO.CUOTA         = '||UN_CUOTABONO||'';

       OPEN MI_RS FOR MI_STRSQL;                   
       LOOP
            FETCH MI_RS INTO MI_VALOR,MI_ANIOAFECT,MI_CUENTA,MI_NATURALEZA,MI_CUENTAPPTAL,MI_CONSECUTIVO,MI_FUENTERECURSO,MI_REFERENCIA;
            EXIT WHEN MI_RS%NOTFOUND;

          MI_CAMPOS := 'COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CUENTA,CONSECUTIVO,NATURALEZA,VALOR_DEBITO, 
                        VALOR_CREDITO,FECHA,HORA,EJECUCION_DEBITO,EJECUCION_CREDITO,DESCRIPCION,CUENTAPPTAL,TERCERO, 
                        SUCURSAL,CENTRO_COSTO,AUXILIAR,ANO_AFECT,TIPO_CPTE_AFECT,CMPTE_AFECTADO,CONSECUTIVOAFECTADO,FUENTE_RECURSO,REFERENCIA';


          MI_VALORES := ' '''||UN_COMPANIA||''',
                          '||EXTRACT(YEAR FROM UN_FECHA)||',
                          '''||MI_CPTERECAUDO||''',
                          '||MI_NUMERO||',
                          '''||MI_CUENTA||''',
                          '||MI_CONSECUTIVODET||',
                          '''||MI_NATURALEZA||''',
                          0,
                          '||MI_VALOR||',
                          '''||UN_FECHA||''',
                          SYSDATE,
                          0,
                          '||MI_VALOR||',
                          '''||UN_DESCRIPCION||''',
                          '''||MI_CUENTAPPTAL||''',
                          '''||UN_TERCERO||''',
                          '''||UN_SUCURSAL||''',
                          '''||MI_CENTROCOSTO||''',
                          '''||MI_AUXILIAR||''',
                          '||MI_ANIOAFECT||',
                          '''||MI_CPTECAUSACION||''',
                          '||UN_NUMERO||',
                          '||MI_CONSECUTIVO||',
                          ''' || MI_FUENTERECURSO || ''',
                          ''' || MI_REFERENCIA || ''' ';
           BEGIN
              BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
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
                    MI_MSGERROR(1).VALOR := MI_NUMERO;
                    MI_MSGERROR(2).CLAVE := 'TIPO';
                    MI_MSGERROR(2).VALOR := MI_CPTERECAUDO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_INSDETACOMPCNT);
            END;

            MI_CONSECUTIVODET := MI_CONSECUTIVODET + 1;             
       END LOOP;                  


    --Creacion del detalle con cuentas 4 para los intereses
---7710297_FACGENERAL (11/05/2022 MROSERO)
    MI_CUENTA4 := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                              UN_NOMBRE    => 'CREACION DEL DETALLE INTERESES CON CUENTAS 4',
                                              UN_MODULO 	 =>	PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                              UN_FECHA_PAR => SYSDATE,
                                              UN_IND_MAYUS => 0
                                              ),'NO');        
 IF MI_CUENTA4 = 'SI' THEN       
    MI_STRSQL := 'SELECT ABS(SF_DETALLE_CUOTAABONO.TOTAL_CUOTA) VALOR,
                        SF_DETALLE_CUOTAABONO.ANO,
                        SF_CONCEPTOS.CUENTACREDITOBASE,
                        PLAN_CONTABLE.NATURALEZA,
                        PLAN_CONTABLE.CUENTA_PPTAL
                FROM SF_DETALLE_CUOTAABONO 
                 INNER JOIN SF_CONCEPTOS 
                    ON SF_DETALLE_CUOTAABONO.COMPANIA   = SF_CONCEPTOS.COMPANIA
                   AND SF_DETALLE_CUOTAABONO.CONCEPTO   = SF_CONCEPTOS.CODIGO
                   AND SF_DETALLE_CUOTAABONO.TIPOFACT   = SF_CONCEPTOS.TIPOCOBRO
                   AND SF_DETALLE_CUOTAABONO.ANO        = SF_CONCEPTOS.ANO
                 INNER JOIN SF_DETALLE_FACTURA 
                    ON  SF_DETALLE_FACTURA.COMPANIA    = SF_DETALLE_CUOTAABONO.COMPANIA
                   AND SF_DETALLE_FACTURA.ANO          = SF_DETALLE_CUOTAABONO.ANO
                   AND SF_DETALLE_FACTURA.TIPO_FACTURA = SF_DETALLE_CUOTAABONO.TIPOFACT
                   AND SF_DETALLE_FACTURA.FACTURA      = SF_DETALLE_CUOTAABONO.NROFACT        
                   AND SF_DETALLE_FACTURA.CONCEPTO     = SF_DETALLE_CUOTAABONO.CONCEPTO
                 INNER JOIN PLAN_CONTABLE 
                    ON PLAN_CONTABLE.COMPANIA = SF_CONCEPTOS.COMPANIA
                   AND PLAN_CONTABLE.ANO      = SF_CONCEPTOS.ANO
                   AND PLAN_CONTABLE.CODIGO   = SF_CONCEPTOS.CUENTACREDITOBASE
                WHERE SF_DETALLE_CUOTAABONO.COMPANIA     = '''||UN_COMPANIA||'''
                  AND SF_DETALLE_CUOTAABONO.CODIGO_ABONO = '||UN_NUMEROABONO||'
                  AND SF_DETALLE_CUOTAABONO.CUOTA        = '||UN_CUOTABONO||' 
                  AND SF_DETALLE_CUOTAABONO.CONCEPTO     = '||MI_CI_FINANCIACION||' ';

       OPEN MI_RS FOR MI_STRSQL;                   
       LOOP
            FETCH MI_RS INTO MI_VALOR,MI_ANIOAFECT,MI_CUENTA,MI_NATURALEZA,MI_CUENTAPPTAL;
            EXIT WHEN MI_RS%NOTFOUND;

          MI_CAMPOS := 'COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CUENTA,CONSECUTIVO,NATURALEZA,VALOR_DEBITO, 
                        VALOR_CREDITO,FECHA,HORA,EJECUCION_DEBITO,EJECUCION_CREDITO,DESCRIPCION,CUENTAPPTAL,TERCERO, 
                        SUCURSAL,CENTRO_COSTO,AUXILIAR,ANO_AFECT,TIPO_CPTE_AFECT,CMPTE_AFECTADO,CONSECUTIVOAFECTADO';



          MI_VALORES := ' '''||UN_COMPANIA||''',
                          '||EXTRACT(YEAR FROM UN_FECHA)||',
                          '''||MI_CPTERECAUDO||''',
                          '||MI_NUMERO||',
                          '''||MI_CUENTA||''',
                          '||MI_CONSECUTIVODET||',
                          '''||MI_NATURALEZA||''',
                          0,
                          '||MI_VALOR||',
                          '''||UN_FECHA||''',
                          SYSDATE,
                          0,
                          '||MI_VALOR||',
                          '''||UN_DESCRIPCION||''',
                          '''||MI_CUENTAPPTAL||''',
                          '''||UN_TERCERO||''',
                          '''||UN_SUCURSAL||''',
                          '''||MI_CENTROCOSTO||''',
                          '''||MI_AUXILIAR||''',
                          '||MI_ANIOAFECT||',
                          '''||MI_CPTECAUSACION||''',
                          '||UN_NUMERO||',
                          '||MI_CONSECUTIVO||'';
           BEGIN
              BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
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
                    MI_MSGERROR(1).VALOR := MI_NUMERO;
                    MI_MSGERROR(2).CLAVE := 'TIPO';
                    MI_MSGERROR(2).VALOR := MI_CPTERECAUDO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_INSDETACOMPCNT);
            END;

            MI_CONSECUTIVODET := MI_CONSECUTIVODET + 1;             
       END LOOP;                    
 END IF; 
  END IF; 
       PCK_FACT_GENERAL_COM3.PR_CREARCOMPROBANTEPPTAL(UN_COMPANIA      => UN_COMPANIA,
                                                    UN_TIPOCPTE        => MI_CPTERECAUDO,
                                                    UN_ANIO            => EXTRACT (YEAR FROM UN_FECHA),
                                                    UN_NUMERO          => MI_NUMERO,
                                                    UN_NUMERO_AFEC     => UN_NUMERO,
                                                    UN_TIPO_PTE_AFEC   => MI_CPTECAUSACION,
                                                    UN_DESCRIPCION     => UN_DESCRIPCION,
                                                    UN_USUARIO         => UN_USUARIO,
                                                    UN_VALORRECAUDO    => UN_VLRABONO);


    BEGIN
     BEGIN

         MI_CAMPOS := 'FECHA_PAGO = '''||UN_FECHA||''',
                           ANODOC = '||EXTRACT(YEAR FROM UN_FECHA)||',
                          TIPODOC = '''||MI_CPTERECAUDO||''',
                           NRODOC = '||MI_NUMERO||',
                       BANCO_PAGO = '''||UN_CUENTA||''',
                           ESTADO = ''R'' ';                                                    

        MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||'''
                  AND  TIPO_ABONO = '''||UN_TIPOABONO||'''
                  AND  CODIGO_ABONO = '||UN_NUMEROABONO||'
                  AND  CUOTA = '||UN_CUOTABONO ;

       PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_ACCION    =>'M',
                                         UN_TABLA     =>'SF_DETALLE_ABONO',
                                         UN_CAMPOS    =>MI_CAMPOS,
                                         UN_CONDICION =>MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
        MI_MSGERROR(1).CLAVE := 'FACTURA';
        MI_MSGERROR(1).VALOR := UN_NUMERO;
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTESTADOFACTURA
                                    ,UN_REEMPLAZOS  => MI_MSGERROR);
    END;                  

    RETURN MI_NUMERO;
END FC_GENERARINGRESOABONO;

FUNCTION FC_VALIDARCUENTACONCEPTO
   /*
  NAME              : FC_VALIDARCUENTACONCEPTO 
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
  DATE MIGRADOR     : 29/07/2019
  TIME              : 16:20 PM
  MODIFIER          :
  DATE MODIFIED     :
  TIME              :
  SOURCE MODULE     : FACTURACION_GENERAL
  DESCRIPTION       : Permite validar las cuentas de los conceptos si tienen o no movimiento

  @NAME:  validarCuentaConcepto
  */
( 
  UN_COMPANIA        IN SF_CONCEPTOS.COMPANIA%TYPE,
  UN_ANO             IN SF_CONCEPTOS.ANO%TYPE,
  UN_TIPOCOBRO       IN SF_CONCEPTOS.TIPOCOBRO%TYPE,
  UN_CONCEPTO        IN SF_CONCEPTOS.CODIGO%TYPE
  )
RETURN VARCHAR2
AS
    MI_NATURALEZA  VARCHAR2(1 CHAR);
    MI_TARIFATERCERO    VARCHAR2(2 CHAR);

BEGIN

 MI_TARIFATERCERO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                    UN_NOMBRE    => 'SF MANEJA TARIFA POR TERCERO',
                                    UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                    UN_FECHA_PAR => SYSDATE,
                                    UN_IND_MAYUS =>0),'NO'); 
                                    
FOR RS IN(
  SELECT CUENTADEBITOBASE
        ,CUENTACREDITOBASE
        ,APLICAIVA
        ,CUENTADEBITOIVA
        ,CUENTACREDITOIVA
        ,APLICARETEFUENTE
        ,CUENTADEBITORETE
        ,CUENTACREDITORETE
        ,APLICADESCUENTO
        ,CUENTADEBITODESCUENTO
        ,CUENTACREDITODESCUENTO
        ,APLICAICA
        ,CUENTADEBITOICA
        ,CUENTACREDITOICA
        ,CUENTADEBITOPUBLICO
        ,CUENTACREDITOPUBLICO
        ,CUENTADEBITOPRIVADO
        ,CUENTACREDITOPRIVADO
    FROM SF_CONCEPTOS        
    WHERE COMPANIA = UN_COMPANIA
    AND ANO        = UN_ANO
    AND TIPOCOBRO  = UN_TIPOCOBRO
    AND CODIGO     = UN_CONCEPTO) LOOP

    IF MI_TARIFATERCERO='SI'  THEN
            IF PCK_FACT_GENERAL_COM3.GL_TIPOENTIDAD = 1 THEN
                 MI_NATURALEZA := PCK_CONTABILIDAD.FC_VALIDARCUENTAUTILIZAR(UN_COMPANIA   =>UN_COMPANIA,
                                                                           UN_ANO        =>UN_ANO,
                                                                           UN_CUENTA     =>RS.CUENTADEBITOPRIVADO,
                                                                           UN_VALIDABLOQUEADO =>-1);  
                                                                            
                  MI_NATURALEZA := PCK_CONTABILIDAD.FC_VALIDARCUENTAUTILIZAR(UN_COMPANIA   =>UN_COMPANIA,
                                                                           UN_ANO        =>UN_ANO,
                                                                           UN_CUENTA     =>RS.CUENTACREDITOPRIVADO,
                                                                           UN_VALIDABLOQUEADO =>-1);    
                                                                           
                ELSIF PCK_FACT_GENERAL_COM3.GL_TIPOENTIDAD = 2 THEN
                  MI_NATURALEZA := PCK_CONTABILIDAD.FC_VALIDARCUENTAUTILIZAR(UN_COMPANIA   =>UN_COMPANIA,
                                                                           UN_ANO        =>UN_ANO,
                                                                           UN_CUENTA     =>RS.CUENTADEBITOPUBLICO,
                                                                           UN_VALIDABLOQUEADO =>-1);  
                                                                            
                  MI_NATURALEZA := PCK_CONTABILIDAD.FC_VALIDARCUENTAUTILIZAR(UN_COMPANIA   =>UN_COMPANIA,
                                                                           UN_ANO        =>UN_ANO,
                                                                           UN_CUENTA     =>RS.CUENTACREDITOPUBLICO,
                                                                           UN_VALIDABLOQUEADO =>-1); 
             END IF;  
      ELSE                                                            
        MI_NATURALEZA := PCK_CONTABILIDAD.FC_VALIDARCUENTAUTILIZAR(UN_COMPANIA   =>UN_COMPANIA,
                                                                   UN_ANO        =>UN_ANO,
                                                                   UN_CUENTA     =>RS.CUENTADEBITOBASE,
                                                                   UN_VALIDABLOQUEADO =>-1);   
    
        MI_NATURALEZA := PCK_CONTABILIDAD.FC_VALIDARCUENTAUTILIZAR(UN_COMPANIA   =>UN_COMPANIA,
                                                                   UN_ANO        =>UN_ANO,
                                                                   UN_CUENTA     =>RS.CUENTACREDITOBASE,
                                                                   UN_VALIDABLOQUEADO =>-1);                                                               
     END IF; 
     
 --******************** CUENTA IVA SI INDICADOR SE ENCUENTRA ACTIVO   *******************
   IF RS.APLICAIVA NOT IN (0)THEN
        MI_NATURALEZA := PCK_CONTABILIDAD.FC_VALIDARCUENTAUTILIZAR(UN_COMPANIA   =>UN_COMPANIA,
                                                               UN_ANO        =>UN_ANO,
                                                               UN_CUENTA     =>RS.CUENTADEBITOIVA,
                                                               UN_VALIDABLOQUEADO =>-1);

        MI_NATURALEZA := PCK_CONTABILIDAD.FC_VALIDARCUENTAUTILIZAR(UN_COMPANIA   =>UN_COMPANIA,
                                                               UN_ANO        =>UN_ANO,
                                                               UN_CUENTA     =>RS.CUENTACREDITOIVA,
                                                               UN_VALIDABLOQUEADO =>-1);                                                               
   END IF;

 --******************** CUENTA RETEFUENTE SI INDICADOR SE ENCUENTRA ACTIVO   *******************
  IF RS.APLICARETEFUENTE NOT IN (0) THEN

        MI_NATURALEZA := PCK_CONTABILIDAD.FC_VALIDARCUENTAUTILIZAR(UN_COMPANIA   =>UN_COMPANIA,
                                                               UN_ANO        =>UN_ANO,
                                                               UN_CUENTA     =>RS.CUENTADEBITORETE,
                                                               UN_VALIDABLOQUEADO =>-1);

        MI_NATURALEZA := PCK_CONTABILIDAD.FC_VALIDARCUENTAUTILIZAR(UN_COMPANIA   =>UN_COMPANIA,
                                                               UN_ANO        =>UN_ANO,
                                                               UN_CUENTA     =>RS.CUENTACREDITORETE,
                                                               UN_VALIDABLOQUEADO =>-1);       

  END IF;

 --******************** CUENTA DESCUENTO SI INDICADOR SE ENCUENTRA ACTIVO   ******************* 
  IF RS.APLICADESCUENTO NOT IN (0) THEN   

        MI_NATURALEZA := PCK_CONTABILIDAD.FC_VALIDARCUENTAUTILIZAR(UN_COMPANIA   =>UN_COMPANIA,
                                                               UN_ANO        =>UN_ANO,
                                                               UN_CUENTA     =>RS.CUENTADEBITODESCUENTO,
                                                               UN_VALIDABLOQUEADO =>-1);

        MI_NATURALEZA := PCK_CONTABILIDAD.FC_VALIDARCUENTAUTILIZAR(UN_COMPANIA   =>UN_COMPANIA,
                                                               UN_ANO        =>UN_ANO,
                                                               UN_CUENTA     =>RS.CUENTACREDITODESCUENTO,
                                                               UN_VALIDABLOQUEADO =>-1);       
  END IF;

 --******************** CUENTA ICA SI INDICADOR SE ENCUENTRA ACTIVO   ******************* 
  IF RS.APLICAICA NOT IN (0) THEN   

        MI_NATURALEZA := PCK_CONTABILIDAD.FC_VALIDARCUENTAUTILIZAR(UN_COMPANIA   =>UN_COMPANIA,
                                                               UN_ANO        =>UN_ANO,
                                                               UN_CUENTA     =>RS.CUENTADEBITOICA,
                                                               UN_VALIDABLOQUEADO =>-1);

        MI_NATURALEZA := PCK_CONTABILIDAD.FC_VALIDARCUENTAUTILIZAR(UN_COMPANIA   =>UN_COMPANIA,
                                                               UN_ANO        =>UN_ANO,
                                                               UN_CUENTA     =>RS.CUENTACREDITOICA,
                                                               UN_VALIDABLOQUEADO =>-1);         
  END IF;

  END LOOP;

RETURN MI_NATURALEZA;

END FC_VALIDARCUENTACONCEPTO;
PROCEDURE PR_CARGAR_TARIFASCONCEPTOS
/*
    NAME              : PR_CARGAR_TARIFASCONCEPTOS
    AUTHOR MIGRACION  : MARIA CAMILA ROSERO PAZOS
    DATE MIGRADOR     : 01/02/2024                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MODIFIED     : 09/04/2024
    TIME              : 6:00PM
    MODIFICATIONS     : SE AJUSTA EL PROCEDIMIENTO PARA QUE INSERTE O ACTUALICE
    DESCRIPTION       : PROCEDIMIENTO QUE CARGA TARIFAS POR CONCEPTOS

    @NAME:    PR_CARGAR_TARIFASCONCEPTOS
    @METHOD:  POST
    */
( 
  UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CADENAPLAN  IN CLOB,
  UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO
)  
AS 
MI_DATOS_FILA         PCK_SYSMAN_UTL.T_SPLIT;
MI_DATOS_COLUMNAS     PCK_SYSMAN_UTL.T_SPLIT;
MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
MI_CODIGO             NUMBER := 0;
MI_EXISTE             NUMBER := 0;
MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
MI_FECHAI             DATE;
MI_FECHAF             DATE;
BEGIN
    MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENAPLAN,
                                                 UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);

  <<SUBIR_TARIFASPORCONCEPTO>>
  FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST 
  LOOP
    MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                     UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);
                                                      
    BEGIN
      BEGIN                                                    
        SELECT COUNT (*) CODIGO
          INTO MI_CODIGO
          FROM LISTATARIFA 
         WHERE COMPANIA= UN_COMPANIA
           AND CODIGO= MI_DATOS_COLUMNAS(3)
           AND FECHAINICIAL= TO_DATE(MI_DATOS_COLUMNAS(5), 'DD/MM/YYYY')
           AND FECHAFINAL= TO_DATE(MI_DATOS_COLUMNAS(6), 'DD/MM/YYYY');                                              
      END;       

      IF MI_CODIGO = 0 THEN 
        MI_CAMPOS := 'COMPANIA
                      ,ANO
                      ,CODIGO
                      ,DESCRIPCION
                      ,FECHAINICIAL
                      ,FECHAFINAL             
                      ,CREATED_BY
                      ,DATE_CREATED';

        MI_VALORES := ' ''' || UN_COMPANIA ||'''
                      ,''' || MI_DATOS_COLUMNAS(1) ||'''
                      ,''' || MI_DATOS_COLUMNAS(3) ||'''
                      ,''' || 'LISTA No ' || MI_DATOS_COLUMNAS(3) ||'''
                      ,''' || 
                          CASE 
                              WHEN MI_DATOS_COLUMNAS(5) = 'NoDato' OR MI_DATOS_COLUMNAS(5) IS NULL OR MI_DATOS_COLUMNAS(5) = 'null' OR MI_DATOS_COLUMNAS(5) = 'NULL' 
                              THEN NULL
                              ELSE NVL(TO_DATE(MI_DATOS_COLUMNAS(5), 'DD/MM/YYYY'), NULL) 
                          END
                      ||'''
                      ,''' ||  
                          CASE 
                              WHEN MI_DATOS_COLUMNAS(6) = 'NoDato' OR MI_DATOS_COLUMNAS(6) IS NULL OR MI_DATOS_COLUMNAS(6) = 'null' OR MI_DATOS_COLUMNAS(6) = 'NULL' 
                              THEN NULL
                              ELSE NVL(TO_DATE(MI_DATOS_COLUMNAS(6), 'DD/MM/YYYY'), NULL)
                          END
                      ||'''
                      ,''' || UN_USUARIO ||'''
                       ,SYSDATE ';
          BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'LISTATARIFA'
                                                   ,UN_ACCION  => 'I'
                                                   ,UN_CAMPOS  => MI_CAMPOS
                                                   ,UN_VALORES => MI_VALORES);

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
               RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
               END;
      END IF;
    BEGIN
      SELECT COUNT (*),FECHAINICIAL,FECHAFINAL
        INTO MI_EXISTE,MI_FECHAI,MI_FECHAF
        FROM TARIFAS_POR_CONCEPTO
       WHERE COMPANIA = UN_COMPANIA
         AND ANO = MI_DATOS_COLUMNAS(1)
         AND CONCEPTO = MI_DATOS_COLUMNAS(2)
         AND NUMEROLISTA = MI_DATOS_COLUMNAS(3)
         GROUP BY FECHAINICIAL, FECHAFINAL;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_EXISTE := 0;
    END;
    IF(MI_EXISTE = 0)THEN
      MI_CAMPOS := 'COMPANIA
                    ,ANO
                    ,CONCEPTO
                    ,NUMEROLISTA
                    ,VALOR
                    ,FECHAINICIAL
                    ,FECHAFINAL             
                    ,CREATED_BY
                    ,DATE_CREATED';

      MI_VALORES := ' '''|| UN_COMPANIA ||'''
                    ,'''|| MI_DATOS_COLUMNAS(1) ||'''
                    ,'''|| MI_DATOS_COLUMNAS(2) ||'''
                    ,'''|| MI_DATOS_COLUMNAS(3) ||'''
                    , '|| MI_DATOS_COLUMNAS(4) ||'
                    ,''' ||                           
                    CASE 
                      WHEN MI_DATOS_COLUMNAS(5) IS NULL OR MI_DATOS_COLUMNAS(5) = 'NoDato' OR MI_DATOS_COLUMNAS(5) = 'null' OR MI_DATOS_COLUMNAS(5) = 'NULL' 
                      THEN NULL
                      ELSE TO_DATE(MI_DATOS_COLUMNAS(5), 'DD/MM/YYYY') 
                    END ||'''
                    ,''' ||                          
                    CASE 
                      WHEN MI_DATOS_COLUMNAS(6) IS NULL OR MI_DATOS_COLUMNAS(6) = 'NoDato' OR MI_DATOS_COLUMNAS(6) = 'null' OR MI_DATOS_COLUMNAS(6) = 'NULL' 
                      THEN NULL
                      ELSE TO_DATE(MI_DATOS_COLUMNAS(6), 'DD/MM/YYYY')
                    END ||'''
                    ,'''|| UN_USUARIO ||'''
                   ,SYSDATE ';        

        BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'TARIFAS_POR_CONCEPTO'
                                                   ,UN_ACCION  => 'I'
                                                   ,UN_CAMPOS  => MI_CAMPOS
                                                   ,UN_VALORES => MI_VALORES);
    
             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
             RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
        END;
    ELSE        
                    MI_CAMPOS := 'VALOR          = '|| MI_DATOS_COLUMNAS(4) ||',
                                  FECHAINICIAL   = ''' || CASE WHEN MI_DATOS_COLUMNAS(5) IS NULL OR MI_DATOS_COLUMNAS(5) = 'NoDato' OR MI_DATOS_COLUMNAS(5) = 'null' OR MI_DATOS_COLUMNAS(5) = 'NULL' 
                                                        THEN NULL
                                                        ELSE TO_DATE(MI_DATOS_COLUMNAS(5), 'DD/MM/YYYY') 
                                                       END ||''',
                                  FECHAFINAL     = ''' ||CASE WHEN MI_DATOS_COLUMNAS(6) IS NULL OR MI_DATOS_COLUMNAS(6) = 'NoDato' OR MI_DATOS_COLUMNAS(6) = 'null' OR MI_DATOS_COLUMNAS(6) = 'NULL' 
                                                        THEN NULL
                                                        ELSE TO_DATE(MI_DATOS_COLUMNAS(6), 'DD/MM/YYYY') 
                                                       END ||''',   
                                  MODIFIED_BY    = '''|| UN_USUARIO ||''',
                                  DATE_MODIFIED  = SYSDATE';
                         
                    MI_CONDICION := 'COMPANIA       =''' ||UN_COMPANIA|| '''
                                    AND ANO         =''' ||MI_DATOS_COLUMNAS(1)|| ''' 
                                    AND CONCEPTO    =''' ||MI_DATOS_COLUMNAS(2)|| '''
                                    AND NUMEROLISTA  =''' ||MI_DATOS_COLUMNAS(3)|| '''
                                    AND FECHAINICIAL =''' ||TO_DATE(MI_FECHAI, 'DD/MM/YYYY')|| '''
                                    AND FECHAFINAL   =''' ||TO_DATE(MI_FECHAF, 'DD/MM/YYYY')|| ''' ';
                    BEGIN
                        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'TARIFAS_POR_CONCEPTO', 
                                                    UN_ACCION    => 'M', 
                                                    UN_CAMPOS    => MI_CAMPOS, 
                                                    UN_CONDICION => MI_CONDICION );
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                    END; 
    END IF;
    END; 
  END LOOP SUBIR_TARIFASPORCONCEPTO;
END PR_CARGAR_TARIFASCONCEPTOS;

FUNCTION FC_DEVOLVER_FACTURAS
/*
      NAME              : FC_DEVOLVER_FACTURAS
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : 
      DATE MIGRADOR     : 17/12/2024
      TIME              :
      SOURCE MODULE     :
      DESCRIPTION       :
      MODIFIER          :
      DATE MODIFIED     :
      TIME MODIFIED     :
      MODIFICATIONS     :
    --NAME:   devolverFacturas
    --METHOD: GET
*/
(
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
  UN_FACTURA        IN SF_OBJETO_COBRO.NRO_FACTURA%TYPE,
  UN_TIPOCOBRO      IN SF_OBJETO_COBRO.TIPOCOBRO%TYPE,
  UN_NUMEROCOBRO    IN SF_OBJETO_COBRO.NRO_FACTURA%TYPE,
  UN_FACTURADO      IN PCK_SUBTIPOS.TI_LOGICO,
  UN_RECAUDO        IN PCK_SUBTIPOS.TI_LOGICO,
  UN_CUENTA         IN PCK_SUBTIPOS.TI_CODIGOCONTA,
  UN_FECHA          IN DATE,
  UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN VARCHAR2 AS 

MI_VALOR   NUMBER(3);
MI_CONSECUTIVO           NUMBER(5,0) := 0;
MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES               PCK_SUBTIPOS.TI_VALORES;
MI_TIPO_DEVOLUCION       VARCHAR2(3 CHAR);
MI_MSGERROR              PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_COMPROBANTE           PCK_SUBTIPOS.TI_ENTERO_LARGO;
 MI_RTA                  PCK_SUBTIPOS.TI_RTA_ACME;
 MI_ANIO                 NUMBER(4,0);
 MI_TERCERO              PCK_SUBTIPOS.TI_TERCERO;
 MI_SUCURSAL             PCK_SUBTIPOS.TI_SUCURSAL;
 MI_DESCRIPCION          DETALLE_COMPROBANTE_CNT.DESCRIPCION%TYPE;
 MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
 MI_TIPO_PAGO            SF_FACTURA.TIPOCPTE_PAGO%TYPE;
 MI_ANIO_PAGO            SF_FACTURA.ANOCPTE_PAGO%TYPE;
 MI_NUMERO_PAGO          SF_FACTURA.NROCPTE_PAGO%TYPE;
 MI_FECHA_SOLICITUD      DATE;

BEGIN
        BEGIN
            SELECT TIPOCPTE_DEVOLUCION
            INTO MI_TIPO_DEVOLUCION
            FROM SF_TIPO_COBRO
            WHERE COMPANIA = UN_COMPANIA
            AND ANO = UN_ANIO
            AND CODIGO = UN_TIPOCOBRO;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
               RETURN 'El campo Tipo Cpte para Devolución en el formulario de Tipo de cobro esta sin configurar';
        END;
        
   BEGIN
    SELECT SF_OBJETO_COBRO.FECHA_SOLICITUD
    INTO MI_FECHA_SOLICITUD
    FROM SF_OBJETO_COBRO
    INNER JOIN SF_FACTURA ON SF_OBJETO_COBRO.COMPANIA = SF_FACTURA.COMPANIA
                         AND SF_OBJETO_COBRO.TIPOCOBRO = SF_FACTURA.TIPOCOBRO
                         AND SF_OBJETO_COBRO.NRO_FACTURA = SF_FACTURA.NUMERO_FACTURA
    WHERE SF_FACTURA.COMPANIA = UN_COMPANIA
      AND SF_FACTURA.TIPO_FACTURA = UN_TIPOCOBRO
      AND SF_FACTURA.NUMERO_FACTURA = UN_FACTURA;
      
        IF UN_FECHA < MI_FECHA_SOLICITUD THEN
    
            RETURN 'La fecha ingresada (' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ') es inferior a la fecha de solicitud (' 
                || TO_CHAR(MI_FECHA_SOLICITUD, 'DD/MM/YYYY') || '). Por favor, ingrese una fecha igual o posterior a la fecha de solicitud.';
        END IF;
    END;

  IF UN_FACTURADO NOT IN (0) AND UN_RECAUDO IN (0) THEN 

   FOR RS IN (SELECT 
               DT.COMPANIA, 
               DT.ANO, 
               DT.TIPO_CPTE, 
               DT.COMPROBANTE, 
               DT.CONSECUTIVO, 
               DT.CUENTA CUENTA_ANT,
               CASE WHEN PC.EQUIVALENTE_DEVOLUCION IS NOT NULL THEN PC.EQUIVALENTE_DEVOLUCION ELSE DT.CUENTA END CUENTA,
               CASE WHEN PC.EQUIVALENTE_DEVOLUCION IS NOT NULL THEN PC.NATURALEZA ELSE DT.NATURALEZA END NATURALEZA, 
               DT.DESCRIPCION, 
               DT.VALOR_DEBITO AS VALOR_CREDITO, 
               DT.VALOR_CREDITO AS VALOR_DEBITO, 
               DT.EJECUCION_DEBITO, 
               DT.EJECUCION_CREDITO, 
               DT.TIPOPPTAL, 
               DT.CENTRO_COSTO, 
               DT.TERCERO, 
               DT.SUCURSAL, 
               DT.AUXILIAR, 
               DT.REFERENCIA, 
               DT.FUENTE_RECURSO,
               DT.BASE_GRAVABLE
            FROM DETALLE_COMPROBANTE_CNT DT
            INNER JOIN PLAN_CONTABLE PC
             ON DT.COMPANIA = PC.COMPANIA
            AND DT.ANO = PC.ANO
            AND DT.CUENTA = PC.CODIGO
            WHERE DT.COMPANIA = UN_COMPANIA
            AND DT.ANO = UN_ANIO
            AND DT.TIPO_CPTE = UN_TIPOCOBRO
            AND DT.COMPROBANTE = UN_NUMEROCOBRO
            ORDER BY CONSECUTIVO)
   LOOP
   
        MI_ANIO := RS.ANO;
        MI_TERCERO := RS.TERCERO;
        MI_SUCURSAL := RS.SUCURSAL;
        MI_DESCRIPCION := 'Devolución ' || RS.DESCRIPCION;

        MI_COMPROBANTE := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'COMPROBANTE_CNT',
                                                                                  UN_CRITERIO => 'COMPANIA = '''||UN_COMPANIA||''' AND ANO = '||UN_ANIO||' AND TIPO = '''||MI_TIPO_DEVOLUCION||'''',
                                                                                  UN_CAMPO    => 'NUMERO', UN_INICIAL => UN_ANIO || '00000' || '1');
   
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
                              EJECUCION_DEBITO,
                              EJECUCION_CREDITO,
                              CENTRO_COSTO,
                              TERCERO,
                              SUCURSAL,
                              AUXILIAR,
                              DESCRIPCION,
                              BASE_GRAVABLE,
                              REFERENCIA,
                              FUENTE_RECURSOS';
                            
                MI_VALORES := ' '''||UN_COMPANIA||'''
                                ,'||RS.ANO||'
                                ,'''||MI_TIPO_DEVOLUCION||'''
                                ,'||MI_COMPROBANTE||'
                                ,'||MI_CONSECUTIVO||'
                                ,'''||RS.CUENTA||'''
                                ,'''||UN_FECHA||'''
                                ,'''||RS.NATURALEZA||'''
                                ,'||RS.VALOR_DEBITO||'
                                ,'||RS.VALOR_CREDITO||'
                                ,'||NVL(RS.VALOR_DEBITO, 0)||'
                                ,'||NVL(RS.VALOR_CREDITO, 0)||'
                                ,'''||NVL(RS.CENTRO_COSTO, 9999999999)||'''
                                ,'''||RS.TERCERO||'''
                                ,'''||RS.SUCURSAL||'''
                                ,'''||NVL(RS.AUXILIAR, 9999999999999999)||'''
                                ,'''||RS.DESCRIPCION||'''
                                ,'||NVL(RS.BASE_GRAVABLE, 0)||'
                                ,'''||RS.REFERENCIA||'''
                                ,'''||RS.FUENTE_RECURSO||''' ';
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
                       MI_MSGERROR(1).VALOR := MI_COMPROBANTE;
                       MI_MSGERROR(2).CLAVE := 'TIPO';
                       MI_MSGERROR(2).VALOR := MI_TIPO_DEVOLUCION;
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                                UN_EXC_COD =>SQLCODE
                                                ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                ,UN_REEMPLAZOS  => MI_MSGERROR);
                  END;
                  
   
   
   
   
   END LOOP;
    MI_RTA :=   PCK_CONTABILIZAR.FC_CONTABILIZAR( UN_COMPANIA         => UN_COMPANIA
                                              ,UN_TIPOCOMPROBANTE  => MI_TIPO_DEVOLUCION
                                              ,UN_NUMERO           => MI_COMPROBANTE
                                              ,UN_ANO              => MI_ANIO
                                              ,UN_FECHA            => UN_FECHA
                                              ,UN_TERCERO          => MI_TERCERO
                                              ,UN_SUCURSAL         => MI_SUCURSAL
                                              ,UN_DESCRIPCION      => MI_DESCRIPCION
                                              ,UN_SIMPLE           => 0
                                              ,UN_INDIMPRESION     => 0
                                              ,UN_INTOMITIRPPTAL   => -1
                                              ,UN_CONCILIAR        => 0
                                              ,UN_PLANO            => 0
                                              ,UN_NONETEA          => 0
                                              ,UN_CONTRATISTA      => 0
                                              ,UN_TEXTO            => 'Devolución'
                                              ,UN_CONTRATO         => 0
                                              ,UN_TIPOCONTRATO     => ''
                                              ,UN_NRO_DOCUMENTO    => ''
                                              ,UN_ALMDEP           => 0
                                              ,UN_TERCE            => 0
                                              ,UN_RESAUXGEN        => 0
                                              ,UN_USUARIO          => UN_USUARIO);
  FOR RS1 IN ( SELECT 
               DT.COMPANIA, 
               DT.ANO, 
               DT.TIPO_CPTE, 
               DT.COMPROBANTE, 
               DT.CONSECUTIVO, 
               DT.CUENTA,
               DT.VALOR_DEBITO, 
               DT.VALOR_CREDITO
            FROM DETALLE_COMPROBANTE_CNT DT
            INNER JOIN PLAN_CONTABLE PC
             ON DT.COMPANIA = PC.COMPANIA
            AND DT.ANO = PC.ANO
            AND DT.CUENTA = PC.CODIGO
            WHERE DT.COMPANIA = UN_COMPANIA
            AND DT.ANO = UN_ANIO
            AND DT.TIPO_CPTE = UN_TIPOCOBRO
            AND DT.COMPROBANTE = UN_NUMEROCOBRO
            AND PC.CLASECUENTA IN ('C')
            ORDER BY CONSECUTIVO)
  LOOP
  
     BEGIN

        MI_CAMPOS := 'ANO_AFECT = '|| RS1.ANO ||', 
                      TIPO_CPTE_AFECT = '''|| RS1.TIPO_CPTE ||''' , 
                      CMPTE_AFECTADO = '|| RS1.COMPROBANTE ||', 
                      CONSECUTIVOAFECTADO = '|| RS1.CONSECUTIVO ||' ';
                      
        MI_CONDICION := ' COMPANIA = '''|| UN_COMPANIA ||'''
                      AND ANO ='|| MI_ANIO ||'
                      AND TIPO_CPTE = '''|| MI_TIPO_DEVOLUCION ||'''
                      AND COMPROBANTE = '|| MI_COMPROBANTE ||' 
                      AND CUENTA = '|| RS1.CUENTA ||' ';
        BEGIN
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME( UN_TABLA     =>  'DETALLE_COMPROBANTE_CNT',
                                               UN_ACCION    =>  'M',
                                               UN_CAMPOS    =>  MI_CAMPOS,
                                               UN_CONDICION =>  MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    =>  SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERRR_ACTEJECUCIONDEBITOCRED,
                                    UN_TABLAERROR => 'DETALLE_COMPROBANTE_CNT');
    END;
    
    
     BEGIN

        MI_CAMPOS := 'CREDITO_AFECTADO = '|| RS1.VALOR_DEBITO ||' ';
                      
        MI_CONDICION := ' COMPANIA = '''|| RS1.COMPANIA ||'''
                      AND ANO ='|| RS1.ANO ||'
                      AND TIPO_CPTE = '''|| RS1.TIPO_CPTE ||'''
                      AND COMPROBANTE = '|| RS1.COMPROBANTE ||' 
                      AND CONSECUTIVO = '|| RS1.CONSECUTIVO ||' ';
        BEGIN
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME( UN_TABLA     =>  'DETALLE_COMPROBANTE_CNT',
                                               UN_ACCION    =>  'M',
                                               UN_CAMPOS    =>  MI_CAMPOS,
                                               UN_CONDICION =>  MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    =>  SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERRR_ACTEJECUCIONDEBITOCRED,
                                    UN_TABLAERROR => 'DETALLE_COMPROBANTE_CNT');
    END;
  
  END LOOP;
  
  RETURN 'Se creo el comprobante: ' || MI_TIPO_DEVOLUCION || ' - ' || MI_COMPROBANTE;
 ELSIF UN_FACTURADO NOT IN (0) AND UN_RECAUDO NOT IN (0) THEN 
          
      BEGIN
         SELECT TIPOCPTE_PAGO, ANOCPTE_PAGO, NROCPTE_PAGO
        INTO MI_TIPO_PAGO, MI_ANIO_PAGO, MI_NUMERO_PAGO
        FROM SF_FACTURA
        WHERE COMPANIA = UN_COMPANIA
        AND ANO = UN_ANIO
        AND TIPO_FACTURA = UN_TIPOCOBRO
        AND NUMERO_FACTURA = UN_FACTURA;
      END;
 
 
  FOR RS IN (SELECT 
              DT.COMPANIA, DT.ANO, DT.TIPO_CPTE, DT.COMPROBANTE,
              CASE WHEN PC.EQUIVALENTE_DEVOLUCION IS NOT NULL THEN PC.EQUIVALENTE_DEVOLUCION ELSE DT.CUENTA END CUENTA,
              CASE WHEN PC.EQUIVALENTE_DEVOLUCION IS NOT NULL THEN PC.NATURALEZA ELSE DT.NATURALEZA END NATURALEZA, 
              DT.DESCRIPCION, 
              DT.VALOR_CREDITO,
              0 VALOR_DEBITO,
              DT.CENTRO_COSTO, 
               DT.TERCERO, 
               DT.SUCURSAL, 
               DT.AUXILIAR, 
               DT.REFERENCIA, 
               DT.FUENTE_RECURSO,
               DT.BASE_GRAVABLE
            FROM DETALLE_COMPROBANTE_CNT DT
            INNER JOIN PLAN_CONTABLE PC
             ON DT.COMPANIA = PC.COMPANIA
            AND DT.ANO = PC.ANO
            AND DT.CUENTA = PC.CODIGO
            WHERE DT.COMPANIA = UN_COMPANIA
            AND DT.ANO = UN_ANIO
            AND DT.TIPO_CPTE = UN_TIPOCOBRO
            AND DT.COMPROBANTE = UN_NUMEROCOBRO
            AND DT.VALOR_CREDITO > 0
            AND PC.NATURALEZA IN ('C') 
            UNION ALL
            SELECT DT.COMPANIA, DT.ANO, DT.TIPO_CPTE, DT.COMPROBANTE,
            UN_CUENTA CUENTA,
            PCN.NATURALEZA,
            DT.DESCRIPCION, 
            0 VALOR_CREDITO,
            VALOR_DEBITO,
            DT.CENTRO_COSTO, 
               DT.TERCERO, 
               DT.SUCURSAL, 
               DT.AUXILIAR, 
               DT.REFERENCIA, 
               DT.FUENTE_RECURSO,
               DT.BASE_GRAVABLE
            FROM DETALLE_COMPROBANTE_CNT DT
            INNER JOIN PLAN_CONTABLE PC
             ON DT.COMPANIA = PC.COMPANIA
            AND DT.ANO = PC.ANO
            AND DT.CUENTA = PC.CODIGO
            INNER JOIN PLAN_CONTABLE PCN
             ON DT.COMPANIA = PCN.COMPANIA
            AND UN_ANIO = PCN.ANO
            AND UN_CUENTA = PCN.CODIGO
            WHERE DT.COMPANIA = UN_COMPANIA
            AND DT.ANO = MI_ANIO_PAGO
            AND DT.TIPO_CPTE = MI_TIPO_PAGO
            AND DT.COMPROBANTE = MI_NUMERO_PAGO
            AND PC.CLASECUENTA IN ('B'))
  LOOP
 
        
        MI_ANIO := RS.ANO;
        MI_TERCERO := RS.TERCERO;
        MI_SUCURSAL := RS.SUCURSAL;
        MI_DESCRIPCION := 'Devolución ' || RS.DESCRIPCION;
   
        MI_COMPROBANTE := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'COMPROBANTE_CNT',
                                                                                  UN_CRITERIO => 'COMPANIA = '''||UN_COMPANIA||''' AND ANO = '||UN_ANIO||' AND TIPO = '''||MI_TIPO_DEVOLUCION||'''',
                                                                                  UN_CAMPO    => 'NUMERO', UN_INICIAL => UN_ANIO || '00000' || '1');
   
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
                              EJECUCION_DEBITO,
                              EJECUCION_CREDITO,
                              CENTRO_COSTO,
                              TERCERO,
                              SUCURSAL,
                              AUXILIAR,
                              DESCRIPCION,
                              BASE_GRAVABLE,
                              REFERENCIA,
                              FUENTE_RECURSOS';
                            
                MI_VALORES := ' '''||UN_COMPANIA||'''
                                ,'||RS.ANO||'
                                ,'''||MI_TIPO_DEVOLUCION||'''
                                ,'||MI_COMPROBANTE||'
                                ,'||MI_CONSECUTIVO||'
                                ,'''||RS.CUENTA||'''
                                ,'''||UN_FECHA||'''
                                ,'''||RS.NATURALEZA||'''
                                ,'||RS.VALOR_CREDITO||'
                                ,'||RS.VALOR_DEBITO||'
                                ,'||NVL(RS.VALOR_CREDITO, 0)||'
                                ,'||NVL(RS.VALOR_DEBITO, 0)||'
                                ,'''||NVL(RS.CENTRO_COSTO, 9999999999)||'''
                                ,'''||RS.TERCERO||'''
                                ,'''||RS.SUCURSAL||'''
                                ,'''||NVL(RS.AUXILIAR, 9999999999999999)||'''
                                ,'''||RS.DESCRIPCION||'''
                                ,'||NVL(RS.BASE_GRAVABLE, 0)||'
                                ,'''||RS.REFERENCIA||'''
                                ,'''||RS.FUENTE_RECURSO||''' ';
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
                       MI_MSGERROR(1).VALOR := MI_COMPROBANTE;
                       MI_MSGERROR(2).CLAVE := 'TIPO';
                       MI_MSGERROR(2).VALOR := MI_TIPO_DEVOLUCION;
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                                UN_EXC_COD =>SQLCODE
                                                ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                ,UN_REEMPLAZOS  => MI_MSGERROR);
                  END;
                  
   
   
   
   
   END LOOP;
    MI_RTA :=   PCK_CONTABILIZAR.FC_CONTABILIZAR( UN_COMPANIA         => UN_COMPANIA
                                              ,UN_TIPOCOMPROBANTE  => MI_TIPO_DEVOLUCION
                                              ,UN_NUMERO           => MI_COMPROBANTE
                                              ,UN_ANO              => MI_ANIO
                                              ,UN_FECHA            => UN_FECHA
                                              ,UN_TERCERO          => MI_TERCERO
                                              ,UN_SUCURSAL         => MI_SUCURSAL
                                              ,UN_DESCRIPCION      => MI_DESCRIPCION
                                              ,UN_SIMPLE           => 0
                                              ,UN_INDIMPRESION     => 0
                                              ,UN_INTOMITIRPPTAL   => -1
                                              ,UN_CONCILIAR        => 0
                                              ,UN_PLANO            => 0
                                              ,UN_NONETEA          => 0
                                              ,UN_CONTRATISTA      => 0
                                              ,UN_TEXTO            => 'Devolución'
                                              ,UN_CONTRATO         => 0
                                              ,UN_TIPOCONTRATO     => ''
                                              ,UN_NRO_DOCUMENTO    => ''
                                              ,UN_ALMDEP           => 0
                                              ,UN_TERCE            => 0
                                              ,UN_RESAUXGEN        => 0
                                              ,UN_USUARIO          => UN_USUARIO);
                                              
     BEGIN

        MI_CAMPOS := 'TIPO_DEVOLUCION = '''|| UN_TIPOCOBRO ||''',
                      NUMERO_DEVOLUCION = '|| UN_FACTURA ||' ';
                      
        MI_CONDICION := ' COMPANIA = '''|| UN_COMPANIA ||'''
                      AND ANO ='|| MI_ANIO ||'
                      AND TIPO = '''|| MI_TIPO_DEVOLUCION||'''
                      AND NUMERO = '|| MI_COMPROBANTE ||' ';
        BEGIN
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME( UN_TABLA     =>  'COMPROBANTE_CNT',
                                               UN_ACCION    =>  'M',
                                               UN_CAMPOS    =>  MI_CAMPOS,
                                               UN_CONDICION =>  MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    =>  SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERRR_ACTEJECUCIONDEBITOCRED,
                                    UN_TABLAERROR => 'COMPROBANTE_CNT');
    END;
 
 RETURN 'Se creo el comprobante: ' || MI_TIPO_DEVOLUCION || ' - ' || MI_COMPROBANTE;
 END IF;
  
END FC_DEVOLVER_FACTURAS;

FUNCTION FC_RECAUDAR_PAGO_PARCIAL
/*
    NAME              : FC_RECAUDAR_PAGO_PARCIAL
    AUTHOR MIGRACION  : GERMAN DAVID ROJAS G.
    DATE MIGRADOR     : 03/02/2025
    TIME              :
    SOURCE MODULE     :
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : PROCEDIMIENTO PARA EL RECAUDO DE PAGOS PARCIALES.

    @NAME:   recaudarPagoParcial
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
  UN_VALORRECAUDO   IN PCK_SUBTIPOS.TI_DOBLE
)
RETURN VARCHAR2 AS

  MI_RTA                VARCHAR2(100 CHAR);
  MI_DESCRIPCION        DETALLE_COMPROBANTE_CNT.DESCRIPCION%TYPE;
  MI_NOCAUSAR           SF_TIPO_COBRO.NOAPLICACAUSACION%TYPE;
  MI_CLASE              PCK_SUBTIPOS.TI_CLASECOMPROBANTE;
  MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
  MI_CAMPOS1            PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
  MI_CUENTA             DETALLE_COMPROBANTE_CNT.CUENTA%TYPE;
  MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_CPTERECAUDO        SF_TIPO_COBRO.CPTE_RECAUDO%TYPE;
  MI_CONSECUTIVO        COMPROBANTE_CNT.NUMERO%TYPE;
  MI_APLICAREC          BOOLEAN DEFAULT FALSE;
  MI_APLCUENTAREC       SF_TIPO_COBRO.APLCUENTAREC%TYPE;
  MI_CAUSARFACTRECAUDO  SF_TIPO_COBRO.INTERFAZ_RECAUDO%TYPE;
  MI_ACME               PCK_SUBTIPOS.TI_ENTERO;
  MI_ERROR              PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_CONSECUTIVODET     PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_EXISTE_PAGO        PCK_SUBTIPOS.TI_ENTERO;
  MI_VALOR_ABONADO      PCK_SUBTIPOS.TI_DOBLE;
  MI_DETALLE_PARCIAL    PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
  MI_CUENTADETALLE      PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
  MI_CADENAINSERTAR     CLOB DEFAULT ' ';
  MI_RETORNO            CLOB := '';
  MI_CANTIDAD_CONCEPTO  NUMBER:= 0;
  MI_PAGO_PARCIAL       NUMBER := 0;
  MI_PAGO_PARCIAL_U     NUMBER := 0;
  MI_CONTADOR           NUMBER := 0;
  
BEGIN

    SELECT SF_TIPO_COBRO.NOAPLICACAUSACION,TIPO_COMPROBANTE.CLASE_CONTABLE,CPTE_RECAUDO,SF_TIPO_COBRO.APLCUENTAREC      
    INTO MI_NOCAUSAR, MI_CLASE, MI_CPTERECAUDO, MI_APLCUENTAREC
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
                 FROM  SF_FACTURA F INNER JOIN SF_OBJETO_COBRO  SO
                         ON F.COMPANIA =  SO.COMPANIA
                        AND F.TIPOCOBRO = SO.TIPOCOBRO
                        AND F.CODIGO_COBRO = SO.CODIGO_COBRO
                 WHERE F.COMPANIA       = UN_COMPANIA
                   AND F.TIPO_CPTE      = UN_TIPOFACTURA
                   AND F.NUMERO_FACTURA = UN_NUMEROFACTURA
    )
    LOOP
        BEGIN
            BEGIN
            
                --Validación del pago parcial
                BEGIN
                    SELECT COUNT(NUMERO_FACTURA), VALOR_ABONADO
                    INTO    MI_EXISTE_PAGO, MI_VALOR_ABONADO 
                    FROM    SF_PAGOSPARCIALES
                    WHERE   COMPANIA        = UN_COMPANIA
                    AND     TIPO_FACTURA    = UN_TIPOFACTURA
                    AND     NUMERO_FACTURA  = UN_NUMEROFACTURA
                    GROUP BY COMPANIA, ANO, TIPO_FACTURA, NUMERO_FACTURA, VALOR_ABONADO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_EXISTE_PAGO   := 0;
                    MI_VALOR_ABONADO := 0;
                END;
                
                IF (UN_VALORRECAUDO > MI_RS.VALOR_TOTAL) OR ((MI_VALOR_ABONADO + UN_VALORRECAUDO) > MI_RS.VALOR_TOTAL) THEN
                    BEGIN
                        RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
                        MI_RETORNO := 'El valor del pago parcial no puede superar el monto de la factura o el saldo pendiente.';
                        MI_MSGERROR (1).CLAVE := 'MENSAJE';
                        MI_MSGERROR (1).VALOR := MI_RETORNO;
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                                                UN_TABLAERROR => 'SF_FACTURA',
                                            UN_REEMPLAZOS => MI_MSGERROR);
                    END;
                END IF;
                
                IF MI_EXISTE_PAGO = 0 THEN
                
                BEGIN
                    BEGIN
                    MI_CAMPOS:='COMPANIA,       ANO,
                                   TIPO_FACTURA,    NUMERO_FACTURA,
                                   TERCERO,         FECHA,
                                   VALOR_FACTURA,   VALOR_ABONADO,
                                   SALDO,
                                   CREATED_BY, DATE_CREATED
                                   ';
                                   
                        MI_VALORES:=''''||UN_COMPANIA       ||''',
                                     '  ||UN_ANIO           ||',
                                     '''||UN_TIPOFACTURA    ||''',
                                     '  ||UN_NUMEROFACTURA  ||',
                                     '''||MI_RS.TERCERO     ||''',
                                     '''||UN_FECHA          ||''',
                                     '  ||MI_RS.VALOR_TOTAL ||',
                                     0,
                                     '  ||MI_RS.VALOR_TOTAL ||',
                                     '''||UN_USUARIO        ||''',
                                     '''||UN_FECHA          ||'''
                                     ';
    
                        MI_ACME:=PCK_DATOS.FC_ACME(UN_TABLA  =>'SF_PAGOSPARCIALES',
                                               UN_ACCION =>'I',
                                               UN_CAMPOS =>MI_CAMPOS,
                                               UN_VALORES=>MI_VALORES);
                        EXCEPTION
                             WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                    END;

                EXCEPTION
                          WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                            MI_ERROR(1).CLAVE := 'PROCESO';
                            MI_ERROR(1).VALOR := 'Creacion pago parcial';
                             
                            PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD    => SQLCODE,
                                UN_TABLAERROR => 'SF_PAGOSPARCIALES',
                                UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSERTPAGPARCIAL,
                                UN_REEMPLAZOS => MI_ERROR
                            );
                END;
                
                END IF;
                
                --Creación de Header
                MI_DESCRIPCION := 'Recaudo parcial de la factura '||UN_TIPOFACTURA||' - '||UN_NUMEROFACTURA||' -- '||UN_OBSERVACION ;
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
                                       AND NUMERO   = '||MI_RS.CODIGO_COBRO||' ';


                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA  => 'COMPROBANTE_CNT',
                                                      UN_ACCION   => 'IS',
                                                      UN_CAMPOS   => MI_CAMPOS,
                                                      UN_VALORES  => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
            MI_MSGERROR(1).CLAVE := 'NUMERO';
            MI_MSGERROR(1).VALOR := MI_RS.CODIGO_COBRO;
            MI_MSGERROR(2).CLAVE := 'TIPO';
            MI_MSGERROR(2).VALOR := MI_CPTERECAUDO;
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSCOMPRINGRESO
                                            ,UN_REEMPLAZOS  => MI_MSGERROR);
        END;

        
        --Distribuir el pago parcial en los conceptos
        
        FOR MI_RS2 IN (SELECT COMPANIA, ANO, TIPO_FACTURA, FACTURA, TIPOCOBRO, CONCEPTO, VALOR_BASE, VALOR_NETO, 
                                TERCERO, SUCURSAL, CENTRO_COSTO, AUXILIAR, REFERENCIA, FUENTE_RECURSO
                                FROM SF_DETALLE_FACTURA
                                WHERE COMPANIA = UN_COMPANIA
                                AND ANO = UN_ANIO
                                AND TIPO_FACTURA = UN_TIPOFACTURA
                                AND FACTURA = UN_NUMEROFACTURA
        )
        LOOP
        
            BEGIN
                BEGIN
                    MI_CAMPOS:='COMPANIA,
                               TIPO_ABONO,
                               CODIGO_ABONO,
                               CUOTA,
                               CONCEPTO,
                               TOTAL_CUOTA,
                               ANO,
                               TIPOFACT,
                               NROFACT
                               ';
                               
                    MI_VALORES:=''''||UN_COMPANIA||''',
                                '''||UN_TIPOFACTURA||''',
                                '||UN_NUMEROFACTURA||',
                                1,
                                '''||MI_RS2.CONCEPTO||''',
                                0,
                                '||UN_ANIO||',
                                '''||UN_TIPOFACTURA||''',
                                '||UN_NUMEROFACTURA||'
                                ';

                    MI_ACME:=PCK_DATOS.FC_ACME(UN_TABLA  =>'TEMP_SF_DETALLE_CUOTAABONO',
                                           UN_ACCION =>'I',
                                           UN_CAMPOS =>MI_CAMPOS,
                                           UN_VALORES=>MI_VALORES);
                    EXCEPTION
                         WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                END;

            EXCEPTION
                  WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                    MI_ERROR(1).CLAVE := 'PROCESO';
                    MI_ERROR(1).VALOR := 'Creacion del detalle';
                     
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE,
                        UN_TABLAERROR => 'TEMP_SF_DETALLE_CUOTAABONO',
                        UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSERTPAGPARCIAL,
                        UN_REEMPLAZOS => MI_ERROR
                    );
            END;
        
            SELECT COUNT(CONCEPTO)
                INTO MI_CANTIDAD_CONCEPTO
            FROM SF_DETALLE_FACTURA
            WHERE COMPANIA = UN_COMPANIA
                AND ANO = UN_ANIO
                AND TIPO_FACTURA = UN_TIPOFACTURA
                AND FACTURA = UN_NUMEROFACTURA;
            
                MI_PAGO_PARCIAL := TRUNC(UN_VALORRECAUDO / MI_CANTIDAD_CONCEPTO, 2);
                MI_PAGO_PARCIAL_U := UN_VALORRECAUDO -(MI_PAGO_PARCIAL *(MI_CANTIDAD_CONCEPTO - 1));
                
                MI_CONTADOR := MI_CONTADOR +1;
                
                IF MI_CONTADOR = MI_CANTIDAD_CONCEPTO THEN
                     MI_CAMPOS := 'TOTAL_CUOTA = ' || MI_PAGO_PARCIAL_U || '';
                ELSE
                    MI_CAMPOS := 'TOTAL_CUOTA = ' || MI_PAGO_PARCIAL || '';
                END IF;
                
                MI_CONDICION := 'COMPANIA        = '''||UN_COMPANIA ||'''
                                AND TIPO_ABONO   = '''||UN_TIPOFACTURA||'''
                                AND CODIGO_ABONO = ' || UN_NUMEROFACTURA || '
                                AND CONCEPTO     = '''||MI_RS2.CONCEPTO||'''';
                
                BEGIN
              BEGIN
                MI_ACME := PCK_DATOS.FC_ACME(UN_ACCION    => 'M'
                                           ,UN_TABLA     => 'TEMP_SF_DETALLE_CUOTAABONO'
                                           ,UN_CAMPOS    => MI_CAMPOS
                                           ,UN_CONDICION => MI_CONDICION);
        
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                MI_ERROR(1).CLAVE := 'PROCESO';
                MI_ERROR(1).VALOR := 'actualizar valor de pago parcial';      
        
                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
              END;
        
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                        ,UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSERTPAGPARCIAL
                                        ,UN_TABLAERROR => 'TEMP_SF_DETALLE_CUOTAABONO'
                                        ,UN_REEMPLAZOS => MI_ERROR);
            END;
        
        END LOOP;
        
         --CREACION DE DETALLES QUE VAN AL DEBITO  
         MI_CONSECUTIVODET := 1;
            BEGIN
                BEGIN
                        MI_CAMPOS := 'COMPANIA,     ANO,
                                      TIPO_CPTE,    COMPROBANTE,
                                      CUENTA,       CONSECUTIVO,
                                      NATURALEZA,   FECHA,
                                      HORA,         DESCRIPCION,
                                      TERCERO,      SUCURSAL,
                                      VALOR_DEBITO, VALOR_CREDITO';
                
                        MI_VALORES := ''''||UN_COMPANIA||''',
                                       '||PCK_SYSMAN_UTL.FC_ANIO(UN_FECHA)||',
                                       '''||MI_CPTERECAUDO||''',
                                       '||MI_CONSECUTIVO||',
                                       '''||UN_CUENTA||''',
                                       '||MI_CONSECUTIVODET||',
                                       ''D'',
                                       '''||UN_FECHA||''',
                                       '''||UN_FECHA||''',
                                       '''||MI_DESCRIPCION||''',
                                       '''||MI_RS.TERCERO||''',
                                       '''||MI_RS.SUCURSAL||''',
                                       '||UN_VALORRECAUDO||',
                                       0';
    
                        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
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
                        MI_MSGERROR(1).VALOR := MI_CONSECUTIVO;
                        MI_MSGERROR(2).CLAVE := 'TIPO';
                        MI_MSGERROR(2).VALOR := MI_CPTERECAUDO;
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_INSDETACOMPCNT);
            END;
        
        --CREACION DE DETALLES QUE VAN AL CREDITO
        FOR MI_RS3 IN(SELECT SF_D.COMPANIA, SF_D.ANO, CP.CUENTADEBITOBASE CUENTA, NVL(TDA.TOTAL_CUOTA,0) VALOR, SF_D.TERCERO, SF_D.SUCURSAL, 
                        SF_D.CENTRO_COSTO, SF_D.AUXILIAR, SF_D.REFERENCIA, SF_D.FUENTE_RECURSO, F.TIPO_CPTE, F.NRO_CPTE, DCC.CONSECUTIVO CONSECUTIVOAFECTADO, PC.RUBRO
                        FROM SF_DETALLE_FACTURA SF_D
                        INNER JOIN SF_FACTURA F
                            ON F.COMPANIA = SF_D.COMPANIA
                            AND F.ANO = SF_D.ANO
                            AND F.TIPO_FACTURA = SF_D.TIPO_FACTURA
                            AND F.NUMERO_FACTURA = SF_D.FACTURA
                        INNER JOIN SF_CONCEPTOS CP
                            ON CP.COMPANIA = SF_D.COMPANIA
                            AND CP.ANO = SF_D.ANO
                            AND CP.CODIGO = SF_D.CONCEPTO
                        INNER JOIN DETALLE_COMPROBANTE_CNT DCC
                            ON F.COMPANIA = DCC.COMPANIA
                            AND F.ANO = DCC.ANO
                            AND F.TIPO_CPTE = DCC.TIPO_CPTE
                            AND F.NRO_CPTE = DCC.COMPROBANTE
                            AND CP.CUENTADEBITOBASE = DCC.CUENTA
                        LEFT JOIN TEMP_SF_DETALLE_CUOTAABONO TDA
                            ON TDA.COMPANIA = SF_D.COMPANIA
                            AND TDA.TIPO_ABONO = SF_D.TIPO_FACTURA
                            AND TDA.CODIGO_ABONO = SF_D.FACTURA
                            AND TDA.CONCEPTO = SF_D.CONCEPTO
                        LEFT JOIN PLAN_PPTAL_CUENTACNT PC
                            ON DCC.COMPANIA = PC.COMPANIA
                            AND PCK_SYSMAN_UTL.FC_ANIO(UN_FECHA) = PC.ANO
                            AND DCC.CUENTA = PC.CUENTA_CONTABLE
                        WHERE SF_D.COMPANIA = UN_COMPANIA
                            AND SF_D.TIPO_FACTURA = UN_TIPOFACTURA
                            AND SF_D.FACTURA = UN_NUMEROFACTURA)
            LOOP 
                BEGIN
                BEGIN
                    MI_CONSECUTIVODET := MI_CONSECUTIVODET + 1;
                    MI_CAMPOS := 'COMPANIA,
                                  ANO,
                                  TIPO_CPTE,
                                  COMPROBANTE,
                                  CUENTA,
                                  CONSECUTIVO,
                                  NATURALEZA,
                                  FECHA,
                                  HORA,
                                  DESCRIPCION,
                                  TERCERO,
                                  SUCURSAL,
                                  CENTRO_COSTO,
                                  AUXILIAR,
                                  REFERENCIA,
                                  FUENTE_RECURSO,
                                  VALOR_DEBITO,
                                  VALOR_CREDITO,
                                  EJECUCION_DEBITO, 
                                  EJECUCION_CREDITO,
                                  ANO_AFECT, 
                                  TIPO_CPTE_AFECT, 
                                  CMPTE_AFECTADO, 
                                  CONSECUTIVOAFECTADO,
                                  CUENTAPPTAL';
            
                    MI_VALORES := ''''||UN_COMPANIA||''',
                                   '||PCK_SYSMAN_UTL.FC_ANIO(UN_FECHA)||',
                                   '''||MI_CPTERECAUDO||''',
                                   '||MI_CONSECUTIVO||',
                                   '''||MI_RS3.CUENTA||''',
                                   '||MI_CONSECUTIVODET||',
                                   ''D'',
                                   '''||UN_FECHA||''',
                                   '''||UN_FECHA||''',
                                   '''||MI_DESCRIPCION||''',
                                   '''||MI_RS3.TERCERO||''',
                                   '''||MI_RS3.SUCURSAL||''',
                                   '''||MI_RS3.CENTRO_COSTO||''',
                                   '''||MI_RS3.AUXILIAR||''',
                                   '''||MI_RS3.REFERENCIA||''',
                                   '''||MI_RS3.FUENTE_RECURSO||''',
                                   0,
                                   '||MI_RS3.VALOR||',
                                   0,
                                   '||MI_RS3.VALOR||',
                                   '''||MI_RS3.ANO||''',
                                   '''||MI_RS3.TIPO_CPTE||''',
                                   '''||MI_RS3.NRO_CPTE||''',
                                   '''||MI_RS3.CONSECUTIVOAFECTADO||''',
                                   '''||MI_RS3.RUBRO||'''';

                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
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
                    MI_MSGERROR(1).VALOR := MI_CONSECUTIVO;
                    MI_MSGERROR(2).CLAVE := 'TIPO';
                    MI_MSGERROR(2).VALOR := MI_CPTERECAUDO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD => SQLCODE,
                    UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_INSDETACOMPCNT);
            END;
            
            END LOOP;
            
        --Creación comprobante pptal
        
            BEGIN
                  MI_CADENAINSERTAR := '';
                  MI_CUENTADETALLE := 0;
                  FOR RS IN(
                          SELECT CONSECUTIVO || ',' || CUENTA || ',' || VALOR || ',' || RUBRO_PPTAL ||  ';' LISTA_CNT
                          FROM(
                              SELECT D.COMPANIA, D.TIPO_CPTE, D.COMPROBANTE,D.CONSECUTIVO,P.CODIGO CUENTA,
                                     CASE WHEN P.NATURALEZA='D' THEN D.VALOR_DEBITO - D.VALOR_CREDITO ELSE D.VALOR_CREDITO - D.VALOR_DEBITO END VALOR,
                                     MIN(C.RUBRO) RUBRO_PPTAL, COUNT(D.COMPANIA) CONTADOR
                              FROM DETALLE_COMPROBANTE_CNT D INNER JOIN PLAN_CONTABLE P
                                    ON D.COMPANIA = P.COMPANIA
                                    AND D.ANO      = P.ANO
                                    AND D.CUENTA   = P.CODIGO
                                  INNER JOIN PLAN_PPTAL_CUENTACNT C
                                    ON P.COMPANIA    = C.COMPANIA
                                    AND P.ANO         = C.ANO
                                    AND P.CODIGO      = C.CUENTA_CONTABLE
                              WHERE D.COMPANIA  = UN_COMPANIA
                                AND D.ANO       = EXTRACT(YEAR FROM UN_FECHA)
                                AND D.TIPO_CPTE = MI_CPTERECAUDO
                                AND D.COMPROBANTE = MI_CONSECUTIVO
                              GROUP BY D.COMPANIA, D.TIPO_CPTE, D.COMPROBANTE, D.CONSECUTIVO, P.CODIGO,
                              CASE WHEN P.NATURALEZA='D' THEN D.VALOR_DEBITO - D.VALOR_CREDITO ELSE D.VALOR_CREDITO - D.VALOR_DEBITO END
                              ORDER BY CONSECUTIVO)
                              )
                  LOOP
                    MI_CUENTADETALLE := MI_CUENTADETALLE + 1;
                    MI_CADENAINSERTAR:= MI_CADENAINSERTAR || TO_CLOB(RS.LISTA_CNT);
                  END LOOP;
                  IF MI_CADENAINSERTAR = '' THEN
                    MI_CADENAINSERTAR := ' ';
                  END IF;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_CADENAINSERTAR := ' ';
                    MI_CUENTADETALLE := 0;
                END;
                
                IF MI_CUENTADETALLE > 0 THEN
                
                    FOR RS IN (SELECT COMPANIA, ANO, TIPO, NUMERO, TERCERO, SUCURSAL, DESCRIPCION, NRO_DOCUMENTO, VLR_DOCUMENTO, FECHA
                              FROM COMPROBANTE_CNT 
                              WHERE COMPANIA  = UN_COMPANIA
                                AND ANO       = EXTRACT(YEAR FROM UN_FECHA)
                                AND TIPO      = MI_CPTERECAUDO
                                AND NUMERO    = MI_CONSECUTIVO
                    )
                    LOOP
                    PCK_CONTABILIDAD1.PR_GENERARCOMPROBANTEPPTAL
                            (UN_COMPANIA        => RS.COMPANIA
                            ,UN_ANO             => RS.ANO
                            ,UN_TIPO            => RS.TIPO
                            ,UN_NUMERO          => RS.NUMERO
                            ,UN_FECHA           => RS.FECHA
                            ,UN_TERCERO         => RS.TERCERO
                            ,UN_SUCURSAL        => RS.SUCURSAL
                            ,UN_DESCRIPCION     => RS.DESCRIPCION
                            ,UN_NUMERODOC       => RS.NRO_DOCUMENTO
                            ,UN_VALORDOC        => RS.VLR_DOCUMENTO
                            ,UN_TIPOPPTAL       => RS.TIPO
                            ,UN_CADENAINSERTAR  => MI_CADENAINSERTAR
                            ,UN_CANTIDAD        => MI_CUENTADETALLE
                            ,UN_USUARIO         => UN_USUARIO
                            ,UN_DESDEINTERFAZ   => -1  );
                    END LOOP;
                END IF;
        
        --Creación detalles pagos parciales
        
        FOR MI_RS4 IN (SELECT COMPANIA, ANO, TIPO_FACTURA, NUMERO_FACTURA, TERCERO, FECHA, VALOR_FACTURA, SALDO 
                        FROM SF_PAGOSPARCIALES 
                        WHERE COMPANIA = UN_COMPANIA 
                        AND TIPO_FACTURA = UN_TIPOFACTURA 
                        AND NUMERO_FACTURA = UN_NUMEROFACTURA)
        LOOP
        
            SELECT COUNT(NUMERO_FACTURA)
                INTO MI_DETALLE_PARCIAL
            FROM SF_PAGOSPARCIALES_DETALLE
            WHERE   COMPANIA        = UN_COMPANIA
            AND     ANO             = UN_ANIO
            AND     TIPO_FACTURA    = UN_TIPOFACTURA
            AND     NUMERO_FACTURA  = UN_NUMEROFACTURA;
                
                MI_DETALLE_PARCIAL := MI_DETALLE_PARCIAL + 1;
        
                BEGIN
                    BEGIN
                    MI_CAMPOS:='COMPANIA,       ANO,
                                TIPO_FACTURA,   NUMERO_FACTURA,
                                CONSECUTIVO,    FECHA_PAGO,
                                VALOR_ABONADO,  SALDO_DET,
                                ANOCPTE_RECAUDO,
                                TIPOCPTE_RECAUDO,
                                NROCPTE_RECAUDO,
                                CREATED_BY, DATE_CREATED';
                                   
                     MI_VALORES:=''''||UN_COMPANIA        ||''',
                                  '  ||UN_ANIO            ||',
                                  '''||UN_TIPOFACTURA     ||''',
                                  '  ||UN_NUMEROFACTURA   ||',
                                  '  ||MI_DETALLE_PARCIAL ||',
                                  '''||UN_FECHA           ||''',
                                  '  ||UN_VALORRECAUDO    ||',
                                  '  ||(MI_RS4.SALDO - UN_VALORRECAUDO)  ||',
                                  '  ||PCK_SYSMAN_UTL.FC_ANIO(UN_FECHA) ||',
                                  '''||MI_CPTERECAUDO     ||''',
                                  '  ||MI_CONSECUTIVO     ||',
                                  '''||UN_USUARIO         ||''',
                                  '''||UN_FECHA           ||'''';   
    
                        MI_ACME:=PCK_DATOS.FC_ACME(UN_TABLA  =>'SF_PAGOSPARCIALES_DETALLE',
                                               UN_ACCION =>'I',
                                               UN_CAMPOS =>MI_CAMPOS,
                                               UN_VALORES=>MI_VALORES);
                        EXCEPTION
                             WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                    END;

                EXCEPTION
                          WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                            MI_ERROR(1).CLAVE := 'PROCESO';
                            MI_ERROR(1).VALOR := 'Creacion pago parcial';
                             
                            PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD    => SQLCODE,
                                UN_TABLAERROR => 'SF_PAGOSPARCIALES_DETALLE',
                                UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSERTPAGPARCIAL,
                                UN_REEMPLAZOS => MI_ERROR
                            );
                END;
                --Actualiza saldo header pago parcial
                MI_CAMPOS := 'SALDO = ' || (MI_RS4.SALDO - UN_VALORRECAUDO) || '
                             , VALOR_ABONADO = VALOR_ABONADO + ' || UN_VALORRECAUDO ||'';
                                
                MI_CONDICION := 'COMPANIA        = '''||UN_COMPANIA ||'''
                              AND TIPO_FACTURA   = '''||UN_TIPOFACTURA||'''
                              AND NUMERO_FACTURA = ' || UN_NUMEROFACTURA || '';
                
                BEGIN
                      BEGIN
                        MI_ACME := PCK_DATOS.FC_ACME(UN_ACCION    => 'M'
                                                   ,UN_TABLA     => 'SF_PAGOSPARCIALES'
                                                   ,UN_CAMPOS    => MI_CAMPOS
                                                   ,UN_CONDICION => MI_CONDICION);
                
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        MI_ERROR(1).CLAVE := 'PROCESO';
                        MI_ERROR(1).VALOR := 'actualizar valor de pago parcial';      
                
                        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                      END;
        
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSERTPAGPARCIAL
                                                ,UN_TABLAERROR => 'SF_PAGOSPARCIALES'
                                                ,UN_REEMPLAZOS => MI_ERROR);
            END;
            
            --Actualiza indicador pago parcial en factura
            MI_CAMPOS := 'IND_PAGOPARCIAL = -1';
                                
                MI_CONDICION := 'COMPANIA        = '''||UN_COMPANIA ||'''
                              AND ANO            = '  ||UN_ANIO || '
                              AND TIPOFACTURA   = '''||UN_TIPOFACTURA||'''
                              AND NRO_FACTURA    = '  ||UN_NUMEROFACTURA || '';
                
                BEGIN
              BEGIN
                MI_ACME := PCK_DATOS.FC_ACME(UN_ACCION    => 'M'
                                           ,UN_TABLA     => 'SF_OBJETO_COBRO'
                                           ,UN_CAMPOS    => MI_CAMPOS
                                           ,UN_CONDICION => MI_CONDICION);
        
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                MI_ERROR(1).CLAVE := 'PROCESO';
                MI_ERROR(1).VALOR := 'actualizar valor de pago parcial';      
        
                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
              END;
        
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                        ,UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSERTPAGPARCIAL
                                        ,UN_TABLAERROR => 'SF_PAGOSPARCIALES'
                                        ,UN_REEMPLAZOS => MI_ERROR);
            END;
            
            --Validación si el saldo de la factura es 0 para activar indicador de recaudo
            
            IF (MI_RS4.SALDO - UN_VALORRECAUDO) = 0 THEN 
            
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
                
                --Actualizacion cpte pago en factura cuando esta pago todo el valor de la factura

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
                
                --Actualiza cuenta banco del detalle de la factura para cuando se recauda a una sola cuenta      
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
            
            END IF;
            
            END LOOP;
                
     END LOOP;
    RETURN MI_CONSECUTIVO;

END FC_RECAUDAR_PAGO_PARCIAL;

FUNCTION FC_GETFATCTERNUM
/*
    NAME              : FC_GETFATCTERNUM
    AUTHOR            : JM CC2971
    DATE CREATED      : 11/11/2025
    DESCRIPTION       : Cuenta el número de facturas filtradas para evitar demoras
*/
(
    UN_COMPANIA IN VARCHAR2,
    UN_TIPO_FACTURA IN VARCHAR2,
    UN_NUMERO_FACTURA IN VARCHAR2,
    UN_VALOR_TOTAL IN VARCHAR2,
    UN_TERCERO IN VARCHAR2,
    UN_SUCURSAL IN VARCHAR2,
    UN_NOMBRE IN VARCHAR2,
    UN_OBSERVACIONES IN VARCHAR2,
    UN_NOMBRETIPOC IN VARCHAR2
)
RETURN number
IS
    MI_RTA number;
BEGIN
    MI_RTA := 0;
    BEGIN
        SELECT COUNT(1) AS TOTAL INTO MI_RTA
        FROM (
            SELECT
                FACTURA.NUMERO_FACTURA,
                FACTURA.VALOR_TOTAL,
                FACTURA.TERCERO,
                FACTURA.SUCURSAL,
                TERCERO.NOMBRE,
                FACTURA.OBSERVACIONES,
                TIPOCOBRO.NOMBRE NOMBRETIPOC
            FROM SF_FACTURA FACTURA
            INNER JOIN TERCERO ON FACTURA.COMPANIA = TERCERO.COMPANIA
                              AND FACTURA.TERCERO = TERCERO.NIT
                              AND FACTURA.SUCURSAL = TERCERO.SUCURSAL
            INNER JOIN SF_OBJETO_COBRO D ON FACTURA.COMPANIA = D.COMPANIA
                                       AND FACTURA.TIPOCOBRO = D.TIPOCOBRO
                                       AND FACTURA.CODIGO_COBRO = D.CODIGO_COBRO
            INNER JOIN SF_TIPO_COBRO TIPOCOBRO ON FACTURA.COMPANIA = TIPOCOBRO.COMPANIA
                                              AND FACTURA.ANO = TIPOCOBRO.ANO
                                              AND FACTURA.TIPO_FACTURA = TIPOCOBRO.CODIGO
            LEFT JOIN PLAN_CONTABLE ON FACTURA.COMPANIA = PLAN_CONTABLE.COMPANIA
                                   AND FACTURA.CUENTA_RECAUDO = PLAN_CONTABLE.CODIGO
                                   AND FACTURA.ANO = PLAN_CONTABLE.ANO
            WHERE FACTURA.COMPANIA = UN_COMPANIA
              AND FACTURA.TIPO_FACTURA = UN_TIPO_FACTURA
              AND FACTURA.FECHA_PAGO IS NULL
              AND D.INDPAGO IN (0)
              AND NVL(FACTURA.ANULADA,0) IN (0)
              AND (NROFRA_PAGOPARCIAL IS NULL OR NROFRA_PAGOPARCIAL = 0)
              AND TIPOFRA_PAGOPARCIAL IS NULL
              AND (
                  (CASE WHEN FACTURA.NUMERO_FACTURA IS NULL THEN ' ' ELSE UPPER(FACTURA.NUMERO_FACTURA) END) LIKE UPPER(CONCAT(UN_NUMERO_FACTURA, '%')) OR UN_NUMERO_FACTURA IS NULL
              )
              AND (
                  (CASE WHEN FACTURA.VALOR_TOTAL IS NULL THEN ' ' ELSE TO_CHAR(FACTURA.VALOR_TOTAL) END) LIKE UPPER(CONCAT(UN_VALOR_TOTAL, '%')) OR UN_VALOR_TOTAL IS NULL
              )
              AND (
                  (CASE WHEN FACTURA.TERCERO IS NULL THEN ' ' ELSE UPPER(FACTURA.TERCERO) END) LIKE UPPER(CONCAT(UN_TERCERO, '%')) OR UN_TERCERO IS NULL
              )
              /*AND (
                  (CASE WHEN FACTURA.SUCURSAL IS NULL THEN ' ' ELSE UPPER(FACTURA.SUCURSAL) END) LIKE UPPER(CONCAT(UN_SUCURSAL, '%')) OR UN_SUCURSAL IS NULL
              )*/
              AND (
                  (CASE WHEN TERCERO.NOMBRE IS NULL THEN ' ' ELSE UPPER(TERCERO.NOMBRE) END) LIKE UPPER(CONCAT(CONCAT('%', UN_NOMBRE), '%')) OR UN_NOMBRE IS NULL
              )
             /* AND (
                  (CASE WHEN FACTURA.OBSERVACIONES IS NULL THEN ' ' ELSE UPPER(FACTURA.OBSERVACIONES) END) LIKE UPPER(CONCAT(CONCAT('%', UN_OBSERVACIONES), '%')) OR UN_OBSERVACIONES IS NULL
              )
              AND (
                  (CASE WHEN TIPOCOBRO.NOMBRE IS NULL THEN ' ' ELSE UPPER(TIPOCOBRO.NOMBRE) END) LIKE UPPER(CONCAT(UN_NOMBRETIPOC, '%')) OR UN_NOMBRETIPOC IS NULL
              )*/
        ) DatosContar;

    EXCEPTION WHEN NO_DATA_FOUND THEN
        RETURN MI_RTA;
    END;

    RETURN NVL(MI_RTA,0);
END FC_GETFATCTERNUM;


FUNCTION FC_GETFATCTERPAG
/*
    NAME              : FC_GETFATCTERPAG
    AUTHOR            : JM CC2971
    DATE CREATED      : 11/11/2025
    DESCRIPTION       : Cuenta el número de facturas filtradas para evitar demoras
*/
(
    UN_COMPANIA IN VARCHAR2,
    UN_TIPO_FACTURA IN VARCHAR2,
    UN_NUMERO_FACTURA IN VARCHAR2,
    UN_VALOR_TOTAL IN VARCHAR2,
    UN_TERCERO IN VARCHAR2,
    UN_SUCURSAL IN VARCHAR2,
    UN_NOMBRE IN VARCHAR2,
    UN_OBSERVACIONES IN VARCHAR2,
    UN_NOMBRETIPOC IN VARCHAR2,
    UN_PAGTAMANIO IN NUMBER,
    UN_PAGINICIO IN NUMBER
) RETURN CLOB
IS
    MI_RTA CLOB;
BEGIN
    MI_RTA := '';

    BEGIN
        FOR MI_RS IN (
            WITH DataFactura AS (
                SELECT
                    FACTURA.NUMERO_FACTURA,
                    D.CODIGO_COBRO,
                    FACTURA.VALOR_TOTAL,
                    FACTURA.TERCERO,
                    FACTURA.SUCURSAL,
                    FACTURA.ANO,
                    PCK_NOMINA_COM5.FC_QUITAESPECIALES(TERCERO.NOMBRE) AS NOMBRE,
                    PCK_NOMINA_COM5.FC_QUITAESPECIALES(FACTURA.OBSERVACIONES) AS OBSERVACIONES,
                    TIPOCOBRO.NOMBRE AS NOMBRETIPOC,
                    FACTURA.TIPO_FACTURA,
                    FACTURA.TIPO_ABONO,
                    FACTURA.NRO_ABONO,
                    (CASE FACTURA.INTERFAZADA WHEN 0 THEN 'false' ELSE 'true' END) AS INTERFAZADA,
                    FACTURA.CUENTA_RECAUDO,
                    PLAN_CONTABLE.NOMBRE AS NOMBRE_BANCO,
                    FACTURA.ANO_CPTE,
                    (CASE FACTURA.DIFERIDA WHEN 0 THEN 'false' ELSE 'true' END) AS DIFERIDA,
                    (TO_CHAR(FACTURA.FECHA_EXPEDICION,'DD/MM/YYYY HH24:MI:SS')) AS FECHA_EXPEDICION,
                    (TO_CHAR(FACTURA.FECHA_VENCIMIENTO,'DD/MM/YYYY HH24:MI:SS')) AS FECHA_VENCIMIENTO,
                    FACTURA.TIPOCOBRO,
                    (CASE FACTURA.PAGOS_PARCIALES WHEN 0 THEN 'false' ELSE 'true' END) AS PAGOS_PARCIALES
                FROM
                    SF_FACTURA FACTURA
                INNER JOIN
                    TERCERO ON FACTURA.COMPANIA = TERCERO.COMPANIA AND FACTURA.TERCERO = TERCERO.NIT AND FACTURA.SUCURSAL = TERCERO.SUCURSAL
                INNER JOIN
                    SF_OBJETO_COBRO D ON FACTURA.COMPANIA = D.COMPANIA AND FACTURA.TIPOCOBRO = D.TIPOCOBRO AND FACTURA.CODIGO_COBRO = D.CODIGO_COBRO
                INNER JOIN
                    SF_TIPO_COBRO TIPOCOBRO ON FACTURA.COMPANIA = TIPOCOBRO.COMPANIA AND FACTURA.ANO = TIPOCOBRO.ANO AND FACTURA.TIPO_FACTURA = TIPOCOBRO.CODIGO
                LEFT JOIN
                    PLAN_CONTABLE ON FACTURA.COMPANIA = PLAN_CONTABLE.COMPANIA AND FACTURA.CUENTA_RECAUDO = PLAN_CONTABLE.CODIGO AND FACTURA.ANO = PLAN_CONTABLE.ANO
                WHERE
                    FACTURA.COMPANIA = UN_COMPANIA
                    AND FACTURA.TIPO_FACTURA = UN_TIPO_FACTURA
                    AND FACTURA.FECHA_PAGO IS NULL
                    AND D.INDPAGO IN (0)
                    AND NVL(FACTURA.ANULADA,0) IN (0)
                    AND (NROFRA_PAGOPARCIAL IS NULL OR NROFRA_PAGOPARCIAL = 0)
                    AND TIPOFRA_PAGOPARCIAL IS NULL
                    AND (UPPER(FACTURA.NUMERO_FACTURA) LIKE UPPER(CONCAT(UN_NUMERO_FACTURA, '%')) OR UN_NUMERO_FACTURA IS NULL)
                    AND (UPPER(TO_CHAR(FACTURA.VALOR_TOTAL)) LIKE UPPER(CONCAT(UN_VALOR_TOTAL, '%')) OR UN_VALOR_TOTAL IS NULL)
                    AND (UPPER(FACTURA.TERCERO) LIKE UPPER(CONCAT(UN_TERCERO, '%')) OR UN_TERCERO IS NULL)
                    --AND (UPPER(FACTURA.SUCURSAL) LIKE UPPER(CONCAT(UN_SUCURSAL, '%')) OR UN_SUCURSAL IS NULL)
                    AND (UPPER(TERCERO.NOMBRE) LIKE UPPER(CONCAT(CONCAT('%', UN_NOMBRE), '%')) OR UN_NOMBRE IS NULL)
                    /*AND (UPPER(FACTURA.OBSERVACIONES) LIKE UPPER(CONCAT(CONCAT('%', UN_OBSERVACIONES), '%')) OR UN_OBSERVACIONES IS NULL)
                    AND (UPPER(TIPOCOBRO.NOMBRE) LIKE UPPER(CONCAT(UN_NOMBRETIPOC, '%')) OR UN_NOMBRETIPOC IS NULL) */
                ORDER BY
                    FACTURA.NUMERO_FACTURA DESC
            )
            SELECT
                JSON_OBJECT(
                    'NUMERO_FACTURA' VALUE NUMERO_FACTURA,
                    'CODIGO_COBRO' VALUE CODIGO_COBRO,
                    'VALOR_TOTAL' VALUE VALOR_TOTAL,
                    'TERCERO' VALUE TERCERO,
                    'SUCURSAL' VALUE SUCURSAL,
                    'ANO' VALUE ANO,
                    'NOMBRE' VALUE NOMBRE,
                    'OBSERVACIONES' VALUE OBSERVACIONES,
                    'NOMBRETIPOC' VALUE NOMBRETIPOC,
                    'TIPO_FACTURA' VALUE TIPO_FACTURA,
                    'TIPO_ABONO' VALUE TIPO_ABONO,
                    'NRO_ABONO' VALUE NRO_ABONO,
                    'INTERFAZADA' VALUE INTERFAZADA,
                    'CUENTA_RECAUDO' VALUE CUENTA_RECAUDO,
                    'NOMBRE_BANCO' VALUE NOMBRE_BANCO,
                    'ANO_CPTE' VALUE ANO_CPTE,
                    'DIFERIDA' VALUE DIFERIDA,
                    'FECHA_EXPEDICION' VALUE FECHA_EXPEDICION,
                    'FECHA_VENCIMIENTO' VALUE FECHA_VENCIMIENTO,
                    'TIPOCOBRO' VALUE TIPOCOBRO,
                    'PAGOS_PARCIALES' VALUE PAGOS_PARCIALES
                ) AS JSONOBJ, ROWNUM AS RNUM
            FROM DataFactura
            WHERE ROWNUM <= (UN_PAGINICIO + UN_PAGTAMANIO)
        )
        LOOP
            IF MI_RS.RNUM > UN_PAGINICIO THEN
                MI_RTA := MI_RTA || MI_RS.JSONOBJ || ',';
            END IF;
        END LOOP;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN MI_RTA;
    END;

    IF LENGTH(MI_RTA) > 0 THEN
        MI_RTA := SUBSTR(MI_RTA, 1, LENGTH(MI_RTA) - 1);
    END IF;

    RETURN NVL(MI_RTA,'');
END FC_GETFATCTERPAG;

FUNCTION FC_ACT_FACTURAS_FRIDA
/*
    NAME              : FC_ACT_FACTURAS_FRIDA
    AUTHOR            : SYSMAN, JM CC 3425.
    DESCRIPTION       : PROCESA EL JSON RESULTANTE DEL API DE FRIDA 
                       (sysman-erp-servicio-frida/servicio/enviofactura?numeroContribuyente=).
                        y lo inserta en TEMP_BORRADO_FACTURAS para que se pueda visaulizar la informacion en el sistema 
    --NAME:   actualizarTablaFacturas
    --METHOD: POST
*/
(
    UN_JSON IN CLOB
) RETURN VARCHAR2 AS

    v_main_array    JSON_ARRAY_T;
    v_inner_array   JSON_ARRAY_T;
    
    MI_RTA          VARCHAR2(100 CHAR);
    MI_DOCESTADO    VARCHAR2(100 CHAR);
    MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
    MI_ACME         PCK_SUBTIPOS.TI_ENTERO; 
    MI_ERROR        PCK_SUBTIPOS.TI_CLAVEVALOR;    
    MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
    
    MI_NUM_FACTURA   VARCHAR2(100);
    MI_PREFIJO       VARCHAR2(50);
    MI_ID_FACTURA    VARCHAR2(100);
    MI_COD_ESTADO    VARCHAR2(50);
    MI_RETORNO       VARCHAR2(50);
BEGIN
    
    v_main_array := JSON_ARRAY_T.parse(UN_JSON);
    

    FOR i IN 0 .. v_main_array.get_size - 1 LOOP
        
        v_inner_array := treat(v_main_array.get(i) as JSON_ARRAY_T);
        

        MI_NUM_FACTURA := v_inner_array.get_string(0);
        MI_PREFIJO     := v_inner_array.get_string(1);
        MI_ID_FACTURA  := v_inner_array.get_string(2);
        MI_COD_ESTADO  := v_inner_array.get_string(3);
        

        MI_DOCESTADO := CASE MI_COD_ESTADO
                            WHEN '000'     THEN 'PERSISTIDA'
                            WHEN '7200001' THEN 'RECIBIDA'
                            WHEN '7200003' THEN 'EN PROCESO DE VALIDACIÓN'
                            WHEN '7200004' THEN 'FALLIDA (Documento no cumple 1 o más validaciones de la DIAN)'
                            ELSE ' ' 
                        END;


        MI_CAMPOS := 'NUM_FACTURA, PREFIJO, ID_FACTURA, 
                      COD_ESTADO, DESCRIPCION_ESTADO, CREATED_BY, 
                      DATE_CREATED, MODIFIED_BY, DATE_MODIFIED';
        

        MI_VALORES := '''' || MI_NUM_FACTURA || ''', ' ||
                      '''' || MI_PREFIJO     || ''', ' ||
                      '''' || MI_ID_FACTURA  || ''', ' ||
                      '''' || MI_COD_ESTADO  || ''', ' ||
                      '''' || MI_DOCESTADO  || ''', ' ||
                      '''USER'', SYSDATE, ''USER'', SYSDATE';

        BEGIN

            MI_ACME := PCK_DATOS.FC_ACME(
                         UN_TABLA  => 'TEMP_BORRADO_FACTURAS',
                         UN_ACCION => 'I',
                         UN_CAMPOS => MI_CAMPOS,
                         UN_VALORES=> MI_VALORES
                       );
        EXCEPTION
            WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        END;

    END LOOP;

    RETURN MI_RTA;

EXCEPTION
    WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
    
        MI_RETORNO := 'Error procesando carga de facturas desde JSON.';
        MI_MSGERROR (1).CLAVE := 'MENSAJE';
        MI_MSGERROR (1).VALOR := MI_RETORNO;
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                    UN_TABLAERROR => 'TEMP_BORRADO_FACTURAS',
                    UN_REEMPLAZOS => MI_MSGERROR);
                    
    WHEN OTHERS THEN
        RAISE;
END FC_ACT_FACTURAS_FRIDA;

FUNCTION FC_CONSECUTIVOOBJ
/*
    NAME              : FC_CONSECUTIVOOBJ
    AUTHOR            : NCARDENAS
    DATE CREATED      :17/02/2026
    DESCRIPTION       : VALIDA CONSECUTIVO OBJETO COBRO
*/

(
     UN_COMPANIA    IN VARCHAR2,
     UN_ANO         IN PCK_SUBTIPOS.TI_ANIO,
     UN_CODIGO      IN VARCHAR2
)RETURN NUMBER
IS 
    MI_CONSECUTIVO  NUMBER;
BEGIN
    SELECT CONSECUTIVO_COBRO
    INTO MI_CONSECUTIVO
    FROM SF_TIPO_COBRO
    WHERE COMPANIA = UN_COMPANIA
      AND ANO      = UN_ANO
      AND CODIGO   = UN_CODIGO;

  RETURN NVL(MI_CONSECUTIVO,0);
  
    EXCEPTION WHEN NO_DATA_FOUND THEN
       RETURN 0;
END FC_CONSECUTIVOOBJ;

FUNCTION FC_SUBIR_COBROS
/*
    NAME              : FC_SUBIR_COBROS
    AUTHOR MIGRACION  : GERMAN DAVID ROJAS GONZALEZ
    DATE MIGRADOR     : 25/05/2026
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : PERMITE EL CARGUE DE LOS COBROS

    @NAME:    cargarCobros
    @METHOD:  POST
    */
( 
  UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO    	  IN PCK_SUBTIPOS.TI_ANIO,
  UN_TIPOCOBRO  IN SF_OBJETO_COBRO.TIPOCOBRO%TYPE,
  UN_CADENA  	  IN CLOB,
  UN_FECHACOMP  IN DATE,
  UN_FECHAVEN   IN DATE,
  UN_USUARIO    IN PCK_SUBTIPOS.TI_USUARIO
) RETURN CLOB AS 

    MI_DATOS_FILA         PCK_SYSMAN_UTL.T_SPLIT;
    MI_DATOS_COLUMNAS     PCK_SYSMAN_UTL.T_SPLIT;
    MI_ANIO               PCK_SUBTIPOS.TI_ANIO;
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
    MI_CONTA              PCK_SUBTIPOS.TI_ENTERO;
    MI_NUM_COBRO          PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_LINEA              PCK_SUBTIPOS.TI_ENTERO := 1;
    MI_EXISTE             PCK_SUBTIPOS.TI_ENTERO;
    MI_TERCERO            TERCERO.NIT%TYPE;
    MI_SUCURSAL           TERCERO.SUCURSAL%TYPE;
    MI_NOMBRETER          TERCERO.NOMBRE%TYPE;
    MI_DIRECTER           TERCERO.DIRECCION%TYPE;
    MI_TELETER            TERCERO.TELEFONOS%TYPE;
    MI_OBSERVACION        SF_OBJETO_COBRO.OBSERVACIONES%TYPE;
    MI_CONSECUTIVO        PCK_SUBTIPOS.TI_ENTERO;
    MI_VALOR              PCK_SUBTIPOS.TI_DOBLE:=0;
    MI_FECHA_INI_C        DATE;
    MI_FECHA_FIN_C        DATE;
    MI_ERROR_LINEA        CLOB := '';
    MI_RETORNO            CLOB := '';

    BEGIN

    MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENA,
                                                 UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);
    
    <<VALIDAR_DATOS>>
      FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST 
        LOOP
            MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                              UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);

            MI_ANIO := MI_DATOS_COLUMNAS(1);
            MI_LINEA := MI_LINEA + 1;
            
            SELECT COUNT(1)
                INTO MI_EXISTE
            FROM SF_CONCEPTOS
            WHERE COMPANIA = UN_COMPANIA
            AND ANO = MI_ANIO
            AND TIPOCOBRO = UN_TIPOCOBRO
            AND CODIGO = MI_DATOS_COLUMNAS(2);
            
            IF MI_EXISTE = 0 THEN
                MI_ERROR_LINEA := MI_ERROR_LINEA || 'Concepto ' || MI_DATOS_COLUMNAS(2) || ' no existe. ';
            END IF;
            
            SELECT COUNT(1)
                INTO MI_EXISTE
            FROM TERCERO
            WHERE COMPANIA = UN_COMPANIA
            AND NIT = MI_DATOS_COLUMNAS(3)
            AND SUCURSAL = '001';
            
            IF MI_EXISTE = 0 THEN
                MI_ERROR_LINEA := MI_ERROR_LINEA || 'Tercero ' || MI_DATOS_COLUMNAS(3) || ' no existe. ';
            END IF;
            
            IF MI_ERROR_LINEA IS NOT NULL THEN
                MI_RETORNO := MI_RETORNO || 'Línea ' || MI_LINEA || ': ' || MI_ERROR_LINEA || CHR(10);
                MI_ERROR_LINEA := NULL;
            END IF;
            
      END LOOP VALIDAR_DATOS;
      
      IF MI_RETORNO IS NULL OR MI_RETORNO = '' THEN
      
          <<SUBIR_DATOS>>
          FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST 
            LOOP
                MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                                  UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);
    
                 MI_ANIO := MI_DATOS_COLUMNAS(1);
                 IF UN_TIPOCOBRO = 'TUA' THEN
                    MI_OBSERVACION := SUBSTR(MI_DATOS_COLUMNAS(18),1,490);
                    MI_VALOR := MI_DATOS_COLUMNAS(17);
                 ELSIF UN_TIPOCOBRO = 'TRE' THEN
                    MI_OBSERVACION := SUBSTR(MI_DATOS_COLUMNAS(14),1,490);
                    MI_VALOR := MI_DATOS_COLUMNAS(13);
                 END IF;
                 MI_FECHA_INI_C := TO_DATE('01/01/' || MI_ANIO, 'DD/MM/YYYY');
                 MI_FECHA_FIN_C := TO_DATE('31/12/' || MI_ANIO, 'DD/MM/YYYY');
                 
                 BEGIN 
                    SELECT COUNT(CODIGO_COBRO) 
                    INTO MI_CONTA 
                    FROM SF_OBJETO_COBRO 
                    WHERE COMPANIA = UN_COMPANIA 
                    AND ANO = MI_ANIO 
                    AND TIPOCOBRO = UN_TIPOCOBRO; 
                END; 
                
                IF MI_CONTA > 0 THEN 
                    SELECT MAX(CODIGO_COBRO) 
                        INTO MI_NUM_COBRO 
                    FROM SF_OBJETO_COBRO 
                    WHERE COMPANIA = UN_COMPANIA 
                    AND ANO = MI_ANIO 
                    AND TIPOCOBRO = UN_TIPOCOBRO; 
                    MI_NUM_COBRO := MI_NUM_COBRO + 1; 
                ELSE 
                    SELECT NVL(CONSECUTIVO_COBRO, (MI_ANIO || '000001')) 
                        INTO MI_NUM_COBRO 
                    FROM SF_TIPO_COBRO 
                    WHERE COMPANIA = UN_COMPANIA 
                    AND ANO = MI_ANIO 
                    AND CODIGO = UN_TIPOCOBRO; 
                END IF;
                
                BEGIN
                    SELECT NIT, SUCURSAL, NOMBRE, DIRECCION, TELEFONOS 
                        INTO MI_TERCERO, MI_SUCURSAL, MI_NOMBRETER, MI_DIRECTER, MI_TELETER
                    FROM TERCERO
                    WHERE COMPANIA = UN_COMPANIA 
                    AND NIT = MI_DATOS_COLUMNAS(3)
                    AND SUCURSAL = '001';
                END;
                --SF_OBJETO_COBRO
                MI_CAMPOS := 'COMPANIA, ANO, TIPOCOBRO, CODIGO_COBRO, TERCERO, SUCURSAL, FECHA_SOLICITUD, FECHA_VENCIMIENTO, OBSERVACIONES,
                            NIT_TERCEROFACTURADO, SUCURSAL_TERCEROFACTURADO, NOMBRE_TERCEROFACTURADO, DIRECCION, TELEFONOS, CREATED_BY, DATE_CREATED';
                
                MI_VALORES := ''''|| UN_COMPANIA ||''' 
                              ,'  || MI_ANIO ||' 
                              ,'''|| UN_TIPOCOBRO ||''' 
                              ,'  || MI_NUM_COBRO ||'
                              ,'''|| MI_TERCERO ||'''
                              ,'''|| MI_SUCURSAL ||'''
                              ,'''|| UN_FECHACOMP ||'''
                              ,'''|| UN_FECHAVEN ||'''
                              ,'''|| MI_OBSERVACION ||''' 
                              ,'''|| MI_TERCERO ||'''
                              ,'''|| MI_SUCURSAL ||'''
                              ,'''|| MI_NOMBRETER ||'''
                              ,'''|| MI_DIRECTER ||'''
                              ,'''|| MI_TELETER ||'''
                              ,'''|| UN_USUARIO ||'''
                              ,SYSDATE
                              ';
                BEGIN
                    BEGIN
                        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'SF_OBJETO_COBRO' 
                                                              ,UN_ACCION => 'I' 
                                                              ,UN_CAMPOS => MI_CAMPOS 
                                                              ,UN_VALORES => MI_VALORES);
                                                              
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
                    END;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                          UN_ERROR_COD   => PCK_ERRORES.ERRR_PREDIAL_UPDATE_PRO_COBRO);
                END;
                --SF_DETALLE_COBRO
                MI_CAMPOS := 'COMPANIA, ANO, TIPOCOBRO, CODIGO_COBRO, CONSECUTIVO, CONCEPTO,
                            TERCERO, SUCURSAL, REFERENCIA, CENTRO_COSTO, AUXILIAR, FUENTE_RECURSO,
                            CANTIDAD, VALOR_UTILIDAD, VALOR_NETO, VALOR_UNITARIO, VALOR_BASE,
                            FECHA_INICIO_COBRO, FECHA_FIN_COBRO, CREATED_BY, DATE_CREATED';
                
                MI_VALORES := ''''|| UN_COMPANIA ||''' 
                              ,'  || MI_ANIO ||' 
                              ,'''|| UN_TIPOCOBRO ||''' 
                              ,'  || MI_NUM_COBRO ||'
                              ,1
                              ,'''|| MI_DATOS_COLUMNAS(2) ||'''
                              ,'''|| MI_TERCERO ||'''
                              ,'''|| MI_SUCURSAL ||'''
                              ,'''|| PCK_DATOS.FC_CONS_REFERENCIA ||'''
                              ,'''|| PCK_DATOS.FC_CONS_CENTRO ||'''
                              ,'''|| PCK_DATOS.FC_CONS_AUXILIAR ||'''
                              ,'''|| PCK_DATOS.FC_CONS_FUENTE ||'''
                              ,1
                              ,1
                              ,'  || MI_VALOR || ' 
                              ,'  || MI_VALOR || ' 
                              ,'  || MI_VALOR || ' 
                              ,'''|| MI_FECHA_INI_C ||'''
                              ,'''|| MI_FECHA_FIN_C ||'''
                              ,'''|| UN_USUARIO ||'''
                              ,SYSDATE
                              ';
                BEGIN
                    BEGIN
                        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'SF_DETALLE_COBRO' 
                                                          ,UN_ACCION => 'I' 
                                                          ,UN_CAMPOS => MI_CAMPOS 
                                                          ,UN_VALORES => MI_VALORES);
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
                    END;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                          UN_ERROR_COD   => PCK_ERRORES.ERRR_PREDIAL_UPDATE_PRO_COBRO);
                END; 
                
                IF UN_TIPOCOBRO = 'TUA' THEN
                    --INSERT SF_COBROS_TUA
                    MI_CAMPOS := 'COMPANIA, ANO, TIPO, COBRO, CONCESION, DOCUMENTO, FEC_DOCUMENTO,  
                                CONCEPTO, FUENTE, FOP_SUB, TUA_SUB, VOLUMEN_SUB, FOP_SUPERFICIAL,
                                TUA_SUPERFICIAL, VOLUMEN_SUP, VAL_PAGAR_SUB, VAL_PAGAR_SUP, COD_UH,
                                NOMBRE_UH, OBSERVACIONES, CREATED_BY, DATE_CREATED';
                                
                    MI_VALORES := ''''|| UN_COMPANIA ||''' 
                              ,'  || MI_ANIO ||' 
                              ,'''|| UN_TIPOCOBRO ||''' 
                              ,'  || MI_NUM_COBRO ||'
                              ,'''|| MI_DATOS_COLUMNAS(4) ||'''
                              ,'''|| MI_DATOS_COLUMNAS(5) ||'''
                              ,'''|| MI_DATOS_COLUMNAS(6) ||'''
                              ,'''|| MI_DATOS_COLUMNAS(7) ||'''
                              ,'''|| MI_DATOS_COLUMNAS(8) ||'''
                              ,'''|| MI_DATOS_COLUMNAS(9) ||'''
                              ,'''|| MI_DATOS_COLUMNAS(10) ||'''
                              ,'''|| MI_DATOS_COLUMNAS(11) ||'''
                              ,'''|| MI_DATOS_COLUMNAS(12) ||'''
                              ,'''|| MI_DATOS_COLUMNAS(13) ||'''
                              ,'''|| MI_DATOS_COLUMNAS(14) ||''' 
                              ,'''|| MI_DATOS_COLUMNAS(15) ||''' 
                              ,'''|| MI_DATOS_COLUMNAS(16) ||'''
                              ,'''|| MI_DATOS_COLUMNAS(19) ||'''
                              ,'''|| MI_DATOS_COLUMNAS(20) ||'''
                              ,'''|| MI_DATOS_COLUMNAS(18) ||'''
                              ,'''|| UN_USUARIO ||'''
                              ,SYSDATE
                              ';
                    BEGIN
                        BEGIN
                            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'SF_COBROS_TUA' 
                                                              ,UN_ACCION => 'I' 
                                                              ,UN_CAMPOS => MI_CAMPOS 
                                                              ,UN_VALORES => MI_VALORES);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
                        END;
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                              UN_ERROR_COD   => PCK_ERRORES.ERRR_PREDIAL_UPDATE_PRO_COBRO);
                    END; 
                    
                 ELSIF UN_TIPOCOBRO = 'TRE' THEN
                    MI_CONSECUTIVO := 1;
                    --INSERT SF_COBROS_TRE
                    MI_CAMPOS := 'COMPANIA, ANO, TIPO, COBRO, CONSECUTIVO, DOCUMENTO_TASA, FACTOR_REGIONAL_DBO,  
                                TARIFA_MINIMA_DBO, CARGA_DBO, VAL_PAGARDBO, FACTOR_REGIONAL_SST, TARIFA_MINIMA_SST, 
                                CARGA_SST, VAL_PAGARSST, VAL_PAGAR_TOTAL, COD_UH, NOMBRE_UH, APRUEBA,
                                OBSERVACIONES, CREATED_BY, DATE_CREATED';
                                
                    FOR I IN 1 .. 2
                    LOOP
                        MI_VALORES := ''''|| UN_COMPANIA ||''' 
                                  ,'  || MI_ANIO ||' 
                                  ,'''|| UN_TIPOCOBRO ||''' 
                                  ,'  || MI_NUM_COBRO ||'
                                  ,'  || MI_CONSECUTIVO ||'
                                  ,'''|| MI_DATOS_COLUMNAS(4) ||'''
                                  ,'''|| CASE WHEN MI_CONSECUTIVO = 1 THEN MI_DATOS_COLUMNAS(5) ELSE 0 END ||'''
                                  ,'''|| CASE WHEN MI_CONSECUTIVO = 1 THEN MI_DATOS_COLUMNAS(6) ELSE 0 END ||'''
                                  ,'''|| CASE WHEN MI_CONSECUTIVO = 1 THEN MI_DATOS_COLUMNAS(7) ELSE 0 END ||'''
                                  ,'''|| CASE WHEN MI_CONSECUTIVO = 1 THEN MI_DATOS_COLUMNAS(8) ELSE 0 END ||'''
                                  ,'''|| CASE WHEN MI_CONSECUTIVO = 1 THEN 0 ELSE MI_DATOS_COLUMNAS(9) END ||'''
                                  ,'''|| CASE WHEN MI_CONSECUTIVO = 1 THEN 0 ELSE MI_DATOS_COLUMNAS(10) END ||'''
                                  ,'''|| CASE WHEN MI_CONSECUTIVO = 1 THEN 0 ELSE MI_DATOS_COLUMNAS(11) END ||'''
                                  ,'''|| CASE WHEN MI_CONSECUTIVO = 1 THEN 0 ELSE MI_DATOS_COLUMNAS(12) END ||'''
                                  ,'''|| MI_DATOS_COLUMNAS(13) ||''' 
                                  ,'''|| MI_DATOS_COLUMNAS(15) ||''' 
                                  ,'''|| MI_DATOS_COLUMNAS(16) ||'''
                                  ,'''|| MI_DATOS_COLUMNAS(17) ||'''
                                  ,'''|| MI_DATOS_COLUMNAS(14) ||'''
                                  ,'''|| UN_USUARIO ||'''
                                  ,SYSDATE
                                  ';
                        BEGIN
                            BEGIN
                                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'SF_COBROS_TRE' 
                                                                  ,UN_ACCION => 'I' 
                                                                  ,UN_CAMPOS => MI_CAMPOS 
                                                                  ,UN_VALORES => MI_VALORES);
                                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
                            END;
                                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                  UN_ERROR_COD   => PCK_ERRORES.ERRR_PREDIAL_UPDATE_PRO_COBRO);
                        END;
                        MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
                    END LOOP;
                    
                 END IF;
                
          END LOOP SUBIR_DATOS;
      
      END IF;
    
    RETURN MI_RETORNO;
END FC_SUBIR_COBROS;

PROCEDURE PR_FACTURACIONLOTE
/*
    NAME              : PR_FACTURACIONLOTE
    AUTHOR MIGRACION  : GERMAN DAVID ROJAS GONZALEZ
    DATE MIGRADOR     : 28/05/2026
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : PERMITE REALIZAR FACTURACION EN LOTE

    @NAME:    factutacionLote
    @METHOD:  POST
    */
( 
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
  UN_TIPOCOBRO      IN SF_OBJETO_COBRO.TIPOCOBRO%TYPE,
  UN_COBRO_INI      IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_COBRO_FIN      IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
)  
AS 

BEGIN

<<FACTURACIONLOTE>>
  FOR RS IN (SELECT SF_OBJETO_COBRO.*  
                FROM SF_OBJETO_COBRO
                LEFT JOIN SF_FACTURA
                ON SF_FACTURA.COMPANIA = SF_OBJETO_COBRO.COMPANIA
                AND SF_FACTURA.ANO = SF_OBJETO_COBRO.ANO
                AND SF_FACTURA.ANO = SF_OBJETO_COBRO.ANO
                AND SF_FACTURA.TIPOCOBRO = SF_OBJETO_COBRO.TIPOCOBRO
                AND SF_FACTURA.CODIGO_COBRO = SF_OBJETO_COBRO.CODIGO_COBRO
                WHERE SF_OBJETO_COBRO.COMPANIA = UN_COMPANIA
                AND SF_OBJETO_COBRO.ANO = UN_ANIO
                AND SF_OBJETO_COBRO.TIPOCOBRO = UN_TIPOCOBRO
                AND SF_OBJETO_COBRO.CODIGO_COBRO BETWEEN UN_COBRO_INI AND UN_COBRO_FIN
                AND SF_FACTURA.COMPANIA IS NULL
                AND SF_OBJETO_COBRO.TIPOFACTURA IS NULL
                AND SF_OBJETO_COBRO.NRO_FACTURA IS NULL
                ORDER BY SF_OBJETO_COBRO.CODIGO_COBRO)
  LOOP
  
    BEGIN PCK_FACT_GENERAL_COM2.PR_FACTURAR_CONCEPTOS(
                        UN_COMPANIA          => RS.COMPANIA, 
                        UN_TIPOCOBRO         => RS.TIPOCOBRO, 
                        UN_CODIGO_COBRO      => RS.CODIGO_COBRO, 
                        UN_NRO_FACTURA       => 0, 
                        UN_ANIO              => RS.ANO, 
                        UN_USUARIO           => UN_USUARIO);
    END;
    
  END LOOP FACTURACIONLOTE;
END PR_FACTURACIONLOTE;
END PCK_FACT_GENERAL_COM4;