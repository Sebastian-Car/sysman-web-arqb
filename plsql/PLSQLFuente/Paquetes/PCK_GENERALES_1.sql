create or replace PACKAGE BODY "PCK_GENERALES" AS
/**@package:  Generales **/

PROCEDURE PR_CALCULARPAGG
 /* 
    NAME              : PR_CALCULARPAGG   - En acces: CalcularPagg
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÍGUEZ
    DATE MIGRADOR     : 17/09/2015
    TIME              : 12:00 
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : 
    @NAME:  calcularPaag
    @METHOD:  PUT     
  */
  (
    UN_COMPANIA IN  VARCHAR2,
    UN_ANO      IN  VARCHAR2
  )

  AS  
    MI_ERROR_FUN  NUMBER:=    GL_ERROR_NUM + 1; 
    MI_STRSQL                 VARCHAR2(3000);   
    MI_CAMPOS                 VARCHAR2(1000);
    MI_VALORES                VARCHAR2(1000);
    MI_CONDICION              VARCHAR2(3000);
    RS                        SYS_REFCURSOR;

    TYPE PAGOS_TYPE IS TABLE OF NUMBER;
    V_PAG                     PAGOS_TYPE;
    V_PAG1                    PAGOS_TYPE;
    V_PAAG_ACUMULADO          PAGOS_TYPE;

    RS_PAAG_MENSUAL           MES.PAAG_MENSUAL%TYPE; 
    RS_PAAG_ACUMULADO         MES.PAAG_ACUMULADO%TYPE; 
    RS_PAAG_ANUAL             MES.PAAG_ANUAL%TYPE; 
    RS_NUMERO                 MES.NUMERO%TYPE;


BEGIN
V_PAG:=PAGOS_TYPE();
V_PAG1:=PAGOS_TYPE();
V_PAAG_ACUMULADO:=PAGOS_TYPE();

MI_STRSQL:= 'SELECT  NUMERO,
                     PAAG_MENSUAL,
                     PAAG_ACUMULADO,
                     PAAG_ANUAL 
               FROM  MES 
               WHERE COMPANIA    = ''' || UN_COMPANIA || ''' 
               AND   ANO           = '  || UN_ANO ||  ' 
               AND   NUMERO <> 0 ';


OPEN RS FOR MI_STRSQL;
   LOOP
      FETCH RS INTO RS_NUMERO,RS_PAAG_MENSUAL,RS_PAAG_ACUMULADO,RS_PAAG_ANUAL;
       EXIT WHEN RS%NOTFOUND;
       V_PAG.EXTEND();
       V_PAG(RS_NUMERO):=RS_PAAG_MENSUAL;

       FOR I IN 1..(RS_NUMERO-1)
         LOOP
         V_PAG.EXTEND();
         V_PAG(I):= V_PAG1(I) * (1 + V_PAG(RS_NUMERO) / 100) + V_PAG(RS_NUMERO);
       END LOOP; 
       V_PAAG_ACUMULADO.EXTEND();
       V_PAAG_ACUMULADO(RS_NUMERO):= PCK_SYSMAN_UTL.FC_ROUND(V_PAG(1),2);

       FOR I IN 1..RS_NUMERO
         LOOP
         V_PAG1.EXTEND();
         V_PAG1(I):= PCK_SYSMAN_UTL.FC_ROUND(V_PAG(I),2);
       END LOOP; 
   END LOOP;
   CLOSE RS; 

 OPEN RS FOR MI_STRSQL;
   LOOP
      FETCH RS INTO RS_NUMERO,RS_PAAG_MENSUAL,RS_PAAG_ACUMULADO,RS_PAAG_ANUAL;
       EXIT WHEN RS%NOTFOUND; 
         MI_VALORES:='PAAG_ACUMULADO='|| V_PAAG_ACUMULADO(RS_NUMERO) ||', PAAG_ANUAL= '|| V_PAG(RS_NUMERO) ||' ';
         MI_CONDICION:='COMPANIA ='''|| UN_COMPANIA || ''' AND ANO =' || UN_ANO || ' AND NUMERO = '|| RS_NUMERO ||' ';
         PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME('MES','M',MI_VALORES,NULL,NULL,MI_CONDICION);
   END LOOP;
   CLOSE RS;
   EXCEPTION WHEN OTHERS THEN
    PCK_DATOS.GL_ERROR_MSG:= 'Error al calcular pagos ';
    PCK_DATOS.GL_ERROR_MSG:= PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  '','',SQLERRM );
    RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );

END PR_CALCULARPAGG; 

--2
FUNCTION FC_NOMBREINVENTARIO
/*
    NAME              : FC_NOMBREINVENTARIO 
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : LIZETH CASTRO ORDOÑEZ
    DATE MIGRADOR     : 19/10/2015
    TIME              : 08:50 AM
    SOURCE MODULE     : SysmanAl2015.10.01 - copia.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Retorna el  Nombre largo del un elemento de Inventario 
    @NAME:  consultarNombreElemento
    @METHOD:  GET      
  */
(
 UN_CODELEMENTO  IN VARCHAR2, 
 UN_COMPANIA     IN VARCHAR2,
 UN_OPCION	     IN NUMBER 
)

RETURN VARCHAR2
AS 
  MI_ERROR_FUN        NUMBER:=    GL_ERROR_NUM + 2; 
  MI_STRSQL           VARCHAR2(3000);
  MI_RS               SYS_REFCURSOR;
  MI_NOMBREINVENTARIO VARCHAR2(3000);

  MI_NOMBRELARGO      INVENTARIO.NOMBRELARGO%TYPE;
  MI_UNIDAD           INVENTARIO.UNIDAD%TYPE;

BEGIN 
  BEGIN 
    SELECT  NVL(NOMBRELARGO,' '), 
            NVL(UNIDAD,' ') 
    INTO  MI_NOMBRELARGO,
          MI_UNIDAD
    FROM  INVENTARIO 
    WHERE  COMPANIA = UN_COMPANIA  
      AND  CODIGOELEMENTO = UN_CODELEMENTO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_NOMBRELARGO:= ' ';
        MI_UNIDAD:= ' ';
  END;
  IF UN_OPCION IS NOT NULL THEN
    IF  UN_OPCION = 1 THEN
          MI_NOMBREINVENTARIO:= MI_UNIDAD;
    ELSE
          MI_NOMBREINVENTARIO:= ' ';
    END IF;
  ELSE
       MI_NOMBREINVENTARIO:= MI_NOMBRELARGO;   
  END IF;

    RETURN MI_NOMBREINVENTARIO;
    EXCEPTION WHEN OTHERS THEN
    PCK_DATOS.GL_ERROR_MSG:= 'Error al calcular pagos ';
    PCK_DATOS.GL_ERROR_MSG:= PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  '','',SQLERRM );
    RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );
END FC_NOMBREINVENTARIO;

FUNCTION FC_VERIFICAR_ESTADOVIGENCIA
/*
    NAME              : VERIFICAR_ESTADOVIGENCIA 
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : SANDRA MILENA DAZA LEGUIZAMÓN
    DATE MIGRADOR     : 22/10/2015
    TIME              : 10:05 AM
    SOURCE MODULE     : sysmanTT2015.07.02
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Retorna el estado de una vigencias. Se debe tener en cuenta el parámetro de entrada que permiste
                        consultar el estado contable (CT), presupuestal (PR), de almácen (AL) o de conciliación (CC)
    @NAME:  consultarEstadoDeVigencia
    @METHOD:  GET                              
*/
  (
    UN_COMPANIA  IN VARCHAR2, 
    UN_ANIO      IN NUMBER,
    UN_PARAMETRO IN VARCHAR2
  )
RETURN VARCHAR2
AS
  MI_ESTADO     VARCHAR2(1) := 'I';
  MI_ERROR_FUN  NUMBER      :=    GL_ERROR_NUM + 3; 
  MI_MODULO     PCK_SUBTIPOS.TI_MODULO;
  MI_PROCESO    PCK_SUBTIPOS.TI_ENTERO;
BEGIN
    IF UN_PARAMETRO = 'CT' THEN
      MI_MODULO  :=1;
      MI_PROCESO :=1;      
    ELSIF UN_PARAMETRO = 'PR' THEN
      MI_MODULO  :=3;
      MI_PROCESO :=1;            
    ELSIF UN_PARAMETRO = 'AL' THEN
      MI_MODULO  :=10;
      MI_PROCESO :=1;            
    ELSIF UN_PARAMETRO = 'CC' THEN
      MI_MODULO  :=1;
      MI_PROCESO :=2;      
    END IF;
    MI_ESTADO := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADOANO(UN_COMPANIA=> UN_COMPANIA, 
                                                       UN_ANO     => UN_ANIO, 
                                                       UN_MODULO  => MI_MODULO, 
                                                       UN_PROCESO => MI_PROCESO);
  RETURN MI_ESTADO;
  EXCEPTION WHEN OTHERS THEN
    PCK_DATOS.GL_ERROR_MSG:= 'No se estable correctamente el estado de la vigencia  ' || UN_ANIO;
    PCK_DATOS.GL_ERROR_MSG:= PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'ANO','',SQLERRM );
    RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );
END FC_VERIFICAR_ESTADOVIGENCIA; 

FUNCTION FC_ACTRES
/*
   NAME             : FC_ACTRES
   AUTHORS          : SYSMAN SAS
   AUTHOR MIGRACION : JUAN CARLOS RODRÍGUEZ AMÉZQUITA
   DATE MIGRADOR    : 27/10/2015
   TIME             : 09:12 AM
   MODULO ORIGEN    : sysmanTT2015.07.02
   DESCRIPTION      : Actualiza cualquier campo en la tabla de inventario.
   MODIFIER         : 
   DATE MODIFIED    : 
   TIME             : 
   MODIFICATIONS    :
   @NAME:  actualizarCampoEnInventario
   @METHOD:  GET    
*/
(
  UN_COMPANIA     IN    VARCHAR2, -- Código de la compañía
  UN_ELEMENTO     IN    VARCHAR2, -- Código del elmento
  UN_CANTIDAD     IN    NUMBER,   -- Cantidad a aumentar
  UN_CAMPO        IN    VARCHAR2  -- Nombre del campo a actualizar
)
RETURN NUMBER
AS
  MI_ERROR_FUN        NUMBER := GL_ERROR_NUM + 4;
  MI_RTA              NUMBER := 0;
  MI_VALORES          VARCHAR2(32000);
  MI_CONDICION        VARCHAR2(32000);
BEGIN
  MI_VALORES := UN_CAMPO || ' = ' || UN_CAMPO || ' + ' || UN_CANTIDAD;
  MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || ''' AND CODIGOELEMENTO = ''' || UN_ELEMENTO || '''';
  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME('INVENTARIO', 'M', MI_VALORES, NULL, NULL, MI_CONDICION);
  MI_RTA := 1;
  --
  RETURN MI_RTA;
