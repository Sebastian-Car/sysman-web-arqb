create or replace PACKAGE BODY PCK_FACT_GENERAL_ABPARCIALES AS

FUNCTION FC_VERIFICAR_ABACTIVOS(
/*
      NAME              : FC_VERIFICAR_ABACTIVOS 
      AUTHORS           : STEFANINI SYSMAN  SAS
      AUTHOR MIGRACION  : SANDRA MILENA DAZA LEGUIZAMON
      DATE CREATED      : 20/09/2021 
      TIME              : 11:45 AM
      SOURCE MODULE     : 
      DESCRIPTION       : 
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : Funcion que valida si una factura tiene un abono por pago parcial activo, esto permite que el usuario 
                            desea mantenerlo activo o anularlo para generar uno nuevo pago parcial 

    @NAME:   verificarAbonoActivo
    @METHOD: GET
*/
  UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANO                IN PCK_SUBTIPOS.TI_ENTERO,
  UN_TIPOCOBRO          IN PCK_SUBTIPOS.TI_TIPO_COBRO,
  UN_FACTURA            IN SF_FACTURA.NUMERO_FACTURA%TYPE
)RETURN PCK_SUBTIPOS.TI_LOGICO  
    AS
    MI_PAGOSPARCIALES  PCK_SUBTIPOS.TI_LOGICO;
    BEGIN
        BEGIN
            SELECT  PAGOS_PARCIALES 
            INTO    MI_PAGOSPARCIALES 
            FROM    SF_FACTURA
            WHERE   COMPANIA        = UN_COMPANIA
            AND     ANO             = UN_ANO
            AND     TIPO_FACTURA    = UN_TIPOCOBRO
            AND     NUMERO_FACTURA  = UN_FACTURA;
            
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_PAGOSPARCIALES := 0;
        END;    
        IF MI_PAGOSPARCIALES = 0 THEN
            RETURN 0;
        ELSE
            -- si la factura tiene activo el indicador de pagos parciales se debe consultar si tiene pagos facturados activos,
            -- es decir, que no han sido recaudados. 
            SELECT  COUNT ('X')
            INTO    MI_PAGOSPARCIALES 
            FROM    SF_ABONOS A
                INNER JOIN SF_DETALLE_ABONO D
                ON  A.COMPANIA = D.COMPANIA
                AND A.TIPO = D.TIPO_ABONO
                AND A.CODIGO = D.CODIGO_ABONO
            WHERE   A.COMPANIA  = UN_COMPANIA
            AND     A.TIPO      = UN_TIPOCOBRO 
            AND     A.TIPO_FACT = UN_TIPOCOBRO
            AND     A.NRO_FACT  = UN_FACTURA
            AND     D.ESTADO    = 'A';
            
            IF MI_PAGOSPARCIALES = 0 THEN
                RETURN 0;
            ELSE
                RETURN -1;
            END IF;
        END IF;
        
END FC_VERIFICAR_ABACTIVOS;


PROCEDURE PR_REGISTRAR_PAGOPARCIAL (
/*
      NAME              : PR_REGISTRAR_PAGOPARCIAL 
      AUTHORS           : STEFANINI SYSMAN  SAS
      AUTHOR MIGRACION  : SANDRA MILENA DAZA LEGUIZAMON
      DATE CREATED      : 20/09/2021 
      TIME              : 03:45 PM
      SOURCE MODULE     : 
      DESCRIPTION       : 
      MODIFIER          : MARIA ALEJANDRA PEREZ SALAZAR
      DATE MODIFIED     : 18/11/2022
      TIME MODIFIED     : 03:00 PM
      MODIFICATIONS     : - Registrar el pago parcial a una factura generada 
                          - Se modifica el registro de pago parcial para involucrar el proceso persuasivo ticket 7714737   

    @NAME:   registrarPagoParcial
    @METHOD: GET
*/
  UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANO                IN PCK_SUBTIPOS.TI_ENTERO,
  UN_TIPOCOBRO          IN PCK_SUBTIPOS.TI_TIPO_COBRO,
  UN_FACTURA            IN SF_FACTURA.NUMERO_FACTURA%TYPE,
  UN_FECHACORTE         IN DATE,
  UN_VLRINTERESES       IN PCK_SUBTIPOS.TI_DOBLE, 
  UN_DIASMORA           IN PCK_SUBTIPOS.TI_ENTERO,
  UN_TASA               IN PCK_SUBTIPOS.TI_DOBLE,
  UN_VLRABONO           IN PCK_SUBTIPOS.TI_DOBLE,
  UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO,
  UN_FECHARESOLUCION    IN SF_FACTURA.FECHA_RESOLUCION%TYPE,
  UN_FECHAEJECUTORA     IN SF_FACTURA.FECHA_EJECUTORA%TYPE,
  UN_RESOLUCION         IN SF_FACTURA.NUMERO_RESOLUCION%TYPE,
  UN_EXPEDIENTE         IN SF_FACTURA.NUMERO_EXPEDIENTE%TYPE  
)
AS
    MI_PAGOSPARCIALES   PCK_SUBTIPOS.TI_LOGICO;
    MI_TERCERO          SF_FACTURA.TERCERO%TYPE;
    MI_SUCURSAL         SF_FACTURA.SUCURSAL%TYPE;
    MI_CENCOSTO         SF_FACTURA.CENTRO_COSTO%TYPE;
    MI_AUXILIAR         SF_FACTURA.AUXILIAR%TYPE;
    MI_NITTERFACTURADO  SF_FACTURA.NIT_TERCEROFACTURADO%TYPE;
    MI_SUCTERFACTURADO  SF_FACTURA.SUCURSAL_TERCEROFACTURADO%TYPE;
    MI_NOMTERFACTURADO  SF_FACTURA.NOMBRE_TERCEROFACTURADO%TYPE;
    MI_EXISTEFRA        PCK_SUBTIPOS.TI_LOGICO;
    MI_EXISTEPAGO       PCK_SUBTIPOS.TI_ENTERO;
    MI_TIPOABONO        SF_ABONOS.TIPO%TYPE;
    MI_NROABONO         SF_ABONOS.CODIGO%TYPE;
    MI_CAPITAL          PCK_SUBTIPOS.TI_DOBLE;
    MI_INTERES          PCK_SUBTIPOS.TI_DOBLE;
    MI_INTERESANT       PCK_SUBTIPOS.TI_DOBLE;
    MI_TOTALES          SF_DETALLE_FACTURA.VALOR_NETO%TYPE;
    MI_VALORES          PCK_SUBTIPOS.TI_CAMPOS;
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
    MI_ACME             PCK_SUBTIPOS.TI_ENTERO;
    MI_ERROR            PCK_SUBTIPOS.TI_CLAVEVALOR;  
    MI_SALDOCAPITAL     PCK_SUBTIPOS.TI_DOBLE;
    MI_SALDOINTERES     PCK_SUBTIPOS.TI_DOBLE;
    MI_NROCUOTA         PCK_SUBTIPOS.TI_ENTERO;
    MI_CPTOINTERES      SF_CONCEPTOS.CODIGO%TYPE;
    MI_SALDOABONO       PCK_SUBTIPOS.TI_DOBLE;
    MI_FRAINTERES       SF_FACTURA.NUMERO_FACTURA%TYPE;
    MI_CUOTASPAGAS      PCK_SUBTIPOS.TI_ENTERO;
    MI_INTERES_EXPEM    PCK_SUBTIPOS.TI_DOBLE;
    MI_CPTOINTERES_EX   SF_CONCEPTOS.CODIGO%TYPE;
    MI_VLR_INTERES      PCK_SUBTIPOS.TI_DOBLE;
    MI_CPTOCAPITAL      SF_CONCEPTOS.CODIGO%TYPE;
    MI_ANO              SF_DETALLE_CUOTAABONO.ANO%TYPE;
