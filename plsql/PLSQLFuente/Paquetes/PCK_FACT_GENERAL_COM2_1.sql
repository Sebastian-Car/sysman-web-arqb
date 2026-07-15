create or replace PACKAGE BODY PCK_FACT_GENERAL_COM2 AS
--1

 FUNCTION FC_CALCULAR_BASE_GRAVABLE
  /*
    NAME              : FC_CALCULAR_BASE_GRAVABLE   ACCES => CalcularBaseGravable
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : JONATHAN ENRIQUE GUERRERO TORRES
    DATE MIGRADOR     : 04/102/2017
    TIME              : 03:23 PM
    DESCRIPTION       : FUNCION ENCARGADA DE CALCULAR LA BASE GRAVABLE EN EL PROCESO DE FACTURACION DE CONCEPTOS
    PARAMETROS        : UN_COMPANIA      => COMPANIA DE INGRESO A LA COMPANIA 
                        UN_TIPOCOBRO     => TIPO DE COBRO A FACTURAR
                        UN_CODIGO_COBRO  => CODIGO COBRO DEL PROCESO DE FACTURACION

  */
(
  UN_COMPANIA      PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOCOBRO      VARCHAR2,
  UN_CODIGO_COBRO   PCK_SUBTIPOS.TI_ENTERO_LARGO
)
RETURN PCK_SUBTIPOS.TI_ENTERO_LARGO AS 

MI_CON_PUBLICIDAD     PCK_SUBTIPOS.TI_PARAMETRO;
MI_ACTIVAR_PROCE      PCK_SUBTIPOS.TI_PARAMETRO;
MI_OBLIGA_BASE_GRAV   PCK_SUBTIPOS.TI_PARAMETRO;
MI_BASE_GRAV_CALCUL   PCK_SUBTIPOS.TI_PARAMETRO;
MI_PARAMETRO          PCK_SUBTIPOS.TI_PARAMETRO;
MI_VALOR_BRUTO        PCK_SUBTIPOS.TI_ENTERO_LARGO;
MI_STRSQL             PCK_SUBTIPOS.TI_STRSQL;
MI_VLR_BASE_GRAVABLE  PCK_SUBTIPOS.TI_ENTERO_LARGO;
MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
MI_RTA                PCK_SUBTIPOS.TI_ENTERO;

BEGIN
  MI_CON_PUBLICIDAD := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                             UN_NOMBRE    => 'SF CONCEPTOS DE PAUTA PUBLICITARIA',
                                             UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                             UN_FECHA_PAR => SYSDATE,
                                             UN_IND_MAYUS => 0);

  MI_ACTIVAR_PROCE  := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                             UN_NOMBRE    => 'SF CALCULAR BASE GRAVABLE CONCEPTOS PUBLICIDAD',
                                             UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                             UN_FECHA_PAR => SYSDATE,
                                             UN_IND_MAYUS => 0); 

  MI_OBLIGA_BASE_GRAV := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                             UN_NOMBRE    => 'SF OBLIGA A BASE GRAVABLE',
                                             UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                             UN_FECHA_PAR => SYSDATE,
                                             UN_IND_MAYUS => 0);                                             

  MI_BASE_GRAV_CALCUL := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                             UN_NOMBRE    => 'SF BASE GRAVABLE CALCULADA',
                                             UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                             UN_FECHA_PAR => SYSDATE,
                                             UN_IND_MAYUS => 0);


  IF NVL(MI_OBLIGA_BASE_GRAV,'NO') = 'SI' THEN 
      IF NVL(MI_BASE_GRAV_CALCUL,'NO')  = 'SI' THEN 

              SELECT SUM(D.VALOR_BASE) - SUM(D.VALOR_DESCUENTO) AS VALOR_BRUTO
                INTO MI_VALOR_BRUTO
                FROM SF_DETALLE_COBRO D
               INNER JOIN SF_CONCEPTOS C
                  ON D.COMPANIA   = C.COMPANIA
                 AND D.ANO          = C.ANO
                 AND D.TIPOCOBRO    = C.TIPOCOBRO
                 AND D.CONCEPTO     = C.CODIGO
               WHERE D.COMPANIA     = UN_COMPANIA
                 AND D.TIPOCOBRO    = UN_TIPOCOBRO
                 AND D.CODIGO_COBRO = UN_CODIGO_COBRO
                 AND C.UNICO_BASEGRAVABLE NOT IN (0);


          IF  MI_VALOR_BRUTO IS NOT NULL AND  MI_VALOR_BRUTO NOT IN (0) THEN 
              MI_VLR_BASE_GRAVABLE := MI_VALOR_BRUTO;
          ELSE 
              IF NVL(MI_ACTIVAR_PROCE,'NO') = 'SI' THEN 
                  MI_CON_PUBLICIDAD := PCK_SYSMAN_UTL.FC_COLOCARCOMILLAS(MI_CON_PUBLICIDAD);

                  SELECT SUM(D.VALOR_BASE) - SUM(D.VALOR_DESCUENTO) AS VALOR_BRUTO
                    INTO MI_VALOR_BRUTO
                    FROM SF_DETALLE_COBRO D  
                   INNER JOIN SF_CONCEPTOS C
                      ON D.COMPANIA          = C.COMPANIA
                     AND D.ANO                = C.ANO
                     AND D.TIPOCOBRO          = C.TIPOCOBRO
                     AND D.CONCEPTO           = C.CODIGO
                   WHERE D.COMPANIA           = UN_COMPANIA
                     AND D.TIPOCOBRO          = UN_TIPOCOBRO
                     AND D.CODIGO_COBRO       = UN_CODIGO_COBRO
                     AND C.UNICO_BASEGRAVABLE = 0
                     AND D.CONCEPTO          IN ( MI_CON_PUBLICIDAD ) ;
              ELSE

                  SELECT SUM(D.VALOR_BASE) - SUM(D.VALOR_DESCUENTO) AS VALOR_BRUTO
                    INTO MI_VALOR_BRUTO
                    FROM SF_DETALLE_COBRO D
                   INNER JOIN SF_CONCEPTOS C
                      ON D.COMPANIA           = C.COMPANIA
                     AND D.ANO                = C.ANO
                     AND D.TIPOCOBRO          = C.TIPOCOBRO
                     AND D.CONCEPTO           = C.CODIGO
                   WHERE D.COMPANIA           = UN_COMPANIA
                     AND D.TIPOCOBRO          = UN_TIPOCOBRO
                     AND D.CODIGO_COBRO       = UN_CODIGO_COBRO;

              END IF;

              IF MI_VALOR_BRUTO IS NOT NULL THEN 
                  MI_VLR_BASE_GRAVABLE := NVL(MI_VALOR_BRUTO,0);
              ELSE 
                  MI_VLR_BASE_GRAVABLE := 0;
              END IF;
          END IF;
      ELSE
          MI_VLR_BASE_GRAVABLE := -1;
      END IF;
  ELSE
      MI_VLR_BASE_GRAVABLE := -1;
  END IF;  

  IF MI_VLR_BASE_GRAVABLE > 0 THEN 
      BEGIN 
          BEGIN 
              MI_TABLA     := 'SF_DETALLE_COBRO';
              MI_CAMPOS    := 'BASE_GRAVABLE = ' ||MI_VLR_BASE_GRAVABLE;
              MI_CONDICION := ' COMPANIA     = '''||UN_COMPANIA||'''
                            AND TIPOCOBRO    = '''||UN_TIPOCOBRO||'''
                            AND CODIGO_COBRO = '||UN_CODIGO_COBRO;

             MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                         UN_ACCION    => 'M',
                                         UN_CAMPOS    => MI_CAMPOS,
                                         UN_CONDICION => MI_CONDICION);              
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;                         
          END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF  THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_TABLAERROR => MI_TABLA,
                                   UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTUA_BASE_GRAV); 
     END;   

  END IF;
  RETURN MI_VLR_BASE_GRAVABLE;
END FC_CALCULAR_BASE_GRAVABLE;

--2
FUNCTION FC_FACTURAR
  /*
    NAME              : FC_FACTURAR   ACCES => facturar
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : JONATHAN ENRIQUE GUERRERO TORRES
    DATE MIGRADOR     : 05/102/2017
    TIME              : 09:32 PM
    DESCRIPTION       : FUNCION ENCARGADA DE GENERAR EL NUMERO DE FACTURA Y LA FACTURACION DE LOS CONCEPTOS
    PARAMETROS        : UN_COMPANIA       => COMPANIA DE INGRESO A LA COMPANIA 
                        UN_TIPOCOBRO      => TIPO DE COBRO A FACTURAR
                        UN_FACINICIAL     => FACTURA INICIAL
                        UN_FACFINAL       => FACTURA FINAL
                        UN_POR_LOTES      => SI SE DEBE GENERAR POR LOTES TENIENDO ENCUENTA LA FACTURA INICIAL Y FACTURA FINAL 
                        UN_NOFACTURA      => NUMERO DE LA FACTURA SI EXISTE
                        UN_FECVENCIMIENTO => FECHA DE VENCIMIENTO DE LA FACTURA.
                        UN_ANIO           => AÑO DE LA FACTURACION
                        UN_USUARIO        => USUARIO QUE EJECUTA EL PROCESO.
  MODIFIER          : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MODIFIED     : 30/12/2022
    TIME MODIFIED     : 
    MODIFICATIONS     : SE AJUSTA LA FUNCION PARA REALIZAR LOS CALCULOS DE AUTORENTA Y AUTOICA

  */
(
UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
UN_TIPOCOBRO      IN VARCHAR2,
UN_FACINICIAL     IN VARCHAR2,
UN_FACFINAL       IN VARCHAR2,
UN_POR_LOTES      IN PCK_SUBTIPOS.TI_LOGICO,
UN_NOFACTURA      IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
)

 RETURN PCK_SUBTIPOS.TI_LOGICO AS 

MI_NRO_FACTURA          PCK_SUBTIPOS.TI_ENTERO_LARGO;
MI_VALIDO               PCK_SUBTIPOS.TI_LOGICO;
MI_CANT                 PCK_SUBTIPOS.TI_ENTERO_LARGO;
MI_PARAMETRO            PCK_SUBTIPOS.TI_PARAMETRO;
MI_EXISTE_COMP          PCK_SUBTIPOS.TI_ENTERO;
MI_TABLA                PCK_SUBTIPOS.TI_TABLA;
MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
MI_RTA                  PCK_SUBTIPOS.TI_ENTERO;
MI_VLR_BASE_GRAVABLE    PCK_SUBTIPOS.TI_ENTERO_LARGO;
MI_FACTURA              VARCHAR2(10 CHAR);
MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_IMPRESO              PCK_SUBTIPOS.TI_LOGICO;
MI_CAUSARFACTRECAUDO    SF_TIPO_COBRO.INTERFAZ_RECAUDO%TYPE;
MI_TIPOCOBRONOFACTURADO SF_TIPO_COBRO.TIPOCOBRO_NOFACTURADO%TYPE;
MI_MANEJA_INVENTARIO    PCK_SUBTIPOS.TI_LOGICO;
MI_DEGREDDIAN           PCK_SUBTIPOS.TI_ENTERO; 
MI_DIFERENCIAREDONDEO   PCK_SUBTIPOS.TI_DOBLE; 
MI_VALORNETO            PCK_SUBTIPOS.TI_DOBLE;  
MI_VALORTOTAL           PCK_SUBTIPOS.TI_DOBLE; 
MI_CODIGO               SF_CONCEPTOS.CODIGO%TYPE;   
MI_STRSQL               PCK_SUBTIPOS.TI_STRSQL;  
MI_RS                   SYS_REFCURSOR;
MI_RS1                  SYS_REFCURSOR;
MI_CONTADOR             PCK_SUBTIPOS.TI_ENTERO := 0;  
MI_CONTEO               PCK_SUBTIPOS.TI_ENTERO; 
MI_PERFACELECT          VARCHAR2(10 CHAR)    ;
MI_REDONDEO         PCK_SUBTIPOS.TI_ENTERO;
MI_PARAMC                VARCHAR2(100 CHAR);
MI_PARAMETRO_OBSERAVACIONES PCK_SUBTIPOS.TI_PARAMETRO;

/*INI_TICKET7723036 MPEREZ*/
MI_APLICAAUTORENTA      SF_CONCEPTOS.APLICAAUTORENTA%TYPE;
MI_PORCENTAJEAUTORENTA  PCK_SUBTIPOS.TI_PORCENTAJE;
MI_APLICAAUTOICA        SF_CONCEPTOS.APLICAAUTOICA%TYPE;
MI_PORCENTAJEAUTOICA    PCK_SUBTIPOS.TI_PORCENTAJE;
MI_VALOR_AUTORENTA      SF_DETALLE_COBRO.VALOR_AUTORENTA%TYPE;
MI_VALOR_AUTOICA        SF_DETALLE_COBRO.VALOR_AUTOICA%TYPE;
/*INI_TICKET7723036 MPEREZ*/
MI_PARAMETRO_50                   PCK_SUBTIPOS.TI_PARAMETRO;  --JM CC 2221
BEGIN

    SELECT INTERFAZ_RECAUDO,MANEJA_INVENTARIO,TIPOCOBRO_NOFACTURADO
      INTO MI_CAUSARFACTRECAUDO,
           MI_MANEJA_INVENTARIO,
           MI_TIPOCOBRONOFACTURADO
      FROM SF_TIPO_COBRO
     WHERE COMPANIA   = UN_COMPANIA
       AND ANO        = UN_ANIO
       AND CODIGO     = UN_TIPOCOBRO;   


 --7707082(Mrosero) 11/02/2022      
  MI_REDONDEO:=NVL(TO_NUMBER(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA=>UN_COMPANIA,
                                                  UN_NOMBRE   =>'SF REDONDEO VALOR',
                                                  UN_MODULO   =>PCK_DATOS.FC_MODULOFACTURACIONGENERAL(),
                                                  UN_FECHA_PAR=>SYSDATE,
                                                  UN_IND_MAYUS=>0)),0); 
--7707082(Mrosero) 11/02/2022


  MI_DEGREDDIAN :=  NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                           UN_NOMBRE    => 'SF NUMERO DIGITOS REDONDEO DIAN',
                                           UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL(),
                                           UN_FECHA_PAR => SYSDATE,
                                           UN_IND_MAYUS => -1),0);

   MI_PERFACELECT :=  NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                           UN_NOMBRE    => 'SF PERMITE CONFIGURAR FACTURA ELECTRONICA',
                                           UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL(),
                                           UN_FECHA_PAR => SYSDATE,
                                           UN_IND_MAYUS => -1),0);

 --7711232_Ini(Carenas) 14/03/2022   

   MI_PARAMC :=PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                            UN_NOMBRE    => 'NOMBRE COMPROBANTE CAUSACION',
                                            UN_MODULO   => PCK_DATOS.FC_MODULOFACTURACIONGENERAL(),
                                            UN_FECHA_PAR => SYSDATE);
 
--7711232_Fin(Carenas) 14/03/2022    
--JM INI CC 2221
    MI_PARAMETRO_50 := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                             UN_NOMBRE    => 'REDONDEO AL 50 MAS CERCANO EN RECAUDO DE FACTURAS',
                                             UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                             UN_FECHA_PAR => SYSDATE,
                                             UN_IND_MAYUS =>0),'NO');
--JM FIN CC 2221

    FOR RS IN (SELECT CODIGO_COBRO,
                      EXTRACT( YEAR FROM FECHA_SOLICITUD) AS ANOSOLICITUD,
                      TERCERO,
                      SUCURSAL,
                      OBSERVACIONES,
                      INDREVERSO,
                      FECHA_SOLICITUD,
                      VALOR_TOTAL,
                      CENTRO_COSTO,
                      AUXILIAR
                 FROM SF_OBJETO_COBRO
                WHERE COMPANIA = UN_COMPANIA
                  AND TIPOCOBRO  = UN_TIPOCOBRO
                  AND CODIGO_COBRO BETWEEN UN_FACINICIAL AND UN_FACFINAL
                  AND IMPRESO IN (0))
    LOOP


      SELECT NVL(SUM(VALOR_NETO),0) VALORNETO          
      INTO MI_VALORNETO
      FROM SF_DETALLE_COBRO 
      WHERE COMPANIA   = UN_COMPANIA
      AND CODIGO_COBRO = UN_FACINICIAL 
      AND TIPOCOBRO = UN_TIPOCOBRO
      AND ANO = UN_ANIO;

    IF MI_PERFACELECT = 'SI' THEN
   --'(Ealvarez)(02/10/2019)
       IF MI_DEGREDDIAN < 10 THEN

 --7707082(Mrosero) 11/02/2022
       --  MI_VALORTOTAL := ROUND(NVL(MI_VALORNETO, 0),MI_DEGREDDIAN);

           MI_VALORTOTAL := ROUND(NVL(MI_VALORNETO, 0),MI_REDONDEO);
--7707082(Mrosero) 11/02/2022

       ELSE
           MI_VALORTOTAL := FLOOR(NVL(MI_VALORNETO, 0) / MI_DEGREDDIAN + 0.501) * MI_DEGREDDIAN;                  
       END IF;

   --'Rutina para el manejo de diferencias para redondeos de faturacion electronica  
        IF MI_DEGREDDIAN = 0 THEN

            MI_DIFERENCIAREDONDEO :=  ROUND(FLOOR(NVL(MI_VALORNETO, 0) + 0.501) - MI_VALORNETO,2);

            IF UPPER(MI_PARAMETRO_50) = 'SI' THEN -- JM CC 2221
                MI_DIFERENCIAREDONDEO :=  MOD(FLOOR(NVL(MI_VALORNETO, 0)), 100) + (NVL(MI_VALORNETO, 0) - FLOOR(NVL(MI_VALORNETO, 0)));
                IF MI_DIFERENCIAREDONDEO <= 24.99 OR (MI_DIFERENCIAREDONDEO > 50 AND MI_DIFERENCIAREDONDEO <= 74.99) THEN
                    IF MI_DIFERENCIAREDONDEO <> 0 THEN
                        IF (MI_DIFERENCIAREDONDEO > 50 AND MI_DIFERENCIAREDONDEO <= 74.99) THEN 
                            MI_DIFERENCIAREDONDEO := 50 - MI_DIFERENCIAREDONDEO;
                         ELSE
                            MI_DIFERENCIAREDONDEO := MI_DIFERENCIAREDONDEO * -1;
                      END IF;
                    END IF;
                  ELSE
                    IF MI_DIFERENCIAREDONDEO <> 0 THEN
                          IF MI_DIFERENCIAREDONDEO <= 50 THEN
                            MI_DIFERENCIAREDONDEO := 50 - MI_DIFERENCIAREDONDEO;
                          ELSE
                            MI_DIFERENCIAREDONDEO := 100 - MI_DIFERENCIAREDONDEO;
                          END IF;
                    END IF;
                  END IF;
            END IF; -- FIN JM CC 2221

        ELSIF MI_DEGREDDIAN < 10 AND MI_DEGREDDIAN > 0 THEN

            MI_DIFERENCIAREDONDEO :=  ROUND(MI_VALORTOTAL ,MI_DEGREDDIAN) - MI_VALORNETO;

        ELSE
            MI_DIFERENCIAREDONDEO :=  ROUND(FLOOR(NVL(MI_VALORNETO, 0) / MI_DEGREDDIAN  + 0.501) * MI_DEGREDDIAN  - MI_VALORNETO,3);
        END IF;


        BEGIN 
            BEGIN
                MI_TABLA := 'SF_OBJETO_COBRO';

             --19/02/2021 @eamaya

                MI_CAMPOS := 'AJUSTE    = '||MI_DIFERENCIAREDONDEO||' ';
              --19/02/2021 @eamaya

                MI_CONDICION := ' COMPANIA     = '''||UN_COMPANIA||'''
                              AND TIPOCOBRO    = '''||UN_TIPOCOBRO||'''
                              AND CODIGO_COBRO = '||RS.CODIGO_COBRO||'
                              AND ANO          = '||UN_ANIO;
                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
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

        IF MI_DIFERENCIAREDONDEO <= 0 THEN --- Descuentos

            MI_STRSQL := 'SELECT CODIGO, APLICAAUTORENTA, PORCENTAJEAUTORENTA, APLICAAUTOICA, PORCENTAJEAUTOICA                   
                FROM SF_CONCEPTOS 
                WHERE COMPANIA = '''||UN_COMPANIA||'''
                  AND ANO = '||UN_ANIO||' 
                  AND TIPOCOBRO    = '''||UN_TIPOCOBRO||'''
                  AND DESCUENTOS_FE NOT IN (0)';              


        ELSIF   MI_DIFERENCIAREDONDEO > 0 THEN --- Cargos

        MI_STRSQL := ' SELECT CODIGO, APLICAAUTORENTA, PORCENTAJEAUTORENTA, APLICAAUTOICA, PORCENTAJEAUTOICA
                       FROM SF_CONCEPTOS 
                       WHERE COMPANIA = '''||UN_COMPANIA||'''
                         AND ANO = '||UN_ANIO||' 
                         AND TIPOCOBRO    = '''||UN_TIPOCOBRO||'''
                         AND CARGO_FE NOT IN (0)';              

        END IF;        
        EXECUTE IMMEDIATE 'SELECT COUNT(1) FROM ('||MI_STRSQL||')' INTO MI_CONTEO;

        IF MI_CONTEO >=1 THEN   

        OPEN MI_RS FOR MI_STRSQL; 
          LOOP --- En caso de que este configurado el concepto              
            FETCH MI_RS 
       INTO MI_CODIGO, MI_APLICAAUTORENTA, MI_PORCENTAJEAUTORENTA, MI_APLICAAUTOICA, MI_PORCENTAJEAUTOICA;  /*TICKET7723036 MPEREZ*/
            EXIT WHEN MI_RS%NOTFOUND;                                                         


             SELECT COUNT(CONCEPTO)
             INTO MI_CONTADOR
             FROM SF_DETALLE_COBRO
             WHERE COMPANIA= UN_COMPANIA 
              AND ANO=UN_ANIO 
              AND TIPOCOBRO=UN_TIPOCOBRO
              AND CODIGO_COBRO=UN_FACINICIAL
              AND CONCEPTO=MI_CODIGO;                              

              IF MI_CONTADOR IN (0) THEN  

        --7709714 (Mrosero) 17/02/2022 
            --IF MI_DEGREDDIAN <> 0 THEN

            IF MI_REDONDEO <> 1 THEN
        --7709714 (Mrosero) 17/02/2022  
        IF MI_DIFERENCIAREDONDEO <> 0 THEN

                  BEGIN
                   BEGIN
            /*INI_TICKET7723036 MPEREZ*/
                        IF(MI_APLICAAUTORENTA NOT IN (0)) THEN 
                            MI_VALOR_AUTORENTA := ROUND(MI_DIFERENCIAREDONDEO*(MI_PORCENTAJEAUTORENTA/100),2);
                        ELSE
                            MI_VALOR_AUTORENTA := 0;
                        END IF;
                        
                        IF(MI_APLICAAUTOICA <> 0) THEN 
                            MI_VALOR_AUTOICA := ROUND(MI_DIFERENCIAREDONDEO*(MI_PORCENTAJEAUTOICA/100),2);
                        ELSE
                            MI_VALOR_AUTOICA := 0;
                        END IF;

                          MI_CAMPOS := 'COMPANIA,ANO,TIPOCOBRO,CODIGO_COBRO,CONCEPTO,CANTIDAD,VALOR_BASE,VALOR_IVA,VALOR_RETEFUENTE,VALOR_DESCUENTO,VALOR_ICA,VALOR_NETO, VALOR_COMPRA, VALOR_UTILIDAD, 
                                        FECHA_INICIO_COBRO, FECHA_FIN_COBRO, TERCERO, SUCURSAL, CENTRO_COSTO, AUXILIAR, VALOR_UNITARIO, VALOR_AUTORENTA,VALOR_AUTOICA';

                          MI_VALORES := ' '''||UN_COMPANIA||''','||UN_ANIO||','''||UN_TIPOCOBRO||''','||UN_FACINICIAL||','''||MI_CODIGO||''',1,'||MI_DIFERENCIAREDONDEO||',0,0,0,0,'||MI_DIFERENCIAREDONDEO||',0,0
                                ,'''||CURRENT_DATE||''','''||CURRENT_DATE||''','''||RS.TERCERO||''','''||RS.SUCURSAL||''','''||RS.CENTRO_COSTO||''','''||RS.AUXILIAR||''','||MI_DIFERENCIAREDONDEO||','||MI_VALOR_AUTORENTA||','||MI_VALOR_AUTOICA||' ';  /*FIN_TICKET7723036 MPEREZ*/

                      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => 'SF_DETALLE_COBRO',
                                                      UN_ACCION   => 'I',
                                                      UN_CAMPOS   => MI_CAMPOS,
                                                      UN_VALORES  => MI_VALORES);

                                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                                                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;                         
                          END;
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF  THEN 
                            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                       UN_TABLAERROR => MI_TABLA,
                                                       UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSCONCECOBRO);                                                        
                       END;                                                    


               END IF;
               END IF;
             ELSE
                    BEGIN
                     BEGIN

                            MI_CAMPOS    := 'VALOR_BASE   = '||MI_DIFERENCIAREDONDEO||',
                                             VALOR_NETO   = '||MI_DIFERENCIAREDONDEO||',
                                             VALOR_UNITARIO = '||MI_DIFERENCIAREDONDEO||',
                                             MODIFIED_BY   = '''||UN_USUARIO||''',
                                             DATE_MODIFIED = SYSDATE';

                            MI_CONDICION := ' COMPANIA     = '''||UN_COMPANIA||'''
                                          AND ANO          = '||UN_ANIO||'  
                                          AND TIPOCOBRO    = '''||UN_TIPOCOBRO||'''
                                          AND CODIGO_COBRO = '||RS.CODIGO_COBRO||'
                                          AND CONCEPTO     = '''||MI_CODIGO||''' ';

                                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'SF_DETALLE_COBRO',
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
             END IF;




          END LOOP;
        CLOSE MI_RS;

       END IF;
     END IF;  
        MI_VALIDO := 0;

        IF UN_NOFACTURA <> 0 THEN 
            MI_NRO_FACTURA := UN_NOFACTURA;
        ELSE 

            MI_NRO_FACTURA := PCK_FACT_GENERAL_COM1.FC_ENUMERAR_FACTURA (UN_COMPANIA   => UN_COMPANIA,
                                                                      UN_TIPOCOBRO  => UN_TIPOCOBRO,
                                                                      UN_ANIO_COBRO => UN_ANIO);
        END IF;


        WHILE MI_VALIDO IN (0) LOOP
            BEGIN
                SELECT COUNT(*) AS CANT
                  INTO MI_CANT
                  FROM SF_FACTURA
                 WHERE SF_FACTURA.COMPANIA        = UN_COMPANIA
                   AND SF_FACTURA.TIPO_FACTURA    = UN_TIPOCOBRO
                   AND SF_FACTURA.NUMERO_FACTURA  = MI_NRO_FACTURA
                   AND SF_FACTURA.ANO             = UN_ANIO;
             EXCEPTION WHEN NO_DATA_FOUND THEN 
                MI_CANT := NULL;
             END ;

           IF MI_CANT IS NOT NULL THEN 
              IF MI_CANT > 0 THEN 
                  IF  MI_NRO_FACTURA = 0 THEN 
                      MI_NRO_FACTURA := PCK_FACT_GENERAL_COM1.FC_ENUMERAR_FACTURA (UN_COMPANIA   => UN_COMPANIA,
                                                                                   UN_TIPOCOBRO  => UN_TIPOCOBRO,
                                                                                   UN_ANIO_COBRO => UN_ANIO);
                      MI_VALIDO := 0;
                  ELSE
                      IF RS.INDREVERSO  = 0 THEN 
                          BEGIN
                              BEGIN
                                 RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                               END;
                               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN

                              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                         UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_NRO_FACTURA_EXIST);
                          END;
                      ELSE
                          MI_VALIDO := -1;                    
                      END IF;
                  END IF;
              ELSE
                  MI_VALIDO := -1;
              END IF;
           ELSE
              MI_VALIDO := -1;
           END IF;

        END LOOP;

        MI_PARAMETRO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                              UN_NOMBRE    => 'SF NRO FACTURA MANUAL COMO NRO COMPROBANTE',
                                              UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                              UN_FECHA_PAR => SYSDATE,
                                              UN_IND_MAYUS => 0);

        IF NVL(MI_PARAMETRO,'NO') = 'SI' THEN 
            SELECT COUNT (*)  EXISTE_COMP
              INTO MI_EXISTE_COMP
              FROM COMPROBANTE_CNT
             WHERE COMPANIA   = UN_COMPANIA
               AND ANO        = UN_ANIO
               AND TIPO       = UN_TIPOCOBRO
               AND NUMERO     = MI_NRO_FACTURA;

               IF MI_EXISTE_COMP > 0 THEN 
                  BEGIN
                      BEGIN
                         MI_MSGERROR(0).CLAVE := 'NRO_FACTURA';
                         MI_MSGERROR(1).VALOR := MI_NRO_FACTURA; 
                         RAISE PCK_EXCEPCIONES.EXC_FACTURACION;                         
                       END;
                       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN

                      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                 UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_COMPROB_EXIST,
                                                 UN_REEMPLAZOS => MI_MSGERROR);
                  END;
               END IF;
        END IF;

        MI_PARAMETRO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                              UN_NOMBRE    => 'SF MANEJA CONSECUTIVO DELINEACION URBANA',
                                              UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                              UN_FECHA_PAR => SYSDATE,
                                              UN_IND_MAYUS => 0);

        -- se consulta parametro para modificar la observacion y que esta no lleva el prefijo y el numero al inicio
        MI_PARAMETRO_OBSERAVACIONES := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                              UN_NOMBRE    => 'SF OBSERVACION SIN PREFIJO Y NUMERO DE FACTURA',
                                              UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                              UN_FECHA_PAR => SYSDATE,
                                              UN_IND_MAYUS => 0);
        IF NVL(MI_PARAMETRO,'NO') = 'SI' THEN 
            BEGIN 
                BEGIN 
                    MI_TABLA   := 'SF_FACTURA';
--7742952_SIGEC
                    MI_CAMPOS  := ' COMPANIA,TIPO_FACTURA,NUMERO_FACTURA,TIPOCOBRO,CODIGO_COBRO,ANO,TERCERO,SUCURSAL,CENTRO_COSTO,AUXILIAR,
                                   FECHA_EXPEDICION,FECHA_VENCIMIENTO,VALOR_TOTAL,OBSERVACIONES,CREATED_BY,DATE_CREATED,ESACUERDO,
                                   INTERFAZADA,AFECTO_INVENTARIO,TIPOCPTE_RECAUDO,CUENTA_RECAUDO,NIT_TERCEROFACTURADO,
                                   NOMBRE_TERCEROFACTURADO,FORMULARIO_DELURBANA,DIRECCION,CODIGOPOSTAL,GRUPO_CONCEPTOS,CANTIDAD_GRUPO,TOTAL_GRUPO,
                                   REF_FACTURACION,TIPO_MEDIOPAGO,TIPO_PAGO,AJUSTE, FACTURA_DOLARES,TRM_DOLARES_FAC,TRM_DOLARES_REC,VALOR_TOTAL_DOLARES, DESCRIPCION_TIPOPAGO,NROCONTRATOSIGEC,TIPOCONTRATOSIGEC';

                  IF(NVL(MI_PARAMETRO_OBSERAVACIONES, 'NO') = 'NO') THEN
                        MI_VALORES :=  'SELECT COMPANIA,
                                           '''||UN_TIPOCOBRO||''',
                                           '||MI_NRO_FACTURA||',
                                           TIPOCOBRO,
                                           CODIGO_COBRO,
                                           ANO,
                                           TERCERO,
                                           SUCURSAL,
                                           CENTRO_COSTO,
                                           AUXILIAR,
                                           FECHA_SOLICITUD,
                                           FECHA_VENCIMIENTO,
                                           VALOR_TOTAL,
                                           ''Factura '||UN_TIPOCOBRO||' - '||MI_NRO_FACTURA||' - ''||OBSERVACIONES||'' - ''||FORMULARIO_DELURBANA OBSERVACIONES,
                                           '''||UN_USUARIO||''',
                                           SYSDATE,                              
                                           0,
                                           0,
                                           0,
                                           TIPOCPTE_RECAUDO,
                                           CUENTA_RECAUDO,
                                           NIT_TERCEROFACTURADO,
                                           NOMBRE_TERCEROFACTURADO,
                                           FORMULARIO_DELURBANA,
                                           DIRECCION,
                                           CODIGOPOSTAL,
                                           GRUPO_CONCEPTOS,
                                           CANTIDAD_GRUPO,
                                           TOTAL_GRUPO,
                                           REF_FACTURACION,
                                           TIPO_MEDIOPAGO,
                                           TIPO_PAGO,AJUSTE,
                                           FACTURA_DOLARES,
                                           TRM_DOLARES_FAC,
                                           TRM_DOLARES_REC,
                                           VALOR_TOTAL_DOLARES,
                                           DESCRIPCION_TIPOPAGO,
                                           NROCONTRATOSIGEC,
                                           TIPOCONTRATOSIGEC
                                      FROM SF_OBJETO_COBRO
                                      WHERE COMPANIA   = '''||UN_COMPANIA||'''
                                      AND TIPOCOBRO    = '''||UN_TIPOCOBRO||'''
                                      AND CODIGO_COBRO = '||RS.CODIGO_COBRO;
                    ELSE
                        MI_VALORES :=  'SELECT COMPANIA,
                                           '''||UN_TIPOCOBRO||''',
                                           '||MI_NRO_FACTURA||',
                                           TIPOCOBRO,
                                           CODIGO_COBRO,
                                           ANO,
                                           TERCERO,
                                           SUCURSAL,
                                           CENTRO_COSTO,
                                           AUXILIAR,
                                           FECHA_SOLICITUD,
                                           FECHA_VENCIMIENTO,
                                           VALOR_TOTAL,
                                           OBSERVACIONES,
                                           '''||UN_USUARIO||''',
                                           SYSDATE,                              
                                           0,
                                           0,
                                           0,
                                           TIPOCPTE_RECAUDO,
                                           CUENTA_RECAUDO,
                                           NIT_TERCEROFACTURADO,
                                           NOMBRE_TERCEROFACTURADO,
                                           FORMULARIO_DELURBANA,
                                           DIRECCION,
                                           CODIGOPOSTAL,
                                           GRUPO_CONCEPTOS,
                                           CANTIDAD_GRUPO,
                                           TOTAL_GRUPO,
                                           REF_FACTURACION,
                                           TIPO_MEDIOPAGO,
                                           TIPO_PAGO,AJUSTE,
                                           FACTURA_DOLARES,
                                           TRM_DOLARES_FAC,
                                           TRM_DOLARES_REC,
                                           VALOR_TOTAL_DOLARES,
                                           DESCRIPCION_TIPOPAGO,
                                           NROCONTRATOSIGEC,
                                           TIPOCONTRATOSIGEC
                                      FROM SF_OBJETO_COBRO
                                      WHERE COMPANIA   = '''||UN_COMPANIA||'''
                                      AND TIPOCOBRO    = '''||UN_TIPOCOBRO||'''
                                      AND CODIGO_COBRO = '||RS.CODIGO_COBRO;
                    END IF;
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => MI_TABLA,
                                                          UN_ACCION   => 'IS',
                                                          UN_CAMPOS   => MI_CAMPOS,
                                                          UN_VALORES  => MI_VALORES);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_TABLAERROR => MI_TABLA,
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_REGISTRAR_FACTURA);  
            END;  

        ELSE 

            IF RS.INDREVERSO = -1 THEN 
                BEGIN 
                    BEGIN 
                        MI_TABLA     := 'SF_FACTURA' ;
                        MI_CAMPOS    := 'VALOR_TOTAL   = '||RS.VALOR_TOTAL||',
                                         MODIFIED_BY   = '''||UN_USUARIO||''',
                                         DATE_MODIFIED = SYSDATE';
                        MI_CONDICION := ' COMPANIA     = '''||UN_COMPANIA||'''
                                      AND TIPOCOBRO    = '''||UN_TIPOCOBRO||'''
                                      AND CODIGO_COBRO = '||RS.CODIGO_COBRO;

                       MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                   UN_ACCION    => 'M',
                                                   UN_CAMPOS    => MI_CAMPOS,
                                                   UN_CONDICION => MI_CONDICION);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                      RAISE PCK_EXCEPCIONES.EXC_FACTURACION;                          
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                          UN_TABLAERROR => MI_TABLA,
                                          UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTUAL_VALTOTAL); 
                END;

                  IF MI_RTA = 0 THEN 
                      BEGIN 
                          BEGIN
                                MI_TABLA   := 'SF_FACTURA';
                                MI_CAMPOS  := 'COMPANIA, TIPO_FACTURA, NUMERO_FACTURA, TIPOCOBRO, ANO, CODIGO_COBRO, TERCERO, SUCURSAL, CENTRO_COSTO, AUXILIAR, FECHA_EXPEDICION, 
                                         FECHA_VENCIMIENTO, VALOR_TOTAL, OBSERVACIONES, CREATED_BY, DATE_CREATED, ESACUERDO, INTERFAZADA, AFECTO_INVENTARIO, 
                                         TIPOCPTE_RECAUDO, CUENTA_RECAUDO, NIT_TERCEROFACTURADO, NOMBRE_TERCEROFACTURADO, FORMULARIO_DELURBANA, DIRECCION,
                                         CODIGOPOSTAL, GRUPO_CONCEPTOS, CANTIDAD_GRUPO, TOTAL_GRUPO,
                                         REF_FACTURACION,TIPO_MEDIOPAGO,TIPO_PAGO,AJUSTE, FACTURA_DOLARES,TRM_DOLARES_FAC,TRM_DOLARES_REC,VALOR_TOTAL_DOLARES,DESCRIPCION_TIPOPAGO';

                                IF(NVL(MI_PARAMETRO_OBSERAVACIONES, 'NO') = 'NO') THEN
                                    MI_VALORES := 'SELECT COMPANIA,
                                                      '''||UN_TIPOCOBRO||''',
                                                      '||MI_NRO_FACTURA||',
                                                      TIPOCOBRO,
                                                      ANO,
                                                      CODIGO_COBRO,
                                                      TERCERO,
                                                      SUCURSAL,
                                                      CENTRO_COSTO,
                                                      AUXILIAR,
                                                      FECHA_SOLICITUD,
                                                      FECHA_VENCIMIENTO,
                                                      VALOR_TOTAL,
                                                      ''Factura '||UN_TIPOCOBRO||' - '||MI_NRO_FACTURA||' - ''||OBSERVACIONES||'' - ''||FORMULARIO_DELURBANA OBSERVACIONES,                                                  
                                                      '''||UN_USUARIO||''',
                                                      SYSDATE,    
                                                      0,
                                                      0,
                                                      0,
                                                      TIPOCPTE_RECAUDO,
                                                      CUENTA_RECAUDO,
                                                      NIT_TERCEROFACTURADO,
                                                      NOMBRE_TERCEROFACTURADO,
                                                      FORMULARIO_DELURBANA,
                                                      DIRECCION,
                                                      CODIGOPOSTAL,
                                                      GRUPO_CONCEPTOS,
                                                      CANTIDAD_GRUPO,
                                                      TOTAL_GRUPO,
                                                      REF_FACTURACION,
                                                      TIPO_MEDIOPAGO,
                                                      TIPO_PAGO,
                                                      AJUSTE,
                                                      FACTURA_DOLARES,
                                                      TRM_DOLARES_FAC,
                                                      TRM_DOLARES_REC,
                                                      VALOR_TOTAL_DOLARES,
                                                      DESCRIPCION_TIPOPAGO
                                                 FROM SF_OBJETO_COBRO
                                                WHERE COMPANIA   = '''||UN_COMPANIA||'''
                                                  AND TIPOCOBRO    = '''||UN_TIPOCOBRO||'''
                                                  AND CODIGO_COBRO = '||RS.CODIGO_COBRO;
                                ELSE
                                    MI_VALORES := 'SELECT COMPANIA,
                                                      '''||UN_TIPOCOBRO||''',
                                                      '||MI_NRO_FACTURA||',
                                                      TIPOCOBRO,
                                                      ANO,
                                                      CODIGO_COBRO,
                                                      TERCERO,
                                                      SUCURSAL,
                                                      CENTRO_COSTO,
                                                      AUXILIAR,
                                                      FECHA_SOLICITUD,
                                                      FECHA_VENCIMIENTO,
                                                      VALOR_TOTAL,
                                                      OBSERVACIONES,                                                 
                                                      '''||UN_USUARIO||''',
                                                      SYSDATE,    
                                                      0,
                                                      0,
                                                      0,
                                                      TIPOCPTE_RECAUDO,
                                                      CUENTA_RECAUDO,
                                                      NIT_TERCEROFACTURADO,
                                                      NOMBRE_TERCEROFACTURADO,
                                                      FORMULARIO_DELURBANA,
                                                      DIRECCION,
                                                      CODIGOPOSTAL,
                                                      GRUPO_CONCEPTOS,
                                                      CANTIDAD_GRUPO,
                                                      TOTAL_GRUPO,
                                                      REF_FACTURACION,
                                                      TIPO_MEDIOPAGO,
                                                      TIPO_PAGO,
                                                      AJUSTE,
                                                      FACTURA_DOLARES,
                                                      TRM_DOLARES_FAC,
                                                      TRM_DOLARES_REC,
                                                      VALOR_TOTAL_DOLARES,
                                                      DESCRIPCION_TIPOPAGO,
                                           			  NROCONTRATOSIGEC,
                                           			  TIPOCONTRATOSIGEC
                                                 FROM SF_OBJETO_COBRO
                                                WHERE COMPANIA   = '''||UN_COMPANIA||'''
                                                  AND TIPOCOBRO    = '''||UN_TIPOCOBRO||'''
                                                  AND CODIGO_COBRO = '||RS.CODIGO_COBRO;
                                END IF;

                                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => MI_TABLA,
                                                            UN_ACCION   => 'IS',
                                                            UN_CAMPOS   => MI_CAMPOS,
                                                            UN_VALORES  => MI_VALORES);

                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                            RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                          END;
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN 
                          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                     UN_TABLAERROR => MI_TABLA,
                                                     UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_REGISTRAR_FACTURA);  
                      END;                  

                  END IF;
            ELSE 
                BEGIN 
                    BEGIN
                          MI_TABLA   := 'SF_FACTURA';
                          MI_CAMPOS  := 'COMPANIA, TIPO_FACTURA, NUMERO_FACTURA, TIPOCOBRO, ANO, CODIGO_COBRO, TERCERO, SUCURSAL, CENTRO_COSTO, AUXILIAR, FECHA_EXPEDICION, 
                                         FECHA_VENCIMIENTO, VALOR_TOTAL, OBSERVACIONES, CREATED_BY, DATE_CREATED, ESACUERDO, INTERFAZADA, AFECTO_INVENTARIO, 
                                         TIPOCPTE_RECAUDO, CUENTA_RECAUDO, NIT_TERCEROFACTURADO, NOMBRE_TERCEROFACTURADO, FORMULARIO_DELURBANA, DIRECCION,
                                         CODIGOPOSTAL, GRUPO_CONCEPTOS, CANTIDAD_GRUPO, TOTAL_GRUPO,
                                         REF_FACTURACION,TIPO_MEDIOPAGO,TIPO_PAGO,AJUSTE, FACTURA_DOLARES,TRM_DOLARES_FAC,TRM_DOLARES_REC,VALOR_TOTAL_DOLARES,DESCRIPCION_TIPOPAGO, NROCONTRATOSIGEC,TIPOCONTRATOSIGEC';
                         IF(NVL(MI_PARAMETRO_OBSERAVACIONES, 'NO') = 'NO') THEN
                                MI_VALORES := 'SELECT COMPANIA,
                                                '''||UN_TIPOCOBRO||''',
                                                '||MI_NRO_FACTURA||',                                    
                                                TIPOCOBRO,
                                                ANO,
                                                CODIGO_COBRO,
                                                TERCERO,
                                                SUCURSAL,
                                                CENTRO_COSTO,
                                                AUXILIAR,
                                                FECHA_SOLICITUD,
                                                FECHA_VENCIMIENTO,
                                                VALOR_TOTAL,
                                                '''||MI_PARAMC||' - '||MI_NRO_FACTURA||' - ''||OBSERVACIONES||'' - ''||FORMULARIO_DELURBANA OBSERVACIONES,
                                                '''||UN_USUARIO||''',
                                                SYSDATE,
                                                0,
                                                0,
                                                0,
                                                TIPOCPTE_RECAUDO,
                                                CUENTA_RECAUDO,
                                                NIT_TERCEROFACTURADO,
                                                NOMBRE_TERCEROFACTURADO,
                                                FORMULARIO_DELURBANA,
                                                DIRECCION,
                                                CODIGOPOSTAL,
                                                GRUPO_CONCEPTOS,
                                                CANTIDAD_GRUPO,
                                                TOTAL_GRUPO,
                                                REF_FACTURACION,
                                                TIPO_MEDIOPAGO,
                                                TIPO_PAGO,AJUSTE,
                                                FACTURA_DOLARES,
                                                TRM_DOLARES_FAC,
                                                TRM_DOLARES_REC,
                                                VALOR_TOTAL_DOLARES,
                                                DESCRIPCION_TIPOPAGO,
                                                NROCONTRATOSIGEC,
                                                TIPOCONTRATOSIGEC
                                           FROM SF_OBJETO_COBRO
                                          WHERE COMPANIA    = '''||UN_COMPANIA||'''
                                           AND TIPOCOBRO    = '''||UN_TIPOCOBRO||'''
                                           AND CODIGO_COBRO = '||RS.CODIGO_COBRO;
                          ELSE
                                MI_VALORES := 'SELECT COMPANIA,
                                                '''||UN_TIPOCOBRO||''',
                                                '||MI_NRO_FACTURA||',                                    
                                                TIPOCOBRO,
                                                ANO,
                                                CODIGO_COBRO,
                                                TERCERO,
                                                SUCURSAL,
                                                CENTRO_COSTO,
                                                AUXILIAR,
                                                FECHA_SOLICITUD,
                                                FECHA_VENCIMIENTO,
                                                VALOR_TOTAL,
                                                OBSERVACIONES,
                                                '''||UN_USUARIO||''',
                                                SYSDATE,
                                                0,
                                                0,
                                                0,
                                                TIPOCPTE_RECAUDO,
                                                CUENTA_RECAUDO,
                                                NIT_TERCEROFACTURADO,
                                                NOMBRE_TERCEROFACTURADO,
                                                FORMULARIO_DELURBANA,
                                                DIRECCION,
                                                CODIGOPOSTAL,
                                                GRUPO_CONCEPTOS,
                                                CANTIDAD_GRUPO,
                                                TOTAL_GRUPO,
                                                REF_FACTURACION,
                                                TIPO_MEDIOPAGO,
                                                TIPO_PAGO,AJUSTE,
                                                FACTURA_DOLARES,
                                                TRM_DOLARES_FAC,
                                                TRM_DOLARES_REC,
                                                VALOR_TOTAL_DOLARES,
                                                DESCRIPCION_TIPOPAGO,
                                                NROCONTRATOSIGEC,
                                                TIPOCONTRATOSIGEC
                                           FROM SF_OBJETO_COBRO
                                          WHERE COMPANIA    = '''||UN_COMPANIA||'''
                                           AND TIPOCOBRO    = '''||UN_TIPOCOBRO||'''
                                           AND CODIGO_COBRO = '||RS.CODIGO_COBRO;
                          END IF;

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => MI_TABLA,
                                                      UN_ACCION   => 'IS',
                                                      UN_CAMPOS   => MI_CAMPOS,
                                                      UN_VALORES  => MI_VALORES);

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                      RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                               UN_TABLAERROR => MI_TABLA,
                                               UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_REGISTRAR_FACTURA);  
                END;

            END IF;
        END IF;

            IF MI_RTA = 0 AND RS.INDREVERSO = 0 THEN 
                BEGIN
                    BEGIN
                       RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                     END;
                     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN

                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                               UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_REGISTRAR_FACTURA);
                END;               
            ELSE 
                MI_VLR_BASE_GRAVABLE := PCK_FACT_GENERAL_COM2.FC_CALCULAR_BASE_GRAVABLE(UN_COMPANIA     => UN_COMPANIA, 
                                                                                        UN_TIPOCOBRO    => UN_TIPOCOBRO,
                                                                                        UN_CODIGO_COBRO => RS.CODIGO_COBRO);

                FOR RS_VAL_DETALLE  IN (SELECT 'X' AS COBRO,
                                               CONCEPTO
                                          FROM SF_DETALLE_COBRO
                                         WHERE COMPANIA                      = UN_COMPANIA
                                           AND SF_DETALLE_COBRO.TIPOCOBRO    = UN_TIPOCOBRO
                                           AND SF_DETALLE_COBRO.CODIGO_COBRO = RS.CODIGO_COBRO)   
                LOOP
                    BEGIN
                        SELECT 'X' AS FACTURA
                          INTO MI_FACTURA
                          FROM SF_DETALLE_FACTURA
                         WHERE COMPANIA                     = UN_COMPANIA
                           AND SF_DETALLE_FACTURA.TIPOCOBRO = UN_TIPOCOBRO
                           AND SF_DETALLE_FACTURA.FACTURA   = MI_NRO_FACTURA
                           AND SF_DETALLE_FACTURA.CONCEPTO  = RS_VAL_DETALLE.CONCEPTO;
                      EXCEPTION WHEN NO_DATA_FOUND THEN 
                        MI_FACTURA := NULL;
                    END ;
                    
                    BEGIN
                        SELECT APLICAAUTORENTA, PORCENTAJEAUTORENTA, APLICAAUTOICA, PORCENTAJEAUTOICA  
                          INTO MI_APLICAAUTORENTA, MI_PORCENTAJEAUTORENTA, MI_APLICAAUTOICA, MI_PORCENTAJEAUTOICA 
                          FROM SF_CONCEPTOS 
                         WHERE CODIGO = RS_VAL_DETALLE.CONCEPTO
                           AND COMPANIA = UN_COMPANIA
                           AND ANO = UN_ANIO 
                           AND TIPOCOBRO    = UN_TIPOCOBRO;
                    END;
          

          /*INI_TICKET7723036 MPEREZ*/    
          --MOD JM CC 3896  (TOME EN CUENTA EL DESCUENTO PARA LA BASE)                
                    IF(MI_APLICAAUTORENTA NOT IN (0)) THEN                         
                        BEGIN
                            BEGIN
                                MI_PORCENTAJEAUTORENTA := MI_PORCENTAJEAUTORENTA/100;
                                MI_CAMPOS    := 'VALOR_AUTORENTA = ROUND((VALOR_BASE-NVL(VALOR_DESCUENTO,0))*'||MI_PORCENTAJEAUTORENTA||',2),
                                                 MODIFIED_BY   = '''||UN_USUARIO||''',
                                                 DATE_MODIFIED = SYSDATE';
    
                                MI_CONDICION := ' COMPANIA     = '''||UN_COMPANIA||'''
                                              AND ANO          = '||UN_ANIO||'  
                                              AND TIPOCOBRO    = '''||UN_TIPOCOBRO||'''
                                              AND CODIGO_COBRO = '||RS.CODIGO_COBRO||'
                                              AND CONCEPTO     = '''||RS_VAL_DETALLE.CONCEPTO||''' ';
    
                                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'SF_DETALLE_COBRO',
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
                    END IF;
                    --MOD JM CC 3896  (TOME EN CUENTA EL DESCUENTO PARA LA BASE) 
                    IF(MI_APLICAAUTOICA NOT IN (0)) THEN 
                        BEGIN
                            BEGIN
                                MI_PORCENTAJEAUTOICA := MI_PORCENTAJEAUTOICA/100;
                                MI_CAMPOS    := 'VALOR_AUTOICA   = ROUND((VALOR_BASE-NVL(VALOR_DESCUENTO,0))*'||MI_PORCENTAJEAUTOICA||',2),
                                                 MODIFIED_BY   = '''||UN_USUARIO||''',
                                                 DATE_MODIFIED = SYSDATE';
    
                                MI_CONDICION := ' COMPANIA     = '''||UN_COMPANIA||'''
                                              AND ANO          = '||UN_ANIO||'  
                                              AND TIPOCOBRO    = '''||UN_TIPOCOBRO||'''
                                              AND CODIGO_COBRO = '||RS.CODIGO_COBRO||'
                                              AND CONCEPTO     = '''||RS_VAL_DETALLE.CONCEPTO||''' ';
    
                                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'SF_DETALLE_COBRO',
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
                    END IF;  
                  END LOOP; -- MPEREZ 7735346                                              
                                                     
                    /*FIN_TICKET7723036 MPEREZ*/          
                    IF MI_FACTURA IS NULL THEN 
                        IF MI_VLR_BASE_GRAVABLE > 0 THEN 
                            BEGIN 
                                BEGIN 

--7740773 VALOR_RETEIVA
                     MI_TABLA   := 'SF_DETALLE_FACTURA';
                                    MI_CAMPOS  := 'COMPANIA, ANO, TIPO_FACTURA, FACTURA, TIPOCOBRO, CONCEPTO, TARIFA, ESTRATO, CANTIDAD, VALOR_BASE, VALOR_IVA, VALOR_RETEFUENTE, 
                                                   VALOR_DESCUENTO, VALOR_ICA, VALOR_NETO, FECHA_INICIO_COBRO, FECHA_FIN_COBRO, VALOR_COMPRA, VALOR_UTILIDAD, TERCERO, SUCURSAL, 
                                                   CENTRO_COSTO, AUXILIAR, VALOR_UNITARIO, ACOMETIDA_SERVICIO, AIU, ADMON_IMPREVISTOS, UTILIDAD_AIU, IVA_UTILIDAD, VALOR_CREE,
                                                   BASE_GRAVABLE, DESCUENTO,REFERENCIA,FUENTE_RECURSO,CUENTA_BANCO,VALOR_IMPOCONSUMO, CREATED_BY, DATE_CREATED, VALOR_AUTORENTA, VALOR_AUTOICA,VALOR_UNITARIO_DOLARES, VALOR_TOTAL_DOLARES, CENTRO_UTILIDAD, VALOR_RETEIVA, CONSECUTIVO,NROCONTRATOSIGEC,TIPOCONTRATOSIGEC'; /*TICKET7723036 MPEREZ*/

                                    MI_VALORES := 'SELECT COMPANIA,
                                                          ANO,
                                                          '''||UN_TIPOCOBRO||''',
                                                          '||MI_NRO_FACTURA||' ,
                                                          TIPOCOBRO,
                                                          CONCEPTO,
                                                          TARIFA,
                                                          ESTRATO,
                                                          CANTIDAD,
                                                          VALOR_BASE,
                                                          VALOR_IVA,
                                                          VALOR_RETEFUENTE,
                                                          VALOR_DESCUENTO,
                                                          VALOR_ICA,
                                                          VALOR_NETO,
                                                          FECHA_INICIO_COBRO,
                                                          FECHA_FIN_COBRO,
                                                          VALOR_COMPRA,
                                                          VALOR_UTILIDAD,
                                                          TERCERO,
                                                          SUCURSAL,
                                                          CENTRO_COSTO,
                                                          AUXILIAR,
                                                          VALOR_UNITARIO,
                                                          ACOMETIDA_SERVICIO,
                                                          AIU,
                                                          ADMON_IMPREVISTOS,
                                                          UTILIDAD_AIU,
                                                          IVA_UTILIDAD,
                                                          VALOR_CREE,
                                                          '||MI_VLR_BASE_GRAVABLE||' ,
                                                          DESCUENTO,
                                                          REFERENCIA,
                                                          FUENTE_RECURSO, 
                                                          CUENTA_BANCO,
                                                          VALOR_IMPOCONSUMO,
                                                          '''||UN_USUARIO||''',
                                                          SYSDATE,
                                                          VALOR_AUTORENTA,
                                                          VALOR_AUTOICA ,
                                                          VALOR_UNITARIO_DOLARES, 
                                                          VALOR_TOTAL_DOLARES,
                                                          CENTRO_UTILIDAD, 
                                                          VALOR_RETEIVA,
                                                          CONSECUTIVO,
                                                          NROCONTRATOSIGEC,
                                                          TIPOCONTRATOSIGEC
                                                        FROM SF_DETALLE_COBRO
                                                        WHERE COMPANIA   = '''||UN_COMPANIA||'''
                                                        AND TIPOCOBRO    = '''||UN_TIPOCOBRO||'''
                                                        AND CODIGO_COBRO = '||RS.CODIGO_COBRO||'';

                                    MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => MI_TABLA,
                                                               UN_ACCION   => 'IS',
                                                               UN_CAMPOS   => MI_CAMPOS,
                                                               UN_VALORES  => MI_VALORES);

                                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                                   RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                                 END;
                             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
                                 PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                            UN_TABLAERROR => MI_TABLA,
                                                            UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_REGISTRAR_FACTURA);  
                             END; 

                        ELSE
                            BEGIN 
                                BEGIN

--7740773 VALOR_RETEIVA
--7742952_SIGEC	
                                    MI_TABLA   := 'SF_DETALLE_FACTURA';
                                    MI_CAMPOS  := 'COMPANIA, ANO, TIPO_FACTURA, FACTURA, TIPOCOBRO, CONCEPTO, TARIFA, ESTRATO, CANTIDAD, VALOR_BASE, VALOR_IVA,
                                                   VALOR_RETEFUENTE, VALOR_DESCUENTO, VALOR_ICA, VALOR_NETO, FECHA_INICIO_COBRO, FECHA_FIN_COBRO, VALOR_COMPRA,
                                                   VALOR_UTILIDAD, TERCERO, SUCURSAL, CENTRO_COSTO, AUXILIAR, VALOR_UNITARIO, ACOMETIDA_SERVICIO, AIU,
                                                   ADMON_IMPREVISTOS, UTILIDAD_AIU, IVA_UTILIDAD, VALOR_CREE, BASE_GRAVABLE, DESCUENTO,REFERENCIA,FUENTE_RECURSO,CUENTA_BANCO,VALOR_IMPOCONSUMO,CREATED_BY,DATE_CREATED, VALOR_AUTORENTA, VALOR_AUTOICA,VALOR_UNITARIO_DOLARES, VALOR_TOTAL_DOLARES, CENTRO_UTILIDAD, VALOR_RETEIVA, CONSECUTIVO, NROCONTRATOSIGEC,TIPOCONTRATOSIGEC'; /*TICKET7723036 MPEREZ*/

                                    MI_VALORES := 'SELECT COMPANIA,
                                                          ANO,
                                                          '''||UN_TIPOCOBRO||''',
                                                          '||MI_NRO_FACTURA||' ,
                                                          TIPOCOBRO,
                                                          CONCEPTO,
                                                          TARIFA,
                                                          ESTRATO,
                                                          CANTIDAD,
                                                          VALOR_BASE,
                                                          VALOR_IVA,
                                                          VALOR_RETEFUENTE,
                                                          VALOR_DESCUENTO,
                                                          VALOR_ICA,
                                                          VALOR_NETO,
                                                          FECHA_INICIO_COBRO,
                                                          FECHA_FIN_COBRO,
                                                          VALOR_COMPRA,
                                                          VALOR_UTILIDAD,
                                                          TERCERO,
                                                          SUCURSAL,
                                                          CENTRO_COSTO,
                                                          AUXILIAR,
                                                          VALOR_UNITARIO,
                                                          ACOMETIDA_SERVICIO,
                                                          AIU,
                                                          ADMON_IMPREVISTOS,
                                                          UTILIDAD_AIU,
                                                          IVA_UTILIDAD,
                                                          VALOR_CREE,
                                                          BASE_GRAVABLE,
                                                          DESCUENTO,
                                                          REFERENCIA,
                                                          FUENTE_RECURSO,  
                                                          CUENTA_BANCO,
                                                          VALOR_IMPOCONSUMO,
                                                          CREATED_BY,
                                                          DATE_CREATED,
                                        				          VALOR_AUTORENTA,
                                                          VALOR_AUTOICA,
                                                          VALOR_UNITARIO_DOLARES, 
                                                          VALOR_TOTAL_DOLARES,
                                                          CENTRO_UTILIDAD, 
                                                          VALOR_RETEIVA,
                                                          CONSECUTIVO,
                                                          NROCONTRATOSIGEC,
                                                          TIPOCONTRATOSIGEC
                                                        FROM SF_DETALLE_COBRO
                                                        WHERE COMPANIA   = '''|| UN_COMPANIA||'''
                                                        AND TIPOCOBRO    = '''|| UN_TIPOCOBRO||'''
                                                        AND CODIGO_COBRO = '|| RS.CODIGO_COBRO ;

                                    MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => MI_TABLA,
                                                                       UN_ACCION   => 'IS',
                                                                       UN_CAMPOS   => MI_CAMPOS,
                                                                       UN_VALORES  => MI_VALORES);

                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                                   RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                                 END;
                             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
                                 PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                            UN_TABLAERROR => MI_TABLA,
                                                            UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_REGISTRAR_FACTURA);  
                             END;
                        END IF;
                    END IF;                    
               -- END LOOP; MPEREZ 7735346

                IF MI_RTA  = 0 THEN 
                    BEGIN 
                        BEGIN 
                            MI_CONDICION := '    COMPANIA = '''||UN_COMPANIA||'''
                                             AND TIPO_FACTURA = '''||UN_TIPOCOBRO||'''
                                             AND NUMERO_FACTURA = '||MI_NRO_FACTURA||'
                                             AND CODIGO_COBRO   = '||RS.CODIGO_COBRO;

                            MI_RTA:= PCK_DATOS.FC_ACME (UN_TABLA     => 'SF_FACTURA',
                                                        UN_ACCION    => 'E',
                                                        UN_CONDICION => MI_CONDICION);                
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
                            RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                        END;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                   UN_TABLAERROR => MI_TABLA,
                                                   UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ELIMINAR_FACTURA);  
                    END;

                    IF MI_RTA = 0 THEN 
                        IF UN_POR_LOTES NOT IN (0) THEN  
                            MI_MSGERROR(0).CLAVE := 'NRO_FACTURA';
                            MI_MSGERROR(1).VALOR := MI_NRO_FACTURA;                        

                            PCK_FACT_GENERAL_COM1.PR_REGISTRARPROCESO(UN_COMPANIA    => UN_COMPANIA,
                                                                      UN_CLASE       => 'FACT',
                                                                      UN_TIPOCOBRO   => UN_TIPOCOBRO,
                                                                      UN_COBRO       => RS.CODIGO_COBRO,
                                                                      UN_DESCRIPCION => PCK_ERR_MSG.FC_MENSAJE(UN_MENSAJE_COD => 69000159,
                                                                                                               UN_REEMPLAZOS  => MI_MSGERROR) ,
                                                                      UN_USUARIO     => UN_USUARIO);
                            CONTINUE;
                        ELSE
                            BEGIN
                                BEGIN
                                    MI_MSGERROR(0).CLAVE := 'NRO_FACTURA';
                                    MI_MSGERROR(1).VALOR := MI_NRO_FACTURA;
                                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                                 END;
                                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN

                                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_FACTURA_SIN_DET);
                            END;
                        END IF;
                    ELSE
                        IF UN_POR_LOTES NOT IN (0) THEN 
                           MI_MSGERROR(0).CLAVE := 'CODIGO_COBRO';
                           MI_MSGERROR(1).VALOR := RS.CODIGO_COBRO;

                           PCK_FACT_GENERAL_COM1.PR_REGISTRARPROCESO(UN_COMPANIA    => UN_COMPANIA,
                                                                  UN_CLASE       => 'FACT',
                                                                  UN_TIPOCOBRO   => UN_TIPOCOBRO,
                                                                  UN_COBRO       => RS.CODIGO_COBRO,
                                                                  UN_DESCRIPCION =>  PCK_ERR_MSG.FC_MENSAJE(UN_MENSAJE_COD => 69000160,
                                                                                                            UN_REEMPLAZOS  => MI_MSGERROR),
                                                                  UN_USUARIO     => UN_USUARIO); 
                           CONTINUE;
                        ELSE
                            BEGIN
                                BEGIN
                                    MI_MSGERROR(0).CLAVE := 'CODIGO_COBRO';
                                    MI_MSGERROR(1).VALOR := RS.CODIGO_COBRO;
                                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                                 END;
                                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN

                                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTUA_NRO_FACT);
                            END;

                        END IF;

                    END IF;


                ELSE 
                    IF UN_POR_LOTES NOT IN (0) THEN 
                        BEGIN
                            BEGIN
                                MI_TABLA     := 'SF_OBJETO_COBRO';
                                MI_CAMPOS    := 'TIPOFACTURA   = '''||UN_TIPOCOBRO||''',
                                                 NRO_FACTURA   = '||MI_NRO_FACTURA||',
                                                 MODIFIED_BY   = '''||UN_USUARIO||''',
                                                 DATE_MODIFIED = SYSDATE';

                                MI_CONDICION := ' COMPANIA     = '''||UN_COMPANIA||'''
                                              AND TIPOCOBRO    = '''||UN_TIPOCOBRO||'''
                                              AND CODIGO_COBRO = '||RS.CODIGO_COBRO;

                                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                       UN_ACCION    => 'M',
                                                       UN_CAMPOS    => MI_CAMPOS,
                                                       UN_CONDICION => MI_CONDICION);

                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                              RAISE PCK_EXCEPCIONES.EXC_FACTURACION;                          
                            END;
                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
                          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                  UN_TABLAERROR => MI_TABLA,
                                                  UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTUA_OBJETO_COB); 
                        END;
                    END IF;
                    IF UN_POR_LOTES NOT IN (0) THEN 
                        IF MI_RTA NOT IN (0) THEN 
                             MI_MSGERROR(0).CLAVE := 'CODIGO_COBRO';
                             MI_MSGERROR(1).VALOR := RS.CODIGO_COBRO;
                            PCK_FACT_GENERAL_COM1.PR_REGISTRARPROCESO(UN_COMPANIA    => UN_COMPANIA,
                                                                      UN_CLASE       => 'FACT',
                                                                      UN_TIPOCOBRO   => UN_TIPOCOBRO,
                                                                      UN_COBRO       => RS.CODIGO_COBRO,
                                                                      UN_DESCRIPCION => PCK_ERR_MSG.FC_MENSAJE(UN_MENSAJE_COD => 69000161,
                                                                                                               UN_REEMPLAZOS  => MI_MSGERROR) ,
                                                                      UN_USUARIO     => UN_USUARIO);     
                            CONTINUE;
                        ELSE 
                            BEGIN
                                BEGIN
                                    MI_MSGERROR(0).CLAVE := 'CODIGO_COBRO';
                                    MI_MSGERROR(1).VALOR := RS.CODIGO_COBRO;
                                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                                 END;
                                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN

                                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTUA_NRO_FACT);
                            END;

                        END IF;

                    END IF;

                END IF ;
            END IF;    

        BEGIN 
            BEGIN
                MI_TABLA := 'SF_OBJETO_COBRO';

             --09/08/2018 @eamaya

                MI_CAMPOS := 'NRO_FACTURA    = '||MI_NRO_FACTURA||',
                              TIPOFACTURA    = '''||UN_TIPOCOBRO||''',
                              IMPRESO        = -1';
              --09/08/2018 @eamaya

                MI_CONDICION := ' COMPANIA     = '''||UN_COMPANIA||'''
                              AND TIPOCOBRO    = '''||UN_TIPOCOBRO||'''
                              AND CODIGO_COBRO = '||RS.CODIGO_COBRO||'
                              AND ANO          = '||UN_ANIO;
                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
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

            BEGIN 
               BEGIN 

                  MI_CAMPOS := 'ANO_CPTE    = '||RS.ANOSOLICITUD||',
                                TIPO_CPTE   = '''||UN_TIPOCOBRO||''',
                                NRO_CPTE    = '||RS.CODIGO_COBRO||'';

                  MI_CONDICION := 'COMPANIA       = '''||UN_COMPANIA||'''
                               AND TIPO_FACTURA   = '''||UN_TIPOCOBRO||'''
                               AND NUMERO_FACTURA = '||MI_NRO_FACTURA||'
                               AND CODIGO_COBRO   = '||RS.CODIGO_COBRO;

                  MI_RTA:= PCK_DATOS.FC_ACME (UN_TABLA     => 'SF_FACTURA',
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


        IF MI_CAUSARFACTRECAUDO IN (0) AND MI_TIPOCOBRONOFACTURADO IN (0) THEN

               MI_RTA := PCK_FACT_GENERAL_COM3.FC_INTERFAZCONTABLE(UN_COMPANIA         => UN_COMPANIA,  
                                                                  UN_TIPO             => UN_TIPOCOBRO,
                                                                  UN_NUMERO           => RS.CODIGO_COBRO,
                                                                  UN_FECHA            => RS.FECHA_SOLICITUD,
                                                                  UN_TERCERO          => RS.TERCERO,
                                                                  UN_SUCURSAL         => RS.SUCURSAL,
                                                                  UN_DESCRIPCION      => 'Factura '||UN_TIPOCOBRO||' - '||MI_NRO_FACTURA||' - '||RS.OBSERVACIONES ||' - ',
                                                                  UN_MANEJAINVENTARIO => MI_MANEJA_INVENTARIO,
                                                                  UN_USUARIO          => UN_USUARIO);

         END  IF;

    END LOOP;
    RETURN -1;

END FC_FACTURAR;
--3
FUNCTION FC_VERFICARFACTURA
/*
      AUTHORS           : SYSMAN  SAS
      NAME              : FC_VERFICARFACTURA ,
      AUTHOR MIGRACION  : JUAN CAMILO RODRIGUEZ DIAZ
      DATE MIGRADOR     : 23/11/2017
      TIME              : 10:30 AM
      SOURCE MODULE     : SysmanSF2017.11.01
      DESCRIPTION       : 
      MODIFIER          : ELKIN GEOVANNY AMAYA SILVA
      DATE MODIFIED     : 06/11/2018
      TIME MODIFIED     : 
      MODIFICATIONS     : 

    @NAME:   verificarFactura
    @METHOD: GET
*/
(
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOFACTURA    IN SF_FACTURA.TIPO_FACTURA%TYPE,
  UN_NOFACTURA      IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_TASA           IN PCK_SUBTIPOS.TI_DOBLE,
  UN_CUOTAS         IN PCK_SUBTIPOS.TI_ENTERO,
  UN_EFECTIVO       IN PCK_SUBTIPOS.TI_DOBLE,
  UN_TIPOCOBRO      IN PCK_SUBTIPOS.TI_TIPO_COBRO,
  UN_ANO            IN PCK_SUBTIPOS.TI_ANIO,
  UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
)RETURN CLOB AS
  MI_MENSAJE      PCK_SUBTIPOS.TI_DESCRIPCION; 
  MI_TIPO         PCK_SUBTIPOS.TI_PARAMETRO;
  MI_RPTA         PCK_SUBTIPOS.TI_ENTERO;
  MI_NOABONO      PCK_SUBTIPOS.TI_DOBLE;
  MI_TOTAL_CUOTA  PCK_SUBTIPOS.TI_DOBLE;
  MI_CUOTAS       PCK_SUBTIPOS.TI_ENTERO;
  MI_VLRCUOTA     PCK_SUBTIPOS.TI_DOBLE;
  MI_FECHAVEN     DATE;
  MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
  MI_ACME         PCK_SUBTIPOS.TI_ENTERO;
  MI_ERROR        PCK_SUBTIPOS.TI_CLAVEVALOR; 
  MI_COD_COBRO    PCK_SUBTIPOS.TI_LONG;
  MI_FECHA_SOLICITUD   DATE;
  MI_OBSERVACIONES PCK_SUBTIPOS.TI_DESCRIPCION;
  MI_TOTALFACTURA  PCK_SUBTIPOS.TI_DOBLE;
  --VARIABLES DE LA CONSULTA DEL COMBO
      MI_NUMERO_FACTURA        PCK_SUBTIPOS.TI_LONG;
      MI_TIPO_FACTURA          SF_FACTURA.TIPO_FACTURA%TYPE;
      MI_TERCERO               PCK_SUBTIPOS.TI_TERCERO;
      MI_SUCURSAL              PCK_SUBTIPOS.TI_SUCURSAL;
      MI_CENTRO_COSTO          PCK_SUBTIPOS.TI_CENTRO_COSTO;
      MI_AUXILIAR              PCK_SUBTIPOS.TI_AUXILIAR;
      MI_VALOR_TOTAL           PCK_SUBTIPOS.TI_DOBLE;
      MI_DIFERIDA              PCK_SUBTIPOS.TI_LOGICO;
      MI_TIPO_ABONO            SF_FACTURA.TIPO_ABONO%TYPE;
      MI_NRO_ABONO             SF_FACTURA.NRO_ABONO%TYPE;
      MI_TASA_INTERES          SF_OBJETO_COBRO.TASA_INTERES%TYPE;
      MI_VLR_EFECTIVO          PCK_SUBTIPOS.TI_DOBLE;
      MI_VLR_CREDITO           PCK_SUBTIPOS.TI_DOBLE;
      MI_CUOTAS_DIFERIDAS      PCK_SUBTIPOS.TI_DOBLE;
      MI_VLR_PROM_CUOTA        PCK_SUBTIPOS.TI_DOBLE;
      MI_FECHA_EXPEDICION      DATE;
      MI_CLOB                  CLOB:='';
      MI_ESTADO                PCK_SUBTIPOS.TI_LOGICO:=0;
      MI_CONTAR                PCK_SUBTIPOS.TI_ENTERO;
    MI_RTA                   PCK_SUBTIPOS.TI_ENTERO;

BEGIN
  BEGIN
  --CONSULTA DEL COMBO TIPO FACTURA

    BEGIN
          BEGIN
              SELECT SF_FACTURA.NUMERO_FACTURA,
                SF_FACTURA.TIPO_FACTURA,
                SF_FACTURA.TERCERO,
                SF_FACTURA.SUCURSAL,
                SF_FACTURA.CENTRO_COSTO,
                SF_FACTURA.AUXILIAR,
                SF_FACTURA.VALOR_TOTAL,
                SF_FACTURA.DIFERIDA,
                SF_FACTURA.TIPO_ABONO,
                SF_FACTURA.NRO_ABONO,
                SF_OBJETO_COBRO.TASA_INTERES,
                SF_OBJETO_COBRO.VLR_EFECTIVO,
                SF_OBJETO_COBRO.VLR_CREDITO,
                SF_OBJETO_COBRO.CUOTAS_DIFERIDAS,
                SF_OBJETO_COBRO.VLR_PROM_CUOTA,
                SF_FACTURA.FECHA_EXPEDICION,
                SF_OBJETO_COBRO.CODIGO_COBRO,
                SF_OBJETO_COBRO.FECHA_SOLICITUD, 
                SF_FACTURA.OBSERVACIONES,
                SF_FACTURA.VALOR_TOTAL
                INTO
                MI_NUMERO_FACTURA,
                MI_TIPO_FACTURA,
                MI_TERCERO,
                MI_SUCURSAL,
                MI_CENTRO_COSTO,
                MI_AUXILIAR,
                MI_VALOR_TOTAL,
                MI_DIFERIDA,
                MI_TIPO_ABONO,
                MI_NRO_ABONO,
                MI_TASA_INTERES,
                MI_VLR_EFECTIVO,
                MI_VLR_CREDITO,
                MI_CUOTAS_DIFERIDAS,
                MI_VLR_PROM_CUOTA,
                MI_FECHA_EXPEDICION,
                MI_COD_COBRO,
                MI_FECHA_SOLICITUD,
                MI_OBSERVACIONES,
                MI_TOTALFACTURA
              FROM SF_FACTURA
              INNER JOIN SF_OBJETO_COBRO
              ON SF_FACTURA.COMPANIA      = SF_OBJETO_COBRO.COMPANIA
              AND SF_FACTURA.TIPOCOBRO    = SF_OBJETO_COBRO.TIPOCOBRO
              AND SF_FACTURA.ANO          = SF_OBJETO_COBRO.ANO 
              AND SF_FACTURA.CODIGO_COBRO = SF_OBJETO_COBRO.CODIGO_COBRO
              WHERE SF_FACTURA.COMPANIA     = UN_COMPANIA
              AND SF_FACTURA.TIPO_FACTURA   = UN_TIPOFACTURA
              AND SF_FACTURA.FECHA_PAGO    IS NULL
              AND SF_FACTURA.IND_ACUERDO    = 0
              AND SF_FACTURA.ANO            =UN_ANO
              AND SF_FACTURA.NUMERO_FACTURA =UN_NOFACTURA;      

              EXCEPTION
                 WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN 
                 BEGIN
                      RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
                      EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_SYSMANSF THEN    
                                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   =>SQLCODE,
                                                               UN_ERROR_COD =>PCK_ERRORES.ERR_SYSMANSF_NINGUN_ABONO);
                 END;

          END;   
          BEGIN
              SELECT 1
                INTO
                MI_CONTAR
                FROM SF_ABONOS
                WHERE COMPANIA = UN_COMPANIA
                AND TIPO       = UN_TIPOFACTURA            
                AND CODIGO     = MI_NRO_ABONO;
              EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
              MI_ESTADO:=1;
          END;

          IF MI_ESTADO = 0 THEN
              RETURN PCK_ERR_MSG.FC_MENSAJE(UN_MENSAJE_COD=>PCK_ERRORES.ERR_SYSMANSF_ABONO_RELACIONADO);
          ELSE

             MI_RPTA:=PCK_FACT_GENERAL.FC_BUSCARABONOSREGISTRAR(UN_COMPANIA     =>UN_COMPANIA,
                                                                UN_NROFACTURA   =>UN_NOFACTURA,
                                                                UN_TIPOFACTURA  =>UN_TIPOFACTURA,
                                                                UN_TIPOCOBRO    =>UN_TIPOCOBRO,
                                                                UN_ANOCOBRO     =>UN_ANO,
                                                                UN_USUARIO      =>UN_USUARIO);

            IF MI_RPTA = 0 THEN 
               BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                               UN_ERROR_COD =>PCK_ERRORES.ERR_SYSMANSF_VERIFICARFACTURA);
                END;                 
            ELSIF MI_RPTA = -1 THEN
              MI_CLOB:=PCK_FACT_GENERAL.FC_DIFERIRFACTURA(UN_COMPANIA    =>UN_COMPANIA,
                                                              UN_TIPOFAC     =>MI_TIPO_FACTURA,
                                                              UN_NROFACT     =>UN_NOFACTURA,
                                                              UN_TASA        =>UN_TASA,
                                                              UN_TERCERO     =>MI_TERCERO,
                                                              UN_SUCURSAL    =>MI_SUCURSAL,
                                                              UN_CENCOSTO    =>MI_CENTRO_COSTO,
                                                              UN_AUXILIAR    =>MI_AUXILIAR,
                                                              UN_NCUOTAS     =>UN_CUOTAS,
                                                              UN_CUOTAINICIAL=>UN_EFECTIVO,
                                                              UN_TIPOCOBRO   =>UN_TIPOCOBRO,
                                                              UN_USUARIO     =>UN_USUARIO);   
               MI_NOABONO:=TO_NUMBER(SUBSTR(MI_CLOB,0,INSTR(MI_CLOB, PCK_DATOS.GL_SEPARADOR_COL)-1));                                              

               PCK_FACT_GENERAL_COM2.PR_APROBARABONO(UN_COMPANIA => UN_COMPANIA,
                                                     UN_TIPOABONO=> UN_TIPOCOBRO,
                                                     UN_ABONO    => MI_NOABONO,
                                                     UN_TIPOFACT => MI_TIPO_FACTURA,
                                                     UN_NROFACT  => UN_NOFACTURA,
                                                     UN_ANO      => UN_ANO,
                                                     UN_USUARIO  => UN_USUARIO);

                BEGIN
                SELECT SUM(SF_DETALLE_ABONO.TOTAL_CUOTA) TOTAL_CUOTA,
                          SF_ABONOS.CUOTAS  CUOTAS
                          INTO
                          MI_TOTAL_CUOTA,
                          MI_CUOTAS                          
                        FROM SF_DETALLE_ABONO
                        INNER JOIN SF_ABONOS
                        ON SF_DETALLE_ABONO.COMPANIA      = SF_ABONOS.COMPANIA
                        AND SF_DETALLE_ABONO.TIPO_ABONO   = SF_ABONOS.TIPO
                        AND SF_DETALLE_ABONO.CODIGO_ABONO = SF_ABONOS.CODIGO
                        WHERE SF_ABONOS.COMPANIA           = UN_COMPANIA
                        AND SF_ABONOS.TIPO                 = UN_TIPOCOBRO
                        AND SF_ABONOS.CODIGO               = MI_NOABONO
                        AND SF_DETALLE_ABONO.CUOTA        NOT IN(0)
                        GROUP BY SF_ABONOS.CUOTAS;

                        MI_VLRCUOTA:=NVL(MI_TOTAL_CUOTA, 0) / MI_CUOTAS;

                EXCEPTION
                     WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
                     MI_VLRCUOTA:=0;                  
                END;   

                 BEGIN
                   BEGIN
                       MI_FECHAVEN :=ADD_MONTHS(MI_FECHA_EXPEDICION,UN_CUOTAS);

                       MI_CAMPOS:='FECHA_VENCIMIENTO='''||MI_FECHAVEN||''','||
                                  'DIFERIDA         = -1,'||
                                  'TIPO_ABONO       = '''||UN_TIPOCOBRO||''','||
                                  'NRO_ABONO        = '||MI_NOABONO||','||
                                  'MODIFIED_BY      ='''||UN_USUARIO||''','||
                                  'DATE_MODIFIED    =SYSDATE';

                       MI_CONDICION :='COMPANIA ='''||UN_COMPANIA||''''||
                                      ' AND  TIPO_FACTURA = '''|| MI_TIPO_FACTURA||''''||
                                      ' AND  NUMERO_FACTURA ='||UN_NOFACTURA;


                       MI_ACME:=PCK_DATOS.FC_ACME(UN_ACCION    =>'M',
                                                  UN_TABLA     =>'SF_FACTURA',
                                                  UN_CAMPOS    =>MI_CAMPOS,
                                                  UN_CONDICION =>MI_CONDICION);
                   EXCEPTION
                       WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                   END;

                   EXCEPTION
                          WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                            MI_ERROR(1).CLAVE := 'TABLA';
                            MI_ERROR(1).VALOR := 'SF_FACTURA';
                            MI_ERROR(2).CLAVE := 'CAMPOS';
                            MI_ERROR(2).VALOR :=  MI_CONDICION;
                           PCK_ERR_MSG.RAISE_WITH_MSG(
                             UN_EXC_COD    => SQLCODE,
                             UN_TABLAERROR => 'SF_FACTURA',
                             UN_ERROR_COD  => PCK_ERRORES.ERR_PR_CALCULARM_ACTUALIZAR,
                             UN_REEMPLAZOS => MI_ERROR
                            );          
               END; 


              MI_CLOB := PCK_FACT_GENERAL_COM4.FC_CARGARINTFINANCIERAFRA(UN_COMPANIA    => UN_COMPANIA,
                                                                UN_TIPOFRA   => MI_TIPO_FACTURA,
                                                                UN_NROFRA    => UN_NOFACTURA,
                                                                UN_TIPOFINAN => UN_TIPOCOBRO,
                                                                UN_NROFINAN  => MI_NOABONO,
                                                                UN_TABLAFIN  => 'SF_DETALLE_CUOTAABONO',
                                                                UN_USUARIO   => UN_USUARIO);

 ------------------------------------------------**********************************************************************---------------------------------------------------
IF  MI_DIFERIDA  = 0 THEN

                 BEGIN 
                           BEGIN 
                MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||''''||
                         'AND ANO = '''||UN_ANO||''''||
                         'AND TIPO_CPTE = '''||UN_TIPOCOBRO||''''||                                           
                         'AND COMPROBANTE   = '||MI_COD_COBRO;

                MI_ACME:= PCK_DATOS.FC_ACME (UN_TABLA     => 'DETALLE_COMPROBANTE_CNT',
                              UN_ACCION    => 'E',
                              UN_CONDICION => MI_CONDICION);                
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
               END;
                           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                       UN_TABLAERROR => 'DETALLE_COMPROBANTE_CNT',
                             UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_ELIMINARREG);
                   END;

       MI_RTA := PCK_FACT_GENERAL_COM3.FC_INTERFAZCONTABLE(UN_COMPANIA         => UN_COMPANIA,  
                                                                  UN_TIPO             => UN_TIPOCOBRO,
                                                                  UN_NUMERO           => MI_COD_COBRO,
                                                                  UN_FECHA            => MI_FECHA_SOLICITUD,
                                                                  UN_TERCERO          => MI_TERCERO,
                                                                  UN_SUCURSAL         => MI_SUCURSAL,
                                                                  UN_DESCRIPCION      => 'Factura '||UN_TIPOCOBRO||' - '||UN_NOFACTURA||' - '||MI_OBSERVACIONES ||' - ',
                                                                  UN_MANEJAINVENTARIO => 0,
                                                                  UN_USUARIO          => UN_USUARIO);  
                            BEGIN
                    SELECT SUM(VALOR_TOTAL) TOTAL
                        INTO
                                                MI_TOTALFACTURA                              
                        FROM SF_FACTURA              
                        WHERE COMPANIA           = UN_COMPANIA
                        AND   ANO                = UN_ANO
                        AND   TIPO_FACTURA       = UN_TIPOCOBRO
                        AND  NUMERO_FACTURA       = UN_NOFACTURA;

                    EXCEPTION
                       WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
                       MI_TOTALFACTURA:=0;                  
                             END;         
 END IF;
 ----------------                           


            END IF;

        END IF;
    END;      

  END;
  MI_CLOB:=SUBSTR(MI_CLOB,0,INSTR(MI_CLOB, PCK_DATOS.GL_SEPARADOR_COL)-1);
  RETURN TO_CHAR(MI_VLRCUOTA)||PCK_DATOS.GL_SEPARADOR_COL||MI_CLOB;
END FC_VERFICARFACTURA;
--4
PROCEDURE PR_APROBARABONO 
/*
      AUTHORS           : SYSMAN  SAS
      NAME              : PR_APROBARABONO ,
      AUTHOR MIGRACION  : JUAN CAMILO RODRIGUEZ DIAZ
      DATE MIGRADOR     : 23/11/2017
      TIME              : 10:30 AM
      SOURCE MODULE     : SysmanSF2017.11.01
      DESCRIPTION       : 
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 

    @NAME:   aprobarAbono
    @METHOD: POST
*/
(
UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
UN_TIPOABONO  IN PCK_SUBTIPOS.TI_PARAMETRO,
UN_ABONO      IN PCK_SUBTIPOS.TI_DOBLE,
UN_TIPOFACT   IN PCK_SUBTIPOS.TI_PARAMETRO,
UN_NROFACT    IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
UN_ANO        IN PCK_SUBTIPOS.TI_ANIO,
UN_USUARIO    IN PCK_SUBTIPOS.TI_USUARIO
)
AS 
MI_CAMPOS      PCK_SUBTIPOS.TI_CAMPOS;
MI_CRITERIO    PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES     PCK_SUBTIPOS.TI_VALORES;
MI_NROABONO    PCK_SUBTIPOS.TI_DOBLE;
MI_ACME        PCK_SUBTIPOS.TI_ENTERO;
MI_CONDICION   PCK_SUBTIPOS.TI_CONDICION;
MI_ERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_STRSQL      PCK_SUBTIPOS.TI_CONSULTA;
BEGIN
  BEGIN
        PCK_FACT_GENERAL_COM2.PR_VERIFICARDISTRIBUCIONABONO(UN_COMPANIA  =>UN_COMPANIA,
                                                            UN_TIPOABONO =>UN_TIPOABONO,
                                                            UN_ABONO     =>UN_ABONO);

        MI_NROABONO:= PCK_FACT_GENERAL_COM4.FC_ENUMERARABONO(UN_COMPANIA  => UN_COMPANIA,
                                                             UN_TIPOCOBRO => UN_TIPOABONO);
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
                             'FECHA_FIN,'||
                             'ESTADO,'||
                             'CUOTA_INICIAL,'||
                             'APLICACONDONACION,'||
                             'CONDONACION,'||
                             'OBSERVACION_CONDONACION,'||
                             'TASA_INTERES,'||
                             'TIPO_FACT,'||
                             'NRO_FACT,'||
                             'CREATED_BY,'||
                             'DATE_CREATED';


                  MI_VALORES:='SELECT COMPANIA,'||  
                              'TIPO,'||
                               MI_NROABONO||','||
                              'TERCERO,'||
                              'SUCURSAL,'||
                              'CENTRO_COSTO,'||
                              'AUXILIAR,'||
                              'DEUDA_CAPITAL,'||
                              'DEUDA_INTERES,'||
                              'DEUDA_TOTAL,'||
                              'CUOTAS,'||
                              'FECHA_INICIO,'||
                              'FECHA_FIN,'||
                              'ESTADO,'||
                              'CUOTA_INICIAL,'||
                              'APLICACONDONACION,'||
                              'NVL(CONDONACION,0)CONDONACION,'||
                              'OBSERVACION_CONDONACION,'||                       
                              'TASA_INTERES,'||
                              ''''||UN_TIPOFACT||''','||
                              UN_NROFACT||','||
                              ''''||UN_USUARIO||''','||
                              'SYSDATE'||
                              ' FROM TEMP_SF_ABONOS'||
                              ' WHERE COMPANIA ='''||UN_COMPANIA||''''||
                              ' AND TIPO       ='''||UN_TIPOABONO||''''||
                              ' AND CODIGO     ='||UN_ABONO;         

                 MI_ACME:=PCK_DATOS.FC_ACME(UN_ACCION  =>'IS',
                                            UN_TABLA   =>'SF_ABONOS',
                                            UN_CAMPOS  =>MI_CAMPOS,
                                            UN_VALORES =>MI_VALORES);
             EXCEPTION
                 WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
             END;

             EXCEPTION
                    WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                      MI_ERROR(1).CLAVE := 'TABLA';
                      MI_ERROR(1).VALOR := 'SF_ABONOS';
                      MI_ERROR(2).CLAVE := 'CAMPOS';
                      MI_ERROR(2).VALOR :=  MI_CAMPOS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(
                       UN_EXC_COD    => SQLCODE,
                       UN_TABLAERROR => 'SF_ABONOS',
                       UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_SFABONO_ACUERDO,
                       UN_REEMPLAZOS => MI_ERROR
                      );
      END; 

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

                MI_VALORES:='SELECT TEMP_SF_DETALLE_ABONO.COMPANIA,'||
                            'TEMP_SF_DETALLE_ABONO.TIPO_ABONO,'||
                             MI_NROABONO||','||
                            'TEMP_SF_DETALLE_ABONO.CUOTA,'||
                            'TEMP_SF_DETALLE_ABONO.FECHACUOTA,'||
                            'TEMP_SF_DETALLE_ABONO.CAPITAL,'||
                            'TEMP_SF_DETALLE_ABONO.INTERES,'||
                            'TEMP_SF_DETALLE_ABONO.INT_FINANCIACION,'||
                            'TEMP_SF_DETALLE_ABONO.INT_RECARGO,'||
                            'TEMP_SF_DETALLE_ABONO.TOTAL_CUOTA,'||
                            'TEMP_SF_DETALLE_ABONO.ESTADO,'||
                            ''''||UN_USUARIO||''','||
                            'SYSDATE'||
                            ' FROM TEMP_SF_DETALLE_ABONO'||
                            ' WHERE COMPANIA   = '''||UN_COMPANIA||''''||
                            'AND TIPO_ABONO   = '''||UN_TIPOABONO||''''||
                            'AND CODIGO_ABONO = '''||UN_ABONO||'''';


                 MI_ACME:=PCK_DATOS.FC_ACME(UN_ACCION  =>'IS',
                                            UN_TABLA   =>'SF_DETALLE_ABONO',
                                            UN_CAMPOS  =>MI_CAMPOS,
                                            UN_VALORES =>MI_VALORES);
             EXCEPTION
                 WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
             END;

             EXCEPTION
                    WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                      MI_ERROR(1).CLAVE := 'TABLA';
                      MI_ERROR(1).VALOR := 'SF_DETALLE_ABONO';
                      MI_ERROR(2).CLAVE := 'CAMPOS';
                      MI_ERROR(2).VALOR :=  MI_CAMPOS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(
                       UN_EXC_COD    => SQLCODE,
                       UN_TABLAERROR => 'SF_DETALLE_ABONO',
                       UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_DETALLEAB_ACUERDO,
                       UN_REEMPLAZOS => MI_ERROR
                      );
      END; 

        BEGIN
             BEGIN
                 MI_CAMPOS:='COMPANIA,'||
                           'TIPO_ABONO,'||
                           'CODIGO_ABONO,'||
                           'CUOTA,'||
                           'ANO,'||
                           'TIPOFACT,'||
                           'NROFACT,'||
                           'CONCEPTO,'||
                           'VALOR_COMPRA,'||
                           'VALOR_BASE,'||
                           'VALOR_IVA,'||
                           'VALOR_RETE,'||
                           'VALOR_DESCUENTO,'||
                           'VALOR_ICA,'||
                           'TOTAL_CUOTA,'||
                           'VALOR_UTILIDAD,'||
                           'TERCERO,'||
                           'SUCURSAL,'||
                           'CENTRO_COSTO,'||
                           'AUXILIAR,'||
                           'ACOMETIDA_SERVICIO,'||
                           'ADMON_IMPREVISTOS,'||
                           'UTILIDAD_AIU,'||
                           'IVA_UTILIDAD,'||
                           'CREATED_BY,'||
                           'DATE_CREATED';                       

                 MI_VALORES:= 'SELECT TEMP_SF_DETALLE_CUOTAABONO.COMPANIA,'||
                              'TEMP_SF_DETALLE_CUOTAABONO.TIPO_ABONO,'||
                               MI_NROABONO||','||
                              'TEMP_SF_DETALLE_CUOTAABONO.CUOTA,'||
                              'TEMP_SF_DETALLE_CUOTAABONO.ANO,'||
                              'TEMP_SF_DETALLE_CUOTAABONO.TIPOFACT,'||
                              'TEMP_SF_DETALLE_CUOTAABONO.NROFACT,'||
                              'TEMP_SF_DETALLE_CUOTAABONO.CONCEPTO,'||
                              'TEMP_SF_DETALLE_CUOTAABONO.VALOR_COMPRA,'||
                              'TEMP_SF_DETALLE_CUOTAABONO.VALOR_BASE,'||
                              'TEMP_SF_DETALLE_CUOTAABONO.VALOR_IVA,'||
                              'TEMP_SF_DETALLE_CUOTAABONO.VALOR_RETE,'||
                              'TEMP_SF_DETALLE_CUOTAABONO.VALOR_DESCUENTO,'||
                              'TEMP_SF_DETALLE_CUOTAABONO.VALOR_ICA,'||
                              'TEMP_SF_DETALLE_CUOTAABONO.TOTAL_CUOTA,'||
                              'TEMP_SF_DETALLE_CUOTAABONO.VALOR_UTILIDAD,'||
                              'TEMP_SF_DETALLE_CUOTAABONO.TERCERO,'||
                              'TEMP_SF_DETALLE_CUOTAABONO.SUCURSAL,'||
                              'TEMP_SF_DETALLE_CUOTAABONO.CENTRO_COSTO,'||
                              'TEMP_SF_DETALLE_CUOTAABONO.AUXILIAR,'||
                              'TEMP_SF_DETALLE_CUOTAABONO.ACOMETIDA_SERVICIO,'||
                              'TEMP_SF_DETALLE_CUOTAABONO.ADMON_IMPREVISTOS,'||
                              'TEMP_SF_DETALLE_CUOTAABONO.UTILIDAD_AIU,'||
                              'TEMP_SF_DETALLE_CUOTAABONO.IVA_UTILIDAD,'||
                              ''''||UN_USUARIO||''','||
                              'SYSDATE'||
                              ' FROM TEMP_SF_DETALLE_CUOTAABONO'||
                              ' WHERE COMPANIA   ='''||UN_COMPANIA||''''||
                              ' AND TIPO_ABONO   ='''||UN_TIPOABONO||''''||
                              ' AND CODIGO_ABONO ='||UN_ABONO ||
                              ' AND ANO = '||UN_ANO; 

                 MI_ACME:=PCK_DATOS.FC_ACME(UN_ACCION  =>'IS',
                                            UN_TABLA   =>'SF_DETALLE_CUOTAABONO',
                                            UN_CAMPOS  =>MI_CAMPOS,
                                            UN_VALORES =>MI_VALORES);
             EXCEPTION
                 WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
             END;

             EXCEPTION
                    WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                      MI_ERROR(1).CLAVE := 'TABLA';
                      MI_ERROR(1).VALOR := 'SF_DETALLE_CUOTAABONO';
                      MI_ERROR(2).CLAVE := 'CAMPOS';
                      MI_ERROR(2).VALOR :=  MI_CAMPOS;
                     PCK_ERR_MSG.RAISE_WITH_MSG(
                       UN_EXC_COD    => SQLCODE,
                       UN_TABLAERROR => 'SF_DETALLE_ABONO',
                       UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_DETALLEAB_ACUERDO,
                       UN_REEMPLAZOS => MI_ERROR
                      );                     

      END; 
      --Actualizar los datos del acuerdo de pago en las facturas que se financieron
       BEGIN
             BEGIN
                 MI_CAMPOS:='DIFERIDA     = -1,'||
                            'TIPO_ABONO   ='''||UN_TIPOABONO||''','||
                            'NRO_ABONO    ='|| MI_NROABONO||','||
                            'MODIFIED_BY  ='''||UN_USUARIO||''','||
                            'DATE_MODIFIED=SYSDATE';                      

                 MI_CONDICION:=' COMPANIA            ='''||UN_COMPANIA||''''||
                               ' AND  TIPO_FACTURA   ='''||UN_TIPOFACT||''''||
                               ' AND  NUMERO_FACTURA ='||UN_NROFACT;


                 MI_ACME:=PCK_DATOS.FC_ACME(UN_ACCION    =>'M',
                                            UN_TABLA     =>'SF_FACTURA',
                                            UN_CAMPOS    =>MI_CAMPOS,
                                            UN_CONDICION =>MI_CONDICION);
             EXCEPTION
                 WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
             END;

             EXCEPTION
                    WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                      MI_ERROR(1).CLAVE := 'TABLA';
                      MI_ERROR(1).VALOR := 'SF_FACTURA';
                      MI_ERROR(2).CLAVE := 'CAMPOS';
                      MI_ERROR(2).VALOR :=  MI_CONDICION;
                     PCK_ERR_MSG.RAISE_WITH_MSG(
                       UN_EXC_COD    => SQLCODE,
                       UN_TABLAERROR => 'SF_FACTURA',
                       UN_ERROR_COD  => PCK_ERRORES.ERR_PR_CALCULARM_ACTUALIZAR,
                       UN_REEMPLAZOS => MI_ERROR
                      );          
      END; 

  END;
END PR_APROBARABONO;

--5
PROCEDURE PR_VERIFICARDISTRIBUCIONABONO 
/*
      NAME              : PR_VERIFICARDISTRIBUCIONABONO METODO MIGRADO EN ACCESS VerificarDistribucionAbono,
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JUAN CAMILO RODRIGUEZ DIAZ
      DATE MIGRADOR     : 23/11/2017
      TIME              : 10:30 AM
      SOURCE MODULE     : SysmanSF2017.11.01
      DESCRIPTION       :'Esta funcion verifica que la sumatoria de las cuotas corresponda al total de la deuda
                         ' y en una segunda fase se verifica que la sumatoria de la distribución por concepto corresponda al valor de la cuota - 19 Enero 20111
                         ' Esta función debe ser llamada antes de aprobar definitivamente el acuerdo de pago debido a las tablas origen para el proceso

      MODIFIER          : ELKIN GEOVANNY AMAYA SILVA
      DATE MODIFIED     : 06/11/2018
      TIME MODIFIED     : 
      MODIFICATIONS     : 

    @NAME:   verificarDistribucionAbono
    @METHOD: POST
*/
(
  UN_COMPANIA  IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOABONO IN PCK_SUBTIPOS.TI_PARAMETRO,
  UN_ABONO     IN PCK_SUBTIPOS.TI_DOBLE
) AS 
  MI_DEUDA_CAPITAL PCK_SUBTIPOS.TI_DOBLE;
  MI_DEUDA_INTERES PCK_SUBTIPOS.TI_DOBLE;
  MI_DEUDA_TOTAL   PCK_SUBTIPOS.TI_DOBLE;
  MI_CONDONACION   PCK_SUBTIPOS.TI_DOBLE;
  MI_STRCUOTAS     PCK_SUBTIPOS.TI_PARAMETRO;
  MI_TCAPITAL      PCK_SUBTIPOS.TI_DOBLE;
  MI_TINTERES      PCK_SUBTIPOS.TI_DOBLE;
  MI_TDEUDA        PCK_SUBTIPOS.TI_DOBLE;
  MI_TINTFIN       PCK_SUBTIPOS.TI_DOBLE;
  MI_ERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;  
  MI_DIFERENCIA    PCK_SUBTIPOS.TI_DOBLE;
  MI_ENTRO         PCK_SUBTIPOS.TI_LOGICO:=0;
BEGIN
    BEGIN
        SELECT DEUDA_CAPITAL,
          DEUDA_INTERES,
          DEUDA_TOTAL,
          CONDONACION
          INTO 
          MI_DEUDA_CAPITAL,
          MI_DEUDA_INTERES,
          MI_DEUDA_TOTAL,
          MI_CONDONACION
        FROM TEMP_SF_ABONOS
        WHERE COMPANIA = UN_COMPANIA
        AND TIPO       = UN_TIPOABONO
        AND CODIGO     = UN_ABONO;

        EXCEPTION
                WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
                 BEGIN
                  RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
                    EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_SYSMANSF THEN    
                                  MI_ERROR(1).CLAVE := 'TIPOABONO';
                                  MI_ERROR(1).VALOR := UN_TIPOABONO;
                                  MI_ERROR(2).CLAVE := 'ABONO';
                                  MI_ERROR(2).VALOR := UN_ABONO;
                                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    =>  SQLCODE,
                                                             UN_ERROR_COD  =>  PCK_ERRORES.ERR_SYSMANSF_NO_VERIFICARDISTR,
                                                             UN_REEMPLAZOS =>  MI_ERROR);
                 END;
    END;

    BEGIN
        SELECT SUM(CAPITAL)                       ,
               SUM(INTERES)                       ,
               SUM(TOTAL_CUOTA - INT_FINANCIACION),
               SUM(INT_FINANCIACION)
               INTO 
               MI_TCAPITAL,
               MI_TINTERES,
               MI_TDEUDA,
               MI_TINTFIN
        FROM TEMP_SF_DETALLE_ABONO
        WHERE COMPANIA   = UN_COMPANIA
        AND TIPO_ABONO   = UN_TIPOABONO
        AND CODIGO_ABONO = UN_ABONO; 

        EXCEPTION
                WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
                 BEGIN
                  RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
                    EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_SYSMANSF THEN    
                                  MI_ERROR(1).CLAVE := 'TIPOABONO';
                                  MI_ERROR(1).VALOR := UN_TIPOABONO;
                                  MI_ERROR(2).CLAVE := 'ABONO';
                                  MI_ERROR(2).VALOR := UN_ABONO;
                                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    =>  SQLCODE,
                                                             UN_ERROR_COD  =>  PCK_ERRORES.ERR_SYSMANSF_NO_VERIFICARDETAL,
                                                             UN_REEMPLAZOS =>  MI_ERROR);
                 END;

     END;          

    BEGIN

      MI_DIFERENCIA := MI_DEUDA_CAPITAL - MI_TCAPITAL;
      IF ABS(MI_DIFERENCIA) > 0.01 THEN
       MI_ERROR(1).CLAVE := 'VALORABONO';
        MI_ERROR(1).VALOR := MI_DEUDA_CAPITAL;
        MI_ERROR(2).CLAVE := 'VALORDETALLE';
        MI_ERROR(2).VALOR := MI_TCAPITAL;
          BEGIN
              RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
                  EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_SYSMANSF THEN                                 
                                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    =>  SQLCODE,
                                                             UN_ERROR_COD  =>  PCK_ERRORES.ERR_SYSMANSF_NO_VERIFICARDIFER,
                                                             UN_REEMPLAZOS =>MI_ERROR);
          END;
      END IF;
       --RsAbono!DEUDA_INTERES <> RsDetAbono!TINTERES=de acuerdo a esta linea es visual basic retorna es un estado pero la variabl difrencia es un doble, se deja el operador menos
       MI_DIFERENCIA := MI_DEUDA_INTERES - MI_TINTERES;
      IF ABS(MI_DIFERENCIA) > 0.01 THEN

          BEGIN
              RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
                  EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_SYSMANSF THEN                                 
                                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    =>  SQLCODE,
                                                             UN_ERROR_COD  =>  PCK_ERRORES.ERR_SYSMANSF_IN_VERIFICARDISTR);
          END;
      END IF;


       IF  PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA =>UN_COMPANIA,
                            UN_NOMBRE   =>'SF FINANCIACION CUOTAS FIJAS CON AMORTIZACION',
                            UN_MODULO   =>PCK_DATOS.FC_MODULOFACTURACIONGENERAL(),
                            UN_FECHA_PAR=>SYSDATE,
                            UN_IND_MAYUS=>-1) = 'NO' THEN
             MI_DIFERENCIA := MI_DEUDA_TOTAL - MI_TDEUDA;
             IF ABS(MI_DIFERENCIA) > 0.01 THEN
                  BEGIN
                      RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
                          EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_SYSMANSF THEN                                 
                                          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    =>  SQLCODE,
                                                                     UN_ERROR_COD  =>  PCK_ERRORES.ERR_SYSMANSF_PARAM_FINANCIACIO);
                  END;
             END IF;
        END IF;

        <<DETALLEAABONO>>
        FOR MI_DETABONO IN (SELECT TEMP_SF_DETALLE_ABONO.TOTAL_CUOTA,
                              TEMP_SF_DETALLE_CUOTAABONO_AUX.TOTALCUOTA,
                              TEMP_SF_DETALLE_ABONO.CUOTA
                            FROM (
                            SELECT TEMP_SF_DETALLE_CUOTAABONO.COMPANIA,
                              TEMP_SF_DETALLE_CUOTAABONO.TIPO_ABONO,
                              TEMP_SF_DETALLE_CUOTAABONO.CODIGO_ABONO,
                              TEMP_SF_DETALLE_CUOTAABONO.CUOTA,
                              SUM(TEMP_SF_DETALLE_CUOTAABONO.TOTAL_CUOTA) AS TOTALCUOTA
                            FROM TEMP_SF_DETALLE_CUOTAABONO
                            GROUP BY TEMP_SF_DETALLE_CUOTAABONO.COMPANIA,
                              TEMP_SF_DETALLE_CUOTAABONO.TIPO_ABONO,
                              TEMP_SF_DETALLE_CUOTAABONO.CODIGO_ABONO,
                              TEMP_SF_DETALLE_CUOTAABONO.CUOTA
                            )TEMP_SF_DETALLE_CUOTAABONO_AUX
                            INNER JOIN TEMP_SF_DETALLE_ABONO
                            ON TEMP_SF_DETALLE_CUOTAABONO_AUX.COMPANIA      = TEMP_SF_DETALLE_ABONO.COMPANIA
                            AND TEMP_SF_DETALLE_CUOTAABONO_AUX.TIPO_ABONO   = TEMP_SF_DETALLE_ABONO.TIPO_ABONO
                            AND TEMP_SF_DETALLE_CUOTAABONO_AUX.CODIGO_ABONO = TEMP_SF_DETALLE_ABONO.CODIGO_ABONO
                            AND TEMP_SF_DETALLE_CUOTAABONO_AUX.CUOTA        = TEMP_SF_DETALLE_ABONO.CUOTA
                            WHERE TEMP_SF_DETALLE_ABONO.COMPANIA        = UN_COMPANIA
                            AND TEMP_SF_DETALLE_ABONO.TIPO_ABONO        = UN_TIPOABONO)

        LOOP
            MI_ENTRO:=-1;
            MI_DIFERENCIA:= NVL(MI_DETABONO.TOTAL_CUOTA, 0) - NVL(MI_DETABONO.TOTALCUOTA, 0);
            IF ABS(MI_DIFERENCIA) >= 0.01 THEN
                IF MI_STRCUOTAS IS NULL THEN
                  MI_STRCUOTAS:= 'La(s) cuota(s)'||' - '||TO_CHAR(MI_DETABONO.CUOTA);
                ELSE
                  MI_STRCUOTAS:= MI_STRCUOTAS ||' - '||TO_CHAR(MI_DETABONO.CUOTA);
                END IF;
            END IF;        

        END LOOP DETALLEAABONO;
         IF MI_STRCUOTAS IS NOT NULL THEN           
             BEGIN
               RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
                  EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_SYSMANSF THEN       
                                  MI_ERROR(1).CLAVE := 'STRCUOTAS';
                                  MI_ERROR(1).VALOR := MI_STRCUOTAS;
                                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    =>  SQLCODE,
                                                             UN_ERROR_COD  =>  PCK_ERRORES.ERR_SYSMANSF_DETALLECUOTA,
                                                             UN_REEMPLAZOS =>  MI_ERROR);
             END;
         END IF;
         IF MI_ENTRO = 0 THEN
            BEGIN
               RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
                  EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_SYSMANSF THEN      

                                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    =>  SQLCODE,
                                                             UN_ERROR_COD  =>  PCK_ERRORES.ERR_SYSMANSF_NODATA_DETALLECUO);
             END;
         END IF;
         MI_STRCUOTAS:= NULL;
         MI_ENTRO:=0;
         <<DETALLECUOTA>>
         FOR MI_DETABONO IN (SELECT TEMP_SF_DETALLE_CUOTAABONO_AUX.COMPANIA,
                              TEMP_SF_DETALLE_CUOTAABONO_AUX.TIPO_ABONO,
                              TEMP_SF_DETALLE_CUOTAABONO_AUX.CODIGO_ABONO,
                              TEMP_SF_DETALLE_CUOTAABONO_AUX.TIPOFACT,
                              TEMP_SF_DETALLE_CUOTAABONO_AUX.NROFACT,
                              TEMP_SF_DETALLE_CUOTAABONO_AUX.CONCEPTO,
                              TEMP_SF_DETALLE_CUOTAABONO_AUX.TOTALCONCEPTO,
                              SF_DETALLE_FACTURA.VALOR_NETO
                            FROM
                              (SELECT TEMP_SF_DETALLE_CUOTAABONO.COMPANIA,
                                TEMP_SF_DETALLE_CUOTAABONO.TIPO_ABONO,
                                TEMP_SF_DETALLE_CUOTAABONO.CODIGO_ABONO,
                                TEMP_SF_DETALLE_CUOTAABONO.TIPOFACT,
                                TEMP_SF_DETALLE_CUOTAABONO.NROFACT,
                                TEMP_SF_DETALLE_CUOTAABONO.CONCEPTO,
                                SUM(TEMP_SF_DETALLE_CUOTAABONO.TOTAL_CUOTA)  TOTALCONCEPTO
                              FROM TEMP_SF_DETALLE_CUOTAABONO
                              GROUP BY TEMP_SF_DETALLE_CUOTAABONO.COMPANIA,
                                TEMP_SF_DETALLE_CUOTAABONO.TIPO_ABONO,
                                TEMP_SF_DETALLE_CUOTAABONO.CODIGO_ABONO,
                                TEMP_SF_DETALLE_CUOTAABONO.TIPOFACT,
                                TEMP_SF_DETALLE_CUOTAABONO.NROFACT,
                                TEMP_SF_DETALLE_CUOTAABONO.CONCEPTO
                              ORDER BY TEMP_SF_DETALLE_CUOTAABONO.COMPANIA,
                                TEMP_SF_DETALLE_CUOTAABONO.TIPOFACT,
                                TEMP_SF_DETALLE_CUOTAABONO.NROFACT,
                                TEMP_SF_DETALLE_CUOTAABONO.CONCEPTO
                              ) TEMP_SF_DETALLE_CUOTAABONO_AUX
                            INNER JOIN SF_DETALLE_FACTURA
                            ON TEMP_SF_DETALLE_CUOTAABONO_AUX.COMPANIA      = SF_DETALLE_FACTURA.COMPANIA
                            AND TEMP_SF_DETALLE_CUOTAABONO_AUX.TIPOFACT     = SF_DETALLE_FACTURA.TIPO_FACTURA
                            AND TEMP_SF_DETALLE_CUOTAABONO_AUX.NROFACT      = SF_DETALLE_FACTURA.FACTURA
                            AND TEMP_SF_DETALLE_CUOTAABONO_AUX.CONCEPTO     = SF_DETALLE_FACTURA.CONCEPTO
                            WHERE TEMP_SF_DETALLE_CUOTAABONO_AUX.COMPANIA   = UN_COMPANIA
                            AND TEMP_SF_DETALLE_CUOTAABONO_AUX.TIPO_ABONO   = UN_TIPOABONO
                            AND TEMP_SF_DETALLE_CUOTAABONO_AUX.CODIGO_ABONO = UN_ABONO)
            LOOP
               MI_ENTRO:=-1;
           MI_DIFERENCIA:= ROUND(NVL(MI_DETABONO.TOTALCONCEPTO, 0)) - ROUND (NVL(MI_DETABONO.VALOR_NETO, 0));
              -- MI_DIFERENCIA:= NVL(MI_DETABONO.TOTALCONCEPTO, 0) - NVL(MI_DETABONO.VALOR_NETO, 0);
              IF ABS(MI_DIFERENCIA) >= 0.01 THEN
                  IF MI_STRCUOTAS IS NULL THEN
                     MI_ERROR(1).CLAVE := 'CONCEPTO';
                     MI_ERROR(1).VALOR := MI_DETABONO.CONCEPTO;
                     MI_ERROR(2).CLAVE := 'TIPOFACT';
                     MI_ERROR(2).VALOR := MI_DETABONO.TIPOFACT;
                     MI_ERROR(3).CLAVE := 'NROFACT';
                     MI_ERROR(3).VALOR := MI_DETABONO.NROFACT;

                     MI_STRCUOTAS:= PCK_ERR_MSG.FC_MENSAJE(UN_MENSAJE_COD=>PCK_ERRORES.ERR_SYSMANSF_CUOTABONO,
                                                           UN_REEMPLAZOS =>MI_ERROR);

                  ElSE
                     MI_ERROR(1).CLAVE := 'CONCEPTO';
                     MI_ERROR(1).VALOR := MI_DETABONO.CONCEPTO;
                     MI_ERROR(2).CLAVE := 'TIPOFACT';
                     MI_ERROR(2).VALOR := MI_DETABONO.TIPOFACT;
                     MI_ERROR(3).CLAVE := 'NROFACT';
                     MI_ERROR(3).VALOR := MI_DETABONO.NROFACT;
                     MI_ERROR(4).CLAVE := 'STRCUOTAS';
                     MI_ERROR(4).VALOR := MI_STRCUOTAS;
                     MI_STRCUOTAS:= PCK_ERR_MSG.FC_MENSAJE(UN_MENSAJE_COD=>PCK_ERRORES.ERR_SYSMANSF_CONCAT_CUOTABONO,
                                                           UN_REEMPLAZOS =>MI_ERROR);

                  END IF;
              END IF;

            END LOOP DETALLECUOTA;

            IF MI_STRCUOTAS IS NOT NULL THEN
                  MI_ERROR(1).CLAVE := 'STRCUOTAS';
                  MI_ERROR(1).VALOR := MI_STRCUOTAS;             
                 BEGIN
                  RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
                      EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_SYSMANSF THEN                
                                      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    =>  SQLCODE,
                                                                 UN_ERROR_COD  =>  PCK_ERRORES.ERR_SYSMANSF_NODATA_CUOTABONO,
                                                                 UN_REEMPLAZOS =>  MI_ERROR);
                 END;
            END IF;

             IF MI_ENTRO = 0  THEN                   
                 BEGIN
                   RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
                      EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_SYSMANSF THEN                
                                      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                                 UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_NODATA_DETCUOTABO,
                                                                 UN_REEMPLAZOS => MI_ERROR);
                 END;
            END IF;

    END;   

END PR_VERIFICARDISTRIBUCIONABONO;


--6

FUNCTION FC_CALCULAR_FECHA_VENCIMIENTO
/*
    NAME              : FC_CALCULAR_FECHA_VENCIMIENTO   ACCES => CalcularFechaVencimiento
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : JONATHAN ENRIQUE GUERRERO TORRES
    DATE MIGRADOR     : 07/12/2017
    TIME              : 11:04 AM
    DESCRIPTION       : FUNCION ENCARGADA DE CALCULAR LA BASE GRAVABLE EN EL PROCESO DE FACTURACION DE CONCEPTOS
    PARAMETROS        : UN_COMPANIA      => COMPANIA DE INGRESO A LA COMPANIA 
                        UN_TIPOCOBRO     => TIPO DE COBRO A FACTURAR
                        UN_CODIGO_COBRO  => CODIGO COBRO DEL PROCESO DE FACTURACION

    @NAME: calcularFechaVencimiento                    

  */

(
  UN_COMPANIA     IN  PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOCOBRO    IN  VARCHAR2,
  UN_APLICA_REL   IN  PCK_SUBTIPOS.TI_LOGICO,
  UN_CODIGO_COBRO IN VARCHAR2
  )

RETURN DATE AS 

MI_PARAMETRO                 PCK_SUBTIPOS.TI_PARAMETRO;
MI_FECHA_PRELIQUIDACION      DATE;
MI_VENCIMIENTO_FACTURACION   VARCHAR2(5 CHAR);
MI_DIAS_VALIDEZ              PCK_SUBTIPOS.TI_ENTERO;
MI_MSGERROR                  PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN
    MI_PARAMETRO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA,
                                          UN_NOMBRE     => 'SF APLICA FECHA PRELIQUIDACION',
                                          UN_MODULO     => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                          UN_FECHA_PAR  => SYSDATE);

    IF NVL(MI_PARAMETRO,'NO') = 'SI' THEN 

            SELECT NVL(FECHA_PRELIQUIDACION,SYSDATE)
              INTO MI_FECHA_PRELIQUIDACION
              FROM SF_OBJETO_COBRO
             WHERE COMPANIA     = UN_COMPANIA
               AND TIPOCOBRO    = UN_TIPOCOBRO
               AND CODIGO_COBRO = UN_CODIGO_COBRO;

    END IF;
    BEGIN  
        BEGIN 
            SELECT VENCIMIENTO_FACTURACION,
                   DIAS_VALIDEZ
              INTO MI_VENCIMIENTO_FACTURACION,
                   MI_DIAS_VALIDEZ
              FROM SF_TIPO_COBRO
             WHERE COMPANIA   = UN_COMPANIA
               AND CODIGO     = UN_TIPOCOBRO
               AND ANO        =  EXTRACT (YEAR FROM SYSDATE);
        EXCEPTION WHEN NO_DATA_FOUND THEN 
         RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
        END;
     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
          MI_MSGERROR(1).CLAVE := 'TIPOCOBRO';
          MI_MSGERROR(1).VALOR := UN_TIPOCOBRO;
          MI_MSGERROR(2).CLAVE := 'ANIO';
          MI_MSGERROR(2).VALOR := EXTRACT (YEAR FROM SYSDATE);
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_FECHA_VENCI,
                                     UN_REEMPLAZOS  => MI_MSGERROR);
      END;

      IF  MI_VENCIMIENTO_FACTURACION IS NULL THEN 
          BEGIN
              BEGIN
                 RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
               END;
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
              MI_MSGERROR(1).CLAVE := 'TIPOCOBRO';
              MI_MSGERROR(1).VALOR := UN_TIPOCOBRO;
              MI_MSGERROR(2).CLAVE := 'ANIO';
              MI_MSGERROR(2).VALOR := EXTRACT (YEAR FROM SYSDATE);              
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                         UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_FECHA_VENCI,
                                         UN_REEMPLAZOS  => MI_MSGERROR);
          END;

      END IF;

      IF MI_VENCIMIENTO_FACTURACION = 'RD' AND MI_DIAS_VALIDEZ  IS NULL THEN 
          BEGIN
              BEGIN
                 RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
               END;
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN

              MI_MSGERROR(1).CLAVE := 'TIPOCOBRO';
              MI_MSGERROR(1).VALOR := UN_TIPOCOBRO;
              MI_MSGERROR(2).CLAVE := 'ANIO';
              MI_MSGERROR(2).VALOR := EXTRACT (YEAR FROM SYSDATE);              
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                         UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_FECHA_VENCI,
                                         UN_REEMPLAZOS  => MI_MSGERROR);
          END;
      END IF;


      IF MI_VENCIMIENTO_FACTURACION = 'DP' THEN 
          IF NVL(MI_PARAMETRO,'NO') = 'NO' AND UN_APLICA_REL  IN (0) THEN
              MI_FECHA_PRELIQUIDACION := SYSDATE;          
          END IF;
      END IF;

      IF MI_VENCIMIENTO_FACTURACION = 'UDM' THEN 
          IF NVL(MI_PARAMETRO,'NO') = 'SI' AND UN_APLICA_REL NOT IN (0) THEN
              MI_FECHA_PRELIQUIDACION := LAST_DAY(MI_FECHA_PRELIQUIDACION);
          ELSE 
              MI_FECHA_PRELIQUIDACION := LAST_DAY(SYSDATE);
          END IF;
      END IF;

      IF MI_VENCIMIENTO_FACTURACION = 'RD' THEN 
          IF NVL(MI_PARAMETRO,'NO') = 'SI' AND UN_APLICA_REL NOT IN (0) THEN
              MI_FECHA_PRELIQUIDACION := MI_FECHA_PRELIQUIDACION + MI_DIAS_VALIDEZ;
          ELSE 
              MI_FECHA_PRELIQUIDACION := SYSDATE+ MI_DIAS_VALIDEZ;
          END IF;

      END IF;

      RETURN MI_FECHA_PRELIQUIDACION;
END FC_CALCULAR_FECHA_VENCIMIENTO;

--7
PROCEDURE PR_VALIDAR_CONC_INTERESES_FIN
/*
    NAME              : PR_VALIDAR_CONC_INTERESES_FIN   ACCES => validarConcInteresesFin
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : JONATHAN ENRIQUE GUERRERO TORRES
    DATE MIGRADOR     : 07/12/2017
    TIME              : 03:33 PM
    DESCRIPTION       : FUNCION ENCARGADA DE CALCULAR LA BASE GRAVABLE EN EL PROCESO DE FACTURACION DE CONCEPTOS
    PARAMETROS        : UN_COMPANIA      => COMPANIA DE INGRESO A LA COMPANIA 
                        UN_TIPOCOBRO     => TIPO DE COBRO A FACTURAR
                        UN_CODIGO_COBRO  => CODIGO COBRO DEL PROCESO DE FACTURACION

    @NAME: validarConcInteresesFin                    

  */
(
  UN_COMPANIA    IN  PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANO         IN PCK_SUBTIPOS.TI_ANIO
)
AS 
MI_CI_FINANCIACION        PCK_SUBTIPOS.TI_PARAMETRO;
MI_TIPODECONCEPTO         SF_CONCEPTOS.TIPODECONCEPTO%TYPE;
MI_CUENTADEBITOBASE       SF_CONCEPTOS.CUENTADEBITOBASE%TYPE;
MI_CUENTACREDITOBASE      SF_CONCEPTOS.CUENTACREDITOBASE%TYPE;
MI_CUENTADEBITOBASE_AV    SF_CONCEPTOS.CUENTADEBITOBASE_AV%TYPE;
MI_CUENTACREDITOBASE_AV   SF_CONCEPTOS.CUENTACREDITOBASE_AV%TYPE; 
MI_CENTRO_COSTO           SF_CONCEPTOS.CENTRO_COSTO%TYPE;
MI_AUXILIAR               SF_CONCEPTOS.AUXILIAR%TYPE;
MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN


  MI_CI_FINANCIACION := PCK_FACT_GENERAL_COM1.FC_VALIDACIONPARAMETRO(UN_COMPANIA  => UN_COMPANIA,
                                                                     UN_PARAMETRO => 'SF CONCEPTO DE INTERES - ACUERDO DE PAGO');
    BEGIN                                                           
        BEGIN 
              SELECT TIPODECONCEPTO,
                     CUENTADEBITOBASE,
                     CUENTACREDITOBASE ,
                     CUENTADEBITOBASE_AV,
                     CUENTACREDITOBASE_AV,
                     CENTRO_COSTO,
                     AUXILIAR
                INTO MI_TIPODECONCEPTO,
                     MI_CUENTADEBITOBASE,
                     MI_CUENTACREDITOBASE ,
                     MI_CUENTADEBITOBASE_AV,
                     MI_CUENTACREDITOBASE_AV,
                     MI_CENTRO_COSTO,
                     MI_AUXILIAR      
                FROM SF_CONCEPTOS
               WHERE COMPANIA = UN_COMPANIA
                 AND CODIGO     = MI_CI_FINANCIACION
                 AND ANO        = UN_ANO;                                                             

        EXCEPTION WHEN NO_DATA_FOUND THEN 
         RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
      END ;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
          MI_MSGERROR(1).CLAVE := 'CONCEPTO';
          MI_MSGERROR(1).VALOR := MI_CI_FINANCIACION;
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_CONC_INTERESES);
      END;       
      IF MI_TIPODECONCEPTO <> 'I' THEN 
          BEGIN
               BEGIN 
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
               END;
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
               MI_MSGERROR(1).CLAVE := 'CONCEPTO';
               MI_MSGERROR(1).VALOR := MI_CI_FINANCIACION;
               PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                          UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_TIPO_CONCEP);
          END;       
      END IF;

      IF NVL(MI_CUENTADEBITOBASE,' ') = ' ' OR NVL(MI_CUENTACREDITOBASE,' ') = ' ' THEN 
          BEGIN
               BEGIN 
                  RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
               END;
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
               MI_MSGERROR(1).CLAVE := 'CONCEPTO';
               MI_MSGERROR(1).VALOR := MI_CI_FINANCIACION;
               PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                          UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_CONFIG_CUENTAS);
          END;
      END IF;




END PR_VALIDAR_CONC_INTERESES_FIN;

--8
PROCEDURE PR_ACTUALIZAR_VALOR_TOTAL
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
    @NAME facturarConceptos 
    MODIFICACIONES:
    AUTHOR            : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MIGRADOR     : 19/01/2023
    TIME              : 09:35 AM
    DESCRIPTION       : SE MODIFICA PARA QUE LA FUNCION TAMBIEN CALCULE LA SUMATORIA DEL VALOR TOTAL EN DOLARES Y LO ACTUALICE EN EL HEADER.                

  */

(
  UN_COMPANIA        PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOCOBRO       VARCHAR2,
  UN_CODIGO_COBRO    PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_ANIO            PCK_SUBTIPOS.TI_ANIO
)
AS 
 MI_VALORNETO            PCK_SUBTIPOS.TI_DOBLE;
 MI_VLRTOTALDOLARES      PCK_SUBTIPOS.TI_DOBLE;
 MI_TABLA                PCK_SUBTIPOS.TI_TABLA;
 MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
 MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
 MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR; 
 MI_RTA                  PCK_SUBTIPOS.TI_PARAMETRO;

BEGIN
    SELECT NVL(SUM(VALOR_NETO),0) VALORNETO, NVL(SUM(VALOR_TOTAL_DOLARES),0) VALORTOTALDOLARES
      INTO MI_VALORNETO, MI_VLRTOTALDOLARES /*MPEREZ ticket7723037*/
      FROM SF_DETALLE_COBRO
     WHERE COMPANIA     = UN_COMPANIA
       AND CODIGO_COBRO = UN_CODIGO_COBRO
       AND ANO          = UN_ANIO
       AND TIPOCOBRO    = UN_TIPOCOBRO;

      BEGIN 
          BEGIN 
              MI_TABLA     := 'SF_OBJETO_COBRO';
              MI_CAMPOS    := 'VALOR_TOTAL    = '||MI_VALORNETO||',
                               VALOR_TOTAL_DOLARES = '||MI_VLRTOTALDOLARES||' '; /*MPEREZ ticket7723037*/
              MI_CONDICION := '   COMPANIA     = '''||UN_COMPANIA||'''
                              AND CODIGO_COBRO = '||UN_CODIGO_COBRO||'
                              AND ANO          = '||UN_ANIO||'
                              AND TIPOCOBRO    = '''||UN_TIPOCOBRO||'''';

              PCK_DATOS.GL_RTA :=PCK_DATOS.FC_ACME(UN_ACCION    =>'M',
                                                   UN_TABLA     =>MI_TABLA,
                                                   UN_CAMPOS    =>MI_CAMPOS,
                                                   UN_CONDICION =>MI_CONDICION);                

         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
              MI_MSGERROR(1).CLAVE := 'CODIGO_COBRO';
              MI_MSGERROR(1).VALOR := UN_CODIGO_COBRO;  
             RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_TABLAERROR => MI_TABLA, 
                                   UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTUA_VAL_TOT,
                                    UN_REEMPLAZOS => MI_MSGERROR);
    END;          



END PR_ACTUALIZAR_VALOR_TOTAL;


--

PROCEDURE PR_FACTURAR_CONCEPTOS
 /*
    NAME              : FC_FACTURAR   ACCES => facturar
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : JONATHAN ENRIQUE GUERRERO TORRES
    DATE MIGRADOR     : 12/10/2017
    TIME              : 09:44 AM
    DESCRIPTION       : FUNCION ENCARGADA DE REALIZAR LA FACTURACION DE CONCEPTOS
    PARAMETROS        : UN_COMPANIA       => COMPANIA DE INGRESO A LA COMPANIA 
                        UN_TIPOCOBRO      => TIPO DE COBRO A FACTURAR
                        UN_NOFACTURA      => NUMERO DE LA FACTURA SI EXISTE
                        UN_ANIO           => AÑO DE LA FACTURACION
                        UN_USUARIO        => USUARIO QUE EJECUTA EL PROCESO.
    @NAME facturarConceptos

  */
( 
  UN_COMPANIA      IN  PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOCOBRO     IN  VARCHAR2,
  UN_CODIGO_COBRO  IN  PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_NRO_FACTURA   IN  PCK_SUBTIPOS.TI_ENTERO_LARGO,  
  UN_ANIO          IN  PCK_SUBTIPOS.TI_ANIO,  
  UN_USUARIO       IN  PCK_SUBTIPOS.TI_USUARIO
 )
AS 
  MI_RTA_FACTURAR      PCK_SUBTIPOS.TI_LOGICO;  
  MI_TABLA             PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS            PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES           PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION         PCK_SUBTIPOS.TI_CONDICION;
  MI_RTA               PCK_SUBTIPOS.TI_ENTERO;
  MI_MANEJA_INVENTARIO PCK_SUBTIPOS.TI_LOGICO;
  MI_PARAMETRO         PCK_SUBTIPOS.TI_PARAMETRO;

BEGIN   

  MI_RTA_FACTURAR := PCK_FACT_GENERAL_COM2.FC_FACTURAR(UN_COMPANIA    => UN_COMPANIA,
                                                       UN_TIPOCOBRO   => UN_TIPOCOBRO,
                                                       UN_FACINICIAL  => UN_CODIGO_COBRO,
                                                       UN_FACFINAL    => UN_CODIGO_COBRO,
                                                       UN_POR_LOTES   => 0,
                                                       UN_NOFACTURA   => UN_NRO_FACTURA,
                                                       UN_ANIO        => UN_ANIO,           
                                                       UN_USUARIO     => UN_USUARIO);

   FOR MI_RS IN ( SELECT SF_DETALLE_COBRO.COMPANIA,
                          SF_DETALLE_COBRO.TIPOCOBRO,
                          SF_DETALLE_COBRO.CODIGO_COBRO,
                          SF_DETALLE_COBRO.VALOR_BASE,
                          SF_DETALLE_COBRO.VALOR_NETO,
                          SF_DETALLE_COBRO.VALOR_UTILIDAD,
                          SF_DETALLE_COBRO.VALOR_UNITARIO,
                          SF_DETALLE_COBRO.CONCEPTO ,
                          SF_DETALLE_COBRO.CANTIDAD,
                          SF_DETALLE_COBRO.ESTRATO,
                          SF_DETALLE_COBRO.TARIFA,
                          SF_DETALLE_COBRO.TERCERO,
                          SF_DETALLE_COBRO.AUXILIAR,
                          SF_DETALLE_COBRO.CENTRO_COSTO,
                          SF_OBJETO_COBRO.NRO_FACTURA
                     FROM SF_DETALLE_COBRO
                    INNER JOIN SF_OBJETO_COBRO
                       ON SF_DETALLE_COBRO.COMPANIA      = SF_OBJETO_COBRO.COMPANIA
                      AND SF_DETALLE_COBRO.ANO          = SF_OBJETO_COBRO.ANO
                      AND SF_DETALLE_COBRO.TIPOCOBRO    = SF_OBJETO_COBRO.TIPOCOBRO
                      AND SF_DETALLE_COBRO.CODIGO_COBRO = SF_OBJETO_COBRO.CODIGO_COBRO
                    WHERE SF_DETALLE_COBRO.COMPANIA     = UN_COMPANIA
                      AND SF_DETALLE_COBRO.TIPOCOBRO    = UN_TIPOCOBRO
                      AND SF_DETALLE_COBRO.CODIGO_COBRO = UN_CODIGO_COBRO)
    LOOP

       --Se comenta este bloque porque esta haciendo un proceso redundante
   --ya que los mismo se hace en la función anterior PCK_FACT_GENERAL_COM2.FC_FACTURAR

 /*
        BEGIN   
            BEGIN
                MI_TABLA   := 'SF_DETALLE_FACTURA';

                MI_CAMPOS  := ' VALOR_BASE     ='||MI_RS.VALOR_BASE||',
                                VALOR_NETO     = '||MI_RS.VALOR_NETO||',
                                VALOR_UTILIDAD = '||MI_RS.VALOR_UTILIDAD||',
                                VALOR_UNITARIO = '||MI_RS.VALOR_UNITARIO||',
                                CANTIDAD       = '||MI_RS.CANTIDAD||',
                                ESTRATO         = '''||MI_RS.ESTRATO||''',
                                TARIFA         = '''||MI_RS.TARIFA||''',
                                TERCERO        = '''||MI_RS.TERCERO||''',
                                CENTRO_COSTO   = '''||MI_RS.CENTRO_COSTO||''',
                                AUXILIAR       = '''||MI_RS.AUXILIAR||''',
                                MODIFIED_BY    = '''||UN_USUARIO||''',
                                DATE_MODIFIED  = SYSDATE';

                MI_CONDICION := ' COMPANIA = '''||UN_COMPANIA||'''
                              AND TIPOCOBRO = '''||UN_TIPOCOBRO||'''
                              AND FACTURA  = '||MI_RS.NRO_FACTURA||'
                              AND CONCEPTO = '''||MI_RS.CONCEPTO||'''';

                 MI_RTA      := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOS,
                                                  UN_CONDICION => MI_CONDICION);

                                                  
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION;                          
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_TABLAERROR => MI_TABLA,
                                   UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTA_DET_FAC); 
        END;

        BEGIN
            IF (NVL(MI_RS.NRO_FACTURA,'') = '' ) THEN 
                RAISE PCK_EXCEPCIONES.EXC_FACTURACION;                          
            END IF;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_TABLAERROR => MI_TABLA,
                                   UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTA_FAC_DET); 
        END;*/


    MI_PARAMETRO :=  PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                           UN_NOMBRE    => 'SF AFECTAR ALMANCENA EN LA FACTURACION',
                                           UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL(),
                                           UN_FECHA_PAR => SYSDATE,
                                           UN_IND_MAYUS => -1);

    SELECT MANEJA_INVENTARIO
      INTO MI_MANEJA_INVENTARIO
      FROM SF_TIPO_COBRO
     WHERE COMPANIA   = UN_COMPANIA
       AND ANO        = UN_ANIO
       AND CODIGO     = UN_TIPOCOBRO;    


    IF MI_MANEJA_INVENTARIO NOT IN (0) AND NVL(MI_PARAMETRO,'NO') = 'SI'  THEN 
        BEGIN 
            BEGIN
                MI_TABLA     := 'SF_FACTURA';
                MI_CAMPOS    := 'AFECTO_INVENTARIO = -1,
                                 MODIFIED_BY   ='''||UN_USUARIO||''',
                                 DATE_MODIFIED = SYSDATE';
                MI_CONDICION := 'COMPANIA       = '''||UN_COMPANIA||'''
                             AND TIPO_FACTURA   = '''||UN_TIPOCOBRO||'''
                             AND NUMERO_FACTURA ='||MI_RS.NRO_FACTURA;
                MI_RTA      := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                 UN_ACCION    => 'M',
                                                 UN_CAMPOS    => MI_CAMPOS,
                                                 UN_CONDICION => MI_CONDICION);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
              RAISE PCK_EXCEPCIONES.EXC_FACTURACION;                          
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_TABLAERROR => MI_TABLA,
                                   UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTA_AFECT_INVET); 
        END;


    END IF;

   END LOOP;                                        




END PR_FACTURAR_CONCEPTOS;

--10

FUNCTION FC_CARGARARCHIVOASOBANCARIA 
 /*
    NAME              : CARGARARCHIVOASOBANCARIA   ACCES => registrar_pagos
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : ANA YESSICA SANA ROJAS
    DATE MIGRADOR     : 12/12/2017
    TIME              : 12:00 PM
    DESCRIPCION       : FUNCION QUE PERMITE SUBIR EL PLANO DE ASOBANCARIA
    PARAMETROS        : UN_COMPANIA   COMPANIA DE INGRESO A LA COMPANIA
                        UN_BANCO      BANCO DE PAGO 
                        UN_USUARIO    USUARIO QUE EJECUTA EL PROCESO.
                        UN_CADENA     CADENA CONCATENADA DESDE EL CONTROLADOR 
  @NAME cargarArchivoAsobancaria
  */
(
  UN_COMPANIA   IN    PCK_SUBTIPOS.TI_COMPANIA,
  UN_BANCO      IN    VARCHAR2,
  UN_USUARIO    IN    PCK_SUBTIPOS.TI_USUARIO,
  UN_CADENA     IN    CLOB
) RETURN VARCHAR2 AS 
  MI_NUMEROCUENTA     VARCHAR2(20);
  MI_NREGISTROS       PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_ARCHIVO          CLOB;
  MI_CADENA           CLOB;
  MI_CODIGOASOBA      VARCHAR2(20);
  MI_NUMEROCTA        VARCHAR2(20);
  MI_BANCO            CUENTABANCOS.BANCO%TYPE;
  MI_NOMBREBANCO      BANCO.NOMBREBANCO%TYPE;
  MI_CUENTANUMUMERO   CUENTABANCOS.CUENTANUMERO%TYPE;
  MI_BANCOASOBANCARIA BANCO.BANCOASOBANCARIA%TYPE;
  MI_TAMANO           PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_PARAMETESORERIA  PARAMETRO.VALOR%TYPE;
  MI_PARAMCODEAN      PARAMETRO.VALOR%TYPE;
  MI_PARAMECODTIPO    PARAMETRO.VALOR%TYPE;
  MI_LINEAS           PCK_SUBTIPOS.TI_ENTERO;
  MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
  MI_RTA              PCK_SUBTIPOS.TI_ENTERO;   
  MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;  
  MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;  
  MI_VALORES          PCK_SUBTIPOS.TI_VALORES;  
  MI_PCONDICION       PCK_SUBTIPOS.TI_CONDICION;  
  MI_PCAMPOS          PCK_SUBTIPOS.TI_CAMPOS;  
  MI_PTABLA           PCK_SUBTIPOS.TI_TABLA;
  MI_PVALORES         PCK_SUBTIPOS.TI_VALORES; 
  MI_TIPOREGISTRO     VARCHAR2(5);
  MI_CONSECUTIVO      PCK_SUBTIPOS.TI_ENTERO; 
  MI_HORA             VARCHAR2(20);
  MI_REEMPLAZOS       PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_EANPORTIPO       PARAMETRO.VALOR%TYPE;
  MI_FECHARECAUDO     SF_ASOBANCARIA_ENC_ARC.FECHA_RECAUDO%TYPE;
  MI_CODIGO           SF_ASOBANCARIA_DETALLE.CODIGO%TYPE;
  MI_VALOR            SF_ASOBANCARIA_DETALLE.VALOR%TYPE;
  MI_ARTOTALREGISTROS SF_ASOBANCARIA_CON_ARC.TOTAL_REGISTROS%TYPE;
  MI_ARVALORTOTAL     SF_ASOBANCARIA_CON_ARC.VALOR_TOTAL%TYPE;
  MI_COTOTALREGISTROS SF_ASOBANCARIA_CON_LOT.TOTAL_REGISTROS%TYPE;
  MI_COVALORTOTAL     SF_ASOBANCARIA_CON_LOT.VALOR_TOTAL%TYPE;
  MI_CONUMEROLOTE     SF_ASOBANCARIA_CON_LOT.NUMERO_LOTE%TYPE;
  MI_EAN              SF_ASOBANCARIA_ENC_LOT.EAN%TYPE;
  MI_ENNUMEROLOTE     SF_ASOBANCARIA_ENC_LOT.NUMERO_LOTE%TYPE;
  MI_EXISTE           PCK_SUBTIPOS.TI_ENTERO_LARGO; 
  MI_EANPARAMETRO     PARAMETRO.VALOR%TYPE;
  MI_ESTADOPERIODODIA DIA_BLOQUEO.ESTADO%TYPE;
  MI_TIPORECAUDO      NUMBER(5);
  MI_TIPOFACTURA      SF_TIPO_COBRO.CODIGO%TYPE;
  MI_PARAMACTAREFERENCIA PARAMETRO.VALOR%TYPE;
  MI_PARCONCREFERENCIA PARAMETRO.VALOR%TYPE;
  MI_CALTOTCADENA     PCK_SUBTIPOS.TI_ENTERO; 
  MI_CODTOTAL         SF_ASOBANCARIA_DETALLE.CODIGO%TYPE;
  MI_NITUSUARIO       VARCHAR(50);
  MI_RECIBOPAGO       VARCHAR(50);
  MI_REFERENCIAS      VARCHAR(50);
  MI_TOTALRECIBO      PCK_SUBTIPOS.TI_DOBLE;
  MI_FACTIPOCPTEPAGO  SF_FACTURA.TIPOCPTE_PAGO%TYPE;
  MI_FACNROCPTEPAGO   SF_FACTURA.NROCPTE_PAGO%TYPE;
  MI_FACFECHVENCIM    SF_FACTURA.FECHA_VENCIMIENTO%TYPE;
  MI_FACINTERFAZADA   SF_FACTURA.INTERFAZADA%TYPE;
  MI_FACTIPOFACTURA   SF_FACTURA.TIPO_FACTURA%TYPE;
  MI_FACNUMEROFACTURA SF_FACTURA.NUMERO_FACTURA%TYPE;
  MI_FACTERCERO       SF_FACTURA.TERCERO%TYPE;
  MI_FACDIFERIDA      SF_FACTURA.DIFERIDA%TYPE;
  MI_FACOBSERVACIONES SF_FACTURA.OBSERVACIONES%TYPE;
  MI_FACANOCPTE       SF_FACTURA.ANO_CPTE%TYPE;
  MI_FACTIPOCPTE      SF_FACTURA.TIPO_CPTE%TYPE;
  MI_FACNROCPTE       SF_FACTURA.NRO_CPTE%TYPE;
  MI_FACTIPOABONO     SF_FACTURA.TIPO_ABONO%TYPE;
  MI_FACNROABONO      SF_FACTURA.NRO_ABONO%TYPE;
  MI_FACCENTROCOSTO   SF_FACTURA.CENTRO_COSTO%TYPE;
  MI_FACAUXILIAR      SF_FACTURA.AUXILIAR%TYPE;
  MI_FACFECHAPAGO     SF_FACTURA.FECHA_PAGO%TYPE;
  MI_FACTCONDICIONT   VARCHAR2(25);
  MI_FACTCONDICIONN   VARCHAR2(25);
  MI_FACTVALORT       VARCHAR2(25);
  MI_FACTVALORN       VARCHAR2(25);
  MI_APLICACAUSACION  PCK_SUBTIPOS.TI_LOGICO;
  MI_MENSAJEERROR     SF_ASOBANCARIA_ERROR.DESCRIPCION%TYPE;
  MI_IDASOBANCARIA    PARAMETRO.VALOR%TYPE;
  MI_CONSECUTIVOERROR SF_ASOBANCARIA_ERROR.CONSECUTIVO%TYPE;
  MI_INTERFAZRECAUDO  SF_TIPO_COBRO.INTERFAZ_RECAUDO%TYPE;
  MI_MANEJAINTSPUBLIC PARAMETRO.VALOR%TYPE;
  MI_DESCRIPCION      SF_ASOBANCARIA_ERROR.DESCRIPCION%TYPE;
  MI_OBJTIPOCOBRO     SF_OBJETO_COBRO.TIPOCOBRO%TYPE;
  MI_OBJCODIGOCOBRO   SF_OBJETO_COBRO.CODIGO_COBRO%TYPE;
  MI_OBJOBSERVACIONES SF_OBJETO_COBRO.OBSERVACIONES%TYPE;
  MI_STROBSERVACION   VARCHAR(300);
  MI_NUMEROPAGOS      PCK_SUBTIPOS.TI_ENTERO;
  MI_CPTERECAUDO      SF_FACTURA.TIPOCPTE_PAGO%TYPE;
  MI_NROCPTEPAGO      SF_FACTURA.NRO_CPTE%TYPE;
  MI_CFECHARECAUDO    DATE;
  MI_SIMPLEINTERFAZ   PARAMETRO.VALOR%TYPE;
  MI_PARAMETRO        PARAMETRO.VALOR%TYPE;
  MI_STRSQL           VARCHAR2(1000 CHAR):='';
  MI_CODIGOBANCO      SF_ASOBANCARIA_LOG.BANCO%TYPE;
  MI_RTA_RECAUDO      VARCHAR2(1000 CHAR);
  MI_RTA_RECAUDOCPTE  SF_FACTURA.NROCPTE_PAGO%TYPE;
  MI_RTA_RECAUDOTIPOCPTE  SF_FACTURA.TIPOCPTE_PAGO%TYPE;
  MI_TAMANO2          VARCHAR2(1000 CHAR);
  MI_RETORNO          VARCHAR2(2);
  MI_INDPAGO          NUMBER;/*0=FUNCIONO -1=NO FUNCIONO*/
  MI_SIN_EANRECAUDO   PARAMETRO.VALOR%TYPE;
  MI_EAN_ERRADO       NUMBER;


BEGIN
        MI_RETORNO := 0;
        MI_CONSECUTIVOERROR := 0;
        MI_PARAMETESORERIA    := PCK_FACT_GENERAL_COM1.FC_VALIDACIONPARAMETRO
                                             (UN_COMPANIA  => UN_COMPANIA
                                             ,UN_PARAMETRO => 'SF MANEJA CODIGO ASOBANCARIA EN TESORERIA');
        MI_EANPORTIPO         := PCK_FACT_GENERAL_COM1.FC_VALIDACIONPARAMETRO
                                             (UN_COMPANIA  => UN_COMPANIA
                                             ,UN_PARAMETRO => 'SF CODIGO EAN POR CADA TIPO DE COBRO');
        MI_EANPARAMETRO       := PCK_FACT_GENERAL_COM1.FC_VALIDACIONPARAMETRO
                                             (UN_COMPANIA  => UN_COMPANIA
                                             ,UN_PARAMETRO => 'SF CODIGO EAN');
        MI_PARAMACTAREFERENCIA:= PCK_FACT_GENERAL_COM1.FC_VALIDACIONPARAMETRO
                                             (UN_COMPANIA  => UN_COMPANIA
                                             ,UN_PARAMETRO    => 'SF MANEJAR REFERENCIA PLANO RECAUDO'); 
        MI_PARCONCREFERENCIA  := PCK_FACT_GENERAL_COM1.FC_VALIDACIONPARAMETRO
                                             (UN_COMPANIA  => UN_COMPANIA
                                             ,UN_PARAMETRO    => 'SF CONCATENAR REFERENCIA FACTURA'); 
        MI_IDASOBANCARIA      := PCK_FACT_GENERAL_COM1.FC_VALIDACIONPARAMETRO
                                             (UN_COMPANIA  => UN_COMPANIA
                                             ,UN_PARAMETRO    => 'SF CONSECUTIVO ASOBANCARIA'); 
        MI_MANEJAINTSPUBLIC   := PCK_FACT_GENERAL_COM1.FC_VALIDACIONPARAMETRO
                                             (UN_COMPANIA  => UN_COMPANIA
                                             ,UN_PARAMETRO    => 'SF MANEJA FINANCIABLES A SERVICIOS PUBLICOS'); 
        MI_SIMPLEINTERFAZ     := PCK_FACT_GENERAL_COM1.FC_VALIDACIONPARAMETRO
                                             (UN_COMPANIA  => UN_COMPANIA
                                             ,UN_PARAMETRO    => 'SF SIMPLIFICAR COMPROBANTES');
        MI_SIN_EANRECAUDO     := PCK_FACT_GENERAL_COM1.FC_VALIDACIONPARAMETRO
                                             (UN_COMPANIA  => UN_COMPANIA
                                             ,UN_PARAMETRO    => 'SF CONSULTAR TIPO COBRO SIN EAN EN RECAUDO');
    BEGIN
        BEGIN
            MI_TABLA     := 'SF_ASOBANCARIA_ENC_ARC';
            MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || '''';
            MI_RTA       := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA, 
                                              UN_ACCION => 'E', 
                                              UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        END;
        BEGIN
            MI_TABLA     := 'SF_ASOBANCARIA_ENC_LOT';
            MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || '''';
            MI_RTA       := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA, 
                                              UN_ACCION => 'E', 
                                              UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        END;
        BEGIN
            MI_TABLA     := 'SF_ASOBANCARIA_DETALLE';
            MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || '''';
            MI_RTA       := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA, 
                                              UN_ACCION => 'E', 
                                              UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        END;
        BEGIN
            MI_TABLA     := 'SF_ASOBANCARIA_CON_LOT';
            MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || '''';
            MI_RTA       := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA, 
                                              UN_ACCION => 'E', 
                                              UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        END;
        BEGIN
            MI_TABLA     := 'SF_ASOBANCARIA_CON_ARC';
            MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || '''';
            MI_RTA       := PCK_DATOS.FC_ACME(UN_TABLA  => MI_TABLA, 
                                              UN_ACCION => 'E', 
                                              UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;

        END;
        BEGIN
            MI_TABLA     := 'SF_ASOBANCARIA_ERROR';
            MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || '''';
            MI_RTA       := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA, 
                                              UN_ACCION => 'E', 
                                              UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ERRORELIMINAR,
                                    UN_TABLAERROR => MI_TABLA);
    END;
    MI_CADENA := UN_CADENA;
    IF REGEXP_COUNT(MI_CADENA, '[^@]+') = 0 THEN
        BEGIN
            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                       UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_NOEXISTENREGISTRO);
        END;
    END IF;

    MI_ARCHIVO      :=  REGEXP_SUBSTR(UN_CADENA,'[^´@]+', 1);
    MI_NREGISTROS   :=  REGEXP_COUNT(UN_CADENA, '[^@]+');
    MI_CODIGOASOBA  := REPLACE(SUBSTR(SUBSTR(MI_ARCHIVO, 21, 20), 1, 3), '-', '');
    MI_NUMEROCTA    := LTRIM(REPLACE(SUBSTR(SUBSTR(MI_ARCHIVO, 21, 20), 4, 18),'-', ''),'0');
    MI_CONSECUTIVO  := 1;
    MI_NUMEROPAGOS  := 0;
    -- Se válida que BANCO y codigo EAN corresponda a uno existente en el sistema.
    SELECT REPLACE(BANCO.BANCOASOBANCARIA,'-','')
    INTO MI_TAMANO2
    FROM BANCO
    INNER JOIN CUENTABANCOS
      ON BANCO.COMPANIA          =  CUENTABANCOS.COMPANIA
     AND BANCO.BANCO             =  CUENTABANCOS.BANCO
    WHERE BANCO.COMPANIA          =  UN_COMPANIA
     AND CUENTABANCOS.ANO        =  EXTRACT(YEAR FROM SYSDATE)
     AND CUENTABANCOS.IDCONTABLE =  UN_BANCO;

    IF MI_PARAMETESORERIA IN ('SI') THEN        
        BEGIN
            BEGIN
            IF LENGTH(MI_TAMANO2)  = 3 THEN

                SELECT NVL(BANCO.NOMBREBANCO,'') 
                  INTO MI_NOMBREBANCO
                  FROM BANCO
                 INNER JOIN CUENTABANCOS
                    ON BANCO.COMPANIA          =  CUENTABANCOS.COMPANIA
                   AND BANCO.BANCO             =  CUENTABANCOS.BANCO
                 WHERE BANCO.COMPANIA          =  UN_COMPANIA
                   AND CUENTABANCOS.ANO        =  EXTRACT(YEAR FROM SYSDATE)
                   AND CUENTABANCOS.IDCONTABLE =  UN_BANCO
                   AND REPLACE(BANCO.BANCOASOBANCARIA,'-','') =  MI_CODIGOASOBA;
            ELSE
                SELECT NVL(BANCO.NOMBREBANCO,'') 
                  INTO MI_NOMBREBANCO
                  FROM BANCO
                 INNER JOIN CUENTABANCOS
                    ON BANCO.COMPANIA          =  CUENTABANCOS.COMPANIA
                   AND BANCO.BANCO             =  CUENTABANCOS.BANCO
                 WHERE BANCO.COMPANIA          =  UN_COMPANIA
                   AND CUENTABANCOS.ANO        =  EXTRACT(YEAR FROM SYSDATE)
                   AND CUENTABANCOS.IDCONTABLE =  UN_BANCO
                   AND SUBSTR(REPLACE(BANCO.BANCOASOBANCARIA,'-',''),2,4) =  MI_CODIGOASOBA;
            END IF;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                             UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_BANCONOCORRESPOND);
        END;
    END IF;    
    --Se valida que el numero de cuenta del plano corresponda con el configurado en la cuenta del banco seleccionado.
    BEGIN
        SELECT NVL(BANCO.NOMBREBANCO,''),
               BANCO.BANCO
          INTO MI_NOMBREBANCO,
               MI_CODIGOBANCO
          FROM BANCO
         INNER JOIN CUENTABANCOS
            ON BANCO.COMPANIA          =  CUENTABANCOS.COMPANIA
           AND BANCO.BANCO             =  CUENTABANCOS.BANCO
         WHERE BANCO.COMPANIA          =  UN_COMPANIA
           AND CUENTABANCOS.ANO        =  EXTRACT(YEAR FROM SYSDATE)
           AND CUENTABANCOS.IDCONTABLE =  UN_BANCO
           AND REPLACE(CUENTABANCOS.CUENTANUMERO,'-','') = TRIM(MI_NUMEROCTA);
    EXCEPTION WHEN NO_DATA_FOUND THEN
        BEGIN
            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                       UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_CUENTANOCORRESPON);
        END;
    END;

    MI_LINEAS := MI_NREGISTROS;

   WHILE MI_LINEAS <> 0
    LOOP
        MI_ARCHIVO    :=  REGEXP_SUBSTR(MI_CADENA, '[^´@]+', 1);
        MI_CADENA     :=  REPLACE(MI_CADENA, MI_ARCHIVO||'@','');
        MI_LINEAS     :=  REGEXP_COUNT(MI_CADENA, '[^@]+');
        MI_TIPOREGISTRO:= SUBSTR(MI_ARCHIVO,1,2);
        MI_CONSECUTIVO:=  PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    =>  MI_TABLA,
                                                           UN_CRITERIO =>  ' COMPANIA = '''||UN_COMPANIA||'''',
                                                           UN_CAMPO    =>  'ID',
                                                           UN_INICIAL  =>  '1');
        IF MI_TIPOREGISTRO IN ('01') THEN
            MI_TABLA      := 'SF_ASOBANCARIA_ENC_ARC';
            MI_CONSECUTIVO:=  PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    =>  MI_TABLA,
                                                           UN_CRITERIO =>  ' COMPANIA = '''||UN_COMPANIA||'''',
                                                           UN_CAMPO    =>  'ID',
                                                           UN_INICIAL  =>  '1');
            MI_CAMPOS     := 'COMPANIA, ID, TIPO_REGISTRO,FECHA_RECAUDO,
                           CODIGO_ENTIDAD,NUMERO_CUENTA,FECHA_ARCHIVO,
                           HORA,MODIFICADOR,TIPO_CUENTA,NIT_EMPRESA,
                           CREATED_BY,DATE_CREATED';
            MI_VALORES    := ' ''' || UN_COMPANIA            || ''',''' 
                                   || MI_CONSECUTIVO          || ''','''
                                   || MI_TIPOREGISTRO        || ''',''' 
                                   || SUBSTR(MI_ARCHIVO,13,8) || ''','''
                                   || SUBSTR(MI_ARCHIVO,21,3)|| ''',''' 
                                   || LTRIM(SUBSTR(MI_ARCHIVO,24,17), '0')|| ''','''
                                   || SUBSTR(MI_ARCHIVO,41,8)|| ''',''' 
                                   || SUBSTR(MI_ARCHIVO,49,4) || ''','''
                                   || LTRIM(SUBSTR(MI_ARCHIVO,53,1),'0')|| ''',''' 
                                   || LTRIM(SUBSTR(MI_ARCHIVO,54,2),'0') || ''','''
                                   || LTRIM(SUBSTR(MI_ARCHIVO,3,10),'0')|| ''',''' 
                                   || UN_USUARIO              || ''',SYSDATE';
            MI_FECHARECAUDO := SUBSTR(MI_ARCHIVO,13,8);
            BEGIN
                BEGIN
                    MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  MI_TABLA, 
                                                           UN_ACCION  =>  'I',
                                                           UN_CAMPOS  =>  MI_CAMPOS, 
                                                           UN_VALORES =>  MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    MI_REEMPLAZOS(0).CLAVE:='TIPO';
                    MI_REEMPLAZOS(0).VALOR:= MI_TIPOREGISTRO;
                    RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTTIPO
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_REEMPLAZOS => MI_REEMPLAZOS);
            END;
         END IF;    
        IF MI_TIPOREGISTRO IN ('05') THEN
            MI_TABLA      :=  'SF_ASOBANCARIA_ENC_LOT';
            MI_CONSECUTIVO:=  PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA=>  MI_TABLA,
                                                           UN_CRITERIO =>  ' COMPANIA = '''||UN_COMPANIA||'''',
                                                           UN_CAMPO    =>  'ID',
                                                           UN_INICIAL  =>  '1');
            MI_CAMPOS  := ' COMPANIA,ID,TIPO_REGISTRO,EAN,NUMERO_LOTE,
                            CREATED_BY,DATE_CREATED';
            MI_VALORES  := ' ''' || UN_COMPANIA             || ''','''
                                 || MI_CONSECUTIVO          || ''','''
                                 || MI_TIPOREGISTRO         || ''','''
                                 || LTRIM(SUBSTR(MI_ARCHIVO,3,13),'0') ||''','''
                                 || SUBSTR(MI_ARCHIVO,18,2) || ''','''
                                 || UN_USUARIO              || ''',SYSDATE';
            MI_EAN := LTRIM(SUBSTR(MI_ARCHIVO,3,13));
            BEGIN
                BEGIN
                    MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  MI_TABLA, 
                                                           UN_ACCION  =>  'I',
                                                           UN_CAMPOS  =>  MI_CAMPOS, 
                                                           UN_VALORES =>  MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    MI_REEMPLAZOS(0).CLAVE:='TIPO';
                    MI_REEMPLAZOS(0).VALOR:= MI_TIPOREGISTRO;
                    RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTTIPO
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_REEMPLAZOS => MI_REEMPLAZOS);
            END;
          END IF;   
          IF MI_TIPOREGISTRO IN ('06') THEN
            MI_TIPORECAUDO := TO_NUMBER(SUBSTR(MI_ARCHIVO,27,5));
            MI_EAN_ERRADO := 0;
            IF MI_EANPORTIPO IN ('SI') THEN       


                BEGIN
                    --CONSULTAR TIPO COBRO SIN EAN EN RECAUDO
                    IF MI_SIN_EANRECAUDO IN('SI') THEN 

                        SELECT CODIGOEAN
                          INTO MI_EXISTE
                          FROM SF_TIPO_COBRO
                         WHERE COMPANIA = UN_COMPANIA
                           AND ANO = SUBSTR(MI_FECHARECAUDO,1,4)
                           AND TIPOFACTURA_RECAUDO = MI_TIPORECAUDO;
                    ELSE 
                        SELECT CODIGOEAN
                          INTO MI_EXISTE
                          FROM SF_TIPO_COBRO
                         WHERE COMPANIA = UN_COMPANIA
                           AND ANO = SUBSTR(MI_FECHARECAUDO,1,4)
                           AND CODIGOEAN = NVL(MI_EAN, '')
                           AND TIPOFACTURA_RECAUDO= MI_TIPORECAUDO;


                    END IF;

                    EXCEPTION WHEN NO_DATA_FOUND THEN
                      MI_EAN_ERRADO := 1;


                END;
            ELSE
                IF MI_EAN <> MI_EANPARAMETRO THEN
                    MI_EAN_ERRADO := 1;

                END IF;
            END IF;

            IF MI_EAN_ERRADO = 0 THEN
                MI_TABLA      :=  'SF_ASOBANCARIA_DETALLE';
                MI_CONSECUTIVO:=  PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA=>  MI_TABLA,
                                  UN_CRITERIO =>  ' COMPANIA = '''||UN_COMPANIA||'''',
                                  UN_CAMPO    =>  'ID',
                                  UN_INICIAL  =>  '1');
                MI_CAMPOS  := ' COMPANIA,ID,TIPO_REGISTRO,CODIGO,VALOR,PROCEDENCIA,                
                            MEDIOS_PAGO,NUMERO_OPERACION,NUMERO_AUTORIZACION,        
                            CODIGO_ENTIDAD,CODIGO_SUCURSAL,SECUENCIA,CAUSAL,
                            CREATED_BY,DATE_CREATED';
                MI_VALORES  := ' ''' || UN_COMPANIA             || ''','''
                                 || MI_CONSECUTIVO          || ''','''
                                 || MI_TIPOREGISTRO         || ''','''
                                 || (SUBSTR(MI_ARCHIVO,3,48)) || ''','''
                                 || SUBSTR(MI_ARCHIVO,51,14)|| ''','''
                                 || SUBSTR(MI_ARCHIVO,65,2) || ''','''
                                 || SUBSTR(MI_ARCHIVO,67,2) || ''','''
                                 || SUBSTR(MI_ARCHIVO,69,6) || ''','''
                                 || SUBSTR(MI_ARCHIVO,75,6) || ''','''
                                 || SUBSTR(MI_ARCHIVO,81,3) || ''','''
                                 || SUBSTR(MI_ARCHIVO,84,4) || ''','''
                                 || SUBSTR(MI_ARCHIVO,88,7) || ''','''
                                 || SUBSTR(MI_ARCHIVO,95,3) || ''','''
                                 || UN_USUARIO              || ''',SYSDATE';


                BEGIN
                    BEGIN
                        MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  MI_TABLA, 
                                                           UN_ACCION  =>  'I',
                                                           UN_CAMPOS  =>  MI_CAMPOS, 
                                                           UN_VALORES =>  MI_VALORES);

                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            MI_REEMPLAZOS(0).CLAVE:='TIPO';
                            MI_REEMPLAZOS(0).VALOR:= MI_TIPOREGISTRO;
                            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;

                    END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
                        PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTTIPO
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_REEMPLAZOS => MI_REEMPLAZOS);

                END;
            ELSE 
                MI_MENSAJEERROR := 'El tipo de factura con codigo equivalente ' || MI_TIPORECAUDO || 
                          ' no existe o el codigo EAN es incorrecto: '|| MI_EAN; 

                MI_CAMPOS   := 'COMPANIA,TIPO, FACTURA, FECHA, BANCO, 
                               ID, CONSECUTIVO, CREATED_BY, DATE_CREATED, DESCRIPCION';                          
                MI_VALORES  := ''''|| UN_COMPANIA             || ''','''
                                     || 'NE'          || ''','''
                                     || TO_NUMBER(SUBSTR(MI_ARCHIVO,41,10)) || ''','''
                                     || TO_DATE(MI_FECHARECAUDO,'YYYYMMDD')|| ''','''
                                     || UN_BANCO                || ''','''
                                     || MI_IDASOBANCARIA        || ''','''
                                     || MI_CONSECUTIVOERROR        || ''','''
                                     || UN_USUARIO              || ''',SYSDATE,';
                MI_VALORES := MI_VALORES || '''' || MI_MENSAJEERROR || '''';

                BEGIN
                    BEGIN 
                        MI_RETORNO := -1;
                        MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  'SF_ASOBANCARIA_ERROR', 
                                                               UN_ACCION  =>  'I',
                                                               UN_CAMPOS  =>  MI_CAMPOS, 
                                                               UN_VALORES =>  MI_VALORES);
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;

                    END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
                              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                 ,UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ERRORINSERERROR);

                END;                   
                
                IF MI_RTA > 0 THEN 
                    MI_CONSECUTIVOERROR := MI_CONSECUTIVOERROR + 1;


                END IF;
            END IF;
        END IF;
        IF MI_TIPOREGISTRO IN ('08') THEN
            MI_TABLA      :=  'SF_ASOBANCARIA_CON_LOT';
            MI_CONSECUTIVO:=  PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA=>  MI_TABLA,
                                  UN_CRITERIO =>  ' COMPANIA = '''||UN_COMPANIA||'''',
                                  UN_CAMPO    =>  'ID',
                                  UN_INICIAL  =>  '1');
            MI_CAMPOS  := ' COMPANIA,ID,TIPO_REGISTRO,TOTAL_REGISTROS,                 
                            VALOR_TOTAL,NUMERO_LOTE,CREATED_BY,DATE_CREATED';
            MI_VALORES  := ' ''' || UN_COMPANIA             || ''','''
                                 || MI_CONSECUTIVO          || ''','''
                                 || MI_TIPOREGISTRO         || ''','''
                                 || LTRIM(SUBSTR(MI_ARCHIVO,3,9),'0') || ''','''
                                 || LTRIM(SUBSTR(MI_ARCHIVO,12,18),'0')|| ''','''
                                 || SUBSTR(MI_ARCHIVO,32,2)|| ''','''
                                 || UN_USUARIO              || ''',SYSDATE';
            BEGIN
                BEGIN
                    MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  MI_TABLA, 
                                                           UN_ACCION  =>  'I',
                                                           UN_CAMPOS  =>  MI_CAMPOS, 
                                                           UN_VALORES =>  MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    MI_REEMPLAZOS(0).CLAVE:='TIPO';
                    MI_REEMPLAZOS(0).VALOR:= MI_TIPOREGISTRO;
                    RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTTIPO
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_REEMPLAZOS => MI_REEMPLAZOS);
             END;
        END IF;    
        IF MI_TIPOREGISTRO IN ('09') THEN
            MI_TABLA      :=  'SF_ASOBANCARIA_CON_ARC';
            MI_CONSECUTIVO:=  PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA=>  MI_TABLA,
                                                           UN_CRITERIO =>  ' COMPANIA = '''||UN_COMPANIA||'''',
                                                           UN_CAMPO    =>  'ID',
                                                           UN_INICIAL  =>  '1');
            MI_CAMPOS  := ' COMPANIA,ID,TIPO_REGISTRO,TOTAL_REGISTROS,                 
                            VALOR_TOTAL,CREATED_BY,DATE_CREATED';
            MI_VALORES  := ' ''' || UN_COMPANIA             || ''','''
                                 || MI_CONSECUTIVO          || ''','''
                                 || MI_TIPOREGISTRO         || ''','''
                                 || LTRIM(SUBSTR(MI_ARCHIVO,3,9),'0')|| ''','''
                                 || LTRIM(SUBSTR(MI_ARCHIVO,12,18),'0')
                                 || ''','''|| UN_USUARIO            || ''',SYSDATE';

            BEGIN
                BEGIN
                    MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  MI_TABLA, 
                                                           UN_ACCION  =>  'I',
                                                           UN_CAMPOS  =>  MI_CAMPOS, 
                                                           UN_VALORES =>  MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    MI_REEMPLAZOS(0).CLAVE:='TIPO';
                    MI_REEMPLAZOS(0).VALOR:= MI_TIPOREGISTRO;
                    RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTTIPO
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_REEMPLAZOS => MI_REEMPLAZOS);
            END;
        END IF;    
    END LOOP;

    BEGIN 
        BEGIN
            MI_IDASOBANCARIA := MI_IDASOBANCARIA + 1;
            MI_CAMPOS := 'VALOR =''' ||  MI_IDASOBANCARIA || '''';
            MI_CONDICION := 'COMPANIA       = ''' || UN_COMPANIA || '''
                             AND NOMBRE     = ''SF CONSECUTIVO ASOBANCARIA'' 
                             AND MODULO     = -1';
            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'PARAMETRO', 
                                                   UN_ACCION    => 'M', 
                                                   UN_CAMPOS    =>  MI_CAMPOS, 
                                                   UN_CONDICION =>  MI_CONDICION );
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN    
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_ERRORACTPARAMETRO);
    END;
        --Validación para que identifique si el código EAN por el que esta recaudando es el correcto
    BEGIN
        BEGIN
            SELECT FECHA_RECAUDO 
              INTO MI_FECHARECAUDO 
              FROM SF_ASOBANCARIA_ENC_ARC 
             WHERE COMPANIA = UN_COMPANIA;
        EXCEPTION  WHEN TOO_MANY_ROWS THEN
               RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;        
        END;

        BEGIN
            SELECT TOTAL_REGISTROS, VALOR_TOTAL
              INTO MI_ARTOTALREGISTROS, MI_ARVALORTOTAL          
              FROM SF_ASOBANCARIA_CON_ARC
             WHERE COMPANIA = UN_COMPANIA;
        EXCEPTION  WHEN TOO_MANY_ROWS THEN
               RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        END;

      /*  BEGIN
            SELECT TOTAL_REGISTROS, VALOR_TOTAL, NUMERO_LOTE
              INTO MI_COTOTALREGISTROS, MI_COVALORTOTAL, MI_CONUMEROLOTE
              FROM SF_ASOBANCARIA_CON_LOT
             WHERE COMPANIA = UN_COMPANIA;
        EXCEPTION  WHEN TOO_MANY_ROWS THEN
             RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        END; */
        /*BEGIN
            SELECT EAN, NUMERO_LOTE
              INTO MI_EAN, MI_ENNUMEROLOTE
              FROM SF_ASOBANCARIA_ENC_LOT
             WHERE COMPANIA = UN_COMPANIA;
        EXCEPTION  WHEN TOO_MANY_ROWS THEN
             RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;         
        END;*/
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                   UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_REGISTROSADICION);
    END;




/*
        SELECT DISTINCT CODIGO
              INTO MI_CODIGO
              FROM SF_ASOBANCARIA_DETALLE   
             WHERE COMPANIA = UN_COMPANIA
                AND ID = 1;                 

        MI_TIPORECAUDO  :=  TO_NUMBER(SUBSTR(MI_CODIGO,25,5));    


*/
     -- Validación de dia activo respecto a la fecha de recaudo 
    MI_CFECHARECAUDO := TO_DATE(SUBSTR(MI_FECHARECAUDO,7,2) || '/'||
                                SUBSTR(MI_FECHARECAUDO,5,2) || '/'|| 
                                SUBSTR(MI_FECHARECAUDO,1,4));

    MI_ESTADOPERIODODIA := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADODIA(
                                UN_COMPANIA => UN_COMPANIA,
                                UN_ANO      => SUBSTR(MI_FECHARECAUDO,1,4),
                                UN_MES      => SUBSTR(MI_FECHARECAUDO,5,2),
                                UN_DIA      => SUBSTR(MI_FECHARECAUDO,7,2),
                                UN_MODULO   => PCK_DATOS.MODULOCONTABILIDAD,
                                UN_PROCESO  => '1');
    IF MI_ESTADOPERIODODIA NOT IN ('A') THEN
        BEGIN
            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_PRECAUDOCERRADO
                           ,UN_REEMPLAZOS => MI_REEMPLAZOS);
        END;
    END IF;
    FOR RS IN (SELECT CODIGO, VALOR
                  INTO MI_CODIGO, MI_VALOR    
                  FROM SF_ASOBANCARIA_DETALLE   
                 WHERE COMPANIA = UN_COMPANIA)
        LOOP
            MI_CODIGO := RS.CODIGO;
            MI_VALOR  := RS.VALOR;
            IF MI_PARCONCREFERENCIA IN ('SI') AND MI_PARAMACTAREFERENCIA IN ('NO') THEN
                MI_RECIBOPAGO :=  SUBSTR(MI_CODIGO, 1, 32);
                MI_NITUSUARIO :=  SUBSTR(MI_CODIGO, 33, 48);
                MI_RECIBOPAGO :=  SUBSTR(MI_RECIBOPAGO, LENGTH(MI_RECIBOPAGO)-12,LENGTH(MI_RECIBOPAGO));
                MI_REFERENCIAS  :=  SUBSTR(MI_RECIBOPAGO, 1, 2);
                IF MI_REFERENCIAS IN (NVL('00','')) THEN
                    MI_RECIBOPAGO := SUBSTR(MI_RECIBOPAGO,4,11);
                ELSE
                    MI_RECIBOPAGO := SUBSTR(MI_RECIBOPAGO,3,11);
                END IF;
                MI_CALTOTCADENA :=  LENGTH(MI_RECIBOPAGO);
                MI_TOTALRECIBO  :=  SUBSTR(MI_VALOR, 1, 12);
            ELSE
                IF MI_PARAMACTAREFERENCIA IN ('SI') THEN
                    MI_RECIBOPAGO :=  SUBSTR(MI_CODIGO, 39, 10);
                    MI_TOTALRECIBO:=  SUBSTR(MI_VALOR,1,14);
                ELSE
                    MI_RECIBOPAGO :=  SUBSTR(MI_CODIGO, 27, 10);
                    MI_NITUSUARIO :=  SUBSTR(MI_CODIGO, 37, 12);
                    MI_TOTALRECIBO:=  SUBSTR(MI_VALOR,1,12);
                END IF;
            END IF; 
            MI_TIPORECAUDO  :=  TO_NUMBER(SUBSTR(MI_CODIGO,25,5));
            BEGIN
                SELECT NVL(CODIGO,'NE')
                  INTO MI_TIPOFACTURA
                  FROM SF_TIPO_COBRO 
                 WHERE COMPANIA           = UN_COMPANIA
                   AND ANO                = SUBSTR(MI_FECHARECAUDO,1,4)
                   AND TIPOFACTURA_RECAUDO= MI_TIPORECAUDO;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_TIPOFACTURA := 'NE';
            END;
            IF MI_TIPOFACTURA IN ('NE') THEN
                MI_FACTCONDICIONT := 'NUMERO_FACTURA';
                MI_FACTCONDICIONN := 'VALOR_TOTAL';
                MI_FACTVALORT     := MI_RECIBOPAGO;
                MI_FACTVALORN     := MI_TOTALRECIBO;
            ELSE
                MI_FACTCONDICIONT := 'TIPO_FACTURA';
                MI_FACTCONDICIONN := 'NUMERO_FACTURA';
                MI_FACTVALORT     := MI_TIPOFACTURA;
                MI_FACTVALORN     := MI_RECIBOPAGO;
            END IF;
            DBMS_OUTPUT.PUT_LINE('VALORES1:'|| MI_FACTCONDICIONT||'**'||MI_FACTVALORT||'**'||MI_FACTCONDICIONN||'**'||MI_FACTVALORN); 
            BEGIN
                MI_STRSQL := 'SELECT  TIPOCPTE_PAGO, NROCPTE_PAGO,FECHA_VENCIMIENTO,INTERFAZADA, 
                                      TIPO_FACTURA, TERCERO, NUMERO_FACTURA, DIFERIDA, 
                                      OBSERVACIONES, ANO_CPTE, TIPO_CPTE, NRO_CPTE, 
                                      TIPO_ABONO, NRO_ABONO, CENTRO_COSTO, AUXILIAR, FECHA_PAGO 
                                FROM  SF_FACTURA
                               WHERE  COMPANIA = ''' || UN_COMPANIA || 
                                         ''' AND ' || MI_FACTCONDICIONT || ' = ''' || MI_FACTVALORT || 
                                         ''' AND ' || MI_FACTCONDICIONN || ' = ''' || MI_FACTVALORN || '''';
                EXECUTE IMMEDIATE MI_STRSQL
                      INTO  MI_FACTIPOCPTEPAGO, MI_FACNROCPTEPAGO,MI_FACFECHVENCIM, MI_FACINTERFAZADA,
                            MI_FACTIPOFACTURA, MI_FACTERCERO,MI_FACNUMEROFACTURA, MI_FACDIFERIDA,
                            MI_FACOBSERVACIONES, MI_FACANOCPTE,MI_FACTIPOCPTE, MI_FACNROCPTE,
                            MI_FACTIPOABONO, MI_FACNROABONO,MI_FACCENTROCOSTO, MI_FACAUXILIAR,
                            MI_FACFECHAPAGO;

                            MI_EXISTE := -1; 
            EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_EXISTE := 0;


            MI_TABLA    :=  'SF_ASOBANCARIA_ERROR';
            MI_CAMPOS   := 'COMPANIA,TIPO, FACTURA, FECHA, BANCO, 
                            ID, CONSECUTIVO, CREATED_BY, DATE_CREATED, DESCRIPCION';
            MI_VALORES  := ''''|| UN_COMPANIA             || ''','''
                               || MI_TIPOFACTURA          || ''','''
                               || MI_RECIBOPAGO           || ''','''
                               || MI_CFECHARECAUDO        || ''','''
                               || UN_BANCO                || ''','''
                               || MI_IDASOBANCARIA        || ''','''
                               || MI_IDASOBANCARIA        || ''','''
                               || UN_USUARIO              || ''',SYSDATE,';
            END;
            --Si existe factura causada
            IF MI_EXISTE <> 0 THEN
                BEGIN 
                    SELECT NOAPLICACAUSACION,
                           INTERFAZ_RECAUDO
                      INTO MI_APLICACAUSACION,
                           MI_INTERFAZRECAUDO
                      FROM SF_TIPO_COBRO
                     WHERE COMPANIA = UN_COMPANIA
                       AND CODIGO = MI_FACTIPOFACTURA
                       AND ANO = SUBSTR(MI_FECHARECAUDO, 1, 4);
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_APLICACAUSACION := 0;
                END;
                IF MI_FACTIPOCPTEPAGO IS NOT NULL AND MI_FACNROCPTEPAGO <> 0 THEN
                    MI_MENSAJEERROR := 'La factura No ' || MI_RECIBOPAGO || 
                    ' ya tiene registro de recaudo con el comprobante '||
                    MI_FACTIPOCPTEPAGO || ' - ' || MI_FACNROCPTEPAGO;

                    MI_CAMPOS   := 'COMPANIA,TIPO, FACTURA, FECHA, BANCO, 
                                    ID, CONSECUTIVO, CREATED_BY, DATE_CREATED, DESCRIPCION';

                     MI_VALORES  := ''''|| UN_COMPANIA             || ''','''
                               || MI_TIPOFACTURA          || ''','''
                               || MI_RECIBOPAGO           || ''','''
                               || MI_CFECHARECAUDO        || ''','''
                               || UN_BANCO                || ''','''
                               || MI_IDASOBANCARIA        || ''','''
                               || MI_CONSECUTIVOERROR        || ''','''
                               || UN_USUARIO              || ''',SYSDATE,';

                    MI_VALORES := MI_VALORES || '''' || MI_MENSAJEERROR || '''';
                    BEGIN
                        BEGIN 
                            MI_RETORNO := -1;
                            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  'SF_ASOBANCARIA_ERROR', 
                                                         UN_ACCION  =>  'I',
                                                         UN_CAMPOS  =>  MI_CAMPOS, 
                                                         UN_VALORES =>  MI_VALORES);
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                        END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ERRORINSERERROR);
                    END;
                    IF MI_RTA > 0 THEN 
                        MI_CONSECUTIVOERROR := MI_CONSECUTIVOERROR + 1;
                    END IF;
                END IF;
                IF MI_FACFECHVENCIM < MI_CFECHARECAUDO THEN
                    MI_MENSAJEERROR := 'La factura No ' || MI_RECIBOPAGO || 
                    ' no se puede recaudar, la fecha de vencimiento '||
                    TO_CHAR(MI_FACFECHVENCIM,'DD/MM/YYYY') || ' se ha superadoo '; 

                     MI_CAMPOS   := 'COMPANIA,TIPO, FACTURA, FECHA, BANCO, 
                                    ID, CONSECUTIVO, CREATED_BY, DATE_CREATED, DESCRIPCION';                    
                     MI_VALORES  := ''''|| UN_COMPANIA             || ''','''
                               || MI_TIPOFACTURA          || ''','''
                               || MI_RECIBOPAGO           || ''','''
                               || MI_CFECHARECAUDO        || ''','''
                               || UN_BANCO                || ''','''
                               || MI_IDASOBANCARIA        || ''','''
                               || MI_CONSECUTIVOERROR        || ''','''
                               || UN_USUARIO              || ''',SYSDATE,';
                    MI_VALORES := MI_VALORES || '''' || MI_MENSAJEERROR || '''';
                    BEGIN
                        BEGIN 
                            MI_RETORNO := -1;
                            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  'SF_ASOBANCARIA_ERROR', 
                                                         UN_ACCION  =>  'I',
                                                         UN_CAMPOS  =>  MI_CAMPOS, 
                                                         UN_VALORES =>  MI_VALORES);
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                        END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ERRORINSERERROR);
                    END;
                    IF MI_RTA > 0 THEN 
                        MI_CONSECUTIVOERROR := MI_CONSECUTIVOERROR + 1;
                    END IF;
                END IF;


                IF  MI_INTERFAZRECAUDO NOT IN (0) AND MI_FACINTERFAZADA  IN(0)AND MI_APLICACAUSACION IN (0) THEN

                   PCK_DATOS.GL_RTA := PCK_FACT_GENERAL_COM3.FC_INTERFAZAR_FACTURA(UN_COMPANIA         => UN_COMPANIA,
                                                           UN_TIPOFACTURA      => MI_TIPOFACTURA,
                                                           UN_NOFACTURA        => MI_RECIBOPAGO ,
                                                           UN_FECHAPAGO        => MI_CFECHARECAUDO,
                                                           UN_VERMENSAJE       => 0,
                                                           UN_MANEJAINVENTARIO => 0,
                                                           UN_USUARIO          => UN_USUARIO);
                   MI_FACINTERFAZADA := -1;

                   IF  PCK_DATOS.GL_RTA IN (0) THEN

                          MI_MENSAJEERROR := 'La factura No ' || MI_RECIBOPAGO || 
                          ' presento inconvenientes en el proceso de causación. '||
                          TO_CHAR(MI_FACFECHVENCIM,'DD/MM/YYYY') || ' se ha superadoo '; 

                          MI_CAMPOS   := 'COMPANIA,TIPO, FACTURA, FECHA, BANCO, 
                                    ID, CONSECUTIVO, CREATED_BY, DATE_CREATED, DESCRIPCION';                          
                          MI_VALORES  := ''''|| UN_COMPANIA             || ''','''
                                     || MI_TIPOFACTURA          || ''','''
                                     || MI_RECIBOPAGO           || ''','''
                                     || MI_CFECHARECAUDO        || ''','''
                                     || UN_BANCO                || ''','''
                                     || MI_IDASOBANCARIA        || ''','''
                                     || MI_CONSECUTIVOERROR        || ''','''
                                     || UN_USUARIO              || ''',SYSDATE,';
                          MI_VALORES := MI_VALORES || '''' || MI_MENSAJEERROR || '''';
                          BEGIN
                              BEGIN 
                              MI_RETORNO := -1;
                                  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  'SF_ASOBANCARIA_ERROR', 
                                                               UN_ACCION  =>  'I',
                                                               UN_CAMPOS  =>  MI_CAMPOS, 
                                                               UN_VALORES =>  MI_VALORES);
                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                  RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                              END;
                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
                              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                 ,UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ERRORINSERERROR);
                          END;                   
                      IF MI_RTA > 0 THEN 
                        MI_CONSECUTIVOERROR := MI_CONSECUTIVOERROR + 1;
                      END IF;

                    MI_FACINTERFAZADA := 0;

                   END IF;


                END IF;

                IF MI_FACINTERFAZADA IN ('0') AND MI_MANEJAINTSPUBLIC NOT IN ('SI') THEN
                    MI_MENSAJEERROR := 'La factura No. ' || MI_RECIBOPAGO || ' no tiene comprobante de causación. ' ;

                    MI_CAMPOS   := 'COMPANIA,TIPO, FACTURA, FECHA, BANCO, 
                                    ID, CONSECUTIVO, CREATED_BY, DATE_CREATED, DESCRIPCION';                
                    MI_VALORES  := ''''|| UN_COMPANIA             || ''','''
                               || MI_TIPOFACTURA          || ''','''
                               || MI_RECIBOPAGO           || ''','''
                               || MI_CFECHARECAUDO        || ''','''
                               || UN_BANCO                || ''','''
                               || MI_IDASOBANCARIA        || ''','''
                               || MI_CONSECUTIVOERROR        || ''','''
                               || UN_USUARIO              || ''',SYSDATE,';

                    MI_VALORES := MI_VALORES || '''' || MI_MENSAJEERROR || '''';           
                    BEGIN
                        BEGIN 
                        MI_RETORNO := -1;
                            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  'SF_ASOBANCARIA_ERROR', 
                                                         UN_ACCION  =>  'I',
                                                         UN_CAMPOS  =>  MI_CAMPOS, 
                                                         UN_VALORES =>  MI_VALORES);
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                        END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ERRORINSERERROR);
                    END;
                    IF MI_RTA > 0 THEN 
                        MI_CONSECUTIVOERROR := MI_CONSECUTIVOERROR + 1;
                    END IF;
                END IF; 
                --DIFERIDA
                IF MI_FACDIFERIDA IN ('0') THEN
                    MI_DESCRIPCION := SUBSTR( 'Pago de la factura ' || MI_TIPOFACTURA || 
                                              ' - ' || MI_RECIBOPAGO || ' ' || 
                                              MI_FACOBSERVACIONES,1,255); 
                    BEGIN
                        SELECT TIPOCOBRO,
                               CODIGO_COBRO,
                               OBSERVACIONES
                          INTO MI_OBJTIPOCOBRO,
                               MI_OBJCODIGOCOBRO,
                               MI_OBJOBSERVACIONES
                          FROM SF_OBJETO_COBRO
                         WHERE COMPANIA  = UN_COMPANIA
                           AND TIPOFACTURA = MI_TIPOFACTURA
                           AND NRO_FACTURA = MI_RECIBOPAGO;

                      EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
                        MI_OBJTIPOCOBRO:=NULL;
                        MI_OBJCODIGOCOBRO:=NULL;
                        MI_OBJOBSERVACIONES:=NULL;
                    END;

                    IF MI_OBJTIPOCOBRO IS NOT NULL AND MI_OBJCODIGOCOBRO IS NOT NULL THEN 

                         MI_CAMPOS   := 'COMPANIA,TIPO, FACTURA, FECHA, BANCO, 
                                    ID, CONSECUTIVO, CREATED_BY, DATE_CREATED, DESCRIPCION';                        
                         MI_VALORES  := ''''|| UN_COMPANIA             || ''','''
                               || MI_TIPOFACTURA          || ''','''
                               || MI_RECIBOPAGO           || ''','''
                               || MI_CFECHARECAUDO        || ''','''
                               || UN_BANCO                || ''','''
                               || MI_IDASOBANCARIA        || ''','''
                               || MI_CONSECUTIVOERROR        || ''','''
                               || UN_USUARIO              || ''',SYSDATE,';

                        IF NVL(MI_FACDIFERIDA,0) <> 0 THEN
                            MI_MENSAJEERROR  := 'Pago Parcial a la factura' 
                                                     || MI_FACTIPOFACTURA   ||  
                                               ' - ' || MI_FACNUMEROFACTURA || 
                                               ' - ' || MI_OBJOBSERVACIONES;
                            MI_VALORES := MI_VALORES || '''' || MI_MENSAJEERROR || '''';                  
                            -- pendiente Llamada a MI_DESCRIPCION = GenerarIngresoAbono() interfaz contabilidad
                        ELSE
                            MI_DESCRIPCION := SUBSTR( 'Recaudo factura ' || MI_FACTIPOFACTURA || 
                                              ' - ' || MI_FACNUMEROFACTURA || ' ' || 
                                              MI_OBJOBSERVACIONES,1,255); 


                        DBMS_OUTPUT.PUT_LINE('VALORES:'|| UN_COMPANIA||'**'||MI_FACANOCPTE||'**'||MI_TIPOFACTURA||'**'||MI_OBJCODIGOCOBRO); 
                        SELECT NVL(INDPAGO,0)
                        INTO MI_INDPAGO
                        FROM SF_OBJETO_COBRO
                        WHERE COMPANIA   = UN_COMPANIA
                      --  AND ANO          = SUBSTR(MI_FECHARECAUDO, 1, 4)
                        AND TIPOCOBRO    = MI_TIPOFACTURA
                        AND CODIGO_COBRO = MI_OBJCODIGOCOBRO;
                        IF MI_INDPAGO IN (0) THEN 
                         MI_RTA_RECAUDO := PCK_FACT_GENERAL_COM3.FC_RECAUDARFACTURA(UN_COMPANIA       => UN_COMPANIA,
                                                              UN_TIPOFACTURA    => MI_TIPOFACTURA,
                                                              UN_NUMEROFACTURA  => MI_RECIBOPAGO,
                                                              UN_OBSERVACION    => MI_DESCRIPCION,
                                                              UN_CUENTA         => UN_BANCO, 
                                                              UN_FECHA          => MI_CFECHARECAUDO,  
                                                              UN_ANIO           => SUBSTR(MI_FECHARECAUDO, 1, 4),                                                              
                                                              UN_DIFERIDA       => MI_FACDIFERIDA,
                                                              UN_USUARIO        => UN_USUARIO);

                            ------- Traer informacion de la factura recuadada.
                            IF MI_RTA_RECAUDO IS NOT NULL THEN
                                        BEGIN
                                            MI_STRSQL := 'SELECT  TIPOCPTE_PAGO, NROCPTE_PAGO
                                            FROM  SF_FACTURA
                                            WHERE  COMPANIA = ''' || UN_COMPANIA || 
                                                     ''' AND ' || MI_FACTCONDICIONT || ' = ''' || MI_FACTVALORT || 
                                                     ''' AND ' || MI_FACTCONDICIONN || ' = ''' || MI_FACTVALORN || '''';
                                            EXECUTE IMMEDIATE MI_STRSQL
                                            INTO  MI_RTA_RECAUDOTIPOCPTE, MI_RTA_RECAUDOCPTE;
                                            EXCEPTION WHEN NO_DATA_FOUND THEN
                                                MI_RTA_RECAUDOTIPOCPTE := NULL;
                                                MI_RTA_RECAUDOCPTE := NULL;
                                        END;
                                        IF MI_RTA_RECAUDOTIPOCPTE IS NULL AND MI_RTA_RECAUDOCPTE IS NULL  THEN
                                            MI_MENSAJEERROR := 'No se pudieron obtener los detalles de la factura No. ' || MI_RECIBOPAGO ;
                                        ELSE
                                            MI_MENSAJEERROR := 'La factura No. ' || MI_RECIBOPAGO || ' se recaudo con el comprobante ' || MI_RTA_RECAUDOTIPOCPTE || ' - ' || MI_RTA_RECAUDOCPTE ;
                                        END IF;

                                            MI_CAMPOS   := 'COMPANIA,TIPO, FACTURA, FECHA, BANCO, 
                                                        ID, CONSECUTIVO, CREATED_BY, DATE_CREATED, DESCRIPCION';                
                                            MI_VALORES  := ''''|| UN_COMPANIA             || ''','''
                                                       || MI_TIPOFACTURA          || ''','''
                                                       || MI_RECIBOPAGO           || ''','''
                                                       || MI_CFECHARECAUDO        || ''','''
                                                       || UN_BANCO                || ''','''
                                                       || MI_IDASOBANCARIA        || ''','''
                                                       || MI_CONSECUTIVOERROR        || ''','''
                                                       || UN_USUARIO              || ''',SYSDATE,';

                                            MI_VALORES := MI_VALORES || '''' || MI_MENSAJEERROR || '''';           
                                            BEGIN
                                                BEGIN
                                                MI_RETORNO := -1;
                                                    MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  'SF_ASOBANCARIA_ERROR', 
                                                                                 UN_ACCION  =>  'I',
                                                                                 UN_CAMPOS  =>  MI_CAMPOS, 
                                                                                 UN_VALORES =>  MI_VALORES);
                                                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                                    RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                                                END;
                                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
                                                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                                   ,UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ERRORINSERERROR);
                                            END;
                                            IF MI_RTA > 0 THEN 
                                                MI_CONSECUTIVOERROR := MI_CONSECUTIVOERROR + 1;
                                            END IF;
                                END IF;

                        ELSE 
                           --En este caso ya esta recaudada por ende el proceso debe CONTINUAR
                          MI_RTA_RECAUDO :='';
                        END IF;

                            MI_NROCPTEPAGO  := SUBSTR(MI_RTA_RECAUDO, INSTR(MI_RTA_RECAUDO,',',1)+1);
                            MI_PTABLA := 'SF_ASOBANCARIA_PAGOS';
                            MI_PCAMPOS := ' COMPANIA, ID, TIPO, FACTURA, PREVAL, FECHA, BANCO, 
                                            ID_LOG, CREATED_BY, DATE_CREATED';
                            MI_PVALORES:=''''|| UN_COMPANIA || ''','''
                                             || MI_IDASOBANCARIA || ''','''
                                             || MI_FACTIPOFACTURA|| ''','''
                                             || MI_RECIBOPAGO    || ''','''
                                             || MI_TOTALRECIBO   || ''','''
                                             || MI_CFECHARECAUDO || ''','''
                                             || UN_BANCO         || ''','''
                                             || MI_IDASOBANCARIA || ''','''
                                             || UN_USUARIO       || ''',
                                             SYSDATE';
                            BEGIN
                                BEGIN 
                                    MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  MI_PTABLA, 
                                                                 UN_ACCION  =>  'I',
                                                                 UN_CAMPOS  =>  MI_PCAMPOS, 
                                                                 UN_VALORES =>  MI_PVALORES);
                                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                    RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                                END;
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
                                MI_MENSAJEERROR := 'La factura No. ' || MI_RECIBOPAGO || ' esta duplicada en el plano o presento alguna inconsistencia al registrar el pago.  ' ;

                                    MI_CAMPOS   := 'COMPANIA,TIPO, FACTURA, FECHA, BANCO, 
                                                    ID, CONSECUTIVO, CREATED_BY, DATE_CREATED, DESCRIPCION';                
                                    MI_VALORES  := ''''|| UN_COMPANIA             || ''','''
                                               || MI_TIPOFACTURA          || ''','''
                                               || MI_RECIBOPAGO           || ''','''
                                               || MI_CFECHARECAUDO        || ''','''
                                               || UN_BANCO                || ''','''
                                               || MI_IDASOBANCARIA        || ''','''
                                               || MI_CONSECUTIVOERROR        || ''','''
                                               || UN_USUARIO              || ''',SYSDATE,';

                                    MI_VALORES := MI_VALORES || '''' || MI_MENSAJEERROR || '''';           
                                    BEGIN
                                        BEGIN 
                                        MI_RETORNO := -1;
                                            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  'SF_ASOBANCARIA_ERROR', 
                                                                         UN_ACCION  =>  'I',
                                                                         UN_CAMPOS  =>  MI_CAMPOS, 
                                                                         UN_VALORES =>  MI_VALORES);
                                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                                        END;
                                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
                                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ERRORINSERERROR);
                                    END;
                                    IF MI_RTA > 0 THEN 
                                        MI_CONSECUTIVOERROR := MI_CONSECUTIVOERROR + 1;
                                    END IF;
                            END;
                            MI_NUMEROPAGOS := MI_NUMEROPAGOS + 1; 

                        END IF;

                    ELSE

                            MI_CAMPOS   := 'COMPANIA,TIPO, FACTURA, FECHA, BANCO, 
                                            ID, CONSECUTIVO, CREATED_BY, DATE_CREATED, DESCRIPCION';            

                            MI_VALORES  := ''''|| UN_COMPANIA             || ''','''
                                               || MI_TIPOFACTURA          || ''','''
                                               || MI_RECIBOPAGO           || ''','''
                                               || MI_CFECHARECAUDO        || ''','''
                                               || UN_BANCO                || ''','''
                                               || MI_IDASOBANCARIA        || ''','''
                                               || MI_CONSECUTIVOERROR        || ''','''
                                               || UN_USUARIO              || ''',SYSDATE,';

                            MI_MENSAJEERROR := 'No se encontró el cobro relacionado a la factura No.  ' || MI_RECIBOPAGO;

                            MI_VALORES := MI_VALORES || '''' || MI_MENSAJEERROR || '''';
                            BEGIN
                                BEGIN 
                                MI_RETORNO := -1;
                                    MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  'SF_ASOBANCARIA_ERROR', 
                                                                 UN_ACCION  =>  'I',
                                                                 UN_CAMPOS  =>  MI_CAMPOS, 
                                                                 UN_VALORES =>  MI_VALORES);
                                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                    RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                                END;
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
                                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                   ,UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ERRORINSERERROR);
                            END;
                            IF MI_RTA > 0 THEN
                                MI_CONSECUTIVOERROR := MI_CONSECUTIVOERROR +1;
                            END IF;
                        END IF;

                ELSE

                    MI_CAMPOS   := 'COMPANIA,TIPO, FACTURA, FECHA, BANCO, 
                                    ID, CONSECUTIVO, CREATED_BY, DATE_CREATED, DESCRIPCION';                    

                    MI_VALORES  := ''''|| UN_COMPANIA             || ''','''
                               || MI_TIPOFACTURA          || ''','''
                               || MI_RECIBOPAGO           || ''','''
                               || MI_CFECHARECAUDO        || ''','''
                               || UN_BANCO                || ''','''
                               || MI_IDASOBANCARIA        || ''','''
                               || MI_CONSECUTIVOERROR        || ''','''
                               || UN_USUARIO              || ''',SYSDATE,'; 
                    MI_MENSAJEERROR := 'No se actualizo la información de pago en la factura No. ' || MI_RECIBOPAGO;
                    MI_VALORES := MI_VALORES || '''' || MI_MENSAJEERROR || '''';
                    BEGIN
                        BEGIN 
                        MI_RETORNO := -1;
                            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  'SF_ASOBANCARIA_ERROR', 
                                                         UN_ACCION  =>  'I',
                                                         UN_CAMPOS  =>  MI_CAMPOS, 
                                                         UN_VALORES =>  MI_VALORES);
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                        END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                            ,UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ERRORINSERERROR);
                    END;
                    IF MI_RTA > 0 THEN
                        MI_CONSECUTIVOERROR := MI_CONSECUTIVOERROR +1;
                    END IF;
                END IF;      
            ELSE

                MI_CAMPOS   := 'COMPANIA,TIPO, FACTURA, FECHA, BANCO, 
                                ID, CONSECUTIVO, CREATED_BY, DATE_CREATED, DESCRIPCION'; 

                MI_VALORES  := ''''|| UN_COMPANIA             || ''','''
                               || MI_TIPOFACTURA          || ''','''
                               || MI_RECIBOPAGO           || ''','''
                               || MI_CFECHARECAUDO        || ''','''
                               || UN_BANCO                || ''','''
                               || MI_IDASOBANCARIA        || ''','''
                               || MI_CONSECUTIVOERROR        || ''','''
                               || UN_USUARIO              || ''',SYSDATE,';
                MI_MENSAJEERROR := 'La factura No. ' || MI_RECIBOPAGO || 
                                  ' no existe en el sistema ó no fue recaudado con código de barras';
                MI_VALORES := MI_VALORES || '''' || MI_MENSAJEERROR || '''';
                BEGIN
                    BEGIN 
                    MI_RETORNO := -1;
                        MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  'SF_ASOBANCARIA_ERROR', 
                                                     UN_ACCION  =>  'I',
                                                     UN_CAMPOS  =>  MI_CAMPOS, 
                                                     UN_VALORES =>  MI_VALORES);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                        ,UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ERRORINSERERROR);
                END;
                IF MI_RTA > 0 THEN
                    MI_CONSECUTIVOERROR := MI_CONSECUTIVOERROR +1;
                END IF;
            END IF; 
        END LOOP; 
        BEGIN
            BEGIN
                MI_CAMPOS := 'COMPANIA, USUARIO, FECHA, HORA, BANCO, 
                              NUMERO_PAGOS, NUMERO_ERRORES, ID, 
                              FECHARECAUDO, DATE_CREATED, CREATED_BY';
                MI_VALORES :=  '''' || UN_COMPANIA || ''',''' || UN_USUARIO || ''',
                              '''     || TO_CHAR(SYSDATE,'DD/MM/YYYY')  || 
                              ''',''' || TO_CHAR(SYSDATE, 'HH:MI')      || 
                              ''',''' || MI_CODIGOBANCO                 || 
                              ''',  ' || MI_NUMEROPAGOS                 || 
                                ','   ||  MI_CONSECUTIVOERROR           ||
                                ','   || MI_IDASOBANCARIA               || 
                                ',''' || TO_DATE(MI_CFECHARECAUDO, 'DD/MM/YYYY') || 
                                ''',SYSDATE, ''' || UN_USUARIO || '''' ;
                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA  => 'SF_ASOBANCARIA_LOG',
                                            UN_ACCION => 'I',
                                            UN_CAMPOS => MI_CAMPOS,
                                            UN_VALORES=> MI_VALORES);            
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
            END;        
       EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_SYSMANSF THEN
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD  => SQLCODE, 
                                        UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_REGISTROLOGASOBAN);
       END;          

  RETURN MI_RETORNO;
END FC_CARGARARCHIVOASOBANCARIA;



FUNCTION FC_RETORNAR_VLR_FORM
/*
    NAME              : FC_RETORNAR_VLR_FORM
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : JONATHAN ENRIQUE GUERRERO TORRES
    DATE MIGRADOR     : 09/01/2017
    TIME              : 09:04 AM
    DESCRIPTION       : FUNCION ENCARGADA DE CALCULAR DE RETORNAR EL VALOR UVT DEL ANO
    PARAMETROS        : UN_COMPANIA      => COMPANIA DE INGRESO A LA COMPANIA 
                        UN_UVT           => CALCULAR UVT


    @NAME: retornarValorAnual

  */
 (
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
  UN_CAMPO          IN VARCHAR2,
  UN_SALARIOMEN     IN PCK_SUBTIPOS.TI_LOGICO)

RETURN PCK_SUBTIPOS.TI_DOBLE AS 
MI_RTA           NUMBER(20,2);
MI_CONSULTA      PCK_SUBTIPOS.TI_STRSQL;

BEGIN
  IF UN_SALARIOMEN = -1 THEN 
      MI_CONSULTA := ' SELECT NVL('||UN_CAMPO||',0) /30  FROM ANO WHERE COMPANIA = '''||UN_COMPANIA ||'''AND NUMERO   = '||UN_ANIO;
  ELSE
      MI_CONSULTA := ' SELECT NVL('||UN_CAMPO||',0) FROM ANO WHERE COMPANIA = '''||UN_COMPANIA ||'''AND NUMERO   = '||UN_ANIO;
  END IF;
  BEGIN
    EXECUTE IMMEDIATE MI_CONSULTA INTO MI_RTA ;
   EXCEPTION WHEN NO_DATA_FOUND THEN 
    MI_RTA := 0 ;
   END ;

 RETURN MI_RTA;
END FC_RETORNAR_VLR_FORM;
--

FUNCTION FC_HALLAR_CONCEP_FORM (
/*
    NAME              : FC_HALLAR_CONCEP_FORM
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : JONATHAN ENRIQUE GUERRERO TORRES
    DATE MIGRADOR     : 09/01/2017
    TIME              : 09:04 AM
    DESCRIPTION       : FUNCION ENCARGADA DE CALCULAR DE RETORNAR EL VALOR BASE APLICANDO FORMULA
    PARAMETROS        : UN_COMPANIA      => COMPANIA DE INGRESO A LA COMPANIA 
                        UN_FORMULA       => FORMULA A HALLAR
                        UN_ANO           => ANO EN EL QUE SE ESTRA HACIENDO EL CALCULO
    @NAME: hallarConceptoFormula                    

  */
 UN_COMPANIA   IN  PCK_SUBTIPOS.TI_COMPANIA,
 UN_FORMULA    IN  VARCHAR2,
 UN_ANO        IN  PCK_SUBTIPOS.TI_ANIO
 )
RETURN PCK_SUBTIPOS.TI_ENTERO_LARGO 
AS 
MI_RTA             PCK_SUBTIPOS.TI_ENTERO_LARGO;
MI_INICIO_FORMULA  PCK_SUBTIPOS.TI_ENTERO;
MI_FORMULA_CHAR    VARCHAR2(3000 CHAR);
MI_FORMULA_DIG     VARCHAR2(3000 CHAR);
MI_UVT             PCK_SUBTIPOS.TI_ENTERO_LARGO;

MI_PRUEBA         VARCHAR2(32000 CHAR);
BEGIN

  MI_FORMULA_CHAR := REPLACE( UPPER (UN_FORMULA),'UVT()','PCK_FACT_GENERAL_COM2.FC_RETORNAR_VLR_FORM('''||UN_COMPANIA||''','||UN_ANO||',''VALORUVT'',0)');  
  MI_FORMULA_CHAR := REPLACE( UPPER (MI_FORMULA_CHAR),'SALARIOMENSUAL()','PCK_FACT_GENERAL_COM2.FC_RETORNAR_VLR_FORM('''||UN_COMPANIA||''','||UN_ANO||',''SALARIOMINIMO'',0)');  
  MI_FORMULA_CHAR := REPLACE( UPPER (MI_FORMULA_CHAR),'SALARIODIARIO()','PCK_FACT_GENERAL_COM2.FC_RETORNAR_VLR_FORM('''||UN_COMPANIA||''','||UN_ANO||',''SALARIOMINIMO'',-1)');  



  MI_RTA := TO_NUMBER(PCK_SYSMAN_UTL.FC_EVAL(MI_FORMULA_CHAR));



  RETURN MI_RTA;
END FC_HALLAR_CONCEP_FORM;
/*
FUNCTION FC_PARCORABASTOS

    NAME              : FC_PARCORABASTOS
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : HENRRY PUERTO
    DATE MIGRADOR     : 02/10/2018
    TIME              : 09:04 AM
    DESCRIPTION       : 
    @NAME: parCorabastos                    

(
 UN_COMPANIA         IN VARCHAR2 DEFAULT '001',
 UN_NOMBRE           IN VARCHAR2
) RETURN VARCHAR2 AS
MI_VALOR  VARCHAR2(254);
BEGIN 
   BEGIN
    SELECT VALOR
    INTO MI_VALOR
    FROM   PARAMETRO
    WHERE  COMPANIA=UN_COMPANIA
    AND NOMBRE=UN_NOMBRE;
  EXCEPTION WHEN NO_DATA_FOUND THEN
    MI_VALOR:='';
  END;
  RETURN MI_VALOR;
END FC_PARCORABASTOS;

*/
/*
PROCEDURE PR_CALCULO_FACTURACION_ABASTOS

    NAME              : PR_CALCULO_FACTURACION_ABASTOS
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : HENRRY PUERTO
    DATE MIGRADOR     : 02/10/2018
    TIME              : 09:04 AM
    DESCRIPTION       : 
    @NAME: calculoFacturacionCorabastos                    
    @METHOD: POST
(
 UN_COMPANIA         IN VARCHAR2 DEFAULT '001',
 UN_ANOFACTURAR      IN NUMBER   DEFAULT 2017,
 UN_MESFACTURAR      IN NUMBER   DEFAULT 9,
 UN_TIPOFACTURA      IN VARCHAR2 DEFAULT 'TDV',
 UN_FECHAFACTURACION IN DATE,
 UN_FECHALIMITE      IN DATE,
 UN_FECHALIMITE1     IN DATE,
 UN_CCI              IN VARCHAR2,
 UN_CCF              IN VARCHAR2,
 UN_DESCRIPCION      IN VARCHAR2,
 UN_STRUSUARIO       IN VARCHAR2,
 UN_FACTURACIONTOTAL IN NUMBER DEFAULT 0
 )
 AS

-- PROGRAM: CREACION DE TABLAS TEMPORALES PARA IMPORTAR PLANOS DE SERVICIOS PUBLICOS ORACLE
-- DATE: JULIO 12 DE 2017
-- AUTHOR: HENRY PUERTO V.

--     CREATE GLOBAL TEMPORARY TABLE PLANOSDIGITAL_WEB_TMP  ON COMMIT PRESERVE ROWS
--       AS
--       SELECT * 
--       FROM PLANOSDISGITAL_WEB 
--     WHERE NSESSION = -1;
--     CREATE GLOBAL TEMPORARY TABLE CONCEPTOSPLANO_TMP ON COMMIT PRESERVE ROWS AS 
--     SELECT * 
--     FROM CONCEPTOSPLANO
--     WHERE NSESSION = -1;


-- PROGRAM: NUEVA VERSION DE CALCULO DE FACTURACION DE CORABATOS
-- AUTHOR : HENRY DE J PUERTO V
-- DATE   : JUL 25 DE 2017
-- REVISADA EN OCT 1 DE 2018
-- ORIGINAL EN VBA
-- Factura por lotes todos los contratos que esten vigentes
-- Parámetros:Tipo de comprobante de factura, fecha de facturación, fecha limite de pago, centro de costo afacturar, descripción de la factura
-- Por cada contrato trae, los conceptos a cobrar y las SF_NOVEDADES para el mes a facturar
-- revisada por hpv en 14 de abril de 2016 
 UN_SALIDA                  VARCHAR2(32000);
MI_CRECARGO                    VARCHAR2(4);
MI_DIASINC                     NUMBER;
MI_PORINTERES                  NUMBER;
MI_STRINTERESSV                VARCHAR(2);
MI_CONCEPTODEUDA               VARCHAR(4);
MI_CONCEPTOINTERES             VARCHAR(4);
MI_CONINTERESACUMULADO         VARCHAR(4);
MI_CONCEPTOACUERDO             VARCHAR(4);       
MI_CONCEPTOACUERDODEUDA        VARCHAR(4);       
MI_CONCEPTOFINANCIACION        VARCHAR(4);       
MI_CCPTOCREE                   VARCHAR(4);       
MI_CONCEPTORETEIVA             VARCHAR(4);       
MI_CONCEPTORETEICA             VARCHAR(4);       
MI_CONSECTIPOFAC               VARCHAR(4);       
MI_CONSECINIFACTURA            VARCHAR(4);     
MI_CONCEPTOSBASERETENCION      VARCHAR(255);     
MI_CONCEPTOINTERESFINANCIACION VARCHAR(4);
MI_CUENTA                      VARCHAR(255);                   
MI_TASAFIN                     NUMBER;               
MI_CALCULA_INTERES_DIAS        VARCHAR(4);
MI_STRPERCORTE                 VARCHAR(12);
MI_ANOCORTEN                   NUMBER;
MI_MESCORTEN                   NUMBER;

MI_DEUDA001                    NUMBER(20,2):=0;
MI_DEUDA037                    NUMBER(20,2):=0;


MI_CCI              VARCHAR2(20);
MI_CCF              VARCHAR2(20);

MI_SESSION          NUMBER        :=0;
MI_TI               DATE          :=SYSDATE;
MI_TF               DATE          :=SYSDATE;
MI_ETAPA            VARCHAR2(512) :='ETAPA 0. INICIO';
MI_RS               NUMBER;
MI_RUTA             VARCHAR2(3000):='INFORME_FACTURACION_'||TO_CHAR(UN_ANOFACTURAR)||'_'||TO_CHAR(UN_MESFACTURAR)||'_'||TO_CHAR(SYSDATE,'RRRR_MM_DD_HH_MI_SS')||'.TXT';
MI_SSQL             VARCHAR2(32000);
MI_NE               NUMBER:=0;
MI_ULTIMO_DIA       DATE;
MI_MENSAJE          VARCHAR2(256):='CALCULO DE FACTURACION VERSION 2017 RELEASE  1.10';
MI_USUARIO          VARCHAR2(50):=UN_STRUSUARIO;
MI_PAGADO_EN        VARCHAR2(60);
MI_NOMBRECONCEPTO   VARCHAR2(60);
MI_MESANTERIOR      NUMBER;
MI_ANOANTERIOR      NUMBER;
MI_FECHACARTERA     DATE;
MI_TASAINTERES      NUMBER;
MI_PORIVAINTERES    NUMBER;
MI_NUMEROINI        NUMBER;
MI_CONSECUTIVO      NUMBER;
MI_NUMERO           NUMBER;
MI_NUMERO_FACTURA   NUMBER;
MI_VALOR_RETEICA    NUMBER;
MI_PRETEICA         NUMBER;
MI_VALOR_RETEIVA    NUMBER;
MI_PORRETEIVA       NUMBER;
MI_PORCIVA          NUMBER;
MI_PORRETEICA       NUMBER;
MI_I                NUMBER;
MI_IND_FACTOTAL     NUMBER:=0;
MI_IND_FACTURADO    NUMBER:=0;

MI_CONCEPTOINTERESACUERDO VARCHAR2(128):=FC_PARCORABASTOS(UN_COMPANIA,'CONCEPTO INTERES CUOTA DEUDA');

BEGIN
    MI_ETAPA:='1' ;   
    SELECT SYS_CONTEXT('USERENV','SESSIONID') 
    INTO MI_SESSION
    FROM DUAL;
    MI_CCI:=UN_CCI;
    MI_CCF:=UN_CCF;
    IF MI_CCI=MI_CCF THEN
       MI_IND_FACTOTAL:=1;
    END IF;
-- SI LA FACTURACION YA ESTA IMPRES NO PERMITE EL CALCULO
--   BEGIN
--        SELECT DISTINCT 1
--        INTO    MI_IND_FACTURADO
--        FROM    FACTURA_CONTRATO
--        WHERE   COMPANIA= UN_COMPANIA
--          AND   ANO     = UN_ANOFACTURAR 
--         AND   MES     = UN_MESFACTURAR
--          AND   TIPO    = UN_TIPOFACTURA
--          AND   INMUEBLEH BETWEEN MI_CCI AND MI_CCF
--          AND   IMPRESA NOT IN (0);
--    EXCEPTION WHEN NO_DATA_FOUND THEN
--       MI_IND_FACTURADO:=0;
--    END;
--    IF MI_IND_FACTURADO<>0 THEN
--        RETURN 'YA HAY FACTURAS IMPRESAS NO SE PERMITE RECALCULAR'; 
--    END IF;


    DBMS_OUTPUT.PUT_LINE(MI_MENSAJE);
    DBMS_OUTPUT.PUT_LINE('FECHA Y HORA INICIO '||TO_CHAR(MI_TI,'DD/MM/RRRR HH:MI:SS'));
    DBMS_OUTPUT.PUT_LINE('');    

    MI_CRECARGO                    := NVL(FC_PARCORABASTOS(UN_COMPANIA,'CONCEPTO RECARGO MORA FACTURACION SEGUNDO VENCIMIENTO'), '999');
    MI_DIASINC                     := NVL(TO_NUMBER(FC_PARCORABASTOS(UN_COMPANIA,'DIAS MORA PARA DEUDA INCOBRABLE'),'9999999.99999'), 90); -- ' tasa de recargo
    MI_PORINTERES                  := NVL(TO_NUMBER(FC_PARCORABASTOS(UN_COMPANIA,'TASA INTERES SEGUNDO VENCIMIENTO'),'9999999.99999'), 0);
    MI_NOMBRECONCEPTO              := NVL(FC_PARCORABASTOS(UN_COMPANIA,'NOMBRE CONCEPTO RECARGO MORA FACTURACION SEGUNDO VENCIMIENTO'), 'NO ASIGNADO');
    MI_STRINTERESSV                := NVL(FC_PARCORABASTOS(UN_COMPANIA,'CALCULA RECARGO MORA SOBRE FECHA DE RECAUDO'), 'NO');--' si no lo hace sobre la fecha de la factura
    MI_CONCEPTODEUDA               := NVL(FC_PARCORABASTOS(UN_COMPANIA,'CONCEPTO DEUDA ANTERIOR'),'001');--'Concepto deuda anterior
    MI_CONCEPTOINTERES             := NVL(FC_PARCORABASTOS(UN_COMPANIA,'CONCEPTO INTERES'),'005');
    MI_CONINTERESACUMULADO         := NVL(FC_PARCORABASTOS(UN_COMPANIA,'CONCEPTO INTERES ACUMULADO'),'037');--'CONCEPTO ACUMILADO DE INTERES.
    MI_CONCEPTOACUERDO             := NVL(FC_PARCORABASTOS(UN_COMPANIA,'CONCEPTO CUOTA DEUDA ACUERDO'),'146');
    MI_CONCEPTOACUERDODEUDA        := NVL(FC_PARCORABASTOS(UN_COMPANIA,'CONCEPTO CUOTA DIFERIDA'),'146');
    MI_CONCEPTOFINANCIACION        := NVL(FC_PARCORABASTOS(UN_COMPANIA,'CONCEPTO CUOTA FINANCIACION'),'146');
    MI_CCPTOCREE                   :=     FC_PARCORABASTOS(UN_COMPANIA,'CONCEPTO PARA RETENCION CREE');
    MI_CONCEPTORETEIVA             :=     FC_PARCORABASTOS(UN_COMPANIA,'CONCEPTO PARA RETENCION IVA');
    MI_CONCEPTORETEICA             :=     FC_PARCORABASTOS(UN_COMPANIA,'CONCEPTO PARA RETENCION ICA');
    MI_CONSECTIPOFAC               := NVL(FC_PARCORABASTOS(UN_COMPANIA,'MANEJA CONSECUTIVO DE FACTURA POR TIPO'),'NO');
    MI_CONSECINIFACTURA            := NVL(FC_PARCORABASTOS(UN_COMPANIA,'MANEJA CODIGO MANUAL PARA CONSECUTIVO INICIAL FACTURACION'),'NO');
    MI_CONCEPTOSBASERETENCION      := NVL(FC_PARCORABASTOS(UN_COMPANIA,'CONCEPTOS BASE PARA RETENCION'),'');
    MI_CONCEPTOSBASERETENCION      := CHR(39)||REPLACE(MI_CONCEPTOSBASERETENCION, ',',CHR(39)||','||CHR(39))||CHR(39);
    MI_CONCEPTOINTERESFINANCIACION := NVL(FC_PARCORABASTOS(UN_COMPANIA,'CONCEPTO PARA INTERES FINANCIACION EN CONTRA'),'');
    MI_CUENTA                      := NVL(FC_PARCORABASTOS(UN_COMPANIA,'CUENTA PARA NOTA CREDITO SALDO A FAVOR USUARIO'),'');
    MI_TASAFIN                     := NVL(TO_NUMBER(FC_PARCORABASTOS(UN_COMPANIA,'TASA INTERES PARA SALDO A FAVOR PRESTAMO'),'9999999.99999'),0);
    MI_CALCULA_INTERES_DIAS        := NVL(FC_PARCORABASTOS(UN_COMPANIA,'CALCULA INTERES POR DIAS'),'');


     MI_MESANTERIOR := CASE WHEN UN_MESFACTURAR = 1 THEN 12 ELSE UN_MESFACTURAR - 1 END;
     MI_ANOANTERIOR := CASE WHEN UN_MESFACTURAR = 1 THEN UN_ANOFACTURAR - 1 ELSE UN_ANOFACTURAR END ;
     MI_FECHACARTERA:= LAST_DAY(TO_DATE('01/' || UN_MESFACTURAR || '/' || UN_ANOFACTURAR,'DD/MM/YYYY'));

     MI_STRPERCORTE:=NVL(FC_PARCORABASTOS(UN_COMPANIA,'CONFIGURAR PERIODO DE CORTE NUEVA FACTURACION'), '201809');

     MI_ANOCORTEN:=TO_NUMBER(SUBSTR(MI_STRPERCORTE,1,4));
     MI_MESCORTEN :=TO_NUMBER(SUBSTR(MI_STRPERCORTE,5,2));

      DBMS_OUTPUT.PUT_LINE('AÑO ANTERIOR '||MI_ANOANTERIOR);
      DBMS_OUTPUT.PUT_LINE('MES ANTERIOR '||MI_MESANTERIOR);
      DBMS_OUTPUT.PUT_LINE('AÑO CORTE '||MI_ANOCORTEN);
      DBMS_OUTPUT.PUT_LINE('MES CORTE '||MI_MESCORTEN);


  BEGIN
        DELETE DETALLE_FACTURA_CONTRATO 
       WHERE  COMPANIA    =UN_COMPANIA 
          AND ANO         =UN_ANOFACTURAR 
          AND MES         =UN_MESFACTURAR
          AND TIPO_FACTURA=UN_TIPOFACTURA
          AND (NUMERO_FACTURA) IN (
                                    SELECT F.NUMERO 
                                    FROM   FACTURA_CONTRATO F  
                                    WHERE  COMPANIA=UN_COMPANIA
                                      AND  ANO     =UN_ANOFACTURAR
                                      AND  MES     =UN_MESFACTURAR
                                      AND  TIPO    =UN_TIPOFACTURA
                                      AND  INMUEBLEH BETWEEN MI_CCI AND MI_CCF 
                                   );
           COMMIT;            
          DELETE  
          FROM FACTURA_CONTRATO
          WHERE COMPANIA = UN_COMPANIA
            AND  ANO     = UN_ANOFACTURAR 
            AND  MES     = UN_MESFACTURAR
            AND  TIPO    = UN_TIPOFACTURA
            AND  INMUEBLEH BETWEEN MI_CCI AND MI_CCF;

     COMMIT;            

    EXCEPTION WHEN OTHERS THEN
      DBMS_OUTPUT.PUT_LINE('SE PRESENTO EL ERRROR AL ELIMINAR '||MI_RUTA ||' -> '|| SQLERRM);
    END;  

     -- Cerrar las facturas del periodo anterior

    UPDATE FACTURA_CONTRATO 
    SET    ESTADO_FACT = 'CE' 
    WHERE   FACTURA_CONTRATO.COMPANIA = UN_COMPANIA
      AND   FACTURA_CONTRATO.ANO      = MI_ANOANTERIOR
      AND   FACTURA_CONTRATO.MES      = MI_MESANTERIOR
      AND   INMUEBLEH BETWEEN MI_CCI  AND MI_CCF
      AND   FACTURA_CONTRATO.TIPO     = UN_TIPOFACTURA
      AND   FACTURA_CONTRATO.ESTADO_FACT NOT IN ('AN','CE');
COMMIT; 
     --INSERTA CONTRATOS ACTIVOS
       IF MI_CONSECTIPOFAC = 'SI' THEN
          SELECT NVL(MAX(NUMERO),0)+1 
          INTO   MI_NUMERO_FACTURA 
          FROM   FACTURA_CONTRATO 
          WHERE  COMPANIA=UN_COMPANIA 
            AND  TIPO=UN_TIPOFACTURA;
       ELSE
           SELECT NVL(MAX(NUMERO),0)+1 
           INTO   MI_NUMERO_FACTURA 
           FROM   FACTURA_CONTRATO 
           WHERE   COMPANIA=UN_COMPANIA;
       END IF;

--    'Inserta  los contratos activos que se van a facturar.
--    ' Nb.Se ajusto  este insert ya que al momento de crear la factura se debe insertar el regimen del contrato 
--    ' De la tabla  tercero

     INSERT INTO FACTURA_CONTRATO(COMPANIA, 
                                  ANO, 
                                  MES, 
                                  TIPO, 
                                  NUMERO, 
                                  CONTRATO, 
                                  FECHA, 
                                  FECHA_LIMITE_PAGO, 
                                  DESCRIPCION, 
                                  VALOR_BASE, 
                                  VALOR_IVA,
                                  VALOR_RTFTE,
                                  TERCERO, 
                                  SUCURSAL, 
                                  CREATED_BY,  
                                  DATE_CREATED, 
                                  IMPRESA, 
                                  CENTRO_COSTO, 
                                  FECHA_SEGUNDO_VENCIMIENTO, 
                                  ESTADO_FACT, 
                                  ANOCORTE, 
                                  MESCORTE,
                                  ESTADO_CONTRATO,
                                  REGIMEN_TERCERO,
                                  REGIMEN_CONTRATO,
                                  INMUEBLEH
                  )
     SELECT   R1.COMPANIA,
              R1.ANO ,
              R1.MES,
              R1.TIPOFACTURA,
              R1.NUMEROFACTURA,
              R1.CODIGO,
              R1.FECHA,
              R1.FECHA_LIMITE_PAGO, 
              R1.DESCRIPCION,
              R1.VALOR_BASE,
              R1.VALOR_IVA,
              R1.VALOR_RTFTE,              
              R1.TERCERO, 
              R1.SUCURSAL,
              R1.CREATED_BY,
              R1.DATE_CREATED,
              R1.IMPRESA,
              R1.CENTROCOSTO, 
              R1.FECHA_SEGUNDO_VENCIMIENTO,
              R1.ESTADO_FACT,
              R1.ANOCORTE,
              R1.MESCORTE,
              R1.ESTADO, 
              R1.REGIMEN_CONTRATO,
              R1.REGIMEN_TERCERO,
              R1.INMUEBLE
      FROM        
          (
             SELECT R5.*, 
                    MI_NUMERO_FACTURA+ROWNUM NUMEROFACTURA
             FROM 
             (
                SELECT DISTINCT CONTRATOS.COMPANIA,
                             UN_ANOFACTURAR ANO,
                             UN_MESFACTURAR MES,
                             UN_TIPOFACTURA TIPOFACTURA,
                             CONTRATOS.CODIGO,
                             UN_FECHAFACTURACION FECHA,
                             UN_FECHALIMITE FECHA_LIMITE_PAGO, 
                             UN_DESCRIPCION DESCRIPCION,
                             0 VALOR_BASE,    
                             0 VALOR_IVA,
                             0 VALOR_RTFTE,
                             CONTRATOS.TERCERO, 
                             CONTRATOS.SUCURSAL,
                             UN_STRUSUARIO CREATED_BY,
                             SYSDATE DATE_CREATED,
                             -1 IMPRESA,
                             INMUEBLES.CENTRO_COSTO CENTROCOSTO, 
                             UN_FECHALIMITE1 FECHA_SEGUNDO_VENCIMIENTO,
                             'AC' ESTADO_FACT,
                             0  ANOCORTE,
                             0  MESCORTE,
                             CONTRATOS.ESTADO,
                             CONTRATOS.REGIMEN REGIMEN_CONTRATO,
                             TERCERO.REGIMEN REGIMEN_TERCERO,
                             CONTRATOS.INMUEBLE
                    FROM    ((CONCEPTOS_CONTRATOS INNER JOIN CONTRATOS ON (CONCEPTOS_CONTRATOS.COMPANIA = CONTRATOS.COMPANIA) 
                             AND   (CONCEPTOS_CONTRATOS.CONTRATO = CONTRATOS.CODIGO)) INNER JOIN INMUEBLES ON CONTRATOS.INMUEBLE = INMUEBLES.DENOMINACION) LEFT JOIN TERCERO ON (CONTRATOS.COMPANIA = TERCERO.COMPANIA) 
                             AND   (CONTRATOS.TERCERO = TERCERO.NIT) AND (CONTRATOS.SUCURSAL = TERCERO.SUCURSAL)
                    WHERE   CONCEPTOS_CONTRATOS.COMPANIA=UN_COMPANIA 
                      AND   CONCEPTOS_CONTRATOS.CONTRATO<>9999999999 
                      AND   CONTRATOS.INMUEBLE BETWEEN MI_CCI AND MI_CCF 
                      AND   CONTRATOS.ESTADO IN (2)
                      AND   CONTRATOS.FECHA_INICIO<=UN_FECHAFACTURACION
                      AND   CONCEPTOS_CONTRATOS.ANO_CANON = UN_ANOFACTURAR 
            ) R5
          ) R1 LEFT JOIN 
          (
              SELECT DISTINCT FACTURA_CONTRATO.COMPANIA,
                              FACTURA_CONTRATO.CONTRATO,
                              FACTURA_CONTRATO.NUMERO,
                              FECHA_LIMITE_PAGO,
                              CENTRO_COSTO,
                              TERCERO,
                              SUCURSAL,
                              ANOCORTE,
                              MESCORTE,
                              ESTADO_FACT,
                              ESTADO
              FROM    FACTURA_CONTRATO 
              WHERE   FACTURA_CONTRATO.COMPANIA= UN_COMPANIA
                AND   FACTURA_CONTRATO.ANO     = UN_ANOFACTURAR 
                AND   FACTURA_CONTRATO.MES     = UN_MESFACTURAR
                AND   FACTURA_CONTRATO.TIPO    = UN_TIPOFACTURA
          ) R2 ON (R1.COMPANIA=R2.COMPANIA) AND (R1.CODIGO = R2.CONTRATO)  
      WHERE    R2.CONTRATO IS NULL ;

      MI_NUMERO_FACTURA:= MI_NUMERO_FACTURA+SQL%ROWCOUNT;
 COMMIT;      
   -- 'Consulta los contratos que tienen novedad  en el mes

   INSERT INTO FACTURA_CONTRATO(COMPANIA,
                                ANO, 
                                MES, 
                                TIPO, 
                                NUMERO, 
                                CONTRATO, 
                                FECHA, 
                                FECHA_LIMITE_PAGO,
                                DESCRIPCION, 
                                VALOR_BASE, 
                                VALOR_IVA,
                                VALOR_RTFTE,
                                TERCERO, 
                                SUCURSAL, 
                                CREATED_BY, 
                                DATE_CREATED, 
                                IMPRESA, 
                                CENTRO_COSTO, 
                                FECHA_SEGUNDO_VENCIMIENTO, 
                                ESTADO_FACT, 
                                ANOCORTE, 
                                MESCORTE,
                                ESTADO_CONTRATO
                                )
           SELECT   R1.COMPANIA,
                    R1.ANO,
                    R1.MES,
                    R1.TIPOFACTURA,
                    R1.NUMEROFACTURA, 
                    R1.CONTRATO,
                    R1.FECHA,
                    R1.FECHA_LIMITE_PAGO, 
                    R1.DESCRIPCION,
                    R1.VALOR_BASE,
                    R1.VALOR_IVA,
                    R1.VALOR_RTFTE,
                    R1.TERCERO, 
                    R1.SUCURSAL,
                    R1.CREATED_BY,
                    R1.DATE_CREATED,
                    R1.IMPRESA,
                    R1.CENTROCOSTO, 
                    R1.FECHA_SEGUNDO_VENCIMIENTO,
                    R1.ESTADO_FACT,
                    R1.ANOCORTE,
                    R1.MESCORTE,
                    R1.ESTADO 
           FROM     
                 (
              SELECT R5.*, 
                    MI_NUMERO_FACTURA+ROWNUM NUMEROFACTURA
              FROM 
              (
                    SELECT   DISTINCT R3.COMPANIA,
                             UN_ANOFACTURAR ANO,
                             UN_MESFACTURAR MES,
                             UN_TIPOFACTURA TIPOFACTURA,
                             R3.CONTRATO,
                             UN_FECHAFACTURACION FECHA,
                             UN_FECHALIMITE FECHA_LIMITE_PAGO,               
                             UN_DESCRIPCION DESCRIPCION,
                             0 VALOR_BASE,
                             0 VALOR_IVA,
                             0 VALOR_RTFTE,
                             R3.TERCERO, 
                             R3.SUCURSAL,
                             UN_STRUSUARIO CREATED_BY,
                             SYSDATE DATE_CREATED,
                             -1 IMPRESA,
                             R3.CENTRO_COSTO CENTROCOSTO,
                             UN_FECHALIMITE1 FECHA_SEGUNDO_VENCIMIENTO,
                             'AC' ESTADO_FACT,
                             0    ANOCORTE,
                             0    MESCORTE,
                             R3.ESTADO 
                    FROM    
                        (
                           SELECT DISTINCT N.COMPANIA,
                                           N.CONTRATO,
                                           CONTRATOS.TERCERO,
                                           CONTRATOS.SUCURSAL,
                                           INMUEBLES.CENTRO_COSTO,
                                           CONTRATOS.ESTADO 
                           FROM         (SF_NOVEDADES N INNER JOIN CONTRATOS ON (N.COMPANIA=CONTRATOS.COMPANIA) 
                             AND       (N.CONTRATO=CONTRATOS.CODIGO)) 
                           INNER JOIN  INMUEBLES 
                             ON        (CONTRATOS.INMUEBLE = INMUEBLES.DENOMINACION) 
                           WHERE       N.COMPANIA=UN_COMPANIA
                             AND       N.ANO     =UN_ANOFACTURAR
                             AND       N.MES     =UN_MESFACTURAR
                             AND       N.CONTRATO<>9999999999
                             AND       CONTRATOS.ESTADO  IN (2)
                             AND       INMUEBLES.DENOMINACION BETWEEN MI_CCI AND MI_CCF
                          )  R3 LEFT JOIN 
                          (
                               SELECT DISTINCT COMPANIA,
                                               CONTRATO,
                                               NUMERO,
                                               FECHA_LIMITE_PAGO,
                                               CENTRO_COSTO,
                                               TERCERO,
                                               SUCURSAL,
                                               ANOCORTE,
                                               MESCORTE,
                                               ESTADO_FACT,
                                               ESTADO 
                               FROM    FACTURA_CONTRATO  
                               WHERE   COMPANIA=UN_COMPANIA 
                                 AND   ANO     =UN_ANOFACTURAR 
                                 AND   MES     =UN_MESFACTURAR 
                                 AND   TIPO    =UN_TIPOFACTURA
                          ) R2 ON (R3.COMPANIA=R2.COMPANIA) AND (R3.CONTRATO = R2.CONTRATO) 
                    WHERE   R2.CONTRATO IS NULL
           ) R5  ) R1 LEFT JOIN 
                        ( SELECT DISTINCT COMPANIA,
                                          CONTRATO,
                                          NUMERO,
                                          FECHA_LIMITE_PAGO,
                                          CENTRO_COSTO,
                                          TERCERO,
                                          SUCURSAL,
                                          ANOCORTE,
                                          MESCORTE,
                                          ESTADO_FACT,
                                          ESTADO                            
                               FROM    FACTURA_CONTRATO  
                               WHERE   COMPANIA=UN_COMPANIA 
                                 AND   ANO     =UN_ANOFACTURAR 
                                 AND   MES     =UN_MESFACTURAR 
                                 AND   TIPO    =UN_TIPOFACTURA
                        ) R2 ON (R1.COMPANIA=R2.COMPANIA) AND (R1.CONTRATO = R2.CONTRATO) 
           WHERE R2.CONTRATO IS NULL;

           MI_NUMERO_FACTURA:= MI_NUMERO_FACTURA+SQL%ROWCOUNT;
 COMMIT;
--    AND (FACTUTA IN (;
-- INSERTAMOS CONCEPTO - 001 Y CONCEPTO 037 ES DECIR DEUDA DE CAPITAL Y DEUDA DE INTERESES
-- SE REQUIERE CREAR UN INDICE EN FACTURA_CONTRATO CON LA LLAVE COMPANIA,CONTRATO,ANO,MES,TIPO_FACTURA,FACTURA para sysman ya fue creada
   BEGIN
      UPDATE FACTURA_CONTRATO FC
      SET    FC.DEUDAACUMULADO001=
               NVL((
                    SELECT SUM(CASE WHEN C.TIPO_CONCEPTO NOT IN ('DI','IN') THEN
                            (D.VALOR_BASE      -D.RECAUDO_BASE      -D.ABONO_BASE  -D.CREDITO_BASE +D.DEUDA_BASE  -D.RECAUDODEUDA_BASE -D.CREDITODEUDA_BASE -D.ABONODEUDA_BASE)
                                     +(D.VALOR_IVA       -D.RECAUDO_IVA       -D.ABONO_IVA   -D.CREDITO_IVA  +D.DEUDA_IVA   -D.RECAUDODEUDA_IVA  -D.CREDITODEUDA_IVA  -D.ABONODEUDA_IVA )
                                     -(D.VALOR_RETEFUENTE-D.RECAUDO_RETE      -D.ABONO_RETE  -D.CREDITO_RETE +D.DEUDA_RETE  -D.RECAUDODEUDA_RETE -D.CREDITODEUDA_RETE -D.ABONODEUDA_RETE)
                               ELSE 0 END) DEUDA001
                    FROM   (DETALLE_FACTURA_CONTRATO D INNER JOIN CONCEPTOFACTURACION C ON (D.COMPANIA=C.COMPANIA) AND (D.CONCEPTO=C.CODIGO)) 
                    WHERE  D.COMPANIA    =UN_COMPANIA 
                       AND D.ANO         BETWEEN MI_ANOCORTEN AND MI_ANOANTERIOR
                       AND D.MES         BETWEEN CASE WHEN D.ANO=MI_ANOCORTEN THEN MI_MESCORTEN ELSE 1 END AND CASE WHEN D.ANO=MI_ANOANTERIOR THEN MI_MESANTERIOR ELSE 12 END
                       AND D.TIPO_FACTURA=UN_TIPOFACTURA 
                       AND D.CONTRATO    =FC.CONTRATO
                       AND D.ESTADO_FACT NOT IN('AN') 
                       AND D.ESTADO      NOT IN('N')
                       AND C.TIPO_CONCEPTO NOT IN ('DC') -- NO INCLUYE LA DEUDA
                   ),0),
                  FC.DEUDAINTERES=NVL((
                  SELECT 
                          SUM(CASE WHEN C.TIPO_CONCEPTO IN ('DI','IN') THEN
                         (D.VALOR_BASE) +(D.VALOR_IVA)-(D.VALOR_RETEFUENTE)-(D.VLR_ACUERDO)
                                ELSE 0 END ) DEUDA037
                    FROM   (DETALLE_FACTURA_CONTRATO D INNER JOIN CONCEPTOFACTURACION C ON (D.COMPANIA=C.COMPANIA) AND (D.CONCEPTO=C.CODIGO)) 
                    WHERE  D.COMPANIA    =UN_COMPANIA 
                       AND D.ANO         BETWEEN MI_ANOCORTEN AND MI_ANOANTERIOR
                       AND D.MES         BETWEEN CASE WHEN D.ANO=MI_ANOCORTEN THEN MI_MESCORTEN ELSE 1 END AND CASE WHEN D.ANO=MI_ANOANTERIOR THEN MI_MESANTERIOR ELSE 12 END
                       AND D.TIPO_FACTURA=UN_TIPOFACTURA 
                       AND D.CONTRATO    =FC.CONTRATO
                       AND D.ESTADO_FACT NOT IN('AN')
                       AND D.ESTADO      NOT IN('N')
                       AND C.TIPO_CONCEPTO NOT IN ('DC') -- NO INCLUYE LA DEUDA
                   ),0)
       WHERE COMPANIA=UN_COMPANIA
         AND ANO     =UN_ANOFACTURAR
         AND MES     =UN_MESFACTURAR
         AND TIPO    =UN_TIPOFACTURA
         AND INMUEBLEH BETWEEN MI_CCI AND MI_CCF ;
    COMMIT;
    EXCEPTION WHEN OTHERS THEN
      DBMS_OUTPUT.PUT_LINE('ERROR EN LA ACTUALIZACION DE DEUDAS '||SQLERRM);
    END;
--'INSERTA CONCEPTOS DE INTERESES
--                GROUP BY  UN_COMPANIA,
--                            UN_ANOFACTURAR,
--                            UN_MESFACTURAR,
--                              UN_TIPOFACTURA,
--                              R2.NUMERO,
--                              MI_USUARIO,
--                              SYSDATE,
--                              R2.CENTRO_COSTO,
--                              R2.CONTRATO,
--                              R2.ESTADO_FACT,
--                              R2.ESTADO

   BEGIN
       SELECT TASADEINTERES,
              PORCIVA
       INTO
              MI_TASAINTERES,
              MI_PORIVAINTERES
       FROM   CONCEPTOFACTURACION 
       WHERE  COMPANIA=UN_COMPANIA 
         AND  CODIGO=MI_CONCEPTOINTERES ;
   EXCEPTION WHEN NO_DATA_FOUND THEN
              MI_TASAINTERES:=0;
              MI_PORIVAINTERES:=0;
   END;



   INSERT INTO DETALLE_FACTURA_CONTRATO(
               COMPANIA,
               ANO, 
               MES, 
               TIPO_FACTURA,
               NUMERO_FACTURA, 
               CONCEPTO, 
               CREATED_BY, 
               DATE_CREATED,
               CENTRO_COSTO, 
               CONTRATO, 
               ESTADO_FACT,
               ESTADO,
               VALOR_BASE, 
               VALOR_IVA, 
               VALOR_RETEFUENTE,
               VALOR_INTERES_INC,
               DEUDA_INTERES_INC,
               DEUDA_BASE,
               DEUDA_IVA,
               DEUDA_RETE)

           SELECT UN_COMPANIA,
                  UN_ANOFACTURAR,
                  UN_MESFACTURAR,
                  UN_TIPOFACTURA,
                  R2.NUMERO,
                  MI_CONCEPTOINTERES,
                  MI_USUARIO,
                  SYSDATE,
                  R2.CENTRO_COSTO,
                  R2.CONTRATO,
                  R2.ESTADO_FACT,
                  R2.ESTADO,
                  (CASE WHEN MONTHS_BETWEEN(UN_FECHAFACTURACION,FA.FECHA) <=3 THEN ROUND(R2.DEUDAACUMULADO001*ROUND(MONTHS_BETWEEN(UN_FECHAFACTURACION,FA.FECHA),0)*MI_TASAINTERES / 100,0) ELSE 0 END) VALOR_BASE,
                  0,
                  0, 
                  (CASE WHEN MONTHS_BETWEEN(UN_FECHAFACTURACION,FA.FECHA) >3 THEN ROUND(R2.DEUDAACUMULADO001*ROUND(MONTHS_BETWEEN(UN_FECHAFACTURACION,FA.FECHA),0)*MI_TASAINTERES / 100,0) ELSE 0 END) VALOR_BASE,
                  0,
                  0,
                  0,
                  0
           FROM DETALLE_FACTURA_CONTRATO D INNER JOIN CONCEPTOFACTURACION  C ON (D.COMPANIA=C.COMPANIA) AND (D.CONCEPTO=C.CODIGO)
           INNER JOIN
            (
                SELECT DISTINCT COMPANIA,ANO,MES,TIPO,NUMERO,FECHA
                FROM            FACTURA_CONTRATO  
                WHERE           INMUEBLEH BETWEEN MI_CCI AND MI_CCF 

            ) FA   ON (D.COMPANIA=FA.COMPANIA) AND (D.ANO=FA.ANO) AND (D.MES=FA.MES) AND (D.TIPO_FACTURA=FA.TIPO) AND (D.NUMERO_FACTURA=FA.NUMERO)          
            INNER JOIN
            (
                SELECT DISTINCT COMPANIA,
                                CONTRATO,
                                ANO,
                                MES,
                                TIPO,
                                NUMERO,
                                FECHA,
                                FECHA_LIMITE_PAGO,
                                CENTRO_COSTO,
                                TERCERO,
                                SUCURSAL,
                                ANOCORTE,
                                MESCORTE,
                                ESTADO_FACT,
                                ESTADO,
                                DEUDAACUMULADO001
                FROM            FACTURA_CONTRATO  
                WHERE           COMPANIA=UN_COMPANIA 
                  AND           ANO     =UN_ANOFACTURAR 
                  AND           MES     =UN_MESFACTURAR 
                  AND           TIPO    =UN_TIPOFACTURA
                  AND           INMUEBLEH BETWEEN MI_CCI AND MI_CCF 
            ) R2   ON (D.COMPANIA=R2.COMPANIA) 
                  AND (D.TIPO_FACTURA     =R2.TIPO)
                  AND (D.CONTRATO=R2.CONTRATO)
          --  LEFT JOIN EDADES_CARTERA  E ON (D.COMPANIA=E.COMPANIA) AND (D.ANO=E.ANO) AND (D.CONTRATO=E.CONTRATO) 
           WHERE D.COMPANIA    =UN_COMPANIA 
             AND D.ANO         BETWEEN MI_ANOCORTEN AND MI_ANOANTERIOR
             AND D.MES         BETWEEN CASE WHEN D.ANO=MI_ANOCORTEN THEN MI_MESCORTEN ELSE 1 END AND CASE WHEN D.ANO=MI_ANOANTERIOR THEN MI_MESANTERIOR ELSE 12 END
             AND D.TIPO_FACTURA=UN_TIPOFACTURA
             AND D.ESTADO_FACT NOT IN('AN') 
             AND D.ESTADO      NOT IN('N')
             AND R2.CONTRATO IS NOT NULL
             AND D.CONCEPTO IN (
                                  SELECT CODIGO 
                                  FROM   CONCEPTOFACTURACION 
                                  WHERE  COMPANIA=UN_COMPANIA 
                                    AND  COBRO_INTERES NOT IN (0)
                                )
           GROUP BY  UN_COMPANIA,
                  UN_ANOFACTURAR,
                  UN_MESFACTURAR,
                  UN_TIPOFACTURA,
                  R2.NUMERO,
                  MI_CONCEPTOINTERES,
                  MI_USUARIO,
                  SYSDATE,
                  R2.CENTRO_COSTO,
                  R2.CONTRATO,
                  R2.ESTADO_FACT,
                  R2.ESTADO,
                  DEUDAACUMULADO001,
                  MONTHS_BETWEEN(UN_FECHAFACTURACION,FA.FECHA)

           HAVING SUM((D.VALOR_BASE+D.VALOR_IVA-D.VALOR_RETEFUENTE)-(D.ABONO_BASE+D.ABONO_IVA-D.ABONO_RETE)-(D.CREDITO_BASE+D.CREDITO_IVA-D.CREDITO_RETE)-(D.RECAUDO_BASE+D.RECAUDO_IVA-D.RECAUDO_RETE)) NOT IN (0);
COMMIT; 






--'INSERTO CUOTAS DE ACUERDOS DE PAGO, DEUDAS DIFERIDAS Y ACUERDOS DE FINANCIACION
      INSERT INTO DETALLE_FACTURA_CONTRATO(
                  COMPANIA,
                  ANO, 
                  MES, 
                  TIPO_FACTURA,
                  NUMERO_FACTURA, 
                  CONCEPTO, 
                  CREATED_BY, 
                  DATE_CREATED,
                  CENTRO_COSTO, 
                  CONTRATO, 
                  ESTADO_FACT,
                  ESTADO,
                  VALOR_BASE, 
                  VALOR_IVA, 
                  VALOR_RETEFUENTE,
                  VALOR_INTERES_INC,
                  DEUDA_INTERES_INC,
                  DEUDA_BASE,
                  DEUDA_IVA,
                  DEUDA_RETE)
           SELECT UN_COMPANIA,
                  UN_ANOFACTURAR,
                  UN_MESFACTURAR,
                  UN_TIPOFACTURA,
                  R2.NUMERO,
                  CASE WHEN D.TIPO_ACUERDO IN ('CDE') THEN MI_CONCEPTOACUERDO 
                       WHEN D.TIPO_ACUERDO IN ('CDF') THEN MI_CONCEPTOACUERDODEUDA 
                       WHEN D.TIPO_ACUERDO IN ('FIN') THEN MI_CONCEPTOFINANCIACION 
                  ELSE '' 
                  END,
                  MI_USUARIO,
                  SYSDATE,
                  R2.CENTRO_COSTO,
                  R2.CONTRATO,
                  R2.ESTADO_FACT,
                  R2.ESTADO,
                  ROUND(SUM(D.VALOR_CUOTA),2),
                  0,
                  0,
                  0,
                  0,
                  0,
                  0,
                  0 
           FROM  DETALLE_ACUERDODEPAGO  D INNER JOIN 
           (
              SELECT DISTINCT COMPANIA,
                              CONTRATO,
                              ANO,
                              MES,
                              TIPO,
                              NUMERO,
                              FECHA_LIMITE_PAGO,
                              CENTRO_COSTO,
                              TERCERO,
                              SUCURSAL,
                              ANOCORTE,
                              MESCORTE,
                              ESTADO_FACT,
                              ESTADO 
                      FROM    FACTURA_CONTRATO  
                      WHERE   COMPANIA=UN_COMPANIA 
                        AND   ANO     =UN_ANOFACTURAR 
                        AND   MES     =UN_MESFACTURAR 
                        AND   TIPO    =UN_TIPOFACTURA
                        AND   INMUEBLEH BETWEEN MI_CCI AND MI_CCF 
           ) R2 ON  (D.COMPANIA=R2.COMPANIA) 
                  AND (D.ANO     =R2.ANO)
                  AND (D.MES     =R2.MES)
                  AND (D.CONTRATO=R2.CONTRATO)
           WHERE D.COMPANIA=UN_COMPANIA
             AND D.ANO     =UN_ANOFACTURAR
             AND D.MES     =UN_MESFACTURAR 
             AND D.TIPO_ACUERDO IN ('CDE','CDF','FIN') 
             AND D.ESTADO_CUOTA NOT IN ('PAG')
             AND R2.CONTRATO IS NOT NULL
             AND D.VALOR_CUOTA > 0
          GROUP BY 
                  UN_COMPANIA,
                  UN_ANOFACTURAR,
                  UN_MESFACTURAR,
                  UN_TIPOFACTURA,
                  R2.NUMERO,
                  CASE WHEN D.TIPO_ACUERDO IN ('CDE') THEN MI_CONCEPTOACUERDO 
                       WHEN D.TIPO_ACUERDO IN ('CDF') THEN MI_CONCEPTOACUERDODEUDA 
                       WHEN D.TIPO_ACUERDO IN ('FIN') THEN MI_CONCEPTOFINANCIACION 
                  ELSE '' 
                  END,
                  MI_USUARIO,
                  SYSDATE,
                  R2.CENTRO_COSTO,
                  R2.CONTRATO,
                  R2.ESTADO_FACT,
                  R2.ESTADO;
  COMMIT;                 
-- ACTUALIZA INTERESES CUOTAS
      INSERT INTO DETALLE_FACTURA_CONTRATO(
                  COMPANIA,
                  ANO, 
                  MES, 
                  TIPO_FACTURA,
                  NUMERO_FACTURA, 
                  CONCEPTO, 
                  CREATED_BY, 
                  DATE_CREATED,
                  CENTRO_COSTO, 
                  CONTRATO, 
                  ESTADO_FACT,
                  ESTADO,
                  VALOR_BASE, 
                  VALOR_IVA, 
                  VALOR_RETEFUENTE,
                  VALOR_INTERES_INC,
                  DEUDA_INTERES_INC,
                  DEUDA_BASE,
                  DEUDA_IVA,
                  DEUDA_RETE)
           SELECT UN_COMPANIA,
                  UN_ANOFACTURAR,
                  UN_MESFACTURAR,
                  UN_TIPOFACTURA,
                  R2.NUMERO,
                  MI_CONCEPTOINTERESACUERDO, 
                  MI_USUARIO,
                  SYSDATE,
                  R2.CENTRO_COSTO,
                  R2.CONTRATO,
                  R2.ESTADO_FACT,
                  R2.ESTADO,
                  ROUND(SUM(D.INTERES_FINANCIACION),2),
                  0,
                  0,
                  0,
                  0,
                  0,
                  0,
                  0 
           FROM  DETALLE_ACUERDODEPAGO  D INNER JOIN 
           (
              SELECT DISTINCT COMPANIA,
                              CONTRATO,
                              NUMERO,
                              ANO,
                              MES,
                              FECHA_LIMITE_PAGO,
                              CENTRO_COSTO,
                              TERCERO,
                              SUCURSAL,
                              ANOCORTE,
                              MESCORTE,
                              ESTADO_FACT,
                              ESTADO 
                      FROM    FACTURA_CONTRATO  
                      WHERE   COMPANIA=UN_COMPANIA 
                        AND   ANO     =UN_ANOFACTURAR 
                        AND   MES     =UN_MESFACTURAR 
                        AND   TIPO    =UN_TIPOFACTURA
                        AND   INMUEBLEH BETWEEN MI_CCI AND MI_CCF 
           ) R2 ON (D.COMPANIA=R2.COMPANIA) AND (D.CONTRATO=R2.CONTRATO) 
                AND D.ANO=R2.ANO AND D.ANO=R2.ANO
           WHERE D.COMPANIA=UN_COMPANIA
             AND D.ANO     =UN_ANOFACTURAR
             AND D.MES     =UN_MESFACTURAR 
             AND D.TIPO_ACUERDO IN ('CDE','CDF','FIN') 
             AND D.ESTADO_CUOTA NOT IN ('PAG')
             AND R2.CONTRATO IS NOT NULL
             AND D.VALOR_CUOTA > 0
          GROUP BY 
                  UN_COMPANIA,
                  UN_ANOFACTURAR,
                  UN_MESFACTURAR,
                  UN_TIPOFACTURA,
                  R2.NUMERO,
                  CASE WHEN D.TIPO_ACUERDO IN ('CDE') THEN MI_CONCEPTOACUERDO 
                       WHEN D.TIPO_ACUERDO IN ('CDF') THEN MI_CONCEPTOACUERDODEUDA 
                       WHEN D.TIPO_ACUERDO IN ('FIN') THEN MI_CONCEPTOFINANCIACION 
                  ELSE '' 
                  END,
                  MI_USUARIO,
                  SYSDATE,
                  R2.CENTRO_COSTO,
                  R2.CONTRATO,
                  R2.ESTADO_FACT,
                  R2.ESTADO;
  COMMIT;                 

  --ACTUALIZA ESTADO DE ACUERDOS DE PAGO                 


    UPDATE DETALLE_ACUERDODEPAGO D 
    SET    ESTADO_CUOTA = 'FAC' 
    WHERE  D.COMPANIA=UN_COMPANIA 
      AND  D.ANO     =UN_ANOFACTURAR
      AND  D.MES     =UN_MESFACTURAR 
      AND  D.COMPANIA=UN_COMPANIA 
      AND  TIPO_ACUERDO IN ('CDE','CDF','FIN')
      AND  D.ESTADO_CUOTA NOT IN ('PAG')
      AND  D.VALOR_CUOTA > 0;
  COMMIT; 

  --'INSERTO CONCEPTOS CONFIGURADOS POR CONTRATO A FACTURAR

  INSERT INTO DETALLE_FACTURA_CONTRATO(
          COMPANIA,
          ANO, 
          MES, 
          TIPO_FACTURA,
          NUMERO_FACTURA, 
          CONCEPTO, 
          CREATED_BY, 
          DATE_CREATED,
          CENTRO_COSTO, 
          CONTRATO, 
          ESTADO_FACT,
          ESTADO,
          VALOR_BASE, 
          VALOR_IVA, 
          VALOR_RETEFUENTE,
          VALOR_INTERES_INC,
          DEUDA_INTERES_INC,
          DEUDA_BASE,
          DEUDA_IVA,
          DEUDA_RETE)
         SELECT UN_COMPANIA,
                UN_ANOFACTURAR,
                UN_MESFACTURAR,
                UN_TIPOFACTURA,
                R2.NUMERO,
                C.CONCEPTO,
                MI_USUARIO,
                SYSDATE,
                R2.CENTRO_COSTO,
                C.CONTRATO,
                R2.ESTADO_FACT,
                R2.ESTADO,
                C.VALOR,
                C.VALOR_IVA,
                C.VALOR_RTFTE,
                0,
                0,
                0,
                0,
                0
           FROM  CONCEPTOS_CONTRATOS  C INNER JOIN 
              (
                 SELECT DISTINCT COMPANIA,
                                 CONTRATO,
                                 ESTADO_FACT,
                                 ESTADO,
                                 CENTRO_COSTO,
                                 NUMERO
                 FROM    FACTURA_CONTRATO 
                 WHERE   COMPANIA=UN_COMPANIA
                   AND        ANO=UN_ANOFACTURAR
                   AND        MES=UN_MESFACTURAR
                   AND       TIPO=UN_TIPOFACTURA
                   AND       INMUEBLEH BETWEEN MI_CCI AND MI_CCF 
              ) R2 ON (C.COMPANIA=R2.COMPANIA) AND (C.CONTRATO=R2.CONTRATO)
           WHERE     C.COMPANIA  =UN_COMPANIA
             AND     C.ANO_CANON =UN_ANOFACTURAR 
             AND     C.VALOR+C.VALOR_IVA+C.VALOR_RTFTE > 0;

COMMIT;
-- VERIFICO SF_NOVEDADES YA EXISTENTES Y LAS ELIMINO
 FOR MI_RS IN 
         ( SELECT R4.COMPANIA,
                  R4.ANO,
                  R4.MES,
                  R4.TIPO,
                  R4.NUMERO,
                  R4.CONCEPTO,
                  R4.CONTRATO,
                  R4.VALOR_BASE ,
                  R4.VALOR_IVA  ,
                  R4.VALOR_RETEFUENTE,
                  D.VALOR_BASE D_VALOR_BASE,
                  D.VALOR_IVA  D_VALOR_IVA,
                  D.VALOR_RETEFUENTE D_VALOR_RETEFUENTE
           FROM 
           (  SELECT 
                    N.COMPANIA,
                    N.ANO,
                    N.MES,
                    F.TIPO,
                    F.NUMERO,
                    N.CONTRATO,
                    N.CONCEPTO,
                    N.VALOR_BASE,
                    N.VALOR_IVA,
                    N.VALOR_RETEFUENTE
               FROM SF_NOVEDADES N INNER JOIN FACTURA_CONTRATO F ON  
                                 ( N.COMPANIA=F.COMPANIA) 
                            AND  ( N.ANO     =F.ANO )
                            AND  ( N.MES     =F.MES )
                            AND  ( N.CONTRATO=F.CONTRATO)
               WHERE  N.COMPANIA=UN_COMPANIA
                 AND  N.ANO     =UN_ANOFACTURAR 
                 AND  N.MES     =UN_MESFACTURAR 
                 AND  F.INMUEBLEH BETWEEN MI_CCI AND MI_CCF 
               ) R4 INNER JOIN DETALLE_FACTURA_CONTRATO D ON (R4.COMPANIA=D.COMPANIA) AND (R4.ANO=D.ANO) AND (R4.MES=D.MES) AND (R4.TIPO=D.TIPO_FACTURA) AND (R4.NUMERO=D.NUMERO_FACTURA) AND (R4.CONTRATO=D.CONTRATO)
          )
         LOOP
          MI_NE:=MI_NE+1;

          DELETE DETALLE_FACTURA_CONTRATO
          WHERE  COMPANIA=MI_RS.COMPANIA
            AND  ANO     =MI_RS.ANO
            AND  MES      =MI_RS.MES
            AND  TIPO_FACTURA = MI_RS.TIPO
            AND  NUMERO_FACTURA   = MI_RS.NUMERO
            AND  CONTRATO =MI_RS.CONTRATO
            AND  CONCEPTO =MI_RS.CONCEPTO;
          COMMIT;
        END LOOP;

--'INSERTO CONCEPTOS CONFIGURADOS POR NOVEDAD  
      INSERT INTO DETALLE_FACTURA_CONTRATO(
                COMPANIA,
                ANO, 
                MES, 
                TIPO_FACTURA,
                NUMERO_FACTURA, 
                CONCEPTO, 
                CREATED_BY, 
                DATE_CREATED,
                CENTRO_COSTO, 
                CONTRATO, 
                ESTADO_FACT,
                ESTADO,
                VALOR_BASE, 
                VALOR_IVA, 
                VALOR_RETEFUENTE, 
                VALOR_INTERES_INC,
                DEUDA_INTERES_INC,
                DEUDA_BASE,
                DEUDA_IVA,
                DEUDA_RETE)
         SELECT F.COMPANIA   ,
                F.ANO        ,
                F.MES ,
                F.TIPO,
                F.NUMERO      ,
                N.CONCEPTO     CODIGO,
                MI_USUARIO,
                SYSDATE,
                F.CENTRO_COSTO,
                F.CONTRATO,
                F.ESTADO_FACT,
                F.ESTADO,
                N.VALOR_BASE*CASE WHEN NVL(C.DESCUENTO,0)<>0 THEN -1 ELSE 1 END,
                N.VALOR_IVA*CASE WHEN NVL(C.DESCUENTO,0)<>0 THEN -1 ELSE 1 END,
                N.VALOR_RETEFUENTE*CASE WHEN NVL(C.DESCUENTO,0)<>0 THEN -1 ELSE 1 END,
                0,
                0,
                0,
                0,
                0
           FROM   SF_NOVEDADES N INNER JOIN FACTURA_CONTRATO F ON  
                             ( N.COMPANIA=F.COMPANIA) 
                        AND  ( N.ANO     =F.ANO )
                        AND  ( N.MES     =F.MES )
                        AND  ( N.CONTRATO=F.CONTRATO)
                   INNER JOIN CONCEPTOFACTURACION  C ON (N.COMPANIA=C.COMPANIA) AND (N.CONCEPTO=C.CODIGO) 
                   LEFT JOIN DETALLE_FACTURA_CONTRATO D 
                          ON D.COMPANIA = N.COMPANIA 
                         AND D.ANO      = N.ANO
                         AND D.MES      = N.MES
                         AND D.TIPO_FACTURA   = F.TIPO
                         AND D.NUMERO_FACTURA = F.NUMERO
                         AND D.CONCEPTO       = N.CONCEPTO
           WHERE  N.COMPANIA=UN_COMPANIA
             AND  N.ANO     =UN_ANOFACTURAR 
             AND  N.MES     =UN_MESFACTURAR 
             AND  N.VALOR_BASE+N.VALOR_IVA+N.VALOR_RETEFUENTE <> 0
             AND  F.INMUEBLEH BETWEEN MI_CCI AND MI_CCF 
             AND  D.COMPANIA IS NULL;

--//DBMS_OUTPUT.PUT_LINE(' INSERTO '||SQL%ROWCOUNT);             
   COMMIT;

--' ACTUALIZO RTETENCIONES EN LA FUENTE
-- AQUI SE INCLUYE LA NUEVA RUTINA DE ACUERDO A LA REFORMA TRIBUTARIA DEL 2016    


   UPDATE   CONTRATOS 
   SET      REGIMEN           =NULL,
            NUM_TERCEROS      =0,
            TERCERO_RETENCION ='999999999999999999',
            SUCURSAL_RETENCION='999'
    WHERE    COMPANIA=UN_COMPANIA;
COMMIT;
-- ACTUALIZA EL REGIMEN A APLICAR
     UPDATE CONTRATOS C1
     SET    C1.REGIMEN=
         (SELECT MIN(CASE WHEN C.REGIMEN='G' OR T.REGIMEN='G' THEN '1' ELSE CASE WHEN C.REGIMEN='C' OR T.REGIMEN='C' THEN '2' ELSE '3' END  END)
           FROM  CONTRATOS C INNER JOIN TERCERO_CONTRATO TC ON (TC.COMPANIA = C.COMPANIA)
                 AND    (TC.CONTRATO = C.CODIGO) INNER JOIN TERCERO  T ON (TC.COMPANIA = T.COMPANIA)
                 AND    (TC.TERCERO= T.NIT) AND (TC.SUCURSAL = T.SUCURSAL)
            WHERE C.COMPANIA=C1.COMPANIA 
              AND C.CODIGO  =C1.CODIGO
          )
     WHERE C1.COMPANIA=UN_COMPANIA;
COMMIT;

     UPDATE CONTRATOS C 
     SET    C.REGIMEN=CASE WHEN C.REGIMEN='1'  THEN 'G' WHEN C.REGIMEN='2' THEN 'C' ELSE 'S' END
     WHERE  C.COMPANIA=UN_COMPANIA;
COMMIT;
-- ACTUALIZA EL NIT MAS ANTIGUO DEL MISMO REGIMEN COMO BASE DE RETENCION
    UPDATE CONTRATOS C1
    SET    C1.TERCERO_RETENCION=
         (SELECT TO_CHAR(MIN(TO_NUMBER(TC.TERCERO)))
           FROM  CONTRATOS C INNER JOIN TERCERO_CONTRATO TC ON (TC.COMPANIA = C.COMPANIA)
                 AND    (TC.CONTRATO = C.CODIGO) INNER JOIN TERCERO  T ON (TC.COMPANIA = T.COMPANIA)
                 AND    (TC.TERCERO= T.NIT) AND (TC.SUCURSAL = T.SUCURSAL)
            WHERE C.COMPANIA=C1.COMPANIA 
              AND C.CODIGO  =C1.CODIGO
              AND T.REGIMEN=C1.REGIMEN
          )
     WHERE C1.COMPANIA=UN_COMPANIA
     AND  
         (SELECT TO_CHAR(MIN(TO_NUMBER(TC.TERCERO)))
           FROM  CONTRATOS C INNER JOIN TERCERO_CONTRATO TC ON (TC.COMPANIA = C.COMPANIA)
                 AND    (TC.CONTRATO = C.CODIGO) INNER JOIN TERCERO  T ON (TC.COMPANIA = T.COMPANIA)
                 AND    (TC.TERCERO= T.NIT) AND (TC.SUCURSAL = T.SUCURSAL)
            WHERE C.COMPANIA=C1.COMPANIA 
              AND C.CODIGO  =C1.CODIGO
              AND T.REGIMEN=C1.REGIMEN
          ) IS NOT NULL;
COMMIT;

-- ACTUALIZA LA SUCURSAL DEL TERCERO MAS ANTIGUO DEL MISMO REGIMEN COMO BASE DE RETENCION
       UPDATE CONTRATOS C1
     SET    C1.REGIMEN=
         (SELECT MIN(CASE WHEN C.REGIMEN='G' OR T.REGIMEN='G' THEN '1' ELSE CASE WHEN C.REGIMEN='C' OR T.REGIMEN='C' THEN '2' ELSE '3' END  END)
           FROM  CONTRATOS C INNER JOIN TERCERO_CONTRATO TC ON (TC.COMPANIA = C.COMPANIA)
                 AND    (TC.CONTRATO = C.CODIGO) INNER JOIN TERCERO  T ON (TC.COMPANIA = T.COMPANIA)
                 AND    (TC.TERCERO= T.NIT) AND (TC.SUCURSAL = T.SUCURSAL)
            WHERE C.COMPANIA=C1.COMPANIA 
              AND C.CODIGO  =C1.CODIGO
          )
     WHERE C1.COMPANIA=UN_COMPANIA;
COMMIT;
COMMIT;
--' ASIGNA LOS QUE TIENE REPRESENTANTE LEGAL
-- VERIFICAMOS QUE NO EXISTA SINO UN REPRESENTANTE LEGAL 

FOR MI_RS IN 
( SELECT TC.CONTRATO,COUNT(*) N
  FROM CONTRATOS C INNER JOIN TERCERO_CONTRATO TC ON (TC.COMPANIA = C.COMPANIA)
                 AND    (TC.CONTRATO = C.CODIGO) 
  WHERE C.COMPANIA=UN_COMPANIA
    AND TC.ESTADO IN('AC') 
    AND TC.TIPO_TERCERO='T'
    AND TC.REPRESENTANTE NOT IN (0)
GROUP BY TC.COMPANIA,TC.CONTRATO
  HAVING COUNT(*) >1
)
LOOP
  MI_NE:=MI_NE+1;
  MI_I:=0;
  FOR MI_RS1 IN
    (SELECT C.COMPANIA,TC.CONTRATO,TC.TERCERO,TC.SUCURSAL
      FROM CONTRATOS C INNER JOIN TERCERO_CONTRATO TC ON (TC.COMPANIA = C.COMPANIA)
                   AND    (TC.CONTRATO = C.CODIGO) 
      WHERE C.COMPANIA   =UN_COMPANIA
        AND TC.CONTRATO=MI_RS.CONTRATO
        AND TC.ESTADO IN('AC') 
        AND TC.TIPO_TERCERO='T'
        AND TC.REPRESENTANTE NOT IN (0)
  ) LOOP
   MI_I:=MI_I+1;
   IF MI_I=1 THEN
      NULL;
   ELSE
      UPDATE TERCERO_CONTRATO TC
      SET    TC.REPRESENTANTE=0
      WHERE TC.COMPANIA   =MI_RS1.COMPANIA
        AND TC.CONTRATO   =MI_RS1.CONTRATO
        AND  TC.TERCERO   =MI_RS1.TERCERO
        AND  TC.SUCURSAL  =MI_RS1.SUCURSAL;
   END IF;
  END LOOP;  
END LOOP;
COMMIT;

     UPDATE 
      (
         SELECT C.TERCERO_RETENCION  C_TERCERO_RETENCION,
                C.SUCURSAL_RETENCION C_SUCURSAL_RETENCION,
                C.TERCERO            C_TERCERO,
                C.SUCURSAL           C_SUCURSAL,
                (SELECT CASE WHEN C.REGIMEN=T.REGIMEN AND TC.REPRESENTANTE<>0  THEN TC.TERCERO ELSE C.TERCERO_RETENCION END 
                 FROM TERCERO_CONTRATO TC INNER JOIN TERCERO  T 
                                          ON (TC.COMPANIA = T.COMPANIA) AND  (TC.TERCERO= T.NIT) AND (TC.SUCURSAL = T.SUCURSAL) 
                 WHERE TC.COMPANIA=C.COMPANIA
                   AND TC.CONTRATO=C.CODIGO
                   AND TC.ESTADO IN('AC') 
                   AND TC.TIPO_TERCERO IN ('T') 
                   AND TC.REPRESENTANTE NOT IN (0)
                 ) T_TERCERO,
                (SELECT  CASE WHEN C.REGIMEN=T.REGIMEN AND TC.REPRESENTANTE<>0 THEN TC.SUCURSAL ELSE C.SUCURSAL_RETENCION END
                 FROM TERCERO_CONTRATO TC INNER JOIN TERCERO  T 
                                          ON (TC.COMPANIA = T.COMPANIA) AND  (TC.TERCERO= T.NIT) AND (TC.SUCURSAL = T.SUCURSAL) 
                 WHERE TC.COMPANIA=C.COMPANIA
                   AND TC.CONTRATO=C.CODIGO
                   AND TC.ESTADO IN('AC') 
                   AND TC.TIPO_TERCERO IN ('T') 
                   AND TC.REPRESENTANTE NOT IN (0)
                 ) T_SUCURSAL
         FROM   CONTRATOS  C 
         WHERE    C.COMPANIA='001'
           AND    C.ESTADO IN(2) 
        )
         SET      C_TERCERO_RETENCION =NVL(T_TERCERO,NVL(C_TERCERO_RETENCION,C_TERCERO)),
                  C_SUCURSAL_RETENCION=NVL(T_SUCURSAL,NVL(C_SUCURSAL_RETENCION,C_SUCURSAL));
COMMIT;

    UPDATE   
        (SELECT  CC.SERVICIO N_CC_SERVICIO,N.SERVICIO N_SERVICIO
          FROM   CONCEPTOS_CONTRATOS CC INNER JOIN CONCEPTOFACTURACION N ON (CC.COMPANIA=N.COMPANIA) AND (CC.CONCEPTO=N.CODIGO)
          WHERE  CC.COMPANIA=UN_COMPANIA 
            AND  CC.ANO_CANON=UN_ANOFACTURAR 
            AND  CC.SERVICIO  NOT IN (N.SERVICIO)
        )
    SET      N_CC_SERVICIO=N_SERVICIO;    

    UPDATE   CONTRATOS 
    SET      CONCEPTO_RETEICA=MI_CONCEPTORETEICA 
    WHERE    COMPANIA=UN_COMPANIA
      AND    (INSTR(MI_CONCEPTORETEICA,CONCEPTO_RETEICA)<=0 OR CONCEPTO_RETEICA IS NULL)
      AND    ESTADO IN (2);

    UPDATE   CONTRATOS 
    SET      CONCEPTO_RETEIVA=MI_CONCEPTORETEIVA 
    WHERE    COMPANIA=UN_COMPANIA
      AND    (INSTR(MI_CONCEPTORETEIVA,CONCEPTO_RETEIVA)<=0 OR CONCEPTO_RETEIVA IS NULL)
      AND    ESTADO IN (2);

--'Crear tabla  temporal
   DELETE TEMPSUMATORIA;
   COMMIT;

    INSERT INTO  TEMPSUMATORIA  (
                                 COMPANIA, 
                                 TERCERO_RETENCION, 
                                 SUCURSAL_RETENCION, 
                                 SERVICIO, 
                                 SUMATORIABASES,
                                 VALOR_MINIMO_RETEICA, 
                                 PORCENTAJE_RETEIVA, 
                                 VALOR_MINIMO_RETEIVA, 
                                 PORCENTAJE_RETEICA, 
                                 VALOR_RETEICA, 
                                 VALOR_RETEIVA,REGIMEN) 
          SELECT       CC.COMPANIA, 
                       TERCERO_RETENCION, 
                       SUCURSAL_RETENCION, 
                       CC.SERVICIO, 
                       SUM(NVL(CC.VALOR,0)) SUMATORIABASES,
                       SUM(0) VALOR_MINIMO_RETEICA, 
                       SUM(0) PORCENTAJE_RETEIVA, 
                       SUM(0) VALOR_MINIMO_RETEIVA, 
                       SUM(0) PORCENTAJE_RETEICA, 
                       SUM(0) VALOR_RETEICA, 
                       SUM(0) VALOR_RETEIVA,
                       C.REGIMEN
          FROM         (CONCEPTOS_CONTRATOS  CC INNER JOIN CONTRATOS  C ON (CC.COMPANIA = C.COMPANIA)
          AND          (CC.CONTRATO = C.CODIGO)) 
          WHERE        CC.COMPANIA=UN_COMPANIA
            AND        CC.ANO_CANON=UN_ANOFACTURAR
            AND        INSTR(MI_CONCEPTOSBASERETENCION,CC.CONCEPTO)>0
            AND        C.ESTADO IN (2)
            AND        CC.SERVICIO IS NOT NULL 
            AND        C.REGIMEN IN ('C','G') 
        GROUP BY       CC.COMPANIA,CC.SERVICIO, C.TERCERO_RETENCION, C.SUCURSAL_RETENCION,C.REGIMEN;

COMMIT; 

        BEGIN
            SELECT VALOR_RETEICA,
                   PORRETEICA,
                   VALOR_RETEIVA,
                   PORRETEIVA,
                   PORCIVA
            INTO   MI_VALOR_RETEICA,
                   MI_PORRETEICA,
                   MI_VALOR_RETEIVA,
                   MI_PORRETEIVA,
                   MI_PORCIVA
            FROM   CONCEPTOFACTURACION 
            WHERE  COMPANIA=UN_COMPANIA 
              AND  CODIGO='002';
        EXCEPTION WHEN NO_DATA_FOUND THEN
                   MI_VALOR_RETEICA:=0;
                   MI_PORRETEICA:=0;
                   MI_VALOR_RETEIVA:=0;
                   MI_PORRETEIVA:=0;
                   MI_PORCIVA:=0;
        END;        
COMMIT;

        UPDATE  TEMPSUMATORIA 
        SET     VALOR_RETEICA=CASE WHEN NVL(SUMATORIABASES,0)>NVL(MI_VALOR_RETEICA, 0) THEN ROUND(NVL(SUMATORIABASES,0) * NVL(MI_PORRETEICA,0)/100,0) ELSE 0 END ,
                VALOR_RETEIVA=CASE WHEN TEMPSUMATORIA.REGIMEN='G' THEN  ROUND( NVL(SUMATORIABASES,0) * MI_PORCIVA * MI_PORRETEIVA / 10000,0) ELSE 0 END 
        WHERE   COMPANIA=UN_COMPANIA;
COMMIT;
--RETURN 'OK';
-- Actualizar SF_NOVEDADES
        DELETE SF_NOVEDADES 
        WHERE  COMPANIA=UN_COMPANIA
          AND  ANO     =UN_ANOFACTURAR 
          AND  MES     =UN_MESFACTURAR 
          AND  INSTR(MI_CONCEPTORETEICA||','||MI_CONCEPTORETEIVA,CONCEPTO)>0 
          AND  TIPO='PL';
COMMIT;
          INSERT INTO SF_NOVEDADES (
                               COMPANIA, 
                               CONTRATO, 
                               ANO, 
                               MES, 
                               CONCEPTO, 
                               VALOR_BASE, 
                               VALOR_IVA, 
                               VALOR_RETEFUENTE,
                               TIPO, 
                               FACTURADA, 
                               CREATED_BY, 
                               DATE_CREATED, 
                               MODIFIED_BY, 
                               DATE_MODIFIED,
                               DOCUMENTO_SOPORTE, 
                               XINMUEBLE, 
                               XARRENDATARIO, 
                               XSUCURSAL, 
                               XTERCERO,
                               VALOR_BASE_RETENCION)
            SELECT             C.COMPANIA,
                               C.CODIGO,
                               UN_ANOFACTURAR     ANO,
                               UN_MESFACTURAR     MES,
                               MI_CONCEPTORETEICA CONCEPTO, 
                               0                  VALOR_BASE,
                               0                  VALOR_IVA,
                               T.VALOR_RETEICA    VALOR_RETEFUENTE,
                               'PL'               TIPO,
                               0                  FACTURADA,
                               MI_USUARIO         CREATED_BY,
                               SYSDATE            DATE_CREATED,
                               MI_USUARIO         MODIFIED_BY,
                               SYSDATE            DATE_MODIFIED,
                               'RETE/ICA'         DOCUMENTO_SOPORTE,
                               C.INMUEBLE         XINMUEBLE,
                               ''                 XARRENDATARIO,
                               C.SUCURSAL         XSUCURSAL,
                               C.TERCERO          XTERCERO,
                               T.VALOR_BASE       VALOR_BASE_RETENCION
            FROM    CONTRATOS  C INNER JOIN 
                          (SELECT COMPANIA,TERCERO_RETENCION,SUCURSAL_RETENCION,SUM(SUMATORIABASES) VALOR_BASE,SUM(VALOR_RETEICA) VALOR_RETEICA
                             FROM TEMPSUMATORIA 
                           GROUP BY COMPANIA,TERCERO_RETENCION,SUCURSAL_RETENCION
                           ) T
                       ON (C.COMPANIA=T.COMPANIA) AND (C.TERCERO=T.TERCERO_RETENCION) AND (C.SUCURSAL=T.SUCURSAL_RETENCION) 
            WHERE   C.COMPANIA=UN_COMPANIA
             AND    C.ESTADO IN (2)
             AND    T.VALOR_RETEICA NOT IN (0)
             AND    C.REGIMEN IN ('C','G');
COMMIT;
        INSERT INTO SF_NOVEDADES(
                              COMPANIA, 
                              CONTRATO, 
                              ANO, 
                              MES, 
                              CONCEPTO, 
                              VALOR_BASE, 
                              VALOR_IVA, 
                              VALOR_RETEFUENTE,
                              TIPO, 
                              FACTURADA, 
                              CREATED_BY, 
                              DATE_CREATED, 
                              MODIFIED_BY, 
                              DATE_MODIFIED,
                              DOCUMENTO_SOPORTE, 
                              XINMUEBLE, 
                              XARRENDATARIO, 
                              XSUCURSAL, 
                              XTERCERO,
                              VALOR_BASE_RETENCION)
            SELECT  C.COMPANIA,
                    C.CODIGO,
                    UN_ANOFACTURAR     ANO,
                    UN_MESFACTURAR     MES,
                    MI_CONCEPTORETEIVA CONCEPTO, 
                    0                  VALOR_BASE,
                    0                  VALOR_IVA,
                    T.VALOR_RETEIVA    VALOR_RETEFUENTE,
                    'PL'               TIPO,
                    0                  FACTURADA,
                    MI_USUARIO         CREATED_BY,
                    SYSDATE            DATE_CREATED,
                    MI_USUARIO         MODIFIED_BY,
                    SYSDATE            DATE_MODIFIED,
                    'RETE/ICA'         DOCUMENTO_SOPORTE,
                    C.INMUEBLE         XINMUEBLE,
                    ''                 XARRENDATARIO,
                    C.SUCURSAL         XSUCURSAL,
                    C.TERCERO          XTERCERO,
                    T.VALOR_BASE       VALOR_BASE_RETENCION
            FROM    CONTRATOS  C INNER JOIN
                    (SELECT COMPANIA,TERCERO_RETENCION,SUCURSAL_RETENCION,SUM(SUMATORIABASES) VALOR_BASE,SUM(VALOR_RETEIVA) VALOR_RETEIVA
                     FROM TEMPSUMATORIA 
                     GROUP BY COMPANIA,TERCERO_RETENCION,SUCURSAL_RETENCION
                     ) T
                     ON (C.COMPANIA=T.COMPANIA) AND (C.TERCERO=T.TERCERO_RETENCION) AND (C.SUCURSAL=T.SUCURSAL_RETENCION) 
            WHERE   C.COMPANIA=UN_COMPANIA 
              AND   C.ESTADO IN (2)
              AND   T.VALOR_RETEIVA NOT IN (0)
              AND   C.REGIMEN IN ('C','G');
    COMMIT;                      

--' EMPIEZA TOTALIZACION DE DATOS Y REVISION DE INFORMACION DEL PERIODO
    UPDATE
       (
           SELECT F.ESTADOINMUEBLE F_ESTADOINMUEBLE, T.ESTADO T_ESTADO 
           FROM   FACTURA_CONTRATO F INNER JOIN CONTRATOS T ON (F.COMPANIA = T.COMPANIA) AND (F.CONTRATO = T.CODIGO) 
           WHERE  F.COMPANIA=UN_COMPANIA 
             AND  F.ANO     =UN_ANOFACTURAR 
             AND  F.MES     =UN_MESFACTURAR 
             AND  F.ESTADOINMUEBLE <> T.ESTADO
       )
    SET    F_ESTADOINMUEBLE = T_ESTADO; 
COMMIT;

--Verifico que los datos del inmueble esten en la factura 

       UPDATE 
        (SELECT F.INMUEBLEH F_INMUEBLEH,
                T.INMUEBLE  T_INMUEBLE
         FROM   FACTURA_CONTRATO F INNER JOIN CONTRATOS  T ON (F.COMPANIA=T.COMPANIA) AND (F.CONTRATO=T.CODIGO)
         WHERE  F.COMPANIA=UN_COMPANIA 
           AND  F.ANO     =UN_ANOFACTURAR 
           AND  F.MES     =UN_MESFACTURAR
           AND  F.INMUEBLEH IS NULL
        )
        SET    F_INMUEBLEH=T_INMUEBLE;

COMMIT;
--Actualizo edades de cartera
-- CALCULA FINANCIACIONES POR TERCERO
--CalcularFinanciacionTer strCompania, Anofacturar, Mesfacturar
--Se creo esta funcion para liquidar un prestamo que maneja la entidad realizado por los usuarios para construccion
--'el cual calcula un interes por el saldo y mensualmente debe reflejar el abono a la deuda del usuario.
--' ACTUALIZADO POR HPV EN ABRIL 23 DE 2016
--'cargo el saldo de la financiacion
--Actualiza saldos de financiación en contratos 
  UPDATE
  ( SELECT  F.SALDOFINAN F_SALDOFINAN, 
            T.VALORFINAN T_VALORFINAN,
            F.TASAFINAN  F_TASAFINAN,
            T.TASAFINAN  T_TASAFIN
    FROM CONTRATOS  T INNER JOIN FACTURA_CONTRATO  F ON (T.MESFIN = F.MES) AND (T.ANOFIN = F.ANO) AND (T.CODIGO = F.CONTRATO) AND (T.COMPANIA = F.COMPANIA) 
            WHERE  F.COMPANIA=UN_COMPANIA 
              AND  F.ANO     =MI_ANOANTERIOR
              AND  F.MES     =MI_MESANTERIOR
   )
   SET    F_SALDOFINAN = T_VALORFINAN,
          F_TASAFINAN  = T_TASAFIN;

--'ACTUALIZO INTERES Y SALDO
 DELETE TEMP_FACTURA;
 COMMIT;

 INSERT INTO TEMP_FACTURA
         (
        COMPANIA,
        ANO,
        MES,
        TIPO,
        NUMERO,
        CONTRATO,
        INTERESFINAN,
        TASAFINAN
         )
         SELECT  F.COMPANIA,
                 F.ANO,
                 F.MES,
                 F.TIPO,
                 F.NUMERO,
                 F.CONTRATO,
                 ROUND(F1.SALDOFINAN*F1.TASAFINAN/100,2) F_SALDO,
                 F1.TASAFINAN F1_TASAFINAN
          FROM  (CONTRATOS T INNER JOIN FACTURA_CONTRATO F1 ON (T.CODIGO = F1.CONTRATO) AND (T.COMPANIA = F1.COMPANIA)) INNER JOIN FACTURA_CONTRATO  F ON (F1.COMPANIA = F.COMPANIA) AND (F1.TIPO = F.TIPO) AND (F1.CONTRATO = F.CONTRATO) 
          WHERE  T.COMPANIA=UN_COMPANIA
            AND  F1.ANO    =MI_ANOANTERIOR 
            AND  F1.MES    =MI_MESANTERIOR 
            AND  F.ANO     =UN_ANOFACTURAR 
            AND  F.MES     =UN_MESFACTURAR 
            AND  F1.SALDOFINAN <> 0 
            AND T.ESTADO IN (2)
            AND  T.VALORFINAN IS NOT NULL;

 COMMIT;




--    UPDATE
--        ( SELECT  F.INTERESFINAN F_INTERESFINAN,
--                  F1.INTERESFINAN F1_INTERESFINAN,
--                  F.TASAFINAN F_TASAFINAN ,
--                  F1.TASAFINAN F1_TASAFINAN
--          FROM   TEMP_FACTURA F1 INNER JOIN FACTURA_CONTRATO F ON (F1.COMPANIA = F.COMPANIA) AND (F1.TIPO = F.TIPO) AND (F1.CONTRATO = F.CONTRATO) 
--          WHERE  F.COMPANIA=UN_COMPANIA
--            AND  F.ANO     =UN_ANOFACTURAR 
--            AND  F.MES     =UN_MESFACTURAR 
--         )
--     SET      F_INTERESFINAN=F1_INTERESFINAN,
--              F_TASAFINAN   =F1_TASAFINAN;
--COMMIT;  






-- 'se deja amarrado al concepto 549
--Actualiza intereses de financiación en contra en detalle

     INSERT INTO DETALLE_FACTURA_CONTRATO(
                                           COMPANIA, 
                                           ANO, 
                                           MES, 
                                           TIPO_FACTURA, 
                                           NUMERO_FACTURA, 
                                           FECHA_PROCESO, 
                                           CONCEPTO, 
                                           VALOR_BASE, 
                                           VALOR_IVA, 
                                           VALOR_RETEFUENTE, 
                                           TEXTO_CONCEPTO, 
                                           CONTRATO, 
                                           CREATED_BY, 
                                           CENTRO_COSTO, 
                                           ESTADO_FACT )
       SELECT F.COMPANIA, 
              F.ANO, 
              F.MES, 
              F.TIPO, 
              F.NUMERO, 
              F.FECHA,
              MI_CONCEPTOINTERESFINANCIACION,
              INTERESFINAN*(-1), 
              0,
              ROUND(INTERESFINAN* (-0.07),0),
              'Saldo a Favor:$ ' || TO_CHAR(SALDOFINAN),
              F.CONTRATO, 
              F.CREATED_BY, 
              F.CENTRO_COSTO, 
              'AC' 
        FROM   FACTURA_CONTRATO F
        WHERE  F.COMPANIA=UN_Compania 
         AND   F.ANO     =UN_Anofacturar 
         AND   F.MES     =UN_Mesfacturar 
         AND   F.ESTADO_CONTRATO IN(2) 
         AND   F.INTERESFINAN>0;

    BEGIN
       SELECT MAX (NUMERO)+1 
       INTO  MI_NUMEROINI
       FROM  RECAUDO 
       WHERE COMPANIA = UN_COMPANIA
         AND TIPO     = 'NCA'
         AND ANO      = MI_ANOANTERIOR
         AND MES      = MI_MESANTERIOR 
         GROUP BY ANO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
       MI_CONSECUTIVO := TO_NUMBER(TO_CHAR(SYSDATE,'RRRR')||'000001');
    END;    


   BEGIN
       SELECT NVL(MAX(NUMERO),0)+1 
       INTO MI_CONSECUTIVO
       FROM  RECAUDO 
       WHERE COMPANIA = UN_COMPANIA
         AND TIPO     = 'NCA'
         AND ANO      = MI_ANOANTERIOR
         AND MES      = MI_MESANTERIOR 
         GROUP BY ANO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
       MI_CONSECUTIVO := TO_NUMBER(TO_CHAR(SYSDATE,'RRRR')||'000001');
    END;

--Inserta recaudos NCA
    INSERT INTO RECAUDO
     (
      COMPANIA,
      ANO , 
      MES , 
      TIPO, 
      TIPO_FACTURA,
      CONTRATO , 
      FECHA ,
      DESCRIPCION,
      VALOR_BASE,
      VALOR_IVA , 
      VALOR_RTFTE , 
      TERCERO , 
      SUCURSAL , 
      CREATED_BY, 
      DATE_CREATED, 
      MODIFIED_BY, 
      DATE_MODIFIED, 
      ESTADO,
      CENTRO_COSTO,  
      FACTURA, 
      FECHA_RECAUDO, 
      RECAUDADOR, 
      PAGADO_EN,
      TIPO_GEN
      )
      SELECT F.COMPANIA, 
              F.ANO , 
              F.MES , 
              'NCA' TIPO, 
              F.TIPO TIPO_FACTURA,
              F.CONTRATO , 
              F.FECHA ,
               'NOTA CREDITO POR SALDO ADEUDADO A USUARIO' DESCRIPCION,
              F.VALOR_BASE-F.INTERESFINAN VALOR_BASE,
              F.VALOR_IVA , 
              F.VALOR_RTFTE , 
              F.TERCERO , 
              F.SUCURSAL , 
              F.CREATED_BY, 
              F.DATE_CREATED, 
              F.MODIFIED_BY, 
              F.DATE_MODIFIED, 
              'P' ESTADO,
              F.CENTRO_COSTO, 
              MI_CONSECUTIVO+ROWNUM FACTURA, 
              F.FECHA FECHA_RECAUDO, 
              F.CREATED_BY RECAUDADOR, 
              MI_PAGADO_EN PAGADO_EN,
              'A' TIPO_GENERACION
     FROM   FACTURA_CONTRATO F 
     WHERE  F.COMPANIA=UN_COMPANIA 
       AND  F.ANO     =UN_ANOFACTURAR 
       AND  F.MES     =UN_MESFACTURAR
       AND  F.ESTADO_CONTRATO IN (2) 
       AND  F.SALDOFINAN>0;


--"Inserta detalles de recaudos NCA 
      INSERT INTO DETALLE_RECAUDO ( 
                                   COMPANIA, 
                                   ANO, 
                                   MES, 
                                   TIPO_RECAUDO, 
                                   RECAUDO, 
                                   DATE_CREATED, 
                                   TIPO_FACTURA, 
                                   NUMERO_FACTURA, 
                                   CONCEPTO, 
                                   VALOR_BASE, 
                                   VALOR_IVA, 
                                   VALOR_RTFTE, 
                                   ESTADO, 
                                   CREATED_BY,
                                   MODIFIED_BY,
                                   DATE_MODIFIED,
                                   CENTRO_COSTO, 
                                   DEUDA_BASE,
                                   DEUDA_IVA,
                                   DEUDA_RETE,
                                   TIPO_GEN)
        SELECT  TMPRECAUDOS.COMPANIA, 
                TMPRECAUDOS.ANO, 
                TMPRECAUDOS.MES, 
                TMPRECAUDOS.TIPO, 
                MI_CONSECUTIVO, 
                TMPRECAUDOS.FECHA, 
                TMPRECAUDOS.TIPO_FACTURA, 
                TMPRECAUDOS.FACTURA, 
                D.CONCEPTO, 
                D.VALOR_BASE, 
                D.VALOR_IVA, 
                D.VALOR_RETEFUENTE, 
                'P' PAGO, 
                TMPRECAUDOS.CREATED_BY, 
                TMPRECAUDOS.MODIFIED_BY, 
                TMPRECAUDOS.DATE_MODIFIED, 
                TMPRECAUDOS.CENTRO_COSTO, 
                D.DEUDA_BASE,
                D.DEUDA_IVA, 
                D.DEUDA_RETE, 
                'A' TIPO_GENERACION
        FROM    
        (
         SELECT F.COMPANIA, 
                F.ANO , 
                F.MES , 
                'NCA' TIPO, 
                F.FECHA ,
                F.TIPO TIPO_FACTURA, 
                F.NUMERO FACTURA,
                F.CREATED_BY , 
                F.MODIFIED_BY, 
                F.DATE_MODIFIED, 
                F.CENTRO_COSTO, 
                SYSDATE  DATE_CREATED 

         FROM   FACTURA_CONTRATO F 
         WHERE  F.COMPANIA=UN_COMPANIA 
           AND  F.ANO     =UN_ANOFACTURAR 
           AND  F.MES     =UN_MESFACTURAR
           AND  F.ESTADO_CONTRATO IN (2) 
           AND  F.SALDOFINAN>0
        ) TMPRECAUDOS INNER JOIN DETALLE_FACTURA_CONTRATO D ON (TMPRECAUDOS.COMPANIA = D.COMPANIA) 
           AND (TMPRECAUDOS.ANO = D.ANO) 
           AND (TMPRECAUDOS.MES = D.MES) 
           AND (TMPRECAUDOS.TIPO_FACTURA = D.TIPO_FACTURA) 
           AND (TMPRECAUDOS.FACTURA = D.NUMERO_FACTURA);

--"Actualiza saldos de financiacion en facturación 

 DELETE TEMP_FACTURA;
 COMMIT;

 INSERT INTO TEMP_FACTURA
         (
        COMPANIA,
        ANO,
        MES,
        TIPO,
        NUMERO,
        CONTRATO,
        SALDOFINAN
          )

      SELECT   F.COMPANIA,
               F.ANO,
               F.MES,
               F.TIPO,
               F.NUMERO,
               F.CONTRATO,
               F1.SALDOFINAN-F.VALOR_BASE-F.VALOR_IVA+F.VALOR_RTFTE F1_SALDOFINAN
       FROM    CONTRATOS T INNER JOIN FACTURA_CONTRATO  F1 ON (T.COMPANIA = F1.COMPANIA)   AND (T.CODIGO = F1.CONTRATO)
                           INNER JOIN FACTURA_CONTRATO  F  ON (F1.COMPANIA = F.COMPANIA) AND (F1.TIPO = F.TIPO)  AND (F1.CONTRATO = F.CONTRATO)
        WHERE T.COMPANIA=UN_COMPANIA
          AND T.VALORFINAN IS NOT NULL 
          AND F.ANO=UN_ANOFACTURAR 
          AND T.ESTADO  IN (2)
          AND F.MES=UN_MESFACTURAR 
          AND F1.ANO=MI_ANOANTERIOR 
          AND F1.MES=MI_MESANTERIOR 
          AND F1.SALDOFINAN NOT IN (0); 






--               UPDATE 
--     (
--       SELECT  F.SALDOFINAN  F_SALDOFINAN,
--               F1.SALDOFINAN F1_SALDOFINAN
--        FROM   TEMP_FACTURA F1 INNER JOIN FACTURA_CONTRATO F ON (F1.COMPANIA = F.COMPANIA) AND (F1.TIPO = F.TIPO) AND (F1.CONTRATO = F.CONTRATO) 
--        WHERE F.COMPANIA=UN_COMPANIA
--          AND F.ANO=UN_ANOFACTURAR 
--          AND F.MES=UN_MESFACTURAR 
--          AND F1.SALDOFINAN NOT IN (0) 
--     )
 --    SET    F_SALDOFINAN = F1_SALDOFINAN;   



--Actualiza saldos de financiación y saldos a favor en factruración y detalle de facturación 
IF LENGTH(TRIM(MI_CONCEPTOINTERESFINANCIACION)) > 0 THEN

  DELETE TEMP_FACTURA_X;

 COMMIT;

INSERT INTO TEMP_FACTURA_X
         (
        COMPANIA,
        ANO,
        MES,
        TIPO_FACTURA,
        NUMERO_FACTURA,
        CONCEPTO,
        TEXTO_CONCEPTO
          )
    SELECT     D.COMPANIA,
               D.ANO,
               D.MES,
               D.TIPO_FACTURA,
               D.NUMERO_FACTURA,
               CONCEPTO,
               'SALDO A FAVOR: $ ' ||TO_CHAR(CASE WHEN F.SALDOFINAN<F.VALOR_BASE + F.VALOR_IVA - F.VALOR_RTFTE THEN 0 ELSE F.SALDOFINAN END,'999,999,999.99') D_TEXTO_CONCEPTO1
        FROM   DETALLE_FACTURA_CONTRATO D INNER JOIN FACTURA_CONTRATO F ON (D.COMPANIA=F.COMPANIA) AND (D.ANO=F.ANO) AND (D.MES=F.MES)  AND (D.TIPO_FACTURA=F.TIPO ) AND (D.NUMERO_FACTURA=F.NUMERO) 
        WHERE  D.COMPANIA = UN_COMPANIA 
          AND  D.ANO      = UN_ANOFACTURAR 
          AND  D.MES      = UN_MESFACTURAR 
          AND  D.CONCEPTO =MI_CONCEPTOINTERESFINANCIACION 
          AND  F.ESTADO_CONTRATO IN (2); 




--        UPDATE 
--        (  SELECT  D.TEXTO_CONCEPTO D_TEXTO_CONCEPTO,
--                   T.TEXTO_CONCEPTO D_TEXTO_CONCEPTO1
--           FROM    TEMP_FACTURA_X  T INNER JOIN (DETALLE_FACTURA_CONTRATO  D INNER JOIN FACTURA_CONTRATO  F ON (D.NUMERO_FACTURA = F.NUMERO) AND (D.TIPO_FACTURA = F.TIPO) AND (D.MES = F.MES) AND (D.ANO = F.ANO) AND (D.COMPANIA = F.COMPANIA)) ON (T.COMPANIA = D.COMPANIA) AND (T.ANO = D.ANO) AND (T.MES = D.MES) AND (T.TIPO_FACTURA = D.TIPO_FACTURA) AND (T.NUMERO_FACTURA = D.NUMERO_FACTURA) AND (T.CONCEPTO = D.CONCEPTO)
--            WHERE  D.COMPANIA = UN_COMPANIA 
--              AND  D.ANO      = UN_ANOFACTURAR 
--              AND  D.MES      = UN_MESFACTURAR 
--              AND  D.CONCEPTO =MI_CONCEPTOINTERESFINANCIACION 
--              AND  F.ESTADO_CONTRATO IN (2)
--          )
--              SET   D_TEXTO_CONCEPTO=D_TEXTO_CONCEPTO1;

--              COMMIT;







 END IF;


-- VERIFICA FINANCIACIONES POR TERCERO
--VerificarFinanciacionTer strCompania, Anofacturar, Mesfacturar

-- Actualiza estado de mes actual
BEGIN 
   UPDATE  MES
      SET  ESTADO_FACTURACION='F'
    WHERE  COMPANIA=UN_COMPANIA
      AND  ANO     =UN_ANOFACTURAR
      AND  NUMERO  =UN_MESFACTURAR;        
--Actualiza cierre de mes anterior

    UPDATE MES
    SET    ESTADO_FACTURACION='C'
    WHERE  COMPANIA=UN_COMPANIA 
      AND  ANO     =MI_ANOANTERIOR 
      AND  NUMERO  =MI_MESANTERIOR;

    MI_TF:=SYSDATE;
    DBMS_OUTPUT.PUT_LINE('FECHA Y HORA FINAL '||TO_CHAR(MI_TF,'DD/MM/RRRR HH:MI:SS'));
    DBMS_OUTPUT.PUT_LINE('PROCESO TERMINADO NORMALMENTE');

EXCEPTION WHEN OTHERS THEN       

      DBMS_OUTPUT.PUT_LINE('SE PRESENTO EL ERROR -> '|| SQLERRM);
      DBMS_OUTPUT.PUT_LINE('ERROR_BACKTRACE: ' || DBMS_UTILITY.FORMAT_ERROR_BACKTRACE);
      UN_SALIDA:='SE PRESENTO EL ERROR -> ' || SQLERRM;
      RETURN;
END;
--'Este  este ajuste se  valida cuando el  saldo a favor es menor al facturado

--Actualiza saldos de financiación y saldos a favor en factruración y detalle de facturación 
IF LENGTH(TRIM(MI_CONCEPTOINTERESFINANCIACION)) > 0 THEN

    DELETE TEMP_FACTURA_X;

   COMMIT;

  INSERT INTO TEMP_FACTURA_X
           (
          COMPANIA,
          ANO,
          MES,
          TIPO_FACTURA,
          NUMERO_FACTURA,
          CONCEPTO,
          TEXTO_CONCEPTO
            )
      SELECT     D.COMPANIA,
                 D.ANO,
                 D.MES,
                 D.TIPO_FACTURA,
                 D.NUMERO_FACTURA,
                 CONCEPTO,
                 'SALDO A FAVOR: $ ' ||TO_CHAR(CASE WHEN F.SALDOFINAN<F.VALOR_BASE + F.VALOR_IVA - F.VALOR_RTFTE THEN 0 ELSE F.SALDOFINAN END,'999,999,999.99') D_TEXTO_CONCEPTO1
          FROM   DETALLE_FACTURA_CONTRATO D INNER JOIN FACTURA_CONTRATO F ON (D.COMPANIA=F.COMPANIA) AND (D.ANO=F.ANO) AND (D.MES=F.MES)  AND (D.TIPO_FACTURA=F.TIPO ) AND (D.NUMERO_FACTURA=F.NUMERO) 
          WHERE  D.COMPANIA = UN_COMPANIA 
            AND  D.ANO      = UN_ANOFACTURAR 
            AND  D.MES      = UN_MESFACTURAR 
            AND  D.CONCEPTO =MI_CONCEPTOINTERESFINANCIACION 
            AND  F.ESTADO_CONTRATO IN (2); 



 --        UPDATE 
 --         (  SELECT  D.TEXTO_CONCEPTO D_TEXTO_CONCEPTO,
 --                    T.TEXTO_CONCEPTO D_TEXTO_CONCEPTO1
 --            FROM    TEMP_FACTURA_X  T INNER JOIN (DETALLE_FACTURA_CONTRATO  D INNER JOIN FACTURA_CONTRATO  F ON (D.NUMERO_FACTURA = F.NUMERO) AND (D.TIPO_FACTURA = F.TIPO) AND (D.MES = F.MES) AND (D.ANO = F.ANO) AND (D.COMPANIA = F.COMPANIA)) ON (T.COMPANIA = D.COMPANIA) AND (T.ANO = D.ANO) AND (T.MES = D.MES) AND (T.TIPO_FACTURA = D.TIPO_FACTURA) AND (T.NUMERO_FACTURA = D.NUMERO_FACTURA) AND (T.CONCEPTO = D.CONCEPTO)
 --             WHERE  D.COMPANIA = UN_COMPANIA 
 --               AND  D.ANO      = UN_ANOFACTURAR 
 --               AND  D.MES      = UN_MESFACTURAR 
 --               AND  D.CONCEPTO =MI_CONCEPTOINTERESFINANCIACION 
 --              AND  F.ESTADO_CONTRATO IN (2)
 --           )
 --               SET   D_TEXTO_CONCEPTO=D_TEXTO_CONCEPTO1;

 --               COMMIT;




 END IF;

-- VERIFICA FINANCIACIONES POR TERCERO
--VerificarFinanciacionTer strCompania, Anofacturar, Mesfacturar
-- Actualiza estado de mes actual
 --Actualiza el ENCABEZADO de lsa factura 

 DELETE TEMP_FACTURA_CONTRATO;
 COMMIT;
  DBMS_OUTPUT.PUT_LINE(' AQUI VOY '||SQL%ROWCOUNT);

  INSERT INTO TEMP_FACTURA_CONTRATO
        (
        COMPANIA,
        ANO,
        MES,
        TIPO,
        NUMERO,
        VALOR_BASE,
        VALOR_IVA,
        VALOR_RTFTE
       )
      SELECT 
                R3.COMPANIA,
                R3.ANO,
                R3.MES,
                R3.TIPO_FACTURA,
                R3.NUMERO_FACTURA,
                R3.TOTAL_BASE+R3.TOTAL_DEUDA_BASE   R3_TOTAL,
                R3.TOTAL_IVA+R3.TOTAL_DEUDA_IVA     R3_TOTAL_IVA,
                R3.TOTAL_RTEFTE+R3.TOTAL_DEUDA_RETE R3_TOTAL_RTEFTE
        FROM   FACTURA_CONTRATO F  INNER JOIN 
           (
             SELECT D.COMPANIA,
                    D.ANO,
                    D.MES,
                    D.TIPO_FACTURA,
                    D.NUMERO_FACTURA,
                    SUM(D.VALOR_BASE) TOTAL_BASE,
                    SUM(D.VALOR_IVA)  TOTAL_IVA, 
                    SUM(D.VALOR_RETEFUENTE) TOTAL_RTEFTE,
                    SUM(DEUDA_BASE) TOTAL_DEUDA_BASE,
                    SUM(DEUDA_IVA)  TOTAL_DEUDA_IVA,
                    SUM(DEUDA_RETE) TOTAL_DEUDA_RETE
             FROM   DETALLE_FACTURA_CONTRATO D 
             WHERE  D.COMPANIA= UN_COMPANIA 
               AND  D.ANO     = UN_ANOFACTURAR 
               AND  D.MES     = UN_MESFACTURAR 
               AND  D.TIPO_FACTURA=UN_TIPOFACTURA
             GROUP BY D.COMPANIA,
                      D.ANO,
                      D.MES,
                      D.TIPO_FACTURA,
                      D.NUMERO_FACTURA
           ) R3
            ON (R3.COMPANIA=F.COMPANIA) AND (R3.ANO=F.ANO) AND (R3.MES=F.MES) AND (R3.TIPO_FACTURA=F.TIPO) AND (R3.NUMERO_FACTURA=F.NUMERO)
        WHERE  R3.COMPANIA=UN_COMPANIA 
          AND  R3.ANO     =UN_ANOFACTURAR 
          AND  R3.MES     =UN_MESFACTURAR 
          AND  R3.TIPO_FACTURA=UN_TIPOFACTURA;
  COMMIT;        

--   UPDATE
--      (
--        SELECT  F.VALOR_BASE                        F_VALOR_BASE,
--                R3.VALOR_BASE                       R3_TOTAL,
--                F.VALOR_IVA                         F_VALOR_IVA,
--                R3.VALOR_IVA                        R3_TOTAL_IVA,
--                F.VALOR_RTFTE                       F_VALOR_RTFTE,
--                R3.VALOR_RTFTE                      R3_TOTAL_RTEFTE
--        FROM   FACTURA_CONTRATO F  INNER JOIN TEMP_FACTURA_CONTRATO R3
--            ON (F.COMPANIA=R3.COMPANIA) AND (F.ANO=R3.ANO) AND (F.MES=R3.MES) AND (F.TIPO=R3.TIPO) AND (F.NUMERO=R3.NUMERO)
--        WHERE  R3.COMPANIA=UN_COMPANIA 
--          AND  R3.ANO     =UN_ANOFACTURAR 
--          AND  R3.MES     =UN_MESFACTURAR 
--          AND  R3.TIPO=UN_TIPOFACTURA 
--        )
--    SET F_VALOR_BASE = R3_TOTAL,
--        F_VALOR_IVA  = R3_TOTAL_IVA,
--        F_VALOR_RTFTE= R3_TOTAL_RTEFTE;

--  COMMIT;


   UPDATE  MES
      SET  ESTADO_FACTURACION='F'
    WHERE  COMPANIA=UN_COMPANIA
      AND  ANO     =UN_ANOFACTURAR
      AND  NUMERO  =UN_MESFACTURAR;        
--Actualiza cierre de mes anterior

    UPDATE MES
    SET    ESTADO_FACTURACION='C'
    WHERE  COMPANIA=UN_COMPANIA 
      AND  ANO     =MI_ANOANTERIOR 
      AND  NUMERO  =MI_MESANTERIOR;

    MI_TF:=SYSDATE;
    DBMS_OUTPUT.PUT_LINE('FECHA Y HORA FINAL '||TO_CHAR(MI_TF,'DD/MM/RRRR HH:MI:SS'));
    DBMS_OUTPUT.PUT_LINE('PROCESO TERMINADO NORMALMENTE');
    UN_SALIDA:= 'PROCESO TERMINADO NORMALMENTE';
    RETURN;

EXCEPTION WHEN OTHERS THEN       

      DBMS_OUTPUT.PUT_LINE('SE PRESENTO EL ERROR -> '|| SQLERRM);
      DBMS_OUTPUT.PUT_LINE('ERROR_BACKTRACE: ' || DBMS_UTILITY.FORMAT_ERROR_BACKTRACE);
      UN_SALIDA := 'SE PRESENTO EL ERROR -> ' || DBMS_UTILITY.FORMAT_ERROR_BACKTRACE ||'->'||SQLERRM;
      RETURN;
END PR_CALCULO_FACTURACION_ABASTOS;
*/

FUNCTION FC_CALCULAR_VENCIMIENTO
/*
    NAME              : FC_CALCULAR_VENCIMIENTO    => CalcularFechaVencimiento
    AUTHORS           : MARIA CAMILA ROSERO
    AUTHOR MIGRACION  : 
    DATE MIGRADOR     : 11/01/2024
    TIME              : 
    DESCRIPTION       : FUNCION ENCARGADA DE CALCULAR LA FECHA DE VENCIMIENTO SEGUN EL CAMPO PERIOCIDAD DE LA TABLA TERCEROS
    PARAMETROS        : UN_COMPANIA      => COMPANIA DE INGRESO A LA COMPANIA 
                        UN_TERCERO    => TERCERO A FACTURAR
                        UN_FECHALSOLICITUD  => FECHA DE SOLICITUD DE LA CREACION DE LA FACTURA

    @NAME: calcularVencimiento                    

  */

(
  UN_COMPANIA        IN  PCK_SUBTIPOS.TI_COMPANIA,
  UN_TERCERO         IN  VARCHAR2,
  UN_FECHALSOLICITUD IN  VARCHAR2
  )

RETURN DATE AS 

MI_PARAMETRO                 PCK_SUBTIPOS.TI_PARAMETRO;
MI_FECHA_PRELIQUIDACION      DATE;
MI_PERIOCIDAD                PCK_SUBTIPOS.TI_ENTERO;
MI_MSGERROR                  PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN
    MI_PARAMETRO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA,
                                          UN_NOMBRE     => 'SF MANEJA TARIFA POR TERCERO',
                                          UN_MODULO     => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                          UN_FECHA_PAR  => SYSDATE);

    IF NVL(MI_PARAMETRO,'NO') = 'SI' THEN 

            SELECT PERIOCIDAD 
                INTO MI_PERIOCIDAD
                FROM TERCERO
            WHERE COMPANIA = UN_COMPANIA
             AND TERCERO.NIT = UN_TERCERO;

    END IF;            
              MI_FECHA_PRELIQUIDACION := TO_DATE(UN_FECHALSOLICITUD, 'DD/MM/YYYY') + MI_PERIOCIDAD;
    -- Imprimir por consola
    DBMS_OUTPUT.PUT_LINE('MI_FECHA_PRELIQUIDACION: ' || TO_CHAR(MI_FECHA_PRELIQUIDACION, 'DD/MM/YYYY HH24:MI:SS'));


      RETURN MI_FECHA_PRELIQUIDACION;
END FC_CALCULAR_VENCIMIENTO;
PROCEDURE PR_CALC_FACT_CONTR
    /*NAME              : PR_CALCULO_FACTURACION_ABASTOS
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : HENRRY PUERTO
    DATE MIGRADOR     : 02/10/2018
    TIME              : 09:04 AM
    DESCRIPTION       :
    --NAME: calculoFacturacionCorabastos
    --METHOD: POST*/
(
 UN_COMPANIA         IN VARCHAR2 DEFAULT '001',
 UN_ANOFACTURAR      IN NUMBER   DEFAULT 2017,
 UN_MESFACTURAR      IN NUMBER   DEFAULT 9,
 UN_TIPOFACTURA      IN VARCHAR2 DEFAULT 'TDV',
 UN_FECHAFACTURACION IN DATE,
 UN_FECHALIMITE      IN DATE,
 UN_FECHALIMITE1     IN DATE,
 UN_CCI              IN VARCHAR2,
 UN_CCF              IN VARCHAR2,
 UN_DESCRIPCION      IN VARCHAR2,
 UN_STRUSUARIO       IN VARCHAR2,
 UN_FACTURACIONTOTAL IN NUMBER DEFAULT 0,
 UN_CHECKDESCUENTO   IN VARCHAR2,
 UN_FECHALIMITEDSCTO IN DATE
 )
 IS
    MI_TABLA           VARCHAR2(20);
    MI_CAMPOS          VARCHAR2(2000);
    MI_VALORES         VARCHAR2(2000);
    MI_CONSECUTIVO     VARCHAR2(20);
    MI_NUMERO          VARCHAR2(20);
    MI_MSGERROR        PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CAMPOS_DETALLE  VARCHAR2(2000);
    MI_VALORES_DETALLE VARCHAR2(2000);
    MI_CONSEC_DETALLE  VARCHAR2(10);
    MI_TABLA_DETALLE   VARCHAR2(20);
    MI_VALOR_FINAL     NUMBER(20);
    MI_PARAM_DESCUENTO NUMBER(3);
 BEGIN
      MI_TABLA     := 'SF_OBJETO_COBRO';
      MI_CAMPOS    := 'COMPANIA,
                       ANO,                      
                       TIPOCOBRO,
                       CODIGO_COBRO, 
                       TERCERO,
                       SUCURSAL,
                       VALOR_TOTAL,
                       FECHA_SOLICITUD,
                       FECHA_VENCIMIENTO
                       ,TXTCONTRATO
                       ,OBSERVACIONES
                       ,APLICA_DSCTO
                       ,FECHAVENCIDSCTO';
     MI_CONSECUTIVO:=0;
     -- OBTENEMOS EL CONSECUTIVO PROXIMO sf_objeto_cobro.
     SELECT TO_CHAR(UN_FECHAFACTURACION,'yyyy') INTO MI_NUMERO FROM DUAL;
     MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO('SF_OBJETO_COBRO', 'SF_OBJETO_COBRO.COMPANIA ='''
                        ||UN_COMPANIA||''''
                        ||' AND SF_OBJETO_COBRO.ANO = '||UN_ANOFACTURAR
                        ||' AND SF_OBJETO_COBRO.TIPOCOBRO = '''
                        ||UN_TIPOFACTURA||''''
                        , 'CODIGO_COBRO', MI_NUMERO||'000001');
      FOR MI_RS IN (SELECT DISTINCT SF_CONTRATOS.TERCERO, SF_CONTRATOS.SUCURSAL, SF_CONTRATOS.TOTAL_TCONTRATADO, SF_CONTRATOS.ESTADO, SF_CONTRATOS.ANO, SF_CONTRATOS.NUMERO, SF_CONTRATOS.OBJETO_CONTRATO 
        FROM SF_CONTRATOS LEFT JOIN SF_OBJETO_COBRO ON 
                        (SF_OBJETO_COBRO.COMPANIA = SF_CONTRATOS.COMPANIA 
                            AND SF_OBJETO_COBRO.TIPOCOBRO = SF_CONTRATOS.TIPO 
                                AND SF_OBJETO_COBRO.TXTCONTRATO = SF_CONTRATOS.NUMERO)
                                LEFT JOIN SF_DETALLE_CONTRATO ON (SF_DETALLE_CONTRATO.COMPANIA = SF_CONTRATOS.COMPANIA 
                                    AND SF_DETALLE_CONTRATO.TIPO = SF_CONTRATOS.TIPO 
                                    AND SF_DETALLE_CONTRATO.NUMERO = SF_CONTRATOS.NUMERO)
                                        WHERE SF_OBJETO_COBRO.TXTCONTRATO IS NULL AND ESTADO IN (2) 
                                        AND SF_CONTRATOS.TIPO = UN_TIPOFACTURA 
                                        AND SF_CONTRATOS.COMPANIA = UN_COMPANIA
                                        AND SF_DETALLE_CONTRATO.INMUEBLE BETWEEN UN_CCI AND UN_CCF )
            LOOP
                BEGIN
                      IF(UN_CHECKDESCUENTO = 'SI') THEN
                        MI_PARAM_DESCUENTO  := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                             UN_NOMBRE    => 'SF DESCUENTO FACTURA',
                                             UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                             UN_FECHA_PAR => SYSDATE,
                                             UN_IND_MAYUS => 0);
                        MI_VALOR_FINAL := MI_RS.TOTAL_TCONTRATADO - (MI_RS.TOTAL_TCONTRATADO*(TO_NUMBER(MI_PARAM_DESCUENTO)/100)); 
                      ELSE
                        MI_VALOR_FINAL := MI_RS.TOTAL_TCONTRATADO;
                      END IF;
                      MI_VALORES := ' '''||UN_COMPANIA||''','||UN_ANOFACTURAR||','''||UN_TIPOFACTURA||''','||MI_CONSECUTIVO||','''||MI_RS.TERCERO||''','''||MI_RS.SUCURSAL||''','||MI_VALOR_FINAL||','''||UN_FECHAFACTURACION||''','''||UN_FECHALIMITE||''','||MI_RS.NUMERO||','''||UN_DESCRIPCION||' - Factura de contrato número: '||MI_RS.NUMERO||''','''||(CASE UN_CHECKDESCUENTO WHEN 'SI' THEN -1 ELSE 0 END)||''','''||UN_FECHALIMITEDSCTO||'''';
                      PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                                              UN_ACCION  => 'I',
                                                                              UN_CAMPOS  => MI_CAMPOS,
                                                                              UN_VALORES => MI_VALORES);
                                                                              
                     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                          MI_MSGERROR(1).CLAVE := 'CODIGO_COBRO';
                          MI_MSGERROR(1).VALOR := MI_CONSECUTIVO;
                         RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                     END;
               MI_CONSEC_DETALLE := 1;
               FOR MI_RS_DT IN (SELECT CONCEPTO, CANTIDAD, VALOR_BASE, VALOR_IVA, VALOR_UNITARIO, VALOR_NETO, TERCERO, '001' SUCURSAL FROM SF_DETALLE_CONTRATO WHERE COMPANIA = UN_COMPANIA AND NUMERO = MI_RS.NUMERO AND TIPO = UN_TIPOFACTURA) LOOP
                    BEGIN
                    MI_TABLA_DETALLE     := 'SF_DETALLE_COBRO';
                    MI_CAMPOS_DETALLE    := 'COMPANIA,
                                             ANO,
                                             TIPOCOBRO,
                                             CODIGO_COBRO,
                                             CONCEPTO,
                                             VALOR_NETO,
                                             VALOR_IVA,
                                             FECHA_INICIO_COBRO,
                                             FECHA_FIN_COBRO,
                                             TERCERO,
                                             CONSECUTIVO,
                                             CANTIDAD,
                                             VALOR_BASE,
                                             VALOR_UNITARIO,
                                             SUCURSAL';
                    MI_VALORES_DETALLE := ' '''||UN_COMPANIA||''','||UN_ANOFACTURAR||','''||UN_TIPOFACTURA||''','||MI_CONSECUTIVO||','''||MI_RS_DT.CONCEPTO||''','''||MI_RS_DT.VALOR_NETO||''','''||MI_RS_DT.VALOR_IVA||''','''||UN_FECHAFACTURACION||''','''||UN_FECHALIMITE||''','''||MI_RS_DT.TERCERO||''','||MI_CONSEC_DETALLE||','''||MI_RS_DT.CANTIDAD||''','''||(MI_RS_DT.VALOR_UNITARIO*MI_RS_DT.CANTIDAD)||''','''||MI_RS_DT.VALOR_UNITARIO||''','''||MI_RS_DT.SUCURSAL||'''';
                    PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA_DETALLE,
                                                                              UN_ACCION  => 'I',
                                                                              UN_CAMPOS  => MI_CAMPOS_DETALLE,
                                                                              UN_VALORES => MI_VALORES_DETALLE);
                                                                              
                     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                          MI_MSGERROR(1).CLAVE := 'CONSECUTIVO';
                          MI_MSGERROR(1).VALOR := MI_CONSEC_DETALLE;
                         RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                     END;
                    DBMS_OUTPUT.PUT_LINE('');
                    MI_CONSEC_DETALLE := MI_CONSEC_DETALLE + 1;
               END LOOP;
               MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
    END LOOP;
END PR_CALC_FACT_CONTR;

PROCEDURE PR_CALCULO_FACT_SINCONTRATO
    /*NAME            : PR_CALCULO_FACT_SINCONTRATO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MIGRADOR     : 04/11/2025
    TIME              : 09:04 AM
    DESCRIPTION       :
    --NAME: calculoFacturacionSinContrato
    --METHOD: POST*/
(
 UN_COMPANIA         IN VARCHAR2,
 UN_ANOFACTURAR      IN NUMBER   ,
 UN_MESFACTURAR      IN NUMBER,
 UN_TIPOFACTURA      IN VARCHAR2,
 UN_FECHAFACTURACION IN DATE,
 UN_FECHALIMITE      IN DATE,
 UN_FECHALIMITE1     IN DATE,
 UN_UBICACIONINI     IN VARCHAR2,
 UN_UBICACIONFIN     IN VARCHAR2,
 UN_TERCEROINI       IN VARCHAR2,
 UN_TERCEROFIN       IN VARCHAR2,
 UN_INMUEBLEINI      IN VARCHAR2,
 UN_INMUEBLEFIN      IN VARCHAR2,
 UN_DESCRIPCION      IN VARCHAR2,
 UN_STRUSUARIO       IN VARCHAR2,
 UN_FACTURACIONTOTAL IN NUMBER DEFAULT 0
 )
 IS
    MI_TABLA              VARCHAR2(20);
    MI_CAMPOS             VARCHAR2(2000);
    MI_VALORES            VARCHAR2(2000);
    MI_CONSECUTIVO        VARCHAR2(20);
    MI_NUMERO             VARCHAR2(20);
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CAMPOS_DETALLE     VARCHAR2(2000);
    MI_VALORES_DETALLE    VARCHAR2(2000);
    MI_CONSEC_DETALLE     VARCHAR2(10);
    MI_TABLA_DETALLE      VARCHAR2(20);
    MI_VALOR_FINAL        NUMBER(20) := 0;
    MI_VALOR_IVA          NUMBER(20) := 0;                      
    MI_VALOR_RETEFUENTE   NUMBER(20) := 0;                     
    MI_VALOR_DESCUENTO    NUMBER(20) := 0;                      
    MI_VALOR_ICA          NUMBER(20) := 0; 
    MI_VALOR_AUTORENTA    NUMBER(20) := 0;                      
    MI_VALOR_AUTOICA      NUMBER(20) := 0;
    MI_VALOR_RETEIVA      NUMBER(20) := 0;
 BEGIN
      MI_TABLA     := 'SF_OBJETO_COBRO';
      MI_CAMPOS    := 'COMPANIA,
                       ANO,                      
                       TIPOCOBRO,
                       CODIGO_COBRO, 
                       TERCERO,
                       SUCURSAL,
                       VALOR_TOTAL,
                       FECHA_SOLICITUD,
                       FECHA_VENCIMIENTO
                       ,OBSERVACIONES';
     MI_CONSECUTIVO:=0;
     -- OBTENEMOS EL CONSECUTIVO PROXIMO SF_OBJETO_COBRO.
     SELECT TO_CHAR(UN_FECHAFACTURACION,'yyyy') INTO MI_NUMERO FROM DUAL;
     MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO('SF_OBJETO_COBRO', 'SF_OBJETO_COBRO.COMPANIA ='''
                        ||UN_COMPANIA||''''
                        ||' AND SF_OBJETO_COBRO.ANO = '||UN_ANOFACTURAR
                        ||' AND SF_OBJETO_COBRO.TIPOCOBRO = '''
                        ||UN_TIPOFACTURA||''''
                        , 'CODIGO_COBRO', MI_NUMERO||'000001');
      FOR MI_RS IN (
        SELECT CODIGOINMUEBLE, DENOMINACION, CONCEPTO1, CONCEPTO2, CONCEPTO3, CONCEPTO4, TIPO, AREA, TERCERO, SUCURSAL
        FROM INMUEBLE_FACTURACION
        WHERE COMPANIA = UN_COMPANIA
          AND UBICACION BETWEEN UN_UBICACIONINI AND UN_UBICACIONFIN
          AND TERCERO  BETWEEN  UN_TERCEROINI AND UN_TERCEROFIN
          AND CODIGOINMUEBLE  BETWEEN  UN_INMUEBLEINI AND UN_INMUEBLEFIN)

            LOOP
                BEGIN                      
                      MI_VALORES := ' '''||UN_COMPANIA||''','||UN_ANOFACTURAR||','''||UN_TIPOFACTURA||''','||MI_CONSECUTIVO||','''||MI_RS.TERCERO||''','''||MI_RS.SUCURSAL||''','||MI_VALOR_FINAL||','''||UN_FECHAFACTURACION||''','''||UN_FECHALIMITE||''','''||UN_DESCRIPCION||' - Factura de inmueble número: '||MI_RS.CODIGOINMUEBLE||'''';
                      PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                                              UN_ACCION  => 'I',
                                                                              UN_CAMPOS  => MI_CAMPOS,
                                                                              UN_VALORES => MI_VALORES);
                                                                              
                     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                          MI_MSGERROR(1).CLAVE := 'CODIGO_COBRO';
                          MI_MSGERROR(1).VALOR := MI_CONSECUTIVO;
                         RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                     END;
               MI_CONSEC_DETALLE := 1;
               FOR MI_RS_DT IN (
                SELECT CODIGO, VALOR_BASE,APLICAIVA,PORCENTAJEIVA,APLICARETEFUENTE, PORCENTAJERETEFUENTE, APLICADESCUENTO, PORCENTAJEDESCUENTO,
                       APLICAICA, PORCENTAJEICA, APLICAAUTOICA, PORCENTAJEAUTOICA, APLICAAUTORENTA, PORCENTAJEAUTORENTA, APLICARETEIVA, PORCENTAJERETEIVA
                FROM SF_CONCEPTOS
                WHERE COMPANIA = UN_COMPANIA
                AND ANO = UN_ANOFACTURAR
                AND TIPOCOBRO = UN_TIPOFACTURA
                AND CODIGO IN (MI_RS.CONCEPTO1,MI_RS.CONCEPTO2,MI_RS.CONCEPTO3,MI_RS.CONCEPTO4)) 
               LOOP
                    BEGIN
                    IF(MI_RS_DT.APLICAIVA <> 0)THEN
                      MI_VALOR_IVA := (MI_RS_DT.VALOR_BASE*MI_RS.AREA)*(MI_RS_DT.PORCENTAJEIVA/100); 
                    END IF;  
                    IF(MI_RS_DT.APLICARETEFUENTE <> 0)THEN
                      MI_VALOR_RETEFUENTE := (MI_RS_DT.VALOR_BASE*MI_RS.AREA)*(MI_RS_DT.PORCENTAJERETEFUENTE/100); 
                    END IF;                   
                    IF(MI_RS_DT.APLICADESCUENTO <> 0)THEN
                      MI_VALOR_DESCUENTO := (MI_RS_DT.VALOR_BASE*MI_RS.AREA)*(MI_RS_DT.PORCENTAJEDESCUENTO/100); 
                    END IF;                      
                    IF(MI_RS_DT.APLICAICA <> 0)THEN
                      MI_VALOR_ICA := (MI_RS_DT.VALOR_BASE*MI_RS.AREA)*(MI_RS_DT.PORCENTAJEICA/100); 
                    END IF;                       
                    IF(MI_RS_DT.APLICAAUTORENTA <> 0)THEN
                      MI_VALOR_AUTORENTA := (MI_RS_DT.VALOR_BASE*MI_RS.AREA)*(MI_RS_DT.PORCENTAJEAUTORENTA/100); 
                    END IF;                     
                    IF(MI_RS_DT.APLICAAUTOICA <> 0)THEN
                      MI_VALOR_AUTOICA := (MI_RS_DT.VALOR_BASE*MI_RS.AREA)*(MI_RS_DT.PORCENTAJEAUTOICA/100); 
                    END IF;                     
                    IF(MI_RS_DT.APLICARETEIVA <> 0)THEN
                      MI_VALOR_RETEIVA := (MI_RS_DT.VALOR_BASE*MI_RS.AREA)*(MI_RS_DT.PORCENTAJERETEIVA/100); 
                    END IF; 

                    MI_TABLA_DETALLE     := 'SF_DETALLE_COBRO';
                    MI_CAMPOS_DETALLE    := 'COMPANIA,
                                             ANO,
                                             TIPOCOBRO,
                                             CODIGO_COBRO,
                                             CONCEPTO,
                                             VALOR_BASE,                     
                                             VALOR_IVA,                      
                                             VALOR_RETEFUENTE,                     
                                             VALOR_DESCUENTO,                      
                                             VALOR_ICA,                      
                                             VALOR_NETO,
                                             FECHA_INICIO_COBRO,
                                             FECHA_FIN_COBRO,
                                             TERCERO,                     
                                             SUCURSAL,
                                             CONSECUTIVO,
                                             CANTIDAD,
                                             VALOR_UNITARIO,
                                             VALOR_AUTORENTA,                      
                                             VALOR_AUTOICA,
                                             VALOR_RETEIVA';
                    MI_VALORES_DETALLE := ' '''||UN_COMPANIA||''','||UN_ANOFACTURAR||','''||UN_TIPOFACTURA||''','||MI_CONSECUTIVO||','''||MI_RS_DT.CODIGO||''',
                    '''||MI_RS_DT.VALOR_BASE||''','''||MI_VALOR_IVA||''','''||MI_VALOR_RETEFUENTE||''','''||MI_VALOR_DESCUENTO||''','''||MI_VALOR_ICA||''',
                    '''||MI_RS_DT.VALOR_BASE*MI_RS.AREA||''','''||UN_FECHAFACTURACION||''','''||UN_FECHALIMITE||''','''||MI_RS.TERCERO||''','''||MI_RS.SUCURSAL||''',
                    '||MI_CONSEC_DETALLE||','''||MI_RS.AREA||''','''||(MI_RS_DT.VALOR_BASE)||''','''||MI_VALOR_AUTORENTA||''','''||MI_VALOR_AUTOICA||''',
                    '''||MI_VALOR_RETEIVA||'''';
                    PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA_DETALLE,
                                                                              UN_ACCION  => 'I',
                                                                              UN_CAMPOS  => MI_CAMPOS_DETALLE,
                                                                              UN_VALORES => MI_VALORES_DETALLE);
                                                                              
                     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                          MI_MSGERROR(1).CLAVE := 'CONSECUTIVO';
                          MI_MSGERROR(1).VALOR := MI_CONSEC_DETALLE;
                         RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                     END;
                    DBMS_OUTPUT.PUT_LINE('');
                    MI_CONSEC_DETALLE := MI_CONSEC_DETALLE + 1;
               END LOOP;
               MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
    END LOOP;
END PR_CALCULO_FACT_SINCONTRATO;
END PCK_FACT_GENERAL_COM2;