EXCEPTION 
  WHEN OTHERS THEN
  PCK_DATOS.GL_ERROR_MSG := 'Interrupción al actualizar el campo ' || UN_CAMPO || ' en el inventario.';
  PCK_DATOS.GL_ERROR_MSG := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'ACTRES', '', SQLERRM );
  RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );
END FC_ACTRES;

FUNCTION FC_SALDOPPTAL 
/*
    NAME              : ACUM
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 08/01/2016
    TIME              : 12:50 PM
    SOURCE MODULE     : PRESUPUESTO
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : TRAE 
    'Apropiado es la apropiación inicial asignada
    'Adiciones son las adiciones hechas a ese rubro
    'Reducciones son las reducciones hechas a ese rubro
    'ApropiacionVigente es apropiacion acumulada
    'Aplazamientos son aplazamientos
    'SaldoApropiacion es el saldo de apropiación a un mes
    'Traslados son Traslados
    'TotalIngresos es Total Recaudado por Ingresos Causados, Efectivo y Papeles (con sus modificaciones)
    'CompromisosAcum es compromisos acumulados
    'ObligacionesAcum son obligaciones acumuladas
    'PagosAcum son pagos acumulados
    'PACAcum es PAC acumulado (PAC+ ModifPAC)
    'DisponibilidadAcum son disponibilidades acumuladas
    'IngresosCausadosAcum son Ingresos causados acumulados (Derechos causados)
    'ModifIngresosCausadosAcum son modificaciones ingresos causados (Derechos anulados)
    'IngresosEfectivoAcum son ingresos en efectivo acumulados
    'IngresosPapelesAcum son ingresos en papeles acumulados
    'ModifIngresosEfectivoAcum son modificaciones a ingresos en efectivo acumulados (Devoluciones)
    'ModifIngresosPapelesAcum son modificaciones a ingresos en papeles acumulados (Otras cancelaciones)                    
    @NAME:  consultarEjecucionPresupuestal
    @METHOD:  GET     
  */
  (
    UN_COMPANIA IN  VARCHAR2,
    UN_COLUMNA  IN  VARCHAR2,
    UN_ANIO     IN  NUMBER  ,
    UN_ID       IN  VARCHAR2,
    UN_MES      IN  NUMBER    
  )

RETURN NUMBER AS 
  MI_STRSQL VARCHAR2(4000);
  MI_VALOR NUMBER;
  MI_ERROR_FUN        NUMBER := GL_ERROR_NUM + 5;
BEGIN
  MI_STRSQL :='
  SELECT COMPANIA, 
       ANO, 
       ID,
       CODIGO, 
       NATURALEZA,
       CENTRO_COSTO,
       TERCERO,
       SUCURSAL,
       AUXILIAR,
       REFERENCIA,
       FUENTE_RECURSO,
       MOVIMIENTO,
       SUM(APROPIADO               ) APROPIADO,
       SUM(ADICION                 ) ADICION,
       SUM(REDUCCION               ) REDUCCION,
       SUM(CREDITO                 ) CREDITO,
       SUM(CONTRACREDITO           ) CONTRACREDITO,
       SUM(ADICIONES               ) ADICIONES,       
       SUM(REDUCCIONES             ) REDUCCIONES,
       SUM(APROPIACIONVIGENTE      ) APROPIACIONVIGENTE,
       SUM(APRDEFINITIVA           ) SALDOAPROPIACION, 
       SUM(APLAZAMIENTO            ) APLAZAMIENTOS, 
       SUM(TRASLADO                ) TRASLADOS,
       SUM(REG_CONTRACT            ) SUMAREGCONTRACT, 
       SUM(REG_NO_CONTRACT         ) SUMAREGNOCONTRACT,
       SUM(REG_REVERSION           ) SUMAREGREVERSION,
       SUM(MODIF_REG_CONT          ) SUMAMODIFREGCONT,
       SUM(MODIF_REG_NOCONT        ) SUMAMODIFREGNOCONT,       
       SUM(TOTALREGCONT            ) TOTALREGCONT ,
       SUM(TOTALREGNOCONTRACT      ) TOTALREGNOCONTRACT,
       SUM(COMPROMISOSACUM         ) COMPROMISOSACUM,
       SUM(REO                     ) SUMAREGOBLI,
       SUM(MODIFREO                ) SUMAMODIFREGOBLI,
       SUM(OBLIGACIONESACUM        ) OBLIGACIONESACUM,
       SUM(DISPONIBILIDAD          ) DISPONIBILIDADACUM, 
       SUM(EJECUCIONPPT            ) PAGOSACUM,
       SUM(INGRESOSCAUSADOS        ) INGRESOSCAUSADOSACUM,
       SUM(MODIFICACIONICA         ) MODIFINGRESOSCAUSADOSACUM,
       SUM(INGRESOS_EFECTIVO       ) INGRESOS_EFECTIVOACUM,
       SUM(INGRESOS_PAPELES        ) INGRESOS_PAPELESACUM,
       SUM(MODIF_INGRESOS_EFECTIVO ) MODIFINGRESOSEFECTIVOACUM,
       SUM(MODIF_INGRESOS_PAPELES  ) MODIFINGRESOSPAPELESACUM,
       SUM(TOTALINGRESOSCAUEFE     ) TOTALINGRESOS, 
       SUM(PAC_APROPIADO           ) PAC,
       SUM(PACTESORERIA            ) TOTALPACTESORERIA,
       SUM(MODIFPAC                ) MODIFPAC,
       SUM(PACTOTAL                ) PACACUM,
       SUM(PAC_PROGRAMADO          ) PACPROGRAMADO,    
       SUM(SALDOPACTESORERIA       ) SALDOPACTESORERIA
 FROM V_RESUMENPPTO_BASE
 WHERE COMPANIA =  ''' || UN_COMPANIA  || '''
   AND ANO      =  '   || UN_ANIO      ||
'  AND MES      <= '   || UN_MES       ||
'  AND ID       =  ''' || UN_ID        || '''
 GROUP BY COMPANIA, 
		   ANO, 
		   ID, 
		   CODIGO,
		   NATURALEZA,
		   CENTRO_COSTO,
		   TERCERO,
		   SUCURSAL,
		   AUXILIAR,
		   REFERENCIA,  
		   FUENTE_RECURSO,
		   MOVIMIENTO';
  BEGIN
    EXECUTE IMMEDIATE 'SELECT ' || UN_COLUMNA || ' FROM (' || MI_STRSQL || ') ' INTO MI_VALOR;
    RETURN MI_VALOR;  
    EXCEPTION WHEN NO_DATA_FOUND THEN
      RETURN 0;
  END;
EXCEPTION WHEN OTHERS THEN
    PCK_DATOS.GL_ERROR_MSG := 'Falló Consulta a saldo de. Año ' || UN_ANIO || ', mes ' || UN_MES || ', rubro ' || UN_ID;
    PCK_DATOS.GL_ERROR_MSG := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'CALCULO','',SQLERRM );
    RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );   

END FC_SALDOPPTAL;

FUNCTION FC_DISPARANOVEDAD
    /*
        NAME              : FC_DISPARANOVEDAD(En Access, DisparaNovedad dentro del Módulo "Contratación")
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : Diego Maldonado
        DATE MIGRADOR     : 22/07/2016
        TIME              : 10:30 AM
        SOURCE MODULE     : SysmanPR2016.03.02.accdb
        MODIFIER          : 
        DATE MODIFIED     : 
        TIME              : 
        DESCRIPTION       : Permite ejecutar la liberación del comprobante presupuestal.
        @NAME:  afectarNovedadEnContratacion
        @METHOD:  GET
        */
    (

        UN_COMPANIA          IN PCK_SUBTIPOS.TI_COMPANIA,
        UN_SMODULO           IN VARCHAR2,
        UN_STIPONOVEDAD      IN VARCHAR2,
        UN_LANO              IN NUMBER,
        UN_DNUMERO           IN NUMBER,
        UN_DFECHAINICIAL     IN VARCHAR2, 
        UN_DFECHAFINAL       IN VARCHAR2, 
        UN_DFECHAVENCIMIENTO IN VARCHAR2, 
        UN_DVALOR            IN NUMBER,
        UN_STIPOCONTRATO     IN VARCHAR2,
        UN_DNUMEROCONTRATO   IN NUMBER,
        UN_USUARIO           IN PCK_SUBTIPOS.TI_USUARIO)
    RETURN NUMBER
    AS
        MI_ERROR_FUN      NUMBER := GL_ERROR_NUM + 1;
        MI_DISPARANOVEDAD NUMBER;
        MI_NOVEDAD        NUMBER;
        MI_STRSQL         PCK_SUBTIPOS.TI_STRSQL;
        MI_CONTEO         NUMBER;
        MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
        MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
        MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
    BEGIN
        MI_STRSQL := 'SELECT COUNT(''X'') ' ||
                    ' FROM NOVEDADCONTRATO ' || 
                    ' WHERE NOVEDADCONTRATO.COMPANIA        =''' || UN_COMPANIA || ''' 
                          AND NOVEDADCONTRATO.CLASEORDEN    =''' || UN_STIPOCONTRATO || ''' 
                          AND NOVEDADCONTRATO.ORDENDECOMPRA =' || UN_DNUMEROCONTRATO || ' 
                          AND NOVEDADCONTRATO.CLASET        =''' || UN_SMODULO || ''' 
                          AND NOVEDADCONTRATO.TIPOT         =''' || UN_STIPONOVEDAD || ''' 
                          AND NOVEDADCONTRATO.NUMERO        =' || UN_DNUMERO || ' ';
        EXECUTE IMMEDIATE MI_STRSQL INTO MI_CONTEO;

        IF MI_CONTEO > 0 THEN
            MI_CAMPOS := 'NOVEDADCONTRATO.FECHA             = TO_DATE(''' || UN_DFECHAINICIAL || ''', ''DD/MM/YYYY''), 
                          NOVEDADCONTRATO.FECHAINICIAL      = TO_DATE(''' || UN_DFECHAINICIAL || ''',''DD/MM/YYYY''), 
                          NOVEDADCONTRATO.FECHAFINAL        = TO_DATE(''' || UN_DFECHAFINAL || ''',''DD/MM/YYYY''),  
                          NOVEDADCONTRATO.FECHAVENCIMIENTO  = TO_DATE(''' || UN_DFECHAVENCIMIENTO || ''',''DD/MM/YYYY''),  
                          NOVEDADCONTRATO.ANO               = ' || UN_LANO || ', 
                          NOVEDADCONTRATO.VALORTOTAL        = ' || UN_DVALOR ||' ,
                          NOVEDADCONTRATO.MODIFIED_BY       = ''' || UN_USUARIO || ''',
                          NOVEDADCONTRATO.DATE_MODIFIED     = SYSDATE
                          ';

            MI_CONDICION := 'NOVEDADCONTRATO.COMPANIA           =''' || UN_COMPANIA || ''' 
                              AND NOVEDADCONTRATO.CLASEORDEN    =''' || UN_STIPOCONTRATO || ''' 
                              AND NOVEDADCONTRATO.ORDENDECOMPRA =' || UN_DNUMEROCONTRATO || ' 
                              AND NOVEDADCONTRATO.CLASET        =''' || UN_SMODULO || ''' 
                              AND NOVEDADCONTRATO.TIPOT         =''' || UN_STIPONOVEDAD || ''' 
                              AND NOVEDADCONTRATO.NUMERO        =' || UN_DNUMERO;

            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => 'NOVEDADCONTRATO',
                                                  UN_ACCION     => 'M',
                                                  UN_CAMPOS     => MI_CAMPOS,
                                                  UN_CONDICION  => MI_CONDICION
                                                  );
            RETURN 0;
        ELSE
            MI_NOVEDAD := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO('NOVEDADCONTRATO', 
                                                           'COMPANIA          = ''' || UN_COMPANIA || ''' 
                                                           AND CLASEORDEN     = ''' || UN_STIPOCONTRATO || ''' 
                                                           AND ORDENDECOMPRA  =' || UN_DNUMEROCONTRATO, 
                                                           'NOVEDAD');

            MI_CAMPOS := 'COMPANIA, CLASEORDEN, ORDENDECOMPRA, NOVEDAD, CLASET, TIPOT, FECHA, 
                          FECHAINICIAL, FECHAFINAL, FECHAVENCIMIENTO, ANO, NUMERO, VALORTOTAL,
                          CREATED_BY, DATE_CREATED
                          ';

            MI_VALORES := '''' || UN_COMPANIA || ''',
                          ''' || UN_STIPOCONTRATO || ''',
                          ' || UN_DNUMEROCONTRATO || ',
                          ' || MI_NOVEDAD|| ',
                          ''' || UN_SMODULO || ''',
                          ''' || UN_STIPONOVEDAD || ''',
                          TO_DATE(''' || UN_DFECHAINICIAL || ''', ''DD/MM/YYYY''),
                          TO_DATE(''' || UN_DFECHAINICIAL || ''', ''DD/MM/YYYY''),
                          TO_DATE(''' || UN_DFECHAFINAL || ''',''DD/MM/YYYY''), 
                          TO_DATE(''' || UN_DFECHAVENCIMIENTO || ''',''DD/MM/YYYY''),  
                          ' || UN_LANO || ',' || UN_DNUMERO || ',' || UN_DVALOR || ',
                          '''|| UN_USUARIO ||''', SYSDATE
                          ';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => 'NOVEDADCONTRATO',
                                                  UN_ACCION   => 'I',
                                                  UN_CAMPOS   => MI_CAMPOS,
                                                  UN_VALORES  => MI_VALORES
                                                  );
            IF PCK_DATOS.GL_RTA > 0 THEN
                MI_DISPARANOVEDAD := -1;
            ELSE
                MI_DISPARANOVEDAD := 0;
            END IF;
        END IF;
        RETURN MI_DISPARANOVEDAD;
        EXCEPTION WHEN OTHERS THEN
            PCK_DATOS.GL_ERROR_MSG := 'Interrupción al disparar la novedad.';
            PCK_DATOS.GL_ERROR_MSG := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, 
                                                                      PCK_DATOS.GL_ERROR_MSG,  
                                                                      'PRESUPUESTO2',
                                                                      '',
                                                                      SQLERRM );
            RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );
    END FC_DISPARANOVEDAD;