BEGIN
    EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS=''.,''';

    IF UN_VLRABONO > 0 THEN

    -- Verificar si la factura tiene activo el indicador de pagos parciales o es el primer pago a registrar 
    BEGIN
        BEGIN
            SELECT  PAGOS_PARCIALES, TERCERO, SUCURSAL, CENTRO_COSTO, AUXILIAR, NIT_TERCEROFACTURADO, SUCURSAL_TERCEROFACTURADO, NOMBRE_TERCEROFACTURADO
            INTO    MI_PAGOSPARCIALES , MI_TERCERO, MI_SUCURSAL, MI_CENCOSTO, MI_AUXILIAR, MI_NITTERFACTURADO, MI_SUCTERFACTURADO, MI_NOMTERFACTURADO
            FROM    SF_FACTURA
            WHERE   COMPANIA        = UN_COMPANIA
            AND     ANO             = UN_ANO
            AND     TIPO_FACTURA    = UN_TIPOCOBRO
            AND     NUMERO_FACTURA  = UN_FACTURA;
            
        EXCEPTION WHEN NO_DATA_FOUND THEN
               RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
        END;            
        
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
        MI_ERROR(1).CLAVE := 'FACTURA';
        MI_ERROR(1).VALOR := UN_TIPOCOBRO || ' - ' || UN_FACTURA;
             
        PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_TABLAERROR => 'SF_ABONOS',
            UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_FACTREGISTRADA,
            UN_REEMPLAZOS => MI_ERROR
        );
    END;

    MI_EXISTEFRA := -1;
    
    --ANULAR FACTURAS PARCIALES QUE NO REGISTRAN PAGO
    
     PR_ANULARFRA_PAGOPARCIAL (
            UN_COMPANIA           => UN_COMPANIA,
            UN_TIPOCOBRO          => UN_TIPOCOBRO,
            UN_FACTURA            => UN_FACTURA,
            UN_USUARIO            => UN_USUARIO);
            
    -- SE DEBE CREAR LA FACTURA POR EL CONCEPTO DE INTERES 
    MI_CPTOINTERES := PCK_SYSMAN_UTL.FC_VALIDARPARAMETRO
                             (UN_COMPANIA  => UN_COMPANIA
                             ,UN_PARAMETRO => 'SF CONCEPTO INTERESES PAGOS PARCIALES'
                             ,UN_MODULO    => PCK_DATOS.MODULOFACTURACIONGENERAL );
    MI_CPTOINTERES_EX := PCK_SYSMAN_UTL.FC_VALIDARPARAMETRO
                             (UN_COMPANIA  => UN_COMPANIA
                             ,UN_PARAMETRO => 'SF CONCEPTO INTERESES EXTEMPORANEOS PAGOS PARCIALES'
                             ,UN_MODULO    => PCK_DATOS.MODULOFACTURACIONGENERAL );
    /*MI_TASA := PCK_FACT_GENERAL.FC_TASAANUAL ( 
                            UN_COMPANIA     =>UN_COMPANIA,
                            UN_ANIO         =>UN_ANO, 
                            UN_TIPOCOBRO    =>UN_TIPOCOBRO,
                            UN_FECHACORTE   =>UN_FECHACORTE);
   */
    IF MI_PAGOSPARCIALES = 0  THEN 
        -- SE DEBE CREAR EL HEADER DEL ABONO        
        BEGIN
            SELECT  SUM(DECODE(SF_CONCEPTOS.TIPODECONCEPTO,'C',SF_DETALLE_FACTURA.VALOR_BASE+SF_DETALLE_FACTURA.VALOR_IVA-SF_DETALLE_FACTURA.VALOR_RETEFUENTE-SF_DETALLE_FACTURA.VALOR_DESCUENTO+SF_DETALLE_FACTURA.VALOR_ICA,0)) CAPITAL,
                    SUM(DECODE(SF_CONCEPTOS.TIPODECONCEPTO,'I',SF_DETALLE_FACTURA.VALOR_BASE+SF_DETALLE_FACTURA.VALOR_IVA-SF_DETALLE_FACTURA.VALOR_RETEFUENTE-SF_DETALLE_FACTURA.VALOR_DESCUENTO+SF_DETALLE_FACTURA.VALOR_ICA,0)) INTERES,
                    SUM(SF_DETALLE_FACTURA.VALOR_NETO) TOTALS INTO
                    MI_CAPITAL,
                    MI_INTERES,
                    MI_TOTALES          
            FROM SF_DETALLE_FACTURA
                INNER JOIN SF_CONCEPTOS
                ON SF_DETALLE_FACTURA.COMPANIA     = SF_CONCEPTOS.COMPANIA
                AND SF_DETALLE_FACTURA.TIPOCOBRO   = SF_CONCEPTOS.TIPOCOBRO
                AND SF_DETALLE_FACTURA.ANO         = SF_CONCEPTOS.ANO
                AND SF_DETALLE_FACTURA.CONCEPTO    = SF_CONCEPTOS.CODIGO
            WHERE   SF_DETALLE_FACTURA.COMPANIA    = UN_COMPANIA 
            AND     SF_DETALLE_FACTURA.TIPO_FACTURA = UN_TIPOCOBRO
            AND     SF_DETALLE_FACTURA.FACTURA      = UN_FACTURA; 
          
        EXCEPTION
               WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
                    MI_EXISTEFRA := 0;          
        END;
        
        IF MI_EXISTEFRA <> 0 THEN
            BEGIN
                
                IF (MI_CAPITAL + MI_INTERES + UN_VLRINTERESES ) < UN_VLRABONO THEN
                    RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                END IF;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                    MI_ERROR(1).CLAVE := 'DEUDA';
                    MI_ERROR(1).VALOR := MI_CAPITAL + MI_INTERES + UN_VLRINTERESES;
                         
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE,
                        UN_TABLAERROR => 'SF_ABONOS',
                        UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_VLRABONOSUPERIOR,
                    UN_REEMPLAZOS => MI_ERROR
                    );
            END ;
            MI_NROABONO:= PCK_FACT_GENERAL_COM4.FC_ENUMERARABONO(UN_COMPANIA  => UN_COMPANIA,
                                                                 UN_TIPOCOBRO => UN_TIPOCOBRO);
            -- CREAR EL HEADER DEL ABONO, EN ESTE SE RELACIONA LA FACTURA QUE ES OBJETO DE PAGOS PARCIALES 
            BEGIN
                BEGIN
                    MI_CAMPOS:='COMPANIA,
                               TIPO,
                               CODIGO,
                               TERCERO,
                               SUCURSAL,
                               CENTRO_COSTO,
                               AUXILIAR,
                               DEUDA_CAPITAL,
                               DEUDA_INTERES,
                               DEUDA_TOTAL,
                               CUOTAS,
                               FECHA_INICIO,
                               ESTADO,
                               CUOTA_INICIAL,
                               TASA_INTERES,
                               TIPO_FACT,
                               NRO_FACT,
                               INTERES_ACT,
                               CREATED_BY,
                               DATE_CREATED';
                    MI_VALORES:=''''||UN_COMPANIA||''',
                                '''||UN_TIPOCOBRO||''',
                                '||MI_NROABONO||',
                                '''||MI_TERCERO||''',
                                '''||MI_SUCURSAL||''',
                                '''||MI_CENCOSTO||''',
                                '''||MI_AUXILIAR||''',
                                '||NVL(MI_CAPITAL,0)||',
                                '||NVL(UN_VLRINTERESES,0)||',
                                '||NVL((MI_CAPITAL+UN_VLRINTERESES),0)||',
                                0,
                                SYSDATE,
                                ''A'',
                                0,
                                '||UN_TASA||',
                                '''||UN_TIPOCOBRO||''',
                                '||UN_FACTURA||',
                                '||NVL(UN_VLRINTERESES,0)||',
                                '''||UN_USUARIO||''',
                                SYSDATE';

                    MI_ACME:=PCK_DATOS.FC_ACME(UN_TABLA  =>'SF_ABONOS',
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
                    MI_ERROR(1).VALOR := 'Creaci�n del pago parcial';
                                    
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE,
                        UN_TABLAERROR => 'SF_ABONOS',
                        UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSERTPAGPARCIAL,
                        UN_REEMPLAZOS => MI_ERROR
                    );
            END;          
        END IF;
        MI_SALDOCAPITAL := MI_CAPITAL;
        MI_SALDOINTERES := MI_INTERES + UN_VLRINTERESES;
        MI_NROCUOTA := 1;
        
        MI_CAMPOS := 'PAGOS_PARCIALES = -1, TIPO_ABONO = '''|| UN_TIPOCOBRO||''', NRO_ABONO = '||MI_NROABONO||
        ', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE, FECHA_RESOLUCION = '''||UN_FECHARESOLUCION||''',
        FECHA_EJECUTORA = '''||UN_FECHAEJECUTORA||''',NUMERO_RESOLUCION = '''||UN_RESOLUCION||''', NUMERO_EXPEDIENTE = '''||UN_EXPEDIENTE||'''
        , FECHA_CORTE = '''||UN_FECHACORTE||'''';
        MI_CONDICION := 'COMPANIA       = '''||UN_COMPANIA ||'''
                     AND ANO   = '''||UN_ANO||'''
                     AND TIPO_FACTURA = ''' || UN_TIPOCOBRO || '''
                     AND NUMERO_FACTURA = ' || UN_FACTURA ;
        BEGIN
          BEGIN
            MI_ACME := PCK_DATOS.FC_ACME(UN_ACCION    => 'M'
                                       ,UN_TABLA     => 'SF_FACTURA'
                                       ,UN_CAMPOS    => MI_CAMPOS
                                       ,UN_CONDICION => MI_CONDICION);
    
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            MI_ERROR(1).CLAVE := 'PROCESO';
            MI_ERROR(1).VALOR := 'actualizaci�n de la factura como base de pagos parciales';      
    
            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
          END;
    
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_UPDATEPAGPARCIAL
                                    ,UN_TABLAERROR => 'SF_DETALLE_ABONO'
                                    ,UN_REEMPLAZOS => MI_ERROR);
        END;    
    ELSE
    
        SELECT TIPO, CODIGO
        INTO   MI_TIPOABONO, MI_NROABONO
        FROM   SF_ABONOS
        WHERE COMPANIA  = UN_COMPANIA
        AND TIPO_FACT   = UN_TIPOCOBRO
        AND NRO_FACT    = UN_FACTURA ;
    
        --SI NO TIENE PAGOS SE ACTUALIZA EL ABONO CON LOS NUEVOS VALORES.
        
        BEGIN
        SELECT COUNT(*)
        INTO MI_EXISTEPAGO
                        FROM SF_DETALLE_ABONO DA                          
                        WHERE DA.COMPANIA        = UN_COMPANIA                                  
                        AND DA.TIPO_ABONO        = MI_TIPOABONO                  
                        AND DA.CODIGO_ABONO      = MI_NROABONO                  
                        AND DA.ESTADO            = 'R';
        EXCEPTION
               WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
                    MI_EXISTEPAGO := 0;          
        END;
        
        IF MI_EXISTEPAGO = 0 THEN
        
                MI_CAMPOS := 'DEUDA_INTERES = ' || UN_VLRINTERESES || ',
                              DEUDA_TOTAL = DEUDA_CAPITAL + ' || UN_VLRINTERESES || '';
                MI_CONDICION := 'COMPANIA  = '''||UN_COMPANIA ||'''
                                AND TIPO   = '''||MI_TIPOABONO||'''
                                AND CODIGO = ' || MI_NROABONO || ' 
                         ';
            BEGIN
              BEGIN
                MI_ACME := PCK_DATOS.FC_ACME(UN_ACCION    => 'M'
                                           ,UN_TABLA     => 'SF_ABONOS'
                                           ,UN_CAMPOS    => MI_CAMPOS
                                           ,UN_CONDICION => MI_CONDICION);
        
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                MI_ERROR(1).CLAVE := 'PROCESO';
                MI_ERROR(1).VALOR := 'actualizaci�n de interes';      
        
                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
              END;
        
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                        ,UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSERTPAGPARCIAL
                                        ,UN_TABLAERROR => 'SF_ABONOS'
                                        ,UN_REEMPLAZOS => MI_ERROR);
            END;
            
        ELSE 
        
        BEGIN 
        
        SELECT INTERES_PAG_PARCIAL, INTERES_EXPEM 
        INTO MI_INTERESANT, MI_INTERES_EXPEM
                FROM SF_DETALLE_ABONO
                WHERE COMPANIA   = UN_COMPANIA
                AND TIPO_ABONO   = MI_TIPOABONO
                AND CODIGO_ABONO = MI_NROABONO
                AND ESTADO = 'A';
                
        EXCEPTION
               WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
                    MI_INTERESANT := 0;
                    MI_INTERES_EXPEM := 0;
                
        END;
        
        MI_CAMPOS := 'DEUDA_INTERES = DEUDA_INTERES + ' || UN_VLRINTERESES || ' - ' || MI_INTERESANT || ',
                              DEUDA_TOTAL = DEUDA_TOTAL + ' || UN_VLRINTERESES || ' - ' || MI_INTERESANT ||'';
                MI_CONDICION := 'COMPANIA  = '''||UN_COMPANIA ||'''
                                AND TIPO   = '''||MI_TIPOABONO||'''
                                AND CODIGO = ' || MI_NROABONO || ' 
                         ';
            BEGIN
              BEGIN
                MI_ACME := PCK_DATOS.FC_ACME(UN_ACCION    => 'M'
                                           ,UN_TABLA     => 'SF_ABONOS'
                                           ,UN_CAMPOS    => MI_CAMPOS
                                           ,UN_CONDICION => MI_CONDICION);
        
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                MI_ERROR(1).CLAVE := 'PROCESO';
                MI_ERROR(1).VALOR := 'actualizaci�n de interes';      
        
                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
              END;
        
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                        ,UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSERTPAGPARCIAL
                                        ,UN_TABLAERROR => 'SF_ABONOS'
                                        ,UN_REEMPLAZOS => MI_ERROR);
            END;   
        
        END IF;
    
    
        -- CONSULTAR EL VALOR DEL SALDO PENDIENTE DE LA DEUD PARA PODER DEFINIR LA DISTRIBUCION DEL NUEVO PAGO PARCIAL A REGISTRAR
        
        SELECT DEUDA_CAPITAL, DEUDA_INTERES, SALDO, INTERES_EXPEM
        INTO MI_CAPITAL, MI_INTERES, MI_TOTALES, MI_INTERES_EXPEM
        FROM (WITH PAGOS AS ( 
                    SELECT DC.COMPANIA, DC.ANO, DC.TIPOFACT, DC.CODIGO_ABONO NROFACT, 
                    A.DEUDA_CAPITAL, (A.INTERES_ACT + A.INTERES_EX) DEUDA_INTERES, A.DEUDA_TOTAL, SUM(DC.TOTAL_CUOTA) PAGOS
                        FROM   SF_DETALLE_CUOTAABONO DC                       
                        INNER JOIN SF_DETALLE_ABONO DA                          
                        ON DC.COMPANIA = DA.COMPANIA                          
                        AND DC.TIPO_ABONO = DA.TIPO_ABONO                          
                        AND DC.CODIGO_ABONO = DA.CODIGO_ABONO                          
                        AND DC.CUOTA = DA.CUOTA  
                        INNER JOIN SF_ABONOS A 
                        ON DC.COMPANIA = A.COMPANIA 
                        AND DC.TIPO_ABONO = A.TIPO 
                        AND DC.CODIGO_ABONO = A.CODIGO 
                        WHERE DC.COMPANIA        = UN_COMPANIA                                  
                        AND DC.TIPO_ABONO        = MI_TIPOABONO                  
                        AND DC.CODIGO_ABONO      = MI_NROABONO                  
                        AND DA.ESTADO            = 'R'                  
                        GROUP BY DC.COMPANIA, DC.ANO, DC.TIPOFACT, DC.CODIGO_ABONO, 
                        A.DEUDA_CAPITAL, (A.INTERES_ACT + A.INTERES_EX), A.DEUDA_TOTAL
                    ) 
                SELECT A.DEUDA_CAPITAL, A.DEUDA_INTERES, (A.DEUDA_TOTAL - NVL(PAGOS.PAGOS, 0)) AS SALDO,
                        CASE WHEN PAGOS.DEUDA_INTERES > PAGOS.PAGOS THEN (PAGOS.DEUDA_INTERES - PAGOS.PAGOS) ELSE 0 END INTERES_EXPEM 
                         FROM SF_ABONOS A 
                         LEFT JOIN PAGOS
                         ON A.COMPANIA = PAGOS.COMPANIA
                         AND A.TIPO = PAGOS.TIPOFACT
                         AND A.CODIGO = PAGOS.NROFACT
                         WHERE A.COMPANIA = UN_COMPANIA
                         AND A.TIPO = MI_TIPOABONO
                         AND A.CODIGO = MI_NROABONO
                         );
        
        IF MI_INTERES_EXPEM > 0 THEN            
        MI_INTERES_EXPEM := MI_INTERES_EXPEM;
        ELSE 
        MI_INTERES_EXPEM := 0;
        END IF;

        MI_SALDOCAPITAL := MI_CAPITAL;
        
        IF MI_EXISTEPAGO = 0 THEN
        MI_SALDOINTERES := MI_INTERES;
        ELSE
        MI_SALDOINTERES := UN_VLRINTERESES + MI_INTERES_EXPEM;
        END IF;
        BEGIN

                IF (MI_TOTALES + UN_VLRINTERESES ) < UN_VLRABONO THEN
                    RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                END IF;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                    MI_ERROR(1).CLAVE := 'DEUDA';
                    MI_ERROR(1).VALOR := MI_TOTALES + UN_VLRINTERESES;

                    PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE,
                        UN_TABLAERROR => 'SF_ABONOS',
                        UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_VLRABONOSUPERIOR,
                    UN_REEMPLAZOS => MI_ERROR
                    );
        END ;
    
        
        PR_ANULARCUOTAS_PAGOPARCIAL (
            UN_COMPANIA           => UN_COMPANIA,
            UN_TIPOABONO          => UN_TIPOCOBRO,
            UN_NROABONO           => MI_NROABONO,
            UN_USUARIO            => UN_USUARIO);
    END IF;
    --Ticket#7737169:Se comenta debido a que no es necesrio crear una factura por los intereses.
    /*IF MI_INTERES < UN_VLRABONO AND UN_VLRINTERESES > 0 THEN
        MI_FRAINTERES := FC_CREARFRA_INTERESES(
                                      UN_COMPANIA               => UN_COMPANIA,
                                      UN_ANO                    => UN_ANO,
                                      UN_TIPOCOBRO              => UN_TIPOCOBRO,
                                      UN_FACTURA                => UN_FACTURA,
                                      UN_CPTOINTERES            => MI_CPTOINTERES,
                                      UN_VLRINTERESES           => UN_VLRINTERESES,   
                                      UN_DIASMORA               => UN_DIASMORA,  
                                      UN_FECHACORTE             => UN_FECHACORTE, 
                                      UN_TERCERO                => MI_TERCERO,
                                      UN_SUCURSAL               => MI_SUCURSAL,
                                      UN_CENCOSTO               => MI_CENCOSTO,
                                      UN_AUXILIAR               => MI_AUXILIAR,
                                      UN_NITTERFACTURADO        => MI_NITTERFACTURADO,
                                      UN_SUCTERFACTURADO        => MI_SUCTERFACTURADO,
                                      UN_NOMTERCEROFACTURADO    => MI_NOMTERFACTURADO,
                                      UN_USUARIO                => UN_USUARIO);
    END IF;*/
    
     

    MI_NROCUOTA := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'SF_DETALLE_ABONO',
                                                     UN_CRITERIO => 'COMPANIA ='''||UN_COMPANIA||''' AND TIPO_ABONO='''||UN_TIPOCOBRO||'''  AND CODIGO_ABONO='||MI_NROABONO||''  ,
                                                     UN_CAMPO    => 'CUOTA');
                                                     
    IF (MI_SALDOCAPITAL + MI_SALDOINTERES) > 0 AND UN_VLRABONO > 0 THEN
        
        -- SE DEBE EVALUAR SI EL VALOR DEL ABONO CUBRE O ES MENOR LOS INTERESES PENDIENTES DE PAGO
        IF MI_SALDOINTERES >= UN_VLRABONO THEN
        
            IF NVL(MI_INTERES_EXPEM,0) >= UN_VLRABONO THEN
                MI_VLR_INTERES      := 0;
                MI_INTERES_EXPEM    := UN_VLRABONO;
                MI_SALDOABONO       := 0;
            ELSE 
                MI_SALDOABONO       := 0;
                MI_VLR_INTERES      := UN_VLRABONO - NVL(MI_INTERES_EXPEM,0);
            END IF;
            
            BEGIN
                BEGIN
                    MI_CAMPOS:='COMPANIA,
                               TIPO_ABONO,
                               CODIGO_ABONO,
                               CUOTA,
                               FECHACUOTA,
                               CAPITAL,
                               INTERES,
                               TOTAL_CUOTA,
                               ESTADO,
                               CREATED_BY,
                               DATE_CREATED,
                               INTERES_PAG_PARCIAL, 
                               INTERES_EXPEM';
                    MI_VALORES:=''''||UN_COMPANIA||''',
                                '''||UN_TIPOCOBRO||''',
                                '||MI_NROABONO||',
                                '||MI_NROCUOTA||',
                                '''||UN_FECHACORTE||''',
                                0,
                                '||UN_VLRABONO||',
                                '||UN_VLRABONO||',
                                ''A'',
                                '''||UN_USUARIO||''',
                                SYSDATE,
                                '||MI_VLR_INTERES||',
                                '||NVL(MI_INTERES_EXPEM,0)||'
                                ';

                    MI_ACME:=PCK_DATOS.FC_ACME(UN_TABLA  =>'SF_DETALLE_ABONO',
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
                    MI_ERROR(1).VALOR := 'Creaci�n del detalle del abono';
                     
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE,
                        UN_TABLAERROR => 'SF_ABONOS',
                        UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSERTPAGPARCIAL,
                        UN_REEMPLAZOS => MI_ERROR
                    );
            END;     
        ELSE
            MI_SALDOABONO := UN_VLRABONO - MI_SALDOINTERES;
            MI_VLR_INTERES := UN_VLRINTERESES;
            
            BEGIN
                BEGIN
                    MI_CAMPOS:='COMPANIA,
                               TIPO_ABONO,
                               CODIGO_ABONO,
                               CUOTA,
                               FECHACUOTA,
                               CAPITAL,
                               INTERES,
                               TOTAL_CUOTA,
                               ESTADO,
                               CREATED_BY,
                               DATE_CREATED,
                               INTERES_PAG_PARCIAL, 
                               INTERES_EXPEM';
                    MI_VALORES:=''''||UN_COMPANIA||''',
                                '''||UN_TIPOCOBRO||''',
                                '||MI_NROABONO||',
                                '||MI_NROCUOTA||',
                                TO_DATE('''||UN_FECHACORTE||''', ''DD/MM/YYYY''),
                                '||MI_SALDOABONO||',
                                '||MI_SALDOINTERES||',
                                '||UN_VLRABONO||',
                                ''A'',
                                '''||UN_USUARIO||''',
                                SYSDATE,
                                '||MI_VLR_INTERES||',
                                '||NVL(MI_INTERES_EXPEM,0)||'
                                ';

                    MI_ACME:=PCK_DATOS.FC_ACME(UN_TABLA  =>'SF_DETALLE_ABONO',
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
                    MI_ERROR(1).VALOR := 'Creaci�n detalle de abono';
                              
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE,
                        UN_TABLAERROR => 'SF_ABONOS',
                        UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSERTPAGPARCIAL,
                    UN_REEMPLAZOS => MI_ERROR
                    );
            END;
        END IF;
        
        BEGIN
            SELECT CONCEPTO, ANO
            INTO MI_CPTOCAPITAL, MI_ANO
            FROM SF_DETALLE_FACTURA
            WHERE   SF_DETALLE_FACTURA.COMPANIA    = UN_COMPANIA
            AND     SF_DETALLE_FACTURA.TIPO_FACTURA = UN_TIPOCOBRO
            AND     SF_DETALLE_FACTURA.FACTURA      = UN_FACTURA;
        EXCEPTION
               WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
                    MI_CPTOCAPITAL := '';
        END;
        
        -- DISTRIBUIR CUOTA Y CREAR REGISTRO DEL DETALLE POR CONCEPTO
        PR_DISTRIBUIRPAGOPARIAL(
                UN_COMPANIA             => UN_COMPANIA,
                UN_ANO                  => MI_ANO,
                UN_TIPOABONO            => UN_TIPOCOBRO,
                UN_NROABONO             => MI_NROABONO,
                UN_CUOTA                => MI_NROCUOTA,
                UN_TIPOFRA_PAGPACIAL    => UN_TIPOCOBRO,
                UN_FRA_CAPITAL          => UN_FACTURA,
                UN_CPTOINTERES          => MI_CPTOINTERES,
                UN_CPTOINTERES_EX       => MI_CPTOINTERES_EX,
                UN_CPTOCAPITAL          => MI_CPTOCAPITAL,
                UN_VLRABONO             => UN_VLRABONO,
                UN_VLR_CAPITAL          => MI_SALDOABONO,
                UN_VLR_INTERES          => MI_VLR_INTERES,
                UN_VLR_INTERES_EX       => MI_INTERES_EXPEM,
                UN_USUARIO              => UN_USUARIO);
    END IF;
    
        MI_CAMPOS := 'FECHA_CORTE = '''||UN_FECHACORTE||'''';
        MI_CONDICION := 'COMPANIA       = '''||UN_COMPANIA ||'''
                     AND ANO   = '''||UN_ANO||'''
                     AND TIPO_FACTURA = ''' || UN_TIPOCOBRO || '''
                     AND NUMERO_FACTURA = ' || UN_FACTURA ;
        BEGIN
          BEGIN
            MI_ACME := PCK_DATOS.FC_ACME(UN_ACCION    => 'M'
                                       ,UN_TABLA     => 'SF_FACTURA'
                                       ,UN_CAMPOS    => MI_CAMPOS
                                       ,UN_CONDICION => MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            MI_ERROR(1).CLAVE := 'PROCESO';
            MI_ERROR(1).VALOR := 'actualizaci¿n de la factura como base de pagos parciales';
            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
          END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_UPDATEPAGPARCIAL
                                    ,UN_TABLAERROR => 'SF_DETALLE_ABONO'
                                    ,UN_REEMPLAZOS => MI_ERROR);
        END;
    
    ELSE
    MI_CAMPOS := 'FECHA_RESOLUCION = '''||UN_FECHARESOLUCION||''', FECHA_EJECUTORA = '''||UN_FECHAEJECUTORA||'''
                    ,NUMERO_RESOLUCION = '''||UN_RESOLUCION||''', NUMERO_EXPEDIENTE = '''||UN_EXPEDIENTE||'''
                    ,FECHA_CORTE = '''||UN_FECHACORTE||'''';
        
        MI_CONDICION := 'COMPANIA       = '''||UN_COMPANIA ||'''
                     AND ANO   = '''||UN_ANO||'''
                     AND TIPO_FACTURA = ''' || UN_TIPOCOBRO || '''
                     AND NUMERO_FACTURA = ' || UN_FACTURA ;
        BEGIN
          BEGIN
            MI_ACME := PCK_DATOS.FC_ACME(UN_ACCION    => 'M'
                                       ,UN_TABLA     => 'SF_FACTURA'
                                       ,UN_CAMPOS    => MI_CAMPOS
                                       ,UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            MI_ERROR(1).CLAVE := 'PROCESO';
            MI_ERROR(1).VALOR := 'actualizaci�n de la factura como base de pagos parciales';      

            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
          END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_UPDATEPAGPARCIAL
                                    ,UN_TABLAERROR => 'SF_DETALLE_ABONO'
                                    ,UN_REEMPLAZOS => MI_ERROR);
        END;
        
    END IF;


END PR_REGISTRAR_PAGOPARCIAL;


FUNCTION FC_CREARFRA_INTERESES(
/*
      NAME              : FC_CREARFRA_INTERESES 
      AUTHORS           : STEFANINI SYSMAN  SAS
      AUTHOR MIGRACION  : SANDRA MILENA DAZA LEGUIZAMON
      DATE CREATED      : 23/09/2021 
      TIME              : 02:20 PM
      SOURCE MODULE     : 
      DESCRIPTION       : 
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : Crea la factura por el concepto de intereses de mora de la factura que es objeto de pagos parciales. Debe retornar el n�mero de factura
                             para  usar en la creaci�n del detalle de la abono parcial

    @NAME:   crearFraIntereses
    @METHOD: GET
*/
  UN_COMPANIA               IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANO                    IN PCK_SUBTIPOS.TI_ENTERO,
  UN_TIPOCOBRO              IN PCK_SUBTIPOS.TI_TIPO_COBRO,
  UN_FACTURA                IN SF_FACTURA.NUMERO_FACTURA%TYPE,
  UN_CPTOINTERES            IN SF_CONCEPTOS.CODIGO%TYPE,
  UN_VLRINTERESES           IN PCK_SUBTIPOS.TI_DOBLE,   
  UN_DIASMORA               IN PCK_SUBTIPOS.TI_ENTERO,  
  UN_FECHACORTE             IN DATE, 
  UN_TERCERO                IN SF_FACTURA.TERCERO%TYPE,
  UN_SUCURSAL               IN SF_FACTURA.SUCURSAL%TYPE,
  UN_CENCOSTO               IN SF_FACTURA.CENTRO_COSTO%TYPE,
  UN_AUXILIAR               IN SF_FACTURA.AUXILIAR%TYPE,
  UN_NITTERFACTURADO        IN SF_OBJETO_COBRO.NIT_TERCEROFACTURADO%TYPE,
  UN_SUCTERFACTURADO        IN SF_OBJETO_COBRO.SUCURSAL_TERCEROFACTURADO%TYPE,
  UN_NOMTERCEROFACTURADO    IN SF_OBJETO_COBRO.NOMBRE_TERCEROFACTURADO%TYPE,
  UN_USUARIO                IN PCK_SUBTIPOS.TI_USUARIO
    
)RETURN NUMBER
AS
    MI_NROCOBRO         SF_OBJETO_COBRO.CODIGO_COBRO%TYPE;
    MI_OBS              SF_OBJETO_COBRO.OBSERVACIONES%TYPE;
    MI_VALORES          PCK_SUBTIPOS.TI_CAMPOS;
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_ACME             PCK_SUBTIPOS.TI_ENTERO;
    MI_ERROR            PCK_SUBTIPOS.TI_CLAVEVALOR;  
    MI_NROFRA           SF_FACTURA.NUMERO_FACTURA%TYPE;
    MI_ANOACTUAL        PCK_SUBTIPOS.TI_ENTERO;
BEGIN
    MI_ANOACTUAL := TO_CHAR(SYSDATE, 'YYYY');
    MI_NROCOBRO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'SF_OBJETO_COBRO',
                                                     UN_CRITERIO => 'COMPANIA ='''||UN_COMPANIA||''' AND TIPOCOBRO='''||UN_TIPOCOBRO||''' '  ,
                                                     UN_CAMPO    => 'CODIGO_COBRO');
    MI_OBS := 'Factura intereses,  '|| UN_DIASMORA ||' d�as en mora de la factura ' || UN_TIPOCOBRO || ' - ' || UN_FACTURA;                                                  
    BEGIN
        BEGIN
            MI_CAMPOS:='COMPANIA,
                        ANO, 
                        TIPOCOBRO,
                        CODIGO_COBRO,
                        TERCERO, 
                        SUCURSAL,
                        CENTRO_COSTO,
                        AUXILIAR,
                        FECHA_SOLICITUD,
                        VALOR_TOTAL,
                        NIT_TERCEROFACTURADO,
                        SUCURSAL_TERCEROFACTURADO,
                        NOMBRE_TERCEROFACTURADO,
                        OBSERVACIONES,
                        CREATED_BY,
                        DATE_CREATED';
            MI_VALORES:=''''||UN_COMPANIA||''',
                        '||MI_ANOACTUAL||',
                        '''||UN_TIPOCOBRO||''',
                        '||MI_NROCOBRO||',
                        '''||UN_TERCERO||''',
                        '''||UN_SUCURSAL||''',
                        '''||UN_CENCOSTO||''',
                        '''||UN_AUXILIAR||''',
                        SYSDATE,
                        '||UN_VLRINTERESES||',
                        '''||UN_TERCERO||''',
                        '''||UN_SUCURSAL||''',
                        '''||UN_NOMTERCEROFACTURADO||''',
                        '''||MI_OBS||''',
                        '''||UN_USUARIO||''',
                        SYSDATE';
                MI_ACME:=PCK_DATOS.FC_ACME(UN_TABLA  =>'SF_OBJETO_COBRO',
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
                MI_ERROR(1).VALOR := ' Creaci�n objeto de cobro';
               PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD    => SQLCODE,
                 UN_TABLAERROR => 'SF_OBJETO_COBRO',
                 UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSERTPAGPARCIAL,
                 UN_REEMPLAZOS => MI_ERROR
                );

      END; 
            
      -- CREAR EL DETALLE DEL OBJETO DE COBRO
      
      BEGIN
        BEGIN
            MI_CAMPOS:='COMPANIA,
                        ANO, 
                        TIPOCOBRO,
                        CODIGO_COBRO,
                        CONCEPTO,
                        VALOR_BASE,
                        CANTIDAD,
                        VALOR_NETO,
                        FECHA_INICIO_COBRO,
                        FECHA_FIN_COBRO, 
                        TERCERO, 
                        SUCURSAL,
                        CENTRO_COSTO,
                        AUXILIAR,
                        VALOR_UNITARIO,                         
                        CREATED_BY,
                        DATE_CREATED';
            MI_VALORES:=''''||UN_COMPANIA||''',
                        '||MI_ANOACTUAL||',
                        '''||UN_TIPOCOBRO||''',
                        '||MI_NROCOBRO||',
                        '''||UN_CPTOINTERES||''',
                        '||UN_VLRINTERESES||',
                        1, 
                        '||UN_VLRINTERESES||',
                        SYSDATE,
                        SYSDATE,
                        '''||UN_TERCERO||''',
                        '''||UN_SUCURSAL||''',
                        '''||UN_CENCOSTO||''',
                        '''||UN_AUXILIAR||''',
                        '||UN_VLRINTERESES||',
                        '''||UN_USUARIO||''',
                        SYSDATE';
                MI_ACME:=PCK_DATOS.FC_ACME(UN_TABLA  =>'SF_DETALLE_COBRO',
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
                MI_ERROR(1).VALOR := 'Creaci�n del detalle del objeto de cobro';
               PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD    => SQLCODE,
                 UN_TABLAERROR => 'SF_DETALLE_COBRO',
                 UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSERTPAGPARCIAL,
                 UN_REEMPLAZOS => MI_ERROR
                );

      END; 
      
      -- CREAR EL HEADER DE LA FACTURA
      MI_NROFRA := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'SF_FACTURA',
                                                     UN_CRITERIO => 'COMPANIA ='''||UN_COMPANIA||''' AND ANO = '||MI_ANOACTUAL||' AND TIPO_FACTURA='''||UN_TIPOCOBRO||''' '  ,
                                                     UN_CAMPO    => 'NUMERO_FACTURA');
      BEGIN
        BEGIN
            MI_CAMPOS:='COMPANIA,
                        ANO, 
                        TIPO_FACTURA,
                        NUMERO_FACTURA,
                        TIPOCOBRO,
                        CODIGO_COBRO, 
                        TERCERO, 
                        SUCURSAL,
                        CENTRO_COSTO,
                        AUXILIAR,
                        FECHA_EXPEDICION,
                        FECHA_VENCIMIENTO,
                        VALOR_TOTAL,
                        OBSERVACIONES,
                        REGISTRADOR_POR,
                        FECHA_REGISTRO, 
                        NIT_TERCEROFACTURADO, 
                        SUCURSAL_TERCEROFACTURADO,
                        NOMBRE_TERCEROFACTURADO,
                        PAGOS_PARCIALES,
                        TIPOFRA_PAGOPARCIAL,
                        NROFRA_PAGOPARCIAL, 
                        CREATED_BY,
                        DATE_CREATED';
            MI_VALORES:=''''||UN_COMPANIA||''',
                        '||MI_ANOACTUAL||',
                        '''||UN_TIPOCOBRO||''',
                        '||MI_NROFRA||',
                        '''||UN_TIPOCOBRO||''',
                        '||MI_NROCOBRO||',
                        '''||UN_TERCERO||''',
                        '''||UN_SUCURSAL||''',
                        '''||UN_CENCOSTO||''',
                        '''||UN_AUXILIAR||''',
                        SYSDATE,
                        '''||UN_FECHACORTE||''',
                        '||UN_VLRINTERESES||',
                        '''||MI_OBS||''',
                        '''||UN_USUARIO||''',
                        SYSDATE,
                        '''||UN_TERCERO||''',
                        '''||UN_SUCURSAL||''',
                        '''||UN_NOMTERCEROFACTURADO||''',
                        -1,
                        '''||UN_TIPOCOBRO||''',
                        '||UN_FACTURA||',
                        '''||UN_USUARIO||''',
                        SYSDATE';
                MI_ACME:=PCK_DATOS.FC_ACME(UN_TABLA  =>'SF_FACTURA',
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
                MI_ERROR(1).VALOR := 'Creaci�n de factura de intereses de mora';
               PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD    => SQLCODE,
                 UN_TABLAERROR => 'SF_FACTURA',
                 UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSERTPAGPARCIAL,
                 UN_REEMPLAZOS => MI_ERROR
                );

      END; 
            
      -- CREAR EL DETALLE DE LA FACTURA
      BEGIN
        BEGIN
            MI_CAMPOS:='COMPANIA,
                        ANO, 
                        TIPO_FACTURA,
                        FACTURA,
                        TIPOCOBRO,
                        CONCEPTO,
                        VALOR_BASE,
                        CANTIDAD,
                        VALOR_NETO,
                        FECHA_INICIO_COBRO,
                        FECHA_FIN_COBRO, 
                        TERCERO, 
                        SUCURSAL,
                        CENTRO_COSTO,
                        AUXILIAR,
                        VALOR_UNITARIO,                         
                        CREATED_BY,
                        DATE_CREATED';
            MI_VALORES:=''''||UN_COMPANIA||''',
                        '||MI_ANOACTUAL||',
                        '''||UN_TIPOCOBRO||''',
                        '||MI_NROFRA||',
                        '''||UN_TIPOCOBRO||''',
                        '''||UN_CPTOINTERES||''',
                        '||UN_VLRINTERESES||',
                        1, 
                        '||UN_VLRINTERESES||',
                        SYSDATE,
                        SYSDATE,
                        '''||UN_TERCERO||''',
                        '''||UN_SUCURSAL||''',
                        '''||UN_CENCOSTO||''',
                        '''||UN_AUXILIAR||''',
                        '||UN_VLRINTERESES||',
                        '''||UN_USUARIO||''',
                        SYSDATE';
                MI_ACME:=PCK_DATOS.FC_ACME(UN_TABLA  =>'SF_DETALLE_FACTURA',
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
                MI_ERROR(1).VALOR := 'Creacion del detalle de factura de intereses de mora';
                MI_ERROR(2).CLAVE := 'CAMPOS';
                MI_ERROR(2).VALOR :=  MI_CAMPOS;
               PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD    => SQLCODE,
                 UN_TABLAERROR => 'SF_DETALLE_FACTURA',
                 UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSERTPAGPARCIAL,
                 UN_REEMPLAZOS => MI_ERROR
                );

      END;
      
      
      RETURN MI_NROFRA;
