CREATE OR REPLACE PACKAGE BODY PCK_FACT_GENERAL_COM1 AS
--1
FUNCTION FC_DISTRIBUIRABONO
/*
      NAME              : FC_DISTRIBUIRABONO MIGRADO DE ACCESS DistribuirAbono
      AUTHORS           FC_MAN_DER_CONN_CONCEP: SYSMAN  SAS
      AUTHOR MIGRACION  : JUAN CAMILO RODRIGUEZ DIAZ
      DATE MIGRADOR     : 23/11/2017
      TIME              : 10:30 AM
      SOURCE MODULE     : SYSMANSF2017.11.01
      DESCRIPTION       :Función que toma la distribución de las cuotas de un acuerdo para con base en estos valores realizar la distribución de la
                         deuda por conceptos - Enero 17 - 18  2011
                         Modificado Feb 11 2011 debido a que al tener en cuenta los auxiliares de los conceptos ya no es posible agrupar la deuda por concepto, se debe
                         realizar la distribución por factura tomando de la de mayor antigüedad a la más reciente
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 

    @NAME:   distribuirAbono
    @METHOD: POST
*/
(
 UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
 UN_TIPOABONO   IN PCK_SUBTIPOS.TI_PARAMETRO,
 UN_NROABONO    IN PCK_SUBTIPOS.TI_DOBLE,
 UN_TIPOFACTURA IN PCK_SUBTIPOS.TI_PARAMETRO,
 UN_FACTURA     IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
 UN_TERCERO     IN PCK_SUBTIPOS.TI_TERCERO,
 UN_SUCURSAL    IN PCK_SUBTIPOS.TI_SUCURSAL,
 UN_TIPOCOBRO   IN PCK_SUBTIPOS.TI_TIPO_COBRO,
 UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO
)RETURN CLOB AS

 MI_CUOTATRABAJO         PCK_SUBTIPOS.TI_ENTERO;
 MI_ANOTRABAJO           PCK_SUBTIPOS.TI_ENTERO;
 MI_CONCEPTOTRABAJO      PCK_SUBTIPOS.TI_PARAMETRO;
 MI_TIPOFACTRABAJO       PCK_SUBTIPOS.TI_PARAMETRO;
 MI_FACTURATRABAJO       PCK_SUBTIPOS.TI_DOBLE;
 MI_SALDOCUOTA           PCK_SUBTIPOS.TI_DOBLE;
 MI_VLRDISTRIBUIR        PCK_SUBTIPOS.TI_DOBLE;
 MI_VLRCONDONADO         PCK_SUBTIPOS.TI_DOBLE;
 MI_VLRDESCUENTO         PCK_SUBTIPOS.TI_DOBLE;
 MI_VLRRETEFUENTE        PCK_SUBTIPOS.TI_DOBLE;
 MI_VLRICA               PCK_SUBTIPOS.TI_DOBLE;
 MI_VLRIVA               PCK_SUBTIPOS.TI_DOBLE;
 MI_VLRBASE              PCK_SUBTIPOS.TI_DOBLE;
 MI_VLRNETO              PCK_SUBTIPOS.TI_DOBLE;
 MI_VLRCOMPRA            PCK_SUBTIPOS.TI_DOBLE;
 MI_VLRUTILIDAD          PCK_SUBTIPOS.TI_DOBLE;
 MI_VLRACOMETIDA         PCK_SUBTIPOS.TI_DOBLE;
 MI_VLRADMONIMP          PCK_SUBTIPOS.TI_DOBLE;
 MI_VLRUTILIDADAIU       PCK_SUBTIPOS.TI_DOBLE;
 MI_VLRIVAUTILIDAD       PCK_SUBTIPOS.TI_DOBLE;
 MI_SALDOCONDONADO       PCK_SUBTIPOS.TI_DOBLE;
 MI_SALDODESCUENTO       PCK_SUBTIPOS.TI_DOBLE;
 MI_SALDORETEFUENTE      PCK_SUBTIPOS.TI_DOBLE;
 MI_SALDOICA             PCK_SUBTIPOS.TI_DOBLE;
 MI_SALDOIVA             PCK_SUBTIPOS.TI_DOBLE;
 MI_SALDOBASE            PCK_SUBTIPOS.TI_DOBLE;
 MI_SALDONETO            PCK_SUBTIPOS.TI_DOBLE;
 MI_SALDOCOMPRA          PCK_SUBTIPOS.TI_DOBLE;
 MI_SALDOUTILIDAD        PCK_SUBTIPOS.TI_DOBLE;
 MI_SALDOACOMETIDA       PCK_SUBTIPOS.TI_DOBLE;
 MI_SALDOADMONIMP        PCK_SUBTIPOS.TI_DOBLE;
 MI_SALDOUTILIDADAIU     PCK_SUBTIPOS.TI_DOBLE;
 MI_SALDOIVAUTILIDAD     PCK_SUBTIPOS.TI_DOBLE;
 MI_TIPOFACTURADET       PCK_SUBTIPOS.TI_PARAMETRO;
 MI_NOFACTURADET         PCK_SUBTIPOS.TI_DOBLE;
 MI_CI_FINANCIACION      PCK_SUBTIPOS.TI_PARAMETRO;-- ' CONCEPTO CONFIGURADO PARA MANEJAR LOS INTERESES DE FINANCIACION
 MI_DIGREDONDEO          PCK_SUBTIPOS.TI_ENTERO;
 MI_VLRIVAFIN            PCK_SUBTIPOS.TI_DOBLE; 
 MI_TIPODECONCEPTO       PCK_SUBTIPOS.TI_PARAMETRO;
 MI_CUENTADEBITOBASE     PCK_SUBTIPOS.TI_PARAMETRO;
 MI_CUENTACREDITOBASE    PCK_SUBTIPOS.TI_PARAMETRO;
 MI_CUENTADEBITOBASE_AV  PCK_SUBTIPOS.TI_PARAMETRO;
 MI_CUENTACREDITOBASE_AV PCK_SUBTIPOS.TI_PARAMETRO;
 MI_CENTRO_COSTO         PCK_SUBTIPOS.TI_PARAMETRO;
 MI_AUXILIAR             PCK_SUBTIPOS.TI_PARAMETRO;
 MI_APLICAIVA            PCK_SUBTIPOS.TI_LOGICO;
 MI_PORCENTAJEIVA        PCK_SUBTIPOS.TI_DOBLE;
 MI_CUOTA                PCK_SUBTIPOS.TI_ENTERO;
 MI_FECHACUOTA           DATE;
 MI_CAPITAL              PCK_SUBTIPOS.TI_DOBLE;
 MI_INTERES              PCK_SUBTIPOS.TI_DOBLE;
 MI_INT_FINANCIACION     PCK_SUBTIPOS.TI_DOBLE;
 MI_TOTAL_CUOTA          PCK_SUBTIPOS.TI_DOBLE;
 MI_ESTADO               PCK_SUBTIPOS.TI_LOGICO;
 MI_ESTADO_DEUDA         PCK_SUBTIPOS.TI_LOGICO;
 MI_ACME                 PCK_SUBTIPOS.TI_ENTERO;
 MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
 MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
 MI_TABLA                PCK_SUBTIPOS.TI_TABLA;
 MI_ERROR                PCK_SUBTIPOS.TI_CLAVEVALOR; 
 MI_RPTA                 PCK_SUBTIPOS.TI_PARAMETRO;
 MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
 MI_CLOB                 CLOB:='';
 MI_STRSQL                PCK_SUBTIPOS.TI_CONSULTA;