--7
FUNCTION FC_NOMBRECOMPANIA
/*
    NAME              : FC_NOMBRECOMPANIA --> ACCESS strNombreCompania
    AUTHORS           : SANDRA MILENA DAZA LEGUIZAMÓN
    DATE              : 26/10/2016
    TIME              : 05:05 PM
    MODIFIER          : SANDRA MILENA DAZA LEGUIZAMÓN
    DATE MODIFIED     : 04/11/2016
    TIME              : 05:40 PM
    DESCRIPTION       :  Función que consulta y retorna el nombre de la compania
                         ingresada por parámetro
    @name             : retornarNombreCompania
    @method           : GET
    @parameters       : UN_COMPANIA, entidad que se consultada                         
  */
  (
    UN_COMPANIA  IN PCK_SUBTIPOS.TI_COMPANIA
  )
RETURN VARCHAR2
AS
  MI_NOMBRECOMPANIA VARCHAR2(32000);
BEGIN 
  BEGIN
    SELECT NOMBRE
    INTO   MI_NOMBRECOMPANIA
    FROM   COMPANIA
    WHERE  CODIGO = UN_COMPANIA;

    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_NOMBRECOMPANIA := 'SIN DEFINIR';
  END;
  RETURN MI_NOMBRECOMPANIA;
END FC_NOMBRECOMPANIA; 

FUNCTION FC_ACT_DATOSERROR_GENERAL 
  /*
  NAME                  : FC_ACT_DATOSERROR_GENERAL
  AUTHORS               : SYSMAN SAS
  AUTHOR MIGRACION      : JUAN CAMILO RODRIGUEZ DIAZ
  DATE MIGRADOR         : 14/02/2017
  TIME                  : 10:00 AM
  MODULO ORIGEN         : PREDIAL
  DESCRIPTION           : El procedimiento actualiza los  datos nulos que generan conflicto en los procesos, caracteres especiales no permitidos, y formatos de datos que se encuentren en conflicto en la Base de Datos de Impuesto Predial.
  MODIFIER              : AURA LILIANA MONROY GARCIA
  DATE MODIFIED         : 19/07/2017
  TIME                  :
  MODIFICATIONS         : Se adiciona el parametro UN_USUARIO para enviar la informacion de auditoria al realizar la actualizacion
  PARAMETROS DE ENTRADA : UN_TABLA    => Tabla de la actualización
                          UN_CAMPO    => Campo de la tabla afectado
                          UN_USUARIO  => Usuario que accede al sistema 
  @NAME: actDatosErrorGeneral
  @METHOD:put
  */
  (
  UN_TABLA   IN PCK_SUBTIPOS.TI_PARAMETRO,
  UN_CAMPO   IN PCK_SUBTIPOS.TI_CAMPOS,
  UN_USUARIO IN PCK_SUBTIPOS.TI_USUARIO
  )
RETURN NUMBER AS
  MI_RPTA        PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_REEMPLAZOS  PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_CAMPO       PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION   PCK_SUBTIPOS.TI_CONDICION;