END FC_CREARFRA_INTERESES;



PROCEDURE PR_DISTRIBUIRPAGOPARIAL(
/*
      NAME              : PR_DISTRIBUIRPAGOPARIAL 
      AUTHORS           : STEFANINI SYSMAN  SAS
      AUTHOR MIGRACION  : SANDRA MILENA DAZA LEGUIZAMON
      DATE CREATED      : 24/09/2021 
      TIME              : 04:20 PM
      SOURCE MODULE     : 
      DESCRIPTION       : 
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : recorre las facturas que se hacen parte del proceso de pagos parciales para distribuir el abono y registrar
                          el detalle de abono por cuota 

    @NAME:   distribuirPagoParcial
    @METHOD: GET
*/
    UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO                  SF_DETALLE_CUOTAABONO.ANO%TYPE,
    UN_TIPOABONO            IN PCK_SUBTIPOS.TI_PARAMETRO,
    UN_NROABONO             IN PCK_SUBTIPOS.TI_DOBLE,
    UN_CUOTA                IN PCK_SUBTIPOS.TI_ENTERO,
    UN_TIPOFRA_PAGPACIAL    IN PCK_SUBTIPOS.TI_PARAMETRO,
    UN_FRA_CAPITAL          IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
    UN_CPTOINTERES          SF_CONCEPTOS.CODIGO%TYPE,
    UN_CPTOINTERES_EX       SF_CONCEPTOS.CODIGO%TYPE,
    UN_CPTOCAPITAL          SF_CONCEPTOS.CODIGO%TYPE,
    UN_VLRABONO             IN PCK_SUBTIPOS.TI_DOBLE,
    UN_VLR_CAPITAL          IN PCK_SUBTIPOS.TI_DOBLE,
    UN_VLR_INTERES          IN PCK_SUBTIPOS.TI_DOBLE,
    UN_VLR_INTERES_EX       IN PCK_SUBTIPOS.TI_DOBLE,
    UN_USUARIO              IN PCK_SUBTIPOS.TI_USUARIO
)
AS
    MI_SALDOABONO            PCK_SUBTIPOS.TI_DOBLE;
    MI_ABBASE                PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_ABIVA                 PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_ABRETE                PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_ABDESC                PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_ABICA                 PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_ABNETO                PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_NROCUOTA              PCK_SUBTIPOS.TI_ENTERO;
    MI_VALORES               PCK_SUBTIPOS.TI_CAMPOS;
    MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
    MI_ACME                  PCK_SUBTIPOS.TI_ENTERO;
    MI_ERROR                 PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_RS                    SYS_REFCURSOR;
    MI_CUOTASPAGAS           PCK_SUBTIPOS.TI_ENTERO;
    MI_STRSQL                VARCHAR2(4000);       
    MI_STRSQL_CPTO           VARCHAR2(4000);  
    MI_ANO                   SF_DETALLE_CUOTAABONO.ANO%TYPE;
    MI_TIPO_FACTURA          SF_DETALLE_CUOTAABONO.TIPOFACT%TYPE;
    MI_FACTURA               SF_DETALLE_CUOTAABONO.NROFACT%TYPE;
    MI_TERCERO               SF_DETALLE_CUOTAABONO.TERCERO%TYPE;
    MI_SUCURSAL              SF_DETALLE_CUOTAABONO.SUCURSAL%TYPE;
    MI_CENTRO_COSTO          SF_DETALLE_CUOTAABONO.CENTRO_COSTO%TYPE;
    MI_AUXILIAR              SF_DETALLE_CUOTAABONO.AUXILIAR%TYPE;
    MI_CONCEPTO              SF_DETALLE_CUOTAABONO.CONCEPTO%TYPE;       
    MI_SALDOBASE             SF_DETALLE_CUOTAABONO.VALOR_BASE%TYPE;
    MI_SALDOIVA              SF_DETALLE_CUOTAABONO.VALOR_IVA%TYPE;
    MI_SALDORETE             SF_DETALLE_CUOTAABONO.VALOR_RETE%TYPE;
    MI_SALDODESC             SF_DETALLE_CUOTAABONO.VALOR_DESCUENTO%TYPE;
    MI_SALDOICA              SF_DETALLE_CUOTAABONO.VALOR_ICA%TYPE;
    MI_SALDONETO             SF_DETALLE_CUOTAABONO.TOTAL_CUOTA%TYPE;
    
    