BEGIN 

   MI_DIGREDONDEO :=TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   =>UN_COMPANIA,
                                                        UN_NOMBRE     =>'SF REDONDEO VALOR',
                                                        UN_MODULO     =>PCK_DATOS.FC_MODULOFACTURACIONGENERAL(),
                                                        UN_FECHA_PAR =>SYSDATE,
                                                        UN_IND_MAYUS  =>0),2));
   --'CREAR UN CURSOR PARA RECORRER LAS CUOTAS DEL ACUERDO Y LOS VALORES DISPONIBLES A DISTRIBUIR EN CADA UNA
   --'SE DEBE TENER EN CUENTA QUE EL VALOR DE LA CUOTA A DISTRIBUIR ES EL TOTAL DE ESTA MENOS LOS INTERESES DE FINANCIACIÓN
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
   ELSE
         BEGIN
             BEGIN
                 SELECT TIPODECONCEPTO,
                        CUENTADEBITOBASE,
                        CUENTACREDITOBASE ,
                        CUENTADEBITOBASE_AV,
                        CUENTACREDITOBASE_AV,
                        CENTRO_COSTO,
                        AUXILIAR,
                        APLICAIVA,
                        PORCENTAJEIVA 
                        INTO
                        MI_TIPODECONCEPTO,
                        MI_CUENTADEBITOBASE,
                        MI_CUENTACREDITOBASE ,
                        MI_CUENTADEBITOBASE_AV,
                        MI_CUENTACREDITOBASE_AV,
                        MI_CENTRO_COSTO,
                        MI_AUXILIAR,
                        MI_APLICAIVA,
                        MI_PORCENTAJEIVA 
                      FROM SF_CONCEPTOS
                      WHERE COMPANIA = UN_COMPANIA
                      AND ANO        = EXTRACT(YEAR FROM SYSDATE)
                      AND TIPOCOBRO  = UN_TIPOFACTURA
                      AND CODIGO     = MI_CI_FINANCIACION; 

                      IF MI_TIPODECONCEPTO NOT IN('I') THEN
                            BEGIN
                                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                                     MI_ERROR(1).CLAVE := 'MI_CI_FINANCIACION';
                                     MI_ERROR(1).VALOR := MI_CI_FINANCIACION;
                                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                                                UN_ERROR_COD =>PCK_ERRORES.ERR_SYSMANSF_FINANCI_CONCEPTOS,
                                                                UN_REEMPLAZOS =>  MI_ERROR);
                            END;                
                      END IF;

                      IF NVL(MI_CUENTADEBITOBASE, ' ') = ' ' OR NVL(MI_CUENTACREDITOBASE, ' ') = ' ' OR NVL(MI_CUENTADEBITOBASE_AV, ' ') = ' ' OR NVL(MI_CUENTACREDITOBASE_AV, ' ') = ' ' THEN
                              BEGIN
                                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                                     MI_ERROR(1).CLAVE := 'MI_CI_FINANCIACION';
                                     MI_ERROR(1).VALOR := MI_CI_FINANCIACION;
                                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                                UN_ERROR_COD  =>PCK_ERRORES.ERR_SYSMANSF_CUENTA_CONCEPTOS,
                                                                UN_REEMPLAZOS =>MI_ERROR);
                            END;     
                      END IF;

                  EXCEPTION  
                       WHEN NO_DATA_FOUND THEN           
                       --' si no se encontraron datos con las facturas seleccionadas se envia un mensaje al usuario y se finaliza el proceso
                        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                       WHEN TOO_MANY_ROWS THEN                    
                        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                  END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                                 MI_ERROR(1).CLAVE := 'MI_CI_FINANCIACION';
                                 MI_ERROR(1).VALOR := MI_CI_FINANCIACION;                  
                                 PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                            UN_ERROR_COD  =>PCK_ERRORES.ERR_SYSMANSF_CONFIGUR_CONCEPTO,
                                                            UN_REEMPLAZOS =>MI_ERROR);
          END;     
   END IF;

   BEGIN



             MI_ANOTRABAJO := 0;
             MI_CONCEPTOTRABAJO := ' ';
             MI_TIPOFACTRABAJO := ' ';
             MI_FACTURATRABAJO := 0;
             MI_TIPOFACTURADET := ' ';
             MI_NOFACTURADET := 0;
             MI_VLRDISTRIBUIR := 0;
             MI_VLRCONDONADO := 0;
             MI_VLRDESCUENTO := 0;
             MI_VLRRETEFUENTE := 0;
             MI_VLRICA := 0;
             MI_VLRIVA := 0;
             MI_VLRBASE := 0;
             MI_VLRNETO := 0;
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
             MI_ESTADO:=-1;
             MI_ESTADO_DEUDA:=-1;

             <<RECORRERDETALLE>>
            FOR MI_RS_ABONO IN (
                 SELECT CUOTA,
              FECHACUOTA,
              CAPITAL,
              INTERES,
              INT_FINANCIACION,
              TOTAL_CUOTA,
              ESTADO
            FROM TEMP_SF_DETALLE_ABONO
            WHERE COMPANIA   = UN_COMPANIA
            AND TIPO_ABONO   = UN_TIPOABONO
            AND CODIGO_ABONO = UN_NROABONO
            ) LOOP

                MI_ESTADO:=0;
                MI_VLRDISTRIBUIR := MI_RS_ABONO.TOTAL_CUOTA - MI_RS_ABONO.INT_FINANCIACION;
                MI_SALDOCUOTA    := MI_VLRDISTRIBUIR;
             <<RECORRERFACTURA>>
                FOR MI_RS_ABONO_DEUDA IN (
                SELECT SF_DETALLE_FACTURA.ANO,
                  SF_DETALLE_FACTURA.TIPO_FACTURA,
                  SF_DETALLE_FACTURA.FACTURA,
                  SF_DETALLE_FACTURA.CONCEPTO,
                  SUM(SF_DETALLE_FACTURA.VALOR_BASE)        VALOR_BASE,
                  SUM(SF_DETALLE_FACTURA.VALOR_IVA)         VALOR_IVA,
                  SUM(SF_DETALLE_FACTURA.VALOR_RETEFUENTE)  VALOR_RETEFUENTE,
                  SUM(SF_DETALLE_FACTURA.VALOR_DESCUENTO)   VALOR_DESCUENTO,
                  SUM(SF_DETALLE_FACTURA.VALOR_ICA)         VALOR_ICA,
                  SUM(SF_DETALLE_FACTURA.VALOR_NETO)        VALOR_NETO,
                  SUM(SF_DETALLE_FACTURA.VALOR_CONDONADO)   VALOR_CONDONADO,
                  SUM(SF_DETALLE_FACTURA.VALOR_COMPRA)      VALOR_COMPRA,
                  SUM(SF_DETALLE_FACTURA.VALOR_UTILIDAD)    VALOR_UTILIDAD,
                  SF_DETALLE_FACTURA.TERCERO,
                  SF_DETALLE_FACTURA.SUCURSAL,
                  SF_DETALLE_FACTURA.CENTRO_COSTO,
                  SF_DETALLE_FACTURA.AUXILIAR,
                  SF_CONCEPTOS.TIPODECONCEPTO,
                  SUM(SF_DETALLE_FACTURA.ACOMETIDA_SERVICIO) ACOMETIDA_SERVICIO,
                  SUM(SF_DETALLE_FACTURA.ADMON_IMPREVISTOS)  ADMON_IMPREVISTOS,
                  SUM(SF_DETALLE_FACTURA.UTILIDAD_AIU)       UTILIDAD_AIU,
                  SUM(SF_DETALLE_FACTURA.IVA_UTILIDAD)       IVA_UTILIDAD
                FROM SF_CONCEPTOS
                INNER JOIN SF_DETALLE_FACTURA
                ON (SF_CONCEPTOS.COMPANIA           = SF_DETALLE_FACTURA.COMPANIA)
                AND (SF_CONCEPTOS.ANO               = SF_DETALLE_FACTURA.ANO)
                AND (SF_CONCEPTOS.CODIGO            = SF_DETALLE_FACTURA.CONCEPTO)
                AND SF_CONCEPTOS.TIPOCOBRO          = SF_DETALLE_FACTURA.TIPO_FACTURA
                WHERE SF_DETALLE_FACTURA.COMPANIA   = UN_COMPANIA
                AND SF_DETALLE_FACTURA.TIPO_FACTURA = UN_TIPOFACTURA
                AND SF_DETALLE_FACTURA.FACTURA      = UN_FACTURA
                GROUP BY SF_DETALLE_FACTURA.ANO,
                  SF_DETALLE_FACTURA.TIPO_FACTURA,
                  SF_DETALLE_FACTURA.FACTURA,
                  SF_DETALLE_FACTURA.CONCEPTO,
                  SF_DETALLE_FACTURA.TERCERO,
                  SF_DETALLE_FACTURA.SUCURSAL,
                  SF_DETALLE_FACTURA.CENTRO_COSTO,
                  SF_DETALLE_FACTURA.AUXILIAR,
                  SF_CONCEPTOS.TIPODECONCEPTO
                ORDER BY SF_DETALLE_FACTURA.ANO ASC,
                  SF_DETALLE_FACTURA.TIPO_FACTURA,
                  SF_DETALLE_FACTURA.FACTURA,
                  TIPODECONCEPTO ASC,
                  CONCEPTO ASC )
                LOOP
                    MI_ESTADO_DEUDA:=0;
                    --'Reasigna el valor a distribuir si al distribuir el concepto anterior quedo saldo, esto con el fin de que la validación
                    --' de los valores asignados y los valores a distruibuir sean reales.
                    IF MI_SALDOCUOTA > 0 THEN
                       MI_VLRDISTRIBUIR := MI_SALDOCUOTA;
                    END IF;      

                    IF MI_TIPOFACTURADET = ' ' OR MI_TIPOFACTURADET IS  NULL THEN
                        MI_TIPOFACTURADET := MI_RS_ABONO_DEUDA.TIPO_FACTURA;
                        MI_NOFACTURADET   := MI_RS_ABONO_DEUDA.FACTURA;
                    END IF;

                   --' Se evalua si ya habia iniciado el proceso de distribución y en que punto quedo
                   IF MI_ANOTRABAJO NOT IN (0) THEN
                      IF NOT(MI_ANOTRABAJO = MI_RS_ABONO_DEUDA.ANO AND MI_CONCEPTOTRABAJO = MI_RS_ABONO_DEUDA.CONCEPTO AND MI_TIPOFACTRABAJO = MI_RS_ABONO_DEUDA.TIPO_FACTURA AND MI_FACTURATRABAJO = MI_RS_ABONO_DEUDA.FACTURA) THEN
                         CONTINUE;
                      ELSIF MI_SALDONETO <= 0 THEN
                        MI_ANOTRABAJO := 0;
                        CONTINUE;
                      END IF;                       
                   END IF; 
                   --'Validar si el valor del concepto que se evalua es cubierto por el valor de la cuota
                  MI_VLRNETO := CASE WHEN   MI_SALDONETO > 0 THEN  MI_SALDONETO ELSE  MI_RS_ABONO_DEUDA.VALOR_NETO - MI_RS_ABONO_DEUDA.VALOR_CONDONADO END;

                  IF MI_VLRNETO <= MI_SALDOCUOTA THEN

                        IF MI_SALDOBASE >= 0 THEN
                              BEGIN
                              MI_TABLA:='TEMP_SF_DETALLE_CUOTAABONO';

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
                                          'VALOR_COMPRA,'||
                                          'VALOR_UTILIDAD,'||
                                          'ACOMETIDA_SERVICIO,'||
                                          'ADMON_IMPREVISTOS,'||
                                          'UTILIDAD_AIU,'||
                                          'IVA_UTILIDAD,'||
                                          'CREATED_BY,'||
                                          'DATE_CREATED';

                            MI_VALORES:=''''||UN_COMPANIA||''','||
                                        ''''||UN_TIPOABONO||''','||
                                        UN_NROABONO||','||
                                        MI_RS_ABONO_DEUDA.ANO||','||
                                        MI_RS_ABONO.CUOTA||','||   
                                        ''''||MI_RS_ABONO_DEUDA.TIPO_FACTURA||''','||
                                        MI_RS_ABONO_DEUDA.FACTURA||','||
                                        ''''||MI_RS_ABONO_DEUDA.CONCEPTO||''','||
                                        MI_SALDOBASE||','||
                                        MI_SALDOIVA||','||   
                                        MI_SALDORETEFUENTE||','||
                                        MI_SALDODESCUENTO||','||
                                        MI_SALDOICA||','||   
                                        MI_SALDONETO||','||
                                        ''''||MI_RS_ABONO_DEUDA.TERCERO||''','||
                                        ''''||MI_RS_ABONO_DEUDA.SUCURSAL||''','||
                                        ''''||MI_RS_ABONO_DEUDA.CENTRO_COSTO||''','||
                                        ''''||MI_RS_ABONO_DEUDA.AUXILIAR||''','||
                                        MI_SALDOCOMPRA||','||
                                        MI_SALDOUTILIDAD||','||   
                                        MI_SALDOACOMETIDA||','||
                                        MI_SALDOADMONIMP||','||
                                        MI_SALDOUTILIDADAIU||','||
                                        MI_SALDOIVAUTILIDAD||','||
                                        ''''||UN_USUARIO||''','||
                                        'SYSDATE';
                              BEGIN

                                  MI_ACME:=PCK_DATOS.FC_ACME(UN_ACCION  =>'I',
                                                             UN_TABLA   =>MI_TABLA,
                                                             UN_CAMPOS  =>MI_CAMPOS,
                                                             UN_VALORES =>MI_VALORES);
                                   EXCEPTION
                                          WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                             END;

                             EXCEPTION
                                  WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                                    MI_ERROR(1).CLAVE := 'TABLA';
                                    MI_ERROR(1).VALOR := MI_TABLA;
                                    MI_ERROR(2).CLAVE := 'CAMPOS';
                                    MI_ERROR(2).VALOR :=  MI_CAMPOS;
                                   PCK_ERR_MSG.RAISE_WITH_MSG(
                                     UN_EXC_COD    => SQLCODE,
                                     UN_TABLAERROR => MI_TABLA,
                                     UN_ERROR_COD  => PCK_ERRORES.ERR_PR_CALCULARM_INSERTAR,
                                     UN_REEMPLAZOS => MI_ERROR
                                    );

                          END;     
                        ELSE
                          BEGIN
                              MI_TABLA  :='TEMP_SF_DETALLE_CUOTAABONO';

                              MI_CAMPOS :='COMPANIA,'||
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
                                          'VALOR_COMPRA,'||
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

                          MI_VALORES:=''''||UN_COMPANIA||''','||
                                      ''''||UN_TIPOABONO||''','||
                                      UN_NROABONO||','||
                                      MI_RS_ABONO_DEUDA.ANO||','||
                                      MI_RS_ABONO.CUOTA||','||
                                      ''''||MI_RS_ABONO_DEUDA.TIPO_FACTURA||''','||
                                      MI_RS_ABONO_DEUDA.FACTURA||','||
                                      ''''||MI_RS_ABONO_DEUDA.CONCEPTO||''','||
                                      MI_RS_ABONO_DEUDA.VALOR_BASE||','||
                                      MI_RS_ABONO_DEUDA.VALOR_IVA||','||
                                      MI_RS_ABONO_DEUDA.VALOR_RETEFUENTE||','||
                                      MI_RS_ABONO_DEUDA.VALOR_DESCUENTO||','||
                                      MI_RS_ABONO_DEUDA.VALOR_ICA||','||
                                      MI_RS_ABONO_DEUDA.VALOR_NETO||','||
                                      MI_RS_ABONO_DEUDA.VALOR_COMPRA||','||
                                      MI_RS_ABONO_DEUDA.VALOR_UTILIDAD||','||
                                      ''''||MI_RS_ABONO_DEUDA.TERCERO||''','||
                                      ''''||MI_RS_ABONO_DEUDA.SUCURSAL||''','||
                                      ''''||MI_RS_ABONO_DEUDA.CENTRO_COSTO||''','||
                                      ''''||MI_RS_ABONO_DEUDA.AUXILIAR||''','||
                                      MI_RS_ABONO_DEUDA.ACOMETIDA_SERVICIO||','||
                                      MI_RS_ABONO_DEUDA.ADMON_IMPREVISTOS||','||
                                      MI_RS_ABONO_DEUDA.UTILIDAD_AIU||','||
                                      MI_RS_ABONO_DEUDA.IVA_UTILIDAD||','||
                                      ''''||UN_USUARIO||''','||
                                      'SYSDATE';

                              BEGIN

                                  MI_ACME:=PCK_DATOS.FC_ACME(UN_ACCION  =>'I',
                                                             UN_TABLA   =>MI_TABLA,
                                                             UN_CAMPOS  =>MI_CAMPOS,
                                                             UN_VALORES =>MI_VALORES);
                                   EXCEPTION
                                          WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                             END;

                             EXCEPTION
                                  WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                                    MI_ERROR(1).CLAVE := 'TABLA';
                                    MI_ERROR(1).VALOR := MI_TABLA;
                                    MI_ERROR(2).CLAVE := 'CAMPOS';
                                    MI_ERROR(2).VALOR :=  MI_CAMPOS;
                                   PCK_ERR_MSG.RAISE_WITH_MSG(
                                     UN_EXC_COD    => SQLCODE,
                                     UN_TABLAERROR => MI_TABLA,
                                     UN_ERROR_COD  => PCK_ERRORES.ERR_PR_CALCULARM_INSERTAR,
                                     UN_REEMPLAZOS => MI_ERROR
                                    );

                          END;   

                        END IF;
                        IF MI_ACME <= 0 THEN 
                           BEGIN
                                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;

                                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN                                           
                                 PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                            UN_ERROR_COD  =>PCK_ERRORES.ERR_SYSMANSF_NO_CUOTAS_CORRECT);

                            END;  
                        END IF;
                         MI_SALDOCUOTA := MI_SALDOCUOTA - MI_VLRNETO;
                         MI_SALDONETO := MI_SALDONETO - MI_VLRNETO;
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
                  ELSE 


                   --' SE DEBE TENER EN CUENTA QUE ALGUNAS DE LOS VALORES SE RESTAN AL VALOR BASE POR CONSIGUIENTE NO SE DEBEN RESTAR AL SALDO DE LA CUOTA
                   --' SINO SUMARLOS PARA CONSERVAR LA DEUDA CORRECTA

                   MI_VLRCONDONADO   := CASE WHEN MI_SALDOCONDONADO > 0 THEN  MI_SALDOCONDONADO ELSE MI_RS_ABONO_DEUDA.VALOR_CONDONADO END;--' ALMACENA EN UNA VARIABLE EL VALOR CONDONADO DEL CONCEPTO QUE SE ESTE EVALUADO
                   MI_SALDOCUOTA     := MI_SALDOCUOTA + MI_VLRCONDONADO;--' COMO EL VALOR CONDONADO ES UN VALOR QUE SE RESTA A LA BASE ESTE VALOR SE SUMA AL SALDO DE LA CUOTA PARA MANTENER LA ECUACIÓN
                   MI_SALDOCONDONADO := 0;

                   MI_VLRDESCUENTO   := CASE WHEN MI_SALDODESCUENTO >= 0 THEN  MI_SALDODESCUENTO ELSE  MI_RS_ABONO_DEUDA.VALOR_DESCUENTO END ;-- ' ALMACENA EN UNA VARIABLE EL VALOR DESCUENTO DEL CONCEPTO QUE SE ESTE EVALUADO
                   MI_SALDOCUOTA     := MI_SALDOCUOTA + MI_VLRDESCUENTO; --' COMO EL VALOR DESCUENTO ES UN VALOR QUE SE RESTA A LA BASE ESTE VALOR SE SUMA AL SALDO DE LA CUOTA PARA MANTENER LA ECUACIÓN
                   MI_SALDODESCUENTO := 0;

                  MI_VLRRETEFUENTE   := CASE WHEN MI_SALDORETEFUENTE > 0 THEN MI_SALDOICA  ELSE MI_RS_ABONO_DEUDA.VALOR_RETEFUENTE END;
                  IF MI_VLRRETEFUENTE <= MI_SALDOCUOTA THEN
                     MI_SALDOCUOTA      := MI_SALDOCUOTA - MI_VLRRETEFUENTE;
                     MI_SALDORETEFUENTE := 0;
                  ELSE
                     MI_SALDORETEFUENTE := MI_VLRRETEFUENTE - MI_SALDOCUOTA;
                     MI_VLRRETEFUENTE   := MI_SALDOCUOTA;
                     MI_SALDOCUOTA      := 0;
                  END IF;

                  MI_VLRICA        :=CASE WHEN MI_SALDOICA > 0 THEN MI_SALDOICA ELSE MI_RS_ABONO_DEUDA.VALOR_ICA END;
                  IF MI_VLRICA <= MI_SALDOCUOTA THEN
                     MI_SALDOCUOTA := MI_SALDOCUOTA - MI_VLRICA;
                     MI_SALDOICA   := 0;
                  ELSE
                     MI_SALDOICA   := MI_VLRICA - MI_SALDOCUOTA;
                     MI_VLRICA     := MI_SALDOCUOTA;
                     MI_SALDOCUOTA := 0;
                  END IF;

                  MI_VLRIVA     :=CASE WHEN MI_SALDOIVA >= 0 THEN  MI_SALDOIVA ELSE MI_RS_ABONO_DEUDA.VALOR_IVA END;
                  IF MI_SALDOCUOTA > 0 AND MI_VLRIVA <= MI_SALDOCUOTA THEN
                     MI_SALDOCUOTA := MI_SALDOCUOTA - MI_VLRIVA;
                     MI_SALDOIVA   := 0;
                  ELSE
                     MI_SALDOIVA   :=  MI_VLRIVA - MI_SALDOCUOTA;
                     MI_VLRIVA     :=  MI_SALDOCUOTA;
                     MI_SALDOCUOTA := 0;
                  END IF;

                  IF PCK_FACT_GENERAL.FC_MANEJADERCONEXIONCONCEPTO(UN_COMPANIA   =>UN_COMPANIA,
                                                                   UN_ANO        =>TO_NUMBER(EXTRACT(YEAR FROM SYSDATE)),
                                                                   UN_TIPOCOBRO  =>UN_TIPOCOBRO,
                                                                   UN_CONCEPTO   =>MI_RS_ABONO_DEUDA.CONCEPTO) = -1 THEN
                         MI_VLRACOMETIDA :=CASE WHEN MI_SALDOACOMETIDA >= 0 THEN  MI_SALDOACOMETIDA ELSE MI_RS_ABONO_DEUDA.ACOMETIDA_SERVICIO END;

                        IF MI_SALDOCUOTA > 0 AND MI_VLRACOMETIDA <= MI_SALDOCUOTA THEN
                           MI_SALDOCUOTA := MI_SALDOCUOTA - MI_VLRACOMETIDA;
                           MI_SALDOACOMETIDA := 0;
                        ELSE
                           MI_SALDOACOMETIDA := MI_VLRACOMETIDA - MI_SALDOCUOTA;
                           MI_VLRACOMETIDA   := MI_SALDOCUOTA;
                           MI_SALDOCUOTA     := 0;
                        END IF;

                        MI_VLRADMONIMP := CASE WHEN MI_SALDOADMONIMP > 0 THEN  MI_SALDOADMONIMP ELSE  MI_RS_ABONO_DEUDA.ADMON_IMPREVISTOS END;
                        IF MI_SALDOCUOTA > 0 AND MI_VLRADMONIMP <= MI_SALDOCUOTA THEN
                           MI_SALDOCUOTA    := MI_SALDOCUOTA - MI_VLRADMONIMP;
                           MI_SALDOADMONIMP := 0;
                        ELSE
                           MI_SALDOADMONIMP := MI_VLRADMONIMP - MI_SALDOCUOTA;
                           MI_VLRADMONIMP   := MI_SALDOCUOTA;
                           MI_SALDOCUOTA    := 0;
                        END IF;

                        MI_VLRUTILIDADAIU := CASE WHEN MI_SALDOUTILIDADAIU > 0 THEN MI_SALDOUTILIDADAIU ELSE MI_RS_ABONO_DEUDA.UTILIDAD_AIU END;
                        IF MI_SALDOCUOTA > 0 AND MI_VLRUTILIDADAIU <= MI_SALDOCUOTA THEN
                           MI_SALDOCUOTA       := MI_SALDOCUOTA - MI_VLRUTILIDADAIU;
                           MI_SALDOUTILIDADAIU := 0;
                        ELSE
                           MI_SALDOUTILIDADAIU := MI_VLRUTILIDADAIU - MI_SALDOCUOTA;
                           MI_VLRUTILIDADAIU   := MI_SALDOCUOTA;
                           MI_SALDOCUOTA       := 0;
                        END IF;

                        MI_VLRIVAUTILIDAD      := CASE WHEN MI_SALDOIVAUTILIDAD > 0 THEN MI_SALDOIVAUTILIDAD  ELSE MI_RS_ABONO_DEUDA.IVA_UTILIDAD END;
                        IF MI_SALDOCUOTA > 0 AND MI_VLRIVAUTILIDAD <= MI_SALDOCUOTA THEN
                           MI_SALDOCUOTA       := MI_SALDOCUOTA - MI_VLRIVAUTILIDAD;
                           MI_SALDOIVAUTILIDAD := 0;
                        ELSE
                           MI_SALDOIVAUTILIDAD := MI_VLRIVAUTILIDAD - MI_SALDOCUOTA;
                           MI_VLRIVAUTILIDAD   := MI_SALDOCUOTA;
                           MI_SALDOCUOTA       := 0;
                        END IF;

                        MI_VLRCOMPRA     := 0;
                        MI_VLRUTILIDAD   := 0;
                        MI_SALDOCOMPRA   := 0;
                        MI_SALDOUTILIDAD := 0;
                        MI_VLRBASE       := MI_VLRACOMETIDA + MI_VLRADMONIMP + MI_VLRUTILIDADAIU + MI_VLRIVAUTILIDAD;
                        MI_SALDOBASE     := MI_SALDOACOMETIDA + MI_SALDOADMONIMP + MI_SALDOUTILIDADAIU + MI_SALDOIVAUTILIDAD;

                   ELSIF PCK_FACT_GENERAL.FC_MANEJAINVENTARIO(UN_COMPANIA =>UN_COMPANIA,
                                             UN_ANO      =>TO_NUMBER(EXTRACT(YEAR FROM SYSDATE)),
                                             UN_TIPOCOBRO=>UN_TIPOCOBRO) = -1 THEN  

                          MI_VLRCOMPRA := CASE WHEN MI_SALDOCOMPRA >= 0 THEN  MI_SALDOCOMPRA ELSE MI_RS_ABONO_DEUDA.VALOR_COMPRA END;

                          IF MI_SALDOCUOTA > 0 AND MI_VLRCOMPRA <= MI_SALDOCUOTA THEN
                             MI_SALDOCUOTA  := MI_SALDOCUOTA - MI_VLRCOMPRA;
                             MI_SALDOCOMPRA := 0;
                          ELSE
                             MI_SALDOCOMPRA := MI_VLRCOMPRA - MI_SALDOCUOTA;
                             MI_VLRCOMPRA   := MI_SALDOCUOTA;
                             MI_SALDOCUOTA  := 0;
                          END IF;

                         MI_VLRUTILIDAD :=CASE WHEN  MI_SALDOUTILIDAD > 0 THEN MI_SALDOUTILIDAD ELSE MI_RS_ABONO_DEUDA.VALOR_UTILIDAD END;
                         IF MI_SALDOCUOTA > 0 AND MI_VLRUTILIDAD <= MI_SALDOCUOTA THEN
                            MI_SALDOCUOTA    := MI_SALDOCUOTA - MI_VLRUTILIDAD;
                            MI_SALDOUTILIDAD := 0;
                         ELSE
                            MI_SALDOUTILIDAD := MI_VLRUTILIDAD - MI_SALDOCUOTA;
                            MI_VLRUTILIDAD   := MI_SALDOCUOTA;
                            MI_SALDOCUOTA    := 0;
                         END IF;

                          MI_VLRBASE          := MI_VLRCOMPRA + MI_VLRUTILIDAD;
                          MI_SALDOBASE        := MI_SALDOCOMPRA + MI_SALDOUTILIDAD;
                          MI_SALDOACOMETIDA   := CASE WHEN MI_SALDOACOMETIDA = -1 THEN  0 ELSE MI_SALDOACOMETIDA END;
                          MI_SALDOADMONIMP    := CASE WHEN MI_SALDOADMONIMP = -1 THEN 0 ELSE MI_SALDOADMONIMP END;
                          MI_SALDOUTILIDADAIU := CASE WHEN MI_SALDOUTILIDADAIU = -1 THEN 0 ELSE MI_SALDOUTILIDADAIU END;
                          MI_SALDOIVAUTILIDAD := CASE WHEN MI_SALDOIVAUTILIDAD = -1 THEN  0 ELSE MI_SALDOIVAUTILIDAD END;
                          MI_VLRACOMETIDA     := 0;
                          MI_VLRADMONIMP      := 0;
                          MI_VLRUTILIDADAIU   := 0;
                          MI_VLRIVAUTILIDAD   := 0;  

                ELSE
                     MI_VLRBASE     := CASE WHEN MI_SALDOBASE > 0  THEN  MI_SALDOBASE ELSE  MI_RS_ABONO_DEUDA.VALOR_BASE END;
                     IF MI_SALDOCUOTA > 0 AND MI_VLRBASE <= MI_SALDOCUOTA THEN
                        MI_SALDOCUOTA  := MI_SALDOCUOTA - MI_VLRBASE;
                        MI_SALDOBASE   := 0;
                     ELSE
                        MI_SALDOBASE   := MI_VLRBASE - MI_SALDOCUOTA;
                        MI_VLRBASE     := MI_SALDOCUOTA;
                        MI_SALDOCUOTA  := 0;
                     END IF;

                     MI_VLRCOMPRA     := 0;
                     MI_VLRUTILIDAD   := 0;
                     MI_SALDOCOMPRA   := 0;
                     MI_SALDOUTILIDAD := 0;
                    --' VLRBASE = VLRACOMETIDA + VLRADMONIMP + VLRUTILIDADAIU + VLRIVAUTILIDAD
                    --' SALDOBASE = SALDOACOMETIDA + SALDOADMONIMP + SALDOUTILIDADAIU + SALDOIVAUTILIDAD                

                 END IF;

                 MI_SALDONETO := MI_SALDOBASE + MI_SALDOIVA - MI_SALDORETEFUENTE - MI_SALDODESCUENTO + MI_SALDOICA;

                 IF (MI_VLRBASE + MI_VLRIVA - MI_VLRRETEFUENTE - MI_VLRDESCUENTO + MI_VLRICA - MI_VLRCONDONADO) = MI_VLRDISTRIBUIR THEN
                    BEGIN
                            MI_TABLA   :='TEMP_SF_DETALLE_CUOTAABONO';
                            MI_CAMPOS  :='COMPANIA,'||
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
                                         'VALOR_COMPRA,'||
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
                            MI_VALORES:=''''||UN_COMPANIA||''','||
                                        ''''||UN_TIPOABONO||''','||
                                        UN_NROABONO||','||
                                        MI_RS_ABONO_DEUDA.ANO||','||
                                        MI_RS_ABONO.CUOTA||','||
                                        ''''||MI_RS_ABONO_DEUDA.TIPO_FACTURA||''','||
                                        MI_RS_ABONO_DEUDA.FACTURA||','||
                                        ''''||MI_RS_ABONO_DEUDA.CONCEPTO||''','||
                                        MI_VLRBASE||','||
                                        MI_VLRIVA||','||
                                        MI_VLRRETEFUENTE||','||
                                        MI_VLRDESCUENTO ||','||
                                        MI_VLRICA||','||
                                        TO_CHAR(MI_VLRBASE + MI_VLRIVA - MI_VLRRETEFUENTE - MI_VLRDESCUENTO + MI_VLRICA - MI_VLRCONDONADO)||','||
                                        MI_VLRCOMPRA||','||
                                        MI_VLRUTILIDAD||','||
                                        ''''||MI_RS_ABONO_DEUDA.TERCERO||''','||
                                        ''''||MI_RS_ABONO_DEUDA.SUCURSAL||''','||
                                        ''''||MI_RS_ABONO_DEUDA.CENTRO_COSTO||''','||
                                        ''''||MI_RS_ABONO_DEUDA.AUXILIAR||''','||
                                        MI_VLRACOMETIDA||','||
                                        MI_VLRADMONIMP||','||
                                        MI_VLRUTILIDADAIU||','||
                                        MI_VLRIVAUTILIDAD||','||
                                        ''''||UN_USUARIO||''','||
                                        'SYSDATE';


                            BEGIN
                                 MI_STRSQL :=  MI_STRSQL || ' INSERT INTO ' || MI_TABLA || '(' || MI_CAMPOS || ') VALUES (' || MI_VALORES || ') ';        
                                  MI_ACME:=PCK_DATOS.FC_ACME(UN_ACCION  =>'I',
                                                             UN_TABLA   =>MI_TABLA,
                                                             UN_CAMPOS  =>MI_CAMPOS,
                                                             UN_VALORES =>MI_VALORES);
                                   EXCEPTION
                                          WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                             END;

                             EXCEPTION
                                  WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                                    MI_ERROR(1).CLAVE := 'TABLA';
                                    MI_ERROR(1).VALOR := MI_TABLA;
                                    MI_ERROR(2).CLAVE := 'CAMPOS';
                                    MI_ERROR(2).VALOR :=  MI_CAMPOS;
                                   PCK_ERR_MSG.RAISE_WITH_MSG(
                                     UN_EXC_COD    => SQLCODE,
                                     UN_TABLAERROR => MI_TABLA,
                                     UN_ERROR_COD  => PCK_ERRORES.ERR_PR_CALCULARM_INSERTAR,
                                     UN_REEMPLAZOS => MI_ERROR
                                    );

                          END;  

                       IF MI_VLRCONDONADO NOT IN(0) THEN
                            BEGIN
                                MI_TABLA:='TEMP_SF_DETALLE_CUOTAABONO';

                                MI_CAMPOS:='COMPANIA,'||
                                             'TIPO_ABONO,'||
                                             'CODIGO_ABONO,'||
                                             'ANO,'||
                                             'CUOTA,'||
                                             'CONCEPTO,'||
                                             'VALOR_BASE,'||
                                             'VALOR_IVA,'||
                                             'VALOR_RETE,'||
                                             'VALOR_DESCUENTO,'||
                                             'VALOR_ICA,'||
                                             'TOTAL_CUOTA,'||
                                             'VALOR_COMPRA,'||
                                             'VALOR_UTILIDAD,'||
                                             'ACOMETIDA_SERVICIO,'||
                                             'ADMON_IMPREVISTOS,'||
                                             'UTILIDADAIU,'||
                                             'IVA_UTILIDAD,'||
                                             'CREATED_BY,'||
                                             'DATE_CREATED';

                                MI_VALORES:=''''||UN_COMPANIA||''','||
                                            ''''||UN_TIPOABONO||''','||
                                            UN_NROABONO||','||
                                            MI_RS_ABONO_DEUDA.ANO||','||
                                            MI_RS_ABONO.CUOTA||','||
                                            '110,'||
                                            MI_VLRCONDONADO||','||
                                            '0,'||
                                            '0,'||
                                            '0,'||
                                            '0,'||
                                            MI_VLRCONDONADO||','||
                                            '0,'||
                                            '0,'||
                                            '0,'||
                                            '0,'||
                                            '0,'||
                                            '0,'||
                                            ''''||UN_USUARIO||''','||
                                            'SYSDATE';

                                BEGIN

                                      MI_ACME:=PCK_DATOS.FC_ACME(UN_ACCION  =>'I',
                                                                 UN_TABLA   =>MI_TABLA,
                                                                 UN_CAMPOS  =>MI_CAMPOS,
                                                                 UN_VALORES =>MI_VALORES);
                                       EXCEPTION
                                              WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                                 END;

                                 EXCEPTION
                                      WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                                        MI_ERROR(1).CLAVE := 'TABLA';
                                        MI_ERROR(1).VALOR := MI_TABLA;
                                        MI_ERROR(2).CLAVE := 'CAMPOS';
                                        MI_ERROR(2).VALOR :=  MI_CAMPOS;
                                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                         UN_EXC_COD    => SQLCODE,
                                         UN_TABLAERROR => MI_TABLA,
                                         UN_ERROR_COD  => PCK_ERRORES.ERR_PR_CALCULARM_INSERTAR,
                                         UN_REEMPLAZOS => MI_ERROR
                                        );

                          END;  
                       END IF;    
                 ELSE
                    MI_ERROR(1).CLAVE := 'CUOTA';
                    MI_ERROR(1).VALOR := MI_RS_ABONO.CUOTA;                       

                    MI_CLOB:=MI_CLOB||PCK_ERR_MSG.FC_MENSAJE(UN_MENSAJE_COD =>PCK_ERRORES.ERR_SYSMANSF_INCONDISTRI_CUOTA,    
                                                             UN_REEMPLAZOS  =>MI_ERROR)||PCK_DATOS.GL_SEPARADOR_COL;

                 END IF;         

                END IF;

                  IF MI_SALDOCUOTA = 0 THEN
                         MI_ANOTRABAJO       := MI_RS_ABONO_DEUDA.ANO;
                         MI_CONCEPTOTRABAJO  := MI_RS_ABONO_DEUDA.CONCEPTO;
                         MI_TIPOFACTRABAJO   := MI_RS_ABONO_DEUDA.TIPO_FACTURA;
                         MI_FACTURATRABAJO   := MI_RS_ABONO_DEUDA.FACTURA;
                       ELSE
                         MI_ANOTRABAJO      := 0;
                         MI_CONCEPTOTRABAJO := ' ';
                         MI_TIPOFACTRABAJO  := ' ';
                         MI_FACTURATRABAJO  := 0;
                         MI_SALDOCONDONADO  := -1;
                         MI_SALDODESCUENTO  := -1;
                         MI_SALDORETEFUENTE := -1;
                         MI_SALDOICA        := -1;
                         MI_SALDOIVA        := -1;
                         MI_SALDOBASE       := -1;
                         MI_SALDONETO       := -1;
                         MI_SALDOCOMPRA     := -1;
                         MI_SALDOUTILIDAD   := -1;
                         MI_SALDOACOMETIDA  := -1;
                         MI_SALDOADMONIMP   := -1;
                         MI_SALDOUTILIDADAIU:= -1;
                         MI_SALDOIVAUTILIDAD:= -1;
                       END IF;
                END LOOP RECORRERFACTURA;
            IF MI_ESTADO_DEUDA = -1 THEN
              BEGIN
                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                                   PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                                              UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_INCONVENIEN_DEUDA);

              END;
            END IF;

            IF MI_RS_ABONO.INT_FINANCIACION NOT IN(0) THEN

               MI_VLRIVAFIN := CASE WHEN MI_APLICAIVA = 0 THEN  0 ELSE ROUND(MI_RS_ABONO.INT_FINANCIACION * MI_PORCENTAJEIVA, MI_DIGREDONDEO) END;
               --' SE DEBE PARAMETRIZAR EN QUE CONCEPTO SE MANEJARA LOS INTERESES DE FINANCIACION Y DE RECARGO PARA ASI TOMAR LAS CUENTAS PARA LOS AUXILIARES


                       BEGIN
                               MI_TABLA   :='TEMP_SF_DETALLE_CUOTAABONO';

                                MI_CAMPOS  :='COMPANIA,'||
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
                                             'VALOR_COMPRA,'||
                                             'VALOR_UTILIDAD,'||
                                             'ACOMETIDA_SERVICIO,'||
                                             'ADMON_IMPREVISTOS,'||
                                             'UTILIDAD_AIU,'||
                                             'IVA_UTILIDAD,'||
                                             'CREATED_BY,'||
                                             'DATE_CREATED';        

                                MI_VALORES:=''''||UN_COMPANIA||''','||
                                            ''''||UN_TIPOABONO||''','||
                                            UN_NROABONO||','||
                                            TO_NUMBER(EXTRACT(YEAR FROM SYSDATE))||','||
                                            MI_RS_ABONO.CUOTA||','||
                                            ''''||MI_TIPOFACTURADET||''','||
                                            MI_NOFACTURADET||','||
                                            ''''||MI_CI_FINANCIACION||''','||
                                            MI_RS_ABONO.INT_FINANCIACION||','||
                                            MI_VLRIVAFIN||','||
                                            '0,'||
                                            '0,'||
                                            '0,'||
                                            TO_CHAR(MI_RS_ABONO.INT_FINANCIACION + MI_VLRIVAFIN)||','||
                                            ''''||UN_TERCERO||''','||
                                            ''''||UN_SUCURSAL||''','||
                                            'NVL('''||MI_CENTRO_COSTO||''',''9999999999''),'||   
                                            'NVL('''||MI_AUXILIAR||''',''9999999999999999''),'||                               
                                            MI_RS_ABONO.INT_FINANCIACION||','||
                                            '0,'||
                                            '0,'||
                                            '0,'||
                                            '0,'||
                                            '0,'||
                                            ''''||UN_USUARIO||''','||
                                            'SYSDATE';




                                BEGIN

                                      MI_ACME:=PCK_DATOS.FC_ACME(UN_ACCION  =>'I',
                                                                 UN_TABLA   =>MI_TABLA,
                                                                 UN_CAMPOS  =>MI_CAMPOS,
                                                                 UN_VALORES =>MI_VALORES);
                                       EXCEPTION
                                              WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                                 END;

                                 EXCEPTION
                                      WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                                        MI_ERROR(1).CLAVE := 'TABLA';
                                        MI_ERROR(1).VALOR := MI_TABLA;
                                        MI_ERROR(2).CLAVE := 'CAMPOS';
                                        MI_ERROR(2).VALOR :=  MI_CAMPOS;
                                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                         UN_EXC_COD    => SQLCODE,
                                         UN_TABLAERROR => MI_TABLA,
                                         UN_ERROR_COD  => PCK_ERRORES.ERR_PR_CALCULARM_INSERTAR,
                                         UN_REEMPLAZOS => MI_ERROR
                                        );

                          END;  


               IF MI_ACME > 0 AND MI_VLRIVAFIN > 0 THEN            



                       BEGIN
                                MI_TABLA   :='TEMP_SF_DETALLE_ABONO';

                                MI_CAMPOS  :='INT_FINANCIACION ='||TO_CHAR(MI_INT_FINANCIACION + MI_VLRIVAFIN)||','|| 
                                             'TOTAL_CUOTA      ='||TO_CHAR(MI_TOTAL_CUOTA + MI_VLRIVAFIN)||','||
                                             'MODIFIED_BY      ='''||UN_USUARIO||''','||
                                             'DATE_MODIFIED    =SYSDATE';
                                MI_CONDICION:='COMPANIA           ='''||UN_COMPANIA||''''||  
                                              ' AND  TIPO_ABONO   = '''||UN_TIPOABONO||''''||  
                                              ' AND  CODIGO_ABONO = '||UN_NROABONO||    
                                              ' AND  CUOTA        = '||MI_RS_ABONO.CUOTA;  


                                BEGIN

                                      MI_ACME:=PCK_DATOS.FC_ACME(UN_ACCION    =>'M',
                                                                 UN_TABLA     =>MI_TABLA,
                                                                 UN_CAMPOS    =>MI_CAMPOS,
                                                                 UN_CONDICION =>MI_CONDICION);
                                       EXCEPTION
                                              WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                                 END;

                                 EXCEPTION
                                      WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                                        MI_ERROR(1).CLAVE := 'TABLA';
                                        MI_ERROR(1).VALOR := MI_TABLA;
                                        MI_ERROR(2).CLAVE := 'CAMPOS';
                                        MI_ERROR(2).VALOR :=  MI_CAMPOS;
                                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                         UN_EXC_COD    => SQLCODE,
                                         UN_TABLAERROR => MI_TABLA,
                                         UN_ERROR_COD  => PCK_ERRORES.ERR_PR_CALCULARM_INSERTAR,
                                         UN_REEMPLAZOS => MI_ERROR
                                        );

                          END;                

               END IF;
            END IF;
            MI_TIPOFACTURADET :=' ';
            MI_NOFACTURADET   := 0;

            END LOOP RECORRERDETALLE;


       IF MI_ESTADO = -1 THEN
           BEGIN
              RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                                 MI_ERROR(1).CLAVE := 'TIPOABONO';
                                 MI_ERROR(1).VALOR := UN_TIPOABONO;               
                                 PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                                            UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_NOINFO_ACUERDO);

           END;
       END IF;
   END;        
RETURN MI_CLOB;
END FC_DISTRIBUIRABONO;

--2
FUNCTION FC_MAN_DER_CONN_CONCEP 
 /*
    NAME              : FC_MAN_DER_CONN_CONCEP   ACCES => ManejaDerConexionConcepto
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : JONATHAN ENRIQUE GUERRERO TORRES
    DATE MIGRADOR     : 28/11/2017
    TIME              : 12:45 PM
    DESCRIPTION       : FUNCION QUE SE ENCARGA DE EVALUAR SI EL CONCEPTO TIENE DERECHOS DE CONEXION O NO 
    PARAMETROS        : UN_COMPANIA  => COMPANIA DE INGRESO A LA COMPANIA 
                        UN_ANIO      => ANO DE FACTURACION 
                        UN_TIPOCOBRO => TIPO DE COBRO A FACTURAR
                        UN_CONCEPTO  => CONCEPTO A EVAUALR SI TIENE CONEXION

  */

(
 UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
 UN_ANIO          IN PCK_SUBTIPOS.TI_ANIO,
 UN_TIPOCOBRO     IN VARCHAR2,
 UN_CONCEPTO      IN SF_CONCEPTOS.CODIGO%TYPE 
   )

RETURN PCK_SUBTIPOS.TI_LOGICO AS 
MI_IND_DERECHOSCONEXION   PCK_SUBTIPOS.TI_LOGICO;
MI_FORMULAR_ACOMETIDA         SF_CONCEPTOS.FORMULAR_ACOMETIDA%TYPE;
MI_FORMULAR_ADMONIMP          SF_CONCEPTOS.FORMULAR_ADMONIMP%TYPE ;
MI_FORMULAR_UTILIDADAIU       SF_CONCEPTOS.FORMULAR_UTILIDADAIU%TYPE;
MI_FORMULAR_IVAUTILIDAD       SF_CONCEPTOS.FORMULAR_IVAUTILIDAD%TYPE;
BEGIN
  BEGIN
      SELECT IND_DERECHOSCONEXION
        INTO MI_IND_DERECHOSCONEXION
        FROM SF_TIPO_COBRO
       WHERE COMPANIA = UN_COMPANIA
         AND ANO        = UN_ANIO
         AND CODIGO     = UN_TIPOCOBRO;
  EXCEPTION WHEN NO_DATA_FOUND THEN 
    MI_IND_DERECHOSCONEXION := 0;
  END;

    IF MI_IND_DERECHOSCONEXION NOT IN (0) THEN 

        SELECT FORMULAR_ACOMETIDA,
               FORMULAR_ADMONIMP,
               FORMULAR_UTILIDADAIU,
               FORMULAR_IVAUTILIDAD
          INTO MI_FORMULAR_ACOMETIDA,
               MI_FORMULAR_ADMONIMP  ,
               MI_FORMULAR_UTILIDADAIU ,
               MI_FORMULAR_IVAUTILIDAD 
          FROM SF_CONCEPTOS
         WHERE COMPANIA = UN_COMPANIA
           AND ANO        = UN_ANIO
           AND TIPOCOBRO  = UN_TIPOCOBRO
           AND CODIGO     = UN_CONCEPTO;


           IF MI_FORMULAR_ACOMETIDA NOT IN (0) AND MI_FORMULAR_ADMONIMP     NOT IN (0) 
                                               AND MI_FORMULAR_UTILIDADAIU  NOT IN (0) 
                                               AND  MI_FORMULAR_IVAUTILIDAD NOT IN (0) THEN 
              RETURN -1;
           ELSE 
              RETURN 0;
           END IF;                                    
    ELSE
        RETURN 0;    
    END IF;

  RETURN NULL;
END FC_MAN_DER_CONN_CONCEP;

--4
PROCEDURE PR_REGISTRARPROCESO
(
  UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CLASE            IN SF_LOG_PROCESOS.CLASE%TYPE,
  UN_TIPOCOBRO        IN SF_LOG_PROCESOS.TIPO_PROCESO%TYPE,
  UN_COBRO            IN SF_LOG_PROCESOS.CODIGO_PROCESO%TYPE,
  UN_DESCRIPCION      IN SF_LOG_PROCESOS.DESCRIPCION%TYPE, 
  UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
)
AS 
  MI_CONSECUTIVO        SF_LOG_PROCESOS.ID%TYPE;
  MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
  MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
BEGIN

  MI_CONSECUTIVO:= PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'SF_LOG_PROCESOS',
                                                    UN_CRITERIO => 'COMPANIA = '''||UN_COMPANIA||'''',
                                                    UN_CAMPO    => 'ID' );
  MI_CAMPOS  :=  'ID, 
                  COMPANIA, 
                  CLASE, 
                  TIPO_PROCESO, 
                  CODIGO_PROCESO, 
                  FECHA_PROCESO, 
                  DESCRIPCION,
                  DATE_CREATED,
                  CREATED_BY ';
  MI_VALORES :=  ''||MI_CONSECUTIVO||', 
                  '''||UN_COMPANIA||''',
                  '''||UN_CLASE||''',
                  '''||UN_TIPOCOBRO||''',
                  '||UN_COBRO||',
                  SYSDATE,
                  '''||UN_DESCRIPCION||''',
                  SYSDATE,
                  '''||UN_USUARIO||'''';
  BEGIN
    BEGIN
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'SF_LOG_PROCESOS'
                                                ,UN_ACCION  => 'I'
                                                ,UN_CAMPOS  => MI_CAMPOS
                                                ,UN_VALORES => MI_VALORES);

     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
    END; 
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSLOGPROCESOS
      );
  END;  
END PR_REGISTRARPROCESO;
--3
FUNCTION FC_CARGA_CONCEP_FORMULA 
/*
    NAME              : FC_MAN_DER_CONN_CONCEP   ACCES => ManejaDerConexionConcepto
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : JONATHAN ENRIQUE GUERRERO TORRES
    DATE MIGRADOR     : 28/11/2017
    TIME              : 12:45 PM
    DESCRIPTION       : FUNCION ENCARGADA DE LA LOGICA INTERNA DE LA FUNCION FC_CARGAR_CONCEPTOS
    PARAMETROS        : 

  */
(
  UN_FORMULA         VARCHAR2,
  UN_CUENTA_DEB     VARCHAR2,
  UN_CUENTADOS_CRED VARCHAR2,
  UN_DIG_REDONDEO   VARCHAR2
)
RETURN PCK_SUBTIPOS.TI_ENTERO_LARGO AS 

MI_RTA          NUMBER;
MI_RTA_AUX      NUMBER;

BEGIN
  IF UN_FORMULA <> '0' THEN 
      IF UN_CUENTA_DEB IS NULL OR UN_CUENTA_DEB = ' ' OR UN_CUENTADOS_CRED IS NULL OR UN_CUENTADOS_CRED = ' ' THEN 
          MI_RTA := 0;
      ELSE 
          MI_RTA :=  PCK_SYSMAN_UTL.EVAL(UN_FORMULA);
      END IF;
  ELSE 
      MI_RTA := 0;
  END IF;
  MI_RTA_AUX := MI_RTA;

  MI_RTA := ABS(MI_RTA);
  IF UN_DIG_REDONDEO <> 2 THEN 
      MI_RTA := TRUNC(MI_RTA/UN_DIG_REDONDEO+0.501)*UN_DIG_REDONDEO;
  END IF;

  IF MI_RTA_AUX < 0 THEN 
    MI_RTA := MI_RTA * -1;
  END IF;


  RETURN MI_RTA;

END FC_CARGA_CONCEP_FORMULA;
--4
FUNCTION FC_CARGA_CONCEP_INDICA 
/*
    NAME              : FC_MAN_DER_CONN_CONCEP   ACCES => ManejaDerConexionConcepto
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : JONATHAN ENRIQUE GUERRERO TORRES
    DATE MIGRADOR     : 28/11/2017
    TIME              : 12:45 PM
    DESCRIPTION       : FUNCION ENCARGADA DE LA LOGICA INTERNA DE LA FUNCION FC_CARGAR_CONCEPTOS
    PARAMETROS        : 

   @NAME:  cargarValorConceptoIndica
  */
(
  UN_INDICADOR     IN PCK_SUBTIPOS.TI_LOGICO,
  UN_VALOR         IN PCK_SUBTIPOS.TI_DOBLE,
  UN_DIGITO        IN PCK_SUBTIPOS.TI_ENTERO
)
RETURN PCK_SUBTIPOS.TI_DOBLE AS 

MI_RTA          NUMBER;

BEGIN
  IF UN_INDICADOR NOT IN (0) THEN 
    MI_RTA := ABS(UN_VALOR);
    IF UN_DIGITO <> 2 THEN 
        MI_RTA := TRUNC(MI_RTA / UN_DIGITO+ 0.501) *UN_DIGITO;
    END IF;
  ELSE 
      MI_RTA := 0;
  END IF;
  IF UN_VALOR < 0 THEN 
      MI_RTA := MI_RTA *-1;
  END IF;

  RETURN MI_RTA;

END FC_CARGA_CONCEP_INDICA;


--5
FUNCTION FC_CARGA_CONCEP_VALOR 
/*
    NAME              : FC_MAN_DER_CONN_CONCEP   ACCES => ManejaDerConexionConcepto
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : JONATHAN ENRIQUE GUERRERO TORRES
    DATE MIGRADOR     : 28/11/2017
    TIME              : 12:45 PM
    DESCRIPTION       : FUNCION ENCARGADA DE LA LOGICA INTERNA DE LA FUNCION FC_CARGAR_CONCEPTOS
    PARAMETROS        : 

  */
(

  UN_VALOR         IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_DIGITO        IN PCK_SUBTIPOS.TI_ENTERO
)
RETURN PCK_SUBTIPOS.TI_ENTERO_LARGO AS 

MI_RTA          NUMBER;

BEGIN

	MI_RTA := ABS(UN_VALOR);
	IF UN_DIGITO <> 2 THEN 
 		MI_RTA := TRUNC(MI_RTA / UN_DIGITO + 0.51) * UN_DIGITO;
	END IF;
  	IF UN_VALOR < 0 THEN 
  			MI_RTA := MI_RTA *-1;
  	END IF;


  RETURN MI_RTA;

END FC_CARGA_CONCEP_VALOR;

--6
FUNCTION FC_CARGA_CONCEP_VALOR_CANT 
/*
    NAME              : FC_MAN_DER_CONN_CONCEP   ACCES => ManejaDerConexionConcepto
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : JONATHAN ENRIQUE GUERRERO TORRES
    DATE MIGRADOR     : 28/11/2017
    TIME              : 12:45 PM
    DESCRIPTION       : FUNCION ENCARGADA DE LA LOGICA INTERNA DE LA FUNCION FC_CARGAR_CONCEPTOS
    PARAMETROS        : 

    @NAME:  cargarValorConceptoCant
  */
(

  UN_VALOR         IN PCK_SUBTIPOS.TI_DOBLE,
  UN_CANTIDAD      IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_DIGITO        IN PCK_SUBTIPOS.TI_ENTERO
)
RETURN PCK_SUBTIPOS.TI_DOBLE AS 

MI_RTA          NUMBER;

BEGIN

	MI_RTA := UN_VALOR * UN_CANTIDAD;
	IF UN_DIGITO <> 2 THEN 
 		MI_RTA := TRUNC(MI_RTA  / UN_DIGITO + 0.501)* UN_DIGITO;
	END IF;
  	IF UN_VALOR < 0 THEN 

        MI_RTA := MI_RTA *-1;

  	END IF;


  RETURN MI_RTA;

END FC_CARGA_CONCEP_VALOR_CANT;

---7

FUNCTION FC_VALIDACIONPARAMETRO
/*
    NAME              : FC_VALIDACIONPARAMETRO   
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : ANA YESSICA SANA ROJAS
    DATE MIGRADOR     : 30/11/2017
    TIME              : 11/:00 AM
    DESCRIPTION       : FUNCION QUE DEVUELVE PARAMETRO EN CASO QUE ESTE NO ESTE NULL
    PARAMETROS        : UN_COMPANIA   => COMPANIA DE SESION
                        UN_PARAMETRO  => PARÁMETRO A CONSULTAR, VALIDAR Y EN CASO DE NO ESTAR NULL RETORNAR.

  */
(
    UN_COMPANIA   IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_PARAMETRO  IN  PARAMETRO.VALOR%TYPE
)
RETURN VARCHAR2 AS 
    MI_PARAMETRO  PARAMETRO.VALOR%TYPE;
    MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_TABLA      PCK_SUBTIPOS.TI_TABLA;
BEGIN
    MI_PARAMETRO    :=  PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                             ,UN_MODULO    => PCK_DATOS.MODULOFACTURACIONGENERAL
                                             ,UN_FECHA_PAR => SYSDATE
                                             ,UN_NOMBRE    => UN_PARAMETRO);
    BEGIN
        MI_TABLA := 'PARAMETRO';
        IF MI_PARAMETRO IS NULL THEN
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                MI_REEMPLAZOS(0).CLAVE := 'PARAMETRO';
                MI_REEMPLAZOS(0).VALOR :=  UN_PARAMETRO;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE
                                  ,UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_PARAMETRONOEXISTE
                                  ,UN_TABLAERROR => MI_TABLA
                                  ,UN_REEMPLAZOS => MI_REEMPLAZOS);
            END;
        END IF;
    END;    
  RETURN MI_PARAMETRO;
END FC_VALIDACIONPARAMETRO;
--8

PROCEDURE PR_CARGAR_CONCEPTOS
/*
    NAME              : FC_MAN_DER_CONN_CONCEP   ACCES => ManejaDerConexionConcepto
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : JONATHAN ENRIQUE GUERRERO TORRES
    DATE MIGRADOR     : 30/11/2017
    TIME              :  02:11 PM
    DESCRIPTION       : FUNCION ENCARGADA DE LA LOGICA INTERNA DE LA FUNCION FC_CARGAR_CONCEPTOS
    PARAMETROS        : 
   @NAME cargarConceptos
  */
(
  UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO               IN PCK_SUBTIPOS.TI_ANIO,
  UN_TIPOCOBRO          IN VARCHAR2,
  UN_CODIGO_COBRO       IN SF_CONCEPTOS.CODIGO%TYPE,
  UN_GRUPO_CONCEPTOS    IN PCK_SUBTIPOS.TI_ENTERO,
  UN_FECHA_SOLICITUD    IN DATE,
  UN_CANTIDAD_GRUPO     IN PCK_SUBTIPOS.TI_ENTERO,
  UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO
 ) AS 

    MI_CANTIDAD_PORDEFECTO      PCK_SUBTIPOS.TI_ENTERO;
    MI_PERMITEMODIFICARCANTIDAD PCK_SUBTIPOS.TI_LOGICO;
    MI_CANTIDAD                 PCK_SUBTIPOS.TI_ENTERO;
    MI_BASE_FIJA                PCK_SUBTIPOS.TI_PARAMETRO;
    MI_PARAMETRO                PCK_SUBTIPOS.TI_PARAMETRO; 
    MI_VLR_TARIFA               PCK_SUBTIPOS.TI_DOBLE;
    MI_VLR_ESTRATO              PCK_SUBTIPOS.TI_DOBLE;
    MI_BASE_FIJO                PCK_SUBTIPOS.TI_DOBLE;
    MI_VALOR_UNITARIO           PCK_SUBTIPOS.TI_DOBLE;
    MI_VALOR_UNIDAD             PCK_SUBTIPOS.TI_DOBLE;
    MI_EXISTENCIA               PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_DIG_REDONDEO             PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_RED_IVA                  PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_RED_RETE                 PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_RED_ICA                  PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_RED_DESC                 PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_DIG_TOTAL                PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_VALOR_COMPRA             PCK_SUBTIPOS.TI_DOBLE;
    MI_VALOR_BASE               PCK_SUBTIPOS.TI_DOBLE;
    MI_VALOR_TEMP               PCK_SUBTIPOS.TI_DOBLE;
    MI_VALOR_DESCUENTO          PCK_SUBTIPOS.TI_DOBLE;
    MI_VALOR_IVA                PCK_SUBTIPOS.TI_DOBLE;
    MI_VALOR_RETEFUENTE         PCK_SUBTIPOS.TI_DOBLE;
    MI_VALOR_ICA                PCK_SUBTIPOS.TI_DOBLE;
    MI_VALOR_NETO               PCK_SUBTIPOS.TI_DOBLE;
    MI_VALOR_UTILIDAD           PCK_SUBTIPOS.TI_DOBLE;
    MI_ACOMETIDA_SERVICIO       PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_FORMULAR_ACOMETIDA       PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_AIU                      PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_FORMULAADMONIMP          PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_ADMON_IMPREVISTOS        PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_UTILIDAD_AIU             PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_IVA_UTILIDAD             PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_SUMAAIU                  PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_DIFAIU                   PCK_SUBTIPOS.TI_ENTERO_LARGO;    
    MI_CENTRO_COSTO             SF_DETALLE_COBRO.CENTRO_COSTO%TYPE;
    MI_AUXILIAR                 SF_DETALLE_COBRO.AUXILIAR%TYPE;        
    MI_TABLA                    PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS                   PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                  PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION                PCK_SUBTIPOS.TI_CONDICION;
    MI_MSGERROR                 PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_VLRBASEFIJO              NUMBER(20,2);
    MI_FORMULA                  SF_CONCEPTOS.FORMULA%TYPE;
    MI_TERCERO                  SF_DETALLE_COBRO.TERCERO%TYPE;
    MI_SUCURSAL                 SF_DETALLE_COBRO.SUCURSAL%TYPE;

BEGIN
    MI_VLR_TARIFA   := 1;
    MI_VLR_ESTRATO  := 1;

   MI_BASE_FIJA :=  PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                          UN_NOMBRE    => 'SF MANEJAR BASE FIJA',
                                          UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                          UN_FECHA_PAR => SYSDATE,
                                          UN_IND_MAYUS => 0);

   FOR RS IN (SELECT SF_CONCEPTOS.PERMITEMODIFICARCANTIDAD,
                     SF_CONCEPTOS.CANTIDAD_PORDEFECTO,
                     SF_CONCEPTOS.APLICAFORMULA,
                     SF_CONCEPTOS.FORMULA,
                     SF_CONCEPTOS.VALOR_BASE,
                     SF_CONCEPTOS.AFECTAINVENTARIO,
                     SF_CONCEPTOS.CODIGO,
                     SF_CONCEPTOS.PORCETAJE_UTILIDAD,
                     SF_CONCEPTOS.FACTOR_RED_BASE,
                     SF_CONCEPTOS.FACTOR_RED_IVA,
                     SF_CONCEPTOS.FACTOR_RED_BASETOTAL,
                     SF_CONCEPTOS.FACTOR_RED_ICA,
                     SF_CONCEPTOS.FACTOR_RED_DESCUENTO,
                     SF_CONCEPTOS.FACTOR_RED_RETE,
                     SF_CONCEPTOS.VALOR_COMPRA,
                     SF_CONCEPTOS.PERMITEMODIFICARVALOR,
                     SF_CONCEPTOS.APLICADESCUENTO,
                     SF_CONCEPTOS.APLICAIVA,
                     SF_CONCEPTOS.PORCENTAJEDESCUENTO,
                     SF_CONCEPTOS.PORCENTAJEIVA,
                     SF_CONCEPTOS.PORCENTAJERETEFUENTE,
                     SF_CONCEPTOS.PORCENTAJEICA,
                     SF_CONCEPTOS.CENTRO_COSTO,
                     SF_CONCEPTOS.AUXILIAR,
                     SF_CONCEPTOS.REFERENCIA,                  
                     SF_CONCEPTOS.FORMULAR_ACOMETIDA,
                     SF_CONCEPTOS.CTADEBACOMETIDA,
                     SF_CONCEPTOS.CTACREDACOMETIDA,
                     SF_CONCEPTOS.FORMULAR_AIU,
                     SF_CONCEPTOS.CTADEBAIU,
                     SF_CONCEPTOS.CTACREDAIU,
                     SF_CONCEPTOS.FORMULAR_ADMONIMP,
                     SF_CONCEPTOS.CTADEBADMONIMP,
                     SF_CONCEPTOS.CTACREDADMONIMP,
                     SF_CONCEPTOS.FORMULAR_UTILIDADAIU,
                     SF_CONCEPTOS.CTADEBUTILIDADAIU,
                     SF_CONCEPTOS.CTACREDUTILIDADAIU,
                     SF_CONCEPTOS.FORMULAR_IVAUTILIDAD,
                     SF_CONCEPTOS.CTADEBIVAUTILIDAD,
                     SF_CONCEPTOS.CTACREDIVAUTILIDAD,
                     SF_CONCEPTOS.APLICAICA,
                     SF_CONCEPTOS.APLICARETEFUENTE,
                     SF_CONCEPTOS.FUENTE_RECURSO,
                     SF_CONCEPTOS.CUENTA_RECAUDO
                FROM SF_DETALLE_GRUPOS_CONCEPTOS
               INNER JOIN SF_GRUPOS_CONCEPTOS
                  ON SF_DETALLE_GRUPOS_CONCEPTOS.COMPANIA  = SF_GRUPOS_CONCEPTOS.COMPANIA
                 AND SF_DETALLE_GRUPOS_CONCEPTOS.TIPOCOBRO = SF_GRUPOS_CONCEPTOS.TIPOCOBRO
                 AND SF_DETALLE_GRUPOS_CONCEPTOS.GRUPO     = SF_GRUPOS_CONCEPTOS.CODIGO
               INNER JOIN SF_CONCEPTOS
                  ON SF_DETALLE_GRUPOS_CONCEPTOS.COMPANIA    = SF_CONCEPTOS.COMPANIA
                 AND SF_DETALLE_GRUPOS_CONCEPTOS.TIPOCOBRO  = SF_CONCEPTOS.TIPOCOBRO
                 AND SF_DETALLE_GRUPOS_CONCEPTOS.CONCEPTO   = SF_CONCEPTOS.CODIGO
               WHERE SF_DETALLE_GRUPOS_CONCEPTOS.COMPANIA   = UN_COMPANIA
                 AND SF_DETALLE_GRUPOS_CONCEPTOS.TIPOCOBRO  = UN_TIPOCOBRO
                 AND SF_DETALLE_GRUPOS_CONCEPTOS.GRUPO      = UN_GRUPO_CONCEPTOS
                 AND SF_CONCEPTOS.ANO                       = EXTRACT (YEAR FROM UN_FECHA_SOLICITUD))

   LOOP 

    MI_DIG_REDONDEO := RS.FACTOR_RED_BASE; 
    MI_RED_IVA      := RS.FACTOR_RED_IVA;
    MI_RED_RETE     := RS.FACTOR_RED_RETE;     
    MI_RED_ICA      := RS.FACTOR_RED_ICA;
    MI_RED_DESC     := RS.FACTOR_RED_DESCUENTO;
    MI_DIG_TOTAL    := RS.FACTOR_RED_BASETOTAL;
    MI_VALOR_UNIDAD := 0;
    MI_VALOR_UNITARIO := 0;
       IF RS.PERMITEMODIFICARCANTIDAD NOT IN (0) THEN 
          MI_CANTIDAD := UN_CANTIDAD_GRUPO;
       ELSE 
          MI_CANTIDAD := RS.CANTIDAD_PORDEFECTO;
      END IF;


    SELECT BASE_FIJA_ANT,TERCERO,SUCURSAL
    INTO MI_VLRBASEFIJO,MI_TERCERO,MI_SUCURSAL
    FROM SF_OBJETO_COBRO
    WHERE COMPANIA   = UN_COMPANIA
      AND ANO          = UN_ANIO
      AND TIPOCOBRO    = UN_TIPOCOBRO
      AND CODIGO_COBRO = UN_CODIGO_COBRO;   

      IF MI_BASE_FIJA = 'SI' THEN 

          IF RS.APLICAFORMULA NOT IN (0) THEN 

            MI_FORMULA := REPLACE(RS.FORMULA,'VlrBaseF()',MI_VLRBASEFIJO);

            MI_FORMULA := REPLACE(MI_FORMULA,'Tarifa()',1);

            MI_FORMULA := REPLACE(MI_FORMULA,'SalarioDiario()',PCK_FACT_GENERAL_COM2.FC_RETORNAR_VLR_FORM(UN_COMPANIA     => UN_COMPANIA,
                                                                                                  UN_ANIO       => UN_ANIO,
                                                                                                  UN_CAMPO      => 'SALARIOMINIMO',
                                                                                                  UN_SALARIOMEN => -1));

            MI_FORMULA := REPLACE(MI_FORMULA,'UVT()',PCK_FACT_GENERAL_COM2.FC_RETORNAR_VLR_FORM(UN_COMPANIA     => UN_COMPANIA,
                                                                                                  UN_ANIO       => UN_ANIO,
                                                                                                  UN_CAMPO      => 'VALORUVT',
                                                                                                  UN_SALARIOMEN => 0));


            MI_BASE_FIJO      := PCK_SYSMAN_UTL.FC_EVAL(MI_FORMULA);
            MI_VALOR_UNITARIO := PCK_FACT_GENERAL_COM1.FC_CARGA_CONCEP_VALOR_CANT(UN_VALOR => MI_BASE_FIJO*MI_VLR_TARIFA*MI_VLR_ESTRATO,
                                                                                  UN_CANTIDAD => 1,
                                                                                  UN_DIGITO   => MI_DIG_TOTAL);
            MI_VALOR_UNIDAD   := MI_VALOR_UNITARIO;

          ELSIF RS.VALOR_BASE > 1 THEN 
            MI_BASE_FIJO      := RS.VALOR_BASE;
            MI_VALOR_UNITARIO := PCK_FACT_GENERAL_COM1.FC_CARGA_CONCEP_VALOR_CANT(UN_VALOR => MI_BASE_FIJO*MI_VLR_TARIFA*MI_VLR_ESTRATO,
                                                                                  UN_CANTIDAD => 1,
                                                                                  UN_DIGITO   => MI_DIG_TOTAL);
            MI_VALOR_UNIDAD   := MI_VALOR_UNITARIO;

          END IF;
      END IF;

     IF RS.AFECTAINVENTARIO IN (0) THEN 
        MI_EXISTENCIA := 999;
     ELSE 
       MI_EXISTENCIA := PCK_FACT_GENERAL.FC_ACT_VALOR_ELEMENTO(UN_COMPANIA      => UN_COMPANIA,
                                                               UN_ANIO          => UN_ANIO,
                                                               UN_TIPOCOBRO     => UN_TIPOCOBRO,
                                                               UN_ELEMENTO      => RS.CODIGO,
                                                               UN_PORC_UTILIDAD => RS.PORCETAJE_UTILIDAD,
                                                                UN_USUARIO       => UN_USUARIO);
     END IF;



     IF MI_DIG_REDONDEO = 0 THEN 
         MI_DIG_REDONDEO := 1;
     END IF;

     IF MI_DIG_TOTAL = 0 THEN 
        MI_DIG_TOTAL := 1;
     END IF;

     IF MI_DIG_REDONDEO = 2 THEN 
        MI_VALOR_COMPRA := RS.VALOR_COMPRA * MI_CANTIDAD;
     ELSE 
        MI_VALOR_COMPRA := TRUNC((TRUNC(RS.VALOR_COMPRA / MI_DIG_REDONDEO + 0.51) * MI_DIG_REDONDEO) * MI_CANTIDAD/ (MI_DIG_REDONDEO+0.501)) * MI_DIG_REDONDEO;
     END IF;


   /* MI_VALOR_BASE := PCK_FACT_GENERAL_COM1.FC_CARGA_CONCEP_VALOR_CANT(UN_VALOR    => MI_VALOR_UNITARIO  ,
                                                                      UN_CANTIDAD => MI_CANTIDAD,
                                                                      UN_DIGITO   => MI_DIG_TOTAL);*/


      MI_VALOR_BASE := MI_VALOR_UNITARIO * MI_CANTIDAD;

    -- AQUI EMPIEZA LA MAGIA DE HARRY POTTER

     IF RS.PERMITEMODIFICARVALOR NOT IN (0) THEN  -- IF PRINCIPAL
        IF NVL(MI_VALOR_UNIDAD,0) = 0 THEN 
            MI_VALOR_UNITARIO := NVL(MI_VALOR_UNITARIO,0);

            IF MI_VALOR_UNITARIO < 0  THEN 
                MI_VALOR_UNITARIO  := MI_VALOR_UNITARIO * -1;
            END IF;

            IF MI_VALOR_BASE = 0 THEN  --INICIO IF VALOR BASE = 0 

                MI_VALOR_UNITARIO := PCK_FACT_GENERAL_COM1.FC_CARGA_CONCEP_VALOR(UN_VALOR  => RS.VALOR_BASE  ,
                                                                                 UN_DIGITO => MI_DIG_REDONDEO);
                MI_VALOR_BASE := PCK_FACT_GENERAL_COM1.FC_CARGA_CONCEP_VALOR(UN_VALOR  => RS.VALOR_BASE * MI_CANTIDAD ,
                                                                             UN_DIGITO => MI_DIG_TOTAL);               


            END IF;    -- FINAL  IF VALOR BASE = 0


        END IF;

    ELSIF MI_VALOR_UNIDAD = 0 THEN  -- ELSEif  DEL IF PRINCIPAL.
        MI_VALOR_BASE := PCK_FACT_GENERAL_COM1.FC_CARGA_CONCEP_VALOR(UN_VALOR    => MI_VALOR_UNITARIO * MI_CANTIDAD ,
                                                                     UN_DIGITO   => MI_DIG_TOTAL); 

        MI_VALOR_UNITARIO :=  PCK_FACT_GENERAL_COM1.FC_CARGA_CONCEP_VALOR(UN_VALOR  => RS.VALOR_BASE  ,
                                                                             UN_DIGITO => MI_DIG_REDONDEO);

        MI_VALOR_BASE := PCK_FACT_GENERAL_COM1.FC_CARGA_CONCEP_VALOR(UN_VALOR    => RS.VALOR_BASE * MI_CANTIDAD,
                                                                     UN_DIGITO   => MI_DIG_TOTAL);                                                                      

        BEGIN
            BEGIN

                MI_TABLA:='SF_DETALLE_COBRO';

                MI_CAMPOS:=' BASE_FIJA     = '||MI_VLRBASEFIJO||',
                                 VALOR_BASE    = '||MI_VALOR_BASE||',
                                 MODIFIED_BY   = '''||UN_USUARIO||''',
                                 DATE_MODIFIED = SYSDATE ';

                MI_CONDICION:=' COMPANIA     = '''||UN_COMPANIA||''' 
                             AND CODIGO_COBRO = '||UN_CODIGO_COBRO||' 
                             AND CONCEPTO     = '||RS.CODIGO;

                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                      UN_ACCION    => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS,
                                                      UN_CONDICION => MI_CONDICION);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                  MI_MSGERROR(1).CLAVE := 'CONCEPTO';
                  MI_MSGERROR(1).VALOR := RS.CODIGO;  
                 RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_TABLAERROR => MI_TABLA, 
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTUA_VALOR_BASE,
                                        UN_REEMPLAZOS => MI_MSGERROR);
        END;

    END IF; -- CIERRE IF PRINCIPAL


    MI_PARAMETRO :=  PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                           UN_NOMBRE    => 'SF CONCEPTO PRINCIPAL PRODESARROLLO',
                                           UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                           UN_FECHA_PAR => SYSDATE,
                                           UN_IND_MAYUS => 0);


     --CALCULA EL DESCUENTO 
    IF RS.APLICADESCUENTO NOT IN (0) THEN 
        MI_VALOR_DESCUENTO := PCK_FACT_GENERAL_COM1.FC_CARGA_CONCEP_INDICA(UN_INDICADOR => RS.APLICAIVA,
                                                                           UN_VALOR     => (MI_VALOR_BASE * (RS.PORCENTAJEDESCUENTO/100)),
                                                                           UN_DIGITO    => MI_RED_DESC);
    ELSE 
        MI_VALOR_DESCUENTO := 0;
    END IF;

    --CALCULA EL VALOR  DEL IVA      
    IF RS.APLICAIVA NOT IN (0) THEN

        MI_VALOR_IVA := PCK_FACT_GENERAL_COM1.FC_CARGA_CONCEP_INDICA(UN_INDICADOR => RS.APLICAIVA,
                                                                 UN_VALOR     => (MI_VALOR_BASE - MI_VALOR_DESCUENTO) * (RS.PORCENTAJEIVA/100),
                                                                 UN_DIGITO    => MI_RED_IVA);          
    ELSE     
        MI_VALOR_IVA := 0;
    END IF;          

   --CALCULA EL VALOR RETEFUENTE
   IF RS.APLICARETEFUENTE NOT IN (0)THEN

        MI_VALOR_RETEFUENTE := PCK_FACT_GENERAL_COM1.FC_CARGA_CONCEP_INDICA(UN_INDICADOR =>  RS.APLICARETEFUENTE,
                                                                            UN_VALOR     => MI_VALOR_BASE * (RS.PORCENTAJERETEFUENTE/100),    
                                                                            UN_DIGITO    => MI_RED_RETE);  
   ELSE     
        MI_VALOR_RETEFUENTE := 0;          
   END IF;

   --CALCULA VALOR ICA
   IF RS.APLICAICA NOT IN (0)THEN
      MI_VALOR_ICA := PCK_FACT_GENERAL_COM1.FC_CARGA_CONCEP_INDICA(UN_INDICADOR => RS.APLICAICA,
                                                                   UN_VALOR     => MI_VALOR_BASE * (RS.PORCENTAJEICA/100),
                                                                   UN_DIGITO    => MI_RED_ICA);
   ELSE
      MI_VALOR_ICA := 0;
   END IF; 

   --CALCULA EL VALOR NETO

     MI_VALOR_NETO := MI_VALOR_BASE + MI_VALOR_IVA + MI_VALOR_RETEFUENTE - MI_VALOR_DESCUENTO + MI_VALOR_ICA;


   IF MI_VALOR_UNITARIO = 0 THEN 
      MI_VALOR_UNITARIO := MI_VALOR_BASE / MI_CANTIDAD;      
   END IF;

   --CALCULA EL VALOR UTILIDAD
   MI_VALOR_UTILIDAD := MI_VALOR_BASE - MI_VALOR_COMPRA;
   MI_VALOR_UNIDAD   := 0;
   MI_CENTRO_COSTO   := NVL(RS.CENTRO_COSTO,'99999999999999999999');
   MI_AUXILIAR       := NVL(RS.AUXILIAR,'9999999999999999');


   IF PCK_FACT_GENERAL_COM1.FC_MAN_DER_CONN_CONCEP(UN_COMPANIA  => UN_COMPANIA,
                                               UN_ANIO      => UN_ANIO,
                                               UN_TIPOCOBRO => UN_TIPOCOBRO,
                                               UN_CONCEPTO  => RS.CODIGO) NOT IN (0) THEN 

      -- CALCULA ACOMETIDA
      --LA FUNCION DE ACCES FormulaAcometida ES UNA CONSULTA A LA TABLA SF_CONCETOS, POR LO QUE SE DECIDIO INCLUIR LOS CAMPOS  EN EL 
      --CURSOR E IMPLEMENTAR LAS VALIDACIONES EN LOS SIGUIENTES CONDICIONALES  
       MI_ACOMETIDA_SERVICIO :=  PCK_FACT_GENERAL_COM1.FC_CARGA_CONCEP_FORMULA(UN_FORMULA        => RS.FORMULAR_ACOMETIDA,
                                                                              UN_CUENTA_DEB     => RS.CTADEBACOMETIDA,
                                                                              UN_CUENTADOS_CRED => RS.CTACREDACOMETIDA,
                                                                              UN_DIG_REDONDEO   => MI_DIG_REDONDEO);




      -- CALCULAR AIU

        MI_AIU :=  PCK_FACT_GENERAL_COM1.FC_CARGA_CONCEP_FORMULA(UN_FORMULA        => RS.FORMULAR_AIU,
                                                                UN_CUENTA_DEB     => RS.CTADEBAIU,
                                                                UN_CUENTADOS_CRED => RS.CTACREDAIU,
                                                                UN_DIG_REDONDEO   => MI_DIG_REDONDEO);

        MI_ADMON_IMPREVISTOS :=  PCK_FACT_GENERAL_COM1.FC_CARGA_CONCEP_FORMULA(UN_FORMULA        => RS.FORMULAR_ADMONIMP,
                                                                              UN_CUENTA_DEB     => RS.CTADEBADMONIMP,
                                                                              UN_CUENTADOS_CRED => RS.CTACREDADMONIMP,
                                                                              UN_DIG_REDONDEO   => MI_DIG_REDONDEO);     


        MI_UTILIDAD_AIU :=  PCK_FACT_GENERAL_COM1.FC_CARGA_CONCEP_FORMULA(UN_FORMULA         => RS.FORMULAR_UTILIDADAIU,
                                                                          UN_CUENTA_DEB     => RS.CTADEBUTILIDADAIU,
                                                                          UN_CUENTADOS_CRED => RS.CTACREDUTILIDADAIU,
                                                                          UN_DIG_REDONDEO   => MI_DIG_REDONDEO);
      -- CALCULA EL IVA UTILIDAD

         MI_IVA_UTILIDAD :=  PCK_FACT_GENERAL_COM1.FC_CARGA_CONCEP_FORMULA(UN_FORMULA        => RS.FORMULAR_IVAUTILIDAD,
                                                                          UN_CUENTA_DEB     => RS.CTADEBIVAUTILIDAD,
                                                                          UN_CUENTADOS_CRED => RS.CTACREDIVAUTILIDAD,
                                                                          UN_DIG_REDONDEO   => MI_DIG_REDONDEO);

         MI_SUMAAIU := MI_ACOMETIDA_SERVICIO + MI_ADMON_IMPREVISTOS + MI_UTILIDAD_AIU + MI_IVA_UTILIDAD;
         MI_DIFAIU  := MI_VALOR_NETO - MI_SUMAAIU;

         IF MI_DIFAIU <> 0 THEN 
            MI_IVA_UTILIDAD := ABS (MI_IVA_UTILIDAD + MI_DIFAIU);
            IF MI_DIG_REDONDEO <> 2 THEN 
                MI_IVA_UTILIDAD := TRUNC(MI_IVA_UTILIDAD/ MI_DIG_REDONDEO+0.501) * MI_DIG_REDONDEO;       
            END IF;

         END IF;
   ELSE
       MI_ACOMETIDA_SERVICIO := 0;
       MI_AIU                := 0;       
       MI_ADMON_IMPREVISTOS  := 0;
       MI_UTILIDAD_AIU       := 0;
       MI_IVA_UTILIDAD       := 0;

   END IF; -- END IF PRINCIPAL

     BEGIN
        BEGIN           
             MI_CAMPOS := 'COMPANIA,
                      ANO,
                      CODIGO_COBRO,
                      TIPOCOBRO,
                      CONCEPTO,
                      ACOMETIDA_SERVICIO,
                      ADMON_IMPREVISTOS,
                      AIU,
                      AUXILIAR,
                      CANTIDAD,
                      CENTRO_COSTO,
                      FECHA_FIN_COBRO,
                      FECHA_INICIO_COBRO,
                      IVA_UTILIDAD,
                      UTILIDAD_AIU,
                      VALOR_BASE,
                      VALOR_COMPRA,
                      VALOR_DESCUENTO,
                      VALOR_ICA,
                      VALOR_IVA,
                      VALOR_NETO,
                      VALOR_RETEFUENTE,
                      VALOR_UNITARIO,
                      VALOR_UTILIDAD,
                      BASE_FIJA,
                      FUENTE_RECURSO,
                      REFERENCIA,
                      TERCERO,
                      SUCURSAL,
                      CUENTA_BANCO,
                      CREATED_BY,
                      DATE_CREATED';

            MI_VALORES := ''''||UN_COMPANIA||''',
                          '||UN_ANIO||',
                          '||UN_CODIGO_COBRO||',
                          '''||UN_TIPOCOBRO||''',
                          '''||RS.CODIGO||''',
                          '||MI_ACOMETIDA_SERVICIO||',
                          '||MI_ADMON_IMPREVISTOS||',
                          '||MI_AIU||',
                          '''||MI_AUXILIAR||''',
                          '||MI_CANTIDAD||',                    
                          '''||MI_CENTRO_COSTO||''',
                          SYSDATE,
                          SYSDATE,
                          '||NVL(MI_IVA_UTILIDAD,0)||',
                          '||NVL(MI_UTILIDAD_AIU,0)||',
                          '||NVL(MI_VALOR_BASE,0)||',
                          '||NVL(MI_VALOR_COMPRA,0)||',
                          '||NVL(MI_VALOR_DESCUENTO,0)||',                    
                          '||NVL(MI_VALOR_ICA,0)||',
                          '||NVL(MI_VALOR_IVA,0)||',
                          '||NVL(MI_VALOR_NETO,0)||',
                          '||NVL(MI_VALOR_RETEFUENTE,0)||',
                          '||NVL(MI_VALOR_UNITARIO,0)||',
                          '||NVL(MI_VALOR_UTILIDAD,0)||',
                          '||NVL(MI_VLRBASEFIJO,0)||',
                          '''||RS.FUENTE_RECURSO||''',
                          '''||RS.REFERENCIA||''',
                          '''||MI_TERCERO||''',
                          '''||MI_SUCURSAL||''',
                          '''||RS.CUENTA_RECAUDO||''',
                          '''||UN_USUARIO||''',
                          SYSDATE';

            PCK_DATOS.GL_RTA :=  PCK_DATOS.FC_ACME(UN_TABLA   => 'SF_DETALLE_COBRO',
                                                   UN_ACCION  => 'I', 
                                                   UN_CAMPOS  => MI_CAMPOS, 
                                                   UN_VALORES => MI_VALORES);  

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
              MI_MSGERROR(1).CLAVE := 'CONCEPTO';
              MI_MSGERROR(1).VALOR := RS.CODIGO;  
              RAISE PCK_EXCEPCIONES.EXC_SYSMANSF; 
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_TABLAERROR => MI_TABLA, 
                                   UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_CARGAR_CONCEPTO,
                                   UN_REEMPLAZOS => MI_MSGERROR);
     END;

  END LOOP;