BEGIN
    BEGIN
        IF UN_TABLA IS NULL OR UN_CAMPO IS NULL THEN  
           MI_RPTA:=0;
           RAISE  PCK_EXCEPCIONES.EXC_GENERAL;       
        END IF;
         EXCEPTION
             WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN 

                  MI_REEMPLAZOS (1).CLAVE := 'CAMPO';
                  MI_REEMPLAZOS (1).VALOR :=  UN_CAMPO;
                  MI_REEMPLAZOS (2).CLAVE := 'TABLA';
                  MI_REEMPLAZOS (2).VALOR :=  UN_TABLA;

                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,                                
                                             UN_ERROR_COD   => PCK_ERRORES.ERRR_GRAL_NULL_PARTABLACAMPO,                                   
                                             UN_REEMPLAZOS  => MI_REEMPLAZOS
                                            );      
    END;
    BEGIN

        MI_CAMPO    := UN_TABLA || '.' || UN_CAMPO||' = REPLACE(' || UN_TABLA || '.' || UN_CAMPO || ','||'chr(39)'||','||''' '''||'), ' ||
                       ' MODIFIED_BY      = ''' || UN_USUARIO ||''', '||
                       ' DATE_MODIFIED    = SYSDATE';
        MI_CONDICION:='INSTR(' || UN_TABLA || '.' || UN_CAMPO || ',chr(39))>0';     
        BEGIN
             MI_RPTA:=PCK_DATOS.FC_ACME(UN_TABLA     => UN_TABLA,
                                        UN_ACCION    => 'M',
                                        UN_CAMPOS    => MI_CAMPO,
                                        UN_CONDICION => MI_CONDICION
                                        );
             EXCEPTION
                  WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                 RAISE  PCK_EXCEPCIONES.EXC_GENERAL;
        END;
        EXCEPTION
             WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN 

                  MI_REEMPLAZOS (1).CLAVE := 'CAMPO';
                  MI_REEMPLAZOS (1).VALOR :=  UN_CAMPO;
                  MI_REEMPLAZOS (2).CLAVE := 'TABLA';
                  MI_REEMPLAZOS (2).VALOR :=  UN_TABLA;

                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE, 
                                             UN_TABLAERROR  => UN_TABLA,
                                             UN_ERROR_COD   => PCK_ERRORES.ERROR_GRAL_ACTUALI_DATOERROR,                                   
                                             UN_REEMPLAZOS  => MI_REEMPLAZOS
                                            );        
    END;   
  RETURN MI_RPTA;
  EXCEPTION
       WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD   => SQLCODE,               
                                        UN_ERROR_COD => PCK_ERRORES.ERRR_GRAL_NOT_ACTIDATOERRO 
                                        );    
END  FC_ACT_DATOSERROR_GENERAL;

  PROCEDURE PR_REGISTRAR_SOLICITUD
  (
    /*
      NAME              : PR_REGISTRAR_SOLICITUD
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : YEISSON ALEJANDRO ROJAS RUIZ
      DATE MIGRADOR     : 06/04/2017
      TIME              : 11:30 AM
      SOURCE MODULE     : 
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PROCEDIMIENTO QUE HACE LLAMADOS A LA FUNCION FC_ACTRES Y ACTUALIZA  EL VALOR DE IND_REG 
                          EN LA TABLA D_ORDENDESUMINISTRO.
      PARAMETERS        : UN_COMPANIA             => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_ORDEN                => NUMERO DE ORDEN DE SUMINISTRO.
                          UN_CAMPO                => CAMPO QUE ES ENVIADO POR PARAMETRO A LA FUNCION FC_ACTRES.
                          UN_CLASE_BODEGA_ALMACEN => CODIGO DE LA CONSTANTE DE LA CLASE BODEGA.
                          UN_CLASE_BODEGA         => VALOR DE LA CLASE BODEGA EN LA CONSULTA DEL CONTROLADOR.
      MODIFICATIONS     : 

      @NAME:    registrarSolicitud
      @METHOD:  PUT
    */
    UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ORDEN                IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
    UN_CAMPO                IN VARCHAR2,
    UN_CLASE_BODEGA_ALMACEN IN VARCHAR2,
    UN_CLASE_BODEGA         IN VARCHAR2
  )
  AS
    MI_STRSQL       PCK_SUBTIPOS.TI_STRSQL;
    MI_RSORDENES    SYS_REFCURSOR;
    MI_ELEMENTO     D_ORDENDESUMINISTRO.ELEMENTO%TYPE;
    MI_CANTIDAD     D_ORDENDESUMINISTRO.CANTIDADAPROBADA%TYPE;
    MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
    MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN
    MI_STRSQL := 'SELECT  ELEMENTO,
                          CANTIDADAPROBADA
                  FROM    D_ORDENDESUMINISTRO 
                  WHERE   COMPANIA          = '''|| UN_COMPANIA ||'''
                    AND   ORDENDESUMINISTRO = '  || UN_ORDEN ||'
                    AND   IND_REG           = 0';

    <<ORDENES>>                
    OPEN MI_RSORDENES FOR MI_STRSQL;
      LOOP
        FETCH MI_RSORDENES
        INTO  MI_ELEMENTO,
              MI_CANTIDAD;
        EXIT WHEN MI_RSORDENES%NOTFOUND;
        PCK_DATOS.GL_RTA := PCK_GENERALES.FC_ACTRES(UN_COMPANIA => UN_COMPANIA,
                                                    UN_ELEMENTO => MI_ELEMENTO,
                                                    UN_CANTIDAD => MI_CANTIDAD,
                                                    UN_CAMPO    => UN_CAMPO);
      END LOOP ORDENES;
    CLOSE MI_RSORDENES;

    MI_CAMPOS    := 'IND_REG = -1';
    MI_CONDICION := '     COMPANIA          = '''|| UN_COMPANIA ||'''
                     AND  ORDENDESUMINISTRO = '  || UN_ORDEN ||' 
                     AND  IND_REG = 0';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'D_ORDENDESUMINISTRO',
                                              UN_ACCION    => 'M',
                                              UN_CAMPOS    => MI_CAMPOS,
                                              UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_GENERAL;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN
        MI_MSGERROR(1).CLAVE := 'ORDENDESUMINISTRO';
        MI_MSGERROR(1).VALOR := UN_ORDEN;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_GRAL_ACT_ORDENDESUMINISTRO,
          UN_REEMPLAZOS => MI_MSGERROR);
    END;

  END PR_REGISTRAR_SOLICITUD;

  FUNCTION FC_CAMBIAR_REQUISICION
  (
    /*
      NAME              : FC_CAMBIAR_REQUISICION
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : YEISSON ALEJANDRO ROJAS RUIZ
      DATE MIGRADOR     : 06/04/2017
      TIME              : 12:00 PM
      SOURCE MODULE     : 
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PROCEDIMIENTO QUE HACE LLAMADOS A LA FUNCION FC_ACTRES Y ACTUALIZA  EL VALOR DE IND_REG 
                          EN LA TABLA D_ORDENDESUMINISTRO.
      PARAMETERS        : UN_COMPANIA        => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_ORDEN           => NUMERO DE ORDEN DE SUMINISTRO.
                          UN_NUMERO_ANTERIOR => NUMERO DE LA ORDEN DE SUMINISTRO ANTERIOR
                          UN_USUARIO         => USUARIO ACTUAL.
                          UN_FECHA           => FECHA ENVIADA DESDE EL REGISTRO.
                          UN_DEPENDENCIA     => DEPENDENCIA ENVIADA DESDE EL REGISTRO.
                          UN_TERCERO         => TERCERO ENVIADA DESDE EL REGISTRO.
                          UN_SUCURSAL        => SUCURSAL ENVIADA DESDE EL REGISTRO.
                          UN_VALORESTIMADO   => VALOR ESTIMADO ENVIADA DESDE EL REGISTRO.
                          UN_DESCRIPCION     => DESCRIPCION ENVIADA DESDE EL REGISTRO.
                          UN_OBSERVACIONES   => OBSERVACIONES ENVIADA DESDE EL REGISTRO.
                          UN_PLAZO           => PLAZO ENVIADA DESDE EL REGISTRO.
                          UN_UNIDAD_TIEMPO   => UNIDAD DE TIEMPO ENVIADA DESDE EL REGISTRO.
                          UN_PERIODICIDAD    => VALOR DE PERIODICIDAD ENVIADA DESDE EL REGISTRO.
                          UN_NUMERO_ENTREGAS => VALOR DEL NUMERO DE ENTREGAS ENVIADA DESDE EL REGISTRO.
                          UN_CLASE_BODEGA    => CLASE DE BODEGA ENVIADA DESDE EL REGISTRO.
                          UN_AUXILIAR        => AUXILIAR ENVIADA DESDE EL REGISTRO.
      MODIFICATIONS     : 

      @NAME:    cambiarRequisicion
      @METHOD:  PUT
    */
    UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ORDEN                IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
    UN_NUMERO_ANTERIOR      IN VARCHAR2,
    UN_USUARIO              IN PCK_SUBTIPOS.TI_USUARIO,
    UN_FECHA                IN VARCHAR2,
    UN_DEPENDENCIA          IN ORDENDESUMINISTRO.DEPENDENCIA%TYPE,
    UN_TERCERO              IN ORDENDESUMINISTRO.TERCERO%TYPE,
    UN_SUCURSAL             IN ORDENDESUMINISTRO.SUCURSAL%TYPE,
    UN_VALORESTIMADO        IN ORDENDESUMINISTRO.VALORESTIMADO%TYPE,
    UN_DESCRIPCION          IN ORDENDESUMINISTRO.DESCRIPCION%TYPE,   
    UN_OBSERVACIONES        IN ORDENDESUMINISTRO.OBSERVACIONES%TYPE,   
    UN_PLAZO                IN ORDENDESUMINISTRO.PLAZO%TYPE,
    UN_UNIDAD_TIEMPO        IN ORDENDESUMINISTRO.UNIDAD_TIEMPO%TYPE,
    UN_PERIODICIDAD         IN ORDENDESUMINISTRO.PERIODICIDAD%TYPE,
    UN_NUMERO_ENTREGAS      IN ORDENDESUMINISTRO.NUMERO_ENTREGAS%TYPE,
    UN_CLASE_BODEGA         IN ORDENDESUMINISTRO.CLASE_BODEGA%TYPE,
    UN_AUXILIAR             IN ORDENDESUMINISTRO.AUXILIAR%TYPE
  )
  RETURN NUMBER
  AS
    MI_CONTEO               PCK_SUBTIPOS.TI_ENTERO;
    MI_CONTEO_ORDEN         PCK_SUBTIPOS.TI_ENTERO;
    MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
    MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
    MI_SALIDA               NUMBER;
    MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN
    SELECT  COUNT('X')
    INTO    MI_CONTEO
    FROM    ORDENDESUMINISTRO
    WHERE   COMPANIA = UN_COMPANIA
      AND   NUMERO   = UN_ORDEN;

    IF MI_CONTEO = 0 THEN
      MI_CAMPOS  := 'COMPANIA, NUMERO, FECHA, DEPENDENCIA, TERCERO, SUCURSAL, VALORESTIMADO, DESCRIPCION, 
                     OBSERVACIONES, PLAZO, UNIDAD_TIEMPO, PERIODICIDAD, NUMERO_ENTREGAS, CLASE_BODEGA, 
                     AUXILIAR, CREATED_BY, DATE_CREATED';
      MI_VALORES := ''''|| UN_COMPANIA ||''',
                    '   || UN_ORDEN ||',
                    TO_DATE('''|| UN_FECHA ||''',''DD/MM/YYYY''),
                    ''' || UN_DEPENDENCIA ||''',
                    ''' || UN_TERCERO ||''',
                    ''' || UN_SUCURSAL ||''',
                    '   || UN_VALORESTIMADO ||',
                    ''' || UN_DESCRIPCION ||''',
                    ''' || UN_OBSERVACIONES ||''',
                    '   || UN_PLAZO ||',
                    ''' || UN_UNIDAD_TIEMPO ||''',
                    ''' || UN_PERIODICIDAD ||''',
                    '   || UN_NUMERO_ENTREGAS ||',
                    ''' || UN_CLASE_BODEGA ||''',
                    ''' || UN_AUXILIAR ||''',
                    ''' || UN_USUARIO ||''', SYSDATE';

      BEGIN
        BEGIN
          MI_SALIDA := PCK_DATOS.FC_ACME(UN_TABLA   => 'ORDENDESUMINISTRO',
                                         UN_ACCION  => 'I',
                                         UN_CAMPOS  => MI_CAMPOS,
                                         UN_VALORES => MI_VALORES);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_GENERAL;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN
          MI_MSGERROR(1).CLAVE := 'ORDEN';
          MI_MSGERROR(1).VALOR := UN_ORDEN;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_GRAL_ACT_ORDENDESUMINISTRO,
            UN_REEMPLAZOS => MI_MSGERROR);
      END;

      SELECT  COUNT('X')
      INTO    MI_CONTEO_ORDEN
      FROM    D_ORDENDESUMINISTRO
      WHERE   COMPANIA          = UN_COMPANIA
        AND   ORDENDESUMINISTRO = UN_NUMERO_ANTERIOR;

      IF MI_CONTEO_ORDEN NOT IN (0) THEN
        MI_CAMPOS    := 'ORDENDESUMINISTRO = '|| UN_ORDEN ||',
                         MODIFIED_BY      = ''' || UN_USUARIO ||''', 
                         DATE_MODIFIED     = SYSDATE';
        MI_CONDICION := '    COMPANIA          = '''|| UN_COMPANIA ||'''
                         AND ORDENDESUMINISTRO = '  || UN_NUMERO_ANTERIOR;

        BEGIN
          BEGIN
            MI_SALIDA := PCK_DATOS.FC_ACME(UN_TABLA     => 'D_ORDENDESUMINISTRO',
                                           UN_ACCION    => 'M',
                                           UN_CAMPOS    => MI_CAMPOS,
                                           UN_CONDICION => MI_CONDICION);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_GENERAL;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN
            MI_MSGERROR(1).CLAVE := 'ORDENANTERIOR';
            MI_MSGERROR(1).VALOR := UN_NUMERO_ANTERIOR;
            MI_MSGERROR(2).CLAVE := 'ORDENNUEVA';
            MI_MSGERROR(2).VALOR := UN_ORDEN;
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_GRAL_ACT_ORDENSUMINISTRO,
              UN_REEMPLAZOS => MI_MSGERROR);
        END;
      END IF;

      MI_CONDICION := '    COMPANIA = '''|| UN_COMPANIA ||'''
                       AND NUMERO   = '  || UN_NUMERO_ANTERIOR;

      BEGIN
        BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'ORDENDESUMINISTRO',
                                                UN_ACCION    => 'E',
                                                UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_GENERAL;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN
          MI_MSGERROR(1).CLAVE := 'ORDEN';
          MI_MSGERROR(1).VALOR := UN_NUMERO_ANTERIOR;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_GRAL_ELMNR_ORDENSUMINISTRO,
              UN_REEMPLAZOS => MI_MSGERROR);
      END;
      MI_SALIDA := -1;
    ELSE
      MI_SALIDA := 0;
    END IF;

    RETURN MI_SALIDA;
  END FC_CAMBIAR_REQUISICION;

  --11. registrarDetalleOrdenDeCompra
  PROCEDURE PR_REGISTRAR_DET_ORDENDECOMPRA 
  /*
  NAME              : PR_REGISTRAR_ORDENDECOMPRA
  AUTHORS           : JUAN CARLOS RODRÍGUEZ AMÉZQUITA
  DATE              : 10/04/2017
  TIME              : 09:30 AM
  MODIFIER          : JUAN CARLOS RODRÍGUEZ AMÉZQUITA
  DATE MODIFIED     : 26/05/2017
  TIME              : 12:32 PM
  DESCRIPTION       : Creación del detalle de las órdenes de compra según las 
                    ordenes de suministro seleccionadas y con cantidad por entregar, 
                    en el formulario AuxOrdenDeSuministro que se abre desde el 
                    formulario de PContratos o desde la opción Modificación de 
                    Contratos. Código originario de los métodos cerrarPcontrato y 
                    cerrarAdicionesPContrato del bean AuxordendesuministrosControlador.
  MODIFICATIONS     : En actualización al detalle de la orden de suministro ajuste 
                    de la condición.
  PARAMETERS        : 
    UN_COMPANIA: Código de la compañía.
    UN_ORDEN: Código de la orden de compra.
    UN_CLASEORDEN: Clase de la orden de compra.
    UN_PORCDESCGLOBAL: Porcentaje que se debe aplicar al valor del descuento de la orden.
    UN_PORCIVAGLOBAL: Porcentaje que se debe aplicar al valor del IVA de la orden.
    UN_ES_MODIFICACION_CONTRATOS: Indica si se ejecuta desde la opción de Modificación de Contratos.
    UN_USUARIO: Código del usuario que inició sesión en la aplicación.

  @NAME: registrarDetalleOrdenDeCompra
  @METHOD: post
  */
  (
    UN_COMPANIA                     IN PCK_SUBTIPOS.TI_COMPANIA
  , UN_ORDEN                        IN ORDENDECOMPRA.NUMERO%TYPE
  , UN_CLASEORDEN                   IN ORDENDECOMPRA.CLASEORDEN%TYPE
  , UN_PORCDESCGLOBAL               IN PCK_SUBTIPOS.TI_DOBLE
  , UN_PORCIVAGLOBAL                IN PCK_SUBTIPOS.TI_DOBLE
  , UN_ES_MODIFICACION_CONTRATOS    IN PCK_SUBTIPOS.TI_LOGICO
  , UN_USUARIO                      IN PCK_SUBTIPOS.TI_USUARIO
  ) AS
    -- Ordenes seleccionadas con cantidad por entregar
    CURSOR MI_ORDENES_CON_CANTIDAD 
    IS
    SELECT NVL(D_ORDENDESUMINISTRO.ORDENDESUMINISTRO,0) AS ORDENDESUMINISTRO,
           D_ORDENDESUMINISTRO.CODIGO AS CODIGO,
           NVL(D_ORDENDESUMINISTRO.ELEMENTO,'') AS ELEMENTO,
           D_ORDENDESUMINISTRO.ESPECIFICACION,
           D_ORDENDESUMINISTRO.CANTIDADPORENTREGAR AS CANTORDENADA ,
           D_ORDENDESUMINISTRO.DEPENDENCIA,
           ORDENDESUMINISTRO.DESCRIPCION ,
           NVL(D_ORDENDESUMINISTRO.CODIGOCUBS,'') AS CUBS,
           ORDENDESUMINISTRO.PERIODICIDAD,
           ORDENDESUMINISTRO.NUMERO_ENTREGAS,
           ORDENDESUMINISTRO.UNIDAD_TIEMPO,
           ORDENDESUMINISTRO.PLAZO
    FROM ORDENDESUMINISTRO
    INNER JOIN D_ORDENDESUMINISTRO 
    ON (ORDENDESUMINISTRO.NUMERO = D_ORDENDESUMINISTRO.ORDENDESUMINISTRO)
    AND (ORDENDESUMINISTRO.COMPANIA = D_ORDENDESUMINISTRO.COMPANIA)
    WHERE ORDENDESUMINISTRO.SELECCIONADA NOT IN 0
      AND D_ORDENDESUMINISTRO.CANTIDADPORENTREGAR > 0
      AND D_ORDENDESUMINISTRO.COMPANIA = UN_COMPANIA
    ORDER BY D_ORDENDESUMINISTRO.ORDENDESUMINISTRO,
             D_ORDENDESUMINISTRO.CODIGO;
    -- Valor unitario promedio para crear el detalle de la orden de compra
    MI_VLR_UNITARIO_PROM            PCK_SUBTIPOS.TI_DOBLE;
    -- Descripción de la orden de compra
    MI_DESCRIPCION                  ORDENDECOMPRA.DESCRIPCION%TYPE DEFAULT '';
    -- Indica si se realizó la inserción en el detalle de la orden de compra
    MI_INSERTADO                    PCK_SUBTIPOS.TI_LOGICO := 0;
    -- Variables ACME    
    MI_CAMPOS                       PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                      PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION                    PCK_SUBTIPOS.TI_CONDICION;
    MI_PCKDATOS                     PCK_SUBTIPOS.TI_RTA_ACME;
    MI_REEMPLAZOS                   PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_NIIF_VIDA_UTIL               D_ORDENDECOMPRA.NIIF_VIDA_UTIL%TYPE;

  BEGIN
    EXECUTE IMMEDIATE q'[ALTER SESSION SET NLS_NUMERIC_CHARACTERS = '.,']'; 
    FOR MI_ORDEN IN MI_ORDENES_CON_CANTIDAD
    LOOP
      -- Datos de la dependencia
      DECLARE
        MI_NOMBRE_DEPENDENCIA              DEPENDENCIA.NOMBRE%TYPE;
      BEGIN
        IF MI_ORDEN.DEPENDENCIA IS NULL Then
          /* La dependencia en la requisicion seleccionada es nula. 
          Por favor Revise e intente nuevamente. */
          CONTINUE;
        END IF;
        --
        IF MI_ORDEN.ORDENDESUMINISTRO = 0 THEN
          -- Construcción de la descripción de la orden de compra para ORDEN 0
          SELECT NOMBRE
          INTO MI_NOMBRE_DEPENDENCIA
          FROM DEPENDENCIA
          WHERE COMPANIA = UN_COMPANIA
          AND CODIGO     = MI_ORDEN.DEPENDENCIA;
          MI_DESCRIPCION := MI_DESCRIPCION || ',' || MI_NOMBRE_DEPENDENCIA 
            || '-' || TRIM(MI_ORDEN.DESCRIPCION);
        END IF;
      EXCEPTION
      WHEN NO_DATA_FOUND THEN
        CONTINUE;
      END;
      -- Extracción del valor unitario según el elemento de la orden
      BEGIN
        SELECT
          CASE
            WHEN PRECIOREPOSICION > VLRUNITARIOPROM
            THEN PRECIOREPOSICION
            ELSE VLRUNITARIOPROM
          END AS VALOR
        INTO MI_VLR_UNITARIO_PROM
        FROM INVENTARIO
        WHERE COMPANIA     = UN_COMPANIA
        AND CODIGOELEMENTO = MI_ORDEN.ELEMENTO;
      EXCEPTION
      WHEN NO_DATA_FOUND THEN
        CONTINUE;
      END;

      BEGIN
        SELECT NVL(ACTDEPRECIABLE.MESESVIDAUTIL, 0)
          INTO MI_NIIF_VIDA_UTIL
          FROM INVENTARIO 
     LEFT JOIN ACTDEPRECIABLE 
            ON INVENTARIO.TIPOACTIVO = ACTDEPRECIABLE.CODIGO
         WHERE COMPANIA = UN_COMPANIA
           AND CODIGOELEMENTO = MI_ORDEN.ELEMENTO;
      EXCEPTION
      WHEN NO_DATA_FOUND THEN
        MI_NIIF_VIDA_UTIL := 0;
      END;

      -- Creación del detalle de la orden de compra.
      DECLARE
        MI_VLRDESCUENTO                 PCK_SUBTIPOS.TI_DOBLE;
        MI_VLRIVA                       PCK_SUBTIPOS.TI_DOBLE;
        MI_VLRTOTAL                     PCK_SUBTIPOS.TI_DOBLE;
        MI_VALORUNITARIODI              PCK_SUBTIPOS.TI_DOBLE;
        MI_CONSECUTIVO                  PCK_SUBTIPOS.TI_ENTERO;
      BEGIN
        -- Valor Unitario * Cantidad
        MI_VLRTOTAL := MI_VLR_UNITARIO_PROM * MI_ORDEN.CANTORDENADA;
        -- Total * Porcentaje Descuento
        MI_VLRDESCUENTO := ROUND(MI_VLRTOTAL * UN_PORCDESCGLOBAL / 100, 2);
        -- Redondear((Total - Descuento) * (1 + Porcentaje IVA / 100), 2) - Total + Descuento
        MI_VLRIVA := ROUND((MI_VLRTOTAL - MI_VLRDESCUENTO) * 
          (1 + UN_PORCIVAGLOBAL / 100), 2) - MI_VLRTOTAL + MI_VLRDESCUENTO;
        -- Total - Descuento + Valor IVA
        MI_VLRTOTAL := MI_VLRTOTAL - MI_VLRDESCUENTO + MI_VLRIVA;
        -- Valor Unitario DI
        MI_VALORUNITARIODI := 0;
        IF MI_ORDEN.CANTORDENADA > 0 THEN
          DECLARE
            MI_VALOR_PARAMETRO              PCK_SUBTIPOS.TI_PARAMETRO;
            MI_DIG_REDONDEO_IVA             PCK_SUBTIPOS.TI_ENTERO;
            MI_DIG_REDONDEO_TOTAL           PCK_SUBTIPOS.TI_ENTERO;  
          BEGIN 
            -- REDONDEAR UNITARIO CON IVA EN O.C.
            MI_VALOR_PARAMETRO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA, 
              'REDONDEAR UNITARIO CON IVA EN O.C.', PCK_DATOS.MODULOCONTABILIDAD, SYSDATE), '');
            IF MI_VALOR_PARAMETRO = 'SI' THEN
              MI_VALOR_PARAMETRO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA, 
                'DIGITOS REDONDEO UNITARIO CON IVA O.C.', PCK_DATOS.MODULOCONTABILIDAD, SYSDATE), 2);
              MI_DIG_REDONDEO_IVA := TO_NUMBER(MI_VALOR_PARAMETRO);
              MI_VALORUNITARIODI := ROUND(MI_VLRTOTAL / MI_ORDEN.CANTORDENADA, MI_DIG_REDONDEO_IVA);
            ELSE
              MI_VALORUNITARIODI := MI_VLRTOTAL / MI_ORDEN.CANTORDENADA;
            END IF;
            --
            MI_VLRTOTAL := MI_VALORUNITARIODI * MI_ORDEN.CANTORDENADA;
            -- REDONDEAR VALOR TOTAL EN O.C.
            MI_VALOR_PARAMETRO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA, 
              'REDONDEAR VALOR TOTAL EN O.C.', PCK_DATOS.MODULOCONTABILIDAD, SYSDATE), 'NO');
            IF MI_VALOR_PARAMETRO = 'SI' THEN
              MI_VALOR_PARAMETRO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA, 
                'DIGITOS REDONDEO VALOR TOTAL O.C.', PCK_DATOS.MODULOCONTABILIDAD, SYSDATE), 2);
              MI_DIG_REDONDEO_TOTAL := TO_NUMBER(MI_VALOR_PARAMETRO);
              MI_VLRTOTAL := ROUND(MI_VLRTOTAL, MI_DIG_REDONDEO_TOTAL);
            END IF;
          END;
        ELSE
          MI_VALORUNITARIODI := 0;
          MI_VLRTOTAL := 0;
        END IF;
        -- Consecutivo D_ORDENDECOMPRA
        MI_CONDICION := 'COMPANIA  = ''' || UN_COMPANIA 
          || ''' AND CLASEORDEN = ''' || UN_CLASEORDEN 
          || ''' AND ORDENDECOMPRA = ' || UN_ORDEN;
        MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(
          UN_TABLA => 'D_ORDENDECOMPRA'
        , UN_CRITERIO => MI_CONDICION
        , UN_CAMPO => 'CODIGO'
        , UN_INICIAL => 1);
        -- Inserción D_ORDENDECOMPRA
        MI_CAMPOS := 'COMPANIA,CLASEORDEN,ORDENDECOMPRA,CODIGO,ELEMENTO,' ||
          'ESPECIFICACION,CANTIDAD,SALDOCANT,VALORUNITARIO,PORCIVA,PORCDESC,' ||
          'VLRTOTAL,ORDENDESUMINISTRO,DEPENDENCIA,VALORUNITARIODI,' ||
          'VLRDESCUENTO,VLRIVA,ITEMORIGEN,CODIGOCUBS,PERIODICIDAD,' || 
          'NUMERO_ENTREGAS,UNIDAD_TIEMPO,PLAZO,NIIF_VIDA_UTIL,DATE_CREATED,CREATED_BY';
        MI_VALORES := '''' || UN_COMPANIA || '''' ||
          ',''' || UN_CLASEORDEN || '''' ||
          ',' || UN_ORDEN ||
          ',' || MI_CONSECUTIVO ||
          ',''' || MI_ORDEN.ELEMENTO || '''' ||
          ',''' || REPLACE(MI_ORDEN.ESPECIFICACION, '|') || '''' ||
          ',' || MI_ORDEN.CANTORDENADA ||
          ',' || MI_ORDEN.CANTORDENADA ||
          ',' || MI_VLR_UNITARIO_PROM ||
          ',' || UN_PORCIVAGLOBAL ||
          ',' || UN_PORCDESCGLOBAL ||
          ',' || MI_VLRTOTAL ||
          ',' || MI_ORDEN.ORDENDESUMINISTRO ||
          ',''' || MI_ORDEN.DEPENDENCIA || '''' ||
          ',' || MI_VALORUNITARIODI ||
          ',' || MI_VLRDESCUENTO ||
          ',' || MI_VLRIVA ||
          ',' || MI_ORDEN.CODIGO ||
          ',''' || MI_ORDEN.CUBS || '''' ||
          ',''' || MI_ORDEN.PERIODICIDAD || '''' ||
          ',' || MI_ORDEN.NUMERO_ENTREGAS ||
          ',''' || MI_ORDEN.UNIDAD_TIEMPO || '''' ||
          ',' || MI_ORDEN.PLAZO ||
          ',' || MI_NIIF_VIDA_UTIL ||
          ', TO_DATE(SYSDATE,''DD/MM/YYYY HH24:MI:SS'') ' ||
          ',''' || UN_USUARIO || '''';
        MI_PCKDATOS := PCK_DATOS.FC_ACME(
          UN_TABLA => 'D_ORDENDECOMPRA', 
          UN_ACCION => 'I', 
          UN_CAMPOS => MI_CAMPOS, 
          UN_VALORES => MI_VALORES
        );
        IF TO_NUMBER(MI_PCKDATOS) > 0 THEN
          -- updateDordenesdesuministroIndVaciaYCantidadporentregar
          MI_CAMPOS := 'VACIA = -1, CANTIDADPORENTREGAR = 0';
          MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA 
            || ''' AND ORDENDESUMINISTRO = ' || MI_ORDEN.ORDENDESUMINISTRO
            || ' AND CODIGO = ' || MI_ORDEN.CODIGO;
          MI_PCKDATOS := PCK_DATOS.FC_ACME(
            UN_TABLA => 'D_ORDENDESUMINISTRO', 
            UN_ACCION => 'M', 
            UN_CAMPOS => MI_CAMPOS, 
            UN_CONDICION => MI_CONDICION
          );
          MI_INSERTADO := -1;
        ELSE
          MI_INSERTADO := 0;
        END IF;
      EXCEPTION
      WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        MI_REEMPLAZOS(1).CLAVE := 'ORDENDECOMPRA';
        MI_REEMPLAZOS(1).VALOR := UN_ORDEN;
        MI_REEMPLAZOS(2).CLAVE := 'CLASEORDEN';
        MI_REEMPLAZOS(2).VALOR := UN_CLASEORDEN;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD => SQLCODE
        , UN_TABLAERROR => 'D_ORDENDECOMPRA'
        , UN_ERROR_COD => PCK_ERRORES.ERR_GRAL_CREAR_D_ORDENDECOMPRA
        , UN_REEMPLAZOS => MI_REEMPLAZOS
        ); 
      WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        MI_REEMPLAZOS(1).CLAVE := 'ORDENDESUMINISTRO';
        MI_REEMPLAZOS(1).VALOR := MI_ORDEN.ORDENDESUMINISTRO;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD => SQLCODE
        , UN_TABLAERROR => 'D_ORDENDESUMINISTRO'
        , UN_ERROR_COD => PCK_ERRORES.ERR_GRAL_ACT_D_ORDENSUMINISTRO
        , UN_REEMPLAZOS => MI_REEMPLAZOS
        ); 
      END;
      --
      IF MI_INSERTADO <> 0 THEN
        -- Actualización de la Orden de Suministro 
        BEGIN
          MI_CAMPOS := 'VACIA = -1';
          MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA 
            || ''' AND NUMERO = ' || UN_ORDEN ;
          MI_PCKDATOS := PCK_DATOS.FC_ACME(
            UN_TABLA => 'ORDENDESUMINISTRO', 
            UN_ACCION => 'M', 
            UN_CAMPOS => MI_CAMPOS, 
            UN_CONDICION => MI_CONDICION
          );
        EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          MI_REEMPLAZOS(1).CLAVE := 'ORDENDESUMINISTRO';
          MI_REEMPLAZOS(1).VALOR := 'número ' || UN_ORDEN;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD => SQLCODE
          , UN_TABLAERROR => 'ORDENDESUMINISTRO'
          , UN_ERROR_COD => PCK_ERRORES.ERR_GRAL_ACT_ORDENDESUMINISTRO
          , UN_REEMPLAZOS => MI_REEMPLAZOS
          );
        END;
      END IF;
    END LOOP;
    --
    BEGIN
      IF UN_ES_MODIFICACION_CONTRATOS <> 0 THEN
        MI_DESCRIPCION := 'OBJETOCONTRATO || CHR(32) || ' || MI_DESCRIPCION;
      END IF;
      -- Actualización de la descripción de la Orden de Compra
      MI_CAMPOS := 'DESCRIPCION = ''' || MI_DESCRIPCION || '''';
      MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA 
        || ''' AND NUMERO = ' || UN_ORDEN 
        || ' AND CLASEORDEN = ''' || UN_CLASEORDEN || '''';
      MI_PCKDATOS := PCK_DATOS.FC_ACME(
        UN_TABLA => 'ORDENDECOMPRA', 
        UN_ACCION => 'M', 
        UN_CAMPOS => MI_CAMPOS, 
        UN_CONDICION => MI_CONDICION
      );
    EXCEPTION
    WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
      MI_REEMPLAZOS(1).CLAVE := 'ORDENDECOMPRA';
      MI_REEMPLAZOS(1).VALOR := UN_ORDEN;
      MI_REEMPLAZOS(2).CLAVE := 'CLASEORDEN';
      MI_REEMPLAZOS(2).VALOR := UN_CLASEORDEN;
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD => SQLCODE
      , UN_TABLAERROR => 'ORDENDECOMPRA'
      , UN_ERROR_COD => PCK_ERRORES.ERR_GRAL_ACT_DESC_ORDENCOMPRA
      , UN_REEMPLAZOS => MI_REEMPLAZOS
      );
    END;
    --
    BEGIN
      IF UN_ES_MODIFICACION_CONTRATOS <> 0 THEN
        MI_CAMPOS := 'SELECCIONADA = 0';
      ELSE
        MI_CAMPOS := 'SELECCIONADA = -1';
      END IF;
      -- updateOrdenesdesuministroIndicadorSeleccionada
      MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || ''' AND SELECCIONADA NOT IN 0';
      MI_PCKDATOS := PCK_DATOS.FC_ACME(
        UN_TABLA => 'ORDENDESUMINISTRO', 
        UN_ACCION => 'M', 
        UN_CAMPOS => MI_CAMPOS, 
        UN_CONDICION => MI_CONDICION
      );
    EXCEPTION
    WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
      MI_REEMPLAZOS(1).CLAVE := 'ORDENDESUMINISTRO';
      MI_REEMPLAZOS(1).VALOR := '';
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD => SQLCODE
      , UN_TABLAERROR => 'ORDENDESUMINISTRO'
      , UN_ERROR_COD => PCK_ERRORES.ERR_GRAL_ACT_ORDENDESUMINISTRO
      , UN_REEMPLAZOS => MI_REEMPLAZOS
      );
    END;
    --
    IF UN_ES_MODIFICACION_CONTRATOS <> 0 THEN
      DECLARE
        MI_VALORSUB                        PCK_SUBTIPOS.TI_DOBLE := 0;
      BEGIN
        FOR RS IN(
          SELECT NVL((CANTIDAD*VALORUNITARIO)-VLRDESCUENTO+VLRIVA,0) AS VALORSUB
          FROM D_ORDENDECOMPRA
          WHERE D_ORDENDECOMPRA.COMPANIA   = UN_COMPANIA
          AND D_ORDENDECOMPRA.CLASEORDEN   = UN_CLASEORDEN
          AND D_ORDENDECOMPRA.ORDENDECOMPRA= UN_ORDEN
        )
        LOOP
          MI_VALORSUB := MI_VALORSUB + RS.VALORSUB;
        END LOOP;
        -- Actualización del valor total de la orden de compra.
        MI_CAMPOS := 'VALORTOTAL = ''' || MI_VALORSUB || '''';
        MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA 
          || ''' AND NUMERO = ' || UN_ORDEN 
          || ' AND CLASEORDEN = ''' || UN_CLASEORDEN || '''';
        MI_PCKDATOS := PCK_DATOS.FC_ACME(
          UN_TABLA => 'ORDENDECOMPRA', 
          UN_ACCION => 'M', 
          UN_CAMPOS => MI_CAMPOS, 
          UN_CONDICION => MI_CONDICION
        );
      EXCEPTION
      WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        MI_REEMPLAZOS(1).CLAVE := 'ORDENDECOMPRA';
        MI_REEMPLAZOS(1).VALOR := UN_ORDEN;
        MI_REEMPLAZOS(2).CLAVE := 'CLASEORDEN';
        MI_REEMPLAZOS(2).VALOR := UN_CLASEORDEN;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD => SQLCODE
        , UN_TABLAERROR => 'ORDENDECOMPRA'
        , UN_ERROR_COD => PCK_ERRORES.ERR_GRAL_ACT_VLR_ORDENDECOMPRA
        , UN_REEMPLAZOS => MI_REEMPLAZOS
        );
      END;
    END IF;
  -- 
  END PR_REGISTRAR_DET_ORDENDECOMPRA;

  --12. registrarDetalleRequisicion
  PROCEDURE PR_REGISTRAR_DETALLEREQUIS 
  /*
  NAME              : PR_REGISTRAR_DETALLEREQUIS
  AUTHORS           : JUAN CARLOS RODRÍGUEZ AMÉZQUITA
  DATE              : 10/04/2017
  TIME              : 09:45 AM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : Creación del detalle de la requisición según las ordenes de 
                    suministro seleccionadas y con cantidad por entregar, en el 
                    formulario AuxOrdenDeSuministro que se abre desde la opción 
                    de Centralización de Requisiciones (Planeación). Código 
                    originario del método cerrarPrequisiciones del bean 
                    AuxordendesuministrosControlador.
  MODIFICATIONS     : 
  PARAMETERS        : 
    UN_COMPANIA: Código de la compañía.
    UN_COD_REQUISICION: Código de la requisición.
    UN_COD_DETALLE: Número del detalle de la requisición.
    UN_USUARIO: Código del usuario que inició sesión en la aplicación.

  @NAME: registrarDetalleRequisicion
  @METHOD: post
  */
  (
    UN_COMPANIA                     IN PCK_SUBTIPOS.TI_COMPANIA
  , UN_COD_REQUISICION              IN DETALLEREQUIS.COD_REQUISICION%TYPE
  , UN_COD_DETALLE                  IN DETALLEREQUIS.COD_DETALLE%TYPE
  , UN_USUARIO                      IN PCK_SUBTIPOS.TI_USUARIO
  ) AS
    -- Ordenes seleccionadas con cantidad por entregar
    CURSOR MI_ORDENES_CON_CANTIDAD 
    IS
    SELECT NVL(D_ORDENDESUMINISTRO.ORDENDESUMINISTRO,0) AS ORDENDESUMINISTRO,
           D_ORDENDESUMINISTRO.CODIGO AS CODIGO,
           NVL(D_ORDENDESUMINISTRO.ELEMENTO,'') AS ELEMENTO,
           D_ORDENDESUMINISTRO.ESPECIFICACION,
           D_ORDENDESUMINISTRO.CANTIDADPORENTREGAR AS CANTORDENADA ,
           D_ORDENDESUMINISTRO.DEPENDENCIA,
           ORDENDESUMINISTRO.DESCRIPCION ,
           NVL(D_ORDENDESUMINISTRO.CODIGOCUBS,'') AS CUBS,
           ORDENDESUMINISTRO.PERIODICIDAD,
           ORDENDESUMINISTRO.NUMERO_ENTREGAS,
           ORDENDESUMINISTRO.UNIDAD_TIEMPO,
           ORDENDESUMINISTRO.PLAZO
    FROM ORDENDESUMINISTRO
    INNER JOIN D_ORDENDESUMINISTRO 
    ON (ORDENDESUMINISTRO.NUMERO = D_ORDENDESUMINISTRO.ORDENDESUMINISTRO)
    AND (ORDENDESUMINISTRO.COMPANIA = D_ORDENDESUMINISTRO.COMPANIA)
    WHERE ORDENDESUMINISTRO.SELECCIONADA NOT IN 0
      AND D_ORDENDESUMINISTRO.CANTIDADPORENTREGAR > 0
      AND D_ORDENDESUMINISTRO.COMPANIA = UN_COMPANIA
    ORDER BY D_ORDENDESUMINISTRO.ORDENDESUMINISTRO,
             D_ORDENDESUMINISTRO.CODIGO;
    -- Valor unitario promedio para crear el detalle de la orden de compra
    MI_VLR_UNITARIO_PROM            PCK_SUBTIPOS.TI_DOBLE;
    -- Variables ACME    
    MI_CAMPOS                       PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                      PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION                    PCK_SUBTIPOS.TI_CONDICION;
    MI_PCKDATOS                     PCK_SUBTIPOS.TI_RTA_ACME;
    MI_REEMPLAZOS                   PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    EXECUTE IMMEDIATE q'[ALTER SESSION SET NLS_NUMERIC_CHARACTERS = '.,']'; 
    FOR MI_ORDEN IN MI_ORDENES_CON_CANTIDAD
    LOOP
      -- Extracción del valor unitario según el elemento de la orden
      BEGIN
        SELECT
          CASE
            WHEN PRECIOREPOSICION > VLRUNITARIOPROM
            THEN PRECIOREPOSICION
            ELSE VLRUNITARIOPROM
          END AS VALOR
        INTO MI_VLR_UNITARIO_PROM
        FROM INVENTARIO
        WHERE COMPANIA     = UN_COMPANIA
        AND CODIGOELEMENTO = MI_ORDEN.ELEMENTO;
      EXCEPTION
      WHEN NO_DATA_FOUND THEN
        CONTINUE;
      END;
      -- Registro del detalle de la requisición
      DECLARE
        MI_CONSECUTIVO                  PCK_SUBTIPOS.TI_ENTERO;
      BEGIN
        -- Consecutivo DETALLEREQUIS
        MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA 
          || ''' AND COD_REQUISICION = ' || UN_COD_REQUISICION;
        MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(
          UN_TABLA => 'DETALLEREQUIS'
        , UN_CRITERIO => MI_CONDICION
        , UN_CAMPO => 'COD_DETALLE'
        , UN_INICIAL => UN_COD_DETALLE);
        -- Inserción DETALLEREQUIS
        MI_CAMPOS := 'COMPANIA,COD_REQUISICION,COD_DETALLE,CODIGOELEMENTO' || 
          ',ESPECIFICACION,CANTIDAD,SALDODETALLE,VALORUNITARIO,VALORTOTAL' || 
          ',ORDENDESUMINISTRO,DEPENDENCIA,DATE_CREATED,CREATED_BY';
        MI_VALORES := '''' || UN_COMPANIA || '''' ||
          ',' || UN_COD_REQUISICION ||
          ',' || MI_CONSECUTIVO ||
          ',''' || MI_ORDEN.ELEMENTO || '''' ||
          ',''' || MI_ORDEN.ESPECIFICACION || '''' ||
          ',' || MI_ORDEN.CANTORDENADA ||
          ',' || MI_ORDEN.CANTORDENADA ||
          ',' || MI_VLR_UNITARIO_PROM || 
          ',' || MI_VLR_UNITARIO_PROM || 
          ',' || MI_ORDEN.ORDENDESUMINISTRO ||
          ',''' || MI_ORDEN.DEPENDENCIA || '''' || 
          ', TO_DATE(SYSDATE,''DD/MM/YYYY HH24:MI:SS''), ''' || UN_USUARIO || '''';
        MI_PCKDATOS := PCK_DATOS.FC_ACME(
          UN_TABLA => 'DETALLEREQUIS', 
          UN_ACCION => 'I', 
          UN_CAMPOS => MI_CAMPOS, 
          UN_VALORES => MI_VALORES
        );
      EXCEPTION
      WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        MI_REEMPLAZOS(1).CLAVE := 'REQUISICION';
        MI_REEMPLAZOS(1).VALOR := UN_COD_REQUISICION;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD => SQLCODE
        , UN_TABLAERROR => 'DETALLEREQUIS'
        , UN_ERROR_COD => PCK_ERRORES.ERR_GRAL_CREAR_DETALLEREQUIS
        , UN_REEMPLAZOS => MI_REEMPLAZOS
        );
      END;
    END LOOP;
    --
    BEGIN
      -- updateOrdenesdesuministroIndicadorSeleccionada
      MI_CAMPOS := 'SELECCIONADA = 0';
      MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || ''' AND SELECCIONADA NOT IN 0';
      MI_PCKDATOS := PCK_DATOS.FC_ACME(
        UN_TABLA => 'ORDENDESUMINISTRO', 
        UN_ACCION => 'M', 
        UN_CAMPOS => MI_CAMPOS, 
        UN_CONDICION => MI_CONDICION
      );
    EXCEPTION
    WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
      MI_REEMPLAZOS(1).CLAVE := 'ORDENDESUMINISTRO';
      MI_REEMPLAZOS(1).VALOR := '';
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD => SQLCODE
      , UN_TABLAERROR => 'ORDENDESUMINISTRO'
      , UN_ERROR_COD => PCK_ERRORES.ERR_GRAL_ACT_ORDENDESUMINISTRO
      , UN_REEMPLAZOS => MI_REEMPLAZOS
      );
    END;
    -- 
  END PR_REGISTRAR_DETALLEREQUIS;

  --13. validarOrdenesDeSuministroVacias
  PROCEDURE PR_VALIDAR_ORDENDESUMIN_VACIAS
  /*
  NAME              : PR_VALIDAR_ORDENDESUMIN_VACIAS
  AUTHORS           : JUAN CARLOS RODRÍGUEZ AMÉZQUITA
  DATE              : 10/04/2017
  TIME              : 03:13 PM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : 
  MODIFICATIONS     : Recorre la lista de cantidades totales por cada detalle de 
                    las ordenes de suministro para identificar que ordenes de 
                    suministro están o no vacías.
  PARAMETERS        : 
    UN_COMPANIA: Código de la compañía.

  @NAME: validarOrdenesDeSuministroVacias
  @METHOD: put
  */
  (
    UN_COMPANIA                     IN PCK_SUBTIPOS.TI_COMPANIA
  )
  AS
    MI_VALOR                        PCK_SUBTIPOS.TI_COMPANIA;
    -- Variables ACME    
    MI_CAMPOS                       PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION                    PCK_SUBTIPOS.TI_CONDICION;
    MI_PCKDATOS                     PCK_SUBTIPOS.TI_RTA_ACME;
    MI_REEMPLAZOS                   PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    BEGIN
      -- Deseleccionar todas las ordenes de suministro vacías y seleccionadas.
      MI_CAMPOS := 'SELECCIONADA = 0';
      MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || ''' AND VACIA <> 0 ' 
        || 'AND SELECCIONADA NOT IN 0';
      MI_PCKDATOS := PCK_DATOS.FC_ACME(
        UN_TABLA => 'ORDENDESUMINISTRO', 
        UN_ACCION => 'M', 
        UN_CAMPOS => MI_CAMPOS, 
        UN_CONDICION => MI_CONDICION
      );
    EXCEPTION
    WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
      MI_REEMPLAZOS(1).CLAVE := 'ORDENDESUMINISTRO';
      MI_REEMPLAZOS(1).VALOR := '';
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD => SQLCODE
      , UN_TABLAERROR => 'ORDENDESUMINISTRO'
      , UN_ERROR_COD => PCK_ERRORES.ERR_GRAL_ACT_ORDENDESUMINISTRO
      , UN_REEMPLAZOS => MI_REEMPLAZOS
      );
    END;
    -- Listado de cantidades totales por orden de suministro
    FOR RS
    IN (
      SELECT ORDENDESUMINISTRO,
             SUM(CANTIDADTT) CANTIDADTOTAL
      FROM D_ORDENDESUMINISTRO
      WHERE COMPANIA = UN_COMPANIA
      GROUP BY ORDENDESUMINISTRO
    )
    LOOP
      IF RS.CANTIDADTOTAL > 0 THEN
        MI_VALOR := 0;
      ELSE
        MI_VALOR := -1;
      END IF;
      --
      BEGIN
        MI_CAMPOS := 'VACIA = ' || MI_VALOR;
        MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA 
          || ''' AND NUMERO = ' || RS.ORDENDESUMINISTRO;
        MI_PCKDATOS := PCK_DATOS.FC_ACME(
          UN_TABLA => 'ORDENDESUMINISTRO', 
          UN_ACCION => 'M', 
          UN_CAMPOS => MI_CAMPOS, 
          UN_CONDICION => MI_CONDICION
        ); 
      EXCEPTION
      WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        MI_REEMPLAZOS(1).CLAVE := 'ORDENDESUMINISTRO';
        MI_REEMPLAZOS(1).VALOR := RS.ORDENDESUMINISTRO;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD => SQLCODE
        , UN_TABLAERROR => 'ORDENDESUMINISTRO'
        , UN_ERROR_COD => PCK_ERRORES.ERR_GRAL_ACT_ORDENDESUMINISTRO
        , UN_REEMPLAZOS => MI_REEMPLAZOS
        );
      END;
    END LOOP;
  END PR_VALIDAR_ORDENDESUMIN_VACIAS;