BEGIN

    BEGIN
    SELECT SDF.TIPO_FACTURA, SDF.FACTURA , SDF.TERCERO, SDF.SUCURSAL, SDF.CENTRO_COSTO, SDF.AUXILIAR
            INTO MI_TIPO_FACTURA, MI_FACTURA, MI_TERCERO, MI_SUCURSAL, MI_CENTRO_COSTO, MI_AUXILIAR
                        FROM SF_DETALLE_ABONO DA
                        INNER JOIN SF_ABONOS A
                        ON DA.COMPANIA = A.COMPANIA
                        AND DA.TIPO_ABONO  = A.TIPO
                        AND DA.CODIGO_ABONO = A.CODIGO
                        INNER JOIN SF_FACTURA SF
                        ON A.COMPANIA = SF.COMPANIA
                        AND A.TIPO_FACT = SF.TIPO_FACTURA
                        AND A.NRO_FACT = SF.NUMERO_FACTURA
                        INNER JOIN SF_DETALLE_FACTURA SDF
                        ON SDF.COMPANIA = SF.COMPANIA
                        AND SDF.ANO  = SF.ANO
                        AND SDF.TIPO_FACTURA = SF.TIPO_FACTURA
                        AND SDF.FACTURA = SF.NUMERO_FACTURA
                        WHERE DA.COMPANIA = UN_COMPANIA 
                        AND DA.TIPO_ABONO = UN_TIPOABONO
                        AND DA.CODIGO_ABONO = UN_NROABONO
                        AND DA.ESTADO          = 'A';
        EXCEPTION
               WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
                    MI_TIPO_FACTURA := ''; 
                    MI_FACTURA := ''; 
                    MI_TERCERO := ''; 
                    MI_SUCURSAL := ''; 
                    MI_CENTRO_COSTO := ''; 
                    MI_AUXILIAR := '';
        END;
    
        IF UN_VLR_INTERES_EX > 0 THEN
            
            MI_CONCEPTO := UN_CPTOINTERES_EX;
            
            BEGIN
              MI_CAMPOS:='COMPANIA,'||
                          'TIPO_ABONO,'||
                          'CODIGO_ABONO,'||
                          'ANO,'||
                          'CUOTA,'||
                          'TIPOFACT,'||
                          'NROFACT,'||
                          'CONCEPTO,'||
                          'VALOR_BASE,'||
                          'VALOR_IVA,'||
                          'VALOR_RETE,'||
                          'VALOR_DESCUENTO,'||
                          'VALOR_ICA,'||
                          'TOTAL_CUOTA,'||
                          'TERCERO,'||
                          'SUCURSAL,'||
                          'CENTRO_COSTO,'||
                          'AUXILIAR,'||
                          'CREATED_BY,'||
                          'DATE_CREATED';
            MI_VALORES:=''''||UN_COMPANIA||''','||
                        ''''||UN_TIPOABONO||''','||
                        UN_NROABONO||','||
                        UN_ANO||','||
                        UN_CUOTA||','||
                        ''''||UN_TIPOFRA_PAGPACIAL||''','||
                        UN_FRA_CAPITAL||','||
                        ''''||MI_CONCEPTO||''','||
                        UN_VLR_INTERES_EX||','||
                        '0,'||
                        '0,'||
                        '0,'||
                        '0,'||
                        UN_VLR_INTERES_EX||','||
                        ''''||MI_TERCERO||''','||
                        ''''||MI_SUCURSAL||''','||
                        ''''||MI_CENTRO_COSTO||''','||
                        ''''||MI_AUXILIAR||''','||
                        ''''||UN_USUARIO||''','||
                        'SYSDATE';
            BEGIN
                MI_ACME:=PCK_DATOS.FC_ACME(UN_ACCION  =>'I',
                                             UN_TABLA   =>'SF_DETALLE_CUOTAABONO',
                                             UN_CAMPOS  =>MI_CAMPOS,
                                             UN_VALORES =>MI_VALORES);
                EXCEPTION
                     WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
            END;
            EXCEPTION
                  WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                    MI_ERROR(1).CLAVE := 'PROCESO';
                    MI_ERROR(1).VALOR := 'creaci¿n detalle de la cuota de abono';
                   PCK_ERR_MSG.RAISE_WITH_MSG(
                     UN_EXC_COD    => SQLCODE,
                     UN_TABLAERROR => 'SF_DETALLE_CUOTAABONO',
                     UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSERTPAGPARCIAL,
                     UN_REEMPLAZOS => MI_ERROR
                    );
            END;
        END IF;
     
        IF UN_VLR_INTERES > 0 THEN
        
        MI_CONCEPTO := UN_CPTOINTERES;
        
            BEGIN
                MI_CAMPOS:='COMPANIA,'||
                          'TIPO_ABONO,'||
                          'CODIGO_ABONO,'||
                          'ANO,'||
                          'CUOTA,'||
                          'TIPOFACT,'||
                          'NROFACT,'||
                          'CONCEPTO,'||
                          'VALOR_BASE,'||
                          'VALOR_IVA,'||
                          'VALOR_RETE,'||
                          'VALOR_DESCUENTO,'||
                          'VALOR_ICA,'||
                          'TOTAL_CUOTA,'||
                          'TERCERO,'||
                          'SUCURSAL,'||
                          'CENTRO_COSTO,'||
                          'AUXILIAR,'||
                          'CREATED_BY,'||
                          'DATE_CREATED';
                MI_VALORES:=''''||UN_COMPANIA||''','||
                            ''''||UN_TIPOABONO||''','||
                            UN_NROABONO||','||
                            UN_ANO||','||
                            UN_CUOTA||','||
                            ''''||UN_TIPOFRA_PAGPACIAL||''','||
                            UN_FRA_CAPITAL||','||
                            ''''||MI_CONCEPTO||''','||
                            UN_VLR_INTERES||','||
                            '0,'||
                            '0,'||
                            '0,'||
                            '0,'||
                            UN_VLR_INTERES||','||
                            ''''||MI_TERCERO||''','||
                            ''''||MI_SUCURSAL||''','||
                            ''''||MI_CENTRO_COSTO||''','||
                            ''''||MI_AUXILIAR||''','||
                            ''''||UN_USUARIO||''','||
                            'SYSDATE';
                BEGIN
                    MI_ACME:=PCK_DATOS.FC_ACME(UN_ACCION  =>'I',
                                             UN_TABLA   =>'SF_DETALLE_CUOTAABONO',
                                             UN_CAMPOS  =>MI_CAMPOS,
                                             UN_VALORES =>MI_VALORES);
                    EXCEPTION
                         WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                END;
                EXCEPTION
                      WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                        MI_ERROR(1).CLAVE := 'PROCESO';
                        MI_ERROR(1).VALOR := 'Creacion del detalle de la cuota de abono';
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                     UN_EXC_COD    => SQLCODE,
                     UN_TABLAERROR => 'SF_DETALLE_CUOTAABONO',
                     UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSERTPAGPARCIAL,
                     UN_REEMPLAZOS => MI_ERROR
                    );
            END;
        END IF;
     
        IF UN_VLR_CAPITAL >0 THEN
        
        MI_CONCEPTO := UN_CPTOCAPITAL;
        
            BEGIN
              MI_CAMPOS:='COMPANIA,'||
                          'TIPO_ABONO,'||
                          'CODIGO_ABONO,'||
                          'ANO,'||
                          'CUOTA,'||
                          'TIPOFACT,'||
                          'NROFACT,'||
                          'CONCEPTO,'||
                          'VALOR_BASE,'||
                          'VALOR_IVA,'||
                          'VALOR_RETE,'||
                          'VALOR_DESCUENTO,'||
                          'VALOR_ICA,'||
                          'TOTAL_CUOTA,'||
                          'TERCERO,'||
                          'SUCURSAL,'||
                          'CENTRO_COSTO,'||
                          'AUXILIAR,'||
                          'CREATED_BY,'||
                          'DATE_CREATED';
            MI_VALORES:=''''||UN_COMPANIA||''','||
                            ''''||UN_TIPOABONO||''','||
                            UN_NROABONO||','||
                            UN_ANO||','||
                            UN_CUOTA||','||
                            ''''||UN_TIPOFRA_PAGPACIAL||''','||
                            UN_FRA_CAPITAL||','||
                            ''''||MI_CONCEPTO||''','||
                            UN_VLR_CAPITAL||','||
                            '0,'||
                            '0,'||
                            '0,'||
                            '0,'||
                            UN_VLR_CAPITAL||','||
                            ''''||MI_TERCERO||''','||
                            ''''||MI_SUCURSAL||''','||
                            ''''||MI_CENTRO_COSTO||''','||
                            ''''||MI_AUXILIAR||''','||
                            ''''||UN_USUARIO||''','||
                            'SYSDATE';
            BEGIN
                MI_ACME:=PCK_DATOS.FC_ACME(UN_ACCION  =>'I',
                                             UN_TABLA   =>'SF_DETALLE_CUOTAABONO',
                                             UN_CAMPOS  =>MI_CAMPOS,
                                             UN_VALORES =>MI_VALORES);
                EXCEPTION
                     WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
            END;
            EXCEPTION
                   WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                    MI_ERROR(1).CLAVE := 'PROCESO';
                    MI_ERROR(1).VALOR := 'Creacion del detalle de la cuota de abono';
                   PCK_ERR_MSG.RAISE_WITH_MSG(
                     UN_EXC_COD    => SQLCODE,
                     UN_TABLAERROR => 'SF_DETALLE_CUOTAABONO',
                     UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSERTPAGPARCIAL,
                     UN_REEMPLAZOS => MI_ERROR
                    );
            END;
        END IF;
     
    MI_CAMPOS := '';