END  PR_CARGAR_CONCEPTOS;


--9
PROCEDURE PR_EJECUTARANULACION
/*
    NAME              : PR_EJECUTARANULACION --> en acces boton aceptar frm_anularfactura
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
    DATE MIGRADOR     : 01/12/2017
    TIME              : 04:20 PM  
    SOURCE MODULE     : SysmanSF2017.11.01
    MODIFIER          : GUSTAVO ANDRÉS FIGUEREDO ÁVILA
    DATE MODIFIED     : 28/09/2021
    TIME              : 14:30
    MODIFICATIONS     : Se añade validación para realizar el proceso de anulación solo para
    					facturas no recaudadas.
    DESCRIPTION       : PROCEDIMIENTO QUE ANULA UN DETERMINADO NUMERO DE FACTURAS
    PARAMETERS        : UN_COMPANIA       => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                        UN_TIPOCOBRO      => TIPO DE COBRO.
                        UN_FACTURAINICIAL => FACTURA FINAL A ANULAR.
                        UN_FACTURAFINAL   => FACTURA FINAL A ANULAR.
                        UN_USUARIO        => USUARIO QUE ESTA REALIZANDO LA OPERACION.

      @NAME:    ejecutarAnulacionFacturas
      @METHOD:  PUT
  */
