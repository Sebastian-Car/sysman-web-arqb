create or replace PACKAGE BODY                                                                                                                                             PCK_CONTABILIZAR AS

--1
PROCEDURE PR_CARGARINTERFAZALMACENCC
/*
    NAME              : PR_CARGARINTERFAZALMACENCC  --> En Access Transaccion_Click()
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
    DATE MIGRADOR     : 04/01/2018
    TIME              : 09:00 AM
    SOURCE MODULE     : InterfacesPb2017.11.02
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : INSERTA EN LA TABLA ALMACENCONTABILIDADCC LOS REGISTROS DE LA TABLA ALMACENCONTABILIDAD DE UN AÑO
    PARAMETERS        : UN_COMPANIA        => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                        UN_CODIGOELEMENTO  =>  CODIGO INTERNO PARA REGISTRAR LA NOVEDAD.
                        UN_ANO             =>  AÑO PARA EL CUAL SE GENERARA LA NOVEDAD.

      @NAME:    cargarInterfazAlmacenCC
      @METHOD:  POST
  */
(
  UN_COMPANIA           IN  PCK_SUBTIPOS.TI_COMPANIA,
  UN_CODIGOELEMENTO     IN  ALMACENCONTABILIDADCC.CODIGOELEMENTO%TYPE,
  UN_ANO                IN  ALMACENCONTABILIDADCC.ANO%TYPE
)
AS
  MI_CONTEO         PCK_SUBTIPOS.TI_ENTERO;
  MI_RS             SYS_REFCURSOR;
  MI_STRSQL         PCK_SUBTIPOS.TI_STRSQL;
  MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
  MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN

    SELECT COUNT('X')
    INTO MI_CONTEO
    FROM ALMACENCONTABILIDADCC
    WHERE ANO          = UN_ANO
     AND CODIGOELEMENTO = UN_CODIGOELEMENTO;

      IF MI_CONTEO = 0 THEN
        <<recorrer_almacencontabilidad>>
        FOR MI_RS
        IN(SELECT ALMACENCONTABILIDAD.COMPANIA,
                  ALMACENCONTABILIDAD.CODIGOELEMENTO,
                  ALMACENCONTABILIDAD.TIPOMOVIMIENTO,
                  ALMACENCONTABILIDAD.ANO,
                  CENTRO_COSTO.CODIGO,
                  ALMACENCONTABILIDAD.CUENTADEBITO,
                  ALMACENCONTABILIDAD.CUENTACREDITO,
                  ALMACENCONTABILIDAD.CUENTADEBITOAJUSTE,
                  ALMACENCONTABILIDAD.CUENTACREDITOAJUSTE,
                  ALMACENCONTABILIDAD.DEBITO_BASE,
                  ALMACENCONTABILIDAD.CREDITO_BASE,
                  ALMACENCONTABILIDAD.CREDITO_IVA,
                  ALMACENCONTABILIDAD.DEBITO_IVA
            FROM ALMACENCONTABILIDAD
            INNER JOIN CENTRO_COSTO
                ON ALMACENCONTABILIDAD.COMPANIA = CENTRO_COSTO.COMPANIA
                AND ALMACENCONTABILIDAD.ANO     = CENTRO_COSTO.ANO
            WHERE ALMACENCONTABILIDAD.COMPANIA = UN_COMPANIA
              AND ALMACENCONTABILIDAD.ANO      = UN_ANO
        )LOOP


            SELECT COUNT('X')
              INTO MI_CONTEO
              FROM ALMACENCONTABILIDADCC
             WHERE CODIGOELEMENTO = MI_RS.CODIGOELEMENTO
               AND TIPOMOVIMIENTO = MI_RS.TIPOMOVIMIENTO
               AND ANO            = MI_RS.ANO
               AND CENTRO_COSTO   = MI_RS.CODIGO ;


              IF MI_CONTEO = 0 THEN

                  MI_CAMPOS :='COMPANIA
                              ,CODIGOELEMENTO
                              ,TIPOMOVIMIENTO
                              ,ANO
                              ,CENTRO_COSTO
                              ,CUENTADEBITO
                              ,CUENTACREDITO
                              ,CUENTADEBITOAJUSTE
                              ,CUENTACREDITOAJUSTE
                              ,DEBITO_BASE
                              ,CREDITO_BASE
                              ,CREDITO_IVA
                              ,DEBITO_IVA';

                   MI_VALORES := ' '''||UN_COMPANIA||'''
                                 , '''||MI_RS.CODIGOELEMENTO||'''
                                 , '''||MI_RS.TIPOMOVIMIENTO||'''
                                 , '||MI_RS.ANO||'
                                 , '''||MI_RS.CODIGO||'''
                                 , '''||MI_RS.CUENTADEBITO||'''
                                 , '''||MI_RS.CUENTACREDITO||'''
                                 , '''||MI_RS.CUENTADEBITOAJUSTE||''' 
                                 , '''||MI_RS.CUENTACREDITOAJUSTE||'''
                                 , '''||MI_RS.DEBITO_BASE||'''
                                 , '''||MI_RS.CREDITO_BASE||'''
                                 , '''||MI_RS.CREDITO_IVA||'''
                                 , '''||MI_RS.DEBITO_IVA||''' ';           
                        BEGIN
                          BEGIN

                               PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME (UN_TABLA   => 'ALMACENCONTABILIDADCC'
                                                                       ,UN_ACCION  => 'I'
                                                                       ,UN_CAMPOS  => MI_CAMPOS
                                                                       ,UN_VALORES => MI_VALORES);  

                                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                          RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                            END ;

                                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                                    MI_MSGERROR(1).CLAVE := 'CODIGOELEMENTO';
                                    MI_MSGERROR(1).VALOR := MI_RS.CODIGOELEMENTO;
                                               PCK_ERR_MSG.RAISE_WITH_MSG(
                                                                  UN_EXC_COD =>SQLCODE
                                                                  ,UN_ERROR_COD=>PCK_ERRORES.ERR_INSERTALMACENCC
                                                                  ,UN_REEMPLAZOS  => MI_MSGERROR);
                       END; 


              END IF;

        END LOOP recorrer_almacencontabilidad;

       END IF;


END PR_CARGARINTERFAZALMACENCC;

--3
FUNCTION FC_CONTABILIZAR(
    /*
        NAME              : En Access InterfazContableH
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
        DATE MIGRADOR     : 16/11/2017
        TIME              : 09:27 AM
        SOURCE MODULE     : INTERFACES InterfacesPb2017.12.02
        MODIFIER          : GUSTAVO ANDRES FIGUEREDO AVILA
        DATE MODIFIED     : 28/01/2022
        TIME              : 15:29
        DESCRIPTION       : Ticket 7708504 Auxiliares web service
        PARAMETERS        :
        MODIFICATIONS     : Se agrega validacion por PLAN_CONTABLE.OBLIGA_CENTRO,PLAN_CONTABLE.OBLIGA_TERCERO,
                            PLAN_CONTABLE.OBLIGA_AUXILIAR,PLAN_CONTABLE.OBLIGA_FUENTE,PLAN_CONTABLE.OBLIGA_REFERENCIA,
                            para que en caso de que alguno de estos valores esté seleccionado en plan contable,
                            inserte el valor en detalle_comprobante_cnt

        @NAME: contabliazar
        @METHOD:  POST
    */
   UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA
  ,UN_TIPOCOMPROBANTE  IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE
  ,UN_NUMERO           IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT
  ,UN_ANO              IN PCK_SUBTIPOS.TI_ANIO
  ,UN_FECHA            IN DATE
  ,UN_TERCERO          IN PCK_SUBTIPOS.TI_TERCERO DEFAULT PCK_DATOS.CONS_TERCERO
  ,UN_SUCURSAL         IN PCK_SUBTIPOS.TI_SUCURSAL DEFAULT PCK_DATOS.CONS_SUCURSAL
  ,UN_CENTRO_COSTO     IN PCK_SUBTIPOS.TI_CENTRO_COSTO    DEFAULT PCK_DATOS.CONS_CENTRO
  ,UN_AUXILIAR         IN PCK_SUBTIPOS.TI_AUXILIAR        DEFAULT PCK_DATOS.CONS_AUXILIAR
  ,UN_FUENTE_RECURSO   IN PCK_SUBTIPOS.TI_FUENTE_RECURSOS DEFAULT PCK_DATOS.CONS_FUENTE
  ,UN_REFERENCIA       IN PCK_SUBTIPOS.TI_REFERENCIA      DEFAULT PCK_DATOS.CONS_REFERENCIA
  ,UN_DESCRIPCION      IN PCK_SUBTIPOS.TI_DESCRIPCION
  ,UN_SIMPLE           IN PCK_SUBTIPOS.TI_LOGICO
  ,UN_INDIMPRESION     IN PCK_SUBTIPOS.TI_LOGICO
  ,UN_INTOMITIRPPTAL   IN PCK_SUBTIPOS.TI_LOGICO := 0
  ,UN_CONCILIAR        IN PCK_SUBTIPOS.TI_LOGICO := 0
  --agrupar
  --Mensaje
  ,UN_PLANO            IN PCK_SUBTIPOS.TI_LOGICO := 0
  --DOCUMENTO
  ,UN_NONETEA          IN PCK_SUBTIPOS.TI_LOGICO := 0
  ,UN_CONTRATISTA      IN PCK_SUBTIPOS.TI_LOGICO := 0
  ,UN_TEXTO            IN VARCHAR2 DEFAULT 'Interface'
  ,UN_CONTRATO         IN COMPROBANTE_CNT.NUMEROCONTRATO%TYPE := 0
  ,UN_TIPOCONTRATO     IN COMPROBANTE_CNT.TIPOCONTRATO%TYPE := ''
  ,UN_NRO_DOCUMENTO    IN COMPROBANTE_CNT.NRO_DOCUMENTO%TYPE := ''
  ,UN_NRO_DOCUMENTO_2  IN COMPROBANTE_CNT.NRO_DOCUMENTO_2%TYPE := ''
  ,UN_ALMDEP           IN PCK_SUBTIPOS.TI_LOGICO := 0
  ,UN_TERCE            IN PCK_SUBTIPOS.TI_LOGICO := 0
  ,UN_RESAUXGEN        IN PCK_SUBTIPOS.TI_LOGICO := 0
  ,UN_USUARIO          IN COMPROBANTE_CNT.CREATED_BY%TYPE

)
RETURN VARCHAR2 AS
 MI_CLASE           PCK_SUBTIPOS.TI_CLASECOMPROBANTE DEFAULT '';
 MI_EXISTENDATOS    PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;

 MI_TABLA             PCK_SUBTIPOS.TI_TABLA;
 MI_CAMPOS            PCK_SUBTIPOS.TI_CAMPOS;
 MI_VALORES           PCK_SUBTIPOS.TI_VALORES;
 MI_CONDICION         PCK_SUBTIPOS.TI_CONDICION;
 MI_FILAS             PCK_SUBTIPOS.TI_ENTERO;
 MI_MERGEUSING        PCK_SUBTIPOS.TI_MERGEUSING;
 MI_MERGEENLACE       PCK_SUBTIPOS.TI_MERGEENLACE;
 MI_MERGEEXISTE       PCK_SUBTIPOS.TI_MERGEEXISTE;
 MI_MERGENOEXISTE     PCK_SUBTIPOS.TI_MERGENOEXISTE;
 MI_DIASVENCIMIENTO   PCK_SUBTIPOS.TI_ENTERO;

 MI_RTAGENERAL          PCK_SUBTIPOS.TI_LOGICO;
 MI_MSGERROR            PCK_SUBTIPOS.TI_CLAVEVALOR;
 MI_ACTUALIZAR          BOOLEAN DEFAULT FALSE;
 MI_RS                  SYS_REFCURSOR;
 MI_CUENTASSINMOV       VARCHAR2(5000 CHAR) DEFAULT ' ';
 MI_RTA                 VARCHAR2(5500 CHAR) DEFAULT ' ';
 MI_NETOSCUENTAS        BOOLEAN DEFAULT FALSE;

 MI_SVALOR_DEBITO           PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
 MI_SVALOR_CREDITO          PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
 MI_COMPDESCUADRADO         BOOLEAN DEFAULT FALSE;
 MI_VALORDOCUMENTO          PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
 MI_EXISTECOMPROBANTE       VARCHAR2(2 CHAR);
 MI_PARINSTERCEROSINAUX     BOOLEAN DEFAULT FALSE;
 MI_PARCODPPTALINTER        BOOLEAN DEFAULT FALSE;
 MI_PARDISTNETOSCCONTA      BOOLEAN DEFAULT FALSE;
 MI_PARDISTNETOSCCONTAORD   BOOLEAN DEFAULT FALSE;
 MI_EXISTEORDCOMPRA         BOOLEAN DEFAULT FALSE;
 MI_NUMEROORDCOMPRA         COMPROBANTE_CNT.NUMEROCONTRATO%TYPE DEFAULT 0;
 MI_CLASEORDCOMPRA          ORDENDECOMPRA.CLASEORDEN%TYPE;
 MI_LSDESC                  VARCHAR2(255 CHAR);
 MI_DBLGIRAR                PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
 MI_CUENTASSINNATU          VARCHAR2(15000 CHAR) DEFAULT ' ';
 MI_CADENAINSERTAR          CLOB DEFAULT ' ';
 MI_CUENTADETALLE           PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
 MI_PARCONTABICERRADO       VARCHAR2(2 CHAR);
 MI_AFECTAPPTALWS           PCK_SUBTIPOS.TI_STRSQL;
 MI_FECHAVENCIMIENTO        DATE; -- MPEREZ TICKET7745545
 MI_PARMANJSIGEC            VARCHAR2(2 CHAR);
 MI_NROCONTRATOSIGEC        VARCHAR2(255 CHAR);
 MI_TIPOCONTRATOSIGEC       VARCHAR2(3 CHAR);
 MI_COMPINTERFASEDIAR       PCK_SUBTIPOS.TI_PARAMETRO;
 MI_COMPINTERFASEMENS       PCK_SUBTIPOS.TI_PARAMETRO; 
BEGIN
    --PR_BINTERFAZ();
    BEGIN
        SELECT COUNT(0) CUENTA
        INTO   MI_EXISTENDATOS
        FROM   TEMP_PLANA_AJUSTES
        WHERE  COMPANIA    = UN_COMPANIA
          AND  ANO         = UN_ANO
          AND  TIPO_CPTE   = UN_TIPOCOMPROBANTE
          AND  COMPROBANTE = UN_NUMERO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_EXISTENDATOS := 0;
    END;

    BEGIN
        IF MI_EXISTENDATOS = 0 THEN
            --No se generaron registros de interface.
            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
        END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERRR_NODATOPLANAAJUSTES,
                                    UN_TABLAERROR => MI_TABLA);
    END;

    MI_PARCONTABICERRADO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                          UN_NOMBRE    => 'CONTABILIZAR NOMINA CON FECHAS CERRADAS',
                                                          UN_MODULO    =>  PCK_DATOS.MODULOCONTABILIDAD,
                                                          UN_FECHA_PAR =>  SYSDATE),'NO'); 
														  
    MI_PARMANJSIGEC := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                      UN_NOMBRE    => 'SF MANEJA FACTURACION DE ESTAMPILLA ELECTRONICA',
                                                      UN_MODULO    =>  PCK_DATOS.FC_MODULOFACTURACIONGENERAL(),
                                                      UN_FECHA_PAR =>  SYSDATE),'NO');
    MI_COMPINTERFASEDIAR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA    => UN_COMPANIA
                                                 ,UN_NOMBRE      => 'TIPO COMPROBANTE INTERFASE DIARIA ALMACEN'
                                                 ,UN_MODULO      => PCK_DATOS.MODULOCONTABILIDAD
                                                 ,UN_FECHA_PAR   => SYSDATE ),'ALM');
    MI_COMPINTERFASEMENS := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA    => UN_COMPANIA
                                                 ,UN_NOMBRE      => 'TIPO COMPROBANTE INTERFASE MENSUAL ALMACEN'
                                                 ,UN_MODULO      => PCK_DATOS.MODULOCONTABILIDAD
                                                 ,UN_FECHA_PAR   => SYSDATE ),'AL1');                                            																																				
    MI_RTA := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADODIA(UN_COMPANIA   => UN_COMPANIA,
                                                    UN_ANO        => UN_ANO,
                                                    UN_MES        => TO_NUMBER(TO_CHAR(UN_FECHA,'MM')),
                                                    UN_DIA        => TO_NUMBER(TO_CHAR(UN_FECHA,'DD')),
                                                    UN_MODULO     => 1,
                                                    UN_PROCESO    => 1); 
    IF MI_PARCONTABICERRADO <> 'SI' THEN      
        IF MI_RTA <> 'A' THEN
            DECLARE
              MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR; 
            BEGIN
              RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;           
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
              MI_MSGERROR(1).CLAVE := 'FECHA';
              MI_MSGERROR(1).VALOR := TO_CHAR(UN_FECHA,'DD/MM/YYYY');
              PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE,
                  UN_TABLAERROR =>'TEMP_PLANA_AJUSTES',
                  UN_ERROR_COD  => PCK_ERRORES.ERROR_GRAL_DIACERRADO,
                  UN_REEMPLAZOS => MI_MSGERROR);
            END;   
        END IF;
    END IF;    
    BEGIN
        SELECT DISTINCT 'X'
        INTO   MI_EXISTECOMPROBANTE
        FROM   COMPROBANTE_CNT
        WHERE  COMPANIA = UN_COMPANIA
          AND  ANO      = UN_ANO
          AND  TIPO     = UN_TIPOCOMPROBANTE
          AND  NUMERO   = UN_NUMERO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_EXISTECOMPROBANTE := ' ';
    END;

    IF NVL(MI_EXISTECOMPROBANTE,' ') <> ' ' THEN
        --Reversa los saldos de la tabla plan contable.
          BEGIN
            MI_TABLA := 'DETALLE_COMPROBANTE_CNT';
            MI_CONDICION := ' COMPANIA = '''|| UN_COMPANIA ||'''
                          AND ANO = '|| UN_ANO ||'
                          AND TIPO_CPTE = '''|| UN_TIPOCOMPROBANTE ||'''
                          AND COMPROBANTE = '|| UN_NUMERO ||' ';
            BEGIN
                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => MI_TABLA
                                           ,UN_ACCION     => 'E'
                                           ,UN_CONDICION  => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
            --El Detalle del comprobante no fue eliminado.
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    =>  SQLCODE,
                                        UN_ERROR_COD  => PCK_ERRORES.ERRR_BORRARDETCOMPRINTERFAZ,
                                        UN_TABLAERROR => MI_TABLA);
        END; 

        BEGIN
            MI_TABLA := 'COMPROBANTE_CNTRETENCION';
            MI_CONDICION := ' COMPANIA = '''|| UN_COMPANIA ||'''
                          AND ANO = '|| UN_ANO ||'
                          AND TIPO = '''|| UN_TIPOCOMPROBANTE ||'''
                          AND NUMERO = '|| UN_NUMERO ||' ';
            BEGIN
                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => MI_TABLA
                                           ,UN_ACCION     => 'E'
                                           ,UN_CONDICION  => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
            --El comprobante de retención no fue eliminado.
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    =>  SQLCODE,
                                        UN_ERROR_COD  => PCK_ERRORES.ERRR_BORRARCOMPRETENINTERFAZ,
                                        UN_TABLAERROR => MI_TABLA);
        END;
        BEGIN
            MI_TABLA := 'COMPROBANTE_CNT';
            MI_CONDICION := ' COMPANIA = '''|| UN_COMPANIA ||'''
                          AND ANO = '|| UN_ANO ||'
                          AND TIPO = '''|| UN_TIPOCOMPROBANTE ||'''
                          AND NUMERO = '|| UN_NUMERO ||' ';
            BEGIN
                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => MI_TABLA
                                           ,UN_ACCION     => 'E'
                                           ,UN_CONDICION  => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
            --El comprobante no fue eliminado.
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    =>  SQLCODE,
                                        UN_ERROR_COD  => PCK_ERRORES.ERRR_BORRARCOMPROBANTEINTERFAZ,
                                        UN_TABLAERROR => MI_TABLA);
        END;

    END IF; 

    BEGIN
        SELECT CLASE_CONTABLE
        INTO   MI_CLASE
        FROM   TIPO_COMPROBANTE
        WHERE  COMPANIA = UN_COMPANIA
          AND  CODIGO   = UN_TIPOCOMPROBANTE;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_CLASE := ' ';
    END;

    --Parámetros
    MI_PARINSTERCEROSINAUX := CASE WHEN  NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                          UN_NOMBRE    => 'INSERTAR TERCERO SIN INDICADOR AUXILIAR DE TERCERO',
                                                          UN_MODULO    =>  PCK_DATOS.MODULOCONTABILIDAD,
                                                          UN_FECHA_PAR =>  SYSDATE),'NO') = 'SI' THEN TRUE ELSE FALSE END;


    MI_ACTUALIZAR := CASE WHEN  NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                          UN_NOMBRE    =>  'SIMPLIFICAR AUXILIARES EN PROCESO DE INTERFACE',
                                                          UN_MODULO    =>  PCK_DATOS.MODULOCONTABILIDAD,
                                                          UN_FECHA_PAR =>  SYSDATE),'NO') = 'SI' THEN TRUE ELSE FALSE END;

    MI_COMPDESCUADRADO := CASE WHEN  NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                            UN_NOMBRE    =>  'INTERFAZ PERMITE GENERAR COMPROBANTE DESCUADRADO',
                                                            UN_MODULO    =>  PCK_DATOS.MODULOCONTABILIDAD,
                                                            UN_FECHA_PAR =>  SYSDATE),'NO') = 'SI' THEN TRUE ELSE FALSE END;

    MI_PARCODPPTALINTER := CASE WHEN  NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                 UN_NOMBRE    =>  'CONSERVAR CODIGO PRESUPUESTAL CONFIGURADO INTERFACE',
                                                                 UN_MODULO    =>  PCK_DATOS.MODULOCONTABILIDAD,
                                                                 UN_FECHA_PAR =>  SYSDATE),'NO') = 'SI' THEN TRUE ELSE FALSE END;

    MI_PARDISTNETOSCCONTA := CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
											  UN_NOMBRE    =>  'DISTRIBUIR NETOS CUENTAS CONTABLES',
											  UN_MODULO    =>  PCK_DATOS.MODULOCONTABILIDAD,
											  UN_FECHA_PAR =>  SYSDATE),'NO') = 'SI' THEN TRUE ELSE FALSE END;

    MI_PARDISTNETOSCCONTAORD := CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
											  UN_NOMBRE    =>  'DISTRIBUIR NETOS CUENTAS CONTABLES POR ORDEN',
											  UN_MODULO    =>  PCK_DATOS.MODULOCONTABILIDAD,
											  UN_FECHA_PAR =>  SYSDATE),'NO') = 'SI' THEN TRUE ELSE FALSE END;

    IF UN_PLANO <> 0 THEN
        IF  NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                      UN_NOMBRE    =>  'MANTENER TERCERO INTERFAZ PLANO',
                                      UN_MODULO    =>  PCK_DATOS.MODULOCONTABILIDAD,
                                      UN_FECHA_PAR =>  SYSDATE),'NO') = 'SI' THEN
            MI_ACTUALIZAR := FALSE;
        END IF;
    END IF;

    IF MI_ACTUALIZAR THEN
        BEGIN
            MI_TABLA     := 'TEMP_PLANA_AJUSTES';   --Tabla
            MI_MERGEUSING := 'SELECT PLAN_CONTABLE.COMPANIA,
                                    PLAN_CONTABLE.ANO,
                                    PLAN_CONTABLE.CODIGO,
                                    PLAN_CONTABLE.MAN_CEN_CTO,
                                    PLAN_CONTABLE.MAN_AUX_TER,
                                    PLAN_CONTABLE.MAN_AUX_GEN,
                                    PLAN_CONTABLE.MAN_AUX_FUE,
                                    PLAN_CONTABLE.MAN_AUX_REF,
									PLAN_CONTABLE.OBLIGA_CENTRO,
                                    PLAN_CONTABLE.OBLIGA_TERCERO,
                                    PLAN_CONTABLE.OBLIGA_AUXILIAR,
                                    PLAN_CONTABLE.OBLIGA_FUENTE,
                                    PLAN_CONTABLE.OBLIGA_REFERENCIA,
                                    PLAN_CONTABLE.NATURALEZA,
                                    PLAN_PPTAL_CUENTACNT.CUENTA_PPTAL
                                    FROM   PLAN_CONTABLE LEFT JOIN (SELECT COMPANIA,ANO,CUENTA_CONTABLE,MIN(RUBRO) CUENTA_PPTAL
                                                                    FROM PLAN_PPTAL_CUENTACNT
                                                                    WHERE PLAN_PPTAL_CUENTACNT.COMPANIA = ''' || UN_COMPANIA || '''
                                                                    AND  PLAN_PPTAL_CUENTACNT.ANO = '|| UN_ANO || '
                                                                    GROUP BY COMPANIA,ANO,CUENTA_CONTABLE
                                                                    HAVING COUNT(COMPANIA) = 1 ) PLAN_PPTAL_CUENTACNT
                                    ON PLAN_CONTABLE.COMPANIA = PLAN_PPTAL_CUENTACNT.COMPANIA
                                    AND PLAN_CONTABLE.ANO = PLAN_PPTAL_CUENTACNT.ANO
                                    AND PLAN_CONTABLE.CODIGO = PLAN_PPTAL_CUENTACNT.CUENTA_CONTABLE
                                    WHERE  PLAN_CONTABLE.COMPANIA = '''|| UN_COMPANIA ||'''
                                    AND  PLAN_CONTABLE.ANO = '|| UN_ANO ||' '; --Vista

            --Definir si se enlaza a la vista o a la tabla
            MI_MERGEENLACE := ' TABLA.COMPANIA    = VISTA.COMPANIA
                             AND TABLA.ANO         = VISTA.ANO
                             AND TABLA.CUENTA      = VISTA.CODIGO
                             AND TABLA.TIPO_CPTE   = '''|| UN_TIPOCOMPROBANTE ||'''
                             AND TABLA.COMPROBANTE = '|| UN_NUMERO ||' ';

            IF MI_COMPDESCUADRADO AND MI_PARINSTERCEROSINAUX  THEN
                MI_MERGEEXISTE := '  UPDATE SET  TABLA.NRO_DOCUMENTO = CASE WHEN 1 = '|| CASE WHEN UN_SIMPLE <> 0 THEN 1 ELSE 0 END || '  THEN '' '' ELSE NVL(TABLA.NRO_DOCUMENTO,'' '') END
                                                ,TABLA.NATURALEZA    = VISTA.NATURALEZA  ';
            ELSE
 				MI_MERGEEXISTE := ' UPDATE SET TABLA.CENTRO_COSTO    = CASE WHEN VISTA.MAN_CEN_CTO = 0 AND 1 = '|| CASE WHEN UN_SIMPLE <> 0 THEN 1 ELSE 0 END ||' AND VISTA.OBLIGA_CENTRO = 0 THEN  '''|| PCK_DATOS.CONS_CENTRO  ||''' ELSE NVL(TABLA.CENTRO_COSTO,'''|| PCK_DATOS.CONS_CENTRO ||''') END
                                              ,TABLA.TERCERO         = CASE WHEN VISTA.MAN_AUX_TER = 0 AND 1 = '|| CASE WHEN UN_SIMPLE <> 0 THEN 1 ELSE 0 END ||' AND 1 = '|| CASE WHEN UN_TERCE = 0 THEN 1 ELSE 0 END ||' AND VISTA.OBLIGA_TERCERO = 0 THEN '''|| PCK_DATOS.CONS_TERCERO ||'''  ELSE NVL(TABLA.TERCERO,'''|| PCK_DATOS.CONS_TERCERO ||''')  END
                                              ,TABLA.SUCURSAL        = CASE WHEN VISTA.MAN_AUX_TER = 0 AND 1 = '|| CASE WHEN UN_SIMPLE <> 0 THEN 1 ELSE 0 END ||' AND 1 = '|| CASE WHEN UN_TERCE = 0 THEN 1 ELSE 0 END ||' AND VISTA.OBLIGA_TERCERO = 0 THEN '''|| PCK_DATOS.CONS_SUCURSAL ||''' ELSE NVL(TABLA.SUCURSAL,'''|| PCK_DATOS.CONS_SUCURSAL ||''')  END
                                              ,TABLA.AUXILIAR        = CASE WHEN VISTA.MAN_AUX_GEN = 0 AND 1 = '|| CASE WHEN UN_SIMPLE <> 0 THEN 1 ELSE 0 END ||' AND 1 = '|| CASE WHEN UN_RESAUXGEN = 0 THEN 1 ELSE 0 END ||' AND VISTA.OBLIGA_AUXILIAR = 0 THEN '''|| PCK_DATOS.CONS_AUXILIAR ||''' ELSE NVL(TABLA.AUXILIAR,'''|| PCK_DATOS.CONS_AUXILIAR ||''')  END
                                              ,TABLA.FUENTE_RECURSOS = CASE WHEN VISTA.MAN_AUX_FUE = 0 AND 1 = '|| CASE WHEN UN_SIMPLE <> 0 THEN 1 ELSE 0 END ||' AND VISTA.OBLIGA_FUENTE = 0 THEN  '''|| PCK_DATOS.CONS_FUENTE  ||''' ELSE NVL(TABLA.FUENTE_RECURSOS,'''|| PCK_DATOS.CONS_FUENTE ||''') END
                                              ,TABLA.REFERENCIA      = CASE WHEN VISTA.MAN_AUX_REF = 0 AND 1 = '|| CASE WHEN UN_SIMPLE <> 0 THEN 1 ELSE 0 END ||' AND VISTA.OBLIGA_REFERENCIA = 0 THEN  '''|| PCK_DATOS.CONS_FUENTE  ||''' ELSE NVL(TABLA.REFERENCIA,'''|| PCK_DATOS.CONS_FUENTE ||''') END
                                              ,TABLA.NRO_DOCUMENTO   = CASE WHEN 1 = '|| CASE WHEN UN_SIMPLE <> 0 THEN 1 ELSE 0 END || '  THEN '' '' ELSE NVL(TABLA.NRO_DOCUMENTO,'' '') END
                                              ,TABLA.NATURALEZA      = VISTA.NATURALEZA  ';
            END IF;

            IF NOT MI_PARCODPPTALINTER THEN
                MI_MERGEEXISTE := MI_MERGEEXISTE || ' ,TABLA.CUENTAPPTAL =  VISTA.CUENTA_PPTAL';
            ELSE
                MI_MERGEEXISTE := MI_MERGEEXISTE || ' ,TABLA.CUENTAPPTAL = CASE WHEN NVL(TABLA.CUENTAPPTAL,'' '') = '' '' THEN VISTA.CUENTA_PPTAL ELSE TABLA.CUENTAPPTAL END  ';
            END IF;

            BEGIN
                MI_FILAS := PCK_DATOS.FC_ACME(UN_ACCION   => 'MM',
                                           UN_TABLA       => MI_TABLA,
                                           UN_MERGEUSING  => MI_MERGEUSING,
                                           UN_MERGEENLACE => MI_MERGEENLACE,
                                           UN_MERGEEXISTE => MI_MERGEEXISTE);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
            --Se presentó error al actualizar los indicadores de las cuentas.
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    =>  SQLCODE,
                                        UN_ERROR_COD  => PCK_ERRORES.ERRR_ACTINDICADORESCUENTA,
                                        UN_TABLAERROR => MI_TABLA);
        END;
    END IF; --Fin actualizar;

    BEGIN
        <<CUENTASSINMOV>>
        FOR MI_RS IN
        (
            SELECT DISTINCT TP.CUENTA,TP.DESCRIPCION
            FROM   TEMP_PLANA_AJUSTES TP INNER JOIN PLAN_CONTABLE PC ON TP.COMPANIA = PC.COMPANIA
                        AND TP.ANO = PC.ANO
                        AND TP.CUENTA = PC.CODIGO
            WHERE  TP.COMPANIA = UN_COMPANIA
              AND  TP.ANO =UN_ANO
              AND  TP.TIPO_CPTE = UN_TIPOCOMPROBANTE
              AND  TP.COMPROBANTE = UN_NUMERO
              AND  (PC.MOVIMIENTO + PC.MAN_CEN_CTO + PC.MAN_AUX_TER + PC.MAN_AUX_GEN + PC.MAN_AUX_REF + PC.MAN_AUX_FUE) = 0
        )
        LOOP
            IF NVL(MI_CUENTASSINMOV,' ') <> ' ' THEN
                MI_CUENTASSINMOV := MI_CUENTASSINMOV || CHR(13) || CHR(10);
            END IF;
            MI_CUENTASSINMOV := MI_CUENTASSINMOV || MI_RS.CUENTA || ' ' || MI_RS.DESCRIPCION;
        END LOOP CUENTASSINMOV;

        IF NVL(MI_CUENTASSINMOV,' ') <> ' ' THEN
            MI_RTA := 'Las siguiente(s) cuenta(s) no tiene(n) movimiento en el plan de cuentas y esta(n) adjudicada(s) a los procesos '|| CHR(13) || CHR(10) || MI_CUENTASSINMOV || CHR(13) || CHR(10) || 'Por Favor Revise su Configuración y vuelva a realizar el proceso' || CHR(13) || CHR(10);
            MI_RTA := MI_RTA || 'No se creo el comprobante ' || UN_NUMERO || ' tipo: ' || UN_TIPOCOMPROBANTE;
            -- RETURN MI_RTA;
            BEGIN
              RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN 
                MI_MSGERROR(1).CLAVE := 'CUENTASSINMOV';
                MI_MSGERROR(1).VALOR :=  MI_CUENTASSINMOV;
                MI_MSGERROR(2).CLAVE := 'NUMERO';
                MI_MSGERROR(2).VALOR :=  UN_NUMERO;
                MI_MSGERROR(3).CLAVE := 'TIPOCOMPROBANTE';
                MI_MSGERROR(3).VALOR :=  UN_TIPOCOMPROBANTE;
              PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD    => SQLCODE,
                          UN_ERROR_COD  => PCK_ERRORES.ALER_CUENTASSINMOV,
                          UN_REEMPLAZOS => MI_MSGERROR);        
            END;
        END IF;
    END;
    MI_AFECTAPPTALWS   := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                          UN_NOMBRE    => 'GENERA AFECTACION PRESUPUESTAL EN LAS CUENTAS 4 Y 13 DE RECAUDO (WEB SERVICE)',
                                          UN_MODULO    => PCK_DATOS.FC_MODULOPRESUPUESTO,
                                          UN_FECHA_PAR => SYSDATE,
                                          UN_IND_MAYUS => 0
                                                ),'NO');
    BEGIN
        MI_TABLA := 'TEMP_PLANA_AJUSTES';
        IF (MI_AFECTAPPTALWS = 'SI') THEN
            MI_CAMPOS := 'VALOR_DEBITO = -(VALOR_DEBITO)
                     ,VALOR_CREDITO = 0 ';
        ELSE
             MI_CAMPOS := 'VALOR_CREDITO = -(VALOR_DEBITO)
                     ,VALOR_DEBITO = 0 ';
        END IF;

        MI_CONDICION := ' COMPANIA = '''|| UN_COMPANIA ||'''
                      AND ANO = '|| UN_ANO ||'
                      AND TIPO_CPTE = '''|| UN_TIPOCOMPROBANTE ||'''
                      AND COMPROBANTE = '|| UN_NUMERO ||'
                      AND VALOR_DEBITO < 0 ';

        BEGIN
            MI_FILAS := PCK_DATOS.FC_ACME( UN_TABLA     =>  MI_TABLA,
                                               UN_ACCION    =>  'M',
                                               UN_CAMPOS    =>  MI_CAMPOS,
                                               UN_CONDICION =>  MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
        END;
        IF (MI_AFECTAPPTALWS = 'SI') THEN
            MI_CAMPOS := ' VALOR_CREDITO = -(VALOR_CREDITO)
                          ,VALOR_DEBITO = 0 ';
        ELSE
            MI_CAMPOS := ' VALOR_DEBITO = -(VALOR_CREDITO)
                          ,VALOR_CREDITO = 0 ';
        END IF;
        MI_CONDICION := ' COMPANIA = '''|| UN_COMPANIA ||'''
                      AND ANO = '|| UN_ANO ||'
                      AND TIPO_CPTE = '''|| UN_TIPOCOMPROBANTE ||'''
                      AND COMPROBANTE = '|| UN_NUMERO ||'
                      AND VALOR_CREDITO < 0 ';

        BEGIN
            MI_FILAS:=PCK_DATOS.FC_ACME( UN_TABLA     =>  MI_TABLA,
                                                 UN_ACCION    =>  'M',
                                                 UN_CAMPOS    =>  MI_CAMPOS,
                                                 UN_CONDICION =>  MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
        --Se presentó error al actualizar los créditos y debitos negativos.
        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    =>  SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERRR_ACTDEBITOCREDNEGATIVO,
                                    UN_TABLAERROR => MI_TABLA);
    END;

    BEGIN
        MI_TABLA := 'TEMP_PLANA_AJUSTES';

        MI_CAMPOS := 'EJECUCION_DEBITO = VALOR_DEBITO
                     ,EJECUCION_CREDITO = VALOR_CREDITO ';

        MI_CONDICION := ' COMPANIA = '''|| UN_COMPANIA ||'''
                      AND ANO ='|| UN_ANO ||'
                      AND TIPO_CPTE = '''|| UN_TIPOCOMPROBANTE ||'''
                      AND COMPROBANTE = '|| UN_NUMERO ||'
                      AND NVL(CUENTAPPTAL,'' '') <>'' ''  ';

        BEGIN
          MI_FILAS:=PCK_DATOS.FC_ACME( UN_TABLA     =>  MI_TABLA,
                                               UN_ACCION    =>  'M',
                                               UN_CAMPOS    =>  MI_CAMPOS,
                                               UN_CONDICION =>  MI_CONDICION);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
        --Se presentó error al actualizar las ejecuciones créditos y débitos.
        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    =>  SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERRR_ACTEJECUCIONDEBITOCRED,
                                    UN_TABLAERROR => MI_TABLA);
    END;

    IF UN_SIMPLE <> 0 THEN
        BEGIN
            --Se inserta la información agrupada con el tipo de planacierre en 2, luego se elimina la información de planacierre que tenga en 1.
            MI_TABLA := 'TEMP_PLANA_AJUSTES';

            MI_MERGEUSING := ' SELECT LOTE, COMPANIA, ANO, TIPO_CPTE, COMPROBANTE, CUENTA,
                                      MAX(CONSECUTIVO) AS CONSECUTIVO, FECHA, NATURALEZA, CODIGO_PPTAL,
                                      DESCRIPCION, SUM(VALOR_DEBITO) AS SUMADEVALOR_DEBITO,
                                      SUM(VALOR_CREDITO) AS SUMADEVALOR_CREDITO,
                                      SUM(DEBITO_EQUIV) AS SUMADEDEBITO_EQUIV,
                                      SUM(CREDITO_EQUIV) AS SUMADECREDITO_EQUIV,
                                      CASE WHEN SUM(VALOR_DEBITO - VALOR_CREDITO) >0 THEN SUM(VALOR_DEBITO - VALOR_CREDITO) ELSE 0 END AS SUMADEEJECUCION_DEBITO,
                                      CASE WHEN SUM(VALOR_CREDITO - VALOR_DEBITO) >0 THEN SUM(VALOR_CREDITO - VALOR_DEBITO) ELSE 0 END AS SUMADEEJECUCION_CREDITO,
                                      SUM(BASE_GRAVABLE) AS BASE_GRAVABLE, TIPO_DOCUMENTO, NRO_DOCUMENTO, NRO_DOCUMENTO_2, CENTRO_COSTO, TERCERO, SUCURSAL, AUXILIAR,
                                      TIPO_CPTE_AFECT, CMPTE_AFECTADO, CHEQUEPARAANULAR, CIERRE, BASE_IVA, PAGADOBANCO,
                                      CUENTAPPTAL, REFERENCIA,FUENTE_RECURSOS, FECHA_CONSIGNACIONPLANO, NOMBRE_FUNCIONARIO
                                      '|| CASE WHEN UN_ALMDEP <> 0 THEN ', D_DEPENDENCIACNT ' ELSE  ' ' END ||'
                               FROM   TEMP_PLANA_AJUSTES
                               WHERE  COMPANIA= '''|| UN_COMPANIA ||'''
                                 AND  ANO = '|| UN_ANO ||'
                                 AND  TIPO_CPTE = '''|| UN_TIPOCOMPROBANTE ||'''
                                 AND  COMPROBANTE = '|| UN_NUMERO ||'
                                 AND  CONSECUTIVO >= 0
                                 AND  PLANACIERRE = 1
                               GROUP BY LOTE, COMPANIA, ANO, TIPO_CPTE, COMPROBANTE, CUENTA,
                                        FECHA, NATURALEZA,
                                        '|| CASE WHEN UN_NONETEA = 0 THEN 'CASE WHEN SUBSTR(CUENTA,1,2) = ''11'' THEN '' '' ELSE CASE WHEN VALOR_DEBITO <>0 THEN ''D'' ELSE ''C'' END END,' ELSE '' END ||'
                                        CODIGO_PPTAL, DESCRIPCION,TIPO_DOCUMENTO,
                                        NRO_DOCUMENTO, NRO_DOCUMENTO_2, CENTRO_COSTO, TERCERO, SUCURSAL,
                                        AUXILIAR, TIPO_CPTE_AFECT, CMPTE_AFECTADO,
                                        CHEQUEPARAANULAR, CIERRE, BASE_IVA, PAGADOBANCO,
                                        CUENTAPPTAL, REFERENCIA,FUENTE_RECURSOS, FECHA_CONSIGNACIONPLANO, NOMBRE_FUNCIONARIO
                                        '|| CASE WHEN UN_ALMDEP <> 0 THEN ', D_DEPENDENCIACNT ' ELSE  ' ' END ||'  ';

            MI_MERGEENLACE := ' TABLA.COMPANIA    = VISTA.COMPANIA
                            AND TABLA.ANO         = VISTA.ANO
                            AND TABLA.TIPO_CPTE   = VISTA.TIPO_CPTE
                            AND TABLA.COMPROBANTE = VISTA.COMPROBANTE
                            AND TABLA.PLANACIERRE = 2  ';

            MI_MERGENOEXISTE :='INSERT (LOTE,COMPANIA, ANO, TIPO_CPTE, COMPROBANTE,CUENTA,
                                        CONSECUTIVO, FECHA, NATURALEZA,CODIGO_PPTAL,
                                        DESCRIPCION,VALOR_DEBITO,
                                        VALOR_CREDITO,
                                        DEBITO_EQUIV,
                                        CREDITO_EQUIV,
                                        EJECUCION_DEBITO,
                                        EJECUCION_CREDITO,
                                        BASE_GRAVABLE, TIPO_DOCUMENTO, NRO_DOCUMENTO, NRO_DOCUMENTO_2, CENTRO_COSTO, TERCERO, SUCURSAL, AUXILIAR,
                                        TIPO_CPTE_AFECT,CMPTE_AFECTADO, CHEQUEPARAANULAR ,  CIERRE , BASE_IVA, PAGADOBANCO,
                                        CUENTAPPTAL, REFERENCIA, FUENTE_RECURSOS,FECHA_CONSIGNACIONPLANO,NOMBRE_FUNCIONARIO
                                        '|| CASE WHEN UN_ALMDEP <> 0 THEN ', D_DEPENDENCIACNT ' ELSE  ' ' END ||', PLANACIERRE )
                                VALUES (VISTA.LOTE, VISTA.COMPANIA, VISTA.ANO, VISTA.TIPO_CPTE, VISTA.COMPROBANTE,VISTA.CUENTA,
                                        VISTA.CONSECUTIVO, VISTA.FECHA, VISTA.NATURALEZA,VISTA.CODIGO_PPTAL,
                                        VISTA.DESCRIPCION, VISTA.SUMADEVALOR_DEBITO,
                                        VISTA.SUMADEVALOR_CREDITO,
                                        VISTA.SUMADEDEBITO_EQUIV,
                                        VISTA.SUMADECREDITO_EQUIV,
                                        VISTA.SUMADEEJECUCION_DEBITO,
                                        VISTA.SUMADEEJECUCION_CREDITO,
                                        VISTA.BASE_GRAVABLE, VISTA.TIPO_DOCUMENTO, VISTA.NRO_DOCUMENTO, VISTA.NRO_DOCUMENTO_2, 
                                        VISTA.CENTRO_COSTO, VISTA.TERCERO, VISTA.SUCURSAL, VISTA.AUXILIAR,
                                        VISTA.TIPO_CPTE_AFECT, VISTA.CMPTE_AFECTADO, VISTA.CHEQUEPARAANULAR , VISTA.CIERRE , VISTA.BASE_IVA, VISTA.PAGADOBANCO,
                                        VISTA.CUENTAPPTAL,  VISTA.REFERENCIA,VISTA.FUENTE_RECURSOS, VISTA.FECHA_CONSIGNACIONPLANO, VISTA.NOMBRE_FUNCIONARIO
                                        '|| CASE WHEN UN_ALMDEP <> 0 THEN ', VISTA.D_DEPENDENCIACNT ' ELSE  ' ' END ||' ,2)   ';

            BEGIN
                MI_FILAS := PCK_DATOS.FC_ACME(UN_ACCION   => 'IN',
                                         UN_TABLA       => MI_TABLA,
                                         UN_MERGEUSING  => MI_MERGEUSING,
                                         UN_MERGEENLACE => MI_MERGEENLACE,
                                         UN_MERGENOEXIS => MI_MERGENOEXISTE);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
            END;

            IF MI_FILAS <= 0 THEN
                MI_RTA := 'Proceso no realizado, No se generaron registros de interface';
                RETURN MI_RTA;
            END IF;

            MI_TABLA := 'TEMP_PLANA_AJUSTES';
            MI_CONDICION := 'COMPANIA= '''|| UN_COMPANIA ||'''
                        AND  ANO = '|| UN_ANO ||'
                        AND  TIPO_CPTE = '''|| UN_TIPOCOMPROBANTE ||'''
                        AND  COMPROBANTE = '|| UN_NUMERO ||'
                        AND  PLANACIERRE = 1 ';
            BEGIN
                MI_FILAS := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                               UN_ACCION    => 'E',
                                               UN_CONDICION => MI_CONDICION);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
            END; 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
            --Se presentó error al agrupar los créditos y débitos en las cuentas y terceros.
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    =>  SQLCODE,
                                        UN_ERROR_COD  => PCK_ERRORES.ERRR_AGRUPARPLANAJUSTE,
                                        UN_TABLAERROR => MI_TABLA);

        END;

    /*ELSE
        //Se dejan los mismos registros en la tabla dado que no se agrupa
        MI_PLANACIERREMES := ' SELECT COMPANIA, ANO, TIPO_CPTE, COMPROBANTE, CONSECUTIVO, CUENTA,  FECHA, NATURALEZA, CODIGO_PPTAL, TIPOPPTAL, NUMEROPPTAL,
                                      CUENTAPPTAL, DESCRIPCION, VALOR_DEBITO, VALOR_CREDITO, DEBITO_EQUIV, CREDITO_EQUIV, EJECUCION_DEBITO, EJECUCION_CREDITO, BASE_GRAVABLE,
                                      TIPO_DOCUMENTO, NRO_DOCUMENTO, CENTRO_COSTO, TERCERO, SUCURSAL, AUXILIAR, TIPO_CPTE_AFECT, CMPTE_AFECTADO,
                                      CHEQUEPARAANULAR,  CIERRE, BASE_IVA, PAGADOBANCO,  CONSECUTIVOPPTO, DESEMBOLSO,
                                      TIPOCONTRATO, NUMEROCONTRATO, SALDOCUENTA, DEBITO_AFECTADO, CREDITO_AFECTADO, CONSECUTIVOAFECTADO,
                                      FECHACONCILIA, FECHA_CONCILIA, REFERENCIA,
                                      FECHA_CONSIGNACIONPLANO, TEXTOD, FECHA_CONCILIACION_PLANO, D_DEPENDENCIACNT
                               FROM   TEMP_PLANA_AJUSTES
                               WHERE  COMPANIA = '''|| UN_COMPANIA ||'''
                                 AND  ANO ='|| UN_ANO ||'
                                 AND  TIPO_CPTE = '''|| UN_TIPOCOMPROBANTE ||'''
                                 AND  COMPROBANTE = '|| UN_NUMERO ||'  ';*/
    END IF;


    MI_NETOSCUENTAS :=  CASE WHEN  NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                            UN_NOMBRE    =>  'DISTRIBUIR NETOS CUENTAS CONTABLES',
                                                            UN_MODULO    =>  PCK_DATOS.MODULOCONTABILIDAD,
                                                            UN_FECHA_PAR =>  SYSDATE),'NO') = 'SI'
                            OR
                                   NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                             UN_NOMBRE    =>  'DISTRIBUIR NETOS CUENTAS CONTABLES',
                                                             UN_MODULO    =>  PCK_DATOS.MODULOCONTABILIDAD,
                                                             UN_FECHA_PAR =>  SYSDATE),'NO') = ' '
                            THEN TRUE ELSE FALSE
                        END;

    /*IF MI_PARDISTNETOSCCONTA THEN
        IF MI_PARDISTNETOSCCONTAORD THEN
            --Falta el campo CONCEPTOSNOM.CUENTA_ORDEN_DISTRIBUCION
        ELSE
            MI_RTAGENERAL := FC_NETEAR_NETOS_NOMINA
                                (UN_COMPANIA   => UN_COMPANIA
                                ,UN_ANO        => UN_ANO
                                ,UN_TIPO       => UN_TIPOCOMPROBANTE
                                ,UN_NUMERO     => UN_NUMERO);
        END IF;
    END IF;*/
    --Verifica las naturalezas de las cuenta y netea las cuentas

    BEGIN
        MI_TABLA := 'TEMP_PLANA_AJUSTES';

        MI_CAMPOS := ' VALOR_DEBITO  = (TRUNC((CASE WHEN NATURALEZA = ''D'' AND (VALOR_DEBITO - VALOR_CREDITO) > 0 THEN (VALOR_DEBITO - VALOR_CREDITO) ELSE 0 END + CASE WHEN NATURALEZA <> ''D'' AND (VALOR_CREDITO - VALOR_DEBITO) < 0  THEN (VALOR_DEBITO - VALOR_CREDITO) ELSE 0 END) * 100 + 0.501) / 100)
                      ,VALOR_CREDITO = (TRUNC((CASE WHEN NATURALEZA <> ''D'' AND (VALOR_CREDITO-VALOR_DEBITO) > 0 THEN (VALOR_CREDITO - VALOR_DEBITO) ELSE  0 END + CASE WHEN NATURALEZA = ''D'' AND (VALOR_DEBITO - VALOR_CREDITO) < 0 THEN (VALOR_CREDITO - VALOR_DEBITO) ELSE 0 END)  * 100 + 0.501) / 100) ';

        MI_CONDICION := ' COMPANIA = '''|| UN_COMPANIA ||'''
                      AND ANO =  '|| UN_ANO ||'
                      AND TIPO_CPTE = '''|| UN_TIPOCOMPROBANTE || '''
                      AND COMPROBANTE = '|| UN_NUMERO ||' ';

        BEGIN
            MI_FILAS:=PCK_DATOS.FC_ACME( UN_TABLA     =>  MI_TABLA,
                                                 UN_ACCION    =>  'M',
                                                 UN_CAMPOS    =>  MI_CAMPOS,
                                                 UN_CONDICION =>  MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
        --Error al netear las naturalezas de las cuentas débito y crédito.
        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    =>  SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERRR_ACTNETEANATURALEZA,
                                    UN_TABLAERROR => MI_TABLA);
    END;

    BEGIN
        SELECT SUM(VALOR_DEBITO) ,SUM(VALOR_CREDITO)
        INTO   MI_SVALOR_DEBITO,MI_SVALOR_CREDITO
        FROM   TEMP_PLANA_AJUSTES
        WHERE  COMPANIA = UN_COMPANIA
          AND  ANO = UN_ANO
          AND  TIPO_CPTE = UN_TIPOCOMPROBANTE
          AND  COMPROBANTE = UN_NUMERO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_SVALOR_DEBITO := 0;
        MI_SVALOR_CREDITO := 0;
    END;


    IF ABS(TRUNC(MI_SVALOR_DEBITO * 100) / 100 - TRUNC(MI_SVALOR_CREDITO * 100) / 100) > 2 AND  MI_COMPDESCUADRADO = FALSE THEN
        BEGIN
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
            --Comprobante Descuadrado, Revise e intente de Nuevo.
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                        UN_ERROR_COD  => PCK_ERRORES.ERRR_COMPDESCUADRADO,
                                        UN_TABLAERROR => MI_TABLA);
        END;
    ELSE
        MI_VALORDOCUMENTO := TRUNC(MI_SVALOR_DEBITO * 100) / 100;
        MI_EXISTENDATOS := 0;
        BEGIN
            SELECT COUNT(0) CUENTA
            INTO   MI_EXISTENDATOS
            FROM COMPROBANTE_PPTAL INNER JOIN TIPO_COMPROBPP
          		ON  COMPROBANTE_PPTAL.COMPANIA = TIPO_COMPROBPP.COMPANIA
          		AND COMPROBANTE_PPTAL.TIPO = TIPO_COMPROBPP.CODIGO
            WHERE COMPROBANTE_PPTAL.COMPANIA   = UN_COMPANIA
              AND COMPROBANTE_PPTAL.ANO        = UN_ANO
              AND COMPROBANTE_PPTAL.TIPO       = UN_TIPOCOMPROBANTE
              AND COMPROBANTE_PPTAL.NUMERO     = UN_NUMERO;

        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_EXISTENDATOS := 0;
        END;

        IF MI_EXISTENDATOS > 0 THEN --Elimina el comprobante presupuestal.
            PCK_CONTABILIDAD1.PR_ELIMINARCOMPROBANTEPPTAL
                            ( UN_COMPANIA    => UN_COMPANIA
                             ,UN_ANIO        => UN_ANO
                             ,UN_TIPO        => UN_TIPOCOMPROBANTE
                             ,UN_NUMERO      => UN_NUMERO
                             ,UN_USUARIO     => UN_USUARIO);
        END IF;

        MI_DIASVENCIMIENTO := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                                  UN_NOMBRE    =>  'DIAS VENCIMIENTO COMPROBANTE CONTABLE',
                                                                  UN_MODULO    =>  PCK_DATOS.MODULOCONTABILIDAD,
                                                                  UN_FECHA_PAR =>  SYSDATE),'0'));

        -- INI TICKET7745545 MPEREZ
        BEGIN
            SELECT SF_OBJETO_COBRO.FECHA_VENCIMIENTO
              INTO MI_FECHAVENCIMIENTO
              FROM SF_OBJETO_COBRO
             WHERE SF_OBJETO_COBRO.COMPANIA   = UN_COMPANIA
               AND SF_OBJETO_COBRO.TIPOCOBRO    = UN_TIPOCOMPROBANTE
               AND SF_OBJETO_COBRO.ANO    = UN_ANO
               AND SF_OBJETO_COBRO.CODIGO_COBRO = UN_NUMERO;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_FECHAVENCIMIENTO := NULL;
        END;

        IF MI_FECHAVENCIMIENTO IS NULL THEN
            MI_FECHAVENCIMIENTO := TO_DATE(TO_CHAR(UN_FECHA + MI_DIASVENCIMIENTO,'DD/MM/YYYY'),'DD/MM/YYYY');
        END IF;
        -- FIN TICKET7745545 MPEREZ
--7742952_SIGEC 
    IF MI_PARMANJSIGEC = 'SI' THEN								  
BEGIN
        SELECT
                    SF_OBJETO_COBRO.NROCONTRATOSIGEC,
                    SF_OBJETO_COBRO.TIPOCONTRATOSIGEC
                   INTO MI_NROCONTRATOSIGEC, MI_TIPOCONTRATOSIGEC
             FROM SF_OBJETO_COBRO
              WHERE SF_OBJETO_COBRO.COMPANIA   = UN_COMPANIA
                  AND SF_OBJETO_COBRO.TIPOCOBRO    = UN_TIPOCOMPROBANTE
                  AND SF_OBJETO_COBRO.ANO    = UN_ANO
                  AND SF_OBJETO_COBRO.CODIGO_COBRO = UN_NUMERO
                  AND SF_OBJETO_COBRO.TERCERO      = UN_TERCERO;
      EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_NROCONTRATOSIGEC := NULL;
            MI_TIPOCONTRATOSIGEC := NULL;
      END;
    END IF;  
        IF UN_CONTRATISTA = 0 THEN
            BEGIN
                MI_TABLA := 'COMPROBANTE_CNT';
                MI_CAMPOS := ' COMPANIA
                              ,ANO
                              ,TIPO
                              ,NUMERO
                              ,FECHA
                              ,DESCRIPCION
                              ,TERCERO
                              ,SUCURSAL
                              ,CENTRO_COSTO
                              ,AUXILIAR
                              ,FUENTE_RECURSO
                              ,REFERENCIA
                              ,VLR_BASE
                              ,ANULADO
                              ,VLR_DOCUMENTO
                              ,TEXTO
                              ,NUMEROCONTRATO
                              ,TIPOCONTRATO
                              ,VLRAGIRAR
                              ,NRO_DOCUMENTO
                              ,NRO_DOCUMENTO_2
                              ,FECHA_VCN_DOC
                              ,CREATED_BY
                              ,DATE_CREATED ';

                MI_VALORES := ' '''|| UN_COMPANIA ||'''
                              , '|| UN_ANO ||'
                              , '''|| UN_TIPOCOMPROBANTE ||'''
                              , '|| UN_NUMERO ||'
                              , TO_DATE('''|| TO_CHAR(UN_FECHA,'DD/MM/YYYY') ||''',''DD/MM/YYYY'')
                              , '''|| SUBSTR(UN_DESCRIPCION,1,250) ||'''
                              ,'''|| UN_TERCERO ||'''
                              ,'''|| UN_SUCURSAL ||'''
                              ,'''|| UN_CENTRO_COSTO ||'''
                              ,'''|| UN_AUXILIAR ||'''
                              ,'''|| UN_FUENTE_RECURSO ||'''
                              ,'''|| UN_REFERENCIA ||'''
                              ,'|| MI_VALORDOCUMENTO ||'
                              , 0
                              ,'|| MI_VALORDOCUMENTO ||'
                              ,'''|| NVL(UN_TEXTO,' ') || ''' 
                              ,'|| CASE WHEN '''' ||MI_PARMANJSIGEC|| '''' = 'SI' THEN MI_NROCONTRATOSIGEC ELSE NVL(UN_CONTRATO, 0) END ||'
                              ,'''|| CASE WHEN '''' ||MI_PARMANJSIGEC|| '''' = 'SI' THEN MI_TIPOCONTRATOSIGEC ELSE NVL(UN_TIPOCONTRATO, '') END ||'''
                              ,'|| MI_VALORDOCUMENTO ||'
                              ,'''|| UN_NRO_DOCUMENTO ||'''
                              ,'''|| UN_NRO_DOCUMENTO_2 ||'''
                              ,TO_DATE('''|| TO_CHAR(MI_FECHAVENCIMIENTO,'DD/MM/YYYY') ||''',''DD/MM/YYYY'')
                              ,'''|| UN_USUARIO ||'''
                              ,SYSDATE ';

                BEGIN
                    MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                  UN_ACCION  => 'I',
                                                  UN_CAMPOS  => MI_CAMPOS,
                                                  UN_VALORES => MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    =>  SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_INSHEADERCOMPINTERFAZ,
                                            UN_TABLAERROR => MI_TABLA);
            END;
        ELSE  --Orden de compra
            MI_EXISTEORDCOMPRA := TRUE;
            BEGIN
                SELECT NUMORD, CLASEORDEN
                INTO   MI_NUMEROORDCOMPRA, MI_CLASEORDCOMPRA
                FROM
                (   SELECT TO_NUMBER(NUMERO) NUMORD, CLASEORDEN
                	FROM   ORDENDECOMPRA
                	WHERE  COMPANIA = UN_COMPANIA
                	  AND  ESTADO = 'V'
                	  AND  TERCERO = UN_TERCE
                	  AND  SUCURSAL = UN_SUCURSAL
                	  AND  CASE WHEN FECHAFIRMA < FECHA THEN FECHAFIRMA ELSE FECHA END	<= UN_FECHA
                	ORDER BY FECHA DESC
                )
                WHERE ROWNUM = 1;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_NUMEROORDCOMPRA := 0;
                MI_CLASEORDCOMPRA := '';
                MI_EXISTEORDCOMPRA := FALSE;
            END;

            BEGIN
                MI_TABLA := 'COMPROBANTE_CNT';
                IF MI_EXISTEORDCOMPRA THEN
                    MI_LSDESC := 'PAGO ' || MI_CLASEORDCOMPRA || ' No. '|| MI_NUMEROORDCOMPRA ||' POR SERVICIOS PROFESIONALES COMO ';
                ELSE
                    MI_LSDESC := UN_DESCRIPCION;
                END IF;
-- AQUI VOY 

                MI_CAMPOS := '  COMPANIA
                               ,ANO
                               ,TIPO
                               ,NUMERO
                               ,FECHA
                               ,DESCRIPCION
                               ,TERCERO
                               ,SUCURSAL
                               ,CENTRO_COSTO
                               ,AUXILIAR
                               ,FUENTE_RECURSO
                               ,REFERENCIA
                               ,VLR_BASE
                               ,ANULADO
                               ,VLR_DOCUMENTO
                               ,TEXTO
                               ,NUMEROCONTRATO
                               ,TIPOCONTRATO
                               ,VLRAGIRAR
                               ,CREATED_BY
                               ,DATE_CREATED ' ;

                MI_VALORES := '   '''|| UN_COMPANIA ||'''
                                , '|| UN_ANO ||'
                                , '''|| UN_TIPOCOMPROBANTE ||'''
                                , '|| UN_NUMERO ||'
                                , TO_DATE('''|| TO_CHAR(UN_FECHA,'DD/MM/YYYY') ||''',''DD/MM/YYYY'')
                                , '''|| SUBSTR(MI_LSDESC,1,250) ||'''
                                ,'''|| UN_TERCERO ||'''
                                ,'''|| UN_SUCURSAL ||'''
                                ,'''|| UN_CENTRO_COSTO ||'''
                                ,'''|| UN_AUXILIAR ||'''
                                ,'''|| UN_FUENTE_RECURSO ||'''
                                ,'''|| UN_REFERENCIA ||'''
                                , 0
                                , 0
                                , '|| MI_VALORDOCUMENTO ||'
                                , '|| CASE WHEN UN_TEXTO IS NULL THEN '' ELSE '''' || UN_TEXTO || '''' END  ||'
                                , '||  CASE WHEN MI_EXISTEORDCOMPRA THEN MI_CLASEORDCOMPRA ELSE
                                       CASE WHEN '''' ||MI_PARMANJSIGEC|| '''' = 'SI' THEN MI_NROCONTRATOSIGEC ELSE UN_CONTRATO END END ||'
                                ,'''|| CASE WHEN MI_EXISTEORDCOMPRA THEN MI_CLASEORDCOMPRA ELSE
                                       CASE WHEN '''' ||MI_PARMANJSIGEC|| '''' = 'SI' THEN MI_TIPOCONTRATOSIGEC ELSE UN_TIPOCONTRATO END END ||'''
                                , '|| MI_VALORDOCUMENTO ||'
								, '''|| UN_USUARIO ||'''
                                , SYSDATE  ';

                IF MI_EXISTEORDCOMPRA THEN
                    MI_CAMPOS := MI_CAMPOS || ',NRO_DOCUMENTO
                                               ,FECHA_VCN_DOC';

                    MI_VALORES := MI_VALORES || ', ''PAGO '|| MI_CLASEORDCOMPRA ||' No. '|| MI_NUMEROORDCOMPRA ||'''
                                                 , TO_DATE('''|| TO_CHAR(UN_FECHA,'DD/MM/YYYY') ||''',''DD/MM/YYYY'')
                        ';
                END IF;

                BEGIN
                    MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                  UN_ACCION  => 'I',
                                                  UN_CAMPOS  => MI_CAMPOS,
                                                  UN_VALORES => MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    =>  SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_INSHEADERCOMPINTERFAZORD,
                                            UN_TABLAERROR => MI_TABLA);
            END;

        END IF;

        IF MI_FILAS <= 0 THEN
            MI_RTA := 'Error al grabar el Header del Comprobante, Revise e intente de Nuevo.';
            RETURN MI_RTA;
        END IF;

        MI_DBLGIRAR := PCK_SYSMAN_UTL.FC_ROUND(PCK_CONTABILIDAD1.FC_CALCULARVLRGIRAR
                        (UN_COMPANIA    => UN_COMPANIA
                        ,UN_ANIO        => UN_ANO
                        ,UN_TIPO        => UN_TIPOCOMPROBANTE
                        ,UN_NUMERO      => UN_NUMERO
                        ,UN_CLASE       => MI_CLASE
                        ,UN_VALORAGIRAR => 0 ), 2) ;

        IF MI_DBLGIRAR <> 0 THEN
            BEGIN
                MI_TABLA := 'COMPROBANTE_CNT';

                MI_CAMPOS := ' VLRAGIRAR = '|| MI_DBLGIRAR ||'
                              ,MODIFIED_BY = '''|| UN_USUARIO ||'''
                              ,DATE_MODIFIED = SYSDATE ';

                MI_CONDICION := ' COMPANIA = '''|| UN_COMPANIA ||'''
                              AND ANO = '|| UN_ANO ||'
                              AND TIPO = '''|| UN_TIPOCOMPROBANTE ||'''
                              AND NUMERO = '|| UN_NUMERO ||'  ';
                BEGIN
                    MI_FILAS := PCK_DATOS.FC_ACME( UN_TABLA     =>  MI_TABLA,
                                                       UN_ACCION    =>  'M',
                                                       UN_CAMPOS    =>  MI_CAMPOS,
                                                       UN_CONDICION =>  MI_CONDICION);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    =>  SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_BORRARDETCOMPRINTERFAZ,
                                            UN_TABLAERROR => MI_TABLA);
            END;
        END IF;


        BEGIN
            MI_TABLA := 'DETALLE_COMPROBANTE_CNT';

            MI_MERGEUSING := '  SELECT  LOTE, COMPANIA, ANO, TIPO_CPTE, COMPROBANTE, CONSECUTIVO, CUENTA,
                                        FECHA, NATURALEZA, CODIGO_PPTAL, TIPOPPTAL,
                                        NUMEROPPTAL, CUENTAPPTAL, DESCRIPCION, VALOR_DEBITO, VALOR_CREDITO,
                                        DEBITO_EQUIV, CREDITO_EQUIV, EJECUCION_DEBITO, EJECUCION_CREDITO,
                                        BASE_GRAVABLE, TIPO_DOCUMENTO, NRO_DOCUMENTO, NRO_DOCUMENTO_2, CENTRO_COSTO, TERCERO,
                                        SUCURSAL, AUXILIAR, TIPO_CPTE_AFECT, CMPTE_AFECTADO,
                                        CHEQUEPARAANULAR, CIERRE, BASE_IVA, PAGADOBANCO,CONSECUTIVOPPTO,
                                        DESEMBOLSO, TIPOCONTRATO, NUMEROCONTRATO, SALDOCUENTA, DEBITO_AFECTADO,
                                        CREDITO_AFECTADO, CONSECUTIVOAFECTADO,FECHACONCILIA, FECHA_CONCILIA,
                                        REFERENCIA, FECHA_CONSIGNACIONPLANO, TEXTOD, FECHA_CONCILIACION_PLANO,
                                        D_DEPENDENCIACNT,FUENTE_RECURSOS, NUMEROPROCESO,EQUIV_SIGEC, NOMBRE_FUNCIONARIO
                                FROM   TEMP_PLANA_AJUSTES
                                WHERE  COMPANIA = '''|| UN_COMPANIA ||'''
                                  AND  ANO = '|| UN_ANO ||'
                                  AND  TIPO_CPTE = '''|| UN_TIPOCOMPROBANTE ||'''
                                  AND  COMPROBANTE = '|| UN_NUMERO ||'
                                  AND  NATURALEZA IS NOT NULL ';

            MI_MERGEENLACE := ' TABLA.COMPANIA    = VISTA.COMPANIA
                            AND TABLA.ANO         = VISTA.ANO
                            AND TABLA.TIPO_CPTE   = VISTA.TIPO_CPTE
                            AND TABLA.COMPROBANTE = VISTA.COMPROBANTE ';

            MI_MERGENOEXISTE := 'INSERT (LOTE,COMPANIA, ANO, TIPO_CPTE, COMPROBANTE
                                        ,CONSECUTIVO,CUENTA, FECHA, NATURALEZA
                                        ,CODIGO_PPTAL, TIPOPPTAL,NUMEROPPTAL,CUENTAPPTAL
                                        ,DESCRIPCION,VALOR_DEBITO,VALOR_CREDITO,DEBITO_EQUIV
                                        ,CREDITO_EQUIV, EJECUCION_DEBITO, EJECUCION_CREDITO
                                        ,BASE_GRAVABLE, TIPO_DOCUMENTO, NRO_DOCUMENTO, NRO_DOCUMENTO_2
                                        ,CENTRO_COSTO, TERCERO, SUCURSAL, AUXILIAR
                                        ,TIPO_CPTE_AFECT, CMPTE_AFECTADO,CHEQUEPARAANULAR
                                        ,CIERRE, BASE_IVA, PAGADOBANCO, CONSECUTIVOPPTO
                                        ,DESEMBOLSO, TIPOCONTRATO, NUMEROCONTRATO, SALDOCUENTA
                                        ,DEBITO_AFECTADO, CREDITO_AFECTADO, CONSECUTIVOAFECTADO
                                        ,FECHACONCILIA, FECHA_CONCILIA, REFERENCIA, FECHA_CONSIGNACIONPLANO
                                        ,TEXTOD, FECHA_CONCILIACION_PLANO, D_DEPENDENCIACNT,FUENTE_RECURSO, NUMEROPROCESO,EQUIV_SIGEC, NOMBRE_FUNCIONARIO
                                        ,CREATED_BY, DATE_CREATED
                                        )
                                 VALUES (VISTA.LOTE, VISTA.COMPANIA, VISTA.ANO, VISTA.TIPO_CPTE, VISTA.COMPROBANTE
                                        ,VISTA.CONSECUTIVO, VISTA.CUENTA, VISTA.FECHA, VISTA.NATURALEZA
                                        ,VISTA.CODIGO_PPTAL, VISTA.TIPOPPTAL,VISTA.NUMEROPPTAL,VISTA.CUENTAPPTAL
                                        ,VISTA.DESCRIPCION, VISTA.VALOR_DEBITO, VISTA.VALOR_CREDITO, VISTA.DEBITO_EQUIV
                                        ,VISTA.CREDITO_EQUIV, VISTA.EJECUCION_DEBITO, VISTA.EJECUCION_CREDITO
                                        ,VISTA.BASE_GRAVABLE, VISTA.TIPO_DOCUMENTO, VISTA.NRO_DOCUMENTO, VISTA.NRO_DOCUMENTO_2
                                        ,VISTA.CENTRO_COSTO, VISTA.TERCERO, VISTA.SUCURSAL, VISTA.AUXILIAR
                                        ,VISTA.TIPO_CPTE_AFECT, VISTA.CMPTE_AFECTADO, VISTA.CHEQUEPARAANULAR
                                        ,VISTA.CIERRE, VISTA.BASE_IVA, VISTA.PAGADOBANCO, VISTA.CONSECUTIVOPPTO
                                        ,VISTA.DESEMBOLSO, 
                                        CASE WHEN '''||MI_PARMANJSIGEC||''' = ''SI'' THEN '''|| NVL(MI_TIPOCONTRATOSIGEC, '')||''' ELSE VISTA.TIPOCONTRATO END,
                                        CASE WHEN '''||MI_PARMANJSIGEC||''' = ''SI'' THEN '||NVL(MI_NROCONTRATOSIGEC, 0)||' ELSE VISTA.NUMEROCONTRATO END, VISTA.SALDOCUENTA
                                        ,VISTA.DEBITO_AFECTADO, VISTA.CREDITO_AFECTADO, VISTA.CONSECUTIVOAFECTADO
                                        ,VISTA.FECHACONCILIA, VISTA.FECHA_CONCILIA, VISTA.REFERENCIA, VISTA.FECHA_CONSIGNACIONPLANO
                                        ,VISTA.TEXTOD, VISTA.FECHA_CONCILIACION_PLANO, VISTA.D_DEPENDENCIACNT , VISTA.FUENTE_RECURSOS, VISTA.NUMEROPROCESO,VISTA.CUENTAPPTAL, VISTA.NOMBRE_FUNCIONARIO
                                        ,'''|| UN_USUARIO ||''', SYSDATE
                                        )
                                ';

            BEGIN
                MI_FILAS := PCK_DATOS.FC_ACME(UN_ACCION   => 'IN',
                                           UN_TABLA       => MI_TABLA,
                                           UN_MERGEUSING  => MI_MERGEUSING,
                                           UN_MERGEENLACE => MI_MERGEENLACE,
                                           UN_MERGENOEXIS => MI_MERGENOEXISTE);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
            END;

            IF MI_FILAS <= 0 THEN
                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
            END IF;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
            --Se presentaron inconsistencias al grabar el detalle del comprobante <br> Tipo: --TIPOCPE--  Número: --COMPROBANTE--  <br> Por Favor verifique.
            MI_MSGERROR(1).CLAVE := 'TIPOCPE';
            MI_MSGERROR(1).VALOR := UN_TIPOCOMPROBANTE;
            MI_MSGERROR(2).CLAVE := 'COMPROBANTE';
            MI_MSGERROR(2).VALOR := UN_NUMERO;

            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                        UN_ERROR_COD  => PCK_ERRORES.ERRR_INSDETALLECOMPINTERFAZ ,
                                        UN_TABLAERROR => MI_TABLA,
                                        UN_REEMPLAZOS => MI_MSGERROR);
        END;

        BEGIN
            SELECT NVL(LISTAGG(CUENTA, ', ') WITHIN GROUP (ORDER BY CONSECUTIVO),' ') LISTA_CNT
            INTO   MI_CUENTASSINNATU
            FROM   TEMP_PLANA_AJUSTES
            WHERE  COMPANIA = UN_COMPANIA
              AND  ANO  = UN_ANO
              AND  TIPO_CPTE = UN_TIPOCOMPROBANTE
              AND  COMPROBANTE = UN_NUMERO
              AND  NATURALEZA IS NULL;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_CUENTASSINNATU := ' ';
        END;

        IF NVL(MI_CUENTASSINNATU, ' ') <> ' ' THEN
            MI_RTA := 'La(s) cuenta(s) '|| MI_CUENTASSINNATU  ||' es incorrecta, Por favor verifique la configuraciòn.';
        END IF;

        IF UN_INTOMITIRPPTAL = 0 AND MI_CLASE IN ('C','I','N','S','B','R','J','V') THEN
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
                            AND D.ANO       = UN_ANO
                            AND D.TIPO_CPTE = UN_TIPOCOMPROBANTE
                            AND D.COMPROBANTE = UN_NUMERO
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
            /*
            BEGIN
                SELECT NVL(LISTAGG(CONSECUTIVO || ',' || CUENTA || ',' || VALOR || ',' || RUBRO_PPTAL ,  ';') WITHIN GROUP (ORDER BY CONSECUTIVO),' ') LISTA_CNT
                      ,COUNT(CONSECUTIVO) CUENTA
                INTO  MI_CADENAINSERTAR, MI_CUENTADETALLE
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
                      AND D.ANO       = UN_ANO
                      AND D.TIPO_CPTE = UN_TIPOCOMPROBANTE
                      AND D.COMPROBANTE = UN_NUMERO
                    GROUP BY D.COMPANIA, D.TIPO_CPTE, D.COMPROBANTE, D.CONSECUTIVO, P.CODIGO,
                    CASE WHEN P.NATURALEZA='D' THEN D.VALOR_DEBITO - D.VALOR_CREDITO ELSE D.VALOR_CREDITO - D.VALOR_DEBITO END
                    ORDER BY CONSECUTIVO) ;


            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_CADENAINSERTAR := ' ';
                MI_CUENTADETALLE := 0;
            END;
            */

            IF MI_CUENTADETALLE > 0 AND MI_COMPINTERFASEDIAR <> UN_TIPOCOMPROBANTE AND MI_COMPINTERFASEMENS <> UN_TIPOCOMPROBANTE THEN
                PCK_CONTABILIDAD1.PR_GENERARCOMPROBANTEPPTAL
                        (UN_COMPANIA        => UN_COMPANIA
                        ,UN_ANO             => UN_ANO
                        ,UN_TIPO            => UN_TIPOCOMPROBANTE
                        ,UN_NUMERO          => UN_NUMERO
                        ,UN_FECHA           => UN_FECHA
                        ,UN_TERCERO         => UN_TERCERO
                        ,UN_SUCURSAL        => UN_SUCURSAL
                        ,UN_DESCRIPCION     => UN_DESCRIPCION
                        ,UN_NUMERODOC       => UN_NRO_DOCUMENTO
                        ,UN_VALORDOC        => MI_VALORDOCUMENTO
                        ,UN_TIPOPPTAL       => UN_TIPOCOMPROBANTE
                        ,UN_CADENAINSERTAR  => MI_CADENAINSERTAR
                        ,UN_CANTIDAD        => MI_CUENTADETALLE
                        ,UN_USUARIO         => UN_USUARIO
                        ,UN_DESDEINTERFAZ   => -1  );
            END IF;
        END IF;

        IF UN_CONCILIAR <> 0 AND MI_CLASE IN('S','E','B','G','D','A','L','I') THEN
            BEGIN
                MI_TABLA     := 'DETALLE_COMPROBANTE_CNT';   --Tabla
                MI_MERGEUSING := 'SELECT COMPANIA,ANO,CODIGO, CLASECUENTA
                                  FROM   PLAN_CONTABLE
                                  WHERE  COMPANIA = '''|| UN_COMPANIA ||'''
                                    AND  ANO = '|| UN_ANO ||'
                                    AND  CLASECUENTA IN (''B'',''J'') ';

                MI_MERGEENLACE := ' TABLA.COMPANIA     = VISTA.COMPANIA
                                 AND TABLA.ANO         = VISTA.ANO
                                 AND TABLA.CUENTA      = VISTA.CODIGO
                                 AND TABLA.TIPO_CPTE   = '''|| UN_TIPOCOMPROBANTE ||'''
                                 AND TABLA.COMPROBANTE = '|| UN_NUMERO ||' ';

                MI_MERGEEXISTE := ' UPDATE SET  TABLA.PAGADOBANCO     = -1
                                               ,TABLA.FECHA_CONCILIA  = LAST_DAY(TO_DATE('''|| TO_CHAR(UN_FECHA,'DD/MM/YYYY') ||''',''DD/MM/YYYY'')) 
                                               ,MODIFIED_BY           = '''|| UN_USUARIO ||'''
                                               ,CONCILIADOR           = '''|| UN_USUARIO ||'''
                                               ,DATE_MODIFIED         = CURRENT_DATE';
                BEGIN
                    MI_FILAS := PCK_DATOS.FC_ACME(UN_ACCION   => 'MM',
                                               UN_TABLA       => MI_TABLA,
                                               UN_MERGEUSING  => MI_MERGEUSING,
                                               UN_MERGEENLACE => MI_MERGEENLACE,
                                               UN_MERGEEXISTE => MI_MERGEEXISTE);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                    RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                END;
            END;
        END IF;

    END IF; --Fin comprobante descuadrado;
    MI_RTA := 'Compania: '|| UN_COMPANIA || ', Tipo de comprobante: '|| UN_TIPOCOMPROBANTE || ', Numero de comprobante: '|| UN_NUMERO;
    RETURN MI_RTA;

END FC_CONTABILIZAR;

--4
FUNCTION FC_CONTABILIZARPORPLANO(
/*
    NAME              : FC_CONTABILIZARPORPLANO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
    DATE MIGRADOR     : 01/02/2018
    TIME              : 05:42 PM
    SOURCE MODULE     : InterfacesPb2018.01.02, En access LlenarTablaPlana
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Función que llena la tabla temporal PlanaAjustes y realiza el proceso
                        de contabilizar, Devuelve un CLOB con las inconsistencias o comprobantes.
    PARAMETERS        :

    MODIFICATIONS     :

    @NAME:contabilizarPorPlano
    @METHOD:  POST
*/

     UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_RESUMIDO         IN PCK_SUBTIPOS.TI_LOGICO
    ,UN_SINPPTAL         IN PCK_SUBTIPOS.TI_LOGICO
    ,UN_TERCERODETALLE   IN PCK_SUBTIPOS.TI_LOGICO
    ,UN_CONCILIAR        IN PCK_SUBTIPOS.TI_LOGICO := 0
    ,UN_PLANO            IN CLOB
    ,UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
) RETURN CLOB

AS
    MI_T_SPLIT		         PCK_SYSMAN_UTL.T_SPLIT;
    MI_CUENTA                PCK_SUBTIPOS.TI_ENTERO;
    MI_RNAUX                 VARCHAR2(2000);
    MI_PARESPACIOSESPPLANO   PARAMETRO.VALOR%TYPE;
    MI_PARAUXILIARAFUENTE    PARAMETRO.VALOR%TYPE;
    MI_PARCREARCOMPPLANO465  BOOLEAN DEFAULT FALSE;
    MI_PARSIMPLIFICAINTER    BOOLEAN DEFAULT FALSE;
    MI_NUM_CAR_DETALLE       PCK_SUBTIPOS.TI_ENTERO;
    MI_RNTIPO                COMPROBANTE_CNT.TIPO%TYPE;
    MI_RNCODIGO_CUENTA       PCK_SUBTIPOS.TI_CODIGOCONTA;
    MI_RNFECHA               DATE;
    MI_FECHAANT              DATE;
    MI_RNFECHACONSIGNACION   DATE;
    MI_MES                   PCK_SUBTIPOS.TI_ENTERO;
    MI_RNBANCO               VARCHAR2(100);
    MI_RNNUMERO              PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT;
    MI_REEMPLAZOS            PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_EXISTEDATO            VARCHAR2(1 CHAR);
    MI_POS                   PCK_SUBTIPOS.TI_ENTERO;
    MI_RNANO                 PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_RNVALORDEBITO         PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
    MI_RNVALORCREDITO        PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
    MI_RNTERCERO             PCK_SUBTIPOS.TI_TERCERO;
    MI_RNSUCURSAL            PCK_SUBTIPOS.TI_SUCURSAL;
    MI_RNCENTRO_COSTO        PCK_SUBTIPOS.TI_CENTRO_COSTO;
    MI_RNAUXILIAR            PCK_SUBTIPOS.TI_AUXILIAR;
    --MROSERO
    MI_RNFUENTERECURSO       PCK_SUBTIPOS.TI_FUENTE_RECURSOS;
    MI_RNREFERENCIA          PCK_SUBTIPOS.TI_REFERENCIA ;
    --MROSERO
    MI_RNNRO_DOCUMENTO       PCK_SUBTIPOS.TI_NRODOCUMENTO;
    MI_RNDESCRIPCION         PCK_SUBTIPOS.TI_DESCRIPCION;
    MI_RNCUENTAPPTAL         PCK_SUBTIPOS.TI_CODIGOPPTAL;
    MI_AUX                   PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_RTA                   CLOB;
    MI_PINCONSISTENCIAS      CLOB;
    MI_INCONSISTENCIA        BOOLEAN;
    MI_CLASE                 PCK_SUBTIPOS.TI_CLASECUENTACONTA;
    MI_CUENTAPPTAL           PLAN_CONTABLE.CUENTA_PPTAL%TYPE;
    MI_NUMREGISTRO           PCK_SUBTIPOS.TI_ENTERO;
    MI_RNCONSECUTIVO         PCK_SUBTIPOS.TI_ENTERO;
    MI_TABLA                 PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICIONACME         PCK_SUBTIPOS.TI_CONDICION;
    MI_FILAS                 PCK_SUBTIPOS.TI_ENTERO;
    MI_VALORES               PCK_SUBTIPOS.TI_VALORES;
    MI_NATURALEZA            PCK_SUBTIPOS.TI_NATURALEZACONTA;
    MI_RS                    SYS_REFCURSOR;
    MI_ETAPA                 VARCHAR2(150 CHAR);
    MI_RESULTADORECORD       VARCHAR2(500 CHAR);
    MI_NITHEADER             VARCHAR2(25 CHAR);
    MI_SUCURSALHEADER        VARCHAR2(25 CHAR);



    MI_NITCONTA              VARCHAR2(25 CHAR);
    MI_SUCURSALCONTA         VARCHAR2(25 CHAR);

    MI_PAR_NOM_FUN_PLA       VARCHAR2(4);
    MI_NOMBRE_FUNCIONARIO    VARCHAR2(44);

    /*MPEREZ TICKET 7723371*/
    MI_RNTEXTO               COMPROBANTE_CNT.TEXTO%TYPE;
	MI_PARNITENTPLANO        PARAMETRO.VALOR%TYPE;
    MI_NUMTERCEROS           NUMBER;								
BEGIN

    BEGIN
        BEGIN
            SELECT NITCOMPANIA
            INTO   MI_NITHEADER
            FROM   COMPANIA
            WHERE  CODIGO = UN_COMPANIA;

            MI_NITHEADER := REPLACE(MI_NITHEADER,'.','');
            IF INSTR(MI_NITHEADER, '-') > 0 THEN
                MI_NITHEADER := SUBSTR(MI_NITHEADER,1,INSTR(MI_NITHEADER, '-') -1);
            END IF;

            SELECT SUCURSAL
            INTO   MI_SUCURSALHEADER
            FROM   TERCERO
            WHERE  COMPANIA = UN_COMPANIA
              AND  NIT = MI_NITHEADER;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
        --No se encontró el tercero correspondiente al nit: --NIT--,Debe configurar el Nit de la entidad como tercero.
        MI_REEMPLAZOS(1).CLAVE := 'NIT';
        MI_REEMPLAZOS(1).VALOR := MI_NITHEADER;

        PCK_ERR_MSG.RAISE_WITH_MSG
                  ( UN_EXC_COD => SQLCODE
                  , UN_TABLAERROR => 'TERCERO'
                  , UN_ERROR_COD  => PCK_ERRORES.ERRR_PLANONITHEADER
                  , UN_REEMPLAZOS => MI_REEMPLAZOS
                  );

    END;

    BEGIN
      MI_PAR_NOM_FUN_PLA :=  NVL(RPAD(PCK_SYSMAN_UTL.FC_PAR(
                                  UN_COMPANIA  =>  UN_COMPANIA,
                                  UN_NOMBRE    =>  'MANEJA NOMBRE FUNCIONARIO EN PLANO CONTABILIZAR',
                                  UN_MODULO    =>  PCK_DATOS.MODULONOMINA,
                                  UN_FECHA_PAR =>  SYSDATE), 2), 'NO');
    END;

    MI_PARAUXILIARAFUENTE := NVL(PCK_SYSMAN_UTL.FC_PAR
                (UN_COMPANIA    => UN_COMPANIA
                ,UN_NOMBRE      => 'EN PLANO LLEVAR EL AUXILIAR A LA FUENTE DE RECURSOS'
                ,UN_MODULO      => PCK_DATOS.MODULOCONTABILIDAD
                ,UN_FECHA_PAR   => SYSDATE ), 'NO');

    MI_PARESPACIOSESPPLANO := NVL(PCK_SYSMAN_UTL.FC_PAR
                (UN_COMPANIA    => UN_COMPANIA
                ,UN_NOMBRE      => 'ESPACIOS ESPECIALES EN FECHA PLANO'
                ,UN_MODULO      => PCK_DATOS.MODULOCONTABILIDAD
                ,UN_FECHA_PAR   => SYSDATE ), 'NO');

    MI_PARCREARCOMPPLANO465 := CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR
                (UN_COMPANIA    => UN_COMPANIA
                ,UN_NOMBRE      => 'CREAR COMPROBANTE CON EL TERCERO ARCHIVO PLANO COLUMNA 465'
                ,UN_MODULO      => PCK_DATOS.MODULOCONTABILIDAD
                ,UN_FECHA_PAR   => SYSDATE ), 'NO') = 'SI' THEN TRUE ELSE FALSE END;

    MI_PARSIMPLIFICAINTER := CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR
                (UN_COMPANIA    => UN_COMPANIA
                ,UN_NOMBRE      => 'SIMPLIFICAR INTERFACE GENERAL'
                ,UN_MODULO      => PCK_DATOS.MODULOCONTABILIDAD
                ,UN_FECHA_PAR   => SYSDATE ), 'NO') = 'SI' THEN TRUE ELSE FALSE END;

    IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA    => UN_COMPANIA
                    ,UN_NOMBRE      => 'DETALLE DE ARCHIVO PLANO DE 250 CARACTERES'
                    ,UN_MODULO      => PCK_DATOS.MODULOCONTABILIDAD
                    ,UN_FECHA_PAR   => SYSDATE ), 'NO') = 'SI' THEN

        MI_NUM_CAR_DETALLE := 250 - 64;
    ELSE
        MI_NUM_CAR_DETALLE := 0;
    END IF;
	MI_PARNITENTPLANO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA
                                                  ,UN_NOMBRE => 'PLANOS CON NIT DE LA ENTIDAD'
                                                  ,UN_MODULO => PCK_DATOS.MODULOCONTABILIDAD
                                                  ,UN_FECHA_PAR => SYSDATE),'SI');	
    
	MI_ETAPA := '01';

    MI_NUMREGISTRO := 1;
    MI_RNCONSECUTIVO := 1;
    MI_FECHAANT := TO_DATE('01/01/1900', 'DD/MM/YYYY');
    MI_INCONSISTENCIA := FALSE;
    MI_PINCONSISTENCIAS := TO_CLOB('Las siguientes inconsistencias se presentan en el archivo plano que se está tratando de subir a Stefanini Sysman:' || CHR(13) || CHR(10));

    MI_T_SPLIT := PCK_SYSMAN_UTL.FC_SPLIT_SYS
                    (UN_LISTA        => '' || UN_PLANO || ''
                    ,UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);

    MI_CUENTA := MI_T_SPLIT.COUNT;
    MI_ETAPA := '02';
    FOR i IN 1..MI_CUENTA LOOP
        MI_RNAUX := NVL(MI_T_SPLIT(i),' ');
        MI_RNAUX := REPLACE(REPLACE(MI_RNAUX ,CHR(13),''),CHR(10),'');


        IF NVL(MI_RNAUX,' ') <> ' ' AND LENGTH(TRIM(MI_RNAUX))>0 THEN
            MI_RNTIPO := TRIM(SUBSTR(MI_RNAUX, 1, 3));
            MI_RNCODIGO_CUENTA := TRIM(SUBSTR(MI_RNAUX, 14, 16));
            MI_RNFECHA := TO_DATE(SUBSTR(MI_RNAUX, 30, 10), 'DD/MM/YYYY');
            MI_ETAPA := '03';
            MI_MES := PCK_SYSMAN_UTL.FC_MES(MI_RNFECHA);
            IF LENGTH(MI_RNBANCO)>210 THEN
               MI_RNBANCO := TRIM(SUBSTR(MI_RNAUX, 210 + MI_NUM_CAR_DETALLE, 3));
           ELSE
               MI_RNBANCO :='';
           END IF;

            IF PCK_CONTABILIDAD4.FC_VERIFICAPERIODO(UN_COMPANIA => UN_COMPANIA
                                                   ,UN_ANO      => PCK_SYSMAN_UTL.FC_ANIO(MI_RNFECHA)
                                                   ,UN_MES      => PCK_SYSMAN_UTL.FC_MES(MI_RNFECHA)) = 0 THEN
                MI_ETAPA := '04';
                BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                    MI_REEMPLAZOS(1).CLAVE := 'ANO';
                    MI_REEMPLAZOS(1).VALOR := PCK_SYSMAN_UTL.FC_ANIO(MI_RNFECHA);
                    MI_REEMPLAZOS(2).CLAVE := 'MES';
                    MI_REEMPLAZOS(2).VALOR := PCK_SYSMAN_UTL.FC_MES(MI_RNFECHA);
                    PCK_ERR_MSG.RAISE_WITH_MSG
                              ( UN_EXC_COD => SQLCODE
                              , UN_TABLAERROR => 'ANO'
                              , UN_ERROR_COD => PCK_ERRORES.ERROR_GRAL_MESCERRADO
                              , UN_REEMPLAZOS => MI_REEMPLAZOS
                              );
                END;

            END IF;

            IF UN_RESUMIDO <> 0 THEN
                MI_ETAPA := '05';
                MI_RNNUMERO := TO_NUMBER(PCK_SYSMAN_UTL.FC_ANIO(MI_RNFECHA) || PCK_SYSMAN_UTL.FC_MES(MI_RNFECHA) || '00' || PCK_SYSMAN_UTL.FC_DIA(MI_RNFECHA));

                IF MI_FECHAANT <> MI_RNFECHA THEN
                    BEGIN
                        SELECT DISTINCT 'X'
                        INTO   MI_EXISTEDATO
                        FROM   COMPROBANTE_CNT
                        WHERE  COMPANIA = UN_COMPANIA
                          AND  TIPO =  MI_RNTIPO
                          AND  TRUNC(FECHA) = MI_RNFECHA;
                    EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_EXISTEDATO := ' ';
                    END;
                    MI_ETAPA := '06';
                    IF NVL(MI_EXISTEDATO, ' ') = 'X' THEN
                        --Esta enviando comprobante resumido y ya existen Comprobantes tipo "  & " a la fecha "  & "  Por favor Borrelos en contabilidad o eliminelo del plano ", vbCritical
                        BEGIN
                            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                            MI_REEMPLAZOS(1).CLAVE := 'TIPO';
                            MI_REEMPLAZOS(1).VALOR := MI_RNTIPO;
                            MI_REEMPLAZOS(2).CLAVE := 'FECHA';
                            MI_REEMPLAZOS(2).VALOR := MI_RNFECHA;
                            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                                        UN_ERROR_COD  => PCK_ERRORES.ERRR_PLANOCOMPRESUMENEXIST,
                                                        UN_REEMPLAZOS => MI_REEMPLAZOS);
                        END;
                    END IF;
                END IF;
                MI_ETAPA := '07';
                MI_FECHAANT := MI_RNFECHA;
            ELSE
                MI_ETAPA := '08';
                MI_RNNUMERO := TO_NUMBER(SUBSTR(MI_RNAUX, 4, 10));
            END IF;

            MI_RNANO := PCK_SYSMAN_UTL.FC_ANIO(MI_RNFECHA);
            MI_ETAPA := '09';
            MI_RNVALORDEBITO := NVL(TO_NUMBER(SUBSTR(MI_RNAUX, 40, 18)), 0);
            MI_POS := INSTR(SUBSTR(MI_RNAUX, 40, 18), '-');
            IF MI_POS > 0 THEN
                MI_RNVALORDEBITO := NVL(TO_NUMBER(SUBSTR(MI_RNAUX, 40 + MI_POS, 18 - MI_POS)), 0) * -1;
            END IF;

            MI_RNVALORCREDITO := NVL(TO_NUMBER(SUBSTR(MI_RNAUX, 58, 18)), 0);
            MI_ETAPA := '10';
            MI_POS := INSTR(SUBSTR(MI_RNAUX, 58, 18), '-');
            IF MI_POS > 0 THEN
                MI_RNVALORDEBITO := NVL(TO_NUMBER(SUBSTR(MI_RNAUX, 58 + MI_POS, 18 - MI_POS)), 0) * -1;
            END IF;

            MI_ETAPA := '11';
            IF MI_PARCREARCOMPPLANO465 THEN
                MI_RNTERCERO := TRIM(SUBSTR(MI_RNAUX, 465, 11));
            ELSE
                MI_RNTERCERO := TRIM(SUBSTR(MI_RNAUX, 76, 11));
            END IF;
            IF MI_RNTERCERO = RPAD('9',11,'9') THEN --Si vienen 11 9 entonces colocar 20 9
                MI_RNTERCERO := PCK_DATOS.CONS_TERCERO;
            END IF;

            MI_ETAPA := '12';
            MI_RNSUCURSAL := TRIM(SUBSTR(MI_RNAUX, 87, 3));
            MI_RNCENTRO_COSTO := TRIM(SUBSTR(MI_RNAUX, 90, 10));

            IF MI_RNCENTRO_COSTO = RPAD('9',10,'9') THEN --Si vienen 10 9 entonces colocar 20 9
                MI_RNCENTRO_COSTO := PCK_DATOS.CONS_CENTRO;
            END IF;

            MI_ETAPA := '13';
            MI_RNAUXILIAR := TRIM(SUBSTR(MI_RNAUX, 100, 16));
            --MROSERO
            MI_RNREFERENCIA:= TRIM(SUBSTR(MI_RNAUX, 226, 16));
            --CC_777 30/01/2025
            MI_RNFUENTERECURSO:= TRIM(SUBSTR(MI_RNAUX, 242, 16));  
            --MPEREZ
            MI_RNTEXTO:= TRIM(SUBSTR(MI_RNAUX, 258));           
            --MROSERO
            MI_RNNRO_DOCUMENTO := TRIM(SUBSTR(MI_RNAUX, 116, 30));
            IF MI_PAR_NOM_FUN_PLA =  'SI'  THEN 
                IF INSTR(TRIM(SUBSTR(MI_RNAUX, 166, 1)), '_') > 0 THEN 
                  MI_RNDESCRIPCION      := TRIM(SUBSTR(MI_RNAUX, 146, 20 + MI_NUM_CAR_DETALLE));
                  MI_NOMBRE_FUNCIONARIO := TRIM(SUBSTR(MI_RNAUX, 167, 44 + MI_NUM_CAR_DETALLE));
                ELSE
                   MI_RNDESCRIPCION := TRIM(SUBSTR(MI_RNAUX, 146, 64 + MI_NUM_CAR_DETALLE));
                   MI_NOMBRE_FUNCIONARIO := '';
                END IF;
            ELSE
                MI_RNDESCRIPCION := TRIM(SUBSTR(MI_RNAUX, 146, 64 + MI_NUM_CAR_DETALLE));
                MI_NOMBRE_FUNCIONARIO := '';
            END IF ;

            MI_RNCUENTAPPTAL := TRIM(SUBSTR(MI_RNAUX, 210 + MI_NUM_CAR_DETALLE, 16));
              IF MI_RNFUENTERECURSO = '9999999999999999' THEN --Ticket#7727987: Si vienen 16 9 entonces colocar 20 9
                MI_RNFUENTERECURSO := PCK_DATOS.CONS_AUXILIAR;
               END IF;
              IF MI_RNREFERENCIA = '9999999999999999' THEN --JM 26/03/2025 CC 1258 Si vienen 16 9 entonces colocar 20 9
                MI_RNREFERENCIA := PCK_DATOS.CONS_REFERENCIA;
               END IF;
            IF MI_RNAUXILIAR = '9999999999999999' THEN --Si vienen 16 9 entonces colocar 20 9
                MI_ETAPA := '14';
                MI_RNAUXILIAR := PCK_DATOS.CONS_AUXILIAR;
            ELSE
                BEGIN
                     IF MI_PARAUXILIARAFUENTE='SI' THEN 
                        SELECT DISTINCT 'X'
                        INTO   MI_EXISTEDATO
                        FROM   FUENTE_RECURSOS
                        WHERE  COMPANIA = UN_COMPANIA
                          AND  ANO = MI_RNANO
                          AND  CODIGO  = MI_RNAUXILIAR;
                     ELSE
                        SELECT DISTINCT 'X'
                        INTO   MI_EXISTEDATO
                        FROM   AUXILIAR
                        WHERE  COMPANIA = UN_COMPANIA
                          AND  ANO = MI_RNANO
                          AND  CODIGO  = MI_RNAUXILIAR;
                    END IF;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_EXISTEDATO := ' ';
                END;

                MI_ETAPA := '15';
                IF NVL(MI_EXISTEDATO, ' ') = ' ' THEN
                    MI_AUX := 1;
                    WHILE MI_AUX < 16 LOOP
                        MI_RNAUXILIAR := TRIM(SUBSTR(MI_RNAUXILIAR, MI_AUX, 16));
                        MI_AUX := MI_AUX + 1;
                        BEGIN
                            IF MI_PARAUXILIARAFUENTE='SI' THEN 
                                SELECT DISTINCT 'X'
                                INTO   MI_EXISTEDATO
                                FROM   FUENTE_RECURSOS
                                WHERE  COMPANIA = UN_COMPANIA
                                  AND  ANO = MI_RNANO
                                  AND  CODIGO  = MI_RNAUXILIAR;
                            ELSE
                                SELECT DISTINCT 'X'
                                INTO   MI_EXISTEDATO
                                FROM   AUXILIAR
                                WHERE  COMPANIA = UN_COMPANIA
                                  AND  ANO = MI_RNANO
                                  AND  CODIGO  = MI_RNAUXILIAR;
                            END IF;
                        EXCEPTION WHEN NO_DATA_FOUND THEN
                            MI_EXISTEDATO := ' ';
                        END;

                        IF NVL(MI_EXISTEDATO, ' ') = ' ' THEN
                           MI_RNAUXILIAR := TRIM(SUBSTR(MI_RNAUX, 100, 16));
                        ELSE
                            MI_AUX := 17;
                        END IF;
                    END LOOP;
                END IF;
            END IF;
            MI_ETAPA := '16';
            IF MI_PARESPACIOSESPPLANO = 'NO' THEN
                MI_ETAPA := '17';
                IF PCK_SYSMAN_UTL.FC_ISDATE(TRIM(SUBSTR(MI_RNAUX, 191 + MI_NUM_CAR_DETALLE, 10)), 'DD/MM/YYYY') <> 0 THEN
                    MI_RNFECHACONSIGNACION := TO_DATE(TRIM(SUBSTR(MI_RNAUX, 191 + MI_NUM_CAR_DETALLE, 10)), 'DD/MM/YYYY');
                ELSE
                    MI_RNFECHACONSIGNACION := TO_DATE(SUBSTR(MI_RNAUX, 30, 10), 'DD/MM/YYYY');
                END IF;
            ELSE
                MI_ETAPA := '18';
                IF PCK_SYSMAN_UTL.FC_ISDATE(TRIM(SUBSTR(MI_RNAUX, 226 + MI_NUM_CAR_DETALLE, 10)), 'DD/MM/YYYY') <> 0 THEN
                    MI_RNFECHACONSIGNACION := TO_DATE(TRIM(SUBSTR(MI_RNAUX, 226 + MI_NUM_CAR_DETALLE, 10)), 'DD/MM/YYYY');
                ELSE
                    MI_RNFECHACONSIGNACION := TO_DATE(SUBSTR(MI_RNAUX, 30, 10), 'DD/MM/YYYY');
                END IF;
            END IF;
            MI_ETAPA := '19';
            BEGIN
                SELECT CLASECUENTA, CUENTA_PPTAL
                INTO   MI_CLASE, MI_CUENTAPPTAL
                FROM   PLAN_CONTABLE
                WHERE  COMPANIA = UN_COMPANIA
                  AND  ANO = MI_RNANO
                  AND  CODIGO = MI_RNCODIGO_CUENTA;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_INCONSISTENCIA := TRUE;
                MI_PINCONSISTENCIAS :=MI_PINCONSISTENCIAS ||  TO_CLOB('El registro No. '|| MI_NUMREGISTRO ||' Presenta problemas con el codigo de cuenta '|| MI_RNCODIGO_CUENTA ||' No existe, Por favor revise configuración en la generación del archivo plano'  || CHR(13) || CHR(10));
            END;

            MI_ETAPA := '20';
            IF NVL(MI_RNTIPO,' ') = ' ' THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El registro No. '|| MI_NUMREGISTRO ||' NO tiene tipo de comprobante.' || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;
            MI_ETAPA := '21';
            IF MI_RNNUMERO = 0 THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El registro No. '|| MI_NUMREGISTRO ||' NO tiene número de comprobante.'  || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;
            IF NVL(MI_RNCODIGO_CUENTA, ' ') = ' ' THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El registro No. '|| MI_NUMREGISTRO ||' NO tiene código de cuenta contable.' || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;
            IF NVL(MI_RNTERCERO, ' ') = ' ' THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El registro No. '|| MI_NUMREGISTRO ||' NO tiene código de tercero.'  || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;
            IF NVL(MI_RNSUCURSAL, ' ') = ' ' THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El registro No. '|| MI_NUMREGISTRO ||' No tiene código de sucursal del tercero.'  || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;

            MI_ETAPA := '22';
            IF NVL(MI_RNCENTRO_COSTO, ' ') = ' ' THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El registro No. '|| MI_NUMREGISTRO ||' No tiene código de centro de costo.'  || CHR(13) || CHR(10));
                MI_RNCENTRO_COSTO := PCK_DATOS.CONS_CENTRO;
                MI_INCONSISTENCIA := FALSE;
            END IF;
            IF NVL(MI_RNAUXILIAR, ' ') = '' THEN
                IF MI_PARAUXILIARAFUENTE='SI' THEN                  
                    MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El registro No. '|| MI_NUMREGISTRO ||' No tiene código de fuente de recursos.'  || CHR(13) || CHR(10));
                ELSE
                    MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El registro No. '|| MI_NUMREGISTRO ||' No tiene código de auxiliar general.'  || CHR(13) || CHR(10));
                END IF;
                MI_INCONSISTENCIA := TRUE;
            END IF;
            IF NVL(MI_RNDESCRIPCION,' ') = ' ' THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El registro No. '|| MI_NUMREGISTRO ||' No tiene descripción.'  || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;

            /*MPEREZ TICKET#7723371*/
            IF NVL(MI_RNTEXTO,' ') = ' ' THEN
                MI_RNTEXTO  := 'Interface'; 
            ELSE               
              MI_RNTEXTO  := REGEXP_REPLACE(REGEXP_REPLACE(MI_RNTEXTO, '[(][A-Z]+[)]', ''), '[^ ./a-zA-Z-0-9]', '');
            END IF;

            MI_ETAPA := '23';
            IF NVL(MI_RNCUENTAPPTAL, ' ') = ' ' THEN
                IF MI_CLASE = 'P' OR MI_CLASE = 'N' THEN
                    MI_RNCUENTAPPTAL := MI_CUENTAPPTAL;
                END IF;
            END IF;

            MI_ETAPA := '24';
            BEGIN
                MI_NATURALEZA := PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RNCODIGO_CUENTA);
                MI_TABLA := 'TEMP_PLANA_AJUSTES';

                MI_CAMPOS := 'COMPANIA ' ||
                             ',ANO ' ||
                             ',TIPO_CPTE ' ||
                             ',COMPROBANTE ' ||
                             ',CONSECUTIVO ' ||
                             ',CUENTA ' ||
                             ',NATURALEZA ' ||
                             ',FECHA ' ||
                             ',DESCRIPCION ' ||
                             ',VALOR_DEBITO ' ||
                             ',VALOR_CREDITO ' ||
                             ',NRO_DOCUMENTO ' ||
                             ',TERCERO ' ||
                             ',SUCURSAL ' ||
                             ',CENTRO_COSTO ' ||
--mrosero                              
                             ',AUXILIAR ' ||                              
                             ',FUENTE_RECURSOS ' ||   
                             ',REFERENCIA ' ||
--mperez
                             ',TEXTOD ' ||   

--mrosero                             
                             ',FECHA_CONSIGNACIONPLANO';

                MI_VALORES := '  '''|| UN_COMPANIA ||'''
                                , '|| MI_RNANO ||'
                                , '''|| MI_RNTIPO ||'''
                                , '|| MI_RNNUMERO ||'
                                , '|| MI_RNCONSECUTIVO ||'
                                , '''|| MI_RNCODIGO_CUENTA ||'''
                                , '''|| MI_NATURALEZA ||'''
                                , TO_DATE(''' || TO_CHAR(MI_RNFECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                                ,'''||  MI_RNDESCRIPCION || '''
                                ,'|| MI_RNVALORDEBITO ||'
                                ,'|| MI_RNVALORCREDITO ||'
                                ,'''|| MI_RNNRO_DOCUMENTO ||'''
                                ,'''|| MI_RNTERCERO ||'''
                                ,'''|| MI_RNSUCURSAL ||'''
                                ,'''|| CASE WHEN MI_RNCENTRO_COSTO  IS NULL THEN '99999999999999999999' ELSE MI_RNCENTRO_COSTO END||'''                           
                                ,'''|| CASE WHEN MI_RNAUXILIAR IS NULL OR MI_PARAUXILIARAFUENTE='SI' THEN '99999999999999999999' ELSE MI_RNAUXILIAR END ||'''
                                ,'''|| CASE WHEN MI_PARAUXILIARAFUENTE='NO' THEN CASE WHEN MI_RNFUENTERECURSO IS NULL THEN '99999999999999999999'  ELSE MI_RNFUENTERECURSO END  ELSE MI_RNAUXILIAR END  ||'''
                                ,'''|| CASE WHEN MI_RNREFERENCIA IS NULL THEN '99999999999999999999' ELSE  MI_RNREFERENCIA END ||'''      
                                ,'''|| MI_RNTEXTO ||'''    
                                ,TO_DATE(''' || TO_CHAR(MI_RNFECHACONSIGNACION, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')';
                IF NVL(MI_RNCUENTAPPTAL, ' ') <> ' ' THEN
                    MI_CAMPOS := MI_CAMPOS ||' ,CUENTAPPTAL
                                               ,EJECUCION_DEBITO
                                               ,EJECUCION_CREDITO ';

                    MI_VALORES := MI_VALORES || ' ,'''|| MI_RNCUENTAPPTAL ||'''
                                                  ,'|| MI_RNVALORDEBITO ||'
                                                  ,'|| MI_RNVALORCREDITO ||'  ';

                END IF;

                IF MI_PAR_NOM_FUN_PLA =  'SI'  THEN 
                        MI_CAMPOS := MI_CAMPOS ||' ,NOMBRE_FUNCIONARIO';
                        MI_VALORES := MI_VALORES || ' ,'''|| MI_NOMBRE_FUNCIONARIO ||''' ';
                END IF ;

                MI_ETAPA := '25';
                BEGIN
                    MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                          UN_ACCION  => 'I',
                                                          UN_CAMPOS  => MI_CAMPOS,
                                                          UN_VALORES => MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                --Se presentó error al insertar el tipo de comprobante: --TIPO-- ,Comprobante: --COMPROBANTE-- en plan ajustes.
                MI_REEMPLAZOS(1).CLAVE := 'TIPO';
                MI_REEMPLAZOS(1).VALOR := MI_RNTIPO;
                MI_REEMPLAZOS(2).CLAVE := 'COMPROBANTE';
                MI_REEMPLAZOS(2).VALOR := MI_RNNUMERO;
                MI_REEMPLAZOS(3).CLAVE := 'LINEA';
                MI_REEMPLAZOS(3).VALOR := MI_NUMREGISTRO;
                PCK_ERR_MSG.RAISE_WITH_MSG
                          ( UN_EXC_COD => SQLCODE
                          , UN_TABLAERROR => MI_TABLA
                          , UN_ERROR_COD => PCK_ERRORES.ERRR_PLANOINSPLANAAJUSTE
                          , UN_REEMPLAZOS => MI_REEMPLAZOS
                          );
            END;
            MI_ETAPA := '26';
            IF MI_FILAS > 0 THEN
                MI_RNCONSECUTIVO := MI_RNCONSECUTIVO + 1;
            END IF;

            MI_NUMREGISTRO := MI_NUMREGISTRO + 1;
        END IF;--Siguiente registro
        --<<SIGUE>>
    END LOOP;
    MI_ETAPA := '27';
    IF MI_INCONSISTENCIA THEN
        MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('Proceso de contabilizar no realizado' || CHR(13) || CHR(10));
        MI_RTA := MI_PINCONSISTENCIAS;
        RETURN MI_RTA;
    END IF;

    BEGIN
        MI_ETAPA := '28';
        MI_INCONSISTENCIA := FALSE;
        MI_PINCONSISTENCIAS := TO_CLOB('Las siguientes inconsistencias se presentan en el archivo plano que se está tratando de subir a Stefanini Sysman:' || CHR(13) || CHR(10));
        <<DATOSINCONSISTENTES>>
        FOR MI_RS IN
            (SELECT TP.COMPANIA,  TP.ANO,  TP.TIPO_CPTE,  TP.COMPROBANTE, TP.TERCERO ,  TP.CENTRO_COSTO , TP.AUXILIAR, TP.SUCURSAL
              , TC.CODIGO TIPOCOMPROBANTE
              , CC.NUMERO NUMCOMPRO
              , T.NIT     NIT
              , CCO.CODIGO CENTROCODIGO
              , AX.CODIGO AUXILIARCODIGO
              , REFE.CODIGO REFERENCIACODIGO
              , FUENTE.CODIGO FUENTECODIGO
            FROM TEMP_PLANA_AJUSTES TP
            LEFT JOIN TIPO_COMPROBANTE TC
                      ON TP.COMPANIA   = TC.COMPANIA
                      AND TP.TIPO_CPTE = TC.CODIGO
            LEFT JOIN COMPROBANTE_CNT CC
                      ON   TP.COMPANIA    = CC.COMPANIA
                      AND  TP.ANO         = CC.ANO
                      AND  TP.TIPO_CPTE   = CC.TIPO
                      AND  TP.COMPROBANTE = CC.NUMERO
            LEFT JOIN TERCERO T
                      ON  TP.COMPANIA = T.COMPANIA
                      AND TP.TERCERO  = T.NIT
                      AND TP.SUCURSAL = T.SUCURSAL
            LEFT JOIN CENTRO_COSTO CCO
                      ON  TP.COMPANIA     = CCO.COMPANIA
                      AND TP.ANO          = CCO.ANO
                      AND TP.CENTRO_COSTO = CCO.CODIGO
            LEFT JOIN AUXILIAR AX
                      ON  TP.COMPANIA = AX.COMPANIA
                      AND TP.ANO      = AX.ANO
                      AND TP.AUXILIAR = AX.CODIGO
            LEFT JOIN REFERENCIA REFE
                      ON  TP.COMPANIA   = REFE.COMPANIA
                      AND TP.ANO        = REFE.ANO
                      AND TP.REFERENCIA = REFE.CODIGO
            LEFT JOIN FUENTE_RECURSOS FUENTE
                      ON  TP.COMPANIA        = FUENTE.COMPANIA
                      AND TP.ANO             = FUENTE.ANO
                      AND TP.FUENTE_RECURSOS = FUENTE.CODIGO

            WHERE TC.CODIGO IS NULL
               OR CC.NUMERO = TP.COMPROBANTE
               OR T.NIT IS NULL
               OR CCO.CODIGO IS NULL
               OR AX.CODIGO IS NULL)
        LOOP
            MI_ETAPA := '29';
            IF MI_RS.TIPOCOMPROBANTE IS NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El tipo de comprobante '|| MI_RS.TIPO_CPTE || ' NO está creado en el sistema.' || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;

            IF MI_RS.NUMCOMPRO IS NOT NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El número de comprobante '|| MI_RS.COMPROBANTE || ' de tipo '|| MI_RS.TIPO_CPTE ||' YA existe en el sistema.' || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;

            IF MI_RS.NIT IS NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El tercero '|| MI_RS.TERCERO || ' sucursal '|| MI_RS.SUCURSAL ||' NO está creado en el sistema.' || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;

            IF MI_RS.CENTROCODIGO IS NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El centro de costo '|| MI_RS.CENTRO_COSTO || ' NO está creado en el sistema.' || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;
            MI_ETAPA := '30';
            IF MI_RS.AUXILIARCODIGO IS NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El auxiliar '|| MI_RS.AUXILIAR || ' NO está creado en el sistema.' || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;

            IF MI_RS.REFERENCIACODIGO IS NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('La referencia '|| MI_RS.REFERENCIACODIGO || ' NO está creada en el sistema.' || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;

            IF MI_RS.FUENTECODIGO IS NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('La fuente de Recursos '|| MI_RS.FUENTECODIGO || ' NO está creada en el sistema.' || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;
        END LOOP DATOSINCONSISTENTES;
    END;

    IF MI_INCONSISTENCIA THEN
        MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('Proceso de contabilizar no realizado' || CHR(13) || CHR(10));
        MI_RTA := MI_PINCONSISTENCIAS;
        RETURN MI_RTA;
    ELSE
        MI_RTA := '';
        MI_PINCONSISTENCIAS := TO_CLOB('Se crearon los siguientes comprobantes:' || CHR(13) || CHR(10));
        <<CONTABILIZAR>>
        FOR MI_RS IN
            (
                SELECT COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,
                       MAX(NRO_DOCUMENTO) NRO_DOCUMENTO,
                       MAX(FECHA) AS FEC,
                       MAX(TERCERO) AS TERDETALLE,
                       MAX(SUCURSAL)  AS SUC,
                       MAX(DESCRIPCION) AS DESCR,
                       MAX(TEXTOD) AS TEXTOD
                FROM TEMP_PLANA_AJUSTES
                WHERE COMPANIA = UN_COMPANIA
                GROUP BY COMPANIA,ANO,TIPO_CPTE,COMPROBANTE
            )
        LOOP
            MI_ETAPA := '30A';
            /*MI_RESULTADORECORD := MI_RS.COMPANIA || 'ANO ' || MI_RS.ANO || ' TIPO ' || MI_RS.TIPO_CPTE ||
                                ' DOC ' || MI_RS.NRO_DOCUMENTO || ' COMP ' || MI_RS.COMPROBANTE || ' FEC ' || MI_RS.FEC || ' TER ' || MI_RS.TER || ' SUC ' || MI_RS.SUC || ' DESC ' || MI_RS.DESCR ;*/
            MI_ETAPA := '31';

			--CC_1526(03/06/2025 JCROJAS)
            IF MI_PARNITENTPLANO = 'SI' THEN
				IF UN_TERCERODETALLE <> 0 THEN
					MI_NITCONTA := MI_RS.TERDETALLE;
					MI_SUCURSALCONTA := MI_RS.SUC;
				ELSE
					MI_NITCONTA := MI_NITHEADER;
					MI_SUCURSALCONTA := MI_SUCURSALHEADER;
				END IF;            
			ELSE
                SELECT COUNT(DISTINCT TERCERO) NUM 
                INTO MI_NUMTERCEROS
                FROM TEMP_PLANA_AJUSTES 
                WHERE COMPANIA = MI_RS.COMPANIA
                    AND ANO = MI_RS.ANO
                    AND TIPO_CPTE = MI_RS.TIPO_CPTE
                    AND COMPROBANTE = MI_RS.COMPROBANTE;
                    
                IF MI_NUMTERCEROS > 1 THEN
                    MI_NITCONTA := MI_NITHEADER;
                    MI_SUCURSALCONTA := MI_SUCURSALHEADER;
                ELSE
                    MI_NITCONTA := MI_RS.TERDETALLE;
                    MI_SUCURSALCONTA := MI_RS.SUC;
                END IF;
            END IF;
            MI_ETAPA := '32';
            MI_PINCONSISTENCIAS := TO_CLOB(PCK_CONTABILIZAR.FC_CONTABILIZAR
                        (UN_COMPANIA         => UN_COMPANIA
                        ,UN_TIPOCOMPROBANTE  => MI_RS.TIPO_CPTE
                        ,UN_NUMERO           => MI_RS.COMPROBANTE
                        ,UN_ANO              => MI_RS.ANO
                        ,UN_FECHA            => MI_RS.FEC
                        ,UN_TERCERO          => MI_NITCONTA
                        ,UN_SUCURSAL         => MI_SUCURSALCONTA
                        ,UN_DESCRIPCION      => MI_RS.DESCR
                        ,UN_SIMPLE           => CASE WHEN MI_PARSIMPLIFICAINTER THEN -1 ELSE 0 END 
                        ,UN_CONCILIAR        => UN_CONCILIAR
                        ,UN_INDIMPRESION     => 0
                        ,UN_INTOMITIRPPTAL   => UN_SINPPTAL
                        ,UN_NRO_DOCUMENTO    => MI_RS.NRO_DOCUMENTO
                        ,UN_USUARIO          => UN_USUARIO
                        ,UN_TEXTO            => MI_RS.TEXTOD));
                        MI_ETAPA := '32A';
            MI_RTA := MI_RTA || MI_PINCONSISTENCIAS || TO_CLOB(CHR(13) || CHR(10));
        END LOOP CONTABILIZAR;

    END IF;

    MI_RTA := MI_RTA || CHR(13) || CHR(10) || 'Proceso terminado Exitosamente';
    EXECUTE IMMEDIATE 'DELETE TEMP_PLANA_AJUSTES WHERE 1 = 1'; --JM 09/12/2024 NO FUNCIONA EL COMMIT; CC_436_NOMINA
    --BORRADO A LA FUERZA PARA QUE PERMITA CARGAR VARIOS COMPORBANTES SEGUIDOS 
    RETURN MI_RTA;
EXCEPTION WHEN OTHERS THEN
    --Se presentó error en la subida del archivo plano, en la línea del plano número: --PLANOLINEA-- , En la etapa: --ETAPA--.
    MI_REEMPLAZOS(1).CLAVE := 'PLANOLINEA';
    MI_REEMPLAZOS(1).VALOR := MI_NUMREGISTRO;
    MI_REEMPLAZOS(2).CLAVE := 'ETAPA';
    MI_REEMPLAZOS(2).VALOR := MI_ETAPA|| ' ERROR '||SQLERRM || CHR(13) || CHR(10) || NVL(MI_RNAUX,' ');
    PCK_ERR_MSG.RAISE_WITH_MSG
              ( UN_EXC_COD => -20000
              , UN_TABLAERROR => 'ANO'
              , UN_ERROR_COD => PCK_ERRORES.ERRR_SUBIRPLANOCONTABILIZA
              , UN_REEMPLAZOS => MI_REEMPLAZOS
              );
END FC_CONTABILIZARPORPLANO;

PROCEDURE PR_CARGARINTERFAZALMACENCCNIIF(

/*
    NAME              : PR_CARGARINTERFAZALMACENCC  --> En Access NIIF_Transaccion_Click()
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : LAURA MELIZA BOTIA PEREZ
    DATE MIGRADOR     : 13/09/2018
    TIME              : 08:00 AM
    SOURCE MODULE     : InterfacesPb2018.07.05
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : INSERTA EN LA TABLA ALMACENCONTABILIDADCC LOS REGISTROS DE LA TABLA ALMACENCONTABILIDAD DE UN AÑO
    PARAMETERS        : UN_COMPANIA        => COMPANIA EN LA QUE SE EST�? TRABAJANDO.
                        UN_CODIGOELEMENTO  =>  CODIGO INTERNO PARA REGISTRAR LA NOVEDAD.
                        UN_ANO             =>  AÑO PARA EL CUAL SE GENERARA LA NOVEDAD.

      @NAME:    cargarInterfazAlmacenCCNIIF
      @METHOD:  POST
  */


  UN_COMPANIA           IN  PCK_SUBTIPOS.TI_COMPANIA,
  UN_CODIGOELEMENTO     IN  ALMACENCONTABILIDADCC.CODIGOELEMENTO%TYPE,
  UN_ANO                IN  ALMACENCONTABILIDADCC.ANO%TYPE

)AS
  MI_CONTEO         PCK_SUBTIPOS.TI_ENTERO;
  MI_RS             SYS_REFCURSOR;
  MI_STRSQL         PCK_SUBTIPOS.TI_STRSQL;
  MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
  MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN

    SELECT COUNT(1)
    INTO MI_CONTEO
    FROM ALMACENCONTABILIDADCC
    WHERE ANO          = UN_ANO
     AND CODIGOELEMENTO = UN_CODIGOELEMENTO;

      IF MI_CONTEO = 0 THEN

        FOR MI_RS
        IN(SELECT ALMACENCONTABILIDAD.COMPANIA,
                  ALMACENCONTABILIDAD.CODIGOELEMENTO,
                  ALMACENCONTABILIDAD.TIPOMOVIMIENTO,
                  ALMACENCONTABILIDAD.ANO,
                  CENTRO_COSTO.CODIGO,
                  ALMACENCONTABILIDAD.NIIF_CUENTADEBITO,
                  ALMACENCONTABILIDAD.NIIF_CUENTACREDITO,
                  ALMACENCONTABILIDAD.NIIF_CUENTADEBITOAJUSTE,
                  ALMACENCONTABILIDAD.NIIF_CUENTACREDITOAJUSTE,
                  ALMACENCONTABILIDAD.NIIF_DEBITO_BASE,
                  ALMACENCONTABILIDAD.NIIF_CREDITO_BASE,
                  ALMACENCONTABILIDAD.NIIF_CREDITO_IVA,
                  ALMACENCONTABILIDAD.NIIF_DEBITO_IVA
            FROM ALMACENCONTABILIDAD
            INNER JOIN CENTRO_COSTO
                ON ALMACENCONTABILIDAD.COMPANIA = CENTRO_COSTO.COMPANIA
                AND ALMACENCONTABILIDAD.ANO     = CENTRO_COSTO.ANO
            WHERE ALMACENCONTABILIDAD.CODIGOELEMENTO = UN_CODIGOELEMENTO
              AND ALMACENCONTABILIDAD.ANO      = UN_ANO
        )
        LOOP    

           SELECT COUNT(1)
              INTO MI_CONTEO
              FROM ALMACENCONTABILIDADCC
             WHERE CODIGOELEMENTO = MI_RS.CODIGOELEMENTO
               AND TIPOMOVIMIENTO = MI_RS.TIPOMOVIMIENTO
               AND ANO            = MI_RS.ANO
               AND CENTRO_COSTO   = MI_RS.CODIGO ;


              IF MI_CONTEO = 0 THEN

                  MI_CAMPOS :='COMPANIA
                              ,CODIGOELEMENTO
                              ,TIPOMOVIMIENTO
                              ,ANO
                              ,CENTRO_COSTO
                              ,NIIF_CUENTADEBITO
                              ,NIIF_CUENTACREDITO 
                              ,NIIF_CUENTADEBITOAJUSTE
                              ,NIIF_CUENTACREDITOAJUSTE
                              ,NIIF_DEBITO_BASE
                              ,NIIF_CREDITO_BASE
                              ,NIIF_CREDITO_IVA
                              ,NIIF_DEBITO_IVA';

                   MI_VALORES := ' '''||UN_COMPANIA||'''
                                 , '''||MI_RS.CODIGOELEMENTO||'''
                                 , '''||MI_RS.TIPOMOVIMIENTO||'''
                                 , '||MI_RS.ANO||'
                                 , '''||MI_RS.CODIGO||'''
                                 , '''||MI_RS.NIIF_CUENTADEBITO||'''
                                 , '''||MI_RS.NIIF_CUENTACREDITO||'''
                                 , '''||MI_RS.NIIF_CUENTADEBITOAJUSTE||''' 
                                 , '''||MI_RS.NIIF_CUENTACREDITOAJUSTE||'''
                                 , '''||MI_RS.NIIF_DEBITO_BASE||'''
                                 , '''||MI_RS.NIIF_CREDITO_BASE||'''
                                 , '''||MI_RS.NIIF_CREDITO_IVA||'''
                                 , '''||MI_RS.NIIF_DEBITO_IVA||''' ';           
                        BEGIN
                          BEGIN

                               PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME (UN_TABLA   => 'ALMACENCONTABILIDADCC'
                                                                       ,UN_ACCION  => 'I'
                                                                       ,UN_CAMPOS  => MI_CAMPOS
                                                                       ,UN_VALORES => MI_VALORES);  

                                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                          RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                            END ;

                                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                                    MI_MSGERROR(1).CLAVE := 'CODIGOELEMENTO';
                                    MI_MSGERROR(1).VALOR := MI_RS.CODIGOELEMENTO;
                                               PCK_ERR_MSG.RAISE_WITH_MSG(
                                                                  UN_EXC_COD =>SQLCODE
                                                                  ,UN_ERROR_COD=>PCK_ERRORES.ERR_INSERTALMACENCC
                                                                  ,UN_REEMPLAZOS  => MI_MSGERROR);
                       END; 


              END IF;

        END LOOP;

       END IF;



END PR_CARGARINTERFAZALMACENCCNIIF;


PROCEDURE PR_PREPARARCONTABI_SIGANO
/*
    NAME              : PR_PREPARARCONTABI_SIGANO  --> En acces boton Iniciar preparacion, PrepararInterfazAlm
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
    DATE MIGRADOR     : 11/02/2019
    TIME              : 12:41 PM  
    SOURCE MODULE     : SysmanAl2018.09.05
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       :  

    @NAME:    prepararContabilizacionSiguienteAnio
    @METHOD:  POST

  */
(
  UN_COMPANIA           IN  PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIOINICIAL        IN  PCK_SUBTIPOS.TI_ANIO,
  UN_ANIOFINAL          IN  PCK_SUBTIPOS.TI_ANIO,
  UN_USUARIO            IN  PCK_SUBTIPOS.TI_USUARIO
)
AS
  MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
  MI_VALORES        PCK_SUBTIPOS.TI_VALORES; 
  MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
  MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_MERGEUSING   PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE  PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE  PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_MERGENOEXISTE PCK_SUBTIPOS.TI_MERGENOEXISTE;
BEGIN
   
    MI_MSGERROR(1).CLAVE := 'ANIO';
    MI_MSGERROR(1).VALOR := UN_ANIOFINAL ;
    --CC_3531 GROJAS: Se ajusta el proceso para realizar merge al momento de actualizar la información.
    MI_MERGEUSING := 'SELECT  COMPANIA, CODIGOELEMENTO, TIPOMOVIMIENTO, '|| UN_ANIOFINAL ||' ANO, CUENTADEBITO, CUENTACREDITO, CUENTADEBITOAJUSTE, CUENTACREDITOAJUSTE, DEBITO_BASE, CREDITO_BASE, CREDITO_IVA, DEBITO_IVA,
                NIIF_CUENTADEBITO, NIIF_CUENTACREDITO, DEBITO_HISTORICO_BAJA, CREDITO_HISTORICO_BAJA, DEBITO_ACUMULADA_BAJA, CREDITO_ACUMULADA_BAJA, DEBITO_LIBROS_BAJA, CREDITO_LIBROS_BAJA,
                DEBITO_RETIRADOS_BAJA, CREDITO_RETIRADOS_BAJA, NIIF_DEBITO_IVA, NIIF_CREDITO_IVA, NIIF_DEBITO_BASE, NIIF_CREDITO_BASE, NIIF_CUENTACREDITOAJUSTE, NIIF_CUENTADEBITOAJUSTE,
                NIIF_DEBITO_HISTORICO_BAJA, NIIF_CREDITO_HISTORICO_BAJA, NIIF_DEBITO_ACUMULADA_BAJA, NIIF_CREDITO_ACUMULADA_BAJA, NIIF_DEBITO_LIBROS_BAJA, NIIF_CREDITO_LIBROS_BAJA,
                NIIF_DEBITO_RETIRADOS_BAJA, NIIF_CREDITO_RETIRADOS_BAJA
            FROM ALMACENCONTABILIDAD
            WHERE COMPANIA = '''|| UN_COMPANIA ||'''
              AND ANO = '|| UN_ANIOINICIAL ||'';
      
      
     MI_MERGEENLACE := 'TABLA.COMPANIA          = VISTA.COMPANIA
                       AND TABLA.CODIGOELEMENTO = VISTA.CODIGOELEMENTO
                       AND TABLA.TIPOMOVIMIENTO = VISTA.TIPOMOVIMIENTO
                       AND TABLA.ANO            = VISTA.ANO';
                       
    MI_MERGEEXISTE := 'UPDATE SET
                        TABLA.CUENTADEBITO               = VISTA.CUENTADEBITO,
                        TABLA.CUENTACREDITO              = VISTA.CUENTACREDITO,
                        TABLA.CUENTADEBITOAJUSTE         = VISTA.CUENTADEBITOAJUSTE,
                        TABLA.CUENTACREDITOAJUSTE        = VISTA.CUENTACREDITOAJUSTE,
                        TABLA.DEBITO_BASE                = VISTA.DEBITO_BASE,
                        TABLA.CREDITO_BASE               = VISTA.CREDITO_BASE,
                        TABLA.CREDITO_IVA                = VISTA.CREDITO_IVA,
                        TABLA.DEBITO_IVA                 = VISTA.DEBITO_IVA,
                        TABLA.NIIF_CUENTADEBITO           = VISTA.NIIF_CUENTADEBITO,
                        TABLA.NIIF_CUENTACREDITO          = VISTA.NIIF_CUENTACREDITO,
                        TABLA.DEBITO_HISTORICO_BAJA       = VISTA.DEBITO_HISTORICO_BAJA,
                        TABLA.CREDITO_HISTORICO_BAJA      = VISTA.CREDITO_HISTORICO_BAJA,
                        TABLA.DEBITO_ACUMULADA_BAJA       = VISTA.DEBITO_ACUMULADA_BAJA,
                        TABLA.CREDITO_ACUMULADA_BAJA      = VISTA.CREDITO_ACUMULADA_BAJA,
                        TABLA.DEBITO_LIBROS_BAJA          = VISTA.DEBITO_LIBROS_BAJA,
                        TABLA.CREDITO_LIBROS_BAJA         = VISTA.CREDITO_LIBROS_BAJA,
                        TABLA.DEBITO_RETIRADOS_BAJA       = VISTA.DEBITO_RETIRADOS_BAJA,
                        TABLA.CREDITO_RETIRADOS_BAJA      = VISTA.CREDITO_RETIRADOS_BAJA,
                        TABLA.NIIF_DEBITO_IVA             = VISTA.NIIF_DEBITO_IVA,
                        TABLA.NIIF_CREDITO_IVA            = VISTA.NIIF_CREDITO_IVA,
                        TABLA.NIIF_DEBITO_BASE            = VISTA.NIIF_DEBITO_BASE,
                        TABLA.NIIF_CREDITO_BASE           = VISTA.NIIF_CREDITO_BASE,
                        TABLA.NIIF_CUENTACREDITOAJUSTE    = VISTA.NIIF_CUENTACREDITOAJUSTE,
                        TABLA.NIIF_CUENTADEBITOAJUSTE     = VISTA.NIIF_CUENTADEBITOAJUSTE,
                        TABLA.NIIF_DEBITO_HISTORICO_BAJA  = VISTA.NIIF_DEBITO_HISTORICO_BAJA,
                        TABLA.NIIF_CREDITO_HISTORICO_BAJA = VISTA.NIIF_CREDITO_HISTORICO_BAJA,
                        TABLA.NIIF_DEBITO_ACUMULADA_BAJA  = VISTA.NIIF_DEBITO_ACUMULADA_BAJA,
                        TABLA.NIIF_CREDITO_ACUMULADA_BAJA = VISTA.NIIF_CREDITO_ACUMULADA_BAJA,
                        TABLA.NIIF_DEBITO_LIBROS_BAJA     = VISTA.NIIF_DEBITO_LIBROS_BAJA,
                        TABLA.NIIF_CREDITO_LIBROS_BAJA    = VISTA.NIIF_CREDITO_LIBROS_BAJA,
                        TABLA.NIIF_DEBITO_RETIRADOS_BAJA  = VISTA.NIIF_DEBITO_RETIRADOS_BAJA,
                        TABLA.NIIF_CREDITO_RETIRADOS_BAJA = VISTA.NIIF_CREDITO_RETIRADOS_BAJA';  
                        
        MI_MERGENOEXISTE:= ' INSERT ( COMPANIA, CODIGOELEMENTO, TIPOMOVIMIENTO, ANO,
                            CUENTADEBITO, CUENTACREDITO, CUENTADEBITOAJUSTE, CUENTACREDITOAJUSTE,
                            DEBITO_BASE, CREDITO_BASE, CREDITO_IVA, DEBITO_IVA,
                            NIIF_CUENTADEBITO, NIIF_CUENTACREDITO,
                            DEBITO_HISTORICO_BAJA, CREDITO_HISTORICO_BAJA,
                            DEBITO_ACUMULADA_BAJA, CREDITO_ACUMULADA_BAJA,
                            DEBITO_LIBROS_BAJA, CREDITO_LIBROS_BAJA,
                            DEBITO_RETIRADOS_BAJA, CREDITO_RETIRADOS_BAJA,
                            NIIF_DEBITO_IVA, NIIF_CREDITO_IVA,
                            NIIF_DEBITO_BASE, NIIF_CREDITO_BASE,
                            NIIF_CUENTACREDITOAJUSTE, NIIF_CUENTADEBITOAJUSTE,
                            NIIF_DEBITO_HISTORICO_BAJA, NIIF_CREDITO_HISTORICO_BAJA,
                            NIIF_DEBITO_ACUMULADA_BAJA, NIIF_CREDITO_ACUMULADA_BAJA,
                            NIIF_DEBITO_LIBROS_BAJA, NIIF_CREDITO_LIBROS_BAJA,
                            NIIF_DEBITO_RETIRADOS_BAJA, NIIF_CREDITO_RETIRADOS_BAJA
                        )
                        VALUES ( VISTA.COMPANIA, VISTA.CODIGOELEMENTO, VISTA.TIPOMOVIMIENTO, VISTA.ANO,
                            VISTA.CUENTADEBITO, VISTA.CUENTACREDITO, VISTA.CUENTADEBITOAJUSTE, VISTA.CUENTACREDITOAJUSTE,
                            VISTA.DEBITO_BASE, VISTA.CREDITO_BASE, VISTA.CREDITO_IVA, VISTA.DEBITO_IVA,
                            VISTA.NIIF_CUENTADEBITO, VISTA.NIIF_CUENTACREDITO,
                            VISTA.DEBITO_HISTORICO_BAJA, VISTA.CREDITO_HISTORICO_BAJA,
                            VISTA.DEBITO_ACUMULADA_BAJA, VISTA.CREDITO_ACUMULADA_BAJA,
                            VISTA.DEBITO_LIBROS_BAJA, VISTA.CREDITO_LIBROS_BAJA,
                            VISTA.DEBITO_RETIRADOS_BAJA, VISTA.CREDITO_RETIRADOS_BAJA,
                            VISTA.NIIF_DEBITO_IVA, VISTA.NIIF_CREDITO_IVA,
                            VISTA.NIIF_DEBITO_BASE, VISTA.NIIF_CREDITO_BASE,
                            VISTA.NIIF_CUENTACREDITOAJUSTE, VISTA.NIIF_CUENTADEBITOAJUSTE,
                            VISTA.NIIF_DEBITO_HISTORICO_BAJA, VISTA.NIIF_CREDITO_HISTORICO_BAJA,
                            VISTA.NIIF_DEBITO_ACUMULADA_BAJA, VISTA.NIIF_CREDITO_ACUMULADA_BAJA,
                            VISTA.NIIF_DEBITO_LIBROS_BAJA, VISTA.NIIF_CREDITO_LIBROS_BAJA,
                            VISTA.NIIF_DEBITO_RETIRADOS_BAJA, VISTA.NIIF_CREDITO_RETIRADOS_BAJA
                        )';
                       
        BEGIN
          BEGIN
               PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA => 'ALMACENCONTABILIDAD' , 
                                                UN_ACCION => 'IM' , 
                                                UN_MERGEUSING => MI_MERGEUSING , 
                                                UN_MERGEENLACE => MI_MERGEENLACE , 
                                                UN_MERGEEXISTE => MI_MERGEEXISTE,
                                                UN_MERGENOEXIS => MI_MERGENOEXISTE);
                                                
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
            END ;

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                   PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE,
                                              UN_ERROR_COD=>PCK_ERRORES.ERR_INSERTARANOSIGUIENTE,
                                              UN_REEMPLAZOS => MI_MSGERROR);
   END;
       
       MI_MERGEUSING := 'SELECT COMPANIA, CODIGOELEMENTO, '|| UN_ANIOFINAL ||' ANO, AJUSINFLACREDITO, AJUSINFLADEBITO, AJUSDEPRECCREDITO, AJUSDEPRECDEBITO, DEPRECCREDITO, DEPRECDEBITO, 
                            COSTOSALDB, COSTOSALCR, COSTOSALAJDB, COSTOSALAJCR, AJUSINFLADEBITOS, AJUSINFLACREDITOS, DEPRECDEBITOS, DEPRECCREDITOS, AJUSDEPRECDEBITOS, 
                            AJUSDEPRECCREDITOS, DEPACUMULADADB, DEPACUMULADACR, AJUSTEDEPRECIACIONDB, AJUSTEDEPRECIACIONCR, DEPRECDEBITOCOMODATO1, DEPRECCREDITOCOMODATO, 
                            DEPRECDEBITOSCOMODATO, DEPRECCREDITOSCOMODATO, DEPACUMULADADBCOMODATO, DEPACUMULADACRCOMODATO, DEPCUANTIAMIN_DEB, DEPCUANTIAMIN_CRE, CENTRO_COSTO
                        FROM INVENTARIOCONTABILIDAD
                        WHERE COMPANIA = '''|| UN_COMPANIA ||'''
                          AND ANO = '|| UN_ANIOINICIAL ||'';
       
       MI_MERGEENLACE := 'TABLA.COMPANIA          = VISTA.COMPANIA
                           AND TABLA.CODIGOELEMENTO = VISTA.CODIGOELEMENTO
                           AND TABLA.ANO            = VISTA.ANO';
                            
       MI_MERGEEXISTE := 'UPDATE SET
                        TABLA.AJUSINFLACREDITO        = VISTA.AJUSINFLACREDITO,
                        TABLA.AJUSINFLADEBITO         = VISTA.AJUSINFLADEBITO,
                        TABLA.AJUSDEPRECCREDITO       = VISTA.AJUSDEPRECCREDITO,
                        TABLA.AJUSDEPRECDEBITO        = VISTA.AJUSDEPRECDEBITO,
                        TABLA.DEPRECCREDITO           = VISTA.DEPRECCREDITO,
                        TABLA.DEPRECDEBITO            = VISTA.DEPRECDEBITO,
                        TABLA.COSTOSALDB              = VISTA.COSTOSALDB,
                        TABLA.COSTOSALCR              = VISTA.COSTOSALCR,
                        TABLA.COSTOSALAJDB            = VISTA.COSTOSALAJDB,
                        TABLA.COSTOSALAJCR            = VISTA.COSTOSALAJCR,
                        TABLA.AJUSINFLADEBITOS        = VISTA.AJUSINFLADEBITOS,
                        TABLA.AJUSINFLACREDITOS       = VISTA.AJUSINFLACREDITOS,
                        TABLA.DEPRECDEBITOS           = VISTA.DEPRECDEBITOS,
                        TABLA.DEPRECCREDITOS          = VISTA.DEPRECCREDITOS,
                        TABLA.AJUSDEPRECDEBITOS       = VISTA.AJUSDEPRECDEBITOS,
                        TABLA.AJUSDEPRECCREDITOS      = VISTA.AJUSDEPRECCREDITOS,
                        TABLA.DEPACUMULADADB          = VISTA.DEPACUMULADADB,
                        TABLA.DEPACUMULADACR          = VISTA.DEPACUMULADACR,
                        TABLA.AJUSTEDEPRECIACIONDB    = VISTA.AJUSTEDEPRECIACIONDB,
                        TABLA.AJUSTEDEPRECIACIONCR    = VISTA.AJUSTEDEPRECIACIONCR,
                        TABLA.DEPRECDEBITOCOMODATO1   = VISTA.DEPRECDEBITOCOMODATO1,
                        TABLA.DEPRECCREDITOCOMODATO   = VISTA.DEPRECCREDITOCOMODATO,
                        TABLA.DEPRECDEBITOSCOMODATO   = VISTA.DEPRECDEBITOSCOMODATO,
                        TABLA.DEPRECCREDITOSCOMODATO  = VISTA.DEPRECCREDITOSCOMODATO,
                        TABLA.DEPACUMULADADBCOMODATO  = VISTA.DEPACUMULADADBCOMODATO,
                        TABLA.DEPACUMULADACRCOMODATO  = VISTA.DEPACUMULADACRCOMODATO,
                        TABLA.DEPCUANTIAMIN_DEB       = VISTA.DEPCUANTIAMIN_DEB,
                        TABLA.DEPCUANTIAMIN_CRE       = VISTA.DEPCUANTIAMIN_CRE,
                        TABLA.CENTRO_COSTO            = VISTA.CENTRO_COSTO';
                        
        MI_MERGENOEXISTE:= ' INSERT (COMPANIA, CODIGOELEMENTO, ANO, AJUSINFLACREDITO, AJUSINFLADEBITO,
                        AJUSDEPRECCREDITO, AJUSDEPRECDEBITO, DEPRECCREDITO, DEPRECDEBITO,
                        COSTOSALDB, COSTOSALCR, COSTOSALAJDB, COSTOSALAJCR,
                        AJUSINFLADEBITOS, AJUSINFLACREDITOS, DEPRECDEBITOS, DEPRECCREDITOS,
                        AJUSDEPRECDEBITOS, AJUSDEPRECCREDITOS, DEPACUMULADADB, DEPACUMULADACR,
                        AJUSTEDEPRECIACIONDB, AJUSTEDEPRECIACIONCR, DEPRECDEBITOCOMODATO1, DEPRECCREDITOCOMODATO,
                        DEPRECDEBITOSCOMODATO, DEPRECCREDITOSCOMODATO, DEPACUMULADADBCOMODATO, DEPACUMULADACRCOMODATO,
                        DEPCUANTIAMIN_DEB, DEPCUANTIAMIN_CRE, CENTRO_COSTO
                    )
                    VALUES (VISTA.COMPANIA, VISTA.CODIGOELEMENTO, VISTA.ANO, VISTA.AJUSINFLACREDITO, VISTA.AJUSINFLADEBITO,
                        VISTA.AJUSDEPRECCREDITO, VISTA.AJUSDEPRECDEBITO, VISTA.DEPRECCREDITO, VISTA.DEPRECDEBITO,
                        VISTA.COSTOSALDB, VISTA.COSTOSALCR, VISTA.COSTOSALAJDB, VISTA.COSTOSALAJCR,
                        VISTA.AJUSINFLADEBITOS, VISTA.AJUSINFLACREDITOS, VISTA.DEPRECDEBITOS, VISTA.DEPRECCREDITOS,
                        VISTA.AJUSDEPRECDEBITOS, VISTA.AJUSDEPRECCREDITOS, VISTA.DEPACUMULADADB, VISTA.DEPACUMULADACR,
                        VISTA.AJUSTEDEPRECIACIONDB, VISTA.AJUSTEDEPRECIACIONCR, VISTA.DEPRECDEBITOCOMODATO1, VISTA.DEPRECCREDITOCOMODATO,
                        VISTA.DEPRECDEBITOSCOMODATO, VISTA.DEPRECCREDITOSCOMODATO, VISTA.DEPACUMULADADBCOMODATO, VISTA.DEPACUMULADACRCOMODATO,
                        VISTA.DEPCUANTIAMIN_DEB, VISTA.DEPCUANTIAMIN_CRE, VISTA.CENTRO_COSTO
                    )';                
                            
        BEGIN
          BEGIN
               PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA => 'INVENTARIOCONTABILIDAD' , 
                                                UN_ACCION => 'IM' , 
                                                UN_MERGEUSING => MI_MERGEUSING , 
                                                UN_MERGEENLACE => MI_MERGEENLACE , 
                                                UN_MERGEEXISTE => MI_MERGEEXISTE,
                                                UN_MERGENOEXIS => MI_MERGENOEXISTE);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
            END ;

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                   PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE,
                                              UN_ERROR_COD=>PCK_ERRORES.ERR_INSERTARANOSIGUIENTE,
                                              UN_REEMPLAZOS => MI_MSGERROR);
       END; 

       IF PCK_SYSMAN_UTL.FC_PAR( UN_COMPANIA => UN_COMPANIA,
                              UN_NOMBRE  => 'MANEJA INTERFAZ DE ALMACEN POR CENTRO DE COSTO',
                              UN_MODULO 	=> 96,
                              UN_FECHA_PAR => SYSDATE,
                              UN_IND_MAYUS => -1)='SI' 
       THEN
            
            MI_MERGEUSING := 'SELECT COMPANIA, CODIGOELEMENTO, TIPOMOVIMIENTO, 2026 ANO, CENTRO_COSTO, CUENTADEBITO, CUENTACREDITO, CUENTADEBITOAJUSTE, CUENTACREDITOAJUSTE, 
                                    DEBITO_BASE, CREDITO_BASE, CREDITO_IVA, DEBITO_IVA, NIIF_CUENTADEBITO, NIIF_CUENTACREDITO, NIIF_DEBITO_BASE, 
                                    NIIF_CREDITO_BASE, NIIF_CREDITO_IVA, NIIF_DEBITO_IVA, NIIF_CUENTADEBITOAJUSTE, NIIF_CUENTACREDITOAJUSTE, FUENTEDERECURSO
                                FROM ALMACENCONTABILIDADCC
                                WHERE COMPANIA = '''|| UN_COMPANIA ||'''
                                AND ANO = '|| UN_ANIOINICIAL ||'';
                                
            MI_MERGEENLACE := 'TABLA.COMPANIA               = VISTA.COMPANIA
                               AND TABLA.CODIGOELEMENTO     = VISTA.CODIGOELEMENTO
                               AND TABLA.TIPOMOVIMIENTO     = VISTA.TIPOMOVIMIENTO
                               AND TABLA.ANO                = VISTA.ANO
                               AND TABLA.CENTRO_COSTO       = VISTA.CENTRO_COSTO
                               AND TABLA.FUENTEDERECURSO    = VISTA.FUENTEDERECURSO';
                               
             MI_MERGEEXISTE := 'UPDATE SET
                        TABLA.CUENTADEBITO               = VISTA.CUENTADEBITO,
                        TABLA.CUENTACREDITO              = VISTA.CUENTACREDITO,
                        TABLA.CUENTADEBITOAJUSTE         = VISTA.CUENTADEBITOAJUSTE,
                        TABLA.CUENTACREDITOAJUSTE        = VISTA.CUENTACREDITOAJUSTE,
                        TABLA.DEBITO_BASE                = VISTA.DEBITO_BASE,
                        TABLA.CREDITO_BASE               = VISTA.CREDITO_BASE,
                        TABLA.CREDITO_IVA                = VISTA.CREDITO_IVA,
                        TABLA.DEBITO_IVA                 = VISTA.DEBITO_IVA,
                        TABLA.NIIF_CUENTADEBITO          = VISTA.NIIF_CUENTADEBITO,
                        TABLA.NIIF_CUENTACREDITO         = VISTA.NIIF_CUENTACREDITO,
                        TABLA.NIIF_DEBITO_BASE           = VISTA.NIIF_DEBITO_BASE,
                        TABLA.NIIF_CREDITO_BASE          = VISTA.NIIF_CREDITO_BASE,
                        TABLA.NIIF_CREDITO_IVA           = VISTA.NIIF_CREDITO_IVA,
                        TABLA.NIIF_DEBITO_IVA            = VISTA.NIIF_DEBITO_IVA,
                        TABLA.NIIF_CUENTADEBITOAJUSTE    = VISTA.NIIF_CUENTADEBITOAJUSTE,
                        TABLA.NIIF_CUENTACREDITOAJUSTE   = VISTA.NIIF_CUENTACREDITOAJUSTE';   

             MI_MERGENOEXISTE:= ' INSERT (
                        COMPANIA, CODIGOELEMENTO, TIPOMOVIMIENTO, ANO, CENTRO_COSTO,
                        CUENTADEBITO, CUENTACREDITO, CUENTADEBITOAJUSTE, CUENTACREDITOAJUSTE,
                        DEBITO_BASE, CREDITO_BASE, CREDITO_IVA, DEBITO_IVA,
                        NIIF_CUENTADEBITO, NIIF_CUENTACREDITO, NIIF_DEBITO_BASE, NIIF_CREDITO_BASE,
                        NIIF_CREDITO_IVA, NIIF_DEBITO_IVA,
                        NIIF_CUENTADEBITOAJUSTE, NIIF_CUENTACREDITOAJUSTE,
                        FUENTEDERECURSO
                    )
                    VALUES (
                        VISTA.COMPANIA, VISTA.CODIGOELEMENTO, VISTA.TIPOMOVIMIENTO, VISTA.ANO, VISTA.CENTRO_COSTO,
                        VISTA.CUENTADEBITO, VISTA.CUENTACREDITO, VISTA.CUENTADEBITOAJUSTE, VISTA.CUENTACREDITOAJUSTE,
                        VISTA.DEBITO_BASE, VISTA.CREDITO_BASE, VISTA.CREDITO_IVA, VISTA.DEBITO_IVA,
                        VISTA.NIIF_CUENTADEBITO, VISTA.NIIF_CUENTACREDITO, VISTA.NIIF_DEBITO_BASE, VISTA.NIIF_CREDITO_BASE,
                        VISTA.NIIF_CREDITO_IVA, VISTA.NIIF_DEBITO_IVA,
                        VISTA.NIIF_CUENTADEBITOAJUSTE, VISTA.NIIF_CUENTACREDITOAJUSTE,
                        VISTA.FUENTEDERECURSO
                    )';
                               
              BEGIN
                BEGIN
                     PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA => 'ALMACENCONTABILIDADCC' , 
                                                UN_ACCION => 'IM' , 
                                                UN_MERGEUSING => MI_MERGEUSING , 
                                                UN_MERGEENLACE => MI_MERGEENLACE , 
                                                UN_MERGEEXISTE => MI_MERGEEXISTE,
                                                UN_MERGENOEXIS => MI_MERGENOEXISTE);
                                                
                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                  END ;

                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                         PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE,
                                                    UN_ERROR_COD=>PCK_ERRORES.ERR_INSERTARANOSIGUIENTE,
                                                    UN_REEMPLAZOS => MI_MSGERROR);
             END;
             
             MI_CONDICION :=   'COMPANIA = '''|| UN_COMPANIA ||''' 
                                AND ANO  = ' || UN_ANIOFINAL ||'';

                MI_MSGERROR(1).CLAVE := 'ANIO';
                MI_MSGERROR(1).VALOR := UN_ANIOFINAL ;
                BEGIN
                  BEGIN
                    PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA      =>  'INVENTARIOCONTABILIDADCC',
                                                        UN_ACCION     =>  'E',
                                                        UN_CONDICION  =>  MI_CONDICION );
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                                   RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                  END;
                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN 
                      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                                 UN_ERROR_COD => PCK_ERRORES.ERR_ELIMINARANOSIGUIENTE,
                                                 UN_REEMPLAZOS => MI_MSGERROR
                    );
                END;
             
             MI_CAMPOS :='COMPANIA,
                          CODIGOELEMENTO,
                          ANO,
                          CENTRO_COSTO,
                          AJUSINFLACREDITO,
                          AJUSINFLADEBITO, 
                          AJUSDEPRECCREDITO, 
                          AJUSDEPRECDEBITO, 
                          DEPRECCREDITO, 
                          DEPRECDEBITO, 
                          COSTOSALDB, 
                          COSTOSALCR, 
                          COSTOSALAJDB, 
                          COSTOSALAJCR, 
                          DEPACUMULADADB, 
                          DEPACUMULADACR, 
                          AJUSTEDEPRECIACIONDB, 
                          AJUSTEDEPRECIACIONCR, 
                          AJUSINFLADEBITOS, 
                          AJUSINFLACREDITOS, 
                          DEPRECDEBITOS, 
                          DEPRECCREDITOS, 
                          AJUSDEPRECDEBITOS, 
                          AJUSDEPRECCREDITOS,
                          DATE_CREATED,
                          CREATED_BY';

             MI_VALORES := ' SELECT INVENTARIOCONTABILIDADCC.COMPANIA,
                                    INVENTARIOCONTABILIDADCC.CODIGOELEMENTO,
                                    '||UN_ANIOFINAL||',
                                    INVENTARIOCONTABILIDADCC.CENTRO_COSTO,
                                    INVENTARIOCONTABILIDADCC.AJUSINFLACREDITO,
                                    INVENTARIOCONTABILIDADCC.AJUSINFLADEBITO,
                                    INVENTARIOCONTABILIDADCC.AJUSDEPRECCREDITO,
                                    INVENTARIOCONTABILIDADCC.AJUSDEPRECDEBITO,
                                    INVENTARIOCONTABILIDADCC.DEPRECCREDITO,
                                    INVENTARIOCONTABILIDADCC.DEPRECDEBITO,
                                    INVENTARIOCONTABILIDADCC.COSTOSALDB,
                                    INVENTARIOCONTABILIDADCC.COSTOSALCR,
                                    INVENTARIOCONTABILIDADCC.COSTOSALAJDB,
                                    INVENTARIOCONTABILIDADCC.COSTOSALAJCR,
                                    INVENTARIOCONTABILIDADCC.DEPACUMULADADB,
                                    INVENTARIOCONTABILIDADCC.DEPACUMULADACR,
                                    INVENTARIOCONTABILIDADCC.AJUSTEDEPRECIACIONDB,
                                    INVENTARIOCONTABILIDADCC.AJUSTEDEPRECIACIONCR,
                                    INVENTARIOCONTABILIDADCC.AJUSINFLADEBITOS,
                                    INVENTARIOCONTABILIDADCC.AJUSINFLACREDITOS,
                                    INVENTARIOCONTABILIDADCC.DEPRECDEBITOS,
                                    INVENTARIOCONTABILIDADCC.DEPRECCREDITOS,
                                    INVENTARIOCONTABILIDADCC.AJUSDEPRECDEBITOS,
                                    INVENTARIOCONTABILIDADCC.AJUSDEPRECCREDITOS,
                                    SYSDATE,
                                    '''||UN_USUARIO||'''
                                  FROM INVENTARIOCONTABILIDADCC
                                  WHERE INVENTARIOCONTABILIDADCC.COMPANIA = ''' || UN_COMPANIA ||'''
                                  AND INVENTARIOCONTABILIDADCC.ANO        = '   || UN_ANIOINICIAL;           
              BEGIN
                BEGIN

                     PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME (UN_TABLA   => 'INVENTARIOCONTABILIDADCC'
                                                             ,UN_ACCION  => 'IS'
                                                             ,UN_CAMPOS  => MI_CAMPOS
                                                             ,UN_VALORES => MI_VALORES);  

                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                  END ;

                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                         PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE,
                                                    UN_ERROR_COD=>PCK_ERRORES.ERR_INSERTARANOSIGUIENTE,
                                                    UN_REEMPLAZOS => MI_MSGERROR);
             END;
       END IF;
       
       IF PCK_SYSMAN_UTL.FC_PAR( UN_COMPANIA => UN_COMPANIA,
                              UN_NOMBRE  => 'MANEJA NIIF EN ALMACEN',
                              UN_MODULO 	=> 96,
                              UN_FECHA_PAR => SYSDATE,
                              UN_IND_MAYUS => -1)='SI' 
       THEN

            BEGIN
            
            MI_MERGEUSING := 'SELECT COMPANIA, CODIGOELEMENTO, '|| UN_ANIOFINAL ||' ANO, TIPOACTIVO, CUENTADEBITO, CUENTACREDITO, DEBITODIF_PASIVA, CREDITODIF_PASIVA, 
                                    DEBITODIF_ACTIVA, CREDITODIF_ACTIVA, DEBITODIF_POSITIVA, CREDITODIF_POSITIVA, DEBITODIF_NEGATIVA, CREDITODIF_NEGATIVA, 
                                    CENTRO_COSTO, CTADEBITOVLRACTIVO, CTACREDITOVLRACTIVO, BODEGA
                                FROM NIIF_INVENTARIOCONTA
                                WHERE COMPANIA = '''|| UN_COMPANIA ||'''
                                  AND ANO = '|| UN_ANIOINICIAL ||'';
            
             MI_MERGEENLACE := 'TABLA.COMPANIA       = VISTA.COMPANIA
                               AND TABLA.CODIGOELEMENTO = VISTA.CODIGOELEMENTO
                               AND TABLA.ANO            = VISTA.ANO
                               AND TABLA.TIPOACTIVO     = VISTA.TIPOACTIVO
                               AND TABLA.CENTRO_COSTO   = VISTA.CENTRO_COSTO
                               AND TABLA.BODEGA         = VISTA.BODEGA';
                               
             MI_MERGEEXISTE := 'UPDATE SET
                        TABLA.CUENTADEBITO          = VISTA.CUENTADEBITO,
                        TABLA.CUENTACREDITO         = VISTA.CUENTACREDITO,
                        TABLA.DEBITODIF_PASIVA      = VISTA.DEBITODIF_PASIVA,
                        TABLA.CREDITODIF_PASIVA     = VISTA.CREDITODIF_PASIVA,
                        TABLA.DEBITODIF_ACTIVA      = VISTA.DEBITODIF_ACTIVA,
                        TABLA.CREDITODIF_ACTIVA     = VISTA.CREDITODIF_ACTIVA,
                        TABLA.DEBITODIF_POSITIVA    = VISTA.DEBITODIF_POSITIVA,
                        TABLA.CREDITODIF_POSITIVA   = VISTA.CREDITODIF_POSITIVA,
                        TABLA.DEBITODIF_NEGATIVA    = VISTA.DEBITODIF_NEGATIVA,
                        TABLA.CREDITODIF_NEGATIVA   = VISTA.CREDITODIF_NEGATIVA,
                        TABLA.CTADEBITOVLRACTIVO    = VISTA.CTADEBITOVLRACTIVO,
                        TABLA.CTACREDITOVLRACTIVO   = VISTA.CTACREDITOVLRACTIVO';                  
              
              MI_MERGENOEXISTE:= ' INSERT (
                            COMPANIA, CODIGOELEMENTO, ANO, TIPOACTIVO,
                            CUENTADEBITO, CUENTACREDITO,
                            DEBITODIF_PASIVA, CREDITODIF_PASIVA,
                            DEBITODIF_ACTIVA, CREDITODIF_ACTIVA,
                            DEBITODIF_POSITIVA, CREDITODIF_POSITIVA,
                            DEBITODIF_NEGATIVA, CREDITODIF_NEGATIVA,
                            CENTRO_COSTO, CTADEBITOVLRACTIVO,
                            CTACREDITOVLRACTIVO, BODEGA
                        )
                        VALUES (
                            VISTA.COMPANIA, VISTA.CODIGOELEMENTO, VISTA.ANO, VISTA.TIPOACTIVO,
                            VISTA.CUENTADEBITO, VISTA.CUENTACREDITO,
                            VISTA.DEBITODIF_PASIVA, VISTA.CREDITODIF_PASIVA,
                            VISTA.DEBITODIF_ACTIVA, VISTA.CREDITODIF_ACTIVA,
                            VISTA.DEBITODIF_POSITIVA, VISTA.CREDITODIF_POSITIVA,
                            VISTA.DEBITODIF_NEGATIVA, VISTA.CREDITODIF_NEGATIVA,
                            VISTA.CENTRO_COSTO, VISTA.CTADEBITOVLRACTIVO,
                            VISTA.CTACREDITOVLRACTIVO, VISTA.BODEGA
                        )';
                       
              BEGIN
                BEGIN
                     PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA => 'NIIF_INVENTARIOCONTA' , 
                                                UN_ACCION => 'IM' , 
                                                UN_MERGEUSING => MI_MERGEUSING , 
                                                UN_MERGEENLACE => MI_MERGEENLACE , 
                                                UN_MERGEEXISTE => MI_MERGEEXISTE,
                                                UN_MERGENOEXIS => MI_MERGENOEXISTE);
                                                
                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                  END ;

                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                         PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE,
                                                    UN_ERROR_COD=>PCK_ERRORES.ERR_INSERTARANOSIGUIENTE,
                                                    UN_REEMPLAZOS => MI_MSGERROR);
             END; 
            END;
       END IF;
       
END PR_PREPARARCONTABI_SIGANO;

FUNCTION FC_CONTABILIZARPORPLANOESP(
/*
    NAME              : FC_CONTABILIZARPORPLANOESP
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
    DATE MIGRADOR     : 03/12/2018
    TIME              : 11:00 AM
    SOURCE MODULE     : 
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Función que llena la tabla temporal PlanaAjustes y realiza el proceso
                        de contabilizar, Devuelve un CLOB con las inconsistencias o comprobantes.
                        Es copia de la funcion FC_CONTABILIZARPORPLANO
    PARAMETERS        :

    MODIFICATIONS     :

    @NAME:contabilizarPorPlanoEsp
    @METHOD:  POST
*/

     UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_RESUMIDO         IN PCK_SUBTIPOS.TI_LOGICO
    ,UN_SINPPTAL         IN PCK_SUBTIPOS.TI_LOGICO
    ,UN_TERCERODETALLE   IN PCK_SUBTIPOS.TI_LOGICO
    ,UN_CONCILIAR        IN PCK_SUBTIPOS.TI_LOGICO := 0
    ,UN_PLANO            IN CLOB
    ,UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
) RETURN CLOB

AS
    MI_T_SPLIT		         PCK_SYSMAN_UTL.T_SPLIT;
    MI_CUENTA                PCK_SUBTIPOS.TI_ENTERO;
    MI_RNAUX                 VARCHAR2(2000);
    MI_PARESPACIOSESPPLANO   PARAMETRO.VALOR%TYPE;
    MI_PARCREARCOMPPLANO465  BOOLEAN DEFAULT FALSE;
    MI_PARSIMPLIFICAINTER    BOOLEAN DEFAULT FALSE;
    MI_NUM_CAR_DETALLE       PCK_SUBTIPOS.TI_ENTERO;
    MI_RNTIPO                COMPROBANTE_CNT.TIPO%TYPE;
    MI_RNCODIGO_CUENTA       PCK_SUBTIPOS.TI_CODIGOCONTA;
    MI_RNFECHA               DATE;
    MI_FECHAANT              DATE;
    MI_RNFECHACONSIGNACION   DATE;
    MI_MES                   PCK_SUBTIPOS.TI_ENTERO;
    MI_RNBANCO               VARCHAR2(100);
    MI_RNNUMERO              PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT;
    MI_REEMPLAZOS            PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_EXISTEDATO            VARCHAR2(1 CHAR);
    MI_POS                   PCK_SUBTIPOS.TI_ENTERO;
    MI_RNANO                 PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_RNVALORDEBITO         PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
    MI_RNVALORCREDITO        PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
    MI_RNTERCERO             PCK_SUBTIPOS.TI_TERCERO;
    MI_RNSUCURSAL            PCK_SUBTIPOS.TI_SUCURSAL;
    MI_RNCENTRO_COSTO        PCK_SUBTIPOS.TI_CENTRO_COSTO;
    MI_RNAUXILIAR            PCK_SUBTIPOS.TI_AUXILIAR;
    MI_RNNRO_DOCUMENTO       PCK_SUBTIPOS.TI_NRODOCUMENTO;
    MI_RNDESCRIPCION         PCK_SUBTIPOS.TI_DESCRIPCION;
    MI_RNCUENTAPPTAL         PCK_SUBTIPOS.TI_CODIGOPPTAL;
    MI_AUX                   PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_RTA                   CLOB;
    MI_PINCONSISTENCIAS      CLOB;
    MI_INCONSISTENCIA        BOOLEAN;
    MI_CLASE                 PCK_SUBTIPOS.TI_CLASECUENTACONTA;
    MI_CUENTAPPTAL           PLAN_CONTABLE.CUENTA_PPTAL%TYPE;
    MI_NUMREGISTRO           PCK_SUBTIPOS.TI_ENTERO;
    MI_RNCONSECUTIVO         PCK_SUBTIPOS.TI_ENTERO;
    MI_TABLA                 PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICIONACME         PCK_SUBTIPOS.TI_CONDICION;
    MI_FILAS                 PCK_SUBTIPOS.TI_ENTERO;
    MI_VALORES               PCK_SUBTIPOS.TI_VALORES;
    MI_NATURALEZA            PCK_SUBTIPOS.TI_NATURALEZACONTA;
    MI_RS                    SYS_REFCURSOR;
    MI_ETAPA                 VARCHAR2(150 CHAR);
    MI_RESULTADORECORD       VARCHAR2(500 CHAR);
    MI_NITHEADER             VARCHAR2(25 CHAR);
    MI_SUCURSALHEADER        VARCHAR2(25 CHAR);



    MI_NITCONTA              VARCHAR2(25 CHAR);
    MI_SUCURSALCONTA         VARCHAR2(25 CHAR);
BEGIN
    BEGIN
        BEGIN
            SELECT NITCOMPANIA
            INTO   MI_NITHEADER
            FROM   COMPANIA
            WHERE  CODIGO = UN_COMPANIA;

            MI_NITHEADER := REPLACE(MI_NITHEADER,'.','');
            IF INSTR(MI_NITHEADER, '-') > 0 THEN
                MI_NITHEADER := SUBSTR(MI_NITHEADER,1,INSTR(MI_NITHEADER, '-') -1);
            END IF;

            SELECT SUCURSAL
            INTO   MI_SUCURSALHEADER
            FROM   TERCERO
            WHERE  COMPANIA = UN_COMPANIA
              AND  NIT = MI_NITHEADER;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
        --No se encontró el tercero correspondiente al nit: --NIT--,Debe configurar el Nit de la entidad como tercero.
        MI_REEMPLAZOS(1).CLAVE := 'NIT';
        MI_REEMPLAZOS(1).VALOR := MI_NITHEADER;

        PCK_ERR_MSG.RAISE_WITH_MSG
                  ( UN_EXC_COD => SQLCODE
                  , UN_TABLAERROR => 'TERCERO'
                  , UN_ERROR_COD  => PCK_ERRORES.ERRR_PLANONITHEADER
                  , UN_REEMPLAZOS => MI_REEMPLAZOS
                  );

    END;


    MI_PARESPACIOSESPPLANO := NVL(PCK_SYSMAN_UTL.FC_PAR
                (UN_COMPANIA    => UN_COMPANIA
                ,UN_NOMBRE      => 'ESPACIOS ESPECIALES EN FECHA PLANO'
                ,UN_MODULO      => PCK_DATOS.MODULOCONTABILIDAD
                ,UN_FECHA_PAR   => SYSDATE ), 'NO');

    MI_PARCREARCOMPPLANO465 := CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR
                (UN_COMPANIA    => UN_COMPANIA
                ,UN_NOMBRE      => 'CREAR COMPROBANTE CON EL TERCERO ARCHIVO PLANO COLUMNA 465'
                ,UN_MODULO      => PCK_DATOS.MODULOCONTABILIDAD
                ,UN_FECHA_PAR   => SYSDATE ), 'NO') = 'SI' THEN TRUE ELSE FALSE END;

    MI_PARSIMPLIFICAINTER := CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR
                (UN_COMPANIA    => UN_COMPANIA
                ,UN_NOMBRE      => 'SIMPLIFICAR INTERFACE GENERAL'
                ,UN_MODULO      => PCK_DATOS.MODULOCONTABILIDAD
                ,UN_FECHA_PAR   => SYSDATE ), 'NO') = 'SI' THEN TRUE ELSE FALSE END;

    IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA    => UN_COMPANIA
                    ,UN_NOMBRE      => 'DETALLE DE ARCHIVO PLANO DE 250 CARACTERES'
                    ,UN_MODULO      => PCK_DATOS.MODULOCONTABILIDAD
                    ,UN_FECHA_PAR   => SYSDATE ), 'NO') = 'SI' THEN

        MI_NUM_CAR_DETALLE := 250 - 64;
    ELSE
        MI_NUM_CAR_DETALLE := 0;
    END IF;
    MI_ETAPA := '01';

    MI_NUMREGISTRO := 1;
    MI_RNCONSECUTIVO := 1;
    MI_FECHAANT := TO_DATE('01/01/1900', 'DD/MM/YYYY');
    MI_INCONSISTENCIA := FALSE;
    MI_PINCONSISTENCIAS := TO_CLOB('Las siguientes inconsistencias se presentan en el archivo plano que se está tratando de subir a Stefanini Sysman:' || CHR(13) || CHR(10));

    MI_T_SPLIT := PCK_SYSMAN_UTL.FC_SPLIT_SYS
                    (UN_LISTA        => '' || UN_PLANO || ''
                    ,UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);

    MI_CUENTA := MI_T_SPLIT.COUNT;
    MI_ETAPA := '02';
    FOR i IN 1..MI_CUENTA LOOP
        MI_RNAUX := NVL(MI_T_SPLIT(i),' ');
        MI_RNAUX := REPLACE(REPLACE(MI_RNAUX ,CHR(13),''),CHR(10),'');


        IF NVL(MI_RNAUX,' ') <> ' ' AND LENGTH(TRIM(MI_RNAUX))>0 THEN
            MI_RNTIPO := TRIM(SUBSTR(MI_RNAUX, 1, 3));
            MI_RNCODIGO_CUENTA := TRIM(SUBSTR(MI_RNAUX, 24, 16));
            MI_RNFECHA := TO_DATE(SUBSTR(MI_RNAUX, 40, 10), 'DD/MM/YYYY');
            MI_ETAPA := '03';
            MI_MES := PCK_SYSMAN_UTL.FC_MES(MI_RNFECHA);
            IF LENGTH(MI_RNBANCO)>210 THEN
               MI_RNBANCO := TRIM(SUBSTR(MI_RNAUX, 210 + MI_NUM_CAR_DETALLE, 3));
           ELSE
               MI_RNBANCO :='';
           END IF;

            IF PCK_CONTABILIDAD4.FC_VERIFICAPERIODO(UN_COMPANIA => UN_COMPANIA
                                                   ,UN_ANO      => PCK_SYSMAN_UTL.FC_ANIO(MI_RNFECHA)
                                                   ,UN_MES      => PCK_SYSMAN_UTL.FC_MES(MI_RNFECHA)) = 0 THEN
                MI_ETAPA := '04';
                BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                    MI_REEMPLAZOS(1).CLAVE := 'ANO';
                    MI_REEMPLAZOS(1).VALOR := PCK_SYSMAN_UTL.FC_ANIO(MI_RNFECHA);
                    MI_REEMPLAZOS(2).CLAVE := 'MES';
                    MI_REEMPLAZOS(2).VALOR := PCK_SYSMAN_UTL.FC_MES(MI_RNFECHA);
                    PCK_ERR_MSG.RAISE_WITH_MSG
                              ( UN_EXC_COD => SQLCODE
                              , UN_TABLAERROR => 'ANO'
                              , UN_ERROR_COD => PCK_ERRORES.ERROR_GRAL_MESCERRADO
                              , UN_REEMPLAZOS => MI_REEMPLAZOS
                              );
                END;

            END IF;

            IF UN_RESUMIDO <> 0 THEN
                MI_ETAPA := '05';
                MI_RNNUMERO := TO_NUMBER(PCK_SYSMAN_UTL.FC_ANIO(MI_RNFECHA) || PCK_SYSMAN_UTL.FC_MES(MI_RNFECHA) || '00' || PCK_SYSMAN_UTL.FC_DIA(MI_RNFECHA));

                IF MI_FECHAANT <> MI_RNFECHA THEN
                    BEGIN
                        SELECT DISTINCT 'X'
                        INTO   MI_EXISTEDATO
                        FROM   COMPROBANTE_CNT
                        WHERE  COMPANIA = UN_COMPANIA
                          AND  TIPO =  MI_RNTIPO
                          AND  TRUNC(FECHA) = MI_RNFECHA;
                    EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_EXISTEDATO := ' ';
                    END;
                    MI_ETAPA := '06';
                    IF NVL(MI_EXISTEDATO, ' ') = 'X' THEN
                        --Esta enviando comprobante resumido y ya existen Comprobantes tipo "  & " a la fecha "  & "  Por favor Borrelos en contabilidad o eliminelo del plano ", vbCritical
                        BEGIN
                            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                            MI_REEMPLAZOS(1).CLAVE := 'TIPO';
                            MI_REEMPLAZOS(1).VALOR := MI_RNTIPO;
                            MI_REEMPLAZOS(2).CLAVE := 'FECHA';
                            MI_REEMPLAZOS(2).VALOR := MI_RNFECHA;
                            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                                        UN_ERROR_COD  => PCK_ERRORES.ERRR_PLANOCOMPRESUMENEXIST,
                                                        UN_REEMPLAZOS => MI_REEMPLAZOS);
                        END;
                    END IF;
                END IF;
                MI_ETAPA := '07';
                MI_FECHAANT := MI_RNFECHA;
            ELSE
                MI_ETAPA := '08';                
                MI_RNNUMERO :=TO_NUMBER(TO_CHAR(SUBSTR(MI_RNAUX, 4, 20)));
            END IF;

            MI_RNANO := PCK_SYSMAN_UTL.FC_ANIO(MI_RNFECHA);
            MI_ETAPA := '09';
            MI_RNVALORDEBITO := NVL(TO_NUMBER(SUBSTR(MI_RNAUX, 50, 18)), 0);
            MI_POS := INSTR(SUBSTR(MI_RNAUX, 50, 18), '-');
            IF MI_POS > 0 THEN
                MI_RNVALORDEBITO := NVL(TO_NUMBER(SUBSTR(MI_RNAUX, 50 + MI_POS, 18 - MI_POS)), 0) * -1;
            END IF;

            MI_RNVALORCREDITO := NVL(TO_NUMBER(SUBSTR(MI_RNAUX, 68, 18)), 0);
            MI_ETAPA := '10';
            MI_POS := INSTR(SUBSTR(MI_RNAUX, 68, 18), '-');
            IF MI_POS > 0 THEN
                MI_RNVALORDEBITO := NVL(TO_NUMBER(SUBSTR(MI_RNAUX, 68 + MI_POS, 18 - MI_POS)), 0) * -1;
            END IF;

            MI_ETAPA := '11';
            IF MI_PARCREARCOMPPLANO465 THEN
                MI_RNTERCERO := TRIM(SUBSTR(MI_RNAUX, 465, 11));
            ELSE
                MI_RNTERCERO := TRIM(SUBSTR(MI_RNAUX, 86, 11));
            END IF;
            IF MI_RNTERCERO = RPAD('9',11,'9') THEN --Si vienen 11 9 entonces colocar 20 9
                MI_RNTERCERO := PCK_DATOS.CONS_TERCERO;
            END IF;

            MI_ETAPA := '12';
            MI_RNSUCURSAL := TRIM(SUBSTR(MI_RNAUX, 97, 3));
            MI_RNCENTRO_COSTO := TRIM(SUBSTR(MI_RNAUX, 100, 10));

            IF MI_RNCENTRO_COSTO = RPAD('9',10,'9') THEN --Si vienen 10 9 entonces colocar 20 9
                MI_RNCENTRO_COSTO := PCK_DATOS.CONS_CENTRO;
            END IF;

            MI_ETAPA := '13';
            MI_RNAUXILIAR := TRIM(SUBSTR(MI_RNAUX, 110, 16));
            MI_RNNRO_DOCUMENTO := TRIM(SUBSTR(MI_RNAUX, 126, 30));
            MI_RNDESCRIPCION := TRIM(SUBSTR(MI_RNAUX, 156, 500 + MI_NUM_CAR_DETALLE));
            MI_RNCUENTAPPTAL := TRIM(SUBSTR(MI_RNAUX, 656 + MI_NUM_CAR_DETALLE, 16));

            IF MI_RNAUXILIAR = '9999999999999999' THEN --Si vienen 16 9 entonces colocar 20 9
                MI_ETAPA := '14';
                MI_RNAUXILIAR := PCK_DATOS.CONS_AUXILIAR;
            ELSE
                BEGIN
                    SELECT DISTINCT 'X'
                    INTO   MI_EXISTEDATO
                    FROM   AUXILIAR
                    WHERE  COMPANIA = UN_COMPANIA
                      AND  ANO = MI_RNANO
                      AND  CODIGO  = MI_RNAUXILIAR;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_EXISTEDATO := ' ';
                END;

                MI_ETAPA := '15';
                IF NVL(MI_EXISTEDATO, ' ') = ' ' THEN

                    MI_AUX := 1;
                    WHILE MI_AUX < 16 LOOP
                        MI_RNAUXILIAR := TRIM(SUBSTR(MI_RNAUXILIAR, MI_AUX, 16));
                        MI_AUX := MI_AUX + 1;

                        BEGIN
                            SELECT DISTINCT 'X'
                            INTO   MI_EXISTEDATO
                            FROM   AUXILIAR
                            WHERE  COMPANIA = UN_COMPANIA
                              AND  ANO = MI_RNANO
                              AND  CODIGO  = MI_RNAUXILIAR;
                        EXCEPTION WHEN NO_DATA_FOUND THEN
                            MI_EXISTEDATO := ' ';
                        END;

                        IF NVL(MI_EXISTEDATO, ' ') = ' ' THEN
                           MI_RNAUXILIAR := TRIM(SUBSTR(MI_RNAUX, 110, 16));
                        ELSE
                            MI_AUX := 17;
                        END IF;
                    END LOOP;
                END IF;
            END IF;
            MI_ETAPA := '16';
            IF MI_PARESPACIOSESPPLANO = 'NO' THEN
                MI_ETAPA := '17';
                IF PCK_SYSMAN_UTL.FC_ISDATE(TRIM(SUBSTR(MI_RNAUX, 201 + MI_NUM_CAR_DETALLE, 10)), 'DD/MM/YYYY') <> 0 THEN
                    MI_RNFECHACONSIGNACION := TO_DATE(TRIM(SUBSTR(MI_RNAUX, 201 + MI_NUM_CAR_DETALLE, 10)), 'DD/MM/YYYY');
                ELSE
                    MI_RNFECHACONSIGNACION := TO_DATE(SUBSTR(MI_RNAUX, 40, 10), 'DD/MM/YYYY');
                END IF;
            ELSE
                MI_ETAPA := '18';
                IF PCK_SYSMAN_UTL.FC_ISDATE(TRIM(SUBSTR(MI_RNAUX, 736 + MI_NUM_CAR_DETALLE, 10)), 'DD/MM/YYYY') <> 0 THEN
                    MI_RNFECHACONSIGNACION := TO_DATE(TRIM(SUBSTR(MI_RNAUX, 736 + MI_NUM_CAR_DETALLE, 10)), 'DD/MM/YYYY');
                ELSE
                    MI_RNFECHACONSIGNACION := TO_DATE(SUBSTR(MI_RNAUX, 40, 10), 'DD/MM/YYYY');
                END IF;
            END IF;
            MI_ETAPA := '19';
            BEGIN
                SELECT CLASECUENTA, CUENTA_PPTAL
                INTO   MI_CLASE, MI_CUENTAPPTAL
                FROM   PLAN_CONTABLE
                WHERE  COMPANIA = UN_COMPANIA
                  AND  ANO = MI_RNANO
                  AND  CODIGO = MI_RNCODIGO_CUENTA;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_INCONSISTENCIA := TRUE;
                MI_PINCONSISTENCIAS :=MI_PINCONSISTENCIAS ||  TO_CLOB('El registro No. '|| MI_NUMREGISTRO ||' Presenta problemas con el codigo de cuenta '|| MI_RNCODIGO_CUENTA ||' No existe, Por favor revise configuración en la generación del archivo plano'  || CHR(13) || CHR(10));
            END;

            MI_ETAPA := '20';
            IF NVL(MI_RNTIPO,' ') = ' ' THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El registro No. '|| MI_NUMREGISTRO ||' NO tiene tipo de comprobante.' || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;
            MI_ETAPA := '21';
            IF MI_RNNUMERO = 0 THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El registro No. '|| MI_NUMREGISTRO ||' NO tiene número de comprobante.'  || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;
            IF NVL(MI_RNCODIGO_CUENTA, ' ') = ' ' THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El registro No. '|| MI_NUMREGISTRO ||' NO tiene código de cuenta contable.' || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;
            IF NVL(MI_RNTERCERO, ' ') = ' ' THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El registro No. '|| MI_NUMREGISTRO ||' NO tiene código de tercero.'  || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;
            IF NVL(MI_RNSUCURSAL, ' ') = ' ' THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El registro No. '|| MI_NUMREGISTRO ||' No tiene código de sucursal del tercero.'  || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;

            MI_ETAPA := '22';
            IF NVL(MI_RNCENTRO_COSTO, ' ') = ' ' THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El registro No. '|| MI_NUMREGISTRO ||' No tiene código de centro de costo.'  || CHR(13) || CHR(10));
                MI_RNCENTRO_COSTO := PCK_DATOS.CONS_CENTRO;
                MI_INCONSISTENCIA := FALSE;
            END IF;
            IF NVL(MI_RNAUXILIAR, ' ') = '' THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El registro No. '|| MI_NUMREGISTRO ||' No tiene código de auxiliar general.'  || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;
            IF NVL(MI_RNDESCRIPCION,' ') = ' ' THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El registro No. '|| MI_NUMREGISTRO ||' No tiene descripción.'  || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;

            MI_ETAPA := '23';
            IF NVL(MI_RNCUENTAPPTAL, ' ') = ' ' THEN
                IF MI_CLASE = 'P' OR MI_CLASE = 'N' THEN
                    MI_RNCUENTAPPTAL := MI_CUENTAPPTAL;
                END IF;
            END IF;

            MI_ETAPA := '24';
            BEGIN
                MI_NATURALEZA := PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RNCODIGO_CUENTA);
                MI_TABLA := 'TEMP_PLANA_AJUSTES';

                MI_CAMPOS := 'COMPANIA
                             ,ANO
                             ,TIPO_CPTE
                             ,COMPROBANTE
                             ,CONSECUTIVO
                             ,CUENTA
                             ,NATURALEZA
                             ,FECHA
                             ,DESCRIPCION
                             ,VALOR_DEBITO
                             ,VALOR_CREDITO
                             ,NRO_DOCUMENTO
                             ,TERCERO
                             ,SUCURSAL
                             ,CENTRO_COSTO
                             ,AUXILIAR
                             ,FECHA_CONSIGNACIONPLANO';

                MI_VALORES := '  '''|| UN_COMPANIA ||'''
                                , '|| MI_RNANO ||'
                                , '''|| MI_RNTIPO ||'''
                                , '|| MI_RNNUMERO ||'
                                , '|| MI_RNCONSECUTIVO ||'
                                , '''|| MI_RNCODIGO_CUENTA ||'''
                                , '''|| MI_NATURALEZA ||'''
                                , TO_DATE(''' || TO_CHAR(MI_RNFECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                                ,'''||  MI_RNDESCRIPCION || '''
                                ,'|| MI_RNVALORDEBITO ||'
                                ,'|| MI_RNVALORCREDITO ||'
                                ,'''|| MI_RNNRO_DOCUMENTO ||'''
                                ,'''|| MI_RNTERCERO ||'''
                                ,'''|| MI_RNSUCURSAL ||'''
                                ,'''|| MI_RNCENTRO_COSTO ||'''
                                ,'''|| MI_RNAUXILIAR ||'''
                                , TO_DATE(''' || TO_CHAR(MI_RNFECHACONSIGNACION, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                                ';
                IF NVL(MI_RNCUENTAPPTAL, ' ') <> ' ' THEN
                    MI_CAMPOS := MI_CAMPOS ||' ,CUENTAPPTAL
                                               ,EJECUCION_DEBITO
                                               ,EJECUCION_CREDITO ';

                    MI_VALORES := MI_VALORES || ' ,'''|| MI_RNCUENTAPPTAL ||'''
                                                  ,'|| MI_RNVALORDEBITO ||'
                                                  ,'|| MI_RNVALORCREDITO ||'  ';

                END IF;

                MI_ETAPA := '25';
                BEGIN
                    MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                          UN_ACCION  => 'I',
                                                          UN_CAMPOS  => MI_CAMPOS,
                                                          UN_VALORES => MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                --Se presentó error al insertar el tipo de comprobante: --TIPO-- ,Comprobante: --COMPROBANTE-- en plan ajustes.
                MI_REEMPLAZOS(1).CLAVE := 'TIPO';
                MI_REEMPLAZOS(1).VALOR := MI_RNTIPO;
                MI_REEMPLAZOS(2).CLAVE := 'COMPROBANTE';
                MI_REEMPLAZOS(2).VALOR := MI_RNNUMERO;
                MI_REEMPLAZOS(3).CLAVE := 'LINEA';
                MI_REEMPLAZOS(3).VALOR := MI_NUMREGISTRO;
                PCK_ERR_MSG.RAISE_WITH_MSG
                          ( UN_EXC_COD => SQLCODE
                          , UN_TABLAERROR => MI_TABLA
                          , UN_ERROR_COD => PCK_ERRORES.ERRR_PLANOINSPLANAAJUSTE
                          , UN_REEMPLAZOS => MI_REEMPLAZOS
                          );
            END;
            MI_ETAPA := '26';
            IF MI_FILAS > 0 THEN
                MI_RNCONSECUTIVO := MI_RNCONSECUTIVO + 1;
            END IF;

            MI_NUMREGISTRO := MI_NUMREGISTRO + 1;
        END IF;--Siguiente registro
        --<<SIGUE>>
    END LOOP;
    MI_ETAPA := '27';
    IF MI_INCONSISTENCIA THEN
        MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('Proceso de contabilizar no realizado' || CHR(13) || CHR(10));
        MI_RTA := MI_PINCONSISTENCIAS;
        RETURN MI_RTA;
    END IF;

    BEGIN
        MI_ETAPA := '28';
        MI_INCONSISTENCIA := FALSE;
        MI_PINCONSISTENCIAS := TO_CLOB('Las siguientes inconsistencias se presentan en el archivo plano que se está tratando de subir a Stefanini Sysman:' || CHR(13) || CHR(10));
        <<DATOSINCONSISTENTES>>
        FOR MI_RS IN
            (SELECT TP.COMPANIA,  TP.ANO,  TP.TIPO_CPTE,  TP.COMPROBANTE, TP.TERCERO ,  TP.CENTRO_COSTO , TP.AUXILIAR, TP.SUCURSAL
              , TC.CODIGO TIPOCOMPROBANTE
              , CC.NUMERO NUMCOMPRO
              , T.NIT     NIT
              , CCO.CODIGO CENTROCODIGO
              , AX.CODIGO AUXILIARCODIGO
              , REFE.CODIGO REFERENCIACODIGO
              , FUENTE.CODIGO FUENTECODIGO
            FROM TEMP_PLANA_AJUSTES TP
            LEFT JOIN TIPO_COMPROBANTE TC
                      ON TP.COMPANIA   = TC.COMPANIA
                      AND TP.TIPO_CPTE = TC.CODIGO
            LEFT JOIN COMPROBANTE_CNT CC
                      ON   TP.COMPANIA    = CC.COMPANIA
                      AND  TP.ANO         = CC.ANO
                      AND  TP.TIPO_CPTE   = CC.TIPO
                      AND  TP.COMPROBANTE = CC.NUMERO
            LEFT JOIN TERCERO T
                      ON  TP.COMPANIA = T.COMPANIA
                      AND TP.TERCERO  = T.NIT
                      AND TP.SUCURSAL = T.SUCURSAL
            LEFT JOIN CENTRO_COSTO CCO
                      ON  TP.COMPANIA     = CCO.COMPANIA
                      AND TP.ANO          = CCO.ANO
                      AND TP.CENTRO_COSTO = CCO.CODIGO
            LEFT JOIN AUXILIAR AX
                      ON  TP.COMPANIA = AX.COMPANIA
                      AND TP.ANO      = AX.ANO
                      AND TP.AUXILIAR = AX.CODIGO
            LEFT JOIN REFERENCIA REFE
                      ON  TP.COMPANIA   = REFE.COMPANIA
                      AND TP.ANO        = REFE.ANO
                      AND TP.REFERENCIA = REFE.CODIGO
            LEFT JOIN FUENTE_RECURSOS FUENTE
                      ON  TP.COMPANIA        = FUENTE.COMPANIA
                      AND TP.ANO             = FUENTE.ANO
                      AND TP.FUENTE_RECURSOS = FUENTE.CODIGO

            WHERE TC.CODIGO IS NULL
               OR CC.NUMERO = TP.COMPROBANTE
               OR T.NIT IS NULL
               OR CCO.CODIGO IS NULL
               OR AX.CODIGO IS NULL)
        LOOP
            MI_ETAPA := '29';
            IF MI_RS.TIPOCOMPROBANTE IS NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El tipo de comprobante '|| MI_RS.TIPO_CPTE || ' NO está creado en el sistema.' || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;

            IF MI_RS.NUMCOMPRO IS NOT NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El número de comprobante '|| MI_RS.COMPROBANTE || ' de tipo '|| MI_RS.TIPO_CPTE ||' YA existe en el sistema.' || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;

            IF MI_RS.NIT IS NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El tercero '|| MI_RS.TERCERO || ' sucursal '|| MI_RS.SUCURSAL ||' NO está creado en el sistema.' || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;

            IF MI_RS.CENTROCODIGO IS NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El centro de costo '|| MI_RS.CENTRO_COSTO || ' NO está creado en el sistema.' || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;
            MI_ETAPA := '30';
            IF MI_RS.AUXILIARCODIGO IS NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El auxiliar '|| MI_RS.AUXILIAR || ' NO está creado en el sistema.' || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;

            IF MI_RS.REFERENCIACODIGO IS NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('La referencia '|| MI_RS.REFERENCIACODIGO || ' NO está creada en el sistema.' || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;

            IF MI_RS.FUENTECODIGO IS NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('La fuente de Recursos '|| MI_RS.FUENTECODIGO || ' NO está creada en el sistema.' || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;
        END LOOP DATOSINCONSISTENTES;
    END;

    IF MI_INCONSISTENCIA THEN
        MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('Proceso de contabilizar no realizado' || CHR(13) || CHR(10));
        MI_RTA := MI_PINCONSISTENCIAS;
        RETURN MI_RTA;
    ELSE
        MI_RTA := '';
        MI_PINCONSISTENCIAS := TO_CLOB('Se crearon los siguientes comprobantes:' || CHR(13) || CHR(10));
        <<CONTABILIZAR>>
        FOR MI_RS IN
            (
                SELECT COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,
                       MAX(NRO_DOCUMENTO) NRO_DOCUMENTO,
                       MAX(FECHA) AS FEC,
                       MAX(TERCERO) AS TERDETALLE,
                       MAX(SUCURSAL)  AS SUC,
                       MAX(DESCRIPCION) AS DESCR
                FROM TEMP_PLANA_AJUSTES
                WHERE COMPANIA = UN_COMPANIA
                GROUP BY COMPANIA,ANO,TIPO_CPTE,COMPROBANTE
            )
        LOOP
            MI_ETAPA := '30A';
            /*MI_RESULTADORECORD := MI_RS.COMPANIA || 'ANO ' || MI_RS.ANO || ' TIPO ' || MI_RS.TIPO_CPTE ||
                                ' DOC ' || MI_RS.NRO_DOCUMENTO || ' COMP ' || MI_RS.COMPROBANTE || ' FEC ' || MI_RS.FEC || ' TER ' || MI_RS.TER || ' SUC ' || MI_RS.SUC || ' DESC ' || MI_RS.DESCR ;*/
            MI_ETAPA := '31';

            IF UN_TERCERODETALLE <> 0 THEN
                MI_NITCONTA := MI_RS.TERDETALLE;
                MI_SUCURSALCONTA := MI_RS.SUC;
            ELSE
                MI_NITCONTA := MI_NITHEADER;
                MI_SUCURSALCONTA := MI_SUCURSALHEADER;
            END IF;            
            MI_ETAPA := '32';
            MI_PINCONSISTENCIAS := TO_CLOB(PCK_CONTABILIZAR.FC_CONTABILIZAR
                        (UN_COMPANIA         => UN_COMPANIA
                        ,UN_TIPOCOMPROBANTE  => MI_RS.TIPO_CPTE
                        ,UN_NUMERO           => MI_RS.COMPROBANTE
                        ,UN_ANO              => MI_RS.ANO
                        ,UN_FECHA            => MI_RS.FEC
                        ,UN_TERCERO          => MI_NITCONTA
                        ,UN_SUCURSAL         => MI_SUCURSALCONTA
                        ,UN_DESCRIPCION      => MI_RS.DESCR
                        ,UN_SIMPLE           => CASE WHEN MI_PARSIMPLIFICAINTER THEN -1 ELSE 0 END 
                        ,UN_CONCILIAR        => UN_CONCILIAR
                        ,UN_INDIMPRESION     => 0
                        ,UN_INTOMITIRPPTAL   => UN_SINPPTAL
                        ,UN_NRO_DOCUMENTO    => MI_RS.NRO_DOCUMENTO
                        ,UN_USUARIO          => UN_USUARIO ));
                        MI_ETAPA := '32A';
            MI_RTA := MI_RTA || MI_PINCONSISTENCIAS || TO_CLOB(CHR(13) || CHR(10));
        END LOOP CONTABILIZAR;

    END IF;

    MI_RTA := MI_RTA || CHR(13) || CHR(10) || 'Proceso terminado Exitosamente';


    RETURN MI_RTA;
EXCEPTION WHEN OTHERS THEN
    --Se presentó error en la subida del archivo plano, en la línea del plano número: --PLANOLINEA-- , En la etapa: --ETAPA--.
    MI_REEMPLAZOS(1).CLAVE := 'PLANOLINEA';
    MI_REEMPLAZOS(1).VALOR := MI_NUMREGISTRO;
    MI_REEMPLAZOS(2).CLAVE := 'ETAPA';
    MI_REEMPLAZOS(2).VALOR := MI_ETAPA|| ' ERROR '||SQLERRM || CHR(13) || CHR(10) || NVL(MI_RNAUX,' ');
    PCK_ERR_MSG.RAISE_WITH_MSG
              ( UN_EXC_COD => -20000
              , UN_TABLAERROR => 'ANO'
              , UN_ERROR_COD => PCK_ERRORES.ERRR_SUBIRPLANOCONTABILIZA
              , UN_REEMPLAZOS => MI_REEMPLAZOS
              );
END FC_CONTABILIZARPORPLANOESP;

FUNCTION FC_CARGARINTERFAZ_POR_XLS(
/*
    NAME              : FC_CARGARINTERFAZ_POR_XLS
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : SEBASTIAN CARDENAS
    DATE MIGRADOR     : 15/03/2021
    TIME              : 09:00 AM
    SOURCE MODULE     : I
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : 
    PARAMETERS        : 

      @NAME:    cargarInterfazporXls
      @METHOD:  GET
  */
    UN_COMPANIA               IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TIPO_CPTE              IN VARCHAR2,
    UN_NUMERO                 IN VARCHAR2,
    UN_CADENA                 IN CLOB,
    UN_COLFECHA               IN PCK_SUBTIPOS.TI_ENTERO, 
    UN_COLIDCONTABLE          IN PCK_SUBTIPOS.TI_ENTERO, 
    UN_COLVALOR               IN PCK_SUBTIPOS.TI_ENTERO,
	UN_COLVALOR_CREDITO       IN PCK_SUBTIPOS.TI_ENTERO,													
    UN_COLDETALLE             IN PCK_SUBTIPOS.TI_ENTERO,
    UN_COLTEXTO               IN PCK_SUBTIPOS.TI_ENTERO,
    UN_COLBASE                IN PCK_SUBTIPOS.TI_ENTERO,
    UN_COLCONTRATO            IN PCK_SUBTIPOS.TI_ENTERO,
    UN_COLDOCUMENTO           IN PCK_SUBTIPOS.TI_ENTERO,
    UN_COLTERCERO             IN PCK_SUBTIPOS.TI_ENTERO,
    UN_COLSUCURSAL            IN PCK_SUBTIPOS.TI_ENTERO,
    UN_COLCENTROCOSTO         IN PCK_SUBTIPOS.TI_ENTERO,
	UN_COLFUENTER             IN PCK_SUBTIPOS.TI_ENTERO,
    UN_COLREFERENCIA          IN PCK_SUBTIPOS.TI_ENTERO,																											
    UN_COLAUXILIAR            IN PCK_SUBTIPOS.TI_ENTERO,
    UN_COLTIPOCONTRATO        IN PCK_SUBTIPOS.TI_ENTERO,
    UN_RETENCIONES            IN PCK_SUBTIPOS.TI_LOGICO,
    UN_TERCERO                IN PCK_SUBTIPOS.TI_LOGICO,
    UN_AGRUPADO               IN PCK_SUBTIPOS.TI_LOGICO,
    UN_APLICAAUX             IN PCK_SUBTIPOS.TI_LOGICO,
    UN_MODULO                 IN PCK_SUBTIPOS.TI_MODULO,
    UN_FILAINI                IN PCK_SUBTIPOS.TI_ENTERO,
    UN_AUX_CONTRA             IN VARCHAR2,
	UN_CENTRO_CONTRA          IN VARCHAR2,
    UN_FUENTER_CONTRA         IN VARCHAR2,
    UN_REFERENCIA_CONTRA      IN VARCHAR2,								  									  
    UN_DESCRIPCION            IN VARCHAR2,
    UN_USUARIO                IN VARCHAR2
    
  )  RETURN CLOB 
AS 

    MI_RTA            VARCHAR2(200);
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
    MI_FILA           PCK_SUBTIPOS.TI_ENTERO_LARGO;  
    MI_ETAPA          VARCHAR2(150 CHAR);
    MI_COMPROBANTE    PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_COMPROBANTE_TERC   PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_CONSECUTIVO    PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_ANIO           PCK_SUBTIPOS.TI_ANIO;
    MI_FECHA          DATE; 
    MI_ID_CONTABLE    PCK_SUBTIPOS.TI_ENTERO_LARGO; --MPEREZ TICKET 7731490
    MI_VALOR          PCK_SUBTIPOS.TI_DOBLE := 0;
	MI_VALORCRED      PCK_SUBTIPOS.TI_DOBLE := 0;											 
    MI_DETALLE        VARCHAR2(30000 CHAR);
	MI_FUENTER        VARCHAR2(30000 CHAR);
    MI_REFERENCIA     VARCHAR2(30000 CHAR);									   									   
    MI_TEXTO          VARCHAR2(30000 CHAR);
    MI_BASE           PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_CONTRATO       PCK_SUBTIPOS.TI_ENTERO;
    MI_DOCUMENTO      VARCHAR2(30000 CHAR);
    MI_TERCERO        PCK_SUBTIPOS.TI_TERCERO;
    MI_NATURALEZA     PCK_SUBTIPOS.TI_NATURALEZACONTA;
    MI_NATURALEZA_CONTRA     PCK_SUBTIPOS.TI_NATURALEZACONTA;
    MI_SUCURSAL       PCK_SUBTIPOS.TI_SUCURSAL;
    MI_CENTRO_COSTO   PCK_SUBTIPOS.TI_CENTRO_COSTO;
    MI_AUXILIAR       PCK_SUBTIPOS.TI_AUXILIAR;
    MI_TIPO_CONTRATO  VARCHAR2(30000 CHAR);
    MI_NOMBRETERCERO  VARCHAR2(30000 CHAR);
    MI_TOTAL          PCK_SUBTIPOS.TI_DOBLE;
    MI_PAR_TOTAL      PARAMETRO.VALOR%TYPE;
    MI_STRSQL         PCK_SUBTIPOS.TI_STRSQL;
    RSDATOS           SYS_REFCURSOR;
    RS_COMPANIA       PCK_SUBTIPOS.TI_COMPANIA;
    RS_ANO            PCK_SUBTIPOS.TI_ANIO;
    RS_TIPO_CPTE      DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE_AFECT%TYPE;
    RS_CMPTE          DETALLE_COMPROBANTE_PPTAL.CMPTE_AFECTADO%TYPE;
    RS_TIPO_RET       VARCHAR2(5 CHAR);
    RS_COD_RET        VARCHAR2(5 CHAR);
    RS_VALOR          PCK_SUBTIPOS.TI_ENTERO;
    RS_VALORBASE      PCK_SUBTIPOS.TI_ENTERO;
    MI_CODIGO_CUENTA         VARCHAR2(30000 CHAR);
    MI_GRUPO                  PCK_SUBTIPOS.TI_LOGICO;
    MI_CON                    PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_MSGERROR        PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_EXISTE                VARCHAR2(2 CHAR);
    MI_CEN_COS         PCK_SUBTIPOS.TI_ENTERO;
    MI_AUX_CUEN        PCK_SUBTIPOS.TI_ENTERO;
    MI_COMP            PCK_SUBTIPOS.TI_ENTERO;
    MI_MAN_CEN_CTO     PCK_SUBTIPOS.TI_ENTERO;
    MI_MAN_AUX_TER     PCK_SUBTIPOS.TI_ENTERO;
    MI_MAN_AUX_FUE     PCK_SUBTIPOS.TI_ENTERO;
    MI_TERC            PCK_SUBTIPOS.TI_ENTERO;
    MI_PCT_APLICAR     PCK_SUBTIPOS.TI_PORCENTAJE;
    MI_VALORB          PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_VLRCRED          PCK_SUBTIPOS.TI_DOBLE;
    MI_SUMCRED          PCK_SUBTIPOS.TI_DOBLE;
    MI_COMPROB         PCK_SUBTIPOS.TI_ENTERO_LARGO;
    
BEGIN
    MI_FILA := UN_FILAINI;
    MI_CADENA :='';
    MI_COMPROBANTE := UN_NUMERO;

EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS = ''.,''';
  MI_ETAPA := '1';
  MI_CONSECUTIVO := 1;

    BEGIN
        BEGIN
            MI_CONDICION := 'COMPANIA    = ''' || UN_COMPANIA || '''
                         AND TIPO_CPTE      = ''' || UN_TIPO_CPTE   || '''';    
            MI_RTAACME := PCK_DATOS.FC_ACME(UN_TABLA     => 'TEMP_PLANA_AJUSTES',
                                          UN_ACCION    => 'E', 
                                          UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
        MI_MSGERROREXC(1).CLAVE := 'TIPO';
        MI_MSGERROREXC(1).VALOR := UN_TIPO_CPTE;
        MI_MSGERROREXC(2).CLAVE := 'ANIO';
        MI_MSGERROREXC(2).VALOR := MI_ANIO;
        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_DEL_PARTCONC,
                                    UN_REEMPLAZOS => MI_MSGERROREXC,
                                    UN_TABLAERROR => 'TEMP_PLANA_AJUSTES'
        );
    END;
    
     
     MI_TOTAL := 0;
     MI_REGISTROS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => '' || CASE WHEN SUBSTR(UN_CADENA, LENGTH(UN_CADENA),1) ='#' THEN SUBSTR(UN_CADENA,1,LENGTH(UN_CADENA)-1) ELSE UN_CADENA END || '',
                                                UN_DELIMITADOR  => '#');
    <<RECORRE_REGISTROS>>
    FOR RS IN MI_REGISTROS.FIRST..MI_REGISTROS.LAST 
    LOOP
        MI_CADENA_REG := '';
        MI_REG_CAMPOS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => '' || TO_CHAR(MI_REGISTROS(RS)) || '', 
                                                     UN_DELIMITADOR  => ';');
        BEGIN 
            BEGIN 
                BEGIN
                    MI_FECHA       := TO_DATE(MI_REG_CAMPOS(UN_COLFECHA),'DD/MM/YYYY');
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG  || '3; - Incosistencia en la Fecha ' || TO_CHAR(MI_REG_CAMPOS(UN_COLFECHA)) || ' formato DD/MM/YYYY';
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_ID_CONTABLE      := TO_NUMBER(NVL(MI_REG_CAMPOS(UN_COLIDCONTABLE),0));
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en el Id Contable ' || TO_CHAR(MI_REG_CAMPOS(UN_COLIDCONTABLE));
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_VALOR      := TO_NUMBER(NVL(TO_CHAR(MI_REG_CAMPOS(UN_COLVALOR)),0));
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en el Valor ' || MI_REG_CAMPOS(UN_COLVALOR);
                    GOTO SIGUIENTE;
                END;
                BEGIN
				    MI_VALORCRED   := TO_NUMBER(NVL(TO_CHAR(MI_REG_CAMPOS(UN_COLVALOR_CREDITO)),0));
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en el Valor ' || MI_REG_CAMPOS(UN_COLVALOR_CREDITO);
                    GOTO SIGUIENTE;
                END;
                BEGIN																					 
                    MI_DETALLE   := NVL(TO_CHAR(MI_REG_CAMPOS(UN_COLDETALLE)), UN_DESCRIPCION);                
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en el detalle ' || MI_REG_CAMPOS(UN_COLDETALLE);
                    GOTO SIGUIENTE;
                END;
               BEGIN
                    MI_TEXTO    := NVL(TO_CHAR(MI_REG_CAMPOS(UN_COLTEXTO)), UN_DESCRIPCION);               
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en el texto ' || MI_REG_CAMPOS(UN_COLTEXTO);
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_BASE    := TO_NUMBER(NVL(TO_CHAR(MI_REG_CAMPOS(UN_COLBASE)),0));
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en la base ' || MI_REG_CAMPOS(UN_COLBASE) ;
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_CONTRATO    := TO_NUMBER(NVL(MI_REG_CAMPOS(UN_COLCONTRATO),0));
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en el contrato ' || MI_REG_CAMPOS(UN_COLCONTRATO) ;
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_TIPO_CONTRATO    := NVL(MI_REG_CAMPOS(UN_COLTIPOCONTRATO),'');
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en el tipo contrato ' || MI_REG_CAMPOS(UN_COLTIPOCONTRATO) ;
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_DOCUMENTO    := NVL(MI_REG_CAMPOS(UN_COLDOCUMENTO),' ');
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en el documento ' || MI_REG_CAMPOS(UN_COLDOCUMENTO) ;
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_TERCERO    := NVL(MI_REG_CAMPOS(UN_COLTERCERO),'999999999999999999' );
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en el tercero ' || MI_REG_CAMPOS(UN_COLTERCERO) ;
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_SUCURSAL    := NVL(MI_REG_CAMPOS(UN_COLSUCURSAL),' ');
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en la sucursal ' || MI_REG_CAMPOS(UN_COLSUCURSAL) ;
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_CENTRO_COSTO    := NVL(MI_REG_CAMPOS(UN_COLCENTROCOSTO),'99999999999999999999');
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en el centro de costo ' || MI_REG_CAMPOS(UN_COLCENTROCOSTO) ;
                    GOTO SIGUIENTE;
                END;
                BEGIN
					MI_FUENTER   := NVL(TO_CHAR(MI_REG_CAMPOS(UN_COLFUENTER)), '99999999999999999999');                
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en la fuente recursos ' || MI_REG_CAMPOS(UN_COLFUENTER);
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_REFERENCIA   := NVL(TO_CHAR(MI_REG_CAMPOS(UN_COLREFERENCIA)), '99999999999999999999');                
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en la referencia ' || MI_REG_CAMPOS(UN_COLREFERENCIA);
                    GOTO SIGUIENTE;
                END;
                BEGIN																									   
                    MI_AUXILIAR    := NVL(MI_REG_CAMPOS(UN_COLAUXILIAR),'99999999999999999999' );
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en el auxiliar ' || MI_REG_CAMPOS(UN_COLAUXILIAR) ;
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
     <<SIGUIENTE>>  
           
     MI_ANIO := PCK_SYSMAN_UTL.FC_ANIO(MI_FECHA);
                 
     IF UN_RETENCIONES NOT IN (0) THEN
     BEGIN
       SELECT DISTINCT 'X' 
        INTO   MI_EXISTE
        FROM   TEMP_COMPROBANTE_CNTRETENCION
        WHERE  COMPANIA = UN_COMPANIA
          AND  ANO      = MI_ANIO
          AND  TIPO     = UN_TIPO_CPTE
          AND  NUMERO   = UN_NUMERO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_EXISTE := ' ';
    END;
    
     IF NVL(MI_EXISTE,' ') = ' ' THEN
        --Revisa si existe informacion en la tabla
        MI_CADENA := TO_CLOB('Para este proceso primero se debe codificar retenciones.' || CHR(13) || CHR(10));
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;           
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                MI_MSGERROR(1).CLAVE := 'MENSAJE';
                MI_MSGERROR(1).VALOR := MI_CADENA;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                                   UN_EXC_COD =>SQLCODE
                                  ,UN_ERROR_COD=>PCK_ERRORES.ERRR_PERSONALIZADO
                                  ,UN_REEMPLAZOS  => MI_MSGERROR);
        END;
     END IF;
    END IF; 
             
  BEGIN
    SELECT 
      COUNT(COMPANIA)
    INTO MI_EXISTE
    FROM COMPROBANTE_CNT
    WHERE COMPANIA = UN_COMPANIA
      AND ANO      = MI_ANIO
      AND TIPO     = UN_TIPO_CPTE
      AND NUMERO   = MI_COMPROBANTE;
  EXCEPTION WHEN NO_DATA_FOUND THEN
    MI_EXISTE:=0;
  END;
  --'Si está creado avisa que existe y termina
  IF MI_EXISTE > 0 THEN
      BEGIN
      MI_CADENA := 'El comprobante de tipo '|| UN_TIPO_CPTE ||', número '|| MI_COMPROBANTE ||' para el año '|| MI_ANIO ||', ya existe';
               RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;           
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                MI_MSGERROR(1).CLAVE := 'MENSAJE';
                MI_MSGERROR(1).VALOR := MI_CADENA;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                                   UN_EXC_COD =>SQLCODE
                                  ,UN_ERROR_COD=>PCK_ERRORES.ERRR_PERSONALIZADO
                                  ,UN_REEMPLAZOS  => MI_MSGERROR);            
      END;
      END IF;
      
      BEGIN
      SELECT COUNT(0)INTO MI_COMP
      FROM TEMP_PLANA_AJUSTES 
      WHERE COMPANIA = UN_COMPANIA AND ANO = MI_ANIO
        AND TIPO_CPTE = UN_TIPO_CPTE AND TERCERO = MI_TERCERO;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_COMP:=0;
      END;

      BEGIN
      SELECT COMPROBANTE INTO MI_COMPROBANTE_TERC
      FROM TEMP_PLANA_AJUSTES 
      WHERE COMPANIA = UN_COMPANIA
        AND ANO = MI_ANIO AND TIPO_CPTE = UN_TIPO_CPTE
        AND TERCERO = MI_TERCERO
        AND ROWNUM <= 1; -- MPEREZ TICKET 7731490
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_COMPROBANTE_TERC:= MI_COMPROBANTE;
      END;  
      
      SELECT MAX(COMPROBANTE) INTO MI_COMPROB FROM TEMP_PLANA_AJUSTES; 
      
      IF MI_COMP > 0 THEN
        MI_COMPROBANTE := MI_COMPROBANTE_TERC;
      ELSE
        MI_COMPROBANTE := NVL((MI_COMPROB + 1), UN_NUMERO);
      END IF;
    
      BEGIN
      SELECT MAN_CEN_CTO,MAN_AUX_TER,MAN_AUX_FUE 
      INTO MI_MAN_CEN_CTO,MI_MAN_AUX_TER,MI_MAN_AUX_FUE
      FROM PLAN_CONTABLE 
      WHERE COMPANIA = UN_COMPANIA AND ANO = MI_ANIO AND CODIGO = MI_ID_CONTABLE;
      EXCEPTION WHEN NO_DATA_FOUND THEN
       MI_MAN_CEN_CTO:=0;
       MI_MAN_AUX_TER:=0;
       MI_MAN_AUX_FUE:=0;
      END;
        
      IF MI_MAN_CEN_CTO NOT IN(0) THEN 
        MI_CENTRO_COSTO := MI_CENTRO_COSTO;
      ELSE 
        MI_CENTRO_COSTO := '99999999999999999999';
      END IF;
      
      IF MI_MAN_AUX_TER NOT IN(0) THEN 
        MI_AUXILIAR := MI_AUXILIAR;
      ELSE 
        MI_AUXILIAR := '99999999999999999999';
      END IF;
      
      IF MI_MAN_AUX_FUE NOT IN(0) THEN 
        MI_FUENTER := MI_FUENTER;
      ELSE 
        MI_FUENTER := '99999999999999999999';
      END IF;
      
      BEGIN
      SELECT COUNT(*) INTO MI_TERC 
      FROM TEMP_COMPROBANTE_CNTRETENCION
        WHERE COMPANIA = UN_COMPANIA AND ANO = MI_ANIO
        AND TIPO = UN_TIPO_CPTE AND NUMERO = MI_COMPROBANTE;
      EXCEPTION WHEN NO_DATA_FOUND THEN
       MI_TERC:=0;
      END;
      
           MI_NATURALEZA := PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_ID_CONTABLE);
             BEGIN
            BEGIN
                MI_CAMPOS    := 'COMPANIA,
                                ANO,
                                TIPO_CPTE,
                                COMPROBANTE,
                                CONSECUTIVO,
                                CUENTA,
                                FECHA,
                                NATURALEZA,
                                DESCRIPCION,
                                CENTRO_COSTO,
                                TERCERO,
                                SUCURSAL,
                                AUXILIAR,
                                VALOR_DEBITO,
                                VALOR_CREDITO,
                                BASE_GRAVABLE,
                                NRO_DOCUMENTO,
                                TEXTOD,
                                NUMEROCONTRATO,
                                TIPOCONTRATO,
								FUENTE_RECURSOS,
                                REFERENCIA';									
                                
                MI_VALORES   := '  '''|| UN_COMPANIA ||'''
                                , '|| MI_ANIO ||'
                                , '''|| UN_TIPO_CPTE ||'''
                                , '|| MI_COMPROBANTE ||'
                                , '|| MI_CONSECUTIVO ||'
                                , '''|| MI_ID_CONTABLE ||'''
                                , TO_DATE(''' || TO_CHAR(MI_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                                , '''|| MI_NATURALEZA ||'''
                                ,'''||  MI_DETALLE || '''
                                ,'''|| MI_CENTRO_COSTO ||'''
                                ,'''|| MI_TERCERO ||'''
                                ,'''|| MI_SUCURSAL ||'''
                                ,'''|| MI_AUXILIAR ||'''
                                ,'|| MI_VALOR ||'
                                ,'|| MI_VALORCRED ||'
                                ,'|| MI_BASE ||'
                                ,'''|| MI_DOCUMENTO ||'''
                                ,'''|| MI_TEXTO ||'''
                                ,'|| MI_CONTRATO ||'
                                ,'''|| MI_TIPO_CONTRATO ||'''
								,'''|| MI_FUENTER ||'''
                                ,'''|| MI_REFERENCIA ||'''';

                MI_RTAACME       := PCK_DATOS.FC_ACME (UN_TABLA     => 'TEMP_PLANA_AJUSTES',
                                                       UN_ACCION    => 'I',
                                                       UN_CAMPOS    => MI_CAMPOS,
                                               UN_VALORES   => MI_VALORES);  
         
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;   
            
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            MI_MSGERROREXC(1).CLAVE := 'FECHA';
            MI_MSGERROREXC(1).VALOR := TO_CHAR(MI_FECHA,'DD/MM/YYYY');
            MI_MSGERROREXC(2).CLAVE := 'ANIO';
            MI_MSGERROREXC(2).VALOR := MI_ANIO;
            MI_MSGERROREXC(3).CLAVE := 'COMPROBANTE';
            MI_MSGERROREXC(3).VALOR := MI_COMPROBANTE;            
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_ACTDETCOMCONC,
                                    UN_REEMPLAZOS => MI_MSGERROREXC,
                                    UN_TABLAERROR => 'TEMP_PLANA_AJUSTES'
            );                      
        END;
        
        BEGIN
            MI_ETAPA := 'Etapa 11';
            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
        END;
        
		
         MI_ETAPA := 'Etapa 12';
                IF UN_RETENCIONES NOT IN (0) THEN
                --creo el registro por cada comprobante. tomo como base el primero.
               IF UN_NUMERO <> MI_COMPROBANTE AND MI_TERC = 0 THEN
                        
                          MI_CAMPOS := 'COMPANIA, 
                                        ANO, 
                                        TIPO, 
                                        NUMERO, 
                                        TIPORETENCION, 
                                        CODIGORETENCION, 
                                        VALOR, 
                                        VALORBASE, 
                                        PORCIVA';
                                        
                       MI_VALORES := 'SELECT COMPANIA, 
                                        ANO, 
                                        TIPO, 
                                        '||MI_COMPROBANTE||', 
                                        TIPORETENCION, 
                                        CODIGORETENCION, 
                                        VALOR, 
                                        VALORBASE, 
                                        PORCIVA
                                        FROM TEMP_COMPROBANTE_CNTRETENCION
                                        WHERE COMPANIA =''' || UN_COMPANIA ||'''
                                        AND ANO = '|| MI_ANIO || '
                                        AND TIPO = ''' || UN_TIPO_CPTE ||'''
                                        AND NUMERO = '|| UN_NUMERO||' ';
                                        
                                        
              MI_RTAACME := PCK_DATOS.FC_ACME(UN_TABLA     => 'TEMP_COMPROBANTE_CNTRETENCION', 
                            UN_ACCION    => 'IS', 
                            UN_CAMPOS    => MI_CAMPOS,
                            UN_VALORES   => MI_VALORES); 
                        MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
                                 MI_ETAPA := 'Etapa 13';
                        END IF;
                        
                MI_ETAPA := 'Etapa 13';
                

																	
													 
										 
                END IF;
        
        MI_FILA := MI_FILA + 1;
        
      END LOOP RECORRE_REGISTROS; 
        
      MI_ETAPA := 'Etapa 15';
    
    IF UN_RETENCIONES NOT IN (0) THEN
        --Se actualiza el valor y valor base de las retenciones
        MI_STRSQL  := 'SELECT COMPANIA,ANO,TIPO,NUMERO,
                        TIPORETENCION,CODIGORETENCION,
                        VALOR,VALORBASE 
                        FROM TEMP_COMPROBANTE_CNTRETENCION';
                
          OPEN RSDATOS FOR MI_STRSQL;
          LOOP
            FETCH RSDATOS INTO RS_COMPANIA,RS_ANO,RS_TIPO_CPTE,RS_CMPTE,RS_TIPO_RET,RS_COD_RET,
                               RS_VALOR,RS_VALORBASE;
            EXIT WHEN RSDATOS%NOTFOUND;
            
            SELECT BASE_GRAVABLE INTO MI_BASE FROM TEMP_PLANA_AJUSTES
            WHERE COMPANIA = UN_COMPANIA
            AND ANO = MI_ANIO
            AND TIPO_CPTE = UN_TIPO_CPTE
            AND COMPROBANTE = RS_CMPTE
            AND VALOR_CREDITO NOT IN(0);
            
            SELECT PCT_APLICAR INTO MI_PCT_APLICAR FROM RETENCIONES
            INNER JOIN TEMP_COMPROBANTE_CNTRETENCION
            ON RETENCIONES.COMPANIA = TEMP_COMPROBANTE_CNTRETENCION.COMPANIA
            AND RETENCIONES.ANO  = TEMP_COMPROBANTE_CNTRETENCION.ANO
            AND RETENCIONES.CODIGO = TEMP_COMPROBANTE_CNTRETENCION.CODIGORETENCION
            AND RETENCIONES.TIPO = TEMP_COMPROBANTE_CNTRETENCION.TIPORETENCION
            WHERE TEMP_COMPROBANTE_CNTRETENCION.COMPANIA = UN_COMPANIA
            AND TEMP_COMPROBANTE_CNTRETENCION.ANO = MI_ANIO
            AND TEMP_COMPROBANTE_CNTRETENCION.TIPO = UN_TIPO_CPTE
            AND TEMP_COMPROBANTE_CNTRETENCION.TIPORETENCION = RS_TIPO_RET
            AND TEMP_COMPROBANTE_CNTRETENCION.CODIGORETENCION = RS_COD_RET
            AND TEMP_COMPROBANTE_CNTRETENCION.NUMERO = RS_CMPTE;
            
            MI_PCT_APLICAR := MI_PCT_APLICAR/100.000000;
            MI_VALORB := PCK_SYSMAN_UTL.FC_ROUND(MI_BASE * MI_PCT_APLICAR,0);
            
            BEGIN
                    MI_CAMPOS := 'VALORBASE      = '|| TO_NUMBER(NVL(TO_CHAR(MI_BASE),0)) ||',
                                  VALOR          = '|| MI_VALORB || ',
                                  MODIFIED_BY    = '''|| UN_USUARIO ||''',
                                  DATE_MODIFIED  = SYSDATE';
                         
                    MI_CONDICION := 'COMPANIA   =''' ||UN_COMPANIA|| '''
                                    AND ANO     =''' ||MI_ANIO|| ''' 
                                    AND TIPO    =''' ||UN_TIPO_CPTE|| '''
                                    AND NUMERO  =''' ||RS_CMPTE|| '''
                                    AND TIPORETENCION =''' ||RS_TIPO_RET|| '''
                                    AND CODIGORETENCION =''' ||RS_COD_RET|| ''' ';
                    BEGIN
                    MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'TEMP_COMPROBANTE_CNTRETENCION', 
                                                UN_ACCION    => 'M', 
                                                UN_CAMPOS    => MI_CAMPOS, 
                                                UN_CONDICION => MI_CONDICION );
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                    END;    
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    =>  SQLCODE,
                                                    UN_ERROR_COD  => PCK_ERRORES.ERRR_BORRARDETCOMPRINTERFAZ,
                                                    UN_TABLAERROR => 'TEMP_COMPROBANTE_CNTRETENCION');
                    END;
          END LOOP;
          --Fin Actualizacion
      END IF;

      MI_CONSECUTIVO := 1; 
      
      FOR RS IN(SELECT TEMP_PLANA_AJUSTES.CONSECUTIVO, 
                       TEMP_PLANA_AJUSTES.TIPO_CPTE,
                       TERCERO.NIT,
                       CCO.CODIGO  CENTROCODIGO,
                       AX.CODIGO   AUXILIARCODIGO,
                       TEMP_PLANA_AJUSTES.TERCERO,
                       TEMP_PLANA_AJUSTES.SUCURSAL, 
                       TEMP_PLANA_AJUSTES.CENTRO_COSTO, 
                       TEMP_PLANA_AJUSTES.AUXILIAR
                            FROM TEMP_PLANA_AJUSTES 
                            LEFT JOIN TERCERO 
                             ON TEMP_PLANA_AJUSTES.SUCURSAL = TERCERO.SUCURSAL 
                            AND TEMP_PLANA_AJUSTES.TERCERO  = TERCERO.NIT 
                            AND TEMP_PLANA_AJUSTES.COMPANIA = TERCERO.COMPANIA
                            LEFT JOIN CENTRO_COSTO CCO
                             ON  TEMP_PLANA_AJUSTES.COMPANIA     = CCO.COMPANIA
                            AND TEMP_PLANA_AJUSTES.ANO          = CCO.ANO
                            AND TEMP_PLANA_AJUSTES.CENTRO_COSTO = CCO.CODIGO
                            LEFT JOIN AUXILIAR AX
                             ON  TEMP_PLANA_AJUSTES.COMPANIA = AX.COMPANIA
                            AND TEMP_PLANA_AJUSTES.ANO      = AX.ANO
                            AND TEMP_PLANA_AJUSTES.AUXILIAR = AX.CODIGO
                            WHERE TEMP_PLANA_AJUSTES.COMPANIA  = UN_COMPANIA
                              AND TEMP_PLANA_AJUSTES.TIPO_CPTE = UN_TIPO_CPTE
                              AND TERCERO.NIT IS NULL
                                OR CCO.CODIGO IS NULL
                                OR AX.CODIGO IS NULL
                            ORDER BY TEMP_PLANA_AJUSTES.CONSECUTIVO)
  LOOP
  
    MI_ETAPA := 'Terceros que no existen en la tabla';
    IF RS.NIT IS NULL THEN
                MI_CADENA := MI_CADENA || TO_CLOB('El tercero '|| RS.TERCERO || ' sucursal '|| RS.SUCURSAL ||' NO está creado en el sistema.' || CHR(13) || CHR(10));
    END IF;
     IF RS.CENTROCODIGO IS NULL THEN
                MI_CADENA := MI_CADENA || TO_CLOB('El centro de costo '|| RS.CENTRO_COSTO || ' NO está creado en el sistema.' || CHR(13) || CHR(10));
     END IF;
     IF RS.AUXILIARCODIGO IS NULL THEN
                MI_CADENA := MI_CADENA || TO_CLOB('El auxiliar '|| RS.AUXILIAR || ' NO está creado en el sistema.' || CHR(13) || CHR(10));
     END IF;
            
             IF MI_CADENA <> ' ' THEN
            MI_CADENA := MI_CADENA || TO_CLOB('Proceso de contabilizar no realizado' || CHR(13) || CHR(10));
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;           
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                MI_MSGERROR(1).CLAVE := 'MENSAJE';
                MI_MSGERROR(1).VALOR := MI_CADENA;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                                   UN_EXC_COD =>SQLCODE
                                  ,UN_ERROR_COD=>PCK_ERRORES.ERRR_PERSONALIZADO
                                  ,UN_REEMPLAZOS  => MI_MSGERROR);
            END;
        END IF;        

  END LOOP;
    
    MI_COMPROBANTE := UN_NUMERO;
    MI_GRUPO := 0;
    
    IF UN_AGRUPADO NOT IN (0) THEN
      MI_TOTAL := 0;
    
        FOR RS IN(SELECT DISTINCT FECHA 
                                FROM TEMP_PLANA_AJUSTES 
                                WHERE TIPO_CPTE = UN_TIPO_CPTE 
                                GROUP BY FECHA ORDER BY FECHA )
      LOOP
      
      MI_ETAPA := 'Etapa 21';
      
       BEGIN
      MI_CAMPOS := 'VALOR         = '|| MI_COMPROBANTE || ',
                    MODIFIED_BY   = '''|| UN_USUARIO ||''',
                    DATE_MODIFIED = SYSDATE
                    ';
      MI_CONDICION := 'COMPANIA   =''' ||UN_COMPANIA||'''
                        AND FECHA = TO_DATE('''|| RS.FECHA ||''' , ''DD/MM/YYY'')';

      MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'TEMP_PLANA_AJUSTES', 
                                  UN_ACCION    => 'M', 
                                  UN_CAMPOS    => MI_CAMPOS, 
                                  UN_CONDICION => MI_CONDICION );
                                
          IF MI_RTA = 0 THEN
            MI_CADENA := MI_CADENA || TO_CLOB('Error al actualizar el Comprobante ' ||MI_COMPROBANTE || CHR(13) || CHR(10));
          END IF;
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;           
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                MI_MSGERROR(1).CLAVE := 'MENSAJE';
                MI_MSGERROR(1).VALOR := MI_CADENA;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                                   UN_EXC_COD =>SQLCODE
                                  ,UN_ERROR_COD=>PCK_ERRORES.ERRR_PERSONALIZADO
                                  ,UN_REEMPLAZOS  => MI_MSGERROR);
            
    END;  
      
      MI_COMPROBANTE := MI_COMPROBANTE + 1;
      
      MI_TOTAL := MI_TOTAL + 1;
    
      END LOOP;
     END IF;
     
      MI_ETAPA := 'Etapa 22';
      
        IF UN_TERCERO NOT IN (0) OR UN_AGRUPADO NOT IN (0) Then
            MI_GRUPO := '-1';
        ELSE
            MI_GRUPO := 0;
        END IF;
       
       MI_CON := 0;
               FOR RS IN(SELECT DISTINCT
                                COMPANIA,
                                TIPO_CPTE,
                                COMPROBANTE,
                                ANO,
                                FECHA,
                                TERCERO,
                                DESCRIPCION,
                                NRO_DOCUMENTO,
                                NUMEROCONTRATO,
                                TIPOCONTRATO,
                                TEXTOD AS TEXTO
                            FROM TEMP_PLANA_AJUSTES
                            WHERE TIPO_CPTE = UN_TIPO_CPTE
                              AND TEXTOD IS NOT NULL
                            GROUP BY COMPANIA,
                                     TIPO_CPTE,
                                     COMPROBANTE,
                                     ANO,
                                     FECHA,
                                     TERCERO,
                                     DESCRIPCION,
                                     NRO_DOCUMENTO,
                                     NUMEROCONTRATO,
                                     TIPOCONTRATO,
                                     TEXTOD)
      LOOP
      
      MI_ETAPA := 'Etapa 23';
      
      BEGIN
      SELECT COUNT(DISTINCT CENTRO_COSTO) INTO MI_CEN_COS FROM TEMP_PLANA_AJUSTES
      WHERE COMPANIA = UN_COMPANIA
        AND ANO = MI_ANIO
        AND TIPO_CPTE = RS.TIPO_CPTE
        AND COMPROBANTE = RS.COMPROBANTE;
      
      SELECT COUNT(DISTINCT AUXILIAR) INTO MI_AUX_CUEN FROM TEMP_PLANA_AJUSTES
      WHERE COMPANIA = UN_COMPANIA
        AND ANO = MI_ANIO
        AND TIPO_CPTE = RS.TIPO_CPTE
        AND COMPROBANTE = RS.COMPROBANTE;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_CEN_COS :=0;
        MI_AUX_CUEN:=0;
      END;
      
      IF MI_CEN_COS > 1 THEN 
        MI_CENTRO_COSTO := '99999999999999999999';
      ELSE 
        MI_CENTRO_COSTO := NVL(MI_REG_CAMPOS(UN_COLCENTROCOSTO), '99999999999999999999');
      END IF;
      
      IF MI_AUX_CUEN > 1 THEN 
        MI_AUXILIAR := '99999999999999999999';
      ELSE 
        MI_AUXILIAR := NVL(MI_REG_CAMPOS(UN_COLAUXILIAR), '99999999999999999999');
      END IF;
      
        IF MI_CONSECUTIVO <> 0 THEN
            MI_RTA:= PCK_CONTABILIZAR.FC_CONTABILIZAR(
                               UN_COMPANIA         => RS.COMPANIA
                              ,UN_TIPOCOMPROBANTE  => RS.TIPO_CPTE
                              ,UN_NUMERO           => RS.COMPROBANTE
                              ,UN_ANO              => TO_NUMBER(RS.ANO)
                              ,UN_FECHA            => RS.FECHA
                              ,UN_TERCERO          => RS.TERCERO
                              ,UN_SUCURSAL         => MI_SUCURSAL
                              ,UN_CENTRO_COSTO     => MI_CENTRO_COSTO
                              ,UN_AUXILIAR         => MI_AUXILIAR
                              ,UN_DESCRIPCION      => RS.DESCRIPCION
                              ,UN_SIMPLE           => CASE WHEN UPPER('FALSE') = 'TRUE' THEN -1 ELSE 0 END
                              ,UN_INDIMPRESION     => 0
                              ,UN_INTOMITIRPPTAL   => CASE WHEN UPPER('TRUE') = 'TRUE' THEN -1 ELSE 0 END 
                              ,UN_CONCILIAR        => CASE WHEN UPPER('FALSE') = 'TRUE' THEN -1 ELSE 0 END  
                              ,UN_PLANO            => -1
                              ,UN_NONETEA          => CASE WHEN UPPER('TRUE') = 'TRUE' THEN -1 ELSE 0 END   
                              ,UN_CONTRATISTA      => CASE WHEN UPPER('FALSE') = 'TRUE' THEN -1 ELSE 0 END    
                              ,UN_TEXTO            => RS.TEXTO
                              ,UN_CONTRATO         => TO_NUMBER(RS.NUMEROCONTRATO)
                              ,UN_TIPOCONTRATO     => RS.TIPOCONTRATO
                              ,UN_NRO_DOCUMENTO    => RS.NRO_DOCUMENTO
                              ,UN_ALMDEP           => CASE WHEN UPPER('FALSE') = 'TRUE' THEN -1 ELSE 0 END
                              ,UN_TERCE            => CASE WHEN UPPER('FALSE') = 'TRUE' THEN -1 ELSE 0 END 
                              ,UN_RESAUXGEN        => CASE WHEN UPPER('FALSE') = 'TRUE' THEN -1 ELSE 0 END
                              ,UN_USUARIO          => UN_USUARIO
                            );
        ELSE
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;           
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                MI_MSGERROR(1).CLAVE := 'COMPANIA';
                MI_MSGERROR(1).VALOR := RS.COMPANIA;
                MI_MSGERROR(2).CLAVE := 'TIPO';
                MI_MSGERROR(2).VALOR := RS.TIPO_CPTE;
                MI_MSGERROR(3).CLAVE := 'NUMERO';
                MI_MSGERROR(3).VALOR := RS.COMPROBANTE;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                                   UN_EXC_COD =>SQLCODE
                                  ,UN_ERROR_COD=>PCK_ERRORES.ERR_CERODETALLESJSON
                                  ,UN_REEMPLAZOS  => MI_MSGERROR);
            END;
        END IF;

      MI_CON := MI_CON + 1;
    
      END LOOP;     
      
          MI_ETAPA := 'Etapa 24';
    IF UN_RETENCIONES NOT IN (0) THEN 
        MI_CAMPOS := 'COMPANIA, 
                      ANO, 
                      TIPO, 
                      NUMERO, 
                      TIPORETENCION, 
                      CODIGORETENCION, 
                      VALOR, 
                      VALORBASE, 
                      PORCIVA';
                                        
        MI_VALORES := 'SELECT COMPANIA, 
                              ANO, 
                              TIPO, 
                              NUMERO, 
                              TIPORETENCION, 
                              CODIGORETENCION, 
                              VALOR, 
                              VALORBASE, 
                              PORCIVA
                       FROM TEMP_COMPROBANTE_CNTRETENCION';
                                                               
										
        MI_RTAACME := PCK_DATOS.FC_ACME(UN_TABLA     => 'COMPROBANTE_CNTRETENCION', 
                                        UN_ACCION    => 'IS', 
                                        UN_CAMPOS    => MI_CAMPOS,
                                        UN_VALORES   => MI_VALORES); 
      
        MI_COMPROBANTE := UN_NUMERO;
        MI_CONSECUTIVO := PCK_CONTABILIDAD2.FC_CONSECUTIVOMENSAJES();
        MI_REGISTROS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => '' || CASE WHEN SUBSTR(UN_CADENA, LENGTH(UN_CADENA),1) ='#' THEN SUBSTR(UN_CADENA,1,LENGTH(UN_CADENA)-1) ELSE UN_CADENA END || '',
                                                    UN_DELIMITADOR  => '#');
                                                    
    <<RECORRE_REGISTROS>>
    FOR MI_RS IN MI_REGISTROS.FIRST..MI_REGISTROS.LAST 
    LOOP
            MI_CADENA_REG := '';
            MI_REG_CAMPOS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => '' || TO_CHAR(MI_REGISTROS(MI_RS)) || '', 
                                                     UN_DELIMITADOR  => ';');
            BEGIN 
            BEGIN 
                BEGIN
                    MI_FECHA       := TO_DATE(MI_REG_CAMPOS(UN_COLFECHA),'DD/MM/YYYY');
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG  || '3; - Incosistencia en la Fecha ' || TO_CHAR(MI_REG_CAMPOS(UN_COLFECHA)) || ' formato DD/MM/YYYY';
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_ID_CONTABLE      := TO_NUMBER(NVL(MI_REG_CAMPOS(UN_COLIDCONTABLE),0));
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en el Id Contable ' || TO_CHAR(MI_REG_CAMPOS(UN_COLIDCONTABLE));
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_VALOR      := TO_NUMBER(NVL(TO_CHAR(MI_REG_CAMPOS(UN_COLVALOR)),0));
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en el Valor ' || MI_REG_CAMPOS(UN_COLVALOR);
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_VALORCRED  := TO_NUMBER(NVL(TO_CHAR(MI_REG_CAMPOS(UN_COLVALOR_CREDITO)),0));
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en el Valor ' || MI_REG_CAMPOS(UN_COLVALOR_CREDITO);
                    GOTO SIGUIENTE;
                END;
				BEGIN				
                    MI_DETALLE   := NVL(TO_CHAR(MI_REG_CAMPOS(UN_COLDETALLE)), UN_DESCRIPCION);                
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en el detalle ' || MI_REG_CAMPOS(UN_COLDETALLE);
                    GOTO SIGUIENTE;
                END;
               BEGIN
                    MI_TEXTO    := NVL(TO_CHAR(MI_REG_CAMPOS(UN_COLTEXTO)), UN_DESCRIPCION);               
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en el texto ' || MI_REG_CAMPOS(UN_COLTEXTO);
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_BASE    := TO_NUMBER(NVL(TO_CHAR(MI_REG_CAMPOS(UN_COLBASE)),0));
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en la base ' || MI_REG_CAMPOS(UN_COLBASE) ;
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_CONTRATO    := TO_NUMBER(NVL(MI_REG_CAMPOS(UN_COLCONTRATO),0));
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en el contrato ' || MI_REG_CAMPOS(UN_COLCONTRATO) ;
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_TIPO_CONTRATO    := NVL(MI_REG_CAMPOS(UN_COLTIPOCONTRATO),'');
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en el tipo contrato ' || MI_REG_CAMPOS(UN_COLTIPOCONTRATO) ;
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_DOCUMENTO    := NVL(MI_REG_CAMPOS(UN_COLDOCUMENTO),' ');
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en el documento ' || MI_REG_CAMPOS(UN_COLDOCUMENTO) ;
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_TERCERO    := NVL(MI_REG_CAMPOS(UN_COLTERCERO),'999999999999999999' );
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en el tercero ' || MI_REG_CAMPOS(UN_COLTERCERO) ;
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_SUCURSAL    := NVL(MI_REG_CAMPOS(UN_COLSUCURSAL),' ');
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en la sucursal ' || MI_REG_CAMPOS(UN_COLSUCURSAL) ;
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_CENTRO_COSTO    := NVL(MI_REG_CAMPOS(UN_COLCENTROCOSTO),'99999999999999999999' );
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en el centro de costo ' || MI_REG_CAMPOS(UN_COLCENTROCOSTO) ;
                    GOTO SIGUIENTE;
                END;
                BEGIN
					MI_FUENTER  := NVL(TO_CHAR(MI_REG_CAMPOS(UN_COLFUENTER)), '99999999999999999999');                
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en la fuente recursos ' || MI_REG_CAMPOS(UN_COLFUENTER);
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_REFERENCIA   := NVL(TO_CHAR(MI_REG_CAMPOS(UN_COLREFERENCIA)), '99999999999999999999');                
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en la referencia ' || MI_REG_CAMPOS(UN_COLREFERENCIA);
                    GOTO SIGUIENTE;
                END;
				BEGIN
                    MI_AUXILIAR    := NVL(MI_REG_CAMPOS(UN_COLAUXILIAR),'99999999999999999999' );
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Incosistencia en el auxiliar ' || MI_REG_CAMPOS(UN_COLAUXILIAR) ;
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
        <<SIGUIENTE>>  
   
    /*   MI_RTA := PCK_CONTABILIDAD2.FC_CALCULARLEY1450(
        UN_COMPANIA         => UN_COMPANIA, 
        UN_MODULO           => 1,
        UN_CONSECMENSAJES   => MI_CONSECUTIVO,
        UN_ANO              => MI_ANIO, 
        UN_FECHA            => MI_FECHA,
        UN_TIPO             => UN_TIPO_CPTE, 
        UN_NUMERO           => MI_COMPROBANTE, 
        UN_TERCERO          => MI_TERCERO, 
        UN_SUCURSAL         => MI_SUCURSAL, 
      	UN_NOMBRETERCERO    => MI_NOMBRETERCERO, 
        UN_VALORBASE        => MI_BASE, 
        UN_VALORBASEIVA     => MI_BASE, 
        UN_DESCRIPCION      => MI_DETALLE, 
        UN_STRCENTRO_COSTO  => MI_CENTRO_COSTO, 
        UN_NRO_DOCUMENTO    => MI_DOCUMENTO,
        UN_USUARIO            => UN_USUARIO);*/
        
      BEGIN
      SELECT COUNT(0)INTO MI_COMP
      FROM TEMP_PLANA_AJUSTES 
      WHERE COMPANIA = UN_COMPANIA AND ANO = MI_ANIO
        AND TIPO_CPTE = UN_TIPO_CPTE AND TERCERO = MI_TERCERO;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_COMP:=0;
      END;
      
      BEGIN
      SELECT DISTINCT COMPROBANTE INTO MI_COMPROBANTE_TERC
      FROM TEMP_PLANA_AJUSTES 
      WHERE COMPANIA = UN_COMPANIA
        AND ANO = MI_ANIO AND TIPO_CPTE = UN_TIPO_CPTE
        AND TERCERO = MI_TERCERO;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_COMPROBANTE_TERC:= MI_COMPROBANTE;
      END;
        
      IF MI_COMP > 0 THEN
        MI_COMPROBANTE := MI_COMPROBANTE_TERC;
      ELSE
        MI_COMPROBANTE := MI_COMPROBANTE;
      END IF;
        
      MI_RTA :=   PCK_CONTABILIDAD2.FC_CALCULORETENCIONES(
      UN_COMPANIA       => UN_COMPANIA,
      UN_MODULO         => UN_MODULO,
      UN_CONSECMENSAJES => MI_CONSECUTIVO,
      UN_ANO            => MI_ANIO,
      UN_FECHA          => MI_FECHA,
      UN_TIPO           => UN_TIPO_CPTE,
      UN_NUMERO         => MI_COMPROBANTE,
      UN_AUXILIAR       => UN_AUX_CONTRA,
      UN_TERCERO        => MI_TERCERO,
      UN_SUCURSAL       => MI_SUCURSAL,
      UN_NOMBRETERCERO  => MI_NOMBRETERCERO,
      UN_VALORBASE      => MI_BASE,
      UN_VALORBASEIVA   => 0,
      UN_DESCRIPCION    => MI_DETALLE,
      UN_CENTROCOSTO    => UN_CENTRO_CONTRA,
      UN_NRODOCUMENTO   => MI_DOCUMENTO,
      UN_USUARIO        => UN_USUARIO,
      UN_FUENTER        => UN_FUENTER_CONTRA,
      UN_REFERENCIA     => UN_REFERENCIA_CONTRA);
        
      SELECT VALOR_CREDITO INTO MI_VLRCRED
      FROM DETALLE_COMPROBANTE_CNT 
      WHERE COMPANIA = UN_COMPANIA AND ANO = MI_ANIO
      AND TIPO_CPTE = UN_TIPO_CPTE AND COMPROBANTE = MI_COMPROBANTE
      AND VALOR_CREDITO NOT IN(0) AND EJECUCION_CREDITO IN(0);

      SELECT SUM(VALOR) INTO MI_SUMCRED FROM COMPROBANTE_CNTRETENCION
      WHERE COMPANIA = UN_COMPANIA AND ANO = MI_ANIO
      AND TIPO = UN_TIPO_CPTE AND NUMERO = MI_COMPROBANTE;
       
      IF(MI_VALORCRED NOT IN(0) AND MI_VALORCRED = MI_VLRCRED) THEN
          BEGIN 
          
          MI_CAMPOS := 'VALOR_CREDITO = '|| PCK_SYSMAN_UTL.FC_ROUND((MI_VLRCRED-MI_SUMCRED),2) || ', 
                        MODIFIED_BY   = '''|| UN_USUARIO ||''',
                        DATE_MODIFIED = SYSDATE';
                        
          MI_CONDICION := 'DETALLE_COMPROBANTE_CNT.COMPANIA       = ''' ||UN_COMPANIA||'''
                          AND DETALLE_COMPROBANTE_CNT.ANO         =   '||MI_ANIO ||'
                          AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE   = '''||UN_TIPO_CPTE ||'''
                          AND DETALLE_COMPROBANTE_CNT.COMPROBANTE =   '||MI_COMPROBANTE||'
                          AND DETALLE_COMPROBANTE_CNT.VALOR_CREDITO NOT IN(0) 
                          AND DETALLE_COMPROBANTE_CNT.EJECUCION_CREDITO IN(0)';
            
          BEGIN  
          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'DETALLE_COMPROBANTE_CNT', 
                                      UN_ACCION    => 'M', 
                                      UN_CAMPOS    => MI_CAMPOS, 
                                      UN_CONDICION => MI_CONDICION );
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
          END;    
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    =>  SQLCODE,
                                        UN_ERROR_COD  => PCK_ERRORES.ERRR_BORRARDETCOMPRINTERFAZ,
                                        UN_TABLAERROR => 'DETALLE_COMPROBANTE_CNT');
          END;
      END IF;
    
      IF UN_TERCERO NOT IN (0) AND MI_COMP = 0 THEN
        MI_COMPROBANTE := MI_COMPROBANTE + 1;
      END IF;
      
    END LOOP;    
	 
    END IF;

  RETURN MI_CADENA_REG;
  
END FC_CARGARINTERFAZ_POR_XLS;

FUNCTION FC_PROCESO_JUDICIAL_XLS(
/*
    NAME              : FC_PROCESO_JUDICIAL_XLS
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : SEBASTIAN CARDENAS
    DATE MIGRADOR     : 15/03/2021
    TIME              : 09:00 AM
    SOURCE MODULE     : I
    MODIFIER          : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MODIFIED     : 27/05/2025
    TIME              : 
    MODIFICATIONS     : Se ajusta proceso para agregar validaciones y que la informacion se genere correctamente
    DESCRIPTION       : 
    PARAMETERS        : 

      @NAME:    cargarProcesoJudicialporXls
      @METHOD:  GET
  */
    UN_COMPANIA               IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO                   IN VARCHAR2,
    UN_TIPO_CPTE              IN VARCHAR2,
    UN_NUMERO                 IN VARCHAR2,
    UN_CADENA                 IN CLOB,
    UN_COLFECHA               IN PCK_SUBTIPOS.TI_ENTERO, 
    UN_COLIDCONTABLE          IN PCK_SUBTIPOS.TI_ENTERO, 												
    UN_COLDETALLE             IN PCK_SUBTIPOS.TI_ENTERO,
    UN_COLTEXTO               IN PCK_SUBTIPOS.TI_ENTERO,
    UN_COLBASE                IN PCK_SUBTIPOS.TI_ENTERO,
    UN_COLCONTRATO            IN PCK_SUBTIPOS.TI_ENTERO,
    UN_COLDOCUMENTO           IN PCK_SUBTIPOS.TI_ENTERO,
    UN_COLTERCERO             IN PCK_SUBTIPOS.TI_ENTERO,
    UN_COLSUCURSAL            IN PCK_SUBTIPOS.TI_ENTERO,
    UN_COLCENTROCOSTO         IN PCK_SUBTIPOS.TI_ENTERO,
	UN_COLFUENTER             IN PCK_SUBTIPOS.TI_ENTERO,
    UN_COLREFERENCIA          IN PCK_SUBTIPOS.TI_ENTERO,																											
    UN_COLAUXILIAR            IN PCK_SUBTIPOS.TI_ENTERO,
    UN_COLTIPOCONTRATO        IN PCK_SUBTIPOS.TI_ENTERO,
    UN_COLTIPOPAGO_SIA        IN PCK_SUBTIPOS.TI_ENTERO,
    UN_COLCODIGO_SIA          IN PCK_SUBTIPOS.TI_ENTERO,
    UN_COLVALOR_DEBITO        IN PCK_SUBTIPOS.TI_ENTERO,	
    UN_COLVALOR_CREDITO       IN PCK_SUBTIPOS.TI_ENTERO,
    UN_COLNRO_PROCESO         IN PCK_SUBTIPOS.TI_ENTERO,
    UN_TERCERO                IN PCK_SUBTIPOS.TI_LOGICO,
    UN_AGRUPADO               IN PCK_SUBTIPOS.TI_LOGICO,
    UN_APLICAAUX              IN PCK_SUBTIPOS.TI_LOGICO,
    UN_MODULO                 IN PCK_SUBTIPOS.TI_MODULO,
    UN_FILAINI                IN PCK_SUBTIPOS.TI_ENTERO,							  									  
    UN_DESCRIPCION            IN VARCHAR2,
    UN_USUARIO                IN VARCHAR2
    
  )  RETURN CLOB 
AS 

    MI_RTA            VARCHAR2(200);
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
    MI_FILA           PCK_SUBTIPOS.TI_ENTERO_LARGO;  
    MI_ETAPA          VARCHAR2(150 CHAR);
    MI_COMPROBANTE    PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_COMPROBANTE_TERC   PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_CONSECUTIVO    PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_ANIO           PCK_SUBTIPOS.TI_ANIO;
    MI_FECHA          DATE; 
    MI_ID_CONTABLE    PCK_SUBTIPOS.TI_ENTERO_LARGO; --MPEREZ TICKET 7731490
    MI_VALOR          PCK_SUBTIPOS.TI_DOBLE := 0;
	MI_VALORCRED      PCK_SUBTIPOS.TI_DOBLE := 0;											 
    MI_DETALLE        VARCHAR2(30000 CHAR);
	MI_FUENTER        VARCHAR2(30000 CHAR);
    MI_REFERENCIA     VARCHAR2(30000 CHAR);									   									   
    MI_TEXTO          VARCHAR2(30000 CHAR);
    MI_BASE           PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_CONTRATO       PCK_SUBTIPOS.TI_ENTERO;
    MI_DOCUMENTO      VARCHAR2(30000 CHAR);
    MI_TERCERO        PCK_SUBTIPOS.TI_TERCERO;
    MI_NATURALEZA     PCK_SUBTIPOS.TI_NATURALEZACONTA;
    MI_NATURALEZA_CONTRA     PCK_SUBTIPOS.TI_NATURALEZACONTA;
    MI_SUCURSAL       PCK_SUBTIPOS.TI_SUCURSAL;
    MI_CENTRO_COSTO   PCK_SUBTIPOS.TI_CENTRO_COSTO;
    MI_AUXILIAR       PCK_SUBTIPOS.TI_AUXILIAR;
    MI_TIPO_CONTRATO  VARCHAR2(30000 CHAR);
    MI_NOMBRETERCERO  VARCHAR2(30000 CHAR);
    MI_TOTAL          PCK_SUBTIPOS.TI_DOBLE;
    MI_PAR_TOTAL      PARAMETRO.VALOR%TYPE;
    MI_STRSQL         PCK_SUBTIPOS.TI_STRSQL;
    RSDATOS           SYS_REFCURSOR;
    RS_COMPANIA       PCK_SUBTIPOS.TI_COMPANIA;
    RS_ANO            PCK_SUBTIPOS.TI_ANIO;
    RS_TIPO_CPTE      DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE_AFECT%TYPE;
    RS_CMPTE          DETALLE_COMPROBANTE_PPTAL.CMPTE_AFECTADO%TYPE;
    RS_TIPO_RET       VARCHAR2(5 CHAR);
    RS_COD_RET        VARCHAR2(5 CHAR);
    RS_VALOR          PCK_SUBTIPOS.TI_ENTERO;
    RS_VALORBASE      PCK_SUBTIPOS.TI_ENTERO;
    MI_CODIGO_CUENTA         VARCHAR2(30000 CHAR);
    MI_GRUPO                  PCK_SUBTIPOS.TI_LOGICO;
    MI_CON                    PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_MSGERROR        PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_EXISTE                VARCHAR2(2 CHAR);
    MI_CEN_COS         PCK_SUBTIPOS.TI_ENTERO;
    MI_AUX_CUEN        PCK_SUBTIPOS.TI_ENTERO;
    MI_COMP            PCK_SUBTIPOS.TI_ENTERO;
    MI_MAN_CEN_CTO     PCK_SUBTIPOS.TI_ENTERO;
    MI_MAN_AUX_TER     PCK_SUBTIPOS.TI_ENTERO;
    MI_MAN_AUX_FUE     PCK_SUBTIPOS.TI_ENTERO;
    MI_TERC            PCK_SUBTIPOS.TI_ENTERO;
    MI_PCT_APLICAR     PCK_SUBTIPOS.TI_PORCENTAJE;
    MI_VALORB          PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_VLRCRED          PCK_SUBTIPOS.TI_DOBLE;
    MI_SUMCRED          PCK_SUBTIPOS.TI_DOBLE;
    MI_COMPROB         PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_PAR_CONTROL     VARCHAR2(5 CHAR);
    MI_TIPOPAGO_SIA    VARCHAR2(30000 CHAR);
    MI_CODIGO_SIA      VARCHAR2(30000 CHAR);
    MI_NRO_PROCESO     VARCHAR2(30000 CHAR);
    MI_ERR_TERCERO     CLOB := NULL;
    MI_ERR_CENTROC     CLOB := NULL;
    MI_ERR_AUXILIAR    CLOB := NULL;  
    MI_TT_TERCERO      CLOB;
    MI_TT_CENTROC      CLOB;
    MI_TT_AUXILIAR     CLOB;
    MI_TTF_TERCERO     CLOB;
    MI_TTF_CENTROC     CLOB;
    MI_TTF_AUXILIAR    CLOB;
    MI_RETORNO         CLOB := NULL;
    MI_FECHA_TEXTO    CLOB;
    MI_SALTO_LINEA    VARCHAR(100 CHAR) := CHR(13) || CHR(10);
    MI_ERR_TERCEROS   CLOB := NULL;
    MI_TERCEROPROCESO   PCK_SUBTIPOS.TI_TERCERO;
    MI_SUCURSALPROCESO  PCK_SUBTIPOS.TI_SUCURSAL;
    
BEGIN
    MI_FILA := UN_FILAINI;
    MI_CADENA :='';
    MI_COMPROBANTE := UN_NUMERO;

  EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS = ''.,''';
  MI_ETAPA := '1';
  MI_CONSECUTIVO := 1;
  
  MI_PAR_CONTROL := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                          UN_NOMBRE    => 'INTERFACE POR ARCHIVO XLS SIN CONTROLES',
                                                          UN_MODULO    =>  PCK_DATOS.MODULOCONTABILIDAD,
                                                          UN_FECHA_PAR =>  SYSDATE),'NO'); 

     
     MI_TOTAL := 0;
     MI_REGISTROS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => '' || CASE WHEN SUBSTR(UN_CADENA, LENGTH(UN_CADENA),1) ='#' THEN SUBSTR(UN_CADENA,1,LENGTH(UN_CADENA)-1) ELSE UN_CADENA END || '',
                                                UN_DELIMITADOR  => '#');
    <<RECORRE_REGISTROS>>
    FOR RS IN MI_REGISTROS.FIRST..MI_REGISTROS.LAST 
    LOOP
        MI_CADENA_REG := '';
        MI_REG_CAMPOS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => '' || TO_CHAR(MI_REGISTROS(RS)) || '', 
                                                     UN_DELIMITADOR  => ';');
        BEGIN 
            BEGIN 
                BEGIN
                    MI_FECHA       := TO_DATE(MI_REG_CAMPOS(UN_COLFECHA),'DD/MM/YYYY');
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG  || '3; - Inconsistencia en la Fecha ' || TO_CHAR(MI_REG_CAMPOS(UN_COLFECHA)) || ' formato DD/MM/YYYY';
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_DOCUMENTO    := NVL(MI_REG_CAMPOS(UN_COLDOCUMENTO),' ');
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Inconsistencia en el documento ' || MI_REG_CAMPOS(UN_COLDOCUMENTO) ;
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_CONTRATO    := TO_NUMBER(NVL(MI_REG_CAMPOS(UN_COLCONTRATO),0));
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Inconsistencia en el contrato ' || MI_REG_CAMPOS(UN_COLCONTRATO) ;
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_TIPO_CONTRATO    := NVL(MI_REG_CAMPOS(UN_COLTIPOCONTRATO),'');
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Inconsistencia en el tipo contrato ' || MI_REG_CAMPOS(UN_COLTIPOCONTRATO) ;
                    GOTO SIGUIENTE;
                END;
 
                BEGIN
                    MI_ID_CONTABLE      := TO_NUMBER(NVL(MI_REG_CAMPOS(UN_COLIDCONTABLE),0));
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Inconsistencia en el Id Contable ' || TO_CHAR(MI_REG_CAMPOS(UN_COLIDCONTABLE));
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_VALOR      := TO_NUMBER(NVL(TO_CHAR(MI_REG_CAMPOS(UN_COLVALOR_DEBITO)),0));
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Inconsistencia en el Valor ' || MI_REG_CAMPOS(UN_COLVALOR_DEBITO) || '. Revise que todos los valores de la columna T sean numericos.';
                    RETURN MI_CADENA_REG;
                END;
                BEGIN
				    MI_VALORCRED   := TO_NUMBER(NVL(TO_CHAR(MI_REG_CAMPOS(UN_COLVALOR_CREDITO)),0));
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Inconsistencia en el Valor ' || MI_REG_CAMPOS(UN_COLVALOR_CREDITO) || '. Revise que todos los valores de la columna U sean numericos.';
                    RETURN MI_CADENA_REG;
                END;
                BEGIN																					 
                    MI_DETALLE   := NVL(TO_CHAR(MI_REG_CAMPOS(UN_COLDETALLE)), UN_DESCRIPCION);                
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Inconsistencia en el detalle ' || MI_REG_CAMPOS(UN_COLDETALLE);
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_BASE    := TO_NUMBER(NVL(TO_CHAR(MI_REG_CAMPOS(UN_COLBASE)),0));
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Inconsistencia en la base ' || MI_REG_CAMPOS(UN_COLBASE) ;
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_TERCERO    := NVL(MI_REG_CAMPOS(UN_COLTERCERO),'999999999999999999' );
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Inconsistencia en el tercero ' || MI_REG_CAMPOS(UN_COLTERCERO) ;
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_SUCURSAL    := NVL(MI_REG_CAMPOS(UN_COLSUCURSAL),' ');
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Inconsistencia en la sucursal ' || MI_REG_CAMPOS(UN_COLSUCURSAL) ;
                    GOTO SIGUIENTE;
                END;
                BEGIN
                    MI_CENTRO_COSTO    := NVL(MI_REG_CAMPOS(UN_COLCENTROCOSTO),'99999999999999999999');
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Inconsistencia en el centro de costo ' || MI_REG_CAMPOS(UN_COLCENTROCOSTO) ;
                    GOTO SIGUIENTE;
                END;
                --control
                BEGIN
                    MI_TEXTO    := NVL(TO_CHAR(MI_REG_CAMPOS(UN_COLTEXTO)), UN_DESCRIPCION);               
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Inconsistencia en el texto ' || MI_REG_CAMPOS(UN_COLTEXTO);
                    GOTO SIGUIENTE;
                END;
                BEGIN																									   
                    MI_AUXILIAR    := NVL(MI_REG_CAMPOS(UN_COLAUXILIAR),'99999999999999999999' );
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Inconsistencia en el auxiliar ' || MI_REG_CAMPOS(UN_COLAUXILIAR) ;
                    GOTO SIGUIENTE;
                END;              
                
                BEGIN																									   
                    MI_NRO_PROCESO := NVL(MI_REG_CAMPOS(UN_COLNRO_PROCESO),'' );
                    IF LENGTH(MI_NRO_PROCESO) <> 23 THEN 
                    MI_CADENA_REG  := MI_CADENA_REG || 'El proceso '|| MI_NRO_PROCESO || ' No cumple con la longitud requerida que son 23 caracteres.'  ;
                    GOTO SIGUIENTE;
                    END IF;
                EXCEPTION WHEN OTHERS THEN
                    MI_CADENA_REG  := MI_CADENA_REG || '3; - Inconsistencia en el auxiliar ' || MI_REG_CAMPOS(UN_COLNRO_PROCESO) ;
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
     <<SIGUIENTE>> 
                 
             
  BEGIN
    SELECT 
      COUNT(COMPANIA)
    INTO MI_EXISTE
    FROM COMPROBANTE_CNT
    WHERE COMPANIA = UN_COMPANIA
      AND ANO      = UN_ANIO
      AND TIPO     = UN_TIPO_CPTE
      AND NUMERO   = MI_COMPROBANTE;
  EXCEPTION WHEN NO_DATA_FOUND THEN
    MI_EXISTE:=0;
  END;
  --'Si está creado avisa que existe y termina
  IF MI_EXISTE > 0 THEN
      BEGIN
      MI_CADENA := 'El comprobante de tipo '|| UN_TIPO_CPTE ||', numero '|| MI_COMPROBANTE ||' para el año '|| MI_ANIO ||', ya existe';
               RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;           
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                MI_MSGERROR(1).CLAVE := 'MENSAJE';
                MI_MSGERROR(1).VALOR := MI_CADENA;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                                   UN_EXC_COD =>SQLCODE
                                  ,UN_ERROR_COD=>PCK_ERRORES.ERRR_PERSONALIZADO
                                  ,UN_REEMPLAZOS  => MI_MSGERROR);            
      END;
      END IF;
      
      BEGIN
      SELECT COUNT(0)INTO MI_COMP
      FROM TEMP_PLANA_AJUSTES 
      WHERE COMPANIA = UN_COMPANIA AND ANO = MI_ANIO
        AND TIPO_CPTE = UN_TIPO_CPTE AND TERCERO = MI_TERCERO;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_COMP:=0;
      END;

      
        MI_NATURALEZA := PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_ID_CONTABLE);   
        BEGIN
            BEGIN
                MI_CAMPOS    := 'COMPANIA,
                                ANO,
                                TIPO_CPTE,
                                COMPROBANTE,
                                CONSECUTIVO,
                                CUENTA,
                                FECHA,
                                NATURALEZA,
                                DESCRIPCION,
                                CENTRO_COSTO,
                                TERCERO,
                                SUCURSAL,
                                AUXILIAR,
                                VALOR_DEBITO,
                                VALOR_CREDITO,
                                BASE_GRAVABLE,
                                NRO_DOCUMENTO,
                                TEXTOD,
                                NUMEROCONTRATO,
                                TIPOCONTRATO,
                                FUENTE_RECURSOS,
                                REFERENCIA,
                                NUMEROPROCESO';									
                                
                MI_VALORES   := '  '''|| UN_COMPANIA ||'''
                                , '|| UN_ANIO ||'
                                , '''|| UN_TIPO_CPTE ||'''
                                , '|| MI_COMPROBANTE ||'
                                , '|| MI_CONSECUTIVO ||'
                                , '''|| MI_ID_CONTABLE ||'''
                                , TO_DATE(''' || TO_CHAR(MI_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                                , '''|| MI_NATURALEZA ||'''
                                ,'''||  MI_DETALLE || '''
                                ,'''|| MI_CENTRO_COSTO ||'''
                                ,'''|| MI_TERCERO ||'''
                                ,'''|| MI_SUCURSAL ||'''
                                ,'''|| MI_AUXILIAR ||'''
                                ,'|| MI_VALOR ||'
                                ,'|| MI_VALORCRED ||'
                                ,'|| MI_BASE ||'
                                ,'''|| MI_DOCUMENTO ||'''
                                ,'''|| MI_TEXTO ||'''
                                ,'|| MI_CONTRATO ||'
                                ,'''|| MI_TIPO_CONTRATO ||'''
								,'''|| NVL(MI_FUENTER,'99999999999999999999') ||'''
                                ,'''|| NVL(MI_REFERENCIA, PCK_DATOS.FC_CONS_REFERENCIA()) ||'''
                                ,'''|| MI_NRO_PROCESO ||''' ';

                MI_RTAACME       := PCK_DATOS.FC_ACME (UN_TABLA     => 'TEMP_PLANA_AJUSTES',
                                                       UN_ACCION    => 'I',
                                                       UN_CAMPOS    => MI_CAMPOS,
                                               UN_VALORES   => MI_VALORES); 
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;   
            
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            MI_MSGERROREXC(1).CLAVE := 'FECHA';
            MI_MSGERROREXC(1).VALOR := TO_CHAR(MI_FECHA,'DD/MM/YYYY');
            MI_MSGERROREXC(2).CLAVE := 'ANIO';
            MI_MSGERROREXC(2).VALOR := UN_ANIO;
            MI_MSGERROREXC(3).CLAVE := 'COMPROBANTE';
            MI_MSGERROREXC(3).VALOR := MI_COMPROBANTE;            
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_ACTDETCOMCONC,
                                    UN_REEMPLAZOS => MI_MSGERROREXC,
                                    UN_TABLAERROR => 'TEMP_PLANA_AJUSTES'
            );                      
        END;
        
        BEGIN
            MI_ETAPA := 'Etapa 11';
            MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
        END;
        
		
         MI_ETAPA := 'Etapa 12';
        
        MI_FILA := MI_FILA + 1;
        
      END LOOP RECORRE_REGISTROS; 
        
      MI_ETAPA := 'Etapa 15';

      MI_CONSECUTIVO := 1; 
      MI_FECHA_TEXTO := ' ***** Fecha Proceso: '|| TO_CHAR(CURRENT_DATE, 'DD/MM/YYYY HH:MI:SS AM') ||'******';
      MI_TT_TERCERO := '---------------------------------- Inconsistencias en Terceros del archivo---------------------------------------'  || MI_SALTO_LINEA || MI_FECHA_TEXTO || MI_SALTO_LINEA;
      MI_TT_CENTROC := '---------------------------------- Inconsistencias en Centro Costo del archivo---------------------------------------' || MI_SALTO_LINEA || MI_FECHA_TEXTO || MI_SALTO_LINEA;
      MI_TT_AUXILIAR := '---------------------------------- Inconsistencias en Auxiliar del archivo---------------------------------------' || MI_SALTO_LINEA || MI_FECHA_TEXTO || MI_SALTO_LINEA;  
      MI_TTF_TERCERO := 'Sugerencias:' || MI_SALTO_LINEA ||
                      '1. Si el tercero Existe, pero tiene el NIT errado. Utilice la opcion MANTENIMIENTO/CAMBIAR NIT A TERCEROS. Para este caso cambielo por el Nit Real el sistema actualizara los datos historicos del tercero.' || MI_SALTO_LINEA || 
                      '2. Si el tercero NO Existe Debe crearlo por ARCHIVOS/TERCEROS' || MI_SALTO_LINEA ||
                      '*****************Fin de revision Terceros*********************************' || MI_SALTO_LINEA;
      MI_TTF_CENTROC := 'Sugerencias:' || MI_SALTO_LINEA ||
                      '1. Si el centro de costo NO Existe Debe crearlo por ARCHIVOS/CENTROS DE COSTO' || MI_SALTO_LINEA ||
                      '*****************Fin de revision Centro Costo*********************************' || MI_SALTO_LINEA;
      MI_TTF_AUXILIAR := 'Sugerencias:' || MI_SALTO_LINEA ||
                      '1. Si el auxiliar NO Existe Debe crearlo por ARCHIVOS/AUXILIARES GENERALES' || MI_SALTO_LINEA ||
                      '*****************Fin de revision Auxiliar*********************************' || MI_SALTO_LINEA;
      FOR RS IN(SELECT TEMP_PLANA_AJUSTES.CONSECUTIVO, 
                       TEMP_PLANA_AJUSTES.TIPO_CPTE,
                       TERCERO.NIT,
                       CCO.CODIGO  CENTROCODIGO,
                       AX.CODIGO   AUXILIARCODIGO,
                       TEMP_PLANA_AJUSTES.TERCERO,
                       TEMP_PLANA_AJUSTES.SUCURSAL, 
                       TEMP_PLANA_AJUSTES.CENTRO_COSTO, 
                       TEMP_PLANA_AJUSTES.AUXILIAR                       
                            FROM TEMP_PLANA_AJUSTES 
                            LEFT JOIN TERCERO 
                             ON TEMP_PLANA_AJUSTES.SUCURSAL = TERCERO.SUCURSAL 
                            AND TEMP_PLANA_AJUSTES.TERCERO  = TERCERO.NIT 
                            AND TEMP_PLANA_AJUSTES.COMPANIA = TERCERO.COMPANIA
                            LEFT JOIN CENTRO_COSTO CCO
                             ON  TEMP_PLANA_AJUSTES.COMPANIA     = CCO.COMPANIA
                            AND TEMP_PLANA_AJUSTES.ANO          = CCO.ANO
                            AND TEMP_PLANA_AJUSTES.CENTRO_COSTO = CCO.CODIGO
                            LEFT JOIN AUXILIAR AX
                             ON  TEMP_PLANA_AJUSTES.COMPANIA = AX.COMPANIA
                            AND TEMP_PLANA_AJUSTES.ANO      = AX.ANO
                            AND TEMP_PLANA_AJUSTES.AUXILIAR = AX.CODIGO
                            WHERE TEMP_PLANA_AJUSTES.COMPANIA  = UN_COMPANIA
                              AND TEMP_PLANA_AJUSTES.TIPO_CPTE = UN_TIPO_CPTE
                              AND TERCERO.NIT IS NULL
                                OR CCO.CODIGO IS NULL
                                OR AX.CODIGO IS NULL
                            ORDER BY TEMP_PLANA_AJUSTES.CONSECUTIVO)
  LOOP
  
    MI_ETAPA := 'Terceros que no existen en la tabla';
    IF RS.NIT IS NULL THEN
                MI_ERR_TERCERO := MI_ERR_TERCERO || TO_CLOB('El Tercero con NIT : '|| RS.TERCERO || ' - Sucursal : '|| RS.SUCURSAL ||', no existe en la base de datos. Debe ser creado antes de interfaz el archivo.' || MI_SALTO_LINEA);

    END IF;
     IF RS.CENTROCODIGO IS NULL THEN
                MI_ERR_CENTROC := MI_ERR_CENTROC || TO_CLOB('El Centro de costo '|| RS.CENTRO_COSTO || ', no existe en la base de datos. Debe ser creado antes de interfaz el archivo.' || MI_SALTO_LINEA);
     END IF;
     IF RS.AUXILIARCODIGO IS NULL THEN
                MI_ERR_AUXILIAR := MI_ERR_AUXILIAR || TO_CLOB('El Auxiliar '|| RS.AUXILIAR || ', no existe en la base de datos. Debe ser creado antes de interfaz el archivo.' || MI_SALTO_LINEA);
     END IF;     
  END LOOP; 

    IF MI_ERR_TERCERO IS NOT NULL OR MI_ERR_CENTROC IS NOT NULL OR MI_ERR_AUXILIAR IS NOT NULL THEN 
    MI_RETORNO := CASE WHEN MI_ERR_TERCERO IS NOT NULL THEN MI_TT_TERCERO || MI_ERR_TERCERO || MI_SALTO_LINEA || MI_TTF_TERCERO || MI_SALTO_LINEA ELSE '' END ||
                  CASE WHEN MI_ERR_CENTROC IS NOT NULL THEN MI_TT_CENTROC || MI_ERR_CENTROC || MI_SALTO_LINEA || MI_TTF_CENTROC || MI_SALTO_LINEA ELSE '' END ||
                  CASE WHEN MI_ERR_AUXILIAR IS NOT NULL THEN MI_TT_AUXILIAR || MI_ERR_AUXILIAR || MI_SALTO_LINEA || MI_TTF_AUXILIAR || MI_SALTO_LINEA ELSE '' END;
    RETURN MI_RETORNO;
    END IF;

    FOR RSPROCESOS IN (SELECT TEMP_PLANA_AJUSTES.TERCERO,TEMP_PLANA_AJUSTES.SUCURSAL,
                              TEMP_PLANA_AJUSTES.NUMEROPROCESO
                         FROM TEMP_PLANA_AJUSTES
                        WHERE TEMP_PLANA_AJUSTES.COMPANIA  = UN_COMPANIA)
    LOOP
        BEGIN
          SELECT DISTINCT TERCERO, SUCURSAL
            INTO MI_TERCEROPROCESO, MI_SUCURSALPROCESO
            FROM PROCESOS_JUDICIALES
           WHERE COMPANIA = UN_COMPANIA
             AND NUMEROPROCESO = RSPROCESOS.NUMEROPROCESO;
          EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_TERCEROPROCESO := NULL;
            MI_SUCURSALPROCESO := NULL;
            WHEN TOO_MANY_ROWS THEN -- JM CC 4003
            MI_ERR_TERCEROS := MI_ERR_TERCEROS || TO_CLOB('El proceso con numero: '|| RSPROCESOS.NUMEROPROCESO || ' solo puede estar asociado a un unico tercero ' || MI_SALTO_LINEA ); --4003
        END;
        IF(MI_TERCEROPROCESO IS NOT NULL AND (RSPROCESOS.TERCERO <> MI_TERCEROPROCESO OR RSPROCESOS.SUCURSAL <> MI_SUCURSALPROCESO))THEN
            MI_ERR_TERCEROS := MI_ERR_TERCEROS || TO_CLOB('El proceso con numero: '|| RSPROCESOS.NUMEROPROCESO || ' ya se encuentra registrado con el tercero: ' || MI_TERCEROPROCESO || ' - Sucursal: '|| MI_SUCURSALPROCESO || MI_SALTO_LINEA );    
        END IF; 
    END LOOP;
     
     IF MI_ERR_TERCEROS IS NOT NULL THEN      
        RETURN MI_ERR_TERCEROS;     
     END IF;
    
    MI_COMPROBANTE := UN_NUMERO;
    MI_GRUPO := 0;
    
    IF UN_AGRUPADO NOT IN (0) THEN
        MI_TOTAL := 0;
    
        FOR RS IN(SELECT DISTINCT FECHA 
                                FROM TEMP_PLANA_AJUSTES 
                                WHERE TIPO_CPTE = UN_TIPO_CPTE 
                                GROUP BY FECHA ORDER BY FECHA )
        LOOP
      
            MI_ETAPA := 'Etapa 21';
      
            BEGIN
                MI_CAMPOS := 'COMPROBANTE  = '|| MI_COMPROBANTE || ',
                             MODIFIED_BY   = '''|| UN_USUARIO ||''',
                             DATE_MODIFIED = SYSDATE
                        ';
                MI_CONDICION := 'COMPANIA   =''' ||UN_COMPANIA||'''
                            AND FECHA = TO_DATE('''|| RS.FECHA ||''' , ''DD/MM/YYY'')';

                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'TEMP_PLANA_AJUSTES', 
                                      UN_ACCION    => 'M', 
                                      UN_CAMPOS    => MI_CAMPOS, 
                                      UN_CONDICION => MI_CONDICION );
                                    
                IF MI_RTA = 0 THEN
                    MI_CADENA := MI_CADENA || TO_CLOB('Error al actualizar el Comprobante ' ||MI_COMPROBANTE || CHR(13) || CHR(10));
                END IF;
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;           
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                MI_MSGERROR(1).CLAVE := 'MENSAJE';
                MI_MSGERROR(1).VALOR := MI_CADENA;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                                   UN_EXC_COD =>SQLCODE
                                  ,UN_ERROR_COD=>PCK_ERRORES.ERRR_PERSONALIZADO
                                  ,UN_REEMPLAZOS  => MI_MSGERROR);
            
            END;  
      
            MI_COMPROBANTE := MI_COMPROBANTE + 1;
      
            MI_TOTAL := MI_TOTAL + 1;
    
        END LOOP;
    END IF;
     
      MI_ETAPA := 'Etapa 22';
      
        IF UN_TERCERO NOT IN (0) OR UN_AGRUPADO NOT IN (0) Then
            MI_GRUPO := '-1';
        ELSE
            MI_GRUPO := 0;
        END IF;       
   
        MI_RTA:= PCK_CONTABILIZAR.FC_CONTABILIZAR(
                               UN_COMPANIA         => UN_COMPANIA
                              ,UN_TIPOCOMPROBANTE  => UN_TIPO_CPTE
                              ,UN_NUMERO           => MI_COMPROBANTE
                              ,UN_ANO              => TO_NUMBER(UN_ANIO)
                              ,UN_FECHA            => MI_FECHA
                              ,UN_TERCERO          => MI_TERCERO
                              ,UN_SUCURSAL         => MI_SUCURSAL
                              ,UN_CENTRO_COSTO     => MI_CENTRO_COSTO
                              ,UN_AUXILIAR         => MI_AUXILIAR
                              ,UN_DESCRIPCION      => MI_DETALLE
                              ,UN_SIMPLE           => CASE WHEN UPPER('FALSE') = 'TRUE' THEN -1 ELSE 0 END
                              ,UN_INDIMPRESION     => 0
                              ,UN_INTOMITIRPPTAL   => 0 
                              ,UN_CONCILIAR        => 0  
                              ,UN_PLANO            => -1
                              ,UN_NONETEA          => 0  
                              ,UN_CONTRATISTA      => 0   
                              ,UN_TEXTO            => MI_TEXTO
                              ,UN_CONTRATO         => TO_NUMBER(MI_CONTRATO)
                              ,UN_TIPOCONTRATO     => MI_TIPO_CONTRATO
                              ,UN_NRO_DOCUMENTO    => MI_NRO_PROCESO
                              ,UN_NRO_DOCUMENTO_2  => MI_NRO_PROCESO
                              ,UN_ALMDEP           => CASE WHEN UPPER('FALSE') = 'TRUE' THEN -1 ELSE 0 END
                              ,UN_TERCE            => CASE WHEN UPPER('FALSE') = 'TRUE' THEN -1 ELSE 0 END 
                              ,UN_RESAUXGEN        => CASE WHEN UPPER('FALSE') = 'TRUE' THEN -1 ELSE 0 END
                              ,UN_USUARIO          => UN_USUARIO
                            );
            PCK_CONTABILIZAR.PR_PROCESOS_JUDICIALES(UN_COMPANIA,UN_ANIO,UN_TIPO_CPTE,MI_COMPROBANTE,UN_USUARIO);      
      
          MI_ETAPA := 'Etapa 24';

  RETURN MI_CADENA_REG;
  
END FC_PROCESO_JUDICIAL_XLS;

PROCEDURE PR_PROCESOS_JUDICIALES

(UN_COMPANIA               IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO                   IN VARCHAR2,
    UN_TIPO_CPTE              IN VARCHAR2,
    UN_NUMERO                 IN VARCHAR2,
    UN_USUARIO                IN VARCHAR2)
    AS 
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES            PCK_SUBTIPOS.TI_VALORES;  
    MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
    MI_VALOR_PJ           NUMBER(20,2);
    MI_STRSQL             PCK_SUBTIPOS.TI_STRSQL;   
    MI_RSPJ               SYS_REFCURSOR;
    MI_NUMEROPROCESO      PROCESOS_JUDICIALES.NUMEROPROCESO%TYPE;
    MI_VALOR_ACTUAL       PROCESOS_JUDICIALES.VALOR_ACTUAL%TYPE;
    MI_CODIGO             PROCESOS_JUDICIALES.CODIGO%TYPE;
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR; 
    MI_VALOR              NUMBER(20,2);
    MI_ACT                VARCHAR2(5 CHAR):= 'FALSE';
    MI_EXISTENCUENTA      NUMBER(1,0) := 0;
    MI_EXISTENCUENTAS     NUMBER(1,0) := 0;
    MI_PARNEGATIVOS       PARAMETRO.VALOR%TYPE; -- JM CC 4003
BEGIN
MI_PARNEGATIVOS := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                          UN_NOMBRE    => 'PERMITE SUBIR PROCESOS JUDICIALES CON SALDOS NEGATIVOS',
                                                          UN_MODULO    =>  PCK_DATOS.MODULOCONTABILIDAD,
                                                          UN_FECHA_PAR =>  SYSDATE),'NO'); -- jm cc 4003    
  IF UN_TIPO_CPTE = 'CPJ' THEN
      FOR RS IN (SELECT DCNT.TIPO_CPTE, DCNT.COMPROBANTE,
                        DCNT.NUMEROPROCESO,
                        DCNT.CUENTA,
                        DCNT.VALOR_DEBITO,
                        DCNT.VALOR_CREDITO,
                        PC.NATURALEZA,
                        DCNT.TERCERO,
                        DCNT.SUCURSAL,
                        DCNT.FECHA,
                        PC.CLASECUENTA  --jm cc 4003
                        FROM DETALLE_COMPROBANTE_CNT DCNT 
                        INNER JOIN COMPROBANTE_CNT CNT 
                         ON CNT.COMPANIA = DCNT.COMPANIA 
                        AND CNT.ANO = DCNT.ANO 
                        AND CNT.TIPO = DCNT.TIPO_CPTE 
                        AND CNT.NUMERO = DCNT.COMPROBANTE
                        INNER JOIN PLAN_CONTABLE  PC 
                         ON DCNT.COMPANIA = PC.COMPANIA 
                        AND DCNT.ANO = PC.ANO 
                        AND DCNT.CUENTA = PC.CODIGO
                        WHERE DCNT.COMPANIA = UN_COMPANIA
                        AND DCNT.ANO = UN_ANIO
                        AND DCNT.TIPO_CPTE = UN_TIPO_CPTE
                        AND DCNT.COMPROBANTE = UN_NUMERO
                        AND NOT DCNT.NUMEROPROCESO IS NULL)
      LOOP
              
              
              MI_CAMPOS := 'PROCESOJUDICIAL   = -1';

              MI_CONDICION := 'COMPANIA = '''|| UN_COMPANIA ||'''
                           AND NIT = '''|| RS.TERCERO ||'''
                           AND SUCURSAL    ='''|| RS.SUCURSAL ||''' ';

  
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'TERCERO'
                                                    ,UN_ACCION    => 'M'
                                                    ,UN_CAMPOS    => MI_CAMPOS
                                                    ,UN_CONDICION => MI_CONDICION);
              
          
            IF RS.NATURALEZA = 'C' THEN

                MI_VALOR_PJ := NVL(RS.VALOR_CREDITO,0) - NVL(RS.VALOR_DEBITO, 0);                
            ELSE

               MI_VALOR_PJ := NVL(RS.VALOR_DEBITO,0) - NVL(RS.VALOR_CREDITO, 0);
            END IF;            
                     
          MI_STRSQL := 'SELECT NUMEROPROCESO,VALOR_ACTUAL
                        FROM PROCESOS_JUDICIALES
                        WHERE COMPANIA = '''|| UN_COMPANIA ||'''
                        AND NUMEROPROCESO = '''|| RS.NUMEROPROCESO ||''' ';
         BEGIN               
            SELECT NUMEROPROCESO, SUM(VALOR_ACTUAL) VALOR_ACTUAL
            INTO MI_NUMEROPROCESO, MI_VALOR_ACTUAL
            FROM PROCESOS_JUDICIALES
            WHERE COMPANIA = UN_COMPANIA
            AND NUMEROPROCESO = RS.NUMEROPROCESO
            AND CODIGO = RS.CUENTA
            GROUP BY NUMEROPROCESO;
            
         EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_EXISTENCUENTAS := -1;
         END;
         
         IF MI_EXISTENCUENTAS NOT IN (0) THEN 
         
         MI_CAMPOS :='COMPANIA,
                       CODIGO,
                       TERCERO,
                       SUCURSAL,
                       NUMEROPROCESO,
                       VALOR,
                       FECHA_INGRESO,
                       TIPO_INGRESO,
                       COMPROBANTE_INGRESO,
                       MODIFICACIONES,
                       VALOR_ACTUAL,
                       CREATED_BY,
                       DATE_CREATED';


           MI_VALORES :=''''||UN_COMPANIA||''',
                         '''||RS.CUENTA||''',
                         '''||TRIM(RS.TERCERO)||''',
                         '''||RS.SUCURSAL||''',
                         '''||RS.NUMEROPROCESO||''',
                         '''||MI_VALOR_PJ||''',
                         TO_DATE(''' ||  TO_CHAR(RS.FECHA,'DD/MM/YYYY') ||''',''DD/MM/YYYY''),
                         '''||RS.TIPO_CPTE||''',
                         '||RS.COMPROBANTE||',
                         0,
                         '''||MI_VALOR_PJ||''',
                         '''||UN_USUARIO||''',
                         CURRENT_DATE';  

               BEGIN
                    BEGIN
                      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'PROCESOS_JUDICIALES',
                                         UN_ACCION   =>  'I', 
                                         UN_CAMPOS   =>  MI_CAMPOS, 
                                         UN_VALORES  =>  MI_VALORES);                             

                         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                 RAISE PCK_EXCEPCIONES.EXC_ALMACEN;--CAMBIAR
                    END;
                       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN  --CAMBIAR
                                MI_MSGERROR(1).CLAVE := 'TABLA';
                                MI_MSGERROR(1).VALOR := 'PROCESOS_JUDICIALES';
                                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD   => SQLCODE,
                                        UN_ERROR_COD => PCK_ERRORES.ERRR_ALMACEN_INSERT_TABLA,
                                        UN_REEMPLAZOS  => MI_MSGERROR 
                                      );
                
              END;               
              
        ELSE 
        MI_EXISTENCUENTA := 0;
        BEGIN               
                SELECT NUMEROPROCESO, VALOR_ACTUAL, CODIGO
            INTO MI_NUMEROPROCESO, MI_VALOR_ACTUAL, MI_CODIGO
            FROM PROCESOS_JUDICIALES
            WHERE COMPANIA = UN_COMPANIA
            AND NUMEROPROCESO = RS.NUMEROPROCESO
            AND CODIGO = RS.CUENTA
            AND ROWNUM = 1;
         EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_EXISTENCUENTA := -1;
         END;
         
         IF MI_EXISTENCUENTA IN (0) THEN 
         
            MI_VALOR := MI_VALOR_ACTUAL + MI_VALOR_PJ;
                IF MI_VALOR < 0  AND RS.CLASECUENTA <> 'O' AND MI_PARNEGATIVOS = 'NO' THEN --jm mod cc 4003
                    BEGIN
                        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;           
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                                MI_MSGERROR(1).CLAVE := 'MENSAJE';
                                MI_MSGERROR(1).VALOR := 'La cuenta '|| RS.CUENTA ||' no puede tener saldos negativos.';
                                PCK_ERR_MSG.RAISE_WITH_MSG(
                                                   UN_EXC_COD =>SQLCODE
                                                  ,UN_ERROR_COD=>PCK_ERRORES.ERRR_PERSONALIZADO
                                                  ,UN_REEMPLAZOS  => MI_MSGERROR);            
                    END;
                END IF;
                                
                MI_CAMPOS := 'CODIGO   = '''|| RS.CUENTA ||''',
                                              VALOR_ACTUAL = '''|| MI_VALOR ||''' ';

                MI_CONDICION := 'COMPANIA = '''|| UN_COMPANIA ||'''
                         AND CODIGO = '''|| MI_CODIGO || '''
                         AND NUMEROPROCESO ='''|| MI_NUMEROPROCESO ||''' ';

  
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'PROCESOS_JUDICIALES'
                                                    ,UN_ACCION    => 'M'
                                                    ,UN_CAMPOS    => MI_CAMPOS
                                                    ,UN_CONDICION => MI_CONDICION);
                                                    
                MI_CAMPOS := 'MODIFICACIONES = VALOR_ACTUAL - VALOR,
                                DATE_MODIFIED = CURRENT_DATE,
                                MODIFIED_BY = '''|| UN_USUARIO ||''' ';

                MI_CONDICION := 'COMPANIA = '''|| UN_COMPANIA ||'''
                         AND CODIGO = '''|| MI_CODIGO || '''
                         AND NUMEROPROCESO ='''|| MI_NUMEROPROCESO ||''' ';

  
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'PROCESOS_JUDICIALES'
                                                    ,UN_ACCION    => 'M'
                                                    ,UN_CAMPOS    => MI_CAMPOS
                                                    ,UN_CONDICION => MI_CONDICION);
                     
         END IF;
      END IF;
      MI_EXISTENCUENTAS := 0;
  
      END LOOP;
  END IF;
END PR_PROCESOS_JUDICIALES;

FUNCTION FC_CONTABILIZARPLANOSIOT(
/*
    NAME              : FC_CONTABILIZARPLANOSIOT
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MIGRADOR     : 18/07/2025
    TIME              : 05:42 PM
    SOURCE MODULE     : InterfacesPb2018.01.02, En access LlenarTablaPlana
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Función que llena la tabla temporal PlanaAjustes conlosdatos del plano SIOT
                        y realiza el proceso de contabilizar, Devuelve un CLOB con las inconsistencias
                        o comprobantes.
    PARAMETERS        :

    MODIFICATIONS     :

    @NAME:contabilizarPlanoSIOT
    @METHOD:  POST
*/

     UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_CONCILIAR        IN PCK_SUBTIPOS.TI_LOGICO := 0
    ,UN_PLANO            IN CLOB
    ,UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
) RETURN CLOB

AS
    MI_T_SPLIT               PCK_SYSMAN_UTL.T_SPLIT;
    MI_CUENTA                PCK_SUBTIPOS.TI_ENTERO;
    MI_RNAUX                 VARCHAR2(2000);
    MI_NUM_CAR_DETALLE       PCK_SUBTIPOS.TI_ENTERO;
    MI_RNTIPO                COMPROBANTE_CNT.TIPO%TYPE;
    MI_RNCODIGO_CUENTA       PCK_SUBTIPOS.TI_CODIGOCONTA;
    MI_RNFECHA               DATE;
    MI_FECHAANT              DATE;
    MI_RNFECHACONSIGNACION   DATE;
    MI_MES                   PCK_SUBTIPOS.TI_ENTERO;
    MI_RNNUMERO              PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT;
    MI_REEMPLAZOS            PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_EXISTEDATO            VARCHAR2(1 CHAR);
    MI_POS                   PCK_SUBTIPOS.TI_ENTERO;
    MI_RNANO                 PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_RNVALORDEBITO         PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
    MI_RNVALORCREDITO        PCK_SUBTIPOS.TI_DOBLE  DEFAULT 0;
    MI_RNTERCERO             PCK_SUBTIPOS.TI_TERCERO;
    MI_RNSUCURSAL            PCK_SUBTIPOS.TI_SUCURSAL;
    MI_RNCENTRO_COSTO        PCK_SUBTIPOS.TI_CENTRO_COSTO;
    MI_RNAUXILIAR            PCK_SUBTIPOS.TI_AUXILIAR;
    MI_RNFUENTERECURSO       PCK_SUBTIPOS.TI_FUENTE_RECURSOS;
    MI_RNREFERENCIA          PCK_SUBTIPOS.TI_REFERENCIA ;
    MI_RNNRO_DOCUMENTO       PCK_SUBTIPOS.TI_NRODOCUMENTO;
    MI_RNDESCRIPCION         PCK_SUBTIPOS.TI_DESCRIPCION;
    MI_RNCUENTAPPTAL         PCK_SUBTIPOS.TI_CODIGOPPTAL;
    MI_AUX                   PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_RTA                   CLOB;
    MI_PINCONSISTENCIAS      CLOB;
    MI_INCONSISTENCIA        BOOLEAN;
    MI_CLASE                 PCK_SUBTIPOS.TI_CLASECUENTACONTA;
    MI_CUENTAPPTAL           PLAN_CONTABLE.CUENTA_PPTAL%TYPE;
    MI_NUMREGISTRO           PCK_SUBTIPOS.TI_ENTERO;
    MI_RNCONSECUTIVO         PCK_SUBTIPOS.TI_ENTERO;
    MI_TABLA                 PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICIONACME         PCK_SUBTIPOS.TI_CONDICION;
    MI_FILAS                 PCK_SUBTIPOS.TI_ENTERO;
    MI_VALORES               PCK_SUBTIPOS.TI_VALORES;
    MI_NATURALEZA            PCK_SUBTIPOS.TI_NATURALEZACONTA;
    MI_RS                    SYS_REFCURSOR;
    MI_ETAPA                 VARCHAR2(150 CHAR);
    MI_RESULTADORECORD       VARCHAR2(500 CHAR);
    MI_NITHEADER             VARCHAR2(25 CHAR);
    MI_SUCURSALHEADER        VARCHAR2(25 CHAR);
    MI_NITCONTA              VARCHAR2(25 CHAR);
    MI_SUCURSALCONTA         VARCHAR2(25 CHAR);
    MI_NUMTERCEROS           NUMBER;                                
BEGIN
    BEGIN
        BEGIN
            SELECT NITCOMPANIA
            INTO   MI_NITHEADER
            FROM   COMPANIA
            WHERE  CODIGO = UN_COMPANIA;

            MI_NITHEADER := REPLACE(MI_NITHEADER,'.','');
            IF INSTR(MI_NITHEADER, '-') > 0 THEN
                MI_NITHEADER := SUBSTR(MI_NITHEADER,1,INSTR(MI_NITHEADER, '-') -1);
            END IF;

            SELECT SUCURSAL
            INTO   MI_SUCURSALHEADER
            FROM   TERCERO
            WHERE  COMPANIA = UN_COMPANIA
              AND  NIT = MI_NITHEADER;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
        --No se encontró el tercero correspondiente al nit: --NIT--,Debe configurar el Nit de la entidad como tercero.
        MI_REEMPLAZOS(1).CLAVE := 'NIT';
        MI_REEMPLAZOS(1).VALOR := MI_NITHEADER;

        PCK_ERR_MSG.RAISE_WITH_MSG
                  ( UN_EXC_COD => SQLCODE
                  , UN_TABLAERROR => 'TERCERO'
                  , UN_ERROR_COD  => PCK_ERRORES.ERRR_PLANONITHEADER
                  , UN_REEMPLAZOS => MI_REEMPLAZOS
                  );

    END;
    
    MI_ETAPA := '01';

    MI_NUMREGISTRO := 1;
    MI_RNCONSECUTIVO := 1;
    MI_FECHAANT := TO_DATE('01/01/1900', 'DD/MM/YYYY');
    MI_INCONSISTENCIA := FALSE;
    MI_PINCONSISTENCIAS := TO_CLOB('Las siguientes inconsistencias se presentan en el archivo plano SIOT que se está tratando de subir a Sysman WEB:' || CHR(13) || CHR(10));

    MI_T_SPLIT := PCK_SYSMAN_UTL.FC_SPLIT_SYS
                    (UN_LISTA        => '' || UN_PLANO || ''
                    ,UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);

    MI_CUENTA := MI_T_SPLIT.COUNT;
    MI_ETAPA := '02';
    FOR i IN 1..MI_CUENTA LOOP
        MI_RNAUX := NVL(MI_T_SPLIT(i),' ');
        MI_RNAUX := REPLACE(REPLACE(MI_RNAUX ,CHR(13),''),CHR(10),'');


        IF NVL(MI_RNAUX,' ') <> ' ' AND LENGTH(TRIM(MI_RNAUX))>0 THEN
            MI_RNTIPO := TRIM(SUBSTR(MI_RNAUX, 1, 3));
            MI_RNCODIGO_CUENTA := TRIM(SUBSTR(MI_RNAUX, 14, 16));
            MI_RNFECHA := TO_DATE(SUBSTR(MI_RNAUX, 30, 10), 'DD/MM/YYYY');
            MI_ETAPA := '03';
            MI_MES := PCK_SYSMAN_UTL.FC_MES(MI_RNFECHA);            

            IF PCK_CONTABILIDAD4.FC_VERIFICAPERIODO(UN_COMPANIA => UN_COMPANIA
                                                   ,UN_ANO      => PCK_SYSMAN_UTL.FC_ANIO(MI_RNFECHA)
                                                   ,UN_MES      => PCK_SYSMAN_UTL.FC_MES(MI_RNFECHA)) = 0 THEN
                MI_ETAPA := '04';
                BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                    MI_REEMPLAZOS(1).CLAVE := 'ANO';
                    MI_REEMPLAZOS(1).VALOR := PCK_SYSMAN_UTL.FC_ANIO(MI_RNFECHA);
                    MI_REEMPLAZOS(2).CLAVE := 'MES';
                    MI_REEMPLAZOS(2).VALOR := PCK_SYSMAN_UTL.FC_MES(MI_RNFECHA);
                    PCK_ERR_MSG.RAISE_WITH_MSG
                              ( UN_EXC_COD => SQLCODE
                              , UN_TABLAERROR => 'ANO'
                              , UN_ERROR_COD => PCK_ERRORES.ERROR_GRAL_MESCERRADO
                              , UN_REEMPLAZOS => MI_REEMPLAZOS
                              );
                END;

            END IF;

            MI_RNNUMERO := TO_NUMBER(SUBSTR(MI_RNAUX, 4, 10));
            MI_RNANO := PCK_SYSMAN_UTL.FC_ANIO(MI_RNFECHA);
            
            MI_ETAPA := '05';
            
            MI_RNVALORDEBITO := NVL(TO_NUMBER(SUBSTR(MI_RNAUX, 40, 18)), 0);
            MI_POS := INSTR(SUBSTR(MI_RNAUX, 40, 18), '-');
            IF MI_POS > 0 THEN
                MI_RNVALORDEBITO := NVL(TO_NUMBER(SUBSTR(MI_RNAUX, 40 + MI_POS, 18 - MI_POS)), 0) * -1;
            END IF;
            
            MI_ETAPA := '06';
            MI_RNVALORCREDITO := NVL(TO_NUMBER(SUBSTR(MI_RNAUX, 58, 18)), 0);
            MI_POS := INSTR(SUBSTR(MI_RNAUX, 58, 18), '-');
            IF MI_POS > 0 THEN
                MI_RNVALORDEBITO := NVL(TO_NUMBER(SUBSTR(MI_RNAUX, 58 + MI_POS, 18 - MI_POS)), 0) * -1;
            END IF;

            MI_ETAPA := '07';
            MI_RNTERCERO := TRIM(SUBSTR(MI_RNAUX, 76, 11));
            IF MI_RNTERCERO = RPAD('9',11,'9') THEN --Si vienen 11 9 entonces colocar 20 9
                MI_RNTERCERO := PCK_DATOS.CONS_TERCERO;
            END IF;

            MI_ETAPA := '08';
            MI_RNSUCURSAL := TRIM(SUBSTR(MI_RNAUX, 87, 3));
            MI_RNCENTRO_COSTO := TRIM(SUBSTR(MI_RNAUX, 90, 10));

            IF MI_RNCENTRO_COSTO = RPAD('9',10,'9') THEN --Si vienen 10 9 entonces colocar 20 9
                MI_RNCENTRO_COSTO := PCK_DATOS.CONS_CENTRO;
            END IF;

            MI_ETAPA := '09';
            MI_RNAUXILIAR := TRIM(SUBSTR(MI_RNAUX, 100, 16));            
            MI_RNREFERENCIA:= TRIM(SUBSTR(MI_RNAUX, 226, 16));
            MI_RNFUENTERECURSO:= TRIM(SUBSTR(MI_RNAUX, 242, 16));
            MI_RNNRO_DOCUMENTO := TRIM(SUBSTR(MI_RNAUX, 116, 30)); 
            MI_RNDESCRIPCION := TRIM(SUBSTR(MI_RNAUX, 146, 64));
            MI_RNCUENTAPPTAL := TRIM(SUBSTR(MI_RNAUX, 210, 16));
              IF MI_RNFUENTERECURSO = '9999999999999999' THEN --Si vienen 16 9 entonces colocar 20 9
                MI_RNFUENTERECURSO := PCK_DATOS.CONS_AUXILIAR;
               END IF;
              IF MI_RNREFERENCIA = '9999999999999999' THEN --Si vienen 16 9 entonces colocar 20 9
                MI_RNREFERENCIA := PCK_DATOS.CONS_REFERENCIA;
               END IF;
            IF MI_RNAUXILIAR = '9999999999999999' THEN --Si vienen 16 9 entonces colocar 20 9
                MI_ETAPA := '10';
                MI_RNAUXILIAR := PCK_DATOS.CONS_AUXILIAR;
            ELSE
                BEGIN                     
                        SELECT DISTINCT 'X'
                        INTO   MI_EXISTEDATO
                        FROM   AUXILIAR
                        WHERE  COMPANIA = UN_COMPANIA
                          AND  ANO = MI_RNANO
                          AND  CODIGO  = MI_RNAUXILIAR;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_EXISTEDATO := ' ';
                END;

                MI_ETAPA := '11';
                IF NVL(MI_EXISTEDATO, ' ') = ' ' THEN
                    MI_AUX := 1;
                    WHILE MI_AUX < 16 LOOP
                        MI_RNAUXILIAR := TRIM(SUBSTR(MI_RNAUXILIAR, MI_AUX, 16));
                        MI_AUX := MI_AUX + 1;
                        BEGIN                            
                                SELECT DISTINCT 'X'
                                INTO   MI_EXISTEDATO
                                FROM   AUXILIAR
                                WHERE  COMPANIA = UN_COMPANIA
                                  AND  ANO = MI_RNANO
                                  AND  CODIGO  = MI_RNAUXILIAR;
                        EXCEPTION WHEN NO_DATA_FOUND THEN
                            MI_EXISTEDATO := ' ';
                        END;

                        IF NVL(MI_EXISTEDATO, ' ') = ' ' THEN
                           MI_RNAUXILIAR := TRIM(SUBSTR(MI_RNAUX, 100, 16));
                        ELSE
                            MI_AUX := 17;
                        END IF;
                    END LOOP;
                END IF;
            END IF;
            MI_ETAPA := '12';
            BEGIN
                SELECT CLASECUENTA, CUENTA_PPTAL
                INTO   MI_CLASE, MI_CUENTAPPTAL
                FROM   PLAN_CONTABLE
                WHERE  COMPANIA = UN_COMPANIA
                  AND  ANO = MI_RNANO
                  AND  CODIGO = MI_RNCODIGO_CUENTA;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_INCONSISTENCIA := TRUE;
                MI_PINCONSISTENCIAS :=MI_PINCONSISTENCIAS ||  TO_CLOB('El registro No. '|| MI_NUMREGISTRO ||' Presenta problemas con el codigo de cuenta '|| MI_RNCODIGO_CUENTA ||' No existe, Por favor revise configuración en la generación del archivo plano'  || CHR(13) || CHR(10));
            END;

            MI_ETAPA := '13';
            IF NVL(MI_RNTIPO,' ') = ' ' THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El registro No. '|| MI_NUMREGISTRO ||' NO tiene tipo de comprobante.' || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;
            MI_ETAPA := '14';
            IF MI_RNNUMERO = 0 THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El registro No. '|| MI_NUMREGISTRO ||' NO tiene número de comprobante.'  || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;
            IF NVL(MI_RNCODIGO_CUENTA, ' ') = ' ' THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El registro No. '|| MI_NUMREGISTRO ||' NO tiene código de cuenta contable.' || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;
            IF NVL(MI_RNTERCERO, ' ') = ' ' THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El registro No. '|| MI_NUMREGISTRO ||' NO tiene código de tercero.'  || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;
            IF NVL(MI_RNSUCURSAL, ' ') = ' ' THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El registro No. '|| MI_NUMREGISTRO ||' No tiene código de sucursal del tercero.'  || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;

            MI_ETAPA := '15';
            IF NVL(MI_RNCENTRO_COSTO, ' ') = ' ' THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El registro No. '|| MI_NUMREGISTRO ||' No tiene código de centro de costo.'  || CHR(13) || CHR(10));
                MI_RNCENTRO_COSTO := PCK_DATOS.CONS_CENTRO;
                MI_INCONSISTENCIA := FALSE;
            END IF;
            IF NVL(MI_RNAUXILIAR, ' ') = '' THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El registro No. '|| MI_NUMREGISTRO ||' No tiene código de auxiliar general.'  || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;
            IF NVL(MI_RNDESCRIPCION,' ') = ' ' THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El registro No. '|| MI_NUMREGISTRO ||' No tiene descripción.'  || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;        

            MI_ETAPA := '16';
            IF NVL(MI_RNCUENTAPPTAL, ' ') = ' ' THEN
                IF MI_CLASE = 'P' OR MI_CLASE = 'N' THEN
                    MI_RNCUENTAPPTAL := MI_CUENTAPPTAL;
                END IF;
            END IF;

            MI_ETAPA := '17';
            BEGIN
                MI_NATURALEZA := PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => MI_RNCODIGO_CUENTA);
                MI_TABLA := 'TEMP_PLANA_AJUSTES';

                MI_CAMPOS := 'COMPANIA ' ||
                             ',ANO ' ||
                             ',TIPO_CPTE ' ||
                             ',COMPROBANTE ' ||
                             ',CONSECUTIVO ' ||
                             ',CUENTA ' ||
                             ',NATURALEZA ' ||
                             ',FECHA ' ||
                             ',DESCRIPCION ' ||
                             ',VALOR_DEBITO ' ||
                             ',VALOR_CREDITO ' ||
                             ',NRO_DOCUMENTO ' ||
                             ',TERCERO ' ||
                             ',SUCURSAL ' ||
                             ',CENTRO_COSTO ' ||
                             ',AUXILIAR ' ||                              
                             ',FUENTE_RECURSOS ' ||   
                             ',REFERENCIA ';

                MI_VALORES := '  '''|| UN_COMPANIA ||'''
                                , '|| MI_RNANO ||'
                                , '''|| MI_RNTIPO ||'''
                                , '|| MI_RNNUMERO ||'
                                , '|| MI_RNCONSECUTIVO ||'
                                , '''|| MI_RNCODIGO_CUENTA ||'''
                                , '''|| MI_NATURALEZA ||'''
                                , TO_DATE(''' || TO_CHAR(MI_RNFECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                                ,'''||  MI_RNDESCRIPCION || '''
                                ,'|| MI_RNVALORDEBITO ||'
                                ,'|| MI_RNVALORCREDITO ||'
                                ,'''|| MI_RNNRO_DOCUMENTO ||'''
                                ,'''|| MI_RNTERCERO ||'''
                                ,'''|| MI_RNSUCURSAL ||'''
                                ,'''|| CASE WHEN MI_RNCENTRO_COSTO  IS NULL THEN '99999999999999999999' ELSE MI_RNCENTRO_COSTO END||'''                           
                                ,'''|| CASE WHEN MI_RNAUXILIAR IS NULL THEN '99999999999999999999' ELSE MI_RNAUXILIAR END ||'''
                                ,'''|| CASE WHEN MI_RNFUENTERECURSO IS NULL THEN '99999999999999999999'  ELSE MI_RNFUENTERECURSO END ||'''
                                ,'''|| CASE WHEN MI_RNREFERENCIA IS NULL THEN '99999999999999999999' ELSE  MI_RNREFERENCIA END ||'''';
                                
                IF NVL(MI_RNCUENTAPPTAL, ' ') <> ' ' THEN
                    MI_CAMPOS := MI_CAMPOS ||' ,CUENTAPPTAL
                                               ,EJECUCION_DEBITO
                                               ,EJECUCION_CREDITO ';

                    MI_VALORES := MI_VALORES || ' ,'''|| MI_RNCUENTAPPTAL ||'''
                                                  ,'|| MI_RNVALORDEBITO ||'
                                                  ,'|| MI_RNVALORCREDITO ||'  ';

                END IF;
                MI_ETAPA := '18';
                BEGIN
                    MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                          UN_ACCION  => 'I',
                                                          UN_CAMPOS  => MI_CAMPOS,
                                                          UN_VALORES => MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                --Se presentó error al insertar el tipo de comprobante: --TIPO-- ,Comprobante: --COMPROBANTE-- en plan ajustes.
                MI_REEMPLAZOS(1).CLAVE := 'TIPO';
                MI_REEMPLAZOS(1).VALOR := MI_RNTIPO;
                MI_REEMPLAZOS(2).CLAVE := 'COMPROBANTE';
                MI_REEMPLAZOS(2).VALOR := MI_RNNUMERO;
                MI_REEMPLAZOS(3).CLAVE := 'LINEA';
                MI_REEMPLAZOS(3).VALOR := MI_NUMREGISTRO;
                PCK_ERR_MSG.RAISE_WITH_MSG
                          ( UN_EXC_COD => SQLCODE
                          , UN_TABLAERROR => MI_TABLA
                          , UN_ERROR_COD => PCK_ERRORES.ERRR_PLANOINSPLANAAJUSTE
                          , UN_REEMPLAZOS => MI_REEMPLAZOS
                          );
            END;
            MI_ETAPA := '19';
            IF MI_FILAS > 0 THEN
                MI_RNCONSECUTIVO := MI_RNCONSECUTIVO + 1;
            END IF;

            MI_NUMREGISTRO := MI_NUMREGISTRO + 1;
        END IF;--Siguiente registro
        --<<SIGUE>>
    END LOOP;
    MI_ETAPA := '20';
    IF MI_INCONSISTENCIA THEN
        MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('Proceso de contabilizar no realizado' || CHR(13) || CHR(10));
        MI_RTA := MI_PINCONSISTENCIAS;
        RETURN MI_RTA;
    END IF;

    BEGIN
        MI_ETAPA := '21';
        MI_INCONSISTENCIA := FALSE;
        MI_PINCONSISTENCIAS := TO_CLOB('Las siguientes inconsistencias se presentan en el archivo plano SIOT que se está tratando de subir a Sysman WEB:' || CHR(13) || CHR(10));
        <<DATOSINCONSISTENTES>>
        FOR MI_RS IN
            (SELECT TP.COMPANIA,  TP.ANO,  TP.TIPO_CPTE,  TP.COMPROBANTE, TP.TERCERO ,  TP.CENTRO_COSTO , TP.AUXILIAR, TP.SUCURSAL
              , TC.CODIGO TIPOCOMPROBANTE
              , CC.NUMERO NUMCOMPRO
              , T.NIT     NIT
              , CCO.CODIGO CENTROCODIGO
              , AX.CODIGO AUXILIARCODIGO
              , REFE.CODIGO REFERENCIACODIGO
              , FUENTE.CODIGO FUENTECODIGO
            FROM TEMP_PLANA_AJUSTES TP
            LEFT JOIN TIPO_COMPROBANTE TC
                      ON TP.COMPANIA   = TC.COMPANIA
                      AND TP.TIPO_CPTE = TC.CODIGO
            LEFT JOIN COMPROBANTE_CNT CC
                      ON   TP.COMPANIA    = CC.COMPANIA
                      AND  TP.ANO         = CC.ANO
                      AND  TP.TIPO_CPTE   = CC.TIPO
                      AND  TP.COMPROBANTE = CC.NUMERO
            LEFT JOIN TERCERO T
                      ON  TP.COMPANIA = T.COMPANIA
                      AND TP.TERCERO  = T.NIT
                      AND TP.SUCURSAL = T.SUCURSAL
            LEFT JOIN CENTRO_COSTO CCO
                      ON  TP.COMPANIA     = CCO.COMPANIA
                      AND TP.ANO          = CCO.ANO
                      AND TP.CENTRO_COSTO = CCO.CODIGO
            LEFT JOIN AUXILIAR AX
                      ON  TP.COMPANIA = AX.COMPANIA
                      AND TP.ANO      = AX.ANO
                      AND TP.AUXILIAR = AX.CODIGO
            LEFT JOIN REFERENCIA REFE
                      ON  TP.COMPANIA   = REFE.COMPANIA
                      AND TP.ANO        = REFE.ANO
                      AND TP.REFERENCIA = REFE.CODIGO
            LEFT JOIN FUENTE_RECURSOS FUENTE
                      ON  TP.COMPANIA        = FUENTE.COMPANIA
                      AND TP.ANO             = FUENTE.ANO
                      AND TP.FUENTE_RECURSOS = FUENTE.CODIGO

            WHERE TC.CODIGO IS NULL
               OR CC.NUMERO = TP.COMPROBANTE
               OR T.NIT IS NULL
               OR CCO.CODIGO IS NULL
               OR AX.CODIGO IS NULL)
        LOOP
            MI_ETAPA := '22';
            IF MI_RS.TIPOCOMPROBANTE IS NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El tipo de comprobante '|| MI_RS.TIPO_CPTE || ' NO está creado en el sistema.' || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;

            IF MI_RS.NUMCOMPRO IS NOT NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El número de comprobante '|| MI_RS.COMPROBANTE || ' de tipo '|| MI_RS.TIPO_CPTE ||' YA existe en el sistema.' || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;

            IF MI_RS.NIT IS NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El tercero '|| MI_RS.TERCERO || ' sucursal '|| MI_RS.SUCURSAL ||' NO está creado en el sistema.' || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;

            IF MI_RS.CENTROCODIGO IS NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El centro de costo '|| MI_RS.CENTRO_COSTO || ' NO está creado en el sistema.' || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;
            MI_ETAPA := '23';
            IF MI_RS.AUXILIARCODIGO IS NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El auxiliar '|| MI_RS.AUXILIAR || ' NO está creado en el sistema.' || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;

            IF MI_RS.REFERENCIACODIGO IS NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('La referencia '|| MI_RS.REFERENCIACODIGO || ' NO está creada en el sistema.' || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;

            IF MI_RS.FUENTECODIGO IS NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('La fuente de Recursos '|| MI_RS.FUENTECODIGO || ' NO está creada en el sistema.' || CHR(13) || CHR(10));
                MI_INCONSISTENCIA := TRUE;
            END IF;
        END LOOP DATOSINCONSISTENTES;
    END;

    IF MI_INCONSISTENCIA THEN
        MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('Proceso de contabilizar no realizado' || CHR(13) || CHR(10));
        MI_RTA := MI_PINCONSISTENCIAS;
        RETURN MI_RTA;
    ELSE
        MI_RTA := '';
        MI_PINCONSISTENCIAS := TO_CLOB('Se crearon los siguientes comprobantes:' || CHR(13) || CHR(10));
        <<CONTABILIZAR>>
        FOR MI_RS IN
            (
                SELECT COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,
                       MAX(NRO_DOCUMENTO) NRO_DOCUMENTO,
                       MAX(FECHA) AS FEC,
                       MAX(TERCERO) AS TERDETALLE,
                       MAX(SUCURSAL)  AS SUC,
                       MAX(DESCRIPCION) AS DESCR,
                       MAX(TEXTOD) AS TEXTOD
                FROM TEMP_PLANA_AJUSTES
                WHERE COMPANIA = UN_COMPANIA
                GROUP BY COMPANIA,ANO,TIPO_CPTE,COMPROBANTE
            )
        LOOP
            MI_ETAPA := '24';         
            MI_NITCONTA := MI_RS.TERDETALLE;
            MI_SUCURSALCONTA := MI_RS.SUC;
            
            SELECT COUNT(DISTINCT TERCERO) NUM 
              INTO MI_NUMTERCEROS
              FROM TEMP_PLANA_AJUSTES 
             WHERE COMPANIA = MI_RS.COMPANIA
               AND ANO = MI_RS.ANO
               AND TIPO_CPTE = MI_RS.TIPO_CPTE
               AND COMPROBANTE = MI_RS.COMPROBANTE;
                    
            IF MI_NUMTERCEROS > 1 THEN
                MI_NITCONTA := MI_NITHEADER;
                MI_SUCURSALCONTA := MI_SUCURSALHEADER;
            ELSE
                MI_NITCONTA := MI_RS.TERDETALLE;
                MI_SUCURSALCONTA := MI_RS.SUC;
            END IF;
            
            MI_ETAPA := '25';
            MI_PINCONSISTENCIAS := TO_CLOB(PCK_CONTABILIZAR.FC_CONTABILIZAR
                        (UN_COMPANIA         => UN_COMPANIA
                        ,UN_TIPOCOMPROBANTE  => MI_RS.TIPO_CPTE
                        ,UN_NUMERO           => MI_RS.COMPROBANTE
                        ,UN_ANO              => MI_RS.ANO
                        ,UN_FECHA            => MI_RS.FEC
                        ,UN_TERCERO          => MI_NITCONTA
                        ,UN_SUCURSAL         => MI_SUCURSALCONTA
                        ,UN_DESCRIPCION      => MI_RS.DESCR
                        ,UN_SIMPLE           => 0 
                        ,UN_CONCILIAR        => UN_CONCILIAR
                        ,UN_INDIMPRESION     => 0
                        ,UN_INTOMITIRPPTAL   => 0
                        ,UN_NRO_DOCUMENTO    => MI_RS.NRO_DOCUMENTO
                        ,UN_USUARIO          => UN_USUARIO
                        ,UN_TEXTO            => MI_RS.TEXTOD));
                        MI_ETAPA := '26';
            MI_RTA := MI_RTA || MI_PINCONSISTENCIAS || TO_CLOB(CHR(13) || CHR(10));
        END LOOP CONTABILIZAR;

    END IF;

    MI_RTA := MI_RTA || CHR(13) || CHR(10) || 'Proceso terminado Exitosamente';
    EXECUTE IMMEDIATE 'DELETE TEMP_PLANA_AJUSTES WHERE 1 = 1'; 
    --BORRADO A LA FUERZA PARA QUE PERMITA CARGAR VARIOS COMPROBANTES SEGUIDOS 
    RETURN MI_RTA;
EXCEPTION WHEN OTHERS THEN
    --Se presentó error en la subida del archivo plano, en la línea del plano número: --PLANOLINEA-- , En la etapa: --ETAPA--.
    MI_REEMPLAZOS(1).CLAVE := 'PLANOLINEA';
    MI_REEMPLAZOS(1).VALOR := MI_NUMREGISTRO;
    MI_REEMPLAZOS(2).CLAVE := 'ETAPA';
    MI_REEMPLAZOS(2).VALOR := MI_ETAPA|| ' ERROR '||SQLERRM || CHR(13) || CHR(10) || NVL(MI_RNAUX,' ');
    PCK_ERR_MSG.RAISE_WITH_MSG
              ( UN_EXC_COD => -20000
              , UN_TABLAERROR => 'ANO'
              , UN_ERROR_COD => PCK_ERRORES.ERRR_SUBIRPLANOCONTABILIZA
              , UN_REEMPLAZOS => MI_REEMPLAZOS
              );
END FC_CONTABILIZARPLANOSIOT;

END PCK_CONTABILIZAR;