END PR_DISTRIBUIRPAGOPARIAL;

PROCEDURE PR_ANULARCUOTAS_PAGOPARCIAL (
    UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TIPOABONO          IN SF_ABONOS.TIPO%TYPE,
    UN_NROABONO           IN SF_ABONOS.CODIGO%TYPE, 
    UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO
)
AS
    MI_VALORES               PCK_SUBTIPOS.TI_VALORES;
    MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;
    MI_ACME                  PCK_SUBTIPOS.TI_ENTERO;
    MI_ERROR                 PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
    
    MI_CAMPOS := 'ESTADO = ''X'', MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE ';
    MI_CONDICION := 'COMPANIA       = '''||UN_COMPANIA ||'''
                 AND TIPO_ABONO   = '''||UN_TIPOABONO||'''
                 AND CODIGO_ABONO = ' || UN_NROABONO || ' 
                 AND ESTADO = ''A'' ';
    BEGIN
      BEGIN
        MI_ACME := PCK_DATOS.FC_ACME(UN_ACCION    => 'M'
                                   ,UN_TABLA     => 'SF_DETALLE_ABONO'
                                   ,UN_CAMPOS    => MI_CAMPOS
                                   ,UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        MI_ERROR(1).CLAVE := 'PROCESO';
        MI_ERROR(1).VALOR := 'anulacion de cuotas de pagos parciales';      

        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSERTPAGPARCIAL
                                ,UN_TABLAERROR => 'SF_DETALLE_ABONO'
                                ,UN_REEMPLAZOS => MI_ERROR);
    END;    
    
    
END PR_ANULARCUOTAS_PAGOPARCIAL;


PROCEDURE PR_ANULARFRA_PAGOPARCIAL (
    UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TIPOCOBRO          IN SF_FACTURA.TIPO_FACTURA%TYPE,
    UN_FACTURA            IN SF_FACTURA.NUMERO_FACTURA%TYPE,
    UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO
)
AS
    MI_VALORES               PCK_SUBTIPOS.TI_VALORES;
    MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;
    MI_ACME                  PCK_SUBTIPOS.TI_ENTERO;
    MI_ERROR                 PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
    
    -- SE DEBE ANULAR LA FACTURA QUE NO FUE CAUSADA PARA QUE NO SUME EN EL TOTAL DE DEUDA A DISTRIBUIR
    MI_CAMPOS := 'ANULADA = -1, MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE ';
    MI_CONDICION := 'COMPANIA               = '''||UN_COMPANIA ||'''
                 AND TIPOFRA_PAGOPARCIAL    = '''||UN_TIPOCOBRO||'''
                 AND NROFRA_PAGOPARCIAL     = ' || UN_FACTURA || ' 
                 AND INTERFAZADA            = 0 ';
    BEGIN
      BEGIN
        MI_ACME := PCK_DATOS.FC_ACME(UN_ACCION    => 'M'
                                   ,UN_TABLA     => 'SF_FACTURA'
                                   ,UN_CAMPOS    => MI_CAMPOS
                                   ,UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        MI_ERROR(1).CLAVE := 'PROCESO';
        MI_ERROR(1).VALOR := 'anulaci�n de facturas de pagos parciales pendientes';      

        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSERTPAGPARCIAL
                                ,UN_TABLAERROR => 'SF_FACTURA'
                                ,UN_REEMPLAZOS => MI_ERROR);
    END;    
END PR_ANULARFRA_PAGOPARCIAL;

FUNCTION FC_CONSULTAR_ABCODIGO(
/*
      NAME              : FC_CONSULTAR_ABCODIGO 
      AUTHORS           : STEFANINI SYSMAN  SAS
      AUTHOR MIGRACION  : 
      DATE CREATED      : 20/10/2021 
      TIME              : 9:00 AM
      SOURCE MODULE     : 
      DESCRIPTION       : 
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : Funcion para obtener el codigo del abono asociado a una factura si esta tiene abonos asociado.

    @NAME:   consultarCodigoAbono
    @METHOD: GET
*/
  UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOCOBRO          IN PCK_SUBTIPOS.TI_TIPO_COBRO,
  UN_FACTURA            IN SF_FACTURA.NUMERO_FACTURA%TYPE
)RETURN NUMBER 
    AS
    MI_ABCODIGO  NUMBER (20,0);
    BEGIN
        BEGIN
        SELECT CODIGO 
        INTO MI_ABCODIGO
        FROM SF_ABONOS
        WHERE SF_ABONOS.COMPANIA = UN_COMPANIA
        AND SF_ABONOS.TIPO_FACT = UN_TIPOCOBRO
        AND SF_ABONOS.NRO_FACT = UN_FACTURA;
            
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_ABCODIGO := 0;
        END;    
        RETURN MI_ABCODIGO;
END FC_CONSULTAR_ABCODIGO;

FUNCTION FC_RECAUDAR_PAGOPARCIAL (
/*
      NAME              : FC_RECAUDAR_PAGOPARCIAL 
      AUTHORS           : STEFANINI SYSMAN  SAS
      AUTHOR MIGRACION  : 
      DATE CREATED      : 20/12/2021 
      TIME              : 14:39
      SOURCE MODULE     : 
      DESCRIPTION       : Funcion para registrar el pago parcial activo de una factura
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 

    @NAME:   recaudarPagoParcial
    @METHOD: GET
*/
  UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANO                IN PCK_SUBTIPOS.TI_ENTERO,
  UN_TIPOFRA            IN PCK_SUBTIPOS.TI_TIPO_COBRO,
  UN_FACTURA            IN SF_FACTURA.NUMERO_FACTURA%TYPE,
  UN_FECHAPAGO          IN DATE,
  UN_FECHAPAGOBANCO     IN DATE,
  UN_CUENTARECAUDO      IN VARCHAR2, 
  UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO
) RETURN VARCHAR
AS
    MI_RTAPROCESO      VARCHAR2(250 CHAR);
    MI_TIPOABONO       SF_ABONOS.TIPO%TYPE;
    MI_NROABONO        SF_ABONOS.CODIGO%TYPE;
    MI_INTERFAZADA     PCK_SUBTIPOS.TI_LOGICO; 
    MI_CLASECUENTA     VARCHAR2(100 CHAR);
    MI_CPTERECAUDO     TIPO_COMPROBANTE.CODIGO%TYPE;
    MI_NRO_CPTEING     COMPROBANTE_CNT.NUMERO%TYPE; 
    MI_CAMPOS          PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES         PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION       PCK_SUBTIPOS.TI_CONDICION;
    MI_MSGERROR        PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_DESCRIPCION     COMPROBANTE_CNT.DESCRIPCION%TYPE ;
    MI_CONSECUTIVODET  PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_CPTOINTERES     SF_CONCEPTOS.CODIGO%TYPE;
    MI_CADENAINSERTAR  CLOB DEFAULT ' ';
    MI_CUENTADETALLE   PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_CPTEINT_CAU     TIPO_COMPROBANTE.CODIGO%TYPE;
    MI_NRO_CPTE_INT     COMPROBANTE_CNT.NUMERO%TYPE;
    MI_CPTOINTERES_EX   SF_CONCEPTOS.CODIGO%TYPE;
    MI_TIENE_RECAUDO    PCK_SUBTIPOS.TI_ENTERO;
    MI_EXTEM_RE         PCK_SUBTIPOS.TI_DOBLE;
BEGIN
    
    MI_RTAPROCESO := FC_CUOTASACTIVAS_PAGOPARCIAL (                        
                          UN_COMPANIA  => UN_COMPANIA,
                          UN_ANO       => UN_ANO,
                          UN_TIPOFRA   => UN_TIPOFRA,
                          UN_FACTURA   => UN_FACTURA); 
    IF INSTR(MI_RTAPROCESO, '-') = 0 THEN
        RETURN MI_RTAPROCESO;
    ELSE
        MI_TIPOABONO := SUBSTR(MI_RTAPROCESO,1,INSTR(MI_RTAPROCESO, '-')-1);
        MI_RTAPROCESO := SUBSTR(MI_RTAPROCESO,INSTR(MI_RTAPROCESO, '-')+1, LENGTH(MI_RTAPROCESO));
        MI_NROABONO := SUBSTR(MI_RTAPROCESO,1,INSTR(MI_RTAPROCESO, '-')-1);
        -- CONSULTAR EL ABONO ACTIVO PARA REGISTRAR EL PAGO Y AFECTACION A LAS FACTURAS RELACIONADAS
        FOR RSFRA IN (
                SELECT DCA.TIPOFACT, DCA.NROFACT, F.TERCERO, F.SUCURSAL, F.OBSERVACIONES, F.TIPOCOBRO, F.CODIGO_COBRO
                FROM    SF_DETALLE_CUOTAABONO DCA
                    INNER JOIN SF_DETALLE_ABONO  DA
                        ON  DCA.COMPANIA = DA.COMPANIA
                        AND DCA.TIPO_ABONO = DA.TIPO_ABONO
                        AND DCA.CODIGO_ABONO = DA.CODIGO_ABONO
                        AND DCA.CUOTA = DA.CUOTA
                    INNER JOIN SF_FACTURA F
                        ON  DCA.COMPANIA = F.COMPANIA
                        AND DCA.TIPOFACT = F.TIPO_FACTURA
                        AND DCA.NROFACT = F.NUMERO_FACTURA
                WHERE  DCA.COMPANIA     = UN_COMPANIA
                AND    DCA.TIPO_ABONO   = MI_TIPOABONO
                AND    DCA.CODIGO_ABONO = MI_NROABONO
                AND    DA.ESTADO        = 'A' 
                AND    F.INTERFAZADA    = 0  
            )
            LOOP
            MI_INTERFAZADA := 0;
            MI_INTERFAZADA := PCK_FACT_GENERAL_COM3.FC_INTERFAZCONTABLE(
                                                       UN_COMPANIA         => UN_COMPANIA,  
                                                       UN_TIPO             => RSFRA.TIPOCOBRO,
                                                       UN_NUMERO           => RSFRA.CODIGO_COBRO,
                                                       UN_FECHA            => UN_FECHAPAGO,
                                                       UN_TERCERO          => RSFRA.TERCERO,
                                                       UN_SUCURSAL         => RSFRA.SUCURSAL,
                                                       UN_DESCRIPCION      => RSFRA.OBSERVACIONES,
                                                       UN_MANEJAINVENTARIO => 0,
                                                       UN_USUARIO          => UN_USUARIO);
            IF MI_INTERFAZADA = 0 THEN
                RETURN 'Se presentaron inconvenientes al causar la factura ' || RSFRA.TIPOFACT || ' - ' || RSFRA.NROFACT;
            END IF;
        END LOOP;  -- FIN DE VERIFICAR LAS FACTURAS QUE HACEN PARTE DEL PAGO PARCIAL Y QUE NO SE HAN INTERFAZADO O CAUSADO 
        
        -- CONSULTAR CONFIGURACION DEL PARAMETRO - SF CALSE CUENTA PARA EL RECAUDO - 
         MI_CLASECUENTA := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                              UN_NOMBRE         =>  'SF CLASE CUENTA PARA EL RECAUDO',
                                              UN_MODULO 	    =>	PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                              UN_FECHA_PAR      =>  SYSDATE,
                                              UN_IND_MAYUS      =>  0
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
            MI_CLASECUENTA := REPLACE(REPLACE(MI_CLASECUENTA,' ',''),',',''',''');
        END IF;   
        
        --Ticket 7748698: Se crea proceso para generar la causación de los intereses
        -- CONSULTAR EL TIPO DE COMPROBANTE PARA LA CAUSACIÓN DE LOS INTERESES
        BEGIN
            SELECT  CPTE_CAUSACION_INTERES
            INTO    MI_CPTEINT_CAU
            FROM    SF_TIPO_COBRO
            WHERE   COMPANIA = UN_COMPANIA
              AND   ANO      = UN_ANO
              AND   CODIGO   = UN_TIPOFRA; 
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_CPTEINT_CAU := 'NA';
        END;

        IF MI_CPTEINT_CAU = 'NA' THEN
            RETURN 'El tipo de cobro ' || UN_TIPOFRA || 'no tiene configurado un tipo de comprobante para el proceso de causación de los intereses';
        END IF;

        MI_CPTOINTERES := PCK_SYSMAN_UTL.FC_VALIDARPARAMETRO
                                     (UN_COMPANIA  => UN_COMPANIA
                                     ,UN_PARAMETRO => 'SF CONCEPTO INTERESES PAGOS PARCIALES'
                                     ,UN_MODULO    => PCK_DATOS.MODULOFACTURACIONGENERAL );
        MI_CPTOINTERES_EX := PCK_SYSMAN_UTL.FC_VALIDARPARAMETRO
                             (UN_COMPANIA  => UN_COMPANIA
                             ,UN_PARAMETRO => 'SF CONCEPTO INTERESES EXTEMPORANEOS PAGOS PARCIALES'
                             ,UN_MODULO    => PCK_DATOS.MODULOFACTURACIONGENERAL );
        -- GENERAR EL CONSECUTIVO PARA EL COMPROBANTE
         MI_NRO_CPTE_INT := PCK_CONTABILIDAD_CPTE.FC_ENUMERARCOMPROBANTECNT (
                                                                        UN_COMPANIA     => UN_COMPANIA
                                                                       ,UN_ANIO         => TO_CHAR(UN_FECHAPAGO,'YYYY')
                                                                       ,UN_TIPO         => MI_CPTEINT_CAU
                                                                       ,UN_NUMERO       => 0
                                                                       ,UN_CENTRO_COSTO => '');
        MI_CONSECUTIVODET := 1;
        
        FOR RS IN (
                SELECT  DCA.TIPO_ABONO, DCA.CODIGO_ABONO, DCA.TIPOFACT, DCA.NROFACT, F.TERCERO, F.SUCURSAL, F.AUXILIAR,
                        F.CENTRO_COSTO, F.TIPOCOBRO, F.CODIGO_COBRO, F.ANO_CPTE, F.TIPO_CPTE, F.NRO_CPTE,
                        DCA.CUOTA, CP.CODIGO CONCEPTO, DCA.VALOR_BASE VALOR, CP.CUENTACREDITOBASE CUENTA,  F.NROFRA_PAGOPARCIAL, F.TIPOFRA_PAGOPARCIAL, F.NUMERO_RESOLUCION,
                        TO_CHAR(FECHA_RESOLUCION,'DD/MM/YYYY') FECHA_RESOLUCION, TO_CHAR(FECHA_EJECUTORA,'DD/MM/YYYY') FECHA_EJECUTORA, NUMERO_EXPEDIENTE
                FROM    SF_DETALLE_CUOTAABONO DCA
                    INNER JOIN SF_DETALLE_ABONO  DA
                        ON  DCA.COMPANIA = DA.COMPANIA
                        AND DCA.TIPO_ABONO = DA.TIPO_ABONO
                        AND DCA.CODIGO_ABONO = DA.CODIGO_ABONO
                        AND DCA.CUOTA = DA.CUOTA
                    INNER JOIN SF_FACTURA F
                        ON  DCA.COMPANIA = F.COMPANIA
                        AND DCA.TIPOFACT = F.TIPO_FACTURA
                        AND DCA.NROFACT = F.NUMERO_FACTURA
                    INNER JOIN SF_CONCEPTOS CP
                        ON DCA.COMPANIA = CP.COMPANIA
                        AND DCA.CONCEPTO = CP.CODIGO
                        AND DCA.ANO = CP.ANO
                WHERE  DCA.COMPANIA     = UN_COMPANIA
                AND    DCA.TIPO_ABONO   = MI_TIPOABONO
                AND    DCA.CODIGO_ABONO = MI_NROABONO
                AND    (DCA.CONCEPTO     = MI_CPTOINTERES OR DCA.CONCEPTO     = MI_CPTOINTERES_EX)
                AND    DA.ESTADO        = 'A')
            LOOP

            MI_DESCRIPCION := 'CAUSACIÓN INETERES SEGÚN ' || RS.NUMERO_RESOLUCION || ' DE ' || RS.FECHA_RESOLUCION || ' CONSTANCIA DE EJECUTORIA EL DIA ' ||
                    RS.FECHA_EJECUTORA || 'DENTRO DEL EXPEDIENTE' || RS.NUMERO_EXPEDIENTE;

            IF MI_CONSECUTIVODET = 1 THEN
                -- CREACION DEL HEADER DEL COMPROBANTE DE INGRESO
                BEGIN
                    BEGIN      

                        MI_CAMPOS :='COMPANIA ,
                                          ANO ,
                                          TIPO ,
                                          NUMERO ,
                                          FECHA ,
                                          HORA,
                                          DESCRIPCION ,
                                          NRO_DOCUMENTO,
                                          FECHACONSIGNACION,
                                          FECHAPAGADOGN,
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
                                                  '||EXTRACT(YEAR FROM UN_FECHAPAGO)||',
                                                  '''||MI_CPTEINT_CAU||''' TIPO ,
                                                  '||MI_NRO_CPTE_INT||' ,
                                                  '''||UN_FECHAPAGO||''' ,
                                                  SYSDATE,
                                                  '''||MI_DESCRIPCION||''' ,
                                                  '''||UN_FECHAPAGOBANCO||''' ,
                                                  '''||UN_FECHAPAGOBANCO||''' ,
                                                  '''||UN_FECHAPAGOBANCO||''' ,
                                                  TERCERO ,
                                                  SUCURSAL ,
                                                  CENTRO_COSTO,
                                                  AUXILIAR,
                                                  0 ,
                                                  0 ,
                                                  0 ,
                                                  FECHA_VCN_DOC ,
                                                  '''||UN_USUARIO||''' ,
                                                  SYSDATE
                                             FROM COMPROBANTE_CNT
                                             WHERE COMPANIA = '''||UN_COMPANIA||'''
                                               AND ANO      = '||RS.ANO_CPTE||'
                                               AND TIPO     = '''||RS.TIPO_CPTE||'''
                                               AND NUMERO   = '||RS.NRO_CPTE||' ';


                        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA  => 'COMPROBANTE_CNT',
                                                          UN_ACCION   => 'IS',
                                                          UN_CAMPOS   => MI_CAMPOS,
                                                          UN_VALORES  => MI_VALORES);
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                    END;

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
                        MI_MSGERROR(1).CLAVE := 'NUMERO';
                        MI_MSGERROR(1).VALOR := MI_NRO_CPTE_INT;
                        MI_MSGERROR(2).CLAVE := 'TIPO';
                        MI_MSGERROR(2).VALOR := MI_CPTEINT_CAU;
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                   UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSCOMPRINGRESO
                                                  ,UN_REEMPLAZOS  => MI_MSGERROR);
                END;
            END IF;
              --CREACION DE DETALLES QUE VAN AL CREDITO   
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
                                       '||EXTRACT(YEAR FROM UN_FECHAPAGO)||',
                                       '''||MI_CPTEINT_CAU||''',
                                       '||MI_NRO_CPTE_INT||',
                                       '''||RS.CUENTA||''',
                                       '||MI_CONSECUTIVODET||',
                                       ''D'',
                                       '''||UN_FECHAPAGO||''',
                                       '''||UN_FECHAPAGO||''',
                                       '''||MI_DESCRIPCION||''',
                                       '''||RS.TERCERO||''',
                                       '''||RS.SUCURSAL||''',
                                       '''||RS.CENTRO_COSTO||''',
                                       '''||RS.AUXILIAR||''',
                                       0,
                                       '||RS.VALOR||''
                                       ;

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
                        MI_MSGERROR(1).VALOR := MI_NRO_CPTEING;
                        MI_MSGERROR(2).CLAVE := 'TIPO';
                        MI_MSGERROR(2).VALOR := MI_CPTERECAUDO;
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_INSDETACOMPCNT);
                END;
            --CREACION DE DETALLES QUE VAN AL DEBITO 
            
            FOR RS1 IN (
                    SELECT DCA.COMPANIA, DCA.TIPOFACT, DCA.NROFACT, DCA.CONCEPTO, DCA.VALOR_BASE VALOR, CP.CUENTADEBITOBASE CUENTA
                    FROM    SF_DETALLE_CUOTAABONO DCA
                    INNER JOIN SF_DETALLE_ABONO  DA
                        ON  DCA.COMPANIA = DA.COMPANIA
                        AND DCA.TIPO_ABONO = DA.TIPO_ABONO
                        AND DCA.CODIGO_ABONO = DA.CODIGO_ABONO
                        AND DCA.CUOTA = DA.CUOTA
                    INNER JOIN SF_CONCEPTOS CP
                        ON DCA.COMPANIA = CP.COMPANIA
                        AND DCA.CONCEPTO = CP.CODIGO
                        AND DCA.ANO = CP.ANO
                   WHERE  DCA.COMPANIA     = UN_COMPANIA
                    AND    DCA.TIPO_ABONO   = MI_TIPOABONO
                    AND    DCA.CODIGO_ABONO = MI_NROABONO
                    AND    DA.ESTADO        = 'A'
                    AND    DCA.CONCEPTO     = RS.CONCEPTO)
            LOOP

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
                                  VALOR_CREDITO,
                                  EJECUCION_DEBITO, 
                                  EJECUCION_CREDITO
                                  ';

                    MI_VALORES := ''''||UN_COMPANIA||''',
                                   '||EXTRACT(YEAR FROM UN_FECHAPAGO)||',
                                   '''||MI_CPTEINT_CAU||''',
                                   '||MI_NRO_CPTE_INT||',
                                   '''||RS1.CUENTA||''',
                                   '||MI_CONSECUTIVODET||',
                                   ''D'',
                                   '''||UN_FECHAPAGO||''',
                                   '''||UN_FECHAPAGO||''',
                                   '''||MI_DESCRIPCION||''',
                                   '''||RS.TERCERO||''',
                                   '''||RS.SUCURSAL||''',
                                   '''||RS.CENTRO_COSTO||''',
                                   '''||RS.AUXILIAR||''',
                                   '||RS1.VALOR||',
                                   0,
                                   '||RS1.VALOR||',
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
                    MI_MSGERROR(1).VALOR := MI_NRO_CPTEING;
                    MI_MSGERROR(2).CLAVE := 'TIPO';
                    MI_MSGERROR(2).VALOR := MI_CPTERECAUDO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD => SQLCODE,
                    UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_INSDETACOMPCNT);
            END;

            END LOOP ;  
        END LOOP ;
     
     -- CONSULTAR EL TIPO DE COMPROBANTE PARA EL RECAUDO 
        MI_CPTERECAUDO := 'NA';
        SELECT  CPTE_RECAUDO
        INTO    MI_CPTERECAUDO
        FROM    SF_TIPO_COBRO
        WHERE   COMPANIA = UN_COMPANIA
          AND   ANO      = UN_ANO
          AND   CODIGO   = UN_TIPOFRA; 
          
        IF MI_CPTERECAUDO = 'NA' THEN
            RETURN 'El tipo de cobro ' || UN_TIPOFRA || 'no tiene configurado un tipo de comprobante para el proceso de recaudo';
        END IF;
        
        -- VERIFICAR QUE EL TIPO DE COMPROBANTE PARA EL INGRESO EXISTE EN LA CONFIGURACI�N DE CONTABILIDAD 
        
        -- GENERAR EL CONSECUTIVO PARA EL COMPROBANTE DE INGRESO
         MI_NRO_CPTEING := PCK_CONTABILIDAD_CPTE.FC_ENUMERARCOMPROBANTECNT (
                                                                        UN_COMPANIA     => UN_COMPANIA
                                                                       ,UN_ANIO         => TO_CHAR(UN_FECHAPAGO,'YYYY')
                                                                       ,UN_TIPO         => MI_CPTERECAUDO
                                                                       ,UN_NUMERO       => 0
                                                                       ,UN_CENTRO_COSTO => '');
        MI_CONSECUTIVODET := 1;
        
        
        
        FOR RS IN (
                SELECT  DCA.TIPO_ABONO, DCA.CODIGO_ABONO, DCA.TIPOFACT, DCA.NROFACT, F.TERCERO, F.SUCURSAL, F.AUXILIAR,
                        F.CENTRO_COSTO, F.OBSERVACIONES, F.TIPOCOBRO, F.CODIGO_COBRO, F.ANO_CPTE, F.TIPO_CPTE, F.NRO_CPTE,
                        DCA.CUOTA, SUM(DCA.TOTAL_CUOTA) TOTAL_CUOTA,  F.NROFRA_PAGOPARCIAL, F.TIPOFRA_PAGOPARCIAL, F.NUMERO_RESOLUCION,
                        TO_CHAR(FECHA_RESOLUCION,'DD/MM/YYYY') FECHA_RESOLUCION, TO_CHAR(FECHA_EJECUTORA,'DD/MM/YYYY') FECHA_EJECUTORA, NUMERO_EXPEDIENTE,
                        DA.INTERES_PAG_PARCIAL, INTERES_EXPEM
                FROM    SF_DETALLE_CUOTAABONO DCA
                    INNER JOIN SF_DETALLE_ABONO  DA
                        ON  DCA.COMPANIA = DA.COMPANIA
                        AND DCA.TIPO_ABONO = DA.TIPO_ABONO
                        AND DCA.CODIGO_ABONO = DA.CODIGO_ABONO
                        AND DCA.CUOTA = DA.CUOTA
                    INNER JOIN SF_FACTURA F
                        ON  DCA.COMPANIA = F.COMPANIA
                        AND DCA.TIPOFACT = F.TIPO_FACTURA
                        AND DCA.NROFACT = F.NUMERO_FACTURA
                    INNER JOIN SF_CONCEPTOS CP
                        ON DCA.COMPANIA = CP.COMPANIA
                        AND DCA.CONCEPTO = CP.CODIGO
                        AND DCA.ANO = CP.ANO
                WHERE  DCA.COMPANIA     = UN_COMPANIA
                AND    DCA.TIPO_ABONO   = MI_TIPOABONO
                AND    DCA.CODIGO_ABONO = MI_NROABONO
                AND    DA.ESTADO        = 'A'
                GROUP BY DCA.TIPO_ABONO, DCA.CODIGO_ABONO, DCA.TIPOFACT, DCA.NROFACT, F.TERCERO, F.SUCURSAL, F.AUXILIAR,
                        F.CENTRO_COSTO, F.OBSERVACIONES, F.TIPOCOBRO, F.CODIGO_COBRO, F.ANO_CPTE, F.TIPO_CPTE, F.NRO_CPTE,
                        DCA.CUOTA, F.NROFRA_PAGOPARCIAL, F.TIPOFRA_PAGOPARCIAL,F.NUMERO_RESOLUCION,
                        FECHA_RESOLUCION, FECHA_EJECUTORA, NUMERO_EXPEDIENTE, DA.INTERES_PAG_PARCIAL, INTERES_EXPEM)
            LOOP

            MI_DESCRIPCION := 'Canc ' || 
                    CASE WHEN RS.TIPOFRA_PAGOPARCIAL IS NULL THEN RS.TIPOFACT ELSE RS.TIPOFRA_PAGOPARCIAL END || ' - ' ||
                    CASE WHEN RS.NROFRA_PAGOPARCIAL IS NULL THEN RS.NROFACT ELSE RS.NROFRA_PAGOPARCIAL END || ' CAUSACIÓN ' ||
                    RS.NUMERO_RESOLUCION || ' DE ' || RS.FECHA_RESOLUCION || ' CONSTANCIA DE EJECUTORIA EL DÍA ' ||
                    RS.FECHA_EJECUTORA;

            IF MI_CONSECUTIVODET = 1 THEN
                -- CREACION DEL HEADER DEL COMPROBANTE DE INGRESO
                BEGIN
                    BEGIN      
        
                        MI_CAMPOS :='COMPANIA ,
                                          ANO ,
                                          TIPO ,
                                          NUMERO ,
                                          FECHA ,
                                          HORA,
                                          DESCRIPCION ,
                                          NRO_DOCUMENTO,
                                          FECHACONSIGNACION,
                                          FECHAPAGADOGN,
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
                                                  '||EXTRACT(YEAR FROM UN_FECHAPAGO)||',
                                                  '''||MI_CPTERECAUDO||''' TIPO ,
                                                  '||MI_NRO_CPTEING||' ,
                                                  '''||UN_FECHAPAGO||''' ,
                                                  SYSDATE,
                                                  '''||MI_DESCRIPCION||''' ,
                                                  '''||UN_FECHAPAGOBANCO||''' ,
                                                  '''||UN_FECHAPAGOBANCO||''' ,
                                                  '''||UN_FECHAPAGOBANCO||''' ,
                                                  TERCERO ,
                                                  SUCURSAL ,
                                                  CENTRO_COSTO,
                                                  AUXILIAR,
                                                  0 ,
                                                  0 ,
                                                  VLR_DOCUMENTO,
                                                  FECHA_VCN_DOC ,
                                                  '''||UN_USUARIO||''',
                                                  SYSDATE
                                             FROM COMPROBANTE_CNT
                                             WHERE COMPANIA = '''||UN_COMPANIA||'''
                                               AND ANO      = '||RS.ANO_CPTE||'
                                               AND TIPO     = '''||RS.TIPO_CPTE||'''
                                               AND NUMERO   = '||RS.NRO_CPTE||' ';


                        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA  => 'COMPROBANTE_CNT',
                                                          UN_ACCION   => 'IS',
                                                          UN_CAMPOS   => MI_CAMPOS,
                                                          UN_VALORES  => MI_VALORES);
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                    END;
                    
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
                        MI_MSGERROR(1).CLAVE := 'NUMERO';
                        MI_MSGERROR(1).VALOR := MI_NRO_CPTEING;
                        MI_MSGERROR(2).CLAVE := 'TIPO';
                        MI_MSGERROR(2).VALOR := MI_CPTERECAUDO;
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                   UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSCOMPRINGRESO
                                                  ,UN_REEMPLAZOS  => MI_MSGERROR);
                END;  
                --CREACION DE DETALLES QUE VAN AL DEBITO   
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
                                       '||EXTRACT(YEAR FROM UN_FECHAPAGO)||',
                                       '''||MI_CPTERECAUDO||''',
                                       '||MI_NRO_CPTEING||',
                                       '''||UN_CUENTARECAUDO||''',
                                       '||MI_CONSECUTIVODET||',
                                       ''D'',
                                       '''||UN_FECHAPAGO||''',
                                       '''||UN_FECHAPAGO||''',
                                       '''||MI_DESCRIPCION||''',
                                       '''||RS.TERCERO||''',
                                       '''||RS.SUCURSAL||''',
                                       '''||RS.CENTRO_COSTO||''',
                                       '''||RS.AUXILIAR||''',
                                       '||RS.TOTAL_CUOTA||',
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
                        MI_MSGERROR(1).VALOR := MI_NRO_CPTEING;
                        MI_MSGERROR(2).CLAVE := 'TIPO';
                        MI_MSGERROR(2).VALOR := MI_CPTERECAUDO;
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_INSDETACOMPCNT);
                END;
            END IF;-- FIN DE VALIDAR SI EL CONSECUTIVODET ES CERO PARA CREAR EL DETALLE DEBITO             
            
            --CREACION DE DETALLES QUE VAN AL CREDITO 
            --Ticket#7737169: Se modifica el proceso de creaci�n de los valores credito para tener en cuenta el capital y el interes
            
            MI_CPTOINTERES := PCK_SYSMAN_UTL.FC_VALIDARPARAMETRO
                             (UN_COMPANIA  => UN_COMPANIA
                             ,UN_PARAMETRO => 'SF CONCEPTO INTERESES PAGOS PARCIALES'
                             ,UN_MODULO    => PCK_DATOS.MODULOFACTURACIONGENERAL );
                             
            FOR RS1 IN (
                    SELECT DCA.COMPANIA, DCA.TIPOFACT, DCA.NROFACT, DCA.CONCEPTO, DA.CAPITAL VALOR, CP.CUENTADEBITOBASE CUENTA,
                    F.TIPO_CPTE, F.NRO_CPTE, DCC.CONSECUTIVO CONSECUTIVOAFECTADO, DCA.ANO, PC.RUBRO
                    FROM    SF_DETALLE_CUOTAABONO DCA
                    INNER JOIN SF_DETALLE_ABONO  DA
                        ON  DCA.COMPANIA = DA.COMPANIA
                        AND DCA.TIPO_ABONO = DA.TIPO_ABONO
                        AND DCA.CODIGO_ABONO = DA.CODIGO_ABONO
                        AND DCA.CUOTA = DA.CUOTA
                    INNER JOIN SF_FACTURA F
                        ON  DCA.COMPANIA = F.COMPANIA
                        AND DCA.TIPOFACT = F.TIPO_FACTURA
                        AND DCA.NROFACT = F.NUMERO_FACTURA
                    INNER JOIN SF_CONCEPTOS CP
                        ON DCA.COMPANIA = CP.COMPANIA
                        AND DCA.CONCEPTO = CP.CODIGO
                        AND DCA.ANO = CP.ANO
                    INNER JOIN DETALLE_COMPROBANTE_CNT DCC
                        ON F.COMPANIA = DCC.COMPANIA
                        AND DCA.ANO = DCC.ANO
                        AND F.TIPO_CPTE = DCC.TIPO_CPTE
                        AND F.NRO_CPTE = DCC.COMPROBANTE
                        AND CP.CUENTADEBITOBASE = DCC.CUENTA
                    LEFT JOIN PLAN_PPTAL_CUENTACNT PC
                        ON DCC.COMPANIA = PC.COMPANIA
                        AND EXTRACT(YEAR FROM UN_FECHAPAGO) = PC.ANO
                        AND DCC.CUENTA = PC.CUENTA_CONTABLE
                    WHERE  DCA.COMPANIA     = UN_COMPANIA
                    AND    DCA.TIPO_ABONO   = MI_TIPOABONO
                    AND    DCA.CODIGO_ABONO = MI_NROABONO
                    AND    DA.ESTADO        = 'A'
                    AND    ROWNUM = 1
                    UNION
                    SELECT DCA.COMPANIA, DCA.TIPOFACT, DCA.NROFACT, DCA.CONCEPTO, DCA.VALOR_BASE VALOR, CP.CUENTADEBITOBASE CUENTA,
                    MI_CPTEINT_CAU TIPO_CPTE, MI_NRO_CPTE_INT NRO_CPTE, DCC.CONSECUTIVO CONSECUTIVOAFECTADO, PC.ANO ANO, PC.RUBRO
                    FROM    SF_DETALLE_CUOTAABONO DCA
                    INNER JOIN SF_DETALLE_ABONO  DA
                        ON  DCA.COMPANIA = DA.COMPANIA
                        AND DCA.TIPO_ABONO = DA.TIPO_ABONO
                        AND DCA.CODIGO_ABONO = DA.CODIGO_ABONO
                        AND DCA.CUOTA = DA.CUOTA
                    INNER JOIN SF_CONCEPTOS CP
                        ON DCA.COMPANIA = CP.COMPANIA
                        AND DCA.CONCEPTO = CP.CODIGO
                        AND DCA.ANO = CP.ANO
                    INNER JOIN DETALLE_COMPROBANTE_CNT DCC
                        ON DCA.COMPANIA = DCC.COMPANIA
                        AND EXTRACT(YEAR FROM UN_FECHAPAGO) = DCC.ANO
                        AND CP.CUENTADEBITOBASE = DCC.CUENTA
                        AND DCA.VALOR_BASE = DCC.VALOR_DEBITO
                    LEFT JOIN (SELECT COMPANIA, ANO, CUENTA_CONTABLE, MAX(RUBRO) RUBRO
                                FROM PLAN_PPTAL_CUENTACNT
                                WHERE COMPANIA = UN_COMPANIA
                                AND ANO = EXTRACT(YEAR FROM UN_FECHAPAGO)
                                GROUP BY COMPANIA, ANO, CUENTA_CONTABLE) PC
                        ON DCA.COMPANIA = PC.COMPANIA
                        AND EXTRACT(YEAR FROM UN_FECHAPAGO) = PC.ANO
                        AND CP.CUENTADEBITOBASE = PC.CUENTA_CONTABLE
                   WHERE  DCA.COMPANIA     = UN_COMPANIA
                    AND    DCA.TIPO_ABONO   = MI_TIPOABONO
                    AND    DCA.CODIGO_ABONO = MI_NROABONO
                    AND    DCC.TIPO_CPTE    = MI_CPTEINT_CAU
                    AND    DCC.COMPROBANTE = MI_NRO_CPTE_INT
                    AND    DA.ESTADO        = 'A')
            LOOP
            
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
                                  VALOR_CREDITO,
                                  EJECUCION_DEBITO, 
                                  EJECUCION_CREDITO,
                                  ANO_AFECT, 
                                  TIPO_CPTE_AFECT, 
                                  CMPTE_AFECTADO, 
                                  CONSECUTIVOAFECTADO,
                                  CUENTAPPTAL';
            
                    MI_VALORES := ''''||UN_COMPANIA||''',
                                   '||EXTRACT(YEAR FROM UN_FECHAPAGO)||',
                                   '''||MI_CPTERECAUDO||''',
                                   '||MI_NRO_CPTEING||',
                                   '''||RS1.CUENTA||''',
                                   '||MI_CONSECUTIVODET||',
                                   ''D'',
                                   '''||UN_FECHAPAGO||''',
                                   '''||UN_FECHAPAGO||''',
                                   '''||MI_DESCRIPCION||''',
                                   '''||RS.TERCERO||''',
                                   '''||RS.SUCURSAL||''',
                                   '''||RS.CENTRO_COSTO||''',
                                   '''||RS.AUXILIAR||''',
                                   0,
                                   '||RS1.VALOR||',
                                   0,
                                   '||RS1.VALOR||',
                                   '''||RS1.ANO||''',
                                   '''||RS1.TIPO_CPTE||''',
                                   '''||RS1.NRO_CPTE||''',
                                   '''||RS1.CONSECUTIVOAFECTADO||''',
                                   '''||RS1.RUBRO||'''';

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
                    MI_MSGERROR(1).VALOR := MI_NRO_CPTEING;
                    MI_MSGERROR(2).CLAVE := 'TIPO';
                    MI_MSGERROR(2).VALOR := MI_CPTERECAUDO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD => SQLCODE,
                    UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_INSDETACOMPCNT);
            END;
            
            END LOOP ;
            -- ACTUALIZAR EL ESTADO DE LA CUOTA A PAGA
            
            BEGIN
                    SELECT COUNT(SF_DETALLE_ABONO.COMPANIA)
                    INTO MI_TIENE_RECAUDO
                    FROM SF_DETALLE_ABONO
                    WHERE   SF_DETALLE_ABONO.COMPANIA       = UN_COMPANIA
                    AND     SF_DETALLE_ABONO.TIPO_ABONO     = RS.TIPO_ABONO
                    AND     SF_DETALLE_ABONO.CODIGO_ABONO   = RS.CODIGO_ABONO
                    AND     SF_DETALLE_ABONO.ESTADO        = 'R';
                EXCEPTION
                       WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
                            MI_TIENE_RECAUDO := 0;
                END;
                
            BEGIN 
                    SELECT NVL(SUM(INTERES_EXPEM),0) EXTEM_RE
                    INTO MI_EXTEM_RE
                    FROM SF_DETALLE_ABONO
                    WHERE COMPANIA = UN_COMPANIA
                    AND TIPO_ABONO = RS.TIPO_ABONO
                    AND CODIGO_ABONO = RS.CODIGO_ABONO
                    AND ESTADO = 'R';
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_EXTEM_RE := 0;
                END;
            
            BEGIN
                BEGIN
                    MI_CAMPOS := 'FECHA_PAGO = '''||UN_FECHAPAGOBANCO||''',
                                      ANODOC = '||EXTRACT(YEAR FROM UN_FECHAPAGOBANCO)||',
                                     TIPODOC = '''||MI_CPTERECAUDO||''',
                                      NRODOC = '||MI_NRO_CPTEING||',
                                  BANCO_PAGO = '''||UN_CUENTARECAUDO||''',
                                      ESTADO = ''R'' ,
                                 MODIFIED_BY = '''||UN_USUARIO||''',
                               DATE_MODIFIED = SYSDATE ';
        
                    MI_CONDICION := 'COMPANIA       = '''||UN_COMPANIA||'''
                                AND  TIPO_ABONO     = '''||RS.TIPO_ABONO||'''
                                AND  CODIGO_ABONO   = '||RS.CODIGO_ABONO||'
                                AND  CUOTA          = '||RS.CUOTA ;
        
               PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_ACCION    =>'M',
                                                 UN_TABLA     =>'SF_DETALLE_ABONO',
                                                 UN_CAMPOS    =>MI_CAMPOS,
                                                 UN_CONDICION =>MI_CONDICION);
        
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
                MI_MSGERROR(1).CLAVE := 'FACTURA';
                MI_MSGERROR(1).VALOR := UN_FACTURA;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTESTADOFACTURA
                                            ,UN_REEMPLAZOS  => MI_MSGERROR);
            END;
            -- ACTUALIZAR LOS VALORES DE LA SF_ABONOS
            
            IF RS.INTERES_EXPEM > 0 THEN 
                MI_CAMPOS := 'INTERES_ACT = DEUDA_INTERES - '||RS.INTERES_EXPEM ||' - '|| MI_EXTEM_RE ||',
                                  INTERES_EX  = INTERES_EX + '||RS.INTERES_EXPEM||'';
            ELSE
                IF MI_TIENE_RECAUDO = 0 THEN
                    MI_CAMPOS := 'INTERES_ACT = INTERES_ACT, INTERES_EX  = INTERES_EX';
                ELSE 
                    MI_CAMPOS := 'INTERES_ACT = INTERES_ACT + '||RS.INTERES_PAG_PARCIAL||', INTERES_EX  = INTERES_EX';
                END IF;
            END IF;
            
            BEGIN
                BEGIN
                    
                    MI_CONDICION := 'COMPANIA       = '''||UN_COMPANIA||'''
                                AND  TIPO     = '''||RS.TIPO_ABONO||'''
                                AND  CODIGO   = '||RS.CODIGO_ABONO ;
               PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_ACCION    =>'M',
                                                 UN_TABLA     =>'SF_ABONOS',
                                                 UN_CAMPOS    =>MI_CAMPOS,
                                                 UN_CONDICION =>MI_CONDICION);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
                MI_MSGERROR(1).CLAVE := 'FACTURA';
                MI_MSGERROR(1).VALOR := UN_FACTURA;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTESTADOFACTURA
                                            ,UN_REEMPLAZOS  => MI_MSGERROR);
            END;
        END LOOP;
    END IF;
    -- REVISAR LAS FACTURAS QUE YA NO TIENE SALDO PARA ACTUALIZAR LOS DATOS DEL PAGO 
    FOR RS IN (
            SELECT ANO, TIPOFACT TIPO_FACTURA, NROFACT FACTURA, 
DEUDA_TOTAL DEUDA, PAGOS, SALDO
        FROM (WITH PAGOS AS (
                    SELECT DC.COMPANIA, DC.ANO, DC.TIPOFACT, DC.NROFACT,
                    A.DEUDA_CAPITAL, A.DEUDA_INTERES, A.DEUDA_TOTAL, SUM(DC.TOTAL_CUOTA) PAGOS
                        FROM   SF_DETALLE_CUOTAABONO DC
                        INNER JOIN SF_DETALLE_ABONO DA
                        ON DC.COMPANIA = DA.COMPANIA
                        AND DC.TIPO_ABONO = DA.TIPO_ABONO
                        AND DC.CODIGO_ABONO = DA.CODIGO_ABONO
                        AND DC.CUOTA = DA.CUOTA
                        INNER JOIN SF_ABONOS A
                        ON DC.COMPANIA = A.COMPANIA
                        AND DC.TIPO_ABONO = A.TIPO
                        AND DC.CODIGO_ABONO = A.CODIGO
                        WHERE DC.COMPANIA        = UN_COMPANIA
                        AND DC.TIPO_ABONO        = MI_TIPOABONO
                        AND DC.CODIGO_ABONO      = MI_NROABONO
                        AND DA.ESTADO            = 'R'
                        GROUP BY DC.COMPANIA, DC.ANO, DC.TIPOFACT, DC.NROFACT,
                        A.DEUDA_CAPITAL, A.DEUDA_INTERES, A.DEUDA_TOTAL
                    )
                SELECT PAGOS.ANO, PAGOS.TIPOFACT, PAGOS.NROFACT, A.DEUDA_TOTAL, A.DEUDA_INTERES, PAGOS.PAGOS, (A.DEUDA_TOTAL - NVL(PAGOS.PAGOS, 0)) AS SALDO
                         FROM SF_ABONOS A
                         LEFT JOIN PAGOS
                         ON A.COMPANIA = PAGOS.COMPANIA
                         AND A.TIPO_FACT = PAGOS.TIPOFACT
                         AND A.NRO_FACT = PAGOS.NROFACT
                         WHERE A.COMPANIA = UN_COMPANIA
                         AND A.TIPO = MI_TIPOABONO
                         AND A.CODIGO = MI_NROABONO
                         )
        )
        LOOP
        IF RS.SALDO = 0      THEN
            BEGIN
                    BEGIN
            
                        MI_CAMPOS := 'FECHA_PAGO = '''||UN_FECHAPAGO||''',                                     
                                      BANCO_PAGO = '''||UN_CUENTARECAUDO||''',
                                     MODIFIED_BY = '''||UN_USUARIO||''',
                                   DATE_MODIFIED = SYSDATE ';
            
                        MI_CONDICION := 'COMPANIA           = '''||UN_COMPANIA||'''
                                    AND  ANO                = '||RS.ANO||'
                                    AND  TIPO_FACTURA       = '''||RS.TIPO_FACTURA||'''
                                    AND  NUMERO_FACTURA     = '||RS.FACTURA ;
            
                   PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_ACCION    =>'M',
                                                     UN_TABLA     =>'SF_FACTURA',
                                                     UN_CAMPOS    =>MI_CAMPOS,
                                                     UN_CONDICION =>MI_CONDICION);
            
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_FACTURACION;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION THEN
                    MI_MSGERROR(1).CLAVE := 'FACTURA';
                    MI_MSGERROR(1).VALOR := UN_FACTURA;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                               UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTESTADOFACTURA
                                                ,UN_REEMPLAZOS  => MI_MSGERROR);
            END;
        END IF;
    END LOOP;
    --Ticket#7737169:Se crea proceso para generar el comprobante presupuestal desde el contable creado previamente.
    --CrearcomprobantePptal
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
                            AND D.ANO       = EXTRACT(YEAR FROM UN_FECHAPAGO)
                            AND D.TIPO_CPTE = MI_CPTERECAUDO
                            AND D.COMPROBANTE = MI_NRO_CPTEING
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
                            AND ANO       = EXTRACT(YEAR FROM UN_FECHAPAGO)
                            AND TIPO      = MI_CPTERECAUDO
                            AND NUMERO    = MI_NRO_CPTEING
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
    
    RETURN   'Se registra el pago con el comprobante '|| MI_CPTERECAUDO ||'-' || MI_NRO_CPTEING;
END FC_RECAUDAR_PAGOPARCIAL;

FUNCTION FC_CUOTASACTIVAS_PAGOPARCIAL (
/*
      NAME              : FC_CUOTASACTIVAS_PAGOPARCIAL 
      AUTHORS           : STEFANINI SYSMAN  SAS
      AUTHOR MIGRACION  : 
      DATE CREATED      : 20/12/2021 
      TIME              : 15:15
      SOURCE MODULE     : 
      DESCRIPTION       : Funcion que retorna 0 si la factura consultada no tiene pagos parciales pendientes, de lo contrario retorna 1
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 

    @NAME:   cuotasActPagoParcial
    @METHOD: GET
*/
  UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANO                IN PCK_SUBTIPOS.TI_ENTERO,
  UN_TIPOFRA            IN PCK_SUBTIPOS.TI_TIPO_COBRO,
  UN_FACTURA            IN SF_FACTURA.NUMERO_FACTURA%TYPE
) RETURN VARCHAR
AS  
    MI_PAGOPARCIAL  SF_FACTURA.PAGOS_PARCIALES%TYPE;
    MI_TIPOABONO    SF_FACTURA.TIPO_ABONO%TYPE;
    MI_NROABONO     SF_FACTURA.NRO_ABONO%TYPE;
    MI_NROCUOTAS    SF_DETALLE_ABONO.CUOTA%TYPE;
    MI_TOTALCUOTA   SF_DETALLE_ABONO.TOTAL_CUOTA%TYPE;
BEGIN
    -- CONSULTAR SI LA FACTURA EXISTE
    
    BEGIN 
        SELECT PAGOS_PARCIALES, TIPO_ABONO, NRO_ABONO
        INTO   MI_PAGOPARCIAL, MI_TIPOABONO, MI_NROABONO
        FROM   SF_FACTURA
        WHERE  COMPANIA       = UN_COMPANIA
        AND    ANO            = UN_ANO  
        AND    TIPO_FACTURA   = UN_TIPOFRA
        AND    NUMERO_FACTURA = UN_FACTURA;
        
        EXCEPTION WHEN OTHERS THEN
            RETURN 'La factura no est� registrada en el sistema';
    END;    
    
    --VERIFICAR SI TIENE PROCESO DE PAGOS PARCIALES
    IF MI_PAGOPARCIAL <> 0 THEN 
        -- CONSULTAR SI TIENE PAGOS PARCIALES ACTIVOS 
        BEGIN
            SELECT COUNT(CUOTA) NROCUOTAS , SUM(TOTAL_CUOTA) TOTALCUOTAS
            INTO   MI_NROCUOTAS , MI_TOTALCUOTA
            FROM   SF_DETALLE_ABONO
            WHERE  COMPANIA     = UN_COMPANIA
            AND    TIPO_ABONO   = MI_TIPOABONO
            AND    CODIGO_ABONO = MI_NROABONO
            AND    ESTADO       = 'A';
            
             EXCEPTION WHEN OTHERS THEN
            RETURN 'La factura no est� registrada en el sistema';
        END;
        
        IF MI_NROCUOTAS > 1 THEN
            RETURN 'La factura tiene ' || MI_NROCUOTAS || ' cuotas activas, no es posible realizar el proceso de recaudo';    
        ELSIF MI_NROCUOTAS = 0 THEN
            RETURN 'La factura no tiene cuotas activas, no es posible realizar el proceso de recaudo';    
        ELSE
            RETURN MI_TIPOABONO || '-' || MI_NROABONO || '-' || MI_TOTALCUOTA;
        END IF;
        
    ELSE
        RETURN 'La factura no registra pagos parciales';
    END IF;
    
   
END FC_CUOTASACTIVAS_PAGOPARCIAL;

END PCK_FACT_GENERAL_ABPARCIALES;