(
  UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOCOBRO       IN SF_FACTURA.TIPOCOBRO%TYPE,
  UN_ANIO            IN SF_TIPO_COBRO.ANO%TYPE,
  UN_FACTURAINICIAL  IN SF_FACTURA.NUMERO_FACTURA%TYPE,
  UN_FACTURAFINAL    IN SF_FACTURA.NUMERO_FACTURA%TYPE,
  UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO
)
AS
  MI_TIPOANULACION          SF_TIPO_COBRO.TIPOCPTE_ANULACION%TYPE;
  MI_EXISTE                 PCK_SUBTIPOS.TI_ENTERO;
  MI_SERIE                  PCK_SUBTIPOS.TI_LOGICO;
  MI_INDPAGO				PCK_SUBTIPOS.TI_ENTERO;
  MI_CODIGO_COBRO			SF_FACTURA.CODIGO_COBRO%TYPE;

BEGIN

  BEGIN
    BEGIN
     SELECT TIPOCPTE_ANULACION 
      INTO MI_TIPOANULACION
      FROM   SF_TIPO_COBRO
        WHERE  COMPANIA = UN_COMPANIA
        AND    ANO =  UN_ANIO
        AND    CODIGO   = UN_TIPOCOBRO;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
    END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    =>  SQLCODE,
                    UN_ERROR_COD  =>  PCK_ERRORES.ERR_SYSMANSF_TIPOANULACION
                  );
  END;

    IF MI_TIPOANULACION IS NULL THEN
      BEGIN
         RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD =>SQLCODE,
            UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_TIPOCOMANULACION
          );
      END;
    END IF;


    SELECT COUNT(1)
    INTO MI_EXISTE 
    FROM   TIPO_COMPROBANTE 
    WHERE  COMPANIA = UN_COMPANIA 
    AND CODIGO = MI_TIPOANULACION;

    IF MI_EXISTE = 0 THEN
       BEGIN
           RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD =>SQLCODE,
              UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_TIPOCOMP 
            );
        END;
     END IF;


     IF UN_FACTURAINICIAL-UN_FACTURAFINAL = 0 THEN
        MI_SERIE := 0;
     ELSE
        MI_SERIE := 1;
     END IF;

     FOR FACTURA IN UN_FACTURAINICIAL..UN_FACTURAFINAL 
     LOOP
     	/* Inicio Ticket 770485 - gfigueredo.
     	 * Se agrega validación sobre la factura, si la factura ya fue recaudada no se puede anular.*/
     
     	SELECT CODIGO_COBRO 
     	INTO MI_CODIGO_COBRO
	 	FROM SF_FACTURA
 		WHERE COMPANIA = UN_COMPANIA
 		AND ANO = UN_ANIO
 		AND TIPOCOBRO = UN_TIPOCOBRO
 		AND NUMERO_FACTURA = FACTURA;
 		
 		--Debido a que la llave de la tabla SF_OBJETO_COBRO es por compania, tipocobro, ano y codigo_cobro,
 		--se obtiene previamente el codigo de cobro desde la tabla SF_FACTURA. Se identificaron varios registros
 		--en la tabla SF_OBJETO_COBRO con el mismo numero de factura y diferente codigo de cobro.
       	SELECT INDPAGO 
       	INTO MI_INDPAGO
  		FROM SF_OBJETO_COBRO
 		WHERE COMPANIA = UN_COMPANIA
  		AND TIPOCOBRO = UN_TIPOCOBRO
  		AND ANO = UN_ANIO
  		AND CODIGO_COBRO = MI_CODIGO_COBRO;
  	
  		IF MI_INDPAGO <> -1 THEN
         	PCK_FACT_GENERAL_COM1.PR_ANULARFACTURACION(UN_COMPANIA       =>  UN_COMPANIA,
	                                                    UN_TIPOCOBRO      =>  UN_TIPOCOBRO,
	                                                    UN_ANIO           =>  UN_ANIO,
	                                                    UN_FACTURA        =>  FACTURA,
	                                                    UN_SERIE          =>  MI_SERIE,
	                                                    UN_TIPOANULACION  =>  MI_TIPOANULACION,
	                                                    UN_USUARIO        =>  UN_USUARIO);
	    END IF;
	   /* Fin Ticket 770485 - gfigueredo.*/
     END LOOP;