FUNCTION FC_CONS_CAMBIONIT
RETURN PCK_SUBTIPOS.TI_LOGICO
AS
BEGIN
  RETURN GL_CAMBIONIT;
END FC_CONS_CAMBIONIT;

FUNCTION FC_CONS_CAMBIOABONO
RETURN PCK_SUBTIPOS.TI_LOGICO
AS
BEGIN
  RETURN GL_CAMBIOABONO;
END FC_CONS_CAMBIOABONO;


  PROCEDURE PR_COPIAR_AUXILIAR (
  /*
    NAME              : COPIAR_AUXILIAR
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 05/02/2015
    TIME              : 08:03 AM
    MODIFIER          : JOSE PASCUAL GOMEZ BLANCO
    DATE MODIFIED     : 05/02/2015
    TIME              : 08:03 AM
    DESCRIPTION       : Copia las auxiliares de un año a otro
    @NAME: copiarAuxiliar
  */
  UN_COMPANIA	        IN VARCHAR2,
  UN_ANO_DESTINO 		  IN INTEGER,
  UN_ANO_ORIGEN       IN INTEGER,
  UN_COMPANIA_DESTINO IN VARCHAR2
)
AS
  MI_CAMPOS       VARCHAR2(32000);
  MI_CONDICION    VARCHAR2(32000);
  MI_VALORES      VARCHAR2(32000);
  MI_EXCLUIDOS    VARCHAR2(32000);
  MI_ANO_INICIAL  INTEGER;