END PR_EJECUTARANULACION;



--10
PROCEDURE PR_ANULARFACTURACION
/*
    NAME              : PR_ANULARFACTURACION --> en acces funcion AnularFactura
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
    DATE MIGRADOR     : 30/11/2017
    TIME              : 11:25 AM  
    SOURCE MODULE     : SysmanSF2017.11.01
    MODIFIER          : GUSTAVO ANDRÉS FIGUEREDO ÁVILA
    DATE MODIFIED     : 08/09/2021
    TIME              : 14:30
    MODIFICATIONS     : Se añade a la consulta que trae los datos del comprobante para
    					insertar en detalle_comprobanet_cnt los nuevos datos de la nota,
    					el valor DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO
    MODIFIER          : GUSTAVO ANDRÉS FIGUEREDO ÁVILA
    DATE MODIFIED     : 28/09/2021
    TIME              : 17:30
    MODIFICATIONS     : Se añade filtro por ano y anulada para realizar el proceso solo sobre
    					facturas no anuladas.
    DESCRIPTION       : PROCEDIMIENTO QUE ANULA UN DETERMINADO NUMERO DE FACTURAS
    PARAMETERS        : UN_COMPANIA       => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                        UN_TIPOCOBRO      => TIPO DE COBRO.
                        UN_FACTURAINICIAL => FACTURA FINAL A ANULAR.
                        UN_FACTURAFINAL   => FACTURA FINAL A ANULAR.
                        UN_USUARIO        => USUARIO QUE ESTA REALIZANDO LA OPERACION.

      @NAME:    anularFacturacion 
      @METHOD:  PUT
  */
(
  UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOCOBRO       IN SF_FACTURA.TIPOCOBRO%TYPE,
  UN_ANIO            IN SF_TIPO_COBRO.ANO%TYPE,
  UN_FACTURA         IN SF_FACTURA.NUMERO_FACTURA%TYPE,
  UN_SERIE           IN PCK_SUBTIPOS.TI_LOGICO,
  UN_TIPOANULACION   IN SF_TIPO_COBRO.TIPOCPTE_ANULACION%TYPE,
  UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO
)
AS 
  MI_EXISTE                 PCK_SUBTIPOS.TI_ENTERO;
  MI_DESCRIPCION            PCK_SUBTIPOS.TI_DESCRIPCION;
  MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
  MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
  MI_RTA                    PCK_SUBTIPOS.TI_LONG;
  MI_AUX                    PCK_SUBTIPOS.TI_NUMORDEN;
  MI_CUENTAPPTAL            PLAN_CONTABLE.CUENTA_PPTAL%TYPE;
  MI_COMPROBANTE            COMPROBANTE_CNT.NUMERO%TYPE;
  MI_CLASECONTABLE          TIPO_COMPROBANTE.CLASE_CONTABLE%TYPE;
  MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_CONSECUTIVO            DETALLE_COMPROBANTE_CNT.CONSECUTIVO%TYPE;
  MI_RTA2                   PCK_SUBTIPOS.TI_LOGICO;
  MI_CODIGONOTA             VARCHAR2(11 CHAR);
  MI_TIPONOTA               VARCHAR2(11 CHAR);
  
BEGIN
    
    IF  PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                         UN_NOMBRE    => 'SF PERMITE CONFIGURAR FACTURA ELECTRONICA', 
                                         UN_MODULO    => 69,
                                         UN_FECHA_PAR => SYSDATE)= 'SI' THEN
    
        MI_CODIGONOTA := '2';
        MI_TIPONOTA := 'CNC';
        
    ELSE
    
       MI_CODIGONOTA := '';
       MI_TIPONOTA := '';
        
    END IF;

	/* Inicio Ticket 770485 - gfigueredo.
     * Se agrega filtro por ano y anulada, debido a que al presionar el boton de aceptar en el 
     * formulario de anular facturación varias veces toma el valor de la factura cada vez, ya que la
     * validación de las facturas que se enviaban a anular se realizaba sobre la lista desplegable y no
     * en el procedimiento.*/
    MI_EXISTE:=0;
       <<SFFACTURA>>
       FOR RS IN (SELECT  INTERFAZADA, 
                          ANO_CPTE, 
                          TIPO_CPTE, 
                          NRO_CPTE, 
                          FECHA_PAGO, 
                          BANCO_PAGO, 
                          ANULADA, 
                          CPTE_ANULACION, 
                          NRO_ANULACION,
                          OBSERVACIONES, 
                          CODIGO_COBRO, 
                          FECHA_EXPEDICION, 
                          TERCERO, 
                          SUCURSAL,
                          TIPOCPTE_PAGO,
                          ANOCPTE_PAGO,
                          NROCPTE_PAGO 
                   FROM   SF_FACTURA   
                   WHERE  COMPANIA = UN_COMPANIA
                     AND  TIPO_FACTURA =  UN_TIPOCOBRO
                     AND  NUMERO_FACTURA =  UN_FACTURA
                     AND  ANO = UN_ANIO
                     AND  SF_FACTURA.ANULADA IN(0)) 
        /* Fin Ticket 770485 - gfigueredo.*/
       LOOP 
           MI_MSGERROR (1).CLAVE := 'FACTURA';
           MI_MSGERROR (1).VALOR := UN_FACTURA;
           MI_MSGERROR (2).CLAVE := 'FECHA';
           MI_MSGERROR (2).VALOR := TO_CHAR(RS.FECHA_PAGO, 'DD/MM/YYYY');
           MI_MSGERROR (3).CLAVE := 'CUENTA';
           MI_MSGERROR (3).VALOR := RS.BANCO_PAGO ;
           IF RS.INTERFAZADA NOT IN(0)THEN 
        /*IF UN_SERIE = 1 THEN 
                PCK_FACT_GENERAL_COM1.PR_REGISTRARPROCESO( UN_COMPANIA     => UN_COMPANIA,
                                                           UN_CLASE        => 'ANFRA' ,
                                                           UN_TIPOCOBRO    => UN_TIPOCOBRO,
                                                           UN_COBRO        => UN_FACTURA,
                                                           UN_DESCRIPCION  => 'La factura '||UN_FACTURA||' ha sido recaudada previamente en la fecha ' ||
                                                                               RS.FECHA_PAGO || ' y en la cuenta '||
                                                                               RS.BANCO_PAGO , 
                                                            UN_USUARIO      => UN_USUARIO);
                RETURN;                                                            
              END IF;

                IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                         UN_NOMBRE    => 'SF PERMITE ANULAR FACTURA SI YA TIENE RECAUDO', 
                                         UN_MODULO    => -1,
                                         UN_FECHA_PAR => SYSDATE)= 'NO' THEN

                  IF UN_SERIE = 1 THEN
                    RETURN;
                  ELSE
                      BEGIN
                         RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 

                          PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    =>  SQLCODE,
                            UN_ERROR_COD  =>  PCK_ERRORES.ERR_SYSMANSF_RECAUDOFACTURA,
                            UN_REEMPLAZOS =>  MI_MSGERROR          
                          );
                      END;
                  END IF;    
                END IF;
    

                SELECT COUNT(1)
                  INTO MI_EXISTE
                  FROM COMPROBANTE_CNT 
                  WHERE  COMPANIA = UN_COMPANIA
                    AND  ANO      = RS.ANOCPTE_PAGO
                    AND  TIPO     = RS.TIPOCPTE_PAGO 
                    AND  NUMERO   = RS.NROCPTE_PAGO;
                  IF MI_EXISTE = 0 THEN
                     BEGIN
                       RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD    =>  SQLCODE,
                          UN_ERROR_COD  =>  PCK_ERRORES.ERR_SYSMANSF_RECAUDOCOMFACT,
                          UN_REEMPLAZOS =>  MI_MSGERROR 
                        );
                     END;
                  ELSE
                    MI_CAMPOS  :=' FECHA_PAGO    = NULL, 
                                   BANCO_PAGO    = NULL,
                                   MODIFIED_BY   = '''||UN_USUARIO||''',
                                   DATE_MODIFIED = SYSDATE';
                    MI_CONDICION:=' COMPANIA            = '''||UN_COMPANIA||''''||  
                                  ' AND  TIPO_FACTURA   = '''||UN_TIPOCOBRO||''''||  
                                  ' AND  NUMERO_FACTURA = '|| UN_FACTURA;  
                    BEGIN
                    BEGIN      
                          PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME( UN_ACCION    => 'M',
                                                               UN_TABLA     => 'SF_FACTURA',
                                                               UN_CAMPOS    => MI_CAMPOS,
                                                               UN_CONDICION => MI_CONDICION);
                           EXCEPTION  WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                     END;                             
                                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                         UN_EXC_COD    => SQLCODE,
                                         UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTFECHABANCO,
                                         UN_REEMPLAZOS =>  MI_MSGERROR
                                        );
                          END;                
                  END IF;/
           END IF;
         IF RS.INTERFAZADA = 0 THEN
            IF UN_SERIE = 1 THEN
                PCK_FACT_GENERAL_COM1.PR_REGISTRARPROCESO( UN_COMPANIA     => UN_COMPANIA,
                                                           UN_CLASE        => 'ANFRA' ,
                                                           UN_TIPOCOBRO    => UN_TIPOCOBRO,
                                                           UN_COBRO        => UN_FACTURA,
                                                           UN_DESCRIPCION  => 'La factura '||UN_FACTURA||' 
                                                                              no se causó correctamente por lo tanto no es posible realizar el proceso de anulación ', 
                                                            UN_USUARIO      => UN_USUARIO);
                RETURN;
            ELSE

              MI_RTA2 := PCK_FACT_GENERAL_COM3.FC_INTERFAZCONTABLE(UN_COMPANIA         => UN_COMPANIA,  
                                          UN_TIPO             => UN_TIPOCOBRO,
                                          UN_NUMERO           => RS.CODIGO_COBRO,
                                          UN_FECHA            => RS.FECHA_EXPEDICION,
                                          UN_TERCERO          => RS.TERCERO,
                                          UN_SUCURSAL         => RS.SUCURSAL,
                                          UN_DESCRIPCION      => 'Factura '|| UN_TIPOCOBRO ||' - '||UN_FACTURA||'  - '||RS.OBSERVACIONES,
                                          UN_MANEJAINVENTARIO => 0,
                                          UN_USUARIO          => UN_USUARIO);
                MI_CAMPOS  :=' INTERFAZADA   = -1, 
                               ANO_CPTE      = '  || EXTRACT( YEAR FROM RS.FECHA_EXPEDICION)||',
                               TIPO_CPTE     = '''||UN_TIPOCOBRO||''',
                               NRO_CPTE      = '  ||RS.CODIGO_COBRO||',
                               MODIFIED_BY   = '''||UN_USUARIO||''',
                               DATE_MODIFIED = SYSDATE';
                MI_CONDICION:=' COMPANIA            = ''' ||UN_COMPANIA ||''''||  
                              ' AND  TIPO_FACTURA   = ''' ||UN_TIPOCOBRO||''''||  
                              ' AND  NUMERO_FACTURA = '   || UN_FACTURA;  
                BEGIN
                  BEGIN      
                      MI_RTA:=PCK_DATOS.FC_ACME( UN_ACCION    => 'M',
                                                           UN_TABLA     => 'SF_FACTURA',
                                                           UN_CAMPOS    => MI_CAMPOS,
                                                           UN_CONDICION => MI_CONDICION);
                       EXCEPTION  WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                  END;                             
                             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                                   PCK_ERR_MSG.RAISE_WITH_MSG(
                                     UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTFACTURA,
                                     UN_REEMPLAZOS =>  MI_MSGERROR
                                    );
                END;
            END IF;
           END IF;

          IF RS.ANULADA <> 0 THEN
              IF UN_SERIE = 1 THEN
                 PCK_FACT_GENERAL_COM1.PR_REGISTRARPROCESO( UN_COMPANIA     => UN_COMPANIA,
                                                            UN_CLASE        => 'ANFRA' ,
                                                            UN_TIPOCOBRO    => UN_TIPOCOBRO,
                                                            UN_COBRO        => UN_FACTURA,
                                                            UN_DESCRIPCION  => 'La factura '||UN_FACTURA||' ha sido anulada con el comprobante  '|| 
                                                                                RS.CPTE_ANULACION ||' - '|| 
                                                                                RS.NRO_ANULACION, 
                                                            UN_USUARIO      => UN_USUARIO);
                 RETURN;
              END IF;
               BEGIN
                   RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
                     MI_MSGERROR (2).CLAVE := 'COMPROBANTE';
                     MI_MSGERROR (2).VALOR := RS.CPTE_ANULACION;
                     MI_MSGERROR (3).CLAVE := 'NUMERO';
                     MI_MSGERROR (3).VALOR := RS.NRO_ANULACION;
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    =>  SQLCODE,
                      UN_ERROR_COD  =>  PCK_ERRORES.ERR_SYSMANSF_FACTURAANULADA,
                      UN_REEMPLAZOS =>  MI_MSGERROR 
                    );
                 END;             
           END IF;

      */  MI_DESCRIPCION := 'Anulación de la factura '||UN_TIPOCOBRO|| ' - '|| UN_FACTURA;

        FOR RS_COM IN (SELECT COMPROBANTE_CNT.COMPANIA,
                               EXTRACT (YEAR FROM SYSDATE) ANO,
                               UN_TIPOANULACION,
                               MI_DESCRIPCION,
                               COMPROBANTE_CNT.VLR_BASE, 
                               COMPROBANTE_CNT.TEXTO, 
                               COMPROBANTE_CNT.NRO_DOCUMENTO, 
                               COMPROBANTE_CNT.FECHA_VCN_DOC, 
                               COMPROBANTE_CNT.VLR_DOCUMENTO, 
                               COMPROBANTE_CNT.TERCERO,
                               COMPROBANTE_CNT.SUCURSAL,
                               COMPROBANTE_CNT.DEBITO,
                               COMPROBANTE_CNT.CREDITO,
                               COMPROBANTE_CNT.VLRAGIRAR,
                               COMPROBANTE_CNT.CENTRO_COSTO,
                               COMPROBANTE_CNT.AUXILIAR,
                               UN_USUARIO
                        FROM COMPROBANTE_CNT 
                        WHERE   COMPROBANTE_CNT.COMPANIA  = UN_COMPANIA
                          AND   COMPROBANTE_CNT.ANO       = RS.ANO_CPTE
                          AND   COMPROBANTE_CNT.TIPO      = RS.TIPO_CPTE
                          AND   COMPROBANTE_CNT.NUMERO    = RS.NRO_CPTE)
        LOOP
        --lvega SE AGREGA EL TIPO SIGEC CON VALOR 1 PARA ANULACION DE LIQUIDACION

         MI_CAMPOS  :=  'COMPANIA, ANO, TIPO, FECHA, DESCRIPCION, 
                        VLR_BASE, TEXTO, NRO_DOCUMENTO, FECHA_VCN_DOC, VLR_DOCUMENTO, 
                        TERCERO, SUCURSAL, DEBITO, CREDITO, VLRAGIRAR, 
                        CENTRO_COSTO, AUXILIAR, TIPO_SIGEC, DATE_CREATED, CREATED_BY ';
        MI_VALORES :=  '''' ||  RS_COM.COMPANIA         ||''',
                       '    ||  RS_COM.ANO              ||',
                       '''  ||  RS_COM.UN_TIPOANULACION ||''',
                       SYSDATE,
                       '''  ||  RS_COM.MI_DESCRIPCION   ||''',
                       '    ||  RS_COM.VLR_BASE         ||', 
                       '''  ||  RS_COM.TEXTO            ||''', 
                       '''  ||  RS_COM.NRO_DOCUMENTO    ||''', 
                       TO_DATE('''|| TO_CHAR(RS_COM.FECHA_VCN_DOC,'DD/MM/YYYY')||''', ''DD/MM/YYYY''), 
                       '    ||  RS_COM.VLR_DOCUMENTO    ||', 
                       '''  ||  RS_COM.TERCERO          ||''',
                       '''  ||  RS_COM.SUCURSAL         ||''',
                       '    ||  RS_COM.DEBITO           ||',
                       '    ||  RS_COM.CREDITO          ||',
                       '    ||  RS_COM.VLRAGIRAR        ||',
                       '''  ||  RS_COM.CENTRO_COSTO     ||''',
                       '''  ||  RS_COM.AUXILIAR         ||''',
                       '    ||  1                       ||',
                       SYSDATE,
                       '''  ||  UN_USUARIO              ||'''';
         BEGIN
          BEGIN
               MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'COMPROBANTE_CNT',
                                            UN_ACCION   => 'I',
                                            UN_CAMPOS   => MI_CAMPOS,
                                            UN_VALORES  => MI_VALORES);

           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
          END; 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    =>  SQLCODE,
              UN_ERROR_COD  =>  PCK_ERRORES.ERR_SYSMANSF_INSCOMPFACTURA,
              UN_REEMPLAZOS =>  MI_MSGERROR
            );
        END;  
        END LOOP;


        SELECT MAX(NUMERO) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) NUMERO
              INTO MI_COMPROBANTE
        FROM COMPROBANTE_CNT 
        WHERE   COMPROBANTE_CNT.COMPANIA  = UN_COMPANIA 
          AND   COMPROBANTE_CNT.ANO       = EXTRACT (YEAR FROM SYSDATE) 
          AND   COMPROBANTE_CNT.TIPO      = UN_TIPOANULACION;


        MI_CAMPOS  :=  'COMPANIA, 
                        ANO,
                        COMPROBANTE, 
                        TIPO_CPTE,
                        ANO_AFECT,
                        COMPROBANTE_AFECT,
                        TIPO_CPTE_AFECT,
                        CREATED_BY,
                        DATE_CREATED';

        MI_VALORES :=  '''' || UN_COMPANIA                 ||''',
                       '    || EXTRACT (YEAR FROM SYSDATE) ||',
                       '    || MI_COMPROBANTE              ||',
                       '''  || UN_TIPOANULACION            ||''',
                       '    || RS.ANO_CPTE                 ||', 
                       '    || RS.NRO_CPTE                 ||', 
                       '''  || RS.TIPO_CPTE                ||''', 
                       '''  || UN_USUARIO                  ||''',
                       SYSDATE';

        BEGIN
          BEGIN
               PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'COMPROBANTE_CNTAFECTADOS',
                                                      UN_ACCION   => 'I',
                                                      UN_CAMPOS   => MI_CAMPOS,
                                                      UN_VALORES  => MI_VALORES);

           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
          END; 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    =>  SQLCODE,
              UN_ERROR_COD  =>  PCK_ERRORES.ERR_SYSMANSF_INSCOMPFACTURA,
              UN_REEMPLAZOS =>  MI_MSGERROR
            );
        END;  

        IF MI_RTA > 0 THEN
        MI_CONSECUTIVO := 1;
          <<DETALLES>>
          FOR RS_DET IN (SELECT DETALLE_COMPROBANTE_CNT.TIPO_CPTE, 
                            DETALLE_COMPROBANTE_CNT.COMPROBANTE, 
                            DETALLE_COMPROBANTE_CNT.CUENTA,
                            DETALLE_COMPROBANTE_CNT.NATURALEZA,
                            DETALLE_COMPROBANTE_CNT.TERCERO, 
                            DETALLE_COMPROBANTE_CNT.SUCURSAL, 
                            DETALLE_COMPROBANTE_CNT.CENTRO_COSTO, 
                            DETALLE_COMPROBANTE_CNT.AUXILIAR, 
                            DETALLE_COMPROBANTE_CNT.CONSECUTIVO, 
                            DETALLE_COMPROBANTE_CNT.VALOR_DEBITO,
                            DETALLE_COMPROBANTE_CNT.DEBITO_AFECTADO,
                            DETALLE_COMPROBANTE_CNT.VALOR_CREDITO,
                            DETALLE_COMPROBANTE_CNT.CREDITO_AFECTADO, 
                            DETALLE_COMPROBANTE_CNT.CUENTAPPTAL,
                            DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO,
                            DETALLE_COMPROBANTE_CNT.EQUIV_SIGEC,
                            DETALLE_COMPROBANTE_CNT.NROCONTRATOSIGEC,
                            DETALLE_COMPROBANTE_CNT.TIPOCONTRATOSIGEC,                          
                            DETALLE_COMPROBANTE_CNT.VALOR_IVA ,
                            DETALLE_COMPROBANTE_CNT.VALOR_RETEIVA,
                            DETALLE_COMPROBANTE_CNT.VALOR_RETEFUENTE,
                            DETALLE_COMPROBANTE_CNT.VALOR_ICA
                     FROM   DETALLE_COMPROBANTE_CNT
                     WHERE  DETALLE_COMPROBANTE_CNT.COMPANIA= UN_COMPANIA
                       AND  DETALLE_COMPROBANTE_CNT.ANO = RS.ANO_CPTE
                       AND  DETALLE_COMPROBANTE_CNT.TIPO_CPTE = RS.TIPO_CPTE 
                       AND  DETALLE_COMPROBANTE_CNT.COMPROBANTE = RS.NRO_CPTE)
          LOOP
            IF RS_DET.VALOR_DEBITO <> 0 THEN
              MI_AUX:='D';
            ELSIF  RS_DET.VALOR_CREDITO <> 0 THEN
              MI_AUX:='C';
            END IF;

            IF RS_DET.CUENTAPPTAL IS NULL THEN
              BEGIN
                SELECT CUENTA_PPTAL
                INTO MI_CUENTAPPTAL
                FROM PLAN_CONTABLE
                WHERE COMPANIA  = UN_COMPANIA
                  AND ANO       = RS.ANO_CPTE
                  AND CODIGO    = RS_DET.CUENTA
                ORDER BY CODIGO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_CUENTAPPTAL:='';
              END;
            END IF;
            --lvega SE AGREGAN CAMPOS DE EQUIVALENTE SIGEC, NUMERO Y TIPO CONTRATO SIGEC A LA DETALLE
              MI_CAMPOS  :=  'COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CONSECUTIVO, CUENTA,NATURALEZA,VALOR_DEBITO,
                         VALOR_CREDITO,FECHA,HORA,EJECUCION_DEBITO,EJECUCION_CREDITO,DESCRIPCION,CUENTAPPTAL,TERCERO,
                         SUCURSAL,CENTRO_COSTO,AUXILIAR, DATE_CREATED, CREATED_BY,ANO_AFECT, TIPO_CPTE_AFECT,CMPTE_AFECTADO,
                         CONSECUTIVOAFECTADO,TIPO_NOTA,CODIGO_NOTA,FUENTE_RECURSO,EQUIV_SIGEC,NROCONTRATOSIGEC,TIPOCONTRATOSIGEC,VALOR_IVA,VALOR_RETEIVA,VALOR_RETEFUENTE,VALOR_ICA';
              MI_VALORES :=  ''''||UN_COMPANIA||''',
                             '   ||EXTRACT( YEAR FROM SYSDATE)||',
                             ''' ||UN_TIPOANULACION||''',
                             '   ||MI_COMPROBANTE||',
                             '   ||MI_CONSECUTIVO||',
                             ''' || RS_DET.CUENTA||''',
                             ''' ||RS_DET.NATURALEZA||''',
                             '   ||CASE MI_AUX
                                        WHEN 'C' THEN RS_DET.VALOR_CREDITO-RS_DET.CREDITO_AFECTADO
                                        WHEN 'D' THEN 0 END||',
                             '   ||CASE MI_AUX
                                        WHEN 'C' THEN 0
                                        WHEN 'D' THEN RS_DET.VALOR_DEBITO-RS_DET.DEBITO_AFECTADO END||',
                             SYSDATE,
                             SYSDATE,
                             '   ||CASE MI_AUX
                                        WHEN 'C' THEN RS_DET.VALOR_CREDITO-RS_DET.CREDITO_AFECTADO
                                        WHEN 'D' THEN 0 END||',
                             '   ||CASE MI_AUX
                                        WHEN 'C' THEN 0
                                        WHEN 'D' THEN RS_DET.VALOR_DEBITO-RS_DET.DEBITO_AFECTADO END||',
                             ''' ||MI_DESCRIPCION||''',
                             ''' ||MI_CUENTAPPTAL||''',
                             ''' ||RS_DET.TERCERO||''',
                             ''' ||RS_DET.SUCURSAL||''',
                             ''' ||RS_DET.CENTRO_COSTO||''',
                             ''' ||RS_DET.AUXILIAR||''',
                             SYSDATE,
                             ''' ||UN_USUARIO||''',
                             '   ||RS.ANO_CPTE||',
                             ''' ||RS.TIPO_CPTE||''',
                             '   ||RS.NRO_CPTE||',
                             '   ||RS_DET.CONSECUTIVO||',
                             ''' ||MI_TIPONOTA||''',
                             ''' ||MI_CODIGONOTA||''',
                             ''' ||RS_DET.FUENTE_RECURSO||''',
                             ''' ||RS_DET.EQUIV_SIGEC||''',
                             ''' ||RS_DET.NROCONTRATOSIGEC||''',
                             ''' ||RS_DET.TIPOCONTRATOSIGEC||''',
                             ''' ||RS_DET.VALOR_IVA||''',
                             ''' ||RS_DET.VALOR_RETEIVA||''',
                             ''' ||RS_DET.VALOR_RETEFUENTE||''',
                             ''' ||RS_DET.VALOR_ICA||''' ';
              BEGIN
                BEGIN
                     MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'DETALLE_COMPROBANTE_CNT',
                                                  UN_ACCION   => 'I',
                                                  UN_CAMPOS   => MI_CAMPOS,
                                                  UN_VALORES  => MI_VALORES);

                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                END; 
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    =>  SQLCODE,
                    UN_ERROR_COD  =>  PCK_ERRORES.ERR_SYSMANSF_INSDETCOMPFACTURA,
                    UN_REEMPLAZOS =>  MI_MSGERROR
                  );
              END;  


            IF MI_RTA > 0 THEN
              MI_CONSECUTIVO:= MI_CONSECUTIVO+1;

               SELECT CLASE_CONTABLE 
               INTO MI_CLASECONTABLE
               FROM   TIPO_COMPROBANTE 
               WHERE  COMPANIA = UN_COMPANIA
                 AND  CODIGO = UN_TIPOANULACION;

              IF MI_CLASECONTABLE = 'E' OR MI_CLASECONTABLE = 'R' OR MI_CLASECONTABLE = 'G' THEN
                  MI_CAMPOS  :=' CREDITOSAFECTADOS = CREDITOSAFECTADOS + '||CASE WHEN RS_DET.VALOR_DEBITO > 0 THEN RS_DET.VALOR_DEBITO ELSE 0 END ||',
                               MODIFIED_BY   = '''||UN_USUARIO||''',
                               DATE_MODIFIED = SYSDATE';
                  MI_CONDICION:=' COMPANIA    = ''' ||UN_COMPANIA ||''' 
                                  AND  ANO    = ' ||RS.ANO_CPTE||'
                                  AND  TIPO   = ''' ||RS.TIPO_CPTE||'''  
                                  AND  NUMERO = '   || RS.NRO_CPTE;  
                  BEGIN
                    BEGIN      
                        PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME( UN_ACCION    => 'M',
                                                             UN_TABLA     => 'COMPROBANTE_CNT',
                                                             UN_CAMPOS    => MI_CAMPOS,
                                                             UN_CONDICION => MI_CONDICION);
                         EXCEPTION  WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                              RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                    END;                             
                               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                                      MI_MSGERROR (2).CLAVE := 'ELEMENTO';
                                      MI_MSGERROR (2).VALOR := 'CREDITOSAFECTADOS';
                                     PCK_ERR_MSG.RAISE_WITH_MSG(
                                       UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTCOMPFACTURA,
                                       UN_REEMPLAZOS =>  MI_MSGERROR
                                      );
                  END;     
              END IF;

              IF MI_CLASECONTABLE = 'S' OR MI_CLASECONTABLE = 'I' OR MI_CLASECONTABLE = 'N' THEN

                   MI_CAMPOS  :=  CASE MI_AUX WHEN 'D' THEN ' DEBITOSAFECTADOS = DEBITOSAFECTADOS + '||(RS_DET.VALOR_DEBITO - RS_DET.DEBITO_AFECTADO)
                                              WHEN 'C' THEN ' CREDITOSAFECTADOS  = CREDITOSAFECTADOS  + '||(RS_DET.VALOR_CREDITO - RS_DET.CREDITO_AFECTADO) END ||',
                               MODIFIED_BY   = '''||UN_USUARIO||''',
                               DATE_MODIFIED = SYSDATE';
                   MI_CONDICION:=' COMPANIA    = ''' ||UN_COMPANIA ||''' 
                                  AND  ANO    = ' ||RS.ANO_CPTE||'
                                  AND  TIPO   = ''' ||RS.TIPO_CPTE||'''  
                                  AND  NUMERO = '   || RS.NRO_CPTE; 

                   MI_MSGERROR (2).CLAVE := 'ELEMENTO';
                   MI_MSGERROR (2).VALOR := CASE MI_AUX WHEN 'C' THEN 'CREDITOSAFECTADOS' WHEN 'D' THEN 'DEBITOSAFECTADOS' END;
                  BEGIN
                    BEGIN      
                        PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME( UN_ACCION    => 'M',
                                                             UN_TABLA     => 'COMPROBANTE_CNT',
                                                             UN_CAMPOS    => MI_CAMPOS,
                                                             UN_CONDICION => MI_CONDICION);
                         EXCEPTION  WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                              RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                    END;                             
                               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                                     PCK_ERR_MSG.RAISE_WITH_MSG(
                                       UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTCOMPFACTURA,
                                       UN_REEMPLAZOS =>  MI_MSGERROR
                                      );
                  END;  
                   MI_CAMPOS  :=  CASE MI_AUX WHEN 'D' THEN ' DEBITO_AFECTADO = DEBITO_AFECTADO + '||(RS_DET.VALOR_DEBITO - RS_DET.DEBITO_AFECTADO)
                                              WHEN 'C' THEN ' CREDITO_AFECTADO  = CREDITO_AFECTADO  + '||(RS_DET.VALOR_CREDITO - RS_DET.CREDITO_AFECTADO) END ||',
                               MODIFIED_BY   = '''||UN_USUARIO||''',
                               DATE_MODIFIED = SYSDATE';
                   MI_CONDICION:=' COMPANIA        = ''' || UN_COMPANIA       ||''' 
                                  AND  ANO         = '   || RS.ANO_CPTE       ||'
                                  AND  TIPO_CPTE   = ''' || RS.TIPO_CPTE      ||'''  
                                  AND  COMPROBANTE = '   || RS.NRO_CPTE       ||'
                                  AND  CONSECUTIVO = '   || RS_DET.CONSECUTIVO;  
                  BEGIN
                    BEGIN      
                        PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME( UN_ACCION    => 'M',
                                                             UN_TABLA     => 'DETALLE_COMPROBANTE_CNT',
                                                             UN_CAMPOS    => MI_CAMPOS,
                                                             UN_CONDICION => MI_CONDICION);
                         EXCEPTION  WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                              RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                    END;                             
                               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                               MI_MSGERROR (2).VALOR := CASE MI_AUX WHEN 'C' THEN 'CREDITO_AFECTADO' WHEN 'D' THEN 'DEBITO_AFECTADO' END;
                                     PCK_ERR_MSG.RAISE_WITH_MSG(
                                       UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTDETCOMPFACTURA,
                                       UN_REEMPLAZOS =>  MI_MSGERROR
                                      );
                  END;      

              END IF;

            ELSE
              MI_CONDICION :=  'COMPANIA        = '''|| UN_COMPANIA ||''' 
                                AND ANO         = '|| EXTRACT (YEAR FROM SYSDATE) ||' 
                                AND TIPO_CPTE   = '''|| UN_TIPOANULACION ||'''   
                                AND COMPROBANTE = '|| MI_COMPROBANTE ||'';
              BEGIN
                BEGIN
                  MI_AUX:=PCK_DATOS.FC_ACME(UN_TABLA      =>  'DETALLE_COMPROBANTE_CNT',
                                            UN_ACCION     =>  'E',
                                            UN_CONDICION  =>  MI_CONDICION );
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                                 RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                END;
                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN    
                                  PCK_ERR_MSG.RAISE_WITH_MSG(
                                  UN_EXC_COD   => SQLCODE,
                                  UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_DELDETCOMPFACTURA
                                );
              END;

              MI_CONDICION:=' COMPANIA    = ''' || UN_COMPANIA                ||''' 
                              AND  ANO    = '   || EXTRACT (YEAR FROM SYSDATE)||'
                              AND  TIPO   = ''' || UN_TIPOANULACION           ||'''  
                              AND  NUMERO = '   || MI_COMPROBANTE;   
              BEGIN
                BEGIN
                  MI_AUX:=PCK_DATOS.FC_ACME(UN_TABLA      =>  'COMPROBANTE_CNT',
                                            UN_ACCION     =>  'E',
                                            UN_CONDICION  =>  MI_CONDICION );
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                                 RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                END;
                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN    
                                  PCK_ERR_MSG.RAISE_WITH_MSG(
                                  UN_EXC_COD   => SQLCODE,
                                  UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_DELCOMPFACTURA
                                );
              END;
            END IF;  
          END LOOP DETALLES;

           MI_CAMPOS:='ANULADO=-1,
                           DESCRIPCION = ''ANULADO - '' || DESCRIPCION,
                           MODIFIED_BY   = '''||UN_USUARIO||''',
                           DATE_MODIFIED = SYSDATE';
           MI_CONDICION:=' COMPANIA    = ''' ||UN_COMPANIA ||''' 
                          AND  ANO    = ' ||RS.ANO_CPTE||'
                          AND  TIPO   = ''' ||RS.TIPO_CPTE||'''  
                          AND  NUMERO = '   || RS.NRO_CPTE;  
          BEGIN
            BEGIN      
                PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME( UN_ACCION    => 'M',
                                                     UN_TABLA     => 'COMPROBANTE_CNT',
                                                     UN_CAMPOS    => MI_CAMPOS,
                                                     UN_CONDICION => MI_CONDICION);
                 EXCEPTION  WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
            END;                             
                       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                              MI_MSGERROR (2).CLAVE := 'COMPROBANTE';
                              MI_MSGERROR (2).VALOR := RS.NRO_CPTE;
                             PCK_ERR_MSG.RAISE_WITH_MSG(
                               UN_EXC_COD    => SQLCODE,
                               UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ANULARCOMPFACTURA,
                               UN_REEMPLAZOS =>  MI_MSGERROR
                              );
          END;   

           MI_CAMPOS:='ANULADA =-1,
                           FECHA_ANULACION = SYSDATE,
                           CPTE_ANULACION = '''||UN_TIPOANULACION||''',
                           NRO_ANULACION  = '||MI_COMPROBANTE||',
                           MODIFIED_BY   = '''||UN_USUARIO||''',
                           DATE_MODIFIED = SYSDATE';
           MI_CONDICION:=' COMPANIA    = ''' ||UN_COMPANIA ||''' 
                          AND  TIPO_FACTURA    = ''' ||UN_TIPOCOBRO||'''
                          AND  NUMERO_FACTURA   = '||UN_FACTURA||'';  
          BEGIN
            BEGIN      
                PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME( UN_ACCION    => 'M',
                                                     UN_TABLA     => 'SF_FACTURA',
                                                     UN_CAMPOS    => MI_CAMPOS,
                                                     UN_CONDICION => MI_CONDICION);
                 EXCEPTION  WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
            END;                             
                       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
                             PCK_ERR_MSG.RAISE_WITH_MSG(
                               UN_EXC_COD    => SQLCODE,
                               UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ANULARFACTURA,
                               UN_REEMPLAZOS =>  MI_MSGERROR
                              );
          END;
        ELSE
           IF UN_SERIE = 1 THEN
                 PCK_FACT_GENERAL_COM1.PR_REGISTRARPROCESO( UN_COMPANIA     => UN_COMPANIA,
                                                            UN_CLASE        => 'ANFRA' ,
                                                            UN_TIPOCOBRO    => UN_TIPOCOBRO,
                                                            UN_COBRO        => UN_FACTURA,
                                                            UN_DESCRIPCION  => 'Se presentaron inconvientes durante la creación del comprobante. Comprobante a afectar '|| 
                                                                                RS.TIPO_CPTE ||' - '|| 
                                                                                RS.NRO_CPTE, 
                                                            UN_USUARIO      => UN_USUARIO);
              END IF;
               BEGIN
                   RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
                    MI_MSGERROR (2).CLAVE := 'COMPROBANTE';
                    MI_MSGERROR (2).VALOR := RS.TIPO_CPTE;
                    MI_MSGERROR (3).CLAVE := 'NUMERO';
                    MI_MSGERROR (3).VALOR := RS.NRO_CPTE;
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD =>SQLCODE,
                      UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_CREARCOMPROBANTE,
                      UN_REEMPLAZOS =>  MI_MSGERROR
                    );
                 END;


        END IF;
        MI_EXISTE:=1;
        END IF;
       END LOOP SFFACTURA;

       IF MI_EXISTE = 0 THEN
          IF UN_SERIE = 1 THEN
            PCK_FACT_GENERAL_COM1.PR_REGISTRARPROCESO(  UN_COMPANIA     => UN_COMPANIA,
                                                        UN_CLASE        => 'ANFRA' ,
                                                        UN_TIPOCOBRO    => UN_TIPOCOBRO,
                                                        UN_COBRO        => UN_FACTURA,
                                                        UN_DESCRIPCION  => 'La factura '||UN_FACTURA||' no se encuentra registrada en el sistema para realizar el proceso de anulación' , 
                                                        UN_USUARIO      => UN_USUARIO);
            RETURN;
          END IF;
           BEGIN
             RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD =>SQLCODE,
                UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_FACTREGISTRADA
              );
           END;
       END IF;   

END PR_ANULARFACTURACION;


--12
FUNCTION FC_ENUMERAR_FACTURA
/*
    NAME              : FC_MAN_DER_CONN_CONCEP   ACCES => ManejaDerConexionConcepto
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : JONATHAN ENRIQUE GUERRERO TORRES
    DATE MIGRADOR     : 04/12/2017
    TIME              : 09:59 PM
    DESCRIPTION       : FUNCION QUE SE ENCARGA DE GENERAR EL CONSECUTIVO DE LA FACTURA
    PARAMETROS        : UN_COMPANIA   => COMPANIA DE INGRESO A LA COMPANIA 
                        UN_ANIO_COBRO => ANO DE FACTURACION 
                        UN_TIPOCOBRO  => TIPO DE COBRO A FACTURAR
                        UN_CONCEPTO   => CONCEPTO A EVAUALR SI TIENE CONEXION

  */
(
  UN_COMPANIA          PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOCOBRO         VARCHAR2,
  UN_ANIO_COBRO        PCK_SUBTIPOS.TI_ANIO
)
RETURN PCK_SUBTIPOS.TI_ENTERO_LARGO
AS
MI_CONSECUTIVO_FACTURACION     PCK_SUBTIPOS.TI_ENTERO_LARGO;
MI_PARAMETRO                   PCK_SUBTIPOS.TI_PARAMETRO;
MI_ENUMERAR_FACTURA            PCK_SUBTIPOS.TI_ENTERO_LARGO;  
MI_NUMERO_FACTURA              PCK_SUBTIPOS.TI_ENTERO_LARGO;
MI_CONSECUTIVO                 PCK_SUBTIPOS.TI_ENTERO_LARGO;
MI_MANEJA_RESFACTURACION       PCK_SUBTIPOS.TI_LOGICO;
MI_INICIAEN                    PCK_SUBTIPOS.TI_ENTERO_LARGO;
MI_NOFINALFACTURACION          NUMBER(20,0);     
MI_NOINICIALFACTIRACION        NUMBER(20,0); 
MI_CONREVERSO                  NUMBER(20,0);  

BEGIN
   BEGIN 
      SELECT CONSECUTIVO_FACTURACION , MANEJA_RESFACTURACION
        INTO MI_CONSECUTIVO_FACTURACION , MI_MANEJA_RESFACTURACION
        FROM SF_TIPO_COBRO
       WHERE COMPANIA   = UN_COMPANIA
         AND ANO        = UN_ANIO_COBRO
         AND CODIGO     = UN_TIPOCOBRO;

    EXCEPTION WHEN NO_DATA_FOUND THEN 
      MI_CONSECUTIVO_FACTURACION := 0;
      MI_MANEJA_RESFACTURACION := 0;
    END ;



      MI_PARAMETRO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                            UN_NOMBRE    => 'SF TOMAR CONSECUTIVO PARAMETRIZADO EN EL TIPO DE COBRO',
                                            UN_MODULO    => PCK_DATOS.FC_MODULOFACTURACIONGENERAL,
                                            UN_FECHA_PAR => SYSDATE,
                                            UN_IND_MAYUS => 0);

      IF NVL(MI_PARAMETRO,'NO') = 'SI' THEN 
        IF MI_MANEJA_RESFACTURACION IN (0)THEN  
           BEGIN 
               SELECT MAX (NUMERO_FACTURA) CONSECUTIVO
                 INTO MI_CONSECUTIVO
                 FROM SF_FACTURA
                WHERE SF_FACTURA.COMPANIA      = UN_COMPANIA
                  AND SF_FACTURA.TIPO_FACTURA  = UN_TIPOCOBRO
                  AND SF_FACTURA.ANO           = UN_ANIO_COBRO
                ORDER BY NUMERO_FACTURA DESC ;

          EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_ENUMERAR_FACTURA := 0;
          END ;    

          IF MI_CONSECUTIVO NOT IN (0) THEN 
              MI_ENUMERAR_FACTURA := MI_CONSECUTIVO +1;
          END IF;   

         END IF; 
      END IF;

      BEGIN 
          SELECT MAX(NUMERO_FACTURA) CONSECUTIVO
            INTO MI_CONSECUTIVO
            FROM SF_FACTURA
           WHERE COMPANIA        = UN_COMPANIA
             AND TIPO_FACTURA    = UN_TIPOCOBRO
             AND ANO             = UN_ANIO_COBRO;
      EXCEPTION WHEN NO_DATA_FOUND THEN 
          MI_CONSECUTIVO := 0;
      END;

      IF MI_CONSECUTIVO NOT IN (0) THEN 
          MI_ENUMERAR_FACTURA := MI_CONSECUTIVO+1;
      ELSE 
          MI_ENUMERAR_FACTURA := TO_NUMBER(UN_ANIO_COBRO||'000001');
      END IF;


  --VERIFICA SI MANEJA RESOLUCION
  BEGIN
      SELECT MANEJA_RESFACTURACION,
             INICIAEN,
             nofinal_facturacion,
             noinicial_facturacion 
        INTO MI_MANEJA_RESFACTURACION,
             MI_INICIAEN   ,
             MI_NOFINALFACTURACION,
             MI_NOINICIALFACTIRACION             
        FROM SF_TIPO_COBRO
       WHERE COMPANIA               = UN_COMPANIA
         AND ANO                    = UN_ANIO_COBRO
         AND CODIGO                 = UN_TIPOCOBRO
         AND MANEJA_RESFACTURACION IN(-1);
  EXCEPTION WHEN NO_DATA_FOUND THEN 
      MI_MANEJA_RESFACTURACION := NULL;
      MI_INICIAEN              := NULL; 
  END;

  IF MI_MANEJA_RESFACTURACION IS NOT NULL  THEN 
     IF MI_MANEJA_RESFACTURACION NOT IN (0) THEN 
          BEGIN 
              SELECT NUMERO_FACTURA
                INTO MI_NUMERO_FACTURA
                FROM SF_FACTURA
               WHERE COMPANIA   = UN_COMPANIA
                 AND TIPO_FACTURA = UN_TIPOCOBRO
                 AND EXTRACT(YEAR FROM FECHA_EXPEDICION) = UN_ANIO_COBRO
                 AND NUMERO_FACTURA = NVL(MI_INICIAEN,0)
               ORDER BY NUMERO_FACTURA DESC ;
          EXCEPTION WHEN NO_DATA_FOUND THEN 
              MI_NUMERO_FACTURA := NULL;
          END;

          IF MI_NUMERO_FACTURA IS NOT NULL THEN 
              MI_ENUMERAR_FACTURA := MI_NUMERO_FACTURA+1;
              
              --GoTo VerificaRango
                 BEGIN            
                    SELECT  MAX(NUMERO_FACTURA)  FAC 
                    INTO MI_NUMERO_FACTURA
                    FROM   SF_FACTURA
                      WHERE COMPANIA = UN_COMPANIA
                        AND TIPO_FACTURA = UN_TIPOCOBRO
                        AND NUMERO_FACTURA Between NVL(MI_NOINICIALFACTIRACION,0) AND NVL(MI_NOFINALFACTURACION,0);
                        
                     EXCEPTION WHEN NO_DATA_FOUND THEN 
                      MI_NUMERO_FACTURA := NULL;
            
                    END ;
                    
                    IF MI_NUMERO_FACTURA IS NOT NULL THEN
                      MI_ENUMERAR_FACTURA := MI_NUMERO_FACTURA+1;
                    END IF;
                    
          ELSE 
              MI_ENUMERAR_FACTURA := MI_INICIAEN;
              --GoTo continuar
                
                BEGIN                     
                    SELECT   REVERSO_N_DOCUMENTO  
                    INTO MI_CONREVERSO 
                    FROM         SF_OBJETO_COBRO
                    WHERE        COMPANIA = UN_COMPANIA
                      AND         REVERSO_TIPO_COMPROBANTE = UN_TIPOCOBRO
                      ORDER BY    REVERSO_N_DOCUMENTO DESC
                      FETCH FIRST 1 ROW ONLY; --MOD JM CC 3015
                               
                      EXCEPTION WHEN NO_DATA_FOUND THEN 
                        MI_CONREVERSO := 0;
            
                END ;                     
            
               IF MI_ENUMERAR_FACTURA < MI_CONREVERSO THEN
                   MI_ENUMERAR_FACTURA := MI_CONREVERSO + 1;
                   
               ELSIF MI_ENUMERAR_FACTURA >= MI_CONREVERSO THEN
                     MI_ENUMERAR_FACTURA := MI_ENUMERAR_FACTURA;
                ELSIF MI_ENUMERAR_FACTURA < MI_CONREVERSO THEN
                      MI_ENUMERAR_FACTURA := MI_CONREVERSO;
                END IF     ;
          END IF; 
    
     END IF;

  END IF;

  RETURN MI_ENUMERAR_FACTURA;
END FC_ENUMERAR_FACTURA;


PROCEDURE PR_PREPARA_CONF_ANIO_SIG (
    /*
    NAME              : PR_PREPARA_CONF_ANIO_SIG
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
    DATE MIGRADOR     : 02/03/2020
    TIME              : 03:37 PM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Copia la configuracion de facturacion general de una vigencia a otra    

    @NAME: prepararConfiguracionAnioSigFactG
  */
  UN_COMPANIA	      IN VARCHAR2,
  UN_ANO_DESTINO 	  IN INTEGER,
  UN_ANO_ORIGEN       IN INTEGER,
  UN_COMPANIA_DESTINO IN VARCHAR2  
)
AS 
  MI_CAMPOS        VARCHAR2(32000);
  MI_CONDICION     VARCHAR2(32000);
  MI_EXCLUIDOS     VARCHAR2(32000);
  MI_VALORES       VARCHAR2(32000);
  MI_TABLA         PCK_SUBTIPOS.TI_TABLA; 
  MI_ANIO          NUMBER;
  MI_REEMPLAZOS    PCK_SUBTIPOS.TI_CLAVEVALOR; 
  MI_SALARIOMINIMO NUMBER(20,2);
  MI_VALORUVT      NUMBER(20,2); 
  MI_CONTADOR      NUMBER(20,2);  
  MI_SALARIODIARIO NUMBER(20,2);  
  MI_FORMULA       VARCHAR2(100 CHAR);
  MI_VALOR         NUMBER(20,2);
---7709782 (25/02/2022 mrosero)
  MI_INICIO_VIG          PCK_SUBTIPOS.TI_PARAMETRO;
BEGIN

  MI_INICIO_VIG := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                             UN_NOMBRE    => 'SF VALIDAR PRESUPUESTO EN INICIO DE VIGENCIA',
                                             UN_MODULO    => 999,
                                             UN_FECHA_PAR => SYSDATE,
                                             UN_IND_MAYUS => 0);
--FIN_7709782 (25/02/2022 mrosero)

  --Validar existencia plan contable  
    SELECT COUNT(*)
    INTO MI_CONTADOR
    FROM PLAN_CONTABLE
    WHERE COMPANIA = UN_COMPANIA_DESTINO
      AND ANO = UN_ANO_DESTINO;

      IF MI_CONTADOR IN (0) THEN
       BEGIN
             RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
               MI_REEMPLAZOS (2).CLAVE := 'ANIO';
               MI_REEMPLAZOS (2).VALOR := UN_ANO_DESTINO;             
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD =>SQLCODE,
                UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_NOPLANCONTABLE,
                UN_REEMPLAZOS =>  MI_REEMPLAZOS
              );
        END;     
      END IF;

--INI_7709782 (25/02/2022 mrosero)

  --Validar existencia plan presupuestal  
IF MI_INICIO_VIG = 'SI' THEN
    SELECT COUNT(*)
    INTO MI_CONTADOR
    FROM PLAN_PRESUPUESTAL
    WHERE COMPANIA = UN_COMPANIA_DESTINO
      AND ANO = UN_ANO_DESTINO;

      IF MI_CONTADOR IN (0) THEN
       BEGIN
             RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
               MI_REEMPLAZOS (2).CLAVE := 'ANIO';
               MI_REEMPLAZOS (2).VALOR := UN_ANO_DESTINO;             
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD =>SQLCODE,
                UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_NOPLANPPTAL,
                UN_REEMPLAZOS =>  MI_REEMPLAZOS
              );
        END;     
      END IF;
    END IF;
--FIN_7709782 (25/02/2022 mrosero)

  --Validar Salario Minimo y Valor UVT
    SELECT ANO.SALARIOMINIMO,ANO.VALORUVT  
      INTO MI_SALARIOMINIMO,MI_VALORUVT   
    FROM ANO  
    WHERE ANO.COMPANIA = UN_COMPANIA_DESTINO
      AND ANO.NUMERO = UN_ANO_DESTINO
    ORDER BY ANO.NUMERO DESC ;

    IF MI_SALARIOMINIMO IN (0) OR MI_VALORUVT IN (0) THEN
        BEGIN
             RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
               MI_REEMPLAZOS (2).CLAVE := 'ANIO';
               MI_REEMPLAZOS (2).VALOR := UN_ANO_DESTINO;             
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD =>SQLCODE,
                UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_ANIOSALARIOUVT,
                UN_REEMPLAZOS =>  MI_REEMPLAZOS
              );
        END;

    END IF;
    
    --Copiar información tabla SF_TIPO_COBRO
  MI_EXCLUIDOS:='COMPANIA,ANO';
      MI_CAMPOS:= PCK_SYSMAN_UTL.FC_LISTA_CAMPOS('SF_TIPO_COBRO',MI_EXCLUIDOS);
      MI_VALORES :=' SELECT '||MI_CAMPOS||','''||UN_COMPANIA_DESTINO||''','||UN_ANO_DESTINO||'
                     FROM SF_TIPO_COBRO ORI
                     WHERE ORI.COMPANIA = '''||UN_COMPANIA||''' 
                       AND ORI.ANO      = '  ||UN_ANO_ORIGEN||'
                       AND ORI.CODIGO NOT IN(SELECT CODIGO 
                                              FROM   SF_TIPO_COBRO DES 
                                              WHERE  DES.COMPANIA = '''|| UN_COMPANIA_DESTINO ||'''
                                                AND  DES.ANO      = '  || UN_ANO_DESTINO ||')'; 
      MI_CAMPOS:= MI_CAMPOS || ',' || MI_EXCLUIDOS;
      
    BEGIN
          BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   =>'SF_TIPO_COBRO', 
                                                    UN_ACCION  => 'IS', 
                                                    UN_CAMPOS  => MI_CAMPOS, 
                                                    UN_VALORES => MI_VALORES); 
                                                    
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
           END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
          MI_REEMPLAZOS(1).CLAVE := 'ANIO';
          MI_REEMPLAZOS(1).VALOR := UN_ANO_DESTINO;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSTIPOCOBRO,
            UN_REEMPLAZOS  => MI_REEMPLAZOS
          ); 
     END; 

    --Copiar información tabla SF_CONCEPTOS
  MI_EXCLUIDOS:='COMPANIA,ANO';
      MI_CAMPOS:= PCK_SYSMAN_UTL.FC_LISTA_CAMPOS('SF_CONCEPTOS',MI_EXCLUIDOS);
      MI_VALORES :=' SELECT '||MI_CAMPOS||','''||UN_COMPANIA_DESTINO||''','||UN_ANO_DESTINO||'
                     FROM SF_CONCEPTOS ORI
                     WHERE ORI.COMPANIA = '''||UN_COMPANIA||''' 
                       AND ORI.ANO      = '  ||UN_ANO_ORIGEN||'
                       AND ORI.CODIGO NOT IN(SELECT CODIGO 
                                              FROM   SF_CONCEPTOS DES 
                                              WHERE  DES.COMPANIA = '''|| UN_COMPANIA_DESTINO ||'''
                                                AND  DES.ANO      = '  || UN_ANO_DESTINO ||')'; 
      MI_CAMPOS:= MI_CAMPOS || ',' || MI_EXCLUIDOS;
      
    BEGIN
          BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   =>'SF_CONCEPTOS', 
                                                    UN_ACCION  => 'IS', 
                                                    UN_CAMPOS  => MI_CAMPOS, 
                                                    UN_VALORES => MI_VALORES); 
                                                    
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
           END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
          MI_REEMPLAZOS(1).CLAVE := 'ANIO';
          MI_REEMPLAZOS(1).VALOR := UN_ANO_DESTINO;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSCONCECOBRO,
            UN_REEMPLAZOS  => MI_REEMPLAZOS
          ); 
     END; 
     
    --Copiar información tabla SF_ESTRATOS     
    
  MI_EXCLUIDOS:='COMPANIA,ANO';
      MI_CAMPOS:= PCK_SYSMAN_UTL.FC_LISTA_CAMPOS('SF_ESTRATO',MI_EXCLUIDOS);
      MI_VALORES :=' SELECT '||MI_CAMPOS||','''||UN_COMPANIA_DESTINO||''','||UN_ANO_DESTINO||'
                     FROM SF_ESTRATO ORI
                     WHERE ORI.COMPANIA = '''||UN_COMPANIA||''' 
                       AND ORI.ANO      = '  ||UN_ANO_ORIGEN||'
                       AND ORI.CODIGO||TIPOCOBRO NOT IN(SELECT CODIGO||TIPOCOBRO 
                                              FROM   SF_ESTRATO DES 
                                              WHERE  DES.COMPANIA = '''|| UN_COMPANIA_DESTINO ||'''
                                                AND  DES.ANO      = '  || UN_ANO_DESTINO ||')';
                                                
      MI_CAMPOS:= MI_CAMPOS || ',' || MI_EXCLUIDOS;
      
    BEGIN
          BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   =>'SF_ESTRATO', 
                                                    UN_ACCION  => 'IS', 
                                                    UN_CAMPOS  => MI_CAMPOS, 
                                                    UN_VALORES => MI_VALORES); 
                                                    
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
           END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
          MI_REEMPLAZOS(1).CLAVE := 'ANIO';
          MI_REEMPLAZOS(1).VALOR := UN_ANO_DESTINO;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSESTRATOS,
            UN_REEMPLAZOS  => MI_REEMPLAZOS
          ); 
     END;    
     
    --Copiar información tabla SF_TARIFA     
    
  MI_EXCLUIDOS:='COMPANIA,ANO';
      MI_CAMPOS:= PCK_SYSMAN_UTL.FC_LISTA_CAMPOS('SF_TARIFA',MI_EXCLUIDOS);
      MI_VALORES :=' SELECT '||MI_CAMPOS||','''||UN_COMPANIA_DESTINO||''','||UN_ANO_DESTINO||'
                     FROM SF_TARIFA ORI
                     WHERE ORI.COMPANIA = '''||UN_COMPANIA||''' 
                       AND ORI.ANO      = '  ||UN_ANO_ORIGEN||'
                       AND ORI.CODIGO||TIPOCOBRO NOT IN(SELECT CODIGO||TIPOCOBRO 
                                              FROM   SF_TARIFA DES 
                                              WHERE  DES.COMPANIA = '''|| UN_COMPANIA_DESTINO ||'''
                                                AND  DES.ANO      = '  || UN_ANO_DESTINO ||')';
                                                
      MI_CAMPOS:= MI_CAMPOS || ',' || MI_EXCLUIDOS;
      
    BEGIN
          BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   =>'SF_TARIFA', 
                                                    UN_ACCION  => 'IS', 
                                                    UN_CAMPOS  => MI_CAMPOS, 
                                                    UN_VALORES => MI_VALORES); 
                                                    
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
           END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
          MI_REEMPLAZOS(1).CLAVE := 'ANIO';
          MI_REEMPLAZOS(1).VALOR := UN_ANO_DESTINO;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSTARIFA,
            UN_REEMPLAZOS  => MI_REEMPLAZOS
          ); 
     END;           

    --Copiar información tabla SF_TARIFAS_BASE     
    
  MI_EXCLUIDOS:='COMPANIA,ANO';
      MI_CAMPOS:= PCK_SYSMAN_UTL.FC_LISTA_CAMPOS('SF_TARIFAS_BASE',MI_EXCLUIDOS);
      MI_VALORES :=' SELECT '||MI_CAMPOS||','''||UN_COMPANIA_DESTINO||''','||UN_ANO_DESTINO||'
                     FROM SF_TARIFAS_BASE ORI
                     WHERE ORI.COMPANIA = '''||UN_COMPANIA||''' 
                       AND ORI.ANO      = '  ||UN_ANO_ORIGEN||'
                       AND ORI.CODIGO||ORI.TIPO_COBRO NOT IN(SELECT CODIGO||TIPO_COBRO 
                                              FROM   SF_TARIFAS_BASE DES 
                                              WHERE  DES.COMPANIA = '''|| UN_COMPANIA_DESTINO ||'''
                                                AND  DES.ANO      = '  || UN_ANO_DESTINO ||')';
                                                
      MI_CAMPOS:= MI_CAMPOS || ',' || MI_EXCLUIDOS;
      
    BEGIN
          BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   =>'SF_TARIFAS_BASE', 
                                                    UN_ACCION  => 'IS', 
                                                    UN_CAMPOS  => MI_CAMPOS, 
                                                    UN_VALORES => MI_VALORES); 
                                                    
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
           END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
          MI_REEMPLAZOS(1).CLAVE := 'ANIO';
          MI_REEMPLAZOS(1).VALOR := UN_ANO_DESTINO;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSTARIFABASE,
            UN_REEMPLAZOS  => MI_REEMPLAZOS
          ); 
     END;
     
    --Copiar información tabla SF_DESCRIPCIONES_ESTAND     
    
  MI_EXCLUIDOS:='COMPANIA,ANO';
      MI_CAMPOS:= PCK_SYSMAN_UTL.FC_LISTA_CAMPOS('SF_DESCRIPCIONES_ESTAND',MI_EXCLUIDOS);
      MI_VALORES :=' SELECT '||MI_CAMPOS||','''||UN_COMPANIA_DESTINO||''','||UN_ANO_DESTINO||'
                     FROM SF_DESCRIPCIONES_ESTAND ORI
                     WHERE ORI.COMPANIA = '''||UN_COMPANIA||''' 
                       AND ORI.ANO      = '  ||UN_ANO_ORIGEN||'
                       AND ORI.CODIGO||ORI.TIPO_COBRO NOT IN(SELECT CODIGO||TIPO_COBRO 
                                              FROM   SF_DESCRIPCIONES_ESTAND DES 
                                              WHERE  DES.COMPANIA = '''|| UN_COMPANIA_DESTINO ||'''
                                                AND  DES.ANO      = '  || UN_ANO_DESTINO ||')';
                                                
      MI_CAMPOS:= MI_CAMPOS || ',' || MI_EXCLUIDOS;
      
    BEGIN
          BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   =>'SF_DESCRIPCIONES_ESTAND', 
                                                    UN_ACCION  => 'IS', 
                                                    UN_CAMPOS  => MI_CAMPOS, 
                                                    UN_VALORES => MI_VALORES); 
                                                    
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
           END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
          MI_REEMPLAZOS(1).CLAVE := 'ANIO';
          MI_REEMPLAZOS(1).VALOR := UN_ANO_DESTINO;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSDESCESTAND,
            UN_REEMPLAZOS  => MI_REEMPLAZOS
          ); 
     END;     
     
    --Copiar información tabla SF_CONCEPTOS_DEPENDIENTES     
    
  MI_EXCLUIDOS:='COMPANIA,ANO';
      MI_CAMPOS:= PCK_SYSMAN_UTL.FC_LISTA_CAMPOS('SF_CONCEPTOS_DEPENDIENTES',MI_EXCLUIDOS);
      MI_VALORES :=' SELECT '||MI_CAMPOS||','''||UN_COMPANIA_DESTINO||''','||UN_ANO_DESTINO||'
                     FROM SF_CONCEPTOS_DEPENDIENTES ORI
                     WHERE ORI.COMPANIA = '''||UN_COMPANIA||''' 
                       AND ORI.ANO      = '  ||UN_ANO_ORIGEN||'
                       AND ORI.TIPOCOBRO||ORI.CONCEPTO_DEPENDIENTE||ORI.CONCEPTO_INDEPENDIENTE NOT IN(SELECT TIPOCOBRO||CONCEPTO_DEPENDIENTE||CONCEPTO_INDEPENDIENTE 
                                              FROM   SF_CONCEPTOS_DEPENDIENTES DES 
                                              WHERE  DES.COMPANIA = '''|| UN_COMPANIA_DESTINO ||'''
                                                AND  DES.ANO      = '  || UN_ANO_DESTINO ||')';
                                                
      MI_CAMPOS:= MI_CAMPOS || ',' || MI_EXCLUIDOS;
      
    BEGIN
          BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   =>'SF_CONCEPTOS_DEPENDIENTES', 
                                                    UN_ACCION  => 'IS', 
                                                    UN_CAMPOS  => MI_CAMPOS, 
                                                    UN_VALORES => MI_VALORES); 
                                                    
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
           END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
          MI_REEMPLAZOS(1).CLAVE := 'ANIO';
          MI_REEMPLAZOS(1).VALOR := UN_ANO_DESTINO;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_INSCONCDEPEN,
            UN_REEMPLAZOS  => MI_REEMPLAZOS
          ); 
     END;         
     
    --Copiar información tabla SF_LEYENDA_TIPOCOBRO     
    
  MI_EXCLUIDOS:='COMPANIA,ANO';
      MI_CAMPOS:= PCK_SYSMAN_UTL.FC_LISTA_CAMPOS('SF_LEYENDA_TIPOCOBRO',MI_EXCLUIDOS);
      MI_VALORES :=' SELECT '||MI_CAMPOS||','''||UN_COMPANIA_DESTINO||''','||UN_ANO_DESTINO||'
                     FROM SF_LEYENDA_TIPOCOBRO ORI
                     WHERE ORI.COMPANIA = '''||UN_COMPANIA||''' 
                       AND ORI.ANO      = '  ||UN_ANO_ORIGEN||'
                       AND ORI.TIPOCOBRO||ORI.CODIGO NOT IN(SELECT TIPOCOBRO||CODIGO 
                                              FROM   SF_LEYENDA_TIPOCOBRO DES 
                                              WHERE  DES.COMPANIA = '''|| UN_COMPANIA_DESTINO ||'''
                                                AND  DES.ANO      = '  || UN_ANO_DESTINO ||')';
                                                
      MI_CAMPOS:= MI_CAMPOS || ',' || MI_EXCLUIDOS;
      
    BEGIN
          BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   =>'SF_LEYENDA_TIPOCOBRO', 
                                                    UN_ACCION  => 'IS', 
                                                    UN_CAMPOS  => MI_CAMPOS, 
                                                    UN_VALORES => MI_VALORES); 
                                                    
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
           END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN
          MI_REEMPLAZOS(1).CLAVE := 'ANIO';
          MI_REEMPLAZOS(1).VALOR := UN_ANO_DESTINO;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_LEYETCOBRO,
            UN_REEMPLAZOS  => MI_REEMPLAZOS
          ); 
     END;       
     
     
    -- Actualizar valor tabla SF_TARIFAS_BASE
        
    FOR MI_RS IN(SELECT FORMULA,CODIGO,TIPO_COBRO
                 FROM SF_TARIFAS_BASE
                 WHERE COMPANIA = UN_COMPANIA_DESTINO
                  AND ANO = UN_ANO_DESTINO)
     LOOP             
    
        MI_SALARIODIARIO := PCK_FACT_GENERAL_COM3.FC_SALARIOMINIMO(UN_COMPANIA   => UN_COMPANIA_DESTINO,
                                                                   UN_ANO        => UN_ANO_DESTINO)/30;
                                                                   
        MI_FORMULA := REPLACE(MI_RS.FORMULA,'SalarioDiario()','('||MI_SALARIODIARIO||')');
         
        MI_VALOR := PCK_FACT_GENERAL_COM3.FC_REEMPLAZARFORMULA(UN_FORMULA => REPLACE(MI_FORMULA,',','.'));
     
              BEGIN 
                  BEGIN 
                      MI_TABLA     := 'SF_TARIFAS_BASE';
                      MI_CAMPOS    := 'VALOR = ' ||REPLACE(MI_VALOR,',','.');
                      MI_CONDICION := ' COMPANIA   = '''||UN_COMPANIA_DESTINO||'''
                                    AND ANO        =  '||UN_ANO_DESTINO||'
                                    AND TIPO_COBRO = '''||MI_RS.TIPO_COBRO||'''
                                    AND CODIGO     = '''||MI_RS.CODIGO||'''';
        
                    PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                 UN_ACCION    => 'M',
                                                 UN_CAMPOS    => MI_CAMPOS,
                                                 UN_CONDICION => MI_CONDICION);              
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                    RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;													
                  END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF  THEN 
                          MI_REEMPLAZOS(1).CLAVE := 'TARIFA';
                          MI_REEMPLAZOS(1).VALOR := MI_RS.CODIGO;
                          MI_REEMPLAZOS(2).CLAVE := 'ANIO';
                          MI_REEMPLAZOS(2).VALOR := UN_ANO_DESTINO;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_TABLAERROR => MI_TABLA,
                                           UN_ERROR_COD  => PCK_ERRORES.ERR_SYSMANSF_ACTVALTARIFA,
                                           UN_REEMPLAZOS => MI_REEMPLAZOS); 
             END;        
             
     END LOOP;
     
END PR_PREPARA_CONF_ANIO_SIG;

FUNCTION FC_CAMBIARFECHAFACTURA
   /*
   NAME 			   : FC_CAMBIARFECHAFACTURA
   AUTHORS 			   : LINA PAOLA VEGA AVELLA
   DATE CREATION	   : 27/07/2025
   MODULO ORIGEN	   : FACTURACION GENERAL
   DESCRIPTION		   : Proceso para cambiar la fecha de todas las facturas.
       --NAME: cambiarFechaFactura

  */ (
         UN_COMPANIA          IN   PCK_SUBTIPOS.TI_COMPANIA,
         UN_ANIO              IN   PCK_SUBTIPOS.TI_ENTERO,
         UN_TIPO              IN   VARCHAR2,
         UN_SOLICITUD         IN   PCK_SUBTIPOS.TI_ENTERO,
         UN_FACT_INICIAL      IN   PCK_SUBTIPOS.TI_ENTERO_LARGO,
         UN_FACT_FINAL        IN   PCK_SUBTIPOS.TI_ENTERO_LARGO,
         UN_FECHA             IN   DATE,
         UN_USUARIO           IN   PCK_SUBTIPOS.TI_USUARIO
    )RETURN VARCHAR2 AS
        MI_FECHA                 DATE;
        MI_RTA                   VARCHAR2(3200 CHAR);
        MI_MENSAJE               VARCHAR2(500 CHAR);
    BEGIN

      IF UN_SOLICITUD = 0 THEN --JM CC 2574
        PCK_CONTABILIDAD.GL_ACT_FECHA_VENC := -1;
        ELSE 
        PCK_CONTABILIDAD.GL_ACT_FECHA_VENC := 0;
    END IF;

     FOR RS IN(
     SELECT SF.ANULADA, SF.CODIGO_COBRO, SF.TIPOCOBRO, SFO.INDPAGO,
               SF.COMPANIA, SF.ANO, SF.TIPO_FACTURA,SF.NUMERO_FACTURA, 
               SF.ANO_CPTE, SF.TIPO_CPTE, SF.NRO_CPTE
        FROM SF_FACTURA SF
        INNER JOIN SF_OBJETO_COBRO SFO
      ON SF.COMPANIA = SFO.COMPANIA
      AND SF.ANO = SFO.ANO
      AND SF.TIPOCOBRO = SFO.TIPOCOBRO
      AND SF.CODIGO_COBRO = SFO.CODIGO_COBRO
    WHERE SF.COMPANIA = UN_COMPANIA
    AND SF.ANO = UN_ANIO
    AND SF.TIPO_FACTURA = UN_TIPO
    AND SF.NUMERO_FACTURA BETWEEN UN_FACT_INICIAL AND UN_FACT_FINAL
    )LOOP
    IF RS.INDPAGO <> 0 THEN
            MI_MENSAJE := 'La factura ' || RS.CODIGO_COBRO || ' ha sido recaudada, no se puede cambiar la fecha.';
    ELSIF RS.ANULADA <> 0 THEN
      MI_MENSAJE := 'La factura ' || RS.CODIGO_COBRO || ' ha sido anulada, no se puede cambiar la fecha.';
    ELSE -- La factura no está recaudada ni anulada, se puede proceder con la actualización
      IF UN_SOLICITUD = 1 THEN -- Check "Fecha de solicitud" activo
                -- 1. Tabla SF_FACTURA: campo FECHA_EXPEDICION
        UPDATE SF_FACTURA
        SET
          FECHA_EXPEDICION = UN_FECHA,
          MODIFIED_BY = UN_USUARIO,
          DATE_MODIFIED = SYSDATE
        WHERE
          COMPANIA = RS.COMPANIA 
          AND ANO = RS.ANO    
          AND TIPOCOBRO = RS.TIPOCOBRO
          AND CODIGO_COBRO = RS.CODIGO_COBRO;

                -- 2. Tabla SF_DETALLE_FACTURA: campo FECHA_INICIO_COBRO
                UPDATE SF_DETALLE_FACTURA
                SET
                    FECHA_INICIO_COBRO = UN_FECHA,
                    MODIFIED_BY = UN_USUARIO,
                    DATE_MODIFIED = SYSDATE
                WHERE
                    COMPANIA = RS.COMPANIA
                    AND ANO = RS.ANO
                    AND TIPO_FACTURA = RS.TIPO_FACTURA
                    AND FACTURA = RS.NUMERO_FACTURA;

                -- 3. Tabla SF_OBJETO_COBRO: campo FECHA_SOLICITUD
                UPDATE SF_OBJETO_COBRO
                SET
                    FECHA_SOLICITUD = UN_FECHA,
                    MODIFIED_BY = UN_USUARIO,
                    DATE_MODIFIED = SYSDATE
                WHERE
                    COMPANIA = RS.COMPANIA
                    AND ANO = RS.ANO
                    AND TIPOCOBRO = RS.TIPOCOBRO
                    AND CODIGO_COBRO = RS.CODIGO_COBRO;

                -- 4. Tabla SF_DETALLE_COBRO: campo FECHA_INICIO_COBRO
                UPDATE SF_DETALLE_COBRO
                SET
                    FECHA_INICIO_COBRO = UN_FECHA,
                    MODIFIED_BY = UN_USUARIO,
                    DATE_MODIFIED = SYSDATE
                WHERE
                    COMPANIA = RS.COMPANIA
                    AND ANO = RS.ANO
                    AND TIPOCOBRO = RS.TIPOCOBRO
                    AND CODIGO_COBRO = RS.CODIGO_COBRO;

                -- 5. Tabla COMPROBANTE_CNT: campo FECHA
                UPDATE COMPROBANTE_CNT
                SET
                    FECHA = UN_FECHA,
                    MODIFIED_BY = UN_USUARIO,
                    DATE_MODIFIED = SYSDATE
                WHERE
                    COMPANIA = RS.COMPANIA
                    AND ANO = RS.ANO_CPTE 
                    AND TIPO = RS.TIPO_CPTE
                    AND NUMERO = RS.NRO_CPTE;

                -- 6. Tabla DETALLE_COMPROBANTE_CNT: campo FECHA
                UPDATE DETALLE_COMPROBANTE_CNT
                SET
                    FECHA = UN_FECHA,
                    MODIFIED_BY = UN_USUARIO,
                    DATE_MODIFIED = SYSDATE
                WHERE
                    COMPANIA = RS.COMPANIA
                    AND ANO = RS.ANO_CPTE
                    AND TIPO_CPTE = RS.TIPO_CPTE
                    AND COMPROBANTE = RS.NRO_CPTE;

        MI_MENSAJE := 'OK';

      ELSIF UN_SOLICITUD = 0 THEN -- Check "Fecha de vencimiento" activo
                -- 1. Tabla SF_FACTURA: campo FECHA_VENCIMIENTO
        UPDATE SF_FACTURA
        SET
          FECHA_VENCIMIENTO = UN_FECHA,
          MODIFIED_BY = UN_USUARIO,
          DATE_MODIFIED = SYSDATE
        WHERE
          COMPANIA = RS.COMPANIA 
          AND ANO = RS.ANO    
          AND TIPOCOBRO = RS.TIPOCOBRO
          AND CODIGO_COBRO = RS.CODIGO_COBRO;

                -- 2. Tabla COMPROBANTE_CNT: campo FECHA_VCN_DOC
                UPDATE COMPROBANTE_CNT
                SET
                    FECHA_VCN_DOC = UN_FECHA,
                    MODIFIED_BY = UN_USUARIO,
                    DATE_MODIFIED = SYSDATE
                WHERE
                    COMPANIA = RS.COMPANIA
                    AND ANO = RS.ANO_CPTE
                    AND TIPO = RS.TIPO_CPTE
                    AND NUMERO = RS.NRO_CPTE;

        MI_MENSAJE := 'OK';

            END IF;
    END IF;
        
        IF MI_RTA IS NOT NULL AND LENGTH(MI_RTA) > 0 THEN
            MI_RTA := MI_RTA || CHR(10) || MI_MENSAJE; 
        ELSE
            MI_RTA := MI_MENSAJE; 
        END IF;

  END LOOP;
    PCK_CONTABILIDAD.GL_ACT_FECHA_VENC := 0; --JM CC 2574
    RETURN MI_RTA;
    END FC_CAMBIARFECHAFACTURA;  
END PCK_FACT_GENERAL_COM1;