BEGIN
  PCK_PREPARAR_ANO.PR_PREPARARAUXILIARFUT(UN_COMPANIA         => UN_COMPANIA,
                         UN_ANO_DESTINO      => UN_ANO_DESTINO,
                         UN_ANO_ORIGEN       => UN_ANO_ORIGEN,
                         UN_COMPANIA_DESTINO => UN_COMPANIA_DESTINO);
  MI_EXCLUIDOS:='COMPANIA,ANO,CODIGOBP';
  MI_CAMPOS:= PCK_SYSMAN_UTL.FC_LISTA_CAMPOS('AUXILIAR',MI_EXCLUIDOS);

  MI_VALORES :=' SELECT ' || MI_CAMPOS || ',''' || UN_COMPANIA_DESTINO ||  ''',' || UN_ANO_DESTINO ||','''''||
               ' FROM AUXILIAR ORI ' ||
               ' WHERE ORI.COMPANIA=''' || UN_COMPANIA || '''' ||
                 ' AND ORI.ANO     =' || UN_ANO_ORIGEN ||
                 ' AND ORI.CODIGO NOT IN(SELECT CODIGO ' ||
                                       ' FROM AUXILIAR DES ' ||
                                       ' WHERE DES.COMPANIA='''|| UN_COMPANIA_DESTINO ||
                                         ''' AND DES.ANO     =' || UN_ANO_DESTINO ||
                                        ')'; 

  MI_CAMPOS:= MI_CAMPOS || ',' || MI_EXCLUIDOS;
  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME('AUXILIAR', 'IS', MI_CAMPOS, MI_VALORES, NULL, NULL); 
END PR_COPIAR_AUXILIAR;

PROCEDURE PR_COPIAR_FUENTE_RECURSO (
    /*
    NAME              : COPIAR_REFERENCIA
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 05/02/2015
    TIME              : 09:13 AM
    MODIFIER          : JOSE PASCUAL GOMEZ BLANCO
    DATE MODIFIED     : 05/02/2015
    TIME              : 09:13 AM
    DESCRIPTION       : Copia las fuentes de recursos de un año a otro
    @NAME: copiarFuenteRecurso
  */
  UN_COMPANIA	        IN VARCHAR2,
  UN_ANO_DESTINO 		  IN INTEGER,
  UN_ANO_ORIGEN       IN INTEGER,
  UN_COMPANIA_DESTINO IN VARCHAR2
)
AS
  MI_CAMPOS       VARCHAR2(32000);
  MI_CONDICION    VARCHAR2(32000);
  MI_VALORES      VARCHAR2(32000);
  MI_EXCLUIDOS    VARCHAR2(32000);
  MI_ANO_INICIAL  INTEGER;
  MI_ERROR_FUN NUMBER:=GL_ERROR_NUM + 16; 
BEGIN
  MI_EXCLUIDOS:='COMPANIA,ANO';
  MI_CAMPOS:= PCK_SYSMAN_UTL.FC_LISTA_CAMPOS('FUENTE_RECURSOS',MI_EXCLUIDOS);

  MI_VALORES :=' SELECT ' || MI_CAMPOS || ',''' || UN_COMPANIA_DESTINO ||  ''',' || UN_ANO_DESTINO ||
               ' FROM FUENTE_RECURSOS ORI ' ||
               ' WHERE ORI.COMPANIA=''' || UN_COMPANIA || '''' ||
                 ' AND ORI.ANO     =' || UN_ANO_ORIGEN ||
                 ' AND ORI.CODIGO NOT IN(SELECT CODIGO ' ||
                                       ' FROM FUENTE_RECURSOS DES ' ||
                                       ' WHERE DES.COMPANIA =''' || UN_COMPANIA_DESTINO ||
                                         ''' AND DES.ANO      =' || UN_ANO_DESTINO ||
                                        ')'; 

  MI_CAMPOS:= MI_CAMPOS || ',' || MI_EXCLUIDOS;
  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME('FUENTE_RECURSOS', 'IS', MI_CAMPOS, MI_VALORES, NULL, NULL); 
    EXCEPTION WHEN OTHERS THEN
    PCK_DATOS.GL_ERROR_MSG := 'Copiando Fuente de Recursos';
    PCK_DATOS.GL_ERROR_RTA := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'FUENTE_RECURSOS','',SQLERRM );
    RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_RTA || CHR(10) || PCK_DATOS.GL_ERROR_MSG );
END PR_COPIAR_FUENTE_RECURSO;

--TICKET 7734212 (19/07/2023 lvega)
FUNCTION FC_CONS_ELIM_COMPROBANTE
RETURN PCK_SUBTIPOS.TI_LOGICO
AS
BEGIN
  RETURN GL_DELETE_CPTE;
END FC_CONS_ELIM_COMPROBANTE;
--TICKET 7734212 (19/07/2023 lvega)

FUNCTION FC_ACT_AXUILIARES_MANT
RETURN PCK_SUBTIPOS.TI_LOGICO
AS
BEGIN
  RETURN GL_CAMBIOAUXMANT;
END FC_ACT_AXUILIARES_MANT;

PROCEDURE PR_COPIAR_TERCEROSXCOMPANIA (
    /*
    NAME              : PR_COPIAR_TERCEROSXCOMPANIA
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MIGRADOR     : 03/11/2023
    TIME              : 09:13 AM
    DESCRIPTION       : Copia la configuracion de los terceros de una compañia a otra
  */
  UN_COMPANIA_ORIGEN  IN VARCHAR2,
  UN_COMPANIA_DESTINO IN VARCHAR2
)
AS 
BEGIN
  PCK_PREPARAR_ANO.PR_COPIAR_TIPOS_DOCUMENTOS(UN_COMPANIA_ORIGEN, UN_COMPANIA_DESTINO);
  PCK_PREPARAR_ANO.PR_COPIAR_TIPO_EMBARGO(UN_COMPANIA_ORIGEN, UN_COMPANIA_DESTINO);
  PCK_PREPARAR_ANO.PR_COPIAR_ZONA(UN_COMPANIA_ORIGEN, UN_COMPANIA_DESTINO);
  PCK_PREPARAR_ANO.PR_COPIAR_UNIDAD_AV(UN_COMPANIA_ORIGEN, UN_COMPANIA_DESTINO);
  PCK_PREPARAR_ANO.PR_COPIAR_GRADOS_AV(UN_COMPANIA_ORIGEN, UN_COMPANIA_DESTINO);
  PCK_PREPARAR_ANO.PR_COPIAR_TERCERO(UN_COMPANIA_ORIGEN, UN_COMPANIA_DESTINO);
END PR_COPIAR_TERCEROSXCOMPANIA;

END PCK_GENERALES;